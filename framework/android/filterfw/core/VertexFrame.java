package android.filterfw.core;

import android.graphics.Bitmap;
import java.nio.ByteBuffer;

public class VertexFrame
  extends Frame
{
  private int vertexFrameId = -1;
  
  static
  {
    System.loadLibrary("filterfw");
  }
  
  VertexFrame(FrameFormat paramFrameFormat, FrameManager paramFrameManager)
  {
    super(paramFrameFormat, paramFrameManager);
    if (getFormat().getSize() <= 0) {
      throw new IllegalArgumentException("Initializing vertex frame with zero size!");
    }
    if (!nativeAllocate(getFormat().getSize())) {
      throw new RuntimeException("Could not allocate vertex frame!");
    }
  }
  
  private native int getNativeVboId();
  
  private native boolean nativeAllocate(int paramInt);
  
  private native boolean nativeDeallocate();
  
  private native boolean setNativeData(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  private native boolean setNativeFloats(float[] paramArrayOfFloat);
  
  private native boolean setNativeInts(int[] paramArrayOfInt);
  
  public Bitmap getBitmap()
  {
    throw new RuntimeException("Vertex frames do not support reading data!");
  }
  
  public ByteBuffer getData()
  {
    throw new RuntimeException("Vertex frames do not support reading data!");
  }
  
  public float[] getFloats()
  {
    throw new RuntimeException("Vertex frames do not support reading data!");
  }
  
  public int[] getInts()
  {
    throw new RuntimeException("Vertex frames do not support reading data!");
  }
  
  public Object getObjectValue()
  {
    throw new RuntimeException("Vertex frames do not support reading data!");
  }
  
  public int getVboId()
  {
    return getNativeVboId();
  }
  
  /* Error */
  protected boolean hasNativeAllocation()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 23	android/filterfw/core/VertexFrame:vertexFrameId	I
    //   6: istore_1
    //   7: iload_1
    //   8: iconst_m1
    //   9: if_icmpeq +9 -> 18
    //   12: iconst_1
    //   13: istore_2
    //   14: aload_0
    //   15: monitorexit
    //   16: iload_2
    //   17: ireturn
    //   18: iconst_0
    //   19: istore_2
    //   20: goto -6 -> 14
    //   23: astore_3
    //   24: aload_0
    //   25: monitorexit
    //   26: aload_3
    //   27: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	28	0	this	VertexFrame
    //   6	4	1	i	int
    //   13	7	2	bool	boolean
    //   23	4	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	7	23	finally
  }
  
  protected void releaseNativeAllocation()
  {
    try
    {
      nativeDeallocate();
      this.vertexFrameId = -1;
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void setBitmap(Bitmap paramBitmap)
  {
    throw new RuntimeException("Unsupported: Cannot set vertex frame bitmap value!");
  }
  
  public void setData(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2)
  {
    assertFrameMutable();
    paramByteBuffer = paramByteBuffer.array();
    if (getFormat().getSize() != paramByteBuffer.length) {
      throw new RuntimeException("Data size in setData does not match vertex frame size!");
    }
    if (!setNativeData(paramByteBuffer, paramInt1, paramInt2)) {
      throw new RuntimeException("Could not set vertex frame data!");
    }
  }
  
  public void setDataFromFrame(Frame paramFrame)
  {
    super.setDataFromFrame(paramFrame);
  }
  
  public void setFloats(float[] paramArrayOfFloat)
  {
    assertFrameMutable();
    if (!setNativeFloats(paramArrayOfFloat)) {
      throw new RuntimeException("Could not set int values for vertex frame!");
    }
  }
  
  public void setInts(int[] paramArrayOfInt)
  {
    assertFrameMutable();
    if (!setNativeInts(paramArrayOfInt)) {
      throw new RuntimeException("Could not set int values for vertex frame!");
    }
  }
  
  public String toString()
  {
    return "VertexFrame (" + getFormat() + ") with VBO ID " + getVboId();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/VertexFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */