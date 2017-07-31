package com.android.server.wm;

import android.content.ClipData;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.ArraySet;
import android.util.Slog;
import android.view.Display;
import android.view.IWindow;
import android.view.IWindowId;
import android.view.IWindowSession.Stub;
import android.view.IWindowSessionCallback;
import android.view.InputChannel;
import android.view.Surface;
import android.view.SurfaceControl;
import android.view.SurfaceSession;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManagerPolicy;
import com.android.internal.view.IInputContext;
import com.android.internal.view.IInputMethodClient;
import com.android.internal.view.IInputMethodManager;
import com.android.server.input.InputManagerService;
import java.io.PrintWriter;

final class Session
  extends IWindowSession.Stub
  implements IBinder.DeathRecipient
{
  final IWindowSessionCallback mCallback;
  final IInputMethodClient mClient;
  boolean mClientDead;
  final IInputContext mInputContext;
  float mLastReportedAnimatorScale;
  int mNumWindow;
  final int mPid;
  final WindowManagerService mService;
  final String mStringName;
  SurfaceSession mSurfaceSession;
  final int mUid;
  
  /* Error */
  public Session(WindowManagerService paramWindowManagerService, IWindowSessionCallback paramIWindowSessionCallback, IInputMethodClient paramIInputMethodClient, IInputContext paramIInputContext)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 33	android/view/IWindowSession$Stub:<init>	()V
    //   4: aload_0
    //   5: iconst_0
    //   6: putfield 35	com/android/server/wm/Session:mNumWindow	I
    //   9: aload_0
    //   10: iconst_0
    //   11: putfield 37	com/android/server/wm/Session:mClientDead	Z
    //   14: aload_0
    //   15: aload_1
    //   16: putfield 39	com/android/server/wm/Session:mService	Lcom/android/server/wm/WindowManagerService;
    //   19: aload_0
    //   20: aload_2
    //   21: putfield 41	com/android/server/wm/Session:mCallback	Landroid/view/IWindowSessionCallback;
    //   24: aload_0
    //   25: aload_3
    //   26: putfield 43	com/android/server/wm/Session:mClient	Lcom/android/internal/view/IInputMethodClient;
    //   29: aload_0
    //   30: aload 4
    //   32: putfield 45	com/android/server/wm/Session:mInputContext	Lcom/android/internal/view/IInputContext;
    //   35: aload_0
    //   36: invokestatic 51	android/os/Binder:getCallingUid	()I
    //   39: putfield 53	com/android/server/wm/Session:mUid	I
    //   42: aload_0
    //   43: invokestatic 56	android/os/Binder:getCallingPid	()I
    //   46: putfield 58	com/android/server/wm/Session:mPid	I
    //   49: aload_0
    //   50: aload_1
    //   51: invokevirtual 64	com/android/server/wm/WindowManagerService:getCurrentAnimatorScale	()F
    //   54: putfield 66	com/android/server/wm/Session:mLastReportedAnimatorScale	F
    //   57: new 68	java/lang/StringBuilder
    //   60: dup
    //   61: invokespecial 69	java/lang/StringBuilder:<init>	()V
    //   64: astore_1
    //   65: aload_1
    //   66: ldc 71
    //   68: invokevirtual 75	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   71: pop
    //   72: aload_1
    //   73: aload_0
    //   74: invokestatic 81	java/lang/System:identityHashCode	(Ljava/lang/Object;)I
    //   77: invokestatic 87	java/lang/Integer:toHexString	(I)Ljava/lang/String;
    //   80: invokevirtual 75	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   83: pop
    //   84: aload_1
    //   85: ldc 89
    //   87: invokevirtual 75	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   90: pop
    //   91: aload_1
    //   92: aload_0
    //   93: getfield 58	com/android/server/wm/Session:mPid	I
    //   96: invokevirtual 92	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   99: pop
    //   100: aload_0
    //   101: getfield 53	com/android/server/wm/Session:mUid	I
    //   104: sipush 10000
    //   107: if_icmpge +140 -> 247
    //   110: aload_1
    //   111: ldc 94
    //   113: invokevirtual 75	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   116: pop
    //   117: aload_1
    //   118: aload_0
    //   119: getfield 53	com/android/server/wm/Session:mUid	I
    //   122: invokevirtual 92	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   125: pop
    //   126: aload_1
    //   127: ldc 96
    //   129: invokevirtual 75	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   132: pop
    //   133: aload_0
    //   134: aload_1
    //   135: invokevirtual 100	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   138: putfield 102	com/android/server/wm/Session:mStringName	Ljava/lang/String;
    //   141: aload_0
    //   142: getfield 39	com/android/server/wm/Session:mService	Lcom/android/server/wm/WindowManagerService;
    //   145: getfield 106	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
    //   148: astore_1
    //   149: aload_1
    //   150: monitorenter
    //   151: aload_0
    //   152: getfield 39	com/android/server/wm/Session:mService	Lcom/android/server/wm/WindowManagerService;
    //   155: getfield 110	com/android/server/wm/WindowManagerService:mInputMethodManager	Lcom/android/internal/view/IInputMethodManager;
    //   158: ifnonnull +30 -> 188
    //   161: aload_0
    //   162: getfield 39	com/android/server/wm/Session:mService	Lcom/android/server/wm/WindowManagerService;
    //   165: getfield 113	com/android/server/wm/WindowManagerService:mHaveInputMethods	Z
    //   168: ifeq +20 -> 188
    //   171: ldc 115
    //   173: invokestatic 121	android/os/ServiceManager:getService	(Ljava/lang/String;)Landroid/os/IBinder;
    //   176: astore_2
    //   177: aload_0
    //   178: getfield 39	com/android/server/wm/Session:mService	Lcom/android/server/wm/WindowManagerService;
    //   181: aload_2
    //   182: invokestatic 127	com/android/internal/view/IInputMethodManager$Stub:asInterface	(Landroid/os/IBinder;)Lcom/android/internal/view/IInputMethodManager;
    //   185: putfield 110	com/android/server/wm/WindowManagerService:mInputMethodManager	Lcom/android/internal/view/IInputMethodManager;
    //   188: aload_1
    //   189: monitorexit
    //   190: invokestatic 131	android/os/Binder:clearCallingIdentity	()J
    //   193: lstore 5
    //   195: aload_0
    //   196: getfield 39	com/android/server/wm/Session:mService	Lcom/android/server/wm/WindowManagerService;
    //   199: getfield 110	com/android/server/wm/WindowManagerService:mInputMethodManager	Lcom/android/internal/view/IInputMethodManager;
    //   202: ifnull +91 -> 293
    //   205: aload_0
    //   206: getfield 39	com/android/server/wm/Session:mService	Lcom/android/server/wm/WindowManagerService;
    //   209: getfield 110	com/android/server/wm/WindowManagerService:mInputMethodManager	Lcom/android/internal/view/IInputMethodManager;
    //   212: aload_3
    //   213: aload 4
    //   215: aload_0
    //   216: getfield 53	com/android/server/wm/Session:mUid	I
    //   219: aload_0
    //   220: getfield 58	com/android/server/wm/Session:mPid	I
    //   223: invokeinterface 137 5 0
    //   228: aload_3
    //   229: invokeinterface 143 1 0
    //   234: aload_0
    //   235: iconst_0
    //   236: invokeinterface 149 3 0
    //   241: lload 5
    //   243: invokestatic 153	android/os/Binder:restoreCallingIdentity	(J)V
    //   246: return
    //   247: aload_1
    //   248: ldc -101
    //   250: invokevirtual 75	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   253: pop
    //   254: aload_1
    //   255: aload_0
    //   256: getfield 53	com/android/server/wm/Session:mUid	I
    //   259: invokestatic 161	android/os/UserHandle:getUserId	(I)I
    //   262: invokevirtual 92	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   265: pop
    //   266: aload_1
    //   267: bipush 97
    //   269: invokevirtual 164	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
    //   272: pop
    //   273: aload_1
    //   274: aload_0
    //   275: getfield 53	com/android/server/wm/Session:mUid	I
    //   278: invokestatic 167	android/os/UserHandle:getAppId	(I)I
    //   281: invokevirtual 92	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   284: pop
    //   285: goto -159 -> 126
    //   288: astore_2
    //   289: aload_1
    //   290: monitorexit
    //   291: aload_2
    //   292: athrow
    //   293: aload_3
    //   294: iconst_0
    //   295: invokeinterface 171 2 0
    //   300: goto -72 -> 228
    //   303: astore_1
    //   304: aload_0
    //   305: getfield 39	com/android/server/wm/Session:mService	Lcom/android/server/wm/WindowManagerService;
    //   308: getfield 110	com/android/server/wm/WindowManagerService:mInputMethodManager	Lcom/android/internal/view/IInputMethodManager;
    //   311: ifnull +16 -> 327
    //   314: aload_0
    //   315: getfield 39	com/android/server/wm/Session:mService	Lcom/android/server/wm/WindowManagerService;
    //   318: getfield 110	com/android/server/wm/WindowManagerService:mInputMethodManager	Lcom/android/internal/view/IInputMethodManager;
    //   321: aload_3
    //   322: invokeinterface 175 2 0
    //   327: lload 5
    //   329: invokestatic 153	android/os/Binder:restoreCallingIdentity	(J)V
    //   332: return
    //   333: astore_1
    //   334: lload 5
    //   336: invokestatic 153	android/os/Binder:restoreCallingIdentity	(J)V
    //   339: aload_1
    //   340: athrow
    //   341: astore_1
    //   342: goto -15 -> 327
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	345	0	this	Session
    //   0	345	1	paramWindowManagerService	WindowManagerService
    //   0	345	2	paramIWindowSessionCallback	IWindowSessionCallback
    //   0	345	3	paramIInputMethodClient	IInputMethodClient
    //   0	345	4	paramIInputContext	IInputContext
    //   193	142	5	l	long
    // Exception table:
    //   from	to	target	type
    //   151	188	288	finally
    //   195	228	303	android/os/RemoteException
    //   228	241	303	android/os/RemoteException
    //   293	300	303	android/os/RemoteException
    //   195	228	333	finally
    //   228	241	333	finally
    //   293	300	333	finally
    //   304	327	333	finally
    //   304	327	341	android/os/RemoteException
  }
  
  public int add(IWindow paramIWindow, int paramInt1, WindowManager.LayoutParams paramLayoutParams, int paramInt2, Rect paramRect1, Rect paramRect2, InputChannel paramInputChannel)
  {
    return addToDisplay(paramIWindow, paramInt1, paramLayoutParams, paramInt2, 0, paramRect1, paramRect2, null, paramInputChannel);
  }
  
  public int addToDisplay(IWindow paramIWindow, int paramInt1, WindowManager.LayoutParams paramLayoutParams, int paramInt2, int paramInt3, Rect paramRect1, Rect paramRect2, Rect paramRect3, InputChannel paramInputChannel)
  {
    return this.mService.addWindow(this, paramIWindow, paramInt1, paramLayoutParams, paramInt2, paramInt3, paramRect1, paramRect2, paramRect3, paramInputChannel);
  }
  
  public int addToDisplayWithoutInputChannel(IWindow paramIWindow, int paramInt1, WindowManager.LayoutParams paramLayoutParams, int paramInt2, int paramInt3, Rect paramRect1, Rect paramRect2)
  {
    return this.mService.addWindow(this, paramIWindow, paramInt1, paramLayoutParams, paramInt2, paramInt3, paramRect1, paramRect2, null, null);
  }
  
  public int addWithoutInputChannel(IWindow paramIWindow, int paramInt1, WindowManager.LayoutParams paramLayoutParams, int paramInt2, Rect paramRect1, Rect paramRect2)
  {
    return addToDisplayWithoutInputChannel(paramIWindow, paramInt1, paramLayoutParams, paramInt2, 0, paramRect1, paramRect2);
  }
  
  public void binderDied()
  {
    try
    {
      if (this.mService.mInputMethodManager != null) {
        this.mService.mInputMethodManager.removeClient(this.mClient);
      }
      synchronized (this.mService.mWindowMap)
      {
        this.mClient.asBinder().unlinkToDeath(this, 0);
        this.mClientDead = true;
        killSessionLocked();
        return;
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;) {}
    }
  }
  
  public void cancelDragAndDrop(IBinder paramIBinder)
  {
    if (WindowManagerDebugConfig.DEBUG_DRAG) {
      Slog.d("WindowManager", "cancelDragAndDrop");
    }
    long l;
    synchronized (this.mService.mWindowMap)
    {
      l = Binder.clearCallingIdentity();
      try
      {
        if (this.mService.mDragState == null)
        {
          Slog.w("WindowManager", "cancelDragAndDrop() without prepareDrag()");
          throw new IllegalStateException("cancelDragAndDrop() without prepareDrag()");
        }
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    if (this.mService.mDragState.mToken != paramIBinder)
    {
      Slog.w("WindowManager", "cancelDragAndDrop() does not match prepareDrag()");
      throw new IllegalStateException("cancelDragAndDrop() does not match prepareDrag()");
    }
    this.mService.mDragState.mDragResult = false;
    this.mService.mDragState.cancelDragLw();
    Binder.restoreCallingIdentity(l);
  }
  
  public void dragRecipientEntered(IWindow paramIWindow)
  {
    if (WindowManagerDebugConfig.DEBUG_DRAG) {
      Slog.d("WindowManager", "Drag into new candidate view @ " + paramIWindow.asBinder());
    }
  }
  
  public void dragRecipientExited(IWindow paramIWindow)
  {
    if (WindowManagerDebugConfig.DEBUG_DRAG) {
      Slog.d("WindowManager", "Drag from old candidate view @ " + paramIWindow.asBinder());
    }
  }
  
  void dump(PrintWriter paramPrintWriter, String paramString)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mNumWindow=");
    paramPrintWriter.print(this.mNumWindow);
    paramPrintWriter.print(" mClientDead=");
    paramPrintWriter.print(this.mClientDead);
    paramPrintWriter.print(" mSurfaceSession=");
    paramPrintWriter.println(this.mSurfaceSession);
  }
  
  public void finishDrawing(IWindow paramIWindow)
  {
    if (WindowManagerService.localLOGV) {
      Slog.v("WindowManager", "IWindow finishDrawing called for " + paramIWindow);
    }
    this.mService.finishDrawingWindow(this, paramIWindow);
  }
  
  public void getDisplayFrame(IWindow paramIWindow, Rect paramRect)
  {
    this.mService.getWindowDisplayFrame(this, paramIWindow, paramRect);
  }
  
  public boolean getInTouchMode()
  {
    synchronized (this.mService.mWindowMap)
    {
      boolean bool = this.mService.mInTouchMode;
      return bool;
    }
  }
  
  public IWindowId getWindowId(IBinder paramIBinder)
  {
    return this.mService.getWindowId(paramIBinder);
  }
  
  void killSessionLocked()
  {
    if ((this.mNumWindow <= 0) && (this.mClientDead))
    {
      this.mService.mSessions.remove(this);
      if (this.mSurfaceSession != null)
      {
        if (WindowManagerService.localLOGV) {
          Slog.v("WindowManager", "Last window removed from " + this + ", destroying " + this.mSurfaceSession);
        }
        if (WindowManagerDebugConfig.SHOW_TRANSACTIONS) {
          Slog.i("WindowManager", "  KILL SURFACE SESSION " + this.mSurfaceSession);
        }
      }
    }
    try
    {
      this.mSurfaceSession.kill();
      this.mSurfaceSession = null;
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        Slog.w("WindowManager", "Exception thrown when killing surface session " + this.mSurfaceSession + " in session " + this + ": " + localException.toString());
      }
    }
  }
  
  public void onRectangleOnScreenRequested(IBinder paramIBinder, Rect paramRect)
  {
    synchronized (this.mService.mWindowMap)
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        this.mService.onRectangleOnScreenRequested(paramIBinder, paramRect);
        Binder.restoreCallingIdentity(l);
        return;
      }
      finally
      {
        paramIBinder = finally;
        Binder.restoreCallingIdentity(l);
        throw paramIBinder;
      }
    }
  }
  
  public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
    throws RemoteException
  {
    try
    {
      boolean bool = super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      return bool;
    }
    catch (RuntimeException paramParcel1)
    {
      if (!(paramParcel1 instanceof SecurityException)) {
        Slog.wtf("WindowManager", "Window Session Crash", paramParcel1);
      }
      throw paramParcel1;
    }
  }
  
  public boolean outOfMemory(IWindow paramIWindow)
  {
    return this.mService.outOfMemoryWindow(this, paramIWindow);
  }
  
  public void performDeferredDestroy(IWindow paramIWindow)
  {
    this.mService.performDeferredDestroyWindow(this, paramIWindow);
  }
  
  public boolean performDrag(IWindow paramIWindow, IBinder paramIBinder, int paramInt, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, ClipData paramClipData)
  {
    if (WindowManagerDebugConfig.DEBUG_DRAG) {
      Slog.d("WindowManager", "perform drag: win=" + paramIWindow + " data=" + paramClipData);
    }
    synchronized (this.mService.mWindowMap)
    {
      if (this.mService.mDragState == null)
      {
        Slog.w("WindowManager", "No drag prepared");
        throw new IllegalStateException("performDrag() without prepareDrag()");
      }
    }
    if (paramIBinder != this.mService.mDragState.mToken)
    {
      Slog.w("WindowManager", "Performing mismatched drag");
      throw new IllegalStateException("performDrag() does not match prepareDrag()");
    }
    paramIBinder = this.mService.windowForClientLocked(null, paramIWindow, false);
    if (paramIBinder == null)
    {
      Slog.w("WindowManager", "Bad requesting window " + paramIWindow);
      return false;
    }
    this.mService.mH.removeMessages(20, paramIWindow.asBinder());
    paramIWindow = paramIBinder.getDisplayContent();
    if (paramIWindow == null) {
      return false;
    }
    paramIWindow = paramIWindow.getDisplay();
    this.mService.mDragState.register(paramIWindow);
    this.mService.mInputMonitor.updateInputWindowsLw(true);
    if (!this.mService.mInputManager.transferTouchFocus(paramIBinder.mInputChannel, this.mService.mDragState.mServerChannel))
    {
      Slog.e("WindowManager", "Unable to transfer touch focus");
      this.mService.mDragState.unregister();
      this.mService.mDragState = null;
      this.mService.mInputMonitor.updateInputWindowsLw(true);
      return false;
    }
    this.mService.mDragState.mData = paramClipData;
    this.mService.mDragState.broadcastDragStartedLw(paramFloat1, paramFloat2);
    this.mService.mDragState.overridePointerIconLw(paramInt);
    this.mService.mDragState.mThumbOffsetX = paramFloat3;
    this.mService.mDragState.mThumbOffsetY = paramFloat4;
    paramIBinder = this.mService.mDragState.mSurfaceControl;
    if (WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
      Slog.i("WindowManager", ">>> OPEN TRANSACTION performDrag");
    }
    SurfaceControl.openTransaction();
    try
    {
      paramIBinder.setPosition(paramFloat1 - paramFloat3, paramFloat2 - paramFloat4);
      paramIBinder.setLayer(this.mService.mDragState.getDragLayerLw());
      paramIBinder.setLayerStack(paramIWindow.getLayerStack());
      paramIBinder.show();
      SurfaceControl.closeTransaction();
      if (WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
        Slog.i("WindowManager", "<<< CLOSE TRANSACTION performDrag");
      }
      this.mService.mDragState.notifyLocationLw(paramFloat1, paramFloat2);
      return true;
    }
    finally
    {
      paramIWindow = finally;
      SurfaceControl.closeTransaction();
      if (WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
        Slog.i("WindowManager", "<<< CLOSE TRANSACTION performDrag");
      }
      throw paramIWindow;
    }
  }
  
  public boolean performHapticFeedback(IWindow paramIWindow, int paramInt, boolean paramBoolean)
  {
    synchronized (this.mService.mWindowMap)
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        paramBoolean = this.mService.mPolicy.performHapticFeedbackLw(this.mService.windowForClientLocked(this, paramIWindow, true), paramInt, paramBoolean);
        Binder.restoreCallingIdentity(l);
        return paramBoolean;
      }
      finally
      {
        paramIWindow = finally;
        Binder.restoreCallingIdentity(l);
        throw paramIWindow;
      }
    }
  }
  
  public void pokeDrawLock(IBinder paramIBinder)
  {
    long l = Binder.clearCallingIdentity();
    try
    {
      this.mService.pokeDrawLock(this, paramIBinder);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public IBinder prepareDrag(IWindow paramIWindow, int paramInt1, int paramInt2, int paramInt3, Surface paramSurface)
  {
    return this.mService.prepareDragSurface(paramIWindow, this.mSurfaceSession, paramInt1, paramInt2, paramInt3, paramSurface);
  }
  
  public void prepareToReplaceWindows(IBinder paramIBinder, boolean paramBoolean)
  {
    this.mService.setReplacingWindows(paramIBinder, paramBoolean);
  }
  
  public int relayout(IWindow paramIWindow, int paramInt1, WindowManager.LayoutParams paramLayoutParams, int paramInt2, int paramInt3, int paramInt4, int paramInt5, Rect paramRect1, Rect paramRect2, Rect paramRect3, Rect paramRect4, Rect paramRect5, Rect paramRect6, Rect paramRect7, Configuration paramConfiguration, Surface paramSurface)
  {
    return this.mService.relayoutWindow(this, paramIWindow, paramInt1, paramLayoutParams, paramInt2, paramInt3, paramInt4, paramInt5, paramRect1, paramRect2, paramRect3, paramRect4, paramRect5, paramRect6, paramRect7, paramConfiguration, paramSurface);
  }
  
  public void remove(IWindow paramIWindow)
  {
    this.mService.removeWindow(this, paramIWindow);
  }
  
  public void reportDropResult(IWindow paramIWindow, boolean paramBoolean)
  {
    IBinder localIBinder = paramIWindow.asBinder();
    if (WindowManagerDebugConfig.DEBUG_DRAG) {
      Slog.d("WindowManager", "Drop result=" + paramBoolean + " reported by " + localIBinder);
    }
    long l;
    synchronized (this.mService.mWindowMap)
    {
      l = Binder.clearCallingIdentity();
      try
      {
        if (this.mService.mDragState == null)
        {
          Slog.w("WindowManager", "Drop result given but no drag in progress");
          Binder.restoreCallingIdentity(l);
          return;
        }
        if (this.mService.mDragState.mToken != localIBinder)
        {
          Slog.w("WindowManager", "Invalid drop-result claim by " + paramIWindow);
          throw new IllegalStateException("reportDropResult() by non-recipient");
        }
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    this.mService.mH.removeMessages(21, paramIWindow.asBinder());
    if (this.mService.windowForClientLocked(null, paramIWindow, false) == null)
    {
      Slog.w("WindowManager", "Bad result-reporting window " + paramIWindow);
      Binder.restoreCallingIdentity(l);
      return;
    }
    this.mService.mDragState.mDragResult = paramBoolean;
    this.mService.mDragState.endDragLw();
    Binder.restoreCallingIdentity(l);
  }
  
  public void repositionChild(IWindow paramIWindow, int paramInt1, int paramInt2, int paramInt3, int paramInt4, long paramLong, Rect paramRect)
  {
    this.mService.repositionChild(this, paramIWindow, paramInt1, paramInt2, paramInt3, paramInt4, paramLong, paramRect);
  }
  
  public Bundle sendWallpaperCommand(IBinder paramIBinder, String paramString, int paramInt1, int paramInt2, int paramInt3, Bundle paramBundle, boolean paramBoolean)
  {
    synchronized (this.mService.mWindowMap)
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        paramIBinder = this.mService.mWallpaperControllerLocked.sendWindowWallpaperCommand(this.mService.windowForClientLocked(this, paramIBinder, true), paramString, paramInt1, paramInt2, paramInt3, paramBundle, paramBoolean);
        Binder.restoreCallingIdentity(l);
        return paramIBinder;
      }
      finally
      {
        paramIBinder = finally;
        Binder.restoreCallingIdentity(l);
        throw paramIBinder;
      }
    }
  }
  
  public void setInTouchMode(boolean paramBoolean)
  {
    synchronized (this.mService.mWindowMap)
    {
      this.mService.mInTouchMode = paramBoolean;
      return;
    }
  }
  
  public void setInsets(IWindow paramIWindow, int paramInt, Rect paramRect1, Rect paramRect2, Region paramRegion)
  {
    this.mService.setInsetsWindow(this, paramIWindow, paramInt, paramRect1, paramRect2, paramRegion);
  }
  
  public void setTransparentRegion(IWindow paramIWindow, Region paramRegion)
  {
    this.mService.setTransparentRegionWindow(this, paramIWindow, paramRegion);
  }
  
  public void setWallpaperDisplayOffset(IBinder paramIBinder, int paramInt1, int paramInt2)
  {
    synchronized (this.mService.mWindowMap)
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        this.mService.mWallpaperControllerLocked.setWindowWallpaperDisplayOffset(this.mService.windowForClientLocked(this, paramIBinder, true), paramInt1, paramInt2);
        Binder.restoreCallingIdentity(l);
        return;
      }
      finally
      {
        paramIBinder = finally;
        Binder.restoreCallingIdentity(l);
        throw paramIBinder;
      }
    }
  }
  
  public void setWallpaperPosition(IBinder paramIBinder, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    synchronized (this.mService.mWindowMap)
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        this.mService.mWallpaperControllerLocked.setWindowWallpaperPosition(this.mService.windowForClientLocked(this, paramIBinder, true), paramFloat1, paramFloat2, paramFloat3, paramFloat4);
        Binder.restoreCallingIdentity(l);
        return;
      }
      finally
      {
        paramIBinder = finally;
        Binder.restoreCallingIdentity(l);
        throw paramIBinder;
      }
    }
  }
  
  public boolean startMovingTask(IWindow paramIWindow, float paramFloat1, float paramFloat2)
  {
    if (WindowManagerDebugConfig.DEBUG_TASK_POSITIONING) {
      Slog.d("WindowManager", "startMovingTask: {" + paramFloat1 + "," + paramFloat2 + "}");
    }
    long l = Binder.clearCallingIdentity();
    try
    {
      boolean bool = this.mService.startMovingTask(paramIWindow, paramFloat1, paramFloat2);
      return bool;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public String toString()
  {
    return this.mStringName;
  }
  
  public void updatePointerIcon(IWindow paramIWindow)
  {
    long l = Binder.clearCallingIdentity();
    try
    {
      this.mService.updatePointerIcon(paramIWindow);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public void wallpaperCommandComplete(IBinder paramIBinder, Bundle arg2)
  {
    synchronized (this.mService.mWindowMap)
    {
      this.mService.mWallpaperControllerLocked.wallpaperCommandComplete(paramIBinder);
      return;
    }
  }
  
  public void wallpaperOffsetsComplete(IBinder paramIBinder)
  {
    synchronized (this.mService.mWindowMap)
    {
      this.mService.mWallpaperControllerLocked.wallpaperOffsetsComplete(paramIBinder);
      return;
    }
  }
  
  void windowAddedLocked()
  {
    if (this.mSurfaceSession == null)
    {
      if (WindowManagerService.localLOGV) {
        Slog.v("WindowManager", "First window added to " + this + ", creating SurfaceSession");
      }
      this.mSurfaceSession = new SurfaceSession();
      if (WindowManagerDebugConfig.SHOW_TRANSACTIONS) {
        Slog.i("WindowManager", "  NEW SURFACE SESSION " + this.mSurfaceSession);
      }
      this.mService.mSessions.add(this);
      if (this.mLastReportedAnimatorScale != this.mService.getCurrentAnimatorScale()) {
        this.mService.dispatchNewAnimatorScaleLocked(this);
      }
    }
    this.mNumWindow += 1;
  }
  
  void windowRemovedLocked()
  {
    this.mNumWindow -= 1;
    killSessionLocked();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/Session.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */