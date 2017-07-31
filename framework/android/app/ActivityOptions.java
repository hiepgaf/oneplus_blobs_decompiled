package android.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IRemoteCallback;
import android.os.IRemoteCallback.Stub;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.transition.Transition;
import android.transition.Transition.TransitionListenerAdapter;
import android.transition.TransitionManager;
import android.util.Pair;
import android.util.Slog;
import android.view.AppTransitionAnimationSpec;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import java.util.ArrayList;

public class ActivityOptions
{
  public static final int ANIM_CLIP_REVEAL = 11;
  public static final int ANIM_CUSTOM = 1;
  public static final int ANIM_CUSTOM_IN_PLACE = 10;
  public static final int ANIM_DEFAULT = 6;
  public static final int ANIM_LAUNCH_TASK_BEHIND = 7;
  public static final int ANIM_NONE = 0;
  public static final int ANIM_SCALE_UP = 2;
  public static final int ANIM_SCENE_TRANSITION = 5;
  public static final int ANIM_THUMBNAIL_ASPECT_SCALE_DOWN = 9;
  public static final int ANIM_THUMBNAIL_ASPECT_SCALE_UP = 8;
  public static final int ANIM_THUMBNAIL_SCALE_DOWN = 4;
  public static final int ANIM_THUMBNAIL_SCALE_UP = 3;
  public static final String EXTRA_USAGE_TIME_REPORT = "android.activity.usage_time";
  public static final String EXTRA_USAGE_TIME_REPORT_PACKAGES = "android.usage_time_packages";
  private static final String KEY_ANIMATION_FINISHED_LISTENER = "android:activity.animationFinishedListener";
  public static final String KEY_ANIM_ENTER_RES_ID = "android:activity.animEnterRes";
  public static final String KEY_ANIM_EXIT_RES_ID = "android:activity.animExitRes";
  public static final String KEY_ANIM_HEIGHT = "android:activity.animHeight";
  public static final String KEY_ANIM_IN_PLACE_RES_ID = "android:activity.animInPlaceRes";
  private static final String KEY_ANIM_SPECS = "android:activity.animSpecs";
  public static final String KEY_ANIM_START_LISTENER = "android:activity.animStartListener";
  public static final String KEY_ANIM_START_X = "android:activity.animStartX";
  public static final String KEY_ANIM_START_Y = "android:activity.animStartY";
  public static final String KEY_ANIM_THUMBNAIL = "android:activity.animThumbnail";
  public static final String KEY_ANIM_TYPE = "android:activity.animType";
  public static final String KEY_ANIM_WIDTH = "android:activity.animWidth";
  private static final String KEY_DOCK_CREATE_MODE = "android:activity.dockCreateMode";
  private static final String KEY_EXIT_COORDINATOR_INDEX = "android:activity.exitCoordinatorIndex";
  public static final String KEY_LAUNCH_BOUNDS = "android:activity.launchBounds";
  private static final String KEY_LAUNCH_STACK_ID = "android.activity.launchStackId";
  private static final String KEY_LAUNCH_TASK_ID = "android.activity.launchTaskId";
  public static final String KEY_PACKAGE_NAME = "android:activity.packageName";
  private static final String KEY_RESULT_CODE = "android:activity.resultCode";
  private static final String KEY_RESULT_DATA = "android:activity.resultData";
  private static final String KEY_ROTATION_ANIMATION_HINT = "android:activity.rotationAnimationHint";
  private static final String KEY_TASK_OVERLAY = "android.activity.taskOverlay";
  private static final String KEY_TRANSITION_COMPLETE_LISTENER = "android:activity.transitionCompleteListener";
  private static final String KEY_TRANSITION_IS_RETURNING = "android:activity.transitionIsReturning";
  private static final String KEY_TRANSITION_SHARED_ELEMENTS = "android:activity.sharedElementNames";
  private static final String KEY_USAGE_TIME_REPORT = "android:activity.usageTimeReport";
  private static final String TAG = "ActivityOptions";
  private AppTransitionAnimationSpec[] mAnimSpecs;
  private IRemoteCallback mAnimationFinishedListener;
  private IRemoteCallback mAnimationStartedListener;
  private int mAnimationType = 0;
  private int mCustomEnterResId;
  private int mCustomExitResId;
  private int mCustomInPlaceResId;
  private int mDockCreateMode = 0;
  private int mExitCoordinatorIndex;
  private int mHeight;
  private boolean mIsReturning;
  private Rect mLaunchBounds;
  private int mLaunchStackId = -1;
  private int mLaunchTaskId = -1;
  private String mPackageName;
  private int mResultCode;
  private Intent mResultData;
  private int mRotationAnimationHint = -1;
  private ArrayList<String> mSharedElementNames;
  private int mStartX;
  private int mStartY;
  private boolean mTaskOverlay;
  private Bitmap mThumbnail;
  private ResultReceiver mTransitionReceiver;
  private PendingIntent mUsageTimeReport;
  private int mWidth;
  
  private ActivityOptions() {}
  
  public ActivityOptions(Bundle paramBundle)
  {
    paramBundle.setDefusable(true);
    this.mPackageName = paramBundle.getString("android:activity.packageName");
    try
    {
      this.mUsageTimeReport = ((PendingIntent)paramBundle.getParcelable("android:activity.usageTimeReport"));
      this.mLaunchBounds = ((Rect)paramBundle.getParcelable("android:activity.launchBounds"));
      this.mAnimationType = paramBundle.getInt("android:activity.animType");
      switch (this.mAnimationType)
      {
      case 6: 
      case 7: 
      default: 
        this.mLaunchStackId = paramBundle.getInt("android.activity.launchStackId", -1);
        this.mLaunchTaskId = paramBundle.getInt("android.activity.launchTaskId", -1);
        this.mTaskOverlay = paramBundle.getBoolean("android.activity.taskOverlay", false);
        this.mDockCreateMode = paramBundle.getInt("android:activity.dockCreateMode", 0);
        if (paramBundle.containsKey("android:activity.animSpecs"))
        {
          Parcelable[] arrayOfParcelable = paramBundle.getParcelableArray("android:activity.animSpecs");
          this.mAnimSpecs = new AppTransitionAnimationSpec[arrayOfParcelable.length];
          int i = arrayOfParcelable.length - 1;
          while (i >= 0)
          {
            this.mAnimSpecs[i] = ((AppTransitionAnimationSpec)arrayOfParcelable[i]);
            i -= 1;
          }
        }
        break;
      }
    }
    catch (RuntimeException localRuntimeException)
    {
      for (;;)
      {
        Slog.w("ActivityOptions", localRuntimeException);
        continue;
        this.mCustomEnterResId = paramBundle.getInt("android:activity.animEnterRes", 0);
        this.mCustomExitResId = paramBundle.getInt("android:activity.animExitRes", 0);
        this.mAnimationStartedListener = IRemoteCallback.Stub.asInterface(paramBundle.getBinder("android:activity.animStartListener"));
        continue;
        this.mCustomInPlaceResId = paramBundle.getInt("android:activity.animInPlaceRes", 0);
        continue;
        this.mStartX = paramBundle.getInt("android:activity.animStartX", 0);
        this.mStartY = paramBundle.getInt("android:activity.animStartY", 0);
        this.mWidth = paramBundle.getInt("android:activity.animWidth", 0);
        this.mHeight = paramBundle.getInt("android:activity.animHeight", 0);
        continue;
        this.mThumbnail = ((Bitmap)paramBundle.getParcelable("android:activity.animThumbnail"));
        this.mStartX = paramBundle.getInt("android:activity.animStartX", 0);
        this.mStartY = paramBundle.getInt("android:activity.animStartY", 0);
        this.mWidth = paramBundle.getInt("android:activity.animWidth", 0);
        this.mHeight = paramBundle.getInt("android:activity.animHeight", 0);
        this.mAnimationStartedListener = IRemoteCallback.Stub.asInterface(paramBundle.getBinder("android:activity.animStartListener"));
        continue;
        this.mTransitionReceiver = ((ResultReceiver)paramBundle.getParcelable("android:activity.transitionCompleteListener"));
        this.mIsReturning = paramBundle.getBoolean("android:activity.transitionIsReturning", false);
        this.mSharedElementNames = paramBundle.getStringArrayList("android:activity.sharedElementNames");
        this.mResultData = ((Intent)paramBundle.getParcelable("android:activity.resultData"));
        this.mResultCode = paramBundle.getInt("android:activity.resultCode");
        this.mExitCoordinatorIndex = paramBundle.getInt("android:activity.exitCoordinatorIndex");
      }
      if (paramBundle.containsKey("android:activity.animationFinishedListener")) {
        this.mAnimationFinishedListener = IRemoteCallback.Stub.asInterface(paramBundle.getBinder("android:activity.animationFinishedListener"));
      }
      this.mRotationAnimationHint = paramBundle.getInt("android:activity.rotationAnimationHint");
    }
  }
  
  public static void abort(ActivityOptions paramActivityOptions)
  {
    if (paramActivityOptions != null) {
      paramActivityOptions.abort();
    }
  }
  
  public static ActivityOptions fromBundle(Bundle paramBundle)
  {
    ActivityOptions localActivityOptions = null;
    if (paramBundle != null) {
      localActivityOptions = new ActivityOptions(paramBundle);
    }
    return localActivityOptions;
  }
  
  private static ActivityOptions makeAspectScaledThumbnailAnimation(View paramView, Bitmap paramBitmap, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Handler paramHandler, OnAnimationStartedListener paramOnAnimationStartedListener, boolean paramBoolean)
  {
    ActivityOptions localActivityOptions = new ActivityOptions();
    localActivityOptions.mPackageName = paramView.getContext().getPackageName();
    if (paramBoolean) {}
    for (int i = 8;; i = 9)
    {
      localActivityOptions.mAnimationType = i;
      localActivityOptions.mThumbnail = paramBitmap;
      paramBitmap = new int[2];
      paramView.getLocationOnScreen(paramBitmap);
      localActivityOptions.mStartX = (paramBitmap[0] + paramInt1);
      localActivityOptions.mStartY = (paramBitmap[1] + paramInt2);
      localActivityOptions.mWidth = paramInt3;
      localActivityOptions.mHeight = paramInt4;
      localActivityOptions.setOnAnimationStartedListener(paramHandler, paramOnAnimationStartedListener);
      return localActivityOptions;
    }
  }
  
  public static ActivityOptions makeBasic()
  {
    return new ActivityOptions();
  }
  
  public static ActivityOptions makeClipRevealAnimation(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    ActivityOptions localActivityOptions = new ActivityOptions();
    localActivityOptions.mAnimationType = 11;
    int[] arrayOfInt = new int[2];
    paramView.getLocationOnScreen(arrayOfInt);
    localActivityOptions.mStartX = (arrayOfInt[0] + paramInt1);
    localActivityOptions.mStartY = (arrayOfInt[1] + paramInt2);
    localActivityOptions.mWidth = paramInt3;
    localActivityOptions.mHeight = paramInt4;
    return localActivityOptions;
  }
  
  public static ActivityOptions makeCustomAnimation(Context paramContext, int paramInt1, int paramInt2)
  {
    return makeCustomAnimation(paramContext, paramInt1, paramInt2, null, null);
  }
  
  public static ActivityOptions makeCustomAnimation(Context paramContext, int paramInt1, int paramInt2, Handler paramHandler, OnAnimationStartedListener paramOnAnimationStartedListener)
  {
    ActivityOptions localActivityOptions = new ActivityOptions();
    localActivityOptions.mPackageName = paramContext.getPackageName();
    localActivityOptions.mAnimationType = 1;
    localActivityOptions.mCustomEnterResId = paramInt1;
    localActivityOptions.mCustomExitResId = paramInt2;
    localActivityOptions.setOnAnimationStartedListener(paramHandler, paramOnAnimationStartedListener);
    return localActivityOptions;
  }
  
  public static ActivityOptions makeCustomInPlaceAnimation(Context paramContext, int paramInt)
  {
    if (paramInt == 0) {
      throw new RuntimeException("You must specify a valid animation.");
    }
    ActivityOptions localActivityOptions = new ActivityOptions();
    localActivityOptions.mPackageName = paramContext.getPackageName();
    localActivityOptions.mAnimationType = 10;
    localActivityOptions.mCustomInPlaceResId = paramInt;
    return localActivityOptions;
  }
  
  public static ActivityOptions makeScaleUpAnimation(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    ActivityOptions localActivityOptions = new ActivityOptions();
    localActivityOptions.mPackageName = paramView.getContext().getPackageName();
    localActivityOptions.mAnimationType = 2;
    int[] arrayOfInt = new int[2];
    paramView.getLocationOnScreen(arrayOfInt);
    localActivityOptions.mStartX = (arrayOfInt[0] + paramInt1);
    localActivityOptions.mStartY = (arrayOfInt[1] + paramInt2);
    localActivityOptions.mWidth = paramInt3;
    localActivityOptions.mHeight = paramInt4;
    return localActivityOptions;
  }
  
  static ActivityOptions makeSceneTransitionAnimation(Activity paramActivity, ExitTransitionCoordinator paramExitTransitionCoordinator, ArrayList<String> paramArrayList, int paramInt, Intent paramIntent)
  {
    ActivityOptions localActivityOptions = new ActivityOptions();
    localActivityOptions.mAnimationType = 5;
    localActivityOptions.mSharedElementNames = paramArrayList;
    localActivityOptions.mTransitionReceiver = paramExitTransitionCoordinator;
    localActivityOptions.mIsReturning = true;
    localActivityOptions.mResultCode = paramInt;
    localActivityOptions.mResultData = paramIntent;
    localActivityOptions.mExitCoordinatorIndex = paramActivity.mActivityTransitionState.addExitTransitionCoordinator(paramExitTransitionCoordinator);
    return localActivityOptions;
  }
  
  public static ActivityOptions makeSceneTransitionAnimation(Activity paramActivity, View paramView, String paramString)
  {
    return makeSceneTransitionAnimation(paramActivity, new Pair[] { Pair.create(paramView, paramString) });
  }
  
  @SafeVarargs
  public static ActivityOptions makeSceneTransitionAnimation(Activity paramActivity, Pair<View, String>... paramVarArgs)
  {
    ActivityOptions localActivityOptions = new ActivityOptions();
    makeSceneTransitionAnimation(paramActivity, paramActivity.getWindow(), localActivityOptions, paramActivity.mExitTransitionListener, paramVarArgs);
    return localActivityOptions;
  }
  
  static ExitTransitionCoordinator makeSceneTransitionAnimation(Activity paramActivity, Window paramWindow, ActivityOptions paramActivityOptions, SharedElementCallback paramSharedElementCallback, Pair<View, String>[] paramArrayOfPair)
  {
    if (!paramWindow.hasFeature(13))
    {
      paramActivityOptions.mAnimationType = 6;
      return null;
    }
    paramActivityOptions.mAnimationType = 5;
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    if (paramArrayOfPair != null)
    {
      int i = 0;
      while (i < paramArrayOfPair.length)
      {
        Pair<View, String> localPair = paramArrayOfPair[i];
        String str = (String)localPair.second;
        if (str == null) {
          throw new IllegalArgumentException("Shared element name must not be null");
        }
        localArrayList1.add(str);
        if ((View)localPair.first == null) {
          throw new IllegalArgumentException("Shared element must not be null");
        }
        localArrayList2.add((View)localPair.first);
        i += 1;
      }
    }
    paramWindow = new ExitTransitionCoordinator(paramActivity, paramWindow, paramSharedElementCallback, localArrayList1, localArrayList1, localArrayList2, false);
    paramActivityOptions.mTransitionReceiver = paramWindow;
    paramActivityOptions.mSharedElementNames = localArrayList1;
    if (paramActivity == null) {}
    for (boolean bool = true;; bool = false)
    {
      paramActivityOptions.mIsReturning = bool;
      if (paramActivity != null) {
        break;
      }
      paramActivityOptions.mExitCoordinatorIndex = -1;
      return paramWindow;
    }
    paramActivityOptions.mExitCoordinatorIndex = paramActivity.mActivityTransitionState.addExitTransitionCoordinator(paramWindow);
    return paramWindow;
  }
  
  public static ActivityOptions makeTaskLaunchBehind()
  {
    ActivityOptions localActivityOptions = new ActivityOptions();
    localActivityOptions.mAnimationType = 7;
    return localActivityOptions;
  }
  
  private static ActivityOptions makeThumbnailAnimation(View paramView, Bitmap paramBitmap, int paramInt1, int paramInt2, OnAnimationStartedListener paramOnAnimationStartedListener, boolean paramBoolean)
  {
    ActivityOptions localActivityOptions = new ActivityOptions();
    localActivityOptions.mPackageName = paramView.getContext().getPackageName();
    if (paramBoolean) {}
    for (int i = 3;; i = 4)
    {
      localActivityOptions.mAnimationType = i;
      localActivityOptions.mThumbnail = paramBitmap;
      paramBitmap = new int[2];
      paramView.getLocationOnScreen(paramBitmap);
      localActivityOptions.mStartX = (paramBitmap[0] + paramInt1);
      localActivityOptions.mStartY = (paramBitmap[1] + paramInt2);
      localActivityOptions.setOnAnimationStartedListener(paramView.getHandler(), paramOnAnimationStartedListener);
      return localActivityOptions;
    }
  }
  
  public static ActivityOptions makeThumbnailAspectScaleDownAnimation(View paramView, Bitmap paramBitmap, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Handler paramHandler, OnAnimationStartedListener paramOnAnimationStartedListener)
  {
    return makeAspectScaledThumbnailAnimation(paramView, paramBitmap, paramInt1, paramInt2, paramInt3, paramInt4, paramHandler, paramOnAnimationStartedListener, false);
  }
  
  public static ActivityOptions makeThumbnailAspectScaleDownAnimation(View paramView, AppTransitionAnimationSpec[] paramArrayOfAppTransitionAnimationSpec, Handler paramHandler, OnAnimationStartedListener paramOnAnimationStartedListener, OnAnimationFinishedListener paramOnAnimationFinishedListener)
  {
    ActivityOptions localActivityOptions = new ActivityOptions();
    localActivityOptions.mPackageName = paramView.getContext().getPackageName();
    localActivityOptions.mAnimationType = 9;
    localActivityOptions.mAnimSpecs = paramArrayOfAppTransitionAnimationSpec;
    localActivityOptions.setOnAnimationStartedListener(paramHandler, paramOnAnimationStartedListener);
    localActivityOptions.setOnAnimationFinishedListener(paramHandler, paramOnAnimationFinishedListener);
    return localActivityOptions;
  }
  
  public static ActivityOptions makeThumbnailAspectScaleUpAnimation(View paramView, Bitmap paramBitmap, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Handler paramHandler, OnAnimationStartedListener paramOnAnimationStartedListener)
  {
    return makeAspectScaledThumbnailAnimation(paramView, paramBitmap, paramInt1, paramInt2, paramInt3, paramInt4, paramHandler, paramOnAnimationStartedListener, true);
  }
  
  public static ActivityOptions makeThumbnailScaleDownAnimation(View paramView, Bitmap paramBitmap, int paramInt1, int paramInt2, OnAnimationStartedListener paramOnAnimationStartedListener)
  {
    return makeThumbnailAnimation(paramView, paramBitmap, paramInt1, paramInt2, paramOnAnimationStartedListener, false);
  }
  
  public static ActivityOptions makeThumbnailScaleUpAnimation(View paramView, Bitmap paramBitmap, int paramInt1, int paramInt2)
  {
    return makeThumbnailScaleUpAnimation(paramView, paramBitmap, paramInt1, paramInt2, null);
  }
  
  public static ActivityOptions makeThumbnailScaleUpAnimation(View paramView, Bitmap paramBitmap, int paramInt1, int paramInt2, OnAnimationStartedListener paramOnAnimationStartedListener)
  {
    return makeThumbnailAnimation(paramView, paramBitmap, paramInt1, paramInt2, paramOnAnimationStartedListener, true);
  }
  
  private void setOnAnimationFinishedListener(final Handler paramHandler, final OnAnimationFinishedListener paramOnAnimationFinishedListener)
  {
    if (paramOnAnimationFinishedListener != null) {
      this.mAnimationFinishedListener = new IRemoteCallback.Stub()
      {
        public void sendResult(Bundle paramAnonymousBundle)
          throws RemoteException
        {
          paramHandler.post(new Runnable()
          {
            public void run()
            {
              this.val$listener.onAnimationFinished();
            }
          });
        }
      };
    }
  }
  
  private void setOnAnimationStartedListener(final Handler paramHandler, final OnAnimationStartedListener paramOnAnimationStartedListener)
  {
    if (paramOnAnimationStartedListener != null) {
      this.mAnimationStartedListener = new IRemoteCallback.Stub()
      {
        public void sendResult(Bundle paramAnonymousBundle)
          throws RemoteException
        {
          paramHandler.post(new Runnable()
          {
            public void run()
            {
              this.val$listener.onAnimationStarted();
            }
          });
        }
      };
    }
  }
  
  @SafeVarargs
  public static ActivityOptions startSharedElementAnimation(Window paramWindow, Pair<View, String>... paramVarArgs)
  {
    ActivityOptions localActivityOptions = new ActivityOptions();
    if (paramWindow.getDecorView() == null) {
      return localActivityOptions;
    }
    paramVarArgs = makeSceneTransitionAnimation(null, paramWindow, localActivityOptions, null, paramVarArgs);
    if (paramVarArgs != null)
    {
      paramVarArgs.setHideSharedElementsCallback(new HideWindowListener(paramWindow, paramVarArgs));
      paramVarArgs.startExit();
    }
    return localActivityOptions;
  }
  
  public static void stopSharedElementAnimation(Window paramWindow)
  {
    paramWindow = paramWindow.getDecorView();
    if (paramWindow == null) {
      return;
    }
    ExitTransitionCoordinator localExitTransitionCoordinator = (ExitTransitionCoordinator)paramWindow.getTag(16908381);
    if (localExitTransitionCoordinator != null)
    {
      localExitTransitionCoordinator.cancelPendingTransitions();
      paramWindow.setTagInternal(16908381, null);
      TransitionManager.endTransitions((ViewGroup)paramWindow);
      localExitTransitionCoordinator.resetViews();
      localExitTransitionCoordinator.clearState();
      paramWindow.setVisibility(0);
    }
  }
  
  public void abort()
  {
    if (this.mAnimationStartedListener != null) {}
    try
    {
      this.mAnimationStartedListener.sendResult(null);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public ActivityOptions forTargetActivity()
  {
    if (this.mAnimationType == 5)
    {
      ActivityOptions localActivityOptions = new ActivityOptions();
      localActivityOptions.update(this);
      return localActivityOptions;
    }
    return null;
  }
  
  public AppTransitionAnimationSpec[] getAnimSpecs()
  {
    return this.mAnimSpecs;
  }
  
  public IRemoteCallback getAnimationFinishedListener()
  {
    return this.mAnimationFinishedListener;
  }
  
  public int getAnimationType()
  {
    return this.mAnimationType;
  }
  
  public int getCustomEnterResId()
  {
    return this.mCustomEnterResId;
  }
  
  public int getCustomExitResId()
  {
    return this.mCustomExitResId;
  }
  
  public int getCustomInPlaceResId()
  {
    return this.mCustomInPlaceResId;
  }
  
  public int getDockCreateMode()
  {
    return this.mDockCreateMode;
  }
  
  public int getExitCoordinatorKey()
  {
    return this.mExitCoordinatorIndex;
  }
  
  public int getHeight()
  {
    return this.mHeight;
  }
  
  public Rect getLaunchBounds()
  {
    return this.mLaunchBounds;
  }
  
  public int getLaunchStackId()
  {
    return this.mLaunchStackId;
  }
  
  public boolean getLaunchTaskBehind()
  {
    return this.mAnimationType == 7;
  }
  
  public int getLaunchTaskId()
  {
    return this.mLaunchTaskId;
  }
  
  public IRemoteCallback getOnAnimationStartListener()
  {
    return this.mAnimationStartedListener;
  }
  
  public String getPackageName()
  {
    return this.mPackageName;
  }
  
  public int getResultCode()
  {
    return this.mResultCode;
  }
  
  public Intent getResultData()
  {
    return this.mResultData;
  }
  
  public ResultReceiver getResultReceiver()
  {
    return this.mTransitionReceiver;
  }
  
  public int getRotationAnimationHint()
  {
    return this.mRotationAnimationHint;
  }
  
  public ArrayList<String> getSharedElementNames()
  {
    return this.mSharedElementNames;
  }
  
  public int getStartX()
  {
    return this.mStartX;
  }
  
  public int getStartY()
  {
    return this.mStartY;
  }
  
  public boolean getTaskOverlay()
  {
    return this.mTaskOverlay;
  }
  
  public Bitmap getThumbnail()
  {
    return this.mThumbnail;
  }
  
  public PendingIntent getUsageTimeReport()
  {
    return this.mUsageTimeReport;
  }
  
  public int getWidth()
  {
    return this.mWidth;
  }
  
  boolean isCrossTask()
  {
    boolean bool = false;
    if (this.mExitCoordinatorIndex < 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isReturning()
  {
    return this.mIsReturning;
  }
  
  public void requestUsageTimeReport(PendingIntent paramPendingIntent)
  {
    this.mUsageTimeReport = paramPendingIntent;
  }
  
  public void setDockCreateMode(int paramInt)
  {
    this.mDockCreateMode = paramInt;
  }
  
  public ActivityOptions setLaunchBounds(Rect paramRect)
  {
    Rect localRect = null;
    if (paramRect != null) {
      localRect = new Rect(paramRect);
    }
    this.mLaunchBounds = localRect;
    return this;
  }
  
  public void setLaunchStackId(int paramInt)
  {
    this.mLaunchStackId = paramInt;
  }
  
  public void setLaunchTaskId(int paramInt)
  {
    this.mLaunchTaskId = paramInt;
  }
  
  public void setRotationAnimationHint(int paramInt)
  {
    this.mRotationAnimationHint = paramInt;
  }
  
  public void setTaskOverlay(boolean paramBoolean)
  {
    this.mTaskOverlay = paramBoolean;
  }
  
  public Bundle toBundle()
  {
    Object localObject2 = null;
    Object localObject1 = null;
    if (this.mAnimationType == 6) {
      return null;
    }
    Bundle localBundle = new Bundle();
    if (this.mPackageName != null) {
      localBundle.putString("android:activity.packageName", this.mPackageName);
    }
    if (this.mLaunchBounds != null) {
      localBundle.putParcelable("android:activity.launchBounds", this.mLaunchBounds);
    }
    localBundle.putInt("android:activity.animType", this.mAnimationType);
    if (this.mUsageTimeReport != null) {
      localBundle.putParcelable("android:activity.usageTimeReport", this.mUsageTimeReport);
    }
    switch (this.mAnimationType)
    {
    }
    for (;;)
    {
      localBundle.putInt("android.activity.launchStackId", this.mLaunchStackId);
      localBundle.putInt("android.activity.launchTaskId", this.mLaunchTaskId);
      localBundle.putBoolean("android.activity.taskOverlay", this.mTaskOverlay);
      localBundle.putInt("android:activity.dockCreateMode", this.mDockCreateMode);
      if (this.mAnimSpecs != null) {
        localBundle.putParcelableArray("android:activity.animSpecs", this.mAnimSpecs);
      }
      if (this.mAnimationFinishedListener != null) {
        localBundle.putBinder("android:activity.animationFinishedListener", this.mAnimationFinishedListener.asBinder());
      }
      localBundle.putInt("android:activity.rotationAnimationHint", this.mRotationAnimationHint);
      return localBundle;
      localBundle.putInt("android:activity.animEnterRes", this.mCustomEnterResId);
      localBundle.putInt("android:activity.animExitRes", this.mCustomExitResId);
      if (this.mAnimationStartedListener != null) {
        localObject1 = this.mAnimationStartedListener.asBinder();
      }
      localBundle.putBinder("android:activity.animStartListener", (IBinder)localObject1);
      continue;
      localBundle.putInt("android:activity.animInPlaceRes", this.mCustomInPlaceResId);
      continue;
      localBundle.putInt("android:activity.animStartX", this.mStartX);
      localBundle.putInt("android:activity.animStartY", this.mStartY);
      localBundle.putInt("android:activity.animWidth", this.mWidth);
      localBundle.putInt("android:activity.animHeight", this.mHeight);
      continue;
      localBundle.putParcelable("android:activity.animThumbnail", this.mThumbnail);
      localBundle.putInt("android:activity.animStartX", this.mStartX);
      localBundle.putInt("android:activity.animStartY", this.mStartY);
      localBundle.putInt("android:activity.animWidth", this.mWidth);
      localBundle.putInt("android:activity.animHeight", this.mHeight);
      localObject1 = localObject2;
      if (this.mAnimationStartedListener != null) {
        localObject1 = this.mAnimationStartedListener.asBinder();
      }
      localBundle.putBinder("android:activity.animStartListener", (IBinder)localObject1);
      continue;
      if (this.mTransitionReceiver != null) {
        localBundle.putParcelable("android:activity.transitionCompleteListener", this.mTransitionReceiver);
      }
      localBundle.putBoolean("android:activity.transitionIsReturning", this.mIsReturning);
      localBundle.putStringArrayList("android:activity.sharedElementNames", this.mSharedElementNames);
      localBundle.putParcelable("android:activity.resultData", this.mResultData);
      localBundle.putInt("android:activity.resultCode", this.mResultCode);
      localBundle.putInt("android:activity.exitCoordinatorIndex", this.mExitCoordinatorIndex);
    }
  }
  
  public String toString()
  {
    return "ActivityOptions(" + hashCode() + "), mPackageName=" + this.mPackageName + ", mAnimationType=" + this.mAnimationType + ", mStartX=" + this.mStartX + ", mStartY=" + this.mStartY + ", mWidth=" + this.mWidth + ", mHeight=" + this.mHeight;
  }
  
  public void update(ActivityOptions paramActivityOptions)
  {
    if (paramActivityOptions.mPackageName != null) {
      this.mPackageName = paramActivityOptions.mPackageName;
    }
    this.mUsageTimeReport = paramActivityOptions.mUsageTimeReport;
    this.mTransitionReceiver = null;
    this.mSharedElementNames = null;
    this.mIsReturning = false;
    this.mResultData = null;
    this.mResultCode = 0;
    this.mExitCoordinatorIndex = 0;
    this.mAnimationType = paramActivityOptions.mAnimationType;
    switch (paramActivityOptions.mAnimationType)
    {
    }
    for (;;)
    {
      this.mAnimSpecs = paramActivityOptions.mAnimSpecs;
      this.mAnimationFinishedListener = paramActivityOptions.mAnimationFinishedListener;
      return;
      this.mCustomEnterResId = paramActivityOptions.mCustomEnterResId;
      this.mCustomExitResId = paramActivityOptions.mCustomExitResId;
      this.mThumbnail = null;
      if (this.mAnimationStartedListener != null) {}
      try
      {
        this.mAnimationStartedListener.sendResult(null);
        this.mAnimationStartedListener = paramActivityOptions.mAnimationStartedListener;
        continue;
        this.mCustomInPlaceResId = paramActivityOptions.mCustomInPlaceResId;
        continue;
        this.mStartX = paramActivityOptions.mStartX;
        this.mStartY = paramActivityOptions.mStartY;
        this.mWidth = paramActivityOptions.mWidth;
        this.mHeight = paramActivityOptions.mHeight;
        if (this.mAnimationStartedListener != null) {}
        try
        {
          this.mAnimationStartedListener.sendResult(null);
          this.mAnimationStartedListener = null;
          continue;
          this.mThumbnail = paramActivityOptions.mThumbnail;
          this.mStartX = paramActivityOptions.mStartX;
          this.mStartY = paramActivityOptions.mStartY;
          this.mWidth = paramActivityOptions.mWidth;
          this.mHeight = paramActivityOptions.mHeight;
          if (this.mAnimationStartedListener != null) {}
          try
          {
            this.mAnimationStartedListener.sendResult(null);
            this.mAnimationStartedListener = paramActivityOptions.mAnimationStartedListener;
            continue;
            this.mTransitionReceiver = paramActivityOptions.mTransitionReceiver;
            this.mSharedElementNames = paramActivityOptions.mSharedElementNames;
            this.mIsReturning = paramActivityOptions.mIsReturning;
            this.mThumbnail = null;
            this.mAnimationStartedListener = null;
            this.mResultData = paramActivityOptions.mResultData;
            this.mResultCode = paramActivityOptions.mResultCode;
            this.mExitCoordinatorIndex = paramActivityOptions.mExitCoordinatorIndex;
          }
          catch (RemoteException localRemoteException1)
          {
            for (;;) {}
          }
        }
        catch (RemoteException localRemoteException2)
        {
          for (;;) {}
        }
      }
      catch (RemoteException localRemoteException3)
      {
        for (;;) {}
      }
    }
  }
  
  private static class HideWindowListener
    extends Transition.TransitionListenerAdapter
    implements ExitTransitionCoordinator.HideSharedElementsCallback
  {
    private final ExitTransitionCoordinator mExit;
    private boolean mSharedElementHidden;
    private ArrayList<View> mSharedElements;
    private boolean mTransitionEnded;
    private final boolean mWaitingForTransition;
    private final Window mWindow;
    
    public HideWindowListener(Window paramWindow, ExitTransitionCoordinator paramExitTransitionCoordinator)
    {
      this.mWindow = paramWindow;
      this.mExit = paramExitTransitionCoordinator;
      this.mSharedElements = new ArrayList(paramExitTransitionCoordinator.mSharedElements);
      paramWindow = this.mWindow.getExitTransition();
      if (paramWindow != null) {
        paramWindow.addListener(this);
      }
      for (this.mWaitingForTransition = true;; this.mWaitingForTransition = false)
      {
        paramWindow = this.mWindow.getDecorView();
        if (paramWindow == null) {
          return;
        }
        if (paramWindow.getTag(16908381) == null) {
          break;
        }
        throw new IllegalStateException("Cannot start a transition while one is running");
      }
      paramWindow.setTagInternal(16908381, paramExitTransitionCoordinator);
    }
    
    private void hideWhenDone()
    {
      if ((this.mSharedElementHidden) && ((!this.mWaitingForTransition) || (this.mTransitionEnded)))
      {
        this.mExit.resetViews();
        int j = this.mSharedElements.size();
        int i = 0;
        while (i < j)
        {
          ((View)this.mSharedElements.get(i)).requestLayout();
          i += 1;
        }
        View localView = this.mWindow.getDecorView();
        if (localView != null)
        {
          localView.setTagInternal(16908381, null);
          localView.setVisibility(8);
        }
      }
    }
    
    public void hideSharedElements()
    {
      this.mSharedElementHidden = true;
      hideWhenDone();
    }
    
    public void onTransitionEnd(Transition paramTransition)
    {
      this.mTransitionEnded = true;
      hideWhenDone();
      paramTransition.removeListener(this);
    }
  }
  
  public static abstract interface OnAnimationFinishedListener
  {
    public abstract void onAnimationFinished();
  }
  
  public static abstract interface OnAnimationStartedListener
  {
    public abstract void onAnimationStarted();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/ActivityOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */