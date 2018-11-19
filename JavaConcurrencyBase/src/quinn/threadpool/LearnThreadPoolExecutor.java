package quinn.threadpool;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 *  线程池
 *  quinn
 */
public class LearnThreadPoolExecutor {

    private static int corePoolSize = 2 * Runtime.getRuntime().availableProcessors() + 1;

    static AtomicInteger taskId = new AtomicInteger(0);
    
    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
  		   corePoolSize,
             20,
             15L,
             TimeUnit.SECONDS,
             new ArrayBlockingQueue<>(200),
             new ThreadFactory() {
                 AtomicInteger increment = new AtomicInteger();
                 @Override
                 public Thread newThread(Runnable r) {
                     Thread t = new Thread(r);
                     t.setName("work-threadPool-"+ increment.incrementAndGet());
                     t.setDaemon(false);
                     return t;
                 }
             });

    public static void main(String args[]) throws Exception {
       
        for (int index = 0; index < 100; index ++) {
            threadPool.execute(new PrintClass(taskId.incrementAndGet()));
        }

        Thread.sleep(1000);
    }
    
    private static void stop() {
    	threadPool.shutdown();
    }
 

    static class PrintClass implements Runnable {
        Integer id ;
        public PrintClass(int id) {
           this.id = id;
        }

        @Override
        public void run() {
            try {
                System.out.println(Thread.currentThread().getName() + " execute task :" + id);
                Thread.sleep(2000);
            } catch(Throwable t) {
                System.err.print(t);
            }
        }

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}
        
        
    }



}

