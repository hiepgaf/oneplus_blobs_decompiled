package com.android.server.policy;

import android.app.StatusBarManager;
import android.os.Handler;
import android.os.SystemClock;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManagerPolicy.WindowState;
import com.android.server.LocalServices;
import com.android.server.statusbar.StatusBarManagerInternal;
import java.io.PrintWriter;

public class BarController
{
  private static final boolean DEBUG = false;
  private static final int TRANSIENT_BAR_HIDING = 3;
  private static final int TRANSIENT_BAR_NONE = 0;
  private static final int TRANSIENT_BAR_SHOWING = 2;
  private static final int TRANSIENT_BAR_SHOW_REQUESTED = 1;
  private static final int TRANSLUCENT_ANIMATION_DELAY_MS = 1000;
  protected final Handler mHandler;
  private long mLastTranslucent;
  private boolean mNoAnimationOnNextShow;
  private boolean mPendingShow;
  private final Object mServiceAquireLock = new Object();
  private boolean mSetUnHideFlagWhenNextTransparent;
  private boolean mShowTransparent;
  private int mState = 0;
  protected StatusBarManagerInternal mStatusBarInternal;
  private final int mStatusBarManagerId;
  protected final String mTag;
  private int mTransientBarState;
  private final int mTransientFlag;
  private final int mTranslucentFlag;
  private final int mTranslucentWmFlag;
  private final int mTransparentFlag;
  private final int mUnhideFlag;
  protected WindowManagerPolicy.WindowState mWin;
  
  public BarController(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    this.mTag = ("BarController." + paramString);
    this.mTransientFlag = paramInt1;
    this.mUnhideFlag = paramInt2;
    this.mTranslucentFlag = paramInt3;
    this.mStatusBarManagerId = paramInt4;
    this.mTranslucentWmFlag = paramInt5;
    this.mTransparentFlag = paramInt6;
    this.mHandler = new Handler();
  }
  
  private int computeStateLw(boolean paramBoolean1, boolean paramBoolean2, WindowManagerPolicy.WindowState paramWindowState, boolean paramBoolean3)
  {
    if (paramWindowState.isDrawnLw())
    {
      boolean bool1 = paramWindowState.isVisibleLw();
      boolean bool2 = paramWindowState.isAnimatingLw();
      if ((this.mState != 1) || (paramBoolean3)) {}
      while ((this.mState == 2) && (bool1))
      {
        return 0;
        if (!bool1) {
          return 2;
        }
      }
      if (paramBoolean3)
      {
        if ((!paramBoolean1) || (!bool1) || (paramBoolean2)) {}
        while (!bool2) {
          return 0;
        }
        return 1;
      }
    }
    return this.mState;
  }
  
  private void setTransientBarState(int paramInt)
  {
    if ((this.mWin != null) && (paramInt != this.mTransientBarState))
    {
      if ((this.mTransientBarState == 2) || (paramInt == 2)) {
        this.mLastTranslucent = SystemClock.uptimeMillis();
      }
      this.mTransientBarState = paramInt;
    }
  }
  
  private static String transientBarStateToString(int paramInt)
  {
    if (paramInt == 3) {
      return "TRANSIENT_BAR_HIDING";
    }
    if (paramInt == 2) {
      return "TRANSIENT_BAR_SHOWING";
    }
    if (paramInt == 1) {
      return "TRANSIENT_BAR_SHOW_REQUESTED";
    }
    if (paramInt == 0) {
      return "TRANSIENT_BAR_NONE";
    }
    throw new IllegalArgumentException("Unknown state " + paramInt);
  }
  
  private boolean updateStateLw(final int paramInt)
  {
    if (paramInt != this.mState)
    {
      this.mState = paramInt;
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          StatusBarManagerInternal localStatusBarManagerInternal = BarController.this.getStatusBarInternal();
          if (localStatusBarManagerInternal != null) {
            localStatusBarManagerInternal.setWindowState(BarController.-get0(BarController.this), paramInt);
          }
        }
      });
      return true;
    }
    return false;
  }
  
  public void adjustSystemUiVisibilityLw(int paramInt1, int paramInt2)
  {
    if ((this.mWin != null) && (this.mTransientBarState == 2) && ((this.mTransientFlag & paramInt2) == 0))
    {
      setTransientBarState(3);
      setBarShowingLw(false);
    }
    while ((this.mWin == null) || ((this.mUnhideFlag & paramInt1) == 0) || ((this.mUnhideFlag & paramInt2) != 0)) {
      return;
    }
    setBarShowingLw(true);
  }
  
  public int applyTranslucentFlagLw(WindowManagerPolicy.WindowState paramWindowState, int paramInt1, int paramInt2)
  {
    int i = paramInt1;
    if (this.mWin != null)
    {
      if ((paramWindowState == null) || ((paramWindowState.getAttrs().privateFlags & 0x200) != 0)) {
        break label87;
      }
      paramInt2 = PolicyControl.getWindowFlags(paramWindowState, null);
      if ((this.mTranslucentWmFlag & paramInt2) == 0) {
        break label70;
      }
      paramInt1 |= this.mTranslucentFlag;
    }
    while ((0x80000000 & paramInt2) != 0)
    {
      i = paramInt1 | this.mTransparentFlag;
      return i;
      label70:
      paramInt1 &= this.mTranslucentFlag;
    }
    return paramInt1 & this.mTransparentFlag;
    label87:
    i = this.mTranslucentFlag;
    int j = this.mTranslucentFlag;
    return this.mTransparentFlag & (i & paramInt1 | j & paramInt2) | this.mTransparentFlag & paramInt2;
  }
  
  public boolean checkHiddenLw()
  {
    if ((this.mWin != null) && (this.mWin.isDrawnLw())) {
      if ((!this.mWin.isVisibleLw()) && (!this.mWin.isAnimatingLw())) {
        break label65;
      }
    }
    while ((this.mTransientBarState != 3) || (this.mWin.isVisibleLw()))
    {
      return false;
      label65:
      updateStateLw(2);
    }
    setTransientBarState(0);
    if (this.mPendingShow)
    {
      setBarShowingLw(true);
      this.mPendingShow = false;
    }
    return true;
  }
  
  public boolean checkShowTransientBarLw()
  {
    if (this.mTransientBarState == 2) {
      return false;
    }
    if (this.mTransientBarState == 1) {
      return false;
    }
    if (this.mWin == null) {
      return false;
    }
    return !this.mWin.isDisplayedLw();
  }
  
  public void dump(PrintWriter paramPrintWriter, String paramString)
  {
    if (this.mWin != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.println(this.mTag);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("  ");
      paramPrintWriter.print("mState");
      paramPrintWriter.print('=');
      paramPrintWriter.println(StatusBarManager.windowStateToString(this.mState));
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("  ");
      paramPrintWriter.print("mTransientBar");
      paramPrintWriter.print('=');
      paramPrintWriter.println(transientBarStateToString(this.mTransientBarState));
    }
  }
  
  protected StatusBarManagerInternal getStatusBarInternal()
  {
    synchronized (this.mServiceAquireLock)
    {
      if (this.mStatusBarInternal == null) {
        this.mStatusBarInternal = ((StatusBarManagerInternal)LocalServices.getService(StatusBarManagerInternal.class));
      }
      StatusBarManagerInternal localStatusBarManagerInternal = this.mStatusBarInternal;
      return localStatusBarManagerInternal;
    }
  }
  
  public boolean isTransientShowRequested()
  {
    return this.mTransientBarState == 1;
  }
  
  public boolean isTransientShowing()
  {
    return this.mTransientBarState == 2;
  }
  
  public boolean setBarShowingLw(boolean paramBoolean)
  {
    boolean bool1 = true;
    if (this.mWin == null) {
      return false;
    }
    if ((paramBoolean) && (this.mTransientBarState == 3))
    {
      this.mPendingShow = true;
      return false;
    }
    boolean bool2 = this.mWin.isVisibleLw();
    boolean bool3 = this.mWin.isAnimatingLw();
    if (paramBoolean)
    {
      localWindowState = this.mWin;
      if ((this.mNoAnimationOnNextShow) || (skipAnimation())) {}
      for (paramBoolean = false;; paramBoolean = true)
      {
        paramBoolean = localWindowState.showLw(paramBoolean);
        this.mNoAnimationOnNextShow = false;
        bool2 = updateStateLw(computeStateLw(bool2, bool3, this.mWin, paramBoolean));
        if (!paramBoolean) {
          bool1 = bool2;
        }
        return bool1;
      }
    }
    WindowManagerPolicy.WindowState localWindowState = this.mWin;
    if ((this.mNoAnimationOnNextShow) || (skipAnimation())) {}
    for (paramBoolean = false;; paramBoolean = true)
    {
      paramBoolean = localWindowState.hideLw(paramBoolean);
      break;
    }
  }
  
  public void setShowTransparent(boolean paramBoolean)
  {
    if (paramBoolean != this.mShowTransparent)
    {
      this.mShowTransparent = paramBoolean;
      this.mSetUnHideFlagWhenNextTransparent = paramBoolean;
      this.mNoAnimationOnNextShow = true;
    }
  }
  
  public void setWindow(WindowManagerPolicy.WindowState paramWindowState)
  {
    this.mWin = paramWindowState;
  }
  
  public void showTransient()
  {
    if (this.mWin != null) {
      setTransientBarState(1);
    }
  }
  
  protected boolean skipAnimation()
  {
    return false;
  }
  
  public int updateVisibilityLw(boolean paramBoolean, int paramInt1, int paramInt2)
  {
    if (this.mWin == null) {
      return paramInt2;
    }
    if (!isTransientShowing())
    {
      i = paramInt2;
      if (!isTransientShowRequested()) {}
    }
    else
    {
      if (!paramBoolean) {
        break label154;
      }
      paramInt2 |= this.mTransientFlag;
      i = paramInt2;
      if ((this.mTransientFlag & paramInt1) == 0) {
        i = paramInt2 | this.mUnhideFlag;
      }
      setTransientBarState(2);
    }
    paramInt2 = i;
    if (this.mShowTransparent)
    {
      i |= this.mTransparentFlag;
      paramInt2 = i;
      if (this.mSetUnHideFlagWhenNextTransparent)
      {
        paramInt2 = i | this.mUnhideFlag;
        this.mSetUnHideFlagWhenNextTransparent = false;
      }
    }
    int i = paramInt2;
    if (this.mTransientBarState != 0) {
      i = (paramInt2 | this.mTransientFlag) & 0xFFFFFFFE;
    }
    if (((this.mTranslucentFlag & i) != 0) || ((this.mTranslucentFlag & paramInt1) != 0)) {}
    for (;;)
    {
      this.mLastTranslucent = SystemClock.uptimeMillis();
      label154:
      do
      {
        return i;
        setTransientBarState(0);
        i = paramInt2;
        break;
      } while (((i | paramInt1) & this.mTransparentFlag) == 0);
    }
  }
  
  public boolean wasRecentlyTranslucent()
  {
    return SystemClock.uptimeMillis() - this.mLastTranslucent < 1000L;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/policy/BarController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */