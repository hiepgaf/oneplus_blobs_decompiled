package com.android.server.wm;

import android.app.ActivityManager.StackId;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Message;
import android.os.RemoteException;
import android.util.Slog;
import android.view.IApplicationToken;
import android.view.IWindow;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import com.android.server.am.OnePlusProcessManager;
import com.android.server.input.InputApplicationHandle;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;

class AppWindowToken
  extends WindowToken
{
  private static final String TAG = "WindowManager";
  final WindowList allAppWindows = new WindowList();
  boolean allDrawn;
  boolean allDrawnExcludingSaved;
  boolean appFullscreen;
  final IApplicationToken appToken;
  boolean clientHidden;
  boolean deferClearAllDrawn;
  boolean firstWindowDrawn;
  boolean hiddenRequested;
  boolean inPendingTransaction;
  long inputDispatchingTimeoutNanos;
  long lastTransactionSequence = Long.MIN_VALUE;
  boolean layoutConfigChanges;
  boolean mAlwaysFocusable;
  final AppWindowAnimator mAppAnimator;
  boolean mAppStopped;
  boolean mEnteringAnimation;
  ArrayDeque<Rect> mFrozenBounds = new ArrayDeque();
  ArrayDeque<Configuration> mFrozenMergedConfig = new ArrayDeque();
  final InputApplicationHandle mInputApplicationHandle;
  boolean mIsExiting;
  boolean mLaunchTaskBehind;
  int mPendingRelaunchCount;
  int mRotationAnimationHint;
  private ArrayList<WindowSurfaceController.SurfaceControlWithBackground> mSurfaceViewBackgrounds = new ArrayList();
  Task mTask;
  int numDrawnWindows;
  int numDrawnWindowsExclusingSaved;
  int numInterestingWindows;
  int numInterestingWindowsExcludingSaved;
  boolean removed;
  boolean reportedDrawn;
  boolean reportedVisible;
  int requestedOrientation = -1;
  boolean showForAllUsers;
  StartingData startingData;
  boolean startingDisplayed;
  boolean startingMoved;
  View startingView;
  WindowState startingWindow;
  int targetSdk;
  final boolean voiceInteraction;
  
  AppWindowToken(WindowManagerService paramWindowManagerService, IApplicationToken paramIApplicationToken, boolean paramBoolean)
  {
    super(paramWindowManagerService, paramIApplicationToken.asBinder(), 2, true);
    this.appWindowToken = this;
    this.appToken = paramIApplicationToken;
    this.voiceInteraction = paramBoolean;
    this.mInputApplicationHandle = new InputApplicationHandle(this);
    this.mAppAnimator = new AppWindowAnimator(this);
  }
  
  private boolean canFreezeBounds()
  {
    return (this.mTask != null) && (!this.mTask.inFreeformWorkspace());
  }
  
  private void destroySurfaces(boolean paramBoolean)
  {
    Object localObject = (ArrayList)this.allAppWindows.clone();
    DisplayContentList localDisplayContentList = new DisplayContentList();
    int i = ((ArrayList)localObject).size() - 1;
    if (i >= 0)
    {
      WindowState localWindowState = (WindowState)((ArrayList)localObject).get(i);
      boolean bool;
      if ((!this.mAppStopped) && (!localWindowState.mWindowRemovalAllowed))
      {
        bool = paramBoolean;
        label61:
        if (bool) {
          break label77;
        }
      }
      label77:
      do
      {
        i -= 1;
        break;
        bool = true;
        break label61;
        localWindowState.mWinAnimator.destroyPreservedSurfaceLocked();
      } while (!localWindowState.mDestroying);
      if (WindowManagerDebugConfig.DEBUG_ADD_REMOVE) {
        Slog.e("WindowManager", "win=" + localWindowState + " destroySurfaces: mAppStopped=" + this.mAppStopped + " win.mWindowRemovalAllowed=" + localWindowState.mWindowRemovalAllowed + " win.mRemoveOnExit=" + localWindowState.mRemoveOnExit);
      }
      if ((!paramBoolean) || (localWindowState.mRemoveOnExit)) {
        localWindowState.destroyOrSaveSurface();
      }
      if (localWindowState.mRemoveOnExit) {
        this.service.removeWindowInnerLocked(localWindowState);
      }
      DisplayContent localDisplayContent = localWindowState.getDisplayContent();
      if ((localDisplayContent == null) || (localDisplayContentList.contains(localDisplayContent))) {}
      for (;;)
      {
        if (paramBoolean) {
          localWindowState.requestUpdateWallpaperIfNeeded();
        }
        localWindowState.mDestroying = false;
        break;
        localDisplayContentList.add(localDisplayContent);
      }
    }
    i = 0;
    while (i < localDisplayContentList.size())
    {
      localObject = (DisplayContent)localDisplayContentList.get(i);
      this.service.mLayersController.assignLayersLocked(((DisplayContent)localObject).getWindowList());
      ((DisplayContent)localObject).layoutNeeded = true;
      i += 1;
    }
  }
  
  private void freezeBounds()
  {
    this.mFrozenBounds.offer(new Rect(this.mTask.mPreparedFrozenBounds));
    if (this.mTask.mPreparedFrozenMergedConfig.equals(Configuration.EMPTY))
    {
      Configuration localConfiguration = new Configuration(this.service.mCurConfiguration);
      localConfiguration.updateFrom(this.mTask.mOverrideConfig);
      this.mFrozenMergedConfig.offer(localConfiguration);
    }
    for (;;)
    {
      this.mTask.mPreparedFrozenMergedConfig.setToDefaults();
      return;
      this.mFrozenMergedConfig.offer(new Configuration(this.mTask.mPreparedFrozenMergedConfig));
    }
  }
  
  private void unfreezeBounds()
  {
    if (!this.mFrozenBounds.isEmpty()) {
      this.mFrozenBounds.remove();
    }
    if (!this.mFrozenMergedConfig.isEmpty()) {
      this.mFrozenMergedConfig.remove();
    }
    int i = this.windows.size() - 1;
    if (i >= 0)
    {
      WindowState localWindowState = (WindowState)this.windows.get(i);
      if (!localWindowState.mHasSurface) {}
      for (;;)
      {
        i -= 1;
        break;
        localWindowState.mLayoutNeeded = true;
        localWindowState.setDisplayLayoutNeeded();
        if (!this.service.mResizingWindows.contains(localWindowState)) {
          this.service.mResizingWindows.add(localWindowState);
        }
      }
    }
    this.service.mWindowPlacerLocked.performSurfacePlacement();
  }
  
  void addSurfaceViewBackground(WindowSurfaceController.SurfaceControlWithBackground paramSurfaceControlWithBackground)
  {
    this.mSurfaceViewBackgrounds.add(paramSurfaceControlWithBackground);
  }
  
  void addWindow(WindowState paramWindowState)
  {
    int i = this.allAppWindows.size() - 1;
    if (i >= 0)
    {
      WindowState localWindowState = (WindowState)this.allAppWindows.get(i);
      if ((localWindowState.mWillReplaceWindow) && (localWindowState.mReplacingWindow == null) && (localWindowState.getWindowTag().toString().equals(paramWindowState.getWindowTag().toString())))
      {
        localWindowState.mReplacingWindow = paramWindowState;
        if (!localWindowState.mAnimateReplacingWindow) {
          break label104;
        }
      }
      label104:
      for (boolean bool = false;; bool = true)
      {
        paramWindowState.mSkipEnterAnimationForSeamlessReplacement = bool;
        this.service.scheduleReplacingWindowTimeouts(this);
        i -= 1;
        break;
      }
    }
    this.allAppWindows.add(paramWindowState);
  }
  
  boolean canRestoreSurfaces()
  {
    int i = this.allAppWindows.size() - 1;
    while (i >= 0)
    {
      if (((WindowState)this.allAppWindows.get(i)).canRestoreSurface()) {
        return true;
      }
      i -= 1;
    }
    return false;
  }
  
  void clearAllDrawn()
  {
    this.allDrawn = false;
    this.deferClearAllDrawn = false;
    this.allDrawnExcludingSaved = false;
  }
  
  void clearAnimatingFlags()
  {
    int i = 0;
    int k = this.allAppWindows.size() - 1;
    if (k >= 0)
    {
      WindowState localWindowState = (WindowState)this.allAppWindows.get(k);
      int j = i;
      if (!localWindowState.mWillReplaceWindow)
      {
        if (!localWindowState.mRemoveOnExit) {
          break label58;
        }
        j = i;
      }
      for (;;)
      {
        k -= 1;
        i = j;
        break;
        label58:
        if (localWindowState.mAnimatingExit)
        {
          localWindowState.mAnimatingExit = false;
          i = 1;
        }
        if (localWindowState.mWinAnimator.mAnimating)
        {
          localWindowState.mWinAnimator.mAnimating = false;
          i = 1;
        }
        j = i;
        if (localWindowState.mDestroying)
        {
          localWindowState.mDestroying = false;
          this.service.mDestroySurface.remove(localWindowState);
          j = 1;
        }
      }
    }
    if (i != 0) {
      requestUpdateWallpaperIfNeeded();
    }
  }
  
  void clearRelaunching()
  {
    if (this.mPendingRelaunchCount == 0) {
      return;
    }
    if (canFreezeBounds()) {
      unfreezeBounds();
    }
    this.mPendingRelaunchCount = 0;
  }
  
  void clearTimedoutReplacesLocked()
  {
    int i = this.allAppWindows.size() - 1;
    if (i >= 0)
    {
      WindowState localWindowState = (WindowState)this.allAppWindows.get(i);
      if (!localWindowState.mWillReplaceWindow) {}
      for (;;)
      {
        i = Math.min(i - 1, this.allAppWindows.size() - 1);
        break;
        localWindowState.mWillReplaceWindow = false;
        if (localWindowState.mReplacingWindow != null) {
          localWindowState.mReplacingWindow.mSkipEnterAnimationForSeamlessReplacement = false;
        }
        this.service.removeWindowInnerLocked(localWindowState);
      }
    }
  }
  
  void clearVisibleBeforeClientHidden()
  {
    int i = this.allAppWindows.size() - 1;
    while (i >= 0)
    {
      ((WindowState)this.allAppWindows.get(i)).clearVisibleBeforeClientHidden();
      i -= 1;
    }
  }
  
  void destroySavedSurfaces()
  {
    int i = this.allAppWindows.size() - 1;
    while (i >= 0)
    {
      ((WindowState)this.allAppWindows.get(i)).destroySavedSurface();
      i -= 1;
    }
  }
  
  void destroySurfaces()
  {
    destroySurfaces(false);
  }
  
  void dump(PrintWriter paramPrintWriter, String paramString)
  {
    super.dump(paramPrintWriter, paramString);
    if (this.appToken != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("app=true voiceInteraction=");
      paramPrintWriter.println(this.voiceInteraction);
    }
    if (this.allAppWindows.size() > 0)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("allAppWindows=");
      paramPrintWriter.println(this.allAppWindows);
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("task=");
    paramPrintWriter.println(this.mTask);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print(" appFullscreen=");
    paramPrintWriter.print(this.appFullscreen);
    paramPrintWriter.print(" requestedOrientation=");
    paramPrintWriter.println(this.requestedOrientation);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("hiddenRequested=");
    paramPrintWriter.print(this.hiddenRequested);
    paramPrintWriter.print(" clientHidden=");
    paramPrintWriter.print(this.clientHidden);
    paramPrintWriter.print(" reportedDrawn=");
    paramPrintWriter.print(this.reportedDrawn);
    paramPrintWriter.print(" reportedVisible=");
    paramPrintWriter.println(this.reportedVisible);
    if (this.paused)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("paused=");
      paramPrintWriter.println(this.paused);
    }
    if (this.mAppStopped)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mAppStopped=");
      paramPrintWriter.println(this.mAppStopped);
    }
    if ((this.numInterestingWindows != 0) || (this.numDrawnWindows != 0))
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("numInterestingWindows=");
      paramPrintWriter.print(this.numInterestingWindows);
      paramPrintWriter.print(" numDrawnWindows=");
      paramPrintWriter.print(this.numDrawnWindows);
      paramPrintWriter.print(" inPendingTransaction=");
      paramPrintWriter.print(this.inPendingTransaction);
      paramPrintWriter.print(" allDrawn=");
      paramPrintWriter.print(this.allDrawn);
      paramPrintWriter.print(" (animator=");
      paramPrintWriter.print(this.mAppAnimator.allDrawn);
      paramPrintWriter.println(")");
      label341:
      if (this.inPendingTransaction)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("inPendingTransaction=");
        paramPrintWriter.println(this.inPendingTransaction);
      }
      if ((this.startingData != null) || (this.removed) || (this.firstWindowDrawn) || (this.mIsExiting))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("startingData=");
        paramPrintWriter.print(this.startingData);
        paramPrintWriter.print(" removed=");
        paramPrintWriter.print(this.removed);
        paramPrintWriter.print(" firstWindowDrawn=");
        paramPrintWriter.print(this.firstWindowDrawn);
        paramPrintWriter.print(" mIsExiting=");
        paramPrintWriter.println(this.mIsExiting);
      }
      if ((this.startingWindow == null) && (this.startingView == null)) {
        break label638;
      }
    }
    for (;;)
    {
      label475:
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("startingWindow=");
      paramPrintWriter.print(this.startingWindow);
      paramPrintWriter.print(" startingView=");
      paramPrintWriter.print(this.startingView);
      paramPrintWriter.print(" startingDisplayed=");
      paramPrintWriter.print(this.startingDisplayed);
      paramPrintWriter.print(" startingMoved=");
      paramPrintWriter.println(this.startingMoved);
      label638:
      do
      {
        if (!this.mFrozenBounds.isEmpty())
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("mFrozenBounds=");
          paramPrintWriter.println(this.mFrozenBounds);
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("mFrozenMergedConfig=");
          paramPrintWriter.println(this.mFrozenMergedConfig);
        }
        if (this.mPendingRelaunchCount != 0)
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("mPendingRelaunchCount=");
          paramPrintWriter.println(this.mPendingRelaunchCount);
        }
        return;
        if (this.allDrawn) {
          break;
        }
        if (!this.mAppAnimator.allDrawn) {
          break label341;
        }
        break;
        if (this.startingDisplayed) {
          break label475;
        }
      } while (!this.startingMoved);
    }
  }
  
  WindowState findMainWindow()
  {
    Object localObject = null;
    int i = this.windows.size();
    while (i > 0)
    {
      int j = i - 1;
      WindowState localWindowState = (WindowState)this.windows.get(j);
      if (localWindowState.mAttrs.type != 1)
      {
        i = j;
        if (localWindowState.mAttrs.type != 3) {
          break;
        }
      }
      else if (localWindowState.mAnimatingExit)
      {
        localObject = localWindowState;
        i = j;
      }
      else
      {
        return localWindowState;
      }
    }
    return (WindowState)localObject;
  }
  
  void finishRelaunching()
  {
    if (canFreezeBounds()) {
      unfreezeBounds();
    }
    if (this.mPendingRelaunchCount > 0) {
      this.mPendingRelaunchCount -= 1;
    }
  }
  
  boolean hasWindowsAlive()
  {
    int i = this.allAppWindows.size() - 1;
    while (i >= 0)
    {
      if (!((WindowState)this.allAppWindows.get(i)).mAppDied) {
        return true;
      }
      i -= 1;
    }
    return false;
  }
  
  boolean isAnimatingInvisibleWithSavedSurface()
  {
    int i = this.allAppWindows.size() - 1;
    while (i >= 0)
    {
      if (((WindowState)this.allAppWindows.get(i)).isAnimatingInvisibleWithSavedSurface()) {
        return true;
      }
      i -= 1;
    }
    return false;
  }
  
  boolean isRelaunching()
  {
    boolean bool = false;
    if (this.mPendingRelaunchCount > 0) {
      bool = true;
    }
    return bool;
  }
  
  boolean isVisible()
  {
    int j = this.allAppWindows.size();
    int i = 0;
    if (i < j)
    {
      WindowState localWindowState = (WindowState)this.allAppWindows.get(i);
      if ((localWindowState.mAppFreezing) || ((localWindowState.mViewVisibility != 0) && (!localWindowState.isAnimatingWithSavedSurface()) && ((!localWindowState.mWinAnimator.isAnimationSet()) || (this.service.mAppTransition.isTransitionSet())))) {}
      while ((localWindowState.mDestroying) || (!localWindowState.isDrawnLw()))
      {
        i += 1;
        break;
      }
      return true;
    }
    return false;
  }
  
  void markSavedSurfaceExiting()
  {
    int i = this.allAppWindows.size() - 1;
    while (i >= 0)
    {
      WindowState localWindowState = (WindowState)this.allAppWindows.get(i);
      if (localWindowState.isAnimatingInvisibleWithSavedSurface())
      {
        localWindowState.mAnimatingExit = true;
        localWindowState.mWinAnimator.mAnimating = true;
      }
      i -= 1;
    }
  }
  
  void notifyAppResumed(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (WindowManagerDebugConfig.DEBUG_ADD_REMOVE) {
      Slog.v(TAG, "notifyAppResumed: wasStopped=" + paramBoolean1 + " allowSavedSurface=" + paramBoolean2 + " " + this);
    }
    this.mAppStopped = false;
    if (!paramBoolean1) {
      destroySurfaces(true);
    }
    if (!paramBoolean2) {
      destroySavedSurfaces();
    }
  }
  
  void notifyAppStopped()
  {
    if (WindowManagerDebugConfig.DEBUG_ADD_REMOVE) {
      Slog.v(TAG, "notifyAppStopped: " + this);
    }
    this.mAppStopped = true;
    destroySurfaces();
    this.mTask.mService.scheduleRemoveStartingWindowLocked(this);
  }
  
  void onFirstWindowDrawn(WindowState paramWindowState, WindowStateAnimator paramWindowStateAnimator)
  {
    this.firstWindowDrawn = true;
    removeAllDeadWindows();
    if (this.startingData != null)
    {
      if ((WindowManagerDebugConfig.DEBUG_STARTING_WINDOW) || (WindowManagerDebugConfig.DEBUG_ANIM)) {
        Slog.v(TAG, "Finish starting " + paramWindowState.mToken + ": first real window is shown, no animation");
      }
      paramWindowStateAnimator.clearAnimation();
      paramWindowStateAnimator.mService.mFinishedStarting.add(this);
      paramWindowStateAnimator.mService.mH.sendEmptyMessage(7);
    }
    updateReportedVisibilityLocked();
  }
  
  void overridePlayingAppAnimations(Animation paramAnimation)
  {
    if (this.mAppAnimator.isAnimating())
    {
      Object localObject = this.mAppAnimator.animation;
      AppWindowAnimator localAppWindowAnimator = this.mAppAnimator;
      if (localObject != AppWindowAnimator.sDummyAnimation)
      {
        localObject = findMainWindow();
        if (localObject == null) {
          return;
        }
        int i = ((WindowState)localObject).mContainingFrame.width();
        int j = ((WindowState)localObject).mContainingFrame.height();
        this.mAppAnimator.setAnimation(paramAnimation, i, j, false, 2);
      }
    }
  }
  
  void removeAllDeadWindows()
  {
    for (int i = this.allAppWindows.size() - 1; i >= 0; i = Math.min(i - 1, this.allAppWindows.size() - 1))
    {
      WindowState localWindowState = (WindowState)this.allAppWindows.get(i);
      if (localWindowState.mAppDied)
      {
        if ((WindowManagerDebugConfig.DEBUG_WINDOW_MOVEMENT) || (WindowManagerDebugConfig.DEBUG_ADD_REMOVE)) {
          Slog.w(TAG, "removeAllDeadWindows: " + localWindowState);
        }
        localWindowState.mDestroying = true;
        this.service.removeWindowLocked(localWindowState);
      }
    }
  }
  
  void removeAllWindows()
  {
    for (int i = this.allAppWindows.size() - 1; i >= 0; i = Math.min(i - 1, this.allAppWindows.size() - 1))
    {
      WindowState localWindowState = (WindowState)this.allAppWindows.get(i);
      if (WindowManagerDebugConfig.DEBUG_WINDOW_MOVEMENT) {
        Slog.w(TAG, "removeAllWindows: removing win=" + localWindowState);
      }
      this.service.removeWindowLocked(localWindowState);
    }
    this.allAppWindows.clear();
    this.windows.clear();
  }
  
  void removeAppFromTaskLocked()
  {
    this.mIsExiting = false;
    removeAllWindows();
    Task localTask = this.mTask;
    if (localTask != null)
    {
      if (!localTask.removeAppToken(this)) {
        Slog.e(TAG, "removeAppFromTaskLocked: token=" + this + " not found.");
      }
      localTask.mStack.mExitingAppTokens.remove(this);
    }
  }
  
  void removeSurfaceViewBackground(WindowSurfaceController.SurfaceControlWithBackground paramSurfaceControlWithBackground)
  {
    this.mSurfaceViewBackgrounds.remove(paramSurfaceControlWithBackground);
    updateSurfaceViewBackgroundVisibilities();
  }
  
  void requestUpdateWallpaperIfNeeded()
  {
    int i = this.allAppWindows.size() - 1;
    while (i >= 0)
    {
      ((WindowState)this.allAppWindows.get(i)).requestUpdateWallpaperIfNeeded();
      i -= 1;
    }
  }
  
  void resetReplacingWindows()
  {
    if (WindowManagerDebugConfig.DEBUG_ADD_REMOVE) {
      Slog.d("WindowManager", "Resetting app token " + this.appWindowToken + " of replacing window marks.");
    }
    int i = this.allAppWindows.size() - 1;
    while (i >= 0)
    {
      ((WindowState)this.allAppWindows.get(i)).resetReplacing();
      i -= 1;
    }
  }
  
  void restoreSavedSurfaces()
  {
    boolean bool2 = false;
    if (!canRestoreSurfaces())
    {
      clearVisibleBeforeClientHidden();
      return;
    }
    int k = 0;
    int j = 0;
    int i = this.allAppWindows.size() - 1;
    if (i >= 0)
    {
      WindowState localWindowState = (WindowState)this.allAppWindows.get(i);
      int m = j;
      int n = k;
      if (localWindowState != this.startingWindow)
      {
        if (!localWindowState.mAppDied) {
          break label88;
        }
        n = k;
        m = j;
      }
      for (;;)
      {
        i -= 1;
        j = m;
        k = n;
        break;
        label88:
        m = j;
        n = k;
        if (localWindowState.wasVisibleBeforeClientHidden()) {
          if (this.mAppAnimator.freezingScreen)
          {
            m = j;
            n = k;
            if (localWindowState.mAppFreezing) {}
          }
          else
          {
            k += 1;
            if (localWindowState.hasSavedSurface()) {
              localWindowState.restoreSavedSurface();
            }
            m = j;
            n = k;
            if (localWindowState.isDrawnLw())
            {
              m = j + 1;
              n = k;
            }
          }
        }
      }
    }
    if (!this.allDrawn)
    {
      boolean bool1 = bool2;
      if (k > 0)
      {
        bool1 = bool2;
        if (k == j) {
          bool1 = true;
        }
      }
      this.allDrawn = bool1;
      if (this.allDrawn) {
        this.service.mH.obtainMessage(32, this.token).sendToTarget();
      }
    }
    clearVisibleBeforeClientHidden();
    if ((WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS) || (WindowManagerDebugConfig.DEBUG_ANIM)) {
      Slog.d(TAG, "restoreSavedSurfaces: " + this.appWindowToken + " allDrawn=" + this.allDrawn + " numInteresting=" + k + " numDrawn=" + j);
    }
  }
  
  void sendAppVisibilityToClients()
  {
    int j = this.allAppWindows.size();
    int i = 0;
    while (i < j)
    {
      Object localObject = (WindowState)this.allAppWindows.get(i);
      if ((localObject == this.startingWindow) && (this.clientHidden))
      {
        i += 1;
      }
      else
      {
        OnePlusProcessManager.resumeProcessByUID_out(((WindowState)localObject).mOwnerUid, "sendAppVisibilityToClients");
        for (;;)
        {
          try
          {
            if (WindowManagerDebugConfig.DEBUG_VISIBILITY)
            {
              String str = TAG;
              StringBuilder localStringBuilder = new StringBuilder().append("Setting visibility of ").append(localObject).append(": ");
              if (!this.clientHidden) {
                break label155;
              }
              bool = false;
              Slog.v(str, bool);
            }
            localObject = ((WindowState)localObject).mClient;
            if (!this.clientHidden) {
              break label160;
            }
            bool = false;
            ((IWindow)localObject).dispatchAppVisibility(bool);
          }
          catch (RemoteException localRemoteException) {}
          break;
          label155:
          boolean bool = true;
          continue;
          label160:
          bool = true;
        }
      }
    }
  }
  
  void setReplacingChildren()
  {
    if (WindowManagerDebugConfig.DEBUG_ADD_REMOVE) {
      Slog.d("WindowManager", "Marking app token " + this.appWindowToken + " with replacing child windows.");
    }
    int i = this.allAppWindows.size() - 1;
    while (i >= 0)
    {
      WindowState localWindowState = (WindowState)this.allAppWindows.get(i);
      if (localWindowState.shouldBeReplacedWithChildren()) {
        localWindowState.setReplacing(false);
      }
      i -= 1;
    }
  }
  
  void setReplacingWindows(boolean paramBoolean)
  {
    if (WindowManagerDebugConfig.DEBUG_ADD_REMOVE) {
      Slog.d("WindowManager", "Marking app token " + this.appWindowToken + " with replacing windows.");
    }
    int i = this.allAppWindows.size() - 1;
    while (i >= 0)
    {
      ((WindowState)this.allAppWindows.get(i)).setReplacing(paramBoolean);
      i -= 1;
    }
    if (paramBoolean)
    {
      if (WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS) {
        Slog.v("WindowManager", "setReplacingWindow() Setting dummy animation on: " + this);
      }
      this.mAppAnimator.setDummyAnimation();
    }
  }
  
  void setVisibleBeforeClientHidden()
  {
    int i = this.allAppWindows.size() - 1;
    while (i >= 0)
    {
      ((WindowState)this.allAppWindows.get(i)).setVisibleBeforeClientHidden();
      i -= 1;
    }
  }
  
  boolean shouldSaveSurface()
  {
    return this.allDrawn;
  }
  
  void startRelaunching()
  {
    if (canFreezeBounds()) {
      freezeBounds();
    }
    this.mPendingRelaunchCount += 1;
  }
  
  void stopUsingSavedSurfaceLocked()
  {
    int i = this.allAppWindows.size() - 1;
    while (i >= 0)
    {
      WindowState localWindowState = (WindowState)this.allAppWindows.get(i);
      if (localWindowState.isAnimatingInvisibleWithSavedSurface())
      {
        if ((WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS) || (WindowManagerDebugConfig.DEBUG_ANIM)) {
          Slog.d(TAG, "stopUsingSavedSurfaceLocked: " + localWindowState);
        }
        localWindowState.clearAnimatingWithSavedSurface();
        localWindowState.mDestroying = true;
        localWindowState.mWinAnimator.hide("stopUsingSavedSurfaceLocked");
        localWindowState.mWinAnimator.mWallpaperControllerLocked.hideWallpapers(localWindowState);
      }
      i -= 1;
    }
    destroySurfaces();
  }
  
  public String toString()
  {
    if (this.stringName == null)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("AppWindowToken{");
      localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
      localStringBuilder.append(" token=");
      localStringBuilder.append(this.token);
      localStringBuilder.append('}');
      this.stringName = localStringBuilder.toString();
    }
    return this.stringName;
  }
  
  void updateReportedVisibilityLocked()
  {
    int i5 = 0;
    if (this.appToken == null) {
      return;
    }
    int i1 = 0;
    int i = 0;
    int n = 0;
    int k = 1;
    if (WindowManagerDebugConfig.DEBUG_VISIBILITY) {
      Slog.v(TAG, "Update reported visibility: " + this);
    }
    int i6 = this.allAppWindows.size();
    int m = 0;
    Object localObject;
    int j;
    label173:
    boolean bool1;
    if (m < i6)
    {
      localObject = (WindowState)this.allAppWindows.get(m);
      j = k;
      int i2 = n;
      int i3 = i1;
      int i4 = i;
      if (localObject != this.startingWindow)
      {
        j = k;
        i2 = n;
        i3 = i1;
        i4 = i;
        if (!((WindowState)localObject).mAppFreezing)
        {
          if (((WindowState)localObject).mViewVisibility == 0) {
            break label173;
          }
          i4 = i;
          i3 = i1;
          i2 = n;
          j = k;
        }
      }
      for (;;)
      {
        m += 1;
        k = j;
        n = i2;
        i1 = i3;
        i = i4;
        break;
        j = k;
        i2 = n;
        i3 = i1;
        i4 = i;
        if (((WindowState)localObject).mAttrs.type != 3)
        {
          j = k;
          i2 = n;
          i3 = i1;
          i4 = i;
          if (!((WindowState)localObject).mDestroying)
          {
            String str;
            StringBuilder localStringBuilder;
            if (WindowManagerDebugConfig.DEBUG_VISIBILITY)
            {
              Slog.v(TAG, "Win " + localObject + ": isDrawn=" + ((WindowState)localObject).isDrawnLw() + ", isAnimationSet=" + ((WindowState)localObject).mWinAnimator.isAnimationSet());
              if (!((WindowState)localObject).isDrawnLw())
              {
                str = TAG;
                localStringBuilder = new StringBuilder().append("Not displayed: s=").append(((WindowState)localObject).mWinAnimator.mSurfaceController).append(" pv=").append(((WindowState)localObject).mPolicyVisibility).append(" mDrawState=").append(((WindowState)localObject).mWinAnimator.mDrawState).append(" ah=").append(((WindowState)localObject).mAttachedHidden).append(" th=");
                if (((WindowState)localObject).mAppToken == null) {
                  break label474;
                }
              }
            }
            label474:
            for (bool1 = ((WindowState)localObject).mAppToken.hiddenRequested;; bool1 = false)
            {
              Slog.v(str, bool1 + " a=" + ((WindowState)localObject).mWinAnimator.mAnimating);
              i1 += 1;
              if (!((WindowState)localObject).isDrawnLw()) {
                break label480;
              }
              i2 = n + 1;
              k = i;
              if (!((WindowState)localObject).mWinAnimator.isAnimationSet()) {
                k = i + 1;
              }
              j = 0;
              i3 = i1;
              i4 = k;
              break;
            }
            label480:
            j = k;
            i2 = n;
            i3 = i1;
            i4 = i;
            if (((WindowState)localObject).mWinAnimator.isAnimationSet())
            {
              j = 0;
              i2 = n;
              i3 = i1;
              i4 = i;
            }
          }
        }
      }
    }
    boolean bool2;
    if ((i1 > 0) && (n >= i1))
    {
      bool1 = true;
      if ((i1 <= 0) || (i < i1)) {
        break label817;
      }
      bool2 = true;
      label549:
      boolean bool4 = bool1;
      boolean bool5 = bool2;
      if (k == 0)
      {
        boolean bool3 = bool1;
        if (!bool1) {
          bool3 = this.reportedDrawn;
        }
        bool4 = bool3;
        bool5 = bool2;
        if (!bool2)
        {
          bool5 = this.reportedVisible;
          bool4 = bool3;
        }
      }
      if (WindowManagerDebugConfig.DEBUG_VISIBILITY) {
        Slog.v(TAG, "VIS " + this + ": interesting=" + i1 + " visible=" + i);
      }
      if (bool4 != this.reportedDrawn)
      {
        if (bool4)
        {
          localObject = this.service.mH.obtainMessage(9, this);
          this.service.mH.sendMessage((Message)localObject);
        }
        this.reportedDrawn = bool4;
      }
      if (bool5 != this.reportedVisible)
      {
        if (WindowManagerDebugConfig.DEBUG_VISIBILITY) {
          Slog.v(TAG, "Visibility changed in " + this + ": vis=" + bool5);
        }
        this.reportedVisible = bool5;
        localObject = this.service.mH;
        if (!bool5) {
          break label823;
        }
      }
    }
    label817:
    label823:
    for (i = 1;; i = 0)
    {
      j = i5;
      if (k != 0) {
        j = 1;
      }
      localObject = ((WindowManagerService.H)localObject).obtainMessage(8, i, j, this);
      this.service.mH.sendMessage((Message)localObject);
      return;
      bool1 = false;
      break;
      bool2 = false;
      break label549;
    }
  }
  
  void updateSurfaceViewBackgroundVisibilities()
  {
    Object localObject1 = null;
    int j = Integer.MAX_VALUE;
    int i = 0;
    Object localObject2;
    while (i < this.mSurfaceViewBackgrounds.size())
    {
      WindowSurfaceController.SurfaceControlWithBackground localSurfaceControlWithBackground = (WindowSurfaceController.SurfaceControlWithBackground)this.mSurfaceViewBackgrounds.get(i);
      localObject2 = localObject1;
      int k = j;
      if (localSurfaceControlWithBackground.mVisible)
      {
        localObject2 = localObject1;
        k = j;
        if (localSurfaceControlWithBackground.mLayer < j)
        {
          k = localSurfaceControlWithBackground.mLayer;
          localObject2 = localSurfaceControlWithBackground;
        }
      }
      i += 1;
      localObject1 = localObject2;
      j = k;
    }
    i = 0;
    if (i < this.mSurfaceViewBackgrounds.size())
    {
      localObject2 = (WindowSurfaceController.SurfaceControlWithBackground)this.mSurfaceViewBackgrounds.get(i);
      if (localObject2 != localObject1) {}
      for (boolean bool = true;; bool = false)
      {
        ((WindowSurfaceController.SurfaceControlWithBackground)localObject2).updateBackgroundVisibility(bool);
        i += 1;
        break;
      }
    }
  }
  
  boolean waitingForReplacement()
  {
    int i = this.allAppWindows.size() - 1;
    while (i >= 0)
    {
      if (((WindowState)this.allAppWindows.get(i)).mWillReplaceWindow) {
        return true;
      }
      i -= 1;
    }
    return false;
  }
  
  boolean windowsAreFocusable()
  {
    if (!ActivityManager.StackId.canReceiveKeys(this.mTask.mStack.mStackId)) {
      return this.mAlwaysFocusable;
    }
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/AppWindowToken.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */