package android.hardware.camera2.marshal.impl;

import android.hardware.camera2.marshal.MarshalQueryable;
import android.hardware.camera2.marshal.Marshaler;
import android.hardware.camera2.utils.TypeReference;
import android.util.Size;
import java.nio.ByteBuffer;

public class MarshalQueryableSize
  implements MarshalQueryable<Size>
{
  private static final int SIZE = 8;
  
  public Marshaler<Size> createMarshaler(TypeReference<Size> paramTypeReference, int paramInt)
  {
    return new MarshalerSize(paramTypeReference, paramInt);
  }
  
  public boolean isTypeMappingSupported(TypeReference<Size> paramTypeReference, int paramInt)
  {
    if (paramInt == 1) {
      return Size.class.equals(paramTypeReference.getType());
    }
    return false;
  }
  
  private class MarshalerSize
    extends Marshaler<Size>
  {
    protected MarshalerSize(int paramInt)
    {
      super(paramInt, i);
    }
    
    public int getNativeSize()
    {
      return 8;
    }
    
    public void marshal(Size paramSize, ByteBuffer paramByteBuffer)
    {
      paramByteBuffer.putInt(paramSize.getWidth());
      paramByteBuffer.putInt(paramSize.getHeight());
    }
    
    public Size unmarshal(ByteBuffer paramByteBuffer)
    {
      return new Size(paramByteBuffer.getInt(), paramByteBuffer.getInt());
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/marshal/impl/MarshalQueryableSize.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */