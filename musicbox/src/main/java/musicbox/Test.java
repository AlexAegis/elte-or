package musicbox;

import hu.akarnokd.rxjava2.operators.FlowableTransformers;
import io.reactivex.*;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.SingleSubject;

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

	public static void main(String[] args) {
		var items = Observable.fromArray("a", "b", "c", "d");
		var zipFinish = SingleSubject.<String>create();
		// zipFinish.subscribe(next -> System.out.println("got something in zipFinish:" + next));
		var interval = Observable.interval(500, TimeUnit.MILLISECONDS);//.takeUntil(zipFinish.toObservable().doOnComplete(() -> System.out.println("notifier completed")));

		Observable.zip(items.doOnComplete(() -> System.out.println("items completed")), interval.doOnDispose(() -> System.out.println("interval disposed")), (item, timer) -> item)
			.doOnComplete(() -> {System.out.println("zip completed");
				zipFinish.onSuccess("Hey");
			})
			.blockingSubscribe(System.out::println);


	}

}
