package android.hardware.camera2.marshal.impl;

import android.hardware.camera2.marshal.MarshalQueryable;
import android.hardware.camera2.marshal.MarshalRegistry;
import android.hardware.camera2.marshal.Marshaler;
import android.hardware.camera2.utils.TypeReference;
import android.util.Log;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class MarshalQueryableArray<T>
  implements MarshalQueryable<T>
{
  private static final boolean DEBUG = false;
  private static final String TAG = MarshalQueryableArray.class.getSimpleName();
  
  public Marshaler<T> createMarshaler(TypeReference<T> paramTypeReference, int paramInt)
  {
    return new MarshalerArray(paramTypeReference, paramInt);
  }
  
  public boolean isTypeMappingSupported(TypeReference<T> paramTypeReference, int paramInt)
  {
    return paramTypeReference.getRawType().isArray();
  }
  
  private class MarshalerArray
    extends Marshaler<T>
  {
    private final Class<T> mClass;
    private final Class<?> mComponentClass;
    private final Marshaler<?> mComponentMarshaler;
    
    protected MarshalerArray(int paramInt)
    {
      super(paramInt, i);
      this.mClass = paramInt.getRawType();
      this$1 = paramInt.getComponentType();
      this.mComponentMarshaler = MarshalRegistry.getMarshaler(MarshalQueryableArray.this, this.mNativeType);
      this.mComponentClass = MarshalQueryableArray.this.getRawType();
    }
    
    private <TElem> int calculateElementMarshalSize(Marshaler<TElem> paramMarshaler, Object paramObject, int paramInt)
    {
      return paramMarshaler.calculateMarshalSize(Array.get(paramObject, paramInt));
    }
    
    private Object copyListToArray(ArrayList<?> paramArrayList, Object paramObject)
    {
      return paramArrayList.toArray((Object[])paramObject);
    }
    
    private <TElem> void marshalArrayElement(Marshaler<TElem> paramMarshaler, ByteBuffer paramByteBuffer, Object paramObject, int paramInt)
    {
      paramMarshaler.marshal(Array.get(paramObject, paramInt), paramByteBuffer);
    }
    
    public int calculateMarshalSize(T paramT)
    {
      int i = this.mComponentMarshaler.getNativeSize();
      int k = Array.getLength(paramT);
      if (i != Marshaler.NATIVE_SIZE_DYNAMIC) {
        return i * k;
      }
      int j = 0;
      i = 0;
      while (i < k)
      {
        j += calculateElementMarshalSize(this.mComponentMarshaler, paramT, i);
        i += 1;
      }
      return j;
    }
    
    public int getNativeSize()
    {
      return NATIVE_SIZE_DYNAMIC;
    }
    
    public void marshal(T paramT, ByteBuffer paramByteBuffer)
    {
      int j = Array.getLength(paramT);
      int i = 0;
      while (i < j)
      {
        marshalArrayElement(this.mComponentMarshaler, paramByteBuffer, paramT, i);
        i += 1;
      }
    }
    
    public T unmarshal(ByteBuffer paramByteBuffer)
    {
      int i = this.mComponentMarshaler.getNativeSize();
      if (i != Marshaler.NATIVE_SIZE_DYNAMIC)
      {
        int k = paramByteBuffer.remaining();
        int j = k / i;
        if (k % i != 0) {
          throw new UnsupportedOperationException("Arrays for " + this.mTypeReference + " must be packed tighly into a multiple of " + i + "; but there are " + k % i + " left over bytes");
        }
        Object localObject2 = Array.newInstance(this.mComponentClass, j);
        i = 0;
        for (;;)
        {
          localObject1 = localObject2;
          if (i >= j) {
            break;
          }
          Array.set(localObject2, i, this.mComponentMarshaler.unmarshal(paramByteBuffer));
          i += 1;
        }
      }
      Object localObject1 = new ArrayList();
      while (paramByteBuffer.hasRemaining()) {
        ((ArrayList)localObject1).add(this.mComponentMarshaler.unmarshal(paramByteBuffer));
      }
      i = ((ArrayList)localObject1).size();
      localObject1 = copyListToArray((ArrayList)localObject1, Array.newInstance(this.mComponentClass, i));
      if (paramByteBuffer.remaining() != 0) {
        Log.e(MarshalQueryableArray.-get0(), "Trailing bytes (" + paramByteBuffer.remaining() + ") left over after unpacking " + this.mClass);
      }
      return (T)this.mClass.cast(localObject1);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/marshal/impl/MarshalQueryableArray.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */