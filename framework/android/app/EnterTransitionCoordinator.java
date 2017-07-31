package android.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.Transition.TransitionListenerAdapter;
import android.transition.TransitionManager;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class EnterTransitionCoordinator
  extends ActivityTransitionCoordinator
{
  private static final int MIN_ANIMATION_FRAMES = 2;
  private static final String TAG = "EnterTransitionCoordinator";
  private Activity mActivity;
  private boolean mAreViewsReady;
  private ObjectAnimator mBackgroundAnimator;
  private Transition mEnterViewsTransition;
  private boolean mHasStopped;
  private boolean mIsCanceled;
  private final boolean mIsCrossTask;
  private boolean mIsExitTransitionComplete;
  private boolean mIsReadyForTransition;
  private boolean mIsViewsTransitionStarted;
  private boolean mSharedElementTransitionStarted;
  private Bundle mSharedElementsBundle;
  private ViewTreeObserver.OnPreDrawListener mViewsReadyListener;
  private boolean mWasOpaque;
  
  public EnterTransitionCoordinator(final Activity paramActivity, ResultReceiver paramResultReceiver, ArrayList<String> paramArrayList, boolean paramBoolean1, boolean paramBoolean2) {}
  
  private boolean allowOverlappingTransitions()
  {
    if (this.mIsReturning) {
      return getWindow().getAllowReturnTransitionOverlap();
    }
    return getWindow().getAllowEnterTransitionOverlap();
  }
  
  private Transition beginTransition(ViewGroup paramViewGroup, boolean paramBoolean1, boolean paramBoolean2)
  {
    Object localObject2 = null;
    Object localObject1 = null;
    Transition localTransition;
    if (paramBoolean2)
    {
      if (!this.mSharedElementNames.isEmpty()) {
        localObject1 = configureTransition(getSharedElementTransition(), false);
      }
      if (localObject1 == null)
      {
        sharedElementTransitionStarted();
        sharedElementTransitionComplete();
        localObject2 = localObject1;
      }
    }
    else
    {
      localObject1 = null;
      localTransition = null;
      if (paramBoolean1)
      {
        this.mIsViewsTransitionStarted = true;
        localObject1 = localTransition;
        if (this.mTransitioningViews != null)
        {
          if (!this.mTransitioningViews.isEmpty()) {
            break label179;
          }
          localObject1 = localTransition;
        }
        label88:
        if (localObject1 != null) {
          break label221;
        }
        viewsTransitionComplete();
      }
    }
    for (;;)
    {
      localObject1 = mergeTransitions((Transition)localObject2, (Transition)localObject1);
      if (localObject1 == null) {
        break label243;
      }
      ((Transition)localObject1).addListener(new ActivityTransitionCoordinator.ContinueTransitionListener(this));
      if (paramBoolean1) {
        setTransitioningViewsVisiblity(4, false);
      }
      TransitionManager.beginDelayedTransition(paramViewGroup, (Transition)localObject1);
      if (paramBoolean1) {
        setTransitioningViewsVisiblity(0, false);
      }
      paramViewGroup.invalidate();
      return (Transition)localObject1;
      ((Transition)localObject1).addListener(new Transition.TransitionListenerAdapter()
      {
        public void onTransitionEnd(Transition paramAnonymousTransition)
        {
          paramAnonymousTransition.removeListener(this);
          EnterTransitionCoordinator.this.sharedElementTransitionComplete();
        }
        
        public void onTransitionStart(Transition paramAnonymousTransition)
        {
          EnterTransitionCoordinator.-wrap2(EnterTransitionCoordinator.this);
        }
      });
      localObject2 = localObject1;
      break;
      label179:
      localTransition = configureTransition(getViewsTransition(), true);
      localObject1 = localTransition;
      if (localTransition == null) {
        break label88;
      }
      localObject1 = localTransition;
      if (this.mIsReturning) {
        break label88;
      }
      stripOffscreenViews();
      localObject1 = localTransition;
      break label88;
      label221:
      ((Transition)localObject1).addListener(new ActivityTransitionCoordinator.ContinueTransitionListener(this)
      {
        public void onTransitionEnd(Transition paramAnonymousTransition)
        {
          EnterTransitionCoordinator.-set0(EnterTransitionCoordinator.this, null);
          paramAnonymousTransition.removeListener(this);
          EnterTransitionCoordinator.this.viewsTransitionComplete();
          super.onTransitionEnd(paramAnonymousTransition);
        }
        
        public void onTransitionStart(Transition paramAnonymousTransition)
        {
          EnterTransitionCoordinator.-set0(EnterTransitionCoordinator.this, paramAnonymousTransition);
          if (this.val$transitioningViews != null) {
            EnterTransitionCoordinator.this.showViews(this.val$transitioningViews, false);
          }
          super.onTransitionStart(paramAnonymousTransition);
        }
      });
    }
    label243:
    transitionStarted();
    return (Transition)localObject1;
  }
  
  private void cancel()
  {
    if (!this.mIsCanceled)
    {
      this.mIsCanceled = true;
      if ((getViewsTransition() != null) && (!this.mIsViewsTransitionStarted)) {
        break label70;
      }
      showViews(this.mSharedElements, true);
    }
    for (;;)
    {
      moveSharedElementsFromOverlay();
      this.mSharedElementNames.clear();
      this.mSharedElements.clear();
      this.mAllSharedElementNames.clear();
      startSharedElementTransition(null);
      onRemoteExitTransitionComplete();
      return;
      label70:
      if (this.mTransitioningViews != null) {
        this.mTransitioningViews.addAll(this.mSharedElements);
      }
    }
  }
  
  private static SharedElementCallback getListener(Activity paramActivity, boolean paramBoolean)
  {
    if (paramBoolean) {
      return paramActivity.mExitTransitionListener;
    }
    return paramActivity.mEnterTransitionListener;
  }
  
  private void makeOpaque()
  {
    if ((!this.mHasStopped) && (this.mActivity != null))
    {
      if (this.mWasOpaque) {
        this.mActivity.convertFromTranslucent();
      }
      this.mActivity = null;
    }
  }
  
  private ArrayMap<String, View> mapNamedElements(ArrayList<String> paramArrayList1, ArrayList<String> paramArrayList2)
  {
    ArrayMap localArrayMap = new ArrayMap();
    Object localObject1 = getDecor();
    if (localObject1 != null) {
      ((ViewGroup)localObject1).findNamedViews(localArrayMap);
    }
    if (paramArrayList1 != null)
    {
      int i = 0;
      if (i < paramArrayList2.size())
      {
        Object localObject2 = (String)paramArrayList2.get(i);
        localObject1 = (String)paramArrayList1.get(i);
        if ((localObject2 == null) || (((String)localObject2).equals(localObject1))) {}
        for (;;)
        {
          i += 1;
          break;
          localObject2 = (View)localArrayMap.remove(localObject2);
          if (localObject2 != null) {
            localArrayMap.put(localObject1, localObject2);
          }
        }
      }
    }
    return localArrayMap;
  }
  
  private void onTakeSharedElements()
  {
    if ((!this.mIsReadyForTransition) || (this.mSharedElementsBundle == null)) {
      return;
    }
    final Object localObject = this.mSharedElementsBundle;
    this.mSharedElementsBundle = null;
    localObject = new SharedElementCallback.OnSharedElementsReadyListener()
    {
      public void onSharedElementsReady()
      {
        final ViewGroup localViewGroup = EnterTransitionCoordinator.this.getDecor();
        if (localViewGroup != null)
        {
          localViewGroup.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
          {
            public boolean onPreDraw()
            {
              localViewGroup.getViewTreeObserver().removeOnPreDrawListener(this);
              EnterTransitionCoordinator.this.startTransition(new Runnable()
              {
                public void run()
                {
                  EnterTransitionCoordinator.-wrap4(EnterTransitionCoordinator.this, this.val$sharedElementState);
                }
              });
              return false;
            }
          });
          localViewGroup.invalidate();
        }
      }
    };
    if (this.mListener == null)
    {
      ((SharedElementCallback.OnSharedElementsReadyListener)localObject).onSharedElementsReady();
      return;
    }
    this.mListener.onSharedElementsArrived(this.mSharedElementNames, this.mSharedElements, (SharedElementCallback.OnSharedElementsReadyListener)localObject);
  }
  
  private static void removeNullViews(ArrayList<View> paramArrayList)
  {
    if (paramArrayList != null)
    {
      int i = paramArrayList.size() - 1;
      while (i >= 0)
      {
        if (paramArrayList.get(i) == null) {
          paramArrayList.remove(i);
        }
        i -= 1;
      }
    }
  }
  
  private void requestLayoutForSharedElements()
  {
    int j = this.mSharedElements.size();
    int i = 0;
    while (i < j)
    {
      ((View)this.mSharedElements.get(i)).requestLayout();
      i += 1;
    }
  }
  
  private void sendSharedElementDestination()
  {
    final Object localObject = getDecor();
    int i;
    if ((allowOverlappingTransitions()) && (getEnterViewsTransition() != null))
    {
      i = 0;
      if (i == 0) {
        break label134;
      }
      localObject = captureSharedElementState();
      moveSharedElementsToOverlay();
      this.mResultReceiver.send(107, (Bundle)localObject);
    }
    for (;;)
    {
      if (allowOverlappingTransitions()) {
        startEnterTransitionOnly();
      }
      return;
      if (localObject == null)
      {
        i = 1;
        break;
      }
      int j;
      label79:
      int k;
      if (((View)localObject).isLayoutRequested())
      {
        j = 0;
        i = j;
        if (j == 0) {
          break;
        }
        k = 0;
      }
      for (;;)
      {
        i = j;
        if (k >= this.mSharedElements.size()) {
          break;
        }
        if (((View)this.mSharedElements.get(k)).isLayoutRequested())
        {
          i = 0;
          break;
          j = 1;
          break label79;
        }
        k += 1;
      }
      label134:
      if (localObject != null) {
        ((View)localObject).getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
        {
          public boolean onPreDraw()
          {
            localObject.getViewTreeObserver().removeOnPreDrawListener(this);
            if (EnterTransitionCoordinator.this.mResultReceiver != null)
            {
              Bundle localBundle = EnterTransitionCoordinator.this.captureSharedElementState();
              EnterTransitionCoordinator.this.moveSharedElementsToOverlay();
              EnterTransitionCoordinator.this.mResultReceiver.send(107, localBundle);
            }
            return true;
          }
        });
      }
    }
  }
  
  private void sharedElementTransitionStarted()
  {
    this.mSharedElementTransitionStarted = true;
    if (this.mIsExitTransitionComplete) {
      send(104, null);
    }
  }
  
  private void startEnterTransition(Transition paramTransition)
  {
    Object localObject = getDecor();
    if ((!this.mIsReturning) && (localObject != null))
    {
      localObject = ((ViewGroup)localObject).getBackground();
      if (localObject != null)
      {
        paramTransition = ((Drawable)localObject).mutate();
        getWindow().setBackgroundDrawable(paramTransition);
        this.mBackgroundAnimator = ObjectAnimator.ofInt(paramTransition, "alpha", new int[] { 255 });
        this.mBackgroundAnimator.setDuration(getFadeDuration());
        this.mBackgroundAnimator.addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            EnterTransitionCoordinator.-wrap1(EnterTransitionCoordinator.this);
          }
        });
        this.mBackgroundAnimator.start();
      }
    }
    else
    {
      return;
    }
    if (paramTransition != null)
    {
      paramTransition.addListener(new Transition.TransitionListenerAdapter()
      {
        public void onTransitionEnd(Transition paramAnonymousTransition)
        {
          paramAnonymousTransition.removeListener(this);
          EnterTransitionCoordinator.-wrap1(EnterTransitionCoordinator.this);
        }
      });
      return;
    }
    makeOpaque();
  }
  
  private void startEnterTransitionOnly()
  {
    startTransition(new Runnable()
    {
      public void run()
      {
        Object localObject = EnterTransitionCoordinator.this.getDecor();
        if (localObject != null)
        {
          localObject = EnterTransitionCoordinator.-wrap0(EnterTransitionCoordinator.this, (ViewGroup)localObject, true, false);
          EnterTransitionCoordinator.-wrap3(EnterTransitionCoordinator.this, (Transition)localObject);
        }
      }
    });
  }
  
  private void startRejectedAnimations(final ArrayList<View> paramArrayList)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty())) {
      return;
    }
    final ViewGroup localViewGroup = getDecor();
    if (localViewGroup != null)
    {
      ViewGroupOverlay localViewGroupOverlay = localViewGroup.getOverlay();
      Object localObject = null;
      int j = paramArrayList.size();
      int i = 0;
      while (i < j)
      {
        localObject = (View)paramArrayList.get(i);
        localViewGroupOverlay.add((View)localObject);
        localObject = ObjectAnimator.ofFloat(localObject, View.ALPHA, new float[] { 1.0F, 0.0F });
        ((ObjectAnimator)localObject).start();
        i += 1;
      }
      ((ObjectAnimator)localObject).addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          paramAnonymousAnimator = localViewGroup.getOverlay();
          int j = paramArrayList.size();
          int i = 0;
          while (i < j)
          {
            paramAnonymousAnimator.remove((View)paramArrayList.get(i));
            i += 1;
          }
        }
      });
    }
  }
  
  private void startSharedElementTransition(Bundle paramBundle)
  {
    ViewGroup localViewGroup = getDecor();
    if (localViewGroup == null) {
      return;
    }
    Object localObject = new ArrayList(this.mAllSharedElementNames);
    ((ArrayList)localObject).removeAll(this.mSharedElementNames);
    localObject = createSnapshots(paramBundle, (Collection)localObject);
    if (this.mListener != null) {
      this.mListener.onRejectSharedElements((List)localObject);
    }
    removeNullViews((ArrayList)localObject);
    startRejectedAnimations((ArrayList)localObject);
    localObject = createSnapshots(paramBundle, this.mSharedElementNames);
    showViews(this.mSharedElements, true);
    scheduleSetSharedElementEnd((ArrayList)localObject);
    paramBundle = setSharedElementState(paramBundle, (ArrayList)localObject);
    requestLayoutForSharedElements();
    if ((!allowOverlappingTransitions()) || (this.mIsReturning)) {}
    for (boolean bool = false;; bool = true)
    {
      setGhostVisibility(4);
      scheduleGhostVisibilityChange(4);
      pauseInput();
      localObject = beginTransition(localViewGroup, bool, true);
      scheduleGhostVisibilityChange(0);
      setGhostVisibility(0);
      if (bool) {
        startEnterTransition((Transition)localObject);
      }
      setOriginalSharedElementState(this.mSharedElements, paramBundle);
      if (this.mResultReceiver != null) {
        localViewGroup.postOnAnimation(new Runnable()
        {
          int mAnimations;
          
          public void run()
          {
            int i = this.mAnimations;
            this.mAnimations = (i + 1);
            if (i < 2)
            {
              localViewGroup = EnterTransitionCoordinator.this.getDecor();
              if (localViewGroup != null) {
                localViewGroup.postOnAnimation(this);
              }
            }
            while (EnterTransitionCoordinator.this.mResultReceiver == null)
            {
              ViewGroup localViewGroup;
              return;
            }
            EnterTransitionCoordinator.this.mResultReceiver.send(101, null);
            EnterTransitionCoordinator.this.mResultReceiver = null;
          }
        });
      }
      return;
    }
  }
  
  private void triggerViewsReady(final ArrayMap<String, View> paramArrayMap)
  {
    if (this.mAreViewsReady) {
      return;
    }
    this.mAreViewsReady = true;
    final ViewGroup localViewGroup = getDecor();
    if ((localViewGroup != null) && ((!localViewGroup.isAttachedToWindow()) || ((!paramArrayMap.isEmpty()) && (((View)paramArrayMap.valueAt(0)).isLayoutRequested()))))
    {
      this.mViewsReadyListener = new ViewTreeObserver.OnPreDrawListener()
      {
        public boolean onPreDraw()
        {
          EnterTransitionCoordinator.-set1(EnterTransitionCoordinator.this, null);
          localViewGroup.getViewTreeObserver().removeOnPreDrawListener(this);
          EnterTransitionCoordinator.this.viewsReady(paramArrayMap);
          return true;
        }
      };
      localViewGroup.getViewTreeObserver().addOnPreDrawListener(this.mViewsReadyListener);
      localViewGroup.invalidate();
      return;
    }
    viewsReady(paramArrayMap);
  }
  
  public boolean cancelEnter()
  {
    setGhostVisibility(4);
    this.mHasStopped = true;
    this.mIsCanceled = true;
    clearState();
    return super.cancelPendingTransitions();
  }
  
  protected void clearState()
  {
    this.mSharedElementsBundle = null;
    this.mEnterViewsTransition = null;
    this.mResultReceiver = null;
    if (this.mBackgroundAnimator != null)
    {
      this.mBackgroundAnimator.cancel();
      this.mBackgroundAnimator = null;
    }
    super.clearState();
  }
  
  public void forceViewsToAppear()
  {
    if (!this.mIsReturning) {
      return;
    }
    if (!this.mIsReadyForTransition)
    {
      this.mIsReadyForTransition = true;
      ViewGroup localViewGroup = getDecor();
      if ((localViewGroup != null) && (this.mViewsReadyListener != null))
      {
        localViewGroup.getViewTreeObserver().removeOnPreDrawListener(this.mViewsReadyListener);
        this.mViewsReadyListener = null;
      }
      showViews(this.mTransitioningViews, true);
      setTransitioningViewsVisiblity(0, true);
      this.mSharedElements.clear();
      this.mAllSharedElementNames.clear();
      this.mTransitioningViews.clear();
      this.mIsReadyForTransition = true;
      viewsTransitionComplete();
      sharedElementTransitionComplete();
    }
    for (;;)
    {
      this.mAreViewsReady = true;
      if (this.mResultReceiver != null)
      {
        this.mResultReceiver.send(106, null);
        this.mResultReceiver = null;
      }
      return;
      if (!this.mSharedElementTransitionStarted)
      {
        moveSharedElementsFromOverlay();
        this.mSharedElementTransitionStarted = true;
        showViews(this.mSharedElements, true);
        this.mSharedElements.clear();
        sharedElementTransitionComplete();
      }
      if (!this.mIsViewsTransitionStarted)
      {
        this.mIsViewsTransitionStarted = true;
        showViews(this.mTransitioningViews, true);
        setTransitioningViewsVisiblity(0, true);
        this.mTransitioningViews.clear();
        viewsTransitionComplete();
      }
      cancelPendingTransitions();
    }
  }
  
  public Transition getEnterViewsTransition()
  {
    return this.mEnterViewsTransition;
  }
  
  protected Transition getSharedElementTransition()
  {
    Window localWindow = getWindow();
    if (localWindow == null) {
      return null;
    }
    if (this.mIsReturning) {
      return localWindow.getSharedElementReenterTransition();
    }
    return localWindow.getSharedElementEnterTransition();
  }
  
  protected Transition getViewsTransition()
  {
    Window localWindow = getWindow();
    if (localWindow == null) {
      return null;
    }
    if (this.mIsReturning) {
      return localWindow.getReenterTransition();
    }
    return localWindow.getEnterTransition();
  }
  
  boolean isCrossTask()
  {
    return this.mIsCrossTask;
  }
  
  public boolean isReturning()
  {
    return this.mIsReturning;
  }
  
  public boolean isWaitingForRemoteExit()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mIsReturning)
    {
      bool1 = bool2;
      if (this.mResultReceiver != null) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public void namedViewsReady(ArrayList<String> paramArrayList1, ArrayList<String> paramArrayList2)
  {
    triggerViewsReady(mapNamedElements(paramArrayList1, paramArrayList2));
  }
  
  protected void onReceiveResult(int paramInt, Bundle paramBundle)
  {
    switch (paramInt)
    {
    case 105: 
    default: 
    case 103: 
    case 104: 
      do
      {
        do
        {
          do
          {
            return;
          } while (this.mIsCanceled);
          this.mSharedElementsBundle = paramBundle;
          onTakeSharedElements();
          return;
        } while (this.mIsCanceled);
        this.mIsExitTransitionComplete = true;
      } while (!this.mSharedElementTransitionStarted);
      onRemoteExitTransitionComplete();
      return;
    }
    cancel();
  }
  
  protected void onRemoteExitTransitionComplete()
  {
    if (!allowOverlappingTransitions()) {
      startEnterTransitionOnly();
    }
  }
  
  protected void onTransitionsComplete()
  {
    moveSharedElementsFromOverlay();
    ViewGroup localViewGroup = getDecor();
    if (localViewGroup != null) {
      localViewGroup.sendAccessibilityEvent(2048);
    }
  }
  
  protected void prepareEnter()
  {
    Object localObject = getDecor();
    if ((this.mActivity == null) || (localObject == null)) {
      return;
    }
    if (!isCrossTask()) {
      this.mActivity.overridePendingTransition(0, 0);
    }
    if (!this.mIsReturning)
    {
      this.mWasOpaque = this.mActivity.convertToTranslucent(null, null);
      localObject = ((ViewGroup)localObject).getBackground();
      if (localObject != null)
      {
        getWindow().setBackgroundDrawable(null);
        localObject = ((Drawable)localObject).mutate();
        ((Drawable)localObject).setAlpha(0);
        getWindow().setBackgroundDrawable((Drawable)localObject);
      }
      return;
    }
    this.mActivity = null;
  }
  
  public void stop()
  {
    if (this.mBackgroundAnimator != null)
    {
      this.mBackgroundAnimator.end();
      this.mBackgroundAnimator = null;
    }
    for (;;)
    {
      makeOpaque();
      this.mIsCanceled = true;
      this.mResultReceiver = null;
      this.mActivity = null;
      moveSharedElementsFromOverlay();
      if (this.mTransitioningViews != null)
      {
        showViews(this.mTransitioningViews, true);
        setTransitioningViewsVisiblity(0, true);
      }
      showViews(this.mSharedElements, true);
      clearState();
      return;
      if (this.mWasOpaque)
      {
        Object localObject = getDecor();
        if (localObject != null)
        {
          localObject = ((ViewGroup)localObject).getBackground();
          if (localObject != null) {
            ((Drawable)localObject).setAlpha(1);
          }
        }
      }
    }
  }
  
  public void viewInstancesReady(ArrayList<String> paramArrayList1, ArrayList<String> paramArrayList2, ArrayList<View> paramArrayList)
  {
    int k = 0;
    int i = 0;
    for (;;)
    {
      j = k;
      if (i >= paramArrayList.size()) {
        break label70;
      }
      View localView = (View)paramArrayList.get(i);
      if ((!TextUtils.equals(localView.getTransitionName(), (CharSequence)paramArrayList2.get(i))) || (!localView.isAttachedToWindow())) {
        break;
      }
      i += 1;
    }
    int j = 1;
    label70:
    if (j != 0)
    {
      triggerViewsReady(mapNamedElements(paramArrayList1, paramArrayList2));
      return;
    }
    triggerViewsReady(mapSharedElements(paramArrayList1, paramArrayList));
  }
  
  protected void viewsReady(ArrayMap<String, View> paramArrayMap)
  {
    super.viewsReady(paramArrayMap);
    this.mIsReadyForTransition = true;
    hideViews(this.mSharedElements);
    if ((getViewsTransition() != null) && (this.mTransitioningViews != null)) {
      hideViews(this.mTransitioningViews);
    }
    if (this.mIsReturning) {
      sendSharedElementDestination();
    }
    for (;;)
    {
      if (this.mSharedElementsBundle != null) {
        onTakeSharedElements();
      }
      return;
      moveSharedElementsToOverlay();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/EnterTransitionCoordinator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */