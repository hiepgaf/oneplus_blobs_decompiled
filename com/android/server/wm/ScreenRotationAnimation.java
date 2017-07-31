package com.android.server.wm;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.Slog;
import android.view.Display;
import android.view.DisplayInfo;
import android.view.Surface;
import android.view.Surface.OutOfResourcesException;
import android.view.SurfaceControl;
import android.view.SurfaceSession;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import java.io.PrintWriter;

class ScreenRotationAnimation
{
  static final boolean DEBUG_STATE = false;
  static final boolean DEBUG_TRANSFORMS = false;
  static final int SCREEN_FREEZE_LAYER_BASE = 2010000;
  static final int SCREEN_FREEZE_LAYER_CUSTOM = 2010003;
  static final int SCREEN_FREEZE_LAYER_ENTER = 2010000;
  static final int SCREEN_FREEZE_LAYER_EXIT = 2010002;
  static final int SCREEN_FREEZE_LAYER_SCREENSHOT = 2010001;
  static final String TAG = "WindowManager";
  static final boolean TWO_PHASE_ANIMATION = false;
  static final boolean USE_CUSTOM_BLACK_FRAME = false;
  boolean mAnimRunning;
  final Context mContext;
  int mCurRotation;
  Rect mCurrentDisplayRect = new Rect();
  BlackFrame mCustomBlackFrame;
  final DisplayContent mDisplayContent;
  final Transformation mEnterTransformation = new Transformation();
  BlackFrame mEnteringBlackFrame;
  final Matrix mExitFrameFinalMatrix = new Matrix();
  final Transformation mExitTransformation = new Transformation();
  BlackFrame mExitingBlackFrame;
  boolean mFinishAnimReady;
  long mFinishAnimStartTime;
  Animation mFinishEnterAnimation;
  final Transformation mFinishEnterTransformation = new Transformation();
  Animation mFinishExitAnimation;
  final Transformation mFinishExitTransformation = new Transformation();
  Animation mFinishFrameAnimation;
  final Transformation mFinishFrameTransformation = new Transformation();
  boolean mForceDefaultOrientation;
  final Matrix mFrameInitialMatrix = new Matrix();
  final Transformation mFrameTransformation = new Transformation();
  long mHalfwayPoint;
  int mHeight;
  Animation mLastRotateEnterAnimation;
  final Transformation mLastRotateEnterTransformation = new Transformation();
  Animation mLastRotateExitAnimation;
  final Transformation mLastRotateExitTransformation = new Transformation();
  Animation mLastRotateFrameAnimation;
  final Transformation mLastRotateFrameTransformation = new Transformation();
  private boolean mMoreFinishEnter;
  private boolean mMoreFinishExit;
  private boolean mMoreFinishFrame;
  private boolean mMoreRotateEnter;
  private boolean mMoreRotateExit;
  private boolean mMoreRotateFrame;
  private boolean mMoreStartEnter;
  private boolean mMoreStartExit;
  private boolean mMoreStartFrame;
  Rect mOriginalDisplayRect = new Rect();
  int mOriginalHeight;
  int mOriginalRotation;
  int mOriginalWidth;
  Animation mRotateEnterAnimation;
  final Transformation mRotateEnterTransformation = new Transformation();
  Animation mRotateExitAnimation;
  final Transformation mRotateExitTransformation = new Transformation();
  Animation mRotateFrameAnimation;
  final Transformation mRotateFrameTransformation = new Transformation();
  final Matrix mSnapshotFinalMatrix = new Matrix();
  final Matrix mSnapshotInitialMatrix = new Matrix();
  Animation mStartEnterAnimation;
  final Transformation mStartEnterTransformation = new Transformation();
  Animation mStartExitAnimation;
  final Transformation mStartExitTransformation = new Transformation();
  Animation mStartFrameAnimation;
  final Transformation mStartFrameTransformation = new Transformation();
  boolean mStarted;
  SurfaceControl mSurfaceControl;
  final float[] mTmpFloats = new float[9];
  final Matrix mTmpMatrix = new Matrix();
  int mWidth;
  
  public ScreenRotationAnimation(Context paramContext, DisplayContent paramDisplayContent, SurfaceSession paramSurfaceSession, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    this.mContext = paramContext;
    this.mDisplayContent = paramDisplayContent;
    paramDisplayContent.getLogicalDisplayRect(this.mOriginalDisplayRect);
    paramContext = paramDisplayContent.getDisplay();
    int k = paramContext.getRotation();
    DisplayInfo localDisplayInfo = paramDisplayContent.getDisplayInfo();
    int j;
    int i;
    if (paramBoolean2)
    {
      this.mForceDefaultOrientation = true;
      j = paramDisplayContent.mBaseDisplayWidth;
      i = paramDisplayContent.mBaseDisplayHeight;
      if ((k != 1) && (k != 3)) {
        break label608;
      }
      this.mWidth = i;
      this.mHeight = j;
      label335:
      this.mOriginalRotation = k;
      this.mOriginalWidth = j;
      this.mOriginalHeight = i;
      if (!paramBoolean1)
      {
        if (WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
          Slog.i("WindowManager", ">>> OPEN TRANSACTION ScreenRotationAnimation");
        }
        SurfaceControl.openTransaction();
      }
      i = 4;
      if (paramBoolean3) {
        i = 132;
      }
    }
    for (;;)
    {
      try
      {
        if (!WindowManagerDebugConfig.DEBUG_SURFACE_TRACE) {
          continue;
        }
        this.mSurfaceControl = new WindowSurfaceController.SurfaceTrace(paramSurfaceSession, "ScreenshotSurface", this.mWidth, this.mHeight, -1, i);
        Slog.w(TAG, "ScreenRotationAnimation ctor: displayOffset=" + this.mOriginalDisplayRect.toShortString());
        paramDisplayContent = new Surface();
        paramDisplayContent.copyFrom(this.mSurfaceControl);
        SurfaceControl.screenshot(SurfaceControl.getBuiltInDisplay(0), paramDisplayContent);
        this.mSurfaceControl.setLayerStack(paramContext.getLayerStack());
        this.mSurfaceControl.setLayer(2010001);
        this.mSurfaceControl.setAlpha(0.0F);
        this.mSurfaceControl.show();
        paramDisplayContent.destroy();
      }
      catch (Surface.OutOfResourcesException paramContext)
      {
        label608:
        Slog.w(TAG, "Unable to allocate freeze surface", paramContext);
        continue;
      }
      finally
      {
        if (paramBoolean1) {
          continue;
        }
        SurfaceControl.closeTransaction();
        if (!WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
          continue;
        }
        Slog.i("WindowManager", "<<< CLOSE TRANSACTION ScreenRotationAnimation");
      }
      if ((WindowManagerDebugConfig.SHOW_TRANSACTIONS) || (WindowManagerDebugConfig.SHOW_SURFACE_ALLOC)) {
        Slog.i("WindowManager", "  FREEZE " + this.mSurfaceControl + ": CREATE");
      }
      setRotationInTransaction(k);
      if (!paramBoolean1)
      {
        SurfaceControl.closeTransaction();
        if (WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
          Slog.i("WindowManager", "<<< CLOSE TRANSACTION ScreenRotationAnimation");
        }
      }
      return;
      j = localDisplayInfo.logicalWidth;
      i = localDisplayInfo.logicalHeight;
      break;
      this.mWidth = j;
      this.mHeight = i;
      break label335;
      this.mSurfaceControl = new SurfaceControl(paramSurfaceSession, "ScreenshotSurface", this.mWidth, this.mHeight, -1, i);
    }
  }
  
  public static void createRotationMatrix(int paramInt1, int paramInt2, int paramInt3, Matrix paramMatrix)
  {
    switch (paramInt1)
    {
    default: 
      return;
    case 0: 
      paramMatrix.reset();
      return;
    case 1: 
      paramMatrix.setRotate(90.0F, 0.0F, 0.0F);
      paramMatrix.postTranslate(paramInt3, 0.0F);
      return;
    case 2: 
      paramMatrix.setRotate(180.0F, 0.0F, 0.0F);
      paramMatrix.postTranslate(paramInt2, paramInt3);
      return;
    }
    paramMatrix.setRotate(270.0F, 0.0F, 0.0F);
    paramMatrix.postTranslate(0.0F, paramInt2);
  }
  
  private boolean hasAnimations()
  {
    if (this.mRotateEnterAnimation != null) {}
    while (this.mRotateExitAnimation != null) {
      return true;
    }
    return false;
  }
  
  private void setRotationInTransaction(int paramInt)
  {
    this.mCurRotation = paramInt;
    createRotationMatrix(DisplayContent.deltaRotation(paramInt, 0), this.mWidth, this.mHeight, this.mSnapshotInitialMatrix);
    setSnapshotTransformInTransaction(this.mSnapshotInitialMatrix, 1.0F);
  }
  
  private void setSnapshotTransformInTransaction(Matrix paramMatrix, float paramFloat)
  {
    if (this.mSurfaceControl != null)
    {
      paramMatrix.getValues(this.mTmpFloats);
      float f4 = this.mTmpFloats[2];
      float f3 = this.mTmpFloats[5];
      float f2 = f4;
      float f1 = f3;
      if (this.mForceDefaultOrientation)
      {
        this.mDisplayContent.getLogicalDisplayRect(this.mCurrentDisplayRect);
        f2 = f4 - this.mCurrentDisplayRect.left;
        f1 = f3 - this.mCurrentDisplayRect.top;
      }
      this.mSurfaceControl.setPosition(f2, f1);
      this.mSurfaceControl.setMatrix(this.mTmpFloats[0], this.mTmpFloats[3], this.mTmpFloats[1], this.mTmpFloats[4]);
      this.mSurfaceControl.setAlpha(paramFloat);
    }
  }
  
  private boolean startAnimation(SurfaceSession paramSurfaceSession, long paramLong, float paramFloat, int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3, int paramInt4)
  {
    if (this.mSurfaceControl == null) {
      return false;
    }
    if (this.mStarted) {
      return true;
    }
    this.mStarted = true;
    int j = DisplayContent.deltaRotation(this.mCurRotation, this.mOriginalRotation);
    if ((paramInt3 != 0) && (paramInt4 != 0))
    {
      int i = 1;
      this.mRotateExitAnimation = AnimationUtils.loadAnimation(this.mContext, paramInt3);
      this.mRotateEnterAnimation = AnimationUtils.loadAnimation(this.mContext, paramInt4);
      paramInt3 = i;
      this.mRotateEnterAnimation.initialize(paramInt1, paramInt2, this.mOriginalWidth, this.mOriginalHeight);
      this.mRotateExitAnimation.initialize(paramInt1, paramInt2, this.mOriginalWidth, this.mOriginalHeight);
      this.mAnimRunning = false;
      this.mFinishAnimReady = false;
      this.mFinishAnimStartTime = -1L;
      this.mRotateExitAnimation.restrictDuration(paramLong);
      this.mRotateExitAnimation.scaleCurrentDuration(paramFloat);
      this.mRotateEnterAnimation.restrictDuration(paramLong);
      this.mRotateEnterAnimation.scaleCurrentDuration(paramFloat);
      paramInt4 = this.mDisplayContent.getDisplay().getLayerStack();
      if ((paramInt3 == 0) && (this.mExitingBlackFrame == null))
      {
        if (WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
          break label531;
        }
        label198:
        SurfaceControl.openTransaction();
      }
    }
    for (;;)
    {
      try
      {
        createRotationMatrix(j, this.mOriginalWidth, this.mOriginalHeight, this.mFrameInitialMatrix);
        if (!this.mForceDefaultOrientation) {
          continue;
        }
        localRect1 = this.mCurrentDisplayRect;
        localRect2 = this.mOriginalDisplayRect;
        this.mExitingBlackFrame = new BlackFrame(paramSurfaceSession, localRect1, localRect2, 2010002, paramInt4, this.mForceDefaultOrientation);
        this.mExitingBlackFrame.setMatrix(this.mFrameInitialMatrix);
        SurfaceControl.closeTransaction();
        if (WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
          continue;
        }
      }
      catch (Surface.OutOfResourcesException localOutOfResourcesException)
      {
        Rect localRect1;
        Rect localRect2;
        label531:
        Slog.w(TAG, "Unable to allocate black surface", localOutOfResourcesException);
        SurfaceControl.closeTransaction();
        if (!WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
          continue;
        }
        Slog.i("WindowManager", "<<< CLOSE TRANSACTION ScreenRotationAnimation.startAnimation");
        continue;
      }
      finally
      {
        SurfaceControl.closeTransaction();
        if (WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
          continue;
        }
        throw paramSurfaceSession;
        Slog.i("WindowManager", "<<< CLOSE TRANSACTION ScreenRotationAnimation.startAnimation");
        continue;
        Slog.i("WindowManager", ">>> OPEN TRANSACTION ScreenRotationAnimation.startAnimation");
        continue;
        Slog.i("WindowManager", "<<< CLOSE TRANSACTION ScreenRotationAnimation.startAnimation");
        continue;
      }
      if ((paramInt3 != 0) && (this.mEnteringBlackFrame == null))
      {
        if (WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
          continue;
        }
        SurfaceControl.openTransaction();
      }
      try
      {
        this.mEnteringBlackFrame = new BlackFrame(paramSurfaceSession, new Rect(-paramInt1 * 1, -paramInt2 * 1, paramInt1 * 2, paramInt2 * 2), new Rect(0, 0, paramInt1, paramInt2), 2010000, paramInt4, false);
      }
      catch (Surface.OutOfResourcesException paramSurfaceSession)
      {
        Slog.w(TAG, "Unable to allocate black surface", paramSurfaceSession);
        SurfaceControl.closeTransaction();
        if (!WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
          continue;
        }
        Slog.i("WindowManager", "<<< CLOSE TRANSACTION ScreenRotationAnimation.startAnimation");
        continue;
      }
      finally
      {
        SurfaceControl.closeTransaction();
        if (WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
          break label740;
        }
      }
      return true;
      paramInt3 = 0;
      switch (j)
      {
      default: 
        break;
      case 0: 
        this.mRotateExitAnimation = AnimationUtils.loadAnimation(this.mContext, 17432688);
        this.mRotateEnterAnimation = AnimationUtils.loadAnimation(this.mContext, 17432687);
        break;
      case 1: 
        this.mRotateExitAnimation = AnimationUtils.loadAnimation(this.mContext, 17432700);
        this.mRotateEnterAnimation = AnimationUtils.loadAnimation(this.mContext, 17432699);
        break;
      case 2: 
        this.mRotateExitAnimation = AnimationUtils.loadAnimation(this.mContext, 17432691);
        this.mRotateEnterAnimation = AnimationUtils.loadAnimation(this.mContext, 17432690);
        break;
      case 3: 
        this.mRotateExitAnimation = AnimationUtils.loadAnimation(this.mContext, 17432697);
        this.mRotateEnterAnimation = AnimationUtils.loadAnimation(this.mContext, 17432696);
        break;
        Slog.i("WindowManager", ">>> OPEN TRANSACTION ScreenRotationAnimation.startAnimation");
        break label198;
        localRect1 = new Rect(-this.mOriginalWidth * 1, -this.mOriginalHeight * 1, this.mOriginalWidth * 2, this.mOriginalHeight * 2);
        localRect2 = new Rect(0, 0, this.mOriginalWidth, this.mOriginalHeight);
        continue;
        Slog.i("WindowManager", "<<< CLOSE TRANSACTION ScreenRotationAnimation.startAnimation");
      }
    }
    for (;;)
    {
      throw paramSurfaceSession;
      label740:
      Slog.i("WindowManager", "<<< CLOSE TRANSACTION ScreenRotationAnimation.startAnimation");
    }
  }
  
  private boolean stepAnimation(long paramLong)
  {
    if (paramLong > this.mHalfwayPoint) {
      this.mHalfwayPoint = Long.MAX_VALUE;
    }
    if ((this.mFinishAnimReady) && (this.mFinishAnimStartTime < 0L)) {
      this.mFinishAnimStartTime = paramLong;
    }
    boolean bool;
    if (this.mFinishAnimReady)
    {
      long l = this.mFinishAnimStartTime;
      this.mMoreRotateExit = false;
      if (this.mRotateExitAnimation != null) {
        this.mMoreRotateExit = this.mRotateExitAnimation.getTransformation(paramLong, this.mRotateExitTransformation);
      }
      this.mMoreRotateEnter = false;
      if (this.mRotateEnterAnimation != null) {
        this.mMoreRotateEnter = this.mRotateEnterAnimation.getTransformation(paramLong, this.mRotateEnterTransformation);
      }
      if ((!this.mMoreRotateExit) && (this.mRotateExitAnimation != null))
      {
        this.mRotateExitAnimation.cancel();
        this.mRotateExitAnimation = null;
        this.mRotateExitTransformation.clear();
      }
      if ((!this.mMoreRotateEnter) && (this.mRotateEnterAnimation != null))
      {
        this.mRotateEnterAnimation.cancel();
        this.mRotateEnterAnimation = null;
        this.mRotateEnterTransformation.clear();
      }
      this.mExitTransformation.set(this.mRotateExitTransformation);
      this.mEnterTransformation.set(this.mRotateEnterTransformation);
      if ((this.mMoreRotateEnter) || (this.mMoreRotateExit)) {
        break label242;
      }
      if (!this.mFinishAnimReady) {
        break label248;
      }
      bool = false;
    }
    for (;;)
    {
      this.mSnapshotFinalMatrix.setConcat(this.mExitTransformation.getMatrix(), this.mSnapshotInitialMatrix);
      return bool;
      break;
      label242:
      bool = true;
      continue;
      label248:
      bool = true;
    }
  }
  
  public boolean dismiss(SurfaceSession paramSurfaceSession, long paramLong, float paramFloat, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.mSurfaceControl == null) {
      return false;
    }
    if (!this.mStarted) {
      startAnimation(paramSurfaceSession, paramLong, paramFloat, paramInt1, paramInt2, true, paramInt3, paramInt4);
    }
    if (!this.mStarted) {
      return false;
    }
    this.mFinishAnimReady = true;
    return true;
  }
  
  public Transformation getEnterTransformation()
  {
    return this.mEnterTransformation;
  }
  
  boolean hasScreenshot()
  {
    return this.mSurfaceControl != null;
  }
  
  public boolean isAnimating()
  {
    return hasAnimations();
  }
  
  public boolean isRotating()
  {
    return this.mCurRotation != this.mOriginalRotation;
  }
  
  public void kill()
  {
    if (this.mSurfaceControl != null)
    {
      if ((WindowManagerDebugConfig.SHOW_TRANSACTIONS) || (WindowManagerDebugConfig.SHOW_SURFACE_ALLOC)) {
        Slog.i("WindowManager", "  FREEZE " + this.mSurfaceControl + ": DESTROY");
      }
      this.mSurfaceControl.destroy();
      this.mSurfaceControl = null;
    }
    if (this.mCustomBlackFrame != null)
    {
      this.mCustomBlackFrame.kill();
      this.mCustomBlackFrame = null;
    }
    if (this.mExitingBlackFrame != null)
    {
      this.mExitingBlackFrame.kill();
      this.mExitingBlackFrame = null;
    }
    if (this.mEnteringBlackFrame != null)
    {
      this.mEnteringBlackFrame.kill();
      this.mEnteringBlackFrame = null;
    }
    if (this.mRotateExitAnimation != null)
    {
      this.mRotateExitAnimation.cancel();
      this.mRotateExitAnimation = null;
    }
    if (this.mRotateEnterAnimation != null)
    {
      this.mRotateEnterAnimation.cancel();
      this.mRotateEnterAnimation = null;
    }
  }
  
  public void printTo(String paramString, PrintWriter paramPrintWriter)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mSurface=");
    paramPrintWriter.print(this.mSurfaceControl);
    paramPrintWriter.print(" mWidth=");
    paramPrintWriter.print(this.mWidth);
    paramPrintWriter.print(" mHeight=");
    paramPrintWriter.println(this.mHeight);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mExitingBlackFrame=");
    paramPrintWriter.println(this.mExitingBlackFrame);
    if (this.mExitingBlackFrame != null) {
      this.mExitingBlackFrame.printTo(paramString + "  ", paramPrintWriter);
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mEnteringBlackFrame=");
    paramPrintWriter.println(this.mEnteringBlackFrame);
    if (this.mEnteringBlackFrame != null) {
      this.mEnteringBlackFrame.printTo(paramString + "  ", paramPrintWriter);
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mCurRotation=");
    paramPrintWriter.print(this.mCurRotation);
    paramPrintWriter.print(" mOriginalRotation=");
    paramPrintWriter.println(this.mOriginalRotation);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mOriginalWidth=");
    paramPrintWriter.print(this.mOriginalWidth);
    paramPrintWriter.print(" mOriginalHeight=");
    paramPrintWriter.println(this.mOriginalHeight);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mStarted=");
    paramPrintWriter.print(this.mStarted);
    paramPrintWriter.print(" mAnimRunning=");
    paramPrintWriter.print(this.mAnimRunning);
    paramPrintWriter.print(" mFinishAnimReady=");
    paramPrintWriter.print(this.mFinishAnimReady);
    paramPrintWriter.print(" mFinishAnimStartTime=");
    paramPrintWriter.println(this.mFinishAnimStartTime);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mStartExitAnimation=");
    paramPrintWriter.print(this.mStartExitAnimation);
    paramPrintWriter.print(" ");
    this.mStartExitTransformation.printShortString(paramPrintWriter);
    paramPrintWriter.println();
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mStartEnterAnimation=");
    paramPrintWriter.print(this.mStartEnterAnimation);
    paramPrintWriter.print(" ");
    this.mStartEnterTransformation.printShortString(paramPrintWriter);
    paramPrintWriter.println();
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mStartFrameAnimation=");
    paramPrintWriter.print(this.mStartFrameAnimation);
    paramPrintWriter.print(" ");
    this.mStartFrameTransformation.printShortString(paramPrintWriter);
    paramPrintWriter.println();
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mFinishExitAnimation=");
    paramPrintWriter.print(this.mFinishExitAnimation);
    paramPrintWriter.print(" ");
    this.mFinishExitTransformation.printShortString(paramPrintWriter);
    paramPrintWriter.println();
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mFinishEnterAnimation=");
    paramPrintWriter.print(this.mFinishEnterAnimation);
    paramPrintWriter.print(" ");
    this.mFinishEnterTransformation.printShortString(paramPrintWriter);
    paramPrintWriter.println();
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mFinishFrameAnimation=");
    paramPrintWriter.print(this.mFinishFrameAnimation);
    paramPrintWriter.print(" ");
    this.mFinishFrameTransformation.printShortString(paramPrintWriter);
    paramPrintWriter.println();
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mRotateExitAnimation=");
    paramPrintWriter.print(this.mRotateExitAnimation);
    paramPrintWriter.print(" ");
    this.mRotateExitTransformation.printShortString(paramPrintWriter);
    paramPrintWriter.println();
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mRotateEnterAnimation=");
    paramPrintWriter.print(this.mRotateEnterAnimation);
    paramPrintWriter.print(" ");
    this.mRotateEnterTransformation.printShortString(paramPrintWriter);
    paramPrintWriter.println();
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mRotateFrameAnimation=");
    paramPrintWriter.print(this.mRotateFrameAnimation);
    paramPrintWriter.print(" ");
    this.mRotateFrameTransformation.printShortString(paramPrintWriter);
    paramPrintWriter.println();
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mExitTransformation=");
    this.mExitTransformation.printShortString(paramPrintWriter);
    paramPrintWriter.println();
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mEnterTransformation=");
    this.mEnterTransformation.printShortString(paramPrintWriter);
    paramPrintWriter.println();
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mFrameTransformation=");
    this.mEnterTransformation.printShortString(paramPrintWriter);
    paramPrintWriter.println();
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mFrameInitialMatrix=");
    this.mFrameInitialMatrix.printShortString(paramPrintWriter);
    paramPrintWriter.println();
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mSnapshotInitialMatrix=");
    this.mSnapshotInitialMatrix.printShortString(paramPrintWriter);
    paramPrintWriter.print(" mSnapshotFinalMatrix=");
    this.mSnapshotFinalMatrix.printShortString(paramPrintWriter);
    paramPrintWriter.println();
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mExitFrameFinalMatrix=");
    this.mExitFrameFinalMatrix.printShortString(paramPrintWriter);
    paramPrintWriter.println();
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mForceDefaultOrientation=");
    paramPrintWriter.print(this.mForceDefaultOrientation);
    if (this.mForceDefaultOrientation)
    {
      paramPrintWriter.print(" mOriginalDisplayRect=");
      paramPrintWriter.print(this.mOriginalDisplayRect.toShortString());
      paramPrintWriter.print(" mCurrentDisplayRect=");
      paramPrintWriter.println(this.mCurrentDisplayRect.toShortString());
    }
  }
  
  public boolean setRotationInTransaction(int paramInt1, SurfaceSession paramSurfaceSession, long paramLong, float paramFloat, int paramInt2, int paramInt3)
  {
    setRotationInTransaction(paramInt1);
    return false;
  }
  
  public boolean stepAnimationLocked(long paramLong)
  {
    if (!hasAnimations())
    {
      this.mFinishAnimReady = false;
      return false;
    }
    if (!this.mAnimRunning)
    {
      if (this.mRotateEnterAnimation != null) {
        this.mRotateEnterAnimation.setStartTime(paramLong);
      }
      if (this.mRotateExitAnimation != null) {
        this.mRotateExitAnimation.setStartTime(paramLong);
      }
      this.mAnimRunning = true;
      this.mHalfwayPoint = (this.mRotateEnterAnimation.getDuration() / 2L + paramLong);
    }
    return stepAnimation(paramLong);
  }
  
  void updateSurfacesInTransaction()
  {
    if (!this.mStarted) {
      return;
    }
    if ((this.mSurfaceControl == null) || (this.mMoreStartExit) || (this.mMoreFinishExit))
    {
      if (this.mCustomBlackFrame != null)
      {
        if ((!this.mMoreStartFrame) && (!this.mMoreFinishFrame)) {
          break label204;
        }
        label50:
        this.mCustomBlackFrame.setMatrix(this.mFrameTransformation.getMatrix());
      }
      label64:
      if (this.mExitingBlackFrame != null)
      {
        if ((!this.mMoreStartExit) && (!this.mMoreFinishExit)) {
          break label221;
        }
        label85:
        this.mExitFrameFinalMatrix.setConcat(this.mExitTransformation.getMatrix(), this.mFrameInitialMatrix);
        this.mExitingBlackFrame.setMatrix(this.mExitFrameFinalMatrix);
        if (this.mForceDefaultOrientation) {
          this.mExitingBlackFrame.setAlpha(this.mExitTransformation.getAlpha());
        }
      }
      label136:
      if (this.mEnteringBlackFrame != null)
      {
        if ((!this.mMoreStartEnter) && (!this.mMoreFinishEnter)) {
          break label238;
        }
        label157:
        this.mEnteringBlackFrame.setMatrix(this.mEnterTransformation.getMatrix());
      }
    }
    for (;;)
    {
      setSnapshotTransformInTransaction(this.mSnapshotFinalMatrix, this.mExitTransformation.getAlpha());
      return;
      if (this.mMoreRotateExit) {
        break;
      }
      this.mSurfaceControl.hide();
      break;
      label204:
      if (this.mMoreRotateFrame) {
        break label50;
      }
      this.mCustomBlackFrame.hide();
      break label64;
      label221:
      if (this.mMoreRotateExit) {
        break label85;
      }
      this.mExitingBlackFrame.hide();
      break label136;
      label238:
      if (this.mMoreRotateEnter) {
        break label157;
      }
      this.mEnteringBlackFrame.hide();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/ScreenRotationAnimation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */