package android.accessibilityservice;

import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Region;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings.Secure;
import android.provider.Settings.SettingNotFoundException;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.WindowManagerImpl;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityInteractionClient;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;
import com.android.internal.os.HandlerCaller;
import com.android.internal.os.HandlerCaller.Callback;
import com.android.internal.os.SomeArgs;
import java.util.List;

public abstract class AccessibilityService
  extends Service
{
  public static final int GESTURE_SWIPE_DOWN = 2;
  public static final int GESTURE_SWIPE_DOWN_AND_LEFT = 15;
  public static final int GESTURE_SWIPE_DOWN_AND_RIGHT = 16;
  public static final int GESTURE_SWIPE_DOWN_AND_UP = 8;
  public static final int GESTURE_SWIPE_LEFT = 3;
  public static final int GESTURE_SWIPE_LEFT_AND_DOWN = 10;
  public static final int GESTURE_SWIPE_LEFT_AND_RIGHT = 5;
  public static final int GESTURE_SWIPE_LEFT_AND_UP = 9;
  public static final int GESTURE_SWIPE_RIGHT = 4;
  public static final int GESTURE_SWIPE_RIGHT_AND_DOWN = 12;
  public static final int GESTURE_SWIPE_RIGHT_AND_LEFT = 6;
  public static final int GESTURE_SWIPE_RIGHT_AND_UP = 11;
  public static final int GESTURE_SWIPE_UP = 1;
  public static final int GESTURE_SWIPE_UP_AND_DOWN = 7;
  public static final int GESTURE_SWIPE_UP_AND_LEFT = 13;
  public static final int GESTURE_SWIPE_UP_AND_RIGHT = 14;
  public static final int GLOBAL_ACTION_BACK = 1;
  public static final int GLOBAL_ACTION_HOME = 2;
  public static final int GLOBAL_ACTION_NOTIFICATIONS = 4;
  public static final int GLOBAL_ACTION_POWER_DIALOG = 6;
  public static final int GLOBAL_ACTION_QUICK_SETTINGS = 5;
  public static final int GLOBAL_ACTION_RECENTS = 3;
  public static final int GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN = 7;
  private static final String LOG_TAG = "AccessibilityService";
  public static final String SERVICE_INTERFACE = "android.accessibilityservice.AccessibilityService";
  public static final String SERVICE_META_DATA = "android.accessibilityservice";
  public static final int SHOW_MODE_AUTO = 0;
  public static final int SHOW_MODE_HIDDEN = 1;
  private int mConnectionId;
  private SparseArray<GestureResultCallbackInfo> mGestureStatusCallbackInfos;
  private int mGestureStatusCallbackSequence;
  private AccessibilityServiceInfo mInfo;
  private final Object mLock = new Object();
  private MagnificationController mMagnificationController;
  private SoftKeyboardController mSoftKeyboardController;
  private WindowManager mWindowManager;
  private IBinder mWindowToken;
  
  private void dispatchServiceConnected()
  {
    if (this.mMagnificationController != null) {
      this.mMagnificationController.onServiceConnected();
    }
    onServiceConnected();
  }
  
  private void onMagnificationChanged(Region paramRegion, float paramFloat1, float paramFloat2, float paramFloat3)
  {
    if (this.mMagnificationController != null) {
      this.mMagnificationController.dispatchMagnificationChanged(paramRegion, paramFloat1, paramFloat2, paramFloat3);
    }
  }
  
  private void onSoftKeyboardShowModeChanged(int paramInt)
  {
    if (this.mSoftKeyboardController != null) {
      this.mSoftKeyboardController.dispatchSoftKeyboardShowModeChanged(paramInt);
    }
  }
  
  private void sendServiceInfo()
  {
    IAccessibilityServiceConnection localIAccessibilityServiceConnection = AccessibilityInteractionClient.getInstance().getConnection(this.mConnectionId);
    if ((this.mInfo != null) && (localIAccessibilityServiceConnection != null)) {}
    try
    {
      localIAccessibilityServiceConnection.setServiceInfo(this.mInfo);
      this.mInfo = null;
      AccessibilityInteractionClient.getInstance().clearCache();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w("AccessibilityService", "Error while setting AccessibilityServiceInfo", localRemoteException);
      localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public final void disableSelf()
  {
    IAccessibilityServiceConnection localIAccessibilityServiceConnection = AccessibilityInteractionClient.getInstance().getConnection(this.mConnectionId);
    if (localIAccessibilityServiceConnection != null) {}
    try
    {
      localIAccessibilityServiceConnection.disableSelf();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException(localRemoteException);
    }
  }
  
  /* Error */
  public final boolean dispatchGesture(GestureDescription paramGestureDescription, GestureResultCallback paramGestureResultCallback, Handler paramHandler)
  {
    // Byte code:
    //   0: invokestatic 166	android/view/accessibility/AccessibilityInteractionClient:getInstance	()Landroid/view/accessibility/AccessibilityInteractionClient;
    //   3: aload_0
    //   4: getfield 110	android/accessibilityservice/AccessibilityService:mConnectionId	I
    //   7: invokevirtual 170	android/view/accessibility/AccessibilityInteractionClient:getConnection	(I)Landroid/accessibilityservice/IAccessibilityServiceConnection;
    //   10: astore 5
    //   12: aload 5
    //   14: ifnonnull +5 -> 19
    //   17: iconst_0
    //   18: ireturn
    //   19: aload_1
    //   20: bipush 100
    //   22: invokestatic 209	android/accessibilityservice/GestureDescription$MotionEventGenerator:getGestureStepsFromGestureDescription	(Landroid/accessibilityservice/GestureDescription;I)Ljava/util/List;
    //   25: astore 6
    //   27: aload_0
    //   28: getfield 143	android/accessibilityservice/AccessibilityService:mLock	Ljava/lang/Object;
    //   31: astore 4
    //   33: aload 4
    //   35: monitorenter
    //   36: aload_0
    //   37: aload_0
    //   38: getfield 211	android/accessibilityservice/AccessibilityService:mGestureStatusCallbackSequence	I
    //   41: iconst_1
    //   42: iadd
    //   43: putfield 211	android/accessibilityservice/AccessibilityService:mGestureStatusCallbackSequence	I
    //   46: aload_2
    //   47: ifnull +44 -> 91
    //   50: aload_0
    //   51: getfield 213	android/accessibilityservice/AccessibilityService:mGestureStatusCallbackInfos	Landroid/util/SparseArray;
    //   54: ifnonnull +14 -> 68
    //   57: aload_0
    //   58: new 215	android/util/SparseArray
    //   61: dup
    //   62: invokespecial 216	android/util/SparseArray:<init>	()V
    //   65: putfield 213	android/accessibilityservice/AccessibilityService:mGestureStatusCallbackInfos	Landroid/util/SparseArray;
    //   68: new 16	android/accessibilityservice/AccessibilityService$GestureResultCallbackInfo
    //   71: dup
    //   72: aload_1
    //   73: aload_2
    //   74: aload_3
    //   75: invokespecial 219	android/accessibilityservice/AccessibilityService$GestureResultCallbackInfo:<init>	(Landroid/accessibilityservice/GestureDescription;Landroid/accessibilityservice/AccessibilityService$GestureResultCallback;Landroid/os/Handler;)V
    //   78: astore_1
    //   79: aload_0
    //   80: getfield 213	android/accessibilityservice/AccessibilityService:mGestureStatusCallbackInfos	Landroid/util/SparseArray;
    //   83: aload_0
    //   84: getfield 211	android/accessibilityservice/AccessibilityService:mGestureStatusCallbackSequence	I
    //   87: aload_1
    //   88: invokevirtual 223	android/util/SparseArray:put	(ILjava/lang/Object;)V
    //   91: aload 5
    //   93: aload_0
    //   94: getfield 211	android/accessibilityservice/AccessibilityService:mGestureStatusCallbackSequence	I
    //   97: new 225	android/content/pm/ParceledListSlice
    //   100: dup
    //   101: aload 6
    //   103: invokespecial 228	android/content/pm/ParceledListSlice:<init>	(Ljava/util/List;)V
    //   106: invokeinterface 232 3 0
    //   111: aload 4
    //   113: monitorexit
    //   114: iconst_1
    //   115: ireturn
    //   116: astore_1
    //   117: aload 4
    //   119: monitorexit
    //   120: aload_1
    //   121: athrow
    //   122: astore_1
    //   123: new 198	java/lang/RuntimeException
    //   126: dup
    //   127: aload_1
    //   128: invokespecial 201	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
    //   131: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	132	0	this	AccessibilityService
    //   0	132	1	paramGestureDescription	GestureDescription
    //   0	132	2	paramGestureResultCallback	GestureResultCallback
    //   0	132	3	paramHandler	Handler
    //   10	82	5	localIAccessibilityServiceConnection	IAccessibilityServiceConnection
    //   25	77	6	localList	List
    // Exception table:
    //   from	to	target	type
    //   36	46	116	finally
    //   50	68	116	finally
    //   68	91	116	finally
    //   91	111	116	finally
    //   27	36	122	android/os/RemoteException
    //   111	114	122	android/os/RemoteException
    //   117	122	122	android/os/RemoteException
  }
  
  public AccessibilityNodeInfo findFocus(int paramInt)
  {
    return AccessibilityInteractionClient.getInstance().findFocus(this.mConnectionId, -2, AccessibilityNodeInfo.ROOT_NODE_ID, paramInt);
  }
  
  public final MagnificationController getMagnificationController()
  {
    synchronized (this.mLock)
    {
      if (this.mMagnificationController == null) {
        this.mMagnificationController = new MagnificationController(this, this.mLock);
      }
      MagnificationController localMagnificationController = this.mMagnificationController;
      return localMagnificationController;
    }
  }
  
  public AccessibilityNodeInfo getRootInActiveWindow()
  {
    return AccessibilityInteractionClient.getInstance().getRootInActiveWindow(this.mConnectionId);
  }
  
  public final AccessibilityServiceInfo getServiceInfo()
  {
    Object localObject = AccessibilityInteractionClient.getInstance().getConnection(this.mConnectionId);
    if (localObject != null) {
      try
      {
        localObject = ((IAccessibilityServiceConnection)localObject).getServiceInfo();
        return (AccessibilityServiceInfo)localObject;
      }
      catch (RemoteException localRemoteException)
      {
        Log.w("AccessibilityService", "Error while getting AccessibilityServiceInfo", localRemoteException);
        localRemoteException.rethrowFromSystemServer();
      }
    }
    return null;
  }
  
  public final SoftKeyboardController getSoftKeyboardController()
  {
    synchronized (this.mLock)
    {
      if (this.mSoftKeyboardController == null) {
        this.mSoftKeyboardController = new SoftKeyboardController(this, this.mLock);
      }
      SoftKeyboardController localSoftKeyboardController = this.mSoftKeyboardController;
      return localSoftKeyboardController;
    }
  }
  
  public Object getSystemService(String paramString)
  {
    if (getBaseContext() == null) {
      throw new IllegalStateException("System services not available to Activities before onCreate()");
    }
    if ("window".equals(paramString))
    {
      if (this.mWindowManager == null) {
        this.mWindowManager = ((WindowManager)getBaseContext().getSystemService(paramString));
      }
      return this.mWindowManager;
    }
    return super.getSystemService(paramString);
  }
  
  public List<AccessibilityWindowInfo> getWindows()
  {
    return AccessibilityInteractionClient.getInstance().getWindows(this.mConnectionId);
  }
  
  public abstract void onAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent);
  
  public final IBinder onBind(Intent paramIntent)
  {
    new IAccessibilityServiceClientWrapper(this, getMainLooper(), new Callbacks()
    {
      public void init(int paramAnonymousInt, IBinder paramAnonymousIBinder)
      {
        AccessibilityService.-set0(AccessibilityService.this, paramAnonymousInt);
        AccessibilityService.-set1(AccessibilityService.this, paramAnonymousIBinder);
        ((WindowManagerImpl)AccessibilityService.this.getSystemService("window")).setDefaultToken(paramAnonymousIBinder);
      }
      
      public void onAccessibilityEvent(AccessibilityEvent paramAnonymousAccessibilityEvent)
      {
        AccessibilityService.this.onAccessibilityEvent(paramAnonymousAccessibilityEvent);
      }
      
      public boolean onGesture(int paramAnonymousInt)
      {
        return AccessibilityService.this.onGesture(paramAnonymousInt);
      }
      
      public void onInterrupt()
      {
        AccessibilityService.this.onInterrupt();
      }
      
      public boolean onKeyEvent(KeyEvent paramAnonymousKeyEvent)
      {
        return AccessibilityService.this.onKeyEvent(paramAnonymousKeyEvent);
      }
      
      public void onMagnificationChanged(Region paramAnonymousRegion, float paramAnonymousFloat1, float paramAnonymousFloat2, float paramAnonymousFloat3)
      {
        AccessibilityService.-wrap1(AccessibilityService.this, paramAnonymousRegion, paramAnonymousFloat1, paramAnonymousFloat2, paramAnonymousFloat3);
      }
      
      public void onPerformGestureResult(int paramAnonymousInt, boolean paramAnonymousBoolean)
      {
        AccessibilityService.this.onPerformGestureResult(paramAnonymousInt, paramAnonymousBoolean);
      }
      
      public void onServiceConnected()
      {
        AccessibilityService.-wrap0(AccessibilityService.this);
      }
      
      public void onSoftKeyboardShowModeChanged(int paramAnonymousInt)
      {
        AccessibilityService.-wrap2(AccessibilityService.this, paramAnonymousInt);
      }
    });
  }
  
  protected boolean onGesture(int paramInt)
  {
    return false;
  }
  
  public abstract void onInterrupt();
  
  protected boolean onKeyEvent(KeyEvent paramKeyEvent)
  {
    return false;
  }
  
  void onPerformGestureResult(int paramInt, final boolean paramBoolean)
  {
    if (this.mGestureStatusCallbackInfos == null) {
      return;
    }
    synchronized (this.mLock)
    {
      final GestureResultCallbackInfo localGestureResultCallbackInfo = (GestureResultCallbackInfo)this.mGestureStatusCallbackInfos.get(paramInt);
      if ((localGestureResultCallbackInfo == null) || (localGestureResultCallbackInfo.gestureDescription == null) || (localGestureResultCallbackInfo.callback == null)) {
        break label104;
      }
      if (localGestureResultCallbackInfo.handler != null)
      {
        localGestureResultCallbackInfo.handler.post(new Runnable()
        {
          public void run()
          {
            if (paramBoolean)
            {
              localGestureResultCallbackInfo.callback.onCompleted(localGestureResultCallbackInfo.gestureDescription);
              return;
            }
            localGestureResultCallbackInfo.callback.onCancelled(localGestureResultCallbackInfo.gestureDescription);
          }
        });
        return;
      }
    }
    if (paramBoolean)
    {
      ((GestureResultCallbackInfo)localObject2).callback.onCompleted(((GestureResultCallbackInfo)localObject2).gestureDescription);
      label104:
      return;
    }
    ((GestureResultCallbackInfo)localObject2).callback.onCancelled(((GestureResultCallbackInfo)localObject2).gestureDescription);
  }
  
  protected void onServiceConnected() {}
  
  public final boolean performGlobalAction(int paramInt)
  {
    IAccessibilityServiceConnection localIAccessibilityServiceConnection = AccessibilityInteractionClient.getInstance().getConnection(this.mConnectionId);
    if (localIAccessibilityServiceConnection != null) {
      try
      {
        boolean bool = localIAccessibilityServiceConnection.performGlobalAction(paramInt);
        return bool;
      }
      catch (RemoteException localRemoteException)
      {
        Log.w("AccessibilityService", "Error while calling performGlobalAction", localRemoteException);
        localRemoteException.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public final void setServiceInfo(AccessibilityServiceInfo paramAccessibilityServiceInfo)
  {
    this.mInfo = paramAccessibilityServiceInfo;
    sendServiceInfo();
  }
  
  public static abstract interface Callbacks
  {
    public abstract void init(int paramInt, IBinder paramIBinder);
    
    public abstract void onAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent);
    
    public abstract boolean onGesture(int paramInt);
    
    public abstract void onInterrupt();
    
    public abstract boolean onKeyEvent(KeyEvent paramKeyEvent);
    
    public abstract void onMagnificationChanged(Region paramRegion, float paramFloat1, float paramFloat2, float paramFloat3);
    
    public abstract void onPerformGestureResult(int paramInt, boolean paramBoolean);
    
    public abstract void onServiceConnected();
    
    public abstract void onSoftKeyboardShowModeChanged(int paramInt);
  }
  
  public static abstract class GestureResultCallback
  {
    public void onCancelled(GestureDescription paramGestureDescription) {}
    
    public void onCompleted(GestureDescription paramGestureDescription) {}
  }
  
  private static class GestureResultCallbackInfo
  {
    AccessibilityService.GestureResultCallback callback;
    GestureDescription gestureDescription;
    Handler handler;
    
    GestureResultCallbackInfo(GestureDescription paramGestureDescription, AccessibilityService.GestureResultCallback paramGestureResultCallback, Handler paramHandler)
    {
      this.gestureDescription = paramGestureDescription;
      this.callback = paramGestureResultCallback;
      this.handler = paramHandler;
    }
  }
  
  public static class IAccessibilityServiceClientWrapper
    extends IAccessibilityServiceClient.Stub
    implements HandlerCaller.Callback
  {
    private static final int DO_CLEAR_ACCESSIBILITY_CACHE = 5;
    private static final int DO_GESTURE_COMPLETE = 9;
    private static final int DO_INIT = 1;
    private static final int DO_ON_ACCESSIBILITY_EVENT = 3;
    private static final int DO_ON_GESTURE = 4;
    private static final int DO_ON_INTERRUPT = 2;
    private static final int DO_ON_KEY_EVENT = 6;
    private static final int DO_ON_MAGNIFICATION_CHANGED = 7;
    private static final int DO_ON_SOFT_KEYBOARD_SHOW_MODE_CHANGED = 8;
    private final AccessibilityService.Callbacks mCallback;
    private final HandlerCaller mCaller;
    private int mConnectionId;
    
    public IAccessibilityServiceClientWrapper(Context paramContext, Looper paramLooper, AccessibilityService.Callbacks paramCallbacks)
    {
      this.mCallback = paramCallbacks;
      this.mCaller = new HandlerCaller(paramContext, paramLooper, this, true);
    }
    
    public void clearAccessibilityCache()
    {
      Message localMessage = this.mCaller.obtainMessage(5);
      this.mCaller.sendMessage(localMessage);
    }
    
    public void executeMessage(Message paramMessage)
    {
      Object localObject1;
      Object localObject2;
      int i;
      switch (paramMessage.what)
      {
      default: 
        Log.w("AccessibilityService", "Unknown message type " + paramMessage.what);
        return;
      case 3: 
        paramMessage = (AccessibilityEvent)paramMessage.obj;
        if (paramMessage != null)
        {
          AccessibilityInteractionClient.getInstance().onAccessibilityEvent(paramMessage);
          this.mCallback.onAccessibilityEvent(paramMessage);
        }
        try
        {
          paramMessage.recycle();
          return;
        }
        catch (IllegalStateException paramMessage)
        {
          return;
        }
      case 2: 
        this.mCallback.onInterrupt();
        return;
      case 1: 
        this.mConnectionId = paramMessage.arg1;
        paramMessage = (SomeArgs)paramMessage.obj;
        localObject1 = (IAccessibilityServiceConnection)paramMessage.arg1;
        localObject2 = (IBinder)paramMessage.arg2;
        paramMessage.recycle();
        if (localObject1 != null)
        {
          AccessibilityInteractionClient.getInstance().addConnection(this.mConnectionId, (IAccessibilityServiceConnection)localObject1);
          this.mCallback.init(this.mConnectionId, (IBinder)localObject2);
          this.mCallback.onServiceConnected();
          return;
        }
        AccessibilityInteractionClient.getInstance().removeConnection(this.mConnectionId);
        this.mConnectionId = -1;
        AccessibilityInteractionClient.getInstance().clearCache();
        this.mCallback.init(-1, null);
        return;
      case 4: 
        i = paramMessage.arg1;
        this.mCallback.onGesture(i);
        return;
      case 5: 
        AccessibilityInteractionClient.getInstance().clearCache();
        return;
      case 6: 
        localObject1 = (KeyEvent)paramMessage.obj;
        try
        {
          localObject2 = AccessibilityInteractionClient.getInstance().getConnection(this.mConnectionId);
          if (localObject2 != null)
          {
            bool = this.mCallback.onKeyEvent((KeyEvent)localObject1);
            i = paramMessage.arg1;
          }
          try
          {
            ((IAccessibilityServiceConnection)localObject2).setOnKeyEventResult(bool, i);
          }
          catch (RemoteException paramMessage)
          {
            for (;;)
            {
              try
              {
                ((KeyEvent)localObject1).recycle();
                return;
              }
              catch (IllegalStateException paramMessage)
              {
                return;
              }
              paramMessage = paramMessage;
            }
          }
          paramMessage = (SomeArgs)paramMessage.obj;
        }
        finally
        {
          try
          {
            ((KeyEvent)localObject1).recycle();
            throw paramMessage;
          }
          catch (IllegalStateException localIllegalStateException)
          {
            for (;;) {}
          }
        }
      case 7: 
        Region localRegion = (Region)paramMessage.arg1;
        float f1 = ((Float)paramMessage.arg2).floatValue();
        float f2 = ((Float)paramMessage.arg3).floatValue();
        float f3 = ((Float)paramMessage.arg4).floatValue();
        this.mCallback.onMagnificationChanged(localRegion, f1, f2, f3);
        return;
      case 8: 
        i = paramMessage.arg1;
        this.mCallback.onSoftKeyboardShowModeChanged(i);
        return;
      }
      if (paramMessage.arg2 == 1) {}
      for (boolean bool = true;; bool = false)
      {
        this.mCallback.onPerformGestureResult(paramMessage.arg1, bool);
        return;
      }
    }
    
    public void init(IAccessibilityServiceConnection paramIAccessibilityServiceConnection, int paramInt, IBinder paramIBinder)
    {
      paramIAccessibilityServiceConnection = this.mCaller.obtainMessageIOO(1, paramInt, paramIAccessibilityServiceConnection, paramIBinder);
      this.mCaller.sendMessage(paramIAccessibilityServiceConnection);
    }
    
    public void onAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
    {
      paramAccessibilityEvent = this.mCaller.obtainMessageO(3, paramAccessibilityEvent);
      this.mCaller.sendMessage(paramAccessibilityEvent);
    }
    
    public void onGesture(int paramInt)
    {
      Message localMessage = this.mCaller.obtainMessageI(4, paramInt);
      this.mCaller.sendMessage(localMessage);
    }
    
    public void onInterrupt()
    {
      Message localMessage = this.mCaller.obtainMessage(2);
      this.mCaller.sendMessage(localMessage);
    }
    
    public void onKeyEvent(KeyEvent paramKeyEvent, int paramInt)
    {
      paramKeyEvent = this.mCaller.obtainMessageIO(6, paramInt, paramKeyEvent);
      this.mCaller.sendMessage(paramKeyEvent);
    }
    
    public void onMagnificationChanged(Region paramRegion, float paramFloat1, float paramFloat2, float paramFloat3)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramRegion;
      localSomeArgs.arg2 = Float.valueOf(paramFloat1);
      localSomeArgs.arg3 = Float.valueOf(paramFloat2);
      localSomeArgs.arg4 = Float.valueOf(paramFloat3);
      paramRegion = this.mCaller.obtainMessageO(7, localSomeArgs);
      this.mCaller.sendMessage(paramRegion);
    }
    
    public void onPerformGestureResult(int paramInt, boolean paramBoolean)
    {
      Object localObject = this.mCaller;
      if (paramBoolean) {}
      for (int i = 1;; i = 0)
      {
        localObject = ((HandlerCaller)localObject).obtainMessageII(9, paramInt, i);
        this.mCaller.sendMessage((Message)localObject);
        return;
      }
    }
    
    public void onSoftKeyboardShowModeChanged(int paramInt)
    {
      Message localMessage = this.mCaller.obtainMessageI(8, paramInt);
      this.mCaller.sendMessage(localMessage);
    }
  }
  
  public static final class MagnificationController
  {
    private ArrayMap<OnMagnificationChangedListener, Handler> mListeners;
    private final Object mLock;
    private final AccessibilityService mService;
    
    MagnificationController(AccessibilityService paramAccessibilityService, Object paramObject)
    {
      this.mService = paramAccessibilityService;
      this.mLock = paramObject;
    }
    
    private void setMagnificationCallbackEnabled(boolean paramBoolean)
    {
      IAccessibilityServiceConnection localIAccessibilityServiceConnection = AccessibilityInteractionClient.getInstance().getConnection(AccessibilityService.-get0(this.mService));
      if (localIAccessibilityServiceConnection != null) {}
      try
      {
        localIAccessibilityServiceConnection.setMagnificationCallbackEnabled(paramBoolean);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw new RuntimeException(localRemoteException);
      }
    }
    
    public void addListener(OnMagnificationChangedListener paramOnMagnificationChangedListener)
    {
      addListener(paramOnMagnificationChangedListener, null);
    }
    
    public void addListener(OnMagnificationChangedListener paramOnMagnificationChangedListener, Handler paramHandler)
    {
      synchronized (this.mLock)
      {
        if (this.mListeners == null) {
          this.mListeners = new ArrayMap();
        }
        boolean bool = this.mListeners.isEmpty();
        this.mListeners.put(paramOnMagnificationChangedListener, paramHandler);
        if (bool) {
          setMagnificationCallbackEnabled(true);
        }
        return;
      }
    }
    
    void dispatchMagnificationChanged(final Region paramRegion, final float paramFloat1, final float paramFloat2, final float paramFloat3)
    {
      for (;;)
      {
        synchronized (this.mLock)
        {
          if ((this.mListeners == null) || (this.mListeners.isEmpty()))
          {
            Slog.d("AccessibilityService", "Received magnification changed callback with no listeners registered!");
            setMagnificationCallbackEnabled(false);
            return;
          }
          ArrayMap localArrayMap = new ArrayMap(this.mListeners);
          int i = 0;
          int j = localArrayMap.size();
          if (i >= j) {
            break;
          }
          ??? = (OnMagnificationChangedListener)localArrayMap.keyAt(i);
          Handler localHandler = (Handler)localArrayMap.valueAt(i);
          if (localHandler != null)
          {
            localHandler.post(new Runnable()
            {
              public void run()
              {
                localObject.onMagnificationChanged(AccessibilityService.MagnificationController.this, paramRegion, paramFloat1, paramFloat2, paramFloat3);
              }
            });
            i += 1;
          }
        }
        ((OnMagnificationChangedListener)???).onMagnificationChanged(this, paramRegion, paramFloat1, paramFloat2, paramFloat3);
      }
    }
    
    public float getCenterX()
    {
      IAccessibilityServiceConnection localIAccessibilityServiceConnection = AccessibilityInteractionClient.getInstance().getConnection(AccessibilityService.-get0(this.mService));
      if (localIAccessibilityServiceConnection != null) {
        try
        {
          float f = localIAccessibilityServiceConnection.getMagnificationCenterX();
          return f;
        }
        catch (RemoteException localRemoteException)
        {
          Log.w("AccessibilityService", "Failed to obtain center X", localRemoteException);
          localRemoteException.rethrowFromSystemServer();
        }
      }
      return 0.0F;
    }
    
    public float getCenterY()
    {
      IAccessibilityServiceConnection localIAccessibilityServiceConnection = AccessibilityInteractionClient.getInstance().getConnection(AccessibilityService.-get0(this.mService));
      if (localIAccessibilityServiceConnection != null) {
        try
        {
          float f = localIAccessibilityServiceConnection.getMagnificationCenterY();
          return f;
        }
        catch (RemoteException localRemoteException)
        {
          Log.w("AccessibilityService", "Failed to obtain center Y", localRemoteException);
          localRemoteException.rethrowFromSystemServer();
        }
      }
      return 0.0F;
    }
    
    public Region getMagnificationRegion()
    {
      Object localObject = AccessibilityInteractionClient.getInstance().getConnection(AccessibilityService.-get0(this.mService));
      if (localObject != null) {
        try
        {
          localObject = ((IAccessibilityServiceConnection)localObject).getMagnificationRegion();
          return (Region)localObject;
        }
        catch (RemoteException localRemoteException)
        {
          Log.w("AccessibilityService", "Failed to obtain magnified region", localRemoteException);
          localRemoteException.rethrowFromSystemServer();
        }
      }
      return Region.obtain();
    }
    
    public float getScale()
    {
      IAccessibilityServiceConnection localIAccessibilityServiceConnection = AccessibilityInteractionClient.getInstance().getConnection(AccessibilityService.-get0(this.mService));
      if (localIAccessibilityServiceConnection != null) {
        try
        {
          float f = localIAccessibilityServiceConnection.getMagnificationScale();
          return f;
        }
        catch (RemoteException localRemoteException)
        {
          Log.w("AccessibilityService", "Failed to obtain scale", localRemoteException);
          localRemoteException.rethrowFromSystemServer();
        }
      }
      return 1.0F;
    }
    
    void onServiceConnected()
    {
      synchronized (this.mLock)
      {
        if (this.mListeners != null)
        {
          boolean bool = this.mListeners.isEmpty();
          if (!bool) {}
        }
        else
        {
          return;
        }
        setMagnificationCallbackEnabled(true);
      }
    }
    
    public boolean removeListener(OnMagnificationChangedListener paramOnMagnificationChangedListener)
    {
      if (this.mListeners == null) {
        return false;
      }
      synchronized (this.mLock)
      {
        int i = this.mListeners.indexOfKey(paramOnMagnificationChangedListener);
        if (i >= 0)
        {
          bool = true;
          if (bool) {
            this.mListeners.removeAt(i);
          }
          if ((bool) && (this.mListeners.isEmpty())) {
            setMagnificationCallbackEnabled(false);
          }
          return bool;
        }
        boolean bool = false;
      }
    }
    
    public boolean reset(boolean paramBoolean)
    {
      IAccessibilityServiceConnection localIAccessibilityServiceConnection = AccessibilityInteractionClient.getInstance().getConnection(AccessibilityService.-get0(this.mService));
      if (localIAccessibilityServiceConnection != null) {
        try
        {
          paramBoolean = localIAccessibilityServiceConnection.resetMagnification(paramBoolean);
          return paramBoolean;
        }
        catch (RemoteException localRemoteException)
        {
          Log.w("AccessibilityService", "Failed to reset", localRemoteException);
          localRemoteException.rethrowFromSystemServer();
        }
      }
      return false;
    }
    
    public boolean setCenter(float paramFloat1, float paramFloat2, boolean paramBoolean)
    {
      IAccessibilityServiceConnection localIAccessibilityServiceConnection = AccessibilityInteractionClient.getInstance().getConnection(AccessibilityService.-get0(this.mService));
      if (localIAccessibilityServiceConnection != null) {
        try
        {
          paramBoolean = localIAccessibilityServiceConnection.setMagnificationScaleAndCenter(NaN.0F, paramFloat1, paramFloat2, paramBoolean);
          return paramBoolean;
        }
        catch (RemoteException localRemoteException)
        {
          Log.w("AccessibilityService", "Failed to set center", localRemoteException);
          localRemoteException.rethrowFromSystemServer();
        }
      }
      return false;
    }
    
    public boolean setScale(float paramFloat, boolean paramBoolean)
    {
      IAccessibilityServiceConnection localIAccessibilityServiceConnection = AccessibilityInteractionClient.getInstance().getConnection(AccessibilityService.-get0(this.mService));
      if (localIAccessibilityServiceConnection != null) {
        try
        {
          paramBoolean = localIAccessibilityServiceConnection.setMagnificationScaleAndCenter(paramFloat, NaN.0F, NaN.0F, paramBoolean);
          return paramBoolean;
        }
        catch (RemoteException localRemoteException)
        {
          Log.w("AccessibilityService", "Failed to set scale", localRemoteException);
          localRemoteException.rethrowFromSystemServer();
        }
      }
      return false;
    }
    
    public static abstract interface OnMagnificationChangedListener
    {
      public abstract void onMagnificationChanged(AccessibilityService.MagnificationController paramMagnificationController, Region paramRegion, float paramFloat1, float paramFloat2, float paramFloat3);
    }
  }
  
  public static final class SoftKeyboardController
  {
    private ArrayMap<OnShowModeChangedListener, Handler> mListeners;
    private final Object mLock;
    private final AccessibilityService mService;
    
    SoftKeyboardController(AccessibilityService paramAccessibilityService, Object paramObject)
    {
      this.mService = paramAccessibilityService;
      this.mLock = paramObject;
    }
    
    private void setSoftKeyboardCallbackEnabled(boolean paramBoolean)
    {
      IAccessibilityServiceConnection localIAccessibilityServiceConnection = AccessibilityInteractionClient.getInstance().getConnection(AccessibilityService.-get0(this.mService));
      if (localIAccessibilityServiceConnection != null) {}
      try
      {
        localIAccessibilityServiceConnection.setSoftKeyboardCallbackEnabled(paramBoolean);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw new RuntimeException(localRemoteException);
      }
    }
    
    public void addOnShowModeChangedListener(OnShowModeChangedListener paramOnShowModeChangedListener)
    {
      addOnShowModeChangedListener(paramOnShowModeChangedListener, null);
    }
    
    public void addOnShowModeChangedListener(OnShowModeChangedListener paramOnShowModeChangedListener, Handler paramHandler)
    {
      synchronized (this.mLock)
      {
        if (this.mListeners == null) {
          this.mListeners = new ArrayMap();
        }
        boolean bool = this.mListeners.isEmpty();
        this.mListeners.put(paramOnShowModeChangedListener, paramHandler);
        if (bool) {
          setSoftKeyboardCallbackEnabled(true);
        }
        return;
      }
    }
    
    void dispatchSoftKeyboardShowModeChanged(final int paramInt)
    {
      for (;;)
      {
        synchronized (this.mLock)
        {
          if ((this.mListeners == null) || (this.mListeners.isEmpty()))
          {
            Slog.d("AccessibilityService", "Received soft keyboard show mode changed callback with no listeners registered!");
            setSoftKeyboardCallbackEnabled(false);
            return;
          }
          ArrayMap localArrayMap = new ArrayMap(this.mListeners);
          int i = 0;
          int j = localArrayMap.size();
          if (i >= j) {
            break;
          }
          ??? = (OnShowModeChangedListener)localArrayMap.keyAt(i);
          Handler localHandler = (Handler)localArrayMap.valueAt(i);
          if (localHandler != null)
          {
            localHandler.post(new Runnable()
            {
              public void run()
              {
                localObject2.onShowModeChanged(AccessibilityService.SoftKeyboardController.this, paramInt);
              }
            });
            i += 1;
          }
        }
        ((OnShowModeChangedListener)???).onShowModeChanged(this, paramInt);
      }
    }
    
    public int getShowMode()
    {
      try
      {
        int i = Settings.Secure.getInt(this.mService.getContentResolver(), "accessibility_soft_keyboard_mode");
        return i;
      }
      catch (Settings.SettingNotFoundException localSettingNotFoundException)
      {
        Log.v("AccessibilityService", "Failed to obtain the soft keyboard mode", localSettingNotFoundException);
      }
      return 0;
    }
    
    void onServiceConnected()
    {
      synchronized (this.mLock)
      {
        if (this.mListeners != null)
        {
          boolean bool = this.mListeners.isEmpty();
          if (!bool) {}
        }
        else
        {
          return;
        }
        setSoftKeyboardCallbackEnabled(true);
      }
    }
    
    public boolean removeOnShowModeChangedListener(OnShowModeChangedListener paramOnShowModeChangedListener)
    {
      if (this.mListeners == null) {
        return false;
      }
      synchronized (this.mLock)
      {
        int i = this.mListeners.indexOfKey(paramOnShowModeChangedListener);
        if (i >= 0)
        {
          bool = true;
          if (bool) {
            this.mListeners.removeAt(i);
          }
          if ((bool) && (this.mListeners.isEmpty())) {
            setSoftKeyboardCallbackEnabled(false);
          }
          return bool;
        }
        boolean bool = false;
      }
    }
    
    public boolean setShowMode(int paramInt)
    {
      IAccessibilityServiceConnection localIAccessibilityServiceConnection = AccessibilityInteractionClient.getInstance().getConnection(AccessibilityService.-get0(this.mService));
      if (localIAccessibilityServiceConnection != null) {
        try
        {
          boolean bool = localIAccessibilityServiceConnection.setSoftKeyboardShowMode(paramInt);
          return bool;
        }
        catch (RemoteException localRemoteException)
        {
          Log.w("AccessibilityService", "Failed to set soft keyboard behavior", localRemoteException);
          localRemoteException.rethrowFromSystemServer();
        }
      }
      return false;
    }
    
    public static abstract interface OnShowModeChangedListener
    {
      public abstract void onShowModeChanged(AccessibilityService.SoftKeyboardController paramSoftKeyboardController, int paramInt);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/accessibilityservice/AccessibilityService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */