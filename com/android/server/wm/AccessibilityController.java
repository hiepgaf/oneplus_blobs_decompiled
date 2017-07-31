package com.android.server.wm;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Region.Op;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Display;
import android.view.IWindow;
import android.view.MagnificationSpec;
import android.view.Surface;
import android.view.Surface.OutOfResourcesException;
import android.view.SurfaceControl;
import android.view.ViewConfiguration;
import android.view.WindowInfo;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManagerInternal.MagnificationCallbacks;
import android.view.WindowManagerInternal.WindowsForAccessibilityCallback;
import android.view.WindowManagerPolicy;
import android.view.animation.DecelerateInterpolator;
import com.android.internal.os.SomeArgs;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class AccessibilityController
{
  private static final float[] sTempFloats = new float[9];
  private DisplayMagnifier mDisplayMagnifier;
  private final WindowManagerService mWindowManagerService;
  private WindowsForAccessibilityObserver mWindowsForAccessibilityObserver;
  
  public AccessibilityController(WindowManagerService paramWindowManagerService)
  {
    this.mWindowManagerService = paramWindowManagerService;
  }
  
  private static void populateTransformationMatrixLocked(WindowState paramWindowState, Matrix paramMatrix)
  {
    sTempFloats[0] = paramWindowState.mWinAnimator.mDsDx;
    sTempFloats[3] = paramWindowState.mWinAnimator.mDtDx;
    sTempFloats[1] = paramWindowState.mWinAnimator.mDsDy;
    sTempFloats[4] = paramWindowState.mWinAnimator.mDtDy;
    sTempFloats[2] = paramWindowState.mShownPosition.x;
    sTempFloats[5] = paramWindowState.mShownPosition.y;
    sTempFloats[6] = 0.0F;
    sTempFloats[7] = 0.0F;
    sTempFloats[8] = 1.0F;
    paramMatrix.setValues(sTempFloats);
  }
  
  public void drawMagnifiedRegionBorderIfNeededLocked()
  {
    if (this.mDisplayMagnifier != null) {
      this.mDisplayMagnifier.drawMagnifiedRegionBorderIfNeededLocked();
    }
  }
  
  public void getMagnificationRegionLocked(Region paramRegion)
  {
    if (this.mDisplayMagnifier != null) {
      this.mDisplayMagnifier.getMagnificationRegionLocked(paramRegion);
    }
  }
  
  public MagnificationSpec getMagnificationSpecForWindowLocked(WindowState paramWindowState)
  {
    if (this.mDisplayMagnifier != null) {
      return this.mDisplayMagnifier.getMagnificationSpecForWindowLocked(paramWindowState);
    }
    return null;
  }
  
  public boolean hasCallbacksLocked()
  {
    return (this.mDisplayMagnifier != null) || (this.mWindowsForAccessibilityObserver != null);
  }
  
  public void onAppWindowTransitionLocked(WindowState paramWindowState, int paramInt)
  {
    if (this.mDisplayMagnifier != null) {
      this.mDisplayMagnifier.onAppWindowTransitionLocked(paramWindowState, paramInt);
    }
  }
  
  public void onRectangleOnScreenRequestedLocked(Rect paramRect)
  {
    if (this.mDisplayMagnifier != null) {
      this.mDisplayMagnifier.onRectangleOnScreenRequestedLocked(paramRect);
    }
  }
  
  public void onRotationChangedLocked(DisplayContent paramDisplayContent, int paramInt)
  {
    if (this.mDisplayMagnifier != null) {
      this.mDisplayMagnifier.onRotationChangedLocked(paramDisplayContent, paramInt);
    }
    if (this.mWindowsForAccessibilityObserver != null) {
      this.mWindowsForAccessibilityObserver.scheduleComputeChangedWindowsLocked();
    }
  }
  
  public void onSomeWindowResizedOrMovedLocked()
  {
    if (this.mWindowsForAccessibilityObserver != null) {
      this.mWindowsForAccessibilityObserver.scheduleComputeChangedWindowsLocked();
    }
  }
  
  public void onWindowFocusChangedNotLocked()
  {
    synchronized (this.mWindowManagerService)
    {
      WindowsForAccessibilityObserver localWindowsForAccessibilityObserver = this.mWindowsForAccessibilityObserver;
      if (localWindowsForAccessibilityObserver != null) {
        localWindowsForAccessibilityObserver.performComputeChangedWindowsNotLocked();
      }
      return;
    }
  }
  
  public void onWindowLayersChangedLocked()
  {
    if (this.mDisplayMagnifier != null) {
      this.mDisplayMagnifier.onWindowLayersChangedLocked();
    }
    if (this.mWindowsForAccessibilityObserver != null) {
      this.mWindowsForAccessibilityObserver.scheduleComputeChangedWindowsLocked();
    }
  }
  
  public void onWindowTransitionLocked(WindowState paramWindowState, int paramInt)
  {
    if (this.mDisplayMagnifier != null) {
      this.mDisplayMagnifier.onWindowTransitionLocked(paramWindowState, paramInt);
    }
    if (this.mWindowsForAccessibilityObserver != null) {
      this.mWindowsForAccessibilityObserver.scheduleComputeChangedWindowsLocked();
    }
  }
  
  public void setMagnificationCallbacksLocked(WindowManagerInternal.MagnificationCallbacks paramMagnificationCallbacks)
  {
    if (paramMagnificationCallbacks != null)
    {
      if (this.mDisplayMagnifier != null) {
        throw new IllegalStateException("Magnification callbacks already set!");
      }
      this.mDisplayMagnifier = new DisplayMagnifier(this.mWindowManagerService, paramMagnificationCallbacks);
      return;
    }
    if (this.mDisplayMagnifier == null) {
      throw new IllegalStateException("Magnification callbacks already cleared!");
    }
    this.mDisplayMagnifier.destroyLocked();
    this.mDisplayMagnifier = null;
  }
  
  public void setMagnificationSpecLocked(MagnificationSpec paramMagnificationSpec)
  {
    if (this.mDisplayMagnifier != null) {
      this.mDisplayMagnifier.setMagnificationSpecLocked(paramMagnificationSpec);
    }
    if (this.mWindowsForAccessibilityObserver != null) {
      this.mWindowsForAccessibilityObserver.scheduleComputeChangedWindowsLocked();
    }
  }
  
  public void setWindowsForAccessibilityCallback(WindowManagerInternal.WindowsForAccessibilityCallback paramWindowsForAccessibilityCallback)
  {
    if (paramWindowsForAccessibilityCallback != null)
    {
      if (this.mWindowsForAccessibilityObserver != null) {
        throw new IllegalStateException("Windows for accessibility callback already set!");
      }
      this.mWindowsForAccessibilityObserver = new WindowsForAccessibilityObserver(this.mWindowManagerService, paramWindowsForAccessibilityCallback);
      return;
    }
    if (this.mWindowsForAccessibilityObserver == null) {
      throw new IllegalStateException("Windows for accessibility callback already cleared!");
    }
    this.mWindowsForAccessibilityObserver = null;
  }
  
  private static final class DisplayMagnifier
  {
    private static final boolean DEBUG_LAYERS = false;
    private static final boolean DEBUG_RECTANGLE_REQUESTED = false;
    private static final boolean DEBUG_ROTATION = false;
    private static final boolean DEBUG_VIEWPORT_WINDOW = false;
    private static final boolean DEBUG_WINDOW_TRANSITIONS = false;
    private static final String LOG_TAG = "WindowManager";
    private final WindowManagerInternal.MagnificationCallbacks mCallbacks;
    private final Context mContext;
    private final Handler mHandler;
    private final long mLongAnimationDuration;
    private final MagnifiedViewport mMagnifedViewport;
    private final Rect mTempRect1 = new Rect();
    private final Rect mTempRect2 = new Rect();
    private final Region mTempRegion1 = new Region();
    private final Region mTempRegion2 = new Region();
    private final Region mTempRegion3 = new Region();
    private final Region mTempRegion4 = new Region();
    private final WindowManagerService mWindowManagerService;
    
    public DisplayMagnifier(WindowManagerService paramWindowManagerService, WindowManagerInternal.MagnificationCallbacks paramMagnificationCallbacks)
    {
      this.mContext = paramWindowManagerService.mContext;
      this.mWindowManagerService = paramWindowManagerService;
      this.mCallbacks = paramMagnificationCallbacks;
      this.mHandler = new MyHandler(this.mWindowManagerService.mH.getLooper());
      this.mMagnifedViewport = new MagnifiedViewport();
      this.mLongAnimationDuration = this.mContext.getResources().getInteger(17694722);
    }
    
    public void destroyLocked()
    {
      this.mMagnifedViewport.destroyWindow();
    }
    
    public void drawMagnifiedRegionBorderIfNeededLocked()
    {
      this.mMagnifedViewport.drawWindowIfNeededLocked();
    }
    
    public void getMagnificationRegionLocked(Region paramRegion)
    {
      this.mMagnifedViewport.getMagnificationRegionLocked(paramRegion);
    }
    
    public MagnificationSpec getMagnificationSpecForWindowLocked(WindowState paramWindowState)
    {
      MagnificationSpec localMagnificationSpec = this.mMagnifedViewport.getMagnificationSpecLocked();
      if ((localMagnificationSpec == null) || (localMagnificationSpec.isNop())) {}
      WindowManagerPolicy localWindowManagerPolicy;
      do
      {
        return localMagnificationSpec;
        localWindowManagerPolicy = this.mWindowManagerService.mPolicy;
        int i = paramWindowState.mAttrs.type;
        if ((!localWindowManagerPolicy.isTopLevelWindow(i)) && (paramWindowState.mAttachedWindow != null) && (!localWindowManagerPolicy.canMagnifyWindow(i))) {
          break;
        }
      } while (localWindowManagerPolicy.canMagnifyWindow(paramWindowState.mAttrs.type));
      return null;
      return null;
    }
    
    public void onAppWindowTransitionLocked(WindowState paramWindowState, int paramInt)
    {
      if (this.mMagnifedViewport.isMagnifyingLocked()) {}
      switch (paramInt)
      {
      case 7: 
      case 9: 
      case 11: 
      default: 
        return;
      }
      this.mHandler.sendEmptyMessage(3);
    }
    
    public void onRectangleOnScreenRequestedLocked(Rect paramRect)
    {
      if (!this.mMagnifedViewport.isMagnifyingLocked()) {
        return;
      }
      Object localObject = this.mTempRect2;
      this.mMagnifedViewport.getMagnifiedFrameInContentCoordsLocked((Rect)localObject);
      if (((Rect)localObject).contains(paramRect)) {
        return;
      }
      localObject = SomeArgs.obtain();
      ((SomeArgs)localObject).argi1 = paramRect.left;
      ((SomeArgs)localObject).argi2 = paramRect.top;
      ((SomeArgs)localObject).argi3 = paramRect.right;
      ((SomeArgs)localObject).argi4 = paramRect.bottom;
      this.mHandler.obtainMessage(2, localObject).sendToTarget();
    }
    
    public void onRotationChangedLocked(DisplayContent paramDisplayContent, int paramInt)
    {
      this.mMagnifedViewport.onRotationChangedLocked();
      this.mHandler.sendEmptyMessage(4);
    }
    
    public void onWindowLayersChangedLocked()
    {
      this.mMagnifedViewport.recomputeBoundsLocked();
      this.mWindowManagerService.scheduleAnimationLocked();
    }
    
    public void onWindowTransitionLocked(WindowState paramWindowState, int paramInt)
    {
      boolean bool = this.mMagnifedViewport.isMagnifyingLocked();
      int i = paramWindowState.mAttrs.type;
      switch (paramInt)
      {
      }
      Rect localRect1;
      Rect localRect2;
      do
      {
        do
        {
          return;
        } while (!bool);
        switch (i)
        {
        default: 
          return;
        }
        localRect1 = this.mTempRect2;
        this.mMagnifedViewport.getMagnifiedFrameInContentCoordsLocked(localRect1);
        localRect2 = this.mTempRect1;
        paramWindowState.getTouchableRegion(this.mTempRegion1);
        this.mTempRegion1.getBounds(localRect2);
      } while (localRect1.intersect(localRect2));
      this.mCallbacks.onRectangleOnScreenRequested(localRect2.left, localRect2.top, localRect2.right, localRect2.bottom);
    }
    
    public void setMagnificationSpecLocked(MagnificationSpec paramMagnificationSpec)
    {
      this.mMagnifedViewport.updateMagnificationSpecLocked(paramMagnificationSpec);
      this.mMagnifedViewport.recomputeBoundsLocked();
      this.mWindowManagerService.scheduleAnimationLocked();
    }
    
    private final class MagnifiedViewport
    {
      private final float mBorderWidth = AccessibilityController.DisplayMagnifier.-get1(AccessibilityController.DisplayMagnifier.this).getResources().getDimension(17105040);
      private final Path mCircularPath;
      private final int mDrawBorderInset = (int)this.mBorderWidth / 2;
      private boolean mFullRedrawNeeded;
      private final int mHalfBorderWidth = (int)Math.ceil(this.mBorderWidth / 2.0F);
      private final Region mMagnificationRegion = new Region();
      private final MagnificationSpec mMagnificationSpec = MagnificationSpec.obtain();
      private final Region mOldMagnificationRegion = new Region();
      private final Matrix mTempMatrix = new Matrix();
      private final Point mTempPoint = new Point();
      private final RectF mTempRectF = new RectF();
      private final SparseArray<WindowState> mTempWindowStates = new SparseArray();
      private final ViewportWindow mWindow = new ViewportWindow(AccessibilityController.DisplayMagnifier.-get1(AccessibilityController.DisplayMagnifier.this));
      private final WindowManager mWindowManager = (WindowManager)AccessibilityController.DisplayMagnifier.-get1(AccessibilityController.DisplayMagnifier.this).getSystemService("window");
      
      public MagnifiedViewport()
      {
        if (AccessibilityController.DisplayMagnifier.-get1(AccessibilityController.DisplayMagnifier.this).getResources().getConfiguration().isScreenRound())
        {
          this.mCircularPath = new Path();
          this.mWindowManager.getDefaultDisplay().getRealSize(this.mTempPoint);
          int i = this.mTempPoint.x / 2;
          this.mCircularPath.addCircle(i, i, i, Path.Direction.CW);
        }
        for (;;)
        {
          recomputeBoundsLocked();
          return;
          this.mCircularPath = null;
        }
      }
      
      private void populateWindowsOnScreenLocked(SparseArray<WindowState> paramSparseArray)
      {
        WindowList localWindowList = AccessibilityController.DisplayMagnifier.-get10(AccessibilityController.DisplayMagnifier.this).getDefaultDisplayContentLocked().getWindowList();
        int j = localWindowList.size();
        int i = 0;
        if (i < j)
        {
          WindowState localWindowState = (WindowState)localWindowList.get(i);
          if ((!localWindowState.isOnScreen()) || (!localWindowState.isVisibleLw()) || (localWindowState.mWinAnimator.mEnterAnimationPending)) {}
          for (;;)
          {
            i += 1;
            break;
            paramSparseArray.put(localWindowState.mLayer, localWindowState);
          }
        }
      }
      
      public void destroyWindow()
      {
        this.mWindow.releaseSurface();
      }
      
      public void drawWindowIfNeededLocked()
      {
        recomputeBoundsLocked();
        this.mWindow.drawIfNeeded();
      }
      
      public void getMagnificationRegionLocked(Region paramRegion)
      {
        paramRegion.set(this.mMagnificationRegion);
      }
      
      public MagnificationSpec getMagnificationSpecLocked()
      {
        return this.mMagnificationSpec;
      }
      
      public void getMagnifiedFrameInContentCoordsLocked(Rect paramRect)
      {
        MagnificationSpec localMagnificationSpec = this.mMagnificationSpec;
        this.mMagnificationRegion.getBounds(paramRect);
        paramRect.offset((int)-localMagnificationSpec.offsetX, (int)-localMagnificationSpec.offsetY);
        paramRect.scale(1.0F / localMagnificationSpec.scale);
      }
      
      public boolean isMagnifyingLocked()
      {
        return this.mMagnificationSpec.scale > 1.0F;
      }
      
      public void onRotationChangedLocked()
      {
        if (isMagnifyingLocked())
        {
          setMagnifiedRegionBorderShownLocked(false, false);
          long l = ((float)AccessibilityController.DisplayMagnifier.-get3(AccessibilityController.DisplayMagnifier.this) * AccessibilityController.DisplayMagnifier.-get10(AccessibilityController.DisplayMagnifier.this).getWindowAnimationScaleLocked());
          Message localMessage = AccessibilityController.DisplayMagnifier.-get2(AccessibilityController.DisplayMagnifier.this).obtainMessage(5);
          AccessibilityController.DisplayMagnifier.-get2(AccessibilityController.DisplayMagnifier.this).sendMessageDelayed(localMessage, l);
        }
        recomputeBoundsLocked();
        this.mWindow.updateSize();
      }
      
      public void recomputeBoundsLocked()
      {
        this.mWindowManager.getDefaultDisplay().getRealSize(this.mTempPoint);
        int j = this.mTempPoint.x;
        int k = this.mTempPoint.y;
        this.mMagnificationRegion.set(0, 0, 0, 0);
        Object localObject2 = AccessibilityController.DisplayMagnifier.-get6(AccessibilityController.DisplayMagnifier.this);
        ((Region)localObject2).set(0, 0, j, k);
        if (this.mCircularPath != null) {
          ((Region)localObject2).setPath(this.mCircularPath, (Region)localObject2);
        }
        Object localObject1 = AccessibilityController.DisplayMagnifier.-get9(AccessibilityController.DisplayMagnifier.this);
        ((Region)localObject1).set(0, 0, 0, 0);
        Object localObject3 = this.mTempWindowStates;
        ((SparseArray)localObject3).clear();
        populateWindowsOnScreenLocked((SparseArray)localObject3);
        int i = ((SparseArray)localObject3).size() - 1;
        Object localObject5;
        if (i >= 0)
        {
          Object localObject4 = (WindowState)((SparseArray)localObject3).valueAt(i);
          if (((WindowState)localObject4).mAttrs.type == 2027) {}
          do
          {
            do
            {
              i -= 1;
              break;
              localObject5 = this.mTempMatrix;
              AccessibilityController.-wrap0((WindowState)localObject4, (Matrix)localObject5);
              Object localObject7 = AccessibilityController.DisplayMagnifier.-get8(AccessibilityController.DisplayMagnifier.this);
              ((WindowState)localObject4).getTouchableRegion((Region)localObject7);
              Object localObject6 = AccessibilityController.DisplayMagnifier.-get5(AccessibilityController.DisplayMagnifier.this);
              ((Region)localObject7).getBounds((Rect)localObject6);
              localObject7 = this.mTempRectF;
              ((RectF)localObject7).set((Rect)localObject6);
              ((RectF)localObject7).offset(-((WindowState)localObject4).mFrame.left, -((WindowState)localObject4).mFrame.top);
              ((Matrix)localObject5).mapRect((RectF)localObject7);
              localObject5 = AccessibilityController.DisplayMagnifier.-get7(AccessibilityController.DisplayMagnifier.this);
              ((Region)localObject5).set((int)((RectF)localObject7).left, (int)((RectF)localObject7).top, (int)((RectF)localObject7).right, (int)((RectF)localObject7).bottom);
              localObject6 = AccessibilityController.DisplayMagnifier.-get8(AccessibilityController.DisplayMagnifier.this);
              ((Region)localObject6).set(this.mMagnificationRegion);
              ((Region)localObject6).op((Region)localObject1, Region.Op.UNION);
              ((Region)localObject5).op((Region)localObject6, Region.Op.DIFFERENCE);
              if (!AccessibilityController.DisplayMagnifier.-get10(AccessibilityController.DisplayMagnifier.this).mPolicy.canMagnifyWindow(((WindowState)localObject4).mAttrs.type)) {
                break label640;
              }
              this.mMagnificationRegion.op((Region)localObject5, Region.Op.UNION);
              this.mMagnificationRegion.op((Region)localObject2, Region.Op.INTERSECT);
              localObject4 = AccessibilityController.DisplayMagnifier.-get7(AccessibilityController.DisplayMagnifier.this);
              ((Region)localObject4).set(this.mMagnificationRegion);
              ((Region)localObject4).op((Region)localObject1, Region.Op.UNION);
              ((Region)localObject4).op(0, 0, j, k, Region.Op.INTERSECT);
            } while (!((Region)localObject4).isRect());
            localObject5 = AccessibilityController.DisplayMagnifier.-get5(AccessibilityController.DisplayMagnifier.this);
            ((Region)localObject4).getBounds((Rect)localObject5);
          } while ((((Rect)localObject5).width() != j) || (((Rect)localObject5).height() != k));
        }
        ((SparseArray)localObject3).clear();
        this.mMagnificationRegion.op(this.mDrawBorderInset, this.mDrawBorderInset, j - this.mDrawBorderInset, k - this.mDrawBorderInset, Region.Op.INTERSECT);
        if (this.mOldMagnificationRegion.equals(this.mMagnificationRegion))
        {
          i = 0;
          label524:
          if (i != 0)
          {
            this.mWindow.setBounds(this.mMagnificationRegion);
            localObject2 = AccessibilityController.DisplayMagnifier.-get5(AccessibilityController.DisplayMagnifier.this);
            if (!this.mFullRedrawNeeded) {
              break label670;
            }
            this.mFullRedrawNeeded = false;
            ((Rect)localObject2).set(this.mDrawBorderInset, this.mDrawBorderInset, j - this.mDrawBorderInset, k - this.mDrawBorderInset);
            this.mWindow.invalidate((Rect)localObject2);
          }
        }
        for (;;)
        {
          this.mOldMagnificationRegion.set(this.mMagnificationRegion);
          localObject1 = SomeArgs.obtain();
          ((SomeArgs)localObject1).arg1 = Region.obtain(this.mMagnificationRegion);
          AccessibilityController.DisplayMagnifier.-get2(AccessibilityController.DisplayMagnifier.this).obtainMessage(1, localObject1).sendToTarget();
          return;
          label640:
          ((Region)localObject1).op((Region)localObject5, Region.Op.UNION);
          ((Region)localObject2).op((Region)localObject5, Region.Op.DIFFERENCE);
          break;
          i = 1;
          break label524;
          label670:
          localObject3 = AccessibilityController.DisplayMagnifier.-get8(AccessibilityController.DisplayMagnifier.this);
          ((Region)localObject3).set(this.mMagnificationRegion);
          ((Region)localObject3).op(this.mOldMagnificationRegion, Region.Op.UNION);
          ((Region)localObject3).op((Region)localObject1, Region.Op.INTERSECT);
          ((Region)localObject3).getBounds((Rect)localObject2);
          this.mWindow.invalidate((Rect)localObject2);
        }
      }
      
      public void setMagnifiedRegionBorderShownLocked(boolean paramBoolean1, boolean paramBoolean2)
      {
        if (paramBoolean1)
        {
          this.mFullRedrawNeeded = true;
          this.mOldMagnificationRegion.set(0, 0, 0, 0);
        }
        this.mWindow.setShown(paramBoolean1, paramBoolean2);
      }
      
      public void updateMagnificationSpecLocked(MagnificationSpec paramMagnificationSpec)
      {
        if (paramMagnificationSpec != null) {
          this.mMagnificationSpec.initialize(paramMagnificationSpec.scale, paramMagnificationSpec.offsetX, paramMagnificationSpec.offsetY);
        }
        for (;;)
        {
          if (!AccessibilityController.DisplayMagnifier.-get2(AccessibilityController.DisplayMagnifier.this).hasMessages(5)) {
            setMagnifiedRegionBorderShownLocked(isMagnifyingLocked(), true);
          }
          return;
          this.mMagnificationSpec.clear();
        }
      }
      
      private final class ViewportWindow
      {
        private static final String SURFACE_TITLE = "Magnification Overlay";
        private int mAlpha;
        private final AnimationController mAnimationController;
        private final Region mBounds = new Region();
        private final Rect mDirtyRect = new Rect();
        private boolean mInvalidated;
        private final Paint mPaint = new Paint();
        private boolean mShown;
        private final Surface mSurface = new Surface();
        private final SurfaceControl mSurfaceControl;
        
        public ViewportWindow(Context paramContext)
        {
          try
          {
            AccessibilityController.DisplayMagnifier.MagnifiedViewport.-get3(AccessibilityController.DisplayMagnifier.MagnifiedViewport.this).getDefaultDisplay().getRealSize(AccessibilityController.DisplayMagnifier.MagnifiedViewport.-get2(AccessibilityController.DisplayMagnifier.MagnifiedViewport.this));
            Object localObject1 = new SurfaceControl(AccessibilityController.DisplayMagnifier.-get10(AccessibilityController.DisplayMagnifier.this).mFxSession, "Magnification Overlay", AccessibilityController.DisplayMagnifier.MagnifiedViewport.-get2(AccessibilityController.DisplayMagnifier.MagnifiedViewport.this).x, AccessibilityController.DisplayMagnifier.MagnifiedViewport.-get2(AccessibilityController.DisplayMagnifier.MagnifiedViewport.this).y, -3, 4);
            this.mSurfaceControl = ((SurfaceControl)localObject1);
            this.mSurfaceControl.setLayerStack(AccessibilityController.DisplayMagnifier.MagnifiedViewport.-get3(AccessibilityController.DisplayMagnifier.MagnifiedViewport.this).getDefaultDisplay().getLayerStack());
            this.mSurfaceControl.setLayer(AccessibilityController.DisplayMagnifier.-get10(AccessibilityController.DisplayMagnifier.this).mPolicy.windowTypeToLayerLw(2027) * 10000);
            this.mSurfaceControl.setPosition(0.0F, 0.0F);
            this.mSurface.copyFrom(this.mSurfaceControl);
            this.mAnimationController = new AnimationController(paramContext, AccessibilityController.DisplayMagnifier.-get10(AccessibilityController.DisplayMagnifier.this).mH.getLooper());
            localObject1 = new TypedValue();
            paramContext.getTheme().resolveAttribute(16843664, (TypedValue)localObject1, true);
            int i = paramContext.getColor(((TypedValue)localObject1).resourceId);
            this.mPaint.setStyle(Paint.Style.STROKE);
            this.mPaint.setStrokeWidth(AccessibilityController.DisplayMagnifier.MagnifiedViewport.-get0(AccessibilityController.DisplayMagnifier.MagnifiedViewport.this));
            this.mPaint.setColor(i);
            this.mInvalidated = true;
            return;
          }
          catch (Surface.OutOfResourcesException localOutOfResourcesException)
          {
            for (;;)
            {
              Object localObject2 = null;
            }
          }
        }
        
        public void drawIfNeeded()
        {
          synchronized (AccessibilityController.DisplayMagnifier.-get10(AccessibilityController.DisplayMagnifier.this).mWindowMap)
          {
            boolean bool = this.mInvalidated;
            if (!bool) {
              return;
            }
            this.mInvalidated = false;
            Object localObject1 = null;
            try
            {
              if (this.mDirtyRect.isEmpty()) {
                this.mBounds.getBounds(this.mDirtyRect);
              }
              this.mDirtyRect.inset(-AccessibilityController.DisplayMagnifier.MagnifiedViewport.-get1(AccessibilityController.DisplayMagnifier.MagnifiedViewport.this), -AccessibilityController.DisplayMagnifier.MagnifiedViewport.-get1(AccessibilityController.DisplayMagnifier.MagnifiedViewport.this));
              Canvas localCanvas = this.mSurface.lockCanvas(this.mDirtyRect);
              localObject1 = localCanvas;
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
            ((Canvas)localObject1).drawColor(0, PorterDuff.Mode.CLEAR);
            this.mPaint.setAlpha(this.mAlpha);
            ((Canvas)localObject1).drawPath(this.mBounds.getBoundaryPath(), this.mPaint);
            this.mSurface.unlockCanvasAndPost((Canvas)localObject1);
            if (this.mAlpha > 0)
            {
              this.mSurfaceControl.show();
              return;
            }
            this.mSurfaceControl.hide();
          }
        }
        
        public int getAlpha()
        {
          synchronized (AccessibilityController.DisplayMagnifier.-get10(AccessibilityController.DisplayMagnifier.this).mWindowMap)
          {
            int i = this.mAlpha;
            return i;
          }
        }
        
        public void invalidate(Rect paramRect)
        {
          if (paramRect != null) {
            this.mDirtyRect.set(paramRect);
          }
          for (;;)
          {
            this.mInvalidated = true;
            AccessibilityController.DisplayMagnifier.-get10(AccessibilityController.DisplayMagnifier.this).scheduleAnimationLocked();
            return;
            this.mDirtyRect.setEmpty();
          }
        }
        
        public void releaseSurface()
        {
          this.mSurfaceControl.release();
          this.mSurface.release();
        }
        
        public void setAlpha(int paramInt)
        {
          synchronized (AccessibilityController.DisplayMagnifier.-get10(AccessibilityController.DisplayMagnifier.this).mWindowMap)
          {
            int i = this.mAlpha;
            if (i == paramInt) {
              return;
            }
            this.mAlpha = paramInt;
            invalidate(null);
            return;
          }
        }
        
        public void setBounds(Region paramRegion)
        {
          synchronized (AccessibilityController.DisplayMagnifier.-get10(AccessibilityController.DisplayMagnifier.this).mWindowMap)
          {
            boolean bool = this.mBounds.equals(paramRegion);
            if (bool) {
              return;
            }
            this.mBounds.set(paramRegion);
            invalidate(this.mDirtyRect);
            return;
          }
        }
        
        public void setShown(boolean paramBoolean1, boolean paramBoolean2)
        {
          synchronized (AccessibilityController.DisplayMagnifier.-get10(AccessibilityController.DisplayMagnifier.this).mWindowMap)
          {
            boolean bool = this.mShown;
            if (bool == paramBoolean1) {
              return;
            }
            this.mShown = paramBoolean1;
            this.mAnimationController.onFrameShownStateChanged(paramBoolean1, paramBoolean2);
            return;
          }
        }
        
        public void updateSize()
        {
          synchronized (AccessibilityController.DisplayMagnifier.-get10(AccessibilityController.DisplayMagnifier.this).mWindowMap)
          {
            AccessibilityController.DisplayMagnifier.MagnifiedViewport.-get3(AccessibilityController.DisplayMagnifier.MagnifiedViewport.this).getDefaultDisplay().getRealSize(AccessibilityController.DisplayMagnifier.MagnifiedViewport.-get2(AccessibilityController.DisplayMagnifier.MagnifiedViewport.this));
            this.mSurfaceControl.setSize(AccessibilityController.DisplayMagnifier.MagnifiedViewport.-get2(AccessibilityController.DisplayMagnifier.MagnifiedViewport.this).x, AccessibilityController.DisplayMagnifier.MagnifiedViewport.-get2(AccessibilityController.DisplayMagnifier.MagnifiedViewport.this).y);
            invalidate(this.mDirtyRect);
            return;
          }
        }
        
        private final class AnimationController
          extends Handler
        {
          private static final int MAX_ALPHA = 255;
          private static final int MIN_ALPHA = 0;
          private static final int MSG_FRAME_SHOWN_STATE_CHANGED = 1;
          private static final String PROPERTY_NAME_ALPHA = "alpha";
          private final ValueAnimator mShowHideFrameAnimator = ObjectAnimator.ofInt(AccessibilityController.DisplayMagnifier.MagnifiedViewport.ViewportWindow.this, "alpha", new int[] { 0, 255 });
          
          public AnimationController(Context paramContext, Looper paramLooper)
          {
            super();
            this$1 = new DecelerateInterpolator(2.5F);
            long l = paramContext.getResources().getInteger(17694722);
            this.mShowHideFrameAnimator.setInterpolator(AccessibilityController.DisplayMagnifier.MagnifiedViewport.ViewportWindow.this);
            this.mShowHideFrameAnimator.setDuration(l);
          }
          
          public void handleMessage(Message paramMessage)
          {
            int j = 1;
            switch (paramMessage.what)
            {
            default: 
              return;
            }
            int i;
            if (paramMessage.arg1 == 1)
            {
              i = 1;
              if (paramMessage.arg2 != 1) {
                break label70;
              }
            }
            for (;;)
            {
              if (j != 0)
              {
                if (this.mShowHideFrameAnimator.isRunning())
                {
                  this.mShowHideFrameAnimator.reverse();
                  return;
                  i = 0;
                  break;
                  label70:
                  j = 0;
                  continue;
                }
                if (i != 0)
                {
                  this.mShowHideFrameAnimator.start();
                  return;
                }
                this.mShowHideFrameAnimator.reverse();
                return;
              }
            }
            this.mShowHideFrameAnimator.cancel();
            if (i != 0)
            {
              AccessibilityController.DisplayMagnifier.MagnifiedViewport.ViewportWindow.this.setAlpha(255);
              return;
            }
            AccessibilityController.DisplayMagnifier.MagnifiedViewport.ViewportWindow.this.setAlpha(0);
          }
          
          public void onFrameShownStateChanged(boolean paramBoolean1, boolean paramBoolean2)
          {
            int j = 0;
            if (paramBoolean1) {}
            for (int i = 1;; i = 0)
            {
              if (paramBoolean2) {
                j = 1;
              }
              obtainMessage(1, i, j).sendToTarget();
              return;
            }
          }
        }
      }
    }
    
    private class MyHandler
      extends Handler
    {
      public static final int MESSAGE_NOTIFY_MAGNIFICATION_REGION_CHANGED = 1;
      public static final int MESSAGE_NOTIFY_RECTANGLE_ON_SCREEN_REQUESTED = 2;
      public static final int MESSAGE_NOTIFY_ROTATION_CHANGED = 4;
      public static final int MESSAGE_NOTIFY_USER_CONTEXT_CHANGED = 3;
      public static final int MESSAGE_SHOW_MAGNIFIED_REGION_BOUNDS_IF_NEEDED = 5;
      
      public MyHandler(Looper paramLooper)
      {
        super();
      }
      
      public void handleMessage(Message arg1)
      {
        int i;
        switch (???.what)
        {
        default: 
          return;
        case 1: 
          ??? = (Region)((SomeArgs)???.obj).arg1;
          AccessibilityController.DisplayMagnifier.-get0(AccessibilityController.DisplayMagnifier.this).onMagnificationRegionChanged(???);
          ???.recycle();
          return;
        case 2: 
          ??? = (SomeArgs)???.obj;
          i = ???.argi1;
          int j = ???.argi2;
          int k = ???.argi3;
          int m = ???.argi4;
          AccessibilityController.DisplayMagnifier.-get0(AccessibilityController.DisplayMagnifier.this).onRectangleOnScreenRequested(i, j, k, m);
          ???.recycle();
          return;
        case 3: 
          AccessibilityController.DisplayMagnifier.-get0(AccessibilityController.DisplayMagnifier.this).onUserContextChanged();
          return;
        case 4: 
          i = ???.arg1;
          AccessibilityController.DisplayMagnifier.-get0(AccessibilityController.DisplayMagnifier.this).onRotationChanged(i);
          return;
        }
        synchronized (AccessibilityController.DisplayMagnifier.-get10(AccessibilityController.DisplayMagnifier.this).mWindowMap)
        {
          if (AccessibilityController.DisplayMagnifier.-get4(AccessibilityController.DisplayMagnifier.this).isMagnifyingLocked())
          {
            AccessibilityController.DisplayMagnifier.-get4(AccessibilityController.DisplayMagnifier.this).setMagnifiedRegionBorderShownLocked(true, true);
            AccessibilityController.DisplayMagnifier.-get10(AccessibilityController.DisplayMagnifier.this).scheduleAnimationLocked();
          }
          return;
        }
      }
    }
  }
  
  private static final class WindowsForAccessibilityObserver
  {
    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "WindowManager";
    private final WindowManagerInternal.WindowsForAccessibilityCallback mCallback;
    private final Context mContext;
    private final Handler mHandler;
    private final List<WindowInfo> mOldWindows = new ArrayList();
    private final long mRecurringAccessibilityEventsIntervalMillis;
    private final Set<IBinder> mTempBinderSet = new ArraySet();
    private final Matrix mTempMatrix = new Matrix();
    private final Point mTempPoint = new Point();
    private final Rect mTempRect = new Rect();
    private final RectF mTempRectF = new RectF();
    private final Region mTempRegion = new Region();
    private final Region mTempRegion1 = new Region();
    private final SparseArray<WindowState> mTempWindowStates = new SparseArray();
    private final WindowManagerService mWindowManagerService;
    
    public WindowsForAccessibilityObserver(WindowManagerService paramWindowManagerService, WindowManagerInternal.WindowsForAccessibilityCallback paramWindowsForAccessibilityCallback)
    {
      this.mContext = paramWindowManagerService.mContext;
      this.mWindowManagerService = paramWindowManagerService;
      this.mCallback = paramWindowsForAccessibilityCallback;
      this.mHandler = new MyHandler(this.mWindowManagerService.mH.getLooper());
      this.mRecurringAccessibilityEventsIntervalMillis = ViewConfiguration.getSendRecurringAccessibilityEventsInterval();
      computeChangedWindows();
    }
    
    private void cacheWindows(List<WindowInfo> paramList)
    {
      int i = this.mOldWindows.size() - 1;
      while (i >= 0)
      {
        ((WindowInfo)this.mOldWindows.remove(i)).recycle();
        i -= 1;
      }
      int j = paramList.size();
      i = 0;
      while (i < j)
      {
        WindowInfo localWindowInfo = (WindowInfo)paramList.get(i);
        this.mOldWindows.add(WindowInfo.obtain(localWindowInfo));
        i += 1;
      }
    }
    
    private static void clearAndRecycleWindows(List<WindowInfo> paramList)
    {
      int i = paramList.size() - 1;
      while (i >= 0)
      {
        ((WindowInfo)paramList.remove(i)).recycle();
        i -= 1;
      }
    }
    
    private void computeWindowBoundsInScreen(WindowState paramWindowState, Rect paramRect)
    {
      Object localObject2 = this.mTempRegion1;
      paramWindowState.getTouchableRegion((Region)localObject2);
      Object localObject1 = this.mTempRect;
      ((Region)localObject2).getBounds((Rect)localObject1);
      localObject2 = this.mTempRectF;
      ((RectF)localObject2).set((Rect)localObject1);
      ((RectF)localObject2).offset(-paramWindowState.mFrame.left, -paramWindowState.mFrame.top);
      localObject1 = this.mTempMatrix;
      AccessibilityController.-wrap0(paramWindowState, (Matrix)localObject1);
      ((Matrix)localObject1).mapRect((RectF)localObject2);
      paramRect.set((int)((RectF)localObject2).left, (int)((RectF)localObject2).top, (int)((RectF)localObject2).right, (int)((RectF)localObject2).bottom);
    }
    
    private static boolean isReportedWindowType(int paramInt)
    {
      boolean bool2 = false;
      boolean bool1 = bool2;
      if (paramInt != 2029)
      {
        bool1 = bool2;
        if (paramInt != 2013)
        {
          bool1 = bool2;
          if (paramInt != 2021)
          {
            bool1 = bool2;
            if (paramInt != 2026)
            {
              bool1 = bool2;
              if (paramInt != 2016)
              {
                bool1 = bool2;
                if (paramInt != 2022)
                {
                  bool1 = bool2;
                  if (paramInt != 2018)
                  {
                    bool1 = bool2;
                    if (paramInt != 2027)
                    {
                      bool1 = bool2;
                      if (paramInt != 1004)
                      {
                        bool1 = bool2;
                        if (paramInt != 2015)
                        {
                          bool1 = bool2;
                          if (paramInt != 2030) {
                            bool1 = true;
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
      return bool1;
    }
    
    private static WindowInfo obtainPopulatedWindowInfo(WindowState paramWindowState, Rect paramRect)
    {
      WindowInfo localWindowInfo = WindowInfo.obtain();
      localWindowInfo.type = paramWindowState.mAttrs.type;
      localWindowInfo.layer = paramWindowState.mLayer;
      localWindowInfo.token = paramWindowState.mClient.asBinder();
      localWindowInfo.title = paramWindowState.mAttrs.accessibilityTitle;
      localWindowInfo.accessibilityIdOfAnchor = paramWindowState.mAttrs.accessibilityIdOfAnchor;
      WindowState localWindowState = paramWindowState.mAttachedWindow;
      if (localWindowState != null) {
        localWindowInfo.parentToken = localWindowState.mClient.asBinder();
      }
      localWindowInfo.focused = paramWindowState.isFocused();
      localWindowInfo.boundsInScreen.set(paramRect);
      int j = paramWindowState.mChildWindows.size();
      if (j > 0)
      {
        if (localWindowInfo.childTokens == null) {
          localWindowInfo.childTokens = new ArrayList();
        }
        int i = 0;
        while (i < j)
        {
          paramRect = (WindowState)paramWindowState.mChildWindows.get(i);
          localWindowInfo.childTokens.add(paramRect.mClient.asBinder());
          i += 1;
        }
      }
      return localWindowInfo;
    }
    
    private void populateVisibleWindowsOnScreenLocked(SparseArray<WindowState> paramSparseArray)
    {
      WindowList localWindowList = this.mWindowManagerService.getDefaultDisplayContentLocked().getWindowList();
      int j = localWindowList.size();
      int i = 0;
      while (i < j)
      {
        WindowState localWindowState = (WindowState)localWindowList.get(i);
        if (localWindowState.isVisibleLw()) {
          paramSparseArray.put(localWindowState.mLayer, localWindowState);
        }
        i += 1;
      }
    }
    
    private boolean windowChangedNoLayer(WindowInfo paramWindowInfo1, WindowInfo paramWindowInfo2)
    {
      if (paramWindowInfo1 == paramWindowInfo2) {
        return false;
      }
      if (paramWindowInfo1 == null) {
        return true;
      }
      if (paramWindowInfo2 == null) {
        return true;
      }
      if (paramWindowInfo1.type != paramWindowInfo2.type) {
        return true;
      }
      if (paramWindowInfo1.focused != paramWindowInfo2.focused) {
        return true;
      }
      if (paramWindowInfo1.token == null)
      {
        if (paramWindowInfo2.token != null) {
          return true;
        }
      }
      else if (!paramWindowInfo1.token.equals(paramWindowInfo2.token)) {
        return true;
      }
      if (paramWindowInfo1.parentToken == null)
      {
        if (paramWindowInfo2.parentToken != null) {
          return true;
        }
      }
      else if (!paramWindowInfo1.parentToken.equals(paramWindowInfo2.parentToken)) {
        return true;
      }
      if (!paramWindowInfo1.boundsInScreen.equals(paramWindowInfo2.boundsInScreen)) {
        return true;
      }
      if ((paramWindowInfo1.childTokens == null) || (paramWindowInfo2.childTokens == null) || (paramWindowInfo1.childTokens.equals(paramWindowInfo2.childTokens)))
      {
        if (!TextUtils.equals(paramWindowInfo1.title, paramWindowInfo2.title)) {
          return true;
        }
      }
      else {
        return true;
      }
      return paramWindowInfo1.accessibilityIdOfAnchor != paramWindowInfo2.accessibilityIdOfAnchor;
    }
    
    public void computeChangedWindows()
    {
      int n = 0;
      ArrayList localArrayList = new ArrayList();
      Object localObject1;
      int i;
      int j;
      Object localObject2;
      Set localSet;
      int i1;
      Object localObject3;
      int k;
      synchronized (this.mWindowManagerService.mWindowMap)
      {
        localObject1 = this.mWindowManagerService.mCurrentFocus;
        if (localObject1 == null) {
          return;
        }
        ((WindowManager)this.mContext.getSystemService("window")).getDefaultDisplay().getRealSize(this.mTempPoint);
        i = this.mTempPoint.x;
        j = this.mTempPoint.y;
        localObject2 = this.mTempRegion;
        ((Region)localObject2).set(0, 0, i, j);
        localObject1 = this.mTempWindowStates;
        populateVisibleWindowsOnScreenLocked((SparseArray)localObject1);
        localSet = this.mTempBinderSet;
        localSet.clear();
        i = 0;
        i1 = ((SparseArray)localObject1).size();
        localObject3 = new HashSet();
        k = i1 - 1;
        m = i;
        if (k < 0) {
          break label750;
        }
        WindowState localWindowState = (WindowState)((SparseArray)localObject1).valueAt(k);
        int i2 = localWindowState.mAttrs.flags;
        Task localTask = localWindowState.getTask();
        if ((localTask != null) && (((HashSet)localObject3).contains(Integer.valueOf(localTask.mTaskId))))
        {
          m = i;
        }
        else
        {
          m = i;
          if ((i2 & 0x10) == 0)
          {
            Rect localRect = this.mTempRect;
            computeWindowBoundsInScreen(localWindowState, localRect);
            m = i;
            if (!((Region)localObject2).quickReject(localRect))
            {
              j = i;
              if (isReportedWindowType(localWindowState.mAttrs.type))
              {
                WindowInfo localWindowInfo = obtainPopulatedWindowInfo(localWindowState, localRect);
                localSet.add(localWindowInfo.token);
                localArrayList.add(localWindowInfo);
                j = i;
                if (localWindowState.isFocused()) {
                  j = 1;
                }
              }
              if (localWindowState.mAttrs.type != 2032) {
                ((Region)localObject2).op(localRect, (Region)localObject2, Region.Op.REVERSE_DIFFERENCE);
              }
              if ((i2 & 0x28) == 0)
              {
                ((Region)localObject2).op(localWindowState.getDisplayFrameLw(), (Region)localObject2, Region.Op.REVERSE_DIFFERENCE);
                m = j;
                if (localTask == null) {
                  break label750;
                }
                ((HashSet)localObject3).add(Integer.valueOf(localTask.mTaskId));
                m = j;
              }
            }
          }
        }
      }
      int m = j;
      if (((Region)localObject2).isEmpty())
      {
        m = j;
        break label750;
        label411:
        if (i >= 0)
        {
          localObject2 = (WindowState)((SparseArray)localObject1).valueAt(i);
          if (!((WindowState)localObject2).isFocused()) {
            break label770;
          }
          localObject3 = this.mTempRect;
          computeWindowBoundsInScreen((WindowState)localObject2, (Rect)localObject3);
          localObject2 = obtainPopulatedWindowInfo((WindowState)localObject2, (Rect)localObject3);
          localSet.add(((WindowInfo)localObject2).token);
          localList.add(localObject2);
        }
        label480:
        k = localList.size();
        i = 0;
        label490:
        if (i < k)
        {
          localObject2 = (WindowInfo)localList.get(i);
          if (!localSet.contains(((WindowInfo)localObject2).parentToken)) {
            ((WindowInfo)localObject2).parentToken = null;
          }
          if (((WindowInfo)localObject2).childTokens == null) {
            break label777;
          }
          j = ((WindowInfo)localObject2).childTokens.size() - 1;
          label550:
          if (j < 0) {
            break label777;
          }
          if (localSet.contains(((WindowInfo)localObject2).childTokens.get(j))) {
            break label763;
          }
          ((WindowInfo)localObject2).childTokens.remove(j);
          break label763;
        }
        ((SparseArray)localObject1).clear();
        localSet.clear();
        if (this.mOldWindows.size() != localList.size()) {
          i = 1;
        }
        for (;;)
        {
          label623:
          if (i != 0) {
            cacheWindows(localList);
          }
          if (i != 0) {
            this.mCallback.onWindowsForAccessibilityChanged(localList);
          }
          clearAndRecycleWindows(localList);
          return;
          if (this.mOldWindows.isEmpty())
          {
            i = n;
            if (!localList.isEmpty()) {
              break;
            }
          }
        }
      }
      for (;;)
      {
        i = n;
        if (j >= k) {
          break label623;
        }
        boolean bool = windowChangedNoLayer((WindowInfo)this.mOldWindows.get(j), (WindowInfo)localList.get(j));
        if (bool)
        {
          i = 1;
          break label623;
        }
        j += 1;
        continue;
        k -= 1;
        i = m;
        break;
        label750:
        if (m != 0) {
          break label480;
        }
        i = i1 - 1;
        break label411;
        label763:
        j -= 1;
        break label550;
        label770:
        i -= 1;
        break label411;
        label777:
        i += 1;
        break label490;
        j = 0;
      }
    }
    
    public void performComputeChangedWindowsNotLocked()
    {
      this.mHandler.removeMessages(1);
      computeChangedWindows();
    }
    
    public void scheduleComputeChangedWindowsLocked()
    {
      if (!this.mHandler.hasMessages(1)) {
        this.mHandler.sendEmptyMessageDelayed(1, this.mRecurringAccessibilityEventsIntervalMillis);
      }
    }
    
    private class MyHandler
      extends Handler
    {
      public static final int MESSAGE_COMPUTE_CHANGED_WINDOWS = 1;
      
      public MyHandler(Looper paramLooper)
      {
        super(null, false);
      }
      
      public void handleMessage(Message paramMessage)
      {
        switch (paramMessage.what)
        {
        default: 
          return;
        }
        AccessibilityController.WindowsForAccessibilityObserver.this.computeChangedWindows();
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/AccessibilityController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */