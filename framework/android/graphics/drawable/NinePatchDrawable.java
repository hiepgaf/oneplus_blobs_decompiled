package android.graphics.drawable;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Insets;
import android.graphics.NinePatch;
import android.graphics.NinePatch.InsetStruct;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import com.android.internal.R.styleable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class NinePatchDrawable
  extends Drawable
{
  private static final boolean DEFAULT_DITHER = false;
  private int mBitmapHeight = -1;
  private int mBitmapWidth = -1;
  private boolean mMutated;
  private NinePatchState mNinePatchState;
  private Insets mOpticalInsets = Insets.NONE;
  private Rect mOutlineInsets;
  private float mOutlineRadius;
  private Rect mPadding;
  private Paint mPaint;
  private int mTargetDensity = 160;
  private Rect mTempRect;
  private PorterDuffColorFilter mTintFilter;
  
  NinePatchDrawable()
  {
    this.mNinePatchState = new NinePatchState();
  }
  
  public NinePatchDrawable(Resources paramResources, Bitmap paramBitmap, byte[] paramArrayOfByte, Rect paramRect1, Rect paramRect2, String paramString)
  {
    this(new NinePatchState(new NinePatch(paramBitmap, paramArrayOfByte, paramString), paramRect1, paramRect2), paramResources);
  }
  
  public NinePatchDrawable(Resources paramResources, Bitmap paramBitmap, byte[] paramArrayOfByte, Rect paramRect, String paramString)
  {
    this(new NinePatchState(new NinePatch(paramBitmap, paramArrayOfByte, paramString), paramRect), paramResources);
  }
  
  public NinePatchDrawable(Resources paramResources, NinePatch paramNinePatch)
  {
    this(new NinePatchState(paramNinePatch, new Rect()), paramResources);
  }
  
  @Deprecated
  public NinePatchDrawable(Bitmap paramBitmap, byte[] paramArrayOfByte, Rect paramRect, String paramString)
  {
    this(new NinePatchState(new NinePatch(paramBitmap, paramArrayOfByte, paramString), paramRect), null);
  }
  
  @Deprecated
  public NinePatchDrawable(NinePatch paramNinePatch)
  {
    this(new NinePatchState(paramNinePatch, new Rect()), null);
  }
  
  private NinePatchDrawable(NinePatchState paramNinePatchState, Resources paramResources)
  {
    this.mNinePatchState = paramNinePatchState;
    updateLocalState(paramResources);
  }
  
  private void computeBitmapSize()
  {
    Object localObject1 = this.mNinePatchState.mNinePatch;
    if (localObject1 == null) {
      return;
    }
    int i = ((NinePatch)localObject1).getDensity();
    int j = this.mTargetDensity;
    Object localObject2 = this.mNinePatchState.mOpticalInsets;
    if (localObject2 != Insets.NONE)
    {
      this.mOpticalInsets = Insets.of(Drawable.scaleFromDensity(((Insets)localObject2).left, i, j, true), Drawable.scaleFromDensity(((Insets)localObject2).top, i, j, true), Drawable.scaleFromDensity(((Insets)localObject2).right, i, j, true), Drawable.scaleFromDensity(((Insets)localObject2).bottom, i, j, true));
      localObject2 = this.mNinePatchState.mPadding;
      if (localObject2 == null) {
        break label297;
      }
      if (this.mPadding == null) {
        this.mPadding = new Rect();
      }
      this.mPadding.left = Drawable.scaleFromDensity(((Rect)localObject2).left, i, j, false);
      this.mPadding.top = Drawable.scaleFromDensity(((Rect)localObject2).top, i, j, false);
      this.mPadding.right = Drawable.scaleFromDensity(((Rect)localObject2).right, i, j, false);
      this.mPadding.bottom = Drawable.scaleFromDensity(((Rect)localObject2).bottom, i, j, false);
    }
    for (;;)
    {
      this.mBitmapHeight = Drawable.scaleFromDensity(((NinePatch)localObject1).getHeight(), i, j, true);
      this.mBitmapWidth = Drawable.scaleFromDensity(((NinePatch)localObject1).getWidth(), i, j, true);
      localObject1 = ((NinePatch)localObject1).getBitmap().getNinePatchInsets();
      if (localObject1 == null) {
        break label305;
      }
      localObject2 = ((NinePatch.InsetStruct)localObject1).outlineRect;
      this.mOutlineInsets = NinePatch.InsetStruct.scaleInsets(((Rect)localObject2).left, ((Rect)localObject2).top, ((Rect)localObject2).right, ((Rect)localObject2).bottom, j / i);
      this.mOutlineRadius = Drawable.scaleFromDensity(((NinePatch.InsetStruct)localObject1).outlineRadius, i, j);
      return;
      this.mOpticalInsets = Insets.NONE;
      break;
      label297:
      this.mPadding = null;
    }
    label305:
    this.mOutlineInsets = null;
  }
  
  private boolean needsMirroring()
  {
    return (isAutoMirrored()) && (getLayoutDirection() == 1);
  }
  
  private void updateLocalState(Resources paramResources)
  {
    NinePatchState localNinePatchState = this.mNinePatchState;
    if (localNinePatchState.mDither) {
      setDither(localNinePatchState.mDither);
    }
    if ((paramResources == null) && (localNinePatchState.mNinePatch != null)) {}
    for (this.mTargetDensity = localNinePatchState.mNinePatch.getDensity();; this.mTargetDensity = Drawable.resolveDensity(paramResources, this.mTargetDensity))
    {
      this.mTintFilter = updateTintFilter(this.mTintFilter, localNinePatchState.mTint, localNinePatchState.mTintMode);
      computeBitmapSize();
      return;
    }
  }
  
  private void updateStateFromTypedArray(TypedArray paramTypedArray)
    throws XmlPullParserException
  {
    Resources localResources = paramTypedArray.getResources();
    NinePatchState localNinePatchState = this.mNinePatchState;
    localNinePatchState.mChangingConfigurations |= paramTypedArray.getChangingConfigurations();
    localNinePatchState.mThemeAttrs = paramTypedArray.extractThemeAttrs();
    localNinePatchState.mDither = paramTypedArray.getBoolean(1, localNinePatchState.mDither);
    int i = paramTypedArray.getResourceId(0, 0);
    if (i != 0)
    {
      BitmapFactory.Options localOptions = new BitmapFactory.Options();
      if (localNinePatchState.mDither) {}
      Rect localRect1;
      Rect localRect2;
      Bitmap localBitmap1;
      for (boolean bool = false;; bool = true)
      {
        localOptions.inDither = bool;
        localOptions.inScreenDensity = localResources.getDisplayMetrics().noncompatDensityDpi;
        localRect1 = new Rect();
        localRect2 = new Rect();
        Bitmap localBitmap2 = null;
        localBitmap1 = localBitmap2;
        try
        {
          TypedValue localTypedValue = new TypedValue();
          localBitmap1 = localBitmap2;
          InputStream localInputStream = localResources.openRawResource(i, localTypedValue);
          localBitmap1 = localBitmap2;
          localBitmap2 = BitmapFactory.decodeResourceStream(localResources, localTypedValue, localInputStream, localRect1, localOptions);
          localBitmap1 = localBitmap2;
          localInputStream.close();
          localBitmap1 = localBitmap2;
        }
        catch (IOException localIOException)
        {
          for (;;) {}
        }
        if (localBitmap1 != null) {
          break;
        }
        throw new XmlPullParserException(paramTypedArray.getPositionDescription() + ": <nine-patch> requires a valid src attribute");
      }
      if (localBitmap1.getNinePatchChunk() == null) {
        throw new XmlPullParserException(paramTypedArray.getPositionDescription() + ": <nine-patch> requires a valid 9-patch source image");
      }
      localBitmap1.getOpticalInsets(localRect2);
      localNinePatchState.mNinePatch = new NinePatch(localBitmap1, localBitmap1.getNinePatchChunk());
      localNinePatchState.mPadding = localRect1;
      localNinePatchState.mOpticalInsets = Insets.of(localRect2);
    }
    else
    {
      localNinePatchState.mAutoMirrored = paramTypedArray.getBoolean(4, localNinePatchState.mAutoMirrored);
      localNinePatchState.mBaseAlpha = paramTypedArray.getFloat(3, localNinePatchState.mBaseAlpha);
      i = paramTypedArray.getInt(5, -1);
      if (i != -1) {
        localNinePatchState.mTintMode = Drawable.parseTintMode(i, PorterDuff.Mode.SRC_IN);
      }
      paramTypedArray = paramTypedArray.getColorStateList(2);
      if (paramTypedArray != null) {
        localNinePatchState.mTint = paramTypedArray;
      }
      return;
    }
  }
  
  public void applyTheme(Resources.Theme paramTheme)
  {
    super.applyTheme(paramTheme);
    NinePatchState localNinePatchState = this.mNinePatchState;
    if (localNinePatchState == null) {
      return;
    }
    if (localNinePatchState.mThemeAttrs != null) {
      localTypedArray = paramTheme.resolveAttributes(localNinePatchState.mThemeAttrs, R.styleable.NinePatchDrawable);
    }
    try
    {
      updateStateFromTypedArray(localTypedArray);
    }
    catch (XmlPullParserException localXmlPullParserException)
    {
      for (;;)
      {
        rethrowAsRuntimeException(localXmlPullParserException);
        localTypedArray.recycle();
      }
    }
    finally
    {
      localTypedArray.recycle();
    }
    if ((localNinePatchState.mTint != null) && (localNinePatchState.mTint.canApplyTheme())) {
      localNinePatchState.mTint = localNinePatchState.mTint.obtainForTheme(paramTheme);
    }
    updateLocalState(paramTheme.getResources());
  }
  
  public boolean canApplyTheme()
  {
    if (this.mNinePatchState != null) {
      return this.mNinePatchState.canApplyTheme();
    }
    return false;
  }
  
  public void clearMutated()
  {
    super.clearMutated();
    this.mMutated = false;
  }
  
  public void draw(Canvas paramCanvas)
  {
    NinePatchState localNinePatchState = this.mNinePatchState;
    Rect localRect2 = getBounds();
    int i = -1;
    int j;
    int k;
    label86:
    int m;
    label96:
    Rect localRect1;
    if ((this.mTintFilter != null) && (getPaint().getColorFilter() == null))
    {
      this.mPaint.setColorFilter(this.mTintFilter);
      j = 1;
      if (localNinePatchState.mBaseAlpha == 1.0F) {
        break label346;
      }
      k = getPaint().getAlpha();
      this.mPaint.setAlpha((int)(k * localNinePatchState.mBaseAlpha + 0.5F));
      if (paramCanvas.getDensity() != 0) {
        break label352;
      }
      m = 1;
      localRect1 = localRect2;
      if (m != 0)
      {
        i = paramCanvas.save();
        float f = this.mTargetDensity / localNinePatchState.mNinePatch.getDensity();
        paramCanvas.scale(f, f, localRect2.left, localRect2.top);
        if (this.mTempRect == null) {
          this.mTempRect = new Rect();
        }
        localRect1 = this.mTempRect;
        localRect1.left = localRect2.left;
        localRect1.top = localRect2.top;
        localRect1.right = (localRect2.left + Math.round(localRect2.width() / f));
        localRect1.bottom = (localRect2.top + Math.round(localRect2.height() / f));
      }
      m = i;
      if (needsMirroring()) {
        if (i < 0) {
          break label358;
        }
      }
    }
    for (;;)
    {
      paramCanvas.scale(-1.0F, 1.0F, (localRect1.left + localRect1.right) / 2.0F, (localRect1.top + localRect1.bottom) / 2.0F);
      m = i;
      localNinePatchState.mNinePatch.draw(paramCanvas, localRect1, this.mPaint);
      if (m >= 0) {
        paramCanvas.restoreToCount(m);
      }
      if (j != 0) {
        this.mPaint.setColorFilter(null);
      }
      if (k >= 0) {
        this.mPaint.setAlpha(k);
      }
      return;
      j = 0;
      break;
      label346:
      k = -1;
      break label86;
      label352:
      m = 0;
      break label96;
      label358:
      i = paramCanvas.save();
    }
  }
  
  public int getAlpha()
  {
    if (this.mPaint == null) {
      return 255;
    }
    return getPaint().getAlpha();
  }
  
  public int getChangingConfigurations()
  {
    return super.getChangingConfigurations() | this.mNinePatchState.getChangingConfigurations();
  }
  
  public Drawable.ConstantState getConstantState()
  {
    this.mNinePatchState.mChangingConfigurations = getChangingConfigurations();
    return this.mNinePatchState;
  }
  
  public int getIntrinsicHeight()
  {
    return this.mBitmapHeight;
  }
  
  public int getIntrinsicWidth()
  {
    return this.mBitmapWidth;
  }
  
  public int getOpacity()
  {
    if ((this.mNinePatchState.mNinePatch.hasAlpha()) || ((this.mPaint != null) && (this.mPaint.getAlpha() < 255))) {
      return -3;
    }
    return -1;
  }
  
  public Insets getOpticalInsets()
  {
    Insets localInsets = this.mOpticalInsets;
    if (needsMirroring()) {
      return Insets.of(localInsets.right, localInsets.top, localInsets.left, localInsets.bottom);
    }
    return localInsets;
  }
  
  public void getOutline(Outline paramOutline)
  {
    Rect localRect = getBounds();
    if (localRect.isEmpty()) {
      return;
    }
    if ((this.mNinePatchState != null) && (this.mOutlineInsets != null))
    {
      NinePatch.InsetStruct localInsetStruct = this.mNinePatchState.mNinePatch.getBitmap().getNinePatchInsets();
      if (localInsetStruct != null)
      {
        int i = localRect.left;
        int j = this.mOutlineInsets.left;
        int k = localRect.top;
        paramOutline.setRoundRect(j + i, this.mOutlineInsets.top + k, localRect.right - this.mOutlineInsets.right, localRect.bottom - this.mOutlineInsets.bottom, this.mOutlineRadius);
        paramOutline.setAlpha(localInsetStruct.outlineAlpha * (getAlpha() / 255.0F));
        return;
      }
    }
    super.getOutline(paramOutline);
  }
  
  public boolean getPadding(Rect paramRect)
  {
    boolean bool = false;
    if (this.mPadding != null)
    {
      paramRect.set(this.mPadding);
      if ((paramRect.left | paramRect.top | paramRect.right | paramRect.bottom) != 0) {
        bool = true;
      }
      return bool;
    }
    return super.getPadding(paramRect);
  }
  
  public Paint getPaint()
  {
    if (this.mPaint == null)
    {
      this.mPaint = new Paint();
      this.mPaint.setDither(false);
    }
    return this.mPaint;
  }
  
  public Region getTransparentRegion()
  {
    return this.mNinePatchState.mNinePatch.getTransparentRegion(getBounds());
  }
  
  public void inflate(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    super.inflate(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
    paramXmlPullParser = obtainAttributes(paramResources, paramTheme, paramAttributeSet, R.styleable.NinePatchDrawable);
    updateStateFromTypedArray(paramXmlPullParser);
    paramXmlPullParser.recycle();
    updateLocalState(paramResources);
  }
  
  public boolean isAutoMirrored()
  {
    return this.mNinePatchState.mAutoMirrored;
  }
  
  public boolean isFilterBitmap()
  {
    if (this.mPaint != null) {
      return getPaint().isFilterBitmap();
    }
    return false;
  }
  
  public boolean isStateful()
  {
    NinePatchState localNinePatchState = this.mNinePatchState;
    if (!super.isStateful())
    {
      if (localNinePatchState.mTint != null) {
        return localNinePatchState.mTint.isStateful();
      }
    }
    else {
      return true;
    }
    return false;
  }
  
  public Drawable mutate()
  {
    if ((!this.mMutated) && (super.mutate() == this))
    {
      this.mNinePatchState = new NinePatchState(this.mNinePatchState);
      this.mMutated = true;
    }
    return this;
  }
  
  protected boolean onStateChange(int[] paramArrayOfInt)
  {
    paramArrayOfInt = this.mNinePatchState;
    if ((paramArrayOfInt.mTint != null) && (paramArrayOfInt.mTintMode != null))
    {
      this.mTintFilter = updateTintFilter(this.mTintFilter, paramArrayOfInt.mTint, paramArrayOfInt.mTintMode);
      return true;
    }
    return false;
  }
  
  public void setAlpha(int paramInt)
  {
    if ((this.mPaint == null) && (paramInt == 255)) {
      return;
    }
    getPaint().setAlpha(paramInt);
    invalidateSelf();
  }
  
  public void setAutoMirrored(boolean paramBoolean)
  {
    this.mNinePatchState.mAutoMirrored = paramBoolean;
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    if ((this.mPaint == null) && (paramColorFilter == null)) {
      return;
    }
    getPaint().setColorFilter(paramColorFilter);
    invalidateSelf();
  }
  
  public void setDither(boolean paramBoolean)
  {
    if ((this.mPaint == null) && (!paramBoolean)) {
      return;
    }
    getPaint().setDither(paramBoolean);
    invalidateSelf();
  }
  
  public void setFilterBitmap(boolean paramBoolean)
  {
    getPaint().setFilterBitmap(paramBoolean);
    invalidateSelf();
  }
  
  public void setTargetDensity(int paramInt)
  {
    int i = paramInt;
    if (paramInt == 0) {
      i = 160;
    }
    if (this.mTargetDensity != i)
    {
      this.mTargetDensity = i;
      computeBitmapSize();
      invalidateSelf();
    }
  }
  
  public void setTargetDensity(Canvas paramCanvas)
  {
    setTargetDensity(paramCanvas.getDensity());
  }
  
  public void setTargetDensity(DisplayMetrics paramDisplayMetrics)
  {
    setTargetDensity(paramDisplayMetrics.densityDpi);
  }
  
  public void setTintList(ColorStateList paramColorStateList)
  {
    this.mNinePatchState.mTint = paramColorStateList;
    this.mTintFilter = updateTintFilter(this.mTintFilter, paramColorStateList, this.mNinePatchState.mTintMode);
    invalidateSelf();
  }
  
  public void setTintMode(PorterDuff.Mode paramMode)
  {
    this.mNinePatchState.mTintMode = paramMode;
    this.mTintFilter = updateTintFilter(this.mTintFilter, this.mNinePatchState.mTint, paramMode);
    invalidateSelf();
  }
  
  static final class NinePatchState
    extends Drawable.ConstantState
  {
    boolean mAutoMirrored = false;
    float mBaseAlpha = 1.0F;
    int mChangingConfigurations;
    boolean mDither = false;
    NinePatch mNinePatch = null;
    Insets mOpticalInsets = Insets.NONE;
    Rect mPadding = null;
    int[] mThemeAttrs;
    ColorStateList mTint = null;
    PorterDuff.Mode mTintMode = NinePatchDrawable.DEFAULT_TINT_MODE;
    
    NinePatchState() {}
    
    NinePatchState(NinePatch paramNinePatch, Rect paramRect)
    {
      this(paramNinePatch, paramRect, null, false, false);
    }
    
    NinePatchState(NinePatch paramNinePatch, Rect paramRect1, Rect paramRect2)
    {
      this(paramNinePatch, paramRect1, paramRect2, false, false);
    }
    
    NinePatchState(NinePatch paramNinePatch, Rect paramRect1, Rect paramRect2, boolean paramBoolean1, boolean paramBoolean2)
    {
      this.mNinePatch = paramNinePatch;
      this.mPadding = paramRect1;
      this.mOpticalInsets = Insets.of(paramRect2);
      this.mDither = paramBoolean1;
      this.mAutoMirrored = paramBoolean2;
    }
    
    NinePatchState(NinePatchState paramNinePatchState)
    {
      this.mChangingConfigurations = paramNinePatchState.mChangingConfigurations;
      this.mNinePatch = paramNinePatchState.mNinePatch;
      this.mTint = paramNinePatchState.mTint;
      this.mTintMode = paramNinePatchState.mTintMode;
      this.mPadding = paramNinePatchState.mPadding;
      this.mOpticalInsets = paramNinePatchState.mOpticalInsets;
      this.mBaseAlpha = paramNinePatchState.mBaseAlpha;
      this.mDither = paramNinePatchState.mDither;
      this.mAutoMirrored = paramNinePatchState.mAutoMirrored;
      this.mThemeAttrs = paramNinePatchState.mThemeAttrs;
    }
    
    public int addAtlasableBitmaps(Collection<Bitmap> paramCollection)
    {
      Bitmap localBitmap = this.mNinePatch.getBitmap();
      if ((isAtlasable(localBitmap)) && (paramCollection.add(localBitmap))) {
        return localBitmap.getWidth() * localBitmap.getHeight();
      }
      return 0;
    }
    
    public boolean canApplyTheme()
    {
      if ((this.mThemeAttrs == null) && ((this.mTint == null) || (!this.mTint.canApplyTheme()))) {
        return super.canApplyTheme();
      }
      return true;
    }
    
    public int getChangingConfigurations()
    {
      int j = this.mChangingConfigurations;
      if (this.mTint != null) {}
      for (int i = this.mTint.getChangingConfigurations();; i = 0) {
        return i | j;
      }
    }
    
    public Drawable newDrawable()
    {
      return new NinePatchDrawable(this, null, null);
    }
    
    public Drawable newDrawable(Resources paramResources)
    {
      return new NinePatchDrawable(this, paramResources, null);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/drawable/NinePatchDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */