package android.hardware.camera2.marshal.impl;

import android.hardware.camera2.marshal.MarshalQueryable;
import android.hardware.camera2.marshal.Marshaler;
import android.hardware.camera2.params.RggbChannelVector;
import android.hardware.camera2.utils.TypeReference;
import java.nio.ByteBuffer;

public class MarshalQueryableRggbChannelVector
  implements MarshalQueryable<RggbChannelVector>
{
  private static final int SIZE = 16;
  
  public Marshaler<RggbChannelVector> createMarshaler(TypeReference<RggbChannelVector> paramTypeReference, int paramInt)
  {
    return new MarshalerRggbChannelVector(paramTypeReference, paramInt);
  }
  
  public boolean isTypeMappingSupported(TypeReference<RggbChannelVector> paramTypeReference, int paramInt)
  {
    if (paramInt == 2) {
      return RggbChannelVector.class.equals(paramTypeReference.getType());
    }
    return false;
  }
  
  private class MarshalerRggbChannelVector
    extends Marshaler<RggbChannelVector>
  {
    protected MarshalerRggbChannelVector(int paramInt)
    {
      super(paramInt, i);
    }
    
    public int getNativeSize()
    {
      return 16;
    }
    
    public void marshal(RggbChannelVector paramRggbChannelVector, ByteBuffer paramByteBuffer)
    {
      int i = 0;
      while (i < 4)
      {
        paramByteBuffer.putFloat(paramRggbChannelVector.getComponent(i));
        i += 1;
      }
    }
    
    public RggbChannelVector unmarshal(ByteBuffer paramByteBuffer)
    {
      return new RggbChannelVector(paramByteBuffer.getFloat(), paramByteBuffer.getFloat(), paramByteBuffer.getFloat(), paramByteBuffer.getFloat());
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/marshal/impl/MarshalQueryableRggbChannelVector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */