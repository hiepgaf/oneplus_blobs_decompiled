package android.app;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.transition.Transition;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

class ActivityTransitionState
{
  private static final String ENTERING_SHARED_ELEMENTS = "android:enteringSharedElements";
  private static final String EXITING_MAPPED_FROM = "android:exitingMappedFrom";
  private static final String EXITING_MAPPED_TO = "android:exitingMappedTo";
  private ExitTransitionCoordinator mCalledExitCoordinator;
  private ActivityOptions mEnterActivityOptions;
  private EnterTransitionCoordinator mEnterTransitionCoordinator;
  private ArrayList<String> mEnteringNames;
  private SparseArray<WeakReference<ExitTransitionCoordinator>> mExitTransitionCoordinators;
  private int mExitTransitionCoordinatorsKey = 1;
  private ArrayList<String> mExitingFrom;
  private ArrayList<String> mExitingTo;
  private ArrayList<View> mExitingToView;
  private boolean mHasExited;
  private boolean mIsEnterPostponed;
  private boolean mIsEnterTriggered;
  private ExitTransitionCoordinator mReturnExitCoordinator;
  
  private void restoreExitedViews()
  {
    if (this.mCalledExitCoordinator != null)
    {
      this.mCalledExitCoordinator.resetViews();
      this.mCalledExitCoordinator = null;
    }
  }
  
  private void restoreReenteringViews()
  {
    if ((this.mEnterTransitionCoordinator == null) || (!this.mEnterTransitionCoordinator.isReturning()) || (this.mEnterTransitionCoordinator.isCrossTask())) {
      return;
    }
    this.mEnterTransitionCoordinator.forceViewsToAppear();
    this.mExitingFrom = null;
    this.mExitingTo = null;
    this.mExitingToView = null;
  }
  
  private void startEnter()
  {
    if (this.mEnterTransitionCoordinator.isReturning()) {
      if (this.mExitingToView != null) {
        this.mEnterTransitionCoordinator.viewInstancesReady(this.mExitingFrom, this.mExitingTo, this.mExitingToView);
      }
    }
    for (;;)
    {
      this.mExitingFrom = null;
      this.mExitingTo = null;
      this.mExitingToView = null;
      this.mEnterActivityOptions = null;
      return;
      this.mEnterTransitionCoordinator.namedViewsReady(this.mExitingFrom, this.mExitingTo);
      continue;
      this.mEnterTransitionCoordinator.namedViewsReady(null, null);
      this.mEnteringNames = this.mEnterTransitionCoordinator.getAllSharedElementNames();
    }
  }
  
  public int addExitTransitionCoordinator(ExitTransitionCoordinator paramExitTransitionCoordinator)
  {
    if (this.mExitTransitionCoordinators == null) {
      this.mExitTransitionCoordinators = new SparseArray();
    }
    paramExitTransitionCoordinator = new WeakReference(paramExitTransitionCoordinator);
    int i = this.mExitTransitionCoordinators.size() - 1;
    while (i >= 0)
    {
      if (((WeakReference)this.mExitTransitionCoordinators.valueAt(i)).get() == null) {
        this.mExitTransitionCoordinators.removeAt(i);
      }
      i -= 1;
    }
    i = this.mExitTransitionCoordinatorsKey;
    this.mExitTransitionCoordinatorsKey = (i + 1);
    this.mExitTransitionCoordinators.append(i, paramExitTransitionCoordinator);
    return i;
  }
  
  public void clear()
  {
    this.mEnteringNames = null;
    this.mExitingFrom = null;
    this.mExitingTo = null;
    this.mExitingToView = null;
    this.mCalledExitCoordinator = null;
    this.mEnterTransitionCoordinator = null;
    this.mEnterActivityOptions = null;
    this.mExitTransitionCoordinators = null;
  }
  
  public void enterReady(Activity paramActivity)
  {
    if ((this.mEnterActivityOptions == null) || (this.mIsEnterTriggered)) {
      return;
    }
    this.mIsEnterTriggered = true;
    this.mHasExited = false;
    ArrayList localArrayList = this.mEnterActivityOptions.getSharedElementNames();
    ResultReceiver localResultReceiver = this.mEnterActivityOptions.getResultReceiver();
    if (this.mEnterActivityOptions.isReturning())
    {
      restoreExitedViews();
      paramActivity.getWindow().getDecorView().setVisibility(0);
    }
    this.mEnterTransitionCoordinator = new EnterTransitionCoordinator(paramActivity, localResultReceiver, localArrayList, this.mEnterActivityOptions.isReturning(), this.mEnterActivityOptions.isCrossTask());
    if (this.mEnterActivityOptions.isCrossTask())
    {
      this.mExitingFrom = new ArrayList(this.mEnterActivityOptions.getSharedElementNames());
      this.mExitingTo = new ArrayList(this.mEnterActivityOptions.getSharedElementNames());
    }
    if (!this.mIsEnterPostponed) {
      startEnter();
    }
  }
  
  public void onResume(Activity paramActivity, boolean paramBoolean)
  {
    if ((paramBoolean) || (this.mEnterTransitionCoordinator == null))
    {
      restoreExitedViews();
      restoreReenteringViews();
      return;
    }
    paramActivity.mHandler.postDelayed(new Runnable()
    {
      public void run()
      {
        if ((ActivityTransitionState.-get0(ActivityTransitionState.this) == null) || (ActivityTransitionState.-get0(ActivityTransitionState.this).isWaitingForRemoteExit()))
        {
          ActivityTransitionState.-wrap0(ActivityTransitionState.this);
          ActivityTransitionState.-wrap1(ActivityTransitionState.this);
        }
      }
    }, 1000L);
  }
  
  public void onStop()
  {
    restoreExitedViews();
    if (this.mEnterTransitionCoordinator != null)
    {
      this.mEnterTransitionCoordinator.stop();
      this.mEnterTransitionCoordinator = null;
    }
    if (this.mReturnExitCoordinator != null)
    {
      this.mReturnExitCoordinator.stop();
      this.mReturnExitCoordinator = null;
    }
  }
  
  public void postponeEnterTransition()
  {
    this.mIsEnterPostponed = true;
  }
  
  public void readState(Bundle paramBundle)
  {
    if (paramBundle != null)
    {
      if ((this.mEnterTransitionCoordinator == null) || (this.mEnterTransitionCoordinator.isReturning())) {
        this.mEnteringNames = paramBundle.getStringArrayList("android:enteringSharedElements");
      }
      if (this.mEnterTransitionCoordinator == null)
      {
        this.mExitingFrom = paramBundle.getStringArrayList("android:exitingMappedFrom");
        this.mExitingTo = paramBundle.getStringArrayList("android:exitingMappedTo");
      }
    }
  }
  
  public void saveState(Bundle paramBundle)
  {
    if (this.mEnteringNames != null) {
      paramBundle.putStringArrayList("android:enteringSharedElements", this.mEnteringNames);
    }
    if (this.mExitingFrom != null)
    {
      paramBundle.putStringArrayList("android:exitingMappedFrom", this.mExitingFrom);
      paramBundle.putStringArrayList("android:exitingMappedTo", this.mExitingTo);
    }
  }
  
  public void setEnterActivityOptions(Activity paramActivity, ActivityOptions paramActivityOptions)
  {
    Window localWindow = paramActivity.getWindow();
    if (localWindow == null) {
      return;
    }
    localWindow.getDecorView();
    if ((localWindow.hasFeature(13)) && (paramActivityOptions != null) && (this.mEnterActivityOptions == null) && (this.mEnterTransitionCoordinator == null) && (paramActivityOptions.getAnimationType() == 5))
    {
      this.mEnterActivityOptions = paramActivityOptions;
      this.mIsEnterTriggered = false;
      if (this.mEnterActivityOptions.isReturning())
      {
        restoreExitedViews();
        int i = this.mEnterActivityOptions.getResultCode();
        if (i != 0) {
          paramActivity.onActivityReenter(i, this.mEnterActivityOptions.getResultData());
        }
      }
    }
  }
  
  public boolean startExitBackTransition(final Activity paramActivity)
  {
    if ((this.mEnteringNames == null) || (this.mCalledExitCoordinator != null)) {
      return false;
    }
    if (!this.mHasExited)
    {
      this.mHasExited = true;
      Object localObject2 = null;
      final Object localObject1 = null;
      int i = 0;
      if (this.mEnterTransitionCoordinator != null)
      {
        Transition localTransition = this.mEnterTransitionCoordinator.getEnterViewsTransition();
        ViewGroup localViewGroup = this.mEnterTransitionCoordinator.getDecor();
        boolean bool = this.mEnterTransitionCoordinator.cancelEnter();
        this.mEnterTransitionCoordinator = null;
        localObject1 = localViewGroup;
        i = bool;
        localObject2 = localTransition;
        if (localTransition != null)
        {
          localObject1 = localViewGroup;
          i = bool;
          localObject2 = localTransition;
          if (localViewGroup != null)
          {
            localTransition.pause(localViewGroup);
            localObject2 = localTransition;
            i = bool;
            localObject1 = localViewGroup;
          }
        }
      }
      this.mReturnExitCoordinator = new ExitTransitionCoordinator(paramActivity, paramActivity.getWindow(), paramActivity.mEnterTransitionListener, this.mEnteringNames, null, null, true);
      if ((localObject2 != null) && (localObject1 != null)) {
        ((Transition)localObject2).resume((View)localObject1);
      }
      if ((i != 0) && (localObject1 != null)) {
        ((ViewGroup)localObject1).getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
        {
          public boolean onPreDraw()
          {
            localObject1.getViewTreeObserver().removeOnPreDrawListener(this);
            if (ActivityTransitionState.-get1(ActivityTransitionState.this) != null) {
              ActivityTransitionState.-get1(ActivityTransitionState.this).startExit(paramActivity.mResultCode, paramActivity.mResultData);
            }
            return true;
          }
        });
      }
    }
    else
    {
      return true;
    }
    this.mReturnExitCoordinator.startExit(paramActivity.mResultCode, paramActivity.mResultData);
    return true;
  }
  
  public void startExitOutTransition(Activity paramActivity, Bundle paramBundle)
  {
    this.mEnterTransitionCoordinator = null;
    if ((!paramActivity.getWindow().hasFeature(13)) || (this.mExitTransitionCoordinators == null)) {
      return;
    }
    paramActivity = new ActivityOptions(paramBundle);
    if (paramActivity.getAnimationType() == 5)
    {
      int i = paramActivity.getExitCoordinatorKey();
      i = this.mExitTransitionCoordinators.indexOfKey(i);
      if (i >= 0)
      {
        this.mCalledExitCoordinator = ((ExitTransitionCoordinator)((WeakReference)this.mExitTransitionCoordinators.valueAt(i)).get());
        this.mExitTransitionCoordinators.removeAt(i);
        if (this.mCalledExitCoordinator != null)
        {
          this.mExitingFrom = this.mCalledExitCoordinator.getAcceptedNames();
          this.mExitingTo = this.mCalledExitCoordinator.getMappedNames();
          this.mExitingToView = this.mCalledExitCoordinator.copyMappedViews();
          this.mCalledExitCoordinator.startExit();
        }
      }
    }
  }
  
  public void startPostponedEnterTransition()
  {
    if (this.mIsEnterPostponed)
    {
      this.mIsEnterPostponed = false;
      if (this.mEnterTransitionCoordinator != null) {
        startEnter();
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/ActivityTransitionState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */