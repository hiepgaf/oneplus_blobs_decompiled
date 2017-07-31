package android.printservice.recommendation;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import java.util.List;

public abstract class RecommendationService
  extends Service
{
  private static final String LOG_TAG = "PrintServiceRecS";
  public static final String SERVICE_INTERFACE = "android.printservice.recommendation.RecommendationService";
  private IRecommendationServiceCallbacks mCallbacks;
  private Handler mHandler;
  
  protected void attachBaseContext(Context paramContext)
  {
    super.attachBaseContext(paramContext);
    this.mHandler = new MyHandler();
  }
  
  public final IBinder onBind(Intent paramIntent)
  {
    new IRecommendationService.Stub()
    {
      public void registerCallbacks(IRecommendationServiceCallbacks paramAnonymousIRecommendationServiceCallbacks)
      {
        if (paramAnonymousIRecommendationServiceCallbacks != null)
        {
          RecommendationService.-get1(RecommendationService.this).obtainMessage(1, paramAnonymousIRecommendationServiceCallbacks).sendToTarget();
          return;
        }
        RecommendationService.-get1(RecommendationService.this).obtainMessage(2).sendToTarget();
      }
    };
  }
  
  public abstract void onConnected();
  
  public abstract void onDisconnected();
  
  public final void updateRecommendations(List<RecommendationInfo> paramList)
  {
    this.mHandler.obtainMessage(3, paramList).sendToTarget();
  }
  
  private class MyHandler
    extends Handler
  {
    static final int MSG_CONNECT = 1;
    static final int MSG_DISCONNECT = 2;
    static final int MSG_UPDATE = 3;
    
    MyHandler()
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      case 1: 
        RecommendationService.-set0(RecommendationService.this, (IRecommendationServiceCallbacks)paramMessage.obj);
        RecommendationService.this.onConnected();
        return;
      case 2: 
        RecommendationService.this.onDisconnected();
        RecommendationService.-set0(RecommendationService.this, null);
        return;
      }
      try
      {
        RecommendationService.-get0(RecommendationService.this).onRecommendationsUpdated((List)paramMessage.obj);
        return;
      }
      catch (RemoteException|NullPointerException paramMessage)
      {
        Log.e("PrintServiceRecS", "Could not update recommended services", paramMessage);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/printservice/recommendation/RecommendationService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */