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

public class ClipDrawable
  extends DrawableWrapper
{
  public static final int HORIZONTAL = 1;
  private static final int MAX_LEVEL = 10000;
  public static final int VERTICAL = 2;
  private ClipState mState;
  private final Rect mTmpRect = new Rect();
  
  ClipDrawable()
  {
    this(new ClipState(null, null), null);
  }
  
  private ClipDrawable(ClipState paramClipState, Resources paramResources)
  {
    super(paramClipState, paramResources);
    this.mState = paramClipState;
  }
  
  public ClipDrawable(Drawable paramDrawable, int paramInt1, int paramInt2)
  {
    this(new ClipState(null, null), null);
    this.mState.mGravity = paramInt1;
    this.mState.mOrientation = paramInt2;
    setDrawable(paramDrawable);
  }
  
  private void updateStateFromTypedArray(TypedArray paramTypedArray)
  {
    ClipState localClipState = this.mState;
    if (localClipState == null) {
      return;
    }
    localClipState.mChangingConfigurations |= paramTypedArray.getChangingConfigurations();
    ClipState.-set0(localClipState, paramTypedArray.extractThemeAttrs());
    localClipState.mOrientation = paramTypedArray.getInt(2, localClipState.mOrientation);
    localClipState.mGravity = paramTypedArray.getInt(0, localClipState.mGravity);
  }
  
  private void verifyRequiredAttributes(TypedArray paramTypedArray)
    throws XmlPullParserException
  {
    if ((getDrawable() == null) && ((ClipState.-get0(this.mState) == null) || (ClipState.-get0(this.mState)[1] == 0))) {
      throw new XmlPullParserException(paramTypedArray.getPositionDescription() + ": <clip> tag requires a 'drawable' attribute or " + "child tag defining a drawable");
    }
  }
  
  public void applyTheme(Resources.Theme paramTheme)
  {
    super.applyTheme(paramTheme);
    ClipState localClipState = this.mState;
    if (localClipState == null) {
      return;
    }
    if (ClipState.-get0(localClipState) != null) {
      paramTheme = paramTheme.resolveAttributes(ClipState.-get0(localClipState), R.styleable.ClipDrawable);
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
    if (localDrawable.getLevel() == 0) {
      return;
    }
    Rect localRect1 = this.mTmpRect;
    Rect localRect2 = getBounds();
    int m = getLevel();
    int j = localRect2.width();
    int i = j;
    if ((this.mState.mOrientation & 0x1) != 0) {
      i = j - (j + 0) * (10000 - m) / 10000;
    }
    int k = localRect2.height();
    j = k;
    if ((this.mState.mOrientation & 0x2) != 0) {
      j = k - (k + 0) * (10000 - m) / 10000;
    }
    k = getLayoutDirection();
    Gravity.apply(this.mState.mGravity, i, j, localRect2, localRect1, k);
    if ((i > 0) && (j > 0))
    {
      paramCanvas.save();
      paramCanvas.clipRect(localRect1);
      localDrawable.draw(paramCanvas);
      paramCanvas.restore();
    }
  }
  
  public int getOpacity()
  {
    Drawable localDrawable = getDrawable();
    if ((localDrawable.getOpacity() == -2) || (localDrawable.getLevel() == 0)) {
      return -2;
    }
    if (getLevel() >= 10000) {
      return localDrawable.getOpacity();
    }
    return -3;
  }
  
  public void inflate(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    TypedArray localTypedArray = obtainAttributes(paramResources, paramTheme, paramAttributeSet, R.styleable.ClipDrawable);
    super.inflate(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
    updateStateFromTypedArray(localTypedArray);
    verifyRequiredAttributes(localTypedArray);
    localTypedArray.recycle();
  }
  
  DrawableWrapper.DrawableWrapperState mutateConstantState()
  {
    this.mState = new ClipState(this.mState, null);
    return this.mState;
  }
  
  protected boolean onLevelChange(int paramInt)
  {
    super.onLevelChange(paramInt);
    invalidateSelf();
    return true;
  }
  
  static final class ClipState
    extends DrawableWrapper.DrawableWrapperState
  {
    int mGravity = 3;
    int mOrientation = 1;
    private int[] mThemeAttrs;
    
    ClipState(ClipState paramClipState, Resources paramResources)
    {
      super(paramResources);
      if (paramClipState != null)
      {
        this.mOrientation = paramClipState.mOrientation;
        this.mGravity = paramClipState.mGravity;
      }
    }
    
    public Drawable newDrawable(Resources paramResources)
    {
      return new ClipDrawable(this, paramResources, null);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/drawable/ClipDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */