package com.android.server.wm;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Slog;
import android.view.Display;
import android.view.Surface;
import android.view.Surface.OutOfResourcesException;
import android.view.SurfaceControl;
import android.view.SurfaceSession;

class CircularDisplayMask
{
  private static final String TAG = "WindowManager";
  private boolean mDimensionsUnequal = false;
  private boolean mDrawNeeded;
  private int mLastDH;
  private int mLastDW;
  private int mMaskThickness;
  private Paint mPaint;
  private int mRotation;
  private int mScreenOffset = 0;
  private Point mScreenSize = new Point();
  private final Surface mSurface = new Surface();
  private final SurfaceControl mSurfaceControl;
  private boolean mVisible;
  
  public CircularDisplayMask(Display paramDisplay, SurfaceSession paramSurfaceSession, int paramInt1, int paramInt2, int paramInt3)
  {
    paramDisplay.getSize(this.mScreenSize);
    if (this.mScreenSize.x != this.mScreenSize.y + paramInt2)
    {
      Slog.w(TAG, "Screen dimensions of displayId = " + paramDisplay.getDisplayId() + "are not equal, circularMask will not be drawn.");
      this.mDimensionsUnequal = true;
    }
    try
    {
      if (WindowManagerDebugConfig.DEBUG_SURFACE_TRACE) {
        paramSurfaceSession = new WindowSurfaceController.SurfaceTrace(paramSurfaceSession, "CircularDisplayMask", this.mScreenSize.x, this.mScreenSize.y, -3, 4);
      }
    }
    catch (Surface.OutOfResourcesException paramDisplay)
    {
      try
      {
        paramSurfaceSession.setLayerStack(paramDisplay.getLayerStack());
        paramSurfaceSession.setLayer(paramInt1);
        paramSurfaceSession.setPosition(0.0F, 0.0F);
        paramSurfaceSession.show();
        this.mSurface.copyFrom(paramSurfaceSession);
        for (;;)
        {
          this.mSurfaceControl = paramSurfaceSession;
          this.mDrawNeeded = true;
          this.mPaint = new Paint();
          this.mPaint.setAntiAlias(true);
          this.mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
          this.mScreenOffset = paramInt2;
          this.mMaskThickness = paramInt3;
          return;
          paramSurfaceSession = new SurfaceControl(paramSurfaceSession, "CircularDisplayMask", this.mScreenSize.x, this.mScreenSize.y, -3, 4);
          break;
          paramDisplay = paramDisplay;
          paramSurfaceSession = null;
        }
      }
      catch (Surface.OutOfResourcesException paramDisplay)
      {
        for (;;) {}
      }
    }
  }
  
  private void drawIfNeeded()
  {
    if ((!this.mDrawNeeded) || (!this.mVisible) || (this.mDimensionsUnequal)) {
      return;
    }
    this.mDrawNeeded = false;
    Object localObject2 = new Rect(0, 0, this.mScreenSize.x, this.mScreenSize.y);
    Object localObject1 = null;
    try
    {
      localObject2 = this.mSurface.lockCanvas((Rect)localObject2);
      localObject1 = localObject2;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      for (;;) {}
    }
    catch (Surface.OutOfResourcesException localOutOfResourcesException)
    {
      for (;;) {}
    }
    if (localObject1 == null) {
      return;
    }
    switch (this.mRotation)
    {
    }
    for (;;)
    {
      int i = this.mScreenSize.x / 2;
      ((Canvas)localObject1).drawColor(-16777216);
      ((Canvas)localObject1).drawCircle(i, i, i - this.mMaskThickness, this.mPaint);
      this.mSurface.unlockCanvasAndPost((Canvas)localObject1);
      return;
      this.mSurfaceControl.setPosition(0.0F, 0.0F);
      continue;
      this.mSurfaceControl.setPosition(0.0F, -this.mScreenOffset);
      continue;
      this.mSurfaceControl.setPosition(-this.mScreenOffset, 0.0F);
    }
  }
  
  void positionSurface(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((this.mLastDW == paramInt1) && (this.mLastDH == paramInt2) && (this.mRotation == paramInt3)) {
      return;
    }
    this.mLastDW = paramInt1;
    this.mLastDH = paramInt2;
    this.mDrawNeeded = true;
    this.mRotation = paramInt3;
    drawIfNeeded();
  }
  
  public void setVisibility(boolean paramBoolean)
  {
    if (this.mSurfaceControl == null) {
      return;
    }
    this.mVisible = paramBoolean;
    drawIfNeeded();
    if (paramBoolean)
    {
      this.mSurfaceControl.show();
      return;
    }
    this.mSurfaceControl.hide();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/CircularDisplayMask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */