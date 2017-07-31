package android.hardware.camera2.marshal.impl;

import android.hardware.camera2.marshal.MarshalQueryable;
import android.hardware.camera2.marshal.Marshaler;
import android.hardware.camera2.params.StreamConfigurationDuration;
import android.hardware.camera2.utils.TypeReference;
import java.nio.ByteBuffer;

public class MarshalQueryableStreamConfigurationDuration
  implements MarshalQueryable<StreamConfigurationDuration>
{
  private static final long MASK_UNSIGNED_INT = 4294967295L;
  private static final int SIZE = 32;
  
  public Marshaler<StreamConfigurationDuration> createMarshaler(TypeReference<StreamConfigurationDuration> paramTypeReference, int paramInt)
  {
    return new MarshalerStreamConfigurationDuration(paramTypeReference, paramInt);
  }
  
  public boolean isTypeMappingSupported(TypeReference<StreamConfigurationDuration> paramTypeReference, int paramInt)
  {
    if (paramInt == 3) {
      return StreamConfigurationDuration.class.equals(paramTypeReference.getType());
    }
    return false;
  }
  
  private class MarshalerStreamConfigurationDuration
    extends Marshaler<StreamConfigurationDuration>
  {
    protected MarshalerStreamConfigurationDuration(int paramInt)
    {
      super(paramInt, i);
    }
    
    public int getNativeSize()
    {
      return 32;
    }
    
    public void marshal(StreamConfigurationDuration paramStreamConfigurationDuration, ByteBuffer paramByteBuffer)
    {
      paramByteBuffer.putLong(paramStreamConfigurationDuration.getFormat() & 0xFFFFFFFF);
      paramByteBuffer.putLong(paramStreamConfigurationDuration.getWidth());
      paramByteBuffer.putLong(paramStreamConfigurationDuration.getHeight());
      paramByteBuffer.putLong(paramStreamConfigurationDuration.getDuration());
    }
    
    public StreamConfigurationDuration unmarshal(ByteBuffer paramByteBuffer)
    {
      return new StreamConfigurationDuration((int)paramByteBuffer.getLong(), (int)paramByteBuffer.getLong(), (int)paramByteBuffer.getLong(), paramByteBuffer.getLong());
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/marshal/impl/MarshalQueryableStreamConfigurationDuration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */