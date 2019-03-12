package battleships.model;

import battleships.exception.AlreadyShotException;
import battleships.exception.BorderShotException;
import battleships.gui.Palette;
import battleships.gui.container.GameWindow;
import battleships.gui.container.Opponent;
import battleships.gui.container.Sea;
import battleships.marker.ShotMarker;
import battleships.net.action.Ready;
import battleships.state.Phase;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * A class representing a player
 */
public class Admiral implements Comparable<Admiral>, Serializable {

	private static final long serialVersionUID = 2452332968381664354L;

	private List<Ship> ships = new ArrayList<>();
	private List<Shot> miss = new ArrayList<>();

	// The Admiral used as a key is name, the value is the mock of that admiral
	private HashMap<String, Admiral> knowledge = new HashMap<>();

	private String name;
	private transient Sea sea;
	private transient Opponent whenOpponent;
	private transient GameWindow whenPlayer;

	private Phase phase = Phase.PLACEMENT;

	public Admiral(String name) {
		this.name = name;
	}

	/**
	 * @param sea the sea to set
	 */
	public Admiral setSea(Sea sea) {
		// TODO Put everything on the sea from ships
		this.sea = sea;
		sea.setAdmiral(this);
		return this;
	}

	/**
	 * @return the sea
	 */
	public Sea getSea() {
		return sea;
	}

