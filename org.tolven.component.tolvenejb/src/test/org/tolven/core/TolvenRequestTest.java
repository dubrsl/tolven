package test.org.tolven.core;

import org.tolven.core.TolvenRequest;

import junit.framework.TestCase;

public class TolvenRequestTest extends TestCase {
    class PrefThread extends Thread {
     PrefThread() {
     }
     
     public void showStatus() {
         TolvenRequest p = TolvenRequest.getInstance();
    	 System.out.println(p.getBrand() + " at " + p.getNow());
     }
     
     public void run() {
    	 try {
			Thread.sleep(Math.round(Math.random()*10000));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   		TolvenRequest p = TolvenRequest.getInstance(); 
// 		p.initializeAccountUser(accountUser);
		p.initializeBrand("Preferences of thread " + Thread.currentThread().getId());
		showStatus();
// 		System.out.println("Preferences from " + Thread.currentThread().getId() + " now: " + p.getNow());
     }
 }
	public void test1() throws InterruptedException {
		TolvenRequest p = TolvenRequest.getInstance(); 
		System.out.println("Preferences now: " + p.getNow());
		TolvenRequest p2 = TolvenRequest.getInstance(); 
		System.out.println("Preferences2 now: " + p2.getNow());
		PrefThread[] threads = new PrefThread[10];
		for (int t = 0; t < 10; t++) {
			threads[t] = new PrefThread();
//			Thread.sleep(1000);
			threads[t].setDaemon(true);
			threads[t].start();
		}
//		for (int t = 0; t < 10; t++) {
//			threads[t].join();
//		}
	}
}
