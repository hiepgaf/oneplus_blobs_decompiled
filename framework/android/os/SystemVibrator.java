package android.os;

import android.content.Context;
import android.media.AudioAttributes;
import android.util.Log;

public class SystemVibrator
  extends Vibrator
{
  private static final String TAG = "Vibrator";
  private final IVibratorService mService = IVibratorService.Stub.asInterface(ServiceManager.getService("vibrator"));
  private final Binder mToken = new Binder();
  
  public SystemVibrator() {}
  
  public SystemVibrator(Context paramContext)
  {
    super(paramContext);
  }
  
  private static int usageForAttributes(AudioAttributes paramAudioAttributes)
  {
    if (paramAudioAttributes != null) {
      return paramAudioAttributes.getUsage();
    }
    return 0;
  }
  
  public void cancel()
  {
    if (this.mService == null) {
      return;
    }
    try
    {
      this.mService.cancelVibrate(this.mToken);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w("Vibrator", "Failed to cancel vibration.", localRemoteException);
    }
  }
  
  public boolean hasVibrator()
  {
    if (this.mService == null)
    {
      Log.w("Vibrator", "Failed to vibrate; no vibrator service.");
      return false;
    }
    try
    {
      boolean bool = this.mService.hasVibrator();
      return bool;
    }
    catch (RemoteException localRemoteException) {}
    return false;
  }
  
  public void vibrate(int paramInt, String paramString, long paramLong, AudioAttributes paramAudioAttributes)
  {
    if (this.mService == null)
    {
      Log.w("Vibrator", "Failed to vibrate; no vibrator service.");
      return;
    }
    try
    {
      this.mService.vibrate(paramInt, paramString, paramLong, usageForAttributes(paramAudioAttributes), this.mToken);
      return;
    }
    catch (RemoteException paramString)
    {
      Log.w("Vibrator", "Failed to vibrate.", paramString);
    }
  }
  
  public void vibrate(int paramInt1, String paramString, long[] paramArrayOfLong, int paramInt2, AudioAttributes paramAudioAttributes)
  {
    if (this.mService == null)
    {
      Log.w("Vibrator", "Failed to vibrate; no vibrator service.");
      return;
    }
    if (paramInt2 < paramArrayOfLong.length) {
      try
      {
        this.mService.vibratePattern(paramInt1, paramString, paramArrayOfLong, paramInt2, usageForAttributes(paramAudioAttributes), this.mToken);
        return;
      }
      catch (RemoteException paramString)
      {
        Log.w("Vibrator", "Failed to vibrate.", paramString);
        return;
      }
    }
    throw new ArrayIndexOutOfBoundsException();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/SystemVibrator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */