package quinn.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;


/**
 * 
 * 
 * 
 * 
 *  以修改设备状态缓存为例子 (伪代码)
 *  学习重入锁
 * 
 *    B计划和C计划； 各自有自己的使用场景
 *    
 *    B计划更适合 CPU耗时操作，例如网络传输
 *    C计划更时候，计算操作，消耗时间短的行为
 * 
 * @author quinn
 *
 */
public class LearnReentrantLock {
	
	
	/**
	 *  修改设备状态(不推荐使用)
	 *  全局锁，单独处理，性能最差
	 * 
	 * @param device
	 */
	public static synchronized void updateDeviceInfo_bad(SocketDevice device) {
		localCacheSet();
	}
	
	
	/**
	 *  改进-A计划 ； 通过细化锁的粒度
	 *  
	 *  获取锁
	 *  
	 */
	private static Map<String, Object> lockObjectMap_bad_A = new HashMap<>();
	
	
	public synchronized static Object getLock_A(String code) {
		if (!lockObjectMap_bad_A.containsKey(code)) {
			return lockObjectMap_bad_A.put(code, new Object());
		}
		return lockObjectMap_bad_A.get(code);
	}
	
	public static void updateDeviceInfo_badA(SocketDevice device) {
		synchronized (getLock_A(device.getCode())) {
			localCacheSet();
		}
	}

	
	/**
	 *  改进-B计划 ； 通过细化锁的粒度  + 改进获取锁的粒度
	 *  
	 *  获取锁
	 *  
	 */
	private static ConcurrentMap<String, Object> lockObjectMap_B = new ConcurrentHashMap<>(); 
	public static Object getLock_B(String code) {
		if(!lockObjectMap_B.containsKey(code)) {
			lockObjectMap_B.putIfAbsent(code, new Object());
		}
		return lockObjectMap_B.get(code);
	}
	
	public static void updateDeviceInfo_B(SocketDevice device) {
		synchronized (getLock_B(device.getCode())) {
			localCacheSet();
		}
	}

	/**
	 *  改进-C计划 ； 通过细化锁的粒度
	 *  
	 *  获取锁
	 *  
	 */	
	private static ConcurrentMap<String, ReentrantLock> lockObjectMap_C = new ConcurrentHashMap<>();
	
	public static ReentrantLock getLock_C(String code) {
		if(!lockObjectMap_C.containsKey(code)) {
			lockObjectMap_C.putIfAbsent(code, new ReentrantLock(true));
		}
		return lockObjectMap_C.get(code);
	}
	
	/**
	 *  从业务上解决, 单位时间内，多条变更操作只处理一条； 情况
	 * 
	 * @param code
	 */
	public static void updateDeviceInfo_C(String code) {
		ReentrantLock lock = getLock_C(code);
		try {
			if(lock.tryLock()) {
				localCacheSet();
			}
			
		} finally {
			lock.unlock();
		}
	}
	
	
	
	/**
	 * 虚假操作
	 */
	private static void localCacheSet() {
		//...省略操作
	}

}


class SocketDevice {
	
	/**
	 *  设备编号
	 * 
	 */
	private String code;
	
	
	/**
	 *  插座名称
	 */
	private String name;
	
	/**
	 *  在线状态
	 * 
	 */
	private boolean online;
	
	/**
	 * 电流
	 */
	private int current;
	
	/**
	 *  电压
	 */
	private int voltage;
	
	/**
	 * 功率
	 */
	private int power;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	public int getVoltage() {
		return voltage;
	}

	public void setVoltage(int voltage) {
		this.voltage = voltage;
	}

	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	
	
}