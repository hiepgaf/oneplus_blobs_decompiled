package com.android.server.wm;

import android.app.ActivityManager.StackId;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Message;
import android.util.EventLog;
import android.util.Slog;
import android.util.SparseArray;
import android.view.DisplayInfo;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import java.io.PrintWriter;
import java.util.ArrayList;

class Task
  implements DimLayer.DimLayerUser
{
  static final int BOUNDS_CHANGE_NONE = 0;
  static final int BOUNDS_CHANGE_POSITION = 1;
  static final int BOUNDS_CHANGE_SIZE = 2;
  static final String TAG = "WindowManager";
  final AppTokenList mAppTokens = new AppTokenList();
  private Rect mBounds = new Rect();
  boolean mDeferRemoval = false;
  private int mDragResizeMode;
  private boolean mDragResizing;
  private boolean mFullscreen = true;
  private boolean mHomeTask;
  Configuration mOverrideConfig = Configuration.EMPTY;
  private Rect mPreScrollBounds = new Rect();
  final Rect mPreparedFrozenBounds = new Rect();
  final Configuration mPreparedFrozenMergedConfig = new Configuration();
  private int mResizeMode;
  int mRotation;
  private boolean mScrollValid;
  final WindowManagerService mService;
  TaskStack mStack;
  final int mTaskId;
  private final Rect mTempInsetBounds = new Rect();
  private Rect mTmpRect = new Rect();
  private Rect mTmpRect2 = new Rect();
  final int mUserId;
  
  Task(int paramInt1, TaskStack paramTaskStack, int paramInt2, WindowManagerService paramWindowManagerService, Rect paramRect, Configuration paramConfiguration)
  {
    this.mTaskId = paramInt1;
    this.mStack = paramTaskStack;
    this.mUserId = paramInt2;
    this.mService = paramWindowManagerService;
    setBounds(paramRect, paramConfiguration);
  }
  
  private boolean hasWindowsAlive()
  {
    int i = this.mAppTokens.size() - 1;
    while (i >= 0)
    {
      if (((AppWindowToken)this.mAppTokens.get(i)).hasWindowsAlive()) {
        return true;
      }
      i -= 1;
    }
    return false;
  }
  
  private boolean inCropWindowsResizeMode()
  {
    if ((this.mHomeTask) || (isResizeable())) {}
    while (this.mResizeMode != 1) {
      return false;
    }
    return true;
  }
  
  private int setBounds(Rect paramRect, Configuration paramConfiguration)
  {
    Configuration localConfiguration = paramConfiguration;
    if (paramConfiguration == null) {
      localConfiguration = Configuration.EMPTY;
    }
    if ((paramRect != null) || (Configuration.EMPTY.equals(localConfiguration)))
    {
      if ((paramRect != null) && (Configuration.EMPTY.equals(localConfiguration))) {
        throw new IllegalArgumentException("non null bounds, but empty configuration");
      }
    }
    else {
      throw new IllegalArgumentException("null bounds but non empty configuration: " + localConfiguration);
    }
    boolean bool2 = this.mFullscreen;
    int j = 0;
    DisplayContent localDisplayContent = this.mStack.getDisplayContent();
    paramConfiguration = paramRect;
    if (localDisplayContent != null)
    {
      localDisplayContent.getLogicalDisplayRect(this.mTmpRect);
      i = localDisplayContent.getDisplayInfo().rotation;
      if (paramRect != null) {
        break label162;
      }
    }
    label162:
    for (boolean bool1 = true;; bool1 = false)
    {
      this.mFullscreen = bool1;
      j = i;
      paramConfiguration = paramRect;
      if (this.mFullscreen)
      {
        paramConfiguration = this.mTmpRect;
        j = i;
      }
      if (paramConfiguration != null) {
        break;
      }
      return 0;
    }
    if ((this.mPreScrollBounds.equals(paramConfiguration)) && (bool2 == this.mFullscreen) && (this.mRotation == j)) {
      return 0;
    }
    int i = 0;
    if ((this.mPreScrollBounds.left != paramConfiguration.left) || (this.mPreScrollBounds.top != paramConfiguration.top)) {
      i = 1;
    }
    int k;
    if (this.mPreScrollBounds.width() == paramConfiguration.width())
    {
      k = i;
      if (this.mPreScrollBounds.height() == paramConfiguration.height()) {}
    }
    else
    {
      k = i | 0x2;
    }
    this.mPreScrollBounds.set(paramConfiguration);
    resetScrollLocked();
    this.mRotation = j;
    if (localDisplayContent != null) {
      localDisplayContent.mDimLayerController.updateDimLayer(this);
    }
    if (this.mFullscreen) {
      localConfiguration = Configuration.EMPTY;
    }
    this.mOverrideConfig = localConfiguration;
    return k;
  }
  
  private boolean useCurrentBounds()
  {
    DisplayContent localDisplayContent = this.mStack.getDisplayContent();
    if ((this.mFullscreen) || (!ActivityManager.StackId.isTaskResizeableByDockedStack(this.mStack.mStackId)) || (localDisplayContent == null)) {}
    while (localDisplayContent.getDockedStackVisibleForUserLocked() != null) {
      return true;
    }
    return false;
  }
  
  void addAppToken(int paramInt1, AppWindowToken paramAppWindowToken, int paramInt2, boolean paramBoolean)
  {
    int k = this.mAppTokens.size();
    int j;
    if (paramInt1 >= k)
    {
      j = k;
      this.mAppTokens.add(j, paramAppWindowToken);
      paramAppWindowToken.mTask = this;
      this.mDeferRemoval = false;
      this.mResizeMode = paramInt2;
      this.mHomeTask = paramBoolean;
      return;
    }
    int i = 0;
    for (;;)
    {
      j = paramInt1;
      if (i >= k) {
        break;
      }
      j = paramInt1;
      if (i >= paramInt1) {
        break;
      }
      j = paramInt1;
      if (((AppWindowToken)this.mAppTokens.get(i)).removed) {
        j = paramInt1 + 1;
      }
      i += 1;
      paramInt1 = j;
    }
  }
  
  void addWindowsWaitingForDrawnIfResizingChanged()
  {
    int i = this.mAppTokens.size() - 1;
    while (i >= 0)
    {
      WindowList localWindowList = ((AppWindowToken)this.mAppTokens.get(i)).allAppWindows;
      int j = localWindowList.size() - 1;
      while (j >= 0)
      {
        WindowState localWindowState = (WindowState)localWindowList.get(j);
        if (localWindowState.isDragResizeChanged()) {
          this.mService.mWaitingForDrawn.add(localWindowState);
        }
        j -= 1;
      }
      i -= 1;
    }
  }
  
  void alignToAdjustedBounds(Rect paramRect1, Rect paramRect2, boolean paramBoolean)
  {
    if ((!isResizeable()) || (this.mOverrideConfig == Configuration.EMPTY)) {
      return;
    }
    getBounds(this.mTmpRect2);
    if (paramBoolean)
    {
      int i = paramRect1.bottom;
      int j = this.mTmpRect2.bottom;
      this.mTmpRect2.offset(0, i - j);
    }
    for (;;)
    {
      setTempInsetBounds(paramRect2);
      resizeLocked(this.mTmpRect2, this.mOverrideConfig, false);
      return;
      this.mTmpRect2.offsetTo(paramRect1.left, paramRect1.top);
    }
  }
  
  void applyScrollToAllWindows(int paramInt1, int paramInt2)
  {
    int i = this.mAppTokens.size() - 1;
    while (i >= 0)
    {
      WindowList localWindowList = ((AppWindowToken)this.mAppTokens.get(i)).allAppWindows;
      int j = localWindowList.size() - 1;
      while (j >= 0)
      {
        WindowState localWindowState = (WindowState)localWindowList.get(j);
        localWindowState.mXOffset = paramInt1;
        localWindowState.mYOffset = paramInt2;
        j -= 1;
      }
      i -= 1;
    }
  }
  
  void applyScrollToWindowIfNeeded(WindowState paramWindowState)
  {
    if (this.mScrollValid)
    {
      paramWindowState.mXOffset = this.mBounds.left;
      paramWindowState.mYOffset = this.mBounds.top;
    }
  }
  
  void cancelTaskThumbnailTransition()
  {
    int i = this.mAppTokens.size() - 1;
    while (i >= 0)
    {
      ((AppWindowToken)this.mAppTokens.get(i)).mAppAnimator.clearThumbnail();
      i -= 1;
    }
  }
  
  void cancelTaskWindowTransition()
  {
    int i = this.mAppTokens.size() - 1;
    while (i >= 0)
    {
      ((AppWindowToken)this.mAppTokens.get(i)).mAppAnimator.clearAnimation();
      i -= 1;
    }
  }
  
  boolean cropWindowsToStackBounds()
  {
    return (!this.mHomeTask) && ((isResizeable()) || (this.mResizeMode == 1));
  }
  
  public boolean dimFullscreen()
  {
    if (!isHomeTask()) {
      return isFullscreen();
    }
    return true;
  }
  
  public void dump(String paramString, PrintWriter paramPrintWriter)
  {
    Object localObject = paramString + "  ";
    paramPrintWriter.println(paramString + "taskId=" + this.mTaskId);
    paramPrintWriter.println((String)localObject + "mFullscreen=" + this.mFullscreen);
    paramPrintWriter.println((String)localObject + "mBounds=" + this.mBounds.toShortString());
    paramPrintWriter.println((String)localObject + "mdr=" + this.mDeferRemoval);
    paramPrintWriter.println((String)localObject + "appTokens=" + this.mAppTokens);
    paramPrintWriter.println((String)localObject + "mTempInsetBounds=" + this.mTempInsetBounds.toShortString());
    paramString = (String)localObject + "  ";
    int i = this.mAppTokens.size() - 1;
    while (i >= 0)
    {
      localObject = (AppWindowToken)this.mAppTokens.get(i);
      paramPrintWriter.println(paramString + "Activity #" + i + " " + localObject);
      ((AppWindowToken)localObject).dump(paramPrintWriter, paramString);
      i -= 1;
    }
  }
  
  void getBounds(Rect paramRect)
  {
    if (useCurrentBounds())
    {
      paramRect.set(this.mBounds);
      return;
    }
    this.mStack.getDisplayContent().getLogicalDisplayRect(paramRect);
  }
  
  public void getDimBounds(Rect paramRect)
  {
    DisplayContent localDisplayContent = this.mStack.getDisplayContent();
    boolean bool;
    if (localDisplayContent != null) {
      bool = localDisplayContent.mDividerControllerLocked.isResizing();
    }
    while (useCurrentBounds()) {
      if ((inFreeformWorkspace()) && (getMaxVisibleBounds(paramRect)))
      {
        return;
        bool = false;
      }
      else
      {
        if (!this.mFullscreen)
        {
          if (bool) {
            this.mStack.getBounds(paramRect);
          }
          for (;;)
          {
            paramRect.set(this.mTmpRect);
            return;
            this.mStack.getBounds(this.mTmpRect);
            this.mTmpRect.intersect(this.mBounds);
          }
        }
        paramRect.set(this.mBounds);
        return;
      }
    }
    localDisplayContent.getLogicalDisplayRect(paramRect);
  }
  
  DisplayContent getDisplayContent()
  {
    return this.mStack.getDisplayContent();
  }
  
  public DisplayInfo getDisplayInfo()
  {
    return this.mStack.getDisplayContent().getDisplayInfo();
  }
  
  int getDragResizeMode()
  {
    return this.mDragResizeMode;
  }
  
  boolean getMaxVisibleBounds(Rect paramRect)
  {
    boolean bool1 = false;
    int i = this.mAppTokens.size() - 1;
    if (i >= 0)
    {
      Object localObject = (AppWindowToken)this.mAppTokens.get(i);
      boolean bool2 = bool1;
      if (!((AppWindowToken)localObject).mIsExiting)
      {
        bool2 = bool1;
        if (!((AppWindowToken)localObject).clientHidden)
        {
          if (!((AppWindowToken)localObject).hiddenRequested) {
            break label72;
          }
          bool2 = bool1;
        }
      }
      for (;;)
      {
        i -= 1;
        bool1 = bool2;
        break;
        label72:
        localObject = ((AppWindowToken)localObject).findMainWindow();
        bool2 = bool1;
        if (localObject != null) {
          if (!bool1)
          {
            paramRect.set(((WindowState)localObject).mVisibleFrame);
            bool2 = true;
          }
          else
          {
            if (((WindowState)localObject).mVisibleFrame.left < paramRect.left) {
              paramRect.left = ((WindowState)localObject).mVisibleFrame.left;
            }
            if (((WindowState)localObject).mVisibleFrame.top < paramRect.top) {
              paramRect.top = ((WindowState)localObject).mVisibleFrame.top;
            }
            if (((WindowState)localObject).mVisibleFrame.right > paramRect.right) {
              paramRect.right = ((WindowState)localObject).mVisibleFrame.right;
            }
            bool2 = bool1;
            if (((WindowState)localObject).mVisibleFrame.bottom > paramRect.bottom)
            {
              paramRect.bottom = ((WindowState)localObject).mVisibleFrame.bottom;
              bool2 = bool1;
            }
          }
        }
      }
    }
    return bool1;
  }
  
  void getTempInsetBounds(Rect paramRect)
  {
    paramRect.set(this.mTempInsetBounds);
  }
  
  AppWindowToken getTopAppToken()
  {
    if (this.mAppTokens.size() > 0) {
      return (AppWindowToken)this.mAppTokens.get(this.mAppTokens.size() - 1);
    }
    return null;
  }
  
  WindowState getTopVisibleAppMainWindow()
  {
    WindowState localWindowState = null;
    AppWindowToken localAppWindowToken = getTopVisibleAppToken();
    if (localAppWindowToken != null) {
      localWindowState = localAppWindowToken.findMainWindow();
    }
    return localWindowState;
  }
  
  AppWindowToken getTopVisibleAppToken()
  {
    int i = this.mAppTokens.size() - 1;
    if (i >= 0)
    {
      AppWindowToken localAppWindowToken = (AppWindowToken)this.mAppTokens.get(i);
      if ((localAppWindowToken.mIsExiting) || (localAppWindowToken.clientHidden)) {}
      while (localAppWindowToken.hiddenRequested)
      {
        i -= 1;
        break;
      }
      return localAppWindowToken;
    }
    return null;
  }
  
  boolean inDockedWorkspace()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mStack != null)
    {
      bool1 = bool2;
      if (this.mStack.mStackId == 3) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  boolean inFreeformWorkspace()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mStack != null)
    {
      bool1 = bool2;
      if (this.mStack.mStackId == 2) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  boolean inHomeStack()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mStack != null)
    {
      bool1 = bool2;
      if (this.mStack.mStackId == 0) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  boolean inPinnedWorkspace()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mStack != null)
    {
      bool1 = bool2;
      if (this.mStack.mStackId == 4) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  boolean isDockedInEffect()
  {
    if (!inDockedWorkspace()) {
      return isResizeableByDockedStack();
    }
    return true;
  }
  
  boolean isDragResizing()
  {
    if (!this.mDragResizing)
    {
      if (this.mStack != null) {
        return this.mStack.isDragResizing();
      }
    }
    else {
      return true;
    }
    return false;
  }
  
  boolean isFloating()
  {
    return ActivityManager.StackId.tasksAreFloating(this.mStack.mStackId);
  }
  
  boolean isFullscreen()
  {
    if (useCurrentBounds()) {
      return this.mFullscreen;
    }
    return true;
  }
  
  boolean isHomeTask()
  {
    return this.mHomeTask;
  }
  
  boolean isResizeable()
  {
    if (!this.mHomeTask)
    {
      if (!ActivityInfo.isResizeableMode(this.mResizeMode)) {
        return this.mService.mForceResizableTasks;
      }
      return true;
    }
    return false;
  }
  
  boolean isResizeableByDockedStack()
  {
    DisplayContent localDisplayContent = getDisplayContent();
    if ((localDisplayContent != null) && (localDisplayContent.getDockedStackLocked() != null) && (this.mStack != null)) {
      return ActivityManager.StackId.isTaskResizeableByDockedStack(this.mStack.mStackId);
    }
    return false;
  }
  
  boolean isTwoFingerScrollMode()
  {
    if (inCropWindowsResizeMode()) {
      return isDockedInEffect();
    }
    return false;
  }
  
  boolean isVisible()
  {
    int i = this.mAppTokens.size() - 1;
    while (i >= 0)
    {
      if (((AppWindowToken)this.mAppTokens.get(i)).isVisible()) {
        return true;
      }
      i -= 1;
    }
    return false;
  }
  
  void moveTaskToStack(TaskStack paramTaskStack, boolean paramBoolean)
  {
    if (paramTaskStack == this.mStack) {
      return;
    }
    if (WindowManagerDebugConfig.DEBUG_STACK) {
      Slog.i(TAG, "moveTaskToStack: removing taskId=" + this.mTaskId + " from stack=" + this.mStack);
    }
    EventLog.writeEvent(31003, new Object[] { Integer.valueOf(this.mTaskId), "moveTask" });
    if (this.mStack != null) {
      this.mStack.removeTask(this);
    }
    paramTaskStack.addTask(this, paramBoolean);
  }
  
  void moveWindows()
  {
    int i = this.mAppTokens.size() - 1;
    while (i >= 0)
    {
      WindowList localWindowList = ((AppWindowToken)this.mAppTokens.get(i)).allAppWindows;
      int j = localWindowList.size() - 1;
      while (j >= 0)
      {
        WindowState localWindowState = (WindowState)localWindowList.get(j);
        if (WindowManagerDebugConfig.DEBUG_RESIZE) {
          Slog.d(TAG, "moveWindows: Moving " + localWindowState);
        }
        localWindowState.mMovedByResize = true;
        j -= 1;
      }
      i -= 1;
    }
  }
  
  void overridePlayingAppAnimations(Animation paramAnimation)
  {
    int i = this.mAppTokens.size() - 1;
    while (i >= 0)
    {
      ((AppWindowToken)this.mAppTokens.get(i)).overridePlayingAppAnimations(paramAnimation);
      i -= 1;
    }
  }
  
  void positionTaskInStack(TaskStack paramTaskStack, int paramInt, Rect paramRect, Configuration paramConfiguration)
  {
    if ((this.mStack != null) && (paramTaskStack != this.mStack))
    {
      if (WindowManagerDebugConfig.DEBUG_STACK) {
        Slog.i(TAG, "positionTaskInStack: removing taskId=" + this.mTaskId + " from stack=" + this.mStack);
      }
      EventLog.writeEvent(31003, new Object[] { Integer.valueOf(this.mTaskId), "moveTask" });
      this.mStack.removeTask(this);
    }
    paramTaskStack.positionTask(this, paramInt, showForAllUsers());
    resizeLocked(paramRect, paramConfiguration, false);
    paramInt = this.mAppTokens.size() - 1;
    while (paramInt >= 0)
    {
      paramTaskStack = ((AppWindowToken)this.mAppTokens.get(paramInt)).allAppWindows;
      int i = paramTaskStack.size() - 1;
      while (i >= 0)
      {
        ((WindowState)paramTaskStack.get(i)).notifyMovedInStack();
        i -= 1;
      }
      paramInt -= 1;
    }
  }
  
  void prepareFreezingBounds()
  {
    this.mPreparedFrozenBounds.set(this.mBounds);
    this.mPreparedFrozenMergedConfig.setTo(this.mService.mCurConfiguration);
    this.mPreparedFrozenMergedConfig.updateFrom(this.mOverrideConfig);
  }
  
  boolean removeAppToken(AppWindowToken paramAppWindowToken)
  {
    boolean bool = this.mAppTokens.remove(paramAppWindowToken);
    if (this.mAppTokens.size() == 0)
    {
      EventLog.writeEvent(31003, new Object[] { Integer.valueOf(this.mTaskId), "removeAppToken: last token" });
      if (this.mDeferRemoval) {
        removeLocked();
      }
    }
    paramAppWindowToken.mTask = null;
    return bool;
  }
  
  void removeLocked()
  {
    if ((hasWindowsAlive()) && (this.mStack.isAnimating()))
    {
      if (WindowManagerDebugConfig.DEBUG_STACK) {
        Slog.i(TAG, "removeTask: deferring removing taskId=" + this.mTaskId);
      }
      this.mDeferRemoval = true;
      return;
    }
    if (WindowManagerDebugConfig.DEBUG_STACK) {
      Slog.i(TAG, "removeTask: removing taskId=" + this.mTaskId);
    }
    EventLog.writeEvent(31003, new Object[] { Integer.valueOf(this.mTaskId), "removeTask" });
    this.mDeferRemoval = false;
    DisplayContent localDisplayContent = getDisplayContent();
    if (localDisplayContent != null) {
      localDisplayContent.mDimLayerController.removeDimLayerUser(this);
    }
    this.mStack.removeTask(this);
    this.mService.mTaskIdToTask.delete(this.mTaskId);
  }
  
  void resetDragResizingChangeReported()
  {
    int i = this.mAppTokens.size() - 1;
    while (i >= 0)
    {
      WindowList localWindowList = ((AppWindowToken)this.mAppTokens.get(i)).allAppWindows;
      int j = localWindowList.size() - 1;
      while (j >= 0)
      {
        ((WindowState)localWindowList.get(j)).resetDragResizingChangeReported();
        j -= 1;
      }
      i -= 1;
    }
  }
  
  void resetScrollLocked()
  {
    if (this.mScrollValid)
    {
      this.mScrollValid = false;
      applyScrollToAllWindows(0, 0);
    }
    this.mBounds.set(this.mPreScrollBounds);
  }
  
  boolean resizeLocked(Rect paramRect, Configuration paramConfiguration, boolean paramBoolean)
  {
    int j = setBounds(paramRect, paramConfiguration);
    int i = j;
    if (paramBoolean) {
      i = j | 0x2;
    }
    if (i == 0) {
      return false;
    }
    if ((i & 0x2) == 2) {
      resizeWindows();
    }
    for (;;)
    {
      return true;
      moveWindows();
    }
  }
  
  void resizeWindows()
  {
    ArrayList localArrayList = this.mService.mResizingWindows;
    int i = this.mAppTokens.size() - 1;
    while (i >= 0)
    {
      Object localObject = (AppWindowToken)this.mAppTokens.get(i);
      ((AppWindowToken)localObject).destroySavedSurfaces();
      localObject = ((AppWindowToken)localObject).allAppWindows;
      int j = ((ArrayList)localObject).size() - 1;
      if (j >= 0)
      {
        WindowState localWindowState = (WindowState)((ArrayList)localObject).get(j);
        if ((!localWindowState.mHasSurface) || (localArrayList.contains(localWindowState))) {}
        for (;;)
        {
          if (localWindowState.isGoneForLayoutLw()) {
            localWindowState.mResizedWhileGone = true;
          }
          j -= 1;
          break;
          if (WindowManagerDebugConfig.DEBUG_RESIZE) {
            Slog.d(TAG, "resizeWindows: Resizing " + localWindowState);
          }
          localArrayList.add(localWindowState);
          if ((!localWindowState.computeDragResizing()) && (localWindowState.mAttrs.type == 1) && (!this.mStack.getBoundsAnimating()) && (!localWindowState.isGoneForLayoutLw()) && (!inPinnedWorkspace())) {
            localWindowState.setResizedWhileNotDragResizing(true);
          }
        }
      }
      i -= 1;
    }
  }
  
  boolean scrollLocked(Rect paramRect)
  {
    this.mStack.getDimBounds(this.mTmpRect);
    if (this.mService.mCurConfiguration.orientation == 2) {
      if (paramRect.left > this.mTmpRect.left)
      {
        paramRect.left = this.mTmpRect.left;
        paramRect.right = (this.mTmpRect.left + this.mBounds.width());
      }
    }
    while ((this.mScrollValid) && (paramRect.equals(this.mBounds)))
    {
      return false;
      if (paramRect.right < this.mTmpRect.right)
      {
        paramRect.left = (this.mTmpRect.right - this.mBounds.width());
        paramRect.right = this.mTmpRect.right;
        continue;
        if (paramRect.top > this.mTmpRect.top)
        {
          paramRect.top = this.mTmpRect.top;
          paramRect.bottom = (this.mTmpRect.top + this.mBounds.height());
        }
        else if (paramRect.bottom < this.mTmpRect.bottom)
        {
          paramRect.top = (this.mTmpRect.bottom - this.mBounds.height());
          paramRect.bottom = this.mTmpRect.bottom;
        }
      }
    }
    this.mBounds.set(paramRect);
    this.mScrollValid = true;
    applyScrollToAllWindows(paramRect.left, paramRect.top);
    return true;
  }
  
  void setDragResizing(boolean paramBoolean, int paramInt)
  {
    if (this.mDragResizing != paramBoolean)
    {
      if (!DragResizeMode.isModeAllowedForStack(this.mStack.mStackId, paramInt)) {
        throw new IllegalArgumentException("Drag resize mode not allow for stack stackId=" + this.mStack.mStackId + " dragResizeMode=" + paramInt);
      }
      this.mDragResizing = paramBoolean;
      this.mDragResizeMode = paramInt;
      resetDragResizingChangeReported();
    }
  }
  
  void setResizeable(int paramInt)
  {
    this.mResizeMode = paramInt;
  }
  
  void setSendingToBottom(boolean paramBoolean)
  {
    int i = 0;
    while (i < this.mAppTokens.size())
    {
      ((AppWindowToken)this.mAppTokens.get(i)).sendingToBottom = paramBoolean;
      i += 1;
    }
  }
  
  void setTempInsetBounds(Rect paramRect)
  {
    if (paramRect != null)
    {
      this.mTempInsetBounds.set(paramRect);
      return;
    }
    this.mTempInsetBounds.setEmpty();
  }
  
  boolean showForAllUsers()
  {
    boolean bool = false;
    int i = this.mAppTokens.size();
    if (i != 0) {
      bool = ((AppWindowToken)this.mAppTokens.get(i - 1)).showForAllUsers;
    }
    return bool;
  }
  
  public String toShortString()
  {
    return "Task=" + this.mTaskId;
  }
  
  public String toString()
  {
    return "{taskId=" + this.mTaskId + " appTokens=" + this.mAppTokens + " mdr=" + this.mDeferRemoval + "}";
  }
  
  void updateDisplayInfo(DisplayContent paramDisplayContent)
  {
    if (paramDisplayContent == null) {
      return;
    }
    if (this.mFullscreen)
    {
      setBounds(null, Configuration.EMPTY);
      return;
    }
    int i = paramDisplayContent.getDisplayInfo().rotation;
    if (this.mRotation == i) {
      return;
    }
    this.mTmpRect2.set(this.mPreScrollBounds);
    if (!ActivityManager.StackId.isTaskResizeAllowed(this.mStack.mStackId))
    {
      setBounds(this.mTmpRect2, this.mOverrideConfig);
      return;
    }
    paramDisplayContent.rotateBounds(this.mRotation, i, this.mTmpRect2);
    if (setBounds(this.mTmpRect2, this.mOverrideConfig) != 0) {
      this.mService.mH.obtainMessage(43, this.mTaskId, 1, this.mPreScrollBounds).sendToTarget();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/Task.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */