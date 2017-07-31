package com.android.server.wm;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.ArrayMap;
import android.util.Slog;
import android.util.TypedValue;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import java.io.PrintWriter;

class DimLayerController
{
  private static final float DEFAULT_DIM_AMOUNT_DEAD_WINDOW = 0.5F;
  private static final int DEFAULT_DIM_DURATION = 200;
  private static final String TAG = "WindowManager";
  private static final String TAG_LOCAL = "DimLayerController";
  private DisplayContent mDisplayContent;
  private DimLayer mSharedFullScreenDimLayer;
  private ArrayMap<DimLayer.DimLayerUser, DimLayerState> mState = new ArrayMap();
  private Rect mTmpBounds = new Rect();
  
  DimLayerController(DisplayContent paramDisplayContent)
  {
    this.mDisplayContent = paramDisplayContent;
  }
  
  private boolean animateDimLayers(DimLayer.DimLayerUser paramDimLayerUser)
  {
    DimLayerState localDimLayerState = (DimLayerState)this.mState.get(paramDimLayerUser);
    if (WindowManagerDebugConfig.DEBUG_DIM_LAYER) {
      Slog.v(TAG, "animateDimLayers, dimLayerUser=" + paramDimLayerUser.toShortString() + " state.animator=" + localDimLayerState.animator + " state.continueDimming=" + localDimLayerState.continueDimming);
    }
    int i;
    float f1;
    float f2;
    if (localDimLayerState.animator == null)
    {
      i = localDimLayerState.dimLayer.getLayer();
      f1 = 0.0F;
      f2 = localDimLayerState.dimLayer.getTargetAlpha();
      if (f2 == f1) {
        break label326;
      }
      if (localDimLayerState.animator != null) {
        break label220;
      }
      localDimLayerState.dimLayer.hide(200L);
    }
    for (;;)
    {
      if (localDimLayerState.dimLayer.isAnimating())
      {
        if (this.mDisplayContent.mService.okToDisplay()) {
          break label352;
        }
        localDimLayerState.dimLayer.show();
      }
      return false;
      if (localDimLayerState.dimAbove)
      {
        i = localDimLayerState.animator.mAnimLayer + 1;
        f1 = 0.5F;
        break;
      }
      i = localDimLayerState.animator.mAnimLayer - 1;
      f1 = localDimLayerState.animator.mWin.mAttrs.dimAmount;
      break;
      label220:
      if ((localDimLayerState.animator.mAnimating) && (localDimLayerState.animator.mAnimation != null)) {}
      for (long l1 = localDimLayerState.animator.mAnimation.computeDurationHint();; l1 = 200L)
      {
        long l2 = l1;
        if (f2 > f1) {
          l2 = getDimLayerFadeDuration(l1);
        }
        localDimLayerState.dimLayer.show(i, f1, l2);
        if (f2 != 0.0F) {
          break;
        }
        paramDimLayerUser = this.mDisplayContent;
        paramDimLayerUser.pendingLayoutChanges |= 0x1;
        this.mDisplayContent.layoutNeeded = true;
        break;
      }
      label326:
      if (localDimLayerState.dimLayer.getLayer() != i) {
        localDimLayerState.dimLayer.setLayer(i);
      }
    }
    label352:
    return localDimLayerState.dimLayer.stepAnimation();
  }
  
  private boolean getContinueDimming(DimLayer.DimLayerUser paramDimLayerUser)
  {
    paramDimLayerUser = (DimLayerState)this.mState.get(paramDimLayerUser);
    if (paramDimLayerUser != null) {
      return paramDimLayerUser.continueDimming;
    }
    return false;
  }
  
  private long getDimLayerFadeDuration(long paramLong)
  {
    TypedValue localTypedValue = new TypedValue();
    this.mDisplayContent.mService.mContext.getResources().getValue(18022400, localTypedValue, true);
    long l;
    if (localTypedValue.type == 6) {
      l = localTypedValue.getFraction((float)paramLong, (float)paramLong);
    }
    do
    {
      do
      {
        return l;
        l = paramLong;
      } while (localTypedValue.type < 16);
      l = paramLong;
    } while (localTypedValue.type > 31);
    return localTypedValue.data;
  }
  
  private static String getDimLayerTag(DimLayer.DimLayerUser paramDimLayerUser)
  {
    return "DimLayerController/" + paramDimLayerUser.toShortString();
  }
  
  private DimLayerState getOrCreateDimLayerState(DimLayer.DimLayerUser paramDimLayerUser)
  {
    if (WindowManagerDebugConfig.DEBUG_DIM_LAYER) {
      Slog.v(TAG, "getOrCreateDimLayerState, dimLayerUser=" + paramDimLayerUser.toShortString());
    }
    DimLayerState localDimLayerState2 = (DimLayerState)this.mState.get(paramDimLayerUser);
    DimLayerState localDimLayerState1 = localDimLayerState2;
    if (localDimLayerState2 == null)
    {
      localDimLayerState1 = new DimLayerState(null);
      this.mState.put(paramDimLayerUser, localDimLayerState1);
    }
    return localDimLayerState1;
  }
  
  private void setContinueDimming(DimLayer.DimLayerUser paramDimLayerUser)
  {
    DimLayerState localDimLayerState = (DimLayerState)this.mState.get(paramDimLayerUser);
    if (localDimLayerState == null)
    {
      if (WindowManagerDebugConfig.DEBUG_DIM_LAYER) {
        Slog.w(TAG, "setContinueDimming, no state for: " + paramDimLayerUser.toShortString());
      }
      return;
    }
    localDimLayerState.continueDimming = true;
  }
  
  private void stopDimmingIfNeeded(DimLayer.DimLayerUser paramDimLayerUser)
  {
    DimLayerState localDimLayerState = (DimLayerState)this.mState.get(paramDimLayerUser);
    if (WindowManagerDebugConfig.DEBUG_DIM_LAYER) {
      Slog.v(TAG, "stopDimmingIfNeeded, dimLayerUser=" + paramDimLayerUser.toShortString() + " state.continueDimming=" + localDimLayerState.continueDimming + " state.dimLayer.isDimming=" + localDimLayerState.dimLayer.isDimming());
    }
    if ((localDimLayerState.animator != null) && (localDimLayerState.animator.mWin.mWillReplaceWindow)) {
      return;
    }
    if ((!localDimLayerState.continueDimming) && (localDimLayerState.dimLayer.isDimming()))
    {
      localDimLayerState.animator = null;
      paramDimLayerUser.getDimBounds(this.mTmpBounds);
      localDimLayerState.dimLayer.setBounds(this.mTmpBounds);
    }
  }
  
  boolean animateDimLayers()
  {
    int m = -1;
    int j = -1;
    boolean bool1 = false;
    int i = this.mState.size() - 1;
    boolean bool2;
    if (i >= 0)
    {
      DimLayer.DimLayerUser localDimLayerUser = (DimLayer.DimLayerUser)this.mState.keyAt(i);
      DimLayerState localDimLayerState = (DimLayerState)this.mState.valueAt(i);
      if ((localDimLayerUser.dimFullscreen()) && (localDimLayerState.dimLayer == this.mSharedFullScreenDimLayer))
      {
        int k = i;
        m = k;
        bool2 = bool1;
        if (((DimLayerState)this.mState.valueAt(i)).continueDimming)
        {
          j = i;
          bool2 = bool1;
          m = k;
        }
      }
      for (;;)
      {
        i -= 1;
        bool1 = bool2;
        break;
        bool2 = bool1 | animateDimLayers(localDimLayerUser);
      }
    }
    if (j != -1) {
      bool2 = bool1 | animateDimLayers((DimLayer.DimLayerUser)this.mState.keyAt(j));
    }
    do
    {
      return bool2;
      bool2 = bool1;
    } while (m == -1);
    return bool1 | animateDimLayers((DimLayer.DimLayerUser)this.mState.keyAt(m));
  }
  
  void applyDim(DimLayer.DimLayerUser paramDimLayerUser, WindowStateAnimator paramWindowStateAnimator, boolean paramBoolean)
  {
    if (paramDimLayerUser == null)
    {
      Slog.e(TAG, "Trying to apply dim layer for: " + this + ", but no dim layer user found.");
      return;
    }
    if (!getContinueDimming(paramDimLayerUser))
    {
      setContinueDimming(paramDimLayerUser);
      if (!isDimming(paramDimLayerUser, paramWindowStateAnimator))
      {
        if (WindowManagerDebugConfig.DEBUG_DIM_LAYER) {
          Slog.v(TAG, "Win " + this + " start dimming.");
        }
        startDimmingIfNeeded(paramDimLayerUser, paramWindowStateAnimator, paramBoolean);
      }
    }
  }
  
  void applyDimAbove(DimLayer.DimLayerUser paramDimLayerUser, WindowStateAnimator paramWindowStateAnimator)
  {
    applyDim(paramDimLayerUser, paramWindowStateAnimator, true);
  }
  
  void applyDimBehind(DimLayer.DimLayerUser paramDimLayerUser, WindowStateAnimator paramWindowStateAnimator)
  {
    applyDim(paramDimLayerUser, paramWindowStateAnimator, false);
  }
  
  void close()
  {
    int i = this.mState.size() - 1;
    while (i >= 0)
    {
      ((DimLayerState)this.mState.valueAt(i)).dimLayer.destroySurface();
      i -= 1;
    }
    this.mState.clear();
    this.mSharedFullScreenDimLayer = null;
  }
  
  void dump(String paramString, PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println(paramString + "DimLayerController");
    String str = paramString + "  ";
    int i = 0;
    int j = this.mState.size();
    if (i < j)
    {
      paramPrintWriter.println(str + ((DimLayer.DimLayerUser)this.mState.keyAt(i)).toShortString());
      DimLayerState localDimLayerState = (DimLayerState)this.mState.valueAt(i);
      StringBuilder localStringBuilder = new StringBuilder().append(str).append("  ").append("dimLayer=");
      if (localDimLayerState.dimLayer == this.mSharedFullScreenDimLayer) {}
      for (paramString = "shared";; paramString = localDimLayerState.dimLayer)
      {
        paramPrintWriter.println(paramString + ", animator=" + localDimLayerState.animator + ", continueDimming=" + localDimLayerState.continueDimming);
        if (localDimLayerState.dimLayer != null) {
          localDimLayerState.dimLayer.printTo(str + "  ", paramPrintWriter);
        }
        i += 1;
        break;
      }
    }
  }
  
  boolean isDimming()
  {
    int i = this.mState.size() - 1;
    while (i >= 0)
    {
      DimLayerState localDimLayerState = (DimLayerState)this.mState.valueAt(i);
      if ((localDimLayerState.dimLayer != null) && (localDimLayerState.dimLayer.isDimming())) {
        return true;
      }
      i -= 1;
    }
    return false;
  }
  
  boolean isDimming(DimLayer.DimLayerUser paramDimLayerUser, WindowStateAnimator paramWindowStateAnimator)
  {
    paramDimLayerUser = (DimLayerState)this.mState.get(paramDimLayerUser);
    if ((paramDimLayerUser != null) && (paramDimLayerUser.animator == paramWindowStateAnimator)) {
      return paramDimLayerUser.dimLayer.isDimming();
    }
    return false;
  }
  
  void removeDimLayerUser(DimLayer.DimLayerUser paramDimLayerUser)
  {
    DimLayerState localDimLayerState = (DimLayerState)this.mState.get(paramDimLayerUser);
    if (localDimLayerState != null)
    {
      if (localDimLayerState.dimLayer != this.mSharedFullScreenDimLayer) {
        localDimLayerState.dimLayer.destroySurface();
      }
      this.mState.remove(paramDimLayerUser);
    }
  }
  
  void resetDimming()
  {
    int i = this.mState.size() - 1;
    while (i >= 0)
    {
      ((DimLayerState)this.mState.valueAt(i)).continueDimming = false;
      i -= 1;
    }
  }
  
  void startDimmingIfNeeded(DimLayer.DimLayerUser paramDimLayerUser, WindowStateAnimator paramWindowStateAnimator, boolean paramBoolean)
  {
    DimLayerState localDimLayerState = getOrCreateDimLayerState(paramDimLayerUser);
    localDimLayerState.dimAbove = paramBoolean;
    if (WindowManagerDebugConfig.DEBUG_DIM_LAYER) {
      Slog.v(TAG, "startDimmingIfNeeded, dimLayerUser=" + paramDimLayerUser.toShortString() + " newWinAnimator=" + paramWindowStateAnimator + " state.animator=" + localDimLayerState.animator);
    }
    if ((paramWindowStateAnimator.getShown()) && ((localDimLayerState.animator == null) || (!localDimLayerState.animator.getShown()) || (localDimLayerState.animator.mAnimLayer <= paramWindowStateAnimator.mAnimLayer)))
    {
      localDimLayerState.animator = paramWindowStateAnimator;
      if ((localDimLayerState.animator.mWin.mAppToken == null) && (!paramDimLayerUser.dimFullscreen())) {
        break label167;
      }
      paramDimLayerUser.getDimBounds(this.mTmpBounds);
    }
    for (;;)
    {
      localDimLayerState.dimLayer.setBounds(this.mTmpBounds);
      return;
      label167:
      this.mDisplayContent.getLogicalDisplayRect(this.mTmpBounds);
    }
  }
  
  void stopDimmingIfNeeded()
  {
    if (WindowManagerDebugConfig.DEBUG_DIM_LAYER) {
      Slog.v(TAG, "stopDimmingIfNeeded, mState.size()=" + this.mState.size());
    }
    int i = this.mState.size() - 1;
    while (i >= 0)
    {
      stopDimmingIfNeeded((DimLayer.DimLayerUser)this.mState.keyAt(i));
      i -= 1;
    }
  }
  
  void updateDimLayer(DimLayer.DimLayerUser paramDimLayerUser)
  {
    DimLayerState localDimLayerState = getOrCreateDimLayerState(paramDimLayerUser);
    int i;
    if (localDimLayerState.dimLayer != null) {
      if (localDimLayerState.dimLayer == this.mSharedFullScreenDimLayer) {
        i = 1;
      }
    }
    int j;
    for (;;)
    {
      j = this.mDisplayContent.getDisplayId();
      if (!paramDimLayerUser.dimFullscreen()) {
        break label186;
      }
      if ((i == 0) || (this.mSharedFullScreenDimLayer == null)) {
        break;
      }
      this.mSharedFullScreenDimLayer.setBoundsForFullscreen();
      return;
      i = 0;
      continue;
      i = 0;
    }
    DimLayer localDimLayer2 = this.mSharedFullScreenDimLayer;
    if (localDimLayer2 == null) {
      if (localDimLayerState.dimLayer != null)
      {
        localDimLayer1 = localDimLayerState.dimLayer;
        paramDimLayerUser.getDimBounds(this.mTmpBounds);
        localDimLayer1.setBounds(this.mTmpBounds);
        this.mSharedFullScreenDimLayer = localDimLayer1;
      }
    }
    for (;;)
    {
      localDimLayerState.dimLayer = localDimLayer1;
      return;
      localDimLayer1 = new DimLayer(this.mDisplayContent.mService, paramDimLayerUser, j, getDimLayerTag(paramDimLayerUser));
      break;
      localDimLayer1 = localDimLayer2;
      if (localDimLayerState.dimLayer != null)
      {
        localDimLayerState.dimLayer.destroySurface();
        localDimLayer1 = localDimLayer2;
      }
    }
    label186:
    if ((localDimLayerState.dimLayer == null) || (i != 0)) {}
    for (DimLayer localDimLayer1 = new DimLayer(this.mDisplayContent.mService, paramDimLayerUser, j, getDimLayerTag(paramDimLayerUser));; localDimLayer1 = localDimLayerState.dimLayer)
    {
      paramDimLayerUser.getDimBounds(this.mTmpBounds);
      localDimLayer1.setBounds(this.mTmpBounds);
      break;
    }
  }
  
  private static class DimLayerState
  {
    WindowStateAnimator animator;
    boolean continueDimming;
    boolean dimAbove;
    DimLayer dimLayer;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/DimLayerController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */