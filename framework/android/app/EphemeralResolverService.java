package android.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.EphemeralResolveInfo;
import android.os.BaseBundle;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IRemoteCallback;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import java.util.List;

public abstract class EphemeralResolverService
  extends Service
{
  private static final String EXTRA_PREFIX = "android.app.PREFIX";
  public static final String EXTRA_RESOLVE_INFO = "android.app.extra.RESOLVE_INFO";
  public static final String EXTRA_SEQUENCE = "android.app.extra.SEQUENCE";
  private Handler mHandler;
  
  public final void attachBaseContext(Context paramContext)
  {
    super.attachBaseContext(paramContext);
    this.mHandler = new ServiceHandler(paramContext.getMainLooper());
  }
  
  public final IBinder onBind(Intent paramIntent)
  {
    new IEphemeralResolver.Stub()
    {
      public void getEphemeralResolveInfoList(IRemoteCallback paramAnonymousIRemoteCallback, int[] paramAnonymousArrayOfInt, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        paramAnonymousIRemoteCallback = EphemeralResolverService.-get0(EphemeralResolverService.this).obtainMessage(1, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousIRemoteCallback);
        Bundle localBundle = new Bundle();
        localBundle.putIntArray("android.app.PREFIX", paramAnonymousArrayOfInt);
        paramAnonymousIRemoteCallback.setData(localBundle);
        paramAnonymousIRemoteCallback.sendToTarget();
      }
    };
  }
  
  public abstract List<EphemeralResolveInfo> onEphemeralResolveInfoList(int[] paramArrayOfInt, int paramInt);
  
  private final class ServiceHandler
    extends Handler
  {
    public static final int MSG_GET_EPHEMERAL_RESOLVE_INFO = 1;
    
    public ServiceHandler(Looper paramLooper)
    {
      super(null, true);
    }
    
    public void handleMessage(Message paramMessage)
    {
      int i = paramMessage.what;
      switch (i)
      {
      default: 
        throw new IllegalArgumentException("Unknown message: " + i);
      }
      IRemoteCallback localIRemoteCallback = (IRemoteCallback)paramMessage.obj;
      Object localObject = paramMessage.getData().getIntArray("android.app.PREFIX");
      localObject = EphemeralResolverService.this.onEphemeralResolveInfoList((int[])localObject, paramMessage.arg1);
      Bundle localBundle = new Bundle();
      localBundle.putInt("android.app.extra.SEQUENCE", paramMessage.arg2);
      localBundle.putParcelableList("android.app.extra.RESOLVE_INFO", (List)localObject);
      try
      {
        localIRemoteCallback.sendResult(localBundle);
        return;
      }
      catch (RemoteException paramMessage) {}
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/EphemeralResolverService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */