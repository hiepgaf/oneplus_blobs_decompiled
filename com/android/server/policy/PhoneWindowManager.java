package com.android.server.policy;

import android.app.ActivityManager;
import android.app.ActivityManager.StackId;
import android.app.ActivityManagerInternal;
import android.app.ActivityManagerInternal.SleepToken;
import android.app.ActivityManagerNative;
import android.app.AppOpsManager;
import android.app.IActivityManager;
import android.app.IUiModeManager;
import android.app.IUiModeManager.Stub;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.UiModeManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.ThemeController;
import android.content.res.TypedArray;
import android.database.ContentObserver;
import android.graphics.Rect;
import android.hardware.hdmi.HdmiControlManager;
import android.hardware.hdmi.HdmiPlaybackClient;
import android.hardware.hdmi.HdmiPlaybackClient.OneTouchPlayCallback;
import android.hardware.input.InputManagerInternal;
import android.media.AudioAttributes;
import android.media.AudioAttributes.Builder;
import android.media.AudioManager;
import android.media.AudioSystem;
import android.media.IAudioService;
import android.media.IAudioService.Stub;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.session.MediaSessionLegacyHelper;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.FactoryTest;
import android.os.Handler;
import android.os.IBinder;
import android.os.IDeviceIdleController;
import android.os.IDeviceIdleController.Stub;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.PowerManagerInternal;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UEventObserver;
import android.os.UEventObserver.UEvent;
import android.os.UserHandle;
import android.os.Vibrator;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.service.dreams.DreamManagerInternal;
import android.service.dreams.IDreamManager;
import android.service.dreams.IDreamManager.Stub;
import android.telecom.TelecomManager;
import android.util.EventLog;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.MutableBoolean;
import android.util.Slog;
import android.util.SparseArray;
import android.view.Display;
import android.view.IApplicationToken;
import android.view.IWindowManager;
import android.view.InputChannel;
import android.view.InputEventReceiver;
import android.view.InputEventReceiver.Factory;
import android.view.KeyCharacterMap;
import android.view.KeyCharacterMap.FallbackAction;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.BadTokenException;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManagerInternal;
import android.view.WindowManagerPolicy;
import android.view.WindowManagerPolicy.InputConsumer;
import android.view.WindowManagerPolicy.OnKeyguardExitResult;
import android.view.WindowManagerPolicy.ScreenOnListener;
import android.view.WindowManagerPolicy.WindowManagerFuncs;
import android.view.WindowManagerPolicy.WindowState;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import com.android.internal.R.styleable;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.policy.IShortcutService;
import com.android.internal.policy.PhoneWindow;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.statusbar.IStatusBarService.Stub;
import com.android.internal.util.ScreenShapeHelper;
import com.android.internal.widget.PointerLocationView;
import com.android.server.GestureLauncherService;
import com.android.server.LocalServices;
import com.android.server.policy.keyguard.KeyguardServiceDelegate;
import com.android.server.policy.keyguard.KeyguardServiceDelegate.DrawnListener;
import com.android.server.policy.keyguard.KeyguardStateMonitor.OnShowingStateChangedCallback;
import com.android.server.statusbar.StatusBarManagerInternal;
import com.oneplus.longshot.ILongScreenshotManager;
import com.oneplus.longshot.ILongScreenshotManager.Stub;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class PhoneWindowManager
  implements WindowManagerPolicy
{
  private static final String ACTION_WIFI_DISPLAY_VIDEO = "org.codeaurora.intent.action.WIFI_DISPLAY_VIDEO";
  static final boolean ALTERNATE_CAR_MODE_NAV_SIZE = false;
  static final int APPLICATION_ABOVE_SUB_PANEL_SUBLAYER = 3;
  static final int APPLICATION_MEDIA_OVERLAY_SUBLAYER = -1;
  static final int APPLICATION_MEDIA_SUBLAYER = -2;
  static final int APPLICATION_PANEL_SUBLAYER = 1;
  static final int APPLICATION_SUB_PANEL_SUBLAYER = 2;
  private static final int BRIGHTNESS_STEPS = 10;
  static boolean DEBUG = false;
  static boolean DEBUG_INPUT = false;
  static boolean DEBUG_KEYGUARD = false;
  static boolean DEBUG_LAYOUT = false;
  public static boolean DEBUG_ONEPLUS = Build.DEBUG_ONEPLUS;
  static boolean DEBUG_STARTING_WINDOW = false;
  static boolean DEBUG_WAKEUP = false;
  private static final int DISMISS_KEYGUARD_CONTINUE = 2;
  private static final int DISMISS_KEYGUARD_NONE = 0;
  private static final int DISMISS_KEYGUARD_START = 1;
  static final int DOUBLE_TAP_HOME_NOTHING = 0;
  static final int DOUBLE_TAP_HOME_RECENT_SYSTEM_UI = 1;
  static final boolean ENABLE_DESK_DOCK_HOME_CAPTURE = false;
  private static final int KEYGUARD_FINGERPRINT_AUTHENTICATE_TIMEOUT_DURATION = 1000;
  protected static final float KEYGUARD_SCREENSHOT_CHORD_DELAY_MULTIPLIER = 1.5625F;
  static final int LAST_LONG_PRESS_HOME_BEHAVIOR = 2;
  static final int LONG_PRESS_BACK_GO_TO_VOICE_ASSIST = 1;
  static final int LONG_PRESS_BACK_NOTHING = 0;
  static final int LONG_PRESS_HOME_ASSIST = 2;
  static final int LONG_PRESS_HOME_NOTHING = 0;
  static final int LONG_PRESS_HOME_RECENT_SYSTEM_UI = 1;
  static final int LONG_PRESS_POWER_GLOBAL_ACTIONS = 1;
  static final int LONG_PRESS_POWER_NOTHING = 0;
  static final int LONG_PRESS_POWER_SHUT_OFF = 2;
  static final int LONG_PRESS_POWER_SHUT_OFF_NO_CONFIRM = 3;
  private static final int MSG_BACK_LONG_PRESS = 18;
  private static final int MSG_DISABLE_POINTER_LOCATION = 2;
  private static final int MSG_DISPATCH_MEDIA_KEY_REPEAT_WITH_WAKE_LOCK = 4;
  private static final int MSG_DISPATCH_MEDIA_KEY_WITH_WAKE_LOCK = 3;
  private static final int MSG_DISPATCH_SHOW_GLOBAL_ACTIONS = 10;
  private static final int MSG_DISPATCH_SHOW_RECENTS = 9;
  private static final int MSG_DISPOSE_INPUT_CONSUMER = 19;
  private static final int MSG_ENABLE_POINTER_LOCATION = 1;
  private static final int MSG_HIDE_BOOT_MESSAGE = 11;
  private static final int MSG_KEYGUARD_DRAWN_COMPLETE = 5;
  private static final int MSG_KEYGUARD_DRAWN_TIMEOUT = 6;
  private static final int MSG_KEYGUARD_FP_AUTHENTICATED_TIMEOUT = 102;
  private static final int MSG_KEYGUARD_FP_STATE_CHANGE = 101;
  private static final int MSG_LAUNCH_VOICE_ASSIST_WITH_WAKE_LOCK = 12;
  private static final int MSG_POWER_DELAYED_PRESS = 13;
  private static final int MSG_POWER_LONG_PRESS = 14;
  private static final int MSG_REQUEST_TRANSIENT_BARS = 16;
  private static final int MSG_REQUEST_TRANSIENT_BARS_ARG_NAVIGATION = 1;
  private static final int MSG_REQUEST_TRANSIENT_BARS_ARG_STATUS = 0;
  private static final int MSG_SHOW_TV_PICTURE_IN_PICTURE_MENU = 17;
  private static final int MSG_UPDATE_DREAMING_SLEEP_TOKEN = 15;
  private static final int MSG_WINDOW_MANAGER_DRAWN_COMPLETE = 7;
  static final int MULTI_PRESS_POWER_BRIGHTNESS_BOOST = 2;
  static final int MULTI_PRESS_POWER_NOTHING = 0;
  static final int MULTI_PRESS_POWER_THEATER_MODE = 1;
  private static final int NAV_BAR_BOTTOM = 0;
  private static final int NAV_BAR_LEFT = 2;
  static final int NAV_BAR_OPAQUE_WHEN_FREEFORM_OR_DOCKED = 0;
  private static final int NAV_BAR_RIGHT = 1;
  static final int NAV_BAR_TRANSLUCENT_WHEN_FREEFORM_OPAQUE_OTHERWISE = 1;
  private static final List<String> OEM_PACKAGE_LIST;
  private static final long PANIC_GESTURE_EXPIRATION = 30000L;
  static final int PENDING_KEY_NULL = -1;
  private static final float POWER_LONGPRESS_CHORD_DELAY_MULTIPLIER = 2.5F;
  static final boolean PRINT_ANIM = false;
  protected static final long SCREENSHOT_CHORD_DEBOUNCE_DELAY_MILLIS = 150L;
  private static final float SCREENSHOT_DELAY_MULTIPLIER = 0.375F;
  static final int SHORT_PRESS_POWER_GO_HOME = 4;
  static final int SHORT_PRESS_POWER_GO_TO_SLEEP = 1;
  static final int SHORT_PRESS_POWER_NOTHING = 0;
  static final int SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP = 2;
  static final int SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP_AND_GO_HOME = 3;
  static final int SHORT_PRESS_SLEEP_GO_TO_SLEEP = 0;
  static final int SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME = 1;
  static final int SHORT_PRESS_WINDOW_NOTHING = 0;
  static final int SHORT_PRESS_WINDOW_PICTURE_IN_PICTURE = 1;
  static boolean SHOW_STARTING_ANIMATIONS = false;
  public static final String SYSTEM_DIALOG_REASON_ASSIST = "assist";
  public static final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
  public static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
  public static final String SYSTEM_DIALOG_REASON_KEY = "reason";
  public static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
  static final int SYSTEM_UI_CHANGING_LAYOUT = -1073709042;
  private static final String SYSUI_PACKAGE = "com.android.systemui";
  private static final String SYSUI_SCREENSHOT_ERROR_RECEIVER = "com.android.systemui.screenshot.ScreenshotServiceErrorReceiver";
  private static final String SYSUI_SCREENSHOT_SERVICE = "com.android.systemui.screenshot.TakeScreenshotService";
  static final String TAG = "WindowManager";
  public static final int TOAST_WINDOW_TIMEOUT = 3500;
  private static final AudioAttributes VIBRATION_ATTRIBUTES;
  static final int WAITING_FOR_DRAWN_TIMEOUT = 1000;
  protected static final int[] WINDOW_TYPES_WHERE_HOME_DOESNT_WORK = { 2003, 2010 };
  static boolean localLOGV = false;
  static final Rect mTmpContentFrame;
  static final Rect mTmpDecorFrame;
  static final Rect mTmpDisplayFrame;
  static final Rect mTmpNavigationFrame;
  static final Rect mTmpOutsetFrame;
  static final Rect mTmpOverscanFrame;
  static final Rect mTmpParentFrame;
  private static final Rect mTmpRect;
  static final Rect mTmpStableFrame;
  static final Rect mTmpVisibleFrame;
  static SparseArray<String> sApplicationLaunchKeyCategories;
  boolean mAccelerometerDefault;
  AccessibilityManager mAccessibilityManager;
  ActivityManagerInternal mActivityManagerInternal;
  int mAllowAllRotations = -1;
  boolean mAllowLockscreenWhenOn;
  private boolean mAllowTheaterModeWakeFromCameraLens;
  private boolean mAllowTheaterModeWakeFromKey;
  private boolean mAllowTheaterModeWakeFromLidSwitch;
  private boolean mAllowTheaterModeWakeFromMotion;
  private boolean mAllowTheaterModeWakeFromMotionWhenNotDreaming;
  private boolean mAllowTheaterModeWakeFromPowerKey;
  private boolean mAllowTheaterModeWakeFromWakeGesture;
  AppOpsManager mAppOpsManager;
  HashSet<IApplicationToken> mAppsThatDismissKeyguard = new HashSet();
  HashSet<IApplicationToken> mAppsToBeHidden = new HashSet();
  boolean mAssistKeyLongPressed;
  boolean mAwake;
  volatile boolean mBackKeyHandled;
  volatile boolean mBeganFromNonInteractive;
  boolean mBootMessageNeedsHiding;
  ProgressDialog mBootMsgDialog = null;
  PowerManager.WakeLock mBroadcastWakeLock;
  BurnInProtectionHelper mBurnInProtectionHelper;
  long[] mCalendarDateVibePattern;
  volatile boolean mCameraGestureTriggeredDuringGoingToSleep;
  int mCameraLensCoverState = -1;
  boolean mCarDockEnablesAccelerometer;
  Intent mCarDockIntent;
  int mCarDockRotation;
  private final Runnable mClearHideNavigationFlag = new Runnable()
  {
    public void run()
    {
      synchronized (PhoneWindowManager.this.mWindowManagerFuncs.getWindowManagerLock())
      {
        PhoneWindowManager localPhoneWindowManager = PhoneWindowManager.this;
        localPhoneWindowManager.mForceClearedSystemUiFlags &= 0xFFFFFFFD;
        PhoneWindowManager.this.mWindowManagerFuncs.reevaluateStatusBarVisibility();
        return;
      }
    }
  };
  long[] mClockTickVibePattern;
  boolean mConsumeSearchKeyUp;
  int mContentBottom;
  int mContentLeft;
  int mContentRight;
  int mContentTop;
  Context mContext;
  long[] mContextClickVibePattern;
  int mCurBottom;
  int mCurLeft;
  int mCurRight;
  int mCurTop;
  int mCurrentAppOrientation = -1;
  protected int mCurrentUserId;
  @GuardedBy("Lw")
  private boolean mCurrentlyDismissingKeyguard;
  private boolean mDeferBindKeyguard;
  int mDemoHdmiRotation;
  boolean mDemoHdmiRotationLock;
  int mDemoRotation;
  boolean mDemoRotationLock;
  boolean mDeskDockEnablesAccelerometer;
  Intent mDeskDockIntent;
  int mDeskDockRotation;
  int mDismissKeyguard = 0;
  Display mDisplay;
  int mDisplayRotation;
  int mDockBottom;
  int mDockLayer;
  int mDockLeft;
  int mDockMode = 0;
  BroadcastReceiver mDockReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context arg1, Intent paramAnonymousIntent)
    {
      if ("android.intent.action.DOCK_EVENT".equals(paramAnonymousIntent.getAction())) {
        PhoneWindowManager.this.mDockMode = paramAnonymousIntent.getIntExtra("android.intent.extra.DOCK_STATE", 0);
      }
      for (;;)
      {
        PhoneWindowManager.this.updateRotation(true);
        synchronized (PhoneWindowManager.-get2(PhoneWindowManager.this))
        {
          PhoneWindowManager.this.updateOrientationListenerLp();
          return;
          try
          {
            ??? = IUiModeManager.Stub.asInterface(ServiceManager.getService("uimode"));
            PhoneWindowManager.this.mUiMode = ???.getCurrentModeType();
          }
          catch (RemoteException ???) {}
        }
      }
    }
  };
  int mDockRight;
  int mDockTop;
  final Rect mDockedStackBounds = new Rect();
  int mDoublePressOnPowerBehavior;
  private int mDoubleTapOnHomeBehavior;
  DreamManagerInternal mDreamManagerInternal;
  BroadcastReceiver mDreamReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("android.intent.action.DREAMING_STARTED".equals(paramAnonymousIntent.getAction())) {
        if (PhoneWindowManager.this.mKeyguardDelegate != null) {
          PhoneWindowManager.this.mKeyguardDelegate.onDreamingStarted();
        }
      }
      while ((!"android.intent.action.DREAMING_STOPPED".equals(paramAnonymousIntent.getAction())) || (PhoneWindowManager.this.mKeyguardDelegate == null)) {
        return;
      }
      PhoneWindowManager.this.mKeyguardDelegate.onDreamingStopped();
    }
  };
  boolean mDreamingLockscreen;
  ActivityManagerInternal.SleepToken mDreamingSleepToken;
  boolean mDreamingSleepTokenNeeded;
  private boolean mEnableCarDockHomeCapture = true;
  boolean mEnableShiftMenuBugReports = false;
  volatile boolean mEndCallKeyHandled;
  private final Runnable mEndCallLongPress = new Runnable()
  {
    public void run()
    {
      PhoneWindowManager.this.mEndCallKeyHandled = true;
      if (!PhoneWindowManager.this.performHapticFeedbackLw(null, 0, false)) {
        PhoneWindowManager.-wrap14(PhoneWindowManager.this);
      }
      PhoneWindowManager.this.showGlobalActionsInternal();
    }
  };
  int mEndcallBehavior;
  private final SparseArray<KeyCharacterMap.FallbackAction> mFallbackActions = new SparseArray();
  IApplicationToken mFocusedApp;
  WindowManagerPolicy.WindowState mFocusedWindow;
  int mForceClearedSystemUiFlags = 0;
  private boolean mForceDefaultOrientation = false;
  boolean mForceShowSystemBars;
  boolean mForceStatusBar;
  boolean mForceStatusBarFromKeyguard;
  private boolean mForceStatusBarTransparent;
  boolean mForcingShowNavBar;
  int mForcingShowNavBarLayer;
  OpGlobalActions mGlobalActions;
  private GlobalKeyManager mGlobalKeyManager;
  private boolean mGoToSleepOnButtonPressTheaterMode;
  volatile boolean mGoingToSleep;
  private UEventObserver mHDMIObserver = new UEventObserver()
  {
    public void onUEvent(UEventObserver.UEvent paramAnonymousUEvent)
    {
      PhoneWindowManager.this.setHdmiPlugged("1".equals(paramAnonymousUEvent.get("SWITCH_STATE")));
    }
  };
  Handler mHandler;
  private boolean mHasFeatureWatch;
  boolean mHasNavigationBar = false;
  boolean mHasSoftInput = false;
  boolean mHaveBuiltInKeyboard;
  boolean mHavePendingMediaKeyRepeatWithWakeLock;
  HdmiControl mHdmiControl;
  boolean mHdmiPlugged;
  private final Runnable mHiddenNavPanic = new Runnable()
  {
    public void run()
    {
      synchronized (PhoneWindowManager.this.mWindowManagerFuncs.getWindowManagerLock())
      {
        boolean bool = PhoneWindowManager.this.isUserSetupComplete();
        if (!bool) {
          return;
        }
        PhoneWindowManager.-set0(PhoneWindowManager.this, SystemClock.uptimeMillis());
        if (!PhoneWindowManager.-wrap0(PhoneWindowManager.this.mLastSystemUiFlags)) {
          PhoneWindowManager.-get3(PhoneWindowManager.this).showTransient();
        }
        return;
      }
    }
  };
  boolean mHideLockScreen;
  final InputEventReceiver.Factory mHideNavInputEventReceiverFactory = new InputEventReceiver.Factory()
  {
    public InputEventReceiver createInputEventReceiver(InputChannel paramAnonymousInputChannel, Looper paramAnonymousLooper)
    {
      return new PhoneWindowManager.HideNavInputEventReceiver(PhoneWindowManager.this, paramAnonymousInputChannel, paramAnonymousLooper);
    }
  };
  boolean mHomeConsumed;
  boolean mHomeDoubleTapPending;
  private final Runnable mHomeDoubleTapTimeoutRunnable = new Runnable()
  {
    public void run()
    {
      if (PhoneWindowManager.this.mHomeDoubleTapPending)
      {
        PhoneWindowManager.this.mHomeDoubleTapPending = false;
        PhoneWindowManager.this.handleShortPressOnHome();
      }
    }
  };
  Intent mHomeIntent;
  boolean mHomePressed;
  private ImmersiveModeConfirmation mImmersiveModeConfirmation;
  int mIncallPowerBehavior;
  int mInitialMetaState;
  WindowManagerPolicy.InputConsumer mInputConsumer = null;
  InputManagerInternal mInputManagerInternal;
  long[] mKeyboardTapVibePattern;
  KeyguardServiceDelegate mKeyguardDelegate;
  boolean mKeyguardDrawComplete;
  final KeyguardServiceDelegate.DrawnListener mKeyguardDrawnCallback = new KeyguardServiceDelegate.DrawnListener()
  {
    public void onDrawn()
    {
      if (PhoneWindowManager.DEBUG_WAKEUP) {
        Slog.d("WindowManager", "mKeyguardDelegate.ShowListener.onDrawn.");
      }
      PhoneWindowManager.this.mHandler.sendEmptyMessage(5);
    }
  };
  private boolean mKeyguardDrawnOnce;
  private Runnable mKeyguardFingerprintChanageCallback = new Runnable()
  {
    public void run()
    {
      PhoneWindowManager.this.mHandler.removeMessages(101);
      PhoneWindowManager.this.mHandler.sendEmptyMessage(101);
    }
  };
  private boolean mKeyguardHidden;
  volatile boolean mKeyguardOccluded;
  private WindowManagerPolicy.WindowState mKeyguardScrim;
  boolean mKeyguardSecure;
  boolean mKeyguardSecureIncludingHidden;
  int mLandscapeRotation = 0;
  boolean mLanguageSwitchKeyPressed;
  final Rect mLastDockedStackBounds = new Rect();
  int mLastDockedStackSysUiFlags;
  boolean mLastFocusNeedsMenu = false;
  int mLastFullscreenStackSysUiFlags;
  WindowManagerPolicy.WindowState mLastInputMethodTargetWindow = null;
  WindowManagerPolicy.WindowState mLastInputMethodWindow = null;
  final Rect mLastNonDockedStackBounds = new Rect();
  int mLastSystemUiFlags;
  boolean mLidControlsScreenLock;
  boolean mLidControlsSleep;
  int mLidKeyboardAccessibility;
  int mLidNavigationAccessibility;
  int mLidOpenRotation;
  int mLidState = -1;
  private final Object mLock = new Object();
  long[] mLockPatternVibePattern;
  int mLockScreenTimeout;
  boolean mLockScreenTimerActive;
  private final LogDecelerateInterpolator mLogDecelerateInterpolator = new LogDecelerateInterpolator(100, 0);
  int mLongPressOnBackBehavior;
  private int mLongPressOnHomeBehavior;
  int mLongPressOnPowerBehavior;
  long[] mLongPressVibePattern;
  int mMetaState;
  BroadcastReceiver mMultiuserReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context arg1, Intent paramAnonymousIntent)
    {
      if ("android.intent.action.USER_SWITCHED".equals(paramAnonymousIntent.getAction())) {
        PhoneWindowManager.this.mSettingsObserver.onChange(false);
      }
      synchronized (PhoneWindowManager.this.mWindowManagerFuncs.getWindowManagerLock())
      {
        PhoneWindowManager.this.mLastSystemUiFlags = 0;
        PhoneWindowManager.-wrap3(PhoneWindowManager.this);
        return;
      }
    }
  };
  int mNavBarOpacityMode = 0;
  WindowManagerPolicy.WindowState mNavigationBar = null;
  boolean mNavigationBarCanMove = false;
  private final BarController mNavigationBarController = new BarController("NavigationBar", 134217728, 536870912, Integer.MIN_VALUE, 2, 134217728, 32768);
  int[] mNavigationBarHeightForRotationDefault = new int[4];
  int[] mNavigationBarHeightForRotationInCarMode = new int[4];
  int mNavigationBarPosition = 0;
  int[] mNavigationBarWidthForRotationDefault = new int[4];
  int[] mNavigationBarWidthForRotationInCarMode = new int[4];
  final Rect mNonDockedStackBounds = new Rect();
  MyOrientationListener mOrientationListener;
  boolean mOrientationSensorEnabled = false;
  int mOverscanBottom = 0;
  int mOverscanLeft = 0;
  int mOverscanRight = 0;
  int mOverscanScreenHeight;
  int mOverscanScreenLeft;
  int mOverscanScreenTop;
  int mOverscanScreenWidth;
  int mOverscanTop = 0;
  boolean mPendingCapsLockToggle;
  boolean mPendingMetaAction;
  private long mPendingPanicGestureUptime;
  volatile int mPendingWakeKey = -1;
  int mPointerLocationMode = 0;
  PointerLocationView mPointerLocationView;
  int mPortraitRotation = 0;
  volatile boolean mPowerKeyHandled;
  volatile int mPowerKeyPressCounter;
  PowerManager.WakeLock mPowerKeyWakeLock;
  PowerManager mPowerManager;
  PowerManagerInternal mPowerManagerInternal;
  boolean mPreloadedRecentApps;
  int mRecentAppsHeldModifiers;
  volatile boolean mRecentsVisible;
  int mResettingSystemUiFlags = 0;
  int mRestrictedOverscanScreenHeight;
  int mRestrictedOverscanScreenLeft;
  int mRestrictedOverscanScreenTop;
  int mRestrictedOverscanScreenWidth;
  int mRestrictedScreenHeight;
  int mRestrictedScreenLeft;
  int mRestrictedScreenTop;
  int mRestrictedScreenWidth;
  boolean mSafeMode;
  long[] mSafeModeDisabledVibePattern;
  long[] mSafeModeEnabledVibePattern;
  ScreenLockTimeout mScreenLockTimeout = new ScreenLockTimeout();
  ActivityManagerInternal.SleepToken mScreenOffSleepToken;
  boolean mScreenOnEarly;
  boolean mScreenOnFully;
  WindowManagerPolicy.ScreenOnListener mScreenOnListener;
  protected boolean mScreenshotChordEnabled;
  protected long mScreenshotChordPowerKeyTime;
  protected boolean mScreenshotChordPowerKeyTriggered;
  protected boolean mScreenshotChordVolumeDownKeyConsumed;
  protected long mScreenshotChordVolumeDownKeyTime;
  protected boolean mScreenshotChordVolumeDownKeyTriggered;
  protected boolean mScreenshotChordVolumeUpKeyTriggered;
  ServiceConnection mScreenshotConnection = null;
  final Object mScreenshotLock = new Object();
  protected final ScreenshotRunnable mScreenshotRunnable = new ScreenshotRunnable();
  final Runnable mScreenshotTimeout = new Runnable()
  {
    public void run()
    {
      synchronized (PhoneWindowManager.this.mScreenshotLock)
      {
        if (PhoneWindowManager.this.mScreenshotConnection != null)
        {
          PhoneWindowManager.this.mContext.unbindService(PhoneWindowManager.this.mScreenshotConnection);
          PhoneWindowManager.this.mScreenshotConnection = null;
          PhoneWindowManager.-wrap13(PhoneWindowManager.this);
        }
        return;
      }
    }
  };
  boolean mSearchKeyShortcutPending;
  SearchManager mSearchManager;
  int mSeascapeRotation = 0;
  private boolean mSecureDismissingKeyguard;
  final Object mServiceAquireLock = new Object();
  SettingsObserver mSettingsObserver;
  int mShortPressOnPowerBehavior;
  int mShortPressOnSleepBehavior;
  int mShortPressWindowBehavior;
  private LongSparseArray<IShortcutService> mShortcutKeyServices = new LongSparseArray();
  ShortcutManager mShortcutManager;
  boolean mShowingDream;
  boolean mShowingLockscreen;
  int mStableBottom;
  int mStableFullscreenBottom;
  int mStableFullscreenLeft;
  int mStableFullscreenRight;
  int mStableFullscreenTop;
  int mStableLeft;
  int mStableRight;
  int mStableTop;
  WindowManagerPolicy.WindowState mStatusBar = null;
  private final StatusBarController mStatusBarController = new StatusBarController();
  int mStatusBarHeight;
  int mStatusBarLayer;
  StatusBarManagerInternal mStatusBarManagerInternal;
  IStatusBarService mStatusBarService;
  boolean mSupportAutoRotation;
  private boolean mSupportLongPressPowerWhenNonInteractive;
  boolean mSystemBooted;
  int mSystemBottom;
  private SystemGesturesPointerEventListener mSystemGestures;
  int mSystemLeft;
  boolean mSystemReady;
  int mSystemRight;
  int mSystemTop;
  private final MutableBoolean mTmpBoolean = new MutableBoolean(false);
  WindowManagerPolicy.WindowState mTopDockedOpaqueOrDimmingWindowState;
  WindowManagerPolicy.WindowState mTopDockedOpaqueWindowState;
  WindowManagerPolicy.WindowState mTopFullscreenOpaqueOrDimmingWindowState;
  WindowManagerPolicy.WindowState mTopFullscreenOpaqueWindowState;
  boolean mTopIsFullscreen;
  boolean mTranslucentDecorEnabled = true;
  int mTriplePressOnPowerBehavior;
  volatile boolean mTvPictureInPictureVisible;
  int mUiMode;
  IUiModeManager mUiModeManager;
  int mUndockedHdmiRotation;
  int mUnrestrictedScreenHeight;
  int mUnrestrictedScreenLeft;
  int mUnrestrictedScreenTop;
  int mUnrestrictedScreenWidth;
  int mUpsideDownRotation = 0;
  boolean mUseTvRouting;
  int mUserRotation = 0;
  int mUserRotationMode = 0;
  int mVibrateOnTouchIntensity = 1;
  Vibrator mVibrator;
  long[] mVirtualKeyVibePattern;
  int mVoiceContentBottom;
  int mVoiceContentLeft;
  int mVoiceContentRight;
  int mVoiceContentTop;
  boolean mWakeGestureEnabledSetting;
  MyWakeGestureListener mWakeGestureListener;
  boolean mWifiDisplayConnected = false;
  int mWifiDisplayCustomRotation = -1;
  BroadcastReceiver mWifiDisplayReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (paramAnonymousIntent.getAction().equals("org.codeaurora.intent.action.WIFI_DISPLAY_VIDEO")) {
        if (paramAnonymousIntent.getIntExtra("state", 0) != 1) {
          break label54;
        }
      }
      label54:
      for (PhoneWindowManager.this.mWifiDisplayConnected = true;; PhoneWindowManager.this.mWifiDisplayConnected = false)
      {
        PhoneWindowManager.this.mWifiDisplayCustomRotation = paramAnonymousIntent.getIntExtra("wfd_UIBC_rot", -1);
        PhoneWindowManager.this.updateRotation(true);
        return;
      }
    }
  };
  private WindowManagerPolicy.WindowState mWinDismissingKeyguard;
  private WindowManagerPolicy.WindowState mWinShowWhenLocked;
  IWindowManager mWindowManager;
  final Runnable mWindowManagerDrawCallback = new Runnable()
  {
    public void run()
    {
      if (PhoneWindowManager.DEBUG_WAKEUP) {
        Slog.i("WindowManager", "All windows ready for display!");
      }
      PhoneWindowManager.this.mHandler.sendEmptyMessage(7);
    }
  };
  boolean mWindowManagerDrawComplete;
  WindowManagerPolicy.WindowManagerFuncs mWindowManagerFuncs;
  WindowManagerInternal mWindowManagerInternal;
  
  static
  {
    DEBUG_INPUT = false;
    DEBUG_KEYGUARD = false;
    DEBUG_LAYOUT = false;
    DEBUG_STARTING_WINDOW = false;
    DEBUG_WAKEUP = true;
    SHOW_STARTING_ANIMATIONS = true;
    VIBRATION_ATTRIBUTES = new AudioAttributes.Builder().setContentType(4).setUsage(13).build();
    OEM_PACKAGE_LIST = Arrays.asList(new String[] { "com.oneplus.deskclock", "com.android.deskclock" });
    sApplicationLaunchKeyCategories = new SparseArray();
    sApplicationLaunchKeyCategories.append(64, "android.intent.category.APP_BROWSER");
    sApplicationLaunchKeyCategories.append(65, "android.intent.category.APP_EMAIL");
    sApplicationLaunchKeyCategories.append(207, "android.intent.category.APP_CONTACTS");
    sApplicationLaunchKeyCategories.append(208, "android.intent.category.APP_CALENDAR");
    sApplicationLaunchKeyCategories.append(209, "android.intent.category.APP_MUSIC");
    sApplicationLaunchKeyCategories.append(210, "android.intent.category.APP_CALCULATOR");
    mTmpParentFrame = new Rect();
    mTmpDisplayFrame = new Rect();
    mTmpOverscanFrame = new Rect();
    mTmpContentFrame = new Rect();
    mTmpVisibleFrame = new Rect();
    mTmpDecorFrame = new Rect();
    mTmpStableFrame = new Rect();
    mTmpNavigationFrame = new Rect();
    mTmpOutsetFrame = new Rect();
    mTmpRect = new Rect();
  }
  
  private void applyLidSwitchState()
  {
    if ((this.mLidState == 0) && (this.mLidControlsSleep)) {
      this.mPowerManager.goToSleep(SystemClock.uptimeMillis(), 3, 1);
    }
    synchronized (this.mLock)
    {
      do
      {
        updateWakeGestureListenerLp();
        return;
      } while ((this.mLidState != 0) || (!this.mLidControlsScreenLock));
      this.mWindowManagerFuncs.lockDeviceNow();
    }
  }
  
  private void applyStableConstraints(int paramInt1, int paramInt2, Rect paramRect)
  {
    if ((paramInt1 & 0x100) != 0)
    {
      if ((paramInt2 & 0x400) == 0) {
        break label93;
      }
      if (paramRect.left < this.mStableFullscreenLeft) {
        paramRect.left = this.mStableFullscreenLeft;
      }
      if (paramRect.top < this.mStableFullscreenTop) {
        paramRect.top = this.mStableFullscreenTop;
      }
      if (paramRect.right > this.mStableFullscreenRight) {
        paramRect.right = this.mStableFullscreenRight;
      }
      if (paramRect.bottom > this.mStableFullscreenBottom) {
        paramRect.bottom = this.mStableFullscreenBottom;
      }
    }
    label93:
    do
    {
      return;
      if (paramRect.left < this.mStableLeft) {
        paramRect.left = this.mStableLeft;
      }
      if (paramRect.top < this.mStableTop) {
        paramRect.top = this.mStableTop;
      }
      if (paramRect.right > this.mStableRight) {
        paramRect.right = this.mStableRight;
      }
    } while (paramRect.bottom <= this.mStableBottom);
    paramRect.bottom = this.mStableBottom;
  }
  
  private boolean areSystemNavigationKeysEnabled()
  {
    return Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "system_navigation_keys_enabled", 0, -2) == 1;
  }
  
  private boolean areTranslucentBarsAllowed()
  {
    return this.mTranslucentDecorEnabled;
  }
  
  private static void awakenDreams()
  {
    IDreamManager localIDreamManager = getDreamManager();
    if (localIDreamManager != null) {}
    try
    {
      localIDreamManager.awaken();
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  private void backLongPress()
  {
    this.mBackKeyHandled = true;
    switch (this.mLongPressOnBackBehavior)
    {
    case 0: 
    default: 
      return;
    }
    startActivityAsUser(new Intent("android.intent.action.VOICE_ASSIST"), UserHandle.CURRENT_OR_SELF);
  }
  
  private void calculateRelevantTaskInsets(Rect paramRect1, Rect paramRect2, int paramInt1, int paramInt2)
  {
    mTmpRect.set(0, 0, paramInt1, paramInt2);
    mTmpRect.inset(paramRect2);
    mTmpRect.intersect(paramRect1);
    paramRect2.set(mTmpRect.left - paramRect1.left, mTmpRect.top - paramRect1.top, paramRect1.right - mTmpRect.right, paramRect1.bottom - mTmpRect.bottom);
  }
  
  private boolean canHideNavigationBar()
  {
    return this.mHasNavigationBar;
  }
  
  private boolean canReceiveInput(WindowManagerPolicy.WindowState paramWindowState)
  {
    int i;
    if ((paramWindowState.getAttrs().flags & 0x8) != 0)
    {
      i = 1;
      if ((paramWindowState.getAttrs().flags & 0x20000) == 0) {
        break label48;
      }
    }
    label48:
    for (int j = 1;; j = 0)
    {
      if ((i ^ j) == 0) {
        break label53;
      }
      return false;
      i = 0;
      break;
    }
    label53:
    return true;
  }
  
  private void cancelPendingBackKeyAction()
  {
    if (!this.mBackKeyHandled)
    {
      this.mBackKeyHandled = true;
      this.mHandler.removeMessages(18);
    }
  }
  
  private void cancelPendingPowerKeyAction()
  {
    if (!this.mPowerKeyHandled)
    {
      this.mPowerKeyHandled = true;
      this.mHandler.removeMessages(14);
    }
  }
  
  private void cancelPendingScreenshotChordAction()
  {
    this.mHandler.removeCallbacks(this.mScreenshotRunnable);
  }
  
  private void clearClearableFlagsLw()
  {
    int i = this.mResettingSystemUiFlags | 0x7;
    if (i != this.mResettingSystemUiFlags)
    {
      this.mResettingSystemUiFlags = i;
      this.mWindowManagerFuncs.reevaluateStatusBarVisibility();
    }
  }
  
  private int configureNavBarOpacity(int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    int i;
    if (this.mNavBarOpacityMode == 0) {
      if ((!paramBoolean1) && (!paramBoolean2))
      {
        i = paramInt;
        if (!paramBoolean3) {}
      }
      else
      {
        i = setNavBarOpaqueFlag(paramInt);
      }
    }
    for (;;)
    {
      paramInt = i;
      if (!areTranslucentBarsAllowed()) {
        paramInt = i & 0x7FFFFFFF;
      }
      return paramInt;
      i = paramInt;
      if (this.mNavBarOpacityMode == 1) {
        if (paramBoolean3) {
          i = setNavBarOpaqueFlag(paramInt);
        } else if (paramBoolean2) {
          i = setNavBarTranslucentFlag(paramInt);
        } else {
          i = setNavBarOpaqueFlag(paramInt);
        }
      }
    }
  }
  
  private void disablePointerLocation()
  {
    if (this.mPointerLocationView != null)
    {
      this.mWindowManagerFuncs.unregisterPointerEventListener(this.mPointerLocationView);
      ((WindowManager)this.mContext.getSystemService("window")).removeView(this.mPointerLocationView);
      this.mPointerLocationView = null;
    }
  }
  
  private void dismissKeyboardShortcutsMenu()
  {
    StatusBarManagerInternal localStatusBarManagerInternal = getStatusBarManagerInternal();
    if (localStatusBarManagerInternal != null) {
      localStatusBarManagerInternal.dismissKeyboardShortcutsMenu();
    }
  }
  
  private void dispatchDirectAudioEvent(KeyEvent paramKeyEvent)
  {
    if (paramKeyEvent.getAction() != 0) {
      return;
    }
    int i = paramKeyEvent.getKeyCode();
    String str = this.mContext.getOpPackageName();
    switch (i)
    {
    }
    for (;;)
    {
      return;
      try
      {
        getAudioService().adjustSuggestedStreamVolume(1, Integer.MIN_VALUE, 4101, str, "WindowManager");
        return;
      }
      catch (RemoteException paramKeyEvent)
      {
        Log.e("WindowManager", "Error dispatching volume up in dispatchTvAudioEvent.", paramKeyEvent);
        return;
      }
      try
      {
        getAudioService().adjustSuggestedStreamVolume(-1, Integer.MIN_VALUE, 4101, str, "WindowManager");
        return;
      }
      catch (RemoteException paramKeyEvent)
      {
        Log.e("WindowManager", "Error dispatching volume down in dispatchTvAudioEvent.", paramKeyEvent);
        return;
      }
      try
      {
        if (paramKeyEvent.getRepeatCount() == 0)
        {
          getAudioService().adjustSuggestedStreamVolume(101, Integer.MIN_VALUE, 4101, str, "WindowManager");
          return;
        }
      }
      catch (RemoteException paramKeyEvent)
      {
        Log.e("WindowManager", "Error dispatching mute in dispatchTvAudioEvent.", paramKeyEvent);
      }
    }
  }
  
  private void disposeInputConsumer(WindowManagerPolicy.InputConsumer paramInputConsumer)
  {
    if (paramInputConsumer != null) {
      paramInputConsumer.dismiss();
    }
  }
  
  private boolean drawsSystemBarBackground(WindowManagerPolicy.WindowState paramWindowState)
  {
    return (paramWindowState == null) || ((paramWindowState.getAttrs().flags & 0x80000000) != 0);
  }
  
  private void enablePointerLocation()
  {
    if (this.mPointerLocationView == null)
    {
      this.mPointerLocationView = new PointerLocationView(this.mContext);
      this.mPointerLocationView.setPrintCoords(false);
      WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams(-1, -1);
      localLayoutParams.type = 2015;
      localLayoutParams.flags = 1304;
      if (ActivityManager.isHighEndGfx())
      {
        localLayoutParams.flags |= 0x1000000;
        localLayoutParams.privateFlags |= 0x2;
      }
      localLayoutParams.format = -3;
      localLayoutParams.setTitle("PointerLocation");
      WindowManager localWindowManager = (WindowManager)this.mContext.getSystemService("window");
      localLayoutParams.inputFeatures |= 0x2;
      localWindowManager.addView(this.mPointerLocationView, localLayoutParams);
      this.mWindowManagerFuncs.registerPointerEventListener(this.mPointerLocationView);
    }
  }
  
  private void finishKeyguardDrawn()
  {
    synchronized (this.mLock)
    {
      if (this.mScreenOnEarly)
      {
        boolean bool = this.mKeyguardDrawComplete;
        if (!bool) {}
      }
      else
      {
        return;
      }
      this.mKeyguardDrawComplete = true;
      if (this.mKeyguardDelegate != null) {
        this.mHandler.removeMessages(6);
      }
      this.mWindowManagerDrawComplete = false;
      this.mWindowManagerInternal.waitForAllWindowsDrawn(this.mWindowManagerDrawCallback, 1000L);
      return;
    }
  }
  
  private void finishPowerKeyPress()
  {
    this.mBeganFromNonInteractive = false;
    this.mPowerKeyPressCounter = 0;
    if (this.mPowerKeyWakeLock.isHeld()) {
      this.mPowerKeyWakeLock.release();
    }
  }
  
  private void finishScreenTurningOn()
  {
    synchronized (this.mLock)
    {
      updateOrientationListenerLp();
    }
    for (;;)
    {
      synchronized (this.mLock)
      {
        if (DEBUG_WAKEUP) {
          Slog.d("WindowManager", "finishScreenTurningOn: mAwake=" + this.mAwake + ", mScreenOnEarly=" + this.mScreenOnEarly + ", mScreenOnFully=" + this.mScreenOnFully + ", mKeyguardDrawComplete=" + this.mKeyguardDrawComplete + ", mWindowManagerDrawComplete=" + this.mWindowManagerDrawComplete + ", mKeyguardDelegate.isShowing()= " + this.mKeyguardDelegate.isShowing() + ", mKeyguardDelegate.isFingerprintAuthenticating()= " + this.mKeyguardDelegate.isFingerprintAuthenticating());
        }
        if ((!this.mScreenOnFully) && (this.mScreenOnEarly) && (this.mWindowManagerDrawComplete) && ((!this.mAwake) || (this.mKeyguardDrawComplete)))
        {
          this.mHandler.removeMessages(102);
          if (this.mKeyguardDelegate.isFingerprintAuthenticating())
          {
            this.mHandler.sendEmptyMessageDelayed(102, 1000L);
            return;
            localObject2 = finally;
            throw ((Throwable)localObject2);
          }
        }
        else
        {
          return;
        }
        if (DEBUG_WAKEUP) {
          Slog.i("WindowManager", "Finished screen turning on...");
        }
        WindowManagerPolicy.ScreenOnListener localScreenOnListener = this.mScreenOnListener;
        this.mScreenOnListener = null;
        this.mScreenOnFully = true;
        if ((!this.mKeyguardDrawnOnce) && (this.mAwake))
        {
          this.mKeyguardDrawnOnce = true;
          int j = 1;
          i = j;
          if (this.mBootMessageNeedsHiding)
          {
            this.mBootMessageNeedsHiding = false;
            hideBootMessages();
            i = j;
          }
          if (localScreenOnListener != null) {
            localScreenOnListener.onScreenOn();
          }
          if (i == 0) {}
        }
      }
      try
      {
        this.mWindowManager.enableScreenIfNeeded();
        return;
      }
      catch (RemoteException localRemoteException) {}
      int i = 0;
    }
    localObject3 = finally;
    throw ((Throwable)localObject3);
  }
  
  private void finishWindowsDrawn()
  {
    synchronized (this.mLock)
    {
      if (this.mScreenOnEarly)
      {
        boolean bool = this.mWindowManagerDrawComplete;
        if (!bool) {}
      }
      else
      {
        return;
      }
      this.mWindowManagerDrawComplete = true;
      finishScreenTurningOn();
      return;
    }
  }
  
  private boolean forcesDrawStatusBarBackground(WindowManagerPolicy.WindowState paramWindowState)
  {
    return (paramWindowState == null) || ((paramWindowState.getAttrs().privateFlags & 0x20000) != 0);
  }
  
  static IAudioService getAudioService()
  {
    IAudioService localIAudioService = IAudioService.Stub.asInterface(ServiceManager.checkService("audio"));
    if (localIAudioService == null) {
      Log.w("WindowManager", "Unable to find IAudioService interface.");
    }
    return localIAudioService;
  }
  
  static IDreamManager getDreamManager()
  {
    return IDreamManager.Stub.asInterface(ServiceManager.checkService("dreams"));
  }
  
  private HdmiControl getHdmiControl()
  {
    if (this.mHdmiControl == null)
    {
      HdmiControlManager localHdmiControlManager = (HdmiControlManager)this.mContext.getSystemService("hdmi_control");
      HdmiPlaybackClient localHdmiPlaybackClient = null;
      if (localHdmiControlManager != null) {
        localHdmiPlaybackClient = localHdmiControlManager.getPlaybackClient();
      }
      this.mHdmiControl = new HdmiControl(localHdmiPlaybackClient, null);
    }
    return this.mHdmiControl;
  }
  
  static long[] getLongIntArray(Resources paramResources, int paramInt)
  {
    paramResources = paramResources.getIntArray(paramInt);
    if (paramResources == null) {
      return null;
    }
    long[] arrayOfLong = new long[paramResources.length];
    paramInt = 0;
    while (paramInt < paramResources.length)
    {
      arrayOfLong[paramInt] = paramResources[paramInt];
      paramInt += 1;
    }
    return arrayOfLong;
  }
  
  private int getMaxMultiPressPowerCount()
  {
    if (this.mTriplePressOnPowerBehavior != 0) {
      return 3;
    }
    if (this.mDoublePressOnPowerBehavior != 0) {
      return 2;
    }
    return 1;
  }
  
  private int getNavigationBarHeight(int paramInt1, int paramInt2)
  {
    return this.mNavigationBarHeightForRotationDefault[paramInt1];
  }
  
  private int getNavigationBarWidth(int paramInt1, int paramInt2)
  {
    return this.mNavigationBarWidthForRotationDefault[paramInt1];
  }
  
  private int getResolvedLongPressOnPowerBehavior()
  {
    if (FactoryTest.isLongPressOnPowerOffEnabled()) {
      return 3;
    }
    return this.mLongPressOnPowerBehavior;
  }
  
  private long getScreenshotChordLongPressDelay()
  {
    if (this.mKeyguardDelegate.isShowing()) {
      return ((float)ViewConfiguration.get(this.mContext).getDeviceGlobalActionKeyTimeout() * 1.5625F);
    }
    return ((float)ViewConfiguration.get(this.mContext).getDeviceGlobalActionKeyTimeout() * 0.375F);
  }
  
  private SearchManager getSearchManager()
  {
    if (this.mSearchManager == null) {
      this.mSearchManager = ((SearchManager)this.mContext.getSystemService("search"));
    }
    return this.mSearchManager;
  }
  
  private void handleDoubleTapOnHome()
  {
    if (this.mDoubleTapOnHomeBehavior == 1)
    {
      this.mHomeConsumed = true;
      toggleRecentApps();
    }
  }
  
  private void handleHideBootMessage()
  {
    synchronized (this.mLock)
    {
      if (!this.mKeyguardDrawnOnce)
      {
        this.mBootMessageNeedsHiding = true;
        return;
      }
      if (this.mBootMsgDialog != null)
      {
        if (DEBUG_WAKEUP) {
          Slog.d("WindowManager", "handleHideBootMessage: dismissing");
        }
        this.mBootMsgDialog.dismiss();
        this.mBootMsgDialog = null;
      }
      return;
    }
  }
  
  private void handleLongPressOnHome(int paramInt)
  {
    if (this.mLongPressOnHomeBehavior == 0) {
      return;
    }
    this.mHomeConsumed = true;
    performHapticFeedbackLw(null, 0, false);
    switch (this.mLongPressOnHomeBehavior)
    {
    default: 
      Log.w("WindowManager", "Undefined home long press behavior: " + this.mLongPressOnHomeBehavior);
      return;
    case 1: 
      toggleRecentApps();
      return;
    }
    launchAssistAction(null, paramInt);
  }
  
  private boolean hasLongPressOnBackBehavior()
  {
    boolean bool = false;
    if (this.mLongPressOnBackBehavior != 0) {
      bool = true;
    }
    return bool;
  }
  
  private boolean hasLongPressOnPowerBehavior()
  {
    boolean bool = false;
    if (getResolvedLongPressOnPowerBehavior() != 0) {
      bool = true;
    }
    return bool;
  }
  
  private void hideRecentApps(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mPreloadedRecentApps = false;
    StatusBarManagerInternal localStatusBarManagerInternal = getStatusBarManagerInternal();
    if (localStatusBarManagerInternal != null) {
      localStatusBarManagerInternal.hideRecentApps(paramBoolean1, paramBoolean2);
    }
  }
  
  private boolean interceptFallback(WindowManagerPolicy.WindowState paramWindowState, KeyEvent paramKeyEvent, int paramInt)
  {
    return ((interceptKeyBeforeQueueing(paramKeyEvent, paramInt) & 0x1) != 0) && (interceptKeyBeforeDispatching(paramWindowState, paramKeyEvent, paramInt) == 0L);
  }
  
  private void interceptPowerKeyDown(KeyEvent paramKeyEvent, boolean paramBoolean)
  {
    if (!this.mPowerKeyWakeLock.isHeld()) {
      this.mPowerKeyWakeLock.acquire();
    }
    if (this.mPowerKeyPressCounter != 0) {
      this.mHandler.removeMessages(13);
    }
    if (this.mImmersiveModeConfirmation.onPowerKeyDown(paramBoolean, SystemClock.elapsedRealtime(), isImmersiveMode(this.mLastSystemUiFlags), isNavBarEmpty(this.mLastSystemUiFlags))) {
      this.mHandler.post(this.mHiddenNavPanic);
    }
    Object localObject;
    boolean bool1;
    boolean bool2;
    if ((!paramBoolean) || (this.mScreenshotChordPowerKeyTriggered))
    {
      localObject = getTelecommService();
      bool1 = false;
      bool2 = bool1;
      if (localObject != null)
      {
        if (!((TelecomManager)localObject).isRinging()) {
          break label310;
        }
        ((TelecomManager)localObject).silenceRinger();
        bool2 = bool1;
      }
      label117:
      localObject = (GestureLauncherService)LocalServices.getService(GestureLauncherService.class);
      bool1 = false;
      if (localObject != null)
      {
        boolean bool3 = ((GestureLauncherService)localObject).interceptPowerKeyDown(paramKeyEvent, paramBoolean, this.mTmpBoolean);
        bool1 = bool3;
        if (this.mTmpBoolean.value)
        {
          bool1 = bool3;
          if (this.mGoingToSleep)
          {
            this.mCameraGestureTriggeredDuringGoingToSleep = true;
            bool1 = bool3;
          }
        }
      }
      if ((bool2) || (this.mScreenshotChordVolumeDownKeyTriggered) || (this.mScreenshotChordVolumeUpKeyTriggered)) {
        break label350;
      }
      label198:
      this.mPowerKeyHandled = bool1;
      if (!this.mPowerKeyHandled)
      {
        if (!paramBoolean) {
          break label370;
        }
        if (hasLongPressOnPowerBehavior())
        {
          paramKeyEvent = this.mHandler.obtainMessage(14);
          paramKeyEvent.setAsynchronous(true);
          localObject = this.mHandler;
          if (!this.mKeyguardDelegate.isShowing()) {
            break label355;
          }
        }
      }
    }
    label310:
    label350:
    label355:
    for (long l = ((float)ViewConfiguration.get(this.mContext).getDeviceGlobalActionKeyTimeout() * 2.5F);; l = ViewConfiguration.get(this.mContext).getDeviceGlobalActionKeyTimeout())
    {
      ((Handler)localObject).sendMessageDelayed(paramKeyEvent, l);
      return;
      if ((paramKeyEvent.getFlags() & 0x400) != 0) {
        break;
      }
      this.mScreenshotChordPowerKeyTriggered = true;
      this.mScreenshotChordPowerKeyTime = paramKeyEvent.getDownTime();
      interceptScreenshotChord();
      break;
      bool2 = bool1;
      if ((this.mIncallPowerBehavior & 0x2) == 0) {
        break label117;
      }
      bool2 = bool1;
      if (!((TelecomManager)localObject).isInCall()) {
        break label117;
      }
      bool2 = bool1;
      if (!paramBoolean) {
        break label117;
      }
      bool2 = ((TelecomManager)localObject).endCall();
      break label117;
      bool1 = true;
      break label198;
    }
    label370:
    wakeUpFromPowerKey(paramKeyEvent.getDownTime());
    if ((this.mSupportLongPressPowerWhenNonInteractive) && (hasLongPressOnPowerBehavior()))
    {
      paramKeyEvent = this.mHandler.obtainMessage(14);
      paramKeyEvent.setAsynchronous(true);
      this.mHandler.sendMessageDelayed(paramKeyEvent, ViewConfiguration.get(this.mContext).getDeviceGlobalActionKeyTimeout());
      this.mBeganFromNonInteractive = true;
      return;
    }
    if (getMaxMultiPressPowerCount() <= 1)
    {
      this.mPowerKeyHandled = true;
      return;
    }
    this.mBeganFromNonInteractive = true;
  }
  
  private void interceptPowerKeyUp(KeyEvent paramKeyEvent, boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = 0;
    if (!paramBoolean2) {}
    long l;
    for (paramBoolean2 = this.mPowerKeyHandled;; paramBoolean2 = true)
    {
      this.mScreenshotChordPowerKeyTriggered = false;
      cancelPendingScreenshotChordAction();
      cancelPendingPowerKeyAction();
      if (paramBoolean2) {
        break label125;
      }
      this.mPowerKeyPressCounter += 1;
      int j = getMaxMultiPressPowerCount();
      l = paramKeyEvent.getDownTime();
      if (this.mPowerKeyPressCounter >= j) {
        break;
      }
      paramKeyEvent = this.mHandler;
      if (paramBoolean1) {
        i = 1;
      }
      paramKeyEvent = paramKeyEvent.obtainMessage(13, i, this.mPowerKeyPressCounter, Long.valueOf(l));
      paramKeyEvent.setAsynchronous(true);
      this.mHandler.sendMessageDelayed(paramKeyEvent, ViewConfiguration.getDoubleTapTimeout());
      return;
    }
    powerPress(l, paramBoolean1, this.mPowerKeyPressCounter);
    label125:
    finishPowerKeyPress();
  }
  
  private void interceptScreenshotChord()
  {
    if ((!this.mScreenshotChordEnabled) || (!this.mScreenshotChordVolumeDownKeyTriggered) || (!this.mScreenshotChordPowerKeyTriggered) || (this.mScreenshotChordVolumeUpKeyTriggered)) {}
    long l;
    do
    {
      return;
      l = SystemClock.uptimeMillis();
    } while ((l > this.mScreenshotChordVolumeDownKeyTime + 150L) || (l > this.mScreenshotChordPowerKeyTime + 150L));
    this.mScreenshotChordVolumeDownKeyConsumed = true;
    cancelPendingPowerKeyAction();
    this.mScreenshotRunnable.setScreenshotType(1);
    this.mHandler.postDelayed(this.mScreenshotRunnable, getScreenshotChordLongPressDelay());
  }
  
  private void interceptSystemNavigationKey(KeyEvent paramKeyEvent)
  {
    IStatusBarService localIStatusBarService;
    if ((paramKeyEvent.getAction() == 1) && (areSystemNavigationKeysEnabled()))
    {
      localIStatusBarService = getStatusBarService();
      if (localIStatusBarService == null) {}
    }
    try
    {
      localIStatusBarService.handleSystemNavigationKey(paramKeyEvent.getKeyCode());
      return;
    }
    catch (RemoteException paramKeyEvent) {}
  }
  
  private boolean isAnyPortrait(int paramInt)
  {
    return (paramInt == this.mPortraitRotation) || (paramInt == this.mUpsideDownRotation);
  }
  
  private boolean isGlobalAccessibilityGestureEnabled()
  {
    return Settings.Global.getInt(this.mContext.getContentResolver(), "enable_accessibility_global_gesture_enabled", 0) == 1;
  }
  
  private boolean isHidden(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return false;
    case 1: 
      return this.mLidState == 0;
    }
    return this.mLidState == 1;
  }
  
  private boolean isImmersiveMode(int paramInt)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mNavigationBar != null)
    {
      bool1 = bool2;
      if ((paramInt & 0x2) != 0)
      {
        bool1 = bool2;
        if ((paramInt & 0x1800) != 0) {
          bool1 = canHideNavigationBar();
        }
      }
    }
    return bool1;
  }
  
  private boolean isKeyguardShowingAndNotOccluded()
  {
    if (this.mKeyguardDelegate == null) {
      return false;
    }
    return (this.mKeyguardDelegate.isShowing()) && (!this.mKeyguardOccluded);
  }
  
  private static boolean isNavBarEmpty(int paramInt)
  {
    return (paramInt & 0x1600000) == 23068672;
  }
  
  private boolean isRoundWindow()
  {
    return this.mContext.getResources().getConfiguration().isScreenRound();
  }
  
  private boolean isStatusBarKeyguard()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mStatusBar != null)
    {
      bool1 = bool2;
      if ((this.mStatusBar.getAttrs().privateFlags & 0x400) != 0) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private boolean isTheaterModeEnabled()
  {
    return Settings.Global.getInt(this.mContext.getContentResolver(), "theater_mode_on", 0) == 1;
  }
  
  private static boolean isValidGlobalKey(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return true;
    }
    return false;
  }
  
  private boolean isWakeKeyWhenScreenOff(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return true;
    case 24: 
    case 25: 
    case 164: 
      return this.mDockMode != 0;
    }
    return false;
  }
  
  private boolean layoutNavigationBar(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, Rect paramRect, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
  {
    if (this.mNavigationBar != null)
    {
      boolean bool = this.mNavigationBarController.isTransientShowing();
      this.mNavigationBarPosition = navigationBarPosition(paramInt1, paramInt2, paramInt3);
      if (this.mNavigationBarPosition == 0)
      {
        paramInt3 = getNavigationBarHeight(paramInt3, paramInt4);
        mTmpNavigationFrame.set(0, paramInt2 - paramInt7 - paramInt3, paramInt1, paramInt2 - paramInt7);
        paramInt1 = mTmpNavigationFrame.top;
        this.mStableFullscreenBottom = paramInt1;
        this.mStableBottom = paramInt1;
        if (bool)
        {
          this.mNavigationBarController.setBarShowingLw(true);
          if ((paramBoolean1) && (!paramBoolean2)) {
            break label338;
          }
        }
      }
      label338:
      label557:
      label749:
      for (;;)
      {
        paramInt1 = this.mDockTop;
        this.mCurTop = paramInt1;
        this.mVoiceContentTop = paramInt1;
        this.mContentTop = paramInt1;
        paramInt1 = this.mDockBottom;
        this.mCurBottom = paramInt1;
        this.mVoiceContentBottom = paramInt1;
        this.mContentBottom = paramInt1;
        paramInt1 = this.mDockLeft;
        this.mCurLeft = paramInt1;
        this.mVoiceContentLeft = paramInt1;
        this.mContentLeft = paramInt1;
        paramInt1 = this.mDockRight;
        this.mCurRight = paramInt1;
        this.mVoiceContentRight = paramInt1;
        this.mContentRight = paramInt1;
        this.mStatusBarLayer = this.mNavigationBar.getSurfaceLayer();
        this.mNavigationBar.computeFrameLw(mTmpNavigationFrame, mTmpNavigationFrame, mTmpNavigationFrame, mTmpNavigationFrame, mTmpNavigationFrame, paramRect, mTmpNavigationFrame, mTmpNavigationFrame);
        if (DEBUG_LAYOUT) {
          Slog.i("WindowManager", "mNavigationBar frame: " + mTmpNavigationFrame);
        }
        if (!this.mNavigationBarController.checkHiddenLw()) {
          break label751;
        }
        return true;
        if (paramBoolean1)
        {
          this.mNavigationBarController.setBarShowingLw(true);
          this.mDockBottom = mTmpNavigationFrame.top;
          this.mRestrictedScreenHeight = (this.mDockBottom - this.mRestrictedScreenTop);
          this.mRestrictedOverscanScreenHeight = (this.mDockBottom - this.mRestrictedOverscanScreenTop);
          break;
        }
        this.mNavigationBarController.setBarShowingLw(paramBoolean4);
        break;
        if ((!paramBoolean3) && (!this.mNavigationBar.isAnimatingLw()) && (!this.mNavigationBarController.wasRecentlyTranslucent()))
        {
          this.mSystemBottom = mTmpNavigationFrame.top;
          continue;
          if (this.mNavigationBarPosition == 1)
          {
            paramInt3 = getNavigationBarWidth(paramInt3, paramInt4);
            mTmpNavigationFrame.set(paramInt1 - paramInt6 - paramInt3, 0, paramInt1 - paramInt6, paramInt2);
            paramInt1 = mTmpNavigationFrame.left;
            this.mStableFullscreenRight = paramInt1;
            this.mStableRight = paramInt1;
            if (bool) {
              this.mNavigationBarController.setBarShowingLw(true);
            }
            for (;;)
            {
              if ((!paramBoolean1) || (paramBoolean2) || (paramBoolean3) || (this.mNavigationBar.isAnimatingLw()) || (this.mNavigationBarController.wasRecentlyTranslucent())) {
                break label557;
              }
              this.mSystemRight = mTmpNavigationFrame.left;
              break;
              if (paramBoolean1)
              {
                this.mNavigationBarController.setBarShowingLw(true);
                this.mDockRight = mTmpNavigationFrame.left;
                this.mRestrictedScreenWidth = (this.mDockRight - this.mRestrictedScreenLeft);
                this.mRestrictedOverscanScreenWidth = (this.mDockRight - this.mRestrictedOverscanScreenLeft);
              }
              else
              {
                this.mNavigationBarController.setBarShowingLw(paramBoolean4);
              }
            }
          }
          else if (this.mNavigationBarPosition == 2)
          {
            paramInt1 = getNavigationBarWidth(paramInt3, paramInt4);
            mTmpNavigationFrame.set(paramInt5, 0, paramInt5 + paramInt1, paramInt2);
            paramInt1 = mTmpNavigationFrame.right;
            this.mStableFullscreenLeft = paramInt1;
            this.mStableLeft = paramInt1;
            if (bool) {
              this.mNavigationBarController.setBarShowingLw(true);
            }
            for (;;)
            {
              if ((!paramBoolean1) || (paramBoolean2) || (paramBoolean3) || (this.mNavigationBar.isAnimatingLw()) || (this.mNavigationBarController.wasRecentlyTranslucent())) {
                break label749;
              }
              this.mSystemLeft = mTmpNavigationFrame.right;
              break;
              if (paramBoolean1)
              {
                this.mNavigationBarController.setBarShowingLw(true);
                this.mDockLeft = mTmpNavigationFrame.right;
                paramInt1 = this.mDockLeft;
                this.mRestrictedOverscanScreenLeft = paramInt1;
                this.mRestrictedScreenLeft = paramInt1;
                this.mRestrictedScreenWidth = (this.mDockRight - this.mRestrictedScreenLeft);
                this.mRestrictedOverscanScreenWidth = (this.mDockRight - this.mRestrictedOverscanScreenLeft);
              }
              else
              {
                this.mNavigationBarController.setBarShowingLw(paramBoolean4);
              }
            }
          }
        }
      }
    }
    label751:
    return false;
  }
  
  private boolean layoutStatusBar(Rect paramRect1, Rect paramRect2, Rect paramRect3, Rect paramRect4, Rect paramRect5, int paramInt, boolean paramBoolean)
  {
    if (this.mStatusBar != null)
    {
      int i = this.mUnrestrictedScreenLeft;
      paramRect3.left = i;
      paramRect2.left = i;
      paramRect1.left = i;
      i = this.mUnrestrictedScreenTop;
      paramRect3.top = i;
      paramRect2.top = i;
      paramRect1.top = i;
      i = this.mUnrestrictedScreenWidth + this.mUnrestrictedScreenLeft;
      paramRect3.right = i;
      paramRect2.right = i;
      paramRect1.right = i;
      i = this.mUnrestrictedScreenHeight + this.mUnrestrictedScreenTop;
      paramRect3.bottom = i;
      paramRect2.bottom = i;
      paramRect1.bottom = i;
      paramRect4.left = this.mStableLeft;
      paramRect4.top = this.mStableTop;
      paramRect4.right = this.mStableRight;
      paramRect4.bottom = this.mStableBottom;
      this.mStatusBarLayer = this.mStatusBar.getSurfaceLayer();
      this.mStatusBar.computeFrameLw(paramRect1, paramRect2, paramRect4, paramRect4, paramRect4, paramRect5, paramRect4, paramRect4);
      this.mStableTop = (this.mUnrestrictedScreenTop + this.mStatusBarHeight);
      label222:
      int j;
      if ((0x4000000 & paramInt) != 0)
      {
        i = 1;
        if ((0x40000008 & paramInt) == 0) {
          break label299;
        }
        paramInt = 1;
        j = paramInt;
        if (!paramBoolean) {
          j = paramInt & areTranslucentBarsAllowed();
        }
        if ((this.mStatusBar.isVisibleLw()) && (i == 0)) {
          break label305;
        }
        label257:
        if ((this.mStatusBar.isVisibleLw()) && (!this.mStatusBar.isAnimatingLw())) {
          break label585;
        }
      }
      for (;;)
      {
        if (!this.mStatusBarController.checkHiddenLw()) {
          break label621;
        }
        return true;
        i = 0;
        break;
        label299:
        paramInt = 0;
        break label222;
        label305:
        this.mDockTop = (this.mUnrestrictedScreenTop + this.mStatusBarHeight);
        paramInt = this.mDockTop;
        this.mCurTop = paramInt;
        this.mVoiceContentTop = paramInt;
        this.mContentTop = paramInt;
        paramInt = this.mDockBottom;
        this.mCurBottom = paramInt;
        this.mVoiceContentBottom = paramInt;
        this.mContentBottom = paramInt;
        paramInt = this.mDockLeft;
        this.mCurLeft = paramInt;
        this.mVoiceContentLeft = paramInt;
        this.mContentLeft = paramInt;
        paramInt = this.mDockRight;
        this.mCurRight = paramInt;
        this.mVoiceContentRight = paramInt;
        this.mContentRight = paramInt;
        if (!DEBUG_LAYOUT) {
          break label257;
        }
        Slog.v("WindowManager", "Status bar: " + String.format("dock=[%d,%d][%d,%d] content=[%d,%d][%d,%d] cur=[%d,%d][%d,%d]", new Object[] { Integer.valueOf(this.mDockLeft), Integer.valueOf(this.mDockTop), Integer.valueOf(this.mDockRight), Integer.valueOf(this.mDockBottom), Integer.valueOf(this.mContentLeft), Integer.valueOf(this.mContentTop), Integer.valueOf(this.mContentRight), Integer.valueOf(this.mContentBottom), Integer.valueOf(this.mCurLeft), Integer.valueOf(this.mCurTop), Integer.valueOf(this.mCurRight), Integer.valueOf(this.mCurBottom) }));
        break label257;
        label585:
        if ((i == 0) && (j == 0) && (!this.mStatusBarController.wasRecentlyTranslucent())) {
          this.mSystemTop = (this.mUnrestrictedScreenTop + this.mStatusBarHeight);
        }
      }
    }
    label621:
    return false;
  }
  
  private void layoutWallpaper(WindowManagerPolicy.WindowState paramWindowState, Rect paramRect1, Rect paramRect2, Rect paramRect3, Rect paramRect4)
  {
    int i = this.mOverscanScreenLeft;
    paramRect2.left = i;
    paramRect1.left = i;
    i = this.mOverscanScreenTop;
    paramRect2.top = i;
    paramRect1.top = i;
    i = this.mOverscanScreenLeft + this.mOverscanScreenWidth;
    paramRect2.right = i;
    paramRect1.right = i;
    i = this.mOverscanScreenTop + this.mOverscanScreenHeight;
    paramRect2.bottom = i;
    paramRect1.bottom = i;
    i = this.mUnrestrictedScreenLeft;
    paramRect4.left = i;
    paramRect3.left = i;
    i = this.mUnrestrictedScreenTop;
    paramRect4.top = i;
    paramRect3.top = i;
    i = this.mUnrestrictedScreenLeft + this.mUnrestrictedScreenWidth;
    paramRect4.right = i;
    paramRect3.right = i;
    i = this.mUnrestrictedScreenTop + this.mUnrestrictedScreenHeight;
    paramRect4.bottom = i;
    paramRect3.bottom = i;
  }
  
  private int navigationBarPosition(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((this.mNavigationBarCanMove) && (paramInt1 > paramInt2))
    {
      if (paramInt3 == 3) {
        return 2;
      }
      return 1;
    }
    return 0;
  }
  
  private void notifyScreenshotError()
  {
    ComponentName localComponentName = new ComponentName("com.android.systemui", "com.android.systemui.screenshot.ScreenshotServiceErrorReceiver");
    Intent localIntent = new Intent("android.intent.action.USER_PRESENT");
    localIntent.setComponent(localComponentName);
    localIntent.addFlags(335544320);
    this.mContext.sendBroadcastAsUser(localIntent, UserHandle.CURRENT);
  }
  
  private boolean oemIsActionPassToUser()
  {
    String str2 = "";
    String str1;
    if (this.mFocusedWindow != null)
    {
      WindowManager.LayoutParams localLayoutParams = this.mFocusedWindow.getAttrs();
      str1 = str2;
      if (localLayoutParams != null) {
        str1 = localLayoutParams.packageName;
      }
    }
    while (OEM_PACKAGE_LIST.contains(str1))
    {
      if (DEBUG_ONEPLUS) {
        Slog.d("WindowManager", "oemIsActionPassToUser: detect power key in deskclock, pass to application");
      }
      return true;
      str1 = str2;
      if (DEBUG_ONEPLUS)
      {
        Slog.d("WindowManager", "oemIsActionPassToUser: No focused window");
        str1 = str2;
      }
    }
    return false;
  }
  
  private void offsetInputMethodWindowLw(WindowManagerPolicy.WindowState paramWindowState)
  {
    int i = Math.max(paramWindowState.getDisplayFrameLw().top, paramWindowState.getContentFrameLw().top) + paramWindowState.getGivenContentInsetsLw().top;
    if (this.mContentBottom > i) {
      this.mContentBottom = i;
    }
    if (this.mVoiceContentBottom > i) {
      this.mVoiceContentBottom = i;
    }
    i = paramWindowState.getVisibleFrameLw().top + paramWindowState.getGivenVisibleInsetsLw().top;
    if (this.mCurBottom > i) {
      this.mCurBottom = i;
    }
    if (DEBUG_LAYOUT) {
      Slog.v("WindowManager", "Input method: mDockBottom=" + this.mDockBottom + " mContentBottom=" + this.mContentBottom + " mCurBottom=" + this.mCurBottom);
    }
  }
  
  private void offsetVoiceInputWindowLw(WindowManagerPolicy.WindowState paramWindowState)
  {
    int i = Math.max(paramWindowState.getDisplayFrameLw().top, paramWindowState.getContentFrameLw().top) + paramWindowState.getGivenContentInsetsLw().top;
    if (this.mVoiceContentBottom > i) {
      this.mVoiceContentBottom = i;
    }
  }
  
  private void onKeyguardShowingStateChanged(boolean paramBoolean)
  {
    if (!paramBoolean) {}
    synchronized (this.mWindowManagerFuncs.getWindowManagerLock())
    {
      this.mCurrentlyDismissingKeyguard = false;
      return;
    }
  }
  
  private void performAuditoryFeedbackForAccessibilityIfNeed()
  {
    if (!isGlobalAccessibilityGestureEnabled()) {
      return;
    }
    if (((AudioManager)this.mContext.getSystemService("audio")).isSilentMode()) {
      return;
    }
    Ringtone localRingtone = RingtoneManager.getRingtone(this.mContext, Settings.System.DEFAULT_NOTIFICATION_URI);
    if (localRingtone != null)
    {
      localRingtone.setStreamType(3);
      localRingtone.play();
    }
  }
  
  private void powerLongPress()
  {
    boolean bool = true;
    int i = getResolvedLongPressOnPowerBehavior();
    switch (i)
    {
    case 0: 
    default: 
      return;
    case 1: 
      this.mPowerKeyHandled = true;
      if (!performHapticFeedbackLw(null, 0, false)) {
        performAuditoryFeedbackForAccessibilityIfNeed();
      }
      showGlobalActionsInternal();
      return;
    }
    this.mPowerKeyHandled = true;
    performHapticFeedbackLw(null, 0, false);
    sendCloseSystemWindows("globalactions");
    WindowManagerPolicy.WindowManagerFuncs localWindowManagerFuncs = this.mWindowManagerFuncs;
    if (i == 2) {}
    for (;;)
    {
      localWindowManagerFuncs.shutdown(bool);
      return;
      bool = false;
    }
  }
  
  private void powerMultiPressAction(long paramLong, boolean paramBoolean, int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
    default: 
    case 1: 
      do
      {
        do
        {
          return;
          if (!isUserSetupComplete())
          {
            Slog.i("WindowManager", "Ignoring toggling theater mode - device not setup.");
            return;
          }
          if (!isTheaterModeEnabled()) {
            break;
          }
          Slog.i("WindowManager", "Toggling theater mode off.");
          Settings.Global.putInt(this.mContext.getContentResolver(), "theater_mode_on", 0);
        } while (paramBoolean);
        wakeUpFromPowerKey(paramLong);
        return;
        Slog.i("WindowManager", "Toggling theater mode on.");
        Settings.Global.putInt(this.mContext.getContentResolver(), "theater_mode_on", 1);
      } while ((!this.mGoToSleepOnButtonPressTheaterMode) || (!paramBoolean));
      this.mPowerManager.goToSleep(paramLong, 4, 0);
      return;
    }
    Slog.i("WindowManager", "Starting brightness boost.");
    if (!paramBoolean) {
      wakeUpFromPowerKey(paramLong);
    }
    this.mPowerManager.boostScreenBrightness(paramLong);
  }
  
  private void powerPress(long paramLong, boolean paramBoolean, int paramInt)
  {
    if ((!this.mScreenOnEarly) || (this.mScreenOnFully))
    {
      if (paramInt != 2) {
        break label41;
      }
      powerMultiPressAction(paramLong, paramBoolean, this.mDoublePressOnPowerBehavior);
    }
    label41:
    do
    {
      return;
      Slog.i("WindowManager", "Suppressed redundant power key press while already in the process of turning the screen on.");
      return;
      if (paramInt == 3)
      {
        powerMultiPressAction(paramLong, paramBoolean, this.mTriplePressOnPowerBehavior);
        return;
      }
    } while ((!paramBoolean) || (this.mBeganFromNonInteractive));
    switch (this.mShortPressOnPowerBehavior)
    {
    case 0: 
    default: 
      return;
    case 1: 
      this.mPowerManager.goToSleep(paramLong, 4, 0);
      return;
    case 2: 
      this.mPowerManager.goToSleep(paramLong, 4, 1);
      return;
    case 3: 
      this.mPowerManager.goToSleep(paramLong, 4, 1);
      launchHomeFromHotKey();
      return;
    }
    launchHomeFromHotKey(true, false);
  }
  
  private void readCameraLensCoverState()
  {
    this.mCameraLensCoverState = this.mWindowManagerFuncs.getCameraLensCoverState();
  }
  
  private void readConfigurationDependentBehaviors()
  {
    Resources localResources = this.mContext.getResources();
    this.mLongPressOnHomeBehavior = localResources.getInteger(17694818);
    if ((this.mLongPressOnHomeBehavior < 0) || (this.mLongPressOnHomeBehavior > 2)) {
      this.mLongPressOnHomeBehavior = 0;
    }
    this.mDoubleTapOnHomeBehavior = localResources.getInteger(17694819);
    if ((this.mDoubleTapOnHomeBehavior < 0) || (this.mDoubleTapOnHomeBehavior > 1)) {
      this.mDoubleTapOnHomeBehavior = 0;
    }
    this.mShortPressWindowBehavior = 0;
    if (this.mContext.getPackageManager().hasSystemFeature("android.software.picture_in_picture")) {
      this.mShortPressWindowBehavior = 1;
    }
    this.mNavBarOpacityMode = localResources.getInteger(17694885);
  }
  
  private int readRotation(int paramInt)
  {
    try
    {
      paramInt = this.mContext.getResources().getInteger(paramInt);
      switch (paramInt)
      {
      }
    }
    catch (Resources.NotFoundException localNotFoundException)
    {
      for (;;) {}
    }
    return -1;
    return 0;
    return 1;
    return 2;
    return 3;
  }
  
  private void requestTransientBars(WindowManagerPolicy.WindowState paramWindowState)
  {
    for (;;)
    {
      boolean bool;
      int i;
      synchronized (this.mWindowManagerFuncs.getWindowManagerLock())
      {
        bool = isUserSetupComplete();
        if (!bool) {
          return;
        }
        bool = this.mStatusBarController.checkShowTransientBarLw();
        if (this.mNavigationBarController.checkShowTransientBarLw())
        {
          if (isNavBarEmpty(this.mLastSystemUiFlags))
          {
            i = 0;
            break label145;
            if ((i == 0) && (paramWindowState == this.mNavigationBar)) {
              if (DEBUG) {
                Slog.d("WindowManager", "Not showing transient bar, wrong swipe target");
              }
            }
          }
          else
          {
            i = 1;
            break label145;
          }
        }
        else
        {
          i = 0;
          break label145;
        }
        if (bool) {
          this.mStatusBarController.showTransient();
        }
        if (i != 0) {
          this.mNavigationBarController.showTransient();
        }
        this.mImmersiveModeConfirmation.confirmCurrentPrompt();
        updateSystemUiVisibilityLw();
        return;
      }
      label145:
      if (!bool) {
        if (i == 0) {}
      }
    }
  }
  
  private int selectDockedDividerAnimationLw(WindowManagerPolicy.WindowState paramWindowState, int paramInt)
  {
    int m = this.mWindowManagerFuncs.getDockedDividerInsetsLw();
    Rect localRect = paramWindowState.getFrameLw();
    int i;
    label58:
    int j;
    label74:
    int k;
    if (this.mNavigationBar != null) {
      if ((this.mNavigationBarPosition == 0) && (localRect.top + m >= this.mNavigationBar.getFrameLw().top))
      {
        i = 1;
        if (localRect.height() <= localRect.width()) {
          break label250;
        }
        j = 1;
        if (j == 0) {
          break label268;
        }
        if (localRect.right - m <= 0) {
          break label256;
        }
        if (localRect.left + m < paramWindowState.getDisplayFrameLw().right) {
          break label262;
        }
        k = 1;
        label113:
        if (j != 0) {
          break label286;
        }
        if (localRect.top - m <= 0) {
          break label274;
        }
        if (localRect.bottom + m < paramWindowState.getDisplayFrameLw().bottom) {
          break label280;
        }
        j = 1;
        label152:
        if (k != 0) {
          break label292;
        }
      }
    }
    for (;;)
    {
      if ((i == 0) && (j == 0)) {
        break label298;
      }
      return 0;
      if ((this.mNavigationBarPosition == 1) && (localRect.left + m >= this.mNavigationBar.getFrameLw().left)) {
        break;
      }
      if (this.mNavigationBarPosition == 2)
      {
        if (localRect.right - m <= this.mNavigationBar.getFrameLw().right)
        {
          i = 1;
          break label58;
        }
        i = 0;
        break label58;
      }
      i = 0;
      break label58;
      i = 0;
      break label58;
      label250:
      j = 0;
      break label74;
      label256:
      k = 1;
      break label113;
      label262:
      k = 0;
      break label113;
      label268:
      k = 0;
      break label113;
      label274:
      j = 1;
      break label152;
      label280:
      j = 0;
      break label152;
      label286:
      j = 0;
      break label152;
      label292:
      j = 1;
    }
    label298:
    if ((paramInt == 1) || (paramInt == 3)) {
      return 17432576;
    }
    if (paramInt == 2) {
      return 17432577;
    }
    return 0;
  }
  
  private int setNavBarOpaqueFlag(int paramInt)
  {
    return paramInt & 0x7FFF7FFF;
  }
  
  private int setNavBarTranslucentFlag(int paramInt)
  {
    return paramInt & 0xFFFF7FFF | 0x80000000;
  }
  
  private boolean shouldDispatchInputWhenNonInteractive(KeyEvent paramKeyEvent)
  {
    if ((this.mDisplay == null) || (this.mDisplay.getState() == 1)) {}
    for (int i = 1; (i == 0) || (this.mHasFeatureWatch); i = 0)
    {
      if ((isKeyguardShowingAndNotOccluded()) && (i == 0)) {
        break label80;
      }
      if ((!this.mHasFeatureWatch) || (paramKeyEvent == null) || ((paramKeyEvent.getKeyCode() != 4) && (paramKeyEvent.getKeyCode() != 264))) {
        break label82;
      }
      return false;
    }
    return false;
    label80:
    return true;
    label82:
    paramKeyEvent = getDreamManager();
    if (paramKeyEvent != null) {
      try
      {
        boolean bool = paramKeyEvent.isDreaming();
        if (bool) {
          return true;
        }
      }
      catch (RemoteException paramKeyEvent)
      {
        Slog.e("WindowManager", "RemoteException when checking if dreaming", paramKeyEvent);
      }
    }
    return false;
  }
  
  private boolean shouldEnableWakeGestureLp()
  {
    if ((!this.mWakeGestureEnabledSetting) || (this.mAwake)) {}
    while ((this.mLidControlsSleep) && (this.mLidState == 0)) {
      return false;
    }
    return this.mWakeGestureListener.isSupported();
  }
  
  private boolean shouldUseOutsets(WindowManager.LayoutParams paramLayoutParams, int paramInt)
  {
    return (paramLayoutParams.type == 2013) || ((0x2000400 & paramInt) != 0);
  }
  
  private void showRecentApps(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mPreloadedRecentApps = false;
    StatusBarManagerInternal localStatusBarManagerInternal = getStatusBarManagerInternal();
    if (localStatusBarManagerInternal != null) {
      localStatusBarManagerInternal.showRecentApps(paramBoolean1, paramBoolean2);
    }
  }
  
  private void showTvPictureInPictureMenu(KeyEvent paramKeyEvent)
  {
    if (DEBUG_INPUT) {
      Log.d("WindowManager", "showTvPictureInPictureMenu event=" + paramKeyEvent);
    }
    this.mHandler.removeMessages(17);
    paramKeyEvent = this.mHandler.obtainMessage(17);
    paramKeyEvent.setAsynchronous(true);
    paramKeyEvent.sendToTarget();
  }
  
  private void showTvPictureInPictureMenuInternal()
  {
    StatusBarManagerInternal localStatusBarManagerInternal = getStatusBarManagerInternal();
    if (localStatusBarManagerInternal != null) {
      localStatusBarManagerInternal.showTvPictureInPictureMenu();
    }
  }
  
  private void sleepPress(long paramLong)
  {
    if (this.mShortPressOnSleepBehavior == 1) {
      launchHomeFromHotKey(false, true);
    }
  }
  
  private void sleepRelease(long paramLong)
  {
    switch (this.mShortPressOnSleepBehavior)
    {
    default: 
      return;
    }
    Slog.i("WindowManager", "sleepRelease() calling goToSleep(GO_TO_SLEEP_REASON_SLEEP_BUTTON)");
    this.mPowerManager.goToSleep(paramLong, 6, 0);
  }
  
  private void stopLongshot()
  {
    ILongScreenshotManager localILongScreenshotManager = ILongScreenshotManager.Stub.asInterface(ServiceManager.getService("longshot"));
    if (localILongScreenshotManager != null) {}
    try
    {
      if (localILongScreenshotManager.isLongshotMode()) {
        localILongScreenshotManager.stopLongshot();
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.d("WindowManager", localRemoteException.toString());
    }
  }
  
  private void toggleKeyboardShortcutsMenu(int paramInt)
  {
    StatusBarManagerInternal localStatusBarManagerInternal = getStatusBarManagerInternal();
    if (localStatusBarManagerInternal != null) {
      localStatusBarManagerInternal.toggleKeyboardShortcutsMenu(paramInt);
    }
  }
  
  private void updateDreamingSleepToken(boolean paramBoolean)
  {
    if (paramBoolean) {
      if (this.mDreamingSleepToken == null) {
        this.mDreamingSleepToken = this.mActivityManagerInternal.acquireSleepToken("Dream");
      }
    }
    while (this.mDreamingSleepToken == null) {
      return;
    }
    this.mDreamingSleepToken.release();
    this.mDreamingSleepToken = null;
  }
  
  private int updateLightStatusBarLw(int paramInt, WindowManagerPolicy.WindowState paramWindowState1, WindowManagerPolicy.WindowState paramWindowState2)
  {
    int i;
    if ((!isStatusBarKeyguard()) || (this.mHideLockScreen))
    {
      i = paramInt;
      if (paramWindowState2 != null)
      {
        if (paramWindowState2 != paramWindowState1) {
          break label54;
        }
        i = paramInt & 0xDFFF | PolicyControl.getSystemUiVisibility(paramWindowState2, null) & 0x2000;
      }
    }
    label54:
    do
    {
      do
      {
        return i;
        paramWindowState2 = this.mStatusBar;
        break;
        i = paramInt;
      } while (paramWindowState2 == null);
      i = paramInt;
    } while (!paramWindowState2.isDimming());
    return paramInt & 0xDFFF;
  }
  
  private void updateLockScreenTimeout()
  {
    synchronized (this.mScreenLockTimeout)
    {
      if ((this.mAllowLockscreenWhenOn) && (this.mAwake) && (this.mKeyguardDelegate != null)) {}
      for (boolean bool = this.mKeyguardDelegate.isSecure(this.mCurrentUserId);; bool = false)
      {
        if (this.mLockScreenTimerActive != bool)
        {
          if (!bool) {
            break;
          }
          if (localLOGV) {
            Log.v("WindowManager", "setting lockscreen timer");
          }
          this.mHandler.removeCallbacks(this.mScreenLockTimeout);
          this.mHandler.postDelayed(this.mScreenLockTimeout, this.mLockScreenTimeout);
          this.mLockScreenTimerActive = bool;
        }
        return;
      }
      if (localLOGV) {
        Log.v("WindowManager", "clearing lockscreen timer");
      }
      this.mHandler.removeCallbacks(this.mScreenLockTimeout);
    }
  }
  
  private void updateScreenOffSleepToken(boolean paramBoolean)
  {
    if (paramBoolean) {
      if (this.mScreenOffSleepToken == null) {
        this.mScreenOffSleepToken = this.mActivityManagerInternal.acquireSleepToken("ScreenOff");
      }
    }
    while (this.mScreenOffSleepToken == null) {
      return;
    }
    this.mScreenOffSleepToken.release();
    this.mScreenOffSleepToken = null;
  }
  
  private int updateSystemBarsLw(WindowManagerPolicy.WindowState paramWindowState, int paramInt1, int paramInt2)
  {
    boolean bool4 = this.mWindowManagerInternal.isStackVisible(3);
    boolean bool5 = this.mWindowManagerInternal.isStackVisible(2);
    boolean bool3 = this.mWindowManagerInternal.isDockedDividerResizing();
    boolean bool1;
    int j;
    label66:
    Object localObject;
    label86:
    int i;
    label148:
    boolean bool2;
    label170:
    int k;
    label188:
    label206:
    int m;
    if ((!bool4) && (!bool5))
    {
      bool1 = bool3;
      this.mForceShowSystemBars = bool1;
      if ((this.mForceShowSystemBars) && (!this.mForceStatusBarFromKeyguard)) {
        break label588;
      }
      j = 0;
      if ((isStatusBarKeyguard()) && (!this.mHideLockScreen)) {
        break label594;
      }
      localObject = this.mTopFullscreenOpaqueWindowState;
      paramInt2 = this.mStatusBarController.applyTranslucentFlagLw((WindowManagerPolicy.WindowState)localObject, paramInt2, paramInt1);
      i = this.mNavigationBarController.applyTranslucentFlagLw((WindowManagerPolicy.WindowState)localObject, paramInt2, paramInt1);
      paramInt2 = this.mStatusBarController.applyTranslucentFlagLw(this.mTopDockedOpaqueWindowState, 0, 0);
      if ((!drawsSystemBarBackground(this.mTopFullscreenOpaqueWindowState)) || ((0x40000000 & i) != 0)) {
        break label603;
      }
      bool1 = true;
      if ((!drawsSystemBarBackground(this.mTopDockedOpaqueWindowState)) || ((0x40000000 & paramInt2) != 0)) {
        break label616;
      }
      bool2 = true;
      if (paramWindowState.getAttrs().type != 2000) {
        break label629;
      }
      k = 1;
      paramInt2 = i;
      if (k != 0)
      {
        if (!isStatusBarKeyguard()) {
          break label635;
        }
        paramInt2 = i;
      }
      if ((!bool1) || (!bool2)) {
        break label662;
      }
      i = (paramInt2 | 0x8) & 0xBFFFFFFF;
      m = configureNavBarOpacity(i, bool4, bool5, bool3);
      if ((m & 0x1000) == 0) {
        break label699;
      }
      bool2 = true;
      label252:
      if (this.mTopFullscreenOpaqueWindowState == null) {
        break label711;
      }
      if ((PolicyControl.getWindowFlags(this.mTopFullscreenOpaqueWindowState, null) & 0x400) == 0) {
        break label705;
      }
      i = 1;
      label277:
      if ((m & 0x4) == 0) {
        break label717;
      }
      paramInt2 = 1;
      label286:
      if ((m & 0x2) == 0) {
        break label722;
      }
      j = 1;
      label296:
      if (this.mStatusBar == null) {
        break label752;
      }
      if (k != 0) {
        break label728;
      }
      if (this.mForceShowSystemBars) {
        break label746;
      }
      if (i != 0) {
        break label734;
      }
      if (paramInt2 == 0) {
        break label740;
      }
      bool1 = bool2;
      label328:
      if ((this.mNavigationBar != null) && (!this.mForceShowSystemBars)) {
        break label758;
      }
      label342:
      bool2 = false;
      label345:
      long l = SystemClock.uptimeMillis();
      if (this.mPendingPanicGestureUptime == 0L) {
        break label772;
      }
      if (l - this.mPendingPanicGestureUptime > 30000L) {
        break label766;
      }
      i = 1;
      label376:
      if ((i != 0) && (j != 0) && (!isStatusBarKeyguard())) {
        break label778;
      }
      label393:
      if ((this.mStatusBarController.isTransientShowRequested()) && (!bool1)) {
        break label815;
      }
      i = 0;
      label411:
      if (!this.mNavigationBarController.isTransientShowRequested()) {
        break label826;
      }
      if (!bool2) {
        break label821;
      }
      paramInt2 = 0;
      label428:
      if ((i == 0) && (paramInt2 == 0))
      {
        paramInt2 = m;
        if (!this.mForceShowSystemBars) {}
      }
      else
      {
        clearClearableFlagsLw();
        paramInt2 = m & 0xFFFFFFF8;
      }
      if ((paramInt2 & 0x800) == 0) {
        break label831;
      }
      k = 1;
      label468:
      if ((paramInt2 & 0x1000) == 0) {
        break label837;
      }
      i = 1;
      label479:
      if (k != 0) {
        break label843;
      }
      label484:
      k = paramInt2;
      if (j != 0)
      {
        if (i == 0) {
          break label849;
        }
        k = paramInt2;
      }
    }
    for (;;)
    {
      paramInt2 = this.mStatusBarController.updateVisibilityLw(bool1, paramInt1, k);
      bool1 = isImmersiveMode(paramInt1);
      bool3 = isImmersiveMode(paramInt2);
      if ((paramWindowState != null) && (bool1 != bool3))
      {
        localObject = paramWindowState.getOwningPackage();
        this.mImmersiveModeConfirmation.immersiveModeChangedLw((String)localObject, bool3, isUserSetupComplete(), isNavBarEmpty(paramWindowState.getSystemUiVisibility()));
      }
      return this.mNavigationBarController.updateVisibilityLw(bool2, paramInt1, paramInt2);
      bool1 = true;
      break;
      label588:
      j = 1;
      break label66;
      label594:
      localObject = this.mStatusBar;
      break label86;
      label603:
      bool1 = forcesDrawStatusBarBackground(this.mTopFullscreenOpaqueWindowState);
      break label148;
      label616:
      bool2 = forcesDrawStatusBarBackground(this.mTopDockedOpaqueWindowState);
      break label170;
      label629:
      k = 0;
      break label188;
      label635:
      paramInt2 = 14342;
      if (this.mHideLockScreen) {
        paramInt2 = -1073727482;
      }
      paramInt2 = paramInt2 & i | paramInt1 & paramInt2;
      break label206;
      label662:
      if ((!areTranslucentBarsAllowed()) && (localObject != this.mStatusBar)) {}
      for (;;)
      {
        i = paramInt2 & 0xBFFFFFF7;
        break;
        i = paramInt2;
        if (j == 0) {
          break;
        }
      }
      label699:
      bool2 = false;
      break label252;
      label705:
      i = 0;
      break label277;
      label711:
      i = 0;
      break label277;
      label717:
      paramInt2 = 0;
      break label286;
      label722:
      j = 0;
      break label296;
      label728:
      bool1 = true;
      break label328;
      label734:
      bool1 = true;
      break label328;
      label740:
      bool1 = false;
      break label328;
      label746:
      bool1 = false;
      break label328;
      label752:
      bool1 = false;
      break label328;
      label758:
      if (j == 0) {
        break label342;
      }
      break label345;
      label766:
      i = 0;
      break label376;
      label772:
      i = 0;
      break label376;
      label778:
      if (!this.mKeyguardDrawComplete) {
        break label393;
      }
      this.mPendingPanicGestureUptime = 0L;
      this.mStatusBarController.showTransient();
      if (isNavBarEmpty(m)) {
        break label393;
      }
      this.mNavigationBarController.showTransient();
      break label393;
      label815:
      i = paramInt2;
      break label411;
      label821:
      paramInt2 = 1;
      break label428;
      label826:
      paramInt2 = 0;
      break label428;
      label831:
      k = 0;
      break label468;
      label837:
      i = 0;
      break label479;
      label843:
      i = 1;
      break label484;
      label849:
      k = paramInt2;
      if (windowTypeToLayerLw(paramWindowState.getBaseType()) > windowTypeToLayerLw(2022)) {
        k = paramInt2 & 0xFFFFFFFD;
      }
    }
  }
  
  private int updateSystemUiVisibilityLw()
  {
    if (this.mFocusedWindow != null) {}
    for (final Object localObject1 = this.mFocusedWindow; localObject1 == null; localObject1 = this.mTopFullscreenOpaqueWindowState) {
      return 0;
    }
    final Object localObject2 = localObject1;
    if (((WindowManagerPolicy.WindowState)localObject1).getAttrs().token == this.mImmersiveModeConfirmation.getWindowToken())
    {
      if (isStatusBarKeyguard()) {}
      for (localObject1 = this.mStatusBar;; localObject1 = this.mTopFullscreenOpaqueWindowState)
      {
        localObject2 = localObject1;
        if (localObject1 != null) {
          break;
        }
        return 0;
      }
    }
    if (((((WindowManagerPolicy.WindowState)localObject2).getAttrs().privateFlags & 0x400) != 0) && (this.mHideLockScreen)) {
      return 0;
    }
    final int j = PolicyControl.getSystemUiVisibility((WindowManagerPolicy.WindowState)localObject2, null) & this.mResettingSystemUiFlags & this.mForceClearedSystemUiFlags;
    final int i = j;
    if (this.mForcingShowNavBar)
    {
      i = j;
      if (((WindowManagerPolicy.WindowState)localObject2).getSurfaceLayer() < this.mForcingShowNavBarLayer) {
        i = j & PolicyControl.adjustClearableFlags((WindowManagerPolicy.WindowState)localObject2, 7);
      }
    }
    j = updateLightStatusBarLw(0, this.mTopFullscreenOpaqueWindowState, this.mTopFullscreenOpaqueOrDimmingWindowState);
    final int k = updateLightStatusBarLw(0, this.mTopDockedOpaqueWindowState, this.mTopDockedOpaqueOrDimmingWindowState);
    this.mWindowManagerFuncs.getStackBounds(0, this.mNonDockedStackBounds);
    this.mWindowManagerFuncs.getStackBounds(3, this.mDockedStackBounds);
    i = updateSystemBarsLw((WindowManagerPolicy.WindowState)localObject2, this.mLastSystemUiFlags, i);
    int m = i ^ this.mLastSystemUiFlags;
    int n = this.mLastFullscreenStackSysUiFlags;
    int i1 = this.mLastDockedStackSysUiFlags;
    final boolean bool = ((WindowManagerPolicy.WindowState)localObject2).getNeedsMenuLw(this.mTopFullscreenOpaqueWindowState);
    if ((m == 0) && ((j ^ n) == 0) && ((k ^ i1) == 0) && (this.mLastFocusNeedsMenu == bool) && (this.mFocusedApp == ((WindowManagerPolicy.WindowState)localObject2).getAppToken()) && (this.mLastNonDockedStackBounds.equals(this.mNonDockedStackBounds)) && (this.mLastDockedStackBounds.equals(this.mDockedStackBounds))) {
      return 0;
    }
    this.mLastSystemUiFlags = i;
    this.mLastFullscreenStackSysUiFlags = j;
    this.mLastDockedStackSysUiFlags = k;
    this.mLastFocusNeedsMenu = bool;
    this.mFocusedApp = ((WindowManagerPolicy.WindowState)localObject2).getAppToken();
    localObject1 = new Rect(this.mNonDockedStackBounds);
    final Rect localRect = new Rect(this.mDockedStackBounds);
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        StatusBarManagerInternal localStatusBarManagerInternal = PhoneWindowManager.this.getStatusBarManagerInternal();
        if (localStatusBarManagerInternal != null)
        {
          localStatusBarManagerInternal.setSystemUiVisibility(i, j, k, -1, localObject1, localRect, localObject2.toString());
          localStatusBarManagerInternal.topAppWindowChanged(bool);
        }
      }
    });
    return m;
  }
  
  private void updateWakeGestureListenerLp()
  {
    if (shouldEnableWakeGestureLp())
    {
      this.mWakeGestureListener.requestWakeUpTrigger();
      return;
    }
    this.mWakeGestureListener.cancelWakeUpTrigger();
  }
  
  private boolean wakeUp(long paramLong, boolean paramBoolean, String paramString)
  {
    boolean bool = isTheaterModeEnabled();
    if ((!paramBoolean) && (bool)) {
      return false;
    }
    if (bool) {
      Settings.Global.putInt(this.mContext.getContentResolver(), "theater_mode_on", 0);
    }
    this.mPowerManager.wakeUp(paramLong, paramString);
    return true;
  }
  
  private void wakeUpFromPowerKey(long paramLong)
  {
    wakeUp(paramLong, this.mAllowTheaterModeWakeFromPowerKey, "android.policy:POWER");
  }
  
  public View addStartingWindow(IBinder paramIBinder, String paramString, int paramInt1, CompatibilityInfo paramCompatibilityInfo, CharSequence paramCharSequence, int paramInt2, int paramInt3, int paramInt4, int paramInt5, Configuration paramConfiguration)
  {
    if (!SHOW_STARTING_ANIMATIONS) {
      return null;
    }
    if (paramString == null) {
      return null;
    }
    Object localObject13 = null;
    Object localObject14 = null;
    Object localObject12 = null;
    Object localObject10 = null;
    Object localObject11 = null;
    Object localObject9 = null;
    Object localObject4 = localObject9;
    Object localObject5 = localObject12;
    Object localObject6 = localObject10;
    Object localObject7 = localObject13;
    Object localObject1 = localObject11;
    Object localObject2 = localObject14;
    try
    {
      localObject8 = this.mContext;
      localObject4 = localObject9;
      localObject5 = localObject12;
      localObject6 = localObject10;
      localObject7 = localObject13;
      localObject1 = localObject11;
      localObject2 = localObject14;
      if (!DEBUG_STARTING_WINDOW)
      {
        localObject4 = localObject9;
        localObject5 = localObject12;
        localObject6 = localObject10;
        localObject7 = localObject13;
        localObject1 = localObject11;
        localObject2 = localObject14;
        if (!DEBUG_ONEPLUS) {}
      }
      else
      {
        localObject4 = localObject9;
        localObject5 = localObject12;
        localObject6 = localObject10;
        localObject7 = localObject13;
        localObject1 = localObject11;
        localObject2 = localObject14;
        Slog.d("WindowManager", "addStartingWindow " + paramString + ": nonLocalizedLabel=" + paramCharSequence + " theme=" + Integer.toHexString(paramInt1));
      }
      localObject4 = localObject9;
      localObject5 = localObject12;
      localObject6 = localObject10;
      localObject7 = localObject13;
      localObject1 = localObject11;
      localObject2 = localObject14;
      int i = ((Context)localObject8).getThemeResId();
      if (paramInt1 == i)
      {
        localObject3 = localObject8;
        if (paramInt2 == 0) {}
      }
      else
      {
        localObject4 = localObject9;
        localObject5 = localObject12;
        localObject6 = localObject10;
        localObject7 = localObject13;
        localObject1 = localObject11;
        localObject2 = localObject14;
        localObject3 = localObject8;
      }
    }
    catch (WindowManager.BadTokenException paramString)
    {
      localObject1 = localObject4;
      localObject2 = localObject5;
      Log.w("WindowManager", paramIBinder + " already running, starting window not displayed. " + paramString.getMessage());
      return null;
    }
    catch (RuntimeException paramString)
    {
      for (;;)
      {
        Object localObject3;
        label317:
        label689:
        localObject1 = localObject6;
        localObject2 = localObject7;
        Log.w("WindowManager", paramIBinder + " failed creating starting window", paramString);
        if ((localObject6 != null) && (((View)localObject6).getParent() == null))
        {
          Log.w("WindowManager", "view not successfully added to wm, removing view");
          ((WindowManager)localObject7).removeViewImmediate((View)localObject6);
          continue;
          localObject4 = localObject9;
          localObject5 = paramCharSequence;
          localObject6 = localObject10;
          localObject7 = paramCharSequence;
          localObject1 = localObject11;
          localObject2 = paramCharSequence;
          ((PhoneWindow)localObject3).setStartupWindowTheme(false, 0);
        }
      }
    }
    finally
    {
      if ((localObject1 != null) && (((View)localObject1).getParent() == null))
      {
        Log.w("WindowManager", "view not successfully added to wm, removing view");
        ((WindowManager)localObject2).removeViewImmediate((View)localObject1);
      }
    }
    try
    {
      localObject8 = ((Context)localObject8).createPackageContext(paramString, 0);
      localObject4 = localObject9;
      localObject5 = localObject12;
      localObject6 = localObject10;
      localObject7 = localObject13;
      localObject1 = localObject11;
      localObject2 = localObject14;
      localObject3 = localObject8;
      ((Context)localObject8).setTheme(paramInt1);
      localObject3 = localObject8;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      break label317;
      localObject8 = localContext;
      break label689;
    }
    localObject8 = localObject3;
    if (paramConfiguration != null)
    {
      localObject8 = localObject3;
      localObject4 = localObject9;
      localObject5 = localObject12;
      localObject6 = localObject10;
      localObject7 = localObject13;
      localObject1 = localObject11;
      localObject2 = localObject14;
      if (paramConfiguration != Configuration.EMPTY)
      {
        localObject4 = localObject9;
        localObject5 = localObject12;
        localObject6 = localObject10;
        localObject7 = localObject13;
        localObject1 = localObject11;
        localObject2 = localObject14;
        if (DEBUG_STARTING_WINDOW)
        {
          localObject4 = localObject9;
          localObject5 = localObject12;
          localObject6 = localObject10;
          localObject7 = localObject13;
          localObject1 = localObject11;
          localObject2 = localObject14;
          Slog.d("WindowManager", "addStartingWindow: creating context based on overrideConfig" + paramConfiguration + " for starting window");
        }
        localObject4 = localObject9;
        localObject5 = localObject12;
        localObject6 = localObject10;
        localObject7 = localObject13;
        localObject1 = localObject11;
        localObject2 = localObject14;
        localContext = ((Context)localObject3).createConfigurationContext(paramConfiguration);
        localObject4 = localObject9;
        localObject5 = localObject12;
        localObject6 = localObject10;
        localObject7 = localObject13;
        localObject1 = localObject11;
        localObject2 = localObject14;
        localContext.setTheme(paramInt1);
        localObject4 = localObject9;
        localObject5 = localObject12;
        localObject6 = localObject10;
        localObject7 = localObject13;
        localObject1 = localObject11;
        localObject2 = localObject14;
        paramInt1 = localContext.obtainStyledAttributes(R.styleable.Window).getResourceId(1, 0);
        localObject8 = localObject3;
        if (paramInt1 != 0)
        {
          localObject8 = localObject3;
          localObject4 = localObject9;
          localObject5 = localObject12;
          localObject6 = localObject10;
          localObject7 = localObject13;
          localObject1 = localObject11;
          localObject2 = localObject14;
          if (localContext.getDrawable(paramInt1) != null)
          {
            localObject4 = localObject9;
            localObject5 = localObject12;
            localObject6 = localObject10;
            localObject7 = localObject13;
            localObject1 = localObject11;
            localObject2 = localObject14;
            if (!DEBUG_STARTING_WINDOW) {
              break label2293;
            }
            localObject4 = localObject9;
            localObject5 = localObject12;
            localObject6 = localObject10;
            localObject7 = localObject13;
            localObject1 = localObject11;
            localObject2 = localObject14;
            Slog.d("WindowManager", "addStartingWindow: apply overrideConfig" + paramConfiguration + " to starting window resId=" + paramInt1);
            break label2293;
          }
        }
      }
    }
    localObject4 = localObject9;
    localObject5 = localObject12;
    localObject6 = localObject10;
    localObject7 = localObject13;
    localObject1 = localObject11;
    localObject2 = localObject14;
    localObject3 = new PhoneWindow((Context)localObject8);
    localObject4 = localObject9;
    localObject5 = localObject12;
    localObject6 = localObject10;
    localObject7 = localObject13;
    localObject1 = localObject11;
    localObject2 = localObject14;
    ((PhoneWindow)localObject3).setIsStartingWindow(true);
    localObject4 = localObject9;
    localObject5 = localObject12;
    localObject6 = localObject10;
    localObject7 = localObject13;
    localObject1 = localObject11;
    localObject2 = localObject14;
    paramConfiguration = ((Context)localObject8).getResources().getText(paramInt2, null);
    if (paramConfiguration != null)
    {
      localObject4 = localObject9;
      localObject5 = localObject12;
      localObject6 = localObject10;
      localObject7 = localObject13;
      localObject1 = localObject11;
      localObject2 = localObject14;
      ((PhoneWindow)localObject3).setTitle(paramConfiguration, true);
      localObject4 = localObject9;
      localObject5 = localObject12;
      localObject6 = localObject10;
      localObject7 = localObject13;
      localObject1 = localObject11;
      localObject2 = localObject14;
      ((PhoneWindow)localObject3).setType(3);
      localObject4 = localObject9;
      localObject5 = localObject12;
      localObject6 = localObject10;
      localObject7 = localObject13;
      localObject1 = localObject11;
      localObject2 = localObject14;
      paramCharSequence = this.mWindowManagerFuncs.getWindowManagerLock();
      localObject4 = localObject9;
      localObject5 = localObject12;
      localObject6 = localObject10;
      localObject7 = localObject13;
      localObject1 = localObject11;
      localObject2 = localObject14;
    }
    for (;;)
    {
      try
      {
        boolean bool = this.mKeyguardHidden;
        paramInt1 = paramInt5;
        if (bool) {
          paramInt1 = paramInt5 | 0x80000;
        }
        localObject4 = localObject9;
        localObject5 = localObject12;
        localObject6 = localObject10;
        localObject7 = localObject13;
        localObject1 = localObject11;
        localObject2 = localObject14;
        localObject4 = localObject9;
        localObject5 = localObject12;
        localObject6 = localObject10;
        localObject7 = localObject13;
        localObject1 = localObject11;
        localObject2 = localObject14;
        ((PhoneWindow)localObject3).setFlags(paramInt1 | 0x10 | 0x8 | 0x20000, paramInt1 | 0x10 | 0x8 | 0x20000);
        localObject4 = localObject9;
        localObject5 = localObject12;
        localObject6 = localObject10;
        localObject7 = localObject13;
        localObject1 = localObject11;
        localObject2 = localObject14;
        ((PhoneWindow)localObject3).setDefaultIcon(paramInt3);
        localObject4 = localObject9;
        localObject5 = localObject12;
        localObject6 = localObject10;
        localObject7 = localObject13;
        localObject1 = localObject11;
        localObject2 = localObject14;
        ((PhoneWindow)localObject3).setDefaultLogo(paramInt4);
        localObject4 = localObject9;
        localObject5 = localObject12;
        localObject6 = localObject10;
        localObject7 = localObject13;
        localObject1 = localObject11;
        localObject2 = localObject14;
        ((PhoneWindow)localObject3).setLayout(-1, -1);
        localObject4 = localObject9;
        localObject5 = localObject12;
        localObject6 = localObject10;
        localObject7 = localObject13;
        localObject1 = localObject11;
        localObject2 = localObject14;
        paramConfiguration = ((PhoneWindow)localObject3).getAttributes();
        localObject4 = localObject9;
        localObject5 = localObject12;
        localObject6 = localObject10;
        localObject7 = localObject13;
        localObject1 = localObject11;
        localObject2 = localObject14;
        paramConfiguration.token = paramIBinder;
        localObject4 = localObject9;
        localObject5 = localObject12;
        localObject6 = localObject10;
        localObject7 = localObject13;
        localObject1 = localObject11;
        localObject2 = localObject14;
        paramConfiguration.packageName = paramString;
        localObject4 = localObject9;
        localObject5 = localObject12;
        localObject6 = localObject10;
        localObject7 = localObject13;
        localObject1 = localObject11;
        localObject2 = localObject14;
        paramConfiguration.windowAnimations = ((PhoneWindow)localObject3).getWindowStyle().getResourceId(8, 0);
        localObject4 = localObject9;
        localObject5 = localObject12;
        localObject6 = localObject10;
        localObject7 = localObject13;
        localObject1 = localObject11;
        localObject2 = localObject14;
        paramConfiguration.privateFlags |= 0x1;
        localObject4 = localObject9;
        localObject5 = localObject12;
        localObject6 = localObject10;
        localObject7 = localObject13;
        localObject1 = localObject11;
        localObject2 = localObject14;
        paramConfiguration.privateFlags |= 0x10;
        localObject4 = localObject9;
        localObject5 = localObject12;
        localObject6 = localObject10;
        localObject7 = localObject13;
        localObject1 = localObject11;
        localObject2 = localObject14;
        if (!paramCompatibilityInfo.supportsScreen())
        {
          localObject4 = localObject9;
          localObject5 = localObject12;
          localObject6 = localObject10;
          localObject7 = localObject13;
          localObject1 = localObject11;
          localObject2 = localObject14;
          paramConfiguration.privateFlags |= 0x80;
        }
        localObject4 = localObject9;
        localObject5 = localObject12;
        localObject6 = localObject10;
        localObject7 = localObject13;
        localObject1 = localObject11;
        localObject2 = localObject14;
        paramConfiguration.setTitle("Starting " + paramString);
        localObject4 = localObject9;
        localObject5 = localObject12;
        localObject6 = localObject10;
        localObject7 = localObject13;
        localObject1 = localObject11;
        localObject2 = localObject14;
        paramCharSequence = (WindowManager)((Context)localObject8).getSystemService("window");
        localObject4 = localObject9;
        localObject5 = paramCharSequence;
        localObject6 = localObject10;
        localObject7 = paramCharSequence;
        localObject1 = localObject11;
        localObject2 = paramCharSequence;
        paramCompatibilityInfo = ThemeController.getInstance((Context)localObject8);
        localObject4 = localObject9;
        localObject5 = paramCharSequence;
        localObject6 = localObject10;
        localObject7 = paramCharSequence;
        localObject1 = localObject11;
        localObject2 = paramCharSequence;
        if (paramCompatibilityInfo.getThemeState() == 2) {
          break label2210;
        }
        localObject4 = localObject9;
        localObject5 = paramCharSequence;
        localObject6 = localObject10;
        localObject7 = paramCharSequence;
        localObject1 = localObject11;
        localObject2 = paramCharSequence;
        if (!paramCompatibilityInfo.checkHasTheme(paramString)) {
          break label2210;
        }
        localObject4 = localObject9;
        localObject5 = paramCharSequence;
        localObject6 = localObject10;
        localObject7 = paramCharSequence;
        localObject1 = localObject11;
        localObject2 = paramCharSequence;
        paramInt1 = paramCompatibilityInfo.getCorrectThemeResource(new int[] { -1, -16777216, -1 });
        localObject4 = localObject9;
        localObject5 = paramCharSequence;
        localObject6 = localObject10;
        localObject7 = paramCharSequence;
        localObject1 = localObject11;
        localObject2 = paramCharSequence;
        ((PhoneWindow)localObject3).setStatusBarColor(paramInt1);
        localObject4 = localObject9;
        localObject5 = paramCharSequence;
        localObject6 = localObject10;
        localObject7 = paramCharSequence;
        localObject1 = localObject11;
        localObject2 = paramCharSequence;
        ((PhoneWindow)localObject3).setStartupWindowTheme(true, paramInt1);
        localObject4 = localObject9;
        localObject5 = paramCharSequence;
        localObject6 = localObject10;
        localObject7 = paramCharSequence;
        localObject1 = localObject11;
        localObject2 = paramCharSequence;
        paramCompatibilityInfo = ((PhoneWindow)localObject3).getDecorView();
        localObject4 = paramCompatibilityInfo;
        localObject5 = paramCharSequence;
        localObject6 = paramCompatibilityInfo;
        localObject7 = paramCharSequence;
        localObject1 = paramCompatibilityInfo;
        localObject2 = paramCharSequence;
        if (DEBUG_STARTING_WINDOW)
        {
          localObject4 = paramCompatibilityInfo;
          localObject5 = paramCharSequence;
          localObject6 = paramCompatibilityInfo;
          localObject7 = paramCharSequence;
          localObject1 = paramCompatibilityInfo;
          localObject2 = paramCharSequence;
          localObject3 = new StringBuilder().append("Adding starting window for ").append(paramString).append(" / ").append(paramIBinder).append(": ");
          localObject4 = paramCompatibilityInfo;
          localObject5 = paramCharSequence;
          localObject6 = paramCompatibilityInfo;
          localObject7 = paramCharSequence;
          localObject1 = paramCompatibilityInfo;
          localObject2 = paramCharSequence;
          if (paramCompatibilityInfo.getParent() == null) {
            break label2278;
          }
          paramString = paramCompatibilityInfo;
          localObject4 = paramCompatibilityInfo;
          localObject5 = paramCharSequence;
          localObject6 = paramCompatibilityInfo;
          localObject7 = paramCharSequence;
          localObject1 = paramCompatibilityInfo;
          localObject2 = paramCharSequence;
          Slog.d("WindowManager", paramString);
        }
        localObject4 = paramCompatibilityInfo;
        localObject5 = paramCharSequence;
        localObject6 = paramCompatibilityInfo;
        localObject7 = paramCharSequence;
        localObject1 = paramCompatibilityInfo;
        localObject2 = paramCharSequence;
        paramCharSequence.addView(paramCompatibilityInfo, paramConfiguration);
        localObject4 = paramCompatibilityInfo;
        localObject5 = paramCharSequence;
        localObject6 = paramCompatibilityInfo;
        localObject7 = paramCharSequence;
        localObject1 = paramCompatibilityInfo;
        localObject2 = paramCharSequence;
        paramString = paramCompatibilityInfo.getParent();
        if (paramString == null) {
          break label2283;
        }
        paramIBinder = paramCompatibilityInfo;
        if ((paramCompatibilityInfo != null) && (paramCompatibilityInfo.getParent() == null))
        {
          Log.w("WindowManager", "view not successfully added to wm, removing view");
          paramCharSequence.removeViewImmediate(paramCompatibilityInfo);
        }
        return paramIBinder;
      }
      finally
      {
        localObject4 = localObject9;
        localObject5 = localObject12;
        localObject6 = localObject10;
        localObject7 = localObject13;
        localObject1 = localObject11;
        localObject2 = localObject14;
        localObject4 = localObject9;
        localObject5 = localObject12;
        localObject6 = localObject10;
        localObject7 = localObject13;
        localObject1 = localObject11;
        localObject2 = localObject14;
      }
      localObject4 = localObject9;
      localObject5 = localObject12;
      localObject6 = localObject10;
      localObject7 = localObject13;
      localObject1 = localObject11;
      localObject2 = localObject14;
      ((PhoneWindow)localObject3).setTitle(paramCharSequence, false);
      break;
      label2210:
      label2278:
      paramString = null;
      continue;
      label2283:
      paramIBinder = null;
    }
  }
  
  public void adjustConfigurationLw(Configuration paramConfiguration, int paramInt1, int paramInt2)
  {
    boolean bool = false;
    if ((paramInt1 & 0x1) != 0) {
      bool = true;
    }
    this.mHaveBuiltInKeyboard = bool;
    readConfigurationDependentBehaviors();
    readLidState();
    if ((paramConfiguration.keyboard == 1) || ((paramInt1 == 1) && (isHidden(this.mLidKeyboardAccessibility))))
    {
      paramConfiguration.hardKeyboardHidden = 2;
      if (!this.mHasSoftInput) {
        paramConfiguration.keyboardHidden = 2;
      }
    }
    if ((paramConfiguration.navigation == 1) || ((paramInt2 == 1) && (isHidden(this.mLidNavigationAccessibility)))) {
      paramConfiguration.navigationHidden = 2;
    }
  }
  
  public int adjustSystemUiVisibilityLw(int paramInt)
  {
    this.mStatusBarController.adjustSystemUiVisibilityLw(this.mLastSystemUiFlags, paramInt);
    this.mNavigationBarController.adjustSystemUiVisibilityLw(this.mLastSystemUiFlags, paramInt);
    this.mResettingSystemUiFlags &= paramInt;
    return this.mResettingSystemUiFlags & paramInt & this.mForceClearedSystemUiFlags;
  }
  
  public void adjustWindowParamsLw(WindowManager.LayoutParams paramLayoutParams)
  {
    switch (paramLayoutParams.type)
    {
    default: 
      if (paramLayoutParams.type != 2000) {
        paramLayoutParams.privateFlags &= 0xFBFF;
      }
      if (ActivityManager.isHighEndGfx())
      {
        if ((paramLayoutParams.flags & 0x80000000) != 0) {
          paramLayoutParams.subtreeSystemUiVisibility |= 0x200;
        }
        if ((paramLayoutParams.privateFlags & 0x20000) == 0) {
          break label275;
        }
      }
      break;
    }
    label275:
    for (int i = 1;; i = 0)
    {
      if (((paramLayoutParams.flags & 0x80000000) != 0) || ((i != 0) && (paramLayoutParams.height == -1) && (paramLayoutParams.width == -1))) {
        paramLayoutParams.subtreeSystemUiVisibility |= 0x400;
      }
      return;
      paramLayoutParams.flags |= 0x18;
      paramLayoutParams.flags &= 0xFFFBFFFF;
      break;
      if (!this.mKeyguardHidden) {
        break;
      }
      paramLayoutParams.flags &= 0xFFEFFFFF;
      paramLayoutParams.privateFlags &= 0xFBFF;
      break;
      paramLayoutParams.flags |= 0x8;
      break;
      if ((paramLayoutParams.hideTimeoutMilliseconds < 0L) || (paramLayoutParams.hideTimeoutMilliseconds > 3500L)) {
        paramLayoutParams.hideTimeoutMilliseconds = 3500L;
      }
      paramLayoutParams.windowAnimations = 16973828;
      break;
    }
  }
  
  public boolean allowAppAnimationsLw()
  {
    return (!isStatusBarKeyguard()) && (!this.mShowingDream);
  }
  
  public void applyPostLayoutPolicyLw(WindowManagerPolicy.WindowState paramWindowState1, WindowManager.LayoutParams paramLayoutParams, WindowManagerPolicy.WindowState paramWindowState2)
  {
    if (DEBUG_LAYOUT) {
      Slog.i("WindowManager", "Win " + paramWindowState1 + ": isVisibleOrBehindKeyguardLw=" + paramWindowState1.isVisibleOrBehindKeyguardLw());
    }
    int i1 = PolicyControl.getWindowFlags(paramWindowState1, paramLayoutParams);
    if ((this.mTopFullscreenOpaqueWindowState == null) && (paramWindowState1.isVisibleLw()) && (paramLayoutParams.type == 2011))
    {
      this.mForcingShowNavBar = true;
      this.mForcingShowNavBarLayer = paramWindowState1.getSurfaceLayer();
    }
    if (paramLayoutParams.type == 2000)
    {
      if ((paramLayoutParams.privateFlags & 0x400) != 0)
      {
        this.mForceStatusBarFromKeyguard = true;
        this.mShowingLockscreen = true;
      }
      if ((paramLayoutParams.privateFlags & 0x1000) != 0) {
        this.mForceStatusBarTransparent = true;
      }
    }
    int k;
    label175:
    int n;
    label187:
    int i2;
    int m;
    if (paramLayoutParams.type >= 1) {
      if (paramLayoutParams.type < 2000)
      {
        i = 1;
        if ((0x80000 & i1) == 0) {
          break label518;
        }
        k = 1;
        if ((0x400000 & i1) == 0) {
          break label524;
        }
        n = 1;
        i2 = paramWindowState1.getStackId();
        if ((this.mTopFullscreenOpaqueWindowState == null) && (paramWindowState1.isVisibleOrBehindKeyguardLw()) && (!paramWindowState1.isGoneForLayoutLw())) {
          break label530;
        }
        m = i;
        if (this.mTopFullscreenOpaqueWindowState == null)
        {
          m = i;
          if (this.mWinShowWhenLocked == null)
          {
            m = i;
            if (paramWindowState1.isAnimatingLw())
            {
              m = i;
              if (i != 0)
              {
                m = i;
                if (k != 0)
                {
                  m = i;
                  if (this.mKeyguardHidden)
                  {
                    this.mHideLockScreen = true;
                    this.mWinShowWhenLocked = paramWindowState1;
                    m = i;
                  }
                }
              }
            }
          }
        }
        label298:
        if ((paramWindowState1.isVisibleOrBehindKeyguardLw()) && (!paramWindowState1.isGoneForLayoutLw())) {
          break label1147;
        }
      }
    }
    label518:
    label524:
    label530:
    label555:
    label715:
    label851:
    label883:
    label983:
    label996:
    label1057:
    label1063:
    label1069:
    label1147:
    for (int i = 0;; i = 1)
    {
      if ((i != 0) && (paramWindowState1.getAttrs().type == 2031))
      {
        if (this.mTopFullscreenOpaqueWindowState == null)
        {
          this.mTopFullscreenOpaqueWindowState = paramWindowState1;
          if (this.mTopFullscreenOpaqueOrDimmingWindowState == null) {
            this.mTopFullscreenOpaqueOrDimmingWindowState = paramWindowState1;
          }
        }
        if (this.mTopDockedOpaqueWindowState == null)
        {
          this.mTopDockedOpaqueWindowState = paramWindowState1;
          if (this.mTopDockedOpaqueOrDimmingWindowState == null) {
            this.mTopDockedOpaqueOrDimmingWindowState = paramWindowState1;
          }
        }
      }
      if ((this.mTopFullscreenOpaqueOrDimmingWindowState == null) && (i != 0) && (paramWindowState1.isDimming()) && (ActivityManager.StackId.normallyFullscreenWindows(i2))) {
        this.mTopFullscreenOpaqueOrDimmingWindowState = paramWindowState1;
      }
      if ((this.mTopDockedOpaqueWindowState == null) && (i != 0) && (m != 0) && (paramWindowState2 == null) && (isFullscreen(paramLayoutParams)) && (i2 == 3))
      {
        this.mTopDockedOpaqueWindowState = paramWindowState1;
        if (this.mTopDockedOpaqueOrDimmingWindowState == null) {
          this.mTopDockedOpaqueOrDimmingWindowState = paramWindowState1;
        }
      }
      if ((this.mTopDockedOpaqueOrDimmingWindowState == null) && (i != 0) && (paramWindowState1.isDimming()) && (i2 == 3)) {
        this.mTopDockedOpaqueOrDimmingWindowState = paramWindowState1;
      }
      return;
      i = 0;
      break;
      i = 0;
      break;
      k = 0;
      break label175;
      n = 0;
      break label187;
      int j;
      IApplicationToken localIApplicationToken;
      boolean bool;
      if ((i1 & 0x800) != 0)
      {
        if ((paramLayoutParams.privateFlags & 0x400) != 0) {
          this.mForceStatusBarFromKeyguard = true;
        }
      }
      else
      {
        j = i;
        if (paramLayoutParams.type == 2023) {
          if (this.mDreamingLockscreen)
          {
            j = i;
            if (paramWindowState1.isVisibleLw())
            {
              j = i;
              if (!paramWindowState1.hasDrawnLw()) {}
            }
          }
          else
          {
            this.mShowingDream = true;
            j = 1;
          }
        }
        localIApplicationToken = paramWindowState1.getAppToken();
        m = j;
        if (j == 0) {
          break label298;
        }
        m = j;
        if (paramWindowState2 != null) {
          break label298;
        }
        if (k == 0) {
          break label996;
        }
        this.mAppsToBeHidden.remove(localIApplicationToken);
        this.mAppsThatDismissKeyguard.remove(localIApplicationToken);
        if (this.mAppsToBeHidden.isEmpty())
        {
          if ((n != 0) && (!this.mKeyguardSecure)) {
            break label983;
          }
          if ((paramWindowState1.isDrawnLw()) || (paramWindowState1.hasAppShownWindows()))
          {
            this.mWinShowWhenLocked = paramWindowState1;
            this.mHideLockScreen = true;
            this.mForceStatusBarFromKeyguard = false;
          }
        }
        if ((isFullscreen(paramLayoutParams)) && (ActivityManager.StackId.normallyFullscreenWindows(i2)))
        {
          if (DEBUG_LAYOUT) {
            Slog.v("WindowManager", "Fullscreen window: " + paramWindowState1);
          }
          this.mTopFullscreenOpaqueWindowState = paramWindowState1;
          if (this.mTopFullscreenOpaqueOrDimmingWindowState == null) {
            this.mTopFullscreenOpaqueOrDimmingWindowState = paramWindowState1;
          }
          if ((this.mAppsThatDismissKeyguard.isEmpty()) || (this.mDismissKeyguard != 0)) {
            break label1069;
          }
          if (DEBUG_LAYOUT) {
            Slog.v("WindowManager", "Setting mDismissKeyguard true by win " + paramWindowState1);
          }
          if ((this.mWinDismissingKeyguard != paramWindowState1) || (this.mSecureDismissingKeyguard != this.mKeyguardSecure)) {
            break label1057;
          }
          i = 2;
          this.mDismissKeyguard = i;
          this.mWinDismissingKeyguard = paramWindowState1;
          this.mSecureDismissingKeyguard = this.mKeyguardSecure;
          if (!this.mShowingLockscreen) {
            break label1063;
          }
          bool = this.mKeyguardSecure;
        }
      }
      for (this.mForceStatusBarFromKeyguard = bool;; this.mForceStatusBarFromKeyguard = false)
      {
        do
        {
          if ((i1 & 0x1) != 0) {
            this.mAllowLockscreenWhenOn = true;
          }
          m = j;
          if (this.mKeyguardHidden) {
            break;
          }
          m = j;
          if (this.mWinShowWhenLocked == null) {
            break;
          }
          m = j;
          if (this.mWinShowWhenLocked.getAppToken() == paramWindowState1.getAppToken()) {
            break;
          }
          m = j;
          if ((paramLayoutParams.flags & 0x80000) != 0) {
            break;
          }
          paramWindowState1.hideLw(false);
          m = j;
          break;
          this.mForceStatusBar = true;
          break label555;
          this.mAppsThatDismissKeyguard.add(localIApplicationToken);
          break label715;
          if (n != 0)
          {
            if (this.mKeyguardSecure) {
              this.mAppsToBeHidden.add(localIApplicationToken);
            }
            for (;;)
            {
              this.mAppsThatDismissKeyguard.add(localIApplicationToken);
              break;
              this.mAppsToBeHidden.remove(localIApplicationToken);
            }
          }
          this.mAppsToBeHidden.add(localIApplicationToken);
          break label715;
          i = 1;
          break label851;
          bool = false;
          break label883;
        } while ((!this.mAppsToBeHidden.isEmpty()) || (k == 0) || ((!paramWindowState1.isDrawnLw()) && (!paramWindowState1.hasAppShownWindows())));
        if (DEBUG_LAYOUT) {
          Slog.v("WindowManager", "Setting mHideLockScreen to true by win " + paramWindowState1);
        }
        this.mHideLockScreen = true;
      }
    }
  }
  
  public void beginLayoutLw(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mDisplayRotation = paramInt3;
    int i;
    int m;
    int j;
    int k;
    Rect localRect1;
    Rect localRect2;
    Rect localRect3;
    Rect localRect4;
    Rect localRect5;
    int i2;
    label582:
    label594:
    int i1;
    label606:
    label617:
    boolean bool1;
    label625:
    label632:
    boolean bool2;
    label655:
    boolean bool3;
    if (paramBoolean) {
      switch (paramInt3)
      {
      default: 
        i = this.mOverscanLeft;
        m = this.mOverscanTop;
        j = this.mOverscanRight;
        k = this.mOverscanBottom;
        this.mRestrictedOverscanScreenLeft = 0;
        this.mOverscanScreenLeft = 0;
        this.mRestrictedOverscanScreenTop = 0;
        this.mOverscanScreenTop = 0;
        this.mRestrictedOverscanScreenWidth = paramInt1;
        this.mOverscanScreenWidth = paramInt1;
        this.mRestrictedOverscanScreenHeight = paramInt2;
        this.mOverscanScreenHeight = paramInt2;
        this.mSystemLeft = 0;
        this.mSystemTop = 0;
        this.mSystemRight = paramInt1;
        this.mSystemBottom = paramInt2;
        this.mUnrestrictedScreenLeft = i;
        this.mUnrestrictedScreenTop = m;
        this.mUnrestrictedScreenWidth = (paramInt1 - i - j);
        this.mUnrestrictedScreenHeight = (paramInt2 - m - k);
        this.mRestrictedScreenLeft = this.mUnrestrictedScreenLeft;
        this.mRestrictedScreenTop = this.mUnrestrictedScreenTop;
        m = this.mUnrestrictedScreenWidth;
        this.mSystemGestures.screenWidth = m;
        this.mRestrictedScreenWidth = m;
        m = this.mUnrestrictedScreenHeight;
        this.mSystemGestures.screenHeight = m;
        this.mRestrictedScreenHeight = m;
        m = this.mUnrestrictedScreenLeft;
        this.mCurLeft = m;
        this.mStableFullscreenLeft = m;
        this.mStableLeft = m;
        this.mVoiceContentLeft = m;
        this.mContentLeft = m;
        this.mDockLeft = m;
        m = this.mUnrestrictedScreenTop;
        this.mCurTop = m;
        this.mStableFullscreenTop = m;
        this.mStableTop = m;
        this.mVoiceContentTop = m;
        this.mContentTop = m;
        this.mDockTop = m;
        m = paramInt1 - j;
        this.mCurRight = m;
        this.mStableFullscreenRight = m;
        this.mStableRight = m;
        this.mVoiceContentRight = m;
        this.mContentRight = m;
        this.mDockRight = m;
        m = paramInt2 - k;
        this.mCurBottom = m;
        this.mStableFullscreenBottom = m;
        this.mStableBottom = m;
        this.mVoiceContentBottom = m;
        this.mContentBottom = m;
        this.mDockBottom = m;
        this.mDockLayer = 268435456;
        this.mStatusBarLayer = -1;
        localRect1 = mTmpParentFrame;
        localRect2 = mTmpDisplayFrame;
        localRect3 = mTmpOverscanFrame;
        localRect4 = mTmpVisibleFrame;
        localRect5 = mTmpDecorFrame;
        m = this.mDockLeft;
        localRect4.left = m;
        localRect3.left = m;
        localRect2.left = m;
        localRect1.left = m;
        m = this.mDockTop;
        localRect4.top = m;
        localRect3.top = m;
        localRect2.top = m;
        localRect1.top = m;
        m = this.mDockRight;
        localRect4.right = m;
        localRect3.right = m;
        localRect2.right = m;
        localRect1.right = m;
        m = this.mDockBottom;
        localRect4.bottom = m;
        localRect3.bottom = m;
        localRect2.bottom = m;
        localRect1.bottom = m;
        localRect5.setEmpty();
        if (paramBoolean)
        {
          i2 = this.mLastSystemUiFlags;
          if ((i2 & 0x2) != 0) {
            break label986;
          }
          m = 1;
          if ((0x80008000 & i2) == 0) {
            break label992;
          }
          n = 1;
          if ((i2 & 0x800) == 0) {
            break label998;
          }
          i1 = 1;
          if ((i2 & 0x1000) == 0) {
            break label1004;
          }
          paramBoolean = true;
          if (i1 != 0) {
            break label1009;
          }
          bool1 = paramBoolean;
          if (!paramBoolean) {
            break label1015;
          }
          i1 = 0;
          paramBoolean = n & i1;
          if ((isStatusBarKeyguard()) && (!this.mHideLockScreen)) {
            break label1021;
          }
          bool2 = false;
          bool3 = paramBoolean;
          if (!bool2) {
            bool3 = paramBoolean & areTranslucentBarsAllowed();
          }
          if ((bool2) || (this.mStatusBar == null) || (this.mStatusBar.getAttrs().height != -1)) {
            break label1032;
          }
          if (this.mStatusBar.getAttrs().width != -1) {
            break label1027;
          }
          paramBoolean = true;
          label717:
          if ((m == 0) && (!bool1)) {
            break label1037;
          }
          if (this.mInputConsumer != null)
          {
            this.mHandler.sendMessage(this.mHandler.obtainMessage(19, this.mInputConsumer));
            this.mInputConsumer = null;
          }
          label760:
          if (!canHideNavigationBar()) {
            break label1071;
          }
        }
        break;
      }
    }
    label986:
    label992:
    label998:
    label1004:
    label1009:
    label1015:
    label1021:
    label1027:
    label1032:
    label1037:
    label1071:
    for (int n = 0;; n = 1)
    {
      paramBoolean = layoutNavigationBar(paramInt1, paramInt2, paramInt3, paramInt4, i, j, k, localRect5, m | n, bool3, bool1, paramBoolean);
      if (DEBUG_LAYOUT) {
        Slog.i("WindowManager", String.format("mDock rect: (%d,%d - %d,%d)", new Object[] { Integer.valueOf(this.mDockLeft), Integer.valueOf(this.mDockTop), Integer.valueOf(this.mDockRight), Integer.valueOf(this.mDockBottom) }));
      }
      if ((paramBoolean | layoutStatusBar(localRect1, localRect2, localRect3, localRect4, localRect5, i2, bool2))) {
        updateSystemUiVisibilityLw();
      }
      return;
      i = this.mOverscanTop;
      m = this.mOverscanRight;
      j = this.mOverscanBottom;
      k = this.mOverscanLeft;
      break;
      i = this.mOverscanRight;
      m = this.mOverscanBottom;
      j = this.mOverscanLeft;
      k = this.mOverscanTop;
      break;
      i = this.mOverscanBottom;
      m = this.mOverscanLeft;
      j = this.mOverscanTop;
      k = this.mOverscanRight;
      break;
      i = 0;
      m = 0;
      j = 0;
      k = 0;
      break;
      m = 0;
      break label582;
      n = 0;
      break label594;
      i1 = 0;
      break label606;
      paramBoolean = false;
      break label617;
      bool1 = true;
      break label625;
      i1 = 1;
      break label632;
      bool2 = true;
      break label655;
      paramBoolean = false;
      break label717;
      paramBoolean = false;
      break label717;
      if (this.mInputConsumer != null) {
        break label760;
      }
      this.mInputConsumer = this.mWindowManagerFuncs.addInputConsumer(this.mHandler.getLooper(), this.mHideNavInputEventReceiverFactory);
      break label760;
    }
  }
  
  public void beginPostLayoutPolicyLw(int paramInt1, int paramInt2)
  {
    boolean bool2 = false;
    this.mTopFullscreenOpaqueWindowState = null;
    this.mTopFullscreenOpaqueOrDimmingWindowState = null;
    this.mTopDockedOpaqueWindowState = null;
    this.mTopDockedOpaqueOrDimmingWindowState = null;
    this.mAppsToBeHidden.clear();
    this.mAppsThatDismissKeyguard.clear();
    this.mForceStatusBar = false;
    this.mForceStatusBarFromKeyguard = false;
    this.mForceStatusBarTransparent = false;
    this.mForcingShowNavBar = false;
    this.mForcingShowNavBarLayer = -1;
    this.mHideLockScreen = false;
    this.mAllowLockscreenWhenOn = false;
    this.mDismissKeyguard = 0;
    this.mShowingLockscreen = false;
    this.mShowingDream = false;
    this.mWinShowWhenLocked = null;
    this.mKeyguardSecure = isKeyguardSecure(this.mCurrentUserId);
    boolean bool1 = bool2;
    if (this.mKeyguardSecure)
    {
      bool1 = bool2;
      if (this.mKeyguardDelegate != null) {
        bool1 = this.mKeyguardDelegate.isShowing();
      }
    }
    this.mKeyguardSecureIncludingHidden = bool1;
  }
  
  public boolean canBeForceHidden(WindowManagerPolicy.WindowState paramWindowState, WindowManager.LayoutParams paramLayoutParams)
  {
    boolean bool = false;
    switch (paramLayoutParams.type)
    {
    default: 
      if (windowTypeToLayerLw(paramWindowState.getBaseType()) < windowTypeToLayerLw(2000)) {
        bool = true;
      }
      return bool;
    }
    return false;
  }
  
  public boolean canMagnifyWindow(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return true;
    }
    return false;
  }
  
  public boolean canShowDismissingWindowWhileLockedLw()
  {
    if ((this.mKeyguardDelegate != null) && (this.mKeyguardDelegate.isTrusted())) {
      return this.mCurrentlyDismissingKeyguard;
    }
    return false;
  }
  
  protected void cancelPreloadRecentApps()
  {
    if (this.mPreloadedRecentApps)
    {
      this.mPreloadedRecentApps = false;
      StatusBarManagerInternal localStatusBarManagerInternal = getStatusBarManagerInternal();
      if (localStatusBarManagerInternal != null) {
        localStatusBarManagerInternal.cancelPreloadRecentApps();
      }
    }
  }
  
  public int checkAddPermission(WindowManager.LayoutParams paramLayoutParams, int[] paramArrayOfInt)
  {
    int i = paramLayoutParams.type;
    paramArrayOfInt[0] = -1;
    if (((i < 1) || (i > 99)) && ((i < 1000) || (i > 1999)) && ((i < 2000) || (i > 2999))) {
      return -10;
    }
    if ((i < 2000) || (i > 2999)) {
      return 0;
    }
    Object localObject2 = null;
    Object localObject1 = localObject2;
    switch (i)
    {
    default: 
      localObject1 = "android.permission.INTERNAL_SYSTEM_WINDOW";
    }
    while (localObject1 != null) {
      if ("android.permission.SYSTEM_ALERT_WINDOW".equals(localObject1))
      {
        i = Binder.getCallingUid();
        if (i == 1000)
        {
          return 0;
          paramArrayOfInt[0] = 45;
          localObject1 = localObject2;
          continue;
          localObject1 = "android.permission.SYSTEM_ALERT_WINDOW";
          paramArrayOfInt[0] = 24;
        }
        else
        {
          switch (this.mAppOpsManager.checkOpNoThrow(paramArrayOfInt[0], i, paramLayoutParams.packageName))
          {
          default: 
            if (this.mContext.checkCallingPermission((String)localObject1) != 0) {
              return -8;
            }
            break;
          case 0: 
          case 1: 
            return 0;
          case 2: 
            try
            {
              i = this.mContext.getPackageManager().getApplicationInfo(paramLayoutParams.packageName, UserHandle.getUserId(i)).targetSdkVersion;
              if (i < 23) {
                return 0;
              }
            }
            catch (PackageManager.NameNotFoundException paramLayoutParams) {}
            return -8;
          }
          return 0;
        }
      }
      else if (this.mContext.checkCallingOrSelfPermission((String)localObject1) != 0)
      {
        return -8;
      }
    }
    return 0;
  }
  
  public boolean checkShowToOwnerOnly(WindowManager.LayoutParams paramLayoutParams)
  {
    switch (paramLayoutParams.type)
    {
    default: 
      if ((paramLayoutParams.privateFlags & 0x10) == 0) {
        return true;
      }
      break;
    }
    return this.mContext.checkCallingOrSelfPermission("android.permission.INTERNAL_SYSTEM_WINDOW") != 0;
  }
  
  public Animation createForceHideEnterAnimation(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean2) {
      return AnimationUtils.loadAnimation(this.mContext, 17432661);
    }
    Object localObject = this.mContext;
    if (paramBoolean1) {}
    for (int i = 17432662;; i = 17432660)
    {
      localObject = (AnimationSet)AnimationUtils.loadAnimation((Context)localObject, i);
      List localList = ((AnimationSet)localObject).getAnimations();
      i = localList.size() - 1;
      while (i >= 0)
      {
        ((Animation)localList.get(i)).setInterpolator(this.mLogDecelerateInterpolator);
        i -= 1;
      }
    }
    return (Animation)localObject;
  }
  
  public Animation createForceHideWallpaperExitAnimation(boolean paramBoolean)
  {
    if (paramBoolean) {
      return null;
    }
    return AnimationUtils.loadAnimation(this.mContext, 17432665);
  }
  
  Intent createHomeDockIntent()
  {
    Intent localIntent;
    if (this.mUiMode == 3)
    {
      if (!this.mEnableCarDockHomeCapture) {
        break label34;
      }
      localIntent = this.mCarDockIntent;
    }
    while (localIntent == null)
    {
      return null;
      if (this.mUiMode == 2)
      {
        label34:
        localIntent = null;
      }
      else
      {
        if (this.mUiMode == 6)
        {
          if ((this.mDockMode == 1) || (this.mDockMode == 4)) {}
          while (this.mDockMode == 3)
          {
            localIntent = this.mDeskDockIntent;
            break;
          }
        }
        localIntent = null;
      }
    }
    ActivityInfo localActivityInfo = null;
    ResolveInfo localResolveInfo = this.mContext.getPackageManager().resolveActivityAsUser(localIntent, 65664, this.mCurrentUserId);
    if (localResolveInfo != null) {
      localActivityInfo = localResolveInfo.activityInfo;
    }
    if ((localActivityInfo != null) && (localActivityInfo.metaData != null) && (localActivityInfo.metaData.getBoolean("android.dock_home")))
    {
      localIntent = new Intent(localIntent);
      localIntent.setClassName(localActivityInfo.packageName, localActivityInfo.name);
      return localIntent;
    }
    return null;
  }
  
  public void dismissKeyguardLw()
  {
    if ((this.mKeyguardDelegate != null) && (this.mKeyguardDelegate.isShowing()))
    {
      if (DEBUG_KEYGUARD) {
        Slog.d("WindowManager", "PWM.dismissKeyguardLw");
      }
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          PhoneWindowManager.this.mKeyguardDelegate.dismiss(false);
        }
      });
    }
  }
  
  void dispatchMediaKeyRepeatWithWakeLock(KeyEvent paramKeyEvent)
  {
    this.mHavePendingMediaKeyRepeatWithWakeLock = false;
    paramKeyEvent = KeyEvent.changeTimeRepeat(paramKeyEvent, SystemClock.uptimeMillis(), 1, paramKeyEvent.getFlags() | 0x80);
    if (DEBUG_INPUT) {
      Slog.d("WindowManager", "dispatchMediaKeyRepeatWithWakeLock: " + paramKeyEvent);
    }
    dispatchMediaKeyWithWakeLockToAudioService(paramKeyEvent);
    this.mBroadcastWakeLock.release();
  }
  
  void dispatchMediaKeyWithWakeLock(KeyEvent paramKeyEvent)
  {
    if (DEBUG_INPUT) {
      Slog.d("WindowManager", "dispatchMediaKeyWithWakeLock: " + paramKeyEvent);
    }
    if (this.mHavePendingMediaKeyRepeatWithWakeLock)
    {
      if (DEBUG_INPUT) {
        Slog.d("WindowManager", "dispatchMediaKeyWithWakeLock: canceled repeat");
      }
      this.mHandler.removeMessages(4);
      this.mHavePendingMediaKeyRepeatWithWakeLock = false;
      this.mBroadcastWakeLock.release();
    }
    dispatchMediaKeyWithWakeLockToAudioService(paramKeyEvent);
    if ((paramKeyEvent.getAction() == 0) && (paramKeyEvent.getRepeatCount() == 0))
    {
      this.mHavePendingMediaKeyRepeatWithWakeLock = true;
      paramKeyEvent = this.mHandler.obtainMessage(4, paramKeyEvent);
      paramKeyEvent.setAsynchronous(true);
      this.mHandler.sendMessageDelayed(paramKeyEvent, ViewConfiguration.getKeyRepeatTimeout());
      return;
    }
    this.mBroadcastWakeLock.release();
  }
  
  void dispatchMediaKeyWithWakeLockToAudioService(KeyEvent paramKeyEvent)
  {
    if (ActivityManagerNative.isSystemReady()) {
      MediaSessionLegacyHelper.getHelper(this.mContext).sendMediaButtonEvent(paramKeyEvent, true);
    }
  }
  
  public KeyEvent dispatchUnhandledKey(WindowManagerPolicy.WindowState paramWindowState, KeyEvent paramKeyEvent, int paramInt)
  {
    if (DEBUG_INPUT) {
      Slog.d("WindowManager", "Unhandled key: win=" + paramWindowState + ", action=" + paramKeyEvent.getAction() + ", flags=" + paramKeyEvent.getFlags() + ", keyCode=" + paramKeyEvent.getKeyCode() + ", scanCode=" + paramKeyEvent.getScanCode() + ", metaState=" + paramKeyEvent.getMetaState() + ", repeatCount=" + paramKeyEvent.getRepeatCount() + ", policyFlags=" + paramInt);
    }
    Object localObject1 = null;
    Object localObject3 = localObject1;
    Object localObject2;
    int j;
    int i;
    if ((paramKeyEvent.getFlags() & 0x400) == 0)
    {
      localObject2 = paramKeyEvent.getKeyCharacterMap();
      j = paramKeyEvent.getKeyCode();
      int k = paramKeyEvent.getMetaState();
      if (paramKeyEvent.getAction() != 0) {
        break label379;
      }
      if (paramKeyEvent.getRepeatCount() != 0) {
        break label373;
      }
      i = 1;
      if (i == 0) {
        break label385;
      }
      localObject2 = ((KeyCharacterMap)localObject2).getFallbackAction(j, k);
      label189:
      localObject3 = localObject1;
      if (localObject2 != null)
      {
        if (DEBUG_INPUT) {
          Slog.d("WindowManager", "Fallback: keyCode=" + ((KeyCharacterMap.FallbackAction)localObject2).keyCode + " metaState=" + Integer.toHexString(((KeyCharacterMap.FallbackAction)localObject2).metaState));
        }
        k = paramKeyEvent.getFlags();
        localObject3 = KeyEvent.obtain(paramKeyEvent.getDownTime(), paramKeyEvent.getEventTime(), paramKeyEvent.getAction(), ((KeyCharacterMap.FallbackAction)localObject2).keyCode, paramKeyEvent.getRepeatCount(), ((KeyCharacterMap.FallbackAction)localObject2).metaState, paramKeyEvent.getDeviceId(), paramKeyEvent.getScanCode(), k | 0x400, paramKeyEvent.getSource(), null);
        localObject1 = localObject3;
        if (!interceptFallback(paramWindowState, (KeyEvent)localObject3, paramInt))
        {
          ((KeyEvent)localObject3).recycle();
          localObject1 = null;
        }
        if (i == 0) {
          break label402;
        }
        this.mFallbackActions.put(j, localObject2);
        localObject3 = localObject1;
      }
    }
    for (;;)
    {
      if (DEBUG_INPUT)
      {
        if (localObject3 != null) {
          break label435;
        }
        Slog.d("WindowManager", "No fallback.");
      }
      return (KeyEvent)localObject3;
      label373:
      i = 0;
      break;
      label379:
      i = 0;
      break;
      label385:
      localObject2 = (KeyCharacterMap.FallbackAction)this.mFallbackActions.get(j);
      break label189;
      label402:
      localObject3 = localObject1;
      if (paramKeyEvent.getAction() == 1)
      {
        this.mFallbackActions.remove(j);
        ((KeyCharacterMap.FallbackAction)localObject2).recycle();
        localObject3 = localObject1;
      }
    }
    label435:
    Slog.d("WindowManager", "Performing fallback: " + localObject3);
    return (KeyEvent)localObject3;
  }
  
  public void dump(String paramString, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    boolean bool = false;
    if ("debuglog".equals(paramString))
    {
      if ("1".equals(paramArrayOfString[2])) {
        bool = true;
      }
      DEBUG = bool;
      localLOGV = bool;
      DEBUG_INPUT = bool;
      DEBUG_KEYGUARD = bool;
      DEBUG_LAYOUT = bool;
      DEBUG_STARTING_WINDOW = bool;
      DEBUG_WAKEUP = bool;
      SHOW_STARTING_ANIMATIONS = bool;
      Log.d("WindowManager", " change " + this + " log to " + bool);
      return;
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mSafeMode=");
    paramPrintWriter.print(this.mSafeMode);
    paramPrintWriter.print(" mSystemReady=");
    paramPrintWriter.print(this.mSystemReady);
    paramPrintWriter.print(" mSystemBooted=");
    paramPrintWriter.println(this.mSystemBooted);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mLidState=");
    paramPrintWriter.print(this.mLidState);
    paramPrintWriter.print(" mLidOpenRotation=");
    paramPrintWriter.print(this.mLidOpenRotation);
    paramPrintWriter.print(" mCameraLensCoverState=");
    paramPrintWriter.print(this.mCameraLensCoverState);
    paramPrintWriter.print(" mHdmiPlugged=");
    paramPrintWriter.println(this.mHdmiPlugged);
    if ((this.mLastSystemUiFlags != 0) || (this.mResettingSystemUiFlags != 0))
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mLastSystemUiFlags=0x");
      paramPrintWriter.print(Integer.toHexString(this.mLastSystemUiFlags));
      paramPrintWriter.print(" mResettingSystemUiFlags=0x");
      paramPrintWriter.print(Integer.toHexString(this.mResettingSystemUiFlags));
      paramPrintWriter.print(" mForceClearedSystemUiFlags=0x");
      paramPrintWriter.println(Integer.toHexString(this.mForceClearedSystemUiFlags));
      label294:
      if (this.mLastFocusNeedsMenu)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mLastFocusNeedsMenu=");
        paramPrintWriter.println(this.mLastFocusNeedsMenu);
      }
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mWakeGestureEnabledSetting=");
      paramPrintWriter.println(this.mWakeGestureEnabledSetting);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mSupportAutoRotation=");
      paramPrintWriter.println(this.mSupportAutoRotation);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mUiMode=");
      paramPrintWriter.print(this.mUiMode);
      paramPrintWriter.print(" mDockMode=");
      paramPrintWriter.print(this.mDockMode);
      paramPrintWriter.print(" mEnableCarDockHomeCapture=");
      paramPrintWriter.print(this.mEnableCarDockHomeCapture);
      paramPrintWriter.print(" mCarDockRotation=");
      paramPrintWriter.print(this.mCarDockRotation);
      paramPrintWriter.print(" mDeskDockRotation=");
      paramPrintWriter.println(this.mDeskDockRotation);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mUserRotationMode=");
      paramPrintWriter.print(this.mUserRotationMode);
      paramPrintWriter.print(" mUserRotation=");
      paramPrintWriter.print(this.mUserRotation);
      paramPrintWriter.print(" mAllowAllRotations=");
      paramPrintWriter.println(this.mAllowAllRotations);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mCurrentAppOrientation=");
      paramPrintWriter.println(this.mCurrentAppOrientation);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mCarDockEnablesAccelerometer=");
      paramPrintWriter.print(this.mCarDockEnablesAccelerometer);
      paramPrintWriter.print(" mDeskDockEnablesAccelerometer=");
      paramPrintWriter.println(this.mDeskDockEnablesAccelerometer);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mLidKeyboardAccessibility=");
      paramPrintWriter.print(this.mLidKeyboardAccessibility);
      paramPrintWriter.print(" mLidNavigationAccessibility=");
      paramPrintWriter.print(this.mLidNavigationAccessibility);
      paramPrintWriter.print(" mLidControlsScreenLock=");
      paramPrintWriter.println(this.mLidControlsScreenLock);
      paramPrintWriter.print(" mLidControlsSleep=");
      paramPrintWriter.println(this.mLidControlsSleep);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print(" mLongPressOnBackBehavior=");
      paramPrintWriter.println(this.mLongPressOnBackBehavior);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mShortPressOnPowerBehavior=");
      paramPrintWriter.print(this.mShortPressOnPowerBehavior);
      paramPrintWriter.print(" mLongPressOnPowerBehavior=");
      paramPrintWriter.println(this.mLongPressOnPowerBehavior);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mDoublePressOnPowerBehavior=");
      paramPrintWriter.print(this.mDoublePressOnPowerBehavior);
      paramPrintWriter.print(" mTriplePressOnPowerBehavior=");
      paramPrintWriter.println(this.mTriplePressOnPowerBehavior);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mHasSoftInput=");
      paramPrintWriter.println(this.mHasSoftInput);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mAwake=");
      paramPrintWriter.println(this.mAwake);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mScreenOnEarly=");
      paramPrintWriter.print(this.mScreenOnEarly);
      paramPrintWriter.print(" mScreenOnFully=");
      paramPrintWriter.println(this.mScreenOnFully);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mKeyguardDrawComplete=");
      paramPrintWriter.print(this.mKeyguardDrawComplete);
      paramPrintWriter.print(" mWindowManagerDrawComplete=");
      paramPrintWriter.println(this.mWindowManagerDrawComplete);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mOrientationSensorEnabled=");
      paramPrintWriter.println(this.mOrientationSensorEnabled);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mOverscanScreen=(");
      paramPrintWriter.print(this.mOverscanScreenLeft);
      paramPrintWriter.print(",");
      paramPrintWriter.print(this.mOverscanScreenTop);
      paramPrintWriter.print(") ");
      paramPrintWriter.print(this.mOverscanScreenWidth);
      paramPrintWriter.print("x");
      paramPrintWriter.println(this.mOverscanScreenHeight);
      if ((this.mOverscanLeft == 0) && (this.mOverscanTop == 0)) {
        break label2582;
      }
    }
    for (;;)
    {
      label910:
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mOverscan left=");
      paramPrintWriter.print(this.mOverscanLeft);
      paramPrintWriter.print(" top=");
      paramPrintWriter.print(this.mOverscanTop);
      paramPrintWriter.print(" right=");
      paramPrintWriter.print(this.mOverscanRight);
      paramPrintWriter.print(" bottom=");
      paramPrintWriter.println(this.mOverscanBottom);
      label2582:
      do
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mRestrictedOverscanScreen=(");
        paramPrintWriter.print(this.mRestrictedOverscanScreenLeft);
        paramPrintWriter.print(",");
        paramPrintWriter.print(this.mRestrictedOverscanScreenTop);
        paramPrintWriter.print(") ");
        paramPrintWriter.print(this.mRestrictedOverscanScreenWidth);
        paramPrintWriter.print("x");
        paramPrintWriter.println(this.mRestrictedOverscanScreenHeight);
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mUnrestrictedScreen=(");
        paramPrintWriter.print(this.mUnrestrictedScreenLeft);
        paramPrintWriter.print(",");
        paramPrintWriter.print(this.mUnrestrictedScreenTop);
        paramPrintWriter.print(") ");
        paramPrintWriter.print(this.mUnrestrictedScreenWidth);
        paramPrintWriter.print("x");
        paramPrintWriter.println(this.mUnrestrictedScreenHeight);
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mRestrictedScreen=(");
        paramPrintWriter.print(this.mRestrictedScreenLeft);
        paramPrintWriter.print(",");
        paramPrintWriter.print(this.mRestrictedScreenTop);
        paramPrintWriter.print(") ");
        paramPrintWriter.print(this.mRestrictedScreenWidth);
        paramPrintWriter.print("x");
        paramPrintWriter.println(this.mRestrictedScreenHeight);
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mStableFullscreen=(");
        paramPrintWriter.print(this.mStableFullscreenLeft);
        paramPrintWriter.print(",");
        paramPrintWriter.print(this.mStableFullscreenTop);
        paramPrintWriter.print(")-(");
        paramPrintWriter.print(this.mStableFullscreenRight);
        paramPrintWriter.print(",");
        paramPrintWriter.print(this.mStableFullscreenBottom);
        paramPrintWriter.println(")");
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mStable=(");
        paramPrintWriter.print(this.mStableLeft);
        paramPrintWriter.print(",");
        paramPrintWriter.print(this.mStableTop);
        paramPrintWriter.print(")-(");
        paramPrintWriter.print(this.mStableRight);
        paramPrintWriter.print(",");
        paramPrintWriter.print(this.mStableBottom);
        paramPrintWriter.println(")");
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mSystem=(");
        paramPrintWriter.print(this.mSystemLeft);
        paramPrintWriter.print(",");
        paramPrintWriter.print(this.mSystemTop);
        paramPrintWriter.print(")-(");
        paramPrintWriter.print(this.mSystemRight);
        paramPrintWriter.print(",");
        paramPrintWriter.print(this.mSystemBottom);
        paramPrintWriter.println(")");
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mCur=(");
        paramPrintWriter.print(this.mCurLeft);
        paramPrintWriter.print(",");
        paramPrintWriter.print(this.mCurTop);
        paramPrintWriter.print(")-(");
        paramPrintWriter.print(this.mCurRight);
        paramPrintWriter.print(",");
        paramPrintWriter.print(this.mCurBottom);
        paramPrintWriter.println(")");
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mContent=(");
        paramPrintWriter.print(this.mContentLeft);
        paramPrintWriter.print(",");
        paramPrintWriter.print(this.mContentTop);
        paramPrintWriter.print(")-(");
        paramPrintWriter.print(this.mContentRight);
        paramPrintWriter.print(",");
        paramPrintWriter.print(this.mContentBottom);
        paramPrintWriter.println(")");
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mVoiceContent=(");
        paramPrintWriter.print(this.mVoiceContentLeft);
        paramPrintWriter.print(",");
        paramPrintWriter.print(this.mVoiceContentTop);
        paramPrintWriter.print(")-(");
        paramPrintWriter.print(this.mVoiceContentRight);
        paramPrintWriter.print(",");
        paramPrintWriter.print(this.mVoiceContentBottom);
        paramPrintWriter.println(")");
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mDock=(");
        paramPrintWriter.print(this.mDockLeft);
        paramPrintWriter.print(",");
        paramPrintWriter.print(this.mDockTop);
        paramPrintWriter.print(")-(");
        paramPrintWriter.print(this.mDockRight);
        paramPrintWriter.print(",");
        paramPrintWriter.print(this.mDockBottom);
        paramPrintWriter.println(")");
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mDockLayer=");
        paramPrintWriter.print(this.mDockLayer);
        paramPrintWriter.print(" mStatusBarLayer=");
        paramPrintWriter.println(this.mStatusBarLayer);
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mShowingLockscreen=");
        paramPrintWriter.print(this.mShowingLockscreen);
        paramPrintWriter.print(" mShowingDream=");
        paramPrintWriter.print(this.mShowingDream);
        paramPrintWriter.print(" mDreamingLockscreen=");
        paramPrintWriter.print(this.mDreamingLockscreen);
        paramPrintWriter.print(" mDreamingSleepToken=");
        paramPrintWriter.println(this.mDreamingSleepToken);
        if (this.mLastInputMethodWindow != null)
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("mLastInputMethodWindow=");
          paramPrintWriter.println(this.mLastInputMethodWindow);
        }
        if (this.mLastInputMethodTargetWindow != null)
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("mLastInputMethodTargetWindow=");
          paramPrintWriter.println(this.mLastInputMethodTargetWindow);
        }
        if (this.mStatusBar != null)
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("mStatusBar=");
          paramPrintWriter.print(this.mStatusBar);
          paramPrintWriter.print(" isStatusBarKeyguard=");
          paramPrintWriter.println(isStatusBarKeyguard());
        }
        if (this.mNavigationBar != null)
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("mNavigationBar=");
          paramPrintWriter.println(this.mNavigationBar);
        }
        if (this.mFocusedWindow != null)
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("mFocusedWindow=");
          paramPrintWriter.println(this.mFocusedWindow);
        }
        if (this.mFocusedApp != null)
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("mFocusedApp=");
          paramPrintWriter.println(this.mFocusedApp);
        }
        if (this.mWinDismissingKeyguard != null)
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("mWinDismissingKeyguard=");
          paramPrintWriter.println(this.mWinDismissingKeyguard);
        }
        if (this.mTopFullscreenOpaqueWindowState != null)
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("mTopFullscreenOpaqueWindowState=");
          paramPrintWriter.println(this.mTopFullscreenOpaqueWindowState);
        }
        if (this.mTopFullscreenOpaqueOrDimmingWindowState != null)
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("mTopFullscreenOpaqueOrDimmingWindowState=");
          paramPrintWriter.println(this.mTopFullscreenOpaqueOrDimmingWindowState);
        }
        if (this.mForcingShowNavBar)
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("mForcingShowNavBar=");
          paramPrintWriter.println(this.mForcingShowNavBar);
          paramPrintWriter.print("mForcingShowNavBarLayer=");
          paramPrintWriter.println(this.mForcingShowNavBarLayer);
        }
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mTopIsFullscreen=");
        paramPrintWriter.print(this.mTopIsFullscreen);
        paramPrintWriter.print(" mHideLockScreen=");
        paramPrintWriter.println(this.mHideLockScreen);
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mForceStatusBar=");
        paramPrintWriter.print(this.mForceStatusBar);
        paramPrintWriter.print(" mForceStatusBarFromKeyguard=");
        paramPrintWriter.println(this.mForceStatusBarFromKeyguard);
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mDismissKeyguard=");
        paramPrintWriter.print(this.mDismissKeyguard);
        paramPrintWriter.print(" mCurrentlyDismissingKeyguard=");
        paramPrintWriter.println(this.mCurrentlyDismissingKeyguard);
        paramPrintWriter.print(" mWinDismissingKeyguard=");
        paramPrintWriter.print(this.mWinDismissingKeyguard);
        paramPrintWriter.print(" mHomePressed=");
        paramPrintWriter.println(this.mHomePressed);
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mAllowLockscreenWhenOn=");
        paramPrintWriter.print(this.mAllowLockscreenWhenOn);
        paramPrintWriter.print(" mLockScreenTimeout=");
        paramPrintWriter.print(this.mLockScreenTimeout);
        paramPrintWriter.print(" mLockScreenTimerActive=");
        paramPrintWriter.println(this.mLockScreenTimerActive);
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mEndcallBehavior=");
        paramPrintWriter.print(this.mEndcallBehavior);
        paramPrintWriter.print(" mIncallPowerBehavior=");
        paramPrintWriter.print(this.mIncallPowerBehavior);
        paramPrintWriter.print(" mLongPressOnHomeBehavior=");
        paramPrintWriter.println(this.mLongPressOnHomeBehavior);
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mLandscapeRotation=");
        paramPrintWriter.print(this.mLandscapeRotation);
        paramPrintWriter.print(" mSeascapeRotation=");
        paramPrintWriter.println(this.mSeascapeRotation);
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mPortraitRotation=");
        paramPrintWriter.print(this.mPortraitRotation);
        paramPrintWriter.print(" mUpsideDownRotation=");
        paramPrintWriter.println(this.mUpsideDownRotation);
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mDemoHdmiRotation=");
        paramPrintWriter.print(this.mDemoHdmiRotation);
        paramPrintWriter.print(" mDemoHdmiRotationLock=");
        paramPrintWriter.println(this.mDemoHdmiRotationLock);
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mUndockedHdmiRotation=");
        paramPrintWriter.println(this.mUndockedHdmiRotation);
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mLockPatternVibePattern=");
        paramPrintWriter.println(Arrays.toString(this.mLockPatternVibePattern));
        this.mGlobalKeyManager.dump(paramString, paramPrintWriter);
        this.mStatusBarController.dump(paramPrintWriter, paramString);
        this.mNavigationBarController.dump(paramPrintWriter, paramString);
        PolicyControl.dump(paramString, paramPrintWriter);
        if (this.mWakeGestureListener != null) {
          this.mWakeGestureListener.dump(paramPrintWriter, paramString);
        }
        if (this.mOrientationListener != null) {
          this.mOrientationListener.dump(paramPrintWriter, paramString);
        }
        if (this.mBurnInProtectionHelper != null) {
          this.mBurnInProtectionHelper.dump(paramString, paramPrintWriter);
        }
        if (this.mKeyguardDelegate != null)
        {
          paramPrintWriter.print("isFingerprintAuthenticating=");
          paramPrintWriter.println(this.mKeyguardDelegate.isFingerprintAuthenticating());
          this.mKeyguardDelegate.dump(paramString, paramPrintWriter);
        }
        return;
        if (this.mForceClearedSystemUiFlags == 0) {
          break label294;
        }
        break;
        if (this.mOverscanRight != 0) {
          break label910;
        }
      } while (this.mOverscanBottom == 0);
    }
  }
  
  public void enableKeyguard(boolean paramBoolean)
  {
    if (this.mKeyguardDelegate != null) {
      this.mKeyguardDelegate.setKeyguardEnabled(paramBoolean);
    }
  }
  
  public void enableScreenAfterBoot()
  {
    readLidState();
    applyLidSwitchState();
    updateRotation(true);
  }
  
  public void exitKeyguardSecurely(WindowManagerPolicy.OnKeyguardExitResult paramOnKeyguardExitResult)
  {
    if (this.mKeyguardDelegate != null) {
      this.mKeyguardDelegate.verifyUnlock(paramOnKeyguardExitResult);
    }
  }
  
  public void finishLayoutLw() {}
  
  public int finishPostLayoutPolicyLw()
  {
    if ((this.mWinShowWhenLocked != null) && (this.mTopFullscreenOpaqueWindowState != null) && (this.mWinShowWhenLocked.getAppToken() != this.mTopFullscreenOpaqueWindowState.getAppToken()) && (isKeyguardLocked()))
    {
      this.mTopFullscreenOpaqueWindowState.hideLw(false);
      this.mTopFullscreenOpaqueWindowState = this.mWinShowWhenLocked;
    }
    int k = 0;
    int m = 0;
    boolean bool1 = false;
    WindowManager.LayoutParams localLayoutParams1;
    label128:
    int i;
    boolean bool2;
    label230:
    int j;
    if (this.mTopFullscreenOpaqueWindowState != null)
    {
      localLayoutParams1 = this.mTopFullscreenOpaqueWindowState.getAttrs();
      if (this.mShowingDream) {
        break label572;
      }
      this.mDreamingLockscreen = this.mShowingLockscreen;
      if (this.mDreamingSleepTokenNeeded)
      {
        this.mDreamingSleepTokenNeeded = false;
        this.mHandler.obtainMessage(15, 0, 1).sendToTarget();
      }
      i = k;
      bool2 = bool1;
      if (this.mStatusBar != null)
      {
        if (DEBUG_LAYOUT) {
          Slog.i("WindowManager", "force=" + this.mForceStatusBar + " forcefkg=" + this.mForceStatusBarFromKeyguard + " top=" + this.mTopFullscreenOpaqueWindowState);
        }
        if ((this.mForceStatusBarTransparent) && (!this.mForceStatusBar)) {
          break label601;
        }
        i = 0;
        if (i != 0) {
          break label618;
        }
        this.mStatusBarController.setShowTransparent(false);
        WindowManager.LayoutParams localLayoutParams2 = this.mStatusBar.getAttrs();
        if (localLayoutParams2.height != -1) {
          break label646;
        }
        if (localLayoutParams2.width != -1) {
          break label641;
        }
        j = 1;
        label261:
        if ((!this.mForceStatusBar) && (!this.mForceStatusBarFromKeyguard) && (!this.mForceStatusBarTransparent) && (j == 0)) {
          break label657;
        }
        if (DEBUG_LAYOUT) {
          Slog.v("WindowManager", "Showing status bar: forced");
        }
        k = m;
        if (this.mStatusBarController.setBarShowingLw(true)) {
          k = 1;
        }
        if (!this.mTopIsFullscreen) {
          break label651;
        }
        bool1 = this.mStatusBar.isAnimatingLw();
        label335:
        if ((this.mForceStatusBarFromKeyguard) && (this.mStatusBarController.isTransientShowing())) {
          this.mStatusBarController.updateVisibilityLw(false, this.mLastSystemUiFlags, this.mLastSystemUiFlags);
        }
        i = k;
        bool2 = bool1;
        if (j != 0)
        {
          i = k;
          bool2 = bool1;
          if (this.mNavigationBar != null)
          {
            i = k;
            bool2 = bool1;
            if (this.mNavigationBarController.setBarShowingLw(true))
            {
              i = k | 0x1;
              bool2 = bool1;
            }
          }
        }
      }
      label417:
      j = i;
      if (this.mTopIsFullscreen != bool2)
      {
        j = i;
        if (!bool2) {
          j = i | 0x1;
        }
        this.mTopIsFullscreen = bool2;
      }
      i = j;
      if (this.mKeyguardDelegate != null)
      {
        i = j;
        if (this.mStatusBar != null)
        {
          if (localLOGV) {
            Slog.v("WindowManager", "finishPostLayoutPolicyLw: mHideKeyguard=" + this.mHideLockScreen);
          }
          if ((this.mDismissKeyguard != 0) && (!this.mKeyguardSecure)) {
            break label983;
          }
          if (!this.mHideLockScreen) {
            break label1036;
          }
          this.mKeyguardHidden = true;
          this.mWinDismissingKeyguard = null;
          i = j;
          if (setKeyguardOccludedLw(true)) {
            i = j | 0x7;
          }
        }
      }
    }
    for (;;)
    {
      j = i;
      if ((updateSystemUiVisibilityLw() & 0xC000800E) != 0) {
        j = i | 0x1;
      }
      updateLockScreenTimeout();
      return j;
      localLayoutParams1 = null;
      break;
      label572:
      if (this.mDreamingSleepTokenNeeded) {
        break label128;
      }
      this.mDreamingSleepTokenNeeded = true;
      this.mHandler.obtainMessage(15, 1, 1).sendToTarget();
      break label128;
      label601:
      if (this.mForceStatusBarFromKeyguard) {}
      for (i = 0;; i = 1) {
        break;
      }
      label618:
      if (this.mStatusBar.isVisibleLw()) {
        break label230;
      }
      this.mStatusBarController.setShowTransparent(true);
      break label230;
      label641:
      j = 0;
      break label261;
      label646:
      j = 0;
      break label261;
      label651:
      bool1 = false;
      break label335;
      label657:
      i = k;
      bool2 = bool1;
      if (this.mTopFullscreenOpaqueWindowState == null) {
        break label417;
      }
      i = PolicyControl.getWindowFlags(null, localLayoutParams1);
      if (localLOGV)
      {
        Slog.d("WindowManager", "frame: " + this.mTopFullscreenOpaqueWindowState.getFrameLw() + " shown position: " + this.mTopFullscreenOpaqueWindowState.getShownPositionLw());
        Slog.d("WindowManager", "attr: " + this.mTopFullscreenOpaqueWindowState.getAttrs() + " lp.flags=0x" + Integer.toHexString(i));
      }
      if ((i & 0x400) == 0)
      {
        if ((this.mLastSystemUiFlags & 0x4) == 0) {
          break label844;
        }
        bool1 = true;
      }
      for (;;)
      {
        if (!this.mStatusBarController.isTransientShowing()) {
          break label850;
        }
        i = k;
        bool2 = bool1;
        if (!this.mStatusBarController.setBarShowingLw(true)) {
          break;
        }
        i = 1;
        bool2 = bool1;
        break;
        bool1 = true;
        continue;
        label844:
        bool1 = false;
      }
      label850:
      if ((!bool1) || (this.mWindowManagerInternal.isStackVisible(2))) {}
      while (this.mWindowManagerInternal.isStackVisible(3))
      {
        if (DEBUG_LAYOUT) {
          Slog.v("WindowManager", "** SHOWING status bar: top is not fullscreen");
        }
        i = k;
        bool2 = bool1;
        if (!this.mStatusBarController.setBarShowingLw(true)) {
          break;
        }
        i = 1;
        bool2 = bool1;
        break;
      }
      if (DEBUG_LAYOUT) {
        Slog.v("WindowManager", "** HIDING status bar");
      }
      if (this.mStatusBarController.setBarShowingLw(false))
      {
        i = 1;
        bool2 = bool1;
        break label417;
      }
      i = k;
      bool2 = bool1;
      if (!DEBUG_LAYOUT) {
        break label417;
      }
      Slog.v("WindowManager", "Status bar already hiding");
      i = k;
      bool2 = bool1;
      break label417;
      label983:
      this.mKeyguardHidden = true;
      k = j;
      if (setKeyguardOccludedLw(true)) {
        k = j | 0x7;
      }
      i = k;
      if (this.mKeyguardDelegate.isShowing())
      {
        this.mHandler.post(new Runnable()
        {
          public void run()
          {
            PhoneWindowManager.this.mKeyguardDelegate.keyguardDone(false, false);
          }
        });
        i = k;
        continue;
        label1036:
        if (this.mDismissKeyguard != 0)
        {
          this.mKeyguardHidden = false;
          m = 0;
          bool2 = this.mKeyguardDelegate.isTrusted();
          if (this.mDismissKeyguard == 1) {
            if ((!bool2) || (!this.mKeyguardOccluded) || (this.mKeyguardDelegate == null)) {
              break label1163;
            }
          }
          label1163:
          for (bool1 = this.mKeyguardDelegate.isShowing();; bool1 = false)
          {
            if (bool1) {
              this.mCurrentlyDismissingKeyguard = true;
            }
            m = 1;
            k = j;
            if (!this.mCurrentlyDismissingKeyguard)
            {
              k = j;
              if (setKeyguardOccludedLw(false)) {
                k = j | 0x7;
              }
            }
            i = k;
            if (m == 0) {
              break;
            }
            this.mHandler.post(new -int_finishPostLayoutPolicyLw__LambdaImpl0(bool2));
            i = k;
            break;
          }
        }
        this.mWinDismissingKeyguard = null;
        this.mSecureDismissingKeyguard = false;
        this.mKeyguardHidden = false;
        i = j;
        if (setKeyguardOccludedLw(false)) {
          i = j | 0x7;
        }
      }
    }
  }
  
  public void finishedGoingToSleep(int paramInt)
  {
    EventLog.writeEvent(70000, 0);
    if (DEBUG_WAKEUP) {
      Slog.i("WindowManager", "Finished going to sleep... (why=" + paramInt + ")");
    }
    MetricsLogger.histogram(this.mContext, "screen_timeout", this.mLockScreenTimeout / 1000);
    this.mGoingToSleep = false;
    synchronized (this.mLock)
    {
      this.mAwake = false;
      updateWakeGestureListenerLp();
      updateOrientationListenerLp();
      updateLockScreenTimeout();
      if (this.mKeyguardDelegate != null) {
        this.mKeyguardDelegate.onFinishedGoingToSleep(paramInt, this.mCameraGestureTriggeredDuringGoingToSleep);
      }
      this.mCameraGestureTriggeredDuringGoingToSleep = false;
      return;
    }
  }
  
  public void finishedWakingUp()
  {
    if (DEBUG_WAKEUP) {
      Slog.i("WindowManager", "Finished waking up...");
    }
  }
  
  public int focusChangedLw(WindowManagerPolicy.WindowState paramWindowState1, WindowManagerPolicy.WindowState paramWindowState2)
  {
    this.mFocusedWindow = paramWindowState2;
    if ((updateSystemUiVisibilityLw() & 0xC000800E) != 0) {
      return 1;
    }
    return 0;
  }
  
  public int getConfigDisplayHeight(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return getNonDecorDisplayHeight(paramInt1, paramInt2, paramInt3, paramInt4) - this.mStatusBarHeight;
  }
  
  public int getConfigDisplayWidth(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return getNonDecorDisplayWidth(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void getContentRectLw(Rect paramRect)
  {
    paramRect.set(this.mContentLeft, this.mContentTop, this.mContentRight, this.mContentBottom);
  }
  
  public int getInputMethodWindowVisibleHeightLw()
  {
    return this.mDockBottom - this.mCurBottom;
  }
  
  public boolean getInsetHintLw(WindowManager.LayoutParams paramLayoutParams, Rect paramRect1, int paramInt1, int paramInt2, int paramInt3, Rect paramRect2, Rect paramRect3, Rect paramRect4)
  {
    int j = PolicyControl.getWindowFlags(null, paramLayoutParams);
    int k = PolicyControl.getSystemUiVisibility(null, paramLayoutParams) | paramLayoutParams.subtreeSystemUiVisibility;
    boolean bool;
    int i;
    if (paramRect4 != null)
    {
      bool = shouldUseOutsets(paramLayoutParams, j);
      if (bool)
      {
        i = ScreenShapeHelper.getWindowOutsetBottomPx(this.mContext.getResources());
        if (i > 0)
        {
          if (paramInt1 != 0) {
            break label228;
          }
          paramRect4.bottom += i;
        }
      }
      label72:
      if ((0x10100 & j) != 65792) {
        break label437;
      }
      if ((!canHideNavigationBar()) || ((k & 0x200) == 0)) {
        break label291;
      }
      i = this.mUnrestrictedScreenLeft + this.mUnrestrictedScreenWidth;
      paramInt1 = this.mUnrestrictedScreenTop + this.mUnrestrictedScreenHeight;
      label121:
      if ((k & 0x100) == 0) {
        break label344;
      }
      if ((j & 0x400) == 0) {
        break label315;
      }
      paramRect2.set(this.mStableFullscreenLeft, this.mStableFullscreenTop, i - this.mStableFullscreenRight, paramInt1 - this.mStableFullscreenBottom);
    }
    for (;;)
    {
      paramRect3.set(this.mStableLeft, this.mStableTop, i - this.mStableRight, paramInt1 - this.mStableBottom);
      if (paramRect1 != null)
      {
        calculateRelevantTaskInsets(paramRect1, paramRect2, paramInt2, paramInt3);
        calculateRelevantTaskInsets(paramRect1, paramRect3, paramInt2, paramInt3);
      }
      return this.mForceShowSystemBars;
      bool = false;
      break;
      label228:
      if (paramInt1 == 1)
      {
        paramRect4.right += i;
        break label72;
      }
      if (paramInt1 == 2)
      {
        paramRect4.top += i;
        break label72;
      }
      if (paramInt1 != 3) {
        break label72;
      }
      paramRect4.left += i;
      break label72;
      label291:
      i = this.mRestrictedScreenLeft + this.mRestrictedScreenWidth;
      paramInt1 = this.mRestrictedScreenTop + this.mRestrictedScreenHeight;
      break label121;
      label315:
      paramRect2.set(this.mStableLeft, this.mStableTop, i - this.mStableRight, paramInt1 - this.mStableBottom);
      continue;
      label344:
      if (((j & 0x400) != 0) || ((0x2000000 & j) != 0)) {
        paramRect2.setEmpty();
      } else if ((k & 0x404) == 0) {
        paramRect2.set(this.mCurLeft, this.mCurTop, i - this.mCurRight, paramInt1 - this.mCurBottom);
      } else {
        paramRect2.set(this.mCurLeft, this.mCurTop, i - this.mCurRight, paramInt1 - this.mCurBottom);
      }
    }
    label437:
    paramRect2.setEmpty();
    paramRect3.setEmpty();
    return this.mForceShowSystemBars;
  }
  
  public int getMaxWallpaperLayer()
  {
    return windowTypeToLayerLw(2000);
  }
  
  public int getNonDecorDisplayHeight(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((this.mHasNavigationBar) && ((!this.mNavigationBarCanMove) || (paramInt1 < paramInt2))) {
      return paramInt2 - getNavigationBarHeight(paramInt3, paramInt4);
    }
    return paramInt2;
  }
  
  public int getNonDecorDisplayWidth(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((this.mHasNavigationBar) && (this.mNavigationBarCanMove) && (paramInt1 > paramInt2)) {
      return paramInt1 - getNavigationBarWidth(paramInt3, paramInt4);
    }
    return paramInt1;
  }
  
  public void getNonDecorInsetsLw(int paramInt1, int paramInt2, int paramInt3, Rect paramRect)
  {
    paramRect.setEmpty();
    if (this.mNavigationBar != null)
    {
      paramInt2 = navigationBarPosition(paramInt2, paramInt3, paramInt1);
      if (paramInt2 != 0) {
        break label39;
      }
      paramRect.bottom = getNavigationBarHeight(paramInt1, this.mUiMode);
    }
    label39:
    do
    {
      return;
      if (paramInt2 == 1)
      {
        paramRect.right = getNavigationBarWidth(paramInt1, this.mUiMode);
        return;
      }
    } while (paramInt2 != 2);
    paramRect.left = getNavigationBarWidth(paramInt1, this.mUiMode);
  }
  
  public void getStableInsetsLw(int paramInt1, int paramInt2, int paramInt3, Rect paramRect)
  {
    paramRect.setEmpty();
    getNonDecorInsetsLw(paramInt1, paramInt2, paramInt3, paramRect);
    if (this.mStatusBar != null) {
      paramRect.top = this.mStatusBarHeight;
    }
  }
  
  StatusBarManagerInternal getStatusBarManagerInternal()
  {
    synchronized (this.mServiceAquireLock)
    {
      if (this.mStatusBarManagerInternal == null) {
        this.mStatusBarManagerInternal = ((StatusBarManagerInternal)LocalServices.getService(StatusBarManagerInternal.class));
      }
      StatusBarManagerInternal localStatusBarManagerInternal = this.mStatusBarManagerInternal;
      return localStatusBarManagerInternal;
    }
  }
  
  IStatusBarService getStatusBarService()
  {
    synchronized (this.mServiceAquireLock)
    {
      if (this.mStatusBarService == null) {
        this.mStatusBarService = IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));
      }
      IStatusBarService localIStatusBarService = this.mStatusBarService;
      return localIStatusBarService;
    }
  }
  
  public int getSystemDecorLayerLw()
  {
    if ((this.mStatusBar != null) && (this.mStatusBar.isVisibleLw())) {
      return this.mStatusBar.getSurfaceLayer();
    }
    if ((this.mNavigationBar != null) && (this.mNavigationBar.isVisibleLw())) {
      return this.mNavigationBar.getSurfaceLayer();
    }
    return 0;
  }
  
  TelecomManager getTelecommService()
  {
    return (TelecomManager)this.mContext.getSystemService("telecom");
  }
  
  public int getUserRotationMode()
  {
    if (Settings.System.getIntForUser(this.mContext.getContentResolver(), "accelerometer_rotation", 0, -2) != 0) {
      return 0;
    }
    return 1;
  }
  
  public WindowManagerPolicy.WindowState getWinShowWhenLockedLw()
  {
    return this.mWinShowWhenLocked;
  }
  
  boolean goHome()
  {
    if (!isUserSetupComplete())
    {
      Slog.i("WindowManager", "Not going home because user setup is in progress.");
      return false;
    }
    try
    {
      if (SystemProperties.getInt("persist.sys.uts-test-mode", 0) == 1) {
        Log.d("WindowManager", "UTS-TEST-MODE");
      }
      while (ActivityManagerNative.getDefault().startActivityAsUser(null, null, this.mHomeIntent, this.mHomeIntent.resolveTypeIfNeeded(this.mContext.getContentResolver()), null, null, 0, 1, null, null, -2) == 1)
      {
        return false;
        ActivityManagerNative.getDefault().stopAppSwitches();
        sendCloseSystemWindows();
        Intent localIntent = createHomeDockIntent();
        if (localIntent != null)
        {
          int i = ActivityManagerNative.getDefault().startActivityAsUser(null, null, localIntent, localIntent.resolveTypeIfNeeded(this.mContext.getContentResolver()), null, null, 0, 1, null, null, -2);
          if (i == 1) {
            return false;
          }
        }
      }
    }
    catch (RemoteException localRemoteException) {}
    return true;
  }
  
  protected void handleShortPressOnHome()
  {
    getHdmiControl().turnOnTv();
    if ((this.mDreamManagerInternal != null) && (this.mDreamManagerInternal.isDreaming()))
    {
      this.mDreamManagerInternal.stopDream(false);
      return;
    }
    launchHomeFromHotKey();
  }
  
  public boolean hasNavigationBar()
  {
    return this.mHasNavigationBar;
  }
  
  public void hideBootMessages()
  {
    this.mHandler.sendEmptyMessage(11);
  }
  
  public boolean inKeyguardRestrictedKeyInputMode()
  {
    if (this.mKeyguardDelegate == null) {
      return false;
    }
    return this.mKeyguardDelegate.isInputRestricted();
  }
  
  public void init(Context paramContext, IWindowManager paramIWindowManager, WindowManagerPolicy.WindowManagerFuncs paramWindowManagerFuncs)
  {
    this.mContext = paramContext;
    this.mWindowManager = paramIWindowManager;
    this.mWindowManagerFuncs = paramWindowManagerFuncs;
    this.mWindowManagerInternal = ((WindowManagerInternal)LocalServices.getService(WindowManagerInternal.class));
    this.mActivityManagerInternal = ((ActivityManagerInternal)LocalServices.getService(ActivityManagerInternal.class));
    this.mInputManagerInternal = ((InputManagerInternal)LocalServices.getService(InputManagerInternal.class));
    this.mDreamManagerInternal = ((DreamManagerInternal)LocalServices.getService(DreamManagerInternal.class));
    this.mPowerManagerInternal = ((PowerManagerInternal)LocalServices.getService(PowerManagerInternal.class));
    this.mAppOpsManager = ((AppOpsManager)this.mContext.getSystemService("appops"));
    this.mHasFeatureWatch = this.mContext.getPackageManager().hasSystemFeature("android.hardware.type.watch");
    boolean bool1 = paramContext.getResources().getBoolean(17957030);
    boolean bool2 = SystemProperties.getBoolean("persist.debug.force_burn_in", false);
    int j;
    int k;
    int m;
    int n;
    int i;
    if ((bool1) || (bool2))
    {
      if (!bool2) {
        break label1466;
      }
      j = -8;
      k = 8;
      m = -8;
      n = -4;
      if (!isRoundWindow()) {
        break label1460;
      }
      i = 6;
    }
    for (;;)
    {
      this.mBurnInProtectionHelper = new BurnInProtectionHelper(paramContext, j, k, m, n, i);
      this.mHandler = new PolicyHandler(null);
      this.mWakeGestureListener = new MyWakeGestureListener(this.mContext, this.mHandler);
      this.mOrientationListener = new MyOrientationListener(this.mContext, this.mHandler);
      try
      {
        this.mOrientationListener.setCurrentRotation(paramIWindowManager.getRotation());
        this.mSettingsObserver = new SettingsObserver(this.mHandler);
        this.mSettingsObserver.observe();
        this.mShortcutManager = new ShortcutManager(paramContext);
        this.mUiMode = paramContext.getResources().getInteger(17694795);
        this.mHomeIntent = new Intent("android.intent.action.MAIN", null);
        this.mHomeIntent.addCategory("android.intent.category.HOME");
        this.mHomeIntent.addFlags(270532608);
        this.mEnableCarDockHomeCapture = paramContext.getResources().getBoolean(17956926);
        this.mCarDockIntent = new Intent("android.intent.action.MAIN", null);
        this.mCarDockIntent.addCategory("android.intent.category.CAR_DOCK");
        this.mCarDockIntent.addFlags(270532608);
        this.mDeskDockIntent = new Intent("android.intent.action.MAIN", null);
        this.mDeskDockIntent.addCategory("android.intent.category.DESK_DOCK");
        this.mDeskDockIntent.addFlags(270532608);
        this.mPowerManager = ((PowerManager)paramContext.getSystemService("power"));
        this.mBroadcastWakeLock = this.mPowerManager.newWakeLock(1, "PhoneWindowManager.mBroadcastWakeLock");
        this.mPowerKeyWakeLock = this.mPowerManager.newWakeLock(1, "PhoneWindowManager.mPowerKeyWakeLock");
        this.mEnableShiftMenuBugReports = "1".equals(SystemProperties.get("ro.debuggable"));
        this.mSupportAutoRotation = this.mContext.getResources().getBoolean(17956919);
        this.mLidOpenRotation = readRotation(17694787);
        this.mCarDockRotation = readRotation(17694792);
        this.mDeskDockRotation = readRotation(17694790);
        this.mUndockedHdmiRotation = readRotation(17694794);
        this.mCarDockEnablesAccelerometer = this.mContext.getResources().getBoolean(17956925);
        this.mDeskDockEnablesAccelerometer = this.mContext.getResources().getBoolean(17956924);
        this.mLidKeyboardAccessibility = this.mContext.getResources().getInteger(17694788);
        this.mLidNavigationAccessibility = this.mContext.getResources().getInteger(17694789);
        this.mLidControlsScreenLock = this.mContext.getResources().getBoolean(17956922);
        this.mLidControlsSleep = this.mContext.getResources().getBoolean(17956923);
        this.mTranslucentDecorEnabled = this.mContext.getResources().getBoolean(17956938);
        this.mAllowTheaterModeWakeFromKey = this.mContext.getResources().getBoolean(17956911);
        if (!this.mAllowTheaterModeWakeFromKey)
        {
          bool1 = this.mContext.getResources().getBoolean(17956910);
          label718:
          this.mAllowTheaterModeWakeFromPowerKey = bool1;
          this.mAllowTheaterModeWakeFromMotion = this.mContext.getResources().getBoolean(17956912);
          this.mAllowTheaterModeWakeFromMotionWhenNotDreaming = this.mContext.getResources().getBoolean(17956913);
          this.mAllowTheaterModeWakeFromCameraLens = this.mContext.getResources().getBoolean(17956909);
          this.mAllowTheaterModeWakeFromLidSwitch = this.mContext.getResources().getBoolean(17956914);
          this.mAllowTheaterModeWakeFromWakeGesture = this.mContext.getResources().getBoolean(17956908);
          this.mGoToSleepOnButtonPressTheaterMode = this.mContext.getResources().getBoolean(17956917);
          this.mSupportLongPressPowerWhenNonInteractive = this.mContext.getResources().getBoolean(17956918);
          this.mLongPressOnBackBehavior = this.mContext.getResources().getInteger(17694801);
          this.mShortPressOnPowerBehavior = this.mContext.getResources().getInteger(17694802);
          this.mLongPressOnPowerBehavior = this.mContext.getResources().getInteger(17694800);
          this.mDoublePressOnPowerBehavior = this.mContext.getResources().getInteger(17694803);
          this.mTriplePressOnPowerBehavior = this.mContext.getResources().getInteger(17694804);
          this.mShortPressOnSleepBehavior = this.mContext.getResources().getInteger(17694805);
          if (AudioSystem.getPlatformType(this.mContext) != 2) {
            break label1525;
          }
        }
        label1460:
        label1466:
        label1525:
        for (bool1 = true;; bool1 = false)
        {
          this.mUseTvRouting = bool1;
          readConfigurationDependentBehaviors();
          this.mAccessibilityManager = ((AccessibilityManager)paramContext.getSystemService("accessibility"));
          paramIWindowManager = new IntentFilter();
          paramIWindowManager.addAction(UiModeManager.ACTION_ENTER_CAR_MODE);
          paramIWindowManager.addAction(UiModeManager.ACTION_EXIT_CAR_MODE);
          paramIWindowManager.addAction(UiModeManager.ACTION_ENTER_DESK_MODE);
          paramIWindowManager.addAction(UiModeManager.ACTION_EXIT_DESK_MODE);
          paramIWindowManager.addAction("android.intent.action.DOCK_EVENT");
          paramIWindowManager = paramContext.registerReceiver(this.mDockReceiver, paramIWindowManager);
          if (paramIWindowManager != null) {
            this.mDockMode = paramIWindowManager.getIntExtra("android.intent.extra.DOCK_STATE", 0);
          }
          paramIWindowManager = new IntentFilter();
          paramIWindowManager.addAction("android.intent.action.DREAMING_STARTED");
          paramIWindowManager.addAction("android.intent.action.DREAMING_STOPPED");
          paramContext.registerReceiver(this.mDreamReceiver, paramIWindowManager);
          paramIWindowManager = new IntentFilter("android.intent.action.USER_SWITCHED");
          paramContext.registerReceiver(this.mMultiuserReceiver, paramIWindowManager);
          this.mSystemGestures = new SystemGesturesPointerEventListener(paramContext, new SystemGesturesPointerEventListener.Callbacks()
          {
            public void onDebug() {}
            
            public void onDown()
            {
              PhoneWindowManager.this.mOrientationListener.onTouchStart();
            }
            
            public void onFling(int paramAnonymousInt)
            {
              if (PhoneWindowManager.this.mPowerManagerInternal != null) {
                PhoneWindowManager.this.mPowerManagerInternal.powerHint(2, paramAnonymousInt);
              }
            }
            
            public void onMouseHoverAtBottom()
            {
              PhoneWindowManager.this.mHandler.removeMessages(16);
              Message localMessage = PhoneWindowManager.this.mHandler.obtainMessage(16);
              localMessage.arg1 = 1;
              PhoneWindowManager.this.mHandler.sendMessageDelayed(localMessage, 500L);
            }
            
            public void onMouseHoverAtTop()
            {
              PhoneWindowManager.this.mHandler.removeMessages(16);
              Message localMessage = PhoneWindowManager.this.mHandler.obtainMessage(16);
              localMessage.arg1 = 0;
              PhoneWindowManager.this.mHandler.sendMessageDelayed(localMessage, 500L);
            }
            
            public void onMouseLeaveFromEdge()
            {
              PhoneWindowManager.this.mHandler.removeMessages(16);
            }
            
            public void onSwipeFromBottom()
            {
              if ((PhoneWindowManager.this.mNavigationBar != null) && (PhoneWindowManager.this.mNavigationBarPosition == 0)) {
                PhoneWindowManager.-wrap17(PhoneWindowManager.this, PhoneWindowManager.this.mNavigationBar);
              }
            }
            
            public void onSwipeFromLeft()
            {
              if ((PhoneWindowManager.this.mNavigationBar != null) && (PhoneWindowManager.this.mNavigationBarPosition == 2)) {
                PhoneWindowManager.-wrap17(PhoneWindowManager.this, PhoneWindowManager.this.mNavigationBar);
              }
            }
            
            public void onSwipeFromRight()
            {
              if ((PhoneWindowManager.this.mNavigationBar != null) && (PhoneWindowManager.this.mNavigationBarPosition == 1)) {
                PhoneWindowManager.-wrap17(PhoneWindowManager.this, PhoneWindowManager.this.mNavigationBar);
              }
            }
            
            public void onSwipeFromTop()
            {
              if (PhoneWindowManager.this.mStatusBar != null) {
                PhoneWindowManager.-wrap17(PhoneWindowManager.this, PhoneWindowManager.this.mStatusBar);
              }
            }
            
            public void onUpOrCancel()
            {
              PhoneWindowManager.this.mOrientationListener.onTouchEnd();
            }
          });
          this.mImmersiveModeConfirmation = new ImmersiveModeConfirmation(this.mContext);
          this.mWindowManagerFuncs.registerPointerEventListener(this.mSystemGestures);
          this.mVibrator = ((Vibrator)paramContext.getSystemService("vibrator"));
          paramIWindowManager = new IntentFilter("org.codeaurora.intent.action.WIFI_DISPLAY_VIDEO");
          paramContext.registerReceiver(this.mWifiDisplayReceiver, paramIWindowManager);
          this.mLongPressVibePattern = getLongIntArray(this.mContext.getResources(), 17236001);
          this.mVirtualKeyVibePattern = getLongIntArray(this.mContext.getResources(), 17236002);
          this.mKeyboardTapVibePattern = getLongIntArray(this.mContext.getResources(), 17236003);
          this.mClockTickVibePattern = getLongIntArray(this.mContext.getResources(), 17236004);
          this.mCalendarDateVibePattern = getLongIntArray(this.mContext.getResources(), 17236005);
          this.mSafeModeDisabledVibePattern = getLongIntArray(this.mContext.getResources(), 17236006);
          this.mSafeModeEnabledVibePattern = getLongIntArray(this.mContext.getResources(), 17236007);
          this.mContextClickVibePattern = getLongIntArray(this.mContext.getResources(), 17236009);
          this.mVibrateOnTouchIntensity = Settings.System.getIntForUser(this.mContext.getContentResolver(), "vibrate_on_touch_intensity", 1, -2);
          this.mLockPatternVibePattern = new long[this.mVirtualKeyVibePattern.length + 1];
          this.mLockPatternVibePattern[0] = ((this.mVibrateOnTouchIntensity + 1) * -1);
          System.arraycopy(this.mVirtualKeyVibePattern, 0, this.mLockPatternVibePattern, 1, this.mVirtualKeyVibePattern.length);
          this.mScreenshotChordEnabled = this.mContext.getResources().getBoolean(17956906);
          this.mGlobalKeyManager = new GlobalKeyManager(this.mContext);
          initializeHdmiState();
          if (!this.mPowerManager.isInteractive())
          {
            startedGoingToSleep(2);
            finishedGoingToSleep(2);
          }
          this.mWindowManagerInternal.registerAppTransitionListener(this.mStatusBarController.getAppTransitionListener());
          return;
          i = -1;
          break;
          paramWindowManagerFuncs = paramContext.getResources();
          j = paramWindowManagerFuncs.getInteger(17694877);
          k = paramWindowManagerFuncs.getInteger(17694878);
          m = paramWindowManagerFuncs.getInteger(17694879);
          n = paramWindowManagerFuncs.getInteger(17694880);
          i = paramWindowManagerFuncs.getInteger(17694876);
          break;
          bool1 = true;
          break label718;
        }
      }
      catch (RemoteException paramIWindowManager)
      {
        for (;;) {}
      }
    }
  }
  
  /* Error */
  void initializeHdmiState()
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore 5
    //   3: iconst_0
    //   4: istore_3
    //   5: iconst_0
    //   6: istore_1
    //   7: iload_3
    //   8: istore_2
    //   9: new 3647	java/io/File
    //   12: dup
    //   13: ldc_w 3649
    //   16: invokespecial 3650	java/io/File:<init>	(Ljava/lang/String;)V
    //   19: invokevirtual 3653	java/io/File:exists	()Z
    //   22: ifeq +90 -> 112
    //   25: aload_0
    //   26: getfield 970	com/android/server/policy/PhoneWindowManager:mHDMIObserver	Landroid/os/UEventObserver;
    //   29: ldc_w 3655
    //   32: invokevirtual 3660	android/os/UEventObserver:startObserving	(Ljava/lang/String;)V
    //   35: aconst_null
    //   36: astore 9
    //   38: aconst_null
    //   39: astore 6
    //   41: aconst_null
    //   42: astore 8
    //   44: new 3662	java/io/FileReader
    //   47: dup
    //   48: ldc_w 3664
    //   51: invokespecial 3665	java/io/FileReader:<init>	(Ljava/lang/String;)V
    //   54: astore 7
    //   56: bipush 15
    //   58: newarray <illegal type>
    //   60: astore 6
    //   62: aload 7
    //   64: aload 6
    //   66: invokevirtual 3669	java/io/FileReader:read	([C)I
    //   69: istore_2
    //   70: iload_2
    //   71: iconst_1
    //   72: if_icmple +26 -> 98
    //   75: new 775	java/lang/String
    //   78: dup
    //   79: aload 6
    //   81: iconst_0
    //   82: iload_2
    //   83: iconst_1
    //   84: isub
    //   85: invokespecial 3672	java/lang/String:<init>	([CII)V
    //   88: invokestatic 3675	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   91: istore_1
    //   92: iload_1
    //   93: ifeq +50 -> 143
    //   96: iconst_1
    //   97: istore_1
    //   98: iload_1
    //   99: istore_2
    //   100: aload 7
    //   102: ifnull +10 -> 112
    //   105: aload 7
    //   107: invokevirtual 3678	java/io/FileReader:close	()V
    //   110: iload_1
    //   111: istore_2
    //   112: iload_2
    //   113: ifeq +188 -> 301
    //   116: iconst_0
    //   117: istore 4
    //   119: aload_0
    //   120: iload 4
    //   122: putfield 2950	com/android/server/policy/PhoneWindowManager:mHdmiPlugged	Z
    //   125: aload_0
    //   126: getfield 2950	com/android/server/policy/PhoneWindowManager:mHdmiPlugged	Z
    //   129: ifeq +178 -> 307
    //   132: iload 5
    //   134: istore 4
    //   136: aload_0
    //   137: iload 4
    //   139: invokevirtual 3681	com/android/server/policy/PhoneWindowManager:setHdmiPlugged	(Z)V
    //   142: return
    //   143: iconst_0
    //   144: istore_1
    //   145: goto -47 -> 98
    //   148: astore 6
    //   150: iload_1
    //   151: istore_2
    //   152: goto -40 -> 112
    //   155: astore 6
    //   157: aload 8
    //   159: astore 7
    //   161: aload 6
    //   163: astore 8
    //   165: aload 7
    //   167: astore 6
    //   169: ldc -17
    //   171: new 1347	java/lang/StringBuilder
    //   174: dup
    //   175: invokespecial 1348	java/lang/StringBuilder:<init>	()V
    //   178: ldc_w 3683
    //   181: invokevirtual 1353	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   184: aload 8
    //   186: invokevirtual 1796	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   189: invokevirtual 1383	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   192: invokestatic 3684	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   195: pop
    //   196: iload_3
    //   197: istore_2
    //   198: aload 7
    //   200: ifnull -88 -> 112
    //   203: aload 7
    //   205: invokevirtual 3678	java/io/FileReader:close	()V
    //   208: iload_3
    //   209: istore_2
    //   210: goto -98 -> 112
    //   213: astore 6
    //   215: iload_3
    //   216: istore_2
    //   217: goto -105 -> 112
    //   220: astore 8
    //   222: aload 9
    //   224: astore 7
    //   226: aload 7
    //   228: astore 6
    //   230: ldc -17
    //   232: new 1347	java/lang/StringBuilder
    //   235: dup
    //   236: invokespecial 1348	java/lang/StringBuilder:<init>	()V
    //   239: ldc_w 3683
    //   242: invokevirtual 1353	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   245: aload 8
    //   247: invokevirtual 1796	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   250: invokevirtual 1383	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   253: invokestatic 3684	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   256: pop
    //   257: iload_3
    //   258: istore_2
    //   259: aload 7
    //   261: ifnull -149 -> 112
    //   264: aload 7
    //   266: invokevirtual 3678	java/io/FileReader:close	()V
    //   269: iload_3
    //   270: istore_2
    //   271: goto -159 -> 112
    //   274: astore 6
    //   276: iload_3
    //   277: istore_2
    //   278: goto -166 -> 112
    //   281: astore 7
    //   283: aload 6
    //   285: ifnull +8 -> 293
    //   288: aload 6
    //   290: invokevirtual 3678	java/io/FileReader:close	()V
    //   293: aload 7
    //   295: athrow
    //   296: astore 6
    //   298: goto -5 -> 293
    //   301: iconst_1
    //   302: istore 4
    //   304: goto -185 -> 119
    //   307: iconst_1
    //   308: istore 4
    //   310: goto -174 -> 136
    //   313: astore 8
    //   315: aload 7
    //   317: astore 6
    //   319: aload 8
    //   321: astore 7
    //   323: goto -40 -> 283
    //   326: astore 8
    //   328: goto -102 -> 226
    //   331: astore 8
    //   333: goto -168 -> 165
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	336	0	this	PhoneWindowManager
    //   6	145	1	i	int
    //   8	270	2	j	int
    //   4	273	3	k	int
    //   117	192	4	bool1	boolean
    //   1	132	5	bool2	boolean
    //   39	41	6	arrayOfChar	char[]
    //   148	1	6	localIOException1	java.io.IOException
    //   155	7	6	localNumberFormatException1	NumberFormatException
    //   167	1	6	localObject1	Object
    //   213	1	6	localIOException2	java.io.IOException
    //   228	1	6	localObject2	Object
    //   274	15	6	localIOException3	java.io.IOException
    //   296	1	6	localIOException4	java.io.IOException
    //   317	1	6	localObject3	Object
    //   54	211	7	localObject4	Object
    //   281	35	7	localObject5	Object
    //   321	1	7	localObject6	Object
    //   42	143	8	localNumberFormatException2	NumberFormatException
    //   220	26	8	localIOException5	java.io.IOException
    //   313	7	8	localObject7	Object
    //   326	1	8	localIOException6	java.io.IOException
    //   331	1	8	localNumberFormatException3	NumberFormatException
    //   36	187	9	localObject8	Object
    // Exception table:
    //   from	to	target	type
    //   105	110	148	java/io/IOException
    //   44	56	155	java/lang/NumberFormatException
    //   203	208	213	java/io/IOException
    //   44	56	220	java/io/IOException
    //   264	269	274	java/io/IOException
    //   44	56	281	finally
    //   169	196	281	finally
    //   230	257	281	finally
    //   288	293	296	java/io/IOException
    //   56	70	313	finally
    //   75	92	313	finally
    //   56	70	326	java/io/IOException
    //   75	92	326	java/io/IOException
    //   56	70	331	java/lang/NumberFormatException
    //   75	92	331	java/lang/NumberFormatException
  }
  
  public long interceptKeyBeforeDispatching(WindowManagerPolicy.WindowState paramWindowState, KeyEvent paramKeyEvent, int paramInt)
  {
    boolean bool2 = keyguardOn();
    int m = paramKeyEvent.getKeyCode();
    int k = paramKeyEvent.getRepeatCount();
    int n = paramKeyEvent.getMetaState();
    paramInt = paramKeyEvent.getFlags();
    boolean bool1;
    boolean bool3;
    if (paramKeyEvent.getAction() == 0)
    {
      bool1 = true;
      bool3 = paramKeyEvent.isCanceled();
      if (DEBUG_INPUT) {
        Log.d("WindowManager", "interceptKeyTi keyCode=" + m + " down=" + bool1 + " repeatCount=" + k + " keyguardOn=" + bool2 + " mHomePressed=" + this.mHomePressed + " canceled=" + bool3);
      }
      if (!SystemProperties.getBoolean("ro.alarm_boot", false)) {
        break label189;
      }
      if ((m != 3) && (m != 84)) {
        break label168;
      }
    }
    label168:
    while ((m == 82) || (m == 187) || (m == 4))
    {
      return -1L;
      bool1 = false;
      break;
    }
    label189:
    long l1;
    long l2;
    if ((this.mScreenshotChordEnabled) && ((paramInt & 0x400) == 0))
    {
      if ((!this.mScreenshotChordVolumeDownKeyTriggered) || (this.mScreenshotChordPowerKeyTriggered)) {}
      while ((m == 25) && (this.mScreenshotChordVolumeDownKeyConsumed))
      {
        if (!bool1) {
          this.mScreenshotChordVolumeDownKeyConsumed = false;
        }
        return -1L;
        l1 = SystemClock.uptimeMillis();
        l2 = this.mScreenshotChordVolumeDownKeyTime + 150L;
        if (l1 < l2) {
          return l2 - l1;
        }
      }
    }
    if ((!this.mPendingMetaAction) || (KeyEvent.isMetaKey(m))) {
      if ((this.mPendingCapsLockToggle) && (!KeyEvent.isMetaKey(m))) {
        break label349;
      }
    }
    label349:
    int i;
    label478:
    int j;
    for (;;)
    {
      if (m == 3)
      {
        if (!bool1)
        {
          cancelPreloadRecentApps();
          this.mHomePressed = false;
          if (this.mHomeConsumed)
          {
            this.mHomeConsumed = false;
            return -1L;
            this.mPendingMetaAction = false;
            break;
            if (KeyEvent.isAltKey(m)) {
              continue;
            }
            this.mPendingCapsLockToggle = false;
            continue;
          }
          if (bool3)
          {
            Log.i("WindowManager", "Ignoring HOME; event canceled.");
            return -1L;
          }
          if (this.mDoubleTapOnHomeBehavior != 0)
          {
            this.mHandler.removeCallbacks(this.mHomeDoubleTapTimeoutRunnable);
            this.mHomeDoubleTapPending = true;
            this.mHandler.postDelayed(this.mHomeDoubleTapTimeoutRunnable, ViewConfiguration.getDoubleTapTimeout());
            return -1L;
          }
          handleShortPressOnHome();
          return -1L;
        }
        if (paramWindowState != null)
        {
          paramWindowState = paramWindowState.getAttrs();
          if (paramWindowState == null) {
            break label524;
          }
          i = paramWindowState.type;
          if ((i != 2029) && (i != 2009)) {
            break label478;
          }
        }
        while ((paramWindowState.privateFlags & 0x400) != 0)
        {
          return 0L;
          paramWindowState = null;
          break;
        }
        j = WINDOW_TYPES_WHERE_HOME_DOESNT_WORK.length;
        paramInt = 0;
        while (paramInt < j)
        {
          if (i == WINDOW_TYPES_WHERE_HOME_DOESNT_WORK[paramInt]) {
            return -1L;
          }
          paramInt += 1;
        }
        label524:
        if (k == 0)
        {
          this.mHomePressed = true;
          if (this.mHomeDoubleTapPending)
          {
            this.mHomeDoubleTapPending = false;
            this.mHandler.removeCallbacks(this.mHomeDoubleTapTimeoutRunnable);
            handleDoubleTapOnHome();
          }
        }
        for (;;)
        {
          return -1L;
          if ((this.mLongPressOnHomeBehavior == 1) || (this.mDoubleTapOnHomeBehavior == 1))
          {
            preloadRecentApps();
            continue;
            if (((paramKeyEvent.getFlags() & 0x80) != 0) && (!bool2)) {
              handleLongPressOnHome(paramKeyEvent.getDeviceId());
            }
          }
        }
      }
    }
    if (m == 82)
    {
      if ((bool1) && (k == 0) && (this.mEnableShiftMenuBugReports) && ((n & 0x1) == 1))
      {
        paramWindowState = new Intent("android.intent.action.BUG_REPORT");
        this.mContext.sendOrderedBroadcastAsUser(paramWindowState, UserHandle.CURRENT, null, null, null, 0, null, null);
        return -1L;
      }
    }
    else
    {
      if (m == 84)
      {
        if (bool1) {
          if (k == 0)
          {
            this.mSearchKeyShortcutPending = true;
            this.mConsumeSearchKeyUp = false;
          }
        }
        do
        {
          return 0L;
          this.mSearchKeyShortcutPending = false;
        } while (!this.mConsumeSearchKeyUp);
        this.mConsumeSearchKeyUp = false;
        return -1L;
      }
      if (m == 187)
      {
        if (!bool2)
        {
          if ((!bool1) || (k != 0)) {
            break label760;
          }
          preloadRecentApps();
        }
        for (;;)
        {
          return -1L;
          label760:
          if (!bool1) {
            toggleRecentApps();
          }
        }
      }
      if ((m != 42) || (!paramKeyEvent.isMetaPressed())) {
        break label865;
      }
      if (bool1)
      {
        paramWindowState = getStatusBarService();
        if (paramWindowState == null) {}
      }
    }
    for (;;)
    {
      try
      {
        paramWindowState.expandNotificationsPanel(0);
        i = 0;
        j = 0;
        paramInt = j;
        if (KeyEvent.isModifierKey(m))
        {
          if (this.mPendingCapsLockToggle) {
            break label1407;
          }
          this.mInitialMetaState = this.mMetaState;
          this.mPendingCapsLockToggle = true;
          paramInt = j;
        }
        this.mMetaState = n;
        if (paramInt == 0) {
          break label1494;
        }
        return -1L;
      }
      catch (RemoteException paramWindowState)
      {
        continue;
      }
      label865:
      if ((m == 47) && (paramKeyEvent.isMetaPressed()) && (paramKeyEvent.isCtrlPressed()))
      {
        if ((!bool1) || (k != 0)) {
          continue;
        }
        if (paramKeyEvent.isShiftPressed()) {}
        for (paramInt = 2;; paramInt = 1)
        {
          this.mScreenshotRunnable.setScreenshotType(paramInt);
          this.mHandler.post(this.mScreenshotRunnable);
          return -1L;
        }
      }
      if ((m == 76) && (paramKeyEvent.isMetaPressed()))
      {
        if ((!bool1) || (k != 0) || (isKeyguardLocked())) {
          continue;
        }
        toggleKeyboardShortcutsMenu(paramKeyEvent.getDeviceId());
        continue;
      }
      if (m == 219)
      {
        if (bool1) {
          if (k == 0) {
            this.mAssistKeyLongPressed = false;
          }
        }
        for (;;)
        {
          return -1L;
          if (k == 1)
          {
            this.mAssistKeyLongPressed = true;
            if (!bool2)
            {
              launchAssistLongPressAction();
              continue;
              if (this.mAssistKeyLongPressed) {
                this.mAssistKeyLongPressed = false;
              } else if (!bool2) {
                launchAssistAction(null, paramKeyEvent.getDeviceId());
              }
            }
          }
        }
      }
      if (m == 231)
      {
        if (bool1) {
          continue;
        }
        if (!bool2)
        {
          paramWindowState = new Intent("android.speech.action.WEB_SEARCH");
          label1087:
          startActivityAsUser(paramWindowState, UserHandle.CURRENT_OR_SELF);
          continue;
        }
        paramWindowState = IDeviceIdleController.Stub.asInterface(ServiceManager.getService("deviceidle"));
        if (paramWindowState == null) {}
      }
      try
      {
        paramWindowState.exitIdle("voice-search");
        paramWindowState = new Intent("android.speech.action.VOICE_SEARCH_HANDS_FREE");
        paramWindowState.putExtra("android.speech.extras.EXTRA_SECURE", true);
        break label1087;
        if (m == 120)
        {
          if ((bool1) && (k == 0))
          {
            this.mScreenshotRunnable.setScreenshotType(1);
            this.mHandler.post(this.mScreenshotRunnable);
          }
          return -1L;
        }
        if ((m == 221) || (m == 220))
        {
          if (bool1) {
            if (m != 221) {
              break label1361;
            }
          }
          label1361:
          for (paramInt = 1;; paramInt = -1)
          {
            if (Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_brightness_mode", 0, -3) != 0) {
              Settings.System.putIntForUser(this.mContext.getContentResolver(), "screen_brightness_mode", 0, -3);
            }
            i = this.mPowerManager.getMinimumScreenBrightnessSetting();
            j = this.mPowerManager.getMaximumScreenBrightnessSetting();
            k = (j - i + 10 - 1) / 10;
            paramInt = Math.max(i, Math.min(j, Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_brightness", this.mPowerManager.getDefaultScreenBrightnessSetting(), -3) + k * paramInt));
            Settings.System.putIntForUser(this.mContext.getContentResolver(), "screen_brightness", paramInt, -3);
            startActivityAsUser(new Intent("android.intent.action.SHOW_BRIGHTNESS_DIALOG"), UserHandle.CURRENT_OR_SELF);
            return -1L;
          }
        }
        if ((m == 24) || (m == 25)) {}
        while (this.mUseTvRouting)
        {
          dispatchDirectAudioEvent(paramKeyEvent);
          return -1L;
          if (m != 164) {
            break;
          }
        }
        label1407:
        paramInt = j;
        if (paramKeyEvent.getAction() != 1) {
          continue;
        }
        j = this.mMetaState & 0x32;
        int i1 = this.mMetaState & 0x70000;
        paramInt = i;
        if (i1 != 0)
        {
          paramInt = i;
          if (j != 0)
          {
            paramInt = i;
            if (this.mInitialMetaState == (this.mMetaState ^ (j | i1)))
            {
              this.mInputManagerInternal.toggleCapsLock(paramKeyEvent.getDeviceId());
              paramInt = 1;
            }
          }
        }
        this.mPendingCapsLockToggle = false;
        continue;
        label1494:
        if (KeyEvent.isMetaKey(m))
        {
          if (bool1) {
            this.mPendingMetaAction = true;
          }
          for (;;)
          {
            return -1L;
            if (this.mPendingMetaAction) {
              launchAssistAction("android.intent.extra.ASSIST_INPUT_HINT_KEYBOARD", paramKeyEvent.getDeviceId());
            }
          }
        }
        if (this.mSearchKeyShortcutPending)
        {
          paramWindowState = paramKeyEvent.getKeyCharacterMap();
          if (paramWindowState.isPrintingKey(m))
          {
            this.mConsumeSearchKeyUp = true;
            this.mSearchKeyShortcutPending = false;
            if ((!bool1) || (k != 0) || (bool2)) {}
            for (;;)
            {
              return -1L;
              paramWindowState = this.mShortcutManager.getIntent(paramWindowState, m, n);
              if (paramWindowState != null)
              {
                paramWindowState.addFlags(268435456);
                try
                {
                  startActivityAsUser(paramWindowState, UserHandle.CURRENT);
                  dismissKeyboardShortcutsMenu();
                }
                catch (ActivityNotFoundException paramWindowState)
                {
                  Slog.w("WindowManager", "Dropping shortcut key combination because the activity to which it is registered was not found: SEARCH+" + KeyEvent.keyCodeToString(m), paramWindowState);
                }
              }
              else
              {
                Slog.i("WindowManager", "Dropping unregistered shortcut key combination: SEARCH+" + KeyEvent.keyCodeToString(m));
              }
            }
          }
        }
        if ((!bool1) || (k != 0) || (bool2))
        {
          if ((bool1) && (k == 0) && (!bool2)) {
            break label1918;
          }
          label1725:
          if ((!bool1) || (k != 0) || (m != 61)) {
            break label2047;
          }
          if ((this.mRecentAppsHeldModifiers == 0) && (!bool2)) {
            break label2008;
          }
          label1754:
          if ((!bool1) || (k != 0) || ((m != 204) && ((m != 62) || ((0x70000 & n) == 0)))) {
            break label2089;
          }
          if ((n & 0xC1) != 0) {
            break label2083;
          }
        }
        label1918:
        label2008:
        label2047:
        label2083:
        for (bool1 = true;; bool1 = false)
        {
          this.mWindowManagerFuncs.switchInputMethod(bool1);
          return -1L;
          if ((0x10000 & n) == 0) {
            break;
          }
          paramWindowState = paramKeyEvent.getKeyCharacterMap();
          if (!paramWindowState.isPrintingKey(m)) {
            break;
          }
          paramWindowState = this.mShortcutManager.getIntent(paramWindowState, m, 0xFFF8FFFF & n);
          if (paramWindowState == null) {
            break;
          }
          paramWindowState.addFlags(268435456);
          try
          {
            startActivityAsUser(paramWindowState, UserHandle.CURRENT);
            dismissKeyboardShortcutsMenu();
            return -1L;
          }
          catch (ActivityNotFoundException paramWindowState)
          {
            for (;;)
            {
              Slog.w("WindowManager", "Dropping shortcut key combination because the activity to which it is registered was not found: META+" + KeyEvent.keyCodeToString(m), paramWindowState);
            }
          }
          paramWindowState = (String)sApplicationLaunchKeyCategories.get(m);
          if (paramWindowState == null) {
            break label1725;
          }
          paramKeyEvent = Intent.makeMainSelectorActivity("android.intent.action.MAIN", paramWindowState);
          paramKeyEvent.setFlags(268435456);
          try
          {
            startActivityAsUser(paramKeyEvent, UserHandle.CURRENT);
            dismissKeyboardShortcutsMenu();
            return -1L;
          }
          catch (ActivityNotFoundException paramKeyEvent)
          {
            for (;;)
            {
              Slog.w("WindowManager", "Dropping application launch key because the activity to which it is registered was not found: keyCode=" + m + ", category=" + paramWindowState, paramKeyEvent);
            }
          }
          if (!isUserSetupComplete()) {
            break label1754;
          }
          paramInt = paramKeyEvent.getModifiers() & 0xFF3E;
          if (!KeyEvent.metaStateHasModifiers(paramInt, 2)) {
            break label1754;
          }
          this.mRecentAppsHeldModifiers = paramInt;
          showRecentApps(true, false);
          return -1L;
          if ((bool1) || (this.mRecentAppsHeldModifiers == 0) || ((this.mRecentAppsHeldModifiers & n) != 0)) {
            break label1754;
          }
          this.mRecentAppsHeldModifiers = 0;
          hideRecentApps(true, false);
          break label1754;
        }
        label2089:
        if ((!this.mLanguageSwitchKeyPressed) || (bool1)) {}
        while ((isValidGlobalKey(m)) && (this.mGlobalKeyManager.handleGlobalKey(this.mContext, m, paramKeyEvent)))
        {
          return -1L;
          if ((m == 204) || (m == 62))
          {
            this.mLanguageSwitchKeyPressed = false;
            return -1L;
          }
        }
        if (bool1)
        {
          l2 = m;
          l1 = l2;
          if (paramKeyEvent.isCtrlPressed()) {
            l1 = l2 | 0x100000000000;
          }
          l2 = l1;
          if (paramKeyEvent.isAltPressed()) {
            l2 = l1 | 0x200000000;
          }
          l1 = l2;
          if (paramKeyEvent.isShiftPressed()) {
            l1 = l2 | 0x100000000;
          }
          l2 = l1;
          if (paramKeyEvent.isMetaPressed()) {
            l2 = l1 | 0x1000000000000;
          }
          paramWindowState = (IShortcutService)this.mShortcutKeyServices.get(l2);
          if (paramWindowState != null) {
            try
            {
              if (isUserSetupComplete()) {
                paramWindowState.notifyShortcutKeyPressed(l2);
              }
              return -1L;
            }
            catch (RemoteException paramWindowState)
            {
              for (;;)
              {
                this.mShortcutKeyServices.delete(l2);
              }
            }
          }
        }
        if ((0x10000 & n) != 0) {
          return -1L;
        }
        return 0L;
      }
      catch (RemoteException paramWindowState)
      {
        for (;;) {}
      }
    }
  }
  
  public int interceptKeyBeforeQueueing(KeyEvent paramKeyEvent, int paramInt)
  {
    if (!this.mSystemBooted) {
      return 0;
    }
    boolean bool1;
    int m;
    label30:
    boolean bool5;
    int n;
    int i;
    label52:
    boolean bool3;
    label62:
    boolean bool2;
    if ((0x20000000 & paramInt) != 0)
    {
      bool1 = true;
      if (paramKeyEvent.getAction() != 0) {
        break label226;
      }
      m = 1;
      bool5 = paramKeyEvent.isCanceled();
      n = paramKeyEvent.getKeyCode();
      if ((0x1000000 & paramInt) == 0) {
        break label232;
      }
      i = 1;
      if (this.mKeyguardDelegate != null) {
        break label237;
      }
      bool3 = false;
      if (DEBUG_INPUT) {
        Log.d("WindowManager", "interceptKeyTq keycode=" + n + " interactive=" + bool1 + " keyguardActive=" + bool3 + " policyFlags=" + Integer.toHexString(paramInt));
      }
      if ((paramInt & 0x1) != 0) {
        break label263;
      }
      bool2 = paramKeyEvent.isWakeKey();
      label142:
      if ((bool1) || ((i != 0) && (!bool2))) {
        break label269;
      }
      if ((bool1) || (!shouldDispatchInputWhenNonInteractive(paramKeyEvent))) {
        break label323;
      }
      i = 1;
      this.mPendingWakeKey = -1;
    }
    label226:
    label232:
    label237:
    label263:
    label269:
    int j;
    for (;;)
    {
      if ((isValidGlobalKey(n)) && (this.mGlobalKeyManager.shouldHandleGlobalKey(n, paramKeyEvent)))
      {
        if (bool2) {
          wakeUp(paramKeyEvent.getEventTime(), this.mAllowTheaterModeWakeFromKey, "android.policy:KEY");
        }
        return i;
        bool1 = false;
        break;
        m = 0;
        break label30;
        i = 0;
        break label52;
        if (bool1)
        {
          bool3 = isKeyguardShowingAndNotOccluded();
          break label62;
        }
        bool3 = this.mKeyguardDelegate.isShowing();
        break label62;
        bool2 = true;
        break label142;
        j = 1;
        i = 1;
        boolean bool4 = false;
        bool2 = bool4;
        if (bool1)
        {
          i = j;
          if (n == this.mPendingWakeKey) {
            if (m == 0) {
              break label318;
            }
          }
          label318:
          for (i = j;; i = 0)
          {
            this.mPendingWakeKey = -1;
            bool2 = bool4;
            break;
          }
          label323:
          j = 0;
          bool4 = bool2;
          if (bool2) {
            if ((m == 0) || (!isWakeKeyWhenScreenOff(n))) {
              break label393;
            }
          }
          label393:
          for (bool4 = bool2;; bool4 = false)
          {
            bool2 = bool4;
            i = j;
            if (!bool4) {
              break;
            }
            bool2 = bool4;
            i = j;
            if (m == 0) {
              break;
            }
            this.mPendingWakeKey = n;
            bool2 = bool4;
            i = j;
            break;
          }
        }
      }
    }
    if ((m != 0) && ((paramInt & 0x2) != 0)) {
      if (paramKeyEvent.getRepeatCount() == 0)
      {
        j = 1;
        switch (n)
        {
        default: 
          n = j;
          paramInt = i;
          bool1 = bool2;
        }
      }
    }
    for (;;)
    {
      if (n != 0) {
        performHapticFeedbackLw(null, 1, false);
      }
      if (bool1) {
        wakeUp(paramKeyEvent.getEventTime(), this.mAllowTheaterModeWakeFromKey, "android.policy:KEY");
      }
      return paramInt;
      j = 0;
      break;
      j = 0;
      break;
      Object localObject;
      if (m != 0)
      {
        this.mBackKeyHandled = false;
        bool1 = bool2;
        paramInt = i;
        n = j;
        if (hasLongPressOnBackBehavior())
        {
          localObject = this.mHandler.obtainMessage(18);
          ((Message)localObject).setAsynchronous(true);
          this.mHandler.sendMessageDelayed((Message)localObject, ViewConfiguration.get(this.mContext).getDeviceGlobalActionKeyTimeout());
          bool1 = bool2;
          paramInt = i;
          n = j;
        }
      }
      else
      {
        bool3 = this.mBackKeyHandled;
        cancelPendingBackKeyAction();
        bool1 = bool2;
        paramInt = i;
        n = j;
        if (bool3)
        {
          paramInt = i & 0xFFFFFFFE;
          bool1 = bool2;
          n = j;
          continue;
          int k = i;
          if (oemIsActionPassToUser()) {
            k = i | 0x1;
          }
          if (n == 25) {
            if (m != 0) {
              if ((bool1) && (!this.mScreenshotChordVolumeDownKeyTriggered)) {}
            }
          }
          for (;;)
          {
            if (m != 0)
            {
              localObject = getTelecommService();
              if (localObject != null)
              {
                if (((TelecomManager)localObject).isRinging())
                {
                  Log.i("WindowManager", "interceptKeyBeforeQueueing: VOLUME key-down while ringing: Silence ringer!");
                  ((TelecomManager)localObject).silenceRinger();
                  paramInt = k & 0xFFFFFFFE;
                  bool1 = bool2;
                  n = j;
                  break;
                  if ((paramKeyEvent.getFlags() & 0x400) != 0) {
                    continue;
                  }
                  this.mScreenshotChordVolumeDownKeyTriggered = true;
                  this.mScreenshotChordVolumeDownKeyTime = paramKeyEvent.getDownTime();
                  this.mScreenshotChordVolumeDownKeyConsumed = false;
                  cancelPendingPowerKeyAction();
                  interceptScreenshotChord();
                  continue;
                  this.mScreenshotChordVolumeDownKeyTriggered = false;
                  cancelPendingScreenshotChordAction();
                  continue;
                  if (n != 24) {
                    continue;
                  }
                  if (m != 0)
                  {
                    if ((!bool1) || (this.mScreenshotChordVolumeUpKeyTriggered) || ((paramKeyEvent.getFlags() & 0x400) != 0)) {
                      continue;
                    }
                    this.mScreenshotChordVolumeUpKeyTriggered = true;
                    cancelPendingPowerKeyAction();
                    cancelPendingScreenshotChordAction();
                    continue;
                  }
                  this.mScreenshotChordVolumeUpKeyTriggered = false;
                  cancelPendingScreenshotChordAction();
                  continue;
                }
                if ((((TelecomManager)localObject).isInCall()) && ((k & 0x1) == 0))
                {
                  MediaSessionLegacyHelper.getHelper(this.mContext).sendVolumeKeyEvent(paramKeyEvent, false);
                  bool1 = bool2;
                  paramInt = k;
                  n = j;
                  break;
                }
              }
            }
          }
          if (this.mUseTvRouting)
          {
            paramInt = k | 0x1;
            bool1 = bool2;
            n = j;
          }
          else
          {
            bool1 = bool2;
            paramInt = k;
            n = j;
            if ((k & 0x1) == 0)
            {
              MediaSessionLegacyHelper.getHelper(this.mContext).sendVolumeKeyEvent(paramKeyEvent, true);
              bool1 = bool2;
              paramInt = k;
              n = j;
              continue;
              i &= 0xFFFFFFFE;
              if (m != 0)
              {
                localObject = getTelecommService();
                bool3 = false;
                if (localObject != null) {
                  bool3 = ((TelecomManager)localObject).endCall();
                }
                if ((!bool1) || (bool3))
                {
                  this.mEndCallKeyHandled = true;
                  bool1 = bool2;
                  paramInt = i;
                  n = j;
                }
                else
                {
                  this.mEndCallKeyHandled = false;
                  this.mHandler.postDelayed(this.mEndCallLongPress, ViewConfiguration.get(this.mContext).getDeviceGlobalActionKeyTimeout());
                  bool1 = bool2;
                  paramInt = i;
                  n = j;
                }
              }
              else
              {
                bool1 = bool2;
                paramInt = i;
                n = j;
                if (!this.mEndCallKeyHandled)
                {
                  this.mHandler.removeCallbacks(this.mEndCallLongPress);
                  bool1 = bool2;
                  paramInt = i;
                  n = j;
                  if (!bool5) {
                    if ((this.mEndcallBehavior & 0x1) != 0)
                    {
                      bool1 = bool2;
                      paramInt = i;
                      n = j;
                      if (goHome()) {}
                    }
                    else
                    {
                      bool1 = bool2;
                      paramInt = i;
                      n = j;
                      if ((this.mEndcallBehavior & 0x2) != 0)
                      {
                        this.mPowerManager.goToSleep(paramKeyEvent.getEventTime(), 4, 0);
                        bool1 = false;
                        paramInt = i;
                        n = j;
                        continue;
                        i &= 0xFFFFFFFE;
                        bool3 = false;
                        paramInt = i;
                        if (oemIsActionPassToUser()) {
                          paramInt = i | 0x1;
                        }
                        bool2 = bool1;
                        if (this.mKeyguardDelegate != null)
                        {
                          bool2 = bool1;
                          if (this.mKeyguardDelegate.isFingerprintAuthenticating())
                          {
                            Log.d("WindowManager", "change interactive to false");
                            bool2 = false;
                          }
                        }
                        if (m != 0)
                        {
                          interceptPowerKeyDown(paramKeyEvent, bool2);
                          bool1 = bool3;
                          n = j;
                        }
                        else
                        {
                          interceptPowerKeyUp(paramKeyEvent, bool2, bool5);
                          bool1 = bool3;
                          n = j;
                          continue;
                          paramInt = i & 0xFFFFFFFE;
                          interceptSystemNavigationKey(paramKeyEvent);
                          bool1 = bool2;
                          n = j;
                          continue;
                          paramInt = i & 0xFFFFFFFE;
                          bool1 = false;
                          if (!this.mPowerManager.isInteractive()) {
                            j = 0;
                          }
                          if (m != 0)
                          {
                            sleepPress(paramKeyEvent.getEventTime());
                            n = j;
                          }
                          else
                          {
                            sleepRelease(paramKeyEvent.getEventTime());
                            n = j;
                            continue;
                            i &= 0xFFFFFFFE;
                            bool2 = false;
                            bool1 = bool2;
                            paramInt = i;
                            n = j;
                            if (m == 0)
                            {
                              this.mPowerManagerInternal.setUserInactiveOverrideFromWindowManager();
                              bool1 = bool2;
                              paramInt = i;
                              n = j;
                              continue;
                              paramInt = i & 0xFFFFFFFE;
                              bool1 = true;
                              n = j;
                              continue;
                              k = i;
                              if (MediaSessionLegacyHelper.getHelper(this.mContext).isGlobalPriorityActive()) {
                                k = i & 0xFFFFFFFE;
                              }
                              bool1 = bool2;
                              paramInt = k;
                              n = j;
                              if ((k & 0x1) == 0)
                              {
                                this.mBroadcastWakeLock.acquire();
                                localObject = this.mHandler.obtainMessage(3, new KeyEvent(paramKeyEvent));
                                ((Message)localObject).setAsynchronous(true);
                                ((Message)localObject).sendToTarget();
                                bool1 = bool2;
                                paramInt = k;
                                n = j;
                                continue;
                                bool1 = bool2;
                                paramInt = i;
                                n = j;
                                if (m != 0)
                                {
                                  localObject = getTelecommService();
                                  bool1 = bool2;
                                  paramInt = i;
                                  n = j;
                                  if (localObject != null)
                                  {
                                    bool1 = bool2;
                                    paramInt = i;
                                    n = j;
                                    if (((TelecomManager)localObject).isRinging())
                                    {
                                      Log.i("WindowManager", "interceptKeyBeforeQueueing: CALL key-down while ringing: Answer the call!");
                                      ((TelecomManager)localObject).acceptRingingCall();
                                      paramInt = i & 0xFFFFFFFE;
                                      bool1 = bool2;
                                      n = j;
                                      continue;
                                      bool1 = bool2;
                                      paramInt = i;
                                      n = j;
                                      if ((i & 0x1) == 0)
                                      {
                                        bool1 = bool2;
                                        paramInt = i;
                                        n = j;
                                        if (m == 0)
                                        {
                                          this.mBroadcastWakeLock.acquire();
                                          localObject = this.mHandler;
                                          if (bool3) {}
                                          for (paramInt = 1;; paramInt = 0)
                                          {
                                            localObject = ((Handler)localObject).obtainMessage(12, paramInt, 0);
                                            ((Message)localObject).setAsynchronous(true);
                                            ((Message)localObject).sendToTarget();
                                            bool1 = bool2;
                                            paramInt = i;
                                            n = j;
                                            break;
                                          }
                                          bool1 = bool2;
                                          paramInt = i;
                                          n = j;
                                          if (this.mShortPressWindowBehavior == 1)
                                          {
                                            bool1 = bool2;
                                            paramInt = i;
                                            n = j;
                                            if (this.mTvPictureInPictureVisible)
                                            {
                                              if (m == 0) {
                                                showTvPictureInPictureMenu(paramKeyEvent);
                                              }
                                              paramInt = i & 0xFFFFFFFE;
                                              bool1 = bool2;
                                              n = j;
                                            }
                                          }
                                        }
                                      }
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
  
  public int interceptMotionBeforeQueueingNonInteractive(long paramLong, int paramInt)
  {
    if (((paramInt & 0x1) != 0) && (wakeUp(paramLong / 1000000L, this.mAllowTheaterModeWakeFromMotion, "android.policy:MOTION"))) {
      return 0;
    }
    if (shouldDispatchInputWhenNonInteractive(null)) {
      return 1;
    }
    if ((isTheaterModeEnabled()) && ((paramInt & 0x1) != 0)) {
      wakeUp(paramLong / 1000000L, this.mAllowTheaterModeWakeFromMotionWhenNotDreaming, "android.policy:MOTION");
    }
    return 0;
  }
  
  public boolean isDefaultOrientationForced()
  {
    return this.mForceDefaultOrientation;
  }
  
  boolean isDeviceProvisioned()
  {
    boolean bool = false;
    if (Settings.Global.getInt(this.mContext.getContentResolver(), "device_provisioned", 0) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isDockSideAllowed(int paramInt)
  {
    if (!this.mNavigationBarCanMove)
    {
      if ((paramInt == 2) || (paramInt == 1)) {}
      while (paramInt == 3) {
        return true;
      }
      return false;
    }
    return (paramInt == 2) || (paramInt == 1);
  }
  
  public boolean isForceHiding(WindowManager.LayoutParams paramLayoutParams)
  {
    return ((paramLayoutParams.privateFlags & 0x400) != 0) || ((isKeyguardHostWindow(paramLayoutParams)) && (this.mKeyguardDelegate != null) && (this.mKeyguardDelegate.isShowing())) || (paramLayoutParams.type == 2029);
  }
  
  boolean isFullscreen(WindowManager.LayoutParams paramLayoutParams)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramLayoutParams.x == 0)
    {
      bool1 = bool2;
      if (paramLayoutParams.y == 0)
      {
        bool1 = bool2;
        if (paramLayoutParams.width == -1)
        {
          bool1 = bool2;
          if (paramLayoutParams.height == -1) {
            bool1 = true;
          }
        }
      }
    }
    return bool1;
  }
  
  public boolean isKeyguardDrawnLw()
  {
    synchronized (this.mLock)
    {
      boolean bool = this.mKeyguardDrawnOnce;
      return bool;
    }
  }
  
  public boolean isKeyguardHostWindow(WindowManager.LayoutParams paramLayoutParams)
  {
    return paramLayoutParams.type == 2000;
  }
  
  public boolean isKeyguardLocked()
  {
    return keyguardOn();
  }
  
  public boolean isKeyguardSecure(int paramInt)
  {
    if (this.mKeyguardDelegate == null) {
      return false;
    }
    return this.mKeyguardDelegate.isSecure(paramInt);
  }
  
  public boolean isKeyguardShowingOrOccluded()
  {
    if (this.mKeyguardDelegate == null) {
      return false;
    }
    return this.mKeyguardDelegate.isShowing();
  }
  
  boolean isLandscapeOrSeascape(int paramInt)
  {
    return (paramInt == this.mLandscapeRotation) || (paramInt == this.mSeascapeRotation);
  }
  
  public boolean isNavBarForcedShownLw(WindowManagerPolicy.WindowState paramWindowState)
  {
    return this.mForceShowSystemBars;
  }
  
  public boolean isScreenOn()
  {
    return this.mScreenOnFully;
  }
  
  public boolean isTopLevelWindow(int paramInt)
  {
    if ((paramInt >= 1000) && (paramInt <= 1999)) {
      return paramInt == 1003;
    }
    return true;
  }
  
  boolean isUserSetupComplete()
  {
    boolean bool = false;
    if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "user_setup_complete", 0, -2) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public void keepScreenOnStartedLw() {}
  
  public void keepScreenOnStoppedLw()
  {
    if (isKeyguardShowingAndNotOccluded()) {
      this.mPowerManager.userActivity(SystemClock.uptimeMillis(), false);
    }
  }
  
  boolean keyguardOn()
  {
    if (!isKeyguardShowingAndNotOccluded()) {
      return inKeyguardRestrictedKeyInputMode();
    }
    return true;
  }
  
  protected void launchAssistAction(String paramString, int paramInt)
  {
    sendCloseSystemWindows("assist");
    if (!isUserSetupComplete()) {
      return;
    }
    Bundle localBundle1 = null;
    if (paramInt > Integer.MIN_VALUE)
    {
      localBundle1 = new Bundle();
      localBundle1.putInt("android.intent.extra.ASSIST_INPUT_DEVICE_ID", paramInt);
    }
    if ((this.mContext.getResources().getConfiguration().uiMode & 0xF) == 4) {
      ((SearchManager)this.mContext.getSystemService("search")).launchLegacyAssist(paramString, UserHandle.myUserId(), localBundle1);
    }
    Bundle localBundle2;
    do
    {
      return;
      localBundle2 = localBundle1;
      if (paramString != null)
      {
        localBundle2 = localBundle1;
        if (localBundle1 == null) {
          localBundle2 = new Bundle();
        }
        localBundle2.putBoolean(paramString, true);
      }
      paramString = getStatusBarManagerInternal();
    } while (paramString == null);
    paramString.startAssist(localBundle2);
  }
  
  protected void launchAssistLongPressAction()
  {
    sendCloseSystemWindows("assist");
    Intent localIntent = new Intent("android.intent.action.SEARCH_LONG_PRESS");
    localIntent.setFlags(268435456);
    try
    {
      SearchManager localSearchManager = getSearchManager();
      if (localSearchManager != null) {
        localSearchManager.stopSearch();
      }
      startActivityAsUser(localIntent, UserHandle.CURRENT);
      return;
    }
    catch (ActivityNotFoundException localActivityNotFoundException)
    {
      Slog.w("WindowManager", "No activity to handle assist long press action.", localActivityNotFoundException);
    }
  }
  
  void launchHomeFromHotKey()
  {
    launchHomeFromHotKey(true, true);
  }
  
  void launchHomeFromHotKey(final boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean2)
    {
      if (isKeyguardShowingAndNotOccluded()) {
        return;
      }
      if ((!this.mHideLockScreen) && (this.mKeyguardDelegate.isInputRestricted()))
      {
        this.mKeyguardDelegate.verifyUnlock(new WindowManagerPolicy.OnKeyguardExitResult()
        {
          public void onKeyguardExitResult(boolean paramAnonymousBoolean)
          {
            if (paramAnonymousBoolean) {}
            try
            {
              ActivityManagerNative.getDefault().stopAppSwitches();
              PhoneWindowManager.this.sendCloseSystemWindows("homekey");
              PhoneWindowManager.this.startDockOrHome(true, paramBoolean1);
              return;
            }
            catch (RemoteException localRemoteException)
            {
              for (;;) {}
            }
          }
        });
        return;
      }
    }
    try
    {
      ActivityManagerNative.getDefault().stopAppSwitches();
      if (this.mRecentsVisible)
      {
        if (paramBoolean1) {
          awakenDreams();
        }
        hideRecentApps(false, true);
        return;
      }
      sendCloseSystemWindows("homekey");
      startDockOrHome(true, paramBoolean1);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;) {}
    }
  }
  
  protected void launchVoiceAssistAction()
  {
    sendCloseSystemWindows("assist");
    Intent localIntent = new Intent("android.intent.action.VOICE_ASSIST");
    localIntent.setFlags(268435456);
    try
    {
      SearchManager localSearchManager = getSearchManager();
      if (localSearchManager != null) {
        localSearchManager.stopSearch();
      }
      startActivityAsUser(localIntent, UserHandle.CURRENT);
      return;
    }
    catch (ActivityNotFoundException localActivityNotFoundException)
    {
      Slog.w("WindowManager", "No activity to handle assist long press action.", localActivityNotFoundException);
    }
  }
  
  void launchVoiceAssistWithWakeLock(boolean paramBoolean)
  {
    Object localObject = IDeviceIdleController.Stub.asInterface(ServiceManager.getService("deviceidle"));
    if (localObject != null) {}
    try
    {
      ((IDeviceIdleController)localObject).exitIdle("voice-search");
      localObject = new Intent("android.speech.action.VOICE_SEARCH_HANDS_FREE");
      ((Intent)localObject).putExtra("android.speech.extras.EXTRA_SECURE", paramBoolean);
      startActivityAsUser((Intent)localObject, UserHandle.CURRENT_OR_SELF);
      this.mBroadcastWakeLock.release();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;) {}
    }
  }
  
  public void layoutWindowLw(WindowManagerPolicy.WindowState paramWindowState1, WindowManagerPolicy.WindowState paramWindowState2)
  {
    if (((paramWindowState1 == this.mStatusBar) && (!canReceiveInput(paramWindowState1))) || (paramWindowState1 == this.mNavigationBar)) {
      return;
    }
    WindowManager.LayoutParams localLayoutParams = paramWindowState1.getAttrs();
    boolean bool2 = paramWindowState1.isDefaultDisplay();
    int i;
    int m;
    int i3;
    int i2;
    Rect localRect1;
    Rect localRect2;
    Rect localRect3;
    Rect localRect4;
    Rect localRect5;
    Rect localRect6;
    Rect localRect7;
    Object localObject2;
    boolean bool1;
    label192:
    int i1;
    label226:
    label256:
    label285:
    Object localObject1;
    int j;
    if (bool2) {
      if ((paramWindowState1 == this.mLastInputMethodTargetWindow) && (this.mLastInputMethodWindow != null))
      {
        i = 1;
        if (i != 0)
        {
          if (DEBUG_LAYOUT) {
            Slog.i("WindowManager", "Offset ime target window by the last ime window state");
          }
          offsetInputMethodWindowLw(this.mLastInputMethodWindow);
        }
        m = PolicyControl.getWindowFlags(paramWindowState1, localLayoutParams);
        i3 = localLayoutParams.privateFlags;
        int n = localLayoutParams.softInputMode;
        i2 = PolicyControl.getSystemUiVisibility(paramWindowState1, null);
        localRect1 = mTmpParentFrame;
        localRect2 = mTmpDisplayFrame;
        localRect3 = mTmpOverscanFrame;
        localRect4 = mTmpContentFrame;
        localRect5 = mTmpVisibleFrame;
        localRect6 = mTmpDecorFrame;
        localRect7 = mTmpStableFrame;
        localObject2 = null;
        localRect6.setEmpty();
        if ((!bool2) || (!this.mHasNavigationBar) || (this.mNavigationBar == null)) {
          break label763;
        }
        bool1 = this.mNavigationBar.isVisibleLw();
        i1 = n & 0xF0;
        if (!bool2) {
          break label769;
        }
        localRect7.set(this.mStableLeft, this.mStableTop, this.mStableRight, this.mStableBottom);
        if (bool2) {
          break label922;
        }
        if (paramWindowState2 == null) {
          break label793;
        }
        setAttachedWindowFrames(paramWindowState1, m, i1, paramWindowState2, true, localRect1, localRect2, localRect3, localRect4, localRect5);
        if (((m & 0x200) != 0) && (localLayoutParams.type != 2010) && (!paramWindowState1.isInMultiWindowMode())) {
          break label5484;
        }
        bool1 = shouldUseOutsets(localLayoutParams, m);
        localObject1 = localObject2;
        if (bool2)
        {
          localObject1 = localObject2;
          if (bool1)
          {
            localObject2 = mTmpOutsetFrame;
            ((Rect)localObject2).set(localRect4.left, localRect4.top, localRect4.right, localRect4.bottom);
            i = ScreenShapeHelper.getWindowOutsetBottomPx(this.mContext.getResources());
            localObject1 = localObject2;
            if (i > 0)
            {
              j = this.mDisplayRotation;
              if (j != 0) {
                break label5626;
              }
              ((Rect)localObject2).bottom += i;
              label385:
              localObject1 = localObject2;
              if (DEBUG_LAYOUT)
              {
                Slog.v("WindowManager", "applying bottom outset of " + i + " with rotation " + j + ", result: " + localObject2);
                localObject1 = localObject2;
              }
            }
          }
        }
        if (DEBUG_LAYOUT)
        {
          localObject2 = new StringBuilder().append("Compute frame ").append(localLayoutParams.getTitle()).append(": sim=#").append(Integer.toHexString(n)).append(" attach=").append(paramWindowState2).append(" type=").append(localLayoutParams.type).append(String.format(" flags=0x%08x", new Object[] { Integer.valueOf(m) })).append(" pf=").append(localRect1.toShortString()).append(" df=").append(localRect2.toShortString()).append(" of=").append(localRect3.toShortString()).append(" cf=").append(localRect4.toShortString()).append(" vf=").append(localRect5.toShortString()).append(" dcf=").append(localRect6.toShortString()).append(" sf=").append(localRect7.toShortString()).append(" osf=");
          if (localObject1 != null) {
            break label5689;
          }
          paramWindowState2 = "null";
          label648:
          Slog.v("WindowManager", paramWindowState2);
        }
        paramWindowState1.computeFrameLw(localRect1, localRect2, localRect3, localRect4, localRect5, localRect6, localRect7, (Rect)localObject1);
        if ((localLayoutParams.type == 2011) && (paramWindowState1.isVisibleOrBehindKeyguardLw()) && (paramWindowState1.isDisplayedLw()) && (!paramWindowState1.getGivenInsetsPendingLw())) {
          break label5698;
        }
      }
    }
    for (;;)
    {
      if ((localLayoutParams.type == 2031) && (paramWindowState1.isVisibleOrBehindKeyguardLw()) && (!paramWindowState1.getGivenInsetsPendingLw())) {
        break label5712;
      }
      return;
      i = 0;
      break;
      i = 0;
      break;
      label763:
      bool1 = false;
      break label192;
      label769:
      localRect7.set(this.mOverscanLeft, this.mOverscanTop, this.mOverscanRight, this.mOverscanBottom);
      break label226;
      label793:
      i = this.mOverscanScreenLeft;
      localRect4.left = i;
      localRect3.left = i;
      localRect2.left = i;
      localRect1.left = i;
      i = this.mOverscanScreenTop;
      localRect4.top = i;
      localRect3.top = i;
      localRect2.top = i;
      localRect1.top = i;
      i = this.mOverscanScreenLeft + this.mOverscanScreenWidth;
      localRect4.right = i;
      localRect3.right = i;
      localRect2.right = i;
      localRect1.right = i;
      i = this.mOverscanScreenTop + this.mOverscanScreenHeight;
      localRect4.bottom = i;
      localRect3.bottom = i;
      localRect2.bottom = i;
      localRect1.bottom = i;
      break label256;
      label922:
      if (localLayoutParams.type == 2011)
      {
        i = this.mDockLeft;
        localRect5.left = i;
        localRect4.left = i;
        localRect3.left = i;
        localRect2.left = i;
        localRect1.left = i;
        i = this.mDockTop;
        localRect5.top = i;
        localRect4.top = i;
        localRect3.top = i;
        localRect2.top = i;
        localRect1.top = i;
        i = this.mDockRight;
        localRect5.right = i;
        localRect4.right = i;
        localRect3.right = i;
        localRect2.right = i;
        localRect1.right = i;
        i = this.mUnrestrictedScreenTop + this.mUnrestrictedScreenHeight;
        localRect3.bottom = i;
        localRect2.bottom = i;
        localRect1.bottom = i;
        i = this.mStableBottom;
        localRect5.bottom = i;
        localRect4.bottom = i;
        if ((this.mStatusBar != null) && (this.mFocusedWindow == this.mStatusBar) && (canReceiveInput(this.mStatusBar)))
        {
          if (this.mNavigationBarPosition != 1) {
            break label1175;
          }
          i = this.mStableRight;
          localRect5.right = i;
          localRect4.right = i;
          localRect3.right = i;
          localRect2.right = i;
          localRect1.right = i;
        }
        for (;;)
        {
          localLayoutParams.gravity = 80;
          this.mDockLayer = paramWindowState1.getSurfaceLayer();
          break;
          label1175:
          if (this.mNavigationBarPosition == 2)
          {
            i = this.mStableLeft;
            localRect5.left = i;
            localRect4.left = i;
            localRect3.left = i;
            localRect2.left = i;
            localRect1.left = i;
          }
        }
      }
      if (localLayoutParams.type == 2031)
      {
        i = this.mUnrestrictedScreenLeft;
        localRect3.left = i;
        localRect2.left = i;
        localRect1.left = i;
        i = this.mUnrestrictedScreenTop;
        localRect3.top = i;
        localRect2.top = i;
        localRect1.top = i;
        i = this.mUnrestrictedScreenLeft + this.mUnrestrictedScreenWidth;
        localRect3.right = i;
        localRect2.right = i;
        localRect1.right = i;
        i = this.mUnrestrictedScreenTop + this.mUnrestrictedScreenHeight;
        localRect3.bottom = i;
        localRect2.bottom = i;
        localRect1.bottom = i;
        if (i1 != 16)
        {
          localRect4.left = this.mDockLeft;
          localRect4.top = this.mDockTop;
          localRect4.right = this.mDockRight;
        }
        for (localRect4.bottom = this.mDockBottom;; localRect4.bottom = this.mContentBottom)
        {
          if (i1 == 48) {
            break label1462;
          }
          localRect5.left = this.mCurLeft;
          localRect5.top = this.mCurTop;
          localRect5.right = this.mCurRight;
          localRect5.bottom = this.mCurBottom;
          break;
          localRect4.left = this.mContentLeft;
          localRect4.top = this.mContentTop;
          localRect4.right = this.mContentRight;
        }
        label1462:
        localRect5.set(localRect4);
        break label256;
      }
      if (localLayoutParams.type == 2013)
      {
        layoutWallpaper(paramWindowState1, localRect1, localRect2, localRect3, localRect4);
        break label256;
      }
      if (paramWindowState1 == this.mStatusBar)
      {
        i = this.mUnrestrictedScreenLeft;
        localRect3.left = i;
        localRect2.left = i;
        localRect1.left = i;
        i = this.mUnrestrictedScreenTop;
        localRect3.top = i;
        localRect2.top = i;
        localRect1.top = i;
        i = this.mUnrestrictedScreenWidth + this.mUnrestrictedScreenLeft;
        localRect3.right = i;
        localRect2.right = i;
        localRect1.right = i;
        i = this.mUnrestrictedScreenHeight + this.mUnrestrictedScreenTop;
        localRect3.bottom = i;
        localRect2.bottom = i;
        localRect1.bottom = i;
        i = this.mStableLeft;
        localRect5.left = i;
        localRect4.left = i;
        i = this.mStableTop;
        localRect5.top = i;
        localRect4.top = i;
        i = this.mStableRight;
        localRect5.right = i;
        localRect4.right = i;
        localRect5.bottom = this.mStableBottom;
        if (i1 == 16)
        {
          localRect4.bottom = this.mContentBottom;
          break label256;
        }
        localRect4.bottom = this.mDockBottom;
        localRect5.bottom = this.mContentBottom;
        break label256;
      }
      localRect6.left = this.mSystemLeft;
      localRect6.top = this.mSystemTop;
      localRect6.right = this.mSystemRight;
      localRect6.bottom = this.mSystemBottom;
      label1760:
      label1781:
      int k;
      if ((localLayoutParams.privateFlags & 0x200) != 0)
      {
        j = 1;
        if (localLayoutParams.type < 1) {
          break label1903;
        }
        if (localLayoutParams.type > 99) {
          break label1898;
        }
        i = 1;
        if ((paramWindowState1 == this.mTopFullscreenOpaqueWindowState) && (!paramWindowState1.isAnimatingLw())) {
          break label1908;
        }
        k = 0;
        label1801:
        if ((i != 0) && (j == 0)) {
          break label1914;
        }
      }
      for (;;)
      {
        if ((0x10100 & m) == 65792)
        {
          if (DEBUG_LAYOUT) {
            Slog.v("WindowManager", "layoutWindowLw(" + localLayoutParams.getTitle() + "): IN_SCREEN, INSET_DECOR");
          }
          if (paramWindowState2 != null)
          {
            setAttachedWindowFrames(paramWindowState1, m, i1, paramWindowState2, true, localRect1, localRect2, localRect3, localRect4, localRect5);
            break;
            j = 0;
            break label1760;
            label1898:
            i = 0;
            break label1781;
            label1903:
            i = 0;
            break label1781;
            label1908:
            k = 1;
            break label1801;
            label1914:
            if (k != 0) {
              continue;
            }
            if (((i2 & 0x4) == 0) && ((m & 0x400) == 0) && ((0x4000000 & m) == 0) && ((0x80000000 & m) == 0) && ((0x20000 & i3) == 0)) {
              localRect6.top = this.mStableTop;
            }
            if (((0x8000000 & m) != 0) || ((i2 & 0x2) != 0) || ((0x80000000 & m) != 0)) {
              continue;
            }
            localRect6.bottom = this.mStableBottom;
            localRect6.right = this.mStableRight;
            continue;
          }
          if ((localLayoutParams.type == 2014) || (localLayoutParams.type == 2017)) {
            if (bool1)
            {
              i = this.mDockLeft;
              label2049:
              localRect3.left = i;
              localRect2.left = i;
              localRect1.left = i;
              i = this.mUnrestrictedScreenTop;
              localRect3.top = i;
              localRect2.top = i;
              localRect1.top = i;
              if (!bool1) {
                break label2340;
              }
              i = this.mRestrictedScreenLeft + this.mRestrictedScreenWidth;
              label2105:
              localRect3.right = i;
              localRect2.right = i;
              localRect1.right = i;
              if (!bool1) {
                break label2353;
              }
              i = this.mRestrictedScreenTop + this.mRestrictedScreenHeight;
              label2138:
              localRect3.bottom = i;
              localRect2.bottom = i;
              localRect1.bottom = i;
              if (DEBUG_LAYOUT) {
                Slog.v("WindowManager", String.format("Laying out status bar window: (%d,%d - %d,%d)", new Object[] { Integer.valueOf(localRect1.left), Integer.valueOf(localRect1.top), Integer.valueOf(localRect1.right), Integer.valueOf(localRect1.bottom) }));
              }
              label2222:
              if ((m & 0x400) != 0) {
                break label2875;
              }
              if (!paramWindowState1.isVoiceInteraction()) {
                break label2790;
              }
              localRect4.left = this.mVoiceContentLeft;
              localRect4.top = this.mVoiceContentTop;
              localRect4.right = this.mVoiceContentRight;
              localRect4.bottom = this.mVoiceContentBottom;
            }
          }
          for (;;)
          {
            applyStableConstraints(i2, m, localRect4);
            if (i1 == 48) {
              break label2924;
            }
            localRect5.left = this.mCurLeft;
            localRect5.top = this.mCurTop;
            localRect5.right = this.mCurRight;
            localRect5.bottom = this.mCurBottom;
            break;
            i = this.mUnrestrictedScreenLeft;
            break label2049;
            label2340:
            i = this.mUnrestrictedScreenLeft + this.mUnrestrictedScreenWidth;
            break label2105;
            label2353:
            i = this.mUnrestrictedScreenTop + this.mUnrestrictedScreenHeight;
            break label2138;
            if (((0x2000000 & m) != 0) && (localLayoutParams.type >= 1) && (localLayoutParams.type <= 1999))
            {
              i = this.mOverscanScreenLeft;
              localRect3.left = i;
              localRect2.left = i;
              localRect1.left = i;
              i = this.mOverscanScreenTop;
              localRect3.top = i;
              localRect2.top = i;
              localRect1.top = i;
              i = this.mOverscanScreenLeft + this.mOverscanScreenWidth;
              localRect3.right = i;
              localRect2.right = i;
              localRect1.right = i;
              i = this.mOverscanScreenTop + this.mOverscanScreenHeight;
              localRect3.bottom = i;
              localRect2.bottom = i;
              localRect1.bottom = i;
              break label2222;
            }
            if ((canHideNavigationBar()) && ((i2 & 0x200) != 0) && (localLayoutParams.type >= 1) && (localLayoutParams.type <= 1999))
            {
              i = this.mOverscanScreenLeft;
              localRect2.left = i;
              localRect1.left = i;
              i = this.mOverscanScreenTop;
              localRect2.top = i;
              localRect1.top = i;
              i = this.mOverscanScreenLeft + this.mOverscanScreenWidth;
              localRect2.right = i;
              localRect1.right = i;
              i = this.mOverscanScreenTop + this.mOverscanScreenHeight;
              localRect2.bottom = i;
              localRect1.bottom = i;
              localRect3.left = this.mUnrestrictedScreenLeft;
              localRect3.top = this.mUnrestrictedScreenTop;
              localRect3.right = (this.mUnrestrictedScreenLeft + this.mUnrestrictedScreenWidth);
              localRect3.bottom = (this.mUnrestrictedScreenTop + this.mUnrestrictedScreenHeight);
              break label2222;
            }
            i = this.mRestrictedOverscanScreenLeft;
            localRect2.left = i;
            localRect1.left = i;
            i = this.mRestrictedOverscanScreenTop;
            localRect2.top = i;
            localRect1.top = i;
            i = this.mRestrictedOverscanScreenLeft + this.mRestrictedOverscanScreenWidth;
            localRect2.right = i;
            localRect1.right = i;
            i = this.mRestrictedOverscanScreenTop + this.mRestrictedOverscanScreenHeight;
            localRect2.bottom = i;
            localRect1.bottom = i;
            localRect3.left = this.mUnrestrictedScreenLeft;
            localRect3.top = this.mUnrestrictedScreenTop;
            localRect3.right = (this.mUnrestrictedScreenLeft + this.mUnrestrictedScreenWidth);
            localRect3.bottom = (this.mUnrestrictedScreenTop + this.mUnrestrictedScreenHeight);
            break label2222;
            label2790:
            if (i1 != 16)
            {
              localRect4.left = this.mDockLeft;
              localRect4.top = this.mDockTop;
              localRect4.right = this.mDockRight;
              localRect4.bottom = this.mDockBottom;
            }
            else
            {
              localRect4.left = this.mContentLeft;
              localRect4.top = this.mContentTop;
              localRect4.right = this.mContentRight;
              localRect4.bottom = this.mContentBottom;
              continue;
              label2875:
              localRect4.left = this.mRestrictedScreenLeft;
              localRect4.top = this.mRestrictedScreenTop;
              localRect4.right = (this.mRestrictedScreenLeft + this.mRestrictedScreenWidth);
              localRect4.bottom = (this.mRestrictedScreenTop + this.mRestrictedScreenHeight);
            }
          }
          label2924:
          localRect5.set(localRect4);
          break;
        }
      }
      if (((m & 0x100) != 0) || ((i2 & 0x600) != 0))
      {
        if (DEBUG_LAYOUT) {
          Slog.v("WindowManager", "layoutWindowLw(" + localLayoutParams.getTitle() + "): IN_SCREEN");
        }
        if ((localLayoutParams.type == 2014) || (localLayoutParams.type == 2017))
        {
          label3016:
          if (!bool1) {
            break label3483;
          }
          i = this.mDockLeft;
          label3026:
          localRect4.left = i;
          localRect3.left = i;
          localRect2.left = i;
          localRect1.left = i;
          i = this.mUnrestrictedScreenTop;
          localRect4.top = i;
          localRect3.top = i;
          localRect2.top = i;
          localRect1.top = i;
          if (!bool1) {
            break label3491;
          }
          i = this.mRestrictedScreenLeft + this.mRestrictedScreenWidth;
          label3094:
          localRect4.right = i;
          localRect3.right = i;
          localRect2.right = i;
          localRect1.right = i;
          if (!bool1) {
            break label3504;
          }
          i = this.mRestrictedScreenTop + this.mRestrictedScreenHeight;
          label3133:
          localRect4.bottom = i;
          localRect3.bottom = i;
          localRect2.bottom = i;
          localRect1.bottom = i;
          if (DEBUG_LAYOUT) {
            Slog.v("WindowManager", String.format("Laying out IN_SCREEN status bar window: (%d,%d - %d,%d)", new Object[] { Integer.valueOf(localRect1.left), Integer.valueOf(localRect1.top), Integer.valueOf(localRect1.right), Integer.valueOf(localRect1.bottom) }));
          }
        }
        for (;;)
        {
          applyStableConstraints(i2, m, localRect4);
          if (i1 == 48) {
            break label4520;
          }
          localRect5.left = this.mCurLeft;
          localRect5.top = this.mCurTop;
          localRect5.right = this.mCurRight;
          localRect5.bottom = this.mCurBottom;
          break;
          if (localLayoutParams.type == 2020) {
            break label3016;
          }
          if ((localLayoutParams.type == 2019) || (localLayoutParams.type == 2024))
          {
            i = this.mUnrestrictedScreenLeft;
            localRect3.left = i;
            localRect2.left = i;
            localRect1.left = i;
            i = this.mUnrestrictedScreenTop;
            localRect3.top = i;
            localRect2.top = i;
            localRect1.top = i;
            i = this.mUnrestrictedScreenLeft + this.mUnrestrictedScreenWidth;
            localRect3.right = i;
            localRect2.right = i;
            localRect1.right = i;
            i = this.mUnrestrictedScreenTop + this.mUnrestrictedScreenHeight;
            localRect3.bottom = i;
            localRect2.bottom = i;
            localRect1.bottom = i;
            if (!DEBUG_LAYOUT) {
              continue;
            }
            Slog.v("WindowManager", String.format("Laying out navigation bar window: (%d,%d - %d,%d)", new Object[] { Integer.valueOf(localRect1.left), Integer.valueOf(localRect1.top), Integer.valueOf(localRect1.right), Integer.valueOf(localRect1.bottom) }));
            continue;
            label3483:
            i = this.mUnrestrictedScreenLeft;
            break label3026;
            label3491:
            i = this.mUnrestrictedScreenLeft + this.mUnrestrictedScreenWidth;
            break label3094;
            label3504:
            i = this.mUnrestrictedScreenTop + this.mUnrestrictedScreenHeight;
            break label3133;
          }
          if ((localLayoutParams.type == 2015) || (localLayoutParams.type == 2021)) {}
          while (localLayoutParams.type == 2036)
          {
            if ((m & 0x400) == 0) {
              break label3688;
            }
            i = this.mOverscanScreenLeft;
            localRect4.left = i;
            localRect3.left = i;
            localRect2.left = i;
            localRect1.left = i;
            i = this.mOverscanScreenTop;
            localRect4.top = i;
            localRect3.top = i;
            localRect2.top = i;
            localRect1.top = i;
            i = this.mOverscanScreenLeft + this.mOverscanScreenWidth;
            localRect4.right = i;
            localRect3.right = i;
            localRect2.right = i;
            localRect1.right = i;
            i = this.mOverscanScreenTop + this.mOverscanScreenHeight;
            localRect4.bottom = i;
            localRect3.bottom = i;
            localRect2.bottom = i;
            localRect1.bottom = i;
            break;
          }
          label3688:
          if (localLayoutParams.type == 2021)
          {
            i = this.mOverscanScreenLeft;
            localRect4.left = i;
            localRect3.left = i;
            localRect2.left = i;
            localRect1.left = i;
            i = this.mOverscanScreenTop;
            localRect4.top = i;
            localRect3.top = i;
            localRect2.top = i;
            localRect1.top = i;
            i = this.mOverscanScreenLeft + this.mOverscanScreenWidth;
            localRect4.right = i;
            localRect3.right = i;
            localRect2.right = i;
            localRect1.right = i;
            i = this.mOverscanScreenTop + this.mOverscanScreenHeight;
            localRect4.bottom = i;
            localRect3.bottom = i;
            localRect2.bottom = i;
            localRect1.bottom = i;
          }
          else if (((0x2000000 & m) != 0) && (localLayoutParams.type >= 1) && (localLayoutParams.type <= 1999))
          {
            i = this.mOverscanScreenLeft;
            localRect4.left = i;
            localRect3.left = i;
            localRect2.left = i;
            localRect1.left = i;
            i = this.mOverscanScreenTop;
            localRect4.top = i;
            localRect3.top = i;
            localRect2.top = i;
            localRect1.top = i;
            i = this.mOverscanScreenLeft + this.mOverscanScreenWidth;
            localRect4.right = i;
            localRect3.right = i;
            localRect2.right = i;
            localRect1.right = i;
            i = this.mOverscanScreenTop + this.mOverscanScreenHeight;
            localRect4.bottom = i;
            localRect3.bottom = i;
            localRect2.bottom = i;
            localRect1.bottom = i;
          }
          else
          {
            if ((canHideNavigationBar()) && ((i2 & 0x200) != 0))
            {
              if ((localLayoutParams.type == 2000) || (localLayoutParams.type == 2005)) {}
              while ((localLayoutParams.type == 2034) || (localLayoutParams.type == 2033) || ((localLayoutParams.type >= 1) && (localLayoutParams.type <= 1999)))
              {
                i = this.mUnrestrictedScreenLeft;
                localRect4.left = i;
                localRect3.left = i;
                localRect2.left = i;
                localRect1.left = i;
                i = this.mUnrestrictedScreenTop;
                localRect4.top = i;
                localRect3.top = i;
                localRect2.top = i;
                localRect1.top = i;
                i = this.mUnrestrictedScreenLeft + this.mUnrestrictedScreenWidth;
                localRect4.right = i;
                localRect3.right = i;
                localRect2.right = i;
                localRect1.right = i;
                i = this.mUnrestrictedScreenTop + this.mUnrestrictedScreenHeight;
                localRect4.bottom = i;
                localRect3.bottom = i;
                localRect2.bottom = i;
                localRect1.bottom = i;
                break;
              }
            }
            if ((i2 & 0x400) != 0)
            {
              i = this.mRestrictedScreenLeft;
              localRect3.left = i;
              localRect2.left = i;
              localRect1.left = i;
              i = this.mRestrictedScreenTop;
              localRect3.top = i;
              localRect2.top = i;
              localRect1.top = i;
              i = this.mRestrictedScreenLeft + this.mRestrictedScreenWidth;
              localRect3.right = i;
              localRect2.right = i;
              localRect1.right = i;
              i = this.mRestrictedScreenTop + this.mRestrictedScreenHeight;
              localRect3.bottom = i;
              localRect2.bottom = i;
              localRect1.bottom = i;
              if (i1 != 16)
              {
                localRect4.left = this.mDockLeft;
                localRect4.top = this.mDockTop;
                localRect4.right = this.mDockRight;
                localRect4.bottom = this.mDockBottom;
              }
              else
              {
                localRect4.left = this.mContentLeft;
                localRect4.top = this.mContentTop;
                localRect4.right = this.mContentRight;
                localRect4.bottom = this.mContentBottom;
              }
            }
            else
            {
              i = this.mRestrictedScreenLeft;
              localRect4.left = i;
              localRect3.left = i;
              localRect2.left = i;
              localRect1.left = i;
              i = this.mRestrictedScreenTop;
              localRect4.top = i;
              localRect3.top = i;
              localRect2.top = i;
              localRect1.top = i;
              i = this.mRestrictedScreenLeft + this.mRestrictedScreenWidth;
              localRect4.right = i;
              localRect3.right = i;
              localRect2.right = i;
              localRect1.right = i;
              i = this.mRestrictedScreenTop + this.mRestrictedScreenHeight;
              localRect4.bottom = i;
              localRect3.bottom = i;
              localRect2.bottom = i;
              localRect1.bottom = i;
            }
          }
        }
        label4520:
        localRect5.set(localRect4);
        break label256;
      }
      if (paramWindowState2 != null)
      {
        if (DEBUG_LAYOUT) {
          Slog.v("WindowManager", "layoutWindowLw(" + localLayoutParams.getTitle() + "): attached to " + paramWindowState2);
        }
        setAttachedWindowFrames(paramWindowState1, m, i1, paramWindowState2, false, localRect1, localRect2, localRect3, localRect4, localRect5);
        if (((localLayoutParams.type != 1005) && (localLayoutParams.type != 1002)) || ((m & 0x100) != 0) || (!localLayoutParams.getTitle().toString().contains("PopupWindow")) || (!paramWindowState2.hasMoved())) {
          break label256;
        }
        if (DEBUG_ONEPLUS) {
          Slog.d("WindowManager", "layoutWindowLw(" + localLayoutParams.getTitle() + "): type=" + localLayoutParams.type + " attached=" + paramWindowState2 + " pf:" + localRect1 + "->" + paramWindowState2.getLastFrameLw());
        }
        localRect1.set(paramWindowState2.getLastFrameLw());
        break label256;
      }
      if (DEBUG_LAYOUT) {
        Slog.v("WindowManager", "layoutWindowLw(" + localLayoutParams.getTitle() + "): normal window");
      }
      if ((localLayoutParams.type == 2014) || (localLayoutParams.type == 2020))
      {
        i = this.mRestrictedScreenLeft;
        localRect4.left = i;
        localRect3.left = i;
        localRect2.left = i;
        localRect1.left = i;
        i = this.mRestrictedScreenTop;
        localRect4.top = i;
        localRect3.top = i;
        localRect2.top = i;
        localRect1.top = i;
        i = this.mRestrictedScreenLeft + this.mRestrictedScreenWidth;
        localRect4.right = i;
        localRect3.right = i;
        localRect2.right = i;
        localRect1.right = i;
        i = this.mRestrictedScreenTop + this.mRestrictedScreenHeight;
        localRect4.bottom = i;
        localRect3.bottom = i;
        localRect2.bottom = i;
        localRect1.bottom = i;
        break label256;
      }
      if ((localLayoutParams.type == 2005) || (localLayoutParams.type == 2003))
      {
        i = this.mStableLeft;
        localRect4.left = i;
        localRect3.left = i;
        localRect2.left = i;
        localRect1.left = i;
        i = this.mStableTop;
        localRect4.top = i;
        localRect3.top = i;
        localRect2.top = i;
        localRect1.top = i;
        i = this.mStableRight;
        localRect4.right = i;
        localRect3.right = i;
        localRect2.right = i;
        localRect1.right = i;
        i = this.mStableBottom;
        localRect4.bottom = i;
        localRect3.bottom = i;
        localRect2.bottom = i;
        localRect1.bottom = i;
        break label256;
      }
      localRect1.left = this.mContentLeft;
      localRect1.top = this.mContentTop;
      localRect1.right = this.mContentRight;
      localRect1.bottom = this.mContentBottom;
      if (paramWindowState1.isVoiceInteraction())
      {
        i = this.mVoiceContentLeft;
        localRect4.left = i;
        localRect3.left = i;
        localRect2.left = i;
        i = this.mVoiceContentTop;
        localRect4.top = i;
        localRect3.top = i;
        localRect2.top = i;
        i = this.mVoiceContentRight;
        localRect4.right = i;
        localRect3.right = i;
        localRect2.right = i;
        i = this.mVoiceContentBottom;
        localRect4.bottom = i;
        localRect3.bottom = i;
        localRect2.bottom = i;
      }
      for (;;)
      {
        if (i1 == 48) {
          break label5474;
        }
        localRect5.left = this.mCurLeft;
        localRect5.top = this.mCurTop;
        localRect5.right = this.mCurRight;
        localRect5.bottom = this.mCurBottom;
        break;
        if (i1 != 16)
        {
          i = this.mDockLeft;
          localRect4.left = i;
          localRect3.left = i;
          localRect2.left = i;
          i = this.mDockTop;
          localRect4.top = i;
          localRect3.top = i;
          localRect2.top = i;
          i = this.mDockRight;
          localRect4.right = i;
          localRect3.right = i;
          localRect2.right = i;
          i = this.mDockBottom;
          localRect4.bottom = i;
          localRect3.bottom = i;
          localRect2.bottom = i;
        }
        else
        {
          i = this.mContentLeft;
          localRect4.left = i;
          localRect3.left = i;
          localRect2.left = i;
          i = this.mContentTop;
          localRect4.top = i;
          localRect3.top = i;
          localRect2.top = i;
          i = this.mContentRight;
          localRect4.right = i;
          localRect3.right = i;
          localRect2.right = i;
          i = this.mContentBottom;
          localRect4.bottom = i;
          localRect3.bottom = i;
          localRect2.bottom = i;
        }
      }
      label5474:
      localRect5.set(localRect4);
      break label256;
      label5484:
      localRect2.top = 55536;
      localRect2.left = 55536;
      localRect2.bottom = 10000;
      localRect2.right = 10000;
      if (localLayoutParams.type == 2013) {
        break label285;
      }
      localRect5.top = 55536;
      localRect5.left = 55536;
      localRect4.top = 55536;
      localRect4.left = 55536;
      localRect3.top = 55536;
      localRect3.left = 55536;
      localRect5.bottom = 10000;
      localRect5.right = 10000;
      localRect4.bottom = 10000;
      localRect4.right = 10000;
      localRect3.bottom = 10000;
      localRect3.right = 10000;
      break label285;
      label5626:
      if (j == 1)
      {
        ((Rect)localObject2).right += i;
        break label385;
      }
      if (j == 2)
      {
        ((Rect)localObject2).top -= i;
        break label385;
      }
      if (j != 3) {
        break label385;
      }
      ((Rect)localObject2).left -= i;
      break label385;
      label5689:
      paramWindowState2 = ((Rect)localObject1).toShortString();
      break label648;
      label5698:
      setLastInputMethodWindowLw(null, null);
      offsetInputMethodWindowLw(paramWindowState1);
    }
    label5712:
    offsetVoiceInputWindowLw(paramWindowState1);
  }
  
  public void lockNow(Bundle paramBundle)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
    this.mHandler.removeCallbacks(this.mScreenLockTimeout);
    if (paramBundle != null) {
      this.mScreenLockTimeout.setLockOptions(paramBundle);
    }
    this.mHandler.post(this.mScreenLockTimeout);
  }
  
  boolean needSensorRunningLp()
  {
    if (this.mSupportAutoRotation)
    {
      if ((this.mCurrentAppOrientation == 4) || (this.mCurrentAppOrientation == 10)) {}
      while ((this.mCurrentAppOrientation == 7) || (this.mCurrentAppOrientation == 6)) {
        return true;
      }
    }
    if ((this.mCarDockEnablesAccelerometer) && (this.mDockMode == 2)) {}
    while ((this.mDeskDockEnablesAccelerometer) && ((this.mDockMode == 1) || (this.mDockMode == 3) || (this.mDockMode == 4))) {
      return true;
    }
    if (this.mUserRotationMode == 1) {
      return false;
    }
    return this.mSupportAutoRotation;
  }
  
  public void notifyActivityDrawnForKeyguardLw()
  {
    if (this.mKeyguardDelegate != null) {
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          PhoneWindowManager.this.mKeyguardDelegate.onActivityDrawn();
        }
      });
    }
  }
  
  public void notifyAppLaunchFailedLw(String paramString) {}
  
  public void notifyCameraLensCoverSwitchChanged(long paramLong, boolean paramBoolean)
  {
    boolean bool = false;
    if (paramBoolean) {}
    for (int i = 1; this.mCameraLensCoverState == i; i = 0) {
      return;
    }
    if ((this.mCameraLensCoverState == 1) && (i == 0))
    {
      if (this.mKeyguardDelegate != null) {
        break label98;
      }
      paramBoolean = bool;
      if (!paramBoolean) {
        break label109;
      }
    }
    label98:
    label109:
    for (Intent localIntent = new Intent("android.media.action.STILL_IMAGE_CAMERA_SECURE");; localIntent = new Intent("android.media.action.STILL_IMAGE_CAMERA"))
    {
      wakeUp(paramLong / 1000000L, this.mAllowTheaterModeWakeFromCameraLens, "android.policy:CAMERA_COVER");
      startActivityAsUser(localIntent, UserHandle.CURRENT_OR_SELF);
      this.mCameraLensCoverState = i;
      return;
      paramBoolean = this.mKeyguardDelegate.isShowing();
      break;
    }
  }
  
  public void notifyLidSwitchChanged(long paramLong, boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 1; i == this.mLidState; i = 0) {
      return;
    }
    this.mLidState = i;
    applyLidSwitchState();
    updateRotation(true);
    if (paramBoolean) {
      wakeUp(SystemClock.uptimeMillis(), this.mAllowTheaterModeWakeFromLidSwitch, "android.policy:LID");
    }
    while (this.mLidControlsSleep) {
      return;
    }
    this.mPowerManager.userActivity(SystemClock.uptimeMillis(), false);
  }
  
  public void onConfigurationChanged()
  {
    Resources localResources = this.mContext.getResources();
    this.mStatusBarHeight = localResources.getDimensionPixelSize(17104921);
    int[] arrayOfInt = this.mNavigationBarHeightForRotationDefault;
    int i = this.mPortraitRotation;
    int j = localResources.getDimensionPixelSize(17104922);
    this.mNavigationBarHeightForRotationDefault[this.mUpsideDownRotation] = j;
    arrayOfInt[i] = j;
    arrayOfInt = this.mNavigationBarHeightForRotationDefault;
    i = this.mLandscapeRotation;
    j = localResources.getDimensionPixelSize(17104923);
    this.mNavigationBarHeightForRotationDefault[this.mSeascapeRotation] = j;
    arrayOfInt[i] = j;
    arrayOfInt = this.mNavigationBarWidthForRotationDefault;
    i = this.mPortraitRotation;
    j = localResources.getDimensionPixelSize(17104924);
    this.mNavigationBarWidthForRotationDefault[this.mSeascapeRotation] = j;
    this.mNavigationBarWidthForRotationDefault[this.mLandscapeRotation] = j;
    this.mNavigationBarWidthForRotationDefault[this.mUpsideDownRotation] = j;
    arrayOfInt[i] = j;
  }
  
  public void onKeyguardDone() {}
  
  public boolean performHapticFeedbackLw(WindowManagerPolicy.WindowState paramWindowState, int paramInt, boolean paramBoolean)
  {
    if (!this.mVibrator.hasVibrator()) {
      return false;
    }
    if (Settings.System.getIntForUser(this.mContext.getContentResolver(), "haptic_feedback_enabled", 0, -2) == 0) {}
    for (int i = 1; (i == 0) || (paramBoolean); i = 0) {
      switch (paramInt)
      {
      default: 
        return false;
      }
    }
    return false;
    long[] arrayOfLong = this.mLongPressVibePattern;
    if (paramWindowState != null) {
      paramInt = paramWindowState.getOwningUid();
    }
    for (paramWindowState = paramWindowState.getOwningPackage();; paramWindowState = this.mContext.getOpPackageName())
    {
      if (arrayOfLong.length != 1) {
        break label274;
      }
      this.mVibrator.vibrate(paramInt, paramWindowState, arrayOfLong[0], VIBRATION_ATTRIBUTES);
      return true;
      arrayOfLong = this.mLockPatternVibePattern;
      break;
      arrayOfLong = this.mKeyboardTapVibePattern;
      break;
      arrayOfLong = this.mClockTickVibePattern;
      break;
      arrayOfLong = this.mCalendarDateVibePattern;
      break;
      arrayOfLong = this.mSafeModeDisabledVibePattern;
      break;
      arrayOfLong = this.mSafeModeEnabledVibePattern;
      break;
      arrayOfLong = this.mContextClickVibePattern;
      break;
      arrayOfLong = this.mLockPatternVibePattern;
      break;
      paramInt = Process.myUid();
    }
    label274:
    this.mVibrator.vibrate(paramInt, paramWindowState, arrayOfLong, -1, VIBRATION_ATTRIBUTES);
    return true;
  }
  
  protected void preloadRecentApps()
  {
    if (isKeyguardShowingOrOccluded()) {
      return;
    }
    this.mPreloadedRecentApps = true;
    StatusBarManagerInternal localStatusBarManagerInternal = getStatusBarManagerInternal();
    if (localStatusBarManagerInternal != null) {
      localStatusBarManagerInternal.preloadRecentApps();
    }
  }
  
  public int prepareAddWindowLw(WindowManagerPolicy.WindowState paramWindowState, WindowManager.LayoutParams paramLayoutParams)
  {
    switch (paramLayoutParams.type)
    {
    }
    for (;;)
    {
      return 0;
      this.mContext.enforceCallingOrSelfPermission("android.permission.STATUS_BAR_SERVICE", "PhoneWindowManager");
      if ((this.mStatusBar != null) && (this.mStatusBar.isAlive())) {
        return -7;
      }
      this.mStatusBar = paramWindowState;
      this.mStatusBarController.setWindow(paramWindowState);
      continue;
      this.mContext.enforceCallingOrSelfPermission("android.permission.STATUS_BAR_SERVICE", "PhoneWindowManager");
      if ((this.mNavigationBar != null) && (this.mNavigationBar.isAlive())) {
        return -7;
      }
      this.mNavigationBar = paramWindowState;
      this.mNavigationBarController.setWindow(paramWindowState);
      if (DEBUG_LAYOUT)
      {
        Slog.i("WindowManager", "NAVIGATION BAR: " + this.mNavigationBar);
        continue;
        this.mContext.enforceCallingOrSelfPermission("android.permission.STATUS_BAR_SERVICE", "PhoneWindowManager");
        continue;
        if (this.mKeyguardScrim != null) {
          return -7;
        }
        this.mKeyguardScrim = paramWindowState;
      }
    }
  }
  
  void readLidState()
  {
    this.mLidState = this.mWindowManagerFuncs.getLidState();
  }
  
  public void registerShortcutKey(long paramLong, IShortcutService paramIShortcutService)
    throws RemoteException
  {
    synchronized (this.mLock)
    {
      IShortcutService localIShortcutService = (IShortcutService)this.mShortcutKeyServices.get(paramLong);
      if ((localIShortcutService != null) && (localIShortcutService.asBinder().pingBinder())) {
        throw new RemoteException("Key already exists.");
      }
    }
    this.mShortcutKeyServices.put(paramLong, paramIShortcutService);
  }
  
  public void removeStartingWindow(IBinder paramIBinder, View paramView)
  {
    if (DEBUG_STARTING_WINDOW) {
      Slog.v("WindowManager", "Removing starting window for " + paramIBinder + ": " + paramView + " Callers=" + Debug.getCallers(4));
    }
    if (paramView != null) {
      ((WindowManager)this.mContext.getSystemService("window")).removeView(paramView);
    }
  }
  
  public void removeWindowLw(WindowManagerPolicy.WindowState paramWindowState)
  {
    if (this.mStatusBar == paramWindowState)
    {
      this.mStatusBar = null;
      this.mStatusBarController.setWindow(null);
      this.mKeyguardDelegate.showScrim();
    }
    for (;;)
    {
      if (this.mNavigationBar == paramWindowState)
      {
        this.mNavigationBar = null;
        this.mNavigationBarController.setWindow(null);
      }
      return;
      if (this.mKeyguardScrim == paramWindowState)
      {
        Log.v("WindowManager", "Removing keyguard scrim");
        this.mKeyguardScrim = null;
      }
    }
  }
  
  public int rotationForOrientationLw(int paramInt1, int paramInt2)
  {
    if (this.mForceDefaultOrientation) {
      return 0;
    }
    for (;;)
    {
      int j;
      int i;
      for (;;)
      {
        synchronized (this.mLock)
        {
          j = this.mOrientationListener.getProposedRotation();
          i = j;
          if (j < 0) {
            i = paramInt2;
          }
          if ((this.mLidState == 1) && (this.mLidOpenRotation >= 0)) {
            i = this.mLidOpenRotation;
          }
          label57:
          label221:
          label250:
          label325:
          boolean bool;
          switch (paramInt1)
          {
          case 2: 
          case 3: 
          case 4: 
          case 5: 
          case 10: 
          default: 
            if (i >= 0)
            {
              return i;
              if ((this.mDockMode == 2) && ((this.mCarDockEnablesAccelerometer) || (this.mCarDockRotation >= 0)))
              {
                if (this.mCarDockEnablesAccelerometer) {
                  break;
                }
                i = this.mCarDockRotation;
                break;
              }
              if ((this.mDockMode == 1) || (this.mDockMode == 3)) {}
              while (this.mDockMode == 4)
              {
                if ((!this.mDeskDockEnablesAccelerometer) && (this.mDeskDockRotation < 0)) {
                  break label221;
                }
                if (!this.mDeskDockEnablesAccelerometer) {
                  break label250;
                }
                break;
              }
              if (((this.mHdmiPlugged) || (this.mWifiDisplayConnected)) && (this.mDemoHdmiRotationLock))
              {
                i = this.mDemoHdmiRotation;
                break;
                i = this.mDeskDockRotation;
                break;
              }
              if ((this.mWifiDisplayConnected) && (this.mWifiDisplayCustomRotation > -1))
              {
                i = this.mWifiDisplayCustomRotation;
                break;
              }
              if ((this.mHdmiPlugged) && (this.mDockMode == 0) && (this.mUndockedHdmiRotation >= 0))
              {
                i = this.mUndockedHdmiRotation;
                break;
              }
              if (!this.mDemoRotationLock) {
                break label655;
              }
              i = this.mDemoRotation;
              break;
              if (!this.mSupportAutoRotation)
              {
                i = -1;
                break;
              }
              if (this.mUserRotationMode == 0)
              {
                if ((paramInt1 != 2) && (paramInt1 != -1)) {
                  break label669;
                }
                if (this.mAllowAllRotations < 0)
                {
                  if (!this.mContext.getResources().getBoolean(17956920)) {
                    break label690;
                  }
                  j = 1;
                  this.mAllowAllRotations = j;
                }
                if (i != 2) {
                  break label666;
                }
                if (this.mAllowAllRotations != 1) {
                  break label696;
                }
                break label666;
              }
              if ((paramInt1 == 4) || (paramInt1 == 10) || (paramInt1 == 6) || (paramInt1 == 7)) {
                continue;
              }
              if ((this.mUserRotationMode != 1) || (paramInt1 == 5)) {
                break label713;
              }
              i = this.mUserRotation;
            }
            break;
          case 1: 
            bool = isAnyPortrait(i);
            if (bool) {
              return i;
            }
            paramInt1 = this.mPortraitRotation;
            return paramInt1;
          case 0: 
            bool = isLandscapeOrSeascape(i);
            if (bool) {
              return i;
            }
            paramInt1 = this.mLandscapeRotation;
            return paramInt1;
          case 9: 
            bool = isAnyPortrait(i);
            if (bool) {
              return i;
            }
            paramInt1 = this.mUpsideDownRotation;
            return paramInt1;
          case 8: 
            bool = isLandscapeOrSeascape(i);
            if (bool) {
              return i;
            }
            paramInt1 = this.mSeascapeRotation;
            return paramInt1;
          case 6: 
          case 11: 
            bool = isLandscapeOrSeascape(i);
            if (bool) {
              return i;
            }
            bool = isLandscapeOrSeascape(paramInt2);
            if (bool) {
              return paramInt2;
            }
            paramInt1 = this.mLandscapeRotation;
            return paramInt1;
          case 7: 
          case 12: 
            bool = isAnyPortrait(i);
            if (bool) {
              return i;
            }
            bool = isAnyPortrait(paramInt2);
            if (bool) {
              return paramInt2;
            }
            paramInt1 = this.mPortraitRotation;
            return paramInt1;
            return 0;
          }
        }
        label655:
        if (paramInt1 != 14) {
          break label325;
        }
        i = paramInt2;
        break label57;
        label666:
        break label57;
        label669:
        if ((paramInt1 != 11) && (paramInt1 != 12) && (paramInt1 != 13))
        {
          continue;
          label690:
          j = 0;
        }
      }
      label696:
      if ((paramInt1 != 10) && (paramInt1 != 13))
      {
        i = paramInt2;
        continue;
        label713:
        i = -1;
      }
    }
  }
  
  public boolean rotationHasCompatibleMetricsLw(int paramInt1, int paramInt2)
  {
    switch (paramInt1)
    {
    case 2: 
    case 3: 
    case 4: 
    case 5: 
    default: 
      return true;
    case 1: 
    case 7: 
    case 9: 
      return isAnyPortrait(paramInt2);
    }
    return isLandscapeOrSeascape(paramInt2);
  }
  
  public void screenTurnedOff()
  {
    if (DEBUG_WAKEUP) {
      Slog.i("WindowManager", "Screen turned off...");
    }
    updateScreenOffSleepToken(true);
    synchronized (this.mLock)
    {
      this.mScreenOnEarly = false;
      this.mScreenOnFully = false;
      this.mKeyguardDrawComplete = false;
      this.mWindowManagerDrawComplete = false;
      this.mScreenOnListener = null;
      updateOrientationListenerLp();
      if (this.mKeyguardDelegate != null) {
        this.mKeyguardDelegate.onScreenTurnedOff();
      }
      return;
    }
  }
  
  public void screenTurnedOn()
  {
    synchronized (this.mLock)
    {
      if (this.mKeyguardDelegate != null) {
        this.mKeyguardDelegate.onScreenTurnedOn();
      }
      return;
    }
  }
  
  public void screenTurningOn(WindowManagerPolicy.ScreenOnListener paramScreenOnListener)
  {
    if (DEBUG_WAKEUP) {
      Slog.i("WindowManager", "Screen turning on...");
    }
    updateScreenOffSleepToken(false);
    synchronized (this.mLock)
    {
      this.mScreenOnEarly = true;
      this.mScreenOnFully = false;
      this.mKeyguardDrawComplete = false;
      this.mWindowManagerDrawComplete = false;
      this.mScreenOnListener = paramScreenOnListener;
      if (this.mKeyguardDelegate != null)
      {
        this.mHandler.removeMessages(6);
        this.mHandler.sendEmptyMessageDelayed(6, 1000L);
        this.mKeyguardDelegate.onScreenTurningOn(this.mKeyguardDrawnCallback);
        return;
      }
      if (DEBUG_WAKEUP) {
        Slog.d("WindowManager", "null mKeyguardDelegate: setting mKeyguardDrawComplete.");
      }
      finishKeyguardDrawn();
    }
  }
  
  public int selectAnimationLw(WindowManagerPolicy.WindowState paramWindowState, int paramInt)
  {
    if (paramWindowState == this.mStatusBar)
    {
      int i;
      if ((paramWindowState.getAttrs().privateFlags & 0x400) != 0) {
        i = 1;
      }
      while ((paramInt == 2) || (paramInt == 4)) {
        if (i != 0)
        {
          return -1;
          i = 0;
        }
        else
        {
          return 17432619;
        }
      }
      if ((paramInt == 1) || (paramInt == 3))
      {
        if (i != 0) {
          return -1;
        }
        return 17432618;
      }
    }
    else if (paramWindowState == this.mNavigationBar)
    {
      if (paramWindowState.getAttrs().windowAnimations != 0) {
        return 0;
      }
      if (this.mNavigationBarPosition == 0)
      {
        if ((paramInt == 2) || (paramInt == 4))
        {
          if (isKeyguardShowingAndNotOccluded()) {
            return 17432613;
          }
          return 17432612;
        }
        if ((paramInt == 1) || (paramInt == 3)) {
          return 17432611;
        }
      }
      else if (this.mNavigationBarPosition == 1)
      {
        if ((paramInt == 2) || (paramInt == 4)) {
          return 17432617;
        }
        if ((paramInt == 1) || (paramInt == 3)) {
          return 17432616;
        }
      }
      else if (this.mNavigationBarPosition == 2)
      {
        if ((paramInt == 2) || (paramInt == 4)) {
          return 17432615;
        }
        if ((paramInt == 1) || (paramInt == 3)) {
          return 17432614;
        }
      }
    }
    else if (paramWindowState.getAttrs().type == 2034)
    {
      return selectDockedDividerAnimationLw(paramWindowState, paramInt);
    }
    if (paramInt == 5)
    {
      if (paramWindowState.hasAppShownWindows()) {
        return 17432593;
      }
    }
    else if ((paramWindowState.getAttrs().type == 2023) && (this.mDreamingLockscreen) && (paramInt == 1)) {
      return -1;
    }
    return 0;
  }
  
  public void selectRotationAnimationLw(int[] paramArrayOfInt)
  {
    if (this.mTopFullscreenOpaqueWindowState != null)
    {
      int j = this.mTopFullscreenOpaqueWindowState.getRotationAnimationHint();
      int i = j;
      if (j < 0)
      {
        i = j;
        if (this.mTopIsFullscreen) {
          i = this.mTopFullscreenOpaqueWindowState.getAttrs().rotationAnimation;
        }
      }
      switch (i)
      {
      default: 
        paramArrayOfInt[1] = 0;
        paramArrayOfInt[0] = 0;
        return;
      case 1: 
      case 3: 
        paramArrayOfInt[0] = 17432686;
        paramArrayOfInt[1] = 17432684;
        return;
      }
      paramArrayOfInt[0] = 17432685;
      paramArrayOfInt[1] = 17432684;
      return;
    }
    paramArrayOfInt[1] = 0;
    paramArrayOfInt[0] = 0;
  }
  
  void sendCloseSystemWindows()
  {
    PhoneWindow.sendCloseSystemWindows(this.mContext, null);
  }
  
  void sendCloseSystemWindows(String paramString)
  {
    PhoneWindow.sendCloseSystemWindows(this.mContext, paramString);
  }
  
  void setAttachedWindowFrames(WindowManagerPolicy.WindowState paramWindowState1, int paramInt1, int paramInt2, WindowManagerPolicy.WindowState paramWindowState2, boolean paramBoolean, Rect paramRect1, Rect paramRect2, Rect paramRect3, Rect paramRect4, Rect paramRect5)
  {
    if ((paramWindowState1.getSurfaceLayer() > this.mDockLayer) && (paramWindowState2.getSurfaceLayer() < this.mDockLayer))
    {
      paramInt2 = this.mDockLeft;
      paramRect5.left = paramInt2;
      paramRect4.left = paramInt2;
      paramRect3.left = paramInt2;
      paramRect2.left = paramInt2;
      paramInt2 = this.mDockTop;
      paramRect5.top = paramInt2;
      paramRect4.top = paramInt2;
      paramRect3.top = paramInt2;
      paramRect2.top = paramInt2;
      paramInt2 = this.mDockRight;
      paramRect5.right = paramInt2;
      paramRect4.right = paramInt2;
      paramRect3.right = paramInt2;
      paramRect2.right = paramInt2;
      paramInt2 = this.mDockBottom;
      paramRect5.bottom = paramInt2;
      paramRect4.bottom = paramInt2;
      paramRect3.bottom = paramInt2;
      paramRect2.bottom = paramInt2;
      if ((paramInt1 & 0x100) == 0) {
        paramRect2 = paramWindowState2.getFrameLw();
      }
      paramRect1.set(paramRect2);
      return;
    }
    if (paramInt2 != 16) {
      if ((0x40000000 & paramInt1) != 0)
      {
        paramWindowState1 = paramWindowState2.getContentFrameLw();
        label190:
        paramRect4.set(paramWindowState1);
        label196:
        if (!paramBoolean) {
          break label472;
        }
      }
    }
    label472:
    for (paramWindowState1 = paramWindowState2.getDisplayFrameLw();; paramWindowState1 = paramRect4)
    {
      paramRect2.set(paramWindowState1);
      if (paramBoolean) {
        paramRect4 = paramWindowState2.getOverscanFrameLw();
      }
      paramRect3.set(paramRect4);
      paramRect5.set(paramWindowState2.getVisibleFrameLw());
      break;
      paramWindowState1 = paramWindowState2.getOverscanFrameLw();
      break label190;
      paramRect4.set(paramWindowState2.getContentFrameLw());
      if (paramWindowState2.isVoiceInteraction())
      {
        if (paramRect4.left < this.mVoiceContentLeft) {
          paramRect4.left = this.mVoiceContentLeft;
        }
        if (paramRect4.top < this.mVoiceContentTop) {
          paramRect4.top = this.mVoiceContentTop;
        }
        if (paramRect4.right > this.mVoiceContentRight) {
          paramRect4.right = this.mVoiceContentRight;
        }
        if (paramRect4.bottom <= this.mVoiceContentBottom) {
          break label196;
        }
        paramRect4.bottom = this.mVoiceContentBottom;
        break label196;
      }
      if (paramWindowState2.getSurfaceLayer() >= this.mDockLayer) {
        break label196;
      }
      if (paramRect4.left < this.mContentLeft) {
        paramRect4.left = this.mContentLeft;
      }
      if (paramRect4.top < this.mContentTop) {
        paramRect4.top = this.mContentTop;
      }
      if (paramRect4.right > this.mContentRight) {
        paramRect4.right = this.mContentRight;
      }
      if (paramRect4.bottom <= this.mContentBottom) {
        break label196;
      }
      paramRect4.bottom = this.mContentBottom;
      break label196;
    }
  }
  
  public void setCurrentOrientationLw(int paramInt)
  {
    synchronized (this.mLock)
    {
      if (paramInt != this.mCurrentAppOrientation)
      {
        this.mCurrentAppOrientation = paramInt;
        updateOrientationListenerLp();
      }
      return;
    }
  }
  
  public void setCurrentUserLw(int paramInt)
  {
    this.mCurrentUserId = paramInt;
    if (this.mKeyguardDelegate != null) {
      this.mKeyguardDelegate.setCurrentUser(paramInt);
    }
    StatusBarManagerInternal localStatusBarManagerInternal = getStatusBarManagerInternal();
    if (localStatusBarManagerInternal != null) {
      localStatusBarManagerInternal.setCurrentUser(paramInt);
    }
    setLastInputMethodWindowLw(null, null);
  }
  
  public void setDisplayOverscan(Display paramDisplay, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (paramDisplay.getDisplayId() == 0)
    {
      this.mOverscanLeft = paramInt1;
      this.mOverscanTop = paramInt2;
      this.mOverscanRight = paramInt3;
      this.mOverscanBottom = paramInt4;
    }
  }
  
  void setHdmiPlugged(boolean paramBoolean)
  {
    if (this.mHdmiPlugged != paramBoolean)
    {
      this.mHdmiPlugged = paramBoolean;
      updateRotation(true, true);
      Intent localIntent = new Intent("android.intent.action.HDMI_PLUGGED");
      localIntent.addFlags(67108864);
      localIntent.putExtra("state", paramBoolean);
      this.mContext.sendStickyBroadcastAsUser(localIntent, UserHandle.ALL);
    }
  }
  
  public void setInitialDisplaySize(Display paramDisplay, int paramInt1, int paramInt2, int paramInt3)
  {
    if ((this.mContext == null) || (paramDisplay.getDisplayId() != 0)) {
      return;
    }
    this.mDisplay = paramDisplay;
    paramDisplay = this.mContext.getResources();
    int j;
    int i;
    int k;
    label107:
    String str;
    if (paramInt1 > paramInt2)
    {
      j = paramInt2;
      i = paramInt1;
      this.mLandscapeRotation = 0;
      this.mSeascapeRotation = 2;
      if (paramDisplay.getBoolean(17956921))
      {
        this.mPortraitRotation = 1;
        this.mUpsideDownRotation = 3;
        j = j * 160 / paramInt3;
        paramInt3 = i * 160 / paramInt3;
        if ((paramInt1 == paramInt2) || (j >= 600)) {
          break label366;
        }
        k = 1;
        this.mNavigationBarCanMove = k;
        this.mHasNavigationBar = paramDisplay.getBoolean(17956971);
        str = SystemProperties.get("qemu.hw.mainkeys");
        if (!"1".equals(str)) {
          break label372;
        }
        this.mHasNavigationBar = false;
        label148:
        k = this.mHasNavigationBar;
        if (Settings.System.getInt(this.mContext.getContentResolver(), "buttons_show_on_screen_navkeys", 0) != 1) {
          break label391;
        }
        paramInt1 = 1;
        label174:
        this.mHasNavigationBar = (paramInt1 | k);
        if (!"portrait".equals(SystemProperties.get("persist.demo.hdmirotation"))) {
          break label396;
        }
        this.mDemoHdmiRotation = this.mPortraitRotation;
        label205:
        this.mDemoHdmiRotationLock = SystemProperties.getBoolean("persist.demo.hdmirotationlock", false);
        if (!"portrait".equals(SystemProperties.get("persist.demo.remoterotation"))) {
          break label407;
        }
        this.mDemoRotation = this.mPortraitRotation;
        label239:
        this.mDemoRotationLock = SystemProperties.getBoolean("persist.demo.rotationlock", false);
        if ((paramInt3 < 960) || (j < 720) || (!paramDisplay.getBoolean(17956999))) {
          break label424;
        }
        if (!"true".equals(SystemProperties.get("config.override_forced_orient"))) {
          break label418;
        }
        k = 0;
      }
    }
    for (;;)
    {
      this.mForceDefaultOrientation = k;
      return;
      this.mPortraitRotation = 3;
      this.mUpsideDownRotation = 1;
      break;
      j = paramInt1;
      i = paramInt2;
      this.mPortraitRotation = 0;
      this.mUpsideDownRotation = 2;
      if (paramDisplay.getBoolean(17956921))
      {
        this.mLandscapeRotation = 3;
        this.mSeascapeRotation = 1;
        break;
      }
      this.mLandscapeRotation = 1;
      this.mSeascapeRotation = 3;
      break;
      label366:
      k = 0;
      break label107;
      label372:
      if (!"0".equals(str)) {
        break label148;
      }
      this.mHasNavigationBar = true;
      break label148;
      label391:
      paramInt1 = 0;
      break label174;
      label396:
      this.mDemoHdmiRotation = this.mLandscapeRotation;
      break label205;
      label407:
      this.mDemoRotation = this.mLandscapeRotation;
      break label239;
      label418:
      k = 1;
      continue;
      label424:
      k = 0;
    }
  }
  
  boolean setKeyguardOccludedLw(boolean paramBoolean)
  {
    boolean bool1 = this.mKeyguardOccluded;
    boolean bool2 = this.mKeyguardDelegate.isShowing();
    if ((!bool1) || (paramBoolean)) {}
    while ((!bool1) && (paramBoolean) && (bool2))
    {
      this.mKeyguardOccluded = true;
      this.mKeyguardDelegate.setOccluded(true, false);
      Object localObject = this.mStatusBar.getAttrs();
      ((WindowManager.LayoutParams)localObject).privateFlags &= 0xFBFF;
      localObject = this.mStatusBar.getAttrs();
      ((WindowManager.LayoutParams)localObject).flags &= 0xFFEFFFFF;
      return true;
      if (bool2)
      {
        this.mKeyguardOccluded = false;
        this.mKeyguardDelegate.setOccluded(false, false);
        localObject = this.mStatusBar.getAttrs();
        ((WindowManager.LayoutParams)localObject).privateFlags |= 0x400;
        if (!this.mKeyguardDelegate.hasLockscreenWallpaper())
        {
          localObject = this.mStatusBar.getAttrs();
          ((WindowManager.LayoutParams)localObject).flags |= 0x100000;
        }
        localObject = AnimationUtils.loadAnimation(this.mContext, 17432750);
        this.mWindowManagerFuncs.overridePlayingAppAnimationsLw((Animation)localObject);
        return true;
      }
    }
    return false;
  }
  
  public void setLastInputMethodWindowLw(WindowManagerPolicy.WindowState paramWindowState1, WindowManagerPolicy.WindowState paramWindowState2)
  {
    this.mLastInputMethodWindow = paramWindowState1;
    this.mLastInputMethodTargetWindow = paramWindowState2;
  }
  
  public void setRecentsVisibilityLw(boolean paramBoolean)
  {
    this.mRecentsVisible = paramBoolean;
  }
  
  public void setRotationLw(int paramInt)
  {
    this.mOrientationListener.setCurrentRotation(paramInt);
  }
  
  public void setSafeMode(boolean paramBoolean)
  {
    this.mSafeMode = paramBoolean;
    if (paramBoolean) {}
    for (int i = 10001;; i = 10000)
    {
      performHapticFeedbackLw(null, i, true);
      return;
    }
  }
  
  public void setTvPipVisibilityLw(boolean paramBoolean)
  {
    this.mTvPictureInPictureVisible = paramBoolean;
  }
  
  public void setUserRotationMode(int paramInt1, int paramInt2)
  {
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    if (paramInt1 == 1)
    {
      Settings.System.putIntForUser(localContentResolver, "user_rotation", paramInt2, -2);
      Settings.System.putIntForUser(localContentResolver, "accelerometer_rotation", 0, -2);
      return;
    }
    Settings.System.putIntForUser(localContentResolver, "accelerometer_rotation", 1, -2);
  }
  
  public boolean shouldRotateSeamlessly(int paramInt1, int paramInt2)
  {
    if ((paramInt1 == this.mUpsideDownRotation) || (paramInt2 == this.mUpsideDownRotation)) {
      return false;
    }
    paramInt2 -= paramInt1;
    paramInt1 = paramInt2;
    if (paramInt2 < 0) {
      paramInt1 = paramInt2 + 4;
    }
    if (paramInt1 == 2) {
      return false;
    }
    WindowManagerPolicy.WindowState localWindowState = this.mTopFullscreenOpaqueWindowState;
    if (localWindowState != this.mFocusedWindow) {
      return false;
    }
    if ((localWindowState == null) || (localWindowState.isAnimatingLw())) {}
    while ((localWindowState.getAttrs().rotationAnimation != 2) && (localWindowState.getAttrs().rotationAnimation != 3)) {
      return false;
    }
    return true;
  }
  
  public void showBootMessage(final CharSequence paramCharSequence, boolean paramBoolean)
  {
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        int i;
        if (PhoneWindowManager.this.mBootMsgDialog == null)
        {
          if (!PhoneWindowManager.this.mContext.getPackageManager().hasSystemFeature("android.hardware.type.television")) {
            break label219;
          }
          i = 16975021;
          PhoneWindowManager.this.mBootMsgDialog = new ProgressDialog(PhoneWindowManager.this.mContext, i)
          {
            public boolean dispatchGenericMotionEvent(MotionEvent paramAnonymous2MotionEvent)
            {
              return true;
            }
            
            public boolean dispatchKeyEvent(KeyEvent paramAnonymous2KeyEvent)
            {
              return true;
            }
            
            public boolean dispatchKeyShortcutEvent(KeyEvent paramAnonymous2KeyEvent)
            {
              return true;
            }
            
            public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent paramAnonymous2AccessibilityEvent)
            {
              return true;
            }
            
            public boolean dispatchTouchEvent(MotionEvent paramAnonymous2MotionEvent)
            {
              return true;
            }
            
            public boolean dispatchTrackballEvent(MotionEvent paramAnonymous2MotionEvent)
            {
              return true;
            }
          };
          if (!PhoneWindowManager.this.mContext.getPackageManager().isUpgrade()) {
            break label224;
          }
          PhoneWindowManager.this.mBootMsgDialog.setTitle(17040321);
        }
        for (;;)
        {
          PhoneWindowManager.this.mBootMsgDialog.setProgressStyle(0);
          PhoneWindowManager.this.mBootMsgDialog.setIndeterminate(true);
          PhoneWindowManager.this.mBootMsgDialog.getWindow().setType(2021);
          PhoneWindowManager.this.mBootMsgDialog.getWindow().addFlags(258);
          PhoneWindowManager.this.mBootMsgDialog.getWindow().setDimAmount(1.0F);
          WindowManager.LayoutParams localLayoutParams = PhoneWindowManager.this.mBootMsgDialog.getWindow().getAttributes();
          localLayoutParams.screenOrientation = 5;
          PhoneWindowManager.this.mBootMsgDialog.getWindow().setAttributes(localLayoutParams);
          PhoneWindowManager.this.mBootMsgDialog.setCancelable(false);
          PhoneWindowManager.this.mBootMsgDialog.show();
          PhoneWindowManager.this.mBootMsgDialog.setMessage(paramCharSequence);
          return;
          label219:
          i = 0;
          break;
          label224:
          PhoneWindowManager.this.mBootMsgDialog.setTitle(17040322);
        }
      }
    });
  }
  
  public void showGlobalActions()
  {
    this.mHandler.removeMessages(10);
    this.mHandler.sendEmptyMessage(10);
  }
  
  void showGlobalActionsInternal()
  {
    sendCloseSystemWindows("globalactions");
    if (this.mGlobalActions == null) {
      this.mGlobalActions = new OpGlobalActions(this.mContext, this.mWindowManagerFuncs);
    }
    boolean bool2 = isKeyguardShowingAndNotOccluded();
    boolean bool1 = isKeyguardSecure(this.mCurrentUserId);
    OpGlobalActions localOpGlobalActions = this.mGlobalActions;
    if (bool2) {}
    for (;;)
    {
      localOpGlobalActions.showDialog(bool1, isDeviceProvisioned());
      stopLongshot();
      if (bool2) {
        this.mPowerManager.userActivity(SystemClock.uptimeMillis(), false);
      }
      return;
      bool1 = false;
    }
  }
  
  public void showRecentApps(boolean paramBoolean)
  {
    this.mHandler.removeMessages(9);
    Handler localHandler = this.mHandler;
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localHandler.obtainMessage(9, i, 0).sendToTarget();
      return;
    }
  }
  
  protected void startActivityAsUser(Intent paramIntent, UserHandle paramUserHandle)
  {
    if (isUserSetupComplete())
    {
      this.mContext.startActivityAsUser(paramIntent, paramUserHandle);
      return;
    }
    Slog.i("WindowManager", "Not starting activity because user setup is in progress: " + paramIntent);
  }
  
  void startDockOrHome(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean2) {
      awakenDreams();
    }
    Intent localIntent1 = createHomeDockIntent();
    if (localIntent1 != null)
    {
      if (paramBoolean1) {}
      try
      {
        localIntent1.putExtra("android.intent.extra.FROM_HOME_KEY", paramBoolean1);
        startActivityAsUser(localIntent1, UserHandle.CURRENT);
        return;
      }
      catch (ActivityNotFoundException localActivityNotFoundException) {}
    }
    Intent localIntent2;
    if (paramBoolean1)
    {
      localIntent2 = new Intent(this.mHomeIntent);
      localIntent2.putExtra("android.intent.extra.FROM_HOME_KEY", paramBoolean1);
    }
    for (;;)
    {
      startActivityAsUser(localIntent2, UserHandle.CURRENT);
      return;
      localIntent2 = this.mHomeIntent;
    }
  }
  
  public void startKeyguardExitAnimation(long paramLong1, long paramLong2)
  {
    if (this.mKeyguardDelegate != null)
    {
      if (DEBUG_KEYGUARD) {
        Slog.d("WindowManager", "PWM.startKeyguardExitAnimation");
      }
      this.mKeyguardDelegate.startKeyguardExitAnimation(paramLong1, paramLong2);
    }
  }
  
  public void startedGoingToSleep(int paramInt)
  {
    if (DEBUG_WAKEUP) {
      Slog.i("WindowManager", "Started going to sleep... (why=" + paramInt + ")");
    }
    this.mCameraGestureTriggeredDuringGoingToSleep = false;
    this.mGoingToSleep = true;
    if (this.mKeyguardDelegate != null) {
      this.mKeyguardDelegate.onStartedGoingToSleep(paramInt);
    }
  }
  
  public void startedWakingUp()
  {
    EventLog.writeEvent(70000, 1);
    if (DEBUG_WAKEUP) {
      Slog.i("WindowManager", "Started waking up...");
    }
    synchronized (this.mLock)
    {
      this.mAwake = true;
      updateWakeGestureListenerLp();
      updateOrientationListenerLp();
      updateLockScreenTimeout();
      if (this.mKeyguardDelegate != null) {
        this.mKeyguardDelegate.onStartedWakingUp();
      }
      return;
    }
  }
  
  public void stopLongshotConnection()
  {
    synchronized (this.mScreenshotLock)
    {
      if (this.mScreenshotConnection != null)
      {
        this.mContext.unbindService(this.mScreenshotConnection);
        this.mScreenshotConnection = null;
        this.mHandler.removeCallbacks(this.mScreenshotTimeout);
      }
      return;
    }
  }
  
  public int subWindowTypeToLayerLw(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      Log.e("WindowManager", "Unknown sub-window type: " + paramInt);
      return 0;
    case 1000: 
    case 1003: 
      return 1;
    case 1001: 
      return -2;
    case 1004: 
      return -1;
    case 1002: 
      return 2;
    }
    return 3;
  }
  
  public void systemBooted()
  {
    int i = 0;
    for (;;)
    {
      synchronized (this.mLock)
      {
        KeyguardServiceDelegate localKeyguardServiceDelegate = this.mKeyguardDelegate;
        if (localKeyguardServiceDelegate != null)
        {
          i = 1;
          if (i != 0)
          {
            this.mKeyguardDelegate.bindService(this.mContext);
            this.mKeyguardDelegate.onBootCompleted();
          }
        }
      }
      synchronized (this.mLock)
      {
        this.mSystemBooted = true;
        startedWakingUp();
        screenTurningOn(null);
        screenTurnedOn();
        return;
        this.mDeferBindKeyguard = true;
        continue;
        localObject2 = finally;
        throw ((Throwable)localObject2);
      }
    }
  }
  
  public void systemReady()
  {
    this.mKeyguardDelegate = new KeyguardServiceDelegate(this.mContext, new -void_systemReady__LambdaImpl0(), this.mKeyguardFingerprintChanageCallback);
    this.mKeyguardDelegate.onSystemReady();
    readCameraLensCoverState();
    updateUiMode();
    synchronized (this.mLock)
    {
      updateOrientationListenerLp();
      this.mSystemReady = true;
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          PhoneWindowManager.this.updateSettings();
        }
      });
      boolean bool = this.mDeferBindKeyguard;
      if (bool) {
        this.mDeferBindKeyguard = false;
      }
      if (bool)
      {
        this.mKeyguardDelegate.bindService(this.mContext);
        this.mKeyguardDelegate.onBootCompleted();
      }
      this.mSystemGestures.systemReady();
      this.mImmersiveModeConfirmation.systemReady();
      return;
    }
  }
  
  protected void takeScreenshot(final int paramInt)
  {
    for (;;)
    {
      synchronized (this.mScreenshotLock)
      {
        if (this.mScreenshotConnection != null)
        {
          Slog.d("WindowManager", "not handle next screenshot before finishing");
          return;
        }
        final Object localObject3 = new ComponentName("com.android.systemui", "com.android.systemui.screenshot.TakeScreenshotService");
        ComponentName localComponentName = new ComponentName("com.oneplus.screenshot", "com.oneplus.screenshot.TakeScreenshotService");
        boolean bool2;
        Intent localIntent;
        if (!this.mWindowManagerInternal.isStackVisible(2))
        {
          bool1 = this.mWindowManagerInternal.isStackVisible(3);
          bool2 = this.mWindowManagerInternal.isMinimizedDock();
          localIntent = new Intent();
          if (paramInt == 2)
          {
            bool1 = false;
            localIntent.setComponent((ComponentName)localObject3);
            localObject3 = new Bundle();
            ((Bundle)localObject3).putBoolean("longshot", bool1);
            if (this.mFocusedWindow != null) {
              ((Bundle)localObject3).putString("focusWindow", this.mFocusedWindow.getAttrs().packageName);
            }
            localObject3 = new ServiceConnection()
            {
              public void onServiceConnected(ComponentName arg1, IBinder paramAnonymousIBinder)
              {
                synchronized (PhoneWindowManager.this.mScreenshotLock)
                {
                  Object localObject = PhoneWindowManager.this.mScreenshotConnection;
                  if (localObject != this) {
                    return;
                  }
                  paramAnonymousIBinder = new Messenger(paramAnonymousIBinder);
                  localObject = Message.obtain(null, paramInt);
                  ((Message)localObject).replyTo = new Messenger(new Handler(PhoneWindowManager.this.mHandler.getLooper())
                  {
                    public void handleMessage(Message arg1)
                    {
                      synchronized (PhoneWindowManager.this.mScreenshotLock)
                      {
                        if (PhoneWindowManager.this.mScreenshotConnection == jdField_this)
                        {
                          PhoneWindowManager.this.mContext.unbindService(PhoneWindowManager.this.mScreenshotConnection);
                          PhoneWindowManager.this.mScreenshotConnection = null;
                          PhoneWindowManager.this.mHandler.removeCallbacks(PhoneWindowManager.this.mScreenshotTimeout);
                        }
                        return;
                      }
                    }
                  });
                  ((Message)localObject).arg2 = 0;
                  ((Message)localObject).arg1 = 0;
                  if ((PhoneWindowManager.this.mStatusBar != null) && (PhoneWindowManager.this.mStatusBar.isVisibleLw())) {
                    ((Message)localObject).arg1 = 1;
                  }
                  if ((PhoneWindowManager.this.mNavigationBar != null) && (PhoneWindowManager.this.mNavigationBar.isVisibleLw())) {
                    ((Message)localObject).arg2 = 1;
                  }
                  ((Message)localObject).obj = localObject3;
                  try
                  {
                    paramAnonymousIBinder.send((Message)localObject);
                    return;
                  }
                  catch (RemoteException paramAnonymousIBinder)
                  {
                    for (;;) {}
                  }
                }
              }
              
              public void onServiceDisconnected(ComponentName arg1)
              {
                synchronized (PhoneWindowManager.this.mScreenshotLock)
                {
                  if (PhoneWindowManager.this.mScreenshotConnection != null)
                  {
                    PhoneWindowManager.this.mContext.unbindService(PhoneWindowManager.this.mScreenshotConnection);
                    PhoneWindowManager.this.mScreenshotConnection = null;
                    PhoneWindowManager.this.mHandler.removeCallbacks(PhoneWindowManager.this.mScreenshotTimeout);
                    PhoneWindowManager.-wrap13(PhoneWindowManager.this);
                  }
                  return;
                }
              }
            };
            if (this.mContext.bindServiceAsUser(localIntent, (ServiceConnection)localObject3, 33554433, UserHandle.CURRENT))
            {
              this.mScreenshotConnection = ((ServiceConnection)localObject3);
              if (!bool1) {
                break label284;
              }
              this.mHandler.postDelayed(this.mScreenshotTimeout, 60000L);
            }
          }
        }
        else
        {
          bool1 = true;
          continue;
        }
        if ((keyguardOn()) || (!isUserSetupComplete()) || (!isDeviceProvisioned()) || ((bool1) && (!bool2))) {
          break label302;
        }
        if (this.mDisplayRotation != 0)
        {
          break label302;
          localIntent.setComponent(localComponentName);
        }
      }
      boolean bool1 = true;
      continue;
      label284:
      this.mHandler.postDelayed(this.mScreenshotTimeout, 10000L);
      continue;
      label302:
      bool1 = false;
    }
  }
  
  protected void toggleRecentApps()
  {
    if (isKeyguardShowingOrOccluded()) {
      return;
    }
    this.mPreloadedRecentApps = false;
    StatusBarManagerInternal localStatusBarManagerInternal = getStatusBarManagerInternal();
    if (localStatusBarManagerInternal != null) {
      localStatusBarManagerInternal.toggleRecentApps();
    }
  }
  
  void updateOrientationListenerLp()
  {
    if (!this.mOrientationListener.canDetectOrientation()) {
      return;
    }
    if ((localLOGV) || (DEBUG_ONEPLUS)) {
      Slog.v("WindowManager", "mScreenOnEarly=" + this.mScreenOnEarly + ", mAwake=" + this.mAwake + ", mCurrentAppOrientation=" + this.mCurrentAppOrientation + ", mOrientationSensorEnabled=" + this.mOrientationSensorEnabled + ", mKeyguardDrawComplete=" + this.mKeyguardDrawComplete + ", mWindowManagerDrawComplete=" + this.mWindowManagerDrawComplete);
    }
    int j = 1;
    int i = j;
    if (this.mScreenOnEarly)
    {
      i = j;
      if (this.mAwake)
      {
        i = j;
        if (this.mKeyguardDrawComplete)
        {
          i = j;
          if (this.mWindowManagerDrawComplete)
          {
            i = j;
            if (needSensorRunningLp())
            {
              j = 0;
              i = j;
              if (!this.mOrientationSensorEnabled)
              {
                this.mOrientationListener.enable();
                if ((localLOGV) || (DEBUG_ONEPLUS)) {
                  Slog.v("WindowManager", "Enabling listeners");
                }
                this.mOrientationSensorEnabled = true;
                i = j;
              }
            }
          }
        }
      }
    }
    if ((i != 0) && (this.mOrientationSensorEnabled))
    {
      this.mOrientationListener.disable();
      if ((localLOGV) || (DEBUG_ONEPLUS)) {
        Slog.v("WindowManager", "Disabling listeners");
      }
      this.mOrientationSensorEnabled = false;
    }
  }
  
  void updateRotation(boolean paramBoolean)
  {
    try
    {
      this.mWindowManager.updateRotation(paramBoolean, false);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  void updateRotation(boolean paramBoolean1, boolean paramBoolean2)
  {
    try
    {
      this.mWindowManager.updateRotation(paramBoolean1, paramBoolean2);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public void updateSettings()
  {
    Object localObject2 = this.mContext.getContentResolver();
    int i = 0;
    for (;;)
    {
      boolean bool;
      int j;
      synchronized (this.mLock)
      {
        this.mEndcallBehavior = Settings.System.getIntForUser((ContentResolver)localObject2, "end_button_behavior", 2, -2);
        this.mIncallPowerBehavior = Settings.Secure.getIntForUser((ContentResolver)localObject2, "incall_power_button_behavior", 1, -2);
        if (Settings.Secure.getIntForUser((ContentResolver)localObject2, "wake_gesture_enabled", 0, -2) != 0)
        {
          bool = true;
          if (this.mWakeGestureEnabledSetting != bool)
          {
            this.mWakeGestureEnabledSetting = bool;
            updateWakeGestureListenerLp();
          }
          if (Settings.System.getInt((ContentResolver)localObject2, "buttons_show_on_screen_navkeys", 0) != 1) {
            break label409;
          }
          bool = true;
          this.mHasNavigationBar = bool;
          j = Settings.System.getIntForUser((ContentResolver)localObject2, "vibrate_on_touch_intensity", 1, -2);
          if (this.mLockPatternVibePattern != null)
          {
            if ((DEBUG_ONEPLUS) && (this.mVibrateOnTouchIntensity != j)) {
              Log.d("WindowManager", "update vibrate_on_touch to " + j);
            }
            this.mVibrateOnTouchIntensity = j;
            this.mLockPatternVibePattern[0] = ((j + 1) * -1);
          }
          j = Settings.System.getIntForUser((ContentResolver)localObject2, "user_rotation", 0, -2);
          if (this.mUserRotation != j)
          {
            this.mUserRotation = j;
            i = 1;
          }
          if (Settings.System.getIntForUser((ContentResolver)localObject2, "accelerometer_rotation", 0, -2) == 0) {
            break label414;
          }
          j = 0;
          if (this.mUserRotationMode != j)
          {
            this.mUserRotationMode = j;
            i = 1;
            updateOrientationListenerLp();
          }
          if (this.mSystemReady)
          {
            j = Settings.System.getIntForUser((ContentResolver)localObject2, "pointer_location", 0, -2);
            if (this.mPointerLocationMode != j)
            {
              this.mPointerLocationMode = j;
              Handler localHandler = this.mHandler;
              if (j == 0) {
                break label419;
              }
              j = 1;
              localHandler.sendEmptyMessage(j);
            }
          }
          this.mLockScreenTimeout = Settings.System.getIntForUser((ContentResolver)localObject2, "screen_off_timeout", 0, -2);
          localObject2 = Settings.Secure.getStringForUser((ContentResolver)localObject2, "default_input_method", -2);
          if ((localObject2 == null) || (((String)localObject2).length() <= 0)) {
            break label424;
          }
          bool = true;
          if (this.mHasSoftInput != bool)
          {
            this.mHasSoftInput = bool;
            i = 1;
          }
          if (this.mImmersiveModeConfirmation != null) {
            this.mImmersiveModeConfirmation.loadSetting(this.mCurrentUserId);
          }
        }
      }
      synchronized (this.mWindowManagerFuncs.getWindowManagerLock())
      {
        PolicyControl.reloadFromSetting(this.mContext);
        if (i != 0) {
          updateRotation(true);
        }
        return;
        bool = false;
        continue;
        label409:
        bool = false;
        continue;
        label414:
        j = 1;
        continue;
        label419:
        j = 2;
        continue;
        label424:
        bool = false;
        continue;
        localObject3 = finally;
        throw ((Throwable)localObject3);
      }
    }
  }
  
  void updateUiMode()
  {
    if (this.mUiModeManager == null) {
      this.mUiModeManager = IUiModeManager.Stub.asInterface(ServiceManager.getService("uimode"));
    }
    try
    {
      this.mUiMode = this.mUiModeManager.getCurrentModeType();
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public void userActivity()
  {
    synchronized (this.mScreenLockTimeout)
    {
      if (this.mLockScreenTimerActive)
      {
        this.mHandler.removeCallbacks(this.mScreenLockTimeout);
        this.mHandler.postDelayed(this.mScreenLockTimeout, this.mLockScreenTimeout);
      }
      return;
    }
  }
  
  public boolean validateRotationAnimationLw(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    switch (paramInt1)
    {
    default: 
      return true;
    }
    if (paramBoolean) {
      return false;
    }
    int[] arrayOfInt = new int[2];
    selectRotationAnimationLw(arrayOfInt);
    return (paramInt1 == arrayOfInt[0]) && (paramInt2 == arrayOfInt[1]);
  }
  
  public int windowTypeToLayerLw(int paramInt)
  {
    if ((paramInt >= 1) && (paramInt <= 99)) {
      return 2;
    }
    switch (paramInt)
    {
    default: 
      Log.e("WindowManager", "Unknown window type: " + paramInt);
      return 2;
    case 2030: 
      return 2;
    case 2013: 
      return 2;
    case 2034: 
      return 2;
    case 2035: 
      return 2;
    case 2002: 
      return 3;
    case 2001: 
    case 2033: 
      return 4;
    case 2031: 
      return 5;
    case 2022: 
      return 6;
    case 2008: 
      return 7;
    case 2005: 
      return 8;
    case 2007: 
      return 9;
    case 2023: 
      return 10;
    case 2003: 
      return 11;
    case 2011: 
      return 12;
    case 2012: 
      return 13;
    case 2029: 
      return 14;
    case 2017: 
      return 15;
    case 2000: 
      return 16;
    case 2014: 
      return 17;
    case 2009: 
      return 18;
    case 2020: 
      return 19;
    case 2006: 
      return 20;
    case 2019: 
      return 21;
    case 2024: 
      return 22;
    case 2036: 
      return 23;
    case 2010: 
      return 24;
    case 2027: 
      return 25;
    case 2026: 
      return 26;
    case 2016: 
      return 27;
    case 2032: 
      return 28;
    case 2015: 
      return 29;
    case 2021: 
      return 30;
    case 2018: 
      return 31;
    }
    return 100;
  }
  
  private static class HdmiControl
  {
    private final HdmiPlaybackClient mClient;
    
    private HdmiControl(HdmiPlaybackClient paramHdmiPlaybackClient)
    {
      this.mClient = paramHdmiPlaybackClient;
    }
    
    public void turnOnTv()
    {
      if (this.mClient == null) {
        return;
      }
      this.mClient.oneTouchPlay(new HdmiPlaybackClient.OneTouchPlayCallback()
      {
        public void onComplete(int paramAnonymousInt)
        {
          if (paramAnonymousInt != 0) {
            Log.w("WindowManager", "One touch play failed: " + paramAnonymousInt);
          }
        }
      });
    }
  }
  
  final class HideNavInputEventReceiver
    extends InputEventReceiver
  {
    public HideNavInputEventReceiver(InputChannel paramInputChannel, Looper paramLooper)
    {
      super(paramLooper);
    }
    
    /* Error */
    public void onInputEvent(android.view.InputEvent paramInputEvent)
    {
      // Byte code:
      //   0: aload_1
      //   1: instanceof 21
      //   4: ifeq +171 -> 175
      //   7: aload_1
      //   8: invokevirtual 27	android/view/InputEvent:getSource	()I
      //   11: iconst_2
      //   12: iand
      //   13: ifeq +162 -> 175
      //   16: aload_1
      //   17: checkcast 21	android/view/MotionEvent
      //   20: invokevirtual 30	android/view/MotionEvent:getAction	()I
      //   23: ifne +152 -> 175
      //   26: iconst_0
      //   27: istore_2
      //   28: aload_0
      //   29: getfield 13	com/android/server/policy/PhoneWindowManager$HideNavInputEventReceiver:this$0	Lcom/android/server/policy/PhoneWindowManager;
      //   32: getfield 34	com/android/server/policy/PhoneWindowManager:mWindowManagerFuncs	Landroid/view/WindowManagerPolicy$WindowManagerFuncs;
      //   35: invokeinterface 40 1 0
      //   40: astore 4
      //   42: aload 4
      //   44: monitorenter
      //   45: aload_0
      //   46: getfield 13	com/android/server/policy/PhoneWindowManager$HideNavInputEventReceiver:this$0	Lcom/android/server/policy/PhoneWindowManager;
      //   49: getfield 44	com/android/server/policy/PhoneWindowManager:mInputConsumer	Landroid/view/WindowManagerPolicy$InputConsumer;
      //   52: astore 5
      //   54: aload 5
      //   56: ifnonnull +13 -> 69
      //   59: aload 4
      //   61: monitorexit
      //   62: aload_0
      //   63: aload_1
      //   64: iconst_0
      //   65: invokevirtual 48	com/android/server/policy/PhoneWindowManager$HideNavInputEventReceiver:finishInputEvent	(Landroid/view/InputEvent;Z)V
      //   68: return
      //   69: aload_0
      //   70: getfield 13	com/android/server/policy/PhoneWindowManager$HideNavInputEventReceiver:this$0	Lcom/android/server/policy/PhoneWindowManager;
      //   73: getfield 52	com/android/server/policy/PhoneWindowManager:mResettingSystemUiFlags	I
      //   76: iconst_2
      //   77: ior
      //   78: iconst_1
      //   79: ior
      //   80: iconst_4
      //   81: ior
      //   82: istore_3
      //   83: aload_0
      //   84: getfield 13	com/android/server/policy/PhoneWindowManager$HideNavInputEventReceiver:this$0	Lcom/android/server/policy/PhoneWindowManager;
      //   87: getfield 52	com/android/server/policy/PhoneWindowManager:mResettingSystemUiFlags	I
      //   90: iload_3
      //   91: if_icmpeq +13 -> 104
      //   94: aload_0
      //   95: getfield 13	com/android/server/policy/PhoneWindowManager$HideNavInputEventReceiver:this$0	Lcom/android/server/policy/PhoneWindowManager;
      //   98: iload_3
      //   99: putfield 52	com/android/server/policy/PhoneWindowManager:mResettingSystemUiFlags	I
      //   102: iconst_1
      //   103: istore_2
      //   104: aload_0
      //   105: getfield 13	com/android/server/policy/PhoneWindowManager$HideNavInputEventReceiver:this$0	Lcom/android/server/policy/PhoneWindowManager;
      //   108: getfield 55	com/android/server/policy/PhoneWindowManager:mForceClearedSystemUiFlags	I
      //   111: iconst_2
      //   112: ior
      //   113: istore_3
      //   114: aload_0
      //   115: getfield 13	com/android/server/policy/PhoneWindowManager$HideNavInputEventReceiver:this$0	Lcom/android/server/policy/PhoneWindowManager;
      //   118: getfield 55	com/android/server/policy/PhoneWindowManager:mForceClearedSystemUiFlags	I
      //   121: iload_3
      //   122: if_icmpeq +34 -> 156
      //   125: aload_0
      //   126: getfield 13	com/android/server/policy/PhoneWindowManager$HideNavInputEventReceiver:this$0	Lcom/android/server/policy/PhoneWindowManager;
      //   129: iload_3
      //   130: putfield 55	com/android/server/policy/PhoneWindowManager:mForceClearedSystemUiFlags	I
      //   133: iconst_1
      //   134: istore_2
      //   135: aload_0
      //   136: getfield 13	com/android/server/policy/PhoneWindowManager$HideNavInputEventReceiver:this$0	Lcom/android/server/policy/PhoneWindowManager;
      //   139: getfield 59	com/android/server/policy/PhoneWindowManager:mHandler	Landroid/os/Handler;
      //   142: aload_0
      //   143: getfield 13	com/android/server/policy/PhoneWindowManager$HideNavInputEventReceiver:this$0	Lcom/android/server/policy/PhoneWindowManager;
      //   146: invokestatic 63	com/android/server/policy/PhoneWindowManager:-get1	(Lcom/android/server/policy/PhoneWindowManager;)Ljava/lang/Runnable;
      //   149: ldc2_w 64
      //   152: invokevirtual 71	android/os/Handler:postDelayed	(Ljava/lang/Runnable;J)Z
      //   155: pop
      //   156: aload 4
      //   158: monitorexit
      //   159: iload_2
      //   160: ifeq +15 -> 175
      //   163: aload_0
      //   164: getfield 13	com/android/server/policy/PhoneWindowManager$HideNavInputEventReceiver:this$0	Lcom/android/server/policy/PhoneWindowManager;
      //   167: getfield 34	com/android/server/policy/PhoneWindowManager:mWindowManagerFuncs	Landroid/view/WindowManagerPolicy$WindowManagerFuncs;
      //   170: invokeinterface 75 1 0
      //   175: aload_0
      //   176: aload_1
      //   177: iconst_0
      //   178: invokevirtual 48	com/android/server/policy/PhoneWindowManager$HideNavInputEventReceiver:finishInputEvent	(Landroid/view/InputEvent;Z)V
      //   181: return
      //   182: astore 5
      //   184: aload 4
      //   186: monitorexit
      //   187: aload 5
      //   189: athrow
      //   190: astore 4
      //   192: aload_0
      //   193: aload_1
      //   194: iconst_0
      //   195: invokevirtual 48	com/android/server/policy/PhoneWindowManager$HideNavInputEventReceiver:finishInputEvent	(Landroid/view/InputEvent;Z)V
      //   198: aload 4
      //   200: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	201	0	this	HideNavInputEventReceiver
      //   0	201	1	paramInputEvent	android.view.InputEvent
      //   27	133	2	i	int
      //   82	48	3	j	int
      //   190	9	4	localObject2	Object
      //   52	3	5	localInputConsumer	WindowManagerPolicy.InputConsumer
      //   182	6	5	localObject3	Object
      // Exception table:
      //   from	to	target	type
      //   45	54	182	finally
      //   69	83	182	finally
      //   83	102	182	finally
      //   104	114	182	finally
      //   114	133	182	finally
      //   135	156	182	finally
      //   0	26	190	finally
      //   28	45	190	finally
      //   59	62	190	finally
      //   156	159	190	finally
      //   163	175	190	finally
      //   184	190	190	finally
    }
  }
  
  class MyOrientationListener
    extends WindowOrientationListener
  {
    private final Runnable mUpdateRotationRunnable = new Runnable()
    {
      public void run()
      {
        PhoneWindowManager.this.mPowerManagerInternal.powerHint(2, 0);
        PhoneWindowManager.this.updateRotation(false);
      }
    };
    
    MyOrientationListener(Context paramContext, Handler paramHandler)
    {
      super(paramHandler);
    }
    
    public void onProposedRotationChanged(int paramInt)
    {
      if ((PhoneWindowManager.localLOGV) || (PhoneWindowManager.DEBUG_ONEPLUS)) {
        Slog.v("WindowManager", "onProposedRotationChanged, rotation=" + paramInt);
      }
      PhoneWindowManager.this.mHandler.post(this.mUpdateRotationRunnable);
    }
  }
  
  class MyWakeGestureListener
    extends WakeGestureListener
  {
    MyWakeGestureListener(Context paramContext, Handler paramHandler)
    {
      super(paramHandler);
    }
    
    public void onWakeUp()
    {
      synchronized (PhoneWindowManager.-get2(PhoneWindowManager.this))
      {
        if (PhoneWindowManager.-wrap1(PhoneWindowManager.this))
        {
          PhoneWindowManager.this.performHapticFeedbackLw(null, 1, false);
          PhoneWindowManager.-wrap2(PhoneWindowManager.this, SystemClock.uptimeMillis(), PhoneWindowManager.-get0(PhoneWindowManager.this), "android.policy:GESTURE");
        }
        return;
      }
    }
  }
  
  private class PolicyHandler
    extends Handler
  {
    private PolicyHandler() {}
    
    public void handleMessage(Message paramMessage)
    {
      boolean bool2 = true;
      boolean bool3 = true;
      boolean bool1 = true;
      switch (paramMessage.what)
      {
      default: 
      case 1: 
      case 2: 
      case 3: 
      case 4: 
      case 9: 
      case 10: 
      case 5: 
      case 6: 
      case 7: 
      case 11: 
      case 12: 
      case 13: 
      case 14: 
      case 15: 
      case 16: 
      case 17: 
      case 18: 
      case 19: 
      case 101: 
        do
        {
          for (;;)
          {
            return;
            PhoneWindowManager.-wrap7(PhoneWindowManager.this);
            return;
            PhoneWindowManager.-wrap5(PhoneWindowManager.this);
            return;
            PhoneWindowManager.this.dispatchMediaKeyWithWakeLock((KeyEvent)paramMessage.obj);
            return;
            PhoneWindowManager.this.dispatchMediaKeyRepeatWithWakeLock((KeyEvent)paramMessage.obj);
            return;
            PhoneWindowManager localPhoneWindowManager = PhoneWindowManager.this;
            if (paramMessage.arg1 != 0) {}
            for (bool1 = true;; bool1 = false)
            {
              PhoneWindowManager.-wrap18(localPhoneWindowManager, false, bool1);
              return;
            }
            PhoneWindowManager.this.showGlobalActionsInternal();
            return;
            if (PhoneWindowManager.DEBUG_WAKEUP) {
              Slog.w("WindowManager", "Setting mKeyguardDrawComplete");
            }
            PhoneWindowManager.-wrap8(PhoneWindowManager.this);
            return;
            Slog.w("WindowManager", "Keyguard drawn timeout. Setting mKeyguardDrawComplete");
            PhoneWindowManager.-wrap8(PhoneWindowManager.this);
            return;
            if (PhoneWindowManager.DEBUG_WAKEUP) {
              Slog.w("WindowManager", "Setting mWindowManagerDrawComplete");
            }
            PhoneWindowManager.-wrap11(PhoneWindowManager.this);
            return;
            PhoneWindowManager.-wrap12(PhoneWindowManager.this);
            return;
            localPhoneWindowManager = PhoneWindowManager.this;
            if (paramMessage.arg1 != 0) {}
            for (;;)
            {
              localPhoneWindowManager.launchVoiceAssistWithWakeLock(bool1);
              return;
              bool1 = false;
            }
            localPhoneWindowManager = PhoneWindowManager.this;
            long l = ((Long)paramMessage.obj).longValue();
            if (paramMessage.arg1 != 0) {}
            for (bool1 = bool2;; bool1 = false)
            {
              PhoneWindowManager.-wrap16(localPhoneWindowManager, l, bool1, paramMessage.arg2);
              PhoneWindowManager.-wrap9(PhoneWindowManager.this);
              return;
            }
            PhoneWindowManager.-wrap15(PhoneWindowManager.this);
            return;
            localPhoneWindowManager = PhoneWindowManager.this;
            if (paramMessage.arg1 != 0) {}
            for (bool1 = bool3;; bool1 = false)
            {
              PhoneWindowManager.-wrap20(localPhoneWindowManager, bool1);
              return;
            }
            if (paramMessage.arg1 == 0) {}
            for (paramMessage = PhoneWindowManager.this.mStatusBar; paramMessage != null; paramMessage = PhoneWindowManager.this.mNavigationBar)
            {
              PhoneWindowManager.-wrap17(PhoneWindowManager.this, paramMessage);
              return;
            }
          }
          PhoneWindowManager.-wrap19(PhoneWindowManager.this);
          return;
          PhoneWindowManager.-wrap4(PhoneWindowManager.this);
          return;
          PhoneWindowManager.-wrap6(PhoneWindowManager.this, (WindowManagerPolicy.InputConsumer)paramMessage.obj);
          return;
          Log.d("WindowManager", "MSG_KEYGUARD_FP_STATE_CHANGE");
        } while (PhoneWindowManager.this.mKeyguardDelegate.isFingerprintAuthenticating());
        PhoneWindowManager.this.mHandler.removeMessages(102);
        PhoneWindowManager.-wrap10(PhoneWindowManager.this);
        return;
      }
      Log.d("WindowManager", "MSG_KEYGUARD_FP_AUTHENTICATED_TIMEOUT");
      PhoneWindowManager.-wrap10(PhoneWindowManager.this);
    }
  }
  
  class ScreenLockTimeout
    implements Runnable
  {
    Bundle options;
    
    ScreenLockTimeout() {}
    
    public void run()
    {
      try
      {
        if (PhoneWindowManager.localLOGV) {
          Log.v("WindowManager", "mScreenLockTimeout activating keyguard");
        }
        if (PhoneWindowManager.this.mKeyguardDelegate != null) {
          PhoneWindowManager.this.mKeyguardDelegate.doKeyguardTimeout(this.options);
        }
        PhoneWindowManager.this.mLockScreenTimerActive = false;
        this.options = null;
        return;
      }
      finally {}
    }
    
    public void setLockOptions(Bundle paramBundle)
    {
      this.options = paramBundle;
    }
  }
  
  protected class ScreenshotRunnable
    implements Runnable
  {
    private int mScreenshotType = 1;
    
    protected ScreenshotRunnable() {}
    
    public void run()
    {
      PhoneWindowManager.this.takeScreenshot(this.mScreenshotType);
    }
    
    public void setScreenshotType(int paramInt)
    {
      this.mScreenshotType = paramInt;
    }
  }
  
  class SettingsObserver
    extends ContentObserver
  {
    SettingsObserver(Handler paramHandler)
    {
      super();
    }
    
    void observe()
    {
      ContentResolver localContentResolver = PhoneWindowManager.this.mContext.getContentResolver();
      localContentResolver.registerContentObserver(Settings.System.getUriFor("end_button_behavior"), false, this, -1);
      localContentResolver.registerContentObserver(Settings.Secure.getUriFor("incall_power_button_behavior"), false, this, -1);
      localContentResolver.registerContentObserver(Settings.Secure.getUriFor("wake_gesture_enabled"), false, this, -1);
      localContentResolver.registerContentObserver(Settings.System.getUriFor("accelerometer_rotation"), false, this, -1);
      localContentResolver.registerContentObserver(Settings.System.getUriFor("user_rotation"), false, this, -1);
      localContentResolver.registerContentObserver(Settings.System.getUriFor("screen_off_timeout"), false, this, -1);
      localContentResolver.registerContentObserver(Settings.System.getUriFor("pointer_location"), false, this, -1);
      localContentResolver.registerContentObserver(Settings.Secure.getUriFor("default_input_method"), false, this, -1);
      localContentResolver.registerContentObserver(Settings.Secure.getUriFor("immersive_mode_confirmations"), false, this, -1);
      localContentResolver.registerContentObserver(Settings.Global.getUriFor("policy_control"), false, this, -1);
      localContentResolver.registerContentObserver(Settings.System.getUriFor("buttons_show_on_screen_navkeys"), false, this, -1);
      localContentResolver.registerContentObserver(Settings.System.getUriFor("vibrate_on_touch_intensity"), false, this, -1);
      PhoneWindowManager.this.updateSettings();
    }
    
    public void onChange(boolean paramBoolean)
    {
      PhoneWindowManager.this.updateSettings();
      PhoneWindowManager.this.updateRotation(false);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/policy/PhoneWindowManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */