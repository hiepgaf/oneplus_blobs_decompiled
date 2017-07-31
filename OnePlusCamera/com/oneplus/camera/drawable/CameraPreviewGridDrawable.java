package com.oneplus.camera.drawable;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import com.oneplus.camera.ui.CameraPreviewGrid.GridType;

public final class CameraPreviewGridDrawable
  extends Drawable
{
  private static final float GOLDEN_RATIO = 1.618F;
  private CameraPreviewGrid.GridType m_GridType = CameraPreviewGrid.GridType.NONE;
  private final Drawable m_HorizontalDrawable;
  private final float m_HorizontalStrokeWidth;
  private final Drawable m_VerticalDrawable;
  private final float m_VerticalStrokeWidth;
  
  public CameraPreviewGridDrawable(Context paramContext)
  {
    int i = paramContext.getColor(2131230773);
    this.m_HorizontalDrawable = new ShadowDrawable(paramContext, new ColorDrawable(i), 2131492900);
    this.m_HorizontalStrokeWidth = paramContext.getResources().getDimensionPixelSize(2131296331);
    this.m_VerticalDrawable = new ShadowDrawable(paramContext, new ColorDrawable(i), 2131492901);
    this.m_VerticalStrokeWidth = this.m_HorizontalStrokeWidth;
  }
  
  public void draw(Canvas paramCanvas)
  {
    if (this.m_GridType == null) {
      return;
    }
    Rect localRect = getBounds();
    switch (-getcom-oneplus-camera-ui-CameraPreviewGrid$GridTypeSwitchesValues()[this.m_GridType.ordinal()])
    {
    default: 
      return;
    case 2: 
      f = localRect.top + localRect.height() / 3.0F - this.m_HorizontalStrokeWidth / 2.0F;
      this.m_HorizontalDrawable.setBounds(localRect.left, (int)f, localRect.right, Math.round(this.m_HorizontalStrokeWidth + f));
      this.m_HorizontalDrawable.draw(paramCanvas);
      f = localRect.top + localRect.height() * 2 / 3.0F - this.m_HorizontalStrokeWidth / 2.0F;
      this.m_HorizontalDrawable.setBounds(localRect.left, (int)f, localRect.right, Math.round(this.m_HorizontalStrokeWidth + f));
      this.m_HorizontalDrawable.draw(paramCanvas);
      f = localRect.left + localRect.width() / 3.0F - this.m_VerticalStrokeWidth / 2.0F;
      this.m_VerticalDrawable.setBounds((int)f, localRect.top, Math.round(this.m_VerticalStrokeWidth + f), localRect.bottom);
      this.m_VerticalDrawable.draw(paramCanvas);
      f = localRect.left + localRect.width() * 2 / 3.0F - this.m_VerticalStrokeWidth / 2.0F;
      this.m_VerticalDrawable.setBounds((int)f, localRect.top, Math.round(this.m_VerticalStrokeWidth + f), localRect.bottom);
      this.m_VerticalDrawable.draw(paramCanvas);
      return;
    case 3: 
      f = localRect.top + localRect.height() / 4.0F - this.m_HorizontalStrokeWidth / 2.0F;
      this.m_HorizontalDrawable.setBounds(localRect.left, (int)f, localRect.right, Math.round(this.m_HorizontalStrokeWidth + f));
      this.m_HorizontalDrawable.draw(paramCanvas);
      f = localRect.top + localRect.height() * 2 / 4.0F - this.m_HorizontalStrokeWidth / 2.0F;
      this.m_HorizontalDrawable.setBounds(localRect.left, (int)f, localRect.right, Math.round(this.m_HorizontalStrokeWidth + f));
      this.m_HorizontalDrawable.draw(paramCanvas);
      f = localRect.top + localRect.height() * 3 / 4.0F - this.m_HorizontalStrokeWidth / 2.0F;
      this.m_HorizontalDrawable.setBounds(localRect.left, (int)f, localRect.right, Math.round(this.m_HorizontalStrokeWidth + f));
      this.m_HorizontalDrawable.draw(paramCanvas);
      f = localRect.left + localRect.width() / 4.0F - this.m_VerticalStrokeWidth / 2.0F;
      this.m_VerticalDrawable.setBounds((int)f, localRect.top, Math.round(this.m_VerticalStrokeWidth + f), localRect.bottom);
      this.m_VerticalDrawable.draw(paramCanvas);
      f = localRect.left + localRect.width() * 2 / 4.0F - this.m_VerticalStrokeWidth / 2.0F;
      this.m_VerticalDrawable.setBounds((int)f, localRect.top, Math.round(this.m_VerticalStrokeWidth + f), localRect.bottom);
      this.m_VerticalDrawable.draw(paramCanvas);
      f = localRect.left + localRect.width() * 3 / 4.0F - this.m_VerticalStrokeWidth / 2.0F;
      this.m_VerticalDrawable.setBounds((int)f, localRect.top, Math.round(this.m_VerticalStrokeWidth + f), localRect.bottom);
      this.m_VerticalDrawable.draw(paramCanvas);
      return;
    }
    float f = localRect.top + localRect.height() / 2.618F - this.m_HorizontalStrokeWidth / 2.0F;
    this.m_HorizontalDrawable.setBounds(localRect.left, (int)f, localRect.right, Math.round(this.m_HorizontalStrokeWidth + f));
    this.m_HorizontalDrawable.draw(paramCanvas);
    f = localRect.top + localRect.height() * 1.618F / 2.618F - this.m_HorizontalStrokeWidth / 2.0F;
    this.m_HorizontalDrawable.setBounds(localRect.left, (int)f, localRect.right, Math.round(this.m_HorizontalStrokeWidth + f));
    this.m_HorizontalDrawable.draw(paramCanvas);
    f = localRect.left + localRect.width() / 2.618F - this.m_VerticalStrokeWidth / 2.0F;
    this.m_VerticalDrawable.setBounds((int)f, localRect.top, Math.round(this.m_VerticalStrokeWidth + f), localRect.bottom);
    this.m_VerticalDrawable.draw(paramCanvas);
    f = localRect.left + localRect.width() * 1.618F / 2.618F - this.m_VerticalStrokeWidth / 2.0F;
    this.m_VerticalDrawable.setBounds((int)f, localRect.top, Math.round(this.m_VerticalStrokeWidth + f) + 1, localRect.bottom);
    this.m_VerticalDrawable.draw(paramCanvas);
  }
  
  public CameraPreviewGrid.GridType getGridType()
  {
    return this.m_GridType;
  }
  
  public int getOpacity()
  {
    return -3;
  }
  
  public void setAlpha(int paramInt)
  {
    this.m_HorizontalDrawable.setAlpha(paramInt);
    this.m_VerticalDrawable.setAlpha(paramInt);
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    this.m_HorizontalDrawable.setColorFilter(paramColorFilter);
    this.m_VerticalDrawable.setColorFilter(paramColorFilter);
  }
  
  public void setGridType(CameraPreviewGrid.GridType paramGridType)
  {
    if (this.m_GridType != paramGridType)
    {
      this.m_GridType = paramGridType;
      invalidateSelf();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/drawable/CameraPreviewGridDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */