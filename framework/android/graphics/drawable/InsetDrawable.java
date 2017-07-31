package android.graphics.drawable;

import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Insets;
import android.graphics.Outline;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import com.android.internal.R.styleable;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class InsetDrawable
  extends DrawableWrapper
{
  private InsetState mState;
  private final Rect mTmpRect = new Rect();
  
  InsetDrawable()
  {
    this(new InsetState(null, null), null);
  }
  
  public InsetDrawable(Drawable paramDrawable, int paramInt)
  {
    this(paramDrawable, paramInt, paramInt, paramInt, paramInt);
  }
  
  public InsetDrawable(Drawable paramDrawable, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this(new InsetState(null, null), null);
    this.mState.mInsetLeft = paramInt1;
    this.mState.mInsetTop = paramInt2;
    this.mState.mInsetRight = paramInt3;
    this.mState.mInsetBottom = paramInt4;
    setDrawable(paramDrawable);
  }
  
  private InsetDrawable(InsetState paramInsetState, Resources paramResources)
  {
    super(paramInsetState, paramResources);
    this.mState = paramInsetState;
  }
  
  private void updateStateFromTypedArray(TypedArray paramTypedArray)
  {
    InsetState localInsetState = this.mState;
    if (localInsetState == null) {
      return;
    }
    localInsetState.mChangingConfigurations |= paramTypedArray.getChangingConfigurations();
    InsetState.-set0(localInsetState, paramTypedArray.extractThemeAttrs());
    if (paramTypedArray.hasValue(6))
    {
      int i = paramTypedArray.getDimensionPixelOffset(6, 0);
      localInsetState.mInsetLeft = i;
      localInsetState.mInsetTop = i;
      localInsetState.mInsetRight = i;
      localInsetState.mInsetBottom = i;
    }
    localInsetState.mInsetLeft = paramTypedArray.getDimensionPixelOffset(2, localInsetState.mInsetLeft);
    localInsetState.mInsetRight = paramTypedArray.getDimensionPixelOffset(3, localInsetState.mInsetRight);
    localInsetState.mInsetTop = paramTypedArray.getDimensionPixelOffset(4, localInsetState.mInsetTop);
    localInsetState.mInsetBottom = paramTypedArray.getDimensionPixelOffset(5, localInsetState.mInsetBottom);
  }
  
  private void verifyRequiredAttributes(TypedArray paramTypedArray)
    throws XmlPullParserException
  {
    if ((getDrawable() == null) && ((InsetState.-get0(this.mState) == null) || (InsetState.-get0(this.mState)[1] == 0))) {
      throw new XmlPullParserException(paramTypedArray.getPositionDescription() + ": <inset> tag requires a 'drawable' attribute or " + "child tag defining a drawable");
    }
  }
  
  public void applyTheme(Resources.Theme paramTheme)
  {
    super.applyTheme(paramTheme);
    InsetState localInsetState = this.mState;
    if (localInsetState == null) {
      return;
    }
    if (InsetState.-get0(localInsetState) != null) {
      paramTheme = paramTheme.resolveAttributes(InsetState.-get0(localInsetState), R.styleable.InsetDrawable);
    }
    try
    {
      updateStateFromTypedArray(paramTheme);
      verifyRequiredAttributes(paramTheme);
      return;
    }
    catch (XmlPullParserException localXmlPullParserException)
    {
      rethrowAsRuntimeException(localXmlPullParserException);
      return;
    }
    finally
    {
      paramTheme.recycle();
    }
  }
  
  public int getIntrinsicHeight()
  {
    int i = getDrawable().getIntrinsicHeight();
    if (i < 0) {
      return -1;
    }
    return this.mState.mInsetTop + i + this.mState.mInsetBottom;
  }
  
  public int getIntrinsicWidth()
  {
    int i = getDrawable().getIntrinsicWidth();
    if (i < 0) {
      return -1;
    }
    return this.mState.mInsetLeft + i + this.mState.mInsetRight;
  }
  
  public int getOpacity()
  {
    InsetState localInsetState = this.mState;
    int i = getDrawable().getOpacity();
    if (i == -1)
    {
      if ((localInsetState.mInsetLeft > 0) || (localInsetState.mInsetTop > 0)) {}
      while ((localInsetState.mInsetRight > 0) || (localInsetState.mInsetBottom > 0)) {
        return -3;
      }
    }
    return i;
  }
  
  public Insets getOpticalInsets()
  {
    Insets localInsets = super.getOpticalInsets();
    return Insets.of(localInsets.left + this.mState.mInsetLeft, localInsets.top + this.mState.mInsetTop, localInsets.right + this.mState.mInsetRight, localInsets.bottom + this.mState.mInsetBottom);
  }
  
  public void getOutline(Outline paramOutline)
  {
    getDrawable().getOutline(paramOutline);
  }
  
  public boolean getPadding(Rect paramRect)
  {
    boolean bool = super.getPadding(paramRect);
    paramRect.left += this.mState.mInsetLeft;
    paramRect.right += this.mState.mInsetRight;
    paramRect.top += this.mState.mInsetTop;
    paramRect.bottom += this.mState.mInsetBottom;
    return (bool) || ((this.mState.mInsetLeft | this.mState.mInsetRight | this.mState.mInsetTop | this.mState.mInsetBottom) != 0);
  }
  
  public void inflate(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    TypedArray localTypedArray = obtainAttributes(paramResources, paramTheme, paramAttributeSet, R.styleable.InsetDrawable);
    super.inflate(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
    updateStateFromTypedArray(localTypedArray);
    verifyRequiredAttributes(localTypedArray);
    localTypedArray.recycle();
  }
  
  DrawableWrapper.DrawableWrapperState mutateConstantState()
  {
    this.mState = new InsetState(this.mState, null);
    return this.mState;
  }
  
  protected void onBoundsChange(Rect paramRect)
  {
    Rect localRect = this.mTmpRect;
    localRect.set(paramRect);
    localRect.left += this.mState.mInsetLeft;
    localRect.top += this.mState.mInsetTop;
    localRect.right -= this.mState.mInsetRight;
    localRect.bottom -= this.mState.mInsetBottom;
    super.onBoundsChange(localRect);
  }
  
  static final class InsetState
    extends DrawableWrapper.DrawableWrapperState
  {
    int mInsetBottom = 0;
    int mInsetLeft = 0;
    int mInsetRight = 0;
    int mInsetTop = 0;
    private int[] mThemeAttrs;
    
    InsetState(InsetState paramInsetState, Resources paramResources)
    {
      super(paramResources);
      if (paramInsetState != null)
      {
        this.mInsetLeft = paramInsetState.mInsetLeft;
        this.mInsetTop = paramInsetState.mInsetTop;
        this.mInsetRight = paramInsetState.mInsetRight;
        this.mInsetBottom = paramInsetState.mInsetBottom;
        if (paramInsetState.mDensity != this.mDensity) {
          applyDensityScaling(paramInsetState.mDensity, this.mDensity);
        }
      }
    }
    
    private void applyDensityScaling(int paramInt1, int paramInt2)
    {
      this.mInsetLeft = Bitmap.scaleFromDensity(this.mInsetLeft, paramInt1, paramInt2);
      this.mInsetTop = Bitmap.scaleFromDensity(this.mInsetTop, paramInt1, paramInt2);
      this.mInsetRight = Bitmap.scaleFromDensity(this.mInsetRight, paramInt1, paramInt2);
      this.mInsetBottom = Bitmap.scaleFromDensity(this.mInsetBottom, paramInt1, paramInt2);
    }
    
    public Drawable newDrawable(Resources paramResources)
    {
      InsetState localInsetState;
      if (paramResources != null)
      {
        int i = paramResources.getDisplayMetrics().densityDpi;
        if (i == 0)
        {
          i = 160;
          if (i == this.mDensity) {
            break label52;
          }
          localInsetState = new InsetState(this, paramResources);
        }
      }
      for (;;)
      {
        return new InsetDrawable(localInsetState, paramResources, null);
        break;
        label52:
        localInsetState = this;
        continue;
        localInsetState = this;
      }
    }
    
    void onDensityChanged(int paramInt1, int paramInt2)
    {
      super.onDensityChanged(paramInt1, paramInt2);
      applyDensityScaling(paramInt1, paramInt2);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/drawable/InsetDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */