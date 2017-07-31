package com.android.server.wm;

import android.animation.ValueAnimator;
import android.annotation.IntDef;
import android.app.ActivityManagerInternal;
import android.app.ActivityManagerNative;
import android.app.AppOpsManager;
import android.app.AppOpsManager.OnOpChangedInternalListener;
import android.app.IActivityManager;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManagerInternal;
import android.hardware.input.InputManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.IRemoteCallback;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.PowerManagerInternal;
import android.os.PowerManagerInternal.LowPowerModeListener;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.SystemService;
import android.os.Trace;
import android.os.UserHandle;
import android.os.WorkSource;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.util.ArraySet;
import android.util.DisplayMetrics;
import android.util.EventLog;
import android.util.Log;
import android.util.OpFeatures;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TimeUtils;
import android.util.TypedValue;
import android.view.AppTransitionAnimationSpec;
import android.view.Choreographer;
import android.view.Display;
import android.view.DisplayInfo;
import android.view.IAppTransitionAnimationSpecsFuture;
import android.view.IApplicationToken;
import android.view.IDockedStackListener;
import android.view.IInputFilter;
import android.view.IOnKeyguardExitResult;
import android.view.IRotationWatcher;
import android.view.IWindow;
import android.view.IWindowId;
import android.view.IWindowManager.Stub;
import android.view.IWindowSession;
import android.view.IWindowSessionCallback;
import android.view.InputChannel;
import android.view.InputDevice;
import android.view.InputEventReceiver;
import android.view.InputEventReceiver.Factory;
import android.view.MagnificationSpec;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceControl;
import android.view.SurfaceSession;
import android.view.WindowContentFrameStats;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManagerInternal;
import android.view.WindowManagerInternal.AppTransitionListener;
import android.view.WindowManagerInternal.MagnificationCallbacks;
import android.view.WindowManagerInternal.OnHardKeyboardStatusChangeListener;
import android.view.WindowManagerInternal.WindowsForAccessibilityCallback;
import android.view.WindowManagerPolicy;
import android.view.WindowManagerPolicy.InputConsumer;
import android.view.WindowManagerPolicy.OnKeyguardExitResult;
import android.view.WindowManagerPolicy.PointerEventListener;
import android.view.WindowManagerPolicy.WindowManagerFuncs;
import android.view.WindowManagerPolicy.WindowState;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManagerInternal;
import com.android.internal.R.styleable;
import com.android.internal.app.ActivityTrigger;
import com.android.internal.app.IAssistScreenshotReceiver;
import com.android.internal.os.IResultReceiver;
import com.android.internal.policy.IShortcutService;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FastPrintWriter;
import com.android.internal.view.IInputContext;
import com.android.internal.view.IInputMethodClient;
import com.android.internal.view.IInputMethodManager;
import com.android.internal.view.RotationPolicy;
import com.android.internal.view.WindowManagerPolicyThread;
import com.android.server.AttributeCache;
import com.android.server.AttributeCache.Entry;
import com.android.server.DisplayThread;
import com.android.server.FgThread;
import com.android.server.LocalServices;
import com.android.server.UiThread;
import com.android.server.Watchdog;
import com.android.server.Watchdog.Monitor;
import com.android.server.am.OnePlusProcessManager;
import com.android.server.input.InputManagerService;
import com.android.server.policy.OemPhoneWindowManager;
import com.android.server.power.ShutdownThread;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class WindowManagerService
  extends IWindowManager.Stub
  implements Watchdog.Monitor, WindowManagerPolicy.WindowManagerFuncs
{
  private static final boolean ALWAYS_KEEP_CURRENT = true;
  private static final int ANIMATION_DURATION_SCALE = 2;
  static final int ANIMATION_SCALE = 3;
  private static final int BOOT_ANIMATION_POLL_INTERVAL = 200;
  private static final String BOOT_ANIMATION_SERVICE = "bootanim";
  static final boolean CUSTOM_SCREEN_ROTATION = true;
  static boolean DEBUG_POLICY = false;
  static final long DEFAULT_INPUT_DISPATCHING_TIMEOUT_NANOS = 5000000000L;
  private static final String DENSITY_OVERRIDE = "ro.config.density_override";
  private static final float DRAG_SHADOW_ALPHA_TRANSPARENT = 0.7071F;
  private static final int INPUT_DEVICES_READY_FOR_SAFE_MODE_DETECTION_TIMEOUT_MILLIS = 1000;
  static final int LAST_ANR_LIFETIME_DURATION_MSECS = 7200000;
  static final int LAYER_OFFSET_DIM = 1;
  static final int LAYER_OFFSET_THUMBNAIL = 4;
  static final int LAYOUT_REPEAT_THRESHOLD = 4;
  static final int MAX_ANIMATION_DURATION = 10000;
  private static final int MAX_SCREENSHOT_RETRIES = 3;
  static final int NETWORK_OPTS = 2;
  static boolean PROFILE_ORIENTATION = false;
  private static final String PROPERTY_BUILD_DATE_UTC = "ro.build.date.utc";
  private static final String PROPERTY_EMULATOR_CIRCULAR = "ro.emulator.circular";
  static final int SEAMLESS_ROTATION_TIMEOUT_DURATION = 2000;
  private static final String SIZE_OVERRIDE = "ro.config.size_override";
  static final int START_PROCESS = 1;
  private static final String SYSTEM_DEBUGGABLE = "ro.debuggable";
  private static final String SYSTEM_SECURE = "ro.secure";
  private static final String TAG = "WindowManager";
  private static final int TRANSITION_ANIMATION_SCALE = 1;
  static final int TYPE_LAYER_MULTIPLIER = 10000;
  static final int TYPE_LAYER_OFFSET = 1000;
  static final int UPDATE_FOCUS_NORMAL = 0;
  static final int UPDATE_FOCUS_PLACING_SURFACES = 2;
  static final int UPDATE_FOCUS_WILL_ASSIGN_LAYERS = 1;
  static final int UPDATE_FOCUS_WILL_PLACE_SURFACES = 3;
  static final int WINDOWS_FREEZING_SCREENS_ACTIVE = 1;
  static final int WINDOWS_FREEZING_SCREENS_NONE = 0;
  static final int WINDOWS_FREEZING_SCREENS_TIMEOUT = 2;
  private static final int WINDOW_ANIMATION_SCALE = 0;
  static final int WINDOW_FREEZE_TIMEOUT_DURATION = 2000;
  static final int WINDOW_LAYER_MULTIPLIER = 5;
  static final int WINDOW_REPLACEMENT_TIMEOUT_DURATION = 2000;
  static boolean localLOGV;
  static ActivityTrigger mActivityTrigger;
  static final boolean mEnableAnimCheck;
  static WindowState mFocusingWindow;
  AccessibilityController mAccessibilityController;
  final IActivityManager mActivityManager;
  private final WindowManagerInternal.AppTransitionListener mActivityManagerAppTransitionNotifier = new WindowManagerInternal.AppTransitionListener()
  {
    public void onAppTransitionCancelledLocked()
    {
      WindowManagerService.this.mH.sendEmptyMessage(48);
    }
    
    public void onAppTransitionFinishedLocked(IBinder paramAnonymousIBinder)
    {
      WindowManagerService.this.mH.sendEmptyMessage(49);
      paramAnonymousIBinder = WindowManagerService.this.findAppWindowToken(paramAnonymousIBinder);
      if (paramAnonymousIBinder == null) {
        return;
      }
      if (paramAnonymousIBinder.mLaunchTaskBehind) {}
      try
      {
        WindowManagerService.this.mActivityManager.notifyLaunchTaskBehindComplete(paramAnonymousIBinder.token);
        paramAnonymousIBinder.mLaunchTaskBehind = false;
        do
        {
          return;
          paramAnonymousIBinder.updateReportedVisibilityLocked();
        } while (!paramAnonymousIBinder.mEnteringAnimation);
        paramAnonymousIBinder.mEnteringAnimation = false;
        try
        {
          if (Build.AUTO_TEST_ONEPLUS) {
            Slog.d("APP_LAUNCH", SystemClock.uptimeMillis() + " WMS: onAppTransitionFinishedLocked " + paramAnonymousIBinder);
          }
          WindowManagerService.this.mActivityManager.notifyEnterAnimationComplete(paramAnonymousIBinder.token);
          return;
        }
        catch (RemoteException paramAnonymousIBinder) {}
      }
      catch (RemoteException localRemoteException)
      {
        for (;;) {}
      }
    }
  };
  final boolean mAllowAnimationsInLowPowerMode;
  final boolean mAllowBootMessages;
  boolean mAllowTheaterModeWakeFromLayout;
  boolean mAltOrientation = false;
  final ActivityManagerInternal mAmInternal;
  boolean mAnimateWallpaperWithTarget;
  boolean mAnimationScheduled;
  boolean mAnimationsDisabled = false;
  final WindowAnimator mAnimator;
  float mAnimatorDurationScaleSetting = 1.0F;
  final AppOpsManager mAppOps;
  final AppTransition mAppTransition;
  int mAppsFreezingScreen = 0;
  boolean mBootAnimationStopped = false;
  private final BoundsAnimationController mBoundsAnimationController;
  final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED".equals(paramAnonymousIntent.getAction())) {
        WindowManagerService.-get2(WindowManagerService.this).sendEmptyMessage(3);
      }
    }
  };
  private final ArrayList<Integer> mChangedStackList = new ArrayList();
  final Choreographer mChoreographer = Choreographer.getInstance();
  CircularDisplayMask mCircularDisplayMask;
  boolean mClientFreezingScreen = false;
  final ArraySet<AppWindowToken> mClosingApps = new ArraySet();
  final DisplayMetrics mCompatDisplayMetrics = new DisplayMetrics();
  float mCompatibleScreenScale;
  final Context mContext;
  Configuration mCurConfiguration = new Configuration();
  WindowState mCurrentFocus = null;
  int[] mCurrentProfileIds = new int[0];
  int mCurrentUserId;
  int mDeferredRotationPauseCount;
  final ArrayList<WindowState> mDestroyPreservedSurface = new ArrayList();
  final ArrayList<WindowState> mDestroySurface = new ArrayList();
  SparseArray<DisplayContent> mDisplayContents = new SparseArray(2);
  boolean mDisplayEnabled = false;
  long mDisplayFreezeTime = 0L;
  boolean mDisplayFrozen = false;
  final DisplayManager mDisplayManager;
  final DisplayManagerInternal mDisplayManagerInternal;
  final DisplayMetrics mDisplayMetrics = new DisplayMetrics();
  boolean mDisplayReady;
  final DisplaySettings mDisplaySettings;
  final Display[] mDisplays;
  Rect mDockedStackCreateBounds;
  int mDockedStackCreateMode = 0;
  DragState mDragState = null;
  final long mDrawLockTimeoutMillis;
  EmulatorDisplayOverlay mEmulatorDisplayOverlay;
  int mEnterAnimId;
  private boolean mEventDispatchingEnabled;
  int mExitAnimId;
  final ArrayList<AppWindowToken> mFinishedEarlyAnim = new ArrayList();
  final ArrayList<AppWindowToken> mFinishedStarting = new ArrayList();
  boolean mFocusMayChange;
  AppWindowToken mFocusedApp = null;
  String mFocusingActivity;
  HashSet<Integer> mFontSmallWindowUids = new HashSet();
  boolean mForceDisplayEnabled = false;
  final ArrayList<WindowState> mForceRemoves = new ArrayList();
  boolean mForceResizableTasks = false;
  final SurfaceSession mFxSession;
  final H mH = new H();
  boolean mHardKeyboardAvailable;
  WindowManagerInternal.OnHardKeyboardStatusChangeListener mHardKeyboardStatusChangeListener;
  final boolean mHasPermanentDpad;
  final boolean mHaveInputMethods;
  Session mHoldingScreenOn;
  PowerManager.WakeLock mHoldingScreenWakeLock;
  boolean mInTouchMode;
  InputConsumerImpl mInputConsumer;
  final InputManagerService mInputManager;
  final ArrayList<WindowState> mInputMethodDialogs = new ArrayList();
  IInputMethodManager mInputMethodManager;
  WindowState mInputMethodTarget = null;
  boolean mInputMethodTargetWaitingAnim;
  WindowState mInputMethodWindow = null;
  final InputMonitor mInputMonitor = new InputMonitor(this);
  boolean mIsTouchDevice;
  private final KeyguardDisableHandler mKeyguardDisableHandler;
  private boolean mKeyguardWaitingForActivityDrawn;
  String mLastANRState;
  int mLastDispatchedSystemUiVisibility = 0;
  int mLastDisplayFreezeDuration = 0;
  Object mLastFinishedFreezeSource = null;
  WindowState mLastFocus = null;
  HashSet<Integer> mLastFontSmallWindowUids = new HashSet();
  int mLastKeyguardForcedOrientation = -1;
  int mLastOrientation = -1;
  int mLastStatusBarVisibility = 0;
  WindowState mLastWakeLockHoldingWindow = null;
  WindowState mLastWakeLockObscuringWindow = null;
  int mLastWindowForcedOrientation = -1;
  final WindowLayersController mLayersController;
  int mLayoutSeq = 0;
  final boolean mLimitedAlphaCompositing;
  ArrayList<WindowState> mLosingFocus = new ArrayList();
  private MousePositionTracker mMousePositionTracker = new MousePositionTracker(null);
  final List<IBinder> mNoAnimationNotifyOnTransitionFinished = new ArrayList();
  final boolean mOnlyCore;
  final ArraySet<AppWindowToken> mOpeningApps = new ArraySet();
  final ArrayList<WindowState> mPendingRemove = new ArrayList();
  WindowState[] mPendingRemoveTmp = new WindowState[20];
  private final PointerEventDispatcher mPointerEventDispatcher;
  final WindowManagerPolicy mPolicy = new OemPhoneWindowManager();
  PowerManager mPowerManager;
  PowerManagerInternal mPowerManagerInternal;
  final DisplayMetrics mRealDisplayMetrics = new DisplayMetrics();
  WindowState[] mRebuildTmp = new WindowState[20];
  private final DisplayContentList mReconfigureOnConfigurationChanged = new DisplayContentList();
  final ArrayList<AppWindowToken> mReplacingWindowTimeouts = new ArrayList();
  final ArrayList<WindowState> mResizingWindows = new ArrayList();
  int mRotation = 0;
  ArrayList<RotationWatcher> mRotationWatchers = new ArrayList();
  boolean mSafeMode;
  SparseArray<Boolean> mScreenCaptureDisabled = new SparseArray();
  private final PowerManager.WakeLock mScreenFrozenLock;
  final Rect mScreenRect = new Rect();
  int mSeamlessRotationCount = 0;
  final ArraySet<Session> mSessions = new ArraySet();
  SettingsObserver mSettingsObserver;
  boolean mShowingBootMessages = false;
  boolean mSkipAppTransitionAnimation = false;
  SparseArray<TaskStack> mStackIdToStack = new SparseArray();
  StrictModeFlash mStrictModeFlash;
  boolean mSystemBooted = false;
  int mSystemDecorLayer = 0;
  SparseArray<Task> mTaskIdToTask = new SparseArray();
  TaskPositioner mTaskPositioner;
  final Configuration mTempConfiguration = new Configuration();
  private WindowContentFrameStats mTempWindowRenderStats;
  final DisplayMetrics mTmpDisplayMetrics = new DisplayMetrics();
  final float[] mTmpFloats = new float[9];
  final Rect mTmpRect = new Rect();
  final Rect mTmpRect2 = new Rect();
  final Rect mTmpRect3 = new Rect();
  final RectF mTmpRectF = new RectF();
  private final SparseIntArray mTmpTaskIds = new SparseIntArray();
  final Matrix mTmpTransform = new Matrix();
  final ArrayList<WindowState> mTmpWindows = new ArrayList();
  final HashMap<IBinder, WindowToken> mTokenMap = new HashMap();
  int mTransactionSequence;
  float mTransitionAnimationScaleSetting = 1.0F;
  boolean mTurnOnScreen;
  private ViewServer mViewServer;
  boolean mWaitingForConfig = false;
  ArrayList<WindowState> mWaitingForDrawn = new ArrayList();
  Runnable mWaitingForDrawnCallback;
  WallpaperController mWallpaperControllerLocked;
  InputConsumerImpl mWallpaperInputConsumer;
  Watermark mWatermark;
  float mWindowAnimationScaleSetting = 1.0F;
  final ArrayList<WindowChangeListener> mWindowChangeListeners = new ArrayList();
  final HashMap<IBinder, WindowState> mWindowMap = new HashMap();
  final WindowSurfacePlacer mWindowPlacerLocked;
  boolean mWindowsChanged = false;
  int mWindowsFreezingScreen = 0;
  
  static
  {
    PROFILE_ORIENTATION = false;
    localLOGV = WindowManagerDebugConfig.DEBUG;
    mEnableAnimCheck = SystemProperties.getBoolean("persist.animcheck.enable", false);
    mActivityTrigger = new ActivityTrigger();
  }
  
  private WindowManagerService(Context paramContext, InputManagerService paramInputManagerService, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    this.mContext = paramContext;
    this.mHaveInputMethods = paramBoolean1;
    this.mAllowBootMessages = paramBoolean2;
    this.mOnlyCore = paramBoolean3;
    this.mLimitedAlphaCompositing = paramContext.getResources().getBoolean(17956876);
    this.mHasPermanentDpad = paramContext.getResources().getBoolean(17957001);
    this.mInTouchMode = paramContext.getResources().getBoolean(17957029);
    this.mDrawLockTimeoutMillis = paramContext.getResources().getInteger(17694871);
    this.mAllowAnimationsInLowPowerMode = paramContext.getResources().getBoolean(17957031);
    this.mInputManager = paramInputManagerService;
    this.mDisplayManagerInternal = ((DisplayManagerInternal)LocalServices.getService(DisplayManagerInternal.class));
    this.mDisplaySettings = new DisplaySettings();
    this.mDisplaySettings.readSettingsLocked();
    this.mWallpaperControllerLocked = new WallpaperController(this);
    this.mWindowPlacerLocked = new WindowSurfacePlacer(this);
    this.mLayersController = new WindowLayersController(this);
    LocalServices.addService(WindowManagerPolicy.class, this.mPolicy);
    this.mPointerEventDispatcher = new PointerEventDispatcher(this.mInputManager.monitorInput("WindowManager"));
    this.mFxSession = new SurfaceSession();
    this.mDisplayManager = ((DisplayManager)paramContext.getSystemService("display"));
    this.mDisplays = this.mDisplayManager.getDisplays();
    paramInputManagerService = this.mDisplays;
    int i = 0;
    int j = paramInputManagerService.length;
    while (i < j)
    {
      createDisplayContentLocked(paramInputManagerService[i]);
      i += 1;
    }
    this.mKeyguardDisableHandler = new KeyguardDisableHandler(this.mContext, this.mPolicy);
    this.mPowerManager = ((PowerManager)paramContext.getSystemService("power"));
    this.mPowerManagerInternal = ((PowerManagerInternal)LocalServices.getService(PowerManagerInternal.class));
    this.mPowerManagerInternal.registerLowPowerModeObserver(new PowerManagerInternal.LowPowerModeListener()
    {
      public void onLowPowerModeChanged(boolean paramAnonymousBoolean)
      {
        int i = SystemProperties.getInt("persist.sys.powersave.rotate", 0);
        Slog.d(WindowManagerService.-get0(), "# onLowPowerModeChanged # enabled=" + paramAnonymousBoolean + ", flags=" + i);
        if (paramAnonymousBoolean) {
          if (!RotationPolicy.isRotationLocked(WindowManagerService.this.mContext)) {
            RotationPolicy.setRotationLock(WindowManagerService.this.mContext, true);
          }
        }
        for (i = 1;; i = 0)
        {
          SystemProperties.set("persist.sys.powersave.rotate", i + "");
          return;
          if ((RotationPolicy.isRotationLocked(WindowManagerService.this.mContext)) && (i != 0)) {
            RotationPolicy.setRotationLock(WindowManagerService.this.mContext, false);
          }
        }
      }
    });
    this.mScreenFrozenLock = this.mPowerManager.newWakeLock(1, "SCREEN_FROZEN");
    this.mScreenFrozenLock.setReferenceCounted(false);
    this.mAppTransition = new AppTransition(paramContext, this);
    this.mAppTransition.registerListenerLocked(this.mActivityManagerAppTransitionNotifier);
    this.mBoundsAnimationController = new BoundsAnimationController(this.mAppTransition, UiThread.getHandler());
    this.mActivityManager = ActivityManagerNative.getDefault();
    this.mAmInternal = ((ActivityManagerInternal)LocalServices.getService(ActivityManagerInternal.class));
    this.mAppOps = ((AppOpsManager)paramContext.getSystemService("appops"));
    paramInputManagerService = new AppOpsManager.OnOpChangedInternalListener()
    {
      public void onOpChanged(int paramAnonymousInt, String paramAnonymousString)
      {
        WindowManagerService.this.updateAppOpsState();
      }
    };
    this.mAppOps.startWatchingMode(24, null, paramInputManagerService);
    this.mAppOps.startWatchingMode(45, null, paramInputManagerService);
    this.mWindowAnimationScaleSetting = Settings.Global.getFloat(paramContext.getContentResolver(), "window_animation_scale", this.mWindowAnimationScaleSetting);
    this.mTransitionAnimationScaleSetting = Settings.Global.getFloat(paramContext.getContentResolver(), "transition_animation_scale", this.mTransitionAnimationScaleSetting);
    setAnimatorDurationScale(Settings.Global.getFloat(paramContext.getContentResolver(), "animator_duration_scale", this.mAnimatorDurationScaleSetting));
    paramInputManagerService = new IntentFilter();
    paramInputManagerService.addAction("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED");
    this.mContext.registerReceiver(this.mBroadcastReceiver, paramInputManagerService);
    this.mSettingsObserver = new SettingsObserver();
    this.mHoldingScreenWakeLock = this.mPowerManager.newWakeLock(536870922, "WindowManager");
    this.mHoldingScreenWakeLock.setReferenceCounted(false);
    this.mAnimator = new WindowAnimator(this);
    this.mAllowTheaterModeWakeFromLayout = paramContext.getResources().getBoolean(17956916);
    LocalServices.addService(WindowManagerInternal.class, new LocalService(null));
    initPolicy();
    Watchdog.getInstance().addMonitor(this);
    SurfaceControl.openTransaction();
    try
    {
      createWatermarkInTransaction();
      SurfaceControl.closeTransaction();
      showEmulatorDisplayOverlayIfNeeded();
      return;
    }
    finally
    {
      SurfaceControl.closeTransaction();
    }
  }
  
  private int addAppWindowToListLocked(WindowState paramWindowState)
  {
    Object localObject3 = paramWindowState.getDisplayContent();
    if (localObject3 == null) {
      return 0;
    }
    Object localObject1 = paramWindowState.mClient;
    Object localObject5 = paramWindowState.mToken;
    Object localObject4 = ((DisplayContent)localObject3).getWindowList();
    Object localObject2 = getTokenWindowsOnDisplay((WindowToken)localObject5, (DisplayContent)localObject3);
    if (!((WindowList)localObject2).isEmpty()) {
      return addAppWindowToTokenListLocked(paramWindowState, (WindowToken)localObject5, (WindowList)localObject4, (WindowList)localObject2);
    }
    if (localLOGV) {
      Slog.v("WindowManager", "Figuring out where to add app window " + ((IWindow)localObject1).asBinder() + " (token=" + localObject5 + ")");
    }
    localObject2 = null;
    ArrayList localArrayList = ((DisplayContent)localObject3).getTasks();
    int i = -1;
    int j = localArrayList.size() - 1;
    int k;
    for (;;)
    {
      localObject1 = localObject2;
      k = j;
      AppTokenList localAppTokenList;
      int m;
      if (j >= 0)
      {
        localAppTokenList = ((Task)localArrayList.get(j)).mAppTokens;
        m = localAppTokenList.size() - 1;
      }
      for (localObject1 = localObject2;; localObject1 = localObject2)
      {
        k = j;
        i = m;
        AppWindowToken localAppWindowToken;
        if (m >= 0)
        {
          localAppWindowToken = (AppWindowToken)localAppTokenList.get(m);
          if (localAppWindowToken != localObject5) {
            break label360;
          }
          m -= 1;
          k = j;
          i = m;
          if (m < 0)
          {
            j -= 1;
            k = j;
            i = m;
            if (j >= 0)
            {
              i = ((Task)localArrayList.get(j)).mAppTokens.size() - 1;
              k = j;
            }
          }
        }
        if (i < 0) {
          break;
        }
        localObject2 = localObject1;
        if (localObject1 == null) {
          break label440;
        }
        localObject4 = (WindowToken)this.mTokenMap.get(((WindowState)localObject1).mClient.asBinder());
        localObject2 = localObject1;
        if (localObject4 != null)
        {
          localObject3 = getTokenWindowsOnDisplay((WindowToken)localObject4, (DisplayContent)localObject3);
          localObject2 = localObject1;
          if (((WindowList)localObject3).size() > 0)
          {
            localObject3 = (WindowState)((WindowList)localObject3).get(0);
            localObject2 = localObject1;
            if (((WindowState)localObject3).mSubLayer < 0) {
              localObject2 = localObject3;
            }
          }
        }
        placeWindowBefore((WindowState)localObject2, paramWindowState);
        return 0;
        label360:
        WindowList localWindowList = getTokenWindowsOnDisplay(localAppWindowToken, (DisplayContent)localObject3);
        localObject2 = localObject1;
        if (!localAppWindowToken.sendingToBottom)
        {
          localObject2 = localObject1;
          if (localWindowList.size() > 0) {
            localObject2 = (WindowState)localWindowList.get(0);
          }
        }
        m -= 1;
      }
      j = k - 1;
      localObject2 = localObject1;
    }
    k -= 1;
    localObject2 = localObject1;
    label440:
    localObject1 = localObject2;
    if (k >= 0) {
      localObject5 = ((Task)localArrayList.get(k)).mAppTokens;
    }
    for (;;)
    {
      localObject1 = localObject2;
      if (i >= 0)
      {
        localObject1 = getTokenWindowsOnDisplay((AppWindowToken)((AppTokenList)localObject5).get(i), (DisplayContent)localObject3);
        j = ((WindowList)localObject1).size();
        if (j > 0) {
          localObject1 = (WindowState)((WindowList)localObject1).get(j - 1);
        }
      }
      else
      {
        if (i < 0) {
          break;
        }
        if (localObject1 == null) {
          break label617;
        }
        localObject3 = (WindowToken)this.mTokenMap.get(((WindowState)localObject1).mClient.asBinder());
        localObject2 = localObject1;
        if (localObject3 != null)
        {
          i = ((WindowToken)localObject3).windows.size();
          localObject2 = localObject1;
          if (i > 0)
          {
            localObject3 = (WindowState)((WindowToken)localObject3).windows.get(i - 1);
            localObject2 = localObject1;
            if (((WindowState)localObject3).mSubLayer >= 0) {
              localObject2 = localObject3;
            }
          }
        }
        placeWindowAfter((WindowState)localObject2, paramWindowState);
        return 0;
      }
      i -= 1;
    }
    label617:
    j = paramWindowState.mBaseLayer;
    i = ((WindowList)localObject4).size() - 1;
    for (;;)
    {
      if (i >= 0)
      {
        localObject1 = (WindowState)((WindowList)localObject4).get(i);
        if ((((WindowState)localObject1).mBaseLayer > j) || (((WindowState)localObject1).mAttrs.type == 2034)) {}
      }
      else
      {
        if ((WindowManagerDebugConfig.DEBUG_FOCUS) || (WindowManagerDebugConfig.DEBUG_WINDOW_MOVEMENT) || (WindowManagerDebugConfig.DEBUG_ADD_REMOVE)) {
          Slog.v("WindowManager", "Based on layer: Adding window " + paramWindowState + " at " + (i + 1) + " of " + ((WindowList)localObject4).size());
        }
        ((WindowList)localObject4).add(i + 1, paramWindowState);
        this.mWindowsChanged = true;
        return 0;
      }
      i -= 1;
    }
  }
  
  private int addAppWindowToTokenListLocked(WindowState paramWindowState, WindowToken paramWindowToken, WindowList paramWindowList1, WindowList paramWindowList2)
  {
    if (paramWindowState.mAttrs.type == 1)
    {
      paramWindowList1 = (WindowState)paramWindowList2.get(0);
      placeWindowBefore(paramWindowList1, paramWindowState);
      return indexOfWinInWindowList(paramWindowList1, paramWindowToken.windows);
    }
    AppWindowToken localAppWindowToken = paramWindowState.mAppToken;
    paramWindowList2 = (WindowState)paramWindowList2.get(paramWindowList2.size() - 1);
    if ((localAppWindowToken != null) && (paramWindowList2 == localAppWindowToken.startingWindow))
    {
      placeWindowBefore(paramWindowList2, paramWindowState);
      return indexOfWinInWindowList(paramWindowList2, paramWindowToken.windows);
    }
    int i = findIdxBasedOnAppTokens(paramWindowState);
    if ((WindowManagerDebugConfig.DEBUG_FOCUS) || (WindowManagerDebugConfig.DEBUG_WINDOW_MOVEMENT) || (WindowManagerDebugConfig.DEBUG_ADD_REMOVE)) {
      Slog.v("WindowManager", "not Base app: Adding window " + paramWindowState + " at " + (i + 1) + " of " + paramWindowList1.size());
    }
    paramWindowList1.add(i + 1, paramWindowState);
    if (i < 0) {}
    for (i = 0;; i = indexOfWinInWindowList((WindowState)paramWindowList1.get(i), paramWindowToken.windows) + 1)
    {
      this.mWindowsChanged = true;
      return i;
    }
  }
  
  private void addAttachedWindowToListLocked(WindowState paramWindowState, boolean paramBoolean)
  {
    WindowToken localWindowToken = paramWindowState.mToken;
    Object localObject1 = paramWindowState.getDisplayContent();
    if (localObject1 == null) {
      return;
    }
    WindowState localWindowState = paramWindowState.mAttachedWindow;
    WindowList localWindowList = getTokenWindowsOnDisplay(localWindowToken, (DisplayContent)localObject1);
    int n = localWindowList.size();
    int i1 = paramWindowState.mSubLayer;
    int i = Integer.MIN_VALUE;
    localObject1 = null;
    int k = 0;
    int j;
    Object localObject2;
    for (;;)
    {
      j = i;
      localObject2 = localObject1;
      int m;
      if (k < n)
      {
        localObject2 = (WindowState)localWindowList.get(k);
        m = ((WindowState)localObject2).mSubLayer;
        j = i;
        if (m >= i)
        {
          j = m;
          localObject1 = localObject2;
        }
        if (i1 >= 0) {
          break label276;
        }
        if (m < i1) {
          break label356;
        }
        if (paramBoolean)
        {
          if (WindowManagerDebugConfig.DEBUG_ADD_REMOVE) {
            Slog.v("WindowManager", "Adding " + paramWindowState + " to " + localWindowToken);
          }
          localWindowToken.windows.add(k, paramWindowState);
        }
        if (m >= 0) {
          localObject2 = localWindowState;
        }
        placeWindowBefore((WindowState)localObject2, paramWindowState);
      }
      for (localObject2 = localObject1;; localObject2 = localObject1)
      {
        if (k >= n)
        {
          if (paramBoolean)
          {
            if (WindowManagerDebugConfig.DEBUG_ADD_REMOVE) {
              Slog.v("WindowManager", "Adding " + paramWindowState + " to " + localWindowToken);
            }
            localWindowToken.windows.add(paramWindowState);
          }
          if (i1 >= 0) {
            break label368;
          }
          placeWindowBefore(localWindowState, paramWindowState);
        }
        return;
        label276:
        if (m <= i1) {
          break;
        }
        if (paramBoolean)
        {
          if (WindowManagerDebugConfig.DEBUG_ADD_REMOVE) {
            Slog.v("WindowManager", "Adding " + paramWindowState + " to " + localWindowToken);
          }
          localWindowToken.windows.add(k, paramWindowState);
        }
        placeWindowBefore((WindowState)localObject2, paramWindowState);
      }
      label356:
      k += 1;
      i = j;
    }
    label368:
    if (j >= 0) {}
    for (;;)
    {
      placeWindowAfter((WindowState)localObject2, paramWindowState);
      return;
      localObject2 = localWindowState;
    }
  }
  
  private void addFreeWindowToListLocked(WindowState paramWindowState)
  {
    WindowList localWindowList = paramWindowState.getWindowList();
    int j = paramWindowState.mBaseLayer;
    int i = localWindowList.size() - 1;
    for (;;)
    {
      if (i >= 0)
      {
        WindowState localWindowState = (WindowState)localWindowList.get(i);
        if ((localWindowState.getBaseType() == 2013) || (localWindowState.mBaseLayer > j)) {}
      }
      else
      {
        i += 1;
        if ((WindowManagerDebugConfig.DEBUG_FOCUS) || (WindowManagerDebugConfig.DEBUG_WINDOW_MOVEMENT) || (WindowManagerDebugConfig.DEBUG_ADD_REMOVE)) {
          Slog.v("WindowManager", "Free window: Adding window " + paramWindowState + " at " + i + " of " + localWindowList.size());
        }
        localWindowList.add(i, paramWindowState);
        this.mWindowsChanged = true;
        return;
      }
      i -= 1;
    }
  }
  
  private void addWindowToListInOrderLocked(WindowState paramWindowState, boolean paramBoolean)
  {
    if (WindowManagerDebugConfig.DEBUG_FOCUS) {
      Slog.d("WindowManager", "addWindowToListInOrderLocked: win=" + paramWindowState + " Callers=" + Debug.getCallers(4));
    }
    Object localObject;
    if (paramWindowState.mAttachedWindow == null)
    {
      localObject = paramWindowState.mToken;
      int i = 0;
      if (((WindowToken)localObject).appWindowToken != null)
      {
        i = addAppWindowToListLocked(paramWindowState);
        if (paramBoolean)
        {
          if (WindowManagerDebugConfig.DEBUG_ADD_REMOVE) {
            Slog.v("WindowManager", "Adding " + paramWindowState + " to " + localObject);
          }
          ((WindowToken)localObject).windows.add(i, paramWindowState);
        }
      }
    }
    for (;;)
    {
      localObject = paramWindowState.mAppToken;
      if ((localObject != null) && (paramBoolean)) {
        ((AppWindowToken)localObject).addWindow(paramWindowState);
      }
      return;
      addFreeWindowToListLocked(paramWindowState);
      break;
      addAttachedWindowToListLocked(paramWindowState, paramBoolean);
    }
  }
  
  private void adjustDisplaySizeRanges(DisplayInfo paramDisplayInfo, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = this.mPolicy.getConfigDisplayWidth(paramInt3, paramInt4, paramInt1, paramInt2);
    if (i < paramDisplayInfo.smallestNominalAppWidth) {
      paramDisplayInfo.smallestNominalAppWidth = i;
    }
    if (i > paramDisplayInfo.largestNominalAppWidth) {
      paramDisplayInfo.largestNominalAppWidth = i;
    }
    paramInt1 = this.mPolicy.getConfigDisplayHeight(paramInt3, paramInt4, paramInt1, paramInt2);
    if (paramInt1 < paramDisplayInfo.smallestNominalAppHeight) {
      paramDisplayInfo.smallestNominalAppHeight = paramInt1;
    }
    if (paramInt1 > paramDisplayInfo.largestNominalAppHeight) {
      paramDisplayInfo.largestNominalAppHeight = paramInt1;
    }
  }
  
  private float animationScalesCheck(int paramInt)
  {
    float f2 = -1.0F;
    if (!this.mAnimationsDisabled)
    {
      float f1 = f2;
      if (mEnableAnimCheck)
      {
        f1 = f2;
        if (this.mFocusingActivity != null)
        {
          if (mActivityTrigger == null) {
            mActivityTrigger = new ActivityTrigger();
          }
          f1 = f2;
          if (mActivityTrigger != null) {
            f1 = mActivityTrigger.activityMiscTrigger(3, this.mFocusingActivity, paramInt, 0);
          }
        }
      }
      if (f1 == -1.0F) {}
      switch (paramInt)
      {
      default: 
        return f1;
      case 0: 
        return this.mWindowAnimationScaleSetting;
      case 1: 
        return this.mTransitionAnimationScaleSetting;
      }
      return this.mAnimatorDurationScaleSetting;
    }
    return 0.0F;
  }
  
  private boolean applyAnimationLocked(AppWindowToken paramAppWindowToken, WindowManager.LayoutParams paramLayoutParams, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    Trace.traceBegin(32L, "WM#applyAnimationLocked");
    WindowState localWindowState;
    Rect localRect1;
    boolean bool;
    if (okToDisplay())
    {
      Object localObject = getDefaultDisplayInfoLocked();
      int i = ((DisplayInfo)localObject).appWidth;
      int j = ((DisplayInfo)localObject).appHeight;
      if ((WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS) || (WindowManagerDebugConfig.DEBUG_ANIM)) {
        Slog.v("WindowManager", "applyAnimation: atoken=" + paramAppWindowToken);
      }
      localWindowState = paramAppWindowToken.findMainWindow();
      localRect1 = new Rect(0, 0, i, j);
      Rect localRect2 = new Rect(0, 0, ((DisplayInfo)localObject).logicalWidth, ((DisplayInfo)localObject).logicalHeight);
      Rect localRect3 = new Rect();
      localObject = null;
      if (localWindowState != null)
      {
        bool = localWindowState.inFreeformWorkspace();
        if (localWindowState != null)
        {
          if (!bool) {
            break label424;
          }
          localRect1.set(localWindowState.mFrame);
          label161:
          localObject = localWindowState.getAttrs().surfaceInsets;
          localRect3.set(localWindowState.mContentInsets);
        }
        if (paramAppWindowToken.mLaunchTaskBehind) {
          paramBoolean1 = false;
        }
        if (WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS) {
          Slog.d("WindowManager", "Loading animation for app transition. transit=" + AppTransition.appTransitionToString(paramInt) + " enter=" + paramBoolean1 + " frame=" + localRect1 + " insets=" + localRect3 + " surfaceInsets=" + localObject);
        }
        paramLayoutParams = this.mAppTransition.loadAnimation(paramLayoutParams, paramInt, paramBoolean1, this.mCurConfiguration.uiMode, this.mCurConfiguration.orientation, localRect1, localRect2, localRect3, (Rect)localObject, paramBoolean2, bool, paramAppWindowToken.mTask.mTaskId);
        if (paramLayoutParams != null)
        {
          if (WindowManagerDebugConfig.DEBUG_ANIM) {
            logWithStack(TAG, "Loaded animation " + paramLayoutParams + " for " + paramAppWindowToken);
          }
          paramInt = localRect1.width();
          i = localRect1.height();
          paramAppWindowToken.mAppAnimator.setAnimation(paramLayoutParams, paramInt, i, this.mAppTransition.canSkipFirstFrame(), this.mAppTransition.getAppStackClipMode());
        }
      }
    }
    for (;;)
    {
      Trace.traceEnd(32L);
      if (paramAppWindowToken.mAppAnimator.animation == null) {
        break label447;
      }
      return true;
      bool = false;
      break;
      label424:
      localRect1.set(localWindowState.mContainingFrame);
      break label161;
      paramAppWindowToken.mAppAnimator.clearAnimation();
    }
    label447:
    return false;
  }
  
  static boolean canBeImeTarget(WindowState paramWindowState)
  {
    int i = paramWindowState.mAttrs.flags & 0x20008;
    int j = paramWindowState.mAttrs.type;
    if ((i == 0) || (i == 131080)) {}
    while (j == 3)
    {
      if (WindowManagerDebugConfig.DEBUG_INPUT_METHOD)
      {
        Slog.i("WindowManager", "isVisibleOrAdding " + paramWindowState + ": " + paramWindowState.isVisibleOrAdding());
        if (!paramWindowState.isVisibleOrAdding())
        {
          Slog.i("WindowManager", "  mSurfaceController=" + paramWindowState.mWinAnimator.mSurfaceController + " relayoutCalled=" + paramWindowState.mRelayoutCalled + " viewVis=" + paramWindowState.mViewVisibility + " policyVis=" + paramWindowState.mPolicyVisibility + " policyVisAfterAnim=" + paramWindowState.mPolicyVisibilityAfterAnim + " attachHid=" + paramWindowState.mAttachedHidden + " exiting=" + paramWindowState.mAnimatingExit + " destroying=" + paramWindowState.mDestroying);
          if (paramWindowState.mAppToken != null) {
            Slog.i("WindowManager", "  mAppToken.hiddenRequested=" + paramWindowState.mAppToken.hiddenRequested);
          }
        }
      }
      return paramWindowState.isVisibleOrAdding();
    }
    return false;
  }
  
  private boolean checkBootAnimationCompleteLocked()
  {
    if (SystemService.isRunning("bootanim"))
    {
      this.mH.removeMessages(37);
      this.mH.sendEmptyMessageDelayed(37, 200L);
      if (WindowManagerDebugConfig.DEBUG_BOOT) {
        Slog.i("WindowManager", "checkBootAnimationComplete: Waiting for anim complete");
      }
      return false;
    }
    if (WindowManagerDebugConfig.DEBUG_BOOT) {
      Slog.i("WindowManager", "checkBootAnimationComplete: Animation complete!");
    }
    return true;
  }
  
  private boolean checkCallingPermission(String paramString1, String paramString2)
  {
    if (Binder.getCallingPid() == Process.myPid()) {
      return true;
    }
    if (this.mContext.checkCallingPermission(paramString1) == 0) {
      return true;
    }
    Slog.w("WindowManager", "Permission Denial: " + paramString2 + " from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires " + paramString1);
    return false;
  }
  
  private boolean checkWaitingForWindowsLocked()
  {
    boolean bool4 = false;
    boolean bool5 = false;
    boolean bool2 = false;
    boolean bool1;
    boolean bool3;
    int i;
    label51:
    WindowState localWindowState;
    label83:
    boolean bool6;
    boolean bool7;
    boolean bool8;
    boolean bool9;
    if (this.mContext.getResources().getBoolean(17956945)) {
      if (this.mOnlyCore)
      {
        bool1 = false;
        bool3 = true;
        WindowList localWindowList = getDefaultWindowListLocked();
        int j = localWindowList.size();
        i = 0;
        if (i >= j) {
          break label309;
        }
        localWindowState = (WindowState)localWindowList.get(i);
        if ((localWindowState.isVisibleLw()) && (!localWindowState.mObscured)) {
          break label169;
        }
        bool6 = bool5;
        bool7 = bool4;
        bool8 = bool3;
        bool9 = bool2;
        if (localWindowState.isDrawnLw())
        {
          if (localWindowState.mAttrs.type != 2021) {
            break label179;
          }
          bool7 = true;
          bool9 = bool2;
          bool8 = bool3;
          bool6 = bool5;
        }
      }
    }
    for (;;)
    {
      i += 1;
      bool5 = bool6;
      bool4 = bool7;
      bool3 = bool8;
      bool2 = bool9;
      break label51;
      bool1 = true;
      break;
      bool1 = false;
      break;
      label169:
      if (localWindowState.isDrawnLw()) {
        break label83;
      }
      return true;
      label179:
      if ((localWindowState.mAttrs.type == 2) || (localWindowState.mAttrs.type == 4))
      {
        bool6 = true;
        bool7 = bool4;
        bool8 = bool3;
        bool9 = bool2;
      }
      else if (localWindowState.mAttrs.type == 2013)
      {
        bool9 = true;
        bool6 = bool5;
        bool7 = bool4;
        bool8 = bool3;
      }
      else
      {
        bool6 = bool5;
        bool7 = bool4;
        bool8 = bool3;
        bool9 = bool2;
        if (localWindowState.mAttrs.type == 2000)
        {
          bool8 = this.mPolicy.isKeyguardDrawnLw();
          bool6 = bool5;
          bool7 = bool4;
          bool9 = bool2;
        }
      }
    }
    label309:
    if ((WindowManagerDebugConfig.DEBUG_SCREEN_ON) || (WindowManagerDebugConfig.DEBUG_BOOT)) {
      Slog.i("WindowManager", "******** booted=" + this.mSystemBooted + " msg=" + this.mShowingBootMessages + " haveBoot=" + bool4 + " haveApp=" + bool5 + " haveWall=" + bool2 + " wallEnabled=" + bool1 + " haveKeyguard=" + bool3);
    }
    if ((this.mSystemBooted) || (bool4))
    {
      if ((!this.mSystemBooted) || (((bool5) || (bool3)) && ((!bool1) || (bool2)))) {
        return false;
      }
    }
    else {
      return true;
    }
    return true;
  }
  
  private int computeCompatSmallestWidth(boolean paramBoolean, int paramInt1, DisplayMetrics paramDisplayMetrics, int paramInt2, int paramInt3)
  {
    this.mTmpDisplayMetrics.setTo(paramDisplayMetrics);
    paramDisplayMetrics = this.mTmpDisplayMetrics;
    if (paramBoolean) {}
    for (;;)
    {
      return reduceCompatConfigWidthSize(reduceCompatConfigWidthSize(reduceCompatConfigWidthSize(reduceCompatConfigWidthSize(0, 0, paramInt1, paramDisplayMetrics, paramInt3, paramInt2), 1, paramInt1, paramDisplayMetrics, paramInt2, paramInt3), 2, paramInt1, paramDisplayMetrics, paramInt3, paramInt2), 3, paramInt1, paramDisplayMetrics, paramInt2, paramInt3);
      int i = paramInt3;
      paramInt3 = paramInt2;
      paramInt2 = i;
    }
  }
  
  private WindowState computeFocusedWindowLocked()
  {
    int j = this.mDisplayContents.size();
    int i = 0;
    while (i < j)
    {
      WindowState localWindowState = findFocusedWindowLocked((DisplayContent)this.mDisplayContents.valueAt(i));
      if (localWindowState != null) {
        return localWindowState;
      }
      i += 1;
    }
    return null;
  }
  
  private Configuration computeNewConfigurationLocked()
  {
    if (!this.mDisplayReady) {
      return null;
    }
    Configuration localConfiguration = new Configuration();
    localConfiguration.fontScale = 0.0F;
    computeScreenConfigurationLocked(localConfiguration);
    return localConfiguration;
  }
  
  private void computeSizeRangesAndScreenLayout(DisplayInfo paramDisplayInfo, boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, float paramFloat, Configuration paramConfiguration)
  {
    if (paramBoolean) {}
    for (;;)
    {
      paramDisplayInfo.smallestNominalAppWidth = 1073741824;
      paramDisplayInfo.smallestNominalAppHeight = 1073741824;
      paramDisplayInfo.largestNominalAppWidth = 0;
      paramDisplayInfo.largestNominalAppHeight = 0;
      adjustDisplaySizeRanges(paramDisplayInfo, 0, paramInt1, paramInt3, paramInt2);
      adjustDisplaySizeRanges(paramDisplayInfo, 1, paramInt1, paramInt2, paramInt3);
      adjustDisplaySizeRanges(paramDisplayInfo, 2, paramInt1, paramInt3, paramInt2);
      adjustDisplaySizeRanges(paramDisplayInfo, 3, paramInt1, paramInt2, paramInt3);
      paramInt1 = reduceConfigLayout(reduceConfigLayout(reduceConfigLayout(reduceConfigLayout(Configuration.resetScreenLayout(paramConfiguration.screenLayout), 0, paramFloat, paramInt3, paramInt2, paramInt1), 1, paramFloat, paramInt2, paramInt3, paramInt1), 2, paramFloat, paramInt3, paramInt2, paramInt1), 3, paramFloat, paramInt2, paramInt3, paramInt1);
      paramConfiguration.smallestScreenWidthDp = ((int)(paramDisplayInfo.smallestNominalAppWidth / paramFloat));
      paramConfiguration.screenLayout = paramInt1;
      return;
      int i = paramInt3;
      paramInt3 = paramInt2;
      paramInt2 = i;
    }
  }
  
  private void configureDisplayPolicyLocked(DisplayContent paramDisplayContent)
  {
    this.mPolicy.setInitialDisplaySize(paramDisplayContent.getDisplay(), paramDisplayContent.mBaseDisplayWidth, paramDisplayContent.mBaseDisplayHeight, paramDisplayContent.mBaseDisplayDensity);
    DisplayInfo localDisplayInfo = paramDisplayContent.getDisplayInfo();
    this.mPolicy.setDisplayOverscan(paramDisplayContent.getDisplay(), localDisplayInfo.overscanLeft, localDisplayInfo.overscanTop, localDisplayInfo.overscanRight, localDisplayInfo.overscanBottom);
  }
  
  private static void convertCropForSurfaceFlinger(Rect paramRect, int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt1 == 1)
    {
      paramInt1 = paramRect.top;
      paramRect.top = (paramInt2 - paramRect.right);
      paramRect.right = paramRect.bottom;
      paramRect.bottom = (paramInt2 - paramRect.left);
      paramRect.left = paramInt1;
    }
    do
    {
      return;
      if (paramInt1 == 2)
      {
        paramInt1 = paramRect.top;
        paramRect.top = (paramInt3 - paramRect.bottom);
        paramRect.bottom = (paramInt3 - paramInt1);
        paramInt1 = paramRect.right;
        paramRect.right = (paramInt2 - paramRect.left);
        paramRect.left = (paramInt2 - paramInt1);
        return;
      }
    } while (paramInt1 != 3);
    paramInt1 = paramRect.top;
    paramRect.top = paramRect.left;
    paramRect.left = (paramInt3 - paramRect.bottom);
    paramRect.bottom = paramRect.right;
    paramRect.right = (paramInt3 - paramInt1);
  }
  
  private int createSurfaceControl(Surface paramSurface, int paramInt, WindowState paramWindowState, WindowStateAnimator paramWindowStateAnimator)
  {
    int i = paramInt;
    if (!paramWindowState.mHasSurface) {
      i = paramInt | 0x4;
    }
    paramWindowStateAnimator = paramWindowStateAnimator.createSurfaceLocked();
    if (paramWindowStateAnimator != null)
    {
      paramWindowStateAnimator.getSurface(paramSurface);
      if (WindowManagerDebugConfig.SHOW_TRANSACTIONS) {
        Slog.i("WindowManager", "  OUT SURFACE " + paramSurface + ": copied");
      }
      return i;
    }
    Slog.w("WindowManager", "Failed to create surface control for " + paramWindowState);
    paramSurface.release();
    return i;
  }
  
  private Task createTaskLocked(int paramInt1, int paramInt2, int paramInt3, AppWindowToken paramAppWindowToken, Rect paramRect, Configuration paramConfiguration)
  {
    if (WindowManagerDebugConfig.DEBUG_STACK) {
      Slog.i("WindowManager", "createTaskLocked: taskId=" + paramInt1 + " stackId=" + paramInt2 + " atoken=" + paramAppWindowToken + " bounds=" + paramRect);
    }
    TaskStack localTaskStack = (TaskStack)this.mStackIdToStack.get(paramInt2);
    if (localTaskStack == null) {
      throw new IllegalArgumentException("addAppToken: invalid stackId=" + paramInt2);
    }
    EventLog.writeEvent(31001, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) });
    paramRect = new Task(paramInt1, localTaskStack, paramInt3, this, paramRect, paramConfiguration);
    this.mTaskIdToTask.put(paramInt1, paramRect);
    if (paramAppWindowToken.mLaunchTaskBehind) {}
    for (boolean bool = false;; bool = true)
    {
      localTaskStack.addTask(paramRect, bool, paramAppWindowToken.showForAllUsers);
      return paramRect;
    }
  }
  
  static int dipToPixel(int paramInt, DisplayMetrics paramDisplayMetrics)
  {
    return (int)TypedValue.applyDimension(1, paramInt, paramDisplayMetrics);
  }
  
  private void displayReady(int paramInt)
  {
    synchronized (this.mWindowMap)
    {
      DisplayContent localDisplayContent = getDisplayContentLocked(paramInt);
      if (localDisplayContent != null)
      {
        this.mAnimator.addDisplayLocked(paramInt);
        localDisplayContent.initializeDisplayBaseInfo();
        if (localDisplayContent.mTapDetector != null) {
          localDisplayContent.mTapDetector.init();
        }
      }
      return;
    }
  }
  
  private boolean doesAddToastWindowRequireToken(String paramString, int paramInt, WindowState paramWindowState)
  {
    if (paramWindowState != null)
    {
      if (paramWindowState.mAppToken != null) {
        return paramWindowState.mAppToken.targetSdk > 25;
      }
      return false;
    }
    try
    {
      paramWindowState = this.mContext.getPackageManager().getApplicationInfoAsUser(paramString, 0, UserHandle.getUserId(paramInt));
      if (paramWindowState.uid != paramInt) {
        throw new SecurityException("Package " + paramString + " not in UID " + paramInt);
      }
      paramInt = paramWindowState.targetSdkVersion;
      if (paramInt > 25) {
        return true;
      }
    }
    catch (PackageManager.NameNotFoundException paramString) {}
    return false;
  }
  
  private static boolean excludeWindowTypeFromTapOutTask(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return false;
    }
    return true;
  }
  
  private int findIdxBasedOnAppTokens(WindowState paramWindowState)
  {
    WindowList localWindowList = paramWindowState.getWindowList();
    int i = localWindowList.size() - 1;
    while (i >= 0)
    {
      if (((WindowState)localWindowList.get(i)).mAppToken == paramWindowState.mAppToken) {
        return i;
      }
      i -= 1;
    }
    return -1;
  }
  
  private WindowState findWindow(int paramInt)
  {
    if (paramInt == -1) {
      return getFocusedWindow();
    }
    synchronized (this.mWindowMap)
    {
      int k = this.mDisplayContents.size();
      int i = 0;
      while (i < k)
      {
        WindowList localWindowList = ((DisplayContent)this.mDisplayContents.valueAt(i)).getWindowList();
        int m = localWindowList.size();
        int j = 0;
        while (j < m)
        {
          WindowState localWindowState = (WindowState)localWindowList.get(j);
          int n = System.identityHashCode(localWindowState);
          if (n == paramInt) {
            return localWindowState;
          }
          j += 1;
        }
        i += 1;
      }
      return null;
    }
  }
  
  private void finishPositioning()
  {
    if (WindowManagerDebugConfig.DEBUG_TASK_POSITIONING) {
      Slog.d("WindowManager", "finishPositioning");
    }
    synchronized (this.mWindowMap)
    {
      if (this.mTaskPositioner != null)
      {
        this.mTaskPositioner.unregister();
        this.mTaskPositioner = null;
        this.mInputMonitor.updateInputWindowsLw(true);
      }
      return;
    }
  }
  
  static float fixScale(float paramFloat)
  {
    float f;
    if (paramFloat < 0.0F) {
      f = 0.0F;
    }
    for (;;)
    {
      return Math.abs(f);
      f = paramFloat;
      if (paramFloat > 20.0F) {
        f = 20.0F;
      }
    }
  }
  
  private int getAppSpecifiedOrientation()
  {
    int i = -1;
    int m = 0;
    boolean bool2 = false;
    ArrayList localArrayList = getDefaultDisplayContentLocked().getTasks();
    boolean bool1;
    boolean bool4;
    int j;
    if (!isStackVisibleLocked(3))
    {
      bool1 = isStackVisibleLocked(2);
      bool4 = getDefaultDisplayContentLocked().mDividerControllerLocked.isMinimizedDock();
      j = localArrayList.size() - 1;
    }
    for (;;)
    {
      if (j < 0) {
        break label594;
      }
      AppTokenList localAppTokenList = ((Task)localArrayList.get(j)).mAppTokens;
      int i2 = localAppTokenList.size() - 1;
      int k = i2;
      if (k >= 0)
      {
        AppWindowToken localAppWindowToken = (AppWindowToken)localAppTokenList.get(k);
        if (WindowManagerDebugConfig.DEBUG_APP_ORIENTATION) {
          Slog.v("WindowManager", "Checking app orientation: " + localAppWindowToken);
        }
        if ((m != 0) || (localAppWindowToken.hidden)) {}
        int i1;
        for (;;)
        {
          if ((k == i2) && (i != 3) && (bool2))
          {
            if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
              Slog.v("WindowManager", "Done at " + localAppWindowToken + " -- end of group, return " + i);
            }
            return i;
            bool1 = true;
            break;
            if (localAppWindowToken.hiddenRequested)
            {
              n = m;
              bool3 = bool2;
              i1 = i;
              if (WindowManagerDebugConfig.DEBUG_ORIENTATION)
              {
                Slog.v("WindowManager", "Skipping " + localAppWindowToken + " -- going to hide");
                i1 = i;
                bool3 = bool2;
                n = m;
              }
            }
          }
        }
        label373:
        do
        {
          do
          {
            for (;;)
            {
              k -= 1;
              m = n;
              bool2 = bool3;
              i = i1;
              break;
              if (!localAppWindowToken.hiddenRequested) {
                break label373;
              }
              n = m;
              bool3 = bool2;
              i1 = i;
              if (WindowManagerDebugConfig.DEBUG_ORIENTATION)
              {
                Slog.v("WindowManager", "Skipping " + localAppWindowToken + " -- hidden on top");
                n = m;
                bool3 = bool2;
                i1 = i;
              }
            }
            if (!bool1) {
              break label416;
            }
            n = m;
            bool3 = bool2;
            i1 = i;
          } while (!localAppWindowToken.mTask.isHomeTask());
          n = m;
          bool3 = bool2;
          i1 = i;
        } while (!bool4);
        label416:
        if (k == 0) {
          i = localAppWindowToken.requestedOrientation;
        }
        int n = localAppWindowToken.requestedOrientation;
        boolean bool3 = localAppWindowToken.appFullscreen;
        if ((bool3) && (n != 3))
        {
          if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
            Slog.v("WindowManager", "Done at " + localAppWindowToken + " -- full screen, return " + n);
          }
          return n;
        }
        if ((n != -1) && (n != 3))
        {
          if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
            Slog.v("WindowManager", "Done at " + localAppWindowToken + " -- explicitly set, return " + n);
          }
          return n;
        }
        if (n == 3) {}
        for (n = 1;; n = 0)
        {
          n = m | n;
          i1 = i;
          break;
        }
      }
      j -= 1;
    }
    label594:
    if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
      Slog.v("WindowManager", "No app is requesting an orientation, return " + this.mLastOrientation);
    }
    if (bool1) {
      return -1;
    }
    return this.mLastOrientation;
  }
  
  private WindowState getFocusedWindow()
  {
    synchronized (this.mWindowMap)
    {
      WindowState localWindowState = getFocusedWindowLocked();
      return localWindowState;
    }
  }
  
  private WindowState getFocusedWindowLocked()
  {
    return this.mCurrentFocus;
  }
  
  private int getForcedDisplayDensityForUserLocked(int paramInt)
  {
    String str2 = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), "display_density_forced", paramInt);
    String str1;
    if (str2 != null)
    {
      str1 = str2;
      if (str2.length() != 0) {}
    }
    else
    {
      str1 = SystemProperties.get("ro.config.density_override", null);
    }
    if ((str1 != null) && (str1.length() > 0)) {
      try
      {
        paramInt = Integer.parseInt(str1);
        return paramInt;
      }
      catch (NumberFormatException localNumberFormatException) {}
    }
    return 0;
  }
  
  private void getNonDecorInsetsLocked(Rect paramRect)
  {
    DisplayInfo localDisplayInfo = getDefaultDisplayInfoLocked();
    this.mPolicy.getNonDecorInsetsLw(localDisplayInfo.rotation, localDisplayInfo.logicalWidth, localDisplayInfo.logicalHeight, paramRect);
  }
  
  static int getPropertyInt(String[] paramArrayOfString, int paramInt1, int paramInt2, int paramInt3, DisplayMetrics paramDisplayMetrics)
  {
    if (paramInt1 < paramArrayOfString.length)
    {
      paramArrayOfString = paramArrayOfString[paramInt1];
      if ((paramArrayOfString != null) && (paramArrayOfString.length() > 0)) {
        try
        {
          paramInt1 = Integer.parseInt(paramArrayOfString);
          return paramInt1;
        }
        catch (Exception paramArrayOfString) {}
      }
    }
    if (paramInt2 == 0) {
      return paramInt3;
    }
    return (int)TypedValue.applyDimension(paramInt2, paramInt3, paramDisplayMetrics);
  }
  
  private WindowList getTokenWindowsOnDisplay(WindowToken paramWindowToken, DisplayContent paramDisplayContent)
  {
    WindowList localWindowList = new WindowList();
    int j = paramWindowToken.windows.size();
    int i = 0;
    while (i < j)
    {
      WindowState localWindowState = (WindowState)paramWindowToken.windows.get(i);
      if (localWindowState.getDisplayContent() == paramDisplayContent) {
        localWindowList.add(localWindowState);
      }
      i += 1;
    }
    return localWindowList;
  }
  
  private void handleDisplayChangedLocked(int paramInt)
  {
    DisplayContent localDisplayContent = getDisplayContentLocked(paramInt);
    if (localDisplayContent != null) {
      localDisplayContent.updateDisplayInfo();
    }
    this.mWindowPlacerLocked.requestTraversal();
  }
  
  private void handleDisplayRemovedLocked(int paramInt)
  {
    DisplayContent localDisplayContent = getDisplayContentLocked(paramInt);
    if (localDisplayContent != null)
    {
      if (localDisplayContent.isAnimating())
      {
        localDisplayContent.mDeferredRemoval = true;
        return;
      }
      if (WindowManagerDebugConfig.DEBUG_DISPLAY) {
        Slog.v("WindowManager", "Removing display=" + localDisplayContent);
      }
      this.mDisplayContents.delete(paramInt);
      localDisplayContent.close();
      if (paramInt == 0)
      {
        unregisterPointerEventListener(localDisplayContent.mTapDetector);
        unregisterPointerEventListener(this.mMousePositionTracker);
      }
    }
    this.mAnimator.removeDisplayLocked(paramInt);
    this.mWindowPlacerLocked.requestTraversal();
  }
  
  private void handleTapOutsideTask(DisplayContent paramDisplayContent, int paramInt1, int paramInt2)
  {
    synchronized (this.mWindowMap)
    {
      Task localTask = paramDisplayContent.findTaskForControlPoint(paramInt1, paramInt2);
      if (localTask != null)
      {
        boolean bool = startPositioningLocked(localTask.getTopVisibleAppMainWindow(), true, paramInt1, paramInt2);
        if (!bool) {
          return;
        }
        paramInt1 = localTask.mTaskId;
        if (paramInt1 < 0) {}
      }
      try
      {
        this.mActivityManager.setFocusedTask(paramInt1);
        return;
      }
      catch (RemoteException paramDisplayContent) {}
      paramInt1 = paramDisplayContent.taskIdFromPoint(paramInt1, paramInt2);
    }
  }
  
  private int indexOfWinInWindowList(WindowState paramWindowState, WindowList paramWindowList)
  {
    int i = paramWindowList.size() - 1;
    while (i >= 0)
    {
      WindowState localWindowState = (WindowState)paramWindowList.get(i);
      if (localWindowState == paramWindowState) {
        return i;
      }
      if ((!localWindowState.mChildWindows.isEmpty()) && (indexOfWinInWindowList(paramWindowState, localWindowState.mChildWindows) >= 0)) {
        return i;
      }
      i -= 1;
    }
    return -1;
  }
  
  private void initPolicy()
  {
    UiThread.getHandler().runWithScissors(new Runnable()
    {
      public void run()
      {
        WindowManagerPolicyThread.set(Thread.currentThread(), Looper.myLooper());
        WindowManagerService.this.mPolicy.init(WindowManagerService.this.mContext, WindowManagerService.this, WindowManagerService.this);
      }
    }, 0L);
  }
  
  private boolean isSystemSecure()
  {
    if ("1".equals(SystemProperties.get("ro.secure", "1"))) {
      return "0".equals(SystemProperties.get("ro.debuggable", "0"));
    }
    return false;
  }
  
  static void logSurface(SurfaceControl paramSurfaceControl, String paramString1, String paramString2)
  {
    Slog.i("WindowManager", "  SURFACE " + paramSurfaceControl + ": " + paramString2 + " / " + paramString1);
  }
  
  static void logSurface(WindowState paramWindowState, String paramString, boolean paramBoolean)
  {
    paramWindowState = "  SURFACE " + paramString + ": " + paramWindowState;
    if (paramBoolean)
    {
      logWithStack(TAG, paramWindowState);
      return;
    }
    Slog.i("WindowManager", paramWindowState);
  }
  
  static void logWithStack(String paramString1, String paramString2)
  {
    RuntimeException localRuntimeException = null;
    if (WindowManagerDebugConfig.SHOW_STACK_CRAWLS)
    {
      localRuntimeException = new RuntimeException();
      localRuntimeException.fillInStackTrace();
    }
    Slog.i(paramString1, paramString2, localRuntimeException);
  }
  
  public static WindowManagerService main(final Context paramContext, final InputManagerService paramInputManagerService, final boolean paramBoolean1, final boolean paramBoolean2, final boolean paramBoolean3)
  {
    WindowManagerService[] arrayOfWindowManagerService = new WindowManagerService[1];
    DisplayThread.getHandler().runWithScissors(new Runnable()
    {
      public void run()
      {
        this.val$holder[0] = new WindowManagerService(paramContext, paramInputManagerService, paramBoolean1, paramBoolean2, paramBoolean3, null);
      }
    }, 0L);
    return arrayOfWindowManagerService[0];
  }
  
  private DisplayContent newDisplayContentLocked(Display paramDisplay)
  {
    DisplayContent localDisplayContent = new DisplayContent(paramDisplay, this);
    int i = paramDisplay.getDisplayId();
    if (WindowManagerDebugConfig.DEBUG_DISPLAY) {
      Slog.v("WindowManager", "Adding display=" + paramDisplay);
    }
    this.mDisplayContents.put(i, localDisplayContent);
    paramDisplay = localDisplayContent.getDisplayInfo();
    Rect localRect = new Rect();
    this.mDisplaySettings.getOverscanLocked(paramDisplay.name, paramDisplay.uniqueId, localRect);
    paramDisplay.overscanLeft = localRect.left;
    paramDisplay.overscanTop = localRect.top;
    paramDisplay.overscanRight = localRect.right;
    paramDisplay.overscanBottom = localRect.bottom;
    this.mDisplayManagerInternal.setDisplayInfoOverrideFromWindowManager(i, paramDisplay);
    configureDisplayPolicyLocked(localDisplayContent);
    if (i == 0)
    {
      localDisplayContent.mTapDetector = new TaskTapPointerEventListener(this, localDisplayContent);
      registerPointerEventListener(localDisplayContent.mTapDetector);
      registerPointerEventListener(this.mMousePositionTracker);
    }
    return localDisplayContent;
  }
  
  private void notifyFocusChanged()
  {
    synchronized (this.mWindowMap)
    {
      boolean bool = this.mWindowChangeListeners.isEmpty();
      if (bool) {
        return;
      }
      WindowChangeListener[] arrayOfWindowChangeListener = new WindowChangeListener[this.mWindowChangeListeners.size()];
      arrayOfWindowChangeListener = (WindowChangeListener[])this.mWindowChangeListeners.toArray(arrayOfWindowChangeListener);
      int j = arrayOfWindowChangeListener.length;
      int i = 0;
      if (i < j)
      {
        arrayOfWindowChangeListener[i].focusChanged();
        i += 1;
      }
    }
  }
  
  private void notifyWindowsChanged()
  {
    synchronized (this.mWindowMap)
    {
      boolean bool = this.mWindowChangeListeners.isEmpty();
      if (bool) {
        return;
      }
      WindowChangeListener[] arrayOfWindowChangeListener = new WindowChangeListener[this.mWindowChangeListeners.size()];
      arrayOfWindowChangeListener = (WindowChangeListener[])this.mWindowChangeListeners.toArray(arrayOfWindowChangeListener);
      int j = arrayOfWindowChangeListener.length;
      int i = 0;
      if (i < j)
      {
        arrayOfWindowChangeListener[i].windowsChanged();
        i += 1;
      }
    }
  }
  
  private int[] onConfigurationChanged()
  {
    this.mPolicy.onConfigurationChanged();
    Object localObject = getDefaultDisplayContentLocked();
    if (!this.mReconfigureOnConfigurationChanged.contains(localObject)) {
      this.mReconfigureOnConfigurationChanged.add(localObject);
    }
    int i = this.mReconfigureOnConfigurationChanged.size() - 1;
    while (i >= 0)
    {
      reconfigureDisplayLocked((DisplayContent)this.mReconfigureOnConfigurationChanged.remove(i));
      i -= 1;
    }
    ((DisplayContent)localObject).getDockedDividerController().onConfigurationChanged();
    this.mChangedStackList.clear();
    i = this.mStackIdToStack.size() - 1;
    while (i >= 0)
    {
      localObject = (TaskStack)this.mStackIdToStack.valueAt(i);
      if (((TaskStack)localObject).onConfigurationChanged()) {
        this.mChangedStackList.add(Integer.valueOf(((TaskStack)localObject).mStackId));
      }
      i -= 1;
    }
    if (this.mChangedStackList.isEmpty()) {
      return null;
    }
    return ArrayUtils.convertToIntArray(this.mChangedStackList);
  }
  
  private void placeWindowAfter(WindowState paramWindowState1, WindowState paramWindowState2)
  {
    WindowList localWindowList = paramWindowState1.getWindowList();
    int i = localWindowList.indexOf(paramWindowState1);
    if ((WindowManagerDebugConfig.DEBUG_FOCUS) || (WindowManagerDebugConfig.DEBUG_WINDOW_MOVEMENT) || (WindowManagerDebugConfig.DEBUG_ADD_REMOVE)) {
      Slog.v("WindowManager", "Adding window " + paramWindowState2 + " at " + (i + 1) + " of " + localWindowList.size() + " (after " + paramWindowState1 + ")");
    }
    localWindowList.add(i + 1, paramWindowState2);
    this.mWindowsChanged = true;
  }
  
  private void placeWindowBefore(WindowState paramWindowState1, WindowState paramWindowState2)
  {
    WindowList localWindowList = paramWindowState1.getWindowList();
    int j = localWindowList.indexOf(paramWindowState1);
    if ((WindowManagerDebugConfig.DEBUG_FOCUS) || (WindowManagerDebugConfig.DEBUG_WINDOW_MOVEMENT) || (WindowManagerDebugConfig.DEBUG_ADD_REMOVE)) {
      Slog.v("WindowManager", "Adding window " + paramWindowState2 + " at " + j + " of " + localWindowList.size() + " (before " + paramWindowState1 + ")");
    }
    int i = j;
    if (j < 0)
    {
      Slog.w("WindowManager", "placeWindowBefore: Unable to find " + paramWindowState1 + " in " + localWindowList);
      i = 0;
    }
    localWindowList.add(i, paramWindowState2);
    this.mWindowsChanged = true;
  }
  
  private void prepareFreezingAllTaskBounds()
  {
    int i = this.mDisplayContents.size() - 1;
    while (i >= 0)
    {
      ArrayList localArrayList = ((DisplayContent)this.mDisplayContents.valueAt(i)).getStacks();
      int j = localArrayList.size() - 1;
      while (j >= 0)
      {
        ((TaskStack)localArrayList.get(j)).prepareFreezingTaskBounds();
        j -= 1;
      }
      i -= 1;
    }
  }
  
  private void prepareNoneTransitionForRelaunching(AppWindowToken paramAppWindowToken)
  {
    if ((!this.mDisplayFrozen) || (this.mOpeningApps.contains(paramAppWindowToken))) {}
    while (!paramAppWindowToken.isRelaunching()) {
      return;
    }
    this.mOpeningApps.add(paramAppWindowToken);
    prepareAppTransition(0, false);
    executeAppTransition();
  }
  
  private boolean prepareWindowReplacementTransition(AppWindowToken paramAppWindowToken)
  {
    paramAppWindowToken.clearAllDrawn();
    Object localObject1 = null;
    int i = paramAppWindowToken.windows.size() - 1;
    while ((i >= 0) && (localObject1 == null))
    {
      WindowState localWindowState = (WindowState)paramAppWindowToken.windows.get(i);
      Object localObject2 = localObject1;
      if (localWindowState.mAnimatingExit)
      {
        localObject2 = localObject1;
        if (localWindowState.mWillReplaceWindow)
        {
          localObject2 = localObject1;
          if (localWindowState.mAnimateReplacingWindow) {
            localObject2 = localWindowState;
          }
        }
      }
      i -= 1;
      localObject1 = localObject2;
    }
    if (localObject1 == null) {
      return false;
    }
    localObject1 = ((WindowState)localObject1).mVisibleFrame;
    this.mOpeningApps.add(paramAppWindowToken);
    prepareAppTransition(18, true);
    this.mAppTransition.overridePendingAppTransitionClipReveal(((Rect)localObject1).left, ((Rect)localObject1).top, ((Rect)localObject1).width(), ((Rect)localObject1).height());
    executeAppTransition();
    return true;
  }
  
  private final int reAddAppWindowsLocked(DisplayContent paramDisplayContent, int paramInt, WindowToken paramWindowToken)
  {
    int k = paramWindowToken.windows.size();
    int j = 0;
    int i = paramInt;
    paramInt = j;
    while (paramInt < k)
    {
      WindowState localWindowState = (WindowState)paramWindowToken.windows.get(paramInt);
      DisplayContent localDisplayContent = localWindowState.getDisplayContent();
      if (localDisplayContent != paramDisplayContent)
      {
        j = i;
        if (localDisplayContent != null) {}
      }
      else
      {
        localWindowState.mDisplayContent = paramDisplayContent;
        j = reAddWindowLocked(i, localWindowState);
      }
      paramInt += 1;
      i = j;
    }
    return i;
  }
  
  private final int reAddWindowLocked(int paramInt, WindowState paramWindowState)
  {
    WindowList localWindowList = paramWindowState.getWindowList();
    int n = paramWindowState.mChildWindows.size();
    int j = 0;
    int i = 0;
    while (i < n)
    {
      WindowState localWindowState = (WindowState)paramWindowState.mChildWindows.get(i);
      int k = j;
      int m = paramInt;
      if (j == 0)
      {
        k = j;
        m = paramInt;
        if (localWindowState.mSubLayer >= 0)
        {
          if (WindowManagerDebugConfig.DEBUG_WINDOW_MOVEMENT) {
            Slog.v("WindowManager", "Re-adding child window at " + paramInt + ": " + localWindowState);
          }
          paramWindowState.mRebuilding = false;
          localWindowList.add(paramInt, paramWindowState);
          m = paramInt + 1;
          k = 1;
        }
      }
      if (WindowManagerDebugConfig.DEBUG_WINDOW_MOVEMENT) {
        Slog.v("WindowManager", "Re-adding window at " + m + ": " + localWindowState);
      }
      localWindowState.mRebuilding = false;
      localWindowList.add(m, localWindowState);
      paramInt = m + 1;
      i += 1;
      j = k;
    }
    i = paramInt;
    if (j == 0)
    {
      if (WindowManagerDebugConfig.DEBUG_WINDOW_MOVEMENT) {
        Slog.v("WindowManager", "Re-adding window at " + paramInt + ": " + paramWindowState);
      }
      paramWindowState.mRebuilding = false;
      localWindowList.add(paramInt, paramWindowState);
      i = paramInt + 1;
    }
    this.mWindowsChanged = true;
    return i;
  }
  
  private void reAddWindowToListInOrderLocked(WindowState paramWindowState)
  {
    addWindowToListInOrderLocked(paramWindowState, false);
    WindowList localWindowList = paramWindowState.getWindowList();
    int i = localWindowList.indexOf(paramWindowState);
    if (i >= 0)
    {
      if (WindowManagerDebugConfig.DEBUG_WINDOW_MOVEMENT) {
        Slog.v("WindowManager", "ReAdd removing from " + i + ": " + paramWindowState);
      }
      localWindowList.remove(i);
      this.mWindowsChanged = true;
      reAddWindowLocked(i, paramWindowState);
    }
  }
  
  private void readForcedDisplayPropertiesLocked(DisplayContent paramDisplayContent)
  {
    String str2 = Settings.Global.getString(this.mContext.getContentResolver(), "display_size_forced");
    String str1;
    if (str2 != null)
    {
      str1 = str2;
      if (str2.length() != 0) {}
    }
    else
    {
      str1 = SystemProperties.get("ro.config.size_override", null);
    }
    int j;
    if ((str1 != null) && (str1.length() > 0))
    {
      j = str1.indexOf(',');
      if ((j <= 0) || (str1.lastIndexOf(',') != j)) {}
    }
    try
    {
      i = Integer.parseInt(str1.substring(0, j));
      j = Integer.parseInt(str1.substring(j + 1));
      if ((paramDisplayContent.mBaseDisplayWidth != i) || (paramDisplayContent.mBaseDisplayHeight != j))
      {
        Slog.i("WindowManager", "FORCED DISPLAY SIZE: " + i + "x" + j);
        paramDisplayContent.mBaseDisplayWidth = i;
        paramDisplayContent.mBaseDisplayHeight = j;
      }
    }
    catch (NumberFormatException localNumberFormatException)
    {
      int i;
      for (;;) {}
    }
    i = getForcedDisplayDensityForUserLocked(this.mCurrentUserId);
    if (i != 0) {
      paramDisplayContent.mBaseDisplayDensity = i;
    }
    if (Settings.Global.getInt(this.mContext.getContentResolver(), "display_scaling_force", 0) != 0)
    {
      Slog.i("WindowManager", "FORCED DISPLAY SCALING DISABLED");
      paramDisplayContent.mDisplayScalingDisabled = true;
    }
  }
  
  private void rebuildAppWindowListLocked(DisplayContent paramDisplayContent)
  {
    Object localObject1 = paramDisplayContent.getWindowList();
    int m = ((WindowList)localObject1).size();
    int k = -1;
    int j = 0;
    if (this.mRebuildTmp.length < m) {
      this.mRebuildTmp = new WindowState[m + 10];
    }
    int i = 0;
    Object localObject2;
    int n;
    while (i < m)
    {
      localObject2 = (WindowState)((WindowList)localObject1).get(i);
      if (((WindowState)localObject2).mAppToken != null)
      {
        localObject2 = (WindowState)((WindowList)localObject1).remove(i);
        ((WindowState)localObject2).mRebuilding = true;
        this.mRebuildTmp[j] = localObject2;
        this.mWindowsChanged = true;
        if (WindowManagerDebugConfig.DEBUG_WINDOW_MOVEMENT) {
          Slog.v("WindowManager", "Rebuild removing window: " + localObject2);
        }
        m -= 1;
        j += 1;
      }
      else
      {
        n = k;
        if (k == i - 1)
        {
          n = k;
          if (((WindowState)localObject2).mAttrs.type == 2013) {
            n = i;
          }
        }
        i += 1;
        k = n;
      }
    }
    int i2 = k + 1;
    i = i2;
    localObject1 = paramDisplayContent.getStacks();
    int i3 = ((ArrayList)localObject1).size();
    k = 0;
    while (k < i3)
    {
      localObject2 = ((TaskStack)((ArrayList)localObject1).get(k)).mExitingAppTokens;
      n = ((AppTokenList)localObject2).size();
      m = 0;
      while (m < n)
      {
        i = reAddAppWindowsLocked(paramDisplayContent, i, (WindowToken)((AppTokenList)localObject2).get(m));
        m += 1;
      }
      k += 1;
    }
    k = 0;
    m = i;
    while (k < i3)
    {
      localObject2 = ((TaskStack)((ArrayList)localObject1).get(k)).getTasks();
      int i4 = ((ArrayList)localObject2).size();
      i = 0;
      while (i < i4)
      {
        AppTokenList localAppTokenList = ((Task)((ArrayList)localObject2).get(i)).mAppTokens;
        int i5 = localAppTokenList.size();
        n = 0;
        while (n < i5)
        {
          AppWindowToken localAppWindowToken = (AppWindowToken)localAppTokenList.get(n);
          int i1;
          if (localAppWindowToken.mIsExiting)
          {
            i1 = m;
            if (!localAppWindowToken.waitingForReplacement()) {}
          }
          else
          {
            i1 = reAddAppWindowsLocked(paramDisplayContent, m, localAppWindowToken);
          }
          n += 1;
          m = i1;
        }
        i += 1;
      }
      k += 1;
    }
    i = m - i2;
    if (i != j)
    {
      paramDisplayContent.layoutNeeded = true;
      Slog.w("WindowManager", "On display=" + paramDisplayContent.getDisplayId() + " Rebuild removed " + j + " windows but added " + i + " rebuildAppWindowListLocked() " + " callers=" + Debug.getCallers(10));
      i = 0;
      while (i < j)
      {
        paramDisplayContent = this.mRebuildTmp[i];
        if (paramDisplayContent.mRebuilding)
        {
          localObject1 = new StringWriter();
          localObject2 = new FastPrintWriter((Writer)localObject1, false, 1024);
          paramDisplayContent.dump((PrintWriter)localObject2, "", true);
          ((PrintWriter)localObject2).flush();
          Slog.w("WindowManager", "This window was lost: " + paramDisplayContent);
          Slog.w("WindowManager", ((StringWriter)localObject1).toString());
          paramDisplayContent.mWinAnimator.destroySurfaceLocked();
        }
        i += 1;
      }
      Slog.w("WindowManager", "Current app token list:");
      dumpAppTokensLocked();
      Slog.w("WindowManager", "Final window list:");
      dumpWindowsLocked();
    }
    Arrays.fill(this.mRebuildTmp, null);
  }
  
  private void reconfigureDisplayLocked(DisplayContent paramDisplayContent)
  {
    if (!this.mDisplayReady) {
      return;
    }
    configureDisplayPolicyLocked(paramDisplayContent);
    paramDisplayContent.layoutNeeded = true;
    boolean bool2 = updateOrientationFromAppTokensLocked(false);
    this.mTempConfiguration.setToDefaults();
    this.mTempConfiguration.updateFrom(this.mCurConfiguration);
    computeScreenConfigurationLocked(this.mTempConfiguration);
    if (this.mCurConfiguration.diff(this.mTempConfiguration) != 0) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      if ((bool2 | bool1))
      {
        this.mWaitingForConfig = true;
        startFreezingDisplayLocked(false, 0, 0);
        this.mH.sendEmptyMessage(18);
        if (!this.mReconfigureOnConfigurationChanged.contains(paramDisplayContent)) {
          this.mReconfigureOnConfigurationChanged.add(paramDisplayContent);
        }
      }
      this.mWindowPlacerLocked.performSurfacePlacement();
      return;
    }
  }
  
  private int reduceCompatConfigWidthSize(int paramInt1, int paramInt2, int paramInt3, DisplayMetrics paramDisplayMetrics, int paramInt4, int paramInt5)
  {
    paramDisplayMetrics.noncompatWidthPixels = this.mPolicy.getNonDecorDisplayWidth(paramInt4, paramInt5, paramInt2, paramInt3);
    paramDisplayMetrics.noncompatHeightPixels = this.mPolicy.getNonDecorDisplayHeight(paramInt4, paramInt5, paramInt2, paramInt3);
    float f = CompatibilityInfo.computeCompatibleScaling(paramDisplayMetrics, null);
    paramInt3 = (int)(paramDisplayMetrics.noncompatWidthPixels / f / paramDisplayMetrics.density + 0.5F);
    if (paramInt1 != 0)
    {
      paramInt2 = paramInt1;
      if (paramInt3 >= paramInt1) {}
    }
    else
    {
      paramInt2 = paramInt3;
    }
    return paramInt2;
  }
  
  private int reduceConfigLayout(int paramInt1, int paramInt2, float paramFloat, int paramInt3, int paramInt4, int paramInt5)
  {
    int i = this.mPolicy.getNonDecorDisplayWidth(paramInt3, paramInt4, paramInt2, paramInt5);
    paramInt2 = this.mPolicy.getNonDecorDisplayHeight(paramInt3, paramInt4, paramInt2, paramInt5);
    paramInt4 = i;
    paramInt3 = paramInt2;
    if (i < paramInt2)
    {
      paramInt3 = i;
      paramInt4 = paramInt2;
    }
    return Configuration.reduceScreenLayout(paramInt1, (int)(paramInt4 / paramFloat), (int)(paramInt3 / paramFloat));
  }
  
  private int relayoutVisibleWindow(Configuration paramConfiguration, int paramInt1, WindowState paramWindowState, WindowStateAnimator paramWindowStateAnimator, int paramInt2, int paramInt3)
  {
    int j = 0;
    int i;
    if (!paramWindowState.isVisibleLw())
    {
      i = 2;
      i = paramInt1 | i;
      if (paramWindowState.mAnimatingExit)
      {
        Slog.d(TAG, "relayoutVisibleWindow: " + paramWindowState + " mAnimatingExit=true, mRemoveOnExit=" + paramWindowState.mRemoveOnExit + ", mDestroying=" + paramWindowState.mDestroying);
        paramWindowStateAnimator.cancelExitAnimationForNextAnimationLocked();
        paramWindowState.mAnimatingExit = false;
      }
      if (paramWindowState.mDestroying)
      {
        paramWindowState.mDestroying = false;
        this.mDestroySurface.remove(paramWindowState);
      }
      if (paramInt3 == 8) {
        paramWindowStateAnimator.mEnterAnimationPending = true;
      }
      paramWindowState.mLastVisibleLayoutRotation = this.mRotation;
      paramWindowStateAnimator.mEnteringAnimation = true;
      if ((i & 0x2) != 0) {
        paramWindowState.prepareWindowToDisplayDuringRelayout(paramConfiguration);
      }
      paramInt1 = i;
      if ((paramInt2 & 0x8) != 0)
      {
        paramInt1 = i;
        if (!paramWindowStateAnimator.tryChangeFormatInPlaceLocked())
        {
          paramWindowStateAnimator.preserveSurfaceLocked();
          paramInt1 = i | 0x6;
        }
      }
      if (!paramWindowState.isDragResizeChanged())
      {
        paramInt3 = paramInt1;
        if (!paramWindowState.isResizedWhileNotDragResizing()) {}
      }
      else
      {
        paramWindowState.setDragResizing();
        paramWindowState.setResizedWhileNotDragResizing(false);
        paramInt3 = paramInt1;
        if (paramWindowState.mHasSurface)
        {
          paramInt3 = paramInt1;
          if (paramWindowState.mAttachedWindow == null)
          {
            paramWindowStateAnimator.preserveSurfaceLocked();
            paramInt3 = paramInt1 | 0x2;
          }
        }
      }
      if (!paramWindowState.isDragResizing()) {
        break label332;
      }
      if (paramWindowState.getResizeMode() != 0) {
        break label326;
      }
      paramInt2 = 1;
      label255:
      if (!paramWindowState.isDragResizing()) {
        break label343;
      }
      if (paramWindowState.getResizeMode() != 1) {
        break label338;
      }
      paramInt1 = 1;
      label272:
      if (paramInt2 == 0) {
        break label348;
      }
    }
    label326:
    label332:
    label338:
    label343:
    label348:
    for (paramInt2 = 16;; paramInt2 = 0)
    {
      i = j;
      if (paramInt1 != 0) {
        i = 8;
      }
      paramInt2 = paramInt3 | paramInt2 | i;
      paramInt1 = paramInt2;
      if (paramWindowState.isAnimatingWithSavedSurface()) {
        paramInt1 = paramInt2 | 0x2;
      }
      return paramInt1;
      i = 0;
      break;
      paramInt2 = 0;
      break label255;
      paramInt2 = 0;
      break label255;
      paramInt1 = 0;
      break label272;
      paramInt1 = 0;
      break label272;
    }
  }
  
  private void setAnimatorDurationScale(float paramFloat)
  {
    this.mAnimatorDurationScaleSetting = paramFloat;
    ValueAnimator.setDurationScale(paramFloat);
  }
  
  private void setForcedDisplayDensityLocked(DisplayContent paramDisplayContent, int paramInt)
  {
    paramDisplayContent.mBaseDisplayDensity = paramInt;
    reconfigureDisplayLocked(paramDisplayContent);
  }
  
  private void setForcedDisplayScalingModeLocked(DisplayContent paramDisplayContent, int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder().append("Using display scaling mode: ");
    String str;
    if (paramInt == 0)
    {
      str = "auto";
      Slog.i("WindowManager", str);
      if (paramInt == 0) {
        break label66;
      }
    }
    label66:
    for (boolean bool = true;; bool = false)
    {
      paramDisplayContent.mDisplayScalingDisabled = bool;
      reconfigureDisplayLocked(paramDisplayContent);
      return;
      str = "off";
      break;
    }
  }
  
  private void setForcedDisplaySizeLocked(DisplayContent paramDisplayContent, int paramInt1, int paramInt2)
  {
    Slog.i("WindowManager", "Using new display size: " + paramInt1 + "x" + paramInt2);
    paramDisplayContent.mBaseDisplayWidth = paramInt1;
    paramDisplayContent.mBaseDisplayHeight = paramInt2;
    reconfigureDisplayLocked(paramDisplayContent);
  }
  
  private void setOverscanLocked(DisplayContent paramDisplayContent, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    DisplayInfo localDisplayInfo = paramDisplayContent.getDisplayInfo();
    localDisplayInfo.overscanLeft = paramInt1;
    localDisplayInfo.overscanTop = paramInt2;
    localDisplayInfo.overscanRight = paramInt3;
    localDisplayInfo.overscanBottom = paramInt4;
    this.mDisplaySettings.setOverscanLocked(localDisplayInfo.uniqueId, localDisplayInfo.name, paramInt1, paramInt2, paramInt3, paramInt4);
    this.mDisplaySettings.writeSettingsLocked();
    reconfigureDisplayLocked(paramDisplayContent);
  }
  
  private void setupWindowForRemoveOnExit(WindowState paramWindowState)
  {
    paramWindowState.mRemoveOnExit = true;
    paramWindowState.setDisplayLayoutNeeded();
    boolean bool = updateFocusedWindowLocked(3, false);
    this.mWindowPlacerLocked.performSurfacePlacement();
    if (bool) {
      this.mInputMonitor.updateInputWindowsLw(false);
    }
  }
  
  private void showAuditSafeModeNotification()
  {
    Object localObject = PendingIntent.getActivity(this.mContext, 0, new Intent("android.intent.action.VIEW", Uri.parse("https://support.google.com/nexus/answer/2852139")), 0);
    String str = this.mContext.getString(17040916);
    localObject = new Notification.Builder(this.mContext).setSmallIcon(17301642).setWhen(0L).setOngoing(true).setTicker(str).setLocalOnly(true).setPriority(1).setVisibility(1).setColor(this.mContext.getColor(17170523)).setContentTitle(str).setContentText(this.mContext.getString(17040917)).setContentIntent((PendingIntent)localObject).build();
    ((NotificationManager)this.mContext.getSystemService("notification")).notifyAsUser(null, 17040916, (Notification)localObject, UserHandle.ALL);
  }
  
  /* Error */
  private void showStrictModeViolation(int paramInt1, int paramInt2)
  {
    // Byte code:
    //   0: iload_1
    //   1: ifeq +124 -> 125
    //   4: iconst_1
    //   5: istore 8
    //   7: aload_0
    //   8: getfield 491	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
    //   11: astore 10
    //   13: aload 10
    //   15: monitorenter
    //   16: iload 8
    //   18: ifeq +130 -> 148
    //   21: iconst_0
    //   22: istore_3
    //   23: aload_0
    //   24: getfield 566	com/android/server/wm/WindowManagerService:mDisplayContents	Landroid/util/SparseArray;
    //   27: invokevirtual 1541	android/util/SparseArray:size	()I
    //   30: istore 6
    //   32: iconst_0
    //   33: istore_1
    //   34: iload_1
    //   35: iload 6
    //   37: if_icmpge +103 -> 140
    //   40: aload_0
    //   41: getfield 566	com/android/server/wm/WindowManagerService:mDisplayContents	Landroid/util/SparseArray;
    //   44: iload_1
    //   45: invokevirtual 1544	android/util/SparseArray:valueAt	(I)Ljava/lang/Object;
    //   48: checkcast 1004	com/android/server/wm/DisplayContent
    //   51: invokevirtual 1008	com/android/server/wm/DisplayContent:getWindowList	()Lcom/android/server/wm/WindowList;
    //   54: astore 11
    //   56: aload 11
    //   58: invokevirtual 1082	com/android/server/wm/WindowList:size	()I
    //   61: istore 7
    //   63: iconst_0
    //   64: istore 5
    //   66: iload_3
    //   67: istore 4
    //   69: iload 5
    //   71: iload 7
    //   73: if_icmpge +42 -> 115
    //   76: aload 11
    //   78: iload 5
    //   80: invokevirtual 1083	com/android/server/wm/WindowList:get	(I)Ljava/lang/Object;
    //   83: checkcast 508	com/android/server/wm/WindowState
    //   86: astore 12
    //   88: aload 12
    //   90: getfield 2486	com/android/server/wm/WindowState:mSession	Lcom/android/server/wm/Session;
    //   93: getfield 2491	com/android/server/wm/Session:mPid	I
    //   96: iload_2
    //   97: if_icmpne +34 -> 131
    //   100: aload 12
    //   102: invokevirtual 1503	com/android/server/wm/WindowState:isVisibleLw	()Z
    //   105: istore 9
    //   107: iload 9
    //   109: ifeq +22 -> 131
    //   112: iconst_1
    //   113: istore 4
    //   115: iload_1
    //   116: iconst_1
    //   117: iadd
    //   118: istore_1
    //   119: iload 4
    //   121: istore_3
    //   122: goto -88 -> 34
    //   125: iconst_0
    //   126: istore 8
    //   128: goto -121 -> 7
    //   131: iload 5
    //   133: iconst_1
    //   134: iadd
    //   135: istore 5
    //   137: goto -71 -> 66
    //   140: iload_3
    //   141: ifne +7 -> 148
    //   144: aload 10
    //   146: monitorexit
    //   147: return
    //   148: getstatic 2494	com/android/server/wm/WindowManagerDebugConfig:SHOW_VERBOSE_TRANSACTIONS	Z
    //   151: ifeq +13 -> 164
    //   154: ldc_w 440
    //   157: ldc_w 2496
    //   160: invokestatic 1392	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   163: pop
    //   164: invokestatic 976	android/view/SurfaceControl:openTransaction	()V
    //   167: aload_0
    //   168: getfield 2498	com/android/server/wm/WindowManagerService:mStrictModeFlash	Lcom/android/server/wm/StrictModeFlash;
    //   171: ifnonnull +25 -> 196
    //   174: aload_0
    //   175: new 2500	com/android/server/wm/StrictModeFlash
    //   178: dup
    //   179: aload_0
    //   180: invokevirtual 1810	com/android/server/wm/WindowManagerService:getDefaultDisplayContentLocked	()Lcom/android/server/wm/DisplayContent;
    //   183: invokevirtual 1583	com/android/server/wm/DisplayContent:getDisplay	()Landroid/view/Display;
    //   186: aload_0
    //   187: getfield 808	com/android/server/wm/WindowManagerService:mFxSession	Landroid/view/SurfaceSession;
    //   190: invokespecial 2503	com/android/server/wm/StrictModeFlash:<init>	(Landroid/view/Display;Landroid/view/SurfaceSession;)V
    //   193: putfield 2498	com/android/server/wm/WindowManagerService:mStrictModeFlash	Lcom/android/server/wm/StrictModeFlash;
    //   196: aload_0
    //   197: getfield 2498	com/android/server/wm/WindowManagerService:mStrictModeFlash	Lcom/android/server/wm/StrictModeFlash;
    //   200: iload 8
    //   202: invokevirtual 2505	com/android/server/wm/StrictModeFlash:setVisibility	(Z)V
    //   205: invokestatic 982	android/view/SurfaceControl:closeTransaction	()V
    //   208: getstatic 2494	com/android/server/wm/WindowManagerDebugConfig:SHOW_VERBOSE_TRANSACTIONS	Z
    //   211: ifeq +13 -> 224
    //   214: ldc_w 440
    //   217: ldc_w 2507
    //   220: invokestatic 1392	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   223: pop
    //   224: aload 10
    //   226: monitorexit
    //   227: return
    //   228: astore 11
    //   230: invokestatic 982	android/view/SurfaceControl:closeTransaction	()V
    //   233: getstatic 2494	com/android/server/wm/WindowManagerDebugConfig:SHOW_VERBOSE_TRANSACTIONS	Z
    //   236: ifeq +13 -> 249
    //   239: ldc_w 440
    //   242: ldc_w 2507
    //   245: invokestatic 1392	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   248: pop
    //   249: aload 11
    //   251: athrow
    //   252: astore 11
    //   254: aload 10
    //   256: monitorexit
    //   257: aload 11
    //   259: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	260	0	this	WindowManagerService
    //   0	260	1	paramInt1	int
    //   0	260	2	paramInt2	int
    //   22	119	3	i	int
    //   67	53	4	j	int
    //   64	72	5	k	int
    //   30	8	6	m	int
    //   61	13	7	n	int
    //   5	196	8	bool1	boolean
    //   105	3	9	bool2	boolean
    //   11	244	10	localHashMap	HashMap
    //   54	23	11	localWindowList	WindowList
    //   228	22	11	localObject1	Object
    //   252	6	11	localObject2	Object
    //   86	15	12	localWindowState	WindowState
    // Exception table:
    //   from	to	target	type
    //   167	196	228	finally
    //   196	205	228	finally
    //   23	32	252	finally
    //   40	63	252	finally
    //   76	107	252	finally
    //   148	164	252	finally
    //   164	167	252	finally
    //   205	224	252	finally
    //   230	249	252	finally
    //   249	252	252	finally
  }
  
  private void startAppFreezingScreenLocked(AppWindowToken paramAppWindowToken)
  {
    if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
      logWithStack(TAG, "Set freezing of " + paramAppWindowToken.appToken + ": hidden=" + paramAppWindowToken.hidden + " freezing=" + paramAppWindowToken.mAppAnimator.freezingScreen);
    }
    if (!paramAppWindowToken.hiddenRequested)
    {
      if (!paramAppWindowToken.mAppAnimator.freezingScreen)
      {
        paramAppWindowToken.mAppAnimator.freezingScreen = true;
        paramAppWindowToken.mAppAnimator.lastFreezeDuration = 0;
        this.mAppsFreezingScreen += 1;
        if (this.mAppsFreezingScreen == 1)
        {
          startFreezingDisplayLocked(false, 0, 0);
          this.mH.removeMessages(17);
          this.mH.sendEmptyMessageDelayed(17, 2000L);
        }
      }
      int j = paramAppWindowToken.allAppWindows.size();
      int i = 0;
      while (i < j)
      {
        ((WindowState)paramAppWindowToken.allAppWindows.get(i)).mAppFreezing = true;
        i += 1;
      }
    }
  }
  
  private void startFreezingDisplayLocked(boolean paramBoolean, int paramInt1, int paramInt2)
  {
    if (this.mDisplayFrozen) {
      return;
    }
    Object localObject1;
    Object localObject2;
    boolean bool2;
    int i;
    if ((this.mDisplayReady) && (this.mPolicy.isScreenOn()))
    {
      if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
        Slog.d("WindowManager", "startFreezingDisplayLocked: inTransaction=" + paramBoolean + " exitAnim=" + paramInt1 + " enterAnim=" + paramInt2 + " called by " + Debug.getCallers(8));
      }
      this.mScreenFrozenLock.acquire();
      this.mDisplayFrozen = true;
      this.mDisplayFreezeTime = SystemClock.elapsedRealtime();
      this.mLastFinishedFreezeSource = null;
      this.mInputMonitor.freezeInputDispatchingLw();
      this.mPolicy.setLastInputMethodWindowLw(null, null);
      if (this.mAppTransition.isTransitionSet()) {
        this.mAppTransition.freeze();
      }
      if (PROFILE_ORIENTATION) {
        Debug.startMethodTracing(new File("/data/system/frozen").toString(), 8388608);
      }
      this.mExitAnimId = paramInt1;
      this.mEnterAnimId = paramInt2;
      localObject1 = getDefaultDisplayContentLocked();
      paramInt2 = ((DisplayContent)localObject1).getDisplayId();
      localObject2 = this.mAnimator.getScreenRotationAnimationLocked(paramInt2);
      if (localObject2 != null) {
        ((ScreenRotationAnimation)localObject2).kill();
      }
      bool2 = false;
      localObject2 = getDefaultWindowListLocked();
      i = ((WindowList)localObject2).size();
      paramInt1 = 0;
    }
    for (;;)
    {
      boolean bool1 = bool2;
      if (paramInt1 < i)
      {
        WindowState localWindowState = (WindowState)((WindowList)localObject2).get(paramInt1);
        if ((localWindowState.isOnScreen()) && ((localWindowState.mAttrs.flags & 0x2000) != 0)) {
          bool1 = true;
        }
      }
      else
      {
        ((DisplayContent)localObject1).updateDisplayInfo();
        localObject1 = new ScreenRotationAnimation(this.mContext, (DisplayContent)localObject1, this.mFxSession, paramBoolean, this.mPolicy.isDefaultOrientationForced(), bool1);
        this.mAnimator.setScreenRotationAnimationLocked(paramInt2, (ScreenRotationAnimation)localObject1);
        return;
        return;
      }
      paramInt1 += 1;
    }
  }
  
  private boolean startPositioningLocked(WindowState paramWindowState, boolean paramBoolean, float paramFloat1, float paramFloat2)
  {
    if (WindowManagerDebugConfig.DEBUG_TASK_POSITIONING) {
      Slog.d("WindowManager", "startPositioningLocked: win=" + paramWindowState + ", resize=" + paramBoolean + ", {" + paramFloat1 + ", " + paramFloat2 + "}");
    }
    if ((paramWindowState == null) || (paramWindowState.getAppToken() == null))
    {
      Slog.w("WindowManager", "startPositioningLocked: Bad window " + paramWindowState);
      return false;
    }
    if (paramWindowState.mInputChannel == null)
    {
      Slog.wtf("WindowManager", "startPositioningLocked: " + paramWindowState + " has no input channel, " + " probably being removed");
      return false;
    }
    Object localObject1 = paramWindowState.getDisplayContent();
    if (localObject1 == null)
    {
      Slog.w("WindowManager", "startPositioningLocked: Invalid display content " + paramWindowState);
      return false;
    }
    localObject1 = ((DisplayContent)localObject1).getDisplay();
    this.mTaskPositioner = new TaskPositioner(this);
    this.mTaskPositioner.register((Display)localObject1);
    this.mInputMonitor.updateInputWindowsLw(true);
    localObject1 = paramWindowState;
    Object localObject2 = localObject1;
    if (this.mCurrentFocus != null)
    {
      localObject2 = localObject1;
      if (this.mCurrentFocus != paramWindowState)
      {
        localObject2 = localObject1;
        if (this.mCurrentFocus.mAppToken == paramWindowState.mAppToken) {
          localObject2 = this.mCurrentFocus;
        }
      }
    }
    if (!this.mInputManager.transferTouchFocus(((WindowState)localObject2).mInputChannel, this.mTaskPositioner.mServerChannel))
    {
      Slog.e("WindowManager", "startPositioningLocked: Unable to transfer touch focus");
      this.mTaskPositioner.unregister();
      this.mTaskPositioner = null;
      this.mInputMonitor.updateInputWindowsLw(true);
      return false;
    }
    this.mTaskPositioner.startDragLocked(paramWindowState, paramBoolean, paramFloat1, paramFloat2);
    return true;
  }
  
  private void startScrollingTask(DisplayContent paramDisplayContent, int paramInt1, int paramInt2)
  {
    if (WindowManagerDebugConfig.DEBUG_TASK_POSITIONING) {
      Slog.d("WindowManager", "startScrollingTask: {" + paramInt1 + ", " + paramInt2 + "}");
    }
    Object localObject = null;
    synchronized (this.mWindowMap)
    {
      int i = paramDisplayContent.taskIdFromPoint(paramInt1, paramInt2);
      paramDisplayContent = (DisplayContent)localObject;
      if (i >= 0) {
        paramDisplayContent = (Task)this.mTaskIdToTask.get(i);
      }
      if ((paramDisplayContent != null) && (paramDisplayContent.isDockedInEffect()))
      {
        boolean bool = startPositioningLocked(paramDisplayContent.getTopVisibleAppMainWindow(), false, paramInt1, paramInt2);
        if (!bool) {}
      }
      try
      {
        this.mActivityManager.setFocusedTask(paramDisplayContent.mTaskId);
        return;
      }
      catch (RemoteException paramDisplayContent) {}
      return;
    }
  }
  
  private int tmpRemoveWindowLocked(int paramInt, WindowState paramWindowState)
  {
    WindowList localWindowList = paramWindowState.getWindowList();
    int k = localWindowList.indexOf(paramWindowState);
    int j = paramInt;
    if (k >= 0)
    {
      int i = paramInt;
      if (k < paramInt) {
        i = paramInt - 1;
      }
      if (WindowManagerDebugConfig.DEBUG_WINDOW_MOVEMENT) {
        Slog.v("WindowManager", "Temp removing at " + k + ": " + paramWindowState);
      }
      localWindowList.remove(k);
      this.mWindowsChanged = true;
      j = paramWindowState.mChildWindows.size();
      paramInt = i;
      i = j;
      for (;;)
      {
        j = paramInt;
        if (i <= 0) {
          break;
        }
        k = i - 1;
        WindowState localWindowState = (WindowState)paramWindowState.mChildWindows.get(k);
        int m = localWindowList.indexOf(localWindowState);
        i = k;
        if (m >= 0)
        {
          j = paramInt;
          if (m < paramInt) {
            j = paramInt - 1;
          }
          if (WindowManagerDebugConfig.DEBUG_WINDOW_MOVEMENT) {
            Slog.v("WindowManager", "Temp removing child at " + m + ": " + localWindowState);
          }
          localWindowList.remove(m);
          i = k;
          paramInt = j;
        }
      }
    }
    return j;
  }
  
  private boolean transferStartingWindow(IBinder paramIBinder, AppWindowToken paramAppWindowToken)
  {
    if (paramIBinder == null) {
      return false;
    }
    paramIBinder = findAppWindowToken(paramIBinder);
    if (paramIBinder == null) {
      return false;
    }
    WindowState localWindowState = paramIBinder.startingWindow;
    if ((localWindowState != null) && (paramIBinder.startingView != null))
    {
      this.mSkipAppTransitionAnimation = true;
      if (WindowManagerDebugConfig.DEBUG_STARTING_WINDOW) {
        Slog.v("WindowManager", "Moving existing starting " + localWindowState + " from " + paramIBinder + " to " + paramAppWindowToken);
      }
      long l = Binder.clearCallingIdentity();
      paramAppWindowToken.startingData = paramIBinder.startingData;
      paramAppWindowToken.startingView = paramIBinder.startingView;
      paramAppWindowToken.startingDisplayed = paramIBinder.startingDisplayed;
      paramIBinder.startingDisplayed = false;
      paramAppWindowToken.startingWindow = localWindowState;
      paramAppWindowToken.reportedVisible = paramIBinder.reportedVisible;
      paramIBinder.startingData = null;
      paramIBinder.startingView = null;
      paramIBinder.startingWindow = null;
      paramIBinder.startingMoved = true;
      localWindowState.mToken = paramAppWindowToken;
      localWindowState.mRootToken = paramAppWindowToken;
      localWindowState.mAppToken = paramAppWindowToken;
      if ((WindowManagerDebugConfig.DEBUG_WINDOW_MOVEMENT) || (WindowManagerDebugConfig.DEBUG_ADD_REMOVE) || (WindowManagerDebugConfig.DEBUG_STARTING_WINDOW)) {
        Slog.v("WindowManager", "Removing starting window: " + localWindowState);
      }
      localWindowState.getWindowList().remove(localWindowState);
      this.mWindowsChanged = true;
      if (WindowManagerDebugConfig.DEBUG_ADD_REMOVE) {
        Slog.v("WindowManager", "Removing starting " + localWindowState + " from " + paramIBinder);
      }
      paramIBinder.windows.remove(localWindowState);
      paramIBinder.allAppWindows.remove(localWindowState);
      addWindowToListInOrderLocked(localWindowState, true);
      if (paramIBinder.allDrawn)
      {
        paramAppWindowToken.allDrawn = true;
        paramAppWindowToken.deferClearAllDrawn = paramIBinder.deferClearAllDrawn;
      }
      if (paramIBinder.firstWindowDrawn) {
        paramAppWindowToken.firstWindowDrawn = true;
      }
      if (!paramIBinder.hidden)
      {
        paramAppWindowToken.hidden = false;
        paramAppWindowToken.hiddenRequested = false;
      }
      if (paramAppWindowToken.clientHidden != paramIBinder.clientHidden)
      {
        paramAppWindowToken.clientHidden = paramIBinder.clientHidden;
        paramAppWindowToken.sendAppVisibilityToClients();
      }
      paramIBinder.mAppAnimator.transferCurrentAnimation(paramAppWindowToken.mAppAnimator, localWindowState.mWinAnimator);
      updateFocusedWindowLocked(3, true);
      getDefaultDisplayContentLocked().layoutNeeded = true;
      this.mWindowPlacerLocked.performSurfacePlacement();
      Binder.restoreCallingIdentity(l);
      return true;
    }
    if (paramIBinder.startingData != null)
    {
      if (WindowManagerDebugConfig.DEBUG_STARTING_WINDOW) {
        Slog.v("WindowManager", "Moving pending starting from " + paramIBinder + " to " + paramAppWindowToken);
      }
      paramAppWindowToken.startingData = paramIBinder.startingData;
      paramIBinder.startingData = null;
      paramIBinder.startingMoved = true;
      paramIBinder = this.mH.obtainMessage(5, paramAppWindowToken);
      this.mH.sendMessageAtFrontOfQueue(paramIBinder);
      return true;
    }
    paramIBinder = paramIBinder.mAppAnimator;
    paramAppWindowToken = paramAppWindowToken.mAppAnimator;
    if (paramIBinder.thumbnail != null)
    {
      if (paramAppWindowToken.thumbnail != null) {
        paramAppWindowToken.thumbnail.destroy();
      }
      paramAppWindowToken.thumbnail = paramIBinder.thumbnail;
      paramAppWindowToken.thumbnailLayer = paramIBinder.thumbnailLayer;
      paramAppWindowToken.thumbnailAnimation = paramIBinder.thumbnailAnimation;
      paramIBinder.thumbnail = null;
    }
    return false;
  }
  
  private boolean tryStartExitingAnimation(WindowState paramWindowState, WindowStateAnimator paramWindowStateAnimator, boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = 2;
    if (paramWindowState.mAttrs.type == 3) {
      i = 5;
    }
    if ((paramWindowState.isWinVisibleLw()) && (paramWindowStateAnimator.applyAnimationLocked(i, false)))
    {
      paramWindowState.mAnimatingExit = true;
      paramWindowState.mWinAnimator.mAnimating = true;
    }
    for (;;)
    {
      if ((this.mAccessibilityController != null) && (paramWindowState.getDisplayId() == 0)) {
        this.mAccessibilityController.onWindowTransitionLocked(paramWindowState, i);
      }
      return paramBoolean1;
      if (paramWindowState.mWinAnimator.isAnimationSet())
      {
        paramWindowState.mAnimatingExit = true;
        paramWindowState.mWinAnimator.mAnimating = true;
        paramBoolean1 = paramBoolean2;
      }
      else if (this.mWallpaperControllerLocked.isWallpaperTarget(paramWindowState))
      {
        paramWindowState.mAnimatingExit = true;
        paramWindowState.mWinAnimator.mAnimating = true;
        paramBoolean1 = paramBoolean2;
      }
      else
      {
        if (this.mInputMethodWindow == paramWindowState) {
          this.mInputMethodWindow = null;
        }
        paramWindowState.destroyOrSaveSurface();
        paramBoolean1 = paramBoolean2;
      }
    }
  }
  
  private void updateCircularDisplayMaskIfNeeded()
  {
    if ((this.mContext.getResources().getConfiguration().isScreenRound()) && (this.mContext.getResources().getBoolean(17957004))) {}
    for (;;)
    {
      synchronized (this.mWindowMap)
      {
        i = this.mCurrentUserId;
        if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "accessibility_display_inversion_enabled", 0, i) == 1)
        {
          i = 0;
          ??? = this.mH.obtainMessage(35);
          ((Message)???).arg1 = i;
          this.mH.sendMessage((Message)???);
          return;
        }
      }
      int i = 1;
    }
  }
  
  private Configuration updateOrientationFromAppTokensLocked(Configuration paramConfiguration, IBinder paramIBinder)
  {
    if (!this.mDisplayReady) {
      return null;
    }
    Object localObject = null;
    if (updateOrientationFromAppTokensLocked(false)) {
      if ((paramIBinder == null) || (this.mWindowPlacerLocked.mOrientationChangeComplete)) {
        paramIBinder = computeNewConfigurationLocked();
      }
    }
    do
    {
      do
      {
        return paramIBinder;
        paramConfiguration = findAppWindowToken(paramIBinder);
        if (paramConfiguration == null) {
          break;
        }
        startAppFreezingScreenLocked(paramConfiguration);
        break;
        paramIBinder = (IBinder)localObject;
      } while (paramConfiguration == null);
      this.mTempConfiguration.setToDefaults();
      this.mTempConfiguration.updateFrom(paramConfiguration);
      computeScreenConfigurationLocked(this.mTempConfiguration);
      paramIBinder = (IBinder)localObject;
    } while (paramConfiguration.diff(this.mTempConfiguration) == 0);
    this.mWaitingForConfig = true;
    paramConfiguration = getDefaultDisplayContentLocked();
    paramConfiguration.layoutNeeded = true;
    paramIBinder = new int[2];
    if (paramConfiguration.isDimming())
    {
      paramIBinder[1] = 0;
      paramIBinder[0] = 0;
    }
    for (;;)
    {
      startFreezingDisplayLocked(false, paramIBinder[0], paramIBinder[1]);
      return new Configuration(this.mTempConfiguration);
      this.mPolicy.selectRotationAnimationLw(paramIBinder);
    }
  }
  
  /* Error */
  public void addAppToken(int paramInt1, IApplicationToken paramIApplicationToken, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, boolean paramBoolean2, int paramInt5, int paramInt6, boolean paramBoolean3, boolean paramBoolean4, Rect paramRect, Configuration paramConfiguration, int paramInt7, boolean paramBoolean5, boolean paramBoolean6, int paramInt8, int paramInt9)
  {
    // Byte code:
    //   0: aload_0
    //   1: ldc_w 2825
    //   4: ldc_w 2827
    //   7: invokespecial 2829	com/android/server/wm/WindowManagerService:checkCallingPermission	(Ljava/lang/String;Ljava/lang/String;)Z
    //   10: ifne +14 -> 24
    //   13: new 1764	java/lang/SecurityException
    //   16: dup
    //   17: ldc_w 2831
    //   20: invokespecial 1769	java/lang/SecurityException:<init>	(Ljava/lang/String;)V
    //   23: athrow
    //   24: aload_2
    //   25: invokeinterface 2836 1 0
    //   30: lstore 19
    //   32: lload 19
    //   34: ldc2_w 2837
    //   37: lmul
    //   38: lstore 19
    //   40: aload_0
    //   41: getfield 491	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
    //   44: astore 23
    //   46: aload 23
    //   48: monitorenter
    //   49: aload_0
    //   50: aload_2
    //   51: invokeinterface 2839 1 0
    //   56: invokevirtual 2672	com/android/server/wm/WindowManagerService:findAppWindowToken	(Landroid/os/IBinder;)Lcom/android/server/wm/AppWindowToken;
    //   59: ifnull +56 -> 115
    //   62: ldc_w 440
    //   65: new 1023	java/lang/StringBuilder
    //   68: dup
    //   69: invokespecial 1024	java/lang/StringBuilder:<init>	()V
    //   72: ldc_w 2841
    //   75: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   78: aload_2
    //   79: invokevirtual 1039	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   82: invokevirtual 1046	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   85: invokestatic 1495	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   88: pop
    //   89: aload 23
    //   91: monitorexit
    //   92: return
    //   93: astore 21
    //   95: ldc_w 440
    //   98: ldc_w 2843
    //   101: aload 21
    //   103: invokestatic 2845	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   106: pop
    //   107: ldc2_w 75
    //   110: lstore 19
    //   112: goto -72 -> 40
    //   115: new 1076	com/android/server/wm/AppWindowToken
    //   118: dup
    //   119: aload_0
    //   120: aload_2
    //   121: iload 10
    //   123: invokespecial 2848	com/android/server/wm/AppWindowToken:<init>	(Lcom/android/server/wm/WindowManagerService;Landroid/view/IApplicationToken;Z)V
    //   126: astore 24
    //   128: aload 24
    //   130: lload 19
    //   132: putfield 2851	com/android/server/wm/AppWindowToken:inputDispatchingTimeoutNanos	J
    //   135: aload 24
    //   137: iload 6
    //   139: putfield 1852	com/android/server/wm/AppWindowToken:appFullscreen	Z
    //   142: aload 24
    //   144: iload 7
    //   146: putfield 1703	com/android/server/wm/AppWindowToken:showForAllUsers	Z
    //   149: aload 24
    //   151: iload 17
    //   153: putfield 1742	com/android/server/wm/AppWindowToken:targetSdk	I
    //   156: aload 24
    //   158: iload 5
    //   160: putfield 1849	com/android/server/wm/AppWindowToken:requestedOrientation	I
    //   163: iload 9
    //   165: sipush 1152
    //   168: iand
    //   169: ifeq +188 -> 357
    //   172: iconst_1
    //   173: istore 6
    //   175: aload 24
    //   177: iload 6
    //   179: putfield 2854	com/android/server/wm/AppWindowToken:layoutConfigChanges	Z
    //   182: aload 24
    //   184: iload 11
    //   186: putfield 1296	com/android/server/wm/AppWindowToken:mLaunchTaskBehind	Z
    //   189: aload 24
    //   191: iload 15
    //   193: putfield 2857	com/android/server/wm/AppWindowToken:mAlwaysFocusable	Z
    //   196: getstatic 2860	com/android/server/wm/WindowManagerDebugConfig:DEBUG_TOKEN_MOVEMENT	Z
    //   199: ifne +9 -> 208
    //   202: getstatic 1121	com/android/server/wm/WindowManagerDebugConfig:DEBUG_ADD_REMOVE	Z
    //   205: ifeq +62 -> 267
    //   208: ldc_w 440
    //   211: new 1023	java/lang/StringBuilder
    //   214: dup
    //   215: invokespecial 1024	java/lang/StringBuilder:<init>	()V
    //   218: ldc_w 2862
    //   221: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   224: aload 24
    //   226: invokevirtual 1039	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   229: ldc_w 2864
    //   232: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   235: iload 4
    //   237: invokevirtual 1128	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   240: ldc_w 2866
    //   243: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   246: iload_3
    //   247: invokevirtual 1128	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   250: ldc_w 1125
    //   253: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   256: iload_1
    //   257: invokevirtual 1128	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   260: invokevirtual 1046	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   263: invokestatic 1052	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   266: pop
    //   267: aload 24
    //   269: iload 18
    //   271: putfield 2869	com/android/server/wm/AppWindowToken:mRotationAnimationHint	I
    //   274: aload_0
    //   275: getfield 683	com/android/server/wm/WindowManagerService:mTaskIdToTask	Landroid/util/SparseArray;
    //   278: iload_3
    //   279: invokevirtual 1672	android/util/SparseArray:get	(I)Ljava/lang/Object;
    //   282: checkcast 1066	com/android/server/wm/Task
    //   285: astore 22
    //   287: aload 22
    //   289: astore 21
    //   291: aload 22
    //   293: ifnonnull +20 -> 313
    //   296: aload_0
    //   297: iload_3
    //   298: iload 4
    //   300: iload 8
    //   302: aload 24
    //   304: aload 12
    //   306: aload 13
    //   308: invokespecial 2871	com/android/server/wm/WindowManagerService:createTaskLocked	(IIILcom/android/server/wm/AppWindowToken;Landroid/graphics/Rect;Landroid/content/res/Configuration;)Lcom/android/server/wm/Task;
    //   311: astore 21
    //   313: aload 21
    //   315: iload_1
    //   316: aload 24
    //   318: iload 14
    //   320: iload 16
    //   322: invokevirtual 2874	com/android/server/wm/Task:addAppToken	(ILcom/android/server/wm/AppWindowToken;IZ)V
    //   325: aload_0
    //   326: getfield 493	com/android/server/wm/WindowManagerService:mTokenMap	Ljava/util/HashMap;
    //   329: aload_2
    //   330: invokeinterface 2839 1 0
    //   335: aload 24
    //   337: invokevirtual 2877	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   340: pop
    //   341: aload 24
    //   343: iconst_1
    //   344: putfield 1830	com/android/server/wm/AppWindowToken:hidden	Z
    //   347: aload 24
    //   349: iconst_1
    //   350: putfield 1444	com/android/server/wm/AppWindowToken:hiddenRequested	Z
    //   353: aload 23
    //   355: monitorexit
    //   356: return
    //   357: iconst_0
    //   358: istore 6
    //   360: goto -185 -> 175
    //   363: astore_2
    //   364: aload 23
    //   366: monitorexit
    //   367: aload_2
    //   368: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	369	0	this	WindowManagerService
    //   0	369	1	paramInt1	int
    //   0	369	2	paramIApplicationToken	IApplicationToken
    //   0	369	3	paramInt2	int
    //   0	369	4	paramInt3	int
    //   0	369	5	paramInt4	int
    //   0	369	6	paramBoolean1	boolean
    //   0	369	7	paramBoolean2	boolean
    //   0	369	8	paramInt5	int
    //   0	369	9	paramInt6	int
    //   0	369	10	paramBoolean3	boolean
    //   0	369	11	paramBoolean4	boolean
    //   0	369	12	paramRect	Rect
    //   0	369	13	paramConfiguration	Configuration
    //   0	369	14	paramInt7	int
    //   0	369	15	paramBoolean5	boolean
    //   0	369	16	paramBoolean6	boolean
    //   0	369	17	paramInt8	int
    //   0	369	18	paramInt9	int
    //   30	101	19	l	long
    //   93	9	21	localRemoteException	RemoteException
    //   289	25	21	localObject	Object
    //   285	7	22	localTask	Task
    //   126	222	24	localAppWindowToken	AppWindowToken
    // Exception table:
    //   from	to	target	type
    //   24	32	93	android/os/RemoteException
    //   49	89	363	finally
    //   115	163	363	finally
    //   175	208	363	finally
    //   208	267	363	finally
    //   267	287	363	finally
    //   296	313	363	finally
    //   313	353	363	finally
  }
  
  public WindowManagerPolicy.InputConsumer addInputConsumer(Looper paramLooper, InputEventReceiver.Factory paramFactory)
  {
    synchronized (this.mWindowMap)
    {
      paramLooper = new HideNavInputConsumer(this, paramLooper, paramFactory);
      this.mInputConsumer = paramLooper;
      this.mInputMonitor.updateInputWindowsLw(true);
      return paramLooper;
    }
  }
  
  void addInputMethodWindowToListLocked(WindowState paramWindowState)
  {
    int i = findDesiredInputMethodWindowIndexLocked(true);
    if (i >= 0)
    {
      paramWindowState.mTargetAppToken = this.mInputMethodTarget.mAppToken;
      if ((WindowManagerDebugConfig.DEBUG_WINDOW_MOVEMENT) || (WindowManagerDebugConfig.DEBUG_ADD_REMOVE)) {
        Slog.v("WindowManager", "Adding input method window " + paramWindowState + " at " + i);
      }
      getDefaultWindowListLocked().add(i, paramWindowState);
      this.mWindowsChanged = true;
      moveInputMethodDialogsLocked(i + 1);
      return;
    }
    paramWindowState.mTargetAppToken = null;
    addWindowToListInOrderLocked(paramWindowState, true);
    moveInputMethodDialogsLocked(i);
  }
  
  public void addTask(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    synchronized (this.mWindowMap)
    {
      if (WindowManagerDebugConfig.DEBUG_STACK)
      {
        localObject3 = new StringBuilder().append("addTask: adding taskId=").append(paramInt1).append(" to ");
        if (!paramBoolean) {
          break label121;
        }
      }
      label121:
      for (Object localObject1 = "top";; localObject1 = "bottom")
      {
        Slog.i("WindowManager", (String)localObject1);
        localObject1 = (Task)this.mTaskIdToTask.get(paramInt1);
        if (localObject1 != null) {
          break;
        }
        if (WindowManagerDebugConfig.DEBUG_STACK) {
          Slog.i("WindowManager", "addTask: could not find taskId=" + paramInt1);
        }
        return;
      }
      Object localObject3 = (TaskStack)this.mStackIdToStack.get(paramInt2);
      ((TaskStack)localObject3).addTask((Task)localObject1, paramBoolean);
      ((TaskStack)localObject3).getDisplayContent().layoutNeeded = true;
      this.mWindowPlacerLocked.performSurfacePlacement();
      return;
    }
  }
  
  public int addWindow(Session paramSession, IWindow paramIWindow, int paramInt1, WindowManager.LayoutParams paramLayoutParams, int paramInt2, int paramInt3, Rect paramRect1, Rect paramRect2, Rect paramRect3, InputChannel paramInputChannel)
  {
    int[] arrayOfInt = new int[1];
    int i = this.mPolicy.checkAddPermission(paramLayoutParams, arrayOfInt);
    if (i != 0) {
      return i;
    }
    i = 0;
    Object localObject1 = null;
    int m = Binder.getCallingUid();
    int k = paramLayoutParams.type;
    this.mFocusingActivity = paramLayoutParams.getTitle().toString();
    synchronized (this.mWindowMap)
    {
      if (!this.mDisplayReady) {
        throw new IllegalStateException("Display has not been initialialized");
      }
    }
    DisplayContent localDisplayContent = getDisplayContentLocked(paramInt3);
    if (localDisplayContent == null)
    {
      Slog.w("WindowManager", "Attempted to add window to a display that does not exist: " + paramInt3 + ".  Aborting.");
      return -9;
    }
    if (!localDisplayContent.hasAccess(paramSession.mUid))
    {
      Slog.w("WindowManager", "Attempted to add window to a display for which the application does not have access: " + paramInt3 + ".  Aborting.");
      return -9;
    }
    if (this.mWindowMap.containsKey(paramIWindow.asBinder()))
    {
      Slog.w("WindowManager", "Window " + paramIWindow + " is already added");
      return -5;
    }
    Object localObject2 = localObject1;
    if (k >= 1000)
    {
      localObject2 = localObject1;
      if (k <= 1999)
      {
        localObject1 = windowForClientLocked(null, paramLayoutParams.token, false);
        if (localObject1 == null)
        {
          Slog.w("WindowManager", "Attempted to add window with token that is not a window: " + paramLayoutParams.token + ".  Aborting.");
          return -2;
        }
        localObject2 = localObject1;
        if (((WindowState)localObject1).mAttrs.type >= 1000)
        {
          localObject2 = localObject1;
          if (((WindowState)localObject1).mAttrs.type <= 1999)
          {
            Slog.w("WindowManager", "Attempted to add window with token that is a sub-window: " + paramLayoutParams.token + ".  Aborting.");
            return -2;
          }
        }
      }
    }
    int j;
    WindowToken localWindowToken;
    AppWindowToken localAppWindowToken2;
    boolean bool2;
    if ((k != 2030) || (localDisplayContent.isPrivate()))
    {
      j = 0;
      localWindowToken = (WindowToken)this.mTokenMap.get(paramLayoutParams.token);
      localAppWindowToken2 = null;
      bool2 = false;
      if (localWindowToken != null) {
        break label965;
      }
      if ((k >= 1) && (k <= 99))
      {
        Slog.w("WindowManager", "Attempted to add application window with unknown token " + paramLayoutParams.token + ".  Aborting.");
        return -1;
      }
    }
    else
    {
      Slog.w("WindowManager", "Attempted to add private presentation window to a non-private display.  Aborting.");
      return -8;
    }
    if (k == 2011)
    {
      Slog.w("WindowManager", "Attempted to add input method window with unknown token " + paramLayoutParams.token + ".  Aborting.");
      return -1;
    }
    if (k == 2031)
    {
      Slog.w("WindowManager", "Attempted to add voice interaction window with unknown token " + paramLayoutParams.token + ".  Aborting.");
      return -1;
    }
    if (k == 2013)
    {
      Slog.w("WindowManager", "Attempted to add wallpaper window with unknown token " + paramLayoutParams.token + ".  Aborting.");
      return -1;
    }
    if (k == 2023)
    {
      Slog.w("WindowManager", "Attempted to add Dream window with unknown token " + paramLayoutParams.token + ".  Aborting.");
      return -1;
    }
    if (k == 2035)
    {
      Slog.w("WindowManager", "Attempted to add QS dialog window with unknown token " + paramLayoutParams.token + ".  Aborting.");
      return -1;
    }
    if (k == 2032)
    {
      Slog.w("WindowManager", "Attempted to add Accessibility overlay window with unknown token " + paramLayoutParams.token + ".  Aborting.");
      return -1;
    }
    if ((k == 2005) && (doesAddToastWindowRequireToken(paramLayoutParams.packageName, m, (WindowState)localObject2)))
    {
      Slog.w("WindowManager", "Attempted to add a toast window with unknown token " + paramLayoutParams.token + ".  Aborting.");
      return -1;
    }
    localObject1 = new WindowToken(this, paramLayoutParams.token, -1, false);
    paramInt3 = 1;
    AppWindowToken localAppWindowToken1 = localAppWindowToken2;
    boolean bool1 = bool2;
    while ((k == 2005) && (!getDefaultDisplayContentLocked().canAddToastWindowForUid(m)))
    {
      Slog.w("WindowManager", "Adding more than one toast window for UID at a time (0).");
      return -5;
      label965:
      if ((k >= 1) && (k <= 99))
      {
        localAppWindowToken2 = localWindowToken.appWindowToken;
        if (localAppWindowToken2 == null)
        {
          Slog.w("WindowManager", "Attempted to add window with non-application token " + localWindowToken + ".  Aborting.");
          return -3;
        }
        if (localAppWindowToken2.removed)
        {
          Slog.w("WindowManager", "Attempted to add window with exiting application token " + localWindowToken + ".  Aborting.");
          return -4;
        }
        localObject1 = localWindowToken;
        bool1 = bool2;
        paramInt3 = j;
        localAppWindowToken1 = localAppWindowToken2;
        if (k == 3)
        {
          localObject1 = localWindowToken;
          bool1 = bool2;
          paramInt3 = j;
          localAppWindowToken1 = localAppWindowToken2;
          if (localAppWindowToken2.firstWindowDrawn)
          {
            if ((WindowManagerDebugConfig.DEBUG_STARTING_WINDOW) || (localLOGV)) {
              Slog.v("WindowManager", "**** NO NEED TO START: " + paramLayoutParams.getTitle());
            }
            return -6;
          }
        }
      }
      else if (k == 2011)
      {
        localObject1 = localWindowToken;
        bool1 = bool2;
        paramInt3 = j;
        localAppWindowToken1 = localAppWindowToken2;
        if (localWindowToken.windowType != 2011)
        {
          Slog.w("WindowManager", "Attempted to add input method window with bad token " + paramLayoutParams.token + ".  Aborting.");
          return -1;
        }
      }
      else if (k == 2031)
      {
        localObject1 = localWindowToken;
        bool1 = bool2;
        paramInt3 = j;
        localAppWindowToken1 = localAppWindowToken2;
        if (localWindowToken.windowType != 2031)
        {
          Slog.w("WindowManager", "Attempted to add voice interaction window with bad token " + paramLayoutParams.token + ".  Aborting.");
          return -1;
        }
      }
      else if (k == 2013)
      {
        localObject1 = localWindowToken;
        bool1 = bool2;
        paramInt3 = j;
        localAppWindowToken1 = localAppWindowToken2;
        if (localWindowToken.windowType != 2013)
        {
          Slog.w("WindowManager", "Attempted to add wallpaper window with bad token " + paramLayoutParams.token + ".  Aborting.");
          return -1;
        }
      }
      else if (k == 2023)
      {
        localObject1 = localWindowToken;
        bool1 = bool2;
        paramInt3 = j;
        localAppWindowToken1 = localAppWindowToken2;
        if (localWindowToken.windowType != 2023)
        {
          Slog.w("WindowManager", "Attempted to add Dream window with bad token " + paramLayoutParams.token + ".  Aborting.");
          return -1;
        }
      }
      else if (k == 2032)
      {
        localObject1 = localWindowToken;
        bool1 = bool2;
        paramInt3 = j;
        localAppWindowToken1 = localAppWindowToken2;
        if (localWindowToken.windowType != 2032)
        {
          Slog.w("WindowManager", "Attempted to add Accessibility overlay window with bad token " + paramLayoutParams.token + ".  Aborting.");
          return -1;
        }
      }
      else if (k == 2005)
      {
        bool2 = doesAddToastWindowRequireToken(paramLayoutParams.packageName, m, (WindowState)localObject2);
        localObject1 = localWindowToken;
        bool1 = bool2;
        paramInt3 = j;
        localAppWindowToken1 = localAppWindowToken2;
        if (bool2)
        {
          localObject1 = localWindowToken;
          bool1 = bool2;
          paramInt3 = j;
          localAppWindowToken1 = localAppWindowToken2;
          if (localWindowToken.windowType != 2005)
          {
            Slog.w("WindowManager", "Attempted to add a toast window with bad token " + paramLayoutParams.token + ".  Aborting.");
            return -1;
          }
        }
      }
      else if (k == 2035)
      {
        localObject1 = localWindowToken;
        bool1 = bool2;
        paramInt3 = j;
        localAppWindowToken1 = localAppWindowToken2;
        if (localWindowToken.windowType != 2035)
        {
          Slog.w("WindowManager", "Attempted to add QS dialog window with bad token " + paramLayoutParams.token + ".  Aborting.");
          return -1;
        }
      }
      else
      {
        localObject1 = localWindowToken;
        bool1 = bool2;
        paramInt3 = j;
        localAppWindowToken1 = localAppWindowToken2;
        if (localWindowToken.appWindowToken != null)
        {
          Slog.w("WindowManager", "Non-null appWindowToken for system window of type=" + k);
          paramLayoutParams.token = null;
          localObject1 = new WindowToken(this, null, -1, false);
          paramInt3 = 1;
          bool1 = bool2;
          localAppWindowToken1 = localAppWindowToken2;
        }
      }
    }
    localObject2 = new WindowState(this, paramSession, paramIWindow, (WindowToken)localObject1, (WindowState)localObject2, arrayOfInt[0], paramInt1, paramLayoutParams, paramInt2, localDisplayContent);
    if (((WindowState)localObject2).mDeathRecipient == null)
    {
      Slog.w("WindowManager", "Adding window client " + paramIWindow.asBinder() + " that is dead, aborting.");
      return -4;
    }
    if (((WindowState)localObject2).getDisplayContent() == null)
    {
      Slog.w("WindowManager", "Adding window to Display that has been removed.");
      return -9;
    }
    this.mPolicy.adjustWindowParamsLw(((WindowState)localObject2).mAttrs);
    ((WindowState)localObject2).setShowToOwnerOnlyLocked(this.mPolicy.checkShowToOwnerOnly(paramLayoutParams));
    paramInt1 = this.mPolicy.prepareAddWindowLw((WindowManagerPolicy.WindowState)localObject2, paramLayoutParams);
    if (paramInt1 != 0) {
      return paramInt1;
    }
    if (paramInputChannel != null) {
      if ((paramLayoutParams.inputFeatures & 0x2) == 0) {
        paramInt1 = 1;
      }
    }
    for (;;)
    {
      if (paramInt1 != 0) {
        ((WindowState)localObject2).openInputChannel(paramInputChannel);
      }
      if (k != 2005) {
        break label2119;
      }
      if (getDefaultDisplayContentLocked().canAddToastWindowForUid(m)) {
        break;
      }
      Slog.w("WindowManager", "Adding more than one toast window for UID at a time.");
      return -5;
      paramInt1 = 0;
      continue;
      paramInt1 = 0;
    }
    label2119:
    long l;
    if (!OpFeatures.isSupport(new int[] { 0 }))
    {
      if ((bool1) || ((paramLayoutParams.flags & 0x8) == 0)) {
        this.mH.sendMessageDelayed(this.mH.obtainMessage(52, localObject2), ((WindowState)localObject2).mAttrs.hideTimeoutMilliseconds);
      }
    }
    else
    {
      paramInt2 = 0;
      if (excludeWindowTypeFromTapOutTask(k)) {
        localDisplayContent.mTapExcludedWindows.add(localObject2);
      }
      l = Binder.clearCallingIdentity();
      if (paramInt3 != 0) {
        this.mTokenMap.put(paramLayoutParams.token, localObject1);
      }
      ((WindowState)localObject2).attach();
      this.mWindowMap.put(paramIWindow.asBinder(), localObject2);
      if (((WindowState)localObject2).mAppOp != -1)
      {
        paramInt1 = this.mAppOps.startOpNoThrow(((WindowState)localObject2).mAppOp, ((WindowState)localObject2).getOwningUid(), ((WindowState)localObject2).getOwningPackage());
        if ((paramInt1 != 0) && (paramInt1 != 3)) {
          ((WindowState)localObject2).setAppOpVisibilityLw(false);
        }
      }
      if ((k == 3) && (((WindowToken)localObject1).appWindowToken != null))
      {
        ((WindowToken)localObject1).appWindowToken.startingWindow = ((WindowState)localObject2);
        if (WindowManagerDebugConfig.DEBUG_STARTING_WINDOW) {
          Slog.v("WindowManager", "addWindow: " + ((WindowToken)localObject1).appWindowToken + " startingWindow=" + localObject2);
        }
      }
      paramInt3 = 1;
      if (k != 2011) {
        break label2752;
      }
      ((WindowState)localObject2).mGivenInsetsPending = true;
      this.mInputMethodWindow = ((WindowState)localObject2);
      addInputMethodWindowToListLocked((WindowState)localObject2);
      paramInt1 = 0;
      label2337:
      ((WindowState)localObject2).applyScrollIfNeeded();
      ((WindowState)localObject2).applyAdjustForImeIfNeeded();
      if (k == 2034) {
        getDefaultDisplayContentLocked().getDockedDividerController().setWindow((WindowState)localObject2);
      }
      paramSession = ((WindowState)localObject2).mWinAnimator;
      paramSession.mEnterAnimationPending = true;
      paramSession.mEnteringAnimation = true;
      if ((localAppWindowToken1 != null) && (!prepareWindowReplacementTransition(localAppWindowToken1))) {
        break label2894;
      }
      label2397:
      if (!localDisplayContent.isDefaultDisplay) {
        break label2903;
      }
      paramLayoutParams = localDisplayContent.getDisplayInfo();
      if ((localAppWindowToken1 == null) || (localAppWindowToken1.mTask == null)) {
        break label2925;
      }
      paramSession = this.mTmpRect;
      localAppWindowToken1.mTask.getBounds(this.mTmpRect);
    }
    for (;;)
    {
      if (this.mPolicy.getInsetHintLw(((WindowState)localObject2).mAttrs, paramSession, this.mRotation, paramLayoutParams.logicalWidth, paramLayoutParams.logicalHeight, paramRect1, paramRect2, paramRect3)) {
        paramInt2 = 4;
      }
      label2483:
      paramInt3 = paramInt2;
      if (this.mInTouchMode) {
        paramInt3 = paramInt2 | 0x1;
      }
      if ((((WindowState)localObject2).mAppToken != null) && (((WindowState)localObject2).mAppToken.clientHidden)) {}
      for (;;)
      {
        this.mInputMonitor.setUpdateInputWindowsNeededLw();
        bool1 = false;
        paramInt2 = paramInt1;
        if (((WindowState)localObject2).canReceiveKeys())
        {
          bool2 = updateFocusedWindowLocked(1, false);
          bool1 = bool2;
          paramInt2 = paramInt1;
          if (bool2)
          {
            paramInt2 = 0;
            bool1 = bool2;
          }
        }
        if (paramInt2 != 0) {
          moveInputMethodWindowsIfNeededLocked(false);
        }
        this.mLayersController.assignLayersLocked(localDisplayContent.getWindowList());
        if (bool1) {
          this.mInputMonitor.setInputFocusLw(this.mCurrentFocus, false);
        }
        this.mInputMonitor.updateInputWindowsLw(false);
        if ((localLOGV) || (WindowManagerDebugConfig.DEBUG_ADD_REMOVE)) {
          Slog.v("WindowManager", "addWindow: New client " + paramIWindow.asBinder() + ": window=" + localObject2 + " Callers=" + Debug.getCallers(5));
        }
        paramInt1 = i;
        if (((WindowState)localObject2).isVisibleOrAdding())
        {
          bool1 = updateOrientationFromAppTokensLocked(false);
          paramInt1 = i;
          if (bool1) {
            paramInt1 = 1;
          }
        }
        if (paramInt1 != 0) {
          sendNewConfiguration();
        }
        Binder.restoreCallingIdentity(l);
        return paramInt3;
        if (this.mCurrentFocus == null) {
          break;
        }
        if (this.mCurrentFocus.mOwnerUid == m) {
          break label2119;
        }
        break;
        label2752:
        if (k == 2012)
        {
          this.mInputMethodDialogs.add(localObject2);
          addWindowToListInOrderLocked((WindowState)localObject2, true);
          moveInputMethodDialogsLocked(findDesiredInputMethodWindowIndexLocked(true));
          paramInt1 = 0;
          break label2337;
        }
        addWindowToListInOrderLocked((WindowState)localObject2, true);
        if (k == 2013)
        {
          this.mWallpaperControllerLocked.clearLastWallpaperTimeoutTime();
          localDisplayContent.pendingLayoutChanges |= 0x4;
          paramInt1 = paramInt3;
          break label2337;
        }
        if ((paramLayoutParams.flags & 0x100000) != 0)
        {
          localDisplayContent.pendingLayoutChanges |= 0x4;
          paramInt1 = paramInt3;
          break label2337;
        }
        paramInt1 = paramInt3;
        if (!this.mWallpaperControllerLocked.isBelowWallpaperTarget((WindowState)localObject2)) {
          break label2337;
        }
        localDisplayContent.pendingLayoutChanges |= 0x4;
        paramInt1 = paramInt3;
        break label2337;
        label2894:
        prepareNoneTransitionForRelaunching(localAppWindowToken1);
        break label2397;
        label2903:
        paramRect1.setEmpty();
        paramRect2.setEmpty();
        break label2483;
        paramInt3 |= 0x2;
      }
      label2925:
      paramSession = null;
    }
  }
  
  public void addWindowChangeListener(WindowChangeListener paramWindowChangeListener)
  {
    synchronized (this.mWindowMap)
    {
      this.mWindowChangeListeners.add(paramWindowChangeListener);
      return;
    }
  }
  
  public void addWindowToken(IBinder paramIBinder, int paramInt)
  {
    if (!checkCallingPermission("android.permission.MANAGE_APP_TOKENS", "addWindowToken()")) {
      throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
    }
    synchronized (this.mWindowMap)
    {
      if ((WindowToken)this.mTokenMap.get(paramIBinder) != null)
      {
        Slog.w("WindowManager", "Attempted to add existing input method token: " + paramIBinder);
        return;
      }
      WindowToken localWindowToken = new WindowToken(this, paramIBinder, paramInt, true);
      this.mTokenMap.put(paramIBinder, localWindowToken);
      if (paramInt == 2013) {
        this.mWallpaperControllerLocked.addWallpaperToken(localWindowToken);
      }
      return;
    }
  }
  
  int adjustAnimationBackground(WindowStateAnimator paramWindowStateAnimator)
  {
    WindowList localWindowList = paramWindowStateAnimator.mWin.getWindowList();
    int i = localWindowList.size() - 1;
    while (i >= 0)
    {
      WindowState localWindowState = (WindowState)localWindowList.get(i);
      if ((localWindowState.mIsWallpaper) && (localWindowState.isVisibleNow())) {
        return localWindowState.mWinAnimator.mAnimLayer;
      }
      i -= 1;
    }
    return paramWindowStateAnimator.mAnimLayer;
  }
  
  void adjustForImeIfNeeded(DisplayContent paramDisplayContent)
  {
    WindowState localWindowState = this.mInputMethodWindow;
    int i;
    boolean bool3;
    Object localObject;
    int j;
    label68:
    int k;
    label76:
    boolean bool2;
    label84:
    int m;
    label123:
    label161:
    TaskStack localTaskStack;
    if ((localWindowState != null) && (localWindowState.isVisibleLw()) && (localWindowState.isDisplayedLw())) {
      if (paramDisplayContent.mDividerControllerLocked.isImeHideRequested())
      {
        i = 0;
        bool3 = isStackVisibleLocked(3);
        localObject = getImeFocusStackLocked();
        if ((!bool3) || (localObject == null)) {
          break label208;
        }
        j = ((TaskStack)localObject).getDockSide();
        if (j != 2) {
          break label213;
        }
        k = 1;
        if (j != 4) {
          break label219;
        }
        bool2 = true;
        boolean bool4 = paramDisplayContent.mDividerControllerLocked.isMinimizedDock();
        m = this.mPolicy.getInputMethodWindowVisibleHeightLw();
        if (i == 0) {
          break label231;
        }
        if (m == paramDisplayContent.mDividerControllerLocked.getImeHeightAdjustedFor()) {
          break label225;
        }
        bool1 = true;
        if ((i != 0) && (bool3) && ((k != 0) || (bool2)) && (!bool4)) {
          break label237;
        }
        localObject = paramDisplayContent.getStacks();
        i = ((ArrayList)localObject).size() - 1;
        if (i < 0) {
          break label361;
        }
        localTaskStack = (TaskStack)((ArrayList)localObject).get(i);
        if (!bool3) {
          break label355;
        }
      }
    }
    label208:
    label213:
    label219:
    label225:
    label231:
    label237:
    label277:
    label303:
    label324:
    label330:
    label355:
    for (boolean bool1 = false;; bool1 = true)
    {
      localTaskStack.resetAdjustedForIme(bool1);
      i -= 1;
      break label161;
      i = 1;
      break;
      i = 0;
      break;
      j = -1;
      break label68;
      k = 0;
      break label76;
      bool2 = false;
      break label84;
      bool1 = false;
      break label123;
      bool1 = false;
      break label123;
      localObject = paramDisplayContent.getStacks();
      i = ((ArrayList)localObject).size() - 1;
      if (i >= 0)
      {
        localTaskStack = (TaskStack)((ArrayList)localObject).get(i);
        if (localTaskStack.getDockSide() == 4)
        {
          j = 1;
          if ((!localTaskStack.isVisibleLocked()) || ((!bool2) && (j == 0))) {
            break label330;
          }
          if (!bool2) {
            break label324;
          }
          bool3 = bool1;
          localTaskStack.setAdjustedForIme(localWindowState, bool3);
        }
        for (;;)
        {
          i -= 1;
          break;
          j = 0;
          break label277;
          bool3 = false;
          break label303;
          localTaskStack.resetAdjustedForIme(false);
        }
      }
      paramDisplayContent.mDividerControllerLocked.setAdjustedForIme(bool2, true, true, localWindowState, m);
      return;
    }
    label361:
    paramDisplayContent.mDividerControllerLocked.setAdjustedForIme(false, false, bool3, localWindowState, m);
  }
  
  public void animateResizePinnedStack(final Rect paramRect, final int paramInt)
  {
    synchronized (this.mWindowMap)
    {
      final TaskStack localTaskStack = (TaskStack)this.mStackIdToStack.get(4);
      if (localTaskStack == null)
      {
        Slog.w(TAG, "animateResizePinnedStack: stackId 4 not found.");
        return;
      }
      final Rect localRect = new Rect();
      localTaskStack.getBounds(localRect);
      UiThread.getHandler().post(new Runnable()
      {
        public void run()
        {
          WindowManagerService.-get1(WindowManagerService.this).animateBounds(localTaskStack, localRect, paramRect, paramInt);
        }
      });
      return;
    }
  }
  
  /* Error */
  public Rect attachStack(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    // Byte code:
    //   0: invokestatic 2686	android/os/Binder:clearCallingIdentity	()J
    //   3: lstore 5
    //   5: aload_0
    //   6: getfield 491	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
    //   9: astore 9
    //   11: aload 9
    //   13: monitorenter
    //   14: aload_0
    //   15: getfield 566	com/android/server/wm/WindowManagerService:mDisplayContents	Landroid/util/SparseArray;
    //   18: iload_2
    //   19: invokevirtual 1672	android/util/SparseArray:get	(I)Ljava/lang/Object;
    //   22: checkcast 1004	com/android/server/wm/DisplayContent
    //   25: astore 10
    //   27: iconst_0
    //   28: istore 4
    //   30: iconst_0
    //   31: istore_2
    //   32: aload 10
    //   34: ifnull +207 -> 241
    //   37: aload_0
    //   38: getfield 685	com/android/server/wm/WindowManagerService:mStackIdToStack	Landroid/util/SparseArray;
    //   41: iload_1
    //   42: invokevirtual 1672	android/util/SparseArray:get	(I)Ljava/lang/Object;
    //   45: checkcast 1674	com/android/server/wm/TaskStack
    //   48: astore 7
    //   50: aload 7
    //   52: astore 8
    //   54: aload 7
    //   56: ifnonnull +104 -> 160
    //   59: getstatic 1663	com/android/server/wm/WindowManagerDebugConfig:DEBUG_STACK	Z
    //   62: ifeq +30 -> 92
    //   65: ldc_w 440
    //   68: new 1023	java/lang/StringBuilder
    //   71: dup
    //   72: invokespecial 1024	java/lang/StringBuilder:<init>	()V
    //   75: ldc_w 3239
    //   78: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   81: iload_1
    //   82: invokevirtual 1128	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   85: invokevirtual 1046	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   88: invokestatic 1184	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   91: pop
    //   92: aload 10
    //   94: iload_1
    //   95: invokevirtual 3243	com/android/server/wm/DisplayContent:getStackById	(I)Lcom/android/server/wm/TaskStack;
    //   98: astore 7
    //   100: aload 7
    //   102: ifnull +98 -> 200
    //   105: aload 10
    //   107: aload 7
    //   109: invokevirtual 3247	com/android/server/wm/DisplayContent:detachStack	(Lcom/android/server/wm/TaskStack;)V
    //   112: aload 7
    //   114: iconst_0
    //   115: putfield 3250	com/android/server/wm/TaskStack:mDeferDetach	Z
    //   118: iconst_1
    //   119: istore_2
    //   120: aload_0
    //   121: getfield 685	com/android/server/wm/WindowManagerService:mStackIdToStack	Landroid/util/SparseArray;
    //   124: iload_1
    //   125: aload 7
    //   127: invokevirtual 1700	android/util/SparseArray:put	(ILjava/lang/Object;)V
    //   130: iload_2
    //   131: istore 4
    //   133: aload 7
    //   135: astore 8
    //   137: iload_1
    //   138: iconst_3
    //   139: if_icmpne +21 -> 160
    //   142: aload_0
    //   143: invokevirtual 1810	com/android/server/wm/WindowManagerService:getDefaultDisplayContentLocked	()Lcom/android/server/wm/DisplayContent;
    //   146: getfield 1817	com/android/server/wm/DisplayContent:mDividerControllerLocked	Lcom/android/server/wm/DockedStackDividerController;
    //   149: iconst_1
    //   150: invokevirtual 3253	com/android/server/wm/DockedStackDividerController:notifyDockedStackExistsChanged	(Z)V
    //   153: aload 7
    //   155: astore 8
    //   157: iload_2
    //   158: istore 4
    //   160: iload 4
    //   162: ifne +10 -> 172
    //   165: aload 8
    //   167: aload 10
    //   169: invokevirtual 3256	com/android/server/wm/TaskStack:attachDisplayContent	(Lcom/android/server/wm/DisplayContent;)V
    //   172: aload 10
    //   174: aload 8
    //   176: iload_3
    //   177: invokevirtual 3259	com/android/server/wm/DisplayContent:attachStack	(Lcom/android/server/wm/TaskStack;Z)V
    //   180: aload 8
    //   182: invokevirtual 3262	com/android/server/wm/TaskStack:getRawFullscreen	()Z
    //   185: istore_3
    //   186: iload_3
    //   187: ifeq +27 -> 214
    //   190: aload 9
    //   192: monitorexit
    //   193: lload 5
    //   195: invokestatic 2729	android/os/Binder:restoreCallingIdentity	(J)V
    //   198: aconst_null
    //   199: areturn
    //   200: new 1674	com/android/server/wm/TaskStack
    //   203: dup
    //   204: aload_0
    //   205: iload_1
    //   206: invokespecial 3264	com/android/server/wm/TaskStack:<init>	(Lcom/android/server/wm/WindowManagerService;I)V
    //   209: astore 7
    //   211: goto -91 -> 120
    //   214: new 531	android/graphics/Rect
    //   217: dup
    //   218: invokespecial 532	android/graphics/Rect:<init>	()V
    //   221: astore 7
    //   223: aload 8
    //   225: aload 7
    //   227: invokevirtual 3267	com/android/server/wm/TaskStack:getRawBounds	(Landroid/graphics/Rect;)V
    //   230: aload 9
    //   232: monitorexit
    //   233: lload 5
    //   235: invokestatic 2729	android/os/Binder:restoreCallingIdentity	(J)V
    //   238: aload 7
    //   240: areturn
    //   241: aload 9
    //   243: monitorexit
    //   244: lload 5
    //   246: invokestatic 2729	android/os/Binder:restoreCallingIdentity	(J)V
    //   249: aconst_null
    //   250: areturn
    //   251: astore 7
    //   253: aload 9
    //   255: monitorexit
    //   256: aload 7
    //   258: athrow
    //   259: astore 7
    //   261: lload 5
    //   263: invokestatic 2729	android/os/Binder:restoreCallingIdentity	(J)V
    //   266: aload 7
    //   268: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	269	0	this	WindowManagerService
    //   0	269	1	paramInt1	int
    //   0	269	2	paramInt2	int
    //   0	269	3	paramBoolean	boolean
    //   28	133	4	i	int
    //   3	259	5	l	long
    //   48	191	7	localObject1	Object
    //   251	6	7	localObject2	Object
    //   259	8	7	localObject3	Object
    //   52	172	8	localObject4	Object
    //   25	148	10	localDisplayContent	DisplayContent
    // Exception table:
    //   from	to	target	type
    //   14	27	251	finally
    //   37	50	251	finally
    //   59	92	251	finally
    //   92	100	251	finally
    //   105	118	251	finally
    //   120	130	251	finally
    //   142	153	251	finally
    //   165	172	251	finally
    //   172	186	251	finally
    //   200	211	251	finally
    //   214	230	251	finally
    //   5	14	259	finally
    //   190	193	259	finally
    //   230	233	259	finally
    //   241	244	259	finally
    //   253	259	259	finally
  }
  
  public void cancelTaskThumbnailTransition(int paramInt)
  {
    synchronized (this.mWindowMap)
    {
      Task localTask = (Task)this.mTaskIdToTask.get(paramInt);
      if (localTask != null) {
        localTask.cancelTaskThumbnailTransition();
      }
      return;
    }
  }
  
  public void cancelTaskWindowTransition(int paramInt)
  {
    synchronized (this.mWindowMap)
    {
      Task localTask = (Task)this.mTaskIdToTask.get(paramInt);
      if (localTask != null) {
        localTask.cancelTaskWindowTransition();
      }
      return;
    }
  }
  
  void checkDrawnWindowsLocked()
  {
    if ((this.mWaitingForDrawn.isEmpty()) || (this.mWaitingForDrawnCallback == null)) {
      return;
    }
    int i = this.mWaitingForDrawn.size() - 1;
    if (i >= 0)
    {
      WindowState localWindowState = (WindowState)this.mWaitingForDrawn.get(i);
      if (WindowManagerDebugConfig.DEBUG_SCREEN_ON) {
        Slog.i("WindowManager", "Waiting for drawn " + localWindowState + ": removed=" + localWindowState.mRemoved + " visible=" + localWindowState.isVisibleLw() + " mHasSurface=" + localWindowState.mHasSurface + " drawState=" + localWindowState.mWinAnimator.mDrawState);
      }
      if ((!localWindowState.mRemoved) && (localWindowState.mHasSurface) && (localWindowState.mPolicyVisibility)) {
        if (localWindowState.hasDrawnLw())
        {
          if ((WindowManagerDebugConfig.DEBUG_SCREEN_ON) || (WindowManagerDebugConfig.DEBUG_ONEPLUS)) {
            Slog.d(TAG, "Window drawn win=" + localWindowState);
          }
          this.mWaitingForDrawn.remove(localWindowState);
        }
      }
      for (;;)
      {
        i -= 1;
        break;
        if ((WindowManagerDebugConfig.DEBUG_SCREEN_ON) || (WindowManagerDebugConfig.DEBUG_ONEPLUS)) {
          Slog.w(TAG, "Aborted waiting for drawn: " + localWindowState);
        }
        this.mWaitingForDrawn.remove(localWindowState);
      }
    }
    if (this.mWaitingForDrawn.isEmpty())
    {
      if (WindowManagerDebugConfig.DEBUG_SCREEN_ON) {
        Slog.d("WindowManager", "All windows drawn!");
      }
      this.mH.removeMessages(24);
      this.mH.sendEmptyMessage(33);
    }
  }
  
  /* Error */
  public void clearForcedDisplayDensityForUser(int paramInt1, int paramInt2)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 713	com/android/server/wm/WindowManagerService:mContext	Landroid/content/Context;
    //   4: ldc_w 3307
    //   7: invokevirtual 3310	android/content/Context:checkCallingOrSelfPermission	(Ljava/lang/String;)I
    //   10: ifeq +14 -> 24
    //   13: new 1764	java/lang/SecurityException
    //   16: dup
    //   17: ldc_w 3312
    //   20: invokespecial 1769	java/lang/SecurityException:<init>	(Ljava/lang/String;)V
    //   23: athrow
    //   24: iload_1
    //   25: ifeq +14 -> 39
    //   28: new 1676	java/lang/IllegalArgumentException
    //   31: dup
    //   32: ldc_w 3314
    //   35: invokespecial 1680	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   38: athrow
    //   39: invokestatic 1473	android/os/Binder:getCallingPid	()I
    //   42: invokestatic 1490	android/os/Binder:getCallingUid	()I
    //   45: iload_2
    //   46: iconst_0
    //   47: iconst_1
    //   48: ldc_w 3315
    //   51: aconst_null
    //   52: invokestatic 3321	android/app/ActivityManager:handleIncomingUser	(IIIZZLjava/lang/String;Ljava/lang/String;)I
    //   55: istore_2
    //   56: invokestatic 2686	android/os/Binder:clearCallingIdentity	()J
    //   59: lstore_3
    //   60: aload_0
    //   61: getfield 491	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
    //   64: astore 5
    //   66: aload 5
    //   68: monitorenter
    //   69: aload_0
    //   70: iload_1
    //   71: invokevirtual 1720	com/android/server/wm/WindowManagerService:getDisplayContentLocked	(I)Lcom/android/server/wm/DisplayContent;
    //   74: astore 6
    //   76: aload 6
    //   78: ifnull +22 -> 100
    //   81: aload_0
    //   82: getfield 2172	com/android/server/wm/WindowManagerService:mCurrentUserId	I
    //   85: iload_2
    //   86: if_icmpne +14 -> 100
    //   89: aload_0
    //   90: aload 6
    //   92: aload 6
    //   94: getfield 3324	com/android/server/wm/DisplayContent:mInitialDisplayDensity	I
    //   97: invokespecial 3326	com/android/server/wm/WindowManagerService:setForcedDisplayDensityLocked	(Lcom/android/server/wm/DisplayContent;I)V
    //   100: aload_0
    //   101: getfield 713	com/android/server/wm/WindowManagerService:mContext	Landroid/content/Context;
    //   104: invokevirtual 911	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   107: ldc_w 1863
    //   110: ldc_w 2224
    //   113: iload_2
    //   114: invokestatic 3330	android/provider/Settings$Secure:putStringForUser	(Landroid/content/ContentResolver;Ljava/lang/String;Ljava/lang/String;I)Z
    //   117: pop
    //   118: aload 5
    //   120: monitorexit
    //   121: lload_3
    //   122: invokestatic 2729	android/os/Binder:restoreCallingIdentity	(J)V
    //   125: return
    //   126: astore 6
    //   128: aload 5
    //   130: monitorexit
    //   131: aload 6
    //   133: athrow
    //   134: astore 5
    //   136: lload_3
    //   137: invokestatic 2729	android/os/Binder:restoreCallingIdentity	(J)V
    //   140: aload 5
    //   142: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	143	0	this	WindowManagerService
    //   0	143	1	paramInt1	int
    //   0	143	2	paramInt2	int
    //   59	78	3	l	long
    //   134	7	5	localObject1	Object
    //   74	19	6	localDisplayContent	DisplayContent
    //   126	6	6	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   69	76	126	finally
    //   81	100	126	finally
    //   100	118	126	finally
    //   60	69	134	finally
    //   118	121	134	finally
    //   128	134	134	finally
  }
  
  /* Error */
  public void clearForcedDisplaySize(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 713	com/android/server/wm/WindowManagerService:mContext	Landroid/content/Context;
    //   4: ldc_w 3307
    //   7: invokevirtual 3310	android/content/Context:checkCallingOrSelfPermission	(Ljava/lang/String;)I
    //   10: ifeq +14 -> 24
    //   13: new 1764	java/lang/SecurityException
    //   16: dup
    //   17: ldc_w 3312
    //   20: invokespecial 1769	java/lang/SecurityException:<init>	(Ljava/lang/String;)V
    //   23: athrow
    //   24: iload_1
    //   25: ifeq +14 -> 39
    //   28: new 1676	java/lang/IllegalArgumentException
    //   31: dup
    //   32: ldc_w 3314
    //   35: invokespecial 1680	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   38: athrow
    //   39: invokestatic 2686	android/os/Binder:clearCallingIdentity	()J
    //   42: lstore_2
    //   43: aload_0
    //   44: getfield 491	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
    //   47: astore 4
    //   49: aload 4
    //   51: monitorenter
    //   52: aload_0
    //   53: iload_1
    //   54: invokevirtual 1720	com/android/server/wm/WindowManagerService:getDisplayContentLocked	(I)Lcom/android/server/wm/DisplayContent;
    //   57: astore 5
    //   59: aload 5
    //   61: ifnull +36 -> 97
    //   64: aload_0
    //   65: aload 5
    //   67: aload 5
    //   69: getfield 3334	com/android/server/wm/DisplayContent:mInitialDisplayWidth	I
    //   72: aload 5
    //   74: getfield 3337	com/android/server/wm/DisplayContent:mInitialDisplayHeight	I
    //   77: invokespecial 3339	com/android/server/wm/WindowManagerService:setForcedDisplaySizeLocked	(Lcom/android/server/wm/DisplayContent;II)V
    //   80: aload_0
    //   81: getfield 713	com/android/server/wm/WindowManagerService:mContext	Landroid/content/Context;
    //   84: invokevirtual 911	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   87: ldc_w 2151
    //   90: ldc_w 2224
    //   93: invokestatic 3343	android/provider/Settings$Global:putString	(Landroid/content/ContentResolver;Ljava/lang/String;Ljava/lang/String;)Z
    //   96: pop
    //   97: aload 4
    //   99: monitorexit
    //   100: lload_2
    //   101: invokestatic 2729	android/os/Binder:restoreCallingIdentity	(J)V
    //   104: return
    //   105: astore 5
    //   107: aload 4
    //   109: monitorexit
    //   110: aload 5
    //   112: athrow
    //   113: astore 4
    //   115: lload_2
    //   116: invokestatic 2729	android/os/Binder:restoreCallingIdentity	(J)V
    //   119: aload 4
    //   121: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	122	0	this	WindowManagerService
    //   0	122	1	paramInt	int
    //   42	74	2	l	long
    //   113	7	4	localObject1	Object
    //   57	16	5	localDisplayContent	DisplayContent
    //   105	6	5	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   52	59	105	finally
    //   64	97	105	finally
    //   43	52	113	finally
    //   97	100	113	finally
    //   107	113	113	finally
  }
  
  public boolean clearWindowContentFrameStats(IBinder paramIBinder)
  {
    if (!checkCallingPermission("android.permission.FRAME_STATS", "clearWindowContentFrameStats()")) {
      throw new SecurityException("Requires FRAME_STATS permission");
    }
    synchronized (this.mWindowMap)
    {
      paramIBinder = (WindowState)this.mWindowMap.get(paramIBinder);
      if (paramIBinder == null) {
        return false;
      }
      paramIBinder = paramIBinder.mWinAnimator.mSurfaceController;
      if (paramIBinder == null) {
        return false;
      }
      boolean bool = paramIBinder.clearWindowContentFrameStats();
      return bool;
    }
  }
  
  public void closeSystemDialogs(String paramString)
  {
    for (;;)
    {
      int i;
      int j;
      WindowState localWindowState;
      synchronized (this.mWindowMap)
      {
        int k = this.mDisplayContents.size();
        i = 0;
        if (i < k)
        {
          WindowList localWindowList = ((DisplayContent)this.mDisplayContents.valueAt(i)).getWindowList();
          int m = localWindowList.size();
          j = 0;
          if (j < m)
          {
            localWindowState = (WindowState)localWindowList.get(j);
            boolean bool = localWindowState.mHasSurface;
            if (!bool) {}
          }
        }
      }
      try
      {
        localWindowState.mClient.closeSystemDialogs(paramString);
        j += 1;
        continue;
        i += 1;
        continue;
        return;
        paramString = finally;
        throw paramString;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;) {}
      }
    }
  }
  
  public Configuration computeNewConfiguration()
  {
    synchronized (this.mWindowMap)
    {
      Configuration localConfiguration = computeNewConfigurationLocked();
      return localConfiguration;
    }
  }
  
  void computeScreenConfigurationLocked(Configuration paramConfiguration)
  {
    Object localObject1 = updateDisplayAndOrientationLocked(paramConfiguration.uiMode);
    int j = ((DisplayInfo)localObject1).logicalWidth;
    int k = ((DisplayInfo)localObject1).logicalHeight;
    int i;
    label123:
    int m;
    label167:
    label276:
    Object localObject2;
    int i2;
    int i1;
    int n;
    if (j <= k)
    {
      i = 1;
      paramConfiguration.orientation = i;
      paramConfiguration.screenWidthDp = ((int)(this.mPolicy.getConfigDisplayWidth(j, k, this.mRotation, paramConfiguration.uiMode) / this.mDisplayMetrics.density));
      paramConfiguration.screenHeightDp = ((int)(this.mPolicy.getConfigDisplayHeight(j, k, this.mRotation, paramConfiguration.uiMode) / this.mDisplayMetrics.density));
      if (this.mRotation == 1) {
        break label421;
      }
      if (this.mRotation != 3) {
        break label427;
      }
      bool = true;
      computeSizeRangesAndScreenLayout((DisplayInfo)localObject1, bool, paramConfiguration.uiMode, j, k, this.mDisplayMetrics.density, paramConfiguration);
      m = paramConfiguration.screenLayout;
      if ((((DisplayInfo)localObject1).flags & 0x10) == 0) {
        break label433;
      }
      i = 512;
      paramConfiguration.screenLayout = (i | m & 0xFCFF);
      paramConfiguration.compatScreenWidthDp = ((int)(paramConfiguration.screenWidthDp / this.mCompatibleScreenScale));
      paramConfiguration.compatScreenHeightDp = ((int)(paramConfiguration.screenHeightDp / this.mCompatibleScreenScale));
      paramConfiguration.compatSmallestScreenWidthDp = computeCompatSmallestWidth(bool, paramConfiguration.uiMode, this.mDisplayMetrics, j, k);
      paramConfiguration.densityDpi = ((DisplayInfo)localObject1).logicalDensityDpi;
      paramConfiguration.touchscreen = 1;
      paramConfiguration.keyboard = 1;
      paramConfiguration.navigation = 1;
      k = 0;
      i = 0;
      localObject1 = this.mInputManager.getInputDevices();
      int i3 = localObject1.length;
      m = 0;
      if (m >= i3) {
        break label491;
      }
      localObject2 = localObject1[m];
      i2 = k;
      i1 = i;
      if (!((InputDevice)localObject2).isVirtual())
      {
        i1 = ((InputDevice)localObject2).getSources();
        if (!((InputDevice)localObject2).isExternal()) {
          break label440;
        }
        n = 2;
        label323:
        if (!this.mIsTouchDevice) {
          break label446;
        }
        if ((i1 & 0x1002) == 4098) {
          paramConfiguration.touchscreen = 3;
        }
        label347:
        if ((0x10004 & i1) != 65540) {
          break label454;
        }
        paramConfiguration.navigation = 3;
        j = i | n;
      }
    }
    for (;;)
    {
      i2 = k;
      i1 = j;
      if (((InputDevice)localObject2).getKeyboardType() == 2)
      {
        paramConfiguration.keyboard = 2;
        i2 = k | n;
        i1 = j;
      }
      m += 1;
      k = i2;
      i = i1;
      break label276;
      i = 2;
      break;
      label421:
      bool = true;
      break label123;
      label427:
      bool = false;
      break label123;
      label433:
      i = 256;
      break label167;
      label440:
      n = 1;
      break label323;
      label446:
      paramConfiguration.touchscreen = 1;
      break label347;
      label454:
      j = i;
      if ((i1 & 0x201) == 513)
      {
        j = i;
        if (paramConfiguration.navigation == 1)
        {
          paramConfiguration.navigation = 2;
          j = i | n;
        }
      }
    }
    label491:
    j = i;
    if (paramConfiguration.navigation == 1)
    {
      j = i;
      if (this.mHasPermanentDpad)
      {
        paramConfiguration.navigation = 2;
        j = i | 0x1;
      }
    }
    if (paramConfiguration.keyboard != 1) {}
    for (boolean bool = true;; bool = false)
    {
      if (bool != this.mHardKeyboardAvailable)
      {
        this.mHardKeyboardAvailable = bool;
        this.mH.removeMessages(22);
        this.mH.sendEmptyMessage(22);
      }
      paramConfiguration.keyboardHidden = 1;
      paramConfiguration.hardKeyboardHidden = 1;
      paramConfiguration.navigationHidden = 1;
      this.mPolicy.adjustConfigurationLw(paramConfiguration, k, j);
      return;
    }
  }
  
  public void continueSurfaceLayout()
  {
    synchronized (this.mWindowMap)
    {
      this.mWindowPlacerLocked.continueLayout();
      return;
    }
  }
  
  public void createDisplayContentLocked(Display paramDisplay)
  {
    if (paramDisplay == null) {
      throw new IllegalArgumentException("getDisplayContent: display must not be null");
    }
    getDisplayContentLocked(paramDisplay.getDisplayId());
  }
  
  public void createWallpaperInputConsumer(InputChannel paramInputChannel)
  {
    synchronized (this.mWindowMap)
    {
      this.mWallpaperInputConsumer = new InputConsumerImpl(this, "wallpaper input", paramInputChannel);
      this.mWallpaperInputConsumer.mWindowHandle.hasWallpaper = true;
      this.mInputMonitor.updateInputWindowsLw(true);
      return;
    }
  }
  
  /* Error */
  void createWatermarkInTransaction()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 3465	com/android/server/wm/WindowManagerService:mWatermark	Lcom/android/server/wm/Watermark;
    //   4: ifnull +4 -> 8
    //   7: return
    //   8: new 2567	java/io/File
    //   11: dup
    //   12: ldc_w 3467
    //   15: invokespecial 2570	java/io/File:<init>	(Ljava/lang/String;)V
    //   18: astore_1
    //   19: aconst_null
    //   20: astore 8
    //   22: aconst_null
    //   23: astore 6
    //   25: aconst_null
    //   26: astore 7
    //   28: aconst_null
    //   29: astore 5
    //   31: aconst_null
    //   32: astore_3
    //   33: aconst_null
    //   34: astore 4
    //   36: new 3469	java/io/FileInputStream
    //   39: dup
    //   40: aload_1
    //   41: invokespecial 3472	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   44: astore_1
    //   45: new 3474	java/io/DataInputStream
    //   48: dup
    //   49: aload_1
    //   50: invokespecial 3477	java/io/DataInputStream:<init>	(Ljava/io/InputStream;)V
    //   53: astore_2
    //   54: aload_2
    //   55: invokevirtual 3480	java/io/DataInputStream:readLine	()Ljava/lang/String;
    //   58: astore_3
    //   59: aload_3
    //   60: ifnull +47 -> 107
    //   63: aload_3
    //   64: ldc_w 3482
    //   67: invokevirtual 3486	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
    //   70: astore_3
    //   71: aload_3
    //   72: ifnull +35 -> 107
    //   75: aload_3
    //   76: arraylength
    //   77: ifle +30 -> 107
    //   80: aload_0
    //   81: new 3488	com/android/server/wm/Watermark
    //   84: dup
    //   85: aload_0
    //   86: invokevirtual 1810	com/android/server/wm/WindowManagerService:getDefaultDisplayContentLocked	()Lcom/android/server/wm/DisplayContent;
    //   89: invokevirtual 1583	com/android/server/wm/DisplayContent:getDisplay	()Landroid/view/Display;
    //   92: aload_0
    //   93: getfield 633	com/android/server/wm/WindowManagerService:mRealDisplayMetrics	Landroid/util/DisplayMetrics;
    //   96: aload_0
    //   97: getfield 808	com/android/server/wm/WindowManagerService:mFxSession	Landroid/view/SurfaceSession;
    //   100: aload_3
    //   101: invokespecial 3491	com/android/server/wm/Watermark:<init>	(Landroid/view/Display;Landroid/util/DisplayMetrics;Landroid/view/SurfaceSession;[Ljava/lang/String;)V
    //   104: putfield 3465	com/android/server/wm/WindowManagerService:mWatermark	Lcom/android/server/wm/Watermark;
    //   107: aload_2
    //   108: ifnull +12 -> 120
    //   111: aload_2
    //   112: invokevirtual 3492	java/io/DataInputStream:close	()V
    //   115: return
    //   116: astore_1
    //   117: goto -2 -> 115
    //   120: aload_1
    //   121: ifnull -6 -> 115
    //   124: aload_1
    //   125: invokevirtual 3493	java/io/FileInputStream:close	()V
    //   128: goto -13 -> 115
    //   131: astore_1
    //   132: goto -17 -> 115
    //   135: astore_1
    //   136: aload 4
    //   138: astore_2
    //   139: aload 7
    //   141: astore_1
    //   142: aload_2
    //   143: ifnull +10 -> 153
    //   146: aload_2
    //   147: invokevirtual 3492	java/io/DataInputStream:close	()V
    //   150: return
    //   151: astore_1
    //   152: return
    //   153: aload_1
    //   154: ifnull -39 -> 115
    //   157: aload_1
    //   158: invokevirtual 3493	java/io/FileInputStream:close	()V
    //   161: return
    //   162: astore_1
    //   163: return
    //   164: astore_1
    //   165: aload 5
    //   167: astore_2
    //   168: aload 8
    //   170: astore_1
    //   171: aload_2
    //   172: ifnull +10 -> 182
    //   175: aload_2
    //   176: invokevirtual 3492	java/io/DataInputStream:close	()V
    //   179: return
    //   180: astore_1
    //   181: return
    //   182: aload_1
    //   183: ifnull -68 -> 115
    //   186: aload_1
    //   187: invokevirtual 3493	java/io/FileInputStream:close	()V
    //   190: return
    //   191: astore_1
    //   192: return
    //   193: astore_2
    //   194: aload 6
    //   196: astore_1
    //   197: aload_3
    //   198: ifnull +13 -> 211
    //   201: aload_3
    //   202: invokevirtual 3492	java/io/DataInputStream:close	()V
    //   205: aload_2
    //   206: athrow
    //   207: astore_1
    //   208: goto -3 -> 205
    //   211: aload_1
    //   212: ifnull -7 -> 205
    //   215: aload_1
    //   216: invokevirtual 3493	java/io/FileInputStream:close	()V
    //   219: goto -14 -> 205
    //   222: astore_1
    //   223: goto -18 -> 205
    //   226: astore_2
    //   227: goto -30 -> 197
    //   230: astore 4
    //   232: aload_2
    //   233: astore_3
    //   234: aload 4
    //   236: astore_2
    //   237: goto -40 -> 197
    //   240: astore_2
    //   241: aload 5
    //   243: astore_2
    //   244: goto -73 -> 171
    //   247: astore_3
    //   248: goto -77 -> 171
    //   251: astore_2
    //   252: aload 4
    //   254: astore_2
    //   255: goto -113 -> 142
    //   258: astore_3
    //   259: goto -117 -> 142
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	262	0	this	WindowManagerService
    //   18	32	1	localObject1	Object
    //   116	9	1	localIOException1	IOException
    //   131	1	1	localIOException2	IOException
    //   135	1	1	localIOException3	IOException
    //   141	1	1	localObject2	Object
    //   151	7	1	localIOException4	IOException
    //   162	1	1	localIOException5	IOException
    //   164	1	1	localFileNotFoundException1	java.io.FileNotFoundException
    //   170	1	1	localObject3	Object
    //   180	7	1	localIOException6	IOException
    //   191	1	1	localIOException7	IOException
    //   196	1	1	localObject4	Object
    //   207	9	1	localIOException8	IOException
    //   222	1	1	localIOException9	IOException
    //   53	123	2	localObject5	Object
    //   193	13	2	localObject6	Object
    //   226	7	2	localObject7	Object
    //   236	1	2	localObject8	Object
    //   240	1	2	localFileNotFoundException2	java.io.FileNotFoundException
    //   243	1	2	localObject9	Object
    //   251	1	2	localIOException10	IOException
    //   254	1	2	localObject10	Object
    //   32	202	3	localObject11	Object
    //   247	1	3	localFileNotFoundException3	java.io.FileNotFoundException
    //   258	1	3	localIOException11	IOException
    //   34	103	4	localObject12	Object
    //   230	23	4	localObject13	Object
    //   29	213	5	localObject14	Object
    //   23	172	6	localObject15	Object
    //   26	114	7	localObject16	Object
    //   20	149	8	localObject17	Object
    // Exception table:
    //   from	to	target	type
    //   111	115	116	java/io/IOException
    //   124	128	131	java/io/IOException
    //   36	45	135	java/io/IOException
    //   146	150	151	java/io/IOException
    //   157	161	162	java/io/IOException
    //   36	45	164	java/io/FileNotFoundException
    //   175	179	180	java/io/IOException
    //   186	190	191	java/io/IOException
    //   36	45	193	finally
    //   201	205	207	java/io/IOException
    //   215	219	222	java/io/IOException
    //   45	54	226	finally
    //   54	59	230	finally
    //   63	71	230	finally
    //   75	107	230	finally
    //   45	54	240	java/io/FileNotFoundException
    //   54	59	247	java/io/FileNotFoundException
    //   63	71	247	java/io/FileNotFoundException
    //   75	107	247	java/io/FileNotFoundException
    //   45	54	251	java/io/IOException
    //   54	59	258	java/io/IOException
    //   63	71	258	java/io/IOException
    //   75	107	258	java/io/IOException
  }
  
  public void deferSurfaceLayout()
  {
    synchronized (this.mWindowMap)
    {
      this.mWindowPlacerLocked.deferLayout();
      return;
    }
  }
  
  void destroyPreservedSurfaceLocked()
  {
    int i = this.mDestroyPreservedSurface.size() - 1;
    while (i >= 0)
    {
      ((WindowState)this.mDestroyPreservedSurface.get(i)).mWinAnimator.destroyPreservedSurfaceLocked();
      i -= 1;
    }
    this.mDestroyPreservedSurface.clear();
  }
  
  public void detachStack(int paramInt)
  {
    synchronized (this.mWindowMap)
    {
      TaskStack localTaskStack = (TaskStack)this.mStackIdToStack.get(paramInt);
      if (localTaskStack != null)
      {
        DisplayContent localDisplayContent = localTaskStack.getDisplayContent();
        if (localDisplayContent != null)
        {
          if (localTaskStack.isAnimating())
          {
            localTaskStack.mDeferDetach = true;
            return;
          }
          detachStackLocked(localDisplayContent, localTaskStack);
        }
      }
      return;
    }
  }
  
  void detachStackLocked(DisplayContent paramDisplayContent, TaskStack paramTaskStack)
  {
    paramDisplayContent.detachStack(paramTaskStack);
    paramTaskStack.detachDisplay();
    if (paramTaskStack.mStackId == 3) {
      getDefaultDisplayContentLocked().mDividerControllerLocked.notifyDockedStackExistsChanged(false);
    }
  }
  
  public boolean detectSafeMode()
  {
    if (!this.mInputMonitor.waitForInputDevicesReady(1000L)) {
      Slog.w("WindowManager", "Devices still not ready after waiting 1000 milliseconds before attempting to detect safe mode.");
    }
    if (Settings.Global.getInt(this.mContext.getContentResolver(), "safe_boot_disallowed", 0) != 0) {
      return false;
    }
    int i = this.mInputManager.getKeyCodeState(-1, 65280, 82);
    int j = this.mInputManager.getKeyCodeState(-1, 65280, 47);
    int k = this.mInputManager.getKeyCodeState(-1, 513, 23);
    int m = this.mInputManager.getScanCodeState(-1, 65540, 272);
    int n = this.mInputManager.getKeyCodeState(-1, 65280, 25);
    boolean bool;
    if ((i > 0) || (j > 0))
    {
      bool = true;
      label126:
      this.mSafeMode = bool;
    }
    try
    {
      if ((SystemProperties.getInt("persist.sys.safemode", 0) != 0) || (SystemProperties.getInt("ro.sys.safemode", 0) != 0))
      {
        n = SystemProperties.getInt("persist.sys.audit_safemode", 0);
        if (n != 0) {
          break label304;
        }
        this.mSafeMode = true;
        SystemProperties.set("persist.sys.safemode", "");
      }
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      label180:
      for (;;) {}
    }
    if (this.mSafeMode)
    {
      Log.i("WindowManager", "SAFE MODE ENABLED (menu=" + i + " s=" + j + " dpad=" + k + " trackball=" + m + ")");
      SystemProperties.set("ro.sys.safemode", "1");
    }
    for (;;)
    {
      this.mPolicy.setSafeMode(this.mSafeMode);
      return this.mSafeMode;
      if ((k > 0) || (m > 0)) {
        break;
      }
      if (n > 0)
      {
        bool = true;
        break label126;
      }
      bool = false;
      break label126;
      label304:
      if (n >= SystemProperties.getInt("ro.build.date.utc", 0))
      {
        this.mSafeMode = true;
        showAuditSafeModeNotification();
        break label180;
      }
      SystemProperties.set("persist.sys.safemode", "");
      SystemProperties.set("persist.sys.audit_safemode", "");
      break label180;
      Log.i("WindowManager", "SAFE MODE not enabled");
    }
  }
  
  public void disableKeyguard(IBinder paramIBinder, String paramString)
  {
    if (this.mContext.checkCallingOrSelfPermission("android.permission.DISABLE_KEYGUARD") != 0) {
      throw new SecurityException("Requires DISABLE_KEYGUARD permission");
    }
    if ((Binder.getCallingUid() != 1000) && (isKeyguardSecure()))
    {
      Log.d("WindowManager", "current mode is SecurityMode, ignore disableKeyguard");
      return;
    }
    if (Binder.getCallingUserHandle().getIdentifier() != this.mCurrentUserId)
    {
      Log.d("WindowManager", "non-current user, ignore disableKeyguard");
      return;
    }
    if (paramIBinder == null) {
      throw new IllegalArgumentException("token == null");
    }
    if (WindowManagerDebugConfig.DEBUG_ONEPLUS) {
      Slog.d(TAG, "disableKeyguard(), tag= " + paramString + ", pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
    }
    this.mKeyguardDisableHandler.sendMessage(this.mKeyguardDisableHandler.obtainMessage(1, new Pair(paramIBinder, paramString)));
  }
  
  public void dismissKeyguard()
  {
    if (this.mContext.checkCallingOrSelfPermission("android.permission.DISABLE_KEYGUARD") != 0) {
      throw new SecurityException("Requires DISABLE_KEYGUARD permission");
    }
    synchronized (this.mWindowMap)
    {
      this.mPolicy.dismissKeyguardLw();
      return;
    }
  }
  
  void dispatchNewAnimatorScaleLocked(Session paramSession)
  {
    this.mH.obtainMessage(34, paramSession).sendToTarget();
  }
  
  public void displayReady()
  {
    ??? = this.mDisplays;
    int i = 0;
    int j = ???.length;
    while (i < j)
    {
      displayReady(???[i].getDisplayId());
      i += 1;
    }
    synchronized (this.mWindowMap)
    {
      readForcedDisplayPropertiesLocked(getDefaultDisplayContentLocked());
      this.mDisplayReady = true;
    }
    try
    {
      this.mActivityManager.updateConfiguration(null);
      synchronized (this.mWindowMap)
      {
        this.mIsTouchDevice = this.mContext.getPackageManager().hasSystemFeature("android.hardware.touchscreen");
        configureDisplayPolicyLocked(getDefaultDisplayContentLocked());
      }
      try
      {
        this.mActivityManager.updateConfiguration(null);
        updateCircularDisplayMaskIfNeeded();
        return;
        localObject2 = finally;
        throw ((Throwable)localObject2);
        localObject3 = finally;
        throw ((Throwable)localObject3);
      }
      catch (RemoteException localRemoteException1)
      {
        for (;;) {}
      }
    }
    catch (RemoteException localRemoteException2)
    {
      for (;;) {}
    }
  }
  
  public void dump(FileDescriptor arg1, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    ??? = null;
    if (this.mContext.checkCallingOrSelfPermission("android.permission.DUMP") != 0)
    {
      paramPrintWriter.println("Permission Denial: can't dump WindowManager from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
      return;
    }
    boolean bool = false;
    int i = 0;
    for (;;)
    {
      if (i < paramArrayOfString.length)
      {
        ??? = paramArrayOfString[i];
        if ((??? != null) && (((String)???).length() > 0)) {}
      }
      else
      {
        label86:
        if (i >= paramArrayOfString.length) {
          break;
        }
        ??? = paramArrayOfString[i];
        i += 1;
        if ((!"lastanr".equals(???)) && (!"l".equals(???))) {
          break label350;
        }
      }
      label350:
      synchronized (this.mWindowMap)
      {
        dumpLastANRLocked(paramPrintWriter);
        return;
        if (((String)???).charAt(0) != '-') {
          break label86;
        }
        i += 1;
        if ("-a".equals(???))
        {
          bool = true;
        }
        else
        {
          if ("-h".equals(???))
          {
            paramPrintWriter.println("Window manager dump options:");
            paramPrintWriter.println("  [-a] [-h] [cmd] ...");
            paramPrintWriter.println("  cmd may be one of:");
            paramPrintWriter.println("    l[astanr]: last ANR information");
            paramPrintWriter.println("    p[policy]: policy state");
            paramPrintWriter.println("    a[animator]: animator state");
            paramPrintWriter.println("    s[essions]: active sessions");
            paramPrintWriter.println("    surfaces: active surfaces (debugging enabled only)");
            paramPrintWriter.println("    d[isplays]: active display contents");
            paramPrintWriter.println("    t[okens]: token list");
            paramPrintWriter.println("    w[indows]: window list");
            paramPrintWriter.println("  cmd may also be a NAME to dump windows.  NAME may");
            paramPrintWriter.println("    be a partial substring in a window name, a");
            paramPrintWriter.println("    Window hex object identifier, or");
            paramPrintWriter.println("    \"all\" for all windows, or");
            paramPrintWriter.println("    \"visible\" for the visible windows.");
            paramPrintWriter.println("    \"visible-apps\" for the visible app windows.");
            paramPrintWriter.println("  -a: include all available server state.");
            return;
          }
          paramPrintWriter.println("Unknown argument: " + (String)??? + "; use -h for help");
        }
      }
    }
    if ("p".equals(???)) {
      synchronized (this.mWindowMap)
      {
        dumpPolicyLocked(paramPrintWriter, paramArrayOfString, true);
        return;
      }
    }
    if (("animator".equals(???)) || ("a".equals(???))) {
      synchronized (this.mWindowMap)
      {
        dumpAnimatorLocked(paramPrintWriter, paramArrayOfString, true);
        return;
      }
    }
    if (("sessions".equals(???)) || ("s".equals(???))) {
      synchronized (this.mWindowMap)
      {
        dumpSessionsLocked(paramPrintWriter, true);
        return;
      }
    }
    if ("surfaces".equals(???)) {
      synchronized (this.mWindowMap)
      {
        WindowSurfaceController.SurfaceTrace.dumpAllSurfaces(paramPrintWriter, null);
        return;
      }
    }
    if (("displays".equals(???)) || ("d".equals(???))) {
      synchronized (this.mWindowMap)
      {
        dumpDisplayContentsLocked(paramPrintWriter, true);
        return;
      }
    }
    if (("tokens".equals(???)) || ("t".equals(???))) {
      synchronized (this.mWindowMap)
      {
        dumpTokensLocked(paramPrintWriter, true);
        return;
      }
    }
    if (("windows".equals(???)) || ("w".equals(???))) {
      synchronized (this.mWindowMap)
      {
        dumpWindowsLocked(paramPrintWriter, true, null);
        return;
      }
    }
    if (("all".equals(???)) || ("a".equals(???))) {
      synchronized (this.mWindowMap)
      {
        dumpWindowsLocked(paramPrintWriter, true, null);
        return;
      }
    }
    if ("log".equals(???))
    {
      dynamicallyConfigLogTag(paramPrintWriter, paramArrayOfString, i);
      return;
    }
    if (!dumpWindows(paramPrintWriter, ???, paramArrayOfString, i, bool))
    {
      paramPrintWriter.println("Bad window command, or no windows match: " + ???);
      paramPrintWriter.println("Use -h for help.");
    }
    return;
    for (;;)
    {
      synchronized (this.mWindowMap)
      {
        paramPrintWriter.println();
        if (bool) {
          paramPrintWriter.println("-------------------------------------------------------------------------------");
        }
        dumpLastANRLocked(paramPrintWriter);
        paramPrintWriter.println();
        if (bool) {
          paramPrintWriter.println("-------------------------------------------------------------------------------");
        }
        dumpPolicyLocked(paramPrintWriter, paramArrayOfString, bool);
        paramPrintWriter.println();
        if (bool) {
          paramPrintWriter.println("-------------------------------------------------------------------------------");
        }
        dumpAnimatorLocked(paramPrintWriter, paramArrayOfString, bool);
        paramPrintWriter.println();
        if (bool) {
          paramPrintWriter.println("-------------------------------------------------------------------------------");
        }
        dumpSessionsLocked(paramPrintWriter, bool);
        paramPrintWriter.println();
        if (bool)
        {
          paramPrintWriter.println("-------------------------------------------------------------------------------");
          break label941;
          WindowSurfaceController.SurfaceTrace.dumpAllSurfaces(paramPrintWriter, ???);
          paramPrintWriter.println();
          if (bool) {
            paramPrintWriter.println("-------------------------------------------------------------------------------");
          }
          dumpDisplayContentsLocked(paramPrintWriter, bool);
          paramPrintWriter.println();
          if (bool) {
            paramPrintWriter.println("-------------------------------------------------------------------------------");
          }
          dumpTokensLocked(paramPrintWriter, bool);
          paramPrintWriter.println();
          if (bool) {
            paramPrintWriter.println("-------------------------------------------------------------------------------");
          }
          dumpWindowsLocked(paramPrintWriter, bool, null);
          return;
        }
      }
      label941:
      if (bool) {
        ??? = "-------------------------------------------------------------------------------";
      }
    }
  }
  
  void dumpAnimatorLocked(PrintWriter paramPrintWriter, String[] paramArrayOfString, boolean paramBoolean)
  {
    paramPrintWriter.println("WINDOW MANAGER ANIMATOR STATE (dumpsys window animator)");
    this.mAnimator.dumpLocked(paramPrintWriter, "    ", paramBoolean);
  }
  
  void dumpAppTokensLocked()
  {
    int m = this.mStackIdToStack.size();
    int i = 0;
    while (i < m)
    {
      Object localObject1 = (TaskStack)this.mStackIdToStack.valueAt(i);
      Slog.v("WindowManager", "  Stack #" + ((TaskStack)localObject1).mStackId + " tasks from bottom to top:");
      localObject1 = ((TaskStack)localObject1).getTasks();
      int n = ((ArrayList)localObject1).size();
      int j = 0;
      while (j < n)
      {
        Object localObject2 = (Task)((ArrayList)localObject1).get(j);
        Slog.v("WindowManager", "    Task #" + ((Task)localObject2).mTaskId + " activities from bottom to top:");
        localObject2 = ((Task)localObject2).mAppTokens;
        int i1 = ((AppTokenList)localObject2).size();
        int k = 0;
        while (k < i1)
        {
          Slog.v("WindowManager", "      activity #" + k + ": " + ((AppWindowToken)((AppTokenList)localObject2).get(k)).token);
          k += 1;
        }
        j += 1;
      }
      i += 1;
    }
  }
  
  void dumpDisplayContentsLocked(PrintWriter paramPrintWriter, boolean paramBoolean)
  {
    paramPrintWriter.println("WINDOW MANAGER DISPLAY CONTENTS (dumpsys window displays)");
    if (this.mDisplayReady)
    {
      int j = this.mDisplayContents.size();
      int i = 0;
      while (i < j)
      {
        ((DisplayContent)this.mDisplayContents.valueAt(i)).dump("  ", paramPrintWriter);
        i += 1;
      }
    }
    paramPrintWriter.println("  NO DISPLAY");
  }
  
  void dumpLastANRLocked(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("WINDOW MANAGER LAST ANR (dumpsys window lastanr)");
    if (this.mLastANRState == null)
    {
      paramPrintWriter.println("  <no ANR has occurred since boot>");
      return;
    }
    paramPrintWriter.println(this.mLastANRState);
  }
  
  void dumpPolicyLocked(PrintWriter paramPrintWriter, String[] paramArrayOfString, boolean paramBoolean)
  {
    paramPrintWriter.println("WINDOW MANAGER POLICY STATE (dumpsys window policy)");
    this.mPolicy.dump("    ", paramPrintWriter, paramArrayOfString);
  }
  
  void dumpSessionsLocked(PrintWriter paramPrintWriter, boolean paramBoolean)
  {
    paramPrintWriter.println("WINDOW MANAGER SESSIONS (dumpsys window sessions)");
    int i = 0;
    while (i < this.mSessions.size())
    {
      Session localSession = (Session)this.mSessions.valueAt(i);
      paramPrintWriter.print("  Session ");
      paramPrintWriter.print(localSession);
      paramPrintWriter.println(':');
      localSession.dump(paramPrintWriter, "    ");
      i += 1;
    }
  }
  
  void dumpTokensLocked(PrintWriter paramPrintWriter, boolean paramBoolean)
  {
    paramPrintWriter.println("WINDOW MANAGER TOKENS (dumpsys window tokens)");
    Object localObject;
    if (!this.mTokenMap.isEmpty())
    {
      paramPrintWriter.println("  All tokens:");
      localObject = this.mTokenMap.values().iterator();
      while (((Iterator)localObject).hasNext())
      {
        WindowToken localWindowToken = (WindowToken)((Iterator)localObject).next();
        paramPrintWriter.print("  ");
        paramPrintWriter.print(localWindowToken);
        if (paramBoolean)
        {
          paramPrintWriter.println(':');
          localWindowToken.dump(paramPrintWriter, "    ");
        }
        else
        {
          paramPrintWriter.println();
        }
      }
    }
    this.mWallpaperControllerLocked.dumpTokens(paramPrintWriter, "  ", paramBoolean);
    if (!this.mFinishedStarting.isEmpty())
    {
      paramPrintWriter.println();
      paramPrintWriter.println("  Finishing start of application tokens:");
      int i = this.mFinishedStarting.size() - 1;
      if (i >= 0)
      {
        localObject = (WindowToken)this.mFinishedStarting.get(i);
        paramPrintWriter.print("  Finished Starting #");
        paramPrintWriter.print(i);
        paramPrintWriter.print(' ');
        paramPrintWriter.print(localObject);
        if (paramBoolean)
        {
          paramPrintWriter.println(':');
          ((WindowToken)localObject).dump(paramPrintWriter, "    ");
        }
        for (;;)
        {
          i -= 1;
          break;
          paramPrintWriter.println();
        }
      }
    }
    if ((this.mOpeningApps.isEmpty()) && (this.mClosingApps.isEmpty())) {}
    do
    {
      return;
      paramPrintWriter.println();
      if (this.mOpeningApps.size() > 0)
      {
        paramPrintWriter.print("  mOpeningApps=");
        paramPrintWriter.println(this.mOpeningApps);
      }
    } while (this.mClosingApps.size() <= 0);
    paramPrintWriter.print("  mClosingApps=");
    paramPrintWriter.println(this.mClosingApps);
  }
  
  boolean dumpWindows(PrintWriter paramPrintWriter, String arg2, String[] paramArrayOfString, int paramInt, boolean paramBoolean)
  {
    WindowList localWindowList = new WindowList();
    int j;
    int i;
    if (("apps".equals(???)) || ("visible".equals(???)) || ("visible-apps".equals(???)))
    {
      boolean bool1 = ???.contains("apps");
      boolean bool2 = ???.contains("visible");
      ??? = this.mWindowMap;
      if (bool1) {}
      try
      {
        dumpDisplayContentsLocked(paramPrintWriter, true);
        j = this.mDisplayContents.size();
        paramInt = 0;
        for (;;)
        {
          paramArrayOfString = ???;
          if (paramInt >= j) {
            break;
          }
          paramArrayOfString = ((DisplayContent)this.mDisplayContents.valueAt(paramInt)).getWindowList();
          i = paramArrayOfString.size() - 1;
          while (i >= 0)
          {
            ??? = (WindowState)paramArrayOfString.get(i);
            if (((!bool2) || (((WindowState)???).mWinAnimator.getShown())) && ((!bool1) || (((WindowState)???).mAppToken != null))) {
              localWindowList.add(???);
            }
            i -= 1;
          }
          paramInt += 1;
        }
        if (localWindowList.size() > 0) {
          break label367;
        }
        return false;
      }
      finally {}
    }
    paramInt = 0;
    for (;;)
    {
      try
      {
        i = Integer.parseInt(???, 16);
        paramInt = i;
        ??? = null;
      }
      catch (RuntimeException paramArrayOfString)
      {
        int k;
        label367:
        continue;
      }
      synchronized (this.mWindowMap)
      {
        k = this.mDisplayContents.size();
        i = 0;
        paramArrayOfString = (String[])???;
        if (i >= k) {
          break;
        }
        paramArrayOfString = ((DisplayContent)this.mDisplayContents.valueAt(i)).getWindowList();
        j = paramArrayOfString.size() - 1;
        if (j >= 0)
        {
          WindowState localWindowState = (WindowState)paramArrayOfString.get(j);
          if (??? != null)
          {
            if (localWindowState.mAttrs.getTitle().toString().contains(???)) {
              localWindowList.add(localWindowState);
            }
          }
          else if (System.identityHashCode(localWindowState) == paramInt) {
            localWindowList.add(localWindowState);
          }
        }
      }
      i += 1;
      continue;
      synchronized (this.mWindowMap)
      {
        dumpWindowsLocked(paramPrintWriter, paramBoolean, localWindowList);
        return true;
      }
      j -= 1;
    }
  }
  
  void dumpWindowsLocked()
  {
    int k = this.mDisplayContents.size();
    int i = 0;
    while (i < k)
    {
      Object localObject = (DisplayContent)this.mDisplayContents.valueAt(i);
      Slog.v("WindowManager", " Display #" + ((DisplayContent)localObject).getDisplayId());
      localObject = ((DisplayContent)localObject).getWindowList();
      int j = ((WindowList)localObject).size() - 1;
      while (j >= 0)
      {
        Slog.v("WindowManager", "  #" + j + ": " + ((WindowList)localObject).get(j));
        j -= 1;
      }
      i += 1;
    }
  }
  
  void dumpWindowsLocked(PrintWriter paramPrintWriter, boolean paramBoolean, ArrayList<WindowState> paramArrayList)
  {
    paramPrintWriter.println("WINDOW MANAGER WINDOWS (dumpsys window windows)");
    dumpWindowsNoHeaderLocked(paramPrintWriter, paramBoolean, paramArrayList);
  }
  
  void dumpWindowsNoHeaderLocked(PrintWriter paramPrintWriter, boolean paramBoolean, ArrayList<WindowState> paramArrayList)
  {
    int k = this.mDisplayContents.size();
    int i = 0;
    Object localObject;
    while (i < k)
    {
      localObject = ((DisplayContent)this.mDisplayContents.valueAt(i)).getWindowList();
      int j = ((WindowList)localObject).size() - 1;
      if (j >= 0)
      {
        WindowState localWindowState = (WindowState)((WindowList)localObject).get(j);
        if ((paramArrayList == null) || (paramArrayList.contains(localWindowState)))
        {
          paramPrintWriter.print("  Window #");
          paramPrintWriter.print(j);
          paramPrintWriter.print(' ');
          paramPrintWriter.print(localWindowState);
          paramPrintWriter.println(":");
          if ((!paramBoolean) && (paramArrayList == null)) {
            break label138;
          }
        }
        label138:
        for (boolean bool = true;; bool = false)
        {
          localWindowState.dump(paramPrintWriter, "    ", bool);
          j -= 1;
          break;
        }
      }
      i += 1;
    }
    if (this.mInputMethodDialogs.size() > 0)
    {
      paramPrintWriter.println();
      paramPrintWriter.println("  Input method dialogs:");
      i = this.mInputMethodDialogs.size() - 1;
      while (i >= 0)
      {
        localObject = (WindowState)this.mInputMethodDialogs.get(i);
        if ((paramArrayList == null) || (paramArrayList.contains(localObject)))
        {
          paramPrintWriter.print("  IM Dialog #");
          paramPrintWriter.print(i);
          paramPrintWriter.print(": ");
          paramPrintWriter.println(localObject);
        }
        i -= 1;
      }
    }
    if (this.mPendingRemove.size() > 0)
    {
      paramPrintWriter.println();
      paramPrintWriter.println("  Remove pending for:");
      i = this.mPendingRemove.size() - 1;
      if (i >= 0)
      {
        localObject = (WindowState)this.mPendingRemove.get(i);
        if ((paramArrayList == null) || (paramArrayList.contains(localObject)))
        {
          paramPrintWriter.print("  Remove #");
          paramPrintWriter.print(i);
          paramPrintWriter.print(' ');
          paramPrintWriter.print(localObject);
          if (!paramBoolean) {
            break label371;
          }
          paramPrintWriter.println(":");
          ((WindowState)localObject).dump(paramPrintWriter, "    ", true);
        }
        for (;;)
        {
          i -= 1;
          break;
          label371:
          paramPrintWriter.println();
        }
      }
    }
    if ((this.mForceRemoves != null) && (this.mForceRemoves.size() > 0))
    {
      paramPrintWriter.println();
      paramPrintWriter.println("  Windows force removing:");
      i = this.mForceRemoves.size() - 1;
      if (i >= 0)
      {
        localObject = (WindowState)this.mForceRemoves.get(i);
        paramPrintWriter.print("  Removing #");
        paramPrintWriter.print(i);
        paramPrintWriter.print(' ');
        paramPrintWriter.print(localObject);
        if (paramBoolean)
        {
          paramPrintWriter.println(":");
          ((WindowState)localObject).dump(paramPrintWriter, "    ", true);
        }
        for (;;)
        {
          i -= 1;
          break;
          paramPrintWriter.println();
        }
      }
    }
    if (this.mDestroySurface.size() > 0)
    {
      paramPrintWriter.println();
      paramPrintWriter.println("  Windows waiting to destroy their surface:");
      i = this.mDestroySurface.size() - 1;
      if (i >= 0)
      {
        localObject = (WindowState)this.mDestroySurface.get(i);
        if ((paramArrayList == null) || (paramArrayList.contains(localObject)))
        {
          paramPrintWriter.print("  Destroy #");
          paramPrintWriter.print(i);
          paramPrintWriter.print(' ');
          paramPrintWriter.print(localObject);
          if (!paramBoolean) {
            break label617;
          }
          paramPrintWriter.println(":");
          ((WindowState)localObject).dump(paramPrintWriter, "    ", true);
        }
        for (;;)
        {
          i -= 1;
          break;
          label617:
          paramPrintWriter.println();
        }
      }
    }
    if (this.mLosingFocus.size() > 0)
    {
      paramPrintWriter.println();
      paramPrintWriter.println("  Windows losing focus:");
      i = this.mLosingFocus.size() - 1;
      if (i >= 0)
      {
        localObject = (WindowState)this.mLosingFocus.get(i);
        if ((paramArrayList == null) || (paramArrayList.contains(localObject)))
        {
          paramPrintWriter.print("  Losing #");
          paramPrintWriter.print(i);
          paramPrintWriter.print(' ');
          paramPrintWriter.print(localObject);
          if (!paramBoolean) {
            break label743;
          }
          paramPrintWriter.println(":");
          ((WindowState)localObject).dump(paramPrintWriter, "    ", true);
        }
        for (;;)
        {
          i -= 1;
          break;
          label743:
          paramPrintWriter.println();
        }
      }
    }
    if (this.mResizingWindows.size() > 0)
    {
      paramPrintWriter.println();
      paramPrintWriter.println("  Windows waiting to resize:");
      i = this.mResizingWindows.size() - 1;
      if (i >= 0)
      {
        localObject = (WindowState)this.mResizingWindows.get(i);
        if ((paramArrayList == null) || (paramArrayList.contains(localObject)))
        {
          paramPrintWriter.print("  Resizing #");
          paramPrintWriter.print(i);
          paramPrintWriter.print(' ');
          paramPrintWriter.print(localObject);
          if (!paramBoolean) {
            break label869;
          }
          paramPrintWriter.println(":");
          ((WindowState)localObject).dump(paramPrintWriter, "    ", true);
        }
        for (;;)
        {
          i -= 1;
          break;
          label869:
          paramPrintWriter.println();
        }
      }
    }
    if (this.mWaitingForDrawn.size() > 0)
    {
      paramPrintWriter.println();
      paramPrintWriter.println("  Clients waiting for these windows to be drawn:");
      i = this.mWaitingForDrawn.size() - 1;
      while (i >= 0)
      {
        paramArrayList = (WindowState)this.mWaitingForDrawn.get(i);
        paramPrintWriter.print("  Waiting #");
        paramPrintWriter.print(i);
        paramPrintWriter.print(' ');
        paramPrintWriter.print(paramArrayList);
        i -= 1;
      }
    }
    paramPrintWriter.println();
    paramPrintWriter.print("  mCurConfiguration=");
    paramPrintWriter.println(this.mCurConfiguration);
    paramPrintWriter.print("  mHasPermanentDpad=");
    paramPrintWriter.println(this.mHasPermanentDpad);
    paramPrintWriter.print("  mCurrentFocus=");
    paramPrintWriter.println(this.mCurrentFocus);
    if (this.mLastFocus != this.mCurrentFocus)
    {
      paramPrintWriter.print("  mLastFocus=");
      paramPrintWriter.println(this.mLastFocus);
    }
    paramPrintWriter.print("  mFocusedApp=");
    paramPrintWriter.println(this.mFocusedApp);
    if (this.mInputMethodTarget != null)
    {
      paramPrintWriter.print("  mInputMethodTarget=");
      paramPrintWriter.println(this.mInputMethodTarget);
    }
    paramPrintWriter.print("  mInTouchMode=");
    paramPrintWriter.print(this.mInTouchMode);
    paramPrintWriter.print(" mLayoutSeq=");
    paramPrintWriter.println(this.mLayoutSeq);
    paramPrintWriter.print("  mLastDisplayFreezeDuration=");
    TimeUtils.formatDuration(this.mLastDisplayFreezeDuration, paramPrintWriter);
    if (this.mLastFinishedFreezeSource != null)
    {
      paramPrintWriter.print(" due to ");
      paramPrintWriter.print(this.mLastFinishedFreezeSource);
    }
    paramPrintWriter.println();
    paramPrintWriter.print("  mLastWakeLockHoldingWindow=");
    paramPrintWriter.print(this.mLastWakeLockHoldingWindow);
    paramPrintWriter.print(" mLastWakeLockObscuringWindow=");
    paramPrintWriter.print(this.mLastWakeLockObscuringWindow);
    paramPrintWriter.println();
    this.mInputMonitor.dump(paramPrintWriter, "  ");
    if (paramBoolean)
    {
      paramPrintWriter.print("  mSystemDecorLayer=");
      paramPrintWriter.print(this.mSystemDecorLayer);
      paramPrintWriter.print(" mScreenRect=");
      paramPrintWriter.println(this.mScreenRect.toShortString());
      if (this.mLastStatusBarVisibility != 0)
      {
        paramPrintWriter.print("  mLastStatusBarVisibility=0x");
        paramPrintWriter.println(Integer.toHexString(this.mLastStatusBarVisibility));
      }
      if (this.mInputMethodWindow != null)
      {
        paramPrintWriter.print("  mInputMethodWindow=");
        paramPrintWriter.println(this.mInputMethodWindow);
      }
      this.mWindowPlacerLocked.dump(paramPrintWriter, "  ");
      this.mWallpaperControllerLocked.dump(paramPrintWriter, "  ");
      this.mLayersController.dump(paramPrintWriter, "  ");
      paramPrintWriter.print("  mSystemBooted=");
      paramPrintWriter.print(this.mSystemBooted);
      paramPrintWriter.print(" mDisplayEnabled=");
      paramPrintWriter.println(this.mDisplayEnabled);
      if (needsLayout())
      {
        paramPrintWriter.print("  layoutNeeded on displays=");
        i = 0;
        while (i < k)
        {
          paramArrayList = (DisplayContent)this.mDisplayContents.valueAt(i);
          if (paramArrayList.layoutNeeded) {
            paramPrintWriter.print(paramArrayList.getDisplayId());
          }
          i += 1;
        }
        paramPrintWriter.println();
      }
      paramPrintWriter.print("  mTransactionSequence=");
      paramPrintWriter.println(this.mTransactionSequence);
      paramPrintWriter.print("  mDisplayFrozen=");
      paramPrintWriter.print(this.mDisplayFrozen);
      paramPrintWriter.print(" windows=");
      paramPrintWriter.print(this.mWindowsFreezingScreen);
      paramPrintWriter.print(" client=");
      paramPrintWriter.print(this.mClientFreezingScreen);
      paramPrintWriter.print(" apps=");
      paramPrintWriter.print(this.mAppsFreezingScreen);
      paramPrintWriter.print(" waitingForConfig=");
      paramPrintWriter.println(this.mWaitingForConfig);
      paramPrintWriter.print("  mRotation=");
      paramPrintWriter.print(this.mRotation);
      paramPrintWriter.print(" mAltOrientation=");
      paramPrintWriter.println(this.mAltOrientation);
      paramPrintWriter.print("  mLastWindowForcedOrientation=");
      paramPrintWriter.print(this.mLastWindowForcedOrientation);
      paramPrintWriter.print(" mLastOrientation=");
      paramPrintWriter.println(this.mLastOrientation);
      paramPrintWriter.print("  mDeferredRotationPauseCount=");
      paramPrintWriter.println(this.mDeferredRotationPauseCount);
      paramPrintWriter.print("  Animation settings: disabled=");
      paramPrintWriter.print(this.mAnimationsDisabled);
      paramPrintWriter.print(" window=");
      paramPrintWriter.print(this.mWindowAnimationScaleSetting);
      paramPrintWriter.print(" transition=");
      paramPrintWriter.print(this.mTransitionAnimationScaleSetting);
      paramPrintWriter.print(" animator=");
      paramPrintWriter.println(this.mAnimatorDurationScaleSetting);
      paramPrintWriter.print(" mSkipAppTransitionAnimation=");
      paramPrintWriter.println(this.mSkipAppTransitionAnimation);
      paramPrintWriter.println("  mLayoutToAnim:");
      this.mAppTransition.dump(paramPrintWriter, "    ");
    }
  }
  
  protected void dynamicallyConfigLogTag(PrintWriter paramPrintWriter, String[] paramArrayOfString, int paramInt)
  {
    paramPrintWriter.println("dynamicallyConfigLogTag, opti:" + paramInt + ", args.length:" + paramArrayOfString.length);
    paramInt = 0;
    while (paramInt < paramArrayOfString.length)
    {
      paramPrintWriter.println("dynamicallyConfigLogTag, args[" + paramInt + "]:" + paramArrayOfString[paramInt]);
      paramInt += 1;
    }
    if (paramArrayOfString.length != 3)
    {
      paramPrintWriter.println("********** Invalid argument! Get detail help as bellow: **********");
      logoutTagConfigHelp(paramPrintWriter);
      return;
    }
    String str = paramArrayOfString[1];
    if ("1".equals(paramArrayOfString[2])) {}
    for (boolean bool = true;; bool = false)
    {
      paramPrintWriter.println("dynamicallyConfigLogTag, tag:" + str + ", on:" + bool);
      if (!"window".equals(str)) {
        break;
      }
      WindowManagerDebugConfig.DEBUG_ADD_REMOVE = bool;
      WindowManagerDebugConfig.DEBUG_FOCUS = bool;
      WindowManagerDebugConfig.DEBUG_FOCUS_LIGHT = bool;
      WindowManagerDebugConfig.DEBUG_WINDOW_MOVEMENT = bool;
      WindowManagerDebugConfig.DEBUG_TASK_MOVEMENT = bool;
      WindowManagerDebugConfig.DEBUG_STARTING_WINDOW = bool;
      WindowManagerDebugConfig.DEBUG_STACK = bool;
      return;
    }
    if ("fresh".equals(str))
    {
      WindowManagerDebugConfig.DEBUG_LAYOUT = bool;
      WindowManagerDebugConfig.DEBUG_RESIZE = bool;
      WindowManagerDebugConfig.DEBUG_VISIBILITY = bool;
      return;
    }
    if ("anim".equals(str))
    {
      WindowManagerDebugConfig.DEBUG_ANIM = bool;
      return;
    }
    if ("input".equals(str))
    {
      WindowManagerDebugConfig.DEBUG_INPUT = bool;
      WindowManagerDebugConfig.DEBUG_INPUT_METHOD = bool;
      WindowManagerDebugConfig.DEBUG_DRAG = bool;
      return;
    }
    if ("screen".equals(str))
    {
      WindowManagerDebugConfig.DEBUG_SCREEN_ON = bool;
      WindowManagerDebugConfig.DEBUG_SCREENSHOT = bool;
      WindowManagerDebugConfig.DEBUG_BOOT = bool;
      return;
    }
    if ("apptoken".equals(str))
    {
      WindowManagerDebugConfig.DEBUG_TOKEN_MOVEMENT = bool;
      WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS = bool;
      WindowManagerDebugConfig.DEBUG_APP_ORIENTATION = bool;
      return;
    }
    if ("wallpaper".equals(str))
    {
      WindowManagerDebugConfig.DEBUG_WALLPAPER = bool;
      WindowManagerDebugConfig.DEBUG_WALLPAPER_LIGHT = bool;
      return;
    }
    if ("config".equals(str))
    {
      WindowManagerDebugConfig.DEBUG_ORIENTATION = bool;
      WindowManagerDebugConfig.DEBUG_APP_ORIENTATION = bool;
      WindowManagerDebugConfig.DEBUG_CONFIGURATION = bool;
      PROFILE_ORIENTATION = bool;
      return;
    }
    if ("trace".equals(str))
    {
      WindowManagerDebugConfig.DEBUG_SURFACE_TRACE = bool;
      WindowManagerDebugConfig.DEBUG_WINDOW_TRACE = bool;
      return;
    }
    if ("surface".equals(str))
    {
      WindowManagerDebugConfig.SHOW_SURFACE_ALLOC = bool;
      WindowManagerDebugConfig.SHOW_TRANSACTIONS = bool;
      WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS = bool;
      return;
    }
    if ("layer".equals(str))
    {
      WindowManagerDebugConfig.DEBUG_LAYERS = bool;
      return;
    }
    if ("policy".equals(str))
    {
      DEBUG_POLICY = bool;
      this.mPolicy.dump("debuglog", paramPrintWriter, paramArrayOfString);
      return;
    }
    if ("local".equals(str))
    {
      localLOGV = bool;
      return;
    }
    paramPrintWriter.println("Failed! Invalid argument! Type cmd for help: dumpsys window log");
  }
  
  public void enableScreenAfterBoot()
  {
    synchronized (this.mWindowMap)
    {
      if (WindowManagerDebugConfig.DEBUG_BOOT)
      {
        RuntimeException localRuntimeException = new RuntimeException("here");
        localRuntimeException.fillInStackTrace();
        Slog.i("WindowManager", "enableScreenAfterBoot: mDisplayEnabled=" + this.mDisplayEnabled + " mForceDisplayEnabled=" + this.mForceDisplayEnabled + " mShowingBootMessages=" + this.mShowingBootMessages + " mSystemBooted=" + this.mSystemBooted, localRuntimeException);
      }
      boolean bool = this.mSystemBooted;
      if (bool) {
        return;
      }
      this.mSystemBooted = true;
      hideBootMessagesLocked();
      this.mH.sendEmptyMessageDelayed(23, 30000L);
      this.mPolicy.systemBooted();
      performEnableScreen();
      return;
    }
  }
  
  public void enableScreenIfNeeded()
  {
    synchronized (this.mWindowMap)
    {
      enableScreenIfNeededLocked();
      return;
    }
  }
  
  void enableScreenIfNeededLocked()
  {
    if (WindowManagerDebugConfig.DEBUG_BOOT)
    {
      RuntimeException localRuntimeException = new RuntimeException("here");
      localRuntimeException.fillInStackTrace();
      Slog.i("WindowManager", "enableScreenIfNeededLocked: mDisplayEnabled=" + this.mDisplayEnabled + " mForceDisplayEnabled=" + this.mForceDisplayEnabled + " mShowingBootMessages=" + this.mShowingBootMessages + " mSystemBooted=" + this.mSystemBooted, localRuntimeException);
    }
    if (this.mDisplayEnabled) {
      return;
    }
    if ((this.mSystemBooted) || (this.mShowingBootMessages))
    {
      this.mH.sendEmptyMessage(16);
      return;
    }
  }
  
  public void endProlongedAnimations()
  {
    synchronized (this.mWindowMap)
    {
      Iterator localIterator = this.mWindowMap.values().iterator();
      while (localIterator.hasNext())
      {
        AppWindowToken localAppWindowToken = ((WindowState)localIterator.next()).mAppToken;
        if ((localAppWindowToken != null) && (localAppWindowToken.mAppAnimator != null)) {
          localAppWindowToken.mAppAnimator.endProlongedAnimation();
        }
      }
    }
    this.mAppTransition.notifyProlongedAnimationsEnded();
  }
  
  public void executeAppTransition()
  {
    if (!checkCallingPermission("android.permission.MANAGE_APP_TOKENS", "executeAppTransition()")) {
      throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
    }
    synchronized (this.mWindowMap)
    {
      if (WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS) {
        Slog.w(TAG, "Execute app transition: " + this.mAppTransition + " Callers=" + Debug.getCallers(3));
      }
      long l;
      if (this.mAppTransition.isTransitionSet())
      {
        this.mAppTransition.setReady();
        l = Binder.clearCallingIdentity();
      }
      try
      {
        this.mWindowPlacerLocked.performSurfacePlacement();
        Binder.restoreCallingIdentity(l);
        return;
      }
      finally
      {
        localObject1 = finally;
        Binder.restoreCallingIdentity(l);
        throw ((Throwable)localObject1);
      }
    }
  }
  
  public void exitKeyguardSecurely(final IOnKeyguardExitResult paramIOnKeyguardExitResult)
  {
    if (this.mContext.checkCallingOrSelfPermission("android.permission.DISABLE_KEYGUARD") != 0) {
      throw new SecurityException("Requires DISABLE_KEYGUARD permission");
    }
    if (paramIOnKeyguardExitResult == null) {
      throw new IllegalArgumentException("callback == null");
    }
    this.mPolicy.exitKeyguardSecurely(new WindowManagerPolicy.OnKeyguardExitResult()
    {
      public void onKeyguardExitResult(boolean paramAnonymousBoolean)
      {
        try
        {
          paramIOnKeyguardExitResult.onKeyguardExitResult(paramAnonymousBoolean);
          return;
        }
        catch (RemoteException localRemoteException) {}
      }
    });
  }
  
  AppWindowToken findAppWindowToken(IBinder paramIBinder)
  {
    paramIBinder = (WindowToken)this.mTokenMap.get(paramIBinder);
    if (paramIBinder == null) {
      return null;
    }
    return paramIBinder.appWindowToken;
  }
  
  int findDesiredInputMethodWindowIndexLocked(boolean paramBoolean)
  {
    WindowList localWindowList1 = getDefaultWindowListLocked();
    Object localObject2 = null;
    int i = localWindowList1.size() - 1;
    int k;
    Object localObject3;
    Object localObject4;
    WindowState localWindowState2;
    for (;;)
    {
      k = i;
      localObject1 = localObject2;
      if (i >= 0)
      {
        localObject3 = (WindowState)localWindowList1.get(i);
        if ((WindowManagerDebugConfig.DEBUG_INPUT_METHOD) && (paramBoolean)) {
          Slog.i("WindowManager", "Checking window @" + i + " " + localObject3 + " fl=0x" + Integer.toHexString(((WindowState)localObject3).mAttrs.flags));
        }
        if (!canBeImeTarget((WindowState)localObject3)) {
          break label332;
        }
        localObject2 = localObject3;
        k = i;
        localObject1 = localObject2;
        if (!paramBoolean)
        {
          k = i;
          localObject1 = localObject2;
          if (((WindowState)localObject3).mAttrs.type == 3)
          {
            k = i;
            localObject1 = localObject2;
            if (i > 0)
            {
              localObject4 = (WindowState)localWindowList1.get(i - 1);
              k = i;
              localObject1 = localObject2;
              if (((WindowState)localObject4).mAppToken == ((WindowState)localObject3).mAppToken)
              {
                k = i;
                localObject1 = localObject2;
                if (canBeImeTarget((WindowState)localObject4))
                {
                  k = i - 1;
                  localObject1 = localObject4;
                }
              }
            }
          }
        }
      }
      if ((WindowManagerDebugConfig.DEBUG_INPUT_METHOD) && (paramBoolean)) {
        Slog.v("WindowManager", "Proposed new IME target: " + localObject1);
      }
      localWindowState2 = this.mInputMethodTarget;
      if ((localWindowState2 == null) || (!localWindowState2.isDisplayedLw()) || (!localWindowState2.isClosing()) || ((localObject1 != null) && (localWindowState2.mWinAnimator.mAnimLayer <= ((WindowState)localObject1).mWinAnimator.mAnimLayer))) {
        break;
      }
      if (WindowManagerDebugConfig.DEBUG_INPUT_METHOD) {
        Slog.v("WindowManager", "Current target higher, not changing");
      }
      return localWindowList1.indexOf(localWindowState2) + 1;
      label332:
      i -= 1;
    }
    if (WindowManagerDebugConfig.DEBUG_INPUT_METHOD) {
      Slog.v("WindowManager", "Desired input method target=" + localObject1 + " willMove=" + paramBoolean);
    }
    if ((paramBoolean) && (localObject1 != null))
    {
      int m;
      int j;
      WindowList localWindowList2;
      if (localWindowState2 == null)
      {
        localObject3 = null;
        if (localObject3 == null) {
          break label738;
        }
        localObject4 = null;
        localObject2 = null;
        m = 0;
        j = 0;
        if ((((AppWindowToken)localObject3).mAppAnimator.animating) || (((AppWindowToken)localObject3).mAppAnimator.animation != null))
        {
          localWindowList2 = localWindowState2.getWindowList();
          i = localWindowList2.indexOf(localWindowState2);
        }
      }
      for (;;)
      {
        m = j;
        localObject4 = localObject2;
        WindowState localWindowState1;
        if (i >= 0)
        {
          localWindowState1 = (WindowState)localWindowList2.get(i);
          if (localWindowState1.mAppToken != localObject3)
          {
            localObject4 = localObject2;
            m = j;
          }
        }
        else
        {
          if (localObject4 == null) {
            break label738;
          }
          if (WindowManagerDebugConfig.DEBUG_INPUT_METHOD) {
            Slog.v("WindowManager", this.mAppTransition + " " + localObject4 + " animating=" + ((WindowState)localObject4).mWinAnimator.isAnimationSet() + " layer=" + ((WindowState)localObject4).mWinAnimator.mAnimLayer + " new layer=" + ((WindowState)localObject1).mWinAnimator.mAnimLayer);
          }
          if (!this.mAppTransition.isTransitionSet()) {
            break label692;
          }
          this.mInputMethodTargetWaitingAnim = true;
          this.mInputMethodTarget = ((WindowState)localObject4);
          return m + 1;
          localObject3 = localWindowState2.mAppToken;
          break;
        }
        m = j;
        localObject4 = localObject2;
        if (!localWindowState1.mRemoved) {
          if (localObject2 != null)
          {
            m = j;
            localObject4 = localObject2;
            if (localWindowState1.mWinAnimator.mAnimLayer <= ((WindowState)localObject2).mWinAnimator.mAnimLayer) {}
          }
          else
          {
            localObject4 = localWindowState1;
            m = i;
          }
        }
        i -= 1;
        j = m;
        localObject2 = localObject4;
      }
      label692:
      if ((((WindowState)localObject4).mWinAnimator.isAnimationSet()) && (((WindowState)localObject4).mWinAnimator.mAnimLayer > ((WindowState)localObject1).mWinAnimator.mAnimLayer))
      {
        this.mInputMethodTargetWaitingAnim = true;
        this.mInputMethodTarget = ((WindowState)localObject4);
        return m + 1;
      }
    }
    label738:
    if (localObject1 != null)
    {
      if (paramBoolean)
      {
        if (WindowManagerDebugConfig.DEBUG_INPUT_METHOD)
        {
          localObject3 = new StringBuilder().append("Moving IM target from ").append(localWindowState2).append(" to ").append(localObject1);
          if (!WindowManagerDebugConfig.SHOW_STACK_CRAWLS) {
            break label917;
          }
          localObject2 = " Callers=" + Debug.getCallers(4);
          Slog.w("WindowManager", (String)localObject2);
        }
        this.mInputMethodTarget = ((WindowState)localObject1);
        this.mInputMethodTargetWaitingAnim = false;
        if (((WindowState)localObject1).mAppToken == null) {
          break label925;
        }
        this.mLayersController.setInputMethodAnimLayerAdjustment(((WindowState)localObject1).mAppToken.mAppAnimator.animLayerAdjustment);
      }
      for (;;)
      {
        localObject1 = ((WindowState)localObject1).mDisplayContent.mDividerControllerLocked.getWindow();
        if ((localObject1 == null) || (!((WindowState)localObject1).isVisibleLw())) {
          break label936;
        }
        i = localWindowList1.indexOf(localObject1);
        if ((i <= 0) || (i <= k)) {
          break label936;
        }
        return i + 1;
        label917:
        localObject2 = "";
        break;
        label925:
        this.mLayersController.setInputMethodAnimLayerAdjustment(0);
      }
      label936:
      return k + 1;
    }
    if (paramBoolean) {
      if (WindowManagerDebugConfig.DEBUG_INPUT_METHOD)
      {
        localObject2 = new StringBuilder().append("Moving IM target from ").append(localWindowState2).append(" to null.");
        if (!WindowManagerDebugConfig.SHOW_STACK_CRAWLS) {
          break label1040;
        }
      }
    }
    label1040:
    for (Object localObject1 = " Callers=" + Debug.getCallers(4);; localObject1 = "")
    {
      Slog.w("WindowManager", (String)localObject1);
      this.mInputMethodTarget = null;
      this.mLayersController.setInputMethodAnimLayerAdjustment(0);
      return -1;
    }
  }
  
  WindowState findFocusedWindowLocked(DisplayContent paramDisplayContent)
  {
    Object localObject2 = paramDisplayContent.getWindowList();
    int i = ((WindowList)localObject2).size() - 1;
    if (i >= 0)
    {
      Object localObject3 = (WindowState)((WindowList)localObject2).get(i);
      if ((localLOGV) || (WindowManagerDebugConfig.DEBUG_FOCUS)) {
        Slog.v("WindowManager", "Looking for focus: " + i + " = " + localObject3 + ", flags=" + ((WindowState)localObject3).mAttrs.flags + ", canReceive=" + ((WindowState)localObject3).canReceiveKeys());
      }
      if (!((WindowState)localObject3).canReceiveKeys()) {
        if ((OnePlusProcessManager.isSupportFrozenApp()) && (((WindowState)localObject3).mHasSurface) && (!((WindowState)localObject3).mAnimatingExit)) {}
      }
      label356:
      do
      {
        for (;;)
        {
          i -= 1;
          break;
          if ((((WindowState)localObject3).mOwnerUid >= 10000) && ((this.mCurrentFocus == null) || (this.mCurrentFocus.mOwnerUid != ((WindowState)localObject3).mOwnerUid)))
          {
            if ((((WindowState)localObject3).mFrame != null) && (((WindowState)localObject3).mFrame.width() > 40) && (((WindowState)localObject3).mFrame.height() > 40)) {}
            for (;;)
            {
              if ((((WindowState)localObject3).getOwningPackage() == null) || (((WindowState)localObject3).getOwningPackage().equals("android")) || (((WindowState)localObject3).getOwningPackage().equals("com.android.systemui")) || (((WindowState)localObject3).getOwningPackage().contains("wallpaper")) || ((((WindowState)localObject3).mAppToken != null) && ((((WindowState)localObject3).mAppToken.clientHidden) || (((WindowState)localObject3).mAppToken.hasVisible)))) {
                break label356;
              }
              this.mFontSmallWindowUids.add(Integer.valueOf(((WindowState)localObject3).mOwnerUid));
              break;
              if ((((WindowState)localObject3).mFrame == null) || (((WindowState)localObject3).mFrame.width() != 0) || (((WindowState)localObject3).mFrame.height() != 0) || (((WindowState)localObject3).mAttrs.type == 3)) {
                break;
              }
            }
          }
        }
        localObject1 = ((WindowState)localObject3).mAppToken;
        if ((localObject1 == null) || ((!((AppWindowToken)localObject1).removed) && (!((AppWindowToken)localObject1).sendingToBottom))) {
          break label459;
        }
      } while (!WindowManagerDebugConfig.DEBUG_FOCUS);
      localObject3 = new StringBuilder().append("Skipping ").append(localObject1).append(" because ");
      if (((AppWindowToken)localObject1).removed) {}
      for (Object localObject1 = "removed";; localObject1 = "sendingToBottom")
      {
        Slog.v("WindowManager", (String)localObject1);
        break;
      }
      label459:
      int j;
      if ((localObject1 != null) && (((WindowState)localObject3).mAttrs.type != 3) && (this.mFocusedApp != null))
      {
        paramDisplayContent = paramDisplayContent.getTasks();
        j = paramDisplayContent.size() - 1;
      }
      for (;;)
      {
        int k;
        if (j >= 0)
        {
          localObject2 = ((Task)paramDisplayContent.get(j)).mAppTokens;
          k = ((AppTokenList)localObject2).size() - 1;
        }
        for (;;)
        {
          AppWindowToken localAppWindowToken;
          if (k >= 0)
          {
            localAppWindowToken = (AppWindowToken)((AppTokenList)localObject2).get(k);
            if (localObject1 != localAppWindowToken) {}
          }
          else
          {
            if (k < 0) {
              break;
            }
            if (OnePlusProcessManager.isSupportFrozenApp())
            {
              if (!this.mFontSmallWindowUids.equals(this.mLastFontSmallWindowUids))
              {
                this.mLastFontSmallWindowUids.clear();
                this.mLastFontSmallWindowUids.addAll(this.mFontSmallWindowUids);
                OnePlusProcessManager.updateTouchWindowUidChange(this.mLastFontSmallWindowUids);
              }
              this.mFontSmallWindowUids.clear();
            }
            if (WindowManagerDebugConfig.DEBUG_FOCUS_LIGHT) {
              Slog.v("WindowManager", "findFocusedWindow: Found new focus @ " + i + " = " + localObject3);
            }
            return (WindowState)localObject3;
          }
          if ((this.mFocusedApp == localAppWindowToken) && (localAppWindowToken.windowsAreFocusable()))
          {
            if ((localLOGV) || (WindowManagerDebugConfig.DEBUG_FOCUS_LIGHT)) {
              Slog.v("WindowManager", "findFocusedWindow: Reached focused app=" + this.mFocusedApp);
            }
            if (this.mFocusedApp.hasWindowsAlive())
            {
              mFocusingWindow = this.mFocusedApp.findMainWindow();
              if (mFocusingWindow != null) {
                this.mFocusingActivity = mFocusingWindow.mAttrs.getTitle().toString();
              }
            }
            return null;
          }
          k -= 1;
        }
        j -= 1;
      }
    }
    if (WindowManagerDebugConfig.DEBUG_FOCUS_LIGHT) {
      Slog.v("WindowManager", "findFocusedWindow: No focusable windows.");
    }
    return null;
  }
  
  /* Error */
  public void finishDrawingWindow(Session paramSession, IWindow paramIWindow)
  {
    // Byte code:
    //   0: invokestatic 2686	android/os/Binder:clearCallingIdentity	()J
    //   3: lstore_3
    //   4: aload_0
    //   5: getfield 491	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
    //   8: astore 5
    //   10: aload 5
    //   12: monitorenter
    //   13: aload_0
    //   14: aload_1
    //   15: aload_2
    //   16: iconst_0
    //   17: invokevirtual 4252	com/android/server/wm/WindowManagerService:windowForClientLocked	(Lcom/android/server/wm/Session;Landroid/view/IWindow;Z)Lcom/android/server/wm/WindowState;
    //   20: astore_2
    //   21: getstatic 1121	com/android/server/wm/WindowManagerDebugConfig:DEBUG_ADD_REMOVE	Z
    //   24: ifeq +56 -> 80
    //   27: new 1023	java/lang/StringBuilder
    //   30: dup
    //   31: invokespecial 1024	java/lang/StringBuilder:<init>	()V
    //   34: ldc_w 4254
    //   37: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   40: aload_2
    //   41: invokevirtual 1039	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   44: ldc_w 4256
    //   47: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   50: astore 6
    //   52: aload_2
    //   53: ifnull +89 -> 142
    //   56: aload_2
    //   57: getfield 1398	com/android/server/wm/WindowState:mWinAnimator	Lcom/android/server/wm/WindowStateAnimator;
    //   60: invokevirtual 4259	com/android/server/wm/WindowStateAnimator:drawStateToString	()Ljava/lang/String;
    //   63: astore_1
    //   64: ldc_w 440
    //   67: aload 6
    //   69: aload_1
    //   70: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   73: invokevirtual 1046	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   76: invokestatic 1184	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   79: pop
    //   80: aload_2
    //   81: ifnull +53 -> 134
    //   84: aload_2
    //   85: getfield 1398	com/android/server/wm/WindowState:mWinAnimator	Lcom/android/server/wm/WindowStateAnimator;
    //   88: invokevirtual 4262	com/android/server/wm/WindowStateAnimator:finishDrawingLocked	()Z
    //   91: ifeq +43 -> 134
    //   94: aload_2
    //   95: getfield 1107	com/android/server/wm/WindowState:mAttrs	Landroid/view/WindowManager$LayoutParams;
    //   98: getfield 1378	android/view/WindowManager$LayoutParams:flags	I
    //   101: ldc_w 3157
    //   104: iand
    //   105: ifeq +18 -> 123
    //   108: aload_0
    //   109: invokevirtual 1810	com/android/server/wm/WindowManagerService:getDefaultDisplayContentLocked	()Lcom/android/server/wm/DisplayContent;
    //   112: astore_1
    //   113: aload_1
    //   114: aload_1
    //   115: getfield 3156	com/android/server/wm/DisplayContent:pendingLayoutChanges	I
    //   118: iconst_4
    //   119: ior
    //   120: putfield 3156	com/android/server/wm/DisplayContent:pendingLayoutChanges	I
    //   123: aload_2
    //   124: invokevirtual 2389	com/android/server/wm/WindowState:setDisplayLayoutNeeded	()V
    //   127: aload_0
    //   128: getfield 779	com/android/server/wm/WindowManagerService:mWindowPlacerLocked	Lcom/android/server/wm/WindowSurfacePlacer;
    //   131: invokevirtual 1899	com/android/server/wm/WindowSurfacePlacer:requestTraversal	()V
    //   134: aload 5
    //   136: monitorexit
    //   137: lload_3
    //   138: invokestatic 2729	android/os/Binder:restoreCallingIdentity	(J)V
    //   141: return
    //   142: ldc_w 4264
    //   145: astore_1
    //   146: goto -82 -> 64
    //   149: astore_1
    //   150: aload 5
    //   152: monitorexit
    //   153: aload_1
    //   154: athrow
    //   155: astore_1
    //   156: lload_3
    //   157: invokestatic 2729	android/os/Binder:restoreCallingIdentity	(J)V
    //   160: aload_1
    //   161: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	162	0	this	WindowManagerService
    //   0	162	1	paramSession	Session
    //   0	162	2	paramIWindow	IWindow
    //   3	154	3	l	long
    //   50	18	6	localStringBuilder	StringBuilder
    // Exception table:
    //   from	to	target	type
    //   13	52	149	finally
    //   56	64	149	finally
    //   64	80	149	finally
    //   84	123	149	finally
    //   123	134	149	finally
    //   4	13	155	finally
    //   134	137	155	finally
    //   150	155	155	finally
  }
  
  public void freezeRotation(int paramInt)
  {
    if (!checkCallingPermission("android.permission.SET_ORIENTATION", "freezeRotation()")) {
      throw new SecurityException("Requires SET_ORIENTATION permission");
    }
    if ((paramInt < -1) || (paramInt > 3)) {
      throw new IllegalArgumentException("Rotation argument must be -1 or a valid rotation constant.");
    }
    if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
      Slog.v("WindowManager", "freezeRotation: mRotation=" + this.mRotation);
    }
    long l = Binder.clearCallingIdentity();
    try
    {
      WindowManagerPolicy localWindowManagerPolicy = this.mPolicy;
      int i = paramInt;
      if (paramInt == -1) {
        i = this.mRotation;
      }
      localWindowManagerPolicy.setUserRotationMode(1, i);
      Binder.restoreCallingIdentity(l);
      updateRotationUnchecked(false, false);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public float getAnimationScale(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return 0.0F;
    case 0: 
      return this.mWindowAnimationScaleSetting;
    case 1: 
      return this.mTransitionAnimationScaleSetting;
    }
    return this.mAnimatorDurationScaleSetting;
  }
  
  public float[] getAnimationScales()
  {
    return new float[] { this.mWindowAnimationScaleSetting, this.mTransitionAnimationScaleSetting, this.mAnimatorDurationScaleSetting };
  }
  
  public int getAppOrientation(IApplicationToken paramIApplicationToken)
  {
    synchronized (this.mWindowMap)
    {
      paramIApplicationToken = findAppWindowToken(paramIApplicationToken.asBinder());
      if (paramIApplicationToken == null) {
        return -1;
      }
      int i = paramIApplicationToken.requestedOrientation;
      return i;
    }
  }
  
  public int getBaseDisplayDensity(int paramInt)
  {
    synchronized (this.mWindowMap)
    {
      DisplayContent localDisplayContent = getDisplayContentLocked(paramInt);
      if ((localDisplayContent != null) && (localDisplayContent.hasAccess(Binder.getCallingUid())))
      {
        paramInt = localDisplayContent.mBaseDisplayDensity;
        return paramInt;
      }
      return -1;
    }
  }
  
  public void getBaseDisplaySize(int paramInt, Point paramPoint)
  {
    synchronized (this.mWindowMap)
    {
      DisplayContent localDisplayContent = getDisplayContentLocked(paramInt);
      if ((localDisplayContent != null) && (localDisplayContent.hasAccess(Binder.getCallingUid())))
      {
        paramPoint.x = localDisplayContent.mBaseDisplayWidth;
        paramPoint.y = localDisplayContent.mBaseDisplayHeight;
      }
      return;
    }
  }
  
  public Rect getBoundsForNewConfiguration(int paramInt)
  {
    synchronized (this.mWindowMap)
    {
      TaskStack localTaskStack = (TaskStack)this.mStackIdToStack.get(paramInt);
      Rect localRect = new Rect();
      localTaskStack.getBoundsForNewConfiguration(localRect);
      return localRect;
    }
  }
  
  public int getCameraLensCoverState()
  {
    int i = this.mInputManager.getSwitchState(-1, 65280, 9);
    if (i > 0) {
      return 1;
    }
    if (i == 0) {
      return 0;
    }
    return -1;
  }
  
  public float getCurrentAnimatorScale()
  {
    synchronized (this.mWindowMap)
    {
      float f = animationScalesCheck(2);
      return f;
    }
  }
  
  public DisplayContent getDefaultDisplayContentLocked()
  {
    return getDisplayContentLocked(0);
  }
  
  public DisplayInfo getDefaultDisplayInfoLocked()
  {
    return getDefaultDisplayContentLocked().getDisplayInfo();
  }
  
  public WindowList getDefaultWindowListLocked()
  {
    return getDefaultDisplayContentLocked().getWindowList();
  }
  
  public DisplayContent getDisplayContentLocked(int paramInt)
  {
    DisplayContent localDisplayContent2 = (DisplayContent)this.mDisplayContents.get(paramInt);
    DisplayContent localDisplayContent1 = localDisplayContent2;
    if (localDisplayContent2 == null)
    {
      Display localDisplay = this.mDisplayManager.getDisplay(paramInt);
      localDisplayContent1 = localDisplayContent2;
      if (localDisplay != null) {
        localDisplayContent1 = newDisplayContentLocked(localDisplay);
      }
    }
    return localDisplayContent1;
  }
  
  public int getDockedDividerInsetsLw()
  {
    return getDefaultDisplayContentLocked().getDockedDividerController().getContentInsets();
  }
  
  public int getDockedStackSide()
  {
    synchronized (this.mWindowMap)
    {
      TaskStack localTaskStack = getDefaultDisplayContentLocked().getDockedStackVisibleForUserLocked();
      if (localTaskStack == null)
      {
        i = -1;
        return i;
      }
      int i = localTaskStack.getDockSide();
    }
  }
  
  int getDragLayerLocked()
  {
    return this.mPolicy.windowTypeToLayerLw(2016) * 10000 + 1000;
  }
  
  TaskStack getImeFocusStackLocked()
  {
    Object localObject2 = null;
    Object localObject1 = localObject2;
    if (this.mFocusedApp != null)
    {
      localObject1 = localObject2;
      if (this.mFocusedApp.mTask != null) {
        localObject1 = this.mFocusedApp.mTask.mStack;
      }
    }
    return (TaskStack)localObject1;
  }
  
  public int getInitialDisplayDensity(int paramInt)
  {
    synchronized (this.mWindowMap)
    {
      DisplayContent localDisplayContent = getDisplayContentLocked(paramInt);
      if ((localDisplayContent != null) && (localDisplayContent.hasAccess(Binder.getCallingUid())))
      {
        paramInt = localDisplayContent.mInitialDisplayDensity;
        return paramInt;
      }
      return -1;
    }
  }
  
  public void getInitialDisplaySize(int paramInt, Point paramPoint)
  {
    synchronized (this.mWindowMap)
    {
      DisplayContent localDisplayContent = getDisplayContentLocked(paramInt);
      if ((localDisplayContent != null) && (localDisplayContent.hasAccess(Binder.getCallingUid())))
      {
        paramPoint.x = localDisplayContent.mInitialDisplayWidth;
        paramPoint.y = localDisplayContent.mInitialDisplayHeight;
      }
      return;
    }
  }
  
  public InputMonitor getInputMonitor()
  {
    return this.mInputMonitor;
  }
  
  public int getLidState()
  {
    int i = this.mInputManager.getSwitchState(-1, 65280, 0);
    if (i > 0) {
      return 0;
    }
    if (i == 0) {
      return 1;
    }
    return -1;
  }
  
  public int getOrientationLocked()
  {
    Object localObject1 = null;
    if (this.mDisplayFrozen)
    {
      if (this.mLastWindowForcedOrientation != -1)
      {
        if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
          Slog.v("WindowManager", "Display is frozen, return " + this.mLastWindowForcedOrientation);
        }
        return this.mLastWindowForcedOrientation;
      }
      if (this.mPolicy.isKeyguardLocked())
      {
        if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
          Slog.v("WindowManager", "Display is frozen while keyguard locked, return " + this.mLastOrientation);
        }
        return this.mLastOrientation;
      }
    }
    else
    {
      Object localObject2 = getDefaultWindowListLocked();
      int i = ((WindowList)localObject2).size() - 1;
      WindowState localWindowState;
      if (i >= 0)
      {
        localWindowState = (WindowState)((WindowList)localObject2).get(i);
        if (localWindowState.mAppToken == null) {}
      }
      else
      {
        this.mLastWindowForcedOrientation = -1;
        if (!this.mPolicy.isKeyguardLocked()) {
          break label395;
        }
        localObject2 = (WindowState)this.mPolicy.getWinShowWhenLockedLw();
        if (localObject2 != null) {
          break label365;
        }
      }
      for (;;)
      {
        if (localObject1 == null) {
          break label374;
        }
        int j = ((AppWindowToken)localObject1).requestedOrientation;
        i = j;
        if (j == 3) {
          i = this.mLastKeyguardForcedOrientation;
        }
        if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
          Slog.v("WindowManager", "Done at " + localObject1 + " -- show when locked, return " + i);
        }
        return i;
        if ((localWindowState.isVisibleLw()) && (localWindowState.mPolicyVisibilityAfterAnim))
        {
          j = localWindowState.mAttrs.screenOrientation;
          if ((j != -1) && (j != 3)) {}
        }
        else
        {
          i -= 1;
          break;
        }
        if ((WindowManagerDebugConfig.DEBUG_ORIENTATION) || (WindowManagerDebugConfig.DEBUG_ONEPLUS)) {
          Slog.v(TAG, localWindowState + " forcing orientation to " + j);
        }
        if (this.mPolicy.isKeyguardHostWindow(localWindowState.mAttrs)) {
          this.mLastKeyguardForcedOrientation = j;
        }
        this.mLastWindowForcedOrientation = j;
        return j;
        label365:
        localObject1 = ((WindowState)localObject2).mAppToken;
      }
      label374:
      if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
        Slog.v("WindowManager", "No one is requesting an orientation when the screen is locked");
      }
      return this.mLastKeyguardForcedOrientation;
    }
    label395:
    return getAppSpecifiedOrientation();
  }
  
  public int getPendingAppTransition()
  {
    return this.mAppTransition.getAppTransition();
  }
  
  public int getPreferredOptionsPanelGravity()
  {
    synchronized (this.mWindowMap)
    {
      int i = getRotation();
      DisplayContent localDisplayContent = getDefaultDisplayContentLocked();
      int j = localDisplayContent.mInitialDisplayWidth;
      int k = localDisplayContent.mInitialDisplayHeight;
      if (j < k)
      {
        switch (i)
        {
        case 0: 
        default: 
          return 81;
        case 1: 
          return 85;
        case 2: 
          return 81;
        }
        return 8388691;
      }
      switch (i)
      {
      case 0: 
      default: 
        return 85;
      case 1: 
        return 81;
      case 2: 
        return 8388691;
      }
      return 81;
    }
  }
  
  public int getRotation()
  {
    return this.mRotation;
  }
  
  public int getSmallestWidthForTaskBounds(Rect paramRect)
  {
    synchronized (this.mWindowMap)
    {
      int i = getDefaultDisplayContentLocked().getDockedDividerController().getSmallestWidthDpForBounds(paramRect);
      return i;
    }
  }
  
  public void getStableInsets(Rect paramRect)
    throws RemoteException
  {
    synchronized (this.mWindowMap)
    {
      getStableInsetsLocked(paramRect);
      return;
    }
  }
  
  void getStableInsetsLocked(Rect paramRect)
  {
    DisplayInfo localDisplayInfo = getDefaultDisplayInfoLocked();
    this.mPolicy.getStableInsetsLw(localDisplayInfo.rotation, localDisplayInfo.logicalWidth, localDisplayInfo.logicalHeight, paramRect);
  }
  
  public void getStackBounds(int paramInt, Rect paramRect)
  {
    synchronized (this.mWindowMap)
    {
      TaskStack localTaskStack = (TaskStack)this.mStackIdToStack.get(paramInt);
      if (localTaskStack != null)
      {
        localTaskStack.getBounds(paramRect);
        return;
      }
      paramRect.setEmpty();
      return;
    }
  }
  
  public void getStackDockedModeBounds(int paramInt, Rect paramRect, boolean paramBoolean)
  {
    synchronized (this.mWindowMap)
    {
      TaskStack localTaskStack = (TaskStack)this.mStackIdToStack.get(paramInt);
      if (localTaskStack != null)
      {
        localTaskStack.getStackDockedModeBoundsLocked(paramRect, paramBoolean);
        return;
      }
      paramRect.setEmpty();
      return;
    }
  }
  
  public void getTaskBounds(int paramInt, Rect paramRect)
  {
    synchronized (this.mWindowMap)
    {
      Task localTask = (Task)this.mTaskIdToTask.get(paramInt);
      if (localTask != null)
      {
        localTask.getBounds(paramRect);
        return;
      }
      paramRect.setEmpty();
      return;
    }
  }
  
  public float getTransitionAnimationScaleLocked()
  {
    return animationScalesCheck(1);
  }
  
  public List<Integer> getVisibleWindowUids()
  {
    synchronized (this.mWindowMap)
    {
      ArrayList localArrayList = new ArrayList();
      int k = this.mDisplayContents.size();
      int i = 0;
      while (i < k)
      {
        WindowList localWindowList = ((DisplayContent)this.mDisplayContents.valueAt(i)).getWindowList();
        int j = localWindowList.size() - 1;
        while (j >= 0)
        {
          WindowState localWindowState = (WindowState)localWindowList.get(j);
          if ((localWindowState.mWinAnimator.mSurfaceController != null) && (localWindowState.mWinAnimator.mSurfaceController.getShown()) && (localWindowState.mOwnerUid >= 10000)) {
            localArrayList.add(Integer.valueOf(localWindowState.mOwnerUid));
          }
          j -= 1;
        }
        i += 1;
      }
      return localArrayList;
    }
  }
  
  public float getWindowAnimationScaleLocked()
  {
    return animationScalesCheck(0);
  }
  
  public WindowContentFrameStats getWindowContentFrameStats(IBinder paramIBinder)
  {
    if (!checkCallingPermission("android.permission.FRAME_STATS", "getWindowContentFrameStats()")) {
      throw new SecurityException("Requires FRAME_STATS permission");
    }
    synchronized (this.mWindowMap)
    {
      paramIBinder = (WindowState)this.mWindowMap.get(paramIBinder);
      if (paramIBinder == null) {
        return null;
      }
      paramIBinder = paramIBinder.mWinAnimator.mSurfaceController;
      if (paramIBinder == null) {
        return null;
      }
      if (this.mTempWindowRenderStats == null) {
        this.mTempWindowRenderStats = new WindowContentFrameStats();
      }
      WindowContentFrameStats localWindowContentFrameStats = this.mTempWindowRenderStats;
      boolean bool = paramIBinder.getWindowContentFrameStats(localWindowContentFrameStats);
      if (!bool) {
        return null;
      }
      return localWindowContentFrameStats;
    }
  }
  
  public void getWindowDisplayFrame(Session paramSession, IWindow paramIWindow, Rect paramRect)
  {
    synchronized (this.mWindowMap)
    {
      paramSession = windowForClientLocked(paramSession, paramIWindow, false);
      if (paramSession == null)
      {
        paramRect.setEmpty();
        return;
      }
      paramRect.set(paramSession.mDisplayFrame);
      return;
    }
  }
  
  public IWindowId getWindowId(IBinder paramIBinder)
  {
    Object localObject = null;
    synchronized (this.mWindowMap)
    {
      WindowState localWindowState = (WindowState)this.mWindowMap.get(paramIBinder);
      paramIBinder = (IBinder)localObject;
      if (localWindowState != null) {
        paramIBinder = localWindowState.mWindowId;
      }
      return paramIBinder;
    }
  }
  
  public WindowList getWindowListLocked(int paramInt)
  {
    WindowList localWindowList = null;
    DisplayContent localDisplayContent = getDisplayContentLocked(paramInt);
    if (localDisplayContent != null) {
      localWindowList = localDisplayContent.getWindowList();
    }
    return localWindowList;
  }
  
  public WindowList getWindowListLocked(Display paramDisplay)
  {
    return getWindowListLocked(paramDisplay.getDisplayId());
  }
  
  public Object getWindowManagerLock()
  {
    return this.mWindowMap;
  }
  
  int handleAnimatingStoppedAndTransitionLocked()
  {
    this.mAppTransition.setIdle();
    int i = this.mNoAnimationNotifyOnTransitionFinished.size() - 1;
    while (i >= 0)
    {
      localObject = (IBinder)this.mNoAnimationNotifyOnTransitionFinished.get(i);
      this.mAppTransition.notifyAppTransitionFinishedLocked((IBinder)localObject);
      i -= 1;
    }
    this.mNoAnimationNotifyOnTransitionFinished.clear();
    this.mWallpaperControllerLocked.hideDeferredWallpapersIfNeeded();
    Object localObject = getDefaultDisplayContentLocked().getStacks();
    i = ((ArrayList)localObject).size() - 1;
    while (i >= 0)
    {
      ArrayList localArrayList = ((TaskStack)((ArrayList)localObject).get(i)).getTasks();
      int j = localArrayList.size() - 1;
      while (j >= 0)
      {
        AppTokenList localAppTokenList = ((Task)localArrayList.get(j)).mAppTokens;
        int k = localAppTokenList.size() - 1;
        while (k >= 0)
        {
          ((AppWindowToken)localAppTokenList.get(k)).sendingToBottom = false;
          k -= 1;
        }
        j -= 1;
      }
      i -= 1;
    }
    rebuildAppWindowListLocked();
    if (WindowManagerDebugConfig.DEBUG_WALLPAPER_LIGHT) {
      Slog.v("WindowManager", "Wallpaper layer changed: assigning layers + relayout");
    }
    moveInputMethodWindowsIfNeededLocked(true);
    this.mWindowPlacerLocked.mWallpaperMayChange = true;
    this.mFocusMayChange = true;
    return 1;
  }
  
  public void handleDisplayAdded(int paramInt)
  {
    synchronized (this.mWindowMap)
    {
      Display localDisplay = this.mDisplayManager.getDisplay(paramInt);
      if (localDisplay != null)
      {
        createDisplayContentLocked(localDisplay);
        displayReady(paramInt);
      }
      this.mWindowPlacerLocked.requestTraversal();
      return;
    }
  }
  
  boolean hasDockedTasksForUser(int paramInt)
  {
    Object localObject = (TaskStack)this.mStackIdToStack.get(3);
    if (localObject == null) {
      return false;
    }
    localObject = ((TaskStack)localObject).getTasks();
    boolean bool = false;
    int i = ((ArrayList)localObject).size() - 1;
    if ((i < 0) || (bool)) {
      return bool;
    }
    if (((Task)((ArrayList)localObject).get(i)).mUserId == paramInt) {}
    for (bool = true;; bool = false)
    {
      i -= 1;
      break;
    }
  }
  
  public boolean hasNavigationBar()
  {
    return this.mPolicy.hasNavigationBar();
  }
  
  public void hideBootMessagesLocked()
  {
    if (WindowManagerDebugConfig.DEBUG_BOOT)
    {
      RuntimeException localRuntimeException = new RuntimeException("here");
      localRuntimeException.fillInStackTrace();
      Slog.i("WindowManager", "hideBootMessagesLocked: mDisplayEnabled=" + this.mDisplayEnabled + " mForceDisplayEnabled=" + this.mForceDisplayEnabled + " mShowingBootMessages=" + this.mShowingBootMessages + " mSystemBooted=" + this.mSystemBooted, localRuntimeException);
    }
    if (this.mShowingBootMessages)
    {
      this.mShowingBootMessages = false;
      this.mPolicy.hideBootMessages();
    }
  }
  
  public boolean inKeyguardRestrictedInputMode()
  {
    return this.mPolicy.inKeyguardRestrictedKeyInputMode();
  }
  
  public boolean inputMethodClientHasFocus(IInputMethodClient paramIInputMethodClient)
  {
    synchronized (this.mWindowMap)
    {
      int i = findDesiredInputMethodWindowIndexLocked(false);
      Object localObject1;
      if (i > 0)
      {
        Object localObject2 = (WindowState)getDefaultWindowListLocked().get(i - 1);
        if (WindowManagerDebugConfig.DEBUG_INPUT_METHOD)
        {
          Slog.i("WindowManager", "Desired input method target: " + localObject2);
          Slog.i("WindowManager", "Current focus: " + this.mCurrentFocus);
          Slog.i("WindowManager", "Last focus: " + this.mLastFocus);
        }
        if (localObject2 != null)
        {
          localObject1 = localObject2;
          if (((WindowState)localObject2).mAttrs.type == 3)
          {
            localObject1 = localObject2;
            if (((WindowState)localObject2).mAppToken != null) {
              i = 0;
            }
          }
          for (;;)
          {
            localObject1 = localObject2;
            if (i < ((WindowState)localObject2).mAppToken.windows.size())
            {
              localObject1 = (WindowState)((WindowState)localObject2).mAppToken.windows.get(i);
              if (localObject1 != localObject2) {
                Log.i("WindowManager", "Switching to real app window: " + localObject1);
              }
            }
            else
            {
              if (WindowManagerDebugConfig.DEBUG_INPUT_METHOD)
              {
                Slog.i("WindowManager", "IM target client: " + ((WindowState)localObject1).mSession.mClient);
                if (((WindowState)localObject1).mSession.mClient != null)
                {
                  Slog.i("WindowManager", "IM target client binder: " + ((WindowState)localObject1).mSession.mClient.asBinder());
                  Slog.i("WindowManager", "Requesting client binder: " + paramIInputMethodClient.asBinder());
                }
              }
              if (((WindowState)localObject1).mSession.mClient == null) {
                break;
              }
              localObject1 = ((WindowState)localObject1).mSession.mClient.asBinder();
              localObject2 = paramIInputMethodClient.asBinder();
              if (localObject1 != localObject2) {
                break;
              }
              return true;
            }
            i += 1;
          }
        }
      }
      if ((this.mCurrentFocus != null) && (this.mCurrentFocus.mSession.mClient != null))
      {
        localObject1 = this.mCurrentFocus.mSession.mClient.asBinder();
        paramIInputMethodClient = paramIInputMethodClient.asBinder();
        if (localObject1 == paramIInputMethodClient) {
          return true;
        }
      }
      return false;
    }
  }
  
  boolean isCurrentProfileLocked(int paramInt)
  {
    if (paramInt == this.mCurrentUserId) {
      return true;
    }
    int i = 0;
    while (i < this.mCurrentProfileIds.length)
    {
      if (this.mCurrentProfileIds[i] == paramInt) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public boolean isKeyguardLocked()
  {
    return this.mPolicy.isKeyguardLocked();
  }
  
  public boolean isKeyguardSecure()
  {
    int i = UserHandle.getCallingUserId();
    long l = Binder.clearCallingIdentity();
    try
    {
      boolean bool = this.mPolicy.isKeyguardSecure(i);
      return bool;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public boolean isRotationFrozen()
  {
    return this.mPolicy.getUserRotationMode() == 1;
  }
  
  public boolean isSafeModeEnabled()
  {
    return this.mSafeMode;
  }
  
  boolean isScreenCaptureDisabledLocked(int paramInt)
  {
    Boolean localBoolean = (Boolean)this.mScreenCaptureDisabled.get(paramInt);
    if (localBoolean == null) {
      return false;
    }
    return localBoolean.booleanValue();
  }
  
  boolean isSecureLocked(WindowState paramWindowState)
  {
    if ((paramWindowState.mAttrs.flags & 0x2000) != 0) {
      return true;
    }
    return isScreenCaptureDisabledLocked(UserHandle.getUserId(paramWindowState.mOwnerUid));
  }
  
  boolean isStackVisibleLocked(int paramInt)
  {
    TaskStack localTaskStack = (TaskStack)this.mStackIdToStack.get(paramInt);
    if (localTaskStack != null) {
      return localTaskStack.isVisibleLocked();
    }
    return false;
  }
  
  public boolean isValidTaskId(int paramInt)
  {
    synchronized (this.mWindowMap)
    {
      Object localObject1 = this.mTaskIdToTask.get(paramInt);
      if (localObject1 != null)
      {
        bool = true;
        return bool;
      }
      boolean bool = false;
    }
  }
  
  public boolean isViewServerRunning()
  {
    boolean bool = false;
    if (isSystemSecure()) {
      return false;
    }
    if (!checkCallingPermission("android.permission.DUMP", "isViewServerRunning")) {
      return false;
    }
    if (this.mViewServer != null) {
      bool = this.mViewServer.isRunning();
    }
    return bool;
  }
  
  public void keyguardGoingAway(int paramInt)
  {
    if (this.mContext.checkCallingOrSelfPermission("android.permission.DISABLE_KEYGUARD") != 0) {
      throw new SecurityException("Requires DISABLE_KEYGUARD permission");
    }
    if (WindowManagerDebugConfig.DEBUG_KEYGUARD) {
      Slog.d("WindowManager", "keyguardGoingAway: flags=0x" + Integer.toHexString(paramInt));
    }
    synchronized (this.mWindowMap)
    {
      this.mAnimator.mKeyguardGoingAway = true;
      this.mAnimator.mKeyguardGoingAwayFlags = paramInt;
      this.mWindowPlacerLocked.requestTraversal();
      return;
    }
  }
  
  public void keyguardWaitingForActivityDrawn()
  {
    if (WindowManagerDebugConfig.DEBUG_KEYGUARD) {
      Slog.d("WindowManager", "keyguardWaitingForActivityDrawn");
    }
    synchronized (this.mWindowMap)
    {
      this.mKeyguardWaitingForActivityDrawn = true;
      return;
    }
  }
  
  public void lockDeviceNow()
  {
    lockNow(null);
  }
  
  public void lockNow(Bundle paramBundle)
  {
    this.mPolicy.lockNow(paramBundle);
  }
  
  void logWindowList(WindowList paramWindowList, String paramString)
  {
    int i = paramWindowList.size();
    while (i > 0)
    {
      i -= 1;
      Slog.v("WindowManager", paramString + "#" + i + ": " + paramWindowList.get(i));
    }
  }
  
  protected void logoutTagConfigHelp(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("********************** Help begin:**********************");
    paramPrintWriter.println("1 Window add or remove:DEBUG_ADD_REMOVE | DEBUG_FOCUS | DEBUG_STARTING_WINDOW | DEBUG_WINDOW_MOVEMENT | DEBUG_FOCUS_LIGHT | DEBUG_TASK_MOVEMENT | DEBUG_STACK");
    paramPrintWriter.println("cmd: dumpsys window log window 0/1");
    paramPrintWriter.println("----------------------------------");
    paramPrintWriter.println("2 Window fresh: DEBUG_LAYOUT | DEBUG_RESIZE | DEBUG_VISIBILITY");
    paramPrintWriter.println("cmd: dumpsys window log fresh 0/1");
    paramPrintWriter.println("----------------------------------");
    paramPrintWriter.println("3 Animation:DEBUG_ANIM");
    paramPrintWriter.println("cmd: dumpsys window log anim 0/1");
    paramPrintWriter.println("----------------------------------");
    paramPrintWriter.println("4 Input envent:DEBUG_INPUT | DEBUG_INPUT_METHOD | DEBUG_DRAG");
    paramPrintWriter.println("cmd: dumpsys window log input 0/1");
    paramPrintWriter.println("----------------------------------");
    paramPrintWriter.println("5 Screen status change:DEBUG_SCREEN_ON | DEBUG_SCREENSHOT | DEBUG_BOOT");
    paramPrintWriter.println("cmd: dumpsys window log screen 0/1");
    paramPrintWriter.println("----------------------------------");
    paramPrintWriter.println("6 App token:DEBUG_TOKEN_MOVEMENT | DEBUG_APP_TRANSITIONS | DEBUG_APP_ORIENTATION");
    paramPrintWriter.println("cmd: dumpsys window log apptoken 0/1");
    paramPrintWriter.println("----------------------------------");
    paramPrintWriter.println("7 Wallpaper change:DEBUG_WALLPAPER | DEBUG_WALLPAPER_LIGH");
    paramPrintWriter.println("cmd: dumpsys window log wallpaper 0/1");
    paramPrintWriter.println("----------------------------------");
    paramPrintWriter.println("8 Config change:DEBUG_ORIENTATION | DEBUG_APP_ORIENTATION | DEBUG_CONFIGURATION | PROFILE_ORIENTATION");
    paramPrintWriter.println("cmd: dumpsys window log config 0/1");
    paramPrintWriter.println("----------------------------------");
    paramPrintWriter.println("9 Trace surface and window:DEBUG_SURFACE_TRACE | DEBUG_WINDOW_TRACE");
    paramPrintWriter.println("cmd: dumpsys window log trace 0/1");
    paramPrintWriter.println("----------------------------------");
    paramPrintWriter.println("10 Surface show change:SHOW_SURFACE_ALLOC | SHOW_TRANSACTIONS | SHOW_LIGHT_TRANSACTIONS");
    paramPrintWriter.println("cmd: dumpsys window log surface 0/1");
    paramPrintWriter.println("----------------------------------");
    paramPrintWriter.println("11 Layer change:DEBUG_LAYERS");
    paramPrintWriter.println("cmd: dumpsys window log layer 0/1");
    paramPrintWriter.println("----------------------------------");
    paramPrintWriter.println("12 PhoneWindowManager log:All PhoneWindowManager debug log switch");
    paramPrintWriter.println("cmd: dumpsys window log policy 0/1");
    paramPrintWriter.println("----------------------------------");
    paramPrintWriter.println("13 local log:localLOGV");
    paramPrintWriter.println("cmd: dumpsys window log local 0/1");
    paramPrintWriter.println("----------------------------------");
    paramPrintWriter.println("********************** Help end.  **********************");
  }
  
  void makeWindowFreezingScreenIfNeededLocked(WindowState paramWindowState)
  {
    if ((!okToDisplay()) && (this.mWindowsFreezingScreen != 2))
    {
      if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
        Slog.v("WindowManager", "Changing surface while display frozen: " + paramWindowState);
      }
      paramWindowState.mOrientationChanging = true;
      paramWindowState.mLastFreezeDuration = 0;
      this.mWindowPlacerLocked.mOrientationChangeComplete = false;
      if (this.mWindowsFreezingScreen == 0)
      {
        this.mWindowsFreezingScreen = 1;
        this.mH.removeMessages(11);
        this.mH.sendEmptyMessageDelayed(11, 2000L);
      }
    }
  }
  
  void markForSeamlessRotation(WindowState paramWindowState, boolean paramBoolean)
  {
    if (paramBoolean == paramWindowState.mSeamlesslyRotated) {
      return;
    }
    paramWindowState.mSeamlesslyRotated = paramBoolean;
    if (paramBoolean) {}
    for (this.mSeamlessRotationCount += 1;; this.mSeamlessRotationCount -= 1)
    {
      if (this.mSeamlessRotationCount == 0)
      {
        if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
          Slog.i(TAG, "Performing post-rotate rotation after seamless rotation");
        }
        if (updateRotationUncheckedLocked(false)) {
          this.mH.sendEmptyMessage(18);
        }
      }
      return;
    }
  }
  
  public void monitor()
  {
    HashMap localHashMap = this.mWindowMap;
  }
  
  void moveInputMethodDialogsLocked(int paramInt)
  {
    ArrayList localArrayList = this.mInputMethodDialogs;
    WindowList localWindowList = getDefaultWindowListLocked();
    int j = localArrayList.size();
    if (WindowManagerDebugConfig.DEBUG_INPUT_METHOD) {
      Slog.v("WindowManager", "Removing " + j + " dialogs w/pos=" + paramInt);
    }
    int i = 0;
    while (i < j)
    {
      paramInt = tmpRemoveWindowLocked(paramInt, (WindowState)localArrayList.get(i));
      i += 1;
    }
    if (WindowManagerDebugConfig.DEBUG_INPUT_METHOD)
    {
      Slog.v("WindowManager", "Window list w/pos=" + paramInt);
      logWindowList(localWindowList, "  ");
    }
    Object localObject;
    if (paramInt >= 0)
    {
      localObject = this.mInputMethodTarget.mAppToken;
      i = paramInt;
      WindowState localWindowState;
      if (this.mInputMethodWindow != null) {
        for (;;)
        {
          i = paramInt;
          if (paramInt >= localWindowList.size()) {
            break;
          }
          localWindowState = (WindowState)localWindowList.get(paramInt);
          if (localWindowState != this.mInputMethodWindow)
          {
            i = paramInt;
            if (localWindowState.mAttachedWindow != this.mInputMethodWindow) {
              break;
            }
          }
          paramInt += 1;
        }
      }
      if (WindowManagerDebugConfig.DEBUG_INPUT_METHOD) {
        Slog.v("WindowManager", "Adding " + j + " dialogs at pos=" + i);
      }
      paramInt = 0;
      while (paramInt < j)
      {
        localWindowState = (WindowState)localArrayList.get(paramInt);
        localWindowState.mTargetAppToken = ((AppWindowToken)localObject);
        i = reAddWindowLocked(i, localWindowState);
        paramInt += 1;
      }
      if (WindowManagerDebugConfig.DEBUG_INPUT_METHOD)
      {
        Slog.v("WindowManager", "Final window list:");
        logWindowList(localWindowList, "  ");
      }
      return;
    }
    paramInt = 0;
    while (paramInt < j)
    {
      localObject = (WindowState)localArrayList.get(paramInt);
      ((WindowState)localObject).mTargetAppToken = null;
      reAddWindowToListInOrderLocked((WindowState)localObject);
      if (WindowManagerDebugConfig.DEBUG_INPUT_METHOD)
      {
        Slog.v("WindowManager", "No IM target, final list:");
        logWindowList(localWindowList, "  ");
      }
      paramInt += 1;
    }
  }
  
  boolean moveInputMethodWindowsIfNeededLocked(boolean paramBoolean)
  {
    WindowState localWindowState3 = this.mInputMethodWindow;
    int j = this.mInputMethodDialogs.size();
    if ((localWindowState3 == null) && (j == 0)) {
      return false;
    }
    WindowList localWindowList = getDefaultWindowListLocked();
    int k = findDesiredInputMethodWindowIndexLocked(true);
    if (k >= 0)
    {
      int m = localWindowList.size();
      WindowState localWindowState2;
      WindowState localWindowState1;
      label78:
      int i;
      if (k < m)
      {
        localWindowState2 = (WindowState)localWindowList.get(k);
        if (localWindowState3 == null) {
          break label212;
        }
        localWindowState1 = localWindowState3;
        Object localObject = localWindowState1;
        if (localWindowState1.mChildWindows.size() > 0)
        {
          WindowState localWindowState4 = (WindowState)localWindowState1.mChildWindows.get(0);
          localObject = localWindowState1;
          if (localWindowState4.mSubLayer < 0) {
            localObject = localWindowState4;
          }
        }
        if (localWindowState2 != localObject) {
          break label242;
        }
        i = k + 1;
        label135:
        if ((i < m) && (((WindowState)localWindowList.get(i)).mIsImWindow)) {
          break label228;
        }
        i += 1;
      }
      for (;;)
      {
        if ((i >= m) || (((WindowState)localWindowList.get(i)).mIsImWindow))
        {
          if (i < m) {
            break label242;
          }
          if (localWindowState3 != null) {
            localWindowState3.mTargetAppToken = this.mInputMethodTarget.mAppToken;
          }
          return false;
          localWindowState2 = null;
          break;
          label212:
          localWindowState1 = (WindowState)this.mInputMethodDialogs.get(0);
          break label78;
          label228:
          i += 1;
          break label135;
        }
        i += 1;
      }
      label242:
      if (localWindowState3 != null)
      {
        if (WindowManagerDebugConfig.DEBUG_INPUT_METHOD)
        {
          Slog.v("WindowManager", "Moving IM from " + k);
          logWindowList(localWindowList, "  ");
        }
        i = tmpRemoveWindowLocked(k, localWindowState3);
        if (WindowManagerDebugConfig.DEBUG_INPUT_METHOD)
        {
          Slog.v("WindowManager", "List after removing with new pos " + i + ":");
          logWindowList(localWindowList, "  ");
        }
        localWindowState3.mTargetAppToken = this.mInputMethodTarget.mAppToken;
        reAddWindowLocked(i, localWindowState3);
        if (WindowManagerDebugConfig.DEBUG_INPUT_METHOD)
        {
          Slog.v("WindowManager", "List after moving IM to " + i + ":");
          logWindowList(localWindowList, "  ");
        }
        if (j > 0) {
          moveInputMethodDialogsLocked(i + 1);
        }
      }
    }
    for (;;)
    {
      if (paramBoolean) {
        this.mLayersController.assignLayersLocked(localWindowList);
      }
      return true;
      moveInputMethodDialogsLocked(k);
      continue;
      if (localWindowState3 != null)
      {
        if (WindowManagerDebugConfig.DEBUG_INPUT_METHOD) {
          Slog.v("WindowManager", "Moving IM from " + k);
        }
        tmpRemoveWindowLocked(0, localWindowState3);
        localWindowState3.mTargetAppToken = null;
        reAddWindowToListInOrderLocked(localWindowState3);
        if (WindowManagerDebugConfig.DEBUG_INPUT_METHOD)
        {
          Slog.v("WindowManager", "List with no IM target:");
          logWindowList(localWindowList, "  ");
        }
        if (j > 0) {
          moveInputMethodDialogsLocked(-1);
        }
      }
      else
      {
        moveInputMethodDialogsLocked(-1);
      }
    }
  }
  
  void moveStackWindowsLocked(DisplayContent paramDisplayContent)
  {
    WindowList localWindowList = paramDisplayContent.getWindowList();
    this.mTmpWindows.addAll(localWindowList);
    rebuildAppWindowListLocked(paramDisplayContent);
    int n = this.mTmpWindows.size();
    int i1 = localWindowList.size();
    int j = 0;
    int i = 0;
    label259:
    for (;;)
    {
      int m = j;
      int k = i;
      if (j < n)
      {
        m = j;
        k = i;
        if (i < i1)
        {
          Object localObject;
          for (k = j;; k = j)
          {
            localObject = this.mTmpWindows;
            j = k + 1;
            localObject = (WindowState)((ArrayList)localObject).get(k);
            k = i;
            if (j >= n) {
              break;
            }
            k = i;
            if (((WindowState)localObject).mAppToken == null) {
              break;
            }
            k = i;
            if (!((WindowState)localObject).mAppToken.mIsExiting) {
              break;
            }
          }
          WindowState localWindowState;
          for (;;)
          {
            i = k + 1;
            localWindowState = (WindowState)localWindowList.get(k);
            if ((i >= i1) || (localWindowState.mAppToken == null) || (!localWindowState.mAppToken.mIsExiting)) {
              break;
            }
            k = i;
          }
          if (localObject == localWindowState) {
            break label259;
          }
          paramDisplayContent.layoutNeeded = true;
          k = i;
          m = j;
        }
      }
      if (m != k) {
        paramDisplayContent.layoutNeeded = true;
      }
      this.mTmpWindows.clear();
      if (!updateFocusedWindowLocked(3, false)) {
        this.mLayersController.assignLayersLocked(paramDisplayContent.getWindowList());
      }
      this.mInputMonitor.setUpdateInputWindowsNeededLw();
      this.mWindowPlacerLocked.performSurfacePlacement();
      this.mInputMonitor.updateInputWindowsLw(false);
      return;
    }
  }
  
  /* Error */
  public void moveTaskToBottom(int paramInt)
  {
    // Byte code:
    //   0: invokestatic 2686	android/os/Binder:clearCallingIdentity	()J
    //   3: lstore_2
    //   4: aload_0
    //   5: getfield 491	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
    //   8: astore 4
    //   10: aload 4
    //   12: monitorenter
    //   13: aload_0
    //   14: getfield 683	com/android/server/wm/WindowManagerService:mTaskIdToTask	Landroid/util/SparseArray;
    //   17: iload_1
    //   18: invokevirtual 1672	android/util/SparseArray:get	(I)Ljava/lang/Object;
    //   21: checkcast 1066	com/android/server/wm/Task
    //   24: astore 5
    //   26: aload 5
    //   28: ifnonnull +44 -> 72
    //   31: ldc_w 440
    //   34: new 1023	java/lang/StringBuilder
    //   37: dup
    //   38: invokespecial 1024	java/lang/StringBuilder:<init>	()V
    //   41: ldc_w 4661
    //   44: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   47: iload_1
    //   48: invokevirtual 1128	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   51: ldc_w 4663
    //   54: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   57: invokevirtual 1046	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   60: invokestatic 2652	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   63: pop
    //   64: aload 4
    //   66: monitorexit
    //   67: lload_2
    //   68: invokestatic 2729	android/os/Binder:restoreCallingIdentity	(J)V
    //   71: return
    //   72: aload 5
    //   74: getfield 4330	com/android/server/wm/Task:mStack	Lcom/android/server/wm/TaskStack;
    //   77: astore 6
    //   79: aload 6
    //   81: aload 5
    //   83: invokevirtual 4666	com/android/server/wm/TaskStack:moveTaskToBottom	(Lcom/android/server/wm/Task;)V
    //   86: aload_0
    //   87: getfield 869	com/android/server/wm/WindowManagerService:mAppTransition	Lcom/android/server/wm/AppTransition;
    //   90: invokevirtual 2562	com/android/server/wm/AppTransition:isTransitionSet	()Z
    //   93: ifeq +9 -> 102
    //   96: aload 5
    //   98: iconst_1
    //   99: invokevirtual 4669	com/android/server/wm/Task:setSendingToBottom	(Z)V
    //   102: aload_0
    //   103: aload 6
    //   105: invokevirtual 2908	com/android/server/wm/TaskStack:getDisplayContent	()Lcom/android/server/wm/DisplayContent;
    //   108: invokevirtual 4671	com/android/server/wm/WindowManagerService:moveStackWindowsLocked	(Lcom/android/server/wm/DisplayContent;)V
    //   111: aload 4
    //   113: monitorexit
    //   114: lload_2
    //   115: invokestatic 2729	android/os/Binder:restoreCallingIdentity	(J)V
    //   118: return
    //   119: astore 5
    //   121: aload 4
    //   123: monitorexit
    //   124: aload 5
    //   126: athrow
    //   127: astore 4
    //   129: lload_2
    //   130: invokestatic 2729	android/os/Binder:restoreCallingIdentity	(J)V
    //   133: aload 4
    //   135: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	136	0	this	WindowManagerService
    //   0	136	1	paramInt	int
    //   3	127	2	l	long
    //   127	7	4	localObject1	Object
    //   24	73	5	localTask	Task
    //   119	6	5	localObject2	Object
    //   77	27	6	localTaskStack	TaskStack
    // Exception table:
    //   from	to	target	type
    //   13	26	119	finally
    //   31	64	119	finally
    //   72	102	119	finally
    //   102	111	119	finally
    //   4	13	127	finally
    //   64	67	127	finally
    //   111	114	127	finally
    //   121	127	127	finally
  }
  
  public void moveTaskToStack(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    synchronized (this.mWindowMap)
    {
      if (WindowManagerDebugConfig.DEBUG_STACK)
      {
        localObject3 = new StringBuilder().append("moveTaskToStack: moving taskId=").append(paramInt1).append(" to stackId=").append(paramInt2).append(" at ");
        if (!paramBoolean) {
          break label131;
        }
      }
      label131:
      for (Object localObject1 = "top";; localObject1 = "bottom")
      {
        Slog.i("WindowManager", (String)localObject1);
        localObject1 = (Task)this.mTaskIdToTask.get(paramInt1);
        if (localObject1 != null) {
          break;
        }
        if (WindowManagerDebugConfig.DEBUG_STACK) {
          Slog.i("WindowManager", "moveTaskToStack: could not find taskId=" + paramInt1);
        }
        return;
      }
      Object localObject3 = (TaskStack)this.mStackIdToStack.get(paramInt2);
      if (localObject3 == null)
      {
        if (WindowManagerDebugConfig.DEBUG_STACK) {
          Slog.i("WindowManager", "moveTaskToStack: could not find stackId=" + paramInt2);
        }
        return;
      }
      ((Task)localObject1).moveTaskToStack((TaskStack)localObject3, paramBoolean);
      ((TaskStack)localObject3).getDisplayContent().layoutNeeded = true;
      this.mWindowPlacerLocked.performSurfacePlacement();
      return;
    }
  }
  
  /* Error */
  public void moveTaskToTop(int paramInt)
  {
    // Byte code:
    //   0: invokestatic 2686	android/os/Binder:clearCallingIdentity	()J
    //   3: lstore_2
    //   4: aload_0
    //   5: getfield 491	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
    //   8: astore 4
    //   10: aload 4
    //   12: monitorenter
    //   13: aload_0
    //   14: getfield 683	com/android/server/wm/WindowManagerService:mTaskIdToTask	Landroid/util/SparseArray;
    //   17: iload_1
    //   18: invokevirtual 1672	android/util/SparseArray:get	(I)Ljava/lang/Object;
    //   21: checkcast 1066	com/android/server/wm/Task
    //   24: astore 5
    //   26: aload 5
    //   28: ifnonnull +11 -> 39
    //   31: aload 4
    //   33: monitorexit
    //   34: lload_2
    //   35: invokestatic 2729	android/os/Binder:restoreCallingIdentity	(J)V
    //   38: return
    //   39: aload 5
    //   41: getfield 4330	com/android/server/wm/Task:mStack	Lcom/android/server/wm/TaskStack;
    //   44: astore 6
    //   46: aload 5
    //   48: invokevirtual 4684	com/android/server/wm/Task:getDisplayContent	()Lcom/android/server/wm/DisplayContent;
    //   51: astore 7
    //   53: aload 7
    //   55: aload 6
    //   57: iconst_1
    //   58: invokevirtual 4687	com/android/server/wm/DisplayContent:moveStack	(Lcom/android/server/wm/TaskStack;Z)V
    //   61: aload 7
    //   63: getfield 3117	com/android/server/wm/DisplayContent:isDefaultDisplay	Z
    //   66: ifeq +25 -> 91
    //   69: aload 7
    //   71: invokevirtual 4690	com/android/server/wm/DisplayContent:getHomeStack	()Lcom/android/server/wm/TaskStack;
    //   74: astore 8
    //   76: aload 8
    //   78: aload 6
    //   80: if_acmpeq +11 -> 91
    //   83: aload 7
    //   85: aload 8
    //   87: iconst_0
    //   88: invokevirtual 4687	com/android/server/wm/DisplayContent:moveStack	(Lcom/android/server/wm/TaskStack;Z)V
    //   91: aload 6
    //   93: aload 5
    //   95: invokevirtual 4692	com/android/server/wm/TaskStack:moveTaskToTop	(Lcom/android/server/wm/Task;)V
    //   98: aload_0
    //   99: getfield 869	com/android/server/wm/WindowManagerService:mAppTransition	Lcom/android/server/wm/AppTransition;
    //   102: invokevirtual 2562	com/android/server/wm/AppTransition:isTransitionSet	()Z
    //   105: ifeq +9 -> 114
    //   108: aload 5
    //   110: iconst_0
    //   111: invokevirtual 4669	com/android/server/wm/Task:setSendingToBottom	(Z)V
    //   114: aload_0
    //   115: aload 7
    //   117: invokevirtual 4671	com/android/server/wm/WindowManagerService:moveStackWindowsLocked	(Lcom/android/server/wm/DisplayContent;)V
    //   120: aload 4
    //   122: monitorexit
    //   123: lload_2
    //   124: invokestatic 2729	android/os/Binder:restoreCallingIdentity	(J)V
    //   127: return
    //   128: astore 5
    //   130: aload 4
    //   132: monitorexit
    //   133: aload 5
    //   135: athrow
    //   136: astore 4
    //   138: lload_2
    //   139: invokestatic 2729	android/os/Binder:restoreCallingIdentity	(J)V
    //   142: aload 4
    //   144: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	145	0	this	WindowManagerService
    //   0	145	1	paramInt	int
    //   3	136	2	l	long
    //   136	7	4	localObject1	Object
    //   24	85	5	localTask	Task
    //   128	6	5	localObject2	Object
    //   44	48	6	localTaskStack1	TaskStack
    //   51	65	7	localDisplayContent	DisplayContent
    //   74	12	8	localTaskStack2	TaskStack
    // Exception table:
    //   from	to	target	type
    //   13	26	128	finally
    //   39	76	128	finally
    //   83	91	128	finally
    //   91	114	128	finally
    //   114	120	128	finally
    //   4	13	136	finally
    //   31	34	136	finally
    //   120	123	136	finally
    //   130	136	136	finally
  }
  
  boolean needsLayout()
  {
    int j = this.mDisplayContents.size();
    int i = 0;
    while (i < j)
    {
      if (((DisplayContent)this.mDisplayContents.valueAt(i)).layoutNeeded) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public void notifyActivityDrawnForKeyguard()
  {
    if (WindowManagerDebugConfig.DEBUG_KEYGUARD) {
      Slog.d("WindowManager", "notifyActivityDrawnForKeyguard: waiting=" + this.mKeyguardWaitingForActivityDrawn + " Callers=" + Debug.getCallers(5));
    }
    synchronized (this.mWindowMap)
    {
      if (this.mKeyguardWaitingForActivityDrawn)
      {
        this.mPolicy.notifyActivityDrawnForKeyguardLw();
        this.mKeyguardWaitingForActivityDrawn = false;
      }
      return;
    }
  }
  
  public void notifyAppRelaunchesCleared(IBinder paramIBinder)
  {
    synchronized (this.mWindowMap)
    {
      paramIBinder = findAppWindowToken(paramIBinder);
      if (paramIBinder != null) {
        paramIBinder.clearRelaunching();
      }
      return;
    }
  }
  
  public void notifyAppRelaunching(IBinder paramIBinder)
  {
    synchronized (this.mWindowMap)
    {
      paramIBinder = findAppWindowToken(paramIBinder);
      if (paramIBinder != null) {
        paramIBinder.startRelaunching();
      }
      return;
    }
  }
  
  public void notifyAppRelaunchingFinished(IBinder paramIBinder)
  {
    synchronized (this.mWindowMap)
    {
      paramIBinder = findAppWindowToken(paramIBinder);
      if (paramIBinder != null) {
        paramIBinder.finishRelaunching();
      }
      return;
    }
  }
  
  public void notifyAppResumed(IBinder paramIBinder, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (!checkCallingPermission("android.permission.MANAGE_APP_TOKENS", "notifyAppResumed()")) {
      throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
    }
    synchronized (this.mWindowMap)
    {
      AppWindowToken localAppWindowToken = findAppWindowToken(paramIBinder);
      if (localAppWindowToken == null)
      {
        Slog.w("WindowManager", "Attempted to notify resumed of non-existing app token: " + paramIBinder);
        return;
      }
      localAppWindowToken.notifyAppResumed(paramBoolean1, paramBoolean2);
      return;
    }
  }
  
  public void notifyAppStopped(IBinder paramIBinder)
  {
    if (!checkCallingPermission("android.permission.MANAGE_APP_TOKENS", "notifyAppStopped()")) {
      throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
    }
    synchronized (this.mWindowMap)
    {
      AppWindowToken localAppWindowToken = findAppWindowToken(paramIBinder);
      if (localAppWindowToken == null)
      {
        Slog.w("WindowManager", "Attempted to notify stopped of non-existing app token: " + paramIBinder);
        return;
      }
      localAppWindowToken.notifyAppStopped();
      return;
    }
  }
  
  void notifyHardKeyboardStatusChange()
  {
    synchronized (this.mWindowMap)
    {
      WindowManagerInternal.OnHardKeyboardStatusChangeListener localOnHardKeyboardStatusChangeListener = this.mHardKeyboardStatusChangeListener;
      boolean bool = this.mHardKeyboardAvailable;
      if (localOnHardKeyboardStatusChangeListener != null) {
        localOnHardKeyboardStatusChangeListener.onHardKeyboardStatusChange(bool);
      }
      return;
    }
  }
  
  boolean okToDisplay()
  {
    if ((!this.mDisplayFrozen) && (this.mDisplayEnabled)) {
      return this.mPolicy.isScreenOn();
    }
    return false;
  }
  
  public void onDisplayAdded(int paramInt)
  {
    this.mH.sendMessage(this.mH.obtainMessage(27, paramInt, 0));
  }
  
  public void onDisplayChanged(int paramInt)
  {
    this.mH.sendMessage(this.mH.obtainMessage(29, paramInt, 0));
  }
  
  public void onDisplayRemoved(int paramInt)
  {
    this.mH.sendMessage(this.mH.obtainMessage(28, paramInt, 0));
  }
  
  public void onKeyguardDone()
  {
    this.mPolicy.onKeyguardDone();
  }
  
  public void onRectangleOnScreenRequested(IBinder paramIBinder, Rect paramRect)
  {
    synchronized (this.mWindowMap)
    {
      if (this.mAccessibilityController != null)
      {
        paramIBinder = (WindowState)this.mWindowMap.get(paramIBinder);
        if ((paramIBinder != null) && (paramIBinder.getDisplayId() == 0)) {
          this.mAccessibilityController.onRectangleOnScreenRequestedLocked(paramRect);
        }
      }
      return;
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
        Slog.wtf("WindowManager", "Window Manager Crash", paramParcel1);
      }
      throw paramParcel1;
    }
  }
  
  public IWindowSession openSession(IWindowSessionCallback paramIWindowSessionCallback, IInputMethodClient paramIInputMethodClient, IInputContext paramIInputContext)
  {
    if (paramIInputMethodClient == null) {
      throw new IllegalArgumentException("null client");
    }
    if (paramIInputContext == null) {
      throw new IllegalArgumentException("null inputContext");
    }
    return new Session(this, paramIWindowSessionCallback, paramIInputMethodClient, paramIInputContext);
  }
  
  /* Error */
  public boolean outOfMemoryWindow(Session paramSession, IWindow paramIWindow)
  {
    // Byte code:
    //   0: invokestatic 2686	android/os/Binder:clearCallingIdentity	()J
    //   3: lstore_3
    //   4: aload_0
    //   5: getfield 491	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
    //   8: astore 6
    //   10: aload 6
    //   12: monitorenter
    //   13: aload_0
    //   14: aload_1
    //   15: aload_2
    //   16: iconst_0
    //   17: invokevirtual 4252	com/android/server/wm/WindowManagerService:windowForClientLocked	(Lcom/android/server/wm/Session;Landroid/view/IWindow;Z)Lcom/android/server/wm/WindowState;
    //   20: astore_1
    //   21: aload_1
    //   22: ifnonnull +12 -> 34
    //   25: aload 6
    //   27: monitorexit
    //   28: lload_3
    //   29: invokestatic 2729	android/os/Binder:restoreCallingIdentity	(J)V
    //   32: iconst_0
    //   33: ireturn
    //   34: aload_0
    //   35: aload_1
    //   36: getfield 1398	com/android/server/wm/WindowState:mWinAnimator	Lcom/android/server/wm/WindowStateAnimator;
    //   39: ldc_w 4768
    //   42: iconst_0
    //   43: invokevirtual 4772	com/android/server/wm/WindowManagerService:reclaimSomeSurfaceMemoryLocked	(Lcom/android/server/wm/WindowStateAnimator;Ljava/lang/String;Z)Z
    //   46: istore 5
    //   48: aload 6
    //   50: monitorexit
    //   51: lload_3
    //   52: invokestatic 2729	android/os/Binder:restoreCallingIdentity	(J)V
    //   55: iload 5
    //   57: ireturn
    //   58: astore_1
    //   59: aload 6
    //   61: monitorexit
    //   62: aload_1
    //   63: athrow
    //   64: astore_1
    //   65: lload_3
    //   66: invokestatic 2729	android/os/Binder:restoreCallingIdentity	(J)V
    //   69: aload_1
    //   70: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	71	0	this	WindowManagerService
    //   0	71	1	paramSession	Session
    //   0	71	2	paramIWindow	IWindow
    //   3	63	3	l	long
    //   46	10	5	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   13	21	58	finally
    //   34	48	58	finally
    //   4	13	64	finally
    //   25	28	64	finally
    //   48	51	64	finally
    //   59	64	64	finally
  }
  
  public void overridePendingAppTransition(String paramString, int paramInt1, int paramInt2, IRemoteCallback paramIRemoteCallback)
  {
    synchronized (this.mWindowMap)
    {
      this.mAppTransition.overridePendingAppTransition(paramString, paramInt1, paramInt2, paramIRemoteCallback);
      return;
    }
  }
  
  public void overridePendingAppTransitionAspectScaledThumb(Bitmap paramBitmap, int paramInt1, int paramInt2, int paramInt3, int paramInt4, IRemoteCallback paramIRemoteCallback, boolean paramBoolean)
  {
    synchronized (this.mWindowMap)
    {
      this.mAppTransition.overridePendingAppTransitionAspectScaledThumb(paramBitmap, paramInt1, paramInt2, paramInt3, paramInt4, paramIRemoteCallback, paramBoolean);
      return;
    }
  }
  
  public void overridePendingAppTransitionClipReveal(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    synchronized (this.mWindowMap)
    {
      this.mAppTransition.overridePendingAppTransitionClipReveal(paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    }
  }
  
  public void overridePendingAppTransitionInPlace(String paramString, int paramInt)
  {
    synchronized (this.mWindowMap)
    {
      this.mAppTransition.overrideInPlaceAppTransition(paramString, paramInt);
      return;
    }
  }
  
  public void overridePendingAppTransitionMultiThumb(AppTransitionAnimationSpec[] paramArrayOfAppTransitionAnimationSpec, IRemoteCallback paramIRemoteCallback1, IRemoteCallback paramIRemoteCallback2, boolean paramBoolean)
  {
    synchronized (this.mWindowMap)
    {
      this.mAppTransition.overridePendingAppTransitionMultiThumb(paramArrayOfAppTransitionAnimationSpec, paramIRemoteCallback1, paramIRemoteCallback2, paramBoolean);
      prolongAnimationsFromSpecs(paramArrayOfAppTransitionAnimationSpec, paramBoolean);
      return;
    }
  }
  
  public void overridePendingAppTransitionMultiThumbFuture(IAppTransitionAnimationSpecsFuture paramIAppTransitionAnimationSpecsFuture, IRemoteCallback paramIRemoteCallback, boolean paramBoolean)
  {
    synchronized (this.mWindowMap)
    {
      this.mAppTransition.overridePendingAppTransitionMultiThumbFuture(paramIAppTransitionAnimationSpecsFuture, paramIRemoteCallback, paramBoolean);
      return;
    }
  }
  
  public void overridePendingAppTransitionScaleUp(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    synchronized (this.mWindowMap)
    {
      this.mAppTransition.overridePendingAppTransitionScaleUp(paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    }
  }
  
  public void overridePendingAppTransitionThumb(Bitmap paramBitmap, int paramInt1, int paramInt2, IRemoteCallback paramIRemoteCallback, boolean paramBoolean)
  {
    synchronized (this.mWindowMap)
    {
      this.mAppTransition.overridePendingAppTransitionThumb(paramBitmap, paramInt1, paramInt2, paramIRemoteCallback, paramBoolean);
      return;
    }
  }
  
  public void overridePlayingAppAnimationsLw(Animation paramAnimation)
  {
    getDefaultDisplayContentLocked().overridePlayingAppAnimationsLw(paramAnimation);
  }
  
  public void pauseKeyDispatching(IBinder paramIBinder)
  {
    if (!checkCallingPermission("android.permission.MANAGE_APP_TOKENS", "pauseKeyDispatching()")) {
      throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
    }
    synchronized (this.mWindowMap)
    {
      paramIBinder = (WindowToken)this.mTokenMap.get(paramIBinder);
      if (paramIBinder != null) {
        this.mInputMonitor.pauseDispatchingLw(paramIBinder);
      }
      return;
    }
  }
  
  void pauseRotationLocked()
  {
    this.mDeferredRotationPauseCount += 1;
  }
  
  public void performBootTimeout()
  {
    synchronized (this.mWindowMap)
    {
      boolean bool = this.mDisplayEnabled;
      if (bool) {
        return;
      }
      Slog.w("WindowManager", "***** BOOT TIMEOUT: forcing display enabled");
      this.mForceDisplayEnabled = true;
      performEnableScreen();
      return;
    }
  }
  
  /* Error */
  public void performDeferredDestroyWindow(Session paramSession, IWindow paramIWindow)
  {
    // Byte code:
    //   0: invokestatic 2686	android/os/Binder:clearCallingIdentity	()J
    //   3: lstore_3
    //   4: aload_0
    //   5: getfield 491	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
    //   8: astore 6
    //   10: aload 6
    //   12: monitorenter
    //   13: aload_0
    //   14: aload_1
    //   15: aload_2
    //   16: iconst_0
    //   17: invokevirtual 4252	com/android/server/wm/WindowManagerService:windowForClientLocked	(Lcom/android/server/wm/Session;Landroid/view/IWindow;Z)Lcom/android/server/wm/WindowState;
    //   20: astore_1
    //   21: aload_1
    //   22: ifnull +14 -> 36
    //   25: aload_1
    //   26: getfield 2116	com/android/server/wm/WindowState:mWillReplaceWindow	Z
    //   29: istore 5
    //   31: iload 5
    //   33: ifeq +11 -> 44
    //   36: aload 6
    //   38: monitorexit
    //   39: lload_3
    //   40: invokestatic 2729	android/os/Binder:restoreCallingIdentity	(J)V
    //   43: return
    //   44: aload_1
    //   45: getfield 1398	com/android/server/wm/WindowState:mWinAnimator	Lcom/android/server/wm/WindowStateAnimator;
    //   48: invokevirtual 4821	com/android/server/wm/WindowStateAnimator:destroyDeferredSurfaceLocked	()V
    //   51: aload 6
    //   53: monitorexit
    //   54: lload_3
    //   55: invokestatic 2729	android/os/Binder:restoreCallingIdentity	(J)V
    //   58: return
    //   59: astore_1
    //   60: aload 6
    //   62: monitorexit
    //   63: aload_1
    //   64: athrow
    //   65: astore_1
    //   66: lload_3
    //   67: invokestatic 2729	android/os/Binder:restoreCallingIdentity	(J)V
    //   70: aload_1
    //   71: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	72	0	this	WindowManagerService
    //   0	72	1	paramSession	Session
    //   0	72	2	paramIWindow	IWindow
    //   3	64	3	l	long
    //   29	3	5	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   13	21	59	finally
    //   25	31	59	finally
    //   44	51	59	finally
    //   4	13	65	finally
    //   36	39	65	finally
    //   51	54	65	finally
    //   60	65	65	finally
  }
  
  /* Error */
  public void performEnableScreen()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 491	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
    //   4: astore_2
    //   5: aload_2
    //   6: monitorenter
    //   7: getstatic 1462	com/android/server/wm/WindowManagerDebugConfig:DEBUG_BOOT	Z
    //   10: ifeq +98 -> 108
    //   13: ldc_w 440
    //   16: new 1023	java/lang/StringBuilder
    //   19: dup
    //   20: invokespecial 1024	java/lang/StringBuilder:<init>	()V
    //   23: ldc_w 4823
    //   26: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   29: aload_0
    //   30: getfield 550	com/android/server/wm/WindowManagerService:mDisplayEnabled	Z
    //   33: invokevirtual 1306	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   36: ldc_w 4107
    //   39: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   42: aload_0
    //   43: getfield 554	com/android/server/wm/WindowManagerService:mForceDisplayEnabled	Z
    //   46: invokevirtual 1306	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   49: ldc_w 4109
    //   52: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   55: aload_0
    //   56: getfield 556	com/android/server/wm/WindowManagerService:mShowingBootMessages	Z
    //   59: invokevirtual 1306	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   62: ldc_w 4111
    //   65: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   68: aload_0
    //   69: getfield 552	com/android/server/wm/WindowManagerService:mSystemBooted	Z
    //   72: invokevirtual 1306	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   75: ldc_w 4825
    //   78: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   81: aload_0
    //   82: getfield 719	com/android/server/wm/WindowManagerService:mOnlyCore	Z
    //   85: invokevirtual 1306	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   88: invokevirtual 1046	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   91: new 1975	java/lang/RuntimeException
    //   94: dup
    //   95: ldc_w 4102
    //   98: invokespecial 4103	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   101: invokevirtual 1980	java/lang/RuntimeException:fillInStackTrace	()Ljava/lang/Throwable;
    //   104: invokestatic 1983	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   107: pop
    //   108: aload_0
    //   109: getfield 550	com/android/server/wm/WindowManagerService:mDisplayEnabled	Z
    //   112: istore_1
    //   113: iload_1
    //   114: ifeq +6 -> 120
    //   117: aload_2
    //   118: monitorexit
    //   119: return
    //   120: aload_0
    //   121: getfield 552	com/android/server/wm/WindowManagerService:mSystemBooted	Z
    //   124: ifne +10 -> 134
    //   127: aload_0
    //   128: getfield 556	com/android/server/wm/WindowManagerService:mShowingBootMessages	Z
    //   131: ifeq +22 -> 153
    //   134: aload_0
    //   135: getfield 554	com/android/server/wm/WindowManagerService:mForceDisplayEnabled	Z
    //   138: ifne +18 -> 156
    //   141: aload_0
    //   142: invokespecial 4827	com/android/server/wm/WindowManagerService:checkWaitingForWindowsLocked	()Z
    //   145: istore_1
    //   146: iload_1
    //   147: ifeq +9 -> 156
    //   150: aload_2
    //   151: monitorexit
    //   152: return
    //   153: aload_2
    //   154: monitorexit
    //   155: return
    //   156: aload_0
    //   157: getfield 558	com/android/server/wm/WindowManagerService:mBootAnimationStopped	Z
    //   160: ifne +59 -> 219
    //   163: ldc2_w 1231
    //   166: ldc_w 4829
    //   169: iconst_0
    //   170: invokestatic 4833	android/os/Trace:asyncTraceBegin	(JLjava/lang/String;I)V
    //   173: ldc_w 4835
    //   176: invokestatic 4840	android/os/ServiceManager:getService	(Ljava/lang/String;)Landroid/os/IBinder;
    //   179: astore_3
    //   180: aload_3
    //   181: ifnull +33 -> 214
    //   184: invokestatic 4846	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   187: astore 4
    //   189: aload 4
    //   191: ldc_w 4848
    //   194: invokevirtual 4851	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   197: aload_3
    //   198: iconst_1
    //   199: aload 4
    //   201: aconst_null
    //   202: iconst_0
    //   203: invokeinterface 4854 5 0
    //   208: pop
    //   209: aload 4
    //   211: invokevirtual 4857	android/os/Parcel:recycle	()V
    //   214: aload_0
    //   215: iconst_1
    //   216: putfield 558	com/android/server/wm/WindowManagerService:mBootAnimationStopped	Z
    //   219: aload_0
    //   220: getfield 554	com/android/server/wm/WindowManagerService:mForceDisplayEnabled	Z
    //   223: ifne +10 -> 233
    //   226: aload_0
    //   227: invokespecial 387	com/android/server/wm/WindowManagerService:checkBootAnimationCompleteLocked	()Z
    //   230: ifeq +113 -> 343
    //   233: sipush 31007
    //   236: invokestatic 4860	android/os/SystemClock:uptimeMillis	()J
    //   239: invokestatic 4863	android/util/EventLog:writeEvent	(IJ)I
    //   242: pop
    //   243: ldc2_w 1231
    //   246: ldc_w 4829
    //   249: iconst_0
    //   250: invokestatic 4866	android/os/Trace:asyncTraceEnd	(JLjava/lang/String;I)V
    //   253: aload_0
    //   254: iconst_1
    //   255: putfield 550	com/android/server/wm/WindowManagerService:mDisplayEnabled	Z
    //   258: getstatic 1515	com/android/server/wm/WindowManagerDebugConfig:DEBUG_SCREEN_ON	Z
    //   261: ifne +15 -> 276
    //   264: getstatic 1462	com/android/server/wm/WindowManagerDebugConfig:DEBUG_BOOT	Z
    //   267: ifne +9 -> 276
    //   270: getstatic 3298	com/android/server/wm/WindowManagerDebugConfig:DEBUG_ONEPLUS	Z
    //   273: ifeq +13 -> 286
    //   276: getstatic 372	com/android/server/wm/WindowManagerService:TAG	Ljava/lang/String;
    //   279: ldc_w 4868
    //   282: invokestatic 1392	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   285: pop
    //   286: aload_0
    //   287: getfield 706	com/android/server/wm/WindowManagerService:mInputMonitor	Lcom/android/server/wm/InputMonitor;
    //   290: aload_0
    //   291: getfield 4870	com/android/server/wm/WindowManagerService:mEventDispatchingEnabled	Z
    //   294: invokevirtual 4873	com/android/server/wm/InputMonitor:setEventDispatchingLw	(Z)V
    //   297: aload_2
    //   298: monitorexit
    //   299: aload_0
    //   300: getfield 892	com/android/server/wm/WindowManagerService:mActivityManager	Landroid/app/IActivityManager;
    //   303: invokeinterface 4876 1 0
    //   308: aload_0
    //   309: getfield 481	com/android/server/wm/WindowManagerService:mPolicy	Landroid/view/WindowManagerPolicy;
    //   312: invokeinterface 4878 1 0
    //   317: aload_0
    //   318: iconst_0
    //   319: iconst_0
    //   320: invokevirtual 4282	com/android/server/wm/WindowManagerService:updateRotationUnchecked	(ZZ)V
    //   323: return
    //   324: astore_3
    //   325: ldc_w 440
    //   328: ldc_w 4880
    //   331: invokestatic 2652	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   334: pop
    //   335: goto -121 -> 214
    //   338: astore_3
    //   339: aload_2
    //   340: monitorexit
    //   341: aload_3
    //   342: athrow
    //   343: getstatic 1462	com/android/server/wm/WindowManagerDebugConfig:DEBUG_BOOT	Z
    //   346: ifeq +13 -> 359
    //   349: ldc_w 440
    //   352: ldc_w 4882
    //   355: invokestatic 1392	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   358: pop
    //   359: aload_2
    //   360: monitorexit
    //   361: return
    //   362: astore_2
    //   363: goto -55 -> 308
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	366	0	this	WindowManagerService
    //   112	35	1	bool	boolean
    //   4	356	2	localHashMap	HashMap
    //   362	1	2	localRemoteException1	RemoteException
    //   179	19	3	localIBinder	IBinder
    //   324	1	3	localRemoteException2	RemoteException
    //   338	4	3	localObject	Object
    //   187	23	4	localParcel	Parcel
    // Exception table:
    //   from	to	target	type
    //   173	180	324	android/os/RemoteException
    //   184	214	324	android/os/RemoteException
    //   7	108	338	finally
    //   108	113	338	finally
    //   120	134	338	finally
    //   134	146	338	finally
    //   156	173	338	finally
    //   173	180	338	finally
    //   184	214	338	finally
    //   214	219	338	finally
    //   219	233	338	finally
    //   233	276	338	finally
    //   276	286	338	finally
    //   286	297	338	finally
    //   325	335	338	finally
    //   343	359	338	finally
    //   299	308	362	android/os/RemoteException
  }
  
  public void pokeDrawLock(Session paramSession, IBinder paramIBinder)
  {
    synchronized (this.mWindowMap)
    {
      paramSession = windowForClientLocked(paramSession, paramIBinder, false);
      if (paramSession != null) {
        paramSession.pokeDrawLockLw(this.mDrawLockTimeoutMillis);
      }
      return;
    }
  }
  
  public void positionTaskInStack(int paramInt1, int paramInt2, int paramInt3, Rect paramRect, Configuration paramConfiguration)
  {
    synchronized (this.mWindowMap)
    {
      if (WindowManagerDebugConfig.DEBUG_STACK) {
        Slog.i("WindowManager", "positionTaskInStack: positioning taskId=" + paramInt1 + " in stackId=" + paramInt2 + " at " + paramInt3);
      }
      Task localTask = (Task)this.mTaskIdToTask.get(paramInt1);
      if (localTask == null)
      {
        if (WindowManagerDebugConfig.DEBUG_STACK) {
          Slog.i("WindowManager", "positionTaskInStack: could not find taskId=" + paramInt1);
        }
        return;
      }
      TaskStack localTaskStack = (TaskStack)this.mStackIdToStack.get(paramInt2);
      if (localTaskStack == null)
      {
        if (WindowManagerDebugConfig.DEBUG_STACK) {
          Slog.i("WindowManager", "positionTaskInStack: could not find stackId=" + paramInt2);
        }
        return;
      }
      localTask.positionTaskInStack(localTaskStack, paramInt3, paramRect, paramConfiguration);
      localTaskStack.getDisplayContent().layoutNeeded = true;
      this.mWindowPlacerLocked.performSurfacePlacement();
      return;
    }
  }
  
  public void prepareAppTransition(int paramInt, boolean paramBoolean)
  {
    if (!checkCallingPermission("android.permission.MANAGE_APP_TOKENS", "prepareAppTransition()")) {
      throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
    }
    synchronized (this.mWindowMap)
    {
      if ((this.mAppTransition.prepareAppTransitionLocked(paramInt, paramBoolean)) && (okToDisplay())) {
        this.mSkipAppTransitionAnimation = false;
      }
      return;
    }
  }
  
  /* Error */
  IBinder prepareDragSurface(IWindow paramIWindow, SurfaceSession paramSurfaceSession, int paramInt1, int paramInt2, int paramInt3, Surface paramSurface)
  {
    // Byte code:
    //   0: getstatic 4052	com/android/server/wm/WindowManagerDebugConfig:DEBUG_DRAG	Z
    //   3: ifeq +80 -> 83
    //   6: ldc_w 440
    //   9: new 1023	java/lang/StringBuilder
    //   12: dup
    //   13: invokespecial 1024	java/lang/StringBuilder:<init>	()V
    //   16: ldc_w 4911
    //   19: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   22: iload 4
    //   24: invokevirtual 1128	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   27: ldc_w 4913
    //   30: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   33: iload 5
    //   35: invokevirtual 1128	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   38: ldc_w 4915
    //   41: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   44: iload_3
    //   45: invokestatic 3949	java/lang/Integer:toHexString	(I)Ljava/lang/String;
    //   48: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   51: ldc_w 4917
    //   54: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   57: aload_1
    //   58: invokevirtual 1039	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   61: ldc_w 4919
    //   64: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   67: aload_1
    //   68: invokeinterface 1036 1 0
    //   73: invokevirtual 1039	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   76: invokevirtual 1046	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   79: invokestatic 1184	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   82: pop
    //   83: invokestatic 1473	android/os/Binder:getCallingPid	()I
    //   86: istore 8
    //   88: invokestatic 1490	android/os/Binder:getCallingUid	()I
    //   91: istore 9
    //   93: invokestatic 2686	android/os/Binder:clearCallingIdentity	()J
    //   96: lstore 10
    //   98: aconst_null
    //   99: astore 15
    //   101: aconst_null
    //   102: astore 14
    //   104: aload_0
    //   105: getfield 491	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
    //   108: astore 16
    //   110: aload 16
    //   112: monitorenter
    //   113: aload 14
    //   115: astore 12
    //   117: aload 15
    //   119: astore 13
    //   121: aload_0
    //   122: getfield 681	com/android/server/wm/WindowManagerService:mDragState	Lcom/android/server/wm/DragState;
    //   125: ifnonnull +325 -> 450
    //   128: aload 14
    //   130: astore 12
    //   132: aload 15
    //   134: astore 13
    //   136: aload_0
    //   137: invokevirtual 1810	com/android/server/wm/WindowManagerService:getDefaultDisplayContentLocked	()Lcom/android/server/wm/DisplayContent;
    //   140: invokevirtual 1583	com/android/server/wm/DisplayContent:getDisplay	()Landroid/view/Display;
    //   143: astore 18
    //   145: aload 14
    //   147: astore 12
    //   149: aload 15
    //   151: astore 13
    //   153: new 973	android/view/SurfaceControl
    //   156: dup
    //   157: aload_2
    //   158: ldc_w 4921
    //   161: iload 4
    //   163: iload 5
    //   165: bipush -3
    //   167: iconst_4
    //   168: invokespecial 4924	android/view/SurfaceControl:<init>	(Landroid/view/SurfaceSession;Ljava/lang/String;IIII)V
    //   171: astore 17
    //   173: aload 14
    //   175: astore 12
    //   177: aload 15
    //   179: astore 13
    //   181: aload 17
    //   183: aload 18
    //   185: invokevirtual 4927	android/view/Display:getLayerStack	()I
    //   188: invokevirtual 4930	android/view/SurfaceControl:setLayerStack	(I)V
    //   191: fconst_1
    //   192: fstore 7
    //   194: iload_3
    //   195: sipush 512
    //   198: iand
    //   199: ifne +7 -> 206
    //   202: ldc 82
    //   204: fstore 7
    //   206: aload 14
    //   208: astore 12
    //   210: aload 15
    //   212: astore 13
    //   214: aload 17
    //   216: fload 7
    //   218: invokevirtual 4933	android/view/SurfaceControl:setAlpha	(F)V
    //   221: aload 14
    //   223: astore 12
    //   225: aload 15
    //   227: astore 13
    //   229: getstatic 1647	com/android/server/wm/WindowManagerDebugConfig:SHOW_TRANSACTIONS	Z
    //   232: ifeq +45 -> 277
    //   235: aload 14
    //   237: astore 12
    //   239: aload 15
    //   241: astore 13
    //   243: ldc_w 440
    //   246: new 1023	java/lang/StringBuilder
    //   249: dup
    //   250: invokespecial 1024	java/lang/StringBuilder:<init>	()V
    //   253: ldc_w 4935
    //   256: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   259: aload 17
    //   261: invokevirtual 1039	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   264: ldc_w 4937
    //   267: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   270: invokevirtual 1046	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   273: invokestatic 1392	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   276: pop
    //   277: aload 14
    //   279: astore 12
    //   281: aload 15
    //   283: astore 13
    //   285: aload 6
    //   287: aload 17
    //   289: invokevirtual 4941	android/view/Surface:copyFrom	(Landroid/view/SurfaceControl;)V
    //   292: aload 14
    //   294: astore 12
    //   296: aload 15
    //   298: astore 13
    //   300: aload_1
    //   301: invokeinterface 1036 1 0
    //   306: astore 6
    //   308: aload 14
    //   310: astore 12
    //   312: aload 15
    //   314: astore 13
    //   316: new 1470	android/os/Binder
    //   319: dup
    //   320: invokespecial 4942	android/os/Binder:<init>	()V
    //   323: astore_2
    //   324: aload_0
    //   325: new 4944	com/android/server/wm/DragState
    //   328: dup
    //   329: aload_0
    //   330: aload_2
    //   331: aload 17
    //   333: iload_3
    //   334: aload 6
    //   336: invokespecial 4947	com/android/server/wm/DragState:<init>	(Lcom/android/server/wm/WindowManagerService;Landroid/os/IBinder;Landroid/view/SurfaceControl;ILandroid/os/IBinder;)V
    //   339: putfield 681	com/android/server/wm/WindowManagerService:mDragState	Lcom/android/server/wm/DragState;
    //   342: aload_0
    //   343: getfield 681	com/android/server/wm/WindowManagerService:mDragState	Lcom/android/server/wm/DragState;
    //   346: iload 8
    //   348: putfield 4948	com/android/server/wm/DragState:mPid	I
    //   351: aload_0
    //   352: getfield 681	com/android/server/wm/WindowManagerService:mDragState	Lcom/android/server/wm/DragState;
    //   355: iload 9
    //   357: putfield 4949	com/android/server/wm/DragState:mUid	I
    //   360: aload_0
    //   361: getfield 681	com/android/server/wm/WindowManagerService:mDragState	Lcom/android/server/wm/DragState;
    //   364: fload 7
    //   366: putfield 4952	com/android/server/wm/DragState:mOriginalAlpha	F
    //   369: new 1470	android/os/Binder
    //   372: dup
    //   373: invokespecial 4942	android/os/Binder:<init>	()V
    //   376: astore_1
    //   377: aload_0
    //   378: getfield 681	com/android/server/wm/WindowManagerService:mDragState	Lcom/android/server/wm/DragState;
    //   381: aload_1
    //   382: putfield 4954	com/android/server/wm/DragState:mToken	Landroid/os/IBinder;
    //   385: aload_1
    //   386: astore 12
    //   388: aload_1
    //   389: astore 13
    //   391: aload_0
    //   392: getfield 640	com/android/server/wm/WindowManagerService:mH	Lcom/android/server/wm/WindowManagerService$H;
    //   395: bipush 20
    //   397: aload 6
    //   399: invokevirtual 4956	com/android/server/wm/WindowManagerService$H:removeMessages	(ILjava/lang/Object;)V
    //   402: aload_1
    //   403: astore 12
    //   405: aload_1
    //   406: astore 13
    //   408: aload_0
    //   409: getfield 640	com/android/server/wm/WindowManagerService:mH	Lcom/android/server/wm/WindowManagerService$H;
    //   412: bipush 20
    //   414: aload 6
    //   416: invokevirtual 2735	com/android/server/wm/WindowManagerService$H:obtainMessage	(ILjava/lang/Object;)Landroid/os/Message;
    //   419: astore_2
    //   420: aload_1
    //   421: astore 12
    //   423: aload_1
    //   424: astore 13
    //   426: aload_0
    //   427: getfield 640	com/android/server/wm/WindowManagerService:mH	Lcom/android/server/wm/WindowManagerService$H;
    //   430: aload_2
    //   431: ldc2_w 4957
    //   434: invokevirtual 3070	com/android/server/wm/WindowManagerService$H:sendMessageDelayed	(Landroid/os/Message;J)Z
    //   437: pop
    //   438: aload_1
    //   439: astore_2
    //   440: aload 16
    //   442: monitorexit
    //   443: lload 10
    //   445: invokestatic 2729	android/os/Binder:restoreCallingIdentity	(J)V
    //   448: aload_2
    //   449: areturn
    //   450: aload 14
    //   452: astore 12
    //   454: aload 15
    //   456: astore 13
    //   458: ldc_w 440
    //   461: ldc_w 4960
    //   464: invokestatic 1495	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   467: pop
    //   468: aconst_null
    //   469: astore_2
    //   470: goto -30 -> 440
    //   473: astore_2
    //   474: aload 12
    //   476: astore_1
    //   477: ldc_w 440
    //   480: new 1023	java/lang/StringBuilder
    //   483: dup
    //   484: invokespecial 1024	java/lang/StringBuilder:<init>	()V
    //   487: ldc_w 4962
    //   490: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   493: iload 4
    //   495: invokevirtual 1128	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   498: ldc_w 4913
    //   501: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   504: iload 5
    //   506: invokevirtual 1128	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   509: invokevirtual 1046	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   512: aload_2
    //   513: invokestatic 4964	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   516: pop
    //   517: aload_1
    //   518: astore_2
    //   519: aload_0
    //   520: getfield 681	com/android/server/wm/WindowManagerService:mDragState	Lcom/android/server/wm/DragState;
    //   523: ifnull -83 -> 440
    //   526: aload_0
    //   527: getfield 681	com/android/server/wm/WindowManagerService:mDragState	Lcom/android/server/wm/DragState;
    //   530: invokevirtual 4967	com/android/server/wm/DragState:reset	()V
    //   533: aload_0
    //   534: aconst_null
    //   535: putfield 681	com/android/server/wm/WindowManagerService:mDragState	Lcom/android/server/wm/DragState;
    //   538: aload_1
    //   539: astore_2
    //   540: goto -100 -> 440
    //   543: astore_1
    //   544: aload 16
    //   546: monitorexit
    //   547: aload_1
    //   548: athrow
    //   549: astore_1
    //   550: lload 10
    //   552: invokestatic 2729	android/os/Binder:restoreCallingIdentity	(J)V
    //   555: aload_1
    //   556: athrow
    //   557: astore_1
    //   558: goto -8 -> 550
    //   561: astore_1
    //   562: goto -18 -> 544
    //   565: astore 6
    //   567: aload_2
    //   568: astore_1
    //   569: aload 6
    //   571: astore_2
    //   572: goto -95 -> 477
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	575	0	this	WindowManagerService
    //   0	575	1	paramIWindow	IWindow
    //   0	575	2	paramSurfaceSession	SurfaceSession
    //   0	575	3	paramInt1	int
    //   0	575	4	paramInt2	int
    //   0	575	5	paramInt3	int
    //   0	575	6	paramSurface	Surface
    //   192	173	7	f	float
    //   86	261	8	i	int
    //   91	265	9	j	int
    //   96	455	10	l	long
    //   115	360	12	localObject1	Object
    //   119	338	13	localObject2	Object
    //   102	349	14	localObject3	Object
    //   99	356	15	localObject4	Object
    //   108	437	16	localHashMap	HashMap
    //   171	161	17	localSurfaceControl	SurfaceControl
    //   143	41	18	localDisplay	Display
    // Exception table:
    //   from	to	target	type
    //   121	128	473	android/view/Surface$OutOfResourcesException
    //   136	145	473	android/view/Surface$OutOfResourcesException
    //   153	173	473	android/view/Surface$OutOfResourcesException
    //   181	191	473	android/view/Surface$OutOfResourcesException
    //   214	221	473	android/view/Surface$OutOfResourcesException
    //   229	235	473	android/view/Surface$OutOfResourcesException
    //   243	277	473	android/view/Surface$OutOfResourcesException
    //   285	292	473	android/view/Surface$OutOfResourcesException
    //   300	308	473	android/view/Surface$OutOfResourcesException
    //   316	324	473	android/view/Surface$OutOfResourcesException
    //   391	402	473	android/view/Surface$OutOfResourcesException
    //   408	420	473	android/view/Surface$OutOfResourcesException
    //   426	438	473	android/view/Surface$OutOfResourcesException
    //   458	468	473	android/view/Surface$OutOfResourcesException
    //   324	385	543	finally
    //   477	517	543	finally
    //   519	538	543	finally
    //   440	443	549	finally
    //   544	549	549	finally
    //   104	113	557	finally
    //   121	128	561	finally
    //   136	145	561	finally
    //   153	173	561	finally
    //   181	191	561	finally
    //   214	221	561	finally
    //   229	235	561	finally
    //   243	277	561	finally
    //   285	292	561	finally
    //   300	308	561	finally
    //   316	324	561	finally
    //   391	402	561	finally
    //   408	420	561	finally
    //   426	438	561	finally
    //   458	468	561	finally
    //   324	385	565	android/view/Surface$OutOfResourcesException
  }
  
  public void prepareFreezingTaskBounds(int paramInt)
  {
    synchronized (this.mWindowMap)
    {
      TaskStack localTaskStack = (TaskStack)this.mStackIdToStack.get(paramInt);
      if (localTaskStack == null) {
        throw new IllegalArgumentException("prepareFreezingTaskBounds: stackId " + paramInt + " not found.");
      }
    }
    ((TaskStack)localObject).prepareFreezingTaskBounds();
  }
  
  void prolongAnimationsFromSpecs(AppTransitionAnimationSpec[] paramArrayOfAppTransitionAnimationSpec, boolean paramBoolean)
  {
    this.mTmpTaskIds.clear();
    int i = paramArrayOfAppTransitionAnimationSpec.length - 1;
    while (i >= 0)
    {
      this.mTmpTaskIds.put(paramArrayOfAppTransitionAnimationSpec[i].taskId, 0);
      i -= 1;
    }
    paramArrayOfAppTransitionAnimationSpec = this.mWindowMap.values().iterator();
    while (paramArrayOfAppTransitionAnimationSpec.hasNext())
    {
      Object localObject = (WindowState)paramArrayOfAppTransitionAnimationSpec.next();
      Task localTask = ((WindowState)localObject).getTask();
      if ((localTask != null) && (this.mTmpTaskIds.get(localTask.mTaskId, -1) != -1) && (localTask.inFreeformWorkspace()))
      {
        localObject = ((WindowState)localObject).mAppToken;
        if ((localObject != null) && (((AppWindowToken)localObject).mAppAnimator != null))
        {
          localObject = ((AppWindowToken)localObject).mAppAnimator;
          if (paramBoolean) {}
          for (i = 2;; i = 1)
          {
            ((AppWindowAnimator)localObject).startProlongAnimation(i);
            break;
          }
        }
      }
    }
  }
  
  public void reboot(boolean paramBoolean)
  {
    ShutdownThread.reboot(this.mContext, "userrequested", paramBoolean);
  }
  
  public void rebootSafeMode(boolean paramBoolean)
  {
    ShutdownThread.rebootSafeMode(this.mContext, paramBoolean);
  }
  
  final void rebuildAppWindowListLocked()
  {
    rebuildAppWindowListLocked(getDefaultDisplayContentLocked());
  }
  
  boolean reclaimSomeSurfaceMemoryLocked(WindowStateAnimator paramWindowStateAnimator, String paramString, boolean paramBoolean)
  {
    WindowSurfaceController localWindowSurfaceController = paramWindowStateAnimator.mSurfaceController;
    int i = 0;
    boolean bool2 = false;
    boolean bool1 = false;
    EventLog.writeEvent(31000, new Object[] { paramWindowStateAnimator.mWin.toString(), Integer.valueOf(paramWindowStateAnimator.mSession.mPid), paramString });
    long l = Binder.clearCallingIdentity();
    for (;;)
    {
      int j;
      int k;
      Object localObject1;
      try
      {
        Slog.i("WindowManager", "Out of memory for surface!  Looking for leaks...");
        int n = this.mDisplayContents.size();
        j = 0;
        Object localObject2;
        if (j < n)
        {
          paramString = ((DisplayContent)this.mDisplayContents.valueAt(j)).getWindowList();
          int i1 = paramString.size();
          k = 0;
          m = i;
          if (k >= i1) {
            break label757;
          }
          localObject1 = (WindowState)paramString.get(k);
          localObject2 = ((WindowState)localObject1).mWinAnimator;
          if (((WindowStateAnimator)localObject2).mSurfaceController == null)
          {
            i = m;
            break label744;
          }
          if (!this.mSessions.contains(((WindowStateAnimator)localObject2).mSession))
          {
            Slog.w("WindowManager", "LEAKED SURFACE (session doesn't exist): " + localObject1 + " surface=" + ((WindowStateAnimator)localObject2).mSurfaceController + " token=" + ((WindowState)localObject1).mToken + " pid=" + ((WindowState)localObject1).mSession.mPid + " uid=" + ((WindowState)localObject1).mSession.mUid);
            ((WindowStateAnimator)localObject2).destroySurface();
            this.mForceRemoves.add(localObject1);
            i = 1;
            break label744;
          }
          i = m;
          if (((WindowState)localObject1).mAppToken == null) {
            break label744;
          }
          i = m;
          if (!((WindowState)localObject1).mAppToken.clientHidden) {
            break label744;
          }
          Slog.w("WindowManager", "LEAKED SURFACE (app token hidden): " + localObject1 + " surface=" + ((WindowStateAnimator)localObject2).mSurfaceController + " token=" + ((WindowState)localObject1).mAppToken + " saved=" + ((WindowState)localObject1).hasSavedSurface());
          if (WindowManagerDebugConfig.SHOW_TRANSACTIONS) {
            logSurface((WindowState)localObject1, "LEAK DESTROY", false);
          }
          ((WindowStateAnimator)localObject2).destroySurface();
          i = 1;
          break label744;
        }
        if (i != 0) {
          break label641;
        }
        Slog.w("WindowManager", "No leaked surfaces; killing applicatons!");
        paramString = new SparseIntArray();
        j = 0;
        bool2 = bool1;
        if (j >= n) {
          break label641;
        }
        localObject1 = ((DisplayContent)this.mDisplayContents.valueAt(j)).getWindowList();
        m = ((WindowList)localObject1).size();
        k = 0;
        if (k < m)
        {
          localObject2 = (WindowState)((WindowList)localObject1).get(k);
          if (this.mForceRemoves.contains(localObject2)) {
            break label770;
          }
          localObject2 = ((WindowState)localObject2).mWinAnimator;
          if (((WindowStateAnimator)localObject2).mSurfaceController == null) {
            break label770;
          }
          paramString.append(((WindowStateAnimator)localObject2).mSession.mPid, ((WindowStateAnimator)localObject2).mSession.mPid);
        }
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
      bool2 = bool1;
      if (paramString.size() > 0)
      {
        localObject1 = new int[paramString.size()];
        k = 0;
        while (k < localObject1.length)
        {
          localObject1[k] = paramString.keyAt(k);
          k += 1;
        }
      }
      try
      {
        boolean bool3 = this.mActivityManager.killPids((int[])localObject1, "Free memory", paramBoolean);
        bool2 = bool1;
        if (bool3) {
          bool2 = true;
        }
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          bool2 = bool1;
        }
      }
      j += 1;
      bool1 = bool2;
      continue;
      label641:
      if ((i != 0) || (bool2))
      {
        Slog.w("WindowManager", "Looks like we have reclaimed some memory, clearing surface for retry.");
        if (localWindowSurfaceController != null)
        {
          if ((WindowManagerDebugConfig.SHOW_TRANSACTIONS) || (WindowManagerDebugConfig.SHOW_SURFACE_ALLOC)) {
            logSurface(paramWindowStateAnimator.mWin, "RECOVER DESTROY", false);
          }
          paramWindowStateAnimator.destroySurface();
          scheduleRemoveStartingWindowLocked(paramWindowStateAnimator.mWin.mAppToken);
        }
      }
      try
      {
        paramWindowStateAnimator.mWin.mClient.dispatchGetNewSurface();
        Binder.restoreCallingIdentity(l);
        if (i == 0) {
          return bool2;
        }
        return true;
      }
      catch (RemoteException paramWindowStateAnimator)
      {
        for (;;) {}
      }
      label744:
      k += 1;
      int m = i;
      continue;
      label757:
      j += 1;
      i = m;
      continue;
      label770:
      k += 1;
    }
  }
  
  public void reenableKeyguard(IBinder paramIBinder)
  {
    if (this.mContext.checkCallingOrSelfPermission("android.permission.DISABLE_KEYGUARD") != 0) {
      throw new SecurityException("Requires DISABLE_KEYGUARD permission");
    }
    if (paramIBinder == null) {
      throw new IllegalArgumentException("token == null");
    }
    if (WindowManagerDebugConfig.DEBUG_ONEPLUS) {
      Slog.d(TAG, "reenableKeyguard() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
    }
    this.mKeyguardDisableHandler.sendMessage(this.mKeyguardDisableHandler.obtainMessage(2, paramIBinder));
  }
  
  public void reevaluateStatusBarVisibility()
  {
    synchronized (this.mWindowMap)
    {
      if (updateStatusBarVisibilityLocked(this.mPolicy.adjustSystemUiVisibilityLw(this.mLastStatusBarVisibility))) {
        this.mWindowPlacerLocked.requestTraversal();
      }
      return;
    }
  }
  
  public void registerDockedStackListener(IDockedStackListener paramIDockedStackListener)
  {
    if (!checkCallingPermission("android.permission.REGISTER_WINDOW_MANAGER_LISTENERS", "registerDockedStackListener()")) {
      return;
    }
    getDefaultDisplayContentLocked().mDividerControllerLocked.registerDockedStackListener(paramIDockedStackListener);
  }
  
  public void registerPointerEventListener(WindowManagerPolicy.PointerEventListener paramPointerEventListener)
  {
    this.mPointerEventDispatcher.registerInputEventListener(paramPointerEventListener);
  }
  
  public void registerShortcutKey(long paramLong, IShortcutService paramIShortcutService)
    throws RemoteException
  {
    if (!checkCallingPermission("android.permission.REGISTER_WINDOW_MANAGER_LISTENERS", "registerShortcutKey")) {
      throw new SecurityException("Requires REGISTER_WINDOW_MANAGER_LISTENERS permission");
    }
    this.mPolicy.registerShortcutKey(paramLong, paramIShortcutService);
  }
  
  public int relayoutWindow(Session paramSession, IWindow paramIWindow, int paramInt1, WindowManager.LayoutParams paramLayoutParams, int paramInt2, int paramInt3, int paramInt4, int paramInt5, Rect paramRect1, Rect paramRect2, Rect paramRect3, Rect paramRect4, Rect paramRect5, Rect paramRect6, Rect paramRect7, Configuration paramConfiguration, Surface paramSurface)
  {
    int n = 0;
    if (this.mContext.checkCallingOrSelfPermission("android.permission.STATUS_BAR") == 0) {
      i = 1;
    }
    long l;
    WindowState localWindowState;
    WindowStateAnimator localWindowStateAnimator;
    for (;;)
    {
      l = Binder.clearCallingIdentity();
      synchronized (this.mWindowMap)
      {
        localWindowState = windowForClientLocked(paramSession, paramIWindow, false);
        if (localWindowState == null)
        {
          return 0;
          i = 0;
        }
        else
        {
          localWindowStateAnimator = localWindowState.mWinAnimator;
          if (paramInt4 != 8) {
            localWindowState.setRequestedSize(paramInt2, paramInt3);
          }
          j = 0;
          k = 0;
          if (paramLayoutParams == null) {
            break label318;
          }
          this.mPolicy.adjustWindowParamsLw(paramLayoutParams);
          if (paramInt1 == localWindowState.mSeq)
          {
            j = paramLayoutParams.systemUiVisibility | paramLayoutParams.subtreeSystemUiVisibility;
            paramInt1 = j;
            if ((0x3FF0000 & j) != 0)
            {
              paramInt1 = j;
              if (i == 0) {
                paramInt1 = j & 0xFC00FFFF;
              }
            }
            localWindowState.mSystemUiVisibility = paramInt1;
          }
          if (localWindowState.mAttrs.type != paramLayoutParams.type) {
            throw new IllegalArgumentException("Window type can not be changed after the window is added.");
          }
        }
      }
    }
    if ((paramLayoutParams.privateFlags & 0x2000) != 0)
    {
      paramLayoutParams.x = localWindowState.mAttrs.x;
      paramLayoutParams.y = localWindowState.mAttrs.y;
      paramLayoutParams.width = localWindowState.mAttrs.width;
      paramLayoutParams.height = localWindowState.mAttrs.height;
    }
    paramSession = localWindowState.mAttrs;
    paramInt1 = paramSession.flags ^ paramLayoutParams.flags;
    paramSession.flags = paramInt1;
    int i = localWindowState.mAttrs.copyFrom(paramLayoutParams);
    int j = i;
    int k = paramInt1;
    if ((i & 0x4001) != 0)
    {
      localWindowState.mLayoutNeeded = true;
      k = paramInt1;
      j = i;
    }
    label318:
    if (WindowManagerDebugConfig.DEBUG_LAYOUT) {
      Slog.v("WindowManager", "Relayout " + localWindowState + ": viewVisibility=" + paramInt4 + " req=" + paramInt2 + "x" + paramInt3 + " " + localWindowState.mAttrs);
    }
    boolean bool1;
    label453:
    label522:
    label531:
    boolean bool4;
    label564:
    label595:
    int i1;
    boolean bool3;
    label770:
    label798:
    boolean bool2;
    label830:
    label883:
    int m;
    if ((localWindowStateAnimator.mSurfaceDestroyDeferred) && ((paramInt5 & 0x2) == 0) && (paramInt4 != 0))
    {
      localWindowStateAnimator.mDeferMayMiss = true;
      break label2052;
      localWindowStateAnimator.mSurfaceDestroyDeferred = bool1;
      if ((localWindowState.mAttrs.privateFlags & 0x80) != 0)
      {
        bool1 = true;
        localWindowState.mEnforceSizeCompat = bool1;
        if ((j & 0x80) != 0) {
          localWindowStateAnimator.mAlpha = paramLayoutParams.alpha;
        }
        localWindowState.setWindowScale(localWindowState.mRequestedWidth, localWindowState.mRequestedHeight);
        if ((localWindowState.mAttrs.surfaceInsets.left == 0) && (localWindowState.mAttrs.surfaceInsets.top == 0)) {
          break label1563;
        }
        localWindowStateAnimator.setOpaqueLocked(false);
        break label2065;
        bool4 = localWindowState.isDefaultDisplay();
        if (!bool4) {
          break label2123;
        }
        if (localWindowState.mViewVisibility != paramInt4) {
          break label2079;
        }
        if ((k & 0x8) == 0) {
          break label1594;
        }
        break label2079;
        if (localWindowState.mViewVisibility == paramInt4) {
          break label2135;
        }
        if ((localWindowState.mAttrs.flags & 0x100000) == 0) {
          break label2129;
        }
        i = 1;
        break label2085;
        if (((k & 0x2000) != 0) && (localWindowStateAnimator.mSurfaceController != null)) {
          localWindowStateAnimator.mSurfaceController.setSecure(isSecureLocked(localWindowState));
        }
        localWindowState.mRelayoutCalled = true;
        localWindowState.mInRelayout = true;
        i1 = localWindowState.mViewVisibility;
        localWindowState.mViewVisibility = paramInt4;
        if (WindowManagerDebugConfig.DEBUG_SCREEN_ON)
        {
          paramSession = new RuntimeException();
          paramSession.fillInStackTrace();
          Slog.i("WindowManager", "Relayout " + localWindowState + ": oldVis=" + i1 + " newVis=" + paramInt4, paramSession);
        }
        if ((paramInt4 == 0) && ((localWindowState.mAppToken == null) || (!localWindowState.mAppToken.clientHidden))) {
          break label1608;
        }
        localWindowStateAnimator.mEnterAnimationPending = false;
        localWindowStateAnimator.mEnteringAnimation = false;
        if (i1 == 0) {
          break label1767;
        }
        bool3 = localWindowState.isAnimatingWithSavedSurface();
        if (((WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS) || (WindowManagerDebugConfig.DEBUG_ANIM)) && (localWindowStateAnimator.hasSurface()) && (!localWindowState.mAnimatingExit)) {
          break label1773;
        }
        j = n;
        bool2 = bool1;
        if (localWindowStateAnimator.hasSurface())
        {
          if (!localWindowState.mAnimatingExit) {
            break label1809;
          }
          bool2 = bool1;
          j = n;
        }
        if ((paramInt4 != 0) || (!localWindowStateAnimator.hasSurface())) {
          break label1899;
        }
        localWindowStateAnimator.mSurfaceController.getSurface(paramSurface);
        bool1 = bool2;
        k = paramInt1;
        if (!bool1) {
          break label2100;
        }
        k = paramInt1;
        if (!updateFocusedWindowLocked(3, false)) {
          break label2100;
        }
        k = 0;
        break label2100;
        if ((k != 0) && ((moveInputMethodWindowsIfNeededLocked(false)) || (paramInt1 != 0))) {
          this.mLayersController.assignLayersLocked(localWindowState.getWindowList());
        }
        if ((i | m) != 0)
        {
          paramSession = getDefaultDisplayContentLocked();
          paramSession.pendingLayoutChanges |= 0x4;
        }
        localWindowState.setDisplayLayoutNeeded();
        if ((paramInt5 & 0x1) == 0) {
          break label2158;
        }
        bool2 = true;
        label950:
        localWindowState.mGivenInsetsPending = bool2;
        bool2 = updateOrientationFromAppTokensLocked(false);
        this.mWindowPlacerLocked.performSurfacePlacement();
        if ((paramInt1 != 0) && (localWindowState.mIsWallpaper))
        {
          paramSession = getDefaultDisplayInfoLocked();
          this.mWallpaperControllerLocked.updateWallpaperOffset(localWindowState, paramSession.logicalWidth, paramSession.logicalHeight, false);
        }
        if (localWindowState.mAppToken != null) {
          localWindowState.mAppToken.updateReportedVisibilityLocked();
        }
        paramInt1 = j;
        if (localWindowStateAnimator.mReportSurfaceResized)
        {
          localWindowStateAnimator.mReportSurfaceResized = false;
          paramInt1 = j | 0x20;
        }
        paramInt5 = paramInt1;
        if (this.mPolicy.isNavBarForcedShownLw(localWindowState)) {
          paramInt5 = paramInt1 | 0x40;
        }
        if (!localWindowState.isGoneForLayoutLw()) {
          localWindowState.mResizedWhileGone = false;
        }
        paramRect1.set(localWindowState.mCompatFrame);
        paramRect2.set(localWindowState.mOverscanInsets);
        paramRect3.set(localWindowState.mContentInsets);
        paramRect4.set(localWindowState.mVisibleInsets);
        paramRect5.set(localWindowState.mStableInsets);
        paramRect6.set(localWindowState.mOutsets);
        paramRect7.set(localWindowState.getBackdropFrame(localWindowState.mFrame));
        if (localLOGV) {
          Slog.v("WindowManager", "Relayout given client " + paramIWindow.asBinder() + ", requestedWidth=" + paramInt2 + ", requestedHeight=" + paramInt3 + ", viewVisibility=" + paramInt4 + "\nRelayout returning frame=" + paramRect1 + ", surface=" + paramSurface);
        }
        if ((localLOGV) || (WindowManagerDebugConfig.DEBUG_FOCUS)) {
          Slog.v("WindowManager", "Relayout of " + localWindowState + ": focusMayChange=" + bool1);
        }
        if (!this.mInTouchMode) {
          break label2164;
        }
      }
    }
    label1442:
    label1462:
    label1563:
    label1594:
    label1608:
    label1767:
    label1773:
    label1809:
    label1899:
    label2021:
    label2052:
    label2065:
    label2079:
    label2085:
    label2100:
    label2117:
    label2123:
    label2129:
    label2135:
    label2141:
    label2147:
    label2153:
    label2158:
    label2164:
    for (paramInt1 = 1;; paramInt1 = 0)
    {
      this.mInputMonitor.updateInputWindowsLw(true);
      if (WindowManagerDebugConfig.DEBUG_LAYOUT) {
        Slog.v("WindowManager", "Relayout complete " + localWindowState + ": outFrame=" + paramRect1.toShortString());
      }
      localWindowState.mInRelayout = false;
      if (WindowManagerDebugConfig.DEBUG_ONEPLUS)
      {
        paramInt2 = localWindowState.mAttrs.privateFlags;
        paramIWindow = TAG;
        paramLayoutParams = new StringBuilder().append("Relayout ").append(localWindowState).append(" fl=0x").append(Integer.toHexString(localWindowState.mAttrs.flags));
        if (paramInt2 == 0)
        {
          paramSession = "";
          paramLayoutParams = paramLayoutParams.append(paramSession);
          if (localWindowState.mSystemUiVisibility != 0) {
            break label2021;
          }
          paramSession = "";
          Slog.d(paramIWindow, paramSession + " outFrame=" + paramRect1.toShortString() + " mViewVis:" + i1 + "->" + localWindowState.mViewVisibility + " mDrawState=" + localWindowStateAnimator.mDrawState);
        }
      }
      else
      {
        if (bool2) {
          sendNewConfiguration();
        }
        Binder.restoreCallingIdentity(l);
        return paramInt5 | paramInt1;
      }
      do
      {
        bool1 = false;
        break;
        bool1 = false;
        break label453;
        if (localWindowState.mAttrs.surfaceInsets.right != 0) {
          break label522;
        }
        if (localWindowState.mAttrs.surfaceInsets.bottom == 0) {
          break label2065;
        }
        break label522;
        if (!localWindowState.mRelayoutCalled) {
          break label2117;
        }
        bool1 = false;
        break label564;
        j = relayoutVisibleWindow(paramConfiguration, 0, localWindowState, localWindowStateAnimator, j, i1);
        try
        {
          j = createSurfaceControl(paramSurface, j, localWindowState, localWindowStateAnimator);
          if ((j & 0x2) != 0) {
            bool1 = bool4;
          }
          k = paramInt1;
          if (localWindowState.mAttrs.type == 2011)
          {
            k = paramInt1;
            if (this.mInputMethodWindow == null)
            {
              this.mInputMethodWindow = localWindowState;
              k = 1;
            }
          }
          localWindowState.adjustStartingWindowFlags();
          paramInt1 = k;
        }
        catch (Exception paramSession)
        {
          this.mInputMonitor.updateInputWindowsLw(true);
          Slog.w("WindowManager", "Exception thrown when creating surface for client " + paramIWindow + " (" + localWindowState.mAttrs.getTitle() + ")", paramSession);
          Binder.restoreCallingIdentity(l);
          return 0;
        }
        bool3 = false;
        break label770;
        if (!bool3) {
          break label798;
        }
        Slog.d(TAG, "Ignoring layout to invisible when using saved surface " + localWindowState);
        break label798;
        j = n;
        bool2 = bool1;
        if (bool3) {
          break label830;
        }
        if (WindowManagerDebugConfig.DEBUG_VISIBILITY) {
          Slog.i("WindowManager", "Relayout invis " + localWindowState + ": mAnimatingExit=" + localWindowState.mAnimatingExit);
        }
        bool2 = bool1;
        if (localWindowState.mWillReplaceWindow) {
          break label2147;
        }
        bool2 = tryStartExitingAnimation(localWindowState, localWindowStateAnimator, bool4, bool1);
        break label2147;
        if (WindowManagerDebugConfig.DEBUG_VISIBILITY) {
          Slog.i("WindowManager", "Releasing surface in: " + localWindowState);
        }
        try
        {
          Trace.traceBegin(32L, "wmReleaseOutSurface_" + localWindowState.mAttrs.getTitle());
          paramSurface.release();
          Trace.traceEnd(32L);
          bool1 = bool2;
        }
        finally
        {
          Trace.traceEnd(32L);
        }
        break label1442;
        paramSession = " mSysUiVis=0x" + Integer.toHexString(localWindowState.mSystemUiVisibility);
        break label1462;
      } while ((paramInt5 & 0x2) == 0);
      bool1 = true;
      break;
      if ((0x20008 & k) != 0)
      {
        paramInt1 = 1;
        break label531;
        bool1 = true;
        break label564;
      }
      for (;;)
      {
        if ((0x100000 & k) == 0) {
          break label2141;
        }
        m = 1;
        break label595;
        if ((j & 0x2) == 0) {
          break label2153;
        }
        paramInt1 = 1;
        break label883;
        paramInt1 = 0;
        break;
        bool1 = true;
        break label564;
        bool1 = false;
        break label564;
        i = 0;
        continue;
        i = 0;
      }
      m = 0;
      break label595;
      j = 4;
      break label830;
      paramInt1 = 0;
      break label883;
      bool2 = false;
      break label950;
    }
  }
  
  public void removeAppStartingWindow(IBinder paramIBinder)
  {
    synchronized (this.mWindowMap)
    {
      scheduleRemoveStartingWindowLocked(((WindowToken)this.mTokenMap.get(paramIBinder)).appWindowToken);
      return;
    }
  }
  
  public void removeAppToken(IBinder paramIBinder)
  {
    if (!checkCallingPermission("android.permission.MANAGE_APP_TOKENS", "removeAppToken()")) {
      throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
    }
    Object localObject2 = null;
    Object localObject4 = null;
    Object localObject3 = null;
    int i = 0;
    long l = Binder.clearCallingIdentity();
    for (;;)
    {
      synchronized (this.mWindowMap)
      {
        Object localObject1 = (WindowToken)this.mTokenMap.remove(paramIBinder);
        if (localObject1 != null)
        {
          localObject1 = ((WindowToken)localObject1).appWindowToken;
          localObject2 = localObject1;
          if (localObject1 != null)
          {
            if (WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS) {
              Slog.v("WindowManager", "Removing app token: " + localObject1);
            }
            boolean bool = setTokenVisibilityLocked((AppWindowToken)localObject1, null, false, -1, true, ((AppWindowToken)localObject1).voiceInteraction);
            ((AppWindowToken)localObject1).inPendingTransaction = false;
            this.mOpeningApps.remove(localObject1);
            ((AppWindowToken)localObject1).waitingToShow = false;
            if (this.mClosingApps.contains(localObject1))
            {
              bool = true;
              if (WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS) {
                Slog.v("WindowManager", "Removing app " + localObject1 + " delayed=" + bool + " animation=" + ((AppWindowToken)localObject1).mAppAnimator.animation + " animating=" + ((AppWindowToken)localObject1).mAppAnimator.animating);
              }
              if ((WindowManagerDebugConfig.DEBUG_ADD_REMOVE) || (WindowManagerDebugConfig.DEBUG_TOKEN_MOVEMENT)) {
                Slog.v("WindowManager", "removeAppToken: " + localObject1 + " delayed=" + bool + " Callers=" + Debug.getCallers(4));
              }
              paramIBinder = ((AppWindowToken)localObject1).mTask.mStack;
              if ((!bool) || (((AppWindowToken)localObject1).allAppWindows.isEmpty()))
              {
                ((AppWindowToken)localObject1).mAppAnimator.clearAnimation();
                ((AppWindowToken)localObject1).mAppAnimator.animating = false;
                ((AppWindowToken)localObject1).removeAppFromTaskLocked();
                ((AppWindowToken)localObject1).removed = true;
                paramIBinder = (IBinder)localObject3;
                if (((AppWindowToken)localObject1).startingData != null) {
                  paramIBinder = (IBinder)localObject1;
                }
                unsetAppFreezingScreenLocked((AppWindowToken)localObject1, true, true);
                localObject2 = localObject1;
                i = bool;
                localObject3 = paramIBinder;
                if (this.mFocusedApp == localObject1)
                {
                  if (WindowManagerDebugConfig.DEBUG_FOCUS_LIGHT) {
                    Slog.v("WindowManager", "Removing focused app token:" + localObject1);
                  }
                  this.mFocusedApp = null;
                  updateFocusedWindowLocked(0, true);
                  this.mInputMonitor.setFocusedAppLw(null);
                  localObject3 = paramIBinder;
                  i = bool;
                  localObject2 = localObject1;
                }
                if ((i == 0) && (localObject2 != null)) {
                  ((AppWindowToken)localObject2).updateReportedVisibilityLocked();
                }
                scheduleRemoveStartingWindowLocked((AppWindowToken)localObject3);
                Binder.restoreCallingIdentity(l);
              }
            }
            else
            {
              if (!this.mAppTransition.isTransitionSet()) {
                continue;
              }
              this.mClosingApps.add(localObject1);
              bool = true;
              continue;
            }
            if ((WindowManagerDebugConfig.DEBUG_ADD_REMOVE) || (WindowManagerDebugConfig.DEBUG_TOKEN_MOVEMENT)) {
              Slog.v("WindowManager", "removeAppToken make exiting: " + localObject1);
            }
            paramIBinder.mExitingAppTokens.add(localObject1);
            ((AppWindowToken)localObject1).mIsExiting = true;
          }
        }
      }
      Slog.w("WindowManager", "Attempted to remove non-existing app token: " + paramIBinder);
      localObject3 = localObject4;
    }
  }
  
  boolean removeInputConsumer()
  {
    synchronized (this.mWindowMap)
    {
      if (this.mInputConsumer != null)
      {
        this.mInputConsumer = null;
        this.mInputMonitor.updateInputWindowsLw(true);
        return true;
      }
      return false;
    }
  }
  
  public void removeRotationWatcher(IRotationWatcher paramIRotationWatcher)
  {
    IBinder localIBinder1 = paramIRotationWatcher.asBinder();
    paramIRotationWatcher = this.mWindowMap;
    int i = 0;
    try
    {
      while (i < this.mRotationWatchers.size())
      {
        int j = i;
        if (localIBinder1 == ((RotationWatcher)this.mRotationWatchers.get(i)).watcher.asBinder())
        {
          RotationWatcher localRotationWatcher = (RotationWatcher)this.mRotationWatchers.remove(i);
          IBinder localIBinder2 = localRotationWatcher.watcher.asBinder();
          if (localIBinder2 != null) {
            localIBinder2.unlinkToDeath(localRotationWatcher.deathRecipient, 0);
          }
          j = i - 1;
        }
        i = j + 1;
      }
      return;
    }
    finally {}
  }
  
  public void removeStack(int paramInt)
  {
    synchronized (this.mWindowMap)
    {
      this.mStackIdToStack.remove(paramInt);
      return;
    }
  }
  
  public void removeTask(int paramInt)
  {
    synchronized (this.mWindowMap)
    {
      Task localTask = (Task)this.mTaskIdToTask.get(paramInt);
      if (localTask == null)
      {
        if (WindowManagerDebugConfig.DEBUG_STACK) {
          Slog.i("WindowManager", "removeTask: could not find taskId=" + paramInt);
        }
        return;
      }
      localTask.removeLocked();
      return;
    }
  }
  
  public void removeWallpaperInputConsumer()
  {
    synchronized (this.mWindowMap)
    {
      if (this.mWallpaperInputConsumer != null)
      {
        this.mWallpaperInputConsumer.disposeChannelsLw();
        this.mWallpaperInputConsumer = null;
        this.mInputMonitor.updateInputWindowsLw(true);
      }
      return;
    }
  }
  
  public void removeWindow(Session paramSession, IWindow paramIWindow)
  {
    synchronized (this.mWindowMap)
    {
      paramSession = windowForClientLocked(paramSession, paramIWindow, false);
      if (paramSession == null) {
        return;
      }
      removeWindowLocked(paramSession);
      return;
    }
  }
  
  public void removeWindowChangeListener(WindowChangeListener paramWindowChangeListener)
  {
    synchronized (this.mWindowMap)
    {
      this.mWindowChangeListeners.remove(paramWindowChangeListener);
      return;
    }
  }
  
  void removeWindowInnerLocked(WindowState paramWindowState)
  {
    if (paramWindowState.mRemoved)
    {
      if (WindowManagerDebugConfig.DEBUG_ADD_REMOVE) {
        Slog.v("WindowManager", "removeWindowInnerLocked: " + paramWindowState + " Already removed...");
      }
      return;
    }
    int i = paramWindowState.mChildWindows.size() - 1;
    Object localObject;
    while (i >= 0)
    {
      localObject = (WindowState)paramWindowState.mChildWindows.get(i);
      Slog.w("WindowManager", "Force-removing child win " + localObject + " from container " + paramWindowState);
      removeWindowInnerLocked((WindowState)localObject);
      i -= 1;
    }
    paramWindowState.mRemoved = true;
    if (this.mInputMethodTarget == paramWindowState) {
      moveInputMethodWindowsIfNeededLocked(false);
    }
    i = paramWindowState.mAttrs.type;
    if (excludeWindowTypeFromTapOutTask(i)) {
      paramWindowState.getDisplayContent().mTapExcludedWindows.remove(paramWindowState);
    }
    this.mPolicy.removeWindowLw(paramWindowState);
    paramWindowState.removeLocked();
    if (WindowManagerDebugConfig.DEBUG_ADD_REMOVE) {
      Slog.v("WindowManager", "removeWindowInnerLocked: " + paramWindowState);
    }
    this.mWindowMap.remove(paramWindowState.mClient.asBinder());
    if (paramWindowState.mAppOp != -1) {
      this.mAppOps.finishOp(paramWindowState.mAppOp, paramWindowState.getOwningUid(), paramWindowState.getOwningPackage());
    }
    this.mPendingRemove.remove(paramWindowState);
    this.mResizingWindows.remove(paramWindowState);
    this.mWindowsChanged = true;
    if (WindowManagerDebugConfig.DEBUG_WINDOW_MOVEMENT) {
      Slog.v("WindowManager", "Final remove of window: " + paramWindowState);
    }
    AppWindowToken localAppWindowToken;
    if (this.mInputMethodWindow == paramWindowState)
    {
      this.mInputMethodWindow = null;
      localObject = paramWindowState.mToken;
      localAppWindowToken = paramWindowState.mAppToken;
      if (WindowManagerDebugConfig.DEBUG_ADD_REMOVE) {
        Slog.v("WindowManager", "Removing " + paramWindowState + " from " + localObject);
      }
      ((WindowToken)localObject).windows.remove(paramWindowState);
      if (localAppWindowToken != null) {
        localAppWindowToken.allAppWindows.remove(paramWindowState);
      }
      if (localLOGV) {
        Slog.v("WindowManager", "**** Removing window " + paramWindowState + ": count=" + ((WindowToken)localObject).windows.size());
      }
      if (((WindowToken)localObject).windows.size() == 0)
      {
        if (((WindowToken)localObject).explicit) {
          break label658;
        }
        this.mTokenMap.remove(((WindowToken)localObject).token);
      }
      label484:
      if (localAppWindowToken != null)
      {
        if (localAppWindowToken.startingWindow != paramWindowState) {
          break label677;
        }
        if (WindowManagerDebugConfig.DEBUG_STARTING_WINDOW) {
          Slog.v("WindowManager", "Notify removed startingWindow " + paramWindowState);
        }
        scheduleRemoveStartingWindowLocked(localAppWindowToken);
      }
      label537:
      if (i != 2013) {
        break label750;
      }
      this.mWallpaperControllerLocked.clearLastWallpaperTimeoutTime();
      localObject = getDefaultDisplayContentLocked();
      ((DisplayContent)localObject).pendingLayoutChanges |= 0x4;
    }
    for (;;)
    {
      localObject = paramWindowState.getWindowList();
      if (localObject != null)
      {
        ((WindowList)localObject).remove(paramWindowState);
        if (!this.mWindowPlacerLocked.isInLayout())
        {
          this.mLayersController.assignLayersLocked((WindowList)localObject);
          paramWindowState.setDisplayLayoutNeeded();
          this.mWindowPlacerLocked.performSurfacePlacement();
          if (paramWindowState.mAppToken != null) {
            paramWindowState.mAppToken.updateReportedVisibilityLocked();
          }
        }
      }
      this.mInputMonitor.updateInputWindowsLw(true);
      return;
      if (paramWindowState.mAttrs.type != 2012) {
        break;
      }
      this.mInputMethodDialogs.remove(paramWindowState);
      break;
      label658:
      if (localAppWindowToken == null) {
        break label484;
      }
      localAppWindowToken.firstWindowDrawn = false;
      localAppWindowToken.clearAllDrawn();
      break label484;
      label677:
      if ((localAppWindowToken.allAppWindows.size() == 0) && (localAppWindowToken.startingData != null))
      {
        if (WindowManagerDebugConfig.DEBUG_STARTING_WINDOW) {
          Slog.v("WindowManager", "Nulling last startingWindow");
        }
        localAppWindowToken.startingData = null;
        break label537;
      }
      if ((localAppWindowToken.allAppWindows.size() != 1) || (localAppWindowToken.startingView == null)) {
        break label537;
      }
      scheduleRemoveStartingWindowLocked(localAppWindowToken);
      break label537;
      label750:
      if ((paramWindowState.mAttrs.flags & 0x100000) != 0)
      {
        localObject = getDefaultDisplayContentLocked();
        ((DisplayContent)localObject).pendingLayoutChanges |= 0x4;
      }
    }
  }
  
  void removeWindowLocked(WindowState paramWindowState)
  {
    removeWindowLocked(paramWindowState, false);
  }
  
  void removeWindowLocked(WindowState paramWindowState, boolean paramBoolean)
  {
    paramWindowState.mWindowRemovalAllowed = true;
    if (WindowManagerDebugConfig.DEBUG_ADD_REMOVE) {
      Slog.v(TAG, "removeWindowLocked: " + paramWindowState + " callers=" + Debug.getCallers(4));
    }
    int i;
    long l;
    Object localObject1;
    if (paramWindowState.mAttrs.type == 3)
    {
      i = 1;
      if ((i != 0) && (WindowManagerDebugConfig.DEBUG_STARTING_WINDOW)) {
        Slog.d("WindowManager", "Starting window removed " + paramWindowState);
      }
      if ((localLOGV) || (WindowManagerDebugConfig.DEBUG_FOCUS) || ((WindowManagerDebugConfig.DEBUG_FOCUS_LIGHT) && (paramWindowState == this.mCurrentFocus))) {
        Slog.v("WindowManager", "Remove " + paramWindowState + " client=" + Integer.toHexString(System.identityHashCode(paramWindowState.mClient.asBinder())) + ", surfaceController=" + paramWindowState.mWinAnimator.mSurfaceController + " Callers=" + Debug.getCallers(4));
      }
      l = Binder.clearCallingIdentity();
      paramWindowState.disposeInputChannel();
      if (WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS)
      {
        localObject2 = new StringBuilder().append("Remove ").append(paramWindowState).append(": mSurfaceController=").append(paramWindowState.mWinAnimator.mSurfaceController).append(" mAnimatingExit=").append(paramWindowState.mAnimatingExit).append(" mRemoveOnExit=").append(paramWindowState.mRemoveOnExit).append(" mHasSurface=").append(paramWindowState.mHasSurface).append(" surfaceShowing=").append(paramWindowState.mWinAnimator.getShown()).append(" isAnimationSet=").append(paramWindowState.mWinAnimator.isAnimationSet()).append(" app-animation=");
        if (paramWindowState.mAppToken == null) {
          break label545;
        }
        localObject1 = paramWindowState.mAppToken.mAppAnimator.animation;
        label353:
        localObject1 = ((StringBuilder)localObject2).append(localObject1).append(" mWillReplaceWindow=").append(paramWindowState.mWillReplaceWindow).append(" inPendingTransaction=");
        if (paramWindowState.mAppToken == null) {
          break label551;
        }
      }
    }
    boolean bool2;
    label545:
    label551:
    for (boolean bool1 = paramWindowState.mAppToken.inPendingTransaction;; bool1 = false)
    {
      Slog.v("WindowManager", bool1 + " mDisplayFrozen=" + this.mDisplayFrozen + " callers=" + Debug.getCallers(6));
      bool2 = false;
      bool1 = bool2;
      if (!paramWindowState.mHasSurface) {
        break label982;
      }
      bool1 = bool2;
      if (!okToDisplay()) {
        break label982;
      }
      localObject1 = paramWindowState.mAppToken;
      if (!paramWindowState.mWillReplaceWindow) {
        break label557;
      }
      if (WindowManagerDebugConfig.DEBUG_ADD_REMOVE) {
        Slog.v("WindowManager", "Preserving " + paramWindowState + " until the new one is " + "added");
      }
      paramWindowState.mAnimatingExit = true;
      paramWindowState.mReplacingRemoveRequested = true;
      Binder.restoreCallingIdentity(l);
      return;
      i = 0;
      break;
      localObject1 = null;
      break label353;
    }
    label557:
    if ((!paramWindowState.isAnimatingWithSavedSurface()) || (((AppWindowToken)localObject1).allDrawnExcludingSaved))
    {
      bool2 = paramWindowState.isWinVisibleLw();
      if (paramBoolean)
      {
        if (WindowManagerDebugConfig.DEBUG_ADD_REMOVE) {
          Slog.v("WindowManager", "Not removing " + paramWindowState + " because app died while it's visible");
        }
        paramWindowState.mAppDied = true;
        paramWindowState.setDisplayLayoutNeeded();
        this.mWindowPlacerLocked.performSurfacePlacement();
        paramWindowState.openInputChannel(null);
        this.mInputMonitor.updateInputWindowsLw(true);
        Binder.restoreCallingIdentity(l);
      }
    }
    else
    {
      if (WindowManagerDebugConfig.DEBUG_ADD_REMOVE) {
        Slog.d("WindowManager", "removeWindowLocked: delay removal of " + paramWindowState + " due to early animation");
      }
      setupWindowForRemoveOnExit(paramWindowState);
      Binder.restoreCallingIdentity(l);
      return;
    }
    Object localObject2 = paramWindowState.mWinAnimator;
    int j;
    if (bool2)
    {
      if (i != 0) {
        break label960;
      }
      j = 2;
      if (((WindowStateAnimator)localObject2).applyAnimationLocked(j, false)) {
        paramWindowState.mAnimatingExit = true;
      }
      if ((this.mAccessibilityController != null) && (paramWindowState.getDisplayId() == 0)) {
        this.mAccessibilityController.onWindowTransitionLocked(paramWindowState, j);
      }
    }
    if ((!((WindowStateAnimator)localObject2).isAnimationSet()) || (((WindowStateAnimator)localObject2).isDummyAnimation()))
    {
      j = 0;
      label783:
      if ((i == 0) || (localObject1 == null)) {
        break label977;
      }
      if (((AppWindowToken)localObject1).allAppWindows.size() != 1) {
        break label972;
      }
      i = 1;
    }
    for (;;)
    {
      bool1 = bool2;
      if (!((WindowStateAnimator)localObject2).getShown()) {
        break label982;
      }
      bool1 = bool2;
      if (!paramWindowState.mAnimatingExit) {
        break label982;
      }
      if (i != 0)
      {
        bool1 = bool2;
        if (j == 0) {
          break label982;
        }
      }
      if (WindowManagerDebugConfig.DEBUG_ADD_REMOVE) {
        Slog.v("WindowManager", "Not removing " + paramWindowState + " due to exit animation ");
      }
      if ((OpFeatures.isSupport(new int[] { 12 })) && (paramWindowState.mAttrs.packageName != null) && (paramWindowState.mAttrs.packageName.equals("com.oneplus.permissionutil")) && (updateOrientationFromAppTokensLocked(false))) {
        this.mH.sendEmptyMessage(18);
      }
      setupWindowForRemoveOnExit(paramWindowState);
      if (localObject1 != null) {
        ((AppWindowToken)localObject1).updateReportedVisibilityLocked();
      }
      Binder.restoreCallingIdentity(l);
      return;
      label960:
      j = 5;
      break;
      j = 1;
      break label783;
      label972:
      i = 0;
      continue;
      label977:
      i = 0;
    }
    label982:
    removeWindowInnerLocked(paramWindowState);
    if ((bool1) && (updateOrientationFromAppTokensLocked(false))) {
      this.mH.sendEmptyMessage(18);
    }
    updateFocusedWindowLocked(0, true);
    Binder.restoreCallingIdentity(l);
  }
  
  public void removeWindowToken(IBinder paramIBinder)
  {
    if (!checkCallingPermission("android.permission.MANAGE_APP_TOKENS", "removeWindowToken()")) {
      throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
    }
    long l = Binder.clearCallingIdentity();
    HashMap localHashMap = this.mWindowMap;
    WindowState localWindowState = null;
    for (;;)
    {
      WindowToken localWindowToken;
      int i;
      try
      {
        localWindowToken = (WindowToken)this.mTokenMap.remove(paramIBinder);
        if (localWindowToken == null) {
          break label304;
        }
        int k = 0;
        if (!localWindowToken.hidden)
        {
          int n = localWindowToken.windows.size();
          int j = 0;
          i = 0;
          paramIBinder = localWindowState;
          if (i < n)
          {
            localWindowState = (WindowState)localWindowToken.windows.get(i);
            paramIBinder = localWindowState.getDisplayContent();
            if (localWindowState.mWinAnimator.isAnimationSet()) {
              k = 1;
            }
            if (!localWindowState.isVisibleNow()) {
              break label334;
            }
            localWindowState.mWinAnimator.applyAnimationLocked(2, false);
            if ((this.mAccessibilityController != null) && (localWindowState.isDefaultDisplay())) {
              this.mAccessibilityController.onWindowTransitionLocked(localWindowState, 2);
            }
            int m = 1;
            j = m;
            if (paramIBinder == null) {
              break label334;
            }
            paramIBinder.layoutNeeded = true;
            j = m;
            break label334;
          }
          localWindowToken.hidden = true;
          if (j != 0)
          {
            this.mWindowPlacerLocked.performSurfacePlacement();
            updateFocusedWindowLocked(0, false);
          }
          if ((k != 0) && (paramIBinder != null))
          {
            paramIBinder.mExitingTokens.add(localWindowToken);
            this.mInputMonitor.updateInputWindowsLw(true);
            Binder.restoreCallingIdentity(l);
            return;
          }
          if (localWindowToken.windowType != 2013) {
            continue;
          }
          this.mWallpaperControllerLocked.removeWallpaperToken(localWindowToken);
          continue;
        }
        if (localWindowToken.windowType != 2013) {
          continue;
        }
      }
      finally {}
      this.mWallpaperControllerLocked.removeWallpaperToken(localWindowToken);
      continue;
      label304:
      Slog.w("WindowManager", "Attempted to remove non-existing token: " + paramIBinder);
      continue;
      label334:
      i += 1;
    }
  }
  
  void repositionChild(Session paramSession, IWindow paramIWindow, int paramInt1, int paramInt2, int paramInt3, int paramInt4, long paramLong, Rect arg9)
  {
    Trace.traceBegin(32L, "repositionChild");
    long l = Binder.clearCallingIdentity();
    try
    {
      synchronized (this.mWindowMap)
      {
        paramSession = windowForClientLocked(paramSession, paramIWindow, false);
        if (paramSession == null) {
          return;
        }
        if (paramSession.mAttachedWindow == null) {
          throw new IllegalArgumentException("repositionChild called but window is notattached to a parent win=" + paramSession);
        }
      }
      paramSession.mAttrs.x = paramInt1;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
      Trace.traceEnd(32L);
    }
    paramSession.mAttrs.y = paramInt2;
    paramSession.mAttrs.width = (paramInt3 - paramInt1);
    paramSession.mAttrs.height = (paramInt4 - paramInt2);
    paramSession.setWindowScale(paramSession.mRequestedWidth, paramSession.mRequestedHeight);
    if (paramSession.mHasSurface)
    {
      if (WindowManagerDebugConfig.SHOW_TRANSACTIONS) {
        Slog.i("WindowManager", ">>> OPEN TRANSACTION repositionChild");
      }
      SurfaceControl.openTransaction();
    }
    try
    {
      paramSession.applyGravityAndUpdateFrame(paramSession.mContainingFrame, paramSession.mDisplayFrame);
      paramSession.mWinAnimator.computeShownFrameLocked();
      paramSession.mWinAnimator.setSurfaceBoundariesLocked(false);
      if (paramLong > 0L) {
        paramSession.mWinAnimator.deferTransactionUntilParentFrame(paramLong);
      }
      SurfaceControl.closeTransaction();
      if (WindowManagerDebugConfig.SHOW_TRANSACTIONS) {
        Slog.i("WindowManager", "<<< CLOSE TRANSACTION repositionChild");
      }
      paramSession = paramSession.mCompatFrame;
      Binder.restoreCallingIdentity(l);
      Trace.traceEnd(32L);
      return;
    }
    finally
    {
      SurfaceControl.closeTransaction();
      if (WindowManagerDebugConfig.SHOW_TRANSACTIONS) {
        Slog.i("WindowManager", "<<< CLOSE TRANSACTION repositionChild");
      }
    }
  }
  
  public void requestAppKeyboardShortcuts(IResultReceiver paramIResultReceiver, int paramInt)
  {
    try
    {
      WindowState localWindowState = getFocusedWindow();
      if ((localWindowState != null) && (localWindowState.mClient != null)) {
        getFocusedWindow().mClient.requestAppKeyboardShortcuts(paramIResultReceiver, paramInt);
      }
      return;
    }
    catch (RemoteException paramIResultReceiver) {}
  }
  
  public boolean requestAssistScreenshot(final IAssistScreenshotReceiver paramIAssistScreenshotReceiver)
  {
    if (!checkCallingPermission("android.permission.READ_FRAME_BUFFER", "requestAssistScreenshot()")) {
      throw new SecurityException("Requires READ_FRAME_BUFFER permission");
    }
    FgThread.getHandler().post(new Runnable()
    {
      public void run()
      {
        Bitmap localBitmap = WindowManagerService.this.screenshotApplicationsInner(null, 0, -1, -1, true, 1.0F, Bitmap.Config.ARGB_8888, false);
        try
        {
          paramIAssistScreenshotReceiver.send(localBitmap);
          return;
        }
        catch (RemoteException localRemoteException) {}
      }
    });
    return true;
  }
  
  void requestTraversal()
  {
    synchronized (this.mWindowMap)
    {
      this.mWindowPlacerLocked.requestTraversal();
      return;
    }
  }
  
  public boolean resizeStack(int paramInt, Rect paramRect, SparseArray<Configuration> paramSparseArray, SparseArray<Rect> paramSparseArray1, SparseArray<Rect> paramSparseArray2)
  {
    TaskStack localTaskStack;
    synchronized (this.mWindowMap)
    {
      localTaskStack = (TaskStack)this.mStackIdToStack.get(paramInt);
      if (localTaskStack == null) {
        throw new IllegalArgumentException("resizeStack: stackId " + paramInt + " not found.");
      }
    }
    if ((localTaskStack.setBounds(paramRect, paramSparseArray, paramSparseArray1, paramSparseArray2)) && (localTaskStack.isVisibleLocked()))
    {
      localTaskStack.getDisplayContent().layoutNeeded = true;
      this.mWindowPlacerLocked.performSurfacePlacement();
    }
    boolean bool = localTaskStack.getRawFullscreen();
    return bool;
  }
  
  public void resizeTask(int paramInt, Rect paramRect, Configuration paramConfiguration, boolean paramBoolean1, boolean paramBoolean2)
  {
    Task localTask;
    synchronized (this.mWindowMap)
    {
      localTask = (Task)this.mTaskIdToTask.get(paramInt);
      if (localTask == null) {
        throw new IllegalArgumentException("resizeTask: taskId " + paramInt + " not found.");
      }
    }
    if ((localTask.resizeLocked(paramRect, paramConfiguration, paramBoolean2)) && (paramBoolean1))
    {
      localTask.getDisplayContent().layoutNeeded = true;
      this.mWindowPlacerLocked.performSurfacePlacement();
    }
  }
  
  void restorePointerIconLocked(DisplayContent paramDisplayContent, float paramFloat1, float paramFloat2)
  {
    this.mMousePositionTracker.updatePosition(paramFloat1, paramFloat2);
    paramDisplayContent = paramDisplayContent.getTouchableWinAtPointLocked(paramFloat1, paramFloat2);
    if (paramDisplayContent != null) {
      try
      {
        paramDisplayContent.mClient.updatePointerIcon(paramDisplayContent.translateToWindowX(paramFloat1), paramDisplayContent.translateToWindowY(paramFloat2));
        return;
      }
      catch (RemoteException paramDisplayContent)
      {
        Slog.w("WindowManager", "unable to restore pointer icon");
        return;
      }
    }
    InputManager.getInstance().setPointerIconType(1000);
  }
  
  public void resumeKeyDispatching(IBinder paramIBinder)
  {
    if (!checkCallingPermission("android.permission.MANAGE_APP_TOKENS", "resumeKeyDispatching()")) {
      throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
    }
    synchronized (this.mWindowMap)
    {
      paramIBinder = (WindowToken)this.mTokenMap.get(paramIBinder);
      if (paramIBinder != null) {
        this.mInputMonitor.resumeDispatchingLw(paramIBinder);
      }
      return;
    }
  }
  
  void resumeRotationLocked()
  {
    if (this.mDeferredRotationPauseCount > 0)
    {
      this.mDeferredRotationPauseCount -= 1;
      if ((this.mDeferredRotationPauseCount == 0) && (updateRotationUncheckedLocked(false))) {
        this.mH.sendEmptyMessage(18);
      }
    }
  }
  
  boolean rotationNeedsUpdateLocked()
  {
    int i = this.mPolicy.rotationForOrientationLw(this.mLastOrientation, this.mRotation);
    if (this.mPolicy.rotationHasCompatibleMetricsLw(this.mLastOrientation, i)) {}
    for (int j = 0; (this.mRotation == i) && (this.mAltOrientation == j); j = 1) {
      return false;
    }
    return true;
  }
  
  public void saveANRStateLocked(AppWindowToken paramAppWindowToken, WindowState paramWindowState, String paramString)
  {
    StringWriter localStringWriter = new StringWriter();
    FastPrintWriter localFastPrintWriter = new FastPrintWriter(localStringWriter, false, 1024);
    localFastPrintWriter.println("  ANR time: " + DateFormat.getInstance().format(new Date()));
    if (paramAppWindowToken != null) {
      localFastPrintWriter.println("  Application at fault: " + paramAppWindowToken.stringName);
    }
    if (paramWindowState != null) {
      localFastPrintWriter.println("  Window at fault: " + paramWindowState.mAttrs.getTitle());
    }
    if (paramString != null) {
      localFastPrintWriter.println("  Reason: " + paramString);
    }
    localFastPrintWriter.println();
    dumpWindowsNoHeaderLocked(localFastPrintWriter, true, null);
    localFastPrintWriter.println();
    localFastPrintWriter.println("Last ANR continued");
    dumpDisplayContentsLocked(localFastPrintWriter, true);
    localFastPrintWriter.close();
    this.mLastANRState = localStringWriter.toString();
    this.mH.removeMessages(38);
    this.mH.sendEmptyMessageDelayed(38, 7200000L);
  }
  
  void scheduleAnimationLocked()
  {
    if (!this.mAnimationScheduled)
    {
      this.mAnimationScheduled = true;
      this.mChoreographer.postFrameCallback(this.mAnimator.mAnimationFrameCallback);
    }
  }
  
  public void scheduleClearReplacingWindowIfNeeded(IBinder paramIBinder, boolean paramBoolean)
  {
    synchronized (this.mWindowMap)
    {
      AppWindowToken localAppWindowToken = findAppWindowToken(paramIBinder);
      if (localAppWindowToken == null)
      {
        Slog.w("WindowManager", "Attempted to reset replacing window on non-existing app token " + paramIBinder);
        return;
      }
      if (paramBoolean)
      {
        scheduleReplacingWindowTimeouts(localAppWindowToken);
        return;
      }
      localAppWindowToken.resetReplacingWindows();
    }
  }
  
  void scheduleRemoveStartingWindowLocked(AppWindowToken paramAppWindowToken)
  {
    if (paramAppWindowToken == null) {
      return;
    }
    if (this.mH.hasMessages(6, paramAppWindowToken)) {
      return;
    }
    if (paramAppWindowToken.startingWindow == null)
    {
      if (paramAppWindowToken.startingData != null)
      {
        if (WindowManagerDebugConfig.DEBUG_STARTING_WINDOW) {
          Slog.v("WindowManager", "Clearing startingData for token=" + paramAppWindowToken);
        }
        paramAppWindowToken.startingData = null;
      }
      return;
    }
    StringBuilder localStringBuilder;
    if (WindowManagerDebugConfig.DEBUG_STARTING_WINDOW)
    {
      localStringBuilder = new StringBuilder().append(Debug.getCallers(1)).append(": Schedule remove starting ").append(paramAppWindowToken);
      if (paramAppWindowToken == null) {
        break label167;
      }
    }
    label167:
    for (String str = " startingWindow=" + paramAppWindowToken.startingWindow;; str = "")
    {
      Slog.v("WindowManager", str);
      paramAppWindowToken = this.mH.obtainMessage(6, paramAppWindowToken);
      this.mH.sendMessage(paramAppWindowToken);
      return;
    }
  }
  
  void scheduleReplacingWindowTimeouts(AppWindowToken paramAppWindowToken)
  {
    if (!this.mReplacingWindowTimeouts.contains(paramAppWindowToken)) {
      this.mReplacingWindowTimeouts.add(paramAppWindowToken);
    }
    this.mH.removeMessages(46);
    this.mH.sendEmptyMessageDelayed(46, 2000L);
  }
  
  public Bitmap screenshotApplications(IBinder paramIBinder, int paramInt1, int paramInt2, int paramInt3, float paramFloat)
  {
    if (!checkCallingPermission("android.permission.READ_FRAME_BUFFER", "screenshotApplications()")) {
      throw new SecurityException("Requires READ_FRAME_BUFFER permission");
    }
    try
    {
      Trace.traceBegin(32L, "screenshotApplications");
      paramIBinder = screenshotApplicationsInner(paramIBinder, paramInt1, paramInt2, paramInt3, false, paramFloat, Bitmap.Config.RGB_565, false);
      return paramIBinder;
    }
    finally
    {
      Trace.traceEnd(32L);
    }
  }
  
  Bitmap screenshotApplicationsInner(IBinder paramIBinder, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, float paramFloat, Bitmap.Config paramConfig, boolean paramBoolean2)
  {
    Object localObject3;
    DisplayInfo localDisplayInfo;
    int i4;
    int i5;
    synchronized (this.mWindowMap)
    {
      localObject3 = getDisplayContentLocked(paramInt1);
      if (localObject3 == null)
      {
        if (WindowManagerDebugConfig.DEBUG_SCREENSHOT) {
          Slog.i("WindowManager", "Screenshot of " + paramIBinder + ": returning null. No Display for displayId=" + paramInt1);
        }
        return null;
      }
      localDisplayInfo = ((DisplayContent)localObject3).getDisplayInfo();
      i4 = localDisplayInfo.logicalWidth;
      i5 = localDisplayInfo.logicalHeight;
      if ((i4 == 0) || (i5 == 0))
      {
        if (WindowManagerDebugConfig.DEBUG_SCREENSHOT) {
          Slog.i("WindowManager", "Screenshot of " + paramIBinder + ": returning null. logical widthxheight=" + i4 + "x" + i5);
        }
        return null;
      }
    }
    int j = 0;
    Rect localRect1 = new Rect();
    Rect localRect2 = new Rect();
    label228:
    boolean bool;
    int m;
    label272:
    int i6;
    HashMap localHashMap;
    if ((paramIBinder != null) || (paramBoolean2))
    {
      paramInt1 = 0;
      i = Integer.MAX_VALUE;
      synchronized (this.mWindowMap)
      {
        if (this.mInputMethodTarget != null)
        {
          ??? = this.mInputMethodTarget.mAppToken;
          if ((??? == null) || (((AppWindowToken)???).appToken == null) || (((AppWindowToken)???).appToken.asBinder() != paramIBinder)) {
            break label418;
          }
          bool = this.mInputMethodTarget.isInMultiWindowMode();
          if (!bool) {
            break label412;
          }
          m = 0;
          i6 = this.mPolicy.windowTypeToLayerLw(2);
          localHashMap = this.mWindowMap;
          ??? = null;
        }
      }
    }
    WindowList localWindowList;
    int n;
    int k;
    int i2;
    int i3;
    for (;;)
    {
      try
      {
        localWindowList = ((DisplayContent)localObject3).getWindowList();
        int i1 = localWindowList.size() - 1;
        n = i;
        k = j;
        ??? = ???;
        i2 = paramInt1;
        WindowState localWindowState;
        if (i1 >= 0)
        {
          localWindowState = (WindowState)localWindowList.get(i1);
          bool = localWindowState.mHasSurface;
          if (!bool)
          {
            i3 = paramInt1;
            localObject3 = ???;
            i2 = j;
            n = i;
            i1 -= 1;
            i = n;
            j = i2;
            ??? = localObject3;
            paramInt1 = i3;
            continue;
            paramInt1 = 1;
            i = 0;
            break;
            ??? = null;
            break label228;
            label412:
            m = 1;
            break label272;
            label418:
            m = 0;
            break label272;
            paramIBinder = finally;
            throw paramIBinder;
          }
          n = i;
          i2 = j;
          localObject3 = ???;
          i3 = paramInt1;
          if (localWindowState.mLayer >= (i6 + 1) * 10000 + 1000) {
            continue;
          }
          if (paramBoolean2)
          {
            n = i;
            i2 = j;
            localObject3 = ???;
            i3 = paramInt1;
            if (!localWindowState.mIsWallpaper) {
              continue;
            }
          }
          if (!localWindowState.mIsImWindow) {
            continue;
          }
          n = i;
          i2 = j;
          localObject3 = ???;
          i3 = paramInt1;
          if (m == 0) {
            continue;
          }
          ??? = ???;
          label525:
          ??? = localWindowState.mWinAnimator;
          n = ((WindowStateAnimator)???).mSurfaceController.getLayer();
          k = j;
          if (j >= n) {
            break label1823;
          }
          k = n;
          break label1823;
          label560:
          if ((!paramBoolean1) && (!localWindowState.mIsWallpaper)) {
            continue;
          }
          if ((localWindowState.mAppToken == null) || (localWindowState.mAppToken.token != paramIBinder)) {
            break label889;
          }
          bool = true;
          i = paramInt1;
          if (bool)
          {
            i = paramInt1;
            if (localWindowState.isDisplayedLw())
            {
              i = paramInt1;
              if (((WindowStateAnimator)???).getShown()) {
                i = 1;
              }
            }
          }
          n = j;
          i2 = k;
          localObject3 = ???;
          i3 = i;
          if (!localWindowState.isObscuringFullscreen(localDisplayInfo)) {
            continue;
          }
          i2 = i;
          n = j;
        }
        if ((paramIBinder == null) || (??? != null)) {
          break label907;
        }
        if (WindowManagerDebugConfig.DEBUG_SCREENSHOT) {
          Slog.i("WindowManager", "Screenshot: Couldn't find a surface matching " + paramIBinder);
        }
        return null;
        if (localWindowState.mIsWallpaper)
        {
          if (!paramBoolean2) {
            break label1841;
          }
          ??? = localWindowState;
          break label1841;
        }
        ??? = ???;
        if (paramIBinder == null) {
          continue;
        }
        n = i;
        i2 = j;
        localObject3 = ???;
        i3 = paramInt1;
        if (localWindowState.mAppToken == null) {
          continue;
        }
        n = i;
        i2 = j;
        localObject3 = ???;
        i3 = paramInt1;
        if (localWindowState.mAppToken.token != paramIBinder) {
          continue;
        }
        ??? = localWindowState;
        continue;
        localObject3 = localWindowState.mFrame;
        Rect localRect3 = localWindowState.mContentInsets;
        localRect1.union(((Rect)localObject3).left + localRect3.left, ((Rect)localObject3).top + localRect3.top, ((Rect)localObject3).right - localRect3.right, ((Rect)localObject3).bottom - localRect3.bottom);
        localWindowState.getVisibleBounds(localRect2);
        if (Rect.intersects(localRect1, localRect2)) {
          continue;
        }
        localRect1.setEmpty();
        continue;
        if (??? == null) {
          break label901;
        }
      }
      finally {}
      label889:
      bool = paramBoolean2;
      continue;
      label901:
      bool = false;
    }
    label907:
    if (i2 == 0)
    {
      paramConfig = new StringBuilder().append("Failed to capture screenshot of ").append(paramIBinder).append(" appWin=");
      if (??? == null) {}
      for (paramIBinder = "null";; paramIBinder = ??? + " drawState=" + ((WindowState)???).mWinAnimator.mDrawState)
      {
        Slog.i("WindowManager", paramIBinder);
        return null;
      }
    }
    if (k == 0)
    {
      if (WindowManagerDebugConfig.DEBUG_SCREENSHOT) {
        Slog.i("WindowManager", "Screenshot of " + paramIBinder + ": returning null maxLayer=" + k);
      }
      return null;
    }
    if (!paramBoolean1) {
      if (!localRect1.intersect(0, 0, i4, i5)) {
        localRect1.setEmpty();
      }
    }
    for (;;)
    {
      paramBoolean1 = localRect1.isEmpty();
      if (!paramBoolean1) {
        break;
      }
      return null;
      localRect1.set(0, 0, i4, i5);
    }
    int i = paramInt2;
    if (paramInt2 < 0) {
      i = (int)(localRect1.width() * paramFloat);
    }
    paramInt2 = paramInt3;
    if (paramInt3 < 0) {
      paramInt2 = (int)(localRect1.height() * paramFloat);
    }
    ??? = new Rect(localRect1);
    if (i / localRect1.width() < paramInt2 / localRect1.height())
    {
      paramInt1 = (int)(i / paramInt2 * localRect1.height());
      ((Rect)???).right = (((Rect)???).left + paramInt1);
      paramInt3 = getDefaultDisplayContentLocked().getDisplay().getRotation();
      if (paramInt3 == 1) {
        break label1868;
      }
      paramInt1 = paramInt3;
      if (paramInt3 == 3) {
        break label1868;
      }
    }
    for (;;)
    {
      convertCropForSurfaceFlinger((Rect)???, paramInt1, i4, i5);
      if (WindowManagerDebugConfig.DEBUG_SCREENSHOT)
      {
        Slog.i("WindowManager", "Screenshot: " + i4 + "x" + i5 + " from " + n + " to " + k + " appToken=" + paramIBinder);
        paramInt3 = 0;
        label1331:
        if (paramInt3 < localWindowList.size())
        {
          localObject3 = (WindowState)localWindowList.get(paramInt3);
          paramIBinder = ((WindowState)localObject3).mWinAnimator.mSurfaceController;
          localObject3 = new StringBuilder().append(localObject3).append(": ").append(((WindowState)localObject3).mLayer).append(" animLayer=").append(((WindowState)localObject3).mWinAnimator.mAnimLayer).append(" surfaceLayer=");
          if (paramIBinder == null) {}
          for (paramIBinder = "null";; paramIBinder = Integer.valueOf(paramIBinder.getLayer()))
          {
            Slog.i("WindowManager", paramIBinder);
            paramInt3 += 1;
            break label1331;
            paramInt1 = (int)(paramInt2 / i * localRect1.width());
            ((Rect)???).bottom = (((Rect)???).top + paramInt1);
            break;
          }
        }
      }
      paramIBinder = this.mAnimator.getScreenRotationAnimationLocked(0);
      if (paramIBinder != null) {}
      for (paramBoolean1 = paramIBinder.isAnimating();; paramBoolean1 = false)
      {
        if ((WindowManagerDebugConfig.DEBUG_SCREENSHOT) && (paramBoolean1)) {
          Slog.v("WindowManager", "Taking screenshot while rotating");
        }
        SurfaceControl.openTransaction();
        SurfaceControl.closeTransactionSync();
        ??? = SurfaceControl.screenshot((Rect)???, i, paramInt2, n, k, paramBoolean1, paramInt1);
        if (??? != null) {
          break;
        }
        Slog.w("WindowManager", "Screenshot failure taking screenshot for (" + i4 + "x" + i5 + ") to layer " + k);
        return null;
      }
      if (WindowManagerDebugConfig.DEBUG_SCREENSHOT)
      {
        paramIBinder = new int[((Bitmap)???).getWidth() * ((Bitmap)???).getHeight()];
        ((Bitmap)???).getPixels(paramIBinder, 0, ((Bitmap)???).getWidth(), 0, 0, ((Bitmap)???).getWidth(), ((Bitmap)???).getHeight());
        paramInt3 = 1;
        i = paramIBinder[0];
        paramInt1 = 0;
        paramInt2 = paramInt3;
        if (paramInt1 < paramIBinder.length)
        {
          if (paramIBinder[paramInt1] == i) {
            break label1809;
          }
          paramInt2 = 0;
        }
        if (paramInt2 != 0)
        {
          localObject3 = new StringBuilder().append("Screenshot ").append(???).append(" was monochrome(").append(Integer.toHexString(i)).append(")! mSurfaceLayer=");
          if (??? == null) {
            break label1816;
          }
        }
      }
      label1809:
      label1816:
      for (paramIBinder = Integer.valueOf(((WindowState)???).mWinAnimator.mSurfaceController.getLayer());; paramIBinder = "null")
      {
        Slog.i("WindowManager", paramIBinder + " minLayer=" + n + " maxLayer=" + k);
        paramIBinder = ((Bitmap)???).createAshmemBitmap(paramConfig);
        ((Bitmap)???).recycle();
        return paramIBinder;
        paramInt1 += 1;
        break;
      }
      label1823:
      j = i;
      if (i <= n) {
        break label560;
      }
      j = n;
      break label560;
      label1841:
      ??? = ???;
      if (??? != null) {
        break label525;
      }
      n = i;
      i2 = j;
      localObject3 = ???;
      i3 = paramInt1;
      break;
      label1868:
      if (paramInt3 == 1) {
        paramInt1 = 3;
      } else {
        paramInt1 = 1;
      }
    }
  }
  
  public Bitmap screenshotWallpaper()
  {
    if (!checkCallingPermission("android.permission.READ_FRAME_BUFFER", "screenshotWallpaper()")) {
      throw new SecurityException("Requires READ_FRAME_BUFFER permission");
    }
    try
    {
      Trace.traceBegin(32L, "screenshotWallpaper");
      Bitmap localBitmap = screenshotApplicationsInner(null, 0, -1, -1, true, 1.0F, Bitmap.Config.ARGB_8888, true);
      return localBitmap;
    }
    finally
    {
      Trace.traceEnd(32L);
    }
  }
  
  public void scrollTask(int paramInt, Rect paramRect)
  {
    Task localTask;
    synchronized (this.mWindowMap)
    {
      localTask = (Task)this.mTaskIdToTask.get(paramInt);
      if (localTask == null) {
        throw new IllegalArgumentException("scrollTask: taskId " + paramInt + " not found.");
      }
    }
    if (localTask.scrollLocked(paramRect))
    {
      localTask.getDisplayContent().layoutNeeded = true;
      this.mInputMonitor.setUpdateInputWindowsNeededLw();
      this.mWindowPlacerLocked.performSurfacePlacement();
    }
  }
  
  void sendNewConfiguration()
  {
    try
    {
      this.mActivityManager.updateConfiguration(null);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public void setAnimationScale(int paramInt, float paramFloat)
  {
    if (!checkCallingPermission("android.permission.SET_ANIMATION_SCALE", "setAnimationScale()")) {
      throw new SecurityException("Requires SET_ANIMATION_SCALE permission");
    }
    paramFloat = fixScale(paramFloat);
    switch (paramInt)
    {
    }
    for (;;)
    {
      this.mH.sendEmptyMessage(14);
      return;
      this.mWindowAnimationScaleSetting = paramFloat;
      continue;
      this.mTransitionAnimationScaleSetting = paramFloat;
      continue;
      this.mAnimatorDurationScaleSetting = paramFloat;
    }
  }
  
  public void setAnimationScales(float[] paramArrayOfFloat)
  {
    if (!checkCallingPermission("android.permission.SET_ANIMATION_SCALE", "setAnimationScale()")) {
      throw new SecurityException("Requires SET_ANIMATION_SCALE permission");
    }
    if (paramArrayOfFloat != null)
    {
      if (paramArrayOfFloat.length >= 1) {
        this.mWindowAnimationScaleSetting = fixScale(paramArrayOfFloat[0]);
      }
      if (paramArrayOfFloat.length >= 2) {
        this.mTransitionAnimationScaleSetting = fixScale(paramArrayOfFloat[1]);
      }
      if (paramArrayOfFloat.length >= 3)
      {
        this.mAnimatorDurationScaleSetting = fixScale(paramArrayOfFloat[2]);
        dispatchNewAnimatorScaleLocked(null);
      }
    }
    this.mH.sendEmptyMessage(14);
  }
  
  public void setAppFullscreen(IBinder paramIBinder, boolean paramBoolean)
  {
    synchronized (this.mWindowMap)
    {
      AppWindowToken localAppWindowToken = findAppWindowToken(paramIBinder);
      if (localAppWindowToken != null)
      {
        localAppWindowToken.appFullscreen = paramBoolean;
        setWindowOpaqueLocked(paramIBinder, paramBoolean);
        this.mWindowPlacerLocked.requestTraversal();
      }
      return;
    }
  }
  
  public void setAppOrientation(IApplicationToken paramIApplicationToken, int paramInt)
  {
    if (!checkCallingPermission("android.permission.MANAGE_APP_TOKENS", "setAppOrientation()")) {
      throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
    }
    synchronized (this.mWindowMap)
    {
      AppWindowToken localAppWindowToken = findAppWindowToken(paramIApplicationToken.asBinder());
      if (localAppWindowToken == null)
      {
        Slog.w("WindowManager", "Attempted to set orientation of non-existing app token: " + paramIApplicationToken);
        return;
      }
      localAppWindowToken.requestedOrientation = paramInt;
      return;
    }
  }
  
  public boolean setAppStartingWindow(IBinder paramIBinder1, String paramString, int paramInt1, CompatibilityInfo paramCompatibilityInfo, CharSequence paramCharSequence, int paramInt2, int paramInt3, int paramInt4, int paramInt5, IBinder paramIBinder2, boolean paramBoolean)
  {
    if (!checkCallingPermission("android.permission.MANAGE_APP_TOKENS", "setAppStartingWindow()")) {
      throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
    }
    synchronized (this.mWindowMap)
    {
      if (WindowManagerDebugConfig.DEBUG_STARTING_WINDOW) {
        Slog.v("WindowManager", "setAppStartingWindow: token=" + paramIBinder1 + " pkg=" + paramString + " transferFrom=" + paramIBinder2);
      }
      AppWindowToken localAppWindowToken = findAppWindowToken(paramIBinder1);
      if (localAppWindowToken == null)
      {
        Slog.w("WindowManager", "Attempted to set icon of non-existing app token: " + paramIBinder1);
        return false;
      }
      boolean bool1 = okToDisplay();
      if (!bool1) {
        return false;
      }
      paramIBinder1 = localAppWindowToken.startingData;
      if (paramIBinder1 != null) {
        return false;
      }
      if (WindowManagerDebugConfig.DEBUG_STARTING_WINDOW) {
        Slog.v("WindowManager", "Checking theme of starting window: 0x" + Integer.toHexString(paramInt1));
      }
      int i = paramInt5;
      if (paramInt1 != 0)
      {
        paramIBinder1 = AttributeCache.instance().get(paramString, paramInt1, R.styleable.Window, this.mCurrentUserId);
        if (paramIBinder1 == null) {
          return false;
        }
        bool1 = paramIBinder1.array.getBoolean(5, false);
        boolean bool2 = paramIBinder1.array.getBoolean(4, false);
        boolean bool3 = paramIBinder1.array.getBoolean(14, false);
        boolean bool4 = paramIBinder1.array.getBoolean(12, false);
        if (WindowManagerDebugConfig.DEBUG_STARTING_WINDOW) {
          Slog.v("WindowManager", "Translucent=" + bool1 + " Floating=" + bool2 + " ShowWallpaper=" + bool3);
        }
        if (bool1) {
          return false;
        }
        if ((bool2) || (bool4)) {
          return false;
        }
        i = paramInt5;
        if (bool3)
        {
          if (this.mWallpaperControllerLocked.getWallpaperTarget() != null) {
            break label405;
          }
          i = paramInt5 | 0x100000;
        }
      }
      bool1 = transferStartingWindow(paramIBinder2, localAppWindowToken);
      if (bool1)
      {
        return true;
        label405:
        return false;
      }
      if (!paramBoolean) {
        return false;
      }
      if (WindowManagerDebugConfig.DEBUG_STARTING_WINDOW) {
        Slog.v("WindowManager", "Creating StartingData");
      }
      localAppWindowToken.startingData = new StartingData(paramString, paramInt1, paramCompatibilityInfo, paramCharSequence, paramInt2, paramInt3, paramInt4, i);
      paramIBinder1 = this.mH.obtainMessage(5, localAppWindowToken);
      if (WindowManagerDebugConfig.DEBUG_STARTING_WINDOW) {
        Slog.v("WindowManager", "Enqueueing ADD_STARTING");
      }
      this.mH.sendMessageAtFrontOfQueue(paramIBinder1);
      return true;
    }
  }
  
  public void setAppTask(IBinder paramIBinder, int paramInt1, int paramInt2, Rect paramRect, Configuration paramConfiguration, int paramInt3, boolean paramBoolean)
  {
    if (!checkCallingPermission("android.permission.MANAGE_APP_TOKENS", "setAppTask()")) {
      throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
    }
    synchronized (this.mWindowMap)
    {
      AppWindowToken localAppWindowToken = findAppWindowToken(paramIBinder);
      if (localAppWindowToken == null)
      {
        Slog.w("WindowManager", "Attempted to set task id of non-existing app token: " + paramIBinder);
        return;
      }
      Task localTask2 = localAppWindowToken.mTask;
      localTask2.removeAppToken(localAppWindowToken);
      Task localTask1 = (Task)this.mTaskIdToTask.get(paramInt1);
      paramIBinder = localTask1;
      if (localTask1 == null) {
        paramIBinder = createTaskLocked(paramInt1, paramInt2, localTask2.mUserId, localAppWindowToken, paramRect, paramConfiguration);
      }
      paramIBinder.addAppToken(Integer.MAX_VALUE, localAppWindowToken, paramInt3, paramBoolean);
      return;
    }
  }
  
  public void setAppVisibility(IBinder paramIBinder, boolean paramBoolean)
  {
    boolean bool = false;
    if (!checkCallingPermission("android.permission.MANAGE_APP_TOKENS", "setAppVisibility()")) {
      throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
    }
    AppWindowToken localAppWindowToken;
    for (;;)
    {
      synchronized (this.mWindowMap)
      {
        localAppWindowToken = findAppWindowToken(paramIBinder);
        if (localAppWindowToken == null)
        {
          Slog.w("WindowManager", "Attempted to set visibility of non-existing app token: " + paramIBinder);
          return;
        }
        if ((WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS) || (WindowManagerDebugConfig.DEBUG_ORIENTATION)) {
          Slog.v("WindowManager", "setAppVisibility(" + paramIBinder + ", visible=" + paramBoolean + "): " + this.mAppTransition + " hidden=" + localAppWindowToken.hidden + " hiddenRequested=" + localAppWindowToken.hiddenRequested + " Callers=" + Debug.getCallers(6));
        }
        this.mOpeningApps.remove(localAppWindowToken);
        this.mClosingApps.remove(localAppWindowToken);
        localAppWindowToken.waitingToShow = false;
        if (paramBoolean)
        {
          localAppWindowToken.hiddenRequested = bool;
          if (!paramBoolean)
          {
            localAppWindowToken.removeAllDeadWindows();
            localAppWindowToken.setVisibleBeforeClientHidden();
            if ((!okToDisplay()) || (!this.mAppTransition.isTransitionSet())) {
              break;
            }
            if ((localAppWindowToken.mAppAnimator.usingTransferredAnimation) && (localAppWindowToken.mAppAnimator.animation == null)) {
              Slog.wtf("WindowManager", "Will NOT set dummy animation on: " + localAppWindowToken + ", using null transfered animation!");
            }
            if ((!localAppWindowToken.mAppAnimator.usingTransferredAnimation) && ((!localAppWindowToken.startingDisplayed) || (this.mSkipAppTransitionAnimation)))
            {
              if (WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS) {
                Slog.v("WindowManager", "Setting dummy animation on: " + localAppWindowToken);
              }
              localAppWindowToken.mAppAnimator.setDummyAnimation();
            }
            localAppWindowToken.inPendingTransaction = true;
            if (!paramBoolean) {
              break label643;
            }
            this.mOpeningApps.add(localAppWindowToken);
            localAppWindowToken.mEnteringAnimation = true;
            if (this.mAppTransition.getAppTransition() == 16)
            {
              paramIBinder = findFocusedWindowLocked(getDefaultDisplayContentLocked());
              if (paramIBinder != null)
              {
                paramIBinder = paramIBinder.mAppToken;
                if (paramIBinder != null)
                {
                  if (WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS) {
                    Slog.d("WindowManager", "TRANSIT_TASK_OPEN_BEHIND,  adding " + paramIBinder + " to mOpeningApps");
                  }
                  paramIBinder.hidden = true;
                  this.mOpeningApps.add(paramIBinder);
                }
              }
            }
          }
        }
        else
        {
          bool = true;
          continue;
        }
        if (!paramBoolean) {
          continue;
        }
        if ((!this.mAppTransition.isTransitionSet()) && (this.mAppTransition.isReady())) {
          this.mOpeningApps.add(localAppWindowToken);
        }
        localAppWindowToken.startingMoved = false;
        if ((localAppWindowToken.hidden) || (localAppWindowToken.mAppStopped))
        {
          localAppWindowToken.clearAllDrawn();
          if (localAppWindowToken.hidden) {
            localAppWindowToken.waitingToShow = true;
          }
          if (localAppWindowToken.clientHidden)
          {
            localAppWindowToken.clientHidden = false;
            localAppWindowToken.sendAppVisibilityToClients();
          }
        }
        localAppWindowToken.requestUpdateWallpaperIfNeeded();
        if (WindowManagerDebugConfig.DEBUG_ADD_REMOVE) {
          Slog.v("WindowManager", "No longer Stopped: " + localAppWindowToken);
        }
        localAppWindowToken.mAppStopped = false;
      }
      label643:
      this.mClosingApps.add(localAppWindowToken);
      localAppWindowToken.mEnteringAnimation = false;
    }
    long l = Binder.clearCallingIdentity();
    localAppWindowToken.inPendingTransaction = false;
    setTokenVisibilityLocked(localAppWindowToken, null, paramBoolean, -1, true, localAppWindowToken.voiceInteraction);
    localAppWindowToken.updateReportedVisibilityLocked();
    Binder.restoreCallingIdentity(l);
  }
  
  public void setCurrentProfileIds(int[] paramArrayOfInt)
  {
    synchronized (this.mWindowMap)
    {
      this.mCurrentProfileIds = paramArrayOfInt;
      return;
    }
  }
  
  public void setCurrentUser(int paramInt, int[] paramArrayOfInt)
  {
    synchronized (this.mWindowMap)
    {
      this.mCurrentUserId = paramInt;
      this.mCurrentProfileIds = paramArrayOfInt;
      this.mAppTransition.setCurrentUser(paramInt);
      this.mPolicy.setCurrentUserLw(paramInt);
      this.mPolicy.enableKeyguard(true);
      int j = this.mDisplayContents.size();
      int i = 0;
      while (i < j)
      {
        paramArrayOfInt = (DisplayContent)this.mDisplayContents.valueAt(i);
        paramArrayOfInt.switchUserStacks();
        rebuildAppWindowListLocked(paramArrayOfInt);
        i += 1;
      }
      this.mWindowPlacerLocked.performSurfacePlacement();
      paramArrayOfInt = getDefaultDisplayContentLocked();
      paramArrayOfInt.mDividerControllerLocked.notifyDockedStackExistsChanged(hasDockedTasksForUser(paramInt));
      if (this.mDisplayReady)
      {
        paramInt = getForcedDisplayDensityForUserLocked(paramInt);
        if (paramInt != 0) {
          setForcedDisplayDensityLocked(paramArrayOfInt, paramInt);
        }
      }
      else
      {
        return;
      }
      paramInt = paramArrayOfInt.mInitialDisplayDensity;
    }
  }
  
  public void setDockedStackCreateState(int paramInt, Rect paramRect)
  {
    synchronized (this.mWindowMap)
    {
      setDockedStackCreateStateLocked(paramInt, paramRect);
      return;
    }
  }
  
  void setDockedStackCreateStateLocked(int paramInt, Rect paramRect)
  {
    this.mDockedStackCreateMode = paramInt;
    this.mDockedStackCreateBounds = paramRect;
  }
  
  public void setDockedStackDividerTouchRegion(Rect paramRect)
  {
    synchronized (this.mWindowMap)
    {
      getDefaultDisplayContentLocked().getDockedDividerController().setTouchRegion(paramRect);
      setFocusTaskRegionLocked();
      return;
    }
  }
  
  public void setDockedStackResizing(boolean paramBoolean)
  {
    synchronized (this.mWindowMap)
    {
      getDefaultDisplayContentLocked().getDockedDividerController().setResizing(paramBoolean);
      requestTraversal();
      return;
    }
  }
  
  public void setEventDispatching(boolean paramBoolean)
  {
    if (!checkCallingPermission("android.permission.MANAGE_APP_TOKENS", "setEventDispatching()")) {
      throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
    }
    synchronized (this.mWindowMap)
    {
      this.mEventDispatchingEnabled = paramBoolean;
      if (this.mDisplayEnabled) {
        this.mInputMonitor.setEventDispatchingLw(paramBoolean);
      }
      return;
    }
  }
  
  void setFocusTaskRegionLocked()
  {
    if (this.mFocusedApp != null)
    {
      Task localTask = this.mFocusedApp.mTask;
      DisplayContent localDisplayContent = localTask.getDisplayContent();
      if (localDisplayContent != null) {
        localDisplayContent.setTouchExcludeRegion(localTask);
      }
    }
  }
  
  public void setFocusedApp(IBinder paramIBinder, boolean paramBoolean)
  {
    if (!checkCallingPermission("android.permission.MANAGE_APP_TOKENS", "setFocusedApp()")) {
      throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
    }
    HashMap localHashMap = this.mWindowMap;
    if (paramIBinder == null) {}
    for (;;)
    {
      int i;
      try
      {
        if (!WindowManagerDebugConfig.DEBUG_FOCUS_LIGHT) {
          break label252;
        }
        Slog.v("WindowManager", "Clearing focused app, was " + this.mFocusedApp);
      }
      finally {}
      if (this.mFocusedApp != paramIBinder)
      {
        i = 1;
        if (i != 0)
        {
          this.mFocusedApp = paramIBinder;
          this.mInputMonitor.setFocusedAppLw(paramIBinder);
          setFocusTaskRegionLocked();
        }
        if ((paramBoolean) && (i != 0))
        {
          long l = Binder.clearCallingIdentity();
          updateFocusedWindowLocked(0, true);
          Binder.restoreCallingIdentity(l);
        }
        return;
        AppWindowToken localAppWindowToken = findAppWindowToken(paramIBinder);
        if (localAppWindowToken == null) {
          Slog.w("WindowManager", "Attempted to set focus to non-existing app token: " + paramIBinder);
        }
        paramIBinder = localAppWindowToken;
        if (WindowManagerDebugConfig.DEBUG_FOCUS_LIGHT)
        {
          Slog.v("WindowManager", "Set focused app to: " + localAppWindowToken + " old focus=" + this.mFocusedApp + " moveFocusNow=" + paramBoolean);
          paramIBinder = localAppWindowToken;
        }
      }
      else
      {
        i = 0;
        continue;
        label252:
        paramIBinder = null;
      }
    }
  }
  
  public void setForceResizableTasks(boolean paramBoolean)
  {
    synchronized (this.mWindowMap)
    {
      this.mForceResizableTasks = paramBoolean;
      return;
    }
  }
  
  /* Error */
  public void setForcedDisplayDensityForUser(int paramInt1, int paramInt2, int paramInt3)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 713	com/android/server/wm/WindowManagerService:mContext	Landroid/content/Context;
    //   4: ldc_w 3307
    //   7: invokevirtual 3310	android/content/Context:checkCallingOrSelfPermission	(Ljava/lang/String;)I
    //   10: ifeq +14 -> 24
    //   13: new 1764	java/lang/SecurityException
    //   16: dup
    //   17: ldc_w 3312
    //   20: invokespecial 1769	java/lang/SecurityException:<init>	(Ljava/lang/String;)V
    //   23: athrow
    //   24: iload_1
    //   25: ifeq +14 -> 39
    //   28: new 1676	java/lang/IllegalArgumentException
    //   31: dup
    //   32: ldc_w 3314
    //   35: invokespecial 1680	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   38: athrow
    //   39: invokestatic 1473	android/os/Binder:getCallingPid	()I
    //   42: invokestatic 1490	android/os/Binder:getCallingUid	()I
    //   45: iload_3
    //   46: iconst_0
    //   47: iconst_1
    //   48: ldc_w 5945
    //   51: aconst_null
    //   52: invokestatic 3321	android/app/ActivityManager:handleIncomingUser	(IIIZZLjava/lang/String;Ljava/lang/String;)I
    //   55: istore_3
    //   56: invokestatic 2686	android/os/Binder:clearCallingIdentity	()J
    //   59: lstore 4
    //   61: aload_0
    //   62: getfield 491	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
    //   65: astore 6
    //   67: aload 6
    //   69: monitorenter
    //   70: aload_0
    //   71: iload_1
    //   72: invokevirtual 1720	com/android/server/wm/WindowManagerService:getDisplayContentLocked	(I)Lcom/android/server/wm/DisplayContent;
    //   75: astore 7
    //   77: aload 7
    //   79: ifnull +18 -> 97
    //   82: aload_0
    //   83: getfield 2172	com/android/server/wm/WindowManagerService:mCurrentUserId	I
    //   86: iload_3
    //   87: if_icmpne +10 -> 97
    //   90: aload_0
    //   91: aload 7
    //   93: iload_2
    //   94: invokespecial 3326	com/android/server/wm/WindowManagerService:setForcedDisplayDensityLocked	(Lcom/android/server/wm/DisplayContent;I)V
    //   97: aload_0
    //   98: getfield 713	com/android/server/wm/WindowManagerService:mContext	Landroid/content/Context;
    //   101: invokevirtual 911	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   104: ldc_w 1863
    //   107: iload_2
    //   108: invokestatic 5947	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   111: iload_3
    //   112: invokestatic 3330	android/provider/Settings$Secure:putStringForUser	(Landroid/content/ContentResolver;Ljava/lang/String;Ljava/lang/String;I)Z
    //   115: pop
    //   116: aload 6
    //   118: monitorexit
    //   119: lload 4
    //   121: invokestatic 2729	android/os/Binder:restoreCallingIdentity	(J)V
    //   124: return
    //   125: astore 7
    //   127: aload 6
    //   129: monitorexit
    //   130: aload 7
    //   132: athrow
    //   133: astore 6
    //   135: lload 4
    //   137: invokestatic 2729	android/os/Binder:restoreCallingIdentity	(J)V
    //   140: aload 6
    //   142: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	143	0	this	WindowManagerService
    //   0	143	1	paramInt1	int
    //   0	143	2	paramInt2	int
    //   0	143	3	paramInt3	int
    //   59	77	4	l	long
    //   133	8	6	localObject1	Object
    //   75	17	7	localDisplayContent	DisplayContent
    //   125	6	7	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   70	77	125	finally
    //   82	97	125	finally
    //   97	116	125	finally
    //   61	70	133	finally
    //   116	119	133	finally
    //   127	133	133	finally
  }
  
  public void setForcedDisplayScalingMode(int paramInt1, int paramInt2)
  {
    if (this.mContext.checkCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS") != 0) {
      throw new SecurityException("Must hold permission android.permission.WRITE_SECURE_SETTINGS");
    }
    if (paramInt1 != 0) {
      throw new IllegalArgumentException("Can only set the default display");
    }
    long l = Binder.clearCallingIdentity();
    for (;;)
    {
      try
      {
        synchronized (this.mWindowMap)
        {
          DisplayContent localDisplayContent = getDisplayContentLocked(paramInt1);
          if (localDisplayContent != null)
          {
            if (paramInt2 >= 0)
            {
              paramInt1 = paramInt2;
              if (paramInt2 <= 1)
              {
                setForcedDisplayScalingModeLocked(localDisplayContent, paramInt1);
                Settings.Global.putInt(this.mContext.getContentResolver(), "display_scaling_force", paramInt1);
              }
            }
          }
          else {
            return;
          }
        }
        paramInt1 = 0;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
  }
  
  /* Error */
  public void setForcedDisplaySize(int paramInt1, int paramInt2, int paramInt3)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 713	com/android/server/wm/WindowManagerService:mContext	Landroid/content/Context;
    //   4: ldc_w 3307
    //   7: invokevirtual 3310	android/content/Context:checkCallingOrSelfPermission	(Ljava/lang/String;)I
    //   10: ifeq +14 -> 24
    //   13: new 1764	java/lang/SecurityException
    //   16: dup
    //   17: ldc_w 3312
    //   20: invokespecial 1769	java/lang/SecurityException:<init>	(Ljava/lang/String;)V
    //   23: athrow
    //   24: iload_1
    //   25: ifeq +14 -> 39
    //   28: new 1676	java/lang/IllegalArgumentException
    //   31: dup
    //   32: ldc_w 3314
    //   35: invokespecial 1680	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   38: athrow
    //   39: invokestatic 2686	android/os/Binder:clearCallingIdentity	()J
    //   42: lstore 4
    //   44: aload_0
    //   45: getfield 491	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
    //   48: astore 6
    //   50: aload 6
    //   52: monitorenter
    //   53: aload_0
    //   54: iload_1
    //   55: invokevirtual 1720	com/android/server/wm/WindowManagerService:getDisplayContentLocked	(I)Lcom/android/server/wm/DisplayContent;
    //   58: astore 7
    //   60: aload 7
    //   62: ifnull +85 -> 147
    //   65: iload_2
    //   66: sipush 200
    //   69: invokestatic 5958	java/lang/Math:max	(II)I
    //   72: aload 7
    //   74: getfield 3334	com/android/server/wm/DisplayContent:mInitialDisplayWidth	I
    //   77: iconst_2
    //   78: imul
    //   79: invokestatic 5961	java/lang/Math:min	(II)I
    //   82: istore_1
    //   83: iload_3
    //   84: sipush 200
    //   87: invokestatic 5958	java/lang/Math:max	(II)I
    //   90: aload 7
    //   92: getfield 3337	com/android/server/wm/DisplayContent:mInitialDisplayHeight	I
    //   95: iconst_2
    //   96: imul
    //   97: invokestatic 5961	java/lang/Math:min	(II)I
    //   100: istore_2
    //   101: aload_0
    //   102: aload 7
    //   104: iload_1
    //   105: iload_2
    //   106: invokespecial 3339	com/android/server/wm/WindowManagerService:setForcedDisplaySizeLocked	(Lcom/android/server/wm/DisplayContent;II)V
    //   109: aload_0
    //   110: getfield 713	com/android/server/wm/WindowManagerService:mContext	Landroid/content/Context;
    //   113: invokevirtual 911	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   116: ldc_w 2151
    //   119: new 1023	java/lang/StringBuilder
    //   122: dup
    //   123: invokespecial 1024	java/lang/StringBuilder:<init>	()V
    //   126: iload_1
    //   127: invokevirtual 1128	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   130: ldc_w 5963
    //   133: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   136: iload_2
    //   137: invokevirtual 1128	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   140: invokevirtual 1046	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   143: invokestatic 3343	android/provider/Settings$Global:putString	(Landroid/content/ContentResolver;Ljava/lang/String;Ljava/lang/String;)Z
    //   146: pop
    //   147: aload 6
    //   149: monitorexit
    //   150: lload 4
    //   152: invokestatic 2729	android/os/Binder:restoreCallingIdentity	(J)V
    //   155: return
    //   156: astore 7
    //   158: aload 6
    //   160: monitorexit
    //   161: aload 7
    //   163: athrow
    //   164: astore 6
    //   166: lload 4
    //   168: invokestatic 2729	android/os/Binder:restoreCallingIdentity	(J)V
    //   171: aload 6
    //   173: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	174	0	this	WindowManagerService
    //   0	174	1	paramInt1	int
    //   0	174	2	paramInt2	int
    //   0	174	3	paramInt3	int
    //   42	125	4	l	long
    //   164	8	6	localObject1	Object
    //   58	45	7	localDisplayContent	DisplayContent
    //   156	6	7	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   53	60	156	finally
    //   65	147	156	finally
    //   44	53	164	finally
    //   147	150	164	finally
    //   158	164	164	finally
  }
  
  void setHoldScreenLocked(Session paramSession)
  {
    if (paramSession != null) {}
    for (int i = 1;; i = 0)
    {
      if ((i != 0) && (this.mHoldingScreenOn != paramSession)) {
        this.mHoldingScreenWakeLock.setWorkSource(new WorkSource(paramSession.mUid));
      }
      this.mHoldingScreenOn = paramSession;
      if (i != this.mHoldingScreenWakeLock.isHeld())
      {
        if (i == 0) {
          break;
        }
        this.mLastWakeLockHoldingWindow = this.mWindowPlacerLocked.mHoldScreenWindow;
        this.mLastWakeLockObscuringWindow = null;
        this.mHoldingScreenWakeLock.acquire();
        this.mPolicy.keepScreenOnStartedLw();
      }
      return;
    }
    this.mLastWakeLockHoldingWindow = null;
    this.mLastWakeLockObscuringWindow = this.mWindowPlacerLocked.mObsuringWindow;
    this.mPolicy.keepScreenOnStoppedLw();
    this.mHoldingScreenWakeLock.release();
  }
  
  public void setInTouchMode(boolean paramBoolean)
  {
    synchronized (this.mWindowMap)
    {
      this.mInTouchMode = paramBoolean;
      return;
    }
  }
  
  /* Error */
  void setInsetsWindow(Session paramSession, IWindow paramIWindow, int paramInt, Rect paramRect1, Rect paramRect2, Region paramRegion)
  {
    // Byte code:
    //   0: invokestatic 2686	android/os/Binder:clearCallingIdentity	()J
    //   3: lstore 7
    //   5: aload_0
    //   6: getfield 491	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
    //   9: astore 9
    //   11: aload 9
    //   13: monitorenter
    //   14: aload_0
    //   15: aload_1
    //   16: aload_2
    //   17: iconst_0
    //   18: invokevirtual 4252	com/android/server/wm/WindowManagerService:windowForClientLocked	(Lcom/android/server/wm/Session;Landroid/view/IWindow;Z)Lcom/android/server/wm/WindowState;
    //   21: astore_1
    //   22: getstatic 4036	com/android/server/wm/WindowManagerDebugConfig:DEBUG_LAYOUT	Z
    //   25: ifeq +125 -> 150
    //   28: getstatic 372	com/android/server/wm/WindowManagerService:TAG	Ljava/lang/String;
    //   31: new 1023	java/lang/StringBuilder
    //   34: dup
    //   35: invokespecial 1024	java/lang/StringBuilder:<init>	()V
    //   38: ldc_w 5994
    //   41: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   44: aload_1
    //   45: invokevirtual 1039	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   48: ldc_w 5996
    //   51: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   54: aload_1
    //   55: getfield 5999	com/android/server/wm/WindowState:mGivenContentInsets	Landroid/graphics/Rect;
    //   58: invokevirtual 1039	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   61: ldc_w 6001
    //   64: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   67: aload 4
    //   69: invokevirtual 1039	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   72: ldc_w 6003
    //   75: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   78: aload_1
    //   79: getfield 6006	com/android/server/wm/WindowState:mGivenVisibleInsets	Landroid/graphics/Rect;
    //   82: invokevirtual 1039	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   85: ldc_w 6001
    //   88: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   91: aload 5
    //   93: invokevirtual 1039	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   96: ldc_w 6008
    //   99: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   102: aload_1
    //   103: getfield 6012	com/android/server/wm/WindowState:mGivenTouchableRegion	Landroid/graphics/Region;
    //   106: invokevirtual 1039	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   109: ldc_w 6001
    //   112: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   115: aload 6
    //   117: invokevirtual 1039	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   120: ldc_w 6014
    //   123: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   126: aload_1
    //   127: getfield 6017	com/android/server/wm/WindowState:mTouchableInsets	I
    //   130: invokevirtual 1128	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   133: ldc_w 6001
    //   136: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   139: iload_3
    //   140: invokevirtual 1128	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   143: invokevirtual 1046	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   146: invokestatic 1184	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   149: pop
    //   150: aload_1
    //   151: ifnull +94 -> 245
    //   154: aload_1
    //   155: iconst_0
    //   156: putfield 3101	com/android/server/wm/WindowState:mGivenInsetsPending	Z
    //   159: aload_1
    //   160: getfield 5999	com/android/server/wm/WindowState:mGivenContentInsets	Landroid/graphics/Rect;
    //   163: aload 4
    //   165: invokevirtual 1283	android/graphics/Rect:set	(Landroid/graphics/Rect;)V
    //   168: aload_1
    //   169: getfield 6006	com/android/server/wm/WindowState:mGivenVisibleInsets	Landroid/graphics/Rect;
    //   172: aload 5
    //   174: invokevirtual 1283	android/graphics/Rect:set	(Landroid/graphics/Rect;)V
    //   177: aload_1
    //   178: getfield 6012	com/android/server/wm/WindowState:mGivenTouchableRegion	Landroid/graphics/Region;
    //   181: aload 6
    //   183: invokevirtual 6022	android/graphics/Region:set	(Landroid/graphics/Region;)Z
    //   186: pop
    //   187: aload_1
    //   188: iload_3
    //   189: putfield 6017	com/android/server/wm/WindowState:mTouchableInsets	I
    //   192: aload_1
    //   193: getfield 6025	com/android/server/wm/WindowState:mGlobalScale	F
    //   196: fconst_1
    //   197: fcmpl
    //   198: ifeq +36 -> 234
    //   201: aload_1
    //   202: getfield 5999	com/android/server/wm/WindowState:mGivenContentInsets	Landroid/graphics/Rect;
    //   205: aload_1
    //   206: getfield 6025	com/android/server/wm/WindowState:mGlobalScale	F
    //   209: invokevirtual 6028	android/graphics/Rect:scale	(F)V
    //   212: aload_1
    //   213: getfield 6006	com/android/server/wm/WindowState:mGivenVisibleInsets	Landroid/graphics/Rect;
    //   216: aload_1
    //   217: getfield 6025	com/android/server/wm/WindowState:mGlobalScale	F
    //   220: invokevirtual 6028	android/graphics/Rect:scale	(F)V
    //   223: aload_1
    //   224: getfield 6012	com/android/server/wm/WindowState:mGivenTouchableRegion	Landroid/graphics/Region;
    //   227: aload_1
    //   228: getfield 6025	com/android/server/wm/WindowState:mGlobalScale	F
    //   231: invokevirtual 6029	android/graphics/Region:scale	(F)V
    //   234: aload_1
    //   235: invokevirtual 2389	com/android/server/wm/WindowState:setDisplayLayoutNeeded	()V
    //   238: aload_0
    //   239: getfield 779	com/android/server/wm/WindowManagerService:mWindowPlacerLocked	Lcom/android/server/wm/WindowSurfacePlacer;
    //   242: invokevirtual 2279	com/android/server/wm/WindowSurfacePlacer:performSurfacePlacement	()V
    //   245: aload 9
    //   247: monitorexit
    //   248: lload 7
    //   250: invokestatic 2729	android/os/Binder:restoreCallingIdentity	(J)V
    //   253: return
    //   254: astore_1
    //   255: aload 9
    //   257: monitorexit
    //   258: aload_1
    //   259: athrow
    //   260: astore_1
    //   261: lload 7
    //   263: invokestatic 2729	android/os/Binder:restoreCallingIdentity	(J)V
    //   266: aload_1
    //   267: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	268	0	this	WindowManagerService
    //   0	268	1	paramSession	Session
    //   0	268	2	paramIWindow	IWindow
    //   0	268	3	paramInt	int
    //   0	268	4	paramRect1	Rect
    //   0	268	5	paramRect2	Rect
    //   0	268	6	paramRegion	Region
    //   3	259	7	l	long
    // Exception table:
    //   from	to	target	type
    //   14	150	254	finally
    //   154	234	254	finally
    //   234	245	254	finally
    //   5	14	260	finally
    //   245	248	260	finally
    //   255	260	260	finally
  }
  
  public int[] setNewConfiguration(Configuration paramConfiguration)
  {
    if (!checkCallingPermission("android.permission.MANAGE_APP_TOKENS", "setNewConfiguration()")) {
      throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
    }
    synchronized (this.mWindowMap)
    {
      if (this.mWaitingForConfig)
      {
        this.mWaitingForConfig = false;
        this.mLastFinishedFreezeSource = "new-config";
      }
      int i = this.mCurConfiguration.diff(paramConfiguration);
      if (i != 0) {}
      for (i = 1; i == 0; i = 0) {
        return null;
      }
      prepareFreezingAllTaskBounds();
      this.mCurConfiguration = new Configuration(paramConfiguration);
      paramConfiguration = onConfigurationChanged();
      return paramConfiguration;
    }
  }
  
  /* Error */
  public void setOverscan(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 713	com/android/server/wm/WindowManagerService:mContext	Landroid/content/Context;
    //   4: ldc_w 3307
    //   7: invokevirtual 3310	android/content/Context:checkCallingOrSelfPermission	(Ljava/lang/String;)I
    //   10: ifeq +14 -> 24
    //   13: new 1764	java/lang/SecurityException
    //   16: dup
    //   17: ldc_w 3312
    //   20: invokespecial 1769	java/lang/SecurityException:<init>	(Ljava/lang/String;)V
    //   23: athrow
    //   24: invokestatic 2686	android/os/Binder:clearCallingIdentity	()J
    //   27: lstore 6
    //   29: aload_0
    //   30: getfield 491	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
    //   33: astore 8
    //   35: aload 8
    //   37: monitorenter
    //   38: aload_0
    //   39: iload_1
    //   40: invokevirtual 1720	com/android/server/wm/WindowManagerService:getDisplayContentLocked	(I)Lcom/android/server/wm/DisplayContent;
    //   43: astore 9
    //   45: aload 9
    //   47: ifnull +15 -> 62
    //   50: aload_0
    //   51: aload 9
    //   53: iload_2
    //   54: iload_3
    //   55: iload 4
    //   57: iload 5
    //   59: invokespecial 6043	com/android/server/wm/WindowManagerService:setOverscanLocked	(Lcom/android/server/wm/DisplayContent;IIII)V
    //   62: aload 8
    //   64: monitorexit
    //   65: lload 6
    //   67: invokestatic 2729	android/os/Binder:restoreCallingIdentity	(J)V
    //   70: return
    //   71: astore 9
    //   73: aload 8
    //   75: monitorexit
    //   76: aload 9
    //   78: athrow
    //   79: astore 8
    //   81: lload 6
    //   83: invokestatic 2729	android/os/Binder:restoreCallingIdentity	(J)V
    //   86: aload 8
    //   88: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	89	0	this	WindowManagerService
    //   0	89	1	paramInt1	int
    //   0	89	2	paramInt2	int
    //   0	89	3	paramInt3	int
    //   0	89	4	paramInt4	int
    //   0	89	5	paramInt5	int
    //   27	55	6	l	long
    //   79	8	8	localObject1	Object
    //   43	9	9	localDisplayContent	DisplayContent
    //   71	6	9	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   38	45	71	finally
    //   50	62	71	finally
    //   29	38	79	finally
    //   62	65	79	finally
    //   73	79	79	finally
  }
  
  public void setRecentsVisibility(boolean paramBoolean)
  {
    if (this.mContext.checkCallingOrSelfPermission("android.permission.STATUS_BAR") != 0) {
      throw new SecurityException("Caller does not hold permission android.permission.STATUS_BAR");
    }
    synchronized (this.mWindowMap)
    {
      this.mPolicy.setRecentsVisibilityLw(paramBoolean);
      return;
    }
  }
  
  public void setReplacingWindow(IBinder paramIBinder, boolean paramBoolean)
  {
    synchronized (this.mWindowMap)
    {
      AppWindowToken localAppWindowToken = findAppWindowToken(paramIBinder);
      if ((localAppWindowToken != null) && (localAppWindowToken.isVisible()))
      {
        localAppWindowToken.setReplacingWindows(paramBoolean);
        return;
      }
      Slog.w("WindowManager", "Attempted to set replacing window on non-existing app token " + paramIBinder);
      return;
    }
  }
  
  public void setReplacingWindows(IBinder paramIBinder, boolean paramBoolean)
  {
    synchronized (this.mWindowMap)
    {
      AppWindowToken localAppWindowToken = findAppWindowToken(paramIBinder);
      if ((localAppWindowToken != null) && (localAppWindowToken.isVisible()))
      {
        if (paramBoolean)
        {
          localAppWindowToken.setReplacingChildren();
          scheduleClearReplacingWindowIfNeeded(paramIBinder, true);
        }
      }
      else
      {
        Slog.w("WindowManager", "Attempted to set replacing window on non-existing app token " + paramIBinder);
        return;
      }
      localAppWindowToken.setReplacingWindows(false);
    }
  }
  
  public void setResizeDimLayer(boolean paramBoolean, int paramInt, float paramFloat)
  {
    synchronized (this.mWindowMap)
    {
      getDefaultDisplayContentLocked().getDockedDividerController().setResizeDimLayer(paramBoolean, paramInt, paramFloat);
      return;
    }
  }
  
  public void setScreenCaptureDisabled(int paramInt, boolean paramBoolean)
  {
    if (Binder.getCallingUid() != 1000) {
      throw new SecurityException("Only system can call setScreenCaptureDisabled.");
    }
    synchronized (this.mWindowMap)
    {
      this.mScreenCaptureDisabled.put(paramInt, Boolean.valueOf(paramBoolean));
      int i = this.mDisplayContents.size() - 1;
      while (i >= 0)
      {
        WindowList localWindowList = ((DisplayContent)this.mDisplayContents.valueAt(i)).getWindowList();
        int j = localWindowList.size() - 1;
        while (j >= 0)
        {
          WindowState localWindowState = (WindowState)localWindowList.get(j);
          if ((localWindowState.mHasSurface) && (paramInt == UserHandle.getUserId(localWindowState.mOwnerUid))) {
            localWindowState.mWinAnimator.setSecureLocked(paramBoolean);
          }
          j -= 1;
        }
        i -= 1;
      }
      return;
    }
  }
  
  public void setStrictModeVisualIndicatorPreference(String paramString)
  {
    SystemProperties.set("persist.sys.strictmode.visual", paramString);
  }
  
  public void setTaskDockedResizing(int paramInt, boolean paramBoolean)
  {
    synchronized (this.mWindowMap)
    {
      Task localTask = (Task)this.mTaskIdToTask.get(paramInt);
      if (localTask == null)
      {
        Slog.w(TAG, "setTaskDockedResizing: taskId " + paramInt + " not found.");
        return;
      }
      localTask.setDragResizing(paramBoolean, 1);
      return;
    }
  }
  
  public void setTaskResizeable(int paramInt1, int paramInt2)
  {
    synchronized (this.mWindowMap)
    {
      Task localTask = (Task)this.mTaskIdToTask.get(paramInt1);
      if (localTask != null) {
        localTask.setResizeable(paramInt2);
      }
      return;
    }
  }
  
  boolean setTokenVisibilityLocked(AppWindowToken paramAppWindowToken, WindowManager.LayoutParams paramLayoutParams, boolean paramBoolean1, int paramInt, boolean paramBoolean2, boolean paramBoolean3)
  {
    boolean bool4 = false;
    boolean bool5 = false;
    boolean bool3 = false;
    boolean bool1;
    int i;
    boolean bool2;
    int j;
    int k;
    if (paramAppWindowToken.clientHidden == paramBoolean1)
    {
      if (paramBoolean1)
      {
        bool1 = false;
        paramAppWindowToken.clientHidden = bool1;
        paramAppWindowToken.sendAppVisibilityToClients();
      }
    }
    else
    {
      i = 0;
      if ((paramAppWindowToken.hidden != paramBoolean1) && ((!paramAppWindowToken.hidden) || (!paramAppWindowToken.mIsExiting)))
      {
        bool2 = bool5;
        j = i;
        if (!paramBoolean1) {
          break label682;
        }
        bool2 = bool5;
        j = i;
        if (!paramAppWindowToken.waitingForReplacement()) {
          break label682;
        }
      }
      i = 0;
      if (WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS) {
        Slog.v("WindowManager", "Changing app " + paramAppWindowToken + " hidden=" + paramAppWindowToken.hidden + " performLayout=" + paramBoolean2);
      }
      j = 0;
      k = 0;
      bool1 = bool4;
      if (paramInt != -1)
      {
        if (paramAppWindowToken.mAppAnimator.animation == AppWindowAnimator.sDummyAnimation) {
          paramAppWindowToken.mAppAnimator.setNullAnimation();
        }
        bool1 = bool3;
        i = k;
        if (applyAnimationLocked(paramAppWindowToken, paramLayoutParams, paramInt, paramBoolean1, paramBoolean3))
        {
          i = 1;
          bool1 = true;
        }
        paramLayoutParams = paramAppWindowToken.findMainWindow();
        if ((paramLayoutParams != null) && (this.mAccessibilityController != null) && (paramLayoutParams.getDisplayId() == 0)) {
          this.mAccessibilityController.onAppWindowTransitionLocked(paramLayoutParams, paramInt);
        }
        paramInt = 1;
        j = i;
        i = paramInt;
      }
      int m = paramAppWindowToken.allAppWindows.size();
      k = 0;
      label266:
      if (k >= m) {
        break label535;
      }
      paramLayoutParams = (WindowState)paramAppWindowToken.allAppWindows.get(k);
      if (paramLayoutParams != paramAppWindowToken.startingWindow) {
        break label365;
      }
      paramInt = i;
      if (!paramBoolean1)
      {
        paramInt = i;
        if (paramLayoutParams.isVisibleNow())
        {
          paramInt = i;
          if (paramAppWindowToken.mAppAnimator.isAnimating())
          {
            paramLayoutParams.mAnimatingExit = true;
            paramLayoutParams.mRemoveOnExit = true;
            paramLayoutParams.mWindowRemovalAllowed = true;
            paramInt = i;
          }
        }
      }
    }
    for (;;)
    {
      k += 1;
      i = paramInt;
      break label266;
      bool1 = true;
      break;
      label365:
      if ((paramBoolean1) && (paramLayoutParams.hasJustMovedInStack()))
      {
        if (WindowManagerDebugConfig.DEBUG_ONEPLUS) {
          Slog.d("WindowManager", "setTokenVisibilityLocked reset mJustMovedInStack win=" + paramLayoutParams);
        }
        paramLayoutParams.resetJustMovedInStack();
      }
      if (paramBoolean1)
      {
        paramInt = i;
        if (!paramLayoutParams.isVisibleNow())
        {
          if (j == 0)
          {
            paramLayoutParams.mWinAnimator.applyAnimationLocked(1, true);
            if ((this.mAccessibilityController != null) && (paramLayoutParams.getDisplayId() == 0)) {
              this.mAccessibilityController.onWindowTransitionLocked(paramLayoutParams, 1);
            }
          }
          paramInt = 1;
          paramLayoutParams.setDisplayLayoutNeeded();
        }
      }
      else
      {
        paramInt = i;
        if (paramLayoutParams.isVisibleNow())
        {
          if (j == 0)
          {
            paramLayoutParams.mWinAnimator.applyAnimationLocked(2, false);
            if ((this.mAccessibilityController != null) && (paramLayoutParams.getDisplayId() == 0)) {
              this.mAccessibilityController.onWindowTransitionLocked(paramLayoutParams, 2);
            }
          }
          paramInt = 1;
          paramLayoutParams.setDisplayLayoutNeeded();
        }
      }
    }
    label535:
    if (paramBoolean1)
    {
      paramBoolean3 = false;
      paramAppWindowToken.hiddenRequested = paramBoolean3;
      paramAppWindowToken.hidden = paramBoolean3;
      paramInt = 1;
      if (paramBoolean1) {
        break label761;
      }
      unsetAppFreezingScreenLocked(paramAppWindowToken, true, true);
      label568:
      if (WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS) {
        Slog.v("WindowManager", "setTokenVisibilityLocked: " + paramAppWindowToken + ": hidden=" + paramAppWindowToken.hidden + " hiddenRequested=" + paramAppWindowToken.hiddenRequested);
      }
      bool2 = bool1;
      j = paramInt;
      if (i != 0)
      {
        this.mInputMonitor.setUpdateInputWindowsNeededLw();
        if (paramBoolean2)
        {
          updateFocusedWindowLocked(3, false);
          this.mWindowPlacerLocked.performSurfacePlacement();
        }
        this.mInputMonitor.updateInputWindowsLw(false);
        j = paramInt;
        bool2 = bool1;
      }
      label682:
      if (paramAppWindowToken.mAppAnimator.animation != null) {
        bool2 = true;
      }
      paramInt = paramAppWindowToken.allAppWindows.size() - 1;
      label706:
      if ((paramInt >= 0) && (!bool2)) {
        break label790;
      }
      if (j != 0) {
        if ((paramBoolean1) && (!bool2)) {
          break label823;
        }
      }
    }
    for (;;)
    {
      if ((!this.mClosingApps.contains(paramAppWindowToken)) && (!this.mOpeningApps.contains(paramAppWindowToken))) {
        break label842;
      }
      return bool2;
      paramBoolean3 = true;
      break;
      label761:
      paramLayoutParams = paramAppWindowToken.startingWindow;
      if ((paramLayoutParams == null) || (paramLayoutParams.isDrawnLw())) {
        break label568;
      }
      paramLayoutParams.mPolicyVisibility = false;
      paramLayoutParams.mPolicyVisibilityAfterAnim = false;
      break label568;
      label790:
      if (((WindowState)paramAppWindowToken.allAppWindows.get(paramInt)).mWinAnimator.isWindowAnimationSet()) {
        bool2 = true;
      }
      paramInt -= 1;
      break label706;
      label823:
      paramAppWindowToken.mEnteringAnimation = true;
      this.mActivityManagerAppTransitionNotifier.onAppTransitionFinishedLocked(paramAppWindowToken.token);
    }
    label842:
    getDefaultDisplayContentLocked().getDockedDividerController().notifyAppVisibilityChanged();
    return bool2;
  }
  
  /* Error */
  void setTransparentRegionWindow(Session paramSession, IWindow paramIWindow, Region paramRegion)
  {
    // Byte code:
    //   0: invokestatic 2686	android/os/Binder:clearCallingIdentity	()J
    //   3: lstore 4
    //   5: aload_0
    //   6: getfield 491	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
    //   9: astore 6
    //   11: aload 6
    //   13: monitorenter
    //   14: aload_0
    //   15: aload_1
    //   16: aload_2
    //   17: iconst_0
    //   18: invokevirtual 4252	com/android/server/wm/WindowManagerService:windowForClientLocked	(Lcom/android/server/wm/Session;Landroid/view/IWindow;Z)Lcom/android/server/wm/WindowState;
    //   21: astore_1
    //   22: getstatic 1647	com/android/server/wm/WindowManagerDebugConfig:SHOW_TRANSACTIONS	Z
    //   25: ifeq +28 -> 53
    //   28: aload_1
    //   29: new 1023	java/lang/StringBuilder
    //   32: dup
    //   33: invokespecial 1024	java/lang/StringBuilder:<init>	()V
    //   36: ldc_w 6130
    //   39: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   42: aload_3
    //   43: invokevirtual 1039	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   46: invokevirtual 1046	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   49: iconst_0
    //   50: invokestatic 5029	com/android/server/wm/WindowManagerService:logSurface	(Lcom/android/server/wm/WindowState;Ljava/lang/String;Z)V
    //   53: aload_1
    //   54: ifnull +18 -> 72
    //   57: aload_1
    //   58: getfield 1634	com/android/server/wm/WindowState:mHasSurface	Z
    //   61: ifeq +11 -> 72
    //   64: aload_1
    //   65: getfield 1398	com/android/server/wm/WindowState:mWinAnimator	Lcom/android/server/wm/WindowStateAnimator;
    //   68: aload_3
    //   69: invokevirtual 6134	com/android/server/wm/WindowStateAnimator:setTransparentRegionHintLocked	(Landroid/graphics/Region;)V
    //   72: aload 6
    //   74: monitorexit
    //   75: lload 4
    //   77: invokestatic 2729	android/os/Binder:restoreCallingIdentity	(J)V
    //   80: return
    //   81: astore_1
    //   82: aload 6
    //   84: monitorexit
    //   85: aload_1
    //   86: athrow
    //   87: astore_1
    //   88: lload 4
    //   90: invokestatic 2729	android/os/Binder:restoreCallingIdentity	(J)V
    //   93: aload_1
    //   94: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	95	0	this	WindowManagerService
    //   0	95	1	paramSession	Session
    //   0	95	2	paramIWindow	IWindow
    //   0	95	3	paramRegion	Region
    //   3	86	4	l	long
    // Exception table:
    //   from	to	target	type
    //   14	53	81	finally
    //   57	72	81	finally
    //   5	14	87	finally
    //   72	75	87	finally
    //   82	87	87	finally
  }
  
  public void setTvPipVisibility(boolean paramBoolean)
  {
    if (this.mContext.checkCallingOrSelfPermission("android.permission.STATUS_BAR") != 0) {
      throw new SecurityException("Caller does not hold permission android.permission.STATUS_BAR");
    }
    synchronized (this.mWindowMap)
    {
      this.mPolicy.setTvPipVisibilityLw(paramBoolean);
      return;
    }
  }
  
  public void setWindowOpaque(IBinder paramIBinder, boolean paramBoolean)
  {
    synchronized (this.mWindowMap)
    {
      setWindowOpaqueLocked(paramIBinder, paramBoolean);
      return;
    }
  }
  
  public void setWindowOpaqueLocked(IBinder paramIBinder, boolean paramBoolean)
  {
    paramIBinder = findAppWindowToken(paramIBinder);
    if (paramIBinder != null)
    {
      paramIBinder = paramIBinder.findMainWindow();
      if (paramIBinder != null) {
        paramIBinder.mWinAnimator.setOpaqueLocked(paramBoolean);
      }
    }
  }
  
  public void showBootMessage(CharSequence paramCharSequence, boolean paramBoolean)
  {
    int i = 0;
    synchronized (this.mWindowMap)
    {
      if (WindowManagerDebugConfig.DEBUG_BOOT)
      {
        RuntimeException localRuntimeException = new RuntimeException("here");
        localRuntimeException.fillInStackTrace();
        Slog.i("WindowManager", "showBootMessage: msg=" + paramCharSequence + " always=" + paramBoolean + " mAllowBootMessages=" + this.mAllowBootMessages + " mShowingBootMessages=" + this.mShowingBootMessages + " mSystemBooted=" + this.mSystemBooted, localRuntimeException);
      }
      boolean bool = this.mAllowBootMessages;
      if (!bool) {
        return;
      }
      bool = this.mShowingBootMessages;
      if (!bool)
      {
        if (!paramBoolean) {
          return;
        }
        i = 1;
      }
      bool = this.mSystemBooted;
      if (bool) {
        return;
      }
      this.mShowingBootMessages = true;
      this.mPolicy.showBootMessage(paramCharSequence, paramBoolean);
      if (i != 0) {
        performEnableScreen();
      }
      return;
    }
  }
  
  public void showCircularMask(boolean paramBoolean)
  {
    synchronized (this.mWindowMap)
    {
      if (WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
        Slog.i("WindowManager", ">>> OPEN TRANSACTION showCircularMask(visible=" + paramBoolean + ")");
      }
      SurfaceControl.openTransaction();
      if (paramBoolean) {}
      do
      {
        try
        {
          if (this.mCircularDisplayMask == null)
          {
            int i = this.mContext.getResources().getInteger(17694870);
            int j = this.mContext.getResources().getDimensionPixelSize(17105059);
            this.mCircularDisplayMask = new CircularDisplayMask(getDefaultDisplayContentLocked().getDisplay(), this.mFxSession, this.mPolicy.windowTypeToLayerLw(2018) * 10000 + 10, i, j);
          }
          this.mCircularDisplayMask.setVisibility(true);
          SurfaceControl.closeTransaction();
          if (WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
            Slog.i("WindowManager", "<<< CLOSE TRANSACTION showCircularMask(visible=" + paramBoolean + ")");
          }
          return;
        }
        finally
        {
          SurfaceControl.closeTransaction();
          if (!WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
            break;
          }
          Slog.i("WindowManager", "<<< CLOSE TRANSACTION showCircularMask(visible=" + paramBoolean + ")");
        }
      } while (this.mCircularDisplayMask == null);
      this.mCircularDisplayMask.setVisibility(false);
      this.mCircularDisplayMask = null;
    }
  }
  
  public void showEmulatorDisplayOverlay()
  {
    synchronized (this.mWindowMap)
    {
      if (WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
        Slog.i("WindowManager", ">>> OPEN TRANSACTION showEmulatorDisplayOverlay");
      }
      SurfaceControl.openTransaction();
      try
      {
        if (this.mEmulatorDisplayOverlay == null) {
          this.mEmulatorDisplayOverlay = new EmulatorDisplayOverlay(this.mContext, getDefaultDisplayContentLocked().getDisplay(), this.mFxSession, this.mPolicy.windowTypeToLayerLw(2018) * 10000 + 10);
        }
        this.mEmulatorDisplayOverlay.setVisibility(true);
        SurfaceControl.closeTransaction();
        if (WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
          Slog.i("WindowManager", "<<< CLOSE TRANSACTION showEmulatorDisplayOverlay");
        }
        return;
      }
      finally
      {
        SurfaceControl.closeTransaction();
        if (WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
          Slog.i("WindowManager", "<<< CLOSE TRANSACTION showEmulatorDisplayOverlay");
        }
      }
    }
  }
  
  public void showEmulatorDisplayOverlayIfNeeded()
  {
    if ((this.mContext.getResources().getBoolean(17957005)) && (SystemProperties.getBoolean("ro.emulator.circular", false)) && (Build.IS_EMULATOR)) {
      this.mH.sendMessage(this.mH.obtainMessage(36));
    }
  }
  
  void showGlobalActions()
  {
    this.mPolicy.showGlobalActions();
  }
  
  public void showRecentApps(boolean paramBoolean)
  {
    this.mPolicy.showRecentApps(paramBoolean);
  }
  
  public void showStrictModeViolation(boolean paramBoolean)
  {
    int j = Binder.getCallingPid();
    H localH1 = this.mH;
    H localH2 = this.mH;
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localH1.sendMessage(localH2.obtainMessage(25, i, j));
      return;
    }
  }
  
  public void shutdown(boolean paramBoolean)
  {
    ShutdownThread.shutdown(this.mContext, "userrequested", paramBoolean);
  }
  
  public void startAppFreezingScreen(IBinder paramIBinder, int paramInt)
  {
    if (!checkCallingPermission("android.permission.MANAGE_APP_TOKENS", "setAppFreezingScreen()")) {
      throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
    }
    HashMap localHashMap = this.mWindowMap;
    if (paramInt == 0) {}
    try
    {
      if (okToDisplay())
      {
        if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
          Slog.v("WindowManager", "Skipping set freeze of " + paramIBinder);
        }
        return;
      }
      paramIBinder = findAppWindowToken(paramIBinder);
      if ((paramIBinder == null) || (paramIBinder.appToken == null))
      {
        Slog.w("WindowManager", "Attempted to freeze screen with non-existing app token: " + paramIBinder);
        return;
      }
      long l = Binder.clearCallingIdentity();
      startAppFreezingScreenLocked(paramIBinder);
      Binder.restoreCallingIdentity(l);
      return;
    }
    finally {}
  }
  
  public void startFreezingScreen(int paramInt1, int paramInt2)
  {
    if (!checkCallingPermission("android.permission.FREEZE_SCREEN", "startFreezingScreen()")) {
      throw new SecurityException("Requires FREEZE_SCREEN permission");
    }
    synchronized (this.mWindowMap)
    {
      long l;
      if (!this.mClientFreezingScreen)
      {
        this.mClientFreezingScreen = true;
        l = Binder.clearCallingIdentity();
      }
      try
      {
        startFreezingDisplayLocked(false, paramInt1, paramInt2);
        this.mH.removeMessages(30);
        this.mH.sendEmptyMessageDelayed(30, 5000L);
        Binder.restoreCallingIdentity(l);
        return;
      }
      finally
      {
        localObject1 = finally;
        Binder.restoreCallingIdentity(l);
        throw ((Throwable)localObject1);
      }
    }
  }
  
  boolean startMovingTask(IWindow paramIWindow, float paramFloat1, float paramFloat2)
  {
    synchronized (this.mWindowMap)
    {
      paramIWindow = windowForClientLocked(null, paramIWindow, false);
      boolean bool = startPositioningLocked(paramIWindow, false, paramFloat1, paramFloat2);
      if (!bool) {
        return false;
      }
    }
    try
    {
      this.mActivityManager.setFocusedTask(paramIWindow.getTask().mTaskId);
      return true;
      paramIWindow = finally;
      throw paramIWindow;
    }
    catch (RemoteException paramIWindow)
    {
      for (;;) {}
    }
  }
  
  public boolean startViewServer(int paramInt)
  {
    if (isSystemSecure()) {
      return false;
    }
    if (!checkCallingPermission("android.permission.DUMP", "startViewServer")) {
      return false;
    }
    if (paramInt < 1024) {
      return false;
    }
    boolean bool;
    if (this.mViewServer != null)
    {
      if (!this.mViewServer.isRunning()) {
        try
        {
          bool = this.mViewServer.start();
          return bool;
        }
        catch (IOException localIOException1)
        {
          Slog.w("WindowManager", "View server did not start");
        }
      }
      return false;
    }
    try
    {
      this.mViewServer = new ViewServer(this, paramInt);
      bool = this.mViewServer.start();
      return bool;
    }
    catch (IOException localIOException2)
    {
      Slog.w("WindowManager", "View server did not start");
    }
    return false;
  }
  
  public void statusBarVisibilityChanged(int paramInt)
  {
    if (this.mContext.checkCallingOrSelfPermission("android.permission.STATUS_BAR") != 0) {
      throw new SecurityException("Caller does not hold permission android.permission.STATUS_BAR");
    }
    synchronized (this.mWindowMap)
    {
      this.mLastStatusBarVisibility = paramInt;
      updateStatusBarVisibilityLocked(this.mPolicy.adjustSystemUiVisibilityLw(paramInt));
      return;
    }
  }
  
  public void stopAppFreezingScreen(IBinder paramIBinder, boolean paramBoolean)
  {
    if (!checkCallingPermission("android.permission.MANAGE_APP_TOKENS", "setAppFreezingScreen()")) {
      throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
    }
    synchronized (this.mWindowMap)
    {
      AppWindowToken localAppWindowToken = findAppWindowToken(paramIBinder);
      if (localAppWindowToken != null)
      {
        IApplicationToken localIApplicationToken = localAppWindowToken.appToken;
        if (localIApplicationToken != null) {}
      }
      else
      {
        return;
      }
      long l = Binder.clearCallingIdentity();
      if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
        Slog.v("WindowManager", "Clear freezing of " + paramIBinder + ": hidden=" + localAppWindowToken.hidden + " freezing=" + localAppWindowToken.mAppAnimator.freezingScreen);
      }
      unsetAppFreezingScreenLocked(localAppWindowToken, true, paramBoolean);
      Binder.restoreCallingIdentity(l);
      return;
    }
  }
  
  void stopFreezingDisplayLocked()
  {
    if (!this.mDisplayFrozen) {
      return;
    }
    if ((this.mWaitingForConfig) || (this.mAppsFreezingScreen > 0)) {}
    while ((this.mWindowsFreezingScreen == 1) || (this.mClientFreezingScreen) || (!this.mOpeningApps.isEmpty()))
    {
      if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
        Slog.d("WindowManager", "stopFreezingDisplayLocked: Returning mWaitingForConfig=" + this.mWaitingForConfig + ", mAppsFreezingScreen=" + this.mAppsFreezingScreen + ", mWindowsFreezingScreen=" + this.mWindowsFreezingScreen + ", mClientFreezingScreen=" + this.mClientFreezingScreen + ", mOpeningApps.size()=" + this.mOpeningApps.size());
      }
      return;
    }
    if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
      Slog.d("WindowManager", "stopFreezingDisplayLocked: Unfreezing now");
    }
    this.mDisplayFrozen = false;
    this.mLastDisplayFreezeDuration = ((int)(SystemClock.elapsedRealtime() - this.mDisplayFreezeTime));
    Object localObject = new StringBuilder(128);
    ((StringBuilder)localObject).append("Screen frozen for ");
    TimeUtils.formatDuration(this.mLastDisplayFreezeDuration, (StringBuilder)localObject);
    if (this.mLastFinishedFreezeSource != null)
    {
      ((StringBuilder)localObject).append(" due to ");
      ((StringBuilder)localObject).append(this.mLastFinishedFreezeSource);
    }
    Slog.i("WindowManager", ((StringBuilder)localObject).toString());
    this.mH.removeMessages(17);
    this.mH.removeMessages(30);
    if (PROFILE_ORIENTATION) {
      Debug.stopMethodTracing();
    }
    int i = 0;
    localObject = getDefaultDisplayContentLocked();
    int j = ((DisplayContent)localObject).getDisplayId();
    ScreenRotationAnimation localScreenRotationAnimation = this.mAnimator.getScreenRotationAnimationLocked(j);
    boolean bool1;
    if ((localScreenRotationAnimation != null) && (localScreenRotationAnimation.hasScreenshot()))
    {
      if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
        Slog.i("WindowManager", "**** Dismissing screen rotation animation");
      }
      DisplayInfo localDisplayInfo = ((DisplayContent)localObject).getDisplayInfo();
      bool1 = ((DisplayContent)localObject).isDimming();
      if (!this.mPolicy.validateRotationAnimationLw(this.mExitAnimId, this.mEnterAnimId, bool1))
      {
        this.mEnterAnimId = 0;
        this.mExitAnimId = 0;
      }
      if (localScreenRotationAnimation.dismiss(this.mFxSession, 10000L, getTransitionAnimationScaleLocked(), localDisplayInfo.logicalWidth, localDisplayInfo.logicalHeight, this.mExitAnimId, this.mEnterAnimId)) {
        scheduleAnimationLocked();
      }
    }
    for (;;)
    {
      this.mInputMonitor.thawInputDispatchingLw();
      boolean bool2 = updateOrientationFromAppTokensLocked(false);
      this.mH.removeMessages(15);
      this.mH.sendEmptyMessageDelayed(15, 2000L);
      this.mScreenFrozenLock.release();
      bool1 = bool2;
      if (i != 0)
      {
        if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
          Slog.d("WindowManager", "Performing post-rotate rotation");
        }
        bool1 = bool2 | updateRotationUncheckedLocked(false);
      }
      if (bool1) {
        this.mH.sendEmptyMessage(18);
      }
      return;
      localScreenRotationAnimation.kill();
      this.mAnimator.setScreenRotationAnimationLocked(j, null);
      i = 1;
      continue;
      if (localScreenRotationAnimation != null)
      {
        localScreenRotationAnimation.kill();
        this.mAnimator.setScreenRotationAnimationLocked(j, null);
      }
      i = 1;
    }
  }
  
  public void stopFreezingScreen()
  {
    if (!checkCallingPermission("android.permission.FREEZE_SCREEN", "stopFreezingScreen()")) {
      throw new SecurityException("Requires FREEZE_SCREEN permission");
    }
    synchronized (this.mWindowMap)
    {
      long l;
      if (this.mClientFreezingScreen)
      {
        this.mClientFreezingScreen = false;
        this.mLastFinishedFreezeSource = "client";
        l = Binder.clearCallingIdentity();
      }
      try
      {
        stopFreezingDisplayLocked();
        Binder.restoreCallingIdentity(l);
        return;
      }
      finally
      {
        localObject1 = finally;
        Binder.restoreCallingIdentity(l);
        throw ((Throwable)localObject1);
      }
    }
  }
  
  void stopUsingSavedSurfaceLocked()
  {
    int i = this.mFinishedEarlyAnim.size() - 1;
    while (i >= 0)
    {
      ((AppWindowToken)this.mFinishedEarlyAnim.get(i)).stopUsingSavedSurfaceLocked();
      i -= 1;
    }
    this.mFinishedEarlyAnim.clear();
  }
  
  public boolean stopViewServer()
  {
    if (isSystemSecure()) {
      return false;
    }
    if (!checkCallingPermission("android.permission.DUMP", "stopViewServer")) {
      return false;
    }
    if (this.mViewServer != null) {
      return this.mViewServer.stop();
    }
    return false;
  }
  
  void subtractInsets(Rect paramRect1, Rect paramRect2, Rect paramRect3)
  {
    this.mTmpRect3.set(paramRect1);
    this.mTmpRect3.inset(paramRect2);
    paramRect3.intersect(this.mTmpRect3);
  }
  
  public void subtractNonDecorInsets(Rect paramRect)
  {
    synchronized (this.mWindowMap)
    {
      getNonDecorInsetsLocked(this.mTmpRect2);
      DisplayInfo localDisplayInfo = getDefaultDisplayInfoLocked();
      this.mTmpRect.set(0, 0, localDisplayInfo.logicalWidth, localDisplayInfo.logicalHeight);
      subtractInsets(this.mTmpRect, this.mTmpRect2, paramRect);
      return;
    }
  }
  
  public void subtractStableInsets(Rect paramRect)
  {
    synchronized (this.mWindowMap)
    {
      getStableInsetsLocked(this.mTmpRect2);
      DisplayInfo localDisplayInfo = getDefaultDisplayInfoLocked();
      this.mTmpRect.set(0, 0, localDisplayInfo.logicalWidth, localDisplayInfo.logicalHeight);
      subtractInsets(this.mTmpRect, this.mTmpRect2, paramRect);
      return;
    }
  }
  
  public void switchInputMethod(boolean paramBoolean)
  {
    InputMethodManagerInternal localInputMethodManagerInternal = (InputMethodManagerInternal)LocalServices.getService(InputMethodManagerInternal.class);
    if (localInputMethodManagerInternal != null) {
      localInputMethodManagerInternal.switchInputMethod(paramBoolean);
    }
  }
  
  public void systemReady()
  {
    this.mPolicy.systemReady();
  }
  
  public void thawRotation()
  {
    if (!checkCallingPermission("android.permission.SET_ORIENTATION", "thawRotation()")) {
      throw new SecurityException("Requires SET_ORIENTATION permission");
    }
    if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
      Slog.v("WindowManager", "thawRotation: mRotation=" + this.mRotation);
    }
    long l = Binder.clearCallingIdentity();
    try
    {
      this.mPolicy.setUserRotationMode(0, 777);
      Binder.restoreCallingIdentity(l);
      updateRotationUnchecked(false, false);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public void unregisterPointerEventListener(WindowManagerPolicy.PointerEventListener paramPointerEventListener)
  {
    this.mPointerEventDispatcher.unregisterInputEventListener(paramPointerEventListener);
  }
  
  void unsetAppFreezingScreenLocked(AppWindowToken paramAppWindowToken, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramAppWindowToken.mAppAnimator.freezingScreen)
    {
      if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
        Slog.v("WindowManager", "Clear freezing of " + paramAppWindowToken + " force=" + paramBoolean2);
      }
      int k = paramAppWindowToken.allAppWindows.size();
      int j = 0;
      int i = 0;
      if (i < k)
      {
        WindowState localWindowState = (WindowState)paramAppWindowToken.allAppWindows.get(i);
        if (localWindowState.mAppFreezing)
        {
          localWindowState.mAppFreezing = false;
          if ((localWindowState.mHasSurface) && (!localWindowState.mOrientationChanging)) {
            break label142;
          }
        }
        for (;;)
        {
          localWindowState.mLastFreezeDuration = 0;
          j = 1;
          localWindowState.setDisplayLayoutNeeded();
          i += 1;
          break;
          label142:
          if (this.mWindowsFreezingScreen != 2)
          {
            if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
              Slog.v("WindowManager", "set mOrientationChanging of " + localWindowState);
            }
            localWindowState.mOrientationChanging = true;
            this.mWindowPlacerLocked.mOrientationChangeComplete = false;
          }
        }
      }
      if ((paramBoolean2) || (j != 0))
      {
        if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
          Slog.v("WindowManager", "No longer freezing: " + paramAppWindowToken);
        }
        paramAppWindowToken.mAppAnimator.freezingScreen = false;
        paramAppWindowToken.mAppAnimator.lastFreezeDuration = ((int)(SystemClock.elapsedRealtime() - this.mDisplayFreezeTime));
        this.mAppsFreezingScreen -= 1;
        this.mLastFinishedFreezeSource = paramAppWindowToken;
      }
      if (paramBoolean1)
      {
        if (j != 0) {
          this.mWindowPlacerLocked.performSurfacePlacement();
        }
        stopFreezingDisplayLocked();
      }
    }
  }
  
  public void updateAppOpsState()
  {
    synchronized (this.mWindowMap)
    {
      int k = this.mDisplayContents.size();
      int i = 0;
      while (i < k)
      {
        WindowList localWindowList = ((DisplayContent)this.mDisplayContents.valueAt(i)).getWindowList();
        int m = localWindowList.size();
        int j = 0;
        if (j < m)
        {
          WindowState localWindowState = (WindowState)localWindowList.get(j);
          boolean bool;
          if (localWindowState.mAppOp != -1)
          {
            int n = this.mAppOps.checkOpNoThrow(localWindowState.mAppOp, localWindowState.getOwningUid(), localWindowState.getOwningPackage());
            if (n == 0) {
              break label127;
            }
            if (n != 3) {
              break label133;
            }
            bool = true;
          }
          for (;;)
          {
            localWindowState.setAppOpVisibilityLw(bool);
            j += 1;
            break;
            label127:
            bool = true;
            continue;
            label133:
            bool = false;
          }
        }
        i += 1;
      }
      return;
    }
  }
  
  DisplayInfo updateDisplayAndOrientationLocked(int paramInt)
  {
    DisplayContent localDisplayContent = getDefaultDisplayContentLocked();
    int j;
    int i;
    label34:
    label44:
    int n;
    int i1;
    int k;
    int m;
    label98:
    DisplayInfo localDisplayInfo;
    if (this.mRotation != 1)
    {
      if (this.mRotation != 3) {
        break label283;
      }
      j = 1;
      if (j == 0) {
        break label288;
      }
      i = localDisplayContent.mBaseDisplayHeight;
      if (j == 0) {
        break label297;
      }
      j = localDisplayContent.mBaseDisplayWidth;
      n = i;
      i1 = j;
      k = i1;
      m = n;
      if (this.mAltOrientation)
      {
        if (i <= j) {
          break label306;
        }
        j = (int)(j / 1.3F);
        k = i1;
        m = n;
        if (j < i)
        {
          m = j;
          k = i1;
        }
      }
      i = this.mPolicy.getNonDecorDisplayWidth(m, k, this.mRotation, paramInt);
      paramInt = this.mPolicy.getNonDecorDisplayHeight(m, k, this.mRotation, paramInt);
      localDisplayInfo = localDisplayContent.getDisplayInfo();
      localDisplayInfo.rotation = this.mRotation;
      localDisplayInfo.logicalWidth = m;
      localDisplayInfo.logicalHeight = k;
      localDisplayInfo.logicalDensityDpi = localDisplayContent.mBaseDisplayDensity;
      localDisplayInfo.appWidth = i;
      localDisplayInfo.appHeight = paramInt;
      localDisplayInfo.getLogicalMetrics(this.mRealDisplayMetrics, CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO, null);
      localDisplayInfo.getAppMetrics(this.mDisplayMetrics);
      if (!localDisplayContent.mDisplayScalingDisabled) {
        break label337;
      }
    }
    label283:
    label288:
    label297:
    label306:
    label337:
    for (localDisplayInfo.flags |= 0x40000000;; localDisplayInfo.flags &= 0xBFFFFFFF)
    {
      this.mDisplayManagerInternal.setDisplayInfoOverrideFromWindowManager(localDisplayContent.getDisplayId(), localDisplayInfo);
      localDisplayContent.mBaseDisplayRect.set(0, 0, m, k);
      this.mCompatibleScreenScale = CompatibilityInfo.computeCompatibleScaling(this.mDisplayMetrics, this.mCompatDisplayMetrics);
      return localDisplayInfo;
      j = 1;
      break;
      j = 0;
      break;
      i = localDisplayContent.mBaseDisplayWidth;
      break label34;
      j = localDisplayContent.mBaseDisplayHeight;
      break label44;
      i = (int)(i / 1.3F);
      k = i1;
      m = n;
      if (i >= j) {
        break label98;
      }
      k = i;
      m = n;
      break label98;
    }
  }
  
  boolean updateFocusedWindowLocked(int paramInt, boolean paramBoolean)
  {
    WindowState localWindowState1 = computeFocusedWindowLocked();
    DisplayContent localDisplayContent;
    if (this.mCurrentFocus != localWindowState1)
    {
      Trace.traceBegin(32L, "wmUpdateFocus");
      this.mH.removeMessages(2);
      this.mH.sendEmptyMessage(2);
      localDisplayContent = getDefaultDisplayContentLocked();
      boolean bool;
      if (paramInt != 1) {
        if (paramInt != 3) {
          bool = true;
        }
      }
      for (;;)
      {
        bool = moveInputMethodWindowsIfNeededLocked(bool);
        if (bool)
        {
          localDisplayContent.layoutNeeded = true;
          localWindowState1 = computeFocusedWindowLocked();
        }
        if ((WindowManagerDebugConfig.DEBUG_FOCUS_LIGHT) || (localLOGV) || (WindowManagerDebugConfig.DEBUG_ONEPLUS)) {
          Slog.v(TAG, "Changing focus from " + this.mCurrentFocus + " to " + localWindowState1 + " Callers=" + Debug.getCallers(4));
        }
        WindowState localWindowState2 = this.mCurrentFocus;
        this.mCurrentFocus = localWindowState1;
        this.mLosingFocus.remove(localWindowState1);
        if ((localWindowState2 != null) && (localWindowState2.mAppToken != null) && (localWindowState2.mAppToken.waitingToShow))
        {
          Slog.d(TAG, "No need waiting to show for old focused win:" + localWindowState2);
          localWindowState2.mAppToken.waitingToShow = false;
        }
        try
        {
          if ((this.mCurrentFocus != null) && (this.mCurrentFocus.mAttrs.type == 2000))
          {
            i = this.mCurrentFocus.mSession.mPid;
            if (WindowManagerDebugConfig.DEBUG_ONEPLUS) {
              Slog.d(TAG, "set focused StatusBar win as top, pid=" + i);
            }
            Process.setProcessGroup(i, 5);
            Process.setThreadPriority(i, -2);
          }
          if ((localWindowState2 != null) && (localWindowState2.mAttrs.type == 2000))
          {
            i = localWindowState2.mSession.mPid;
            if (WindowManagerDebugConfig.DEBUG_ONEPLUS) {
              Slog.d(TAG, "set non-focused StatusBar win as default, pid=" + i);
            }
            Process.setProcessGroup(i, -1);
            Process.setThreadPriority(i, 0);
          }
        }
        catch (Exception localException)
        {
          for (;;)
          {
            int j;
            Slog.w(TAG, "setProcessGroup for StatusBar window failed.", localException);
            continue;
            int i = j;
            if (paramInt == 3)
            {
              this.mLayersController.assignLayersLocked(localDisplayContent.getWindowList());
              i = j;
            }
          }
        }
        j = this.mPolicy.focusChangedLw(localWindowState2, localWindowState1);
        i = j;
        if (bool)
        {
          i = j;
          if (localWindowState2 != this.mInputMethodWindow)
          {
            if (paramInt != 2) {
              break;
            }
            this.mWindowPlacerLocked.performLayoutLockedInner(localDisplayContent, true, paramBoolean);
            i = j & 0xFFFFFFFE;
          }
        }
        if ((i & 0x1) != 0)
        {
          localDisplayContent.layoutNeeded = true;
          if (paramInt == 2) {
            this.mWindowPlacerLocked.performLayoutLockedInner(localDisplayContent, true, paramBoolean);
          }
        }
        if (paramInt != 1) {
          this.mInputMonitor.setInputFocusLw(this.mCurrentFocus, paramBoolean);
        }
        adjustForImeIfNeeded(localDisplayContent);
        getDefaultDisplayContentLocked().scheduleToastWindowsTimeoutIfNeededLocked(localWindowState2, localWindowState1);
        Trace.traceEnd(32L);
        return true;
        bool = false;
        continue;
        bool = false;
      }
    }
    return false;
  }
  
  public Configuration updateOrientationFromAppTokens(Configuration paramConfiguration, IBinder paramIBinder)
  {
    if (!checkCallingPermission("android.permission.MANAGE_APP_TOKENS", "updateOrientationFromAppTokens()")) {
      throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
    }
    long l = Binder.clearCallingIdentity();
    synchronized (this.mWindowMap)
    {
      paramConfiguration = updateOrientationFromAppTokensLocked(paramConfiguration, paramIBinder);
      Binder.restoreCallingIdentity(l);
      return paramConfiguration;
    }
  }
  
  boolean updateOrientationFromAppTokensLocked(boolean paramBoolean)
  {
    long l = Binder.clearCallingIdentity();
    try
    {
      int i = getOrientationLocked();
      if (i != this.mLastOrientation)
      {
        this.mLastOrientation = i;
        this.mPolicy.setCurrentOrientationLw(i);
        paramBoolean = updateRotationUncheckedLocked(paramBoolean);
        if (paramBoolean) {
          return true;
        }
      }
      return false;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  void updatePointerIcon(IWindow paramIWindow)
  {
    float f1;
    float f2;
    synchronized (this.mMousePositionTracker)
    {
      boolean bool = MousePositionTracker.-get0(this.mMousePositionTracker);
      if (!bool) {
        return;
      }
      f1 = MousePositionTracker.-get1(this.mMousePositionTracker);
      f2 = MousePositionTracker.-get2(this.mMousePositionTracker);
    }
    synchronized (this.mWindowMap)
    {
      Object localObject2 = this.mDragState;
      if (localObject2 != null)
      {
        return;
        paramIWindow = finally;
        throw paramIWindow;
      }
      localObject2 = windowForClientLocked(null, paramIWindow, false);
      if (localObject2 == null)
      {
        Slog.w("WindowManager", "Bad requesting window " + paramIWindow);
        return;
      }
      paramIWindow = ((WindowState)localObject2).getDisplayContent();
      if (paramIWindow == null) {
        return;
      }
      paramIWindow = paramIWindow.getTouchableWinAtPointLocked(f1, f2);
      if (paramIWindow != localObject2) {
        return;
      }
      try
      {
        paramIWindow.mClient.updatePointerIcon(paramIWindow.translateToWindowX(f1), paramIWindow.translateToWindowY(f2));
        return;
      }
      catch (RemoteException paramIWindow)
      {
        for (;;)
        {
          Slog.w("WindowManager", "unable to update pointer icon");
        }
      }
    }
  }
  
  void updateResizingWindows(WindowState paramWindowState)
  {
    WindowStateAnimator localWindowStateAnimator = paramWindowState.mWinAnimator;
    if ((!paramWindowState.mHasSurface) || (paramWindowState.mLayoutSeq != this.mLayoutSeq) || (paramWindowState.isGoneForLayoutLw())) {}
    label362:
    do
    {
      return;
      Task localTask = paramWindowState.getTask();
      if ((localTask != null) && (localTask.mStack.getBoundsAnimating())) {
        return;
      }
      paramWindowState.setReportResizeHints();
      boolean bool2 = paramWindowState.isConfigChanged();
      if ((WindowManagerDebugConfig.DEBUG_CONFIGURATION) && (bool2)) {
        Slog.v("WindowManager", "Win " + paramWindowState + " config changed: " + this.mCurConfiguration);
      }
      boolean bool1;
      if (paramWindowState.isDragResizeChanged()) {
        if (paramWindowState.isDragResizingChangeReported()) {
          bool1 = false;
        }
      }
      for (;;)
      {
        if (localLOGV) {
          Slog.v("WindowManager", "Resizing " + paramWindowState + ": configChanged=" + bool2 + " dragResizingChanged=" + bool1 + " last=" + paramWindowState.mLastFrame + " frame=" + paramWindowState.mFrame);
        }
        paramWindowState.mLastFrame.set(paramWindowState.mFrame);
        if ((paramWindowState.mContentInsetsChanged) || (paramWindowState.mVisibleInsetsChanged) || (localWindowStateAnimator.mSurfaceResized) || (paramWindowState.mOutsetsChanged) || (paramWindowState.mFrameSizeChanged) || (bool2) || (bool1) || (!paramWindowState.isResizedWhileNotDragResizingReported())) {
          break label362;
        }
        if ((!paramWindowState.mOrientationChanging) || (!paramWindowState.isDrawnLw())) {
          break;
        }
        if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
          Slog.v("WindowManager", "Orientation not waiting for draw in " + paramWindowState + ", surfaceController " + localWindowStateAnimator.mSurfaceController);
        }
        paramWindowState.mOrientationChanging = false;
        paramWindowState.mLastFreezeDuration = ((int)(SystemClock.elapsedRealtime() - this.mDisplayFreezeTime));
        return;
        bool1 = true;
        continue;
        bool1 = false;
      }
      if ((WindowManagerDebugConfig.DEBUG_RESIZE) || (WindowManagerDebugConfig.DEBUG_ORIENTATION)) {
        Slog.v("WindowManager", "Resize reasons for w=" + paramWindowState + ": " + " contentInsetsChanged=" + paramWindowState.mContentInsetsChanged + " " + paramWindowState.mContentInsets.toShortString() + " visibleInsetsChanged=" + paramWindowState.mVisibleInsetsChanged + " " + paramWindowState.mVisibleInsets.toShortString() + " stableInsetsChanged=" + paramWindowState.mStableInsetsChanged + " " + paramWindowState.mStableInsets.toShortString() + " outsetsChanged=" + paramWindowState.mOutsetsChanged + " " + paramWindowState.mOutsets.toShortString() + " surfaceResized=" + localWindowStateAnimator.mSurfaceResized + " configChanged=" + bool2 + " dragResizingChanged=" + bool1 + " resizedWhileNotDragResizingReported=" + paramWindowState.isResizedWhileNotDragResizingReported());
      }
      if ((paramWindowState.mAppToken != null) && (paramWindowState.mAppDied))
      {
        paramWindowState.mAppToken.removeAllDeadWindows();
        return;
      }
      paramWindowState.mLastOverscanInsets.set(paramWindowState.mOverscanInsets);
      paramWindowState.mLastContentInsets.set(paramWindowState.mContentInsets);
      paramWindowState.mLastVisibleInsets.set(paramWindowState.mVisibleInsets);
      paramWindowState.mLastStableInsets.set(paramWindowState.mStableInsets);
      paramWindowState.mLastOutsets.set(paramWindowState.mOutsets);
      makeWindowFreezingScreenIfNeededLocked(paramWindowState);
      if ((paramWindowState.mOrientationChanging) || (bool1) || (paramWindowState.isResizedWhileNotDragResizing()))
      {
        if ((WindowManagerDebugConfig.DEBUG_SURFACE_TRACE) || (WindowManagerDebugConfig.DEBUG_ANIM) || (WindowManagerDebugConfig.DEBUG_ORIENTATION) || (WindowManagerDebugConfig.DEBUG_RESIZE)) {
          Slog.v("WindowManager", "Orientation or resize start waiting for draw, mDrawState=DRAW_PENDING in " + paramWindowState + ", surfaceController " + localWindowStateAnimator.mSurfaceController);
        }
        localWindowStateAnimator.mDrawState = 1;
        if (paramWindowState.mAppToken != null) {
          paramWindowState.mAppToken.clearAllDrawn();
        }
      }
    } while (this.mResizingWindows.contains(paramWindowState));
    if ((WindowManagerDebugConfig.DEBUG_RESIZE) || (WindowManagerDebugConfig.DEBUG_ORIENTATION)) {
      Slog.v("WindowManager", "Resizing window " + paramWindowState);
    }
    this.mResizingWindows.add(paramWindowState);
  }
  
  public void updateRotation(boolean paramBoolean1, boolean paramBoolean2)
  {
    updateRotationUnchecked(paramBoolean1, paramBoolean2);
  }
  
  public void updateRotationUnchecked(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
      Slog.v("WindowManager", "updateRotationUnchecked(alwaysSendConfiguration=" + paramBoolean1 + ")");
    }
    long l = Binder.clearCallingIdentity();
    synchronized (this.mWindowMap)
    {
      boolean bool = updateRotationUncheckedLocked(false);
      if ((!bool) || (paramBoolean2))
      {
        getDefaultDisplayContentLocked().layoutNeeded = true;
        this.mWindowPlacerLocked.performSurfacePlacement();
      }
      if ((bool) || (paramBoolean1)) {
        sendNewConfiguration();
      }
      Binder.restoreCallingIdentity(l);
      return;
    }
  }
  
  public boolean updateRotationUncheckedLocked(boolean paramBoolean)
  {
    if (this.mDeferredRotationPauseCount > 0)
    {
      if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
        Slog.v("WindowManager", "Deferring rotation, rotation is paused.");
      }
      return false;
    }
    Object localObject1 = this.mAnimator.getScreenRotationAnimationLocked(0);
    if ((localObject1 != null) && (((ScreenRotationAnimation)localObject1).isAnimating()))
    {
      if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
        Slog.v("WindowManager", "Deferring rotation, animation in progress.");
      }
      return false;
    }
    if (this.mDisplayFrozen)
    {
      if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
        Slog.v("WindowManager", "Deferring rotation, still finishing previous rotation");
      }
      return false;
    }
    if (!this.mDisplayEnabled)
    {
      if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
        Slog.v("WindowManager", "Deferring rotation, display is not enabled.");
      }
      return false;
    }
    DisplayContent localDisplayContent = getDefaultDisplayContentLocked();
    WindowList localWindowList = localDisplayContent.getWindowList();
    int k = this.mRotation;
    int j = this.mPolicy.rotationForOrientationLw(this.mLastOrientation, this.mRotation);
    boolean bool1 = this.mPolicy.shouldRotateSeamlessly(k, j);
    boolean bool2 = bool1;
    if (bool1)
    {
      i = localWindowList.size() - 1;
      bool2 = bool1;
      if (i >= 0)
      {
        localObject1 = (WindowState)localWindowList.get(i);
        if (((WindowState)localObject1).mSeamlesslyRotated) {
          return false;
        }
        bool2 = bool1;
        if ((((WindowState)localObject1).isChildWindow() & ((WindowState)localObject1).isVisibleNow())) {
          if (!((WindowState)localObject1).mWinAnimator.mSurfaceController.getTransformToDisplayInverse()) {
            break label260;
          }
        }
        label260:
        for (bool2 = bool1;; bool2 = false)
        {
          i -= 1;
          bool1 = bool2;
          break;
        }
      }
    }
    Object localObject3;
    if (this.mPolicy.rotationHasCompatibleMetricsLw(this.mLastOrientation, j))
    {
      bool1 = false;
      if (WindowManagerDebugConfig.DEBUG_ORIENTATION)
      {
        localObject3 = new StringBuilder().append("Selected orientation ").append(this.mLastOrientation).append(", got rotation ").append(j).append(" which has ");
        if (!bool1) {
          break label388;
        }
      }
    }
    label388:
    for (localObject1 = "incompatible";; localObject1 = "compatible")
    {
      Slog.v("WindowManager", (String)localObject1 + " metrics");
      if ((this.mRotation != j) || (this.mAltOrientation != bool1)) {
        break label396;
      }
      return false;
      bool1 = true;
      break;
    }
    label396:
    if (WindowManagerDebugConfig.DEBUG_ORIENTATION)
    {
      localObject3 = new StringBuilder().append("Rotation changed to ").append(j);
      if (bool1)
      {
        localObject1 = " (alt)";
        localObject3 = ((StringBuilder)localObject3).append((String)localObject1).append(" from ").append(this.mRotation);
        if (!this.mAltOrientation) {
          break label750;
        }
        localObject1 = " (alt)";
        label465:
        Slog.v("WindowManager", (String)localObject1 + ", lastOrientation=" + this.mLastOrientation);
      }
    }
    else
    {
      this.mRotation = j;
      this.mAltOrientation = bool1;
      this.mPolicy.setRotationLw(this.mRotation);
      this.mWindowsFreezingScreen = 1;
      this.mH.removeMessages(11);
      this.mH.sendEmptyMessageDelayed(11, 2000L);
      this.mWaitingForConfig = true;
      localDisplayContent.layoutNeeded = true;
      localObject1 = new int[2];
      if (!localDisplayContent.isDimming()) {
        break label758;
      }
      localObject1[1] = 0;
      localObject1[0] = 0;
      label580:
      if (bool2) {
        break label772;
      }
      startFreezingDisplayLocked(paramBoolean, localObject1[0], localObject1[1]);
      localObject1 = this.mAnimator.getScreenRotationAnimationLocked(0);
      label608:
      updateDisplayAndOrientationLocked(this.mCurConfiguration.uiMode);
      localObject3 = localDisplayContent.getDisplayInfo();
      if (!paramBoolean)
      {
        if (WindowManagerDebugConfig.SHOW_TRANSACTIONS) {
          Slog.i("WindowManager", ">>> OPEN TRANSACTION setRotationUnchecked");
        }
        SurfaceControl.openTransaction();
      }
      if (localObject1 == null) {}
    }
    for (;;)
    {
      try
      {
        if ((((ScreenRotationAnimation)localObject1).hasScreenshot()) && (((ScreenRotationAnimation)localObject1).setRotationInTransaction(j, this.mFxSession, 10000L, getTransitionAnimationScaleLocked(), ((DisplayInfo)localObject3).logicalWidth, ((DisplayInfo)localObject3).logicalHeight))) {
          scheduleAnimationLocked();
        }
        if (bool2)
        {
          i = localWindowList.size() - 1;
          if (i >= 0)
          {
            ((WindowState)localWindowList.get(i)).mWinAnimator.seamlesslyRotateWindow(k, this.mRotation);
            i -= 1;
            continue;
            localObject1 = "";
            break;
            label750:
            localObject1 = "";
            break label465;
            label758:
            this.mPolicy.selectRotationAnimationLw((int[])localObject1);
            break label580;
            label772:
            localObject1 = null;
            this.mSeamlessRotationCount = 0;
            break label608;
          }
        }
        this.mDisplayManagerInternal.performTraversalInTransactionFromWindowManager();
        if (!paramBoolean)
        {
          SurfaceControl.closeTransaction();
          if (WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
            Slog.i("WindowManager", "<<< CLOSE TRANSACTION setRotationUnchecked");
          }
        }
        i = localWindowList.size() - 1;
        if (i < 0) {
          break label957;
        }
        localObject3 = (WindowState)localWindowList.get(i);
        if (((WindowState)localObject3).mAppToken != null) {
          ((WindowState)localObject3).mAppToken.destroySavedSurfaces();
        }
        if ((!((WindowState)localObject3).mHasSurface) || (bool2))
        {
          i -= 1;
          continue;
        }
        if (!WindowManagerDebugConfig.DEBUG_ORIENTATION) {
          break label934;
        }
      }
      finally
      {
        if (!paramBoolean)
        {
          SurfaceControl.closeTransaction();
          if (WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
            Slog.i("WindowManager", "<<< CLOSE TRANSACTION setRotationUnchecked");
          }
        }
      }
      Slog.v("WindowManager", "Set mOrientationChanging of " + localObject3);
      label934:
      ((WindowState)localObject3).mOrientationChanging = true;
      this.mWindowPlacerLocked.mOrientationChangeComplete = false;
      ((WindowState)localObject3).mLastFreezeDuration = 0;
    }
    label957:
    if (bool2)
    {
      this.mH.removeMessages(54);
      this.mH.sendEmptyMessageDelayed(54, 2000L);
    }
    int i = this.mRotationWatchers.size() - 1;
    for (;;)
    {
      if (i >= 0) {}
      try
      {
        ((RotationWatcher)this.mRotationWatchers.get(i)).watcher.onRotationChanged(j);
        i -= 1;
        continue;
        if ((localObject2 == null) && (this.mAccessibilityController != null) && (localDisplayContent.getDisplayId() == 0)) {
          this.mAccessibilityController.onRotationChangedLocked(getDefaultDisplayContentLocked(), j);
        }
        return true;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;) {}
      }
    }
  }
  
  boolean updateStatusBarVisibilityLocked(int paramInt)
  {
    if (this.mLastDispatchedSystemUiVisibility == paramInt) {
      return false;
    }
    int j = this.mLastDispatchedSystemUiVisibility;
    this.mLastDispatchedSystemUiVisibility = paramInt;
    this.mInputManager.setSystemUiVisibility(paramInt);
    WindowList localWindowList = getDefaultWindowListLocked();
    int k = localWindowList.size();
    int i = 0;
    while (i < k)
    {
      WindowState localWindowState = (WindowState)localWindowList.get(i);
      try
      {
        int m = localWindowState.mSystemUiVisibility;
        int n = (m ^ paramInt) & (j ^ paramInt) & 0x7 & paramInt;
        int i1 = n & m | paramInt & n;
        if (i1 != m)
        {
          localWindowState.mSeq += 1;
          localWindowState.mSystemUiVisibility = i1;
        }
        if ((i1 != m) || (localWindowState.mAttrs.hasSystemUiListeners)) {
          localWindowState.mClient.dispatchSystemUiVisibilityChanged(localWindowState.mSeq, paramInt, i1, n);
        }
      }
      catch (RemoteException localRemoteException)
      {
        for (;;) {}
      }
      i += 1;
    }
    return true;
  }
  
  void updateTokenInPlaceLocked(AppWindowToken paramAppWindowToken, int paramInt)
  {
    if (paramInt != -1)
    {
      if (paramAppWindowToken.mAppAnimator.animation == AppWindowAnimator.sDummyAnimation) {
        paramAppWindowToken.mAppAnimator.setNullAnimation();
      }
      applyAnimationLocked(paramAppWindowToken, null, paramInt, false, false);
    }
  }
  
  public void validateAppTokens(int paramInt, List<TaskGroup> paramList)
  {
    for (;;)
    {
      int i;
      int j;
      int k;
      synchronized (this.mWindowMap)
      {
        i = paramList.size() - 1;
        if (i < 0)
        {
          Slog.w("WindowManager", "validateAppTokens: empty task list");
          return;
        }
        int m = ((TaskGroup)paramList.get(0)).taskId;
        DisplayContent localDisplayContent = ((Task)this.mTaskIdToTask.get(m)).getDisplayContent();
        if (localDisplayContent == null)
        {
          Slog.w("WindowManager", "validateAppTokens: no Display for taskId=" + m);
          return;
        }
        ArrayList localArrayList1 = ((TaskStack)this.mStackIdToStack.get(paramInt)).getTasks();
        j = localArrayList1.size() - 1;
        Object localObject = localDisplayContent;
        if ((j < 0) || (i < 0)) {
          break label420;
        }
        AppTokenList localAppTokenList = ((Task)localArrayList1.get(j)).mAppTokens;
        TaskGroup localTaskGroup = (TaskGroup)paramList.get(i);
        ArrayList localArrayList2 = localTaskGroup.tokens;
        localDisplayContent = ((Task)this.mTaskIdToTask.get(m)).getDisplayContent();
        if (localDisplayContent != localObject)
        {
          Slog.w("WindowManager", "validateAppTokens: displayContent changed in TaskGroup list!");
          return;
        }
        paramInt = localAppTokenList.size() - 1;
        k = localTaskGroup.tokens.size() - 1;
        if ((paramInt >= 0) && (k >= 0))
        {
          localObject = (AppWindowToken)localAppTokenList.get(paramInt);
          if (((AppWindowToken)localObject).removed)
          {
            paramInt -= 1;
            continue;
          }
          if (localArrayList2.get(k) != ((AppWindowToken)localObject).token)
          {
            break label411;
            Slog.w("WindowManager", "validateAppTokens: Mismatch! ActivityManager=" + paramList);
            Slog.w("WindowManager", "validateAppTokens: Mismatch! WindowManager=" + localArrayList1);
            Slog.w("WindowManager", "validateAppTokens: Mismatch! Callers=" + Debug.getCallers(4));
          }
          else
          {
            paramInt -= 1;
            k -= 1;
            continue;
            j -= 1;
            i -= 1;
          }
        }
      }
      label411:
      if ((paramInt >= 0) || (k >= 0)) {
        label420:
        if (j < 0) {
          if (i < 0) {}
        }
      }
    }
  }
  
  public void validateStackOrder(Integer[] paramArrayOfInteger) {}
  
  /* Error */
  boolean viewServerGetFocusedWindow(java.net.Socket paramSocket)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 4517	com/android/server/wm/WindowManagerService:isSystemSecure	()Z
    //   4: ifeq +5 -> 9
    //   7: iconst_0
    //   8: ireturn
    //   9: iconst_1
    //   10: istore_3
    //   11: aload_0
    //   12: invokespecial 1778	com/android/server/wm/WindowManagerService:getFocusedWindow	()Lcom/android/server/wm/WindowState;
    //   15: astore 6
    //   17: aconst_null
    //   18: astore 4
    //   20: aconst_null
    //   21: astore 5
    //   23: new 6589	java/io/BufferedWriter
    //   26: dup
    //   27: new 6591	java/io/OutputStreamWriter
    //   30: dup
    //   31: aload_1
    //   32: invokevirtual 6597	java/net/Socket:getOutputStream	()Ljava/io/OutputStream;
    //   35: invokespecial 6600	java/io/OutputStreamWriter:<init>	(Ljava/io/OutputStream;)V
    //   38: sipush 8192
    //   41: invokespecial 6603	java/io/BufferedWriter:<init>	(Ljava/io/Writer;I)V
    //   44: astore_1
    //   45: aload 6
    //   47: ifnull +34 -> 81
    //   50: aload_1
    //   51: aload 6
    //   53: invokestatic 1784	java/lang/System:identityHashCode	(Ljava/lang/Object;)I
    //   56: invokestatic 3949	java/lang/Integer:toHexString	(I)Ljava/lang/String;
    //   59: invokevirtual 6606	java/io/BufferedWriter:write	(Ljava/lang/String;)V
    //   62: aload_1
    //   63: bipush 32
    //   65: invokevirtual 6608	java/io/BufferedWriter:write	(I)V
    //   68: aload_1
    //   69: aload 6
    //   71: getfield 1107	com/android/server/wm/WindowState:mAttrs	Landroid/view/WindowManager$LayoutParams;
    //   74: invokevirtual 2917	android/view/WindowManager$LayoutParams:getTitle	()Ljava/lang/CharSequence;
    //   77: invokevirtual 6611	java/io/BufferedWriter:append	(Ljava/lang/CharSequence;)Ljava/io/Writer;
    //   80: pop
    //   81: aload_1
    //   82: bipush 10
    //   84: invokevirtual 6608	java/io/BufferedWriter:write	(I)V
    //   87: aload_1
    //   88: invokevirtual 6612	java/io/BufferedWriter:flush	()V
    //   91: iload_3
    //   92: istore_2
    //   93: aload_1
    //   94: ifnull +9 -> 103
    //   97: aload_1
    //   98: invokevirtual 6613	java/io/BufferedWriter:close	()V
    //   101: iload_3
    //   102: istore_2
    //   103: iload_2
    //   104: ireturn
    //   105: astore_1
    //   106: iconst_0
    //   107: istore_2
    //   108: goto -5 -> 103
    //   111: astore_1
    //   112: aload 5
    //   114: astore_1
    //   115: iconst_0
    //   116: istore_2
    //   117: aload_1
    //   118: ifnull -15 -> 103
    //   121: aload_1
    //   122: invokevirtual 6613	java/io/BufferedWriter:close	()V
    //   125: iconst_0
    //   126: ireturn
    //   127: astore_1
    //   128: iconst_0
    //   129: ireturn
    //   130: astore_1
    //   131: aload 4
    //   133: ifnull +8 -> 141
    //   136: aload 4
    //   138: invokevirtual 6613	java/io/BufferedWriter:close	()V
    //   141: aload_1
    //   142: athrow
    //   143: astore 4
    //   145: goto -4 -> 141
    //   148: astore 5
    //   150: aload_1
    //   151: astore 4
    //   153: aload 5
    //   155: astore_1
    //   156: goto -25 -> 131
    //   159: astore 4
    //   161: goto -46 -> 115
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	164	0	this	WindowManagerService
    //   0	164	1	paramSocket	java.net.Socket
    //   92	25	2	bool1	boolean
    //   10	92	3	bool2	boolean
    //   18	119	4	localObject1	Object
    //   143	1	4	localIOException	IOException
    //   151	1	4	localSocket	java.net.Socket
    //   159	1	4	localException	Exception
    //   21	92	5	localObject2	Object
    //   148	6	5	localObject3	Object
    //   15	55	6	localWindowState	WindowState
    // Exception table:
    //   from	to	target	type
    //   97	101	105	java/io/IOException
    //   23	45	111	java/lang/Exception
    //   121	125	127	java/io/IOException
    //   23	45	130	finally
    //   136	141	143	java/io/IOException
    //   50	81	148	finally
    //   81	91	148	finally
    //   50	81	159	java/lang/Exception
    //   81	91	159	java/lang/Exception
  }
  
  /* Error */
  boolean viewServerListWindows(java.net.Socket paramSocket)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 4517	com/android/server/wm/WindowManagerService:isSystemSecure	()Z
    //   4: ifeq +5 -> 9
    //   7: iconst_0
    //   8: ireturn
    //   9: iconst_1
    //   10: istore 5
    //   12: new 1014	com/android/server/wm/WindowList
    //   15: dup
    //   16: invokespecial 1893	com/android/server/wm/WindowList:<init>	()V
    //   19: astore 8
    //   21: aload_0
    //   22: getfield 491	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
    //   25: astore 6
    //   27: aload 6
    //   29: monitorenter
    //   30: aload_0
    //   31: getfield 566	com/android/server/wm/WindowManagerService:mDisplayContents	Landroid/util/SparseArray;
    //   34: invokevirtual 1541	android/util/SparseArray:size	()I
    //   37: istore_3
    //   38: iconst_0
    //   39: istore_2
    //   40: iload_2
    //   41: iload_3
    //   42: if_icmpge +30 -> 72
    //   45: aload 8
    //   47: aload_0
    //   48: getfield 566	com/android/server/wm/WindowManagerService:mDisplayContents	Landroid/util/SparseArray;
    //   51: iload_2
    //   52: invokevirtual 1544	android/util/SparseArray:valueAt	(I)Ljava/lang/Object;
    //   55: checkcast 1004	com/android/server/wm/DisplayContent
    //   58: invokevirtual 1008	com/android/server/wm/DisplayContent:getWindowList	()Lcom/android/server/wm/WindowList;
    //   61: invokevirtual 6615	com/android/server/wm/WindowList:addAll	(Ljava/util/Collection;)Z
    //   64: pop
    //   65: iload_2
    //   66: iconst_1
    //   67: iadd
    //   68: istore_2
    //   69: goto -29 -> 40
    //   72: aload 6
    //   74: monitorexit
    //   75: aconst_null
    //   76: astore 6
    //   78: aconst_null
    //   79: astore 7
    //   81: new 6589	java/io/BufferedWriter
    //   84: dup
    //   85: new 6591	java/io/OutputStreamWriter
    //   88: dup
    //   89: aload_1
    //   90: invokevirtual 6597	java/net/Socket:getOutputStream	()Ljava/io/OutputStream;
    //   93: invokespecial 6600	java/io/OutputStreamWriter:<init>	(Ljava/io/OutputStream;)V
    //   96: sipush 8192
    //   99: invokespecial 6603	java/io/BufferedWriter:<init>	(Ljava/io/Writer;I)V
    //   102: astore_1
    //   103: aload 8
    //   105: invokevirtual 1082	com/android/server/wm/WindowList:size	()I
    //   108: istore_3
    //   109: iconst_0
    //   110: istore_2
    //   111: iload_2
    //   112: iload_3
    //   113: if_icmpge +64 -> 177
    //   116: aload 8
    //   118: iload_2
    //   119: invokevirtual 1083	com/android/server/wm/WindowList:get	(I)Ljava/lang/Object;
    //   122: checkcast 508	com/android/server/wm/WindowState
    //   125: astore 6
    //   127: aload_1
    //   128: aload 6
    //   130: invokestatic 1784	java/lang/System:identityHashCode	(Ljava/lang/Object;)I
    //   133: invokestatic 3949	java/lang/Integer:toHexString	(I)Ljava/lang/String;
    //   136: invokevirtual 6606	java/io/BufferedWriter:write	(Ljava/lang/String;)V
    //   139: aload_1
    //   140: bipush 32
    //   142: invokevirtual 6608	java/io/BufferedWriter:write	(I)V
    //   145: aload_1
    //   146: aload 6
    //   148: getfield 1107	com/android/server/wm/WindowState:mAttrs	Landroid/view/WindowManager$LayoutParams;
    //   151: invokevirtual 2917	android/view/WindowManager$LayoutParams:getTitle	()Ljava/lang/CharSequence;
    //   154: invokevirtual 6611	java/io/BufferedWriter:append	(Ljava/lang/CharSequence;)Ljava/io/Writer;
    //   157: pop
    //   158: aload_1
    //   159: bipush 10
    //   161: invokevirtual 6608	java/io/BufferedWriter:write	(I)V
    //   164: iload_2
    //   165: iconst_1
    //   166: iadd
    //   167: istore_2
    //   168: goto -57 -> 111
    //   171: astore_1
    //   172: aload 6
    //   174: monitorexit
    //   175: aload_1
    //   176: athrow
    //   177: aload_1
    //   178: ldc_w 6617
    //   181: invokevirtual 6606	java/io/BufferedWriter:write	(Ljava/lang/String;)V
    //   184: aload_1
    //   185: invokevirtual 6612	java/io/BufferedWriter:flush	()V
    //   188: iload 5
    //   190: istore 4
    //   192: aload_1
    //   193: ifnull +11 -> 204
    //   196: aload_1
    //   197: invokevirtual 6613	java/io/BufferedWriter:close	()V
    //   200: iload 5
    //   202: istore 4
    //   204: iload 4
    //   206: ireturn
    //   207: astore_1
    //   208: iconst_0
    //   209: istore 4
    //   211: goto -7 -> 204
    //   214: astore_1
    //   215: aload 7
    //   217: astore_1
    //   218: iconst_0
    //   219: istore 4
    //   221: aload_1
    //   222: ifnull -18 -> 204
    //   225: aload_1
    //   226: invokevirtual 6613	java/io/BufferedWriter:close	()V
    //   229: iconst_0
    //   230: ireturn
    //   231: astore_1
    //   232: iconst_0
    //   233: ireturn
    //   234: astore_1
    //   235: aload 6
    //   237: ifnull +8 -> 245
    //   240: aload 6
    //   242: invokevirtual 6613	java/io/BufferedWriter:close	()V
    //   245: aload_1
    //   246: athrow
    //   247: astore 6
    //   249: goto -4 -> 245
    //   252: astore 7
    //   254: aload_1
    //   255: astore 6
    //   257: aload 7
    //   259: astore_1
    //   260: goto -25 -> 235
    //   263: astore 6
    //   265: goto -47 -> 218
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	268	0	this	WindowManagerService
    //   0	268	1	paramSocket	java.net.Socket
    //   39	129	2	i	int
    //   37	77	3	j	int
    //   190	30	4	bool1	boolean
    //   10	191	5	bool2	boolean
    //   25	216	6	localObject1	Object
    //   247	1	6	localIOException	IOException
    //   255	1	6	localSocket	java.net.Socket
    //   263	1	6	localException	Exception
    //   79	137	7	localObject2	Object
    //   252	6	7	localObject3	Object
    //   19	98	8	localWindowList	WindowList
    // Exception table:
    //   from	to	target	type
    //   30	38	171	finally
    //   45	65	171	finally
    //   196	200	207	java/io/IOException
    //   81	103	214	java/lang/Exception
    //   225	229	231	java/io/IOException
    //   81	103	234	finally
    //   240	245	247	java/io/IOException
    //   103	109	252	finally
    //   116	164	252	finally
    //   177	188	252	finally
    //   103	109	263	java/lang/Exception
    //   116	164	263	java/lang/Exception
    //   177	188	263	java/lang/Exception
  }
  
  /* Error */
  boolean viewServerWindowCommand(java.net.Socket paramSocket, String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 4517	com/android/server/wm/WindowManagerService:isSystemSecure	()Z
    //   4: ifeq +5 -> 9
    //   7: iconst_0
    //   8: ireturn
    //   9: iconst_1
    //   10: istore 7
    //   12: aconst_null
    //   13: astore 20
    //   15: aconst_null
    //   16: astore 11
    //   18: aconst_null
    //   19: astore 19
    //   21: aconst_null
    //   22: astore 10
    //   24: aconst_null
    //   25: astore 17
    //   27: aconst_null
    //   28: astore 18
    //   30: aconst_null
    //   31: astore 16
    //   33: aload 11
    //   35: astore 13
    //   37: aload 10
    //   39: astore 14
    //   41: aload_3
    //   42: astore 15
    //   44: aload 20
    //   46: astore 8
    //   48: aload 18
    //   50: astore 12
    //   52: aload 19
    //   54: astore 9
    //   56: aload_3
    //   57: bipush 32
    //   59: invokevirtual 2157	java/lang/String:indexOf	(I)I
    //   62: istore 5
    //   64: iload 5
    //   66: istore 4
    //   68: iload 5
    //   70: iconst_m1
    //   71: if_icmpne +32 -> 103
    //   74: aload 11
    //   76: astore 13
    //   78: aload 10
    //   80: astore 14
    //   82: aload_3
    //   83: astore 15
    //   85: aload 20
    //   87: astore 8
    //   89: aload 18
    //   91: astore 12
    //   93: aload 19
    //   95: astore 9
    //   97: aload_3
    //   98: invokevirtual 1874	java/lang/String:length	()I
    //   101: istore 4
    //   103: aload 11
    //   105: astore 13
    //   107: aload 10
    //   109: astore 14
    //   111: aload_3
    //   112: astore 15
    //   114: aload 20
    //   116: astore 8
    //   118: aload 18
    //   120: astore 12
    //   122: aload 19
    //   124: astore 9
    //   126: aload_3
    //   127: iconst_0
    //   128: iload 4
    //   130: invokevirtual 2164	java/lang/String:substring	(II)Ljava/lang/String;
    //   133: bipush 16
    //   135: invokestatic 6625	java/lang/Long:parseLong	(Ljava/lang/String;I)J
    //   138: l2i
    //   139: istore 5
    //   141: aload 11
    //   143: astore 13
    //   145: aload 10
    //   147: astore 14
    //   149: aload_3
    //   150: astore 15
    //   152: aload 20
    //   154: astore 8
    //   156: aload 18
    //   158: astore 12
    //   160: aload 19
    //   162: astore 9
    //   164: iload 4
    //   166: aload_3
    //   167: invokevirtual 1874	java/lang/String:length	()I
    //   170: if_icmpge +648 -> 818
    //   173: aload 11
    //   175: astore 13
    //   177: aload 10
    //   179: astore 14
    //   181: aload_3
    //   182: astore 15
    //   184: aload 20
    //   186: astore 8
    //   188: aload 18
    //   190: astore 12
    //   192: aload 19
    //   194: astore 9
    //   196: aload_3
    //   197: iload 4
    //   199: iconst_1
    //   200: iadd
    //   201: invokevirtual 2166	java/lang/String:substring	(I)Ljava/lang/String;
    //   204: astore_3
    //   205: aload 11
    //   207: astore 13
    //   209: aload 10
    //   211: astore 14
    //   213: aload_3
    //   214: astore 15
    //   216: aload 20
    //   218: astore 8
    //   220: aload 18
    //   222: astore 12
    //   224: aload 19
    //   226: astore 9
    //   228: aload_0
    //   229: iload 5
    //   231: invokespecial 6627	com/android/server/wm/WindowManagerService:findWindow	(I)Lcom/android/server/wm/WindowState;
    //   234: astore 21
    //   236: aload 21
    //   238: ifnonnull +5 -> 243
    //   241: iconst_0
    //   242: ireturn
    //   243: aload 11
    //   245: astore 13
    //   247: aload 10
    //   249: astore 14
    //   251: aload_3
    //   252: astore 15
    //   254: aload 20
    //   256: astore 8
    //   258: aload 18
    //   260: astore 12
    //   262: aload 19
    //   264: astore 9
    //   266: invokestatic 4846	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   269: astore 11
    //   271: aload 11
    //   273: astore 13
    //   275: aload 10
    //   277: astore 14
    //   279: aload_3
    //   280: astore 15
    //   282: aload 11
    //   284: astore 8
    //   286: aload 18
    //   288: astore 12
    //   290: aload 19
    //   292: astore 9
    //   294: aload 11
    //   296: ldc_w 6629
    //   299: invokevirtual 4851	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   302: aload 11
    //   304: astore 13
    //   306: aload 10
    //   308: astore 14
    //   310: aload_3
    //   311: astore 15
    //   313: aload 11
    //   315: astore 8
    //   317: aload 18
    //   319: astore 12
    //   321: aload 19
    //   323: astore 9
    //   325: aload 11
    //   327: aload_2
    //   328: invokevirtual 6632	android/os/Parcel:writeString	(Ljava/lang/String;)V
    //   331: aload 11
    //   333: astore 13
    //   335: aload 10
    //   337: astore 14
    //   339: aload_3
    //   340: astore 15
    //   342: aload 11
    //   344: astore 8
    //   346: aload 18
    //   348: astore 12
    //   350: aload 19
    //   352: astore 9
    //   354: aload 11
    //   356: aload_3
    //   357: invokevirtual 6632	android/os/Parcel:writeString	(Ljava/lang/String;)V
    //   360: aload 11
    //   362: astore 13
    //   364: aload 10
    //   366: astore 14
    //   368: aload_3
    //   369: astore 15
    //   371: aload 11
    //   373: astore 8
    //   375: aload 18
    //   377: astore 12
    //   379: aload 19
    //   381: astore 9
    //   383: aload 11
    //   385: iconst_1
    //   386: invokevirtual 6635	android/os/Parcel:writeInt	(I)V
    //   389: aload 11
    //   391: astore 13
    //   393: aload 10
    //   395: astore 14
    //   397: aload_3
    //   398: astore 15
    //   400: aload 11
    //   402: astore 8
    //   404: aload 18
    //   406: astore 12
    //   408: aload 19
    //   410: astore 9
    //   412: aload_1
    //   413: invokestatic 6641	android/os/ParcelFileDescriptor:fromSocket	(Ljava/net/Socket;)Landroid/os/ParcelFileDescriptor;
    //   416: aload 11
    //   418: iconst_0
    //   419: invokevirtual 6645	android/os/ParcelFileDescriptor:writeToParcel	(Landroid/os/Parcel;I)V
    //   422: aload 11
    //   424: astore 13
    //   426: aload 10
    //   428: astore 14
    //   430: aload_3
    //   431: astore 15
    //   433: aload 11
    //   435: astore 8
    //   437: aload 18
    //   439: astore 12
    //   441: aload 19
    //   443: astore 9
    //   445: invokestatic 4846	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   448: astore 10
    //   450: aload 11
    //   452: astore 13
    //   454: aload 10
    //   456: astore 14
    //   458: aload_3
    //   459: astore 15
    //   461: aload 11
    //   463: astore 8
    //   465: aload 18
    //   467: astore 12
    //   469: aload 10
    //   471: astore 9
    //   473: aload 21
    //   475: getfield 998	com/android/server/wm/WindowState:mClient	Landroid/view/IWindow;
    //   478: invokeinterface 1036 1 0
    //   483: iconst_1
    //   484: aload 11
    //   486: aload 10
    //   488: iconst_0
    //   489: invokeinterface 4854 5 0
    //   494: pop
    //   495: aload 11
    //   497: astore 13
    //   499: aload 10
    //   501: astore 14
    //   503: aload_3
    //   504: astore 15
    //   506: aload 11
    //   508: astore 8
    //   510: aload 18
    //   512: astore 12
    //   514: aload 10
    //   516: astore 9
    //   518: aload 10
    //   520: invokevirtual 6648	android/os/Parcel:readException	()V
    //   523: aload 11
    //   525: astore 13
    //   527: aload 10
    //   529: astore 14
    //   531: aload_3
    //   532: astore 15
    //   534: aload 11
    //   536: astore 8
    //   538: aload 18
    //   540: astore 12
    //   542: aload 10
    //   544: astore 9
    //   546: aload_1
    //   547: invokevirtual 6651	java/net/Socket:isOutputShutdown	()Z
    //   550: ifne +59 -> 609
    //   553: aload 11
    //   555: astore 13
    //   557: aload 10
    //   559: astore 14
    //   561: aload_3
    //   562: astore 15
    //   564: aload 11
    //   566: astore 8
    //   568: aload 18
    //   570: astore 12
    //   572: aload 10
    //   574: astore 9
    //   576: new 6589	java/io/BufferedWriter
    //   579: dup
    //   580: new 6591	java/io/OutputStreamWriter
    //   583: dup
    //   584: aload_1
    //   585: invokevirtual 6597	java/net/Socket:getOutputStream	()Ljava/io/OutputStream;
    //   588: invokespecial 6600	java/io/OutputStreamWriter:<init>	(Ljava/io/OutputStream;)V
    //   591: invokespecial 6654	java/io/BufferedWriter:<init>	(Ljava/io/Writer;)V
    //   594: astore_1
    //   595: aload_1
    //   596: ldc_w 6656
    //   599: invokevirtual 6606	java/io/BufferedWriter:write	(Ljava/lang/String;)V
    //   602: aload_1
    //   603: invokevirtual 6612	java/io/BufferedWriter:flush	()V
    //   606: aload_1
    //   607: astore 16
    //   609: aload 11
    //   611: ifnull +8 -> 619
    //   614: aload 11
    //   616: invokevirtual 4857	android/os/Parcel:recycle	()V
    //   619: aload 10
    //   621: ifnull +8 -> 629
    //   624: aload 10
    //   626: invokevirtual 4857	android/os/Parcel:recycle	()V
    //   629: iload 7
    //   631: istore 6
    //   633: aload 16
    //   635: ifnull +12 -> 647
    //   638: aload 16
    //   640: invokevirtual 6613	java/io/BufferedWriter:close	()V
    //   643: iload 7
    //   645: istore 6
    //   647: iload 6
    //   649: ireturn
    //   650: astore_1
    //   651: iconst_1
    //   652: ireturn
    //   653: astore 8
    //   655: aload 15
    //   657: astore_3
    //   658: aload 14
    //   660: astore 10
    //   662: aload 17
    //   664: astore_1
    //   665: aload 8
    //   667: astore 14
    //   669: aload 13
    //   671: astore 11
    //   673: aload 11
    //   675: astore 8
    //   677: aload_1
    //   678: astore 12
    //   680: aload 10
    //   682: astore 9
    //   684: ldc_w 440
    //   687: new 1023	java/lang/StringBuilder
    //   690: dup
    //   691: invokespecial 1024	java/lang/StringBuilder:<init>	()V
    //   694: ldc_w 6658
    //   697: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   700: aload_2
    //   701: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   704: ldc_w 6660
    //   707: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   710: aload_3
    //   711: invokevirtual 1030	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   714: invokevirtual 1046	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   717: aload 14
    //   719: invokestatic 2845	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   722: pop
    //   723: iconst_0
    //   724: istore 6
    //   726: aload 11
    //   728: ifnull +8 -> 736
    //   731: aload 11
    //   733: invokevirtual 4857	android/os/Parcel:recycle	()V
    //   736: aload 10
    //   738: ifnull +8 -> 746
    //   741: aload 10
    //   743: invokevirtual 4857	android/os/Parcel:recycle	()V
    //   746: aload_1
    //   747: ifnull -100 -> 647
    //   750: aload_1
    //   751: invokevirtual 6613	java/io/BufferedWriter:close	()V
    //   754: iconst_0
    //   755: ireturn
    //   756: astore_1
    //   757: iconst_0
    //   758: ireturn
    //   759: astore_1
    //   760: aload 8
    //   762: ifnull +8 -> 770
    //   765: aload 8
    //   767: invokevirtual 4857	android/os/Parcel:recycle	()V
    //   770: aload 9
    //   772: ifnull +8 -> 780
    //   775: aload 9
    //   777: invokevirtual 4857	android/os/Parcel:recycle	()V
    //   780: aload 12
    //   782: ifnull +8 -> 790
    //   785: aload 12
    //   787: invokevirtual 6613	java/io/BufferedWriter:close	()V
    //   790: aload_1
    //   791: athrow
    //   792: astore_2
    //   793: goto -3 -> 790
    //   796: astore_2
    //   797: aload 11
    //   799: astore 8
    //   801: aload_1
    //   802: astore 12
    //   804: aload 10
    //   806: astore 9
    //   808: aload_2
    //   809: astore_1
    //   810: goto -50 -> 760
    //   813: astore 14
    //   815: goto -142 -> 673
    //   818: ldc_w 2224
    //   821: astore_3
    //   822: goto -617 -> 205
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	825	0	this	WindowManagerService
    //   0	825	1	paramSocket	java.net.Socket
    //   0	825	2	paramString1	String
    //   0	825	3	paramString2	String
    //   66	135	4	i	int
    //   62	168	5	j	int
    //   631	94	6	bool1	boolean
    //   10	634	7	bool2	boolean
    //   46	521	8	localObject1	Object
    //   653	13	8	localException1	Exception
    //   675	125	8	localObject2	Object
    //   54	753	9	localObject3	Object
    //   22	783	10	localObject4	Object
    //   16	782	11	localObject5	Object
    //   50	753	12	localObject6	Object
    //   35	635	13	localObject7	Object
    //   39	679	14	localObject8	Object
    //   813	1	14	localException2	Exception
    //   42	614	15	str	String
    //   31	608	16	localSocket	java.net.Socket
    //   25	638	17	localObject9	Object
    //   28	541	18	localObject10	Object
    //   19	423	19	localObject11	Object
    //   13	242	20	localObject12	Object
    //   234	240	21	localWindowState	WindowState
    // Exception table:
    //   from	to	target	type
    //   638	643	650	java/io/IOException
    //   56	64	653	java/lang/Exception
    //   97	103	653	java/lang/Exception
    //   126	141	653	java/lang/Exception
    //   164	173	653	java/lang/Exception
    //   196	205	653	java/lang/Exception
    //   228	236	653	java/lang/Exception
    //   266	271	653	java/lang/Exception
    //   294	302	653	java/lang/Exception
    //   325	331	653	java/lang/Exception
    //   354	360	653	java/lang/Exception
    //   383	389	653	java/lang/Exception
    //   412	422	653	java/lang/Exception
    //   445	450	653	java/lang/Exception
    //   473	495	653	java/lang/Exception
    //   518	523	653	java/lang/Exception
    //   546	553	653	java/lang/Exception
    //   576	595	653	java/lang/Exception
    //   750	754	756	java/io/IOException
    //   56	64	759	finally
    //   97	103	759	finally
    //   126	141	759	finally
    //   164	173	759	finally
    //   196	205	759	finally
    //   228	236	759	finally
    //   266	271	759	finally
    //   294	302	759	finally
    //   325	331	759	finally
    //   354	360	759	finally
    //   383	389	759	finally
    //   412	422	759	finally
    //   445	450	759	finally
    //   473	495	759	finally
    //   518	523	759	finally
    //   546	553	759	finally
    //   576	595	759	finally
    //   684	723	759	finally
    //   785	790	792	java/io/IOException
    //   595	606	796	finally
    //   595	606	813	java/lang/Exception
  }
  
  public int watchRotation(IRotationWatcher paramIRotationWatcher)
  {
    IBinder.DeathRecipient local9 = new IBinder.DeathRecipient()
    {
      public void binderDied()
      {
        HashMap localHashMap = WindowManagerService.this.mWindowMap;
        int i = 0;
        try
        {
          while (i < WindowManagerService.this.mRotationWatchers.size())
          {
            int j = i;
            if (this.val$watcherBinder == ((WindowManagerService.RotationWatcher)WindowManagerService.this.mRotationWatchers.get(i)).watcher.asBinder())
            {
              IBinder localIBinder = ((WindowManagerService.RotationWatcher)WindowManagerService.this.mRotationWatchers.remove(i)).watcher.asBinder();
              if (localIBinder != null) {
                localIBinder.unlinkToDeath(this, 0);
              }
              j = i - 1;
            }
            i = j + 1;
          }
          return;
        }
        finally {}
      }
    };
    try
    {
      synchronized (this.mWindowMap)
      {
        paramIRotationWatcher.asBinder().linkToDeath(local9, 0);
        this.mRotationWatchers.add(new RotationWatcher(paramIRotationWatcher, local9));
        int i = this.mRotation;
        return i;
      }
    }
    catch (RemoteException paramIRotationWatcher)
    {
      for (;;) {}
    }
  }
  
  final WindowState windowForClientLocked(Session paramSession, IBinder paramIBinder, boolean paramBoolean)
  {
    WindowState localWindowState = (WindowState)this.mWindowMap.get(paramIBinder);
    if (localLOGV) {
      Slog.v("WindowManager", "Looking up client " + paramIBinder + ": " + localWindowState);
    }
    if (localWindowState == null)
    {
      paramSession = new IllegalArgumentException("Requested window " + paramIBinder + " does not exist");
      if (paramBoolean) {
        throw paramSession;
      }
      Slog.w("WindowManager", "Failed looking up window", paramSession);
      return null;
    }
    if ((paramSession != null) && (localWindowState.mSession != paramSession))
    {
      paramSession = new IllegalArgumentException("Requested window " + paramIBinder + " is in session " + localWindowState.mSession + ", not " + paramSession);
      if (paramBoolean) {
        throw paramSession;
      }
      Slog.w("WindowManager", "Failed looking up window", paramSession);
      return null;
    }
    return localWindowState;
  }
  
  final WindowState windowForClientLocked(Session paramSession, IWindow paramIWindow, boolean paramBoolean)
  {
    return windowForClientLocked(paramSession, paramIWindow.asBinder(), paramBoolean);
  }
  
  final class DragInputEventReceiver
    extends InputEventReceiver
  {
    private boolean mIsStartEvent = true;
    private boolean mStylusButtonDownAtStart;
    
    public DragInputEventReceiver(InputChannel paramInputChannel, Looper paramLooper)
    {
      super(paramLooper);
    }
    
    /* Error */
    public void onInputEvent(android.view.InputEvent paramInputEvent)
    {
      // Byte code:
      //   0: iconst_0
      //   1: istore 6
      //   3: iload 6
      //   5: istore 5
      //   7: aload_1
      //   8: instanceof 28
      //   11: ifeq +178 -> 189
      //   14: iload 6
      //   16: istore 5
      //   18: aload_1
      //   19: invokevirtual 34	android/view/InputEvent:getSource	()I
      //   22: iconst_2
      //   23: iand
      //   24: ifeq +165 -> 189
      //   27: iload 6
      //   29: istore 5
      //   31: aload_0
      //   32: getfield 16	com/android/server/wm/WindowManagerService$DragInputEventReceiver:this$0	Lcom/android/server/wm/WindowManagerService;
      //   35: getfield 38	com/android/server/wm/WindowManagerService:mDragState	Lcom/android/server/wm/DragState;
      //   38: ifnull +151 -> 189
      //   41: aload_1
      //   42: checkcast 28	android/view/MotionEvent
      //   45: astore 7
      //   47: iconst_0
      //   48: istore 6
      //   50: aload 7
      //   52: invokevirtual 42	android/view/MotionEvent:getRawX	()F
      //   55: fstore_2
      //   56: aload 7
      //   58: invokevirtual 45	android/view/MotionEvent:getRawY	()F
      //   61: fstore_3
      //   62: aload 7
      //   64: invokevirtual 48	android/view/MotionEvent:getButtonState	()I
      //   67: bipush 32
      //   69: iand
      //   70: ifeq +127 -> 197
      //   73: iconst_1
      //   74: istore 4
      //   76: aload_0
      //   77: getfield 21	com/android/server/wm/WindowManagerService$DragInputEventReceiver:mIsStartEvent	Z
      //   80: ifeq +18 -> 98
      //   83: iload 4
      //   85: ifeq +8 -> 93
      //   88: aload_0
      //   89: iconst_1
      //   90: putfield 50	com/android/server/wm/WindowManagerService$DragInputEventReceiver:mStylusButtonDownAtStart	Z
      //   93: aload_0
      //   94: iconst_0
      //   95: putfield 21	com/android/server/wm/WindowManagerService$DragInputEventReceiver:mIsStartEvent	Z
      //   98: aload 7
      //   100: invokevirtual 53	android/view/MotionEvent:getAction	()I
      //   103: tableswitch	default:+394->497, 0:+100->203, 1:+289->392, 2:+144->247, 3:+369->472
      //   132: iload 5
      //   134: ifeq +52 -> 186
      //   137: getstatic 58	com/android/server/wm/WindowManagerDebugConfig:DEBUG_DRAG	Z
      //   140: ifeq +11 -> 151
      //   143: ldc 60
      //   145: ldc 62
      //   147: invokestatic 68	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   150: pop
      //   151: aload_0
      //   152: getfield 16	com/android/server/wm/WindowManagerService$DragInputEventReceiver:this$0	Lcom/android/server/wm/WindowManagerService;
      //   155: getfield 72	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
      //   158: astore 7
      //   160: aload 7
      //   162: monitorenter
      //   163: aload_0
      //   164: getfield 16	com/android/server/wm/WindowManagerService$DragInputEventReceiver:this$0	Lcom/android/server/wm/WindowManagerService;
      //   167: getfield 38	com/android/server/wm/WindowManagerService:mDragState	Lcom/android/server/wm/DragState;
      //   170: invokevirtual 78	com/android/server/wm/DragState:endDragLw	()V
      //   173: aload 7
      //   175: monitorexit
      //   176: aload_0
      //   177: iconst_0
      //   178: putfield 50	com/android/server/wm/WindowManagerService$DragInputEventReceiver:mStylusButtonDownAtStart	Z
      //   181: aload_0
      //   182: iconst_1
      //   183: putfield 21	com/android/server/wm/WindowManagerService$DragInputEventReceiver:mIsStartEvent	Z
      //   186: iconst_1
      //   187: istore 5
      //   189: aload_0
      //   190: aload_1
      //   191: iload 5
      //   193: invokevirtual 82	com/android/server/wm/WindowManagerService$DragInputEventReceiver:finishInputEvent	(Landroid/view/InputEvent;Z)V
      //   196: return
      //   197: iconst_0
      //   198: istore 4
      //   200: goto -124 -> 76
      //   203: iload 6
      //   205: istore 5
      //   207: getstatic 58	com/android/server/wm/WindowManagerDebugConfig:DEBUG_DRAG	Z
      //   210: ifeq -78 -> 132
      //   213: ldc 60
      //   215: ldc 84
      //   217: invokestatic 87	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   220: pop
      //   221: iload 6
      //   223: istore 5
      //   225: goto -93 -> 132
      //   228: astore 7
      //   230: ldc 60
      //   232: ldc 89
      //   234: aload 7
      //   236: invokestatic 93	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   239: pop
      //   240: aload_0
      //   241: aload_1
      //   242: iconst_0
      //   243: invokevirtual 82	com/android/server/wm/WindowManagerService$DragInputEventReceiver:finishInputEvent	(Landroid/view/InputEvent;Z)V
      //   246: return
      //   247: aload_0
      //   248: getfield 50	com/android/server/wm/WindowManagerService$DragInputEventReceiver:mStylusButtonDownAtStart	Z
      //   251: ifeq +8 -> 259
      //   254: iload 4
      //   256: ifeq +48 -> 304
      //   259: aload_0
      //   260: getfield 16	com/android/server/wm/WindowManagerService$DragInputEventReceiver:this$0	Lcom/android/server/wm/WindowManagerService;
      //   263: getfield 72	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
      //   266: astore 7
      //   268: aload 7
      //   270: monitorenter
      //   271: aload_0
      //   272: getfield 16	com/android/server/wm/WindowManagerService$DragInputEventReceiver:this$0	Lcom/android/server/wm/WindowManagerService;
      //   275: getfield 38	com/android/server/wm/WindowManagerService:mDragState	Lcom/android/server/wm/DragState;
      //   278: fload_2
      //   279: fload_3
      //   280: invokevirtual 97	com/android/server/wm/DragState:notifyMoveLw	(FF)V
      //   283: aload 7
      //   285: monitorexit
      //   286: iload 6
      //   288: istore 5
      //   290: goto -158 -> 132
      //   293: astore 7
      //   295: aload_0
      //   296: aload_1
      //   297: iconst_0
      //   298: invokevirtual 82	com/android/server/wm/WindowManagerService$DragInputEventReceiver:finishInputEvent	(Landroid/view/InputEvent;Z)V
      //   301: aload 7
      //   303: athrow
      //   304: getstatic 58	com/android/server/wm/WindowManagerDebugConfig:DEBUG_DRAG	Z
      //   307: ifeq +37 -> 344
      //   310: ldc 60
      //   312: new 99	java/lang/StringBuilder
      //   315: dup
      //   316: invokespecial 101	java/lang/StringBuilder:<init>	()V
      //   319: ldc 103
      //   321: invokevirtual 107	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   324: fload_2
      //   325: invokevirtual 110	java/lang/StringBuilder:append	(F)Ljava/lang/StringBuilder;
      //   328: ldc 112
      //   330: invokevirtual 107	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   333: fload_3
      //   334: invokevirtual 110	java/lang/StringBuilder:append	(F)Ljava/lang/StringBuilder;
      //   337: invokevirtual 116	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   340: invokestatic 68	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   343: pop
      //   344: aload_0
      //   345: getfield 16	com/android/server/wm/WindowManagerService$DragInputEventReceiver:this$0	Lcom/android/server/wm/WindowManagerService;
      //   348: getfield 72	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
      //   351: astore 7
      //   353: aload 7
      //   355: monitorenter
      //   356: aload_0
      //   357: getfield 16	com/android/server/wm/WindowManagerService$DragInputEventReceiver:this$0	Lcom/android/server/wm/WindowManagerService;
      //   360: getfield 38	com/android/server/wm/WindowManagerService:mDragState	Lcom/android/server/wm/DragState;
      //   363: fload_2
      //   364: fload_3
      //   365: invokevirtual 120	com/android/server/wm/DragState:notifyDropLw	(FF)Z
      //   368: istore 5
      //   370: aload 7
      //   372: monitorexit
      //   373: goto -241 -> 132
      //   376: astore 8
      //   378: aload 7
      //   380: monitorexit
      //   381: aload 8
      //   383: athrow
      //   384: astore 8
      //   386: aload 7
      //   388: monitorexit
      //   389: aload 8
      //   391: athrow
      //   392: getstatic 58	com/android/server/wm/WindowManagerDebugConfig:DEBUG_DRAG	Z
      //   395: ifeq +37 -> 432
      //   398: ldc 60
      //   400: new 99	java/lang/StringBuilder
      //   403: dup
      //   404: invokespecial 101	java/lang/StringBuilder:<init>	()V
      //   407: ldc 122
      //   409: invokevirtual 107	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   412: fload_2
      //   413: invokevirtual 110	java/lang/StringBuilder:append	(F)Ljava/lang/StringBuilder;
      //   416: ldc 112
      //   418: invokevirtual 107	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   421: fload_3
      //   422: invokevirtual 110	java/lang/StringBuilder:append	(F)Ljava/lang/StringBuilder;
      //   425: invokevirtual 116	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   428: invokestatic 68	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   431: pop
      //   432: aload_0
      //   433: getfield 16	com/android/server/wm/WindowManagerService$DragInputEventReceiver:this$0	Lcom/android/server/wm/WindowManagerService;
      //   436: getfield 72	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
      //   439: astore 7
      //   441: aload 7
      //   443: monitorenter
      //   444: aload_0
      //   445: getfield 16	com/android/server/wm/WindowManagerService$DragInputEventReceiver:this$0	Lcom/android/server/wm/WindowManagerService;
      //   448: getfield 38	com/android/server/wm/WindowManagerService:mDragState	Lcom/android/server/wm/DragState;
      //   451: fload_2
      //   452: fload_3
      //   453: invokevirtual 120	com/android/server/wm/DragState:notifyDropLw	(FF)Z
      //   456: istore 5
      //   458: aload 7
      //   460: monitorexit
      //   461: goto -329 -> 132
      //   464: astore 8
      //   466: aload 7
      //   468: monitorexit
      //   469: aload 8
      //   471: athrow
      //   472: getstatic 58	com/android/server/wm/WindowManagerDebugConfig:DEBUG_DRAG	Z
      //   475: ifeq +29 -> 504
      //   478: ldc 60
      //   480: ldc 124
      //   482: invokestatic 68	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   485: pop
      //   486: goto +18 -> 504
      //   489: astore 8
      //   491: aload 7
      //   493: monitorexit
      //   494: aload 8
      //   496: athrow
      //   497: iload 6
      //   499: istore 5
      //   501: goto -369 -> 132
      //   504: iconst_1
      //   505: istore 5
      //   507: goto -375 -> 132
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	510	0	this	DragInputEventReceiver
      //   0	510	1	paramInputEvent	android.view.InputEvent
      //   55	397	2	f1	float
      //   61	392	3	f2	float
      //   74	181	4	i	int
      //   5	501	5	bool1	boolean
      //   1	497	6	bool2	boolean
      //   228	7	7	localException	Exception
      //   293	9	7	localObject2	Object
      //   376	6	8	localObject3	Object
      //   384	6	8	localObject4	Object
      //   464	6	8	localObject5	Object
      //   489	6	8	localObject6	Object
      // Exception table:
      //   from	to	target	type
      //   7	14	228	java/lang/Exception
      //   18	27	228	java/lang/Exception
      //   31	47	228	java/lang/Exception
      //   50	73	228	java/lang/Exception
      //   76	83	228	java/lang/Exception
      //   88	93	228	java/lang/Exception
      //   93	98	228	java/lang/Exception
      //   98	132	228	java/lang/Exception
      //   137	151	228	java/lang/Exception
      //   151	163	228	java/lang/Exception
      //   173	186	228	java/lang/Exception
      //   207	221	228	java/lang/Exception
      //   247	254	228	java/lang/Exception
      //   259	271	228	java/lang/Exception
      //   283	286	228	java/lang/Exception
      //   304	344	228	java/lang/Exception
      //   344	356	228	java/lang/Exception
      //   370	373	228	java/lang/Exception
      //   378	384	228	java/lang/Exception
      //   386	392	228	java/lang/Exception
      //   392	432	228	java/lang/Exception
      //   432	444	228	java/lang/Exception
      //   458	461	228	java/lang/Exception
      //   466	472	228	java/lang/Exception
      //   472	486	228	java/lang/Exception
      //   491	497	228	java/lang/Exception
      //   7	14	293	finally
      //   18	27	293	finally
      //   31	47	293	finally
      //   50	73	293	finally
      //   76	83	293	finally
      //   88	93	293	finally
      //   93	98	293	finally
      //   98	132	293	finally
      //   137	151	293	finally
      //   151	163	293	finally
      //   173	186	293	finally
      //   207	221	293	finally
      //   230	240	293	finally
      //   247	254	293	finally
      //   259	271	293	finally
      //   283	286	293	finally
      //   304	344	293	finally
      //   344	356	293	finally
      //   370	373	293	finally
      //   378	384	293	finally
      //   386	392	293	finally
      //   392	432	293	finally
      //   432	444	293	finally
      //   458	461	293	finally
      //   466	472	293	finally
      //   472	486	293	finally
      //   491	497	293	finally
      //   356	370	376	finally
      //   271	283	384	finally
      //   444	458	464	finally
      //   163	173	489	finally
    }
  }
  
  final class H
    extends Handler
  {
    public static final int ADD_STARTING = 5;
    public static final int ALL_WINDOWS_DRAWN = 33;
    public static final int APP_FREEZE_TIMEOUT = 17;
    public static final int APP_TRANSITION_TIMEOUT = 13;
    public static final int BOOT_TIMEOUT = 23;
    public static final int CHECK_IF_BOOT_ANIMATION_FINISHED = 37;
    public static final int CLIENT_FREEZE_TIMEOUT = 30;
    public static final int DO_ANIMATION_CALLBACK = 26;
    public static final int DO_DISPLAY_ADDED = 27;
    public static final int DO_DISPLAY_CHANGED = 29;
    public static final int DO_DISPLAY_REMOVED = 28;
    public static final int DO_TRAVERSAL = 4;
    public static final int DRAG_END_TIMEOUT = 21;
    public static final int DRAG_START_TIMEOUT = 20;
    public static final int ENABLE_SCREEN = 16;
    public static final int FINISHED_STARTING = 7;
    public static final int FINISH_TASK_POSITIONING = 40;
    public static final int FORCE_GC = 15;
    public static final int NEW_ANIMATOR_SCALE = 34;
    public static final int NOTIFY_ACTIVITY_DRAWN = 32;
    public static final int NOTIFY_APP_TRANSITION_CANCELLED = 48;
    public static final int NOTIFY_APP_TRANSITION_FINISHED = 49;
    public static final int NOTIFY_APP_TRANSITION_STARTING = 47;
    public static final int NOTIFY_DOCKED_STACK_MINIMIZED_CHANGED = 53;
    public static final int NOTIFY_STARTING_WINDOW_DRAWN = 50;
    public static final int PERSIST_ANIMATION_SCALE = 14;
    public static final int REMOVE_STARTING = 6;
    public static final int REPORT_APPLICATION_TOKEN_DRAWN = 9;
    public static final int REPORT_APPLICATION_TOKEN_WINDOWS = 8;
    public static final int REPORT_FOCUS_CHANGE = 2;
    public static final int REPORT_HARD_KEYBOARD_STATUS_CHANGE = 22;
    public static final int REPORT_LOSING_FOCUS = 3;
    public static final int REPORT_WINDOWS_CHANGE = 19;
    public static final int RESET_ANR_MESSAGE = 38;
    public static final int RESIZE_STACK = 42;
    public static final int RESIZE_TASK = 43;
    public static final int SEAMLESS_ROTATION_TIMEOUT = 54;
    public static final int SEND_NEW_CONFIGURATION = 18;
    public static final int SHOW_CIRCULAR_DISPLAY_MASK = 35;
    public static final int SHOW_EMULATOR_DISPLAY_OVERLAY = 36;
    public static final int SHOW_STRICT_MODE_VIOLATION = 25;
    public static final int TAP_OUTSIDE_TASK = 31;
    public static final int TWO_FINGER_SCROLL_START = 44;
    public static final int UNUSED = 0;
    public static final int UPDATE_ANIMATION_SCALE = 51;
    public static final int UPDATE_DOCKED_STACK_DIVIDER = 41;
    public static final int WAITING_FOR_DRAWN_TIMEOUT = 24;
    public static final int WALLPAPER_DRAW_PENDING_TIMEOUT = 39;
    public static final int WINDOW_FREEZE_TIMEOUT = 11;
    public static final int WINDOW_HIDE_TIMEOUT = 52;
    public static final int WINDOW_REPLACEMENT_TIMEOUT = 46;
    
    H() {}
    
    /* Error */
    public void handleMessage(Message arg1)
    {
      // Byte code:
      //   0: getstatic 132	com/android/server/wm/WindowManagerDebugConfig:DEBUG_WINDOW_TRACE	Z
      //   3: ifeq +31 -> 34
      //   6: ldc -122
      //   8: new 136	java/lang/StringBuilder
      //   11: dup
      //   12: invokespecial 137	java/lang/StringBuilder:<init>	()V
      //   15: ldc -117
      //   17: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   20: aload_1
      //   21: getfield 148	android/os/Message:what	I
      //   24: invokevirtual 151	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   27: invokevirtual 155	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   30: invokestatic 161	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
      //   33: pop
      //   34: aload_1
      //   35: getfield 148	android/os/Message:what	I
      //   38: tableswitch	default:+226->264, 2:+241->279, 3:+553->591, 4:+673->711, 5:+705->743, 6:+1166->1204, 7:+1344->1382, 8:+1596->1634, 9:+1540->1578, 10:+226->264, 11:+1711->1749, 12:+226->264, 13:+1861->1899, 14:+2082->2120, 15:+2296->2334, 16:+2377->2415, 17:+2387->2425, 18:+2652->2690, 19:+2668->2706, 20:+2715->2753, 21:+2831->2869, 22:+2929->2967, 23:+2939->2977, 24:+2949->2987, 25:+3042->3080, 26:+3103->3141, 27:+3123->3161, 28:+3137->3175, 29:+3172->3210, 30:+2590->2628, 31:+3232->3270, 32:+3267->3305, 33:+3293->3331, 34:+3337->3375, 35:+3060->3098, 36:+3093->3131, 37:+3509->3547, 38:+3567->3605, 39:+3595->3633, 40:+3257->3295, 41:+3630->3668, 42:+3718->3756, 43:+3684->3722, 44:+3207->3245, 45:+226->264, 46:+3780->3818, 47:+3846->3884, 48:+3869->3907, 49:+3882->3920, 50:+3895->3933, 51:+2157->2195, 52:+3908->3946, 53:+3978->4016, 54:+4014->4052
      //   264: getstatic 132	com/android/server/wm/WindowManagerDebugConfig:DEBUG_WINDOW_TRACE	Z
      //   267: ifeq +11 -> 278
      //   270: ldc -122
      //   272: ldc -93
      //   274: invokestatic 161	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
      //   277: pop
      //   278: return
      //   279: aconst_null
      //   280: astore 9
      //   282: aload_0
      //   283: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   286: getfield 167	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
      //   289: astore 11
      //   291: aload 11
      //   293: monitorenter
      //   294: aload 9
      //   296: astore_1
      //   297: aload_0
      //   298: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   301: getfield 171	com/android/server/wm/WindowManagerService:mAccessibilityController	Lcom/android/server/wm/AccessibilityController;
      //   304: ifnull +27 -> 331
      //   307: aload 9
      //   309: astore_1
      //   310: aload_0
      //   311: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   314: invokevirtual 175	com/android/server/wm/WindowManagerService:getDefaultDisplayContentLocked	()Lcom/android/server/wm/DisplayContent;
      //   317: invokevirtual 181	com/android/server/wm/DisplayContent:getDisplayId	()I
      //   320: ifne +11 -> 331
      //   323: aload_0
      //   324: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   327: getfield 171	com/android/server/wm/WindowManagerService:mAccessibilityController	Lcom/android/server/wm/AccessibilityController;
      //   330: astore_1
      //   331: aload_0
      //   332: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   335: getfield 185	com/android/server/wm/WindowManagerService:mLastFocus	Lcom/android/server/wm/WindowState;
      //   338: astore 10
      //   340: aload_0
      //   341: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   344: getfield 188	com/android/server/wm/WindowManagerService:mCurrentFocus	Lcom/android/server/wm/WindowState;
      //   347: astore 12
      //   349: aload 10
      //   351: aload 12
      //   353: if_acmpne +7 -> 360
      //   356: aload 11
      //   358: monitorexit
      //   359: return
      //   360: aload_0
      //   361: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   364: aload 12
      //   366: putfield 185	com/android/server/wm/WindowManagerService:mLastFocus	Lcom/android/server/wm/WindowState;
      //   369: getstatic 191	com/android/server/wm/WindowManagerDebugConfig:DEBUG_FOCUS_LIGHT	Z
      //   372: ifeq +39 -> 411
      //   375: ldc -122
      //   377: new 136	java/lang/StringBuilder
      //   380: dup
      //   381: invokespecial 137	java/lang/StringBuilder:<init>	()V
      //   384: ldc -63
      //   386: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   389: aload 10
      //   391: invokevirtual 196	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   394: ldc -58
      //   396: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   399: aload 12
      //   401: invokevirtual 196	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   404: invokevirtual 155	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   407: invokestatic 201	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
      //   410: pop
      //   411: aload 10
      //   413: astore 9
      //   415: aload 12
      //   417: ifnull +28 -> 445
      //   420: aload 10
      //   422: astore 9
      //   424: aload 10
      //   426: ifnull +19 -> 445
      //   429: aload 12
      //   431: invokevirtual 207	com/android/server/wm/WindowState:isDisplayedLw	()Z
      //   434: istore 7
      //   436: iload 7
      //   438: ifeq +128 -> 566
      //   441: aload 10
      //   443: astore 9
      //   445: aload 11
      //   447: monitorexit
      //   448: aload_1
      //   449: ifnull +7 -> 456
      //   452: aload_1
      //   453: invokevirtual 212	com/android/server/wm/AccessibilityController:onWindowFocusChangedNotLocked	()V
      //   456: aload 12
      //   458: ifnull +55 -> 513
      //   461: getstatic 191	com/android/server/wm/WindowManagerDebugConfig:DEBUG_FOCUS_LIGHT	Z
      //   464: ifeq +29 -> 493
      //   467: ldc -122
      //   469: new 136	java/lang/StringBuilder
      //   472: dup
      //   473: invokespecial 137	java/lang/StringBuilder:<init>	()V
      //   476: ldc -42
      //   478: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   481: aload 12
      //   483: invokevirtual 196	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   486: invokevirtual 155	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   489: invokestatic 201	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
      //   492: pop
      //   493: aload 12
      //   495: iconst_1
      //   496: aload_0
      //   497: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   500: getfield 217	com/android/server/wm/WindowManagerService:mInTouchMode	Z
      //   503: invokevirtual 221	com/android/server/wm/WindowState:reportFocusChangedSerialized	(ZZ)V
      //   506: aload_0
      //   507: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   510: invokestatic 224	com/android/server/wm/WindowManagerService:-wrap6	(Lcom/android/server/wm/WindowManagerService;)V
      //   513: aload 9
      //   515: ifnull -251 -> 264
      //   518: getstatic 191	com/android/server/wm/WindowManagerDebugConfig:DEBUG_FOCUS_LIGHT	Z
      //   521: ifeq +29 -> 550
      //   524: ldc -122
      //   526: new 136	java/lang/StringBuilder
      //   529: dup
      //   530: invokespecial 137	java/lang/StringBuilder:<init>	()V
      //   533: ldc -30
      //   535: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   538: aload 9
      //   540: invokevirtual 196	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   543: invokevirtual 155	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   546: invokestatic 201	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
      //   549: pop
      //   550: aload 9
      //   552: iconst_0
      //   553: aload_0
      //   554: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   557: getfield 217	com/android/server/wm/WindowManagerService:mInTouchMode	Z
      //   560: invokevirtual 221	com/android/server/wm/WindowState:reportFocusChangedSerialized	(ZZ)V
      //   563: goto -299 -> 264
      //   566: aload_0
      //   567: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   570: getfield 230	com/android/server/wm/WindowManagerService:mLosingFocus	Ljava/util/ArrayList;
      //   573: aload 10
      //   575: invokevirtual 236	java/util/ArrayList:add	(Ljava/lang/Object;)Z
      //   578: pop
      //   579: aconst_null
      //   580: astore 9
      //   582: goto -137 -> 445
      //   585: astore_1
      //   586: aload 11
      //   588: monitorexit
      //   589: aload_1
      //   590: athrow
      //   591: aload_0
      //   592: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   595: getfield 167	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
      //   598: astore_1
      //   599: aload_1
      //   600: monitorenter
      //   601: aload_0
      //   602: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   605: getfield 230	com/android/server/wm/WindowManagerService:mLosingFocus	Ljava/util/ArrayList;
      //   608: astore 9
      //   610: aload_0
      //   611: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   614: new 232	java/util/ArrayList
      //   617: dup
      //   618: invokespecial 237	java/util/ArrayList:<init>	()V
      //   621: putfield 230	com/android/server/wm/WindowManagerService:mLosingFocus	Ljava/util/ArrayList;
      //   624: aload_1
      //   625: monitorexit
      //   626: aload 9
      //   628: invokevirtual 240	java/util/ArrayList:size	()I
      //   631: istore 4
      //   633: iconst_0
      //   634: istore_3
      //   635: iload_3
      //   636: iload 4
      //   638: if_icmpge -374 -> 264
      //   641: getstatic 191	com/android/server/wm/WindowManagerDebugConfig:DEBUG_FOCUS_LIGHT	Z
      //   644: ifeq +33 -> 677
      //   647: ldc -122
      //   649: new 136	java/lang/StringBuilder
      //   652: dup
      //   653: invokespecial 137	java/lang/StringBuilder:<init>	()V
      //   656: ldc -14
      //   658: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   661: aload 9
      //   663: iload_3
      //   664: invokevirtual 246	java/util/ArrayList:get	(I)Ljava/lang/Object;
      //   667: invokevirtual 196	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   670: invokevirtual 155	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   673: invokestatic 201	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
      //   676: pop
      //   677: aload 9
      //   679: iload_3
      //   680: invokevirtual 246	java/util/ArrayList:get	(I)Ljava/lang/Object;
      //   683: checkcast 203	com/android/server/wm/WindowState
      //   686: iconst_0
      //   687: aload_0
      //   688: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   691: getfield 217	com/android/server/wm/WindowManagerService:mInTouchMode	Z
      //   694: invokevirtual 221	com/android/server/wm/WindowState:reportFocusChangedSerialized	(ZZ)V
      //   697: iload_3
      //   698: iconst_1
      //   699: iadd
      //   700: istore_3
      //   701: goto -66 -> 635
      //   704: astore 9
      //   706: aload_1
      //   707: monitorexit
      //   708: aload 9
      //   710: athrow
      //   711: aload_0
      //   712: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   715: getfield 167	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
      //   718: astore_1
      //   719: aload_1
      //   720: monitorenter
      //   721: aload_0
      //   722: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   725: getfield 250	com/android/server/wm/WindowManagerService:mWindowPlacerLocked	Lcom/android/server/wm/WindowSurfacePlacer;
      //   728: invokevirtual 255	com/android/server/wm/WindowSurfacePlacer:performSurfacePlacement	()V
      //   731: aload_1
      //   732: monitorexit
      //   733: goto -469 -> 264
      //   736: astore 9
      //   738: aload_1
      //   739: monitorexit
      //   740: aload 9
      //   742: athrow
      //   743: aload_1
      //   744: getfield 259	android/os/Message:obj	Ljava/lang/Object;
      //   747: checkcast 261	com/android/server/wm/AppWindowToken
      //   750: astore 10
      //   752: aload 10
      //   754: getfield 265	com/android/server/wm/AppWindowToken:startingData	Lcom/android/server/wm/StartingData;
      //   757: astore 11
      //   759: aload 11
      //   761: ifnonnull +4 -> 765
      //   764: return
      //   765: getstatic 268	com/android/server/wm/WindowManagerDebugConfig:DEBUG_STARTING_WINDOW	Z
      //   768: ifeq +44 -> 812
      //   771: ldc -122
      //   773: new 136	java/lang/StringBuilder
      //   776: dup
      //   777: invokespecial 137	java/lang/StringBuilder:<init>	()V
      //   780: ldc_w 270
      //   783: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   786: aload 10
      //   788: invokevirtual 196	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   791: ldc_w 272
      //   794: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   797: aload 11
      //   799: getfield 278	com/android/server/wm/StartingData:pkg	Ljava/lang/String;
      //   802: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   805: invokevirtual 155	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   808: invokestatic 161	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
      //   811: pop
      //   812: getstatic 283	android/os/Build:AUTO_TEST_ONEPLUS	Z
      //   815: ifeq +37 -> 852
      //   818: ldc_w 285
      //   821: new 136	java/lang/StringBuilder
      //   824: dup
      //   825: invokespecial 137	java/lang/StringBuilder:<init>	()V
      //   828: invokestatic 291	android/os/SystemClock:uptimeMillis	()J
      //   831: invokevirtual 294	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
      //   834: ldc_w 296
      //   837: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   840: aload 10
      //   842: invokevirtual 196	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   845: invokevirtual 155	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   848: invokestatic 299	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   851: pop
      //   852: aconst_null
      //   853: astore 9
      //   855: aload 10
      //   857: ifnull +252 -> 1109
      //   860: aload 10
      //   862: getfield 303	com/android/server/wm/AppWindowToken:mTask	Lcom/android/server/wm/Task;
      //   865: ifnull +244 -> 1109
      //   868: aload 10
      //   870: getfield 303	com/android/server/wm/AppWindowToken:mTask	Lcom/android/server/wm/Task;
      //   873: getfield 309	com/android/server/wm/Task:mOverrideConfig	Landroid/content/res/Configuration;
      //   876: astore_1
      //   877: aload_0
      //   878: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   881: getfield 313	com/android/server/wm/WindowManagerService:mPolicy	Landroid/view/WindowManagerPolicy;
      //   884: aload 10
      //   886: getfield 317	com/android/server/wm/AppWindowToken:token	Landroid/os/IBinder;
      //   889: aload 11
      //   891: getfield 278	com/android/server/wm/StartingData:pkg	Ljava/lang/String;
      //   894: aload 11
      //   896: getfield 320	com/android/server/wm/StartingData:theme	I
      //   899: aload 11
      //   901: getfield 324	com/android/server/wm/StartingData:compatInfo	Landroid/content/res/CompatibilityInfo;
      //   904: aload 11
      //   906: getfield 328	com/android/server/wm/StartingData:nonLocalizedLabel	Ljava/lang/CharSequence;
      //   909: aload 11
      //   911: getfield 331	com/android/server/wm/StartingData:labelRes	I
      //   914: aload 11
      //   916: getfield 334	com/android/server/wm/StartingData:icon	I
      //   919: aload 11
      //   921: getfield 337	com/android/server/wm/StartingData:logo	I
      //   924: aload 11
      //   926: getfield 340	com/android/server/wm/StartingData:windowFlags	I
      //   929: aload_1
      //   930: invokeinterface 346 11 0
      //   935: astore_1
      //   936: aload_1
      //   937: ifnull -673 -> 264
      //   940: iconst_0
      //   941: istore_3
      //   942: aload_0
      //   943: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   946: getfield 167	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
      //   949: astore 9
      //   951: aload 9
      //   953: monitorenter
      //   954: aload 10
      //   956: getfield 349	com/android/server/wm/AppWindowToken:removed	Z
      //   959: ifne +11 -> 970
      //   962: aload 10
      //   964: getfield 265	com/android/server/wm/AppWindowToken:startingData	Lcom/android/server/wm/StartingData;
      //   967: ifnonnull +164 -> 1131
      //   970: aload 10
      //   972: getfield 352	com/android/server/wm/AppWindowToken:startingWindow	Lcom/android/server/wm/WindowState;
      //   975: ifnull +78 -> 1053
      //   978: getstatic 268	com/android/server/wm/WindowManagerDebugConfig:DEBUG_STARTING_WINDOW	Z
      //   981: ifeq +58 -> 1039
      //   984: ldc -122
      //   986: new 136	java/lang/StringBuilder
      //   989: dup
      //   990: invokespecial 137	java/lang/StringBuilder:<init>	()V
      //   993: ldc_w 354
      //   996: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   999: aload 10
      //   1001: invokevirtual 196	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   1004: ldc_w 356
      //   1007: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1010: aload 10
      //   1012: getfield 349	com/android/server/wm/AppWindowToken:removed	Z
      //   1015: invokevirtual 359	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
      //   1018: ldc_w 361
      //   1021: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1024: aload 10
      //   1026: getfield 265	com/android/server/wm/AppWindowToken:startingData	Lcom/android/server/wm/StartingData;
      //   1029: invokevirtual 196	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   1032: invokevirtual 155	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   1035: invokestatic 161	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
      //   1038: pop
      //   1039: aload 10
      //   1041: aconst_null
      //   1042: putfield 352	com/android/server/wm/AppWindowToken:startingWindow	Lcom/android/server/wm/WindowState;
      //   1045: aload 10
      //   1047: aconst_null
      //   1048: putfield 265	com/android/server/wm/AppWindowToken:startingData	Lcom/android/server/wm/StartingData;
      //   1051: iconst_1
      //   1052: istore_3
      //   1053: getstatic 268	com/android/server/wm/WindowManagerDebugConfig:DEBUG_STARTING_WINDOW	Z
      //   1056: istore 7
      //   1058: iload 7
      //   1060: ifeq +7 -> 1067
      //   1063: iload_3
      //   1064: ifeq +82 -> 1146
      //   1067: aload 9
      //   1069: monitorexit
      //   1070: iload_3
      //   1071: ifeq -807 -> 264
      //   1074: aload_0
      //   1075: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   1078: getfield 313	com/android/server/wm/WindowManagerService:mPolicy	Landroid/view/WindowManagerPolicy;
      //   1081: aload 10
      //   1083: getfield 317	com/android/server/wm/AppWindowToken:token	Landroid/os/IBinder;
      //   1086: aload_1
      //   1087: invokeinterface 365 3 0
      //   1092: goto -828 -> 264
      //   1095: astore_1
      //   1096: ldc -122
      //   1098: ldc_w 367
      //   1101: aload_1
      //   1102: invokestatic 371	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   1105: pop
      //   1106: goto -842 -> 264
      //   1109: aconst_null
      //   1110: astore_1
      //   1111: goto -234 -> 877
      //   1114: astore_1
      //   1115: ldc -122
      //   1117: ldc_w 373
      //   1120: aload_1
      //   1121: invokestatic 371	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   1124: pop
      //   1125: aload 9
      //   1127: astore_1
      //   1128: goto -192 -> 936
      //   1131: aload 10
      //   1133: aload_1
      //   1134: putfield 377	com/android/server/wm/AppWindowToken:startingView	Landroid/view/View;
      //   1137: goto -84 -> 1053
      //   1140: astore_1
      //   1141: aload 9
      //   1143: monitorexit
      //   1144: aload_1
      //   1145: athrow
      //   1146: ldc -122
      //   1148: new 136	java/lang/StringBuilder
      //   1151: dup
      //   1152: invokespecial 137	java/lang/StringBuilder:<init>	()V
      //   1155: ldc_w 379
      //   1158: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1161: aload 10
      //   1163: invokevirtual 196	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   1166: ldc_w 381
      //   1169: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1172: aload 10
      //   1174: getfield 352	com/android/server/wm/AppWindowToken:startingWindow	Lcom/android/server/wm/WindowState;
      //   1177: invokevirtual 196	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   1180: ldc_w 383
      //   1183: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1186: aload 10
      //   1188: getfield 377	com/android/server/wm/AppWindowToken:startingView	Landroid/view/View;
      //   1191: invokevirtual 196	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   1194: invokevirtual 155	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   1197: invokestatic 161	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
      //   1200: pop
      //   1201: goto -134 -> 1067
      //   1204: aload_1
      //   1205: getfield 259	android/os/Message:obj	Ljava/lang/Object;
      //   1208: checkcast 261	com/android/server/wm/AppWindowToken
      //   1211: astore 11
      //   1213: aconst_null
      //   1214: astore_1
      //   1215: aconst_null
      //   1216: astore 9
      //   1218: aload_0
      //   1219: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   1222: getfield 167	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
      //   1225: astore 10
      //   1227: aload 10
      //   1229: monitorenter
      //   1230: getstatic 268	com/android/server/wm/WindowManagerDebugConfig:DEBUG_STARTING_WINDOW	Z
      //   1233: ifeq +58 -> 1291
      //   1236: ldc -122
      //   1238: new 136	java/lang/StringBuilder
      //   1241: dup
      //   1242: invokespecial 137	java/lang/StringBuilder:<init>	()V
      //   1245: ldc_w 385
      //   1248: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1251: aload 11
      //   1253: invokevirtual 196	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   1256: ldc_w 381
      //   1259: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1262: aload 11
      //   1264: getfield 352	com/android/server/wm/AppWindowToken:startingWindow	Lcom/android/server/wm/WindowState;
      //   1267: invokevirtual 196	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   1270: ldc_w 383
      //   1273: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1276: aload 11
      //   1278: getfield 377	com/android/server/wm/AppWindowToken:startingView	Landroid/view/View;
      //   1281: invokevirtual 196	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   1284: invokevirtual 155	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   1287: invokestatic 161	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
      //   1290: pop
      //   1291: aload 11
      //   1293: getfield 352	com/android/server/wm/AppWindowToken:startingWindow	Lcom/android/server/wm/WindowState;
      //   1296: ifnull +40 -> 1336
      //   1299: aload 11
      //   1301: getfield 377	com/android/server/wm/AppWindowToken:startingView	Landroid/view/View;
      //   1304: astore 9
      //   1306: aload 11
      //   1308: getfield 317	com/android/server/wm/AppWindowToken:token	Landroid/os/IBinder;
      //   1311: astore_1
      //   1312: aload 11
      //   1314: aconst_null
      //   1315: putfield 265	com/android/server/wm/AppWindowToken:startingData	Lcom/android/server/wm/StartingData;
      //   1318: aload 11
      //   1320: aconst_null
      //   1321: putfield 377	com/android/server/wm/AppWindowToken:startingView	Landroid/view/View;
      //   1324: aload 11
      //   1326: aconst_null
      //   1327: putfield 352	com/android/server/wm/AppWindowToken:startingWindow	Lcom/android/server/wm/WindowState;
      //   1330: aload 11
      //   1332: iconst_0
      //   1333: putfield 388	com/android/server/wm/AppWindowToken:startingDisplayed	Z
      //   1336: aload 10
      //   1338: monitorexit
      //   1339: aload 9
      //   1341: ifnull -1077 -> 264
      //   1344: aload_0
      //   1345: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   1348: getfield 313	com/android/server/wm/WindowManagerService:mPolicy	Landroid/view/WindowManagerPolicy;
      //   1351: aload_1
      //   1352: aload 9
      //   1354: invokeinterface 365 3 0
      //   1359: goto -1095 -> 264
      //   1362: astore_1
      //   1363: ldc -122
      //   1365: ldc_w 367
      //   1368: aload_1
      //   1369: invokestatic 371	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   1372: pop
      //   1373: goto -1109 -> 264
      //   1376: astore_1
      //   1377: aload 10
      //   1379: monitorexit
      //   1380: aload_1
      //   1381: athrow
      //   1382: aload_0
      //   1383: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   1386: getfield 167	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
      //   1389: astore 9
      //   1391: aload 9
      //   1393: monitorenter
      //   1394: aload_0
      //   1395: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   1398: getfield 391	com/android/server/wm/WindowManagerService:mFinishedStarting	Ljava/util/ArrayList;
      //   1401: invokevirtual 240	java/util/ArrayList:size	()I
      //   1404: istore_3
      //   1405: aload 9
      //   1407: astore_1
      //   1408: iload_3
      //   1409: ifle -678 -> 731
      //   1412: aload_0
      //   1413: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   1416: getfield 391	com/android/server/wm/WindowManagerService:mFinishedStarting	Ljava/util/ArrayList;
      //   1419: iload_3
      //   1420: iconst_1
      //   1421: isub
      //   1422: invokevirtual 394	java/util/ArrayList:remove	(I)Ljava/lang/Object;
      //   1425: checkcast 261	com/android/server/wm/AppWindowToken
      //   1428: astore_1
      //   1429: getstatic 268	com/android/server/wm/WindowManagerDebugConfig:DEBUG_STARTING_WINDOW	Z
      //   1432: ifeq +55 -> 1487
      //   1435: ldc -122
      //   1437: new 136	java/lang/StringBuilder
      //   1440: dup
      //   1441: invokespecial 137	java/lang/StringBuilder:<init>	()V
      //   1444: ldc_w 396
      //   1447: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1450: aload_1
      //   1451: invokevirtual 196	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   1454: ldc_w 381
      //   1457: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1460: aload_1
      //   1461: getfield 352	com/android/server/wm/AppWindowToken:startingWindow	Lcom/android/server/wm/WindowState;
      //   1464: invokevirtual 196	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   1467: ldc_w 383
      //   1470: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1473: aload_1
      //   1474: getfield 377	com/android/server/wm/AppWindowToken:startingView	Landroid/view/View;
      //   1477: invokevirtual 196	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   1480: invokevirtual 155	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   1483: invokestatic 161	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
      //   1486: pop
      //   1487: aload_1
      //   1488: getfield 352	com/android/server/wm/AppWindowToken:startingWindow	Lcom/android/server/wm/WindowState;
      //   1491: astore 10
      //   1493: aload 10
      //   1495: ifnonnull +9 -> 1504
      //   1498: aload 9
      //   1500: monitorexit
      //   1501: goto -119 -> 1382
      //   1504: aload_1
      //   1505: getfield 377	com/android/server/wm/AppWindowToken:startingView	Landroid/view/View;
      //   1508: astore 10
      //   1510: aload_1
      //   1511: getfield 317	com/android/server/wm/AppWindowToken:token	Landroid/os/IBinder;
      //   1514: astore 11
      //   1516: aload_1
      //   1517: aconst_null
      //   1518: putfield 265	com/android/server/wm/AppWindowToken:startingData	Lcom/android/server/wm/StartingData;
      //   1521: aload_1
      //   1522: aconst_null
      //   1523: putfield 377	com/android/server/wm/AppWindowToken:startingView	Landroid/view/View;
      //   1526: aload_1
      //   1527: aconst_null
      //   1528: putfield 352	com/android/server/wm/AppWindowToken:startingWindow	Lcom/android/server/wm/WindowState;
      //   1531: aload_1
      //   1532: iconst_0
      //   1533: putfield 388	com/android/server/wm/AppWindowToken:startingDisplayed	Z
      //   1536: aload 9
      //   1538: monitorexit
      //   1539: aload_0
      //   1540: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   1543: getfield 313	com/android/server/wm/WindowManagerService:mPolicy	Landroid/view/WindowManagerPolicy;
      //   1546: aload 11
      //   1548: aload 10
      //   1550: invokeinterface 365 3 0
      //   1555: goto -173 -> 1382
      //   1558: astore_1
      //   1559: ldc -122
      //   1561: ldc_w 367
      //   1564: aload_1
      //   1565: invokestatic 371	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   1568: pop
      //   1569: goto -187 -> 1382
      //   1572: astore_1
      //   1573: aload 9
      //   1575: monitorexit
      //   1576: aload_1
      //   1577: athrow
      //   1578: aload_1
      //   1579: getfield 259	android/os/Message:obj	Ljava/lang/Object;
      //   1582: checkcast 261	com/android/server/wm/AppWindowToken
      //   1585: astore_1
      //   1586: getstatic 399	com/android/server/wm/WindowManagerDebugConfig:DEBUG_VISIBILITY	Z
      //   1589: ifeq +29 -> 1618
      //   1592: ldc -122
      //   1594: new 136	java/lang/StringBuilder
      //   1597: dup
      //   1598: invokespecial 137	java/lang/StringBuilder:<init>	()V
      //   1601: ldc_w 401
      //   1604: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1607: aload_1
      //   1608: invokevirtual 196	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   1611: invokevirtual 155	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   1614: invokestatic 161	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
      //   1617: pop
      //   1618: aload_1
      //   1619: getfield 405	com/android/server/wm/AppWindowToken:appToken	Landroid/view/IApplicationToken;
      //   1622: invokeinterface 410 1 0
      //   1627: goto -1363 -> 264
      //   1630: astore_1
      //   1631: goto -1367 -> 264
      //   1634: aload_1
      //   1635: getfield 259	android/os/Message:obj	Ljava/lang/Object;
      //   1638: checkcast 261	com/android/server/wm/AppWindowToken
      //   1641: astore 9
      //   1643: aload_1
      //   1644: getfield 413	android/os/Message:arg1	I
      //   1647: ifeq +2520 -> 4167
      //   1650: iconst_1
      //   1651: istore 7
      //   1653: aload_1
      //   1654: getfield 416	android/os/Message:arg2	I
      //   1657: ifeq +2516 -> 4173
      //   1660: iconst_1
      //   1661: istore 8
      //   1663: getstatic 399	com/android/server/wm/WindowManagerDebugConfig:DEBUG_VISIBILITY	Z
      //   1666: ifeq +52 -> 1718
      //   1669: ldc -122
      //   1671: new 136	java/lang/StringBuilder
      //   1674: dup
      //   1675: invokespecial 137	java/lang/StringBuilder:<init>	()V
      //   1678: ldc_w 418
      //   1681: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1684: aload 9
      //   1686: invokevirtual 196	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   1689: ldc_w 420
      //   1692: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1695: iload 7
      //   1697: invokevirtual 359	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
      //   1700: ldc_w 422
      //   1703: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1706: iload 8
      //   1708: invokevirtual 359	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
      //   1711: invokevirtual 155	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   1714: invokestatic 161	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
      //   1717: pop
      //   1718: iload 7
      //   1720: ifeq +16 -> 1736
      //   1723: aload 9
      //   1725: getfield 405	com/android/server/wm/AppWindowToken:appToken	Landroid/view/IApplicationToken;
      //   1728: invokeinterface 425 1 0
      //   1733: goto -1469 -> 264
      //   1736: aload 9
      //   1738: getfield 405	com/android/server/wm/AppWindowToken:appToken	Landroid/view/IApplicationToken;
      //   1741: invokeinterface 428 1 0
      //   1746: goto -1482 -> 264
      //   1749: aload_0
      //   1750: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   1753: getfield 167	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
      //   1756: astore_1
      //   1757: aload_1
      //   1758: monitorenter
      //   1759: ldc -122
      //   1761: ldc_w 430
      //   1764: invokestatic 432	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   1767: pop
      //   1768: aload_0
      //   1769: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   1772: iconst_2
      //   1773: putfield 435	com/android/server/wm/WindowManagerService:mWindowsFreezingScreen	I
      //   1776: aload_0
      //   1777: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   1780: invokevirtual 439	com/android/server/wm/WindowManagerService:getDefaultWindowListLocked	()Lcom/android/server/wm/WindowList;
      //   1783: astore 9
      //   1785: aload 9
      //   1787: invokevirtual 442	com/android/server/wm/WindowList:size	()I
      //   1790: istore_3
      //   1791: iload_3
      //   1792: ifle +94 -> 1886
      //   1795: iload_3
      //   1796: iconst_1
      //   1797: isub
      //   1798: istore 4
      //   1800: aload 9
      //   1802: iload 4
      //   1804: invokevirtual 443	com/android/server/wm/WindowList:get	(I)Ljava/lang/Object;
      //   1807: checkcast 203	com/android/server/wm/WindowState
      //   1810: astore 10
      //   1812: iload 4
      //   1814: istore_3
      //   1815: aload 10
      //   1817: getfield 446	com/android/server/wm/WindowState:mOrientationChanging	Z
      //   1820: ifeq -29 -> 1791
      //   1823: aload 10
      //   1825: iconst_0
      //   1826: putfield 446	com/android/server/wm/WindowState:mOrientationChanging	Z
      //   1829: aload 10
      //   1831: invokestatic 449	android/os/SystemClock:elapsedRealtime	()J
      //   1834: aload_0
      //   1835: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   1838: getfield 453	com/android/server/wm/WindowManagerService:mDisplayFreezeTime	J
      //   1841: lsub
      //   1842: l2i
      //   1843: putfield 456	com/android/server/wm/WindowState:mLastFreezeDuration	I
      //   1846: ldc -122
      //   1848: new 136	java/lang/StringBuilder
      //   1851: dup
      //   1852: invokespecial 137	java/lang/StringBuilder:<init>	()V
      //   1855: ldc_w 458
      //   1858: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1861: aload 10
      //   1863: invokevirtual 196	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   1866: invokevirtual 155	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   1869: invokestatic 432	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   1872: pop
      //   1873: iload 4
      //   1875: istore_3
      //   1876: goto -85 -> 1791
      //   1879: astore 9
      //   1881: aload_1
      //   1882: monitorexit
      //   1883: aload 9
      //   1885: athrow
      //   1886: aload_0
      //   1887: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   1890: getfield 250	com/android/server/wm/WindowManagerService:mWindowPlacerLocked	Lcom/android/server/wm/WindowSurfacePlacer;
      //   1893: invokevirtual 255	com/android/server/wm/WindowSurfacePlacer:performSurfacePlacement	()V
      //   1896: goto -1165 -> 731
      //   1899: aload_0
      //   1900: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   1903: getfield 167	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
      //   1906: astore 9
      //   1908: aload 9
      //   1910: monitorenter
      //   1911: aload_0
      //   1912: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   1915: getfield 462	com/android/server/wm/WindowManagerService:mAppTransition	Lcom/android/server/wm/AppTransition;
      //   1918: invokevirtual 467	com/android/server/wm/AppTransition:isTransitionSet	()Z
      //   1921: ifne +32 -> 1953
      //   1924: aload_0
      //   1925: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   1928: getfield 471	com/android/server/wm/WindowManagerService:mOpeningApps	Landroid/util/ArraySet;
      //   1931: invokevirtual 476	android/util/ArraySet:isEmpty	()Z
      //   1934: ifeq +19 -> 1953
      //   1937: aload 9
      //   1939: astore_1
      //   1940: aload_0
      //   1941: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   1944: getfield 479	com/android/server/wm/WindowManagerService:mClosingApps	Landroid/util/ArraySet;
      //   1947: invokevirtual 476	android/util/ArraySet:isEmpty	()Z
      //   1950: ifne -1219 -> 731
      //   1953: getstatic 482	com/android/server/wm/WindowManagerDebugConfig:DEBUG_APP_TRANSITIONS	Z
      //   1956: ifeq +76 -> 2032
      //   1959: ldc -122
      //   1961: new 136	java/lang/StringBuilder
      //   1964: dup
      //   1965: invokespecial 137	java/lang/StringBuilder:<init>	()V
      //   1968: ldc_w 484
      //   1971: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1974: aload_0
      //   1975: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   1978: getfield 462	com/android/server/wm/WindowManagerService:mAppTransition	Lcom/android/server/wm/AppTransition;
      //   1981: invokevirtual 467	com/android/server/wm/AppTransition:isTransitionSet	()Z
      //   1984: invokevirtual 359	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
      //   1987: ldc_w 486
      //   1990: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1993: aload_0
      //   1994: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   1997: getfield 471	com/android/server/wm/WindowManagerService:mOpeningApps	Landroid/util/ArraySet;
      //   2000: invokevirtual 487	android/util/ArraySet:size	()I
      //   2003: invokevirtual 151	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   2006: ldc_w 489
      //   2009: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   2012: aload_0
      //   2013: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2016: getfield 479	com/android/server/wm/WindowManagerService:mClosingApps	Landroid/util/ArraySet;
      //   2019: invokevirtual 487	android/util/ArraySet:size	()I
      //   2022: invokevirtual 151	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   2025: invokevirtual 155	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   2028: invokestatic 161	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
      //   2031: pop
      //   2032: aload_0
      //   2033: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2036: getfield 471	com/android/server/wm/WindowManagerService:mOpeningApps	Landroid/util/ArraySet;
      //   2039: invokevirtual 487	android/util/ArraySet:size	()I
      //   2042: istore 4
      //   2044: iconst_0
      //   2045: istore_3
      //   2046: iload_3
      //   2047: iload 4
      //   2049: if_icmpge +39 -> 2088
      //   2052: aload_0
      //   2053: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2056: getfield 313	com/android/server/wm/WindowManagerService:mPolicy	Landroid/view/WindowManagerPolicy;
      //   2059: aload_0
      //   2060: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2063: getfield 471	com/android/server/wm/WindowManagerService:mOpeningApps	Landroid/util/ArraySet;
      //   2066: iload_3
      //   2067: invokevirtual 492	android/util/ArraySet:valueAt	(I)Ljava/lang/Object;
      //   2070: checkcast 261	com/android/server/wm/AppWindowToken
      //   2073: invokevirtual 493	com/android/server/wm/AppWindowToken:toString	()Ljava/lang/String;
      //   2076: invokeinterface 497 2 0
      //   2081: iload_3
      //   2082: iconst_1
      //   2083: iadd
      //   2084: istore_3
      //   2085: goto -39 -> 2046
      //   2088: aload_0
      //   2089: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2092: getfield 462	com/android/server/wm/WindowManagerService:mAppTransition	Lcom/android/server/wm/AppTransition;
      //   2095: invokevirtual 500	com/android/server/wm/AppTransition:setTimeout	()V
      //   2098: aload_0
      //   2099: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2102: getfield 250	com/android/server/wm/WindowManagerService:mWindowPlacerLocked	Lcom/android/server/wm/WindowSurfacePlacer;
      //   2105: invokevirtual 255	com/android/server/wm/WindowSurfacePlacer:performSurfacePlacement	()V
      //   2108: aload 9
      //   2110: astore_1
      //   2111: goto -1380 -> 731
      //   2114: astore_1
      //   2115: aload 9
      //   2117: monitorexit
      //   2118: aload_1
      //   2119: athrow
      //   2120: aload_0
      //   2121: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2124: getfield 504	com/android/server/wm/WindowManagerService:mContext	Landroid/content/Context;
      //   2127: invokevirtual 510	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
      //   2130: ldc_w 512
      //   2133: aload_0
      //   2134: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2137: getfield 516	com/android/server/wm/WindowManagerService:mWindowAnimationScaleSetting	F
      //   2140: invokestatic 522	android/provider/Settings$Global:putFloat	(Landroid/content/ContentResolver;Ljava/lang/String;F)Z
      //   2143: pop
      //   2144: aload_0
      //   2145: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2148: getfield 504	com/android/server/wm/WindowManagerService:mContext	Landroid/content/Context;
      //   2151: invokevirtual 510	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
      //   2154: ldc_w 524
      //   2157: aload_0
      //   2158: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2161: getfield 527	com/android/server/wm/WindowManagerService:mTransitionAnimationScaleSetting	F
      //   2164: invokestatic 522	android/provider/Settings$Global:putFloat	(Landroid/content/ContentResolver;Ljava/lang/String;F)Z
      //   2167: pop
      //   2168: aload_0
      //   2169: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2172: getfield 504	com/android/server/wm/WindowManagerService:mContext	Landroid/content/Context;
      //   2175: invokevirtual 510	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
      //   2178: ldc_w 529
      //   2181: aload_0
      //   2182: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2185: getfield 532	com/android/server/wm/WindowManagerService:mAnimatorDurationScaleSetting	F
      //   2188: invokestatic 522	android/provider/Settings$Global:putFloat	(Landroid/content/ContentResolver;Ljava/lang/String;F)Z
      //   2191: pop
      //   2192: goto -1928 -> 264
      //   2195: aload_1
      //   2196: getfield 413	android/os/Message:arg1	I
      //   2199: tableswitch	default:+25->2224, 0:+28->2227, 1:+61->2260, 2:+94->2293
      //   2224: goto -1960 -> 264
      //   2227: aload_0
      //   2228: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2231: aload_0
      //   2232: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2235: getfield 504	com/android/server/wm/WindowManagerService:mContext	Landroid/content/Context;
      //   2238: invokevirtual 510	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
      //   2241: ldc_w 512
      //   2244: aload_0
      //   2245: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2248: getfield 516	com/android/server/wm/WindowManagerService:mWindowAnimationScaleSetting	F
      //   2251: invokestatic 536	android/provider/Settings$Global:getFloat	(Landroid/content/ContentResolver;Ljava/lang/String;F)F
      //   2254: putfield 516	com/android/server/wm/WindowManagerService:mWindowAnimationScaleSetting	F
      //   2257: goto -1993 -> 264
      //   2260: aload_0
      //   2261: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2264: aload_0
      //   2265: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2268: getfield 504	com/android/server/wm/WindowManagerService:mContext	Landroid/content/Context;
      //   2271: invokevirtual 510	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
      //   2274: ldc_w 524
      //   2277: aload_0
      //   2278: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2281: getfield 527	com/android/server/wm/WindowManagerService:mTransitionAnimationScaleSetting	F
      //   2284: invokestatic 536	android/provider/Settings$Global:getFloat	(Landroid/content/ContentResolver;Ljava/lang/String;F)F
      //   2287: putfield 527	com/android/server/wm/WindowManagerService:mTransitionAnimationScaleSetting	F
      //   2290: goto -2026 -> 264
      //   2293: aload_0
      //   2294: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2297: aload_0
      //   2298: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2301: getfield 504	com/android/server/wm/WindowManagerService:mContext	Landroid/content/Context;
      //   2304: invokevirtual 510	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
      //   2307: ldc_w 529
      //   2310: aload_0
      //   2311: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2314: getfield 532	com/android/server/wm/WindowManagerService:mAnimatorDurationScaleSetting	F
      //   2317: invokestatic 536	android/provider/Settings$Global:getFloat	(Landroid/content/ContentResolver;Ljava/lang/String;F)F
      //   2320: putfield 532	com/android/server/wm/WindowManagerService:mAnimatorDurationScaleSetting	F
      //   2323: aload_0
      //   2324: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2327: aconst_null
      //   2328: invokevirtual 540	com/android/server/wm/WindowManagerService:dispatchNewAnimatorScaleLocked	(Lcom/android/server/wm/Session;)V
      //   2331: goto -2067 -> 264
      //   2334: aload_0
      //   2335: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2338: getfield 167	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
      //   2341: astore_1
      //   2342: aload_1
      //   2343: monitorenter
      //   2344: aload_0
      //   2345: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2348: getfield 544	com/android/server/wm/WindowManagerService:mAnimator	Lcom/android/server/wm/WindowAnimator;
      //   2351: invokevirtual 549	com/android/server/wm/WindowAnimator:isAnimating	()Z
      //   2354: ifne +13 -> 2367
      //   2357: aload_0
      //   2358: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2361: getfield 552	com/android/server/wm/WindowManagerService:mAnimationScheduled	Z
      //   2364: ifeq +16 -> 2380
      //   2367: aload_0
      //   2368: bipush 15
      //   2370: ldc2_w 553
      //   2373: invokevirtual 558	com/android/server/wm/WindowManagerService$H:sendEmptyMessageDelayed	(IJ)Z
      //   2376: pop
      //   2377: aload_1
      //   2378: monitorexit
      //   2379: return
      //   2380: aload_0
      //   2381: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2384: getfield 561	com/android/server/wm/WindowManagerService:mDisplayFrozen	Z
      //   2387: istore 7
      //   2389: iload 7
      //   2391: ifeq +6 -> 2397
      //   2394: aload_1
      //   2395: monitorexit
      //   2396: return
      //   2397: aload_1
      //   2398: monitorexit
      //   2399: invokestatic 567	java/lang/Runtime:getRuntime	()Ljava/lang/Runtime;
      //   2402: invokevirtual 570	java/lang/Runtime:gc	()V
      //   2405: goto -2141 -> 264
      //   2408: astore 9
      //   2410: aload_1
      //   2411: monitorexit
      //   2412: aload 9
      //   2414: athrow
      //   2415: aload_0
      //   2416: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2419: invokevirtual 573	com/android/server/wm/WindowManagerService:performEnableScreen	()V
      //   2422: goto -2158 -> 264
      //   2425: aload_0
      //   2426: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2429: getfield 167	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
      //   2432: astore 9
      //   2434: aload 9
      //   2436: monitorenter
      //   2437: ldc -122
      //   2439: ldc_w 575
      //   2442: invokestatic 432	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   2445: pop
      //   2446: aload_0
      //   2447: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2450: iconst_2
      //   2451: putfield 435	com/android/server/wm/WindowManagerService:mWindowsFreezingScreen	I
      //   2454: aload_0
      //   2455: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2458: getfield 579	com/android/server/wm/WindowManagerService:mStackIdToStack	Landroid/util/SparseArray;
      //   2461: invokevirtual 582	android/util/SparseArray:size	()I
      //   2464: istore 6
      //   2466: iconst_0
      //   2467: istore_3
      //   2468: aload 9
      //   2470: astore_1
      //   2471: iload_3
      //   2472: iload 6
      //   2474: if_icmpge -1743 -> 731
      //   2477: aload_0
      //   2478: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2481: getfield 579	com/android/server/wm/WindowManagerService:mStackIdToStack	Landroid/util/SparseArray;
      //   2484: iload_3
      //   2485: invokevirtual 583	android/util/SparseArray:valueAt	(I)Ljava/lang/Object;
      //   2488: checkcast 585	com/android/server/wm/TaskStack
      //   2491: invokevirtual 589	com/android/server/wm/TaskStack:getTasks	()Ljava/util/ArrayList;
      //   2494: astore_1
      //   2495: aload_1
      //   2496: invokevirtual 240	java/util/ArrayList:size	()I
      //   2499: iconst_1
      //   2500: isub
      //   2501: istore 4
      //   2503: iload 4
      //   2505: iflt +110 -> 2615
      //   2508: aload_1
      //   2509: iload 4
      //   2511: invokevirtual 246	java/util/ArrayList:get	(I)Ljava/lang/Object;
      //   2514: checkcast 305	com/android/server/wm/Task
      //   2517: getfield 593	com/android/server/wm/Task:mAppTokens	Lcom/android/server/wm/AppTokenList;
      //   2520: astore 10
      //   2522: aload 10
      //   2524: invokevirtual 596	com/android/server/wm/AppTokenList:size	()I
      //   2527: iconst_1
      //   2528: isub
      //   2529: istore 5
      //   2531: iload 5
      //   2533: iflt +73 -> 2606
      //   2536: aload 10
      //   2538: iload 5
      //   2540: invokevirtual 597	com/android/server/wm/AppTokenList:get	(I)Ljava/lang/Object;
      //   2543: checkcast 261	com/android/server/wm/AppWindowToken
      //   2546: astore 11
      //   2548: aload 11
      //   2550: getfield 601	com/android/server/wm/AppWindowToken:mAppAnimator	Lcom/android/server/wm/AppWindowAnimator;
      //   2553: getfield 606	com/android/server/wm/AppWindowAnimator:freezingScreen	Z
      //   2556: ifeq +41 -> 2597
      //   2559: ldc -122
      //   2561: new 136	java/lang/StringBuilder
      //   2564: dup
      //   2565: invokespecial 137	java/lang/StringBuilder:<init>	()V
      //   2568: ldc_w 608
      //   2571: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   2574: aload 11
      //   2576: invokevirtual 196	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   2579: invokevirtual 155	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   2582: invokestatic 432	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   2585: pop
      //   2586: aload_0
      //   2587: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2590: aload 11
      //   2592: iconst_1
      //   2593: iconst_1
      //   2594: invokevirtual 612	com/android/server/wm/WindowManagerService:unsetAppFreezingScreenLocked	(Lcom/android/server/wm/AppWindowToken;ZZ)V
      //   2597: iload 5
      //   2599: iconst_1
      //   2600: isub
      //   2601: istore 5
      //   2603: goto -72 -> 2531
      //   2606: iload 4
      //   2608: iconst_1
      //   2609: isub
      //   2610: istore 4
      //   2612: goto -109 -> 2503
      //   2615: iload_3
      //   2616: iconst_1
      //   2617: iadd
      //   2618: istore_3
      //   2619: goto -151 -> 2468
      //   2622: astore_1
      //   2623: aload 9
      //   2625: monitorexit
      //   2626: aload_1
      //   2627: athrow
      //   2628: aload_0
      //   2629: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2632: getfield 167	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
      //   2635: astore 9
      //   2637: aload 9
      //   2639: monitorenter
      //   2640: aload 9
      //   2642: astore_1
      //   2643: aload_0
      //   2644: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2647: getfield 615	com/android/server/wm/WindowManagerService:mClientFreezingScreen	Z
      //   2650: ifeq -1919 -> 731
      //   2653: aload_0
      //   2654: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2657: iconst_0
      //   2658: putfield 615	com/android/server/wm/WindowManagerService:mClientFreezingScreen	Z
      //   2661: aload_0
      //   2662: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2665: ldc_w 617
      //   2668: putfield 620	com/android/server/wm/WindowManagerService:mLastFinishedFreezeSource	Ljava/lang/Object;
      //   2671: aload_0
      //   2672: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2675: invokevirtual 623	com/android/server/wm/WindowManagerService:stopFreezingDisplayLocked	()V
      //   2678: aload 9
      //   2680: astore_1
      //   2681: goto -1950 -> 731
      //   2684: astore_1
      //   2685: aload 9
      //   2687: monitorexit
      //   2688: aload_1
      //   2689: athrow
      //   2690: aload_0
      //   2691: bipush 18
      //   2693: invokevirtual 627	com/android/server/wm/WindowManagerService$H:removeMessages	(I)V
      //   2696: aload_0
      //   2697: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2700: invokevirtual 630	com/android/server/wm/WindowManagerService:sendNewConfiguration	()V
      //   2703: goto -2439 -> 264
      //   2706: aload_0
      //   2707: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2710: getfield 633	com/android/server/wm/WindowManagerService:mWindowsChanged	Z
      //   2713: ifeq -2449 -> 264
      //   2716: aload_0
      //   2717: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2720: getfield 167	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
      //   2723: astore_1
      //   2724: aload_1
      //   2725: monitorenter
      //   2726: aload_0
      //   2727: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2730: iconst_0
      //   2731: putfield 633	com/android/server/wm/WindowManagerService:mWindowsChanged	Z
      //   2734: aload_1
      //   2735: monitorexit
      //   2736: aload_0
      //   2737: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2740: invokestatic 636	com/android/server/wm/WindowManagerService:-wrap7	(Lcom/android/server/wm/WindowManagerService;)V
      //   2743: goto -2479 -> 264
      //   2746: astore 9
      //   2748: aload_1
      //   2749: monitorexit
      //   2750: aload 9
      //   2752: athrow
      //   2753: aload_1
      //   2754: getfield 259	android/os/Message:obj	Ljava/lang/Object;
      //   2757: checkcast 638	android/os/IBinder
      //   2760: astore_1
      //   2761: getstatic 641	com/android/server/wm/WindowManagerDebugConfig:DEBUG_DRAG	Z
      //   2764: ifeq +29 -> 2793
      //   2767: ldc -122
      //   2769: new 136	java/lang/StringBuilder
      //   2772: dup
      //   2773: invokespecial 137	java/lang/StringBuilder:<init>	()V
      //   2776: ldc_w 643
      //   2779: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   2782: aload_1
      //   2783: invokevirtual 196	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   2786: invokevirtual 155	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   2789: invokestatic 432	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   2792: pop
      //   2793: aload_0
      //   2794: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2797: getfield 167	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
      //   2800: astore 9
      //   2802: aload 9
      //   2804: monitorenter
      //   2805: aload 9
      //   2807: astore_1
      //   2808: aload_0
      //   2809: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2812: getfield 647	com/android/server/wm/WindowManagerService:mDragState	Lcom/android/server/wm/DragState;
      //   2815: ifnull -2084 -> 731
      //   2818: aload_0
      //   2819: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2822: getfield 647	com/android/server/wm/WindowManagerService:mDragState	Lcom/android/server/wm/DragState;
      //   2825: invokevirtual 652	com/android/server/wm/DragState:unregister	()V
      //   2828: aload_0
      //   2829: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2832: getfield 656	com/android/server/wm/WindowManagerService:mInputMonitor	Lcom/android/server/wm/InputMonitor;
      //   2835: iconst_1
      //   2836: invokevirtual 662	com/android/server/wm/InputMonitor:updateInputWindowsLw	(Z)V
      //   2839: aload_0
      //   2840: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2843: getfield 647	com/android/server/wm/WindowManagerService:mDragState	Lcom/android/server/wm/DragState;
      //   2846: invokevirtual 665	com/android/server/wm/DragState:reset	()V
      //   2849: aload_0
      //   2850: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2853: aconst_null
      //   2854: putfield 647	com/android/server/wm/WindowManagerService:mDragState	Lcom/android/server/wm/DragState;
      //   2857: aload 9
      //   2859: astore_1
      //   2860: goto -2129 -> 731
      //   2863: astore_1
      //   2864: aload 9
      //   2866: monitorexit
      //   2867: aload_1
      //   2868: athrow
      //   2869: aload_1
      //   2870: getfield 259	android/os/Message:obj	Ljava/lang/Object;
      //   2873: checkcast 638	android/os/IBinder
      //   2876: astore_1
      //   2877: getstatic 641	com/android/server/wm/WindowManagerDebugConfig:DEBUG_DRAG	Z
      //   2880: ifeq +29 -> 2909
      //   2883: ldc -122
      //   2885: new 136	java/lang/StringBuilder
      //   2888: dup
      //   2889: invokespecial 137	java/lang/StringBuilder:<init>	()V
      //   2892: ldc_w 667
      //   2895: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   2898: aload_1
      //   2899: invokevirtual 196	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   2902: invokevirtual 155	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   2905: invokestatic 432	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   2908: pop
      //   2909: aload_0
      //   2910: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2913: getfield 167	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
      //   2916: astore 9
      //   2918: aload 9
      //   2920: monitorenter
      //   2921: aload 9
      //   2923: astore_1
      //   2924: aload_0
      //   2925: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2928: getfield 647	com/android/server/wm/WindowManagerService:mDragState	Lcom/android/server/wm/DragState;
      //   2931: ifnull -2200 -> 731
      //   2934: aload_0
      //   2935: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2938: getfield 647	com/android/server/wm/WindowManagerService:mDragState	Lcom/android/server/wm/DragState;
      //   2941: iconst_0
      //   2942: putfield 670	com/android/server/wm/DragState:mDragResult	Z
      //   2945: aload_0
      //   2946: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2949: getfield 647	com/android/server/wm/WindowManagerService:mDragState	Lcom/android/server/wm/DragState;
      //   2952: invokevirtual 673	com/android/server/wm/DragState:endDragLw	()V
      //   2955: aload 9
      //   2957: astore_1
      //   2958: goto -2227 -> 731
      //   2961: astore_1
      //   2962: aload 9
      //   2964: monitorexit
      //   2965: aload_1
      //   2966: athrow
      //   2967: aload_0
      //   2968: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2971: invokevirtual 676	com/android/server/wm/WindowManagerService:notifyHardKeyboardStatusChange	()V
      //   2974: goto -2710 -> 264
      //   2977: aload_0
      //   2978: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2981: invokevirtual 679	com/android/server/wm/WindowManagerService:performBootTimeout	()V
      //   2984: goto -2720 -> 264
      //   2987: aload_0
      //   2988: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   2991: getfield 167	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
      //   2994: astore_1
      //   2995: aload_1
      //   2996: monitorenter
      //   2997: ldc -122
      //   2999: new 136	java/lang/StringBuilder
      //   3002: dup
      //   3003: invokespecial 137	java/lang/StringBuilder:<init>	()V
      //   3006: ldc_w 681
      //   3009: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   3012: aload_0
      //   3013: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3016: getfield 684	com/android/server/wm/WindowManagerService:mWaitingForDrawn	Ljava/util/ArrayList;
      //   3019: invokevirtual 196	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   3022: invokevirtual 155	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   3025: invokestatic 432	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   3028: pop
      //   3029: aload_0
      //   3030: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3033: getfield 684	com/android/server/wm/WindowManagerService:mWaitingForDrawn	Ljava/util/ArrayList;
      //   3036: invokevirtual 687	java/util/ArrayList:clear	()V
      //   3039: aload_0
      //   3040: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3043: getfield 691	com/android/server/wm/WindowManagerService:mWaitingForDrawnCallback	Ljava/lang/Runnable;
      //   3046: astore 9
      //   3048: aload_0
      //   3049: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3052: aconst_null
      //   3053: putfield 691	com/android/server/wm/WindowManagerService:mWaitingForDrawnCallback	Ljava/lang/Runnable;
      //   3056: aload_1
      //   3057: monitorexit
      //   3058: aload 9
      //   3060: ifnull -2796 -> 264
      //   3063: aload 9
      //   3065: invokeinterface 696 1 0
      //   3070: goto -2806 -> 264
      //   3073: astore 9
      //   3075: aload_1
      //   3076: monitorexit
      //   3077: aload 9
      //   3079: athrow
      //   3080: aload_0
      //   3081: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3084: aload_1
      //   3085: getfield 413	android/os/Message:arg1	I
      //   3088: aload_1
      //   3089: getfield 416	android/os/Message:arg2	I
      //   3092: invokestatic 700	com/android/server/wm/WindowManagerService:-wrap8	(Lcom/android/server/wm/WindowManagerService;II)V
      //   3095: goto -2831 -> 264
      //   3098: aload_0
      //   3099: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3102: astore 9
      //   3104: aload_1
      //   3105: getfield 413	android/os/Message:arg1	I
      //   3108: iconst_1
      //   3109: if_icmpne +16 -> 3125
      //   3112: iconst_1
      //   3113: istore 7
      //   3115: aload 9
      //   3117: iload 7
      //   3119: invokevirtual 703	com/android/server/wm/WindowManagerService:showCircularMask	(Z)V
      //   3122: goto -2858 -> 264
      //   3125: iconst_0
      //   3126: istore 7
      //   3128: goto -13 -> 3115
      //   3131: aload_0
      //   3132: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3135: invokevirtual 706	com/android/server/wm/WindowManagerService:showEmulatorDisplayOverlay	()V
      //   3138: goto -2874 -> 264
      //   3141: aload_1
      //   3142: getfield 259	android/os/Message:obj	Ljava/lang/Object;
      //   3145: checkcast 708	android/os/IRemoteCallback
      //   3148: aconst_null
      //   3149: invokeinterface 712 2 0
      //   3154: goto -2890 -> 264
      //   3157: astore_1
      //   3158: goto -2894 -> 264
      //   3161: aload_0
      //   3162: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3165: aload_1
      //   3166: getfield 413	android/os/Message:arg1	I
      //   3169: invokevirtual 715	com/android/server/wm/WindowManagerService:handleDisplayAdded	(I)V
      //   3172: goto -2908 -> 264
      //   3175: aload_0
      //   3176: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3179: getfield 167	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
      //   3182: astore 9
      //   3184: aload 9
      //   3186: monitorenter
      //   3187: aload_0
      //   3188: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3191: aload_1
      //   3192: getfield 413	android/os/Message:arg1	I
      //   3195: invokestatic 719	com/android/server/wm/WindowManagerService:-wrap4	(Lcom/android/server/wm/WindowManagerService;I)V
      //   3198: aload 9
      //   3200: astore_1
      //   3201: goto -2470 -> 731
      //   3204: astore_1
      //   3205: aload 9
      //   3207: monitorexit
      //   3208: aload_1
      //   3209: athrow
      //   3210: aload_0
      //   3211: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3214: getfield 167	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
      //   3217: astore 9
      //   3219: aload 9
      //   3221: monitorenter
      //   3222: aload_0
      //   3223: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3226: aload_1
      //   3227: getfield 413	android/os/Message:arg1	I
      //   3230: invokestatic 722	com/android/server/wm/WindowManagerService:-wrap3	(Lcom/android/server/wm/WindowManagerService;I)V
      //   3233: aload 9
      //   3235: astore_1
      //   3236: goto -2505 -> 731
      //   3239: astore_1
      //   3240: aload 9
      //   3242: monitorexit
      //   3243: aload_1
      //   3244: athrow
      //   3245: aload_0
      //   3246: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3249: aload_1
      //   3250: getfield 259	android/os/Message:obj	Ljava/lang/Object;
      //   3253: checkcast 177	com/android/server/wm/DisplayContent
      //   3256: aload_1
      //   3257: getfield 413	android/os/Message:arg1	I
      //   3260: aload_1
      //   3261: getfield 416	android/os/Message:arg2	I
      //   3264: invokestatic 726	com/android/server/wm/WindowManagerService:-wrap9	(Lcom/android/server/wm/WindowManagerService;Lcom/android/server/wm/DisplayContent;II)V
      //   3267: goto -3003 -> 264
      //   3270: aload_0
      //   3271: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3274: aload_1
      //   3275: getfield 259	android/os/Message:obj	Ljava/lang/Object;
      //   3278: checkcast 177	com/android/server/wm/DisplayContent
      //   3281: aload_1
      //   3282: getfield 413	android/os/Message:arg1	I
      //   3285: aload_1
      //   3286: getfield 416	android/os/Message:arg2	I
      //   3289: invokestatic 729	com/android/server/wm/WindowManagerService:-wrap5	(Lcom/android/server/wm/WindowManagerService;Lcom/android/server/wm/DisplayContent;II)V
      //   3292: goto -3028 -> 264
      //   3295: aload_0
      //   3296: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3299: invokestatic 732	com/android/server/wm/WindowManagerService:-wrap2	(Lcom/android/server/wm/WindowManagerService;)V
      //   3302: goto -3038 -> 264
      //   3305: aload_0
      //   3306: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3309: getfield 736	com/android/server/wm/WindowManagerService:mActivityManager	Landroid/app/IActivityManager;
      //   3312: aload_1
      //   3313: getfield 259	android/os/Message:obj	Ljava/lang/Object;
      //   3316: checkcast 638	android/os/IBinder
      //   3319: invokeinterface 742 2 0
      //   3324: goto -3060 -> 264
      //   3327: astore_1
      //   3328: goto -3064 -> 264
      //   3331: aload_0
      //   3332: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3335: getfield 167	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
      //   3338: astore 9
      //   3340: aload 9
      //   3342: monitorenter
      //   3343: aload_0
      //   3344: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3347: getfield 691	com/android/server/wm/WindowManagerService:mWaitingForDrawnCallback	Ljava/lang/Runnable;
      //   3350: astore 10
      //   3352: aload_0
      //   3353: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3356: aconst_null
      //   3357: putfield 691	com/android/server/wm/WindowManagerService:mWaitingForDrawnCallback	Ljava/lang/Runnable;
      //   3360: aload 9
      //   3362: monitorexit
      //   3363: aload 10
      //   3365: ifnull +10 -> 3375
      //   3368: aload 10
      //   3370: invokeinterface 696 1 0
      //   3375: aload_0
      //   3376: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3379: invokevirtual 746	com/android/server/wm/WindowManagerService:getCurrentAnimatorScale	()F
      //   3382: fstore_2
      //   3383: fload_2
      //   3384: invokestatic 752	android/animation/ValueAnimator:setDurationScale	(F)V
      //   3387: aload_1
      //   3388: getfield 259	android/os/Message:obj	Ljava/lang/Object;
      //   3391: checkcast 754	com/android/server/wm/Session
      //   3394: astore_1
      //   3395: invokestatic 759	com/android/server/am/OnePlusProcessManager:isSupportFrozenApp	()Z
      //   3398: ifeq +17 -> 3415
      //   3401: aload_1
      //   3402: ifnull +13 -> 3415
      //   3405: aload_1
      //   3406: getfield 762	com/android/server/wm/Session:mUid	I
      //   3409: ldc_w 764
      //   3412: invokestatic 768	com/android/server/am/OnePlusProcessManager:resumeProcessByUID_out	(ILjava/lang/String;)V
      //   3415: aload_1
      //   3416: ifnull +26 -> 3442
      //   3419: aload_1
      //   3420: getfield 772	com/android/server/wm/Session:mCallback	Landroid/view/IWindowSessionCallback;
      //   3423: fload_2
      //   3424: invokeinterface 777 2 0
      //   3429: goto -3165 -> 264
      //   3432: astore_1
      //   3433: goto -3169 -> 264
      //   3436: astore_1
      //   3437: aload 9
      //   3439: monitorexit
      //   3440: aload_1
      //   3441: athrow
      //   3442: new 232	java/util/ArrayList
      //   3445: dup
      //   3446: invokespecial 237	java/util/ArrayList:<init>	()V
      //   3449: astore_1
      //   3450: aload_0
      //   3451: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3454: getfield 167	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
      //   3457: astore 9
      //   3459: aload 9
      //   3461: monitorenter
      //   3462: iconst_0
      //   3463: istore_3
      //   3464: iload_3
      //   3465: aload_0
      //   3466: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3469: getfield 780	com/android/server/wm/WindowManagerService:mSessions	Landroid/util/ArraySet;
      //   3472: invokevirtual 487	android/util/ArraySet:size	()I
      //   3475: if_icmpge +32 -> 3507
      //   3478: aload_1
      //   3479: aload_0
      //   3480: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3483: getfield 780	com/android/server/wm/WindowManagerService:mSessions	Landroid/util/ArraySet;
      //   3486: iload_3
      //   3487: invokevirtual 492	android/util/ArraySet:valueAt	(I)Ljava/lang/Object;
      //   3490: checkcast 754	com/android/server/wm/Session
      //   3493: getfield 772	com/android/server/wm/Session:mCallback	Landroid/view/IWindowSessionCallback;
      //   3496: invokevirtual 236	java/util/ArrayList:add	(Ljava/lang/Object;)Z
      //   3499: pop
      //   3500: iload_3
      //   3501: iconst_1
      //   3502: iadd
      //   3503: istore_3
      //   3504: goto -40 -> 3464
      //   3507: aload 9
      //   3509: monitorexit
      //   3510: iconst_0
      //   3511: istore_3
      //   3512: iload_3
      //   3513: aload_1
      //   3514: invokevirtual 240	java/util/ArrayList:size	()I
      //   3517: if_icmpge -3253 -> 264
      //   3520: aload_1
      //   3521: iload_3
      //   3522: invokevirtual 246	java/util/ArrayList:get	(I)Ljava/lang/Object;
      //   3525: checkcast 774	android/view/IWindowSessionCallback
      //   3528: fload_2
      //   3529: invokeinterface 777 2 0
      //   3534: iload_3
      //   3535: iconst_1
      //   3536: iadd
      //   3537: istore_3
      //   3538: goto -26 -> 3512
      //   3541: astore_1
      //   3542: aload 9
      //   3544: monitorexit
      //   3545: aload_1
      //   3546: athrow
      //   3547: aload_0
      //   3548: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3551: getfield 167	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
      //   3554: astore_1
      //   3555: aload_1
      //   3556: monitorenter
      //   3557: getstatic 783	com/android/server/wm/WindowManagerDebugConfig:DEBUG_BOOT	Z
      //   3560: ifeq +12 -> 3572
      //   3563: ldc -122
      //   3565: ldc_w 785
      //   3568: invokestatic 201	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
      //   3571: pop
      //   3572: aload_0
      //   3573: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3576: invokestatic 789	com/android/server/wm/WindowManagerService:-wrap0	(Lcom/android/server/wm/WindowManagerService;)Z
      //   3579: istore 7
      //   3581: aload_1
      //   3582: monitorexit
      //   3583: iload 7
      //   3585: ifeq -3321 -> 264
      //   3588: aload_0
      //   3589: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3592: invokevirtual 573	com/android/server/wm/WindowManagerService:performEnableScreen	()V
      //   3595: goto -3331 -> 264
      //   3598: astore 9
      //   3600: aload_1
      //   3601: monitorexit
      //   3602: aload 9
      //   3604: athrow
      //   3605: aload_0
      //   3606: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3609: getfield 167	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
      //   3612: astore_1
      //   3613: aload_1
      //   3614: monitorenter
      //   3615: aload_0
      //   3616: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3619: aconst_null
      //   3620: putfield 792	com/android/server/wm/WindowManagerService:mLastANRState	Ljava/lang/String;
      //   3623: goto -2892 -> 731
      //   3626: astore 9
      //   3628: aload_1
      //   3629: monitorexit
      //   3630: aload 9
      //   3632: athrow
      //   3633: aload_0
      //   3634: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3637: getfield 167	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
      //   3640: astore_1
      //   3641: aload_1
      //   3642: monitorenter
      //   3643: aload_0
      //   3644: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3647: getfield 796	com/android/server/wm/WindowManagerService:mWallpaperControllerLocked	Lcom/android/server/wm/WallpaperController;
      //   3650: invokevirtual 801	com/android/server/wm/WallpaperController:processWallpaperDrawPendingTimeout	()Z
      //   3653: ifeq +13 -> 3666
      //   3656: aload_0
      //   3657: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3660: getfield 250	com/android/server/wm/WindowManagerService:mWindowPlacerLocked	Lcom/android/server/wm/WindowSurfacePlacer;
      //   3663: invokevirtual 255	com/android/server/wm/WindowSurfacePlacer:performSurfacePlacement	()V
      //   3666: aload_1
      //   3667: monitorexit
      //   3668: aload_0
      //   3669: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3672: getfield 167	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
      //   3675: astore_1
      //   3676: aload_1
      //   3677: monitorenter
      //   3678: aload_0
      //   3679: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3682: invokevirtual 175	com/android/server/wm/WindowManagerService:getDefaultDisplayContentLocked	()Lcom/android/server/wm/DisplayContent;
      //   3685: astore 9
      //   3687: aload 9
      //   3689: invokevirtual 805	com/android/server/wm/DisplayContent:getDockedDividerController	()Lcom/android/server/wm/DockedStackDividerController;
      //   3692: iconst_0
      //   3693: invokevirtual 810	com/android/server/wm/DockedStackDividerController:reevaluateVisibility	(Z)V
      //   3696: aload_0
      //   3697: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3700: aload 9
      //   3702: invokevirtual 814	com/android/server/wm/WindowManagerService:adjustForImeIfNeeded	(Lcom/android/server/wm/DisplayContent;)V
      //   3705: goto -2974 -> 731
      //   3708: astore 9
      //   3710: aload_1
      //   3711: monitorexit
      //   3712: aload 9
      //   3714: athrow
      //   3715: astore 9
      //   3717: aload_1
      //   3718: monitorexit
      //   3719: aload 9
      //   3721: athrow
      //   3722: aload_0
      //   3723: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3726: getfield 736	com/android/server/wm/WindowManagerService:mActivityManager	Landroid/app/IActivityManager;
      //   3729: aload_1
      //   3730: getfield 413	android/os/Message:arg1	I
      //   3733: aload_1
      //   3734: getfield 259	android/os/Message:obj	Ljava/lang/Object;
      //   3737: checkcast 816	android/graphics/Rect
      //   3740: aload_1
      //   3741: getfield 416	android/os/Message:arg2	I
      //   3744: invokeinterface 820 4 0
      //   3749: goto -3485 -> 264
      //   3752: astore_1
      //   3753: goto -3489 -> 264
      //   3756: aload_0
      //   3757: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3760: getfield 736	com/android/server/wm/WindowManagerService:mActivityManager	Landroid/app/IActivityManager;
      //   3763: astore 9
      //   3765: aload_1
      //   3766: getfield 413	android/os/Message:arg1	I
      //   3769: istore_3
      //   3770: aload_1
      //   3771: getfield 259	android/os/Message:obj	Ljava/lang/Object;
      //   3774: checkcast 816	android/graphics/Rect
      //   3777: astore 10
      //   3779: aload_1
      //   3780: getfield 416	android/os/Message:arg2	I
      //   3783: iconst_1
      //   3784: if_icmpne +28 -> 3812
      //   3787: iconst_1
      //   3788: istore 7
      //   3790: aload 9
      //   3792: iload_3
      //   3793: aload 10
      //   3795: iload 7
      //   3797: iconst_0
      //   3798: iconst_0
      //   3799: iconst_m1
      //   3800: invokeinterface 824 7 0
      //   3805: goto -3541 -> 264
      //   3808: astore_1
      //   3809: goto -3545 -> 264
      //   3812: iconst_0
      //   3813: istore 7
      //   3815: goto -25 -> 3790
      //   3818: aload_0
      //   3819: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3822: getfield 167	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
      //   3825: astore 9
      //   3827: aload 9
      //   3829: monitorenter
      //   3830: aload_0
      //   3831: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3834: getfield 827	com/android/server/wm/WindowManagerService:mReplacingWindowTimeouts	Ljava/util/ArrayList;
      //   3837: invokevirtual 240	java/util/ArrayList:size	()I
      //   3840: iconst_1
      //   3841: isub
      //   3842: istore_3
      //   3843: iload_3
      //   3844: iflt +27 -> 3871
      //   3847: aload_0
      //   3848: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3851: getfield 827	com/android/server/wm/WindowManagerService:mReplacingWindowTimeouts	Ljava/util/ArrayList;
      //   3854: iload_3
      //   3855: invokevirtual 246	java/util/ArrayList:get	(I)Ljava/lang/Object;
      //   3858: checkcast 261	com/android/server/wm/AppWindowToken
      //   3861: invokevirtual 830	com/android/server/wm/AppWindowToken:clearTimedoutReplacesLocked	()V
      //   3864: iload_3
      //   3865: iconst_1
      //   3866: isub
      //   3867: istore_3
      //   3868: goto -25 -> 3843
      //   3871: aload_0
      //   3872: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3875: getfield 827	com/android/server/wm/WindowManagerService:mReplacingWindowTimeouts	Ljava/util/ArrayList;
      //   3878: invokevirtual 687	java/util/ArrayList:clear	()V
      //   3881: aload 9
      //   3883: monitorexit
      //   3884: aload_0
      //   3885: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3888: getfield 834	com/android/server/wm/WindowManagerService:mAmInternal	Landroid/app/ActivityManagerInternal;
      //   3891: aload_1
      //   3892: getfield 413	android/os/Message:arg1	I
      //   3895: invokevirtual 839	android/app/ActivityManagerInternal:notifyAppTransitionStarting	(I)V
      //   3898: goto -3634 -> 264
      //   3901: astore_1
      //   3902: aload 9
      //   3904: monitorexit
      //   3905: aload_1
      //   3906: athrow
      //   3907: aload_0
      //   3908: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3911: getfield 834	com/android/server/wm/WindowManagerService:mAmInternal	Landroid/app/ActivityManagerInternal;
      //   3914: invokevirtual 842	android/app/ActivityManagerInternal:notifyAppTransitionCancelled	()V
      //   3917: goto -3653 -> 264
      //   3920: aload_0
      //   3921: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3924: getfield 834	com/android/server/wm/WindowManagerService:mAmInternal	Landroid/app/ActivityManagerInternal;
      //   3927: invokevirtual 845	android/app/ActivityManagerInternal:notifyAppTransitionFinished	()V
      //   3930: goto -3666 -> 264
      //   3933: aload_0
      //   3934: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3937: getfield 834	com/android/server/wm/WindowManagerService:mAmInternal	Landroid/app/ActivityManagerInternal;
      //   3940: invokevirtual 848	android/app/ActivityManagerInternal:notifyStartingWindowDrawn	()V
      //   3943: goto -3679 -> 264
      //   3946: aload_1
      //   3947: getfield 259	android/os/Message:obj	Ljava/lang/Object;
      //   3950: checkcast 203	com/android/server/wm/WindowState
      //   3953: astore 9
      //   3955: aload_0
      //   3956: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   3959: getfield 167	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
      //   3962: astore_1
      //   3963: aload_1
      //   3964: monitorenter
      //   3965: aload 9
      //   3967: getfield 852	com/android/server/wm/WindowState:mAttrs	Landroid/view/WindowManager$LayoutParams;
      //   3970: astore 10
      //   3972: aload 10
      //   3974: aload 10
      //   3976: getfield 857	android/view/WindowManager$LayoutParams:flags	I
      //   3979: sipush 65407
      //   3982: iand
      //   3983: putfield 857	android/view/WindowManager$LayoutParams:flags	I
      //   3986: aload 9
      //   3988: invokevirtual 860	com/android/server/wm/WindowState:hidePermanentlyLw	()V
      //   3991: aload 9
      //   3993: invokevirtual 863	com/android/server/wm/WindowState:setDisplayLayoutNeeded	()V
      //   3996: aload_0
      //   3997: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   4000: getfield 250	com/android/server/wm/WindowManagerService:mWindowPlacerLocked	Lcom/android/server/wm/WindowSurfacePlacer;
      //   4003: invokevirtual 255	com/android/server/wm/WindowSurfacePlacer:performSurfacePlacement	()V
      //   4006: goto -3275 -> 731
      //   4009: astore 9
      //   4011: aload_1
      //   4012: monitorexit
      //   4013: aload 9
      //   4015: athrow
      //   4016: aload_0
      //   4017: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   4020: getfield 834	com/android/server/wm/WindowManagerService:mAmInternal	Landroid/app/ActivityManagerInternal;
      //   4023: astore 9
      //   4025: aload_1
      //   4026: getfield 413	android/os/Message:arg1	I
      //   4029: iconst_1
      //   4030: if_icmpne +16 -> 4046
      //   4033: iconst_1
      //   4034: istore 7
      //   4036: aload 9
      //   4038: iload 7
      //   4040: invokevirtual 866	android/app/ActivityManagerInternal:notifyDockedStackMinimizedChanged	(Z)V
      //   4043: goto -3779 -> 264
      //   4046: iconst_0
      //   4047: istore 7
      //   4049: goto -13 -> 4036
      //   4052: aload_0
      //   4053: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   4056: getfield 167	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
      //   4059: astore 9
      //   4061: aload 9
      //   4063: monitorenter
      //   4064: aload_0
      //   4065: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   4068: invokevirtual 175	com/android/server/wm/WindowManagerService:getDefaultDisplayContentLocked	()Lcom/android/server/wm/DisplayContent;
      //   4071: invokevirtual 869	com/android/server/wm/DisplayContent:getWindowList	()Lcom/android/server/wm/WindowList;
      //   4074: astore_1
      //   4075: iconst_0
      //   4076: istore 4
      //   4078: aload_1
      //   4079: invokevirtual 442	com/android/server/wm/WindowList:size	()I
      //   4082: iconst_1
      //   4083: isub
      //   4084: istore_3
      //   4085: iload_3
      //   4086: iflt +42 -> 4128
      //   4089: aload_1
      //   4090: iload_3
      //   4091: invokevirtual 443	com/android/server/wm/WindowList:get	(I)Ljava/lang/Object;
      //   4094: checkcast 203	com/android/server/wm/WindowState
      //   4097: astore 10
      //   4099: aload 10
      //   4101: getfield 872	com/android/server/wm/WindowState:mSeamlesslyRotated	Z
      //   4104: ifeq +75 -> 4179
      //   4107: iconst_1
      //   4108: istore 4
      //   4110: aload 10
      //   4112: invokevirtual 863	com/android/server/wm/WindowState:setDisplayLayoutNeeded	()V
      //   4115: aload_0
      //   4116: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   4119: aload 10
      //   4121: iconst_0
      //   4122: invokevirtual 876	com/android/server/wm/WindowManagerService:markForSeamlessRotation	(Lcom/android/server/wm/WindowState;Z)V
      //   4125: goto +54 -> 4179
      //   4128: aload 9
      //   4130: astore_1
      //   4131: iload 4
      //   4133: ifeq -3402 -> 731
      //   4136: aload_0
      //   4137: getfield 116	com/android/server/wm/WindowManagerService$H:this$0	Lcom/android/server/wm/WindowManagerService;
      //   4140: getfield 250	com/android/server/wm/WindowManagerService:mWindowPlacerLocked	Lcom/android/server/wm/WindowSurfacePlacer;
      //   4143: invokevirtual 255	com/android/server/wm/WindowSurfacePlacer:performSurfacePlacement	()V
      //   4146: aload 9
      //   4148: astore_1
      //   4149: goto -3418 -> 731
      //   4152: astore_1
      //   4153: aload 9
      //   4155: monitorexit
      //   4156: aload_1
      //   4157: athrow
      //   4158: astore 9
      //   4160: goto -626 -> 3534
      //   4163: astore_1
      //   4164: goto -3900 -> 264
      //   4167: iconst_0
      //   4168: istore 7
      //   4170: goto -2517 -> 1653
      //   4173: iconst_0
      //   4174: istore 8
      //   4176: goto -2513 -> 1663
      //   4179: iload_3
      //   4180: iconst_1
      //   4181: isub
      //   4182: istore_3
      //   4183: goto -98 -> 4085
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	4186	0	this	H
      //   3382	147	2	f	float
      //   634	3549	3	i	int
      //   631	3501	4	j	int
      //   2529	73	5	k	int
      //   2464	11	6	m	int
      //   434	3735	7	bool1	boolean
      //   1661	2514	8	bool2	boolean
      //   280	398	9	localObject1	Object
      //   704	5	9	localObject2	Object
      //   736	5	9	localObject3	Object
      //   853	948	9	localObject4	Object
      //   1879	5	9	localObject5	Object
      //   1906	210	9	localHashMap1	HashMap
      //   2408	5	9	localObject6	Object
      //   2432	254	9	localHashMap2	HashMap
      //   2746	5	9	localObject7	Object
      //   2800	264	9	localObject8	Object
      //   3073	5	9	localObject9	Object
      //   3102	441	9	localObject10	Object
      //   3598	5	9	localObject11	Object
      //   3626	5	9	localObject12	Object
      //   3685	16	9	localDisplayContent	DisplayContent
      //   3708	5	9	localObject13	Object
      //   3715	5	9	localObject14	Object
      //   4009	5	9	localObject16	Object
      //   4158	1	9	localRemoteException	RemoteException
      //   338	3782	10	localObject18	Object
      //   289	2302	11	localObject19	Object
      //   347	147	12	localWindowState	WindowState
      // Exception table:
      //   from	to	target	type
      //   297	307	585	finally
      //   310	331	585	finally
      //   331	349	585	finally
      //   360	411	585	finally
      //   429	436	585	finally
      //   566	579	585	finally
      //   601	624	704	finally
      //   721	731	736	finally
      //   1074	1092	1095	java/lang/Exception
      //   860	877	1114	java/lang/Exception
      //   877	936	1114	java/lang/Exception
      //   954	970	1140	finally
      //   970	1039	1140	finally
      //   1039	1051	1140	finally
      //   1053	1058	1140	finally
      //   1131	1137	1140	finally
      //   1146	1201	1140	finally
      //   1344	1359	1362	java/lang/Exception
      //   1230	1291	1376	finally
      //   1291	1336	1376	finally
      //   1539	1555	1558	java/lang/Exception
      //   1394	1405	1572	finally
      //   1412	1487	1572	finally
      //   1487	1493	1572	finally
      //   1504	1536	1572	finally
      //   1586	1618	1630	android/os/RemoteException
      //   1618	1627	1630	android/os/RemoteException
      //   1759	1791	1879	finally
      //   1800	1812	1879	finally
      //   1815	1873	1879	finally
      //   1886	1896	1879	finally
      //   1911	1937	2114	finally
      //   1940	1953	2114	finally
      //   1953	2032	2114	finally
      //   2032	2044	2114	finally
      //   2052	2081	2114	finally
      //   2088	2108	2114	finally
      //   2344	2367	2408	finally
      //   2367	2377	2408	finally
      //   2380	2389	2408	finally
      //   2437	2466	2622	finally
      //   2477	2503	2622	finally
      //   2508	2531	2622	finally
      //   2536	2597	2622	finally
      //   2643	2678	2684	finally
      //   2726	2734	2746	finally
      //   2808	2857	2863	finally
      //   2924	2955	2961	finally
      //   2997	3056	3073	finally
      //   3141	3154	3157	android/os/RemoteException
      //   3187	3198	3204	finally
      //   3222	3233	3239	finally
      //   3305	3324	3327	android/os/RemoteException
      //   3419	3429	3432	android/os/RemoteException
      //   3343	3360	3436	finally
      //   3464	3500	3541	finally
      //   3557	3572	3598	finally
      //   3572	3581	3598	finally
      //   3615	3623	3626	finally
      //   3678	3705	3708	finally
      //   3643	3666	3715	finally
      //   3722	3749	3752	android/os/RemoteException
      //   3756	3787	3808	android/os/RemoteException
      //   3790	3805	3808	android/os/RemoteException
      //   3830	3843	3901	finally
      //   3847	3864	3901	finally
      //   3871	3881	3901	finally
      //   3965	4006	4009	finally
      //   4064	4075	4152	finally
      //   4078	4085	4152	finally
      //   4089	4099	4152	finally
      //   4099	4107	4152	finally
      //   4110	4125	4152	finally
      //   4136	4146	4152	finally
      //   3520	3534	4158	android/os/RemoteException
      //   1663	1718	4163	android/os/RemoteException
      //   1723	1733	4163	android/os/RemoteException
      //   1736	1746	4163	android/os/RemoteException
    }
  }
  
  private static final class HideNavInputConsumer
    extends InputConsumerImpl
    implements WindowManagerPolicy.InputConsumer
  {
    private final InputEventReceiver mInputEventReceiver;
    
    HideNavInputConsumer(WindowManagerService paramWindowManagerService, Looper paramLooper, InputEventReceiver.Factory paramFactory)
    {
      super("input consumer", null);
      this.mInputEventReceiver = paramFactory.createInputEventReceiver(this.mClientChannel, paramLooper);
    }
    
    public void dismiss()
    {
      if (this.mService.removeInputConsumer()) {}
      synchronized (this.mService.mWindowMap)
      {
        this.mInputEventReceiver.dispose();
        disposeChannelsLw();
        return;
      }
    }
  }
  
  private final class LocalService
    extends WindowManagerInternal
  {
    private LocalService() {}
    
    public void addWindowToken(IBinder paramIBinder, int paramInt)
    {
      WindowManagerService.this.addWindowToken(paramIBinder, paramInt);
    }
    
    public void clearLastInputMethodWindowForTransition()
    {
      synchronized (WindowManagerService.this.mWindowMap)
      {
        WindowManagerService.this.mPolicy.setLastInputMethodWindowLw(null, null);
        return;
      }
    }
    
    public MagnificationSpec getCompatibleMagnificationSpecForWindow(IBinder paramIBinder)
    {
      synchronized (WindowManagerService.this.mWindowMap)
      {
        WindowState localWindowState = (WindowState)WindowManagerService.this.mWindowMap.get(paramIBinder);
        if (localWindowState == null) {
          return null;
        }
        paramIBinder = null;
        if (WindowManagerService.this.mAccessibilityController != null) {
          paramIBinder = WindowManagerService.this.mAccessibilityController.getMagnificationSpecForWindowLocked(localWindowState);
        }
        if ((paramIBinder == null) || (paramIBinder.isNop()))
        {
          float f = localWindowState.mGlobalScale;
          if (f == 1.0F) {
            return null;
          }
        }
        if (paramIBinder == null)
        {
          paramIBinder = MagnificationSpec.obtain();
          paramIBinder.scale *= localWindowState.mGlobalScale;
          return paramIBinder;
        }
        paramIBinder = MagnificationSpec.obtain(paramIBinder);
      }
    }
    
    public IBinder getFocusedWindowToken()
    {
      synchronized (WindowManagerService.this.mWindowMap)
      {
        Object localObject1 = WindowManagerService.-wrap1(WindowManagerService.this);
        if (localObject1 != null)
        {
          localObject1 = ((WindowState)localObject1).mClient.asBinder();
          return (IBinder)localObject1;
        }
        return null;
      }
    }
    
    public int getInputMethodWindowVisibleHeight()
    {
      synchronized (WindowManagerService.this.mWindowMap)
      {
        int i = WindowManagerService.this.mPolicy.getInputMethodWindowVisibleHeightLw();
        return i;
      }
    }
    
    public void getMagnificationRegion(Region paramRegion)
    {
      synchronized (WindowManagerService.this.mWindowMap)
      {
        if (WindowManagerService.this.mAccessibilityController != null)
        {
          WindowManagerService.this.mAccessibilityController.getMagnificationRegionLocked(paramRegion);
          return;
        }
        throw new IllegalStateException("Magnification callbacks not set!");
      }
    }
    
    public void getWindowFrame(IBinder paramIBinder, Rect paramRect)
    {
      synchronized (WindowManagerService.this.mWindowMap)
      {
        paramIBinder = (WindowState)WindowManagerService.this.mWindowMap.get(paramIBinder);
        if (paramIBinder != null)
        {
          paramRect.set(paramIBinder.mFrame);
          return;
        }
        paramRect.setEmpty();
      }
    }
    
    public boolean isAnimating()
    {
      synchronized (WindowManagerService.this.mWindowMap)
      {
        if ((!WindowManagerService.this.mAnimator.isAnimating()) && (!WindowManagerService.this.mAppTransition.isRunning()))
        {
          bool = WindowManagerService.this.mAnimationScheduled;
          return bool;
        }
        boolean bool = true;
      }
    }
    
    public boolean isDockedDividerResizing()
    {
      synchronized (WindowManagerService.this.mWindowMap)
      {
        boolean bool = WindowManagerService.this.getDefaultDisplayContentLocked().getDockedDividerController().isResizing();
        return bool;
      }
    }
    
    public boolean isHardKeyboardAvailable()
    {
      synchronized (WindowManagerService.this.mWindowMap)
      {
        boolean bool = WindowManagerService.this.mHardKeyboardAvailable;
        return bool;
      }
    }
    
    public boolean isKeyguardLocked()
    {
      return WindowManagerService.this.isKeyguardLocked();
    }
    
    public boolean isMinimizedDock()
    {
      synchronized (WindowManagerService.this.mWindowMap)
      {
        boolean bool = WindowManagerService.this.getDefaultDisplayContentLocked().getDockedDividerController().isMinimizedDock();
        return bool;
      }
    }
    
    public boolean isStackVisible(int paramInt)
    {
      synchronized (WindowManagerService.this.mWindowMap)
      {
        boolean bool = WindowManagerService.this.isStackVisibleLocked(paramInt);
        return bool;
      }
    }
    
    public void killInputMethodProc(String paramString)
    {
      for (;;)
      {
        int i;
        int j;
        synchronized (WindowManagerService.this.mWindowMap)
        {
          int k = WindowManagerService.this.mDisplayContents.size();
          i = 0;
          if (i < k)
          {
            WindowList localWindowList = ((DisplayContent)WindowManagerService.this.mDisplayContents.valueAt(i)).getWindowList();
            j = localWindowList.size() - 1;
            if (j >= 0)
            {
              WindowState localWindowState = (WindowState)localWindowList.get(j);
              if (localWindowState.mAttrs.type == 2011)
              {
                i = localWindowState.mSession.mPid;
                Slog.d(WindowManagerService.-get0(), "kill InputMethod reason=" + paramString + " pkg=" + localWindowState.getOwningPackage() + " pid=" + i);
              }
            }
          }
        }
        try
        {
          WindowManagerService.this.mActivityManager.killPids(new int[] { i }, paramString, true);
          return;
          j -= 1;
          continue;
          i += 1;
          continue;
          return;
          paramString = finally;
          throw paramString;
        }
        catch (RemoteException paramString)
        {
          for (;;) {}
        }
      }
    }
    
    public void registerAppTransitionListener(WindowManagerInternal.AppTransitionListener paramAppTransitionListener)
    {
      synchronized (WindowManagerService.this.mWindowMap)
      {
        WindowManagerService.this.mAppTransition.registerListenerLocked(paramAppTransitionListener);
        return;
      }
    }
    
    public void removeWindowToken(IBinder paramIBinder, boolean paramBoolean)
    {
      HashMap localHashMap = WindowManagerService.this.mWindowMap;
      if (paramBoolean) {}
      try
      {
        WindowToken localWindowToken = (WindowToken)WindowManagerService.this.mTokenMap.remove(paramIBinder);
        if (localWindowToken != null) {
          localWindowToken.removeAllWindows();
        }
        WindowManagerService.this.removeWindowToken(paramIBinder);
        return;
      }
      finally {}
    }
    
    public void requestTraversalFromDisplayManager()
    {
      WindowManagerService.this.requestTraversal();
    }
    
    public void saveLastInputMethodWindowForTransition()
    {
      synchronized (WindowManagerService.this.mWindowMap)
      {
        if (WindowManagerService.this.mInputMethodWindow != null) {
          WindowManagerService.this.mPolicy.setLastInputMethodWindowLw(WindowManagerService.this.mInputMethodWindow, WindowManagerService.this.mInputMethodTarget);
        }
        return;
      }
    }
    
    public void setInputFilter(IInputFilter paramIInputFilter)
    {
      WindowManagerService.this.mInputManager.setInputFilter(paramIInputFilter);
    }
    
    public void setMagnificationCallbacks(WindowManagerInternal.MagnificationCallbacks paramMagnificationCallbacks)
    {
      synchronized (WindowManagerService.this.mWindowMap)
      {
        if (WindowManagerService.this.mAccessibilityController == null) {
          WindowManagerService.this.mAccessibilityController = new AccessibilityController(WindowManagerService.this);
        }
        WindowManagerService.this.mAccessibilityController.setMagnificationCallbacksLocked(paramMagnificationCallbacks);
        if (!WindowManagerService.this.mAccessibilityController.hasCallbacksLocked()) {
          WindowManagerService.this.mAccessibilityController = null;
        }
        return;
      }
    }
    
    public void setMagnificationSpec(MagnificationSpec paramMagnificationSpec)
    {
      synchronized (WindowManagerService.this.mWindowMap)
      {
        if (WindowManagerService.this.mAccessibilityController != null)
        {
          WindowManagerService.this.mAccessibilityController.setMagnificationSpecLocked(paramMagnificationSpec);
          if (Binder.getCallingPid() != Process.myPid()) {
            paramMagnificationSpec.recycle();
          }
          return;
        }
        throw new IllegalStateException("Magnification callbacks not set!");
      }
    }
    
    public void setOnHardKeyboardStatusChangeListener(WindowManagerInternal.OnHardKeyboardStatusChangeListener paramOnHardKeyboardStatusChangeListener)
    {
      synchronized (WindowManagerService.this.mWindowMap)
      {
        WindowManagerService.this.mHardKeyboardStatusChangeListener = paramOnHardKeyboardStatusChangeListener;
        return;
      }
    }
    
    public void setWindowsForAccessibilityCallback(WindowManagerInternal.WindowsForAccessibilityCallback paramWindowsForAccessibilityCallback)
    {
      synchronized (WindowManagerService.this.mWindowMap)
      {
        if (WindowManagerService.this.mAccessibilityController == null) {
          WindowManagerService.this.mAccessibilityController = new AccessibilityController(WindowManagerService.this);
        }
        WindowManagerService.this.mAccessibilityController.setWindowsForAccessibilityCallback(paramWindowsForAccessibilityCallback);
        if (!WindowManagerService.this.mAccessibilityController.hasCallbacksLocked()) {
          WindowManagerService.this.mAccessibilityController = null;
        }
        return;
      }
    }
    
    public void showGlobalActions()
    {
      WindowManagerService.this.showGlobalActions();
    }
    
    public void waitForAllWindowsDrawn(Runnable paramRunnable, long paramLong)
    {
      int j = 0;
      synchronized (WindowManagerService.this.mWindowMap)
      {
        WindowManagerService.this.mWaitingForDrawnCallback = paramRunnable;
        WindowList localWindowList = WindowManagerService.this.getDefaultWindowListLocked();
        int i = localWindowList.size() - 1;
        for (;;)
        {
          boolean bool1;
          if (i >= 0)
          {
            WindowState localWindowState = (WindowState)localWindowList.get(i);
            bool1 = WindowManagerService.this.mPolicy.isForceHiding(localWindowState.mAttrs);
            boolean bool2 = WindowManagerService.this.mPolicy.isKeyguardHostWindow(localWindowState.mAttrs);
            if ((localWindowState.isVisibleLw()) && ((localWindowState.mAppToken != null) || (bool1) || (bool2)))
            {
              localWindowState.mWinAnimator.mDrawState = 1;
              localWindowState.mLastContentInsets.set(-1, -1, -1, -1);
              if (WindowManagerDebugConfig.DEBUG_ONEPLUS) {
                Slog.i(WindowManagerService.-get0(), "Waiting for " + localWindowState + " drawn.");
              }
              WindowManagerService.this.mWaitingForDrawn.add(localWindowState);
              if (!bool1) {}
            }
          }
          else
          {
            WindowManagerService.this.mWindowPlacerLocked.requestTraversal();
            WindowManagerService.this.mH.removeMessages(24);
            bool1 = WindowManagerService.this.mWaitingForDrawn.isEmpty();
            if (!bool1) {
              break;
            }
            i = 1;
            if (i != 0) {
              paramRunnable.run();
            }
            return;
          }
          i -= 1;
        }
        WindowManagerService.this.mH.sendEmptyMessageDelayed(24, paramLong);
        WindowManagerService.this.checkDrawnWindowsLocked();
        i = j;
      }
    }
  }
  
  private static class MousePositionTracker
    implements WindowManagerPolicy.PointerEventListener
  {
    private boolean mLatestEventWasMouse;
    private float mLatestMouseX;
    private float mLatestMouseY;
    
    public void onPointerEvent(MotionEvent paramMotionEvent)
    {
      if (paramMotionEvent.isFromSource(8194))
      {
        updatePosition(paramMotionEvent.getRawX(), paramMotionEvent.getRawY());
        return;
      }
      try
      {
        this.mLatestEventWasMouse = false;
        return;
      }
      finally
      {
        paramMotionEvent = finally;
        throw paramMotionEvent;
      }
    }
    
    void updatePosition(float paramFloat1, float paramFloat2)
    {
      try
      {
        this.mLatestEventWasMouse = true;
        this.mLatestMouseX = paramFloat1;
        this.mLatestMouseY = paramFloat2;
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
  }
  
  class RotationWatcher
  {
    IBinder.DeathRecipient deathRecipient;
    IRotationWatcher watcher;
    
    RotationWatcher(IRotationWatcher paramIRotationWatcher, IBinder.DeathRecipient paramDeathRecipient)
    {
      this.watcher = paramIRotationWatcher;
      this.deathRecipient = paramDeathRecipient;
    }
  }
  
  private final class SettingsObserver
    extends ContentObserver
  {
    private final Uri mAnimationDurationScaleUri = Settings.Global.getUriFor("animator_duration_scale");
    private final Uri mDisplayInversionEnabledUri = Settings.Secure.getUriFor("accessibility_display_inversion_enabled");
    private final Uri mTransitionAnimationScaleUri = Settings.Global.getUriFor("transition_animation_scale");
    private final Uri mWindowAnimationScaleUri = Settings.Global.getUriFor("window_animation_scale");
    
    public SettingsObserver()
    {
      super();
      this$1 = WindowManagerService.this.mContext.getContentResolver();
      WindowManagerService.this.registerContentObserver(this.mDisplayInversionEnabledUri, false, this, -1);
      WindowManagerService.this.registerContentObserver(this.mWindowAnimationScaleUri, false, this, -1);
      WindowManagerService.this.registerContentObserver(this.mTransitionAnimationScaleUri, false, this, -1);
      WindowManagerService.this.registerContentObserver(this.mAnimationDurationScaleUri, false, this, -1);
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      if (paramUri == null) {
        return;
      }
      if (this.mDisplayInversionEnabledUri.equals(paramUri))
      {
        WindowManagerService.-wrap10(WindowManagerService.this);
        return;
      }
      int i;
      if (this.mWindowAnimationScaleUri.equals(paramUri)) {
        i = 0;
      }
      for (;;)
      {
        paramUri = WindowManagerService.this.mH.obtainMessage(51, i, 0);
        WindowManagerService.this.mH.sendMessage(paramUri);
        return;
        if (this.mTransitionAnimationScaleUri.equals(paramUri))
        {
          i = 1;
        }
        else
        {
          if (!this.mAnimationDurationScaleUri.equals(paramUri)) {
            break;
          }
          i = 2;
        }
      }
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @IntDef({0L, 1L, 2L})
  private static @interface UpdateAnimationScaleMode {}
  
  public static abstract interface WindowChangeListener
  {
    public abstract void focusChanged();
    
    public abstract void windowsChanged();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/WindowManagerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */