package android.graphics.drawable;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.shapes.Shape;
import android.util.AttributeSet;
import android.util.Log;
import com.android.internal.R.styleable;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ShapeDrawable
  extends Drawable
{
  private boolean mMutated;
  private ShapeState mShapeState;
  private PorterDuffColorFilter mTintFilter;
  
  public ShapeDrawable()
  {
    this(new ShapeState(null), null);
  }
  
  private ShapeDrawable(ShapeState paramShapeState, Resources paramResources)
  {
    this.mShapeState = paramShapeState;
    updateLocalState(paramResources);
  }
  
  public ShapeDrawable(Shape paramShape)
  {
    this(new ShapeState(null), null);
    this.mShapeState.mShape = paramShape;
  }
  
  private static int modulateAlpha(int paramInt1, int paramInt2)
  {
    return paramInt1 * (paramInt2 + (paramInt2 >>> 7)) >>> 8;
  }
  
  private void updateLocalState(Resources paramResources)
  {
    this.mTintFilter = updateTintFilter(this.mTintFilter, this.mShapeState.mTint, this.mShapeState.mTintMode);
  }
  
  private void updateShape()
  {
    if (this.mShapeState.mShape != null)
    {
      Rect localRect = getBounds();
      int i = localRect.width();
      int j = localRect.height();
      this.mShapeState.mShape.resize(i, j);
      if (this.mShapeState.mShaderFactory != null) {
        this.mShapeState.mPaint.setShader(this.mShapeState.mShaderFactory.resize(i, j));
      }
    }
    invalidateSelf();
  }
  
  private void updateStateFromTypedArray(TypedArray paramTypedArray)
  {
    ShapeState localShapeState = this.mShapeState;
    Paint localPaint = localShapeState.mPaint;
    localShapeState.mChangingConfigurations |= paramTypedArray.getChangingConfigurations();
    localShapeState.mThemeAttrs = paramTypedArray.extractThemeAttrs();
    localPaint.setColor(paramTypedArray.getColor(4, localPaint.getColor()));
    localPaint.setDither(paramTypedArray.getBoolean(0, localPaint.isDither()));
    setIntrinsicWidth((int)paramTypedArray.getDimension(3, localShapeState.mIntrinsicWidth));
    setIntrinsicHeight((int)paramTypedArray.getDimension(2, localShapeState.mIntrinsicHeight));
    int i = paramTypedArray.getInt(5, -1);
    if (i != -1) {
      localShapeState.mTintMode = Drawable.parseTintMode(i, PorterDuff.Mode.SRC_IN);
    }
    paramTypedArray = paramTypedArray.getColorStateList(1);
    if (paramTypedArray != null) {
      localShapeState.mTint = paramTypedArray;
    }
  }
  
  public void applyTheme(Resources.Theme paramTheme)
  {
    super.applyTheme(paramTheme);
    ShapeState localShapeState = this.mShapeState;
    if (localShapeState == null) {
      return;
    }
    if (localShapeState.mThemeAttrs != null)
    {
      TypedArray localTypedArray = paramTheme.resolveAttributes(localShapeState.mThemeAttrs, R.styleable.ShapeDrawable);
      updateStateFromTypedArray(localTypedArray);
      localTypedArray.recycle();
    }
    if ((localShapeState.mTint != null) && (localShapeState.mTint.canApplyTheme())) {
      localShapeState.mTint = localShapeState.mTint.obtainForTheme(paramTheme);
    }
    updateLocalState(paramTheme.getResources());
  }
  
  public void clearMutated()
  {
    super.clearMutated();
    this.mMutated = false;
  }
  
  public void draw(Canvas paramCanvas)
  {
    Rect localRect = getBounds();
    ShapeState localShapeState = this.mShapeState;
    Paint localPaint = localShapeState.mPaint;
    int j = localPaint.getAlpha();
    localPaint.setAlpha(modulateAlpha(j, localShapeState.mAlpha));
    int i;
    if ((localPaint.getAlpha() != 0) || (localPaint.getXfermode() != null))
    {
      if ((this.mTintFilter == null) || (localPaint.getColorFilter() != null)) {
        break label159;
      }
      localPaint.setColorFilter(this.mTintFilter);
      i = 1;
      label82:
      if (localShapeState.mShape == null) {
        break label164;
      }
      int k = paramCanvas.save();
      paramCanvas.translate(localRect.left, localRect.top);
      onDraw(localShapeState.mShape, paramCanvas, localPaint);
      paramCanvas.restoreToCount(k);
    }
    for (;;)
    {
      if (i != 0) {
        localPaint.setColorFilter(null);
      }
      do
      {
        localPaint.setAlpha(j);
        return;
      } while (!localPaint.hasShadowLayer());
      break;
      label159:
      i = 0;
      break label82;
      label164:
      paramCanvas.drawRect(localRect, localPaint);
    }
  }
  
  public int getAlpha()
  {
    return this.mShapeState.mAlpha;
  }
  
  public int getChangingConfigurations()
  {
    return super.getChangingConfigurations() | this.mShapeState.getChangingConfigurations();
  }
  
  public Drawable.ConstantState getConstantState()
  {
    this.mShapeState.mChangingConfigurations = getChangingConfigurations();
    return this.mShapeState;
  }
  
  public int getIntrinsicHeight()
  {
    return this.mShapeState.mIntrinsicHeight;
  }
  
  public int getIntrinsicWidth()
  {
    return this.mShapeState.mIntrinsicWidth;
  }
  
  public int getOpacity()
  {
    if (this.mShapeState.mShape == null)
    {
      Paint localPaint = this.mShapeState.mPaint;
      if (localPaint.getXfermode() == null)
      {
        int i = localPaint.getAlpha();
        if (i == 0) {
          return -2;
        }
        if (i == 255) {
          return -1;
        }
      }
    }
    return -3;
  }
  
  public void getOutline(Outline paramOutline)
  {
    if (this.mShapeState.mShape != null)
    {
      this.mShapeState.mShape.getOutline(paramOutline);
      paramOutline.setAlpha(getAlpha() / 255.0F);
    }
  }
  
  public boolean getPadding(Rect paramRect)
  {
    if (this.mShapeState.mPadding != null)
    {
      paramRect.set(this.mShapeState.mPadding);
      return true;
    }
    return super.getPadding(paramRect);
  }
  
  public Paint getPaint()
  {
    return this.mShapeState.mPaint;
  }
  
  public ShaderFactory getShaderFactory()
  {
    return this.mShapeState.mShaderFactory;
  }
  
  public Shape getShape()
  {
    return this.mShapeState.mShape;
  }
  
  public void inflate(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    super.inflate(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
    paramTheme = obtainAttributes(paramResources, paramTheme, paramAttributeSet, R.styleable.ShapeDrawable);
    updateStateFromTypedArray(paramTheme);
    paramTheme.recycle();
    int i = paramXmlPullParser.getDepth();
    for (;;)
    {
      int j = paramXmlPullParser.next();
      if ((j == 1) || ((j == 3) && (paramXmlPullParser.getDepth() <= i))) {
        break;
      }
      if (j == 2)
      {
        paramTheme = paramXmlPullParser.getName();
        if (!inflateTag(paramTheme, paramResources, paramXmlPullParser, paramAttributeSet)) {
          Log.w("drawable", "Unknown element: " + paramTheme + " for ShapeDrawable " + this);
        }
      }
    }
    updateLocalState(paramResources);
  }
  
  protected boolean inflateTag(String paramString, Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet)
  {
    if ("padding".equals(paramString))
    {
      paramString = paramResources.obtainAttributes(paramAttributeSet, R.styleable.ShapeDrawablePadding);
      setPadding(paramString.getDimensionPixelOffset(0, 0), paramString.getDimensionPixelOffset(1, 0), paramString.getDimensionPixelOffset(2, 0), paramString.getDimensionPixelOffset(3, 0));
      paramString.recycle();
      return true;
    }
    return false;
  }
  
  public boolean isStateful()
  {
    ShapeState localShapeState = this.mShapeState;
    if (!super.isStateful())
    {
      if (localShapeState.mTint != null) {
        return localShapeState.mTint.isStateful();
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
      if (this.mShapeState.mPaint == null) {
        break label101;
      }
      this.mShapeState.mPaint = new Paint(this.mShapeState.mPaint);
      if (this.mShapeState.mPadding == null) {
        break label119;
      }
    }
    for (this.mShapeState.mPadding = new Rect(this.mShapeState.mPadding);; this.mShapeState.mPadding = new Rect())
    {
      label101:
      label119:
      try
      {
        this.mShapeState.mShape = this.mShapeState.mShape.clone();
        this.mMutated = true;
        return this;
      }
      catch (CloneNotSupportedException localCloneNotSupportedException) {}
      this.mShapeState.mPaint = new Paint(1);
      break;
    }
    return null;
  }
  
  protected void onBoundsChange(Rect paramRect)
  {
    super.onBoundsChange(paramRect);
    updateShape();
  }
  
  protected void onDraw(Shape paramShape, Canvas paramCanvas, Paint paramPaint)
  {
    paramShape.draw(paramCanvas, paramPaint);
  }
  
  protected boolean onStateChange(int[] paramArrayOfInt)
  {
    paramArrayOfInt = this.mShapeState;
    if ((paramArrayOfInt.mTint != null) && (paramArrayOfInt.mTintMode != null))
    {
      this.mTintFilter = updateTintFilter(this.mTintFilter, paramArrayOfInt.mTint, paramArrayOfInt.mTintMode);
      return true;
    }
    return false;
  }
  
  public void setAlpha(int paramInt)
  {
    this.mShapeState.mAlpha = paramInt;
    invalidateSelf();
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    this.mShapeState.mPaint.setColorFilter(paramColorFilter);
    invalidateSelf();
  }
  
  public void setDither(boolean paramBoolean)
  {
    this.mShapeState.mPaint.setDither(paramBoolean);
    invalidateSelf();
  }
  
  public void setIntrinsicHeight(int paramInt)
  {
    this.mShapeState.mIntrinsicHeight = paramInt;
    invalidateSelf();
  }
  
  public void setIntrinsicWidth(int paramInt)
  {
    this.mShapeState.mIntrinsicWidth = paramInt;
    invalidateSelf();
  }
  
  public void setPadding(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((paramInt1 | paramInt2 | paramInt3 | paramInt4) == 0) {
      this.mShapeState.mPadding = null;
    }
    for (;;)
    {
      invalidateSelf();
      return;
      if (this.mShapeState.mPadding == null) {
        this.mShapeState.mPadding = new Rect();
      }
      this.mShapeState.mPadding.set(paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  public void setPadding(Rect paramRect)
  {
    if (paramRect == null) {
      this.mShapeState.mPadding = null;
    }
    for (;;)
    {
      invalidateSelf();
      return;
      if (this.mShapeState.mPadding == null) {
        this.mShapeState.mPadding = new Rect();
      }
      this.mShapeState.mPadding.set(paramRect);
    }
  }
  
  public void setShaderFactory(ShaderFactory paramShaderFactory)
  {
    this.mShapeState.mShaderFactory = paramShaderFactory;
  }
  
  public void setShape(Shape paramShape)
  {
    this.mShapeState.mShape = paramShape;
    updateShape();
  }
  
  public void setTintList(ColorStateList paramColorStateList)
  {
    this.mShapeState.mTint = paramColorStateList;
    this.mTintFilter = updateTintFilter(this.mTintFilter, paramColorStateList, this.mShapeState.mTintMode);
    invalidateSelf();
  }
  
  public void setTintMode(PorterDuff.Mode paramMode)
  {
    this.mShapeState.mTintMode = paramMode;
    this.mTintFilter = updateTintFilter(this.mTintFilter, this.mShapeState.mTint, paramMode);
    invalidateSelf();
  }
  
  public static abstract class ShaderFactory
  {
    public abstract Shader resize(int paramInt1, int paramInt2);
  }
  
  static final class ShapeState
    extends Drawable.ConstantState
  {
    int mAlpha = 255;
    int mChangingConfigurations;
    int mIntrinsicHeight;
    int mIntrinsicWidth;
    Rect mPadding;
    Paint mPaint;
    ShapeDrawable.ShaderFactory mShaderFactory;
    Shape mShape;
    int[] mThemeAttrs;
    ColorStateList mTint = null;
    PorterDuff.Mode mTintMode = ShapeDrawable.DEFAULT_TINT_MODE;
    
    ShapeState(ShapeState paramShapeState)
    {
      if (paramShapeState != null)
      {
        this.mThemeAttrs = paramShapeState.mThemeAttrs;
        this.mPaint = paramShapeState.mPaint;
        this.mShape = paramShapeState.mShape;
        this.mTint = paramShapeState.mTint;
        this.mTintMode = paramShapeState.mTintMode;
        this.mPadding = paramShapeState.mPadding;
        this.mIntrinsicWidth = paramShapeState.mIntrinsicWidth;
        this.mIntrinsicHeight = paramShapeState.mIntrinsicHeight;
        this.mAlpha = paramShapeState.mAlpha;
        this.mShaderFactory = paramShapeState.mShaderFactory;
        return;
      }
      this.mPaint = new Paint(1);
    }
    
    public boolean canApplyTheme()
    {
      if (this.mThemeAttrs == null)
      {
        if (this.mTint != null) {
          return this.mTint.canApplyTheme();
        }
      }
      else {
        return true;
      }
      return false;
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
      return new ShapeDrawable(this, null, null);
    }
    
    public Drawable newDrawable(Resources paramResources)
    {
      return new ShapeDrawable(this, paramResources, null);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/drawable/ShapeDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */