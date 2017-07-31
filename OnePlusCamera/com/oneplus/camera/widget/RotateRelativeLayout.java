package com.oneplus.camera.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.MotionEvent.PointerProperties;
import android.view.ViewParent;
import android.widget.RelativeLayout;
import com.oneplus.base.Rotation;
import com.oneplus.camera.CameraActivity;

public class RotateRelativeLayout
  extends RelativeLayout
{
  private static final boolean ENABLE_LOG = false;
  private static final String TAG = RotateRelativeLayout.class.getSimpleName();
  private Matrix m_InvMatrix = new Matrix();
  private RectF m_NewRectF = new RectF();
  private int m_RotDiff;
  private Matrix m_RotMatrix = new Matrix();
  private Rotation m_Rotation;
  
  public RotateRelativeLayout(Context paramContext)
  {
    super(paramContext);
  }
  
  public RotateRelativeLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private MotionEvent createRotatedMotionEvent(MotionEvent paramMotionEvent)
  {
    MotionEvent.PointerProperties[] arrayOfPointerProperties = new MotionEvent.PointerProperties[paramMotionEvent.getPointerCount()];
    MotionEvent.PointerCoords[] arrayOfPointerCoords = new MotionEvent.PointerCoords[paramMotionEvent.getPointerCount()];
    float f2 = paramMotionEvent.getX();
    float f1 = paramMotionEvent.getY();
    int i = 0;
    if (i < paramMotionEvent.getPointerCount())
    {
      arrayOfPointerProperties[i] = new MotionEvent.PointerProperties();
      paramMotionEvent.getPointerProperties(i, arrayOfPointerProperties[i]);
      arrayOfPointerCoords[i] = new MotionEvent.PointerCoords();
      paramMotionEvent.getPointerCoords(i, arrayOfPointerCoords[i]);
      float f3 = arrayOfPointerCoords[i].x;
      float f4 = arrayOfPointerCoords[i].y;
      float[] arrayOfFloat = new float[2];
      this.m_RotMatrix.mapPoints(arrayOfFloat, new float[] { f3, f4 });
      switch (this.m_RotDiff)
      {
      default: 
        label164:
        if (arrayOfFloat[0] < 0.0F) {
          arrayOfFloat[0] = 0.0F;
        }
        if (arrayOfFloat[1] < 0.0F) {
          arrayOfFloat[1] = 0.0F;
        }
        if (i == 0)
        {
          arrayOfPointerCoords[0].x = paramMotionEvent.getRawX();
          arrayOfPointerCoords[0].y = paramMotionEvent.getRawY();
          f2 = arrayOfFloat[0];
          f1 = arrayOfFloat[1];
        }
        break;
      }
      for (;;)
      {
        i += 1;
        break;
        arrayOfFloat[0] -= 1.0F;
        break label164;
        arrayOfFloat[1] -= 1.0F;
        break label164;
        arrayOfPointerCoords[i].x = (arrayOfFloat[0] + (paramMotionEvent.getRawX() - f2));
        arrayOfPointerCoords[i].y = (arrayOfFloat[1] + (paramMotionEvent.getRawY() - f1));
      }
    }
    paramMotionEvent = MotionEvent.obtain(paramMotionEvent.getDownTime(), paramMotionEvent.getEventTime(), paramMotionEvent.getAction(), paramMotionEvent.getPointerCount(), arrayOfPointerProperties, arrayOfPointerCoords, paramMotionEvent.getMetaState(), paramMotionEvent.getButtonState(), paramMotionEvent.getXPrecision(), paramMotionEvent.getYPrecision(), paramMotionEvent.getDeviceId(), paramMotionEvent.getEdgeFlags(), paramMotionEvent.getSource(), paramMotionEvent.getFlags());
    paramMotionEvent.setLocation(f2, f1);
    return paramMotionEvent;
  }
  
  private Rotation getActivityRotation()
  {
    Context localContext = getContext();
    if ((localContext instanceof CameraActivity)) {
      return (Rotation)((CameraActivity)localContext).get(CameraActivity.PROP_ACTIVITY_ROTATION);
    }
    if ((localContext instanceof Activity)) {
      return Rotation.fromScreenOrientation(((Activity)localContext).getRequestedOrientation());
    }
    return this.m_Rotation;
  }
  
  protected void dispatchDraw(Canvas paramCanvas)
  {
    paramCanvas.save();
    paramCanvas.concat(this.m_InvMatrix);
    super.dispatchDraw(paramCanvas);
    paramCanvas.restore();
  }
  
  public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
  {
    MotionEvent localMotionEvent = createRotatedMotionEvent(paramMotionEvent);
    paramMotionEvent.getPointerCount();
    boolean bool = super.dispatchTouchEvent(localMotionEvent);
    localMotionEvent.recycle();
    return bool;
  }
  
  public Rotation getLayoutRotation()
  {
    return this.m_Rotation;
  }
  
  public ViewParent invalidateChildInParent(int[] paramArrayOfInt, Rect paramRect)
  {
    paramRect.offset(paramArrayOfInt[0], paramArrayOfInt[1]);
    this.m_NewRectF.set(paramRect);
    this.m_InvMatrix.mapRect(this.m_NewRectF);
    this.m_NewRectF.roundOut(paramRect);
    invalidate(paramRect);
    return super.invalidateChildInParent(paramArrayOfInt, paramRect);
  }
  
  protected void onAnimationEnd()
  {
    super.onAnimationEnd();
    requestLayout();
    invalidate();
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Rotation localRotation = getActivityRotation();
    if ((localRotation != null) && (this.m_Rotation != null) && (localRotation.isLandscape() != this.m_Rotation.isLandscape()))
    {
      super.onLayout(paramBoolean, paramInt2, paramInt1, paramInt4, paramInt3);
      return;
    }
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    Rotation localRotation = getActivityRotation();
    if ((localRotation != null) && (this.m_Rotation != null) && (localRotation.isLandscape() != this.m_Rotation.isLandscape())) {
      super.onMeasure(paramInt2, paramInt1);
    }
    for (;;)
    {
      rotateMeasurement();
      return;
      super.onMeasure(paramInt1, paramInt2);
    }
  }
  
  protected void rotateMeasurement()
  {
    Rotation localRotation = getActivityRotation();
    if ((localRotation != null) && (this.m_Rotation != null) && (localRotation.isLandscape() != this.m_Rotation.isLandscape())) {
      setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }
    if ((localRotation != null) && (this.m_Rotation != null))
    {
      this.m_RotDiff = Math.abs(localRotation.getDeviceOrientation() - this.m_Rotation.getDeviceOrientation());
      this.m_RotMatrix.reset();
      switch (this.m_RotDiff)
      {
      }
    }
    for (;;)
    {
      this.m_InvMatrix = new Matrix(this.m_RotMatrix);
      this.m_RotMatrix.invert(this.m_InvMatrix);
      return;
      this.m_RotDiff = 0;
      break;
      this.m_RotMatrix.setRotate(0.0F);
      this.m_RotMatrix.postTranslate(0.0F, 0.0F);
      continue;
      this.m_RotMatrix.setRotate(90.0F);
      this.m_RotMatrix.postTranslate(getMeasuredHeight(), 0.0F);
      continue;
      this.m_RotMatrix.setRotate(180.0F);
      this.m_RotMatrix.postTranslate(getMeasuredWidth(), getMeasuredHeight());
      continue;
      this.m_RotMatrix.setRotate(270.0F);
      this.m_RotMatrix.postTranslate(0.0F, getMeasuredWidth());
    }
  }
  
  public final void setRotation(Rotation paramRotation)
  {
    if (this.m_Rotation == paramRotation) {
      return;
    }
    this.m_Rotation = paramRotation;
    requestLayout();
    invalidate();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/widget/RotateRelativeLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */