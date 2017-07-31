package android.hardware.camera2.marshal.impl;

import android.hardware.camera2.marshal.MarshalQueryable;
import android.hardware.camera2.marshal.Marshaler;
import android.hardware.camera2.params.StreamConfiguration;
import android.hardware.camera2.utils.TypeReference;
import java.nio.ByteBuffer;

public class MarshalQueryableStreamConfiguration
  implements MarshalQueryable<StreamConfiguration>
{
  private static final int SIZE = 16;
  
  public Marshaler<StreamConfiguration> createMarshaler(TypeReference<StreamConfiguration> paramTypeReference, int paramInt)
  {
    return new MarshalerStreamConfiguration(paramTypeReference, paramInt);
  }
  
  public boolean isTypeMappingSupported(TypeReference<StreamConfiguration> paramTypeReference, int paramInt)
  {
    if (paramInt == 1) {
      return paramTypeReference.getType().equals(StreamConfiguration.class);
    }
    return false;
  }
  
  private class MarshalerStreamConfiguration
    extends Marshaler<StreamConfiguration>
  {
    protected MarshalerStreamConfiguration(int paramInt)
    {
      super(paramInt, i);
    }
    
    public int getNativeSize()
    {
      return 16;
    }
    
    public void marshal(StreamConfiguration paramStreamConfiguration, ByteBuffer paramByteBuffer)
    {
      paramByteBuffer.putInt(paramStreamConfiguration.getFormat());
      paramByteBuffer.putInt(paramStreamConfiguration.getWidth());
      paramByteBuffer.putInt(paramStreamConfiguration.getHeight());
      if (paramStreamConfiguration.isInput()) {}
      for (int i = 1;; i = 0)
      {
        paramByteBuffer.putInt(i);
        return;
      }
    }
    
    public StreamConfiguration unmarshal(ByteBuffer paramByteBuffer)
    {
      int i = paramByteBuffer.getInt();
      int j = paramByteBuffer.getInt();
      int k = paramByteBuffer.getInt();
      if (paramByteBuffer.getInt() != 0) {}
      for (boolean bool = true;; bool = false) {
        return new StreamConfiguration(i, j, k, bool);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/marshal/impl/MarshalQueryableStreamConfiguration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */