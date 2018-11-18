package quinn.collection;

import java.util.Random;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * 
 * 
 * 
 * @author quinn
 *
 */
public class LearnDelayQueue {
private DelayQueue<DelayTask> delayQueue = new DelayQueue<DelayTask>();
	
	public static void main(String args[]) {
		LearnDelayQueue tester = new LearnDelayQueue();
		
		ExecutorService executorService = Executors.newFixedThreadPool(2, new ThreadFactory() {
			AtomicInteger increment = new AtomicInteger();
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setName("worker-thread-" + increment.incrementAndGet());
				t.setDaemon(true);
				return t;
			}
		});
		
		executorService.submit(tester.new DelayTaskCustomer());
		executorService.submit(tester.new DelayTaskCustomer());

		Random random = new Random();
		
		long startTime = System.currentTimeMillis();
		
		for (int i = 0; i < 100; i++) {
			tester.delayQueue.offer(new DelayTask(random.nextInt(20 * 1000), startTime));
		}
		
		try {
			Thread.sleep(21 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		executorService.shutdown();
		
	}
	
	class DelayTaskCustomer implements Runnable {
		
		@Override
		public void run() {
			while (true) {
				try {
					DelayTask delayTask = delayQueue.take();
					delayTask.print();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}

}

class DelayTask implements Delayed {
	
	private final long timeStamp;
	
	// 记录开始时间
	private final long startTime;
	
	private final long delayTime;
	
	public DelayTask (final long delayTime, final long startTime) {
		this.delayTime = delayTime;
		this.timeStamp = System.currentTimeMillis();
		
		this.startTime = startTime;
	}
	
	public long getExpectTime() {
		return timeStamp + delayTime;
	}
	
	@Override
	public int compareTo(Delayed o) {
		if (this.getExpectTime() > ((DelayTask) o).getExpectTime()) {
			return 1;
		} else if (this.getExpectTime() < ((DelayTask) o).getExpectTime()) {
			return -1;
		} 
		
		return 0;
	}
	
	@Override
	public long getDelay(TimeUnit unit) {
		return unit.convert((this.timeStamp + delayTime) - System.currentTimeMillis(), TimeUnit.NANOSECONDS);
	}
	
	/*
	 */
	public void print() {
		long now = System.currentTimeMillis();
		long realDelayTime = now - this.timeStamp;
		long Deviation  = realDelayTime - this.delayTime;
		
		System.out.println(Thread.currentThread().getName() +"--延迟时间是:" 
				+ this.delayTime +"..真实延迟时间.......:" + realDelayTime + "......误差时间(单位毫秒)..::" +
				Deviation + "此时完成任务时间共经历时间: " + (now - startTime));
	}
}
