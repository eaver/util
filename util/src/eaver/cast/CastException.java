package eaver.cast;
/**
 * 
 * @author eaver
 *
 */
public class CastException extends RuntimeException{
 
	private static final long serialVersionUID = 1L;

	public CastException(String message){
		super(message);
	}
	
	public CastException(String message,Throwable e){
		super(message,e);
	}
	
	public CastException(Class<?> src,Class<?> dst){
		super("unsupport cast "+src.getName()+" to "+dst.getName());
	}
	public CastException(Class<?> src,String dstname){
		super("unsupport cast "+src.getName()+" to "+dstname);
	}
	
	
}
