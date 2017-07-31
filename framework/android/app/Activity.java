package android.app;

import android.app.assist.AssistContent;
import android.content.ComponentCallbacks2;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.session.MediaController;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.BadParcelableException;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.os.StrictMode;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.TextKeyListener;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.BoostFramework;
import android.util.EventLog;
import android.util.Log;
import android.util.PrintWriterPrinter;
import android.util.SparseArray;
import android.util.SuperNotCalledException;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ContextThemeWrapper;
import android.view.DragAndDropPermissions;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.KeyEvent.Callback;
import android.view.KeyEvent.DispatcherState;
import android.view.KeyboardShortcutGroup;
import android.view.KeyboardShortcutInfo;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory2;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewManager;
import android.view.ViewRootImpl;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.Window.Callback;
import android.view.Window.OnWindowDismissedCallback;
import android.view.Window.WindowControllerCallback;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManagerGlobal;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;
import android.widget.Toolbar;
import com.android.internal.R.styleable;
import com.android.internal.app.IVoiceInteractor;
import com.android.internal.app.ToolbarActionBar;
import com.android.internal.app.WindowDecorActionBar;
import com.android.internal.policy.PhoneWindow;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Activity
  extends ContextThemeWrapper
  implements LayoutInflater.Factory2, Window.Callback, KeyEvent.Callback, View.OnCreateContextMenuListener, ComponentCallbacks2, Window.OnWindowDismissedCallback, Window.WindowControllerCallback
{
  private static final boolean DEBUG_LIFECYCLE = false;
  public static final int DEFAULT_KEYS_DIALER = 1;
  public static final int DEFAULT_KEYS_DISABLE = 0;
  public static final int DEFAULT_KEYS_SEARCH_GLOBAL = 4;
  public static final int DEFAULT_KEYS_SEARCH_LOCAL = 3;
  public static final int DEFAULT_KEYS_SHORTCUT = 2;
  public static final int DONT_FINISH_TASK_WITH_ACTIVITY = 0;
  public static final int FINISH_TASK_WITH_ACTIVITY = 2;
  public static final int FINISH_TASK_WITH_ROOT_ACTIVITY = 1;
  protected static final int[] FOCUSED_STATE_SET = { 16842908 };
  static final String FRAGMENTS_TAG = "android:fragments";
  private static final String HAS_CURENT_PERMISSIONS_REQUEST_KEY = "android:hasCurrentPermissionsRequest";
  private static final String KEYBOARD_SHORTCUTS_RECEIVER_CLASS_NAME = "com.android.systemui.statusbar.KeyboardShortcutsReceiver";
  private static final String KEYBOARD_SHORTCUTS_RECEIVER_PKG_NAME = "com.android.systemui";
  private static final String REQUEST_PERMISSIONS_WHO_PREFIX = "@android:requestPermissions:";
  public static final int RESULT_CANCELED = 0;
  public static final int RESULT_FIRST_USER = 1;
  public static final int RESULT_OK = -1;
  private static final String SAVED_DIALOGS_TAG = "android:savedDialogs";
  private static final String SAVED_DIALOG_ARGS_KEY_PREFIX = "android:dialog_args_";
  private static final String SAVED_DIALOG_IDS_KEY = "android:savedDialogIds";
  private static final String SAVED_DIALOG_KEY_PREFIX = "android:dialog_";
  private static final String TAG = "Activity";
  private static final String WINDOW_HIERARCHY_TAG = "android:viewHierarchyState";
  private static int[] mAsParamVal;
  private static int mDragBoostPossible;
  private static BoostFramework mPerf = null;
  private static int mPerfLockDuration;
  ActionBar mActionBar = null;
  private int mActionModeTypeStarting = 0;
  ActivityInfo mActivityInfo;
  ActivityTransitionState mActivityTransitionState = new ActivityTransitionState();
  private Application mApplication;
  boolean mCalled;
  private boolean mChangeCanvasToTranslucent;
  boolean mChangingConfigurations = false;
  private ComponentName mComponent;
  int mConfigChangeFlags;
  Configuration mCurrentConfig;
  View mDecor = null;
  private int mDefaultKeyMode = 0;
  private SpannableStringBuilder mDefaultKeySsb = null;
  private boolean mDestroyed;
  private boolean mDoReportFullyDrawn = true;
  private boolean mEatKeyUpEvent;
  String mEmbeddedID;
  private boolean mEnableDefaultActionBarUp;
  SharedElementCallback mEnterTransitionListener = SharedElementCallback.NULL_CALLBACK;
  SharedElementCallback mExitTransitionListener = SharedElementCallback.NULL_CALLBACK;
  boolean mFinished;
  final FragmentController mFragments = FragmentController.createController(new HostCallbacks());
  final Handler mHandler = new Handler();
  private boolean mHasCurrentPermissionsRequest;
  private int mIdent;
  private final Object mInstanceTracker = StrictMode.trackActivity(this);
  private Instrumentation mInstrumentation;
  Intent mIntent;
  NonConfigurationInstances mLastNonConfigurationInstances;
  ActivityThread mMainThread;
  private final ArrayList<ManagedCursor> mManagedCursors = new ArrayList();
  private SparseArray<ManagedDialog> mManagedDialogs;
  private MenuInflater mMenuInflater;
  Activity mParent;
  String mReferrer;
  int mResultCode = 0;
  Intent mResultData = null;
  boolean mResumed;
  private SearchEvent mSearchEvent;
  private SearchManager mSearchManager;
  boolean mStartedActivity;
  boolean mStopped;
  private ActivityManager.TaskDescription mTaskDescription = new ActivityManager.TaskDescription();
  boolean mTemporaryPause = false;
  private CharSequence mTitle;
  private int mTitleColor = 0;
  private boolean mTitleReady = false;
  private IBinder mToken;
  private TranslucentConversionListener mTranslucentCallback;
  private Thread mUiThread;
  boolean mVisibleBehind;
  boolean mVisibleFromClient = true;
  boolean mVisibleFromServer = false;
  private VoiceInteractor mVoiceInteractor;
  private Window mWindow;
  boolean mWindowAdded = false;
  private WindowManager mWindowManager;
  
  static
  {
    mDragBoostPossible = -1;
    mPerfLockDuration = -1;
  }
  
  private void cancelInputsAndStartExitTransition(Bundle paramBundle)
  {
    View localView = null;
    if (this.mWindow != null) {
      localView = this.mWindow.peekDecorView();
    }
    if (localView != null) {
      localView.cancelPendingInputEvents();
    }
    if ((paramBundle == null) || (isTopOfTask())) {
      return;
    }
    this.mActivityTransitionState.startExitOutTransition(this, paramBundle);
  }
  
  private Dialog createDialog(Integer paramInteger, Bundle paramBundle1, Bundle paramBundle2)
  {
    paramInteger = onCreateDialog(paramInteger.intValue(), paramBundle2);
    if (paramInteger == null) {
      return null;
    }
    paramInteger.dispatchOnCreate(paramBundle1);
    return paramInteger;
  }
  
  private void dispatchRequestPermissionsResult(int paramInt, Intent paramIntent)
  {
    this.mHasCurrentPermissionsRequest = false;
    String[] arrayOfString;
    if (paramIntent != null)
    {
      arrayOfString = paramIntent.getStringArrayExtra("android.content.pm.extra.REQUEST_PERMISSIONS_NAMES");
      if (paramIntent == null) {
        break label45;
      }
    }
    label45:
    for (paramIntent = paramIntent.getIntArrayExtra("android.content.pm.extra.REQUEST_PERMISSIONS_RESULTS");; paramIntent = new int[0])
    {
      onRequestPermissionsResult(paramInt, arrayOfString, paramIntent);
      return;
      arrayOfString = new String[0];
      break;
    }
  }
  
  private void dispatchRequestPermissionsResultToFragment(int paramInt, Intent paramIntent, Fragment paramFragment)
  {
    String[] arrayOfString;
    if (paramIntent != null)
    {
      arrayOfString = paramIntent.getStringArrayExtra("android.content.pm.extra.REQUEST_PERMISSIONS_NAMES");
      if (paramIntent == null) {
        break label43;
      }
    }
    label43:
    for (paramIntent = paramIntent.getIntArrayExtra("android.content.pm.extra.REQUEST_PERMISSIONS_RESULTS");; paramIntent = new int[0])
    {
      paramFragment.onRequestPermissionsResult(paramInt, arrayOfString, paramIntent);
      return;
      arrayOfString = new String[0];
      break;
    }
  }
  
  private void ensureSearchManager()
  {
    if (this.mSearchManager != null) {
      return;
    }
    this.mSearchManager = new SearchManager(this, null);
  }
  
  /* Error */
  private void finish(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 366	android/app/Activity:mParent	Landroid/app/Activity;
    //   4: ifnonnull +55 -> 59
    //   7: aload_0
    //   8: monitorenter
    //   9: aload_0
    //   10: getfield 246	android/app/Activity:mResultCode	I
    //   13: istore_2
    //   14: aload_0
    //   15: getfield 248	android/app/Activity:mResultData	Landroid/content/Intent;
    //   18: astore_3
    //   19: aload_0
    //   20: monitorexit
    //   21: aload_3
    //   22: ifnull +8 -> 30
    //   25: aload_3
    //   26: aload_0
    //   27: invokevirtual 370	android/content/Intent:prepareToLeaveProcess	(Landroid/content/Context;)V
    //   30: invokestatic 376	android/app/ActivityManagerNative:getDefault	()Landroid/app/IActivityManager;
    //   33: aload_0
    //   34: getfield 378	android/app/Activity:mToken	Landroid/os/IBinder;
    //   37: iload_2
    //   38: aload_3
    //   39: iload_1
    //   40: invokeinterface 384 5 0
    //   45: ifeq +8 -> 53
    //   48: aload_0
    //   49: iconst_1
    //   50: putfield 386	android/app/Activity:mFinished	Z
    //   53: return
    //   54: astore_3
    //   55: aload_0
    //   56: monitorexit
    //   57: aload_3
    //   58: athrow
    //   59: aload_0
    //   60: getfield 366	android/app/Activity:mParent	Landroid/app/Activity;
    //   63: aload_0
    //   64: invokevirtual 389	android/app/Activity:finishFromChild	(Landroid/app/Activity;)V
    //   67: return
    //   68: astore_3
    //   69: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	70	0	this	Activity
    //   0	70	1	paramInt	int
    //   13	25	2	i	int
    //   18	21	3	localIntent	Intent
    //   54	4	3	localObject	Object
    //   68	1	3	localRemoteException	RemoteException
    // Exception table:
    //   from	to	target	type
    //   9	19	54	finally
    //   25	30	68	android/os/RemoteException
    //   30	53	68	android/os/RemoteException
  }
  
  private static native String getDlWarning();
  
  private void initWindowDecorActionBar()
  {
    Window localWindow = getWindow();
    localWindow.getDecorView();
    if ((isChild()) || (!localWindow.hasFeature(8)) || (this.mActionBar != null)) {
      return;
    }
    this.mActionBar = new WindowDecorActionBar(this);
    this.mActionBar.setDefaultDisplayHomeAsUpEnabled(this.mEnableDefaultActionBarUp);
    this.mWindow.setDefaultIcon(this.mActivityInfo.getIconResource());
    this.mWindow.setDefaultLogo(this.mActivityInfo.getLogoResource());
  }
  
  private boolean isTopOfTask()
  {
    if ((this.mToken == null) || (this.mWindow == null)) {
      return false;
    }
    try
    {
      boolean bool = ActivityManagerNative.getDefault().isTopOfTask(getActivityToken());
      return bool;
    }
    catch (RemoteException localRemoteException) {}
    return false;
  }
  
  private IllegalArgumentException missingDialog(int paramInt)
  {
    return new IllegalArgumentException("no dialog with id " + paramInt + " was ever " + "shown via Activity#showDialog");
  }
  
  private native boolean nativeGetwalflag(String paramString);
  
  private void restoreHasCurrentPermissionRequest(Bundle paramBundle)
  {
    if (paramBundle != null) {
      this.mHasCurrentPermissionsRequest = paramBundle.getBoolean("android:hasCurrentPermissionsRequest", false);
    }
  }
  
  private void restoreManagedDialogs(Bundle paramBundle)
  {
    paramBundle = paramBundle.getBundle("android:savedDialogs");
    if (paramBundle == null) {
      return;
    }
    int[] arrayOfInt = paramBundle.getIntArray("android:savedDialogIds");
    int j = arrayOfInt.length;
    this.mManagedDialogs = new SparseArray(j);
    int i = 0;
    while (i < j)
    {
      Integer localInteger = Integer.valueOf(arrayOfInt[i]);
      Bundle localBundle = paramBundle.getBundle(savedDialogKeyFor(localInteger.intValue()));
      if (localBundle != null)
      {
        ManagedDialog localManagedDialog = new ManagedDialog(null);
        localManagedDialog.mArgs = paramBundle.getBundle(savedDialogArgsKeyFor(localInteger.intValue()));
        localManagedDialog.mDialog = createDialog(localInteger, localBundle, localManagedDialog.mArgs);
        if (localManagedDialog.mDialog != null)
        {
          this.mManagedDialogs.put(localInteger.intValue(), localManagedDialog);
          onPrepareDialog(localInteger.intValue(), localManagedDialog.mDialog, localManagedDialog.mArgs);
          localManagedDialog.mDialog.onRestoreInstanceState(localBundle);
        }
      }
      i += 1;
    }
  }
  
  private void saveManagedDialogs(Bundle paramBundle)
  {
    if (this.mManagedDialogs == null) {
      return;
    }
    int j = this.mManagedDialogs.size();
    if (j == 0) {
      return;
    }
    Bundle localBundle = new Bundle();
    int[] arrayOfInt = new int[this.mManagedDialogs.size()];
    int i = 0;
    while (i < j)
    {
      int k = this.mManagedDialogs.keyAt(i);
      arrayOfInt[i] = k;
      ManagedDialog localManagedDialog = (ManagedDialog)this.mManagedDialogs.valueAt(i);
      localBundle.putBundle(savedDialogKeyFor(k), localManagedDialog.mDialog.onSaveInstanceState());
      if (localManagedDialog.mArgs != null) {
        localBundle.putBundle(savedDialogArgsKeyFor(k), localManagedDialog.mArgs);
      }
      i += 1;
    }
    localBundle.putIntArray("android:savedDialogIds", arrayOfInt);
    paramBundle.putBundle("android:savedDialogs", localBundle);
  }
  
  private static String savedDialogArgsKeyFor(int paramInt)
  {
    return "android:dialog_args_" + paramInt;
  }
  
  private static String savedDialogKeyFor(int paramInt)
  {
    return "android:dialog_" + paramInt;
  }
  
  private void startIntentSenderForResultInner(IntentSender paramIntentSender, String paramString, int paramInt1, Intent paramIntent, int paramInt2, int paramInt3, Bundle paramBundle)
    throws IntentSender.SendIntentException
  {
    String str = null;
    if (paramIntent != null) {}
    try
    {
      paramIntent.migrateExtraStreamToClipData();
      paramIntent.prepareToLeaveProcess(this);
      str = paramIntent.resolveTypeIfNeeded(getContentResolver());
      paramInt2 = ActivityManagerNative.getDefault().startActivityIntentSender(this.mMainThread.getApplicationThread(), paramIntentSender, paramIntent, str, this.mToken, paramString, paramInt1, paramInt2, paramInt3, paramBundle);
      if (paramInt2 == -6) {
        throw new IntentSender.SendIntentException();
      }
    }
    catch (RemoteException paramIntentSender) {}
    for (;;)
    {
      if (paramInt1 >= 0) {
        this.mStartedActivity = true;
      }
      return;
      Instrumentation.checkStartActivityResult(paramInt2, null);
    }
  }
  
  private void storeHasCurrentPermissionRequest(Bundle paramBundle)
  {
    if ((paramBundle != null) && (this.mHasCurrentPermissionsRequest)) {
      paramBundle.putBoolean("android:hasCurrentPermissionsRequest", true);
    }
  }
  
  private Bundle transferSpringboardActivityOptions(Bundle paramBundle)
  {
    if ((paramBundle != null) || (this.mWindow == null) || (this.mWindow.isActive())) {}
    ActivityOptions localActivityOptions;
    do
    {
      return paramBundle;
      localActivityOptions = getActivityOptions();
    } while ((localActivityOptions == null) || (localActivityOptions.getAnimationType() != 5));
    return localActivityOptions.toBundle();
  }
  
  public void addContentView(View paramView, ViewGroup.LayoutParams paramLayoutParams)
  {
    getWindow().addContentView(paramView, paramLayoutParams);
    initWindowDecorActionBar();
  }
  
  final void attach(Context paramContext, ActivityThread paramActivityThread, Instrumentation paramInstrumentation, IBinder paramIBinder, int paramInt, Application paramApplication, Intent paramIntent, ActivityInfo paramActivityInfo, CharSequence paramCharSequence, Activity paramActivity, String paramString1, NonConfigurationInstances paramNonConfigurationInstances, Configuration paramConfiguration, String paramString2, IVoiceInteractor paramIVoiceInteractor, Window paramWindow)
  {
    attachBaseContext(paramContext);
    this.mFragments.attachHost(null);
    this.mWindow = new PhoneWindow(this, paramWindow);
    this.mWindow.setWindowControllerCallback(this);
    this.mWindow.setCallback(this);
    this.mWindow.setOnWindowDismissedCallback(this);
    this.mWindow.getLayoutInflater().setPrivateFactory(this);
    if (paramActivityInfo.softInputMode != 0) {
      this.mWindow.setSoftInputMode(paramActivityInfo.softInputMode);
    }
    if (paramActivityInfo.uiOptions != 0) {
      this.mWindow.setUiOptions(paramActivityInfo.uiOptions);
    }
    this.mUiThread = Thread.currentThread();
    this.mMainThread = paramActivityThread;
    this.mInstrumentation = paramInstrumentation;
    this.mToken = paramIBinder;
    this.mIdent = paramInt;
    this.mApplication = paramApplication;
    this.mIntent = paramIntent;
    this.mReferrer = paramString2;
    this.mComponent = paramIntent.getComponent();
    this.mActivityInfo = paramActivityInfo;
    this.mTitle = paramCharSequence;
    this.mParent = paramActivity;
    this.mEmbeddedID = paramString1;
    this.mLastNonConfigurationInstances = paramNonConfigurationInstances;
    if (paramIVoiceInteractor != null)
    {
      if (paramNonConfigurationInstances != null) {
        this.mVoiceInteractor = paramNonConfigurationInstances.voiceInteractor;
      }
    }
    else
    {
      paramActivityThread = this.mWindow;
      paramContext = (WindowManager)paramContext.getSystemService("window");
      paramInstrumentation = this.mToken;
      paramIBinder = this.mComponent.flattenToString();
      if ((paramActivityInfo.flags & 0x200) == 0) {
        break label322;
      }
    }
    label322:
    for (boolean bool = true;; bool = false)
    {
      paramActivityThread.setWindowManager(paramContext, paramInstrumentation, paramIBinder, bool);
      if (this.mParent != null) {
        this.mWindow.setContainer(this.mParent.getWindow());
      }
      this.mWindowManager = this.mWindow.getWindowManager();
      this.mCurrentConfig = paramConfiguration;
      return;
      this.mVoiceInteractor = new VoiceInteractor(paramIVoiceInteractor, this, this, Looper.myLooper());
      break;
    }
  }
  
  public boolean canStartActivityForResult()
  {
    return true;
  }
  
  public void closeContextMenu()
  {
    if (this.mWindow.hasFeature(6)) {
      this.mWindow.closePanel(6);
    }
  }
  
  public void closeOptionsMenu()
  {
    if (this.mWindow.hasFeature(0)) {
      this.mWindow.closePanel(0);
    }
  }
  
  public void convertFromTranslucent()
  {
    try
    {
      this.mTranslucentCallback = null;
      if (ActivityManagerNative.getDefault().convertFromTranslucent(this.mToken)) {
        WindowManagerGlobal.getInstance().changeCanvasOpacity(this.mToken, true);
      }
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public boolean convertToTranslucent(TranslucentConversionListener paramTranslucentConversionListener, ActivityOptions paramActivityOptions)
  {
    try
    {
      this.mTranslucentCallback = paramTranslucentConversionListener;
      this.mChangeCanvasToTranslucent = ActivityManagerNative.getDefault().convertToTranslucent(this.mToken, paramActivityOptions);
      WindowManagerGlobal.getInstance().changeCanvasOpacity(this.mToken, false);
      bool = true;
    }
    catch (RemoteException paramTranslucentConversionListener)
    {
      for (;;)
      {
        this.mChangeCanvasToTranslucent = false;
        boolean bool = false;
      }
    }
    if ((!this.mChangeCanvasToTranslucent) && (this.mTranslucentCallback != null)) {
      this.mTranslucentCallback.onTranslucentConversionComplete(bool);
    }
    return this.mChangeCanvasToTranslucent;
  }
  
  public PendingIntent createPendingResult(int paramInt1, Intent paramIntent, int paramInt2)
  {
    String str1 = getPackageName();
    try
    {
      paramIntent.prepareToLeaveProcess(this);
      IActivityManager localIActivityManager = ActivityManagerNative.getDefault();
      if (this.mParent == null) {}
      for (IBinder localIBinder = this.mToken;; localIBinder = this.mParent.mToken)
      {
        String str2 = this.mEmbeddedID;
        int i = UserHandle.myUserId();
        paramIntent = localIActivityManager.getIntentSender(3, str1, localIBinder, str2, paramInt1, new Intent[] { paramIntent }, null, paramInt2, null, i);
        if (paramIntent == null) {
          break;
        }
        return new PendingIntent(paramIntent);
      }
      return null;
    }
    catch (RemoteException paramIntent) {}
    return null;
  }
  
  @Deprecated
  public final void dismissDialog(int paramInt)
  {
    if (this.mManagedDialogs == null) {
      throw missingDialog(paramInt);
    }
    ManagedDialog localManagedDialog = (ManagedDialog)this.mManagedDialogs.get(paramInt);
    if (localManagedDialog == null) {
      throw missingDialog(paramInt);
    }
    localManagedDialog.mDialog.dismiss();
  }
  
  public final void dismissKeyboardShortcutsHelper()
  {
    Intent localIntent = new Intent("android.intent.action.DISMISS_KEYBOARD_SHORTCUTS");
    localIntent.setComponent(new ComponentName("com.android.systemui", "com.android.systemui.statusbar.KeyboardShortcutsReceiver"));
    sendBroadcast(localIntent);
  }
  
  void dispatchActivityResult(String paramString, int paramInt1, int paramInt2, Intent paramIntent)
  {
    this.mFragments.noteStateNotSaved();
    if (paramString == null)
    {
      onActivityResult(paramInt1, paramInt2, paramIntent);
      return;
    }
    do
    {
      Iterator localIterator;
      do
      {
        while (paramString.startsWith("@android:requestPermissions:"))
        {
          paramString = paramString.substring("@android:requestPermissions:".length());
          if (TextUtils.isEmpty(paramString))
          {
            dispatchRequestPermissionsResult(paramInt1, paramIntent);
            return;
          }
          paramString = this.mFragments.findFragmentByWho(paramString);
          if (paramString != null)
          {
            dispatchRequestPermissionsResultToFragment(paramInt1, paramIntent, paramString);
            return;
          }
        }
        if (!paramString.startsWith("@android:view:")) {
          break;
        }
        localIterator = WindowManagerGlobal.getInstance().getRootViews(getActivityToken()).iterator();
      } while (!localIterator.hasNext());
      ViewRootImpl localViewRootImpl = (ViewRootImpl)localIterator.next();
      if ((localViewRootImpl.getView() == null) || (!localViewRootImpl.getView().dispatchActivityResult(paramString, paramInt1, paramInt2, paramIntent))) {
        break;
      }
      return;
      paramString = this.mFragments.findFragmentByWho(paramString);
    } while (paramString == null);
    paramString.onActivityResult(paramInt1, paramInt2, paramIntent);
  }
  
  public void dispatchEnterAnimationComplete()
  {
    onEnterAnimationComplete();
    if ((getWindow() != null) && (getWindow().getDecorView() != null)) {
      getWindow().getDecorView().getViewTreeObserver().dispatchOnEnterAnimationComplete();
    }
  }
  
  public boolean dispatchGenericMotionEvent(MotionEvent paramMotionEvent)
  {
    onUserInteraction();
    if (getWindow().superDispatchGenericMotionEvent(paramMotionEvent)) {
      return true;
    }
    return onGenericMotionEvent(paramMotionEvent);
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    KeyEvent.DispatcherState localDispatcherState = null;
    onUserInteraction();
    if ((paramKeyEvent.getKeyCode() == 82) && (this.mActionBar != null) && (this.mActionBar.onMenuKeyEvent(paramKeyEvent))) {
      return true;
    }
    if ((paramKeyEvent.isCtrlPressed()) && (paramKeyEvent.getUnicodeChar(paramKeyEvent.getMetaState() & 0x8FFF) == 60))
    {
      int i = paramKeyEvent.getAction();
      if (i == 0)
      {
        localObject = getActionBar();
        if ((localObject != null) && (((ActionBar)localObject).isShowing()) && (((ActionBar)localObject).requestFocus()))
        {
          this.mEatKeyUpEvent = true;
          return true;
        }
      }
      else if ((i == 1) && (this.mEatKeyUpEvent))
      {
        this.mEatKeyUpEvent = false;
        return true;
      }
    }
    Window localWindow = getWindow();
    if (localWindow.superDispatchKeyEvent(paramKeyEvent)) {
      return true;
    }
    View localView = this.mDecor;
    Object localObject = localView;
    if (localView == null) {
      localObject = localWindow.getDecorView();
    }
    if (localObject != null) {
      localDispatcherState = ((View)localObject).getKeyDispatcherState();
    }
    return paramKeyEvent.dispatch(this, localDispatcherState, this);
  }
  
  public boolean dispatchKeyShortcutEvent(KeyEvent paramKeyEvent)
  {
    onUserInteraction();
    if (getWindow().superDispatchKeyShortcutEvent(paramKeyEvent)) {
      return true;
    }
    return onKeyShortcut(paramKeyEvent.getKeyCode(), paramKeyEvent);
  }
  
  final void dispatchMultiWindowModeChanged(boolean paramBoolean)
  {
    this.mFragments.dispatchMultiWindowModeChanged(paramBoolean);
    if (this.mWindow != null) {
      this.mWindow.onMultiWindowModeChanged();
    }
    onMultiWindowModeChanged(paramBoolean);
  }
  
  final void dispatchPictureInPictureModeChanged(boolean paramBoolean)
  {
    this.mFragments.dispatchPictureInPictureModeChanged(paramBoolean);
    onPictureInPictureModeChanged(paramBoolean);
  }
  
  public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    boolean bool2 = false;
    paramAccessibilityEvent.setClassName(getClass().getName());
    paramAccessibilityEvent.setPackageName(getPackageName());
    Object localObject = getWindow().getAttributes();
    boolean bool1 = bool2;
    if (((ViewGroup.LayoutParams)localObject).width == -1)
    {
      bool1 = bool2;
      if (((ViewGroup.LayoutParams)localObject).height == -1) {
        bool1 = true;
      }
    }
    paramAccessibilityEvent.setFullScreen(bool1);
    localObject = getTitle();
    if (!TextUtils.isEmpty((CharSequence)localObject)) {
      paramAccessibilityEvent.getText().add(localObject);
    }
    return true;
  }
  
  public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = 0;
    Object localObject;
    String[] arrayOfString;
    int j;
    if (mDragBoostPossible == -1)
    {
      mDragBoostPossible = 0;
      localObject = getPackageName();
      arrayOfString = getResources().getStringArray(17236052);
      if (arrayOfString != null) {
        j = arrayOfString.length;
      }
    }
    for (;;)
    {
      if (i < j)
      {
        if (((String)localObject).indexOf(arrayOfString[i]) != -1) {
          mDragBoostPossible = 1;
        }
      }
      else
      {
        localObject = getApplicationContext();
        if (mPerf == null) {
          mPerf = new BoostFramework();
        }
        boolean bool = mPerf.boostOverride((Context)localObject, paramMotionEvent, getResources().getDisplayMetrics());
        if ((mDragBoostPossible == 1) && (!bool))
        {
          if (mPerf == null) {
            mPerf = new BoostFramework();
          }
          if (mPerfLockDuration == -1)
          {
            mPerfLockDuration = getResources().getInteger(17694898);
            mAsParamVal = getResources().getIntArray(17236053);
          }
          mPerf.perfLockAcquireTouch(paramMotionEvent, getResources().getDisplayMetrics(), mPerfLockDuration, mAsParamVal);
        }
        if (paramMotionEvent.getAction() == 0) {
          onUserInteraction();
        }
        if (!getWindow().superDispatchTouchEvent(paramMotionEvent)) {
          break;
        }
        return true;
      }
      i += 1;
    }
    return onTouchEvent(paramMotionEvent);
  }
  
  public boolean dispatchTrackballEvent(MotionEvent paramMotionEvent)
  {
    onUserInteraction();
    if (getWindow().superDispatchTrackballEvent(paramMotionEvent)) {
      return true;
    }
    return onTrackballEvent(paramMotionEvent);
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    dumpInner(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
  }
  
  void dumpInner(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("Local Activity ");
    paramPrintWriter.print(Integer.toHexString(System.identityHashCode(this)));
    paramPrintWriter.println(" State:");
    String str = paramString + "  ";
    paramPrintWriter.print(str);
    paramPrintWriter.print("mResumed=");
    paramPrintWriter.print(this.mResumed);
    paramPrintWriter.print(" mStopped=");
    paramPrintWriter.print(this.mStopped);
    paramPrintWriter.print(" mFinished=");
    paramPrintWriter.println(this.mFinished);
    paramPrintWriter.print(str);
    paramPrintWriter.print("mChangingConfigurations=");
    paramPrintWriter.println(this.mChangingConfigurations);
    paramPrintWriter.print(str);
    paramPrintWriter.print("mCurrentConfig=");
    paramPrintWriter.println(this.mCurrentConfig);
    this.mFragments.dumpLoaders(str, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    this.mFragments.getFragmentManager().dump(str, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    if (this.mVoiceInteractor != null) {
      this.mVoiceInteractor.dump(str, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
    if ((getWindow() != null) && (getWindow().peekDecorView() != null) && (getWindow().peekDecorView().getViewRootImpl() != null)) {
      getWindow().peekDecorView().getViewRootImpl().dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
    this.mHandler.getLooper().dump(new PrintWriterPrinter(paramPrintWriter), paramString);
  }
  
  public void enterPictureInPictureMode()
  {
    try
    {
      ActivityManagerNative.getDefault().enterPictureInPictureMode(this.mToken);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public void enterPictureInPictureModeIfPossible()
  {
    if (this.mActivityInfo.resizeMode == 3) {
      enterPictureInPictureMode();
    }
  }
  
  public void exitFreeformMode()
    throws RemoteException
  {
    ActivityManagerNative.getDefault().exitFreeformMode(this.mToken);
  }
  
  public View findViewById(int paramInt)
  {
    return getWindow().findViewById(paramInt);
  }
  
  public void finish()
  {
    finish(0);
  }
  
  public void finishActivity(int paramInt)
  {
    if (this.mParent == null) {}
    try
    {
      ActivityManagerNative.getDefault().finishSubActivity(this.mToken, this.mEmbeddedID, paramInt);
      return;
    }
    catch (RemoteException localRemoteException) {}
    this.mParent.finishActivityFromChild(this, paramInt);
    return;
  }
  
  public void finishActivityFromChild(Activity paramActivity, int paramInt)
  {
    try
    {
      ActivityManagerNative.getDefault().finishSubActivity(this.mToken, paramActivity.mEmbeddedID, paramInt);
      return;
    }
    catch (RemoteException paramActivity) {}
  }
  
  public void finishAffinity()
  {
    if (this.mParent != null) {
      throw new IllegalStateException("Can not be called from an embedded activity");
    }
    if ((this.mResultCode != 0) || (this.mResultData != null)) {
      throw new IllegalStateException("Can not be called to deliver a result");
    }
    try
    {
      if (ActivityManagerNative.getDefault().finishActivityAffinity(this.mToken)) {
        this.mFinished = true;
      }
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public void finishAfterTransition()
  {
    if (!this.mActivityTransitionState.startExitBackTransition(this)) {
      finish();
    }
  }
  
  public void finishAndRemoveTask()
  {
    finish(1);
  }
  
  public void finishFromChild(Activity paramActivity)
  {
    finish();
  }
  
  public ActionBar getActionBar()
  {
    initWindowDecorActionBar();
    return this.mActionBar;
  }
  
  ActivityOptions getActivityOptions()
  {
    try
    {
      ActivityOptions localActivityOptions = ActivityManagerNative.getDefault().getActivityOptions(this.mToken);
      return localActivityOptions;
    }
    catch (RemoteException localRemoteException) {}
    return null;
  }
  
  public final IBinder getActivityToken()
  {
    if (this.mParent != null) {
      return this.mParent.getActivityToken();
    }
    return this.mToken;
  }
  
  public final Application getApplication()
  {
    return this.mApplication;
  }
  
  public ComponentName getCallingActivity()
  {
    try
    {
      ComponentName localComponentName = ActivityManagerNative.getDefault().getCallingActivity(this.mToken);
      return localComponentName;
    }
    catch (RemoteException localRemoteException) {}
    return null;
  }
  
  public String getCallingPackage()
  {
    try
    {
      String str = ActivityManagerNative.getDefault().getCallingPackage(this.mToken);
      return str;
    }
    catch (RemoteException localRemoteException) {}
    return null;
  }
  
  public int getChangingConfigurations()
  {
    return this.mConfigChangeFlags;
  }
  
  public ComponentName getComponentName()
  {
    return this.mComponent;
  }
  
  public Scene getContentScene()
  {
    return getWindow().getContentScene();
  }
  
  public TransitionManager getContentTransitionManager()
  {
    return getWindow().getTransitionManager();
  }
  
  public View getCurrentFocus()
  {
    View localView = null;
    if (this.mWindow != null) {
      localView = this.mWindow.getCurrentFocus();
    }
    return localView;
  }
  
  public FragmentManager getFragmentManager()
  {
    return this.mFragments.getFragmentManager();
  }
  
  public Intent getIntent()
  {
    return this.mIntent;
  }
  
  HashMap<String, Object> getLastNonConfigurationChildInstances()
  {
    HashMap localHashMap = null;
    if (this.mLastNonConfigurationInstances != null) {
      localHashMap = this.mLastNonConfigurationInstances.children;
    }
    return localHashMap;
  }
  
  public Object getLastNonConfigurationInstance()
  {
    Object localObject = null;
    if (this.mLastNonConfigurationInstances != null) {
      localObject = this.mLastNonConfigurationInstances.activity;
    }
    return localObject;
  }
  
  public LayoutInflater getLayoutInflater()
  {
    return getWindow().getLayoutInflater();
  }
  
  public LoaderManager getLoaderManager()
  {
    return this.mFragments.getLoaderManager();
  }
  
  public String getLocalClassName()
  {
    String str1 = getPackageName();
    String str2 = this.mComponent.getClassName();
    int i = str1.length();
    if ((!str2.startsWith(str1)) || (str2.length() <= i)) {}
    while (str2.charAt(i) != '.') {
      return str2;
    }
    return str2.substring(i + 1);
  }
  
  public final MediaController getMediaController()
  {
    return getWindow().getMediaController();
  }
  
  public MenuInflater getMenuInflater()
  {
    if (this.mMenuInflater == null)
    {
      initWindowDecorActionBar();
      if (this.mActionBar == null) {
        break label42;
      }
    }
    label42:
    for (this.mMenuInflater = new MenuInflater(this.mActionBar.getThemedContext(), this);; this.mMenuInflater = new MenuInflater(this)) {
      return this.mMenuInflater;
    }
  }
  
  public final Activity getParent()
  {
    return this.mParent;
  }
  
  public Intent getParentActivityIntent()
  {
    String str = this.mActivityInfo.parentActivityName;
    if (TextUtils.isEmpty(str)) {
      return null;
    }
    Object localObject = new ComponentName(this, str);
    try
    {
      if (getPackageManager().getActivityInfo((ComponentName)localObject, 0).parentActivityName == null) {
        return Intent.makeMainActivity((ComponentName)localObject);
      }
      localObject = new Intent().setComponent((ComponentName)localObject);
      return (Intent)localObject;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      Log.e("Activity", "getParentActivityIntent: bad parentActivityName '" + str + "' in manifest");
    }
    return null;
  }
  
  public SharedPreferences getPreferences(int paramInt)
  {
    return getSharedPreferences(getLocalClassName(), paramInt);
  }
  
  public Uri getReferrer()
  {
    Object localObject = getIntent();
    try
    {
      Uri localUri = (Uri)((Intent)localObject).getParcelableExtra("android.intent.extra.REFERRER");
      if (localUri != null) {
        return localUri;
      }
      localObject = ((Intent)localObject).getStringExtra("android.intent.extra.REFERRER_NAME");
      if (localObject != null)
      {
        localObject = Uri.parse((String)localObject);
        return (Uri)localObject;
      }
    }
    catch (BadParcelableException localBadParcelableException)
    {
      Log.w("Activity", "Cannot read referrer from intent; intent extras contain unknown custom Parcelable objects");
      if (this.mReferrer != null) {
        return new Uri.Builder().scheme("android-app").authority(this.mReferrer).build();
      }
    }
    return null;
  }
  
  public int getRequestedOrientation()
  {
    if (this.mParent == null) {}
    try
    {
      int i = ActivityManagerNative.getDefault().getRequestedOrientation(this.mToken);
      return i;
    }
    catch (RemoteException localRemoteException) {}
    return this.mParent.getRequestedOrientation();
    return -1;
  }
  
  public final SearchEvent getSearchEvent()
  {
    return this.mSearchEvent;
  }
  
  public Object getSystemService(String paramString)
  {
    if (getBaseContext() == null) {
      throw new IllegalStateException("System services not available to Activities before onCreate()");
    }
    if ("window".equals(paramString)) {
      return this.mWindowManager;
    }
    if ("search".equals(paramString))
    {
      ensureSearchManager();
      return this.mSearchManager;
    }
    return super.getSystemService(paramString);
  }
  
  public int getTaskId()
  {
    try
    {
      int i = ActivityManagerNative.getDefault().getTaskForActivity(this.mToken, false);
      return i;
    }
    catch (RemoteException localRemoteException) {}
    return -1;
  }
  
  public final CharSequence getTitle()
  {
    return this.mTitle;
  }
  
  public final int getTitleColor()
  {
    return this.mTitleColor;
  }
  
  public VoiceInteractor getVoiceInteractor()
  {
    return this.mVoiceInteractor;
  }
  
  public final int getVolumeControlStream()
  {
    return getWindow().getVolumeControlStream();
  }
  
  public Window getWindow()
  {
    return this.mWindow;
  }
  
  public WindowManager getWindowManager()
  {
    return this.mWindowManager;
  }
  
  public int getWindowStackId()
    throws RemoteException
  {
    return ActivityManagerNative.getDefault().getActivityStackId(this.mToken);
  }
  
  public boolean hasWindowFocus()
  {
    Object localObject = getWindow();
    if (localObject != null)
    {
      localObject = ((Window)localObject).getDecorView();
      if (localObject != null) {
        return ((View)localObject).hasWindowFocus();
      }
    }
    return false;
  }
  
  public void invalidateOptionsMenu()
  {
    if ((!this.mWindow.hasFeature(0)) || ((this.mActionBar != null) && (this.mActionBar.invalidateOptionsMenu()))) {
      return;
    }
    this.mWindow.invalidatePanelMenu(0);
  }
  
  public boolean isBackgroundVisibleBehind()
  {
    try
    {
      boolean bool = ActivityManagerNative.getDefault().isBackgroundVisibleBehind(this.mToken);
      return bool;
    }
    catch (RemoteException localRemoteException) {}
    return false;
  }
  
  public boolean isChangingConfigurations()
  {
    return this.mChangingConfigurations;
  }
  
  public final boolean isChild()
  {
    return this.mParent != null;
  }
  
  public boolean isDestroyed()
  {
    return this.mDestroyed;
  }
  
  public boolean isFinishing()
  {
    return this.mFinished;
  }
  
  public boolean isImmersive()
  {
    try
    {
      boolean bool = ActivityManagerNative.getDefault().isImmersive(this.mToken);
      return bool;
    }
    catch (RemoteException localRemoteException) {}
    return false;
  }
  
  public boolean isInMultiWindowMode()
  {
    try
    {
      boolean bool = ActivityManagerNative.getDefault().isInMultiWindowMode(this.mToken);
      return bool;
    }
    catch (RemoteException localRemoteException) {}
    return false;
  }
  
  public boolean isInPictureInPictureMode()
  {
    try
    {
      boolean bool = ActivityManagerNative.getDefault().isInPictureInPictureMode(this.mToken);
      return bool;
    }
    catch (RemoteException localRemoteException) {}
    return false;
  }
  
  public boolean isLocalVoiceInteractionSupported()
  {
    try
    {
      boolean bool = ActivityManagerNative.getDefault().supportsLocalVoiceInteraction();
      return bool;
    }
    catch (RemoteException localRemoteException) {}
    return false;
  }
  
  public boolean isOverlayWithDecorCaptionEnabled()
  {
    return this.mWindow.isOverlayWithDecorCaptionEnabled();
  }
  
  public final boolean isResumed()
  {
    return this.mResumed;
  }
  
  public boolean isTaskRoot()
  {
    try
    {
      int i = ActivityManagerNative.getDefault().getTaskForActivity(this.mToken, true);
      return i >= 0;
    }
    catch (RemoteException localRemoteException) {}
    return false;
  }
  
  public boolean isVoiceInteraction()
  {
    return this.mVoiceInteractor != null;
  }
  
  public boolean isVoiceInteractionRoot()
  {
    boolean bool = false;
    try
    {
      if (this.mVoiceInteractor != null) {
        bool = ActivityManagerNative.getDefault().isRootVoiceInteraction(this.mToken);
      }
      return bool;
    }
    catch (RemoteException localRemoteException) {}
    return false;
  }
  
  void makeVisible()
  {
    if (!this.mWindowAdded)
    {
      getWindowManager().addView(this.mDecor, getWindow().getAttributes());
      this.mWindowAdded = true;
    }
    this.mDecor.setVisibility(0);
  }
  
  @Deprecated
  public final Cursor managedQuery(Uri paramUri, String[] paramArrayOfString, String paramString1, String paramString2)
  {
    paramUri = getContentResolver().query(paramUri, paramArrayOfString, paramString1, null, paramString2);
    if (paramUri != null) {
      startManagingCursor(paramUri);
    }
    return paramUri;
  }
  
  @Deprecated
  public final Cursor managedQuery(Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2)
  {
    paramUri = getContentResolver().query(paramUri, paramArrayOfString1, paramString1, paramArrayOfString2, paramString2);
    if (paramUri != null) {
      startManagingCursor(paramUri);
    }
    return paramUri;
  }
  
  public boolean moveTaskToBack(boolean paramBoolean)
  {
    try
    {
      paramBoolean = ActivityManagerNative.getDefault().moveActivityTaskToBack(this.mToken, paramBoolean);
      return paramBoolean;
    }
    catch (RemoteException localRemoteException) {}
    return false;
  }
  
  /* Error */
  public boolean navigateUpTo(Intent paramIntent)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 366	android/app/Activity:mParent	Landroid/app/Activity;
    //   4: ifnonnull +104 -> 108
    //   7: aload_1
    //   8: astore 4
    //   10: aload_1
    //   11: invokevirtual 681	android/content/Intent:getComponent	()Landroid/content/ComponentName;
    //   14: ifnonnull +38 -> 52
    //   17: aload_1
    //   18: aload_0
    //   19: invokevirtual 1289	android/app/Activity:getPackageManager	()Landroid/content/pm/PackageManager;
    //   22: invokevirtual 1473	android/content/Intent:resolveActivity	(Landroid/content/pm/PackageManager;)Landroid/content/ComponentName;
    //   25: astore 5
    //   27: aload 5
    //   29: ifnonnull +5 -> 34
    //   32: iconst_0
    //   33: ireturn
    //   34: new 331	android/content/Intent
    //   37: dup
    //   38: aload_1
    //   39: invokespecial 1475	android/content/Intent:<init>	(Landroid/content/Intent;)V
    //   42: astore 4
    //   44: aload 4
    //   46: aload 5
    //   48: invokevirtual 811	android/content/Intent:setComponent	(Landroid/content/ComponentName;)Landroid/content/Intent;
    //   51: pop
    //   52: aload_0
    //   53: monitorenter
    //   54: aload_0
    //   55: getfield 246	android/app/Activity:mResultCode	I
    //   58: istore_2
    //   59: aload_0
    //   60: getfield 248	android/app/Activity:mResultData	Landroid/content/Intent;
    //   63: astore_1
    //   64: aload_0
    //   65: monitorexit
    //   66: aload_1
    //   67: ifnull +8 -> 75
    //   70: aload_1
    //   71: aload_0
    //   72: invokevirtual 370	android/content/Intent:prepareToLeaveProcess	(Landroid/content/Context;)V
    //   75: aload 4
    //   77: aload_0
    //   78: invokevirtual 370	android/content/Intent:prepareToLeaveProcess	(Landroid/content/Context;)V
    //   81: invokestatic 376	android/app/ActivityManagerNative:getDefault	()Landroid/app/IActivityManager;
    //   84: aload_0
    //   85: getfield 378	android/app/Activity:mToken	Landroid/os/IBinder;
    //   88: aload 4
    //   90: iload_2
    //   91: aload_1
    //   92: invokeinterface 1478 5 0
    //   97: istore_3
    //   98: iload_3
    //   99: ireturn
    //   100: astore_1
    //   101: aload_0
    //   102: monitorexit
    //   103: aload_1
    //   104: athrow
    //   105: astore_1
    //   106: iconst_0
    //   107: ireturn
    //   108: aload_0
    //   109: getfield 366	android/app/Activity:mParent	Landroid/app/Activity;
    //   112: aload_0
    //   113: aload_1
    //   114: invokevirtual 1482	android/app/Activity:navigateUpToFromChild	(Landroid/app/Activity;Landroid/content/Intent;)Z
    //   117: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	118	0	this	Activity
    //   0	118	1	paramIntent	Intent
    //   58	33	2	i	int
    //   97	2	3	bool	boolean
    //   8	81	4	localIntent	Intent
    //   25	22	5	localComponentName	ComponentName
    // Exception table:
    //   from	to	target	type
    //   54	64	100	finally
    //   75	98	105	android/os/RemoteException
  }
  
  public boolean navigateUpToFromChild(Activity paramActivity, Intent paramIntent)
  {
    return navigateUpTo(paramIntent);
  }
  
  public void onActionModeFinished(ActionMode paramActionMode) {}
  
  public void onActionModeStarted(ActionMode paramActionMode) {}
  
  public void onActivityReenter(int paramInt, Intent paramIntent) {}
  
  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent) {}
  
  protected void onApplyThemeResource(Resources.Theme paramTheme, int paramInt, boolean paramBoolean)
  {
    if (this.mParent == null) {
      super.onApplyThemeResource(paramTheme, paramInt, paramBoolean);
    }
    for (;;)
    {
      paramTheme = paramTheme.obtainStyledAttributes(R.styleable.ActivityTaskDescription);
      if (this.mTaskDescription.getPrimaryColor() == 0)
      {
        paramInt = paramTheme.getColor(1, 0);
        if ((paramInt != 0) && (Color.alpha(paramInt) == 255)) {
          this.mTaskDescription.setPrimaryColor(paramInt);
        }
      }
      if (this.mTaskDescription.getBackgroundColor() == 0)
      {
        paramInt = paramTheme.getColor(0, 0);
        if ((paramInt != 0) && (Color.alpha(paramInt) == 255)) {
          this.mTaskDescription.setBackgroundColor(paramInt);
        }
      }
      paramTheme.recycle();
      setTaskDescription(this.mTaskDescription);
      return;
      try
      {
        paramTheme.setTo(this.mParent.getTheme());
        paramTheme.applyStyle(paramInt, false);
      }
      catch (Exception localException)
      {
        for (;;) {}
      }
    }
  }
  
  public void onAttachFragment(Fragment paramFragment) {}
  
  public void onAttachedToWindow() {}
  
  public void onBackPressed()
  {
    if ((this.mActionBar != null) && (this.mActionBar.collapseActionView())) {
      return;
    }
    if (!this.mFragments.getFragmentManager().popBackStackImmediate()) {
      finishAfterTransition();
    }
  }
  
  public void onBackgroundVisibleBehindChanged(boolean paramBoolean) {}
  
  protected void onChildTitleChanged(Activity paramActivity, CharSequence paramCharSequence) {}
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    this.mCalled = true;
    this.mFragments.dispatchConfigurationChanged(paramConfiguration);
    if (this.mWindow != null) {
      this.mWindow.onConfigurationChanged(paramConfiguration);
    }
    if (this.mActionBar != null) {
      this.mActionBar.onConfigurationChanged(paramConfiguration);
    }
  }
  
  public void onContentChanged() {}
  
  public boolean onContextItemSelected(MenuItem paramMenuItem)
  {
    if (this.mParent != null) {
      return this.mParent.onContextItemSelected(paramMenuItem);
    }
    return false;
  }
  
  public void onContextMenuClosed(Menu paramMenu)
  {
    if (this.mParent != null) {
      this.mParent.onContextMenuClosed(paramMenu);
    }
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    FragmentManagerNonConfig localFragmentManagerNonConfig = null;
    if (this.mLastNonConfigurationInstances != null) {
      this.mFragments.restoreLoaderNonConfig(this.mLastNonConfigurationInstances.loaders);
    }
    if (this.mActivityInfo.parentActivityName != null)
    {
      if (this.mActionBar != null) {
        break label135;
      }
      this.mEnableDefaultActionBarUp = true;
    }
    for (;;)
    {
      if (nativeGetwalflag(getPackageName())) {
        SQLiteDatabase.enableActivityWALMode();
      }
      if (paramBundle != null)
      {
        Parcelable localParcelable = paramBundle.getParcelable("android:fragments");
        FragmentController localFragmentController = this.mFragments;
        if (this.mLastNonConfigurationInstances != null) {
          localFragmentManagerNonConfig = this.mLastNonConfigurationInstances.fragments;
        }
        localFragmentController.restoreAllState(localParcelable, localFragmentManagerNonConfig);
      }
      this.mFragments.dispatchCreate();
      getApplication().dispatchActivityCreated(this, paramBundle);
      if (this.mVoiceInteractor != null) {
        this.mVoiceInteractor.attachActivity(this);
      }
      this.mCalled = true;
      return;
      label135:
      this.mActionBar.setDefaultDisplayHomeAsUpEnabled(true);
    }
  }
  
  public void onCreate(Bundle paramBundle, PersistableBundle paramPersistableBundle)
  {
    onCreate(paramBundle);
  }
  
  public void onCreateContextMenu(ContextMenu paramContextMenu, View paramView, ContextMenu.ContextMenuInfo paramContextMenuInfo) {}
  
  public CharSequence onCreateDescription()
  {
    return null;
  }
  
  @Deprecated
  protected Dialog onCreateDialog(int paramInt)
  {
    return null;
  }
  
  @Deprecated
  protected Dialog onCreateDialog(int paramInt, Bundle paramBundle)
  {
    return onCreateDialog(paramInt);
  }
  
  public void onCreateNavigateUpTaskStack(TaskStackBuilder paramTaskStackBuilder)
  {
    paramTaskStackBuilder.addParentStack(this);
  }
  
  public boolean onCreateOptionsMenu(Menu paramMenu)
  {
    if (this.mParent != null) {
      return this.mParent.onCreateOptionsMenu(paramMenu);
    }
    return true;
  }
  
  public boolean onCreatePanelMenu(int paramInt, Menu paramMenu)
  {
    if (paramInt == 0) {
      return onCreateOptionsMenu(paramMenu) | this.mFragments.dispatchCreateOptionsMenu(paramMenu, getMenuInflater());
    }
    return false;
  }
  
  public View onCreatePanelView(int paramInt)
  {
    return null;
  }
  
  public boolean onCreateThumbnail(Bitmap paramBitmap, Canvas paramCanvas)
  {
    return false;
  }
  
  public View onCreateView(View paramView, String paramString, Context paramContext, AttributeSet paramAttributeSet)
  {
    if (!"fragment".equals(paramString)) {
      return onCreateView(paramString, paramContext, paramAttributeSet);
    }
    return this.mFragments.onCreateView(paramView, paramString, paramContext, paramAttributeSet);
  }
  
  public View onCreateView(String paramString, Context paramContext, AttributeSet paramAttributeSet)
  {
    return null;
  }
  
  protected void onDestroy()
  {
    this.mCalled = true;
    int j;
    int i;
    if (this.mManagedDialogs != null)
    {
      j = this.mManagedDialogs.size();
      i = 0;
      while (i < j)
      {
        ??? = (ManagedDialog)this.mManagedDialogs.valueAt(i);
        if (((ManagedDialog)???).mDialog.isShowing()) {
          ((ManagedDialog)???).mDialog.dismiss();
        }
        i += 1;
      }
      this.mManagedDialogs = null;
    }
    for (;;)
    {
      synchronized (this.mManagedCursors)
      {
        j = this.mManagedCursors.size();
        i = 0;
        if (i < j)
        {
          ManagedCursor localManagedCursor = (ManagedCursor)this.mManagedCursors.get(i);
          if (localManagedCursor == null) {
            break label194;
          }
          if (ManagedCursor.-get0(localManagedCursor) != null) {
            ManagedCursor.-get0(localManagedCursor).close();
          } else {
            Log.w("Activity", "Skip to close the empty c.mCursor.");
          }
        }
      }
      this.mManagedCursors.clear();
      if (this.mSearchManager != null) {
        this.mSearchManager.stopSearch();
      }
      if (this.mActionBar != null) {
        this.mActionBar.onDestroy();
      }
      getApplication().dispatchActivityDestroyed(this);
      return;
      label194:
      i += 1;
    }
  }
  
  public void onDetachedFromWindow() {}
  
  public void onEnterAnimationComplete() {}
  
  public boolean onGenericMotionEvent(MotionEvent paramMotionEvent)
  {
    return false;
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if (paramInt == 4)
    {
      if (getApplicationInfo().targetSdkVersion >= 5)
      {
        paramKeyEvent.startTracking();
        return true;
      }
      onBackPressed();
      return true;
    }
    if (this.mDefaultKeyMode == 0) {
      return false;
    }
    if (this.mDefaultKeyMode == 2)
    {
      Window localWindow = getWindow();
      return (localWindow.hasFeature(0)) && (localWindow.performPanelShortcut(0, paramInt, paramKeyEvent, 2));
    }
    int i = 0;
    boolean bool1;
    if ((paramKeyEvent.getRepeatCount() != 0) || (paramKeyEvent.isSystem()))
    {
      paramInt = 1;
      bool1 = false;
    }
    for (;;)
    {
      if (paramInt != 0)
      {
        this.mDefaultKeySsb.clear();
        this.mDefaultKeySsb.clearSpans();
        Selection.setSelection(this.mDefaultKeySsb, 0);
      }
      return bool1;
      boolean bool2 = TextKeyListener.getInstance().onKeyDown(null, this.mDefaultKeySsb, paramInt, paramKeyEvent);
      paramInt = i;
      bool1 = bool2;
      if (bool2)
      {
        paramInt = i;
        bool1 = bool2;
        if (this.mDefaultKeySsb.length() > 0)
        {
          paramKeyEvent = this.mDefaultKeySsb.toString();
          paramInt = 1;
          switch (this.mDefaultKeyMode)
          {
          case 2: 
          default: 
            bool1 = bool2;
            break;
          case 1: 
            paramKeyEvent = new Intent("android.intent.action.DIAL", Uri.parse("tel:" + paramKeyEvent));
            paramKeyEvent.addFlags(268435456);
            startActivity(paramKeyEvent);
            bool1 = bool2;
            break;
          case 3: 
            startSearch(paramKeyEvent, false, null, false);
            bool1 = bool2;
            break;
          case 4: 
            startSearch(paramKeyEvent, false, null, true);
            bool1 = bool2;
          }
        }
      }
    }
  }
  
  public boolean onKeyLongPress(int paramInt, KeyEvent paramKeyEvent)
  {
    return false;
  }
  
  public boolean onKeyMultiple(int paramInt1, int paramInt2, KeyEvent paramKeyEvent)
  {
    return false;
  }
  
  public boolean onKeyShortcut(int paramInt, KeyEvent paramKeyEvent)
  {
    ActionBar localActionBar = getActionBar();
    if (localActionBar != null) {
      return localActionBar.onKeyShortcut(paramInt, paramKeyEvent);
    }
    return false;
  }
  
  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    if ((getApplicationInfo().targetSdkVersion < 5) || (paramInt != 4) || (!paramKeyEvent.isTracking()) || (paramKeyEvent.isCanceled())) {
      return false;
    }
    onBackPressed();
    return true;
  }
  
  public void onLocalVoiceInteractionStarted() {}
  
  public void onLocalVoiceInteractionStopped() {}
  
  public void onLowMemory()
  {
    this.mCalled = true;
    this.mFragments.dispatchLowMemory();
  }
  
  public boolean onMenuItemSelected(int paramInt, MenuItem paramMenuItem)
  {
    CharSequence localCharSequence = paramMenuItem.getTitleCondensed();
    switch (paramInt)
    {
    default: 
      return false;
    case 0: 
      if (localCharSequence != null) {
        EventLog.writeEvent(50000, new Object[] { Integer.valueOf(0), localCharSequence.toString() });
      }
      if (onOptionsItemSelected(paramMenuItem)) {
        return true;
      }
      if (this.mFragments.dispatchOptionsItemSelected(paramMenuItem)) {
        return true;
      }
      if ((paramMenuItem.getItemId() == 16908332) && (this.mActionBar != null) && ((this.mActionBar.getDisplayOptions() & 0x4) != 0))
      {
        if (this.mParent == null) {
          return onNavigateUp();
        }
        return this.mParent.onNavigateUpFromChild(this);
      }
      return false;
    }
    if (localCharSequence != null) {
      EventLog.writeEvent(50000, new Object[] { Integer.valueOf(1), localCharSequence.toString() });
    }
    if (onContextItemSelected(paramMenuItem)) {
      return true;
    }
    return this.mFragments.dispatchContextItemSelected(paramMenuItem);
  }
  
  public boolean onMenuOpened(int paramInt, Menu paramMenu)
  {
    if (paramInt == 8)
    {
      initWindowDecorActionBar();
      if (this.mActionBar != null) {
        this.mActionBar.dispatchMenuVisibilityChanged(true);
      }
    }
    else
    {
      return true;
    }
    Log.e("Activity", "Tried to open action bar menu with no action bar");
    return true;
  }
  
  public void onMultiWindowModeChanged(boolean paramBoolean) {}
  
  public boolean onNavigateUp()
  {
    Object localObject = getParentActivityIntent();
    if (localObject != null)
    {
      if (this.mActivityInfo.taskAffinity == null) {
        finish();
      }
      for (;;)
      {
        return true;
        if (shouldUpRecreateTask((Intent)localObject))
        {
          localObject = TaskStackBuilder.create(this);
          onCreateNavigateUpTaskStack((TaskStackBuilder)localObject);
          onPrepareNavigateUpTaskStack((TaskStackBuilder)localObject);
          ((TaskStackBuilder)localObject).startActivities();
          if ((this.mResultCode != 0) || (this.mResultData != null))
          {
            Log.i("Activity", "onNavigateUp only finishing topmost activity to return a result");
            finish();
          }
          else
          {
            finishAffinity();
          }
        }
        else
        {
          navigateUpTo((Intent)localObject);
        }
      }
    }
    return false;
  }
  
  public boolean onNavigateUpFromChild(Activity paramActivity)
  {
    return onNavigateUp();
  }
  
  public void onNewActivityOptions(ActivityOptions paramActivityOptions)
  {
    this.mActivityTransitionState.setEnterActivityOptions(this, paramActivityOptions);
    if (!this.mStopped) {
      this.mActivityTransitionState.enterReady(this);
    }
  }
  
  protected void onNewIntent(Intent paramIntent) {}
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    if (this.mParent != null) {
      return this.mParent.onOptionsItemSelected(paramMenuItem);
    }
    return false;
  }
  
  public void onOptionsMenuClosed(Menu paramMenu)
  {
    if (this.mParent != null) {
      this.mParent.onOptionsMenuClosed(paramMenu);
    }
  }
  
  public void onPanelClosed(int paramInt, Menu paramMenu)
  {
    switch (paramInt)
    {
    default: 
      return;
    case 0: 
      this.mFragments.dispatchOptionsMenuClosed(paramMenu);
      onOptionsMenuClosed(paramMenu);
      return;
    case 6: 
      onContextMenuClosed(paramMenu);
      return;
    }
    initWindowDecorActionBar();
    this.mActionBar.dispatchMenuVisibilityChanged(false);
  }
  
  protected void onPause()
  {
    getApplication().dispatchActivityPaused(this);
    this.mCalled = true;
  }
  
  public void onPictureInPictureModeChanged(boolean paramBoolean) {}
  
  protected void onPostCreate(Bundle paramBundle)
  {
    if (!isChild())
    {
      this.mTitleReady = true;
      onTitleChanged(getTitle(), getTitleColor());
    }
    this.mCalled = true;
  }
  
  public void onPostCreate(Bundle paramBundle, PersistableBundle paramPersistableBundle)
  {
    onPostCreate(paramBundle);
  }
  
  protected void onPostResume()
  {
    Window localWindow = getWindow();
    if (localWindow != null) {
      localWindow.makeActive();
    }
    if (this.mActionBar != null) {
      this.mActionBar.setShowHideAnimationEnabled(true);
    }
    this.mCalled = true;
  }
  
  @Deprecated
  protected void onPrepareDialog(int paramInt, Dialog paramDialog)
  {
    paramDialog.setOwnerActivity(this);
  }
  
  @Deprecated
  protected void onPrepareDialog(int paramInt, Dialog paramDialog, Bundle paramBundle)
  {
    onPrepareDialog(paramInt, paramDialog);
  }
  
  public void onPrepareNavigateUpTaskStack(TaskStackBuilder paramTaskStackBuilder) {}
  
  public boolean onPrepareOptionsMenu(Menu paramMenu)
  {
    if (this.mParent != null) {
      return this.mParent.onPrepareOptionsMenu(paramMenu);
    }
    return true;
  }
  
  public boolean onPreparePanel(int paramInt, View paramView, Menu paramMenu)
  {
    if ((paramInt == 0) && (paramMenu != null)) {
      return onPrepareOptionsMenu(paramMenu) | this.mFragments.dispatchPrepareOptionsMenu(paramMenu);
    }
    return true;
  }
  
  public void onProvideAssistContent(AssistContent paramAssistContent) {}
  
  public void onProvideAssistData(Bundle paramBundle) {}
  
  public void onProvideKeyboardShortcuts(List<KeyboardShortcutGroup> paramList, Menu paramMenu, int paramInt)
  {
    if (paramMenu == null) {
      return;
    }
    Object localObject1 = null;
    int i = paramMenu.size();
    paramInt = 0;
    if (paramInt < i)
    {
      Object localObject2 = paramMenu.getItem(paramInt);
      CharSequence localCharSequence = ((MenuItem)localObject2).getTitle();
      char c = ((MenuItem)localObject2).getAlphabeticShortcut();
      localObject2 = localObject1;
      int j;
      if (localCharSequence != null)
      {
        localObject2 = localObject1;
        if (c != 0)
        {
          localObject2 = localObject1;
          if (localObject1 == null)
          {
            j = this.mApplication.getApplicationInfo().labelRes;
            if (j == 0) {
              break label144;
            }
          }
        }
      }
      label144:
      for (localObject1 = getString(j);; localObject1 = null)
      {
        localObject2 = new KeyboardShortcutGroup((CharSequence)localObject1);
        ((KeyboardShortcutGroup)localObject2).addItem(new KeyboardShortcutInfo(localCharSequence, c, 4096));
        paramInt += 1;
        localObject1 = localObject2;
        break;
      }
    }
    if (localObject1 != null) {
      paramList.add(localObject1);
    }
  }
  
  public Uri onProvideReferrer()
  {
    return null;
  }
  
  public void onRequestPermissionsResult(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt) {}
  
  protected void onRestart()
  {
    this.mCalled = true;
  }
  
  protected void onRestoreInstanceState(Bundle paramBundle)
  {
    if (this.mWindow != null)
    {
      paramBundle = paramBundle.getBundle("android:viewHierarchyState");
      if (paramBundle != null) {
        this.mWindow.restoreHierarchyState(paramBundle);
      }
    }
  }
  
  public void onRestoreInstanceState(Bundle paramBundle, PersistableBundle paramPersistableBundle)
  {
    if (paramBundle != null) {
      onRestoreInstanceState(paramBundle);
    }
  }
  
  protected void onResume()
  {
    getApplication().dispatchActivityResumed(this);
    this.mActivityTransitionState.onResume(this, isTopOfTask());
    this.mCalled = true;
  }
  
  HashMap<String, Object> onRetainNonConfigurationChildInstances()
  {
    return null;
  }
  
  public Object onRetainNonConfigurationInstance()
  {
    return null;
  }
  
  protected void onSaveInstanceState(Bundle paramBundle)
  {
    paramBundle.putBundle("android:viewHierarchyState", this.mWindow.saveHierarchyState());
    Parcelable localParcelable = this.mFragments.saveAllState();
    if (localParcelable != null) {
      paramBundle.putParcelable("android:fragments", localParcelable);
    }
    getApplication().dispatchActivitySaveInstanceState(this, paramBundle);
  }
  
  public void onSaveInstanceState(Bundle paramBundle, PersistableBundle paramPersistableBundle)
  {
    onSaveInstanceState(paramBundle);
  }
  
  public boolean onSearchRequested()
  {
    if ((getResources().getConfiguration().uiMode & 0xF) != 4)
    {
      startSearch(null, false, null, false);
      return true;
    }
    return false;
  }
  
  public boolean onSearchRequested(SearchEvent paramSearchEvent)
  {
    this.mSearchEvent = paramSearchEvent;
    boolean bool = onSearchRequested();
    this.mSearchEvent = null;
    return bool;
  }
  
  protected void onStart()
  {
    this.mCalled = true;
    this.mFragments.doLoaderStart();
    getApplication().dispatchActivityStarted(this);
  }
  
  public void onStateNotSaved() {}
  
  protected void onStop()
  {
    if (this.mActionBar != null) {
      this.mActionBar.setShowHideAnimationEnabled(false);
    }
    this.mActivityTransitionState.onStop();
    getApplication().dispatchActivityStopped(this);
    this.mTranslucentCallback = null;
    this.mCalled = true;
  }
  
  protected void onTitleChanged(CharSequence paramCharSequence, int paramInt)
  {
    if (this.mTitleReady)
    {
      Window localWindow = getWindow();
      if (localWindow != null)
      {
        localWindow.setTitle(paramCharSequence);
        if (paramInt != 0) {
          localWindow.setTitleColor(paramInt);
        }
      }
      if (this.mActionBar != null) {
        this.mActionBar.setWindowTitle(paramCharSequence);
      }
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (this.mWindow.shouldCloseOnTouch(this, paramMotionEvent))
    {
      finish();
      return true;
    }
    return false;
  }
  
  public boolean onTrackballEvent(MotionEvent paramMotionEvent)
  {
    return false;
  }
  
  void onTranslucentConversionComplete(boolean paramBoolean)
  {
    if (this.mTranslucentCallback != null)
    {
      this.mTranslucentCallback.onTranslucentConversionComplete(paramBoolean);
      this.mTranslucentCallback = null;
    }
    if (this.mChangeCanvasToTranslucent) {
      WindowManagerGlobal.getInstance().changeCanvasOpacity(this.mToken, false);
    }
  }
  
  public void onTrimMemory(int paramInt)
  {
    this.mCalled = true;
    this.mFragments.dispatchTrimMemory(paramInt);
  }
  
  public void onUserInteraction() {}
  
  protected void onUserLeaveHint() {}
  
  public void onVisibleBehindCanceled()
  {
    this.mCalled = true;
  }
  
  public void onWindowAttributesChanged(WindowManager.LayoutParams paramLayoutParams)
  {
    if (this.mParent == null)
    {
      View localView = this.mDecor;
      if ((localView != null) && (localView.getParent() != null)) {
        getWindowManager().updateViewLayout(localView, paramLayoutParams);
      }
    }
  }
  
  public void onWindowDismissed(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 2;; i = 0)
    {
      finish(i);
      return;
    }
  }
  
  public void onWindowFocusChanged(boolean paramBoolean) {}
  
  public ActionMode onWindowStartingActionMode(ActionMode.Callback paramCallback)
  {
    if (this.mActionModeTypeStarting == 0)
    {
      initWindowDecorActionBar();
      if (this.mActionBar != null) {
        return this.mActionBar.startActionMode(paramCallback);
      }
    }
    return null;
  }
  
  public ActionMode onWindowStartingActionMode(ActionMode.Callback paramCallback, int paramInt)
  {
    try
    {
      this.mActionModeTypeStarting = paramInt;
      paramCallback = onWindowStartingActionMode(paramCallback);
      return paramCallback;
    }
    finally
    {
      this.mActionModeTypeStarting = 0;
    }
  }
  
  public void openContextMenu(View paramView)
  {
    paramView.showContextMenu();
  }
  
  public void openOptionsMenu()
  {
    if ((!this.mWindow.hasFeature(0)) || ((this.mActionBar != null) && (this.mActionBar.openOptionsMenu()))) {
      return;
    }
    this.mWindow.openPanel(0, null);
  }
  
  public void overridePendingTransition(int paramInt1, int paramInt2)
  {
    try
    {
      ActivityManagerNative.getDefault().overridePendingTransition(this.mToken, getPackageName(), paramInt1, paramInt2);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  final void performCreate(Bundle paramBundle)
  {
    restoreHasCurrentPermissionRequest(paramBundle);
    onCreate(paramBundle);
    this.mActivityTransitionState.readState(paramBundle);
    performCreateCommon();
  }
  
  final void performCreate(Bundle paramBundle, PersistableBundle paramPersistableBundle)
  {
    restoreHasCurrentPermissionRequest(paramBundle);
    onCreate(paramBundle, paramPersistableBundle);
    this.mActivityTransitionState.readState(paramBundle);
    performCreateCommon();
  }
  
  final void performCreateCommon()
  {
    boolean bool = false;
    if (this.mWindow.getWindowStyle().getBoolean(10, false)) {}
    for (;;)
    {
      this.mVisibleFromClient = bool;
      this.mFragments.dispatchActivityCreated();
      this.mActivityTransitionState.setEnterActivityOptions(this, getActivityOptions());
      return;
      bool = true;
    }
  }
  
  final void performDestroy()
  {
    this.mDestroyed = true;
    this.mWindow.destroy();
    this.mFragments.dispatchDestroy();
    onDestroy();
    this.mFragments.doLoaderDestroy();
    if (this.mVoiceInteractor != null) {
      this.mVoiceInteractor.detachActivity();
    }
  }
  
  final void performPause()
  {
    this.mDoReportFullyDrawn = false;
    this.mFragments.dispatchPause();
    this.mCalled = false;
    onPause();
    this.mResumed = false;
    if ((!this.mCalled) && (getApplicationInfo().targetSdkVersion >= 9)) {
      throw new SuperNotCalledException("Activity " + this.mComponent.toShortString() + " did not call through to super.onPause()");
    }
    this.mResumed = false;
  }
  
  final void performRestart()
  {
    this.mFragments.noteStateNotSaved();
    if ((this.mToken != null) && (this.mParent == null)) {
      WindowManagerGlobal.getInstance().setStoppedState(this.mToken, false);
    }
    if (this.mStopped)
    {
      this.mStopped = false;
      for (;;)
      {
        int i;
        synchronized (this.mManagedCursors)
        {
          int j = this.mManagedCursors.size();
          i = 0;
          if (i >= j) {
            break;
          }
          ManagedCursor localManagedCursor1 = (ManagedCursor)this.mManagedCursors.get(i);
          if ((!ManagedCursor.-get1(localManagedCursor1)) && (!ManagedCursor.-get2(localManagedCursor1))) {
            break label173;
          }
          if ((!ManagedCursor.-get0(localManagedCursor1).requery()) && (getApplicationInfo().targetSdkVersion >= 14)) {
            throw new IllegalStateException("trying to requery an already closed cursor  " + ManagedCursor.-get0(localManagedCursor1));
          }
        }
        ManagedCursor.-set0(localManagedCursor2, false);
        ManagedCursor.-set1(localManagedCursor2, false);
        label173:
        i += 1;
      }
      this.mCalled = false;
      this.mInstrumentation.callActivityOnRestart(this);
      if (!this.mCalled) {
        throw new SuperNotCalledException("Activity " + this.mComponent.toShortString() + " did not call through to super.onRestart()");
      }
      performStart();
    }
  }
  
  final void performRestoreInstanceState(Bundle paramBundle)
  {
    onRestoreInstanceState(paramBundle);
    restoreManagedDialogs(paramBundle);
  }
  
  final void performRestoreInstanceState(Bundle paramBundle, PersistableBundle paramPersistableBundle)
  {
    onRestoreInstanceState(paramBundle, paramPersistableBundle);
    if (paramBundle != null) {
      restoreManagedDialogs(paramBundle);
    }
  }
  
  final void performResume()
  {
    performRestart();
    this.mFragments.execPendingActions();
    this.mLastNonConfigurationInstances = null;
    this.mCalled = false;
    this.mInstrumentation.callActivityOnResume(this);
    if (!this.mCalled) {
      throw new SuperNotCalledException("Activity " + this.mComponent.toShortString() + " did not call through to super.onResume()");
    }
    if ((this.mVisibleFromClient) || (this.mFinished)) {}
    do
    {
      this.mCalled = false;
      this.mFragments.dispatchResume();
      this.mFragments.execPendingActions();
      onPostResume();
      if (this.mCalled) {
        break;
      }
      throw new SuperNotCalledException("Activity " + this.mComponent.toShortString() + " did not call through to super.onPostResume()");
      Log.w("Activity", "An activity without a UI must call finish() before onResume() completes");
    } while (getApplicationInfo().targetSdkVersion <= 22);
    throw new IllegalStateException("Activity " + this.mComponent.toShortString() + " did not call finish() prior to onResume() completing");
  }
  
  final void performSaveInstanceState(Bundle paramBundle)
  {
    onSaveInstanceState(paramBundle);
    saveManagedDialogs(paramBundle);
    this.mActivityTransitionState.saveState(paramBundle);
    storeHasCurrentPermissionRequest(paramBundle);
  }
  
  final void performSaveInstanceState(Bundle paramBundle, PersistableBundle paramPersistableBundle)
  {
    onSaveInstanceState(paramBundle, paramPersistableBundle);
    saveManagedDialogs(paramBundle);
    storeHasCurrentPermissionRequest(paramBundle);
  }
  
  final void performStart()
  {
    this.mActivityTransitionState.setEnterActivityOptions(this, getActivityOptions());
    this.mFragments.noteStateNotSaved();
    this.mCalled = false;
    this.mFragments.execPendingActions();
    this.mInstrumentation.callActivityOnStart(this);
    if (!this.mCalled) {
      throw new SuperNotCalledException("Activity " + this.mComponent.toShortString() + " did not call through to super.onStart()");
    }
    this.mFragments.dispatchStart();
    this.mFragments.reportLoaderStart();
    int i;
    int j;
    label131:
    String str2;
    String str1;
    if (SystemProperties.getInt("ro.bionic.ld.warning", 0) == 1)
    {
      i = 1;
      if ((this.mApplication.getApplicationInfo().flags & 0x2) == 0) {
        break label239;
      }
      j = 1;
      if ((j != 0) || (i != 0))
      {
        str2 = getDlWarning();
        if (str2 != null)
        {
          str1 = getApplicationInfo().loadLabel(getPackageManager()).toString();
          str2 = "Detected problems with app native libraries\n(please consult log for detail):\n" + str2;
          if (j == 0) {
            break label244;
          }
          new AlertDialog.Builder(this).setTitle(str1).setMessage(str2).setPositiveButton(17039370, null).setCancelable(false).show();
        }
      }
    }
    for (;;)
    {
      this.mActivityTransitionState.enterReady(this);
      return;
      i = 0;
      break;
      label239:
      j = 0;
      break label131;
      label244:
      Toast.makeText(this, str1 + "\n" + str2, 1).show();
    }
  }
  
  final void performStop(boolean paramBoolean)
  {
    this.mDoReportFullyDrawn = false;
    this.mFragments.doLoaderStop(this.mChangingConfigurations);
    if (!this.mStopped)
    {
      if (this.mWindow != null) {
        this.mWindow.closeAllPanels();
      }
      if ((!paramBoolean) && (this.mToken != null) && (this.mParent == null)) {
        WindowManagerGlobal.getInstance().setStoppedState(this.mToken, true);
      }
      this.mFragments.dispatchStop();
      this.mCalled = false;
      this.mInstrumentation.callActivityOnStop(this);
      if (!this.mCalled) {
        throw new SuperNotCalledException("Activity " + this.mComponent.toShortString() + " did not call through to super.onStop()");
      }
    }
    for (;;)
    {
      int i;
      synchronized (this.mManagedCursors)
      {
        int j = this.mManagedCursors.size();
        i = 0;
        if (i < j)
        {
          ManagedCursor localManagedCursor = (ManagedCursor)this.mManagedCursors.get(i);
          if (ManagedCursor.-get1(localManagedCursor)) {
            break label240;
          }
          if (ManagedCursor.-get0(localManagedCursor) != null)
          {
            ManagedCursor.-get0(localManagedCursor).deactivate();
            ManagedCursor.-set0(localManagedCursor, true);
            break label240;
          }
          Log.w("Activity", "Skip to deactive the empty mc.mCursor.");
        }
      }
      this.mStopped = true;
      this.mResumed = false;
      return;
      label240:
      i += 1;
    }
  }
  
  final void performUserLeaving()
  {
    onUserInteraction();
    onUserLeaveHint();
  }
  
  public void postponeEnterTransition()
  {
    this.mActivityTransitionState.postponeEnterTransition();
  }
  
  public void recreate()
  {
    if (this.mParent != null) {
      throw new IllegalStateException("Can only be called on top-level activity");
    }
    if (Looper.myLooper() != this.mMainThread.getLooper()) {
      throw new IllegalStateException("Must be called from main thread");
    }
    this.mMainThread.requestRelaunchActivity(this.mToken, null, null, 0, false, null, null, false, false);
  }
  
  public void registerForContextMenu(View paramView)
  {
    paramView.setOnCreateContextMenuListener(this);
  }
  
  public boolean releaseInstance()
  {
    try
    {
      boolean bool = ActivityManagerNative.getDefault().releaseActivityInstance(this.mToken);
      return bool;
    }
    catch (RemoteException localRemoteException) {}
    return false;
  }
  
  @Deprecated
  public final void removeDialog(int paramInt)
  {
    if (this.mManagedDialogs != null)
    {
      ManagedDialog localManagedDialog = (ManagedDialog)this.mManagedDialogs.get(paramInt);
      if (localManagedDialog != null)
      {
        localManagedDialog.mDialog.dismiss();
        this.mManagedDialogs.remove(paramInt);
      }
    }
  }
  
  public void reportFullyDrawn()
  {
    if (this.mDoReportFullyDrawn) {
      this.mDoReportFullyDrawn = false;
    }
    try
    {
      ActivityManagerNative.getDefault().reportActivityFullyDrawn(this.mToken);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public DragAndDropPermissions requestDragAndDropPermissions(DragEvent paramDragEvent)
  {
    paramDragEvent = DragAndDropPermissions.obtain(paramDragEvent);
    if ((paramDragEvent != null) && (paramDragEvent.take(getActivityToken()))) {
      return paramDragEvent;
    }
    return null;
  }
  
  public final void requestPermissions(String[] paramArrayOfString, int paramInt)
  {
    if (this.mHasCurrentPermissionsRequest)
    {
      Log.w("Activity", "Can reqeust only one set of permissions at a time");
      onRequestPermissionsResult(paramInt, new String[0], new int[0]);
      return;
    }
    startActivityForResult("@android:requestPermissions:", getPackageManager().buildRequestPermissionsIntent(paramArrayOfString), paramInt, null);
    this.mHasCurrentPermissionsRequest = true;
  }
  
  public final void requestShowKeyboardShortcuts()
  {
    Intent localIntent = new Intent("android.intent.action.SHOW_KEYBOARD_SHORTCUTS");
    localIntent.setComponent(new ComponentName("com.android.systemui", "com.android.systemui.statusbar.KeyboardShortcutsReceiver"));
    sendBroadcast(localIntent);
  }
  
  public boolean requestVisibleBehind(boolean paramBoolean)
  {
    if (!this.mResumed) {}
    for (paramBoolean = false;; paramBoolean = false)
    {
      try
      {
        if (!ActivityManagerNative.getDefault().requestVisibleBehind(this.mToken, paramBoolean)) {
          continue;
        }
        this.mVisibleBehind = paramBoolean;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          this.mVisibleBehind = false;
        }
      }
      return this.mVisibleBehind;
    }
  }
  
  public final boolean requestWindowFeature(int paramInt)
  {
    return getWindow().requestFeature(paramInt);
  }
  
  NonConfigurationInstances retainNonConfigurationInstances()
  {
    Object localObject = onRetainNonConfigurationInstance();
    HashMap localHashMap = onRetainNonConfigurationChildInstances();
    FragmentManagerNonConfig localFragmentManagerNonConfig = this.mFragments.retainNestedNonConfig();
    this.mFragments.doLoaderStart();
    this.mFragments.doLoaderStop(true);
    ArrayMap localArrayMap = this.mFragments.retainLoaderNonConfig();
    if ((localObject == null) && (localHashMap == null) && (localFragmentManagerNonConfig == null) && (localArrayMap == null) && (this.mVoiceInteractor == null)) {
      return null;
    }
    NonConfigurationInstances localNonConfigurationInstances = new NonConfigurationInstances();
    localNonConfigurationInstances.activity = localObject;
    localNonConfigurationInstances.children = localHashMap;
    localNonConfigurationInstances.fragments = localFragmentManagerNonConfig;
    localNonConfigurationInstances.loaders = localArrayMap;
    if (this.mVoiceInteractor != null)
    {
      this.mVoiceInteractor.retainInstance();
      localNonConfigurationInstances.voiceInteractor = this.mVoiceInteractor;
    }
    return localNonConfigurationInstances;
  }
  
  public final void runOnUiThread(Runnable paramRunnable)
  {
    if (Thread.currentThread() != this.mUiThread)
    {
      this.mHandler.post(paramRunnable);
      return;
    }
    paramRunnable.run();
  }
  
  public void setActionBar(Toolbar paramToolbar)
  {
    ActionBar localActionBar = getActionBar();
    if ((localActionBar instanceof WindowDecorActionBar)) {
      throw new IllegalStateException("This Activity already has an action bar supplied by the window decor. Do not request Window.FEATURE_ACTION_BAR and set android:windowActionBar to false in your theme to use a Toolbar instead.");
    }
    this.mMenuInflater = null;
    if (localActionBar != null) {
      localActionBar.onDestroy();
    }
    if (paramToolbar != null)
    {
      paramToolbar = new ToolbarActionBar(paramToolbar, getTitle(), this);
      this.mActionBar = paramToolbar;
      this.mWindow.setCallback(paramToolbar.getWrappedWindowCallback());
    }
    for (;;)
    {
      invalidateOptionsMenu();
      return;
      this.mActionBar = null;
      this.mWindow.setCallback(this);
    }
  }
  
  public void setContentTransitionManager(TransitionManager paramTransitionManager)
  {
    getWindow().setTransitionManager(paramTransitionManager);
  }
  
  public void setContentView(int paramInt)
  {
    getWindow().setContentView(paramInt);
    initWindowDecorActionBar();
  }
  
  public void setContentView(View paramView)
  {
    getWindow().setContentView(paramView);
    initWindowDecorActionBar();
  }
  
  public void setContentView(View paramView, ViewGroup.LayoutParams paramLayoutParams)
  {
    getWindow().setContentView(paramView, paramLayoutParams);
    initWindowDecorActionBar();
  }
  
  public final void setDefaultKeyMode(int paramInt)
  {
    this.mDefaultKeyMode = paramInt;
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException();
    case 0: 
    case 2: 
      this.mDefaultKeySsb = null;
      return;
    }
    this.mDefaultKeySsb = new SpannableStringBuilder();
    Selection.setSelection(this.mDefaultKeySsb, 0);
  }
  
  public void setEnterSharedElementCallback(SharedElementCallback paramSharedElementCallback)
  {
    SharedElementCallback localSharedElementCallback = paramSharedElementCallback;
    if (paramSharedElementCallback == null) {
      localSharedElementCallback = SharedElementCallback.NULL_CALLBACK;
    }
    this.mEnterTransitionListener = localSharedElementCallback;
  }
  
  public void setExitSharedElementCallback(SharedElementCallback paramSharedElementCallback)
  {
    SharedElementCallback localSharedElementCallback = paramSharedElementCallback;
    if (paramSharedElementCallback == null) {
      localSharedElementCallback = SharedElementCallback.NULL_CALLBACK;
    }
    this.mExitTransitionListener = localSharedElementCallback;
  }
  
  public final void setFeatureDrawable(int paramInt, Drawable paramDrawable)
  {
    getWindow().setFeatureDrawable(paramInt, paramDrawable);
  }
  
  public final void setFeatureDrawableAlpha(int paramInt1, int paramInt2)
  {
    getWindow().setFeatureDrawableAlpha(paramInt1, paramInt2);
  }
  
  public final void setFeatureDrawableResource(int paramInt1, int paramInt2)
  {
    getWindow().setFeatureDrawableResource(paramInt1, paramInt2);
  }
  
  public final void setFeatureDrawableUri(int paramInt, Uri paramUri)
  {
    getWindow().setFeatureDrawableUri(paramInt, paramUri);
  }
  
  public void setFinishOnTouchOutside(boolean paramBoolean)
  {
    this.mWindow.setCloseOnTouchOutside(paramBoolean);
  }
  
  public void setImmersive(boolean paramBoolean)
  {
    try
    {
      ActivityManagerNative.getDefault().setImmersive(this.mToken, paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public void setIntent(Intent paramIntent)
  {
    this.mIntent = paramIntent;
  }
  
  public final void setMediaController(MediaController paramMediaController)
  {
    getWindow().setMediaController(paramMediaController);
  }
  
  public void setOverlayWithDecorCaptionEnabled(boolean paramBoolean)
  {
    this.mWindow.setOverlayWithDecorCaptionEnabled(paramBoolean);
  }
  
  final void setParent(Activity paramActivity)
  {
    this.mParent = paramActivity;
  }
  
  @Deprecated
  public void setPersistent(boolean paramBoolean) {}
  
  @Deprecated
  public final void setProgress(int paramInt)
  {
    getWindow().setFeatureInt(2, paramInt + 0);
  }
  
  @Deprecated
  public final void setProgressBarIndeterminate(boolean paramBoolean)
  {
    Window localWindow = getWindow();
    if (paramBoolean) {}
    for (int i = -3;; i = -4)
    {
      localWindow.setFeatureInt(2, i);
      return;
    }
  }
  
  @Deprecated
  public final void setProgressBarIndeterminateVisibility(boolean paramBoolean)
  {
    Window localWindow = getWindow();
    if (paramBoolean) {}
    for (int i = -1;; i = -2)
    {
      localWindow.setFeatureInt(5, i);
      return;
    }
  }
  
  @Deprecated
  public final void setProgressBarVisibility(boolean paramBoolean)
  {
    Window localWindow = getWindow();
    if (paramBoolean) {}
    for (int i = -1;; i = -2)
    {
      localWindow.setFeatureInt(2, i);
      return;
    }
  }
  
  public void setRequestedOrientation(int paramInt)
  {
    if (this.mParent == null) {}
    try
    {
      ActivityManagerNative.getDefault().setRequestedOrientation(this.mToken, paramInt);
      return;
    }
    catch (RemoteException localRemoteException) {}
    this.mParent.setRequestedOrientation(paramInt);
    return;
  }
  
  public final void setResult(int paramInt)
  {
    try
    {
      this.mResultCode = paramInt;
      this.mResultData = null;
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public final void setResult(int paramInt, Intent paramIntent)
  {
    try
    {
      this.mResultCode = paramInt;
      this.mResultData = paramIntent;
      return;
    }
    finally
    {
      paramIntent = finally;
      throw paramIntent;
    }
  }
  
  @Deprecated
  public final void setSecondaryProgress(int paramInt)
  {
    getWindow().setFeatureInt(2, paramInt + 20000);
  }
  
  public void setTaskDescription(ActivityManager.TaskDescription paramTaskDescription)
  {
    if (this.mTaskDescription != paramTaskDescription)
    {
      this.mTaskDescription.copyFrom(paramTaskDescription);
      if ((paramTaskDescription.getIconFilename() == null) && (paramTaskDescription.getIcon() != null))
      {
        int i = ActivityManager.getLauncherLargeIconSizeInner(this);
        paramTaskDescription = Bitmap.createScaledBitmap(paramTaskDescription.getIcon(), i, i, true);
        this.mTaskDescription.setIcon(paramTaskDescription);
      }
    }
    try
    {
      ActivityManagerNative.getDefault().setTaskDescription(this.mToken, this.mTaskDescription);
      return;
    }
    catch (RemoteException paramTaskDescription) {}
  }
  
  public void setTheme(int paramInt)
  {
    super.setTheme(paramInt);
    this.mWindow.setTheme(paramInt);
  }
  
  public void setTitle(int paramInt)
  {
    setTitle(getText(paramInt));
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    this.mTitle = paramCharSequence;
    onTitleChanged(paramCharSequence, this.mTitleColor);
    if (this.mParent != null) {
      this.mParent.onChildTitleChanged(this, paramCharSequence);
    }
  }
  
  @Deprecated
  public void setTitleColor(int paramInt)
  {
    this.mTitleColor = paramInt;
    onTitleChanged(this.mTitle, paramInt);
  }
  
  public void setVisible(boolean paramBoolean)
  {
    if (this.mVisibleFromClient != paramBoolean)
    {
      this.mVisibleFromClient = paramBoolean;
      if (this.mVisibleFromServer)
      {
        if (!paramBoolean) {
          break label29;
        }
        makeVisible();
      }
    }
    return;
    label29:
    this.mDecor.setVisibility(4);
  }
  
  void setVoiceInteractor(IVoiceInteractor paramIVoiceInteractor)
  {
    if (this.mVoiceInteractor != null)
    {
      VoiceInteractor.Request[] arrayOfRequest = this.mVoiceInteractor.getActiveRequests();
      int i = 0;
      int j = arrayOfRequest.length;
      while (i < j)
      {
        VoiceInteractor.Request localRequest = arrayOfRequest[i];
        localRequest.cancel();
        localRequest.clear();
        i += 1;
      }
    }
    if (paramIVoiceInteractor == null)
    {
      this.mVoiceInteractor = null;
      return;
    }
    this.mVoiceInteractor = new VoiceInteractor(paramIVoiceInteractor, this, this, Looper.myLooper());
  }
  
  public final void setVolumeControlStream(int paramInt)
  {
    getWindow().setVolumeControlStream(paramInt);
  }
  
  public void setVrModeEnabled(boolean paramBoolean, ComponentName paramComponentName)
    throws PackageManager.NameNotFoundException
  {
    try
    {
      if (ActivityManagerNative.getDefault().setVrMode(this.mToken, paramBoolean, paramComponentName) != 0) {
        throw new PackageManager.NameNotFoundException(paramComponentName.flattenToString());
      }
    }
    catch (RemoteException paramComponentName) {}
  }
  
  public boolean shouldShowRequestPermissionRationale(String paramString)
  {
    return getPackageManager().shouldShowRequestPermissionRationale(paramString);
  }
  
  public boolean shouldUpRecreateTask(Intent paramIntent)
  {
    try
    {
      PackageManager localPackageManager = getPackageManager();
      ComponentName localComponentName2 = paramIntent.getComponent();
      ComponentName localComponentName1 = localComponentName2;
      if (localComponentName2 == null) {
        localComponentName1 = paramIntent.resolveActivity(localPackageManager);
      }
      paramIntent = localPackageManager.getActivityInfo(localComponentName1, 0);
      if (paramIntent.taskAffinity == null) {
        return false;
      }
      boolean bool = ActivityManagerNative.getDefault().shouldUpRecreateTask(this.mToken, paramIntent.taskAffinity);
      return bool;
    }
    catch (PackageManager.NameNotFoundException paramIntent)
    {
      return false;
    }
    catch (RemoteException paramIntent) {}
    return false;
  }
  
  public boolean showAssist(Bundle paramBundle)
  {
    try
    {
      boolean bool = ActivityManagerNative.getDefault().showAssistFromActivity(this.mToken, paramBundle);
      return bool;
    }
    catch (RemoteException paramBundle) {}
    return false;
  }
  
  @Deprecated
  public final void showDialog(int paramInt)
  {
    showDialog(paramInt, null);
  }
  
  @Deprecated
  public final boolean showDialog(int paramInt, Bundle paramBundle)
  {
    if (this.mManagedDialogs == null) {
      this.mManagedDialogs = new SparseArray();
    }
    ManagedDialog localManagedDialog2 = (ManagedDialog)this.mManagedDialogs.get(paramInt);
    ManagedDialog localManagedDialog1 = localManagedDialog2;
    if (localManagedDialog2 == null)
    {
      localManagedDialog1 = new ManagedDialog(null);
      localManagedDialog1.mDialog = createDialog(Integer.valueOf(paramInt), null, paramBundle);
      if (localManagedDialog1.mDialog == null) {
        return false;
      }
      this.mManagedDialogs.put(paramInt, localManagedDialog1);
    }
    localManagedDialog1.mArgs = paramBundle;
    onPrepareDialog(paramInt, localManagedDialog1.mDialog, paramBundle);
    localManagedDialog1.mDialog.show();
    return true;
  }
  
  public void showLockTaskEscapeMessage()
  {
    try
    {
      ActivityManagerNative.getDefault().showLockTaskEscapeMessage(this.mToken);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public ActionMode startActionMode(ActionMode.Callback paramCallback)
  {
    return this.mWindow.getDecorView().startActionMode(paramCallback);
  }
  
  public ActionMode startActionMode(ActionMode.Callback paramCallback, int paramInt)
  {
    return this.mWindow.getDecorView().startActionMode(paramCallback, paramInt);
  }
  
  public void startActivities(Intent[] paramArrayOfIntent)
  {
    startActivities(paramArrayOfIntent, null);
  }
  
  public void startActivities(Intent[] paramArrayOfIntent, Bundle paramBundle)
  {
    this.mInstrumentation.execStartActivities(this, this.mMainThread.getApplicationThread(), this.mToken, this, paramArrayOfIntent, paramBundle);
  }
  
  public void startActivity(Intent paramIntent)
  {
    startActivity(paramIntent, null);
  }
  
  public void startActivity(Intent paramIntent, Bundle paramBundle)
  {
    if (paramBundle != null)
    {
      startActivityForResult(paramIntent, -1, paramBundle);
      return;
    }
    startActivityForResult(paramIntent, -1);
  }
  
  public void startActivityAsCaller(Intent paramIntent, Bundle paramBundle, boolean paramBoolean, int paramInt)
  {
    if (this.mParent != null) {
      throw new RuntimeException("Can't be called from a child");
    }
    paramBundle = transferSpringboardActivityOptions(paramBundle);
    paramIntent = this.mInstrumentation.execStartActivityAsCaller(this, this.mMainThread.getApplicationThread(), this.mToken, this, paramIntent, -1, paramBundle, paramBoolean, paramInt);
    if (paramIntent != null) {
      this.mMainThread.sendActivityResult(this.mToken, this.mEmbeddedID, -1, paramIntent.getResultCode(), paramIntent.getResultData());
    }
    cancelInputsAndStartExitTransition(paramBundle);
  }
  
  public void startActivityAsUser(Intent paramIntent, Bundle paramBundle, UserHandle paramUserHandle)
  {
    if (this.mParent != null) {
      throw new RuntimeException("Can't be called from a child");
    }
    paramBundle = transferSpringboardActivityOptions(paramBundle);
    paramIntent = this.mInstrumentation.execStartActivity(this, this.mMainThread.getApplicationThread(), this.mToken, this, paramIntent, -1, paramBundle, paramUserHandle);
    if (paramIntent != null) {
      this.mMainThread.sendActivityResult(this.mToken, this.mEmbeddedID, -1, paramIntent.getResultCode(), paramIntent.getResultData());
    }
    cancelInputsAndStartExitTransition(paramBundle);
  }
  
  public void startActivityAsUser(Intent paramIntent, UserHandle paramUserHandle)
  {
    startActivityAsUser(paramIntent, null, paramUserHandle);
  }
  
  public void startActivityForResult(Intent paramIntent, int paramInt)
  {
    startActivityForResult(paramIntent, paramInt, null);
  }
  
  public void startActivityForResult(Intent paramIntent, int paramInt, Bundle paramBundle)
  {
    if (this.mParent == null)
    {
      paramBundle = transferSpringboardActivityOptions(paramBundle);
      paramIntent = this.mInstrumentation.execStartActivity(this, this.mMainThread.getApplicationThread(), this.mToken, this, paramIntent, paramInt, paramBundle);
      if (paramIntent != null) {
        this.mMainThread.sendActivityResult(this.mToken, this.mEmbeddedID, paramInt, paramIntent.getResultCode(), paramIntent.getResultData());
      }
      if (paramInt >= 0) {
        this.mStartedActivity = true;
      }
      cancelInputsAndStartExitTransition(paramBundle);
      return;
    }
    if (paramBundle != null)
    {
      this.mParent.startActivityFromChild(this, paramIntent, paramInt, paramBundle);
      return;
    }
    this.mParent.startActivityFromChild(this, paramIntent, paramInt);
  }
  
  public void startActivityForResult(String paramString, Intent paramIntent, int paramInt, Bundle paramBundle)
  {
    Uri localUri = onProvideReferrer();
    if (localUri != null) {
      paramIntent.putExtra("android.intent.extra.REFERRER", localUri);
    }
    paramBundle = transferSpringboardActivityOptions(paramBundle);
    paramIntent = this.mInstrumentation.execStartActivity(this, this.mMainThread.getApplicationThread(), this.mToken, paramString, paramIntent, paramInt, paramBundle);
    if (paramIntent != null) {
      this.mMainThread.sendActivityResult(this.mToken, paramString, paramInt, paramIntent.getResultCode(), paramIntent.getResultData());
    }
    cancelInputsAndStartExitTransition(paramBundle);
  }
  
  public void startActivityForResultAsUser(Intent paramIntent, int paramInt, Bundle paramBundle, UserHandle paramUserHandle)
  {
    if (this.mParent != null) {
      throw new RuntimeException("Can't be called from a child");
    }
    paramBundle = transferSpringboardActivityOptions(paramBundle);
    paramIntent = this.mInstrumentation.execStartActivity(this, this.mMainThread.getApplicationThread(), this.mToken, this, paramIntent, paramInt, paramBundle, paramUserHandle);
    if (paramIntent != null) {
      this.mMainThread.sendActivityResult(this.mToken, this.mEmbeddedID, paramInt, paramIntent.getResultCode(), paramIntent.getResultData());
    }
    if (paramInt >= 0) {
      this.mStartedActivity = true;
    }
    cancelInputsAndStartExitTransition(paramBundle);
  }
  
  public void startActivityForResultAsUser(Intent paramIntent, int paramInt, UserHandle paramUserHandle)
  {
    startActivityForResultAsUser(paramIntent, paramInt, null, paramUserHandle);
  }
  
  public void startActivityFromChild(Activity paramActivity, Intent paramIntent, int paramInt)
  {
    startActivityFromChild(paramActivity, paramIntent, paramInt, null);
  }
  
  public void startActivityFromChild(Activity paramActivity, Intent paramIntent, int paramInt, Bundle paramBundle)
  {
    paramBundle = transferSpringboardActivityOptions(paramBundle);
    paramIntent = this.mInstrumentation.execStartActivity(this, this.mMainThread.getApplicationThread(), this.mToken, paramActivity, paramIntent, paramInt, paramBundle);
    if (paramIntent != null) {
      this.mMainThread.sendActivityResult(this.mToken, paramActivity.mEmbeddedID, paramInt, paramIntent.getResultCode(), paramIntent.getResultData());
    }
    cancelInputsAndStartExitTransition(paramBundle);
  }
  
  public void startActivityFromFragment(Fragment paramFragment, Intent paramIntent, int paramInt)
  {
    startActivityFromFragment(paramFragment, paramIntent, paramInt, null);
  }
  
  public void startActivityFromFragment(Fragment paramFragment, Intent paramIntent, int paramInt, Bundle paramBundle)
  {
    startActivityForResult(paramFragment.mWho, paramIntent, paramInt, paramBundle);
  }
  
  public boolean startActivityIfNeeded(Intent paramIntent, int paramInt)
  {
    return startActivityIfNeeded(paramIntent, paramInt, null);
  }
  
  public boolean startActivityIfNeeded(Intent paramIntent, int paramInt, Bundle paramBundle)
  {
    int i;
    if (this.mParent == null) {
      i = 1;
    }
    try
    {
      Uri localUri = onProvideReferrer();
      if (localUri != null) {
        paramIntent.putExtra("android.intent.extra.REFERRER", localUri);
      }
      paramIntent.migrateExtraStreamToClipData();
      paramIntent.prepareToLeaveProcess(this);
      int j = ActivityManagerNative.getDefault().startActivity(this.mMainThread.getApplicationThread(), getBasePackageName(), paramIntent, paramIntent.resolveTypeIfNeeded(getContentResolver()), this.mToken, this.mEmbeddedID, paramInt, 1, null, paramBundle);
      i = j;
    }
    catch (RemoteException paramBundle)
    {
      for (;;) {}
    }
    Instrumentation.checkStartActivityResult(i, paramIntent);
    if (paramInt >= 0) {
      this.mStartedActivity = true;
    }
    return i != 1;
    throw new UnsupportedOperationException("startActivityIfNeeded can only be called from a top-level activity");
  }
  
  public void startIntentSender(IntentSender paramIntentSender, Intent paramIntent, int paramInt1, int paramInt2, int paramInt3)
    throws IntentSender.SendIntentException
  {
    startIntentSender(paramIntentSender, paramIntent, paramInt1, paramInt2, paramInt3, null);
  }
  
  public void startIntentSender(IntentSender paramIntentSender, Intent paramIntent, int paramInt1, int paramInt2, int paramInt3, Bundle paramBundle)
    throws IntentSender.SendIntentException
  {
    if (paramBundle != null)
    {
      startIntentSenderForResult(paramIntentSender, -1, paramIntent, paramInt1, paramInt2, paramInt3, paramBundle);
      return;
    }
    startIntentSenderForResult(paramIntentSender, -1, paramIntent, paramInt1, paramInt2, paramInt3);
  }
  
  public void startIntentSenderForResult(IntentSender paramIntentSender, int paramInt1, Intent paramIntent, int paramInt2, int paramInt3, int paramInt4)
    throws IntentSender.SendIntentException
  {
    startIntentSenderForResult(paramIntentSender, paramInt1, paramIntent, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void startIntentSenderForResult(IntentSender paramIntentSender, int paramInt1, Intent paramIntent, int paramInt2, int paramInt3, int paramInt4, Bundle paramBundle)
    throws IntentSender.SendIntentException
  {
    if (this.mParent == null)
    {
      startIntentSenderForResultInner(paramIntentSender, this.mEmbeddedID, paramInt1, paramIntent, paramInt2, paramInt3, paramBundle);
      return;
    }
    if (paramBundle != null)
    {
      this.mParent.startIntentSenderFromChild(this, paramIntentSender, paramInt1, paramIntent, paramInt2, paramInt3, paramInt4, paramBundle);
      return;
    }
    this.mParent.startIntentSenderFromChild(this, paramIntentSender, paramInt1, paramIntent, paramInt2, paramInt3, paramInt4);
  }
  
  public void startIntentSenderFromChild(Activity paramActivity, IntentSender paramIntentSender, int paramInt1, Intent paramIntent, int paramInt2, int paramInt3, int paramInt4)
    throws IntentSender.SendIntentException
  {
    startIntentSenderFromChild(paramActivity, paramIntentSender, paramInt1, paramIntent, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void startIntentSenderFromChild(Activity paramActivity, IntentSender paramIntentSender, int paramInt1, Intent paramIntent, int paramInt2, int paramInt3, int paramInt4, Bundle paramBundle)
    throws IntentSender.SendIntentException
  {
    startIntentSenderForResultInner(paramIntentSender, paramActivity.mEmbeddedID, paramInt1, paramIntent, paramInt2, paramInt3, paramBundle);
  }
  
  public void startIntentSenderFromChildFragment(Fragment paramFragment, IntentSender paramIntentSender, int paramInt1, Intent paramIntent, int paramInt2, int paramInt3, int paramInt4, Bundle paramBundle)
    throws IntentSender.SendIntentException
  {
    startIntentSenderForResultInner(paramIntentSender, paramFragment.mWho, paramInt1, paramIntent, paramInt2, paramInt3, paramBundle);
  }
  
  public void startLocalVoiceInteraction(Bundle paramBundle)
  {
    try
    {
      ActivityManagerNative.getDefault().startLocalVoiceInteraction(this.mToken, paramBundle);
      return;
    }
    catch (RemoteException paramBundle) {}
  }
  
  public void startLockTask()
  {
    try
    {
      ActivityManagerNative.getDefault().startLockTaskMode(this.mToken);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  @Deprecated
  public void startManagingCursor(Cursor paramCursor)
  {
    synchronized (this.mManagedCursors)
    {
      this.mManagedCursors.add(new ManagedCursor(paramCursor));
      return;
    }
  }
  
  public boolean startNextMatchingActivity(Intent paramIntent)
  {
    return startNextMatchingActivity(paramIntent, null);
  }
  
  public boolean startNextMatchingActivity(Intent paramIntent, Bundle paramBundle)
  {
    if (this.mParent == null) {
      try
      {
        paramIntent.migrateExtraStreamToClipData();
        paramIntent.prepareToLeaveProcess(this);
        boolean bool = ActivityManagerNative.getDefault().startNextMatchingActivity(this.mToken, paramIntent, paramBundle);
        return bool;
      }
      catch (RemoteException paramIntent)
      {
        return false;
      }
    }
    throw new UnsupportedOperationException("startNextMatchingActivity can only be called from a top-level activity");
  }
  
  public void startPostponedEnterTransition()
  {
    this.mActivityTransitionState.startPostponedEnterTransition();
  }
  
  public void startSearch(String paramString, boolean paramBoolean1, Bundle paramBundle, boolean paramBoolean2)
  {
    ensureSearchManager();
    this.mSearchManager.startSearch(paramString, paramBoolean1, getComponentName(), paramBundle, paramBoolean2);
  }
  
  public void stopLocalVoiceInteraction()
  {
    try
    {
      ActivityManagerNative.getDefault().stopLocalVoiceInteraction(this.mToken);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public void stopLockTask()
  {
    try
    {
      ActivityManagerNative.getDefault().stopLockTaskMode();
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  @Deprecated
  public void stopManagingCursor(Cursor paramCursor)
  {
    synchronized (this.mManagedCursors)
    {
      int j = this.mManagedCursors.size();
      int i = 0;
      if (i < j)
      {
        if (ManagedCursor.-get0((ManagedCursor)this.mManagedCursors.get(i)) == paramCursor) {
          this.mManagedCursors.remove(i);
        }
      }
      else {
        return;
      }
      i += 1;
    }
  }
  
  public void takeKeyEvents(boolean paramBoolean)
  {
    getWindow().takeKeyEvents(paramBoolean);
  }
  
  public void triggerSearch(String paramString, Bundle paramBundle)
  {
    ensureSearchManager();
    this.mSearchManager.triggerSearch(paramString, getComponentName(), paramBundle);
  }
  
  public void unregisterForContextMenu(View paramView)
  {
    paramView.setOnCreateContextMenuListener(null);
  }
  
  class HostCallbacks
    extends FragmentHostCallback<Activity>
  {
    public HostCallbacks()
    {
      super();
    }
    
    public void onAttachFragment(Fragment paramFragment)
    {
      Activity.this.onAttachFragment(paramFragment);
    }
    
    public void onDump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      Activity.this.dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
    
    public View onFindViewById(int paramInt)
    {
      return Activity.this.findViewById(paramInt);
    }
    
    public Activity onGetHost()
    {
      return Activity.this;
    }
    
    public LayoutInflater onGetLayoutInflater()
    {
      LayoutInflater localLayoutInflater = Activity.this.getLayoutInflater();
      if (onUseFragmentManagerInflaterFactory()) {
        return localLayoutInflater.cloneInContext(Activity.this);
      }
      return localLayoutInflater;
    }
    
    public int onGetWindowAnimations()
    {
      Window localWindow = Activity.this.getWindow();
      if (localWindow == null) {
        return 0;
      }
      return localWindow.getAttributes().windowAnimations;
    }
    
    public boolean onHasView()
    {
      boolean bool2 = false;
      Window localWindow = Activity.this.getWindow();
      boolean bool1 = bool2;
      if (localWindow != null)
      {
        bool1 = bool2;
        if (localWindow.peekDecorView() != null) {
          bool1 = true;
        }
      }
      return bool1;
    }
    
    public boolean onHasWindowAnimations()
    {
      return Activity.this.getWindow() != null;
    }
    
    public void onInvalidateOptionsMenu()
    {
      Activity.this.invalidateOptionsMenu();
    }
    
    public void onRequestPermissionsFromFragment(Fragment paramFragment, String[] paramArrayOfString, int paramInt)
    {
      paramFragment = "@android:requestPermissions:" + paramFragment.mWho;
      paramArrayOfString = Activity.this.getPackageManager().buildRequestPermissionsIntent(paramArrayOfString);
      Activity.this.startActivityForResult(paramFragment, paramArrayOfString, paramInt, null);
    }
    
    public boolean onShouldSaveFragmentState(Fragment paramFragment)
    {
      return !Activity.this.isFinishing();
    }
    
    public void onStartActivityFromFragment(Fragment paramFragment, Intent paramIntent, int paramInt, Bundle paramBundle)
    {
      Activity.this.startActivityFromFragment(paramFragment, paramIntent, paramInt, paramBundle);
    }
    
    public void onStartIntentSenderFromFragment(Fragment paramFragment, IntentSender paramIntentSender, int paramInt1, Intent paramIntent, int paramInt2, int paramInt3, int paramInt4, Bundle paramBundle)
      throws IntentSender.SendIntentException
    {
      if (Activity.this.mParent == null) {
        Activity.-wrap0(Activity.this, paramIntentSender, paramFragment.mWho, paramInt1, paramIntent, paramInt2, paramInt3, paramBundle);
      }
      while (paramBundle == null) {
        return;
      }
      Activity.this.mParent.startIntentSenderFromChildFragment(paramFragment, paramIntentSender, paramInt1, paramIntent, paramInt2, paramInt3, paramInt4, paramBundle);
    }
    
    public boolean onUseFragmentManagerInflaterFactory()
    {
      return Activity.this.getApplicationInfo().targetSdkVersion >= 21;
    }
  }
  
  private static final class ManagedCursor
  {
    private final Cursor mCursor;
    private boolean mReleased;
    private boolean mUpdated;
    
    ManagedCursor(Cursor paramCursor)
    {
      this.mCursor = paramCursor;
      this.mReleased = false;
      this.mUpdated = false;
    }
  }
  
  private static class ManagedDialog
  {
    Bundle mArgs;
    Dialog mDialog;
  }
  
  static final class NonConfigurationInstances
  {
    Object activity;
    HashMap<String, Object> children;
    FragmentManagerNonConfig fragments;
    ArrayMap<String, LoaderManager> loaders;
    VoiceInteractor voiceInteractor;
  }
  
  public static abstract interface TranslucentConversionListener
  {
    public abstract void onTranslucentConversionComplete(boolean paramBoolean);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/Activity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */