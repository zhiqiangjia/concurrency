package quinn.threadpool;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 *  调度器
 * 
 * @author quinn
 *
 */
public class LearnExecutors {
	
	private static int corePoolSize = 2 * Runtime.getRuntime().availableProcessors() + 1;
	
	public static void main(String args[]) throws InterruptedException {
		schueduleThreadPool();
		Thread.sleep(5000);
	}
	
	public static void schueduleThreadPool() {
		ScheduledExecutorService scheduleService = Executors.newScheduledThreadPool(corePoolSize, new ThreadFactory() {
			AtomicInteger increment = new AtomicInteger();
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setName("worker-thread-" + increment.incrementAndGet());
				t.setDaemon(false);
				return t;
			}
		});
		
		
		scheduleService.scheduleAtFixedRate(new Runnable() {
			AtomicInteger taskIds = new AtomicInteger();
			
			@Override
			public void run() {
				int taskid = taskIds.incrementAndGet();
				System.out.println(Thread.currentThread().getName()
						+ " 任务id: "
						+ taskid
						+  " 开始时间：" + System.currentTimeMillis());
			}
		}, 1, 2, TimeUnit.SECONDS);
	}
	
	

}
