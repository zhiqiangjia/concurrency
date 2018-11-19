package quinn.threadpool;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 *  任务调度器
 * 
 * @author quinn
 *
 */
public class LearnExecutors {
	
	private static int corePoolSize = 2 * Runtime.getRuntime().availableProcessors() + 1;
	// 设置全局休眠时间
	private static final int sleepTime = 5000;
	// 是否启用休眠
	private static boolean ifsleep = true;
	
	public static void main(String args[]) throws InterruptedException {
		ExecutorService executorService = schueduleThreadPool(1);
		Thread.sleep(12000 );
		// 优雅关闭
		executorService.shutdown();
	}
	
	public static ExecutorService schueduleThreadPool(int choose) {
		ExecutorThreadFactory threaFactory = new ExecutorThreadFactory(choose);
		ScheduledExecutorService scheduleService = Executors.newScheduledThreadPool(1,
				threaFactory);
		ExecutorService executorService = Executors.newFixedThreadPool(corePoolSize, threaFactory);
		switch (choose) {
			case 1:
				fexedDelaySchedule(scheduleService);
				return scheduleService;
			case 2: fixedRateSchedule(scheduleService);
				return scheduleService;

			case 3:newScheduledThreadPool(executorService);
				return executorService;
				default: return null;
		}

	}


	private static void newScheduledThreadPool(ExecutorService executorService) {
		//TODO
	}


	/**
	 *  固定延迟的调度
	 */
	private static void fexedDelaySchedule(ScheduledExecutorService scheduleService) {
		// 固定延迟执行
		scheduleService.scheduleWithFixedDelay(new Runnable() {
			AtomicInteger taskIds = new AtomicInteger();

			@Override
			public void run() {
				int taskid = taskIds.incrementAndGet();

				System.out.println(Thread.currentThread().getName()
						+ " taskId: "
						+ taskid
						+  " startTime" + System.currentTimeMillis());

				if (ifsleep) {
					try {
						Thread.sleep(sleepTime);
					} catch (Throwable t) {
						System.err.println(t);
					}

					System.out.println(Thread.currentThread().getName()
							+ " taskId: "
							+ taskid
							+  " endTime" + System.currentTimeMillis());
				}

			}
		},1, 2, TimeUnit.SECONDS);
	}


	/**
	 * 	固定频率执行
	 * @param scheduleService
	 */
	private static void fixedRateSchedule(ScheduledExecutorService scheduleService) {

		scheduleService.scheduleAtFixedRate(new Runnable() {
			AtomicInteger taskIds = new AtomicInteger();

			@Override
			public void run() {
				int taskid = taskIds.incrementAndGet();
				System.out.println(Thread.currentThread().getName()
						+ " taskId: "
						+ taskid
						+  " startTime" + System.currentTimeMillis());
				if (ifsleep) {
					try {
						Thread.sleep(sleepTime);
					} catch (Throwable t) {
						System.err.println(t);
					}

					System.out.println(Thread.currentThread().getName()
							+ " taskId: "
							+ taskid
							+  " endTime" + System.currentTimeMillis());
				}

			}
		}, 1, 2, TimeUnit.SECONDS);

	}



	static class ExecutorThreadFactory implements ThreadFactory {
		AtomicInteger increment = new AtomicInteger();

		private final int choose;


		public ExecutorThreadFactory(int choose) {
			this.choose = choose;
		}
		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r);
			t.setName(getNamePrefix()+ increment.incrementAndGet());
			t.setDaemon(false);

			return t;
		}


		/**
		 *  线程名称
		 * @param choose
		 * @return
		 */
		private String getNamePrefix() {
			switch (choose) {
				case 1:
					return "fixedDelay-work-thread-";
				case 2: return "fixedDelay-work-thread-";
				default:
					return "work-thread-";
			}
		}

	}
}
