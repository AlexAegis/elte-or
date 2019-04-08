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
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
		var map = new HashMap<Integer, String>();
		map.put(2, "a");
		map.put(1, "a");
		//map.put(4, "a");
		var keys = map.keySet();
		var min = 0;
		var max = 0;
		try {
			min = Collections.min(keys);
			max = Collections.max(keys);
		} finally {
			Set<Integer> k = IntStream.rangeClosed(++min , ++max).boxed().collect(Collectors.toSet());
			k.removeAll(keys);
			k.stream().findFirst().ifPresentOrElse(System.out::println, () -> System.out.println("Err"));
		}

	}
}
