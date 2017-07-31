package android.animation;

import android.os.Looper;
import android.os.Trace;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ValueAnimator
  extends Animator
  implements AnimationHandler.AnimationFrameCallback
{
  private static final boolean DEBUG = false;
  public static final int INFINITE = -1;
  public static final int RESTART = 1;
  public static final int REVERSE = 2;
  private static final String TAG = "ValueAnimator";
  private static final TimeInterpolator sDefaultInterpolator = new AccelerateDecelerateInterpolator();
  private static float sDurationScale = 1.0F;
  private boolean mAnimationEndRequested = false;
  private float mCurrentFraction = 0.0F;
  private long mDuration = 300L;
  boolean mInitialized = false;
  private TimeInterpolator mInterpolator = sDefaultInterpolator;
  private long mLastFrameTime = 0L;
  private float mOverallFraction = 0.0F;
  private long mPauseTime;
  private int mRepeatCount = 0;
  private int mRepeatMode = 1;
  private boolean mResumed = false;
  private boolean mReversing;
  private boolean mRunning = false;
  float mSeekFraction = -1.0F;
  private long mStartDelay = 0L;
  private boolean mStartListenersCalled = false;
  long mStartTime;
  boolean mStartTimeCommitted;
  private boolean mStarted = false;
  ArrayList<AnimatorUpdateListener> mUpdateListeners = null;
  PropertyValuesHolder[] mValues;
  HashMap<String, PropertyValuesHolder> mValuesMap;
  
  private float clampFraction(float paramFloat)
  {
    float f;
    if (paramFloat < 0.0F) {
      f = 0.0F;
    }
    do
    {
      return f;
      f = paramFloat;
    } while (this.mRepeatCount == -1);
    return Math.min(paramFloat, this.mRepeatCount + 1);
  }
  
  private void endAnimation()
  {
    if (this.mAnimationEndRequested) {
      return;
    }
    AnimationHandler.getInstance().removeCallback(this);
    this.mAnimationEndRequested = true;
    this.mPaused = false;
    if (((this.mStarted) || (this.mRunning)) && (this.mListeners != null))
    {
      if (!this.mRunning) {
        notifyStartListeners();
      }
      ArrayList localArrayList = (ArrayList)this.mListeners.clone();
      int j = localArrayList.size();
      int i = 0;
      while (i < j)
      {
        ((Animator.AnimatorListener)localArrayList.get(i)).onAnimationEnd(this);
        i += 1;
      }
    }
    this.mRunning = false;
    this.mStarted = false;
    this.mStartListenersCalled = false;
    this.mReversing = false;
    this.mLastFrameTime = 0L;
    if (Trace.isTagEnabled(8L)) {
      Trace.asyncTraceEnd(8L, getNameForTrace(), System.identityHashCode(this));
    }
  }
  
  public static int getCurrentAnimationsCount()
  {
    return AnimationHandler.getAnimationCount();
  }
  
  private int getCurrentIteration(float paramFloat)
  {
    paramFloat = clampFraction(paramFloat);
    double d2 = Math.floor(paramFloat);
    double d1 = d2;
    if (paramFloat == d2)
    {
      d1 = d2;
      if (paramFloat > 0.0F) {
        d1 = d2 - 1.0D;
      }
    }
    return (int)d1;
  }
  
  private float getCurrentIterationFraction(float paramFloat)
  {
    paramFloat = clampFraction(paramFloat);
    int i = getCurrentIteration(paramFloat);
    float f = paramFloat - i;
    paramFloat = f;
    if (shouldPlayBackward(i)) {
      paramFloat = 1.0F - f;
    }
    return paramFloat;
  }
  
  public static float getDurationScale()
  {
    return sDurationScale;
  }
  
  public static long getFrameDelay()
  {
    AnimationHandler.getInstance();
    return AnimationHandler.getFrameDelay();
  }
  
  private long getScaledDuration()
  {
    return ((float)this.mDuration * sDurationScale);
  }
  
  private boolean isPulsingInternal()
  {
    return this.mLastFrameTime > 0L;
  }
  
  private void notifyStartListeners()
  {
    if ((this.mListeners == null) || (this.mStartListenersCalled)) {}
    for (;;)
    {
      this.mStartListenersCalled = true;
      return;
      ArrayList localArrayList = (ArrayList)this.mListeners.clone();
      int j = localArrayList.size();
      int i = 0;
      while (i < j)
      {
        ((Animator.AnimatorListener)localArrayList.get(i)).onAnimationStart(this);
        i += 1;
      }
    }
  }
  
  public static ValueAnimator ofArgb(int... paramVarArgs)
  {
    ValueAnimator localValueAnimator = new ValueAnimator();
    localValueAnimator.setIntValues(paramVarArgs);
    localValueAnimator.setEvaluator(ArgbEvaluator.getInstance());
    return localValueAnimator;
  }
  
  public static ValueAnimator ofFloat(float... paramVarArgs)
  {
    ValueAnimator localValueAnimator = new ValueAnimator();
    localValueAnimator.setFloatValues(paramVarArgs);
    return localValueAnimator;
  }
  
  public static ValueAnimator ofInt(int... paramVarArgs)
  {
    ValueAnimator localValueAnimator = new ValueAnimator();
    localValueAnimator.setIntValues(paramVarArgs);
    return localValueAnimator;
  }
  
  public static ValueAnimator ofObject(TypeEvaluator paramTypeEvaluator, Object... paramVarArgs)
  {
    ValueAnimator localValueAnimator = new ValueAnimator();
    localValueAnimator.setObjectValues(paramVarArgs);
    localValueAnimator.setEvaluator(paramTypeEvaluator);
    return localValueAnimator;
  }
  
  public static ValueAnimator ofPropertyValuesHolder(PropertyValuesHolder... paramVarArgs)
  {
    ValueAnimator localValueAnimator = new ValueAnimator();
    localValueAnimator.setValues(paramVarArgs);
    return localValueAnimator;
  }
  
  public static void setDurationScale(float paramFloat)
  {
    sDurationScale = paramFloat;
  }
  
  public static void setFrameDelay(long paramLong)
  {
    AnimationHandler.getInstance();
    AnimationHandler.setFrameDelay(paramLong);
  }
  
  private boolean shouldPlayBackward(int paramInt)
  {
    if ((paramInt > 0) && (this.mRepeatMode == 2) && ((paramInt < this.mRepeatCount + 1) || (this.mRepeatCount == -1)))
    {
      if (this.mReversing) {
        return paramInt % 2 == 0;
      }
      return paramInt % 2 != 0;
    }
    return this.mReversing;
  }
  
  private void start(boolean paramBoolean)
  {
    if (Looper.myLooper() == null) {
      throw new AndroidRuntimeException("Animators may only be run on Looper threads");
    }
    this.mReversing = paramBoolean;
    if ((paramBoolean) && (this.mSeekFraction != -1.0F) && (this.mSeekFraction != 0.0F)) {
      if (this.mRepeatCount != -1) {
        break label154;
      }
    }
    label154:
    for (this.mSeekFraction = (1.0F - (float)(this.mSeekFraction - Math.floor(this.mSeekFraction)));; this.mSeekFraction = (this.mRepeatCount + 1 - this.mSeekFraction))
    {
      this.mStarted = true;
      this.mPaused = false;
      this.mRunning = false;
      this.mAnimationEndRequested = false;
      this.mLastFrameTime = 0L;
      AnimationHandler.getInstance().addAnimationFrameCallback(this, ((float)this.mStartDelay * sDurationScale));
      if ((this.mStartDelay == 0L) || (this.mSeekFraction >= 0.0F))
      {
        startAnimation();
        if (this.mSeekFraction != -1.0F) {
          break;
        }
        setCurrentPlayTime(0L);
      }
      return;
    }
    setCurrentFraction(this.mSeekFraction);
  }
  
  private void startAnimation()
  {
    if (Trace.isTagEnabled(8L)) {
      Trace.asyncTraceBegin(8L, getNameForTrace(), System.identityHashCode(this));
    }
    this.mAnimationEndRequested = false;
    initAnimation();
    this.mRunning = true;
    if (this.mSeekFraction >= 0.0F) {}
    for (this.mOverallFraction = this.mSeekFraction;; this.mOverallFraction = 0.0F)
    {
      if (this.mListeners != null) {
        notifyStartListeners();
      }
      return;
    }
  }
  
  public void addUpdateListener(AnimatorUpdateListener paramAnimatorUpdateListener)
  {
    if (this.mUpdateListeners == null) {
      this.mUpdateListeners = new ArrayList();
    }
    this.mUpdateListeners.add(paramAnimatorUpdateListener);
  }
  
  boolean animateBasedOnTime(long paramLong)
  {
    boolean bool1 = false;
    boolean bool2 = false;
    float f1;
    if (this.mRunning)
    {
      long l = getScaledDuration();
      if (l <= 0L) {
        break label112;
      }
      f1 = (float)(paramLong - this.mStartTime) / (float)l;
      float f2 = this.mOverallFraction;
      if ((int)f1 <= (int)f2) {
        break label117;
      }
      j = 1;
      label55:
      if (f1 < this.mRepeatCount + 1) {
        break label129;
      }
      if (this.mRepeatCount == -1) {
        break label123;
      }
      i = 1;
      label78:
      if (l != 0L) {
        break label135;
      }
      bool1 = true;
    }
    label112:
    label117:
    label123:
    label129:
    label135:
    label160:
    do
    {
      for (;;)
      {
        this.mOverallFraction = clampFraction(f1);
        animateValue(getCurrentIterationFraction(this.mOverallFraction));
        return bool1;
        f1 = 1.0F;
        break;
        j = 0;
        break label55;
        i = 0;
        break label78;
        i = 0;
        break label78;
        if ((j != 0) && (i == 0)) {
          break label160;
        }
        bool1 = bool2;
        if (i != 0) {
          bool1 = true;
        }
      }
      bool1 = bool2;
    } while (this.mListeners == null);
    int j = this.mListeners.size();
    int i = 0;
    for (;;)
    {
      bool1 = bool2;
      if (i >= j) {
        break;
      }
      ((Animator.AnimatorListener)this.mListeners.get(i)).onAnimationRepeat(this);
      i += 1;
    }
  }
  
  void animateValue(float paramFloat)
  {
    paramFloat = this.mInterpolator.getInterpolation(paramFloat);
    this.mCurrentFraction = paramFloat;
    int j = this.mValues.length;
    int i = 0;
    while (i < j)
    {
      this.mValues[i].calculateValue(paramFloat);
      i += 1;
    }
    if (this.mUpdateListeners != null)
    {
      j = this.mUpdateListeners.size();
      i = 0;
      while (i < j)
      {
        ((AnimatorUpdateListener)this.mUpdateListeners.get(i)).onAnimationUpdate(this);
        i += 1;
      }
    }
  }
  
  public boolean canReverse()
  {
    return true;
  }
  
  public void cancel()
  {
    if (Looper.myLooper() == null) {
      throw new AndroidRuntimeException("Animators may only be run on Looper threads");
    }
    if (this.mAnimationEndRequested) {
      return;
    }
    if (((this.mStarted) || (this.mRunning)) && (this.mListeners != null))
    {
      if (!this.mRunning) {
        notifyStartListeners();
      }
      Iterator localIterator = ((ArrayList)this.mListeners.clone()).iterator();
      while (localIterator.hasNext()) {
        ((Animator.AnimatorListener)localIterator.next()).onAnimationCancel(this);
      }
    }
    endAnimation();
  }
  
  public ValueAnimator clone()
  {
    ValueAnimator localValueAnimator = (ValueAnimator)super.clone();
    if (this.mUpdateListeners != null) {
      localValueAnimator.mUpdateListeners = new ArrayList(this.mUpdateListeners);
    }
    localValueAnimator.mSeekFraction = -1.0F;
    localValueAnimator.mReversing = false;
    localValueAnimator.mInitialized = false;
    localValueAnimator.mStarted = false;
    localValueAnimator.mRunning = false;
    localValueAnimator.mPaused = false;
    localValueAnimator.mResumed = false;
    localValueAnimator.mStartListenersCalled = false;
    localValueAnimator.mStartTime = 0L;
    localValueAnimator.mStartTimeCommitted = false;
    localValueAnimator.mAnimationEndRequested = false;
    localValueAnimator.mPauseTime = 0L;
    localValueAnimator.mLastFrameTime = 0L;
    localValueAnimator.mOverallFraction = 0.0F;
    localValueAnimator.mCurrentFraction = 0.0F;
    PropertyValuesHolder[] arrayOfPropertyValuesHolder = this.mValues;
    if (arrayOfPropertyValuesHolder != null)
    {
      int j = arrayOfPropertyValuesHolder.length;
      localValueAnimator.mValues = new PropertyValuesHolder[j];
      localValueAnimator.mValuesMap = new HashMap(j);
      int i = 0;
      while (i < j)
      {
        PropertyValuesHolder localPropertyValuesHolder = arrayOfPropertyValuesHolder[i].clone();
        localValueAnimator.mValues[i] = localPropertyValuesHolder;
        localValueAnimator.mValuesMap.put(localPropertyValuesHolder.getPropertyName(), localPropertyValuesHolder);
        i += 1;
      }
    }
    return localValueAnimator;
  }
  
  public void commitAnimationFrame(long paramLong)
  {
    if (!this.mStartTimeCommitted)
    {
      this.mStartTimeCommitted = true;
      paramLong -= this.mLastFrameTime;
      if (paramLong > 0L) {
        this.mStartTime += paramLong;
      }
    }
  }
  
  public final void doAnimationFrame(long paramLong)
  {
    AnimationHandler localAnimationHandler = AnimationHandler.getInstance();
    if (this.mLastFrameTime == 0L)
    {
      localAnimationHandler.addOneShotCommitCallback(this);
      if (this.mStartDelay > 0L) {
        startAnimation();
      }
      if (this.mSeekFraction >= 0.0F) {
        break label73;
      }
      this.mStartTime = paramLong;
    }
    for (;;)
    {
      this.mStartTimeCommitted = false;
      this.mLastFrameTime = paramLong;
      if (!this.mPaused) {
        break;
      }
      this.mPauseTime = paramLong;
      localAnimationHandler.removeCallback(this);
      return;
      label73:
      this.mStartTime = (paramLong - ((float)getScaledDuration() * this.mSeekFraction));
      this.mSeekFraction = -1.0F;
    }
    if (this.mResumed)
    {
      this.mResumed = false;
      if (this.mPauseTime > 0L)
      {
        this.mStartTime += paramLong - this.mPauseTime;
        this.mStartTimeCommitted = false;
      }
      localAnimationHandler.addOneShotCommitCallback(this);
    }
    if (animateBasedOnTime(Math.max(paramLong, this.mStartTime))) {
      endAnimation();
    }
  }
  
  public void end()
  {
    if (Looper.myLooper() == null) {
      throw new AndroidRuntimeException("Animators may only be run on Looper threads");
    }
    if (!this.mRunning)
    {
      startAnimation();
      this.mStarted = true;
      if (!shouldPlayBackward(this.mRepeatCount)) {
        break label70;
      }
    }
    label70:
    for (float f = 0.0F;; f = 1.0F)
    {
      animateValue(f);
      endAnimation();
      return;
      if (this.mInitialized) {
        break;
      }
      initAnimation();
      break;
    }
  }
  
  public float getAnimatedFraction()
  {
    return this.mCurrentFraction;
  }
  
  public Object getAnimatedValue()
  {
    if ((this.mValues != null) && (this.mValues.length > 0)) {
      return this.mValues[0].getAnimatedValue();
    }
    return null;
  }
  
  public Object getAnimatedValue(String paramString)
  {
    paramString = (PropertyValuesHolder)this.mValuesMap.get(paramString);
    if (paramString != null) {
      return paramString.getAnimatedValue();
    }
    return null;
  }
  
  public long getCurrentPlayTime()
  {
    if ((!this.mInitialized) || ((!this.mStarted) && (this.mSeekFraction < 0.0F))) {
      return 0L;
    }
    if (this.mSeekFraction >= 0.0F) {
      return ((float)this.mDuration * this.mSeekFraction);
    }
    if (sDurationScale == 0.0F) {}
    for (float f = 1.0F;; f = sDurationScale) {
      return ((float)(AnimationUtils.currentAnimationTimeMillis() - this.mStartTime) / f);
    }
  }
  
  public long getDuration()
  {
    return this.mDuration;
  }
  
  public TimeInterpolator getInterpolator()
  {
    return this.mInterpolator;
  }
  
  String getNameForTrace()
  {
    return "animator";
  }
  
  public int getRepeatCount()
  {
    return this.mRepeatCount;
  }
  
  public int getRepeatMode()
  {
    return this.mRepeatMode;
  }
  
  public long getStartDelay()
  {
    return this.mStartDelay;
  }
  
  public long getTotalDuration()
  {
    if (this.mRepeatCount == -1) {
      return -1L;
    }
    return this.mStartDelay + this.mDuration * (this.mRepeatCount + 1);
  }
  
  public PropertyValuesHolder[] getValues()
  {
    return this.mValues;
  }
  
  void initAnimation()
  {
    if (!this.mInitialized)
    {
      int j = this.mValues.length;
      int i = 0;
      while (i < j)
      {
        this.mValues[i].init();
        i += 1;
      }
      this.mInitialized = true;
    }
  }
  
  public boolean isRunning()
  {
    return this.mRunning;
  }
  
  public boolean isStarted()
  {
    return this.mStarted;
  }
  
  public void pause()
  {
    boolean bool = this.mPaused;
    super.pause();
    if ((!bool) && (this.mPaused))
    {
      this.mPauseTime = -1L;
      this.mResumed = false;
    }
  }
  
  public void removeAllUpdateListeners()
  {
    if (this.mUpdateListeners == null) {
      return;
    }
    this.mUpdateListeners.clear();
    this.mUpdateListeners = null;
  }
  
  public void removeUpdateListener(AnimatorUpdateListener paramAnimatorUpdateListener)
  {
    if (this.mUpdateListeners == null) {
      return;
    }
    this.mUpdateListeners.remove(paramAnimatorUpdateListener);
    if (this.mUpdateListeners.size() == 0) {
      this.mUpdateListeners = null;
    }
  }
  
  public void resume()
  {
    if (Looper.myLooper() == null) {
      throw new AndroidRuntimeException("Animators may only be resumed from the same thread that the animator was started on");
    }
    if ((!this.mPaused) || (this.mResumed)) {}
    for (;;)
    {
      super.resume();
      return;
      this.mResumed = true;
      if (this.mPauseTime > 0L) {
        AnimationHandler.getInstance().addAnimationFrameCallback(this, 0L);
      }
    }
  }
  
  public void reverse()
  {
    boolean bool2 = false;
    boolean bool1 = false;
    if (isPulsingInternal())
    {
      long l1 = AnimationUtils.currentAnimationTimeMillis();
      long l2 = this.mStartTime;
      this.mStartTime = (l1 - (getScaledDuration() - (l1 - l2)));
      this.mStartTimeCommitted = true;
      if (this.mReversing) {}
      for (;;)
      {
        this.mReversing = bool1;
        return;
        bool1 = true;
      }
    }
    if (this.mStarted)
    {
      if (this.mReversing) {}
      for (bool1 = bool2;; bool1 = true)
      {
        this.mReversing = bool1;
        end();
        return;
      }
    }
    start(true);
  }
  
  public void setAllowRunningAsynchronously(boolean paramBoolean) {}
  
  public void setCurrentFraction(float paramFloat)
  {
    initAnimation();
    paramFloat = clampFraction(paramFloat);
    long l = ((float)getScaledDuration() * paramFloat);
    this.mStartTime = (AnimationUtils.currentAnimationTimeMillis() - l);
    this.mStartTimeCommitted = true;
    if (!isPulsingInternal()) {
      this.mSeekFraction = paramFloat;
    }
    this.mOverallFraction = paramFloat;
    animateValue(getCurrentIterationFraction(paramFloat));
  }
  
  public void setCurrentPlayTime(long paramLong)
  {
    if (this.mDuration > 0L) {}
    for (float f = (float)paramLong / (float)this.mDuration;; f = 1.0F)
    {
      setCurrentFraction(f);
      return;
    }
  }
  
  public ValueAnimator setDuration(long paramLong)
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("Animators cannot have negative duration: " + paramLong);
    }
    this.mDuration = paramLong;
    return this;
  }
  
  public void setEvaluator(TypeEvaluator paramTypeEvaluator)
  {
    if ((paramTypeEvaluator != null) && (this.mValues != null) && (this.mValues.length > 0)) {
      this.mValues[0].setEvaluator(paramTypeEvaluator);
    }
  }
  
  public void setFloatValues(float... paramVarArgs)
  {
    if ((paramVarArgs == null) || (paramVarArgs.length == 0)) {
      return;
    }
    if ((this.mValues == null) || (this.mValues.length == 0)) {
      setValues(new PropertyValuesHolder[] { PropertyValuesHolder.ofFloat("", paramVarArgs) });
    }
    for (;;)
    {
      this.mInitialized = false;
      return;
      this.mValues[0].setFloatValues(paramVarArgs);
    }
  }
  
  public void setIntValues(int... paramVarArgs)
  {
    if ((paramVarArgs == null) || (paramVarArgs.length == 0)) {
      return;
    }
    if ((this.mValues == null) || (this.mValues.length == 0)) {
      setValues(new PropertyValuesHolder[] { PropertyValuesHolder.ofInt("", paramVarArgs) });
    }
    for (;;)
    {
      this.mInitialized = false;
      return;
      this.mValues[0].setIntValues(paramVarArgs);
    }
  }
  
  public void setInterpolator(TimeInterpolator paramTimeInterpolator)
  {
    if (paramTimeInterpolator != null)
    {
      this.mInterpolator = paramTimeInterpolator;
      return;
    }
    this.mInterpolator = new LinearInterpolator();
  }
  
  public void setObjectValues(Object... paramVarArgs)
  {
    if ((paramVarArgs == null) || (paramVarArgs.length == 0)) {
      return;
    }
    if ((this.mValues == null) || (this.mValues.length == 0)) {
      setValues(new PropertyValuesHolder[] { PropertyValuesHolder.ofObject("", null, paramVarArgs) });
    }
    for (;;)
    {
      this.mInitialized = false;
      return;
      this.mValues[0].setObjectValues(paramVarArgs);
    }
  }
  
  public void setRepeatCount(int paramInt)
  {
    this.mRepeatCount = paramInt;
  }
  
  public void setRepeatMode(int paramInt)
  {
    this.mRepeatMode = paramInt;
  }
  
  public void setStartDelay(long paramLong)
  {
    long l = paramLong;
    if (paramLong < 0L)
    {
      Log.w("ValueAnimator", "Start delay should always be non-negative");
      l = 0L;
    }
    this.mStartDelay = l;
  }
  
  public void setValues(PropertyValuesHolder... paramVarArgs)
  {
    int j = paramVarArgs.length;
    this.mValues = paramVarArgs;
    this.mValuesMap = new HashMap(j);
    int i = 0;
    while (i < j)
    {
      PropertyValuesHolder localPropertyValuesHolder = paramVarArgs[i];
      this.mValuesMap.put(localPropertyValuesHolder.getPropertyName(), localPropertyValuesHolder);
      i += 1;
    }
    this.mInitialized = false;
  }
  
  public void start()
  {
    start(false);
  }
  
  public String toString()
  {
    String str1 = "ValueAnimator@" + Integer.toHexString(hashCode());
    String str2 = str1;
    if (this.mValues != null)
    {
      int i = 0;
      for (;;)
      {
        str2 = str1;
        if (i >= this.mValues.length) {
          break;
        }
        str1 = str1 + "\n    " + this.mValues[i].toString();
        i += 1;
      }
    }
    return str2;
  }
  
  public static abstract interface AnimatorUpdateListener
  {
    public abstract void onAnimationUpdate(ValueAnimator paramValueAnimator);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/animation/ValueAnimator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */