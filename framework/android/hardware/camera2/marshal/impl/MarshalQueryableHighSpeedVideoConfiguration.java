package android.hardware.camera2.marshal.impl;

import android.hardware.camera2.marshal.MarshalQueryable;
import android.hardware.camera2.marshal.Marshaler;
import android.hardware.camera2.params.HighSpeedVideoConfiguration;
import android.hardware.camera2.utils.TypeReference;
import java.nio.ByteBuffer;

public class MarshalQueryableHighSpeedVideoConfiguration
  implements MarshalQueryable<HighSpeedVideoConfiguration>
{
  private static final int SIZE = 20;
  
  public Marshaler<HighSpeedVideoConfiguration> createMarshaler(TypeReference<HighSpeedVideoConfiguration> paramTypeReference, int paramInt)
  {
    return new MarshalerHighSpeedVideoConfiguration(paramTypeReference, paramInt);
  }
  
  public boolean isTypeMappingSupported(TypeReference<HighSpeedVideoConfiguration> paramTypeReference, int paramInt)
  {
    if (paramInt == 1) {
      return paramTypeReference.getType().equals(HighSpeedVideoConfiguration.class);
    }
    return false;
  }
  
  private class MarshalerHighSpeedVideoConfiguration
    extends Marshaler<HighSpeedVideoConfiguration>
  {
    protected MarshalerHighSpeedVideoConfiguration(int paramInt)
    {
      super(paramInt, i);
    }
    
    public int getNativeSize()
    {
      return 20;
    }
    
    public void marshal(HighSpeedVideoConfiguration paramHighSpeedVideoConfiguration, ByteBuffer paramByteBuffer)
    {
      paramByteBuffer.putInt(paramHighSpeedVideoConfiguration.getWidth());
      paramByteBuffer.putInt(paramHighSpeedVideoConfiguration.getHeight());
      paramByteBuffer.putInt(paramHighSpeedVideoConfiguration.getFpsMin());
      paramByteBuffer.putInt(paramHighSpeedVideoConfiguration.getFpsMax());
      paramByteBuffer.putInt(paramHighSpeedVideoConfiguration.getBatchSizeMax());
    }
    
    public HighSpeedVideoConfiguration unmarshal(ByteBuffer paramByteBuffer)
    {
      return new HighSpeedVideoConfiguration(paramByteBuffer.getInt(), paramByteBuffer.getInt(), paramByteBuffer.getInt(), paramByteBuffer.getInt(), paramByteBuffer.getInt());
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/marshal/impl/MarshalQueryableHighSpeedVideoConfiguration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */