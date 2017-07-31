package com.android.server.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.GestureDescription.MotionEventGenerator;
import android.accessibilityservice.IAccessibilityServiceClient;
import android.accessibilityservice.IAccessibilityServiceConnection;
import android.accessibilityservice.IAccessibilityServiceConnection.Stub;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.app.StatusBarManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.UserInfo;
import android.database.ContentObserver;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Region.Op;
import android.hardware.display.DisplayManager;
import android.hardware.input.InputManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.text.TextUtils.SimpleStringSplitter;
import android.util.Slog;
import android.util.SparseArray;
import android.view.Display;
import android.view.IWindow;
import android.view.KeyEvent;
import android.view.MagnificationSpec;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowInfo;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManagerInternal;
import android.view.WindowManagerInternal.WindowsForAccessibilityCallback;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityInteractionClient;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;
import android.view.accessibility.IAccessibilityInteractionConnection;
import android.view.accessibility.IAccessibilityInteractionConnectionCallback;
import android.view.accessibility.IAccessibilityManager.Stub;
import android.view.accessibility.IAccessibilityManagerClient;
import com.android.internal.content.PackageMonitor;
import com.android.internal.os.SomeArgs;
import com.android.server.LocalServices;
import com.android.server.statusbar.StatusBarManagerInternal;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.xmlpull.v1.XmlPullParserException;

public class AccessibilityManagerService
  extends IAccessibilityManager.Stub
{
  private static final char COMPONENT_NAME_SEPARATOR = ':';
  private static final boolean DEBUG = false;
  private static final String FUNCTION_DUMP = "dump";
  private static final String FUNCTION_REGISTER_UI_TEST_AUTOMATION_SERVICE = "registerUiTestAutomationService";
  private static final String GET_WINDOW_TOKEN = "getWindowToken";
  private static final String LOG_TAG = "AccessibilityManagerService";
  public static final int MAGNIFICATION_GESTURE_HANDLER_ID = 0;
  private static final int OWN_PROCESS_ID = Process.myPid();
  private static final String TEMPORARY_ENABLE_ACCESSIBILITY_UNTIL_KEYGUARD_REMOVED = "temporaryEnableAccessibilityStateUntilKeyguardRemoved";
  private static final int WAIT_FOR_USER_STATE_FULLY_INITIALIZED_MILLIS = 3000;
  private static final int WAIT_MOTION_INJECTOR_TIMEOUT_MILLIS = 1000;
  private static final int WAIT_WINDOWS_TIMEOUT_MILLIS = 5000;
  private static final int WINDOW_ID_UNKNOWN = -1;
  private static final ComponentName sFakeAccessibilityServiceComponentName = new ComponentName("foo.bar", "FakeService");
  private static int sIdCounter = 1;
  private static int sNextWindowId;
  private final ActivityManager mActivityManager;
  private final Context mContext;
  private int mCurrentUserId = 0;
  private AlertDialog mEnableTouchExplorationDialog;
  private final List<AccessibilityServiceInfo> mEnabledServicesForFeedbackTempList = new ArrayList();
  private final RemoteCallbackList<IAccessibilityManagerClient> mGlobalClients = new RemoteCallbackList();
  private final SparseArray<AccessibilityConnectionWrapper> mGlobalInteractionConnections = new SparseArray();
  private final SparseArray<IBinder> mGlobalWindowTokens = new SparseArray();
  private boolean mHasInputFilter;
  private boolean mInitialized;
  private AccessibilityInputFilter mInputFilter;
  private InteractionBridge mInteractionBridge;
  private KeyEventDispatcher mKeyEventDispatcher;
  private final Object mLock = new Object();
  private MagnificationController mMagnificationController;
  private final MainHandler mMainHandler;
  private MotionEventInjector mMotionEventInjector;
  private final PackageManager mPackageManager;
  private final PowerManager mPowerManager;
  private final SecurityPolicy mSecurityPolicy;
  private final TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
  private final List<AccessibilityServiceInfo> mTempAccessibilityServiceInfoList = new ArrayList();
  private final Set<ComponentName> mTempComponentNameSet = new HashSet();
  private final Point mTempPoint = new Point();
  private final Rect mTempRect = new Rect();
  private final Rect mTempRect1 = new Rect();
  private final UserManager mUserManager;
  private final SparseArray<UserState> mUserStates = new SparseArray();
  private final WindowManagerInternal mWindowManagerService;
  private WindowsForAccessibilityCallback mWindowsForAccessibilityCallback;
  
  public AccessibilityManagerService(Context paramContext)
  {
    this.mContext = paramContext;
    this.mActivityManager = ((ActivityManager)paramContext.getSystemService("activity"));
    this.mPackageManager = this.mContext.getPackageManager();
    this.mPowerManager = ((PowerManager)this.mContext.getSystemService("power"));
    this.mWindowManagerService = ((WindowManagerInternal)LocalServices.getService(WindowManagerInternal.class));
    this.mUserManager = ((UserManager)paramContext.getSystemService("user"));
    this.mSecurityPolicy = new SecurityPolicy();
    this.mMainHandler = new MainHandler(this.mContext.getMainLooper());
    registerBroadcastReceivers();
    new AccessibilityContentObserver(this.mMainHandler).register(paramContext.getContentResolver());
  }
  
  private void addServiceLocked(Service paramService, UserState paramUserState)
  {
    try
    {
      if (!paramUserState.mBoundServices.contains(paramService))
      {
        paramService.onAdded();
        paramUserState.mBoundServices.add(paramService);
        paramUserState.mComponentNameToServiceMap.put(paramService.mComponentName, paramService);
      }
      return;
    }
    catch (RemoteException paramService) {}
  }
  
  private boolean canDispatchEventToServiceLocked(Service paramService, AccessibilityEvent paramAccessibilityEvent)
  {
    if (!paramService.canReceiveEventsLocked()) {
      return false;
    }
    if ((paramAccessibilityEvent.getWindowId() == -1) || (paramAccessibilityEvent.isImportantForAccessibility())) {}
    while ((paramService.mFetchFlags & 0x8) != 0)
    {
      int i = paramAccessibilityEvent.getEventType();
      if ((paramService.mEventTypes & i) == i) {
        break;
      }
      return false;
    }
    return false;
    Set localSet = paramService.mPackageNames;
    if (paramAccessibilityEvent.getPackageName() != null) {}
    for (paramService = paramAccessibilityEvent.getPackageName().toString(); !localSet.isEmpty(); paramService = null) {
      return localSet.contains(paramService);
    }
    return true;
  }
  
  private boolean canRequestAndRequestsEnhancedWebAccessibilityLocked(Service paramService)
  {
    if ((paramService.canReceiveEventsLocked()) && (paramService.mRequestEnhancedWebAccessibility))
    {
      if ((paramService.mIsAutomation) || ((paramService.mAccessibilityServiceInfo.getCapabilities() & 0x4) != 0)) {
        return true;
      }
    }
    else {
      return false;
    }
    return false;
  }
  
  private boolean canRequestAndRequestsTouchExplorationLocked(Service paramService)
  {
    if ((paramService.canReceiveEventsLocked()) && (paramService.mRequestTouchExplorationMode))
    {
      if (paramService.mIsAutomation) {
        return true;
      }
    }
    else {
      return false;
    }
    if (paramService.mResolveInfo.serviceInfo.applicationInfo.targetSdkVersion <= 17)
    {
      if (getUserStateLocked(paramService.mUserId).mTouchExplorationGrantedServices.contains(paramService.mComponentName)) {
        return true;
      }
      if ((this.mEnableTouchExplorationDialog == null) || (!this.mEnableTouchExplorationDialog.isShowing())) {}
    }
    while ((paramService.mAccessibilityServiceInfo.getCapabilities() & 0x2) == 0)
    {
      return false;
      this.mMainHandler.obtainMessage(7, paramService).sendToTarget();
      return false;
    }
    return true;
  }
  
  private void ensureWindowsAvailableTimed()
  {
    synchronized (this.mLock)
    {
      Object localObject2 = this.mSecurityPolicy.mWindows;
      if (localObject2 != null) {
        return;
      }
      if (this.mWindowsForAccessibilityCallback == null) {
        onUserStateChangedLocked(getCurrentUserStateLocked());
      }
      localObject2 = this.mWindowsForAccessibilityCallback;
      if (localObject2 == null) {
        return;
      }
      long l1 = SystemClock.uptimeMillis();
      while (this.mSecurityPolicy.mWindows == null)
      {
        long l2 = SystemClock.uptimeMillis();
        l2 = 5000L - (l2 - l1);
        if (l2 <= 0L) {
          return;
        }
        try
        {
          this.mLock.wait(l2);
        }
        catch (InterruptedException localInterruptedException) {}
      }
      return;
    }
  }
  
  private int findWindowIdLocked(IBinder paramIBinder)
  {
    int i = this.mGlobalWindowTokens.indexOfValue(paramIBinder);
    if (i >= 0) {
      return this.mGlobalWindowTokens.keyAt(i);
    }
    UserState localUserState = getCurrentUserStateLocked();
    i = localUserState.mWindowTokens.indexOfValue(paramIBinder);
    if (i >= 0) {
      return localUserState.mWindowTokens.keyAt(i);
    }
    return -1;
  }
  
  private MagnificationSpec getCompatibleMagnificationSpecLocked(int paramInt)
  {
    IBinder localIBinder2 = (IBinder)this.mGlobalWindowTokens.get(paramInt);
    IBinder localIBinder1 = localIBinder2;
    if (localIBinder2 == null) {
      localIBinder1 = (IBinder)getCurrentUserStateLocked().mWindowTokens.get(paramInt);
    }
    if (localIBinder1 != null) {
      return this.mWindowManagerService.getCompatibleMagnificationSpecForWindow(localIBinder1);
    }
    return null;
  }
  
  private UserState getCurrentUserStateLocked()
  {
    return getUserStateLocked(this.mCurrentUserId);
  }
  
  private InteractionBridge getInteractionBridgeLocked()
  {
    if (this.mInteractionBridge == null) {
      this.mInteractionBridge = new InteractionBridge();
    }
    return this.mInteractionBridge;
  }
  
  private KeyEventDispatcher getKeyEventDispatcher()
  {
    if (this.mKeyEventDispatcher == null) {
      this.mKeyEventDispatcher = new KeyEventDispatcher(this.mMainHandler, 8, this.mLock, this.mPowerManager);
    }
    return this.mKeyEventDispatcher;
  }
  
  private UserState getUserStateLocked(int paramInt)
  {
    UserState localUserState2 = (UserState)this.mUserStates.get(paramInt);
    UserState localUserState1 = localUserState2;
    if (localUserState2 == null)
    {
      localUserState1 = new UserState(paramInt);
      this.mUserStates.put(paramInt, localUserState1);
    }
    return localUserState1;
  }
  
  private void notifyAccessibilityServicesDelayedLocked(AccessibilityEvent paramAccessibilityEvent, boolean paramBoolean)
  {
    try
    {
      UserState localUserState = getCurrentUserStateLocked();
      int i = 0;
      int j = localUserState.mBoundServices.size();
      while (i < j)
      {
        Service localService = (Service)localUserState.mBoundServices.get(i);
        if ((localService.mIsDefault == paramBoolean) && (canDispatchEventToServiceLocked(localService, paramAccessibilityEvent))) {
          localService.notifyAccessibilityEvent(paramAccessibilityEvent);
        }
        i += 1;
      }
      return;
    }
    catch (IndexOutOfBoundsException paramAccessibilityEvent) {}
  }
  
  private void notifyClearAccessibilityCacheLocked()
  {
    UserState localUserState = getCurrentUserStateLocked();
    int i = localUserState.mBoundServices.size() - 1;
    while (i >= 0)
    {
      ((Service)localUserState.mBoundServices.get(i)).notifyClearAccessibilityNodeInfoCache();
      i -= 1;
    }
  }
  
  private boolean notifyGestureLocked(int paramInt, boolean paramBoolean)
  {
    UserState localUserState = getCurrentUserStateLocked();
    int i = localUserState.mBoundServices.size() - 1;
    while (i >= 0)
    {
      Service localService = (Service)localUserState.mBoundServices.get(i);
      if ((localService.mRequestTouchExplorationMode) && (localService.mIsDefault == paramBoolean))
      {
        localService.notifyGesture(paramInt);
        return true;
      }
      i -= 1;
    }
    return false;
  }
  
  private void notifyMagnificationChangedLocked(Region paramRegion, float paramFloat1, float paramFloat2, float paramFloat3)
  {
    UserState localUserState = getCurrentUserStateLocked();
    int i = localUserState.mBoundServices.size() - 1;
    while (i >= 0)
    {
      ((Service)localUserState.mBoundServices.get(i)).notifyMagnificationChangedLocked(paramRegion, paramFloat1, paramFloat2, paramFloat3);
      i -= 1;
    }
  }
  
  private void notifySoftKeyboardShowModeChangedLocked(int paramInt)
  {
    UserState localUserState = getCurrentUserStateLocked();
    int i = localUserState.mBoundServices.size() - 1;
    while (i >= 0)
    {
      ((Service)localUserState.mBoundServices.get(i)).notifySoftKeyboardShowModeChangedLocked(paramInt);
      i -= 1;
    }
  }
  
  private void onUserStateChangedLocked(UserState paramUserState)
  {
    this.mInitialized = true;
    updateLegacyCapabilitiesLocked(paramUserState);
    updateServicesLocked(paramUserState);
    updateWindowsForAccessibilityCallbackLocked(paramUserState);
    updateAccessibilityFocusBehaviorLocked(paramUserState);
    updateFilterKeyEventsLocked(paramUserState);
    updateTouchExplorationLocked(paramUserState);
    updatePerformGesturesLocked(paramUserState);
    updateEnhancedWebAccessibilityLocked(paramUserState);
    updateDisplayDaltonizerLocked(paramUserState);
    updateDisplayInversionLocked(paramUserState);
    updateMagnificationLocked(paramUserState);
    updateSoftKeyboardShowModeLocked(paramUserState);
    scheduleUpdateInputFilter(paramUserState);
    scheduleUpdateClientsIfNeededLocked(paramUserState);
  }
  
  private void persistComponentNamesToSettingLocked(String paramString, Set<ComponentName> paramSet, int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    paramSet = paramSet.iterator();
    while (paramSet.hasNext())
    {
      ComponentName localComponentName = (ComponentName)paramSet.next();
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append(':');
      }
      localStringBuilder.append(localComponentName.flattenToShortString());
    }
    long l = Binder.clearCallingIdentity();
    try
    {
      Settings.Secure.putStringForUser(this.mContext.getContentResolver(), paramString, localStringBuilder.toString(), paramInt);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private boolean readAutoclickEnabledSettingLocked(UserState paramUserState)
  {
    if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "accessibility_autoclick_enabled", 0, paramUserState.mUserId) == 1) {}
    for (boolean bool = true; bool != paramUserState.mIsAutoclickEnabled; bool = false)
    {
      paramUserState.mIsAutoclickEnabled = bool;
      return true;
    }
    return false;
  }
  
  private void readComponentNamesFromSettingLocked(String paramString, int paramInt, Set<ComponentName> paramSet)
  {
    readComponentNamesFromStringLocked(Settings.Secure.getStringForUser(this.mContext.getContentResolver(), paramString, paramInt), paramSet, false);
  }
  
  private void readComponentNamesFromStringLocked(String paramString, Set<ComponentName> paramSet, boolean paramBoolean)
  {
    if (!paramBoolean) {
      paramSet.clear();
    }
    if (paramString != null)
    {
      TextUtils.SimpleStringSplitter localSimpleStringSplitter = this.mStringColonSplitter;
      localSimpleStringSplitter.setString(paramString);
      while (localSimpleStringSplitter.hasNext())
      {
        paramString = localSimpleStringSplitter.next();
        if ((paramString != null) && (paramString.length() > 0))
        {
          paramString = ComponentName.unflattenFromString(paramString);
          if (paramString != null) {
            paramSet.add(paramString);
          }
        }
      }
    }
  }
  
  private boolean readConfigurationForUserStateLocked(UserState paramUserState)
  {
    return readInstalledAccessibilityServiceLocked(paramUserState) | readEnabledAccessibilityServicesLocked(paramUserState) | readTouchExplorationGrantedAccessibilityServicesLocked(paramUserState) | readTouchExplorationEnabledSettingLocked(paramUserState) | readHighTextContrastEnabledSettingLocked(paramUserState) | readEnhancedWebAccessibilityEnabledChangedLocked(paramUserState) | readDisplayMagnificationEnabledSettingLocked(paramUserState) | readAutoclickEnabledSettingLocked(paramUserState);
  }
  
  private boolean readDisplayMagnificationEnabledSettingLocked(UserState paramUserState)
  {
    if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "accessibility_display_magnification_enabled", 0, paramUserState.mUserId) == 1) {}
    for (boolean bool = true; bool != paramUserState.mIsDisplayMagnificationEnabled; bool = false)
    {
      paramUserState.mIsDisplayMagnificationEnabled = bool;
      return true;
    }
    return false;
  }
  
  private boolean readEnabledAccessibilityServicesLocked(UserState paramUserState)
  {
    this.mTempComponentNameSet.clear();
    readComponentNamesFromSettingLocked("enabled_accessibility_services", paramUserState.mUserId, this.mTempComponentNameSet);
    if (!this.mTempComponentNameSet.equals(paramUserState.mEnabledServices))
    {
      paramUserState.mEnabledServices.clear();
      paramUserState.mEnabledServices.addAll(this.mTempComponentNameSet);
      if (UserState.-get1(paramUserState) != null) {
        paramUserState.mEnabledServices.add(sFakeAccessibilityServiceComponentName);
      }
      this.mTempComponentNameSet.clear();
      return true;
    }
    this.mTempComponentNameSet.clear();
    return false;
  }
  
  private boolean readEnhancedWebAccessibilityEnabledChangedLocked(UserState paramUserState)
  {
    if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "accessibility_script_injection", 0, paramUserState.mUserId) == 1) {}
    for (boolean bool = true; bool != paramUserState.mIsEnhancedWebAccessibilityEnabled; bool = false)
    {
      paramUserState.mIsEnhancedWebAccessibilityEnabled = bool;
      return true;
    }
    return false;
  }
  
  private boolean readHighTextContrastEnabledSettingLocked(UserState paramUserState)
  {
    if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "high_text_contrast_enabled", 0, paramUserState.mUserId) == 1) {}
    for (boolean bool = true; bool != paramUserState.mIsTextHighContrastEnabled; bool = false)
    {
      paramUserState.mIsTextHighContrastEnabled = bool;
      return true;
    }
    return false;
  }
  
  private boolean readInstalledAccessibilityServiceLocked(UserState paramUserState)
  {
    this.mTempAccessibilityServiceInfoList.clear();
    List localList = this.mPackageManager.queryIntentServicesAsUser(new Intent("android.accessibilityservice.AccessibilityService"), 819332, this.mCurrentUserId);
    int i = 0;
    int j = localList.size();
    if (i < j)
    {
      Object localObject = (ResolveInfo)localList.get(i);
      ServiceInfo localServiceInfo = ((ResolveInfo)localObject).serviceInfo;
      if (!"android.permission.BIND_ACCESSIBILITY_SERVICE".equals(localServiceInfo.permission)) {
        Slog.w("AccessibilityManagerService", "Skipping accessibilty service " + new ComponentName(localServiceInfo.packageName, localServiceInfo.name).flattenToShortString() + ": it does not require the permission " + "android.permission.BIND_ACCESSIBILITY_SERVICE");
      }
      for (;;)
      {
        i += 1;
        break;
        try
        {
          localObject = new AccessibilityServiceInfo((ResolveInfo)localObject, this.mContext);
          this.mTempAccessibilityServiceInfoList.add(localObject);
        }
        catch (XmlPullParserException|IOException localXmlPullParserException)
        {
          Slog.e("AccessibilityManagerService", "Error while initializing AccessibilityServiceInfo", localXmlPullParserException);
        }
      }
    }
    if (!this.mTempAccessibilityServiceInfoList.equals(paramUserState.mInstalledServices))
    {
      paramUserState.mInstalledServices.clear();
      paramUserState.mInstalledServices.addAll(this.mTempAccessibilityServiceInfoList);
      this.mTempAccessibilityServiceInfoList.clear();
      return true;
    }
    this.mTempAccessibilityServiceInfoList.clear();
    return false;
  }
  
  private boolean readSoftKeyboardShowModeChangedLocked(UserState paramUserState)
  {
    int i = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "accessibility_soft_keyboard_mode", 0, paramUserState.mUserId);
    if (i != paramUserState.mSoftKeyboardShowMode)
    {
      paramUserState.mSoftKeyboardShowMode = i;
      return true;
    }
    return false;
  }
  
  private boolean readTouchExplorationEnabledSettingLocked(UserState paramUserState)
  {
    if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "touch_exploration_enabled", 0, paramUserState.mUserId) == 1) {}
    for (boolean bool = true; bool != paramUserState.mIsTouchExplorationEnabled; bool = false)
    {
      paramUserState.mIsTouchExplorationEnabled = bool;
      return true;
    }
    return false;
  }
  
  private boolean readTouchExplorationGrantedAccessibilityServicesLocked(UserState paramUserState)
  {
    this.mTempComponentNameSet.clear();
    readComponentNamesFromSettingLocked("touch_exploration_granted_accessibility_services", paramUserState.mUserId, this.mTempComponentNameSet);
    if (!this.mTempComponentNameSet.equals(paramUserState.mTouchExplorationGrantedServices))
    {
      paramUserState.mTouchExplorationGrantedServices.clear();
      paramUserState.mTouchExplorationGrantedServices.addAll(this.mTempComponentNameSet);
      this.mTempComponentNameSet.clear();
      return true;
    }
    this.mTempComponentNameSet.clear();
    return false;
  }
  
  private void registerBroadcastReceivers()
  {
    new PackageMonitor()
    {
      public boolean onHandleForceStop(Intent arg1, String[] paramAnonymousArrayOfString, int paramAnonymousInt, boolean paramAnonymousBoolean)
      {
        synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
        {
          int i = getChangingUserId();
          paramAnonymousInt = AccessibilityManagerService.-get3(AccessibilityManagerService.this);
          if (i != paramAnonymousInt) {
            return false;
          }
          AccessibilityManagerService.UserState localUserState = AccessibilityManagerService.-wrap12(AccessibilityManagerService.this, i);
          Iterator localIterator = localUserState.mEnabledServices.iterator();
          while (localIterator.hasNext())
          {
            String str = ((ComponentName)localIterator.next()).getPackageName();
            paramAnonymousInt = 0;
            int j = paramAnonymousArrayOfString.length;
            while (paramAnonymousInt < j)
            {
              boolean bool = str.equals(paramAnonymousArrayOfString[paramAnonymousInt]);
              if (bool)
              {
                if (!paramAnonymousBoolean) {
                  return true;
                }
                localIterator.remove();
                AccessibilityManagerService.-wrap19(AccessibilityManagerService.this, "enabled_accessibility_services", localUserState.mEnabledServices, i);
                if (!localUserState.isUiAutomationSuppressingOtherServices()) {
                  AccessibilityManagerService.-wrap18(AccessibilityManagerService.this, localUserState);
                }
              }
              paramAnonymousInt += 1;
            }
          }
          return false;
        }
      }
      
      public void onPackageRemoved(String paramAnonymousString, int paramAnonymousInt)
      {
        synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
        {
          paramAnonymousInt = getChangingUserId();
          int i = AccessibilityManagerService.-get3(AccessibilityManagerService.this);
          if (paramAnonymousInt != i) {
            return;
          }
          AccessibilityManagerService.UserState localUserState = AccessibilityManagerService.-wrap12(AccessibilityManagerService.this, paramAnonymousInt);
          Iterator localIterator = localUserState.mEnabledServices.iterator();
          while (localIterator.hasNext())
          {
            ComponentName localComponentName = (ComponentName)localIterator.next();
            if (localComponentName.getPackageName().equals(paramAnonymousString))
            {
              localIterator.remove();
              AccessibilityManagerService.-wrap19(AccessibilityManagerService.this, "enabled_accessibility_services", localUserState.mEnabledServices, paramAnonymousInt);
              localUserState.mTouchExplorationGrantedServices.remove(localComponentName);
              AccessibilityManagerService.-wrap19(AccessibilityManagerService.this, "touch_exploration_granted_accessibility_services", localUserState.mTouchExplorationGrantedServices, paramAnonymousInt);
              if (!localUserState.isUiAutomationSuppressingOtherServices()) {
                AccessibilityManagerService.-wrap18(AccessibilityManagerService.this, localUserState);
              }
              return;
            }
          }
          return;
        }
      }
      
      public void onPackageUpdateFinished(String paramAnonymousString, int paramAnonymousInt)
      {
        for (;;)
        {
          synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
          {
            paramAnonymousInt = getChangingUserId();
            int i = AccessibilityManagerService.-get3(AccessibilityManagerService.this);
            if (paramAnonymousInt != i) {
              return;
            }
            AccessibilityManagerService.UserState localUserState = AccessibilityManagerService.-wrap12(AccessibilityManagerService.this, paramAnonymousInt);
            i = 0;
            paramAnonymousInt = localUserState.mBoundServices.size() - 1;
            if (paramAnonymousInt >= 0)
            {
              AccessibilityManagerService.Service localService = (AccessibilityManagerService.Service)localUserState.mBoundServices.get(paramAnonymousInt);
              if (localService.mComponentName.getPackageName().equals(paramAnonymousString))
              {
                localService.unbindLocked();
                i = 1;
              }
            }
            else
            {
              if (i != 0) {
                AccessibilityManagerService.-wrap18(AccessibilityManagerService.this, localUserState);
              }
              return;
            }
          }
          paramAnonymousInt -= 1;
        }
      }
      
      public void onSomePackagesChanged()
      {
        synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
        {
          int i = getChangingUserId();
          int j = AccessibilityManagerService.-get3(AccessibilityManagerService.this);
          if (i != j) {
            return;
          }
          AccessibilityManagerService.UserState localUserState = AccessibilityManagerService.-wrap11(AccessibilityManagerService.this);
          localUserState.mInstalledServices.clear();
          if ((!localUserState.isUiAutomationSuppressingOtherServices()) && (AccessibilityManagerService.-wrap2(AccessibilityManagerService.this, localUserState))) {
            AccessibilityManagerService.-wrap18(AccessibilityManagerService.this, localUserState);
          }
          return;
        }
      }
    }.register(this.mContext, null, UserHandle.ALL, true);
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.USER_SWITCHED");
    localIntentFilter.addAction("android.intent.action.USER_UNLOCKED");
    localIntentFilter.addAction("android.intent.action.USER_REMOVED");
    localIntentFilter.addAction("android.intent.action.USER_PRESENT");
    localIntentFilter.addAction("android.os.action.SETTING_RESTORED");
    this.mContext.registerReceiverAsUser(new BroadcastReceiver()
    {
      public void onReceive(Context arg1, Intent arg2)
      {
        ??? = ???.getAction();
        if ("android.intent.action.USER_SWITCHED".equals(???))
        {
          AccessibilityManagerService.-wrap24(AccessibilityManagerService.this, ???.getIntExtra("android.intent.extra.user_handle", 0));
          return;
        }
        if ("android.intent.action.USER_UNLOCKED".equals(???))
        {
          AccessibilityManagerService.-wrap26(AccessibilityManagerService.this, ???.getIntExtra("android.intent.extra.user_handle", 0));
          return;
        }
        if ("android.intent.action.USER_REMOVED".equals(???))
        {
          AccessibilityManagerService.-wrap22(AccessibilityManagerService.this, ???.getIntExtra("android.intent.extra.user_handle", 0));
          return;
        }
        if ("android.intent.action.USER_PRESENT".equals(???)) {}
        for (;;)
        {
          synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
          {
            AccessibilityManagerService.UserState localUserState = AccessibilityManagerService.-wrap11(AccessibilityManagerService.this);
            ??? = ???;
            if (!localUserState.isUiAutomationSuppressingOtherServices())
            {
              ??? = ???;
              if (AccessibilityManagerService.-wrap2(AccessibilityManagerService.this, localUserState))
              {
                AccessibilityManagerService.-wrap18(AccessibilityManagerService.this, localUserState);
                ??? = ???;
              }
            }
            return;
          }
          if ((!"android.os.action.SETTING_RESTORED".equals(???)) || (!"enabled_accessibility_services".equals(???.getStringExtra("setting_name")))) {
            break;
          }
          synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
          {
            AccessibilityManagerService.this.restoreEnabledAccessibilityServicesLocked(???.getStringExtra("previous_value"), ???.getStringExtra("new_value"));
          }
        }
      }
    }, UserHandle.ALL, localIntentFilter, null, null);
  }
  
  private int removeAccessibilityInteractionConnectionInternalLocked(IBinder paramIBinder, SparseArray<IBinder> paramSparseArray, SparseArray<AccessibilityConnectionWrapper> paramSparseArray1)
  {
    int j = paramSparseArray.size();
    int i = 0;
    while (i < j)
    {
      if (paramSparseArray.valueAt(i) == paramIBinder)
      {
        j = paramSparseArray.keyAt(i);
        paramSparseArray.removeAt(i);
        ((AccessibilityConnectionWrapper)paramSparseArray1.get(j)).unlinkToDeath();
        paramSparseArray1.remove(j);
        return j;
      }
      i += 1;
    }
    return -1;
  }
  
  private void removeAccessibilityInteractionConnectionLocked(int paramInt1, int paramInt2)
  {
    if (paramInt2 == -1)
    {
      this.mGlobalWindowTokens.remove(paramInt1);
      this.mGlobalInteractionConnections.remove(paramInt1);
      return;
    }
    UserState localUserState = getCurrentUserStateLocked();
    localUserState.mWindowTokens.remove(paramInt1);
    localUserState.mInteractionConnections.remove(paramInt1);
  }
  
  private void removeServiceLocked(Service paramService, UserState paramUserState)
  {
    paramUserState.mBoundServices.remove(paramService);
    paramService.onRemoved();
    paramUserState.mComponentNameToServiceMap.clear();
    int i = 0;
    while (i < paramUserState.mBoundServices.size())
    {
      paramService = (Service)paramUserState.mBoundServices.get(i);
      paramUserState.mComponentNameToServiceMap.put(paramService.mComponentName, paramService);
      i += 1;
    }
  }
  
  private void removeUser(int paramInt)
  {
    synchronized (this.mLock)
    {
      this.mUserStates.remove(paramInt);
      return;
    }
  }
  
  private void scheduleUpdateClientsIfNeededLocked(UserState paramUserState)
  {
    int i = paramUserState.getClientState();
    if ((paramUserState.mLastSentClientState != i) && ((this.mGlobalClients.getRegisteredCallbackCount() > 0) || (paramUserState.mClients.getRegisteredCallbackCount() > 0)))
    {
      paramUserState.mLastSentClientState = i;
      this.mMainHandler.obtainMessage(2, i, paramUserState.mUserId).sendToTarget();
    }
  }
  
  private void scheduleUpdateInputFilter(UserState paramUserState)
  {
    this.mMainHandler.obtainMessage(6, paramUserState).sendToTarget();
  }
  
  private void showEnableTouchExplorationDialog(final Service paramService)
  {
    synchronized (this.mLock)
    {
      String str = paramService.mResolveInfo.loadLabel(this.mContext.getPackageManager()).toString();
      final UserState localUserState = getCurrentUserStateLocked();
      boolean bool = localUserState.mIsTouchExplorationEnabled;
      if (bool) {
        return;
      }
      if (this.mEnableTouchExplorationDialog != null)
      {
        bool = this.mEnableTouchExplorationDialog.isShowing();
        if (bool) {
          return;
        }
      }
      this.mEnableTouchExplorationDialog = new AlertDialog.Builder(this.mContext).setIconAttribute(16843605).setPositiveButton(17039370, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          localUserState.mTouchExplorationGrantedServices.add(paramService.mComponentName);
          AccessibilityManagerService.-wrap19(AccessibilityManagerService.this, "touch_exploration_granted_accessibility_services", localUserState.mTouchExplorationGrantedServices, localUserState.mUserId);
          paramAnonymousDialogInterface = AccessibilityManagerService.-wrap12(AccessibilityManagerService.this, paramService.mUserId);
          paramAnonymousDialogInterface.mIsTouchExplorationEnabled = true;
          long l = Binder.clearCallingIdentity();
          try
          {
            Settings.Secure.putIntForUser(AccessibilityManagerService.-get2(AccessibilityManagerService.this).getContentResolver(), "touch_exploration_enabled", 1, paramService.mUserId);
            Binder.restoreCallingIdentity(l);
            AccessibilityManagerService.-wrap18(AccessibilityManagerService.this, paramAnonymousDialogInterface);
            return;
          }
          finally
          {
            Binder.restoreCallingIdentity(l);
          }
        }
      }).setNegativeButton(17039360, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          paramAnonymousDialogInterface.dismiss();
        }
      }).setTitle(17040218).setMessage(this.mContext.getString(17040219, new Object[] { str })).create();
      this.mEnableTouchExplorationDialog.getWindow().setType(2003);
      paramService = this.mEnableTouchExplorationDialog.getWindow().getAttributes();
      paramService.privateFlags |= 0x10;
      this.mEnableTouchExplorationDialog.setCanceledOnTouchOutside(true);
      this.mEnableTouchExplorationDialog.show();
      return;
    }
  }
  
  private void switchUser(int paramInt)
  {
    synchronized (this.mLock)
    {
      if (this.mCurrentUserId == paramInt)
      {
        boolean bool = this.mInitialized;
        if (bool) {
          return;
        }
      }
      UserState localUserState = getCurrentUserStateLocked();
      localUserState.onSwitchToAnotherUser();
      if (localUserState.mClients.getRegisteredCallbackCount() > 0) {
        this.mMainHandler.obtainMessage(3, localUserState.mUserId, 0).sendToTarget();
      }
      if (((UserManager)this.mContext.getSystemService("user")).getUsers().size() > 1)
      {
        i = 1;
        this.mCurrentUserId = paramInt;
        localUserState = getCurrentUserStateLocked();
        if (UserState.-get1(localUserState) != null) {
          UserState.-get1(localUserState).binderDied();
        }
        readConfigurationForUserStateLocked(localUserState);
        onUserStateChangedLocked(localUserState);
        if (i != 0) {
          this.mMainHandler.sendEmptyMessageDelayed(5, 3000L);
        }
        return;
      }
      int i = 0;
    }
  }
  
  private void unbindAllServicesLocked(UserState paramUserState)
  {
    paramUserState = paramUserState.mBoundServices;
    int i = 0;
    int k;
    for (int j = paramUserState.size(); i < j; j = k)
    {
      k = j;
      int m = i;
      if (((Service)paramUserState.get(i)).unbindLocked())
      {
        m = i - 1;
        k = j - 1;
      }
      i = m + 1;
    }
  }
  
  private void unlockUser(int paramInt)
  {
    synchronized (this.mLock)
    {
      if (SecurityPolicy.-wrap2(this.mSecurityPolicy, paramInt) == this.mCurrentUserId) {
        onUserStateChangedLocked(getUserStateLocked(this.mCurrentUserId));
      }
      return;
    }
  }
  
  /* Error */
  private void updateAccessibilityEnabledSetting(UserState paramUserState)
  {
    // Byte code:
    //   0: invokestatic 783	android/os/Binder:clearCallingIdentity	()J
    //   3: lstore_3
    //   4: aload_0
    //   5: getfield 196	com/android/server/accessibility/AccessibilityManagerService:mContext	Landroid/content/Context;
    //   8: invokevirtual 485	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   11: astore 5
    //   13: aload_1
    //   14: invokevirtual 1108	com/android/server/accessibility/AccessibilityManagerService$UserState:isHandlingAccessibilityEvents	()Z
    //   17: ifeq +24 -> 41
    //   20: iconst_1
    //   21: istore_2
    //   22: aload 5
    //   24: ldc_w 1110
    //   27: iload_2
    //   28: aload_1
    //   29: getfield 798	com/android/server/accessibility/AccessibilityManagerService$UserState:mUserId	I
    //   32: invokestatic 1114	android/provider/Settings$Secure:putIntForUser	(Landroid/content/ContentResolver;Ljava/lang/String;II)Z
    //   35: pop
    //   36: lload_3
    //   37: invokestatic 793	android/os/Binder:restoreCallingIdentity	(J)V
    //   40: return
    //   41: iconst_0
    //   42: istore_2
    //   43: goto -21 -> 22
    //   46: astore_1
    //   47: lload_3
    //   48: invokestatic 793	android/os/Binder:restoreCallingIdentity	(J)V
    //   51: aload_1
    //   52: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	53	0	this	AccessibilityManagerService
    //   0	53	1	paramUserState	UserState
    //   21	22	2	i	int
    //   3	45	3	l	long
    //   11	12	5	localContentResolver	ContentResolver
    // Exception table:
    //   from	to	target	type
    //   4	20	46	finally
    //   22	36	46	finally
  }
  
  private void updateAccessibilityFocusBehaviorLocked(UserState paramUserState)
  {
    CopyOnWriteArrayList localCopyOnWriteArrayList = paramUserState.mBoundServices;
    int j = localCopyOnWriteArrayList.size();
    int i = 0;
    while (i < j)
    {
      if (((Service)localCopyOnWriteArrayList.get(i)).canRetrieveInteractiveWindowsLocked())
      {
        paramUserState.mAccessibilityFocusOnlyInActiveWindow = false;
        return;
      }
      i += 1;
    }
    paramUserState.mAccessibilityFocusOnlyInActiveWindow = true;
  }
  
  private void updateDisplayDaltonizerLocked(UserState paramUserState)
  {
    DisplayAdjustmentUtils.applyDaltonizerSetting(this.mContext, paramUserState.mUserId);
  }
  
  private void updateDisplayInversionLocked(UserState paramUserState)
  {
    DisplayAdjustmentUtils.applyInversionSetting(this.mContext, paramUserState.mUserId);
  }
  
  /* Error */
  private void updateEnhancedWebAccessibilityLocked(UserState paramUserState)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore 5
    //   3: aload_1
    //   4: getfield 495	com/android/server/accessibility/AccessibilityManagerService$UserState:mBoundServices	Ljava/util/concurrent/CopyOnWriteArrayList;
    //   7: invokevirtual 685	java/util/concurrent/CopyOnWriteArrayList:size	()I
    //   10: istore_3
    //   11: iconst_0
    //   12: istore_2
    //   13: iload 5
    //   15: istore 4
    //   17: iload_2
    //   18: iload_3
    //   19: if_icmpge +24 -> 43
    //   22: aload_0
    //   23: aload_1
    //   24: getfield 495	com/android/server/accessibility/AccessibilityManagerService$UserState:mBoundServices	Ljava/util/concurrent/CopyOnWriteArrayList;
    //   27: iload_2
    //   28: invokevirtual 686	java/util/concurrent/CopyOnWriteArrayList:get	(I)Ljava/lang/Object;
    //   31: checkcast 29	com/android/server/accessibility/AccessibilityManagerService$Service
    //   34: invokespecial 1131	com/android/server/accessibility/AccessibilityManagerService:canRequestAndRequestsEnhancedWebAccessibilityLocked	(Lcom/android/server/accessibility/AccessibilityManagerService$Service;)Z
    //   37: ifeq +62 -> 99
    //   40: iconst_1
    //   41: istore 4
    //   43: iload 4
    //   45: aload_1
    //   46: getfield 865	com/android/server/accessibility/AccessibilityManagerService$UserState:mIsEnhancedWebAccessibilityEnabled	Z
    //   49: if_icmpeq +49 -> 98
    //   52: aload_1
    //   53: iload 4
    //   55: putfield 865	com/android/server/accessibility/AccessibilityManagerService$UserState:mIsEnhancedWebAccessibilityEnabled	Z
    //   58: invokestatic 783	android/os/Binder:clearCallingIdentity	()J
    //   61: lstore 6
    //   63: aload_0
    //   64: getfield 196	com/android/server/accessibility/AccessibilityManagerService:mContext	Landroid/content/Context;
    //   67: invokevirtual 485	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   70: astore 8
    //   72: iload 4
    //   74: ifeq +32 -> 106
    //   77: iconst_1
    //   78: istore_2
    //   79: aload 8
    //   81: ldc_w 862
    //   84: iload_2
    //   85: aload_1
    //   86: getfield 798	com/android/server/accessibility/AccessibilityManagerService$UserState:mUserId	I
    //   89: invokestatic 1114	android/provider/Settings$Secure:putIntForUser	(Landroid/content/ContentResolver;Ljava/lang/String;II)Z
    //   92: pop
    //   93: lload 6
    //   95: invokestatic 793	android/os/Binder:restoreCallingIdentity	(J)V
    //   98: return
    //   99: iload_2
    //   100: iconst_1
    //   101: iadd
    //   102: istore_2
    //   103: goto -90 -> 13
    //   106: iconst_0
    //   107: istore_2
    //   108: goto -29 -> 79
    //   111: astore_1
    //   112: lload 6
    //   114: invokestatic 793	android/os/Binder:restoreCallingIdentity	(J)V
    //   117: aload_1
    //   118: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	119	0	this	AccessibilityManagerService
    //   0	119	1	paramUserState	UserState
    //   12	96	2	i	int
    //   10	10	3	j	int
    //   15	58	4	bool1	boolean
    //   1	13	5	bool2	boolean
    //   61	52	6	l	long
    //   70	10	8	localContentResolver	ContentResolver
    // Exception table:
    //   from	to	target	type
    //   63	72	111	finally
    //   79	93	111	finally
  }
  
  private void updateFilterKeyEventsLocked(UserState paramUserState)
  {
    int j = paramUserState.mBoundServices.size();
    int i = 0;
    while (i < j)
    {
      Service localService = (Service)paramUserState.mBoundServices.get(i);
      if ((localService.mRequestFilterKeyEvents) && ((localService.mAccessibilityServiceInfo.getCapabilities() & 0x8) != 0))
      {
        paramUserState.mIsFilterKeyEventsEnabled = true;
        return;
      }
      i += 1;
    }
    paramUserState.mIsFilterKeyEventsEnabled = false;
  }
  
  /* Error */
  private void updateInputFilter(UserState paramUserState)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore 6
    //   3: iconst_0
    //   4: istore 5
    //   6: aconst_null
    //   7: astore 8
    //   9: aconst_null
    //   10: astore 7
    //   12: aload_0
    //   13: getfield 223	com/android/server/accessibility/AccessibilityManagerService:mLock	Ljava/lang/Object;
    //   16: astore 9
    //   18: aload 9
    //   20: monitorenter
    //   21: iconst_0
    //   22: istore_3
    //   23: aload_1
    //   24: getfield 843	com/android/server/accessibility/AccessibilityManagerService$UserState:mIsDisplayMagnificationEnabled	Z
    //   27: ifeq +5 -> 32
    //   30: iconst_1
    //   31: istore_3
    //   32: iload_3
    //   33: istore_2
    //   34: aload_0
    //   35: aload_1
    //   36: invokespecial 1140	com/android/server/accessibility/AccessibilityManagerService:userHasMagnificationServicesLocked	(Lcom/android/server/accessibility/AccessibilityManagerService$UserState;)Z
    //   39: ifeq +8 -> 47
    //   42: iload_3
    //   43: bipush 32
    //   45: ior
    //   46: istore_2
    //   47: iload_2
    //   48: istore_3
    //   49: aload_1
    //   50: invokevirtual 1108	com/android/server/accessibility/AccessibilityManagerService$UserState:isHandlingAccessibilityEvents	()Z
    //   53: ifeq +16 -> 69
    //   56: iload_2
    //   57: istore_3
    //   58: aload_1
    //   59: getfield 939	com/android/server/accessibility/AccessibilityManagerService$UserState:mIsTouchExplorationEnabled	Z
    //   62: ifeq +7 -> 69
    //   65: iload_2
    //   66: iconst_2
    //   67: ior
    //   68: istore_3
    //   69: iload_3
    //   70: istore_2
    //   71: aload_1
    //   72: getfield 1137	com/android/server/accessibility/AccessibilityManagerService$UserState:mIsFilterKeyEventsEnabled	Z
    //   75: ifeq +7 -> 82
    //   78: iload_3
    //   79: iconst_4
    //   80: ior
    //   81: istore_2
    //   82: iload_2
    //   83: istore_3
    //   84: aload_1
    //   85: getfield 805	com/android/server/accessibility/AccessibilityManagerService$UserState:mIsAutoclickEnabled	Z
    //   88: ifeq +8 -> 96
    //   91: iload_2
    //   92: bipush 8
    //   94: ior
    //   95: istore_3
    //   96: iload_3
    //   97: istore 4
    //   99: aload_1
    //   100: getfield 1143	com/android/server/accessibility/AccessibilityManagerService$UserState:mIsPerformGesturesEnabled	Z
    //   103: ifeq +9 -> 112
    //   106: iload_3
    //   107: bipush 16
    //   109: ior
    //   110: istore 4
    //   112: iload 4
    //   114: ifeq +79 -> 193
    //   117: iload 5
    //   119: istore_2
    //   120: aload_0
    //   121: getfield 215	com/android/server/accessibility/AccessibilityManagerService:mHasInputFilter	Z
    //   124: ifne +39 -> 163
    //   127: aload_0
    //   128: iconst_1
    //   129: putfield 215	com/android/server/accessibility/AccessibilityManagerService:mHasInputFilter	Z
    //   132: aload_0
    //   133: getfield 219	com/android/server/accessibility/AccessibilityManagerService:mInputFilter	Lcom/android/server/accessibility/AccessibilityInputFilter;
    //   136: ifnonnull +19 -> 155
    //   139: aload_0
    //   140: new 1145	com/android/server/accessibility/AccessibilityInputFilter
    //   143: dup
    //   144: aload_0
    //   145: getfield 196	com/android/server/accessibility/AccessibilityManagerService:mContext	Landroid/content/Context;
    //   148: aload_0
    //   149: invokespecial 1148	com/android/server/accessibility/AccessibilityInputFilter:<init>	(Landroid/content/Context;Lcom/android/server/accessibility/AccessibilityManagerService;)V
    //   152: putfield 219	com/android/server/accessibility/AccessibilityManagerService:mInputFilter	Lcom/android/server/accessibility/AccessibilityInputFilter;
    //   155: aload_0
    //   156: getfield 219	com/android/server/accessibility/AccessibilityManagerService:mInputFilter	Lcom/android/server/accessibility/AccessibilityInputFilter;
    //   159: astore 7
    //   161: iconst_1
    //   162: istore_2
    //   163: aload_0
    //   164: getfield 219	com/android/server/accessibility/AccessibilityManagerService:mInputFilter	Lcom/android/server/accessibility/AccessibilityInputFilter;
    //   167: aload_1
    //   168: getfield 798	com/android/server/accessibility/AccessibilityManagerService$UserState:mUserId	I
    //   171: iload 4
    //   173: invokevirtual 1151	com/android/server/accessibility/AccessibilityInputFilter:setUserAndEnabledFeatures	(II)V
    //   176: aload 9
    //   178: monitorexit
    //   179: iload_2
    //   180: ifeq +12 -> 192
    //   183: aload_0
    //   184: getfield 184	com/android/server/accessibility/AccessibilityManagerService:mWindowManagerService	Landroid/view/WindowManagerInternal;
    //   187: aload 7
    //   189: invokevirtual 1155	android/view/WindowManagerInternal:setInputFilter	(Landroid/view/IInputFilter;)V
    //   192: return
    //   193: aload 8
    //   195: astore 7
    //   197: iload 6
    //   199: istore_2
    //   200: aload_0
    //   201: getfield 215	com/android/server/accessibility/AccessibilityManagerService:mHasInputFilter	Z
    //   204: ifeq -28 -> 176
    //   207: aload_0
    //   208: iconst_0
    //   209: putfield 215	com/android/server/accessibility/AccessibilityManagerService:mHasInputFilter	Z
    //   212: aload_0
    //   213: getfield 219	com/android/server/accessibility/AccessibilityManagerService:mInputFilter	Lcom/android/server/accessibility/AccessibilityInputFilter;
    //   216: aload_1
    //   217: getfield 798	com/android/server/accessibility/AccessibilityManagerService$UserState:mUserId	I
    //   220: iconst_0
    //   221: invokevirtual 1151	com/android/server/accessibility/AccessibilityInputFilter:setUserAndEnabledFeatures	(II)V
    //   224: aconst_null
    //   225: astore 7
    //   227: iconst_1
    //   228: istore_2
    //   229: goto -53 -> 176
    //   232: astore_1
    //   233: aload 9
    //   235: monitorexit
    //   236: aload_1
    //   237: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	238	0	this	AccessibilityManagerService
    //   0	238	1	paramUserState	UserState
    //   33	196	2	i	int
    //   22	88	3	j	int
    //   97	75	4	k	int
    //   4	114	5	m	int
    //   1	197	6	n	int
    //   10	216	7	localObject1	Object
    //   7	187	8	localObject2	Object
    //   16	218	9	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   23	30	232	finally
    //   34	42	232	finally
    //   49	56	232	finally
    //   58	65	232	finally
    //   71	78	232	finally
    //   84	91	232	finally
    //   99	106	232	finally
    //   120	155	232	finally
    //   155	161	232	finally
    //   163	176	232	finally
    //   200	224	232	finally
  }
  
  private void updateLegacyCapabilitiesLocked(UserState paramUserState)
  {
    int j = paramUserState.mInstalledServices.size();
    int i = 0;
    while (i < j)
    {
      AccessibilityServiceInfo localAccessibilityServiceInfo = (AccessibilityServiceInfo)paramUserState.mInstalledServices.get(i);
      Object localObject = localAccessibilityServiceInfo.getResolveInfo();
      if (((localAccessibilityServiceInfo.getCapabilities() & 0x2) == 0) && (((ResolveInfo)localObject).serviceInfo.applicationInfo.targetSdkVersion <= 17))
      {
        localObject = new ComponentName(((ResolveInfo)localObject).serviceInfo.packageName, ((ResolveInfo)localObject).serviceInfo.name);
        if (paramUserState.mTouchExplorationGrantedServices.contains(localObject)) {
          localAccessibilityServiceInfo.setCapabilities(localAccessibilityServiceInfo.getCapabilities() | 0x2);
        }
      }
      i += 1;
    }
  }
  
  private void updateMagnificationLocked(UserState paramUserState)
  {
    if (paramUserState.mUserId != this.mCurrentUserId) {
      return;
    }
    if ((paramUserState.mIsDisplayMagnificationEnabled) || (userHasListeningMagnificationServicesLocked(paramUserState)))
    {
      getMagnificationController();
      this.mMagnificationController.register();
    }
    while (this.mMagnificationController == null) {
      return;
    }
    this.mMagnificationController.unregister();
  }
  
  private void updatePerformGesturesLocked(UserState paramUserState)
  {
    int j = paramUserState.mBoundServices.size();
    int i = 0;
    while (i < j)
    {
      if ((((Service)paramUserState.mBoundServices.get(i)).mAccessibilityServiceInfo.getCapabilities() & 0x20) != 0)
      {
        paramUserState.mIsPerformGesturesEnabled = true;
        return;
      }
      i += 1;
    }
    paramUserState.mIsPerformGesturesEnabled = false;
  }
  
  private void updateServicesLocked(UserState paramUserState)
  {
    Map localMap = paramUserState.mComponentNameToServiceMap;
    boolean bool = ((UserManager)this.mContext.getSystemService(UserManager.class)).isUserUnlockingOrUnlocked(paramUserState.mUserId);
    int i = 0;
    int j = paramUserState.mInstalledServices.size();
    if (i < j)
    {
      Object localObject = (AccessibilityServiceInfo)paramUserState.mInstalledServices.get(i);
      ComponentName localComponentName = ComponentName.unflattenFromString(((AccessibilityServiceInfo)localObject).getId());
      Service localService = (Service)localMap.get(localComponentName);
      if ((bool) || (((AccessibilityServiceInfo)localObject).isDirectBootAware())) {
        if (!paramUserState.mBindingServices.contains(localComponentName)) {
          break label148;
        }
      }
      for (;;)
      {
        i += 1;
        break;
        Slog.d("AccessibilityManagerService", "Ignoring non-encryption-aware service " + localComponentName);
        continue;
        label148:
        if (paramUserState.mEnabledServices.contains(localComponentName))
        {
          if (localService == null) {
            localObject = new Service(paramUserState.mUserId, localComponentName, (AccessibilityServiceInfo)localObject);
          }
          do
          {
            ((Service)localObject).bindLocked();
            break;
            localObject = localService;
          } while (!paramUserState.mBoundServices.contains(localService));
        }
        else if (localService != null)
        {
          localService.unbindLocked();
        }
      }
    }
    updateAccessibilityEnabledSetting(paramUserState);
  }
  
  private void updateSoftKeyboardShowModeLocked(UserState paramUserState)
  {
    long l;
    if ((paramUserState.mUserId == this.mCurrentUserId) && (paramUserState.mSoftKeyboardShowMode != 0) && (!paramUserState.mEnabledServices.contains(paramUserState.mServiceChangingSoftKeyboardMode))) {
      l = Binder.clearCallingIdentity();
    }
    try
    {
      Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "accessibility_soft_keyboard_mode", 0, paramUserState.mUserId);
      Binder.restoreCallingIdentity(l);
      paramUserState.mSoftKeyboardShowMode = 0;
      paramUserState.mServiceChangingSoftKeyboardMode = null;
      notifySoftKeyboardShowModeChangedLocked(paramUserState.mSoftKeyboardShowMode);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  /* Error */
  private void updateTouchExplorationLocked(UserState paramUserState)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore 5
    //   3: aload_1
    //   4: getfield 495	com/android/server/accessibility/AccessibilityManagerService$UserState:mBoundServices	Ljava/util/concurrent/CopyOnWriteArrayList;
    //   7: invokevirtual 685	java/util/concurrent/CopyOnWriteArrayList:size	()I
    //   10: istore_3
    //   11: iconst_0
    //   12: istore_2
    //   13: iload 5
    //   15: istore 4
    //   17: iload_2
    //   18: iload_3
    //   19: if_icmpge +24 -> 43
    //   22: aload_0
    //   23: aload_1
    //   24: getfield 495	com/android/server/accessibility/AccessibilityManagerService$UserState:mBoundServices	Ljava/util/concurrent/CopyOnWriteArrayList;
    //   27: iload_2
    //   28: invokevirtual 686	java/util/concurrent/CopyOnWriteArrayList:get	(I)Ljava/lang/Object;
    //   31: checkcast 29	com/android/server/accessibility/AccessibilityManagerService$Service
    //   34: invokespecial 1217	com/android/server/accessibility/AccessibilityManagerService:canRequestAndRequestsTouchExplorationLocked	(Lcom/android/server/accessibility/AccessibilityManagerService$Service;)Z
    //   37: ifeq +62 -> 99
    //   40: iconst_1
    //   41: istore 4
    //   43: iload 4
    //   45: aload_1
    //   46: getfield 939	com/android/server/accessibility/AccessibilityManagerService$UserState:mIsTouchExplorationEnabled	Z
    //   49: if_icmpeq +49 -> 98
    //   52: aload_1
    //   53: iload 4
    //   55: putfield 939	com/android/server/accessibility/AccessibilityManagerService$UserState:mIsTouchExplorationEnabled	Z
    //   58: invokestatic 783	android/os/Binder:clearCallingIdentity	()J
    //   61: lstore 6
    //   63: aload_0
    //   64: getfield 196	com/android/server/accessibility/AccessibilityManagerService:mContext	Landroid/content/Context;
    //   67: invokevirtual 485	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   70: astore 8
    //   72: iload 4
    //   74: ifeq +32 -> 106
    //   77: iconst_1
    //   78: istore_2
    //   79: aload 8
    //   81: ldc_w 936
    //   84: iload_2
    //   85: aload_1
    //   86: getfield 798	com/android/server/accessibility/AccessibilityManagerService$UserState:mUserId	I
    //   89: invokestatic 1114	android/provider/Settings$Secure:putIntForUser	(Landroid/content/ContentResolver;Ljava/lang/String;II)Z
    //   92: pop
    //   93: lload 6
    //   95: invokestatic 793	android/os/Binder:restoreCallingIdentity	(J)V
    //   98: return
    //   99: iload_2
    //   100: iconst_1
    //   101: iadd
    //   102: istore_2
    //   103: goto -90 -> 13
    //   106: iconst_0
    //   107: istore_2
    //   108: goto -29 -> 79
    //   111: astore_1
    //   112: lload 6
    //   114: invokestatic 793	android/os/Binder:restoreCallingIdentity	(J)V
    //   117: aload_1
    //   118: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	119	0	this	AccessibilityManagerService
    //   0	119	1	paramUserState	UserState
    //   12	96	2	i	int
    //   10	10	3	j	int
    //   15	58	4	bool1	boolean
    //   1	13	5	bool2	boolean
    //   61	52	6	l	long
    //   70	10	8	localContentResolver	ContentResolver
    // Exception table:
    //   from	to	target	type
    //   63	72	111	finally
    //   79	93	111	finally
  }
  
  private void updateWindowsForAccessibilityCallbackLocked(UserState paramUserState)
  {
    paramUserState = paramUserState.mBoundServices;
    int j = paramUserState.size();
    int i = 0;
    while (i < j)
    {
      if (((Service)paramUserState.get(i)).canRetrieveInteractiveWindowsLocked())
      {
        if (this.mWindowsForAccessibilityCallback == null)
        {
          this.mWindowsForAccessibilityCallback = new WindowsForAccessibilityCallback();
          this.mWindowManagerService.setWindowsForAccessibilityCallback(this.mWindowsForAccessibilityCallback);
        }
        return;
      }
      i += 1;
    }
    if (this.mWindowsForAccessibilityCallback != null)
    {
      this.mWindowsForAccessibilityCallback = null;
      this.mWindowManagerService.setWindowsForAccessibilityCallback(null);
      this.mSecurityPolicy.clearWindowsLocked();
    }
  }
  
  private boolean userHasListeningMagnificationServicesLocked(UserState paramUserState)
  {
    paramUserState = paramUserState.mBoundServices;
    int i = 0;
    int j = paramUserState.size();
    while (i < j)
    {
      Service localService = (Service)paramUserState.get(i);
      if ((this.mSecurityPolicy.canControlMagnification(localService)) && (AccessibilityManagerService.Service.InvocationHandler.-get0(localService.mInvocationHandler))) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private boolean userHasMagnificationServicesLocked(UserState paramUserState)
  {
    paramUserState = paramUserState.mBoundServices;
    int i = 0;
    int j = paramUserState.size();
    while (i < j)
    {
      Service localService = (Service)paramUserState.get(i);
      if (this.mSecurityPolicy.canControlMagnification(localService)) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  boolean accessibilityFocusOnlyInActiveWindow()
  {
    synchronized (this.mLock)
    {
      WindowsForAccessibilityCallback localWindowsForAccessibilityCallback = this.mWindowsForAccessibilityCallback;
      if (localWindowsForAccessibilityCallback == null)
      {
        bool = true;
        return bool;
      }
      boolean bool = false;
    }
  }
  
  public int addAccessibilityInteractionConnection(IWindow paramIWindow, IAccessibilityInteractionConnection paramIAccessibilityInteractionConnection, int paramInt)
    throws RemoteException
  {
    synchronized (this.mLock)
    {
      int i = this.mSecurityPolicy.resolveCallingUserIdEnforcingPermissionsLocked(paramInt);
      int j = sNextWindowId;
      sNextWindowId = j + 1;
      if (this.mSecurityPolicy.isCallerInteractingAcrossUsers(paramInt))
      {
        paramIAccessibilityInteractionConnection = new AccessibilityConnectionWrapper(j, paramIAccessibilityInteractionConnection, -1);
        paramIAccessibilityInteractionConnection.linkToDeath();
        this.mGlobalInteractionConnections.put(j, paramIAccessibilityInteractionConnection);
        this.mGlobalWindowTokens.put(j, paramIWindow.asBinder());
        return j;
      }
      paramIAccessibilityInteractionConnection = new AccessibilityConnectionWrapper(j, paramIAccessibilityInteractionConnection, i);
      paramIAccessibilityInteractionConnection.linkToDeath();
      UserState localUserState = getUserStateLocked(i);
      localUserState.mInteractionConnections.put(j, paramIAccessibilityInteractionConnection);
      localUserState.mWindowTokens.put(j, paramIWindow.asBinder());
    }
  }
  
  public int addClient(IAccessibilityManagerClient paramIAccessibilityManagerClient, int paramInt)
  {
    synchronized (this.mLock)
    {
      int i = this.mSecurityPolicy.resolveCallingUserIdEnforcingPermissionsLocked(paramInt);
      UserState localUserState = getUserStateLocked(i);
      if (this.mSecurityPolicy.isCallerInteractingAcrossUsers(paramInt))
      {
        this.mGlobalClients.register(paramIAccessibilityManagerClient);
        paramInt = localUserState.getClientState();
        return paramInt;
      }
      localUserState.mClients.register(paramIAccessibilityManagerClient);
      if (i == this.mCurrentUserId)
      {
        paramInt = localUserState.getClientState();
        return paramInt;
      }
      paramInt = 0;
    }
  }
  
  public void disableAccessibilityService(ComponentName paramComponentName, int paramInt)
  {
    synchronized (this.mLock)
    {
      if (Binder.getCallingUid() != 1000) {
        throw new SecurityException("only SYSTEM can call disableAccessibility");
      }
    }
    Object localObject2 = new SettingsStringHelper("enabled_accessibility_services", paramInt);
    ((SettingsStringHelper)localObject2).deleteService(paramComponentName);
    ((SettingsStringHelper)localObject2).writeToSettings();
    localObject2 = getUserStateLocked(paramInt);
    if (((UserState)localObject2).mEnabledServices.remove(paramComponentName)) {
      onUserStateChangedLocked((UserState)localObject2);
    }
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    SecurityPolicy.-wrap3(this.mSecurityPolicy, "android.permission.DUMP", "dump");
    for (;;)
    {
      synchronized (this.mLock)
      {
        paramPrintWriter.println("ACCESSIBILITY MANAGER (dumpsys accessibility)");
        paramPrintWriter.println();
        int k = this.mUserStates.size();
        int i = 0;
        int j;
        if (i < k)
        {
          UserState localUserState = (UserState)this.mUserStates.valueAt(i);
          paramPrintWriter.append("User state[attributes:{id=" + localUserState.mUserId);
          StringBuilder localStringBuilder = new StringBuilder().append(", currentUser=");
          if (localUserState.mUserId == this.mCurrentUserId)
          {
            bool = true;
            paramPrintWriter.append(bool);
            paramPrintWriter.append(", touchExplorationEnabled=" + localUserState.mIsTouchExplorationEnabled);
            paramPrintWriter.append(", displayMagnificationEnabled=" + localUserState.mIsDisplayMagnificationEnabled);
            paramPrintWriter.append(", autoclickEnabled=" + localUserState.mIsAutoclickEnabled);
            if (UserState.-get1(localUserState) != null)
            {
              paramPrintWriter.append(", ");
              UserState.-get1(localUserState).dump(paramFileDescriptor, paramPrintWriter, paramArrayOfString);
              paramPrintWriter.println();
            }
            paramPrintWriter.append("}");
            paramPrintWriter.println();
            paramPrintWriter.append("           services:{");
            int m = localUserState.mBoundServices.size();
            j = 0;
            if (j < m)
            {
              if (j > 0)
              {
                paramPrintWriter.append(", ");
                paramPrintWriter.println();
                paramPrintWriter.append("                     ");
              }
              ((Service)localUserState.mBoundServices.get(j)).dump(paramFileDescriptor, paramPrintWriter, paramArrayOfString);
              j += 1;
              continue;
            }
            paramPrintWriter.println("}]");
            paramPrintWriter.println();
            i += 1;
          }
        }
        else
        {
          if (this.mSecurityPolicy.mWindows != null)
          {
            j = this.mSecurityPolicy.mWindows.size();
            i = 0;
            if (i < j)
            {
              if (i > 0)
              {
                paramPrintWriter.append(',');
                paramPrintWriter.println();
              }
              paramPrintWriter.append("Window[");
              paramPrintWriter.append(((AccessibilityWindowInfo)this.mSecurityPolicy.mWindows.get(i)).toString());
              paramPrintWriter.append(']');
              i += 1;
              continue;
            }
          }
          return;
        }
      }
      boolean bool = false;
    }
  }
  
  public void enableAccessibilityService(ComponentName paramComponentName, int paramInt)
  {
    synchronized (this.mLock)
    {
      if (Binder.getCallingUid() != 1000) {
        throw new SecurityException("only SYSTEM can call enableAccessibilityService.");
      }
    }
    Object localObject2 = new SettingsStringHelper("enabled_accessibility_services", paramInt);
    ((SettingsStringHelper)localObject2).addService(paramComponentName);
    ((SettingsStringHelper)localObject2).writeToSettings();
    localObject2 = getUserStateLocked(paramInt);
    if (((UserState)localObject2).mEnabledServices.add(paramComponentName)) {
      onUserStateChangedLocked((UserState)localObject2);
    }
  }
  
  boolean getAccessibilityFocusClickPointInScreen(Point paramPoint)
  {
    return getInteractionBridgeLocked().getAccessibilityFocusClickPointInScreenNotLocked(paramPoint);
  }
  
  int getActiveWindowId()
  {
    return this.mSecurityPolicy.getActiveWindowId();
  }
  
  public List<AccessibilityServiceInfo> getEnabledAccessibilityServiceList(int paramInt1, int paramInt2)
  {
    synchronized (this.mLock)
    {
      Object localObject3 = getUserStateLocked(this.mSecurityPolicy.resolveCallingUserIdEnforcingPermissionsLocked(paramInt2));
      if (((UserState)localObject3).isUiAutomationSuppressingOtherServices())
      {
        localList = Collections.emptyList();
        return localList;
      }
      List localList = this.mEnabledServicesForFeedbackTempList;
      localList.clear();
      localObject3 = ((UserState)localObject3).mBoundServices;
      if (paramInt1 != 0)
      {
        int j = 1 << Integer.numberOfTrailingZeros(paramInt1);
        int i = paramInt1 & j;
        int k = ((List)localObject3).size();
        paramInt2 = 0;
        for (;;)
        {
          paramInt1 = i;
          if (paramInt2 >= k) {
            break;
          }
          Service localService = (Service)((List)localObject3).get(paramInt2);
          if ((!sFakeAccessibilityServiceComponentName.equals(localService.mComponentName)) && ((localService.mFeedbackType & j) != 0)) {
            localList.add(localService.mAccessibilityServiceInfo);
          }
          paramInt2 += 1;
        }
      }
      return localList;
    }
  }
  
  public List<AccessibilityServiceInfo> getInstalledAccessibilityServiceList(int paramInt)
  {
    synchronized (this.mLock)
    {
      Object localObject2 = getUserStateLocked(this.mSecurityPolicy.resolveCallingUserIdEnforcingPermissionsLocked(paramInt));
      if (UserState.-get1((UserState)localObject2) != null)
      {
        ArrayList localArrayList = new ArrayList();
        localArrayList.addAll(((UserState)localObject2).mInstalledServices);
        localArrayList.remove(UserState.-get1((UserState)localObject2).mAccessibilityServiceInfo);
        return localArrayList;
      }
      localObject2 = ((UserState)localObject2).mInstalledServices;
      return (List<AccessibilityServiceInfo>)localObject2;
    }
  }
  
  MagnificationController getMagnificationController()
  {
    synchronized (this.mLock)
    {
      if (this.mMagnificationController == null)
      {
        this.mMagnificationController = new MagnificationController(this.mContext, this, this.mLock);
        this.mMagnificationController.setUserId(this.mCurrentUserId);
      }
      MagnificationController localMagnificationController = this.mMagnificationController;
      return localMagnificationController;
    }
  }
  
  boolean getWindowBounds(int paramInt, Rect paramRect)
  {
    synchronized (this.mLock)
    {
      IBinder localIBinder2 = (IBinder)this.mGlobalWindowTokens.get(paramInt);
      IBinder localIBinder1 = localIBinder2;
      if (localIBinder2 == null) {
        localIBinder1 = (IBinder)getCurrentUserStateLocked().mWindowTokens.get(paramInt);
      }
      this.mWindowManagerService.getWindowFrame(localIBinder1, paramRect);
      if (!paramRect.isEmpty()) {
        return true;
      }
    }
    return false;
  }
  
  public IBinder getWindowToken(int paramInt1, int paramInt2)
  {
    SecurityPolicy.-wrap3(this.mSecurityPolicy, "android.permission.RETRIEVE_WINDOW_TOKEN", "getWindowToken");
    synchronized (this.mLock)
    {
      paramInt2 = this.mSecurityPolicy.resolveCallingUserIdEnforcingPermissionsLocked(paramInt2);
      int i = this.mCurrentUserId;
      if (paramInt2 != i) {
        return null;
      }
      Object localObject2 = SecurityPolicy.-wrap0(this.mSecurityPolicy, paramInt1);
      if (localObject2 == null) {
        return null;
      }
      localObject2 = (IBinder)this.mGlobalWindowTokens.get(paramInt1);
      if (localObject2 != null) {
        return (IBinder)localObject2;
      }
      localObject2 = (IBinder)getCurrentUserStateLocked().mWindowTokens.get(paramInt1);
      return (IBinder)localObject2;
    }
  }
  
  public void interrupt(int paramInt)
  {
    for (;;)
    {
      synchronized (this.mLock)
      {
        paramInt = this.mSecurityPolicy.resolveCallingUserIdEnforcingPermissionsLocked(paramInt);
        int i = this.mCurrentUserId;
        if (paramInt != i) {
          return;
        }
        CopyOnWriteArrayList localCopyOnWriteArrayList = getUserStateLocked(paramInt).mBoundServices;
        paramInt = 0;
        i = localCopyOnWriteArrayList.size();
        if (paramInt >= i) {
          return;
        }
        ??? = (Service)localCopyOnWriteArrayList.get(paramInt);
      }
      try
      {
        ((Service)???).mServiceInterface.onInterrupt();
        paramInt += 1;
        continue;
        localObject1 = finally;
        throw ((Throwable)localObject1);
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Slog.e("AccessibilityManagerService", "Error during sending interrupt request to " + ((Service)???).mService, localRemoteException);
        }
      }
    }
  }
  
  boolean notifyKeyEvent(KeyEvent paramKeyEvent, int paramInt)
  {
    synchronized (this.mLock)
    {
      CopyOnWriteArrayList localCopyOnWriteArrayList = getCurrentUserStateLocked().mBoundServices;
      boolean bool = localCopyOnWriteArrayList.isEmpty();
      if (bool) {
        return false;
      }
      bool = getKeyEventDispatcher().notifyKeyEventLocked(paramKeyEvent, paramInt, localCopyOnWriteArrayList);
      return bool;
    }
  }
  
  void notifyMagnificationChanged(Region paramRegion, float paramFloat1, float paramFloat2, float paramFloat3)
  {
    synchronized (this.mLock)
    {
      notifyMagnificationChangedLocked(paramRegion, paramFloat1, paramFloat2, paramFloat3);
      return;
    }
  }
  
  boolean onGesture(int paramInt)
  {
    synchronized (this.mLock)
    {
      boolean bool2 = notifyGestureLocked(paramInt, false);
      boolean bool1 = bool2;
      if (!bool2) {
        bool1 = notifyGestureLocked(paramInt, true);
      }
      return bool1;
    }
  }
  
  void onMagnificationStateChanged()
  {
    notifyClearAccessibilityCacheLocked();
  }
  
  void onTouchInteractionEnd()
  {
    this.mSecurityPolicy.onTouchInteractionEnd();
  }
  
  void onTouchInteractionStart()
  {
    this.mSecurityPolicy.onTouchInteractionStart();
  }
  
  public void registerUiTestAutomationService(IBinder paramIBinder, IAccessibilityServiceClient paramIAccessibilityServiceClient, AccessibilityServiceInfo paramAccessibilityServiceInfo, int paramInt)
  {
    SecurityPolicy.-wrap3(this.mSecurityPolicy, "android.permission.RETRIEVE_WINDOW_CONTENT", "registerUiTestAutomationService");
    paramAccessibilityServiceInfo.setComponentName(sFakeAccessibilityServiceComponentName);
    UserState localUserState;
    synchronized (this.mLock)
    {
      localUserState = getCurrentUserStateLocked();
      if (UserState.-get1(localUserState) != null) {
        throw new IllegalStateException("UiAutomationService " + paramIAccessibilityServiceClient + "already registered!");
      }
    }
    try
    {
      paramIBinder.linkToDeath(UserState.-get0(localUserState), 0);
      UserState.-set3(localUserState, paramIBinder);
      UserState.-set2(localUserState, paramIAccessibilityServiceClient);
      UserState.-set0(localUserState, paramInt);
      localUserState.mInstalledServices.add(paramAccessibilityServiceInfo);
      if ((paramInt & 0x1) == 0)
      {
        localUserState.mIsTouchExplorationEnabled = false;
        localUserState.mIsEnhancedWebAccessibilityEnabled = false;
        localUserState.mIsDisplayMagnificationEnabled = false;
        localUserState.mIsAutoclickEnabled = false;
        localUserState.mEnabledServices.clear();
      }
      localUserState.mEnabledServices.add(sFakeAccessibilityServiceComponentName);
      localUserState.mTouchExplorationGrantedServices.add(sFakeAccessibilityServiceComponentName);
      onUserStateChangedLocked(localUserState);
      return;
    }
    catch (RemoteException paramIBinder)
    {
      Slog.e("AccessibilityManagerService", "Couldn't register for the death of a UiTestAutomationService!", paramIBinder);
    }
  }
  
  public void removeAccessibilityInteractionConnection(IWindow paramIWindow)
  {
    synchronized (this.mLock)
    {
      this.mSecurityPolicy.resolveCallingUserIdEnforcingPermissionsLocked(UserHandle.getCallingUserId());
      paramIWindow = paramIWindow.asBinder();
      int i = removeAccessibilityInteractionConnectionInternalLocked(paramIWindow, this.mGlobalWindowTokens, this.mGlobalInteractionConnections);
      if (i >= 0) {
        return;
      }
      int j = this.mUserStates.size();
      i = 0;
      while (i < j)
      {
        UserState localUserState = (UserState)this.mUserStates.valueAt(i);
        int k = removeAccessibilityInteractionConnectionInternalLocked(paramIWindow, localUserState.mWindowTokens, localUserState.mInteractionConnections);
        if (k >= 0) {
          return;
        }
        i += 1;
      }
      return;
    }
  }
  
  void restoreEnabledAccessibilityServicesLocked(String paramString1, String paramString2)
  {
    readComponentNamesFromStringLocked(paramString1, this.mTempComponentNameSet, false);
    readComponentNamesFromStringLocked(paramString2, this.mTempComponentNameSet, true);
    paramString1 = getUserStateLocked(0);
    paramString1.mEnabledServices.clear();
    paramString1.mEnabledServices.addAll(this.mTempComponentNameSet);
    persistComponentNamesToSettingLocked("enabled_accessibility_services", paramString1.mEnabledServices, 0);
    onUserStateChangedLocked(paramString1);
  }
  
  public boolean sendAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent, int paramInt)
  {
    synchronized (this.mLock)
    {
      paramInt = this.mSecurityPolicy.resolveCallingUserIdEnforcingPermissionsLocked(paramInt);
      int i = this.mCurrentUserId;
      if (paramInt != i) {
        return true;
      }
      if (SecurityPolicy.-wrap1(this.mSecurityPolicy, paramAccessibilityEvent))
      {
        this.mSecurityPolicy.updateActiveAndAccessibilityFocusedWindowLocked(paramAccessibilityEvent.getWindowId(), paramAccessibilityEvent.getSourceNodeId(), paramAccessibilityEvent.getEventType(), paramAccessibilityEvent.getAction());
        this.mSecurityPolicy.updateEventSourceLocked(paramAccessibilityEvent);
        notifyAccessibilityServicesDelayedLocked(paramAccessibilityEvent, false);
        notifyAccessibilityServicesDelayedLocked(paramAccessibilityEvent, true);
      }
      if ((this.mHasInputFilter) && (this.mInputFilter != null)) {
        this.mMainHandler.obtainMessage(1, AccessibilityEvent.obtain(paramAccessibilityEvent)).sendToTarget();
      }
      paramAccessibilityEvent.recycle();
      if (OWN_PROCESS_ID != Binder.getCallingPid()) {
        return true;
      }
    }
    return false;
  }
  
  void setMotionEventInjector(MotionEventInjector paramMotionEventInjector)
  {
    synchronized (this.mLock)
    {
      this.mMotionEventInjector = paramMotionEventInjector;
      this.mLock.notifyAll();
      return;
    }
  }
  
  public void temporaryEnableAccessibilityStateUntilKeyguardRemoved(ComponentName paramComponentName, boolean paramBoolean)
  {
    SecurityPolicy.-wrap3(this.mSecurityPolicy, "android.permission.TEMPORARY_ENABLE_ACCESSIBILITY", "temporaryEnableAccessibilityStateUntilKeyguardRemoved");
    if (!this.mWindowManagerService.isKeyguardLocked()) {
      return;
    }
    synchronized (this.mLock)
    {
      UserState localUserState = getCurrentUserStateLocked();
      boolean bool = localUserState.isUiAutomationSuppressingOtherServices();
      if (bool) {
        return;
      }
      localUserState.mIsTouchExplorationEnabled = paramBoolean;
      localUserState.mIsEnhancedWebAccessibilityEnabled = false;
      localUserState.mIsDisplayMagnificationEnabled = false;
      localUserState.mIsAutoclickEnabled = false;
      localUserState.mEnabledServices.clear();
      localUserState.mEnabledServices.add(paramComponentName);
      localUserState.mBindingServices.clear();
      localUserState.mTouchExplorationGrantedServices.clear();
      localUserState.mTouchExplorationGrantedServices.add(paramComponentName);
      onUserStateChangedLocked(localUserState);
      return;
    }
  }
  
  public void unregisterUiTestAutomationService(IAccessibilityServiceClient paramIAccessibilityServiceClient)
  {
    synchronized (this.mLock)
    {
      UserState localUserState = getCurrentUserStateLocked();
      if ((UserState.-get1(localUserState) != null) && (paramIAccessibilityServiceClient != null) && (UserState.-get1(localUserState).mServiceInterface != null) && (UserState.-get1(localUserState).mServiceInterface.asBinder() == paramIAccessibilityServiceClient.asBinder()))
      {
        UserState.-get1(localUserState).binderDied();
        return;
      }
      throw new IllegalStateException("UiAutomationService " + paramIAccessibilityServiceClient + " not registered!");
    }
  }
  
  private class AccessibilityConnectionWrapper
    implements IBinder.DeathRecipient
  {
    private final IAccessibilityInteractionConnection mConnection;
    private final int mUserId;
    private final int mWindowId;
    
    public AccessibilityConnectionWrapper(int paramInt1, IAccessibilityInteractionConnection paramIAccessibilityInteractionConnection, int paramInt2)
    {
      this.mWindowId = paramInt1;
      this.mUserId = paramInt2;
      this.mConnection = paramIAccessibilityInteractionConnection;
    }
    
    public void binderDied()
    {
      unlinkToDeath();
      synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
      {
        AccessibilityManagerService.-wrap20(AccessibilityManagerService.this, this.mWindowId, this.mUserId);
        return;
      }
    }
    
    public void linkToDeath()
      throws RemoteException
    {
      this.mConnection.asBinder().linkToDeath(this, 0);
    }
    
    public void unlinkToDeath()
    {
      this.mConnection.asBinder().unlinkToDeath(this, 0);
    }
  }
  
  private final class AccessibilityContentObserver
    extends ContentObserver
  {
    private final Uri mAccessibilitySoftKeyboardModeUri = Settings.Secure.getUriFor("accessibility_soft_keyboard_mode");
    private final Uri mAutoclickEnabledUri = Settings.Secure.getUriFor("accessibility_autoclick_enabled");
    private final Uri mDisplayDaltonizerEnabledUri = Settings.Secure.getUriFor("accessibility_display_daltonizer_enabled");
    private final Uri mDisplayDaltonizerUri = Settings.Secure.getUriFor("accessibility_display_daltonizer");
    private final Uri mDisplayInversionEnabledUri = Settings.Secure.getUriFor("accessibility_display_inversion_enabled");
    private final Uri mDisplayMagnificationEnabledUri = Settings.Secure.getUriFor("accessibility_display_magnification_enabled");
    private final Uri mEnabledAccessibilityServicesUri = Settings.Secure.getUriFor("enabled_accessibility_services");
    private final Uri mEnhancedWebAccessibilityUri = Settings.Secure.getUriFor("accessibility_script_injection");
    private final Uri mHighTextContrastUri = Settings.Secure.getUriFor("high_text_contrast_enabled");
    private final Uri mTouchExplorationEnabledUri = Settings.Secure.getUriFor("touch_exploration_enabled");
    private final Uri mTouchExplorationGrantedAccessibilityServicesUri = Settings.Secure.getUriFor("touch_exploration_granted_accessibility_services");
    
    public AccessibilityContentObserver(Handler paramHandler)
    {
      super();
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      for (;;)
      {
        AccessibilityManagerService.UserState localUserState;
        synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
        {
          localUserState = AccessibilityManagerService.-wrap11(AccessibilityManagerService.this);
          paramBoolean = localUserState.isUiAutomationSuppressingOtherServices();
          if (paramBoolean) {
            return;
          }
          if (this.mTouchExplorationEnabledUri.equals(paramUri))
          {
            if (AccessibilityManagerService.-wrap8(AccessibilityManagerService.this, localUserState)) {
              AccessibilityManagerService.-wrap18(AccessibilityManagerService.this, localUserState);
            }
            return;
          }
          if (this.mDisplayMagnificationEnabledUri.equals(paramUri))
          {
            if (!AccessibilityManagerService.-wrap3(AccessibilityManagerService.this, localUserState)) {
              continue;
            }
            AccessibilityManagerService.-wrap18(AccessibilityManagerService.this, localUserState);
          }
        }
        if (this.mAutoclickEnabledUri.equals(paramUri))
        {
          if (AccessibilityManagerService.-wrap1(AccessibilityManagerService.this, localUserState)) {
            AccessibilityManagerService.-wrap18(AccessibilityManagerService.this, localUserState);
          }
        }
        else if (this.mEnabledAccessibilityServicesUri.equals(paramUri))
        {
          if (AccessibilityManagerService.-wrap4(AccessibilityManagerService.this, localUserState)) {
            AccessibilityManagerService.-wrap18(AccessibilityManagerService.this, localUserState);
          }
        }
        else if (this.mTouchExplorationGrantedAccessibilityServicesUri.equals(paramUri))
        {
          if (AccessibilityManagerService.-wrap9(AccessibilityManagerService.this, localUserState)) {
            AccessibilityManagerService.-wrap18(AccessibilityManagerService.this, localUserState);
          }
        }
        else if (this.mEnhancedWebAccessibilityUri.equals(paramUri))
        {
          if (AccessibilityManagerService.-wrap5(AccessibilityManagerService.this, localUserState)) {
            AccessibilityManagerService.-wrap18(AccessibilityManagerService.this, localUserState);
          }
        }
        else if ((this.mDisplayDaltonizerEnabledUri.equals(paramUri)) || (this.mDisplayDaltonizerUri.equals(paramUri)))
        {
          AccessibilityManagerService.-wrap27(AccessibilityManagerService.this, localUserState);
        }
        else if (this.mDisplayInversionEnabledUri.equals(paramUri))
        {
          AccessibilityManagerService.-wrap28(AccessibilityManagerService.this, localUserState);
        }
        else if (this.mHighTextContrastUri.equals(paramUri))
        {
          if (AccessibilityManagerService.-wrap6(AccessibilityManagerService.this, localUserState)) {
            AccessibilityManagerService.-wrap18(AccessibilityManagerService.this, localUserState);
          }
        }
        else if ((this.mAccessibilitySoftKeyboardModeUri.equals(paramUri)) && (AccessibilityManagerService.-wrap7(AccessibilityManagerService.this, localUserState)))
        {
          AccessibilityManagerService.-wrap17(AccessibilityManagerService.this, localUserState.mSoftKeyboardShowMode);
          AccessibilityManagerService.-wrap18(AccessibilityManagerService.this, localUserState);
        }
      }
    }
    
    public void register(ContentResolver paramContentResolver)
    {
      paramContentResolver.registerContentObserver(this.mTouchExplorationEnabledUri, false, this, -1);
      paramContentResolver.registerContentObserver(this.mDisplayMagnificationEnabledUri, false, this, -1);
      paramContentResolver.registerContentObserver(this.mAutoclickEnabledUri, false, this, -1);
      paramContentResolver.registerContentObserver(this.mEnabledAccessibilityServicesUri, false, this, -1);
      paramContentResolver.registerContentObserver(this.mTouchExplorationGrantedAccessibilityServicesUri, false, this, -1);
      paramContentResolver.registerContentObserver(this.mEnhancedWebAccessibilityUri, false, this, -1);
      paramContentResolver.registerContentObserver(this.mDisplayInversionEnabledUri, false, this, -1);
      paramContentResolver.registerContentObserver(this.mDisplayDaltonizerEnabledUri, false, this, -1);
      paramContentResolver.registerContentObserver(this.mDisplayDaltonizerUri, false, this, -1);
      paramContentResolver.registerContentObserver(this.mHighTextContrastUri, false, this, -1);
      paramContentResolver.registerContentObserver(this.mAccessibilitySoftKeyboardModeUri, false, this, -1);
    }
  }
  
  private final class InteractionBridge
  {
    private final AccessibilityInteractionClient mClient;
    private final int mConnectionId;
    private final Display mDefaultDisplay;
    
    public InteractionBridge()
    {
      Object localObject = new AccessibilityServiceInfo();
      ((AccessibilityServiceInfo)localObject).setCapabilities(1);
      ((AccessibilityServiceInfo)localObject).flags |= 0x40;
      ((AccessibilityServiceInfo)localObject).flags |= 0x2;
      localObject = new AccessibilityManagerService.Service(AccessibilityManagerService.this, 55536, AccessibilityManagerService.-get19(), (AccessibilityServiceInfo)localObject);
      this.mConnectionId = ((AccessibilityManagerService.Service)localObject).mId;
      this.mClient = AccessibilityInteractionClient.getInstance();
      this.mClient.addConnection(this.mConnectionId, (IAccessibilityServiceConnection)localObject);
      this.mDefaultDisplay = ((DisplayManager)AccessibilityManagerService.-get2(AccessibilityManagerService.this).getSystemService("display")).getDisplay(0);
    }
    
    private AccessibilityNodeInfo getAccessibilityFocusNotLocked()
    {
      synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
      {
        int i = AccessibilityManagerService.-get12(AccessibilityManagerService.this).mAccessibilityFocusedWindowId;
        if (i == -1) {
          return null;
        }
        return getAccessibilityFocusNotLocked(i);
      }
    }
    
    private AccessibilityNodeInfo getAccessibilityFocusNotLocked(int paramInt)
    {
      return this.mClient.findFocus(this.mConnectionId, paramInt, AccessibilityNodeInfo.ROOT_NODE_ID, 2);
    }
    
    public void clearAccessibilityFocusNotLocked(int paramInt)
    {
      AccessibilityNodeInfo localAccessibilityNodeInfo = getAccessibilityFocusNotLocked(paramInt);
      if (localAccessibilityNodeInfo != null) {
        localAccessibilityNodeInfo.performAction(128);
      }
    }
    
    public boolean getAccessibilityFocusClickPointInScreenNotLocked(Point paramPoint)
    {
      Object localObject2 = getAccessibilityFocusNotLocked();
      if (localObject2 == null) {
        return false;
      }
      Rect localRect;
      synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
      {
        localRect = AccessibilityManagerService.-get14(AccessibilityManagerService.this);
        ((AccessibilityNodeInfo)localObject2).getBoundsInScreen(localRect);
        Object localObject3 = AccessibilityManagerService.-wrap0(AccessibilityManagerService.this, ((AccessibilityNodeInfo)localObject2).getWindowId());
        if ((localObject3 == null) || (((MagnificationSpec)localObject3).isNop()))
        {
          localObject3 = AccessibilityManagerService.-get15(AccessibilityManagerService.this);
          AccessibilityManagerService.this.getWindowBounds(((AccessibilityNodeInfo)localObject2).getWindowId(), (Rect)localObject3);
          bool = localRect.intersect((Rect)localObject3);
          if (!bool) {
            return false;
          }
        }
        else
        {
          localRect.offset((int)-((MagnificationSpec)localObject3).offsetX, (int)-((MagnificationSpec)localObject3).offsetY);
          localRect.scale(1.0F / ((MagnificationSpec)localObject3).scale);
        }
      }
      localObject2 = AccessibilityManagerService.-get13(AccessibilityManagerService.this);
      this.mDefaultDisplay.getRealSize((Point)localObject2);
      boolean bool = localRect.intersect(0, 0, ((Point)localObject2).x, ((Point)localObject2).y);
      if (!bool) {
        return false;
      }
      paramPoint.set(localRect.centerX(), localRect.centerY());
      return true;
    }
  }
  
  private final class MainHandler
    extends Handler
  {
    public static final int MSG_ANNOUNCE_NEW_USER_IF_NEEDED = 5;
    public static final int MSG_CLEAR_ACCESSIBILITY_FOCUS = 9;
    public static final int MSG_SEND_ACCESSIBILITY_EVENT_TO_INPUT_FILTER = 1;
    public static final int MSG_SEND_CLEARED_STATE_TO_CLIENTS_FOR_USER = 3;
    public static final int MSG_SEND_KEY_EVENT_TO_INPUT_FILTER = 8;
    public static final int MSG_SEND_STATE_TO_CLIENTS = 2;
    public static final int MSG_SHOW_ENABLED_TOUCH_EXPLORATION_DIALOG = 7;
    public static final int MSG_UPDATE_INPUT_FILTER = 6;
    
    public MainHandler(Looper paramLooper)
    {
      super();
    }
    
    private void announceNewUserIfNeeded()
    {
      synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
      {
        if (AccessibilityManagerService.-wrap11(AccessibilityManagerService.this).isHandlingAccessibilityEvents())
        {
          Object localObject2 = (UserManager)AccessibilityManagerService.-get2(AccessibilityManagerService.this).getSystemService("user");
          localObject2 = AccessibilityManagerService.-get2(AccessibilityManagerService.this).getString(17040710, new Object[] { ((UserManager)localObject2).getUserInfo(AccessibilityManagerService.-get3(AccessibilityManagerService.this)).name });
          AccessibilityEvent localAccessibilityEvent = AccessibilityEvent.obtain(16384);
          localAccessibilityEvent.getText().add(localObject2);
          AccessibilityManagerService.this.sendAccessibilityEvent(localAccessibilityEvent, AccessibilityManagerService.-get3(AccessibilityManagerService.this));
        }
        return;
      }
    }
    
    private void sendStateToClients(int paramInt, RemoteCallbackList<IAccessibilityManagerClient> paramRemoteCallbackList)
    {
      try
      {
        int j = paramRemoteCallbackList.beginBroadcast();
        int i = 0;
        for (;;)
        {
          if (i < j)
          {
            IAccessibilityManagerClient localIAccessibilityManagerClient = (IAccessibilityManagerClient)paramRemoteCallbackList.getBroadcastItem(i);
            try
            {
              localIAccessibilityManagerClient.setState(paramInt);
              i += 1;
            }
            catch (RemoteException localRemoteException)
            {
              for (;;) {}
            }
          }
        }
        return;
      }
      finally
      {
        paramRemoteCallbackList.finishBroadcast();
      }
    }
    
    private void sendStateToClientsForUser(int paramInt1, int paramInt2)
    {
      synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
      {
        AccessibilityManagerService.UserState localUserState = AccessibilityManagerService.-wrap12(AccessibilityManagerService.this, paramInt2);
        sendStateToClients(paramInt1, localUserState.mClients);
        return;
      }
    }
    
    public void handleMessage(Message arg1)
    {
      switch (???.what)
      {
      case 4: 
      default: 
        return;
      case 1: 
        AccessibilityEvent localAccessibilityEvent = (AccessibilityEvent)???.obj;
        synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
        {
          if ((AccessibilityManagerService.-get6(AccessibilityManagerService.this)) && (AccessibilityManagerService.-get7(AccessibilityManagerService.this) != null)) {
            AccessibilityManagerService.-get7(AccessibilityManagerService.this).notifyAccessibilityEvent(localAccessibilityEvent);
          }
          localAccessibilityEvent.recycle();
          return;
        }
      case 8: 
        KeyEvent localKeyEvent = (KeyEvent)???.obj;
        i = ???.arg1;
        synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
        {
          if ((AccessibilityManagerService.-get6(AccessibilityManagerService.this)) && (AccessibilityManagerService.-get7(AccessibilityManagerService.this) != null)) {
            AccessibilityManagerService.-get7(AccessibilityManagerService.this).sendInputEvent(localKeyEvent, i);
          }
          localKeyEvent.recycle();
          return;
        }
      case 2: 
        i = ???.arg1;
        int j = ???.arg2;
        sendStateToClients(i, AccessibilityManagerService.-get4(AccessibilityManagerService.this));
        sendStateToClientsForUser(i, j);
        return;
      case 3: 
        sendStateToClientsForUser(0, ???.arg1);
        return;
      case 5: 
        announceNewUserIfNeeded();
        return;
      case 6: 
        ??? = (AccessibilityManagerService.UserState)???.obj;
        AccessibilityManagerService.-wrap29(AccessibilityManagerService.this, ???);
        return;
      case 7: 
        ??? = (AccessibilityManagerService.Service)???.obj;
        AccessibilityManagerService.-wrap23(AccessibilityManagerService.this, ???);
        return;
      }
      int i = ???.arg1;
      synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
      {
        AccessibilityManagerService.InteractionBridge localInteractionBridge = AccessibilityManagerService.-wrap10(AccessibilityManagerService.this);
        localInteractionBridge.clearAccessibilityFocusNotLocked(i);
        return;
      }
    }
  }
  
  final class SecurityPolicy
  {
    public static final int INVALID_WINDOW_ID = -1;
    private static final int RETRIEVAL_ALLOWING_EVENT_TYPES = 244159;
    public long mAccessibilityFocusNodeId = 2147483647L;
    public int mAccessibilityFocusedWindowId = -1;
    public int mActiveWindowId = -1;
    public int mFocusedWindowId = -1;
    private boolean mTouchInteractionInProgress;
    public List<AccessibilityWindowInfo> mWindows;
    
    SecurityPolicy() {}
    
    private boolean canDispatchAccessibilityEventLocked(AccessibilityEvent paramAccessibilityEvent)
    {
      switch (paramAccessibilityEvent.getEventType())
      {
      default: 
        return isRetrievalAllowingWindow(paramAccessibilityEvent.getWindowId());
      }
      return true;
    }
    
    private void enforceCallingPermission(String paramString1, String paramString2)
    {
      if (AccessibilityManagerService.-get0() == Binder.getCallingPid()) {
        return;
      }
      if (!hasPermission(paramString1)) {
        throw new SecurityException("You do not have " + paramString1 + " required to call " + paramString2 + " from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
      }
    }
    
    private AccessibilityWindowInfo findWindowById(int paramInt)
    {
      if (this.mWindows != null)
      {
        int j = this.mWindows.size();
        int i = 0;
        while (i < j)
        {
          AccessibilityWindowInfo localAccessibilityWindowInfo = (AccessibilityWindowInfo)this.mWindows.get(i);
          if (localAccessibilityWindowInfo.getId() == paramInt) {
            return localAccessibilityWindowInfo;
          }
          i += 1;
        }
      }
      return null;
    }
    
    private int getFocusedWindowId()
    {
      IBinder localIBinder = AccessibilityManagerService.-get17(AccessibilityManagerService.this).getFocusedWindowToken();
      synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
      {
        int i = AccessibilityManagerService.-wrap14(AccessibilityManagerService.this, localIBinder);
        return i;
      }
    }
    
    private boolean hasPermission(String paramString)
    {
      boolean bool = false;
      if (AccessibilityManagerService.-get2(AccessibilityManagerService.this).checkCallingPermission(paramString) == 0) {
        bool = true;
      }
      return bool;
    }
    
    private boolean isRetrievalAllowingWindow(int paramInt)
    {
      if (Binder.getCallingUid() == 1000) {
        return true;
      }
      if (paramInt == this.mActiveWindowId) {
        return true;
      }
      return findWindowById(paramInt) != null;
    }
    
    private void notifyWindowsChanged()
    {
      if (AccessibilityManagerService.-get18(AccessibilityManagerService.this) == null) {
        return;
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        AccessibilityEvent localAccessibilityEvent = AccessibilityEvent.obtain(4194304);
        localAccessibilityEvent.setEventTime(SystemClock.uptimeMillis());
        AccessibilityManagerService.this.sendAccessibilityEvent(localAccessibilityEvent, AccessibilityManagerService.-get3(AccessibilityManagerService.this));
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    private int resolveProfileParentLocked(int paramInt)
    {
      long l;
      if (paramInt != AccessibilityManagerService.-get3(AccessibilityManagerService.this)) {
        l = Binder.clearCallingIdentity();
      }
      try
      {
        UserInfo localUserInfo = AccessibilityManagerService.-get16(AccessibilityManagerService.this).getProfileParent(paramInt);
        if (localUserInfo != null)
        {
          paramInt = localUserInfo.getUserHandle().getIdentifier();
          return paramInt;
        }
        return paramInt;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    private void setAccessibilityFocusedWindowLocked(int paramInt)
    {
      if (this.mAccessibilityFocusedWindowId != paramInt)
      {
        this.mAccessibilityFocusedWindowId = paramInt;
        if (this.mWindows != null)
        {
          int j = this.mWindows.size();
          int i = 0;
          if (i < j)
          {
            AccessibilityWindowInfo localAccessibilityWindowInfo = (AccessibilityWindowInfo)this.mWindows.get(i);
            if (localAccessibilityWindowInfo.getId() == paramInt) {}
            for (boolean bool = true;; bool = false)
            {
              localAccessibilityWindowInfo.setAccessibilityFocused(bool);
              i += 1;
              break;
            }
          }
        }
        notifyWindowsChanged();
      }
    }
    
    private void setActiveWindowLocked(int paramInt)
    {
      if (this.mActiveWindowId != paramInt)
      {
        this.mActiveWindowId = paramInt;
        if (this.mWindows != null)
        {
          int j = this.mWindows.size();
          int i = 0;
          if (i < j)
          {
            AccessibilityWindowInfo localAccessibilityWindowInfo = (AccessibilityWindowInfo)this.mWindows.get(i);
            if (localAccessibilityWindowInfo.getId() == paramInt) {}
            for (boolean bool = true;; bool = false)
            {
              localAccessibilityWindowInfo.setActive(bool);
              i += 1;
              break;
            }
          }
        }
        notifyWindowsChanged();
      }
    }
    
    public boolean canControlMagnification(AccessibilityManagerService.Service paramService)
    {
      boolean bool = false;
      if ((paramService.mAccessibilityServiceInfo.getCapabilities() & 0x10) != 0) {
        bool = true;
      }
      return bool;
    }
    
    public boolean canGetAccessibilityNodeInfoLocked(AccessibilityManagerService.Service paramService, int paramInt)
    {
      if (canRetrieveWindowContentLocked(paramService)) {
        return isRetrievalAllowingWindow(paramInt);
      }
      return false;
    }
    
    public boolean canPerformGestures(AccessibilityManagerService.Service paramService)
    {
      boolean bool = false;
      if ((paramService.mAccessibilityServiceInfo.getCapabilities() & 0x20) != 0) {
        bool = true;
      }
      return bool;
    }
    
    public boolean canRetrieveWindowContentLocked(AccessibilityManagerService.Service paramService)
    {
      boolean bool = false;
      if ((paramService.mAccessibilityServiceInfo.getCapabilities() & 0x1) != 0) {
        bool = true;
      }
      return bool;
    }
    
    public boolean canRetrieveWindowsLocked(AccessibilityManagerService.Service paramService)
    {
      if (canRetrieveWindowContentLocked(paramService)) {
        return paramService.mRetrieveInteractiveWindows;
      }
      return false;
    }
    
    public void clearWindowsLocked()
    {
      List localList = Collections.emptyList();
      int i = this.mActiveWindowId;
      updateWindowsLocked(localList);
      this.mActiveWindowId = i;
      this.mWindows = null;
    }
    
    public boolean computePartialInteractiveRegionForWindowLocked(int paramInt, Region paramRegion)
    {
      if (this.mWindows == null) {
        return false;
      }
      Object localObject1 = null;
      boolean bool1 = false;
      int i = this.mWindows.size() - 1;
      if (i >= 0)
      {
        AccessibilityWindowInfo localAccessibilityWindowInfo = (AccessibilityWindowInfo)this.mWindows.get(i);
        Object localObject2;
        boolean bool2;
        if (localObject1 == null)
        {
          localObject2 = localObject1;
          bool2 = bool1;
          if (localAccessibilityWindowInfo.getId() == paramInt)
          {
            localObject1 = AccessibilityManagerService.-get14(AccessibilityManagerService.this);
            localAccessibilityWindowInfo.getBoundsInScreen((Rect)localObject1);
            paramRegion.set((Rect)localObject1);
            localObject2 = paramRegion;
            bool2 = bool1;
          }
        }
        for (;;)
        {
          i -= 1;
          localObject1 = localObject2;
          bool1 = bool2;
          break;
          localObject2 = localObject1;
          bool2 = bool1;
          if (localAccessibilityWindowInfo.getType() != 4)
          {
            Rect localRect = AccessibilityManagerService.-get14(AccessibilityManagerService.this);
            localAccessibilityWindowInfo.getBoundsInScreen(localRect);
            localObject2 = localObject1;
            bool2 = bool1;
            if (((Region)localObject1).op(localRect, Region.Op.DIFFERENCE))
            {
              bool2 = true;
              localObject2 = localObject1;
            }
          }
        }
      }
      return bool1;
    }
    
    public int getActiveWindowId()
    {
      if ((this.mActiveWindowId != -1) || (this.mTouchInteractionInProgress)) {}
      for (;;)
      {
        return this.mActiveWindowId;
        this.mActiveWindowId = getFocusedWindowId();
      }
    }
    
    public boolean isCallerInteractingAcrossUsers(int paramInt)
    {
      int i = Binder.getCallingUid();
      if ((Binder.getCallingPid() == Process.myPid()) || (i == 2000)) {}
      while ((paramInt == -2) || (paramInt == -3)) {
        return true;
      }
      return false;
    }
    
    public void onTouchInteractionEnd()
    {
      synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
      {
        this.mTouchInteractionInProgress = false;
        int i = AccessibilityManagerService.-get12(AccessibilityManagerService.this).mActiveWindowId;
        setActiveWindowLocked(this.mFocusedWindowId);
        if ((i != AccessibilityManagerService.-get12(AccessibilityManagerService.this).mActiveWindowId) && (this.mAccessibilityFocusedWindowId == i) && (AccessibilityManagerService.-wrap11(AccessibilityManagerService.this).mAccessibilityFocusOnlyInActiveWindow)) {
          AccessibilityManagerService.-get9(AccessibilityManagerService.this).obtainMessage(9, i, 0).sendToTarget();
        }
        return;
      }
    }
    
    public void onTouchInteractionStart()
    {
      synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
      {
        this.mTouchInteractionInProgress = true;
        return;
      }
    }
    
    public int resolveCallingUserIdEnforcingPermissionsLocked(int paramInt)
    {
      int i = Binder.getCallingUid();
      if ((i == 0) || (i == 1000)) {}
      while ((paramInt == -2) || (paramInt == -3))
      {
        return AccessibilityManagerService.-get3(AccessibilityManagerService.this);
        if (i != 2000)
        {
          i = UserHandle.getUserId(i);
          if (i != paramInt) {
            break label64;
          }
          return resolveProfileParentLocked(paramInt);
        }
      }
      return resolveProfileParentLocked(paramInt);
      label64:
      if ((resolveProfileParentLocked(i) == AccessibilityManagerService.-get3(AccessibilityManagerService.this)) && ((paramInt == -2) || (paramInt == -3))) {
        return AccessibilityManagerService.-get3(AccessibilityManagerService.this);
      }
      if ((hasPermission("android.permission.INTERACT_ACROSS_USERS")) || (hasPermission("android.permission.INTERACT_ACROSS_USERS_FULL")))
      {
        if ((paramInt == -2) || (paramInt == -3)) {
          return AccessibilityManagerService.-get3(AccessibilityManagerService.this);
        }
      }
      else {
        throw new SecurityException("Call from user " + i + " as user " + paramInt + " without permission INTERACT_ACROSS_USERS or " + "INTERACT_ACROSS_USERS_FULL not allowed.");
      }
      throw new IllegalArgumentException("Calling user can be changed to only UserHandle.USER_CURRENT or UserHandle.USER_CURRENT_OR_SELF.");
    }
    
    /* Error */
    public void updateActiveAndAccessibilityFocusedWindowLocked(int paramInt1, long paramLong, int paramInt2, int paramInt3)
    {
      // Byte code:
      //   0: iload 4
      //   2: lookupswitch	default:+42->44, 32:+43->45, 128:+110->112, 32768:+165->167, 65536:+240->242
      //   44: return
      //   45: aload_0
      //   46: getfield 53	com/android/server/accessibility/AccessibilityManagerService$SecurityPolicy:this$0	Lcom/android/server/accessibility/AccessibilityManagerService;
      //   49: invokestatic 152	com/android/server/accessibility/AccessibilityManagerService:-get8	(Lcom/android/server/accessibility/AccessibilityManagerService;)Ljava/lang/Object;
      //   52: astore 7
      //   54: aload 7
      //   56: monitorenter
      //   57: aload 7
      //   59: astore 6
      //   61: aload_0
      //   62: getfield 53	com/android/server/accessibility/AccessibilityManagerService$SecurityPolicy:this$0	Lcom/android/server/accessibility/AccessibilityManagerService;
      //   65: invokestatic 171	com/android/server/accessibility/AccessibilityManagerService:-get18	(Lcom/android/server/accessibility/AccessibilityManagerService;)Lcom/android/server/accessibility/AccessibilityManagerService$WindowsForAccessibilityCallback;
      //   68: ifnonnull +32 -> 100
      //   71: aload_0
      //   72: aload_0
      //   73: invokespecial 301	com/android/server/accessibility/AccessibilityManagerService$SecurityPolicy:getFocusedWindowId	()I
      //   76: putfield 60	com/android/server/accessibility/AccessibilityManagerService$SecurityPolicy:mFocusedWindowId	I
      //   79: aload 7
      //   81: astore 6
      //   83: iload_1
      //   84: aload_0
      //   85: getfield 60	com/android/server/accessibility/AccessibilityManagerService$SecurityPolicy:mFocusedWindowId	I
      //   88: if_icmpne +12 -> 100
      //   91: aload_0
      //   92: iload_1
      //   93: putfield 58	com/android/server/accessibility/AccessibilityManagerService$SecurityPolicy:mActiveWindowId	I
      //   96: aload 7
      //   98: astore 6
      //   100: aload 6
      //   102: monitorexit
      //   103: return
      //   104: astore 6
      //   106: aload 7
      //   108: monitorexit
      //   109: aload 6
      //   111: athrow
      //   112: aload_0
      //   113: getfield 53	com/android/server/accessibility/AccessibilityManagerService$SecurityPolicy:this$0	Lcom/android/server/accessibility/AccessibilityManagerService;
      //   116: invokestatic 152	com/android/server/accessibility/AccessibilityManagerService:-get8	(Lcom/android/server/accessibility/AccessibilityManagerService;)Ljava/lang/Object;
      //   119: astore 7
      //   121: aload 7
      //   123: monitorenter
      //   124: aload 7
      //   126: astore 6
      //   128: aload_0
      //   129: getfield 299	com/android/server/accessibility/AccessibilityManagerService$SecurityPolicy:mTouchInteractionInProgress	Z
      //   132: ifeq -32 -> 100
      //   135: aload 7
      //   137: astore 6
      //   139: aload_0
      //   140: getfield 58	com/android/server/accessibility/AccessibilityManagerService$SecurityPolicy:mActiveWindowId	I
      //   143: iload_1
      //   144: if_icmpeq -44 -> 100
      //   147: aload_0
      //   148: iload_1
      //   149: invokespecial 314	com/android/server/accessibility/AccessibilityManagerService$SecurityPolicy:setActiveWindowLocked	(I)V
      //   152: aload 7
      //   154: astore 6
      //   156: goto -56 -> 100
      //   159: astore 6
      //   161: aload 7
      //   163: monitorexit
      //   164: aload 6
      //   166: athrow
      //   167: aload_0
      //   168: getfield 53	com/android/server/accessibility/AccessibilityManagerService$SecurityPolicy:this$0	Lcom/android/server/accessibility/AccessibilityManagerService;
      //   171: invokestatic 152	com/android/server/accessibility/AccessibilityManagerService:-get8	(Lcom/android/server/accessibility/AccessibilityManagerService;)Ljava/lang/Object;
      //   174: astore 7
      //   176: aload 7
      //   178: monitorenter
      //   179: aload 7
      //   181: astore 6
      //   183: aload_0
      //   184: getfield 62	com/android/server/accessibility/AccessibilityManagerService$SecurityPolicy:mAccessibilityFocusedWindowId	I
      //   187: iload_1
      //   188: if_icmpeq -88 -> 100
      //   191: aload_0
      //   192: getfield 53	com/android/server/accessibility/AccessibilityManagerService$SecurityPolicy:this$0	Lcom/android/server/accessibility/AccessibilityManagerService;
      //   195: invokestatic 327	com/android/server/accessibility/AccessibilityManagerService:-get9	(Lcom/android/server/accessibility/AccessibilityManagerService;)Lcom/android/server/accessibility/AccessibilityManagerService$MainHandler;
      //   198: bipush 9
      //   200: aload_0
      //   201: getfield 62	com/android/server/accessibility/AccessibilityManagerService$SecurityPolicy:mAccessibilityFocusedWindowId	I
      //   204: iconst_0
      //   205: invokevirtual 333	com/android/server/accessibility/AccessibilityManagerService$MainHandler:obtainMessage	(III)Landroid/os/Message;
      //   208: invokevirtual 338	android/os/Message:sendToTarget	()V
      //   211: aload_0
      //   212: getfield 53	com/android/server/accessibility/AccessibilityManagerService$SecurityPolicy:this$0	Lcom/android/server/accessibility/AccessibilityManagerService;
      //   215: invokestatic 312	com/android/server/accessibility/AccessibilityManagerService:-get12	(Lcom/android/server/accessibility/AccessibilityManagerService;)Lcom/android/server/accessibility/AccessibilityManagerService$SecurityPolicy;
      //   218: iload_1
      //   219: invokespecial 364	com/android/server/accessibility/AccessibilityManagerService$SecurityPolicy:setAccessibilityFocusedWindowLocked	(I)V
      //   222: aload_0
      //   223: lload_2
      //   224: putfield 66	com/android/server/accessibility/AccessibilityManagerService$SecurityPolicy:mAccessibilityFocusNodeId	J
      //   227: aload 7
      //   229: astore 6
      //   231: goto -131 -> 100
      //   234: astore 6
      //   236: aload 7
      //   238: monitorexit
      //   239: aload 6
      //   241: athrow
      //   242: aload_0
      //   243: getfield 53	com/android/server/accessibility/AccessibilityManagerService$SecurityPolicy:this$0	Lcom/android/server/accessibility/AccessibilityManagerService;
      //   246: invokestatic 152	com/android/server/accessibility/AccessibilityManagerService:-get8	(Lcom/android/server/accessibility/AccessibilityManagerService;)Ljava/lang/Object;
      //   249: astore 7
      //   251: aload 7
      //   253: monitorenter
      //   254: aload_0
      //   255: getfield 66	com/android/server/accessibility/AccessibilityManagerService$SecurityPolicy:mAccessibilityFocusNodeId	J
      //   258: lload_2
      //   259: lcmp
      //   260: ifne +10 -> 270
      //   263: aload_0
      //   264: ldc2_w 63
      //   267: putfield 66	com/android/server/accessibility/AccessibilityManagerService$SecurityPolicy:mAccessibilityFocusNodeId	J
      //   270: aload 7
      //   272: astore 6
      //   274: aload_0
      //   275: getfield 66	com/android/server/accessibility/AccessibilityManagerService$SecurityPolicy:mAccessibilityFocusNodeId	J
      //   278: ldc2_w 63
      //   281: lcmp
      //   282: ifne -182 -> 100
      //   285: aload 7
      //   287: astore 6
      //   289: aload_0
      //   290: getfield 62	com/android/server/accessibility/AccessibilityManagerService$SecurityPolicy:mAccessibilityFocusedWindowId	I
      //   293: iload_1
      //   294: if_icmpne -194 -> 100
      //   297: aload 7
      //   299: astore 6
      //   301: iload 5
      //   303: bipush 64
      //   305: if_icmpeq -205 -> 100
      //   308: aload_0
      //   309: iconst_m1
      //   310: putfield 62	com/android/server/accessibility/AccessibilityManagerService$SecurityPolicy:mAccessibilityFocusedWindowId	I
      //   313: aload 7
      //   315: astore 6
      //   317: goto -217 -> 100
      //   320: astore 6
      //   322: aload 7
      //   324: monitorexit
      //   325: aload 6
      //   327: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	328	0	this	SecurityPolicy
      //   0	328	1	paramInt1	int
      //   0	328	2	paramLong	long
      //   0	328	4	paramInt2	int
      //   0	328	5	paramInt3	int
      //   59	42	6	localObject1	Object
      //   104	6	6	localObject2	Object
      //   126	29	6	localObject3	Object
      //   159	6	6	localObject4	Object
      //   181	49	6	localObject5	Object
      //   234	6	6	localObject6	Object
      //   272	44	6	localObject7	Object
      //   320	6	6	localObject8	Object
      // Exception table:
      //   from	to	target	type
      //   61	79	104	finally
      //   83	96	104	finally
      //   128	135	159	finally
      //   139	152	159	finally
      //   183	227	234	finally
      //   254	270	320	finally
      //   274	285	320	finally
      //   289	297	320	finally
      //   308	313	320	finally
    }
    
    public void updateEventSourceLocked(AccessibilityEvent paramAccessibilityEvent)
    {
      if ((paramAccessibilityEvent.getEventType() & 0x3B9BF) == 0) {
        paramAccessibilityEvent.setSource(null);
      }
    }
    
    public void updateWindowsLocked(List<AccessibilityWindowInfo> paramList)
    {
      if (this.mWindows == null) {
        this.mWindows = new ArrayList();
      }
      int i = this.mWindows.size() - 1;
      while (i >= 0)
      {
        ((AccessibilityWindowInfo)this.mWindows.remove(i)).recycle();
        i -= 1;
      }
      this.mFocusedWindowId = -1;
      if (!this.mTouchInteractionInProgress) {
        this.mActiveWindowId = -1;
      }
      int j = 1;
      int m = paramList.size();
      if (m > 0)
      {
        i = 0;
        if (i < m)
        {
          AccessibilityWindowInfo localAccessibilityWindowInfo = (AccessibilityWindowInfo)paramList.get(i);
          int n = localAccessibilityWindowInfo.getId();
          int k = j;
          if (localAccessibilityWindowInfo.isFocused())
          {
            this.mFocusedWindowId = n;
            if (this.mTouchInteractionInProgress) {
              break label177;
            }
            this.mActiveWindowId = n;
            localAccessibilityWindowInfo.setActive(true);
            k = j;
          }
          for (;;)
          {
            this.mWindows.add(localAccessibilityWindowInfo);
            i += 1;
            j = k;
            break;
            label177:
            k = j;
            if (n == this.mActiveWindowId) {
              k = 0;
            }
          }
        }
        if ((this.mTouchInteractionInProgress) && (j != 0)) {
          this.mActiveWindowId = this.mFocusedWindowId;
        }
        i = 0;
        while (i < m)
        {
          paramList = (AccessibilityWindowInfo)this.mWindows.get(i);
          if (paramList.getId() == this.mActiveWindowId) {
            paramList.setActive(true);
          }
          if (paramList.getId() == this.mAccessibilityFocusedWindowId) {
            paramList.setAccessibilityFocused(true);
          }
          i += 1;
        }
      }
      notifyWindowsChanged();
    }
  }
  
  class Service
    extends IAccessibilityServiceConnection.Stub
    implements ServiceConnection, IBinder.DeathRecipient
  {
    AccessibilityServiceInfo mAccessibilityServiceInfo;
    ComponentName mComponentName;
    public Handler mEventDispatchHandler = new Handler(AccessibilityManagerService.-get9(AccessibilityManagerService.this).getLooper())
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        int i = paramAnonymousMessage.what;
        paramAnonymousMessage = (AccessibilityEvent)paramAnonymousMessage.obj;
        AccessibilityManagerService.Service.-wrap0(AccessibilityManagerService.Service.this, i, paramAnonymousMessage);
      }
    };
    int mEventTypes;
    int mFeedbackType;
    int mFetchFlags;
    int mId = 0;
    Intent mIntent;
    public final InvocationHandler mInvocationHandler = new InvocationHandler(AccessibilityManagerService.-get9(AccessibilityManagerService.this).getLooper());
    boolean mIsAutomation;
    boolean mIsDefault;
    long mNotificationTimeout;
    final IBinder mOverlayWindowToken = new Binder();
    Set<String> mPackageNames = new HashSet();
    final SparseArray<AccessibilityEvent> mPendingEvents = new SparseArray();
    boolean mRequestEnhancedWebAccessibility;
    boolean mRequestFilterKeyEvents;
    boolean mRequestTouchExplorationMode;
    final ResolveInfo mResolveInfo;
    boolean mRetrieveInteractiveWindows;
    IBinder mService;
    IAccessibilityServiceClient mServiceInterface;
    final int mUserId;
    boolean mWasConnectedAndDied;
    
    public Service(int paramInt, ComponentName paramComponentName, AccessibilityServiceInfo paramAccessibilityServiceInfo)
    {
      this.mUserId = paramInt;
      this.mResolveInfo = paramAccessibilityServiceInfo.getResolveInfo();
      paramInt = AccessibilityManagerService.-get20();
      AccessibilityManagerService.-set0(paramInt + 1);
      this.mId = paramInt;
      this.mComponentName = paramComponentName;
      this.mAccessibilityServiceInfo = paramAccessibilityServiceInfo;
      this.mIsAutomation = AccessibilityManagerService.-get19().equals(paramComponentName);
      long l;
      if (!this.mIsAutomation)
      {
        this.mIntent = new Intent().setComponent(this.mComponentName);
        this.mIntent.putExtra("android.intent.extra.client_label", 17040514);
        l = Binder.clearCallingIdentity();
      }
      try
      {
        this.mIntent.putExtra("android.intent.extra.client_intent", PendingIntent.getActivity(AccessibilityManagerService.-get2(AccessibilityManagerService.this), 0, new Intent("android.settings.ACCESSIBILITY_SETTINGS"), 0));
        Binder.restoreCallingIdentity(l);
        setDynamicallyConfigurableProperties(paramAccessibilityServiceInfo);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    private void expandNotifications()
    {
      long l = Binder.clearCallingIdentity();
      ((StatusBarManager)AccessibilityManagerService.-get2(AccessibilityManagerService.this).getSystemService("statusbar")).expandNotificationsPanel();
      Binder.restoreCallingIdentity(l);
    }
    
    private void expandQuickSettings()
    {
      long l = Binder.clearCallingIdentity();
      ((StatusBarManager)AccessibilityManagerService.-get2(AccessibilityManagerService.this).getSystemService("statusbar")).expandSettingsPanel();
      Binder.restoreCallingIdentity(l);
    }
    
    private IAccessibilityInteractionConnection getConnectionLocked(int paramInt)
    {
      AccessibilityManagerService.AccessibilityConnectionWrapper localAccessibilityConnectionWrapper2 = (AccessibilityManagerService.AccessibilityConnectionWrapper)AccessibilityManagerService.-get5(AccessibilityManagerService.this).get(paramInt);
      AccessibilityManagerService.AccessibilityConnectionWrapper localAccessibilityConnectionWrapper1 = localAccessibilityConnectionWrapper2;
      if (localAccessibilityConnectionWrapper2 == null) {
        localAccessibilityConnectionWrapper1 = (AccessibilityManagerService.AccessibilityConnectionWrapper)AccessibilityManagerService.-wrap11(AccessibilityManagerService.this).mInteractionConnections.get(paramInt);
      }
      if ((localAccessibilityConnectionWrapper1 != null) && (AccessibilityManagerService.AccessibilityConnectionWrapper.-get0(localAccessibilityConnectionWrapper1) != null)) {
        return AccessibilityManagerService.AccessibilityConnectionWrapper.-get0(localAccessibilityConnectionWrapper1);
      }
      return null;
    }
    
    private String getPkgNameOfBind(Intent paramIntent, ComponentName paramComponentName)
    {
      String str = null;
      if (paramComponentName != null) {
        str = paramComponentName.getPackageName();
      }
      if (str != null) {
        return str;
      }
      if (paramIntent == null) {
        return null;
      }
      str = paramIntent.getPackage();
      paramComponentName = str;
      if (str == null)
      {
        paramIntent = paramIntent.getComponent();
        paramComponentName = str;
        if (paramIntent != null) {
          paramComponentName = paramIntent.getPackageName();
        }
      }
      return paramComponentName;
    }
    
    private boolean isCalledForCurrentUserLocked()
    {
      return AccessibilityManagerService.-get12(AccessibilityManagerService.this).resolveCallingUserIdEnforcingPermissionsLocked(-2) == AccessibilityManagerService.-get3(AccessibilityManagerService.this);
    }
    
    private void notifyAccessibilityEventInternal(int paramInt, AccessibilityEvent paramAccessibilityEvent)
    {
      synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
      {
        localIAccessibilityServiceClient = this.mServiceInterface;
        if (localIAccessibilityServiceClient == null) {
          return;
        }
        localAccessibilityEvent = paramAccessibilityEvent;
        if (paramAccessibilityEvent == null)
        {
          localAccessibilityEvent = (AccessibilityEvent)this.mPendingEvents.get(paramInt);
          if (localAccessibilityEvent == null) {
            return;
          }
          this.mPendingEvents.remove(paramInt);
        }
        if (AccessibilityManagerService.-get12(AccessibilityManagerService.this).canRetrieveWindowContentLocked(this))
        {
          localAccessibilityEvent.setConnectionId(this.mId);
          localAccessibilityEvent.setSealed(true);
        }
        try
        {
          localIAccessibilityServiceClient.onAccessibilityEvent(localAccessibilityEvent);
          return;
        }
        catch (RemoteException paramAccessibilityEvent)
        {
          Slog.e("AccessibilityManagerService", "Error during sending " + localAccessibilityEvent + " to " + localIAccessibilityServiceClient, paramAccessibilityEvent);
          return;
        }
        finally
        {
          localAccessibilityEvent.recycle();
        }
        localAccessibilityEvent.setSource(null);
      }
    }
    
    private void notifyClearAccessibilityCacheInternal()
    {
      IAccessibilityServiceClient localIAccessibilityServiceClient;
      synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
      {
        localIAccessibilityServiceClient = this.mServiceInterface;
        if (localIAccessibilityServiceClient == null) {}
      }
    }
    
    private void notifyGestureInternal(int paramInt)
    {
      IAccessibilityServiceClient localIAccessibilityServiceClient;
      synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
      {
        localIAccessibilityServiceClient = this.mServiceInterface;
        if (localIAccessibilityServiceClient == null) {}
      }
    }
    
    private void notifyMagnificationChangedInternal(Region paramRegion, float paramFloat1, float paramFloat2, float paramFloat3)
    {
      IAccessibilityServiceClient localIAccessibilityServiceClient;
      synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
      {
        localIAccessibilityServiceClient = this.mServiceInterface;
        if (localIAccessibilityServiceClient == null) {}
      }
    }
    
    private void notifySoftKeyboardShowModeChangedInternal(int paramInt)
    {
      IAccessibilityServiceClient localIAccessibilityServiceClient;
      synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
      {
        localIAccessibilityServiceClient = this.mServiceInterface;
        if (localIAccessibilityServiceClient == null) {}
      }
    }
    
    private void openRecents()
    {
      long l = Binder.clearCallingIdentity();
      ((StatusBarManagerInternal)LocalServices.getService(StatusBarManagerInternal.class)).toggleRecentApps();
      Binder.restoreCallingIdentity(l);
    }
    
    private int resolveAccessibilityWindowIdForFindFocusLocked(int paramInt1, int paramInt2)
    {
      if (paramInt1 == Integer.MAX_VALUE) {
        return AccessibilityManagerService.-get12(AccessibilityManagerService.this).mActiveWindowId;
      }
      if (paramInt1 == -2)
      {
        if (paramInt2 == 1) {
          return AccessibilityManagerService.-get12(AccessibilityManagerService.this).mFocusedWindowId;
        }
        if (paramInt2 == 2) {
          return AccessibilityManagerService.-get12(AccessibilityManagerService.this).mAccessibilityFocusedWindowId;
        }
      }
      return paramInt1;
    }
    
    private int resolveAccessibilityWindowIdLocked(int paramInt)
    {
      if (paramInt == Integer.MAX_VALUE) {
        return AccessibilityManagerService.-get12(AccessibilityManagerService.this).getActiveWindowId();
      }
      return paramInt;
    }
    
    private void sendDownAndUpKeyEvents(int paramInt)
    {
      long l1 = Binder.clearCallingIdentity();
      long l2 = SystemClock.uptimeMillis();
      KeyEvent localKeyEvent = KeyEvent.obtain(l2, l2, 0, paramInt, 0, 0, -1, 0, 8, 257, null);
      InputManager.getInstance().injectInputEvent(localKeyEvent, 0);
      localKeyEvent.recycle();
      localKeyEvent = KeyEvent.obtain(l2, SystemClock.uptimeMillis(), 1, paramInt, 0, 0, -1, 0, 8, 257, null);
      InputManager.getInstance().injectInputEvent(localKeyEvent, 0);
      localKeyEvent.recycle();
      Binder.restoreCallingIdentity(l1);
    }
    
    private void showGlobalActions()
    {
      AccessibilityManagerService.-get17(AccessibilityManagerService.this).showGlobalActions();
    }
    
    private void toggleSplitScreen()
    {
      ((StatusBarManagerInternal)LocalServices.getService(StatusBarManagerInternal.class)).toggleSplitScreen();
    }
    
    public boolean bindLocked()
    {
      Object localObject = getPkgNameOfBind(this.mIntent, this.mComponentName);
      if ((localObject != null) && (((String)localObject).length() > 1)) {
        AccessibilityManagerService.-get1(AccessibilityManagerService.this).updateAccesibilityServiceFlag((String)localObject, 1);
      }
      localObject = AccessibilityManagerService.-wrap12(AccessibilityManagerService.this, this.mUserId);
      long l;
      if (!this.mIsAutomation) {
        l = Binder.clearCallingIdentity();
      }
      for (;;)
      {
        try
        {
          if ((this.mService == null) && (AccessibilityManagerService.-get2(AccessibilityManagerService.this).bindServiceAsUser(this.mIntent, this, 33554433, new UserHandle(this.mUserId)))) {
            ((AccessibilityManagerService.UserState)localObject).mBindingServices.add(this.mComponentName);
          }
          return false;
        }
        finally
        {
          Binder.restoreCallingIdentity(l);
        }
        localUserState.mBindingServices.add(this.mComponentName);
        AccessibilityManagerService.-get9(AccessibilityManagerService.this).post(new Runnable()
        {
          public void run()
          {
            AccessibilityManagerService.Service.this.onServiceConnected(AccessibilityManagerService.Service.this.mComponentName, AccessibilityManagerService.UserState.-get2(localUserState).asBinder());
          }
        });
        AccessibilityManagerService.UserState.-set1(localUserState, this);
      }
    }
    
    public void binderDied()
    {
      synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
      {
        boolean bool = isConnectedLocked();
        if (!bool) {
          return;
        }
        this.mWasConnectedAndDied = true;
        AccessibilityManagerService.-wrap13(AccessibilityManagerService.this).flush(this);
        AccessibilityManagerService.UserState localUserState = AccessibilityManagerService.-wrap12(AccessibilityManagerService.this, this.mUserId);
        resetLocked();
        if (this.mIsAutomation)
        {
          AccessibilityManagerService.-wrap21(AccessibilityManagerService.this, this, localUserState);
          localUserState.mInstalledServices.remove(this.mAccessibilityServiceInfo);
          localUserState.mEnabledServices.remove(this.mComponentName);
          localUserState.destroyUiAutomationService();
          AccessibilityManagerService.-wrap2(AccessibilityManagerService.this, localUserState);
        }
        if (this.mId == AccessibilityManagerService.this.getMagnificationController().getIdOfLastServiceToMagnify()) {
          AccessibilityManagerService.this.getMagnificationController().resetIfNeeded(true);
        }
        AccessibilityManagerService.-wrap18(AccessibilityManagerService.this, localUserState);
        return;
      }
    }
    
    public boolean canReceiveEventsLocked()
    {
      boolean bool2 = false;
      boolean bool1 = bool2;
      if (this.mEventTypes != 0)
      {
        bool1 = bool2;
        if (this.mFeedbackType != 0)
        {
          bool1 = bool2;
          if (this.mService != null) {
            bool1 = true;
          }
        }
      }
      return bool1;
    }
    
    public boolean canRetrieveInteractiveWindowsLocked()
    {
      if (AccessibilityManagerService.-get12(AccessibilityManagerService.this).canRetrieveWindowContentLocked(this)) {
        return this.mRetrieveInteractiveWindows;
      }
      return false;
    }
    
    public void disableSelf()
    {
      synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
      {
        AccessibilityManagerService.UserState localUserState = AccessibilityManagerService.-wrap12(AccessibilityManagerService.this, this.mUserId);
        long l;
        if (localUserState.mEnabledServices.remove(this.mComponentName)) {
          l = Binder.clearCallingIdentity();
        }
        try
        {
          AccessibilityManagerService.-wrap19(AccessibilityManagerService.this, "enabled_accessibility_services", localUserState.mEnabledServices, this.mUserId);
          Binder.restoreCallingIdentity(l);
          AccessibilityManagerService.-wrap18(AccessibilityManagerService.this, localUserState);
          return;
        }
        finally
        {
          localObject2 = finally;
          Binder.restoreCallingIdentity(l);
          throw ((Throwable)localObject2);
        }
      }
    }
    
    public void dump(FileDescriptor arg1, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      AccessibilityManagerService.SecurityPolicy.-wrap3(AccessibilityManagerService.-get12(AccessibilityManagerService.this), "android.permission.DUMP", "dump");
      synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
      {
        paramPrintWriter.append("Service[label=" + this.mAccessibilityServiceInfo.getResolveInfo().loadLabel(AccessibilityManagerService.-get2(AccessibilityManagerService.this).getPackageManager()));
        paramPrintWriter.append(", feedbackType" + AccessibilityServiceInfo.feedbackTypeToString(this.mFeedbackType));
        paramPrintWriter.append(", capabilities=" + this.mAccessibilityServiceInfo.getCapabilities());
        paramPrintWriter.append(", eventTypes=" + AccessibilityEvent.eventTypeToString(this.mEventTypes));
        paramPrintWriter.append(", notificationTimeout=" + this.mNotificationTimeout);
        paramPrintWriter.append("]");
        return;
      }
    }
    
    public boolean findAccessibilityNodeInfoByAccessibilityId(int paramInt1, long paramLong1, int paramInt2, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, int paramInt3, long paramLong2)
      throws RemoteException
    {
      Object localObject2 = Region.obtain();
      int i;
      synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
      {
        boolean bool = isCalledForCurrentUserLocked();
        if (!bool) {
          return false;
        }
        paramInt1 = resolveAccessibilityWindowIdLocked(paramInt1);
        bool = AccessibilityManagerService.-get12(AccessibilityManagerService.this).canGetAccessibilityNodeInfoLocked(this, paramInt1);
        if (!bool) {
          return false;
        }
        localIAccessibilityInteractionConnection = getConnectionLocked(paramInt1);
        if (localIAccessibilityInteractionConnection == null) {
          return false;
        }
        localObject1 = localObject2;
        if (!AccessibilityManagerService.-get12(AccessibilityManagerService.this).computePartialInteractiveRegionForWindowLocked(paramInt1, (Region)localObject2))
        {
          ((Region)localObject2).recycle();
          localObject1 = null;
        }
        i = Binder.getCallingPid();
        l = Binder.clearCallingIdentity();
        localObject2 = AccessibilityManagerService.-wrap0(AccessibilityManagerService.this, paramInt1);
      }
    }
    
    public boolean findAccessibilityNodeInfosByText(int paramInt1, long paramLong1, String paramString, int paramInt2, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, long paramLong2)
      throws RemoteException
    {
      Object localObject2 = Region.obtain();
      int i;
      synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
      {
        boolean bool = isCalledForCurrentUserLocked();
        if (!bool) {
          return false;
        }
        paramInt1 = resolveAccessibilityWindowIdLocked(paramInt1);
        bool = AccessibilityManagerService.-get12(AccessibilityManagerService.this).canGetAccessibilityNodeInfoLocked(this, paramInt1);
        if (!bool) {
          return false;
        }
        localIAccessibilityInteractionConnection = getConnectionLocked(paramInt1);
        if (localIAccessibilityInteractionConnection == null) {
          return false;
        }
        localObject1 = localObject2;
        if (!AccessibilityManagerService.-get12(AccessibilityManagerService.this).computePartialInteractiveRegionForWindowLocked(paramInt1, (Region)localObject2))
        {
          ((Region)localObject2).recycle();
          localObject1 = null;
        }
        i = Binder.getCallingPid();
        l = Binder.clearCallingIdentity();
        localObject2 = AccessibilityManagerService.-wrap0(AccessibilityManagerService.this, paramInt1);
      }
    }
    
    public boolean findAccessibilityNodeInfosByViewId(int paramInt1, long paramLong1, String paramString, int paramInt2, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, long paramLong2)
      throws RemoteException
    {
      Object localObject2 = Region.obtain();
      int i;
      synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
      {
        boolean bool = isCalledForCurrentUserLocked();
        if (!bool) {
          return false;
        }
        paramInt1 = resolveAccessibilityWindowIdLocked(paramInt1);
        bool = AccessibilityManagerService.-get12(AccessibilityManagerService.this).canGetAccessibilityNodeInfoLocked(this, paramInt1);
        if (!bool) {
          return false;
        }
        localIAccessibilityInteractionConnection = getConnectionLocked(paramInt1);
        if (localIAccessibilityInteractionConnection == null) {
          return false;
        }
        localObject1 = localObject2;
        if (!AccessibilityManagerService.-get12(AccessibilityManagerService.this).computePartialInteractiveRegionForWindowLocked(paramInt1, (Region)localObject2))
        {
          ((Region)localObject2).recycle();
          localObject1 = null;
        }
        i = Binder.getCallingPid();
        l = Binder.clearCallingIdentity();
        localObject2 = AccessibilityManagerService.-wrap0(AccessibilityManagerService.this, paramInt1);
      }
    }
    
    public boolean findFocus(int paramInt1, long paramLong1, int paramInt2, int paramInt3, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, long paramLong2)
      throws RemoteException
    {
      Object localObject2 = Region.obtain();
      int i;
      synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
      {
        boolean bool = isCalledForCurrentUserLocked();
        if (!bool) {
          return false;
        }
        paramInt1 = resolveAccessibilityWindowIdForFindFocusLocked(paramInt1, paramInt2);
        bool = AccessibilityManagerService.-get12(AccessibilityManagerService.this).canGetAccessibilityNodeInfoLocked(this, paramInt1);
        if (!bool) {
          return false;
        }
        localIAccessibilityInteractionConnection = getConnectionLocked(paramInt1);
        if (localIAccessibilityInteractionConnection == null) {
          return false;
        }
        localObject1 = localObject2;
        if (!AccessibilityManagerService.-get12(AccessibilityManagerService.this).computePartialInteractiveRegionForWindowLocked(paramInt1, (Region)localObject2))
        {
          ((Region)localObject2).recycle();
          localObject1 = null;
        }
        i = Binder.getCallingPid();
        l = Binder.clearCallingIdentity();
        localObject2 = AccessibilityManagerService.-wrap0(AccessibilityManagerService.this, paramInt1);
      }
    }
    
    public boolean focusSearch(int paramInt1, long paramLong1, int paramInt2, int paramInt3, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, long paramLong2)
      throws RemoteException
    {
      Object localObject2 = Region.obtain();
      int i;
      synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
      {
        boolean bool = isCalledForCurrentUserLocked();
        if (!bool) {
          return false;
        }
        paramInt1 = resolveAccessibilityWindowIdLocked(paramInt1);
        bool = AccessibilityManagerService.-get12(AccessibilityManagerService.this).canGetAccessibilityNodeInfoLocked(this, paramInt1);
        if (!bool) {
          return false;
        }
        localIAccessibilityInteractionConnection = getConnectionLocked(paramInt1);
        if (localIAccessibilityInteractionConnection == null) {
          return false;
        }
        localObject1 = localObject2;
        if (!AccessibilityManagerService.-get12(AccessibilityManagerService.this).computePartialInteractiveRegionForWindowLocked(paramInt1, (Region)localObject2))
        {
          ((Region)localObject2).recycle();
          localObject1 = null;
        }
        i = Binder.getCallingPid();
        l = Binder.clearCallingIdentity();
        localObject2 = AccessibilityManagerService.-wrap0(AccessibilityManagerService.this, paramInt1);
      }
    }
    
    public float getMagnificationCenterX()
    {
      float f;
      synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
      {
        boolean bool = isCalledForCurrentUserLocked();
        if (!bool) {
          return 0.0F;
        }
        l = Binder.clearCallingIdentity();
      }
    }
    
    public float getMagnificationCenterY()
    {
      float f;
      synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
      {
        boolean bool = isCalledForCurrentUserLocked();
        if (!bool) {
          return 0.0F;
        }
        l = Binder.clearCallingIdentity();
      }
    }
    
    public Region getMagnificationRegion()
    {
      synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
      {
        Region localRegion = Region.obtain();
        boolean bool1 = isCalledForCurrentUserLocked();
        if (!bool1) {
          return localRegion;
        }
        MagnificationController localMagnificationController = AccessibilityManagerService.this.getMagnificationController();
        bool1 = AccessibilityManagerService.-get12(AccessibilityManagerService.this).canControlMagnification(this);
        boolean bool2 = localMagnificationController.isRegisteredLocked();
        if ((!bool2) && (bool1)) {
          localMagnificationController.register();
        }
        long l = Binder.clearCallingIdentity();
        try
        {
          localMagnificationController.getMagnificationRegion(localRegion);
          Binder.restoreCallingIdentity(l);
          if ((!bool2) && (bool1)) {
            localMagnificationController.unregister();
          }
          return localRegion;
        }
        finally
        {
          localObject3 = finally;
          Binder.restoreCallingIdentity(l);
          if ((!bool2) && (bool1)) {
            localMagnificationController.unregister();
          }
          throw ((Throwable)localObject3);
        }
      }
    }
    
    public float getMagnificationScale()
    {
      float f;
      synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
      {
        boolean bool = isCalledForCurrentUserLocked();
        if (!bool) {
          return 1.0F;
        }
        l = Binder.clearCallingIdentity();
      }
    }
    
    public AccessibilityServiceInfo getServiceInfo()
    {
      synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
      {
        AccessibilityServiceInfo localAccessibilityServiceInfo = this.mAccessibilityServiceInfo;
        return localAccessibilityServiceInfo;
      }
    }
    
    public AccessibilityWindowInfo getWindow(int paramInt)
    {
      AccessibilityManagerService.-wrap16(AccessibilityManagerService.this);
      synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
      {
        boolean bool = isCalledForCurrentUserLocked();
        if (!bool) {
          return null;
        }
        bool = AccessibilityManagerService.-get12(AccessibilityManagerService.this).canRetrieveWindowsLocked(this);
        if (!bool) {
          return null;
        }
        AccessibilityWindowInfo localAccessibilityWindowInfo = AccessibilityManagerService.SecurityPolicy.-wrap0(AccessibilityManagerService.-get12(AccessibilityManagerService.this), paramInt);
        if (localAccessibilityWindowInfo != null)
        {
          localAccessibilityWindowInfo = AccessibilityWindowInfo.obtain(localAccessibilityWindowInfo);
          localAccessibilityWindowInfo.setConnectionId(this.mId);
          return localAccessibilityWindowInfo;
        }
        return null;
      }
    }
    
    public List<AccessibilityWindowInfo> getWindows()
    {
      AccessibilityManagerService.-wrap16(AccessibilityManagerService.this);
      synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
      {
        boolean bool = isCalledForCurrentUserLocked();
        if (!bool) {
          return null;
        }
        bool = AccessibilityManagerService.-get12(AccessibilityManagerService.this).canRetrieveWindowsLocked(this);
        if (!bool) {
          return null;
        }
        Object localObject2 = AccessibilityManagerService.-get12(AccessibilityManagerService.this).mWindows;
        if (localObject2 == null) {
          return null;
        }
        localObject2 = new ArrayList();
        int j = AccessibilityManagerService.-get12(AccessibilityManagerService.this).mWindows.size();
        int i = 0;
        while (i < j)
        {
          AccessibilityWindowInfo localAccessibilityWindowInfo = AccessibilityWindowInfo.obtain((AccessibilityWindowInfo)AccessibilityManagerService.-get12(AccessibilityManagerService.this).mWindows.get(i));
          localAccessibilityWindowInfo.setConnectionId(this.mId);
          ((List)localObject2).add(localAccessibilityWindowInfo);
          i += 1;
        }
        return (List<AccessibilityWindowInfo>)localObject2;
      }
    }
    
    public boolean isConnectedLocked()
    {
      return this.mService != null;
    }
    
    public void notifyAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
    {
      synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
      {
        int i = paramAccessibilityEvent.getEventType();
        paramAccessibilityEvent = AccessibilityEvent.obtain(paramAccessibilityEvent);
        if ((this.mNotificationTimeout > 0L) && (i != 2048))
        {
          AccessibilityEvent localAccessibilityEvent = (AccessibilityEvent)this.mPendingEvents.get(i);
          this.mPendingEvents.put(i, paramAccessibilityEvent);
          if (localAccessibilityEvent != null)
          {
            this.mEventDispatchHandler.removeMessages(i);
            localAccessibilityEvent.recycle();
          }
          paramAccessibilityEvent = this.mEventDispatchHandler.obtainMessage(i);
          this.mEventDispatchHandler.sendMessageDelayed(paramAccessibilityEvent, this.mNotificationTimeout);
          return;
        }
        paramAccessibilityEvent = this.mEventDispatchHandler.obtainMessage(i, paramAccessibilityEvent);
      }
    }
    
    public void notifyClearAccessibilityNodeInfoCache()
    {
      this.mInvocationHandler.sendEmptyMessage(2);
    }
    
    public void notifyGesture(int paramInt)
    {
      this.mInvocationHandler.obtainMessage(1, paramInt, 0).sendToTarget();
    }
    
    public void notifyMagnificationChangedLocked(Region paramRegion, float paramFloat1, float paramFloat2, float paramFloat3)
    {
      this.mInvocationHandler.notifyMagnificationChangedLocked(paramRegion, paramFloat1, paramFloat2, paramFloat3);
    }
    
    public void notifySoftKeyboardShowModeChangedLocked(int paramInt)
    {
      this.mInvocationHandler.notifySoftKeyboardShowModeChangedLocked(paramInt);
    }
    
    public void onAdded()
      throws RemoteException
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        AccessibilityManagerService.-get17(AccessibilityManagerService.this).addWindowToken(this.mOverlayWindowToken, 2032);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void onRemoved()
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        AccessibilityManagerService.-get17(AccessibilityManagerService.this).removeWindowToken(this.mOverlayWindowToken, true);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    /* Error */
    public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 92	com/android/server/accessibility/AccessibilityManagerService$Service:this$0	Lcom/android/server/accessibility/AccessibilityManagerService;
      //   4: invokestatic 287	com/android/server/accessibility/AccessibilityManagerService:-get8	(Lcom/android/server/accessibility/AccessibilityManagerService;)Ljava/lang/Object;
      //   7: astore_1
      //   8: aload_1
      //   9: monitorenter
      //   10: aload_0
      //   11: getfield 358	com/android/server/accessibility/AccessibilityManagerService$Service:mService	Landroid/os/IBinder;
      //   14: aload_2
      //   15: if_acmpeq +38 -> 53
      //   18: aload_0
      //   19: getfield 358	com/android/server/accessibility/AccessibilityManagerService$Service:mService	Landroid/os/IBinder;
      //   22: ifnull +15 -> 37
      //   25: aload_0
      //   26: getfield 358	com/android/server/accessibility/AccessibilityManagerService$Service:mService	Landroid/os/IBinder;
      //   29: aload_0
      //   30: iconst_0
      //   31: invokeinterface 793 3 0
      //   36: pop
      //   37: aload_0
      //   38: aload_2
      //   39: putfield 358	com/android/server/accessibility/AccessibilityManagerService$Service:mService	Landroid/os/IBinder;
      //   42: aload_0
      //   43: getfield 358	com/android/server/accessibility/AccessibilityManagerService$Service:mService	Landroid/os/IBinder;
      //   46: aload_0
      //   47: iconst_0
      //   48: invokeinterface 797 3 0
      //   53: aload_0
      //   54: aload_2
      //   55: invokestatic 803	android/accessibilityservice/IAccessibilityServiceClient$Stub:asInterface	(Landroid/os/IBinder;)Landroid/accessibilityservice/IAccessibilityServiceClient;
      //   58: putfield 289	com/android/server/accessibility/AccessibilityManagerService$Service:mServiceInterface	Landroid/accessibilityservice/IAccessibilityServiceClient;
      //   61: aload_0
      //   62: getfield 92	com/android/server/accessibility/AccessibilityManagerService$Service:this$0	Lcom/android/server/accessibility/AccessibilityManagerService;
      //   65: aload_0
      //   66: getfield 131	com/android/server/accessibility/AccessibilityManagerService$Service:mUserId	I
      //   69: invokestatic 453	com/android/server/accessibility/AccessibilityManagerService:-wrap12	(Lcom/android/server/accessibility/AccessibilityManagerService;I)Lcom/android/server/accessibility/AccessibilityManagerService$UserState;
      //   72: astore_3
      //   73: aload_0
      //   74: getfield 92	com/android/server/accessibility/AccessibilityManagerService$Service:this$0	Lcom/android/server/accessibility/AccessibilityManagerService;
      //   77: aload_0
      //   78: aload_3
      //   79: invokestatic 806	com/android/server/accessibility/AccessibilityManagerService:-wrap15	(Lcom/android/server/accessibility/AccessibilityManagerService;Lcom/android/server/accessibility/AccessibilityManagerService$Service;Lcom/android/server/accessibility/AccessibilityManagerService$UserState;)V
      //   82: aload_3
      //   83: getfield 465	com/android/server/accessibility/AccessibilityManagerService$UserState:mBindingServices	Ljava/util/Set;
      //   86: aload_0
      //   87: getfield 149	com/android/server/accessibility/AccessibilityManagerService$Service:mComponentName	Landroid/content/ComponentName;
      //   90: invokeinterface 809 2 0
      //   95: ifne +10 -> 105
      //   98: aload_0
      //   99: getfield 487	com/android/server/accessibility/AccessibilityManagerService$Service:mWasConnectedAndDied	Z
      //   102: ifeq +110 -> 212
      //   105: aload_3
      //   106: getfield 465	com/android/server/accessibility/AccessibilityManagerService$UserState:mBindingServices	Ljava/util/Set;
      //   109: aload_0
      //   110: getfield 149	com/android/server/accessibility/AccessibilityManagerService$Service:mComponentName	Landroid/content/ComponentName;
      //   113: invokeinterface 515 2 0
      //   118: pop
      //   119: aload_0
      //   120: iconst_0
      //   121: putfield 487	com/android/server/accessibility/AccessibilityManagerService$Service:mWasConnectedAndDied	Z
      //   124: aload_0
      //   125: getfield 289	com/android/server/accessibility/AccessibilityManagerService$Service:mServiceInterface	Landroid/accessibilityservice/IAccessibilityServiceClient;
      //   128: aload_0
      //   129: aload_0
      //   130: getfield 96	com/android/server/accessibility/AccessibilityManagerService$Service:mId	I
      //   133: aload_0
      //   134: getfield 106	com/android/server/accessibility/AccessibilityManagerService$Service:mOverlayWindowToken	Landroid/os/IBinder;
      //   137: invokeinterface 813 4 0
      //   142: aload_0
      //   143: getfield 92	com/android/server/accessibility/AccessibilityManagerService$Service:this$0	Lcom/android/server/accessibility/AccessibilityManagerService;
      //   146: aload_3
      //   147: invokestatic 538	com/android/server/accessibility/AccessibilityManagerService:-wrap18	(Lcom/android/server/accessibility/AccessibilityManagerService;Lcom/android/server/accessibility/AccessibilityManagerService$UserState;)V
      //   150: aload_1
      //   151: monitorexit
      //   152: return
      //   153: astore_2
      //   154: ldc_w 320
      //   157: ldc_w 815
      //   160: invokestatic 818	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   163: pop
      //   164: aload_0
      //   165: invokevirtual 820	com/android/server/accessibility/AccessibilityManagerService$Service:binderDied	()V
      //   168: aload_1
      //   169: monitorexit
      //   170: return
      //   171: astore_3
      //   172: ldc_w 320
      //   175: new 322	java/lang/StringBuilder
      //   178: dup
      //   179: invokespecial 323	java/lang/StringBuilder:<init>	()V
      //   182: ldc_w 822
      //   185: invokevirtual 329	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   188: aload_2
      //   189: invokevirtual 332	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   192: invokevirtual 337	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   195: aload_3
      //   196: invokestatic 825	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   199: pop
      //   200: aload_0
      //   201: invokevirtual 820	com/android/server/accessibility/AccessibilityManagerService$Service:binderDied	()V
      //   204: goto -54 -> 150
      //   207: astore_2
      //   208: aload_1
      //   209: monitorexit
      //   210: aload_2
      //   211: athrow
      //   212: aload_0
      //   213: invokevirtual 820	com/android/server/accessibility/AccessibilityManagerService$Service:binderDied	()V
      //   216: goto -66 -> 150
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	219	0	this	Service
      //   0	219	1	paramComponentName	ComponentName
      //   0	219	2	paramIBinder	IBinder
      //   72	75	3	localUserState	AccessibilityManagerService.UserState
      //   171	25	3	localRemoteException	RemoteException
      // Exception table:
      //   from	to	target	type
      //   42	53	153	android/os/RemoteException
      //   124	150	171	android/os/RemoteException
      //   10	37	207	finally
      //   37	42	207	finally
      //   42	53	207	finally
      //   53	105	207	finally
      //   105	124	207	finally
      //   124	150	207	finally
      //   154	168	207	finally
      //   172	204	207	finally
      //   212	216	207	finally
    }
    
    public void onServiceDisconnected(ComponentName paramComponentName)
    {
      binderDied();
    }
    
    public boolean performAccessibilityAction(int paramInt1, long paramLong1, int paramInt2, Bundle paramBundle, int paramInt3, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, long paramLong2)
      throws RemoteException
    {
      synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
      {
        boolean bool = isCalledForCurrentUserLocked();
        if (!bool) {
          return false;
        }
        paramInt1 = resolveAccessibilityWindowIdLocked(paramInt1);
        bool = AccessibilityManagerService.-get12(AccessibilityManagerService.this).canGetAccessibilityNodeInfoLocked(this, paramInt1);
        if (!bool) {
          return false;
        }
        IAccessibilityInteractionConnection localIAccessibilityInteractionConnection = getConnectionLocked(paramInt1);
        if (localIAccessibilityInteractionConnection == null) {
          return false;
        }
        paramInt1 = Binder.getCallingPid();
        l = Binder.clearCallingIdentity();
        try
        {
          AccessibilityManagerService.-get11(AccessibilityManagerService.this).userActivity(SystemClock.uptimeMillis(), 3, 0);
          localIAccessibilityInteractionConnection.performAccessibilityAction(paramLong1, paramInt2, paramBundle, paramInt3, paramIAccessibilityInteractionConnectionCallback, this.mFetchFlags, paramInt1, paramLong2);
          Binder.restoreCallingIdentity(l);
        }
        catch (RemoteException paramBundle)
        {
          for (;;)
          {
            paramBundle = paramBundle;
            Binder.restoreCallingIdentity(l);
          }
        }
        finally
        {
          paramBundle = finally;
          Binder.restoreCallingIdentity(l);
          throw paramBundle;
        }
        return true;
      }
    }
    
    public boolean performGlobalAction(int paramInt)
    {
      long l;
      synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
      {
        boolean bool = isCalledForCurrentUserLocked();
        if (!bool) {
          return false;
        }
        l = Binder.clearCallingIdentity();
      }
      try
      {
        AccessibilityManagerService.-get11(AccessibilityManagerService.this).userActivity(SystemClock.uptimeMillis(), 3, 0);
        switch (paramInt)
        {
        default: 
          return false;
          localObject3 = finally;
          throw ((Throwable)localObject3);
        case 1: 
          sendDownAndUpKeyEvents(4);
          return true;
        case 2: 
          sendDownAndUpKeyEvents(3);
          return true;
        case 3: 
          openRecents();
          return true;
        case 4: 
          expandNotifications();
          return true;
        case 5: 
          expandQuickSettings();
          return true;
        case 6: 
          showGlobalActions();
          return true;
        }
        toggleSplitScreen();
        return true;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void resetLocked()
    {
      try
      {
        if (this.mServiceInterface != null) {
          this.mServiceInterface.init(null, this.mId, null);
        }
        if (this.mService != null)
        {
          this.mService.unlinkToDeath(this, 0);
          this.mService = null;
        }
        this.mServiceInterface = null;
        return;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;) {}
      }
    }
    
    public boolean resetMagnification(boolean paramBoolean)
    {
      synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
      {
        boolean bool = isCalledForCurrentUserLocked();
        if (!bool) {
          return false;
        }
        bool = AccessibilityManagerService.-get12(AccessibilityManagerService.this).canControlMagnification(this);
        if (!bool) {
          return false;
        }
        l = Binder.clearCallingIdentity();
      }
    }
    
    public void sendGesture(int paramInt, ParceledListSlice paramParceledListSlice)
    {
      for (;;)
      {
        synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
        {
          if (AccessibilityManagerService.-get12(AccessibilityManagerService.this).canPerformGestures(this))
          {
            long l1 = SystemClock.uptimeMillis() + 1000L;
            if (AccessibilityManagerService.-get10(AccessibilityManagerService.this) == null)
            {
              long l2 = SystemClock.uptimeMillis();
              if (l2 < l1)
              {
                try
                {
                  AccessibilityManagerService.-get8(AccessibilityManagerService.this).wait(l1 - SystemClock.uptimeMillis());
                }
                catch (InterruptedException localInterruptedException) {}
                continue;
              }
            }
            if (AccessibilityManagerService.-get10(AccessibilityManagerService.this) == null) {
              break label166;
            }
            paramParceledListSlice = GestureDescription.MotionEventGenerator.getMotionEventsFromGestureSteps(paramParceledListSlice.getList());
            if (((MotionEvent)paramParceledListSlice.get(paramParceledListSlice.size() - 1)).getAction() == 1)
            {
              AccessibilityManagerService.-get10(AccessibilityManagerService.this).injectEvents(paramParceledListSlice, this.mServiceInterface, paramInt);
              return;
            }
            Slog.e("AccessibilityManagerService", "Gesture is not well-formed");
          }
        }
        try
        {
          this.mServiceInterface.onPerformGestureResult(paramInt, false);
          return;
        }
        catch (RemoteException paramParceledListSlice)
        {
          label166:
          Slog.e("AccessibilityManagerService", "Error sending motion event injection failure to " + this.mServiceInterface, paramParceledListSlice);
        }
        Slog.e("AccessibilityManagerService", "MotionEventInjector installation timed out");
      }
      paramParceledListSlice = finally;
      throw paramParceledListSlice;
    }
    
    public void setDynamicallyConfigurableProperties(AccessibilityServiceInfo paramAccessibilityServiceInfo)
    {
      boolean bool2 = true;
      this.mEventTypes = paramAccessibilityServiceInfo.eventTypes;
      this.mFeedbackType = paramAccessibilityServiceInfo.feedbackType;
      String[] arrayOfString = paramAccessibilityServiceInfo.packageNames;
      if (arrayOfString != null) {
        this.mPackageNames.addAll(Arrays.asList(arrayOfString));
      }
      this.mNotificationTimeout = paramAccessibilityServiceInfo.notificationTimeout;
      if ((paramAccessibilityServiceInfo.flags & 0x1) != 0)
      {
        bool1 = true;
        this.mIsDefault = bool1;
        if ((this.mIsAutomation) || (paramAccessibilityServiceInfo.getResolveInfo().serviceInfo.applicationInfo.targetSdkVersion >= 16))
        {
          if ((paramAccessibilityServiceInfo.flags & 0x2) == 0) {
            break label207;
          }
          this.mFetchFlags |= 0x8;
        }
        label113:
        if ((paramAccessibilityServiceInfo.flags & 0x10) == 0) {
          break label221;
        }
        this.mFetchFlags |= 0x10;
        label134:
        if ((paramAccessibilityServiceInfo.flags & 0x4) == 0) {
          break label235;
        }
        bool1 = true;
        label145:
        this.mRequestTouchExplorationMode = bool1;
        if ((paramAccessibilityServiceInfo.flags & 0x8) == 0) {
          break label240;
        }
        bool1 = true;
        label162:
        this.mRequestEnhancedWebAccessibility = bool1;
        if ((paramAccessibilityServiceInfo.flags & 0x20) == 0) {
          break label245;
        }
        bool1 = true;
        label179:
        this.mRequestFilterKeyEvents = bool1;
        if ((paramAccessibilityServiceInfo.flags & 0x40) == 0) {
          break label250;
        }
      }
      label207:
      label221:
      label235:
      label240:
      label245:
      label250:
      for (boolean bool1 = bool2;; bool1 = false)
      {
        this.mRetrieveInteractiveWindows = bool1;
        return;
        bool1 = false;
        break;
        this.mFetchFlags &= 0xFFFFFFF7;
        break label113;
        this.mFetchFlags &= 0xFFFFFFEF;
        break label134;
        bool1 = false;
        break label145;
        bool1 = false;
        break label162;
        bool1 = false;
        break label179;
      }
    }
    
    public void setMagnificationCallbackEnabled(boolean paramBoolean)
    {
      this.mInvocationHandler.setMagnificationCallbackEnabled(paramBoolean);
    }
    
    public boolean setMagnificationScaleAndCenter(float paramFloat1, float paramFloat2, float paramFloat3, boolean paramBoolean)
    {
      synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
      {
        boolean bool = isCalledForCurrentUserLocked();
        if (!bool) {
          return false;
        }
        bool = AccessibilityManagerService.-get12(AccessibilityManagerService.this).canControlMagnification(this);
        if (!bool) {
          return false;
        }
        long l = Binder.clearCallingIdentity();
        try
        {
          MagnificationController localMagnificationController = AccessibilityManagerService.this.getMagnificationController();
          if (!localMagnificationController.isRegisteredLocked()) {
            localMagnificationController.register();
          }
          paramBoolean = localMagnificationController.setScaleAndCenter(paramFloat1, paramFloat2, paramFloat3, paramBoolean, this.mId);
          Binder.restoreCallingIdentity(l);
          return paramBoolean;
        }
        finally
        {
          Binder.restoreCallingIdentity(l);
        }
      }
    }
    
    public void setOnKeyEventResult(boolean paramBoolean, int paramInt)
    {
      AccessibilityManagerService.-wrap13(AccessibilityManagerService.this).setOnKeyEventResult(this, paramBoolean, paramInt);
    }
    
    /* Error */
    public void setServiceInfo(AccessibilityServiceInfo paramAccessibilityServiceInfo)
    {
      // Byte code:
      //   0: invokestatic 183	android/os/Binder:clearCallingIdentity	()J
      //   3: lstore_2
      //   4: aload_0
      //   5: getfield 92	com/android/server/accessibility/AccessibilityManagerService$Service:this$0	Lcom/android/server/accessibility/AccessibilityManagerService;
      //   8: invokestatic 287	com/android/server/accessibility/AccessibilityManagerService:-get8	(Lcom/android/server/accessibility/AccessibilityManagerService;)Ljava/lang/Object;
      //   11: astore 4
      //   13: aload 4
      //   15: monitorenter
      //   16: aload_0
      //   17: getfield 151	com/android/server/accessibility/AccessibilityManagerService$Service:mAccessibilityServiceInfo	Landroid/accessibilityservice/AccessibilityServiceInfo;
      //   20: astore 5
      //   22: aload 5
      //   24: ifnull +43 -> 67
      //   27: aload 5
      //   29: aload_1
      //   30: invokevirtual 974	android/accessibilityservice/AccessibilityServiceInfo:updateDynamicallyConfigurableProperties	(Landroid/accessibilityservice/AccessibilityServiceInfo;)V
      //   33: aload_0
      //   34: aload 5
      //   36: invokevirtual 211	com/android/server/accessibility/AccessibilityManagerService$Service:setDynamicallyConfigurableProperties	(Landroid/accessibilityservice/AccessibilityServiceInfo;)V
      //   39: aload_0
      //   40: getfield 92	com/android/server/accessibility/AccessibilityManagerService$Service:this$0	Lcom/android/server/accessibility/AccessibilityManagerService;
      //   43: aload_0
      //   44: getfield 131	com/android/server/accessibility/AccessibilityManagerService$Service:mUserId	I
      //   47: invokestatic 453	com/android/server/accessibility/AccessibilityManagerService:-wrap12	(Lcom/android/server/accessibility/AccessibilityManagerService;I)Lcom/android/server/accessibility/AccessibilityManagerService$UserState;
      //   50: astore_1
      //   51: aload_0
      //   52: getfield 92	com/android/server/accessibility/AccessibilityManagerService$Service:this$0	Lcom/android/server/accessibility/AccessibilityManagerService;
      //   55: aload_1
      //   56: invokestatic 538	com/android/server/accessibility/AccessibilityManagerService:-wrap18	(Lcom/android/server/accessibility/AccessibilityManagerService;Lcom/android/server/accessibility/AccessibilityManagerService$UserState;)V
      //   59: aload 4
      //   61: monitorexit
      //   62: lload_2
      //   63: invokestatic 207	android/os/Binder:restoreCallingIdentity	(J)V
      //   66: return
      //   67: aload_0
      //   68: aload_1
      //   69: invokevirtual 211	com/android/server/accessibility/AccessibilityManagerService$Service:setDynamicallyConfigurableProperties	(Landroid/accessibilityservice/AccessibilityServiceInfo;)V
      //   72: goto -33 -> 39
      //   75: astore_1
      //   76: aload 4
      //   78: monitorexit
      //   79: aload_1
      //   80: athrow
      //   81: astore_1
      //   82: lload_2
      //   83: invokestatic 207	android/os/Binder:restoreCallingIdentity	(J)V
      //   86: aload_1
      //   87: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	88	0	this	Service
      //   0	88	1	paramAccessibilityServiceInfo	AccessibilityServiceInfo
      //   3	80	2	l	long
      //   20	15	5	localAccessibilityServiceInfo	AccessibilityServiceInfo
      // Exception table:
      //   from	to	target	type
      //   16	22	75	finally
      //   27	39	75	finally
      //   39	59	75	finally
      //   67	72	75	finally
      //   4	16	81	finally
      //   59	62	81	finally
      //   76	81	81	finally
    }
    
    public void setSoftKeyboardCallbackEnabled(boolean paramBoolean)
    {
      this.mInvocationHandler.setSoftKeyboardCallbackEnabled(paramBoolean);
    }
    
    public boolean setSoftKeyboardShowMode(int paramInt)
    {
      for (;;)
      {
        AccessibilityManagerService.UserState localUserState;
        synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
        {
          boolean bool = isCalledForCurrentUserLocked();
          if (!bool) {
            return false;
          }
          localUserState = AccessibilityManagerService.-wrap11(AccessibilityManagerService.this);
          l = Binder.clearCallingIdentity();
          if (paramInt != 0) {}
        }
        ((AccessibilityManagerService.UserState)localObject3).mServiceChangingSoftKeyboardMode = this.mComponentName;
      }
    }
    
    public boolean unbindLocked()
    {
      AccessibilityManagerService.UserState localUserState = AccessibilityManagerService.-wrap12(AccessibilityManagerService.this, this.mUserId);
      AccessibilityManagerService.-wrap13(AccessibilityManagerService.this).flush(this);
      if (!this.mIsAutomation) {
        AccessibilityManagerService.-get2(AccessibilityManagerService.this).unbindService(this);
      }
      for (;;)
      {
        AccessibilityManagerService.-wrap21(AccessibilityManagerService.this, this, localUserState);
        resetLocked();
        return true;
        localUserState.destroyUiAutomationService();
      }
    }
    
    private final class InvocationHandler
      extends Handler
    {
      public static final int MSG_CLEAR_ACCESSIBILITY_CACHE = 2;
      public static final int MSG_ON_GESTURE = 1;
      private static final int MSG_ON_MAGNIFICATION_CHANGED = 5;
      private static final int MSG_ON_SOFT_KEYBOARD_STATE_CHANGED = 6;
      private boolean mIsMagnificationCallbackEnabled = false;
      private boolean mIsSoftKeyboardCallbackEnabled = false;
      
      public InvocationHandler(Looper paramLooper)
      {
        super(null, true);
      }
      
      public void handleMessage(Message paramMessage)
      {
        int i = paramMessage.what;
        switch (i)
        {
        case 3: 
        case 4: 
        default: 
          throw new IllegalArgumentException("Unknown message: " + i);
        case 1: 
          i = paramMessage.arg1;
          AccessibilityManagerService.Service.-wrap2(AccessibilityManagerService.Service.this, i);
          return;
        case 2: 
          AccessibilityManagerService.Service.-wrap1(AccessibilityManagerService.Service.this);
          return;
        case 5: 
          paramMessage = (SomeArgs)paramMessage.obj;
          Region localRegion = (Region)paramMessage.arg1;
          float f1 = ((Float)paramMessage.arg2).floatValue();
          float f2 = ((Float)paramMessage.arg3).floatValue();
          float f3 = ((Float)paramMessage.arg4).floatValue();
          AccessibilityManagerService.Service.-wrap3(AccessibilityManagerService.Service.this, localRegion, f1, f2, f3);
          return;
        }
        i = paramMessage.arg1;
        AccessibilityManagerService.Service.-wrap4(AccessibilityManagerService.Service.this, i);
      }
      
      public void notifyMagnificationChangedLocked(Region paramRegion, float paramFloat1, float paramFloat2, float paramFloat3)
      {
        if (!this.mIsMagnificationCallbackEnabled) {
          return;
        }
        SomeArgs localSomeArgs = SomeArgs.obtain();
        localSomeArgs.arg1 = paramRegion;
        localSomeArgs.arg2 = Float.valueOf(paramFloat1);
        localSomeArgs.arg3 = Float.valueOf(paramFloat2);
        localSomeArgs.arg4 = Float.valueOf(paramFloat3);
        obtainMessage(5, localSomeArgs).sendToTarget();
      }
      
      public void notifySoftKeyboardShowModeChangedLocked(int paramInt)
      {
        if (!this.mIsSoftKeyboardCallbackEnabled) {
          return;
        }
        obtainMessage(6, paramInt, 0).sendToTarget();
      }
      
      public void setMagnificationCallbackEnabled(boolean paramBoolean)
      {
        this.mIsMagnificationCallbackEnabled = paramBoolean;
      }
      
      public void setSoftKeyboardCallbackEnabled(boolean paramBoolean)
      {
        this.mIsSoftKeyboardCallbackEnabled = paramBoolean;
      }
    }
  }
  
  private class SettingsStringHelper
  {
    private static final String SETTINGS_DELIMITER = ":";
    private ContentResolver mContentResolver;
    private Set<String> mServices;
    private final String mSettingsName;
    private final int mUserId;
    
    public SettingsStringHelper(String paramString, int paramInt)
    {
      this.mUserId = paramInt;
      this.mSettingsName = paramString;
      this.mContentResolver = AccessibilityManagerService.-get2(AccessibilityManagerService.this).getContentResolver();
      paramString = Settings.Secure.getStringForUser(this.mContentResolver, this.mSettingsName, paramInt);
      this.mServices = new HashSet();
      if (!TextUtils.isEmpty(paramString))
      {
        this$1 = new TextUtils.SimpleStringSplitter(":".charAt(0));
        AccessibilityManagerService.this.setString(paramString);
        while (AccessibilityManagerService.this.hasNext())
        {
          paramString = AccessibilityManagerService.this.next();
          this.mServices.add(paramString);
        }
      }
    }
    
    public void addService(ComponentName paramComponentName)
    {
      this.mServices.add(paramComponentName.flattenToString());
    }
    
    public void deleteService(ComponentName paramComponentName)
    {
      this.mServices.remove(paramComponentName.flattenToString());
    }
    
    public void writeToSettings()
    {
      Settings.Secure.putStringForUser(this.mContentResolver, this.mSettingsName, TextUtils.join(":", this.mServices), this.mUserId);
    }
  }
  
  private class UserState
  {
    public boolean mAccessibilityFocusOnlyInActiveWindow;
    public final Set<ComponentName> mBindingServices = new HashSet();
    public final CopyOnWriteArrayList<AccessibilityManagerService.Service> mBoundServices = new CopyOnWriteArrayList();
    public final RemoteCallbackList<IAccessibilityManagerClient> mClients = new RemoteCallbackList();
    public final Map<ComponentName, AccessibilityManagerService.Service> mComponentNameToServiceMap = new HashMap();
    public final Set<ComponentName> mEnabledServices = new HashSet();
    public final List<AccessibilityServiceInfo> mInstalledServices = new ArrayList();
    public final SparseArray<AccessibilityManagerService.AccessibilityConnectionWrapper> mInteractionConnections = new SparseArray();
    public boolean mIsAutoclickEnabled;
    public boolean mIsDisplayMagnificationEnabled;
    public boolean mIsEnhancedWebAccessibilityEnabled;
    public boolean mIsFilterKeyEventsEnabled;
    public boolean mIsPerformGesturesEnabled;
    public boolean mIsTextHighContrastEnabled;
    public boolean mIsTouchExplorationEnabled;
    public int mLastSentClientState = -1;
    public ComponentName mServiceChangingSoftKeyboardMode;
    public int mSoftKeyboardShowMode = 0;
    public final Set<ComponentName> mTouchExplorationGrantedServices = new HashSet();
    private int mUiAutomationFlags;
    private final IBinder.DeathRecipient mUiAutomationSerivceOnwerDeathRecipient = new IBinder.DeathRecipient()
    {
      public void binderDied()
      {
        AccessibilityManagerService.UserState.-get3(AccessibilityManagerService.UserState.this).unlinkToDeath(AccessibilityManagerService.UserState.-get0(AccessibilityManagerService.UserState.this), 0);
        AccessibilityManagerService.UserState.-set3(AccessibilityManagerService.UserState.this, null);
        if (AccessibilityManagerService.UserState.-get1(AccessibilityManagerService.UserState.this) != null) {
          AccessibilityManagerService.UserState.-get1(AccessibilityManagerService.UserState.this).binderDied();
        }
      }
    };
    private AccessibilityManagerService.Service mUiAutomationService;
    private IAccessibilityServiceClient mUiAutomationServiceClient;
    private IBinder mUiAutomationServiceOwner;
    public final int mUserId;
    public final SparseArray<IBinder> mWindowTokens = new SparseArray();
    
    public UserState(int paramInt)
    {
      this.mUserId = paramInt;
    }
    
    public void destroyUiAutomationService()
    {
      this.mUiAutomationService = null;
      this.mUiAutomationFlags = 0;
      this.mUiAutomationServiceClient = null;
      if (this.mUiAutomationServiceOwner != null)
      {
        this.mUiAutomationServiceOwner.unlinkToDeath(this.mUiAutomationSerivceOnwerDeathRecipient, 0);
        this.mUiAutomationServiceOwner = null;
      }
    }
    
    public int getClientState()
    {
      int j = 0;
      if (isHandlingAccessibilityEvents()) {
        j = 1;
      }
      int i = j;
      if (isHandlingAccessibilityEvents())
      {
        i = j;
        if (this.mIsTouchExplorationEnabled) {
          i = j | 0x2;
        }
      }
      j = i;
      if (this.mIsTextHighContrastEnabled) {
        j = i | 0x4;
      }
      return j;
    }
    
    public boolean isHandlingAccessibilityEvents()
    {
      boolean bool2 = true;
      boolean bool1 = bool2;
      if (this.mBoundServices.isEmpty())
      {
        bool1 = bool2;
        if (this.mBindingServices.isEmpty()) {
          bool1 = false;
        }
      }
      return bool1;
    }
    
    boolean isUiAutomationSuppressingOtherServices()
    {
      boolean bool2 = false;
      boolean bool1 = bool2;
      if (this.mUiAutomationService != null)
      {
        bool1 = bool2;
        if ((this.mUiAutomationFlags & 0x1) == 0) {
          bool1 = true;
        }
      }
      return bool1;
    }
    
    public void onSwitchToAnotherUser()
    {
      if (this.mUiAutomationService != null) {
        this.mUiAutomationService.binderDied();
      }
      AccessibilityManagerService.-wrap25(AccessibilityManagerService.this, this);
      this.mBoundServices.clear();
      this.mBindingServices.clear();
      this.mLastSentClientState = -1;
      this.mEnabledServices.clear();
      this.mTouchExplorationGrantedServices.clear();
      this.mIsTouchExplorationEnabled = false;
      this.mIsEnhancedWebAccessibilityEnabled = false;
      this.mIsDisplayMagnificationEnabled = false;
      this.mIsAutoclickEnabled = false;
      this.mSoftKeyboardShowMode = 0;
    }
  }
  
  final class WindowsForAccessibilityCallback
    implements WindowManagerInternal.WindowsForAccessibilityCallback
  {
    WindowsForAccessibilityCallback() {}
    
    private int getTypeForWindowManagerWindowType(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return -1;
      case 1: 
      case 2: 
      case 3: 
      case 4: 
      case 1000: 
      case 1001: 
      case 1002: 
      case 1003: 
      case 1005: 
      case 2002: 
      case 2005: 
      case 2007: 
        return 1;
      case 2011: 
      case 2012: 
        return 2;
      case 2000: 
      case 2001: 
      case 2003: 
      case 2006: 
      case 2008: 
      case 2009: 
      case 2010: 
      case 2014: 
      case 2017: 
      case 2019: 
      case 2020: 
      case 2024: 
      case 2036: 
        return 3;
      case 2034: 
        return 5;
      }
      return 4;
    }
    
    private AccessibilityWindowInfo populateReportedWindow(WindowInfo paramWindowInfo)
    {
      int i = AccessibilityManagerService.-wrap14(AccessibilityManagerService.this, paramWindowInfo.token);
      if (i < 0) {
        return null;
      }
      AccessibilityWindowInfo localAccessibilityWindowInfo = AccessibilityWindowInfo.obtain();
      localAccessibilityWindowInfo.setId(i);
      localAccessibilityWindowInfo.setType(getTypeForWindowManagerWindowType(paramWindowInfo.type));
      localAccessibilityWindowInfo.setLayer(paramWindowInfo.layer);
      localAccessibilityWindowInfo.setFocused(paramWindowInfo.focused);
      localAccessibilityWindowInfo.setBoundsInScreen(paramWindowInfo.boundsInScreen);
      localAccessibilityWindowInfo.setTitle(paramWindowInfo.title);
      localAccessibilityWindowInfo.setAnchorId(paramWindowInfo.accessibilityIdOfAnchor);
      i = AccessibilityManagerService.-wrap14(AccessibilityManagerService.this, paramWindowInfo.parentToken);
      if (i >= 0) {
        localAccessibilityWindowInfo.setParentId(i);
      }
      if (paramWindowInfo.childTokens != null)
      {
        int j = paramWindowInfo.childTokens.size();
        i = 0;
        while (i < j)
        {
          IBinder localIBinder = (IBinder)paramWindowInfo.childTokens.get(i);
          int k = AccessibilityManagerService.-wrap14(AccessibilityManagerService.this, localIBinder);
          if (k >= 0) {
            localAccessibilityWindowInfo.addChild(k);
          }
          i += 1;
        }
      }
      return localAccessibilityWindowInfo;
    }
    
    public void onWindowsForAccessibilityChanged(List<WindowInfo> paramList)
    {
      for (;;)
      {
        int i;
        synchronized (AccessibilityManagerService.-get8(AccessibilityManagerService.this))
        {
          ArrayList localArrayList = new ArrayList();
          int j = paramList.size();
          i = 0;
          if (i < j)
          {
            AccessibilityWindowInfo localAccessibilityWindowInfo = populateReportedWindow((WindowInfo)paramList.get(i));
            if (localAccessibilityWindowInfo != null) {
              localArrayList.add(localAccessibilityWindowInfo);
            }
          }
          else
          {
            AccessibilityManagerService.-get12(AccessibilityManagerService.this).updateWindowsLocked(localArrayList);
            AccessibilityManagerService.-get8(AccessibilityManagerService.this).notifyAll();
            return;
          }
        }
        i += 1;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/accessibility/AccessibilityManagerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */