package android.hardware.camera2.marshal.impl;

import android.hardware.camera2.marshal.MarshalHelpers;
import android.hardware.camera2.marshal.MarshalQueryable;
import android.hardware.camera2.marshal.Marshaler;
import android.hardware.camera2.utils.TypeReference;
import android.util.Log;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class MarshalQueryableEnum<T extends Enum<T>>
  implements MarshalQueryable<T>
{
  private static final boolean DEBUG = false;
  private static final String TAG = MarshalQueryableEnum.class.getSimpleName();
  private static final int UINT8_MASK = 255;
  private static final int UINT8_MAX = 255;
  private static final int UINT8_MIN = 0;
  private static final HashMap<Class<? extends Enum>, int[]> sEnumValues = new HashMap();
  
  private static <T extends Enum<T>> T getEnumFromValue(Class<T> paramClass, int paramInt)
  {
    boolean bool = true;
    int[] arrayOfInt = (int[])sEnumValues.get(paramClass);
    int i;
    int j;
    label45:
    Enum[] arrayOfEnum;
    if (arrayOfInt != null)
    {
      int k = -1;
      i = 0;
      j = k;
      if (i < arrayOfInt.length)
      {
        if (arrayOfInt[i] == paramInt) {
          j = i;
        }
      }
      else
      {
        arrayOfEnum = (Enum[])paramClass.getEnumConstants();
        if ((j >= 0) && (j < arrayOfEnum.length)) {
          break label124;
        }
        if (arrayOfInt == null) {
          break label118;
        }
      }
    }
    for (;;)
    {
      throw new IllegalArgumentException(String.format("Argument 'value' (%d) was not a valid enum value for type %s (registered? %b)", new Object[] { Integer.valueOf(paramInt), paramClass, Boolean.valueOf(bool) }));
      i += 1;
      break;
      j = paramInt;
      break label45;
      label118:
      bool = false;
    }
    label124:
    return arrayOfEnum[j];
  }
  
  private static <T extends Enum<T>> int getEnumValue(T paramT)
  {
    int[] arrayOfInt = (int[])sEnumValues.get(paramT.getClass());
    int i = paramT.ordinal();
    if (arrayOfInt != null) {
      return arrayOfInt[i];
    }
    return i;
  }
  
  public static <T extends Enum<T>> void registerEnumValues(Class<T> paramClass, int[] paramArrayOfInt)
  {
    if (((Enum[])paramClass.getEnumConstants()).length != paramArrayOfInt.length) {
      throw new IllegalArgumentException("Expected values array to be the same size as the enumTypes values " + paramArrayOfInt.length + " for type " + paramClass);
    }
    sEnumValues.put(paramClass, paramArrayOfInt);
  }
  
  public Marshaler<T> createMarshaler(TypeReference<T> paramTypeReference, int paramInt)
  {
    return new MarshalerEnum(paramTypeReference, paramInt);
  }
  
  public boolean isTypeMappingSupported(TypeReference<T> paramTypeReference, int paramInt)
  {
    if (((paramInt == 1) || (paramInt == 0)) && ((paramTypeReference.getType() instanceof Class)))
    {
      paramTypeReference = (Class)paramTypeReference.getType();
      if (!paramTypeReference.isEnum()) {}
    }
    try
    {
      paramTypeReference.getDeclaredConstructor(new Class[] { String.class, Integer.TYPE });
      return true;
    }
    catch (SecurityException localSecurityException)
    {
      Log.e(TAG, "Can't marshal class " + paramTypeReference + "; not accessible");
      return false;
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      Log.e(TAG, "Can't marshal class " + paramTypeReference + "; no default constructor");
    }
    return false;
  }
  
  private class MarshalerEnum
    extends Marshaler<T>
  {
    private final Class<T> mClass;
    
    protected MarshalerEnum(int paramInt)
    {
      super(paramInt, i);
      this.mClass = paramInt.getRawType();
    }
    
    public int getNativeSize()
    {
      return MarshalHelpers.getPrimitiveTypeSize(this.mNativeType);
    }
    
    public void marshal(T paramT, ByteBuffer paramByteBuffer)
    {
      int i = MarshalQueryableEnum.-wrap0(paramT);
      if (this.mNativeType == 1)
      {
        paramByteBuffer.putInt(i);
        return;
      }
      if (this.mNativeType == 0)
      {
        if ((i < 0) || (i > 255)) {
          throw new UnsupportedOperationException(String.format("Enum value %x too large to fit into unsigned byte", new Object[] { Integer.valueOf(i) }));
        }
        paramByteBuffer.put((byte)i);
        return;
      }
      throw new AssertionError();
    }
    
    public T unmarshal(ByteBuffer paramByteBuffer)
    {
      switch (this.mNativeType)
      {
      default: 
        throw new AssertionError("Unexpected native type; impossible since its not supported");
      }
      for (int i = paramByteBuffer.getInt();; i = paramByteBuffer.get() & 0xFF) {
        return MarshalQueryableEnum.-wrap1(this.mClass, i);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/marshal/impl/MarshalQueryableEnum.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */