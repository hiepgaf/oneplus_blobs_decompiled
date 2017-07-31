package android.hardware.camera2.marshal;

import android.hardware.camera2.utils.TypeReference;
import com.android.internal.util.Preconditions;
import java.nio.ByteBuffer;

public abstract class Marshaler<T>
{
  public static int NATIVE_SIZE_DYNAMIC = -1;
  protected final int mNativeType;
  protected final TypeReference<T> mTypeReference;
  
  protected Marshaler(MarshalQueryable<T> paramMarshalQueryable, TypeReference<T> paramTypeReference, int paramInt)
  {
    this.mTypeReference = ((TypeReference)Preconditions.checkNotNull(paramTypeReference, "typeReference must not be null"));
    this.mNativeType = MarshalHelpers.checkNativeType(paramInt);
    if (!paramMarshalQueryable.isTypeMappingSupported(paramTypeReference, paramInt)) {
      throw new UnsupportedOperationException("Unsupported type marshaling for managed type " + paramTypeReference + " and native type " + MarshalHelpers.toStringNativeType(paramInt));
    }
  }
  
  public int calculateMarshalSize(T paramT)
  {
    int i = getNativeSize();
    if (i == NATIVE_SIZE_DYNAMIC) {
      throw new AssertionError("Override this function for dynamically-sized objects");
    }
    return i;
  }
  
  public abstract int getNativeSize();
  
  public int getNativeType()
  {
    return this.mNativeType;
  }
  
  public TypeReference<T> getTypeReference()
  {
    return this.mTypeReference;
  }
  
  public abstract void marshal(T paramT, ByteBuffer paramByteBuffer);
  
  public abstract T unmarshal(ByteBuffer paramByteBuffer);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/marshal/Marshaler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */