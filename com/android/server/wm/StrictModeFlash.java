package com.android.server.wm;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.view.Display;
import android.view.Surface;
import android.view.Surface.OutOfResourcesException;
import android.view.SurfaceControl;
import android.view.SurfaceSession;

class StrictModeFlash
{
  private static final String TAG = "WindowManager";
  private boolean mDrawNeeded;
  private int mLastDH;
  private int mLastDW;
  private final Surface mSurface = new Surface();
  private final SurfaceControl mSurfaceControl;
  private final int mThickness = 20;
  
  public StrictModeFlash(Display paramDisplay, SurfaceSession paramSurfaceSession)
  {
    try
    {
      paramSurfaceSession = new SurfaceControl(paramSurfaceSession, "StrictModeFlash", 1, 1, -3, 4);
    }
    catch (Surface.OutOfResourcesException paramDisplay)
    {
      for (;;)
      {
        label71:
        paramDisplay = null;
      }
    }
    try
    {
      paramSurfaceSession.setLayerStack(paramDisplay.getLayerStack());
      paramSurfaceSession.setLayer(1010000);
      paramSurfaceSession.setPosition(0.0F, 0.0F);
      paramSurfaceSession.show();
      this.mSurface.copyFrom(paramSurfaceSession);
      paramDisplay = paramSurfaceSession;
    }
    catch (Surface.OutOfResourcesException paramDisplay)
    {
      paramDisplay = paramSurfaceSession;
      break label71;
    }
    this.mSurfaceControl = paramDisplay;
    this.mDrawNeeded = true;
  }
  
  private void drawIfNeeded()
  {
    if (!this.mDrawNeeded) {
      return;
    }
    this.mDrawNeeded = false;
    int i = this.mLastDW;
    int j = this.mLastDH;
    Object localObject2 = new Rect(0, 0, i, j);
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
    ((Canvas)localObject1).clipRect(new Rect(0, 0, i, 20), Region.Op.REPLACE);
    ((Canvas)localObject1).drawColor(-65536);
    ((Canvas)localObject1).clipRect(new Rect(0, 0, 20, j), Region.Op.REPLACE);
    ((Canvas)localObject1).drawColor(-65536);
    ((Canvas)localObject1).clipRect(new Rect(i - 20, 0, i, j), Region.Op.REPLACE);
    ((Canvas)localObject1).drawColor(-65536);
    ((Canvas)localObject1).clipRect(new Rect(0, j - 20, i, j), Region.Op.REPLACE);
    ((Canvas)localObject1).drawColor(-65536);
    this.mSurface.unlockCanvasAndPost((Canvas)localObject1);
  }
  
  void positionSurface(int paramInt1, int paramInt2)
  {
    if ((this.mLastDW == paramInt1) && (this.mLastDH == paramInt2)) {
      return;
    }
    this.mLastDW = paramInt1;
    this.mLastDH = paramInt2;
    this.mSurfaceControl.setSize(paramInt1, paramInt2);
    this.mDrawNeeded = true;
  }
  
  public void setVisibility(boolean paramBoolean)
  {
    if (this.mSurfaceControl == null) {
      return;
    }
    drawIfNeeded();
    if (paramBoolean)
    {
      this.mSurfaceControl.show();
      return;
    }
    this.mSurfaceControl.hide();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/StrictModeFlash.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */