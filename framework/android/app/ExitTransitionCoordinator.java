package android.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.transition.Transition;
import android.transition.Transition.TransitionListenerAdapter;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import java.util.ArrayList;

class ExitTransitionCoordinator
  extends ActivityTransitionCoordinator
{
  private static final long MAX_WAIT_MS = 1000L;
  private static final String TAG = "ExitTransitionCoordinator";
  private Activity mActivity;
  private ObjectAnimator mBackgroundAnimator;
  private boolean mExitNotified;
  private Bundle mExitSharedElementBundle;
  private Handler mHandler;
  private HideSharedElementsCallback mHideSharedElementsCallback;
  private boolean mIsBackgroundReady;
  private boolean mIsCanceled;
  private boolean mIsExitStarted;
  private boolean mIsHidden;
  private Bundle mSharedElementBundle;
  private boolean mSharedElementNotified;
  private boolean mSharedElementsHidden;
  
  public ExitTransitionCoordinator(Activity paramActivity, Window paramWindow, SharedElementCallback paramSharedElementCallback, ArrayList<String> paramArrayList1, ArrayList<String> paramArrayList2, ArrayList<View> paramArrayList, boolean paramBoolean)
  {
    super(paramWindow, paramArrayList1, paramSharedElementCallback, paramBoolean);
    viewsReady(mapSharedElements(paramArrayList2, paramArrayList));
    stripOffscreenViews();
    if (paramBoolean) {}
    for (paramBoolean = false;; paramBoolean = true)
    {
      this.mIsBackgroundReady = paramBoolean;
      this.mActivity = paramActivity;
      return;
    }
  }
  
  private void beginTransitions()
  {
    Transition localTransition2 = getSharedElementExitTransition();
    Transition localTransition1 = getExitTransition();
    localTransition2 = mergeTransitions(localTransition2, localTransition1);
    ViewGroup localViewGroup = getDecor();
    if ((localTransition2 != null) && (localViewGroup != null))
    {
      setGhostVisibility(4);
      scheduleGhostVisibilityChange(4);
      if (localTransition1 != null) {
        setTransitioningViewsVisiblity(0, false);
      }
      TransitionManager.beginDelayedTransition(localViewGroup, localTransition2);
      scheduleGhostVisibilityChange(0);
      setGhostVisibility(0);
      if (localTransition1 != null) {
        setTransitioningViewsVisiblity(4, false);
      }
      localViewGroup.invalidate();
      return;
    }
    transitionStarted();
  }
  
  private Bundle captureExitSharedElementsState()
  {
    Bundle localBundle1 = new Bundle();
    RectF localRectF = new RectF();
    Matrix localMatrix = new Matrix();
    int i = 0;
    if (i < this.mSharedElements.size())
    {
      String str = (String)this.mSharedElementNames.get(i);
      Bundle localBundle2 = this.mExitSharedElementBundle.getBundle(str);
      if (localBundle2 != null) {
        localBundle1.putBundle(str, localBundle2);
      }
      for (;;)
      {
        i += 1;
        break;
        captureSharedElementState((View)this.mSharedElements.get(i), str, localBundle1, localMatrix, localRectF);
      }
    }
    return localBundle1;
  }
  
  private void delayCancel()
  {
    if (this.mHandler != null) {
      this.mHandler.sendEmptyMessageDelayed(106, 1000L);
    }
  }
  
  private void fadeOutBackground()
  {
    if (this.mBackgroundAnimator == null)
    {
      Object localObject = getDecor();
      if (localObject != null)
      {
        localObject = ((ViewGroup)localObject).getBackground();
        if (localObject != null)
        {
          localObject = ((Drawable)localObject).mutate();
          getWindow().setBackgroundDrawable((Drawable)localObject);
          this.mBackgroundAnimator = ObjectAnimator.ofInt(localObject, "alpha", new int[] { 0 });
          this.mBackgroundAnimator.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
              ExitTransitionCoordinator.-set0(ExitTransitionCoordinator.this, null);
              if (!ExitTransitionCoordinator.-get2(ExitTransitionCoordinator.this))
              {
                ExitTransitionCoordinator.-set1(ExitTransitionCoordinator.this, true);
                ExitTransitionCoordinator.this.notifyComplete();
              }
            }
          });
          this.mBackgroundAnimator.setDuration(getFadeDuration());
          this.mBackgroundAnimator.start();
        }
      }
    }
    else
    {
      return;
    }
    this.mIsBackgroundReady = true;
  }
  
  private void finish()
  {
    stopCancel();
    if (this.mActivity != null)
    {
      this.mActivity.mActivityTransitionState.clear();
      this.mActivity.finish();
      this.mActivity.overridePendingTransition(0, 0);
      this.mActivity = null;
    }
    clearState();
  }
  
  private void finishIfNecessary()
  {
    if ((this.mIsReturning) && (this.mExitNotified) && (this.mActivity != null) && ((this.mSharedElements.isEmpty()) || (this.mSharedElementsHidden))) {
      finish();
    }
    if ((!this.mIsReturning) && (this.mExitNotified)) {
      this.mActivity = null;
    }
  }
  
  private Transition getExitTransition()
  {
    Object localObject2 = null;
    Object localObject1 = localObject2;
    if (this.mTransitioningViews != null) {
      if (!this.mTransitioningViews.isEmpty()) {
        break label33;
      }
    }
    label33:
    for (localObject1 = localObject2; localObject1 == null; localObject1 = configureTransition(getViewsTransition(), true))
    {
      viewsTransitionComplete();
      return (Transition)localObject1;
    }
    ((Transition)localObject1).addListener(new ActivityTransitionCoordinator.ContinueTransitionListener(this)
    {
      public void onTransitionEnd(Transition paramAnonymousTransition)
      {
        paramAnonymousTransition.removeListener(this);
        ExitTransitionCoordinator.this.viewsTransitionComplete();
        if ((ExitTransitionCoordinator.-get3(ExitTransitionCoordinator.this)) && (this.val$transitioningViews != null))
        {
          ExitTransitionCoordinator.this.showViews(this.val$transitioningViews, true);
          ExitTransitionCoordinator.this.setTransitioningViewsVisiblity(0, true);
        }
        if (ExitTransitionCoordinator.-get4(ExitTransitionCoordinator.this) != null) {
          ExitTransitionCoordinator.-wrap1(ExitTransitionCoordinator.this);
        }
        super.onTransitionEnd(paramAnonymousTransition);
      }
    });
    return (Transition)localObject1;
  }
  
  private Transition getSharedElementExitTransition()
  {
    Transition localTransition = null;
    if (!this.mSharedElements.isEmpty()) {
      localTransition = configureTransition(getSharedElementTransition(), false);
    }
    if (localTransition == null)
    {
      sharedElementTransitionComplete();
      return localTransition;
    }
    localTransition.addListener(new ActivityTransitionCoordinator.ContinueTransitionListener(this)
    {
      public void onTransitionEnd(Transition paramAnonymousTransition)
      {
        paramAnonymousTransition.removeListener(this);
        ExitTransitionCoordinator.this.sharedElementTransitionComplete();
        if (ExitTransitionCoordinator.-get3(ExitTransitionCoordinator.this)) {
          ExitTransitionCoordinator.this.showViews(ExitTransitionCoordinator.this.mSharedElements, true);
        }
      }
    });
    ((View)this.mSharedElements.get(0)).invalidate();
    return localTransition;
  }
  
  private void hideSharedElements()
  {
    moveSharedElementsFromOverlay();
    if (this.mHideSharedElementsCallback != null) {
      this.mHideSharedElementsCallback.hideSharedElements();
    }
    if (!this.mIsHidden) {
      hideViews(this.mSharedElements);
    }
    this.mSharedElementsHidden = true;
    finishIfNecessary();
  }
  
  private void notifyExitComplete()
  {
    if ((!this.mExitNotified) && (isViewsTransitionComplete()))
    {
      this.mExitNotified = true;
      this.mResultReceiver.send(104, null);
      this.mResultReceiver = null;
      ViewGroup localViewGroup = getDecor();
      if ((!this.mIsReturning) && (localViewGroup != null)) {
        localViewGroup.suppressLayout(false);
      }
      finishIfNecessary();
    }
  }
  
  private void sharedElementExitBack()
  {
    final ViewGroup localViewGroup = getDecor();
    if (localViewGroup != null) {
      localViewGroup.suppressLayout(true);
    }
    if ((localViewGroup == null) || (this.mExitSharedElementBundle == null) || (this.mExitSharedElementBundle.isEmpty())) {}
    while ((this.mSharedElements.isEmpty()) || (getSharedElementTransition() == null))
    {
      sharedElementTransitionComplete();
      return;
    }
    startTransition(new Runnable()
    {
      public void run()
      {
        ExitTransitionCoordinator.-wrap6(ExitTransitionCoordinator.this, localViewGroup);
      }
    });
  }
  
  private void startExitTransition()
  {
    Transition localTransition = getExitTransition();
    ViewGroup localViewGroup = getDecor();
    if ((localTransition != null) && (localViewGroup != null) && (this.mTransitioningViews != null))
    {
      setTransitioningViewsVisiblity(0, false);
      TransitionManager.beginDelayedTransition(localViewGroup, localTransition);
      setTransitioningViewsVisiblity(4, false);
      localViewGroup.invalidate();
      return;
    }
    transitionStarted();
  }
  
  private void startSharedElementExit(final ViewGroup paramViewGroup)
  {
    Transition localTransition = getSharedElementExitTransition();
    localTransition.addListener(new Transition.TransitionListenerAdapter()
    {
      public void onTransitionEnd(Transition paramAnonymousTransition)
      {
        paramAnonymousTransition.removeListener(this);
        if (ExitTransitionCoordinator.this.isViewsTransitionComplete()) {
          ExitTransitionCoordinator.-wrap1(ExitTransitionCoordinator.this);
        }
      }
    });
    final ArrayList localArrayList = createSnapshots(this.mExitSharedElementBundle, this.mSharedElementNames);
    paramViewGroup.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
    {
      public boolean onPreDraw()
      {
        paramViewGroup.getViewTreeObserver().removeOnPreDrawListener(this);
        ExitTransitionCoordinator.this.setSharedElementState(ExitTransitionCoordinator.-get1(ExitTransitionCoordinator.this), localArrayList);
        return true;
      }
    });
    setGhostVisibility(4);
    scheduleGhostVisibilityChange(4);
    if (this.mListener != null) {
      this.mListener.onSharedElementEnd(this.mSharedElementNames, this.mSharedElements, localArrayList);
    }
    TransitionManager.beginDelayedTransition(paramViewGroup, localTransition);
    scheduleGhostVisibilityChange(0);
    setGhostVisibility(0);
    paramViewGroup.invalidate();
  }
  
  private void stopCancel()
  {
    if (this.mHandler != null) {
      this.mHandler.removeMessages(106);
    }
  }
  
  protected void clearState()
  {
    this.mHandler = null;
    this.mSharedElementBundle = null;
    if (this.mBackgroundAnimator != null)
    {
      this.mBackgroundAnimator.cancel();
      this.mBackgroundAnimator = null;
    }
    this.mExitSharedElementBundle = null;
    super.clearState();
  }
  
  protected Transition getSharedElementTransition()
  {
    if (this.mIsReturning) {
      return getWindow().getSharedElementReturnTransition();
    }
    return getWindow().getSharedElementExitTransition();
  }
  
  protected Transition getViewsTransition()
  {
    if (this.mIsReturning) {
      return getWindow().getReturnTransition();
    }
    return getWindow().getExitTransition();
  }
  
  protected boolean isReadyToNotify()
  {
    if ((this.mSharedElementBundle != null) && (this.mResultReceiver != null)) {
      return this.mIsBackgroundReady;
    }
    return false;
  }
  
  protected boolean moveSharedElementWithParent()
  {
    return !this.mIsReturning;
  }
  
  protected void notifyComplete()
  {
    if (isReadyToNotify())
    {
      if (this.mSharedElementNotified) {
        break label84;
      }
      this.mSharedElementNotified = true;
      delayCancel();
      if (this.mListener == null)
      {
        this.mResultReceiver.send(103, this.mSharedElementBundle);
        notifyExitComplete();
      }
    }
    else
    {
      return;
    }
    final ResultReceiver localResultReceiver = this.mResultReceiver;
    final Bundle localBundle = this.mSharedElementBundle;
    this.mListener.onSharedElementsArrived(this.mSharedElementNames, this.mSharedElements, new SharedElementCallback.OnSharedElementsReadyListener()
    {
      public void onSharedElementsReady()
      {
        localResultReceiver.send(103, localBundle);
        ExitTransitionCoordinator.-wrap4(ExitTransitionCoordinator.this);
      }
    });
    return;
    label84:
    notifyExitComplete();
  }
  
  protected void onReceiveResult(int paramInt, Bundle paramBundle)
  {
    switch (paramInt)
    {
    case 102: 
    case 103: 
    case 104: 
    default: 
    case 100: 
    case 101: 
      do
      {
        return;
        stopCancel();
        this.mResultReceiver = ((ResultReceiver)paramBundle.getParcelable("android:remoteReceiver"));
        if (this.mIsCanceled)
        {
          this.mResultReceiver.send(106, null);
          this.mResultReceiver = null;
          return;
        }
        notifyComplete();
        return;
        stopCancel();
      } while (this.mIsCanceled);
      hideSharedElements();
      return;
    case 105: 
      this.mHandler.removeMessages(106);
      startExit();
      return;
    case 107: 
      this.mExitSharedElementBundle = paramBundle;
      sharedElementExitBack();
      return;
    }
    this.mIsCanceled = true;
    finish();
  }
  
  protected void onTransitionsComplete()
  {
    notifyComplete();
  }
  
  public void resetViews()
  {
    if (this.mTransitioningViews != null)
    {
      showViews(this.mTransitioningViews, true);
      setTransitioningViewsVisiblity(0, true);
    }
    showViews(this.mSharedElements, true);
    this.mIsHidden = true;
    ViewGroup localViewGroup = getDecor();
    if ((!this.mIsReturning) && (localViewGroup != null)) {
      localViewGroup.suppressLayout(false);
    }
    moveSharedElementsFromOverlay();
    clearState();
  }
  
  void setHideSharedElementsCallback(HideSharedElementsCallback paramHideSharedElementsCallback)
  {
    this.mHideSharedElementsCallback = paramHideSharedElementsCallback;
  }
  
  protected void sharedElementTransitionComplete()
  {
    if (this.mExitSharedElementBundle == null) {}
    for (Bundle localBundle = captureSharedElementState();; localBundle = captureExitSharedElementsState())
    {
      this.mSharedElementBundle = localBundle;
      super.sharedElementTransitionComplete();
      return;
    }
  }
  
  public void startExit()
  {
    if (!this.mIsExitStarted)
    {
      this.mIsExitStarted = true;
      pauseInput();
      ViewGroup localViewGroup = getDecor();
      if (localViewGroup != null) {
        localViewGroup.suppressLayout(true);
      }
      moveSharedElementsToOverlay();
      startTransition(new Runnable()
      {
        public void run()
        {
          if (ExitTransitionCoordinator.-get0(ExitTransitionCoordinator.this) != null)
          {
            ExitTransitionCoordinator.-wrap0(ExitTransitionCoordinator.this);
            return;
          }
          ExitTransitionCoordinator.-wrap5(ExitTransitionCoordinator.this);
        }
      });
    }
  }
  
  public void startExit(int paramInt, Intent paramIntent)
  {
    int j = 1;
    int i;
    if (!this.mIsExitStarted)
    {
      this.mIsExitStarted = true;
      pauseInput();
      localObject = getDecor();
      if (localObject != null) {
        ((ViewGroup)localObject).suppressLayout(true);
      }
      this.mHandler = new Handler()
      {
        public void handleMessage(Message paramAnonymousMessage)
        {
          ExitTransitionCoordinator.-set2(ExitTransitionCoordinator.this, true);
          ExitTransitionCoordinator.-wrap3(ExitTransitionCoordinator.this);
        }
      };
      delayCancel();
      moveSharedElementsToOverlay();
      if ((localObject != null) && (((ViewGroup)localObject).getBackground() == null)) {
        getWindow().setBackgroundDrawable(new ColorDrawable(-16777216));
      }
      i = j;
      if (localObject != null)
      {
        if (((ViewGroup)localObject).getContext().getApplicationInfo().targetSdkVersion < 23) {
          break label166;
        }
        i = j;
      }
      if (i == 0) {
        break label171;
      }
    }
    label166:
    label171:
    for (Object localObject = this.mSharedElementNames;; localObject = this.mAllSharedElementNames)
    {
      paramIntent = ActivityOptions.makeSceneTransitionAnimation(this.mActivity, this, (ArrayList)localObject, paramInt, paramIntent);
      this.mActivity.convertToTranslucent(new Activity.TranslucentConversionListener()
      {
        public void onTranslucentConversionComplete(boolean paramAnonymousBoolean)
        {
          if (!ExitTransitionCoordinator.-get2(ExitTransitionCoordinator.this)) {
            ExitTransitionCoordinator.-wrap2(ExitTransitionCoordinator.this);
          }
        }
      }, paramIntent);
      startTransition(new Runnable()
      {
        public void run()
        {
          ExitTransitionCoordinator.-wrap5(ExitTransitionCoordinator.this);
        }
      });
      return;
      i = 0;
      break;
    }
  }
  
  public void stop()
  {
    if ((this.mIsReturning) && (this.mActivity != null))
    {
      this.mActivity.convertToTranslucent(null, null);
      finish();
    }
  }
  
  static abstract interface HideSharedElementsCallback
  {
    public abstract void hideSharedElements();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/ExitTransitionCoordinator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */