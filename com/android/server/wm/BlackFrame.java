package com.android.server.wm;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.Slog;
import android.view.Surface.OutOfResourcesException;
import android.view.SurfaceControl;
import android.view.SurfaceSession;
import java.io.PrintWriter;

public class BlackFrame
{
  final BlackSurface[] mBlackSurfaces = new BlackSurface[4];
  final boolean mForceDefaultOrientation;
  final Rect mInnerRect;
  final Rect mOuterRect;
  final float[] mTmpFloats = new float[9];
  final Matrix mTmpMatrix = new Matrix();
  
  public BlackFrame(SurfaceSession paramSurfaceSession, Rect paramRect1, Rect paramRect2, int paramInt1, int paramInt2, boolean paramBoolean)
    throws Surface.OutOfResourcesException
  {
    this.mForceDefaultOrientation = paramBoolean;
    this.mOuterRect = new Rect(paramRect1);
    this.mInnerRect = new Rect(paramRect2);
    try
    {
      if (paramRect1.top < paramRect2.top) {
        this.mBlackSurfaces[0] = new BlackSurface(paramSurfaceSession, paramInt1, paramRect1.left, paramRect1.top, paramRect2.right, paramRect2.top, paramInt2);
      }
      if (paramRect1.left < paramRect2.left) {
        this.mBlackSurfaces[1] = new BlackSurface(paramSurfaceSession, paramInt1, paramRect1.left, paramRect2.top, paramRect2.left, paramRect1.bottom, paramInt2);
      }
      if (paramRect1.bottom > paramRect2.bottom) {
        this.mBlackSurfaces[2] = new BlackSurface(paramSurfaceSession, paramInt1, paramRect2.left, paramRect2.bottom, paramRect1.right, paramRect1.bottom, paramInt2);
      }
      if (paramRect1.right > paramRect2.right) {
        this.mBlackSurfaces[3] = new BlackSurface(paramSurfaceSession, paramInt1, paramRect2.right, paramRect1.top, paramRect1.right, paramRect2.bottom, paramInt2);
      }
      if (1 == 0) {
        kill();
      }
      return;
    }
    finally
    {
      if (0 == 0) {
        kill();
      }
    }
  }
  
  public void clearMatrix()
  {
    int i = 0;
    while (i < this.mBlackSurfaces.length)
    {
      if (this.mBlackSurfaces[i] != null) {
        this.mBlackSurfaces[i].clearMatrix();
      }
      i += 1;
    }
  }
  
  public void hide()
  {
    if (this.mBlackSurfaces != null)
    {
      int i = 0;
      while (i < this.mBlackSurfaces.length)
      {
        if (this.mBlackSurfaces[i] != null) {
          this.mBlackSurfaces[i].surface.hide();
        }
        i += 1;
      }
    }
  }
  
  public void kill()
  {
    if (this.mBlackSurfaces != null)
    {
      int i = 0;
      while (i < this.mBlackSurfaces.length)
      {
        if (this.mBlackSurfaces[i] != null)
        {
          if ((WindowManagerDebugConfig.SHOW_TRANSACTIONS) || (WindowManagerDebugConfig.SHOW_SURFACE_ALLOC)) {
            Slog.i("WindowManager", "  BLACK " + this.mBlackSurfaces[i].surface + ": DESTROY");
          }
          this.mBlackSurfaces[i].surface.destroy();
          this.mBlackSurfaces[i] = null;
        }
        i += 1;
      }
    }
  }
  
  public void printTo(String paramString, PrintWriter paramPrintWriter)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("Outer: ");
    this.mOuterRect.printShortString(paramPrintWriter);
    paramPrintWriter.print(" / Inner: ");
    this.mInnerRect.printShortString(paramPrintWriter);
    paramPrintWriter.println();
    int i = 0;
    while (i < this.mBlackSurfaces.length)
    {
      BlackSurface localBlackSurface = this.mBlackSurfaces[i];
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("#");
      paramPrintWriter.print(i);
      paramPrintWriter.print(": ");
      paramPrintWriter.print(localBlackSurface.surface);
      paramPrintWriter.print(" left=");
      paramPrintWriter.print(localBlackSurface.left);
      paramPrintWriter.print(" top=");
      paramPrintWriter.println(localBlackSurface.top);
      i += 1;
    }
  }
  
  public void setAlpha(float paramFloat)
  {
    int i = 0;
    while (i < this.mBlackSurfaces.length)
    {
      if (this.mBlackSurfaces[i] != null) {
        this.mBlackSurfaces[i].setAlpha(paramFloat);
      }
      i += 1;
    }
  }
  
  public void setMatrix(Matrix paramMatrix)
  {
    int i = 0;
    while (i < this.mBlackSurfaces.length)
    {
      if (this.mBlackSurfaces[i] != null) {
        this.mBlackSurfaces[i].setMatrix(paramMatrix);
      }
      i += 1;
    }
  }
  
  class BlackSurface
  {
    final int layer;
    final int left;
    final SurfaceControl surface;
    final int top;
    
    BlackSurface(SurfaceSession paramSurfaceSession, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
      throws Surface.OutOfResourcesException
    {
      this.left = paramInt2;
      this.top = paramInt3;
      this.layer = paramInt1;
      paramInt4 -= paramInt2;
      paramInt5 -= paramInt3;
      if (WindowManagerDebugConfig.DEBUG_SURFACE_TRACE) {}
      for (this.surface = new WindowSurfaceController.SurfaceTrace(paramSurfaceSession, "BlackSurface(" + paramInt2 + ", " + paramInt3 + ")", paramInt4, paramInt5, -1, 131076);; this.surface = new SurfaceControl(paramSurfaceSession, "BlackSurface", paramInt4, paramInt5, -1, 131076))
      {
        this.surface.setAlpha(1.0F);
        this.surface.setLayerStack(paramInt6);
        this.surface.setLayer(paramInt1);
        this.surface.show();
        if ((WindowManagerDebugConfig.SHOW_TRANSACTIONS) || (WindowManagerDebugConfig.SHOW_SURFACE_ALLOC)) {
          Slog.i("WindowManager", "  BLACK " + this.surface + ": CREATE layer=" + paramInt1);
        }
        return;
      }
    }
    
    void clearMatrix()
    {
      this.surface.setMatrix(1.0F, 0.0F, 0.0F, 1.0F);
    }
    
    void setAlpha(float paramFloat)
    {
      this.surface.setAlpha(paramFloat);
    }
    
    void setMatrix(Matrix paramMatrix)
    {
      BlackFrame.this.mTmpMatrix.setTranslate(this.left, this.top);
      BlackFrame.this.mTmpMatrix.postConcat(paramMatrix);
      BlackFrame.this.mTmpMatrix.getValues(BlackFrame.this.mTmpFloats);
      this.surface.setPosition(BlackFrame.this.mTmpFloats[2], BlackFrame.this.mTmpFloats[5]);
      this.surface.setMatrix(BlackFrame.this.mTmpFloats[0], BlackFrame.this.mTmpFloats[3], BlackFrame.this.mTmpFloats[1], BlackFrame.this.mTmpFloats[4]);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/BlackFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */