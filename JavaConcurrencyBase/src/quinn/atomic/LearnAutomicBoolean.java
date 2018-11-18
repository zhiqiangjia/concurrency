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
	 *  设置在使用 （不好的使用方式）
	 * 
	 * @return true:成功， false:失败
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
	 * 设置取消使用 （不好的使用方式）
	 * 
	 * @return true:成功， false:失败
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
	 *  设置在使用 （推荐使用方式）
	 * 
	 * @return true:成功， false:失败
	 */
	public static boolean setUse_good() {
		return isUse_good.compareAndSet(false, true);
	}
	
	/**
	 * 
	 * 设置取消使用 （推荐使用方式）
	 * 
	 * @return true:成功， false:失败
	 * 
	 */
	public static boolean setNotUse_good() {
		return isUse_good.compareAndSet(true, false);
	}
	
}
