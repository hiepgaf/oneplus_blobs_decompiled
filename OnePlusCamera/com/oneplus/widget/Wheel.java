package com.oneplus.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class Wheel
  extends View
{
  private static final int COLOR_TICK = -1;
  private static final int COLOR_TICK_HIGHLIGHTED = Color.argb(255, 92, 107, 192);
  public static final int ORIENTATION_HORIZONTAL = 0;
  public static final int ORIENTATION_VERTICAL = 1;
  private final List<Callback> m_Callbacks = new ArrayList();
  private int m_CenterValue = 50;
  private Drawable m_CurrentValueIndicatorDrawable;
  private int m_CurrentValueIndicatorHeight;
  private int m_CurrentValueIndicatorWidth;
  private int m_CurrentValuePosition;
  private Drawable m_DefaultCurrentValueIndicatorDrawable;
  private WheelDrawable m_DefaultWheelDrawable;
  private Bitmap m_FadingEdgeBitmapEnd;
  private Bitmap m_FadingEdgeBitmapStart;
  private int m_FadingEdgeLength = 300;
  private final Rect m_FadingEdgeMaskDstRect = new Rect();
  private final Rect m_FadingEdgeMaskSrcRect = new Rect();
  private Paint m_FadingEdgePaint;
  private boolean m_IsMovingByUser;
  private int m_MaxValue = 100;
  private int m_Orientation = 0;
  private final Rect m_TouchDownWheelBounds = new Rect();
  private float m_TouchDownX;
  private float m_TouchDownY;
  private int m_Value;
  private final Rect m_WheelBounds = new Rect();
  private Drawable m_WheelDrawable;
  private float m_WheelLengthRatio = 1.2F;
  
  public Wheel(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private void onValueChanged(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    int i;
    if (paramInt < 0) {
      i = 0;
    }
    while (this.m_Value == i)
    {
      return;
      i = paramInt;
      if (paramInt > this.m_MaxValue) {
        i = this.m_MaxValue;
      }
    }
    this.m_Value = i;
    if (paramBoolean1)
    {
      updateWheelBounds();
      invalidate();
    }
    paramInt = this.m_Callbacks.size() - 1;
    while (paramInt >= 0)
    {
      ((Callback)this.m_Callbacks.get(paramInt)).onValueChanged(this, i, paramBoolean2);
      paramInt -= 1;
    }
  }
  
  private void updateWheelBounds()
  {
    int i = getPaddingLeft();
    int m = getPaddingTop();
    int k = getPaddingRight();
    int j = getPaddingBottom();
    int n = Math.max(0, getWidth() - i - k);
    k = Math.max(0, getHeight() - m - j);
    float f = this.m_Value / this.m_MaxValue;
    switch (this.m_Orientation)
    {
    case 1: 
    default: 
      return;
    }
    this.m_CurrentValuePosition = (n / 2 + i);
    n = Math.round(n * this.m_WheelLengthRatio);
    if (this.m_WheelDrawable != null)
    {
      i = this.m_WheelDrawable.getIntrinsicHeight();
      if (i > 0) {
        break label185;
      }
      j = k;
    }
    for (;;)
    {
      this.m_WheelBounds.set(0, 0, n, j);
      this.m_WheelBounds.offsetTo(this.m_CurrentValuePosition - Math.round(n * f), (k - j) / 2 + m);
      return;
      i = 0;
      break;
      label185:
      j = i;
      if (i > k) {
        j = k;
      }
    }
  }
  
  public void addCallback(Callback paramCallback)
  {
    this.m_Callbacks.add(paramCallback);
  }
  
  public int getCenterValue()
  {
    return this.m_CenterValue;
  }
  
  public int getFadingEdgeLength()
  {
    return this.m_FadingEdgeLength;
  }
  
  public int getMaxValue()
  {
    return this.m_MaxValue;
  }
  
  public int getValue()
  {
    return this.m_Value;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    int i3 = getPaddingLeft();
    int i2 = getPaddingTop();
    int j = getPaddingRight();
    int i = getPaddingBottom();
    int k = Math.max(0, getWidth() - i3 - j);
    int m = Math.max(0, getHeight() - i2 - i);
    int i1 = paramCanvas.saveLayer(i3, i2, i3 + k, i2 + m, null);
    int n;
    try
    {
      Drawable localDrawable = this.m_WheelDrawable;
      localObject1 = localDrawable;
      if (localDrawable == null)
      {
        if (this.m_DefaultWheelDrawable == null) {
          this.m_DefaultWheelDrawable = new WheelDrawable(getResources(), this.m_Orientation);
        }
        localObject1 = this.m_DefaultWheelDrawable;
      }
      if ((localObject1 instanceof WheelDrawable)) {}
      switch (this.m_Orientation)
      {
      case 1: 
        ((WheelDrawable)localObject1).setValues(this.m_MaxValue, this.m_CenterValue, this.m_Value);
        ((Drawable)localObject1).setBounds(this.m_WheelBounds);
        ((Drawable)localObject1).draw(paramCanvas);
        localDrawable = this.m_CurrentValueIndicatorDrawable;
        localObject1 = localDrawable;
        if (localDrawable == null)
        {
          if (this.m_DefaultCurrentValueIndicatorDrawable == null)
          {
            this.m_DefaultCurrentValueIndicatorDrawable = new DefaultCurrentValueIndicatorDrawable(getResources(), this.m_Orientation);
            this.m_CurrentValueIndicatorWidth = this.m_DefaultCurrentValueIndicatorDrawable.getIntrinsicWidth();
            this.m_CurrentValueIndicatorHeight = this.m_DefaultCurrentValueIndicatorDrawable.getIntrinsicHeight();
          }
          localObject1 = this.m_DefaultCurrentValueIndicatorDrawable;
        }
        j = this.m_CurrentValueIndicatorWidth;
        n = this.m_CurrentValueIndicatorHeight;
        if (this.m_Orientation == 1) {
          break label741;
        }
        if (j > 0) {
          break label465;
        }
      }
    }
    finally
    {
      Object localObject1;
      label290:
      paramCanvas.restoreToCount(i1);
    }
    if (this.m_Orientation != 0) {
      if (n <= 0) {
        break label747;
      }
    }
    for (;;)
    {
      k = i3 + (k - i) / 2;
      m = i2 + (m - j) / 2;
      ((Drawable)localObject1).setBounds(k, m, k + i, m + j);
      ((Drawable)localObject1).draw(paramCanvas);
      if (this.m_FadingEdgeLength > 0)
      {
        i = getWidth();
        j = getHeight();
        if (this.m_FadingEdgePaint == null)
        {
          this.m_FadingEdgePaint = new Paint();
          this.m_FadingEdgePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        }
        k = this.m_Orientation;
      }
      switch (k)
      {
      case 1: 
      default: 
        paramCanvas.restoreToCount(i1);
        return;
        ((WheelDrawable)localObject1).setCurrentValuePosition(this.m_CurrentValuePosition);
        break;
        i = j;
        if (j <= k) {
          break label290;
        }
        break label741;
        j = n;
        if (n <= m) {}
        break;
      case 0: 
        label465:
        if ((this.m_FadingEdgeBitmapStart == null) || (this.m_FadingEdgeBitmapStart.getWidth() != this.m_FadingEdgeLength)) {}
        for (;;)
        {
          this.m_FadingEdgeBitmapStart = Bitmap.createBitmap(this.m_FadingEdgeLength, 1, Bitmap.Config.ARGB_8888);
          this.m_FadingEdgeBitmapEnd = Bitmap.createBitmap(this.m_FadingEdgeLength, 1, Bitmap.Config.ARGB_8888);
          GradientDrawable localGradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[] { 0, -1 });
          localGradientDrawable.setBounds(0, 0, this.m_FadingEdgeBitmapStart.getWidth(), this.m_FadingEdgeBitmapStart.getHeight());
          localGradientDrawable.draw(new Canvas(this.m_FadingEdgeBitmapStart));
          localGradientDrawable.setOrientation(GradientDrawable.Orientation.RIGHT_LEFT);
          localGradientDrawable.draw(new Canvas(this.m_FadingEdgeBitmapEnd));
          do
          {
            this.m_FadingEdgeMaskSrcRect.set(0, 0, this.m_FadingEdgeBitmapStart.getWidth(), this.m_FadingEdgeBitmapStart.getHeight());
            this.m_FadingEdgeMaskDstRect.set(0, 0, this.m_FadingEdgeLength, j);
            paramCanvas.drawBitmap(this.m_FadingEdgeBitmapStart, this.m_FadingEdgeMaskSrcRect, this.m_FadingEdgeMaskDstRect, this.m_FadingEdgePaint);
            this.m_FadingEdgeMaskDstRect.set(i - this.m_FadingEdgeLength, 0, i, j);
            paramCanvas.drawBitmap(this.m_FadingEdgeBitmapEnd, this.m_FadingEdgeMaskSrcRect, this.m_FadingEdgeMaskDstRect, this.m_FadingEdgePaint);
            break;
            k = this.m_FadingEdgeBitmapStart.getHeight();
          } while (k == 1);
        }
        break;
        label741:
        i = k;
        break label290;
        label747:
        j = m;
      }
    }
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    updateWheelBounds();
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    float f1 = paramMotionEvent.getX();
    float f2 = paramMotionEvent.getY();
    switch (paramMotionEvent.getAction())
    {
    }
    for (;;)
    {
      return true;
      this.m_TouchDownX = f1;
      this.m_TouchDownY = f2;
      this.m_IsMovingByUser = true;
      this.m_TouchDownWheelBounds.set(this.m_WheelBounds);
      int i = this.m_Callbacks.size() - 1;
      while (i >= 0)
      {
        ((Callback)this.m_Callbacks.get(i)).onStartTrackingTouch(this);
        i -= 1;
      }
      switch (this.m_Orientation)
      {
      case 1: 
      default: 
        invalidate();
        return true;
      }
      this.m_WheelBounds.set(this.m_TouchDownWheelBounds);
      this.m_WheelBounds.offset(Math.round(f1 - this.m_TouchDownX), 0);
      if (this.m_WheelBounds.left > this.m_CurrentValuePosition) {
        this.m_WheelBounds.offset(this.m_CurrentValuePosition - this.m_WheelBounds.left, 0);
      }
      for (;;)
      {
        onValueChanged(Math.round(this.m_MaxValue * ((this.m_CurrentValuePosition - this.m_WheelBounds.left) / this.m_WheelBounds.width())), false, true);
        break;
        if (this.m_WheelBounds.right < this.m_CurrentValuePosition) {
          this.m_WheelBounds.offset(this.m_CurrentValuePosition - this.m_WheelBounds.right, 0);
        }
      }
      this.m_IsMovingByUser = false;
      i = this.m_Callbacks.size() - 1;
      while (i >= 0)
      {
        ((Callback)this.m_Callbacks.get(i)).onStopTrackingTouch(this);
        i -= 1;
      }
    }
  }
  
  public void removeCallback(Callback paramCallback)
  {
    this.m_Callbacks.remove(paramCallback);
  }
  
  public void setCenterValue(int paramInt)
  {
    int i;
    if (paramInt < 0) {
      i = -1;
    }
    while (this.m_CenterValue == i)
    {
      return;
      i = paramInt;
      if (paramInt > this.m_MaxValue) {
        i = this.m_MaxValue;
      }
    }
    this.m_CenterValue = i;
    invalidate();
  }
  
  public void setCurrentValueIndicatorDrawable(Drawable paramDrawable)
  {
    this.m_CurrentValueIndicatorDrawable = paramDrawable;
  }
  
  public void setFadingEdgeLength(int paramInt)
  {
    if (this.m_FadingEdgeLength == paramInt) {
      return;
    }
    this.m_FadingEdgeLength = paramInt;
    this.m_FadingEdgeBitmapStart = null;
    this.m_FadingEdgeBitmapEnd = null;
    invalidate();
  }
  
  public void setMaxValue(int paramInt)
  {
    int i = paramInt;
    if (paramInt < 0) {
      i = 0;
    }
    if (this.m_MaxValue == i) {
      return;
    }
    if (this.m_MaxValue > i)
    {
      this.m_CenterValue = Math.min(this.m_CenterValue, i);
      if (this.m_Value > i)
      {
        this.m_Value = i;
        onValueChanged(this.m_Value, false, false);
      }
    }
    this.m_MaxValue = i;
    updateWheelBounds();
    invalidate();
  }
  
  public void setValue(int paramInt)
  {
    int i;
    if (paramInt < 0) {
      i = 0;
    }
    for (;;)
    {
      onValueChanged(i, true, false);
      return;
      i = paramInt;
      if (paramInt > this.m_MaxValue) {
        i = this.m_MaxValue;
      }
    }
  }
  
  public void setWheelDrawable(Drawable paramDrawable)
  {
    this.m_WheelDrawable = paramDrawable;
  }
  
  public void setWheelLengthRatio(float paramFloat)
  {
    this.m_WheelLengthRatio = paramFloat;
    updateWheelBounds();
  }
  
  public static abstract class Callback
  {
    public void onStartTrackingTouch(Wheel paramWheel) {}
    
    public void onStopTrackingTouch(Wheel paramWheel) {}
    
    public void onValueChanged(Wheel paramWheel, int paramInt, boolean paramBoolean) {}
  }
  
  private static final class DefaultCurrentValueIndicatorDrawable
    extends Drawable
  {
    private static float INDICATOR_SIZE_DP = 5.0F;
    private final Paint m_IndicatorPaint = new Paint();
    private final RectF m_IndicatorRect = new RectF();
    private final int m_IndicatorSize;
    private final int m_Orientation;
    
    public DefaultCurrentValueIndicatorDrawable(Resources paramResources, int paramInt)
    {
      this.m_IndicatorPaint.setStyle(Paint.Style.FILL);
      this.m_IndicatorPaint.setAntiAlias(true);
      this.m_IndicatorPaint.setColor(Wheel.-get0());
      this.m_IndicatorSize = ViewUtils.convertDpToPx(paramResources, INDICATOR_SIZE_DP);
      this.m_IndicatorRect.set(0.0F, 0.0F, this.m_IndicatorSize, this.m_IndicatorSize);
      this.m_Orientation = paramInt;
    }
    
    public void draw(Canvas paramCanvas)
    {
      Rect localRect = getBounds();
      switch (this.m_Orientation)
      {
      }
      for (;;)
      {
        paramCanvas.drawOval(this.m_IndicatorRect, this.m_IndicatorPaint);
        return;
        this.m_IndicatorRect.offsetTo(localRect.left + (localRect.width() - this.m_IndicatorRect.width()) / 2.0F, localRect.top);
      }
    }
    
    public int getIntrinsicHeight()
    {
      return this.m_IndicatorSize;
    }
    
    public int getIntrinsicWidth()
    {
      return this.m_IndicatorSize;
    }
    
    public int getOpacity()
    {
      return -3;
    }
    
    public void setAlpha(int paramInt)
    {
      this.m_IndicatorPaint.setAlpha(paramInt);
    }
    
    public void setColorFilter(ColorFilter paramColorFilter)
    {
      this.m_IndicatorPaint.setColorFilter(paramColorFilter);
    }
  }
  
  public static class WheelDrawable
    extends Drawable
  {
    private static final float MIN_TICK_MARGIN_DP = 5.0F;
    private static final float PADDING_DP = 13.0F;
    private static final float TICK_THICKNESS_DP = 1.0F;
    protected int m_CenterValue;
    protected int m_CurrentValuePosition;
    protected int m_MaxValue;
    protected final float m_MinTickMargin;
    protected final int m_Orientation;
    protected final int m_Padding;
    protected final Paint m_TickPaint = new Paint();
    protected int m_Value;
    
    public WheelDrawable(Resources paramResources, int paramInt)
    {
      this.m_Orientation = paramInt;
      this.m_Padding = ViewUtils.convertDpToPx(paramResources, 13.0F);
      this.m_MinTickMargin = ViewUtils.convertDpToPx(paramResources, 5.0F);
      this.m_TickPaint.setStyle(Paint.Style.STROKE);
      this.m_TickPaint.setStrokeWidth(ViewUtils.convertDpToPx(paramResources, 1.0F));
    }
    
    private void prepareTickPaint(Rect paramRect, int paramInt)
    {
      int i = getCenterValue();
      if (i < 0)
      {
        this.m_TickPaint.setColor(-1);
        return;
      }
      int j = getCurrentValuePosition();
      switch (this.m_Orientation)
      {
      case 1: 
      default: 
        return;
      }
      float f = paramRect.left + paramRect.width() * i / getMaxValue();
      if (Math.abs(j - f) <= 2.0F)
      {
        this.m_TickPaint.setColor(-1);
        return;
      }
      if (j < f)
      {
        if ((paramInt < j) || (paramInt > f))
        {
          this.m_TickPaint.setColor(-1);
          return;
        }
        this.m_TickPaint.setColor(Wheel.-get0());
        return;
      }
      if ((paramInt > j) || (paramInt < f))
      {
        this.m_TickPaint.setColor(-1);
        return;
      }
      this.m_TickPaint.setColor(Wheel.-get0());
    }
    
    public void draw(Canvas paramCanvas)
    {
      Rect localRect = getBounds();
      switch (this.m_Orientation)
      {
      }
      for (;;)
      {
        return;
        int i = localRect.height() - this.m_Padding - this.m_Padding;
        if (i > 0)
        {
          int j = (int)(localRect.width() / this.m_MinTickMargin);
          int k = localRect.top + this.m_Padding;
          int m = k + i;
          prepareTickPaint(localRect, localRect.left);
          paramCanvas.drawLine(localRect.left, k, localRect.left, m, this.m_TickPaint);
          i = 1;
          while (i <= j)
          {
            int n = Math.round(localRect.left + localRect.width() * (i / j));
            prepareTickPaint(localRect, n);
            paramCanvas.drawLine(n, k, n, m, this.m_TickPaint);
            i += 1;
          }
        }
      }
    }
    
    public int getCenterValue()
    {
      return this.m_CenterValue;
    }
    
    public int getCurrentValue()
    {
      return this.m_Value;
    }
    
    public int getCurrentValuePosition()
    {
      return this.m_CurrentValuePosition;
    }
    
    public int getMaxValue()
    {
      return this.m_MaxValue;
    }
    
    public int getOpacity()
    {
      return -3;
    }
    
    public void setAlpha(int paramInt)
    {
      this.m_TickPaint.setAlpha(paramInt);
    }
    
    public void setColorFilter(ColorFilter paramColorFilter)
    {
      this.m_TickPaint.setColorFilter(paramColorFilter);
    }
    
    final void setCurrentValuePosition(int paramInt)
    {
      this.m_CurrentValuePosition = paramInt;
    }
    
    final void setValues(int paramInt1, int paramInt2, int paramInt3)
    {
      this.m_MaxValue = paramInt1;
      this.m_CenterValue = paramInt2;
      this.m_Value = paramInt3;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/widget/Wheel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */