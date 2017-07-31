package com.oneplus.camera.watermark;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.Log;
import com.oneplus.camera.drawable.ShadowDrawable;
import com.oneplus.drawable.ShadowTextDrawable;

public class SloganWatermarkDrawable
  extends Drawable
{
  private static final int DRAWABLE_SPACING = 20;
  private static final String TAG = SloganWatermarkDrawable.class.getSimpleName();
  private int m_BaseLogoDrawableHeight;
  private int m_BaseLogoDrawableMarginLeft;
  private int m_BaseLogoDrawableMarginRight;
  private int m_BaseLogoDrawableWidth;
  private int m_BaseSubtitleTextHeight;
  private float m_BaseSubtitleTextSize;
  private int m_BaseTitleMarginTop;
  private int m_BaseTitleTextHeight;
  private float m_BaseTitleTextSize;
  private int m_BaseWatermarkHeight;
  private ShadowDrawable m_LogoDrawable;
  private ShadowTextDrawable m_ShadowSubtitleDrawable;
  private ShadowTextDrawable m_ShadowTitleDrawable;
  private String m_Subtitle;
  private String m_Title;
  private String m_UserSubtitle;
  private String m_UserSubtitlePrefix;
  
  public SloganWatermarkDrawable()
  {
    BaseApplication localBaseApplication = BaseApplication.current();
    Object localObject = localBaseApplication.getResources();
    this.m_BaseWatermarkHeight = ((Resources)localObject).getDimensionPixelSize(2131296653);
    this.m_BaseLogoDrawableWidth = ((Resources)localObject).getDimensionPixelSize(2131296654);
    this.m_BaseLogoDrawableHeight = ((Resources)localObject).getDimensionPixelSize(2131296655);
    this.m_BaseLogoDrawableMarginLeft = ((Resources)localObject).getDimensionPixelSize(2131296656);
    this.m_BaseLogoDrawableMarginRight = ((Resources)localObject).getDimensionPixelSize(2131296657);
    this.m_BaseTitleMarginTop = ((Resources)localObject).getDimensionPixelSize(2131296658);
    this.m_BaseTitleTextHeight = ((Resources)localObject).getDimensionPixelSize(2131296659);
    this.m_BaseTitleTextSize = ((Resources)localObject).getDimensionPixelSize(2131296660);
    this.m_BaseSubtitleTextHeight = ((Resources)localObject).getDimensionPixelSize(2131296661);
    this.m_BaseSubtitleTextSize = ((Resources)localObject).getDimensionPixelSize(2131296662);
    this.m_Title = localBaseApplication.getString(2131558629);
    this.m_Subtitle = localBaseApplication.getString(2131558630);
    this.m_UserSubtitlePrefix = localBaseApplication.getString(2131558631);
    localObject = Typeface.createFromAsset(localBaseApplication.getAssets(), "watermark_font.ttf");
    this.m_LogoDrawable = new ShadowDrawable(localBaseApplication, 2130838279, 2131492940);
    this.m_LogoDrawable.setPaddings(0, 0, 20, 20);
    this.m_ShadowTitleDrawable = new ShadowTextDrawable();
    this.m_ShadowTitleDrawable.setText(this.m_Title);
    this.m_ShadowTitleDrawable.setTextAppearance(localBaseApplication, 2131492942);
    this.m_ShadowTitleDrawable.setTypeface((Typeface)localObject);
    this.m_ShadowSubtitleDrawable = new ShadowTextDrawable();
    this.m_ShadowSubtitleDrawable.setTextAppearance(localBaseApplication, 2131492942);
    this.m_ShadowSubtitleDrawable.setTypeface((Typeface)localObject);
  }
  
  private void draw(Canvas paramCanvas, float paramFloat)
  {
    Object localObject = getBounds();
    Log.v(TAG, "draw() - Scale ratio: ", Float.valueOf(paramFloat), ", bounds: ", localObject);
    int i3 = Math.round(this.m_BaseWatermarkHeight * paramFloat);
    int i2 = Math.round(this.m_BaseLogoDrawableWidth * paramFloat);
    int m = Math.round(this.m_BaseLogoDrawableHeight * paramFloat);
    int n = Math.round(this.m_BaseLogoDrawableMarginLeft * paramFloat);
    int i1 = Math.round(this.m_BaseLogoDrawableMarginRight * paramFloat);
    int k = Math.round(this.m_BaseTitleMarginTop * paramFloat);
    int j = Math.round(this.m_BaseTitleTextHeight * paramFloat);
    int i = Math.round(this.m_BaseSubtitleTextHeight * paramFloat);
    float f1 = this.m_BaseTitleTextSize;
    float f2 = this.m_BaseSubtitleTextSize;
    i3 = ((Rect)localObject).top + (i3 - m) / 2;
    i2 = n + i2;
    this.m_LogoDrawable.setBounds(n, i3, i2 + 20, i3 + m + 20);
    this.m_LogoDrawable.draw(paramCanvas);
    this.m_ShadowTitleDrawable.setTextSize(f1 * paramFloat);
    Rect localRect = new Rect();
    this.m_ShadowTitleDrawable.getTextBounds(localRect);
    m = i2 + i1;
    k = ((Rect)localObject).top + k;
    n = localRect.width();
    j = k + j;
    this.m_ShadowTitleDrawable.setBounds(m, k, m + n, j);
    this.m_ShadowTitleDrawable.draw(paramCanvas);
    if (this.m_UserSubtitle != null) {}
    for (localObject = this.m_UserSubtitlePrefix + " " + this.m_UserSubtitle;; localObject = this.m_Subtitle)
    {
      this.m_ShadowSubtitleDrawable.setTextSize(f2 * paramFloat);
      this.m_ShadowSubtitleDrawable.setText((CharSequence)localObject);
      this.m_ShadowSubtitleDrawable.getTextBounds(localRect);
      k = localRect.width();
      this.m_ShadowSubtitleDrawable.setBounds(m, j, m + k, j + i);
      this.m_ShadowSubtitleDrawable.draw(paramCanvas);
      return;
    }
  }
  
  public boolean apply(Bitmap paramBitmap)
  {
    if (paramBitmap == null) {
      return false;
    }
    Canvas localCanvas = new Canvas(paramBitmap);
    float f = Math.min(paramBitmap.getWidth(), paramBitmap.getHeight()) / 1080.0F;
    int i = Math.round(this.m_BaseWatermarkHeight * f);
    setBounds(0, paramBitmap.getHeight() - i, paramBitmap.getWidth(), paramBitmap.getHeight());
    draw(localCanvas, f);
    return true;
  }
  
  public Rect calculateWatermarkBounds(int paramInt1, int paramInt2, Rect paramRect)
  {
    Rect localRect = paramRect;
    if (paramRect == null) {
      localRect = new Rect();
    }
    float f = Math.min(paramInt1, paramInt2) / 1080.0F;
    localRect.set(0, paramInt2 - Math.round(this.m_BaseWatermarkHeight * f / 2.0F) * 2, paramInt1, paramInt2);
    return localRect;
  }
  
  public Bitmap createWatermarkBitmap(int paramInt1, int paramInt2)
  {
    if ((paramInt1 <= 0) || (paramInt2 <= 0)) {
      return null;
    }
    float f = Math.min(paramInt1, paramInt2) / 1080.0F;
    paramInt2 = Math.round(this.m_BaseWatermarkHeight * f / 2.0F) * 2;
    if (paramInt2 <= 0) {
      return null;
    }
    Bitmap localBitmap = Bitmap.createBitmap(paramInt1, paramInt2, Bitmap.Config.ARGB_8888);
    Canvas localCanvas = new Canvas(localBitmap);
    setBounds(0, 0, paramInt1, paramInt2);
    draw(localCanvas, f);
    return localBitmap;
  }
  
  public Bitmap createWatermarkOverlay(int paramInt1, int paramInt2)
  {
    if ((paramInt1 <= 0) || (paramInt2 <= 0)) {
      return null;
    }
    float f = Math.min(paramInt1, paramInt2) / 1080.0F;
    int i = Math.round(this.m_BaseWatermarkHeight * f);
    if (i <= 0) {
      return null;
    }
    Bitmap localBitmap = Bitmap.createBitmap(paramInt1, paramInt2, Bitmap.Config.ARGB_8888);
    Canvas localCanvas = new Canvas(localBitmap);
    setBounds(0, paramInt2 - i, paramInt1, paramInt2);
    draw(localCanvas, f);
    return localBitmap;
  }
  
  public void draw(Canvas paramCanvas)
  {
    draw(paramCanvas, getBounds().width() / 1080.0F);
  }
  
  public int getOpacity()
  {
    return 1;
  }
  
  public String getSubtitleText()
  {
    if (this.m_UserSubtitle != null) {
      return this.m_UserSubtitle;
    }
    return this.m_Subtitle;
  }
  
  public void setAlpha(int paramInt)
  {
    this.m_LogoDrawable.setAlpha(paramInt);
    this.m_ShadowTitleDrawable.setAlpha(paramInt);
    this.m_ShadowSubtitleDrawable.setAlpha(paramInt);
    invalidateSelf();
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    this.m_LogoDrawable.setColorFilter(paramColorFilter);
    this.m_ShadowTitleDrawable.setColorFilter(paramColorFilter);
    this.m_ShadowSubtitleDrawable.setColorFilter(paramColorFilter);
    invalidateSelf();
  }
  
  public void setSubtitleText(String paramString)
  {
    if ((paramString == null) || (paramString.isEmpty())) {}
    for (this.m_UserSubtitle = null;; this.m_UserSubtitle = paramString)
    {
      invalidateSelf();
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/watermark/SloganWatermarkDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */