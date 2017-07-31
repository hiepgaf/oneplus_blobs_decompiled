package com.oneplus.camera.drawable;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

public class RecordingButtonIconDrawable
  extends Drawable
{
  private static final BitmapFactory.Options BITMAP_OPTIONS = new BitmapFactory.Options();
  public static final long DURATION_ANIMATION = 300L;
  private static final Interpolator INTERPOLATOR_END = new DecelerateInterpolator(2.0F);
  private static final Interpolator INTERPOLATOR_START;
  private static final int STATE_READY = 0;
  private static final int STATE_RECORDING = 2;
  private static final int STATE_STARTING = 1;
  private static final int STATE_STOPPING = 3;
  private int m_Alpha = 255;
  private long m_AnimationDuration;
  private final Runnable m_AnimationRunnable = new Runnable()
  {
    public void run()
    {
      RecordingButtonIconDrawable.-wrap0(RecordingButtonIconDrawable.this);
    }
  };
  private long m_AnimationStartTime;
  private Bitmap m_BufferBitmap;
  private Paint m_BufferBitmapPaint;
  private Drawable m_CenterDrawable;
  private float m_CurrentRadius;
  private float m_OriginalRadius;
  private final Paint m_Paint = new Paint();
  private int m_State = 0;
  
  static
  {
    BITMAP_OPTIONS.inTargetDensity = 480;
    INTERPOLATOR_START = new AccelerateInterpolator(1.2F);
  }
  
  public RecordingButtonIconDrawable(Context paramContext)
  {
    this(paramContext, new BitmapDrawable(BitmapFactory.decodeResource(paramContext.getResources(), 2130837529, BITMAP_OPTIONS)));
  }
  
  public RecordingButtonIconDrawable(Context paramContext, int paramInt, float paramFloat)
  {
    this.m_OriginalRadius = paramFloat;
    this.m_CurrentRadius = paramFloat;
    this.m_Paint.setStyle(Paint.Style.FILL);
    this.m_Paint.setAntiAlias(true);
    this.m_Paint.setColor(paramInt);
  }
  
  public RecordingButtonIconDrawable(Context paramContext, Drawable paramDrawable)
  {
    paramContext = paramContext.getResources();
    this.m_OriginalRadius = paramContext.getDimension(2131296507);
    this.m_CurrentRadius = this.m_OriginalRadius;
    this.m_CenterDrawable = paramDrawable;
    this.m_Paint.setStyle(Paint.Style.FILL);
    this.m_Paint.setAntiAlias(true);
    this.m_Paint.setColor(paramContext.getColor(2131230726));
  }
  
  private void animate()
  {
    float f;
    switch (this.m_State)
    {
    case 2: 
    default: 
      return;
    case 1: 
      l1 = SystemClock.uptimeMillis();
      l2 = l1 - this.m_AnimationStartTime;
      if (l2 < this.m_AnimationDuration)
      {
        f = (float)l2 / (float)this.m_AnimationDuration;
        this.m_CurrentRadius = ((1.0F - INTERPOLATOR_START.getInterpolation(f)) * this.m_OriginalRadius);
        scheduleSelf(this.m_AnimationRunnable, l1 + 30L);
      }
      for (;;)
      {
        invalidateSelf();
        return;
        this.m_CurrentRadius = 0.0F;
        this.m_State = 2;
      }
    }
    long l1 = SystemClock.uptimeMillis();
    long l2 = l1 - this.m_AnimationStartTime;
    if (l2 < this.m_AnimationDuration)
    {
      f = (float)l2 / (float)this.m_AnimationDuration;
      this.m_CurrentRadius = (INTERPOLATOR_END.getInterpolation(f) * this.m_OriginalRadius);
      scheduleSelf(this.m_AnimationRunnable, l1 + 30L);
    }
    for (;;)
    {
      invalidateSelf();
      return;
      this.m_CurrentRadius = this.m_OriginalRadius;
      this.m_State = 0;
    }
  }
  
  public void draw(Canvas paramCanvas)
  {
    int i = (int)Math.ceil(this.m_OriginalRadius * 2.0F);
    if ((this.m_BufferBitmap == null) || (this.m_BufferBitmap.getWidth() != i)) {
      this.m_BufferBitmap = Bitmap.createBitmap(i, i, Bitmap.Config.ARGB_8888);
    }
    if (this.m_BufferBitmapPaint == null)
    {
      this.m_BufferBitmapPaint = new Paint();
      this.m_BufferBitmapPaint.setAntiAlias(true);
      this.m_BufferBitmapPaint.setFilterBitmap(true);
      this.m_BufferBitmapPaint.setStyle(Paint.Style.FILL);
    }
    float f1 = this.m_OriginalRadius;
    float f2 = this.m_CurrentRadius;
    float f3 = this.m_OriginalRadius;
    float f4 = this.m_CurrentRadius;
    float f5 = this.m_OriginalRadius;
    float f6 = this.m_CurrentRadius;
    float f7 = this.m_OriginalRadius;
    float f8 = this.m_CurrentRadius;
    this.m_BufferBitmap.eraseColor(0);
    Object localObject = new Canvas(this.m_BufferBitmap);
    float f9;
    float f10;
    Bitmap localBitmap;
    int j;
    if (this.m_CenterDrawable != null)
    {
      f9 = this.m_OriginalRadius;
      f10 = this.m_OriginalRadius;
      if (!(this.m_CenterDrawable instanceof BitmapDrawable)) {
        break label347;
      }
      localBitmap = ((BitmapDrawable)this.m_CenterDrawable).getBitmap();
      j = localBitmap.getWidth();
    }
    for (i = localBitmap.getHeight();; i = this.m_CenterDrawable.getIntrinsicHeight())
    {
      int k = (int)(f9 - j / 2);
      int m = (int)(f10 - i / 2);
      this.m_CenterDrawable.setBounds(k, m, k + j, m + i);
      this.m_CenterDrawable.draw((Canvas)localObject);
      ((Canvas)localObject).drawOval(f1 - f2, f3 - f4, f5 + f6, f7 + f8, this.m_Paint);
      localObject = getBounds();
      f1 = ((Rect)localObject).centerX();
      f2 = ((Rect)localObject).centerY();
      this.m_BufferBitmapPaint.setAlpha(this.m_Alpha);
      paramCanvas.drawBitmap(this.m_BufferBitmap, f1 - this.m_OriginalRadius, f2 - this.m_OriginalRadius, this.m_BufferBitmapPaint);
      return;
      label347:
      j = this.m_CenterDrawable.getIntrinsicWidth();
    }
  }
  
  public int getIntrinsicHeight()
  {
    return (int)(this.m_OriginalRadius * 2.0F);
  }
  
  public int getIntrinsicWidth()
  {
    return (int)(this.m_OriginalRadius * 2.0F);
  }
  
  public int getOpacity()
  {
    return this.m_Alpha;
  }
  
  public void resetState()
  {
    this.m_CurrentRadius = this.m_OriginalRadius;
    this.m_State = 0;
    invalidateSelf();
  }
  
  public void setAlpha(int paramInt)
  {
    this.m_Alpha = paramInt;
  }
  
  public void setColorFilter(ColorFilter paramColorFilter) {}
  
  public void startVideoCaptrueAnimation()
  {
    startVideoCaptrueAnimation(300L);
  }
  
  public void startVideoCaptrueAnimation(long paramLong)
  {
    if (this.m_State != 1)
    {
      this.m_AnimationStartTime = SystemClock.uptimeMillis();
      this.m_AnimationDuration = paramLong;
      if (this.m_State != 3) {
        scheduleSelf(this.m_AnimationRunnable, this.m_AnimationStartTime);
      }
      this.m_State = 1;
    }
  }
  
  public void stopVideoCaptrueAnimation()
  {
    stopVideoCaptrueAnimation(300L);
  }
  
  public void stopVideoCaptrueAnimation(long paramLong)
  {
    if (this.m_State != 3)
    {
      this.m_AnimationStartTime = SystemClock.uptimeMillis();
      this.m_AnimationDuration = paramLong;
      if (this.m_State != 1) {
        scheduleSelf(this.m_AnimationRunnable, this.m_AnimationStartTime);
      }
      this.m_State = 3;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/drawable/RecordingButtonIconDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */