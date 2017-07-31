package android.filterfw.core;

class GLFrameTimer
{
  private static StopWatchMap mTimer = null;
  
  public static StopWatchMap get()
  {
    if (mTimer == null) {
      mTimer = new StopWatchMap();
    }
    return mTimer;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/GLFrameTimer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */