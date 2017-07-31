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
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import com.android.internal.R.styleable;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ColorDrawable
  extends Drawable
{
  @ViewDebug.ExportedProperty(deepExport=true, prefix="state_")
  private ColorState mColorState;
  private boolean mMutated;
  private final Paint mPaint = new Paint(1);
  private PorterDuffColorFilter mTintFilter;
  
  public ColorDrawable()
  {
    this.mColorState = new ColorState();
  }
  
  public ColorDrawable(int paramInt)
  {
    this.mColorState = new ColorState();
    setColor(paramInt);
  }
  
  private ColorDrawable(ColorState paramColorState, Resources paramResources)
  {
    this.mColorState = paramColorState;
    updateLocalState(paramResources);
  }
  
  private void updateLocalState(Resources paramResources)
  {
    this.mTintFilter = updateTintFilter(this.mTintFilter, this.mColorState.mTint, this.mColorState.mTintMode);
  }
  
  private void updateStateFromTypedArray(TypedArray paramTypedArray)
  {
    ColorState localColorState = this.mColorState;
    localColorState.mChangingConfigurations |= paramTypedArray.getChangingConfigurations();
    localColorState.mThemeAttrs = paramTypedArray.extractThemeAttrs();
    localColorState.mBaseColor = paramTypedArray.getColor(0, localColorState.mBaseColor);
    localColorState.mUseColor = localColorState.mBaseColor;
  }
  
  public void applyTheme(Resources.Theme paramTheme)
  {
    super.applyTheme(paramTheme);
    ColorState localColorState = this.mColorState;
    if (localColorState == null) {
      return;
    }
    if (localColorState.mThemeAttrs != null)
    {
      TypedArray localTypedArray = paramTheme.resolveAttributes(localColorState.mThemeAttrs, R.styleable.ColorDrawable);
      updateStateFromTypedArray(localTypedArray);
      localTypedArray.recycle();
    }
    if ((localColorState.mTint != null) && (localColorState.mTint.canApplyTheme())) {
      localColorState.mTint = localColorState.mTint.obtainForTheme(paramTheme);
    }
    updateLocalState(paramTheme.getResources());
  }
  
  public boolean canApplyTheme()
  {
    if (!this.mColorState.canApplyTheme()) {
      return super.canApplyTheme();
    }
    return true;
  }
  
  public void clearMutated()
  {
    super.clearMutated();
    this.mMutated = false;
  }
  
  public void draw(Canvas paramCanvas)
  {
    ColorFilter localColorFilter = this.mPaint.getColorFilter();
    if ((this.mColorState.mUseColor >>> 24 != 0) || (localColorFilter != null)) {}
    for (;;)
    {
      if (localColorFilter == null) {
        this.mPaint.setColorFilter(this.mTintFilter);
      }
      this.mPaint.setColor(this.mColorState.mUseColor);
      paramCanvas.drawRect(getBounds(), this.mPaint);
      this.mPaint.setColorFilter(localColorFilter);
      do
      {
        return;
      } while (this.mTintFilter == null);
    }
  }
  
  public int getAlpha()
  {
    return this.mColorState.mUseColor >>> 24;
  }
  
  public int getChangingConfigurations()
  {
    return super.getChangingConfigurations() | this.mColorState.getChangingConfigurations();
  }
  
  public int getColor()
  {
    return this.mColorState.mUseColor;
  }
  
  public Drawable.ConstantState getConstantState()
  {
    return this.mColorState;
  }
  
  public int getOpacity()
  {
    if ((this.mTintFilter != null) || (this.mPaint.getColorFilter() != null)) {
      return -3;
    }
    switch (this.mColorState.mUseColor >>> 24)
    {
    default: 
      return -3;
    case 255: 
      return -1;
    }
    return -2;
  }
  
  public void getOutline(Outline paramOutline)
  {
    paramOutline.setRect(getBounds());
    paramOutline.setAlpha(getAlpha() / 255.0F);
  }
  
  public void inflate(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    super.inflate(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
    paramXmlPullParser = obtainAttributes(paramResources, paramTheme, paramAttributeSet, R.styleable.ColorDrawable);
    updateStateFromTypedArray(paramXmlPullParser);
    paramXmlPullParser.recycle();
    updateLocalState(paramResources);
  }
  
  public boolean isStateful()
  {
    if (this.mColorState.mTint != null) {
      return this.mColorState.mTint.isStateful();
    }
    return false;
  }
  
  public Drawable mutate()
  {
    if ((!this.mMutated) && (super.mutate() == this))
    {
      this.mColorState = new ColorState(this.mColorState);
      this.mMutated = true;
    }
    return this;
  }
  
  protected boolean onStateChange(int[] paramArrayOfInt)
  {
    paramArrayOfInt = this.mColorState;
    if ((paramArrayOfInt.mTint != null) && (paramArrayOfInt.mTintMode != null))
    {
      this.mTintFilter = updateTintFilter(this.mTintFilter, paramArrayOfInt.mTint, paramArrayOfInt.mTintMode);
      return true;
    }
    return false;
  }
  
  public void setAlpha(int paramInt)
  {
    int i = this.mColorState.mBaseColor;
    paramInt = this.mColorState.mBaseColor << 8 >>> 8 | (i >>> 24) * (paramInt + (paramInt >> 7)) >> 8 << 24;
    if (this.mColorState.mUseColor != paramInt)
    {
      this.mColorState.mUseColor = paramInt;
      invalidateSelf();
    }
  }
  
  public void setColor(int paramInt)
  {
    if ((this.mColorState.mBaseColor != paramInt) || (this.mColorState.mUseColor != paramInt))
    {
      ColorState localColorState = this.mColorState;
      this.mColorState.mUseColor = paramInt;
      localColorState.mBaseColor = paramInt;
      invalidateSelf();
    }
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    this.mPaint.setColorFilter(paramColorFilter);
  }
  
  public void setTintList(ColorStateList paramColorStateList)
  {
    this.mColorState.mTint = paramColorStateList;
    this.mTintFilter = updateTintFilter(this.mTintFilter, paramColorStateList, this.mColorState.mTintMode);
    invalidateSelf();
  }
  
  public void setTintMode(PorterDuff.Mode paramMode)
  {
    this.mColorState.mTintMode = paramMode;
    this.mTintFilter = updateTintFilter(this.mTintFilter, this.mColorState.mTint, paramMode);
    invalidateSelf();
  }
  
  static final class ColorState
    extends Drawable.ConstantState
  {
    int mBaseColor;
    int mChangingConfigurations;
    int[] mThemeAttrs;
    ColorStateList mTint = null;
    PorterDuff.Mode mTintMode = ColorDrawable.DEFAULT_TINT_MODE;
    @ViewDebug.ExportedProperty
    int mUseColor;
    
    ColorState() {}
    
    ColorState(ColorState paramColorState)
    {
      this.mThemeAttrs = paramColorState.mThemeAttrs;
      this.mBaseColor = paramColorState.mBaseColor;
      this.mUseColor = paramColorState.mUseColor;
      this.mChangingConfigurations = paramColorState.mChangingConfigurations;
      this.mTint = paramColorState.mTint;
      this.mTintMode = paramColorState.mTintMode;
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
      return new ColorDrawable(this, null, null);
    }
    
    public Drawable newDrawable(Resources paramResources)
    {
      return new ColorDrawable(this, paramResources, null);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/drawable/ColorDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */