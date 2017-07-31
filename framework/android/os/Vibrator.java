package android.os;

import android.app.ActivityThread;
import android.content.Context;
import android.media.AudioAttributes;

public abstract class Vibrator
{
  private final String mPackageName;
  
  public Vibrator()
  {
    this.mPackageName = ActivityThread.currentPackageName();
  }
  
  protected Vibrator(Context paramContext)
  {
    this.mPackageName = paramContext.getOpPackageName();
  }
  
  public abstract void cancel();
  
  public abstract boolean hasVibrator();
  
  public abstract void vibrate(int paramInt, String paramString, long paramLong, AudioAttributes paramAudioAttributes);
  
  public abstract void vibrate(int paramInt1, String paramString, long[] paramArrayOfLong, int paramInt2, AudioAttributes paramAudioAttributes);
  
  public void vibrate(long paramLong)
  {
    vibrate(paramLong, null);
  }
  
  public void vibrate(long paramLong, AudioAttributes paramAudioAttributes)
  {
    vibrate(Process.myUid(), this.mPackageName, paramLong, paramAudioAttributes);
  }
  
  public void vibrate(long[] paramArrayOfLong, int paramInt)
  {
    vibrate(paramArrayOfLong, paramInt, null);
  }
  
  public void vibrate(long[] paramArrayOfLong, int paramInt, AudioAttributes paramAudioAttributes)
  {
    vibrate(Process.myUid(), this.mPackageName, paramArrayOfLong, paramInt, paramAudioAttributes);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/Vibrator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */