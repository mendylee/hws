package com.xrk.hws.dal;

public class DataMoveTest
{
	public static void main(String[] args)
    {
		long a = 143443444343L,b = 20000;
		
	    a = a ^ b;
	    b = a ^ b;
	    a = a ^ b;
	    
	    System.out.println(String.format("a:%d,b:%d",a,b));
    }
	
	/**
	 * 生成新的UUID.  
	 *    
	 * @param ouid	旧用户ID.
	 * @param uuid	新生成的UUID.
	 * @return		
	 */
	public static long uuid(long ouid,long uuid)
	{
		return ouid ^ uuid;
	}
	
	public static long ouid(long uuid,long nuid)
	{
		return uuid ^ nuid;
	}
}
