package android.animation;

import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LayoutTransition
{
  private static TimeInterpolator ACCEL_DECEL_INTERPOLATOR;
  public static final int APPEARING = 2;
  public static final int CHANGE_APPEARING = 0;
  public static final int CHANGE_DISAPPEARING = 1;
  public static final int CHANGING = 4;
  private static TimeInterpolator DECEL_INTERPOLATOR;
  private static long DEFAULT_DURATION = 300L;
  public static final int DISAPPEARING = 3;
  private static final int FLAG_APPEARING = 1;
  private static final int FLAG_CHANGE_APPEARING = 4;
  private static final int FLAG_CHANGE_DISAPPEARING = 8;
  private static final int FLAG_CHANGING = 16;
  private static final int FLAG_DISAPPEARING = 2;
  private static ObjectAnimator defaultChange;
  private static ObjectAnimator defaultChangeIn;
  private static ObjectAnimator defaultChangeOut;
  private static ObjectAnimator defaultFadeIn;
  private static ObjectAnimator defaultFadeOut;
  private static TimeInterpolator sAppearingInterpolator;
  private static TimeInterpolator sChangingAppearingInterpolator = DECEL_INTERPOLATOR;
  private static TimeInterpolator sChangingDisappearingInterpolator = DECEL_INTERPOLATOR;
  private static TimeInterpolator sChangingInterpolator = DECEL_INTERPOLATOR;
  private static TimeInterpolator sDisappearingInterpolator;
  private final LinkedHashMap<View, Animator> currentAppearingAnimations = new LinkedHashMap();
  private final LinkedHashMap<View, Animator> currentChangingAnimations = new LinkedHashMap();
  private final LinkedHashMap<View, Animator> currentDisappearingAnimations = new LinkedHashMap();
  private final HashMap<View, View.OnLayoutChangeListener> layoutChangeListenerMap = new HashMap();
  private boolean mAnimateParentHierarchy = true;
  private Animator mAppearingAnim = null;
  private long mAppearingDelay = DEFAULT_DURATION;
  private long mAppearingDuration = DEFAULT_DURATION;
  private TimeInterpolator mAppearingInterpolator = sAppearingInterpolator;
  private Animator mChangingAnim = null;
  private Animator mChangingAppearingAnim = null;
  private long mChangingAppearingDelay = 0L;
  private long mChangingAppearingDuration = DEFAULT_DURATION;
  private TimeInterpolator mChangingAppearingInterpolator = sChangingAppearingInterpolator;
  private long mChangingAppearingStagger = 0L;
  private long mChangingDelay = 0L;
  private Animator mChangingDisappearingAnim = null;
  private long mChangingDisappearingDelay = DEFAULT_DURATION;
  private long mChangingDisappearingDuration = DEFAULT_DURATION;
  private TimeInterpolator mChangingDisappearingInterpolator = sChangingDisappearingInterpolator;
  private long mChangingDisappearingStagger = 0L;
  private long mChangingDuration = DEFAULT_DURATION;
  private TimeInterpolator mChangingInterpolator = sChangingInterpolator;
  private long mChangingStagger = 0L;
  private Animator mDisappearingAnim = null;
  private long mDisappearingDelay = 0L;
  private long mDisappearingDuration = DEFAULT_DURATION;
  private TimeInterpolator mDisappearingInterpolator = sDisappearingInterpolator;
  private ArrayList<TransitionListener> mListeners;
  private int mTransitionTypes = 15;
  private final HashMap<View, Animator> pendingAnimations = new HashMap();
  private long staggerDelay;
  
  static
  {
    ACCEL_DECEL_INTERPOLATOR = new AccelerateDecelerateInterpolator();
    DECEL_INTERPOLATOR = new DecelerateInterpolator();
    sAppearingInterpolator = ACCEL_DECEL_INTERPOLATOR;
    sDisappearingInterpolator = ACCEL_DECEL_INTERPOLATOR;
  }
  
  public LayoutTransition()
  {
    if (defaultChangeIn == null)
    {
      PropertyValuesHolder localPropertyValuesHolder1 = PropertyValuesHolder.ofInt("left", new int[] { 0, 1 });
      PropertyValuesHolder localPropertyValuesHolder2 = PropertyValuesHolder.ofInt("top", new int[] { 0, 1 });
      PropertyValuesHolder localPropertyValuesHolder3 = PropertyValuesHolder.ofInt("right", new int[] { 0, 1 });
      PropertyValuesHolder localPropertyValuesHolder4 = PropertyValuesHolder.ofInt("bottom", new int[] { 0, 1 });
      PropertyValuesHolder localPropertyValuesHolder5 = PropertyValuesHolder.ofInt("scrollX", new int[] { 0, 1 });
      PropertyValuesHolder localPropertyValuesHolder6 = PropertyValuesHolder.ofInt("scrollY", new int[] { 0, 1 });
      defaultChangeIn = ObjectAnimator.ofPropertyValuesHolder((Object)null, new PropertyValuesHolder[] { localPropertyValuesHolder1, localPropertyValuesHolder2, localPropertyValuesHolder3, localPropertyValuesHolder4, localPropertyValuesHolder5, localPropertyValuesHolder6 });
      defaultChangeIn.setDuration(DEFAULT_DURATION);
      defaultChangeIn.setStartDelay(this.mChangingAppearingDelay);
      defaultChangeIn.setInterpolator(this.mChangingAppearingInterpolator);
      defaultChangeOut = defaultChangeIn.clone();
      defaultChangeOut.setStartDelay(this.mChangingDisappearingDelay);
      defaultChangeOut.setInterpolator(this.mChangingDisappearingInterpolator);
      defaultChange = defaultChangeIn.clone();
      defaultChange.setStartDelay(this.mChangingDelay);
      defaultChange.setInterpolator(this.mChangingInterpolator);
      defaultFadeIn = ObjectAnimator.ofFloat(null, "alpha", new float[] { 0.0F, 1.0F });
      defaultFadeIn.setDuration(DEFAULT_DURATION);
      defaultFadeIn.setStartDelay(this.mAppearingDelay);
      defaultFadeIn.setInterpolator(this.mAppearingInterpolator);
      defaultFadeOut = ObjectAnimator.ofFloat(null, "alpha", new float[] { 1.0F, 0.0F });
      defaultFadeOut.setDuration(DEFAULT_DURATION);
      defaultFadeOut.setStartDelay(this.mDisappearingDelay);
      defaultFadeOut.setInterpolator(this.mDisappearingInterpolator);
    }
    this.mChangingAppearingAnim = defaultChangeIn;
    this.mChangingDisappearingAnim = defaultChangeOut;
    this.mChangingAnim = defaultChange;
    this.mAppearingAnim = defaultFadeIn;
    this.mDisappearingAnim = defaultFadeOut;
  }
  
  private void addChild(ViewGroup paramViewGroup, View paramView, boolean paramBoolean)
  {
    if (paramViewGroup.getWindowVisibility() != 0) {
      return;
    }
    if ((this.mTransitionTypes & 0x1) == 1) {
      cancel(3);
    }
    if ((paramBoolean) && ((this.mTransitionTypes & 0x4) == 4))
    {
      cancel(0);
      cancel(4);
    }
    if ((hasListeners()) && ((this.mTransitionTypes & 0x1) == 1))
    {
      Iterator localIterator = ((ArrayList)this.mListeners.clone()).iterator();
      while (localIterator.hasNext()) {
        ((TransitionListener)localIterator.next()).startTransition(this, paramViewGroup, paramView, 2);
      }
    }
    if ((paramBoolean) && ((this.mTransitionTypes & 0x4) == 4)) {
      runChangeTransition(paramViewGroup, paramView, 2);
    }
    if ((this.mTransitionTypes & 0x1) == 1) {
      runAppearingTransition(paramViewGroup, paramView);
    }
  }
  
  private boolean hasListeners()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mListeners != null)
    {
      bool1 = bool2;
      if (this.mListeners.size() > 0) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private void removeChild(ViewGroup paramViewGroup, View paramView, boolean paramBoolean)
  {
    if (paramViewGroup.getWindowVisibility() != 0) {
      return;
    }
    if ((this.mTransitionTypes & 0x2) == 2) {
      cancel(2);
    }
    if ((paramBoolean) && ((this.mTransitionTypes & 0x8) == 8))
    {
      cancel(1);
      cancel(4);
    }
    if ((hasListeners()) && ((this.mTransitionTypes & 0x2) == 2))
    {
      Iterator localIterator = ((ArrayList)this.mListeners.clone()).iterator();
      while (localIterator.hasNext()) {
        ((TransitionListener)localIterator.next()).startTransition(this, paramViewGroup, paramView, 3);
      }
    }
    if ((paramBoolean) && ((this.mTransitionTypes & 0x8) == 8)) {
      runChangeTransition(paramViewGroup, paramView, 3);
    }
    if ((this.mTransitionTypes & 0x2) == 2) {
      runDisappearingTransition(paramViewGroup, paramView);
    }
  }
  
  private void runAppearingTransition(final ViewGroup paramViewGroup, final View paramView)
  {
    Object localObject = (Animator)this.currentDisappearingAnimations.get(paramView);
    if (localObject != null) {
      ((Animator)localObject).cancel();
    }
    if (this.mAppearingAnim == null)
    {
      if (hasListeners())
      {
        localObject = ((ArrayList)this.mListeners.clone()).iterator();
        while (((Iterator)localObject).hasNext()) {
          ((TransitionListener)((Iterator)localObject).next()).endTransition(this, paramViewGroup, paramView, 2);
        }
      }
      return;
    }
    localObject = this.mAppearingAnim.clone();
    ((Animator)localObject).setTarget(paramView);
    ((Animator)localObject).setStartDelay(this.mAppearingDelay);
    ((Animator)localObject).setDuration(this.mAppearingDuration);
    if (this.mAppearingInterpolator != sAppearingInterpolator) {
      ((Animator)localObject).setInterpolator(this.mAppearingInterpolator);
    }
    if ((localObject instanceof ObjectAnimator)) {
      ((ObjectAnimator)localObject).setCurrentPlayTime(0L);
    }
    ((Animator)localObject).addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        LayoutTransition.-get0(LayoutTransition.this).remove(paramView);
        if (LayoutTransition.-wrap0(LayoutTransition.this))
        {
          paramAnonymousAnimator = ((ArrayList)LayoutTransition.-get13(LayoutTransition.this).clone()).iterator();
          while (paramAnonymousAnimator.hasNext()) {
            ((LayoutTransition.TransitionListener)paramAnonymousAnimator.next()).endTransition(LayoutTransition.this, paramViewGroup, paramView, 2);
          }
        }
      }
    });
    this.currentAppearingAnimations.put(paramView, localObject);
    ((Animator)localObject).start();
  }
  
  private void runChangeTransition(ViewGroup paramViewGroup, View paramView, int paramInt)
  {
    Object localObject = null;
    ObjectAnimator localObjectAnimator = null;
    long l;
    switch (paramInt)
    {
    default: 
      l = 0L;
    }
    while (localObject == null)
    {
      return;
      localObject = this.mChangingAppearingAnim;
      l = this.mChangingAppearingDuration;
      localObjectAnimator = defaultChangeIn;
      continue;
      localObject = this.mChangingDisappearingAnim;
      l = this.mChangingDisappearingDuration;
      localObjectAnimator = defaultChangeOut;
      continue;
      localObject = this.mChangingAnim;
      l = this.mChangingDuration;
      localObjectAnimator = defaultChange;
    }
    this.staggerDelay = 0L;
    ViewTreeObserver localViewTreeObserver = paramViewGroup.getViewTreeObserver();
    if (!localViewTreeObserver.isAlive()) {
      return;
    }
    int j = paramViewGroup.getChildCount();
    int i = 0;
    while (i < j)
    {
      View localView = paramViewGroup.getChildAt(i);
      if (localView != paramView) {
        setupChangeAnimation(paramViewGroup, paramInt, (Animator)localObject, l, localView);
      }
      i += 1;
    }
    if (this.mAnimateParentHierarchy)
    {
      paramView = paramViewGroup;
      while (paramView != null)
      {
        localObject = paramView.getParent();
        if ((localObject instanceof ViewGroup))
        {
          setupChangeAnimation((ViewGroup)localObject, paramInt, localObjectAnimator, l, paramView);
          paramView = (ViewGroup)localObject;
        }
        else
        {
          paramView = null;
        }
      }
    }
    paramView = new CleanupCallback(this.layoutChangeListenerMap, paramViewGroup);
    localViewTreeObserver.addOnPreDrawListener(paramView);
    paramViewGroup.addOnAttachStateChangeListener(paramView);
  }
  
  private void runDisappearingTransition(final ViewGroup paramViewGroup, final View paramView)
  {
    Object localObject = (Animator)this.currentAppearingAnimations.get(paramView);
    if (localObject != null) {
      ((Animator)localObject).cancel();
    }
    if (this.mDisappearingAnim == null)
    {
      if (hasListeners())
      {
        localObject = ((ArrayList)this.mListeners.clone()).iterator();
        while (((Iterator)localObject).hasNext()) {
          ((TransitionListener)((Iterator)localObject).next()).endTransition(this, paramViewGroup, paramView, 3);
        }
      }
      return;
    }
    localObject = this.mDisappearingAnim.clone();
    ((Animator)localObject).setStartDelay(this.mDisappearingDelay);
    ((Animator)localObject).setDuration(this.mDisappearingDuration);
    if (this.mDisappearingInterpolator != sDisappearingInterpolator) {
      ((Animator)localObject).setInterpolator(this.mDisappearingInterpolator);
    }
    ((Animator)localObject).setTarget(paramView);
    ((Animator)localObject).addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        LayoutTransition.-get2(LayoutTransition.this).remove(paramView);
        paramView.setAlpha(this.val$preAnimAlpha);
        if (LayoutTransition.-wrap0(LayoutTransition.this))
        {
          paramAnonymousAnimator = ((ArrayList)LayoutTransition.-get13(LayoutTransition.this).clone()).iterator();
          while (paramAnonymousAnimator.hasNext()) {
            ((LayoutTransition.TransitionListener)paramAnonymousAnimator.next()).endTransition(LayoutTransition.this, paramViewGroup, paramView, 3);
          }
        }
      }
    });
    if ((localObject instanceof ObjectAnimator)) {
      ((ObjectAnimator)localObject).setCurrentPlayTime(0L);
    }
    this.currentDisappearingAnimations.put(paramView, localObject);
    ((Animator)localObject).start();
  }
  
  private void setupChangeAnimation(final ViewGroup paramViewGroup, final int paramInt, final Animator paramAnimator, final long paramLong, final View paramView)
  {
    if (this.layoutChangeListenerMap.get(paramView) != null) {
      return;
    }
    if ((paramView.getWidth() == 0) && (paramView.getHeight() == 0)) {
      return;
    }
    paramAnimator = paramAnimator.clone();
    paramAnimator.setTarget(paramView);
    paramAnimator.setupStartValues();
    final Object localObject = (Animator)this.pendingAnimations.get(paramView);
    if (localObject != null)
    {
      ((Animator)localObject).cancel();
      this.pendingAnimations.remove(paramView);
    }
    this.pendingAnimations.put(paramView, paramAnimator);
    localObject = ValueAnimator.ofFloat(new float[] { 0.0F, 1.0F }).setDuration(100L + paramLong);
    ((ValueAnimator)localObject).addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        LayoutTransition.-get14(LayoutTransition.this).remove(paramView);
      }
    });
    ((ValueAnimator)localObject).start();
    localObject = new View.OnLayoutChangeListener()
    {
      public void onLayoutChange(View paramAnonymousView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4, int paramAnonymousInt5, int paramAnonymousInt6, int paramAnonymousInt7, int paramAnonymousInt8)
      {
        paramAnimator.setupEndValues();
        if ((paramAnimator instanceof ValueAnimator))
        {
          paramAnonymousInt1 = 0;
          paramAnonymousView = ((ValueAnimator)paramAnimator).getValues();
          paramAnonymousInt2 = 0;
          if (paramAnonymousInt2 < paramAnonymousView.length)
          {
            KeyframeSet localKeyframeSet = paramAnonymousView[paramAnonymousInt2];
            if ((localKeyframeSet.mKeyframes instanceof KeyframeSet))
            {
              localKeyframeSet = (KeyframeSet)localKeyframeSet.mKeyframes;
              if ((localKeyframeSet.mFirstKeyframe == null) || (localKeyframeSet.mLastKeyframe == null)) {
                label80:
                paramAnonymousInt1 = 1;
              }
            }
            for (;;)
            {
              paramAnonymousInt2 += 1;
              break;
              if (!localKeyframeSet.mFirstKeyframe.getValue().equals(localKeyframeSet.mLastKeyframe.getValue())) {
                break label80;
              }
              continue;
              if (!localKeyframeSet.mKeyframes.getValue(0.0F).equals(localKeyframeSet.mKeyframes.getValue(1.0F))) {
                paramAnonymousInt1 = 1;
              }
            }
          }
          if (paramAnonymousInt1 == 0) {
            return;
          }
        }
        long l1 = 0L;
        switch (paramInt)
        {
        }
        for (;;)
        {
          paramAnimator.setStartDelay(l1);
          paramAnimator.setDuration(paramLong);
          paramAnonymousView = (Animator)LayoutTransition.-get1(LayoutTransition.this).get(paramViewGroup);
          if (paramAnonymousView != null) {
            paramAnonymousView.cancel();
          }
          if ((Animator)LayoutTransition.-get14(LayoutTransition.this).get(paramViewGroup) != null) {
            LayoutTransition.-get14(LayoutTransition.this).remove(paramViewGroup);
          }
          LayoutTransition.-get1(LayoutTransition.this).put(paramViewGroup, paramAnimator);
          this.val$parent.requestTransitionStart(LayoutTransition.this);
          paramViewGroup.removeOnLayoutChangeListener(this);
          LayoutTransition.-get3(LayoutTransition.this).remove(paramViewGroup);
          return;
          long l2 = LayoutTransition.-get4(LayoutTransition.this) + LayoutTransition.-get18(LayoutTransition.this);
          paramAnonymousView = LayoutTransition.this;
          LayoutTransition.-set0(paramAnonymousView, LayoutTransition.-get18(paramAnonymousView) + LayoutTransition.-get6(LayoutTransition.this));
          l1 = l2;
          if (LayoutTransition.-get5(LayoutTransition.this) != LayoutTransition.-get15())
          {
            paramAnimator.setInterpolator(LayoutTransition.-get5(LayoutTransition.this));
            l1 = l2;
            continue;
            l2 = LayoutTransition.-get8(LayoutTransition.this) + LayoutTransition.-get18(LayoutTransition.this);
            paramAnonymousView = LayoutTransition.this;
            LayoutTransition.-set0(paramAnonymousView, LayoutTransition.-get18(paramAnonymousView) + LayoutTransition.-get10(LayoutTransition.this));
            l1 = l2;
            if (LayoutTransition.-get9(LayoutTransition.this) != LayoutTransition.-get16())
            {
              paramAnimator.setInterpolator(LayoutTransition.-get9(LayoutTransition.this));
              l1 = l2;
              continue;
              l2 = LayoutTransition.-get7(LayoutTransition.this) + LayoutTransition.-get18(LayoutTransition.this);
              paramAnonymousView = LayoutTransition.this;
              LayoutTransition.-set0(paramAnonymousView, LayoutTransition.-get18(paramAnonymousView) + LayoutTransition.-get12(LayoutTransition.this));
              l1 = l2;
              if (LayoutTransition.-get11(LayoutTransition.this) != LayoutTransition.-get17())
              {
                paramAnimator.setInterpolator(LayoutTransition.-get11(LayoutTransition.this));
                l1 = l2;
              }
            }
          }
        }
      }
    };
    paramAnimator.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationCancel(Animator paramAnonymousAnimator)
      {
        paramView.removeOnLayoutChangeListener(localObject);
        LayoutTransition.-get3(LayoutTransition.this).remove(paramView);
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        LayoutTransition.-get1(LayoutTransition.this).remove(paramView);
        if (LayoutTransition.-wrap0(LayoutTransition.this))
        {
          paramAnonymousAnimator = ((ArrayList)LayoutTransition.-get13(LayoutTransition.this).clone()).iterator();
          if (paramAnonymousAnimator.hasNext())
          {
            LayoutTransition.TransitionListener localTransitionListener = (LayoutTransition.TransitionListener)paramAnonymousAnimator.next();
            LayoutTransition localLayoutTransition = LayoutTransition.this;
            ViewGroup localViewGroup = paramViewGroup;
            View localView = paramView;
            int i;
            if (paramInt == 2) {
              i = 0;
            }
            for (;;)
            {
              localTransitionListener.endTransition(localLayoutTransition, localViewGroup, localView, i);
              break;
              if (paramInt == 3) {
                i = 1;
              } else {
                i = 4;
              }
            }
          }
        }
      }
      
      public void onAnimationStart(Animator paramAnonymousAnimator)
      {
        if (LayoutTransition.-wrap0(LayoutTransition.this))
        {
          paramAnonymousAnimator = ((ArrayList)LayoutTransition.-get13(LayoutTransition.this).clone()).iterator();
          if (paramAnonymousAnimator.hasNext())
          {
            LayoutTransition.TransitionListener localTransitionListener = (LayoutTransition.TransitionListener)paramAnonymousAnimator.next();
            LayoutTransition localLayoutTransition = LayoutTransition.this;
            ViewGroup localViewGroup = paramViewGroup;
            View localView = paramView;
            int i;
            if (paramInt == 2) {
              i = 0;
            }
            for (;;)
            {
              localTransitionListener.startTransition(localLayoutTransition, localViewGroup, localView, i);
              break;
              if (paramInt == 3) {
                i = 1;
              } else {
                i = 4;
              }
            }
          }
        }
      }
    });
    paramView.addOnLayoutChangeListener((View.OnLayoutChangeListener)localObject);
    this.layoutChangeListenerMap.put(paramView, localObject);
  }
  
  public void addChild(ViewGroup paramViewGroup, View paramView)
  {
    addChild(paramViewGroup, paramView, true);
  }
  
  public void addTransitionListener(TransitionListener paramTransitionListener)
  {
    if (this.mListeners == null) {
      this.mListeners = new ArrayList();
    }
    this.mListeners.add(paramTransitionListener);
  }
  
  public void cancel()
  {
    Iterator localIterator;
    if (this.currentChangingAnimations.size() > 0)
    {
      localIterator = ((LinkedHashMap)this.currentChangingAnimations.clone()).values().iterator();
      while (localIterator.hasNext()) {
        ((Animator)localIterator.next()).cancel();
      }
      this.currentChangingAnimations.clear();
    }
    if (this.currentAppearingAnimations.size() > 0)
    {
      localIterator = ((LinkedHashMap)this.currentAppearingAnimations.clone()).values().iterator();
      while (localIterator.hasNext()) {
        ((Animator)localIterator.next()).end();
      }
      this.currentAppearingAnimations.clear();
    }
    if (this.currentDisappearingAnimations.size() > 0)
    {
      localIterator = ((LinkedHashMap)this.currentDisappearingAnimations.clone()).values().iterator();
      while (localIterator.hasNext()) {
        ((Animator)localIterator.next()).end();
      }
      this.currentDisappearingAnimations.clear();
    }
  }
  
  public void cancel(int paramInt)
  {
    switch (paramInt)
    {
    }
    do
    {
      do
      {
        do
        {
          return;
        } while (this.currentChangingAnimations.size() <= 0);
        localIterator = ((LinkedHashMap)this.currentChangingAnimations.clone()).values().iterator();
        while (localIterator.hasNext()) {
          ((Animator)localIterator.next()).cancel();
        }
        this.currentChangingAnimations.clear();
        return;
      } while (this.currentAppearingAnimations.size() <= 0);
      localIterator = ((LinkedHashMap)this.currentAppearingAnimations.clone()).values().iterator();
      while (localIterator.hasNext()) {
        ((Animator)localIterator.next()).end();
      }
      this.currentAppearingAnimations.clear();
      return;
    } while (this.currentDisappearingAnimations.size() <= 0);
    Iterator localIterator = ((LinkedHashMap)this.currentDisappearingAnimations.clone()).values().iterator();
    while (localIterator.hasNext()) {
      ((Animator)localIterator.next()).end();
    }
    this.currentDisappearingAnimations.clear();
  }
  
  public void disableTransitionType(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return;
    case 2: 
      this.mTransitionTypes &= 0xFFFFFFFE;
      return;
    case 3: 
      this.mTransitionTypes &= 0xFFFFFFFD;
      return;
    case 0: 
      this.mTransitionTypes &= 0xFFFFFFFB;
      return;
    case 1: 
      this.mTransitionTypes &= 0xFFFFFFF7;
      return;
    }
    this.mTransitionTypes &= 0xFFFFFFEF;
  }
  
  public void enableTransitionType(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return;
    case 2: 
      this.mTransitionTypes |= 0x1;
      return;
    case 3: 
      this.mTransitionTypes |= 0x2;
      return;
    case 0: 
      this.mTransitionTypes |= 0x4;
      return;
    case 1: 
      this.mTransitionTypes |= 0x8;
      return;
    }
    this.mTransitionTypes |= 0x10;
  }
  
  public void endChangingAnimations()
  {
    Iterator localIterator = ((LinkedHashMap)this.currentChangingAnimations.clone()).values().iterator();
    while (localIterator.hasNext())
    {
      Animator localAnimator = (Animator)localIterator.next();
      localAnimator.start();
      localAnimator.end();
    }
    this.currentChangingAnimations.clear();
  }
  
  public Animator getAnimator(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return null;
    case 0: 
      return this.mChangingAppearingAnim;
    case 1: 
      return this.mChangingDisappearingAnim;
    case 4: 
      return this.mChangingAnim;
    case 2: 
      return this.mAppearingAnim;
    }
    return this.mDisappearingAnim;
  }
  
  public long getDuration(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return 0L;
    case 0: 
      return this.mChangingAppearingDuration;
    case 1: 
      return this.mChangingDisappearingDuration;
    case 4: 
      return this.mChangingDuration;
    case 2: 
      return this.mAppearingDuration;
    }
    return this.mDisappearingDuration;
  }
  
  public TimeInterpolator getInterpolator(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return null;
    case 0: 
      return this.mChangingAppearingInterpolator;
    case 1: 
      return this.mChangingDisappearingInterpolator;
    case 4: 
      return this.mChangingInterpolator;
    case 2: 
      return this.mAppearingInterpolator;
    }
    return this.mDisappearingInterpolator;
  }
  
  public long getStagger(int paramInt)
  {
    switch (paramInt)
    {
    case 2: 
    case 3: 
    default: 
      return 0L;
    case 0: 
      return this.mChangingAppearingStagger;
    case 1: 
      return this.mChangingDisappearingStagger;
    }
    return this.mChangingStagger;
  }
  
  public long getStartDelay(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return 0L;
    case 0: 
      return this.mChangingAppearingDelay;
    case 1: 
      return this.mChangingDisappearingDelay;
    case 4: 
      return this.mChangingDelay;
    case 2: 
      return this.mAppearingDelay;
    }
    return this.mDisappearingDelay;
  }
  
  public List<TransitionListener> getTransitionListeners()
  {
    return this.mListeners;
  }
  
  @Deprecated
  public void hideChild(ViewGroup paramViewGroup, View paramView)
  {
    removeChild(paramViewGroup, paramView, true);
  }
  
  public void hideChild(ViewGroup paramViewGroup, View paramView, int paramInt)
  {
    if (paramInt == 8) {}
    for (boolean bool = true;; bool = false)
    {
      removeChild(paramViewGroup, paramView, bool);
      return;
    }
  }
  
  public boolean isChangingLayout()
  {
    boolean bool = false;
    if (this.currentChangingAnimations.size() > 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isRunning()
  {
    if ((this.currentChangingAnimations.size() > 0) || (this.currentAppearingAnimations.size() > 0)) {}
    while (this.currentDisappearingAnimations.size() > 0) {
      return true;
    }
    return false;
  }
  
  public boolean isTransitionTypeEnabled(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return false;
    case 2: 
      return (this.mTransitionTypes & 0x1) == 1;
    case 3: 
      return (this.mTransitionTypes & 0x2) == 2;
    case 0: 
      return (this.mTransitionTypes & 0x4) == 4;
    case 1: 
      return (this.mTransitionTypes & 0x8) == 8;
    }
    return (this.mTransitionTypes & 0x10) == 16;
  }
  
  public void layoutChange(ViewGroup paramViewGroup)
  {
    if (paramViewGroup.getWindowVisibility() != 0) {
      return;
    }
    if (((this.mTransitionTypes & 0x10) != 16) || (isRunning())) {
      return;
    }
    runChangeTransition(paramViewGroup, null, 4);
  }
  
  public void removeChild(ViewGroup paramViewGroup, View paramView)
  {
    removeChild(paramViewGroup, paramView, true);
  }
  
  public void removeTransitionListener(TransitionListener paramTransitionListener)
  {
    if (this.mListeners == null) {
      return;
    }
    this.mListeners.remove(paramTransitionListener);
  }
  
  public void setAnimateParentHierarchy(boolean paramBoolean)
  {
    this.mAnimateParentHierarchy = paramBoolean;
  }
  
  public void setAnimator(int paramInt, Animator paramAnimator)
  {
    switch (paramInt)
    {
    default: 
      return;
    case 0: 
      this.mChangingAppearingAnim = paramAnimator;
      return;
    case 1: 
      this.mChangingDisappearingAnim = paramAnimator;
      return;
    case 4: 
      this.mChangingAnim = paramAnimator;
      return;
    case 2: 
      this.mAppearingAnim = paramAnimator;
      return;
    }
    this.mDisappearingAnim = paramAnimator;
  }
  
  public void setDuration(int paramInt, long paramLong)
  {
    switch (paramInt)
    {
    default: 
      return;
    case 0: 
      this.mChangingAppearingDuration = paramLong;
      return;
    case 1: 
      this.mChangingDisappearingDuration = paramLong;
      return;
    case 4: 
      this.mChangingDuration = paramLong;
      return;
    case 2: 
      this.mAppearingDuration = paramLong;
      return;
    }
    this.mDisappearingDuration = paramLong;
  }
  
  public void setDuration(long paramLong)
  {
    this.mChangingAppearingDuration = paramLong;
    this.mChangingDisappearingDuration = paramLong;
    this.mChangingDuration = paramLong;
    this.mAppearingDuration = paramLong;
    this.mDisappearingDuration = paramLong;
  }
  
  public void setInterpolator(int paramInt, TimeInterpolator paramTimeInterpolator)
  {
    switch (paramInt)
    {
    default: 
      return;
    case 0: 
      this.mChangingAppearingInterpolator = paramTimeInterpolator;
      return;
    case 1: 
      this.mChangingDisappearingInterpolator = paramTimeInterpolator;
      return;
    case 4: 
      this.mChangingInterpolator = paramTimeInterpolator;
      return;
    case 2: 
      this.mAppearingInterpolator = paramTimeInterpolator;
      return;
    }
    this.mDisappearingInterpolator = paramTimeInterpolator;
  }
  
  public void setStagger(int paramInt, long paramLong)
  {
    switch (paramInt)
    {
    case 2: 
    case 3: 
    default: 
      return;
    case 0: 
      this.mChangingAppearingStagger = paramLong;
      return;
    case 1: 
      this.mChangingDisappearingStagger = paramLong;
      return;
    }
    this.mChangingStagger = paramLong;
  }
  
  public void setStartDelay(int paramInt, long paramLong)
  {
    switch (paramInt)
    {
    default: 
      return;
    case 0: 
      this.mChangingAppearingDelay = paramLong;
      return;
    case 1: 
      this.mChangingDisappearingDelay = paramLong;
      return;
    case 4: 
      this.mChangingDelay = paramLong;
      return;
    case 2: 
      this.mAppearingDelay = paramLong;
      return;
    }
    this.mDisappearingDelay = paramLong;
  }
  
  @Deprecated
  public void showChild(ViewGroup paramViewGroup, View paramView)
  {
    addChild(paramViewGroup, paramView, true);
  }
  
  public void showChild(ViewGroup paramViewGroup, View paramView, int paramInt)
  {
    if (paramInt == 8) {}
    for (boolean bool = true;; bool = false)
    {
      addChild(paramViewGroup, paramView, bool);
      return;
    }
  }
  
  public void startChangingAnimations()
  {
    Iterator localIterator = ((LinkedHashMap)this.currentChangingAnimations.clone()).values().iterator();
    while (localIterator.hasNext())
    {
      Animator localAnimator = (Animator)localIterator.next();
      if ((localAnimator instanceof ObjectAnimator)) {
        ((ObjectAnimator)localAnimator).setCurrentPlayTime(0L);
      }
      localAnimator.start();
    }
  }
  
  private static final class CleanupCallback
    implements ViewTreeObserver.OnPreDrawListener, View.OnAttachStateChangeListener
  {
    final Map<View, View.OnLayoutChangeListener> layoutChangeListenerMap;
    final ViewGroup parent;
    
    CleanupCallback(Map<View, View.OnLayoutChangeListener> paramMap, ViewGroup paramViewGroup)
    {
      this.layoutChangeListenerMap = paramMap;
      this.parent = paramViewGroup;
    }
    
    private void cleanup()
    {
      this.parent.getViewTreeObserver().removeOnPreDrawListener(this);
      this.parent.removeOnAttachStateChangeListener(this);
      if (this.layoutChangeListenerMap.size() > 0)
      {
        Iterator localIterator = this.layoutChangeListenerMap.keySet().iterator();
        while (localIterator.hasNext())
        {
          View localView = (View)localIterator.next();
          localView.removeOnLayoutChangeListener((View.OnLayoutChangeListener)this.layoutChangeListenerMap.get(localView));
        }
        this.layoutChangeListenerMap.clear();
      }
    }
    
    public boolean onPreDraw()
    {
      cleanup();
      return true;
    }
    
    public void onViewAttachedToWindow(View paramView) {}
    
    public void onViewDetachedFromWindow(View paramView)
    {
      cleanup();
    }
  }
  
  public static abstract interface TransitionListener
  {
    public abstract void endTransition(LayoutTransition paramLayoutTransition, ViewGroup paramViewGroup, View paramView, int paramInt);
    
    public abstract void startTransition(LayoutTransition paramLayoutTransition, ViewGroup paramViewGroup, View paramView, int paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/animation/LayoutTransition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */