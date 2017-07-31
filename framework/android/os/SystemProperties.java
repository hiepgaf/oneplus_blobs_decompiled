package android.os;

import java.util.ArrayList;

public class SystemProperties
{
  public static final int PROP_NAME_MAX = 31;
  public static final int PROP_VALUE_MAX = 91;
  private static final ArrayList<Runnable> sChangeCallbacks = new ArrayList();
  
  public static void addChangeCallback(Runnable paramRunnable)
  {
    synchronized (sChangeCallbacks)
    {
      if (sChangeCallbacks.size() == 0) {
        native_add_change_callback();
      }
      sChangeCallbacks.add(paramRunnable);
      return;
    }
  }
  
  static void callChangeCallbacks()
  {
    synchronized (sChangeCallbacks)
    {
      int i = sChangeCallbacks.size();
      if (i == 0) {
        return;
      }
      ArrayList localArrayList2 = new ArrayList(sChangeCallbacks);
      i = 0;
      while (i < localArrayList2.size())
      {
        ((Runnable)localArrayList2.get(i)).run();
        i += 1;
      }
      return;
    }
  }
  
  public static String get(String paramString)
  {
    if (paramString.length() > 31) {
      throw new IllegalArgumentException("key.length > 31");
    }
    return native_get(paramString);
  }
  
  public static String get(String paramString1, String paramString2)
  {
    if (paramString1.length() > 31) {
      throw new IllegalArgumentException("key.length > 31");
    }
    return native_get(paramString1, paramString2);
  }
  
  public static boolean getBoolean(String paramString, boolean paramBoolean)
  {
    if (paramString.length() > 31) {
      throw new IllegalArgumentException("key.length > 31");
    }
    return native_get_boolean(paramString, paramBoolean);
  }
  
  public static int getInt(String paramString, int paramInt)
  {
    if (paramString.length() > 31) {
      throw new IllegalArgumentException("key.length > 31");
    }
    return native_get_int(paramString, paramInt);
  }
  
  public static long getLong(String paramString, long paramLong)
  {
    if (paramString.length() > 31) {
      throw new IllegalArgumentException("key.length > 31");
    }
    return native_get_long(paramString, paramLong);
  }
  
  private static native void native_add_change_callback();
  
  private static native String native_get(String paramString);
  
  private static native String native_get(String paramString1, String paramString2);
  
  private static native boolean native_get_boolean(String paramString, boolean paramBoolean);
  
  private static native int native_get_int(String paramString, int paramInt);
  
  private static native long native_get_long(String paramString, long paramLong);
  
  private static native void native_set(String paramString1, String paramString2);
  
  public static void set(String paramString1, String paramString2)
  {
    if (paramString1.length() > 31) {
      throw new IllegalArgumentException("key.length > 31");
    }
    if ((paramString2 != null) && (paramString2.length() > 91)) {
      throw new IllegalArgumentException("val.length > 91");
    }
    native_set(paramString1, paramString2);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/SystemProperties.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */