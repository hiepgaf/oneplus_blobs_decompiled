package com.android.server.display;

import android.content.Context;
import android.os.Handler;
import android.util.FloatProperty;
import android.util.IntProperty;
import android.util.Slog;
import android.view.Choreographer;
import android.view.Display;
import java.io.PrintWriter;

final class DisplayPowerState
{
  public static final FloatProperty<DisplayPowerState> COLOR_FADE_LEVEL = new FloatProperty("electronBeamLevel")
  {
    public Float get(DisplayPowerState paramAnonymousDisplayPowerState)
    {
      return Float.valueOf(paramAnonymousDisplayPowerState.getColorFadeLevel());
    }
    
    public void setValue(DisplayPowerState paramAnonymousDisplayPowerState, float paramAnonymousFloat)
    {
      paramAnonymousDisplayPowerState.setColorFadeLevel(paramAnonymousFloat);
    }
  };
  static boolean DEBUG = false;
  public static final IntProperty<DisplayPowerState> SCREEN_BRIGHTNESS = new IntProperty("screenBrightness")
  {
    public Integer get(DisplayPowerState paramAnonymousDisplayPowerState)
    {
      return Integer.valueOf(paramAnonymousDisplayPowerState.getScreenBrightness());
    }
    
    public void setValue(DisplayPowerState paramAnonymousDisplayPowerState, int paramAnonymousInt)
    {
      paramAnonymousDisplayPowerState.setScreenBrightness(paramAnonymousInt);
    }
  };
  private static final String TAG = "DisplayPowerState";
  private final DisplayBlanker mBlanker;
  private final Choreographer mChoreographer = Choreographer.getInstance();
  private Runnable mCleanListener;
  private final ColorFade mColorFade;
  private boolean mColorFadeDrawPending;
  private final Runnable mColorFadeDrawRunnable = new Runnable()
  {
    public void run()
    {
      DisplayPowerState.-set0(DisplayPowerState.this, false);
      if (DisplayPowerState.-get3(DisplayPowerState.this)) {
        DisplayPowerState.-get1(DisplayPowerState.this).draw(DisplayPowerState.-get2(DisplayPowerState.this));
      }
      DisplayPowerState.-set1(DisplayPowerState.this, true);
      DisplayPowerState.-wrap0(DisplayPowerState.this);
    }
  };
  private float mColorFadeLevel;
  private boolean mColorFadePrepared;
  private boolean mColorFadeReady;
  private final Handler mHandler = new Handler(true);
  private final PhotonicModulator mPhotonicModulator;
  private int mScreenBrightness;
  private boolean mScreenReady;
  private int mScreenState;
  private boolean mScreenUpdatePending;
  private final Runnable mScreenUpdateRunnable = new Runnable()
  {
    public void run()
    {
      DisplayPowerState.-set3(DisplayPowerState.this, false);
      int i;
      if ((DisplayPowerState.-get6(DisplayPowerState.this) != 1) && (DisplayPowerState.-get2(DisplayPowerState.this) > 0.0F))
      {
        i = DisplayPowerState.-get5(DisplayPowerState.this);
        if (!DisplayPowerState.-get4(DisplayPowerState.this).setState(DisplayPowerState.-get6(DisplayPowerState.this), i)) {
          break label97;
        }
        if (DisplayPowerState.DEBUG) {
          Slog.d("DisplayPowerState", "Screen ready");
        }
        DisplayPowerState.-set2(DisplayPowerState.this, true);
        DisplayPowerState.-wrap0(DisplayPowerState.this);
      }
      label97:
      while (!DisplayPowerState.DEBUG)
      {
        return;
        i = 0;
        break;
      }
      Slog.d("DisplayPowerState", "Screen not ready");
    }
  };
  
  public DisplayPowerState(DisplayBlanker paramDisplayBlanker, ColorFade paramColorFade)
  {
    this.mBlanker = paramDisplayBlanker;
    this.mColorFade = paramColorFade;
    this.mPhotonicModulator = new PhotonicModulator();
    this.mPhotonicModulator.start();
    this.mScreenState = 2;
    this.mScreenBrightness = 255;
    scheduleScreenUpdate();
    this.mColorFadePrepared = false;
    this.mColorFadeLevel = 1.0F;
    this.mColorFadeReady = true;
  }
  
  private void invokeCleanListenerIfNeeded()
  {
    Runnable localRunnable = this.mCleanListener;
    if ((localRunnable != null) && (this.mScreenReady) && (this.mColorFadeReady))
    {
      this.mCleanListener = null;
      localRunnable.run();
    }
  }
  
  private void postScreenUpdateThreadSafe()
  {
    this.mHandler.removeCallbacks(this.mScreenUpdateRunnable);
    this.mHandler.post(this.mScreenUpdateRunnable);
  }
  
  private void scheduleColorFadeDraw()
  {
    if (!this.mColorFadeDrawPending)
    {
      this.mColorFadeDrawPending = true;
      this.mChoreographer.postCallback(2, this.mColorFadeDrawRunnable, null);
    }
  }
  
  private void scheduleScreenUpdate()
  {
    if (!this.mScreenUpdatePending)
    {
      this.mScreenUpdatePending = true;
      postScreenUpdateThreadSafe();
    }
  }
  
  public void dismissColorFade()
  {
    this.mColorFade.dismiss();
    this.mColorFadePrepared = false;
    this.mColorFadeReady = true;
  }
  
  public void dismissColorFadeResources()
  {
    this.mColorFade.dismissResources();
  }
  
  public void dump(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println();
    paramPrintWriter.println("Display Power State:");
    paramPrintWriter.println("  mScreenState=" + Display.stateToString(this.mScreenState));
    paramPrintWriter.println("  mScreenBrightness=" + this.mScreenBrightness);
    paramPrintWriter.println("  mScreenReady=" + this.mScreenReady);
    paramPrintWriter.println("  mScreenUpdatePending=" + this.mScreenUpdatePending);
    paramPrintWriter.println("  mColorFadePrepared=" + this.mColorFadePrepared);
    paramPrintWriter.println("  mColorFadeLevel=" + this.mColorFadeLevel);
    paramPrintWriter.println("  mColorFadeReady=" + this.mColorFadeReady);
    paramPrintWriter.println("  mColorFadeDrawPending=" + this.mColorFadeDrawPending);
    this.mPhotonicModulator.dump(paramPrintWriter);
    this.mColorFade.dump(paramPrintWriter);
  }
  
  public float getColorFadeLevel()
  {
    return this.mColorFadeLevel;
  }
  
  public int getScreenBrightness()
  {
    return this.mScreenBrightness;
  }
  
  public int getScreenState()
  {
    return this.mScreenState;
  }
  
  public boolean prepareColorFade(Context paramContext, int paramInt)
  {
    if (!this.mColorFade.prepare(paramContext, paramInt))
    {
      this.mColorFadePrepared = false;
      this.mColorFadeReady = true;
      return false;
    }
    this.mColorFadePrepared = true;
    this.mColorFadeReady = false;
    scheduleColorFadeDraw();
    return true;
  }
  
  public void setColorFadeLevel(float paramFloat)
  {
    if (this.mColorFadeLevel != paramFloat)
    {
      if (DEBUG) {
        Slog.d("DisplayPowerState", "setColorFadeLevel: level=" + paramFloat);
      }
      this.mColorFadeLevel = paramFloat;
      if (this.mScreenState != 1)
      {
        this.mScreenReady = false;
        scheduleScreenUpdate();
      }
      if (this.mColorFadePrepared)
      {
        this.mColorFadeReady = false;
        scheduleColorFadeDraw();
      }
    }
  }
  
  public void setScreenBrightness(int paramInt)
  {
    if (this.mScreenBrightness != paramInt)
    {
      if (DEBUG) {
        Slog.d("DisplayPowerState", "setScreenBrightness: brightness=" + paramInt);
      }
      this.mScreenBrightness = paramInt;
      if (this.mScreenState != 1)
      {
        this.mScreenReady = false;
        scheduleScreenUpdate();
      }
    }
  }
  
  public void setScreenState(int paramInt)
  {
    if (this.mScreenState != paramInt)
    {
      if (DEBUG) {
        Slog.d("DisplayPowerState", "setScreenState: state=" + paramInt);
      }
      this.mScreenState = paramInt;
      this.mScreenReady = false;
      scheduleScreenUpdate();
    }
  }
  
  public boolean waitUntilClean(Runnable paramRunnable)
  {
    if ((this.mScreenReady) && (this.mColorFadeReady))
    {
      this.mCleanListener = null;
      return true;
    }
    this.mCleanListener = paramRunnable;
    return false;
  }
  
  private final class PhotonicModulator
    extends Thread
  {
    private static final int INITIAL_BACKLIGHT = -1;
    private static final int INITIAL_SCREEN_STATE = 1;
    private int mActualBacklight = -1;
    private int mActualState = 1;
    private boolean mBacklightChangeInProgress;
    private final Object mLock = new Object();
    private int mPendingBacklight = -1;
    private int mPendingState = 1;
    private boolean mStateChangeInProgress;
    
    public PhotonicModulator()
    {
      super();
    }
    
    public void dump(PrintWriter paramPrintWriter)
    {
      synchronized (this.mLock)
      {
        paramPrintWriter.println();
        paramPrintWriter.println("Photonic Modulator State:");
        paramPrintWriter.println("  mPendingState=" + Display.stateToString(this.mPendingState));
        paramPrintWriter.println("  mPendingBacklight=" + this.mPendingBacklight);
        paramPrintWriter.println("  mActualState=" + Display.stateToString(this.mActualState));
        paramPrintWriter.println("  mActualBacklight=" + this.mActualBacklight);
        paramPrintWriter.println("  mStateChangeInProgress=" + this.mStateChangeInProgress);
        paramPrintWriter.println("  mBacklightChangeInProgress=" + this.mBacklightChangeInProgress);
        return;
      }
    }
    
    public void run()
    {
      for (;;)
      {
        int i;
        int j;
        synchronized (this.mLock)
        {
          int k = this.mPendingState;
          if (k != this.mActualState)
          {
            i = 1;
            int m = this.mPendingBacklight;
            if (m != this.mActualBacklight)
            {
              j = 1;
              if (i == 0)
              {
                DisplayPowerState.-wrap1(DisplayPowerState.this);
                this.mStateChangeInProgress = false;
              }
              if (j != 0) {
                break label181;
              }
              this.mBacklightChangeInProgress = false;
              break label181;
              label69:
              this.mActualState = k;
              this.mActualBacklight = m;
              if (DisplayPowerState.DEBUG) {
                Slog.d("DisplayPowerState", "Updating screen state: state=" + Display.stateToString(k) + ", backlight=" + m);
              }
              DisplayPowerState.-get0(DisplayPowerState.this).requestDisplayState(k, m);
            }
          }
          else
          {
            i = 0;
            continue;
          }
          j = 0;
        }
        label181:
        do
        {
          try
          {
            this.mLock.wait();
            break;
            localObject2 = finally;
            throw ((Throwable)localObject2);
          }
          catch (InterruptedException localInterruptedException)
          {
            for (;;) {}
          }
          if (i != 0) {
            break label69;
          }
        } while (j == 0);
      }
    }
    
    public boolean setState(int paramInt1, int paramInt2)
    {
      for (;;)
      {
        boolean bool1;
        boolean bool2;
        synchronized (this.mLock)
        {
          if (paramInt1 != this.mPendingState)
          {
            bool1 = true;
            if (paramInt2 != this.mPendingBacklight)
            {
              bool2 = true;
              break label168;
              if (DisplayPowerState.DEBUG) {
                Slog.d("DisplayPowerState", "Requesting new screen state: state=" + Display.stateToString(paramInt1) + ", backlight=" + paramInt2);
              }
              this.mPendingState = paramInt1;
              this.mPendingBacklight = paramInt2;
              if (this.mStateChangeInProgress) {
                continue;
              }
              bool3 = this.mBacklightChangeInProgress;
              this.mStateChangeInProgress = bool1;
              this.mBacklightChangeInProgress = bool2;
              if (!bool3) {
                this.mLock.notifyAll();
              }
              bool1 = this.mStateChangeInProgress;
              if (!bool1) {
                continue;
              }
              bool1 = false;
              return bool1;
            }
          }
          else
          {
            bool1 = false;
            continue;
          }
          bool2 = false;
          break label168;
          boolean bool3 = true;
          continue;
          bool1 = true;
        }
        label168:
        if (!bool1) {
          if (!bool2) {}
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/display/DisplayPowerState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */