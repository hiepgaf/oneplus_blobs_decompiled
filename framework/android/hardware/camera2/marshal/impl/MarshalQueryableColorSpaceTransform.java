package android.hardware.camera2.marshal.impl;

import android.hardware.camera2.marshal.MarshalQueryable;
import android.hardware.camera2.marshal.Marshaler;
import android.hardware.camera2.params.ColorSpaceTransform;
import android.hardware.camera2.utils.TypeReference;
import java.nio.ByteBuffer;

public class MarshalQueryableColorSpaceTransform
  implements MarshalQueryable<ColorSpaceTransform>
{
  private static final int ELEMENTS_INT32 = 18;
  private static final int SIZE = 72;
  
  public Marshaler<ColorSpaceTransform> createMarshaler(TypeReference<ColorSpaceTransform> paramTypeReference, int paramInt)
  {
    return new MarshalerColorSpaceTransform(paramTypeReference, paramInt);
  }
  
  public boolean isTypeMappingSupported(TypeReference<ColorSpaceTransform> paramTypeReference, int paramInt)
  {
    if (paramInt == 5) {
      return ColorSpaceTransform.class.equals(paramTypeReference.getType());
    }
    return false;
  }
  
  private class MarshalerColorSpaceTransform
    extends Marshaler<ColorSpaceTransform>
  {
    protected MarshalerColorSpaceTransform(int paramInt)
    {
      super(paramInt, i);
    }
    
    public int getNativeSize()
    {
      return 72;
    }
    
    public void marshal(ColorSpaceTransform paramColorSpaceTransform, ByteBuffer paramByteBuffer)
    {
      int[] arrayOfInt = new int[18];
      paramColorSpaceTransform.copyElements(arrayOfInt, 0);
      int i = 0;
      while (i < 18)
      {
        paramByteBuffer.putInt(arrayOfInt[i]);
        i += 1;
      }
    }
    
    public ColorSpaceTransform unmarshal(ByteBuffer paramByteBuffer)
    {
      int[] arrayOfInt = new int[18];
      int i = 0;
      while (i < 18)
      {
        arrayOfInt[i] = paramByteBuffer.getInt();
        i += 1;
      }
      return new ColorSpaceTransform(arrayOfInt);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/marshal/impl/MarshalQueryableColorSpaceTransform.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */