package com.android.server.wm;

import android.app.ActivityManager;
import android.app.ActivityManager.StackId;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Region.Op;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UserHandle;
import android.os.WorkSource;
import android.util.ArraySet;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TimeUtils;
import android.view.DisplayInfo;
import android.view.Gravity;
import android.view.IApplicationToken;
import android.view.IWindow;
import android.view.IWindow.Stub;
import android.view.IWindowFocusObserver;
import android.view.IWindowId;
import android.view.IWindowId.Stub;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.InputEventReceiver;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManagerPolicy;
import android.view.WindowManagerPolicy.WindowState;
import com.android.server.am.OnePlusProcessManager;
import com.android.server.input.InputManagerService;
import com.android.server.input.InputWindowHandle;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

final class WindowState
  implements WindowManagerPolicy.WindowState
{
  static final boolean DEBUG_DISABLE_SAVING_SURFACES = false;
  static final int MINIMUM_VISIBLE_HEIGHT_IN_DP = 32;
  static final int MINIMUM_VISIBLE_WIDTH_IN_DP = 48;
  static final int RESIZE_HANDLE_WIDTH_IN_DP = 30;
  static final String TAG = "WindowManager";
  static final Region sEmptyRegion = new Region();
  private static final Rect sTmpRect = new Rect();
  boolean mAnimateReplacingWindow = false;
  boolean mAnimatingExit;
  private boolean mAnimatingWithSavedSurface;
  boolean mAppDied;
  boolean mAppFreezing;
  final int mAppOp;
  boolean mAppOpVisibility = true;
  AppWindowToken mAppToken;
  boolean mAttachedHidden;
  final WindowState mAttachedWindow;
  final WindowManager.LayoutParams mAttrs = new WindowManager.LayoutParams();
  final int mBaseLayer;
  final WindowList mChildWindows = new WindowList();
  final IWindow mClient;
  InputChannel mClientChannel;
  final Rect mCompatFrame = new Rect();
  private boolean mConfigHasChanged;
  final Rect mContainingFrame = new Rect();
  boolean mContentChanged;
  final Rect mContentFrame = new Rect();
  final Rect mContentInsets = new Rect();
  boolean mContentInsetsChanged;
  final Context mContext;
  private DeadWindowEventReceiver mDeadWindowEventReceiver;
  final DeathRecipient mDeathRecipient;
  final Rect mDecorFrame = new Rect();
  boolean mDestroying;
  DisplayContent mDisplayContent;
  final Rect mDisplayFrame = new Rect();
  boolean mDragResizing;
  boolean mDragResizingChangeReported;
  PowerManager.WakeLock mDrawLock;
  boolean mEnforceSizeCompat;
  RemoteCallbackList<IWindowFocusObserver> mFocusCallbacks;
  final Rect mFrame = new Rect();
  boolean mFrameSizeChanged = false;
  final Rect mGivenContentInsets = new Rect();
  boolean mGivenInsetsPending;
  final Region mGivenTouchableRegion = new Region();
  final Rect mGivenVisibleInsets = new Rect();
  float mGlobalScale = 1.0F;
  float mHScale = 1.0F;
  boolean mHasSurface = false;
  boolean mHaveFrame;
  boolean mInRelayout;
  InputChannel mInputChannel;
  final InputWindowHandle mInputWindowHandle;
  final Rect mInsetFrame = new Rect();
  float mInvGlobalScale = 1.0F;
  final boolean mIsFloatingLayer;
  final boolean mIsImWindow;
  final boolean mIsWallpaper;
  private boolean mJustMovedInStack;
  final Rect mLastContentInsets = new Rect();
  final Rect mLastFrame = new Rect();
  int mLastFreezeDuration;
  float mLastHScale = 1.0F;
  final Rect mLastOutsets = new Rect();
  final Rect mLastOverscanInsets = new Rect();
  int mLastRequestedHeight;
  int mLastRequestedWidth;
  final Rect mLastStableInsets = new Rect();
  CharSequence mLastTitle;
  float mLastVScale = 1.0F;
  final Rect mLastVisibleInsets = new Rect();
  int mLastVisibleLayoutRotation = -1;
  int mLayer;
  final boolean mLayoutAttached;
  boolean mLayoutNeeded;
  int mLayoutSeq = -1;
  private Configuration mMergedConfiguration = new Configuration();
  boolean mMovedByResize;
  boolean mNotOnAppsDisplay = false;
  boolean mObscured;
  boolean mOrientationChanging;
  final Rect mOutsetFrame = new Rect();
  final Rect mOutsets = new Rect();
  boolean mOutsetsChanged = false;
  final Rect mOverscanFrame = new Rect();
  final Rect mOverscanInsets = new Rect();
  boolean mOverscanInsetsChanged;
  final int mOwnerUid;
  final Rect mParentFrame = new Rect();
  boolean mPermanentlyHidden;
  final WindowManagerPolicy mPolicy;
  boolean mPolicyVisibility = true;
  boolean mPolicyVisibilityAfterAnim = true;
  boolean mRebuilding;
  boolean mRelayoutCalled;
  boolean mRemoveOnExit;
  boolean mRemoved;
  boolean mReplacingRemoveRequested = false;
  WindowState mReplacingWindow = null;
  int mRequestedHeight;
  int mRequestedWidth;
  int mResizeMode;
  boolean mResizedWhileGone = false;
  private boolean mResizedWhileNotDragResizing;
  private boolean mResizedWhileNotDragResizingReported;
  WindowToken mRootToken;
  boolean mSeamlesslyRotated = false;
  int mSeq;
  final WindowManagerService mService;
  final Session mSession;
  private boolean mShowToOwnerOnly;
  final Point mShownPosition = new Point();
  boolean mSkipEnterAnimationForSeamlessReplacement = false;
  final Rect mStableFrame = new Rect();
  final Rect mStableInsets = new Rect();
  boolean mStableInsetsChanged;
  String mStringNameCache;
  final int mSubLayer;
  private boolean mSurfaceSaved = false;
  int mSystemUiVisibility;
  AppWindowToken mTargetAppToken;
  private final Configuration mTmpConfig = new Configuration();
  final Matrix mTmpMatrix = new Matrix();
  private final Rect mTmpRect = new Rect();
  WindowToken mToken;
  int mTouchableInsets = 0;
  boolean mTurnOnScreen;
  float mVScale = 1.0F;
  int mViewVisibility;
  final Rect mVisibleFrame = new Rect();
  final Rect mVisibleInsets = new Rect();
  boolean mVisibleInsetsChanged;
  int mWallpaperDisplayOffsetX = Integer.MIN_VALUE;
  int mWallpaperDisplayOffsetY = Integer.MIN_VALUE;
  boolean mWallpaperVisible;
  float mWallpaperX = -1.0F;
  float mWallpaperXStep = -1.0F;
  float mWallpaperY = -1.0F;
  float mWallpaperYStep = -1.0F;
  boolean mWasExiting;
  boolean mWasVisibleBeforeClientHidden;
  boolean mWillReplaceWindow = false;
  final WindowStateAnimator mWinAnimator;
  final IWindowId mWindowId;
  boolean mWindowRemovalAllowed;
  int mXOffset;
  int mYOffset;
  
  WindowState(WindowManagerService paramWindowManagerService, Session paramSession, IWindow paramIWindow, WindowToken paramWindowToken, WindowState paramWindowState, int paramInt1, int paramInt2, WindowManager.LayoutParams paramLayoutParams, int paramInt3, DisplayContent paramDisplayContent)
  {
    this.mService = paramWindowManagerService;
    this.mSession = paramSession;
    this.mClient = paramIWindow;
    this.mAppOp = paramInt1;
    this.mToken = paramWindowToken;
    this.mOwnerUid = paramSession.mUid;
    this.mWindowId = new IWindowId.Stub()
    {
      public boolean isFocused()
      {
        return WindowState.this.isFocused();
      }
      
      public void registerFocusObserver(IWindowFocusObserver paramAnonymousIWindowFocusObserver)
      {
        WindowState.this.registerFocusObserver(paramAnonymousIWindowFocusObserver);
      }
      
      public void unregisterFocusObserver(IWindowFocusObserver paramAnonymousIWindowFocusObserver)
      {
        WindowState.this.unregisterFocusObserver(paramAnonymousIWindowFocusObserver);
      }
    };
    this.mAttrs.copyFrom(paramLayoutParams);
    this.mViewVisibility = paramInt3;
    this.mDisplayContent = paramDisplayContent;
    this.mPolicy = this.mService.mPolicy;
    this.mContext = this.mService.mContext;
    paramWindowManagerService = new DeathRecipient(null);
    this.mSeq = paramInt2;
    if ((this.mAttrs.privateFlags & 0x80) != 0)
    {
      bool = true;
      this.mEnforceSizeCompat = bool;
      if (WindowManagerService.localLOGV) {
        Slog.v(TAG, "Window " + this + " client=" + paramIWindow.asBinder() + " token=" + paramWindowToken + " (" + this.mAttrs.token + ")" + " params=" + paramLayoutParams);
      }
    }
    for (;;)
    {
      int i;
      try
      {
        paramIWindow.asBinder().linkToDeath(paramWindowManagerService, 0);
        this.mDeathRecipient = paramWindowManagerService;
        if ((this.mAttrs.type < 1000) || (this.mAttrs.type > 1999)) {
          break label1212;
        }
        this.mBaseLayer = (this.mPolicy.windowTypeToLayerLw(paramWindowState.mAttrs.type) * 10000 + 1000);
        this.mSubLayer = this.mPolicy.subWindowTypeToLayerLw(paramLayoutParams.type);
        this.mAttachedWindow = paramWindowState;
        if (WindowManagerDebugConfig.DEBUG_ADD_REMOVE) {
          Slog.v(TAG, "Adding " + this + " to " + this.mAttachedWindow);
        }
        paramWindowManagerService = this.mAttachedWindow.mChildWindows;
        i = paramWindowManagerService.size();
        if (i == 0)
        {
          paramWindowManagerService.add(this);
          if (this.mAttrs.type == 1003) {
            break label1182;
          }
          bool = true;
          this.mLayoutAttached = bool;
          if (paramWindowState.mAttrs.type == 2011) {
            break label1188;
          }
          if (paramWindowState.mAttrs.type != 2012) {
            break label1194;
          }
          bool = true;
          this.mIsImWindow = bool;
          if (paramWindowState.mAttrs.type != 2013) {
            break label1200;
          }
          bool = true;
          this.mIsWallpaper = bool;
          if (this.mIsImWindow) {
            break label1206;
          }
          bool = this.mIsWallpaper;
          this.mIsFloatingLayer = bool;
          paramWindowManagerService = this;
          if (!paramWindowManagerService.isChildWindow()) {
            break label1356;
          }
          paramWindowManagerService = paramWindowManagerService.mAttachedWindow;
          continue;
          bool = false;
        }
      }
      catch (RemoteException paramWindowManagerService)
      {
        this.mDeathRecipient = null;
        this.mAttachedWindow = null;
        this.mLayoutAttached = false;
        this.mIsImWindow = false;
        this.mIsWallpaper = false;
        this.mIsFloatingLayer = false;
        this.mBaseLayer = 0;
        this.mSubLayer = 0;
        this.mInputWindowHandle = null;
        this.mWinAnimator = null;
        return;
      }
      paramInt3 = 0;
      paramInt1 = 0;
      for (;;)
      {
        paramInt2 = paramInt3;
        if (paramInt1 < i)
        {
          paramInt2 = ((WindowState)paramWindowManagerService.get(paramInt1)).mSubLayer;
          if ((this.mSubLayer < paramInt2) || ((this.mSubLayer == paramInt2) && (paramInt2 < 0)))
          {
            paramWindowManagerService.add(paramInt1, this);
            paramInt2 = 1;
          }
        }
        else
        {
          if (paramInt2 != 0) {
            break;
          }
          paramWindowManagerService.add(this);
          break;
        }
        paramInt1 += 1;
      }
      label1182:
      bool = false;
      continue;
      label1188:
      bool = true;
      continue;
      label1194:
      bool = false;
      continue;
      label1200:
      bool = false;
      continue;
      label1206:
      bool = true;
    }
    label1212:
    this.mBaseLayer = (this.mPolicy.windowTypeToLayerLw(paramLayoutParams.type) * 10000 + 1000);
    this.mSubLayer = 0;
    this.mAttachedWindow = null;
    this.mLayoutAttached = false;
    if (this.mAttrs.type != 2011)
    {
      if (this.mAttrs.type != 2012) {
        break label1338;
      }
      bool = true;
      label1282:
      this.mIsImWindow = bool;
      if (this.mAttrs.type != 2013) {
        break label1344;
      }
      bool = true;
      label1304:
      this.mIsWallpaper = bool;
      if (this.mIsImWindow) {
        break label1350;
      }
    }
    label1338:
    label1344:
    label1350:
    for (boolean bool = this.mIsWallpaper;; bool = true)
    {
      this.mIsFloatingLayer = bool;
      break;
      bool = true;
      break label1282;
      bool = false;
      break label1282;
      bool = false;
      break label1304;
    }
    label1356:
    paramWindowManagerService = paramWindowManagerService.mToken;
    if (paramWindowManagerService.appWindowToken == null)
    {
      paramSession = (WindowToken)this.mService.mTokenMap.get(paramWindowManagerService.token);
      if ((paramSession != null) && (paramWindowManagerService != paramSession)) {}
    }
    else
    {
      this.mRootToken = paramWindowManagerService;
      this.mAppToken = paramWindowManagerService.appWindowToken;
      if (this.mAppToken != null)
      {
        if (paramDisplayContent == getDisplayContent()) {
          break label1558;
        }
        bool = true;
        label1427:
        this.mNotOnAppsDisplay = bool;
        if (this.mAppToken.showForAllUsers)
        {
          paramWindowManagerService = this.mAttrs;
          paramWindowManagerService.flags |= 0x80000;
        }
      }
      this.mWinAnimator = new WindowStateAnimator(this);
      this.mWinAnimator.mAlpha = paramLayoutParams.alpha;
      this.mRequestedWidth = 0;
      this.mRequestedHeight = 0;
      this.mLastRequestedWidth = 0;
      this.mLastRequestedHeight = 0;
      this.mXOffset = 0;
      this.mYOffset = 0;
      this.mLayer = 0;
      if (this.mAppToken == null) {
        break label1564;
      }
    }
    label1558:
    label1564:
    for (paramWindowManagerService = this.mAppToken.mInputApplicationHandle;; paramWindowManagerService = null)
    {
      this.mInputWindowHandle = new InputWindowHandle(paramWindowManagerService, this, paramDisplayContent.getDisplayId());
      return;
      paramWindowManagerService = paramSession;
      break;
      bool = false;
      break label1427;
    }
  }
  
  private static void applyInsets(Region paramRegion, Rect paramRect1, Rect paramRect2)
  {
    paramRegion.set(paramRect1.left + paramRect2.left, paramRect1.top + paramRect2.top, paramRect1.right - paramRect2.right, paramRect1.bottom - paramRect2.bottom);
  }
  
  private void dispatchResized(Rect paramRect1, Rect paramRect2, Rect paramRect3, Rect paramRect4, Rect paramRect5, Rect paramRect6, boolean paramBoolean, Configuration paramConfiguration)
    throws RemoteException
  {
    if (!isDragResizeChanged()) {}
    for (boolean bool = this.mResizedWhileNotDragResizing;; bool = true)
    {
      this.mClient.resized(paramRect1, paramRect2, paramRect3, paramRect4, paramRect5, paramRect6, paramBoolean, paramConfiguration, getBackdropFrame(paramRect1), bool, this.mPolicy.isNavBarForcedShownLw(this));
      this.mDragResizingChangeReported = true;
      return;
    }
  }
  
  private void getMergedConfig(Configuration paramConfiguration)
  {
    if ((this.mAppToken != null) && (this.mAppToken.mFrozenMergedConfig.size() > 0))
    {
      paramConfiguration.setTo((Configuration)this.mAppToken.mFrozenMergedConfig.peek());
      return;
    }
    Object localObject = getTask();
    if (localObject != null) {}
    for (localObject = ((Task)localObject).mOverrideConfig;; localObject = Configuration.EMPTY)
    {
      paramConfiguration.setTo(this.mService.mCurConfiguration);
      if (localObject != Configuration.EMPTY) {
        paramConfiguration.updateFrom((Configuration)localObject);
      }
      return;
    }
  }
  
  private boolean isVisibleUnchecked()
  {
    if ((!this.mHasSurface) || (!this.mPolicyVisibility) || (this.mAttachedHidden)) {}
    while ((this.mAnimatingExit) || (this.mDestroying)) {
      return false;
    }
    if (this.mIsWallpaper) {
      return this.mWallpaperVisible;
    }
    return true;
  }
  
  private boolean shouldSaveSurface()
  {
    if (this.mWinAnimator.mSurfaceController == null) {
      return false;
    }
    if (!this.mWasVisibleBeforeClientHidden) {
      return false;
    }
    if ((this.mAttrs.flags & 0x2000) != 0) {
      return false;
    }
    if (ActivityManager.isLowRamDeviceStatic()) {
      return false;
    }
    Object localObject = getTask();
    if ((localObject == null) || (((Task)localObject).inHomeStack())) {
      return false;
    }
    localObject = ((Task)localObject).getTopVisibleAppToken();
    if ((localObject != null) && (localObject != this.mAppToken)) {
      return false;
    }
    if (this.mResizedWhileGone) {
      return false;
    }
    if (SystemProperties.getBoolean("persist.sys.disableSaveSurface", false)) {
      return false;
    }
    return this.mAppToken.shouldSaveSurface();
  }
  
  private void subtractInsets(Rect paramRect1, Rect paramRect2, Rect paramRect3, Rect paramRect4)
  {
    paramRect1.inset(Math.max(0, paramRect3.left - Math.max(paramRect2.left, paramRect4.left)), Math.max(0, paramRect3.top - Math.max(paramRect2.top, paramRect4.top)), Math.max(0, Math.min(paramRect2.right, paramRect4.right) - paramRect3.right), Math.max(0, Math.min(paramRect2.bottom, paramRect4.bottom) - paramRect3.bottom));
  }
  
  private Configuration updateConfiguration()
  {
    boolean bool = isConfigChanged();
    getMergedConfig(this.mMergedConfiguration);
    this.mConfigHasChanged = false;
    if (((WindowManagerDebugConfig.DEBUG_RESIZE) || (WindowManagerDebugConfig.DEBUG_ORIENTATION) || (WindowManagerDebugConfig.DEBUG_CONFIGURATION) || (WindowManagerDebugConfig.DEBUG_ONEPLUS)) && (bool)) {
      Slog.i(TAG, "Sending new config to window " + this + ": " + " / mergedConfig=" + this.mMergedConfiguration);
    }
    return this.mMergedConfiguration;
  }
  
  void adjustStartingWindowFlags()
  {
    if ((this.mAttrs.type == 1) && (this.mAppToken != null) && (this.mAppToken.startingWindow != null))
    {
      WindowManager.LayoutParams localLayoutParams = this.mAppToken.startingWindow.mAttrs;
      localLayoutParams.flags = (localLayoutParams.flags & 0xFFB7FFFE | this.mAttrs.flags & 0x480001);
    }
  }
  
  void applyAdjustForImeIfNeeded()
  {
    Task localTask = getTask();
    if ((localTask != null) && (localTask.mStack != null) && (localTask.mStack.isAdjustedForIme())) {
      localTask.mStack.applyAdjustForImeIfNeeded(localTask);
    }
  }
  
  void applyDimLayerIfNeeded()
  {
    AppWindowToken localAppWindowToken = this.mAppToken;
    if ((localAppWindowToken != null) && (localAppWindowToken.removed)) {
      return;
    }
    if ((!this.mAnimatingExit) && (this.mAppDied)) {
      this.mDisplayContent.mDimLayerController.applyDimAbove(getDimLayerUser(), this.mWinAnimator);
    }
    while (((this.mAttrs.flags & 0x2) == 0) || (this.mDisplayContent == null) || (this.mAnimatingExit) || (!isVisibleUnchecked())) {
      return;
    }
    this.mDisplayContent.mDimLayerController.applyDimBehind(getDimLayerUser(), this.mWinAnimator);
  }
  
  void applyGravityAndUpdateFrame(Rect paramRect1, Rect paramRect2)
  {
    int m = paramRect1.width();
    int n = paramRect1.height();
    Task localTask = getTask();
    boolean bool = isInMultiWindowMode();
    int i;
    int k;
    label66:
    int j;
    label94:
    label108:
    float f1;
    float f2;
    label144:
    int i1;
    int i2;
    if ((this.mAttrs.flags & 0x200) != 0)
    {
      i = 1;
      if ((localTask == null) || (!bool)) {
        break label276;
      }
      if ((isChildWindow()) && (i == 0)) {
        break label282;
      }
      k = 0;
      if ((this.mAttrs.flags & 0x4000) == 0) {
        break label372;
      }
      if (this.mAttrs.width >= 0) {
        break label288;
      }
      j = m;
      if (this.mAttrs.height >= 0) {
        break label330;
      }
      i = n;
      if (!this.mEnforceSizeCompat) {
        break label501;
      }
      f1 = this.mAttrs.x * this.mGlobalScale;
      f2 = this.mAttrs.y * this.mGlobalScale;
      i1 = j;
      i2 = i;
      if (bool)
      {
        if (!layoutInParentFrame()) {
          break label523;
        }
        i2 = i;
        i1 = j;
      }
    }
    for (;;)
    {
      Gravity.apply(this.mAttrs.gravity, i1, i2, paramRect1, (int)(this.mAttrs.horizontalMargin * m + f1), (int)(this.mAttrs.verticalMargin * n + f2), this.mFrame);
      if (k != 0) {
        Gravity.applyDisplay(this.mAttrs.gravity, paramRect2, this.mFrame);
      }
      this.mCompatFrame.set(this.mFrame);
      if (this.mEnforceSizeCompat) {
        this.mCompatFrame.scale(this.mInvGlobalScale);
      }
      return;
      i = 0;
      break;
      label276:
      k = 1;
      break label66;
      label282:
      k = 1;
      break label66;
      label288:
      if (this.mEnforceSizeCompat)
      {
        j = (int)(this.mAttrs.width * this.mGlobalScale + 0.5F);
        break label94;
      }
      j = this.mAttrs.width;
      break label94;
      label330:
      if (this.mEnforceSizeCompat)
      {
        i = (int)(this.mAttrs.height * this.mGlobalScale + 0.5F);
        break label108;
      }
      i = this.mAttrs.height;
      break label108;
      label372:
      if (this.mAttrs.width == -1) {
        i = m;
      }
      for (;;)
      {
        if (this.mAttrs.height != -1) {
          break label449;
        }
        i1 = n;
        j = i;
        i = i1;
        break;
        if (this.mEnforceSizeCompat) {
          i = (int)(this.mRequestedWidth * this.mGlobalScale + 0.5F);
        } else {
          i = this.mRequestedWidth;
        }
      }
      label449:
      if (this.mEnforceSizeCompat)
      {
        i1 = (int)(this.mRequestedHeight * this.mGlobalScale + 0.5F);
        j = i;
        i = i1;
        break label108;
      }
      i1 = this.mRequestedHeight;
      j = i;
      i = i1;
      break label108;
      label501:
      f1 = this.mAttrs.x;
      f2 = this.mAttrs.y;
      break label144;
      label523:
      i1 = Math.min(j, m);
      i2 = Math.min(i, n);
    }
  }
  
  void applyScrollIfNeeded()
  {
    Task localTask = getTask();
    if (localTask != null) {
      localTask.applyScrollToWindowIfNeeded(this);
    }
  }
  
  void attach()
  {
    if (WindowManagerService.localLOGV) {
      Slog.v(TAG, "Attaching " + this + " token=" + this.mToken + ", list=" + this.mToken.windows);
    }
    this.mSession.windowAddedLocked();
  }
  
  boolean canReceiveKeys()
  {
    if ((!isVisibleOrAdding()) || (this.mViewVisibility != 0) || (this.mRemoveOnExit)) {}
    while (((this.mAttrs.flags & 0x8) != 0) || ((this.mAppToken != null) && (!this.mAppToken.windowsAreFocusable())) || (isAdjustedForMinimizedDock())) {
      return false;
    }
    return true;
  }
  
  boolean canRestoreSurface()
  {
    if (this.mWasVisibleBeforeClientHidden) {
      return this.mSurfaceSaved;
    }
    return false;
  }
  
  void checkPolicyVisibilityChange()
  {
    if (this.mPolicyVisibility != this.mPolicyVisibilityAfterAnim)
    {
      if (WindowManagerDebugConfig.DEBUG_VISIBILITY) {
        Slog.v(TAG, "Policy visibility changing after anim in " + this.mWinAnimator + ": " + this.mPolicyVisibilityAfterAnim);
      }
      this.mPolicyVisibility = this.mPolicyVisibilityAfterAnim;
      setDisplayLayoutNeeded();
      if (!this.mPolicyVisibility)
      {
        if (this.mService.mCurrentFocus == this)
        {
          if (WindowManagerDebugConfig.DEBUG_FOCUS_LIGHT) {
            Slog.i(TAG, "setAnimationLocked: setting mFocusMayChange true");
          }
          this.mService.mFocusMayChange = true;
        }
        this.mService.enableScreenIfNeededLocked();
      }
    }
  }
  
  boolean clearAnimatingWithSavedSurface()
  {
    if (this.mAnimatingWithSavedSurface)
    {
      if (WindowManagerDebugConfig.DEBUG_ANIM) {
        Slog.d(TAG, "clearAnimatingWithSavedSurface(): win=" + this);
      }
      this.mAnimatingWithSavedSurface = false;
      return true;
    }
    return false;
  }
  
  void clearHasSavedSurface()
  {
    this.mSurfaceSaved = false;
    this.mAnimatingWithSavedSurface = false;
    if (this.mWasVisibleBeforeClientHidden) {
      this.mAppToken.destroySavedSurfaces();
    }
  }
  
  public void clearVisibleBeforeClientHidden()
  {
    this.mWasVisibleBeforeClientHidden = false;
  }
  
  boolean computeDragResizing()
  {
    Task localTask = getTask();
    if (localTask == null) {
      return false;
    }
    if ((this.mAttrs.width != -1) || (this.mAttrs.height != -1)) {
      return false;
    }
    if (localTask.isDragResizing()) {
      return true;
    }
    if ((!this.mDisplayContent.mDividerControllerLocked.isResizing()) && ((this.mAppToken == null) || (this.mAppToken.mFrozenBounds.isEmpty()))) {}
    while ((localTask.inFreeformWorkspace()) || (isGoneForLayoutLw())) {
      return false;
    }
    return true;
  }
  
  public void computeFrameLw(Rect paramRect1, Rect paramRect2, Rect paramRect3, Rect paramRect4, Rect paramRect5, Rect paramRect6, Rect paramRect7, Rect paramRect8)
  {
    Object localObject;
    boolean bool;
    label54:
    label66:
    int i;
    int j;
    int i2;
    int i3;
    int k;
    label240:
    int i4;
    int i5;
    if ((!this.mWillReplaceWindow) || ((!this.mAnimatingExit) && (this.mReplacingRemoveRequested)))
    {
      this.mHaveFrame = true;
      localObject = getTask();
      if (!isInMultiWindowMode()) {
        break label939;
      }
      m = 0;
      if (localObject == null) {
        break label945;
      }
      bool = ((Task)localObject).isFloating();
      if (m == 0) {
        break label951;
      }
      this.mInsetFrame.setEmpty();
      if ((m == 0) && (!layoutInParentFrame())) {
        break label963;
      }
      this.mContainingFrame.set(paramRect1);
      this.mDisplayFrame.set(paramRect2);
      localObject = paramRect1;
      i = 0;
      j = 0;
      i2 = this.mContainingFrame.width();
      i3 = this.mContainingFrame.height();
      if (!this.mParentFrame.equals(paramRect1))
      {
        this.mParentFrame.set(paramRect1);
        this.mContentChanged = true;
      }
      if ((this.mRequestedWidth != this.mLastRequestedWidth) || (this.mRequestedHeight != this.mLastRequestedHeight))
      {
        this.mLastRequestedWidth = this.mRequestedWidth;
        this.mLastRequestedHeight = this.mRequestedHeight;
        this.mContentChanged = true;
      }
      this.mOverscanFrame.set(paramRect3);
      this.mContentFrame.set(paramRect4);
      this.mVisibleFrame.set(paramRect5);
      this.mDecorFrame.set(paramRect6);
      this.mStableFrame.set(paramRect7);
      if (paramRect8 == null) {
        break label1378;
      }
      k = 1;
      if (k != 0) {
        this.mOutsetFrame.set(paramRect8);
      }
      i4 = this.mFrame.width();
      i5 = this.mFrame.height();
      applyGravityAndUpdateFrame((Rect)localObject, paramRect2);
      if (k == 0) {
        break label1384;
      }
      this.mOutsets.set(Math.max(this.mContentFrame.left - this.mOutsetFrame.left, 0), Math.max(this.mContentFrame.top - this.mOutsetFrame.top, 0), Math.max(this.mOutsetFrame.right - this.mContentFrame.right, 0), Math.max(this.mOutsetFrame.bottom - this.mContentFrame.bottom, 0));
      label367:
      if ((bool) && (!this.mFrame.isEmpty())) {
        break label1398;
      }
      if (this.mAttrs.type != 2034) {
        break label1597;
      }
      this.mDisplayContent.getDockedDividerController().positionDockedStackedDivider(this.mFrame);
      this.mContentFrame.set(this.mFrame);
      if (!this.mFrame.equals(this.mLastFrame)) {
        this.mMovedByResize = true;
      }
      label439:
      if ((m != 0) && (!bool)) {
        break label1825;
      }
    }
    label939:
    label945:
    label951:
    label963:
    label992:
    label1075:
    label1137:
    label1164:
    label1329:
    label1357:
    label1363:
    label1369:
    label1378:
    label1384:
    label1398:
    int n;
    int i6;
    int i1;
    for (;;)
    {
      if (this.mAttrs.type != 2034) {
        break label1903;
      }
      this.mStableInsets.set(Math.max(this.mStableFrame.left - this.mDisplayFrame.left, 0), Math.max(this.mStableFrame.top - this.mDisplayFrame.top, 0), Math.max(this.mDisplayFrame.right - this.mStableFrame.right, 0), Math.max(this.mDisplayFrame.bottom - this.mStableFrame.bottom, 0));
      this.mContentInsets.setEmpty();
      this.mVisibleInsets.setEmpty();
      this.mFrame.offset(-i, -j);
      this.mCompatFrame.offset(-i, -j);
      this.mContentFrame.offset(-i, -j);
      this.mVisibleFrame.offset(-i, -j);
      this.mStableFrame.offset(-i, -j);
      this.mCompatFrame.set(this.mFrame);
      if (this.mEnforceSizeCompat)
      {
        this.mOverscanInsets.scale(this.mInvGlobalScale);
        this.mContentInsets.scale(this.mInvGlobalScale);
        this.mVisibleInsets.scale(this.mInvGlobalScale);
        this.mStableInsets.scale(this.mInvGlobalScale);
        this.mOutsets.scale(this.mInvGlobalScale);
        this.mCompatFrame.scale(this.mInvGlobalScale);
      }
      if ((this.mIsWallpaper) && ((i4 != this.mFrame.width()) || (i5 != this.mFrame.height())))
      {
        paramRect1 = getDisplayContent();
        if (paramRect1 != null)
        {
          paramRect1 = paramRect1.getDisplayInfo();
          this.mService.mWallpaperControllerLocked.updateWallpaperOffset(this, paramRect1.logicalWidth, paramRect1.logicalHeight, false);
        }
      }
      if ((WindowManagerDebugConfig.DEBUG_LAYOUT) || (WindowManagerService.localLOGV)) {
        Slog.v(TAG, "Resolving (mRequestedWidth=" + this.mRequestedWidth + ", mRequestedheight=" + this.mRequestedHeight + ") to" + " (pw=" + i2 + ", ph=" + i3 + "): frame=" + this.mFrame.toShortString() + " ci=" + this.mContentInsets.toShortString() + " vi=" + this.mVisibleInsets.toShortString() + " si=" + this.mStableInsets.toShortString() + " of=" + this.mOutsets.toShortString());
      }
      return;
      return;
      m = 1;
      break;
      bool = false;
      break label54;
      ((Task)localObject).getTempInsetBounds(this.mInsetFrame);
      break label66;
      ((Task)localObject).getBounds(this.mContainingFrame);
      if ((this.mAppToken == null) || (this.mAppToken.mFrozenBounds.isEmpty()))
      {
        localObject = this.mService.mInputMethodWindow;
        if ((localObject != null) && (((WindowState)localObject).isVisibleNow()) && (this.mService.mInputMethodTarget == this))
        {
          if ((!bool) || (this.mContainingFrame.bottom <= paramRect4.bottom)) {
            break label1329;
          }
          localObject = this.mContainingFrame;
          ((Rect)localObject).top -= this.mContainingFrame.bottom - paramRect4.bottom;
        }
        if ((bool) && (this.mContainingFrame.isEmpty())) {
          this.mContainingFrame.set(paramRect4);
        }
        this.mDisplayFrame.set(this.mContainingFrame);
        if (this.mInsetFrame.isEmpty()) {
          break label1357;
        }
        i = this.mInsetFrame.left - this.mContainingFrame.left;
        if (this.mInsetFrame.isEmpty()) {
          break label1363;
        }
        j = this.mInsetFrame.top - this.mContainingFrame.top;
        if (this.mInsetFrame.isEmpty()) {
          break label1369;
        }
      }
      for (localObject = this.mInsetFrame;; localObject = this.mContainingFrame)
      {
        this.mTmpRect.set(0, 0, this.mDisplayContent.getDisplayInfo().logicalWidth, this.mDisplayContent.getDisplayInfo().logicalHeight);
        subtractInsets(this.mDisplayFrame, (Rect)localObject, paramRect2, this.mTmpRect);
        if (!layoutInParentFrame())
        {
          subtractInsets(this.mContainingFrame, (Rect)localObject, paramRect1, this.mTmpRect);
          subtractInsets(this.mInsetFrame, (Rect)localObject, paramRect1, this.mTmpRect);
        }
        paramRect2.intersect((Rect)localObject);
        break;
        localObject = (Rect)this.mAppToken.mFrozenBounds.peek();
        this.mContainingFrame.right = (this.mContainingFrame.left + ((Rect)localObject).width());
        this.mContainingFrame.bottom = (this.mContainingFrame.top + ((Rect)localObject).height());
        break label992;
        if (this.mContainingFrame.bottom <= paramRect1.bottom) {
          break label1075;
        }
        this.mContainingFrame.bottom = paramRect1.bottom;
        break label1075;
        i = 0;
        break label1137;
        j = 0;
        break label1164;
      }
      k = 0;
      break label240;
      this.mOutsets.set(0, 0, 0, 0);
      break label367;
      k = Math.min(this.mFrame.height(), this.mContentFrame.height());
      n = Math.min(this.mContentFrame.width(), this.mFrame.width());
      paramRect1 = getDisplayContent().getDisplayMetrics();
      i6 = Math.min(k, WindowManagerService.dipToPixel(32, paramRect1));
      i1 = Math.min(n, WindowManagerService.dipToPixel(48, paramRect1));
      i6 = Math.max(this.mContentFrame.top, Math.min(this.mFrame.top, this.mContentFrame.bottom - i6));
      i1 = Math.max(this.mContentFrame.left + i1 - n, Math.min(this.mFrame.left, this.mContentFrame.right - i1));
      this.mFrame.set(i1, i6, i1 + n, i6 + k);
      this.mContentFrame.set(this.mFrame);
      this.mVisibleFrame.set(this.mContentFrame);
      this.mStableFrame.set(this.mContentFrame);
      break label439;
      label1597:
      this.mContentFrame.set(Math.max(this.mContentFrame.left, this.mFrame.left), Math.max(this.mContentFrame.top, this.mFrame.top), Math.min(this.mContentFrame.right, this.mFrame.right), Math.min(this.mContentFrame.bottom, this.mFrame.bottom));
      this.mVisibleFrame.set(Math.max(this.mVisibleFrame.left, this.mFrame.left), Math.max(this.mVisibleFrame.top, this.mFrame.top), Math.min(this.mVisibleFrame.right, this.mFrame.right), Math.min(this.mVisibleFrame.bottom, this.mFrame.bottom));
      this.mStableFrame.set(Math.max(this.mStableFrame.left, this.mFrame.left), Math.max(this.mStableFrame.top, this.mFrame.top), Math.min(this.mStableFrame.right, this.mFrame.right), Math.min(this.mStableFrame.bottom, this.mFrame.bottom));
      break label439;
      label1825:
      this.mOverscanInsets.set(Math.max(this.mOverscanFrame.left - ((Rect)localObject).left, 0), Math.max(this.mOverscanFrame.top - ((Rect)localObject).top, 0), Math.max(((Rect)localObject).right - this.mOverscanFrame.right, 0), Math.max(((Rect)localObject).bottom - this.mOverscanFrame.bottom, 0));
    }
    label1903:
    getDisplayContent().getLogicalDisplayRect(this.mTmpRect);
    if ((m == 0) && (this.mFrame.right > this.mTmpRect.right))
    {
      k = 1;
      label1939:
      if ((m != 0) || (this.mFrame.bottom <= this.mTmpRect.bottom)) {
        break label2290;
      }
      m = 1;
      label1964:
      paramRect1 = this.mContentInsets;
      i6 = this.mContentFrame.left;
      int i7 = this.mFrame.left;
      int i8 = this.mContentFrame.top;
      int i9 = this.mFrame.top;
      if (k == 0) {
        break label2296;
      }
      n = this.mTmpRect.right - this.mContentFrame.right;
      label2027:
      if (m == 0) {
        break label2316;
      }
      i1 = this.mTmpRect.bottom - this.mContentFrame.bottom;
      label2049:
      paramRect1.set(i6 - i7, i8 - i9, n, i1);
      paramRect1 = this.mVisibleInsets;
      i6 = this.mVisibleFrame.left;
      i7 = this.mFrame.left;
      i8 = this.mVisibleFrame.top;
      i9 = this.mFrame.top;
      if (k == 0) {
        break label2336;
      }
      n = this.mTmpRect.right - this.mVisibleFrame.right;
      label2130:
      if (m == 0) {
        break label2356;
      }
      i1 = this.mTmpRect.bottom - this.mVisibleFrame.bottom;
      label2152:
      paramRect1.set(i6 - i7, i8 - i9, n, i1);
      paramRect1 = this.mStableInsets;
      n = Math.max(this.mStableFrame.left - this.mFrame.left, 0);
      i1 = Math.max(this.mStableFrame.top - this.mFrame.top, 0);
      if (k == 0) {
        break label2376;
      }
      k = Math.max(this.mTmpRect.right - this.mStableFrame.right, 0);
      label2243:
      if (m == 0) {
        break label2400;
      }
    }
    label2290:
    label2296:
    label2316:
    label2336:
    label2356:
    label2376:
    label2400:
    for (int m = Math.max(this.mTmpRect.bottom - this.mStableFrame.bottom, 0);; m = Math.max(this.mFrame.bottom - this.mStableFrame.bottom, 0))
    {
      paramRect1.set(n, i1, k, m);
      break;
      k = 0;
      break label1939;
      m = 0;
      break label1964;
      n = this.mFrame.right - this.mContentFrame.right;
      break label2027;
      i1 = this.mFrame.bottom - this.mContentFrame.bottom;
      break label2049;
      n = this.mFrame.right - this.mVisibleFrame.right;
      break label2130;
      i1 = this.mFrame.bottom - this.mVisibleFrame.bottom;
      break label2152;
      k = Math.max(this.mFrame.right - this.mStableFrame.right, 0);
      break label2243;
    }
  }
  
  void cropRegionToStackBoundsIfNeeded(Region paramRegion)
  {
    Object localObject = getTask();
    if ((localObject != null) && (((Task)localObject).cropWindowsToStackBounds()))
    {
      localObject = ((Task)localObject).mStack;
      if (localObject != null) {}
    }
    else
    {
      return;
    }
    ((TaskStack)localObject).getDimBounds(this.mTmpRect);
    paramRegion.op(this.mTmpRect, Region.Op.INTERSECT);
  }
  
  void destroyOrSaveSurface()
  {
    this.mSurfaceSaved = shouldSaveSurface();
    if (this.mSurfaceSaved)
    {
      if ((WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS) || (WindowManagerDebugConfig.DEBUG_ANIM)) {
        Slog.v(TAG, "Saving surface: " + this);
      }
      this.mSession.setTransparentRegion(this.mClient, sEmptyRegion);
      this.mWinAnimator.hide("saved surface");
      this.mWinAnimator.mDrawState = 0;
      setHasSurface(false);
      if (this.mWinAnimator.mSurfaceController != null) {
        this.mWinAnimator.mSurfaceController.disconnectInTransaction();
      }
      this.mAnimatingWithSavedSurface = false;
    }
    for (;;)
    {
      this.mAnimatingExit = false;
      return;
      this.mWinAnimator.destroySurfaceLocked();
    }
  }
  
  void destroySavedSurface()
  {
    if (this.mSurfaceSaved)
    {
      if ((WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS) || (WindowManagerDebugConfig.DEBUG_ANIM)) {
        Slog.v(TAG, "Destroying saved surface: " + this);
      }
      this.mWinAnimator.destroySurfaceLocked();
      this.mSurfaceSaved = false;
    }
    this.mWasVisibleBeforeClientHidden = false;
  }
  
  void disposeInputChannel()
  {
    if (this.mDeadWindowEventReceiver != null)
    {
      this.mDeadWindowEventReceiver.dispose();
      this.mDeadWindowEventReceiver = null;
    }
    if (this.mInputChannel != null)
    {
      this.mService.mInputManager.unregisterInputChannel(this.mInputChannel);
      this.mInputChannel.dispose();
      this.mInputChannel = null;
    }
    if (this.mClientChannel != null)
    {
      this.mClientChannel.dispose();
      this.mClientChannel = null;
    }
    this.mInputWindowHandle.inputChannel = null;
  }
  
  void dump(PrintWriter paramPrintWriter, String paramString, boolean paramBoolean)
  {
    int i = 0;
    Object localObject = getStack();
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mDisplayId=");
    paramPrintWriter.print(getDisplayId());
    if (localObject != null)
    {
      paramPrintWriter.print(" stackId=");
      paramPrintWriter.print(((TaskStack)localObject).mStackId);
    }
    if (this.mNotOnAppsDisplay)
    {
      paramPrintWriter.print(" mNotOnAppsDisplay=");
      paramPrintWriter.print(this.mNotOnAppsDisplay);
    }
    paramPrintWriter.print(" mSession=");
    paramPrintWriter.print(this.mSession);
    paramPrintWriter.print(" mClient=");
    paramPrintWriter.println(this.mClient.asBinder());
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mOwnerUid=");
    paramPrintWriter.print(this.mOwnerUid);
    paramPrintWriter.print(" mShowToOwnerOnly=");
    paramPrintWriter.print(this.mShowToOwnerOnly);
    paramPrintWriter.print(" package=");
    paramPrintWriter.print(this.mAttrs.packageName);
    paramPrintWriter.print(" appop=");
    paramPrintWriter.println(AppOpsManager.opToName(this.mAppOp));
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mAttrs=");
    paramPrintWriter.println(this.mAttrs);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("Requested w=");
    paramPrintWriter.print(this.mRequestedWidth);
    paramPrintWriter.print(" h=");
    paramPrintWriter.print(this.mRequestedHeight);
    paramPrintWriter.print(" mLayoutSeq=");
    paramPrintWriter.println(this.mLayoutSeq);
    if ((this.mRequestedWidth != this.mLastRequestedWidth) || (this.mRequestedHeight != this.mLastRequestedHeight))
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("LastRequested w=");
      paramPrintWriter.print(this.mLastRequestedWidth);
      paramPrintWriter.print(" h=");
      paramPrintWriter.println(this.mLastRequestedHeight);
    }
    if ((isChildWindow()) || (this.mLayoutAttached))
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mAttachedWindow=");
      paramPrintWriter.print(this.mAttachedWindow);
      paramPrintWriter.print(" mLayoutAttached=");
      paramPrintWriter.println(this.mLayoutAttached);
    }
    if ((this.mIsImWindow) || (this.mIsWallpaper) || (this.mIsFloatingLayer))
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mIsImWindow=");
      paramPrintWriter.print(this.mIsImWindow);
      paramPrintWriter.print(" mIsWallpaper=");
      paramPrintWriter.print(this.mIsWallpaper);
      paramPrintWriter.print(" mIsFloatingLayer=");
      paramPrintWriter.print(this.mIsFloatingLayer);
      paramPrintWriter.print(" mWallpaperVisible=");
      paramPrintWriter.println(this.mWallpaperVisible);
    }
    if (paramBoolean)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mBaseLayer=");
      paramPrintWriter.print(this.mBaseLayer);
      paramPrintWriter.print(" mSubLayer=");
      paramPrintWriter.print(this.mSubLayer);
      paramPrintWriter.print(" mAnimLayer=");
      paramPrintWriter.print(this.mLayer);
      paramPrintWriter.print("+");
      if (this.mTargetAppToken == null) {
        break label2253;
      }
      i = this.mTargetAppToken.mAppAnimator.animLayerAdjustment;
    }
    for (;;)
    {
      paramPrintWriter.print(i);
      paramPrintWriter.print("=");
      paramPrintWriter.print(this.mWinAnimator.mAnimLayer);
      paramPrintWriter.print(" mLastLayer=");
      paramPrintWriter.println(this.mWinAnimator.mLastLayer);
      if (paramBoolean)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mToken=");
        paramPrintWriter.println(this.mToken);
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mRootToken=");
        paramPrintWriter.println(this.mRootToken);
        if (this.mAppToken != null)
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("mAppToken=");
          paramPrintWriter.println(this.mAppToken);
          paramPrintWriter.print(paramString);
          paramPrintWriter.print(" isAnimatingWithSavedSurface()=");
          paramPrintWriter.print(isAnimatingWithSavedSurface());
          paramPrintWriter.print(" mAppDied=");
          paramPrintWriter.println(this.mAppDied);
        }
        if (this.mTargetAppToken != null)
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("mTargetAppToken=");
          paramPrintWriter.println(this.mTargetAppToken);
        }
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mViewVisibility=0x");
        paramPrintWriter.print(Integer.toHexString(this.mViewVisibility));
        paramPrintWriter.print(" mHaveFrame=");
        paramPrintWriter.print(this.mHaveFrame);
        paramPrintWriter.print(" mObscured=");
        paramPrintWriter.println(this.mObscured);
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mSeq=");
        paramPrintWriter.print(this.mSeq);
        paramPrintWriter.print(" mSystemUiVisibility=0x");
        paramPrintWriter.println(Integer.toHexString(this.mSystemUiVisibility));
      }
      if ((!this.mPolicyVisibility) || (!this.mPolicyVisibilityAfterAnim) || (!this.mAppOpVisibility) || (this.mAttachedHidden) || (this.mPermanentlyHidden))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mPolicyVisibility=");
        paramPrintWriter.print(this.mPolicyVisibility);
        paramPrintWriter.print(" mPolicyVisibilityAfterAnim=");
        paramPrintWriter.print(this.mPolicyVisibilityAfterAnim);
        paramPrintWriter.print(" mAppOpVisibility=");
        paramPrintWriter.print(this.mAppOpVisibility);
        paramPrintWriter.print(" mAttachedHidden=");
        paramPrintWriter.println(this.mAttachedHidden);
        paramPrintWriter.print(" mPermanentlyHidden=");
        paramPrintWriter.println(this.mPermanentlyHidden);
      }
      if ((!this.mRelayoutCalled) || (this.mLayoutNeeded))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mRelayoutCalled=");
        paramPrintWriter.print(this.mRelayoutCalled);
        paramPrintWriter.print(" mLayoutNeeded=");
        paramPrintWriter.println(this.mLayoutNeeded);
      }
      if ((this.mXOffset != 0) || (this.mYOffset != 0))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("Offsets x=");
        paramPrintWriter.print(this.mXOffset);
        paramPrintWriter.print(" y=");
        paramPrintWriter.println(this.mYOffset);
      }
      if (paramBoolean)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mGivenContentInsets=");
        this.mGivenContentInsets.printShortString(paramPrintWriter);
        paramPrintWriter.print(" mGivenVisibleInsets=");
        this.mGivenVisibleInsets.printShortString(paramPrintWriter);
        paramPrintWriter.println();
        if ((this.mTouchableInsets != 0) || (this.mGivenInsetsPending))
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("mTouchableInsets=");
          paramPrintWriter.print(this.mTouchableInsets);
          paramPrintWriter.print(" mGivenInsetsPending=");
          paramPrintWriter.println(this.mGivenInsetsPending);
          localObject = new Region();
          getTouchableRegion((Region)localObject);
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("touchable region=");
          paramPrintWriter.println(localObject);
        }
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mMergedConfiguration=");
        paramPrintWriter.println(this.mMergedConfiguration);
      }
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mHasSurface=");
      paramPrintWriter.print(this.mHasSurface);
      paramPrintWriter.print(" mShownPosition=");
      this.mShownPosition.printShortString(paramPrintWriter);
      paramPrintWriter.print(" isReadyForDisplay()=");
      paramPrintWriter.print(isReadyForDisplay());
      paramPrintWriter.print(" hasSavedSurface()=");
      paramPrintWriter.print(hasSavedSurface());
      paramPrintWriter.print(" mWindowRemovalAllowed=");
      paramPrintWriter.println(this.mWindowRemovalAllowed);
      if (paramBoolean)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mFrame=");
        this.mFrame.printShortString(paramPrintWriter);
        paramPrintWriter.print(" last=");
        this.mLastFrame.printShortString(paramPrintWriter);
        paramPrintWriter.println();
      }
      if (this.mEnforceSizeCompat)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mCompatFrame=");
        this.mCompatFrame.printShortString(paramPrintWriter);
        paramPrintWriter.println();
      }
      if (paramBoolean)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("Frames: containing=");
        this.mContainingFrame.printShortString(paramPrintWriter);
        paramPrintWriter.print(" parent=");
        this.mParentFrame.printShortString(paramPrintWriter);
        paramPrintWriter.println();
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("    display=");
        this.mDisplayFrame.printShortString(paramPrintWriter);
        paramPrintWriter.print(" overscan=");
        this.mOverscanFrame.printShortString(paramPrintWriter);
        paramPrintWriter.println();
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("    content=");
        this.mContentFrame.printShortString(paramPrintWriter);
        paramPrintWriter.print(" visible=");
        this.mVisibleFrame.printShortString(paramPrintWriter);
        paramPrintWriter.println();
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("    decor=");
        this.mDecorFrame.printShortString(paramPrintWriter);
        paramPrintWriter.println();
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("    outset=");
        this.mOutsetFrame.printShortString(paramPrintWriter);
        paramPrintWriter.println();
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("Cur insets: overscan=");
        this.mOverscanInsets.printShortString(paramPrintWriter);
        paramPrintWriter.print(" content=");
        this.mContentInsets.printShortString(paramPrintWriter);
        paramPrintWriter.print(" visible=");
        this.mVisibleInsets.printShortString(paramPrintWriter);
        paramPrintWriter.print(" stable=");
        this.mStableInsets.printShortString(paramPrintWriter);
        paramPrintWriter.print(" surface=");
        this.mAttrs.surfaceInsets.printShortString(paramPrintWriter);
        paramPrintWriter.print(" outsets=");
        this.mOutsets.printShortString(paramPrintWriter);
        paramPrintWriter.println();
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("Lst insets: overscan=");
        this.mLastOverscanInsets.printShortString(paramPrintWriter);
        paramPrintWriter.print(" content=");
        this.mLastContentInsets.printShortString(paramPrintWriter);
        paramPrintWriter.print(" visible=");
        this.mLastVisibleInsets.printShortString(paramPrintWriter);
        paramPrintWriter.print(" stable=");
        this.mLastStableInsets.printShortString(paramPrintWriter);
        paramPrintWriter.print(" physical=");
        this.mLastOutsets.printShortString(paramPrintWriter);
        paramPrintWriter.print(" outset=");
        this.mLastOutsets.printShortString(paramPrintWriter);
        paramPrintWriter.println();
      }
      paramPrintWriter.print(paramString);
      paramPrintWriter.print(this.mWinAnimator);
      paramPrintWriter.println(":");
      this.mWinAnimator.dump(paramPrintWriter, paramString + "  ", paramBoolean);
      if ((this.mAnimatingExit) || (this.mRemoveOnExit) || (this.mDestroying) || (this.mRemoved))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mAnimatingExit=");
        paramPrintWriter.print(this.mAnimatingExit);
        paramPrintWriter.print(" mRemoveOnExit=");
        paramPrintWriter.print(this.mRemoveOnExit);
        paramPrintWriter.print(" mDestroying=");
        paramPrintWriter.print(this.mDestroying);
        paramPrintWriter.print(" mRemoved=");
        paramPrintWriter.println(this.mRemoved);
      }
      if ((this.mOrientationChanging) || (this.mAppFreezing) || (this.mTurnOnScreen))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mOrientationChanging=");
        paramPrintWriter.print(this.mOrientationChanging);
        paramPrintWriter.print(" mAppFreezing=");
        paramPrintWriter.print(this.mAppFreezing);
        paramPrintWriter.print(" mTurnOnScreen=");
        paramPrintWriter.println(this.mTurnOnScreen);
      }
      if (this.mLastFreezeDuration != 0)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mLastFreezeDuration=");
        TimeUtils.formatDuration(this.mLastFreezeDuration, paramPrintWriter);
        paramPrintWriter.println();
      }
      if ((this.mHScale != 1.0F) || (this.mVScale != 1.0F))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mHScale=");
        paramPrintWriter.print(this.mHScale);
        paramPrintWriter.print(" mVScale=");
        paramPrintWriter.println(this.mVScale);
      }
      if ((this.mWallpaperX != -1.0F) || (this.mWallpaperY != -1.0F))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mWallpaperX=");
        paramPrintWriter.print(this.mWallpaperX);
        paramPrintWriter.print(" mWallpaperY=");
        paramPrintWriter.println(this.mWallpaperY);
      }
      if ((this.mWallpaperXStep != -1.0F) || (this.mWallpaperYStep != -1.0F))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mWallpaperXStep=");
        paramPrintWriter.print(this.mWallpaperXStep);
        paramPrintWriter.print(" mWallpaperYStep=");
        paramPrintWriter.println(this.mWallpaperYStep);
      }
      if ((this.mWallpaperDisplayOffsetX != Integer.MIN_VALUE) || (this.mWallpaperDisplayOffsetY != Integer.MIN_VALUE))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mWallpaperDisplayOffsetX=");
        paramPrintWriter.print(this.mWallpaperDisplayOffsetX);
        paramPrintWriter.print(" mWallpaperDisplayOffsetY=");
        paramPrintWriter.println(this.mWallpaperDisplayOffsetY);
      }
      if (this.mDrawLock != null)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("mDrawLock=" + this.mDrawLock);
      }
      if (isDragResizing())
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("isDragResizing=" + isDragResizing());
      }
      if (computeDragResizing())
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("computeDragResizing=" + computeDragResizing());
      }
      return;
      label2253:
      if (this.mAppToken != null) {
        i = this.mAppToken.mAppAnimator.animLayerAdjustment;
      }
    }
  }
  
  int getAnimLayerAdjustment()
  {
    if (this.mTargetAppToken != null) {
      return this.mTargetAppToken.mAppAnimator.animLayerAdjustment;
    }
    if (this.mAppToken != null) {
      return this.mAppToken.mAppAnimator.animLayerAdjustment;
    }
    return 0;
  }
  
  public IApplicationToken getAppToken()
  {
    IApplicationToken localIApplicationToken = null;
    if (this.mAppToken != null) {
      localIApplicationToken = this.mAppToken.appToken;
    }
    return localIApplicationToken;
  }
  
  public WindowManager.LayoutParams getAttrs()
  {
    return this.mAttrs;
  }
  
  Rect getBackdropFrame(Rect paramRect)
  {
    if (!isDragResizing()) {}
    for (boolean bool = isDragResizeChanged(); (!ActivityManager.StackId.useWindowFrameForBackdrop(getStackId())) && (bool); bool = true)
    {
      paramRect = getDisplayInfo();
      this.mTmpRect.set(0, 0, paramRect.logicalWidth, paramRect.logicalHeight);
      return this.mTmpRect;
    }
    return paramRect;
  }
  
  public int getBaseType()
  {
    for (WindowState localWindowState = this; localWindowState.isChildWindow(); localWindowState = localWindowState.mAttachedWindow) {}
    return localWindowState.mAttrs.type;
  }
  
  public Rect getContentFrameLw()
  {
    return this.mContentFrame;
  }
  
  DimLayer.DimLayerUser getDimLayerUser()
  {
    Task localTask = getTask();
    if (localTask != null) {
      return localTask;
    }
    return getStack();
  }
  
  public DisplayContent getDisplayContent()
  {
    if ((this.mAppToken == null) || (this.mNotOnAppsDisplay)) {
      return this.mDisplayContent;
    }
    TaskStack localTaskStack = getStack();
    if (localTaskStack == null) {
      return this.mDisplayContent;
    }
    return localTaskStack.getDisplayContent();
  }
  
  public Rect getDisplayFrameLw()
  {
    return this.mDisplayFrame;
  }
  
  public int getDisplayId()
  {
    DisplayContent localDisplayContent = getDisplayContent();
    if (localDisplayContent == null) {
      return -1;
    }
    return localDisplayContent.getDisplayId();
  }
  
  public DisplayInfo getDisplayInfo()
  {
    DisplayInfo localDisplayInfo = null;
    DisplayContent localDisplayContent = getDisplayContent();
    if (localDisplayContent != null) {
      localDisplayInfo = localDisplayContent.getDisplayInfo();
    }
    return localDisplayInfo;
  }
  
  public Rect getFrameLw()
  {
    return this.mFrame;
  }
  
  public Rect getGivenContentInsetsLw()
  {
    return this.mGivenContentInsets;
  }
  
  public boolean getGivenInsetsPendingLw()
  {
    return this.mGivenInsetsPending;
  }
  
  public Rect getGivenVisibleInsetsLw()
  {
    return this.mGivenVisibleInsets;
  }
  
  public long getInputDispatchingTimeoutNanos()
  {
    if (this.mAppToken != null) {
      return this.mAppToken.inputDispatchingTimeoutNanos;
    }
    return 5000000000L;
  }
  
  public Rect getLastFrameLw()
  {
    return this.mLastFrame;
  }
  
  public boolean getNeedsMenuLw(WindowManagerPolicy.WindowState paramWindowState)
  {
    int i = -1;
    WindowState localWindowState = this;
    WindowList localWindowList = getWindowList();
    for (;;)
    {
      if (localWindowState.mAttrs.needsMenuKey != 0) {
        return localWindowState.mAttrs.needsMenuKey == 1;
      }
      if (localWindowState == paramWindowState) {
        return false;
      }
      int j = i;
      if (i < 0) {
        j = localWindowList.indexOf(localWindowState);
      }
      i = j - 1;
      if (i < 0) {
        return false;
      }
      localWindowState = (WindowState)localWindowList.get(i);
    }
  }
  
  public Rect getOverscanFrameLw()
  {
    return this.mOverscanFrame;
  }
  
  public String getOwningPackage()
  {
    return this.mAttrs.packageName;
  }
  
  public int getOwningUid()
  {
    return this.mOwnerUid;
  }
  
  int getResizeMode()
  {
    return this.mResizeMode;
  }
  
  public int getRotationAnimationHint()
  {
    if (this.mAppToken != null) {
      return this.mAppToken.mRotationAnimationHint;
    }
    return -1;
  }
  
  public Point getShownPositionLw()
  {
    return this.mShownPosition;
  }
  
  TaskStack getStack()
  {
    Object localObject2 = null;
    Object localObject1 = getTask();
    if ((localObject1 != null) && (((Task)localObject1).mStack != null)) {
      return ((Task)localObject1).mStack;
    }
    localObject1 = localObject2;
    if (this.mAttrs.type >= 2000)
    {
      localObject1 = localObject2;
      if (this.mDisplayContent != null) {
        localObject1 = this.mDisplayContent.getHomeStack();
      }
    }
    return (TaskStack)localObject1;
  }
  
  public int getStackId()
  {
    TaskStack localTaskStack = getStack();
    if (localTaskStack == null) {
      return -1;
    }
    return localTaskStack.mStackId;
  }
  
  public int getSurfaceLayer()
  {
    return this.mLayer;
  }
  
  public int getSystemUiVisibility()
  {
    return this.mSystemUiVisibility;
  }
  
  Task getTask()
  {
    Task localTask = null;
    if (this.mAppToken != null) {
      localTask = this.mAppToken.mTask;
    }
    return localTask;
  }
  
  int getTouchableRegion(Region paramRegion, int paramInt)
  {
    int i = 0;
    if ((paramInt & 0x28) == 0) {
      i = 1;
    }
    if ((i != 0) && (this.mAppToken != null))
    {
      DimLayer.DimLayerUser localDimLayerUser = getDimLayerUser();
      if (localDimLayerUser != null) {
        localDimLayerUser.getDimBounds(this.mTmpRect);
      }
      for (;;)
      {
        if (inFreeformWorkspace())
        {
          i = WindowManagerService.dipToPixel(30, getDisplayContent().getDisplayMetrics());
          this.mTmpRect.inset(-i, -i);
        }
        paramRegion.set(this.mTmpRect);
        cropRegionToStackBoundsIfNeeded(paramRegion);
        return paramInt | 0x20;
        getVisibleBounds(this.mTmpRect);
      }
    }
    getTouchableRegion(paramRegion);
    return paramInt;
  }
  
  void getTouchableRegion(Region paramRegion)
  {
    Rect localRect = this.mFrame;
    switch (this.mTouchableInsets)
    {
    case 0: 
    default: 
      paramRegion.set(localRect);
    }
    for (;;)
    {
      cropRegionToStackBoundsIfNeeded(paramRegion);
      return;
      applyInsets(paramRegion, localRect, this.mGivenContentInsets);
      continue;
      applyInsets(paramRegion, localRect, this.mGivenVisibleInsets);
      continue;
      paramRegion.set(this.mGivenTouchableRegion);
      paramRegion.translate(localRect.left, localRect.top);
    }
  }
  
  void getVisibleBounds(Rect paramRect)
  {
    Object localObject = getTask();
    boolean bool1;
    if (localObject != null)
    {
      bool1 = ((Task)localObject).cropWindowsToStackBounds();
      paramRect.setEmpty();
      this.mTmpRect.setEmpty();
      bool2 = bool1;
      if (bool1)
      {
        localObject = ((Task)localObject).mStack;
        if (localObject == null) {
          break label112;
        }
        ((TaskStack)localObject).getDimBounds(this.mTmpRect);
      }
    }
    label112:
    for (boolean bool2 = bool1;; bool2 = false)
    {
      paramRect.set(this.mVisibleFrame);
      if (bool2) {
        paramRect.intersect(this.mTmpRect);
      }
      if (!paramRect.isEmpty()) {
        return;
      }
      paramRect.set(this.mFrame);
      if (bool2) {
        paramRect.intersect(this.mTmpRect);
      }
      return;
      bool1 = false;
      break;
    }
  }
  
  public Rect getVisibleFrameLw()
  {
    return this.mVisibleFrame;
  }
  
  WindowList getWindowList()
  {
    DisplayContent localDisplayContent = getDisplayContent();
    if (localDisplayContent == null) {
      return null;
    }
    return localDisplayContent.getWindowList();
  }
  
  public int getWindowPid()
  {
    return this.mSession.mPid;
  }
  
  CharSequence getWindowTag()
  {
    CharSequence localCharSequence = this.mAttrs.getTitle();
    Object localObject;
    if (localCharSequence != null)
    {
      localObject = localCharSequence;
      if (localCharSequence.length() > 0) {}
    }
    else
    {
      localObject = this.mAttrs.packageName;
    }
    return (CharSequence)localObject;
  }
  
  public boolean hasAppShownWindows()
  {
    if (this.mAppToken != null)
    {
      if (!this.mAppToken.firstWindowDrawn) {
        return this.mAppToken.startingDisplayed;
      }
      return true;
    }
    return false;
  }
  
  public boolean hasDrawnLw()
  {
    return this.mWinAnimator.mDrawState == 4;
  }
  
  boolean hasJustMovedInStack()
  {
    return this.mJustMovedInStack;
  }
  
  public boolean hasMoved()
  {
    boolean bool2 = true;
    boolean bool1;
    if ((!this.mHasSurface) || ((!this.mContentChanged) && (!this.mMovedByResize)) || (this.mAnimatingExit)) {
      bool1 = false;
    }
    do
    {
      do
      {
        return bool1;
        if ((this.mFrame.top == this.mLastFrame.top) && (this.mFrame.left == this.mLastFrame.left)) {
          break;
        }
        bool1 = bool2;
      } while (this.mAttachedWindow == null);
      bool1 = bool2;
    } while (!this.mAttachedWindow.hasMoved());
    return false;
  }
  
  boolean hasSavedSurface()
  {
    return this.mSurfaceSaved;
  }
  
  public boolean hideLw(boolean paramBoolean)
  {
    return hideLw(paramBoolean, true);
  }
  
  boolean hideLw(boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool = paramBoolean1;
    if (paramBoolean1)
    {
      bool = paramBoolean1;
      if (!this.mService.okToDisplay()) {
        bool = false;
      }
    }
    if (bool) {}
    for (paramBoolean1 = this.mPolicyVisibilityAfterAnim; !paramBoolean1; paramBoolean1 = this.mPolicyVisibility) {
      return false;
    }
    paramBoolean1 = bool;
    if (bool)
    {
      this.mWinAnimator.applyAnimationLocked(2, false);
      paramBoolean1 = bool;
      if (this.mWinAnimator.mAnimation == null) {
        paramBoolean1 = false;
      }
    }
    if (paramBoolean1) {
      this.mPolicyVisibilityAfterAnim = false;
    }
    for (;;)
    {
      if (paramBoolean2) {
        this.mService.scheduleAnimationLocked();
      }
      return true;
      if (WindowManagerDebugConfig.DEBUG_VISIBILITY) {
        Slog.v(TAG, "Policy visibility false: " + this);
      }
      this.mPolicyVisibilityAfterAnim = false;
      this.mPolicyVisibility = false;
      this.mService.enableScreenIfNeededLocked();
      if (this.mService.mCurrentFocus == this)
      {
        if (WindowManagerDebugConfig.DEBUG_FOCUS_LIGHT) {
          Slog.i(TAG, "WindowState.hideLw: setting mFocusMayChange true");
        }
        this.mService.mFocusMayChange = true;
      }
    }
  }
  
  public void hidePermanentlyLw()
  {
    if (!this.mPermanentlyHidden)
    {
      this.mPermanentlyHidden = true;
      hideLw(true, true);
    }
  }
  
  boolean inDockedWorkspace()
  {
    Task localTask = getTask();
    if (localTask != null) {
      return localTask.inDockedWorkspace();
    }
    return false;
  }
  
  boolean inFreeformWorkspace()
  {
    Task localTask = getTask();
    if (localTask != null) {
      return localTask.inFreeformWorkspace();
    }
    return false;
  }
  
  boolean inPinnedWorkspace()
  {
    Task localTask = getTask();
    if (localTask != null) {
      return localTask.inPinnedWorkspace();
    }
    return false;
  }
  
  boolean isAdjustedForMinimizedDock()
  {
    if ((this.mAppToken != null) && (this.mAppToken.mTask != null)) {
      return this.mAppToken.mTask.mStack.isAdjustedForMinimizedDock();
    }
    return false;
  }
  
  public boolean isAlive()
  {
    return this.mClient.asBinder().isBinderAlive();
  }
  
  boolean isAnimatingInvisibleWithSavedSurface()
  {
    boolean bool = false;
    if (this.mAnimatingWithSavedSurface)
    {
      if (this.mViewVisibility == 0) {
        bool = this.mWindowRemovalAllowed;
      }
    }
    else {
      return bool;
    }
    return true;
  }
  
  public boolean isAnimatingLw()
  {
    return (this.mWinAnimator.mAnimation != null) || ((this.mAppToken != null) && (this.mAppToken.mAppAnimator.animation != null));
  }
  
  boolean isAnimatingWithSavedSurface()
  {
    return this.mAnimatingWithSavedSurface;
  }
  
  boolean isChildWindow()
  {
    return this.mAttachedWindow != null;
  }
  
  boolean isClosing()
  {
    if (!this.mAnimatingExit) {
      return this.mService.mClosingApps.contains(this.mAppToken);
    }
    return true;
  }
  
  boolean isConfigChanged()
  {
    getMergedConfig(this.mTmpConfig);
    boolean bool1;
    if (!this.mMergedConfiguration.equals(Configuration.EMPTY))
    {
      if (this.mTmpConfig.diff(this.mMergedConfiguration) == 0) {
        break label75;
      }
      bool1 = true;
    }
    for (;;)
    {
      boolean bool2 = bool1;
      if ((this.mAttrs.privateFlags & 0x400) != 0)
      {
        this.mConfigHasChanged |= bool1;
        bool2 = this.mConfigHasChanged;
      }
      return bool2;
      bool1 = true;
      continue;
      label75:
      bool1 = false;
    }
  }
  
  public boolean isDefaultDisplay()
  {
    DisplayContent localDisplayContent = getDisplayContent();
    if (localDisplayContent == null) {
      return false;
    }
    return localDisplayContent.isDefaultDisplay;
  }
  
  public boolean isDimming()
  {
    DimLayer.DimLayerUser localDimLayerUser = getDimLayerUser();
    if ((localDimLayerUser != null) && (this.mDisplayContent != null)) {
      return this.mDisplayContent.mDimLayerController.isDimming(localDimLayerUser, this.mWinAnimator);
    }
    return false;
  }
  
  public boolean isDisplayedLw()
  {
    AppWindowToken localAppWindowToken = this.mAppToken;
    if ((isDrawnLw()) && (this.mPolicyVisibility)) {
      return ((!this.mAttachedHidden) && ((localAppWindowToken == null) || (!localAppWindowToken.hiddenRequested))) || (this.mWinAnimator.mAnimating) || ((localAppWindowToken != null) && (localAppWindowToken.mAppAnimator.animation != null));
    }
    return false;
  }
  
  boolean isDockedInEffect()
  {
    Task localTask = getTask();
    if (localTask != null) {
      return localTask.isDockedInEffect();
    }
    return false;
  }
  
  boolean isDockedResizing()
  {
    return (this.mDragResizing) && (getResizeMode() == 1);
  }
  
  boolean isDragResizeChanged()
  {
    return this.mDragResizing != computeDragResizing();
  }
  
  boolean isDragResizing()
  {
    return this.mDragResizing;
  }
  
  boolean isDragResizingChangeReported()
  {
    return this.mDragResizingChangeReported;
  }
  
  public boolean isDrawFinishedLw()
  {
    boolean bool2 = true;
    boolean bool1;
    if ((!this.mHasSurface) || (this.mDestroying)) {
      bool1 = false;
    }
    do
    {
      do
      {
        do
        {
          return bool1;
          bool1 = bool2;
        } while (this.mWinAnimator.mDrawState == 2);
        bool1 = bool2;
      } while (this.mWinAnimator.mDrawState == 3);
      bool1 = bool2;
    } while (this.mWinAnimator.mDrawState == 4);
    return false;
  }
  
  public boolean isDrawnLw()
  {
    boolean bool2 = true;
    boolean bool1;
    if ((!this.mHasSurface) || (this.mDestroying)) {
      bool1 = false;
    }
    do
    {
      do
      {
        return bool1;
        bool1 = bool2;
      } while (this.mWinAnimator.mDrawState == 3);
      bool1 = bool2;
    } while (this.mWinAnimator.mDrawState == 4);
    return false;
  }
  
  public boolean isFocused()
  {
    synchronized (this.mService.mWindowMap)
    {
      WindowState localWindowState = this.mService.mCurrentFocus;
      if (localWindowState == this)
      {
        bool = true;
        return bool;
      }
      boolean bool = false;
    }
  }
  
  boolean isFrameFullscreen(DisplayInfo paramDisplayInfo)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mFrame.left <= 0)
    {
      bool1 = bool2;
      if (this.mFrame.top <= 0)
      {
        bool1 = bool2;
        if (this.mFrame.right >= paramDisplayInfo.appWidth)
        {
          bool1 = bool2;
          if (this.mFrame.bottom >= paramDisplayInfo.appHeight) {
            bool1 = true;
          }
        }
      }
    }
    return bool1;
  }
  
  public boolean isGoneForLayoutLw()
  {
    AppWindowToken localAppWindowToken = this.mAppToken;
    if ((this.mViewVisibility != 8) && (this.mRelayoutCalled) && ((localAppWindowToken != null) || (!this.mRootToken.hidden)) && ((localAppWindowToken == null) || (!localAppWindowToken.hiddenRequested)) && (!this.mAttachedHidden) && ((!this.mAnimatingExit) || (isAnimatingLw()))) {
      return this.mDestroying;
    }
    return true;
  }
  
  boolean isHiddenFromUserLocked()
  {
    for (WindowState localWindowState = this; localWindowState.isChildWindow(); localWindowState = localWindowState.mAttachedWindow) {}
    if ((localWindowState.mAttrs.type < 2000) && (localWindowState.mAppToken != null) && (localWindowState.mAppToken.showForAllUsers) && (localWindowState.mFrame.left <= localWindowState.mDisplayFrame.left) && (localWindowState.mFrame.top <= localWindowState.mDisplayFrame.top) && (localWindowState.mFrame.right >= localWindowState.mStableFrame.right) && (localWindowState.mFrame.bottom >= localWindowState.mStableFrame.bottom)) {
      return false;
    }
    return (localWindowState.mShowToOwnerOnly) && (!this.mService.isCurrentProfileLocked(UserHandle.getUserId(localWindowState.mOwnerUid)));
  }
  
  boolean isIdentityMatrix(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    if ((paramFloat1 < 0.99999F) || (paramFloat1 > 1.00001F)) {
      return false;
    }
    if ((paramFloat4 < 0.99999F) || (paramFloat4 > 1.00001F)) {
      return false;
    }
    if ((paramFloat2 < -1.0E-6F) || (paramFloat2 > 1.0E-6F)) {
      return false;
    }
    return (paramFloat3 >= -1.0E-6F) && (paramFloat3 <= 1.0E-6F);
  }
  
  public boolean isInMultiWindowMode()
  {
    Task localTask = getTask();
    return (localTask != null) && (!localTask.isFullscreen());
  }
  
  boolean isInteresting()
  {
    boolean bool2 = true;
    boolean bool1;
    if ((this.mAppToken == null) || (this.mAppDied)) {
      bool1 = false;
    }
    do
    {
      do
      {
        return bool1;
        bool1 = bool2;
      } while (!this.mAppToken.mAppAnimator.freezingScreen);
      bool1 = bool2;
    } while (!this.mAppFreezing);
    return false;
  }
  
  boolean isObscuringFullscreen(DisplayInfo paramDisplayInfo)
  {
    Task localTask = getTask();
    if ((localTask == null) || (localTask.mStack == null) || (localTask.mStack.isFullscreen()))
    {
      if ((isOpaqueDrawn()) && (isFrameFullscreen(paramDisplayInfo))) {
        return true;
      }
    }
    else {
      return false;
    }
    return false;
  }
  
  boolean isOnScreen()
  {
    if (this.mPolicyVisibility) {
      return isOnScreenIgnoringKeyguard();
    }
    return false;
  }
  
  boolean isOnScreenIgnoringKeyguard()
  {
    if ((!this.mHasSurface) || (this.mDestroying)) {
      return false;
    }
    AppWindowToken localAppWindowToken = this.mAppToken;
    if (localAppWindowToken != null)
    {
      if (((!this.mAttachedHidden) && (!localAppWindowToken.hiddenRequested)) || (this.mWinAnimator.mAnimation != null)) {}
      while (localAppWindowToken.mAppAnimator.animation != null) {
        return true;
      }
      return false;
    }
    return (!this.mAttachedHidden) || (this.mWinAnimator.mAnimation != null);
  }
  
  boolean isOpaqueDrawn()
  {
    if ((!this.mIsWallpaper) && (this.mAttrs.format == -1)) {}
    while ((this.mIsWallpaper) && (this.mWallpaperVisible))
    {
      if ((!isDrawnLw()) || (this.mWinAnimator.mAnimation != null)) {
        break;
      }
      if ((this.mAppToken != null) && (this.mAppToken.mAppAnimator.animation != null)) {
        break label73;
      }
      return true;
    }
    return false;
    label73:
    return false;
  }
  
  boolean isPotentialDragTarget()
  {
    if ((!isVisibleNow()) || (this.mRemoved)) {}
    while ((this.mInputChannel == null) || (this.mInputWindowHandle == null)) {
      return false;
    }
    return true;
  }
  
  boolean isReadyForDisplay()
  {
    boolean bool2 = true;
    if ((this.mRootToken.waitingToShow) && (this.mService.mAppTransition.isTransitionSet())) {
      return false;
    }
    boolean bool1;
    if ((!this.mHasSurface) || (!this.mPolicyVisibility) || (this.mDestroying)) {
      bool1 = false;
    }
    do
    {
      do
      {
        do
        {
          return bool1;
          if ((this.mAttachedHidden) || (this.mViewVisibility != 0)) {
            break;
          }
          bool1 = bool2;
        } while (!this.mRootToken.hidden);
        bool1 = bool2;
      } while (this.mWinAnimator.mAnimation != null);
      if (this.mAppToken == null) {
        break;
      }
      bool1 = bool2;
    } while (this.mAppToken.mAppAnimator.animation != null);
    return false;
  }
  
  boolean isReadyForDisplayIgnoringKeyguard()
  {
    if ((this.mRootToken.waitingToShow) && (this.mService.mAppTransition.isTransitionSet())) {
      return false;
    }
    AppWindowToken localAppWindowToken = this.mAppToken;
    if ((localAppWindowToken != null) || (this.mPolicyVisibility))
    {
      if ((!this.mHasSurface) || (this.mDestroying)) {
        return false;
      }
    }
    else {
      return false;
    }
    if (((!this.mAttachedHidden) && (this.mViewVisibility == 0) && (!this.mRootToken.hidden)) || (this.mWinAnimator.mAnimation != null)) {}
    while ((localAppWindowToken != null) && (localAppWindowToken.mAppAnimator.animation != null) && (!this.mWinAnimator.isDummyAnimation())) {
      return true;
    }
    return isAnimatingWithSavedSurface();
  }
  
  public boolean isRemovedOrHidden()
  {
    return (this.mPermanentlyHidden) || (this.mAnimatingExit) || (this.mRemoveOnExit) || (this.mWindowRemovalAllowed) || (this.mViewVisibility == 8);
  }
  
  boolean isResizedWhileNotDragResizing()
  {
    return this.mResizedWhileNotDragResizing;
  }
  
  boolean isResizedWhileNotDragResizingReported()
  {
    return this.mResizedWhileNotDragResizingReported;
  }
  
  public boolean isRtl()
  {
    return this.mMergedConfiguration.getLayoutDirection() == 1;
  }
  
  public boolean isVisibleLw()
  {
    if ((this.mAppToken != null) && (this.mAppToken.hiddenRequested)) {
      return false;
    }
    return isVisibleUnchecked();
  }
  
  boolean isVisibleNow()
  {
    if ((!this.mRootToken.hidden) || (this.mAttrs.type == 3)) {
      return isVisibleUnchecked();
    }
    return false;
  }
  
  boolean isVisibleOrAdding()
  {
    AppWindowToken localAppWindowToken = this.mAppToken;
    if (((!this.mHasSurface) && ((this.mRelayoutCalled) || (this.mViewVisibility != 0))) || (!this.mPolicyVisibility) || (this.mAttachedHidden)) {}
    while (((localAppWindowToken != null) && (localAppWindowToken.hiddenRequested)) || (this.mAnimatingExit) || (this.mDestroying)) {
      return false;
    }
    return true;
  }
  
  public boolean isVisibleOrBehindKeyguardLw()
  {
    if ((this.mRootToken.waitingToShow) && (this.mService.mAppTransition.isTransitionSet())) {
      return false;
    }
    AppWindowToken localAppWindowToken = this.mAppToken;
    boolean bool;
    if ((localAppWindowToken != null) && (localAppWindowToken.mAppAnimator.animation != null))
    {
      bool = true;
      if ((this.mHasSurface) && (!this.mDestroying)) {
        break label69;
      }
    }
    for (;;)
    {
      label60:
      bool = false;
      label69:
      while (((this.mAttachedHidden) || (this.mViewVisibility != 0) || (this.mRootToken.hidden)) && (this.mWinAnimator.mAnimation == null))
      {
        return bool;
        bool = false;
        break;
        if (this.mAnimatingExit) {
          break label60;
        }
        if (localAppWindowToken != null) {
          break label123;
        }
        if (!this.mPolicyVisibility) {
          break label60;
        }
      }
      return true;
      label123:
      if (!localAppWindowToken.hiddenRequested) {}
    }
  }
  
  public boolean isVoiceInteraction()
  {
    if (this.mAppToken != null) {
      return this.mAppToken.voiceInteraction;
    }
    return false;
  }
  
  public boolean isWinVisibleLw()
  {
    if ((this.mAppToken == null) || (!this.mAppToken.hiddenRequested) || (this.mAppToken.mAppAnimator.animating)) {
      return isVisibleUnchecked();
    }
    return false;
  }
  
  boolean layoutInParentFrame()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (isChildWindow())
    {
      bool1 = bool2;
      if ((this.mAttrs.privateFlags & 0x10000) != 0) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  String makeInputChannelName()
  {
    return Integer.toHexString(System.identityHashCode(this)) + " " + getWindowTag();
  }
  
  void maybeRemoveReplacedWindow()
  {
    if (this.mAppToken == null) {
      return;
    }
    int i = this.mAppToken.allAppWindows.size() - 1;
    if (i >= 0)
    {
      WindowState localWindowState = (WindowState)this.mAppToken.allAppWindows.get(i);
      if ((localWindowState.mWillReplaceWindow) && (localWindowState.mReplacingWindow == this) && (hasDrawnLw()))
      {
        if (WindowManagerDebugConfig.DEBUG_ADD_REMOVE) {
          Slog.d(TAG, "Removing replaced window: " + localWindowState);
        }
        if (localWindowState.isDimming()) {
          localWindowState.transferDimToReplacement();
        }
        localWindowState.mWillReplaceWindow = false;
        boolean bool = localWindowState.mAnimateReplacingWindow;
        localWindowState.mAnimateReplacingWindow = false;
        localWindowState.mReplacingRemoveRequested = false;
        localWindowState.mReplacingWindow = null;
        this.mSkipEnterAnimationForSeamlessReplacement = false;
        if ((localWindowState.mAnimatingExit) || (!bool)) {
          break label154;
        }
      }
      for (;;)
      {
        i -= 1;
        break;
        label154:
        this.mService.removeWindowInnerLocked(localWindowState);
      }
    }
  }
  
  boolean mightAffectAllDrawn(boolean paramBoolean)
  {
    int i;
    if ((this.mAppToken != null) && (this.mAppToken.clientHidden))
    {
      i = 0;
      label19:
      if (((!isOnScreenIgnoringKeyguard()) || ((paramBoolean) && (i == 0))) && (this.mWinAnimator.mAttrType != 1)) {
        break label78;
      }
      label45:
      if (!this.mAnimatingExit) {
        break label92;
      }
    }
    label78:
    label92:
    while (this.mDestroying)
    {
      do
      {
        return false;
        if (this.mViewVisibility != 0) {
          break;
        }
        if (this.mWindowRemovalAllowed)
        {
          i = 0;
          break label19;
        }
        i = 1;
        break label19;
      } while (this.mWinAnimator.mAttrType != 4);
      break label45;
    }
    return true;
  }
  
  void notifyMovedInStack()
  {
    this.mJustMovedInStack = true;
  }
  
  void openInputChannel(InputChannel paramInputChannel)
  {
    if (this.mInputChannel != null) {
      throw new IllegalStateException("Window already has an input channel.");
    }
    InputChannel[] arrayOfInputChannel = InputChannel.openInputChannelPair(makeInputChannelName());
    this.mInputChannel = arrayOfInputChannel[0];
    this.mClientChannel = arrayOfInputChannel[1];
    this.mInputWindowHandle.inputChannel = arrayOfInputChannel[0];
    if (paramInputChannel != null)
    {
      this.mClientChannel.transferTo(paramInputChannel);
      this.mClientChannel.dispose();
      this.mClientChannel = null;
    }
    for (;;)
    {
      this.mService.mInputManager.registerInputChannel(this.mInputChannel, this.mInputWindowHandle);
      return;
      this.mDeadWindowEventReceiver = new DeadWindowEventReceiver(this.mClientChannel);
    }
  }
  
  public void pokeDrawLockLw(long paramLong)
  {
    if (isVisibleOrAdding())
    {
      if (this.mDrawLock == null)
      {
        localCharSequence = getWindowTag();
        this.mDrawLock = this.mService.mPowerManager.newWakeLock(128, "Window:" + localCharSequence);
        this.mDrawLock.setReferenceCounted(false);
        this.mDrawLock.setWorkSource(new WorkSource(this.mOwnerUid, this.mAttrs.packageName));
      }
      if (WindowManagerDebugConfig.DEBUG_POWER) {
        Slog.d(TAG, "pokeDrawLock: poking draw lock on behalf of visible window owned by " + this.mAttrs.packageName);
      }
      this.mDrawLock.acquire(paramLong);
    }
    while (!WindowManagerDebugConfig.DEBUG_POWER)
    {
      CharSequence localCharSequence;
      return;
    }
    Slog.d(TAG, "pokeDrawLock: suppressed draw lock request for invisible window owned by " + this.mAttrs.packageName);
  }
  
  void prelayout()
  {
    if (this.mEnforceSizeCompat)
    {
      this.mGlobalScale = this.mService.mCompatibleScreenScale;
      this.mInvGlobalScale = (1.0F / this.mGlobalScale);
      return;
    }
    this.mInvGlobalScale = 1.0F;
    this.mGlobalScale = 1.0F;
  }
  
  void prepareWindowToDisplayDuringRelayout(Configuration paramConfiguration)
  {
    if ((this.mAttrs.softInputMode & 0xF0) == 16) {
      this.mLayoutNeeded = true;
    }
    if ((isDrawnLw()) && (this.mService.okToDisplay())) {
      this.mWinAnimator.applyEnterAnimationLocked();
    }
    if ((this.mAttrs.flags & 0x200000) != 0)
    {
      if (WindowManagerDebugConfig.DEBUG_VISIBILITY) {
        Slog.v(TAG, "Relayout window turning screen on: " + this);
      }
      this.mTurnOnScreen = true;
    }
    if (isConfigChanged())
    {
      Configuration localConfiguration = updateConfiguration();
      if (WindowManagerDebugConfig.DEBUG_CONFIGURATION) {
        Slog.i(TAG, "Window " + this + " visible with new config: " + localConfiguration);
      }
      paramConfiguration.setTo(localConfiguration);
    }
  }
  
  public void registerFocusObserver(IWindowFocusObserver paramIWindowFocusObserver)
  {
    synchronized (this.mService.mWindowMap)
    {
      if (this.mFocusCallbacks == null) {
        this.mFocusCallbacks = new RemoteCallbackList();
      }
      this.mFocusCallbacks.register(paramIWindowFocusObserver);
      return;
    }
  }
  
  void removeLocked()
  {
    disposeInputChannel();
    if (isChildWindow())
    {
      if (WindowManagerDebugConfig.DEBUG_ADD_REMOVE) {
        Slog.v(TAG, "Removing " + this + " from " + this.mAttachedWindow);
      }
      this.mAttachedWindow.mChildWindows.remove(this);
    }
    this.mWinAnimator.destroyDeferredSurfaceLocked();
    this.mWinAnimator.destroySurfaceLocked();
    this.mSession.windowRemovedLocked();
    try
    {
      this.mClient.asBinder().unlinkToDeath(this.mDeathRecipient, 0);
      return;
    }
    catch (RuntimeException localRuntimeException) {}
  }
  
  public void reportFocusChangedSerialized(boolean paramBoolean1, boolean paramBoolean2)
  {
    try
    {
      this.mClient.windowFocusChanged(paramBoolean1, paramBoolean2);
      if (this.mFocusCallbacks != null)
      {
        int j = this.mFocusCallbacks.beginBroadcast();
        i = 0;
        if (i < j)
        {
          IWindowFocusObserver localIWindowFocusObserver = (IWindowFocusObserver)this.mFocusCallbacks.getBroadcastItem(i);
          if (paramBoolean1) {}
          try
          {
            localIWindowFocusObserver.focusGained(this.mWindowId.asBinder());
          }
          catch (RemoteException localRemoteException1) {}
          localIWindowFocusObserver.focusLost(this.mWindowId.asBinder());
        }
        else
        {
          this.mFocusCallbacks.finishBroadcast();
        }
      }
      else
      {
        return;
      }
    }
    catch (RemoteException localRemoteException2)
    {
      for (;;)
      {
        int i;
        continue;
        i += 1;
      }
    }
  }
  
  void reportResized()
  {
    Trace.traceBegin(32L, "wm.reportResized_" + getWindowTag());
    for (;;)
    {
      try
      {
        if ((WindowManagerDebugConfig.DEBUG_RESIZE) || (WindowManagerDebugConfig.DEBUG_ORIENTATION)) {
          Slog.v(TAG, "Reporting new frame to " + this + ": " + this.mCompatFrame);
        }
        if (!isConfigChanged()) {
          continue;
        }
        localConfiguration = updateConfiguration();
        if ((WindowManagerDebugConfig.DEBUG_ORIENTATION) && (this.mWinAnimator.mDrawState == 1)) {
          Slog.i(TAG, "Resizing " + this + " WITH DRAW PENDING");
        }
        if (OnePlusProcessManager.isSupportFrozenApp()) {
          OnePlusProcessManager.resumeProcessByUID_out(this.mOwnerUid, "reportResized");
        }
        localRect1 = this.mFrame;
        localRect2 = this.mLastOverscanInsets;
        localRect3 = this.mLastContentInsets;
        localRect4 = this.mLastVisibleInsets;
        localRect5 = this.mLastStableInsets;
        localRect6 = this.mLastOutsets;
        if (this.mWinAnimator.mDrawState != 1) {
          continue;
        }
        bool = true;
        if ((this.mAttrs.type == 3) || (!(this.mClient instanceof IWindow.Stub))) {
          continue;
        }
        this.mService.mH.post(new Runnable()
        {
          public void run()
          {
            try
            {
              WindowState.-wrap0(WindowState.this, localRect1, localRect2, localRect3, localRect4, localRect5, localRect6, bool, localConfiguration);
              return;
            }
            catch (RemoteException localRemoteException) {}
          }
        });
        if ((this.mService.mAccessibilityController != null) && (getDisplayId() == 0)) {
          this.mService.mAccessibilityController.onSomeWindowResizedOrMovedLocked();
        }
        this.mOverscanInsetsChanged = false;
        this.mContentInsetsChanged = false;
        this.mVisibleInsetsChanged = false;
        this.mStableInsetsChanged = false;
        this.mOutsetsChanged = false;
        this.mFrameSizeChanged = false;
        this.mResizedWhileNotDragResizingReported = true;
        this.mWinAnimator.mSurfaceResized = false;
      }
      catch (RemoteException localRemoteException)
      {
        final Configuration localConfiguration;
        final Rect localRect1;
        final Rect localRect2;
        final Rect localRect3;
        final Rect localRect4;
        final Rect localRect5;
        final Rect localRect6;
        final boolean bool;
        this.mOrientationChanging = false;
        this.mLastFreezeDuration = ((int)(SystemClock.elapsedRealtime() - this.mService.mDisplayFreezeTime));
        Slog.w(TAG, "Failed to report 'resized' to the client of " + this + ", removing this window.");
        this.mService.mPendingRemove.add(this);
        this.mService.mWindowPlacerLocked.requestTraversal();
        continue;
      }
      Trace.traceEnd(32L);
      return;
      localConfiguration = null;
      continue;
      bool = false;
      continue;
      dispatchResized(localRect1, localRect2, localRect3, localRect4, localRect5, localRect6, bool, localConfiguration);
    }
  }
  
  void requestUpdateWallpaperIfNeeded()
  {
    if ((this.mDisplayContent != null) && ((this.mAttrs.flags & 0x100000) != 0))
    {
      DisplayContent localDisplayContent = this.mDisplayContent;
      localDisplayContent.pendingLayoutChanges |= 0x4;
      this.mDisplayContent.layoutNeeded = true;
      this.mService.mWindowPlacerLocked.requestTraversal();
    }
  }
  
  void resetDragResizingChangeReported()
  {
    this.mDragResizingChangeReported = false;
  }
  
  void resetJustMovedInStack()
  {
    this.mJustMovedInStack = false;
  }
  
  void resetReplacing()
  {
    this.mWillReplaceWindow = false;
    this.mReplacingWindow = null;
    this.mAnimateReplacingWindow = false;
  }
  
  void restoreSavedSurface()
  {
    if (!this.mSurfaceSaved) {
      return;
    }
    if (this.mLastVisibleLayoutRotation != this.mService.mRotation)
    {
      destroySavedSurface();
      return;
    }
    this.mSurfaceSaved = false;
    if (this.mWinAnimator.mSurfaceController != null)
    {
      setHasSurface(true);
      this.mWinAnimator.mDrawState = 3;
      this.mAnimatingWithSavedSurface = true;
      if ((WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS) || (WindowManagerDebugConfig.DEBUG_ANIM)) {
        Slog.v(TAG, "Restoring saved surface: " + this);
      }
      return;
    }
    Slog.wtf(TAG, "Failed to restore saved surface: surface gone! " + this);
  }
  
  void scheduleAnimationIfDimming()
  {
    if (this.mDisplayContent == null) {
      return;
    }
    DimLayer.DimLayerUser localDimLayerUser = getDimLayerUser();
    if ((localDimLayerUser != null) && (this.mDisplayContent.mDimLayerController.isDimming(localDimLayerUser, this.mWinAnimator))) {
      this.mService.scheduleAnimationLocked();
    }
  }
  
  public void setAppOpVisibilityLw(boolean paramBoolean)
  {
    if (this.mAppOpVisibility != paramBoolean)
    {
      this.mAppOpVisibility = paramBoolean;
      if (paramBoolean) {
        showLw(true, true);
      }
    }
    else
    {
      return;
    }
    hideLw(true, true);
  }
  
  void setDisplayLayoutNeeded()
  {
    if (this.mDisplayContent != null) {
      this.mDisplayContent.layoutNeeded = true;
    }
  }
  
  void setDragResizing()
  {
    boolean bool = computeDragResizing();
    if (bool == this.mDragResizing) {
      return;
    }
    this.mDragResizing = bool;
    Task localTask = getTask();
    if ((localTask != null) && (localTask.isDragResizing()))
    {
      this.mResizeMode = localTask.getDragResizeMode();
      return;
    }
    if ((this.mDragResizing) && (this.mDisplayContent.mDividerControllerLocked.isResizing())) {}
    for (int i = 1;; i = 0)
    {
      this.mResizeMode = i;
      return;
    }
  }
  
  void setHasSurface(boolean paramBoolean)
  {
    this.mHasSurface = paramBoolean;
  }
  
  void setReplacing(boolean paramBoolean)
  {
    if (((this.mAttrs.privateFlags & 0x8000) != 0) || (this.mAttrs.type == 3)) {
      return;
    }
    this.mWillReplaceWindow = true;
    this.mReplacingWindow = null;
    this.mAnimateReplacingWindow = paramBoolean;
  }
  
  boolean setReportResizeHints()
  {
    boolean bool2 = false;
    boolean bool4 = true;
    boolean bool3 = this.mOverscanInsetsChanged;
    if (this.mLastOverscanInsets.equals(this.mOverscanInsets))
    {
      bool1 = false;
      this.mOverscanInsetsChanged = (bool1 | bool3);
      bool3 = this.mContentInsetsChanged;
      if (!this.mLastContentInsets.equals(this.mContentInsets)) {
        break label247;
      }
      bool1 = false;
      label54:
      this.mContentInsetsChanged = (bool1 | bool3);
      bool3 = this.mVisibleInsetsChanged;
      if (!this.mLastVisibleInsets.equals(this.mVisibleInsets)) {
        break label252;
      }
      bool1 = false;
      label82:
      this.mVisibleInsetsChanged = (bool1 | bool3);
      bool3 = this.mStableInsetsChanged;
      if (!this.mLastStableInsets.equals(this.mStableInsets)) {
        break label257;
      }
      bool1 = false;
      label110:
      this.mStableInsetsChanged = (bool1 | bool3);
      bool3 = this.mOutsetsChanged;
      if (!this.mLastOutsets.equals(this.mOutsets)) {
        break label262;
      }
      bool1 = false;
      label138:
      this.mOutsetsChanged = (bool1 | bool3);
      bool3 = this.mFrameSizeChanged;
      if (this.mLastFrame.width() != this.mFrame.width()) {
        break label267;
      }
      bool1 = bool2;
      if (this.mLastFrame.height() == this.mFrame.height()) {}
    }
    label247:
    label252:
    label257:
    label262:
    label267:
    for (boolean bool1 = true;; bool1 = true)
    {
      this.mFrameSizeChanged = (bool3 | bool1);
      bool3 = bool4;
      if (!this.mOverscanInsetsChanged)
      {
        bool3 = bool4;
        if (!this.mContentInsetsChanged)
        {
          bool3 = bool4;
          if (!this.mVisibleInsetsChanged)
          {
            bool3 = bool4;
            if (!this.mOutsetsChanged) {
              bool3 = this.mFrameSizeChanged;
            }
          }
        }
      }
      return bool3;
      bool1 = true;
      break;
      bool1 = true;
      break label54;
      bool1 = true;
      break label82;
      bool1 = true;
      break label110;
      bool1 = true;
      break label138;
    }
  }
  
  void setRequestedSize(int paramInt1, int paramInt2)
  {
    if ((this.mRequestedWidth != paramInt1) || (this.mRequestedHeight != paramInt2))
    {
      this.mLayoutNeeded = true;
      this.mRequestedWidth = paramInt1;
      this.mRequestedHeight = paramInt2;
    }
  }
  
  void setResizedWhileNotDragResizing(boolean paramBoolean)
  {
    this.mResizedWhileNotDragResizing = paramBoolean;
    if (paramBoolean) {}
    for (paramBoolean = false;; paramBoolean = true)
    {
      this.mResizedWhileNotDragResizingReported = paramBoolean;
      return;
    }
  }
  
  public void setShowToOwnerOnlyLocked(boolean paramBoolean)
  {
    this.mShowToOwnerOnly = paramBoolean;
  }
  
  public void setVisibleBeforeClientHidden()
  {
    boolean bool2 = this.mWasVisibleBeforeClientHidden;
    if (this.mViewVisibility != 0) {}
    for (boolean bool1 = this.mAnimatingWithSavedSurface;; bool1 = true)
    {
      this.mWasVisibleBeforeClientHidden = (bool1 | bool2);
      return;
    }
  }
  
  void setWindowScale(int paramInt1, int paramInt2)
  {
    int i = 0;
    float f2 = 1.0F;
    if ((this.mAttrs.flags & 0x4000) != 0) {
      i = 1;
    }
    if (i != 0)
    {
      if (this.mAttrs.width != paramInt1) {}
      for (float f1 = this.mAttrs.width / paramInt1;; f1 = 1.0F)
      {
        this.mHScale = f1;
        f1 = f2;
        if (this.mAttrs.height != paramInt2) {
          f1 = this.mAttrs.height / paramInt2;
        }
        this.mVScale = f1;
        return;
      }
    }
    this.mVScale = 1.0F;
    this.mHScale = 1.0F;
  }
  
  boolean shouldBeReplacedWithChildren()
  {
    if ((isChildWindow()) || (this.mAttrs.type == 2)) {}
    while (this.mAttrs.type == 4) {
      return true;
    }
    return false;
  }
  
  boolean shouldKeepVisibleDeadAppWindow()
  {
    boolean bool = false;
    if ((!isWinVisibleLw()) || (this.mAppToken == null)) {}
    while (this.mAppToken.clientHidden) {
      return false;
    }
    if (this.mAttrs.token != this.mClient.asBinder()) {
      return false;
    }
    if (this.mAttrs.type == 3) {
      return false;
    }
    TaskStack localTaskStack = getStack();
    if (localTaskStack != null) {
      bool = ActivityManager.StackId.keepVisibleDeadAppWindowOnScreen(localTaskStack.mStackId);
    }
    return bool;
  }
  
  public boolean showLw(boolean paramBoolean)
  {
    return showLw(paramBoolean, true);
  }
  
  boolean showLw(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (isHiddenFromUserLocked()) {
      return false;
    }
    if (!this.mAppOpVisibility) {
      return false;
    }
    if (this.mPermanentlyHidden) {
      return false;
    }
    if ((this.mPolicyVisibility) && (this.mPolicyVisibilityAfterAnim)) {
      return false;
    }
    if (WindowManagerDebugConfig.DEBUG_VISIBILITY) {
      Slog.v(TAG, "Policy visibility true: " + this);
    }
    boolean bool = paramBoolean1;
    if (paramBoolean1)
    {
      if (WindowManagerDebugConfig.DEBUG_VISIBILITY) {
        Slog.v(TAG, "doAnimation: mPolicyVisibility=" + this.mPolicyVisibility + " mAnimation=" + this.mWinAnimator.mAnimation);
      }
      if (this.mService.okToDisplay()) {
        break label282;
      }
      bool = false;
    }
    for (;;)
    {
      if ((this.mAttrs != null) && (this.mAttrs.type == 2000))
      {
        paramBoolean1 = isFrameFullscreen(getDisplayContent().getDisplayInfo());
        Slog.v(TAG, "doAnimation: isFull=" + paramBoolean1 + " win=" + this);
        if ((this.mService.mCurrentFocus != this) && (paramBoolean1))
        {
          this.mService.enableScreenIfNeededLocked();
          this.mService.mFocusMayChange = true;
        }
      }
      this.mPolicyVisibility = true;
      this.mPolicyVisibilityAfterAnim = true;
      if (bool) {
        this.mWinAnimator.applyAnimationLocked(1, true);
      }
      if (paramBoolean2) {
        this.mService.scheduleAnimationLocked();
      }
      return true;
      label282:
      bool = paramBoolean1;
      if (this.mPolicyVisibility)
      {
        bool = paramBoolean1;
        if (this.mWinAnimator.mAnimation == null) {
          bool = false;
        }
      }
    }
  }
  
  public String toString()
  {
    Object localObject = getWindowTag();
    StringBuilder localStringBuilder;
    if ((this.mStringNameCache == null) || (this.mLastTitle != localObject))
    {
      this.mLastTitle = ((CharSequence)localObject);
      this.mWasExiting = this.mAnimatingExit;
      localStringBuilder = new StringBuilder().append("Window{").append(Integer.toHexString(System.identityHashCode(this))).append(" u").append(UserHandle.getUserId(this.mSession.mUid)).append(" ").append(this.mLastTitle);
      if (!this.mAnimatingExit) {
        break label131;
      }
    }
    label131:
    for (localObject = " EXITING}";; localObject = "}")
    {
      this.mStringNameCache = ((String)localObject);
      do
      {
        return this.mStringNameCache;
      } while (this.mWasExiting == this.mAnimatingExit);
      break;
    }
  }
  
  void transferDimToReplacement()
  {
    boolean bool = false;
    DimLayer.DimLayerUser localDimLayerUser = getDimLayerUser();
    if ((localDimLayerUser != null) && (this.mDisplayContent != null))
    {
      DimLayerController localDimLayerController = this.mDisplayContent.mDimLayerController;
      WindowStateAnimator localWindowStateAnimator = this.mReplacingWindow.mWinAnimator;
      if ((this.mAttrs.flags & 0x2) != 0) {
        bool = true;
      }
      localDimLayerController.applyDim(localDimLayerUser, localWindowStateAnimator, bool);
    }
  }
  
  void transformClipRectFromScreenToSurfaceSpace(Rect paramRect)
  {
    if (this.mHScale >= 0.0F)
    {
      paramRect.left = ((int)(paramRect.left / this.mHScale));
      paramRect.right = ((int)Math.ceil(paramRect.right / this.mHScale));
    }
    if (this.mVScale >= 0.0F)
    {
      paramRect.top = ((int)(paramRect.top / this.mVScale));
      paramRect.bottom = ((int)Math.ceil(paramRect.bottom / this.mVScale));
    }
  }
  
  float translateToWindowX(float paramFloat)
  {
    float f = paramFloat - this.mFrame.left;
    paramFloat = f;
    if (this.mEnforceSizeCompat) {
      paramFloat = f * this.mGlobalScale;
    }
    return paramFloat;
  }
  
  float translateToWindowY(float paramFloat)
  {
    float f = paramFloat - this.mFrame.top;
    paramFloat = f;
    if (this.mEnforceSizeCompat) {
      paramFloat = f * this.mGlobalScale;
    }
    return paramFloat;
  }
  
  public void unregisterFocusObserver(IWindowFocusObserver paramIWindowFocusObserver)
  {
    synchronized (this.mService.mWindowMap)
    {
      if (this.mFocusCallbacks != null) {
        this.mFocusCallbacks.unregister(paramIWindowFocusObserver);
      }
      return;
    }
  }
  
  public boolean wasVisibleBeforeClientHidden()
  {
    return this.mWasVisibleBeforeClientHidden;
  }
  
  private final class DeadWindowEventReceiver
    extends InputEventReceiver
  {
    DeadWindowEventReceiver(InputChannel paramInputChannel)
    {
      super(WindowState.this.mService.mH.getLooper());
    }
    
    public void onInputEvent(InputEvent paramInputEvent)
    {
      finishInputEvent(paramInputEvent, true);
    }
  }
  
  private class DeathRecipient
    implements IBinder.DeathRecipient
  {
    private DeathRecipient() {}
    
    public void binderDied()
    {
      try
      {
        synchronized (WindowState.this.mService.mWindowMap)
        {
          Object localObject1 = WindowState.this.mService.windowForClientLocked(WindowState.this.mSession, WindowState.this.mClient, false);
          Slog.i(WindowState.TAG, "WIN DEATH: " + localObject1);
          if (localObject1 != null)
          {
            WindowState.this.mService.mPolicy.notifyAppLaunchFailedLw(((WindowState)localObject1).getOwningPackage());
            WindowState.this.mService.removeWindowLocked((WindowState)localObject1, WindowState.this.shouldKeepVisibleDeadAppWindow());
            if (((WindowState)localObject1).mAttrs.type == 2034)
            {
              localObject1 = (TaskStack)WindowState.this.mService.mStackIdToStack.get(3);
              if (localObject1 != null) {
                ((TaskStack)localObject1).resetDockedStackToMiddle();
              }
              WindowState.this.mService.setDockedStackResizing(false);
            }
          }
          while (!WindowState.this.mHasSurface) {
            return;
          }
          Slog.e(WindowState.TAG, "!!! LEAK !!! Window removed but surface still valid.");
          WindowState.this.mService.removeWindowLocked(WindowState.this);
        }
        return;
      }
      catch (IllegalArgumentException localIllegalArgumentException) {}
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/WindowState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */