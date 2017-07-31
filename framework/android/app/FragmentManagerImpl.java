package android.app;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DebugUtils;
import android.util.Log;
import android.util.LogWriter;
import android.util.SparseArray;
import android.util.SuperNotCalledException;
import android.view.LayoutInflater.Factory2;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.android.internal.R.styleable;
import com.android.internal.util.FastPrintWriter;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class FragmentManagerImpl
  extends FragmentManager
  implements LayoutInflater.Factory2
{
  static boolean DEBUG = false;
  static final String TAG = "FragmentManager";
  static final String TARGET_REQUEST_CODE_STATE_TAG = "android:target_req_state";
  static final String TARGET_STATE_TAG = "android:target_state";
  static final String USER_VISIBLE_HINT_TAG = "android:user_visible_hint";
  static final String VIEW_STATE_TAG = "android:view_state";
  ArrayList<Fragment> mActive;
  ArrayList<Fragment> mAdded;
  ArrayList<Integer> mAvailBackStackIndices;
  ArrayList<Integer> mAvailIndices;
  ArrayList<BackStackRecord> mBackStack;
  ArrayList<FragmentManager.OnBackStackChangedListener> mBackStackChangeListeners;
  ArrayList<BackStackRecord> mBackStackIndices;
  FragmentContainer mContainer;
  FragmentController mController;
  ArrayList<Fragment> mCreatedMenus;
  int mCurState = 0;
  boolean mDestroyed;
  Runnable mExecCommit = new Runnable()
  {
    public void run()
    {
      FragmentManagerImpl.this.execPendingActions();
    }
  };
  boolean mExecutingActions;
  boolean mHavePendingDeferredStart;
  FragmentHostCallback<?> mHost;
  boolean mNeedMenuInvalidate;
  String mNoTransactionsBecause;
  Fragment mParent;
  ArrayList<Runnable> mPendingActions;
  SparseArray<Parcelable> mStateArray = null;
  Bundle mStateBundle = null;
  boolean mStateSaved;
  Runnable[] mTmpActions;
  
  private void checkStateLoss()
  {
    if (this.mStateSaved) {
      throw new IllegalStateException("Can not perform this action after onSaveInstanceState");
    }
    if (this.mNoTransactionsBecause != null) {
      throw new IllegalStateException("Can not perform this action inside of " + this.mNoTransactionsBecause);
    }
  }
  
  static boolean modifiesAlpha(Animator paramAnimator)
  {
    if (paramAnimator == null) {
      return false;
    }
    int i;
    if ((paramAnimator instanceof ValueAnimator))
    {
      paramAnimator = ((ValueAnimator)paramAnimator).getValues();
      i = 0;
      while (i < paramAnimator.length)
      {
        if ("alpha".equals(paramAnimator[i].getPropertyName())) {
          return true;
        }
        i += 1;
      }
    }
    if ((paramAnimator instanceof AnimatorSet))
    {
      paramAnimator = ((AnimatorSet)paramAnimator).getChildAnimations();
      i = 0;
      while (i < paramAnimator.size())
      {
        if (modifiesAlpha((Animator)paramAnimator.get(i))) {
          return true;
        }
        i += 1;
      }
    }
    return false;
  }
  
  public static int reverseTransit(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return 0;
    case 4097: 
      return 8194;
    case 8194: 
      return 4097;
    }
    return 4099;
  }
  
  private void setHWLayerAnimListenerIfAlpha(View paramView, Animator paramAnimator)
  {
    if ((paramView == null) || (paramAnimator == null)) {
      return;
    }
    if (shouldRunOnHWLayer(paramView, paramAnimator)) {
      paramAnimator.addListener(new AnimateOnHWLayerIfNeededListener(paramView));
    }
  }
  
  static boolean shouldRunOnHWLayer(View paramView, Animator paramAnimator)
  {
    boolean bool2 = false;
    if ((paramView == null) || (paramAnimator == null)) {
      return false;
    }
    boolean bool1 = bool2;
    if (paramView.getLayerType() == 0)
    {
      bool1 = bool2;
      if (paramView.hasOverlappingRendering()) {
        bool1 = modifiesAlpha(paramAnimator);
      }
    }
    return bool1;
  }
  
  private void throwException(RuntimeException paramRuntimeException)
  {
    Log.e("FragmentManager", paramRuntimeException.getMessage());
    FastPrintWriter localFastPrintWriter = new FastPrintWriter(new LogWriter(6, "FragmentManager"), false, 1024);
    if (this.mHost != null) {
      Log.e("FragmentManager", "Activity state:");
    }
    for (;;)
    {
      try
      {
        this.mHost.onDump("  ", null, localFastPrintWriter, new String[0]);
        localFastPrintWriter.flush();
        throw paramRuntimeException;
      }
      catch (Exception localException1)
      {
        localFastPrintWriter.flush();
        Log.e("FragmentManager", "Failed dumping state", localException1);
        continue;
      }
      Log.e("FragmentManager", "Fragment manager state:");
      try
      {
        dump("  ", null, localFastPrintWriter, new String[0]);
      }
      catch (Exception localException2)
      {
        localFastPrintWriter.flush();
        Log.e("FragmentManager", "Failed dumping state", localException2);
      }
    }
  }
  
  public static int transitToStyleIndex(int paramInt, boolean paramBoolean)
  {
    switch (paramInt)
    {
    default: 
      return -1;
    case 4097: 
      if (paramBoolean) {
        return 0;
      }
      return 1;
    case 8194: 
      if (paramBoolean) {
        return 2;
      }
      return 3;
    }
    if (paramBoolean) {
      return 4;
    }
    return 5;
  }
  
  void addBackStackState(BackStackRecord paramBackStackRecord)
  {
    if (this.mBackStack == null) {
      this.mBackStack = new ArrayList();
    }
    this.mBackStack.add(paramBackStackRecord);
    reportBackStackChanged();
  }
  
  public void addFragment(Fragment paramFragment, boolean paramBoolean)
  {
    if (this.mAdded == null) {
      this.mAdded = new ArrayList();
    }
    if (DEBUG) {
      Log.v("FragmentManager", "add: " + paramFragment);
    }
    makeActive(paramFragment);
    if (!paramFragment.mDetached)
    {
      if (this.mAdded.contains(paramFragment)) {
        throw new IllegalStateException("Fragment already added: " + paramFragment);
      }
      this.mAdded.add(paramFragment);
      paramFragment.mAdded = true;
      paramFragment.mRemoving = false;
      if ((paramFragment.mHasMenu) && (paramFragment.mMenuVisible)) {
        this.mNeedMenuInvalidate = true;
      }
      if (paramBoolean) {
        moveToState(paramFragment);
      }
    }
  }
  
  public void addOnBackStackChangedListener(FragmentManager.OnBackStackChangedListener paramOnBackStackChangedListener)
  {
    if (this.mBackStackChangeListeners == null) {
      this.mBackStackChangeListeners = new ArrayList();
    }
    this.mBackStackChangeListeners.add(paramOnBackStackChangedListener);
  }
  
  public int allocBackStackIndex(BackStackRecord paramBackStackRecord)
  {
    try
    {
      if ((this.mAvailBackStackIndices == null) || (this.mAvailBackStackIndices.size() <= 0))
      {
        if (this.mBackStackIndices == null) {
          this.mBackStackIndices = new ArrayList();
        }
        i = this.mBackStackIndices.size();
        if (DEBUG) {
          Log.v("FragmentManager", "Setting back stack index " + i + " to " + paramBackStackRecord);
        }
        this.mBackStackIndices.add(paramBackStackRecord);
        return i;
      }
      int i = ((Integer)this.mAvailBackStackIndices.remove(this.mAvailBackStackIndices.size() - 1)).intValue();
      if (DEBUG) {
        Log.v("FragmentManager", "Adding back stack index " + i + " with " + paramBackStackRecord);
      }
      this.mBackStackIndices.set(i, paramBackStackRecord);
      return i;
    }
    finally {}
  }
  
  public void attachController(FragmentHostCallback<?> paramFragmentHostCallback, FragmentContainer paramFragmentContainer, Fragment paramFragment)
  {
    if (this.mHost != null) {
      throw new IllegalStateException("Already attached");
    }
    this.mHost = paramFragmentHostCallback;
    this.mContainer = paramFragmentContainer;
    this.mParent = paramFragment;
  }
  
  public void attachFragment(Fragment paramFragment, int paramInt1, int paramInt2)
  {
    if (DEBUG) {
      Log.v("FragmentManager", "attach: " + paramFragment);
    }
    if (paramFragment.mDetached)
    {
      paramFragment.mDetached = false;
      if (!paramFragment.mAdded)
      {
        if (this.mAdded == null) {
          this.mAdded = new ArrayList();
        }
        if (this.mAdded.contains(paramFragment)) {
          throw new IllegalStateException("Fragment already added: " + paramFragment);
        }
        if (DEBUG) {
          Log.v("FragmentManager", "add from attach: " + paramFragment);
        }
        this.mAdded.add(paramFragment);
        paramFragment.mAdded = true;
        if ((paramFragment.mHasMenu) && (paramFragment.mMenuVisible)) {
          this.mNeedMenuInvalidate = true;
        }
        moveToState(paramFragment, this.mCurState, paramInt1, paramInt2, false);
      }
    }
  }
  
  public FragmentTransaction beginTransaction()
  {
    return new BackStackRecord(this);
  }
  
  public void detachFragment(Fragment paramFragment, int paramInt1, int paramInt2)
  {
    if (DEBUG) {
      Log.v("FragmentManager", "detach: " + paramFragment);
    }
    if (!paramFragment.mDetached)
    {
      paramFragment.mDetached = true;
      if (paramFragment.mAdded)
      {
        if (this.mAdded != null)
        {
          if (DEBUG) {
            Log.v("FragmentManager", "remove from detach: " + paramFragment);
          }
          this.mAdded.remove(paramFragment);
        }
        if ((paramFragment.mHasMenu) && (paramFragment.mMenuVisible)) {
          this.mNeedMenuInvalidate = true;
        }
        paramFragment.mAdded = false;
        moveToState(paramFragment, 1, paramInt1, paramInt2, false);
      }
    }
  }
  
  public void dispatchActivityCreated()
  {
    this.mStateSaved = false;
    moveToState(2, false);
  }
  
  public void dispatchConfigurationChanged(Configuration paramConfiguration)
  {
    if (this.mAdded != null)
    {
      int i = 0;
      while (i < this.mAdded.size())
      {
        Fragment localFragment = (Fragment)this.mAdded.get(i);
        if (localFragment != null) {
          localFragment.performConfigurationChanged(paramConfiguration);
        }
        i += 1;
      }
    }
  }
  
  public boolean dispatchContextItemSelected(MenuItem paramMenuItem)
  {
    if (this.mAdded != null)
    {
      int i = 0;
      while (i < this.mAdded.size())
      {
        Fragment localFragment = (Fragment)this.mAdded.get(i);
        if ((localFragment != null) && (localFragment.performContextItemSelected(paramMenuItem))) {
          return true;
        }
        i += 1;
      }
    }
    return false;
  }
  
  public void dispatchCreate()
  {
    this.mStateSaved = false;
    moveToState(1, false);
  }
  
  public boolean dispatchCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    boolean bool2 = false;
    boolean bool1 = false;
    Object localObject2 = null;
    Object localObject1 = null;
    int i;
    if (this.mAdded != null)
    {
      i = 0;
      for (;;)
      {
        localObject2 = localObject1;
        bool2 = bool1;
        if (i >= this.mAdded.size()) {
          break;
        }
        Fragment localFragment = (Fragment)this.mAdded.get(i);
        localObject2 = localObject1;
        bool2 = bool1;
        if (localFragment != null)
        {
          localObject2 = localObject1;
          bool2 = bool1;
          if (localFragment.performCreateOptionsMenu(paramMenu, paramMenuInflater))
          {
            bool2 = true;
            localObject2 = localObject1;
            if (localObject1 == null) {
              localObject2 = new ArrayList();
            }
            ((ArrayList)localObject2).add(localFragment);
          }
        }
        i += 1;
        localObject1 = localObject2;
        bool1 = bool2;
      }
    }
    if (this.mCreatedMenus != null)
    {
      i = 0;
      if (i < this.mCreatedMenus.size())
      {
        paramMenu = (Fragment)this.mCreatedMenus.get(i);
        if ((localObject2 != null) && (((ArrayList)localObject2).contains(paramMenu))) {}
        for (;;)
        {
          i += 1;
          break;
          paramMenu.onDestroyOptionsMenu();
        }
      }
    }
    this.mCreatedMenus = ((ArrayList)localObject2);
    return bool2;
  }
  
  public void dispatchDestroy()
  {
    this.mDestroyed = true;
    execPendingActions();
    moveToState(0, false);
    this.mHost = null;
    this.mContainer = null;
    this.mParent = null;
  }
  
  public void dispatchDestroyView()
  {
    moveToState(1, false);
  }
  
  public void dispatchLowMemory()
  {
    if (this.mAdded != null)
    {
      int i = 0;
      while (i < this.mAdded.size())
      {
        Fragment localFragment = (Fragment)this.mAdded.get(i);
        if (localFragment != null) {
          localFragment.performLowMemory();
        }
        i += 1;
      }
    }
  }
  
  public void dispatchMultiWindowModeChanged(boolean paramBoolean)
  {
    if (this.mAdded == null) {
      return;
    }
    int i = this.mAdded.size() - 1;
    while (i >= 0)
    {
      Fragment localFragment = (Fragment)this.mAdded.get(i);
      if (localFragment != null) {
        localFragment.performMultiWindowModeChanged(paramBoolean);
      }
      i -= 1;
    }
  }
  
  public boolean dispatchOptionsItemSelected(MenuItem paramMenuItem)
  {
    if (this.mAdded != null)
    {
      int i = 0;
      while (i < this.mAdded.size())
      {
        Fragment localFragment = (Fragment)this.mAdded.get(i);
        if ((localFragment != null) && (localFragment.performOptionsItemSelected(paramMenuItem))) {
          return true;
        }
        i += 1;
      }
    }
    return false;
  }
  
  public void dispatchOptionsMenuClosed(Menu paramMenu)
  {
    if (this.mAdded != null)
    {
      int i = 0;
      while (i < this.mAdded.size())
      {
        Fragment localFragment = (Fragment)this.mAdded.get(i);
        if (localFragment != null) {
          localFragment.performOptionsMenuClosed(paramMenu);
        }
        i += 1;
      }
    }
  }
  
  public void dispatchPause()
  {
    moveToState(4, false);
  }
  
  public void dispatchPictureInPictureModeChanged(boolean paramBoolean)
  {
    if (this.mAdded == null) {
      return;
    }
    int i = this.mAdded.size() - 1;
    while (i >= 0)
    {
      Fragment localFragment = (Fragment)this.mAdded.get(i);
      if (localFragment != null) {
        localFragment.performPictureInPictureModeChanged(paramBoolean);
      }
      i -= 1;
    }
  }
  
  public boolean dispatchPrepareOptionsMenu(Menu paramMenu)
  {
    boolean bool2 = false;
    boolean bool1 = false;
    if (this.mAdded != null)
    {
      int i = 0;
      for (;;)
      {
        bool2 = bool1;
        if (i >= this.mAdded.size()) {
          break;
        }
        Fragment localFragment = (Fragment)this.mAdded.get(i);
        bool2 = bool1;
        if (localFragment != null)
        {
          bool2 = bool1;
          if (localFragment.performPrepareOptionsMenu(paramMenu)) {
            bool2 = true;
          }
        }
        i += 1;
        bool1 = bool2;
      }
    }
    return bool2;
  }
  
  public void dispatchResume()
  {
    this.mStateSaved = false;
    moveToState(5, false);
  }
  
  public void dispatchStart()
  {
    this.mStateSaved = false;
    moveToState(4, false);
  }
  
  public void dispatchStop()
  {
    moveToState(3, false);
  }
  
  public void dispatchTrimMemory(int paramInt)
  {
    if (this.mAdded != null)
    {
      int i = 0;
      while (i < this.mAdded.size())
      {
        Fragment localFragment = (Fragment)this.mAdded.get(i);
        if (localFragment != null) {
          localFragment.performTrimMemory(paramInt);
        }
        i += 1;
      }
    }
  }
  
  void doPendingDeferredStart()
  {
    if (this.mHavePendingDeferredStart)
    {
      boolean bool1 = false;
      int i = 0;
      while (i < this.mActive.size())
      {
        Fragment localFragment = (Fragment)this.mActive.get(i);
        boolean bool2 = bool1;
        if (localFragment != null)
        {
          bool2 = bool1;
          if (localFragment.mLoaderManager != null) {
            bool2 = bool1 | localFragment.mLoaderManager.hasRunningLoaders();
          }
        }
        i += 1;
        bool1 = bool2;
      }
      if (!bool1)
      {
        this.mHavePendingDeferredStart = false;
        startPendingDeferredFragments();
      }
    }
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    String str = paramString + "    ";
    int j;
    int i;
    Object localObject;
    if (this.mActive != null)
    {
      j = this.mActive.size();
      if (j > 0)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("Active Fragments in ");
        paramPrintWriter.print(Integer.toHexString(System.identityHashCode(this)));
        paramPrintWriter.println(":");
        i = 0;
        while (i < j)
        {
          localObject = (Fragment)this.mActive.get(i);
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  #");
          paramPrintWriter.print(i);
          paramPrintWriter.print(": ");
          paramPrintWriter.println(localObject);
          if (localObject != null) {
            ((Fragment)localObject).dump(str, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
          }
          i += 1;
        }
      }
    }
    if (this.mAdded != null)
    {
      j = this.mAdded.size();
      if (j > 0)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("Added Fragments:");
        i = 0;
        while (i < j)
        {
          localObject = (Fragment)this.mAdded.get(i);
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  #");
          paramPrintWriter.print(i);
          paramPrintWriter.print(": ");
          paramPrintWriter.println(((Fragment)localObject).toString());
          i += 1;
        }
      }
    }
    if (this.mCreatedMenus != null)
    {
      j = this.mCreatedMenus.size();
      if (j > 0)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("Fragments Created Menus:");
        i = 0;
        while (i < j)
        {
          localObject = (Fragment)this.mCreatedMenus.get(i);
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  #");
          paramPrintWriter.print(i);
          paramPrintWriter.print(": ");
          paramPrintWriter.println(((Fragment)localObject).toString());
          i += 1;
        }
      }
    }
    if (this.mBackStack != null)
    {
      j = this.mBackStack.size();
      if (j > 0)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("Back Stack:");
        i = 0;
        while (i < j)
        {
          localObject = (BackStackRecord)this.mBackStack.get(i);
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  #");
          paramPrintWriter.print(i);
          paramPrintWriter.print(": ");
          paramPrintWriter.println(((BackStackRecord)localObject).toString());
          ((BackStackRecord)localObject).dump(str, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
          i += 1;
        }
      }
    }
    try
    {
      if (this.mBackStackIndices != null)
      {
        j = this.mBackStackIndices.size();
        if (j > 0)
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.println("Back Stack Indices:");
          i = 0;
          while (i < j)
          {
            paramFileDescriptor = (BackStackRecord)this.mBackStackIndices.get(i);
            paramPrintWriter.print(paramString);
            paramPrintWriter.print("  #");
            paramPrintWriter.print(i);
            paramPrintWriter.print(": ");
            paramPrintWriter.println(paramFileDescriptor);
            i += 1;
          }
        }
      }
      if ((this.mAvailBackStackIndices != null) && (this.mAvailBackStackIndices.size() > 0))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mAvailBackStackIndices: ");
        paramPrintWriter.println(Arrays.toString(this.mAvailBackStackIndices.toArray()));
      }
      if (this.mPendingActions != null)
      {
        j = this.mPendingActions.size();
        if (j > 0)
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.println("Pending Actions:");
          i = 0;
          while (i < j)
          {
            paramFileDescriptor = (Runnable)this.mPendingActions.get(i);
            paramPrintWriter.print(paramString);
            paramPrintWriter.print("  #");
            paramPrintWriter.print(i);
            paramPrintWriter.print(": ");
            paramPrintWriter.println(paramFileDescriptor);
            i += 1;
          }
        }
      }
      paramPrintWriter.print(paramString);
    }
    finally {}
    paramPrintWriter.println("FragmentManager misc state:");
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("  mHost=");
    paramPrintWriter.println(this.mHost);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("  mContainer=");
    paramPrintWriter.println(this.mContainer);
    if (this.mParent != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("  mParent=");
      paramPrintWriter.println(this.mParent);
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("  mCurState=");
    paramPrintWriter.print(this.mCurState);
    paramPrintWriter.print(" mStateSaved=");
    paramPrintWriter.print(this.mStateSaved);
    paramPrintWriter.print(" mDestroyed=");
    paramPrintWriter.println(this.mDestroyed);
    if (this.mNeedMenuInvalidate)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("  mNeedMenuInvalidate=");
      paramPrintWriter.println(this.mNeedMenuInvalidate);
    }
    if (this.mNoTransactionsBecause != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("  mNoTransactionsBecause=");
      paramPrintWriter.println(this.mNoTransactionsBecause);
    }
    if ((this.mAvailIndices != null) && (this.mAvailIndices.size() > 0))
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("  mAvailIndices: ");
      paramPrintWriter.println(Arrays.toString(this.mAvailIndices.toArray()));
    }
  }
  
  public void enqueueAction(Runnable paramRunnable, boolean paramBoolean)
  {
    if (!paramBoolean) {
      checkStateLoss();
    }
    try
    {
      if ((this.mDestroyed) || (this.mHost == null)) {
        throw new IllegalStateException("Activity has been destroyed");
      }
    }
    finally
    {
      throw paramRunnable;
      if (this.mPendingActions == null) {
        this.mPendingActions = new ArrayList();
      }
      this.mPendingActions.add(paramRunnable);
      if (this.mPendingActions.size() == 1) {
        this.mHost.getHandler().removeCallbacks(this.mExecCommit);
      }
    }
  }
  
  public boolean execPendingActions()
  {
    if (this.mExecutingActions) {
      throw new IllegalStateException("Recursive entry to executePendingTransactions");
    }
    if (Looper.myLooper() != this.mHost.getHandler().getLooper()) {
      throw new IllegalStateException("Must be called from main thread of process");
    }
    for (boolean bool = false;; bool = true) {
      try
      {
        if (this.mPendingActions != null)
        {
          i = this.mPendingActions.size();
          if (i != 0) {}
        }
        else
        {
          doPendingDeferredStart();
          return bool;
        }
        int j = this.mPendingActions.size();
        if ((this.mTmpActions == null) || (this.mTmpActions.length < j)) {
          this.mTmpActions = new Runnable[j];
        }
        this.mPendingActions.toArray(this.mTmpActions);
        this.mPendingActions.clear();
        this.mHost.getHandler().removeCallbacks(this.mExecCommit);
        this.mExecutingActions = true;
        int i = 0;
        while (i < j)
        {
          this.mTmpActions[i].run();
          this.mTmpActions[i] = null;
          i += 1;
        }
        this.mExecutingActions = false;
      }
      finally {}
    }
  }
  
  public void execSingleAction(Runnable paramRunnable, boolean paramBoolean)
  {
    if (this.mExecutingActions) {
      throw new IllegalStateException("FragmentManager is already executing transactions");
    }
    if (Looper.myLooper() != this.mHost.getHandler().getLooper()) {
      throw new IllegalStateException("Must be called from main thread of fragment host");
    }
    if (!paramBoolean) {
      checkStateLoss();
    }
    this.mExecutingActions = true;
    paramRunnable.run();
    this.mExecutingActions = false;
    doPendingDeferredStart();
  }
  
  public boolean executePendingTransactions()
  {
    return execPendingActions();
  }
  
  public Fragment findFragmentById(int paramInt)
  {
    int i;
    Fragment localFragment;
    if (this.mAdded != null)
    {
      i = this.mAdded.size() - 1;
      while (i >= 0)
      {
        localFragment = (Fragment)this.mAdded.get(i);
        if ((localFragment != null) && (localFragment.mFragmentId == paramInt)) {
          return localFragment;
        }
        i -= 1;
      }
    }
    if (this.mActive != null)
    {
      i = this.mActive.size() - 1;
      while (i >= 0)
      {
        localFragment = (Fragment)this.mActive.get(i);
        if ((localFragment != null) && (localFragment.mFragmentId == paramInt)) {
          return localFragment;
        }
        i -= 1;
      }
    }
    return null;
  }
  
  public Fragment findFragmentByTag(String paramString)
  {
    int i;
    Fragment localFragment;
    if ((this.mAdded != null) && (paramString != null))
    {
      i = this.mAdded.size() - 1;
      while (i >= 0)
      {
        localFragment = (Fragment)this.mAdded.get(i);
        if ((localFragment != null) && (paramString.equals(localFragment.mTag))) {
          return localFragment;
        }
        i -= 1;
      }
    }
    if ((this.mActive != null) && (paramString != null))
    {
      i = this.mActive.size() - 1;
      while (i >= 0)
      {
        localFragment = (Fragment)this.mActive.get(i);
        if ((localFragment != null) && (paramString.equals(localFragment.mTag))) {
          return localFragment;
        }
        i -= 1;
      }
    }
    return null;
  }
  
  public Fragment findFragmentByWho(String paramString)
  {
    if ((this.mActive != null) && (paramString != null))
    {
      int i = this.mActive.size() - 1;
      while (i >= 0)
      {
        Fragment localFragment = (Fragment)this.mActive.get(i);
        if (localFragment != null)
        {
          localFragment = localFragment.findFragmentByWho(paramString);
          if (localFragment != null) {
            return localFragment;
          }
        }
        i -= 1;
      }
    }
    return null;
  }
  
  public void freeBackStackIndex(int paramInt)
  {
    try
    {
      this.mBackStackIndices.set(paramInt, null);
      if (this.mAvailBackStackIndices == null) {
        this.mAvailBackStackIndices = new ArrayList();
      }
      if (DEBUG) {
        Log.v("FragmentManager", "Freeing back stack index " + paramInt);
      }
      this.mAvailBackStackIndices.add(Integer.valueOf(paramInt));
      return;
    }
    finally {}
  }
  
  public FragmentManager.BackStackEntry getBackStackEntryAt(int paramInt)
  {
    return (FragmentManager.BackStackEntry)this.mBackStack.get(paramInt);
  }
  
  public int getBackStackEntryCount()
  {
    if (this.mBackStack != null) {
      return this.mBackStack.size();
    }
    return 0;
  }
  
  public Fragment getFragment(Bundle paramBundle, String paramString)
  {
    int i = paramBundle.getInt(paramString, -1);
    if (i == -1) {
      return null;
    }
    if (i >= this.mActive.size()) {
      throwException(new IllegalStateException("Fragment no longer exists for key " + paramString + ": index " + i));
    }
    paramBundle = (Fragment)this.mActive.get(i);
    if (paramBundle == null) {
      throwException(new IllegalStateException("Fragment no longer exists for key " + paramString + ": index " + i));
    }
    return paramBundle;
  }
  
  LayoutInflater.Factory2 getLayoutInflaterFactory()
  {
    return this;
  }
  
  public void hideFragment(final Fragment paramFragment, int paramInt1, int paramInt2)
  {
    if (DEBUG) {
      Log.v("FragmentManager", "hide: " + paramFragment);
    }
    if (!paramFragment.mHidden)
    {
      paramFragment.mHidden = true;
      if (paramFragment.mView != null)
      {
        Animator localAnimator = loadAnimator(paramFragment, paramInt1, false, paramInt2);
        if (localAnimator == null) {
          break label136;
        }
        localAnimator.setTarget(paramFragment.mView);
        localAnimator.addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            if (paramFragment.mView != null) {
              paramFragment.mView.setVisibility(8);
            }
          }
        });
        setHWLayerAnimListenerIfAlpha(paramFragment.mView, localAnimator);
        localAnimator.start();
      }
    }
    for (;;)
    {
      if ((paramFragment.mAdded) && (paramFragment.mHasMenu) && (paramFragment.mMenuVisible)) {
        this.mNeedMenuInvalidate = true;
      }
      paramFragment.onHiddenChanged(true);
      return;
      label136:
      paramFragment.mView.setVisibility(8);
    }
  }
  
  public void invalidateOptionsMenu()
  {
    if ((this.mHost != null) && (this.mCurState == 5))
    {
      this.mHost.onInvalidateOptionsMenu();
      return;
    }
    this.mNeedMenuInvalidate = true;
  }
  
  public boolean isDestroyed()
  {
    return this.mDestroyed;
  }
  
  boolean isStateAtLeast(int paramInt)
  {
    return this.mCurState >= paramInt;
  }
  
  Animator loadAnimator(Fragment paramFragment, int paramInt1, boolean paramBoolean, int paramInt2)
  {
    Animator localAnimator = paramFragment.onCreateAnimator(paramInt1, paramBoolean, paramFragment.mNextAnim);
    if (localAnimator != null) {
      return localAnimator;
    }
    if (paramFragment.mNextAnim != 0)
    {
      paramFragment = AnimatorInflater.loadAnimator(this.mHost.getContext(), paramFragment.mNextAnim);
      if (paramFragment != null) {
        return paramFragment;
      }
    }
    if (paramInt1 == 0) {
      return null;
    }
    int i = transitToStyleIndex(paramInt1, paramBoolean);
    if (i < 0) {
      return null;
    }
    paramInt1 = paramInt2;
    if (paramInt2 == 0)
    {
      paramInt1 = paramInt2;
      if (this.mHost.onHasWindowAnimations()) {
        paramInt1 = this.mHost.onGetWindowAnimations();
      }
    }
    if (paramInt1 == 0) {
      return null;
    }
    paramFragment = this.mHost.getContext().obtainStyledAttributes(paramInt1, R.styleable.FragmentAnimation);
    paramInt1 = paramFragment.getResourceId(i, 0);
    paramFragment.recycle();
    if (paramInt1 == 0) {
      return null;
    }
    return AnimatorInflater.loadAnimator(this.mHost.getContext(), paramInt1);
  }
  
  void makeActive(Fragment paramFragment)
  {
    if (paramFragment.mIndex >= 0) {
      return;
    }
    if ((this.mAvailIndices == null) || (this.mAvailIndices.size() <= 0))
    {
      if (this.mActive == null) {
        this.mActive = new ArrayList();
      }
      paramFragment.setIndex(this.mActive.size(), this.mParent);
      this.mActive.add(paramFragment);
    }
    for (;;)
    {
      if (DEBUG) {
        Log.v("FragmentManager", "Allocated fragment index " + paramFragment);
      }
      return;
      paramFragment.setIndex(((Integer)this.mAvailIndices.remove(this.mAvailIndices.size() - 1)).intValue(), this.mParent);
      this.mActive.set(paramFragment.mIndex, paramFragment);
    }
  }
  
  void makeInactive(Fragment paramFragment)
  {
    if (paramFragment.mIndex < 0) {
      return;
    }
    if (DEBUG) {
      Log.v("FragmentManager", "Freeing fragment index " + paramFragment);
    }
    this.mActive.set(paramFragment.mIndex, null);
    if (this.mAvailIndices == null) {
      this.mAvailIndices = new ArrayList();
    }
    this.mAvailIndices.add(Integer.valueOf(paramFragment.mIndex));
    this.mHost.inactivateFragment(paramFragment.mWho);
    paramFragment.initState();
  }
  
  void moveToState(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    if ((this.mHost == null) && (paramInt1 != 0)) {
      throw new IllegalStateException("No activity");
    }
    if ((!paramBoolean) && (this.mCurState == paramInt1)) {
      return;
    }
    this.mCurState = paramInt1;
    if (this.mActive != null)
    {
      boolean bool1 = false;
      int i = 0;
      while (i < this.mActive.size())
      {
        Fragment localFragment = (Fragment)this.mActive.get(i);
        boolean bool2 = bool1;
        if (localFragment != null)
        {
          moveToState(localFragment, paramInt1, paramInt2, paramInt3, false);
          bool2 = bool1;
          if (localFragment.mLoaderManager != null) {
            bool2 = bool1 | localFragment.mLoaderManager.hasRunningLoaders();
          }
        }
        i += 1;
        bool1 = bool2;
      }
      if (!bool1) {
        startPendingDeferredFragments();
      }
      if ((this.mNeedMenuInvalidate) && (this.mHost != null) && (this.mCurState == 5))
      {
        this.mHost.onInvalidateOptionsMenu();
        this.mNeedMenuInvalidate = false;
      }
    }
  }
  
  void moveToState(int paramInt, boolean paramBoolean)
  {
    moveToState(paramInt, 0, 0, paramBoolean);
  }
  
  void moveToState(Fragment paramFragment)
  {
    moveToState(paramFragment, this.mCurState, 0, 0, false);
  }
  
  void moveToState(final Fragment paramFragment, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    int i;
    if ((!DEBUG) || (paramFragment.mAdded))
    {
      i = paramInt1;
      if (!paramFragment.mDetached) {}
    }
    else
    {
      i = paramInt1;
      if (paramInt1 > 1) {
        i = 1;
      }
    }
    int j = i;
    if (paramFragment.mRemoving)
    {
      j = i;
      if (i > paramFragment.mState) {
        j = paramFragment.mState;
      }
    }
    paramInt1 = j;
    if (paramFragment.mDeferStart)
    {
      paramInt1 = j;
      if (paramFragment.mState < 4)
      {
        paramInt1 = j;
        if (j > 3) {
          paramInt1 = 3;
        }
      }
    }
    int k;
    int m;
    if (paramFragment.mState < paramInt1) {
      if ((!paramFragment.mFromLayout) || (paramFragment.mInLayout))
      {
        if (paramFragment.mAnimatingAway != null)
        {
          paramFragment.mAnimatingAway = null;
          moveToState(paramFragment, paramFragment.mStateAfterAnimating, 0, 0, true);
        }
        i = paramInt1;
        k = paramInt1;
        m = paramInt1;
        j = paramInt1;
        switch (paramFragment.mState)
        {
        default: 
          i = paramInt1;
        }
      }
    }
    for (;;)
    {
      if (paramFragment.mState != i)
      {
        Log.w("FragmentManager", "moveToState: Fragment state for " + paramFragment + " not updated inline; " + "expected state " + i + " found " + paramFragment.mState);
        paramFragment.mState = i;
      }
      return;
      return;
      if (DEBUG) {
        Log.v("FragmentManager", "moveto CREATED: " + paramFragment);
      }
      j = paramInt1;
      if (paramFragment.mSavedFragmentState != null)
      {
        paramFragment.mSavedViewState = paramFragment.mSavedFragmentState.getSparseParcelableArray("android:view_state");
        paramFragment.mTarget = getFragment(paramFragment.mSavedFragmentState, "android:target_state");
        if (paramFragment.mTarget != null) {
          paramFragment.mTargetRequestCode = paramFragment.mSavedFragmentState.getInt("android:target_req_state", 0);
        }
        paramFragment.mUserVisibleHint = paramFragment.mSavedFragmentState.getBoolean("android:user_visible_hint", true);
        j = paramInt1;
        if (!paramFragment.mUserVisibleHint)
        {
          paramFragment.mDeferStart = true;
          j = paramInt1;
          if (paramInt1 > 3) {
            j = 3;
          }
        }
      }
      paramFragment.mHost = this.mHost;
      paramFragment.mParentFragment = this.mParent;
      if (this.mParent != null) {}
      for (Object localObject1 = this.mParent.mChildFragmentManager;; localObject1 = this.mHost.getFragmentManagerImpl())
      {
        paramFragment.mFragmentManager = ((FragmentManagerImpl)localObject1);
        paramFragment.mCalled = false;
        paramFragment.onAttach(this.mHost.getContext());
        if (paramFragment.mCalled) {
          break;
        }
        throw new SuperNotCalledException("Fragment " + paramFragment + " did not call through to super.onAttach()");
      }
      label520:
      label535:
      final Object localObject3;
      if (paramFragment.mParentFragment == null)
      {
        this.mHost.onAttachFragment(paramFragment);
        if (paramFragment.mRetaining) {
          break label1063;
        }
        paramFragment.performCreate(paramFragment.mSavedFragmentState);
        paramFragment.mRetaining = false;
        i = j;
        if (paramFragment.mFromLayout)
        {
          paramFragment.mView = paramFragment.performCreateView(paramFragment.getLayoutInflater(paramFragment.mSavedFragmentState), null, paramFragment.mSavedFragmentState);
          i = j;
          if (paramFragment.mView != null)
          {
            paramFragment.mView.setSaveFromParentEnabled(false);
            if (paramFragment.mHidden) {
              paramFragment.mView.setVisibility(8);
            }
            paramFragment.onViewCreated(paramFragment.mView, paramFragment.mSavedFragmentState);
            i = j;
          }
        }
        k = i;
        if (i > 1)
        {
          if (DEBUG) {
            Log.v("FragmentManager", "moveto ACTIVITY_CREATED: " + paramFragment);
          }
          if (!paramFragment.mFromLayout)
          {
            localObject1 = null;
            if (paramFragment.mContainerId != 0)
            {
              if (paramFragment.mContainerId == -1) {
                throwException(new IllegalArgumentException("Cannot create fragment " + paramFragment + " for a container view with no id"));
              }
              localObject3 = (ViewGroup)this.mContainer.onFindViewById(paramFragment.mContainerId);
              localObject1 = localObject3;
              if (localObject3 == null)
              {
                if (!paramFragment.mRestored) {
                  break label1080;
                }
                localObject1 = localObject3;
              }
            }
          }
        }
      }
      label1063:
      label1080:
      Object localObject2;
      for (;;)
      {
        paramFragment.mContainer = ((ViewGroup)localObject1);
        paramFragment.mView = paramFragment.performCreateView(paramFragment.getLayoutInflater(paramFragment.mSavedFragmentState), (ViewGroup)localObject1, paramFragment.mSavedFragmentState);
        if (paramFragment.mView != null)
        {
          paramFragment.mView.setSaveFromParentEnabled(false);
          if (localObject1 != null)
          {
            localObject3 = loadAnimator(paramFragment, paramInt2, true, paramInt3);
            if (localObject3 != null)
            {
              ((Animator)localObject3).setTarget(paramFragment.mView);
              setHWLayerAnimListenerIfAlpha(paramFragment.mView, (Animator)localObject3);
              ((Animator)localObject3).start();
            }
            ((ViewGroup)localObject1).addView(paramFragment.mView);
          }
          if (paramFragment.mHidden) {
            paramFragment.mView.setVisibility(8);
          }
          paramFragment.onViewCreated(paramFragment.mView, paramFragment.mSavedFragmentState);
        }
        paramFragment.performActivityCreated(paramFragment.mSavedFragmentState);
        if (paramFragment.mView != null) {
          paramFragment.restoreViewState(paramFragment.mSavedFragmentState);
        }
        paramFragment.mSavedFragmentState = null;
        k = i;
        m = k;
        if (k > 2)
        {
          paramFragment.mState = 3;
          m = k;
        }
        j = m;
        if (m > 3)
        {
          if (DEBUG) {
            Log.v("FragmentManager", "moveto STARTED: " + paramFragment);
          }
          paramFragment.performStart();
          j = m;
        }
        i = j;
        if (j <= 4) {
          break;
        }
        if (DEBUG) {
          Log.v("FragmentManager", "moveto RESUMED: " + paramFragment);
        }
        paramFragment.performResume();
        paramFragment.mSavedFragmentState = null;
        paramFragment.mSavedViewState = null;
        i = j;
        break;
        paramFragment.mParentFragment.onAttachFragment(paramFragment);
        break label520;
        paramFragment.restoreChildFragmentState(paramFragment.mSavedFragmentState, true);
        paramFragment.mState = 1;
        break label535;
        try
        {
          localObject1 = paramFragment.getResources().getResourceName(paramFragment.mContainerId);
          throwException(new IllegalArgumentException("No view found for id 0x" + Integer.toHexString(paramFragment.mContainerId) + " (" + (String)localObject1 + ") for fragment " + paramFragment));
          localObject1 = localObject3;
        }
        catch (Resources.NotFoundException localNotFoundException)
        {
          for (;;)
          {
            localObject2 = "unknown";
          }
        }
      }
      i = paramInt1;
      if (paramFragment.mState > paramInt1) {
        switch (paramFragment.mState)
        {
        default: 
          i = paramInt1;
          break;
        case 1: 
        case 5: 
        case 4: 
        case 2: 
        case 3: 
          do
          {
            i = paramInt1;
            if (paramInt1 >= 1) {
              break;
            }
            if ((this.mDestroyed) && (paramFragment.mAnimatingAway != null))
            {
              localObject2 = paramFragment.mAnimatingAway;
              paramFragment.mAnimatingAway = null;
              ((Animator)localObject2).cancel();
            }
            if (paramFragment.mAnimatingAway == null) {
              break label1585;
            }
            paramFragment.mStateAfterAnimating = paramInt1;
            i = 1;
            break;
            if (paramInt1 < 5)
            {
              if (DEBUG) {
                Log.v("FragmentManager", "movefrom RESUMED: " + paramFragment);
              }
              paramFragment.performPause();
            }
            if (paramInt1 < 4)
            {
              if (DEBUG) {
                Log.v("FragmentManager", "movefrom STARTED: " + paramFragment);
              }
              paramFragment.performStop();
            }
          } while (paramInt1 >= 2);
          if (DEBUG) {
            Log.v("FragmentManager", "movefrom ACTIVITY_CREATED: " + paramFragment);
          }
          if ((paramFragment.mView != null) && (this.mHost.onShouldSaveFragmentState(paramFragment)) && (paramFragment.mSavedViewState == null)) {
            saveFragmentViewState(paramFragment);
          }
          paramFragment.performDestroyView();
          if ((paramFragment.mView != null) && (paramFragment.mContainer != null))
          {
            localObject3 = null;
            localObject2 = localObject3;
            if (this.mCurState > 0) {
              if (!this.mDestroyed) {
                break label1571;
              }
            }
          }
          label1571:
          for (localObject2 = localObject3;; localObject2 = loadAnimator(paramFragment, paramInt2, false, paramInt3))
          {
            if (localObject2 != null)
            {
              localObject3 = paramFragment.mContainer;
              final View localView = paramFragment.mView;
              ((ViewGroup)localObject3).startViewTransition(localView);
              paramFragment.mAnimatingAway = ((Animator)localObject2);
              paramFragment.mStateAfterAnimating = paramInt1;
              ((Animator)localObject2).addListener(new AnimatorListenerAdapter()
              {
                public void onAnimationEnd(Animator paramAnonymousAnimator)
                {
                  localObject3.endViewTransition(localView);
                  if (paramFragment.mAnimatingAway != null)
                  {
                    paramFragment.mAnimatingAway = null;
                    FragmentManagerImpl.this.moveToState(paramFragment, paramFragment.mStateAfterAnimating, 0, 0, false);
                  }
                }
              });
              ((Animator)localObject2).setTarget(paramFragment.mView);
              setHWLayerAnimListenerIfAlpha(paramFragment.mView, (Animator)localObject2);
              ((Animator)localObject2).start();
            }
            paramFragment.mContainer.removeView(paramFragment.mView);
            paramFragment.mContainer = null;
            paramFragment.mView = null;
            break;
          }
          label1585:
          if (DEBUG) {
            Log.v("FragmentManager", "movefrom CREATED: " + paramFragment);
          }
          if (!paramFragment.mRetaining) {
            paramFragment.performDestroy();
          }
          for (;;)
          {
            paramFragment.performDetach();
            i = paramInt1;
            if (paramBoolean) {
              break;
            }
            if (paramFragment.mRetaining) {
              break label1666;
            }
            makeInactive(paramFragment);
            i = paramInt1;
            break;
            paramFragment.mState = 0;
          }
          label1666:
          paramFragment.mHost = null;
          paramFragment.mParentFragment = null;
          paramFragment.mFragmentManager = null;
          i = paramInt1;
        }
      }
    }
  }
  
  public void noteStateNotSaved()
  {
    this.mStateSaved = false;
  }
  
  public View onCreateView(View paramView, String paramString, Context paramContext, AttributeSet paramAttributeSet)
  {
    Object localObject = null;
    if (!"fragment".equals(paramString)) {
      return null;
    }
    paramString = paramAttributeSet.getAttributeValue(null, "class");
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.Fragment);
    String str1 = paramString;
    if (paramString == null) {
      str1 = localTypedArray.getString(0);
    }
    int k = localTypedArray.getResourceId(1, -1);
    String str2 = localTypedArray.getString(2);
    localTypedArray.recycle();
    if (paramView != null) {}
    for (int i = paramView.getId(); (i == -1) && (k == -1) && (str2 == null); i = 0) {
      throw new IllegalArgumentException(paramAttributeSet.getPositionDescription() + ": Must specify unique android:id, android:tag, or have a parent with" + " an id for " + str1);
    }
    paramString = (String)localObject;
    if (k != -1) {
      paramString = findFragmentById(k);
    }
    paramView = paramString;
    if (paramString == null)
    {
      paramView = paramString;
      if (str2 != null) {
        paramView = findFragmentByTag(str2);
      }
    }
    paramString = paramView;
    if (paramView == null)
    {
      paramString = paramView;
      if (i != -1) {
        paramString = findFragmentById(i);
      }
    }
    if (DEBUG) {
      Log.v("FragmentManager", "onCreateView: id=0x" + Integer.toHexString(k) + " fname=" + str1 + " existing=" + paramString);
    }
    int j;
    if (paramString == null)
    {
      paramView = Fragment.instantiate(paramContext, str1);
      paramView.mFromLayout = true;
      if (k != 0)
      {
        j = k;
        paramView.mFragmentId = j;
        paramView.mContainerId = i;
        paramView.mTag = str2;
        paramView.mInLayout = true;
        paramView.mFragmentManager = this;
        paramView.mHost = this.mHost;
        paramView.onInflate(this.mHost.getContext(), paramAttributeSet, paramView.mSavedFragmentState);
        addFragment(paramView, true);
        label351:
        if ((this.mCurState >= 1) || (!paramView.mFromLayout)) {
          break label553;
        }
        moveToState(paramView, 1, 0, 0, false);
      }
    }
    for (;;)
    {
      if (paramView.mView != null) {
        break label561;
      }
      throw new IllegalStateException("Fragment " + str1 + " did not create a view.");
      j = i;
      break;
      if (paramString.mInLayout) {
        throw new IllegalArgumentException(paramAttributeSet.getPositionDescription() + ": Duplicate id 0x" + Integer.toHexString(k) + ", tag " + str2 + ", or parent id 0x" + Integer.toHexString(i) + " with another fragment for " + str1);
      }
      paramString.mInLayout = true;
      paramString.mHost = this.mHost;
      paramView = paramString;
      if (paramString.mRetaining) {
        break label351;
      }
      paramString.onInflate(this.mHost.getContext(), paramAttributeSet, paramString.mSavedFragmentState);
      paramView = paramString;
      break label351;
      label553:
      moveToState(paramView);
    }
    label561:
    if (k != 0) {
      paramView.mView.setId(k);
    }
    if (paramView.mView.getTag() == null) {
      paramView.mView.setTag(str2);
    }
    return paramView.mView;
  }
  
  public View onCreateView(String paramString, Context paramContext, AttributeSet paramAttributeSet)
  {
    return null;
  }
  
  public void performPendingDeferredStart(Fragment paramFragment)
  {
    if (paramFragment.mDeferStart)
    {
      if (this.mExecutingActions)
      {
        this.mHavePendingDeferredStart = true;
        return;
      }
      paramFragment.mDeferStart = false;
      moveToState(paramFragment, this.mCurState, 0, 0, false);
    }
  }
  
  public void popBackStack()
  {
    enqueueAction(new Runnable()
    {
      public void run()
      {
        FragmentManagerImpl.this.popBackStackState(FragmentManagerImpl.this.mHost.getHandler(), null, -1, 0);
      }
    }, false);
  }
  
  public void popBackStack(final int paramInt1, final int paramInt2)
  {
    if (paramInt1 < 0) {
      throw new IllegalArgumentException("Bad id: " + paramInt1);
    }
    enqueueAction(new Runnable()
    {
      public void run()
      {
        FragmentManagerImpl.this.popBackStackState(FragmentManagerImpl.this.mHost.getHandler(), null, paramInt1, paramInt2);
      }
    }, false);
  }
  
  public void popBackStack(final String paramString, final int paramInt)
  {
    enqueueAction(new Runnable()
    {
      public void run()
      {
        FragmentManagerImpl.this.popBackStackState(FragmentManagerImpl.this.mHost.getHandler(), paramString, -1, paramInt);
      }
    }, false);
  }
  
  public boolean popBackStackImmediate()
  {
    checkStateLoss();
    executePendingTransactions();
    return popBackStackState(this.mHost.getHandler(), null, -1, 0);
  }
  
  public boolean popBackStackImmediate(int paramInt1, int paramInt2)
  {
    checkStateLoss();
    executePendingTransactions();
    if (paramInt1 < 0) {
      throw new IllegalArgumentException("Bad id: " + paramInt1);
    }
    return popBackStackState(this.mHost.getHandler(), null, paramInt1, paramInt2);
  }
  
  public boolean popBackStackImmediate(String paramString, int paramInt)
  {
    checkStateLoss();
    executePendingTransactions();
    return popBackStackState(this.mHost.getHandler(), paramString, -1, paramInt);
  }
  
  boolean popBackStackState(Handler paramHandler, String paramString, int paramInt1, int paramInt2)
  {
    if (this.mBackStack == null) {
      return false;
    }
    SparseArray localSparseArray1;
    if ((paramString == null) && (paramInt1 < 0) && ((paramInt2 & 0x1) == 0))
    {
      paramInt1 = this.mBackStack.size() - 1;
      if (paramInt1 < 0) {
        return false;
      }
      paramHandler = (BackStackRecord)this.mBackStack.remove(paramInt1);
      paramString = new SparseArray();
      localSparseArray1 = new SparseArray();
      if (this.mCurState >= 1) {
        paramHandler.calculateBackFragments(paramString, localSparseArray1);
      }
      paramHandler.popFromBackStack(true, null, paramString, localSparseArray1);
      reportBackStackChanged();
    }
    for (;;)
    {
      return true;
      int i = -1;
      if ((paramString != null) || (paramInt1 >= 0))
      {
        int j = this.mBackStack.size() - 1;
        for (;;)
        {
          if (j >= 0)
          {
            paramHandler = (BackStackRecord)this.mBackStack.get(j);
            if ((paramString == null) || (!paramString.equals(paramHandler.getName()))) {
              break label162;
            }
          }
          label162:
          while ((paramInt1 >= 0) && (paramInt1 == paramHandler.mIndex))
          {
            if (j >= 0) {
              break;
            }
            return false;
          }
          j -= 1;
        }
        i = j;
        if ((paramInt2 & 0x1) != 0)
        {
          paramInt2 = j - 1;
          for (;;)
          {
            i = paramInt2;
            if (paramInt2 < 0) {
              break;
            }
            paramHandler = (BackStackRecord)this.mBackStack.get(paramInt2);
            if ((paramString == null) || (!paramString.equals(paramHandler.getName())))
            {
              i = paramInt2;
              if (paramInt1 < 0) {
                break;
              }
              i = paramInt2;
              if (paramInt1 != paramHandler.mIndex) {
                break;
              }
            }
            paramInt2 -= 1;
          }
        }
      }
      if (i == this.mBackStack.size() - 1) {
        return false;
      }
      paramString = new ArrayList();
      paramInt1 = this.mBackStack.size() - 1;
      while (paramInt1 > i)
      {
        paramString.add((BackStackRecord)this.mBackStack.remove(paramInt1));
        paramInt1 -= 1;
      }
      paramInt2 = paramString.size() - 1;
      localSparseArray1 = new SparseArray();
      SparseArray localSparseArray2 = new SparseArray();
      if (this.mCurState >= 1)
      {
        paramInt1 = 0;
        while (paramInt1 <= paramInt2)
        {
          ((BackStackRecord)paramString.get(paramInt1)).calculateBackFragments(localSparseArray1, localSparseArray2);
          paramInt1 += 1;
        }
      }
      paramHandler = null;
      paramInt1 = 0;
      if (paramInt1 <= paramInt2)
      {
        if (DEBUG) {
          Log.v("FragmentManager", "Popping back stack state: " + paramString.get(paramInt1));
        }
        BackStackRecord localBackStackRecord = (BackStackRecord)paramString.get(paramInt1);
        if (paramInt1 == paramInt2) {}
        for (boolean bool = true;; bool = false)
        {
          paramHandler = localBackStackRecord.popFromBackStack(bool, paramHandler, localSparseArray1, localSparseArray2);
          paramInt1 += 1;
          break;
        }
      }
      reportBackStackChanged();
    }
  }
  
  public void putFragment(Bundle paramBundle, String paramString, Fragment paramFragment)
  {
    if (paramFragment.mIndex < 0) {
      throwException(new IllegalStateException("Fragment " + paramFragment + " is not currently in the FragmentManager"));
    }
    paramBundle.putInt(paramString, paramFragment.mIndex);
  }
  
  public void removeFragment(Fragment paramFragment, int paramInt1, int paramInt2)
  {
    int j = 1;
    if (DEBUG) {
      Log.v("FragmentManager", "remove: " + paramFragment + " nesting=" + paramFragment.mBackStackNesting);
    }
    if (paramFragment.isInBackStack()) {}
    for (int i = 0;; i = 1)
    {
      if ((!paramFragment.mDetached) || (i != 0))
      {
        if (this.mAdded != null) {
          this.mAdded.remove(paramFragment);
        }
        if ((paramFragment.mHasMenu) && (paramFragment.mMenuVisible)) {
          this.mNeedMenuInvalidate = true;
        }
        paramFragment.mAdded = false;
        paramFragment.mRemoving = true;
        if (i != 0) {
          j = 0;
        }
        moveToState(paramFragment, j, paramInt1, paramInt2, false);
      }
      return;
    }
  }
  
  public void removeOnBackStackChangedListener(FragmentManager.OnBackStackChangedListener paramOnBackStackChangedListener)
  {
    if (this.mBackStackChangeListeners != null) {
      this.mBackStackChangeListeners.remove(paramOnBackStackChangedListener);
    }
  }
  
  void reportBackStackChanged()
  {
    if (this.mBackStackChangeListeners != null)
    {
      int i = 0;
      while (i < this.mBackStackChangeListeners.size())
      {
        ((FragmentManager.OnBackStackChangedListener)this.mBackStackChangeListeners.get(i)).onBackStackChanged();
        i += 1;
      }
    }
  }
  
  void restoreAllState(Parcelable paramParcelable, FragmentManagerNonConfig paramFragmentManagerNonConfig)
  {
    if (paramParcelable == null) {
      return;
    }
    FragmentManagerState localFragmentManagerState = (FragmentManagerState)paramParcelable;
    if (localFragmentManagerState.mActive == null) {
      return;
    }
    paramParcelable = null;
    List localList;
    Object localObject;
    int j;
    FragmentState localFragmentState;
    if (paramFragmentManagerNonConfig != null)
    {
      localList = paramFragmentManagerNonConfig.getFragments();
      localObject = paramFragmentManagerNonConfig.getChildNonConfigs();
      if (localList != null) {}
      for (i = localList.size();; i = 0)
      {
        j = 0;
        for (;;)
        {
          paramParcelable = (Parcelable)localObject;
          if (j >= i) {
            break;
          }
          paramParcelable = (Fragment)localList.get(j);
          if (DEBUG) {
            Log.v("FragmentManager", "restoreAllState: re-attaching retained " + paramParcelable);
          }
          localFragmentState = localFragmentManagerState.mActive[paramParcelable.mIndex];
          localFragmentState.mInstance = paramParcelable;
          paramParcelable.mSavedViewState = null;
          paramParcelable.mBackStackNesting = 0;
          paramParcelable.mInLayout = false;
          paramParcelable.mAdded = false;
          paramParcelable.mTarget = null;
          if (localFragmentState.mSavedFragmentState != null)
          {
            localFragmentState.mSavedFragmentState.setClassLoader(this.mHost.getContext().getClassLoader());
            paramParcelable.mSavedViewState = localFragmentState.mSavedFragmentState.getSparseParcelableArray("android:view_state");
            paramParcelable.mSavedFragmentState = localFragmentState.mSavedFragmentState;
          }
          j += 1;
        }
      }
    }
    this.mActive = new ArrayList(localFragmentManagerState.mActive.length);
    if (this.mAvailIndices != null) {
      this.mAvailIndices.clear();
    }
    int i = 0;
    if (i < localFragmentManagerState.mActive.length)
    {
      localFragmentState = localFragmentManagerState.mActive[i];
      if (localFragmentState != null)
      {
        localList = null;
        localObject = localList;
        if (paramParcelable != null)
        {
          localObject = localList;
          if (i < paramParcelable.size()) {
            localObject = (FragmentManagerNonConfig)paramParcelable.get(i);
          }
        }
        localObject = localFragmentState.instantiate(this.mHost, this.mParent, (FragmentManagerNonConfig)localObject);
        if (DEBUG) {
          Log.v("FragmentManager", "restoreAllState: active #" + i + ": " + localObject);
        }
        this.mActive.add(localObject);
        localFragmentState.mInstance = null;
      }
      for (;;)
      {
        i += 1;
        break;
        this.mActive.add(null);
        if (this.mAvailIndices == null) {
          this.mAvailIndices = new ArrayList();
        }
        if (DEBUG) {
          Log.v("FragmentManager", "restoreAllState: avail #" + i);
        }
        this.mAvailIndices.add(Integer.valueOf(i));
      }
    }
    if (paramFragmentManagerNonConfig != null)
    {
      paramParcelable = paramFragmentManagerNonConfig.getFragments();
      if (paramParcelable != null)
      {
        i = paramParcelable.size();
        j = 0;
        label488:
        if (j >= i) {
          break label606;
        }
        paramFragmentManagerNonConfig = (Fragment)paramParcelable.get(j);
        if (paramFragmentManagerNonConfig.mTargetIndex >= 0) {
          if (paramFragmentManagerNonConfig.mTargetIndex >= this.mActive.size()) {
            break label559;
          }
        }
      }
      for (paramFragmentManagerNonConfig.mTarget = ((Fragment)this.mActive.get(paramFragmentManagerNonConfig.mTargetIndex));; paramFragmentManagerNonConfig.mTarget = null)
      {
        j += 1;
        break label488;
        i = 0;
        break;
        label559:
        Log.w("FragmentManager", "Re-attaching retained fragment " + paramFragmentManagerNonConfig + " target no longer exists: " + paramFragmentManagerNonConfig.mTargetIndex);
      }
    }
    label606:
    if (localFragmentManagerState.mAdded != null)
    {
      this.mAdded = new ArrayList(localFragmentManagerState.mAdded.length);
      i = 0;
      while (i < localFragmentManagerState.mAdded.length)
      {
        paramParcelable = (Fragment)this.mActive.get(localFragmentManagerState.mAdded[i]);
        if (paramParcelable == null) {
          throwException(new IllegalStateException("No instantiated fragment for index #" + localFragmentManagerState.mAdded[i]));
        }
        paramParcelable.mAdded = true;
        if (DEBUG) {
          Log.v("FragmentManager", "restoreAllState: added #" + i + ": " + paramParcelable);
        }
        if (this.mAdded.contains(paramParcelable)) {
          throw new IllegalStateException("Already added!");
        }
        this.mAdded.add(paramParcelable);
        i += 1;
      }
    }
    this.mAdded = null;
    if (localFragmentManagerState.mBackStack != null)
    {
      this.mBackStack = new ArrayList(localFragmentManagerState.mBackStack.length);
      i = 0;
      while (i < localFragmentManagerState.mBackStack.length)
      {
        paramParcelable = localFragmentManagerState.mBackStack[i].instantiate(this);
        if (DEBUG)
        {
          Log.v("FragmentManager", "restoreAllState: back stack #" + i + " (index " + paramParcelable.mIndex + "): " + paramParcelable);
          paramFragmentManagerNonConfig = new FastPrintWriter(new LogWriter(2, "FragmentManager"), false, 1024);
          paramParcelable.dump("  ", paramFragmentManagerNonConfig, false);
          paramFragmentManagerNonConfig.flush();
        }
        this.mBackStack.add(paramParcelable);
        if (paramParcelable.mIndex >= 0) {
          setBackStackIndex(paramParcelable.mIndex, paramParcelable);
        }
        i += 1;
      }
    }
    this.mBackStack = null;
  }
  
  FragmentManagerNonConfig retainNonConfig()
  {
    Object localObject4 = null;
    Object localObject2 = null;
    Object localObject3 = null;
    Object localObject1 = null;
    if (this.mActive != null)
    {
      int i = 0;
      localObject3 = localObject1;
      localObject4 = localObject2;
      if (i < this.mActive.size())
      {
        Fragment localFragment = (Fragment)this.mActive.get(i);
        localObject4 = localObject1;
        Object localObject5 = localObject2;
        if (localFragment != null)
        {
          localObject3 = localObject2;
          if (localFragment.mRetainInstance)
          {
            localObject4 = localObject2;
            if (localObject2 == null) {
              localObject4 = new ArrayList();
            }
            ((ArrayList)localObject4).add(localFragment);
            localFragment.mRetaining = true;
            if (localFragment.mTarget == null) {
              break label254;
            }
          }
          label254:
          for (int j = localFragment.mTarget.mIndex;; j = -1)
          {
            localFragment.mTargetIndex = j;
            localObject3 = localObject4;
            if (DEBUG)
            {
              Log.v("FragmentManager", "retainNonConfig: keeping retained " + localFragment);
              localObject3 = localObject4;
            }
            int k = 0;
            j = k;
            localObject2 = localObject1;
            if (localFragment.mChildFragmentManager == null) {
              break label269;
            }
            localObject4 = localFragment.mChildFragmentManager.retainNonConfig();
            j = k;
            localObject2 = localObject1;
            if (localObject4 == null) {
              break label269;
            }
            localObject2 = localObject1;
            if (localObject1 != null) {
              break;
            }
            localObject1 = new ArrayList();
            j = 0;
            for (;;)
            {
              localObject2 = localObject1;
              if (j >= i) {
                break;
              }
              ((ArrayList)localObject1).add(null);
              j += 1;
            }
          }
          ((ArrayList)localObject2).add(localObject4);
          j = 1;
          label269:
          localObject4 = localObject2;
          localObject5 = localObject3;
          if (localObject2 != null)
          {
            if (j == 0) {
              break label309;
            }
            localObject5 = localObject3;
            localObject4 = localObject2;
          }
        }
        for (;;)
        {
          i += 1;
          localObject1 = localObject4;
          localObject2 = localObject5;
          break;
          label309:
          ((ArrayList)localObject2).add(null);
          localObject4 = localObject2;
          localObject5 = localObject3;
        }
      }
    }
    if ((localObject4 == null) && (localObject3 == null)) {
      return null;
    }
    return new FragmentManagerNonConfig((List)localObject4, (List)localObject3);
  }
  
  Parcelable saveAllState()
  {
    execPendingActions();
    this.mStateSaved = true;
    if ((this.mActive == null) || (this.mActive.size() <= 0)) {
      return null;
    }
    int m = this.mActive.size();
    FragmentState[] arrayOfFragmentState = new FragmentState[m];
    int j = 0;
    int i = 0;
    if (i < m)
    {
      localObject1 = (Fragment)this.mActive.get(i);
      int k;
      if (localObject1 != null)
      {
        if (((Fragment)localObject1).mIndex < 0) {
          throwException(new IllegalStateException("Failure saving state: active " + localObject1 + " has cleared index: " + ((Fragment)localObject1).mIndex));
        }
        k = 1;
        localObject2 = new FragmentState((Fragment)localObject1);
        arrayOfFragmentState[i] = localObject2;
        if ((((Fragment)localObject1).mState <= 0) || (((FragmentState)localObject2).mSavedFragmentState != null)) {
          break label355;
        }
        ((FragmentState)localObject2).mSavedFragmentState = saveFragmentBasicState((Fragment)localObject1);
        if (((Fragment)localObject1).mTarget != null)
        {
          if (((Fragment)localObject1).mTarget.mIndex < 0) {
            throwException(new IllegalStateException("Failure saving state: " + localObject1 + " has target not in fragment manager: " + ((Fragment)localObject1).mTarget));
          }
          if (((FragmentState)localObject2).mSavedFragmentState == null) {
            ((FragmentState)localObject2).mSavedFragmentState = new Bundle();
          }
          putFragment(((FragmentState)localObject2).mSavedFragmentState, "android:target_state", ((Fragment)localObject1).mTarget);
          if (((Fragment)localObject1).mTargetRequestCode != 0) {
            ((FragmentState)localObject2).mSavedFragmentState.putInt("android:target_req_state", ((Fragment)localObject1).mTargetRequestCode);
          }
        }
      }
      for (;;)
      {
        j = k;
        if (DEBUG)
        {
          Log.v("FragmentManager", "Saved state of " + localObject1 + ": " + ((FragmentState)localObject2).mSavedFragmentState);
          j = k;
        }
        i += 1;
        break;
        label355:
        ((FragmentState)localObject2).mSavedFragmentState = ((Fragment)localObject1).mSavedFragmentState;
      }
    }
    if (j == 0)
    {
      if (DEBUG) {
        Log.v("FragmentManager", "saveAllState: no fragments!");
      }
      return null;
    }
    Object localObject2 = null;
    Object localObject3 = null;
    Object localObject1 = localObject2;
    if (this.mAdded != null)
    {
      j = this.mAdded.size();
      localObject1 = localObject2;
      if (j > 0)
      {
        localObject2 = new int[j];
        i = 0;
        for (;;)
        {
          localObject1 = localObject2;
          if (i >= j) {
            break;
          }
          localObject2[i] = ((Fragment)this.mAdded.get(i)).mIndex;
          if (localObject2[i] < 0) {
            throwException(new IllegalStateException("Failure saving state: active " + this.mAdded.get(i) + " has cleared index: " + localObject2[i]));
          }
          if (DEBUG) {
            Log.v("FragmentManager", "saveAllState: adding fragment #" + i + ": " + this.mAdded.get(i));
          }
          i += 1;
        }
      }
    }
    localObject2 = localObject3;
    if (this.mBackStack != null)
    {
      j = this.mBackStack.size();
      localObject2 = localObject3;
      if (j > 0)
      {
        localObject3 = new BackStackState[j];
        i = 0;
        for (;;)
        {
          localObject2 = localObject3;
          if (i >= j) {
            break;
          }
          localObject3[i] = new BackStackState(this, (BackStackRecord)this.mBackStack.get(i));
          if (DEBUG) {
            Log.v("FragmentManager", "saveAllState: adding back stack #" + i + ": " + this.mBackStack.get(i));
          }
          i += 1;
        }
      }
    }
    localObject3 = new FragmentManagerState();
    ((FragmentManagerState)localObject3).mActive = arrayOfFragmentState;
    ((FragmentManagerState)localObject3).mAdded = ((int[])localObject1);
    ((FragmentManagerState)localObject3).mBackStack = ((BackStackState[])localObject2);
    return (Parcelable)localObject3;
  }
  
  Bundle saveFragmentBasicState(Fragment paramFragment)
  {
    Object localObject2 = null;
    if (this.mStateBundle == null) {
      this.mStateBundle = new Bundle();
    }
    paramFragment.performSaveInstanceState(this.mStateBundle);
    if (!this.mStateBundle.isEmpty())
    {
      localObject2 = this.mStateBundle;
      this.mStateBundle = null;
    }
    if (paramFragment.mView != null) {
      saveFragmentViewState(paramFragment);
    }
    Object localObject1 = localObject2;
    if (paramFragment.mSavedViewState != null)
    {
      localObject1 = localObject2;
      if (localObject2 == null) {
        localObject1 = new Bundle();
      }
      ((Bundle)localObject1).putSparseParcelableArray("android:view_state", paramFragment.mSavedViewState);
    }
    localObject2 = localObject1;
    if (!paramFragment.mUserVisibleHint)
    {
      localObject2 = localObject1;
      if (localObject1 == null) {
        localObject2 = new Bundle();
      }
      ((Bundle)localObject2).putBoolean("android:user_visible_hint", paramFragment.mUserVisibleHint);
    }
    return (Bundle)localObject2;
  }
  
  public Fragment.SavedState saveFragmentInstanceState(Fragment paramFragment)
  {
    Object localObject = null;
    if (paramFragment.mIndex < 0) {
      throwException(new IllegalStateException("Fragment " + paramFragment + " is not currently in the FragmentManager"));
    }
    if (paramFragment.mState > 0)
    {
      Bundle localBundle = saveFragmentBasicState(paramFragment);
      paramFragment = (Fragment)localObject;
      if (localBundle != null) {
        paramFragment = new Fragment.SavedState(localBundle);
      }
      return paramFragment;
    }
    return null;
  }
  
  void saveFragmentViewState(Fragment paramFragment)
  {
    if (paramFragment.mView == null) {
      return;
    }
    if (this.mStateArray == null) {
      this.mStateArray = new SparseArray();
    }
    for (;;)
    {
      paramFragment.mView.saveHierarchyState(this.mStateArray);
      if (this.mStateArray.size() > 0)
      {
        paramFragment.mSavedViewState = this.mStateArray;
        this.mStateArray = null;
      }
      return;
      this.mStateArray.clear();
    }
  }
  
  /* Error */
  public void setBackStackIndex(int paramInt, BackStackRecord paramBackStackRecord)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 310	android/app/FragmentManagerImpl:mBackStackIndices	Ljava/util/ArrayList;
    //   6: ifnonnull +14 -> 20
    //   9: aload_0
    //   10: new 250	java/util/ArrayList
    //   13: dup
    //   14: invokespecial 251	java/util/ArrayList:<init>	()V
    //   17: putfield 310	android/app/FragmentManagerImpl:mBackStackIndices	Ljava/util/ArrayList;
    //   20: aload_0
    //   21: getfield 310	android/app/FragmentManagerImpl:mBackStackIndices	Ljava/util/ArrayList;
    //   24: invokevirtual 308	java/util/ArrayList:size	()I
    //   27: istore 4
    //   29: iload 4
    //   31: istore_3
    //   32: iload_1
    //   33: iload 4
    //   35: if_icmpge +58 -> 93
    //   38: getstatic 85	android/app/FragmentManagerImpl:DEBUG	Z
    //   41: ifeq +39 -> 80
    //   44: ldc 28
    //   46: new 114	java/lang/StringBuilder
    //   49: dup
    //   50: invokespecial 115	java/lang/StringBuilder:<init>	()V
    //   53: ldc_w 312
    //   56: invokevirtual 121	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   59: iload_1
    //   60: invokevirtual 315	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   63: ldc_w 317
    //   66: invokevirtual 121	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   69: aload_2
    //   70: invokevirtual 266	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   73: invokevirtual 125	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   76: invokestatic 269	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   79: pop
    //   80: aload_0
    //   81: getfield 310	android/app/FragmentManagerImpl:mBackStackIndices	Ljava/util/ArrayList;
    //   84: iload_1
    //   85: aload_2
    //   86: invokevirtual 333	java/util/ArrayList:set	(ILjava/lang/Object;)Ljava/lang/Object;
    //   89: pop
    //   90: aload_0
    //   91: monitorexit
    //   92: return
    //   93: iload_3
    //   94: iload_1
    //   95: if_icmpge +81 -> 176
    //   98: aload_0
    //   99: getfield 310	android/app/FragmentManagerImpl:mBackStackIndices	Ljava/util/ArrayList;
    //   102: aconst_null
    //   103: invokevirtual 254	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   106: pop
    //   107: aload_0
    //   108: getfield 307	android/app/FragmentManagerImpl:mAvailBackStackIndices	Ljava/util/ArrayList;
    //   111: ifnonnull +14 -> 125
    //   114: aload_0
    //   115: new 250	java/util/ArrayList
    //   118: dup
    //   119: invokespecial 251	java/util/ArrayList:<init>	()V
    //   122: putfield 307	android/app/FragmentManagerImpl:mAvailBackStackIndices	Ljava/util/ArrayList;
    //   125: getstatic 85	android/app/FragmentManagerImpl:DEBUG	Z
    //   128: ifeq +29 -> 157
    //   131: ldc 28
    //   133: new 114	java/lang/StringBuilder
    //   136: dup
    //   137: invokespecial 115	java/lang/StringBuilder:<init>	()V
    //   140: ldc_w 1250
    //   143: invokevirtual 121	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   146: iload_3
    //   147: invokevirtual 315	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   150: invokevirtual 125	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   153: invokestatic 269	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   156: pop
    //   157: aload_0
    //   158: getfield 307	android/app/FragmentManagerImpl:mAvailBackStackIndices	Ljava/util/ArrayList;
    //   161: iload_3
    //   162: invokestatic 610	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   165: invokevirtual 254	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   168: pop
    //   169: iload_3
    //   170: iconst_1
    //   171: iadd
    //   172: istore_3
    //   173: goto -80 -> 93
    //   176: getstatic 85	android/app/FragmentManagerImpl:DEBUG	Z
    //   179: ifeq +39 -> 218
    //   182: ldc 28
    //   184: new 114	java/lang/StringBuilder
    //   187: dup
    //   188: invokespecial 115	java/lang/StringBuilder:<init>	()V
    //   191: ldc_w 327
    //   194: invokevirtual 121	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   197: iload_1
    //   198: invokevirtual 315	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   201: ldc_w 329
    //   204: invokevirtual 121	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   207: aload_2
    //   208: invokevirtual 266	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   211: invokevirtual 125	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   214: invokestatic 269	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   217: pop
    //   218: aload_0
    //   219: getfield 310	android/app/FragmentManagerImpl:mBackStackIndices	Ljava/util/ArrayList;
    //   222: aload_2
    //   223: invokevirtual 254	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   226: pop
    //   227: goto -137 -> 90
    //   230: astore_2
    //   231: aload_0
    //   232: monitorexit
    //   233: aload_2
    //   234: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	235	0	this	FragmentManagerImpl
    //   0	235	1	paramInt	int
    //   0	235	2	paramBackStackRecord	BackStackRecord
    //   31	142	3	i	int
    //   27	9	4	j	int
    // Exception table:
    //   from	to	target	type
    //   2	20	230	finally
    //   20	29	230	finally
    //   38	80	230	finally
    //   80	90	230	finally
    //   98	125	230	finally
    //   125	157	230	finally
    //   157	169	230	finally
    //   176	218	230	finally
    //   218	227	230	finally
  }
  
  public void showFragment(Fragment paramFragment, int paramInt1, int paramInt2)
  {
    if (DEBUG) {
      Log.v("FragmentManager", "show: " + paramFragment);
    }
    if (paramFragment.mHidden)
    {
      paramFragment.mHidden = false;
      if (paramFragment.mView != null)
      {
        Animator localAnimator = loadAnimator(paramFragment, paramInt1, true, paramInt2);
        if (localAnimator != null)
        {
          localAnimator.setTarget(paramFragment.mView);
          setHWLayerAnimListenerIfAlpha(paramFragment.mView, localAnimator);
          localAnimator.start();
        }
        paramFragment.mView.setVisibility(0);
      }
      if ((paramFragment.mAdded) && (paramFragment.mHasMenu) && (paramFragment.mMenuVisible)) {
        this.mNeedMenuInvalidate = true;
      }
      paramFragment.onHiddenChanged(false);
    }
  }
  
  void startPendingDeferredFragments()
  {
    if (this.mActive == null) {
      return;
    }
    int i = 0;
    while (i < this.mActive.size())
    {
      Fragment localFragment = (Fragment)this.mActive.get(i);
      if (localFragment != null) {
        performPendingDeferredStart(localFragment);
      }
      i += 1;
    }
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    localStringBuilder.append("FragmentManager{");
    localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
    localStringBuilder.append(" in ");
    if (this.mParent != null) {
      DebugUtils.buildShortClassTag(this.mParent, localStringBuilder);
    }
    for (;;)
    {
      localStringBuilder.append("}}");
      return localStringBuilder.toString();
      DebugUtils.buildShortClassTag(this.mHost, localStringBuilder);
    }
  }
  
  static class AnimateOnHWLayerIfNeededListener
    implements Animator.AnimatorListener
  {
    private boolean mShouldRunOnHWLayer = false;
    private View mView;
    
    public AnimateOnHWLayerIfNeededListener(View paramView)
    {
      if (paramView == null) {
        return;
      }
      this.mView = paramView;
    }
    
    public void onAnimationCancel(Animator paramAnimator) {}
    
    public void onAnimationEnd(Animator paramAnimator)
    {
      if (this.mShouldRunOnHWLayer) {
        this.mView.setLayerType(0, null);
      }
      this.mView = null;
      paramAnimator.removeListener(this);
    }
    
    public void onAnimationRepeat(Animator paramAnimator) {}
    
    public void onAnimationStart(Animator paramAnimator)
    {
      this.mShouldRunOnHWLayer = FragmentManagerImpl.shouldRunOnHWLayer(this.mView, paramAnimator);
      if (this.mShouldRunOnHWLayer) {
        this.mView.setLayerType(2, null);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/FragmentManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */