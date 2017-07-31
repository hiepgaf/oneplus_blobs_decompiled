package android.graphics;

import com.android.internal.util.ArrayUtils;

public class TemporaryBuffer
{
  private static char[] sTemp = null;
  
  public static char[] obtain(int paramInt)
  {
    try
    {
      char[] arrayOfChar2 = sTemp;
      sTemp = null;
      char[] arrayOfChar1;
      if (arrayOfChar2 != null)
      {
        arrayOfChar1 = arrayOfChar2;
        if (arrayOfChar2.length >= paramInt) {}
      }
      else
      {
        arrayOfChar1 = ArrayUtils.newUnpaddedCharArray(paramInt);
      }
      return arrayOfChar1;
    }
    finally {}
  }
  
  public static void recycle(char[] paramArrayOfChar)
  {
    if (paramArrayOfChar.length > 1000) {
      return;
    }
    try
    {
      sTemp = paramArrayOfChar;
      return;
    }
    finally
    {
      paramArrayOfChar = finally;
      throw paramArrayOfChar;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/TemporaryBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */