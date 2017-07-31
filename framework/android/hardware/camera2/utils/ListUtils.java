package android.hardware.camera2.utils;

import java.util.Iterator;
import java.util.List;

public class ListUtils
{
  private ListUtils()
  {
    throw new AssertionError();
  }
  
  public static <T> boolean listContains(List<T> paramList, T paramT)
  {
    if (paramList == null) {
      return false;
    }
    return paramList.contains(paramT);
  }
  
  public static <T> boolean listElementsEqualTo(List<T> paramList, T paramT)
  {
    boolean bool = false;
    if (paramList == null) {
      return false;
    }
    if (paramList.size() == 1) {
      bool = paramList.contains(paramT);
    }
    return bool;
  }
  
  public static <T> T listSelectFirstFrom(List<T> paramList, T[] paramArrayOfT)
  {
    if (paramList == null) {
      return null;
    }
    int i = 0;
    int j = paramArrayOfT.length;
    while (i < j)
    {
      T ? = paramArrayOfT[i];
      if (paramList.contains(?)) {
        return ?;
      }
      i += 1;
    }
    return null;
  }
  
  public static <T> String listToString(List<T> paramList)
  {
    if (paramList == null) {
      return null;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append('[');
    int j = paramList.size();
    int i = 0;
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      localStringBuilder.append(paramList.next());
      if (i != j - 1) {
        localStringBuilder.append(',');
      }
      i += 1;
    }
    localStringBuilder.append(']');
    return localStringBuilder.toString();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/utils/ListUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */