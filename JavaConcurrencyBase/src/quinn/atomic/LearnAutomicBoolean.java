package quinn.atomic;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 
 * 
 * 
 * @author quinn
 *
 */
public class LearnAutomicBoolean {
	
	
	public static void main(String args[]) {
		
	}
	
	private static AtomicBoolean isUse_good = new AtomicBoolean(false);
	
	private static volatile boolean isUser_bad = false;
	
	/**
	 *  ������ʹ�� �����õ�ʹ�÷�ʽ��
	 * 
	 * @return true:�ɹ��� false:ʧ��
	 */
	public synchronized static boolean setUse_bad() {
		if (!isUser_bad) {
			isUser_bad = true;
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * ����ȡ��ʹ�� �����õ�ʹ�÷�ʽ��
	 * 
	 * @return true:�ɹ��� false:ʧ��
	 * 
	 */
	public synchronized static boolean setNotUse_bad() {
		if (isUser_bad) {
			isUser_bad = false;
			return true;
		}
		
		return false;
	}
	
	/**
	 *  ������ʹ�� ���Ƽ�ʹ�÷�ʽ��
	 * 
	 * @return true:�ɹ��� false:ʧ��
	 */
	public static boolean setUse_good() {
		return isUse_good.compareAndSet(false, true);
	}
	
	/**
	 * 
	 * ����ȡ��ʹ�� ���Ƽ�ʹ�÷�ʽ��
	 * 
	 * @return true:�ɹ��� false:ʧ��
	 * 
	 */
	public static boolean setNotUse_good() {
		return isUse_good.compareAndSet(true, false);
	}
	
}
