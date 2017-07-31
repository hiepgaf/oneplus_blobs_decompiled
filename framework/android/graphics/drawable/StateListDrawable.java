package android.graphics.drawable;

import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.StateSet;
import com.android.internal.R.styleable;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class StateListDrawable
  extends DrawableContainer
{
  private static final boolean DEBUG = false;
  private static final String TAG = "StateListDrawable";
  private boolean mMutated;
  private StateListState mStateListState;
  
  public StateListDrawable()
  {
    this(null, null);
  }
  
  StateListDrawable(StateListState paramStateListState)
  {
    if (paramStateListState != null) {
      setConstantState(paramStateListState);
    }
  }
  
  private StateListDrawable(StateListState paramStateListState, Resources paramResources)
  {
    setConstantState(new StateListState(paramStateListState, this, paramResources));
    onStateChange(getState());
  }
  
  private void inflateChildElements(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    StateListState localStateListState = this.mStateListState;
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
        Object localObject = obtainAttributes(paramResources, paramTheme, paramAttributeSet, R.styleable.StateListDrawableItem);
        Drawable localDrawable = ((TypedArray)localObject).getDrawable(0);
        ((TypedArray)localObject).recycle();
        int[] arrayOfInt = extractStateSet(paramAttributeSet);
        localObject = localDrawable;
        if (localDrawable == null)
        {
          do
          {
            j = paramXmlPullParser.next();
          } while (j == 4);
          if (j != 2) {
            throw new XmlPullParserException(paramXmlPullParser.getPositionDescription() + ": <item> tag requires a 'drawable' attribute or " + "child tag defining a drawable");
          }
          localObject = Drawable.createFromXmlInner(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
        }
        localStateListState.addStateSet(arrayOfInt, (Drawable)localObject);
      }
    }
  }
  
  private void updateStateFromTypedArray(TypedArray paramTypedArray)
  {
    StateListState localStateListState = this.mStateListState;
    localStateListState.mChangingConfigurations |= paramTypedArray.getChangingConfigurations();
    localStateListState.mThemeAttrs = paramTypedArray.extractThemeAttrs();
    localStateListState.mVariablePadding = paramTypedArray.getBoolean(2, localStateListState.mVariablePadding);
    localStateListState.mConstantSize = paramTypedArray.getBoolean(3, localStateListState.mConstantSize);
    localStateListState.mEnterFadeDuration = paramTypedArray.getInt(4, localStateListState.mEnterFadeDuration);
    localStateListState.mExitFadeDuration = paramTypedArray.getInt(5, localStateListState.mExitFadeDuration);
    localStateListState.mDither = paramTypedArray.getBoolean(0, localStateListState.mDither);
    localStateListState.mAutoMirrored = paramTypedArray.getBoolean(6, localStateListState.mAutoMirrored);
  }
  
  public void addState(int[] paramArrayOfInt, Drawable paramDrawable)
  {
    if (paramDrawable != null)
    {
      this.mStateListState.addStateSet(paramArrayOfInt, paramDrawable);
      onStateChange(getState());
    }
  }
  
  public void applyTheme(Resources.Theme paramTheme)
  {
    super.applyTheme(paramTheme);
    onStateChange(getState());
  }
  
  public void clearMutated()
  {
    super.clearMutated();
    this.mMutated = false;
  }
  
  StateListState cloneConstantState()
  {
    return new StateListState(this.mStateListState, this, null);
  }
  
  int[] extractStateSet(AttributeSet paramAttributeSet)
  {
    int n = paramAttributeSet.getAttributeCount();
    int[] arrayOfInt = new int[n];
    int j = 0;
    int i = 0;
    if (j < n)
    {
      int k = paramAttributeSet.getAttributeNameResource(j);
      int m;
      switch (k)
      {
      default: 
        m = i + 1;
        if (!paramAttributeSet.getAttributeBooleanValue(j, false)) {
          break;
        }
      }
      for (;;)
      {
        arrayOfInt[i] = k;
        i = m;
        for (;;)
        {
          j += 1;
          break;
        }
        k = -k;
      }
    }
    return StateSet.trimStateSet(arrayOfInt, i);
  }
  
  public int getStateCount()
  {
    return this.mStateListState.getChildCount();
  }
  
  public Drawable getStateDrawable(int paramInt)
  {
    return this.mStateListState.getChild(paramInt);
  }
  
  public int getStateDrawableIndex(int[] paramArrayOfInt)
  {
    return this.mStateListState.indexOfStateSet(paramArrayOfInt);
  }
  
  StateListState getStateListState()
  {
    return this.mStateListState;
  }
  
  public int[] getStateSet(int paramInt)
  {
    return this.mStateListState.mStateSets[paramInt];
  }
  
  public void inflate(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    TypedArray localTypedArray = obtainAttributes(paramResources, paramTheme, paramAttributeSet, R.styleable.StateListDrawable);
    super.inflateWithAttributes(paramResources, paramXmlPullParser, localTypedArray, 1);
    updateStateFromTypedArray(localTypedArray);
    updateDensity(paramResources);
    localTypedArray.recycle();
    inflateChildElements(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
    onStateChange(getState());
  }
  
  public boolean isStateful()
  {
    return true;
  }
  
  public Drawable mutate()
  {
    if ((!this.mMutated) && (super.mutate() == this))
    {
      this.mStateListState.mutate();
      this.mMutated = true;
    }
    return this;
  }
  
  protected boolean onStateChange(int[] paramArrayOfInt)
  {
    boolean bool = super.onStateChange(paramArrayOfInt);
    int j = this.mStateListState.indexOfStateSet(paramArrayOfInt);
    int i = j;
    if (j < 0) {
      i = this.mStateListState.indexOfStateSet(StateSet.WILD_CARD);
    }
    if (!selectDrawable(i)) {
      return bool;
    }
    return true;
  }
  
  protected void setConstantState(DrawableContainer.DrawableContainerState paramDrawableContainerState)
  {
    super.setConstantState(paramDrawableContainerState);
    if ((paramDrawableContainerState instanceof StateListState)) {
      this.mStateListState = ((StateListState)paramDrawableContainerState);
    }
  }
  
  static class StateListState
    extends DrawableContainer.DrawableContainerState
  {
    int[][] mStateSets;
    int[] mThemeAttrs;
    
    StateListState(StateListState paramStateListState, StateListDrawable paramStateListDrawable, Resources paramResources)
    {
      super(paramStateListDrawable, paramResources);
      if (paramStateListState != null)
      {
        this.mThemeAttrs = paramStateListState.mThemeAttrs;
        this.mStateSets = paramStateListState.mStateSets;
        return;
      }
      this.mThemeAttrs = null;
      this.mStateSets = new int[getCapacity()][];
    }
    
    int addStateSet(int[] paramArrayOfInt, Drawable paramDrawable)
    {
      int i = addChild(paramDrawable);
      this.mStateSets[i] = paramArrayOfInt;
      return i;
    }
    
    public boolean canApplyTheme()
    {
      if (this.mThemeAttrs == null) {
        return super.canApplyTheme();
      }
      return true;
    }
    
    public void growArray(int paramInt1, int paramInt2)
    {
      super.growArray(paramInt1, paramInt2);
      int[][] arrayOfInt = new int[paramInt2][];
      System.arraycopy(this.mStateSets, 0, arrayOfInt, 0, paramInt1);
      this.mStateSets = arrayOfInt;
    }
    
    int indexOfStateSet(int[] paramArrayOfInt)
    {
      int[][] arrayOfInt = this.mStateSets;
      int j = getChildCount();
      int i = 0;
      while (i < j)
      {
        if (StateSet.stateSetMatches(arrayOfInt[i], paramArrayOfInt)) {
          return i;
        }
        i += 1;
      }
      return -1;
    }
    
    void mutate()
    {
      int[][] arrayOfInt1;
      int i;
      if (this.mThemeAttrs != null)
      {
        arrayOfInt = (int[])this.mThemeAttrs.clone();
        this.mThemeAttrs = arrayOfInt;
        arrayOfInt1 = new int[this.mStateSets.length][];
        i = this.mStateSets.length - 1;
        label40:
        if (i < 0) {
          break label87;
        }
        if (this.mStateSets[i] == null) {
          break label82;
        }
      }
      label82:
      for (int[] arrayOfInt = (int[])this.mStateSets[i].clone();; arrayOfInt = null)
      {
        arrayOfInt1[i] = arrayOfInt;
        i -= 1;
        break label40;
        arrayOfInt = null;
        break;
      }
      label87:
      this.mStateSets = arrayOfInt1;
    }
    
    public Drawable newDrawable()
    {
      return new StateListDrawable(this, null, null);
    }
    
    public Drawable newDrawable(Resources paramResources)
    {
      return new StateListDrawable(this, paramResources, null);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/drawable/StateListDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */