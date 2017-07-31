package android.hardware.camera2.marshal.impl;

import android.hardware.camera2.marshal.MarshalQueryable;
import android.hardware.camera2.marshal.Marshaler;
import android.hardware.camera2.utils.TypeReference;
import java.nio.ByteBuffer;

public class MarshalQueryableBoolean
  implements MarshalQueryable<Boolean>
{
  public Marshaler<Boolean> createMarshaler(TypeReference<Boolean> paramTypeReference, int paramInt)
  {
    return new MarshalerBoolean(paramTypeReference, paramInt);
  }
  
  public boolean isTypeMappingSupported(TypeReference<Boolean> paramTypeReference, int paramInt)
  {
    boolean bool2 = false;
    boolean bool1;
    if (!Boolean.class.equals(paramTypeReference.getType()))
    {
      bool1 = bool2;
      if (!Boolean.TYPE.equals(paramTypeReference.getType())) {}
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
  
  private class MarshalerBoolean
    extends Marshaler<Boolean>
  {
    protected MarshalerBoolean(int paramInt)
    {
      super(paramInt, i);
    }
    
    public int getNativeSize()
    {
      return 1;
    }
    
    public void marshal(Boolean paramBoolean, ByteBuffer paramByteBuffer)
    {
      if (paramBoolean.booleanValue()) {}
      for (int i = 1;; i = 0)
      {
        paramByteBuffer.put((byte)i);
        return;
      }
    }
    
    public Boolean unmarshal(ByteBuffer paramByteBuffer)
    {
      boolean bool = false;
      if (paramByteBuffer.get() != 0) {
        bool = true;
      }
      return Boolean.valueOf(bool);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/marshal/impl/MarshalQueryableBoolean.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */