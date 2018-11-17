package com.xrk.hws.common.utils;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.xrk.hws.common.logger.Logger;

public class UUIDGenerator
{
	private static UUIDGenerator ug = null;
    protected final long dbIdMakerBirthDate = 1349980022551L; // unix时间戳，保证唯一性
    private final long machineIdSize = 10L; // 10 bits
    private final long maxMachineId = -1L ^ (-1L << machineIdSize);
    private final long sequenceSize = 12L; // 12 bits
    private final long maxSequenceValue = -1L ^ (-1L << sequenceSize);
    private final long machineIdLeftShift = sequenceSize;
    private final long timestampLeftShift = sequenceSize + machineIdSize;
    private final long machineId;
    private long lastTime = -1L;
    private long sequence = 0L;
    
    public static UUIDGenerator getInstance()
    {
    	try 
    	{
    		if(ug == null)
    		{
    			ug = new UUIDGenerator();
    		}
        }
        catch (Exception e) 
        {
        	Logger.error("UUIDGenerator getInstance() fail. msg: %s", e);
	        e.printStackTrace();  
        }
    	
    	return ug;
    }

    /**
     * 根据指定的mac机器地址，实始化 LongUUIDMaker.
     * 
     * @throws UnknownHostException
     * @throws SocketException
     */
    public UUIDGenerator() throws UnknownHostException, SocketException {
        final InetAddress ip = InetAddress.getLocalHost();
        final NetworkInterface network = NetworkInterface.getByInetAddress(ip);
        final byte[] mac = network.getHardwareAddress();
        final byte[] macMachineId = new byte[] {0, mac[3], mac[4], mac[5]};
        machineId = new BigInteger(macMachineId).longValue() & maxMachineId;
        assert machineId >= 0 && machineId <= maxMachineId;
    }

    /**
     * 根据机器ID实始化LongUUIDMaker对象.
     * 
     * @param machineId 必须是唯一的集群和最大10位. (<= 1023)
     */
    public UUIDGenerator(final long machineId) {
        if (machineId > maxMachineId || machineId < 0) {
            throw new IllegalArgumentException("Machine id argument must be between 0 and " + maxMachineId);
        }
        this.machineId = machineId;
    }

    /**
     * 
     * makeId:(生成ID). <br/>  
     *  
     * @return
     */
    @SuppressWarnings("unused")
    public synchronized  long makeId() {
        long timestamp = time();
        StringBuffer sb = new StringBuffer();
        if (timestamp < lastTime) {
            throw new IllegalStateException(String.format("Clock is in past. Please, repeat an attempt after %d milliseconds", lastTime - timestamp));
        }

        if (lastTime == timestamp) 
        {
            sequence = (sequence + 1) & maxSequenceValue;
            if (sequence == 0) 
            {
                while (timestamp <= lastTime) 
                {
                    timestamp = time();
                }
            }
        } 
        else 
        {
            sequence = 0;
        }

        lastTime = timestamp;
        long time = (timestamp - dbIdMakerBirthDate << timestampLeftShift);
        long machine = (machineId << machineIdLeftShift);
        long uuid =  time | machine | sequence;
       
        return uuid;
    }

    /**
     * 
     * time:(获取系统当时时间). <br/>  
     *  
     * @return
     */
    protected long time() 
    {
        return System.currentTimeMillis();
    }
    
	public static synchronized long uuid()
	{
		long uid = -1;
		try
		{
			uid = UUIDGenerator.getInstance().makeId();
		}
		catch(Exception e)
		{
			Logger.error("UUIDGenerator.getInstance().makeId() fail. msg: %s", e);
			uid = -1;
		}
		return uid;
	}
}
