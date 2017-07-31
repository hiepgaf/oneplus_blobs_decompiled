package android.service.dreams;

import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.IRemoteCallback;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.MathUtils;
import android.util.Slog;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.Window.Callback;
import android.view.WindowManager;
import android.view.WindowManager.BadTokenException;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManagerGlobal;
import android.view.accessibility.AccessibilityEvent;
import com.android.internal.policy.PhoneWindow;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.DumpUtils.Dump;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class DreamService
  extends Service
  implements Window.Callback
{
  public static final String DREAM_META_DATA = "android.service.dream";
  public static final String DREAM_SERVICE = "dreams";
  public static final String SERVICE_INTERFACE = "android.service.dreams.DreamService";
  private final String TAG = DreamService.class.getSimpleName() + "[" + getClass().getSimpleName() + "]";
  private boolean mCanDoze;
  private boolean mDebug = false;
  private int mDozeScreenBrightness = -1;
  private int mDozeScreenState = 0;
  private boolean mDozing;
  private boolean mFinished;
  private boolean mFullscreen;
  private final Handler mHandler = new Handler();
  private boolean mInteractive;
  private boolean mLowProfile = true;
  private final IDreamManager mSandman = IDreamManager.Stub.asInterface(ServiceManager.getService("dreams"));
  private boolean mScreenBright = true;
  private boolean mStarted;
  private boolean mWaking;
  private Window mWindow;
  private IBinder mWindowToken;
  private boolean mWindowless;
  
  private int applyFlags(int paramInt1, int paramInt2, int paramInt3)
  {
    return paramInt3 & paramInt1 | paramInt2 & paramInt3;
  }
  
  private void applySystemUiVisibilityFlags(int paramInt1, int paramInt2)
  {
    View localView = null;
    if (this.mWindow == null) {}
    for (;;)
    {
      if (localView != null) {
        localView.setSystemUiVisibility(applyFlags(localView.getSystemUiVisibility(), paramInt1, paramInt2));
      }
      return;
      localView = this.mWindow.getDecorView();
    }
  }
  
  private void applyWindowFlags(int paramInt1, int paramInt2)
  {
    if (this.mWindow != null)
    {
      WindowManager.LayoutParams localLayoutParams = this.mWindow.getAttributes();
      localLayoutParams.flags = applyFlags(localLayoutParams.flags, paramInt1, paramInt2);
      this.mWindow.setAttributes(localLayoutParams);
      this.mWindow.getWindowManager().updateViewLayout(this.mWindow.getDecorView(), localLayoutParams);
    }
  }
  
  private final void attach(IBinder paramIBinder, boolean paramBoolean, final IRemoteCallback paramIRemoteCallback)
  {
    int k = 0;
    if (this.mWindowToken != null)
    {
      Slog.e(this.TAG, "attach() called when already attached with token=" + this.mWindowToken);
      return;
    }
    if ((this.mFinished) || (this.mWaking)) {
      Slog.w(this.TAG, "attach() called after dream already finished");
    }
    try
    {
      this.mSandman.finishSelf(paramIBinder, true);
      return;
    }
    catch (RemoteException paramIBinder) {}
    this.mWindowToken = paramIBinder;
    this.mCanDoze = paramBoolean;
    WindowManager.LayoutParams localLayoutParams;
    int m;
    int i;
    if ((!this.mWindowless) || (this.mCanDoze)) {
      if (!this.mWindowless)
      {
        this.mWindow = new PhoneWindow(this);
        this.mWindow.setCallback(this);
        this.mWindow.requestFeature(1);
        this.mWindow.setBackgroundDrawable(new ColorDrawable(-16777216));
        this.mWindow.setFormat(-1);
        if (this.mDebug) {
          Slog.v(this.TAG, String.format("Attaching window token: %s to window of type %s", new Object[] { paramIBinder, Integer.valueOf(2023) }));
        }
        localLayoutParams = this.mWindow.getAttributes();
        localLayoutParams.type = 2023;
        localLayoutParams.token = paramIBinder;
        localLayoutParams.windowAnimations = 16974584;
        m = localLayoutParams.flags;
        if (!this.mFullscreen) {
          break label382;
        }
        i = 1024;
        if (!this.mScreenBright) {
          break label388;
        }
      }
    }
    label382:
    label388:
    for (int j = 128;; j = 0)
    {
      localLayoutParams.flags = (j | 0x490101 | i | m);
      this.mWindow.setAttributes(localLayoutParams);
      this.mWindow.clearFlags(Integer.MIN_VALUE);
      this.mWindow.setWindowManager(null, paramIBinder, "dream", true);
      i = k;
      if (this.mLowProfile) {
        i = 1;
      }
      applySystemUiVisibilityFlags(i, 1);
      try
      {
        getWindowManager().addView(this.mWindow.getDecorView(), this.mWindow.getAttributes());
        this.mHandler.post(new Runnable()
        {
          /* Error */
          public void run()
          {
            // Byte code:
            //   0: aload_0
            //   1: getfield 19	android/service/dreams/DreamService$1:this$0	Landroid/service/dreams/DreamService;
            //   4: invokestatic 32	android/service/dreams/DreamService:-get3	(Landroid/service/dreams/DreamService;)Landroid/view/Window;
            //   7: ifnonnull +13 -> 20
            //   10: aload_0
            //   11: getfield 19	android/service/dreams/DreamService$1:this$0	Landroid/service/dreams/DreamService;
            //   14: invokestatic 36	android/service/dreams/DreamService:-get4	(Landroid/service/dreams/DreamService;)Z
            //   17: ifeq +52 -> 69
            //   20: aload_0
            //   21: getfield 19	android/service/dreams/DreamService$1:this$0	Landroid/service/dreams/DreamService;
            //   24: invokestatic 39	android/service/dreams/DreamService:-get1	(Landroid/service/dreams/DreamService;)Z
            //   27: ifeq +16 -> 43
            //   30: aload_0
            //   31: getfield 19	android/service/dreams/DreamService$1:this$0	Landroid/service/dreams/DreamService;
            //   34: invokestatic 43	android/service/dreams/DreamService:-get0	(Landroid/service/dreams/DreamService;)Ljava/lang/String;
            //   37: ldc 45
            //   39: invokestatic 51	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
            //   42: pop
            //   43: aload_0
            //   44: getfield 19	android/service/dreams/DreamService$1:this$0	Landroid/service/dreams/DreamService;
            //   47: iconst_1
            //   48: invokestatic 55	android/service/dreams/DreamService:-set0	(Landroid/service/dreams/DreamService;Z)Z
            //   51: pop
            //   52: aload_0
            //   53: getfield 19	android/service/dreams/DreamService$1:this$0	Landroid/service/dreams/DreamService;
            //   56: invokevirtual 58	android/service/dreams/DreamService:onDreamingStarted	()V
            //   59: aload_0
            //   60: getfield 21	android/service/dreams/DreamService$1:val$started	Landroid/os/IRemoteCallback;
            //   63: aconst_null
            //   64: invokeinterface 64 2 0
            //   69: return
            //   70: astore_1
            //   71: aload_1
            //   72: invokevirtual 68	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
            //   75: athrow
            //   76: astore_1
            //   77: aload_0
            //   78: getfield 21	android/service/dreams/DreamService$1:val$started	Landroid/os/IRemoteCallback;
            //   81: aconst_null
            //   82: invokeinterface 64 2 0
            //   87: aload_1
            //   88: athrow
            //   89: astore_1
            //   90: aload_1
            //   91: invokevirtual 68	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
            //   94: athrow
            // Local variable table:
            //   start	length	slot	name	signature
            //   0	95	0	this	1
            //   70	2	1	localRemoteException1	RemoteException
            //   76	12	1	localObject	Object
            //   89	2	1	localRemoteException2	RemoteException
            // Exception table:
            //   from	to	target	type
            //   59	69	70	android/os/RemoteException
            //   52	59	76	finally
            //   77	87	89	android/os/RemoteException
          }
        });
        return;
      }
      catch (WindowManager.BadTokenException paramIBinder)
      {
        Slog.i(this.TAG, "attach() called after window token already removed, dream will finish soon");
        this.mWindow = null;
        return;
      }
      throw new IllegalStateException("Only doze dreams can be windowless");
      i = 0;
      break;
    }
  }
  
  private static int clampAbsoluteBrightness(int paramInt)
  {
    return MathUtils.constrain(paramInt, 0, 255);
  }
  
  private final void detach()
  {
    if (this.mStarted)
    {
      if (this.mDebug) {
        Slog.v(this.TAG, "detach(): Calling onDreamingStopped()");
      }
      this.mStarted = false;
      onDreamingStopped();
    }
    if (this.mWindow != null)
    {
      if (this.mDebug) {
        Slog.v(this.TAG, "detach(): Removing window from window manager");
      }
      this.mWindow.getWindowManager().removeViewImmediate(this.mWindow.getDecorView());
      this.mWindow = null;
    }
    if (this.mWindowToken != null)
    {
      WindowManagerGlobal.getInstance().closeAll(this.mWindowToken, getClass().getName(), "Dream");
      this.mWindowToken = null;
      this.mCanDoze = false;
    }
  }
  
  private boolean getSystemUiVisibilityFlagValue(int paramInt, boolean paramBoolean)
  {
    View localView = null;
    if (this.mWindow == null) {}
    while (localView == null)
    {
      return paramBoolean;
      localView = this.mWindow.getDecorView();
    }
    return (localView.getSystemUiVisibility() & paramInt) != 0;
  }
  
  private boolean getWindowFlagValue(int paramInt, boolean paramBoolean)
  {
    if (this.mWindow == null) {
      return paramBoolean;
    }
    return (this.mWindow.getAttributes().flags & paramInt) != 0;
  }
  
  private void updateDoze()
  {
    if (this.mDozing) {}
    try
    {
      this.mSandman.startDozing(this.mWindowToken, this.mDozeScreenState, this.mDozeScreenBrightness);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  private void wakeUp(boolean paramBoolean)
  {
    if (this.mDebug) {
      Slog.v(this.TAG, "wakeUp(): fromSystem=" + paramBoolean + ", mWaking=" + this.mWaking + ", mFinished=" + this.mFinished);
    }
    if ((this.mWaking) || (this.mFinished)) {}
    do
    {
      return;
      this.mWaking = true;
      onWakeUp();
    } while ((paramBoolean) || (this.mFinished));
    if (this.mWindowToken == null)
    {
      Slog.w(this.TAG, "WakeUp was called before the dream was attached.");
      return;
    }
    try
    {
      this.mSandman.finishSelf(this.mWindowToken, false);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public void addContentView(View paramView, ViewGroup.LayoutParams paramLayoutParams)
  {
    getWindow().addContentView(paramView, paramLayoutParams);
  }
  
  public boolean canDoze()
  {
    return this.mCanDoze;
  }
  
  public boolean dispatchGenericMotionEvent(MotionEvent paramMotionEvent)
  {
    if (!this.mInteractive)
    {
      if (this.mDebug) {
        Slog.v(this.TAG, "Waking up on genericMotionEvent");
      }
      wakeUp();
      return true;
    }
    return this.mWindow.superDispatchGenericMotionEvent(paramMotionEvent);
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    if (!this.mInteractive)
    {
      if (this.mDebug) {
        Slog.v(this.TAG, "Waking up on keyEvent");
      }
      wakeUp();
      return true;
    }
    if (paramKeyEvent.getKeyCode() == 4)
    {
      if (this.mDebug) {
        Slog.v(this.TAG, "Waking up on back key");
      }
      wakeUp();
      return true;
    }
    return this.mWindow.superDispatchKeyEvent(paramKeyEvent);
  }
  
  public boolean dispatchKeyShortcutEvent(KeyEvent paramKeyEvent)
  {
    if (!this.mInteractive)
    {
      if (this.mDebug) {
        Slog.v(this.TAG, "Waking up on keyShortcutEvent");
      }
      wakeUp();
      return true;
    }
    return this.mWindow.superDispatchKeyShortcutEvent(paramKeyEvent);
  }
  
  public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    return false;
  }
  
  public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
  {
    if (!this.mInteractive)
    {
      if (this.mDebug) {
        Slog.v(this.TAG, "Waking up on touchEvent");
      }
      wakeUp();
      return true;
    }
    return this.mWindow.superDispatchTouchEvent(paramMotionEvent);
  }
  
  public boolean dispatchTrackballEvent(MotionEvent paramMotionEvent)
  {
    if (!this.mInteractive)
    {
      if (this.mDebug) {
        Slog.v(this.TAG, "Waking up on trackballEvent");
      }
      wakeUp();
      return true;
    }
    return this.mWindow.superDispatchTrackballEvent(paramMotionEvent);
  }
  
  protected void dump(final FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, final String[] paramArrayOfString)
  {
    DumpUtils.dumpAsync(this.mHandler, new DumpUtils.Dump()
    {
      public void dump(PrintWriter paramAnonymousPrintWriter, String paramAnonymousString)
      {
        DreamService.this.dumpOnHandler(paramFileDescriptor, paramAnonymousPrintWriter, paramArrayOfString);
      }
    }, paramPrintWriter, "", 1000L);
  }
  
  protected void dumpOnHandler(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.print(this.TAG + ": ");
    if (this.mWindowToken == null)
    {
      paramPrintWriter.println("stopped");
      paramPrintWriter.println("  window: " + this.mWindow);
      paramPrintWriter.print("  flags:");
      if (isInteractive()) {
        paramPrintWriter.print(" interactive");
      }
      if (isLowProfile()) {
        paramPrintWriter.print(" lowprofile");
      }
      if (isFullscreen()) {
        paramPrintWriter.print(" fullscreen");
      }
      if (isScreenBright()) {
        paramPrintWriter.print(" bright");
      }
      if (isWindowless()) {
        paramPrintWriter.print(" windowless");
      }
      if (!isDozing()) {
        break label264;
      }
      paramPrintWriter.print(" dozing");
    }
    for (;;)
    {
      paramPrintWriter.println();
      if (canDoze())
      {
        paramPrintWriter.println("  doze screen state: " + Display.stateToString(this.mDozeScreenState));
        paramPrintWriter.println("  doze screen brightness: " + this.mDozeScreenBrightness);
      }
      return;
      paramPrintWriter.println("running (token=" + this.mWindowToken + ")");
      break;
      label264:
      if (canDoze()) {
        paramPrintWriter.print(" candoze");
      }
    }
  }
  
  public View findViewById(int paramInt)
  {
    return getWindow().findViewById(paramInt);
  }
  
  public final void finish()
  {
    if (this.mDebug) {
      Slog.v(this.TAG, "finish(): mFinished=" + this.mFinished);
    }
    if (!this.mFinished)
    {
      this.mFinished = true;
      if (this.mWindowToken != null) {
        break label73;
      }
      Slog.w(this.TAG, "Finish was called before the dream was attached.");
    }
    for (;;)
    {
      stopSelf();
      return;
      try
      {
        label73:
        this.mSandman.finishSelf(this.mWindowToken, true);
      }
      catch (RemoteException localRemoteException) {}
    }
  }
  
  public int getDozeScreenBrightness()
  {
    return this.mDozeScreenBrightness;
  }
  
  public int getDozeScreenState()
  {
    return this.mDozeScreenState;
  }
  
  public Window getWindow()
  {
    return this.mWindow;
  }
  
  public WindowManager getWindowManager()
  {
    WindowManager localWindowManager = null;
    if (this.mWindow != null) {
      localWindowManager = this.mWindow.getWindowManager();
    }
    return localWindowManager;
  }
  
  public boolean isDozing()
  {
    return this.mDozing;
  }
  
  public boolean isFullscreen()
  {
    return this.mFullscreen;
  }
  
  public boolean isInteractive()
  {
    return this.mInteractive;
  }
  
  public boolean isLowProfile()
  {
    return getSystemUiVisibilityFlagValue(1, this.mLowProfile);
  }
  
  public boolean isScreenBright()
  {
    return getWindowFlagValue(128, this.mScreenBright);
  }
  
  public boolean isWindowless()
  {
    return this.mWindowless;
  }
  
  public void onActionModeFinished(ActionMode paramActionMode) {}
  
  public void onActionModeStarted(ActionMode paramActionMode) {}
  
  public void onAttachedToWindow() {}
  
  public final IBinder onBind(Intent paramIntent)
  {
    if (this.mDebug) {
      Slog.v(this.TAG, "onBind() intent = " + paramIntent);
    }
    return new DreamServiceWrapper(null);
  }
  
  public void onContentChanged() {}
  
  public void onCreate()
  {
    if (this.mDebug) {
      Slog.v(this.TAG, "onCreate()");
    }
    super.onCreate();
  }
  
  public boolean onCreatePanelMenu(int paramInt, Menu paramMenu)
  {
    return false;
  }
  
  public View onCreatePanelView(int paramInt)
  {
    return null;
  }
  
  public void onDestroy()
  {
    if (this.mDebug) {
      Slog.v(this.TAG, "onDestroy()");
    }
    detach();
    super.onDestroy();
  }
  
  public void onDetachedFromWindow() {}
  
  public void onDreamingStarted()
  {
    if (this.mDebug) {
      Slog.v(this.TAG, "onDreamingStarted()");
    }
  }
  
  public void onDreamingStopped()
  {
    if (this.mDebug) {
      Slog.v(this.TAG, "onDreamingStopped()");
    }
  }
  
  public boolean onMenuItemSelected(int paramInt, MenuItem paramMenuItem)
  {
    return false;
  }
  
  public boolean onMenuOpened(int paramInt, Menu paramMenu)
  {
    return false;
  }
  
  public void onPanelClosed(int paramInt, Menu paramMenu) {}
  
  public boolean onPreparePanel(int paramInt, View paramView, Menu paramMenu)
  {
    return false;
  }
  
  public boolean onSearchRequested()
  {
    return false;
  }
  
  public boolean onSearchRequested(SearchEvent paramSearchEvent)
  {
    return onSearchRequested();
  }
  
  public void onWakeUp()
  {
    finish();
  }
  
  public void onWindowAttributesChanged(WindowManager.LayoutParams paramLayoutParams) {}
  
  public void onWindowFocusChanged(boolean paramBoolean) {}
  
  public ActionMode onWindowStartingActionMode(ActionMode.Callback paramCallback)
  {
    return null;
  }
  
  public ActionMode onWindowStartingActionMode(ActionMode.Callback paramCallback, int paramInt)
  {
    return null;
  }
  
  public void setContentView(int paramInt)
  {
    getWindow().setContentView(paramInt);
  }
  
  public void setContentView(View paramView)
  {
    getWindow().setContentView(paramView);
  }
  
  public void setContentView(View paramView, ViewGroup.LayoutParams paramLayoutParams)
  {
    getWindow().setContentView(paramView, paramLayoutParams);
  }
  
  public void setDebug(boolean paramBoolean)
  {
    this.mDebug = paramBoolean;
  }
  
  public void setDozeScreenBrightness(int paramInt)
  {
    int i = paramInt;
    if (paramInt != -1) {
      i = clampAbsoluteBrightness(paramInt);
    }
    if (this.mDozeScreenBrightness != i)
    {
      this.mDozeScreenBrightness = i;
      updateDoze();
    }
  }
  
  public void setDozeScreenState(int paramInt)
  {
    if (this.mDozeScreenState != paramInt)
    {
      this.mDozeScreenState = paramInt;
      updateDoze();
    }
  }
  
  public void setFullscreen(boolean paramBoolean)
  {
    if (this.mFullscreen != paramBoolean)
    {
      this.mFullscreen = paramBoolean;
      if (!this.mFullscreen) {
        break label33;
      }
    }
    label33:
    for (int i = 1024;; i = 0)
    {
      applyWindowFlags(i, 1024);
      return;
    }
  }
  
  public void setInteractive(boolean paramBoolean)
  {
    this.mInteractive = paramBoolean;
  }
  
  public void setLowProfile(boolean paramBoolean)
  {
    if (this.mLowProfile != paramBoolean)
    {
      this.mLowProfile = paramBoolean;
      if (!this.mLowProfile) {
        break label29;
      }
    }
    label29:
    for (int i = 1;; i = 0)
    {
      applySystemUiVisibilityFlags(i, 1);
      return;
    }
  }
  
  public void setScreenBright(boolean paramBoolean)
  {
    if (this.mScreenBright != paramBoolean)
    {
      this.mScreenBright = paramBoolean;
      if (!this.mScreenBright) {
        break label33;
      }
    }
    label33:
    for (int i = 128;; i = 0)
    {
      applyWindowFlags(i, 128);
      return;
    }
  }
  
  public void setWindowless(boolean paramBoolean)
  {
    this.mWindowless = paramBoolean;
  }
  
  public void startDozing()
  {
    if ((!this.mCanDoze) || (this.mDozing)) {
      return;
    }
    this.mDozing = true;
    updateDoze();
  }
  
  public void stopDozing()
  {
    if (this.mDozing) {
      this.mDozing = false;
    }
    try
    {
      this.mSandman.stopDozing(this.mWindowToken);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public final void wakeUp()
  {
    wakeUp(false);
  }
  
  private final class DreamServiceWrapper
    extends IDreamService.Stub
  {
    private DreamServiceWrapper() {}
    
    public void attach(final IBinder paramIBinder, final boolean paramBoolean, final IRemoteCallback paramIRemoteCallback)
    {
      DreamService.-get2(DreamService.this).post(new Runnable()
      {
        public void run()
        {
          DreamService.-wrap0(DreamService.this, paramIBinder, paramBoolean, paramIRemoteCallback);
        }
      });
    }
    
    public void detach()
    {
      DreamService.-get2(DreamService.this).post(new Runnable()
      {
        public void run()
        {
          DreamService.-wrap1(DreamService.this);
        }
      });
    }
    
    public void wakeUp()
    {
      DreamService.-get2(DreamService.this).post(new Runnable()
      {
        public void run()
        {
          DreamService.-wrap2(DreamService.this, true);
        }
      });
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/dreams/DreamService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */