package musicbox;

import hu.akarnokd.rxjava2.operators.FlowableTransformers;
import io.reactivex.*;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.SingleSubject;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Test {
	/*public static void main(String[] args) {
		var flowable = Flowable.concat(Flowable.fromArray("h", "3", "a", "b", "c", "1", "a", "2", "a", "b"), Flowable.never());
		flowable.compose(FlowableTransformers.bufferUntil(new Predicate<>() {
			private int remaining = 0;
			@Override
			public boolean test(String next) {
				if(next.chars().allMatch(Character::isDigit)) {
					remaining = Integer.parseInt(next);
				}
				return --remaining < 0;
			}
		})).blockingSubscribe(System.out::println);
	}*/

	public static void main(String[] args) throws MidiUnavailableException {
/*
		var stopper = BehaviorSubject.create();
		var stopper$ = stopper.doOnDispose(() -> System.out.println("I'm disposed"));
		Observable.just(1).delay(1000, TimeUnit.MILLISECONDS).subscribe(stopper::onNext);
		Observable.interval(100, TimeUnit.MILLISECONDS)
			.takeUntil(stopper$)
			.blockingSubscribe(System.out::println);
*/
	}

	public static int parseNote(char baseNote, int half, int octave) {
		var baseNoteForCalc = baseNote;
		if(baseNote == 'A') {
			baseNoteForCalc = 'H';
		} else if(baseNote == 'B') {
			baseNoteForCalc = 'I';
		}

		int pitch = (((baseNoteForCalc - 7) - 60) * 2) + 60;
		//System.out.println("Pitch should be " + (int )baseNote + " when converting back from " + pitch + " but it's: " + parsePitch(pitch) );
		int result = pitch + half + octave * 12;


	//	System.out.println("note: "  + baseNote + " half: " + half + " octave: " + octave +" : " + result);

		return result;
	}

	public static int parsePitch(int pitch) {
		return ((((((pitch - 60) ) / 2) + 2) % 7) + 60 + 7 - 2);
	}

	public static int applyTranspose(int pitch, int transpose) {
		return (((pitch - 67) + transpose) % 7) + 67;
	}

}
