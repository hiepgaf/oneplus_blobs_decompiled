package android.os;

import android.media.AudioAttributes;

public class NullVibrator
  extends Vibrator
{
  private static final NullVibrator sInstance = new NullVibrator();
  
  public static NullVibrator getInstance()
  {
    return sInstance;
  }
  
  public void cancel() {}
  
  public boolean hasVibrator()
  {
    return false;
  }
  
  public void vibrate(int paramInt, String paramString, long paramLong, AudioAttributes paramAudioAttributes) {}
  
  public void vibrate(int paramInt1, String paramString, long[] paramArrayOfLong, int paramInt2, AudioAttributes paramAudioAttributes)
  {
    if (paramInt2 >= paramArrayOfLong.length) {
      throw new ArrayIndexOutOfBoundsException();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/NullVibrator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */