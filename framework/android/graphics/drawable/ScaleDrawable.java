package android.graphics.drawable;

import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import com.android.internal.R.styleable;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ScaleDrawable
  extends DrawableWrapper
{
  private static final int MAX_LEVEL = 10000;
  private ScaleState mState;
  private final Rect mTmpRect = new Rect();
  
  ScaleDrawable()
  {
    this(new ScaleState(null, null), null);
  }
  
  public ScaleDrawable(Drawable paramDrawable, int paramInt, float paramFloat1, float paramFloat2)
  {
    this(new ScaleState(null, null), null);
    this.mState.mGravity = paramInt;
    this.mState.mScaleWidth = paramFloat1;
    this.mState.mScaleHeight = paramFloat2;
    setDrawable(paramDrawable);
  }
  
  private ScaleDrawable(ScaleState paramScaleState, Resources paramResources)
  {
    super(paramScaleState, paramResources);
    this.mState = paramScaleState;
    updateLocalState();
  }
  
  private static float getPercent(TypedArray paramTypedArray, int paramInt, float paramFloat)
  {
    int i = paramTypedArray.getType(paramInt);
    if ((i == 6) || (i == 0)) {
      return paramTypedArray.getFraction(paramInt, 1, 1, paramFloat);
    }
    paramTypedArray = paramTypedArray.getString(paramInt);
    if ((paramTypedArray != null) && (paramTypedArray.endsWith("%"))) {
      return Float.parseFloat(paramTypedArray.substring(0, paramTypedArray.length() - 1)) / 100.0F;
    }
    return paramFloat;
  }
  
  private void updateLocalState()
  {
    setLevel(this.mState.mInitialLevel);
  }
  
  private void updateStateFromTypedArray(TypedArray paramTypedArray)
  {
    ScaleState localScaleState = this.mState;
    if (localScaleState == null) {
      return;
    }
    localScaleState.mChangingConfigurations |= paramTypedArray.getChangingConfigurations();
    ScaleState.-set0(localScaleState, paramTypedArray.extractThemeAttrs());
    localScaleState.mScaleWidth = getPercent(paramTypedArray, 1, localScaleState.mScaleWidth);
    localScaleState.mScaleHeight = getPercent(paramTypedArray, 2, localScaleState.mScaleHeight);
    localScaleState.mGravity = paramTypedArray.getInt(3, localScaleState.mGravity);
    localScaleState.mUseIntrinsicSizeAsMin = paramTypedArray.getBoolean(4, localScaleState.mUseIntrinsicSizeAsMin);
    localScaleState.mInitialLevel = paramTypedArray.getInt(5, localScaleState.mInitialLevel);
  }
  
  private void verifyRequiredAttributes(TypedArray paramTypedArray)
    throws XmlPullParserException
  {
    if ((getDrawable() == null) && ((ScaleState.-get0(this.mState) == null) || (ScaleState.-get0(this.mState)[0] == 0))) {
      throw new XmlPullParserException(paramTypedArray.getPositionDescription() + ": <scale> tag requires a 'drawable' attribute or " + "child tag defining a drawable");
    }
  }
  
  public void applyTheme(Resources.Theme paramTheme)
  {
    super.applyTheme(paramTheme);
    ScaleState localScaleState = this.mState;
    if (localScaleState == null) {
      return;
    }
    if (ScaleState.-get0(localScaleState) != null) {
      paramTheme = paramTheme.resolveAttributes(ScaleState.-get0(localScaleState), R.styleable.ScaleDrawable);
    }
    try
    {
      updateStateFromTypedArray(paramTheme);
      verifyRequiredAttributes(paramTheme);
    }
    catch (XmlPullParserException localXmlPullParserException)
    {
      for (;;)
      {
        rethrowAsRuntimeException(localXmlPullParserException);
        paramTheme.recycle();
      }
    }
    finally
    {
      paramTheme.recycle();
    }
    updateLocalState();
  }
  
  public void draw(Canvas paramCanvas)
  {
    Drawable localDrawable = getDrawable();
    if ((localDrawable != null) && (localDrawable.getLevel() != 0)) {
      localDrawable.draw(paramCanvas);
    }
  }
  
  public int getOpacity()
  {
    Drawable localDrawable = getDrawable();
    if (localDrawable.getLevel() == 0) {
      return -2;
    }
    int i = localDrawable.getOpacity();
    if ((i == -1) && (localDrawable.getLevel() < 10000)) {
      return -3;
    }
    return i;
  }
  
  public void inflate(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    TypedArray localTypedArray = obtainAttributes(paramResources, paramTheme, paramAttributeSet, R.styleable.ScaleDrawable);
    super.inflate(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
    updateStateFromTypedArray(localTypedArray);
    verifyRequiredAttributes(localTypedArray);
    localTypedArray.recycle();
    updateLocalState();
  }
  
  DrawableWrapper.DrawableWrapperState mutateConstantState()
  {
    this.mState = new ScaleState(this.mState, null);
    return this.mState;
  }
  
  protected void onBoundsChange(Rect paramRect)
  {
    Drawable localDrawable = getDrawable();
    Rect localRect = this.mTmpRect;
    boolean bool = this.mState.mUseIntrinsicSizeAsMin;
    int m = getLevel();
    int j = paramRect.width();
    int i = j;
    int k;
    if (this.mState.mScaleWidth > 0.0F)
    {
      if (bool)
      {
        i = localDrawable.getIntrinsicWidth();
        i = j - (int)((j - i) * (10000 - m) * this.mState.mScaleWidth / 10000.0F);
      }
    }
    else
    {
      k = paramRect.height();
      j = k;
      if (this.mState.mScaleHeight > 0.0F) {
        if (!bool) {
          break label205;
        }
      }
    }
    label205:
    for (j = localDrawable.getIntrinsicHeight();; j = 0)
    {
      j = k - (int)((k - j) * (10000 - m) * this.mState.mScaleHeight / 10000.0F);
      k = getLayoutDirection();
      Gravity.apply(this.mState.mGravity, i, j, paramRect, localRect, k);
      if ((i > 0) && (j > 0)) {
        localDrawable.setBounds(localRect.left, localRect.top, localRect.right, localRect.bottom);
      }
      return;
      i = 0;
      break;
    }
  }
  
  protected boolean onLevelChange(int paramInt)
  {
    super.onLevelChange(paramInt);
    onBoundsChange(getBounds());
    invalidateSelf();
    return true;
  }
  
  static final class ScaleState
    extends DrawableWrapper.DrawableWrapperState
  {
    private static final float DO_NOT_SCALE = -1.0F;
    int mGravity = 3;
    int mInitialLevel = 0;
    float mScaleHeight = -1.0F;
    float mScaleWidth = -1.0F;
    private int[] mThemeAttrs;
    boolean mUseIntrinsicSizeAsMin = false;
    
    ScaleState(ScaleState paramScaleState, Resources paramResources)
    {
      super(paramResources);
      if (paramScaleState != null)
      {
        this.mScaleWidth = paramScaleState.mScaleWidth;
        this.mScaleHeight = paramScaleState.mScaleHeight;
        this.mGravity = paramScaleState.mGravity;
        this.mUseIntrinsicSizeAsMin = paramScaleState.mUseIntrinsicSizeAsMin;
        this.mInitialLevel = paramScaleState.mInitialLevel;
      }
    }
    
    public Drawable newDrawable(Resources paramResources)
    {
      return new ScaleDrawable(this, paramResources, null);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/drawable/ScaleDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */