package quinn.atomic;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 *  ���Զ�ID������Ϊ��
 * 
 *  ѧϰAtomicLong ,AtomicInteger, ������
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
	 *  ��ȡid��(�Ƽ���ʽ)
	 * 
	 * @return newId
	 */
	public static String getId_Good() {
		
		return ID_PREFIX + idIncrement_good.incrementAndGet();
	}
	
	
	/**
	 *  
	 * ��ȡid��(���õ�ʹ�÷�ʽ)
	 * 
	 * @return newId
	 */
	public static synchronized String getId_Bad() {
		idIncrement_bad += 1;
		return ID_PREFIX + idIncrement_bad;
	}

}
