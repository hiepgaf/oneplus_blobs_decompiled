package com.android.server.wm;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.ArraySet;
import android.util.DisplayMetrics;
import android.util.Slog;
import android.util.SparseArray;
import android.view.DisplayInfo;
import android.view.IDockedStackListener;
import android.view.SurfaceControl;
import android.view.WindowManagerPolicy;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import android.view.inputmethod.InputMethodManagerInternal;
import com.android.internal.policy.DividerSnapAlgorithm;
import com.android.internal.policy.DividerSnapAlgorithm.SnapTarget;
import com.android.internal.policy.DockedDividerUtils;
import com.android.server.LocalServices;
import java.io.PrintWriter;
import java.util.ArrayList;

public class DockedStackDividerController
  implements DimLayer.DimLayerUser
{
  private static final float CLIP_REVEAL_MEET_EARLIEST = 0.6F;
  private static final float CLIP_REVEAL_MEET_FRACTION_MAX = 0.8F;
  private static final float CLIP_REVEAL_MEET_FRACTION_MIN = 0.4F;
  private static final float CLIP_REVEAL_MEET_LAST = 1.0F;
  private static final int DIVIDER_WIDTH_INACTIVE_DP = 4;
  private static final long IME_ADJUST_ANIM_DURATION = 280L;
  private static final long IME_ADJUST_DRAWN_TIMEOUT = 200L;
  private static final Interpolator IME_ADJUST_ENTRY_INTERPOLATOR = new PathInterpolator(0.2F, 0.0F, 0.1F, 1.0F);
  private static final String TAG = "WindowManager";
  private boolean mAdjustedForDivider;
  private boolean mAdjustedForIme;
  private boolean mAnimatingForIme;
  private boolean mAnimatingForMinimizedDockedStack;
  private long mAnimationDuration;
  private float mAnimationStart;
  private boolean mAnimationStartDelayed;
  private long mAnimationStartTime;
  private boolean mAnimationStarted;
  private float mAnimationTarget;
  private WindowState mDelayedImeWin;
  private final DimLayer mDimLayer;
  private final DisplayContent mDisplayContent;
  private float mDividerAnimationStart;
  private float mDividerAnimationTarget;
  private int mDividerInsets;
  private int mDividerWindowWidth;
  private int mDividerWindowWidthInactive;
  private final RemoteCallbackList<IDockedStackListener> mDockedStackListeners = new RemoteCallbackList();
  private int mImeHeight;
  private boolean mImeHideRequested;
  private float mLastAnimationProgress;
  private float mLastDividerProgress;
  private final Rect mLastRect = new Rect();
  private boolean mLastVisibility = false;
  private float mMaximizeMeetFraction;
  private boolean mMinimizedDock;
  private final Interpolator mMinimizedDockInterpolator;
  private boolean mResizing;
  private final WindowManagerService mService;
  private final DividerSnapAlgorithm[] mSnapAlgorithmForRotation = new DividerSnapAlgorithm[4];
  private final Rect mTmpRect = new Rect();
  private final Rect mTmpRect2 = new Rect();
  private final Rect mTmpRect3 = new Rect();
  private final Rect mTouchRegion = new Rect();
  private WindowState mWindow;
  
  DockedStackDividerController(WindowManagerService paramWindowManagerService, DisplayContent paramDisplayContent)
  {
    this.mService = paramWindowManagerService;
    this.mDisplayContent = paramDisplayContent;
    paramWindowManagerService = paramWindowManagerService.mContext;
    this.mDimLayer = new DimLayer(paramDisplayContent.mService, this, paramDisplayContent.getDisplayId(), "DockedStackDim");
    this.mMinimizedDockInterpolator = AnimationUtils.loadInterpolator(paramWindowManagerService, 17563661);
    loadDimens();
  }
  
  private float adjustMaximizeAmount(TaskStack paramTaskStack, float paramFloat1, float paramFloat2)
  {
    if (this.mMaximizeMeetFraction == 1.0F) {
      return paramFloat2;
    }
    int i = paramTaskStack.getMinimizeDistance();
    float f1 = this.mService.mAppTransition.getLastClipRevealMaxTranslation() / i;
    float f2 = this.mAnimationTarget;
    float f3 = Math.min(paramFloat1 / this.mMaximizeMeetFraction, 1.0F);
    return (f2 * paramFloat1 + (1.0F - paramFloat1) * f1) * f3 + (1.0F - f3) * paramFloat2;
  }
  
  private boolean animateForIme(long paramLong)
  {
    if ((!this.mAnimationStarted) || (this.mAnimationStartDelayed))
    {
      this.mAnimationStarted = true;
      this.mAnimationStartTime = paramLong;
      this.mAnimationDuration = ((this.mService.getWindowAnimationScaleLocked() * 280.0F));
    }
    float f = Math.min(1.0F, (float)(paramLong - this.mAnimationStartTime) / (float)this.mAnimationDuration);
    Object localObject;
    boolean bool1;
    int i;
    label101:
    TaskStack localTaskStack;
    boolean bool2;
    if (this.mAnimationTarget == 1.0F)
    {
      localObject = IME_ADJUST_ENTRY_INTERPOLATOR;
      f = ((Interpolator)localObject).getInterpolation(f);
      localObject = this.mDisplayContent.getStacks();
      bool1 = false;
      i = ((ArrayList)localObject).size() - 1;
      if (i < 0) {
        break label252;
      }
      localTaskStack = (TaskStack)((ArrayList)localObject).get(i);
      bool2 = bool1;
      if (localTaskStack != null)
      {
        bool2 = bool1;
        if (localTaskStack.isAdjustedForIme())
        {
          if ((f < 1.0F) || (this.mAnimationTarget != 0.0F) || (this.mDividerAnimationTarget != 0.0F)) {
            break label212;
          }
          localTaskStack.resetAdjustedForIme(true);
          bool1 = true;
        }
      }
    }
    for (;;)
    {
      bool2 = bool1;
      if (f >= 1.0F)
      {
        localTaskStack.endImeAdjustAnimation();
        bool2 = bool1;
      }
      i -= 1;
      bool1 = bool2;
      break label101;
      localObject = AppTransition.TOUCH_RESPONSE_INTERPOLATOR;
      break;
      label212:
      this.mLastAnimationProgress = getInterpolatedAnimationValue(f);
      this.mLastDividerProgress = getInterpolatedDividerValue(f);
      bool1 |= localTaskStack.updateAdjustForIme(this.mLastAnimationProgress, this.mLastDividerProgress, false);
    }
    label252:
    if (bool1) {
      this.mService.mWindowPlacerLocked.performSurfacePlacement();
    }
    if (f >= 1.0F)
    {
      this.mLastAnimationProgress = this.mAnimationTarget;
      this.mLastDividerProgress = this.mDividerAnimationTarget;
      this.mAnimatingForIme = false;
      return false;
    }
    return true;
  }
  
  private boolean animateForMinimizedDockedStack(long paramLong)
  {
    TaskStack localTaskStack = (TaskStack)this.mService.mStackIdToStack.get(3);
    long l;
    float f;
    if (!this.mAnimationStarted)
    {
      this.mAnimationStarted = true;
      this.mAnimationStartTime = paramLong;
      if (isAnimationMaximizing())
      {
        l = this.mService.mAppTransition.getLastClipRevealTransitionDuration();
        this.mAnimationDuration = (((float)l * this.mService.getTransitionAnimationScaleLocked()));
        this.mMaximizeMeetFraction = getClipRevealMeetFraction(localTaskStack);
        notifyDockedStackMinimizedChanged(this.mMinimizedDock, ((float)this.mAnimationDuration * this.mMaximizeMeetFraction));
      }
    }
    else
    {
      f = Math.min(1.0F, (float)(paramLong - this.mAnimationStartTime) / (float)this.mAnimationDuration);
      if (!isAnimationMaximizing()) {
        break label187;
      }
    }
    label187:
    for (Interpolator localInterpolator = AppTransition.TOUCH_RESPONSE_INTERPOLATOR;; localInterpolator = this.mMinimizedDockInterpolator)
    {
      f = localInterpolator.getInterpolation(f);
      if ((localTaskStack != null) && (localTaskStack.setAdjustedForMinimizedDock(getMinimizeAmount(localTaskStack, f)))) {
        this.mService.mWindowPlacerLocked.performSurfacePlacement();
      }
      if (f < 1.0F) {
        break label196;
      }
      this.mAnimatingForMinimizedDockedStack = false;
      return false;
      l = 200L;
      break;
    }
    label196:
    return true;
  }
  
  private void checkMinimizeChanged(boolean paramBoolean)
  {
    boolean bool2 = false;
    if (this.mDisplayContent.getDockedStackVisibleForUserLocked() == null) {
      return;
    }
    Object localObject = this.mDisplayContent.getHomeStack();
    if (localObject == null) {
      return;
    }
    Task localTask1 = ((TaskStack)localObject).findHomeTask();
    int j;
    int i;
    if ((localTask1 != null) && (isWithinDisplay(localTask1)))
    {
      TaskStack localTaskStack = (TaskStack)this.mService.mStackIdToStack.get(1);
      localObject = ((TaskStack)localObject).getTasks();
      Task localTask2 = (Task)((ArrayList)localObject).get(((ArrayList)localObject).size() - 1);
      if (localTask1.getTopVisibleAppToken() == null) {
        break label156;
      }
      j = 1;
      if ((localTaskStack != null) && (localTaskStack.isVisibleLocked())) {
        break label161;
      }
      if ((((ArrayList)localObject).size() <= 1) || (localTask2 == localTask1)) {
        break label166;
      }
      i = 1;
      label131:
      bool1 = bool2;
      if (j != 0) {
        if (i == 0) {
          break label171;
        }
      }
    }
    label156:
    label161:
    label166:
    label171:
    for (boolean bool1 = bool2;; bool1 = true)
    {
      setMinimizedDockedStack(bool1, paramBoolean);
      return;
      return;
      j = 0;
      break;
      i = 1;
      break label131;
      i = 0;
      break label131;
    }
  }
  
  private boolean clearImeAdjustAnimation()
  {
    boolean bool1 = false;
    ArrayList localArrayList = this.mDisplayContent.getStacks();
    int i = localArrayList.size() - 1;
    while (i >= 0)
    {
      TaskStack localTaskStack = (TaskStack)localArrayList.get(i);
      boolean bool2 = bool1;
      if (localTaskStack != null)
      {
        bool2 = bool1;
        if (localTaskStack.isAdjustedForIme())
        {
          localTaskStack.resetAdjustedForIme(true);
          bool2 = true;
        }
      }
      i -= 1;
      bool1 = bool2;
    }
    this.mAnimatingForIme = false;
    return bool1;
  }
  
  private boolean containsAppInDockedStack(ArraySet<AppWindowToken> paramArraySet)
  {
    int i = paramArraySet.size() - 1;
    while (i >= 0)
    {
      AppWindowToken localAppWindowToken = (AppWindowToken)paramArraySet.valueAt(i);
      if ((localAppWindowToken.mTask != null) && (localAppWindowToken.mTask.mStack.mStackId == 3)) {
        return true;
      }
      i -= 1;
    }
    return false;
  }
  
  private float getClipRevealMeetFraction(TaskStack paramTaskStack)
  {
    if ((!isAnimationMaximizing()) || (paramTaskStack == null)) {}
    while (!this.mService.mAppTransition.hadClipRevealAnimation()) {
      return 1.0F;
    }
    int i = paramTaskStack.getMinimizeDistance();
    return (1.0F - Math.max(0.0F, Math.min(1.0F, (Math.abs(this.mService.mAppTransition.getLastClipRevealMaxTranslation()) / i - 0.4F) / 0.4F))) * 0.39999998F + 0.6F;
  }
  
  private float getInterpolatedAnimationValue(float paramFloat)
  {
    return this.mAnimationTarget * paramFloat + (1.0F - paramFloat) * this.mAnimationStart;
  }
  
  private float getInterpolatedDividerValue(float paramFloat)
  {
    return this.mDividerAnimationTarget * paramFloat + (1.0F - paramFloat) * this.mDividerAnimationStart;
  }
  
  private float getMinimizeAmount(TaskStack paramTaskStack, float paramFloat)
  {
    float f = getInterpolatedAnimationValue(paramFloat);
    if (isAnimationMaximizing()) {
      return adjustMaximizeAmount(paramTaskStack, paramFloat, f);
    }
    return f;
  }
  
  private void initSnapAlgorithmForRotations()
  {
    Configuration localConfiguration1 = this.mService.mCurConfiguration;
    Configuration localConfiguration2 = new Configuration();
    int i = 0;
    if (i < 4)
    {
      int k;
      label37:
      int j;
      label49:
      label61:
      int m;
      label93:
      Object localObject;
      DividerSnapAlgorithm[] arrayOfDividerSnapAlgorithm;
      if ((i == 1) || (i == 3))
      {
        k = 1;
        if (k == 0) {
          break label256;
        }
        j = this.mDisplayContent.mBaseDisplayHeight;
        if (k == 0) {
          break label267;
        }
        k = this.mDisplayContent.mBaseDisplayWidth;
        this.mService.mPolicy.getStableInsetsLw(i, j, k, this.mTmpRect);
        localConfiguration2.setToDefaults();
        if (j > k) {
          break label278;
        }
        m = 1;
        localConfiguration2.orientation = m;
        localConfiguration2.screenWidthDp = ((int)(this.mService.mPolicy.getConfigDisplayWidth(j, k, i, localConfiguration1.uiMode) / this.mDisplayContent.getDisplayMetrics().density));
        localConfiguration2.screenHeightDp = ((int)(this.mService.mPolicy.getConfigDisplayHeight(j, k, i, localConfiguration1.uiMode) / this.mDisplayContent.getDisplayMetrics().density));
        localObject = this.mService.mContext.createConfigurationContext(localConfiguration2);
        arrayOfDividerSnapAlgorithm = this.mSnapAlgorithmForRotation;
        localObject = ((Context)localObject).getResources();
        m = getContentWidth();
        if (localConfiguration2.orientation != 1) {
          break label284;
        }
      }
      label256:
      label267:
      label278:
      label284:
      for (boolean bool = true;; bool = false)
      {
        arrayOfDividerSnapAlgorithm[i] = new DividerSnapAlgorithm((Resources)localObject, j, k, m, bool, this.mTmpRect);
        i += 1;
        break;
        k = 0;
        break label37;
        j = this.mDisplayContent.mBaseDisplayWidth;
        break label49;
        k = this.mDisplayContent.mBaseDisplayHeight;
        break label61;
        m = 2;
        break label93;
      }
    }
  }
  
  private boolean isAnimationMaximizing()
  {
    return this.mAnimationTarget == 0.0F;
  }
  
  private boolean isWithinDisplay(Task paramTask)
  {
    paramTask.mStack.getBounds(this.mTmpRect);
    this.mDisplayContent.getLogicalDisplayRect(this.mTmpRect2);
    return this.mTmpRect.intersect(this.mTmpRect2);
  }
  
  private void loadDimens()
  {
    Context localContext = this.mService.mContext;
    this.mDividerWindowWidth = localContext.getResources().getDimensionPixelSize(17104931);
    this.mDividerInsets = localContext.getResources().getDimensionPixelSize(17104932);
    this.mDividerWindowWidthInactive = WindowManagerService.dipToPixel(4, this.mDisplayContent.getDisplayMetrics());
    initSnapAlgorithmForRotations();
  }
  
  private void resetDragResizingChangeReported()
  {
    WindowList localWindowList = this.mDisplayContent.getWindowList();
    int i = localWindowList.size() - 1;
    while (i >= 0)
    {
      ((WindowState)localWindowList.get(i)).resetDragResizingChangeReported();
      i -= 1;
    }
  }
  
  private void setMinimizedDockedStack(boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool1 = this.mMinimizedDock;
    this.mMinimizedDock = paramBoolean1;
    if (paramBoolean1 == bool1) {
      return;
    }
    boolean bool2 = clearImeAdjustAnimation();
    bool1 = false;
    if (paramBoolean1) {
      if (paramBoolean2)
      {
        startAdjustAnimation(0.0F, 1.0F);
        paramBoolean2 = bool1;
        if ((bool2) || (paramBoolean2)) {
          if ((bool2) && (!paramBoolean2)) {
            break label102;
          }
        }
      }
    }
    for (;;)
    {
      this.mService.mWindowPlacerLocked.performSurfacePlacement();
      return;
      paramBoolean2 = setMinimizedDockedStack(true);
      break;
      if (paramBoolean2)
      {
        startAdjustAnimation(1.0F, 0.0F);
        paramBoolean2 = bool1;
        break;
      }
      paramBoolean2 = setMinimizedDockedStack(false);
      break;
      label102:
      Slog.d(TAG, "setMinimizedDockedStack: IME adjust changed due to minimizing, minimizedDock=" + paramBoolean1 + " minimizedChange=" + paramBoolean2);
    }
  }
  
  private boolean setMinimizedDockedStack(boolean paramBoolean)
  {
    TaskStack localTaskStack = this.mDisplayContent.getDockedStackVisibleForUserLocked();
    notifyDockedStackMinimizedChanged(paramBoolean, 0L);
    if (localTaskStack != null)
    {
      if (paramBoolean) {}
      for (float f = 1.0F;; f = 0.0F) {
        return localTaskStack.setAdjustedForMinimizedDock(f);
      }
    }
    return false;
  }
  
  private void startAdjustAnimation(float paramFloat1, float paramFloat2)
  {
    this.mAnimatingForMinimizedDockedStack = true;
    this.mAnimationStarted = false;
    this.mAnimationStart = paramFloat1;
    this.mAnimationTarget = paramFloat2;
  }
  
  private void startImeAdjustAnimation(boolean paramBoolean1, boolean paramBoolean2, WindowState paramWindowState)
  {
    int j = 0;
    boolean bool = true;
    if (!this.mAnimatingForIme) {
      if (this.mAdjustedForIme)
      {
        i = 1;
        this.mAnimationStart = i;
        if (!this.mAdjustedForDivider) {
          break label176;
        }
        i = 1;
        label40:
        this.mDividerAnimationStart = i;
        this.mLastAnimationProgress = this.mAnimationStart;
        this.mLastDividerProgress = this.mDividerAnimationStart;
        label63:
        this.mAnimatingForIme = true;
        this.mAnimationStarted = false;
        if (!paramBoolean1) {
          break label201;
        }
      }
    }
    label176:
    label201:
    for (int i = 1;; i = 0)
    {
      this.mAnimationTarget = i;
      i = j;
      if (paramBoolean2) {
        i = 1;
      }
      this.mDividerAnimationTarget = i;
      ArrayList localArrayList = this.mDisplayContent.getStacks();
      i = localArrayList.size() - 1;
      while (i >= 0)
      {
        TaskStack localTaskStack = (TaskStack)localArrayList.get(i);
        if ((localTaskStack.isVisibleLocked()) && (localTaskStack.isAdjustedForIme())) {
          localTaskStack.beginImeAdjustAnimation();
        }
        i -= 1;
      }
      i = 0;
      break;
      i = 0;
      break label40;
      this.mAnimationStart = this.mLastAnimationProgress;
      this.mDividerAnimationStart = this.mLastDividerProgress;
      break label63;
    }
    if (!this.mService.mWaitingForDrawn.isEmpty())
    {
      this.mService.mH.removeMessages(24);
      this.mService.mH.sendEmptyMessageDelayed(24, 200L);
      this.mAnimationStartDelayed = true;
      if (paramWindowState != null)
      {
        if (this.mDelayedImeWin != null) {
          this.mDelayedImeWin.mWinAnimator.endDelayingAnimationStart();
        }
        this.mDelayedImeWin = paramWindowState;
        paramWindowState.mWinAnimator.startDelayingAnimationStart();
      }
      paramWindowState = this.mService.mWaitingForDrawnCallback;
      this.mService.mWaitingForDrawnCallback = new -void_startImeAdjustAnimation_boolean_adjustedForIme_boolean_adjustedForDivider_com_android_server_wm_WindowState_imeWin_LambdaImpl0(paramBoolean1, paramBoolean2, paramWindowState);
      return;
    }
    if (!paramBoolean1) {
      bool = paramBoolean2;
    }
    notifyAdjustedForImeChanged(bool, 280L);
  }
  
  public boolean animate(long paramLong)
  {
    if (this.mWindow == null) {
      return false;
    }
    if (this.mAnimatingForMinimizedDockedStack) {
      return animateForMinimizedDockedStack(paramLong);
    }
    if (this.mAnimatingForIme) {
      return animateForIme(paramLong);
    }
    if ((this.mDimLayer != null) && (this.mDimLayer.isDimming())) {
      this.mDimLayer.setLayer(this.mService.mLayersController.getResizeDimLayer());
    }
    return false;
  }
  
  public boolean dimFullscreen()
  {
    return false;
  }
  
  void dump(String paramString, PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println(paramString + "DockedStackDividerController");
    paramPrintWriter.println(paramString + "  mLastVisibility=" + this.mLastVisibility);
    paramPrintWriter.println(paramString + "  mMinimizedDock=" + this.mMinimizedDock);
    paramPrintWriter.println(paramString + "  mAdjustedForIme=" + this.mAdjustedForIme);
    paramPrintWriter.println(paramString + "  mAdjustedForDivider=" + this.mAdjustedForDivider);
    if (this.mDimLayer.isDimming())
    {
      paramPrintWriter.println(paramString + "  Dim layer is dimming: ");
      this.mDimLayer.printTo(paramString + "    ", paramPrintWriter);
    }
  }
  
  int getContentInsets()
  {
    return this.mDividerInsets;
  }
  
  int getContentWidth()
  {
    return this.mDividerWindowWidth - this.mDividerInsets * 2;
  }
  
  int getContentWidthInactive()
  {
    return this.mDividerWindowWidthInactive;
  }
  
  public void getDimBounds(Rect paramRect) {}
  
  public DisplayInfo getDisplayInfo()
  {
    return this.mDisplayContent.getDisplayInfo();
  }
  
  int getImeHeightAdjustedFor()
  {
    return this.mImeHeight;
  }
  
  int getSmallestWidthDpForBounds(Rect paramRect)
  {
    DisplayInfo localDisplayInfo = this.mDisplayContent.getDisplayInfo();
    if ((paramRect == null) || ((paramRect.left == 0) && (paramRect.top == 0) && (paramRect.right == localDisplayInfo.logicalWidth) && (paramRect.bottom == localDisplayInfo.logicalHeight))) {
      return this.mService.mCurConfiguration.smallestScreenWidthDp;
    }
    int i = this.mDisplayContent.mBaseDisplayWidth;
    int j = this.mDisplayContent.mBaseDisplayHeight;
    int m = Integer.MAX_VALUE;
    int k = 0;
    if (k < 4)
    {
      this.mTmpRect.set(paramRect);
      this.mDisplayContent.rotateBounds(localDisplayInfo.rotation, k, this.mTmpRect);
      int i1;
      if ((k == 1) || (k == 3))
      {
        i1 = 1;
        label133:
        Rect localRect = this.mTmpRect2;
        if (i1 == 0) {
          break label338;
        }
        n = j;
        label147:
        if (i1 == 0) {
          break label344;
        }
        i1 = i;
        label155:
        localRect.set(0, 0, n, i1);
        if (this.mTmpRect2.width() > this.mTmpRect2.height()) {
          break label350;
        }
      }
      label338:
      label344:
      label350:
      for (int n = 1;; n = 2)
      {
        n = TaskStack.getDockSideUnchecked(this.mTmpRect, this.mTmpRect2, n);
        i1 = DockedDividerUtils.calculatePositionForBounds(this.mTmpRect, n, getContentWidth());
        DockedDividerUtils.calculateBoundsForPosition(this.mSnapAlgorithmForRotation[k].calculateNonDismissingSnapTarget(i1).position, n, this.mTmpRect, this.mTmpRect2.width(), this.mTmpRect2.height(), getContentWidth());
        this.mService.mPolicy.getStableInsetsLw(k, this.mTmpRect2.width(), this.mTmpRect2.height(), this.mTmpRect3);
        this.mService.subtractInsets(this.mTmpRect2, this.mTmpRect3, this.mTmpRect);
        m = Math.min(this.mTmpRect.width(), m);
        k += 1;
        break;
        i1 = 0;
        break label133;
        n = i;
        break label147;
        i1 = j;
        break label155;
      }
    }
    return (int)(m / this.mDisplayContent.getDisplayMetrics().density);
  }
  
  void getTouchRegion(Rect paramRect)
  {
    paramRect.set(this.mTouchRegion);
    paramRect.offset(this.mWindow.getFrameLw().left, this.mWindow.getFrameLw().top);
  }
  
  WindowState getWindow()
  {
    return this.mWindow;
  }
  
  boolean isImeHideRequested()
  {
    return this.mImeHideRequested;
  }
  
  boolean isMinimizedDock()
  {
    return this.mMinimizedDock;
  }
  
  boolean isResizing()
  {
    return this.mResizing;
  }
  
  void notifyAdjustedForImeChanged(boolean paramBoolean, long paramLong)
  {
    int j = this.mDockedStackListeners.beginBroadcast();
    int i = 0;
    for (;;)
    {
      if (i < j)
      {
        IDockedStackListener localIDockedStackListener = (IDockedStackListener)this.mDockedStackListeners.getBroadcastItem(i);
        try
        {
          localIDockedStackListener.onAdjustedForImeChanged(paramBoolean, paramLong);
          i += 1;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;)
          {
            Slog.e("WindowManager", "Error delivering adjusted for ime changed event.", localRemoteException);
          }
        }
      }
    }
    this.mDockedStackListeners.finishBroadcast();
  }
  
  void notifyAppTransitionStarting(ArraySet<AppWindowToken> paramArraySet, int paramInt)
  {
    boolean bool = this.mMinimizedDock;
    checkMinimizeChanged(true);
    if ((bool) && (this.mMinimizedDock) && (containsAppInDockedStack(paramArraySet)) && (paramInt != 0)) {
      this.mService.showRecentApps(true);
    }
  }
  
  void notifyAppVisibilityChanged()
  {
    checkMinimizeChanged(false);
  }
  
  void notifyDockSideChanged(int paramInt)
  {
    int j = this.mDockedStackListeners.beginBroadcast();
    int i = 0;
    for (;;)
    {
      if (i < j)
      {
        IDockedStackListener localIDockedStackListener = (IDockedStackListener)this.mDockedStackListeners.getBroadcastItem(i);
        try
        {
          localIDockedStackListener.onDockSideChanged(paramInt);
          i += 1;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;)
          {
            Slog.e("WindowManager", "Error delivering dock side changed event.", localRemoteException);
          }
        }
      }
    }
    this.mDockedStackListeners.finishBroadcast();
  }
  
  void notifyDockedDividerVisibilityChanged(boolean paramBoolean)
  {
    int j = this.mDockedStackListeners.beginBroadcast();
    int i = 0;
    for (;;)
    {
      if (i < j)
      {
        IDockedStackListener localIDockedStackListener = (IDockedStackListener)this.mDockedStackListeners.getBroadcastItem(i);
        try
        {
          localIDockedStackListener.onDividerVisibilityChanged(paramBoolean);
          i += 1;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;)
          {
            Slog.e("WindowManager", "Error delivering divider visibility changed event.", localRemoteException);
          }
        }
      }
    }
    this.mDockedStackListeners.finishBroadcast();
  }
  
  void notifyDockedStackExistsChanged(boolean paramBoolean)
  {
    int j = this.mDockedStackListeners.beginBroadcast();
    int i = 0;
    for (;;)
    {
      if (i < j)
      {
        IDockedStackListener localIDockedStackListener = (IDockedStackListener)this.mDockedStackListeners.getBroadcastItem(i);
        try
        {
          localIDockedStackListener.onDockedStackExistsChanged(paramBoolean);
          i += 1;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;)
          {
            Slog.e("WindowManager", "Error delivering docked stack exists changed event.", localRemoteException);
          }
        }
      }
    }
    this.mDockedStackListeners.finishBroadcast();
    if (paramBoolean)
    {
      localInputMethodManagerInternal = (InputMethodManagerInternal)LocalServices.getService(InputMethodManagerInternal.class);
      if (localInputMethodManagerInternal != null)
      {
        localInputMethodManagerInternal.hideCurrentInputMethod();
        this.mImeHideRequested = true;
      }
    }
    while (!setMinimizedDockedStack(false))
    {
      InputMethodManagerInternal localInputMethodManagerInternal;
      return;
    }
    this.mService.mWindowPlacerLocked.performSurfacePlacement();
  }
  
  void notifyDockedStackMinimizedChanged(boolean paramBoolean, long paramLong)
  {
    this.mService.mH.removeMessages(53);
    Object localObject = this.mService.mH;
    int i;
    if (paramBoolean) {
      i = 1;
    }
    for (;;)
    {
      ((WindowManagerService.H)localObject).obtainMessage(53, i, 0).sendToTarget();
      int j = this.mDockedStackListeners.beginBroadcast();
      i = 0;
      label53:
      if (i < j)
      {
        localObject = (IDockedStackListener)this.mDockedStackListeners.getBroadcastItem(i);
        try
        {
          ((IDockedStackListener)localObject).onDockedStackMinimizedChanged(paramBoolean, paramLong);
          i += 1;
          break label53;
          i = 0;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;)
          {
            Slog.e("WindowManager", "Error delivering minimized dock changed event.", localRemoteException);
          }
        }
      }
    }
    this.mDockedStackListeners.finishBroadcast();
  }
  
  void onConfigurationChanged()
  {
    loadDimens();
  }
  
  void positionDockedStackedDivider(Rect paramRect)
  {
    TaskStack localTaskStack = this.mDisplayContent.getDockedStackLocked();
    if (localTaskStack == null)
    {
      paramRect.set(this.mLastRect);
      return;
    }
    localTaskStack.getDimBounds(this.mTmpRect);
    switch (localTaskStack.getDockSide())
    {
    }
    for (;;)
    {
      this.mLastRect.set(paramRect);
      return;
      paramRect.set(this.mTmpRect.right - this.mDividerInsets, paramRect.top, this.mTmpRect.right + paramRect.width() - this.mDividerInsets, paramRect.bottom);
      continue;
      paramRect.set(paramRect.left, this.mTmpRect.bottom - this.mDividerInsets, this.mTmpRect.right, this.mTmpRect.bottom + paramRect.height() - this.mDividerInsets);
      continue;
      paramRect.set(this.mTmpRect.left - paramRect.width() + this.mDividerInsets, paramRect.top, this.mTmpRect.left + this.mDividerInsets, paramRect.bottom);
      continue;
      paramRect.set(paramRect.left, this.mTmpRect.top - paramRect.height() + this.mDividerInsets, paramRect.right, this.mTmpRect.top + this.mDividerInsets);
    }
  }
  
  void reevaluateVisibility(boolean paramBoolean)
  {
    if (this.mWindow == null) {
      return;
    }
    if ((TaskStack)this.mDisplayContent.mService.mStackIdToStack.get(3) != null) {}
    for (boolean bool = true; (this.mLastVisibility != bool) || (paramBoolean); bool = false)
    {
      this.mLastVisibility = bool;
      notifyDockedDividerVisibilityChanged(bool);
      if (!bool) {
        setResizeDimLayer(false, -1, 0.0F);
      }
      return;
    }
  }
  
  void registerDockedStackListener(IDockedStackListener paramIDockedStackListener)
  {
    this.mDockedStackListeners.register(paramIDockedStackListener);
    notifyDockedDividerVisibilityChanged(wasVisible());
    if (this.mDisplayContent.mService.mStackIdToStack.get(3) != null) {}
    for (boolean bool = true;; bool = false)
    {
      notifyDockedStackExistsChanged(bool);
      notifyDockedStackMinimizedChanged(this.mMinimizedDock, 0L);
      notifyAdjustedForImeChanged(this.mAdjustedForIme, 0L);
      return;
    }
  }
  
  void resetImeHideRequested()
  {
    this.mImeHideRequested = false;
  }
  
  void setAdjustedForIme(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, WindowState paramWindowState, int paramInt)
  {
    if ((this.mAdjustedForIme != paramBoolean1) || ((paramBoolean1) && (this.mImeHeight != paramInt)))
    {
      if ((paramBoolean3) && (!this.mAnimatingForMinimizedDockedStack)) {
        break label72;
      }
      if (paramBoolean1) {
        break label83;
      }
    }
    label72:
    label83:
    for (paramBoolean3 = paramBoolean2;; paramBoolean3 = true)
    {
      notifyAdjustedForImeChanged(paramBoolean3, 0L);
      for (;;)
      {
        this.mAdjustedForIme = paramBoolean1;
        this.mImeHeight = paramInt;
        this.mAdjustedForDivider = paramBoolean2;
        do
        {
          return;
        } while (this.mAdjustedForDivider == paramBoolean2);
        break;
        startImeAdjustAnimation(paramBoolean1, paramBoolean2, paramWindowState);
      }
    }
  }
  
  void setResizeDimLayer(boolean paramBoolean, int paramInt, float paramFloat)
  {
    int i = 0;
    SurfaceControl.openTransaction();
    TaskStack localTaskStack1 = (TaskStack)this.mDisplayContent.mService.mStackIdToStack.get(paramInt);
    TaskStack localTaskStack2 = this.mDisplayContent.getDockedStackLocked();
    paramInt = i;
    if (paramBoolean)
    {
      paramInt = i;
      if (localTaskStack1 != null)
      {
        paramInt = i;
        if (localTaskStack2 != null) {
          paramInt = 1;
        }
      }
    }
    i = paramInt;
    if (paramInt != 0)
    {
      localTaskStack1.getDimBounds(this.mTmpRect);
      if ((this.mTmpRect.height() <= 0) || (this.mTmpRect.width() <= 0)) {
        break label144;
      }
      this.mDimLayer.setBounds(this.mTmpRect);
      this.mDimLayer.show(this.mService.mLayersController.getResizeDimLayer(), paramFloat, 0L);
    }
    label144:
    for (i = paramInt;; i = 0)
    {
      if (i == 0) {
        this.mDimLayer.hide();
      }
      SurfaceControl.closeTransaction();
      return;
    }
  }
  
  void setResizing(boolean paramBoolean)
  {
    if (this.mResizing != paramBoolean)
    {
      this.mResizing = paramBoolean;
      resetDragResizingChangeReported();
    }
  }
  
  void setTouchRegion(Rect paramRect)
  {
    this.mTouchRegion.set(paramRect);
  }
  
  void setWindow(WindowState paramWindowState)
  {
    this.mWindow = paramWindowState;
    reevaluateVisibility(false);
  }
  
  public String toShortString()
  {
    return TAG;
  }
  
  boolean wasVisible()
  {
    return this.mLastVisibility;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/DockedStackDividerController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */