package android.graphics.drawable;

import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.os.SystemClock;
import android.util.AttributeSet;
import com.android.internal.R.styleable;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class AnimationDrawable
  extends DrawableContainer
  implements Runnable, Animatable
{
  private boolean mAnimating;
  private AnimationState mAnimationState;
  private int mCurFrame = 0;
  private boolean mMutated;
  private boolean mRunning;
  
  public AnimationDrawable()
  {
    this(null, null);
  }
  
  private AnimationDrawable(AnimationState paramAnimationState, Resources paramResources)
  {
    setConstantState(new AnimationState(paramAnimationState, this, paramResources));
    if (paramAnimationState != null) {
      setFrame(0, true, false);
    }
  }
  
  private void inflateChildElements(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    int i = paramXmlPullParser.getDepth() + 1;
    for (;;)
    {
      int j = paramXmlPullParser.next();
      if (j == 1) {
        break;
      }
      int k = paramXmlPullParser.getDepth();
      if ((k < i) && (j == 3)) {
        break;
      }
      if ((j == 2) && (k <= i) && (paramXmlPullParser.getName().equals("item")))
      {
        Object localObject = obtainAttributes(paramResources, paramTheme, paramAttributeSet, R.styleable.AnimationDrawableItem);
        j = ((TypedArray)localObject).getInt(0, -1);
        if (j < 0) {
          throw new XmlPullParserException(paramXmlPullParser.getPositionDescription() + ": <item> tag requires a 'duration' attribute");
        }
        Drawable localDrawable = ((TypedArray)localObject).getDrawable(1);
        ((TypedArray)localObject).recycle();
        localObject = localDrawable;
        if (localDrawable == null)
        {
          do
          {
            k = paramXmlPullParser.next();
          } while (k == 4);
          if (k != 2) {
            throw new XmlPullParserException(paramXmlPullParser.getPositionDescription() + ": <item> tag requires a 'drawable' attribute or child tag" + " defining a drawable");
          }
          localObject = Drawable.createFromXmlInner(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
        }
        this.mAnimationState.addFrame((Drawable)localObject, j);
        if (localObject != null) {
          ((Drawable)localObject).setCallback(this);
        }
      }
    }
  }
  
  private void nextFrame(boolean paramBoolean)
  {
    int k = this.mCurFrame + 1;
    int m = this.mAnimationState.getChildCount();
    int i;
    int j;
    if ((AnimationState.-get1(this.mAnimationState)) && (k >= m - 1))
    {
      i = 1;
      j = k;
      if (!AnimationState.-get1(this.mAnimationState))
      {
        j = k;
        if (k >= m) {
          j = 0;
        }
      }
      if (i == 0) {
        break label84;
      }
    }
    label84:
    for (boolean bool = false;; bool = true)
    {
      setFrame(j, paramBoolean, bool);
      return;
      i = 0;
      break;
    }
  }
  
  private void setFrame(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramInt >= this.mAnimationState.getChildCount()) {
      return;
    }
    this.mAnimating = paramBoolean2;
    this.mCurFrame = paramInt;
    selectDrawable(paramInt);
    if ((paramBoolean1) || (paramBoolean2)) {
      unscheduleSelf(this);
    }
    if (paramBoolean2)
    {
      this.mCurFrame = paramInt;
      this.mRunning = true;
      scheduleSelf(this, SystemClock.uptimeMillis() + AnimationState.-get0(this.mAnimationState)[paramInt]);
    }
  }
  
  private void updateStateFromTypedArray(TypedArray paramTypedArray)
  {
    this.mAnimationState.mVariablePadding = paramTypedArray.getBoolean(1, this.mAnimationState.mVariablePadding);
    AnimationState.-set0(this.mAnimationState, paramTypedArray.getBoolean(2, AnimationState.-get1(this.mAnimationState)));
  }
  
  public void addFrame(Drawable paramDrawable, int paramInt)
  {
    this.mAnimationState.addFrame(paramDrawable, paramInt);
    if (!this.mRunning) {
      setFrame(0, true, false);
    }
  }
  
  public void clearMutated()
  {
    super.clearMutated();
    this.mMutated = false;
  }
  
  AnimationState cloneConstantState()
  {
    return new AnimationState(this.mAnimationState, this, null);
  }
  
  public int getDuration(int paramInt)
  {
    return AnimationState.-get0(this.mAnimationState)[paramInt];
  }
  
  public Drawable getFrame(int paramInt)
  {
    return this.mAnimationState.getChild(paramInt);
  }
  
  public int getNumberOfFrames()
  {
    return this.mAnimationState.getChildCount();
  }
  
  public void inflate(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    TypedArray localTypedArray = obtainAttributes(paramResources, paramTheme, paramAttributeSet, R.styleable.AnimationDrawable);
    super.inflateWithAttributes(paramResources, paramXmlPullParser, localTypedArray, 0);
    updateStateFromTypedArray(localTypedArray);
    updateDensity(paramResources);
    localTypedArray.recycle();
    inflateChildElements(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
    setFrame(0, true, false);
  }
  
  public boolean isOneShot()
  {
    return AnimationState.-get1(this.mAnimationState);
  }
  
  public boolean isRunning()
  {
    return this.mRunning;
  }
  
  public Drawable mutate()
  {
    if ((!this.mMutated) && (super.mutate() == this))
    {
      AnimationState.-wrap0(this.mAnimationState);
      this.mMutated = true;
    }
    return this;
  }
  
  public void run()
  {
    nextFrame(false);
  }
  
  protected void setConstantState(DrawableContainer.DrawableContainerState paramDrawableContainerState)
  {
    super.setConstantState(paramDrawableContainerState);
    if ((paramDrawableContainerState instanceof AnimationState)) {
      this.mAnimationState = ((AnimationState)paramDrawableContainerState);
    }
  }
  
  public void setOneShot(boolean paramBoolean)
  {
    AnimationState.-set0(this.mAnimationState, paramBoolean);
  }
  
  public boolean setVisible(boolean paramBoolean1, boolean paramBoolean2)
  {
    int j = 0;
    boolean bool = super.setVisible(paramBoolean1, paramBoolean2);
    if (paramBoolean1)
    {
      if ((paramBoolean2) || (bool))
      {
        if ((paramBoolean2) || ((!this.mRunning) && (!AnimationState.-get1(this.mAnimationState)))) {
          break label81;
        }
        if (this.mCurFrame < this.mAnimationState.getChildCount()) {
          break label86;
        }
        i = 1;
        if (i == 0) {
          break label91;
        }
      }
      label81:
      label86:
      label91:
      for (int i = j;; i = this.mCurFrame)
      {
        setFrame(i, true, this.mAnimating);
        return bool;
        i = 1;
        break;
        i = 0;
        break;
      }
    }
    unscheduleSelf(this);
    return bool;
  }
  
  public void start()
  {
    boolean bool2 = true;
    this.mAnimating = true;
    if (!isRunning())
    {
      boolean bool1 = bool2;
      if (this.mAnimationState.getChildCount() <= 1)
      {
        bool1 = bool2;
        if (AnimationState.-get1(this.mAnimationState)) {
          bool1 = false;
        }
      }
      setFrame(0, false, bool1);
    }
  }
  
  public void stop()
  {
    this.mAnimating = false;
    if (isRunning())
    {
      this.mCurFrame = 0;
      unscheduleSelf(this);
    }
  }
  
  public void unscheduleSelf(Runnable paramRunnable)
  {
    this.mRunning = false;
    super.unscheduleSelf(paramRunnable);
  }
  
  private static final class AnimationState
    extends DrawableContainer.DrawableContainerState
  {
    private int[] mDurations;
    private boolean mOneShot = false;
    
    AnimationState(AnimationState paramAnimationState, AnimationDrawable paramAnimationDrawable, Resources paramResources)
    {
      super(paramAnimationDrawable, paramResources);
      if (paramAnimationState != null)
      {
        this.mDurations = paramAnimationState.mDurations;
        this.mOneShot = paramAnimationState.mOneShot;
        return;
      }
      this.mDurations = new int[getCapacity()];
      this.mOneShot = false;
    }
    
    private void mutate()
    {
      this.mDurations = ((int[])this.mDurations.clone());
    }
    
    public void addFrame(Drawable paramDrawable, int paramInt)
    {
      int i = super.addChild(paramDrawable);
      this.mDurations[i] = paramInt;
    }
    
    public void growArray(int paramInt1, int paramInt2)
    {
      super.growArray(paramInt1, paramInt2);
      int[] arrayOfInt = new int[paramInt2];
      System.arraycopy(this.mDurations, 0, arrayOfInt, 0, paramInt1);
      this.mDurations = arrayOfInt;
    }
    
    public Drawable newDrawable()
    {
      return new AnimationDrawable(this, null, null);
    }
    
    public Drawable newDrawable(Resources paramResources)
    {
      return new AnimationDrawable(this, paramResources, null);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/drawable/AnimationDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */