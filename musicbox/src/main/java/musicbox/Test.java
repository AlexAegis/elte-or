package musicbox;

import hu.akarnokd.rxjava2.operators.FlowableTransformers;
import io.reactivex.*;
import io.reactivex.functions.Predicate;
import io.reactivex.internal.operators.flowable.FlowableCombineLatest;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.SingleSubject;
import musicbox.misc.Pair;
import musicbox.net.Connection;
import org.reactivestreams.Subscriber;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import java.util.Arrays;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;

public class Test {
	static class Hello extends Observable<String> {
		public void say() {
			System.out.println("I'm still here!");
		}


		@Override
		protected void subscribeActual(Observer<? super String> s) {
			s.onNext("Hello1!");
			s.onNext("Hello2!");
			s.onNext("Hello3!");
			s.onNext("Hello4!");
			s.onComplete();
		}
	}

	public static void main(String[] args) throws MidiUnavailableException {
		/*Flowable.just(new Hello())
			.flatMap(next -> next, Pair::new)
			.blockingSubscribe((next) -> {
			System.out.println(next.getY()); // Hello1!, Hello2!, Hello3!, Hello4!
			next.getX().say(); // I'm still here!, I'm still here!, I'm still here!, I'm still here!
		});*/

		// , Pair::new
		Flowable.just(new Hello().concatWith(Observable.never()))
			.parallel()
			.runOn(Schedulers.io())
			.flatMap(r -> r.onTerminateDetach().toFlowable(BackpressureStrategy.BUFFER).withLatestFrom(Flowable.just(r), Pair::new))
			.sequential()
			.blockingSubscribe(System.out::println);


		/*


				Observable.just(new Hello()).flatMap(next -> {
			return Observable.combineLatest(next, Observable.just(next), Pair::new);
		}).blockingSubscribe((next) -> {
			System.out.println(next.getX()); // Hello1!, Hello1!, Hello1!, Hello1!
			next.getY().say(); // I'm still here!, I'm still here!, I'm still here!, I'm still here!
		});

		 */

	}
}
