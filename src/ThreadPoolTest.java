import static org.junit.Assert.*;

import org.junit.Test;

import edu.berkeley.cs162.ThreadPool;


public class ThreadPoolTest {
	int numThreads= 0;
	@Test
	public void test() throws InterruptedException {
		
		ThreadPool tp = new ThreadPool(8);
		for (int k =0; k <20; k++){
			//add a bunch of Runnables to the thread pool
			tp.addToQueue(new Runnable(){
			
				public void run(){
					System.out.println("This thread is running");
					numThreads++;
					
				}
			});
		}
		Thread.sleep(30);//need this to wait for all the threads to finish
		//sometimes it doesn't work without the above.
		assertTrue(numThreads == 20);
	}

	@Test
	public void test2() throws InterruptedException{
		ThreadPool tp = new ThreadPool (8);
		for (int k=0; k <3; k++){
			tp.addToQueue(new Runnable(){
				
				public void run(){
					System.out.println("This thread is running");
					numThreads++;
					
				}
			});
		}
		Thread.sleep(30);//need this to wait for all the threads to finish
		//sometimes it doesn't work without the above.
		assertTrue(numThreads == 3);
		
	}
}//ends class

