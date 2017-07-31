package android.media.session;

import android.content.ComponentName;
import android.content.Context;
import android.media.IRemoteVolumeController;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public final class MediaSessionManager
{
  private static final String TAG = "SessionManager";
  private Context mContext;
  private final ArrayMap<OnActiveSessionsChangedListener, SessionsChangedWrapper> mListeners = new ArrayMap();
  private final Object mLock = new Object();
  private final ISessionManager mService;
  
  public MediaSessionManager(Context paramContext)
  {
    this.mContext = paramContext;
    this.mService = ISessionManager.Stub.asInterface(ServiceManager.getService("media_session"));
  }
  
  public void addOnActiveSessionsChangedListener(OnActiveSessionsChangedListener paramOnActiveSessionsChangedListener, ComponentName paramComponentName)
  {
    addOnActiveSessionsChangedListener(paramOnActiveSessionsChangedListener, paramComponentName, null);
  }
  
  public void addOnActiveSessionsChangedListener(OnActiveSessionsChangedListener paramOnActiveSessionsChangedListener, ComponentName paramComponentName, int paramInt, Handler arg4)
  {
    if (paramOnActiveSessionsChangedListener == null) {
      throw new IllegalArgumentException("listener may not be null");
    }
    Object localObject = ???;
    if (??? == null) {
      localObject = new Handler();
    }
    synchronized (this.mLock)
    {
      if (this.mListeners.get(paramOnActiveSessionsChangedListener) != null)
      {
        Log.w("SessionManager", "Attempted to add session listener twice, ignoring.");
        return;
      }
      localObject = new SessionsChangedWrapper(this.mContext, paramOnActiveSessionsChangedListener, (Handler)localObject);
      try
      {
        this.mService.addSessionsListener(SessionsChangedWrapper.-get3((SessionsChangedWrapper)localObject), paramComponentName, paramInt);
        this.mListeners.put(paramOnActiveSessionsChangedListener, localObject);
        return;
      }
      catch (RemoteException paramOnActiveSessionsChangedListener)
      {
        for (;;)
        {
          Log.e("SessionManager", "Error in addOnActiveSessionsChangedListener.", paramOnActiveSessionsChangedListener);
        }
      }
    }
  }
  
  public void addOnActiveSessionsChangedListener(OnActiveSessionsChangedListener paramOnActiveSessionsChangedListener, ComponentName paramComponentName, Handler paramHandler)
  {
    addOnActiveSessionsChangedListener(paramOnActiveSessionsChangedListener, paramComponentName, UserHandle.myUserId(), paramHandler);
  }
  
  public ISession createSession(MediaSession.CallbackStub paramCallbackStub, String paramString, int paramInt)
    throws RemoteException
  {
    return this.mService.createSession(this.mContext.getPackageName(), paramCallbackStub, paramString, paramInt);
  }
  
  public void dispatchAdjustVolume(int paramInt1, int paramInt2, int paramInt3)
  {
    try
    {
      this.mService.dispatchAdjustVolume(paramInt1, paramInt2, paramInt3);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("SessionManager", "Failed to send adjust volume.", localRemoteException);
    }
  }
  
  public void dispatchMediaKeyEvent(KeyEvent paramKeyEvent)
  {
    dispatchMediaKeyEvent(paramKeyEvent, false);
  }
  
  public void dispatchMediaKeyEvent(KeyEvent paramKeyEvent, boolean paramBoolean)
  {
    try
    {
      this.mService.dispatchMediaKeyEvent(paramKeyEvent, paramBoolean);
      return;
    }
    catch (RemoteException paramKeyEvent)
    {
      Log.e("SessionManager", "Failed to send key event.", paramKeyEvent);
    }
  }
  
  public List<MediaController> getActiveSessions(ComponentName paramComponentName)
  {
    return getActiveSessionsForUser(paramComponentName, UserHandle.myUserId());
  }
  
  public List<MediaController> getActiveSessionsForUser(ComponentName paramComponentName, int paramInt)
  {
    localArrayList = new ArrayList();
    try
    {
      paramComponentName = this.mService.getSessions(paramComponentName, paramInt);
      int i = paramComponentName.size();
      paramInt = 0;
      while (paramInt < i)
      {
        localArrayList.add(new MediaController(this.mContext, ISessionController.Stub.asInterface((IBinder)paramComponentName.get(paramInt))));
        paramInt += 1;
      }
      return localArrayList;
    }
    catch (RemoteException paramComponentName)
    {
      Log.e("SessionManager", "Failed to get active sessions: ", paramComponentName);
    }
  }
  
  public boolean isGlobalPriorityActive()
  {
    try
    {
      boolean bool = this.mService.isGlobalPriorityActive();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("SessionManager", "Failed to check if the global priority is active.", localRemoteException);
    }
    return false;
  }
  
  public void removeOnActiveSessionsChangedListener(OnActiveSessionsChangedListener paramOnActiveSessionsChangedListener)
  {
    if (paramOnActiveSessionsChangedListener == null) {
      throw new IllegalArgumentException("listener may not be null");
    }
    synchronized (this.mLock)
    {
      paramOnActiveSessionsChangedListener = (SessionsChangedWrapper)this.mListeners.remove(paramOnActiveSessionsChangedListener);
      if (paramOnActiveSessionsChangedListener != null) {}
      try
      {
        this.mService.removeSessionsListener(SessionsChangedWrapper.-get3(paramOnActiveSessionsChangedListener));
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Log.e("SessionManager", "Error in removeOnActiveSessionsChangedListener.", localRemoteException);
          SessionsChangedWrapper.-wrap0(paramOnActiveSessionsChangedListener);
        }
        paramOnActiveSessionsChangedListener = finally;
        throw paramOnActiveSessionsChangedListener;
      }
      finally
      {
        SessionsChangedWrapper.-wrap0(paramOnActiveSessionsChangedListener);
      }
      return;
    }
  }
  
  public void setRemoteVolumeController(IRemoteVolumeController paramIRemoteVolumeController)
  {
    try
    {
      this.mService.setRemoteVolumeController(paramIRemoteVolumeController);
      return;
    }
    catch (RemoteException paramIRemoteVolumeController)
    {
      Log.e("SessionManager", "Error in setRemoteVolumeController.", paramIRemoteVolumeController);
    }
  }
  
  public static abstract interface OnActiveSessionsChangedListener
  {
    public abstract void onActiveSessionsChanged(List<MediaController> paramList);
  }
  
  private static final class SessionsChangedWrapper
  {
    private Context mContext;
    private Handler mHandler;
    private MediaSessionManager.OnActiveSessionsChangedListener mListener;
    private final IActiveSessionsListener.Stub mStub = new IActiveSessionsListener.Stub()
    {
      public void onActiveSessionsChanged(final List<MediaSession.Token> paramAnonymousList)
      {
        if (MediaSessionManager.SessionsChangedWrapper.-get1(MediaSessionManager.SessionsChangedWrapper.this) != null) {
          MediaSessionManager.SessionsChangedWrapper.-get1(MediaSessionManager.SessionsChangedWrapper.this).post(new Runnable()
          {
            public void run()
            {
              if (MediaSessionManager.SessionsChangedWrapper.-get2(MediaSessionManager.SessionsChangedWrapper.this) != null)
              {
                ArrayList localArrayList = new ArrayList();
                int j = paramAnonymousList.size();
                int i = 0;
                while (i < j)
                {
                  localArrayList.add(new MediaController(MediaSessionManager.SessionsChangedWrapper.-get0(MediaSessionManager.SessionsChangedWrapper.this), (MediaSession.Token)paramAnonymousList.get(i)));
                  i += 1;
                }
                MediaSessionManager.SessionsChangedWrapper.-get2(MediaSessionManager.SessionsChangedWrapper.this).onActiveSessionsChanged(localArrayList);
              }
            }
          });
        }
      }
    };
    
    public SessionsChangedWrapper(Context paramContext, MediaSessionManager.OnActiveSessionsChangedListener paramOnActiveSessionsChangedListener, Handler paramHandler)
    {
      this.mContext = paramContext;
      this.mListener = paramOnActiveSessionsChangedListener;
      this.mHandler = paramHandler;
    }
    
    private void release()
    {
      this.mContext = null;
      this.mListener = null;
      this.mHandler = null;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/session/MediaSessionManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */