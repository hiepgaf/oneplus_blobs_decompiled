package android.hardware.display;

import android.content.Context;
import android.media.projection.MediaProjection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.DisplayAdjustments;
import android.view.DisplayInfo;
import android.view.Surface;
import java.util.ArrayList;

public final class DisplayManagerGlobal
{
  private static final boolean DEBUG = false;
  public static final int EVENT_DISPLAY_ADDED = 1;
  public static final int EVENT_DISPLAY_CHANGED = 2;
  public static final int EVENT_DISPLAY_REMOVED = 3;
  private static final String TAG = "DisplayManager";
  private static final boolean USE_CACHE = false;
  private static DisplayManagerGlobal sInstance;
  private DisplayManagerCallback mCallback;
  private int[] mDisplayIdCache;
  private final SparseArray<DisplayInfo> mDisplayInfoCache = new SparseArray();
  private final ArrayList<DisplayListenerDelegate> mDisplayListeners = new ArrayList();
  private final IDisplayManager mDm;
  private final Object mLock = new Object();
  private int mWifiDisplayScanNestCount;
  
  private DisplayManagerGlobal(IDisplayManager paramIDisplayManager)
  {
    this.mDm = paramIDisplayManager;
  }
  
  private int findDisplayListenerLocked(DisplayManager.DisplayListener paramDisplayListener)
  {
    int j = this.mDisplayListeners.size();
    int i = 0;
    while (i < j)
    {
      if (((DisplayListenerDelegate)this.mDisplayListeners.get(i)).mListener == paramDisplayListener) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  public static DisplayManagerGlobal getInstance()
  {
    try
    {
      if (sInstance == null)
      {
        localObject1 = ServiceManager.getService("display");
        if (localObject1 != null) {
          sInstance = new DisplayManagerGlobal(IDisplayManager.Stub.asInterface((IBinder)localObject1));
        }
      }
      Object localObject1 = sInstance;
      return (DisplayManagerGlobal)localObject1;
    }
    finally {}
  }
  
  private void handleDisplayEvent(int paramInt1, int paramInt2)
  {
    synchronized (this.mLock)
    {
      int j = this.mDisplayListeners.size();
      int i = 0;
      while (i < j)
      {
        ((DisplayListenerDelegate)this.mDisplayListeners.get(i)).sendDisplayEvent(paramInt1, paramInt2);
        i += 1;
      }
      return;
    }
  }
  
  private void registerCallbackIfNeededLocked()
  {
    if (this.mCallback == null) {
      this.mCallback = new DisplayManagerCallback(null);
    }
    try
    {
      this.mDm.registerCallback(this.mCallback);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void connectWifiDisplay(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("deviceAddress must not be null");
    }
    try
    {
      this.mDm.connectWifiDisplay(paramString);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public VirtualDisplay createVirtualDisplay(Context paramContext, MediaProjection paramMediaProjection, String paramString, int paramInt1, int paramInt2, int paramInt3, Surface paramSurface, int paramInt4, VirtualDisplay.Callback paramCallback, Handler paramHandler)
  {
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("name must be non-null and non-empty");
    }
    if ((paramInt1 <= 0) || (paramInt2 <= 0)) {}
    while (paramInt3 <= 0) {
      throw new IllegalArgumentException("width, height, and densityDpi must be greater than 0");
    }
    paramCallback = new VirtualDisplayCallback(paramCallback, paramHandler);
    if (paramMediaProjection != null) {}
    for (paramMediaProjection = paramMediaProjection.getProjection();; paramMediaProjection = null) {
      try
      {
        paramInt1 = this.mDm.createVirtualDisplay(paramCallback, paramMediaProjection, paramContext.getPackageName(), paramString, paramInt1, paramInt2, paramInt3, paramSurface, paramInt4);
        if (paramInt1 >= 0) {
          break;
        }
        Log.e("DisplayManager", "Could not create virtual display: " + paramString);
        return null;
      }
      catch (RemoteException paramContext)
      {
        throw paramContext.rethrowFromSystemServer();
      }
    }
    paramContext = getRealDisplay(paramInt1);
    if (paramContext == null)
    {
      Log.wtf("DisplayManager", "Could not obtain display info for newly created virtual display: " + paramString);
      try
      {
        this.mDm.releaseVirtualDisplay(paramCallback);
        return null;
      }
      catch (RemoteException paramContext)
      {
        throw paramContext.rethrowFromSystemServer();
      }
    }
    return new VirtualDisplay(this, paramContext, paramCallback, paramSurface);
  }
  
  public void disconnectWifiDisplay()
  {
    try
    {
      this.mDm.disconnectWifiDisplay();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void forgetWifiDisplay(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("deviceAddress must not be null");
    }
    try
    {
      this.mDm.forgetWifiDisplay(paramString);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public Display getCompatibleDisplay(int paramInt, DisplayAdjustments paramDisplayAdjustments)
  {
    DisplayInfo localDisplayInfo = getDisplayInfo(paramInt);
    if (localDisplayInfo == null) {
      return null;
    }
    return new Display(this, paramInt, localDisplayInfo, paramDisplayAdjustments);
  }
  
  /* Error */
  public int[] getDisplayIds()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 62	android/hardware/display/DisplayManagerGlobal:mLock	Ljava/lang/Object;
    //   4: astore_1
    //   5: aload_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 74	android/hardware/display/DisplayManagerGlobal:mDm	Landroid/hardware/display/IDisplayManager;
    //   11: invokeinterface 226 1 0
    //   16: astore_2
    //   17: aload_0
    //   18: invokespecial 228	android/hardware/display/DisplayManagerGlobal:registerCallbackIfNeededLocked	()V
    //   21: aload_1
    //   22: monitorexit
    //   23: aload_2
    //   24: areturn
    //   25: astore_2
    //   26: aload_1
    //   27: monitorexit
    //   28: aload_2
    //   29: athrow
    //   30: astore_1
    //   31: aload_1
    //   32: invokevirtual 129	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
    //   35: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	36	0	this	DisplayManagerGlobal
    //   30	2	1	localRemoteException	RemoteException
    //   16	8	2	arrayOfInt	int[]
    //   25	4	2	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   7	21	25	finally
    //   0	7	30	android/os/RemoteException
    //   21	23	30	android/os/RemoteException
    //   26	30	30	android/os/RemoteException
  }
  
  /* Error */
  public DisplayInfo getDisplayInfo(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 62	android/hardware/display/DisplayManagerGlobal:mLock	Ljava/lang/Object;
    //   4: astore_2
    //   5: aload_2
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 74	android/hardware/display/DisplayManagerGlobal:mDm	Landroid/hardware/display/IDisplayManager;
    //   11: iload_1
    //   12: invokeinterface 229 2 0
    //   17: astore_3
    //   18: aload_3
    //   19: ifnonnull +7 -> 26
    //   22: aload_2
    //   23: monitorexit
    //   24: aconst_null
    //   25: areturn
    //   26: aload_0
    //   27: invokespecial 228	android/hardware/display/DisplayManagerGlobal:registerCallbackIfNeededLocked	()V
    //   30: aload_2
    //   31: monitorexit
    //   32: aload_3
    //   33: areturn
    //   34: astore_3
    //   35: aload_2
    //   36: monitorexit
    //   37: aload_3
    //   38: athrow
    //   39: astore_2
    //   40: aload_2
    //   41: invokevirtual 129	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
    //   44: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	45	0	this	DisplayManagerGlobal
    //   0	45	1	paramInt	int
    //   39	2	2	localRemoteException	RemoteException
    //   17	16	3	localDisplayInfo	DisplayInfo
    //   34	4	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   7	18	34	finally
    //   26	30	34	finally
    //   0	7	39	android/os/RemoteException
    //   22	24	39	android/os/RemoteException
    //   30	32	39	android/os/RemoteException
    //   35	39	39	android/os/RemoteException
  }
  
  public Display getRealDisplay(int paramInt)
  {
    return getCompatibleDisplay(paramInt, DisplayAdjustments.DEFAULT_DISPLAY_ADJUSTMENTS);
  }
  
  public WifiDisplayStatus getWifiDisplayStatus()
  {
    try
    {
      WifiDisplayStatus localWifiDisplayStatus = this.mDm.getWifiDisplayStatus();
      return localWifiDisplayStatus;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void pauseWifiDisplay()
  {
    try
    {
      this.mDm.pauseWifiDisplay();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void registerDisplayListener(DisplayManager.DisplayListener paramDisplayListener, Handler paramHandler)
  {
    if (paramDisplayListener == null) {
      throw new IllegalArgumentException("listener must not be null");
    }
    synchronized (this.mLock)
    {
      if (findDisplayListenerLocked(paramDisplayListener) < 0)
      {
        this.mDisplayListeners.add(new DisplayListenerDelegate(paramDisplayListener, paramHandler));
        registerCallbackIfNeededLocked();
      }
      return;
    }
  }
  
  public void releaseVirtualDisplay(IVirtualDisplayCallback paramIVirtualDisplayCallback)
  {
    try
    {
      this.mDm.releaseVirtualDisplay(paramIVirtualDisplayCallback);
      return;
    }
    catch (RemoteException paramIVirtualDisplayCallback)
    {
      throw paramIVirtualDisplayCallback.rethrowFromSystemServer();
    }
  }
  
  public void renameWifiDisplay(String paramString1, String paramString2)
  {
    if (paramString1 == null) {
      throw new IllegalArgumentException("deviceAddress must not be null");
    }
    try
    {
      this.mDm.renameWifiDisplay(paramString1, paramString2);
      return;
    }
    catch (RemoteException paramString1)
    {
      throw paramString1.rethrowFromSystemServer();
    }
  }
  
  public void requestColorMode(int paramInt1, int paramInt2)
  {
    try
    {
      this.mDm.requestColorMode(paramInt1, paramInt2);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void resizeVirtualDisplay(IVirtualDisplayCallback paramIVirtualDisplayCallback, int paramInt1, int paramInt2, int paramInt3)
  {
    try
    {
      this.mDm.resizeVirtualDisplay(paramIVirtualDisplayCallback, paramInt1, paramInt2, paramInt3);
      return;
    }
    catch (RemoteException paramIVirtualDisplayCallback)
    {
      throw paramIVirtualDisplayCallback.rethrowFromSystemServer();
    }
  }
  
  public void resumeWifiDisplay()
  {
    try
    {
      this.mDm.resumeWifiDisplay();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void setVirtualDisplaySurface(IVirtualDisplayCallback paramIVirtualDisplayCallback, Surface paramSurface)
  {
    try
    {
      this.mDm.setVirtualDisplaySurface(paramIVirtualDisplayCallback, paramSurface);
      return;
    }
    catch (RemoteException paramIVirtualDisplayCallback)
    {
      throw paramIVirtualDisplayCallback.rethrowFromSystemServer();
    }
  }
  
  public void startWifiDisplayScan()
  {
    synchronized (this.mLock)
    {
      int i = this.mWifiDisplayScanNestCount;
      this.mWifiDisplayScanNestCount = (i + 1);
      if (i == 0) {
        registerCallbackIfNeededLocked();
      }
      try
      {
        this.mDm.startWifiDisplayScan();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
  }
  
  public void stopWifiDisplayScan()
  {
    for (;;)
    {
      synchronized (this.mLock)
      {
        int i = this.mWifiDisplayScanNestCount - 1;
        this.mWifiDisplayScanNestCount = i;
        if (i == 0) {
          try
          {
            this.mDm.stopWifiDisplayScan();
            return;
          }
          catch (RemoteException localRemoteException)
          {
            throw localRemoteException.rethrowFromSystemServer();
          }
        }
      }
      if (this.mWifiDisplayScanNestCount < 0)
      {
        Log.wtf("DisplayManager", "Wifi display scan nest count became negative: " + this.mWifiDisplayScanNestCount);
        this.mWifiDisplayScanNestCount = 0;
      }
    }
  }
  
  public void unregisterDisplayListener(DisplayManager.DisplayListener paramDisplayListener)
  {
    if (paramDisplayListener == null) {
      throw new IllegalArgumentException("listener must not be null");
    }
    synchronized (this.mLock)
    {
      int i = findDisplayListenerLocked(paramDisplayListener);
      if (i >= 0)
      {
        ((DisplayListenerDelegate)this.mDisplayListeners.get(i)).clearEvents();
        this.mDisplayListeners.remove(i);
      }
      return;
    }
  }
  
  private static final class DisplayListenerDelegate
    extends Handler
  {
    public final DisplayManager.DisplayListener mListener;
    
    public DisplayListenerDelegate(DisplayManager.DisplayListener paramDisplayListener, Handler paramHandler) {}
    
    public void clearEvents()
    {
      removeCallbacksAndMessages(null);
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      case 1: 
        this.mListener.onDisplayAdded(paramMessage.arg1);
        return;
      case 2: 
        this.mListener.onDisplayChanged(paramMessage.arg1);
        return;
      }
      this.mListener.onDisplayRemoved(paramMessage.arg1);
    }
    
    public void sendDisplayEvent(int paramInt1, int paramInt2)
    {
      sendMessage(obtainMessage(paramInt2, paramInt1, 0));
    }
  }
  
  private final class DisplayManagerCallback
    extends IDisplayManagerCallback.Stub
  {
    private DisplayManagerCallback() {}
    
    public void onDisplayEvent(int paramInt1, int paramInt2)
    {
      DisplayManagerGlobal.-wrap0(DisplayManagerGlobal.this, paramInt1, paramInt2);
    }
  }
  
  private static final class VirtualDisplayCallback
    extends IVirtualDisplayCallback.Stub
  {
    private DisplayManagerGlobal.VirtualDisplayCallbackDelegate mDelegate;
    
    public VirtualDisplayCallback(VirtualDisplay.Callback paramCallback, Handler paramHandler)
    {
      if (paramCallback != null) {
        this.mDelegate = new DisplayManagerGlobal.VirtualDisplayCallbackDelegate(paramCallback, paramHandler);
      }
    }
    
    public void onPaused()
    {
      if (this.mDelegate != null) {
        this.mDelegate.sendEmptyMessage(0);
      }
    }
    
    public void onResumed()
    {
      if (this.mDelegate != null) {
        this.mDelegate.sendEmptyMessage(1);
      }
    }
    
    public void onStopped()
    {
      if (this.mDelegate != null) {
        this.mDelegate.sendEmptyMessage(2);
      }
    }
  }
  
  private static final class VirtualDisplayCallbackDelegate
    extends Handler
  {
    public static final int MSG_DISPLAY_PAUSED = 0;
    public static final int MSG_DISPLAY_RESUMED = 1;
    public static final int MSG_DISPLAY_STOPPED = 2;
    private final VirtualDisplay.Callback mCallback;
    
    public VirtualDisplayCallbackDelegate(VirtualDisplay.Callback paramCallback, Handler paramHandler) {}
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      case 0: 
        this.mCallback.onPaused();
        return;
      case 1: 
        this.mCallback.onResumed();
        return;
      }
      this.mCallback.onStopped();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/display/DisplayManagerGlobal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */