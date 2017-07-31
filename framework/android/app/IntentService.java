package android.app;

import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

public abstract class IntentService
  extends Service
{
  private String mName;
  private boolean mRedelivery;
  private volatile ServiceHandler mServiceHandler;
  private volatile Looper mServiceLooper;
  
  public IntentService(String paramString)
  {
    this.mName = paramString;
  }
  
  public IBinder onBind(Intent paramIntent)
  {
    return null;
  }
  
  public void onCreate()
  {
    super.onCreate();
    HandlerThread localHandlerThread = new HandlerThread("IntentService[" + this.mName + "]");
    localHandlerThread.start();
    this.mServiceLooper = localHandlerThread.getLooper();
    this.mServiceHandler = new ServiceHandler(this.mServiceLooper);
  }
  
  public void onDestroy()
  {
    this.mServiceLooper.quit();
  }
  
  protected abstract void onHandleIntent(Intent paramIntent);
  
  public void onStart(Intent paramIntent, int paramInt)
  {
    Message localMessage = this.mServiceHandler.obtainMessage();
    localMessage.arg1 = paramInt;
    localMessage.obj = paramIntent;
    this.mServiceHandler.sendMessage(localMessage);
  }
  
  public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2)
  {
    onStart(paramIntent, paramInt2);
    if (this.mRedelivery) {
      return 3;
    }
    return 2;
  }
  
  public void setIntentRedelivery(boolean paramBoolean)
  {
    this.mRedelivery = paramBoolean;
  }
  
  private final class ServiceHandler
    extends Handler
  {
    public ServiceHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      IntentService.this.onHandleIntent((Intent)paramMessage.obj);
      IntentService.this.stopSelf(paramMessage.arg1);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/IntentService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */