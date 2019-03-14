package battleships.net.result;

import battleships.net.action.Turn;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Test {
	public static void main(String[] args) {/*
		Observable.timer(2000, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.computation())
			.switchMap(next ->  {
				System.out.println("Hello ther!");
				return Observable.just("Kenobi");
			})
			.blockingSubscribe(ack -> {

				System.out.println("General!" + ack);
			});

		*/

		var sub = PublishSubject.<String>create();
		//var disp = sub.take(1).unsubscribeOn(Schedulers.).System.out::println);




	}
}