	/**
	 * @param game the game to set
	 */
	public void setGame(GameWindow game) {
		this.whenPlayer = game;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param phase the phase to set
	 */
	public void setPhase(Phase phase) {
		if(this.phase.ordinal() >= 2 && phase.ordinal() < 2) {
			Logger.getGlobal().severe("!!! Tried to mess up a running game!");
			return;
		}
		this.phase = phase;
		System.out.println("PHASE: " + phase + " game: " + whenPlayer + " oppon: " + whenOpponent + " where: " + this);
		if(whenPlayer != null) {
			switch (phase) {
				case READY:
					whenPlayer.getReadyLabel().ready();
					break;
				case PLACEMENT:
					whenPlayer.getReadyLabel().notReady();
					break;
				case GAME:
					// What to do when it's not my turn?
					Logger.getGlobal().info("It's not my turn!");
					whenPlayer.getActionBar().hideReadyButton();
					whenPlayer.getReadyLabel().hide();
					whenPlayer.getPlayerName().setForegroundColor(Palette.SMOKE);
					// Focus on sea, inspect your ships
					whenPlayer.getAdmiral().getSea().takeFocus();
					break;
				case ACTIVE:
					// What to do when it's my turn?
					Logger.getGlobal().info("It's my turn!");
					whenPlayer.getActionBar().hideReadyButton();
					whenPlayer.getPlayerName().setForegroundColor(Palette.READY);
					whenPlayer.getReadyLabel().hide();
					whenPlayer.getOpponentBar().takeFocus();
					break;
				default:
					whenPlayer.getReadyLabel().base();
			}
		}
		if(whenOpponent != null) {
			switch (phase) {
				case READY:
					whenOpponent.getLabel().ready();
					break;
				case PLACEMENT:
					whenOpponent.getLabel().notReady();
					break;
				case GAME:
					// What does this opponent do when it's not his turn?
					Logger.getGlobal().info("It's not his turn!");
					whenOpponent.getLabel().base();
					break;
				case ACTIVE:
					// What does this opponent do when it's his turn?
					Logger.getGlobal().info("It's his turn!");
					whenOpponent.getLabel().ready();
					break;
				default:
					whenOpponent.getLabel().base();

			}
		}

	}

	/**
	 * @return the phase
	 */
	public Phase getPhase() {
		return phase;
	}

	public Admiral(String name, List<Coord> shipPieces) {
		this(name);
		placeAll(shipPieces);
	}

	public void placeAll(List<Coord> shipPieces) {
		if (shipPieces != null) {
			shipPieces.forEach(this::place);
		}
	}

	public Optional<GameWindow> whenPlayer() {
		return Optional.ofNullable(whenPlayer);
	}

	public Optional<Opponent> whenOpponent() {
		return Optional.ofNullable(whenOpponent);
	}

	public void place(Coord shipPiece) {
		place(this, shipPiece, null);
	}

	public void place(Admiral admiral, Coord shipPiece) {
		place(admiral, shipPiece, null);
	}

	public static void place(Admiral admiral, Coord shipPiece, Shot shot) {
		var toBeRemoved = new ArrayList<Ship>();
		admiral.getShips().stream()
				.filter(ship -> ship.getBody().keySet().stream().anyMatch(coord -> coord.neighbours(shipPiece)))
				.reduce((acc, next) -> {
					toBeRemoved.add(next);
					acc.merge(next);
					return acc;
				}).ifPresentOrElse(ship -> ship.addBody(shipPiece, shot),
						() -> admiral.getShips().add(new Ship(admiral, shipPiece, shot)));
		toBeRemoved.forEach(admiral.getShips()::remove);
	}

	/**
	 * SERVER METHOD
	 *
	 * Each ship can check if it's hit it or not.
	 * This does the exact same amount of checks if I were
	 * to search the hit among the coordinates containing ships.
	 * @param target
	 *
	 */
	public Shot shoot(Admiral admiral, Coord target) throws AlreadyShotException, BorderShotException {
		var shot = new Shot(this, target, ShotMarker.MISS);
		// knowledge.putIfAbsent(admiral, new Admiral(admiral.getName()));
		if (knowledge.values().stream().flatMap(a -> a.ships.stream()).flatMap(ship -> ship.getBorder().stream())
				.anyMatch(coord -> coord.equals(target))) {
			throw new BorderShotException();
		}
		var shotResults =
				admiral.ships.stream().map(ship -> ship.recieveShot(shot)).distinct().collect(Collectors.toList());
		if (shotResults.contains(true)) {
			shot.setResult(ShotMarker.HIT_AND_FINISHED);
		} else if (shotResults.contains(false)) {
			shot.setResult(ShotMarker.HIT);
		} else {
			admiral.getMiss().add(shot);
			knowledge.get(admiral).getMiss().add(shot);
		}
		if (shotResults.contains(false) || shotResults.contains(true)) {
			place(knowledge.get(admiral), shot.getTarget(), shot);
		}
		return shot;
	}


	public Admiral setReady(boolean ready) {
		if(whenPlayer != null) {
			whenPlayer.getClient().sendRequest(new Ready(getName(), getName(), ready)).subscribe(res -> {
				Logger.getGlobal().info("Notified the server about my ready state! " + res);
			});
		}

		if (ready) {
			setPhase(Phase.READY);
		} else {
			setPhase(Phase.PLACEMENT);
		}

		return this;
	}


	public void print(PrintStream ps) {
		ps.println(toString());
		ps.println();
	}

	public String field() {
		return field(null);
	}

	/**
	 * Prints out it's own field when no admiral is given
	 * Prints out the knowledge about the opponent if admiral is given
	 * @return
	 */
	@Deprecated
	public String field(Admiral admiral) {
		var field = Table.empty();
		if (admiral != null) {
			// knowledge.putIfAbsent(admiral, new Admiral(admiral.getName()));
			// knowledge.get(admiral).print(field);
		} else {
			print(field);
		}
		return Arrays.stream(field).map(row -> Arrays.stream(row).collect(Collectors.joining()))
				.collect(Collectors.joining("\n"));
	}

	public void print(String[][] into) {
		ships.forEach(ship -> ship.print(into));
		miss.forEach(miss -> miss.print(into));
	}

	public String state() {
		return state(null);
	}

	public String state(Admiral admiral) {
		if (admiral != null) {
			admiral = knowledge.get(admiral);
		} else {
			admiral = this;
		}
		return admiral.getShips().stream().map(Ship::toString).collect(Collectors.joining("\n"));
	}

	/**
	* The isEmpty check is there so if there is a player without ships, it automatically qualifies for godmode
	*
	* @return
	*/
	public Boolean isLost() {
		return ships.stream().allMatch(Ship::isDead) && !ships.isEmpty();
	}

	/**
	 * @return the miss
	 */
	public List<Shot> getMiss() {
		return miss;
	}

	/**
	 * @return the ships
	 */
	public List<Ship> getShips() {
		return ships;
	}

	/**
	 * @return the knowledge
	 */
	public HashMap<String, Admiral> getKnowledge() {
		return knowledge;
	}

	@Override
	public String toString() {
		return "A("+hashCode()+"):{name: " + getName() + " phase: " + getPhase() + " isPlayer?: " + (whenPlayer != null) + " isOpponent?: " + (whenOpponent != null) + " knowledge: " + getKnowledge().entrySet().stream().map(e -> "k: " + e.getKey() + " Adm name: " + e.getValue().getName() + " hash: "+ e.getValue().hashCode() + " isPlayer? " + (e.getValue().whenPlayer != null) + " isOpponent? " + (e.getValue().whenOpponent != null)).collect(Collectors.joining(",")) + " }";
	}

	public void finishBorders() {
		ships.forEach(ship -> ship.finishBorder());
	}

	public Boolean isReady() {
		if (Phase.PLACEMENT.equals(phase)) {
			return false;
		} else if (Phase.READY.equals(phase)) {
			return true;
		} else {
			return null;
		}
	}

	public void setName(String name) {
		if(whenPlayer != null) {
			whenPlayer.getPlayerName().setText(name);
		}
		this.name = name;
	}

	public void refresh() {
		setName(getName());
		setReady(isReady());
	}

	@Override
	public int compareTo(Admiral o) {
		return getName().compareTo(o.getName());
	}

	public Admiral setOpponent(Opponent opponent) {
		System.out.println("setOpponent OPTIOOONNNAL");
		whenOpponent = opponent;
		refresh();
		return this;
	}

	public void inspect(battleships.gui.element.Ship ship) {
		if(whenPlayer != null) {
			Logger.getGlobal().info("Inspecting own ship: " + ship);
			whenPlayer.getInspector().inspect(ship);
		}
		if(whenOpponent != null) {
			Logger.getGlobal().info("Inspecting opponents ship: " + ship);
			whenOpponent.getGame().getInspector().inspect(ship);
		}
	}
}
