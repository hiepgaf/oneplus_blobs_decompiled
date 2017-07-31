package com.android.server.wm;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Debug;
import android.os.IBinder;
import android.util.Slog;
import android.view.Surface;
import android.view.Surface.OutOfResourcesException;
import android.view.SurfaceControl;
import android.view.SurfaceSession;
import android.view.WindowContentFrameStats;
import java.io.PrintWriter;
import java.util.ArrayList;

class WindowSurfaceController
{
  static final String TAG = "WindowManager";
  final WindowStateAnimator mAnimator;
  private boolean mHiddenForCrop = false;
  private boolean mHiddenForOtherReasons = true;
  private float mSurfaceAlpha = 0.0F;
  private SurfaceControl mSurfaceControl;
  private float mSurfaceH = 0.0F;
  private int mSurfaceLayer = 0;
  private boolean mSurfaceShown = false;
  private float mSurfaceW = 0.0F;
  private float mSurfaceX = 0.0F;
  private float mSurfaceY = 0.0F;
  private final String title;
  
  public WindowSurfaceController(SurfaceSession paramSurfaceSession, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, WindowStateAnimator paramWindowStateAnimator)
  {
    this.mAnimator = paramWindowStateAnimator;
    this.mSurfaceW = paramInt1;
    this.mSurfaceH = paramInt2;
    this.title = paramString;
    if ((paramWindowStateAnimator.mWin.isChildWindow()) && (paramWindowStateAnimator.mWin.mSubLayer < 0) && (paramWindowStateAnimator.mWin.mAppToken != null))
    {
      this.mSurfaceControl = new SurfaceControlWithBackground(paramSurfaceSession, paramString, paramInt1, paramInt2, paramInt3, paramInt4, paramWindowStateAnimator.mWin.mAppToken);
      return;
    }
    if (WindowManagerDebugConfig.DEBUG_SURFACE_TRACE)
    {
      this.mSurfaceControl = new SurfaceTrace(paramSurfaceSession, paramString, paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    }
    this.mSurfaceControl = new SurfaceControl(paramSurfaceSession, paramString, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  private void hideSurface()
  {
    if (this.mSurfaceControl != null) {
      this.mSurfaceShown = false;
    }
    try
    {
      this.mSurfaceControl.hide();
      if (WindowManagerDebugConfig.DEBUG_ONEPLUS) {
        WindowManagerService.logSurface(this.mAnimator.mWin, "HIDE", false);
      }
      return;
    }
    catch (RuntimeException localRuntimeException)
    {
      Slog.w(TAG, "Exception hiding surface in " + this);
    }
  }
  
  private boolean showSurface()
  {
    try
    {
      this.mSurfaceShown = true;
      this.mSurfaceControl.show();
      if (WindowManagerDebugConfig.DEBUG_ONEPLUS) {
        WindowManagerService.logSurface(this.mAnimator.mWin, "SHOW alpha=" + this.mAnimator.mShownAlpha + " layer=" + this.mAnimator.mAnimLayer, false);
      }
      return true;
    }
    catch (RuntimeException localRuntimeException)
    {
      Slog.w(TAG, "Failure showing surface " + this.mSurfaceControl + " in " + this, localRuntimeException);
      this.mAnimator.reclaimSomeSurfaceMemory("show", true);
    }
    return false;
  }
  
  private boolean updateVisibility()
  {
    if ((this.mHiddenForCrop) || (this.mHiddenForOtherReasons))
    {
      if (this.mSurfaceShown) {
        hideSurface();
      }
      return false;
    }
    if (!this.mSurfaceShown) {
      return showSurface();
    }
    return true;
  }
  
  void clearCropInTransaction(boolean paramBoolean)
  {
    if (WindowManagerDebugConfig.SHOW_TRANSACTIONS) {
      logSurface("CLEAR CROP", null);
    }
    try
    {
      Rect localRect = new Rect(0, 0, -1, -1);
      this.mSurfaceControl.setWindowCrop(localRect);
      return;
    }
    catch (RuntimeException localRuntimeException)
    {
      do
      {
        Slog.w(TAG, "Error setting clearing crop of " + this, localRuntimeException);
      } while (paramBoolean);
      this.mAnimator.reclaimSomeSurfaceMemory("crop", true);
    }
  }
  
  boolean clearWindowContentFrameStats()
  {
    if (this.mSurfaceControl == null) {
      return false;
    }
    return this.mSurfaceControl.clearContentFrameStats();
  }
  
  void deferTransactionUntil(IBinder paramIBinder, long paramLong)
  {
    this.mSurfaceControl.deferTransactionUntil(paramIBinder, paramLong);
  }
  
  void destroyInTransaction()
  {
    if ((WindowManagerDebugConfig.SHOW_TRANSACTIONS) || (WindowManagerDebugConfig.SHOW_SURFACE_ALLOC)) {
      Slog.i(TAG, "Destroying surface " + this + " called by " + Debug.getCallers(8));
    }
    try
    {
      if (this.mSurfaceControl != null) {
        this.mSurfaceControl.destroy();
      }
      this.mSurfaceShown = false;
    }
    catch (RuntimeException localRuntimeException)
    {
      for (;;)
      {
        Slog.w(TAG, "Error destroying surface in: " + this, localRuntimeException);
        this.mSurfaceShown = false;
      }
    }
    finally
    {
      this.mSurfaceShown = false;
      this.mSurfaceControl = null;
    }
    this.mSurfaceControl = null;
  }
  
  void disconnectInTransaction()
  {
    if ((WindowManagerDebugConfig.SHOW_TRANSACTIONS) || (WindowManagerDebugConfig.SHOW_SURFACE_ALLOC)) {
      Slog.i(TAG, "Disconnecting client: " + this);
    }
    try
    {
      if (this.mSurfaceControl != null) {
        this.mSurfaceControl.disconnect();
      }
      return;
    }
    catch (RuntimeException localRuntimeException)
    {
      Slog.w(TAG, "Error disconnecting surface in: " + this, localRuntimeException);
    }
  }
  
  public void dump(PrintWriter paramPrintWriter, String paramString, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mSurface=");
      paramPrintWriter.println(this.mSurfaceControl);
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("Surface: shown=");
    paramPrintWriter.print(this.mSurfaceShown);
    paramPrintWriter.print(" layer=");
    paramPrintWriter.print(this.mSurfaceLayer);
    paramPrintWriter.print(" alpha=");
    paramPrintWriter.print(this.mSurfaceAlpha);
    paramPrintWriter.print(" rect=(");
    paramPrintWriter.print(this.mSurfaceX);
    paramPrintWriter.print(",");
    paramPrintWriter.print(this.mSurfaceY);
    paramPrintWriter.print(") ");
    paramPrintWriter.print(this.mSurfaceW);
    paramPrintWriter.print(" x ");
    paramPrintWriter.println(this.mSurfaceH);
  }
  
  void forceScaleableInTransaction(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 1;; i = -1)
    {
      this.mSurfaceControl.setOverrideScalingMode(i);
      return;
    }
  }
  
  IBinder getHandle()
  {
    if (this.mSurfaceControl == null) {
      return null;
    }
    return this.mSurfaceControl.getHandle();
  }
  
  float getHeight()
  {
    return this.mSurfaceH;
  }
  
  int getLayer()
  {
    return this.mSurfaceLayer;
  }
  
  boolean getShown()
  {
    return this.mSurfaceShown;
  }
  
  void getSurface(Surface paramSurface)
  {
    paramSurface.copyFrom(this.mSurfaceControl);
  }
  
  boolean getTransformToDisplayInverse()
  {
    return this.mSurfaceControl.getTransformToDisplayInverse();
  }
  
  float getWidth()
  {
    return this.mSurfaceW;
  }
  
  boolean getWindowContentFrameStats(WindowContentFrameStats paramWindowContentFrameStats)
  {
    if (this.mSurfaceControl == null) {
      return false;
    }
    return this.mSurfaceControl.getContentFrameStats(paramWindowContentFrameStats);
  }
  
  float getX()
  {
    return this.mSurfaceX;
  }
  
  float getY()
  {
    return this.mSurfaceY;
  }
  
  boolean hasSurface()
  {
    return this.mSurfaceControl != null;
  }
  
  void hideInTransaction(String paramString)
  {
    if (WindowManagerDebugConfig.SHOW_TRANSACTIONS) {
      logSurface("HIDE ( " + paramString + " )", null);
    }
    this.mHiddenForOtherReasons = true;
    this.mAnimator.destroyPreservedSurfaceLocked();
    updateVisibility();
  }
  
  void logSurface(String paramString, RuntimeException paramRuntimeException)
  {
    paramString = "  SURFACE " + paramString + ": " + this.title;
    if (paramRuntimeException != null)
    {
      Slog.i(TAG, paramString, paramRuntimeException);
      return;
    }
    Slog.i(TAG, paramString);
  }
  
  boolean prepareToShowInTransaction(float paramFloat1, int paramInt, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, boolean paramBoolean)
  {
    if (this.mSurfaceControl != null) {}
    try
    {
      this.mSurfaceAlpha = paramFloat1;
      this.mSurfaceControl.setAlpha(paramFloat1);
      this.mSurfaceLayer = paramInt;
      this.mSurfaceControl.setLayer(paramInt);
      this.mSurfaceControl.setMatrix(paramFloat2, paramFloat3, paramFloat4, paramFloat5);
      return true;
    }
    catch (RuntimeException localRuntimeException)
    {
      Slog.w(TAG, "Error updating surface in " + this.title, localRuntimeException);
      if (!paramBoolean) {
        this.mAnimator.reclaimSomeSurfaceMemory("update", true);
      }
    }
    return false;
  }
  
  void setCropInTransaction(Rect paramRect, boolean paramBoolean)
  {
    if (WindowManagerDebugConfig.SHOW_TRANSACTIONS) {
      logSurface("CROP " + paramRect.toShortString(), null);
    }
    try
    {
      if ((paramRect.width() > 0) && (paramRect.height() > 0))
      {
        this.mSurfaceControl.setWindowCrop(paramRect);
        this.mHiddenForCrop = false;
        updateVisibility();
        return;
      }
      this.mHiddenForCrop = true;
      this.mAnimator.destroyPreservedSurfaceLocked();
      updateVisibility();
      return;
    }
    catch (RuntimeException localRuntimeException)
    {
      Slog.w(TAG, "Error setting crop surface of " + this + " crop=" + paramRect.toShortString(), localRuntimeException);
      if (!paramBoolean) {
        this.mAnimator.reclaimSomeSurfaceMemory("crop", true);
      }
    }
  }
  
  void setFinalCropInTransaction(Rect paramRect)
  {
    if (WindowManagerDebugConfig.SHOW_TRANSACTIONS) {
      logSurface("FINAL CROP " + paramRect.toShortString(), null);
    }
    try
    {
      this.mSurfaceControl.setFinalCrop(paramRect);
      return;
    }
    catch (RuntimeException paramRect)
    {
      Slog.w(TAG, "Error disconnecting surface in: " + this, paramRect);
    }
  }
  
  void setGeometryAppliesWithResizeInTransaction(boolean paramBoolean)
  {
    this.mSurfaceControl.setGeometryAppliesWithResize();
  }
  
  void setLayer(int paramInt)
  {
    if (this.mSurfaceControl != null) {
      SurfaceControl.openTransaction();
    }
    try
    {
      this.mSurfaceControl.setLayer(paramInt);
      return;
    }
    finally
    {
      SurfaceControl.closeTransaction();
    }
  }
  
  void setMatrixInTransaction(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, boolean paramBoolean)
  {
    try
    {
      if (WindowManagerDebugConfig.SHOW_TRANSACTIONS) {
        logSurface("MATRIX [" + paramFloat1 + "," + paramFloat2 + "," + paramFloat3 + "," + paramFloat4 + "]", null);
      }
      this.mSurfaceControl.setMatrix(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
      return;
    }
    catch (RuntimeException localRuntimeException)
    {
      do
      {
        Slog.e(TAG, "Error setting matrix on surface surface" + this.title + " MATRIX [" + paramFloat1 + "," + paramFloat2 + "," + paramFloat3 + "," + paramFloat4 + "]", null);
      } while (paramBoolean);
      this.mAnimator.reclaimSomeSurfaceMemory("matrix", true);
    }
  }
  
  void setOpaque(boolean paramBoolean)
  {
    if (WindowManagerDebugConfig.SHOW_TRANSACTIONS) {
      logSurface("isOpaque=" + paramBoolean, null);
    }
    if (this.mSurfaceControl == null) {
      return;
    }
    if (WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
      Slog.i(TAG, ">>> OPEN TRANSACTION setOpaqueLocked");
    }
    SurfaceControl.openTransaction();
    try
    {
      this.mSurfaceControl.setOpaque(paramBoolean);
      return;
    }
    finally
    {
      SurfaceControl.closeTransaction();
      if (WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
        Slog.i(TAG, "<<< CLOSE TRANSACTION setOpaqueLocked");
      }
    }
  }
  
  void setPositionAndLayer(float paramFloat1, float paramFloat2, int paramInt1, int paramInt2)
  {
    
    try
    {
      this.mSurfaceX = paramFloat1;
      this.mSurfaceY = paramFloat2;
      try
      {
        if (WindowManagerDebugConfig.SHOW_TRANSACTIONS) {
          logSurface("POS (setPositionAndLayer) @ (" + paramFloat1 + "," + paramFloat2 + ")", null);
        }
        this.mSurfaceControl.setPosition(paramFloat1, paramFloat2);
        this.mSurfaceControl.setLayerStack(paramInt1);
        this.mSurfaceControl.setLayer(paramInt2);
        this.mSurfaceControl.setAlpha(0.0F);
        this.mSurfaceShown = false;
        SurfaceControl.closeTransaction();
      }
      catch (RuntimeException localRuntimeException)
      {
        for (;;)
        {
          Slog.w(TAG, "Error creating surface in " + this, localRuntimeException);
          this.mAnimator.reclaimSomeSurfaceMemory("create-init", true);
        }
      }
      if (WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
        Slog.i(TAG, "<<< CLOSE TRANSACTION setPositionAndLayer");
      }
      return;
    }
    finally
    {
      SurfaceControl.closeTransaction();
      if (WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
        Slog.i(TAG, "<<< CLOSE TRANSACTION setPositionAndLayer");
      }
    }
  }
  
  void setPositionInTransaction(float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    if ((this.mSurfaceX != paramFloat1) || (this.mSurfaceY != paramFloat2)) {}
    for (int i = 1;; i = 0)
    {
      if (i != 0)
      {
        this.mSurfaceX = paramFloat1;
        this.mSurfaceY = paramFloat2;
      }
      try
      {
        if (WindowManagerDebugConfig.SHOW_TRANSACTIONS) {
          logSurface("POS (setPositionInTransaction) @ (" + paramFloat1 + "," + paramFloat2 + ")", null);
        }
        this.mSurfaceControl.setPosition(paramFloat1, paramFloat2);
        return;
      }
      catch (RuntimeException localRuntimeException)
      {
        do
        {
          Slog.w(TAG, "Error positioning surface of " + this + " pos=(" + paramFloat1 + "," + paramFloat2 + ")", localRuntimeException);
        } while (paramBoolean);
        this.mAnimator.reclaimSomeSurfaceMemory("position", true);
      }
    }
  }
  
  void setSecure(boolean paramBoolean)
  {
    if (WindowManagerDebugConfig.SHOW_TRANSACTIONS) {
      logSurface("isSecure=" + paramBoolean, null);
    }
    if (this.mSurfaceControl == null) {
      return;
    }
    if (WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
      Slog.i(TAG, ">>> OPEN TRANSACTION setSecureLocked");
    }
    SurfaceControl.openTransaction();
    try
    {
      this.mSurfaceControl.setSecure(paramBoolean);
      return;
    }
    finally
    {
      SurfaceControl.closeTransaction();
      if (WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
        Slog.i(TAG, "<<< CLOSE TRANSACTION setSecureLocked");
      }
    }
  }
  
  void setShown(boolean paramBoolean)
  {
    this.mSurfaceShown = paramBoolean;
  }
  
  boolean setSizeInTransaction(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if ((this.mSurfaceW != paramInt1) || (this.mSurfaceH != paramInt2)) {}
    for (int i = 1;; i = 0)
    {
      if (i == 0) {
        break label175;
      }
      this.mSurfaceW = paramInt1;
      this.mSurfaceH = paramInt2;
      try
      {
        if (WindowManagerDebugConfig.SHOW_TRANSACTIONS) {
          logSurface("SIZE " + paramInt1 + "x" + paramInt2, null);
        }
        this.mSurfaceControl.setSize(paramInt1, paramInt2);
        return true;
      }
      catch (RuntimeException localRuntimeException)
      {
        Slog.e(TAG, "Error resizing surface of " + this.title + " size=(" + paramInt1 + "x" + paramInt2 + ")", localRuntimeException);
        if (paramBoolean) {
          break;
        }
        this.mAnimator.reclaimSomeSurfaceMemory("size", true);
        return false;
      }
    }
    label175:
    return false;
  }
  
  void setTransparentRegionHint(Region paramRegion)
  {
    if (this.mSurfaceControl == null)
    {
      Slog.w(TAG, "setTransparentRegionHint: null mSurface after mHasSurface true");
      return;
    }
    if (WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
      Slog.i(TAG, ">>> OPEN TRANSACTION setTransparentRegion");
    }
    SurfaceControl.openTransaction();
    try
    {
      this.mSurfaceControl.setTransparentRegionHint(paramRegion);
      return;
    }
    finally
    {
      SurfaceControl.closeTransaction();
      if (WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
        Slog.i(TAG, "<<< CLOSE TRANSACTION setTransparentRegion");
      }
    }
  }
  
  boolean showRobustlyInTransaction()
  {
    if (WindowManagerDebugConfig.SHOW_TRANSACTIONS) {
      logSurface("SHOW (performLayout)", null);
    }
    if (WindowManagerDebugConfig.DEBUG_VISIBILITY) {
      Slog.v(TAG, "Showing " + this + " during relayout");
    }
    this.mHiddenForOtherReasons = false;
    return updateVisibility();
  }
  
  public String toString()
  {
    return this.mSurfaceControl.toString();
  }
  
  class SurfaceControlWithBackground
    extends SurfaceControl
  {
    private boolean mAppForcedInvisible = false;
    private AppWindowToken mAppToken;
    private SurfaceControl mBackgroundControl;
    public int mLayer = -1;
    private boolean mOpaque = true;
    public boolean mVisible = false;
    
    public SurfaceControlWithBackground(SurfaceSession paramSurfaceSession, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, AppWindowToken paramAppWindowToken)
      throws Surface.OutOfResourcesException
    {
      super(paramString, paramInt1, paramInt2, paramInt3, paramInt4);
      this.mBackgroundControl = new SurfaceControl(paramSurfaceSession, paramString, paramInt1, paramInt2, -1, paramInt4 | 0x20000);
      if ((paramInt4 & 0x400) != 0) {}
      for (boolean bool = true;; bool = false)
      {
        this.mOpaque = bool;
        this.mAppToken = paramAppWindowToken;
        this.mAppToken.addSurfaceViewBackground(this);
        return;
      }
    }
    
    public void deferTransactionUntil(IBinder paramIBinder, long paramLong)
    {
      super.deferTransactionUntil(paramIBinder, paramLong);
      this.mBackgroundControl.deferTransactionUntil(paramIBinder, paramLong);
    }
    
    public void destroy()
    {
      super.destroy();
      this.mBackgroundControl.destroy();
      this.mAppToken.removeSurfaceViewBackground(this);
    }
    
    public void hide()
    {
      super.hide();
      if (this.mVisible)
      {
        this.mVisible = false;
        this.mAppToken.updateSurfaceViewBackgroundVisibilities();
      }
    }
    
    public void release()
    {
      super.release();
      this.mBackgroundControl.release();
    }
    
    public void setAlpha(float paramFloat)
    {
      super.setAlpha(paramFloat);
      this.mBackgroundControl.setAlpha(paramFloat);
    }
    
    public void setFinalCrop(Rect paramRect)
    {
      super.setFinalCrop(paramRect);
      this.mBackgroundControl.setFinalCrop(paramRect);
    }
    
    public void setLayer(int paramInt)
    {
      super.setLayer(paramInt);
      this.mBackgroundControl.setLayer(paramInt - 1);
      if (this.mLayer != paramInt)
      {
        this.mLayer = paramInt;
        this.mAppToken.updateSurfaceViewBackgroundVisibilities();
      }
    }
    
    public void setLayerStack(int paramInt)
    {
      super.setLayerStack(paramInt);
      this.mBackgroundControl.setLayerStack(paramInt);
    }
    
    public void setMatrix(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
    {
      super.setMatrix(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
      this.mBackgroundControl.setMatrix(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
    }
    
    public void setOpaque(boolean paramBoolean)
    {
      super.setOpaque(paramBoolean);
      this.mOpaque = paramBoolean;
      updateBackgroundVisibility(this.mAppForcedInvisible);
    }
    
    public void setPosition(float paramFloat1, float paramFloat2)
    {
      super.setPosition(paramFloat1, paramFloat2);
      this.mBackgroundControl.setPosition(paramFloat1, paramFloat2);
    }
    
    public void setSecure(boolean paramBoolean)
    {
      super.setSecure(paramBoolean);
    }
    
    public void setSize(int paramInt1, int paramInt2)
    {
      super.setSize(paramInt1, paramInt2);
      this.mBackgroundControl.setSize(paramInt1, paramInt2);
    }
    
    public void setTransparentRegionHint(Region paramRegion)
    {
      super.setTransparentRegionHint(paramRegion);
      this.mBackgroundControl.setTransparentRegionHint(paramRegion);
    }
    
    public void setWindowCrop(Rect paramRect)
    {
      super.setWindowCrop(paramRect);
      this.mBackgroundControl.setWindowCrop(paramRect);
    }
    
    public void show()
    {
      super.show();
      if (!this.mVisible)
      {
        this.mVisible = true;
        this.mAppToken.updateSurfaceViewBackgroundVisibilities();
      }
    }
    
    void updateBackgroundVisibility(boolean paramBoolean)
    {
      this.mAppForcedInvisible = paramBoolean;
      if ((!this.mOpaque) || (!this.mVisible) || (this.mAppForcedInvisible))
      {
        this.mBackgroundControl.hide();
        return;
      }
      this.mBackgroundControl.show();
    }
  }
  
  static class SurfaceTrace
    extends SurfaceControl
  {
    private static final boolean LOG_SURFACE_TRACE = WindowManagerDebugConfig.DEBUG_SURFACE_TRACE;
    private static final String SURFACE_TAG = "WindowManager";
    static final ArrayList<SurfaceTrace> sSurfaces = new ArrayList();
    private float mDsdx;
    private float mDsdy;
    private float mDtdx;
    private float mDtdy;
    private final Rect mFinalCrop = new Rect();
    private boolean mIsOpaque;
    private int mLayer;
    private int mLayerStack;
    private final String mName;
    private final PointF mPosition = new PointF();
    private boolean mShown = false;
    private final Point mSize = new Point();
    private float mSurfaceTraceAlpha = 0.0F;
    private final Rect mWindowCrop = new Rect();
    
    public SurfaceTrace(SurfaceSession arg1, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
      throws Surface.OutOfResourcesException
    {
      super(paramString, paramInt1, paramInt2, paramInt3, paramInt4);
      if (paramString != null) {}
      for (;;)
      {
        this.mName = paramString;
        this.mSize.set(paramInt1, paramInt2);
        if (LOG_SURFACE_TRACE) {
          Slog.v(SURFACE_TAG, "ctor: " + this + ". Called by " + Debug.getCallers(3));
        }
        synchronized (sSurfaces)
        {
          sSurfaces.add(0, this);
          return;
          paramString = "Not named";
        }
      }
    }
    
    static void dumpAllSurfaces(PrintWriter paramPrintWriter, String paramString)
    {
      synchronized (sSurfaces)
      {
        int j = sSurfaces.size();
        if (j <= 0) {
          return;
        }
        if (paramString != null) {
          paramPrintWriter.println(paramString);
        }
        paramPrintWriter.println("WINDOW MANAGER SURFACES (dumpsys window surfaces)");
        int i = 0;
        while (i < j)
        {
          paramString = (SurfaceTrace)sSurfaces.get(i);
          paramPrintWriter.print("  Surface #");
          paramPrintWriter.print(i);
          paramPrintWriter.print(": #");
          paramPrintWriter.print(Integer.toHexString(System.identityHashCode(paramString)));
          paramPrintWriter.print(" ");
          paramPrintWriter.println(paramString.mName);
          paramPrintWriter.print("    mLayerStack=");
          paramPrintWriter.print(paramString.mLayerStack);
          paramPrintWriter.print(" mLayer=");
          paramPrintWriter.println(paramString.mLayer);
          paramPrintWriter.print("    mShown=");
          paramPrintWriter.print(paramString.mShown);
          paramPrintWriter.print(" mAlpha=");
          paramPrintWriter.print(paramString.mSurfaceTraceAlpha);
          paramPrintWriter.print(" mIsOpaque=");
          paramPrintWriter.println(paramString.mIsOpaque);
          paramPrintWriter.print("    mPosition=");
          paramPrintWriter.print(paramString.mPosition.x);
          paramPrintWriter.print(",");
          paramPrintWriter.print(paramString.mPosition.y);
          paramPrintWriter.print(" mSize=");
          paramPrintWriter.print(paramString.mSize.x);
          paramPrintWriter.print("x");
          paramPrintWriter.println(paramString.mSize.y);
          paramPrintWriter.print("    mCrop=");
          paramString.mWindowCrop.printShortString(paramPrintWriter);
          paramPrintWriter.println();
          paramPrintWriter.print("    mFinalCrop=");
          paramString.mFinalCrop.printShortString(paramPrintWriter);
          paramPrintWriter.println();
          paramPrintWriter.print("    Transform: (");
          paramPrintWriter.print(paramString.mDsdx);
          paramPrintWriter.print(", ");
          paramPrintWriter.print(paramString.mDtdx);
          paramPrintWriter.print(", ");
          paramPrintWriter.print(paramString.mDsdy);
          paramPrintWriter.print(", ");
          paramPrintWriter.print(paramString.mDtdy);
          paramPrintWriter.println(")");
          i += 1;
        }
        return;
      }
    }
    
    public void destroy()
    {
      super.destroy();
      if (LOG_SURFACE_TRACE) {
        Slog.v(SURFACE_TAG, "destroy: " + this + ". Called by " + Debug.getCallers(3));
      }
      synchronized (sSurfaces)
      {
        sSurfaces.remove(this);
        return;
      }
    }
    
    public void hide()
    {
      if (this.mShown)
      {
        if (LOG_SURFACE_TRACE) {
          Slog.v(SURFACE_TAG, "hide: OLD:" + this + ". Called by " + Debug.getCallers(3));
        }
        this.mShown = false;
      }
      super.hide();
    }
    
    public void release()
    {
      super.release();
      if (LOG_SURFACE_TRACE) {
        Slog.v(SURFACE_TAG, "release: " + this + ". Called by " + Debug.getCallers(3));
      }
      synchronized (sSurfaces)
      {
        sSurfaces.remove(this);
        return;
      }
    }
    
    public void setAlpha(float paramFloat)
    {
      if (this.mSurfaceTraceAlpha != paramFloat)
      {
        if (LOG_SURFACE_TRACE) {
          Slog.v(SURFACE_TAG, "setAlpha(" + paramFloat + "): OLD:" + this + ". Called by " + Debug.getCallers(3));
        }
        this.mSurfaceTraceAlpha = paramFloat;
      }
      super.setAlpha(paramFloat);
    }
    
    public void setFinalCrop(Rect paramRect)
    {
      if ((paramRect != null) && (!paramRect.equals(this.mFinalCrop)))
      {
        if (LOG_SURFACE_TRACE) {
          Slog.v(SURFACE_TAG, "setFinalCrop(" + paramRect.toShortString() + "): OLD:" + this + ". Called by " + Debug.getCallers(3));
        }
        this.mFinalCrop.set(paramRect);
      }
      super.setFinalCrop(paramRect);
    }
    
    public void setGeometryAppliesWithResize()
    {
      if (LOG_SURFACE_TRACE) {
        Slog.v(SURFACE_TAG, "setGeometryAppliesWithResize(): OLD: " + this + ". Called by" + Debug.getCallers(3));
      }
      super.setGeometryAppliesWithResize();
    }
    
    public void setLayer(int paramInt)
    {
      if (paramInt != this.mLayer)
      {
        if (LOG_SURFACE_TRACE) {
          Slog.v(SURFACE_TAG, "setLayer(" + paramInt + "): OLD:" + this + ". Called by " + Debug.getCallers(3));
        }
        this.mLayer = paramInt;
      }
      super.setLayer(paramInt);
      synchronized (sSurfaces)
      {
        sSurfaces.remove(this);
        int i = sSurfaces.size() - 1;
        if ((i < 0) || (((SurfaceTrace)sSurfaces.get(i)).mLayer < paramInt))
        {
          sSurfaces.add(i + 1, this);
          return;
        }
        i -= 1;
      }
    }
    
    public void setLayerStack(int paramInt)
    {
      if (paramInt != this.mLayerStack)
      {
        if (LOG_SURFACE_TRACE) {
          Slog.v(SURFACE_TAG, "setLayerStack(" + paramInt + "): OLD:" + this + ". Called by " + Debug.getCallers(3));
        }
        this.mLayerStack = paramInt;
      }
      super.setLayerStack(paramInt);
    }
    
    public void setMatrix(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
    {
      if ((paramFloat1 != this.mDsdx) || (paramFloat2 != this.mDtdx)) {}
      for (;;)
      {
        if (LOG_SURFACE_TRACE) {
          Slog.v(SURFACE_TAG, "setMatrix(" + paramFloat1 + "," + paramFloat2 + "," + paramFloat3 + "," + paramFloat4 + "): OLD:" + this + ". Called by " + Debug.getCallers(3));
        }
        this.mDsdx = paramFloat1;
        this.mDtdx = paramFloat2;
        this.mDsdy = paramFloat3;
        this.mDtdy = paramFloat4;
        do
        {
          super.setMatrix(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
          return;
          if (paramFloat3 != this.mDsdy) {
            break;
          }
        } while (paramFloat4 == this.mDtdy);
      }
    }
    
    public void setOpaque(boolean paramBoolean)
    {
      if (paramBoolean != this.mIsOpaque)
      {
        if (LOG_SURFACE_TRACE) {
          Slog.v(SURFACE_TAG, "setOpaque(" + paramBoolean + "): OLD:" + this + ". Called by " + Debug.getCallers(3));
        }
        this.mIsOpaque = paramBoolean;
      }
      super.setOpaque(paramBoolean);
    }
    
    public void setPosition(float paramFloat1, float paramFloat2)
    {
      if ((paramFloat1 != this.mPosition.x) || (paramFloat2 != this.mPosition.y))
      {
        if (LOG_SURFACE_TRACE) {
          Slog.v(SURFACE_TAG, "setPosition(" + paramFloat1 + "," + paramFloat2 + "): OLD:" + this + ". Called by " + Debug.getCallers(3));
        }
        this.mPosition.set(paramFloat1, paramFloat2);
      }
      super.setPosition(paramFloat1, paramFloat2);
    }
    
    public void setSecure(boolean paramBoolean)
    {
      super.setSecure(paramBoolean);
    }
    
    public void setSize(int paramInt1, int paramInt2)
    {
      if ((paramInt1 != this.mSize.x) || (paramInt2 != this.mSize.y))
      {
        if (LOG_SURFACE_TRACE) {
          Slog.v(SURFACE_TAG, "setSize(" + paramInt1 + "," + paramInt2 + "): OLD:" + this + ". Called by " + Debug.getCallers(3));
        }
        this.mSize.set(paramInt1, paramInt2);
      }
      super.setSize(paramInt1, paramInt2);
    }
    
    public void setTransparentRegionHint(Region paramRegion)
    {
      if (LOG_SURFACE_TRACE) {
        Slog.v(SURFACE_TAG, "setTransparentRegionHint(" + paramRegion + "): OLD: " + this + " . Called by " + Debug.getCallers(3));
      }
      super.setTransparentRegionHint(paramRegion);
    }
    
    public void setWindowCrop(Rect paramRect)
    {
      if ((paramRect != null) && (!paramRect.equals(this.mWindowCrop)))
      {
        if (LOG_SURFACE_TRACE) {
          Slog.v(SURFACE_TAG, "setWindowCrop(" + paramRect.toShortString() + "): OLD:" + this + ". Called by " + Debug.getCallers(3));
        }
        this.mWindowCrop.set(paramRect);
      }
      super.setWindowCrop(paramRect);
    }
    
    public void show()
    {
      if (!this.mShown)
      {
        if (LOG_SURFACE_TRACE) {
          Slog.v(SURFACE_TAG, "show: OLD:" + this + ". Called by " + Debug.getCallers(3));
        }
        this.mShown = true;
      }
      super.show();
    }
    
    public String toString()
    {
      return "Surface " + Integer.toHexString(System.identityHashCode(this)) + " " + this.mName + " (" + this.mLayerStack + "): shown=" + this.mShown + " layer=" + this.mLayer + " alpha=" + this.mSurfaceTraceAlpha + " " + this.mPosition.x + "," + this.mPosition.y + " " + this.mSize.x + "x" + this.mSize.y + " crop=" + this.mWindowCrop.toShortString() + " opaque=" + this.mIsOpaque + " (" + this.mDsdx + "," + this.mDtdx + "," + this.mDsdy + "," + this.mDtdy + ")";
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/WindowSurfaceController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */