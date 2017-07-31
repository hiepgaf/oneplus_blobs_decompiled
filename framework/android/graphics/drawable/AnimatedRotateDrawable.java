package android.graphics.drawable;

import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.TypedValue;
import com.android.internal.R.styleable;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class AnimatedRotateDrawable
  extends DrawableWrapper
  implements Animatable
{
  private float mCurrentDegrees;
  private float mIncrement;
  private final Runnable mNextFrame = new Runnable()
  {
    public void run()
    {
      AnimatedRotateDrawable localAnimatedRotateDrawable = AnimatedRotateDrawable.this;
      AnimatedRotateDrawable.-set0(localAnimatedRotateDrawable, AnimatedRotateDrawable.-get0(localAnimatedRotateDrawable) + AnimatedRotateDrawable.-get1(AnimatedRotateDrawable.this));
      if (AnimatedRotateDrawable.-get0(AnimatedRotateDrawable.this) > 360.0F - AnimatedRotateDrawable.-get1(AnimatedRotateDrawable.this)) {
        AnimatedRotateDrawable.-set0(AnimatedRotateDrawable.this, 0.0F);
      }
      AnimatedRotateDrawable.this.invalidateSelf();
      AnimatedRotateDrawable.-wrap0(AnimatedRotateDrawable.this);
    }
  };
  private boolean mRunning;
  private AnimatedRotateState mState;
  
  public AnimatedRotateDrawable()
  {
    this(new AnimatedRotateState(null, null), null);
  }
  
  private AnimatedRotateDrawable(AnimatedRotateState paramAnimatedRotateState, Resources paramResources)
  {
    super(paramAnimatedRotateState, paramResources);
    this.mState = paramAnimatedRotateState;
    updateLocalState();
  }
  
  private void nextFrame()
  {
    unscheduleSelf(this.mNextFrame);
    scheduleSelf(this.mNextFrame, SystemClock.uptimeMillis() + this.mState.mFrameDuration);
  }
  
  private void updateLocalState()
  {
    this.mIncrement = (360.0F / this.mState.mFramesCount);
    Drawable localDrawable = getDrawable();
    if (localDrawable != null)
    {
      localDrawable.setFilterBitmap(true);
      if ((localDrawable instanceof BitmapDrawable)) {
        ((BitmapDrawable)localDrawable).setAntiAlias(true);
      }
    }
  }
  
  private void updateStateFromTypedArray(TypedArray paramTypedArray)
  {
    boolean bool2 = true;
    AnimatedRotateState localAnimatedRotateState = this.mState;
    if (localAnimatedRotateState == null) {
      return;
    }
    localAnimatedRotateState.mChangingConfigurations |= paramTypedArray.getChangingConfigurations();
    AnimatedRotateState.-set0(localAnimatedRotateState, paramTypedArray.extractThemeAttrs());
    TypedValue localTypedValue;
    boolean bool1;
    if (paramTypedArray.hasValue(2))
    {
      localTypedValue = paramTypedArray.peekValue(2);
      if (localTypedValue.type == 6)
      {
        bool1 = true;
        localAnimatedRotateState.mPivotXRel = bool1;
        if (!localAnimatedRotateState.mPivotXRel) {
          break label185;
        }
        f = localTypedValue.getFraction(1.0F, 1.0F);
        label89:
        localAnimatedRotateState.mPivotX = f;
      }
    }
    else if (paramTypedArray.hasValue(3))
    {
      localTypedValue = paramTypedArray.peekValue(3);
      if (localTypedValue.type != 6) {
        break label194;
      }
      bool1 = bool2;
      label123:
      localAnimatedRotateState.mPivotYRel = bool1;
      if (!localAnimatedRotateState.mPivotYRel) {
        break label199;
      }
    }
    label185:
    label194:
    label199:
    for (float f = localTypedValue.getFraction(1.0F, 1.0F);; f = localTypedValue.getFloat())
    {
      localAnimatedRotateState.mPivotY = f;
      setFramesCount(paramTypedArray.getInt(5, localAnimatedRotateState.mFramesCount));
      setFramesDuration(paramTypedArray.getInt(4, localAnimatedRotateState.mFrameDuration));
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
    if ((getDrawable() == null) && ((AnimatedRotateState.-get0(this.mState) == null) || (AnimatedRotateState.-get0(this.mState)[1] == 0))) {
      throw new XmlPullParserException(paramTypedArray.getPositionDescription() + ": <animated-rotate> tag requires a 'drawable' attribute or " + "child tag defining a drawable");
    }
  }
  
  public void applyTheme(Resources.Theme paramTheme)
  {
    super.applyTheme(paramTheme);
    AnimatedRotateState localAnimatedRotateState = this.mState;
    if (localAnimatedRotateState == null) {
      return;
    }
    if (AnimatedRotateState.-get0(localAnimatedRotateState) != null) {
      paramTheme = paramTheme.resolveAttributes(AnimatedRotateState.-get0(localAnimatedRotateState), R.styleable.AnimatedRotateDrawable);
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
    Rect localRect = localDrawable.getBounds();
    int i = localRect.right;
    int j = localRect.left;
    int k = localRect.bottom;
    int m = localRect.top;
    AnimatedRotateState localAnimatedRotateState = this.mState;
    float f1;
    if (localAnimatedRotateState.mPivotXRel)
    {
      f1 = (i - j) * localAnimatedRotateState.mPivotX;
      if (!localAnimatedRotateState.mPivotYRel) {
        break label141;
      }
    }
    label141:
    for (float f2 = (k - m) * localAnimatedRotateState.mPivotY;; f2 = localAnimatedRotateState.mPivotY)
    {
      i = paramCanvas.save();
      paramCanvas.rotate(this.mCurrentDegrees, localRect.left + f1, localRect.top + f2);
      localDrawable.draw(paramCanvas);
      paramCanvas.restoreToCount(i);
      return;
      f1 = localAnimatedRotateState.mPivotX;
      break;
    }
  }
  
  public void inflate(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    TypedArray localTypedArray = obtainAttributes(paramResources, paramTheme, paramAttributeSet, R.styleable.AnimatedRotateDrawable);
    super.inflate(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
    updateStateFromTypedArray(localTypedArray);
    verifyRequiredAttributes(localTypedArray);
    localTypedArray.recycle();
    updateLocalState();
  }
  
  public boolean isRunning()
  {
    return this.mRunning;
  }
  
  DrawableWrapper.DrawableWrapperState mutateConstantState()
  {
    this.mState = new AnimatedRotateState(this.mState, null);
    return this.mState;
  }
  
  public void setFramesCount(int paramInt)
  {
    this.mState.mFramesCount = paramInt;
    this.mIncrement = (360.0F / this.mState.mFramesCount);
  }
  
  public void setFramesDuration(int paramInt)
  {
    this.mState.mFrameDuration = paramInt;
  }
  
  public boolean setVisible(boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool = super.setVisible(paramBoolean1, paramBoolean2);
    if (paramBoolean1)
    {
      if ((bool) || (paramBoolean2))
      {
        this.mCurrentDegrees = 0.0F;
        nextFrame();
      }
      return bool;
    }
    unscheduleSelf(this.mNextFrame);
    return bool;
  }
  
  public void start()
  {
    if (!this.mRunning)
    {
      this.mRunning = true;
      nextFrame();
    }
  }
  
  public void stop()
  {
    this.mRunning = false;
    unscheduleSelf(this.mNextFrame);
  }
  
  static final class AnimatedRotateState
    extends DrawableWrapper.DrawableWrapperState
  {
    int mFrameDuration = 150;
    int mFramesCount = 12;
    float mPivotX = 0.0F;
    boolean mPivotXRel = false;
    float mPivotY = 0.0F;
    boolean mPivotYRel = false;
    private int[] mThemeAttrs;
    
    public AnimatedRotateState(AnimatedRotateState paramAnimatedRotateState, Resources paramResources)
    {
      super(paramResources);
      if (paramAnimatedRotateState != null)
      {
        this.mPivotXRel = paramAnimatedRotateState.mPivotXRel;
        this.mPivotX = paramAnimatedRotateState.mPivotX;
        this.mPivotYRel = paramAnimatedRotateState.mPivotYRel;
        this.mPivotY = paramAnimatedRotateState.mPivotY;
        this.mFramesCount = paramAnimatedRotateState.mFramesCount;
        this.mFrameDuration = paramAnimatedRotateState.mFrameDuration;
      }
    }
    
    public Drawable newDrawable(Resources paramResources)
    {
      return new AnimatedRotateDrawable(this, paramResources, null);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/drawable/AnimatedRotateDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */