package android.hardware.camera2.marshal.impl;

import android.hardware.camera2.marshal.MarshalQueryable;
import android.hardware.camera2.marshal.Marshaler;
import android.hardware.camera2.params.BlackLevelPattern;
import android.hardware.camera2.utils.TypeReference;
import java.nio.ByteBuffer;

public class MarshalQueryableBlackLevelPattern
  implements MarshalQueryable<BlackLevelPattern>
{
  private static final int SIZE = 16;
  
  public Marshaler<BlackLevelPattern> createMarshaler(TypeReference<BlackLevelPattern> paramTypeReference, int paramInt)
  {
    return new MarshalerBlackLevelPattern(paramTypeReference, paramInt);
  }
  
  public boolean isTypeMappingSupported(TypeReference<BlackLevelPattern> paramTypeReference, int paramInt)
  {
    if (paramInt == 1) {
      return BlackLevelPattern.class.equals(paramTypeReference.getType());
    }
    return false;
  }
  
  private class MarshalerBlackLevelPattern
    extends Marshaler<BlackLevelPattern>
  {
    protected MarshalerBlackLevelPattern(int paramInt)
    {
      super(paramInt, i);
    }
    
    public int getNativeSize()
    {
      return 16;
    }
    
    public void marshal(BlackLevelPattern paramBlackLevelPattern, ByteBuffer paramByteBuffer)
    {
      int i = 0;
      while (i < 2)
      {
        int j = 0;
        while (j < 2)
        {
          paramByteBuffer.putInt(paramBlackLevelPattern.getOffsetForIndex(j, i));
          j += 1;
        }
        i += 1;
      }
    }
    
    public BlackLevelPattern unmarshal(ByteBuffer paramByteBuffer)
    {
      int[] arrayOfInt = new int[4];
      int i = 0;
      while (i < 4)
      {
        arrayOfInt[i] = paramByteBuffer.getInt();
        i += 1;
      }
      return new BlackLevelPattern(arrayOfInt);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/marshal/impl/MarshalQueryableBlackLevelPattern.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */