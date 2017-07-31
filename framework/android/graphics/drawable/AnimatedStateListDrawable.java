package android.graphics.drawable;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.LongSparseLongArray;
import android.util.SparseIntArray;
import android.util.StateSet;
import com.android.internal.R.styleable;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class AnimatedStateListDrawable
  extends StateListDrawable
{
  private static final String ELEMENT_ITEM = "item";
  private static final String ELEMENT_TRANSITION = "transition";
  private static final String LOGTAG = AnimatedStateListDrawable.class.getSimpleName();
  private boolean mMutated;
  private AnimatedStateListState mState;
  private Transition mTransition;
  private int mTransitionFromIndex = -1;
  private int mTransitionToIndex = -1;
  
  public AnimatedStateListDrawable()
  {
    this(null, null);
  }
  
  private AnimatedStateListDrawable(AnimatedStateListState paramAnimatedStateListState, Resources paramResources)
  {
    super(null);
    setConstantState(new AnimatedStateListState(paramAnimatedStateListState, this, paramResources));
    onStateChange(getState());
    jumpToCurrentState();
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
      if ((j == 2) && (k <= i)) {
        if (paramXmlPullParser.getName().equals("item")) {
          parseItem(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
        } else if (paramXmlPullParser.getName().equals("transition")) {
          parseTransition(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
        }
      }
    }
  }
  
  private void init()
  {
    onStateChange(getState());
  }
  
  private int parseItem(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    Object localObject = obtainAttributes(paramResources, paramTheme, paramAttributeSet, R.styleable.AnimatedStateListDrawableItem);
    int i = ((TypedArray)localObject).getResourceId(0, 0);
    Drawable localDrawable = ((TypedArray)localObject).getDrawable(1);
    ((TypedArray)localObject).recycle();
    int[] arrayOfInt = extractStateSet(paramAttributeSet);
    localObject = localDrawable;
    if (localDrawable == null)
    {
      int j;
      do
      {
        j = paramXmlPullParser.next();
      } while (j == 4);
      if (j != 2) {
        throw new XmlPullParserException(paramXmlPullParser.getPositionDescription() + ": <item> tag requires a 'drawable' attribute or " + "child tag defining a drawable");
      }
      localObject = Drawable.createFromXmlInner(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
    }
    return this.mState.addStateSet(arrayOfInt, (Drawable)localObject, i);
  }
  
  private int parseTransition(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    Object localObject = obtainAttributes(paramResources, paramTheme, paramAttributeSet, R.styleable.AnimatedStateListDrawableTransition);
    int i = ((TypedArray)localObject).getResourceId(2, 0);
    int j = ((TypedArray)localObject).getResourceId(1, 0);
    boolean bool = ((TypedArray)localObject).getBoolean(3, false);
    Drawable localDrawable = ((TypedArray)localObject).getDrawable(0);
    ((TypedArray)localObject).recycle();
    localObject = localDrawable;
    if (localDrawable == null)
    {
      int k;
      do
      {
        k = paramXmlPullParser.next();
      } while (k == 4);
      if (k != 2) {
        throw new XmlPullParserException(paramXmlPullParser.getPositionDescription() + ": <transition> tag requires a 'drawable' attribute or " + "child tag defining a drawable");
      }
      localObject = Drawable.createFromXmlInner(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
    }
    return this.mState.addTransition(i, j, (Drawable)localObject, bool);
  }
  
  private boolean selectTransition(int paramInt)
  {
    Object localObject = this.mTransition;
    int i;
    if (localObject != null)
    {
      if (paramInt == this.mTransitionToIndex) {
        return true;
      }
      if ((paramInt == this.mTransitionFromIndex) && (((Transition)localObject).canReverse()))
      {
        ((Transition)localObject).reverse();
        this.mTransitionToIndex = this.mTransitionFromIndex;
        this.mTransitionFromIndex = paramInt;
        return true;
      }
      i = this.mTransitionToIndex;
      ((Transition)localObject).stop();
    }
    int j;
    int k;
    for (;;)
    {
      this.mTransition = null;
      this.mTransitionFromIndex = -1;
      this.mTransitionToIndex = -1;
      localObject = this.mState;
      j = ((AnimatedStateListState)localObject).getKeyframeIdAt(i);
      k = ((AnimatedStateListState)localObject).getKeyframeIdAt(paramInt);
      if ((k != 0) && (j != 0)) {
        break;
      }
      return false;
      i = getCurrentIndex();
    }
    int m = ((AnimatedStateListState)localObject).indexOfTransition(j, k);
    if (m < 0) {
      return false;
    }
    boolean bool1 = ((AnimatedStateListState)localObject).transitionHasReversibleFlag(j, k);
    selectDrawable(m);
    Drawable localDrawable = getCurrent();
    boolean bool2;
    if ((localDrawable instanceof AnimationDrawable))
    {
      bool2 = ((AnimatedStateListState)localObject).isTransitionReversed(j, k);
      localObject = new AnimationDrawableTransition((AnimationDrawable)localDrawable, bool2, bool1);
    }
    for (;;)
    {
      ((Transition)localObject).start();
      this.mTransition = ((Transition)localObject);
      this.mTransitionFromIndex = i;
      this.mTransitionToIndex = paramInt;
      return true;
      if ((localDrawable instanceof AnimatedVectorDrawable))
      {
        bool2 = ((AnimatedStateListState)localObject).isTransitionReversed(j, k);
        localObject = new AnimatedVectorDrawableTransition((AnimatedVectorDrawable)localDrawable, bool2, bool1);
      }
      else
      {
        if (!(localDrawable instanceof Animatable)) {
          break;
        }
        localObject = new AnimatableTransition((Animatable)localDrawable);
      }
    }
    return false;
  }
  
  private void updateStateFromTypedArray(TypedArray paramTypedArray)
  {
    AnimatedStateListState localAnimatedStateListState = this.mState;
    localAnimatedStateListState.mChangingConfigurations |= paramTypedArray.getChangingConfigurations();
    localAnimatedStateListState.mAnimThemeAttrs = paramTypedArray.extractThemeAttrs();
    localAnimatedStateListState.setVariablePadding(paramTypedArray.getBoolean(2, localAnimatedStateListState.mVariablePadding));
    localAnimatedStateListState.setConstantSize(paramTypedArray.getBoolean(3, localAnimatedStateListState.mConstantSize));
    localAnimatedStateListState.setEnterFadeDuration(paramTypedArray.getInt(4, localAnimatedStateListState.mEnterFadeDuration));
    localAnimatedStateListState.setExitFadeDuration(paramTypedArray.getInt(5, localAnimatedStateListState.mExitFadeDuration));
    setDither(paramTypedArray.getBoolean(0, localAnimatedStateListState.mDither));
    setAutoMirrored(paramTypedArray.getBoolean(6, localAnimatedStateListState.mAutoMirrored));
  }
  
  public void addState(int[] paramArrayOfInt, Drawable paramDrawable, int paramInt)
  {
    if (paramDrawable == null) {
      throw new IllegalArgumentException("Drawable must not be null");
    }
    this.mState.addStateSet(paramArrayOfInt, paramDrawable, paramInt);
    onStateChange(getState());
  }
  
  public <T extends Drawable,  extends Animatable> void addTransition(int paramInt1, int paramInt2, T paramT, boolean paramBoolean)
  {
    if (paramT == null) {
      throw new IllegalArgumentException("Transition drawable must not be null");
    }
    this.mState.addTransition(paramInt1, paramInt2, paramT, paramBoolean);
  }
  
  public void applyTheme(Resources.Theme paramTheme)
  {
    super.applyTheme(paramTheme);
    AnimatedStateListState localAnimatedStateListState = this.mState;
    if ((localAnimatedStateListState == null) || (localAnimatedStateListState.mAnimThemeAttrs == null)) {
      return;
    }
    paramTheme = paramTheme.resolveAttributes(localAnimatedStateListState.mAnimThemeAttrs, R.styleable.AnimatedRotateDrawable);
    updateStateFromTypedArray(paramTheme);
    paramTheme.recycle();
    init();
  }
  
  public void clearMutated()
  {
    super.clearMutated();
    this.mMutated = false;
  }
  
  AnimatedStateListState cloneConstantState()
  {
    return new AnimatedStateListState(this.mState, this, null);
  }
  
  public void inflate(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    TypedArray localTypedArray = obtainAttributes(paramResources, paramTheme, paramAttributeSet, R.styleable.AnimatedStateListDrawable);
    super.inflateWithAttributes(paramResources, paramXmlPullParser, localTypedArray, 1);
    updateStateFromTypedArray(localTypedArray);
    updateDensity(paramResources);
    localTypedArray.recycle();
    inflateChildElements(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
    init();
  }
  
  public boolean isStateful()
  {
    return true;
  }
  
  public void jumpToCurrentState()
  {
    super.jumpToCurrentState();
    if (this.mTransition != null)
    {
      this.mTransition.stop();
      this.mTransition = null;
      selectDrawable(this.mTransitionToIndex);
      this.mTransitionToIndex = -1;
      this.mTransitionFromIndex = -1;
    }
  }
  
  public Drawable mutate()
  {
    if ((!this.mMutated) && (super.mutate() == this))
    {
      this.mState.mutate();
      this.mMutated = true;
    }
    return this;
  }
  
  protected boolean onStateChange(int[] paramArrayOfInt)
  {
    int i = this.mState.indexOfKeyframe(paramArrayOfInt);
    boolean bool1;
    if (i != getCurrentIndex()) {
      if (!selectTransition(i)) {
        bool1 = selectDrawable(i);
      }
    }
    for (;;)
    {
      Drawable localDrawable = getCurrent();
      boolean bool2 = bool1;
      if (localDrawable != null) {
        bool2 = bool1 | localDrawable.setState(paramArrayOfInt);
      }
      return bool2;
      bool1 = true;
      continue;
      bool1 = false;
    }
  }
  
  protected void setConstantState(DrawableContainer.DrawableContainerState paramDrawableContainerState)
  {
    super.setConstantState(paramDrawableContainerState);
    if ((paramDrawableContainerState instanceof AnimatedStateListState)) {
      this.mState = ((AnimatedStateListState)paramDrawableContainerState);
    }
  }
  
  public boolean setVisible(boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool = super.setVisible(paramBoolean1, paramBoolean2);
    if ((this.mTransition != null) && ((bool) || (paramBoolean2)))
    {
      if (paramBoolean1) {
        this.mTransition.start();
      }
    }
    else {
      return bool;
    }
    jumpToCurrentState();
    return bool;
  }
  
  private static class AnimatableTransition
    extends AnimatedStateListDrawable.Transition
  {
    private final Animatable mA;
    
    public AnimatableTransition(Animatable paramAnimatable)
    {
      super();
      this.mA = paramAnimatable;
    }
    
    public void start()
    {
      this.mA.start();
    }
    
    public void stop()
    {
      this.mA.stop();
    }
  }
  
  static class AnimatedStateListState
    extends StateListDrawable.StateListState
  {
    private static final long REVERSED_BIT = 4294967296L;
    private static final long REVERSIBLE_FLAG_BIT = 8589934592L;
    int[] mAnimThemeAttrs;
    SparseIntArray mStateIds;
    LongSparseLongArray mTransitions;
    
    AnimatedStateListState(AnimatedStateListState paramAnimatedStateListState, AnimatedStateListDrawable paramAnimatedStateListDrawable, Resources paramResources)
    {
      super(paramAnimatedStateListDrawable, paramResources);
      if (paramAnimatedStateListState != null)
      {
        this.mAnimThemeAttrs = paramAnimatedStateListState.mAnimThemeAttrs;
        this.mTransitions = paramAnimatedStateListState.mTransitions;
        this.mStateIds = paramAnimatedStateListState.mStateIds;
        return;
      }
      this.mTransitions = new LongSparseLongArray();
      this.mStateIds = new SparseIntArray();
    }
    
    private static long generateTransitionKey(int paramInt1, int paramInt2)
    {
      return paramInt1 << 32 | paramInt2;
    }
    
    int addStateSet(int[] paramArrayOfInt, Drawable paramDrawable, int paramInt)
    {
      int i = super.addStateSet(paramArrayOfInt, paramDrawable);
      this.mStateIds.put(i, paramInt);
      return i;
    }
    
    int addTransition(int paramInt1, int paramInt2, Drawable paramDrawable, boolean paramBoolean)
    {
      int i = super.addChild(paramDrawable);
      long l2 = generateTransitionKey(paramInt1, paramInt2);
      long l1 = 0L;
      if (paramBoolean) {
        l1 = 8589934592L;
      }
      this.mTransitions.append(l2, i | l1);
      if (paramBoolean)
      {
        l2 = generateTransitionKey(paramInt2, paramInt1);
        this.mTransitions.append(l2, i | 0x100000000 | l1);
      }
      return i;
    }
    
    public boolean canApplyTheme()
    {
      if (this.mAnimThemeAttrs == null) {
        return super.canApplyTheme();
      }
      return true;
    }
    
    int getKeyframeIdAt(int paramInt)
    {
      if (paramInt < 0) {
        return 0;
      }
      return this.mStateIds.get(paramInt, 0);
    }
    
    int indexOfKeyframe(int[] paramArrayOfInt)
    {
      int i = super.indexOfStateSet(paramArrayOfInt);
      if (i >= 0) {
        return i;
      }
      return super.indexOfStateSet(StateSet.WILD_CARD);
    }
    
    int indexOfTransition(int paramInt1, int paramInt2)
    {
      long l = generateTransitionKey(paramInt1, paramInt2);
      return (int)this.mTransitions.get(l, -1L);
    }
    
    boolean isTransitionReversed(int paramInt1, int paramInt2)
    {
      long l = generateTransitionKey(paramInt1, paramInt2);
      return (this.mTransitions.get(l, -1L) & 0x100000000) != 0L;
    }
    
    void mutate()
    {
      this.mTransitions = this.mTransitions.clone();
      this.mStateIds = this.mStateIds.clone();
    }
    
    public Drawable newDrawable()
    {
      return new AnimatedStateListDrawable(this, null, null);
    }
    
    public Drawable newDrawable(Resources paramResources)
    {
      return new AnimatedStateListDrawable(this, paramResources, null);
    }
    
    boolean transitionHasReversibleFlag(int paramInt1, int paramInt2)
    {
      long l = generateTransitionKey(paramInt1, paramInt2);
      return (this.mTransitions.get(l, -1L) & 0x200000000) != 0L;
    }
  }
  
  private static class AnimatedVectorDrawableTransition
    extends AnimatedStateListDrawable.Transition
  {
    private final AnimatedVectorDrawable mAvd;
    private final boolean mHasReversibleFlag;
    private final boolean mReversed;
    
    public AnimatedVectorDrawableTransition(AnimatedVectorDrawable paramAnimatedVectorDrawable, boolean paramBoolean1, boolean paramBoolean2)
    {
      super();
      this.mAvd = paramAnimatedVectorDrawable;
      this.mReversed = paramBoolean1;
      this.mHasReversibleFlag = paramBoolean2;
    }
    
    public boolean canReverse()
    {
      if (this.mAvd.canReverse()) {
        return this.mHasReversibleFlag;
      }
      return false;
    }
    
    public void reverse()
    {
      if (canReverse())
      {
        this.mAvd.reverse();
        return;
      }
      Log.w(AnimatedStateListDrawable.-get0(), "Can't reverse, either the reversible is set to false, or the AnimatedVectorDrawable can't reverse");
    }
    
    public void start()
    {
      if (this.mReversed)
      {
        reverse();
        return;
      }
      this.mAvd.start();
    }
    
    public void stop()
    {
      this.mAvd.stop();
    }
  }
  
  private static class AnimationDrawableTransition
    extends AnimatedStateListDrawable.Transition
  {
    private final ObjectAnimator mAnim;
    private final boolean mHasReversibleFlag;
    
    public AnimationDrawableTransition(AnimationDrawable paramAnimationDrawable, boolean paramBoolean1, boolean paramBoolean2)
    {
      super();
      int j = paramAnimationDrawable.getNumberOfFrames();
      int i;
      if (paramBoolean1)
      {
        i = j - 1;
        if (!paramBoolean1) {
          break label98;
        }
        j = 0;
      }
      for (;;)
      {
        AnimatedStateListDrawable.FrameInterpolator localFrameInterpolator = new AnimatedStateListDrawable.FrameInterpolator(paramAnimationDrawable, paramBoolean1);
        paramAnimationDrawable = ObjectAnimator.ofInt(paramAnimationDrawable, "currentIndex", new int[] { i, j });
        paramAnimationDrawable.setAutoCancel(true);
        paramAnimationDrawable.setDuration(localFrameInterpolator.getTotalDuration());
        paramAnimationDrawable.setInterpolator(localFrameInterpolator);
        this.mHasReversibleFlag = paramBoolean2;
        this.mAnim = paramAnimationDrawable;
        return;
        i = 0;
        break;
        label98:
        j -= 1;
      }
    }
    
    public boolean canReverse()
    {
      return this.mHasReversibleFlag;
    }
    
    public void reverse()
    {
      this.mAnim.reverse();
    }
    
    public void start()
    {
      this.mAnim.start();
    }
    
    public void stop()
    {
      this.mAnim.cancel();
    }
  }
  
  private static class FrameInterpolator
    implements TimeInterpolator
  {
    private int[] mFrameTimes;
    private int mFrames;
    private int mTotalDuration;
    
    public FrameInterpolator(AnimationDrawable paramAnimationDrawable, boolean paramBoolean)
    {
      updateFrames(paramAnimationDrawable, paramBoolean);
    }
    
    public float getInterpolation(float paramFloat)
    {
      int j = (int)(this.mTotalDuration * paramFloat + 0.5F);
      int k = this.mFrames;
      int[] arrayOfInt = this.mFrameTimes;
      int i = 0;
      while ((i < k) && (j >= arrayOfInt[i]))
      {
        j -= arrayOfInt[i];
        i += 1;
      }
      if (i < k) {}
      for (paramFloat = j / this.mTotalDuration;; paramFloat = 0.0F) {
        return i / k + paramFloat;
      }
    }
    
    public int getTotalDuration()
    {
      return this.mTotalDuration;
    }
    
    public int updateFrames(AnimationDrawable paramAnimationDrawable, boolean paramBoolean)
    {
      int m = paramAnimationDrawable.getNumberOfFrames();
      this.mFrames = m;
      if ((this.mFrameTimes == null) || (this.mFrameTimes.length < m)) {
        this.mFrameTimes = new int[m];
      }
      int[] arrayOfInt = this.mFrameTimes;
      int j = 0;
      int i = 0;
      if (i < m)
      {
        if (paramBoolean) {}
        for (int k = m - i - 1;; k = i)
        {
          k = paramAnimationDrawable.getDuration(k);
          arrayOfInt[i] = k;
          j += k;
          i += 1;
          break;
        }
      }
      this.mTotalDuration = j;
      return j;
    }
  }
  
  private static abstract class Transition
  {
    public boolean canReverse()
    {
      return false;
    }
    
    public void reverse() {}
    
    public abstract void start();
    
    public abstract void stop();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/drawable/AnimatedStateListDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */