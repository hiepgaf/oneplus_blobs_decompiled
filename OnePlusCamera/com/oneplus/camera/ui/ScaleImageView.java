package com.oneplus.camera.ui;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.Rotation;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.R.styleable;
import com.oneplus.media.ImageUtils;
import java.io.File;
import java.lang.ref.WeakReference;

public class ScaleImageView
  extends ImageView
{
  private static final int CLICK_THREADHOLD = 6;
  private static final float DEFAULT_SCALE_TIMES = 3.0F;
  private static final int DELETE_THREADHOLD = 20;
  private static final int DOUBLE_CLICK_TIME = 350;
  private static final int DRAG = 1;
  private static final int DURATION_SCALE_ANIMATION = 200;
  private static final float MAX_SCALE_TIMES = 5.0F;
  private static final int MODE_SLIDER = 0;
  private static final int MODE_VIEWER = 1;
  private static final int NONE = 0;
  private static final int ORIENTAL_HORIZONTAL = 0;
  private static final int ORIENTAL_VERTICAL = 1;
  static final String TAG = ScaleImageView.class.getSimpleName();
  private static final int ZOOM = 2;
  private ValueAnimator mAnimator = null;
  private CameraActivity m_CameraActivity;
  private PointF m_Center = new PointF();
  private boolean m_Click = true;
  private BitmapWorkerTask m_DecodeTask = null;
  private int m_DiffX;
  private int m_DiffY;
  private int m_DrawableHeight;
  private int m_DrawableWidth;
  private PreviewGallery m_Gallery;
  private Bitmap m_InitialBitmap;
  private boolean m_IsBitmapFullSize = false;
  private boolean m_IsDeleted;
  private boolean m_IsVideo = false;
  private boolean m_IsZoomIn = false;
  private Matrix m_Matrix = new Matrix();
  private final float[] m_MatrixValues = new float[9];
  private float m_MaxScale;
  private int m_MaxX;
  private int m_MaxY;
  private PointF m_Mid = new PointF();
  private int m_MidX;
  private int m_MidY;
  private Matrix m_MinMatrix = new Matrix();
  private float m_MinScale;
  private int m_MinX;
  private int m_MinY;
  private int m_Mode = 0;
  private float m_OldDist = 1.0F;
  private int m_Oriental;
  private int m_PaddingX;
  private int m_PaddingY;
  private String m_Path;
  private int m_PreviousX;
  private int m_PreviousY;
  private Matrix m_SavedMatrix = new Matrix();
  private ScaleImageViewDrawable m_ScaleDrawable;
  private PointF m_Start = new PointF();
  private int m_TouchMode = 0;
  private int m_ViewHeight;
  private int m_ViewWidth;
  private float m_ViewerPreviousDist = -1.0F;
  private View m_parent;
  private boolean waitDouble = true;
  
  public ScaleImageView(Context paramContext)
  {
    super(paramContext);
  }
  
  public ScaleImageView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ScaleImageView);
    this.m_Oriental = paramContext.getInt(0, 0);
    Log.d(TAG, "oriental: " + this.m_Oriental);
    paramContext.recycle();
  }
  
  private void animateCenterToSlider()
  {
    final float f1 = getScale();
    final float f2 = f1 * this.m_DrawableWidth;
    float f6 = f1 * this.m_DrawableHeight;
    Log.d(TAG, "currentWidth: " + f2 + " currentHeight: " + f6);
    Log.d(TAG, "MTRANS_X: " + getValue(this.m_Matrix, 2) + "  MTRANS_Y: " + getValue(this.m_Matrix, 5));
    final float f3;
    float f5;
    final float f4;
    if (this.m_Oriental == 0)
    {
      f3 = getTranslateX();
      f5 = -(f2 - this.m_ViewWidth) / 2.0F;
      f4 = Math.abs(f5 - f3);
      f1 = getTranslateY();
    }
    for (f2 = -(f6 - this.m_ViewHeight) / 2.0F - f1; f4 == 0.0F; f2 = (this.m_ViewHeight - f2) / 2.0F - f1)
    {
      animateToSliderInternal();
      return;
      f3 = getTranslateX();
      f5 = this.m_ViewWidth + (f6 - this.m_ViewWidth) / 2.0F;
      f4 = Math.abs(f5 - f3);
      f1 = getTranslateY();
    }
    this.mAnimator = ValueAnimator.ofFloat(new float[] { f3, f5 });
    this.mAnimator.setDuration(500L);
    this.mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        float f1 = ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue();
        float f2 = f1;
        float f3 = f2;
        float f4 = Math.abs(f1 - f3) / f4;
        ScaleImageView.-wrap5(ScaleImageView.this, f1);
        ScaleImageView.-wrap6(ScaleImageView.this, f2 + f3 * f4);
        ScaleImageView.this.setImageMatrix(ScaleImageView.-get6(ScaleImageView.this));
      }
    });
    this.mAnimator.addListener(new Animator.AnimatorListener()
    {
      public void onAnimationCancel(Animator paramAnonymousAnimator)
      {
        ScaleImageView.-get4(ScaleImageView.this).setSwipeable(true);
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        ScaleImageView.-set0(ScaleImageView.this, null);
        ScaleImageView.-get16(ScaleImageView.this).set(ScaleImageView.-get6(ScaleImageView.this));
        ScaleImageView.-wrap3(ScaleImageView.this);
      }
      
      public void onAnimationRepeat(Animator paramAnonymousAnimator) {}
      
      public void onAnimationStart(Animator paramAnonymousAnimator)
      {
        ScaleImageView.-get4(ScaleImageView.this).setSwipeable(false);
      }
    });
    this.mAnimator.start();
  }
  
  private void animateToSlider()
  {
    float f = this.m_MinScale / getScale();
    this.mAnimator = ValueAnimator.ofFloat(new float[] { 1.0F, f });
    this.mAnimator.setDuration(200L);
    this.mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        ScaleImageView.-get6(ScaleImageView.this).set(ScaleImageView.-get16(ScaleImageView.this));
        paramAnonymousValueAnimator = (Float)paramAnonymousValueAnimator.getAnimatedValue();
        ScaleImageView.-wrap8(ScaleImageView.this, paramAnonymousValueAnimator.floatValue(), ScaleImageView.-get0(ScaleImageView.this));
        float f2 = ScaleImageView.-wrap0(ScaleImageView.this);
        float f1 = f2 * ScaleImageView.-get3(ScaleImageView.this);
        float f5 = f2 * ScaleImageView.-get2(ScaleImageView.this);
        float f4;
        float f3;
        if (ScaleImageView.-get12(ScaleImageView.this) == 0)
        {
          f4 = ScaleImageView.-wrap1(ScaleImageView.this);
          f3 = -(f1 - ScaleImageView.-get20(ScaleImageView.this)) / 2.0F - f4;
          f2 = ScaleImageView.-wrap2(ScaleImageView.this);
        }
        for (f1 = -(f5 - ScaleImageView.-get19(ScaleImageView.this)) / 2.0F - f2;; f1 = (ScaleImageView.-get19(ScaleImageView.this) - f1) / 2.0F - f2)
        {
          f3 = f4 + (paramAnonymousValueAnimator.floatValue() - 1.0F) * f3 / this.val$range;
          f1 = f2 + (paramAnonymousValueAnimator.floatValue() - 1.0F) * f1 / this.val$range;
          Log.d(ScaleImageView.TAG, "valueX: " + f3 + " valueY: " + f1);
          ScaleImageView.-wrap5(ScaleImageView.this, f3);
          ScaleImageView.-wrap6(ScaleImageView.this, f1);
          ScaleImageView.this.setImageMatrix(ScaleImageView.-get6(ScaleImageView.this));
          return;
          f4 = ScaleImageView.-wrap1(ScaleImageView.this);
          f3 = ScaleImageView.-get20(ScaleImageView.this) + (f5 - ScaleImageView.-get20(ScaleImageView.this)) / 2.0F - f4;
          f2 = ScaleImageView.-wrap2(ScaleImageView.this);
        }
      }
    });
    this.mAnimator.addListener(new Animator.AnimatorListener()
    {
      public void onAnimationCancel(Animator paramAnonymousAnimator)
      {
        ScaleImageView.-set0(ScaleImageView.this, null);
        ScaleImageView.-get4(ScaleImageView.this).setSwipeable(true);
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        ScaleImageView.-set0(ScaleImageView.this, null);
        ScaleImageView.-wrap7(ScaleImageView.this);
      }
      
      public void onAnimationRepeat(Animator paramAnonymousAnimator) {}
      
      public void onAnimationStart(Animator paramAnonymousAnimator)
      {
        ScaleImageView.-get4(ScaleImageView.this).setSwipeable(false);
      }
    });
    this.mAnimator.start();
  }
  
  private void animateToSliderInternal()
  {
    this.mAnimator = ValueAnimator.ofFloat(new float[] { 1.0F, this.m_MinScale / getScale() });
    this.mAnimator.setDuration(400L);
    this.mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        ScaleImageView.-get6(ScaleImageView.this).set(ScaleImageView.-get16(ScaleImageView.this));
        paramAnonymousValueAnimator = (Float)paramAnonymousValueAnimator.getAnimatedValue();
        ScaleImageView.-get6(ScaleImageView.this).postScale(paramAnonymousValueAnimator.floatValue(), paramAnonymousValueAnimator.floatValue(), ScaleImageView.-get0(ScaleImageView.this).x, ScaleImageView.-get0(ScaleImageView.this).y);
        ScaleImageView.this.setImageMatrix(ScaleImageView.-get6(ScaleImageView.this));
      }
    });
    this.mAnimator.addListener(new Animator.AnimatorListener()
    {
      public void onAnimationCancel(Animator paramAnonymousAnimator)
      {
        ScaleImageView.-get4(ScaleImageView.this).setSwipeable(true);
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        ScaleImageView.-set0(ScaleImageView.this, null);
        ScaleImageView.-wrap7(ScaleImageView.this);
      }
      
      public void onAnimationRepeat(Animator paramAnonymousAnimator) {}
      
      public void onAnimationStart(Animator paramAnonymousAnimator)
      {
        ScaleImageView.-get4(ScaleImageView.this).setSwipeable(false);
      }
    });
    this.mAnimator.start();
  }
  
  private void animateToViewer()
  {
    this.mAnimator = ValueAnimator.ofFloat(new float[] { 1.0F, 3.0F });
    this.mAnimator.setDuration(200L);
    this.mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        ScaleImageView.-get6(ScaleImageView.this).set(ScaleImageView.-get16(ScaleImageView.this));
        paramAnonymousValueAnimator = (Float)paramAnonymousValueAnimator.getAnimatedValue();
        ScaleImageView.-wrap8(ScaleImageView.this, paramAnonymousValueAnimator.floatValue(), ScaleImageView.-get18(ScaleImageView.this));
        float f1 = ScaleImageView.-wrap0(ScaleImageView.this);
        float f7 = f1 * ScaleImageView.-get3(ScaleImageView.this);
        f1 *= ScaleImageView.-get2(ScaleImageView.this);
        float f3 = ScaleImageView.-wrap1(ScaleImageView.this);
        float f5 = ScaleImageView.-wrap2(ScaleImageView.this);
        float f2;
        float f4;
        if (ScaleImageView.-get12(ScaleImageView.this) == 0)
        {
          f2 = ScaleImageView.-get20(ScaleImageView.this) - f7;
          f4 = ScaleImageView.-get19(ScaleImageView.this) - f1;
          if (f3 > 0.0F)
          {
            f2 = 0.0F;
            if (f1 <= ScaleImageView.-get19(ScaleImageView.this)) {
              break label348;
            }
            f1 = f5 + (ScaleImageView.-get19(ScaleImageView.this) / 2 - ScaleImageView.-get18(ScaleImageView.this).y);
            if (f1 >= f4) {
              break label334;
            }
            f1 = f4;
          }
        }
        for (;;)
        {
          f2 = (paramAnonymousValueAnimator.floatValue() - 1.0F) * (f2 - f3) / 2.0F;
          f1 = (paramAnonymousValueAnimator.floatValue() - 1.0F) * (f1 - f5) / 2.0F;
          ScaleImageView.-wrap5(ScaleImageView.this, f3 + f2);
          ScaleImageView.-wrap6(ScaleImageView.this, f5 + f1);
          ScaleImageView.this.setImageMatrix(ScaleImageView.-get6(ScaleImageView.this));
          return;
          if (f3 < ScaleImageView.-get20(ScaleImageView.this) - f7)
          {
            f2 = ScaleImageView.-get20(ScaleImageView.this) - f7;
            break;
          }
          float f6 = f3 + (ScaleImageView.-get20(ScaleImageView.this) / 2 - ScaleImageView.-get18(ScaleImageView.this).x);
          if (f6 < f2) {
            break;
          }
          if (f6 > 0.0F)
          {
            f2 = 0.0F;
            break;
          }
          f2 = f6;
          break;
          label334:
          if (f1 > 0.0F)
          {
            f1 = 0.0F;
          }
          else
          {
            continue;
            label348:
            f1 = (ScaleImageView.-get19(ScaleImageView.this) - f1) / 2.0F;
            continue;
            f2 = ScaleImageView.-get20(ScaleImageView.this);
            f4 = ScaleImageView.-get19(ScaleImageView.this) - f7;
            if (f1 >= ScaleImageView.-get20(ScaleImageView.this)) {
              if (f3 <= f1) {}
            }
            for (;;)
            {
              if (ScaleImageView.-get19(ScaleImageView.this) <= f7) {
                break label529;
              }
              f4 = (ScaleImageView.-get19(ScaleImageView.this) - f7) / 2.0F;
              f2 = f1;
              f1 = f4;
              break;
              if (f3 < ScaleImageView.-get20(ScaleImageView.this))
              {
                f1 = ScaleImageView.-get20(ScaleImageView.this);
              }
              else
              {
                f6 = f3 + (ScaleImageView.-get20(ScaleImageView.this) / 2 - ScaleImageView.-get18(ScaleImageView.this).x);
                if (f6 < f2)
                {
                  f1 = f2;
                }
                else if (f6 <= f1)
                {
                  f1 = f6;
                  continue;
                  f1 = f3;
                }
              }
            }
            label529:
            if (f5 < ScaleImageView.-get19(ScaleImageView.this) - f7)
            {
              f4 = ScaleImageView.-get19(ScaleImageView.this) - f7;
              f2 = f1;
              f1 = f4;
            }
            else if (f5 > 0.0F)
            {
              f4 = 0.0F;
              f2 = f1;
              f1 = f4;
            }
            else
            {
              f2 = f5 + (ScaleImageView.-get19(ScaleImageView.this) / 2 - ScaleImageView.-get18(ScaleImageView.this).y);
              if (f2 < f4)
              {
                f2 = f1;
                f1 = f4;
              }
              else if (f2 > 0.0F)
              {
                f4 = 0.0F;
                f2 = f1;
                f1 = f4;
              }
              else
              {
                f4 = f2;
                f2 = f1;
                f1 = f4;
              }
            }
          }
        }
      }
    });
    this.mAnimator.addListener(new Animator.AnimatorListener()
    {
      public void onAnimationCancel(Animator paramAnonymousAnimator)
      {
        Log.d(ScaleImageView.TAG, "onAnimationCancel");
        ScaleImageView.-set0(ScaleImageView.this, null);
        ScaleImageView.-get4(ScaleImageView.this).setSwipeable(true);
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        ScaleImageView.-set0(ScaleImageView.this, null);
        ScaleImageView.-wrap7(ScaleImageView.this);
      }
      
      public void onAnimationRepeat(Animator paramAnonymousAnimator) {}
      
      public void onAnimationStart(Animator paramAnonymousAnimator)
      {
        ScaleImageView.-get4(ScaleImageView.this).setSwipeable(false);
      }
    });
    this.mAnimator.start();
  }
  
  private void animateZoomEnd()
  {
    final float f1 = getScale();
    final float f3 = f1 * this.m_DrawableWidth;
    f1 *= this.m_DrawableHeight;
    final float f2 = getTranslateX();
    final float f4 = getTranslateY();
    if (this.m_Oriental == 0) {
      if (f2 > 0.0F)
      {
        f3 = 0.0F;
        if (f1 <= this.m_ViewHeight) {
          break label150;
        }
        if (f4 >= this.m_ViewHeight - f1) {
          break label132;
        }
        f1 = this.m_ViewHeight - f1;
      }
    }
    for (;;)
    {
      f3 -= f2;
      f1 -= f4;
      if ((f3 != 0.0F) || (f1 != 0.0F)) {
        break label297;
      }
      handleUp();
      return;
      if (f2 < this.m_ViewWidth - f3)
      {
        f3 = this.m_ViewWidth - f3;
        break;
      }
      f3 = f2;
      break;
      label132:
      if (f4 > 0.0F)
      {
        f1 = 0.0F;
      }
      else
      {
        f1 = f4;
        continue;
        label150:
        f1 = (this.m_ViewHeight - f1) / 2.0F;
        continue;
        if (f1 >= this.m_ViewWidth) {
          if (f2 <= f1) {}
        }
        float f5;
        for (;;)
        {
          if (this.m_ViewHeight <= f3) {
            break label237;
          }
          f5 = (this.m_ViewHeight - f3) / 2.0F;
          f3 = f1;
          f1 = f5;
          break;
          if (f2 < this.m_ViewWidth)
          {
            f1 = this.m_ViewWidth;
          }
          else
          {
            f1 = f2;
            continue;
            f1 = f2;
          }
        }
        label237:
        if (f4 < this.m_ViewHeight - f3)
        {
          f5 = this.m_ViewHeight - f3;
          f3 = f1;
          f1 = f5;
        }
        else if (f4 > 0.0F)
        {
          f5 = 0.0F;
          f3 = f1;
          f1 = f5;
        }
        else
        {
          f5 = f4;
          f3 = f1;
          f1 = f5;
        }
      }
    }
    label297:
    this.mAnimator = ValueAnimator.ofFloat(new float[] { 0.0F, 1000.0F });
    this.mAnimator.setDuration(100L);
    this.mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        paramAnonymousValueAnimator = (Float)paramAnonymousValueAnimator.getAnimatedValue();
        ScaleImageView.-wrap5(ScaleImageView.this, f2 + f3 * paramAnonymousValueAnimator.floatValue() / 1000.0F);
        ScaleImageView.-wrap6(ScaleImageView.this, f4 + f1 * paramAnonymousValueAnimator.floatValue() / 1000.0F);
        ScaleImageView.this.setImageMatrix(ScaleImageView.-get6(ScaleImageView.this));
      }
    });
    this.mAnimator.addListener(new Animator.AnimatorListener()
    {
      public void onAnimationCancel(Animator paramAnonymousAnimator)
      {
        ScaleImageView.-get4(ScaleImageView.this).setSwipeable(true);
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        ScaleImageView.-set0(ScaleImageView.this, null);
        ScaleImageView.-wrap4(ScaleImageView.this);
      }
      
      public void onAnimationRepeat(Animator paramAnonymousAnimator) {}
      
      public void onAnimationStart(Animator paramAnonymousAnimator)
      {
        ScaleImageView.-get4(ScaleImageView.this).setSwipeable(false);
      }
    });
    this.mAnimator.start();
  }
  
  private void cutting()
  {
    float f2 = getScale();
    Log.d(TAG, " getScale() " + f2);
    float f1 = f2 * this.m_DrawableWidth;
    f2 *= this.m_DrawableHeight;
    float f3 = getTranslateX();
    float f4 = getTranslateY();
    if (this.m_Oriental == 0) {
      if (f1 >= this.m_ViewWidth) {
        if (f3 >= 0.0F) {
          setTranslateX(0.0F);
        }
      }
    }
    label108:
    label200:
    label367:
    do
    {
      do
      {
        do
        {
          do
          {
            break label108;
            break label108;
            for (;;)
            {
              if (f2 < this.m_ViewHeight) {
                break label200;
              }
              if (f4 < 0.0F) {
                break;
              }
              setTranslateY(0.0F);
              return;
              if (f3 + f1 < this.m_ViewWidth)
              {
                setTranslateX(this.m_ViewWidth - f1);
                continue;
                if (f3 <= 0.0F) {
                  setTranslateX(0.0F);
                } else if (f3 + f1 > this.m_ViewWidth) {
                  setTranslateX(this.m_ViewWidth - f1);
                }
              }
            }
          } while (f4 + f2 >= this.m_ViewHeight);
          setTranslateY(this.m_ViewHeight - f2);
          return;
          if (f4 <= 0.0F)
          {
            setTranslateY(0.0F);
            return;
          }
        } while (f4 + f2 <= this.m_ViewHeight);
        setTranslateY(this.m_ViewHeight - f2);
        return;
        if (f2 >= this.m_ViewWidth) {
          if (f3 <= this.m_ViewWidth) {
            setTranslateX(this.m_ViewWidth);
          }
        }
        for (;;)
        {
          if (f1 < this.m_ViewHeight) {
            break label367;
          }
          if (f4 < 0.0F) {
            break;
          }
          setTranslateY(0.0F);
          return;
          if (f2 < f3)
          {
            setTranslateX(f2);
            continue;
            if (f3 >= this.m_ViewWidth) {
              setTranslateX(this.m_ViewWidth);
            } else if (f3 < f2) {
              setTranslateX(f2);
            }
          }
        }
      } while (getTranslateY() + f1 >= this.m_ViewHeight);
      setTranslateY(this.m_ViewHeight - f1);
      return;
      if (f4 <= 0.0F)
      {
        setTranslateY(0.0F);
        return;
      }
    } while (getTranslateY() + f1 <= this.m_ViewHeight);
    setTranslateY(this.m_ViewHeight - f1);
  }
  
  private void finishLandscape()
  {
    if (this.m_PaddingX == this.m_MinX) {}
    for (this.m_IsDeleted = true;; this.m_IsDeleted = false)
    {
      this.m_PreviousX = 0;
      return;
      if (this.m_PaddingX != this.m_MaxX) {
        break;
      }
    }
    if (this.m_PaddingX > this.m_MidX) {
      this.mAnimator = ValueAnimator.ofInt(new int[] { this.m_PaddingX, this.m_MaxX });
    }
    for (;;)
    {
      this.mAnimator.setDuration(180L);
      this.mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
      {
        public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
        {
          paramAnonymousValueAnimator = (Integer)paramAnonymousValueAnimator.getAnimatedValue();
          ScaleImageView.-set4(ScaleImageView.this, paramAnonymousValueAnimator.intValue());
          ScaleImageView.-get21(ScaleImageView.this).scrollTo(ScaleImageView.-get13(ScaleImageView.this), 0);
          ScaleImageView.-get21(ScaleImageView.this).setAlpha(1.0F - Math.abs(ScaleImageView.-get13(ScaleImageView.this) / ScaleImageView.-get9(ScaleImageView.this)));
          if (ScaleImageView.-get13(ScaleImageView.this) == ScaleImageView.-get9(ScaleImageView.this))
          {
            ScaleImageView.-set3(ScaleImageView.this, true);
            ScaleImageView.-get21(ScaleImageView.this).setAlpha(1.0F);
            HandlerUtils.sendMessage(ScaleImageView.-get4(ScaleImageView.this), 1002, 0, 0, new File(ScaleImageView.-get15(ScaleImageView.this)));
          }
          for (;;)
          {
            Log.d(ScaleImageView.TAG, "isOpened: " + ScaleImageView.-get5(ScaleImageView.this));
            return;
            if (ScaleImageView.-get13(ScaleImageView.this) == ScaleImageView.-get7(ScaleImageView.this)) {
              ScaleImageView.-set3(ScaleImageView.this, false);
            }
          }
        }
      });
      this.mAnimator.addListener(new Animator.AnimatorListener()
      {
        public void onAnimationCancel(Animator paramAnonymousAnimator)
        {
          ScaleImageView.-get4(ScaleImageView.this).setSwipeable(true);
        }
        
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          ScaleImageView.-set0(ScaleImageView.this, null);
          ScaleImageView.-get4(ScaleImageView.this).setSwipeable(true);
        }
        
        public void onAnimationRepeat(Animator paramAnonymousAnimator) {}
        
        public void onAnimationStart(Animator paramAnonymousAnimator)
        {
          ScaleImageView.-get4(ScaleImageView.this).setSwipeable(false);
        }
      });
      this.mAnimator.start();
      break;
      if (this.m_PaddingX <= this.m_MidX) {
        this.mAnimator = ValueAnimator.ofInt(new int[] { this.m_PaddingX, this.m_MinX });
      }
    }
  }
  
  private void finishPortrait()
  {
    if (this.m_PaddingY == this.m_MinY) {}
    for (this.m_IsDeleted = true;; this.m_IsDeleted = false)
    {
      this.m_PreviousY = 0;
      return;
      if (this.m_PaddingY != this.m_MaxY) {
        break;
      }
    }
    if (this.m_PaddingY > this.m_MidY) {
      this.mAnimator = ValueAnimator.ofInt(new int[] { this.m_PaddingY, this.m_MaxY });
    }
    for (;;)
    {
      this.mAnimator.setDuration(180L);
      this.mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
      {
        public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
        {
          paramAnonymousValueAnimator = (Integer)paramAnonymousValueAnimator.getAnimatedValue();
          ScaleImageView.-set5(ScaleImageView.this, paramAnonymousValueAnimator.intValue());
          ScaleImageView.-get21(ScaleImageView.this).scrollTo(0, ScaleImageView.-get14(ScaleImageView.this) * -1);
          ScaleImageView.-get21(ScaleImageView.this).setAlpha(1.0F - Math.abs(ScaleImageView.-get14(ScaleImageView.this) / ScaleImageView.-get10(ScaleImageView.this)));
          if (ScaleImageView.-get14(ScaleImageView.this) == ScaleImageView.-get10(ScaleImageView.this))
          {
            ScaleImageView.-set3(ScaleImageView.this, true);
            ScaleImageView.-get21(ScaleImageView.this).setAlpha(1.0F);
            HandlerUtils.sendMessage(ScaleImageView.-get4(ScaleImageView.this), 1002, 0, 0, new File(ScaleImageView.-get15(ScaleImageView.this)));
          }
          for (;;)
          {
            Log.d(ScaleImageView.TAG, "isOpened: " + ScaleImageView.-get5(ScaleImageView.this));
            return;
            if (ScaleImageView.-get14(ScaleImageView.this) == ScaleImageView.-get8(ScaleImageView.this)) {
              ScaleImageView.-set3(ScaleImageView.this, false);
            }
          }
        }
      });
      this.mAnimator.addListener(new Animator.AnimatorListener()
      {
        public void onAnimationCancel(Animator paramAnonymousAnimator)
        {
          ScaleImageView.-get4(ScaleImageView.this).setSwipeable(true);
        }
        
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          ScaleImageView.-set4(ScaleImageView.this, ScaleImageView.-get7(ScaleImageView.this));
          ScaleImageView.-set5(ScaleImageView.this, ScaleImageView.-get8(ScaleImageView.this));
          ScaleImageView.-set0(ScaleImageView.this, null);
          ScaleImageView.-get4(ScaleImageView.this).setSwipeable(true);
        }
        
        public void onAnimationRepeat(Animator paramAnonymousAnimator) {}
        
        public void onAnimationStart(Animator paramAnonymousAnimator)
        {
          ScaleImageView.-get4(ScaleImageView.this).setSwipeable(false);
        }
      });
      this.mAnimator.start();
      break;
      if (this.m_PaddingY <= this.m_MidY) {
        this.mAnimator = ValueAnimator.ofInt(new int[] { this.m_PaddingY, this.m_MinY });
      }
    }
  }
  
  private Rotation getLayoutRotation()
  {
    return (Rotation)this.m_CameraActivity.get(CameraActivity.PROP_ROTATION);
  }
  
  private float getScale()
  {
    if (this.m_Oriental == 0) {
      return getValue(this.m_Matrix, 0);
    }
    return getValue(this.m_Matrix, 3);
  }
  
  private float getTranslateX()
  {
    return getValue(this.m_Matrix, 2);
  }
  
  private float getTranslateY()
  {
    return getValue(this.m_Matrix, 5);
  }
  
  private float getValue(Matrix paramMatrix, int paramInt)
  {
    paramMatrix.getValues(this.m_MatrixValues);
    return this.m_MatrixValues[paramInt];
  }
  
  private void handleClick()
  {
    if (this.waitDouble)
    {
      this.waitDouble = false;
      new AsyncTask()
      {
        protected Void doInBackground(Void... paramAnonymousVarArgs)
        {
          try
          {
            Thread.sleep(350L);
            return null;
          }
          catch (InterruptedException paramAnonymousVarArgs)
          {
            for (;;)
            {
              paramAnonymousVarArgs.printStackTrace();
              ScaleImageView.-set6(ScaleImageView.this, true);
            }
          }
        }
        
        protected void onPostExecute(Void paramAnonymousVoid)
        {
          if (!ScaleImageView.-get22(ScaleImageView.this))
          {
            ScaleImageView.-set6(ScaleImageView.this, true);
            if (ScaleImageView.-get11(ScaleImageView.this) == 0) {
              ScaleImageView.this.performClick();
            }
          }
        }
      }.execute(new Void[0]);
      return;
    }
    if (this.m_Mode == 0)
    {
      if (this.m_IsVideo) {
        return;
      }
      updateImageToFullSize();
      animateToViewer();
    }
    for (;;)
    {
      this.waitDouble = true;
      return;
      if (this.m_IsVideo) {
        return;
      }
      updateImageToInitialSize();
      animateToSlider();
    }
  }
  
  private void handleUp()
  {
    if ((this.m_TouchMode == 2) && (getScale() == this.m_MinScale)) {
      toggleMode();
    }
    this.m_TouchMode = 0;
    Log.d(TAG, "mode=NONE");
    if (this.m_Click) {
      handleClick();
    }
    while ((this.m_IsZoomIn) || (this.m_Mode == 1)) {
      return;
    }
    updateImageToInitialSize();
  }
  
  private void midPoint(PointF paramPointF, MotionEvent paramMotionEvent)
  {
    float f1 = paramMotionEvent.getX(0);
    float f2 = paramMotionEvent.getX(1);
    float f3 = paramMotionEvent.getY(0);
    float f4 = paramMotionEvent.getY(1);
    paramPointF.set((f1 + f2) / 2.0F, (f3 + f4) / 2.0F);
  }
  
  private void recomputeImgMatrix()
  {
    if (getDrawable() == null)
    {
      Log.d(TAG, "recomputeImgMatrix getDrawable() == null");
      return;
    }
    int i = getWidth() - getPaddingLeft() - getPaddingRight();
    int j = getHeight() - getPaddingTop() - getPaddingBottom();
    if (i != 0)
    {
      this.m_ViewWidth = i;
      this.m_Center.x = (i / 2);
    }
    if (j != 0)
    {
      this.m_ViewHeight = j;
      this.m_Center.y = (j / 2);
    }
    int k = getDrawable().getIntrinsicWidth();
    int m = getDrawable().getIntrinsicHeight();
    if (k != 0) {
      this.m_DrawableWidth = k;
    }
    if (m != 0) {
      this.m_DrawableHeight = m;
    }
    this.m_MinY = (j * -1);
    this.m_MidY = (this.m_MinY / 3);
    this.m_MinX = (i * -1);
    this.m_MidX = (this.m_MinX / 3);
    if (this.m_Mode == 0) {
      setMatrixCenterFit(k, m, i, j);
    }
  }
  
  private float scaling(MotionEvent paramMotionEvent)
  {
    float f2 = spacing(paramMotionEvent) / this.m_OldDist;
    float f3 = getScale();
    float f1;
    if (f3 * f2 > this.m_MaxScale) {
      f1 = this.m_MaxScale / f3;
    }
    do
    {
      return f1;
      f1 = f2;
    } while (f3 * f2 >= this.m_MinScale);
    return this.m_MinScale / f3;
  }
  
  private void setMatrixCenterFit(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Matrix localMatrix = getImageMatrix();
    RectF localRectF = new RectF(0.0F, 0.0F, paramInt1, paramInt2);
    if (this.m_Oriental == 0) {
      localMatrix.setRectToRect(localRectF, new RectF(0.0F, 0.0F, paramInt3, paramInt4), Matrix.ScaleToFit.CENTER);
    }
    for (;;)
    {
      setImageMatrix(localMatrix);
      this.m_Matrix.set(localMatrix);
      this.m_MinMatrix.set(localMatrix);
      this.m_MinScale = getScale();
      this.m_MaxScale = (this.m_MinScale * 5.0F);
      return;
      localMatrix.setRectToRect(localRectF, new RectF(-paramInt4 / 2, -paramInt3 / 2, paramInt4 / 2, paramInt3 / 2), Matrix.ScaleToFit.CENTER);
      localMatrix.postRotate(90.0F);
      localMatrix.postTranslate(paramInt3 / 2, paramInt4 / 2);
    }
  }
  
  private void setTranslateX(float paramFloat)
  {
    this.m_Matrix.getValues(this.m_MatrixValues);
    this.m_MatrixValues[2] = paramFloat;
    this.m_Matrix.setValues(this.m_MatrixValues);
  }
  
  private void setTranslateY(float paramFloat)
  {
    this.m_Matrix.getValues(this.m_MatrixValues);
    this.m_MatrixValues[5] = paramFloat;
    this.m_Matrix.setValues(this.m_MatrixValues);
  }
  
  private boolean sliderTouchHandler(MotionEvent paramMotionEvent)
  {
    int i = 1;
    if ((getLayoutRotation() == Rotation.INVERSE_PORTRAIT) || (getLayoutRotation() == Rotation.INVERSE_LANDSCAPE)) {
      i = -1;
    }
    switch (paramMotionEvent.getAction() & 0xFF)
    {
    case 4: 
    default: 
      return false;
    case 0: 
      this.m_PreviousX = 0;
      this.m_PreviousY = 0;
      this.m_Click = true;
      this.m_SavedMatrix.set(this.m_Matrix);
      this.m_Start.set(paramMotionEvent.getX(), paramMotionEvent.getY());
      return true;
    case 5: 
      if ((this.m_PaddingX == 0) && (this.m_PaddingY == 0))
      {
        this.m_OldDist = spacing(paramMotionEvent);
        Log.d(TAG, "oldDist=" + this.m_OldDist);
        if (this.m_OldDist > 5.0F)
        {
          this.m_SavedMatrix.set(this.m_Matrix);
          midPoint(this.m_Mid, paramMotionEvent);
          this.m_TouchMode = 2;
          toggleMode();
          Log.d(TAG, "mode=ZOOM");
          updateImageToFullSize();
        }
        return true;
      }
      if (this.m_Oriental == 0) {
        finishPortrait();
      }
      for (;;)
      {
        this.m_Click = false;
        return true;
        finishLandscape();
      }
    case 2: 
      if (this.m_PreviousY == 0)
      {
        this.m_DiffY = 0;
        this.m_PreviousY = ((int)paramMotionEvent.getRawY() * i);
        return true;
      }
      if (this.m_PreviousX == 0)
      {
        this.m_DiffX = 0;
        this.m_PreviousX = ((int)paramMotionEvent.getRawX() * i);
        return true;
      }
      this.m_DiffY = ((int)paramMotionEvent.getRawY() * i - this.m_PreviousY);
      this.m_DiffX = ((int)paramMotionEvent.getRawX() * i - this.m_PreviousX);
      this.m_PaddingY += this.m_DiffY;
      this.m_PaddingX -= this.m_DiffX;
      if (this.m_PaddingY > this.m_MaxY)
      {
        this.m_PaddingY = this.m_MaxY;
        if (this.m_PaddingX <= this.m_MaxX) {
          break label518;
        }
        this.m_PaddingX = this.m_MaxX;
        this.m_PreviousY = ((int)paramMotionEvent.getRawY() * i);
        this.m_PreviousX = ((int)paramMotionEvent.getRawX() * i);
        if (this.m_Oriental != 0) {
          break label540;
        }
        if (Math.abs(this.m_PaddingY) > 20)
        {
          this.m_parent.scrollTo(0, this.m_PaddingY * -1);
          this.m_parent.setAlpha(1.0F - Math.abs(this.m_PaddingY / this.m_MinY));
        }
        if (Math.abs(this.m_MaxY - this.m_PaddingY) > 6) {
          this.m_Click = false;
        }
      }
      do
      {
        return true;
        if (this.m_PaddingY >= this.m_MinY) {
          break;
        }
        this.m_PaddingY = this.m_MinY;
        break;
        if (this.m_PaddingX >= this.m_MinX) {
          break label394;
        }
        this.m_PaddingX = this.m_MinX;
        break label394;
        if (Math.abs(this.m_PaddingX) > 20)
        {
          this.m_parent.scrollTo(this.m_PaddingX, 0);
          this.m_parent.setAlpha(1.0F - Math.abs(this.m_PaddingX / this.m_MinX));
        }
      } while (Math.abs(this.m_MaxX - this.m_PaddingX) <= 6);
      this.m_Click = false;
      return true;
    case 3: 
      label394:
      label518:
      label540:
      if (this.m_Oriental == 0) {
        finishPortrait();
      }
      for (;;)
      {
        this.m_Click = false;
        return true;
        finishLandscape();
      }
    }
    if (this.m_Oriental == 0) {
      finishPortrait();
    }
    for (;;)
    {
      if (this.m_Click) {
        handleClick();
      }
      return true;
      finishLandscape();
    }
  }
  
  private float spacing(MotionEvent paramMotionEvent)
  {
    float f1 = paramMotionEvent.getX(0) - paramMotionEvent.getX(1);
    float f2 = paramMotionEvent.getY(0) - paramMotionEvent.getY(1);
    return (float)Math.sqrt(f1 * f1 + f2 * f2);
  }
  
  private void toggleMode()
  {
    this.m_Click = false;
    if (this.m_Mode == 0)
    {
      this.m_Mode = 1;
      this.m_Gallery.setSwipeable(false);
      return;
    }
    this.m_Mode = 0;
    this.m_Gallery.setSwipeable(true);
    updateImageToInitialSize();
    requestLayout();
  }
  
  private void updateImageToFullSize()
  {
    Log.d(TAG, "updateImageToFullSize()");
    if ((!this.m_IsBitmapFullSize) && (this.m_DecodeTask == null))
    {
      this.m_DecodeTask = new BitmapWorkerTask(this);
      this.m_DecodeTask.execute(new String[] { this.m_Path });
    }
  }
  
  private void updateImageToInitialSize()
  {
    Log.d(TAG, "updateImageToInitialSize() m_IsBitmapFullSize " + this.m_IsBitmapFullSize);
    if (!this.m_IsBitmapFullSize) {
      cancelDeocdingFullSizeImage();
    }
    if (this.m_ScaleDrawable != null) {
      this.m_ScaleDrawable.setBitmap(this.m_InitialBitmap);
    }
    this.m_IsBitmapFullSize = false;
  }
  
  private boolean viewerTouchHandler(MotionEvent paramMotionEvent)
  {
    switch (paramMotionEvent.getAction() & 0xFF)
    {
    }
    float f;
    do
    {
      do
      {
        for (;;)
        {
          setImageMatrix(this.m_Matrix);
          return true;
          this.m_SavedMatrix.set(this.m_Matrix);
          this.m_Start.set(paramMotionEvent.getX(), paramMotionEvent.getY());
          Log.d(TAG, "mode=DRAG");
          this.m_TouchMode = 1;
          this.m_Click = true;
          continue;
          animateZoomEnd();
          Log.d(TAG, "ACTION_UP");
          continue;
          Log.d(TAG, "ACTION_POINTER_UP");
          continue;
          this.m_Click = false;
          this.m_OldDist = spacing(paramMotionEvent);
          Log.d(TAG, "oldDist=" + this.m_OldDist);
          if (this.m_OldDist > 5.0F)
          {
            this.m_SavedMatrix.set(this.m_Matrix);
            midPoint(this.m_Mid, paramMotionEvent);
            this.m_TouchMode = 2;
            Log.d(TAG, "mode=ZOOM");
            continue;
            if (this.m_TouchMode != 1) {
              break;
            }
            this.m_Matrix.set(this.m_SavedMatrix);
            if (getLeft() >= -this.m_ViewWidth)
            {
              Log.d(TAG, "postTranslate: " + (paramMotionEvent.getX() - this.m_Start.x));
              this.m_Matrix.postTranslate(paramMotionEvent.getX() - this.m_Start.x, paramMotionEvent.getY() - this.m_Start.y);
              cutting();
            }
          }
        }
      } while ((this.m_TouchMode != 2) || (this.m_IsVideo));
      f = spacing(paramMotionEvent);
      Log.d(TAG, "newDist=" + f);
    } while (f <= 5.0F);
    this.m_Matrix.set(this.m_SavedMatrix);
    if (this.m_ViewerPreviousDist == -1.0F) {
      this.m_ViewerPreviousDist = f;
    }
    zoomTo(scaling(paramMotionEvent), this.m_Mid);
    if (f - this.m_ViewerPreviousDist >= 0.0F) {}
    for (this.m_IsZoomIn = true;; this.m_IsZoomIn = false)
    {
      this.m_ViewerPreviousDist = f;
      break;
    }
  }
  
  private void zoomTo(float paramFloat, PointF paramPointF)
  {
    this.m_Matrix.postScale(paramFloat, paramFloat, paramPointF.x, paramPointF.y);
  }
  
  public void cancelDeocdingFullSizeImage()
  {
    if (this.m_DecodeTask != null)
    {
      Log.d(TAG, "cancelDeocdingFullSizeImage() - Cancel decode task");
      this.m_DecodeTask.cancel(true);
      this.m_DecodeTask = null;
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.m_parent = ((View)getParent());
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    recomputeImgMatrix();
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (this.mAnimator != null)
    {
      Log.d(TAG, "mAnimator != null");
      return true;
    }
    if ((paramMotionEvent.getAction() == 0) && (this.m_Gallery != null)) {
      this.m_Gallery.hideUndoDeletionBar(true);
    }
    switch (this.m_Mode)
    {
    default: 
      return super.onTouchEvent(paramMotionEvent);
    case 0: 
      return sliderTouchHandler(paramMotionEvent);
    }
    return viewerTouchHandler(paramMotionEvent);
  }
  
  public void reset()
  {
    this.m_Mode = 0;
    this.m_PreviousX = 0;
    this.m_PaddingX = 0;
    this.m_DiffX = 0;
    this.m_PreviousY = 0;
    this.m_PaddingY = 0;
    this.m_DiffY = 0;
    if (this.m_parent != null)
    {
      this.m_parent.setAlpha(1.0F);
      this.m_parent.scrollTo(0, 0);
    }
    if (this.m_Gallery != null) {
      this.m_Gallery.setSwipeable(true);
    }
    updateImageToInitialSize();
    requestLayout();
  }
  
  protected boolean setFrame(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    recomputeImgMatrix();
    return super.setFrame(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public boolean setPhoto(Bitmap paramBitmap, String paramString, PreviewGallery paramPreviewGallery, boolean paramBoolean)
  {
    reset();
    if (TextUtils.isEmpty(paramString)) {
      return false;
    }
    if (paramPreviewGallery == null) {
      return false;
    }
    this.m_Path = paramString;
    this.m_Gallery = paramPreviewGallery;
    this.m_CameraActivity = paramPreviewGallery.getCameraActivity();
    this.m_IsVideo = paramBoolean;
    this.m_InitialBitmap = paramBitmap;
    this.m_ScaleDrawable = new ScaleImageViewDrawable(paramBitmap);
    setImageDrawable(this.m_ScaleDrawable);
    requestLayout();
    return true;
  }
  
  class BitmapWorkerTask
    extends AsyncTask<String, Void, Bitmap>
  {
    private final WeakReference<ImageView> imageViewReference;
    private String path = null;
    
    public BitmapWorkerTask(ImageView paramImageView)
    {
      this.imageViewReference = new WeakReference(paramImageView);
    }
    
    protected Bitmap doInBackground(String... paramVarArgs)
    {
      this.path = paramVarArgs[0];
      return ImageUtils.decodeBitmap(this.path, 4096, 4096, 4, Bitmap.Config.ARGB_8888);
    }
    
    protected void onPostExecute(Bitmap paramBitmap)
    {
      if (ScaleImageView.-get1(ScaleImageView.this) == this)
      {
        if ((this.imageViewReference != null) && (paramBitmap != null) && ((ImageView)this.imageViewReference.get() != null) && (ScaleImageView.-get17(ScaleImageView.this) != null) && (ScaleImageView.-get11(ScaleImageView.this) == 1))
        {
          ScaleImageView.-get17(ScaleImageView.this).setBitmap(paramBitmap);
          ScaleImageView.-set2(ScaleImageView.this, true);
        }
        ScaleImageView.-set1(ScaleImageView.this, null);
        return;
      }
      Log.d(ScaleImageView.TAG, "Not the same task");
    }
  }
  
  public class ScaleImageViewDrawable
    extends Drawable
  {
    private BitmapDrawable m_BitmapDrawable = null;
    
    public ScaleImageViewDrawable(Bitmap paramBitmap)
    {
      if (paramBitmap != null) {
        this.m_BitmapDrawable = new BitmapDrawable(paramBitmap);
      }
    }
    
    public void draw(Canvas paramCanvas)
    {
      if (this.m_BitmapDrawable != null)
      {
        this.m_BitmapDrawable.setBounds(getBounds());
        this.m_BitmapDrawable.draw(paramCanvas);
      }
    }
    
    public int getIntrinsicHeight()
    {
      if (this.m_BitmapDrawable != null) {
        return this.m_BitmapDrawable.getIntrinsicHeight();
      }
      return 0;
    }
    
    public int getIntrinsicWidth()
    {
      if (this.m_BitmapDrawable != null) {
        return this.m_BitmapDrawable.getIntrinsicWidth();
      }
      return 0;
    }
    
    public int getOpacity()
    {
      if (this.m_BitmapDrawable != null) {
        return this.m_BitmapDrawable.getOpacity();
      }
      return 0;
    }
    
    public void setAlpha(int paramInt)
    {
      if (this.m_BitmapDrawable != null) {
        this.m_BitmapDrawable.setAlpha(paramInt);
      }
    }
    
    public void setBitmap(Bitmap paramBitmap)
    {
      if (paramBitmap != null)
      {
        this.m_BitmapDrawable = new BitmapDrawable(paramBitmap);
        invalidateSelf();
      }
    }
    
    public void setColorFilter(ColorFilter paramColorFilter)
    {
      if (this.m_BitmapDrawable != null) {
        this.m_BitmapDrawable.setColorFilter(paramColorFilter);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/ScaleImageView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */