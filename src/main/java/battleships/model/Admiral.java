package battleships.model;

import battleships.exception.AlreadyShotException;
import battleships.exception.BorderShotException;
import battleships.gui.Palette;
import battleships.gui.container.GameWindow;
import battleships.gui.container.Opponent;
import battleships.gui.container.Sea;
import battleships.gui.element.ShipSegment;
import battleships.marker.ShotMarker;
import battleships.net.action.Ready;
import battleships.state.Phase;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
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

	public void removeAllShipModels() {
		ships.clear();
	}

	/**
	 * @param sea the sea to set
	 */
	public Admiral setSea(Sea sea) {
		// TODO Put everything on the sea from ships LASO THE DAMAGE IFD ITS AN OPPONEnT REVEAL!
		this.sea = sea;
		sea.setAdmiral(this);
		if(whenPlayer != null && !getShipModels().isEmpty()) { // Then its a relog from the player the drawer has to be emptied
			whenPlayer.getDrawer().removeAllComponents();
		}
		getShipModels().forEach(ship -> {
			var restored = new battleships.gui.element.Ship(ShipType.getWithLengthAtLeast(ship.getBody().size()));
			ship.getHead().map(Coord::convertToTerminalPosition).ifPresent(restored::setPosition);
			if (ship.getBody().keySet().stream()
				.map(Coord::getX)
				.reduce((acc, next) -> acc = acc - next)
				.orElse(0) == 0) {
				restored.setLayoutToVertical();
			} else {
				restored.setLayoutToHorizontal();
			}
			getSea().addComponent(restored);
		});

		getShipModels().stream()
			.flatMap(ship -> ship.getBody().values().stream())
			.filter(Objects::nonNull)
			.forEach(shot -> getSea().receiveShot(shot));
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
		Logger.getGlobal().log(Level.INFO, "Setting phase to: " + phase + " game?: " + (whenPlayer != null) + " opponent?: " +( whenOpponent != null) + " admiral: " + this);
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
		admiral.getShipModels().stream()
				.filter(ship -> ship.getBody().keySet().stream().anyMatch(coord -> coord.neighbours(shipPiece)))
				.reduce((acc, next) -> {
					toBeRemoved.add(next);
					acc.merge(next);
					return acc;
				}).ifPresentOrElse(ship -> ship.addBody(shipPiece, shot),
						() -> admiral.getShipModels().add(new Ship(admiral, shipPiece, shot)));
		toBeRemoved.forEach(admiral.getShipModels()::remove);
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
	public Shot shoot(Admiral admiral, Coord target) {
		var shot = new Shot(this, admiral, target, ShotMarker.MISS);

		var optionalTarget = admiral.ships.stream().filter(ship -> ship.getBody().keySet().contains(shot.getTarget())).findFirst();
		optionalTarget.ifPresentOrElse(ship -> {
			ship.receiveShot(shot);
			place(knowledge.get(admiral.getName()), shot.getTarget(), shot);
		}, () -> {
			shot.setResult(ShotMarker.MISS);
			admiral.getMiss().add(shot);
			knowledge.get(admiral.getName()).getMiss().add(shot);
		});

		return shot;
	}

	/**
	 * Client method
	 * When readying, send all the ship coordinates to the server
	 *
	 * @param ready
	 * @return
	 */
	public Admiral setReady(Boolean ready) {
		if(whenPlayer != null) {
			var shipCoords = getSea().getShips().stream()
				.flatMap(ship -> ship.getBody().stream())
				.map(ShipSegment::getAbsolutePosition)
				.map(Coord::new)
				.collect(Collectors.toList());
			System.out.println("Sending ready with these coords: " + shipCoords);
			whenPlayer.getClient().sendRequest(new Ready(getName(), getName(), shipCoords, ready)).subscribe(res -> {
				Logger.getGlobal().info("Notified the server about my ready state! " + res);
			});
		}

		if (ready != null && ready == true) {
			setPhase(Phase.READY);
		} else if(ready != null && ready == false) {
			setPhase(Phase.PLACEMENT);
		}

		System.out.println("Final phase: " + getPhase() + " for " + this);

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
		return admiral.getShipModels().stream().map(Ship::toString).collect(Collectors.joining("\n"));
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
	public List<Ship> getShipModels() {
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
		ships.forEach(Ship::finishBorder);
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
		System.out.println(" ----- WHATS YO PROBLEM getName(): " + getName() + " isReady() " + isReady());
		setName(getName());
		setReady(isReady());
	}

	@Override
	public int compareTo(Admiral o) {
		return getName().compareTo(o.getName());
	}

	public Admiral setOpponent(Opponent opponent) {
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
