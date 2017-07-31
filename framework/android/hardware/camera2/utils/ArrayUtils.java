package android.hardware.camera2.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ArrayUtils
{
  private static final boolean DEBUG = false;
  private static final String TAG = "ArrayUtils";
  
  private ArrayUtils()
  {
    throw new AssertionError();
  }
  
  public static boolean contains(int[] paramArrayOfInt, int paramInt)
  {
    return getArrayIndex(paramArrayOfInt, paramInt) != -1;
  }
  
  public static <T> boolean contains(T[] paramArrayOfT, T paramT)
  {
    return getArrayIndex(paramArrayOfT, paramT) != -1;
  }
  
  public static int[] convertStringListToIntArray(List<String> paramList, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    if (paramList == null) {
      return null;
    }
    paramList = convertStringListToIntList(paramList, paramArrayOfString, paramArrayOfInt);
    paramArrayOfString = new int[paramList.size()];
    int i = 0;
    while (i < paramArrayOfString.length)
    {
      paramArrayOfString[i] = ((Integer)paramList.get(i)).intValue();
      i += 1;
    }
    return paramArrayOfString;
  }
  
  public static List<Integer> convertStringListToIntList(List<String> paramList, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    if (paramList == null) {
      return null;
    }
    ArrayList localArrayList = new ArrayList(paramList.size());
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      int i = getArrayIndex(paramArrayOfString, (String)paramList.next());
      if ((i >= 0) && (i < paramArrayOfInt.length)) {
        localArrayList.add(Integer.valueOf(paramArrayOfInt[i]));
      }
    }
    return localArrayList;
  }
  
  public static int getArrayIndex(int[] paramArrayOfInt, int paramInt)
  {
    if (paramArrayOfInt == null) {
      return -1;
    }
    int i = 0;
    while (i < paramArrayOfInt.length)
    {
      if (paramArrayOfInt[i] == paramInt) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  public static <T> int getArrayIndex(T[] paramArrayOfT, T paramT)
  {
    if (paramArrayOfT == null) {
      return -1;
    }
    int j = 0;
    int i = 0;
    int k = paramArrayOfT.length;
    while (i < k)
    {
      if (Objects.equals(paramArrayOfT[i], paramT)) {
        return j;
      }
      j += 1;
      i += 1;
    }
    return -1;
  }
  
  public static int[] toIntArray(List<Integer> paramList)
  {
    if (paramList == null) {
      return null;
    }
    int[] arrayOfInt = new int[paramList.size()];
    int i = 0;
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      arrayOfInt[i] = ((Integer)paramList.next()).intValue();
      i += 1;
    }
    return arrayOfInt;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/utils/ArrayUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */