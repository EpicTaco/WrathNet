/**
 * The MIT License (MIT)
 * Wrath Net Engine Copyright (c) 2016 Trent Spears
 */
package wrath.net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;

/**
 * Class used to transport and convert raw byte data to other kinds of data.
 * Also works vise-versa.
 * @author Trent Spears
 */
public class Packet
{
    public static final byte[] RUDP_REQ = ((char) 0 + "rudp" + (char) 0).getBytes(Charset.forName("UTF-8"));
    public static final byte[] TERMINATION_CALL = ((char) 0 + "bye" + (char) 0).getBytes(Charset.forName("UTF-8"));
    
    private byte[] data = new byte[0];
    private transient Object dataAsObj = null;
    private transient Object[] dataAsArr = null;
    
    /**
     * Constructor.
     * @param data The raw data the packet will hold.
     * This constructor is the fastest by a significant amount.
     */
    public Packet(byte[] data)
    {
        this.data = data;
    }
    
    /**
     * Constructor.
     * @param object The object data the packet will hold.
     */
    public Packet(Serializable object)
    {
        try
        {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream conv = new ObjectOutputStream(bos);
            conv.writeObject(object);
            conv.close();
            bos.close();
            data = bos.toByteArray();
        }
        catch(IOException e)
        {
            System.err.println("] ERROR: Could not convert object data to bytes, I/O ERROR!");
        }
    }
    
    /**
     * Constructor.
     * @param objects The object array data the packet will hold.
     * Note that this constructor takes the longest amount of time and will nearly triple the time it takes to convert the data.
     * It is strongly recommended that only one Object is stored per Packet.
     */
    public Packet(Serializable[] objects)
    {
        try
        {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream conv = new ObjectOutputStream(bos);
            conv.writeObject(objects);
            conv.close();
            bos.close();
            data = bos.toByteArray();
        }
        catch(IOException e)
        {
            System.err.println("] ERROR: Could not convert object data to bytes, I/O ERROR!");
        }
    }
    
    /**
     * Converts the Packet's byte data to a singular generic Object.
     * @return Returns an {@link java.lang.Object} represented by the Packet's data.
     */
    public Object getDataAsObject()
    {
        if(dataAsObj != null) return dataAsObj;
        if(dataAsArr != null)
        {
            if(dataAsArr.length == 1) dataAsObj = dataAsArr[0];
            else dataAsObj = (Object) dataAsArr;
            return dataAsObj;
        }
        
        try
        {
            ObjectInputStream conv = new ObjectInputStream(new ByteArrayInputStream(data));
            Object r = conv.readObject();
            conv.close();
            this.dataAsObj = r;
            this.dataAsArr = new Object[]{r};
            return r;
        }
        catch(ClassNotFoundException e)
        {
            System.err.println("] ERROR: Could not read packet data as an Object, Unknown or corrupted data!");
            this.dataAsObj = null;
        }
        catch(IOException e)
        {
            System.err.println("] ERROR: Could not read packet data as an Object, I/O ERROR!");
            this.dataAsObj = null;
        }
        
        return null;
    }
    
    /**
     * Converts the Packet's byte data to an array of generic Objects.
     * @return Returns an array of {@link java.lang.Object}s represented by the Packet's data.
     */
    public Object[] getDataAsObjectArray()
    {
        if(dataAsArr != null) return dataAsArr;
        if(dataAsObj != null) return (Object[]) dataAsObj;
            
        try
        {
            ObjectInputStream conv = new ObjectInputStream(new ByteArrayInputStream(data));
            Object r = conv.readObject();
            conv.close();
            Object[] ra;
            if(r instanceof Object[]) ra = (Object[]) r;
            else ra = new Object[]{r};
            this.dataAsArr = ra;
            this.dataAsObj = r;
            return ra;
        }
        catch(ClassNotFoundException e)
        {
            System.err.println("] ERROR: Could not read packet data as an Object, Unknown or corrupted data!");
        }
        catch(IOException e)
        {
            System.err.println("] ERROR: Could not read packet data as an Object, I/O ERROR!");
        }
        
        return null;
    }
    
    /**
     * Gets the raw byte data contained in the packet.
     * @return Returns the raw byte data contained in the packet.
     */
    public byte[] getRawData()
    {
        return data;
    }
}
