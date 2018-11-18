package quinn.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * 
 * condition: 条件队列； 队列是一个FIFO队列，队列的每个节点都是等待在Condition对象上的线程的引用
 * 
 *   功能： 主要为ReentrantLock提供： 等待和唤醒的功能
 *  
 *  
 *  以jdk源码提供的例子展示
 * 
 * 
 * @author quinn
 *
 */
public class LearnCondition {
	
	

}


class BoundedBuffer {
	final Lock lock = new ReentrantLock();
	final Condition notFull  = lock.newCondition();
	final Condition notEmpty = lock.newCondition();
	
	final Object[] items = new Object[100];
	
	int putptr, takeptr, count;
	
	
	 public void put(Object x) throws InterruptedException {
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
	 
	 
	 public Object take() throws InterruptedException {
		 lock.lock();
		 
		 try {
			 while (count == 0) {
				 notEmpty.await();
			 }
			  Object x = items[takeptr];
			  if (++takeptr == items.length) takeptr = 0;
			  --count;
			  notFull.signal();
			  return x;
		 } finally {
			 lock.unlock();
		 }
	 }
	 
	
}