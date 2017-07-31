package android.service.media;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

public abstract class CameraPrewarmService
  extends Service
{
  public static final String ACTION_PREWARM = "android.service.media.CameraPrewarmService.ACTION_PREWARM";
  public static final int MSG_CAMERA_FIRED = 1;
  private boolean mCameraIntentFired;
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default: 
        super.handleMessage(paramAnonymousMessage);
        return;
      }
      CameraPrewarmService.-set0(CameraPrewarmService.this, true);
    }
  };
  
  public IBinder onBind(Intent paramIntent)
  {
    if ("android.service.media.CameraPrewarmService.ACTION_PREWARM".equals(paramIntent.getAction()))
    {
      onPrewarm();
      return new Messenger(this.mHandler).getBinder();
    }
    return null;
  }
  
  public abstract void onCooldown(boolean paramBoolean);
  
  public abstract void onPrewarm();
  
  public boolean onUnbind(Intent paramIntent)
  {
    if ("android.service.media.CameraPrewarmService.ACTION_PREWARM".equals(paramIntent.getAction())) {
      onCooldown(this.mCameraIntentFired);
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/media/CameraPrewarmService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */