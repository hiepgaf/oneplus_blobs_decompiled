package com.android.server.wm;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.Surface;
import android.view.Surface.OutOfResourcesException;
import android.view.SurfaceControl;
import android.view.SurfaceSession;

class EmulatorDisplayOverlay
{
  private static final String TAG = "WindowManager";
  private boolean mDrawNeeded;
  private int mLastDH;
  private int mLastDW;
  private Drawable mOverlay;
  private int mRotation;
  private Point mScreenSize = new Point();
  private final Surface mSurface = new Surface();
  private final SurfaceControl mSurfaceControl;
  private boolean mVisible;
  
  public EmulatorDisplayOverlay(Context paramContext, Display paramDisplay, SurfaceSession paramSurfaceSession, int paramInt)
  {
    paramDisplay.getSize(this.mScreenSize);
    try
    {
      if (WindowManagerDebugConfig.DEBUG_SURFACE_TRACE) {
        paramSurfaceSession = new WindowSurfaceController.SurfaceTrace(paramSurfaceSession, "EmulatorDisplayOverlay", this.mScreenSize.x, this.mScreenSize.y, -3, 4);
      }
    }
    catch (Surface.OutOfResourcesException paramDisplay)
    {
      try
      {
        paramSurfaceSession.setLayerStack(paramDisplay.getLayerStack());
        paramSurfaceSession.setLayer(paramInt);
        paramSurfaceSession.setPosition(0.0F, 0.0F);
        paramSurfaceSession.show();
        this.mSurface.copyFrom(paramSurfaceSession);
        for (;;)
        {
          this.mSurfaceControl = paramSurfaceSession;
          this.mDrawNeeded = true;
          this.mOverlay = paramContext.getDrawable(17302187);
          return;
          paramSurfaceSession = new SurfaceControl(paramSurfaceSession, "EmulatorDisplayOverlay", this.mScreenSize.x, this.mScreenSize.y, -3, 4);
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
    Object localObject2;
    Object localObject1;
    if ((this.mDrawNeeded) && (this.mVisible))
    {
      this.mDrawNeeded = false;
      localObject2 = new Rect(0, 0, this.mScreenSize.x, this.mScreenSize.y);
      localObject1 = null;
    }
    try
    {
      localObject2 = this.mSurface.lockCanvas((Rect)localObject2);
      localObject1 = localObject2;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      int i;
      for (;;) {}
    }
    catch (Surface.OutOfResourcesException localOutOfResourcesException)
    {
      for (;;) {}
    }
    if (localObject1 == null)
    {
      return;
      return;
    }
    ((Canvas)localObject1).drawColor(0, PorterDuff.Mode.SRC);
    this.mSurfaceControl.setPosition(0.0F, 0.0F);
    i = Math.max(this.mScreenSize.x, this.mScreenSize.y);
    this.mOverlay.setBounds(0, 0, i, i);
    this.mOverlay.draw((Canvas)localObject1);
    this.mSurface.unlockCanvasAndPost((Canvas)localObject1);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/EmulatorDisplayOverlay.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */