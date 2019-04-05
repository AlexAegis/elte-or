package lesson05;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PingPong extends Thread {

	private final AtomicBoolean lock;
	private final AtomicBoolean otherLock;

	private String msg;
	private final AtomicInteger count = new AtomicInteger(0);

	private PingPong(AtomicBoolean lock, AtomicBoolean otherLock, String msg) {
		this.lock = lock;
		this.otherLock = otherLock;
		this.msg = msg;
	}

	@Override
	public void run() {
		try {
			while(!interrupted()) {

				synchronized(count) {
					if(count.incrementAndGet() >= 4) {
						interrupt();
					}

					while(!lock.get()) {
						synchronized (lock) {
							lock.wait();
						}
					}
					synchronized (lock) {
						lock.set(false);
					}
					System.out.println(msg);
					Thread.sleep(1000);
					synchronized (otherLock) {
						otherLock.set(true);
						otherLock.notify();
					}
				}
			}
		} catch (InterruptedException e) {
			Logger.getGlobal().log(Level.INFO, "Interrupted", e);
		}

	}


	public static void main(String[] args) {
		var lock1 = new AtomicBoolean(false);
		var lock2 = new AtomicBoolean(false);
		var p1 = new PingPong(lock1, lock2, "ping");
		var p2 = new PingPong(lock2, lock1, "pong");
		p1.start();
		p2.start();
		synchronized (p1) {
			p1.notify();
		}
	}

}
