package com.android.server.wm;

import android.app.ActivityManager.StackId;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Region.Op;
import android.hardware.display.DisplayManagerInternal;
import android.util.DisplayMetrics;
import android.util.OpFeatures;
import android.util.Slog;
import android.util.SparseArray;
import android.view.Display;
import android.view.DisplayInfo;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import java.io.PrintWriter;
import java.util.ArrayList;

class DisplayContent
{
  final boolean isDefaultDisplay;
  boolean layoutNeeded;
  int mBaseDisplayDensity = 0;
  int mBaseDisplayHeight = 0;
  Rect mBaseDisplayRect = new Rect();
  int mBaseDisplayWidth = 0;
  Rect mContentRect = new Rect();
  boolean mDeferredRemoval;
  final DimLayerController mDimLayerController;
  private final Display mDisplay;
  private final int mDisplayId;
  private final DisplayInfo mDisplayInfo = new DisplayInfo();
  private final DisplayMetrics mDisplayMetrics = new DisplayMetrics();
  boolean mDisplayScalingDisabled;
  final DockedStackDividerController mDividerControllerLocked;
  final ArrayList<WindowToken> mExitingTokens = new ArrayList();
  private TaskStack mHomeStack = null;
  int mInitialDisplayDensity = 0;
  int mInitialDisplayHeight = 0;
  int mInitialDisplayWidth = 0;
  Region mNonResizeableRegion = new Region();
  final WindowManagerService mService;
  private final ArrayList<TaskStack> mStacks = new ArrayList();
  TaskTapPointerEventListener mTapDetector;
  final ArrayList<WindowState> mTapExcludedWindows = new ArrayList();
  private final Matrix mTmpMatrix = new Matrix();
  private final Rect mTmpRect = new Rect();
  private final Rect mTmpRect2 = new Rect();
  private final RectF mTmpRectF = new RectF();
  private final Region mTmpRegion = new Region();
  final ArrayList<Task> mTmpTaskHistory = new ArrayList();
  Region mTouchExcludeRegion = new Region();
  private final WindowList mWindows = new WindowList();
  int pendingLayoutChanges;
  
  DisplayContent(Display paramDisplay, WindowManagerService paramWindowManagerService)
  {
    this.mDisplay = paramDisplay;
    this.mDisplayId = paramDisplay.getDisplayId();
    paramDisplay.getDisplayInfo(this.mDisplayInfo);
    paramDisplay.getMetrics(this.mDisplayMetrics);
    if (this.mDisplayId == 0) {
      bool = true;
    }
    this.isDefaultDisplay = bool;
    this.mService = paramWindowManagerService;
    initializeDisplayBaseInfo();
    this.mDividerControllerLocked = new DockedStackDividerController(paramWindowManagerService, this);
    this.mDimLayerController = new DimLayerController(this);
  }
  
  static void createRotationMatrix(int paramInt, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, Matrix paramMatrix)
  {
    switch (paramInt)
    {
    default: 
      return;
    case 0: 
      paramMatrix.reset();
      return;
    case 3: 
      paramMatrix.setRotate(270.0F, 0.0F, 0.0F);
      paramMatrix.postTranslate(0.0F, paramFloat4);
      paramMatrix.postTranslate(paramFloat2, 0.0F);
      return;
    case 2: 
      paramMatrix.reset();
      return;
    }
    paramMatrix.setRotate(90.0F, 0.0F, 0.0F);
    paramMatrix.postTranslate(paramFloat3, 0.0F);
    paramMatrix.postTranslate(-paramFloat2, paramFloat1);
  }
  
  static void createRotationMatrix(int paramInt, float paramFloat1, float paramFloat2, Matrix paramMatrix)
  {
    createRotationMatrix(paramInt, 0.0F, 0.0F, paramFloat1, paramFloat2, paramMatrix);
  }
  
  static int deltaRotation(int paramInt1, int paramInt2)
  {
    paramInt2 -= paramInt1;
    paramInt1 = paramInt2;
    if (paramInt2 < 0) {
      paramInt1 = paramInt2 + 4;
    }
    return paramInt1;
  }
  
  private void getLogicalDisplayRect(Rect paramRect, int paramInt)
  {
    getLogicalDisplayRect(paramRect);
    paramInt = deltaRotation(this.mDisplayInfo.rotation, paramInt);
    if ((paramInt == 1) || (paramInt == 3))
    {
      createRotationMatrix(paramInt, this.mBaseDisplayWidth, this.mBaseDisplayHeight, this.mTmpMatrix);
      this.mTmpRectF.set(paramRect);
      this.mTmpMatrix.mapRect(this.mTmpRectF);
      this.mTmpRectF.round(paramRect);
    }
  }
  
  boolean animateDimLayers()
  {
    return this.mDimLayerController.animateDimLayers();
  }
  
  void attachStack(TaskStack paramTaskStack, boolean paramBoolean)
  {
    if (paramTaskStack.mStackId == 0)
    {
      if (this.mHomeStack != null) {
        throw new IllegalArgumentException("attachStack: HOME_STACK_ID (0) not first.");
      }
      this.mHomeStack = paramTaskStack;
    }
    if (paramBoolean) {
      this.mStacks.add(paramTaskStack);
    }
    for (;;)
    {
      this.layoutNeeded = true;
      return;
      this.mStacks.add(0, paramTaskStack);
    }
  }
  
  boolean canAddToastWindowForUid(int paramInt)
  {
    if (OpFeatures.isSupport(new int[] { 0 })) {
      return true;
    }
    int j = 0;
    int m = this.mWindows.size();
    int i = 0;
    if (i < m)
    {
      WindowState localWindowState = (WindowState)this.mWindows.get(i);
      if ((localWindowState.isFocused()) && (localWindowState.getOwningUid() == paramInt)) {
        return true;
      }
      int k = j;
      if (localWindowState.mAttrs.type == 2005)
      {
        k = j;
        if (localWindowState.getOwningUid() == paramInt) {
          if (!localWindowState.isRemovedOrHidden()) {
            break label116;
          }
        }
      }
      label116:
      for (k = j;; k = 1)
      {
        i += 1;
        j = k;
        break;
      }
    }
    return j == 0;
  }
  
  void checkForDeferredActions()
  {
    int j = 0;
    int i = this.mStacks.size() - 1;
    while (i >= 0)
    {
      Object localObject = (TaskStack)this.mStacks.get(i);
      int m;
      if (((TaskStack)localObject).isAnimating())
      {
        m = 1;
        i -= 1;
        j = m;
      }
      else
      {
        if (((TaskStack)localObject).mDeferDetach) {
          this.mService.detachStackLocked(this, (TaskStack)localObject);
        }
        localObject = ((TaskStack)localObject).getTasks();
        int k = ((ArrayList)localObject).size() - 1;
        for (;;)
        {
          m = j;
          if (k < 0) {
            break;
          }
          AppTokenList localAppTokenList = ((Task)((ArrayList)localObject).get(k)).mAppTokens;
          m = localAppTokenList.size() - 1;
          while (m >= 0)
          {
            AppWindowToken localAppWindowToken = (AppWindowToken)localAppTokenList.get(m);
            if (localAppWindowToken.mIsExiting) {
              localAppWindowToken.removeAppFromTaskLocked();
            }
            m -= 1;
          }
          k -= 1;
        }
      }
    }
    if ((j == 0) && (this.mDeferredRemoval)) {
      this.mService.onDisplayRemoved(this.mDisplayId);
    }
  }
  
  void close()
  {
    this.mDimLayerController.close();
    int i = this.mStacks.size() - 1;
    while (i >= 0)
    {
      ((TaskStack)this.mStacks.get(i)).close();
      i -= 1;
    }
  }
  
  void detachStack(TaskStack paramTaskStack)
  {
    this.mDimLayerController.removeDimLayerUser(paramTaskStack);
    this.mStacks.remove(paramTaskStack);
  }
  
  public void dump(String paramString, PrintWriter paramPrintWriter)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("Display: mDisplayId=");
    paramPrintWriter.println(this.mDisplayId);
    Object localObject = "  " + paramString;
    paramPrintWriter.print((String)localObject);
    paramPrintWriter.print("init=");
    paramPrintWriter.print(this.mInitialDisplayWidth);
    paramPrintWriter.print("x");
    paramPrintWriter.print(this.mInitialDisplayHeight);
    paramPrintWriter.print(" ");
    paramPrintWriter.print(this.mInitialDisplayDensity);
    paramPrintWriter.print("dpi");
    if ((this.mInitialDisplayWidth != this.mBaseDisplayWidth) || (this.mInitialDisplayHeight != this.mBaseDisplayHeight)) {}
    int i;
    for (;;)
    {
      paramPrintWriter.print(" base=");
      paramPrintWriter.print(this.mBaseDisplayWidth);
      paramPrintWriter.print("x");
      paramPrintWriter.print(this.mBaseDisplayHeight);
      paramPrintWriter.print(" ");
      paramPrintWriter.print(this.mBaseDisplayDensity);
      paramPrintWriter.print("dpi");
      do
      {
        if (this.mDisplayScalingDisabled) {
          paramPrintWriter.println(" noscale");
        }
        paramPrintWriter.print(" cur=");
        paramPrintWriter.print(this.mDisplayInfo.logicalWidth);
        paramPrintWriter.print("x");
        paramPrintWriter.print(this.mDisplayInfo.logicalHeight);
        paramPrintWriter.print(" app=");
        paramPrintWriter.print(this.mDisplayInfo.appWidth);
        paramPrintWriter.print("x");
        paramPrintWriter.print(this.mDisplayInfo.appHeight);
        paramPrintWriter.print(" rng=");
        paramPrintWriter.print(this.mDisplayInfo.smallestNominalAppWidth);
        paramPrintWriter.print("x");
        paramPrintWriter.print(this.mDisplayInfo.smallestNominalAppHeight);
        paramPrintWriter.print("-");
        paramPrintWriter.print(this.mDisplayInfo.largestNominalAppWidth);
        paramPrintWriter.print("x");
        paramPrintWriter.println(this.mDisplayInfo.largestNominalAppHeight);
        paramPrintWriter.print((String)localObject);
        paramPrintWriter.print("deferred=");
        paramPrintWriter.print(this.mDeferredRemoval);
        paramPrintWriter.print(" layoutNeeded=");
        paramPrintWriter.println(this.layoutNeeded);
        paramPrintWriter.println();
        paramPrintWriter.println("  Application tokens in top down Z order:");
        i = this.mStacks.size() - 1;
        while (i >= 0)
        {
          ((TaskStack)this.mStacks.get(i)).dump(paramString + "  ", paramPrintWriter);
          i -= 1;
        }
      } while (this.mInitialDisplayDensity == this.mBaseDisplayDensity);
    }
    paramPrintWriter.println();
    if (!this.mExitingTokens.isEmpty())
    {
      paramPrintWriter.println();
      paramPrintWriter.println("  Exiting tokens:");
      i = this.mExitingTokens.size() - 1;
      while (i >= 0)
      {
        localObject = (WindowToken)this.mExitingTokens.get(i);
        paramPrintWriter.print("  Exiting #");
        paramPrintWriter.print(i);
        paramPrintWriter.print(' ');
        paramPrintWriter.print(localObject);
        paramPrintWriter.println(':');
        ((WindowToken)localObject).dump(paramPrintWriter, "    ");
        i -= 1;
      }
    }
    paramPrintWriter.println();
    this.mDimLayerController.dump(paramString + "  ", paramPrintWriter);
    paramPrintWriter.println();
    this.mDividerControllerLocked.dump(paramString + "  ", paramPrintWriter);
  }
  
  Task findTaskForControlPoint(int paramInt1, int paramInt2)
  {
    Object localObject = this.mService;
    int k = WindowManagerService.dipToPixel(30, this.mDisplayMetrics);
    int i = this.mStacks.size() - 1;
    for (;;)
    {
      if (i >= 0)
      {
        localObject = (TaskStack)this.mStacks.get(i);
        if (ActivityManager.StackId.isTaskResizeAllowed(((TaskStack)localObject).mStackId)) {}
      }
      else
      {
        return null;
      }
      localObject = ((TaskStack)localObject).getTasks();
      int j = ((ArrayList)localObject).size() - 1;
      while (j >= 0)
      {
        Task localTask = (Task)((ArrayList)localObject).get(j);
        if (localTask.isFullscreen()) {
          return null;
        }
        localTask.getDimBounds(this.mTmpRect);
        this.mTmpRect.inset(-k, -k);
        if (this.mTmpRect.contains(paramInt1, paramInt2))
        {
          this.mTmpRect.inset(k, k);
          if (!this.mTmpRect.contains(paramInt1, paramInt2)) {
            return localTask;
          }
          return null;
        }
        j -= 1;
      }
      i -= 1;
    }
  }
  
  void getContentRect(Rect paramRect)
  {
    paramRect.set(this.mContentRect);
  }
  
  Display getDisplay()
  {
    return this.mDisplay;
  }
  
  int getDisplayId()
  {
    return this.mDisplayId;
  }
  
  DisplayInfo getDisplayInfo()
  {
    return this.mDisplayInfo;
  }
  
  DisplayMetrics getDisplayMetrics()
  {
    return this.mDisplayMetrics;
  }
  
  DockedStackDividerController getDockedDividerController()
  {
    return this.mDividerControllerLocked;
  }
  
  TaskStack getDockedStackLocked()
  {
    TaskStack localTaskStack = (TaskStack)this.mService.mStackIdToStack.get(3);
    if ((localTaskStack != null) && (localTaskStack.isVisibleLocked())) {
      return localTaskStack;
    }
    return null;
  }
  
  TaskStack getDockedStackVisibleForUserLocked()
  {
    TaskStack localTaskStack = (TaskStack)this.mService.mStackIdToStack.get(3);
    if ((localTaskStack != null) && (localTaskStack.isVisibleLocked(true))) {
      return localTaskStack;
    }
    return null;
  }
  
  TaskStack getHomeStack()
  {
    if ((this.mHomeStack == null) && (this.mDisplayId == 0)) {
      Slog.e("WindowManager", "getHomeStack: Returning null from this=" + this);
    }
    return this.mHomeStack;
  }
  
  void getLogicalDisplayRect(Rect paramRect)
  {
    int j = 1;
    int k = this.mDisplayInfo.rotation;
    int i = j;
    if (k != 1)
    {
      if (k == 3) {
        i = j;
      }
    }
    else
    {
      if (i == 0) {
        break label97;
      }
      j = this.mBaseDisplayHeight;
      label36:
      if (i == 0) {
        break label105;
      }
    }
    label97:
    label105:
    for (i = this.mBaseDisplayWidth;; i = this.mBaseDisplayHeight)
    {
      k = this.mDisplayInfo.logicalWidth;
      j = (j - k) / 2;
      int m = this.mDisplayInfo.logicalHeight;
      i = (i - m) / 2;
      paramRect.set(j, i, j + k, i + m);
      return;
      i = 0;
      break;
      j = this.mBaseDisplayWidth;
      break label36;
    }
  }
  
  TaskStack getStackById(int paramInt)
  {
    int i = this.mStacks.size() - 1;
    while (i >= 0)
    {
      TaskStack localTaskStack = (TaskStack)this.mStacks.get(i);
      if (localTaskStack.mStackId == paramInt) {
        return localTaskStack;
      }
      i -= 1;
    }
    return null;
  }
  
  ArrayList<TaskStack> getStacks()
  {
    return this.mStacks;
  }
  
  ArrayList<Task> getTasks()
  {
    this.mTmpTaskHistory.clear();
    int j = this.mStacks.size();
    int i = 0;
    while (i < j)
    {
      this.mTmpTaskHistory.addAll(((TaskStack)this.mStacks.get(i)).getTasks());
      i += 1;
    }
    return this.mTmpTaskHistory;
  }
  
  WindowState getTouchableWinAtPointLocked(float paramFloat1, float paramFloat2)
  {
    Object localObject2 = null;
    int j = (int)paramFloat1;
    int k = (int)paramFloat2;
    int i = this.mWindows.size() - 1;
    Object localObject1 = localObject2;
    if (i >= 0)
    {
      localObject1 = (WindowState)this.mWindows.get(i);
      int m = ((WindowState)localObject1).mAttrs.flags;
      if (!((WindowState)localObject1).isVisibleLw()) {}
      do
      {
        do
        {
          do
          {
            i -= 1;
            break;
          } while ((m & 0x10) != 0);
          ((WindowState)localObject1).getVisibleBounds(this.mTmpRect);
        } while (!this.mTmpRect.contains(j, k));
        ((WindowState)localObject1).getTouchableRegion(this.mTmpRegion);
      } while ((!this.mTmpRegion.contains(j, k)) && ((m & 0x28) != 0));
    }
    return (WindowState)localObject1;
  }
  
  WindowList getWindowList()
  {
    return this.mWindows;
  }
  
  public boolean hasAccess(int paramInt)
  {
    return this.mDisplay.hasAccess(paramInt);
  }
  
  void initializeDisplayBaseInfo()
  {
    DisplayInfo localDisplayInfo = this.mService.mDisplayManagerInternal.getDisplayInfo(this.mDisplayId);
    if (localDisplayInfo != null) {
      this.mDisplayInfo.copyFrom(localDisplayInfo);
    }
    int i = this.mDisplayInfo.logicalWidth;
    this.mInitialDisplayWidth = i;
    this.mBaseDisplayWidth = i;
    i = this.mDisplayInfo.logicalHeight;
    this.mInitialDisplayHeight = i;
    this.mBaseDisplayHeight = i;
    i = this.mDisplayInfo.logicalDensityDpi;
    this.mInitialDisplayDensity = i;
    this.mBaseDisplayDensity = i;
    this.mBaseDisplayRect.set(0, 0, this.mBaseDisplayWidth, this.mBaseDisplayHeight);
  }
  
  boolean isAnimating()
  {
    int i = this.mStacks.size() - 1;
    while (i >= 0)
    {
      if (((TaskStack)this.mStacks.get(i)).isAnimating()) {
        return true;
      }
      i -= 1;
    }
    return false;
  }
  
  boolean isDimming()
  {
    return this.mDimLayerController.isDimming();
  }
  
  public boolean isPrivate()
  {
    boolean bool = false;
    if ((this.mDisplay.getFlags() & 0x4) != 0) {
      bool = true;
    }
    return bool;
  }
  
  void moveStack(TaskStack paramTaskStack, boolean paramBoolean)
  {
    if ((!ActivityManager.StackId.isAlwaysOnTop(paramTaskStack.mStackId)) || (paramBoolean))
    {
      if (!this.mStacks.remove(paramTaskStack)) {
        Slog.wtf("WindowManager", "moving stack that was not added: " + paramTaskStack, new Throwable());
      }
      if (!paramBoolean) {
        break label193;
      }
    }
    int j;
    label193:
    for (int i = this.mStacks.size();; i = 0)
    {
      j = i;
      if (!paramBoolean) {
        break;
      }
      j = i;
      if (!this.mService.isStackVisibleLocked(4)) {
        break;
      }
      j = i;
      if (paramTaskStack.mStackId == 4) {
        break;
      }
      i -= 1;
      j = i;
      if (((TaskStack)this.mStacks.get(i)).mStackId == 4) {
        break;
      }
      throw new IllegalStateException("Pinned stack isn't top stack??? " + this.mStacks);
      Slog.w("WindowManager", "Ignoring move of always-on-top stack=" + paramTaskStack + " to bottom");
      return;
    }
    this.mStacks.add(j, paramTaskStack);
  }
  
  void overridePlayingAppAnimationsLw(Animation paramAnimation)
  {
    int i = this.mStacks.size() - 1;
    while (i >= 0)
    {
      ((TaskStack)this.mStacks.get(i)).overridePlayingAppAnimations(paramAnimation);
      i -= 1;
    }
  }
  
  void resetAnimationBackgroundAnimator()
  {
    int i = this.mStacks.size() - 1;
    while (i >= 0)
    {
      ((TaskStack)this.mStacks.get(i)).resetAnimationBackgroundAnimator();
      i -= 1;
    }
  }
  
  void resetDimming()
  {
    this.mDimLayerController.resetDimming();
  }
  
  void resize(Rect paramRect)
  {
    this.mContentRect.set(paramRect);
  }
  
  void rotateBounds(int paramInt1, int paramInt2, Rect paramRect)
  {
    getLogicalDisplayRect(this.mTmpRect, paramInt2);
    createRotationMatrix(deltaRotation(paramInt2, paramInt1), this.mTmpRect.width(), this.mTmpRect.height(), this.mTmpMatrix);
    this.mTmpRectF.set(paramRect);
    this.mTmpMatrix.mapRect(this.mTmpRectF);
    this.mTmpRectF.round(paramRect);
  }
  
  void scheduleToastWindowsTimeoutIfNeededLocked(WindowState paramWindowState1, WindowState paramWindowState2)
  {
    if ((paramWindowState1 == null) || ((paramWindowState2 != null) && (paramWindowState2.mOwnerUid == paramWindowState1.mOwnerUid))) {
      return;
    }
    int j = paramWindowState1.mOwnerUid;
    paramWindowState1 = getWindowList();
    int k = paramWindowState1.size();
    int i = 0;
    while (i < k)
    {
      paramWindowState2 = (WindowState)paramWindowState1.get(i);
      if ((paramWindowState2.mAttrs.type == 2005) && (paramWindowState2.mOwnerUid == j) && (!this.mService.mH.hasMessages(52, paramWindowState2))) {
        this.mService.mH.sendMessageDelayed(this.mService.mH.obtainMessage(52, paramWindowState2), paramWindowState2.mAttrs.hideTimeoutMilliseconds);
      }
      i += 1;
    }
  }
  
  void setTouchExcludeRegion(Task paramTask)
  {
    this.mTouchExcludeRegion.set(this.mBaseDisplayRect);
    Object localObject = this.mService;
    int n = WindowManagerService.dipToPixel(30, this.mDisplayMetrics);
    int i = 0;
    this.mNonResizeableRegion.setEmpty();
    int k = this.mStacks.size() - 1;
    if (k >= 0)
    {
      localObject = (TaskStack)this.mStacks.get(k);
      ArrayList localArrayList = ((TaskStack)localObject).getTasks();
      int m = localArrayList.size() - 1;
      for (;;)
      {
        int j = i;
        if (m >= 0)
        {
          Task localTask = (Task)localArrayList.get(m);
          AppWindowToken localAppWindowToken = localTask.getTopVisibleAppToken();
          j = i;
          if (localAppWindowToken != null)
          {
            j = i;
            if (localAppWindowToken.isVisible())
            {
              localTask.getDimBounds(this.mTmpRect);
              if (localTask == paramTask)
              {
                i = 1;
                this.mTmpRect2.set(this.mTmpRect);
              }
              boolean bool = localTask.inFreeformWorkspace();
              if ((localTask != paramTask) || (bool))
              {
                if (bool)
                {
                  this.mTmpRect.inset(-n, -n);
                  this.mTmpRect.intersect(this.mContentRect);
                }
                this.mTouchExcludeRegion.op(this.mTmpRect, Region.Op.DIFFERENCE);
              }
              j = i;
              if (localTask.isTwoFingerScrollMode())
              {
                ((TaskStack)localObject).getBounds(this.mTmpRect);
                this.mNonResizeableRegion.op(this.mTmpRect, Region.Op.UNION);
                j = i;
              }
            }
          }
        }
        else
        {
          k -= 1;
          i = j;
          break;
        }
        m -= 1;
        i = j;
      }
    }
    if (i != 0) {
      this.mTouchExcludeRegion.op(this.mTmpRect2, Region.Op.UNION);
    }
    paramTask = this.mService.mInputMethodWindow;
    if ((paramTask != null) && (paramTask.isVisibleLw()))
    {
      paramTask.getTouchableRegion(this.mTmpRegion);
      this.mTouchExcludeRegion.op(this.mTmpRegion, Region.Op.UNION);
    }
    i = this.mTapExcludedWindows.size() - 1;
    while (i >= 0)
    {
      ((WindowState)this.mTapExcludedWindows.get(i)).getTouchableRegion(this.mTmpRegion);
      this.mTouchExcludeRegion.op(this.mTmpRegion, Region.Op.UNION);
      i -= 1;
    }
    if (getDockedStackVisibleForUserLocked() != null)
    {
      this.mDividerControllerLocked.getTouchRegion(this.mTmpRect);
      this.mTmpRegion.set(this.mTmpRect);
      this.mTouchExcludeRegion.op(this.mTmpRegion, Region.Op.UNION);
    }
    if (this.mTapDetector != null) {
      this.mTapDetector.setTouchExcludeRegion(this.mTouchExcludeRegion, this.mNonResizeableRegion);
    }
  }
  
  void stopDimmingIfNeeded()
  {
    this.mDimLayerController.stopDimmingIfNeeded();
  }
  
  void switchUserStacks()
  {
    WindowList localWindowList = getWindowList();
    int i = 0;
    while (i < localWindowList.size())
    {
      WindowState localWindowState = (WindowState)localWindowList.get(i);
      if (localWindowState.isHiddenFromUserLocked())
      {
        if (WindowManagerDebugConfig.DEBUG_VISIBILITY) {
          Slog.w("WindowManager", "user changing, hiding " + localWindowState + ", attrs=" + localWindowState.mAttrs.type + ", belonging to " + localWindowState.mOwnerUid);
        }
        localWindowState.hideLw(false);
      }
      i += 1;
    }
    i = this.mStacks.size() - 1;
    while (i >= 0)
    {
      ((TaskStack)this.mStacks.get(i)).switchUser();
      i -= 1;
    }
  }
  
  int taskIdFromPoint(int paramInt1, int paramInt2)
  {
    int i = this.mStacks.size() - 1;
    while (i >= 0)
    {
      Object localObject = (TaskStack)this.mStacks.get(i);
      ((TaskStack)localObject).getBounds(this.mTmpRect);
      if ((!this.mTmpRect.contains(paramInt1, paramInt2)) || (((TaskStack)localObject).isAdjustedForMinimizedDockedStack()))
      {
        i -= 1;
      }
      else
      {
        localObject = ((TaskStack)localObject).getTasks();
        int j = ((ArrayList)localObject).size() - 1;
        label79:
        Task localTask;
        if (j >= 0)
        {
          localTask = (Task)((ArrayList)localObject).get(j);
          if (localTask.getTopVisibleAppMainWindow() != null) {
            break label113;
          }
        }
        label113:
        do
        {
          j -= 1;
          break label79;
          break;
          localTask.getDimBounds(this.mTmpRect);
        } while (!this.mTmpRect.contains(paramInt1, paramInt2));
        return localTask.mTaskId;
      }
    }
    return -1;
  }
  
  public String toString()
  {
    return "Display " + this.mDisplayId + " info=" + this.mDisplayInfo + " stacks=" + this.mStacks;
  }
  
  void updateDisplayInfo()
  {
    this.mDisplay.getDisplayInfo(this.mDisplayInfo);
    this.mDisplay.getMetrics(this.mDisplayMetrics);
    int i = this.mStacks.size() - 1;
    while (i >= 0)
    {
      ((TaskStack)this.mStacks.get(i)).updateDisplayInfo(null);
      i -= 1;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/DisplayContent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */