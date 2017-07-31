package android.graphics.drawable;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Insets;
import android.graphics.Outline;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import com.android.internal.R.styleable;
import java.io.IOException;
import java.util.Collection;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public abstract class DrawableWrapper
  extends Drawable
  implements Drawable.Callback
{
  private Drawable mDrawable;
  private boolean mMutated;
  private DrawableWrapperState mState;
  
  public DrawableWrapper(Drawable paramDrawable)
  {
    this.mState = null;
    this.mDrawable = paramDrawable;
  }
  
  DrawableWrapper(DrawableWrapperState paramDrawableWrapperState, Resources paramResources)
  {
    this.mState = paramDrawableWrapperState;
    updateLocalState(paramResources);
  }
  
  private void inflateChildDrawable(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    Drawable localDrawable = null;
    int i = paramXmlPullParser.getDepth();
    for (;;)
    {
      int j = paramXmlPullParser.next();
      if ((j == 1) || ((j == 3) && (paramXmlPullParser.getDepth() <= i))) {
        break;
      }
      if (j == 2) {
        localDrawable = Drawable.createFromXmlInner(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
      }
    }
    if (localDrawable != null) {
      setDrawable(localDrawable);
    }
  }
  
  private void updateLocalState(Resources paramResources)
  {
    if ((this.mState != null) && (this.mState.mDrawableState != null)) {
      setDrawable(this.mState.mDrawableState.newDrawable(paramResources));
    }
  }
  
  private void updateStateFromTypedArray(TypedArray paramTypedArray)
  {
    DrawableWrapperState localDrawableWrapperState = this.mState;
    if (localDrawableWrapperState == null) {
      return;
    }
    localDrawableWrapperState.mChangingConfigurations |= paramTypedArray.getChangingConfigurations();
    DrawableWrapperState.-set0(localDrawableWrapperState, paramTypedArray.extractThemeAttrs());
    if (paramTypedArray.hasValueOrEmpty(0)) {
      setDrawable(paramTypedArray.getDrawable(0));
    }
  }
  
  public void applyTheme(Resources.Theme paramTheme)
  {
    super.applyTheme(paramTheme);
    if ((this.mDrawable != null) && (this.mDrawable.canApplyTheme())) {
      this.mDrawable.applyTheme(paramTheme);
    }
    DrawableWrapperState localDrawableWrapperState = this.mState;
    if (localDrawableWrapperState == null) {
      return;
    }
    int i = paramTheme.getResources().getDisplayMetrics().densityDpi;
    if (i == 0) {
      i = 160;
    }
    for (;;)
    {
      localDrawableWrapperState.setDensity(i);
      if (DrawableWrapperState.-get0(localDrawableWrapperState) != null)
      {
        paramTheme = paramTheme.resolveAttributes(DrawableWrapperState.-get0(localDrawableWrapperState), R.styleable.DrawableWrapper);
        updateStateFromTypedArray(paramTheme);
        paramTheme.recycle();
      }
      return;
    }
  }
  
  public boolean canApplyTheme()
  {
    if ((this.mState == null) || (!this.mState.canApplyTheme())) {
      return super.canApplyTheme();
    }
    return true;
  }
  
  public void clearMutated()
  {
    super.clearMutated();
    if (this.mDrawable != null) {
      this.mDrawable.clearMutated();
    }
    this.mMutated = false;
  }
  
  public void draw(Canvas paramCanvas)
  {
    if (this.mDrawable != null) {
      this.mDrawable.draw(paramCanvas);
    }
  }
  
  public int getAlpha()
  {
    if (this.mDrawable != null) {
      return this.mDrawable.getAlpha();
    }
    return 255;
  }
  
  public int getChangingConfigurations()
  {
    int j = super.getChangingConfigurations();
    if (this.mState != null) {}
    for (int i = this.mState.getChangingConfigurations();; i = 0) {
      return i | j | this.mDrawable.getChangingConfigurations();
    }
  }
  
  public Drawable.ConstantState getConstantState()
  {
    if ((this.mState != null) && (this.mState.canConstantState()))
    {
      this.mState.mChangingConfigurations = getChangingConfigurations();
      return this.mState;
    }
    return null;
  }
  
  public Drawable getDrawable()
  {
    return this.mDrawable;
  }
  
  public void getHotspotBounds(Rect paramRect)
  {
    if (this.mDrawable != null)
    {
      this.mDrawable.getHotspotBounds(paramRect);
      return;
    }
    paramRect.set(getBounds());
  }
  
  public int getIntrinsicHeight()
  {
    if (this.mDrawable != null) {
      return this.mDrawable.getIntrinsicHeight();
    }
    return -1;
  }
  
  public int getIntrinsicWidth()
  {
    if (this.mDrawable != null) {
      return this.mDrawable.getIntrinsicWidth();
    }
    return -1;
  }
  
  public int getOpacity()
  {
    if (this.mDrawable != null) {
      return this.mDrawable.getOpacity();
    }
    return -2;
  }
  
  public Insets getOpticalInsets()
  {
    if (this.mDrawable != null) {
      return this.mDrawable.getOpticalInsets();
    }
    return Insets.NONE;
  }
  
  public void getOutline(Outline paramOutline)
  {
    if (this.mDrawable != null)
    {
      this.mDrawable.getOutline(paramOutline);
      return;
    }
    super.getOutline(paramOutline);
  }
  
  public boolean getPadding(Rect paramRect)
  {
    if (this.mDrawable != null) {
      return this.mDrawable.getPadding(paramRect);
    }
    return false;
  }
  
  public void inflate(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    super.inflate(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
    Object localObject = this.mState;
    if (localObject == null) {
      return;
    }
    int i = paramResources.getDisplayMetrics().densityDpi;
    if (i == 0) {
      i = 160;
    }
    for (;;)
    {
      ((DrawableWrapperState)localObject).setDensity(i);
      localObject = obtainAttributes(paramResources, paramTheme, paramAttributeSet, R.styleable.DrawableWrapper);
      updateStateFromTypedArray((TypedArray)localObject);
      ((TypedArray)localObject).recycle();
      inflateChildDrawable(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
      return;
    }
  }
  
  public void invalidateDrawable(Drawable paramDrawable)
  {
    paramDrawable = getCallback();
    if (paramDrawable != null) {
      paramDrawable.invalidateDrawable(this);
    }
  }
  
  public boolean isStateful()
  {
    if (this.mDrawable != null) {
      return this.mDrawable.isStateful();
    }
    return false;
  }
  
  public Drawable mutate()
  {
    Drawable.ConstantState localConstantState = null;
    if ((!this.mMutated) && (super.mutate() == this))
    {
      this.mState = mutateConstantState();
      if (this.mDrawable != null) {
        this.mDrawable.mutate();
      }
      if (this.mState != null)
      {
        DrawableWrapperState localDrawableWrapperState = this.mState;
        if (this.mDrawable != null) {
          localConstantState = this.mDrawable.getConstantState();
        }
        localDrawableWrapperState.mDrawableState = localConstantState;
      }
      this.mMutated = true;
    }
    return this;
  }
  
  DrawableWrapperState mutateConstantState()
  {
    return this.mState;
  }
  
  protected void onBoundsChange(Rect paramRect)
  {
    if (this.mDrawable != null) {
      this.mDrawable.setBounds(paramRect);
    }
  }
  
  public boolean onLayoutDirectionChanged(int paramInt)
  {
    if (this.mDrawable != null) {
      return this.mDrawable.setLayoutDirection(paramInt);
    }
    return false;
  }
  
  protected boolean onLevelChange(int paramInt)
  {
    if (this.mDrawable != null) {
      return this.mDrawable.setLevel(paramInt);
    }
    return false;
  }
  
  protected boolean onStateChange(int[] paramArrayOfInt)
  {
    if ((this.mDrawable != null) && (this.mDrawable.isStateful()))
    {
      boolean bool = this.mDrawable.setState(paramArrayOfInt);
      if (bool) {
        onBoundsChange(getBounds());
      }
      return bool;
    }
    return false;
  }
  
  public void scheduleDrawable(Drawable paramDrawable, Runnable paramRunnable, long paramLong)
  {
    paramDrawable = getCallback();
    if (paramDrawable != null) {
      paramDrawable.scheduleDrawable(this, paramRunnable, paramLong);
    }
  }
  
  public void setAlpha(int paramInt)
  {
    if (this.mDrawable != null) {
      this.mDrawable.setAlpha(paramInt);
    }
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    if (this.mDrawable != null) {
      this.mDrawable.setColorFilter(paramColorFilter);
    }
  }
  
  public void setDrawable(Drawable paramDrawable)
  {
    if (this.mDrawable != null) {
      this.mDrawable.setCallback(null);
    }
    this.mDrawable = paramDrawable;
    if (paramDrawable != null)
    {
      paramDrawable.setCallback(this);
      paramDrawable.setVisible(isVisible(), true);
      paramDrawable.setState(getState());
      paramDrawable.setLevel(getLevel());
      paramDrawable.setBounds(getBounds());
      paramDrawable.setLayoutDirection(getLayoutDirection());
      if (this.mState != null) {
        this.mState.mDrawableState = paramDrawable.getConstantState();
      }
    }
    invalidateSelf();
  }
  
  public void setHotspot(float paramFloat1, float paramFloat2)
  {
    if (this.mDrawable != null) {
      this.mDrawable.setHotspot(paramFloat1, paramFloat2);
    }
  }
  
  public void setHotspotBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.mDrawable != null) {
      this.mDrawable.setHotspotBounds(paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  public void setTintList(ColorStateList paramColorStateList)
  {
    if (this.mDrawable != null) {
      this.mDrawable.setTintList(paramColorStateList);
    }
  }
  
  public void setTintMode(PorterDuff.Mode paramMode)
  {
    if (this.mDrawable != null) {
      this.mDrawable.setTintMode(paramMode);
    }
  }
  
  public boolean setVisible(boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool = super.setVisible(paramBoolean1, paramBoolean2);
    if (this.mDrawable != null) {}
    for (paramBoolean1 = this.mDrawable.setVisible(paramBoolean1, paramBoolean2);; paramBoolean1 = false) {
      return bool | paramBoolean1;
    }
  }
  
  public void unscheduleDrawable(Drawable paramDrawable, Runnable paramRunnable)
  {
    paramDrawable = getCallback();
    if (paramDrawable != null) {
      paramDrawable.unscheduleDrawable(this, paramRunnable);
    }
  }
  
  static abstract class DrawableWrapperState
    extends Drawable.ConstantState
  {
    int mChangingConfigurations;
    int mDensity = 160;
    Drawable.ConstantState mDrawableState;
    private int[] mThemeAttrs;
    
    DrawableWrapperState(DrawableWrapperState paramDrawableWrapperState, Resources paramResources)
    {
      if (paramDrawableWrapperState != null)
      {
        this.mThemeAttrs = paramDrawableWrapperState.mThemeAttrs;
        this.mChangingConfigurations = paramDrawableWrapperState.mChangingConfigurations;
        this.mDrawableState = paramDrawableWrapperState.mDrawableState;
      }
      int i;
      if (paramResources != null) {
        i = paramResources.getDisplayMetrics().densityDpi;
      }
      for (;;)
      {
        int j = i;
        if (i == 0) {
          j = 160;
        }
        this.mDensity = j;
        return;
        if (paramDrawableWrapperState != null) {
          i = paramDrawableWrapperState.mDensity;
        } else {
          i = 0;
        }
      }
    }
    
    public int addAtlasableBitmaps(Collection<Bitmap> paramCollection)
    {
      Drawable.ConstantState localConstantState = this.mDrawableState;
      if (localConstantState != null) {
        return localConstantState.addAtlasableBitmaps(paramCollection);
      }
      return 0;
    }
    
    public boolean canApplyTheme()
    {
      if ((this.mThemeAttrs == null) && ((this.mDrawableState == null) || (!this.mDrawableState.canApplyTheme()))) {
        return super.canApplyTheme();
      }
      return true;
    }
    
    public boolean canConstantState()
    {
      return this.mDrawableState != null;
    }
    
    public int getChangingConfigurations()
    {
      int j = this.mChangingConfigurations;
      if (this.mDrawableState != null) {}
      for (int i = this.mDrawableState.getChangingConfigurations();; i = 0) {
        return i | j;
      }
    }
    
    public Drawable newDrawable()
    {
      return newDrawable(null);
    }
    
    public abstract Drawable newDrawable(Resources paramResources);
    
    void onDensityChanged(int paramInt1, int paramInt2) {}
    
    public final void setDensity(int paramInt)
    {
      if (this.mDensity != paramInt)
      {
        int i = this.mDensity;
        this.mDensity = paramInt;
        onDensityChanged(i, paramInt);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/drawable/DrawableWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */