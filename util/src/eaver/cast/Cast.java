package eaver.cast;
 
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.DataInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * data cast util
 * support three type
 * 	1 primitive and it's object type
 * 2 object(pojo/map) type
 * 3 array and collection type
 *     
 *   public static <T> T cast(Object obj,Class<? extends T> clazz)
 *   
 *   attention:
 *    1. boolean -> Number , true -> 1,false -> 0
 *    2. Number -> boolean , 0->false, otherwise -> 1
 *    3. String - > boolean , str.length() == 1 ? return str.charAt(0) != '0':"true".equalsIgnoreCase(str)
 *    4. boolean -> String , true->"true",false->"false"
 *    5. long -> Date , new Date(long)
 *    6. Date -> long , date.getTime()
 *    7. int -> Date  ,    new Date(1000L* int)
 *    8. Date -> int  ,    (int)(date.getTime()/1000)
 *    7. String <-> Date  ,  yyyy-MM-dd HH:mm:ss  
 *    8. String -> char  ,  str.length()>0?str.charAt(0):throw CastException
 *    9. char -> String  , 	  toString()
 *    10. char -> Number  , ascii cast ,  (int) ((Character)obj).charValue()
 *    11. Number -> char  , ascii cast , (char)((Number)obj).intValue()
 *    
 * @author eaver
 *
 */
public class Cast {
	
	public final static <T> T cast(Object obj,Class<? extends T> clazz){
		return cast(obj, clazz, null);
	}
 
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final static <T> T cast(Object obj,Class<? extends T> clazz,Castors<?,T> castors){
		if(obj == null)
			return null;
		if(clazz == null)
			return (T)obj;
		if(clazz == String.class)
            return (T)toString(obj);
		//some typical class
        if(clazz == Integer.class || clazz == int.class)
            return (T)toInteger(obj);
        if(clazz == Double.class||clazz==double.class)
            return(T)toDouble(obj);
        if(clazz == Long.class || clazz == long.class)
            return (T)toLong(obj);
        
        if(clazz.isAssignableFrom(obj.getClass()))
			return (T)obj; 
        if(clazz == Byte.class || clazz == byte.class)
            return (T)toByte(obj);
        if(clazz == Character.class || clazz == char.class)
            return (T)toCharacter(obj);
        if(clazz == Short.class || clazz == short.class)
            return (T)toShort(obj);
        if(clazz == Float.class || clazz == float.class)
            return (T)toFloat(obj);
        if(clazz == Boolean.class || clazz == boolean.class)
            return (T)toBoolean(obj);
      
        if(clazz == Map.class || clazz == HashMap.class){
        	if(obj instanceof Map){
        		return (T)map2map(obj, clazz, castors);
        	}else{
        		return (T)object2Map(obj, clazz, false, true, castors);
        	}
        }
		if(clazz == BigInteger.class)
            return (T)toBigInteger(obj);
        if(clazz == BigDecimal.class)
            return (T)toBigDecimal(obj);
		if(clazz == InputStream.class)
            return (T)toInputStream(obj);
		if(clazz == Reader.class)
            return (T)toReader(obj);
		if(clazz == Date.class)
            return (T)toDate(obj);
        if(clazz == java.sql.Date.class)
            return (T)toSqlDate(obj);
        if(clazz == java.sql.Time.class)
            return (T)toSqlTime(obj);
        if(clazz == java.sql.Timestamp.class)
            return (T)toSqlTimestamp(obj);
        if(clazz == Number.class)
        	return (T)toNumber(obj);
        if(clazz == Collection.class)
        	return (T)toCollection4Object(obj);
        
        String className   = clazz.getName(); 
        //array
        if(className.charAt(0) == '['){
        	//primitive array
        	if(className.length()==2){
        		switch(className.charAt(1)){
	        		case 'Z':return (T)toArray4boolean(obj);
	        		case 'C':return (T)toArray4char(obj);
	        		case 'B':return (T)toArray4byte(obj);
	        		case 'S':return (T)toArray4short(obj);
	        		case 'I':return (T)toArray4int(obj);
	        		case 'J':return (T)toArray4long(obj);
	        		case 'F':return (T)toArray4float(obj);
	        		case 'D':return (T)toArray4double(obj);
        		}
        	
        	}else if(className.startsWith("[[")){
                return (T) toArray4MultiDimensions(obj, clazz);
            }else if(className.startsWith("[Ljava.lang.")){
    	        if("[Ljava.lang.Boolean;".equals(className))
    	            return (T)toArray4Boolean(obj);
    	        if("[Ljava.lang.Character;".equals(className))
    	            return (T)toArray4Character(obj);
    	        if("[Ljava.lang.Byte;".equals(className))
    	            return (T)toArray4Byte(obj);
    	        if("[Ljava.lang.Short;".equals(className))
    	            return (T)toArray4Short(obj);
    	        if("[Ljava.lang.Integer;".equals(className))
    	            return (T)toArray4Integer(obj);
    	        if("[Ljava.lang.Long;".equals(className))
    	            return (T)toArray4Long(obj);
    	        if("[Ljava.lang.Float;".equals(className))
    	            return (T)toArray4Float(obj);
    	        if("[Ljava.lang.Double;".equals(className))
    	            return (T)toArray4Double(obj);
    	        if("java.lang.Class".equals(className))
    	            return (T)toClass(obj);
    	        if("[Ljava.lang.Object;".equals(className))
    	            return (T)toArray4Object(obj);
            }
        }
       //attention: such as int[][] also match this,so toArray4MultiDimensions() must before it.
		if(Object[].class.isAssignableFrom(clazz))
			return (T)toArray4Object(obj);
        if(Collection.class.isAssignableFrom(clazz))
			return (T)toCollection4Object(obj,clazz);
		if(InputStream.class.isAssignableFrom(clazz))
			return (T)toInputStream(obj,clazz);
		if(Reader.class.isAssignableFrom(clazz))
			return (T)toReader(obj,clazz);
		if(Enum.class.isAssignableFrom(clazz))
			return (T)Enum.valueOf((Class<Enum>)clazz,toString(obj));
		return castObject(obj,clazz,castors);
	}

 
  
	public final static String toString(Object obj){
		if(obj == null)
			return null;
		if(obj instanceof String)
			return (String)obj;
		if(obj instanceof Reader)
			return reader2string((Reader)obj);
		if(obj instanceof InputStream)
			return toString(toReader(obj));
		if(obj instanceof byte[])
			return new String((byte[])obj);
		if(obj instanceof char[])
			return new String((char[])obj);
		if(obj instanceof java.sql.Time)
			return sqltime2string((java.sql.Time)obj);
		if(obj instanceof java.sql.Timestamp)
			return sqltimestamp2string((java.sql.Timestamp)obj);
		if(obj instanceof Date)
			return date2string((Date)obj);
		if(obj instanceof Calendar)
			return date2string(((Calendar)obj).getTime());
		return obj.toString();	 
	}
	
	public final static Boolean toBoolean(Object obj){
		if(obj==null)
			return null;
		if(obj instanceof Boolean)
			return (Boolean)obj;
		if(obj instanceof Number)
			return ((Number)obj).intValue() != 0;
		if(obj instanceof Character)
			return ((Character)obj) != '0';
		if(obj instanceof String)
			return string2boolean((String)obj);
		throw new CastException(obj.getClass(),Boolean.class);	
	}
	
	public final static Character toCharacter(Object obj){
		if(obj == null)
			return null;
		if(obj instanceof Character)
			return (Character)obj;
		if(obj instanceof String)
			return string2character((String)obj);
		if(obj instanceof Number)
			return (char)((Number)obj).intValue();
		if(obj instanceof Boolean)
			return ((Boolean)obj)?'1':'0';
		throw new CastException(obj.getClass(),Character.class);
	}
	
	public final static Byte toByte(Object obj){
		if(obj == null)
			return null;
		if(obj instanceof Byte)
			return (Byte)obj;
		if(obj instanceof Number)
			return ((Number)obj).byteValue();
		if(obj instanceof String)
			return Byte.parseByte(obj.toString());
		if(obj instanceof Character)
			return (byte)(((Character)obj).charValue());
		if(obj instanceof Boolean)
			return ((Boolean)obj)?(byte)1:(byte)0;
		throw new CastException(obj.getClass(),Byte.class);
	}
	
	public final static Short toShort(Object obj){
		if(obj == null)
			return null;
		if(obj instanceof Short)
			return (Short)obj;
		if(obj instanceof Number)
			return ((Number)obj).shortValue();
		if(obj instanceof String)
			return Short.parseShort((String)obj);
		if(obj instanceof Character)
			return (short)((Character)obj).charValue();
		if(obj instanceof Boolean)
			return ((Boolean)obj)?(short)1:(short)0;
		throw new CastException(obj.getClass(),Short.class);
	}
	
	public final static Integer toInteger(Object obj){
		if(obj == null)
			return null;
		if(obj instanceof Integer)
			return (Integer)obj;
		if(obj instanceof Number)
			return ((Number)obj).intValue();
		if(obj instanceof String)
			return Integer.parseInt((String)obj);
		if(obj instanceof Character)
			return (int)((Character)obj).charValue();
		if(obj instanceof Boolean)
			return ((Boolean)obj)?(int)1:(int)0;
		if(obj instanceof Date)
			return (int) (((Date)obj).getTime()/1000);
		throw new CastException(obj.getClass(),Integer.class);
	}
	
	public final static Long toLong(Object obj){
		if(obj == null)
			return null;
		if(obj instanceof Long)
			return (Long)obj;
		if(obj instanceof Number)
			return ((Number)obj).longValue();
		if(obj instanceof String)
			return Long.parseLong((String)obj);
		if(obj instanceof Date)
			return ((Date)obj).getTime();
		if(obj instanceof Character)
			return (long)((Character)obj).charValue();
		if(obj instanceof Boolean)
			return ((Boolean)obj)?(long)1:(long)0;
		throw new CastException(obj.getClass(),Long.class);
	}
	
	public final static Float toFloat(Object obj){
		if(obj == null)
			return null;
		if(obj instanceof Float)
			return (Float)obj;
		if(obj instanceof Number)
			return ((Number)obj).floatValue();
		if(obj instanceof String)
			return Float.parseFloat((String)obj);
		if(obj instanceof Character)
			return (float)((Character)obj).charValue();
		if(obj instanceof Boolean)
			return ((Boolean)obj)?(float)1:(float)0;
		throw new CastException(obj.getClass(),Float.class);
	}
	
	public final static Double toDouble(Object obj){
		if(obj == null)
			return null;
		if(obj instanceof Double)
			return (Double)obj;
		if(obj instanceof Number)
			return ((Number)obj).doubleValue();
		if(obj instanceof String)
			return Double.parseDouble((String)obj);
		if(obj instanceof Character)
			return (double)((Character)obj).charValue();
		if(obj instanceof Boolean)
			return ((Boolean)obj)?(double)1:(double)0;
		throw new CastException(obj.getClass(),Float.class);
	}
	
	public final static BigInteger toBigInteger(Object obj){
		if(obj == null)
			return null;
		if(obj instanceof BigInteger)
			return (BigInteger)obj;
		if(obj instanceof BigDecimal)
			return ((BigDecimal)obj).toBigInteger();
		if(obj instanceof Number)
			return BigInteger.valueOf(((Number)obj).longValue());
		if(obj instanceof String)
			return new BigInteger((String)obj);
		if(obj instanceof Character)
			return BigInteger.valueOf((long)((Character)obj).charValue());
		if(obj instanceof Boolean)
			return new BigInteger((((Boolean)obj)?"1":"0"));
		throw new CastException(obj.getClass(),BigInteger.class);			
	}
	
	public final static BigDecimal toBigDecimal(Object obj){
		if(obj == null)
			return null;
		if(obj instanceof BigDecimal)
			return (BigDecimal)obj;
		if(obj instanceof BigInteger)
			return new BigDecimal(((BigInteger)obj));
		if(obj instanceof Number)
			return BigDecimal.valueOf(((Number)obj).doubleValue());
		if(obj instanceof String)
			return new BigDecimal((String)obj);
		if(obj instanceof Character)
			return BigDecimal.valueOf((double)((Character)obj).charValue());
		if(obj instanceof Boolean)
			return new BigDecimal((((Boolean)obj)?"1":"0"));
		throw new CastException(obj.getClass(),BigDecimal.class);			
	}
	
	public final static Number toNumber(Object obj){
		if(obj==null)
			return null;
		if(obj instanceof Number)
			return (Number)obj;
		if(obj instanceof String){
			String str = (String)obj;
			double d = Double.parseDouble(str);
			if((int)d == d)
				return (int)d;
			else if((long)d == d)
				return (long)d;
			else
				return d;
		}
		if(obj instanceof Boolean)
			return ((Boolean)obj)?1:0;
		if(obj instanceof Character)
			return (int)((Character)obj).charValue();
		if(obj instanceof Date)
			return ((Date)obj).getTime();
		return toNumber(obj.toString());
	}
	
	public final static Date toDate(Object obj){
		if(obj == null)
			return null;
		if(obj instanceof Date)
			return (Date)obj;
		if(obj instanceof Long)
			return new Date((Long)obj);
		if(obj instanceof Integer)
			return toDate(1000L*(Integer)obj);
		if(obj instanceof String)
			return string2date((String)obj);
		throw new CastException(obj.getClass(),Date.class);
	}
	
	public final static java.sql.Date toSqlDate(Object obj){
		if(obj == null)
			return null;
		if(obj instanceof java.sql.Date)
			return (java.sql.Date)obj;
		if(obj instanceof Date)
			return new java.sql.Date(((Date)obj).getTime());
		if(obj instanceof Long)
			return new java.sql.Date((Long)obj);
		if(obj instanceof Integer)
			return new java.sql.Date(1000L*(Integer)obj);
		if(obj instanceof String)
			return string2sqldate((String)obj);
		throw new CastException(obj.getClass(),java.sql.Date.class);
	}

	public final static java.sql.Time toSqlTime(Object obj){
		if(obj == null)
			return null;
		if(obj instanceof java.sql.Time)
			return (java.sql.Time)obj;
		if(obj instanceof Date)
			return new java.sql.Time(((Date)obj).getTime());
		if(obj instanceof String)
			return string2sqltime((String)obj);
		if(obj instanceof Integer)
			return new java.sql.Time(1000L*(Integer)obj);
		if(obj instanceof Long)
			return new java.sql.Time((Long)obj);
		throw new CastException(obj.getClass(),java.sql.Time.class);
	}

	public final static java.sql.Timestamp toSqlTimestamp(Object obj){
		if(obj == null)
			return null;
		if(obj instanceof java.sql.Timestamp)
			return (java.sql.Timestamp)obj;
		if(obj instanceof Date)
			return new java.sql.Timestamp(((Date)obj).getTime());
		if(obj instanceof String)
			return string2sqltimestamp((String)obj);
		if(obj instanceof Integer)
			return new java.sql.Timestamp(1000L*(Integer)obj);
		if(obj instanceof Long)
			return new java.sql.Timestamp((Long)obj);
		throw new CastException(obj.getClass(),java.sql.Timestamp.class);
	}

	public final static <T> T toReader(Object obj){
		return toReader(obj,null);
	}
	@SuppressWarnings("unchecked")
	public final static <T> T toReader(Object obj,Class<T> clazz){
		if(obj == null)
			return null;
		Reader reader = null;
		if(obj instanceof Reader)
			reader = (Reader)reader;
		else if(obj instanceof String)
			reader = new StringReader((String)obj);
		else if(obj instanceof InputStream)
			reader = new InputStreamReader((InputStream)obj);
		else
			reader = new CharArrayReader(toArray4char(obj));
		if(clazz == null)
			return (T)reader;
		if(clazz == BufferedReader.class || BufferedReader.class.isAssignableFrom(clazz)){
			return (T) new BufferedReader(reader);
		}else if(clazz == CharArrayReader.class || CharArrayReader.class.isAssignableFrom(clazz)){
			if(reader instanceof CharArrayReader){
				return (T)reader;
			}else{ 
				return (T)new CharArrayReader(toArray4char(reader));
			}
		}else if(clazz == FileReader.class || FileReader.class.isAssignableFrom(clazz)){
			if(reader instanceof FileReader){
				return (T)reader;
			}else{
				throw new CastException("unsupport "+obj.getClass().getName()+" cast to FileReader");
			}
		}else if(clazz == InputStreamReader.class || InputStreamReader.class.isAssignableFrom(clazz)){
			if(reader instanceof InputStreamReader){
				return (T)reader;
			}else{
				return (T)new InputStreamReader((InputStream)toInputStream(reader));
			}
		}else if(clazz == StringReader.class || StringReader.class.isAssignableFrom(clazz)){
			if(reader instanceof StringReader){
				return (T)reader;
			}else{
				return (T)new StringReader(toString(reader));
			} 
		}
		return (T)reader;
	}
	
	public final static <T> T toInputStream(Object obj){
		return toInputStream(obj,null);
	}

	@SuppressWarnings("unchecked")
	public final static <T> T toInputStream(Object obj,Class<T> clazz){
		if(obj == null)
			return null;
		InputStream is = null;
		if(obj instanceof InputStream){
			is = (InputStream)obj;
		}else if(obj instanceof byte[]){
			is = new  ByteArrayInputStream((byte[])obj);
		}else if(obj instanceof Reader){
			byte[] arr = toArray4byte(obj);
			is = new  ByteArrayInputStream(arr);
		}else if(obj instanceof String){
			is = new ByteArrayInputStream(((String)obj).getBytes());
		}else{
			is = new ByteArrayInputStream(toArray4byte(obj));
		}
		if(clazz == null)
			return (T)is;
		 if(clazz == ByteArrayInputStream.class || ByteArrayInputStream.class.isAssignableFrom(clazz)){
			 if(is instanceof ByteArrayInputStream){
				 return (T)(ByteArrayInputStream)is;
			 }else{
				return (T) new  ByteArrayInputStream(toArray4byte(is));
			 }
		 }else if(clazz == BufferedInputStream.class|| BufferedInputStream.class.isAssignableFrom(clazz)){
				return (T) new BufferedInputStream(is);
		 }else if(clazz == DataInputStream.class|| DataInputStream.class.isAssignableFrom(clazz)){
				return (T) new DataInputStream(is);
		 }else if(clazz == ObjectInputStream.class|| ObjectInputStream.class.isAssignableFrom(clazz)){
				try {
					return (T) new ObjectInputStream(is);
				} catch (IOException e) {
					throw new CastException(obj.getClass().getName()+" cast to ObjectInputStream:"+e.getMessage());
				}
		 }else if(clazz == PushbackInputStream.class|| PushbackInputStream.class.isAssignableFrom(clazz)){
				return (T) new PushbackInputStream((InputStream)obj);
		 }else{
			 return (T)is;
		 }
	}
	
	public final static boolean[] toArray4boolean(Object obj){
		if(obj == null)
			return null;
		if(obj instanceof boolean[])
			return (boolean[])obj;
		if(obj instanceof Boolean[])
			return BooleanArrayAdaptor.toBooleanArray((Boolean[])obj);
		if(obj instanceof Number[])
			return NumberArrayAdaptor.toBooleanArray((Number[])obj);
		if(obj instanceof String[])
			return StringArrayAdaptor.toBooleanArray((String[])obj);
		if(obj instanceof Character[])
			return CharArrayAdaptor.toBooleanArray((Character[])obj);
		if(obj instanceof byte[])
			return ByteArrayAdaptor.toBooleanArray((byte[])obj);
		if(obj instanceof char[])
			return CharArrayAdaptor.toBooleanArray((char[])obj);
		if(obj instanceof short[])
			return ShortArrayAdaptor.toBooleanArray((short[])obj);
		if(obj instanceof int[])
			return IntArrayAdaptor.toBooleanArray((int[])obj);
		if(obj instanceof long[])
			return LongArrayAdaptor.toBooleanArray((long[])obj);
		if(obj instanceof float[])
			return FloatArrayAdaptor.toBooleanArray((float[])obj);
		if(obj instanceof double[])
			return DoubleArrayAdaptor.toBooleanArray((double[])obj);
		if(obj instanceof Collection)
			return toArray4boolean(((Collection<?>)obj).toArray());
		throw new CastException(obj.getClass(),boolean[].class);
	}

	public final static Boolean[] toArray4Boolean(Object obj){
		if(obj == null)
			return null;
		if(obj instanceof Boolean[])
			return (Boolean[])obj;
		boolean[] t = toArray4boolean(obj);
		return BooleanArrayAdaptor.toBooleanArray4Object(t);
	}

	public final static byte[] toArray4byte(Object obj){
		if(obj == null)
			return null;
		if(obj instanceof byte[])
			return (byte[])obj;
		if(obj instanceof String)
			return ((String)obj).getBytes();
		if(obj instanceof InputStream)
			return inputstream2bytes((InputStream)obj);
		if(obj instanceof Reader)
			return toArray4byte(toString(obj));
		if(obj instanceof Number[])
			return NumberArrayAdaptor.toByteArray((Number[])obj);
		if(obj instanceof int[])
			return IntArrayAdaptor.toByteArray((int[])obj);
		if(obj instanceof long[])
			return LongArrayAdaptor.toByteArray((long[])obj);
		if(obj instanceof double[])
			return DoubleArrayAdaptor.toByteArray((double[])obj);
		if(obj instanceof float[])
			return FloatArrayAdaptor.toByteArray((float[])obj);
		if(obj instanceof char[])
			return CharArrayAdaptor.toByteArray((char[])obj);
		if(obj instanceof boolean[])
			return BooleanArrayAdaptor.toByteArray((boolean[])obj);
		if(obj instanceof Boolean[])
			return BooleanArrayAdaptor.toByteArray((Boolean[])obj);
		if(obj instanceof Character[])
			return CharArrayAdaptor.toByteArray((Character[])obj);
		if(obj instanceof String[])
			return StringArrayAdaptor.toByteArray((String[])obj);
		if(obj instanceof Collection)
			return toArray4byte(((Collection<?>)obj).toArray());
		if(obj instanceof Number)
			return new byte[]{((Number)obj).byteValue()};
		if(obj instanceof Character)
			return new byte[]{(byte)((Character)obj).charValue()};
		throw new CastException(obj.getClass(),byte[].class);
	}

	public final static Byte[] toArray4Byte(Object obj){
		if(obj == null)
			return null;
		if(obj instanceof Byte[])
			return (Byte[])obj;
		return ByteArrayAdaptor.toByteArray4Object(toArray4byte(obj));
	}


	public final static char[] toArray4char(Object obj){
		if(obj == null)
			return null;
		if(obj instanceof char[])
			return (char[])obj;
		if(obj instanceof String)
			return ((String)obj).toCharArray();
		if(obj instanceof Reader)
			return toArray4char(toString(obj));
		if(obj instanceof Character[])
			return CharArrayAdaptor.toCharArray((Character[])obj);
		if(obj instanceof Number[])
			return NumberArrayAdaptor.toCharArray((Number[])obj);
		if(obj instanceof Boolean[])
			return BooleanArrayAdaptor.toCharArray((Boolean[])obj);
		if(obj instanceof boolean[])
			return BooleanArrayAdaptor.toCharArray((boolean[])obj);
		if(obj instanceof byte[])
			return ByteArrayAdaptor.toCharArray((byte[])obj);
		if(obj instanceof short[])
			return ShortArrayAdaptor.toCharArray((short[])obj);
		if(obj instanceof int[])
			return IntArrayAdaptor.toCharArray((int[])obj);
		if(obj instanceof long[])
			return LongArrayAdaptor.toCharArray((long[])obj);
		if(obj instanceof float[])
			return FloatArrayAdaptor.toCharArray((float[])obj);
		if(obj instanceof double[])
			return DoubleArrayAdaptor.toCharArray((double[])obj);
		if(obj instanceof String[])
			return StringArrayAdaptor.toCharArray((String[])obj);
		if(obj instanceof Collection)
			return toArray4char(((Collection<?>)obj).toArray());
		throw new CastException(obj.getClass(),char[].class);
	}

	public final static Character[] toArray4Character(Object obj){
		if(obj == null)
			return null;
		if(obj instanceof Character[])
			return (Character[])obj;
		return CharArrayAdaptor.toCharArray4Object(toArray4char(obj));
	}

	public final static short[] toArray4short(Object obj){
		if(obj == null)
			return null;
		if(obj instanceof short[])
			return (short[])obj;
		if(obj instanceof byte[])
			return ByteArrayAdaptor.toShortArray((byte[])obj);
		if(obj instanceof int[])
			return IntArrayAdaptor.toShortArray((int[])obj);
		if(obj instanceof long[])
			return LongArrayAdaptor.toShortArray((long[])obj);
		if(obj instanceof double[])
			return DoubleArrayAdaptor.toShortArray((double[])obj);
		if(obj instanceof float[])
			return FloatArrayAdaptor.toShortArray((float[])obj);
		if(obj instanceof char[])
			return CharArrayAdaptor.toShortArray((char[])obj);
		if(obj instanceof boolean[])
			return BooleanArrayAdaptor.toShortArray((boolean[])obj);
		if(obj instanceof Character[])
			return CharArrayAdaptor.toShortArray((Character[])obj);
		if(obj instanceof Number[])
			return NumberArrayAdaptor.toShortArray((Number[])obj);
		if(obj instanceof String[])
			return StringArrayAdaptor.toShortArray((String[])obj);
		if(obj instanceof Boolean[])
			return BooleanArrayAdaptor.toShortArray((Boolean[])obj);
		if(obj instanceof Collection)
			return toArray4short(((Collection<?>)obj).toArray());
		throw new CastException(obj.getClass(),short[].class);
	}

	public final static Short[] toArray4Short(Object obj){
		if(obj == null)
			return null;
		if(obj instanceof Short[])
			return (Short[])obj;
		return ShortArrayAdaptor.toShortArray4Object(toArray4short(obj));
	}

	@SuppressWarnings("rawtypes")
	public final static int[] toArray4int(Object obj){
		if(obj == null)
			return null;
		if(obj instanceof int[])
			return (int[])obj;
		if(obj instanceof byte[])
			return ByteArrayAdaptor.toIntArray((byte[])obj);
		if(obj instanceof short[])
			return ShortArrayAdaptor.toIntArray((short[])obj);
		if(obj instanceof long[])
			return LongArrayAdaptor.toIntArray((long[])obj);
		if(obj instanceof double[])
			return DoubleArrayAdaptor.toIntArray((double[])obj);
		if(obj instanceof float[])
			return FloatArrayAdaptor.toIntArray((float[])obj);
		if(obj instanceof char[])
			return CharArrayAdaptor.toIntArray((char[])obj);
		if(obj instanceof Character[])
			return CharArrayAdaptor.toIntArray((Character[])obj);
		if(obj instanceof Number[])
			return NumberArrayAdaptor.toIntArray((Number[])obj);
		if(obj instanceof String[])
			return StringArrayAdaptor.toIntArray((String[])obj);
		if(obj instanceof Boolean[])
			return BooleanArrayAdaptor.toIntArray((Boolean[])obj);
		if(obj instanceof boolean[])
			return BooleanArrayAdaptor.toIntArray((boolean[])obj);
		if(obj instanceof Collection)
			return toArray4int(((Collection)obj).toArray());
		throw new CastException(obj.getClass(),int[].class);
	}

	public final static Integer[] toArray4Integer(Object obj){
		if(obj == null)
			return null;
		if(obj instanceof Integer[])
			return (Integer[])obj;
		return IntArrayAdaptor.toIntArray4Object(toArray4int(obj));
	}
	public final static long[] toArray4long(Object obj){
		if(obj == null)
			return null;
		if(obj instanceof long[])
			return (long[])obj;
		if(obj instanceof byte[])
			return ByteArrayAdaptor.toLongArray((byte[])obj);
		if(obj instanceof int[])
			return IntArrayAdaptor.toLongArray((int[])obj);
		if(obj instanceof short[])
			return ShortArrayAdaptor.toLongArray((short[])obj);
		if(obj instanceof double[])
			return DoubleArrayAdaptor.toLongArray((double[])obj);
		if(obj instanceof float[])
			return FloatArrayAdaptor.toLongArray((float[])obj);
		if(obj instanceof char[])
			return CharArrayAdaptor.toLongArray((char[])obj);
		if(obj instanceof Character[])
			return CharArrayAdaptor.toLongArray((Character[])obj);
		if(obj instanceof Number[])
			return NumberArrayAdaptor.toLongArray((Number[])obj);
		if(obj instanceof String[])
			return StringArrayAdaptor.toLongArray((String[])obj);
		if(obj instanceof Boolean[])
			return BooleanArrayAdaptor.toLongArray((Boolean[])obj);
		if(obj instanceof boolean[])
			return BooleanArrayAdaptor.toLongArray((boolean[])obj);
		if(obj instanceof Collection)
			return toArray4long(((Collection<?>)obj).toArray());
		throw new CastException(obj.getClass(),long[].class);
	}

	public final static Long[] toArray4Long(Object obj){
		if(obj == null)
			return null;
		if(obj instanceof Long[])
			return (Long[])obj;
		return LongArrayAdaptor.toLongArray4Object(toArray4long(obj));
	}

	public final static float[] toArray4float(Object obj){
		if(obj == null)
			return null;
		if(obj instanceof float[])
			return (float[])obj;
		if(obj instanceof byte[])
			return ByteArrayAdaptor.toFloatArray((byte[])obj);
		if(obj instanceof int[])
			return IntArrayAdaptor.toFloatArray((int[])obj);
		if(obj instanceof short[])
			return ShortArrayAdaptor.toFloatArray((short[])obj);
		if(obj instanceof double[])
			return DoubleArrayAdaptor.toFloatArray((double[])obj);
		if(obj instanceof long[])
			return LongArrayAdaptor.toFloatArray((long[])obj);
		if(obj instanceof char[])
			return CharArrayAdaptor.toFloatArray((char[])obj);
		if(obj instanceof Character[])
			return CharArrayAdaptor.toFloatArray((Character[])obj);
		if(obj instanceof Number[])
			return NumberArrayAdaptor.toFloatArray((Number[])obj);
		if(obj instanceof String[])
			return StringArrayAdaptor.toFloatArray((String[])obj);
		if(obj instanceof Boolean[])
			return BooleanArrayAdaptor.toFloatArray((Boolean[])obj);
		if(obj instanceof boolean[])
			return BooleanArrayAdaptor.toFloatArray((boolean[])obj);
		if(obj instanceof Collection)
			return toArray4float(((Collection<?>)obj).toArray());
		throw new CastException(obj.getClass(),float[].class);
	}

	public final static Float[] toArray4Float(Object obj){
		if(obj == null)
			return null;
		if(obj instanceof Float[])
			return (Float[])obj;
		return FloatArrayAdaptor.toFloatArray4Object(toArray4float(obj));
	}

	public final static double[] toArray4double(Object obj){
		if(obj == null)
			return null;
		if(obj instanceof double[])
			return (double[])obj;
		if(obj instanceof byte[])
			return ByteArrayAdaptor.toDoubleArray((byte[])obj);
		if(obj instanceof int[])
			return IntArrayAdaptor.toDoubleArray((int[])obj);
		if(obj instanceof short[])
			return ShortArrayAdaptor.toDoubleArray((short[])obj);
		if(obj instanceof float[])
			return FloatArrayAdaptor.toDoubleArray((float[])obj);
		if(obj instanceof long[])
			return LongArrayAdaptor.toDoubleArray((long[])obj);
		if(obj instanceof char[])
			return CharArrayAdaptor.toDoubleArray((char[])obj);
		if(obj instanceof Character[])
			return CharArrayAdaptor.toDoubleArray((Character[])obj);
		if(obj instanceof Number[])
			return NumberArrayAdaptor.toDoubleArray((Number[])obj);
		if(obj instanceof String[])
			return StringArrayAdaptor.toDoubleArray((String[])obj);
		if(obj instanceof Boolean[])
			return BooleanArrayAdaptor.toDoubleArray((Boolean[])obj);
		if(obj instanceof boolean[])
			return BooleanArrayAdaptor.toDoubleArray((boolean[])obj);
		if(obj instanceof Collection)
			return toArray4double(((Collection<?>)obj).toArray());
		throw new CastException(obj.getClass(),double[].class);
	}

	public final static Double[] toArray4Double(Object obj){
		if(obj == null)
			return null;
		if(obj instanceof Double[])
			return (Double[])obj;
		return DoubleArrayAdaptor.toDoubleArray4Object(toArray4double(obj));
	}



	@SuppressWarnings({"rawtypes" })
	public final static Object[] toArray4Object(Object obj){
		if(obj == null)
			return null;
		if(obj instanceof Object[])
			return (Object[])obj;
		if(obj instanceof Collection)
			return ((Collection)obj).toArray();
		if(obj instanceof boolean[])
			return BooleanArrayAdaptor.toBooleanArray4Object((boolean[])obj);
		if(obj instanceof int[])
			return IntArrayAdaptor.toIntArray4Object((int[])obj);
		if(obj instanceof double[])
			return DoubleArrayAdaptor.toDoubleArray4Object((double[])obj);
		if(obj instanceof long[])
			return LongArrayAdaptor.toLongArray4Object((long[])obj);
		if(obj instanceof short[])
			return ShortArrayAdaptor.toShortArray4Object((short[])obj);
		if(obj instanceof float[])
			return FloatArrayAdaptor.toFloatArray4Object((float[])obj);
		if(obj instanceof byte[])
			return ByteArrayAdaptor.toByteArray4Object((byte[])obj);
		if(obj instanceof char[])
			return CharArrayAdaptor.toCharArray4Object((char[])obj);
		return new Object[]{obj};
	}
	
	  /**
     * 多维数据的转换
     *  只支持数组到数组的转换
     * @param obj
     * @param clazz
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
	public final static <T> T toArray4MultiDimensions(Object obj,Class<T> clazz){
        if(obj == null)
            return null;
        if(clazz == null)
            return (T)obj;
        String className = clazz.getName();
        //获取数组的维数
        int dimension = getDimension(className);
        /**
         * 一维数组
         */
        if(dimension <= 1 )
            return cast(obj,clazz);
        
        /**
         * 二维或二维数组以上的数组
         */
        //获取第一维的元素个数
        int length = Array.getLength(obj);
        //创建目标数组  长度为length
        Object targetArray = Array.newInstance(clazz.getComponentType(),length);
        //创建一个比目标数组维度少1 的数组    长度为0  主要是为了得到类型
        Class<?> subClazz =    Array.newInstance(clazz.getComponentType().getComponentType(),0).getClass();
        for(int i=0;i<length;i++)
            Array.set(targetArray,i,toArray4MultiDimensions(Array.get(obj, i), subClazz));
        return (T)targetArray;
    }
    
    public final static <T> T toCollection4Object(Object obj){
    	return toCollection4Object(obj, null);
    }

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final static <T> T toCollection4Object(Object obj,Class<T> collType){
		if(obj == null)
			return null;
		Collection coll = null;
		if(coll instanceof Collection){
			coll = (Collection)obj;
		}else if(obj instanceof Object[]){
			coll = Arrays.asList((Object[])obj);
		}else{
			coll = Arrays.asList(toArray4Object(obj));
		}
		if(collType == null)
			return (T) coll;
		
		if(collType == ArrayBlockingQueue.class){
			ArrayBlockingQueue list = new ArrayBlockingQueue(coll.size());
			list.addAll(coll);
			return (T)list;
		}else if(collType == LinkedBlockingQueue.class){
			LinkedBlockingQueue list = new LinkedBlockingQueue(coll.size());
			list.addAll(coll);
			return (T)list;
		}else{
			Collection rs = (Collection)newInstance(collType);
			rs.addAll(coll);
			return (T)rs;
		}
	}
 

	public final static void arraycopy(Object src,int srcPos,Object dest,int destPos,int length){
		arraycopy(src, srcPos, dest, destPos, length, null);
	}
	
	public final static <S,T>  void arraycopy(Object src,int srcPos,Object dest,int destPos,int length,Castors<S,T> castors){
		if(src == null || dest  == null)
			throw new NullPointerException((src == null?"src":"target")+" is null");
		if(length<=0)
			throw new RuntimeException("length is "+length);
		if(!src.getClass().isArray())
			throw new RuntimeException("src is not array type");
		if(!dest.getClass().isArray())
			throw new RuntimeException("dest is not array type");
		/**
		 * 类型相同 , 则调用jvm自带的数组复制方法
		 * 当有自定义的转换配置时除外
		 */
		if(castors==null && src.getClass() == dest.getClass()){ 
			System.arraycopy(src, srcPos, dest, destPos, length);
			return;
		}
		int srcLength = Array.getLength(src);
		int destLength = Array.getLength(dest);
		if(srcPos<0||srcPos>=srcLength)
			throw new IndexOutOfBoundsException(srcPos+"");
		if(srcPos+length > srcLength)
			throw new IndexOutOfBoundsException("src "+(srcPos+length-1));
		if(destPos<0||destPos>=destLength)
			throw new IndexOutOfBoundsException(destPos+"");
		if(destPos+length >destLength)
			throw new IndexOutOfBoundsException("target "+(destPos+length-1));
		
		@SuppressWarnings("unchecked")
		Class<T> type = (Class<T>)dest.getClass().getComponentType(); 
		for(int i=srcPos;i<srcPos+length;i++)
			Array.set(dest, destPos+i-srcPos, cast(Array.get(src, i),type,castors));
	}
	
	public final static <T> Collection<T> toCollection(Object obj,Class<? extends T> type){
		return toCollection(obj, type,null);
	}
	
	@SuppressWarnings("unchecked")
	public final static <T> Collection<T> toCollection(Object obj,Class<? extends T> type,Castors<?,T> castors){
		if(obj == null)
			return null;
		Collection<?> coll = (Collection<?>) toCollection4Object(obj);
		if(type == null)
			return (Collection<T>)coll;
		Iterator<?> iterator = coll.iterator();
		Object item = null;
		boolean[] notNeedCastFlags = new boolean[coll.size()];
		int index = 0;
		boolean allNotNeedCast = true;
		while(iterator.hasNext()){
			item = iterator.next();
			notNeedCastFlags[index] = item == null || item.getClass() == type || type.isAssignableFrom(item.getClass());
			allNotNeedCast = allNotNeedCast && notNeedCastFlags[index];
			index++;
		}
		if(allNotNeedCast)
			return (Collection<T>)obj;
		Collection<T> newcoll = null;
		try {
			newcoll = (Collection<T>)Cast.newInstance(coll.getClass());
		} catch (RuntimeException e) {
			//throw new CastException(obj.getClass().getName() +" cast to Collection<"+type.getName()+"> error("+e.getClass().getName()+"):"+e.getMessage());
			newcoll = new ArrayList<T>(coll.size());
		}
		iterator = coll.iterator();
		index = 0;
		while(iterator.hasNext()){
			if(notNeedCastFlags[index++])
				newcoll.add((T)iterator.next());
			else
				newcoll.add(cast(iterator.next(),type,castors));
		}
		return newcoll;
	}
	
	public final static Object toArray(Object obj,Class<?> type){
		return toArray(obj, type,null);
	}
	
	public final static Object toArray(Object obj,Class<?> type,Castors<?,?> castors){
		if(obj == null)
			return null;
		if(type == null)
			throw new RuntimeException(obj + " toArray error:type is null.");
		int len = 0;
		if(obj instanceof Collection){
			len = ((Collection<?>)obj).size();
			obj =((Collection<?>)obj).toArray();
		}else if(obj.getClass().isArray()){
			len = Array.getLength(obj);
		}else{
			len = 1;
			obj = new Object[]{obj};
		}
		Object newValue = Array.newInstance(type, len);
		arraycopy(obj, 0, newValue, 0, len,castors);
		return newValue;
	}
	 
	public final static <T> T resultSetToObject(ResultSet rs,String[] columnLabels,int[] columnTypes,Class<? extends T> clazz,boolean isBasicType) throws SQLException{
		 if(clazz == null)
			 throw new CastException("resultSetToObject return type is null");
		 if(isBasicType)
			 return cast(getFromResultSet(rs,columnLabels[0],columnTypes[0]),clazz);
		 else if(Map.class==clazz||HashMap.class==clazz||Map.class.isAssignableFrom(clazz))
			 return resultSetToMap(rs,columnLabels,columnTypes,clazz);
		 else
			 return resultSetToPOJO(rs,columnLabels,columnTypes,clazz); 
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private final static <T> T resultSetToMap(ResultSet rs,String[] columnLabels,int[] columnTypes,Class<? extends T> clazz){
		Object columnValue = null;
		T instance = null;
		try{
			instance = (T)Cast.newInstance(clazz);
			for(int i=0;i<columnLabels.length;i++){
				columnValue = Cast.getFromResultSet(rs,columnLabels[i],columnTypes[i]);
				if(columnValue!=null)
					((Map)instance).put(columnLabels[i], columnValue);
			}
			return instance;
		}catch(Throwable e){
			throw new CastException("cast to "+clazz.getName()+" message : "+e.getMessage()+" ["+e.getClass().getName()+"]",e);
		}
	}
	

	private final static <T> T resultSetToPOJO(ResultSet rs,String[] columnLabels,int[] columnTypes,Class<? extends T> clazz){
		Object columnValue = null;
		Map<String, GetSet> getsets = GetSetUtil.getGetSetBean(clazz).getLowerKeySetMethods();
		GetSet getset = null;
		try{
			T instance = clazz.newInstance();
			for(int i=0;i<columnLabels.length;i++){
				getset = getsets.get(columnLabels[i]);
				if(getset!=null){
					columnValue = getFromResultSet(rs,columnLabels[i],columnTypes[i]);
					getset.setAndCast(instance,columnValue);
				} 
			}
			return instance;
		}catch(Throwable e){
			throw new CastException("cast to "+clazz.getName()+" message : "+e.getMessage()+" ["+e.getClass().getName()+"]",e);
		}
	}
	
	private final static Object getFromResultSet(ResultSet rs,String columnLabel,int columnType) throws SQLException{
		switch (columnType) {
			case Types.ROWID:
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:return rs.getString(columnLabel);
			case Types.NCHAR:
			case Types.NVARCHAR:
			case Types.LONGNVARCHAR:return rs.getNString(columnLabel);
			case Types.BINARY:
			case Types.BLOB:
			case Types.VARBINARY:
			case Types.LONGVARBINARY:return rs.getBinaryStream(columnLabel);
			case Types.SQLXML:
			case Types.CLOB:return rs.getCharacterStream(columnLabel);
			case Types.NCLOB:return rs.getNCharacterStream(columnLabel);
			case Types.BOOLEAN:return rs.getBoolean(columnLabel);
			case Types.BIGINT:return rs.getBigDecimal(columnLabel);
			case Types.DATE:return rs.getDate(columnLabel);
			case Types.TIME:
			case Types.TIMESTAMP:
			case Types.TINYINT:
			case Types.SMALLINT:
			case Types.INTEGER:return rs.getInt(columnLabel);
			case Types.REAL:
			case Types.NUMERIC:
			case Types.DECIMAL:
			case Types.FLOAT:
			case Types.DOUBLE:return rs.getDouble(columnLabel);
			case Types.OTHER:return rs.getObject(columnLabel);
			default:return rs.getObject(columnLabel);
		}
	}
	
	
	protected final static <T> T castObject(Object obj,Class<? extends T> clazz,Castors<?,T> castors){
		return castObject(obj, clazz, false,true,castors);
	}

	/**
	 * cast obj to clazz type's data
	 * @param obj		waiting for cast
	 * @param clazz	target type
	 * @param ignoreCase   ignore property's name case
	 *  								true		ignore
	 * 									false		not ignore
	 * @param reflectByMethodElseField  
	 * @param castors	self cast define
	 * 								it only effect one level
	 * @return
	 */

	@SuppressWarnings({ "rawtypes", "unchecked"})
	private final static <S,T> T castObject(Object obj,Class<? extends T> clazz,boolean ignoreCase,boolean reflectByMethodElseField,Castors<S,T> castors){
		if(obj == null)
			return null;
		if(clazz == null)
			return (T)obj;
		if(clazz.isAssignableFrom(obj.getClass()))
			return (T)obj;
		if(obj instanceof Map && Map.class.isAssignableFrom(clazz))
			return map2map(obj,clazz,castors);
		if(Map.class.isAssignableFrom(clazz))
			return (T)object2Map(obj, clazz, ignoreCase,reflectByMethodElseField,castors);
		if(obj instanceof Map)
			return (T)map2Object((Map)obj, clazz, ignoreCase,reflectByMethodElseField,castors);
		return object2object(obj, clazz, ignoreCase,reflectByMethodElseField,castors);
	}

	@SuppressWarnings("unchecked")
	protected final static <T> Class<T> toClass(Object obj){
		if(obj == null)
			return null;
		if(obj == Class.class)
			return (Class<T>)obj;
		String className = toString(obj);
		try {
			return (Class<T>)Class.forName(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
	 
    private final static int getDimension(String className){
        if(className.indexOf('[')==-1)
            return 0;
        return className.lastIndexOf('[')+1;
    }
   
	@SuppressWarnings("unchecked")
	private final static <S,T> T object2object(Object obj,Class<? extends T> clazz,boolean ignoreCase,boolean reflectByMethodElseField,Castors<S,T> castors){
		GetSet srcGetset = null;
		T instance = null;
		GetSetBean src = GetSetUtil.FM_CACHE.get(obj.getClass());
		GetSetBean dst = GetSetUtil.FM_CACHE.get(clazz);
		Map<String,GetSet> srcGetsets = reflectByMethodElseField?
						ignoreCase?src.getLowerKeyGetMethods():src.getGetMethods():
						ignoreCase?src.getLowerKeyFields():src.getGetSetFields();
		Map<String,GetSet> dstGetsets = reflectByMethodElseField?
						ignoreCase?dst.getLowerKeySetMethods():dst.getSetMethods():
						ignoreCase?dst.getLowerKeyFields():dst.getGetSetFields();
		try {
			instance = (T)Cast.newInstance(clazz);
			for(String fieldName:dstGetsets.keySet()){
				if(castors!=null&&castors.match(fieldName)){
					castors.customCast(fieldName, (S)obj, instance);
				}else{
					srcGetset = srcGetsets.get(fieldName); 
					if(srcGetset != null)
						dstGetsets.get(fieldName).setAndCast(instance,srcGetset.get(obj));	
				}
			}
			return instance;
		} catch (Throwable e) {
			throw new CastException(obj.getClass().getName()+ " to "+clazz.getName()+" message : "+e.getMessage()+" ["+e.getClass().getName()+"]");
		} 
	}
	
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private final static <S,T> T object2Map(Object obj,Class<? extends T> clazz,boolean ignoreCase,boolean useMethodElseField,Castors<S,T> castors){
		Object value = null;
		Map instance = null; 
		GetSetBean src = GetSetUtil.FM_CACHE.get(obj.getClass());
		Map<String,GetSet> getsets = useMethodElseField?
						ignoreCase?src.getLowerKeyGetMethods():src.getGetMethods():
						ignoreCase?src.getLowerKeyFields():src.getGetSetFields();
		try {
			instance = (Map)Cast.newInstance(clazz);
			for(String fieldName:getsets.keySet()){
				if(castors!=null&&castors.match(fieldName)){
					castors.customCast(fieldName, (S)obj, (T)instance);
				}else{
					value = getsets.get(fieldName).get(obj);
					if(value!=null)
						instance.put(fieldName,value);
				}
			}
			return (T)instance;	
		} catch (Throwable e) {
			throw new CastException(obj.getClass().getName()+" to "+clazz.getName()+" message : "+e.getMessage()+" ["+e.getClass().getName()+"]");
		}
	}
	  
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private final static <S,T> T map2Object(Map data,Class<? extends T> clazz,boolean ignoreCase,boolean useMethodOrField,Castors<S,T> castors){
		Object instance = null;
		GetSetBean dst = GetSetUtil.FM_CACHE.get(clazz);
		Map<String,GetSet> getsets = useMethodOrField?
					ignoreCase?dst.getLowerKeySetMethods():dst.getSetMethods():
					ignoreCase?dst.getLowerKeyFields():dst.getGetSetFields();
		data = ignoreCase?lowerKeys(data):data;
		try {
			instance = Cast.newInstance(clazz);
			for(String fieldName:getsets.keySet())
				if(castors!=null&&castors.match(fieldName)){
					castors.customCast(fieldName, (S)data, (T)instance);
				}else{
					if(data.containsKey(fieldName))
						getsets.get(fieldName).setAndCast(instance,data.get(fieldName));
				}
			return (T)instance;
		} catch (Throwable e) {
			throw new CastException(data.getClass().getName()+ " to "+clazz.getName()+" message : "+e.getMessage()+" ["+e.getClass().getName()+"]");
		} 
	} 
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private final static <S,T> T map2map(Object obj,Class<? extends T> clazz,Castors<S,T> castors){
		Map src = (Map)obj;
		Map instance = null;  
		try {
			instance = (Map)Cast.newInstance(clazz);
			if(castors == null){
				instance.putAll(src);
				return (T)instance;
			}else{
				Iterator<Entry> iterator = src.entrySet().iterator();
				Entry entry = null;
				Object key = null;
				Object value = null;
				while(iterator.hasNext()){
					entry = iterator.next();
					key = entry.getKey();
					if(key instanceof String && castors.match((String)key)){
						castors.customCast((String)key, (S)obj, (T)instance);
					}else{
						value = entry.getValue();
						if(value!=null)
							instance.put(key,value);
					}
				}
			}
			return (T)instance;	
		} catch (Throwable e) {
			throw new CastException(obj.getClass().getName()+" to "+clazz.getName()+" message : "+e.getMessage()+" ["+e.getClass().getName()+"]");
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private final static Map lowerKeys(Map map){
		if(map==null||map.size()==0)
			return map;
		Map rs = new HashMap(map.size());
		Entry entry = null;
		Object key = null; 
		Iterator<Entry> iterator  = map.entrySet().iterator();
		while(iterator.hasNext()){
			entry = iterator.next();
			if(entry != null){
				key = entry.getKey();
				if(key instanceof String)
					rs.put(((String)key).toLowerCase(),entry.getValue());
				else
					rs.put(key, entry.getValue());
			}
		}
		return rs;
	} 
	
	private final static String reader2string(Reader reader){
		CharArrayWriter caw = new CharArrayWriter();
		char[] buf = new char[1024];
		int len = 0;
		try {
			while((len=reader.read(buf))!=-1)
				caw.write(buf, 0, len);
			caw.close();
			return caw.toString();
		} catch (IOException e) {
			throw new CastException(reader.getClass().getName()+" to String error,message "+e.getMessage());
		}  
	}
	
	private final static byte[] inputstream2bytes(InputStream is){
		ByteArrayOutputStream bais = new ByteArrayOutputStream();
		byte[] buf = new byte[1024*10];
		int len = 0;
		try {
			while((len=is.read(buf))!=-1)
				bais.write(buf, 0, len);
			bais.close();
			return bais.toByteArray();
		} catch (IOException e) {
			throw new CastException(is.getClass().getName()+" to byte[] error,message "+e.getMessage());
		}  
	}
	
	protected final static boolean string2boolean(String str){
		if(str.length()==1)
			return str.charAt(0) != '0';
		else
			return "true".equalsIgnoreCase(str);
	}
	
	protected final static Character string2character(String str){
		if(str.length()>0)
			return str.charAt(0);
		else
			throw new CastException("String:\"\" can't cast to java.lang.Character !");
	}
	
	private final static Date string2date(String str){
		try {
			//yyyy-MM-dd HH:mm:ss
			if(str.length()==19){
				return ((SimpleDateFormat)datetimeFormat.clone()).parse(str);
			//yyyy-MM-dd
			}else if(str.length()==10){
				return ((SimpleDateFormat)dateFormat.clone()).parse(str);
			//HH:mm:ss
			}else if(str.length()==8){
				return ((SimpleDateFormat)timeFormat.clone()).parse(str);
			}
			return SimpleDateFormat.getDateInstance().parse(str);
		} catch (ParseException e) {
			throw new CastException(str+" get it's java.util.Date value error,message "+e.getMessage());
		}
	}
	
	private final static java.sql.Date string2sqldate(String str){
		return new java.sql.Date(string2date(str).getTime());
	}
	
	private final static java.sql.Time string2sqltime(String str){
		return new java.sql.Time(string2date(str).getTime());
	}
	
	private final static java.sql.Timestamp string2sqltimestamp(String str){
		return new java.sql.Timestamp(string2date(str).getTime());
	}
	
	private final static String date2string(Date date){
		long t = date.getTime();
		//only date
		if(t%100000 == 0){
			return ((SimpleDateFormat)dateFormat.clone()).format(date);
		//only time
		}else if(t<0){
			return ((SimpleDateFormat)timeFormat.clone()).format(date);
		//datetime
		}else{
			return ((SimpleDateFormat)datetimeFormat.clone()).format(date);
		} 
	}
	 
	private final static String sqltime2string(java.sql.Time time){
		return ((SimpleDateFormat)timeFormat.clone()).format(time);
	}
	
	private final static String sqltimestamp2string(java.sql.Timestamp timestamp){
		return ((SimpleDateFormat)datetimeFormat.clone()).format(timestamp);
	}
	
	/**
	 * java.util.ArrayList<com.lova.Dao<com.lova.User>>
	 *  ->
	 * com.lova.Dao
	 * @param field
	 * @return
	 */
	protected static Class<?> getComponentTypeByField(Field field){
		if(field == null)
			return null;
		return toClass(getGenericTypeName(field.getGenericType().toString()));
	}
	
	protected static Class<?> getComponentTypeByMethod(Method method){
		if(method.getParameterTypes() == null ||method.getParameterTypes().length==0)
			return null;
		return toClass(getGenericTypeName(method.getGenericParameterTypes()[0].toString()));
	}
	
	private final static String getGenericTypeName(String genericTypeString){
		if(genericTypeString.indexOf('<')==-1||genericTypeString.indexOf('>')==-1)
			return null;
		String genericClassName = genericTypeString.substring(genericTypeString.indexOf('<')+1,genericTypeString.lastIndexOf('>'));
		if(genericClassName.indexOf('<')!=-1){
			genericClassName = genericClassName.substring(0,genericClassName.indexOf('<'));
		} 
		return genericClassName;
	}
	
	private final static SimpleDateFormat dateFormat =  new SimpleDateFormat("yyyy-MM-dd");
	private final static SimpleDateFormat timeFormat =  new SimpleDateFormat("HH:mm:ss");
	private final static SimpleDateFormat datetimeFormat =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private final static <T> T newInstance(Class<? extends T> clazz){
		if(clazz == null)
			throw new RuntimeException("ClassNotFoundException:null");
		try { 
				if(clazz == Map.class  || clazz == HashMap.class){
					return (T)new HashMap();
				}else if(clazz == Collection.class || clazz == List.class || clazz == ArrayList.class){
					return (T) new ArrayList();
				}else if(clazz == TreeMap.class){
					return (T)new TreeMap();
				}else if(clazz == ConcurrentHashMap.class){
					return (T)new ConcurrentHashMap();
				}else if(clazz == Hashtable.class){
					return (T)new Hashtable();
				}else if(clazz == LinkedHashMap.class){
					return (T)new LinkedHashMap();
				}else if(clazz == WeakHashMap.class){
					return (T)new WeakHashMap();
				}else if(clazz == LinkedList.class){
					return (T) new LinkedList();
				}else if(clazz == Vector.class){
					return (T) new Vector();
				}else if(clazz == TreeSet.class){
					return (T) new TreeSet();
				}else if(clazz == HashSet.class){
					return (T) new HashSet();
				}else if(clazz == LinkedHashSet.class){
					return (T) new LinkedHashSet();
				}else if(clazz == SynchronousQueue.class){
					return (T) new SynchronousQueue();
				}else if(clazz == ArrayDeque.class){
					return (T) new ArrayDeque();
				}else if(clazz == ConcurrentLinkedQueue.class){
					return (T) new ConcurrentLinkedQueue();
				}else if(clazz == Stack.class){
					return (T) new Stack();
				}else{
					return clazz.getConstructor().newInstance();
				}
		} catch (Throwable e) {
			throw new RuntimeException("newInstance by "+(clazz!=null?clazz.getName():null)+" "+e.getClass().getSimpleName()+":"+e.getMessage());
		}
	}
}

class BooleanArrayAdaptor {
	
	public final static Boolean[] toBooleanArray4Object(boolean[] arr){
		Boolean[] rs = new Boolean[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i];
		return rs;
	}
	
	public final static byte[] toByteArray(boolean[] arr){
		byte[] rs = new byte[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i]?(byte)1:(byte)0;
		return rs;
	}
	
	public final static Byte[] toByteArray4Object(boolean[] arr){
		Byte[] rs = new Byte[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i]?(byte)1:(byte)0;
		return rs;
	} 
	 
	public final static char[] toCharArray(boolean[] arr){
		char[] rs = new char[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i]?'1':'0';
		return rs;
	}
	
	public final static Character[] toCharArray4Object(boolean[] arr){
		Character[] rs = new Character[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i]?'1':'0';
		return rs;
	}
	
	public final static short[] toShortArray(boolean[] arr){
		short[] rs = new short[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i]?(short)1:(short)0;
		return rs;
	}
	
	public final static Short[] toShortArray4Object(boolean[] arr){
		Short[] rs = new Short[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i]?(short)1:(short)0;
		return rs;
	}
	
	public final static int[] toIntArray(boolean[] arr){
		int[] rs = new int[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i]?(int)1:(int)0;
		return rs;
	}
	
	public final static Integer[] toIntArray4Object(boolean[] arr){
		Integer[] rs = new Integer[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i]?(int)1:(int)0;
		return rs;
	}
	
	public final static long[] toLongArray(boolean[] arr){
		long[] rs = new long[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i]?(long)1:(long)0;
		return rs;
	}
	
	public final static Long[] toLongArray4Object(boolean[] arr){
		Long[] rs = new Long[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i]?(long)1:(long)0;
		return rs;
	}
	
	public final static float[] toFloatArray(boolean[] arr){
		float[] rs = new float[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i]?(float)1:(float)0;
		return rs;
	}
	
	public final static Float[] toFloatArray4Object(boolean[] arr){
		Float[] rs = new Float[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i]?(float)1:(float)0;
		return rs;
	}
	
	public final static double[] toDoubleArray(boolean[] arr){
		double[] rs = new double[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i]?(double)1:(double)0;
		return rs;
	}
	
	public final static Double[] toDoubleArray4Object(boolean[] arr){
		Double[] rs = new Double[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i]?(double)1:(double)0;
		return rs;
	}
	
	public final static BigInteger[] toBigIntegerArray(boolean[] arr){
		BigInteger[] rs = new BigInteger[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = BigInteger.valueOf(arr[i]?(long)1:(long)0);
		return rs;
	}
	
	public final static BigDecimal[] toBigDecimalArray(boolean[] arr){
		BigDecimal[] rs = new BigDecimal[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = BigDecimal.valueOf(arr[i]?(double)1:(double)0);
		return rs;
	}
	
	public final static String[] toStringArray(boolean[] arr){
		String[] rs = new String[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = String.valueOf(arr[i]);
		return rs;
	}
	
	public final static boolean[] toBooleanArray(Boolean[] arr){
		boolean[] rs = new boolean[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i];
		return rs;
	}
	 
	public final static byte[] toByteArray(Boolean[] arr){
		byte[] rs = new byte[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i]?(byte)1:(byte)0;
		return rs;
	}
	
	public final static Byte[] toByteArray4Object(Boolean[] arr){
		Byte[] rs = new Byte[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i]?(byte)1:(byte)0;
		return rs;
	} 
	 
	public final static char[] toCharArray(Boolean[] arr){
		char[] rs = new char[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i]?'1':'0';
		return rs;
	}
	
	public final static Character[] toCharArray4Object(Boolean[] arr){
		Character[] rs = new Character[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i]?'1':'0';
		return rs;
	}
	
	public final static short[] toShortArray(Boolean[] arr){
		short[] rs = new short[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i]?(short)1:(short)0;
		return rs;
	}
	
	public final static Short[] toShortArray4Object(Boolean[] arr){
		Short[] rs = new Short[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i]?(short)1:(short)0;
		return rs;
	}
	
	public final static int[] toIntArray(Boolean[] arr){
		int[] rs = new int[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i]?(int)1:(int)0;
		return rs;
	}
	
	public final static Integer[] toIntArray4Object(Boolean[] arr){
		Integer[] rs = new Integer[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i]?(int)1:(int)0;
		return rs;
	}
	
	public final static long[] toLongArray(Boolean[] arr){
		long[] rs = new long[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i]?(long)1:(long)0;
		return rs;
	}
	
	public final static Long[] toLongArray4Object(Boolean[] arr){
		Long[] rs = new Long[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i]?(long)1:(long)0;
		return rs;
	}
	
	public final static float[] toFloatArray(Boolean[] arr){
		float[] rs = new float[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i]?(float)1:(float)0;
		return rs;
	}
	
	public final static Float[] toFloatArray4Object(Boolean[] arr){
		Float[] rs = new Float[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i]?(float)1:(float)0;
		return rs;
	}
	
	public final static double[] toDoubleArray(Boolean[] arr){
		double[] rs = new double[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i]?(double)1:(double)0;
		return rs;
	}
	
	public final static Double[] toDoubleArray4Object(Boolean[] arr){
		Double[] rs = new Double[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i]?(double)1:(double)0;
		return rs;
	}
	
	public final static BigInteger[] toBigIntegerArray(Boolean[] arr){
		BigInteger[] rs = new BigInteger[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = BigInteger.valueOf(arr[i]?(long)1:(long)0);
		return rs;
	}
	
	public final static BigDecimal[] toBigDecimalArray(Boolean[] arr){
		BigDecimal[] rs = new BigDecimal[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = BigDecimal.valueOf(arr[i]?(double)1:(double)0);
		return rs;
	}
	
	public final static String[] toStringArray(Boolean[] arr){
		String[] rs = new String[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = String.valueOf(arr[i]);
		return rs;
	}
	
}


class ByteArrayAdaptor {
	 
	public final static Byte[] toByteArray4Object(byte[] arr){
		Byte[] rs = new Byte[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i];
		return rs;
	}
	
	public final static boolean[] toBooleanArray(byte[] arr){
		boolean[] rs = new boolean[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = 0!=arr[i];
		return rs;
	}
	
	public final static Boolean[] toBooleanArray4Object(byte[] arr){
		Boolean[] rs = new Boolean[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = 0!=arr[i];
		return rs;
	} 
	 
	public final static char[] toCharArray(byte[] arr){
		char[] rs = new char[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (char)arr[i];
		return rs;
	}
	
	public final static Character[] toCharArray4Object(byte[] arr){
		Character[] rs = new Character[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (char)arr[i];
		return rs;
	}
	
	public final static short[] toShortArray(byte[] arr){
		short[] rs = new short[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (short)arr[i];
		return rs;
	}
	
	public final static Short[] toShortArray4Object(byte[] arr){
		Short[] rs = new Short[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (short)arr[i];
		return rs;
	}
	
	public final static int[] toIntArray(byte[] arr){
		int[] rs = new int[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (int)arr[i];
		return rs;
	}
	
	public final static Integer[] toIntArray4Object(byte[] arr){
		Integer[] rs = new Integer[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (int)arr[i];
		return rs;
	}
	
	public final static long[] toLongArray(byte[] arr){
		long[] rs = new long[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (long)arr[i];
		return rs;
	}
	
	public final static Long[] toLongArray4Object(byte[] arr){
		Long[] rs = new Long[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (long)arr[i];
		return rs;
	}
	
	public final static float[] toFloatArray(byte[] arr){
		float[] rs = new float[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (float)arr[i];
		return rs;
	}
	
	public final static Float[] toFloatArray4Object(byte[] arr){
		Float[] rs = new Float[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (float)arr[i];
		return rs;
	}
	
	public final static double[] toDoubleArray(byte[] arr){
		double[] rs = new double[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (double)arr[i];
		return rs;
	}
	
	public final static Double[] toDoubleArray4Object(byte[] arr){
		Double[] rs = new Double[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (double)arr[i];
		return rs;
	}
	
	public final static BigInteger[] toBigIntegerArray(byte[] arr){
		BigInteger[] rs = new BigInteger[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = BigInteger.valueOf((long)arr[i]);
		return rs;
	}
	
	public final static BigDecimal[] toBigDecimalArray(byte[] arr){
		BigDecimal[] rs = new BigDecimal[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = BigDecimal.valueOf((long)arr[i]);
		return rs;
	}
	
	public final static String[] toStringArray(byte[] arr){
		String[] rs = new String[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = String.valueOf(arr[i]);
		return rs;
	}
	
}

class CharArrayAdaptor {

	public final static Character[] toCharArray4Object(char[] arr){
		Character[] rs = new Character[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] =  arr[i];
		return rs;
	}
	
	public final static boolean[] toBooleanArray(char[] arr){
		boolean[] rs = new boolean[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = '0'!=arr[i];
		return rs;
	}
	
	public final static Boolean[] toBooleanArray4Object(char[] arr){
		Boolean[] rs = new Boolean[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = '0'!=arr[i];
		return rs;
	} 
	 
	public final static byte[] toByteArray(char[] arr){
		byte[] rs = new byte[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (byte)arr[i];
		return rs;
	}
	
	public final static Byte[] toByteArray4Object(char[] arr){
		Byte[] rs = new Byte[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (byte)arr[i];
		return rs;
	}
	
	public final static short[] toShortArray(char[] arr){
		short[] rs = new short[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (short)arr[i];
		return rs;
	}
	
	public final static Short[] toShortArray4Object(char[] arr){
		Short[] rs = new Short[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (short)arr[i];
		return rs;
	}
	
	public final static int[] toIntArray(char[] arr){
		int[] rs = new int[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (int)arr[i];
		return rs;
	}
	
	public final static Integer[] toIntArray4Object(char[] arr){
		Integer[] rs = new Integer[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (int)arr[i];
		return rs;
	}
	
	public final static long[] toLongArray(char[] arr){
		long[] rs = new long[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (long)arr[i];
		return rs;
	}
	
	public final static Long[] toLongArray4Object(char[] arr){
		Long[] rs = new Long[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (long)arr[i];
		return rs;
	}
	
	public final static float[] toFloatArray(char[] arr){
		float[] rs = new float[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (float)arr[i];
		return rs;
	}
	
	public final static Float[] toFloatArray4Object(char[] arr){
		Float[] rs = new Float[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (float)arr[i];
		return rs;
	}
	
	public final static double[] toDoubleArray(char[] arr){
		double[] rs = new double[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (double)arr[i];
		return rs;
	}
	
	public final static Double[] toDoubleArray4Object(char[] arr){
		Double[] rs = new Double[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (double)arr[i];
		return rs;
	}
	
	public final static BigInteger[] toBigIntegerArray(char[] arr){
		BigInteger[] rs = new BigInteger[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = BigInteger.valueOf((long)arr[i]);
		return rs;
	}
	
	public final static BigDecimal[] toBigDecimalArray(char[] arr){
		BigDecimal[] rs = new BigDecimal[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = BigDecimal.valueOf((long)arr[i]);
		return rs;
	}
	
	public final static String[] toStringArray(char[] arr){
		String[] rs = new String[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = String.valueOf(arr[i]);
		return rs;
	}
	
	public final static char[] toCharArray(Character[] arr){
		char[] rs = new char[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] =  arr[i];
		return rs;
	}
  
	public final static boolean[] toBooleanArray(Character[] arr){
		boolean[] rs = new boolean[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = '0'!=arr[i];
		return rs;
	}
	
	public final static Boolean[] toBooleanArray4Object(Character[] arr){
		Boolean[] rs = new Boolean[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = '0'!=arr[i];
		return rs;
	} 
	 
	public final static byte[] toByteArray(Character[] arr){
		byte[] rs = new byte[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (byte)arr[i].charValue();
		return rs;
	}
	
	public final static Byte[] toByteArray4Object(Character[] arr){
		Byte[] rs = new Byte[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (byte)arr[i].charValue();
		return rs;
	}
	
	public final static short[] toShortArray(Character[] arr){
		short[] rs = new short[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (short)arr[i].charValue();
		return rs;
	}
	
	public final static Short[] toShortArray4Object(Character[] arr){
		Short[] rs = new Short[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (short)arr[i].charValue();
		return rs;
	}
	
	public final static int[] toIntArray(Character[] arr){
		int[] rs = new int[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (int)arr[i].charValue();
		return rs;
	}
	
	public final static Integer[] toIntArray4Object(Character[] arr){
		Integer[] rs = new Integer[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (int)arr[i].charValue();
		return rs;
	}
	
	public final static long[] toLongArray(Character[] arr){
		long[] rs = new long[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (long)arr[i].charValue();
		return rs;
	}
	
	public final static Long[] toLongArray4Object(Character[] arr){
		Long[] rs = new Long[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (long)arr[i].charValue();
		return rs;
	}
	
	public final static float[] toFloatArray(Character[] arr){
		float[] rs = new float[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (float)arr[i].charValue();
		return rs;
	}
	
	public final static Float[] toFloatArray4Object(Character[] arr){
		Float[] rs = new Float[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (float)arr[i].charValue();
		return rs;
	}
	
	public final static double[] toDoubleArray(Character[] arr){
		double[] rs = new double[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (double)arr[i].charValue();
		return rs;
	}
	
	public final static Double[] toDoubleArray4Object(Character[] arr){
		Double[] rs = new Double[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (double)arr[i].charValue();
		return rs;
	}
	
	public final static BigInteger[] toBigIntegerArray(Character[] arr){
		BigInteger[] rs = new BigInteger[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = BigInteger.valueOf((long)arr[i].charValue());
		return rs;
	}
	
	public final static BigDecimal[] toBigDecimalArray(Character[] arr){
		BigDecimal[] rs = new BigDecimal[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = BigDecimal.valueOf((long)arr[i].charValue());
		return rs;
	}
	
	public final static String[] toStringArray(Character[] arr){
		String[] rs = new String[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = String.valueOf(arr[i]);
		return rs;
	}
	
}

final class ShortArrayAdaptor {

	public final static Short[] toShortArray4Object(short[] arr){
		Short[] rs = new Short[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i];
		return rs;
	}
	
	public final static boolean[] toBooleanArray(short[] arr){
		boolean[] rs = new boolean[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = 0!=arr[i];
		return rs;
	}
	
	public final static Boolean[] toBooleanArray4Object(short[] arr){
		Boolean[] rs = new Boolean[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = 0!=arr[i];
		return rs;
	} 
	
	public final static byte[] toByteArray(short[] arr){
		byte[] rs = new byte[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (byte)arr[i];
		return rs;
	}
	
	public final static Byte[] toByteArray4Object(short[] arr){
		Byte[] rs = new Byte[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (byte)arr[i];
		return rs;
	}
	
	public final static char[] toCharArray(short[] arr){
		char[] rs = new char[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (char)arr[i];
		return rs;
	}
	
	public final static Character[] toCharArray4Object(short[] arr){
		Character[] rs = new Character[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (char)arr[i];
		return rs;
	}
	
	public final static int[] toIntArray(short[] arr){
		int[] rs = new int[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (int)arr[i];
		return rs;
	}
	
	public final static Integer[] toIntegerArray4Object(short[] arr){
		Integer[] rs = new Integer[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (int)arr[i];
		return rs;
	}
	
	public final static long[] toLongArray(short[] arr){
		long[] rs = new long[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (long)arr[i];
		return rs;
	}
	
	public final static Long[] toLongArray4Object(short[] arr){
		Long[] rs = new Long[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (long)arr[i];
		return rs;
	}
	
	public final static float[] toFloatArray(short[] arr){
		float[] rs = new float[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (float)arr[i];
		return rs;
	}
	
	public final static Float[] toFloatArray4Object(short[] arr){
		Float[] rs = new Float[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (float)arr[i];
		return rs;
	}
	
	public final static double[] toDoubleArray(short[] arr){
		double[] rs = new double[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (double)arr[i];
		return rs;
	}
	
	public final static Double[] toDoubleArray4Object(short[] arr){
		Double[] rs = new Double[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (double)arr[i];
		return rs;
	}
	
	public final static BigInteger[] toBigIntegerArray(short[] arr){
		BigInteger[] rs = new BigInteger[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = BigInteger.valueOf((long)arr[i]);
		return rs;
	}
	
	public final static BigDecimal[] toBigDecimalArray(short[] arr){
		BigDecimal[] rs = new BigDecimal[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = BigDecimal.valueOf((long)arr[i]);
		return rs;
	}
	
	public final static String[] toStringArray(short[] arr){
		String[] rs = new String[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = String.valueOf(arr[i]);
		return rs;
	}
	
}

final class IntArrayAdaptor {

	public final static Integer[] toIntArray4Object(int[] arr){
		Integer[] rs = new Integer[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i];
		return rs;
	}
	
	public final static boolean[] toBooleanArray(int[] arr){
		boolean[] rs = new boolean[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = 0!=arr[i];
		return rs;
	}
	
	public final static Boolean[] toBooleanArray4Object(int[] arr){
		Boolean[] rs = new Boolean[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = 0!=arr[i];
		return rs;
	} 
	
	public final static byte[] toByteArray(int[] arr){
		byte[] rs = new byte[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (byte)arr[i];
		return rs;
	}
	
	public final static Byte[] toByteArray4Object(int[] arr){
		Byte[] rs = new Byte[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (byte)arr[i];
		return rs;
	}
	
	public final static char[] toCharArray(int[] arr){
		char[] rs = new char[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (char)arr[i];
		return rs;
	}
	
	public final static Character[] toCharArray4Object(int[] arr){
		Character[] rs = new Character[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (char)arr[i];
		return rs;
	}
	
	public final static short[] toShortArray(int[] arr){
		short[] rs = new short[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (short)arr[i];
		return rs;
	}
	
	public final static Short[] toShortArray4Object(int[] arr){
		Short[] rs = new Short[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (short)arr[i];
		return rs;
	}
	
	public final static long[] toLongArray(int[] arr){
		long[] rs = new long[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (long)arr[i];
		return rs;
	}
	
	public final static Long[] toLongArray4Object(int[] arr){
		Long[] rs = new Long[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (long)arr[i];
		return rs;
	}
	
	public final static float[] toFloatArray(int[] arr){
		float[] rs = new float[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (float)arr[i];
		return rs;
	}
	
	public final static Float[] toFloatArray4Object(int[] arr){
		Float[] rs = new Float[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (float)arr[i];
		return rs;
	}
	
	public final static double[] toDoubleArray(int[] arr){
		double[] rs = new double[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (double)arr[i];
		return rs;
	}
	
	public final static Double[] toDoubleArray4Object(int[] arr){
		Double[] rs = new Double[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (double)arr[i];
		return rs;
	}
	
	public final static BigInteger[] toBigIntArray(int[] arr){
		BigInteger[] rs = new BigInteger[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = BigInteger.valueOf((long)arr[i]);
		return rs;
	}
	
	public final static BigDecimal[] toBigDecimalArray(int[] arr){
		BigDecimal[] rs = new BigDecimal[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = BigDecimal.valueOf((long)arr[i]);
		return rs;
	}
	
	public final static String[] toStringArray(int[] arr){
		String[] rs = new String[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = String.valueOf(arr[i]);
		return rs;
	}
	
}

final class LongArrayAdaptor {

	public final static Long[] toLongArray4Object(long[] arr){
		Long[] rs = new Long[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i];
		return rs;
	}
	
	public final static boolean[] toBooleanArray(long[] arr){
		boolean[] rs = new boolean[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = 0!=arr[i];
		return rs;
	}
	
	public final static Boolean[] toBooleanArray4Object(long[] arr){
		Boolean[] rs = new Boolean[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = 0!=arr[i];
		return rs;
	} 
	
	public final static byte[] toByteArray(long[] arr){
		byte[] rs = new byte[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (byte)arr[i];
		return rs;
	}
	
	public final static Byte[] toByteArray4Object(long[] arr){
		Byte[] rs = new Byte[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (byte)arr[i];
		return rs;
	}
	
	public final static char[] toCharArray(long[] arr){
		char[] rs = new char[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (char)arr[i];
		return rs;
	}
	
	public final static Character[] toCharArray4Object(long[] arr){
		Character[] rs = new Character[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (char)arr[i];
		return rs;
	}
	
	public final static short[] toShortArray(long[] arr){
		short[] rs = new short[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (short)arr[i];
		return rs;
	}
	
	public final static Short[] toShortArray4Object(long[] arr){
		Short[] rs = new Short[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (short)arr[i];
		return rs;
	}
	
	public final static int[] toIntArray(long[] arr){
		int[] rs = new int[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (int)arr[i];
		return rs;
	}
	
	public final static Integer[] toIntArray4Object(long[] arr){
		Integer[] rs = new Integer[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (int)arr[i];
		return rs;
	}
	
	public final static float[] toFloatArray(long[] arr){
		float[] rs = new float[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (float)arr[i];
		return rs;
	}
	
	public final static Float[] toFloatArray4Object(long[] arr){
		Float[] rs = new Float[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (float)arr[i];
		return rs;
	}
	
	public final static double[] toDoubleArray(long[] arr){
		double[] rs = new double[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (double)arr[i];
		return rs;
	}
	
	public final static Double[] toDoubleArray4Object(long[] arr){
		Double[] rs = new Double[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (double)arr[i];
		return rs;
	}
	
	public final static BigInteger[] toBigIntegerArray(long[] arr){
		BigInteger[] rs = new BigInteger[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = BigInteger.valueOf((long)arr[i]);
		return rs;
	}
	
	public final static BigDecimal[] toBigDecimalArray(long[] arr){
		BigDecimal[] rs = new BigDecimal[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = BigDecimal.valueOf((long)arr[i]);
		return rs;
	}
	
	public final static String[] toStringArray(long[] arr){
		String[] rs = new String[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = String.valueOf(arr[i]);
		return rs;
	}
	
}


final class FloatArrayAdaptor {

	public final static Float[] toFloatArray4Object(float[] arr){
		Float[] rs = new Float[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i];
		return rs;
	}
	
	public final static boolean[] toBooleanArray(float[] arr){
		boolean[] rs = new boolean[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = 0!=arr[i];
		return rs;
	}
	
	public final static Boolean[] toBooleanArray4Object(float[] arr){
		Boolean[] rs = new Boolean[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = 0!=arr[i];
		return rs;
	} 
	
	public final static byte[] toByteArray(float[] arr){
		byte[] rs = new byte[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (byte)arr[i];
		return rs;
	}
	
	public final static Byte[] toByteArray4Object(float[] arr){
		Byte[] rs = new Byte[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (byte)arr[i];
		return rs;
	}
	
	public final static char[] toCharArray(float[] arr){
		char[] rs = new char[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (char)arr[i];
		return rs;
	}
	
	public final static Character[] toCharArray4Object(float[] arr){
		Character[] rs = new Character[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (char)arr[i];
		return rs;
	}
	
	public final static short[] toShortArray(float[] arr){
		short[] rs = new short[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (short)arr[i];
		return rs;
	}
	
	public final static Short[] toShortArray4Object(float[] arr){
		Short[] rs = new Short[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (short)arr[i];
		return rs;
	}
	
	public final static int[] toIntArray(float[] arr){
		int[] rs = new int[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (int)arr[i];
		return rs;
	}
	
	public final static Integer[] toIntArray4Object(float[] arr){
		Integer[] rs = new Integer[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (int)arr[i];
		return rs;
	}
	
	public final static long[] toLongArray(float[] arr){
		long[] rs = new long[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (long)arr[i];
		return rs;
	}
	
	public final static Long[] toLongArray4Object(float[] arr){
		Long[] rs = new Long[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (long)arr[i];
		return rs;
	}
	
	public final static double[] toDoubleArray(float[] arr){
		double[] rs = new double[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (double)arr[i];
		return rs;
	}
	
	public final static Double[] toDoubleArray4Object(float[] arr){
		Double[] rs = new Double[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (double)arr[i];
		return rs;
	}
	
	public final static BigInteger[] toBigIntegerArray(float[] arr){
		BigInteger[] rs = new BigInteger[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = BigInteger.valueOf((long)arr[i]);
		return rs;
	}
	
	public final static BigDecimal[] toBigDecimalArray(float[] arr){
		BigDecimal[] rs = new BigDecimal[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = BigDecimal.valueOf((long)arr[i]);
		return rs;
	}
	
	public final static String[] toStringArray(float[] arr){
		String[] rs = new String[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = String.valueOf(arr[i]);
		return rs;
	}
	
}

final class DoubleArrayAdaptor {

	public final static Double[] toDoubleArray4Object(double[] arr){
		Double[] rs = new Double[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i];
		return rs;
	}
	
	public final static boolean[] toBooleanArray(double[] arr){
		boolean[] rs = new boolean[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = 0!=arr[i];
		return rs;
	}
	
	public final static Boolean[] toBooleanArray4Object(double[] arr){
		Boolean[] rs = new Boolean[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = 0!=arr[i];
		return rs;
	} 
	
	public final static byte[] toByteArray(double[] arr){
		byte[] rs = new byte[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (byte)arr[i];
		return rs;
	}
	
	public final static Byte[] toByteArray4Object(double[] arr){
		Byte[] rs = new Byte[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (byte)arr[i];
		return rs;
	}
	
	public final static char[] toCharArray(double[] arr){
		char[] rs = new char[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (char)arr[i];
		return rs;
	}
	
	public final static Character[] toCharArray4Object(double[] arr){
		Character[] rs = new Character[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (char)arr[i];
		return rs;
	}
	
	public final static short[] toShortArray(double[] arr){
		short[] rs = new short[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (short)arr[i];
		return rs;
	}
	
	public final static Short[] toShortArray4Object(double[] arr){
		Short[] rs = new Short[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (short)arr[i];
		return rs;
	}
	
	public final static int[] toIntArray(double[] arr){
		int[] rs = new int[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (int)arr[i];
		return rs;
	}
	
	public final static Integer[] toIntArray4Object(double[] arr){
		Integer[] rs = new Integer[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (int)arr[i];
		return rs;
	}
	
	public final static long[] toLongArray(double[] arr){
		long[] rs = new long[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (long)arr[i];
		return rs;
	}
	
	public final static Long[] toLongArray4Object(double[] arr){
		Long[] rs = new Long[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (long)arr[i];
		return rs;
	}
	
	public final static float[] toFloatArray(double[] arr){
		float[] rs = new float[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (float)arr[i];
		return rs;
	}
	
	public final static Float[] toFloatArray4Object(double[] arr){
		Float[] rs = new Float[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (float)arr[i];
		return rs;
	}
	
	public final static BigInteger[] toBigIntegerArray(double[] arr){
		BigInteger[] rs = new BigInteger[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = BigInteger.valueOf((long)arr[i]);
		return rs;
	}
	
	public final static BigDecimal[] toBigDecimalArray(double[] arr){
		BigDecimal[] rs = new BigDecimal[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = BigDecimal.valueOf((long)arr[i]);
		return rs;
	}
	
	public final static String[] toStringArray(double[] arr){
		String[] rs = new String[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = String.valueOf(arr[i]);
		return rs;
	}
	
}


final class NumberArrayAdaptor {

	
	public final static boolean[] toBooleanArray(Number[] arr){
		boolean[] rs = new boolean[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i].intValue() != 0;
		return rs;
	}
	
	public final static Boolean[] toBooleanArray4Object(Number[] arr){
		Boolean[] rs = new Boolean[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i].intValue() != 0;
		return rs;
	} 
	
	public final static byte[] toByteArray(Number[] arr){
		byte[] rs = new byte[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i].byteValue();
		return rs;
	}
	
	public final static Byte[] toByteArray4Object(Number[] arr){
		Byte[] rs = new Byte[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i].byteValue();
		return rs;
	}
	
	public final static char[] toCharArray(Number[] arr){
		char[] rs = new char[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (char)arr[i].intValue();
		return rs;
	}
	
	public final static Character[] toCharArray4Object(Number[] arr){
		Character[] rs = new Character[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = (char)arr[i].intValue();
		return rs;
	}
	
	public final static short[] toShortArray(Number[] arr){
		short[] rs = new short[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i].shortValue();
		return rs;
	}
	
	public final static Short[] toShortArray4Object(Number[] arr){
		Short[] rs = new Short[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i].shortValue();
		return rs;
	}
	
	public final static int[] toIntArray(Number[] arr){
		int[] rs = new int[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i].intValue();
		return rs;
	}
	
	public final static Integer[] toIntArray4Object(Number[] arr){
		Integer[] rs = new Integer[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i].intValue();
		return rs;
	}
	
	public final static long[] toLongArray(Number[] arr){
		long[] rs = new long[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i].longValue();
		return rs;
	}
	
	public final static Long[] toLongArray4Object(Number[] arr){
		Long[] rs = new Long[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i].longValue();
		return rs;
	}
	
	public final static float[] toFloatArray(Number[] arr){
		float[] rs = new float[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i].floatValue();
		return rs;
	}
	
	public final static Float[] toFloatArray4Object(Number[] arr){
		Float[] rs = new Float[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i].floatValue();
		return rs;
	}
	
	public final static double[] toDoubleArray(Number[] arr){
		double[] rs = new double[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i].doubleValue();
		return rs;
	}
	
	public final static Double[] toDoubleArray4Object(Number[] arr){
		Double[] rs = new Double[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i].doubleValue();
		return rs;
	}
	
	public final static BigInteger[] toBigIntegerArray(Number[] arr){
		BigInteger[] rs = new BigInteger[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = BigInteger.valueOf(arr[i].longValue());
		return rs;
	}
	
	public final static BigDecimal[] toBigDecimalArray(Number[] arr){
		BigDecimal[] rs = new BigDecimal[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = BigDecimal.valueOf(arr[i].doubleValue());
		return rs;
	} 
	
	public final static String[] toStringArray(Number[] arr){
		String[] rs = new String[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i].toString();
		return rs;
	} 
}

final class StringArrayAdaptor {

	
	public final static boolean[] toBooleanArray(String[] arr){
		boolean[] rs = new boolean[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = Cast.string2boolean(arr[i]);
		return rs;
	}
	
	public final static Boolean[] toBooleanArray4Object(String[] arr){
		Boolean[] rs = new Boolean[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = arr[i]==null?null:Cast.string2boolean(arr[i]);
		return rs;
	} 
	
	public final static byte[] toByteArray(String[] arr){
		byte[] rs = new byte[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = Byte.valueOf(arr[i]);
		return rs;
	}
	
	public final static Byte[] toByteArray4Object(String[] arr){
		Byte[] rs = new Byte[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = Byte.valueOf(arr[i]);
		return rs;
	}
	
	public final static char[] toCharArray(String[] arr){
		char[] rs = new char[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = Cast.string2character(arr[i]);
		return rs;
	}
	
	public final static Character[] toCharArray4Object(String[] arr){
		Character[] rs = new Character[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = Cast.string2character(arr[i]);
		return rs;
	}
	
	public final static short[] toShortArray(String[] arr){
		short[] rs = new short[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = Short.valueOf(arr[i]);
		return rs;
	}
	
	public final static Short[] toShortArray4Object(String[] arr){
		Short[] rs = new Short[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = Short.valueOf(arr[i]);
		return rs;
	}
	
	public final static int[] toIntArray(String[] arr){
		int[] rs = new int[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = Integer.valueOf(arr[i]);
		return rs;
	}
	
	public final static Integer[] toIntArray4Object(String[] arr){
		Integer[] rs = new Integer[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = Integer.valueOf(arr[i]);
		return rs;
	}
	
	public final static long[] toLongArray(String[] arr){
		long[] rs = new long[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = Long.valueOf(arr[i]);
		return rs;
	}
	
	public final static Long[] toLongArray4Object(String[] arr){
		Long[] rs = new Long[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = Long.valueOf(arr[i]);
		return rs;
	}
	
	public final static float[] toFloatArray(String[] arr){
		float[] rs = new float[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = Float.valueOf(arr[i]);
		return rs;
	}
	
	public final static Float[] toFloatArray4Object(String[] arr){
		Float[] rs = new Float[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = Float.valueOf(arr[i]);
		return rs;
	}
	
	public final static double[] toDoubleArray(String[] arr){
		double[] rs = new double[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = Double.parseDouble(arr[i]);
		return rs;
	}
	
	public final static Double[] toDoubleArray4Object(String[] arr){
		Double[] rs = new Double[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = Double.parseDouble(arr[i]);
		return rs;
	}
	
	public final static BigInteger[] toBigIntegerArray(String[] arr){
		BigInteger[] rs = new BigInteger[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = new BigInteger(arr[i]);
		return rs;
	}
	
	public final static BigDecimal[] toBigDecimalArray(String[] arr){
		BigDecimal[] rs = new BigDecimal[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			rs[i] = new BigDecimal(arr[i]);
		return rs;
	} 
}