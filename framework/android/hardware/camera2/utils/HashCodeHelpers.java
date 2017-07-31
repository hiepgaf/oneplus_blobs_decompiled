package android.hardware.camera2.utils;

public final class HashCodeHelpers
{
  public static int hashCode(float... paramVarArgs)
  {
    int i = 0;
    if (paramVarArgs == null) {
      return 0;
    }
    int j = 1;
    int k = paramVarArgs.length;
    while (i < k)
    {
      j = (j << 5) - j ^ Float.floatToIntBits(paramVarArgs[i]);
      i += 1;
    }
    return j;
  }
  
  public static int hashCode(int... paramVarArgs)
  {
    int i = 0;
    if (paramVarArgs == null) {
      return 0;
    }
    int j = 1;
    int k = paramVarArgs.length;
    while (i < k)
    {
      j = (j << 5) - j ^ paramVarArgs[i];
      i += 1;
    }
    return j;
  }
  
  public static <T> int hashCodeGeneric(T... paramVarArgs)
  {
    if (paramVarArgs == null) {
      return 0;
    }
    int j = 1;
    int m = paramVarArgs.length;
    int i = 0;
    if (i < m)
    {
      T ? = paramVarArgs[i];
      if (? == null) {}
      for (int k = 0;; k = ?.hashCode())
      {
        j = (j << 5) - j ^ k;
        i += 1;
        break;
      }
    }
    return j;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/utils/HashCodeHelpers.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */