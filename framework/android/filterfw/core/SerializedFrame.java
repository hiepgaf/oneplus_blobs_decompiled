package android.filterfw.core;

import android.filterfw.format.ObjectFormat;
import android.graphics.Bitmap;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class SerializedFrame
  extends Frame
{
  private static final int INITIAL_CAPACITY = 64;
  private DirectByteOutputStream mByteOutputStream;
  private ObjectOutputStream mObjectOut;
  
  SerializedFrame(FrameFormat paramFrameFormat, FrameManager paramFrameManager)
  {
    super(paramFrameFormat, paramFrameManager);
    setReusable(false);
    try
    {
      this.mByteOutputStream = new DirectByteOutputStream(64);
      this.mObjectOut = new ObjectOutputStream(this.mByteOutputStream);
      this.mByteOutputStream.markHeaderEnd();
      return;
    }
    catch (IOException paramFrameFormat)
    {
      throw new RuntimeException("Could not create serialization streams for SerializedFrame!", paramFrameFormat);
    }
  }
  
  private final Object deserializeObjectValue()
  {
    try
    {
      Object localObject = new ObjectInputStream(this.mByteOutputStream.getInputStream()).readObject();
      return localObject;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new RuntimeException("Unable to deserialize object of unknown class in " + this + "!", localClassNotFoundException);
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException("Could not deserialize object in " + this + "!", localIOException);
    }
  }
  
  private final void serializeObjectValue(Object paramObject)
  {
    try
    {
      this.mByteOutputStream.reset();
      this.mObjectOut.writeObject(paramObject);
      this.mObjectOut.flush();
      this.mObjectOut.close();
      return;
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException("Could not serialize object " + paramObject + " in " + this + "!", localIOException);
    }
  }
  
  static SerializedFrame wrapObject(Object paramObject, FrameManager paramFrameManager)
  {
    paramFrameManager = new SerializedFrame(ObjectFormat.fromObject(paramObject, 1), paramFrameManager);
    paramFrameManager.setObjectValue(paramObject);
    return paramFrameManager;
  }
  
  public Bitmap getBitmap()
  {
    Object localObject = deserializeObjectValue();
    if ((localObject instanceof Bitmap)) {
      return (Bitmap)localObject;
    }
    return null;
  }
  
  public ByteBuffer getData()
  {
    Object localObject = deserializeObjectValue();
    if ((localObject instanceof ByteBuffer)) {
      return (ByteBuffer)localObject;
    }
    return null;
  }
  
  public float[] getFloats()
  {
    Object localObject = deserializeObjectValue();
    if ((localObject instanceof float[])) {
      return (float[])localObject;
    }
    return null;
  }
  
  public int[] getInts()
  {
    Object localObject = deserializeObjectValue();
    if ((localObject instanceof int[])) {
      return (int[])localObject;
    }
    return null;
  }
  
  public Object getObjectValue()
  {
    return deserializeObjectValue();
  }
  
  protected boolean hasNativeAllocation()
  {
    return false;
  }
  
  protected void releaseNativeAllocation() {}
  
  public void setBitmap(Bitmap paramBitmap)
  {
    assertFrameMutable();
    setGenericObjectValue(paramBitmap);
  }
  
  public void setData(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2)
  {
    assertFrameMutable();
    setGenericObjectValue(ByteBuffer.wrap(paramByteBuffer.array(), paramInt1, paramInt2));
  }
  
  public void setFloats(float[] paramArrayOfFloat)
  {
    assertFrameMutable();
    setGenericObjectValue(paramArrayOfFloat);
  }
  
  protected void setGenericObjectValue(Object paramObject)
  {
    serializeObjectValue(paramObject);
  }
  
  public void setInts(int[] paramArrayOfInt)
  {
    assertFrameMutable();
    setGenericObjectValue(paramArrayOfInt);
  }
  
  public String toString()
  {
    return "SerializedFrame (" + getFormat() + ")";
  }
  
  private class DirectByteInputStream
    extends InputStream
  {
    private byte[] mBuffer;
    private int mPos = 0;
    private int mSize;
    
    public DirectByteInputStream(byte[] paramArrayOfByte, int paramInt)
    {
      this.mBuffer = paramArrayOfByte;
      this.mSize = paramInt;
    }
    
    public final int available()
    {
      return this.mSize - this.mPos;
    }
    
    public final int read()
    {
      if (this.mPos < this.mSize)
      {
        byte[] arrayOfByte = this.mBuffer;
        int i = this.mPos;
        this.mPos = (i + 1);
        return arrayOfByte[i] & 0xFF;
      }
      return -1;
    }
    
    public final int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
      if (this.mPos >= this.mSize) {
        return -1;
      }
      int i = paramInt2;
      if (this.mPos + paramInt2 > this.mSize) {
        i = this.mSize - this.mPos;
      }
      System.arraycopy(this.mBuffer, this.mPos, paramArrayOfByte, paramInt1, i);
      this.mPos += i;
      return i;
    }
    
    public final long skip(long paramLong)
    {
      long l = paramLong;
      if (this.mPos + paramLong > this.mSize) {
        l = this.mSize - this.mPos;
      }
      if (l < 0L) {
        return 0L;
      }
      this.mPos = ((int)(this.mPos + l));
      return l;
    }
  }
  
  private class DirectByteOutputStream
    extends OutputStream
  {
    private byte[] mBuffer = null;
    private int mDataOffset = 0;
    private int mOffset = 0;
    
    public DirectByteOutputStream(int paramInt)
    {
      this.mBuffer = new byte[paramInt];
    }
    
    private final void ensureFit(int paramInt)
    {
      if (this.mOffset + paramInt > this.mBuffer.length)
      {
        byte[] arrayOfByte = this.mBuffer;
        this.mBuffer = new byte[Math.max(this.mOffset + paramInt, this.mBuffer.length * 2)];
        System.arraycopy(arrayOfByte, 0, this.mBuffer, 0, this.mOffset);
      }
    }
    
    public byte[] getByteArray()
    {
      return this.mBuffer;
    }
    
    public final SerializedFrame.DirectByteInputStream getInputStream()
    {
      return new SerializedFrame.DirectByteInputStream(SerializedFrame.this, this.mBuffer, this.mOffset);
    }
    
    public final int getSize()
    {
      return this.mOffset;
    }
    
    public final void markHeaderEnd()
    {
      this.mDataOffset = this.mOffset;
    }
    
    public final void reset()
    {
      this.mOffset = this.mDataOffset;
    }
    
    public final void write(int paramInt)
    {
      ensureFit(1);
      byte[] arrayOfByte = this.mBuffer;
      int i = this.mOffset;
      this.mOffset = (i + 1);
      arrayOfByte[i] = ((byte)paramInt);
    }
    
    public final void write(byte[] paramArrayOfByte)
    {
      write(paramArrayOfByte, 0, paramArrayOfByte.length);
    }
    
    public final void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
      ensureFit(paramInt2);
      System.arraycopy(paramArrayOfByte, paramInt1, this.mBuffer, this.mOffset, paramInt2);
      this.mOffset += paramInt2;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/SerializedFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */