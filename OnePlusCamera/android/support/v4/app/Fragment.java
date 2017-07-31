package android.support.v4.app;

import android.app.Activity;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import android.support.v4.util.DebugUtils;
import android.support.v4.util.SimpleArrayMap;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class Fragment
  implements ComponentCallbacks, View.OnCreateContextMenuListener
{
  static final int ACTIVITY_CREATED = 2;
  static final int CREATED = 1;
  static final int INITIALIZING = 0;
  static final int RESUMED = 5;
  static final int STARTED = 4;
  static final int STOPPED = 3;
  static final Object USE_DEFAULT_TRANSITION = new Object();
  private static final SimpleArrayMap<String, Class<?>> sClassMap = new SimpleArrayMap();
  FragmentActivity mActivity;
  boolean mAdded;
  Boolean mAllowEnterTransitionOverlap;
  Boolean mAllowReturnTransitionOverlap;
  View mAnimatingAway;
  Bundle mArguments;
  int mBackStackNesting;
  boolean mCalled;
  boolean mCheckedForLoaderManager;
  FragmentManagerImpl mChildFragmentManager;
  ViewGroup mContainer;
  int mContainerId;
  boolean mDeferStart;
  boolean mDetached;
  Object mEnterTransition = null;
  SharedElementCallback mEnterTransitionCallback = null;
  Object mExitTransition = null;
  SharedElementCallback mExitTransitionCallback = null;
  int mFragmentId;
  FragmentManagerImpl mFragmentManager;
  boolean mFromLayout;
  boolean mHasMenu;
  boolean mHidden;
  boolean mInLayout;
  int mIndex = -1;
  View mInnerView;
  LoaderManagerImpl mLoaderManager;
  boolean mLoadersStarted;
  boolean mMenuVisible = true;
  int mNextAnim;
  Fragment mParentFragment;
  Object mReenterTransition = USE_DEFAULT_TRANSITION;
  boolean mRemoving;
  boolean mRestored;
  boolean mResumed;
  boolean mRetainInstance;
  boolean mRetaining;
  Object mReturnTransition = USE_DEFAULT_TRANSITION;
  Bundle mSavedFragmentState;
  SparseArray<Parcelable> mSavedViewState;
  Object mSharedElementEnterTransition = null;
  Object mSharedElementReturnTransition = USE_DEFAULT_TRANSITION;
  int mState = 0;
  int mStateAfterAnimating;
  String mTag;
  Fragment mTarget;
  int mTargetIndex = -1;
  int mTargetRequestCode;
  boolean mUserVisibleHint = true;
  View mView;
  String mWho;
  
  public static Fragment instantiate(Context paramContext, String paramString)
  {
    return instantiate(paramContext, paramString, null);
  }
  
  /* Error */
  public static Fragment instantiate(Context paramContext, String paramString, Bundle paramBundle)
  {
    // Byte code:
    //   0: getstatic 109	android/support/v4/app/Fragment:sClassMap	Landroid/support/v4/util/SimpleArrayMap;
    //   3: aload_1
    //   4: invokevirtual 154	android/support/v4/util/SimpleArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   7: checkcast 156	java/lang/Class
    //   10: astore_3
    //   11: aload_3
    //   12: ifnull +19 -> 31
    //   15: aload_3
    //   16: astore_0
    //   17: aload_0
    //   18: invokevirtual 160	java/lang/Class:newInstance	()Ljava/lang/Object;
    //   21: checkcast 2	android/support/v4/app/Fragment
    //   24: astore_0
    //   25: aload_2
    //   26: ifnonnull +65 -> 91
    //   29: aload_0
    //   30: areturn
    //   31: aload_0
    //   32: invokevirtual 166	android/content/Context:getClassLoader	()Ljava/lang/ClassLoader;
    //   35: aload_1
    //   36: invokevirtual 172	java/lang/ClassLoader:loadClass	(Ljava/lang/String;)Ljava/lang/Class;
    //   39: astore_0
    //   40: getstatic 109	android/support/v4/app/Fragment:sClassMap	Landroid/support/v4/util/SimpleArrayMap;
    //   43: aload_1
    //   44: aload_0
    //   45: invokevirtual 176	android/support/v4/util/SimpleArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   48: pop
    //   49: goto -32 -> 17
    //   52: astore_0
    //   53: new 12	android/support/v4/app/Fragment$InstantiationException
    //   56: dup
    //   57: new 178	java/lang/StringBuilder
    //   60: dup
    //   61: invokespecial 179	java/lang/StringBuilder:<init>	()V
    //   64: ldc -75
    //   66: invokevirtual 185	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   69: aload_1
    //   70: invokevirtual 185	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   73: ldc -69
    //   75: invokevirtual 185	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   78: ldc -67
    //   80: invokevirtual 185	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   83: invokevirtual 193	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   86: aload_0
    //   87: invokespecial 196	android/support/v4/app/Fragment$InstantiationException:<init>	(Ljava/lang/String;Ljava/lang/Exception;)V
    //   90: athrow
    //   91: aload_2
    //   92: aload_0
    //   93: invokevirtual 200	java/lang/Object:getClass	()Ljava/lang/Class;
    //   96: invokevirtual 201	java/lang/Class:getClassLoader	()Ljava/lang/ClassLoader;
    //   99: invokevirtual 207	android/os/Bundle:setClassLoader	(Ljava/lang/ClassLoader;)V
    //   102: aload_0
    //   103: aload_2
    //   104: putfield 209	android/support/v4/app/Fragment:mArguments	Landroid/os/Bundle;
    //   107: aload_0
    //   108: areturn
    //   109: astore_0
    //   110: new 12	android/support/v4/app/Fragment$InstantiationException
    //   113: dup
    //   114: new 178	java/lang/StringBuilder
    //   117: dup
    //   118: invokespecial 179	java/lang/StringBuilder:<init>	()V
    //   121: ldc -75
    //   123: invokevirtual 185	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   126: aload_1
    //   127: invokevirtual 185	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   130: ldc -69
    //   132: invokevirtual 185	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   135: ldc -67
    //   137: invokevirtual 185	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   140: invokevirtual 193	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   143: aload_0
    //   144: invokespecial 196	android/support/v4/app/Fragment$InstantiationException:<init>	(Ljava/lang/String;Ljava/lang/Exception;)V
    //   147: athrow
    //   148: astore_0
    //   149: new 12	android/support/v4/app/Fragment$InstantiationException
    //   152: dup
    //   153: new 178	java/lang/StringBuilder
    //   156: dup
    //   157: invokespecial 179	java/lang/StringBuilder:<init>	()V
    //   160: ldc -75
    //   162: invokevirtual 185	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   165: aload_1
    //   166: invokevirtual 185	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   169: ldc -69
    //   171: invokevirtual 185	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   174: ldc -67
    //   176: invokevirtual 185	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   179: invokevirtual 193	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   182: aload_0
    //   183: invokespecial 196	android/support/v4/app/Fragment$InstantiationException:<init>	(Ljava/lang/String;Ljava/lang/Exception;)V
    //   186: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	187	0	paramContext	Context
    //   0	187	1	paramString	String
    //   0	187	2	paramBundle	Bundle
    //   10	6	3	localClass	Class
    // Exception table:
    //   from	to	target	type
    //   0	11	52	java/lang/ClassNotFoundException
    //   17	25	52	java/lang/ClassNotFoundException
    //   31	49	52	java/lang/ClassNotFoundException
    //   91	107	52	java/lang/ClassNotFoundException
    //   0	11	109	java/lang/InstantiationException
    //   17	25	109	java/lang/InstantiationException
    //   31	49	109	java/lang/InstantiationException
    //   91	107	109	java/lang/InstantiationException
    //   0	11	148	java/lang/IllegalAccessException
    //   17	25	148	java/lang/IllegalAccessException
    //   31	49	148	java/lang/IllegalAccessException
    //   91	107	148	java/lang/IllegalAccessException
  }
  
  static boolean isSupportFragmentClass(Context paramContext, String paramString)
  {
    try
    {
      Class localClass = (Class)sClassMap.get(paramString);
      if (localClass != null) {
        paramContext = localClass;
      }
      for (;;)
      {
        return Fragment.class.isAssignableFrom(paramContext);
        paramContext = paramContext.getClassLoader().loadClass(paramString);
        sClassMap.put(paramString, paramContext);
      }
      return false;
    }
    catch (ClassNotFoundException paramContext) {}
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mFragmentId=#");
    paramPrintWriter.print(Integer.toHexString(this.mFragmentId));
    paramPrintWriter.print(" mContainerId=#");
    paramPrintWriter.print(Integer.toHexString(this.mContainerId));
    paramPrintWriter.print(" mTag=");
    paramPrintWriter.println(this.mTag);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mState=");
    paramPrintWriter.print(this.mState);
    paramPrintWriter.print(" mIndex=");
    paramPrintWriter.print(this.mIndex);
    paramPrintWriter.print(" mWho=");
    paramPrintWriter.print(this.mWho);
    paramPrintWriter.print(" mBackStackNesting=");
    paramPrintWriter.println(this.mBackStackNesting);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mAdded=");
    paramPrintWriter.print(this.mAdded);
    paramPrintWriter.print(" mRemoving=");
    paramPrintWriter.print(this.mRemoving);
    paramPrintWriter.print(" mResumed=");
    paramPrintWriter.print(this.mResumed);
    paramPrintWriter.print(" mFromLayout=");
    paramPrintWriter.print(this.mFromLayout);
    paramPrintWriter.print(" mInLayout=");
    paramPrintWriter.println(this.mInLayout);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mHidden=");
    paramPrintWriter.print(this.mHidden);
    paramPrintWriter.print(" mDetached=");
    paramPrintWriter.print(this.mDetached);
    paramPrintWriter.print(" mMenuVisible=");
    paramPrintWriter.print(this.mMenuVisible);
    paramPrintWriter.print(" mHasMenu=");
    paramPrintWriter.println(this.mHasMenu);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mRetainInstance=");
    paramPrintWriter.print(this.mRetainInstance);
    paramPrintWriter.print(" mRetaining=");
    paramPrintWriter.print(this.mRetaining);
    paramPrintWriter.print(" mUserVisibleHint=");
    paramPrintWriter.println(this.mUserVisibleHint);
    if (this.mFragmentManager == null)
    {
      if (this.mActivity != null) {
        break label432;
      }
      label324:
      if (this.mParentFragment != null) {
        break label455;
      }
      label331:
      if (this.mArguments != null) {
        break label478;
      }
      label338:
      if (this.mSavedFragmentState != null) {
        break label501;
      }
      label345:
      if (this.mSavedViewState != null) {
        break label524;
      }
      label352:
      if (this.mTarget != null) {
        break label547;
      }
      label359:
      if (this.mNextAnim != 0) {
        break label585;
      }
      label366:
      if (this.mContainer != null) {
        break label608;
      }
      label373:
      if (this.mView != null) {
        break label631;
      }
      label380:
      if (this.mInnerView != null) {
        break label654;
      }
      label387:
      if (this.mAnimatingAway != null) {
        break label677;
      }
      label394:
      if (this.mLoaderManager != null) {
        break label720;
      }
    }
    for (;;)
    {
      if (this.mChildFragmentManager != null) {
        break label766;
      }
      return;
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mFragmentManager=");
      paramPrintWriter.println(this.mFragmentManager);
      break;
      label432:
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mActivity=");
      paramPrintWriter.println(this.mActivity);
      break label324;
      label455:
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mParentFragment=");
      paramPrintWriter.println(this.mParentFragment);
      break label331;
      label478:
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mArguments=");
      paramPrintWriter.println(this.mArguments);
      break label338;
      label501:
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mSavedFragmentState=");
      paramPrintWriter.println(this.mSavedFragmentState);
      break label345;
      label524:
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mSavedViewState=");
      paramPrintWriter.println(this.mSavedViewState);
      break label352;
      label547:
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mTarget=");
      paramPrintWriter.print(this.mTarget);
      paramPrintWriter.print(" mTargetRequestCode=");
      paramPrintWriter.println(this.mTargetRequestCode);
      break label359;
      label585:
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mNextAnim=");
      paramPrintWriter.println(this.mNextAnim);
      break label366;
      label608:
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mContainer=");
      paramPrintWriter.println(this.mContainer);
      break label373;
      label631:
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mView=");
      paramPrintWriter.println(this.mView);
      break label380;
      label654:
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mInnerView=");
      paramPrintWriter.println(this.mView);
      break label387;
      label677:
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mAnimatingAway=");
      paramPrintWriter.println(this.mAnimatingAway);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mStateAfterAnimating=");
      paramPrintWriter.println(this.mStateAfterAnimating);
      break label394;
      label720:
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("Loader Manager:");
      this.mLoaderManager.dump(paramString + "  ", paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
    label766:
    paramPrintWriter.print(paramString);
    paramPrintWriter.println("Child " + this.mChildFragmentManager + ":");
    this.mChildFragmentManager.dump(paramString + "  ", paramFileDescriptor, paramPrintWriter, paramArrayOfString);
  }
  
  public final boolean equals(Object paramObject)
  {
    return super.equals(paramObject);
  }
  
  Fragment findFragmentByWho(String paramString)
  {
    if (!paramString.equals(this.mWho))
    {
      if (this.mChildFragmentManager == null) {
        return null;
      }
    }
    else {
      return this;
    }
    return this.mChildFragmentManager.findFragmentByWho(paramString);
  }
  
  public final FragmentActivity getActivity()
  {
    return this.mActivity;
  }
  
  public boolean getAllowEnterTransitionOverlap()
  {
    if (this.mAllowEnterTransitionOverlap != null) {
      return this.mAllowEnterTransitionOverlap.booleanValue();
    }
    return true;
  }
  
  public boolean getAllowReturnTransitionOverlap()
  {
    if (this.mAllowReturnTransitionOverlap != null) {
      return this.mAllowReturnTransitionOverlap.booleanValue();
    }
    return true;
  }
  
  public final Bundle getArguments()
  {
    return this.mArguments;
  }
  
  public final FragmentManager getChildFragmentManager()
  {
    if (this.mChildFragmentManager != null) {}
    for (;;)
    {
      return this.mChildFragmentManager;
      instantiateChildFragmentManager();
      if (this.mState < 5)
      {
        if (this.mState < 4)
        {
          if (this.mState >= 2) {
            break label78;
          }
          if (this.mState < 1) {
            continue;
          }
          this.mChildFragmentManager.dispatchCreate();
        }
      }
      else
      {
        this.mChildFragmentManager.dispatchResume();
        continue;
      }
      this.mChildFragmentManager.dispatchStart();
      continue;
      label78:
      this.mChildFragmentManager.dispatchActivityCreated();
    }
  }
  
  public Object getEnterTransition()
  {
    return this.mEnterTransition;
  }
  
  public Object getExitTransition()
  {
    return this.mExitTransition;
  }
  
  public final FragmentManager getFragmentManager()
  {
    return this.mFragmentManager;
  }
  
  public final int getId()
  {
    return this.mFragmentId;
  }
  
  public LayoutInflater getLayoutInflater(Bundle paramBundle)
  {
    paramBundle = this.mActivity.getLayoutInflater().cloneInContext(this.mActivity);
    getChildFragmentManager();
    paramBundle.setFactory(this.mChildFragmentManager.getLayoutInflaterFactory());
    return paramBundle;
  }
  
  public LoaderManager getLoaderManager()
  {
    if (this.mLoaderManager == null)
    {
      if (this.mActivity != null)
      {
        this.mCheckedForLoaderManager = true;
        this.mLoaderManager = this.mActivity.getLoaderManager(this.mWho, this.mLoadersStarted, true);
        return this.mLoaderManager;
      }
    }
    else {
      return this.mLoaderManager;
    }
    throw new IllegalStateException("Fragment " + this + " not attached to Activity");
  }
  
  public final Fragment getParentFragment()
  {
    return this.mParentFragment;
  }
  
  public Object getReenterTransition()
  {
    if (this.mReenterTransition != USE_DEFAULT_TRANSITION) {
      return this.mReenterTransition;
    }
    return getExitTransition();
  }
  
  public final Resources getResources()
  {
    if (this.mActivity != null) {
      return this.mActivity.getResources();
    }
    throw new IllegalStateException("Fragment " + this + " not attached to Activity");
  }
  
  public final boolean getRetainInstance()
  {
    return this.mRetainInstance;
  }
  
  public Object getReturnTransition()
  {
    if (this.mReturnTransition != USE_DEFAULT_TRANSITION) {
      return this.mReturnTransition;
    }
    return getEnterTransition();
  }
  
  public Object getSharedElementEnterTransition()
  {
    return this.mSharedElementEnterTransition;
  }
  
  public Object getSharedElementReturnTransition()
  {
    if (this.mSharedElementReturnTransition != USE_DEFAULT_TRANSITION) {
      return this.mSharedElementReturnTransition;
    }
    return getSharedElementEnterTransition();
  }
  
  public final String getString(int paramInt)
  {
    return getResources().getString(paramInt);
  }
  
  public final String getString(int paramInt, Object... paramVarArgs)
  {
    return getResources().getString(paramInt, paramVarArgs);
  }
  
  public final String getTag()
  {
    return this.mTag;
  }
  
  public final Fragment getTargetFragment()
  {
    return this.mTarget;
  }
  
  public final int getTargetRequestCode()
  {
    return this.mTargetRequestCode;
  }
  
  public final CharSequence getText(int paramInt)
  {
    return getResources().getText(paramInt);
  }
  
  public boolean getUserVisibleHint()
  {
    return this.mUserVisibleHint;
  }
  
  @Nullable
  public View getView()
  {
    return this.mView;
  }
  
  public final boolean hasOptionsMenu()
  {
    return this.mHasMenu;
  }
  
  public final int hashCode()
  {
    return super.hashCode();
  }
  
  void initState()
  {
    this.mIndex = -1;
    this.mWho = null;
    this.mAdded = false;
    this.mRemoving = false;
    this.mResumed = false;
    this.mFromLayout = false;
    this.mInLayout = false;
    this.mRestored = false;
    this.mBackStackNesting = 0;
    this.mFragmentManager = null;
    this.mChildFragmentManager = null;
    this.mActivity = null;
    this.mFragmentId = 0;
    this.mContainerId = 0;
    this.mTag = null;
    this.mHidden = false;
    this.mDetached = false;
    this.mRetaining = false;
    this.mLoaderManager = null;
    this.mLoadersStarted = false;
    this.mCheckedForLoaderManager = false;
  }
  
  void instantiateChildFragmentManager()
  {
    this.mChildFragmentManager = new FragmentManagerImpl();
    this.mChildFragmentManager.attachActivity(this.mActivity, new FragmentContainer()
    {
      public View findViewById(int paramAnonymousInt)
      {
        if (Fragment.this.mView != null) {
          return Fragment.this.mView.findViewById(paramAnonymousInt);
        }
        throw new IllegalStateException("Fragment does not have a view");
      }
      
      public boolean hasView()
      {
        return Fragment.this.mView != null;
      }
    }, this);
  }
  
  public final boolean isAdded()
  {
    if (this.mActivity == null) {}
    while (!this.mAdded) {
      return false;
    }
    return true;
  }
  
  public final boolean isDetached()
  {
    return this.mDetached;
  }
  
  public final boolean isHidden()
  {
    return this.mHidden;
  }
  
  final boolean isInBackStack()
  {
    return this.mBackStackNesting > 0;
  }
  
  public final boolean isInLayout()
  {
    return this.mInLayout;
  }
  
  public final boolean isMenuVisible()
  {
    return this.mMenuVisible;
  }
  
  public final boolean isRemoving()
  {
    return this.mRemoving;
  }
  
  public final boolean isResumed()
  {
    return this.mResumed;
  }
  
  public final boolean isVisible()
  {
    if (!isAdded()) {}
    while ((isHidden()) || (this.mView == null) || (this.mView.getWindowToken() == null) || (this.mView.getVisibility() != 0)) {
      return false;
    }
    return true;
  }
  
  public void onActivityCreated(@Nullable Bundle paramBundle)
  {
    this.mCalled = true;
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent) {}
  
  public void onAttach(Activity paramActivity)
  {
    this.mCalled = true;
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    this.mCalled = true;
  }
  
  public boolean onContextItemSelected(MenuItem paramMenuItem)
  {
    return false;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    this.mCalled = true;
  }
  
  public Animation onCreateAnimation(int paramInt1, boolean paramBoolean, int paramInt2)
  {
    return null;
  }
  
  public void onCreateContextMenu(ContextMenu paramContextMenu, View paramView, ContextMenu.ContextMenuInfo paramContextMenuInfo)
  {
    getActivity().onCreateContextMenu(paramContextMenu, paramView, paramContextMenuInfo);
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater) {}
  
  public View onCreateView(LayoutInflater paramLayoutInflater, @Nullable ViewGroup paramViewGroup, @Nullable Bundle paramBundle)
  {
    return null;
  }
  
  public void onDestroy()
  {
    this.mCalled = true;
    if (this.mCheckedForLoaderManager) {}
    while (this.mLoaderManager == null)
    {
      return;
      this.mCheckedForLoaderManager = true;
      this.mLoaderManager = this.mActivity.getLoaderManager(this.mWho, this.mLoadersStarted, false);
    }
    this.mLoaderManager.doDestroy();
  }
  
  public void onDestroyOptionsMenu() {}
  
  public void onDestroyView()
  {
    this.mCalled = true;
  }
  
  public void onDetach()
  {
    this.mCalled = true;
  }
  
  public void onHiddenChanged(boolean paramBoolean) {}
  
  public void onInflate(Activity paramActivity, AttributeSet paramAttributeSet, Bundle paramBundle)
  {
    this.mCalled = true;
  }
  
  public void onLowMemory()
  {
    this.mCalled = true;
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    return false;
  }
  
  public void onOptionsMenuClosed(Menu paramMenu) {}
  
  public void onPause()
  {
    this.mCalled = true;
  }
  
  public void onPrepareOptionsMenu(Menu paramMenu) {}
  
  public void onResume()
  {
    this.mCalled = true;
  }
  
  public void onSaveInstanceState(Bundle paramBundle) {}
  
  public void onStart()
  {
    this.mCalled = true;
    if (this.mLoadersStarted) {}
    for (;;)
    {
      return;
      this.mLoadersStarted = true;
      if (this.mCheckedForLoaderManager) {}
      while (this.mLoaderManager != null)
      {
        this.mLoaderManager.doStart();
        return;
        this.mCheckedForLoaderManager = true;
        this.mLoaderManager = this.mActivity.getLoaderManager(this.mWho, this.mLoadersStarted, false);
      }
    }
  }
  
  public void onStop()
  {
    this.mCalled = true;
  }
  
  public void onViewCreated(View paramView, @Nullable Bundle paramBundle) {}
  
  public void onViewStateRestored(@Nullable Bundle paramBundle)
  {
    this.mCalled = true;
  }
  
  void performActivityCreated(Bundle paramBundle)
  {
    if (this.mChildFragmentManager == null) {}
    for (;;)
    {
      this.mCalled = false;
      onActivityCreated(paramBundle);
      if (!this.mCalled) {
        break;
      }
      if (this.mChildFragmentManager != null) {
        break label76;
      }
      return;
      this.mChildFragmentManager.noteStateNotSaved();
    }
    throw new SuperNotCalledException("Fragment " + this + " did not call through to super.onActivityCreated()");
    label76:
    this.mChildFragmentManager.dispatchActivityCreated();
  }
  
  void performConfigurationChanged(Configuration paramConfiguration)
  {
    onConfigurationChanged(paramConfiguration);
    if (this.mChildFragmentManager == null) {
      return;
    }
    this.mChildFragmentManager.dispatchConfigurationChanged(paramConfiguration);
  }
  
  boolean performContextItemSelected(MenuItem paramMenuItem)
  {
    if (this.mHidden) {}
    do
    {
      return false;
      if (onContextItemSelected(paramMenuItem)) {
        break;
      }
    } while ((this.mChildFragmentManager == null) || (!this.mChildFragmentManager.dispatchContextItemSelected(paramMenuItem)));
    return true;
    return true;
  }
  
  void performCreate(Bundle paramBundle)
  {
    if (this.mChildFragmentManager == null)
    {
      this.mCalled = false;
      onCreate(paramBundle);
      if (!this.mCalled) {
        break label39;
      }
      if (paramBundle != null) {
        break label73;
      }
    }
    label39:
    label73:
    do
    {
      return;
      this.mChildFragmentManager.noteStateNotSaved();
      break;
      throw new SuperNotCalledException("Fragment " + this + " did not call through to super.onCreate()");
      paramBundle = paramBundle.getParcelable("android:support:fragments");
    } while (paramBundle == null);
    if (this.mChildFragmentManager != null) {}
    for (;;)
    {
      this.mChildFragmentManager.restoreAllState(paramBundle, null);
      this.mChildFragmentManager.dispatchCreate();
      return;
      instantiateChildFragmentManager();
    }
  }
  
  boolean performCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    boolean bool1 = false;
    boolean bool2 = false;
    if (this.mHidden) {
      return bool2;
    }
    if (!this.mHasMenu) {}
    for (;;)
    {
      bool2 = bool1;
      if (this.mChildFragmentManager == null) {
        break;
      }
      return bool1 | this.mChildFragmentManager.dispatchCreateOptionsMenu(paramMenu, paramMenuInflater);
      if (this.mMenuVisible)
      {
        bool1 = true;
        onCreateOptionsMenu(paramMenu, paramMenuInflater);
      }
    }
  }
  
  View performCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    if (this.mChildFragmentManager == null) {}
    for (;;)
    {
      return onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
      this.mChildFragmentManager.noteStateNotSaved();
    }
  }
  
  void performDestroy()
  {
    if (this.mChildFragmentManager == null) {}
    for (;;)
    {
      this.mCalled = false;
      onDestroy();
      if (!this.mCalled) {
        break;
      }
      return;
      this.mChildFragmentManager.dispatchDestroy();
    }
    throw new SuperNotCalledException("Fragment " + this + " did not call through to super.onDestroy()");
  }
  
  void performDestroyView()
  {
    if (this.mChildFragmentManager == null) {}
    for (;;)
    {
      this.mCalled = false;
      onDestroyView();
      if (!this.mCalled) {
        break;
      }
      if (this.mLoaderManager != null) {
        break label75;
      }
      return;
      this.mChildFragmentManager.dispatchDestroyView();
    }
    throw new SuperNotCalledException("Fragment " + this + " did not call through to super.onDestroyView()");
    label75:
    this.mLoaderManager.doReportNextStart();
  }
  
  void performLowMemory()
  {
    onLowMemory();
    if (this.mChildFragmentManager == null) {
      return;
    }
    this.mChildFragmentManager.dispatchLowMemory();
  }
  
  boolean performOptionsItemSelected(MenuItem paramMenuItem)
  {
    if (this.mHidden) {}
    do
    {
      while ((this.mChildFragmentManager == null) || (!this.mChildFragmentManager.dispatchOptionsItemSelected(paramMenuItem)))
      {
        return false;
        if (this.mHasMenu) {
          break;
        }
      }
      return true;
    } while ((!this.mMenuVisible) || (!onOptionsItemSelected(paramMenuItem)));
    return true;
  }
  
  void performOptionsMenuClosed(Menu paramMenu)
  {
    if (this.mHidden) {}
    for (;;)
    {
      return;
      if (!this.mHasMenu) {}
      while (this.mChildFragmentManager != null)
      {
        this.mChildFragmentManager.dispatchOptionsMenuClosed(paramMenu);
        return;
        if (this.mMenuVisible) {
          onOptionsMenuClosed(paramMenu);
        }
      }
    }
  }
  
  void performPause()
  {
    if (this.mChildFragmentManager == null) {}
    for (;;)
    {
      this.mCalled = false;
      onPause();
      if (!this.mCalled) {
        break;
      }
      return;
      this.mChildFragmentManager.dispatchPause();
    }
    throw new SuperNotCalledException("Fragment " + this + " did not call through to super.onPause()");
  }
  
  boolean performPrepareOptionsMenu(Menu paramMenu)
  {
    boolean bool1 = false;
    boolean bool2 = false;
    if (this.mHidden) {
      return bool2;
    }
    if (!this.mHasMenu) {}
    for (;;)
    {
      bool2 = bool1;
      if (this.mChildFragmentManager == null) {
        break;
      }
      return bool1 | this.mChildFragmentManager.dispatchPrepareOptionsMenu(paramMenu);
      if (this.mMenuVisible)
      {
        bool1 = true;
        onPrepareOptionsMenu(paramMenu);
      }
    }
  }
  
  void performReallyStop()
  {
    if (this.mChildFragmentManager == null) {
      if (this.mLoadersStarted) {
        break label25;
      }
    }
    for (;;)
    {
      return;
      this.mChildFragmentManager.dispatchReallyStop();
      break;
      label25:
      this.mLoadersStarted = false;
      if (this.mCheckedForLoaderManager) {}
      while (this.mLoaderManager != null)
      {
        if (!this.mActivity.mRetaining) {
          break label90;
        }
        this.mLoaderManager.doRetain();
        return;
        this.mCheckedForLoaderManager = true;
        this.mLoaderManager = this.mActivity.getLoaderManager(this.mWho, this.mLoadersStarted, false);
      }
    }
    label90:
    this.mLoaderManager.doStop();
  }
  
  void performResume()
  {
    if (this.mChildFragmentManager == null) {}
    for (;;)
    {
      this.mCalled = false;
      onResume();
      if (!this.mCalled) {
        break;
      }
      if (this.mChildFragmentManager != null) {
        break label83;
      }
      return;
      this.mChildFragmentManager.noteStateNotSaved();
      this.mChildFragmentManager.execPendingActions();
    }
    throw new SuperNotCalledException("Fragment " + this + " did not call through to super.onResume()");
    label83:
    this.mChildFragmentManager.dispatchResume();
    this.mChildFragmentManager.execPendingActions();
  }
  
  void performSaveInstanceState(Bundle paramBundle)
  {
    onSaveInstanceState(paramBundle);
    if (this.mChildFragmentManager == null) {}
    Parcelable localParcelable;
    do
    {
      return;
      localParcelable = this.mChildFragmentManager.saveAllState();
    } while (localParcelable == null);
    paramBundle.putParcelable("android:support:fragments", localParcelable);
  }
  
  void performStart()
  {
    if (this.mChildFragmentManager == null)
    {
      this.mCalled = false;
      onStart();
      if (!this.mCalled) {
        break label56;
      }
      if (this.mChildFragmentManager != null) {
        break label90;
      }
    }
    for (;;)
    {
      if (this.mLoaderManager != null) {
        break label100;
      }
      return;
      this.mChildFragmentManager.noteStateNotSaved();
      this.mChildFragmentManager.execPendingActions();
      break;
      label56:
      throw new SuperNotCalledException("Fragment " + this + " did not call through to super.onStart()");
      label90:
      this.mChildFragmentManager.dispatchStart();
    }
    label100:
    this.mLoaderManager.doReportStart();
  }
  
  void performStop()
  {
    if (this.mChildFragmentManager == null) {}
    for (;;)
    {
      this.mCalled = false;
      onStop();
      if (!this.mCalled) {
        break;
      }
      return;
      this.mChildFragmentManager.dispatchStop();
    }
    throw new SuperNotCalledException("Fragment " + this + " did not call through to super.onStop()");
  }
  
  public void registerForContextMenu(View paramView)
  {
    paramView.setOnCreateContextMenuListener(this);
  }
  
  final void restoreViewState(Bundle paramBundle)
  {
    if (this.mSavedViewState == null) {}
    for (;;)
    {
      this.mCalled = false;
      onViewStateRestored(paramBundle);
      if (!this.mCalled) {
        break;
      }
      return;
      this.mInnerView.restoreHierarchyState(this.mSavedViewState);
      this.mSavedViewState = null;
    }
    throw new SuperNotCalledException("Fragment " + this + " did not call through to super.onViewStateRestored()");
  }
  
  public void setAllowEnterTransitionOverlap(boolean paramBoolean)
  {
    this.mAllowEnterTransitionOverlap = Boolean.valueOf(paramBoolean);
  }
  
  public void setAllowReturnTransitionOverlap(boolean paramBoolean)
  {
    this.mAllowReturnTransitionOverlap = Boolean.valueOf(paramBoolean);
  }
  
  public void setArguments(Bundle paramBundle)
  {
    if (this.mIndex < 0)
    {
      this.mArguments = paramBundle;
      return;
    }
    throw new IllegalStateException("Fragment already active");
  }
  
  public void setEnterSharedElementCallback(SharedElementCallback paramSharedElementCallback)
  {
    this.mEnterTransitionCallback = paramSharedElementCallback;
  }
  
  public void setEnterTransition(Object paramObject)
  {
    this.mEnterTransition = paramObject;
  }
  
  public void setExitSharedElementCallback(SharedElementCallback paramSharedElementCallback)
  {
    this.mExitTransitionCallback = paramSharedElementCallback;
  }
  
  public void setExitTransition(Object paramObject)
  {
    this.mExitTransition = paramObject;
  }
  
  public void setHasOptionsMenu(boolean paramBoolean)
  {
    if (this.mHasMenu == paramBoolean) {}
    do
    {
      return;
      this.mHasMenu = paramBoolean;
    } while ((!isAdded()) || (isHidden()));
    this.mActivity.supportInvalidateOptionsMenu();
  }
  
  final void setIndex(int paramInt, Fragment paramFragment)
  {
    this.mIndex = paramInt;
    if (paramFragment == null)
    {
      this.mWho = ("android:fragment:" + this.mIndex);
      return;
    }
    this.mWho = (paramFragment.mWho + ":" + this.mIndex);
  }
  
  public void setInitialSavedState(SavedState paramSavedState)
  {
    Bundle localBundle = null;
    if (this.mIndex < 0) {
      if (paramSavedState != null) {
        break label30;
      }
    }
    for (;;)
    {
      this.mSavedFragmentState = localBundle;
      return;
      throw new IllegalStateException("Fragment already active");
      label30:
      if (paramSavedState.mState != null) {
        localBundle = paramSavedState.mState;
      }
    }
  }
  
  public void setMenuVisibility(boolean paramBoolean)
  {
    if (this.mMenuVisible == paramBoolean) {}
    do
    {
      return;
      this.mMenuVisible = paramBoolean;
    } while ((!this.mHasMenu) || (!isAdded()) || (isHidden()));
    this.mActivity.supportInvalidateOptionsMenu();
  }
  
  public void setReenterTransition(Object paramObject)
  {
    this.mReenterTransition = paramObject;
  }
  
  public void setRetainInstance(boolean paramBoolean)
  {
    if (!paramBoolean) {}
    while (this.mParentFragment == null)
    {
      this.mRetainInstance = paramBoolean;
      return;
    }
    throw new IllegalStateException("Can't retain fragements that are nested in other fragments");
  }
  
  public void setReturnTransition(Object paramObject)
  {
    this.mReturnTransition = paramObject;
  }
  
  public void setSharedElementEnterTransition(Object paramObject)
  {
    this.mSharedElementEnterTransition = paramObject;
  }
  
  public void setSharedElementReturnTransition(Object paramObject)
  {
    this.mSharedElementReturnTransition = paramObject;
  }
  
  public void setTargetFragment(Fragment paramFragment, int paramInt)
  {
    this.mTarget = paramFragment;
    this.mTargetRequestCode = paramInt;
  }
  
  public void setUserVisibleHint(boolean paramBoolean)
  {
    boolean bool = false;
    if (this.mUserVisibleHint)
    {
      this.mUserVisibleHint = paramBoolean;
      if (!paramBoolean) {
        break label49;
      }
    }
    label49:
    for (paramBoolean = bool;; paramBoolean = true)
    {
      this.mDeferStart = paramBoolean;
      return;
      if ((!paramBoolean) || (this.mState >= 4)) {
        break;
      }
      this.mFragmentManager.performPendingDeferredStart(this);
      break;
    }
  }
  
  public void startActivity(Intent paramIntent)
  {
    if (this.mActivity != null)
    {
      this.mActivity.startActivityFromFragment(this, paramIntent, -1);
      return;
    }
    throw new IllegalStateException("Fragment " + this + " not attached to Activity");
  }
  
  public void startActivityForResult(Intent paramIntent, int paramInt)
  {
    if (this.mActivity != null)
    {
      this.mActivity.startActivityFromFragment(this, paramIntent, paramInt);
      return;
    }
    throw new IllegalStateException("Fragment " + this + " not attached to Activity");
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    DebugUtils.buildShortClassTag(this, localStringBuilder);
    if (this.mIndex < 0)
    {
      if (this.mFragmentId != 0) {
        break label69;
      }
      label30:
      if (this.mTag != null) {
        break label92;
      }
    }
    for (;;)
    {
      localStringBuilder.append('}');
      return localStringBuilder.toString();
      localStringBuilder.append(" #");
      localStringBuilder.append(this.mIndex);
      break;
      label69:
      localStringBuilder.append(" id=0x");
      localStringBuilder.append(Integer.toHexString(this.mFragmentId));
      break label30;
      label92:
      localStringBuilder.append(" ");
      localStringBuilder.append(this.mTag);
    }
  }
  
  public void unregisterForContextMenu(View paramView)
  {
    paramView.setOnCreateContextMenuListener(null);
  }
  
  public static class InstantiationException
    extends RuntimeException
  {
    public InstantiationException(String paramString, Exception paramException)
    {
      super(paramException);
    }
  }
  
  public static class SavedState
    implements Parcelable
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public Fragment.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new Fragment.SavedState(paramAnonymousParcel, null);
      }
      
      public Fragment.SavedState[] newArray(int paramAnonymousInt)
      {
        return new Fragment.SavedState[paramAnonymousInt];
      }
    };
    final Bundle mState;
    
    SavedState(Bundle paramBundle)
    {
      this.mState = paramBundle;
    }
    
    SavedState(Parcel paramParcel, ClassLoader paramClassLoader)
    {
      this.mState = paramParcel.readBundle();
      if (paramClassLoader == null) {}
      while (this.mState == null) {
        return;
      }
      this.mState.setClassLoader(paramClassLoader);
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeBundle(this.mState);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/app/Fragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */