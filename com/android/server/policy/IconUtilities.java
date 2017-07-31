package com.android.server.policy;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.TableMaskFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public final class IconUtilities
{
  private static final String TAG = "IconUtilities";
  private static final int[] sColors = { -65536, -16711936, -16776961 };
  private final Paint mBlurPaint = new Paint();
  private final Canvas mCanvas = new Canvas();
  private int mColorIndex = 0;
  private final DisplayMetrics mDisplayMetrics;
  private final Paint mGlowColorFocusedPaint = new Paint();
  private final Paint mGlowColorPressedPaint = new Paint();
  private int mIconHeight = -1;
  private int mIconTextureHeight = -1;
  private int mIconTextureWidth = -1;
  private int mIconWidth = -1;
  private final Rect mOldBounds = new Rect();
  private final Paint mPaint = new Paint();
  
  public IconUtilities(Context paramContext)
  {
    Object localObject1 = paramContext.getResources();
    Object localObject2 = ((Resources)localObject1).getDisplayMetrics();
    this.mDisplayMetrics = ((DisplayMetrics)localObject2);
    float f = 5.0F * ((DisplayMetrics)localObject2).density;
    int i = (int)((Resources)localObject1).getDimension(17104896);
    this.mIconHeight = i;
    this.mIconWidth = i;
    i = this.mIconWidth + (int)(2.0F * f);
    this.mIconTextureHeight = i;
    this.mIconTextureWidth = i;
    this.mBlurPaint.setMaskFilter(new BlurMaskFilter(f, BlurMaskFilter.Blur.NORMAL));
    localObject1 = new TypedValue();
    localObject2 = this.mGlowColorPressedPaint;
    if (paramContext.getTheme().resolveAttribute(16843661, (TypedValue)localObject1, true))
    {
      i = ((TypedValue)localObject1).data;
      ((Paint)localObject2).setColor(i);
      this.mGlowColorPressedPaint.setMaskFilter(TableMaskFilter.CreateClipTable(0, 30));
      localObject2 = this.mGlowColorFocusedPaint;
      if (!paramContext.getTheme().resolveAttribute(16843663, (TypedValue)localObject1, true)) {
        break label320;
      }
    }
    label320:
    for (i = ((TypedValue)localObject1).data;; i = 36352)
    {
      ((Paint)localObject2).setColor(i);
      this.mGlowColorFocusedPaint.setMaskFilter(TableMaskFilter.CreateClipTable(0, 30));
      new ColorMatrix().setSaturation(0.2F);
      this.mCanvas.setDrawFilter(new PaintFlagsDrawFilter(4, 2));
      return;
      i = 49920;
      break;
    }
  }
  
  private Bitmap createSelectedBitmap(Bitmap paramBitmap, boolean paramBoolean)
  {
    Bitmap localBitmap1 = Bitmap.createBitmap(this.mIconTextureWidth, this.mIconTextureHeight, Bitmap.Config.ARGB_8888);
    Canvas localCanvas = new Canvas(localBitmap1);
    localCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
    Object localObject = new int[2];
    Bitmap localBitmap2 = paramBitmap.extractAlpha(this.mBlurPaint, (int[])localObject);
    float f1 = localObject[0];
    float f2 = localObject[1];
    if (paramBoolean) {}
    for (localObject = this.mGlowColorPressedPaint;; localObject = this.mGlowColorFocusedPaint)
    {
      localCanvas.drawBitmap(localBitmap2, f1, f2, (Paint)localObject);
      localBitmap2.recycle();
      localCanvas.drawBitmap(paramBitmap, 0.0F, 0.0F, this.mPaint);
      localCanvas.setBitmap(null);
      return localBitmap1;
    }
  }
  
  public Bitmap createIconBitmap(Drawable paramDrawable)
  {
    int k = this.mIconWidth;
    int m = this.mIconHeight;
    Object localObject;
    int i1;
    int n;
    int i;
    int j;
    float f;
    if ((paramDrawable instanceof PaintDrawable))
    {
      localObject = (PaintDrawable)paramDrawable;
      ((PaintDrawable)localObject).setIntrinsicWidth(k);
      ((PaintDrawable)localObject).setIntrinsicHeight(m);
      i1 = paramDrawable.getIntrinsicWidth();
      n = paramDrawable.getIntrinsicHeight();
      i = m;
      j = k;
      if (i1 > 0)
      {
        i = m;
        j = k;
        if (n > 0)
        {
          if ((k >= i1) && (m >= n)) {
            break label278;
          }
          f = i1 / n;
          if (i1 <= n) {
            break label250;
          }
          i = (int)(k / f);
          j = k;
        }
      }
    }
    for (;;)
    {
      m = this.mIconTextureWidth;
      k = this.mIconTextureHeight;
      localObject = Bitmap.createBitmap(m, k, Bitmap.Config.ARGB_8888);
      Canvas localCanvas = this.mCanvas;
      localCanvas.setBitmap((Bitmap)localObject);
      m = (m - j) / 2;
      k = (k - i) / 2;
      this.mOldBounds.set(paramDrawable.getBounds());
      paramDrawable.setBounds(m, k, m + j, k + i);
      paramDrawable.draw(localCanvas);
      paramDrawable.setBounds(this.mOldBounds);
      return (Bitmap)localObject;
      if (!(paramDrawable instanceof BitmapDrawable)) {
        break;
      }
      localObject = (BitmapDrawable)paramDrawable;
      if (((BitmapDrawable)localObject).getBitmap().getDensity() != 0) {
        break;
      }
      ((BitmapDrawable)localObject).setTargetDensity(this.mDisplayMetrics);
      break;
      label250:
      i = m;
      j = k;
      if (n > i1)
      {
        j = (int)(m * f);
        i = m;
        continue;
        label278:
        i = m;
        j = k;
        if (i1 < k)
        {
          i = m;
          j = k;
          if (n < m)
          {
            j = i1;
            i = n;
          }
        }
      }
    }
  }
  
  public Drawable createIconDrawable(Drawable paramDrawable)
  {
    Object localObject = createIconBitmap(paramDrawable);
    paramDrawable = new StateListDrawable();
    BitmapDrawable localBitmapDrawable = new BitmapDrawable(createSelectedBitmap((Bitmap)localObject, false));
    paramDrawable.addState(new int[] { 16842908 }, localBitmapDrawable);
    localBitmapDrawable = new BitmapDrawable(createSelectedBitmap((Bitmap)localObject, true));
    paramDrawable.addState(new int[] { 16842919 }, localBitmapDrawable);
    localObject = new BitmapDrawable((Bitmap)localObject);
    paramDrawable.addState(new int[0], (Drawable)localObject);
    paramDrawable.setBounds(0, 0, this.mIconTextureWidth, this.mIconTextureHeight);
    return paramDrawable;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/policy/IconUtilities.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */