package com.android.server.wm;

import android.app.ActivityManager.StackId;
import android.app.IActivityManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Debug;
import android.os.Message;
import android.os.RemoteException;
import android.util.EventLog;
import android.util.Slog;
import android.util.SparseArray;
import android.view.DisplayInfo;
import android.view.SurfaceControl;
import android.view.WindowManagerPolicy;
import android.view.animation.Animation;
import com.android.internal.policy.DividerSnapAlgorithm;
import com.android.internal.policy.DividerSnapAlgorithm.SnapTarget;
import com.android.internal.policy.DockedDividerUtils;
import java.io.PrintWriter;
import java.util.ArrayList;

public class TaskStack
  implements DimLayer.DimLayerUser, BoundsAnimationController.AnimateBoundsUser
{
  private static final float ADJUSTED_STACK_FRACTION_MIN = 0.3F;
  private static final float IME_ADJUST_DIM_AMOUNT = 0.25F;
  private float mAdjustDividerAmount;
  private float mAdjustImeAmount;
  private final Rect mAdjustedBounds = new Rect();
  private boolean mAdjustedForIme;
  WindowStateAnimator mAnimationBackgroundAnimator;
  DimLayer mAnimationBackgroundSurface;
  private Rect mBounds = new Rect();
  private final Rect mBoundsAfterRotation = new Rect();
  private boolean mBoundsAnimating = false;
  boolean mDeferDetach;
  int mDensity;
  private DisplayContent mDisplayContent;
  private final int mDockedStackMinimizeThickness;
  private boolean mDragResizing;
  final AppTokenList mExitingAppTokens = new AppTokenList();
  private boolean mFullscreen = true;
  private final Rect mFullyAdjustedImeBounds = new Rect();
  private boolean mImeGoingAway;
  private WindowState mImeWin;
  private float mMinimizeAmount;
  int mRotation;
  private final WindowManagerService mService;
  final int mStackId;
  private final ArrayList<Task> mTasks = new ArrayList();
  private final Rect mTmpAdjustedBounds = new Rect();
  private Rect mTmpRect = new Rect();
  private Rect mTmpRect2 = new Rect();
  
  TaskStack(WindowManagerService paramWindowManagerService, int paramInt)
  {
    this.mService = paramWindowManagerService;
    this.mStackId = paramInt;
    this.mDockedStackMinimizeThickness = paramWindowManagerService.mContext.getResources().getDimensionPixelSize(17104933);
    EventLog.writeEvent(31004, paramInt);
  }
  
  private boolean adjustForIME(WindowState paramWindowState)
  {
    int j = getDockSide();
    int i;
    Rect localRect1;
    int m;
    int k;
    if ((j == 2) || (j == 4))
    {
      i = 1;
      if ((paramWindowState == null) || (i == 0)) {
        break label227;
      }
      localRect1 = this.mTmpRect;
      Rect localRect2 = this.mTmpRect2;
      getDisplayContent().getContentRect(localRect1);
      localRect2.set(localRect1);
      i = Math.max(paramWindowState.getFrameLw().top, localRect2.top) + paramWindowState.getGivenContentInsetsLw().top;
      if (localRect2.bottom > i) {
        localRect2.bottom = i;
      }
      m = localRect1.bottom - localRect2.bottom;
      i = getDisplayContent().mDividerControllerLocked.getContentWidth();
      k = getDisplayContent().mDividerControllerLocked.getContentWidthInactive();
      if (j != 2) {
        break label229;
      }
      j = getMinTopStackBottom(localRect1, this.mBounds.bottom);
      i = Math.max(this.mBounds.bottom - m + i - k, j);
      this.mTmpAdjustedBounds.set(this.mBounds);
      this.mTmpAdjustedBounds.bottom = ((int)(this.mAdjustImeAmount * i + (1.0F - this.mAdjustImeAmount) * this.mBounds.bottom));
      this.mFullyAdjustedImeBounds.set(this.mBounds);
    }
    for (;;)
    {
      return true;
      i = 0;
      break;
      label227:
      return false;
      label229:
      j = this.mBounds.top;
      int n = getMinTopStackBottom(localRect1, this.mBounds.top - i);
      m = Math.max(this.mBounds.top - m, n + k);
      this.mTmpAdjustedBounds.set(this.mBounds);
      this.mTmpAdjustedBounds.top = (this.mBounds.top + (int)(this.mAdjustImeAmount * (m - (j - i + k)) + this.mAdjustDividerAmount * (k - i)));
      this.mFullyAdjustedImeBounds.set(this.mBounds);
      this.mFullyAdjustedImeBounds.top = m;
      this.mFullyAdjustedImeBounds.bottom = (this.mBounds.height() + m);
    }
  }
  
  private boolean adjustForMinimizedDockedStack(float paramFloat)
  {
    int i = getDockSide();
    if ((i != -1) || (this.mTmpAdjustedBounds.isEmpty()))
    {
      if (i != 2) {
        break label84;
      }
      this.mService.getStableInsetsLocked(this.mTmpRect);
      i = this.mTmpRect.top;
      this.mTmpAdjustedBounds.set(this.mBounds);
      this.mTmpAdjustedBounds.bottom = ((int)(i * paramFloat + (1.0F - paramFloat) * this.mBounds.bottom));
    }
    label84:
    do
    {
      return true;
      return false;
      if (i == 1)
      {
        this.mTmpAdjustedBounds.set(this.mBounds);
        i = this.mBounds.width();
        this.mTmpAdjustedBounds.right = ((int)(this.mDockedStackMinimizeThickness * paramFloat + (1.0F - paramFloat) * this.mBounds.right));
        this.mTmpAdjustedBounds.left = (this.mTmpAdjustedBounds.right - i);
        return true;
      }
    } while (i != 3);
    this.mTmpAdjustedBounds.set(this.mBounds);
    this.mTmpAdjustedBounds.left = ((int)((this.mBounds.right - this.mDockedStackMinimizeThickness) * paramFloat + (1.0F - paramFloat) * this.mBounds.left));
    return true;
  }
  
  private void alignTasksToAdjustedBounds(Rect paramRect1, Rect paramRect2)
  {
    if (this.mFullscreen) {
      return;
    }
    int i = this.mTasks.size() - 1;
    while (i >= 0)
    {
      Task localTask = (Task)this.mTasks.get(i);
      if (localTask.isTwoFingerScrollMode())
      {
        localTask.resizeLocked(null, null, false);
        localTask.getBounds(this.mTmpRect2);
        localTask.scrollLocked(this.mTmpRect2);
        i -= 1;
      }
      else
      {
        if ((this.mAdjustedForIme) && (getDockSide() == 2)) {}
        for (boolean bool = true;; bool = false)
        {
          localTask.alignToAdjustedBounds(paramRect1, paramRect2, bool);
          break;
        }
      }
    }
  }
  
  private int computeMaxPosition(int paramInt)
  {
    for (;;)
    {
      Task localTask;
      if (paramInt > 0)
      {
        localTask = (Task)this.mTasks.get(paramInt - 1);
        if (localTask.showForAllUsers()) {
          break label43;
        }
      }
      label43:
      for (boolean bool = this.mService.isCurrentProfileLocked(localTask.mUserId); !bool; bool = true) {
        return paramInt;
      }
      paramInt -= 1;
    }
  }
  
  private int computeMinPosition(int paramInt1, int paramInt2)
  {
    for (;;)
    {
      Task localTask;
      if (paramInt1 < paramInt2)
      {
        localTask = (Task)this.mTasks.get(paramInt1);
        if (localTask.showForAllUsers()) {
          break label45;
        }
      }
      label45:
      for (boolean bool = this.mService.isCurrentProfileLocked(localTask.mUserId); bool; bool = true) {
        return paramInt1;
      }
      paramInt1 += 1;
    }
  }
  
  static int getDockSideUnchecked(Rect paramRect1, Rect paramRect2, int paramInt)
  {
    if (paramInt == 1)
    {
      if (paramRect1.top - paramRect2.top <= paramRect2.bottom - paramRect1.bottom) {
        return 2;
      }
      return 4;
    }
    if (paramInt == 2)
    {
      if (paramRect1.left - paramRect2.left <= paramRect2.right - paramRect1.right) {
        return 1;
      }
      return 3;
    }
    return -1;
  }
  
  private void getStackDockedModeBounds(Rect paramRect1, Rect paramRect2, int paramInt1, Rect paramRect3, int paramInt2, boolean paramBoolean)
  {
    if (paramInt1 == 3)
    {
      i = 1;
      if (paramRect1.width() <= paramRect1.height()) {
        break label59;
      }
    }
    label59:
    for (paramInt1 = 1;; paramInt1 = 0)
    {
      paramRect2.set(paramRect1);
      if (i == 0) {
        break label223;
      }
      if (this.mService.mDockedStackCreateBounds == null) {
        break label64;
      }
      paramRect2.set(this.mService.mDockedStackCreateBounds);
      return;
      i = 0;
      break;
    }
    label64:
    paramRect1 = this.mDisplayContent.getDisplayInfo();
    this.mService.mPolicy.getStableInsetsLw(paramRect1.rotation, paramRect1.logicalWidth, paramRect1.logicalHeight, this.mTmpRect2);
    paramRect3 = this.mService.mContext.getResources();
    int i = paramRect1.logicalWidth;
    int j = paramRect1.logicalHeight;
    if (this.mService.mCurConfiguration.orientation == 1) {}
    for (boolean bool = true;; bool = false)
    {
      i = new DividerSnapAlgorithm(paramRect3, i, j, paramInt2, bool, this.mTmpRect2).getMiddleTarget().position;
      if (!paramBoolean) {
        break label199;
      }
      if (paramInt1 == 0) {
        break;
      }
      paramRect2.right = i;
      return;
    }
    paramRect2.bottom = i;
    return;
    label199:
    if (paramInt1 != 0)
    {
      paramRect2.left = (i + paramInt2);
      return;
    }
    paramRect2.top = (i + paramInt2);
    return;
    label223:
    if (!paramBoolean) {
      if (paramInt1 != 0)
      {
        paramRect2.right = (paramRect3.left - paramInt2);
        if (!paramBoolean) {
          break label308;
        }
      }
    }
    label308:
    for (paramBoolean = false;; paramBoolean = true)
    {
      DockedDividerUtils.sanitizeStackBounds(paramRect2, paramBoolean);
      return;
      paramRect2.bottom = (paramRect3.top - paramInt2);
      break;
      if (paramInt1 != 0)
      {
        paramRect2.left = (paramRect3.right + paramInt2);
        break;
      }
      paramRect2.top = (paramRect3.bottom + paramInt2);
      break;
    }
  }
  
  private void repositionDockedStackAfterRotation(Rect paramRect)
  {
    int i = getDockSide(paramRect);
    if (this.mService.mPolicy.isDockSideAllowed(i)) {
      return;
    }
    this.mDisplayContent.getLogicalDisplayRect(this.mTmpRect);
    switch (DockedDividerUtils.invertDockSide(i))
    {
    default: 
      return;
    case 1: 
      i = paramRect.left;
      paramRect.left -= i;
      paramRect.right -= i;
      return;
    case 3: 
      i = this.mTmpRect.right - paramRect.right;
      paramRect.left += i;
      paramRect.right += i;
      return;
    case 2: 
      i = paramRect.top;
      paramRect.top -= i;
      paramRect.bottom -= i;
      return;
    }
    i = this.mTmpRect.bottom - paramRect.bottom;
    paramRect.top += i;
    paramRect.bottom += i;
  }
  
  private void setAdjustedBounds(Rect paramRect)
  {
    int i;
    if ((!this.mAdjustedBounds.equals(paramRect)) || (isAnimatingForIme()))
    {
      this.mAdjustedBounds.set(paramRect);
      if (!this.mAdjustedBounds.isEmpty()) {
        break label81;
      }
      i = 0;
      localRect = null;
      if ((i == 0) || (!isAdjustedForMinimizedDock())) {
        break label86;
      }
      paramRect = this.mBounds;
      label56:
      if (i == 0) {
        break label124;
      }
    }
    label81:
    label86:
    label124:
    for (Rect localRect = this.mAdjustedBounds;; localRect = this.mBounds)
    {
      alignTasksToAdjustedBounds(localRect, paramRect);
      this.mDisplayContent.layoutNeeded = true;
      return;
      return;
      i = 1;
      break;
      paramRect = localRect;
      if (i == 0) {
        break label56;
      }
      paramRect = localRect;
      if (!this.mAdjustedForIme) {
        break label56;
      }
      if (this.mImeGoingAway)
      {
        paramRect = this.mBounds;
        break label56;
      }
      paramRect = this.mFullyAdjustedImeBounds;
      break label56;
    }
  }
  
  private boolean setBounds(Rect paramRect)
  {
    boolean bool2 = this.mFullscreen;
    int j = 0;
    int i = 0;
    Rect localRect = paramRect;
    int k;
    int m;
    if (this.mDisplayContent != null)
    {
      this.mDisplayContent.getLogicalDisplayRect(this.mTmpRect);
      k = this.mDisplayContent.getDisplayInfo().rotation;
      m = this.mDisplayContent.getDisplayInfo().logicalDensityDpi;
      if (paramRect != null) {
        break label103;
      }
    }
    label103:
    for (boolean bool1 = true;; bool1 = false)
    {
      this.mFullscreen = bool1;
      i = m;
      j = k;
      localRect = paramRect;
      if (this.mFullscreen)
      {
        localRect = this.mTmpRect;
        j = k;
        i = m;
      }
      if (localRect != null) {
        break;
      }
      return false;
    }
    if ((this.mBounds.equals(localRect)) && (bool2 == this.mFullscreen) && (this.mRotation == j)) {
      return false;
    }
    if (this.mDisplayContent != null)
    {
      this.mDisplayContent.mDimLayerController.updateDimLayer(this);
      this.mAnimationBackgroundSurface.setBounds(localRect);
    }
    this.mBounds.set(localRect);
    this.mRotation = j;
    this.mDensity = i;
    updateAdjustedBounds();
    return true;
  }
  
  private void snapDockedStackAfterRotation(Rect paramRect)
  {
    DisplayInfo localDisplayInfo = this.mDisplayContent.getDisplayInfo();
    int i = this.mService.getDefaultDisplayContentLocked().getDockedDividerController().getContentWidth();
    int j = getDockSide(paramRect);
    int k = DockedDividerUtils.calculatePositionForBounds(paramRect, j, i);
    int m = this.mDisplayContent.getDisplayInfo().logicalWidth;
    int n = this.mDisplayContent.getDisplayInfo().logicalHeight;
    int i1 = localDisplayInfo.rotation;
    int i2 = this.mService.mCurConfiguration.orientation;
    this.mService.mPolicy.getStableInsetsLw(i1, m, n, paramRect);
    Resources localResources = this.mService.mContext.getResources();
    if (i2 == 1) {}
    for (boolean bool = true;; bool = false)
    {
      DockedDividerUtils.calculateBoundsForPosition(new DividerSnapAlgorithm(localResources, m, n, i, bool, paramRect).calculateNonDismissingSnapTarget(k).position, j, paramRect, localDisplayInfo.logicalWidth, localDisplayInfo.logicalHeight, i);
      return;
    }
  }
  
  private void updateAdjustedBounds()
  {
    boolean bool = false;
    if (this.mMinimizeAmount != 0.0F)
    {
      bool = adjustForMinimizedDockedStack(this.mMinimizeAmount);
      if (!bool) {
        this.mTmpAdjustedBounds.setEmpty();
      }
      setAdjustedBounds(this.mTmpAdjustedBounds);
      if (this.mService.getImeFocusStackLocked() != this) {
        break label87;
      }
    }
    label87:
    for (int i = 1;; i = 0)
    {
      if ((this.mAdjustedForIme) && (bool) && (i == 0)) {
        break label92;
      }
      return;
      if (!this.mAdjustedForIme) {
        break;
      }
      bool = adjustForIME(this.mImeWin);
      break;
    }
    label92:
    float f = Math.max(this.mAdjustImeAmount, this.mAdjustDividerAmount);
    this.mService.setResizeDimLayer(true, this.mStackId, f * 0.25F);
  }
  
  private boolean updateBoundsAfterConfigChange()
  {
    int j = 0;
    if (this.mDisplayContent == null) {
      return false;
    }
    int i = getDisplayInfo().rotation;
    int k = getDisplayInfo().logicalDensityDpi;
    if ((this.mRotation == i) && (this.mDensity == k)) {
      return false;
    }
    if (this.mFullscreen)
    {
      setBounds(null);
      return false;
    }
    this.mTmpRect2.set(this.mBounds);
    this.mDisplayContent.rotateBounds(this.mRotation, i, this.mTmpRect2);
    WindowManagerService localWindowManagerService;
    if (this.mStackId == 3)
    {
      repositionDockedStackAfterRotation(this.mTmpRect2);
      snapDockedStackAfterRotation(this.mTmpRect2);
      k = getDockSide(this.mTmpRect2);
      localWindowManagerService = this.mService;
      i = j;
      if (k != 1) {
        if (k != 2) {
          break label171;
        }
      }
    }
    label171:
    for (i = j;; i = 1)
    {
      localWindowManagerService.setDockedStackCreateStateLocked(i, null);
      this.mDisplayContent.getDockedDividerController().notifyDockSideChanged(k);
      this.mBoundsAfterRotation.set(this.mTmpRect2);
      return true;
    }
  }
  
  private boolean useCurrentBounds()
  {
    if ((this.mFullscreen) || (!ActivityManager.StackId.isResizeableByDockedStack(this.mStackId)) || (this.mDisplayContent == null)) {}
    while (this.mDisplayContent.getDockedStackLocked() != null) {
      return true;
    }
    return false;
  }
  
  void addTask(Task paramTask, boolean paramBoolean)
  {
    addTask(paramTask, paramBoolean, paramTask.showForAllUsers());
  }
  
  void addTask(Task paramTask, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1) {}
    for (int i = this.mTasks.size();; i = 0)
    {
      positionTask(paramTask, i, paramBoolean2);
      return;
    }
  }
  
  void applyAdjustForImeIfNeeded(Task paramTask)
  {
    if ((this.mMinimizeAmount != 0.0F) || (!this.mAdjustedForIme) || (this.mAdjustedBounds.isEmpty())) {
      return;
    }
    Rect localRect1;
    Rect localRect2;
    if (this.mImeGoingAway)
    {
      localRect1 = this.mBounds;
      localRect2 = this.mAdjustedBounds;
      if (getDockSide() != 2) {
        break label80;
      }
    }
    label80:
    for (boolean bool = true;; bool = false)
    {
      paramTask.alignToAdjustedBounds(localRect2, localRect1, bool);
      this.mDisplayContent.layoutNeeded = true;
      return;
      localRect1 = this.mFullyAdjustedImeBounds;
      break;
    }
  }
  
  void attachDisplayContent(DisplayContent paramDisplayContent)
  {
    if (this.mDisplayContent != null) {
      throw new IllegalStateException("attachDisplayContent: Already attached");
    }
    this.mDisplayContent = paramDisplayContent;
    this.mAnimationBackgroundSurface = new DimLayer(this.mService, this, this.mDisplayContent.getDisplayId(), "animation background stackId=" + this.mStackId);
    Object localObject2 = null;
    TaskStack localTaskStack = (TaskStack)this.mService.mStackIdToStack.get(3);
    if (this.mStackId != 3)
    {
      localObject1 = localObject2;
      if (localTaskStack != null)
      {
        localObject1 = localObject2;
        if (ActivityManager.StackId.isResizeableByDockedStack(this.mStackId))
        {
          if (!localTaskStack.isFullscreen()) {
            break label134;
          }
          localObject1 = localObject2;
        }
      }
      updateDisplayInfo((Rect)localObject1);
      return;
    }
    label134:
    Object localObject1 = new Rect();
    paramDisplayContent.getLogicalDisplayRect(this.mTmpRect);
    this.mTmpRect2.setEmpty();
    if (localTaskStack != null) {
      localTaskStack.getRawBounds(this.mTmpRect2);
    }
    if (this.mService.mDockedStackCreateMode == 0) {}
    for (boolean bool = true;; bool = false)
    {
      getStackDockedModeBounds(this.mTmpRect, (Rect)localObject1, this.mStackId, this.mTmpRect2, this.mDisplayContent.mDividerControllerLocked.getContentWidth(), bool);
      break;
    }
  }
  
  void beginImeAdjustAnimation()
  {
    int i = this.mTasks.size() - 1;
    while (i >= 0)
    {
      Task localTask = (Task)this.mTasks.get(i);
      if (localTask.isVisible())
      {
        localTask.setDragResizing(true, 1);
        localTask.addWindowsWaitingForDrawnIfResizingChanged();
      }
      i -= 1;
    }
  }
  
  void close()
  {
    if (this.mAnimationBackgroundSurface != null)
    {
      this.mAnimationBackgroundSurface.destroySurface();
      this.mAnimationBackgroundSurface = null;
    }
    this.mDisplayContent = null;
  }
  
  void detachDisplay()
  {
    EventLog.writeEvent(31006, this.mStackId);
    int k = 0;
    int i = this.mTasks.size() - 1;
    while (i >= 0)
    {
      AppTokenList localAppTokenList = ((Task)this.mTasks.get(i)).mAppTokens;
      int j = localAppTokenList.size() - 1;
      while (j >= 0)
      {
        WindowList localWindowList = ((AppWindowToken)localAppTokenList.get(j)).allAppWindows;
        int m = localWindowList.size() - 1;
        while (m >= 0)
        {
          this.mService.removeWindowLocked((WindowState)localWindowList.get(m));
          k = 1;
          m -= 1;
        }
        j -= 1;
      }
      i -= 1;
    }
    if (k != 0) {
      this.mService.mWindowPlacerLocked.requestTraversal();
    }
    close();
  }
  
  public boolean dimFullscreen()
  {
    if (this.mStackId != 0) {
      return isFullscreen();
    }
    return true;
  }
  
  public void dump(String paramString, PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println(paramString + "mStackId=" + this.mStackId);
    paramPrintWriter.println(paramString + "mDeferDetach=" + this.mDeferDetach);
    paramPrintWriter.println(paramString + "mFullscreen=" + this.mFullscreen);
    paramPrintWriter.println(paramString + "mBounds=" + this.mBounds.toShortString());
    if (this.mMinimizeAmount != 0.0F) {
      paramPrintWriter.println(paramString + "mMinimizeAmout=" + this.mMinimizeAmount);
    }
    if (this.mAdjustedForIme)
    {
      paramPrintWriter.println(paramString + "mAdjustedForIme=true");
      paramPrintWriter.println(paramString + "mAdjustImeAmount=" + this.mAdjustImeAmount);
      paramPrintWriter.println(paramString + "mAdjustDividerAmount=" + this.mAdjustDividerAmount);
    }
    if (!this.mAdjustedBounds.isEmpty()) {
      paramPrintWriter.println(paramString + "mAdjustedBounds=" + this.mAdjustedBounds.toShortString());
    }
    int i = this.mTasks.size() - 1;
    while (i >= 0)
    {
      ((Task)this.mTasks.get(i)).dump(paramString + "  ", paramPrintWriter);
      i -= 1;
    }
    if (this.mAnimationBackgroundSurface.isDimming())
    {
      paramPrintWriter.println(paramString + "mWindowAnimationBackgroundSurface:");
      this.mAnimationBackgroundSurface.printTo(paramString + "  ", paramPrintWriter);
    }
    if (!this.mExitingAppTokens.isEmpty())
    {
      paramPrintWriter.println();
      paramPrintWriter.println("  Exiting application tokens:");
      i = this.mExitingAppTokens.size() - 1;
      while (i >= 0)
      {
        paramString = (WindowToken)this.mExitingAppTokens.get(i);
        paramPrintWriter.print("  Exiting App #");
        paramPrintWriter.print(i);
        paramPrintWriter.print(' ');
        paramPrintWriter.print(paramString);
        paramPrintWriter.println(':');
        paramString.dump(paramPrintWriter, "    ");
        i -= 1;
      }
    }
  }
  
  void endImeAdjustAnimation()
  {
    int i = this.mTasks.size() - 1;
    while (i >= 0)
    {
      ((Task)this.mTasks.get(i)).setDragResizing(false, 1);
      i -= 1;
    }
  }
  
  Task findHomeTask()
  {
    if (this.mStackId != 0) {
      return null;
    }
    int i = this.mTasks.size() - 1;
    while (i >= 0)
    {
      if (((Task)this.mTasks.get(i)).isHomeTask()) {
        return (Task)this.mTasks.get(i);
      }
      i -= 1;
    }
    return null;
  }
  
  void forceWindowsScaleable(Task paramTask, boolean paramBoolean)
  {
    
    try
    {
      paramTask = paramTask.mAppTokens;
      int i = paramTask.size() - 1;
      while (i >= 0)
      {
        WindowList localWindowList = ((AppWindowToken)paramTask.get(i)).allAppWindows;
        int j = localWindowList.size() - 1;
        while (j >= 0)
        {
          WindowStateAnimator localWindowStateAnimator = ((WindowState)localWindowList.get(j)).mWinAnimator;
          if ((localWindowStateAnimator != null) && (localWindowStateAnimator.hasSurface())) {
            localWindowStateAnimator.mSurfaceController.forceScaleableInTransaction(paramBoolean);
          }
          j -= 1;
        }
        i -= 1;
      }
      return;
    }
    finally
    {
      SurfaceControl.closeTransaction();
    }
  }
  
  public void getBounds(Rect paramRect)
  {
    if (useCurrentBounds())
    {
      if (!this.mAdjustedBounds.isEmpty())
      {
        paramRect.set(this.mAdjustedBounds);
        return;
      }
      paramRect.set(this.mBounds);
      return;
    }
    this.mDisplayContent.getLogicalDisplayRect(paramRect);
  }
  
  public boolean getBoundsAnimating()
  {
    return this.mBoundsAnimating;
  }
  
  void getBoundsForNewConfiguration(Rect paramRect)
  {
    paramRect.set(this.mBoundsAfterRotation);
    this.mBoundsAfterRotation.setEmpty();
  }
  
  public void getDimBounds(Rect paramRect)
  {
    getBounds(paramRect);
  }
  
  DisplayContent getDisplayContent()
  {
    return this.mDisplayContent;
  }
  
  public DisplayInfo getDisplayInfo()
  {
    return this.mDisplayContent.getDisplayInfo();
  }
  
  int getDockSide()
  {
    return getDockSide(this.mBounds);
  }
  
  int getDockSide(Rect paramRect)
  {
    if ((this.mStackId == 3) || (ActivityManager.StackId.isResizeableByDockedStack(this.mStackId)))
    {
      if (this.mDisplayContent == null) {
        return -1;
      }
    }
    else {
      return -1;
    }
    this.mDisplayContent.getLogicalDisplayRect(this.mTmpRect);
    int i = this.mService.mCurConfiguration.orientation;
    return getDockSideUnchecked(paramRect, this.mTmpRect, i);
  }
  
  public boolean getForceScaleToCrop()
  {
    return this.mBoundsAnimating;
  }
  
  public void getFullScreenBounds(Rect paramRect)
  {
    getDisplayContent().getContentRect(paramRect);
  }
  
  int getMinTopStackBottom(Rect paramRect, int paramInt)
  {
    return paramRect.top + (int)((paramInt - paramRect.top) * 0.3F);
  }
  
  int getMinimizeDistance()
  {
    int i = getDockSide();
    if (i == -1) {
      return 0;
    }
    if (i == 2)
    {
      this.mService.getStableInsetsLocked(this.mTmpRect);
      i = this.mTmpRect.top;
      return this.mBounds.bottom - i;
    }
    if ((i == 1) || (i == 3)) {
      return this.mBounds.width() - this.mDockedStackMinimizeThickness;
    }
    return 0;
  }
  
  void getRawBounds(Rect paramRect)
  {
    paramRect.set(this.mBounds);
  }
  
  boolean getRawFullscreen()
  {
    return this.mFullscreen;
  }
  
  void getStackDockedModeBoundsLocked(Rect paramRect, boolean paramBoolean)
  {
    if (((this.mStackId != 3) && (!ActivityManager.StackId.isResizeableByDockedStack(this.mStackId))) || (this.mDisplayContent == null))
    {
      paramRect.set(this.mBounds);
      return;
    }
    TaskStack localTaskStack = (TaskStack)this.mService.mStackIdToStack.get(3);
    if (localTaskStack == null) {
      throw new IllegalStateException("Calling getStackDockedModeBoundsLocked() when there is no docked stack.");
    }
    int i;
    if ((paramBoolean) || (localTaskStack.isVisibleLocked()))
    {
      i = localTaskStack.getDockSide();
      if (i == -1)
      {
        Slog.e("WindowManager", "Failed to get valid docked side for docked stack=" + localTaskStack);
        paramRect.set(this.mBounds);
      }
    }
    else
    {
      this.mDisplayContent.getLogicalDisplayRect(paramRect);
      return;
    }
    this.mDisplayContent.getLogicalDisplayRect(this.mTmpRect);
    localTaskStack.getRawBounds(this.mTmpRect2);
    if ((i == 2) || (i == 1)) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      getStackDockedModeBounds(this.mTmpRect, paramRect, this.mStackId, this.mTmpRect2, this.mDisplayContent.mDividerControllerLocked.getContentWidth(), paramBoolean);
      return;
    }
  }
  
  ArrayList<Task> getTasks()
  {
    return this.mTasks;
  }
  
  public boolean hasMovementAnimations()
  {
    return ActivityManager.StackId.hasMovementAnimations(this.mStackId);
  }
  
  boolean isAdjustedForIme()
  {
    return this.mAdjustedForIme;
  }
  
  boolean isAdjustedForMinimizedDock()
  {
    return this.mMinimizeAmount != 0.0F;
  }
  
  boolean isAdjustedForMinimizedDockedStack()
  {
    return this.mMinimizeAmount != 0.0F;
  }
  
  boolean isAnimating()
  {
    int i = this.mTasks.size() - 1;
    while (i >= 0)
    {
      AppTokenList localAppTokenList = ((Task)this.mTasks.get(i)).mAppTokens;
      int j = localAppTokenList.size() - 1;
      while (j >= 0)
      {
        WindowList localWindowList = ((AppWindowToken)localAppTokenList.get(j)).allAppWindows;
        int k = localWindowList.size() - 1;
        while (k >= 0)
        {
          WindowStateAnimator localWindowStateAnimator = ((WindowState)localWindowList.get(k)).mWinAnimator;
          if ((localWindowStateAnimator.isAnimationSet()) || (localWindowStateAnimator.mWin.mAnimatingExit)) {
            return true;
          }
          k -= 1;
        }
        j -= 1;
      }
      i -= 1;
    }
    return false;
  }
  
  boolean isAnimatingForIme()
  {
    if (this.mImeWin != null) {
      return this.mImeWin.isAnimatingLw();
    }
    return false;
  }
  
  boolean isDragResizing()
  {
    return this.mDragResizing;
  }
  
  boolean isFullscreen()
  {
    if (useCurrentBounds()) {
      return this.mFullscreen;
    }
    return true;
  }
  
  boolean isFullscreenBounds(Rect paramRect)
  {
    if ((this.mDisplayContent == null) || (paramRect == null)) {
      return true;
    }
    this.mDisplayContent.getLogicalDisplayRect(this.mTmpRect);
    return this.mTmpRect.equals(paramRect);
  }
  
  boolean isVisibleLocked()
  {
    return isVisibleLocked(false);
  }
  
  boolean isVisibleLocked(boolean paramBoolean)
  {
    int i;
    if (this.mService.mPolicy.isKeyguardShowingOrOccluded()) {
      if (this.mService.mAnimator.mKeyguardGoingAway)
      {
        i = 0;
        if ((!paramBoolean) && (i != 0) && (!ActivityManager.StackId.isAllowedOverLockscreen(this.mStackId))) {
          break label120;
        }
        i = this.mTasks.size() - 1;
      }
    }
    for (;;)
    {
      if (i < 0) {
        break label136;
      }
      Task localTask = (Task)this.mTasks.get(i);
      int j = localTask.mAppTokens.size() - 1;
      for (;;)
      {
        if (j < 0) {
          break label129;
        }
        if (!((AppWindowToken)localTask.mAppTokens.get(j)).hidden)
        {
          return true;
          i = 1;
          break;
          i = 0;
          break;
          label120:
          return false;
        }
        j -= 1;
      }
      label129:
      i -= 1;
    }
    label136:
    return false;
  }
  
  void moveTaskToBottom(Task paramTask)
  {
    if (WindowManagerDebugConfig.DEBUG_TASK_MOVEMENT) {
      Slog.d("WindowManager", "moveTaskToBottom: task=" + paramTask);
    }
    this.mTasks.remove(paramTask);
    addTask(paramTask, false);
  }
  
  void moveTaskToTop(Task paramTask)
  {
    if (WindowManagerDebugConfig.DEBUG_TASK_MOVEMENT) {
      Slog.d("WindowManager", "moveTaskToTop: task=" + paramTask + " Callers=" + Debug.getCallers(6));
    }
    this.mTasks.remove(paramTask);
    addTask(paramTask, true);
  }
  
  public void moveToFullscreen()
  {
    try
    {
      this.mService.mActivityManager.moveTasksToFullscreenStack(this.mStackId, true);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      localRemoteException.printStackTrace();
    }
  }
  
  public void onAnimationEnd()
  {
    synchronized (this.mService.mWindowMap)
    {
      this.mBoundsAnimating = false;
      this.mService.requestTraversal();
      if (this.mStackId != 4) {}
    }
  }
  
  public void onAnimationStart()
  {
    synchronized (this.mService.mWindowMap)
    {
      this.mBoundsAnimating = true;
      return;
    }
  }
  
  boolean onConfigurationChanged()
  {
    return updateBoundsAfterConfigChange();
  }
  
  void overridePlayingAppAnimations(Animation paramAnimation)
  {
    int i = this.mTasks.size() - 1;
    while (i >= 0)
    {
      ((Task)this.mTasks.get(i)).overridePlayingAppAnimations(paramAnimation);
      i -= 1;
    }
  }
  
  void positionTask(Task paramTask, int paramInt, boolean paramBoolean)
  {
    int k = 0;
    int m;
    int j;
    int i;
    if (!paramBoolean)
    {
      paramBoolean = this.mService.isCurrentProfileLocked(paramTask.mUserId);
      this.mTasks.remove(paramTask);
      m = this.mTasks.size();
      j = 0;
      i = m;
      if (!paramBoolean) {
        break label252;
      }
      j = computeMinPosition(0, m);
      label57:
      j = Math.min(Math.max(paramInt, j), i);
      if (WindowManagerDebugConfig.DEBUG_TASK_MOVEMENT) {
        Slog.d("WindowManager", "positionTask: task=" + paramTask + " position=" + j);
      }
      this.mTasks.add(j, paramTask);
      if (paramTask.mStack != this) {
        paramTask.resetScrollLocked();
      }
      paramTask.mStack = this;
      paramTask.updateDisplayInfo(this.mDisplayContent);
      if (j != this.mTasks.size() - 1) {
        break label263;
      }
      paramInt = 1;
      label165:
      if (paramInt != 0) {
        this.mDisplayContent.moveStack(this, true);
      }
      if (!ActivityManager.StackId.windowsAreScaleable(this.mStackId)) {
        break label268;
      }
      forceWindowsScaleable(paramTask, true);
    }
    for (;;)
    {
      m = paramTask.mTaskId;
      i = k;
      if (paramInt != 0) {
        i = 1;
      }
      EventLog.writeEvent(31002, new Object[] { Integer.valueOf(m), Integer.valueOf(i), Integer.valueOf(j) });
      return;
      paramBoolean = true;
      break;
      label252:
      i = computeMaxPosition(m);
      break label57;
      label263:
      paramInt = 0;
      break label165;
      label268:
      forceWindowsScaleable(paramTask, false);
    }
  }
  
  void prepareFreezingTaskBounds()
  {
    int i = this.mTasks.size() - 1;
    while (i >= 0)
    {
      ((Task)this.mTasks.get(i)).prepareFreezingBounds();
      i -= 1;
    }
  }
  
  void removeTask(Task paramTask)
  {
    if (WindowManagerDebugConfig.DEBUG_TASK_MOVEMENT) {
      Slog.d("WindowManager", "removeTask: task=" + paramTask);
    }
    this.mTasks.remove(paramTask);
    if (this.mDisplayContent != null)
    {
      if (this.mTasks.isEmpty()) {
        this.mDisplayContent.moveStack(this, false);
      }
      this.mDisplayContent.layoutNeeded = true;
    }
    int i = this.mExitingAppTokens.size() - 1;
    while (i >= 0)
    {
      AppWindowToken localAppWindowToken = (AppWindowToken)this.mExitingAppTokens.get(i);
      if (localAppWindowToken.mTask == paramTask)
      {
        localAppWindowToken.mIsExiting = false;
        this.mExitingAppTokens.remove(i);
      }
      i -= 1;
    }
  }
  
  void resetAdjustedForIme(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mImeWin = null;
      this.mAdjustedForIme = false;
      this.mImeGoingAway = false;
      this.mAdjustImeAmount = 0.0F;
      this.mAdjustDividerAmount = 0.0F;
      updateAdjustedBounds();
      this.mService.setResizeDimLayer(false, this.mStackId, 1.0F);
      return;
    }
    this.mImeGoingAway |= this.mAdjustedForIme;
  }
  
  void resetAnimationBackgroundAnimator()
  {
    this.mAnimationBackgroundAnimator = null;
    this.mAnimationBackgroundSurface.hide();
  }
  
  void resetDockedStackToMiddle()
  {
    if (this.mStackId != 3) {
      throw new IllegalStateException("Not a docked stack=" + this);
    }
    this.mService.mDockedStackCreateBounds = null;
    Rect localRect = new Rect();
    getStackDockedModeBoundsLocked(localRect, true);
    this.mService.mH.obtainMessage(42, 3, 1, localRect).sendToTarget();
  }
  
  void setAdjustedForIme(WindowState paramWindowState, boolean paramBoolean)
  {
    this.mImeWin = paramWindowState;
    this.mImeGoingAway = false;
    if ((!this.mAdjustedForIme) || (paramBoolean))
    {
      this.mAdjustedForIme = true;
      this.mAdjustImeAmount = 0.0F;
      this.mAdjustDividerAmount = 0.0F;
      updateAdjustForIme(0.0F, 0.0F, true);
    }
  }
  
  boolean setAdjustedForMinimizedDock(float paramFloat)
  {
    if (paramFloat != this.mMinimizeAmount)
    {
      this.mMinimizeAmount = paramFloat;
      updateAdjustedBounds();
      return isVisibleLocked(true);
    }
    return false;
  }
  
  void setAnimationBackground(WindowStateAnimator paramWindowStateAnimator, int paramInt)
  {
    int i = paramWindowStateAnimator.mAnimLayer;
    if ((this.mAnimationBackgroundAnimator == null) || (i < this.mAnimationBackgroundAnimator.mAnimLayer))
    {
      this.mAnimationBackgroundAnimator = paramWindowStateAnimator;
      i = this.mService.adjustAnimationBackground(paramWindowStateAnimator);
      if (this.mService.mCurrentFocus == null) {
        break label81;
      }
    }
    label81:
    for (boolean bool = "com.oneplus.applocker".equals(this.mService.mCurrentFocus.getOwningPackage()); bool; bool = false)
    {
      Slog.i("WindowManager", "AppLocker: skip setAnimationBackground due to focus locked");
      return;
    }
    this.mAnimationBackgroundSurface.show(i - 1, (paramInt >> 24 & 0xFF) / 255.0F, 0L);
  }
  
  boolean setBounds(Rect paramRect, SparseArray<Configuration> paramSparseArray, SparseArray<Rect> paramSparseArray1, SparseArray<Rect> paramSparseArray2)
  {
    setBounds(paramRect);
    int i = this.mTasks.size() - 1;
    if (i >= 0)
    {
      Task localTask = (Task)this.mTasks.get(i);
      paramRect = (Configuration)paramSparseArray.get(localTask.mTaskId);
      Rect localRect;
      if (paramRect != null)
      {
        localRect = (Rect)paramSparseArray1.get(localTask.mTaskId);
        if (localTask.isTwoFingerScrollMode())
        {
          localTask.resizeLocked(localRect, paramRect, false);
          localTask.getBounds(this.mTmpRect);
          localTask.scrollLocked(this.mTmpRect);
        }
      }
      for (;;)
      {
        i -= 1;
        break;
        localTask.resizeLocked(localRect, paramRect, false);
        if (paramSparseArray2 != null) {}
        for (paramRect = (Rect)paramSparseArray2.get(localTask.mTaskId);; paramRect = null)
        {
          localTask.setTempInsetBounds(paramRect);
          break;
        }
        Slog.wtf("WindowManager", "No config for task: " + localTask + ", is there a mismatch with AM?");
      }
    }
    return true;
  }
  
  void setDragResizingLocked(boolean paramBoolean)
  {
    if (this.mDragResizing == paramBoolean) {
      return;
    }
    this.mDragResizing = paramBoolean;
    int i = this.mTasks.size() - 1;
    while (i >= 0)
    {
      ((Task)this.mTasks.get(i)).resetDragResizingChangeReported();
      i -= 1;
    }
  }
  
  public boolean setPinnedStackSize(Rect paramRect1, Rect paramRect2)
  {
    synchronized (this.mService.mWindowMap)
    {
      DisplayContent localDisplayContent = this.mDisplayContent;
      if (localDisplayContent == null) {
        return false;
      }
      if (this.mStackId != 4)
      {
        Slog.w("WindowManager", "Attempt to use pinned stack resize animation helper onnon pinned stack");
        return false;
      }
    }
    try
    {
      this.mService.mActivityManager.resizePinnedStack(paramRect1, paramRect2);
      return true;
      paramRect1 = finally;
      throw paramRect1;
    }
    catch (RemoteException paramRect1)
    {
      for (;;) {}
    }
  }
  
  public boolean setSize(Rect paramRect)
  {
    synchronized (this.mService.mWindowMap)
    {
      DisplayContent localDisplayContent = this.mDisplayContent;
      if (localDisplayContent == null) {
        return false;
      }
    }
    return true;
  }
  
  void switchUser()
  {
    int j = this.mTasks.size();
    int i = 0;
    while (i < j)
    {
      Task localTask = (Task)this.mTasks.get(i);
      int k;
      if (!this.mService.isCurrentProfileLocked(localTask.mUserId))
      {
        k = j;
        if (!localTask.showForAllUsers()) {}
      }
      else
      {
        this.mTasks.remove(i);
        this.mTasks.add(localTask);
        k = j - 1;
      }
      i += 1;
      j = k;
    }
  }
  
  public String toShortString()
  {
    return "Stack=" + this.mStackId;
  }
  
  public String toString()
  {
    return "{stackId=" + this.mStackId + " tasks=" + this.mTasks + "}";
  }
  
  boolean updateAdjustForIme(float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    if ((paramFloat1 != this.mAdjustImeAmount) || (paramFloat2 != this.mAdjustDividerAmount)) {}
    while (paramBoolean)
    {
      this.mAdjustImeAmount = paramFloat1;
      this.mAdjustDividerAmount = paramFloat2;
      updateAdjustedBounds();
      return isVisibleLocked(true);
    }
    return false;
  }
  
  void updateDisplayInfo(Rect paramRect)
  {
    if (this.mDisplayContent == null) {
      return;
    }
    int i = this.mTasks.size() - 1;
    while (i >= 0)
    {
      ((Task)this.mTasks.get(i)).updateDisplayInfo(this.mDisplayContent);
      i -= 1;
    }
    if (paramRect != null)
    {
      setBounds(paramRect);
      return;
    }
    if (this.mFullscreen)
    {
      setBounds(null);
      return;
    }
    this.mTmpRect2.set(this.mBounds);
    i = this.mDisplayContent.getDisplayInfo().rotation;
    int j = this.mDisplayContent.getDisplayInfo().logicalDensityDpi;
    if ((this.mRotation == i) && (this.mDensity == j)) {
      setBounds(this.mTmpRect2);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/TaskStack.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */