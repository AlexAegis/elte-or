package lesson05;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PingPong implements Runnable {

	private Object lock;
	private Object otherLock;
	private String msg;

	private PingPong(Object lock, Object otherLock, String msg) {
		this.lock = lock;
		this.otherLock = otherLock;
		this.msg = msg;
	}

	@Override
	public void run() {
		while(true) {
			System.out.println(msg);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Logger.getGlobal().log(Level.INFO, "Interrupted", e);
			}
			synchronized (otherLock) {
				otherLock.notify();
			}
			try {
				synchronized (lock) {
					lock.wait(1000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}


	public static void main(String[] args) {
		Object lock1 = new Object();
		Object lock2 = new Object();

		new Thread(new PingPong(lock1, lock2, "ping")).start();
		new Thread(new PingPong(lock2, lock1, "pong")).start();
	}

}
