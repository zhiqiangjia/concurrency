package quinn.atomic;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 *  以自动ID生成器为例
 * 
 *  学习AtomicLong ,AtomicInteger, 的优势
 * 
 * 
 * @author quinn
 *
 */
public class LearnAtomicLong {
	
	private static AtomicLong idIncrement_good = new AtomicLong(0);
	
	private volatile static Long idIncrement_bad = new Long(0);
	
	private static final String ID_PREFIX = "jiazq";
	
	/**
	 * 
	 *  获取id；(推荐方式)
	 * 
	 * @return newId
	 */
	public static String getId_Good() {
		
		return ID_PREFIX + idIncrement_good.incrementAndGet();
	}
	
	
	/**
	 *  
	 * 获取id；(不好的使用方式)
	 * 
	 * @return newId
	 */
	public static synchronized String getId_Bad() {
		idIncrement_bad += 1;
		return ID_PREFIX + idIncrement_bad;
	}

}
