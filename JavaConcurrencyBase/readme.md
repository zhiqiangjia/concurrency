



生活不止眼前的苟且，还有诗和远方

---
Java不止简单CURD, 还有并发和NIO




[TOC]


### 目的

- 介绍简单Java 自带的并发工具类；
- 运行简单的demo 先走一波



### CAS
（Compare and swap）比较和替换是设计并发算法时用到的一种技术；

- Java中的底层CAS自带原子性操作；
- 使用Java5+提供的CAS特性而不是使用自己实现的的好处是Java5+中内置的CAS特性可以让你利用底层的你的程序所运行机器的CPU的CAS特性。这会使还有CAS的代码运行更快。

### 原子操作(atomic)
- AtomicBoolean
常用操作
先比较再修改操作；CAS
```
# bad code
volatile boolean ifUse = false;
..
if (ifuse  == false) {
    ifuse = true;
    return true;
}
....



# good code

AtomicBoolean isuse = new AtomicBoolean(false);
..
return isuse.compareAndSet(true, false)

```


- AtomicInteger

 常用操作
 
```

AtomicInteger increment = new AtomicInteger(10);

# 1.新获返回值，再自增
increment.getAndIncrement();

上述表达式的值是： 10； increment最终是11
# 2.先自增，再获取返回值
increment.incrementAndGet();

上述表达式的值是： 11； increment最终是11

# 3. 先获取返回值，再自减
increment.getAndDecrement();

上述表达式的值是： 10； increment最终是9

# 4. 先自减，再获取返回值
increment.decrementAndGet();

上述表达式的值是： 9； increment最终是9


...
```


- AtomicLong

常用使用场景和方式 同 AtomicLong

- AtomicReference

支持对象引用类型的CAS;

- AtomicLongArray

 Long数组类型的CAS


```
 # 1. 先获取指定索引下的值，再更改
 public final long getAndSet(int i, long newValue) ;
 # 2. 先比较指定索引下的值，再更改
 public final boolean compareAndSet(int i, long expect, long update);
 # 3. 先获取再增加
 public final long getAndIncrement(int i)；
 # 4. 先自增，再获取返回值
public final long increment.incrementAndGet(int i);
 
 ...
 
```


 


### 锁(locks)
- ReentrantLock
  主要功能： 可定时，可轮询, 可中断的锁获取操作
          ： 公平队列,非块结构的锁
          
- Condition
condition: 条件队列；
队列是一个FIFO队列，队列的每个节点都是等待在Condition对象上的线程的引用

 功能： 主要为ReentrantLock提供： 等待和唤醒的功能

```
Condition condition = lock.newCondition()

# 常用方法
void await()// 等待
void signal(); // 唤醒
```

  

### 容器(collection)
#### BlockingQueue

阻塞队列，一般用于
    生成者消费者中模式
实现代码解耦和缓冲作用

- LinkedBlockingQueue
链表类型的无界阻塞队列；

- ArrayBlockingQueue

数组类型的有界队列；一般消费者处理能力低于生产者处理能力

- DelayQueue

延迟队列；适用于大量延迟操作任务，定时任务无法处理到的情况

- PriorityBlockingQueue

带有优先级的阻塞队列；

- SynchronousQueue
 无缓冲的阻塞队列；



```
常用方法：
put ： 生产
take: 消费（没有任务就阻塞）

```


#### BlockingDeque
- LinkedBlockingDeque
链表类型的阻塞双端队列；(并发编程-模式篇，详细介绍)


### 线程池

#### Executors

使用Executors原因，它比自定义Thread，提供更多的工作机制，和优雅的关闭方式
 对 ThreadPoolExecutor使用的封装
- newFixedThreadPool; 固定长度的线程池；直接开启最大的线程数量

 = LinkedBlockingQueue + ThreadPoolExecutor
- newCachedThreadPool; 带缓存的线程池，闲置回收，任务繁忙就新增，数量不限
 
(SynchronousQueue + ThreadPoolExecutor)
- newSingleThreadExecutor; 单线程模式 ( LinkedBlockingQueue + ThreadPoolExecutor)
- newScheduledThreadPool; 带调度任务的线程池；类似于timer,支持多线程模式

- shutdown; 设置线程为终止状态，继续完未完成的任务;
- shutdownNow; 中断所有任务，返回未完成任务列表

```
scheduleAtFixedRate: 带固定频率的工作计划

任务计算超时，下一次周期会压缩时间，进行时间追赶

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
		
		
fexedDelaySchedule ： 带固定延迟的工作调度计划
用法同 scheduleAtFixedRate


```


#### ThreadPoolExecutor

```
final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
  		    corePoolSize,  // 池中数量
             20,  // 运行最大临时增加的数量
             15L, // 超过corePoolSize的线程活跃时间
             TimeUnit.SECONDS,// 单位i
             new ArrayBlockingQueue<>(200), //阻塞队列
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
```






### 其他
- Semaphore

信号，常用于定义资源池大小; 边界处理
- CyclicBarrier
多线程并行执行操作类

- ConcurrentHashMap
- ConcurrentLinkedDeque
- ConcurrentLinkedQueue
- ConcurrentSkipListMap
- ConcurrentSkipListSet


### 框架示例

```
##dubbo
1. ExecuteLimitFilter

<dubbo:service interface="com.test.UserServiceBo" ref="userService"
            group="dubbo" version="1.0.0" timeout="3000" >
            <dubbo:method name="sayHello" executes="10" />
</dubbo:service>

使用 semaphore 来控制调用并发执行线程数量


# 2. CachedThreadPool 此线程池可伸缩，线程空闲一分钟后回收，新请求重新创建线程
public class CachedThreadPool implements ThreadPool {

    public Executor getExecutor(URL url) {
        String name = url.getParameter(Constants.THREAD_NAME_KEY, Constants.DEFAULT_THREAD_NAME);
        int cores = url.getParameter(Constants.CORE_THREADS_KEY, Constants.DEFAULT_CORE_THREADS);
        int threads = url.getParameter(Constants.THREADS_KEY, Integer.MAX_VALUE);
        int queues = url.getParameter(Constants.QUEUES_KEY, Constants.DEFAULT_QUEUES);
        int alive = url.getParameter(Constants.ALIVE_KEY, Constants.DEFAULT_ALIVE);
        return new ThreadPoolExecutor(cores, threads, alive, TimeUnit.MILLISECONDS,
                queues == 0 ? new SynchronousQueue<Runnable>() :
                        (queues < 0 ? new LinkedBlockingQueue<Runnable>()
                                : new LinkedBlockingQueue<Runnable>(queues)),
                new NamedThreadFactory(name, true), new AbortPolicyWithReport(name, url));
    }

}


# 3. DubboRegistry 防止重连使用的可重入索
 protected final void connect() {
        try {
            // 检查是否已连接
            if (isAvailable()) {
                return;
            }
            if (logger.isInfoEnabled()) {
                logger.info("Reconnect to registry " + getUrl());
            }
            clientLock.lock();
            try {
                // 双重检查是否已连接
                if (isAvailable()) {
                    return;
                }
                recover();
            } finally {
                clientLock.unlock();
            }
        } catch (Throwable t) { // 忽略所有异常，等待下次重试
            if (getUrl().getParameter(Constants.CHECK_KEY, true)) {
                if (t instanceof RuntimeException) {
                    throw (RuntimeException) t;
                }
                throw new RuntimeException(t.getMessage(), t);
            }
            logger.error("Failed to connect to registry " + getUrl().getAddress() + " from provider/consumer " + NetUtils.getLocalHost() + " use dubbo " + Version.getVersion() + ", cause: " + t.getMessage(), t);
        }
    }

```