package android.graphics.drawable;

import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.MathUtils;
import android.util.TypedValue;
import com.android.internal.R.styleable;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class RotateDrawable
  extends DrawableWrapper
{
  private static final int MAX_LEVEL = 10000;
  private RotateState mState;
  
  public RotateDrawable()
  {
    this(new RotateState(null, null), null);
  }
  
  private RotateDrawable(RotateState paramRotateState, Resources paramResources)
  {
    super(paramRotateState, paramResources);
    this.mState = paramRotateState;
  }
  
  private void updateStateFromTypedArray(TypedArray paramTypedArray)
  {
    boolean bool2 = true;
    RotateState localRotateState = this.mState;
    if (localRotateState == null) {
      return;
    }
    localRotateState.mChangingConfigurations |= paramTypedArray.getChangingConfigurations();
    RotateState.-set0(localRotateState, paramTypedArray.extractThemeAttrs());
    TypedValue localTypedValue;
    boolean bool1;
    if (paramTypedArray.hasValue(4))
    {
      localTypedValue = paramTypedArray.peekValue(4);
      if (localTypedValue.type == 6)
      {
        bool1 = true;
        localRotateState.mPivotXRel = bool1;
        if (!localRotateState.mPivotXRel) {
          break label197;
        }
        f = localTypedValue.getFraction(1.0F, 1.0F);
        label89:
        localRotateState.mPivotX = f;
      }
    }
    else if (paramTypedArray.hasValue(5))
    {
      localTypedValue = paramTypedArray.peekValue(5);
      if (localTypedValue.type != 6) {
        break label206;
      }
      bool1 = bool2;
      label123:
      localRotateState.mPivotYRel = bool1;
      if (!localRotateState.mPivotYRel) {
        break label211;
      }
    }
    label197:
    label206:
    label211:
    for (float f = localTypedValue.getFraction(1.0F, 1.0F);; f = localTypedValue.getFloat())
    {
      localRotateState.mPivotY = f;
      localRotateState.mFromDegrees = paramTypedArray.getFloat(2, localRotateState.mFromDegrees);
      localRotateState.mToDegrees = paramTypedArray.getFloat(3, localRotateState.mToDegrees);
      localRotateState.mCurrentDegrees = localRotateState.mFromDegrees;
      return;
      bool1 = false;
      break;
      f = localTypedValue.getFloat();
      break label89;
      bool1 = false;
      break label123;
    }
  }
  
  private void verifyRequiredAttributes(TypedArray paramTypedArray)
    throws XmlPullParserException
  {
    if ((getDrawable() == null) && ((RotateState.-get0(this.mState) == null) || (RotateState.-get0(this.mState)[1] == 0))) {
      throw new XmlPullParserException(paramTypedArray.getPositionDescription() + ": <rotate> tag requires a 'drawable' attribute or " + "child tag defining a drawable");
    }
  }
  
  public void applyTheme(Resources.Theme paramTheme)
  {
    super.applyTheme(paramTheme);
    RotateState localRotateState = this.mState;
    if (localRotateState == null) {
      return;
    }
    if (RotateState.-get0(localRotateState) != null) {
      paramTheme = paramTheme.resolveAttributes(RotateState.-get0(localRotateState), R.styleable.RotateDrawable);
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
  
  public void draw(Canvas paramCanvas)
  {
    Drawable localDrawable = getDrawable();
    Rect localRect = localDrawable.getBounds();
    int i = localRect.right;
    int j = localRect.left;
    int k = localRect.bottom;
    int m = localRect.top;
    RotateState localRotateState = this.mState;
    float f1;
    if (localRotateState.mPivotXRel)
    {
      f1 = (i - j) * localRotateState.mPivotX;
      if (!localRotateState.mPivotYRel) {
        break label142;
      }
    }
    label142:
    for (float f2 = (k - m) * localRotateState.mPivotY;; f2 = localRotateState.mPivotY)
    {
      i = paramCanvas.save();
      paramCanvas.rotate(localRotateState.mCurrentDegrees, localRect.left + f1, localRect.top + f2);
      localDrawable.draw(paramCanvas);
      paramCanvas.restoreToCount(i);
      return;
      f1 = localRotateState.mPivotX;
      break;
    }
  }
  
  public float getFromDegrees()
  {
    return this.mState.mFromDegrees;
  }
  
  public float getPivotX()
  {
    return this.mState.mPivotX;
  }
  
  public float getPivotY()
  {
    return this.mState.mPivotY;
  }
  
  public float getToDegrees()
  {
    return this.mState.mToDegrees;
  }
  
  public void inflate(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    TypedArray localTypedArray = obtainAttributes(paramResources, paramTheme, paramAttributeSet, R.styleable.RotateDrawable);
    super.inflate(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
    updateStateFromTypedArray(localTypedArray);
    verifyRequiredAttributes(localTypedArray);
    localTypedArray.recycle();
  }
  
  public boolean isPivotXRelative()
  {
    return this.mState.mPivotXRel;
  }
  
  public boolean isPivotYRelative()
  {
    return this.mState.mPivotYRel;
  }
  
  DrawableWrapper.DrawableWrapperState mutateConstantState()
  {
    this.mState = new RotateState(this.mState, null);
    return this.mState;
  }
  
  protected boolean onLevelChange(int paramInt)
  {
    super.onLevelChange(paramInt);
    float f = paramInt / 10000.0F;
    f = MathUtils.lerp(this.mState.mFromDegrees, this.mState.mToDegrees, f);
    this.mState.mCurrentDegrees = f;
    invalidateSelf();
    return true;
  }
  
  public void setFromDegrees(float paramFloat)
  {
    if (this.mState.mFromDegrees != paramFloat)
    {
      this.mState.mFromDegrees = paramFloat;
      invalidateSelf();
    }
  }
  
  public void setPivotX(float paramFloat)
  {
    if (this.mState.mPivotX != paramFloat)
    {
      this.mState.mPivotX = paramFloat;
      invalidateSelf();
    }
  }
  
  public void setPivotXRelative(boolean paramBoolean)
  {
    if (this.mState.mPivotXRel != paramBoolean)
    {
      this.mState.mPivotXRel = paramBoolean;
      invalidateSelf();
    }
  }
  
  public void setPivotY(float paramFloat)
  {
    if (this.mState.mPivotY != paramFloat)
    {
      this.mState.mPivotY = paramFloat;
      invalidateSelf();
    }
  }
  
  public void setPivotYRelative(boolean paramBoolean)
  {
    if (this.mState.mPivotYRel != paramBoolean)
    {
      this.mState.mPivotYRel = paramBoolean;
      invalidateSelf();
    }
  }
  
  public void setToDegrees(float paramFloat)
  {
    if (this.mState.mToDegrees != paramFloat)
    {
      this.mState.mToDegrees = paramFloat;
      invalidateSelf();
    }
  }
  
  static final class RotateState
    extends DrawableWrapper.DrawableWrapperState
  {
    float mCurrentDegrees = 0.0F;
    float mFromDegrees = 0.0F;
    float mPivotX = 0.5F;
    boolean mPivotXRel = true;
    float mPivotY = 0.5F;
    boolean mPivotYRel = true;
    private int[] mThemeAttrs;
    float mToDegrees = 360.0F;
    
    RotateState(RotateState paramRotateState, Resources paramResources)
    {
      super(paramResources);
      if (paramRotateState != null)
      {
        this.mPivotXRel = paramRotateState.mPivotXRel;
        this.mPivotX = paramRotateState.mPivotX;
        this.mPivotYRel = paramRotateState.mPivotYRel;
        this.mPivotY = paramRotateState.mPivotY;
        this.mFromDegrees = paramRotateState.mFromDegrees;
        this.mToDegrees = paramRotateState.mToDegrees;
        this.mCurrentDegrees = paramRotateState.mCurrentDegrees;
      }
    }
    
    public Drawable newDrawable(Resources paramResources)
    {
      return new RotateDrawable(this, paramResources, null);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/drawable/RotateDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */