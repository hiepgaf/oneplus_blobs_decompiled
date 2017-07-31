package com.android.server.wm;

import android.graphics.Rect;
import android.os.SystemClock;
import android.util.Slog;
import android.view.DisplayInfo;
import android.view.SurfaceControl;
import java.io.PrintWriter;

public class DimLayer
{
  private static final String TAG = "WindowManager";
  private float mAlpha = 0.0F;
  private final Rect mBounds = new Rect();
  private boolean mDestroyed = false;
  private SurfaceControl mDimSurface;
  private final int mDisplayId;
  private long mDuration;
  private final Rect mLastBounds = new Rect();
  private int mLayer = -1;
  private final String mName;
  private final WindowManagerService mService;
  private boolean mShowing = false;
  private float mStartAlpha = 0.0F;
  private long mStartTime;
  private float mTargetAlpha = 0.0F;
  private final DimLayerUser mUser;
  
  DimLayer(WindowManagerService paramWindowManagerService, DimLayerUser paramDimLayerUser, int paramInt, String paramString)
  {
    this.mUser = paramDimLayerUser;
    this.mDisplayId = paramInt;
    this.mService = paramWindowManagerService;
    this.mName = paramString;
    if (WindowManagerDebugConfig.DEBUG_DIM_LAYER) {
      Slog.v(TAG, "Ctor: displayId=" + paramInt);
    }
  }
  
  private void adjustAlpha(float paramFloat)
  {
    if (WindowManagerDebugConfig.DEBUG_DIM_LAYER) {
      Slog.v(TAG, "setAlpha alpha=" + paramFloat);
    }
    try
    {
      if (this.mDimSurface != null) {
        this.mDimSurface.setAlpha(paramFloat);
      }
      if ((paramFloat == 0.0F) && (this.mShowing))
      {
        if (WindowManagerDebugConfig.DEBUG_DIM_LAYER) {
          Slog.v(TAG, "setAlpha hiding");
        }
        if (this.mDimSurface != null)
        {
          this.mDimSurface.hide();
          this.mShowing = false;
        }
      }
      else if ((paramFloat > 0.0F) && (!this.mShowing))
      {
        if (WindowManagerDebugConfig.DEBUG_DIM_LAYER) {
          Slog.v(TAG, "setAlpha showing");
        }
        if (this.mDimSurface != null)
        {
          this.mDimSurface.show();
          this.mShowing = true;
          return;
        }
      }
    }
    catch (RuntimeException localRuntimeException)
    {
      Slog.w(TAG, "Failure setting alpha immediately", localRuntimeException);
    }
  }
  
  private void adjustBounds()
  {
    if (this.mUser.dimFullscreen()) {
      getBoundsForFullscreen(this.mBounds);
    }
    if (this.mDimSurface != null)
    {
      this.mDimSurface.setPosition(this.mBounds.left, this.mBounds.top);
      this.mDimSurface.setSize(this.mBounds.width(), this.mBounds.height());
      if (WindowManagerDebugConfig.DEBUG_DIM_LAYER) {
        Slog.v(TAG, "adjustBounds user=" + this.mUser.toShortString() + " mBounds=" + this.mBounds);
      }
    }
    this.mLastBounds.set(this.mBounds);
  }
  
  private void adjustLayer(int paramInt)
  {
    if (this.mDimSurface != null) {
      this.mDimSurface.setLayer(paramInt);
    }
  }
  
  /* Error */
  private void constructSurface(WindowManagerService paramWindowManagerService)
  {
    // Byte code:
    //   0: invokestatic 184	android/view/SurfaceControl:openTransaction	()V
    //   3: getstatic 187	com/android/server/wm/WindowManagerDebugConfig:DEBUG_SURFACE_TRACE	Z
    //   6: ifeq +108 -> 114
    //   9: aload_0
    //   10: new 189	com/android/server/wm/WindowSurfaceController$SurfaceTrace
    //   13: dup
    //   14: aload_1
    //   15: getfield 195	com/android/server/wm/WindowManagerService:mFxSession	Landroid/view/SurfaceSession;
    //   18: ldc -59
    //   20: bipush 16
    //   22: bipush 16
    //   24: iconst_m1
    //   25: ldc -58
    //   27: invokespecial 201	com/android/server/wm/WindowSurfaceController$SurfaceTrace:<init>	(Landroid/view/SurfaceSession;Ljava/lang/String;IIII)V
    //   30: putfield 108	com/android/server/wm/DimLayer:mDimSurface	Landroid/view/SurfaceControl;
    //   33: getstatic 204	com/android/server/wm/WindowManagerDebugConfig:SHOW_TRANSACTIONS	Z
    //   36: ifne +9 -> 45
    //   39: getstatic 207	com/android/server/wm/WindowManagerDebugConfig:SHOW_SURFACE_ALLOC	Z
    //   42: ifeq +37 -> 79
    //   45: getstatic 38	com/android/server/wm/DimLayer:TAG	Ljava/lang/String;
    //   48: new 77	java/lang/StringBuilder
    //   51: dup
    //   52: invokespecial 78	java/lang/StringBuilder:<init>	()V
    //   55: ldc -47
    //   57: invokevirtual 84	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   60: aload_0
    //   61: getfield 108	com/android/server/wm/DimLayer:mDimSurface	Landroid/view/SurfaceControl;
    //   64: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   67: ldc -45
    //   69: invokevirtual 84	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   72: invokevirtual 91	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   75: invokestatic 214	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   78: pop
    //   79: aload_0
    //   80: getfield 108	com/android/server/wm/DimLayer:mDimSurface	Landroid/view/SurfaceControl;
    //   83: aload_0
    //   84: getfield 66	com/android/server/wm/DimLayer:mDisplayId	I
    //   87: invokevirtual 217	android/view/SurfaceControl:setLayerStack	(I)V
    //   90: aload_0
    //   91: invokespecial 219	com/android/server/wm/DimLayer:adjustBounds	()V
    //   94: aload_0
    //   95: aload_0
    //   96: getfield 45	com/android/server/wm/DimLayer:mAlpha	F
    //   99: invokespecial 221	com/android/server/wm/DimLayer:adjustAlpha	(F)V
    //   102: aload_0
    //   103: aload_0
    //   104: getfield 47	com/android/server/wm/DimLayer:mLayer	I
    //   107: invokespecial 223	com/android/server/wm/DimLayer:adjustLayer	(I)V
    //   110: invokestatic 226	android/view/SurfaceControl:closeTransaction	()V
    //   113: return
    //   114: aload_0
    //   115: new 110	android/view/SurfaceControl
    //   118: dup
    //   119: aload_1
    //   120: getfield 195	com/android/server/wm/WindowManagerService:mFxSession	Landroid/view/SurfaceSession;
    //   123: aload_0
    //   124: getfield 70	com/android/server/wm/DimLayer:mName	Ljava/lang/String;
    //   127: bipush 16
    //   129: bipush 16
    //   131: iconst_m1
    //   132: ldc -58
    //   134: invokespecial 227	android/view/SurfaceControl:<init>	(Landroid/view/SurfaceSession;Ljava/lang/String;IIII)V
    //   137: putfield 108	com/android/server/wm/DimLayer:mDimSurface	Landroid/view/SurfaceControl;
    //   140: goto -107 -> 33
    //   143: astore_1
    //   144: ldc 36
    //   146: ldc -27
    //   148: aload_1
    //   149: invokestatic 232	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   152: pop
    //   153: invokestatic 226	android/view/SurfaceControl:closeTransaction	()V
    //   156: return
    //   157: astore_1
    //   158: invokestatic 226	android/view/SurfaceControl:closeTransaction	()V
    //   161: aload_1
    //   162: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	163	0	this	DimLayer
    //   0	163	1	paramWindowManagerService	WindowManagerService
    // Exception table:
    //   from	to	target	type
    //   3	33	143	java/lang/Exception
    //   33	45	143	java/lang/Exception
    //   45	79	143	java/lang/Exception
    //   79	110	143	java/lang/Exception
    //   114	140	143	java/lang/Exception
    //   3	33	157	finally
    //   33	45	157	finally
    //   45	79	157	finally
    //   79	110	157	finally
    //   114	140	157	finally
    //   144	153	157	finally
  }
  
  private boolean durationEndsEarlier(long paramLong)
  {
    return SystemClock.uptimeMillis() + paramLong < this.mStartTime + this.mDuration;
  }
  
  private void getBoundsForFullscreen(Rect paramRect)
  {
    DisplayInfo localDisplayInfo = this.mUser.getDisplayInfo();
    int i = (int)(localDisplayInfo.logicalWidth * 1.5D);
    int j = (int)(localDisplayInfo.logicalHeight * 1.5D);
    float f1 = i * -1 / 6;
    float f2 = j * -1 / 6;
    paramRect.set((int)f1, (int)f2, (int)f1 + i, (int)f2 + j);
  }
  
  private void setAlpha(float paramFloat)
  {
    if (this.mAlpha == paramFloat) {
      return;
    }
    this.mAlpha = paramFloat;
    adjustAlpha(paramFloat);
  }
  
  void destroySurface()
  {
    if (WindowManagerDebugConfig.DEBUG_DIM_LAYER) {
      Slog.v(TAG, "destroySurface.");
    }
    if (this.mDimSurface != null)
    {
      this.mDimSurface.destroy();
      this.mDimSurface = null;
    }
    this.mDestroyed = true;
  }
  
  int getLayer()
  {
    return this.mLayer;
  }
  
  float getTargetAlpha()
  {
    return this.mTargetAlpha;
  }
  
  void hide()
  {
    if (this.mShowing)
    {
      if (WindowManagerDebugConfig.DEBUG_DIM_LAYER) {
        Slog.v(TAG, "hide: immediate");
      }
      hide(0L);
    }
  }
  
  void hide(long paramLong)
  {
    if ((this.mShowing) && ((this.mTargetAlpha != 0.0F) || (durationEndsEarlier(paramLong))))
    {
      if (WindowManagerDebugConfig.DEBUG_DIM_LAYER) {
        Slog.v(TAG, "hide: duration=" + paramLong);
      }
      show(this.mLayer, 0.0F, paramLong);
    }
  }
  
  boolean isAnimating()
  {
    return this.mTargetAlpha != this.mAlpha;
  }
  
  boolean isDimming()
  {
    return this.mTargetAlpha != 0.0F;
  }
  
  public void printTo(String paramString, PrintWriter paramPrintWriter)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mDimSurface=");
    paramPrintWriter.print(this.mDimSurface);
    paramPrintWriter.print(" mLayer=");
    paramPrintWriter.print(this.mLayer);
    paramPrintWriter.print(" mAlpha=");
    paramPrintWriter.println(this.mAlpha);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mLastBounds=");
    paramPrintWriter.print(this.mLastBounds.toShortString());
    paramPrintWriter.print(" mBounds=");
    paramPrintWriter.println(this.mBounds.toShortString());
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("Last animation: ");
    paramPrintWriter.print(" mDuration=");
    paramPrintWriter.print(this.mDuration);
    paramPrintWriter.print(" mStartTime=");
    paramPrintWriter.print(this.mStartTime);
    paramPrintWriter.print(" curTime=");
    paramPrintWriter.println(SystemClock.uptimeMillis());
    paramPrintWriter.print(paramString);
    paramPrintWriter.print(" mStartAlpha=");
    paramPrintWriter.print(this.mStartAlpha);
    paramPrintWriter.print(" mTargetAlpha=");
    paramPrintWriter.println(this.mTargetAlpha);
  }
  
  void setBounds(Rect paramRect)
  {
    this.mBounds.set(paramRect);
    if ((!isDimming()) || (this.mLastBounds.equals(paramRect))) {
      return;
    }
    try
    {
      SurfaceControl.openTransaction();
      adjustBounds();
      return;
    }
    catch (RuntimeException paramRect)
    {
      Slog.w(TAG, "Failure setting size", paramRect);
      return;
    }
    finally
    {
      SurfaceControl.closeTransaction();
    }
  }
  
  void setBoundsForFullscreen()
  {
    getBoundsForFullscreen(this.mBounds);
    setBounds(this.mBounds);
  }
  
  void setLayer(int paramInt)
  {
    if (this.mLayer == paramInt) {
      return;
    }
    this.mLayer = paramInt;
    adjustLayer(paramInt);
  }
  
  void show()
  {
    if (isAnimating())
    {
      if (WindowManagerDebugConfig.DEBUG_DIM_LAYER) {
        Slog.v(TAG, "show: immediate");
      }
      show(this.mLayer, this.mTargetAlpha, 0L);
    }
  }
  
  void show(int paramInt, float paramFloat, long paramLong)
  {
    if (WindowManagerDebugConfig.DEBUG_DIM_LAYER) {
      Slog.v(TAG, "show: layer=" + paramInt + " alpha=" + paramFloat + " duration=" + paramLong + ", mDestroyed=" + this.mDestroyed);
    }
    if (this.mDestroyed)
    {
      Slog.e(TAG, "show: no Surface");
      this.mAlpha = 0.0F;
      this.mTargetAlpha = 0.0F;
      return;
    }
    if (this.mDimSurface == null) {
      constructSurface(this.mService);
    }
    if (!this.mLastBounds.equals(this.mBounds)) {
      adjustBounds();
    }
    setLayer(paramInt);
    long l = SystemClock.uptimeMillis();
    boolean bool = isAnimating();
    if (((bool) && ((this.mTargetAlpha != paramFloat) || (durationEndsEarlier(paramLong)))) || ((!bool) && (this.mAlpha != paramFloat)))
    {
      if (paramLong > 0L) {
        break label258;
      }
      setAlpha(paramFloat);
    }
    for (;;)
    {
      this.mTargetAlpha = paramFloat;
      if (WindowManagerDebugConfig.DEBUG_DIM_LAYER) {
        Slog.v(TAG, "show: mStartAlpha=" + this.mStartAlpha + " mStartTime=" + this.mStartTime + " mTargetAlpha=" + this.mTargetAlpha);
      }
      return;
      label258:
      this.mStartAlpha = this.mAlpha;
      this.mStartTime = l;
      this.mDuration = paramLong;
    }
  }
  
  boolean stepAnimation()
  {
    if (this.mDestroyed)
    {
      Slog.e(TAG, "stepAnimation: surface destroyed");
      this.mAlpha = 0.0F;
      this.mTargetAlpha = 0.0F;
      return false;
    }
    long l;
    float f3;
    float f2;
    if (isAnimating())
    {
      l = SystemClock.uptimeMillis();
      f3 = this.mTargetAlpha - this.mStartAlpha;
      f2 = this.mStartAlpha + (float)(l - this.mStartTime) * f3 / (float)this.mDuration;
      if ((f3 <= 0.0F) || (f2 <= this.mTargetAlpha)) {
        break label147;
      }
    }
    for (;;)
    {
      float f1 = this.mTargetAlpha;
      label147:
      do
      {
        do
        {
          if (WindowManagerDebugConfig.DEBUG_DIM_LAYER) {
            Slog.v(TAG, "stepAnimation: curTime=" + l + " alpha=" + f1);
          }
          setAlpha(f1);
          return isAnimating();
          f1 = f2;
        } while (f3 >= 0.0F);
        f1 = f2;
      } while (f2 >= this.mTargetAlpha);
    }
  }
  
  static abstract interface DimLayerUser
  {
    public abstract boolean dimFullscreen();
    
    public abstract void getDimBounds(Rect paramRect);
    
    public abstract DisplayInfo getDisplayInfo();
    
    public abstract String toShortString();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/DimLayer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */