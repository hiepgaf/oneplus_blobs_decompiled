package android.hardware.camera2.marshal;

import android.hardware.camera2.utils.TypeReference;

public abstract interface MarshalQueryable<T>
{
  public abstract Marshaler<T> createMarshaler(TypeReference<T> paramTypeReference, int paramInt);
  
  public abstract boolean isTypeMappingSupported(TypeReference<T> paramTypeReference, int paramInt);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/marshal/MarshalQueryable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */