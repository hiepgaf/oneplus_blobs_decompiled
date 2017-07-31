package android.service.quicksettings;

import android.app.Dialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.Window;

public class TileService
  extends Service
{
  public static final String ACTION_QS_TILE = "android.service.quicksettings.action.QS_TILE";
  public static final String ACTION_QS_TILE_PREFERENCES = "android.service.quicksettings.action.QS_TILE_PREFERENCES";
  public static final String ACTION_REQUEST_LISTENING = "android.service.quicksettings.action.REQUEST_LISTENING";
  public static final String EXTRA_COMPONENT = "android.service.quicksettings.extra.COMPONENT";
  public static final String EXTRA_SERVICE = "service";
  public static final String EXTRA_TOKEN = "token";
  public static final String META_DATA_ACTIVE_TILE = "android.service.quicksettings.ACTIVE_TILE";
  private final H mHandler = new H(Looper.getMainLooper());
  private boolean mListening = false;
  private IQSService mService;
  private Tile mTile;
  private IBinder mTileToken;
  private IBinder mToken;
  private Runnable mUnlockRunnable;
  
  public static final void requestListeningState(Context paramContext, ComponentName paramComponentName)
  {
    Intent localIntent = new Intent("android.service.quicksettings.action.REQUEST_LISTENING");
    localIntent.putExtra("android.service.quicksettings.extra.COMPONENT", paramComponentName);
    paramContext.sendBroadcast(localIntent, "android.permission.BIND_QUICK_SETTINGS_TILE");
  }
  
  public final Tile getQsTile()
  {
    return this.mTile;
  }
  
  public final boolean isLocked()
  {
    try
    {
      boolean bool = this.mService.isLocked();
      return bool;
    }
    catch (RemoteException localRemoteException) {}
    return true;
  }
  
  public final boolean isSecure()
  {
    try
    {
      boolean bool = this.mService.isSecure();
      return bool;
    }
    catch (RemoteException localRemoteException) {}
    return true;
  }
  
  public IBinder onBind(Intent paramIntent)
  {
    this.mService = IQSService.Stub.asInterface(paramIntent.getIBinderExtra("service"));
    this.mTileToken = paramIntent.getIBinderExtra("token");
    try
    {
      this.mTile = this.mService.getTile(this.mTileToken);
      if (this.mTile != null)
      {
        this.mTile.setService(this.mService, this.mTileToken);
        this.mHandler.sendEmptyMessage(7);
      }
      new IQSTileService.Stub()
      {
        public void onClick(IBinder paramAnonymousIBinder)
          throws RemoteException
        {
          TileService.-get0(TileService.this).obtainMessage(5, paramAnonymousIBinder).sendToTarget();
        }
        
        public void onStartListening()
          throws RemoteException
        {
          TileService.-get0(TileService.this).sendEmptyMessage(1);
        }
        
        public void onStopListening()
          throws RemoteException
        {
          TileService.-get0(TileService.this).sendEmptyMessage(2);
        }
        
        public void onTileAdded()
          throws RemoteException
        {
          TileService.-get0(TileService.this).sendEmptyMessage(3);
        }
        
        public void onTileRemoved()
          throws RemoteException
        {
          TileService.-get0(TileService.this).sendEmptyMessage(4);
        }
        
        public void onUnlockComplete()
          throws RemoteException
        {
          TileService.-get0(TileService.this).sendEmptyMessage(6);
        }
      };
    }
    catch (RemoteException paramIntent)
    {
      throw new RuntimeException("Unable to reach IQSService", paramIntent);
    }
  }
  
  public void onClick() {}
  
  public void onDestroy()
  {
    if (this.mListening)
    {
      onStopListening();
      this.mListening = false;
    }
    super.onDestroy();
  }
  
  public void onStartListening() {}
  
  public void onStopListening() {}
  
  public void onTileAdded() {}
  
  public void onTileRemoved() {}
  
  public final void setStatusIcon(Icon paramIcon, String paramString)
  {
    if (this.mService != null) {}
    try
    {
      this.mService.updateStatusIcon(this.mTileToken, paramIcon, paramString);
      return;
    }
    catch (RemoteException paramIcon) {}
  }
  
  public final void showDialog(Dialog paramDialog)
  {
    paramDialog.getWindow().getAttributes().token = this.mToken;
    paramDialog.getWindow().setType(2035);
    paramDialog.getWindow().getDecorView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener()
    {
      public void onViewAttachedToWindow(View paramAnonymousView) {}
      
      public void onViewDetachedFromWindow(View paramAnonymousView)
      {
        try
        {
          TileService.-get2(TileService.this).onDialogHidden(TileService.-get3(TileService.this));
          return;
        }
        catch (RemoteException paramAnonymousView) {}
      }
    });
    paramDialog.show();
    try
    {
      this.mService.onShowDialog(this.mTileToken);
      return;
    }
    catch (RemoteException paramDialog) {}
  }
  
  public final void startActivityAndCollapse(Intent paramIntent)
  {
    startActivity(paramIntent);
    try
    {
      this.mService.onStartActivity(this.mTileToken);
      return;
    }
    catch (RemoteException paramIntent) {}
  }
  
  public final void unlockAndRun(Runnable paramRunnable)
  {
    this.mUnlockRunnable = paramRunnable;
    try
    {
      this.mService.startUnlockAndRun(this.mTileToken);
      return;
    }
    catch (RemoteException paramRunnable) {}
  }
  
  private class H
    extends Handler
  {
    private static final int MSG_START_LISTENING = 1;
    private static final int MSG_START_SUCCESS = 7;
    private static final int MSG_STOP_LISTENING = 2;
    private static final int MSG_TILE_ADDED = 3;
    private static final int MSG_TILE_CLICKED = 5;
    private static final int MSG_TILE_REMOVED = 4;
    private static final int MSG_UNLOCK_COMPLETE = 6;
    
    public H(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
      case 3: 
      case 4: 
      case 2: 
      case 1: 
      case 5: 
      case 6: 
        do
        {
          do
          {
            do
            {
              return;
              TileService.this.onTileAdded();
              return;
              if (TileService.-get1(TileService.this))
              {
                TileService.-set0(TileService.this, false);
                TileService.this.onStopListening();
              }
              TileService.this.onTileRemoved();
              return;
            } while (!TileService.-get1(TileService.this));
            TileService.-set0(TileService.this, false);
            TileService.this.onStopListening();
            return;
          } while (TileService.-get1(TileService.this));
          TileService.-set0(TileService.this, true);
          TileService.this.onStartListening();
          return;
          TileService.-set1(TileService.this, (IBinder)paramMessage.obj);
          TileService.this.onClick();
          return;
        } while (TileService.-get4(TileService.this) == null);
        TileService.-get4(TileService.this).run();
        return;
      }
      try
      {
        TileService.-get2(TileService.this).onStartSuccessful(TileService.-get3(TileService.this));
        return;
      }
      catch (RemoteException paramMessage) {}
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/quicksettings/TileService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */