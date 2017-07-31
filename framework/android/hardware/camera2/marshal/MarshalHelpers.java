package android.hardware.camera2.marshal;

import android.util.Rational;
import com.android.internal.util.Preconditions;

public final class MarshalHelpers
{
  public static final int SIZEOF_BYTE = 1;
  public static final int SIZEOF_DOUBLE = 8;
  public static final int SIZEOF_FLOAT = 4;
  public static final int SIZEOF_INT32 = 4;
  public static final int SIZEOF_INT64 = 8;
  public static final int SIZEOF_RATIONAL = 8;
  
  private MarshalHelpers()
  {
    throw new AssertionError();
  }
  
  public static int checkNativeType(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new UnsupportedOperationException("Unknown nativeType " + paramInt);
    }
    return paramInt;
  }
  
  public static int checkNativeTypeEquals(int paramInt1, int paramInt2)
  {
    if (paramInt1 != paramInt2) {
      throw new UnsupportedOperationException(String.format("Expected native type %d, but got %d", new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) }));
    }
    return paramInt2;
  }
  
  public static <T> Class<T> checkPrimitiveClass(Class<T> paramClass)
  {
    Preconditions.checkNotNull(paramClass, "klass must not be null");
    if (isPrimitiveClass(paramClass)) {
      return paramClass;
    }
    throw new UnsupportedOperationException("Unsupported class '" + paramClass + "'; expected a metadata primitive class");
  }
  
  public static int getPrimitiveTypeSize(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new UnsupportedOperationException("Unknown type, can't get size for " + paramInt);
    case 0: 
      return 1;
    case 1: 
      return 4;
    case 2: 
      return 4;
    case 3: 
      return 8;
    case 4: 
      return 8;
    }
    return 8;
  }
  
  public static <T> boolean isPrimitiveClass(Class<T> paramClass)
  {
    if (paramClass == null) {
      return false;
    }
    if ((paramClass == Byte.TYPE) || (paramClass == Byte.class)) {
      return true;
    }
    if ((paramClass == Integer.TYPE) || (paramClass == Integer.class)) {
      return true;
    }
    if ((paramClass == Float.TYPE) || (paramClass == Float.class)) {
      return true;
    }
    if ((paramClass == Long.TYPE) || (paramClass == Long.class)) {
      return true;
    }
    if ((paramClass == Double.TYPE) || (paramClass == Double.class)) {
      return true;
    }
    return paramClass == Rational.class;
  }
  
  public static String toStringNativeType(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "UNKNOWN(" + paramInt + ")";
    case 0: 
      return "TYPE_BYTE";
    case 1: 
      return "TYPE_INT32";
    case 2: 
      return "TYPE_FLOAT";
    case 3: 
      return "TYPE_INT64";
    case 4: 
      return "TYPE_DOUBLE";
    }
    return "TYPE_RATIONAL";
  }
  
  public static <T> Class<T> wrapClassIfPrimitive(Class<T> paramClass)
  {
    if (paramClass == Byte.TYPE) {
      return Byte.class;
    }
    if (paramClass == Integer.TYPE) {
      return Integer.class;
    }
    if (paramClass == Float.TYPE) {
      return Float.class;
    }
    if (paramClass == Long.TYPE) {
      return Long.class;
    }
    if (paramClass == Double.TYPE) {
      return Double.class;
    }
    return paramClass;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/marshal/MarshalHelpers.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */