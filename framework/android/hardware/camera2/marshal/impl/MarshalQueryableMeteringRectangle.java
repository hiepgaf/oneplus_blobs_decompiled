package android.hardware.camera2.marshal.impl;

import android.hardware.camera2.marshal.MarshalQueryable;
import android.hardware.camera2.marshal.Marshaler;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.utils.TypeReference;
import java.nio.ByteBuffer;

public class MarshalQueryableMeteringRectangle
  implements MarshalQueryable<MeteringRectangle>
{
  private static final int SIZE = 20;
  
  public Marshaler<MeteringRectangle> createMarshaler(TypeReference<MeteringRectangle> paramTypeReference, int paramInt)
  {
    return new MarshalerMeteringRectangle(paramTypeReference, paramInt);
  }
  
  public boolean isTypeMappingSupported(TypeReference<MeteringRectangle> paramTypeReference, int paramInt)
  {
    if (paramInt == 1) {
      return MeteringRectangle.class.equals(paramTypeReference.getType());
    }
    return false;
  }
  
  private class MarshalerMeteringRectangle
    extends Marshaler<MeteringRectangle>
  {
    protected MarshalerMeteringRectangle(int paramInt)
    {
      super(paramInt, i);
    }
    
    public int getNativeSize()
    {
      return 20;
    }
    
    public void marshal(MeteringRectangle paramMeteringRectangle, ByteBuffer paramByteBuffer)
    {
      int i = paramMeteringRectangle.getX();
      int j = paramMeteringRectangle.getY();
      int k = paramMeteringRectangle.getWidth();
      int m = paramMeteringRectangle.getHeight();
      int n = paramMeteringRectangle.getMeteringWeight();
      paramByteBuffer.putInt(i);
      paramByteBuffer.putInt(j);
      paramByteBuffer.putInt(i + k);
      paramByteBuffer.putInt(j + m);
      paramByteBuffer.putInt(n);
    }
    
    public MeteringRectangle unmarshal(ByteBuffer paramByteBuffer)
    {
      int i = paramByteBuffer.getInt();
      int j = paramByteBuffer.getInt();
      return new MeteringRectangle(i, j, paramByteBuffer.getInt() - i, paramByteBuffer.getInt() - j, paramByteBuffer.getInt());
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/marshal/impl/MarshalQueryableMeteringRectangle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */