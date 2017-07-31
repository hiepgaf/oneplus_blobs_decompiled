package android.hardware.camera2.marshal.impl;

import android.hardware.camera2.marshal.MarshalQueryable;
import android.hardware.camera2.marshal.Marshaler;
import android.hardware.camera2.utils.TypeReference;
import android.util.SizeF;
import java.nio.ByteBuffer;

public class MarshalQueryableSizeF
  implements MarshalQueryable<SizeF>
{
  private static final int SIZE = 8;
  
  public Marshaler<SizeF> createMarshaler(TypeReference<SizeF> paramTypeReference, int paramInt)
  {
    return new MarshalerSizeF(paramTypeReference, paramInt);
  }
  
  public boolean isTypeMappingSupported(TypeReference<SizeF> paramTypeReference, int paramInt)
  {
    if (paramInt == 2) {
      return SizeF.class.equals(paramTypeReference.getType());
    }
    return false;
  }
  
  private class MarshalerSizeF
    extends Marshaler<SizeF>
  {
    protected MarshalerSizeF(int paramInt)
    {
      super(paramInt, i);
    }
    
    public int getNativeSize()
    {
      return 8;
    }
    
    public void marshal(SizeF paramSizeF, ByteBuffer paramByteBuffer)
    {
      paramByteBuffer.putFloat(paramSizeF.getWidth());
      paramByteBuffer.putFloat(paramSizeF.getHeight());
    }
    
    public SizeF unmarshal(ByteBuffer paramByteBuffer)
    {
      return new SizeF(paramByteBuffer.getFloat(), paramByteBuffer.getFloat());
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/marshal/impl/MarshalQueryableSizeF.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */