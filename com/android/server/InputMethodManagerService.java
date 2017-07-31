package com.android.server;

import android.annotation.IntDef;
import android.app.ActivityManagerNative;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.AppOpsManager;
import android.app.KeyguardManager;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.ThemeController;
import android.content.res.TypedArray;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.hardware.input.InputManagerInternal;
import android.net.Uri;
import android.os.Binder;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.IInterface;
import android.os.LocaleList;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.text.style.SuggestionSpan;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.EventLog;
import android.util.LruCache;
import android.util.Pair;
import android.util.Slog;
import android.view.ContextThemeWrapper;
import android.view.IWindowManager;
import android.view.InputChannel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManagerInternal;
import android.view.WindowManagerInternal.OnHardKeyboardStatusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputBinding;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManagerInternal;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.android.internal.R.styleable;
import com.android.internal.content.PackageMonitor;
import com.android.internal.inputmethod.IInputContentUriToken;
import com.android.internal.inputmethod.InputMethodSubtypeSwitchingController;
import com.android.internal.inputmethod.InputMethodSubtypeSwitchingController.ImeSubtypeListItem;
import com.android.internal.inputmethod.InputMethodUtils;
import com.android.internal.inputmethod.InputMethodUtils.InputMethodSettings;
import com.android.internal.os.HandlerCaller;
import com.android.internal.os.HandlerCaller.Callback;
import com.android.internal.os.SomeArgs;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.view.IInputContext;
import com.android.internal.view.IInputMethod;
import com.android.internal.view.IInputMethod.Stub;
import com.android.internal.view.IInputMethodClient;
import com.android.internal.view.IInputMethodManager.Stub;
import com.android.internal.view.IInputMethodSession;
import com.android.internal.view.IInputSessionCallback;
import com.android.internal.view.IInputSessionCallback.Stub;
import com.android.internal.view.InputBindResult;
import com.android.server.am.OnePlusAppBootManager;
import com.android.server.am.OnePlusProcessManager;
import com.android.server.statusbar.StatusBarManagerService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import org.xmlpull.v1.XmlSerializer;

public class InputMethodManagerService
  extends IInputMethodManager.Stub
  implements ServiceConnection, Handler.Callback
{
  static final boolean DEBUG = false;
  static final boolean DEBUG_RESTORE = false;
  static final int MSG_ATTACH_TOKEN = 1040;
  static final int MSG_BIND_CLIENT = 3010;
  static final int MSG_BIND_INPUT = 1010;
  static final int MSG_CREATE_SESSION = 1050;
  static final int MSG_HARD_KEYBOARD_SWITCH_CHANGED = 4000;
  static final int MSG_HIDE_CURRENT_INPUT_METHOD = 1035;
  static final int MSG_HIDE_SOFT_INPUT = 1030;
  static final int MSG_RESTART_INPUT = 2010;
  static final int MSG_SET_ACTIVE = 3020;
  static final int MSG_SET_INTERACTIVE = 3030;
  static final int MSG_SET_USER_ACTION_NOTIFICATION_SEQUENCE_NUMBER = 3040;
  static final int MSG_SHOW_IM_CONFIG = 3;
  static final int MSG_SHOW_IM_SUBTYPE_ENABLER = 2;
  static final int MSG_SHOW_IM_SUBTYPE_PICKER = 1;
  static final int MSG_SHOW_SOFT_INPUT = 1020;
  static final int MSG_START_INPUT = 2000;
  static final int MSG_SWITCH_IME = 3050;
  static final int MSG_SYSTEM_UNLOCK_USER = 5000;
  static final int MSG_UNBIND_CLIENT = 3000;
  static final int MSG_UNBIND_INPUT = 1000;
  private static final int NOT_A_SUBTYPE_ID = -1;
  static final int SECURE_SUGGESTION_SPANS_MAX_SIZE = 20;
  static final String TAG = "InputMethodManagerService";
  private static final String TAG_TRY_SUPPRESSING_IME_SWITCHER = "TrySuppressingImeSwitcher";
  static final long TIME_TO_RECONNECT = 3000L;
  private boolean mAccessibilityRequestingNoSoftKeyboard;
  private final AppOpsManager mAppOpsManager;
  int mBackDisposition;
  boolean mBoundToMethod;
  final HandlerCaller mCaller;
  final HashMap<IBinder, ClientState> mClients;
  final Context mContext;
  EditorInfo mCurAttribute;
  ClientState mCurClient;
  private boolean mCurClientInKeyguard;
  IBinder mCurFocusedWindow;
  ClientState mCurFocusedWindowClient;
  String mCurId;
  IInputContext mCurInputContext;
  int mCurInputContextMissingMethods;
  Intent mCurIntent;
  IInputMethod mCurMethod;
  String mCurMethodId;
  int mCurSeq;
  IBinder mCurToken;
  int mCurUserActionNotificationSequenceNumber;
  private InputMethodSubtype mCurrentSubtype;
  private AlertDialog.Builder mDialogBuilder;
  SessionState mEnabledSession;
  private InputMethodFileManager mFileManager;
  final Handler mHandler;
  private final int mHardKeyboardBehavior;
  private final HardKeyboardListener mHardKeyboardListener;
  final boolean mHasFeature;
  boolean mHaveConnection;
  private final IPackageManager mIPackageManager;
  final IWindowManager mIWindowManager;
  private final boolean mImeSelectedOnBoot;
  private PendingIntent mImeSwitchPendingIntent;
  private Notification.Builder mImeSwitcherNotification;
  int mImeWindowVis;
  private InputMethodInfo[] mIms;
  boolean mInputShown;
  boolean mIsInteractive;
  private KeyguardManager mKeyguardManager;
  long mLastBindTime;
  private LocaleList mLastSystemLocales;
  final ArrayList<InputMethodInfo> mMethodList;
  final HashMap<String, InputMethodInfo> mMethodMap;
  private final MyPackageMonitor mMyPackageMonitor;
  final InputBindResult mNoBinding;
  private NotificationManager mNotificationManager;
  private boolean mNotificationShown;
  final Resources mRes;
  private final LruCache<SuggestionSpan, InputMethodInfo> mSecureSuggestionSpans;
  final InputMethodUtils.InputMethodSettings mSettings;
  final SettingsObserver mSettingsObserver;
  private final HashMap<InputMethodInfo, ArrayList<InputMethodSubtype>> mShortcutInputMethodsAndSubtypes;
  boolean mShowExplicitlyRequested;
  boolean mShowForced;
  private boolean mShowImeWithHardKeyboard;
  private boolean mShowOngoingImeSwitcherForPhones;
  boolean mShowRequested;
  private final String mSlotIme;
  private StatusBarManagerService mStatusBar;
  private int[] mSubtypeIds;
  private Toast mSubtypeSwitchedByShortCutToast;
  private final InputMethodSubtypeSwitchingController mSwitchingController;
  private AlertDialog mSwitchingDialog;
  private View mSwitchingDialogTitleView;
  boolean mSystemReady;
  private final UserManager mUserManager;
  boolean mVisibleBound;
  final ServiceConnection mVisibleConnection;
  final WindowManagerInternal mWindowManagerInternal;
  
  /* Error */
  public InputMethodManagerService(Context arg1)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 296	com/android/internal/view/IInputMethodManager$Stub:<init>	()V
    //   4: aload_0
    //   5: new 298	com/android/internal/view/InputBindResult
    //   8: dup
    //   9: aconst_null
    //   10: aconst_null
    //   11: aconst_null
    //   12: iconst_m1
    //   13: iconst_m1
    //   14: invokespecial 301	com/android/internal/view/InputBindResult:<init>	(Lcom/android/internal/view/IInputMethodSession;Landroid/view/InputChannel;Ljava/lang/String;II)V
    //   17: putfield 303	com/android/server/InputMethodManagerService:mNoBinding	Lcom/android/internal/view/InputBindResult;
    //   20: aload_0
    //   21: new 305	java/util/ArrayList
    //   24: dup
    //   25: invokespecial 306	java/util/ArrayList:<init>	()V
    //   28: putfield 308	com/android/server/InputMethodManagerService:mMethodList	Ljava/util/ArrayList;
    //   31: aload_0
    //   32: new 310	java/util/HashMap
    //   35: dup
    //   36: invokespecial 311	java/util/HashMap:<init>	()V
    //   39: putfield 313	com/android/server/InputMethodManagerService:mMethodMap	Ljava/util/HashMap;
    //   42: aload_0
    //   43: new 315	android/util/LruCache
    //   46: dup
    //   47: bipush 20
    //   49: invokespecial 318	android/util/LruCache:<init>	(I)V
    //   52: putfield 320	com/android/server/InputMethodManagerService:mSecureSuggestionSpans	Landroid/util/LruCache;
    //   55: aload_0
    //   56: new 10	com/android/server/InputMethodManagerService$1
    //   59: dup
    //   60: aload_0
    //   61: invokespecial 322	com/android/server/InputMethodManagerService$1:<init>	(Lcom/android/server/InputMethodManagerService;)V
    //   64: putfield 324	com/android/server/InputMethodManagerService:mVisibleConnection	Landroid/content/ServiceConnection;
    //   67: aload_0
    //   68: iconst_0
    //   69: putfield 326	com/android/server/InputMethodManagerService:mVisibleBound	Z
    //   72: aload_0
    //   73: new 310	java/util/HashMap
    //   76: dup
    //   77: invokespecial 311	java/util/HashMap:<init>	()V
    //   80: putfield 328	com/android/server/InputMethodManagerService:mClients	Ljava/util/HashMap;
    //   83: aload_0
    //   84: new 310	java/util/HashMap
    //   87: dup
    //   88: invokespecial 311	java/util/HashMap:<init>	()V
    //   91: putfield 330	com/android/server/InputMethodManagerService:mShortcutInputMethodsAndSubtypes	Ljava/util/HashMap;
    //   94: aload_0
    //   95: iconst_1
    //   96: putfield 332	com/android/server/InputMethodManagerService:mIsInteractive	Z
    //   99: aload_0
    //   100: iconst_0
    //   101: putfield 334	com/android/server/InputMethodManagerService:mCurUserActionNotificationSequenceNumber	I
    //   104: aload_0
    //   105: iconst_0
    //   106: putfield 336	com/android/server/InputMethodManagerService:mBackDisposition	I
    //   109: aload_0
    //   110: new 51	com/android/server/InputMethodManagerService$MyPackageMonitor
    //   113: dup
    //   114: aload_0
    //   115: invokespecial 337	com/android/server/InputMethodManagerService$MyPackageMonitor:<init>	(Lcom/android/server/InputMethodManagerService;)V
    //   118: putfield 339	com/android/server/InputMethodManagerService:mMyPackageMonitor	Lcom/android/server/InputMethodManagerService$MyPackageMonitor;
    //   121: aload_0
    //   122: invokestatic 345	android/app/AppGlobals:getPackageManager	()Landroid/content/pm/IPackageManager;
    //   125: putfield 246	com/android/server/InputMethodManagerService:mIPackageManager	Landroid/content/pm/IPackageManager;
    //   128: aload_0
    //   129: aload_1
    //   130: putfield 347	com/android/server/InputMethodManagerService:mContext	Landroid/content/Context;
    //   133: aload_0
    //   134: aload_1
    //   135: invokevirtual 353	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   138: putfield 355	com/android/server/InputMethodManagerService:mRes	Landroid/content/res/Resources;
    //   141: aload_0
    //   142: new 357	android/os/Handler
    //   145: dup
    //   146: aload_0
    //   147: invokespecial 360	android/os/Handler:<init>	(Landroid/os/Handler$Callback;)V
    //   150: putfield 362	com/android/server/InputMethodManagerService:mHandler	Landroid/os/Handler;
    //   153: aload_0
    //   154: new 57	com/android/server/InputMethodManagerService$SettingsObserver
    //   157: dup
    //   158: aload_0
    //   159: aload_0
    //   160: getfield 362	com/android/server/InputMethodManagerService:mHandler	Landroid/os/Handler;
    //   163: invokespecial 365	com/android/server/InputMethodManagerService$SettingsObserver:<init>	(Lcom/android/server/InputMethodManagerService;Landroid/os/Handler;)V
    //   166: putfield 367	com/android/server/InputMethodManagerService:mSettingsObserver	Lcom/android/server/InputMethodManagerService$SettingsObserver;
    //   169: aload_0
    //   170: ldc_w 369
    //   173: invokestatic 375	android/os/ServiceManager:getService	(Ljava/lang/String;)Landroid/os/IBinder;
    //   176: invokestatic 381	android/view/IWindowManager$Stub:asInterface	(Landroid/os/IBinder;)Landroid/view/IWindowManager;
    //   179: putfield 383	com/android/server/InputMethodManagerService:mIWindowManager	Landroid/view/IWindowManager;
    //   182: aload_0
    //   183: ldc_w 385
    //   186: invokestatic 390	com/android/server/LocalServices:getService	(Ljava/lang/Class;)Ljava/lang/Object;
    //   189: checkcast 385	android/view/WindowManagerInternal
    //   192: putfield 392	com/android/server/InputMethodManagerService:mWindowManagerInternal	Landroid/view/WindowManagerInternal;
    //   195: aload_0
    //   196: new 394	com/android/internal/os/HandlerCaller
    //   199: dup
    //   200: aload_1
    //   201: aconst_null
    //   202: new 12	com/android/server/InputMethodManagerService$2
    //   205: dup
    //   206: aload_0
    //   207: invokespecial 395	com/android/server/InputMethodManagerService$2:<init>	(Lcom/android/server/InputMethodManagerService;)V
    //   210: iconst_1
    //   211: invokespecial 398	com/android/internal/os/HandlerCaller:<init>	(Landroid/content/Context;Landroid/os/Looper;Lcom/android/internal/os/HandlerCaller$Callback;Z)V
    //   214: putfield 400	com/android/server/InputMethodManagerService:mCaller	Lcom/android/internal/os/HandlerCaller;
    //   217: aload_0
    //   218: aload_0
    //   219: getfield 347	com/android/server/InputMethodManagerService:mContext	Landroid/content/Context;
    //   222: ldc_w 402
    //   225: invokevirtual 405	android/content/Context:getSystemService	(Ljava/lang/Class;)Ljava/lang/Object;
    //   228: checkcast 402	android/app/AppOpsManager
    //   231: putfield 407	com/android/server/InputMethodManagerService:mAppOpsManager	Landroid/app/AppOpsManager;
    //   234: aload_0
    //   235: aload_0
    //   236: getfield 347	com/android/server/InputMethodManagerService:mContext	Landroid/content/Context;
    //   239: ldc_w 409
    //   242: invokevirtual 405	android/content/Context:getSystemService	(Ljava/lang/Class;)Ljava/lang/Object;
    //   245: checkcast 409	android/os/UserManager
    //   248: putfield 411	com/android/server/InputMethodManagerService:mUserManager	Landroid/os/UserManager;
    //   251: aload_0
    //   252: new 30	com/android/server/InputMethodManagerService$HardKeyboardListener
    //   255: dup
    //   256: aload_0
    //   257: aconst_null
    //   258: invokespecial 414	com/android/server/InputMethodManagerService$HardKeyboardListener:<init>	(Lcom/android/server/InputMethodManagerService;Lcom/android/server/InputMethodManagerService$HardKeyboardListener;)V
    //   261: putfield 416	com/android/server/InputMethodManagerService:mHardKeyboardListener	Lcom/android/server/InputMethodManagerService$HardKeyboardListener;
    //   264: aload_0
    //   265: aload_1
    //   266: invokevirtual 419	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
    //   269: ldc_w 421
    //   272: invokevirtual 427	android/content/pm/PackageManager:hasSystemFeature	(Ljava/lang/String;)Z
    //   275: putfield 429	com/android/server/InputMethodManagerService:mHasFeature	Z
    //   278: aload_0
    //   279: aload_0
    //   280: getfield 347	com/android/server/InputMethodManagerService:mContext	Landroid/content/Context;
    //   283: ldc_w 430
    //   286: invokevirtual 434	android/content/Context:getString	(I)Ljava/lang/String;
    //   289: putfield 436	com/android/server/InputMethodManagerService:mSlotIme	Ljava/lang/String;
    //   292: aload_0
    //   293: aload_0
    //   294: getfield 347	com/android/server/InputMethodManagerService:mContext	Landroid/content/Context;
    //   297: invokevirtual 353	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   300: ldc_w 437
    //   303: invokevirtual 443	android/content/res/Resources:getInteger	(I)I
    //   306: putfield 445	com/android/server/InputMethodManagerService:mHardKeyboardBehavior	I
    //   309: new 447	android/os/Bundle
    //   312: dup
    //   313: invokespecial 448	android/os/Bundle:<init>	()V
    //   316: astore 5
    //   318: aload 5
    //   320: ldc_w 450
    //   323: iconst_1
    //   324: invokevirtual 454	android/os/Bundle:putBoolean	(Ljava/lang/String;Z)V
    //   327: aload_0
    //   328: new 456	android/app/Notification$Builder
    //   331: dup
    //   332: aload_0
    //   333: getfield 347	com/android/server/InputMethodManagerService:mContext	Landroid/content/Context;
    //   336: invokespecial 458	android/app/Notification$Builder:<init>	(Landroid/content/Context;)V
    //   339: ldc_w 459
    //   342: invokevirtual 463	android/app/Notification$Builder:setSmallIcon	(I)Landroid/app/Notification$Builder;
    //   345: lconst_0
    //   346: invokevirtual 467	android/app/Notification$Builder:setWhen	(J)Landroid/app/Notification$Builder;
    //   349: iconst_1
    //   350: invokevirtual 471	android/app/Notification$Builder:setOngoing	(Z)Landroid/app/Notification$Builder;
    //   353: aload 5
    //   355: invokevirtual 475	android/app/Notification$Builder:addExtras	(Landroid/os/Bundle;)Landroid/app/Notification$Builder;
    //   358: ldc_w 477
    //   361: invokevirtual 481	android/app/Notification$Builder:setCategory	(Ljava/lang/String;)Landroid/app/Notification$Builder;
    //   364: ldc_w 482
    //   367: invokevirtual 485	android/app/Notification$Builder:setColor	(I)Landroid/app/Notification$Builder;
    //   370: putfield 487	com/android/server/InputMethodManagerService:mImeSwitcherNotification	Landroid/app/Notification$Builder;
    //   373: new 489	android/content/Intent
    //   376: dup
    //   377: ldc_w 491
    //   380: invokespecial 493	android/content/Intent:<init>	(Ljava/lang/String;)V
    //   383: astore 5
    //   385: aload 5
    //   387: ldc_w 494
    //   390: invokevirtual 498	android/content/Intent:addFlags	(I)Landroid/content/Intent;
    //   393: pop
    //   394: aload_0
    //   395: aload_0
    //   396: getfield 347	com/android/server/InputMethodManagerService:mContext	Landroid/content/Context;
    //   399: iconst_0
    //   400: aload 5
    //   402: iconst_0
    //   403: invokestatic 504	android/app/PendingIntent:getBroadcast	(Landroid/content/Context;ILandroid/content/Intent;I)Landroid/app/PendingIntent;
    //   406: putfield 506	com/android/server/InputMethodManagerService:mImeSwitchPendingIntent	Landroid/app/PendingIntent;
    //   409: aload_0
    //   410: iconst_0
    //   411: putfield 508	com/android/server/InputMethodManagerService:mShowOngoingImeSwitcherForPhones	Z
    //   414: new 510	android/content/IntentFilter
    //   417: dup
    //   418: invokespecial 511	android/content/IntentFilter:<init>	()V
    //   421: astore 5
    //   423: aload 5
    //   425: ldc_w 513
    //   428: invokevirtual 516	android/content/IntentFilter:addAction	(Ljava/lang/String;)V
    //   431: aload 5
    //   433: ldc_w 518
    //   436: invokevirtual 516	android/content/IntentFilter:addAction	(Ljava/lang/String;)V
    //   439: aload 5
    //   441: ldc_w 520
    //   444: invokevirtual 516	android/content/IntentFilter:addAction	(Ljava/lang/String;)V
    //   447: aload 5
    //   449: ldc_w 522
    //   452: invokevirtual 516	android/content/IntentFilter:addAction	(Ljava/lang/String;)V
    //   455: aload_0
    //   456: getfield 347	com/android/server/InputMethodManagerService:mContext	Landroid/content/Context;
    //   459: new 36	com/android/server/InputMethodManagerService$ImmsBroadcastReceiver
    //   462: dup
    //   463: aload_0
    //   464: invokespecial 523	com/android/server/InputMethodManagerService$ImmsBroadcastReceiver:<init>	(Lcom/android/server/InputMethodManagerService;)V
    //   467: aload 5
    //   469: invokevirtual 527	android/content/Context:registerReceiver	(Landroid/content/BroadcastReceiver;Landroid/content/IntentFilter;)Landroid/content/Intent;
    //   472: pop
    //   473: aload_0
    //   474: iconst_0
    //   475: putfield 529	com/android/server/InputMethodManagerService:mNotificationShown	Z
    //   478: iconst_0
    //   479: istore_2
    //   480: invokestatic 535	android/app/ActivityManagerNative:getDefault	()Landroid/app/IActivityManager;
    //   483: invokeinterface 541 1 0
    //   488: getfield 546	android/content/pm/UserInfo:id	I
    //   491: istore_3
    //   492: iload_3
    //   493: istore_2
    //   494: aload_0
    //   495: getfield 339	com/android/server/InputMethodManagerService:mMyPackageMonitor	Lcom/android/server/InputMethodManagerService$MyPackageMonitor;
    //   498: aload_0
    //   499: getfield 347	com/android/server/InputMethodManagerService:mContext	Landroid/content/Context;
    //   502: aconst_null
    //   503: getstatic 552	android/os/UserHandle:ALL	Landroid/os/UserHandle;
    //   506: iconst_1
    //   507: invokevirtual 556	com/android/server/InputMethodManagerService$MyPackageMonitor:register	(Landroid/content/Context;Landroid/os/Looper;Landroid/os/UserHandle;Z)V
    //   510: aload_0
    //   511: getfield 355	com/android/server/InputMethodManagerService:mRes	Landroid/content/res/Resources;
    //   514: astore 5
    //   516: aload_1
    //   517: invokevirtual 560	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   520: astore 6
    //   522: aload_0
    //   523: getfield 313	com/android/server/InputMethodManagerService:mMethodMap	Ljava/util/HashMap;
    //   526: astore 7
    //   528: aload_0
    //   529: getfield 308	com/android/server/InputMethodManagerService:mMethodList	Ljava/util/ArrayList;
    //   532: astore 8
    //   534: aload_0
    //   535: getfield 562	com/android/server/InputMethodManagerService:mSystemReady	Z
    //   538: ifeq +233 -> 771
    //   541: iconst_0
    //   542: istore 4
    //   544: aload_0
    //   545: new 564	com/android/internal/inputmethod/InputMethodUtils$InputMethodSettings
    //   548: dup
    //   549: aload 5
    //   551: aload 6
    //   553: aload 7
    //   555: aload 8
    //   557: iload_2
    //   558: iload 4
    //   560: invokespecial 567	com/android/internal/inputmethod/InputMethodUtils$InputMethodSettings:<init>	(Landroid/content/res/Resources;Landroid/content/ContentResolver;Ljava/util/HashMap;Ljava/util/ArrayList;IZ)V
    //   563: putfield 569	com/android/server/InputMethodManagerService:mSettings	Lcom/android/internal/inputmethod/InputMethodUtils$InputMethodSettings;
    //   566: aload_0
    //   567: invokevirtual 572	com/android/server/InputMethodManagerService:updateCurrentProfileIds	()V
    //   570: aload_0
    //   571: new 39	com/android/server/InputMethodManagerService$InputMethodFileManager
    //   574: dup
    //   575: aload_0
    //   576: getfield 313	com/android/server/InputMethodManagerService:mMethodMap	Ljava/util/HashMap;
    //   579: iload_2
    //   580: invokespecial 575	com/android/server/InputMethodManagerService$InputMethodFileManager:<init>	(Ljava/util/HashMap;I)V
    //   583: putfield 242	com/android/server/InputMethodManagerService:mFileManager	Lcom/android/server/InputMethodManagerService$InputMethodFileManager;
    //   586: aload_0
    //   587: getfield 313	com/android/server/InputMethodManagerService:mMethodMap	Ljava/util/HashMap;
    //   590: astore 5
    //   592: aload 5
    //   594: monitorenter
    //   595: aload_0
    //   596: aload_0
    //   597: getfield 569	com/android/server/InputMethodManagerService:mSettings	Lcom/android/internal/inputmethod/InputMethodUtils$InputMethodSettings;
    //   600: aload_1
    //   601: invokestatic 581	com/android/internal/inputmethod/InputMethodSubtypeSwitchingController:createInstanceLocked	(Lcom/android/internal/inputmethod/InputMethodUtils$InputMethodSettings;Landroid/content/Context;)Lcom/android/internal/inputmethod/InputMethodSubtypeSwitchingController;
    //   604: putfield 583	com/android/server/InputMethodManagerService:mSwitchingController	Lcom/android/internal/inputmethod/InputMethodSubtypeSwitchingController;
    //   607: aload 5
    //   609: monitorexit
    //   610: aload_0
    //   611: getfield 569	com/android/server/InputMethodManagerService:mSettings	Lcom/android/internal/inputmethod/InputMethodUtils$InputMethodSettings;
    //   614: invokevirtual 587	com/android/internal/inputmethod/InputMethodUtils$InputMethodSettings:getSelectedInputMethod	()Ljava/lang/String;
    //   617: invokestatic 593	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   620: ifeq +163 -> 783
    //   623: iconst_0
    //   624: istore 4
    //   626: aload_0
    //   627: iload 4
    //   629: putfield 595	com/android/server/InputMethodManagerService:mImeSelectedOnBoot	Z
    //   632: aload_0
    //   633: getfield 313	com/android/server/InputMethodManagerService:mMethodMap	Ljava/util/HashMap;
    //   636: astore 5
    //   638: aload 5
    //   640: monitorenter
    //   641: aload_0
    //   642: getfield 595	com/android/server/InputMethodManagerService:mImeSelectedOnBoot	Z
    //   645: ifeq +144 -> 789
    //   648: iconst_0
    //   649: istore 4
    //   651: aload_0
    //   652: iload 4
    //   654: invokevirtual 599	com/android/server/InputMethodManagerService:buildInputMethodListLocked	(Z)V
    //   657: aload 5
    //   659: monitorexit
    //   660: aload_0
    //   661: getfield 569	com/android/server/InputMethodManagerService:mSettings	Lcom/android/internal/inputmethod/InputMethodUtils$InputMethodSettings;
    //   664: invokevirtual 602	com/android/internal/inputmethod/InputMethodUtils$InputMethodSettings:enableAllIMEsIfThereIsNoEnabledIME	()V
    //   667: aload_0
    //   668: getfield 595	com/android/server/InputMethodManagerService:mImeSelectedOnBoot	Z
    //   671: ifne +29 -> 700
    //   674: ldc 111
    //   676: ldc_w 604
    //   679: invokestatic 610	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   682: pop
    //   683: aload_0
    //   684: getfield 313	com/android/server/InputMethodManagerService:mMethodMap	Ljava/util/HashMap;
    //   687: astore 5
    //   689: aload 5
    //   691: monitorenter
    //   692: aload_0
    //   693: aload_1
    //   694: invokespecial 613	com/android/server/InputMethodManagerService:resetDefaultImeLocked	(Landroid/content/Context;)V
    //   697: aload 5
    //   699: monitorexit
    //   700: aload_0
    //   701: getfield 313	com/android/server/InputMethodManagerService:mMethodMap	Ljava/util/HashMap;
    //   704: astore_1
    //   705: aload_1
    //   706: monitorenter
    //   707: aload_0
    //   708: getfield 367	com/android/server/InputMethodManagerService:mSettingsObserver	Lcom/android/server/InputMethodManagerService$SettingsObserver;
    //   711: iload_2
    //   712: invokevirtual 616	com/android/server/InputMethodManagerService$SettingsObserver:registerContentObserverLocked	(I)V
    //   715: aload_0
    //   716: iconst_1
    //   717: invokevirtual 619	com/android/server/InputMethodManagerService:updateFromSettingsLocked	(Z)V
    //   720: aload_1
    //   721: monitorexit
    //   722: new 510	android/content/IntentFilter
    //   725: dup
    //   726: invokespecial 511	android/content/IntentFilter:<init>	()V
    //   729: astore_1
    //   730: aload_1
    //   731: ldc_w 621
    //   734: invokevirtual 516	android/content/IntentFilter:addAction	(Ljava/lang/String;)V
    //   737: aload_0
    //   738: getfield 347	com/android/server/InputMethodManagerService:mContext	Landroid/content/Context;
    //   741: new 14	com/android/server/InputMethodManagerService$3
    //   744: dup
    //   745: aload_0
    //   746: invokespecial 622	com/android/server/InputMethodManagerService$3:<init>	(Lcom/android/server/InputMethodManagerService;)V
    //   749: aload_1
    //   750: invokevirtual 527	android/content/Context:registerReceiver	(Landroid/content/BroadcastReceiver;Landroid/content/IntentFilter;)Landroid/content/Intent;
    //   753: pop
    //   754: return
    //   755: astore 5
    //   757: ldc 111
    //   759: ldc_w 624
    //   762: aload 5
    //   764: invokestatic 627	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   767: pop
    //   768: goto -274 -> 494
    //   771: iconst_1
    //   772: istore 4
    //   774: goto -230 -> 544
    //   777: astore_1
    //   778: aload 5
    //   780: monitorexit
    //   781: aload_1
    //   782: athrow
    //   783: iconst_1
    //   784: istore 4
    //   786: goto -160 -> 626
    //   789: iconst_1
    //   790: istore 4
    //   792: goto -141 -> 651
    //   795: astore_1
    //   796: aload 5
    //   798: monitorexit
    //   799: aload_1
    //   800: athrow
    //   801: astore_1
    //   802: aload 5
    //   804: monitorexit
    //   805: aload_1
    //   806: athrow
    //   807: astore 5
    //   809: aload_1
    //   810: monitorexit
    //   811: aload 5
    //   813: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	814	0	this	InputMethodManagerService
    //   479	233	2	i	int
    //   491	2	3	j	int
    //   542	249	4	bool	boolean
    //   755	48	5	localRemoteException	RemoteException
    //   807	5	5	localObject2	Object
    //   520	32	6	localContentResolver	ContentResolver
    //   526	28	7	localHashMap	HashMap
    //   532	24	8	localArrayList	ArrayList
    // Exception table:
    //   from	to	target	type
    //   480	492	755	android/os/RemoteException
    //   595	607	777	finally
    //   641	648	795	finally
    //   651	657	795	finally
    //   692	697	801	finally
    //   707	720	807	finally
  }
  
  private boolean bindCurrentInputMethodService(Intent paramIntent, ServiceConnection paramServiceConnection, int paramInt)
  {
    if ((paramIntent == null) || (paramServiceConnection == null))
    {
      Slog.e("InputMethodManagerService", "--- bind failed: service = " + paramIntent + ", conn = " + paramServiceConnection);
      return false;
    }
    if (OnePlusAppBootManager.IN_USING) {
      OnePlusAppBootManager.getInstance(null).setCurrentIME(paramIntent);
    }
    return this.mContext.bindServiceAsUser(paramIntent, paramServiceConnection, paramInt, new UserHandle(this.mSettings.getCurrentUserId()));
  }
  
  private boolean calledFromValidUser()
  {
    int i = Binder.getCallingUid();
    int j = UserHandle.getUserId(i);
    if ((i == 1000) || (this.mSettings.isCurrentProfile(j))) {
      return true;
    }
    if (this.mContext.checkCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL") == 0) {
      return true;
    }
    Slog.w("InputMethodManagerService", "--- IPC called from background users. Ignore. callers=" + Debug.getCallers(10));
    return false;
  }
  
  private boolean calledWithValidToken(IBinder paramIBinder)
  {
    return (paramIBinder != null) && (this.mCurToken == paramIBinder);
  }
  
  private boolean chooseNewDefaultIMELocked()
  {
    InputMethodInfo localInputMethodInfo = InputMethodUtils.getMostApplicableDefaultIME(this.mSettings.getEnabledInputMethodListLocked());
    if (localInputMethodInfo != null)
    {
      resetSelectedInputMethodAndSubtypeLocked(localInputMethodInfo.getId());
      return true;
    }
    return false;
  }
  
  private Pair<InputMethodInfo, InputMethodSubtype> findLastResortApplicableShortcutInputMethodAndSubtypeLocked(String paramString)
  {
    Object localObject1 = this.mSettings.getEnabledInputMethodListLocked();
    Object localObject3 = null;
    Object localObject2 = null;
    int i = 0;
    Iterator localIterator = ((Iterable)localObject1).iterator();
    for (;;)
    {
      Object localObject4 = localObject3;
      localObject1 = localObject2;
      InputMethodInfo localInputMethodInfo;
      String str;
      Object localObject5;
      if (localIterator.hasNext())
      {
        localInputMethodInfo = (InputMethodInfo)localIterator.next();
        str = localInputMethodInfo.getId();
        if ((i != 0) && (!str.equals(this.mCurMethodId))) {
          continue;
        }
        localObject4 = null;
        localObject5 = this.mSettings.getEnabledInputMethodSubtypeListLocked(this.mContext, localInputMethodInfo, true);
        if (this.mCurrentSubtype != null) {
          localObject4 = InputMethodUtils.findLastResortApplicableSubtypeLocked(this.mRes, (List)localObject5, paramString, this.mCurrentSubtype.getLocale(), false);
        }
        localObject1 = localObject4;
        if (localObject4 == null) {
          localObject1 = InputMethodUtils.findLastResortApplicableSubtypeLocked(this.mRes, (List)localObject5, paramString, null, true);
        }
        localObject5 = InputMethodUtils.getOverridingImplicitlyEnabledSubtypes(localInputMethodInfo, paramString);
        if (!((ArrayList)localObject5).isEmpty()) {
          break label260;
        }
        localObject5 = InputMethodUtils.getSubtypes(localInputMethodInfo);
      }
      label260:
      for (;;)
      {
        localObject4 = localObject1;
        if (localObject1 == null)
        {
          localObject4 = localObject1;
          if (this.mCurrentSubtype != null) {
            localObject4 = InputMethodUtils.findLastResortApplicableSubtypeLocked(this.mRes, (List)localObject5, paramString, this.mCurrentSubtype.getLocale(), false);
          }
        }
        localObject1 = localObject4;
        if (localObject4 == null) {
          localObject1 = InputMethodUtils.findLastResortApplicableSubtypeLocked(this.mRes, (List)localObject5, paramString, null, true);
        }
        if (localObject1 == null) {
          break;
        }
        if (!str.equals(this.mCurMethodId)) {
          break label263;
        }
        localObject4 = localInputMethodInfo;
        if (localObject4 == null) {
          break label306;
        }
        return new Pair(localObject4, localObject1);
      }
      label263:
      if (i == 0)
      {
        localObject4 = localInputMethodInfo;
        localObject3 = localObject4;
        localObject2 = localObject1;
        if ((localInputMethodInfo.getServiceInfo().applicationInfo.flags & 0x1) != 0)
        {
          i = 1;
          localObject3 = localObject4;
          localObject2 = localObject1;
        }
      }
    }
    label306:
    return null;
  }
  
  private void finishSessionLocked(SessionState paramSessionState)
  {
    if ((paramSessionState == null) || (paramSessionState.session != null)) {}
    try
    {
      paramSessionState.session.finishSession();
      paramSessionState.session = null;
      if (paramSessionState.channel != null)
      {
        paramSessionState.channel.dispose();
        paramSessionState.channel = null;
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Slog.w("InputMethodManagerService", "Session failed to close due to remote exception", localRemoteException);
        updateSystemUiLocked(this.mCurToken, 0, this.mBackDisposition);
      }
    }
  }
  
  private int getAppShowFlags()
  {
    int i = 0;
    if (this.mShowForced) {
      i = 2;
    }
    while (this.mShowExplicitlyRequested) {
      return i;
    }
    return 1;
  }
  
  private InputMethodSubtype getCurrentInputMethodSubtypeLocked()
  {
    if (this.mCurMethodId == null) {
      return null;
    }
    boolean bool = this.mSettings.isSubtypeSelected();
    Object localObject = (InputMethodInfo)this.mMethodMap.get(this.mCurMethodId);
    if ((localObject == null) || (((InputMethodInfo)localObject).getSubtypeCount() == 0)) {
      return null;
    }
    int i;
    if ((!bool) || (this.mCurrentSubtype == null))
    {
      i = this.mSettings.getSelectedInputMethodSubtypeId(this.mCurMethodId);
      if (i != -1) {
        break label185;
      }
      localObject = this.mSettings.getEnabledInputMethodSubtypeListLocked(this.mContext, (InputMethodInfo)localObject, true);
      if (((List)localObject).size() != 1) {
        break label133;
      }
      this.mCurrentSubtype = ((InputMethodSubtype)((List)localObject).get(0));
    }
    for (;;)
    {
      return this.mCurrentSubtype;
      if (!InputMethodUtils.isValidSubtypeId((InputMethodInfo)localObject, this.mCurrentSubtype.hashCode())) {
        break;
      }
      continue;
      label133:
      if (((List)localObject).size() > 1)
      {
        this.mCurrentSubtype = InputMethodUtils.findLastResortApplicableSubtypeLocked(this.mRes, (List)localObject, "keyboard", null, true);
        if (this.mCurrentSubtype == null)
        {
          this.mCurrentSubtype = InputMethodUtils.findLastResortApplicableSubtypeLocked(this.mRes, (List)localObject, null, null, true);
          continue;
          label185:
          this.mCurrentSubtype = ((InputMethodSubtype)InputMethodUtils.getSubtypes((InputMethodInfo)localObject).get(i));
        }
      }
    }
  }
  
  private int getImeShowFlags()
  {
    int i = 0;
    if (this.mShowForced) {
      i = 3;
    }
    while (!this.mShowExplicitlyRequested) {
      return i;
    }
    return 1;
  }
  
  private void handleSetInteractive(boolean paramBoolean)
  {
    int j = 0;
    synchronized (this.mMethodMap)
    {
      this.mIsInteractive = paramBoolean;
      Object localObject1 = this.mCurToken;
      if (paramBoolean)
      {
        i = this.mImeWindowVis;
        updateSystemUiLocked((IBinder)localObject1, i, this.mBackDisposition);
        if ((this.mCurClient != null) && (this.mCurClient.client != null))
        {
          localObject1 = this.mCurClient.client;
          HandlerCaller localHandlerCaller = this.mCaller;
          i = j;
          if (this.mIsInteractive) {
            i = 1;
          }
          executeOrSendMessage((IInterface)localObject1, localHandlerCaller.obtainMessageIO(3020, i, this.mCurClient));
        }
        return;
      }
      int i = 0;
    }
  }
  
  private void handleSwitchInputMethod(boolean paramBoolean)
  {
    synchronized (this.mMethodMap)
    {
      Object localObject1 = this.mSwitchingController.getNextInputMethodLocked(false, (InputMethodInfo)this.mMethodMap.get(this.mCurMethodId), this.mCurrentSubtype, paramBoolean);
      if (localObject1 == null) {
        return;
      }
      setInputMethodLocked(((InputMethodSubtypeSwitchingController.ImeSubtypeListItem)localObject1).mImi.getId(), ((InputMethodSubtypeSwitchingController.ImeSubtypeListItem)localObject1).mSubtypeId);
      localObject1 = (InputMethodInfo)this.mMethodMap.get(this.mCurMethodId);
      if (localObject1 == null) {
        return;
      }
      localObject1 = InputMethodUtils.getImeAndSubtypeDisplayName(this.mContext, (InputMethodInfo)localObject1, this.mCurrentSubtype);
      if (!TextUtils.isEmpty((CharSequence)localObject1))
      {
        if (this.mSubtypeSwitchedByShortCutToast == null)
        {
          this.mSubtypeSwitchedByShortCutToast = Toast.makeText(this.mContext, (CharSequence)localObject1, 0);
          this.mSubtypeSwitchedByShortCutToast.show();
        }
      }
      else {
        return;
      }
      this.mSubtypeSwitchedByShortCutToast.setText((CharSequence)localObject1);
    }
  }
  
  private static String imeWindowStatusToString(int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 1;
    if ((paramInt & 0x1) != 0)
    {
      localStringBuilder.append("Active");
      i = 0;
    }
    if ((paramInt & 0x2) != 0)
    {
      if (i == 0) {
        localStringBuilder.append("|");
      }
      localStringBuilder.append("Visible");
    }
    return localStringBuilder.toString();
  }
  
  private boolean isKeyguardLocked()
  {
    if (this.mKeyguardManager != null) {
      return this.mKeyguardManager.isKeyguardLocked();
    }
    return false;
  }
  
  private boolean isScreenLocked()
  {
    if ((this.mKeyguardManager != null) && (this.mKeyguardManager.isKeyguardLocked())) {
      return this.mKeyguardManager.isKeyguardSecure();
    }
    return false;
  }
  
  private boolean isSwiftKeyIme(InputMethodInfo paramInputMethodInfo)
  {
    if (paramInputMethodInfo != null) {
      try
      {
        if (this.mContext.getPackageManager().getPackageInfo("com.touchtype.swiftkey", 0) != null)
        {
          boolean bool = "com.touchtype.swiftkey/com.touchtype.KeyboardService".equals(paramInputMethodInfo.getId());
          return bool;
        }
      }
      catch (PackageManager.NameNotFoundException paramInputMethodInfo) {}
    }
    return false;
  }
  
  private void notifyInputMethodSubtypeChanged(int paramInt, InputMethodInfo paramInputMethodInfo, InputMethodSubtype paramInputMethodSubtype)
  {
    InputManagerInternal localInputManagerInternal = (InputManagerInternal)LocalServices.getService(InputManagerInternal.class);
    if (localInputManagerInternal != null) {
      localInputManagerInternal.onInputMethodSubtypeChanged(paramInt, paramInputMethodInfo, paramInputMethodSubtype);
    }
  }
  
  private void resetAllInternalStateLocked(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (!this.mSystemReady) {
      return;
    }
    LocaleList localLocaleList = this.mRes.getConfiguration().getLocales();
    if ((paramBoolean1) && ((localLocaleList == null) || (localLocaleList.equals(this.mLastSystemLocales)))) {
      return;
    }
    if (!paramBoolean1)
    {
      hideCurrentInputLocked(0, null);
      resetCurrentMethodAndClient(6);
    }
    buildInputMethodListLocked(paramBoolean2);
    if (!paramBoolean1) {
      if (TextUtils.isEmpty(this.mSettings.getSelectedInputMethod())) {
        resetDefaultImeLocked(this.mContext);
      }
    }
    for (;;)
    {
      updateFromSettingsLocked(true);
      this.mLastSystemLocales = localLocaleList;
      if (paramBoolean1) {
        break;
      }
      try
      {
        startInputInnerLocked();
        return;
      }
      catch (RuntimeException localRuntimeException)
      {
        Slog.w("InputMethodManagerService", "Unexpected exception", localRuntimeException);
        return;
      }
      resetDefaultImeLocked(this.mContext);
    }
  }
  
  private void resetDefaultImeLocked(Context paramContext)
  {
    if ((this.mCurMethodId == null) || (InputMethodUtils.isSystemIme((InputMethodInfo)this.mMethodMap.get(this.mCurMethodId))))
    {
      Slog.i("InputMethodManagerService", "Current Keybaord set : " + this.mMethodMap.get(this.mCurMethodId));
      paramContext = InputMethodUtils.getDefaultEnabledImes(paramContext, this.mSystemReady, this.mSettings.getEnabledInputMethodListLocked());
      if (paramContext.isEmpty()) {
        Slog.i("InputMethodManagerService", "No default found");
      }
    }
    else
    {
      return;
    }
    if (isSwiftKeyIme((InputMethodInfo)this.mMethodMap.get(this.mCurMethodId))) {}
    for (paramContext = (InputMethodInfo)this.mMethodMap.get(this.mCurMethodId);; paramContext = (InputMethodInfo)paramContext.get(0))
    {
      Slog.i("InputMethodManagerService", "Default found, using " + paramContext.getId());
      setSelectedInputMethodAndSubtypeLocked(paramContext, -1, false);
      return;
    }
  }
  
  private void resetSelectedInputMethodAndSubtypeLocked(String paramString)
  {
    InputMethodInfo localInputMethodInfo = (InputMethodInfo)this.mMethodMap.get(paramString);
    int j = -1;
    int i = j;
    if (localInputMethodInfo != null)
    {
      if (!TextUtils.isEmpty(paramString)) {
        break label40;
      }
      i = j;
    }
    for (;;)
    {
      setSelectedInputMethodAndSubtypeLocked(localInputMethodInfo, i, false);
      return;
      label40:
      paramString = this.mSettings.getLastSubtypeForInputMethodLocked(paramString);
      i = j;
      if (paramString != null) {
        try
        {
          i = InputMethodUtils.getSubtypeIdFromHashCode(localInputMethodInfo, Integer.parseInt(paramString));
        }
        catch (NumberFormatException localNumberFormatException)
        {
          Slog.w("InputMethodManagerService", "HashCode for subtype looks broken: " + paramString, localNumberFormatException);
          i = j;
        }
      }
    }
  }
  
  private void resetStateIfCurrentLocaleChangedLocked()
  {
    resetAllInternalStateLocked(true, true);
  }
  
  static void restoreEnabledInputMethods(Context paramContext, String paramString1, String paramString2)
  {
    ArrayMap localArrayMap = InputMethodUtils.parseInputMethodsAndSubtypesString(paramString1);
    Iterator localIterator = InputMethodUtils.parseInputMethodsAndSubtypesString(paramString2).entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str = (String)localEntry.getKey();
      paramString2 = (ArraySet)localArrayMap.get(str);
      paramString1 = paramString2;
      if (paramString2 == null)
      {
        paramString1 = new ArraySet(2);
        localArrayMap.put(str, paramString1);
      }
      paramString1.addAll((ArraySet)localEntry.getValue());
    }
    paramString1 = InputMethodUtils.buildInputMethodsAndSubtypesString(localArrayMap);
    Settings.Secure.putString(paramContext.getContentResolver(), "enabled_input_methods", paramString1);
  }
  
  private void setInputMethodWithSubtypeId(IBinder paramIBinder, String paramString, int paramInt)
  {
    synchronized (this.mMethodMap)
    {
      setInputMethodWithSubtypeIdLocked(paramIBinder, paramString, paramInt);
      return;
    }
  }
  
  private void setInputMethodWithSubtypeIdLocked(IBinder paramIBinder, String paramString, int paramInt)
  {
    if (paramIBinder == null)
    {
      if (this.mContext.checkCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS") != 0) {
        throw new SecurityException("Using null token requires permission android.permission.WRITE_SECURE_SETTINGS");
      }
    }
    else if (this.mCurToken != paramIBinder)
    {
      Slog.w("InputMethodManagerService", "Ignoring setInputMethod of uid " + Binder.getCallingUid() + " token: " + paramIBinder);
      return;
    }
    long l = Binder.clearCallingIdentity();
    try
    {
      setInputMethodLocked(paramString, paramInt);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private void setSelectedInputMethodAndSubtypeLocked(InputMethodInfo paramInputMethodInfo, int paramInt, boolean paramBoolean)
  {
    this.mSettings.saveCurrentInputMethodAndSubtypeToHistory(this.mCurMethodId, this.mCurrentSubtype);
    this.mCurUserActionNotificationSequenceNumber = Math.max(this.mCurUserActionNotificationSequenceNumber + 1, 1);
    if ((this.mCurClient != null) && (this.mCurClient.client != null)) {
      executeOrSendMessage(this.mCurClient.client, this.mCaller.obtainMessageIO(3040, this.mCurUserActionNotificationSequenceNumber, this.mCurClient));
    }
    Object localObject;
    if ((paramInputMethodInfo == null) || (paramInt < 0))
    {
      this.mSettings.putSelectedSubtype(-1);
      this.mCurrentSubtype = null;
      if (!paramBoolean)
      {
        localObject = this.mSettings;
        if (paramInputMethodInfo == null) {
          break label177;
        }
      }
    }
    label177:
    for (paramInputMethodInfo = paramInputMethodInfo.getId();; paramInputMethodInfo = "")
    {
      ((InputMethodUtils.InputMethodSettings)localObject).putSelectedInputMethod(paramInputMethodInfo);
      return;
      if (paramInt < paramInputMethodInfo.getSubtypeCount())
      {
        localObject = paramInputMethodInfo.getSubtypeAt(paramInt);
        this.mSettings.putSelectedSubtype(((InputMethodSubtype)localObject).hashCode());
        this.mCurrentSubtype = ((InputMethodSubtype)localObject);
        break;
      }
      this.mSettings.putSelectedSubtype(-1);
      this.mCurrentSubtype = getCurrentInputMethodSubtypeLocked();
      break;
    }
  }
  
  private boolean shouldShowImeSwitcherLocked(int paramInt)
  {
    if (!this.mShowOngoingImeSwitcherForPhones) {
      return false;
    }
    if (this.mSwitchingDialog != null) {
      return false;
    }
    if (isScreenLocked()) {
      return false;
    }
    if ((paramInt & 0x1) == 0) {
      return false;
    }
    if (this.mWindowManagerInternal.isHardKeyboardAvailable())
    {
      if (this.mHardKeyboardBehavior == 0) {
        return true;
      }
    }
    else if ((paramInt & 0x2) == 0) {
      return false;
    }
    ArrayList localArrayList = this.mSettings.getEnabledInputMethodListLocked();
    int i1 = localArrayList.size();
    if (i1 > 2) {
      return true;
    }
    if (i1 < 1) {
      return false;
    }
    paramInt = 0;
    int i = 0;
    Object localObject1 = null;
    Object localObject2 = null;
    int k = 0;
    if (k < i1)
    {
      Object localObject3 = (InputMethodInfo)localArrayList.get(k);
      List localList = this.mSettings.getEnabledInputMethodSubtypeListLocked(this.mContext, (InputMethodInfo)localObject3, true);
      int i2 = localList.size();
      int j;
      Object localObject4;
      int n;
      if (i2 == 0)
      {
        j = paramInt + 1;
        localObject3 = localObject1;
        localObject4 = localObject2;
        n = i;
      }
      int m;
      do
      {
        k += 1;
        i = n;
        localObject2 = localObject4;
        paramInt = j;
        localObject1 = localObject3;
        break;
        m = 0;
        n = i;
        localObject4 = localObject2;
        j = paramInt;
        localObject3 = localObject1;
      } while (m >= i2);
      localObject3 = (InputMethodSubtype)localList.get(m);
      if (!((InputMethodSubtype)localObject3).isAuxiliary())
      {
        paramInt += 1;
        localObject1 = localObject3;
      }
      for (;;)
      {
        m += 1;
        break;
        i += 1;
        localObject2 = localObject3;
      }
    }
    if ((paramInt > 1) || (i > 1)) {
      return true;
    }
    if ((paramInt == 1) && (i == 1)) {
      return (localObject1 == null) || (localObject2 == null) || ((!((InputMethodSubtype)localObject1).getLocale().equals(((InputMethodSubtype)localObject2).getLocale())) && (!((InputMethodSubtype)localObject2).overridesImplicitlyEnabledSubtype()) && (!((InputMethodSubtype)localObject1).overridesImplicitlyEnabledSubtype())) || (!((InputMethodSubtype)localObject1).containsExtraValueKey("TrySuppressingImeSwitcher"));
    }
    return false;
  }
  
  private void showConfigureInputMethods()
  {
    Intent localIntent = new Intent("android.settings.INPUT_METHOD_SETTINGS");
    localIntent.setFlags(337641472);
    this.mContext.startActivityAsUser(localIntent, null, UserHandle.CURRENT);
  }
  
  private void showInputMethodAndSubtypeEnabler(String arg1)
  {
    Intent localIntent = new Intent("android.settings.INPUT_METHOD_SUBTYPE_SETTINGS");
    localIntent.setFlags(337641472);
    if (!TextUtils.isEmpty(???)) {
      localIntent.putExtra("input_method_id", ???);
    }
    synchronized (this.mMethodMap)
    {
      int i = this.mSettings.getCurrentUserId();
      this.mContext.startActivityAsUser(localIntent, null, UserHandle.of(i));
      return;
    }
  }
  
  private void showInputMethodMenu(boolean paramBoolean)
  {
    Object localObject3 = this.mContext;
    boolean bool = isScreenLocked();
    Object localObject4 = this.mSettings.getSelectedInputMethod();
    int i = this.mSettings.getSelectedInputMethodSubtypeId((String)localObject4);
    int j;
    int k;
    int m;
    int i1;
    synchronized (this.mMethodMap)
    {
      final Object localObject1 = this.mSettings.getExplicitlyOrImplicitlyEnabledInputMethodsAndSubtypeListLocked(this.mContext);
      if (localObject1 != null)
      {
        j = ((HashMap)localObject1).size();
        if (j != 0) {}
      }
      else
      {
        return;
      }
      hideInputMethodMenuLocked();
      localObject1 = this.mSwitchingController.getSortedInputMethodAndSubtypeListLocked(paramBoolean, bool);
      j = i;
      Object localObject5;
      if (i == -1)
      {
        localObject5 = getCurrentInputMethodSubtypeLocked();
        j = i;
        if (localObject5 != null) {
          j = InputMethodUtils.getSubtypeIdFromHashCode((InputMethodInfo)this.mMethodMap.get(this.mCurMethodId), ((InputMethodSubtype)localObject5).hashCode());
        }
      }
      int n = ((List)localObject1).size();
      this.mIms = new InputMethodInfo[n];
      this.mSubtypeIds = new int[n];
      k = 0;
      i = 0;
      if (i < n)
      {
        localObject5 = (InputMethodSubtypeSwitchingController.ImeSubtypeListItem)((List)localObject1).get(i);
        this.mIms[i] = ((InputMethodSubtypeSwitchingController.ImeSubtypeListItem)localObject5).mImi;
        this.mSubtypeIds[i] = ((InputMethodSubtypeSwitchingController.ImeSubtypeListItem)localObject5).mSubtypeId;
        m = k;
        if (!this.mIms[i].getId().equals(localObject4)) {
          break label623;
        }
        i1 = this.mSubtypeIds[i];
        if (i1 != -1) {
          if ((j != -1) || (i1 != 0)) {
            break label634;
          }
        }
      }
      else
      {
        this.mDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper((Context)localObject3, ThemeController.getInstance(this.mContext).getCorrectThemeResource(new int[] { 16974394, 16974374 })));
        this.mDialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
          public void onCancel(DialogInterface paramAnonymousDialogInterface)
          {
            InputMethodManagerService.this.hideInputMethodMenu();
          }
        });
        localObject3 = this.mDialogBuilder.getContext();
        localObject4 = ((Context)localObject3).obtainStyledAttributes(null, R.styleable.DialogPreference, 16842845, 0);
        localObject5 = ((TypedArray)localObject4).getDrawable(2);
        ((TypedArray)localObject4).recycle();
        this.mDialogBuilder.setIcon((Drawable)localObject5);
        localObject4 = ((LayoutInflater)((Context)localObject3).getSystemService(LayoutInflater.class)).inflate(17367152, null);
        this.mDialogBuilder.setCustomTitle((View)localObject4);
        this.mSwitchingDialogTitleView = ((View)localObject4);
        localObject4 = this.mSwitchingDialogTitleView.findViewById(16909195);
        if (this.mWindowManagerInternal.isHardKeyboardAvailable())
        {
          i = 0;
          ((View)localObject4).setVisibility(i);
          localObject4 = (Switch)this.mSwitchingDialogTitleView.findViewById(16909196);
          ((Switch)localObject4).setChecked(this.mShowImeWithHardKeyboard);
          ((Switch)localObject4).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
          {
            public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
            {
              InputMethodManagerService.this.mSettings.setShowImeWithHardKeyboard(paramAnonymousBoolean);
              InputMethodManagerService.this.hideInputMethodMenu();
            }
          });
          localObject1 = new ImeSubtypeListAdapter((Context)localObject3, 17367153, (List)localObject1, k);
          localObject3 = new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface arg1, int paramAnonymousInt)
            {
              for (;;)
              {
                synchronized (InputMethodManagerService.this.mMethodMap)
                {
                  if (InputMethodManagerService.-get3(InputMethodManagerService.this) != null)
                  {
                    i = InputMethodManagerService.-get3(InputMethodManagerService.this).length;
                    if (i > paramAnonymousInt) {}
                  }
                  else
                  {
                    return;
                  }
                  if ((InputMethodManagerService.-get5(InputMethodManagerService.this) == null) || (InputMethodManagerService.-get5(InputMethodManagerService.this).length <= paramAnonymousInt)) {
                    continue;
                  }
                  InputMethodInfo localInputMethodInfo = InputMethodManagerService.-get3(InputMethodManagerService.this)[paramAnonymousInt];
                  int i = InputMethodManagerService.-get5(InputMethodManagerService.this)[paramAnonymousInt];
                  localObject1.mCheckedItem = paramAnonymousInt;
                  localObject1.notifyDataSetChanged();
                  InputMethodManagerService.this.hideInputMethodMenu();
                  if (localInputMethodInfo != null)
                  {
                    if (i >= 0)
                    {
                      paramAnonymousInt = i;
                      if (i < localInputMethodInfo.getSubtypeCount()) {
                        InputMethodManagerService.this.setInputMethodLocked(localInputMethodInfo.getId(), paramAnonymousInt);
                      }
                    }
                  }
                  else {
                    return;
                  }
                }
                paramAnonymousInt = -1;
              }
            }
          };
          this.mDialogBuilder.setSingleChoiceItems((ListAdapter)localObject1, k, (DialogInterface.OnClickListener)localObject3);
          this.mSwitchingDialog = this.mDialogBuilder.create();
          this.mSwitchingDialog.setCanceledOnTouchOutside(true);
          this.mSwitchingDialog.getWindow().setType(2012);
          localObject1 = this.mSwitchingDialog.getWindow().getAttributes();
          ((WindowManager.LayoutParams)localObject1).privateFlags |= 0x10;
          this.mSwitchingDialog.getWindow().getAttributes().setTitle("Select input method");
          updateSystemUi(this.mCurToken, this.mImeWindowVis, this.mBackDisposition);
          this.mSwitchingDialog.show();
          return;
        }
        i = 8;
      }
    }
    for (;;)
    {
      m = i;
      label623:
      label634:
      do
      {
        i += 1;
        k = m;
        break;
        m = k;
      } while (i1 != j);
    }
  }
  
  private InputBindResult startInput(int paramInt1, IInputMethodClient paramIInputMethodClient, IInputContext paramIInputContext, int paramInt2, EditorInfo paramEditorInfo, int paramInt3)
  {
    if (!calledFromValidUser()) {
      return null;
    }
    synchronized (this.mMethodMap)
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        paramIInputMethodClient = startInputLocked(paramInt1, paramIInputMethodClient, paramIInputContext, paramInt2, paramEditorInfo, paramInt3);
        Binder.restoreCallingIdentity(l);
        return paramIInputMethodClient;
      }
      finally
      {
        paramIInputMethodClient = finally;
        Binder.restoreCallingIdentity(l);
        throw paramIInputMethodClient;
      }
    }
  }
  
  private void switchUserLocked(int paramInt)
  {
    this.mSettingsObserver.registerContentObserverLocked(paramInt);
    if ((this.mSystemReady) && (this.mUserManager.isUserUnlockingOrUnlocked(paramInt))) {}
    for (boolean bool = false;; bool = true)
    {
      this.mSettings.switchCurrentUser(paramInt, bool);
      updateCurrentProfileIds();
      this.mFileManager = new InputMethodFileManager(this.mMethodMap, paramInt);
      bool = TextUtils.isEmpty(this.mSettings.getSelectedInputMethod());
      resetAllInternalStateLocked(false, bool);
      if (bool) {
        InputMethodUtils.setNonSelectedSystemImesDisabledUntilUsed(this.mIPackageManager, this.mSettings.getEnabledInputMethodListLocked(), paramInt, this.mContext.getBasePackageName());
      }
      return;
    }
  }
  
  private void updateSystemUi(IBinder paramIBinder, int paramInt1, int paramInt2)
  {
    synchronized (this.mMethodMap)
    {
      updateSystemUiLocked(paramIBinder, paramInt1, paramInt2);
      return;
    }
  }
  
  /* Error */
  private void updateSystemUiLocked(IBinder paramIBinder, int paramInt1, int paramInt2)
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: invokespecial 1320	com/android/server/InputMethodManagerService:calledWithValidToken	(Landroid/os/IBinder;)Z
    //   5: ifne +44 -> 49
    //   8: invokestatic 677	android/os/Binder:getCallingUid	()I
    //   11: istore_2
    //   12: ldc 111
    //   14: new 631	java/lang/StringBuilder
    //   17: dup
    //   18: invokespecial 632	java/lang/StringBuilder:<init>	()V
    //   21: ldc_w 1322
    //   24: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   27: iload_2
    //   28: invokevirtual 1075	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   31: ldc_w 1324
    //   34: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   37: aload_1
    //   38: invokevirtual 641	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   41: invokevirtual 646	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   44: invokestatic 649	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   47: pop
    //   48: return
    //   49: invokestatic 1081	android/os/Binder:clearCallingIdentity	()J
    //   52: lstore 5
    //   54: iload_2
    //   55: istore 4
    //   57: iload_2
    //   58: ifeq +23 -> 81
    //   61: iload_2
    //   62: istore 4
    //   64: aload_0
    //   65: invokespecial 1325	com/android/server/InputMethodManagerService:isKeyguardLocked	()Z
    //   68: ifeq +13 -> 81
    //   71: aload_0
    //   72: getfield 1327	com/android/server/InputMethodManagerService:mCurClientInKeyguard	Z
    //   75: ifeq +136 -> 211
    //   78: iload_2
    //   79: istore 4
    //   81: aload_0
    //   82: iload 4
    //   84: invokespecial 1329	com/android/server/InputMethodManagerService:shouldShowImeSwitcherLocked	(I)Z
    //   87: istore 7
    //   89: aload_0
    //   90: getfield 1331	com/android/server/InputMethodManagerService:mStatusBar	Lcom/android/server/statusbar/StatusBarManagerService;
    //   93: ifnull +16 -> 109
    //   96: aload_0
    //   97: getfield 1331	com/android/server/InputMethodManagerService:mStatusBar	Lcom/android/server/statusbar/StatusBarManagerService;
    //   100: aload_1
    //   101: iload 4
    //   103: iload_3
    //   104: iload 7
    //   106: invokevirtual 1337	com/android/server/statusbar/StatusBarManagerService:setImeWindowStatus	(Landroid/os/IBinder;IIZ)V
    //   109: aload_0
    //   110: getfield 313	com/android/server/InputMethodManagerService:mMethodMap	Ljava/util/HashMap;
    //   113: aload_0
    //   114: getfield 735	com/android/server/InputMethodManagerService:mCurMethodId	Ljava/lang/String;
    //   117: invokevirtual 824	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   120: checkcast 713	android/view/inputmethod/InputMethodInfo
    //   123: astore 8
    //   125: aload 8
    //   127: ifnull +123 -> 250
    //   130: iload 7
    //   132: ifeq +118 -> 250
    //   135: aload_0
    //   136: getfield 355	com/android/server/InputMethodManagerService:mRes	Landroid/content/res/Resources;
    //   139: ldc_w 1338
    //   142: invokevirtual 1342	android/content/res/Resources:getText	(I)Ljava/lang/CharSequence;
    //   145: astore_1
    //   146: aload_0
    //   147: getfield 347	com/android/server/InputMethodManagerService:mContext	Landroid/content/Context;
    //   150: aload 8
    //   152: aload_0
    //   153: getfield 747	com/android/server/InputMethodManagerService:mCurrentSubtype	Landroid/view/inputmethod/InputMethodSubtype;
    //   156: invokestatic 888	com/android/internal/inputmethod/InputMethodUtils:getImeAndSubtypeDisplayName	(Landroid/content/Context;Landroid/view/inputmethod/InputMethodInfo;Landroid/view/inputmethod/InputMethodSubtype;)Ljava/lang/CharSequence;
    //   159: astore 8
    //   161: aload_0
    //   162: getfield 487	com/android/server/InputMethodManagerService:mImeSwitcherNotification	Landroid/app/Notification$Builder;
    //   165: aload_1
    //   166: invokevirtual 1346	android/app/Notification$Builder:setContentTitle	(Ljava/lang/CharSequence;)Landroid/app/Notification$Builder;
    //   169: aload 8
    //   171: invokevirtual 1349	android/app/Notification$Builder:setContentText	(Ljava/lang/CharSequence;)Landroid/app/Notification$Builder;
    //   174: aload_0
    //   175: getfield 506	com/android/server/InputMethodManagerService:mImeSwitchPendingIntent	Landroid/app/PendingIntent;
    //   178: invokevirtual 1353	android/app/Notification$Builder:setContentIntent	(Landroid/app/PendingIntent;)Landroid/app/Notification$Builder;
    //   181: pop
    //   182: aload_0
    //   183: getfield 1355	com/android/server/InputMethodManagerService:mNotificationManager	Landroid/app/NotificationManager;
    //   186: ifnull +19 -> 205
    //   189: aload_0
    //   190: getfield 383	com/android/server/InputMethodManagerService:mIWindowManager	Landroid/view/IWindowManager;
    //   193: invokeinterface 1360 1 0
    //   198: istore 7
    //   200: iload 7
    //   202: ifeq +15 -> 217
    //   205: lload 5
    //   207: invokestatic 1085	android/os/Binder:restoreCallingIdentity	(J)V
    //   210: return
    //   211: iconst_0
    //   212: istore 4
    //   214: goto -133 -> 81
    //   217: aload_0
    //   218: getfield 1355	com/android/server/InputMethodManagerService:mNotificationManager	Landroid/app/NotificationManager;
    //   221: aconst_null
    //   222: ldc_w 1338
    //   225: aload_0
    //   226: getfield 487	com/android/server/InputMethodManagerService:mImeSwitcherNotification	Landroid/app/Notification$Builder;
    //   229: invokevirtual 1364	android/app/Notification$Builder:build	()Landroid/app/Notification;
    //   232: getstatic 552	android/os/UserHandle:ALL	Landroid/os/UserHandle;
    //   235: invokevirtual 1370	android/app/NotificationManager:notifyAsUser	(Ljava/lang/String;ILandroid/app/Notification;Landroid/os/UserHandle;)V
    //   238: aload_0
    //   239: iconst_1
    //   240: putfield 529	com/android/server/InputMethodManagerService:mNotificationShown	Z
    //   243: goto -38 -> 205
    //   246: astore_1
    //   247: goto -42 -> 205
    //   250: aload_0
    //   251: getfield 529	com/android/server/InputMethodManagerService:mNotificationShown	Z
    //   254: ifeq -49 -> 205
    //   257: aload_0
    //   258: getfield 1355	com/android/server/InputMethodManagerService:mNotificationManager	Landroid/app/NotificationManager;
    //   261: ifnull -56 -> 205
    //   264: aload_0
    //   265: getfield 1355	com/android/server/InputMethodManagerService:mNotificationManager	Landroid/app/NotificationManager;
    //   268: aconst_null
    //   269: ldc_w 1338
    //   272: getstatic 552	android/os/UserHandle:ALL	Landroid/os/UserHandle;
    //   275: invokevirtual 1374	android/app/NotificationManager:cancelAsUser	(Ljava/lang/String;ILandroid/os/UserHandle;)V
    //   278: aload_0
    //   279: iconst_0
    //   280: putfield 529	com/android/server/InputMethodManagerService:mNotificationShown	Z
    //   283: goto -78 -> 205
    //   286: astore_1
    //   287: lload 5
    //   289: invokestatic 1085	android/os/Binder:restoreCallingIdentity	(J)V
    //   292: aload_1
    //   293: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	294	0	this	InputMethodManagerService
    //   0	294	1	paramIBinder	IBinder
    //   0	294	2	paramInt1	int
    //   0	294	3	paramInt2	int
    //   55	158	4	i	int
    //   52	236	5	l	long
    //   87	114	7	bool	boolean
    //   123	47	8	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   182	200	246	android/os/RemoteException
    //   217	243	246	android/os/RemoteException
    //   64	78	286	finally
    //   81	109	286	finally
    //   109	125	286	finally
    //   135	182	286	finally
    //   182	200	286	finally
    //   217	243	286	finally
    //   250	283	286	finally
  }
  
  private InputBindResult windowGainedFocus(int paramInt1, IInputMethodClient paramIInputMethodClient, IBinder paramIBinder, int paramInt2, int paramInt3, int paramInt4, EditorInfo paramEditorInfo, IInputContext paramIInputContext, int paramInt5)
  {
    boolean bool = calledFromValidUser();
    Object localObject2 = null;
    Object localObject3 = null;
    Object localObject4 = null;
    Object localObject1 = null;
    long l = Binder.clearCallingIdentity();
    ClientState localClientState;
    try
    {
      synchronized (this.mMethodMap)
      {
        localClientState = (ClientState)this.mClients.get(paramIInputMethodClient.asBinder());
        if (localClientState == null) {
          throw new IllegalArgumentException("unknown client " + paramIInputMethodClient.asBinder());
        }
      }
      try
      {
        if (!this.mIWindowManager.inputMethodClientHasFocus(localClientState.client))
        {
          Slog.w("InputMethodManagerService", "Focus gain on non-focused client " + localClientState.client + " (uid=" + localClientState.uid + " pid=" + localClientState.pid + ")");
          return null;
        }
      }
      catch (RemoteException localRemoteException)
      {
        if (!bool)
        {
          Slog.w("InputMethodManagerService", "A background user is requesting window. Hiding IME.");
          Slog.w("InputMethodManagerService", "If you want to interect with IME, you need android.permission.INTERACT_ACROSS_USERS_FULL");
          hideCurrentInputLocked(0, null);
          return null;
        }
        if (this.mCurFocusedWindow == paramIBinder)
        {
          Slog.w("InputMethodManagerService", "Window already focused, ignoring focus gain of: " + paramIInputMethodClient + " attribute=" + paramEditorInfo + ", token = " + paramIBinder);
          if (paramEditorInfo != null)
          {
            paramIInputMethodClient = startInputUncheckedLocked(localClientState, paramIInputContext, paramInt5, paramEditorInfo, paramInt2);
            return paramIInputMethodClient;
          }
          return null;
        }
        this.mCurFocusedWindow = paramIBinder;
        this.mCurFocusedWindowClient = localClientState;
        if ((paramInt3 & 0xF0) == 16) {
          break label406;
        }
      }
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
    bool = this.mRes.getConfiguration().isLayoutSizeAtLeast(3);
    for (;;)
    {
      paramIBinder = paramIInputMethodClient;
      if (paramInt1 == 0)
      {
        paramIBinder = paramIInputMethodClient;
        if (paramEditorInfo != null) {
          paramIBinder = startInputUncheckedLocked(localClientState, paramIInputContext, paramInt5, paramEditorInfo, paramInt2);
        }
      }
      Binder.restoreCallingIdentity(l);
      return paramIBinder;
      label406:
      bool = true;
      while ((paramInt2 & 0x2) == 0)
      {
        i = 0;
        break label681;
        if ((i != 0) && (bool))
        {
          paramInt1 = j;
          paramIInputMethodClient = (IInputMethodClient)localObject1;
          if (i == 0) {
            break;
          }
          paramInt1 = j;
          paramIInputMethodClient = (IInputMethodClient)localObject1;
          if (!bool) {
            break;
          }
          paramInt1 = j;
          paramIInputMethodClient = (IInputMethodClient)localObject1;
          if ((paramInt3 & 0x100) == 0) {
            break;
          }
          paramInt1 = k;
          paramIInputMethodClient = (IInputMethodClient)localObject2;
          if (paramEditorInfo != null)
          {
            paramIInputMethodClient = startInputUncheckedLocked(localClientState, paramIInputContext, paramInt5, paramEditorInfo, paramInt2);
            paramInt1 = 1;
          }
          showCurrentInputLocked(1, null);
          break;
        }
        paramInt1 = j;
        paramIInputMethodClient = (IInputMethodClient)localObject1;
        if (!WindowManager.LayoutParams.mayUseInputMethod(paramInt4)) {
          break;
        }
        hideCurrentInputLocked(2, null);
        paramInt1 = j;
        paramIInputMethodClient = (IInputMethodClient)localObject1;
        break;
        paramInt1 = j;
        paramIInputMethodClient = (IInputMethodClient)localObject1;
        if ((paramInt3 & 0x100) == 0) {
          break;
        }
        hideCurrentInputLocked(0, null);
        paramInt1 = j;
        paramIInputMethodClient = (IInputMethodClient)localObject1;
        break;
        hideCurrentInputLocked(0, null);
        paramInt1 = j;
        paramIInputMethodClient = (IInputMethodClient)localObject1;
        break;
        paramInt1 = j;
        paramIInputMethodClient = (IInputMethodClient)localObject1;
        if ((paramInt3 & 0x100) == 0) {
          break;
        }
        paramInt1 = m;
        paramIInputMethodClient = (IInputMethodClient)localObject3;
        if (paramEditorInfo != null)
        {
          paramIInputMethodClient = startInputUncheckedLocked(localClientState, paramIInputContext, paramInt5, paramEditorInfo, paramInt2);
          paramInt1 = 1;
        }
        showCurrentInputLocked(1, null);
        break;
        paramInt1 = n;
        paramIInputMethodClient = (IInputMethodClient)localObject4;
        if (paramEditorInfo != null)
        {
          paramIInputMethodClient = startInputUncheckedLocked(localClientState, paramIInputContext, paramInt5, paramEditorInfo, paramInt2);
          paramInt1 = 1;
        }
        showCurrentInputLocked(1, null);
        break;
      }
      int i = 1;
      label681:
      int k = 0;
      int m = 0;
      int n = 0;
      int j = 0;
      paramInt1 = j;
      paramIInputMethodClient = (IInputMethodClient)localObject1;
      switch (paramInt3 & 0xF)
      {
      }
      paramInt1 = j;
      paramIInputMethodClient = (IInputMethodClient)localObject1;
    }
  }
  
  public void addClient(IInputMethodClient paramIInputMethodClient, IInputContext paramIInputContext, int paramInt1, int paramInt2)
  {
    if (!calledFromValidUser()) {
      return;
    }
    synchronized (this.mMethodMap)
    {
      this.mClients.put(paramIInputMethodClient.asBinder(), new ClientState(paramIInputMethodClient, paramIInputContext, paramInt1, paramInt2));
      return;
    }
  }
  
  InputBindResult attachNewInputLocked(boolean paramBoolean)
  {
    InputChannel localInputChannel = null;
    if (!this.mBoundToMethod)
    {
      executeOrSendMessage(this.mCurMethod, this.mCaller.obtainMessageOO(1010, this.mCurMethod, this.mCurClient.binding));
      this.mBoundToMethod = true;
    }
    SessionState localSessionState = this.mCurClient.curSession;
    if (paramBoolean) {
      executeOrSendMessage(localSessionState.method, this.mCaller.obtainMessageIOOO(2000, this.mCurInputContextMissingMethods, localSessionState, this.mCurInputContext, this.mCurAttribute));
    }
    for (;;)
    {
      if (this.mShowRequested) {
        showCurrentInputLocked(getAppShowFlags(), null);
      }
      IInputMethodSession localIInputMethodSession = localSessionState.session;
      if (localSessionState.channel != null) {
        localInputChannel = localSessionState.channel.dup();
      }
      return new InputBindResult(localIInputMethodSession, localInputChannel, this.mCurId, this.mCurSeq, this.mCurUserActionNotificationSequenceNumber);
      executeOrSendMessage(localSessionState.method, this.mCaller.obtainMessageIOOO(2010, this.mCurInputContextMissingMethods, localSessionState, this.mCurInputContext, this.mCurAttribute));
    }
  }
  
  void buildInputMethodListLocked(boolean paramBoolean)
  {
    this.mMethodList.clear();
    this.mMethodMap.clear();
    Object localObject1 = this.mContext.getPackageManager().queryIntentServicesAsUser(new Intent("android.view.InputMethod"), 32896, this.mSettings.getCurrentUserId());
    Object localObject2 = this.mFileManager.getAllAdditionalInputMethodSubtypes();
    int i = 0;
    if (i < ((List)localObject1).size())
    {
      Object localObject3 = (ResolveInfo)((List)localObject1).get(i);
      Object localObject4 = ((ResolveInfo)localObject3).serviceInfo;
      ComponentName localComponentName = new ComponentName(((ServiceInfo)localObject4).packageName, ((ServiceInfo)localObject4).name);
      if (!"android.permission.BIND_INPUT_METHOD".equals(((ServiceInfo)localObject4).permission)) {
        Slog.w("InputMethodManagerService", "Skipping input method " + localComponentName + ": it does not require the permission " + "android.permission.BIND_INPUT_METHOD");
      }
      for (;;)
      {
        i += 1;
        break;
        try
        {
          localObject3 = new InputMethodInfo(this.mContext, (ResolveInfo)localObject3, (Map)localObject2);
          this.mMethodList.add(localObject3);
          localObject4 = ((InputMethodInfo)localObject3).getId();
          this.mMethodMap.put(localObject4, localObject3);
        }
        catch (Exception localException)
        {
          Slog.wtf("InputMethodManagerService", "Unable to load input method " + localComponentName, localException);
        }
      }
    }
    boolean bool = paramBoolean;
    int k;
    int m;
    if (!paramBoolean)
    {
      k = 0;
      localObject1 = this.mSettings.getEnabledInputMethodListLocked();
      m = ((List)localObject1).size();
      i = 0;
    }
    for (;;)
    {
      int j = k;
      if (i < m)
      {
        localObject2 = (InputMethodInfo)((List)localObject1).get(i);
        if (this.mMethodList.contains(localObject2)) {
          j = 1;
        }
      }
      else
      {
        bool = paramBoolean;
        if (j == 0)
        {
          Slog.i("InputMethodManagerService", "All the enabled IMEs are gone. Reset default enabled IMEs.");
          bool = true;
          resetSelectedInputMethodAndSubtypeLocked("");
        }
        if (!bool) {
          break;
        }
        localObject1 = InputMethodUtils.getDefaultEnabledImes(this.mContext, this.mSystemReady, this.mMethodList);
        j = ((ArrayList)localObject1).size();
        i = 0;
        while (i < j)
        {
          setInputMethodEnabledLocked(((InputMethodInfo)((ArrayList)localObject1).get(i)).getId(), true);
          i += 1;
        }
      }
      i += 1;
    }
    localObject1 = this.mSettings.getSelectedInputMethod();
    if (!TextUtils.isEmpty((CharSequence)localObject1))
    {
      if (this.mMethodMap.containsKey(localObject1)) {
        break label471;
      }
      Slog.w("InputMethodManagerService", "Default IME is uninstalled. Choose new default IME.");
      if (chooseNewDefaultIMELocked()) {
        updateInputMethodsFromSettingsLocked(true);
      }
    }
    for (;;)
    {
      this.mSwitchingController.resetCircularListLocked(this.mContext);
      return;
      label471:
      setInputMethodEnabledLocked((String)localObject1, true);
    }
  }
  
  void clearClientSessionLocked(ClientState paramClientState)
  {
    finishSessionLocked(paramClientState.curSession);
    paramClientState.curSession = null;
    paramClientState.sessionRequested = false;
  }
  
  void clearCurMethodLocked()
  {
    if (this.mCurMethod != null)
    {
      Iterator localIterator = this.mClients.values().iterator();
      while (localIterator.hasNext()) {
        clearClientSessionLocked((ClientState)localIterator.next());
      }
      finishSessionLocked(this.mEnabledSession);
      this.mEnabledSession = null;
      this.mCurMethod = null;
    }
    if (this.mStatusBar != null) {
      this.mStatusBar.setIconVisibility(this.mSlotIme, false);
    }
  }
  
  /* Error */
  public void clearLastInputMethodWindowForTransition(IBinder paramIBinder)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 1300	com/android/server/InputMethodManagerService:calledFromValidUser	()Z
    //   4: ifne +4 -> 8
    //   7: return
    //   8: invokestatic 1081	android/os/Binder:clearCallingIdentity	()J
    //   11: lstore_3
    //   12: aload_0
    //   13: getfield 313	com/android/server/InputMethodManagerService:mMethodMap	Ljava/util/HashMap;
    //   16: astore 5
    //   18: aload 5
    //   20: monitorenter
    //   21: aload_0
    //   22: aload_1
    //   23: invokespecial 1320	com/android/server/InputMethodManagerService:calledWithValidToken	(Landroid/os/IBinder;)Z
    //   26: ifne +51 -> 77
    //   29: invokestatic 677	android/os/Binder:getCallingUid	()I
    //   32: istore_2
    //   33: ldc 111
    //   35: new 631	java/lang/StringBuilder
    //   38: dup
    //   39: invokespecial 632	java/lang/StringBuilder:<init>	()V
    //   42: ldc_w 1577
    //   45: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   48: iload_2
    //   49: invokevirtual 1075	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   52: ldc_w 1324
    //   55: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   58: aload_1
    //   59: invokevirtual 641	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   62: invokevirtual 646	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   65: invokestatic 649	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   68: pop
    //   69: aload 5
    //   71: monitorexit
    //   72: lload_3
    //   73: invokestatic 1085	android/os/Binder:restoreCallingIdentity	(J)V
    //   76: return
    //   77: aload 5
    //   79: monitorexit
    //   80: aload_0
    //   81: getfield 392	com/android/server/InputMethodManagerService:mWindowManagerInternal	Landroid/view/WindowManagerInternal;
    //   84: invokevirtual 1579	android/view/WindowManagerInternal:clearLastInputMethodWindowForTransition	()V
    //   87: lload_3
    //   88: invokestatic 1085	android/os/Binder:restoreCallingIdentity	(J)V
    //   91: return
    //   92: astore_1
    //   93: aload 5
    //   95: monitorexit
    //   96: aload_1
    //   97: athrow
    //   98: astore_1
    //   99: lload_3
    //   100: invokestatic 1085	android/os/Binder:restoreCallingIdentity	(J)V
    //   103: aload_1
    //   104: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	105	0	this	InputMethodManagerService
    //   0	105	1	paramIBinder	IBinder
    //   32	17	2	i	int
    //   11	89	3	l	long
    // Exception table:
    //   from	to	target	type
    //   21	69	92	finally
    //   12	21	98	finally
    //   69	72	98	finally
    //   77	87	98	finally
    //   93	98	98	finally
  }
  
  public IInputContentUriToken createInputContentUriToken(IBinder paramIBinder, Uri paramUri, String paramString)
  {
    if (!calledFromValidUser()) {
      return null;
    }
    if (paramIBinder == null) {
      throw new NullPointerException("token");
    }
    if (paramString == null) {
      throw new NullPointerException("packageName");
    }
    if (paramUri == null) {
      throw new NullPointerException("contentUri");
    }
    if (!"content".equals(paramUri.getScheme())) {
      throw new InvalidParameterException("contentUri must have content scheme");
    }
    synchronized (this.mMethodMap)
    {
      int i = Binder.getCallingUid();
      String str = this.mCurMethodId;
      if (str == null) {
        return null;
      }
      if (this.mCurToken != paramIBinder)
      {
        Slog.e("InputMethodManagerService", "Ignoring createInputContentUriToken mCurToken=" + this.mCurToken + " token=" + paramIBinder);
        return null;
      }
      if (!TextUtils.equals(this.mCurAttribute.packageName, paramString))
      {
        Slog.e("InputMethodManagerService", "Ignoring createInputContentUriToken mCurAttribute.packageName=" + this.mCurAttribute.packageName + " packageName=" + paramString);
        return null;
      }
      paramIBinder = new InputContentUriTokenHandler(paramUri, i, paramString, UserHandle.getUserId(i), UserHandle.getUserId(this.mCurClient.uid));
      return paramIBinder;
    }
  }
  
  /* Error */
  protected void dump(java.io.FileDescriptor paramFileDescriptor, java.io.PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 347	com/android/server/InputMethodManagerService:mContext	Landroid/content/Context;
    //   4: ldc_w 1624
    //   7: invokevirtual 690	android/content/Context:checkCallingOrSelfPermission	(Ljava/lang/String;)I
    //   10: ifeq +42 -> 52
    //   13: aload_2
    //   14: new 631	java/lang/StringBuilder
    //   17: dup
    //   18: invokespecial 632	java/lang/StringBuilder:<init>	()V
    //   21: ldc_w 1626
    //   24: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   27: invokestatic 1629	android/os/Binder:getCallingPid	()I
    //   30: invokevirtual 1075	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   33: ldc_w 1631
    //   36: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   39: invokestatic 677	android/os/Binder:getCallingUid	()I
    //   42: invokevirtual 1075	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   45: invokevirtual 646	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   48: invokevirtual 1636	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   51: return
    //   52: new 1638	android/util/PrintWriterPrinter
    //   55: dup
    //   56: aload_2
    //   57: invokespecial 1641	android/util/PrintWriterPrinter:<init>	(Ljava/io/PrintWriter;)V
    //   60: astore 6
    //   62: aload_0
    //   63: getfield 313	com/android/server/InputMethodManagerService:mMethodMap	Ljava/util/HashMap;
    //   66: astore 7
    //   68: aload 7
    //   70: monitorenter
    //   71: aload 6
    //   73: ldc_w 1643
    //   76: invokeinterface 1646 2 0
    //   81: aload_0
    //   82: getfield 308	com/android/server/InputMethodManagerService:mMethodList	Ljava/util/ArrayList;
    //   85: invokevirtual 1539	java/util/ArrayList:size	()I
    //   88: istore 5
    //   90: aload 6
    //   92: ldc_w 1648
    //   95: invokeinterface 1646 2 0
    //   100: iconst_0
    //   101: istore 4
    //   103: iload 4
    //   105: iload 5
    //   107: if_icmpge +70 -> 177
    //   110: aload_0
    //   111: getfield 308	com/android/server/InputMethodManagerService:mMethodList	Ljava/util/ArrayList;
    //   114: iload 4
    //   116: invokevirtual 848	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   119: checkcast 713	android/view/inputmethod/InputMethodInfo
    //   122: astore 8
    //   124: aload 6
    //   126: new 631	java/lang/StringBuilder
    //   129: dup
    //   130: invokespecial 632	java/lang/StringBuilder:<init>	()V
    //   133: ldc_w 1650
    //   136: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   139: iload 4
    //   141: invokevirtual 1075	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   144: ldc_w 1652
    //   147: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   150: invokevirtual 646	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   153: invokeinterface 1646 2 0
    //   158: aload 8
    //   160: aload 6
    //   162: ldc_w 1654
    //   165: invokevirtual 1657	android/view/inputmethod/InputMethodInfo:dump	(Landroid/util/Printer;Ljava/lang/String;)V
    //   168: iload 4
    //   170: iconst_1
    //   171: iadd
    //   172: istore 4
    //   174: goto -71 -> 103
    //   177: aload 6
    //   179: ldc_w 1659
    //   182: invokeinterface 1646 2 0
    //   187: aload_0
    //   188: getfield 328	com/android/server/InputMethodManagerService:mClients	Ljava/util/HashMap;
    //   191: invokevirtual 1566	java/util/HashMap:values	()Ljava/util/Collection;
    //   194: invokeinterface 724 1 0
    //   199: astore 8
    //   201: aload 8
    //   203: invokeinterface 729 1 0
    //   208: ifeq +182 -> 390
    //   211: aload 8
    //   213: invokeinterface 733 1 0
    //   218: checkcast 24	com/android/server/InputMethodManagerService$ClientState
    //   221: astore 9
    //   223: aload 6
    //   225: new 631	java/lang/StringBuilder
    //   228: dup
    //   229: invokespecial 632	java/lang/StringBuilder:<init>	()V
    //   232: ldc_w 1661
    //   235: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   238: aload 9
    //   240: invokevirtual 641	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   243: ldc_w 1652
    //   246: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   249: invokevirtual 646	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   252: invokeinterface 1646 2 0
    //   257: aload 6
    //   259: new 631	java/lang/StringBuilder
    //   262: dup
    //   263: invokespecial 632	java/lang/StringBuilder:<init>	()V
    //   266: ldc_w 1663
    //   269: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   272: aload 9
    //   274: getfield 858	com/android/server/InputMethodManagerService$ClientState:client	Lcom/android/internal/view/IInputMethodClient;
    //   277: invokevirtual 641	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   280: invokevirtual 646	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   283: invokeinterface 1646 2 0
    //   288: aload 6
    //   290: new 631	java/lang/StringBuilder
    //   293: dup
    //   294: invokespecial 632	java/lang/StringBuilder:<init>	()V
    //   297: ldc_w 1665
    //   300: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   303: aload 9
    //   305: getfield 1668	com/android/server/InputMethodManagerService$ClientState:inputContext	Lcom/android/internal/view/IInputContext;
    //   308: invokevirtual 641	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   311: invokevirtual 646	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   314: invokeinterface 1646 2 0
    //   319: aload 6
    //   321: new 631	java/lang/StringBuilder
    //   324: dup
    //   325: invokespecial 632	java/lang/StringBuilder:<init>	()V
    //   328: ldc_w 1670
    //   331: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   334: aload 9
    //   336: getfield 1561	com/android/server/InputMethodManagerService$ClientState:sessionRequested	Z
    //   339: invokevirtual 1673	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   342: invokevirtual 646	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   345: invokeinterface 1646 2 0
    //   350: aload 6
    //   352: new 631	java/lang/StringBuilder
    //   355: dup
    //   356: invokespecial 632	java/lang/StringBuilder:<init>	()V
    //   359: ldc_w 1675
    //   362: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   365: aload 9
    //   367: getfield 1454	com/android/server/InputMethodManagerService$ClientState:curSession	Lcom/android/server/InputMethodManagerService$SessionState;
    //   370: invokevirtual 641	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   373: invokevirtual 646	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   376: invokeinterface 1646 2 0
    //   381: goto -180 -> 201
    //   384: astore_1
    //   385: aload 7
    //   387: monitorexit
    //   388: aload_1
    //   389: athrow
    //   390: aload 6
    //   392: new 631	java/lang/StringBuilder
    //   395: dup
    //   396: invokespecial 632	java/lang/StringBuilder:<init>	()V
    //   399: ldc_w 1677
    //   402: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   405: aload_0
    //   406: getfield 735	com/android/server/InputMethodManagerService:mCurMethodId	Ljava/lang/String;
    //   409: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   412: invokevirtual 646	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   415: invokeinterface 1646 2 0
    //   420: aload_0
    //   421: getfield 854	com/android/server/InputMethodManagerService:mCurClient	Lcom/android/server/InputMethodManagerService$ClientState;
    //   424: astore 9
    //   426: aload 6
    //   428: new 631	java/lang/StringBuilder
    //   431: dup
    //   432: invokespecial 632	java/lang/StringBuilder:<init>	()V
    //   435: ldc_w 1679
    //   438: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   441: aload 9
    //   443: invokevirtual 641	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   446: ldc_w 1681
    //   449: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   452: aload_0
    //   453: getfield 1479	com/android/server/InputMethodManagerService:mCurSeq	I
    //   456: invokevirtual 1075	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   459: invokevirtual 646	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   462: invokeinterface 1646 2 0
    //   467: aload 6
    //   469: new 631	java/lang/StringBuilder
    //   472: dup
    //   473: invokespecial 632	java/lang/StringBuilder:<init>	()V
    //   476: ldc_w 1683
    //   479: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   482: aload_0
    //   483: getfield 1411	com/android/server/InputMethodManagerService:mCurFocusedWindow	Landroid/os/IBinder;
    //   486: invokevirtual 641	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   489: invokevirtual 646	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   492: invokeinterface 1646 2 0
    //   497: aload_0
    //   498: getfield 1423	com/android/server/InputMethodManagerService:mCurFocusedWindowClient	Lcom/android/server/InputMethodManagerService$ClientState;
    //   501: astore 10
    //   503: aload 6
    //   505: new 631	java/lang/StringBuilder
    //   508: dup
    //   509: invokespecial 632	java/lang/StringBuilder:<init>	()V
    //   512: ldc_w 1685
    //   515: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   518: aload 10
    //   520: invokevirtual 641	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   523: invokevirtual 646	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   526: invokeinterface 1646 2 0
    //   531: aload 6
    //   533: new 631	java/lang/StringBuilder
    //   536: dup
    //   537: invokespecial 632	java/lang/StringBuilder:<init>	()V
    //   540: ldc_w 1687
    //   543: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   546: aload_0
    //   547: getfield 1477	com/android/server/InputMethodManagerService:mCurId	Ljava/lang/String;
    //   550: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   553: ldc_w 1689
    //   556: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   559: aload_0
    //   560: getfield 1691	com/android/server/InputMethodManagerService:mHaveConnection	Z
    //   563: invokevirtual 1673	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   566: ldc_w 1693
    //   569: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   572: aload_0
    //   573: getfield 1441	com/android/server/InputMethodManagerService:mBoundToMethod	Z
    //   576: invokevirtual 1673	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   579: invokevirtual 646	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   582: invokeinterface 1646 2 0
    //   587: aload 6
    //   589: new 631	java/lang/StringBuilder
    //   592: dup
    //   593: invokespecial 632	java/lang/StringBuilder:<init>	()V
    //   596: ldc_w 1695
    //   599: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   602: aload_0
    //   603: getfield 701	com/android/server/InputMethodManagerService:mCurToken	Landroid/os/IBinder;
    //   606: invokevirtual 641	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   609: invokevirtual 646	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   612: invokeinterface 1646 2 0
    //   617: aload 6
    //   619: new 631	java/lang/StringBuilder
    //   622: dup
    //   623: invokespecial 632	java/lang/StringBuilder:<init>	()V
    //   626: ldc_w 1697
    //   629: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   632: aload_0
    //   633: getfield 1699	com/android/server/InputMethodManagerService:mCurIntent	Landroid/content/Intent;
    //   636: invokevirtual 641	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   639: invokevirtual 646	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   642: invokeinterface 1646 2 0
    //   647: aload_0
    //   648: getfield 1443	com/android/server/InputMethodManagerService:mCurMethod	Lcom/android/internal/view/IInputMethod;
    //   651: astore 8
    //   653: aload 6
    //   655: new 631	java/lang/StringBuilder
    //   658: dup
    //   659: invokespecial 632	java/lang/StringBuilder:<init>	()V
    //   662: ldc_w 1701
    //   665: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   668: aload_0
    //   669: getfield 1443	com/android/server/InputMethodManagerService:mCurMethod	Lcom/android/internal/view/IInputMethod;
    //   672: invokevirtual 641	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   675: invokevirtual 646	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   678: invokeinterface 1646 2 0
    //   683: aload 6
    //   685: new 631	java/lang/StringBuilder
    //   688: dup
    //   689: invokespecial 632	java/lang/StringBuilder:<init>	()V
    //   692: ldc_w 1703
    //   695: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   698: aload_0
    //   699: getfield 1570	com/android/server/InputMethodManagerService:mEnabledSession	Lcom/android/server/InputMethodManagerService$SessionState;
    //   702: invokevirtual 641	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   705: invokevirtual 646	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   708: invokeinterface 1646 2 0
    //   713: aload 6
    //   715: new 631	java/lang/StringBuilder
    //   718: dup
    //   719: invokespecial 632	java/lang/StringBuilder:<init>	()V
    //   722: ldc_w 1705
    //   725: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   728: aload_0
    //   729: getfield 852	com/android/server/InputMethodManagerService:mImeWindowVis	I
    //   732: invokestatic 1707	com/android/server/InputMethodManagerService:imeWindowStatusToString	(I)Ljava/lang/String;
    //   735: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   738: invokevirtual 646	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   741: invokeinterface 1646 2 0
    //   746: aload 6
    //   748: new 631	java/lang/StringBuilder
    //   751: dup
    //   752: invokespecial 632	java/lang/StringBuilder:<init>	()V
    //   755: ldc_w 1709
    //   758: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   761: aload_0
    //   762: getfield 1469	com/android/server/InputMethodManagerService:mShowRequested	Z
    //   765: invokevirtual 1673	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   768: ldc_w 1711
    //   771: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   774: aload_0
    //   775: getfield 815	com/android/server/InputMethodManagerService:mShowExplicitlyRequested	Z
    //   778: invokevirtual 1673	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   781: ldc_w 1713
    //   784: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   787: aload_0
    //   788: getfield 813	com/android/server/InputMethodManagerService:mShowForced	Z
    //   791: invokevirtual 1673	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   794: ldc_w 1715
    //   797: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   800: aload_0
    //   801: getfield 1717	com/android/server/InputMethodManagerService:mInputShown	Z
    //   804: invokevirtual 1673	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   807: invokevirtual 646	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   810: invokeinterface 1646 2 0
    //   815: aload 6
    //   817: new 631	java/lang/StringBuilder
    //   820: dup
    //   821: invokespecial 632	java/lang/StringBuilder:<init>	()V
    //   824: ldc_w 1719
    //   827: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   830: aload_0
    //   831: getfield 334	com/android/server/InputMethodManagerService:mCurUserActionNotificationSequenceNumber	I
    //   834: invokevirtual 1075	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   837: invokevirtual 646	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   840: invokeinterface 1646 2 0
    //   845: aload 6
    //   847: new 631	java/lang/StringBuilder
    //   850: dup
    //   851: invokespecial 632	java/lang/StringBuilder:<init>	()V
    //   854: ldc_w 1721
    //   857: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   860: aload_0
    //   861: getfield 562	com/android/server/InputMethodManagerService:mSystemReady	Z
    //   864: invokevirtual 1673	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   867: ldc_w 1723
    //   870: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   873: aload_0
    //   874: getfield 332	com/android/server/InputMethodManagerService:mIsInteractive	Z
    //   877: invokevirtual 1673	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   880: invokevirtual 646	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   883: invokeinterface 1646 2 0
    //   888: aload 6
    //   890: new 631	java/lang/StringBuilder
    //   893: dup
    //   894: invokespecial 632	java/lang/StringBuilder:<init>	()V
    //   897: ldc_w 1725
    //   900: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   903: aload_0
    //   904: getfield 367	com/android/server/InputMethodManagerService:mSettingsObserver	Lcom/android/server/InputMethodManagerService$SettingsObserver;
    //   907: invokevirtual 641	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   910: invokevirtual 646	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   913: invokeinterface 1646 2 0
    //   918: aload 6
    //   920: ldc_w 1727
    //   923: invokeinterface 1646 2 0
    //   928: aload_0
    //   929: getfield 583	com/android/server/InputMethodManagerService:mSwitchingController	Lcom/android/internal/inputmethod/InputMethodSubtypeSwitchingController;
    //   932: aload 6
    //   934: invokevirtual 1730	com/android/internal/inputmethod/InputMethodSubtypeSwitchingController:dump	(Landroid/util/Printer;)V
    //   937: aload 6
    //   939: ldc_w 1732
    //   942: invokeinterface 1646 2 0
    //   947: aload_0
    //   948: getfield 569	com/android/server/InputMethodManagerService:mSettings	Lcom/android/internal/inputmethod/InputMethodUtils$InputMethodSettings;
    //   951: aload 6
    //   953: ldc_w 1654
    //   956: invokevirtual 1735	com/android/internal/inputmethod/InputMethodUtils$InputMethodSettings:dumpLocked	(Landroid/util/Printer;Ljava/lang/String;)V
    //   959: aload 7
    //   961: monitorexit
    //   962: aload 6
    //   964: ldc_w 1737
    //   967: invokeinterface 1646 2 0
    //   972: aload 9
    //   974: ifnull +164 -> 1138
    //   977: aload_2
    //   978: invokevirtual 1740	java/io/PrintWriter:flush	()V
    //   981: aload 9
    //   983: getfield 858	com/android/server/InputMethodManagerService$ClientState:client	Lcom/android/internal/view/IInputMethodClient;
    //   986: invokeinterface 1382 1 0
    //   991: aload_1
    //   992: aload_3
    //   993: invokeinterface 1745 3 0
    //   998: aload 10
    //   1000: ifnull +71 -> 1071
    //   1003: aload 9
    //   1005: aload 10
    //   1007: if_acmpeq +64 -> 1071
    //   1010: aload 6
    //   1012: ldc_w 1737
    //   1015: invokeinterface 1646 2 0
    //   1020: aload 6
    //   1022: ldc_w 1747
    //   1025: invokeinterface 1646 2 0
    //   1030: aload 6
    //   1032: ldc_w 1749
    //   1035: invokeinterface 1646 2 0
    //   1040: aload 6
    //   1042: ldc_w 1737
    //   1045: invokeinterface 1646 2 0
    //   1050: aload_2
    //   1051: invokevirtual 1740	java/io/PrintWriter:flush	()V
    //   1054: aload 10
    //   1056: getfield 858	com/android/server/InputMethodManagerService$ClientState:client	Lcom/android/internal/view/IInputMethodClient;
    //   1059: invokeinterface 1382 1 0
    //   1064: aload_1
    //   1065: aload_3
    //   1066: invokeinterface 1745 3 0
    //   1071: aload 6
    //   1073: ldc_w 1737
    //   1076: invokeinterface 1646 2 0
    //   1081: aload 8
    //   1083: ifnull +130 -> 1213
    //   1086: aload_2
    //   1087: invokevirtual 1740	java/io/PrintWriter:flush	()V
    //   1090: aload 8
    //   1092: invokeinterface 1752 1 0
    //   1097: aload_1
    //   1098: aload_3
    //   1099: invokeinterface 1745 3 0
    //   1104: return
    //   1105: astore 7
    //   1107: aload 6
    //   1109: new 631	java/lang/StringBuilder
    //   1112: dup
    //   1113: invokespecial 632	java/lang/StringBuilder:<init>	()V
    //   1116: ldc_w 1754
    //   1119: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1122: aload 7
    //   1124: invokevirtual 641	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1127: invokevirtual 646	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1130: invokeinterface 1646 2 0
    //   1135: goto -137 -> 998
    //   1138: aload 6
    //   1140: ldc_w 1756
    //   1143: invokeinterface 1646 2 0
    //   1148: goto -150 -> 998
    //   1151: astore 7
    //   1153: aload 6
    //   1155: new 631	java/lang/StringBuilder
    //   1158: dup
    //   1159: invokespecial 632	java/lang/StringBuilder:<init>	()V
    //   1162: ldc_w 1758
    //   1165: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1168: aload 7
    //   1170: invokevirtual 641	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1173: invokevirtual 646	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1176: invokeinterface 1646 2 0
    //   1181: goto -110 -> 1071
    //   1184: astore_1
    //   1185: aload 6
    //   1187: new 631	java/lang/StringBuilder
    //   1190: dup
    //   1191: invokespecial 632	java/lang/StringBuilder:<init>	()V
    //   1194: ldc_w 1760
    //   1197: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1200: aload_1
    //   1201: invokevirtual 641	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1204: invokevirtual 646	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1207: invokeinterface 1646 2 0
    //   1212: return
    //   1213: aload 6
    //   1215: ldc_w 1762
    //   1218: invokeinterface 1646 2 0
    //   1223: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1224	0	this	InputMethodManagerService
    //   0	1224	1	paramFileDescriptor	java.io.FileDescriptor
    //   0	1224	2	paramPrintWriter	java.io.PrintWriter
    //   0	1224	3	paramArrayOfString	String[]
    //   101	72	4	i	int
    //   88	20	5	j	int
    //   60	1154	6	localPrintWriterPrinter	android.util.PrintWriterPrinter
    //   66	894	7	localHashMap	HashMap
    //   1105	18	7	localRemoteException1	RemoteException
    //   1151	18	7	localRemoteException2	RemoteException
    //   122	969	8	localObject	Object
    //   221	783	9	localClientState1	ClientState
    //   501	554	10	localClientState2	ClientState
    // Exception table:
    //   from	to	target	type
    //   71	100	384	finally
    //   110	168	384	finally
    //   177	201	384	finally
    //   201	381	384	finally
    //   390	959	384	finally
    //   981	998	1105	android/os/RemoteException
    //   1054	1071	1151	android/os/RemoteException
    //   1090	1104	1184	android/os/RemoteException
  }
  
  void executeOrSendMessage(IInterface paramIInterface, Message paramMessage)
  {
    if ((paramIInterface.asBinder() instanceof Binder))
    {
      this.mCaller.sendMessage(paramMessage);
      return;
    }
    handleMessage(paramMessage);
    paramMessage.recycle();
  }
  
  public void finishInput(IInputMethodClient paramIInputMethodClient) {}
  
  public InputMethodSubtype getCurrentInputMethodSubtype()
  {
    if (!calledFromValidUser()) {
      return null;
    }
    synchronized (this.mMethodMap)
    {
      InputMethodSubtype localInputMethodSubtype = getCurrentInputMethodSubtypeLocked();
      return localInputMethodSubtype;
    }
  }
  
  public List<InputMethodInfo> getEnabledInputMethodList()
  {
    if (!calledFromValidUser()) {
      return Collections.emptyList();
    }
    synchronized (this.mMethodMap)
    {
      ArrayList localArrayList = this.mSettings.getEnabledInputMethodListLocked();
      return localArrayList;
    }
  }
  
  public List<InputMethodSubtype> getEnabledInputMethodSubtypeList(String paramString, boolean paramBoolean)
  {
    if (!calledFromValidUser()) {
      return Collections.emptyList();
    }
    HashMap localHashMap = this.mMethodMap;
    if (paramString == null) {}
    try
    {
      if (this.mCurMethodId != null) {}
      for (paramString = (InputMethodInfo)this.mMethodMap.get(this.mCurMethodId); paramString == null; paramString = (InputMethodInfo)this.mMethodMap.get(paramString))
      {
        paramString = Collections.emptyList();
        return paramString;
      }
      paramString = this.mSettings.getEnabledInputMethodSubtypeListLocked(this.mContext, paramString, paramBoolean);
      return paramString;
    }
    finally {}
  }
  
  public List<InputMethodInfo> getInputMethodList()
  {
    if (!calledFromValidUser()) {
      return Collections.emptyList();
    }
    synchronized (this.mMethodMap)
    {
      ArrayList localArrayList = new ArrayList(this.mMethodList);
      return localArrayList;
    }
  }
  
  public int getInputMethodWindowVisibleHeight()
  {
    return this.mWindowManagerInternal.getInputMethodWindowVisibleHeight();
  }
  
  public InputMethodSubtype getLastInputMethodSubtype()
  {
    if (!calledFromValidUser()) {
      return null;
    }
    synchronized (this.mMethodMap)
    {
      Object localObject1 = this.mSettings.getLastInputMethodAndSubtypeLocked();
      if ((localObject1 != null) && (!TextUtils.isEmpty((CharSequence)((Pair)localObject1).first)))
      {
        boolean bool = TextUtils.isEmpty((CharSequence)((Pair)localObject1).second);
        if (!bool) {}
      }
      else
      {
        return null;
      }
      InputMethodInfo localInputMethodInfo = (InputMethodInfo)this.mMethodMap.get(((Pair)localObject1).first);
      if (localInputMethodInfo == null) {
        return null;
      }
      try
      {
        int i = InputMethodUtils.getSubtypeIdFromHashCode(localInputMethodInfo, Integer.parseInt((String)((Pair)localObject1).second));
        if (i >= 0)
        {
          int j = localInputMethodInfo.getSubtypeCount();
          if (i < j) {}
        }
        else
        {
          return null;
        }
        localObject1 = localInputMethodInfo.getSubtypeAt(i);
        return (InputMethodSubtype)localObject1;
      }
      catch (NumberFormatException localNumberFormatException)
      {
        return null;
      }
    }
  }
  
  public List getShortcutInputMethodsAndSubtypes()
  {
    synchronized (this.mMethodMap)
    {
      ArrayList localArrayList = new ArrayList();
      if (this.mShortcutInputMethodsAndSubtypes.size() == 0)
      {
        localObject1 = findLastResortApplicableShortcutInputMethodAndSubtypeLocked("voice");
        if (localObject1 != null)
        {
          localArrayList.add(((Pair)localObject1).first);
          localArrayList.add(((Pair)localObject1).second);
        }
        return localArrayList;
      }
      Object localObject1 = this.mShortcutInputMethodsAndSubtypes.keySet().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        Object localObject2 = (InputMethodInfo)((Iterator)localObject1).next();
        localArrayList.add(localObject2);
        localObject2 = ((ArrayList)this.mShortcutInputMethodsAndSubtypes.get(localObject2)).iterator();
        if (((Iterator)localObject2).hasNext()) {
          localArrayList.add((InputMethodSubtype)((Iterator)localObject2).next());
        }
      }
    }
    return localList;
  }
  
  public boolean handleMessage(Message arg1)
  {
    boolean bool;
    switch (???.what)
    {
    default: 
      return false;
    case 1: 
      switch (???.arg1)
      {
      default: 
        Slog.e("InputMethodManagerService", "Unknown subtype picker mode = " + ???.arg1);
        return false;
      case 0: 
        bool = this.mInputShown;
      }
      for (;;)
      {
        showInputMethodMenu(bool);
        return true;
        bool = true;
        continue;
        bool = false;
      }
    case 2: 
      showInputMethodAndSubtypeEnabler((String)???.obj);
      return true;
    case 3: 
      showConfigureInputMethods();
      return true;
    }
    try
    {
      ((IInputMethod)???.obj).unbindInput();
      return true;
      ??? = (SomeArgs)???.obj;
      try
      {
        ((IInputMethod)???.arg1).bindInput((InputBinding)???.arg2);
        ???.recycle();
        return true;
        SomeArgs localSomeArgs1 = (SomeArgs)???.obj;
        try
        {
          ((IInputMethod)localSomeArgs1.arg1).showSoftInput(???.arg1, (ResultReceiver)localSomeArgs1.arg2);
          localSomeArgs1.recycle();
          return true;
          ??? = (SomeArgs)???.obj;
          try
          {
            ((IInputMethod)???.arg1).hideSoftInput(0, (ResultReceiver)???.arg2);
            ???.recycle();
            return true;
            synchronized (this.mMethodMap)
            {
              hideCurrentInputLocked(0, null);
              return true;
            }
            ??? = (SomeArgs)???.obj;
            try
            {
              ((IInputMethod)???.arg1).attachToken((IBinder)???.arg2);
              ???.recycle();
              return true;
              SomeArgs localSomeArgs2 = (SomeArgs)???.obj;
              ??? = (IInputMethod)localSomeArgs2.arg1;
              Object localObject2 = (InputChannel)localSomeArgs2.arg2;
              try
              {
                ???.createSession((InputChannel)localObject2, (IInputSessionCallback)localSomeArgs2.arg3);
                if ((localObject2 != null) && (Binder.isProxy(???))) {
                  ((InputChannel)localObject2).dispose();
                }
              }
              catch (RemoteException localRemoteException8)
              {
                for (;;)
                {
                  localRemoteException8 = localRemoteException8;
                  if ((localObject2 != null) && (Binder.isProxy(???))) {
                    ((InputChannel)localObject2).dispose();
                  }
                }
              }
              finally
              {
                localObject3 = finally;
                if ((localObject2 == null) || (!Binder.isProxy(???))) {
                  break label569;
                }
                ((InputChannel)localObject2).dispose();
                throw ((Throwable)localObject3);
              }
              localSomeArgs2.recycle();
              return true;
              label569:
              int i = ???.arg1;
              ??? = (SomeArgs)???.obj;
              try
              {
                localObject2 = (SessionState)???.arg1;
                setEnabledSessionInMainThread((SessionState)localObject2);
                ((SessionState)localObject2).method.startInput((IInputContext)???.arg2, i, (EditorInfo)???.arg3);
                ???.recycle();
                return true;
                i = ???.arg1;
                ??? = (SomeArgs)???.obj;
                try
                {
                  localObject2 = (SessionState)???.arg1;
                  setEnabledSessionInMainThread((SessionState)localObject2);
                  ((SessionState)localObject2).method.restartInput((IInputContext)???.arg2, i, (EditorInfo)???.arg3);
                  ???.recycle();
                  return true;
                  try
                  {
                    ((IInputMethodClient)???.obj).onUnbindMethod(???.arg1, ???.arg2);
                    return true;
                    SomeArgs localSomeArgs3 = (SomeArgs)???.obj;
                    ??? = (IInputMethodClient)localSomeArgs3.arg1;
                    localObject2 = (InputBindResult)localSomeArgs3.arg2;
                    try
                    {
                      ???.onBindMethod((InputBindResult)localObject2);
                    }
                    catch (RemoteException localRemoteException9)
                    {
                      for (;;)
                      {
                        Slog.w("InputMethodManagerService", "Client died receiving input method " + localSomeArgs3.arg2);
                        if ((((InputBindResult)localObject2).channel != null) && (Binder.isProxy(???))) {
                          ((InputBindResult)localObject2).channel.dispose();
                        }
                      }
                    }
                    finally
                    {
                      if ((((InputBindResult)localObject2).channel == null) || (!Binder.isProxy(???))) {
                        break label861;
                      }
                      ((InputBindResult)localObject2).channel.dispose();
                    }
                    localSomeArgs3.recycle();
                    return true;
                    for (;;)
                    {
                      try
                      {
                        label861:
                        localObject2 = ((ClientState)???.obj).client;
                        if (???.arg1 == 0) {
                          continue;
                        }
                        bool = true;
                        ((IInputMethodClient)localObject2).setActive(bool);
                      }
                      catch (RemoteException localRemoteException1)
                      {
                        Slog.w("InputMethodManagerService", "Got RemoteException sending setActive(false) notification to pid " + ((ClientState)???.obj).pid + " uid " + ((ClientState)???.obj).uid);
                        continue;
                      }
                      return true;
                      bool = false;
                    }
                    if (???.arg1 != 0) {}
                    for (bool = true;; bool = false)
                    {
                      handleSetInteractive(bool);
                      return true;
                    }
                    if (???.arg1 != 0) {}
                    for (bool = true;; bool = false)
                    {
                      handleSwitchInputMethod(bool);
                      return true;
                    }
                    i = ???.arg1;
                    ??? = (ClientState)???.obj;
                    try
                    {
                      ???.client.setUserActionNotificationSequenceNumber(i);
                      return true;
                    }
                    catch (RemoteException localRemoteException2)
                    {
                      for (;;)
                      {
                        Slog.w("InputMethodManagerService", "Got RemoteException sending setUserActionNotificationSequenceNumber(" + i + ") notification to pid " + ???.pid + " uid " + ???.uid);
                      }
                    }
                    HardKeyboardListener localHardKeyboardListener = this.mHardKeyboardListener;
                    if (???.arg1 == 1) {}
                    for (bool = true;; bool = false)
                    {
                      localHardKeyboardListener.handleHardKeyboardStatusChange(bool);
                      return true;
                    }
                    onUnlockUser(???.arg1);
                    return true;
                  }
                  catch (RemoteException ???)
                  {
                    for (;;) {}
                  }
                }
                catch (RemoteException localRemoteException3)
                {
                  for (;;) {}
                }
              }
              catch (RemoteException localRemoteException4)
              {
                for (;;) {}
              }
            }
            catch (RemoteException localRemoteException5)
            {
              for (;;) {}
            }
          }
          catch (RemoteException localRemoteException6)
          {
            for (;;) {}
          }
        }
        catch (RemoteException ???)
        {
          for (;;) {}
        }
      }
      catch (RemoteException localRemoteException7)
      {
        for (;;) {}
      }
    }
    catch (RemoteException ???)
    {
      for (;;) {}
    }
  }
  
  boolean hideCurrentInputLocked(int paramInt, ResultReceiver paramResultReceiver)
  {
    int i = 1;
    if (((paramInt & 0x1) != 0) && ((this.mShowExplicitlyRequested) || (this.mShowForced))) {
      return false;
    }
    if ((this.mShowForced) && ((paramInt & 0x2) != 0)) {
      return false;
    }
    if (this.mCurMethod != null)
    {
      paramInt = i;
      if (!this.mInputShown)
      {
        if ((this.mImeWindowVis & 0x1) != 0) {
          paramInt = i;
        }
      }
      else
      {
        if (paramInt == 0) {
          break label159;
        }
        executeOrSendMessage(this.mCurMethod, this.mCaller.obtainMessageOO(1030, this.mCurMethod, paramResultReceiver));
      }
    }
    label159:
    for (boolean bool = true;; bool = false)
    {
      if ((this.mHaveConnection) && (this.mVisibleBound))
      {
        this.mContext.unbindService(this.mVisibleConnection);
        this.mVisibleBound = false;
      }
      this.mInputShown = false;
      this.mShowRequested = false;
      this.mShowExplicitlyRequested = false;
      this.mShowForced = false;
      return bool;
      paramInt = 0;
      break;
      paramInt = 0;
      break;
    }
  }
  
  void hideInputMethodMenu()
  {
    synchronized (this.mMethodMap)
    {
      hideInputMethodMenuLocked();
      return;
    }
  }
  
  void hideInputMethodMenuLocked()
  {
    if (this.mSwitchingDialog != null)
    {
      this.mSwitchingDialog.dismiss();
      this.mSwitchingDialog = null;
    }
    updateSystemUiLocked(this.mCurToken, this.mImeWindowVis, this.mBackDisposition);
    this.mDialogBuilder = null;
    this.mIms = null;
  }
  
  public void hideMySoftInput(IBinder paramIBinder, int paramInt)
  {
    if (!calledFromValidUser()) {
      return;
    }
    synchronized (this.mMethodMap)
    {
      if (!calledWithValidToken(paramIBinder))
      {
        paramInt = Binder.getCallingUid();
        Slog.e("InputMethodManagerService", "Ignoring hideInputMethod due to an invalid token. uid:" + paramInt + " token:" + paramIBinder);
        return;
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        hideCurrentInputLocked(paramInt, null);
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
  
  /* Error */
  public boolean hideSoftInput(IInputMethodClient paramIInputMethodClient, int paramInt, ResultReceiver paramResultReceiver)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 1300	com/android/server/InputMethodManagerService:calledFromValidUser	()Z
    //   4: ifne +5 -> 9
    //   7: iconst_0
    //   8: ireturn
    //   9: invokestatic 677	android/os/Binder:getCallingUid	()I
    //   12: pop
    //   13: invokestatic 1081	android/os/Binder:clearCallingIdentity	()J
    //   16: lstore 4
    //   18: aload_0
    //   19: getfield 313	com/android/server/InputMethodManagerService:mMethodMap	Ljava/util/HashMap;
    //   22: astore 7
    //   24: aload 7
    //   26: monitorenter
    //   27: aload_0
    //   28: getfield 854	com/android/server/InputMethodManagerService:mCurClient	Lcom/android/server/InputMethodManagerService$ClientState;
    //   31: astore 8
    //   33: aload 8
    //   35: ifnull +7 -> 42
    //   38: aload_1
    //   39: ifnonnull +30 -> 69
    //   42: aload_0
    //   43: getfield 383	com/android/server/InputMethodManagerService:mIWindowManager	Landroid/view/IWindowManager;
    //   46: aload_1
    //   47: invokeinterface 1391 2 0
    //   52: istore 6
    //   54: iload 6
    //   56: ifne +34 -> 90
    //   59: aload 7
    //   61: monitorexit
    //   62: lload 4
    //   64: invokestatic 1085	android/os/Binder:restoreCallingIdentity	(J)V
    //   67: iconst_0
    //   68: ireturn
    //   69: aload_0
    //   70: getfield 854	com/android/server/InputMethodManagerService:mCurClient	Lcom/android/server/InputMethodManagerService$ClientState;
    //   73: getfield 858	com/android/server/InputMethodManagerService$ClientState:client	Lcom/android/internal/view/IInputMethodClient;
    //   76: invokeinterface 1382 1 0
    //   81: aload_1
    //   82: invokeinterface 1382 1 0
    //   87: if_acmpne -45 -> 42
    //   90: aload_0
    //   91: iload_2
    //   92: aload_3
    //   93: invokevirtual 963	com/android/server/InputMethodManagerService:hideCurrentInputLocked	(ILandroid/os/ResultReceiver;)Z
    //   96: istore 6
    //   98: aload 7
    //   100: monitorexit
    //   101: lload 4
    //   103: invokestatic 1085	android/os/Binder:restoreCallingIdentity	(J)V
    //   106: iload 6
    //   108: ireturn
    //   109: astore_1
    //   110: aload 7
    //   112: monitorexit
    //   113: lload 4
    //   115: invokestatic 1085	android/os/Binder:restoreCallingIdentity	(J)V
    //   118: iconst_0
    //   119: ireturn
    //   120: astore_1
    //   121: aload 7
    //   123: monitorexit
    //   124: aload_1
    //   125: athrow
    //   126: astore_1
    //   127: lload 4
    //   129: invokestatic 1085	android/os/Binder:restoreCallingIdentity	(J)V
    //   132: aload_1
    //   133: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	134	0	this	InputMethodManagerService
    //   0	134	1	paramIInputMethodClient	IInputMethodClient
    //   0	134	2	paramInt	int
    //   0	134	3	paramResultReceiver	ResultReceiver
    //   16	112	4	l	long
    //   52	55	6	bool	boolean
    //   31	3	8	localClientState	ClientState
    // Exception table:
    //   from	to	target	type
    //   42	54	109	android/os/RemoteException
    //   27	33	120	finally
    //   42	54	120	finally
    //   69	90	120	finally
    //   90	98	120	finally
    //   18	27	126	finally
    //   59	62	126	finally
    //   98	101	126	finally
    //   110	113	126	finally
    //   121	126	126	finally
  }
  
  /* Error */
  public boolean hideSoftInputForLongshot(int paramInt, ResultReceiver paramResultReceiver)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 1300	com/android/server/InputMethodManagerService:calledFromValidUser	()Z
    //   4: ifne +5 -> 9
    //   7: iconst_0
    //   8: ireturn
    //   9: invokestatic 677	android/os/Binder:getCallingUid	()I
    //   12: istore_3
    //   13: invokestatic 1081	android/os/Binder:clearCallingIdentity	()J
    //   16: lstore 4
    //   18: aload_0
    //   19: getfield 313	com/android/server/InputMethodManagerService:mMethodMap	Ljava/util/HashMap;
    //   22: astore 7
    //   24: aload 7
    //   26: monitorenter
    //   27: ldc 111
    //   29: new 631	java/lang/StringBuilder
    //   32: dup
    //   33: invokespecial 632	java/lang/StringBuilder:<init>	()V
    //   36: ldc_w 1942
    //   39: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   42: iload_3
    //   43: invokevirtual 1075	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   46: invokevirtual 646	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   49: invokestatic 1945	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   52: pop
    //   53: aload_0
    //   54: iload_1
    //   55: aload_2
    //   56: invokevirtual 963	com/android/server/InputMethodManagerService:hideCurrentInputLocked	(ILandroid/os/ResultReceiver;)Z
    //   59: istore 6
    //   61: aload 7
    //   63: monitorexit
    //   64: lload 4
    //   66: invokestatic 1085	android/os/Binder:restoreCallingIdentity	(J)V
    //   69: iload 6
    //   71: ireturn
    //   72: astore_2
    //   73: aload 7
    //   75: monitorexit
    //   76: aload_2
    //   77: athrow
    //   78: astore_2
    //   79: lload 4
    //   81: invokestatic 1085	android/os/Binder:restoreCallingIdentity	(J)V
    //   84: aload_2
    //   85: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	86	0	this	InputMethodManagerService
    //   0	86	1	paramInt	int
    //   0	86	2	paramResultReceiver	ResultReceiver
    //   12	31	3	i	int
    //   16	64	4	l	long
    //   59	11	6	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   27	61	72	finally
    //   18	27	78	finally
    //   61	64	78	finally
    //   73	78	78	finally
  }
  
  public boolean notifySuggestionPicked(SuggestionSpan paramSuggestionSpan, String paramString, int paramInt)
  {
    if (!calledFromValidUser()) {
      return false;
    }
    synchronized (this.mMethodMap)
    {
      InputMethodInfo localInputMethodInfo = (InputMethodInfo)this.mSecureSuggestionSpans.get(paramSuggestionSpan);
      if (localInputMethodInfo != null)
      {
        String[] arrayOfString = paramSuggestionSpan.getSuggestions();
        if (paramInt >= 0)
        {
          int i = arrayOfString.length;
          if (paramInt < i) {}
        }
        else
        {
          return false;
        }
        String str = paramSuggestionSpan.getNotificationTargetClassName();
        Intent localIntent = new Intent();
        localIntent.setClassName(localInputMethodInfo.getPackageName(), str);
        localIntent.setAction("android.text.style.SUGGESTION_PICKED");
        localIntent.putExtra("before", paramString);
        localIntent.putExtra("after", arrayOfString[paramInt]);
        localIntent.putExtra("hashcode", paramSuggestionSpan.hashCode());
        long l = Binder.clearCallingIdentity();
        try
        {
          this.mContext.sendBroadcastAsUser(localIntent, UserHandle.CURRENT);
          Binder.restoreCallingIdentity(l);
          return true;
        }
        finally
        {
          paramSuggestionSpan = finally;
          Binder.restoreCallingIdentity(l);
          throw paramSuggestionSpan;
        }
      }
    }
    return false;
  }
  
  public void notifyUserAction(int paramInt)
  {
    synchronized (this.mMethodMap)
    {
      int i = this.mCurUserActionNotificationSequenceNumber;
      if (i != paramInt) {
        return;
      }
      InputMethodInfo localInputMethodInfo = (InputMethodInfo)this.mMethodMap.get(this.mCurMethodId);
      if (localInputMethodInfo != null) {
        this.mSwitchingController.onUserActionLocked(localInputMethodInfo, this.mCurrentSubtype);
      }
      return;
    }
  }
  
  public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
  {
    synchronized (this.mMethodMap)
    {
      if ((this.mCurIntent != null) && (paramComponentName.equals(this.mCurIntent.getComponent())))
      {
        this.mCurMethod = IInputMethod.Stub.asInterface(paramIBinder);
        if (this.mCurToken == null)
        {
          Slog.w("InputMethodManagerService", "Service connected without a token!");
          unbindCurrentMethodLocked(false);
          return;
        }
        executeOrSendMessage(this.mCurMethod, this.mCaller.obtainMessageOO(1040, this.mCurMethod, this.mCurToken));
        if (this.mCurClient != null)
        {
          clearClientSessionLocked(this.mCurClient);
          requestClientSessionLocked(this.mCurClient);
        }
      }
      return;
    }
  }
  
  public void onServiceDisconnected(ComponentName paramComponentName)
  {
    synchronized (this.mMethodMap)
    {
      if ((this.mCurMethod != null) && (this.mCurIntent != null) && (paramComponentName.equals(this.mCurIntent.getComponent())))
      {
        clearCurMethodLocked();
        this.mLastBindTime = SystemClock.uptimeMillis();
        this.mShowRequested = this.mInputShown;
        this.mInputShown = false;
        if (this.mCurClient != null) {
          executeOrSendMessage(this.mCurClient.client, this.mCaller.obtainMessageIIO(3000, 3, this.mCurSeq, this.mCurClient.client));
        }
      }
      return;
    }
  }
  
  void onSessionCreated(IInputMethod paramIInputMethod, IInputMethodSession paramIInputMethodSession, InputChannel paramInputChannel)
  {
    synchronized (this.mMethodMap)
    {
      if ((this.mCurMethod != null) && (paramIInputMethod != null) && (this.mCurMethod.asBinder() == paramIInputMethod.asBinder()) && (this.mCurClient != null))
      {
        clearClientSessionLocked(this.mCurClient);
        this.mCurClient.curSession = new SessionState(this.mCurClient, paramIInputMethod, paramIInputMethodSession, paramInputChannel);
        paramIInputMethod = attachNewInputLocked(true);
        if (paramIInputMethod.method != null) {
          executeOrSendMessage(this.mCurClient.client, this.mCaller.obtainMessageOO(3010, this.mCurClient.client, paramIInputMethod));
        }
        return;
      }
      paramInputChannel.dispose();
      return;
    }
  }
  
  void onSwitchUser(int paramInt)
  {
    synchronized (this.mMethodMap)
    {
      switchUserLocked(paramInt);
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
        Slog.wtf("InputMethodManagerService", "Input Method Manager Crash", paramParcel1);
      }
      throw paramParcel1;
    }
  }
  
  void onUnlockUser(int paramInt)
  {
    boolean bool = false;
    synchronized (this.mMethodMap)
    {
      int i = this.mSettings.getCurrentUserId();
      if (paramInt != i) {
        return;
      }
      InputMethodUtils.InputMethodSettings localInputMethodSettings = this.mSettings;
      if (this.mSystemReady)
      {
        localInputMethodSettings.switchCurrentUser(i, bool);
        buildInputMethodListLocked(false);
        updateInputMethodsFromSettingsLocked(true);
        return;
      }
      bool = true;
    }
  }
  
  public void registerSuggestionSpansForNotification(SuggestionSpan[] paramArrayOfSuggestionSpan)
  {
    if (!calledFromValidUser()) {
      return;
    }
    synchronized (this.mMethodMap)
    {
      InputMethodInfo localInputMethodInfo = (InputMethodInfo)this.mMethodMap.get(this.mCurMethodId);
      int i = 0;
      while (i < paramArrayOfSuggestionSpan.length)
      {
        SuggestionSpan localSuggestionSpan = paramArrayOfSuggestionSpan[i];
        if (!TextUtils.isEmpty(localSuggestionSpan.getNotificationTargetClassName())) {
          this.mSecureSuggestionSpans.put(localSuggestionSpan, localInputMethodInfo);
        }
        i += 1;
      }
      return;
    }
  }
  
  public void removeClient(IInputMethodClient paramIInputMethodClient)
  {
    if (!calledFromValidUser()) {
      return;
    }
    synchronized (this.mMethodMap)
    {
      paramIInputMethodClient = (ClientState)this.mClients.remove(paramIInputMethodClient.asBinder());
      if (paramIInputMethodClient != null)
      {
        clearClientSessionLocked(paramIInputMethodClient);
        if (this.mCurClient == paramIInputMethodClient) {
          this.mCurClient = null;
        }
        if (this.mCurFocusedWindowClient == paramIInputMethodClient) {
          this.mCurFocusedWindowClient = null;
        }
      }
      return;
    }
  }
  
  void requestClientSessionLocked(ClientState paramClientState)
  {
    if (!paramClientState.sessionRequested)
    {
      InputChannel[] arrayOfInputChannel = InputChannel.openInputChannelPair(paramClientState.toString());
      paramClientState.sessionRequested = true;
      executeOrSendMessage(this.mCurMethod, this.mCaller.obtainMessageOOO(1050, this.mCurMethod, arrayOfInputChannel[1], new MethodCallback(this, this.mCurMethod, arrayOfInputChannel[0])));
    }
  }
  
  void resetCurrentMethodAndClient(int paramInt)
  {
    this.mCurMethodId = null;
    unbindCurrentMethodLocked(false);
    unbindCurrentClientLocked(paramInt);
  }
  
  public void setAdditionalInputMethodSubtypes(String paramString, InputMethodSubtype[] paramArrayOfInputMethodSubtype)
  {
    if (!calledFromValidUser()) {
      return;
    }
    if ((TextUtils.isEmpty(paramString)) || (paramArrayOfInputMethodSubtype == null)) {
      return;
    }
    for (;;)
    {
      int i;
      synchronized (this.mMethodMap)
      {
        paramString = (InputMethodInfo)this.mMethodMap.get(paramString);
        if (paramString == null) {
          return;
        }
        try
        {
          String[] arrayOfString = this.mIPackageManager.getPackagesForUid(Binder.getCallingUid());
          if (arrayOfString == null) {
            break;
          }
          int j = arrayOfString.length;
          i = 0;
          if (i >= j) {
            break;
          }
          if (arrayOfString[i].equals(paramString.getPackageName()))
          {
            this.mFileManager.addInputMethodSubtypes(paramString, paramArrayOfInputMethodSubtype);
            long l = Binder.clearCallingIdentity();
            paramString = finally;
          }
        }
        catch (RemoteException paramString)
        {
          try
          {
            buildInputMethodListLocked(false);
            Binder.restoreCallingIdentity(l);
            return;
          }
          finally
          {
            paramString = finally;
            Binder.restoreCallingIdentity(l);
            throw paramString;
          }
          paramString = paramString;
          Slog.e("InputMethodManagerService", "Failed to get package infos");
          return;
        }
      }
      i += 1;
    }
  }
  
  public boolean setCurrentInputMethodSubtype(InputMethodSubtype paramInputMethodSubtype)
  {
    if (!calledFromValidUser()) {
      return false;
    }
    HashMap localHashMap = this.mMethodMap;
    if (paramInputMethodSubtype != null) {}
    try
    {
      if (this.mCurMethodId != null)
      {
        int i = InputMethodUtils.getSubtypeIdFromHashCode((InputMethodInfo)this.mMethodMap.get(this.mCurMethodId), paramInputMethodSubtype.hashCode());
        if (i != -1)
        {
          setInputMethodLocked(this.mCurMethodId, i);
          return true;
        }
      }
      return false;
    }
    finally {}
  }
  
  void setEnabledSessionInMainThread(SessionState paramSessionState)
  {
    if ((this.mEnabledSession == paramSessionState) || ((this.mEnabledSession != null) && (this.mEnabledSession.session != null))) {}
    try
    {
      this.mEnabledSession.method.setSessionEnabled(this.mEnabledSession.session, false);
      this.mEnabledSession = paramSessionState;
      if ((this.mEnabledSession != null) && (this.mEnabledSession.session != null)) {}
      try
      {
        this.mEnabledSession.method.setSessionEnabled(this.mEnabledSession.session, true);
        return;
      }
      catch (RemoteException paramSessionState) {}
    }
    catch (RemoteException localRemoteException)
    {
      for (;;) {}
    }
  }
  
  public void setImeWindowStatus(IBinder paramIBinder, int paramInt1, int paramInt2)
  {
    if (!calledWithValidToken(paramIBinder))
    {
      paramInt1 = Binder.getCallingUid();
      Slog.e("InputMethodManagerService", "Ignoring setImeWindowStatus due to an invalid token. uid:" + paramInt1 + " token:" + paramIBinder);
      return;
    }
    synchronized (this.mMethodMap)
    {
      this.mImeWindowVis = paramInt1;
      this.mBackDisposition = paramInt2;
      updateSystemUiLocked(paramIBinder, paramInt1, paramInt2);
      return;
    }
  }
  
  public void setInputMethod(IBinder paramIBinder, String paramString)
  {
    if (!calledFromValidUser()) {
      return;
    }
    setInputMethodWithSubtypeId(paramIBinder, paramString, -1);
  }
  
  public void setInputMethodAndSubtype(IBinder paramIBinder, String paramString, InputMethodSubtype paramInputMethodSubtype)
  {
    if (!calledFromValidUser()) {
      return;
    }
    localHashMap = this.mMethodMap;
    if (paramInputMethodSubtype != null) {}
    for (;;)
    {
      try
      {
        setInputMethodWithSubtypeIdLocked(paramIBinder, paramString, InputMethodUtils.getSubtypeIdFromHashCode((InputMethodInfo)this.mMethodMap.get(paramString), paramInputMethodSubtype.hashCode()));
        return;
      }
      finally {}
      setInputMethod(paramIBinder, paramString);
    }
  }
  
  public boolean setInputMethodEnabled(String paramString, boolean paramBoolean)
  {
    if (!calledFromValidUser()) {
      return false;
    }
    synchronized (this.mMethodMap)
    {
      if (this.mContext.checkCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS") != 0) {
        throw new SecurityException("Requires permission android.permission.WRITE_SECURE_SETTINGS");
      }
    }
    long l = Binder.clearCallingIdentity();
    try
    {
      paramBoolean = setInputMethodEnabledLocked(paramString, paramBoolean);
      Binder.restoreCallingIdentity(l);
      return paramBoolean;
    }
    finally
    {
      paramString = finally;
      Binder.restoreCallingIdentity(l);
      throw paramString;
    }
  }
  
  boolean setInputMethodEnabledLocked(String paramString, boolean paramBoolean)
  {
    if ((InputMethodInfo)this.mMethodMap.get(paramString) == null) {
      throw new IllegalArgumentException("Unknown id: " + this.mCurMethodId);
    }
    Object localObject = this.mSettings.getEnabledInputMethodsAndSubtypeListLocked();
    if (paramBoolean)
    {
      localObject = ((Iterable)localObject).iterator();
      while (((Iterator)localObject).hasNext()) {
        if (((String)((Pair)((Iterator)localObject).next()).first).equals(paramString)) {
          return true;
        }
      }
      this.mSettings.appendAndPutEnabledInputMethodLocked(paramString, false);
      return false;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    if (this.mSettings.buildAndPutEnabledInputMethodsStrRemovingIdLocked(localStringBuilder, (List)localObject, paramString))
    {
      if ((!paramString.equals(this.mSettings.getSelectedInputMethod())) || (chooseNewDefaultIMELocked())) {
        return true;
      }
      Slog.i("InputMethodManagerService", "Can't find new IME, unsetting the current input method.");
      resetSelectedInputMethodAndSubtypeLocked("");
      return true;
    }
    return false;
  }
  
  void setInputMethodLocked(String paramString, int paramInt)
  {
    InputMethodInfo localInputMethodInfo = (InputMethodInfo)this.mMethodMap.get(paramString);
    if (localInputMethodInfo == null) {
      throw new IllegalArgumentException("Unknown id: " + paramString);
    }
    if ((this.mCurMethodId != null) && (paramString.equals(this.mCurMethodId))) {}
    Object localObject;
    while (paramString.equals(this.mCurMethodId))
    {
      int i = localInputMethodInfo.getSubtypeCount();
      if (i <= 0)
      {
        return;
        OnePlusProcessManager.setCurrentInputMethod(localInputMethodInfo.getServiceInfo());
      }
      else
      {
        localObject = this.mCurrentSubtype;
        if ((paramInt >= 0) && (paramInt < i)) {}
        for (paramString = localInputMethodInfo.getSubtypeAt(paramInt); (paramString == null) || (localObject == null); paramString = getCurrentInputMethodSubtypeLocked())
        {
          Slog.w("InputMethodManagerService", "Illegal subtype state: old subtype = " + localObject + ", new subtype = " + paramString);
          return;
        }
        if (paramString != localObject)
        {
          setSelectedInputMethodAndSubtypeLocked(localInputMethodInfo, paramInt, true);
          if (this.mCurMethod == null) {}
        }
        try
        {
          updateSystemUiLocked(this.mCurToken, this.mImeWindowVis, this.mBackDisposition);
          this.mCurMethod.changeInputMethodSubtype(paramString);
          notifyInputMethodSubtypeChanged(this.mSettings.getCurrentUserId(), localInputMethodInfo, paramString);
          return;
        }
        catch (RemoteException paramString)
        {
          Slog.w("InputMethodManagerService", "Failed to call changeInputMethodSubtype");
          return;
        }
      }
    }
    long l = Binder.clearCallingIdentity();
    try
    {
      setSelectedInputMethodAndSubtypeLocked(localInputMethodInfo, paramInt, false);
      this.mCurMethodId = paramString;
      if (ActivityManagerNative.isSystemReady())
      {
        localObject = new Intent("android.intent.action.INPUT_METHOD_CHANGED");
        ((Intent)localObject).addFlags(536870912);
        ((Intent)localObject).putExtra("input_method_id", paramString);
        this.mContext.sendBroadcastAsUser((Intent)localObject, UserHandle.CURRENT);
      }
      unbindCurrentClientLocked(2);
      Binder.restoreCallingIdentity(l);
      notifyInputMethodSubtypeChanged(this.mSettings.getCurrentUserId(), localInputMethodInfo, getCurrentInputMethodSubtypeLocked());
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public boolean shouldOfferSwitchingToNextInputMethod(IBinder paramIBinder)
  {
    if (!calledFromValidUser()) {
      return false;
    }
    synchronized (this.mMethodMap)
    {
      if (!calledWithValidToken(paramIBinder))
      {
        int i = Binder.getCallingUid();
        Slog.e("InputMethodManagerService", "Ignoring shouldOfferSwitchingToNextInputMethod due to an invalid token. uid:" + i + " token:" + paramIBinder);
        return false;
      }
      paramIBinder = this.mSwitchingController.getNextInputMethodLocked(false, (InputMethodInfo)this.mMethodMap.get(this.mCurMethodId), this.mCurrentSubtype, true);
      return paramIBinder != null;
    }
  }
  
  boolean showCurrentInputLocked(int paramInt, ResultReceiver paramResultReceiver)
  {
    this.mShowRequested = true;
    if (this.mAccessibilityRequestingNoSoftKeyboard) {
      return false;
    }
    if ((paramInt & 0x2) != 0)
    {
      this.mShowExplicitlyRequested = true;
      this.mShowForced = true;
    }
    while (!this.mSystemReady)
    {
      return false;
      if ((paramInt & 0x1) == 0) {
        this.mShowExplicitlyRequested = true;
      }
    }
    boolean bool2 = false;
    boolean bool1;
    if (this.mCurMethod != null)
    {
      executeOrSendMessage(this.mCurMethod, this.mCaller.obtainMessageIOO(1020, getImeShowFlags(), this.mCurMethod, paramResultReceiver));
      this.mInputShown = true;
      if ((!this.mHaveConnection) || (this.mVisibleBound)) {
        bool1 = true;
      }
    }
    do
    {
      do
      {
        return bool1;
        bindCurrentInputMethodService(this.mCurIntent, this.mVisibleConnection, 201326593);
        this.mVisibleBound = true;
        break;
        bool1 = bool2;
      } while (!this.mHaveConnection);
      bool1 = bool2;
    } while (SystemClock.uptimeMillis() < this.mLastBindTime + 3000L);
    EventLog.writeEvent(32000, new Object[] { this.mCurMethodId, Long.valueOf(SystemClock.uptimeMillis() - this.mLastBindTime), Integer.valueOf(1) });
    Slog.w("InputMethodManagerService", "Force disconnect/connect to the IME in showCurrentInputLocked()");
    this.mContext.unbindService(this);
    bindCurrentInputMethodService(this.mCurIntent, this, 1073741825);
    return false;
  }
  
  public void showInputMethodAndSubtypeEnablerFromClient(IInputMethodClient arg1, String paramString)
  {
    if (!calledFromValidUser()) {
      return;
    }
    synchronized (this.mMethodMap)
    {
      executeOrSendMessage(this.mCurMethod, this.mCaller.obtainMessageO(2, paramString));
      return;
    }
  }
  
  public void showInputMethodPickerFromClient(IInputMethodClient paramIInputMethodClient, int paramInt)
  {
    if (!calledFromValidUser()) {
      return;
    }
    synchronized (this.mMethodMap)
    {
      if ((this.mCurClient == null) || (paramIInputMethodClient == null)) {
        Slog.w("InputMethodManagerService", "Ignoring showInputMethodPickerFromClient of uid " + Binder.getCallingUid() + ": " + paramIInputMethodClient);
      }
      IBinder localIBinder1;
      IBinder localIBinder2;
      do
      {
        this.mHandler.sendMessage(this.mCaller.obtainMessageI(1, paramInt));
        return;
        localIBinder1 = this.mCurClient.client.asBinder();
        localIBinder2 = paramIInputMethodClient.asBinder();
      } while (localIBinder1 == localIBinder2);
    }
  }
  
  public void showMySoftInput(IBinder paramIBinder, int paramInt)
  {
    if (!calledFromValidUser()) {
      return;
    }
    synchronized (this.mMethodMap)
    {
      if (!calledWithValidToken(paramIBinder))
      {
        paramInt = Binder.getCallingUid();
        Slog.e("InputMethodManagerService", "Ignoring showMySoftInput due to an invalid token. uid:" + paramInt + " token:" + paramIBinder);
        return;
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        showCurrentInputLocked(paramInt, null);
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
  
  /* Error */
  public boolean showSoftInput(IInputMethodClient paramIInputMethodClient, int paramInt, ResultReceiver paramResultReceiver)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 1300	com/android/server/InputMethodManagerService:calledFromValidUser	()Z
    //   4: ifne +5 -> 9
    //   7: iconst_0
    //   8: ireturn
    //   9: invokestatic 677	android/os/Binder:getCallingUid	()I
    //   12: istore 4
    //   14: invokestatic 1081	android/os/Binder:clearCallingIdentity	()J
    //   17: lstore 5
    //   19: aload_0
    //   20: getfield 313	com/android/server/InputMethodManagerService:mMethodMap	Ljava/util/HashMap;
    //   23: astore 8
    //   25: aload 8
    //   27: monitorenter
    //   28: aload_0
    //   29: getfield 854	com/android/server/InputMethodManagerService:mCurClient	Lcom/android/server/InputMethodManagerService$ClientState;
    //   32: astore 9
    //   34: aload 9
    //   36: ifnull +7 -> 43
    //   39: aload_1
    //   40: ifnonnull +63 -> 103
    //   43: aload_0
    //   44: getfield 383	com/android/server/InputMethodManagerService:mIWindowManager	Landroid/view/IWindowManager;
    //   47: aload_1
    //   48: invokeinterface 1391 2 0
    //   53: ifne +71 -> 124
    //   56: ldc 111
    //   58: new 631	java/lang/StringBuilder
    //   61: dup
    //   62: invokespecial 632	java/lang/StringBuilder:<init>	()V
    //   65: ldc_w 2191
    //   68: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   71: iload 4
    //   73: invokevirtual 1075	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   76: ldc_w 2180
    //   79: invokevirtual 638	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   82: aload_1
    //   83: invokevirtual 641	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   86: invokevirtual 646	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   89: invokestatic 610	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   92: pop
    //   93: aload 8
    //   95: monitorexit
    //   96: lload 5
    //   98: invokestatic 1085	android/os/Binder:restoreCallingIdentity	(J)V
    //   101: iconst_0
    //   102: ireturn
    //   103: aload_0
    //   104: getfield 854	com/android/server/InputMethodManagerService:mCurClient	Lcom/android/server/InputMethodManagerService$ClientState;
    //   107: getfield 858	com/android/server/InputMethodManagerService$ClientState:client	Lcom/android/internal/view/IInputMethodClient;
    //   110: invokeinterface 1382 1 0
    //   115: aload_1
    //   116: invokeinterface 1382 1 0
    //   121: if_acmpne -78 -> 43
    //   124: aload_0
    //   125: iload_2
    //   126: aload_3
    //   127: invokevirtual 1429	com/android/server/InputMethodManagerService:showCurrentInputLocked	(ILandroid/os/ResultReceiver;)Z
    //   130: istore 7
    //   132: aload 8
    //   134: monitorexit
    //   135: lload 5
    //   137: invokestatic 1085	android/os/Binder:restoreCallingIdentity	(J)V
    //   140: iload 7
    //   142: ireturn
    //   143: astore_1
    //   144: aload 8
    //   146: monitorexit
    //   147: lload 5
    //   149: invokestatic 1085	android/os/Binder:restoreCallingIdentity	(J)V
    //   152: iconst_0
    //   153: ireturn
    //   154: astore_1
    //   155: aload 8
    //   157: monitorexit
    //   158: aload_1
    //   159: athrow
    //   160: astore_1
    //   161: lload 5
    //   163: invokestatic 1085	android/os/Binder:restoreCallingIdentity	(J)V
    //   166: aload_1
    //   167: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	168	0	this	InputMethodManagerService
    //   0	168	1	paramIInputMethodClient	IInputMethodClient
    //   0	168	2	paramInt	int
    //   0	168	3	paramResultReceiver	ResultReceiver
    //   12	60	4	i	int
    //   17	145	5	l	long
    //   130	11	7	bool	boolean
    //   32	3	9	localClientState	ClientState
    // Exception table:
    //   from	to	target	type
    //   43	93	143	android/os/RemoteException
    //   28	34	154	finally
    //   43	93	154	finally
    //   103	124	154	finally
    //   124	132	154	finally
    //   19	28	160	finally
    //   93	96	160	finally
    //   132	135	160	finally
    //   144	147	160	finally
    //   155	160	160	finally
  }
  
  InputBindResult startInputInnerLocked()
  {
    if (this.mCurMethodId == null) {
      return this.mNoBinding;
    }
    if (!this.mSystemReady) {
      return new InputBindResult(null, null, this.mCurMethodId, this.mCurSeq, this.mCurUserActionNotificationSequenceNumber);
    }
    InputMethodInfo localInputMethodInfo = (InputMethodInfo)this.mMethodMap.get(this.mCurMethodId);
    if (localInputMethodInfo == null) {
      throw new IllegalArgumentException("Unknown id: " + this.mCurMethodId);
    }
    unbindCurrentMethodLocked(true);
    this.mCurIntent = new Intent("android.view.InputMethod");
    this.mCurIntent.setComponent(localInputMethodInfo.getComponent());
    this.mCurIntent.putExtra("android.intent.extra.client_label", 17040512);
    this.mCurIntent.putExtra("android.intent.extra.client_intent", PendingIntent.getActivity(this.mContext, 0, new Intent("android.settings.INPUT_METHOD_SETTINGS"), 0));
    if (bindCurrentInputMethodService(this.mCurIntent, this, 1610612741))
    {
      this.mLastBindTime = SystemClock.uptimeMillis();
      this.mHaveConnection = true;
      this.mCurId = localInputMethodInfo.getId();
      this.mCurToken = new Binder();
    }
    try
    {
      Slog.v("InputMethodManagerService", "Adding window token: " + this.mCurToken);
      this.mIWindowManager.addWindowToken(this.mCurToken, 2011);
      return new InputBindResult(null, null, this.mCurId, this.mCurSeq, this.mCurUserActionNotificationSequenceNumber);
      this.mCurIntent = null;
      Slog.w("InputMethodManagerService", "Failure connecting to input method service: " + this.mCurIntent);
      return null;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;) {}
    }
  }
  
  InputBindResult startInputLocked(int paramInt1, IInputMethodClient paramIInputMethodClient, IInputContext paramIInputContext, int paramInt2, EditorInfo paramEditorInfo, int paramInt3)
  {
    if (this.mCurMethodId == null) {
      return this.mNoBinding;
    }
    ClientState localClientState = (ClientState)this.mClients.get(paramIInputMethodClient.asBinder());
    if (localClientState == null) {
      throw new IllegalArgumentException("unknown client " + paramIInputMethodClient.asBinder());
    }
    if (paramEditorInfo == null)
    {
      Slog.w("InputMethodManagerService", "Ignoring startInput with null EditorInfo. uid=" + localClientState.uid + " pid=" + localClientState.pid);
      return null;
    }
    try
    {
      if (!this.mIWindowManager.inputMethodClientHasFocus(localClientState.client))
      {
        Slog.w("InputMethodManagerService", "Starting input on non-focused client " + localClientState.client + " (uid=" + localClientState.uid + " pid=" + localClientState.pid + ")");
        return null;
      }
    }
    catch (RemoteException paramIInputMethodClient) {}
    return startInputUncheckedLocked(localClientState, paramIInputContext, paramInt2, paramEditorInfo, paramInt3);
  }
  
  public InputBindResult startInputOrWindowGainedFocus(int paramInt1, IInputMethodClient paramIInputMethodClient, IBinder paramIBinder, int paramInt2, int paramInt3, int paramInt4, EditorInfo paramEditorInfo, IInputContext paramIInputContext, int paramInt5)
  {
    if (paramIBinder != null) {
      return windowGainedFocus(paramInt1, paramIInputMethodClient, paramIBinder, paramInt2, paramInt3, paramInt4, paramEditorInfo, paramIInputContext, paramInt5);
    }
    return startInput(paramInt1, paramIInputMethodClient, paramIInputContext, paramInt5, paramEditorInfo, paramInt2);
  }
  
  InputBindResult startInputUncheckedLocked(ClientState paramClientState, IInputContext paramIInputContext, int paramInt1, EditorInfo paramEditorInfo, int paramInt2)
  {
    boolean bool = true;
    if (this.mCurMethodId == null) {
      return this.mNoBinding;
    }
    if (!InputMethodUtils.checkIfPackageBelongsToUid(this.mAppOpsManager, paramClientState.uid, paramEditorInfo.packageName))
    {
      Slog.e("InputMethodManagerService", "Rejecting this client as it reported an invalid package name. uid=" + paramClientState.uid + " package=" + paramEditorInfo.packageName);
      return this.mNoBinding;
    }
    int i;
    if (this.mCurClient != paramClientState)
    {
      this.mCurClientInKeyguard = isKeyguardLocked();
      unbindCurrentClientLocked(1);
      if (this.mIsInteractive)
      {
        IInputMethodClient localIInputMethodClient = paramClientState.client;
        HandlerCaller localHandlerCaller = this.mCaller;
        if (!this.mIsInteractive) {
          break label236;
        }
        i = 1;
        executeOrSendMessage(localIInputMethodClient, localHandlerCaller.obtainMessageIO(3020, i, paramClientState));
      }
    }
    this.mCurSeq += 1;
    if (this.mCurSeq <= 0) {
      this.mCurSeq = 1;
    }
    this.mCurClient = paramClientState;
    this.mCurInputContext = paramIInputContext;
    this.mCurInputContextMissingMethods = paramInt1;
    this.mCurAttribute = paramEditorInfo;
    if ((this.mCurId != null) && (this.mCurId.equals(this.mCurMethodId)))
    {
      if (paramClientState.curSession != null)
      {
        if ((paramInt2 & 0x100) != 0) {}
        for (;;)
        {
          return attachNewInputLocked(bool);
          label236:
          i = 0;
          break;
          bool = false;
        }
      }
      if (this.mHaveConnection)
      {
        if (this.mCurMethod != null)
        {
          requestClientSessionLocked(paramClientState);
          return new InputBindResult(null, null, this.mCurId, this.mCurSeq, this.mCurUserActionNotificationSequenceNumber);
        }
        if (SystemClock.uptimeMillis() < this.mLastBindTime + 3000L) {
          return new InputBindResult(null, null, this.mCurId, this.mCurSeq, this.mCurUserActionNotificationSequenceNumber);
        }
        EventLog.writeEvent(32000, new Object[] { this.mCurMethodId, Long.valueOf(SystemClock.uptimeMillis() - this.mLastBindTime), Integer.valueOf(0) });
      }
    }
    return startInputInnerLocked();
  }
  
  public boolean switchToLastInputMethod(IBinder paramIBinder)
  {
    if (!calledFromValidUser()) {
      return false;
    }
    for (;;)
    {
      Object localObject2;
      String str;
      int k;
      int i;
      Object localObject1;
      boolean bool;
      int m;
      int j;
      synchronized (this.mMethodMap)
      {
        Object localObject3 = this.mSettings.getLastInputMethodAndSubtypeLocked();
        if (localObject3 != null)
        {
          localObject2 = (InputMethodInfo)this.mMethodMap.get(((Pair)localObject3).first);
          str = null;
          k = -1;
          i = k;
          localObject1 = str;
          if (localObject3 != null)
          {
            i = k;
            localObject1 = str;
            if (localObject2 != null)
            {
              bool = ((InputMethodInfo)localObject2).getId().equals(this.mCurMethodId);
              m = Integer.parseInt((String)((Pair)localObject3).second);
              if (this.mCurrentSubtype != null) {
                continue;
              }
              j = -1;
              break label413;
              localObject1 = (String)((Pair)localObject3).first;
              i = InputMethodUtils.getSubtypeIdFromHashCode((InputMethodInfo)localObject2, m);
            }
          }
          j = i;
          localObject2 = localObject1;
          if (TextUtils.isEmpty((CharSequence)localObject1))
          {
            if (!InputMethodUtils.canAddToLastInputMethod(this.mCurrentSubtype)) {
              continue;
            }
            localObject2 = localObject1;
            j = i;
          }
          if (TextUtils.isEmpty((CharSequence)localObject2)) {
            continue;
          }
          setInputMethodWithSubtypeIdLocked(paramIBinder, (String)localObject2, j);
          return true;
        }
        else
        {
          localObject2 = null;
          continue;
        }
        j = this.mCurrentSubtype.hashCode();
        break label413;
        localObject3 = this.mSettings.getEnabledInputMethodListLocked();
        j = i;
        localObject2 = localObject1;
        if (localObject3 == null) {
          continue;
        }
        m = ((List)localObject3).size();
        if (this.mCurrentSubtype == null)
        {
          str = this.mRes.getConfiguration().locale.toString();
          break label434;
          j = i;
          localObject2 = localObject1;
          if (k >= m) {
            continue;
          }
          InputMethodInfo localInputMethodInfo = (InputMethodInfo)((List)localObject3).get(k);
          j = i;
          localObject2 = localObject1;
          if (localInputMethodInfo.getSubtypeCount() <= 0) {
            break label440;
          }
          j = i;
          localObject2 = localObject1;
          if (!InputMethodUtils.isSystemIme(localInputMethodInfo)) {
            break label440;
          }
          InputMethodSubtype localInputMethodSubtype = InputMethodUtils.findLastResortApplicableSubtypeLocked(this.mRes, InputMethodUtils.getSubtypes(localInputMethodInfo), "keyboard", str, true);
          j = i;
          localObject2 = localObject1;
          if (localInputMethodSubtype == null) {
            break label440;
          }
          localObject1 = localInputMethodInfo.getId();
          i = InputMethodUtils.getSubtypeIdFromHashCode(localInputMethodInfo, localInputMethodSubtype.hashCode());
          j = i;
          localObject2 = localObject1;
          if (localInputMethodSubtype.getLocale().equals(str)) {
            continue;
          }
          j = i;
          localObject2 = localObject1;
          break label440;
        }
        str = this.mCurrentSubtype.getLocale();
        break label434;
        return false;
      }
      label413:
      if (bool)
      {
        i = k;
        localObject1 = str;
        if (m != j)
        {
          continue;
          label434:
          k = 0;
          continue;
          label440:
          k += 1;
          i = j;
          localObject1 = localObject2;
        }
      }
    }
  }
  
  public boolean switchToNextInputMethod(IBinder paramIBinder, boolean paramBoolean)
  {
    if (!calledFromValidUser()) {
      return false;
    }
    synchronized (this.mMethodMap)
    {
      if (!calledWithValidToken(paramIBinder))
      {
        int i = Binder.getCallingUid();
        Slog.e("InputMethodManagerService", "Ignoring switchToNextInputMethod due to an invalid token. uid:" + i + " token:" + paramIBinder);
        return false;
      }
      InputMethodSubtypeSwitchingController.ImeSubtypeListItem localImeSubtypeListItem = this.mSwitchingController.getNextInputMethodLocked(paramBoolean, (InputMethodInfo)this.mMethodMap.get(this.mCurMethodId), this.mCurrentSubtype, true);
      if (localImeSubtypeListItem == null) {
        return false;
      }
      setInputMethodWithSubtypeIdLocked(paramIBinder, localImeSubtypeListItem.mImi.getId(), localImeSubtypeListItem.mSubtypeId);
      return true;
    }
  }
  
  public void systemRunning(StatusBarManagerService paramStatusBarManagerService)
  {
    synchronized (this.mMethodMap)
    {
      int i;
      InputMethodUtils.InputMethodSettings localInputMethodSettings;
      boolean bool;
      if (!this.mSystemReady)
      {
        this.mSystemReady = true;
        i = this.mSettings.getCurrentUserId();
        localInputMethodSettings = this.mSettings;
        if (!this.mUserManager.isUserUnlockingOrUnlocked(i)) {
          break label246;
        }
        bool = false;
      }
      for (;;)
      {
        localInputMethodSettings.switchCurrentUser(i, bool);
        this.mKeyguardManager = ((KeyguardManager)this.mContext.getSystemService(KeyguardManager.class));
        this.mNotificationManager = ((NotificationManager)this.mContext.getSystemService(NotificationManager.class));
        this.mStatusBar = paramStatusBarManagerService;
        if (this.mStatusBar != null) {
          this.mStatusBar.setIconVisibility(this.mSlotIme, false);
        }
        updateSystemUiLocked(this.mCurToken, this.mImeWindowVis, this.mBackDisposition);
        this.mShowOngoingImeSwitcherForPhones = this.mRes.getBoolean(17956870);
        if (this.mShowOngoingImeSwitcherForPhones) {
          this.mWindowManagerInternal.setOnHardKeyboardStatusChangeListener(this.mHardKeyboardListener);
        }
        if (this.mImeSelectedOnBoot)
        {
          bool = false;
          buildInputMethodListLocked(bool);
          if (!this.mImeSelectedOnBoot)
          {
            Slog.w("InputMethodManagerService", "Reset the default IME as \"Resource\" is ready here.");
            resetStateIfCurrentLocaleChangedLocked();
            InputMethodUtils.setNonSelectedSystemImesDisabledUntilUsed(this.mIPackageManager, this.mSettings.getEnabledInputMethodListLocked(), this.mSettings.getCurrentUserId(), this.mContext.getBasePackageName());
          }
          this.mLastSystemLocales = this.mRes.getConfiguration().getLocales();
        }
        try
        {
          startInputInnerLocked();
          return;
          label246:
          bool = true;
          continue;
          bool = true;
        }
        catch (RuntimeException paramStatusBarManagerService)
        {
          for (;;)
          {
            Slog.w("InputMethodManagerService", "Unexpected exception", paramStatusBarManagerService);
          }
        }
      }
    }
  }
  
  void unbindCurrentClientLocked(int paramInt)
  {
    if (this.mCurClient != null)
    {
      if (this.mBoundToMethod)
      {
        this.mBoundToMethod = false;
        if (this.mCurMethod != null) {
          executeOrSendMessage(this.mCurMethod, this.mCaller.obtainMessageO(1000, this.mCurMethod));
        }
      }
      executeOrSendMessage(this.mCurClient.client, this.mCaller.obtainMessageIO(3020, 0, this.mCurClient));
      executeOrSendMessage(this.mCurClient.client, this.mCaller.obtainMessageIIO(3000, this.mCurSeq, paramInt, this.mCurClient.client));
      this.mCurClient.sessionRequested = false;
      this.mCurClient = null;
      hideInputMethodMenuLocked();
    }
  }
  
  void unbindCurrentMethodLocked(boolean paramBoolean)
  {
    if (this.mVisibleBound)
    {
      this.mContext.unbindService(this.mVisibleConnection);
      this.mVisibleBound = false;
    }
    if (this.mHaveConnection)
    {
      this.mContext.unbindService(this);
      this.mHaveConnection = false;
    }
    if (this.mCurToken != null) {}
    try
    {
      if (((this.mImeWindowVis & 0x1) != 0) && (paramBoolean)) {
        this.mWindowManagerInternal.saveLastInputMethodWindowForTransition();
      }
      this.mIWindowManager.removeWindowToken(this.mCurToken);
    }
    catch (RemoteException localRemoteException)
    {
      for (;;) {}
    }
    this.mCurToken = null;
    this.mCurId = null;
    clearCurMethodLocked();
  }
  
  void updateCurrentProfileIds()
  {
    this.mSettings.setCurrentProfileIds(this.mUserManager.getProfileIdsWithDisabled(this.mSettings.getCurrentUserId()));
  }
  
  void updateFromSettingsLocked(boolean paramBoolean)
  {
    updateInputMethodsFromSettingsLocked(paramBoolean);
    updateKeyboardFromSettingsLocked();
  }
  
  void updateInputMethodsFromSettingsLocked(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      localObject1 = this.mSettings.getEnabledInputMethodListLocked();
      int i = 0;
      while (i < ((List)localObject1).size())
      {
        localObject2 = (InputMethodInfo)((List)localObject1).get(i);
        try
        {
          ApplicationInfo localApplicationInfo = this.mIPackageManager.getApplicationInfo(((InputMethodInfo)localObject2).getPackageName(), 32768, this.mSettings.getCurrentUserId());
          if ((localApplicationInfo != null) && (localApplicationInfo.enabledSetting == 4)) {
            this.mIPackageManager.setApplicationEnabledSetting(((InputMethodInfo)localObject2).getPackageName(), 0, 1, this.mSettings.getCurrentUserId(), this.mContext.getBasePackageName());
          }
        }
        catch (RemoteException localRemoteException)
        {
          for (;;) {}
        }
        i += 1;
      }
    }
    Object localObject2 = this.mSettings.getSelectedInputMethod();
    Object localObject1 = localObject2;
    if (TextUtils.isEmpty((CharSequence)localObject2))
    {
      localObject1 = localObject2;
      if (chooseNewDefaultIMELocked()) {
        localObject1 = this.mSettings.getSelectedInputMethod();
      }
    }
    if (!TextUtils.isEmpty((CharSequence)localObject1)) {}
    for (;;)
    {
      try
      {
        setInputMethodLocked((String)localObject1, this.mSettings.getSelectedInputMethodSubtypeId((String)localObject1));
        this.mShortcutInputMethodsAndSubtypes.clear();
        this.mSwitchingController.resetCircularListLocked(this.mContext);
        return;
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        Slog.w("InputMethodManagerService", "Unknown input method from prefs: " + (String)localObject1, localIllegalArgumentException);
        resetCurrentMethodAndClient(5);
        continue;
      }
      resetCurrentMethodAndClient(4);
    }
  }
  
  public void updateKeyboardFromSettingsLocked()
  {
    this.mShowImeWithHardKeyboard = this.mSettings.isShowImeWithHardKeyboardEnabled();
    if ((this.mSwitchingDialog != null) && (this.mSwitchingDialogTitleView != null) && (this.mSwitchingDialog.isShowing()))
    {
      final Switch localSwitch = (Switch)this.mSwitchingDialogTitleView.findViewById(16909196);
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          localSwitch.setChecked(InputMethodManagerService.-get4(InputMethodManagerService.this));
        }
      });
    }
  }
  
  public void updateStatusIcon(IBinder paramIBinder, String paramString, int paramInt)
  {
    long l = Binder.clearCallingIdentity();
    for (;;)
    {
      try
      {
        synchronized (this.mMethodMap)
        {
          if (!calledWithValidToken(paramIBinder))
          {
            paramInt = Binder.getCallingUid();
            Slog.e("InputMethodManagerService", "Ignoring updateStatusIcon due to an invalid token. uid:" + paramInt + " token:" + paramIBinder);
            return;
          }
          if (paramInt == 0)
          {
            if (this.mStatusBar != null) {
              this.mStatusBar.setIconVisibility(this.mSlotIme, false);
            }
            return;
          }
          if (paramString == null) {
            continue;
          }
          paramIBinder = null;
          try
          {
            localObject = this.mContext.getPackageManager().getApplicationLabel(this.mIPackageManager.getApplicationInfo(paramString, 0, this.mSettings.getCurrentUserId()));
            paramIBinder = (IBinder)localObject;
          }
          catch (RemoteException localRemoteException)
          {
            Object localObject;
            String str;
            continue;
          }
          if (this.mStatusBar == null) {
            continue;
          }
          localObject = this.mStatusBar;
          str = this.mSlotIme;
          if (paramIBinder != null)
          {
            paramIBinder = paramIBinder.toString();
            ((StatusBarManagerService)localObject).setIcon(str, paramString, paramInt, 0, paramIBinder);
            this.mStatusBar.setIconVisibility(this.mSlotIme, true);
          }
        }
        paramIBinder = null;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
  }
  
  static final class ClientState
  {
    final InputBinding binding;
    final IInputMethodClient client;
    InputMethodManagerService.SessionState curSession;
    final IInputContext inputContext;
    final int pid;
    boolean sessionRequested;
    final int uid;
    
    ClientState(IInputMethodClient paramIInputMethodClient, IInputContext paramIInputContext, int paramInt1, int paramInt2)
    {
      this.client = paramIInputMethodClient;
      this.inputContext = paramIInputContext;
      this.uid = paramInt1;
      this.pid = paramInt2;
      this.binding = new InputBinding(null, this.inputContext.asBinder(), this.uid, this.pid);
    }
    
    public String toString()
    {
      return "ClientState{" + Integer.toHexString(System.identityHashCode(this)) + " uid " + this.uid + " pid " + this.pid + "}";
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @IntDef({0L, 1L})
  private static @interface HardKeyboardBehavior
  {
    public static final int WIRED_AFFORDANCE = 1;
    public static final int WIRELESS_AFFORDANCE = 0;
  }
  
  private class HardKeyboardListener
    implements WindowManagerInternal.OnHardKeyboardStatusChangeListener
  {
    private HardKeyboardListener() {}
    
    public void handleHardKeyboardStatusChange(boolean paramBoolean)
    {
      synchronized (InputMethodManagerService.this.mMethodMap)
      {
        if ((InputMethodManagerService.-get6(InputMethodManagerService.this) != null) && (InputMethodManagerService.-get7(InputMethodManagerService.this) != null) && (InputMethodManagerService.-get6(InputMethodManagerService.this).isShowing()))
        {
          View localView = InputMethodManagerService.-get7(InputMethodManagerService.this).findViewById(16909195);
          if (paramBoolean)
          {
            i = 0;
            localView.setVisibility(i);
          }
        }
        else
        {
          return;
        }
        int i = 8;
      }
    }
    
    public void onHardKeyboardStatusChange(boolean paramBoolean)
    {
      Handler localHandler1 = InputMethodManagerService.this.mHandler;
      Handler localHandler2 = InputMethodManagerService.this.mHandler;
      if (paramBoolean) {}
      for (int i = 1;; i = 0)
      {
        localHandler1.sendMessage(localHandler2.obtainMessage(4000, Integer.valueOf(i)));
        return;
      }
    }
  }
  
  private static class ImeSubtypeListAdapter
    extends ArrayAdapter<InputMethodSubtypeSwitchingController.ImeSubtypeListItem>
  {
    public int mCheckedItem;
    private final LayoutInflater mInflater;
    private final List<InputMethodSubtypeSwitchingController.ImeSubtypeListItem> mItemsList;
    private final int mTextViewResourceId;
    
    public ImeSubtypeListAdapter(Context paramContext, int paramInt1, List<InputMethodSubtypeSwitchingController.ImeSubtypeListItem> paramList, int paramInt2)
    {
      super(paramInt1, paramList);
      this.mTextViewResourceId = paramInt1;
      this.mItemsList = paramList;
      this.mCheckedItem = paramInt2;
      this.mInflater = ((LayoutInflater)paramContext.getSystemService(LayoutInflater.class));
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      boolean bool = false;
      if (paramView != null) {}
      while ((paramInt < 0) || (paramInt >= this.mItemsList.size()))
      {
        return paramView;
        paramView = this.mInflater.inflate(this.mTextViewResourceId, null);
      }
      Object localObject = (InputMethodSubtypeSwitchingController.ImeSubtypeListItem)this.mItemsList.get(paramInt);
      paramViewGroup = ((InputMethodSubtypeSwitchingController.ImeSubtypeListItem)localObject).mImeName;
      localObject = ((InputMethodSubtypeSwitchingController.ImeSubtypeListItem)localObject).mSubtypeName;
      TextView localTextView1 = (TextView)paramView.findViewById(16908308);
      TextView localTextView2 = (TextView)paramView.findViewById(16908309);
      if (TextUtils.isEmpty((CharSequence)localObject))
      {
        localTextView1.setText(paramViewGroup);
        localTextView2.setVisibility(8);
      }
      for (;;)
      {
        paramViewGroup = (RadioButton)paramView.findViewById(16909197);
        if (paramInt == this.mCheckedItem) {
          bool = true;
        }
        paramViewGroup.setChecked(bool);
        return paramView;
        localTextView1.setText((CharSequence)localObject);
        localTextView2.setText(paramViewGroup);
        localTextView2.setVisibility(0);
      }
    }
  }
  
  class ImmsBroadcastReceiver
    extends BroadcastReceiver
  {
    ImmsBroadcastReceiver() {}
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      paramContext = paramIntent.getAction();
      if ("android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(paramContext))
      {
        InputMethodManagerService.this.hideInputMethodMenu();
        return;
      }
      if (("android.intent.action.USER_ADDED".equals(paramContext)) || ("android.intent.action.USER_REMOVED".equals(paramContext)))
      {
        InputMethodManagerService.this.updateCurrentProfileIds();
        return;
      }
      if ("android.os.action.SETTING_RESTORED".equals(paramContext))
      {
        if ("enabled_input_methods".equals(paramIntent.getStringExtra("setting_name")))
        {
          paramContext = paramIntent.getStringExtra("previous_value");
          paramIntent = paramIntent.getStringExtra("new_value");
          InputMethodManagerService.restoreEnabledInputMethods(InputMethodManagerService.this.mContext, paramContext, paramIntent);
        }
        return;
      }
      Slog.w("InputMethodManagerService", "Unexpected intent " + paramIntent);
    }
  }
  
  private static class InputMethodFileManager
  {
    private static final String ADDITIONAL_SUBTYPES_FILE_NAME = "subtypes.xml";
    private static final String ATTR_ICON = "icon";
    private static final String ATTR_ID = "id";
    private static final String ATTR_IME_SUBTYPE_EXTRA_VALUE = "imeSubtypeExtraValue";
    private static final String ATTR_IME_SUBTYPE_ID = "subtypeId";
    private static final String ATTR_IME_SUBTYPE_LANGUAGE_TAG = "languageTag";
    private static final String ATTR_IME_SUBTYPE_LOCALE = "imeSubtypeLocale";
    private static final String ATTR_IME_SUBTYPE_MODE = "imeSubtypeMode";
    private static final String ATTR_IS_ASCII_CAPABLE = "isAsciiCapable";
    private static final String ATTR_IS_AUXILIARY = "isAuxiliary";
    private static final String ATTR_LABEL = "label";
    private static final String INPUT_METHOD_PATH = "inputmethod";
    private static final String NODE_IMI = "imi";
    private static final String NODE_SUBTYPE = "subtype";
    private static final String NODE_SUBTYPES = "subtypes";
    private static final String SYSTEM_PATH = "system";
    private final AtomicFile mAdditionalInputMethodSubtypeFile;
    private final HashMap<String, List<InputMethodSubtype>> mAdditionalSubtypesMap = new HashMap();
    private final HashMap<String, InputMethodInfo> mMethodMap;
    
    public InputMethodFileManager(HashMap<String, InputMethodInfo> paramHashMap, int paramInt)
    {
      if (paramHashMap == null) {
        throw new NullPointerException("methodMap is null");
      }
      this.mMethodMap = paramHashMap;
      File localFile;
      if (paramInt == 0)
      {
        localFile = new File(Environment.getDataDirectory(), "system");
        localFile = new File(localFile, "inputmethod");
        if ((!localFile.exists()) && (!localFile.mkdirs())) {
          break label127;
        }
      }
      for (;;)
      {
        localFile = new File(localFile, "subtypes.xml");
        this.mAdditionalInputMethodSubtypeFile = new AtomicFile(localFile);
        if (localFile.exists()) {
          break label158;
        }
        writeAdditionalInputMethodSubtypes(this.mAdditionalSubtypesMap, this.mAdditionalInputMethodSubtypeFile, paramHashMap);
        return;
        localFile = Environment.getUserSystemDirectory(paramInt);
        break;
        label127:
        Slog.w("InputMethodManagerService", "Couldn't create dir.: " + localFile.getAbsolutePath());
      }
      label158:
      readAdditionalInputMethodSubtypes(this.mAdditionalSubtypesMap, this.mAdditionalInputMethodSubtypeFile);
    }
    
    private void deleteAllInputMethodSubtypes(String paramString)
    {
      synchronized (this.mMethodMap)
      {
        this.mAdditionalSubtypesMap.remove(paramString);
        writeAdditionalInputMethodSubtypes(this.mAdditionalSubtypesMap, this.mAdditionalInputMethodSubtypeFile, this.mMethodMap);
        return;
      }
    }
    
    /* Error */
    private static void readAdditionalInputMethodSubtypes(HashMap<String, List<InputMethodSubtype>> paramHashMap, AtomicFile paramAtomicFile)
    {
      // Byte code:
      //   0: aload_0
      //   1: ifnull +7 -> 8
      //   4: aload_1
      //   5: ifnonnull +4 -> 9
      //   8: return
      //   9: aload_0
      //   10: invokevirtual 166	java/util/HashMap:clear	()V
      //   13: aconst_null
      //   14: astore 12
      //   16: aconst_null
      //   17: astore 11
      //   19: aconst_null
      //   20: astore 8
      //   22: aconst_null
      //   23: astore 7
      //   25: aload_1
      //   26: invokevirtual 170	android/util/AtomicFile:openRead	()Ljava/io/FileInputStream;
      //   29: astore_1
      //   30: aload_1
      //   31: astore 7
      //   33: aload_1
      //   34: astore 8
      //   36: invokestatic 176	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
      //   39: astore 13
      //   41: aload_1
      //   42: astore 7
      //   44: aload_1
      //   45: astore 8
      //   47: aload 13
      //   49: aload_1
      //   50: getstatic 182	java/nio/charset/StandardCharsets:UTF_8	Ljava/nio/charset/Charset;
      //   53: invokevirtual 187	java/nio/charset/Charset:name	()Ljava/lang/String;
      //   56: invokeinterface 193 3 0
      //   61: aload_1
      //   62: astore 7
      //   64: aload_1
      //   65: astore 8
      //   67: aload 13
      //   69: invokeinterface 197 1 0
      //   74: pop
      //   75: aload_1
      //   76: astore 7
      //   78: aload_1
      //   79: astore 8
      //   81: aload 13
      //   83: invokeinterface 200 1 0
      //   88: istore_2
      //   89: iload_2
      //   90: iconst_2
      //   91: if_icmpeq +8 -> 99
      //   94: iload_2
      //   95: iconst_1
      //   96: if_icmpne -21 -> 75
      //   99: aload_1
      //   100: astore 7
      //   102: aload_1
      //   103: astore 8
      //   105: ldc 53
      //   107: aload 13
      //   109: invokeinterface 203 1 0
      //   114: invokevirtual 209	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   117: ifne +58 -> 175
      //   120: aload_1
      //   121: astore 7
      //   123: aload_1
      //   124: astore 8
      //   126: new 159	org/xmlpull/v1/XmlPullParserException
      //   129: dup
      //   130: ldc -45
      //   132: invokespecial 212	org/xmlpull/v1/XmlPullParserException:<init>	(Ljava/lang/String;)V
      //   135: athrow
      //   136: astore_0
      //   137: aload_0
      //   138: athrow
      //   139: astore_1
      //   140: aload_0
      //   141: astore 8
      //   143: aload 7
      //   145: ifnull +11 -> 156
      //   148: aload 7
      //   150: invokevirtual 217	java/io/FileInputStream:close	()V
      //   153: aload_0
      //   154: astore 8
      //   156: aload 8
      //   158: ifnull +586 -> 744
      //   161: aload 8
      //   163: athrow
      //   164: astore_0
      //   165: ldc 123
      //   167: ldc -37
      //   169: aload_0
      //   170: invokestatic 222	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   173: pop
      //   174: return
      //   175: aload_1
      //   176: astore 7
      //   178: aload_1
      //   179: astore 8
      //   181: aload 13
      //   183: invokeinterface 225 1 0
      //   188: istore_2
      //   189: aconst_null
      //   190: astore 10
      //   192: aconst_null
      //   193: astore 9
      //   195: aload_1
      //   196: astore 7
      //   198: aload_1
      //   199: astore 8
      //   201: aload 13
      //   203: invokeinterface 200 1 0
      //   208: istore_3
      //   209: iload_3
      //   210: iconst_3
      //   211: if_icmpne +20 -> 231
      //   214: aload_1
      //   215: astore 7
      //   217: aload_1
      //   218: astore 8
      //   220: aload 13
      //   222: invokeinterface 225 1 0
      //   227: iload_2
      //   228: if_icmple +475 -> 703
      //   231: iload_3
      //   232: iconst_1
      //   233: if_icmpeq +470 -> 703
      //   236: iload_3
      //   237: iconst_2
      //   238: if_icmpne -43 -> 195
      //   241: aload_1
      //   242: astore 7
      //   244: aload_1
      //   245: astore 8
      //   247: aload 13
      //   249: invokeinterface 203 1 0
      //   254: astore 14
      //   256: aload_1
      //   257: astore 7
      //   259: aload_1
      //   260: astore 8
      //   262: ldc 47
      //   264: aload 14
      //   266: invokevirtual 209	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   269: ifeq +85 -> 354
      //   272: aload_1
      //   273: astore 7
      //   275: aload_1
      //   276: astore 8
      //   278: aload 13
      //   280: aconst_null
      //   281: ldc 17
      //   283: invokeinterface 229 3 0
      //   288: astore 10
      //   290: aload_1
      //   291: astore 7
      //   293: aload_1
      //   294: astore 8
      //   296: aload 10
      //   298: invokestatic 235	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
      //   301: ifeq +20 -> 321
      //   304: aload_1
      //   305: astore 7
      //   307: aload_1
      //   308: astore 8
      //   310: ldc 123
      //   312: ldc -19
      //   314: invokestatic 145	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   317: pop
      //   318: goto -123 -> 195
      //   321: aload_1
      //   322: astore 7
      //   324: aload_1
      //   325: astore 8
      //   327: new 239	java/util/ArrayList
      //   330: dup
      //   331: invokespecial 240	java/util/ArrayList:<init>	()V
      //   334: astore 9
      //   336: aload_1
      //   337: astore 7
      //   339: aload_1
      //   340: astore 8
      //   342: aload_0
      //   343: aload 10
      //   345: aload 9
      //   347: invokevirtual 244	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      //   350: pop
      //   351: goto -156 -> 195
      //   354: aload_1
      //   355: astore 7
      //   357: aload_1
      //   358: astore 8
      //   360: ldc 50
      //   362: aload 14
      //   364: invokevirtual 209	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   367: ifeq -172 -> 195
      //   370: aload_1
      //   371: astore 7
      //   373: aload_1
      //   374: astore 8
      //   376: aload 10
      //   378: invokestatic 235	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
      //   381: ifne +8 -> 389
      //   384: aload 9
      //   386: ifnonnull +38 -> 424
      //   389: aload_1
      //   390: astore 7
      //   392: aload_1
      //   393: astore 8
      //   395: ldc 123
      //   397: new 125	java/lang/StringBuilder
      //   400: dup
      //   401: invokespecial 126	java/lang/StringBuilder:<init>	()V
      //   404: ldc -10
      //   406: invokevirtual 132	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   409: aload 10
      //   411: invokevirtual 132	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   414: invokevirtual 139	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   417: invokestatic 145	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   420: pop
      //   421: goto -226 -> 195
      //   424: aload_1
      //   425: astore 7
      //   427: aload_1
      //   428: astore 8
      //   430: aload 13
      //   432: aconst_null
      //   433: ldc 14
      //   435: invokeinterface 229 3 0
      //   440: invokestatic 252	java/lang/Integer:parseInt	(Ljava/lang/String;)I
      //   443: istore_3
      //   444: aload_1
      //   445: astore 7
      //   447: aload_1
      //   448: astore 8
      //   450: aload 13
      //   452: aconst_null
      //   453: ldc 41
      //   455: invokeinterface 229 3 0
      //   460: invokestatic 252	java/lang/Integer:parseInt	(Ljava/lang/String;)I
      //   463: istore 4
      //   465: aload_1
      //   466: astore 7
      //   468: aload_1
      //   469: astore 8
      //   471: aload 13
      //   473: aconst_null
      //   474: ldc 29
      //   476: invokeinterface 229 3 0
      //   481: astore 14
      //   483: aload_1
      //   484: astore 7
      //   486: aload_1
      //   487: astore 8
      //   489: aload 13
      //   491: aconst_null
      //   492: ldc 26
      //   494: invokeinterface 229 3 0
      //   499: astore 15
      //   501: aload_1
      //   502: astore 7
      //   504: aload_1
      //   505: astore 8
      //   507: aload 13
      //   509: aconst_null
      //   510: ldc 32
      //   512: invokeinterface 229 3 0
      //   517: astore 16
      //   519: aload_1
      //   520: astore 7
      //   522: aload_1
      //   523: astore 8
      //   525: aload 13
      //   527: aconst_null
      //   528: ldc 20
      //   530: invokeinterface 229 3 0
      //   535: astore 17
      //   537: aload_1
      //   538: astore 7
      //   540: aload_1
      //   541: astore 8
      //   543: ldc -2
      //   545: aload 13
      //   547: aconst_null
      //   548: ldc 38
      //   550: invokeinterface 229 3 0
      //   555: invokestatic 258	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
      //   558: invokevirtual 209	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   561: istore 5
      //   563: aload_1
      //   564: astore 7
      //   566: aload_1
      //   567: astore 8
      //   569: ldc -2
      //   571: aload 13
      //   573: aconst_null
      //   574: ldc 35
      //   576: invokeinterface 229 3 0
      //   581: invokestatic 258	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
      //   584: invokevirtual 209	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   587: istore 6
      //   589: aload_1
      //   590: astore 7
      //   592: aload_1
      //   593: astore 8
      //   595: new 260	android/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder
      //   598: dup
      //   599: invokespecial 261	android/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder:<init>	()V
      //   602: iload 4
      //   604: invokevirtual 265	android/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder:setSubtypeNameResId	(I)Landroid/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder;
      //   607: iload_3
      //   608: invokevirtual 268	android/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder:setSubtypeIconResId	(I)Landroid/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder;
      //   611: aload 14
      //   613: invokevirtual 272	android/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder:setSubtypeLocale	(Ljava/lang/String;)Landroid/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder;
      //   616: aload 15
      //   618: invokevirtual 275	android/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder:setLanguageTag	(Ljava/lang/String;)Landroid/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder;
      //   621: aload 16
      //   623: invokevirtual 278	android/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder:setSubtypeMode	(Ljava/lang/String;)Landroid/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder;
      //   626: aload 17
      //   628: invokevirtual 281	android/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder:setSubtypeExtraValue	(Ljava/lang/String;)Landroid/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder;
      //   631: iload 5
      //   633: invokevirtual 285	android/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder:setIsAuxiliary	(Z)Landroid/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder;
      //   636: iload 6
      //   638: invokevirtual 288	android/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder:setIsAsciiCapable	(Z)Landroid/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder;
      //   641: astore 14
      //   643: aload_1
      //   644: astore 7
      //   646: aload_1
      //   647: astore 8
      //   649: aload 13
      //   651: aconst_null
      //   652: ldc 23
      //   654: invokeinterface 229 3 0
      //   659: astore 15
      //   661: aload 15
      //   663: ifnull +20 -> 683
      //   666: aload_1
      //   667: astore 7
      //   669: aload_1
      //   670: astore 8
      //   672: aload 14
      //   674: aload 15
      //   676: invokestatic 252	java/lang/Integer:parseInt	(Ljava/lang/String;)I
      //   679: invokevirtual 291	android/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder:setSubtypeId	(I)Landroid/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder;
      //   682: pop
      //   683: aload_1
      //   684: astore 7
      //   686: aload_1
      //   687: astore 8
      //   689: aload 9
      //   691: aload 14
      //   693: invokevirtual 295	android/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder:build	()Landroid/view/inputmethod/InputMethodSubtype;
      //   696: invokevirtual 298	java/util/ArrayList:add	(Ljava/lang/Object;)Z
      //   699: pop
      //   700: goto -505 -> 195
      //   703: aload 12
      //   705: astore_0
      //   706: aload_1
      //   707: ifnull +10 -> 717
      //   710: aload_1
      //   711: invokevirtual 217	java/io/FileInputStream:close	()V
      //   714: aload 12
      //   716: astore_0
      //   717: aload_0
      //   718: ifnull +28 -> 746
      //   721: aload_0
      //   722: athrow
      //   723: aload_0
      //   724: astore 8
      //   726: aload_0
      //   727: aload 7
      //   729: if_acmpeq -573 -> 156
      //   732: aload_0
      //   733: aload 7
      //   735: invokevirtual 302	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
      //   738: aload_0
      //   739: astore 8
      //   741: goto -585 -> 156
      //   744: aload_1
      //   745: athrow
      //   746: return
      //   747: astore_1
      //   748: aload 8
      //   750: astore 7
      //   752: aload 11
      //   754: astore_0
      //   755: goto -615 -> 140
      //   758: astore_0
      //   759: goto -42 -> 717
      //   762: astore 7
      //   764: aload_0
      //   765: ifnonnull -42 -> 723
      //   768: aload 7
      //   770: astore 8
      //   772: goto -616 -> 156
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	775	0	paramHashMap	HashMap<String, List<InputMethodSubtype>>
      //   0	775	1	paramAtomicFile	AtomicFile
      //   88	141	2	i	int
      //   208	400	3	j	int
      //   463	140	4	k	int
      //   561	71	5	bool1	boolean
      //   587	50	6	bool2	boolean
      //   23	728	7	localObject1	Object
      //   762	7	7	localThrowable	Throwable
      //   20	751	8	localObject2	Object
      //   193	497	9	localArrayList	ArrayList
      //   190	220	10	str1	String
      //   17	736	11	localObject3	Object
      //   14	701	12	localObject4	Object
      //   39	611	13	localXmlPullParser	org.xmlpull.v1.XmlPullParser
      //   254	438	14	localObject5	Object
      //   499	176	15	str2	String
      //   517	105	16	str3	String
      //   535	92	17	str4	String
      // Exception table:
      //   from	to	target	type
      //   25	30	136	java/lang/Throwable
      //   36	41	136	java/lang/Throwable
      //   47	61	136	java/lang/Throwable
      //   67	75	136	java/lang/Throwable
      //   81	89	136	java/lang/Throwable
      //   105	120	136	java/lang/Throwable
      //   126	136	136	java/lang/Throwable
      //   181	189	136	java/lang/Throwable
      //   201	209	136	java/lang/Throwable
      //   220	231	136	java/lang/Throwable
      //   247	256	136	java/lang/Throwable
      //   262	272	136	java/lang/Throwable
      //   278	290	136	java/lang/Throwable
      //   296	304	136	java/lang/Throwable
      //   310	318	136	java/lang/Throwable
      //   327	336	136	java/lang/Throwable
      //   342	351	136	java/lang/Throwable
      //   360	370	136	java/lang/Throwable
      //   376	384	136	java/lang/Throwable
      //   395	421	136	java/lang/Throwable
      //   430	444	136	java/lang/Throwable
      //   450	465	136	java/lang/Throwable
      //   471	483	136	java/lang/Throwable
      //   489	501	136	java/lang/Throwable
      //   507	519	136	java/lang/Throwable
      //   525	537	136	java/lang/Throwable
      //   543	563	136	java/lang/Throwable
      //   569	589	136	java/lang/Throwable
      //   595	643	136	java/lang/Throwable
      //   649	661	136	java/lang/Throwable
      //   672	683	136	java/lang/Throwable
      //   689	700	136	java/lang/Throwable
      //   137	139	139	finally
      //   148	153	164	org/xmlpull/v1/XmlPullParserException
      //   148	153	164	java/io/IOException
      //   148	153	164	java/lang/NumberFormatException
      //   161	164	164	org/xmlpull/v1/XmlPullParserException
      //   161	164	164	java/io/IOException
      //   161	164	164	java/lang/NumberFormatException
      //   710	714	164	org/xmlpull/v1/XmlPullParserException
      //   710	714	164	java/io/IOException
      //   710	714	164	java/lang/NumberFormatException
      //   721	723	164	org/xmlpull/v1/XmlPullParserException
      //   721	723	164	java/io/IOException
      //   721	723	164	java/lang/NumberFormatException
      //   732	738	164	org/xmlpull/v1/XmlPullParserException
      //   732	738	164	java/io/IOException
      //   732	738	164	java/lang/NumberFormatException
      //   744	746	164	org/xmlpull/v1/XmlPullParserException
      //   744	746	164	java/io/IOException
      //   744	746	164	java/lang/NumberFormatException
      //   25	30	747	finally
      //   36	41	747	finally
      //   47	61	747	finally
      //   67	75	747	finally
      //   81	89	747	finally
      //   105	120	747	finally
      //   126	136	747	finally
      //   181	189	747	finally
      //   201	209	747	finally
      //   220	231	747	finally
      //   247	256	747	finally
      //   262	272	747	finally
      //   278	290	747	finally
      //   296	304	747	finally
      //   310	318	747	finally
      //   327	336	747	finally
      //   342	351	747	finally
      //   360	370	747	finally
      //   376	384	747	finally
      //   395	421	747	finally
      //   430	444	747	finally
      //   450	465	747	finally
      //   471	483	747	finally
      //   489	501	747	finally
      //   507	519	747	finally
      //   525	537	747	finally
      //   543	563	747	finally
      //   569	589	747	finally
      //   595	643	747	finally
      //   649	661	747	finally
      //   672	683	747	finally
      //   689	700	747	finally
      //   710	714	758	java/lang/Throwable
      //   148	153	762	java/lang/Throwable
    }
    
    private static void writeAdditionalInputMethodSubtypes(HashMap<String, List<InputMethodSubtype>> paramHashMap, AtomicFile paramAtomicFile, HashMap<String, InputMethodInfo> paramHashMap1)
    {
      if ((paramHashMap1 != null) && (paramHashMap1.size() > 0)) {}
      for (int i = 1;; i = 0)
      {
        Object localObject1 = null;
        FileOutputStream localFileOutputStream;
        FastXmlSerializer localFastXmlSerializer;
        for (;;)
        {
          try
          {
            localFileOutputStream = paramAtomicFile.startWrite();
            localObject1 = localFileOutputStream;
            localFastXmlSerializer = new FastXmlSerializer();
            localObject1 = localFileOutputStream;
            localFastXmlSerializer.setOutput(localFileOutputStream, StandardCharsets.UTF_8.name());
            localObject1 = localFileOutputStream;
            localFastXmlSerializer.startDocument(null, Boolean.valueOf(true));
            localObject1 = localFileOutputStream;
            localFastXmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            localObject1 = localFileOutputStream;
            localFastXmlSerializer.startTag(null, "subtypes");
            localObject1 = localFileOutputStream;
            Iterator localIterator = paramHashMap.keySet().iterator();
            localObject1 = localFileOutputStream;
            if (!localIterator.hasNext()) {
              break;
            }
            localObject1 = localFileOutputStream;
            Object localObject2 = (String)localIterator.next();
            if (i != 0)
            {
              localObject1 = localFileOutputStream;
              if (!paramHashMap1.containsKey(localObject2)) {}
            }
            else
            {
              localObject1 = localFileOutputStream;
              localFastXmlSerializer.startTag(null, "imi");
              localObject1 = localFileOutputStream;
              localFastXmlSerializer.attribute(null, "id", (String)localObject2);
              localObject1 = localFileOutputStream;
              localObject2 = (List)paramHashMap.get(localObject2);
              localObject1 = localFileOutputStream;
              int m = ((List)localObject2).size();
              int j = 0;
              if (j >= m) {
                break label588;
              }
              localObject1 = localFileOutputStream;
              InputMethodSubtype localInputMethodSubtype = (InputMethodSubtype)((List)localObject2).get(j);
              localObject1 = localFileOutputStream;
              localFastXmlSerializer.startTag(null, "subtype");
              localObject1 = localFileOutputStream;
              if (localInputMethodSubtype.hasSubtypeId())
              {
                localObject1 = localFileOutputStream;
                localFastXmlSerializer.attribute(null, "subtypeId", String.valueOf(localInputMethodSubtype.getSubtypeId()));
              }
              localObject1 = localFileOutputStream;
              localFastXmlSerializer.attribute(null, "icon", String.valueOf(localInputMethodSubtype.getIconResId()));
              localObject1 = localFileOutputStream;
              localFastXmlSerializer.attribute(null, "label", String.valueOf(localInputMethodSubtype.getNameResId()));
              localObject1 = localFileOutputStream;
              localFastXmlSerializer.attribute(null, "imeSubtypeLocale", localInputMethodSubtype.getLocale());
              localObject1 = localFileOutputStream;
              localFastXmlSerializer.attribute(null, "languageTag", localInputMethodSubtype.getLanguageTag());
              localObject1 = localFileOutputStream;
              localFastXmlSerializer.attribute(null, "imeSubtypeMode", localInputMethodSubtype.getMode());
              localObject1 = localFileOutputStream;
              localFastXmlSerializer.attribute(null, "imeSubtypeExtraValue", localInputMethodSubtype.getExtraValue());
              localObject1 = localFileOutputStream;
              if (!localInputMethodSubtype.isAuxiliary()) {
                break label576;
              }
              k = 1;
              localObject1 = localFileOutputStream;
              localFastXmlSerializer.attribute(null, "isAuxiliary", String.valueOf(k));
              localObject1 = localFileOutputStream;
              if (!localInputMethodSubtype.isAsciiCapable()) {
                break label582;
              }
              k = 1;
              localObject1 = localFileOutputStream;
              localFastXmlSerializer.attribute(null, "isAsciiCapable", String.valueOf(k));
              localObject1 = localFileOutputStream;
              localFastXmlSerializer.endTag(null, "subtype");
              j += 1;
              continue;
            }
            localObject1 = localFileOutputStream;
            Slog.w("InputMethodManagerService", "IME uninstalled or not valid.: " + (String)localObject2);
            continue;
            k = 0;
          }
          catch (IOException paramHashMap)
          {
            Slog.w("InputMethodManagerService", "Error writing subtypes", paramHashMap);
            if (localObject1 != null) {
              paramAtomicFile.failWrite((FileOutputStream)localObject1);
            }
            return;
          }
          label576:
          continue;
          label582:
          int k = 0;
          continue;
          label588:
          localObject1 = localFileOutputStream;
          localFastXmlSerializer.endTag(null, "imi");
        }
        localObject1 = localFileOutputStream;
        localFastXmlSerializer.endTag(null, "subtypes");
        localObject1 = localFileOutputStream;
        localFastXmlSerializer.endDocument();
        localObject1 = localFileOutputStream;
        paramAtomicFile.finishWrite(localFileOutputStream);
        return;
      }
    }
    
    public void addInputMethodSubtypes(InputMethodInfo paramInputMethodInfo, InputMethodSubtype[] paramArrayOfInputMethodSubtype)
    {
      for (;;)
      {
        ArrayList localArrayList;
        int i;
        synchronized (this.mMethodMap)
        {
          localArrayList = new ArrayList();
          int j = paramArrayOfInputMethodSubtype.length;
          i = 0;
          if (i < j)
          {
            InputMethodSubtype localInputMethodSubtype = paramArrayOfInputMethodSubtype[i];
            if (!localArrayList.contains(localInputMethodSubtype)) {
              localArrayList.add(localInputMethodSubtype);
            } else {
              Slog.w("InputMethodManagerService", "Duplicated subtype definition found: " + localInputMethodSubtype.getLocale() + ", " + localInputMethodSubtype.getMode());
            }
          }
        }
        this.mAdditionalSubtypesMap.put(paramInputMethodInfo.getId(), localArrayList);
        writeAdditionalInputMethodSubtypes(this.mAdditionalSubtypesMap, this.mAdditionalInputMethodSubtypeFile, this.mMethodMap);
        return;
        i += 1;
      }
    }
    
    public HashMap<String, List<InputMethodSubtype>> getAllAdditionalInputMethodSubtypes()
    {
      synchronized (this.mMethodMap)
      {
        HashMap localHashMap2 = this.mAdditionalSubtypesMap;
        return localHashMap2;
      }
    }
  }
  
  public static final class Lifecycle
    extends SystemService
  {
    private InputMethodManagerService mService;
    
    public Lifecycle(Context paramContext)
    {
      super();
      this.mService = new InputMethodManagerService(paramContext);
    }
    
    public void onBootPhase(int paramInt)
    {
      if (paramInt == 550)
      {
        StatusBarManagerService localStatusBarManagerService = (StatusBarManagerService)ServiceManager.getService("statusbar");
        this.mService.systemRunning(localStatusBarManagerService);
      }
    }
    
    public void onStart()
    {
      LocalServices.addService(InputMethodManagerInternal.class, new InputMethodManagerService.LocalServiceImpl(this.mService.mHandler));
      publishBinderService("input_method", this.mService);
    }
    
    public void onSwitchUser(int paramInt)
    {
      this.mService.onSwitchUser(paramInt);
    }
    
    public void onUnlockUser(int paramInt)
    {
      this.mService.mHandler.sendMessage(this.mService.mHandler.obtainMessage(5000, paramInt, 0));
    }
  }
  
  private static final class LocalServiceImpl
    implements InputMethodManagerInternal
  {
    private final Handler mHandler;
    
    LocalServiceImpl(Handler paramHandler)
    {
      this.mHandler = paramHandler;
    }
    
    public void hideCurrentInputMethod()
    {
      this.mHandler.removeMessages(1035);
      this.mHandler.sendEmptyMessage(1035);
    }
    
    public void setInteractive(boolean paramBoolean)
    {
      Handler localHandler1 = this.mHandler;
      Handler localHandler2 = this.mHandler;
      if (paramBoolean) {}
      for (int i = 1;; i = 0)
      {
        localHandler1.sendMessage(localHandler2.obtainMessage(3030, i, 0));
        return;
      }
    }
    
    public void switchInputMethod(boolean paramBoolean)
    {
      Handler localHandler1 = this.mHandler;
      Handler localHandler2 = this.mHandler;
      if (paramBoolean) {}
      for (int i = 1;; i = 0)
      {
        localHandler1.sendMessage(localHandler2.obtainMessage(3050, i, 0));
        return;
      }
    }
  }
  
  private static final class MethodCallback
    extends IInputSessionCallback.Stub
  {
    private final InputChannel mChannel;
    private final IInputMethod mMethod;
    private final InputMethodManagerService mParentIMMS;
    
    MethodCallback(InputMethodManagerService paramInputMethodManagerService, IInputMethod paramIInputMethod, InputChannel paramInputChannel)
    {
      this.mParentIMMS = paramInputMethodManagerService;
      this.mMethod = paramIInputMethod;
      this.mChannel = paramInputChannel;
    }
    
    public void sessionCreated(IInputMethodSession paramIInputMethodSession)
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        this.mParentIMMS.onSessionCreated(this.mMethod, paramIInputMethodSession, this.mChannel);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
  }
  
  class MyPackageMonitor
    extends PackageMonitor
  {
    MyPackageMonitor() {}
    
    private boolean isChangingPackagesOfCurrentUser()
    {
      return getChangingUserId() == InputMethodManagerService.this.mSettings.getCurrentUserId();
    }
    
    public boolean onHandleForceStop(Intent arg1, String[] paramArrayOfString, int paramInt, boolean paramBoolean)
    {
      if (!isChangingPackagesOfCurrentUser()) {
        return false;
      }
      synchronized (InputMethodManagerService.this.mMethodMap)
      {
        String str1 = InputMethodManagerService.this.mSettings.getSelectedInputMethod();
        int j = InputMethodManagerService.this.mMethodList.size();
        if (str1 != null)
        {
          paramInt = 0;
          while (paramInt < j)
          {
            InputMethodInfo localInputMethodInfo = (InputMethodInfo)InputMethodManagerService.this.mMethodList.get(paramInt);
            if (localInputMethodInfo.getId().equals(str1))
            {
              int k = paramArrayOfString.length;
              int i = 0;
              while (i < k)
              {
                String str2 = paramArrayOfString[i];
                boolean bool = localInputMethodInfo.getPackageName().equals(str2);
                if (bool)
                {
                  if (!paramBoolean) {
                    return true;
                  }
                  InputMethodManagerService.-wrap1(InputMethodManagerService.this, "");
                  InputMethodManagerService.-wrap0(InputMethodManagerService.this);
                  return true;
                }
                i += 1;
              }
            }
            paramInt += 1;
          }
        }
        return false;
      }
    }
    
    public void onSomePackagesChanged()
    {
      if (!isChangingPackagesOfCurrentUser()) {
        return;
      }
      HashMap localHashMap = InputMethodManagerService.this.mMethodMap;
      Object localObject1 = null;
      for (Object localObject3 = null;; localObject3 = localObject2)
      {
        int i;
        int k;
        label278:
        do
        {
          String str1;
          boolean bool2;
          try
          {
            str1 = InputMethodManagerService.this.mSettings.getSelectedInputMethod();
            int j = InputMethodManagerService.this.mMethodList.size();
            if (str1 != null)
            {
              i = 0;
              localObject1 = localObject3;
              if (i < j)
              {
                localObject4 = (InputMethodInfo)InputMethodManagerService.this.mMethodList.get(i);
                String str2 = ((InputMethodInfo)localObject4).getId();
                localObject1 = localObject3;
                if (str2.equals(str1)) {
                  localObject1 = localObject4;
                }
                k = isPackageDisappearing(((InputMethodInfo)localObject4).getPackageName());
                if (!isPackageModified(((InputMethodInfo)localObject4).getPackageName())) {
                  continue;
                }
                InputMethodManagerService.InputMethodFileManager.-wrap0(InputMethodManagerService.-get1(InputMethodManagerService.this), str2);
                continue;
                Slog.i("InputMethodManagerService", "Input method uninstalled, disabling: " + ((InputMethodInfo)localObject4).getComponent());
                InputMethodManagerService.this.setInputMethodEnabledLocked(((InputMethodInfo)localObject4).getId(), false);
                break;
              }
            }
            InputMethodManagerService.this.buildInputMethodListLocked(false);
            bool2 = false;
            bool1 = bool2;
            localObject4 = localObject1;
            if (localObject1 != null)
            {
              i = isPackageDisappearing(((InputMethodInfo)localObject1).getPackageName());
              if (i != 2)
              {
                bool1 = bool2;
                localObject4 = localObject1;
                if (i != 3) {}
              }
              else
              {
                localObject3 = null;
              }
            }
          }
          finally {}
          try
          {
            localObject4 = InputMethodManagerService.-get2(InputMethodManagerService.this).getServiceInfo(((InputMethodInfo)localObject1).getComponent(), 0, InputMethodManagerService.this.mSettings.getCurrentUserId());
            localObject3 = localObject4;
          }
          catch (RemoteException localRemoteException)
          {
            break label278;
          }
          boolean bool1 = bool2;
          Object localObject4 = localObject1;
          if (localObject3 == null)
          {
            Slog.i("InputMethodManagerService", "Current input method removed: " + str1);
            InputMethodManagerService.-wrap3(InputMethodManagerService.this, InputMethodManagerService.this.mCurToken, 0, InputMethodManagerService.this.mBackDisposition);
            bool1 = bool2;
            localObject4 = localObject1;
            if (!InputMethodManagerService.-wrap0(InputMethodManagerService.this))
            {
              bool1 = true;
              localObject4 = null;
              Slog.i("InputMethodManagerService", "Unsetting current input method");
              InputMethodManagerService.-wrap1(InputMethodManagerService.this, "");
            }
          }
          if (localObject4 == null) {
            bool2 = InputMethodManagerService.-wrap0(InputMethodManagerService.this);
          }
          for (;;)
          {
            if (bool2) {
              InputMethodManagerService.this.updateFromSettingsLocked(false);
            }
            return;
            bool2 = bool1;
            if (!bool1)
            {
              boolean bool3 = isPackageModified(((InputMethodInfo)localObject4).getPackageName());
              bool2 = bool1;
              if (bool3) {
                bool2 = true;
              }
            }
          }
        } while ((k == 2) || (k == 3));
        i += 1;
      }
    }
  }
  
  static class SessionState
  {
    InputChannel channel;
    final InputMethodManagerService.ClientState client;
    final IInputMethod method;
    IInputMethodSession session;
    
    SessionState(InputMethodManagerService.ClientState paramClientState, IInputMethod paramIInputMethod, IInputMethodSession paramIInputMethodSession, InputChannel paramInputChannel)
    {
      this.client = paramClientState;
      this.method = paramIInputMethod;
      this.session = paramIInputMethodSession;
      this.channel = paramInputChannel;
    }
    
    public String toString()
    {
      return "SessionState{uid " + this.client.uid + " pid " + this.client.pid + " method " + Integer.toHexString(System.identityHashCode(this.method)) + " session " + Integer.toHexString(System.identityHashCode(this.session)) + " channel " + this.channel + "}";
    }
  }
  
  class SettingsObserver
    extends ContentObserver
  {
    String mLastEnabled = "";
    boolean mRegistered = false;
    int mUserId;
    
    SettingsObserver(Handler paramHandler)
    {
      super();
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      paramBoolean = true;
      Uri localUri1 = Settings.Secure.getUriFor("show_ime_with_hard_keyboard");
      Uri localUri2 = Settings.Secure.getUriFor("accessibility_soft_keyboard_mode");
      for (;;)
      {
        synchronized (InputMethodManagerService.this.mMethodMap)
        {
          if (localUri1.equals(paramUri))
          {
            InputMethodManagerService.this.updateKeyboardFromSettingsLocked();
            return;
          }
          if (!localUri2.equals(paramUri)) {
            break label161;
          }
          paramUri = InputMethodManagerService.this;
          if (Settings.Secure.getIntForUser(InputMethodManagerService.this.mContext.getContentResolver(), "accessibility_soft_keyboard_mode", 0, this.mUserId) == 1)
          {
            InputMethodManagerService.-set0(paramUri, paramBoolean);
            if (!InputMethodManagerService.-get0(InputMethodManagerService.this)) {
              break label138;
            }
            paramBoolean = InputMethodManagerService.this.mShowRequested;
            InputMethodManagerService.this.hideCurrentInputLocked(0, null);
            InputMethodManagerService.this.mShowRequested = paramBoolean;
          }
        }
        paramBoolean = false;
        continue;
        label138:
        if (InputMethodManagerService.this.mShowRequested)
        {
          InputMethodManagerService.this.showCurrentInputLocked(1, null);
          continue;
          label161:
          paramBoolean = false;
          paramUri = InputMethodManagerService.this.mSettings.getEnabledInputMethodsStr();
          if (!this.mLastEnabled.equals(paramUri))
          {
            this.mLastEnabled = paramUri;
            paramBoolean = true;
          }
          InputMethodManagerService.this.updateInputMethodsFromSettingsLocked(paramBoolean);
        }
      }
    }
    
    public void registerContentObserverLocked(int paramInt)
    {
      if ((this.mRegistered) && (this.mUserId == paramInt)) {
        return;
      }
      ContentResolver localContentResolver = InputMethodManagerService.this.mContext.getContentResolver();
      if (this.mRegistered)
      {
        InputMethodManagerService.this.mContext.getContentResolver().unregisterContentObserver(this);
        this.mRegistered = false;
      }
      if (this.mUserId != paramInt)
      {
        this.mLastEnabled = "";
        this.mUserId = paramInt;
      }
      localContentResolver.registerContentObserver(Settings.Secure.getUriFor("default_input_method"), false, this, paramInt);
      localContentResolver.registerContentObserver(Settings.Secure.getUriFor("enabled_input_methods"), false, this, paramInt);
      localContentResolver.registerContentObserver(Settings.Secure.getUriFor("selected_input_method_subtype"), false, this, paramInt);
      localContentResolver.registerContentObserver(Settings.Secure.getUriFor("show_ime_with_hard_keyboard"), false, this, paramInt);
      localContentResolver.registerContentObserver(Settings.Secure.getUriFor("accessibility_soft_keyboard_mode"), false, this, paramInt);
      this.mRegistered = true;
    }
    
    public String toString()
    {
      return "SettingsObserver{mUserId=" + this.mUserId + " mRegistered=" + this.mRegistered + " mLastEnabled=" + this.mLastEnabled + "}";
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/InputMethodManagerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */