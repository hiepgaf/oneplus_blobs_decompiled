package com.android.server.wm;

import android.util.Slog;
import android.view.WindowManager.LayoutParams;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class WindowLayersController
{
  private WindowState mDockDivider = null;
  private ArrayDeque<WindowState> mDockedWindows = new ArrayDeque();
  private int mHighestApplicationLayer = 0;
  private int mInputMethodAnimLayerAdjustment;
  private ArrayDeque<WindowState> mInputMethodWindows = new ArrayDeque();
  private ArrayDeque<WindowState> mPinnedWindows = new ArrayDeque();
  private ArrayDeque<WindowState> mReplacingWindows = new ArrayDeque();
  private final WindowManagerService mService;
  
  public WindowLayersController(WindowManagerService paramWindowManagerService)
  {
    this.mService = paramWindowManagerService;
  }
  
  private void adjustSpecialWindows()
  {
    for (int i = this.mHighestApplicationLayer + 5; !this.mDockedWindows.isEmpty(); i = assignAndIncreaseLayerIfNeeded((WindowState)this.mDockedWindows.remove(), i)) {}
    int j = assignAndIncreaseLayerIfNeeded(this.mDockDivider, i);
    i = j;
    if (this.mDockDivider != null)
    {
      i = j;
      if (this.mDockDivider.isVisibleLw()) {
        for (;;)
        {
          i = j;
          if (this.mInputMethodWindows.isEmpty()) {
            break;
          }
          WindowState localWindowState = (WindowState)this.mInputMethodWindows.remove();
          if (j > localWindowState.mLayer) {
            j = assignAndIncreaseLayerIfNeeded(localWindowState, j);
          }
        }
      }
    }
    for (;;)
    {
      j = i;
      if (this.mReplacingWindows.isEmpty()) {
        break;
      }
      i = assignAndIncreaseLayerIfNeeded((WindowState)this.mReplacingWindows.remove(), i);
    }
    while (!this.mPinnedWindows.isEmpty()) {
      j = assignAndIncreaseLayerIfNeeded((WindowState)this.mPinnedWindows.remove(), j);
    }
  }
  
  private int assignAndIncreaseLayerIfNeeded(WindowState paramWindowState, int paramInt)
  {
    int i = paramInt;
    if (paramWindowState != null)
    {
      assignAnimLayer(paramWindowState, paramInt);
      i = paramInt + 5;
    }
    return i;
  }
  
  private void assignAnimLayer(WindowState paramWindowState, int paramInt)
  {
    paramWindowState.mLayer = paramInt;
    paramWindowState.mWinAnimator.mAnimLayer = (paramWindowState.mLayer + paramWindowState.getAnimLayerAdjustment() + getSpecialWindowAnimLayerAdjustment(paramWindowState));
    if ((paramWindowState.mAppToken != null) && (paramWindowState.mAppToken.mAppAnimator.thumbnailForceAboveLayer > 0) && (paramWindowState.mWinAnimator.mAnimLayer > paramWindowState.mAppToken.mAppAnimator.thumbnailForceAboveLayer)) {
      paramWindowState.mAppToken.mAppAnimator.thumbnailForceAboveLayer = paramWindowState.mWinAnimator.mAnimLayer;
    }
  }
  
  private void clear()
  {
    this.mHighestApplicationLayer = 0;
    this.mPinnedWindows.clear();
    this.mInputMethodWindows.clear();
    this.mDockedWindows.clear();
    this.mReplacingWindows.clear();
    this.mDockDivider = null;
  }
  
  private void collectSpecialWindows(WindowState paramWindowState)
  {
    if (paramWindowState.mAttrs.type == 2034)
    {
      this.mDockDivider = paramWindowState;
      return;
    }
    if (paramWindowState.mWillReplaceWindow) {
      this.mReplacingWindows.add(paramWindowState);
    }
    if (paramWindowState.mIsImWindow)
    {
      this.mInputMethodWindows.add(paramWindowState);
      return;
    }
    TaskStack localTaskStack = paramWindowState.getStack();
    if (localTaskStack == null) {
      return;
    }
    if (localTaskStack.mStackId == 4) {
      this.mPinnedWindows.add(paramWindowState);
    }
    while (localTaskStack.mStackId != 3) {
      return;
    }
    this.mDockedWindows.add(paramWindowState);
  }
  
  private void logDebugLayers(WindowList paramWindowList)
  {
    int i = 0;
    int j = paramWindowList.size();
    if (i < j)
    {
      Object localObject = (WindowState)paramWindowList.get(i);
      WindowStateAnimator localWindowStateAnimator = ((WindowState)localObject).mWinAnimator;
      StringBuilder localStringBuilder = new StringBuilder().append("Assign layer ").append(localObject).append(": ").append("mBase=").append(((WindowState)localObject).mBaseLayer).append(" mLayer=").append(((WindowState)localObject).mLayer);
      if (((WindowState)localObject).mAppToken == null) {}
      for (localObject = "";; localObject = " mAppLayer=" + ((WindowState)localObject).mAppToken.mAppAnimator.animLayerAdjustment)
      {
        Slog.v("WindowManager", (String)localObject + " =mAnimLayer=" + localWindowStateAnimator.mAnimLayer);
        i += 1;
        break;
      }
    }
  }
  
  final void assignLayersLocked(WindowList paramWindowList)
  {
    if (WindowManagerDebugConfig.DEBUG_LAYERS) {
      Slog.v("WindowManager", "Assigning layers based on windows=" + paramWindowList, new RuntimeException("here").fillInStackTrace());
    }
    clear();
    int m = 0;
    int i = 0;
    int k = 0;
    int j = 0;
    int i1 = paramWindowList.size();
    if (j < i1)
    {
      WindowState localWindowState = (WindowState)paramWindowList.get(j);
      int n = 0;
      int i2 = localWindowState.mLayer;
      if ((localWindowState.mBaseLayer == m) || (localWindowState.mIsImWindow) || ((j > 0) && (localWindowState.mIsWallpaper))) {
        i += 5;
      }
      for (;;)
      {
        assignAnimLayer(localWindowState, i);
        if ((localWindowState.mLayer != i2) || (localWindowState.mWinAnimator.mAnimLayer != i2))
        {
          n = 1;
          k = 1;
        }
        if (localWindowState.mAppToken != null) {
          this.mHighestApplicationLayer = Math.max(this.mHighestApplicationLayer, localWindowState.mWinAnimator.mAnimLayer);
        }
        collectSpecialWindows(localWindowState);
        if (n != 0) {
          localWindowState.scheduleAnimationIfDimming();
        }
        j += 1;
        break;
        i = localWindowState.mBaseLayer;
        m = i;
      }
    }
    adjustSpecialWindows();
    if ((this.mService.mAccessibilityController != null) && (k != 0) && (((WindowState)paramWindowList.get(paramWindowList.size() - 1)).getDisplayId() == 0)) {
      this.mService.mAccessibilityController.onWindowLayersChangedLocked();
    }
    if (WindowManagerDebugConfig.DEBUG_LAYERS) {
      logDebugLayers(paramWindowList);
    }
  }
  
  void dump(PrintWriter paramPrintWriter, String paramString)
  {
    if ((this.mInputMethodAnimLayerAdjustment != 0) || (this.mService.mWallpaperControllerLocked.getAnimLayerAdjustment() != 0))
    {
      paramPrintWriter.print("  mInputMethodAnimLayerAdjustment=");
      paramPrintWriter.print(this.mInputMethodAnimLayerAdjustment);
      paramPrintWriter.print("  mWallpaperAnimLayerAdjustment=");
      paramPrintWriter.println(this.mService.mWallpaperControllerLocked.getAnimLayerAdjustment());
    }
  }
  
  int getResizeDimLayer()
  {
    if (this.mDockDivider != null) {
      return this.mDockDivider.mLayer - 1;
    }
    return 1;
  }
  
  int getSpecialWindowAnimLayerAdjustment(WindowState paramWindowState)
  {
    if (paramWindowState.mIsImWindow) {
      return this.mInputMethodAnimLayerAdjustment;
    }
    if (paramWindowState.mIsWallpaper) {
      return this.mService.mWallpaperControllerLocked.getAnimLayerAdjustment();
    }
    return 0;
  }
  
  void setInputMethodAnimLayerAdjustment(int paramInt)
  {
    if (WindowManagerDebugConfig.DEBUG_LAYERS) {
      Slog.v("WindowManager", "Setting im layer adj to " + paramInt);
    }
    this.mInputMethodAnimLayerAdjustment = paramInt;
    WindowState localWindowState1 = this.mService.mInputMethodWindow;
    WindowState localWindowState2;
    if (localWindowState1 != null)
    {
      localWindowState1.mWinAnimator.mAnimLayer = (localWindowState1.mLayer + paramInt);
      if (WindowManagerDebugConfig.DEBUG_LAYERS) {
        Slog.v("WindowManager", "IM win " + localWindowState1 + " anim layer: " + localWindowState1.mWinAnimator.mAnimLayer);
      }
      i = localWindowState1.mChildWindows.size() - 1;
      while (i >= 0)
      {
        localWindowState2 = (WindowState)localWindowState1.mChildWindows.get(i);
        localWindowState2.mWinAnimator.mAnimLayer = (localWindowState2.mLayer + paramInt);
        if (WindowManagerDebugConfig.DEBUG_LAYERS) {
          Slog.v("WindowManager", "IM win " + localWindowState2 + " anim layer: " + localWindowState2.mWinAnimator.mAnimLayer);
        }
        i -= 1;
      }
    }
    int i = this.mService.mInputMethodDialogs.size() - 1;
    while (i >= 0)
    {
      localWindowState2 = (WindowState)this.mService.mInputMethodDialogs.get(i);
      localWindowState2.mWinAnimator.mAnimLayer = (localWindowState2.mLayer + paramInt);
      if (WindowManagerDebugConfig.DEBUG_LAYERS) {
        Slog.v("WindowManager", "IM win " + localWindowState1 + " anim layer: " + localWindowState2.mWinAnimator.mAnimLayer);
      }
      i -= 1;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/WindowLayersController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */