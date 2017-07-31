package com.android.server.wm;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.graphics.Rect;
import android.os.Debug;
import android.os.RemoteException;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.view.IApplicationToken;
import android.view.InputChannel;
import android.view.KeyEvent;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManagerPolicy;
import com.android.server.input.InputApplicationHandle;
import com.android.server.input.InputManagerService;
import com.android.server.input.InputManagerService.WindowManagerCallbacks;
import com.android.server.input.InputWindowHandle;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;

final class InputMonitor
  implements InputManagerService.WindowManagerCallbacks
{
  private boolean mInputDevicesReady;
  private final Object mInputDevicesReadyMonitor = new Object();
  private boolean mInputDispatchEnabled;
  private boolean mInputDispatchFrozen;
  private WindowState mInputFocus;
  private String mInputFreezeReason = null;
  private int mInputWindowHandleCount;
  private InputWindowHandle[] mInputWindowHandles;
  private final WindowManagerService mService;
  private boolean mUpdateInputWindowsNeeded = true;
  
  public InputMonitor(WindowManagerService paramWindowManagerService)
  {
    this.mService = paramWindowManagerService;
  }
  
  private void addInputWindowHandleLw(InputWindowHandle paramInputWindowHandle)
  {
    if (this.mInputWindowHandles == null) {
      this.mInputWindowHandles = new InputWindowHandle[16];
    }
    if (this.mInputWindowHandleCount >= this.mInputWindowHandles.length) {
      this.mInputWindowHandles = ((InputWindowHandle[])Arrays.copyOf(this.mInputWindowHandles, this.mInputWindowHandleCount * 2));
    }
    InputWindowHandle[] arrayOfInputWindowHandle = this.mInputWindowHandles;
    int i = this.mInputWindowHandleCount;
    this.mInputWindowHandleCount = (i + 1);
    arrayOfInputWindowHandle[i] = paramInputWindowHandle;
  }
  
  private void addInputWindowHandleLw(InputWindowHandle paramInputWindowHandle, WindowState paramWindowState, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    paramInputWindowHandle.name = paramWindowState.toString();
    paramInputWindowHandle.layoutParamsFlags = paramWindowState.getTouchableRegion(paramInputWindowHandle.touchableRegion, paramInt1);
    paramInputWindowHandle.layoutParamsType = paramInt2;
    paramInputWindowHandle.dispatchingTimeoutNanos = paramWindowState.getInputDispatchingTimeoutNanos();
    paramInputWindowHandle.visible = paramBoolean1;
    paramInputWindowHandle.canReceiveKeys = paramWindowState.canReceiveKeys();
    paramInputWindowHandle.hasFocus = paramBoolean2;
    paramInputWindowHandle.hasWallpaper = paramBoolean3;
    if (paramWindowState.mAppToken != null)
    {
      paramBoolean1 = paramWindowState.mAppToken.paused;
      paramInputWindowHandle.paused = paramBoolean1;
      paramInputWindowHandle.layer = paramWindowState.mLayer;
      paramInputWindowHandle.ownerPid = paramWindowState.mSession.mPid;
      paramInputWindowHandle.ownerUid = paramWindowState.mSession.mUid;
      paramInputWindowHandle.inputFeatures = paramWindowState.mAttrs.inputFeatures;
      Rect localRect = paramWindowState.mFrame;
      paramInputWindowHandle.frameLeft = localRect.left;
      paramInputWindowHandle.frameTop = localRect.top;
      paramInputWindowHandle.frameRight = localRect.right;
      paramInputWindowHandle.frameBottom = localRect.bottom;
      if (paramWindowState.isDockedInEffect())
      {
        paramInputWindowHandle.frameLeft += paramWindowState.mXOffset;
        paramInputWindowHandle.frameTop += paramWindowState.mYOffset;
        paramInputWindowHandle.frameRight += paramWindowState.mXOffset;
        paramInputWindowHandle.frameBottom += paramWindowState.mYOffset;
      }
      if (paramWindowState.mGlobalScale == 1.0F) {
        break label296;
      }
    }
    label296:
    for (paramInputWindowHandle.scaleFactor = (1.0F / paramWindowState.mGlobalScale);; paramInputWindowHandle.scaleFactor = 1.0F)
    {
      if (WindowManagerDebugConfig.DEBUG_INPUT) {
        Slog.d("WindowManager", "addInputWindowHandle: " + paramWindowState + ", " + paramInputWindowHandle);
      }
      addInputWindowHandleLw(paramInputWindowHandle);
      return;
      paramBoolean1 = false;
      break;
    }
  }
  
  private void clearInputWindowHandlesLw()
  {
    while (this.mInputWindowHandleCount != 0)
    {
      InputWindowHandle[] arrayOfInputWindowHandle = this.mInputWindowHandles;
      int i = this.mInputWindowHandleCount - 1;
      this.mInputWindowHandleCount = i;
      arrayOfInputWindowHandle[i] = null;
    }
  }
  
  private void updateInputDispatchModeLw()
  {
    this.mService.mInputManager.setInputDispatchMode(this.mInputDispatchEnabled, this.mInputDispatchFrozen);
  }
  
  public KeyEvent dispatchUnhandledKey(InputWindowHandle paramInputWindowHandle, KeyEvent paramKeyEvent, int paramInt)
  {
    if (paramInputWindowHandle != null) {}
    for (paramInputWindowHandle = (WindowState)paramInputWindowHandle.windowState;; paramInputWindowHandle = null) {
      return this.mService.mPolicy.dispatchUnhandledKey(paramInputWindowHandle, paramKeyEvent, paramInt);
    }
  }
  
  void dump(PrintWriter paramPrintWriter, String paramString)
  {
    if (this.mInputFreezeReason != null) {
      paramPrintWriter.println(paramString + "mInputFreezeReason=" + this.mInputFreezeReason);
    }
  }
  
  public void freezeInputDispatchingLw()
  {
    if (!this.mInputDispatchFrozen)
    {
      if (WindowManagerDebugConfig.DEBUG_INPUT) {
        Slog.v("WindowManager", "Freezing input dispatching");
      }
      this.mInputDispatchFrozen = true;
      if (!WindowManagerDebugConfig.DEBUG_INPUT) {}
      this.mInputFreezeReason = Debug.getCallers(6);
      updateInputDispatchModeLw();
    }
  }
  
  public int getPointerLayer()
  {
    return this.mService.mPolicy.windowTypeToLayerLw(2018) * 10000 + 1000;
  }
  
  public long interceptKeyBeforeDispatching(InputWindowHandle paramInputWindowHandle, KeyEvent paramKeyEvent, int paramInt)
  {
    if (paramInputWindowHandle != null) {}
    for (paramInputWindowHandle = (WindowState)paramInputWindowHandle.windowState;; paramInputWindowHandle = null) {
      return this.mService.mPolicy.interceptKeyBeforeDispatching(paramInputWindowHandle, paramKeyEvent, paramInt);
    }
  }
  
  public int interceptKeyBeforeQueueing(KeyEvent paramKeyEvent, int paramInt)
  {
    return this.mService.mPolicy.interceptKeyBeforeQueueing(paramKeyEvent, paramInt);
  }
  
  public int interceptMotionBeforeQueueingNonInteractive(long paramLong, int paramInt)
  {
    return this.mService.mPolicy.interceptMotionBeforeQueueingNonInteractive(paramLong, paramInt);
  }
  
  public long notifyANR(InputApplicationHandle paramInputApplicationHandle, InputWindowHandle paramInputWindowHandle, String paramString)
  {
    Object localObject2 = null;
    InputWindowHandle localInputWindowHandle = null;
    boolean bool = false;
    HashMap localHashMap = this.mService.mWindowMap;
    Object localObject1 = localObject2;
    if (paramInputWindowHandle != null) {}
    long l;
    for (;;)
    {
      try
      {
        paramInputWindowHandle = (WindowState)paramInputWindowHandle.windowState;
        localObject1 = localObject2;
        localInputWindowHandle = paramInputWindowHandle;
        if (paramInputWindowHandle != null)
        {
          localObject1 = paramInputWindowHandle.mAppToken;
          localInputWindowHandle = paramInputWindowHandle;
        }
        paramInputWindowHandle = (InputWindowHandle)localObject1;
        if (localObject1 == null)
        {
          paramInputWindowHandle = (InputWindowHandle)localObject1;
          if (paramInputApplicationHandle != null) {
            paramInputWindowHandle = (AppWindowToken)paramInputApplicationHandle.appWindowToken;
          }
        }
        if (localInputWindowHandle != null)
        {
          Slog.i("WindowManager", "Input event dispatching timed out sending to " + localInputWindowHandle.mAttrs.getTitle() + ".  Reason: " + paramString);
          int i = this.mService.mPolicy.windowTypeToLayerLw(2003);
          if (localInputWindowHandle.mBaseLayer > i)
          {
            bool = true;
            this.mService.saveANRStateLocked(paramInputWindowHandle, localInputWindowHandle, paramString);
            if ((paramInputWindowHandle == null) || (paramInputWindowHandle.appToken == null)) {
              break;
            }
          }
        }
      }
      finally {}
      try
      {
        if (paramInputWindowHandle.appToken.keyDispatchingTimedOut(paramString)) {
          break label333;
        }
        l = paramInputWindowHandle.inputDispatchingTimeoutNanos;
        return l;
      }
      catch (RemoteException paramInputApplicationHandle)
      {
        for (;;) {}
      }
      bool = false;
      continue;
      if (paramInputWindowHandle != null) {
        Slog.i("WindowManager", "Input event dispatching timed out sending to application " + paramInputWindowHandle.stringName + ".  Reason: " + paramString);
      } else {
        Slog.i("WindowManager", "Input event dispatching timed out .  Reason: " + paramString);
      }
    }
    if (localInputWindowHandle != null) {
      try
      {
        l = ActivityManagerNative.getDefault().inputDispatchingTimedOut(localInputWindowHandle.mSession.mPid, bool, paramString);
        if (l >= 0L) {
          return 1000000L * l;
        }
      }
      catch (RemoteException paramInputApplicationHandle) {}
    }
    label333:
    return 0L;
  }
  
  public void notifyCameraLensCoverSwitchChanged(long paramLong, boolean paramBoolean)
  {
    this.mService.mPolicy.notifyCameraLensCoverSwitchChanged(paramLong, paramBoolean);
  }
  
  public void notifyConfigurationChanged()
  {
    this.mService.sendNewConfiguration();
    synchronized (this.mInputDevicesReadyMonitor)
    {
      if (!this.mInputDevicesReady)
      {
        this.mInputDevicesReady = true;
        this.mInputDevicesReadyMonitor.notifyAll();
      }
      return;
    }
  }
  
  public void notifyInputChannelBroken(InputWindowHandle paramInputWindowHandle)
  {
    if (paramInputWindowHandle == null) {
      return;
    }
    synchronized (this.mService.mWindowMap)
    {
      paramInputWindowHandle = (WindowState)paramInputWindowHandle.windowState;
      if (paramInputWindowHandle != null)
      {
        Slog.i("WindowManager", "WINDOW DIED " + paramInputWindowHandle);
        this.mService.removeWindowLocked(paramInputWindowHandle);
      }
      return;
    }
  }
  
  public void notifyLidSwitchChanged(long paramLong, boolean paramBoolean)
  {
    this.mService.mPolicy.notifyLidSwitchChanged(paramLong, paramBoolean);
  }
  
  public void pauseDispatchingLw(WindowToken paramWindowToken)
  {
    if (!paramWindowToken.paused)
    {
      if (WindowManagerDebugConfig.DEBUG_INPUT) {
        Slog.v("WindowManager", "Pausing WindowToken " + paramWindowToken);
      }
      paramWindowToken.paused = true;
      updateInputWindowsLw(true);
    }
  }
  
  public void resumeDispatchingLw(WindowToken paramWindowToken)
  {
    if (paramWindowToken.paused)
    {
      if (WindowManagerDebugConfig.DEBUG_INPUT) {
        Slog.v("WindowManager", "Resuming WindowToken " + paramWindowToken);
      }
      paramWindowToken.paused = false;
      updateInputWindowsLw(true);
    }
  }
  
  public void setEventDispatchingLw(boolean paramBoolean)
  {
    if (this.mInputDispatchEnabled != paramBoolean)
    {
      if (WindowManagerDebugConfig.DEBUG_INPUT) {
        Slog.v("WindowManager", "Setting event dispatching to " + paramBoolean);
      }
      this.mInputDispatchEnabled = paramBoolean;
      updateInputDispatchModeLw();
    }
  }
  
  public void setFocusedAppLw(AppWindowToken paramAppWindowToken)
  {
    if (paramAppWindowToken == null)
    {
      this.mService.mInputManager.setFocusedApplication(null);
      return;
    }
    InputApplicationHandle localInputApplicationHandle = paramAppWindowToken.mInputApplicationHandle;
    localInputApplicationHandle.name = paramAppWindowToken.toString();
    localInputApplicationHandle.dispatchingTimeoutNanos = paramAppWindowToken.inputDispatchingTimeoutNanos;
    this.mService.mInputManager.setFocusedApplication(localInputApplicationHandle);
  }
  
  public void setInputFocusLw(WindowState paramWindowState, boolean paramBoolean)
  {
    if ((WindowManagerDebugConfig.DEBUG_FOCUS_LIGHT) || (WindowManagerDebugConfig.DEBUG_INPUT)) {
      Slog.d("WindowManager", "Input focus has changed to " + paramWindowState);
    }
    if (paramWindowState != this.mInputFocus)
    {
      if ((paramWindowState != null) && (paramWindowState.canReceiveKeys())) {
        paramWindowState.mToken.paused = false;
      }
      this.mInputFocus = paramWindowState;
      setUpdateInputWindowsNeededLw();
      if (paramBoolean) {
        updateInputWindowsLw(false);
      }
    }
  }
  
  public void setUpdateInputWindowsNeededLw()
  {
    this.mUpdateInputWindowsNeeded = true;
  }
  
  public void thawInputDispatchingLw()
  {
    if (this.mInputDispatchFrozen)
    {
      if (WindowManagerDebugConfig.DEBUG_INPUT) {
        Slog.v("WindowManager", "Thawing input dispatching");
      }
      this.mInputDispatchFrozen = false;
      this.mInputFreezeReason = null;
      updateInputDispatchModeLw();
    }
  }
  
  public void updateInputWindowsLw(boolean paramBoolean)
  {
    int m;
    int n;
    Object localObject;
    label75:
    int i;
    label87:
    label129:
    label141:
    int j;
    label153:
    int i5;
    int i1;
    int k;
    if ((paramBoolean) || (this.mUpdateInputWindowsNeeded))
    {
      this.mUpdateInputWindowsNeeded = false;
      m = 0;
      if (this.mService.mDragState == null) {
        break label303;
      }
      n = 1;
      if (n != 0)
      {
        if (WindowManagerDebugConfig.DEBUG_DRAG) {
          Log.d("WindowManager", "Inserting drag window");
        }
        localObject = this.mService.mDragState.mDragWindowHandle;
        if (localObject == null) {
          break label309;
        }
        addInputWindowHandleLw((InputWindowHandle)localObject);
      }
      if (this.mService.mTaskPositioner == null) {
        break label321;
      }
      i = 1;
      if (i != 0)
      {
        if (WindowManagerDebugConfig.DEBUG_TASK_POSITIONING) {
          Log.d("WindowManager", "Inserting window handle for repositioning");
        }
        localObject = this.mService.mTaskPositioner.mDragWindowHandle;
        if (localObject == null) {
          break label326;
        }
        addInputWindowHandleLw((InputWindowHandle)localObject);
      }
      if (this.mService.mInputConsumer == null) {
        break label338;
      }
      i = 1;
      if (this.mService.mWallpaperInputConsumer == null) {
        break label343;
      }
      j = 1;
      i5 = this.mService.mDisplayContents.size();
      localObject = this.mService.mWallpaperControllerLocked;
      i1 = 0;
      k = i;
    }
    for (;;)
    {
      if (i1 >= i5) {
        break label663;
      }
      WindowList localWindowList = ((DisplayContent)this.mService.mDisplayContents.valueAt(i1)).getWindowList();
      int i2 = localWindowList.size() - 1;
      i = m;
      m = j;
      label222:
      if (i2 >= 0)
      {
        WindowState localWindowState = (WindowState)localWindowList.get(i2);
        InputChannel localInputChannel = localWindowState.mInputChannel;
        InputWindowHandle localInputWindowHandle = localWindowState.mInputWindowHandle;
        j = k;
        int i3 = m;
        int i4 = i;
        if (localInputChannel != null)
        {
          if (localInputWindowHandle != null) {
            break label348;
          }
          i4 = i;
          i3 = m;
          j = k;
        }
        label303:
        label309:
        label321:
        label326:
        label338:
        label343:
        label348:
        do
        {
          do
          {
            i2 -= 1;
            k = j;
            m = i3;
            i = i4;
            break label222;
            return;
            n = 0;
            break;
            Slog.w("WindowManager", "Drag is in progress but there is no drag window handle.");
            break label75;
            i = 0;
            break label87;
            Slog.e("WindowManager", "Repositioning is in progress but there is no drag window handle.");
            break label129;
            i = 0;
            break label141;
            j = 0;
            break label153;
            j = k;
            i3 = m;
            i4 = i;
          } while (localWindowState.mRemoved);
          j = k;
          i3 = m;
          i4 = i;
        } while (localWindowState.isAdjustedForMinimizedDock());
        j = k;
        if (k != 0)
        {
          j = k;
          if (localInputWindowHandle.layer <= this.mService.mInputConsumer.mWindowHandle.layer)
          {
            addInputWindowHandleLw(this.mService.mInputConsumer.mWindowHandle);
            j = 0;
          }
        }
        k = m;
        if (m != 0)
        {
          k = m;
          if (localWindowState.mAttrs.type == 2013)
          {
            addInputWindowHandleLw(this.mService.mWallpaperInputConsumer.mWindowHandle);
            k = 0;
          }
        }
        i3 = localWindowState.mAttrs.flags;
        m = localWindowState.mAttrs.privateFlags;
        i4 = localWindowState.mAttrs.type;
        boolean bool1;
        label518:
        boolean bool2;
        if (localWindowState == this.mInputFocus)
        {
          bool1 = true;
          bool2 = localWindowState.isVisibleLw();
          if ((m & 0x800) != 0) {
            i = 1;
          }
          if ((!((WallpaperController)localObject).isWallpaperTarget(localWindowState)) || ((m & 0x400) != 0)) {
            break label637;
          }
          if (i == 0) {
            break label632;
          }
          paramBoolean = false;
          label561:
          if (localWindowState.getDisplayId() != 0) {
            break label642;
          }
        }
        label632:
        label637:
        label642:
        for (m = 1;; m = 0)
        {
          if ((n != 0) && (bool2) && (m != 0)) {
            this.mService.mDragState.sendDragStartedIfNeededLw(localWindowState);
          }
          addInputWindowHandleLw(localInputWindowHandle, localWindowState, i3, i4, bool2, bool1, paramBoolean);
          i3 = k;
          i4 = i;
          break;
          bool1 = false;
          break label518;
          paramBoolean = true;
          break label561;
          paramBoolean = false;
          break label561;
        }
      }
      i1 += 1;
      j = m;
      m = i;
    }
    label663:
    if (j != 0) {
      addInputWindowHandleLw(this.mService.mWallpaperInputConsumer.mWindowHandle);
    }
    this.mService.mInputManager.setInputWindows(this.mInputWindowHandles);
    clearInputWindowHandlesLw();
  }
  
  public boolean waitForInputDevicesReady(long paramLong)
  {
    boolean bool;
    synchronized (this.mInputDevicesReadyMonitor)
    {
      bool = this.mInputDevicesReady;
      if (bool) {}
    }
    try
    {
      this.mInputDevicesReadyMonitor.wait(paramLong);
      bool = this.mInputDevicesReady;
      return bool;
      localObject2 = finally;
      throw ((Throwable)localObject2);
    }
    catch (InterruptedException localInterruptedException)
    {
      for (;;) {}
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/InputMonitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */