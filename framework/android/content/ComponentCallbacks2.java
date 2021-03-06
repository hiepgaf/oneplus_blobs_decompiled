package android.content;

public abstract interface ComponentCallbacks2
  extends ComponentCallbacks
{
  public static final int TRIM_MEMORY_BACKGROUND = 40;
  public static final int TRIM_MEMORY_COMPLETE = 80;
  public static final int TRIM_MEMORY_MODERATE = 60;
  public static final int TRIM_MEMORY_RUNNING_CRITICAL = 15;
  public static final int TRIM_MEMORY_RUNNING_LOW = 10;
  public static final int TRIM_MEMORY_RUNNING_MODERATE = 5;
  public static final int TRIM_MEMORY_UI_HIDDEN = 20;
  
  public abstract void onTrimMemory(int paramInt);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/ComponentCallbacks2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */