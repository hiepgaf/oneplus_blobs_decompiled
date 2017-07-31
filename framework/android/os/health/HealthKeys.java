package android.os.health;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Arrays;

public class HealthKeys
{
  public static final int BASE_PACKAGE = 40000;
  public static final int BASE_PID = 20000;
  public static final int BASE_PROCESS = 30000;
  public static final int BASE_SERVICE = 50000;
  public static final int BASE_UID = 10000;
  public static final int TYPE_COUNT = 5;
  public static final int TYPE_MEASUREMENT = 1;
  public static final int TYPE_MEASUREMENTS = 4;
  public static final int TYPE_STATS = 2;
  public static final int TYPE_TIMER = 0;
  public static final int TYPE_TIMERS = 3;
  public static final int UNKNOWN_KEY = 0;
  
  @Retention(RetentionPolicy.RUNTIME)
  @Target({java.lang.annotation.ElementType.FIELD})
  public static @interface Constant
  {
    int type();
  }
  
  public static class Constants
  {
    private final String mDataType;
    private final int[][] mKeys = new int[5][];
    
    public Constants(Class paramClass)
    {
      this.mDataType = paramClass.getSimpleName();
      Field[] arrayOfField = paramClass.getDeclaredFields();
      int j = arrayOfField.length;
      HealthKeys.SortedIntArray[] arrayOfSortedIntArray = new HealthKeys.SortedIntArray[this.mKeys.length];
      int i = 0;
      while (i < arrayOfSortedIntArray.length)
      {
        arrayOfSortedIntArray[i] = new HealthKeys.SortedIntArray(j);
        i += 1;
      }
      i = 0;
      while (i < j)
      {
        paramClass = arrayOfField[i];
        HealthKeys.Constant localConstant = (HealthKeys.Constant)paramClass.getAnnotation(HealthKeys.Constant.class);
        int k;
        if (localConstant != null)
        {
          k = localConstant.type();
          if (k >= arrayOfSortedIntArray.length) {
            throw new RuntimeException("Unknown Constant type " + k + " on " + paramClass);
          }
        }
        try
        {
          arrayOfSortedIntArray[k].addValue(paramClass.getInt(null));
          i += 1;
        }
        catch (IllegalAccessException localIllegalAccessException)
        {
          throw new RuntimeException("Can't read constant value type=" + k + " field=" + paramClass, localIllegalAccessException);
        }
      }
      i = 0;
      while (i < arrayOfSortedIntArray.length)
      {
        this.mKeys[i] = arrayOfSortedIntArray[i].getArray();
        i += 1;
      }
    }
    
    public String getDataType()
    {
      return this.mDataType;
    }
    
    public int getIndex(int paramInt1, int paramInt2)
    {
      int i = Arrays.binarySearch(this.mKeys[paramInt1], paramInt2);
      if (i >= 0) {
        return i;
      }
      throw new RuntimeException("Unknown Constant " + paramInt2 + " (of type " + paramInt1 + " )");
    }
    
    public int[] getKeys(int paramInt)
    {
      return this.mKeys[paramInt];
    }
    
    public int getSize(int paramInt)
    {
      return this.mKeys[paramInt].length;
    }
  }
  
  private static class SortedIntArray
  {
    int[] mArray;
    int mCount;
    
    SortedIntArray(int paramInt)
    {
      this.mArray = new int[paramInt];
    }
    
    void addValue(int paramInt)
    {
      int[] arrayOfInt = this.mArray;
      int i = this.mCount;
      this.mCount = (i + 1);
      arrayOfInt[i] = paramInt;
    }
    
    int[] getArray()
    {
      if (this.mCount == this.mArray.length)
      {
        Arrays.sort(this.mArray);
        return this.mArray;
      }
      int[] arrayOfInt = new int[this.mCount];
      System.arraycopy(this.mArray, 0, arrayOfInt, 0, this.mCount);
      Arrays.sort(arrayOfInt);
      return arrayOfInt;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/health/HealthKeys.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */