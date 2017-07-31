package android.app;

import android.accessibilityservice.AccessibilityService.Callbacks;
import android.accessibilityservice.AccessibilityService.IAccessibilityServiceClientWrapper;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.IAccessibilityServiceClient;
import android.accessibilityservice.IAccessibilityServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Region;
import android.hardware.display.DisplayManagerGlobal;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.Log;
import android.view.Display;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.WindowAnimationFrameStats;
import android.view.WindowContentFrameStats;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityInteractionClient;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public final class UiAutomation
{
  private static final int CONNECTION_ID_UNDEFINED = -1;
  private static final long CONNECT_TIMEOUT_MILLIS = 5000L;
  private static final boolean DEBUG = false;
  public static final int FLAG_DONT_SUPPRESS_ACCESSIBILITY_SERVICES = 1;
  private static final String LOG_TAG = UiAutomation.class.getSimpleName();
  public static final int ROTATION_FREEZE_0 = 0;
  public static final int ROTATION_FREEZE_180 = 2;
  public static final int ROTATION_FREEZE_270 = 3;
  public static final int ROTATION_FREEZE_90 = 1;
  public static final int ROTATION_FREEZE_CURRENT = -1;
  public static final int ROTATION_UNFREEZE = -2;
  private final IAccessibilityServiceClient mClient;
  private int mConnectionId = -1;
  private final ArrayList<AccessibilityEvent> mEventQueue = new ArrayList();
  private int mFlags;
  private boolean mIsConnecting;
  private boolean mIsDestroyed;
  private long mLastEventTimeMillis;
  private final Object mLock = new Object();
  private OnAccessibilityEventListener mOnAccessibilityEventListener;
  private final IUiAutomationConnection mUiAutomationConnection;
  private boolean mWaitingForEventDelivery;
  
  public UiAutomation(Looper paramLooper, IUiAutomationConnection paramIUiAutomationConnection)
  {
    if (paramLooper == null) {
      throw new IllegalArgumentException("Looper cannot be null!");
    }
    if (paramIUiAutomationConnection == null) {
      throw new IllegalArgumentException("Connection cannot be null!");
    }
    this.mUiAutomationConnection = paramIUiAutomationConnection;
    this.mClient = new IAccessibilityServiceClientImpl(paramLooper);
  }
  
  private static float getDegreesForRotation(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return 0.0F;
    case 1: 
      return 270.0F;
    case 2: 
      return 180.0F;
    }
    return 90.0F;
  }
  
  private boolean isConnectedLocked()
  {
    return this.mConnectionId != -1;
  }
  
  private void throwIfConnectedLocked()
  {
    if (this.mConnectionId != -1) {
      throw new IllegalStateException("UiAutomation not connected!");
    }
  }
  
  private void throwIfNotConnectedLocked()
  {
    if (!isConnectedLocked()) {
      throw new IllegalStateException("UiAutomation not connected!");
    }
  }
  
  public void clearWindowAnimationFrameStats()
  {
    synchronized (this.mLock)
    {
      throwIfNotConnectedLocked();
    }
    try
    {
      this.mUiAutomationConnection.clearWindowAnimationFrameStats();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e(LOG_TAG, "Error clearing window animation frame stats!", localRemoteException);
    }
    localObject2 = finally;
    throw ((Throwable)localObject2);
  }
  
  public boolean clearWindowContentFrameStats(int paramInt)
  {
    synchronized (this.mLock)
    {
      throwIfNotConnectedLocked();
    }
    try
    {
      boolean bool = this.mUiAutomationConnection.clearWindowContentFrameStats(paramInt);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e(LOG_TAG, "Error clearing window content frame stats!", localRemoteException);
    }
    localObject2 = finally;
    throw ((Throwable)localObject2);
    return false;
  }
  
  public void connect()
  {
    connect(0);
  }
  
  /* Error */
  public void connect(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 64	android/app/UiAutomation:mLock	Ljava/lang/Object;
    //   4: astore 7
    //   6: aload 7
    //   8: monitorenter
    //   9: aload_0
    //   10: invokespecial 160	android/app/UiAutomation:throwIfConnectedLocked	()V
    //   13: aload_0
    //   14: getfield 162	android/app/UiAutomation:mIsConnecting	Z
    //   17: istore_2
    //   18: iload_2
    //   19: ifeq +7 -> 26
    //   22: aload 7
    //   24: monitorexit
    //   25: return
    //   26: aload_0
    //   27: iconst_1
    //   28: putfield 162	android/app/UiAutomation:mIsConnecting	Z
    //   31: aload 7
    //   33: monitorexit
    //   34: aload_0
    //   35: getfield 108	android/app/UiAutomation:mUiAutomationConnection	Landroid/app/IUiAutomationConnection;
    //   38: aload_0
    //   39: getfield 113	android/app/UiAutomation:mClient	Landroid/accessibilityservice/IAccessibilityServiceClient;
    //   42: iload_1
    //   43: invokeinterface 165 3 0
    //   48: aload_0
    //   49: iload_1
    //   50: putfield 167	android/app/UiAutomation:mFlags	I
    //   53: aload_0
    //   54: getfield 64	android/app/UiAutomation:mLock	Ljava/lang/Object;
    //   57: astore 7
    //   59: aload 7
    //   61: monitorenter
    //   62: invokestatic 173	android/os/SystemClock:uptimeMillis	()J
    //   65: lstore_3
    //   66: aload_0
    //   67: invokespecial 129	android/app/UiAutomation:isConnectedLocked	()Z
    //   70: istore_2
    //   71: iload_2
    //   72: ifeq +34 -> 106
    //   75: aload_0
    //   76: iconst_0
    //   77: putfield 162	android/app/UiAutomation:mIsConnecting	Z
    //   80: aload 7
    //   82: monitorexit
    //   83: return
    //   84: astore 8
    //   86: aload 7
    //   88: monitorexit
    //   89: aload 8
    //   91: athrow
    //   92: astore 7
    //   94: new 175	java/lang/RuntimeException
    //   97: dup
    //   98: ldc -79
    //   100: aload 7
    //   102: invokespecial 180	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   105: athrow
    //   106: ldc2_w 21
    //   109: invokestatic 173	android/os/SystemClock:uptimeMillis	()J
    //   112: lload_3
    //   113: lsub
    //   114: lsub
    //   115: lstore 5
    //   117: lload 5
    //   119: lconst_0
    //   120: lcmp
    //   121: ifgt +31 -> 152
    //   124: new 175	java/lang/RuntimeException
    //   127: dup
    //   128: ldc -79
    //   130: invokespecial 181	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   133: athrow
    //   134: astore 8
    //   136: aload_0
    //   137: iconst_0
    //   138: putfield 162	android/app/UiAutomation:mIsConnecting	Z
    //   141: aload 8
    //   143: athrow
    //   144: astore 8
    //   146: aload 7
    //   148: monitorexit
    //   149: aload 8
    //   151: athrow
    //   152: aload_0
    //   153: getfield 64	android/app/UiAutomation:mLock	Ljava/lang/Object;
    //   156: lload 5
    //   158: invokevirtual 185	java/lang/Object:wait	(J)V
    //   161: goto -95 -> 66
    //   164: astore 8
    //   166: goto -100 -> 66
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	169	0	this	UiAutomation
    //   0	169	1	paramInt	int
    //   17	55	2	bool	boolean
    //   65	48	3	l1	long
    //   115	42	5	l2	long
    //   92	55	7	localRemoteException	RemoteException
    //   84	6	8	localObject2	Object
    //   134	8	8	localObject3	Object
    //   144	6	8	localObject4	Object
    //   164	1	8	localInterruptedException	InterruptedException
    // Exception table:
    //   from	to	target	type
    //   9	18	84	finally
    //   26	31	84	finally
    //   34	53	92	android/os/RemoteException
    //   66	71	134	finally
    //   106	117	134	finally
    //   124	134	134	finally
    //   152	161	134	finally
    //   62	66	144	finally
    //   75	80	144	finally
    //   136	144	144	finally
    //   152	161	164	java/lang/InterruptedException
  }
  
  public void destroy()
  {
    disconnect();
    this.mIsDestroyed = true;
  }
  
  public void disconnect()
  {
    synchronized (this.mLock)
    {
      if (this.mIsConnecting) {
        throw new IllegalStateException("Cannot call disconnect() while connecting!");
      }
    }
    throwIfNotConnectedLocked();
    this.mConnectionId = -1;
    try
    {
      this.mUiAutomationConnection.disconnect();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("Error while disconnecting UiAutomation", localRemoteException);
    }
  }
  
  public AccessibilityEvent executeAndWaitForEvent(Runnable paramRunnable, AccessibilityEventFilter paramAccessibilityEventFilter, long paramLong)
    throws TimeoutException
  {
    long l1;
    synchronized (this.mLock)
    {
      throwIfNotConnectedLocked();
      this.mEventQueue.clear();
      this.mWaitingForEventDelivery = true;
      l1 = SystemClock.uptimeMillis();
      paramRunnable.run();
      paramRunnable = this.mLock;
    }
    for (;;)
    {
      long l2;
      try
      {
        l2 = SystemClock.uptimeMillis();
        if (this.mEventQueue.isEmpty()) {
          break label165;
        }
        ??? = (AccessibilityEvent)this.mEventQueue.remove(0);
        if (((AccessibilityEvent)???).getEventTime() < l1) {
          continue;
        }
        boolean bool = paramAccessibilityEventFilter.accept((AccessibilityEvent)???);
        if (!bool) {}
      }
      finally
      {
        this.mWaitingForEventDelivery = false;
        this.mEventQueue.clear();
        this.mLock.notifyAll();
      }
      try
      {
        this.mWaitingForEventDelivery = false;
        this.mEventQueue.clear();
        this.mLock.notifyAll();
        return (AccessibilityEvent)???;
      }
      finally {}
      paramRunnable = finally;
      throw paramRunnable;
      ((AccessibilityEvent)???).recycle();
      continue;
      label165:
      long l3 = paramLong - (SystemClock.uptimeMillis() - l2);
      if (l3 <= 0L) {
        throw new TimeoutException("Expected event not received within: " + paramLong + " ms.");
      }
      try
      {
        this.mLock.wait(l3);
      }
      catch (InterruptedException localInterruptedException) {}
    }
  }
  
  public ParcelFileDescriptor executeShellCommand(String paramString)
  {
    Object localObject4;
    ParcelFileDescriptor localParcelFileDescriptor;
    synchronized (this.mLock)
    {
      throwIfNotConnectedLocked();
      localObject6 = null;
      localObject5 = null;
      localObject3 = null;
      ??? = null;
      localObject2 = null;
    }
  }
  
  public AccessibilityNodeInfo findFocus(int paramInt)
  {
    return AccessibilityInteractionClient.getInstance().findFocus(this.mConnectionId, -2, AccessibilityNodeInfo.ROOT_NODE_ID, paramInt);
  }
  
  public int getConnectionId()
  {
    synchronized (this.mLock)
    {
      throwIfNotConnectedLocked();
      int i = this.mConnectionId;
      return i;
    }
  }
  
  public int getFlags()
  {
    return this.mFlags;
  }
  
  public AccessibilityNodeInfo getRootInActiveWindow()
  {
    synchronized (this.mLock)
    {
      throwIfNotConnectedLocked();
      int i = this.mConnectionId;
      return AccessibilityInteractionClient.getInstance().getRootInActiveWindow(i);
    }
  }
  
  public final AccessibilityServiceInfo getServiceInfo()
  {
    IAccessibilityServiceConnection localIAccessibilityServiceConnection;
    synchronized (this.mLock)
    {
      throwIfNotConnectedLocked();
      localIAccessibilityServiceConnection = AccessibilityInteractionClient.getInstance().getConnection(this.mConnectionId);
      if (localIAccessibilityServiceConnection == null) {}
    }
    return null;
  }
  
  public WindowAnimationFrameStats getWindowAnimationFrameStats()
  {
    synchronized (this.mLock)
    {
      throwIfNotConnectedLocked();
    }
    try
    {
      ??? = this.mUiAutomationConnection.getWindowAnimationFrameStats();
      return (WindowAnimationFrameStats)???;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e(LOG_TAG, "Error getting window animation frame stats!", localRemoteException);
    }
    localObject2 = finally;
    throw ((Throwable)localObject2);
    return null;
  }
  
  public WindowContentFrameStats getWindowContentFrameStats(int paramInt)
  {
    synchronized (this.mLock)
    {
      throwIfNotConnectedLocked();
    }
    try
    {
      ??? = this.mUiAutomationConnection.getWindowContentFrameStats(paramInt);
      return (WindowContentFrameStats)???;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e(LOG_TAG, "Error getting window content frame stats!", localRemoteException);
    }
    localObject2 = finally;
    throw ((Throwable)localObject2);
    return null;
  }
  
  public List<AccessibilityWindowInfo> getWindows()
  {
    synchronized (this.mLock)
    {
      throwIfNotConnectedLocked();
      int i = this.mConnectionId;
      return AccessibilityInteractionClient.getInstance().getWindows(i);
    }
  }
  
  public boolean grantRuntimePermission(String paramString1, String paramString2, UserHandle paramUserHandle)
  {
    synchronized (this.mLock)
    {
      throwIfNotConnectedLocked();
    }
    try
    {
      this.mUiAutomationConnection.grantRuntimePermission(paramString1, paramString2, paramUserHandle.getIdentifier());
      return true;
    }
    catch (RemoteException paramString1)
    {
      Log.e(LOG_TAG, "Error granting runtime permission", paramString1);
    }
    paramString1 = finally;
    throw paramString1;
    return false;
  }
  
  public boolean injectInputEvent(InputEvent paramInputEvent, boolean paramBoolean)
  {
    synchronized (this.mLock)
    {
      throwIfNotConnectedLocked();
    }
    try
    {
      paramBoolean = this.mUiAutomationConnection.injectInputEvent(paramInputEvent, paramBoolean);
      return paramBoolean;
    }
    catch (RemoteException paramInputEvent)
    {
      Log.e(LOG_TAG, "Error while injecting input event!", paramInputEvent);
    }
    paramInputEvent = finally;
    throw paramInputEvent;
    return false;
  }
  
  public boolean isDestroyed()
  {
    return this.mIsDestroyed;
  }
  
  public final boolean performGlobalAction(int paramInt)
  {
    IAccessibilityServiceConnection localIAccessibilityServiceConnection;
    boolean bool;
    synchronized (this.mLock)
    {
      throwIfNotConnectedLocked();
      localIAccessibilityServiceConnection = AccessibilityInteractionClient.getInstance().getConnection(this.mConnectionId);
      if (localIAccessibilityServiceConnection == null) {}
    }
    return false;
  }
  
  public boolean revokeRuntimePermission(String paramString1, String paramString2, UserHandle paramUserHandle)
  {
    synchronized (this.mLock)
    {
      throwIfNotConnectedLocked();
    }
    try
    {
      this.mUiAutomationConnection.revokeRuntimePermission(paramString1, paramString2, paramUserHandle.getIdentifier());
      return true;
    }
    catch (RemoteException paramString1)
    {
      Log.e(LOG_TAG, "Error revoking runtime permission", paramString1);
    }
    paramString1 = finally;
    throw paramString1;
    return false;
  }
  
  public void setOnAccessibilityEventListener(OnAccessibilityEventListener paramOnAccessibilityEventListener)
  {
    synchronized (this.mLock)
    {
      this.mOnAccessibilityEventListener = paramOnAccessibilityEventListener;
      return;
    }
  }
  
  public boolean setRotation(int paramInt)
  {
    synchronized (this.mLock)
    {
      throwIfNotConnectedLocked();
      switch (paramInt)
      {
      default: 
        throw new IllegalArgumentException("Invalid rotation.");
      }
    }
    try
    {
      this.mUiAutomationConnection.setRotation(paramInt);
      return true;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e(LOG_TAG, "Error while setting rotation!", localRemoteException);
    }
    return false;
  }
  
  public void setRunAsMonkey(boolean paramBoolean)
  {
    synchronized (this.mLock)
    {
      throwIfNotConnectedLocked();
    }
    try
    {
      ActivityManagerNative.getDefault().setUserIsMonkey(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e(LOG_TAG, "Error while setting run as monkey!", localRemoteException);
    }
    localObject2 = finally;
    throw ((Throwable)localObject2);
  }
  
  public final void setServiceInfo(AccessibilityServiceInfo paramAccessibilityServiceInfo)
  {
    IAccessibilityServiceConnection localIAccessibilityServiceConnection;
    synchronized (this.mLock)
    {
      throwIfNotConnectedLocked();
      AccessibilityInteractionClient.getInstance().clearCache();
      localIAccessibilityServiceConnection = AccessibilityInteractionClient.getInstance().getConnection(this.mConnectionId);
      if (localIAccessibilityServiceConnection == null) {}
    }
  }
  
  public Bitmap takeScreenshot()
  {
    int i;
    int j;
    int k;
    synchronized (this.mLock)
    {
      throwIfNotConnectedLocked();
      ??? = DisplayManagerGlobal.getInstance().getRealDisplay(0);
      Point localPoint = new Point();
      ((Display)???).getRealSize(localPoint);
      i = localPoint.x;
      j = localPoint.y;
      k = ((Display)???).getRotation();
      switch (k)
      {
      default: 
        throw new IllegalArgumentException("Invalid rotation: " + k);
      }
    }
    float f2 = i;
    float f1 = j;
    for (;;)
    {
      try
      {
        ??? = this.mUiAutomationConnection.takeScreenshot((int)f2, (int)f1);
        if (??? != null) {
          break;
        }
        return null;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e(LOG_TAG, "Error while taking screnshot!", localRemoteException);
        return null;
      }
      f2 = j;
      f1 = i;
      continue;
      f2 = i;
      f1 = j;
      continue;
      f2 = j;
      f1 = i;
    }
    Object localObject3 = localRemoteException;
    if (k != 0)
    {
      localObject3 = Bitmap.createBitmap(i, j, Bitmap.Config.ARGB_8888);
      Canvas localCanvas = new Canvas((Bitmap)localObject3);
      localCanvas.translate(((Bitmap)localObject3).getWidth() / 2, ((Bitmap)localObject3).getHeight() / 2);
      localCanvas.rotate(getDegreesForRotation(k));
      localCanvas.translate(-f2 / 2.0F, -f1 / 2.0F);
      localCanvas.drawBitmap(localRemoteException, 0.0F, 0.0F, null);
      localCanvas.setBitmap(null);
      localRemoteException.recycle();
    }
    ((Bitmap)localObject3).setHasAlpha(false);
    return (Bitmap)localObject3;
  }
  
  public void waitForIdle(long paramLong1, long paramLong2)
    throws TimeoutException
  {
    for (;;)
    {
      synchronized (this.mLock)
      {
        throwIfNotConnectedLocked();
        long l1 = SystemClock.uptimeMillis();
        if (this.mLastEventTimeMillis <= 0L) {
          this.mLastEventTimeMillis = l1;
        }
        l2 = SystemClock.uptimeMillis();
        if (paramLong2 - (l2 - l1) <= 0L) {
          throw new TimeoutException("No idle state with idle timeout: " + paramLong1 + " within global timeout: " + paramLong2);
        }
      }
      long l3 = this.mLastEventTimeMillis;
      long l2 = paramLong1 - (l2 - l3);
      if (l2 <= 0L) {
        return;
      }
      try
      {
        this.mLock.wait(l2);
      }
      catch (InterruptedException localInterruptedException) {}
    }
  }
  
  public static abstract interface AccessibilityEventFilter
  {
    public abstract boolean accept(AccessibilityEvent paramAccessibilityEvent);
  }
  
  private class IAccessibilityServiceClientImpl
    extends AccessibilityService.IAccessibilityServiceClientWrapper
  {
    public IAccessibilityServiceClientImpl(Looper paramLooper)
    {
      super(paramLooper, new AccessibilityService.Callbacks()
      {
        public void init(int paramAnonymousInt, IBinder arg2)
        {
          synchronized (UiAutomation.-get1(this.val$this$0))
          {
            UiAutomation.-set0(this.val$this$0, paramAnonymousInt);
            UiAutomation.-get1(this.val$this$0).notifyAll();
            return;
          }
        }
        
        public void onAccessibilityEvent(AccessibilityEvent paramAnonymousAccessibilityEvent)
        {
          synchronized (UiAutomation.-get1(this.val$this$0))
          {
            UiAutomation.-set1(this.val$this$0, paramAnonymousAccessibilityEvent.getEventTime());
            if (UiAutomation.-get3(this.val$this$0)) {
              UiAutomation.-get0(this.val$this$0).add(AccessibilityEvent.obtain(paramAnonymousAccessibilityEvent));
            }
            UiAutomation.-get1(this.val$this$0).notifyAll();
            ??? = UiAutomation.-get2(this.val$this$0);
            if (??? != null) {
              ((UiAutomation.OnAccessibilityEventListener)???).onAccessibilityEvent(AccessibilityEvent.obtain(paramAnonymousAccessibilityEvent));
            }
            return;
          }
        }
        
        public boolean onGesture(int paramAnonymousInt)
        {
          return false;
        }
        
        public void onInterrupt() {}
        
        public boolean onKeyEvent(KeyEvent paramAnonymousKeyEvent)
        {
          return false;
        }
        
        public void onMagnificationChanged(Region paramAnonymousRegion, float paramAnonymousFloat1, float paramAnonymousFloat2, float paramAnonymousFloat3) {}
        
        public void onPerformGestureResult(int paramAnonymousInt, boolean paramAnonymousBoolean) {}
        
        public void onServiceConnected() {}
        
        public void onSoftKeyboardShowModeChanged(int paramAnonymousInt) {}
      });
    }
  }
  
  public static abstract interface OnAccessibilityEventListener
  {
    public abstract void onAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/UiAutomation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */