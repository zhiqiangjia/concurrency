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
 *  ���޸��豸״̬����Ϊ���� (α����)
 *  ѧϰ������
 * 
 *    B�ƻ���C�ƻ��� �������Լ���ʹ�ó���
 *    
 *    B�ƻ����ʺ� CPU��ʱ�������������紫��
 *    C�ƻ���ʱ�򣬼������������ʱ��̵���Ϊ
 * 
 * @author quinn
 *
 */
public class LearnReentrantLock {
	
	
	/**
	 *  �޸��豸״̬(���Ƽ�ʹ��)
	 *  ȫ���������������������
	 * 
	 * @param device
	 */
	public static synchronized void updateDeviceInfo_bad(SocketDevice device) {
		localCacheSet();
	}
	
	
	/**
	 *  �Ľ�-A�ƻ� �� ͨ��ϸ����������
	 *  
	 *  ��ȡ��
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
	 *  �Ľ�-B�ƻ� �� ͨ��ϸ����������  + �Ľ���ȡ��������
	 *  
	 *  ��ȡ��
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
	 *  �Ľ�-C�ƻ� �� ͨ��ϸ����������
	 *  
	 *  ��ȡ��
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
	 *  ��ҵ���Ͻ��, ��λʱ���ڣ������������ֻ����һ���� ���
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
	 * ��ٲ���
	 */
	private static void localCacheSet() {
		//...ʡ�Բ���
	}

}


class SocketDevice {
	
	/**
	 *  �豸���
	 * 
	 */
	private String code;
	
	
	/**
	 *  ��������
	 */
	private String name;
	
	/**
	 *  ����״̬
	 * 
	 */
	private boolean online;
	
	/**
	 * ����
	 */
	private int current;
	
	/**
	 *  ��ѹ
	 */
	private int voltage;
	
	/**
	 * ����
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