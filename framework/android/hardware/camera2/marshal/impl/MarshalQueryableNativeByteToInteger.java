package android.hardware.camera2.marshal.impl;

import android.hardware.camera2.marshal.MarshalQueryable;
import android.hardware.camera2.marshal.Marshaler;
import android.hardware.camera2.utils.TypeReference;
import java.nio.ByteBuffer;

public class MarshalQueryableNativeByteToInteger
  implements MarshalQueryable<Integer>
{
  private static final int UINT8_MASK = 255;
  
  public Marshaler<Integer> createMarshaler(TypeReference<Integer> paramTypeReference, int paramInt)
  {
    return new MarshalerNativeByteToInteger(paramTypeReference, paramInt);
  }
  
  public boolean isTypeMappingSupported(TypeReference<Integer> paramTypeReference, int paramInt)
  {
    boolean bool2 = false;
    boolean bool1;
    if (!Integer.class.equals(paramTypeReference.getType()))
    {
      bool1 = bool2;
      if (!Integer.TYPE.equals(paramTypeReference.getType())) {}
    }
    else
    {
      bool1 = bool2;
      if (paramInt == 0) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private class MarshalerNativeByteToInteger
    extends Marshaler<Integer>
  {
    protected MarshalerNativeByteToInteger(int paramInt)
    {
      super(paramInt, i);
    }
    
    public int getNativeSize()
    {
      return 1;
    }
    
    public void marshal(Integer paramInteger, ByteBuffer paramByteBuffer)
    {
      paramByteBuffer.put((byte)paramInteger.intValue());
    }
    
    public Integer unmarshal(ByteBuffer paramByteBuffer)
    {
      return Integer.valueOf(paramByteBuffer.get() & 0xFF);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/marshal/impl/MarshalQueryableNativeByteToInteger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */