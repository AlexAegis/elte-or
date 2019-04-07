package musicbox.model;

import io.reactivex.Observable;
import io.reactivex.Observer;
import musicbox.net.result.Note;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Song extends Observable<Note> implements Serializable {

	private static final long serialVersionUID = -4592367101254279598L;

	private String title;
	private List<String> notes;
	private List<String> lyrics;

	public Song(String title, List<String> notes) {
		this.title = title;
		this.notes = notes;
	}

	public String getTitle() {
		return title;
	}

	public List<String> getNotes() {
		return notes;
	}

	public List<String> getLyrics() {
		return lyrics;
	}

	public void setLyrics(List<String> lyrics) {
		this.lyrics = lyrics;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Song song = (Song) o;
		return Objects.equals(title, song.title);
	}

	@Override
	public int hashCode() {
		return Objects.hash(title);
	}

	@Override
	protected void subscribeActual(Observer<? super Note> observer) {
		for (int i = 0; i < notes.size(); i++) {

		}
		observer.onComplete();
	}
}
