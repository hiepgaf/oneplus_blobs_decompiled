package android.animation;

import android.content.res.ConstantState;
import android.util.StateSet;
import android.view.View;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class StateListAnimator
  implements Cloneable
{
  private AnimatorListenerAdapter mAnimatorListener;
  private int mChangingConfigurations;
  private StateListAnimatorConstantState mConstantState;
  private Tuple mLastMatch = null;
  private Animator mRunningAnimator = null;
  private ArrayList<Tuple> mTuples = new ArrayList();
  private WeakReference<View> mViewRef;
  
  public StateListAnimator()
  {
    initAnimatorListener();
  }
  
  private void cancel()
  {
    if (this.mRunningAnimator != null)
    {
      this.mRunningAnimator.cancel();
      this.mRunningAnimator = null;
    }
  }
  
  private void clearTarget()
  {
    int j = this.mTuples.size();
    int i = 0;
    while (i < j)
    {
      ((Tuple)this.mTuples.get(i)).mAnimator.setTarget(null);
      i += 1;
    }
    this.mViewRef = null;
    this.mLastMatch = null;
    this.mRunningAnimator = null;
  }
  
  private void initAnimatorListener()
  {
    this.mAnimatorListener = new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        paramAnonymousAnimator.setTarget(null);
        if (StateListAnimator.-get0(StateListAnimator.this) == paramAnonymousAnimator) {
          StateListAnimator.-set1(StateListAnimator.this, null);
        }
      }
    };
  }
  
  private void start(Tuple paramTuple)
  {
    paramTuple.mAnimator.setTarget(getTarget());
    this.mRunningAnimator = paramTuple.mAnimator;
    this.mRunningAnimator.start();
  }
  
  public void addState(int[] paramArrayOfInt, Animator paramAnimator)
  {
    paramArrayOfInt = new Tuple(paramArrayOfInt, paramAnimator, null);
    paramArrayOfInt.mAnimator.addListener(this.mAnimatorListener);
    this.mTuples.add(paramArrayOfInt);
    this.mChangingConfigurations |= paramAnimator.getChangingConfigurations();
  }
  
  public void appendChangingConfigurations(int paramInt)
  {
    this.mChangingConfigurations |= paramInt;
  }
  
  public StateListAnimator clone()
  {
    try
    {
      StateListAnimator localStateListAnimator = (StateListAnimator)super.clone();
      localStateListAnimator.mTuples = new ArrayList(this.mTuples.size());
      localStateListAnimator.mLastMatch = null;
      localStateListAnimator.mRunningAnimator = null;
      localStateListAnimator.mViewRef = null;
      localStateListAnimator.mAnimatorListener = null;
      localStateListAnimator.initAnimatorListener();
      int j = this.mTuples.size();
      int i = 0;
      while (i < j)
      {
        Tuple localTuple = (Tuple)this.mTuples.get(i);
        Animator localAnimator = localTuple.mAnimator.clone();
        localAnimator.removeListener(this.mAnimatorListener);
        localStateListAnimator.addState(localTuple.mSpecs, localAnimator);
        i += 1;
      }
      localStateListAnimator.setChangingConfigurations(getChangingConfigurations());
      return localStateListAnimator;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new AssertionError("cannot clone state list animator", localCloneNotSupportedException);
    }
  }
  
  public ConstantState<StateListAnimator> createConstantState()
  {
    return new StateListAnimatorConstantState(this);
  }
  
  public int getChangingConfigurations()
  {
    return this.mChangingConfigurations;
  }
  
  public Animator getRunningAnimator()
  {
    return this.mRunningAnimator;
  }
  
  public View getTarget()
  {
    if (this.mViewRef == null) {
      return null;
    }
    return (View)this.mViewRef.get();
  }
  
  public ArrayList<Tuple> getTuples()
  {
    return this.mTuples;
  }
  
  public void jumpToCurrentState()
  {
    if (this.mRunningAnimator != null) {
      this.mRunningAnimator.end();
    }
  }
  
  public void setChangingConfigurations(int paramInt)
  {
    this.mChangingConfigurations = paramInt;
  }
  
  public void setState(int[] paramArrayOfInt)
  {
    Object localObject2 = null;
    int j = this.mTuples.size();
    int i = 0;
    Object localObject1;
    for (;;)
    {
      localObject1 = localObject2;
      if (i < j)
      {
        localObject1 = (Tuple)this.mTuples.get(i);
        if (!StateSet.stateSetMatches(((Tuple)localObject1).mSpecs, paramArrayOfInt)) {}
      }
      else
      {
        if (localObject1 != this.mLastMatch) {
          break;
        }
        return;
      }
      i += 1;
    }
    if (this.mLastMatch != null) {
      cancel();
    }
    this.mLastMatch = ((Tuple)localObject1);
    if (localObject1 != null) {
      start((Tuple)localObject1);
    }
  }
  
  public void setTarget(View paramView)
  {
    View localView = getTarget();
    if (localView == paramView) {
      return;
    }
    if (localView != null) {
      clearTarget();
    }
    if (paramView != null) {
      this.mViewRef = new WeakReference(paramView);
    }
  }
  
  private static class StateListAnimatorConstantState
    extends ConstantState<StateListAnimator>
  {
    final StateListAnimator mAnimator;
    int mChangingConf;
    
    public StateListAnimatorConstantState(StateListAnimator paramStateListAnimator)
    {
      this.mAnimator = paramStateListAnimator;
      StateListAnimator.-set0(this.mAnimator, this);
      this.mChangingConf = this.mAnimator.getChangingConfigurations();
    }
    
    public int getChangingConfigurations()
    {
      return this.mChangingConf;
    }
    
    public StateListAnimator newInstance()
    {
      StateListAnimator localStateListAnimator = this.mAnimator.clone();
      StateListAnimator.-set0(localStateListAnimator, this);
      return localStateListAnimator;
    }
  }
  
  public static class Tuple
  {
    final Animator mAnimator;
    final int[] mSpecs;
    
    private Tuple(int[] paramArrayOfInt, Animator paramAnimator)
    {
      this.mSpecs = paramArrayOfInt;
      this.mAnimator = paramAnimator;
    }
    
    public Animator getAnimator()
    {
      return this.mAnimator;
    }
    
    public int[] getSpecs()
    {
      return this.mSpecs;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/animation/StateListAnimator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */