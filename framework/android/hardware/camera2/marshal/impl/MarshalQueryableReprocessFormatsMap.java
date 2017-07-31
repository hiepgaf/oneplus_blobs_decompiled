package android.hardware.camera2.marshal.impl;

import android.hardware.camera2.marshal.MarshalQueryable;
import android.hardware.camera2.marshal.Marshaler;
import android.hardware.camera2.params.ReprocessFormatsMap;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.hardware.camera2.utils.TypeReference;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class MarshalQueryableReprocessFormatsMap
  implements MarshalQueryable<ReprocessFormatsMap>
{
  public Marshaler<ReprocessFormatsMap> createMarshaler(TypeReference<ReprocessFormatsMap> paramTypeReference, int paramInt)
  {
    return new MarshalerReprocessFormatsMap(paramTypeReference, paramInt);
  }
  
  public boolean isTypeMappingSupported(TypeReference<ReprocessFormatsMap> paramTypeReference, int paramInt)
  {
    if (paramInt == 1) {
      return paramTypeReference.getType().equals(ReprocessFormatsMap.class);
    }
    return false;
  }
  
  private class MarshalerReprocessFormatsMap
    extends Marshaler<ReprocessFormatsMap>
  {
    protected MarshalerReprocessFormatsMap(int paramInt)
    {
      super(paramInt, i);
    }
    
    public int calculateMarshalSize(ReprocessFormatsMap paramReprocessFormatsMap)
    {
      int j = 0;
      int[] arrayOfInt = paramReprocessFormatsMap.getInputs();
      int i = 0;
      int k = arrayOfInt.length;
      while (i < k)
      {
        j = j + 1 + 1 + paramReprocessFormatsMap.getOutputs(arrayOfInt[i]).length;
        i += 1;
      }
      return j * 4;
    }
    
    public int getNativeSize()
    {
      return NATIVE_SIZE_DYNAMIC;
    }
    
    public void marshal(ReprocessFormatsMap paramReprocessFormatsMap, ByteBuffer paramByteBuffer)
    {
      int[] arrayOfInt1 = StreamConfigurationMap.imageFormatToInternal(paramReprocessFormatsMap.getInputs());
      int k = arrayOfInt1.length;
      int i = 0;
      while (i < k)
      {
        int j = arrayOfInt1[i];
        paramByteBuffer.putInt(j);
        int[] arrayOfInt2 = StreamConfigurationMap.imageFormatToInternal(paramReprocessFormatsMap.getOutputs(j));
        paramByteBuffer.putInt(arrayOfInt2.length);
        int m = arrayOfInt2.length;
        j = 0;
        while (j < m)
        {
          paramByteBuffer.putInt(arrayOfInt2[j]);
          j += 1;
        }
        i += 1;
      }
    }
    
    public ReprocessFormatsMap unmarshal(ByteBuffer paramByteBuffer)
    {
      int i = paramByteBuffer.remaining() / 4;
      if (paramByteBuffer.remaining() % 4 != 0) {
        throw new AssertionError("ReprocessFormatsMap was not TYPE_INT32");
      }
      int[] arrayOfInt = new int[i];
      paramByteBuffer.asIntBuffer().get(arrayOfInt);
      return new ReprocessFormatsMap(arrayOfInt);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/marshal/impl/MarshalQueryableReprocessFormatsMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */