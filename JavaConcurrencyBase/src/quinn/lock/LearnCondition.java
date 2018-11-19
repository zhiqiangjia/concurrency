package quinn.lock;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * 
 * condition: 条件队列； 队列是一个FIFO队列，队列的每个节点都是等待在Condition对象上的线程的引用

 * 

 *   功能： 主要为ReentrantLock提供： 等待和唤醒的功能


 *  以jdk源码提供的例子展示
 * 
 * @author quinn
 *
 */
public class LearnCondition {
	static BoundedBuffer buffer = new BoundedBuffer();
	static AtomicInteger idIncrements = new AtomicInteger(0);
	
	public static void main(String args[]) throws InterruptedException {
		
		// 生产线程
		Thread[] producerThreads = new Thread[3];
		for (int index = 0; index < 3; index ++) {
			producerThreads[index] =  new Thread(new Runnable() {
				
				@Override
				public void run() {
					for (int index = 0 ;index < 33; index ++) {
						try {
							buffer.put(Thread.currentThread().getName() + " : " + idIncrements.incrementAndGet());
						} catch (InterruptedException e) {
							e.printStackTrace();
						};
					}
					System.err.println(Thread.currentThread().getName() + " 生产完成！");
				}
			});
			
			producerThreads[index].setName("producer-thread-" + index);
			producerThreads[index].setDaemon(false);
		}
		
		// 消费者线程
		Thread[] consumerThreads = new Thread[3];
		for (int index = 0; index < 3; index ++) {
			consumerThreads[index] = new Thread(new Runnable() {
				
				@Override
				public void run() {
					for (;;) {
						try {
							System.out.println(Thread.currentThread().getName() + " 获取的内容：" + buffer.take());
							Thread.sleep(300);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			});
			consumerThreads[index].setName("consumer-thread-" + index);
			consumerThreads[index].setDaemon(false);
		}
		
		// 启动
		for (int index = 0; index < 3; index ++) {
			producerThreads[index].start();
			consumerThreads[index].start();
		}
		
		Thread.sleep(1000);
	}
	

}


class BoundedBuffer {
	final Lock lock = new ReentrantLock();
	final Condition notFull  = lock.newCondition();
	final Condition notEmpty = lock.newCondition();
	
	final String[] items = new String[10];
	
	int putptr, takeptr, count;
	
	/**
	 *  循环按顺序存放，直到count == items.length，阻塞等待take唤醒
	 * 
	 * @param x
	 * @throws InterruptedException
	 */
	 public void put(String x) throws InterruptedException {
		 lock.lock();
		 
		 try {
			 while (count == items.length) {
				 notFull.await();
			 }
			 
			 items[putptr] = x;
			 if (++putptr == items.length) {
				 putptr = 0;
			 }
			 
			 ++ count;
			 
			 notEmpty.signal();
		 } finally {
			 lock.unlock();
		 }
	 }
	 
	 /**
	  *  循环按顺序取出， 直到count == 0; 阻塞等待put唤醒
	  * 
	  * @return
	  * @throws InterruptedException
	  */
	 public String take() throws InterruptedException {
		 lock.lock();
		 
		 try {
			 while (count == 0) {
				 notEmpty.await();
			 }
			 String x = items[takeptr];
			  if (++takeptr == items.length) takeptr = 0;
			  --count;
			  notFull.signal();
			  return x;
		 } finally {
			 lock.unlock();
		 }
	 }
	 
	
}