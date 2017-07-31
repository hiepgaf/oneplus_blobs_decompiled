package android.media.tv;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Rect;
import android.hardware.hdmi.HdmiDeviceInfo;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.InputEventReceiver;
import android.view.KeyEvent;
import android.view.KeyEvent.Callback;
import android.view.KeyEvent.DispatcherState;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewRootImpl;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import com.android.internal.os.SomeArgs;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class TvInputService
  extends Service
{
  private static final boolean DEBUG = false;
  private static final int DETACH_OVERLAY_VIEW_TIMEOUT_MS = 5000;
  public static final String SERVICE_INTERFACE = "android.media.tv.TvInputService";
  public static final String SERVICE_META_DATA = "android.media.tv.input";
  private static final String TAG = "TvInputService";
  private final RemoteCallbackList<ITvInputServiceCallback> mCallbacks = new RemoteCallbackList();
  private final Handler mServiceHandler = new ServiceHandler(null);
  private TvInputManager mTvInputManager;
  
  public static boolean isNavigationKey(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return false;
    }
    return true;
  }
  
  private boolean isPassthroughInput(String paramString)
  {
    if (this.mTvInputManager == null) {
      this.mTvInputManager = ((TvInputManager)getSystemService("tv_input"));
    }
    paramString = this.mTvInputManager.getTvInputInfo(paramString);
    if (paramString != null) {
      return paramString.isPassthroughInput();
    }
    return false;
  }
  
  public final IBinder onBind(Intent paramIntent)
  {
    new ITvInputService.Stub()
    {
      public void createRecordingSession(ITvInputSessionCallback paramAnonymousITvInputSessionCallback, String paramAnonymousString)
      {
        if (paramAnonymousITvInputSessionCallback == null) {
          return;
        }
        SomeArgs localSomeArgs = SomeArgs.obtain();
        localSomeArgs.arg1 = paramAnonymousITvInputSessionCallback;
        localSomeArgs.arg2 = paramAnonymousString;
        TvInputService.-get1(TvInputService.this).obtainMessage(3, localSomeArgs).sendToTarget();
      }
      
      public void createSession(InputChannel paramAnonymousInputChannel, ITvInputSessionCallback paramAnonymousITvInputSessionCallback, String paramAnonymousString)
      {
        if (paramAnonymousInputChannel == null) {
          Log.w("TvInputService", "Creating session without input channel");
        }
        if (paramAnonymousITvInputSessionCallback == null) {
          return;
        }
        SomeArgs localSomeArgs = SomeArgs.obtain();
        localSomeArgs.arg1 = paramAnonymousInputChannel;
        localSomeArgs.arg2 = paramAnonymousITvInputSessionCallback;
        localSomeArgs.arg3 = paramAnonymousString;
        TvInputService.-get1(TvInputService.this).obtainMessage(1, localSomeArgs).sendToTarget();
      }
      
      public void notifyHardwareAdded(TvInputHardwareInfo paramAnonymousTvInputHardwareInfo)
      {
        TvInputService.-get1(TvInputService.this).obtainMessage(4, paramAnonymousTvInputHardwareInfo).sendToTarget();
      }
      
      public void notifyHardwareRemoved(TvInputHardwareInfo paramAnonymousTvInputHardwareInfo)
      {
        TvInputService.-get1(TvInputService.this).obtainMessage(5, paramAnonymousTvInputHardwareInfo).sendToTarget();
      }
      
      public void notifyHdmiDeviceAdded(HdmiDeviceInfo paramAnonymousHdmiDeviceInfo)
      {
        TvInputService.-get1(TvInputService.this).obtainMessage(6, paramAnonymousHdmiDeviceInfo).sendToTarget();
      }
      
      public void notifyHdmiDeviceRemoved(HdmiDeviceInfo paramAnonymousHdmiDeviceInfo)
      {
        TvInputService.-get1(TvInputService.this).obtainMessage(7, paramAnonymousHdmiDeviceInfo).sendToTarget();
      }
      
      public void registerCallback(ITvInputServiceCallback paramAnonymousITvInputServiceCallback)
      {
        if (paramAnonymousITvInputServiceCallback != null) {
          TvInputService.-get0(TvInputService.this).register(paramAnonymousITvInputServiceCallback);
        }
      }
      
      public void unregisterCallback(ITvInputServiceCallback paramAnonymousITvInputServiceCallback)
      {
        if (paramAnonymousITvInputServiceCallback != null) {
          TvInputService.-get0(TvInputService.this).unregister(paramAnonymousITvInputServiceCallback);
        }
      }
    };
  }
  
  public RecordingSession onCreateRecordingSession(String paramString)
  {
    return null;
  }
  
  public abstract Session onCreateSession(String paramString);
  
  public TvInputInfo onHardwareAdded(TvInputHardwareInfo paramTvInputHardwareInfo)
  {
    return null;
  }
  
  public String onHardwareRemoved(TvInputHardwareInfo paramTvInputHardwareInfo)
  {
    return null;
  }
  
  public TvInputInfo onHdmiDeviceAdded(HdmiDeviceInfo paramHdmiDeviceInfo)
  {
    return null;
  }
  
  public String onHdmiDeviceRemoved(HdmiDeviceInfo paramHdmiDeviceInfo)
  {
    return null;
  }
  
  public static abstract class HardwareSession
    extends TvInputService.Session
  {
    private TvInputManager.Session mHardwareSession;
    private final TvInputManager.SessionCallback mHardwareSessionCallback = new TvInputManager.SessionCallback()
    {
      public void onSessionCreated(TvInputManager.Session paramAnonymousSession)
      {
        TvInputService.HardwareSession.-set0(TvInputService.HardwareSession.this, paramAnonymousSession);
        SomeArgs localSomeArgs = SomeArgs.obtain();
        if (paramAnonymousSession != null)
        {
          localSomeArgs.arg1 = TvInputService.HardwareSession.this;
          localSomeArgs.arg2 = TvInputService.HardwareSession.-get2(TvInputService.HardwareSession.this);
          localSomeArgs.arg3 = TvInputService.HardwareSession.-get3(TvInputService.HardwareSession.this);
          localSomeArgs.arg4 = paramAnonymousSession.getToken();
          paramAnonymousSession.tune(TvContract.buildChannelUriForPassthroughInput(TvInputService.HardwareSession.this.getHardwareInputId()));
        }
        for (;;)
        {
          TvInputService.HardwareSession.-get4(TvInputService.HardwareSession.this).obtainMessage(2, localSomeArgs).sendToTarget();
          return;
          localSomeArgs.arg1 = null;
          localSomeArgs.arg2 = null;
          localSomeArgs.arg3 = TvInputService.HardwareSession.-get3(TvInputService.HardwareSession.this);
          localSomeArgs.arg4 = null;
          TvInputService.HardwareSession.this.onRelease();
        }
      }
      
      public void onVideoAvailable(TvInputManager.Session paramAnonymousSession)
      {
        if (TvInputService.HardwareSession.-get0(TvInputService.HardwareSession.this) == paramAnonymousSession) {
          TvInputService.HardwareSession.this.onHardwareVideoAvailable();
        }
      }
      
      public void onVideoUnavailable(TvInputManager.Session paramAnonymousSession, int paramAnonymousInt)
      {
        if (TvInputService.HardwareSession.-get0(TvInputService.HardwareSession.this) == paramAnonymousSession) {
          TvInputService.HardwareSession.this.onHardwareVideoUnavailable(paramAnonymousInt);
        }
      }
    };
    private ITvInputSession mProxySession;
    private ITvInputSessionCallback mProxySessionCallback;
    private Handler mServiceHandler;
    
    public HardwareSession(Context paramContext)
    {
      super();
    }
    
    public abstract String getHardwareInputId();
    
    public void onHardwareVideoAvailable() {}
    
    public void onHardwareVideoUnavailable(int paramInt) {}
    
    public final boolean onSetSurface(Surface paramSurface)
    {
      Log.e("TvInputService", "onSetSurface() should not be called in HardwareProxySession.");
      return false;
    }
    
    void release()
    {
      if (this.mHardwareSession != null)
      {
        this.mHardwareSession.release();
        this.mHardwareSession = null;
      }
      super.release();
    }
  }
  
  private static final class OverlayViewCleanUpTask
    extends AsyncTask<View, Void, Void>
  {
    protected Void doInBackground(View... paramVarArgs)
    {
      paramVarArgs = paramVarArgs[0];
      try
      {
        Thread.sleep(5000L);
        if (isCancelled()) {
          return null;
        }
      }
      catch (InterruptedException paramVarArgs)
      {
        return null;
      }
      if (paramVarArgs.isAttachedToWindow())
      {
        Log.e("TvInputService", "Time out on releasing overlay view. Killing " + paramVarArgs.getContext().getPackageName());
        Process.killProcess(Process.myPid());
      }
      return null;
    }
  }
  
  public static abstract class RecordingSession
  {
    final Handler mHandler;
    private final Object mLock = new Object();
    private final List<Runnable> mPendingActions = new ArrayList();
    private ITvInputSessionCallback mSessionCallback;
    
    public RecordingSession(Context paramContext)
    {
      this.mHandler = new Handler(paramContext.getMainLooper());
    }
    
    private void executeOrPostRunnableOnMainThread(Runnable paramRunnable)
    {
      for (;;)
      {
        synchronized (this.mLock)
        {
          if (this.mSessionCallback == null)
          {
            this.mPendingActions.add(paramRunnable);
            return;
          }
          if (this.mHandler.getLooper().isCurrentThread()) {
            paramRunnable.run();
          }
        }
        this.mHandler.post(paramRunnable);
      }
    }
    
    private void initialize(ITvInputSessionCallback paramITvInputSessionCallback)
    {
      synchronized (this.mLock)
      {
        this.mSessionCallback = paramITvInputSessionCallback;
        paramITvInputSessionCallback = this.mPendingActions.iterator();
        if (paramITvInputSessionCallback.hasNext()) {
          ((Runnable)paramITvInputSessionCallback.next()).run();
        }
      }
      this.mPendingActions.clear();
    }
    
    void appPrivateCommand(String paramString, Bundle paramBundle)
    {
      onAppPrivateCommand(paramString, paramBundle);
    }
    
    public void notifyError(int paramInt)
    {
      final int i;
      if (paramInt >= 0)
      {
        i = paramInt;
        if (paramInt <= 2) {}
      }
      else
      {
        Log.w("TvInputService", "notifyError - invalid error code (" + paramInt + ") is changed to RECORDING_ERROR_UNKNOWN.");
        i = 0;
      }
      executeOrPostRunnableOnMainThread(new Runnable()
      {
        public void run()
        {
          try
          {
            if (TvInputService.RecordingSession.-get0(TvInputService.RecordingSession.this) != null) {
              TvInputService.RecordingSession.-get0(TvInputService.RecordingSession.this).onError(i);
            }
            return;
          }
          catch (RemoteException localRemoteException)
          {
            Log.w("TvInputService", "error in notifyError", localRemoteException);
          }
        }
      });
    }
    
    public void notifyRecordingStopped(final Uri paramUri)
    {
      executeOrPostRunnableOnMainThread(new Runnable()
      {
        public void run()
        {
          try
          {
            if (TvInputService.RecordingSession.-get0(TvInputService.RecordingSession.this) != null) {
              TvInputService.RecordingSession.-get0(TvInputService.RecordingSession.this).onRecordingStopped(paramUri);
            }
            return;
          }
          catch (RemoteException localRemoteException)
          {
            Log.w("TvInputService", "error in notifyRecordingStopped", localRemoteException);
          }
        }
      });
    }
    
    public void notifySessionEvent(final String paramString, final Bundle paramBundle)
    {
      Preconditions.checkNotNull(paramString);
      executeOrPostRunnableOnMainThread(new Runnable()
      {
        public void run()
        {
          try
          {
            if (TvInputService.RecordingSession.-get0(TvInputService.RecordingSession.this) != null) {
              TvInputService.RecordingSession.-get0(TvInputService.RecordingSession.this).onSessionEvent(paramString, paramBundle);
            }
            return;
          }
          catch (RemoteException localRemoteException)
          {
            Log.w("TvInputService", "error in sending event (event=" + paramString + ")", localRemoteException);
          }
        }
      });
    }
    
    public void notifyTuned(final Uri paramUri)
    {
      executeOrPostRunnableOnMainThread(new Runnable()
      {
        public void run()
        {
          try
          {
            if (TvInputService.RecordingSession.-get0(TvInputService.RecordingSession.this) != null) {
              TvInputService.RecordingSession.-get0(TvInputService.RecordingSession.this).onTuned(paramUri);
            }
            return;
          }
          catch (RemoteException localRemoteException)
          {
            Log.w("TvInputService", "error in notifyTuned", localRemoteException);
          }
        }
      });
    }
    
    public void onAppPrivateCommand(String paramString, Bundle paramBundle) {}
    
    public abstract void onRelease();
    
    public abstract void onStartRecording(Uri paramUri);
    
    public abstract void onStopRecording();
    
    public abstract void onTune(Uri paramUri);
    
    public void onTune(Uri paramUri, Bundle paramBundle)
    {
      onTune(paramUri);
    }
    
    void release()
    {
      onRelease();
    }
    
    void startRecording(Uri paramUri)
    {
      onStartRecording(paramUri);
    }
    
    void stopRecording()
    {
      onStopRecording();
    }
    
    void tune(Uri paramUri, Bundle paramBundle)
    {
      onTune(paramUri, paramBundle);
    }
  }
  
  @SuppressLint({"HandlerLeak"})
  private final class ServiceHandler
    extends Handler
  {
    private static final int DO_ADD_HARDWARE_INPUT = 4;
    private static final int DO_ADD_HDMI_INPUT = 6;
    private static final int DO_CREATE_RECORDING_SESSION = 3;
    private static final int DO_CREATE_SESSION = 1;
    private static final int DO_NOTIFY_SESSION_CREATED = 2;
    private static final int DO_REMOVE_HARDWARE_INPUT = 5;
    private static final int DO_REMOVE_HDMI_INPUT = 7;
    
    private ServiceHandler() {}
    
    private void broadcastAddHardwareInput(int paramInt, TvInputInfo paramTvInputInfo)
    {
      int j = TvInputService.-get0(TvInputService.this).beginBroadcast();
      int i = 0;
      for (;;)
      {
        if (i < j) {
          try
          {
            ((ITvInputServiceCallback)TvInputService.-get0(TvInputService.this).getBroadcastItem(i)).addHardwareInput(paramInt, paramTvInputInfo);
            i += 1;
          }
          catch (RemoteException localRemoteException)
          {
            for (;;)
            {
              Log.e("TvInputService", "error in broadcastAddHardwareInput", localRemoteException);
            }
          }
        }
      }
      TvInputService.-get0(TvInputService.this).finishBroadcast();
    }
    
    private void broadcastAddHdmiInput(int paramInt, TvInputInfo paramTvInputInfo)
    {
      int j = TvInputService.-get0(TvInputService.this).beginBroadcast();
      int i = 0;
      for (;;)
      {
        if (i < j) {
          try
          {
            ((ITvInputServiceCallback)TvInputService.-get0(TvInputService.this).getBroadcastItem(i)).addHdmiInput(paramInt, paramTvInputInfo);
            i += 1;
          }
          catch (RemoteException localRemoteException)
          {
            for (;;)
            {
              Log.e("TvInputService", "error in broadcastAddHdmiInput", localRemoteException);
            }
          }
        }
      }
      TvInputService.-get0(TvInputService.this).finishBroadcast();
    }
    
    private void broadcastRemoveHardwareInput(String paramString)
    {
      int j = TvInputService.-get0(TvInputService.this).beginBroadcast();
      int i = 0;
      for (;;)
      {
        if (i < j) {
          try
          {
            ((ITvInputServiceCallback)TvInputService.-get0(TvInputService.this).getBroadcastItem(i)).removeHardwareInput(paramString);
            i += 1;
          }
          catch (RemoteException localRemoteException)
          {
            for (;;)
            {
              Log.e("TvInputService", "error in broadcastRemoveHardwareInput", localRemoteException);
            }
          }
        }
      }
      TvInputService.-get0(TvInputService.this).finishBroadcast();
    }
    
    public final void handleMessage(Message paramMessage)
    {
      Object localObject1;
      Object localObject2;
      Object localObject3;
      Object localObject4;
      switch (paramMessage.what)
      {
      default: 
        Log.w("TvInputService", "Unhandled message code: " + paramMessage.what);
        return;
      case 1: 
        localObject1 = (SomeArgs)paramMessage.obj;
        localObject2 = (InputChannel)((SomeArgs)localObject1).arg1;
        paramMessage = (ITvInputSessionCallback)((SomeArgs)localObject1).arg2;
        localObject3 = (String)((SomeArgs)localObject1).arg3;
        ((SomeArgs)localObject1).recycle();
        localObject1 = TvInputService.this.onCreateSession((String)localObject3);
        if (localObject1 == null) {
          try
          {
            paramMessage.onSessionCreated(null, null);
            return;
          }
          catch (RemoteException paramMessage)
          {
            Log.e("TvInputService", "error in onSessionCreated", paramMessage);
            return;
          }
        }
        localObject2 = new ITvInputSessionWrapper(TvInputService.this, (TvInputService.Session)localObject1, (InputChannel)localObject2);
        if ((localObject1 instanceof TvInputService.HardwareSession))
        {
          localObject3 = (TvInputService.HardwareSession)localObject1;
          localObject4 = ((TvInputService.HardwareSession)localObject3).getHardwareInputId();
          if ((!TextUtils.isEmpty((CharSequence)localObject4)) && (TvInputService.-wrap0(TvInputService.this, (String)localObject4)))
          {
            TvInputService.HardwareSession.-set1((TvInputService.HardwareSession)localObject3, (ITvInputSession)localObject2);
            TvInputService.HardwareSession.-set2((TvInputService.HardwareSession)localObject3, paramMessage);
            TvInputService.HardwareSession.-set3((TvInputService.HardwareSession)localObject3, TvInputService.-get1(TvInputService.this));
            ((TvInputManager)TvInputService.this.getSystemService("tv_input")).createSession((String)localObject4, TvInputService.HardwareSession.-get1((TvInputService.HardwareSession)localObject3), TvInputService.-get1(TvInputService.this));
            return;
          }
          if (TextUtils.isEmpty((CharSequence)localObject4)) {
            Log.w("TvInputService", "Hardware input id is not setup yet.");
          }
          for (;;)
          {
            ((TvInputService.Session)localObject1).onRelease();
            try
            {
              paramMessage.onSessionCreated(null, null);
              return;
            }
            catch (RemoteException paramMessage)
            {
              Log.e("TvInputService", "error in onSessionCreated", paramMessage);
              return;
            }
            Log.w("TvInputService", "Invalid hardware input id : " + (String)localObject4);
          }
        }
        localObject3 = SomeArgs.obtain();
        ((SomeArgs)localObject3).arg1 = localObject1;
        ((SomeArgs)localObject3).arg2 = localObject2;
        ((SomeArgs)localObject3).arg3 = paramMessage;
        ((SomeArgs)localObject3).arg4 = null;
        TvInputService.-get1(TvInputService.this).obtainMessage(2, localObject3).sendToTarget();
        return;
      case 2: 
        paramMessage = (SomeArgs)paramMessage.obj;
        localObject1 = (TvInputService.Session)paramMessage.arg1;
        localObject3 = (ITvInputSession)paramMessage.arg2;
        localObject2 = (ITvInputSessionCallback)paramMessage.arg3;
        localObject4 = (IBinder)paramMessage.arg4;
        try
        {
          ((ITvInputSessionCallback)localObject2).onSessionCreated((ITvInputSession)localObject3, (IBinder)localObject4);
          if (localObject1 != null) {
            TvInputService.Session.-wrap0((TvInputService.Session)localObject1, (ITvInputSessionCallback)localObject2);
          }
          paramMessage.recycle();
          return;
        }
        catch (RemoteException localRemoteException2)
        {
          for (;;)
          {
            Log.e("TvInputService", "error in onSessionCreated", localRemoteException2);
          }
        }
      case 3: 
        localObject1 = (SomeArgs)paramMessage.obj;
        paramMessage = (ITvInputSessionCallback)((SomeArgs)localObject1).arg1;
        localObject2 = (String)((SomeArgs)localObject1).arg2;
        ((SomeArgs)localObject1).recycle();
        localObject1 = TvInputService.this.onCreateRecordingSession((String)localObject2);
        if (localObject1 == null) {
          try
          {
            paramMessage.onSessionCreated(null, null);
            return;
          }
          catch (RemoteException paramMessage)
          {
            Log.e("TvInputService", "error in onSessionCreated", paramMessage);
            return;
          }
        }
        localObject2 = new ITvInputSessionWrapper(TvInputService.this, (TvInputService.RecordingSession)localObject1);
        try
        {
          paramMessage.onSessionCreated((ITvInputSession)localObject2, null);
          TvInputService.RecordingSession.-wrap0((TvInputService.RecordingSession)localObject1, paramMessage);
          return;
        }
        catch (RemoteException localRemoteException1)
        {
          for (;;)
          {
            Log.e("TvInputService", "error in onSessionCreated", localRemoteException1);
          }
        }
      case 4: 
        paramMessage = (TvInputHardwareInfo)paramMessage.obj;
        localObject1 = TvInputService.this.onHardwareAdded(paramMessage);
        if (localObject1 != null) {
          broadcastAddHardwareInput(paramMessage.getDeviceId(), (TvInputInfo)localObject1);
        }
        return;
      case 5: 
        paramMessage = (TvInputHardwareInfo)paramMessage.obj;
        paramMessage = TvInputService.this.onHardwareRemoved(paramMessage);
        if (paramMessage != null) {
          broadcastRemoveHardwareInput(paramMessage);
        }
        return;
      case 6: 
        paramMessage = (HdmiDeviceInfo)paramMessage.obj;
        localObject1 = TvInputService.this.onHdmiDeviceAdded(paramMessage);
        if (localObject1 != null) {
          broadcastAddHdmiInput(paramMessage.getId(), (TvInputInfo)localObject1);
        }
        return;
      }
      paramMessage = (HdmiDeviceInfo)paramMessage.obj;
      paramMessage = TvInputService.this.onHdmiDeviceRemoved(paramMessage);
      if (paramMessage != null) {
        broadcastRemoveHardwareInput(paramMessage);
      }
    }
  }
  
  public static abstract class Session
    implements KeyEvent.Callback
  {
    private static final int POSITION_UPDATE_INTERVAL_MS = 1000;
    private final Context mContext;
    private long mCurrentPositionMs;
    private final KeyEvent.DispatcherState mDispatcherState = new KeyEvent.DispatcherState();
    final Handler mHandler;
    private final Object mLock = new Object();
    private Rect mOverlayFrame;
    private View mOverlayView;
    private TvInputService.OverlayViewCleanUpTask mOverlayViewCleanUpTask;
    private FrameLayout mOverlayViewContainer;
    private boolean mOverlayViewEnabled;
    private final List<Runnable> mPendingActions = new ArrayList();
    private ITvInputSessionCallback mSessionCallback;
    private long mStartPositionMs;
    private Surface mSurface;
    private final TimeShiftPositionTrackingRunnable mTimeShiftPositionTrackingRunnable = new TimeShiftPositionTrackingRunnable(null);
    private final WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowParams;
    private IBinder mWindowToken;
    
    public Session(Context paramContext)
    {
      this.mContext = paramContext;
      this.mWindowManager = ((WindowManager)paramContext.getSystemService("window"));
      this.mHandler = new Handler(paramContext.getMainLooper());
      this.mCurrentPositionMs = Long.MIN_VALUE;
    }
    
    private void executeOrPostRunnableOnMainThread(Runnable paramRunnable)
    {
      for (;;)
      {
        synchronized (this.mLock)
        {
          if (this.mSessionCallback == null)
          {
            this.mPendingActions.add(paramRunnable);
            return;
          }
          if (this.mHandler.getLooper().isCurrentThread()) {
            paramRunnable.run();
          }
        }
        this.mHandler.post(paramRunnable);
      }
    }
    
    private void initialize(ITvInputSessionCallback paramITvInputSessionCallback)
    {
      synchronized (this.mLock)
      {
        this.mSessionCallback = paramITvInputSessionCallback;
        paramITvInputSessionCallback = this.mPendingActions.iterator();
        if (paramITvInputSessionCallback.hasNext()) {
          ((Runnable)paramITvInputSessionCallback.next()).run();
        }
      }
      this.mPendingActions.clear();
    }
    
    private void notifyTimeShiftCurrentPositionChanged(final long paramLong)
    {
      executeOrPostRunnableOnMainThread(new Runnable()
      {
        public void run()
        {
          try
          {
            if (TvInputService.Session.-get3(TvInputService.Session.this) != null) {
              TvInputService.Session.-get3(TvInputService.Session.this).onTimeShiftCurrentPositionChanged(paramLong);
            }
            return;
          }
          catch (RemoteException localRemoteException)
          {
            Log.w("TvInputService", "error in notifyTimeShiftCurrentPositionChanged", localRemoteException);
          }
        }
      });
    }
    
    private void notifyTimeShiftStartPositionChanged(final long paramLong)
    {
      executeOrPostRunnableOnMainThread(new Runnable()
      {
        public void run()
        {
          try
          {
            if (TvInputService.Session.-get3(TvInputService.Session.this) != null) {
              TvInputService.Session.-get3(TvInputService.Session.this).onTimeShiftStartPositionChanged(paramLong);
            }
            return;
          }
          catch (RemoteException localRemoteException)
          {
            Log.w("TvInputService", "error in notifyTimeShiftStartPositionChanged", localRemoteException);
          }
        }
      });
    }
    
    void appPrivateCommand(String paramString, Bundle paramBundle)
    {
      onAppPrivateCommand(paramString, paramBundle);
    }
    
    void createOverlayView(IBinder paramIBinder, Rect paramRect)
    {
      if (this.mOverlayViewContainer != null) {
        removeOverlayView(false);
      }
      this.mWindowToken = paramIBinder;
      this.mOverlayFrame = paramRect;
      onOverlayViewSizeChanged(paramRect.right - paramRect.left, paramRect.bottom - paramRect.top);
      if (!this.mOverlayViewEnabled) {
        return;
      }
      this.mOverlayView = onCreateOverlayView();
      if (this.mOverlayView == null) {
        return;
      }
      if (this.mOverlayViewCleanUpTask != null)
      {
        this.mOverlayViewCleanUpTask.cancel(true);
        this.mOverlayViewCleanUpTask = null;
      }
      this.mOverlayViewContainer = new FrameLayout(this.mContext.getApplicationContext());
      this.mOverlayViewContainer.addView(this.mOverlayView);
      int i = 536;
      if (ActivityManager.isHighEndGfx()) {
        i = 16777752;
      }
      this.mWindowParams = new WindowManager.LayoutParams(paramRect.right - paramRect.left, paramRect.bottom - paramRect.top, paramRect.left, paramRect.top, 1004, i, -2);
      paramRect = this.mWindowParams;
      paramRect.privateFlags |= 0x40;
      this.mWindowParams.gravity = 8388659;
      this.mWindowParams.token = paramIBinder;
      this.mWindowManager.addView(this.mOverlayViewContainer, this.mWindowParams);
    }
    
    int dispatchInputEvent(InputEvent paramInputEvent, InputEventReceiver paramInputEventReceiver)
    {
      boolean bool2 = false;
      int j = 0;
      boolean bool1;
      int i;
      if ((paramInputEvent instanceof KeyEvent))
      {
        if (paramInputEvent.dispatch(this, this.mDispatcherState, this)) {
          return 1;
        }
        bool1 = TvInputService.isNavigationKey(paramInputEvent.getKeyCode());
        if (!KeyEvent.isMediaKey(paramInputEvent.getKeyCode()))
        {
          if (paramInputEvent.getKeyCode() != 222) {
            break label87;
          }
          i = 1;
        }
      }
      while ((this.mOverlayViewContainer == null) || (!this.mOverlayViewContainer.isAttachedToWindow()) || (i != 0))
      {
        return 0;
        i = 1;
        continue;
        label87:
        i = 0;
        continue;
        bool1 = bool2;
        i = j;
        if ((paramInputEvent instanceof MotionEvent))
        {
          i = paramInputEvent.getSource();
          if (paramInputEvent.isTouchEvent())
          {
            bool1 = bool2;
            i = j;
            if (onTouchEvent(paramInputEvent)) {
              return 1;
            }
          }
          else if ((i & 0x4) != 0)
          {
            bool1 = bool2;
            i = j;
            if (onTrackballEvent(paramInputEvent)) {
              return 1;
            }
          }
          else
          {
            bool1 = bool2;
            i = j;
            if (onGenericMotionEvent(paramInputEvent)) {
              return 1;
            }
          }
        }
      }
      if (!this.mOverlayViewContainer.hasWindowFocus()) {
        this.mOverlayViewContainer.getViewRootImpl().windowFocusChanged(true, true);
      }
      if ((bool1) && (this.mOverlayViewContainer.hasFocusable()))
      {
        this.mOverlayViewContainer.getViewRootImpl().dispatchInputEvent(paramInputEvent);
        return 1;
      }
      this.mOverlayViewContainer.getViewRootImpl().dispatchInputEvent(paramInputEvent, paramInputEventReceiver);
      return -1;
    }
    
    void dispatchSurfaceChanged(int paramInt1, int paramInt2, int paramInt3)
    {
      onSurfaceChanged(paramInt1, paramInt2, paramInt3);
    }
    
    public void layoutSurface(final int paramInt1, final int paramInt2, final int paramInt3, final int paramInt4)
    {
      if ((paramInt1 > paramInt3) || (paramInt2 > paramInt4)) {
        throw new IllegalArgumentException("Invalid parameter");
      }
      executeOrPostRunnableOnMainThread(new Runnable()
      {
        public void run()
        {
          try
          {
            if (TvInputService.Session.-get3(TvInputService.Session.this) != null) {
              TvInputService.Session.-get3(TvInputService.Session.this).onLayoutSurface(paramInt1, paramInt2, paramInt3, paramInt4);
            }
            return;
          }
          catch (RemoteException localRemoteException)
          {
            Log.w("TvInputService", "error in layoutSurface", localRemoteException);
          }
        }
      });
    }
    
    public void notifyChannelRetuned(final Uri paramUri)
    {
      executeOrPostRunnableOnMainThread(new Runnable()
      {
        public void run()
        {
          try
          {
            if (TvInputService.Session.-get3(TvInputService.Session.this) != null) {
              TvInputService.Session.-get3(TvInputService.Session.this).onChannelRetuned(paramUri);
            }
            return;
          }
          catch (RemoteException localRemoteException)
          {
            Log.w("TvInputService", "error in notifyChannelRetuned", localRemoteException);
          }
        }
      });
    }
    
    public void notifyContentAllowed()
    {
      executeOrPostRunnableOnMainThread(new Runnable()
      {
        public void run()
        {
          try
          {
            if (TvInputService.Session.-get3(TvInputService.Session.this) != null) {
              TvInputService.Session.-get3(TvInputService.Session.this).onContentAllowed();
            }
            return;
          }
          catch (RemoteException localRemoteException)
          {
            Log.w("TvInputService", "error in notifyContentAllowed", localRemoteException);
          }
        }
      });
    }
    
    public void notifyContentBlocked(final TvContentRating paramTvContentRating)
    {
      Preconditions.checkNotNull(paramTvContentRating);
      executeOrPostRunnableOnMainThread(new Runnable()
      {
        public void run()
        {
          try
          {
            if (TvInputService.Session.-get3(TvInputService.Session.this) != null) {
              TvInputService.Session.-get3(TvInputService.Session.this).onContentBlocked(paramTvContentRating.flattenToString());
            }
            return;
          }
          catch (RemoteException localRemoteException)
          {
            Log.w("TvInputService", "error in notifyContentBlocked", localRemoteException);
          }
        }
      });
    }
    
    public void notifySessionEvent(final String paramString, final Bundle paramBundle)
    {
      Preconditions.checkNotNull(paramString);
      executeOrPostRunnableOnMainThread(new Runnable()
      {
        public void run()
        {
          try
          {
            if (TvInputService.Session.-get3(TvInputService.Session.this) != null) {
              TvInputService.Session.-get3(TvInputService.Session.this).onSessionEvent(paramString, paramBundle);
            }
            return;
          }
          catch (RemoteException localRemoteException)
          {
            Log.w("TvInputService", "error in sending event (event=" + paramString + ")", localRemoteException);
          }
        }
      });
    }
    
    public void notifyTimeShiftStatusChanged(final int paramInt)
    {
      executeOrPostRunnableOnMainThread(new Runnable()
      {
        public void run()
        {
          try
          {
            if (TvInputService.Session.-get3(TvInputService.Session.this) != null) {
              TvInputService.Session.-get3(TvInputService.Session.this).onTimeShiftStatusChanged(paramInt);
            }
            return;
          }
          catch (RemoteException localRemoteException)
          {
            Log.w("TvInputService", "error in notifyTimeShiftStatusChanged", localRemoteException);
          }
        }
      });
    }
    
    public void notifyTrackSelected(final int paramInt, final String paramString)
    {
      executeOrPostRunnableOnMainThread(new Runnable()
      {
        public void run()
        {
          try
          {
            if (TvInputService.Session.-get3(TvInputService.Session.this) != null) {
              TvInputService.Session.-get3(TvInputService.Session.this).onTrackSelected(paramInt, paramString);
            }
            return;
          }
          catch (RemoteException localRemoteException)
          {
            Log.w("TvInputService", "error in notifyTrackSelected", localRemoteException);
          }
        }
      });
    }
    
    public void notifyTracksChanged(List<TvTrackInfo> paramList)
    {
      executeOrPostRunnableOnMainThread(new Runnable()
      {
        public void run()
        {
          try
          {
            if (TvInputService.Session.-get3(TvInputService.Session.this) != null) {
              TvInputService.Session.-get3(TvInputService.Session.this).onTracksChanged(this.val$tracksCopy);
            }
            return;
          }
          catch (RemoteException localRemoteException)
          {
            Log.w("TvInputService", "error in notifyTracksChanged", localRemoteException);
          }
        }
      });
    }
    
    public void notifyVideoAvailable()
    {
      executeOrPostRunnableOnMainThread(new Runnable()
      {
        public void run()
        {
          try
          {
            if (TvInputService.Session.-get3(TvInputService.Session.this) != null) {
              TvInputService.Session.-get3(TvInputService.Session.this).onVideoAvailable();
            }
            return;
          }
          catch (RemoteException localRemoteException)
          {
            Log.w("TvInputService", "error in notifyVideoAvailable", localRemoteException);
          }
        }
      });
    }
    
    public void notifyVideoUnavailable(final int paramInt)
    {
      if ((paramInt < 0) || (paramInt > 4)) {
        Log.e("TvInputService", "notifyVideoUnavailable - unknown reason: " + paramInt);
      }
      executeOrPostRunnableOnMainThread(new Runnable()
      {
        public void run()
        {
          try
          {
            if (TvInputService.Session.-get3(TvInputService.Session.this) != null) {
              TvInputService.Session.-get3(TvInputService.Session.this).onVideoUnavailable(paramInt);
            }
            return;
          }
          catch (RemoteException localRemoteException)
          {
            Log.w("TvInputService", "error in notifyVideoUnavailable", localRemoteException);
          }
        }
      });
    }
    
    public void onAppPrivateCommand(String paramString, Bundle paramBundle) {}
    
    public View onCreateOverlayView()
    {
      return null;
    }
    
    public boolean onGenericMotionEvent(MotionEvent paramMotionEvent)
    {
      return false;
    }
    
    public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
    {
      return false;
    }
    
    public boolean onKeyLongPress(int paramInt, KeyEvent paramKeyEvent)
    {
      return false;
    }
    
    public boolean onKeyMultiple(int paramInt1, int paramInt2, KeyEvent paramKeyEvent)
    {
      return false;
    }
    
    public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
    {
      return false;
    }
    
    public void onOverlayViewSizeChanged(int paramInt1, int paramInt2) {}
    
    public abstract void onRelease();
    
    public boolean onSelectTrack(int paramInt, String paramString)
    {
      return false;
    }
    
    public abstract void onSetCaptionEnabled(boolean paramBoolean);
    
    public void onSetMain(boolean paramBoolean) {}
    
    public abstract void onSetStreamVolume(float paramFloat);
    
    public abstract boolean onSetSurface(Surface paramSurface);
    
    public void onSurfaceChanged(int paramInt1, int paramInt2, int paramInt3) {}
    
    public long onTimeShiftGetCurrentPosition()
    {
      return Long.MIN_VALUE;
    }
    
    public long onTimeShiftGetStartPosition()
    {
      return Long.MIN_VALUE;
    }
    
    public void onTimeShiftPause() {}
    
    public void onTimeShiftPlay(Uri paramUri) {}
    
    public void onTimeShiftResume() {}
    
    public void onTimeShiftSeekTo(long paramLong) {}
    
    public void onTimeShiftSetPlaybackParams(PlaybackParams paramPlaybackParams) {}
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      return false;
    }
    
    public boolean onTrackballEvent(MotionEvent paramMotionEvent)
    {
      return false;
    }
    
    public abstract boolean onTune(Uri paramUri);
    
    public boolean onTune(Uri paramUri, Bundle paramBundle)
    {
      return onTune(paramUri);
    }
    
    public void onUnblockContent(TvContentRating paramTvContentRating) {}
    
    void relayoutOverlayView(Rect paramRect)
    {
      if ((this.mOverlayFrame == null) || (this.mOverlayFrame.width() != paramRect.width())) {}
      for (;;)
      {
        onOverlayViewSizeChanged(paramRect.right - paramRect.left, paramRect.bottom - paramRect.top);
        do
        {
          this.mOverlayFrame = paramRect;
          if ((this.mOverlayViewEnabled) && (this.mOverlayViewContainer != null)) {
            break;
          }
          return;
        } while (this.mOverlayFrame.height() == paramRect.height());
      }
      this.mWindowParams.x = paramRect.left;
      this.mWindowParams.y = paramRect.top;
      this.mWindowParams.width = (paramRect.right - paramRect.left);
      this.mWindowParams.height = (paramRect.bottom - paramRect.top);
      this.mWindowManager.updateViewLayout(this.mOverlayViewContainer, this.mWindowParams);
    }
    
    void release()
    {
      onRelease();
      if (this.mSurface != null)
      {
        this.mSurface.release();
        this.mSurface = null;
      }
      synchronized (this.mLock)
      {
        this.mSessionCallback = null;
        this.mPendingActions.clear();
        removeOverlayView(true);
        this.mHandler.removeCallbacks(this.mTimeShiftPositionTrackingRunnable);
        return;
      }
    }
    
    void removeOverlayView(boolean paramBoolean)
    {
      if (paramBoolean)
      {
        this.mWindowToken = null;
        this.mOverlayFrame = null;
      }
      if (this.mOverlayViewContainer != null)
      {
        this.mOverlayViewContainer.removeView(this.mOverlayView);
        this.mOverlayView = null;
        this.mWindowManager.removeView(this.mOverlayViewContainer);
        this.mOverlayViewContainer = null;
        this.mWindowParams = null;
      }
    }
    
    void scheduleOverlayViewCleanup()
    {
      FrameLayout localFrameLayout = this.mOverlayViewContainer;
      if (localFrameLayout != null)
      {
        this.mOverlayViewCleanUpTask = new TvInputService.OverlayViewCleanUpTask(null);
        this.mOverlayViewCleanUpTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new View[] { localFrameLayout });
      }
    }
    
    void selectTrack(int paramInt, String paramString)
    {
      onSelectTrack(paramInt, paramString);
    }
    
    void setCaptionEnabled(boolean paramBoolean)
    {
      onSetCaptionEnabled(paramBoolean);
    }
    
    void setMain(boolean paramBoolean)
    {
      onSetMain(paramBoolean);
    }
    
    public void setOverlayViewEnabled(final boolean paramBoolean)
    {
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          if (paramBoolean == TvInputService.Session.-get2(TvInputService.Session.this)) {
            return;
          }
          TvInputService.Session.-set1(TvInputService.Session.this, paramBoolean);
          if (paramBoolean)
          {
            if (TvInputService.Session.-get6(TvInputService.Session.this) != null) {
              TvInputService.Session.this.createOverlayView(TvInputService.Session.-get6(TvInputService.Session.this), TvInputService.Session.-get1(TvInputService.Session.this));
            }
            return;
          }
          TvInputService.Session.this.removeOverlayView(false);
        }
      });
    }
    
    void setStreamVolume(float paramFloat)
    {
      onSetStreamVolume(paramFloat);
    }
    
    void setSurface(Surface paramSurface)
    {
      onSetSurface(paramSurface);
      if (this.mSurface != null) {
        this.mSurface.release();
      }
      this.mSurface = paramSurface;
    }
    
    void timeShiftEnablePositionTracking(boolean paramBoolean)
    {
      if (paramBoolean)
      {
        this.mHandler.post(this.mTimeShiftPositionTrackingRunnable);
        return;
      }
      this.mHandler.removeCallbacks(this.mTimeShiftPositionTrackingRunnable);
      this.mStartPositionMs = Long.MIN_VALUE;
      this.mCurrentPositionMs = Long.MIN_VALUE;
    }
    
    void timeShiftPause()
    {
      onTimeShiftPause();
    }
    
    void timeShiftPlay(Uri paramUri)
    {
      this.mCurrentPositionMs = 0L;
      onTimeShiftPlay(paramUri);
    }
    
    void timeShiftResume()
    {
      onTimeShiftResume();
    }
    
    void timeShiftSeekTo(long paramLong)
    {
      onTimeShiftSeekTo(paramLong);
    }
    
    void timeShiftSetPlaybackParams(PlaybackParams paramPlaybackParams)
    {
      onTimeShiftSetPlaybackParams(paramPlaybackParams);
    }
    
    void tune(Uri paramUri, Bundle paramBundle)
    {
      this.mCurrentPositionMs = Long.MIN_VALUE;
      onTune(paramUri, paramBundle);
    }
    
    void unblockContent(String paramString)
    {
      onUnblockContent(TvContentRating.unflattenFromString(paramString));
    }
    
    private final class TimeShiftPositionTrackingRunnable
      implements Runnable
    {
      private TimeShiftPositionTrackingRunnable() {}
      
      public void run()
      {
        long l1 = TvInputService.Session.this.onTimeShiftGetStartPosition();
        if (TvInputService.Session.-get4(TvInputService.Session.this) != l1)
        {
          TvInputService.Session.-set2(TvInputService.Session.this, l1);
          TvInputService.Session.-wrap2(TvInputService.Session.this, l1);
        }
        long l2 = TvInputService.Session.this.onTimeShiftGetCurrentPosition();
        l1 = l2;
        if (l2 < TvInputService.Session.-get4(TvInputService.Session.this))
        {
          Log.w("TvInputService", "Current position (" + l2 + ") cannot be earlier than" + " start position (" + TvInputService.Session.-get4(TvInputService.Session.this) + "). Reset to the start " + "position.");
          l1 = TvInputService.Session.-get4(TvInputService.Session.this);
        }
        if (TvInputService.Session.-get0(TvInputService.Session.this) != l1)
        {
          TvInputService.Session.-set0(TvInputService.Session.this, l1);
          TvInputService.Session.-wrap1(TvInputService.Session.this, l1);
        }
        TvInputService.Session.this.mHandler.removeCallbacks(TvInputService.Session.-get5(TvInputService.Session.this));
        TvInputService.Session.this.mHandler.postDelayed(TvInputService.Session.-get5(TvInputService.Session.this), 1000L);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/tv/TvInputService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */