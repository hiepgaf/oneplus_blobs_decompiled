package android.support.v4.app;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.v4.util.DebugUtils;
import android.support.v4.util.LogWriter;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater.Factory;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class FragmentManagerImpl
  extends FragmentManager
  implements LayoutInflater.Factory
{
  static final Interpolator ACCELERATE_CUBIC;
  static final Interpolator ACCELERATE_QUINT;
  static final int ANIM_DUR = 220;
  public static final int ANIM_STYLE_CLOSE_ENTER = 3;
  public static final int ANIM_STYLE_CLOSE_EXIT = 4;
  public static final int ANIM_STYLE_FADE_ENTER = 5;
  public static final int ANIM_STYLE_FADE_EXIT = 6;
  public static final int ANIM_STYLE_OPEN_ENTER = 1;
  public static final int ANIM_STYLE_OPEN_EXIT = 2;
  static boolean DEBUG = false;
  static final Interpolator DECELERATE_CUBIC;
  static final Interpolator DECELERATE_QUINT;
  static final boolean HONEYCOMB;
  static final String TAG = "FragmentManager";
  static final String TARGET_REQUEST_CODE_STATE_TAG = "android:target_req_state";
  static final String TARGET_STATE_TAG = "android:target_state";
  static final String USER_VISIBLE_HINT_TAG = "android:user_visible_hint";
  static final String VIEW_STATE_TAG = "android:view_state";
  ArrayList<Fragment> mActive;
  FragmentActivity mActivity;
  ArrayList<Fragment> mAdded;
  ArrayList<Integer> mAvailBackStackIndices;
  ArrayList<Integer> mAvailIndices;
  ArrayList<BackStackRecord> mBackStack;
  ArrayList<FragmentManager.OnBackStackChangedListener> mBackStackChangeListeners;
  ArrayList<BackStackRecord> mBackStackIndices;
  FragmentContainer mContainer;
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
  boolean mNeedMenuInvalidate;
  String mNoTransactionsBecause;
  Fragment mParent;
  ArrayList<Runnable> mPendingActions;
  SparseArray<Parcelable> mStateArray = null;
  Bundle mStateBundle = null;
  boolean mStateSaved;
  Runnable[] mTmpActions;
  
  static
  {
    boolean bool = false;
    DEBUG = false;
    if (Build.VERSION.SDK_INT < 11) {}
    for (;;)
    {
      HONEYCOMB = bool;
      DECELERATE_QUINT = new DecelerateInterpolator(2.5F);
      DECELERATE_CUBIC = new DecelerateInterpolator(1.5F);
      ACCELERATE_QUINT = new AccelerateInterpolator(2.5F);
      ACCELERATE_CUBIC = new AccelerateInterpolator(1.5F);
      return;
      bool = true;
    }
  }
  
  private void checkStateLoss()
  {
    if (!this.mStateSaved)
    {
      if (this.mNoTransactionsBecause != null) {}
    }
    else {
      throw new IllegalStateException("Can not perform this action after onSaveInstanceState");
    }
    throw new IllegalStateException("Can not perform this action inside of " + this.mNoTransactionsBecause);
  }
  
  static Animation makeFadeAnimation(Context paramContext, float paramFloat1, float paramFloat2)
  {
    paramContext = new AlphaAnimation(paramFloat1, paramFloat2);
    paramContext.setInterpolator(DECELERATE_CUBIC);
    paramContext.setDuration(220L);
    return paramContext;
  }
  
  static Animation makeOpenCloseAnimation(Context paramContext, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    paramContext = new AnimationSet(false);
    Object localObject = new ScaleAnimation(paramFloat1, paramFloat2, paramFloat1, paramFloat2, 1, 0.5F, 1, 0.5F);
    ((ScaleAnimation)localObject).setInterpolator(DECELERATE_QUINT);
    ((ScaleAnimation)localObject).setDuration(220L);
    paramContext.addAnimation((Animation)localObject);
    localObject = new AlphaAnimation(paramFloat3, paramFloat4);
    ((AlphaAnimation)localObject).setInterpolator(DECELERATE_CUBIC);
    ((AlphaAnimation)localObject).setDuration(220L);
    paramContext.addAnimation((Animation)localObject);
    return paramContext;
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
  
  private void throwException(RuntimeException paramRuntimeException)
  {
    Log.e("FragmentManager", paramRuntimeException.getMessage());
    Log.e("FragmentManager", "Activity state:");
    PrintWriter localPrintWriter = new PrintWriter(new LogWriter("FragmentManager"));
    if (this.mActivity == null) {}
    try
    {
      dump("  ", null, localPrintWriter, new String[0]);
      for (;;)
      {
        throw paramRuntimeException;
        try
        {
          this.mActivity.dump("  ", null, localPrintWriter, new String[0]);
        }
        catch (Exception localException1)
        {
          Log.e("FragmentManager", "Failed dumping state", localException1);
        }
      }
    }
    catch (Exception localException2)
    {
      for (;;)
      {
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
      if (!paramBoolean) {
        return 2;
      }
      return 1;
    case 8194: 
      if (!paramBoolean) {
        return 4;
      }
      return 3;
    }
    if (!paramBoolean) {
      return 6;
    }
    return 5;
  }
  
  void addBackStackState(BackStackRecord paramBackStackRecord)
  {
    if (this.mBackStack != null) {}
    for (;;)
    {
      this.mBackStack.add(paramBackStackRecord);
      reportBackStackChanged();
      return;
      this.mBackStack = new ArrayList();
    }
  }
  
  public void addFragment(Fragment paramFragment, boolean paramBoolean)
  {
    if (this.mAdded != null)
    {
      if (DEBUG) {
        break label40;
      }
      label13:
      makeActive(paramFragment);
      if (!paramFragment.mDetached) {
        break label69;
      }
    }
    for (;;)
    {
      return;
      this.mAdded = new ArrayList();
      break;
      label40:
      Log.v("FragmentManager", "add: " + paramFragment);
      break label13;
      label69:
      if (!this.mAdded.contains(paramFragment))
      {
        this.mAdded.add(paramFragment);
        paramFragment.mAdded = true;
        paramFragment.mRemoving = false;
        if (paramFragment.mHasMenu) {
          break label144;
        }
      }
      while (paramBoolean)
      {
        moveToState(paramFragment);
        return;
        throw new IllegalStateException("Fragment already added: " + paramFragment);
        label144:
        if (paramFragment.mMenuVisible) {
          this.mNeedMenuInvalidate = true;
        }
      }
    }
  }
  
  public void addOnBackStackChangedListener(FragmentManager.OnBackStackChangedListener paramOnBackStackChangedListener)
  {
    if (this.mBackStackChangeListeners != null) {}
    for (;;)
    {
      this.mBackStackChangeListeners.add(paramOnBackStackChangedListener);
      return;
      this.mBackStackChangeListeners = new ArrayList();
    }
  }
  
  public int allocBackStackIndex(BackStackRecord paramBackStackRecord)
  {
    for (;;)
    {
      int i;
      try
      {
        if (this.mAvailBackStackIndices == null)
        {
          if (this.mBackStackIndices != null)
          {
            i = this.mBackStackIndices.size();
            if (DEBUG) {
              break label115;
            }
            this.mBackStackIndices.add(paramBackStackRecord);
            return i;
          }
        }
        else
        {
          if (this.mAvailBackStackIndices.size() <= 0) {
            continue;
          }
          i = ((Integer)this.mAvailBackStackIndices.remove(this.mAvailBackStackIndices.size() - 1)).intValue();
          if (DEBUG) {
            break label154;
          }
          this.mBackStackIndices.set(i, paramBackStackRecord);
          return i;
        }
        this.mBackStackIndices = new ArrayList();
        continue;
        Log.v("FragmentManager", "Setting back stack index " + i + " to " + paramBackStackRecord);
      }
      finally {}
      label115:
      continue;
      label154:
      Log.v("FragmentManager", "Adding back stack index " + i + " with " + paramBackStackRecord);
    }
  }
  
  public void attachActivity(FragmentActivity paramFragmentActivity, FragmentContainer paramFragmentContainer, Fragment paramFragment)
  {
    if (this.mActivity == null)
    {
      this.mActivity = paramFragmentActivity;
      this.mContainer = paramFragmentContainer;
      this.mParent = paramFragment;
      return;
    }
    throw new IllegalStateException("Already attached");
  }
  
  public void attachFragment(Fragment paramFragment, int paramInt1, int paramInt2)
  {
    if (!DEBUG) {
      if (paramFragment.mDetached) {
        break label43;
      }
    }
    label43:
    do
    {
      return;
      Log.v("FragmentManager", "attach: " + paramFragment);
      break;
      paramFragment.mDetached = false;
    } while (paramFragment.mAdded);
    if (this.mAdded != null)
    {
      if (this.mAdded.contains(paramFragment)) {
        break label127;
      }
      if (DEBUG) {
        break label155;
      }
      label79:
      this.mAdded.add(paramFragment);
      paramFragment.mAdded = true;
      if (paramFragment.mHasMenu) {
        break label184;
      }
    }
    for (;;)
    {
      moveToState(paramFragment, this.mCurState, paramInt1, paramInt2, false);
      return;
      this.mAdded = new ArrayList();
      break;
      label127:
      throw new IllegalStateException("Fragment already added: " + paramFragment);
      label155:
      Log.v("FragmentManager", "add from attach: " + paramFragment);
      break label79;
      label184:
      if (paramFragment.mMenuVisible) {
        this.mNeedMenuInvalidate = true;
      }
    }
  }
  
  public FragmentTransaction beginTransaction()
  {
    return new BackStackRecord(this);
  }
  
  public void detachFragment(Fragment paramFragment, int paramInt1, int paramInt2)
  {
    if (!DEBUG) {
      if (!paramFragment.mDetached) {
        break label43;
      }
    }
    label43:
    do
    {
      return;
      Log.v("FragmentManager", "detach: " + paramFragment);
      break;
      paramFragment.mDetached = true;
    } while (!paramFragment.mAdded);
    if (this.mAdded == null) {
      if (paramFragment.mHasMenu) {
        break label131;
      }
    }
    for (;;)
    {
      paramFragment.mAdded = false;
      moveToState(paramFragment, 1, paramInt1, paramInt2, false);
      return;
      if (!DEBUG) {}
      for (;;)
      {
        this.mAdded.remove(paramFragment);
        break;
        Log.v("FragmentManager", "remove from detach: " + paramFragment);
      }
      label131:
      if (paramFragment.mMenuVisible) {
        this.mNeedMenuInvalidate = true;
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
    if (this.mAdded == null) {
      return;
    }
    int i = 0;
    label10:
    Fragment localFragment;
    if (i < this.mAdded.size())
    {
      localFragment = (Fragment)this.mAdded.get(i);
      if (localFragment != null) {
        break label44;
      }
    }
    for (;;)
    {
      i += 1;
      break label10;
      break;
      label44:
      localFragment.performConfigurationChanged(paramConfiguration);
    }
  }
  
  public boolean dispatchContextItemSelected(MenuItem paramMenuItem)
  {
    if (this.mAdded == null) {
      return false;
    }
    int i = 0;
    label11:
    Fragment localFragment;
    if (i < this.mAdded.size())
    {
      localFragment = (Fragment)this.mAdded.get(i);
      if (localFragment != null) {
        break label45;
      }
    }
    label45:
    while (!localFragment.performContextItemSelected(paramMenuItem))
    {
      i += 1;
      break label11;
      break;
    }
    return true;
  }
  
  public void dispatchCreate()
  {
    this.mStateSaved = false;
    moveToState(1, false);
  }
  
  public boolean dispatchCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    int j = 0;
    Object localObject2;
    boolean bool2;
    if (this.mAdded == null)
    {
      localObject2 = null;
      bool2 = false;
    }
    int i;
    Object localObject1;
    do
    {
      i = j;
      if (this.mCreatedMenus != null) {
        break;
      }
      this.mCreatedMenus = ((ArrayList)localObject2);
      return bool2;
      i = 0;
      localObject1 = null;
      bool1 = false;
      localObject2 = localObject1;
      bool2 = bool1;
    } while (i >= this.mAdded.size());
    Fragment localFragment = (Fragment)this.mAdded.get(i);
    if (localFragment == null) {
      localObject2 = localObject1;
    }
    do
    {
      i += 1;
      localObject1 = localObject2;
      break;
      localObject2 = localObject1;
    } while (!localFragment.performCreateOptionsMenu(paramMenu, paramMenuInflater));
    boolean bool1 = true;
    if (localObject1 != null) {}
    for (;;)
    {
      ((ArrayList)localObject1).add(localFragment);
      localObject2 = localObject1;
      break;
      localObject1 = new ArrayList();
    }
    label144:
    if (((ArrayList)localObject2).contains(paramMenu)) {}
    for (;;)
    {
      i += 1;
      if (i >= this.mCreatedMenus.size()) {
        break;
      }
      paramMenu = (Fragment)this.mCreatedMenus.get(i);
      if (localObject2 != null) {
        break label144;
      }
      paramMenu.onDestroyOptionsMenu();
    }
  }
  
  public void dispatchDestroy()
  {
    this.mDestroyed = true;
    execPendingActions();
    moveToState(0, false);
    this.mActivity = null;
    this.mContainer = null;
    this.mParent = null;
  }
  
  public void dispatchDestroyView()
  {
    moveToState(1, false);
  }
  
  public void dispatchLowMemory()
  {
    if (this.mAdded == null) {
      return;
    }
    int i = 0;
    label10:
    Fragment localFragment;
    if (i < this.mAdded.size())
    {
      localFragment = (Fragment)this.mAdded.get(i);
      if (localFragment != null) {
        break label44;
      }
    }
    for (;;)
    {
      i += 1;
      break label10;
      break;
      label44:
      localFragment.performLowMemory();
    }
  }
  
  public boolean dispatchOptionsItemSelected(MenuItem paramMenuItem)
  {
    if (this.mAdded == null) {
      return false;
    }
    int i = 0;
    label11:
    Fragment localFragment;
    if (i < this.mAdded.size())
    {
      localFragment = (Fragment)this.mAdded.get(i);
      if (localFragment != null) {
        break label45;
      }
    }
    label45:
    while (!localFragment.performOptionsItemSelected(paramMenuItem))
    {
      i += 1;
      break label11;
      break;
    }
    return true;
  }
  
  public void dispatchOptionsMenuClosed(Menu paramMenu)
  {
    if (this.mAdded == null) {
      return;
    }
    int i = 0;
    label10:
    Fragment localFragment;
    if (i < this.mAdded.size())
    {
      localFragment = (Fragment)this.mAdded.get(i);
      if (localFragment != null) {
        break label44;
      }
    }
    for (;;)
    {
      i += 1;
      break label10;
      break;
      label44:
      localFragment.performOptionsMenuClosed(paramMenu);
    }
  }
  
  public void dispatchPause()
  {
    moveToState(4, false);
  }
  
  public boolean dispatchPrepareOptionsMenu(Menu paramMenu)
  {
    if (this.mAdded == null) {
      return false;
    }
    int i = 0;
    boolean bool = false;
    if (i >= this.mAdded.size()) {
      return bool;
    }
    Fragment localFragment = (Fragment)this.mAdded.get(i);
    if (localFragment == null) {}
    for (;;)
    {
      i += 1;
      break;
      if (localFragment.performPrepareOptionsMenu(paramMenu)) {
        bool = true;
      }
    }
  }
  
  public void dispatchReallyStop()
  {
    moveToState(2, false);
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
    this.mStateSaved = true;
    moveToState(3, false);
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    int j = 0;
    String str = paramString + "    ";
    if (this.mActive == null)
    {
      if (this.mAdded != null) {
        break label336;
      }
      label39:
      if (this.mCreatedMenus != null) {
        break label429;
      }
      label46:
      if (this.mBackStack != null) {
        break label522;
      }
    }
    label53:
    label336:
    label429:
    label522:
    label758:
    label847:
    label870:
    label893:
    label916:
    do
    {
      for (;;)
      {
        int k;
        int i;
        try
        {
          if (this.mBackStackIndices == null)
          {
            if (this.mAvailBackStackIndices != null) {
              continue;
            }
            if (this.mPendingActions != null) {
              break label758;
            }
            paramPrintWriter.print(paramString);
            paramPrintWriter.println("FragmentManager misc state:");
            paramPrintWriter.print(paramString);
            paramPrintWriter.print("  mActivity=");
            paramPrintWriter.println(this.mActivity);
            paramPrintWriter.print(paramString);
            paramPrintWriter.print("  mContainer=");
            paramPrintWriter.println(this.mContainer);
            if (this.mParent != null) {
              break label847;
            }
            paramPrintWriter.print(paramString);
            paramPrintWriter.print("  mCurState=");
            paramPrintWriter.print(this.mCurState);
            paramPrintWriter.print(" mStateSaved=");
            paramPrintWriter.print(this.mStateSaved);
            paramPrintWriter.print(" mDestroyed=");
            paramPrintWriter.println(this.mDestroyed);
            if (this.mNeedMenuInvalidate) {
              break label870;
            }
            if (this.mNoTransactionsBecause != null) {
              break label893;
            }
            if (this.mAvailIndices != null) {
              break label916;
            }
            return;
            k = this.mActive.size();
            if (k <= 0) {
              break;
            }
            paramPrintWriter.print(paramString);
            paramPrintWriter.print("Active Fragments in ");
            paramPrintWriter.print(Integer.toHexString(System.identityHashCode(this)));
            paramPrintWriter.println(":");
            i = 0;
            if (i >= k) {
              break;
            }
            Object localObject = (Fragment)this.mActive.get(i);
            paramPrintWriter.print(paramString);
            paramPrintWriter.print("  #");
            paramPrintWriter.print(i);
            paramPrintWriter.print(": ");
            paramPrintWriter.println(localObject);
            if (localObject == null)
            {
              i += 1;
              continue;
            }
            ((Fragment)localObject).dump(str, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
            continue;
            k = this.mAdded.size();
            if (k <= 0) {
              break label39;
            }
            paramPrintWriter.print(paramString);
            paramPrintWriter.println("Added Fragments:");
            i = 0;
            if (i >= k) {
              break label39;
            }
            localObject = (Fragment)this.mAdded.get(i);
            paramPrintWriter.print(paramString);
            paramPrintWriter.print("  #");
            paramPrintWriter.print(i);
            paramPrintWriter.print(": ");
            paramPrintWriter.println(((Fragment)localObject).toString());
            i += 1;
            continue;
            k = this.mCreatedMenus.size();
            if (k <= 0) {
              break label46;
            }
            paramPrintWriter.print(paramString);
            paramPrintWriter.println("Fragments Created Menus:");
            i = 0;
            if (i >= k) {
              break label46;
            }
            localObject = (Fragment)this.mCreatedMenus.get(i);
            paramPrintWriter.print(paramString);
            paramPrintWriter.print("  #");
            paramPrintWriter.print(i);
            paramPrintWriter.print(": ");
            paramPrintWriter.println(((Fragment)localObject).toString());
            i += 1;
            continue;
            k = this.mBackStack.size();
            if (k <= 0) {
              break label53;
            }
            paramPrintWriter.print(paramString);
            paramPrintWriter.println("Back Stack:");
            i = 0;
            if (i >= k) {
              break label53;
            }
            localObject = (BackStackRecord)this.mBackStack.get(i);
            paramPrintWriter.print(paramString);
            paramPrintWriter.print("  #");
            paramPrintWriter.print(i);
            paramPrintWriter.print(": ");
            paramPrintWriter.println(((BackStackRecord)localObject).toString());
            ((BackStackRecord)localObject).dump(str, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
            i += 1;
            continue;
          }
          k = this.mBackStackIndices.size();
          if (k <= 0) {
            continue;
          }
          paramPrintWriter.print(paramString);
          paramPrintWriter.println("Back Stack Indices:");
          i = 0;
          if (i < k)
          {
            paramFileDescriptor = (BackStackRecord)this.mBackStackIndices.get(i);
            paramPrintWriter.print(paramString);
            paramPrintWriter.print("  #");
            paramPrintWriter.print(i);
            paramPrintWriter.print(": ");
            paramPrintWriter.println(paramFileDescriptor);
            i += 1;
            continue;
          }
          continue;
          if (this.mAvailBackStackIndices.size() <= 0) {
            continue;
          }
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("mAvailBackStackIndices: ");
          paramPrintWriter.println(Arrays.toString(this.mAvailBackStackIndices.toArray()));
          continue;
          k = this.mPendingActions.size();
        }
        finally {}
        if (k > 0)
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.println("Pending Actions:");
          i = j;
          while (i < k)
          {
            paramFileDescriptor = (Runnable)this.mPendingActions.get(i);
            paramPrintWriter.print(paramString);
            paramPrintWriter.print("  #");
            paramPrintWriter.print(i);
            paramPrintWriter.print(": ");
            paramPrintWriter.println(paramFileDescriptor);
            i += 1;
          }
          continue;
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  mParent=");
          paramPrintWriter.println(this.mParent);
          continue;
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  mNeedMenuInvalidate=");
          paramPrintWriter.println(this.mNeedMenuInvalidate);
          continue;
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  mNoTransactionsBecause=");
          paramPrintWriter.println(this.mNoTransactionsBecause);
        }
      }
    } while (this.mAvailIndices.size() <= 0);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("  mAvailIndices: ");
    paramPrintWriter.println(Arrays.toString(this.mAvailIndices.toArray()));
  }
  
  public void enqueueAction(Runnable paramRunnable, boolean paramBoolean)
  {
    if (paramBoolean) {}
    try
    {
      if (this.mDestroyed) {
        throw new IllegalStateException("Activity has been destroyed");
      }
    }
    finally
    {
      do
      {
        throw paramRunnable;
        checkStateLoss();
        break;
      } while (this.mActivity == null);
      if (this.mPendingActions != null) {}
      for (;;)
      {
        this.mPendingActions.add(paramRunnable);
        if (this.mPendingActions.size() == 1) {
          break;
        }
        return;
        this.mPendingActions = new ArrayList();
      }
      this.mActivity.mHandler.removeCallbacks(this.mExecCommit);
    }
  }
  
  public boolean execPendingActions()
  {
    boolean bool2;
    if (!this.mExecutingActions)
    {
      if (Looper.myLooper() != this.mActivity.mHandler.getLooper()) {
        break label56;
      }
      bool2 = false;
    }
    label56:
    int j;
    int i;
    label176:
    label201:
    do
    {
      for (;;)
      {
        try
        {
          if (this.mPendingActions == null)
          {
            if (this.mHavePendingDeferredStart) {
              break label201;
            }
            return bool2;
            throw new IllegalStateException("Recursive entry to executePendingTransactions");
            throw new IllegalStateException("Must be called from main thread of process");
          }
          if (this.mPendingActions.size() == 0) {
            continue;
          }
          j = this.mPendingActions.size();
          if (this.mTmpActions == null)
          {
            this.mTmpActions = new Runnable[j];
            this.mPendingActions.toArray(this.mTmpActions);
            this.mPendingActions.clear();
            this.mActivity.mHandler.removeCallbacks(this.mExecCommit);
            this.mExecutingActions = true;
            i = 0;
            if (i < j) {
              break label176;
            }
            this.mExecutingActions = false;
            bool2 = true;
            break;
          }
          if (this.mTmpActions.length < j) {
            continue;
          }
          continue;
          this.mTmpActions[i].run();
        }
        finally {}
        this.mTmpActions[i] = null;
        i += 1;
      }
      i = 0;
      j = 0;
      if (j < this.mActive.size()) {
        break label231;
      }
    } while (i != 0);
    this.mHavePendingDeferredStart = false;
    startPendingDeferredFragments();
    return bool2;
    label231:
    Fragment localFragment = (Fragment)this.mActive.get(j);
    if (localFragment == null) {}
    for (;;)
    {
      j += 1;
      break;
      if (localFragment.mLoaderManager != null)
      {
        boolean bool1;
        i |= localFragment.mLoaderManager.hasRunningLoaders();
      }
    }
  }
  
  public boolean executePendingTransactions()
  {
    return execPendingActions();
  }
  
  public Fragment findFragmentById(int paramInt)
  {
    label26:
    Fragment localFragment;
    if (this.mAdded == null)
    {
      if (this.mActive == null) {
        return null;
      }
    }
    else
    {
      i = this.mAdded.size() - 1;
      if (i >= 0)
      {
        localFragment = (Fragment)this.mAdded.get(i);
        if (localFragment != null) {
          break label53;
        }
      }
      label53:
      while (localFragment.mFragmentId != paramInt)
      {
        i -= 1;
        break label26;
        break;
      }
      return localFragment;
    }
    int i = this.mActive.size() - 1;
    label73:
    if (i >= 0)
    {
      localFragment = (Fragment)this.mActive.get(i);
      if (localFragment != null) {
        break label100;
      }
    }
    label100:
    while (localFragment.mFragmentId != paramInt)
    {
      i -= 1;
      break label73;
      break;
    }
    return localFragment;
  }
  
  public Fragment findFragmentByTag(String paramString)
  {
    if (this.mAdded == null) {
      if (this.mActive != null) {
        break label70;
      }
    }
    label30:
    Fragment localFragment;
    label57:
    label70:
    while (paramString == null)
    {
      return null;
      if (paramString == null) {
        break;
      }
      i = this.mAdded.size() - 1;
      if (i >= 0)
      {
        localFragment = (Fragment)this.mAdded.get(i);
        if (localFragment != null) {
          break label57;
        }
      }
      while (!paramString.equals(localFragment.mTag))
      {
        i -= 1;
        break label30;
        break;
      }
      return localFragment;
    }
    int i = this.mActive.size() - 1;
    label84:
    if (i >= 0)
    {
      localFragment = (Fragment)this.mActive.get(i);
      if (localFragment != null) {
        break label111;
      }
    }
    label111:
    while (!paramString.equals(localFragment.mTag))
    {
      i -= 1;
      break label84;
      break;
    }
    return localFragment;
  }
  
  public Fragment findFragmentByWho(String paramString)
  {
    if (this.mActive == null) {}
    while (paramString == null) {
      return null;
    }
    int i = this.mActive.size() - 1;
    label23:
    Fragment localFragment;
    if (i >= 0)
    {
      localFragment = (Fragment)this.mActive.get(i);
      if (localFragment != null) {
        break label50;
      }
    }
    label50:
    do
    {
      i -= 1;
      break label23;
      break;
      localFragment = localFragment.findFragmentByWho(paramString);
    } while (localFragment == null);
    return localFragment;
  }
  
  public void freeBackStackIndex(int paramInt)
  {
    for (;;)
    {
      try
      {
        this.mBackStackIndices.set(paramInt, null);
        if (this.mAvailBackStackIndices != null)
        {
          if (!DEBUG) {
            this.mAvailBackStackIndices.add(Integer.valueOf(paramInt));
          }
        }
        else
        {
          this.mAvailBackStackIndices = new ArrayList();
          continue;
        }
        Log.v("FragmentManager", "Freeing back stack index " + paramInt);
      }
      finally {}
    }
  }
  
  public FragmentManager.BackStackEntry getBackStackEntryAt(int paramInt)
  {
    return (FragmentManager.BackStackEntry)this.mBackStack.get(paramInt);
  }
  
  public int getBackStackEntryCount()
  {
    if (this.mBackStack == null) {
      return 0;
    }
    return this.mBackStack.size();
  }
  
  public Fragment getFragment(Bundle paramBundle, String paramString)
  {
    int i = paramBundle.getInt(paramString, -1);
    if (i != -1) {
      if (i >= this.mActive.size()) {
        break label43;
      }
    }
    for (;;)
    {
      paramBundle = (Fragment)this.mActive.get(i);
      if (paramBundle == null) {
        break;
      }
      return paramBundle;
      return null;
      label43:
      throwException(new IllegalStateException("Fragment no longer exists for key " + paramString + ": index " + i));
    }
    throwException(new IllegalStateException("Fragment no longer exists for key " + paramString + ": index " + i));
    return paramBundle;
  }
  
  public List<Fragment> getFragments()
  {
    return this.mActive;
  }
  
  LayoutInflater.Factory getLayoutInflaterFactory()
  {
    return this;
  }
  
  public void hideFragment(Fragment paramFragment, int paramInt1, int paramInt2)
  {
    if (!DEBUG) {}
    while (paramFragment.mHidden)
    {
      return;
      Log.v("FragmentManager", "hide: " + paramFragment);
    }
    paramFragment.mHidden = true;
    if (paramFragment.mView == null) {
      if (paramFragment.mAdded) {
        break label107;
      }
    }
    for (;;)
    {
      paramFragment.onHiddenChanged(true);
      return;
      Animation localAnimation = loadAnimation(paramFragment, paramInt1, false, paramInt2);
      if (localAnimation == null) {}
      for (;;)
      {
        paramFragment.mView.setVisibility(8);
        break;
        paramFragment.mView.startAnimation(localAnimation);
      }
      label107:
      if ((paramFragment.mHasMenu) && (paramFragment.mMenuVisible)) {
        this.mNeedMenuInvalidate = true;
      }
    }
  }
  
  public boolean isDestroyed()
  {
    return this.mDestroyed;
  }
  
  Animation loadAnimation(Fragment paramFragment, int paramInt1, boolean paramBoolean, int paramInt2)
  {
    Animation localAnimation = paramFragment.onCreateAnimation(paramInt1, paramBoolean, paramFragment.mNextAnim);
    if (localAnimation == null)
    {
      if (paramFragment.mNextAnim != 0) {
        break label91;
      }
      if (paramInt1 == 0) {
        break label109;
      }
      paramInt1 = transitToStyleIndex(paramInt1, paramBoolean);
      if (paramInt1 < 0) {
        break label111;
      }
      switch (paramInt1)
      {
      default: 
        if (paramInt2 == 0) {
          break;
        }
      }
    }
    for (;;)
    {
      if (paramInt2 == 0) {
        break label217;
      }
      return null;
      return localAnimation;
      label91:
      paramFragment = AnimationUtils.loadAnimation(this.mActivity, paramFragment.mNextAnim);
      if (paramFragment == null) {
        break;
      }
      return paramFragment;
      label109:
      return null;
      label111:
      return null;
      return makeOpenCloseAnimation(this.mActivity, 1.125F, 1.0F, 0.0F, 1.0F);
      return makeOpenCloseAnimation(this.mActivity, 1.0F, 0.975F, 1.0F, 0.0F);
      return makeOpenCloseAnimation(this.mActivity, 0.975F, 1.0F, 0.0F, 1.0F);
      return makeOpenCloseAnimation(this.mActivity, 1.0F, 1.075F, 1.0F, 0.0F);
      return makeFadeAnimation(this.mActivity, 0.0F, 1.0F);
      return makeFadeAnimation(this.mActivity, 1.0F, 0.0F);
      if (this.mActivity.getWindow() != null) {
        paramInt2 = this.mActivity.getWindow().getAttributes().windowAnimations;
      }
    }
    label217:
    return null;
  }
  
  void makeActive(Fragment paramFragment)
  {
    if (paramFragment.mIndex < 0)
    {
      if (this.mAvailIndices != null) {
        break label53;
      }
      if (this.mActive == null) {
        break label109;
      }
    }
    for (;;)
    {
      paramFragment.setIndex(this.mActive.size(), this.mParent);
      this.mActive.add(paramFragment);
      for (;;)
      {
        if (DEBUG) {
          break label123;
        }
        return;
        return;
        label53:
        if (this.mAvailIndices.size() <= 0) {
          break;
        }
        paramFragment.setIndex(((Integer)this.mAvailIndices.remove(this.mAvailIndices.size() - 1)).intValue(), this.mParent);
        this.mActive.set(paramFragment.mIndex, paramFragment);
      }
      label109:
      this.mActive = new ArrayList();
    }
    label123:
    Log.v("FragmentManager", "Allocated fragment index " + paramFragment);
  }
  
  void makeInactive(Fragment paramFragment)
  {
    if (paramFragment.mIndex >= 0)
    {
      if (DEBUG) {
        break label65;
      }
      this.mActive.set(paramFragment.mIndex, null);
      if (this.mAvailIndices == null) {
        break label94;
      }
    }
    for (;;)
    {
      this.mAvailIndices.add(Integer.valueOf(paramFragment.mIndex));
      this.mActivity.invalidateSupportFragment(paramFragment.mWho);
      paramFragment.initState();
      return;
      return;
      label65:
      Log.v("FragmentManager", "Freeing fragment index " + paramFragment);
      break;
      label94:
      this.mAvailIndices = new ArrayList();
    }
  }
  
  void moveToState(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    if (this.mActivity != null)
    {
      if (!paramBoolean) {
        break label40;
      }
      label12:
      this.mCurState = paramInt1;
      if (this.mActive != null) {
        break label49;
      }
    }
    label40:
    label49:
    label172:
    label177:
    for (;;)
    {
      return;
      if (paramInt1 == 0) {
        break;
      }
      throw new IllegalStateException("No activity");
      if (this.mCurState != paramInt1) {
        break label12;
      }
      return;
      int i = 0;
      boolean bool = false;
      if (i >= this.mActive.size()) {
        if (!bool) {
          break label172;
        }
      }
      for (;;)
      {
        if ((!this.mNeedMenuInvalidate) || (this.mActivity == null) || (this.mCurState != 5)) {
          break label177;
        }
        this.mActivity.supportInvalidateOptionsMenu();
        this.mNeedMenuInvalidate = false;
        return;
        Fragment localFragment = (Fragment)this.mActive.get(i);
        if (localFragment == null) {}
        for (;;)
        {
          i += 1;
          break;
          moveToState(localFragment, paramInt1, paramInt2, paramInt3, false);
          if (localFragment.mLoaderManager != null) {
            bool |= localFragment.mLoaderManager.hasRunningLoaders();
          }
        }
        startPendingDeferredFragments();
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
    label12:
    int i;
    if (!paramFragment.mAdded)
    {
      if (paramInt1 > 1) {
        break label66;
      }
      if (paramFragment.mRemoving) {
        break label71;
      }
      i = paramInt1;
      label22:
      if (paramFragment.mDeferStart) {
        break label91;
      }
      paramInt1 = i;
      label32:
      if (paramFragment.mState < paramInt1) {
        break label116;
      }
      if (paramFragment.mState > paramInt1) {
        break label1119;
      }
      paramInt2 = paramInt1;
    }
    for (;;)
    {
      paramFragment.mState = paramInt2;
      return;
      if (paramFragment.mDetached) {
        break;
      }
      break label12;
      label66:
      paramInt1 = 1;
      break label12;
      label71:
      i = paramInt1;
      if (paramInt1 <= paramFragment.mState) {
        break label22;
      }
      i = paramFragment.mState;
      break label22;
      label91:
      paramInt1 = i;
      if (paramFragment.mState >= 4) {
        break label32;
      }
      paramInt1 = i;
      if (i <= 3) {
        break label32;
      }
      paramInt1 = 3;
      break label32;
      label116:
      label123:
      label130:
      int j;
      int k;
      label187:
      Object localObject1;
      if (!paramFragment.mFromLayout)
      {
        if (paramFragment.mAnimatingAway != null) {
          break label349;
        }
        j = paramInt1;
        k = paramInt1;
        i = paramInt1;
        switch (paramFragment.mState)
        {
        default: 
          paramInt2 = paramInt1;
          break;
        case 0: 
          if (!DEBUG)
          {
            if (paramFragment.mSavedFragmentState != null) {
              break label398;
            }
            j = paramInt1;
            paramFragment.mActivity = this.mActivity;
            paramFragment.mParentFragment = this.mParent;
            if (this.mParent != null) {
              break label506;
            }
            localObject1 = this.mActivity.mFragments;
            paramFragment.mFragmentManager = ((FragmentManagerImpl)localObject1);
            paramFragment.mCalled = false;
            paramFragment.onAttach(this.mActivity);
            if (!paramFragment.mCalled) {
              break label518;
            }
            if (paramFragment.mParentFragment == null) {
              break label552;
            }
            if (!paramFragment.mRetaining) {
              break label563;
            }
            paramFragment.mRetaining = false;
            if (paramFragment.mFromLayout) {
              break label574;
            }
          }
          break;
        case 1: 
          if (j <= 1) {
            k = j;
          }
          break;
        case 2: 
        case 3: 
          if (k <= 3) {
            i = k;
          }
          break;
        case 4: 
          label229:
          label262:
          label269:
          label281:
          paramInt2 = i;
          if (i <= 4) {
            continue;
          }
          if (DEBUG) {}
          break;
        }
      }
      for (;;)
      {
        paramFragment.mResumed = true;
        paramFragment.performResume();
        paramFragment.mSavedFragmentState = null;
        paramFragment.mSavedViewState = null;
        paramInt2 = i;
        break;
        if (paramFragment.mInLayout) {
          break label123;
        }
        return;
        label349:
        paramFragment.mAnimatingAway = null;
        moveToState(paramFragment, paramFragment.mStateAfterAnimating, 0, 0, true);
        break label130;
        Log.v("FragmentManager", "moveto CREATED: " + paramFragment);
        break label187;
        label398:
        paramFragment.mSavedFragmentState.setClassLoader(this.mActivity.getClassLoader());
        paramFragment.mSavedViewState = paramFragment.mSavedFragmentState.getSparseParcelableArray("android:view_state");
        paramFragment.mTarget = getFragment(paramFragment.mSavedFragmentState, "android:target_state");
        if (paramFragment.mTarget == null) {}
        for (;;)
        {
          paramFragment.mUserVisibleHint = paramFragment.mSavedFragmentState.getBoolean("android:user_visible_hint", true);
          j = paramInt1;
          if (paramFragment.mUserVisibleHint) {
            break;
          }
          paramFragment.mDeferStart = true;
          j = paramInt1;
          if (paramInt1 <= 3) {
            break;
          }
          j = 3;
          break;
          paramFragment.mTargetRequestCode = paramFragment.mSavedFragmentState.getInt("android:target_req_state", 0);
        }
        label506:
        localObject1 = this.mParent.mChildFragmentManager;
        break label229;
        label518:
        throw new SuperNotCalledException("Fragment " + paramFragment + " did not call through to super.onAttach()");
        label552:
        this.mActivity.onAttachFragment(paramFragment);
        break label262;
        label563:
        paramFragment.performCreate(paramFragment.mSavedFragmentState);
        break label269;
        label574:
        paramFragment.mView = paramFragment.performCreateView(paramFragment.getLayoutInflater(paramFragment.mSavedFragmentState), null, paramFragment.mSavedFragmentState);
        if (paramFragment.mView == null)
        {
          paramFragment.mInnerView = null;
          break label281;
        }
        paramFragment.mInnerView = paramFragment.mView;
        if (Build.VERSION.SDK_INT < 11)
        {
          paramFragment.mView = NoSaveStateFrameLayout.wrap(paramFragment.mView);
          label637:
          if (paramFragment.mHidden) {
            break label670;
          }
        }
        for (;;)
        {
          paramFragment.onViewCreated(paramFragment.mView, paramFragment.mSavedFragmentState);
          break;
          ViewCompat.setSaveFromParentEnabled(paramFragment.mView, false);
          break label637;
          label670:
          paramFragment.mView.setVisibility(8);
        }
        if (!DEBUG)
        {
          label688:
          if (!paramFragment.mFromLayout) {
            break label751;
          }
          paramFragment.performActivityCreated(paramFragment.mSavedFragmentState);
          if (paramFragment.mView != null) {
            break label1033;
          }
        }
        for (;;)
        {
          paramFragment.mSavedFragmentState = null;
          k = j;
          break;
          Log.v("FragmentManager", "moveto ACTIVITY_CREATED: " + paramFragment);
          break label688;
          label751:
          if (paramFragment.mContainerId == 0) {
            localObject1 = null;
          }
          Object localObject2;
          for (;;)
          {
            paramFragment.mContainer = ((ViewGroup)localObject1);
            paramFragment.mView = paramFragment.performCreateView(paramFragment.getLayoutInflater(paramFragment.mSavedFragmentState), (ViewGroup)localObject1, paramFragment.mSavedFragmentState);
            if (paramFragment.mView != null) {
              break label916;
            }
            paramFragment.mInnerView = null;
            break;
            localObject2 = (ViewGroup)this.mContainer.findViewById(paramFragment.mContainerId);
            localObject1 = localObject2;
            if (localObject2 == null)
            {
              localObject1 = localObject2;
              if (!paramFragment.mRestored)
              {
                throwException(new IllegalArgumentException("No view found for id 0x" + Integer.toHexString(paramFragment.mContainerId) + " (" + paramFragment.getResources().getResourceName(paramFragment.mContainerId) + ") for fragment " + paramFragment));
                localObject1 = localObject2;
              }
            }
          }
          label916:
          paramFragment.mInnerView = paramFragment.mView;
          if (Build.VERSION.SDK_INT < 11)
          {
            paramFragment.mView = NoSaveStateFrameLayout.wrap(paramFragment.mView);
            label943:
            if (localObject1 != null) {
              break label981;
            }
            if (paramFragment.mHidden) {
              break label1021;
            }
          }
          for (;;)
          {
            paramFragment.onViewCreated(paramFragment.mView, paramFragment.mSavedFragmentState);
            break;
            ViewCompat.setSaveFromParentEnabled(paramFragment.mView, false);
            break label943;
            label981:
            localObject2 = loadAnimation(paramFragment, paramInt2, true, paramInt3);
            if (localObject2 == null) {}
            for (;;)
            {
              ((ViewGroup)localObject1).addView(paramFragment.mView);
              break;
              paramFragment.mView.startAnimation((Animation)localObject2);
            }
            label1021:
            paramFragment.mView.setVisibility(8);
          }
          label1033:
          paramFragment.restoreViewState(paramFragment.mSavedFragmentState);
        }
        if (!DEBUG) {}
        for (;;)
        {
          paramFragment.performStart();
          i = k;
          break;
          Log.v("FragmentManager", "moveto STARTED: " + paramFragment);
        }
        Log.v("FragmentManager", "moveto RESUMED: " + paramFragment);
      }
      label1119:
      switch (paramFragment.mState)
      {
      default: 
        paramInt2 = paramInt1;
        break;
      case 1: 
        paramInt2 = paramInt1;
        if (paramInt1 < 1) {
          if (!this.mDestroyed)
          {
            if (paramFragment.mAnimatingAway != null) {
              break label1619;
            }
            if (DEBUG) {
              break label1629;
            }
            if (!paramFragment.mRetaining) {
              break label1658;
            }
          }
        }
        break;
      case 5: 
      case 4: 
      case 3: 
      case 2: 
        for (;;)
        {
          label1175:
          label1188:
          paramFragment.mCalled = false;
          paramFragment.onDetach();
          if (!paramFragment.mCalled) {
            break label1665;
          }
          paramInt2 = paramInt1;
          if (paramBoolean) {
            break;
          }
          if (!paramFragment.mRetaining) {
            break label1699;
          }
          paramFragment.mActivity = null;
          paramFragment.mParentFragment = null;
          paramFragment.mFragmentManager = null;
          paramFragment.mChildFragmentManager = null;
          paramInt2 = paramInt1;
          break;
          if (paramInt1 >= 5)
          {
            if (paramInt1 < 4) {
              break label1359;
            }
            if (paramInt1 < 3) {
              break label1401;
            }
            label1265:
            if (paramInt1 >= 2) {
              break label1412;
            }
            if (DEBUG) {
              break label1443;
            }
            label1276:
            if (paramFragment.mView != null) {
              break label1472;
            }
            label1283:
            paramFragment.performDestroyView();
            if (paramFragment.mView != null) {
              break label1497;
            }
          }
          label1359:
          label1401:
          label1412:
          label1443:
          label1472:
          label1497:
          while (paramFragment.mContainer == null)
          {
            paramFragment.mContainer = null;
            paramFragment.mView = null;
            paramFragment.mInnerView = null;
            break;
            if (!DEBUG) {}
            for (;;)
            {
              paramFragment.performPause();
              paramFragment.mResumed = false;
              break;
              Log.v("FragmentManager", "movefrom RESUMED: " + paramFragment);
            }
            if (!DEBUG) {}
            for (;;)
            {
              paramFragment.performStop();
              break;
              Log.v("FragmentManager", "movefrom STARTED: " + paramFragment);
            }
            if (!DEBUG) {}
            for (;;)
            {
              paramFragment.performReallyStop();
              break label1265;
              break;
              Log.v("FragmentManager", "movefrom STOPPED: " + paramFragment);
            }
            Log.v("FragmentManager", "movefrom ACTIVITY_CREATED: " + paramFragment);
            break label1276;
            if ((this.mActivity.isFinishing()) || (paramFragment.mSavedViewState != null)) {
              break label1283;
            }
            saveFragmentViewState(paramFragment);
            break label1283;
          }
          if (this.mCurState <= 0)
          {
            label1511:
            localObject1 = null;
            label1514:
            if (localObject1 != null) {
              break label1554;
            }
          }
          for (;;)
          {
            paramFragment.mContainer.removeView(paramFragment.mView);
            break;
            if (this.mDestroyed) {
              break label1511;
            }
            localObject1 = loadAnimation(paramFragment, paramInt2, false, paramInt3);
            break label1514;
            label1554:
            paramFragment.mAnimatingAway = paramFragment.mView;
            paramFragment.mStateAfterAnimating = paramInt1;
            ((Animation)localObject1).setAnimationListener(new Animation.AnimationListener()
            {
              public void onAnimationEnd(Animation paramAnonymousAnimation)
              {
                if (paramFragment.mAnimatingAway == null) {
                  return;
                }
                paramFragment.mAnimatingAway = null;
                FragmentManagerImpl.this.moveToState(paramFragment, paramFragment.mStateAfterAnimating, 0, 0, false);
              }
              
              public void onAnimationRepeat(Animation paramAnonymousAnimation) {}
              
              public void onAnimationStart(Animation paramAnonymousAnimation) {}
            });
            paramFragment.mView.startAnimation((Animation)localObject1);
          }
          if (paramFragment.mAnimatingAway == null) {
            break label1175;
          }
          localObject1 = paramFragment.mAnimatingAway;
          paramFragment.mAnimatingAway = null;
          ((View)localObject1).clearAnimation();
          break label1175;
          label1619:
          paramFragment.mStateAfterAnimating = paramInt1;
          paramInt2 = 1;
          break;
          label1629:
          Log.v("FragmentManager", "movefrom CREATED: " + paramFragment);
          break label1188;
          label1658:
          paramFragment.performDestroy();
        }
        label1665:
        throw new SuperNotCalledException("Fragment " + paramFragment + " did not call through to super.onDetach()");
        label1699:
        makeInactive(paramFragment);
        paramInt2 = paramInt1;
      }
    }
  }
  
  public void noteStateNotSaved()
  {
    this.mStateSaved = false;
  }
  
  public View onCreateView(String paramString, Context paramContext, AttributeSet paramAttributeSet)
  {
    Object localObject = null;
    String str1;
    int j;
    String str2;
    if ("fragment".equals(paramString))
    {
      str1 = paramAttributeSet.getAttributeValue(null, "class");
      paramString = paramContext.obtainStyledAttributes(paramAttributeSet, FragmentTag.Fragment);
      if (str1 == null) {
        break label166;
      }
      j = paramString.getResourceId(1, -1);
      str2 = paramString.getString(2);
      paramString.recycle();
      if (!Fragment.isSupportFragmentClass(this.mActivity, str1)) {
        break label176;
      }
      if (0 != 0) {
        break label178;
      }
      if (-1 == 0) {
        break label186;
      }
      label78:
      if (j != -1) {
        break label235;
      }
      paramString = (String)localObject;
      label87:
      if (paramString == null) {
        break label245;
      }
      label91:
      if (paramString == null) {
        break label260;
      }
      label95:
      if (DEBUG) {
        break label273;
      }
      label101:
      if (paramString == null) {
        break label327;
      }
      if (paramString.mInLayout) {
        break label403;
      }
      paramString.mInLayout = true;
      if (!paramString.mRetaining) {
        break label479;
      }
      label124:
      if (this.mCurState < 1) {
        break label495;
      }
      label132:
      moveToState(paramString);
      label137:
      if (paramString.mView == null) {
        break label514;
      }
      if (j != 0) {
        break label549;
      }
      label149:
      if (paramString.mView.getTag() == null) {
        break label561;
      }
    }
    for (;;)
    {
      return paramString.mView;
      return null;
      label166:
      str1 = paramString.getString(0);
      break;
      label176:
      return null;
      label178:
      throw new NullPointerException();
      label186:
      if ((j != -1) || (str2 != null)) {
        break label78;
      }
      throw new IllegalArgumentException(paramAttributeSet.getPositionDescription() + ": Must specify unique android:id, android:tag, or have a parent with an id for " + str1);
      label235:
      paramString = findFragmentById(j);
      break label87;
      label245:
      if (str2 == null) {
        break label91;
      }
      paramString = findFragmentByTag(str2);
      break label91;
      label260:
      if (-1 == 0) {
        break label95;
      }
      paramString = findFragmentById(0);
      break label95;
      label273:
      Log.v("FragmentManager", "onCreateView: id=0x" + Integer.toHexString(j) + " fname=" + str1 + " existing=" + paramString);
      break label101;
      label327:
      paramString = Fragment.instantiate(paramContext, str1);
      paramString.mFromLayout = true;
      if (j == 0) {}
      for (int i = 0;; i = j)
      {
        paramString.mFragmentId = i;
        paramString.mContainerId = 0;
        paramString.mTag = str2;
        paramString.mInLayout = true;
        paramString.mFragmentManager = this;
        paramString.onInflate(this.mActivity, paramAttributeSet, paramString.mSavedFragmentState);
        addFragment(paramString, true);
        break;
      }
      label403:
      throw new IllegalArgumentException(paramAttributeSet.getPositionDescription() + ": Duplicate id 0x" + Integer.toHexString(j) + ", tag " + str2 + ", or parent id 0x" + Integer.toHexString(0) + " with another fragment for " + str1);
      label479:
      paramString.onInflate(this.mActivity, paramAttributeSet, paramString.mSavedFragmentState);
      break label124;
      label495:
      if (!paramString.mFromLayout) {
        break label132;
      }
      moveToState(paramString, 1, 0, 0, false);
      break label137;
      label514:
      throw new IllegalStateException("Fragment " + str1 + " did not create a view.");
      label549:
      paramString.mView.setId(j);
      break label149;
      label561:
      paramString.mView.setTag(str2);
    }
  }
  
  public void performPendingDeferredStart(Fragment paramFragment)
  {
    if (!paramFragment.mDeferStart) {
      return;
    }
    if (!this.mExecutingActions)
    {
      paramFragment.mDeferStart = false;
      moveToState(paramFragment, this.mCurState, 0, 0, false);
      return;
    }
    this.mHavePendingDeferredStart = true;
  }
  
  public void popBackStack()
  {
    enqueueAction(new Runnable()
    {
      public void run()
      {
        FragmentManagerImpl.this.popBackStackState(FragmentManagerImpl.this.mActivity.mHandler, null, -1, 0);
      }
    }, false);
  }
  
  public void popBackStack(final int paramInt1, final int paramInt2)
  {
    if (paramInt1 >= 0)
    {
      enqueueAction(new Runnable()
      {
        public void run()
        {
          FragmentManagerImpl.this.popBackStackState(FragmentManagerImpl.this.mActivity.mHandler, null, paramInt1, paramInt2);
        }
      }, false);
      return;
    }
    throw new IllegalArgumentException("Bad id: " + paramInt1);
  }
  
  public void popBackStack(final String paramString, final int paramInt)
  {
    enqueueAction(new Runnable()
    {
      public void run()
      {
        FragmentManagerImpl.this.popBackStackState(FragmentManagerImpl.this.mActivity.mHandler, paramString, -1, paramInt);
      }
    }, false);
  }
  
  public boolean popBackStackImmediate()
  {
    checkStateLoss();
    executePendingTransactions();
    return popBackStackState(this.mActivity.mHandler, null, -1, 0);
  }
  
  public boolean popBackStackImmediate(int paramInt1, int paramInt2)
  {
    checkStateLoss();
    executePendingTransactions();
    if (paramInt1 >= 0) {
      return popBackStackState(this.mActivity.mHandler, null, paramInt1, paramInt2);
    }
    throw new IllegalArgumentException("Bad id: " + paramInt1);
  }
  
  public boolean popBackStackImmediate(String paramString, int paramInt)
  {
    checkStateLoss();
    executePendingTransactions();
    return popBackStackState(this.mActivity.mHandler, paramString, -1, paramInt);
  }
  
  boolean popBackStackState(Handler paramHandler, String paramString, int paramInt1, int paramInt2)
  {
    int i;
    label18:
    label34:
    label49:
    label79:
    SparseArray localSparseArray1;
    SparseArray localSparseArray2;
    if (this.mBackStack != null)
    {
      if (paramString == null) {
        break label143;
      }
      i = -1;
      if (paramString == null) {
        break label222;
      }
      i = this.mBackStack.size() - 1;
      if (i >= 0) {
        break label232;
      }
      if (i < 0) {
        break label287;
      }
      if ((paramInt2 & 0x1) != 0) {
        break label289;
      }
      paramInt1 = i;
      if (paramInt1 == this.mBackStack.size() - 1) {
        break label364;
      }
      paramString = new ArrayList();
      paramInt2 = this.mBackStack.size();
      paramInt2 -= 1;
      if (paramInt2 > paramInt1) {
        break label366;
      }
      paramInt2 = paramString.size() - 1;
      localSparseArray1 = new SparseArray();
      localSparseArray2 = new SparseArray();
      paramInt1 = 0;
    }
    for (;;)
    {
      if (paramInt1 > paramInt2)
      {
        paramHandler = null;
        paramInt1 = 0;
        if (paramInt1 <= paramInt2) {
          break label405;
        }
        reportBackStackChanged();
        return true;
        return false;
        label143:
        if ((paramInt1 >= 0) || ((paramInt2 & 0x1) != 0)) {
          break;
        }
        paramInt1 = this.mBackStack.size() - 1;
        if (paramInt1 >= 0)
        {
          paramHandler = (BackStackRecord)this.mBackStack.remove(paramInt1);
          paramString = new SparseArray();
          localSparseArray1 = new SparseArray();
          paramHandler.calculateBackFragments(paramString, localSparseArray1);
          paramHandler.popFromBackStack(true, null, paramString, localSparseArray1);
          reportBackStackChanged();
          return true;
        }
        return false;
        label222:
        if (paramInt1 >= 0) {
          break label18;
        }
        paramInt1 = i;
        break label49;
        label232:
        paramHandler = (BackStackRecord)this.mBackStack.get(i);
        if (paramString == null) {
          label249:
          if (paramInt1 >= 0) {
            break label276;
          }
        }
        for (;;)
        {
          i -= 1;
          break;
          if (paramString.equals(paramHandler.getName())) {
            break label34;
          }
          break label249;
          label276:
          if (paramInt1 == paramHandler.mIndex) {
            break label34;
          }
        }
        label287:
        return false;
        label289:
        paramInt2 = i - 1;
        if (paramInt2 < 0)
        {
          paramInt1 = paramInt2;
          break label49;
        }
        paramHandler = (BackStackRecord)this.mBackStack.get(paramInt2);
        if (paramString == null) {
          label323:
          if (paramInt1 >= 0) {
            break label353;
          }
        }
        for (;;)
        {
          paramInt1 = paramInt2;
          break;
          if (!paramString.equals(paramHandler.getName())) {
            break label323;
          }
          label353:
          do
          {
            paramInt2 -= 1;
            break;
          } while (paramInt1 == paramHandler.mIndex);
        }
        label364:
        return false;
        label366:
        paramString.add(this.mBackStack.remove(paramInt2));
        break label79;
      }
      ((BackStackRecord)paramString.get(paramInt1)).calculateBackFragments(localSparseArray1, localSparseArray2);
      paramInt1 += 1;
    }
    label405:
    label411:
    BackStackRecord localBackStackRecord;
    if (!DEBUG)
    {
      localBackStackRecord = (BackStackRecord)paramString.get(paramInt1);
      if (paramInt1 == paramInt2) {
        break label483;
      }
    }
    label483:
    for (boolean bool = false;; bool = true)
    {
      paramHandler = localBackStackRecord.popFromBackStack(bool, paramHandler, localSparseArray1, localSparseArray2);
      paramInt1 += 1;
      break;
      Log.v("FragmentManager", "Popping back stack state: " + paramString.get(paramInt1));
      break label411;
    }
  }
  
  public void putFragment(Bundle paramBundle, String paramString, Fragment paramFragment)
  {
    if (paramFragment.mIndex >= 0) {}
    for (;;)
    {
      paramBundle.putInt(paramString, paramFragment.mIndex);
      return;
      throwException(new IllegalStateException("Fragment " + paramFragment + " is not currently in the FragmentManager"));
    }
  }
  
  public void removeFragment(Fragment paramFragment, int paramInt1, int paramInt2)
  {
    if (!DEBUG)
    {
      if (!paramFragment.isInBackStack()) {
        break label108;
      }
      i = 0;
      label16:
      if (paramFragment.mDetached) {
        break label114;
      }
      label23:
      if (this.mAdded != null) {
        break label120;
      }
      label30:
      if (paramFragment.mHasMenu) {
        break label132;
      }
      label37:
      paramFragment.mAdded = false;
      paramFragment.mRemoving = true;
      if (i != 0) {
        break label147;
      }
    }
    label108:
    label114:
    label120:
    label132:
    label147:
    for (int i = 1;; i = 0)
    {
      moveToState(paramFragment, i, paramInt1, paramInt2, false);
      return;
      Log.v("FragmentManager", "remove: " + paramFragment + " nesting=" + paramFragment.mBackStackNesting);
      break;
      i = 1;
      break label16;
      if (i != 0) {
        break label23;
      }
      return;
      this.mAdded.remove(paramFragment);
      break label30;
      if (!paramFragment.mMenuVisible) {
        break label37;
      }
      this.mNeedMenuInvalidate = true;
      break label37;
    }
  }
  
  public void removeOnBackStackChangedListener(FragmentManager.OnBackStackChangedListener paramOnBackStackChangedListener)
  {
    if (this.mBackStackChangeListeners == null) {
      return;
    }
    this.mBackStackChangeListeners.remove(paramOnBackStackChangedListener);
  }
  
  void reportBackStackChanged()
  {
    if (this.mBackStackChangeListeners == null) {}
    for (;;)
    {
      return;
      int i = 0;
      while (i < this.mBackStackChangeListeners.size())
      {
        ((FragmentManager.OnBackStackChangedListener)this.mBackStackChangeListeners.get(i)).onBackStackChanged();
        i += 1;
      }
    }
  }
  
  void restoreAllState(Parcelable paramParcelable, ArrayList<Fragment> paramArrayList)
  {
    if (paramParcelable != null)
    {
      paramParcelable = (FragmentManagerState)paramParcelable;
      if (paramParcelable.mActive == null) {
        break label84;
      }
      if (paramArrayList != null) {
        break label85;
      }
      this.mActive = new ArrayList(paramParcelable.mActive.length);
      if (this.mAvailIndices != null) {
        break label248;
      }
    }
    label84:
    label85:
    label87:
    label111:
    Object localObject2;
    for (;;)
    {
      i = 0;
      if (i < paramParcelable.mActive.length) {
        break label258;
      }
      if (paramArrayList != null) {
        break label435;
      }
      if (paramParcelable.mAdded != null) {
        break label558;
      }
      this.mAdded = null;
      if (paramParcelable.mBackStack != null) {
        break label733;
      }
      this.mBackStack = null;
      return;
      return;
      return;
      i = 0;
      if (i < paramArrayList.size())
      {
        localObject1 = (Fragment)paramArrayList.get(i);
        if (DEBUG) {
          break label175;
        }
        localObject2 = paramParcelable.mActive[localObject1.mIndex];
        ((FragmentState)localObject2).mInstance = ((Fragment)localObject1);
        ((Fragment)localObject1).mSavedViewState = null;
        ((Fragment)localObject1).mBackStackNesting = 0;
        ((Fragment)localObject1).mInLayout = false;
        ((Fragment)localObject1).mAdded = false;
        ((Fragment)localObject1).mTarget = null;
        if (((FragmentState)localObject2).mSavedFragmentState != null) {
          break label205;
        }
      }
      for (;;)
      {
        i += 1;
        break label87;
        break;
        label175:
        Log.v("FragmentManager", "restoreAllState: re-attaching retained " + localObject1);
        break label111;
        label205:
        ((FragmentState)localObject2).mSavedFragmentState.setClassLoader(this.mActivity.getClassLoader());
        ((Fragment)localObject1).mSavedViewState = ((FragmentState)localObject2).mSavedFragmentState.getSparseParcelableArray("android:view_state");
        ((Fragment)localObject1).mSavedFragmentState = ((FragmentState)localObject2).mSavedFragmentState;
      }
      label248:
      this.mAvailIndices.clear();
    }
    label258:
    Object localObject1 = paramParcelable.mActive[i];
    if (localObject1 == null)
    {
      this.mActive.add(null);
      if (this.mAvailIndices == null) {
        break label392;
      }
      label287:
      if (DEBUG) {
        break label406;
      }
    }
    for (;;)
    {
      this.mAvailIndices.add(Integer.valueOf(i));
      i += 1;
      break;
      localObject2 = ((FragmentState)localObject1).instantiate(this.mActivity, this.mParent);
      if (!DEBUG) {}
      for (;;)
      {
        this.mActive.add(localObject2);
        ((FragmentState)localObject1).mInstance = null;
        break;
        Log.v("FragmentManager", "restoreAllState: active #" + i + ": " + localObject2);
      }
      label392:
      this.mAvailIndices = new ArrayList();
      break label287;
      label406:
      Log.v("FragmentManager", "restoreAllState: avail #" + i);
    }
    label435:
    int i = 0;
    label437:
    if (i < paramArrayList.size())
    {
      localObject1 = (Fragment)paramArrayList.get(i);
      if (((Fragment)localObject1).mTargetIndex >= 0) {
        break label470;
      }
    }
    for (;;)
    {
      i += 1;
      break label437;
      break;
      label470:
      if (((Fragment)localObject1).mTargetIndex >= this.mActive.size())
      {
        Log.w("FragmentManager", "Re-attaching retained fragment " + localObject1 + " target no longer exists: " + ((Fragment)localObject1).mTargetIndex);
        ((Fragment)localObject1).mTarget = null;
      }
      else
      {
        ((Fragment)localObject1).mTarget = ((Fragment)this.mActive.get(((Fragment)localObject1).mTargetIndex));
      }
    }
    label558:
    this.mAdded = new ArrayList(paramParcelable.mAdded.length);
    i = 0;
    label576:
    if (i < paramParcelable.mAdded.length)
    {
      paramArrayList = (Fragment)this.mActive.get(paramParcelable.mAdded[i]);
      if (paramArrayList == null) {
        break label644;
      }
      label606:
      paramArrayList.mAdded = true;
      if (DEBUG) {
        break label683;
      }
    }
    for (;;)
    {
      if (this.mAdded.contains(paramArrayList)) {
        break label722;
      }
      this.mAdded.add(paramArrayList);
      i += 1;
      break label576;
      break;
      label644:
      throwException(new IllegalStateException("No instantiated fragment for index #" + paramParcelable.mAdded[i]));
      break label606;
      label683:
      Log.v("FragmentManager", "restoreAllState: added #" + i + ": " + paramArrayList);
    }
    label722:
    throw new IllegalStateException("Already added!");
    label733:
    this.mBackStack = new ArrayList(paramParcelable.mBackStack.length);
    i = 0;
    label751:
    if (i < paramParcelable.mBackStack.length)
    {
      paramArrayList = paramParcelable.mBackStack[i].instantiate(this);
      if (DEBUG) {
        break label800;
      }
      label777:
      this.mBackStack.add(paramArrayList);
      if (paramArrayList.mIndex >= 0) {
        break label875;
      }
    }
    for (;;)
    {
      i += 1;
      break label751;
      break;
      label800:
      Log.v("FragmentManager", "restoreAllState: back stack #" + i + " (index " + paramArrayList.mIndex + "): " + paramArrayList);
      paramArrayList.dump("  ", new PrintWriter(new LogWriter("FragmentManager")), false);
      break label777;
      label875:
      setBackStackIndex(paramArrayList.mIndex, paramArrayList);
    }
  }
  
  ArrayList<Fragment> retainNonConfig()
  {
    if (this.mActive == null) {
      return null;
    }
    Object localObject1 = null;
    int i = 0;
    if (i >= this.mActive.size()) {
      return (ArrayList<Fragment>)localObject1;
    }
    Fragment localFragment = (Fragment)this.mActive.get(i);
    Object localObject2;
    if (localFragment == null) {
      localObject2 = localObject1;
    }
    do
    {
      i += 1;
      localObject1 = localObject2;
      break;
      localObject2 = localObject1;
    } while (!localFragment.mRetainInstance);
    if (localObject1 != null)
    {
      label72:
      ((ArrayList)localObject1).add(localFragment);
      localFragment.mRetaining = true;
      if (localFragment.mTarget != null) {
        break label154;
      }
    }
    label154:
    for (int j = -1;; j = localFragment.mTarget.mIndex)
    {
      localFragment.mTargetIndex = j;
      localObject2 = localObject1;
      if (!DEBUG) {
        break;
      }
      Log.v("FragmentManager", "retainNonConfig: keeping retained " + localFragment);
      localObject2 = localObject1;
      break;
      localObject1 = new ArrayList();
      break label72;
    }
  }
  
  Parcelable saveAllState()
  {
    Object localObject2 = null;
    execPendingActions();
    if (!HONEYCOMB) {
      if (this.mActive != null) {
        break label31;
      }
    }
    label31:
    while (this.mActive.size() <= 0)
    {
      return null;
      this.mStateSaved = true;
      break;
    }
    int k = this.mActive.size();
    FragmentState[] arrayOfFragmentState = new FragmentState[k];
    int j = 0;
    int i = 0;
    Object localObject1;
    if (j >= k)
    {
      if (i == 0) {
        break label442;
      }
      if (this.mAdded != null) {
        break label461;
      }
      localObject1 = null;
      label78:
      if (this.mBackStack != null) {
        break label633;
      }
    }
    Object localObject3;
    label151:
    label176:
    label186:
    label246:
    label284:
    label383:
    label398:
    label442:
    label461:
    label520:
    label587:
    label633:
    do
    {
      do
      {
        localObject3 = new FragmentManagerState();
        ((FragmentManagerState)localObject3).mActive = arrayOfFragmentState;
        ((FragmentManagerState)localObject3).mAdded = ((int[])localObject1);
        ((FragmentManagerState)localObject3).mBackStack = ((BackStackState[])localObject2);
        return (Parcelable)localObject3;
        localObject1 = (Fragment)this.mActive.get(j);
        if (localObject1 == null)
        {
          j += 1;
          break;
        }
        if (((Fragment)localObject1).mIndex >= 0)
        {
          localObject3 = new FragmentState((Fragment)localObject1);
          arrayOfFragmentState[j] = localObject3;
          if (((Fragment)localObject1).mState > 0) {
            break label246;
          }
          ((FragmentState)localObject3).mSavedFragmentState = ((Fragment)localObject1).mSavedFragmentState;
          if (DEBUG) {
            break label398;
          }
        }
        for (;;)
        {
          i = 1;
          break;
          throwException(new IllegalStateException("Failure saving state: active " + localObject1 + " has cleared index: " + ((Fragment)localObject1).mIndex));
          break label151;
          if (((FragmentState)localObject3).mSavedFragmentState != null) {
            break label176;
          }
          ((FragmentState)localObject3).mSavedFragmentState = saveFragmentBasicState((Fragment)localObject1);
          if (((Fragment)localObject1).mTarget == null) {
            break label186;
          }
          if (((Fragment)localObject1).mTarget.mIndex >= 0) {
            if (((FragmentState)localObject3).mSavedFragmentState == null) {
              break label383;
            }
          }
          for (;;)
          {
            putFragment(((FragmentState)localObject3).mSavedFragmentState, "android:target_state", ((Fragment)localObject1).mTarget);
            if (((Fragment)localObject1).mTargetRequestCode == 0) {
              break;
            }
            ((FragmentState)localObject3).mSavedFragmentState.putInt("android:target_req_state", ((Fragment)localObject1).mTargetRequestCode);
            break;
            throwException(new IllegalStateException("Failure saving state: " + localObject1 + " has target not in fragment manager: " + ((Fragment)localObject1).mTarget));
            break label284;
            ((FragmentState)localObject3).mSavedFragmentState = new Bundle();
          }
          Log.v("FragmentManager", "Saved state of " + localObject1 + ": " + ((FragmentState)localObject3).mSavedFragmentState);
        }
        if (!DEBUG) {
          return null;
        }
        Log.v("FragmentManager", "saveAllState: no fragments!");
        return null;
        j = this.mAdded.size();
        if (j <= 0)
        {
          localObject1 = null;
          break label78;
        }
        localObject3 = new int[j];
        i = 0;
        localObject1 = localObject3;
        if (i >= j) {
          break label78;
        }
        localObject3[i] = ((Fragment)this.mAdded.get(i)).mIndex;
        if (localObject3[i] >= 0) {
          if (DEBUG) {
            break label587;
          }
        }
        for (;;)
        {
          i += 1;
          break;
          throwException(new IllegalStateException("Failure saving state: active " + this.mAdded.get(i) + " has cleared index: " + localObject3[i]));
          break label520;
          Log.v("FragmentManager", "saveAllState: adding fragment #" + i + ": " + this.mAdded.get(i));
        }
        j = this.mBackStack.size();
      } while (j <= 0);
      localObject3 = new BackStackState[j];
      i = 0;
      localObject2 = localObject3;
    } while (i >= j);
    localObject3[i] = new BackStackState(this, (BackStackRecord)this.mBackStack.get(i));
    if (!DEBUG) {}
    for (;;)
    {
      i += 1;
      break;
      Log.v("FragmentManager", "saveAllState: adding back stack #" + i + ": " + this.mBackStack.get(i));
    }
  }
  
  Bundle saveFragmentBasicState(Fragment paramFragment)
  {
    Bundle localBundle = null;
    if (this.mStateBundle != null)
    {
      paramFragment.performSaveInstanceState(this.mStateBundle);
      if (!this.mStateBundle.isEmpty()) {
        break label64;
      }
      label27:
      if (paramFragment.mView != null) {
        break label77;
      }
    }
    for (;;)
    {
      if (paramFragment.mSavedViewState != null) {
        break label85;
      }
      if (!paramFragment.mUserVisibleHint) {
        break label113;
      }
      return localBundle;
      this.mStateBundle = new Bundle();
      break;
      label64:
      localBundle = this.mStateBundle;
      this.mStateBundle = null;
      break label27;
      label77:
      saveFragmentViewState(paramFragment);
    }
    label85:
    if (localBundle != null) {}
    for (;;)
    {
      localBundle.putSparseParcelableArray("android:view_state", paramFragment.mSavedViewState);
      break;
      localBundle = new Bundle();
    }
    label113:
    if (localBundle != null) {}
    for (;;)
    {
      localBundle.putBoolean("android:user_visible_hint", paramFragment.mUserVisibleHint);
      return localBundle;
      localBundle = new Bundle();
    }
  }
  
  public Fragment.SavedState saveFragmentInstanceState(Fragment paramFragment)
  {
    if (paramFragment.mIndex >= 0) {}
    while (paramFragment.mState <= 0)
    {
      return null;
      throwException(new IllegalStateException("Fragment " + paramFragment + " is not currently in the FragmentManager"));
    }
    paramFragment = saveFragmentBasicState(paramFragment);
    if (paramFragment == null) {
      return null;
    }
    return new Fragment.SavedState(paramFragment);
  }
  
  void saveFragmentViewState(Fragment paramFragment)
  {
    if (paramFragment.mInnerView != null)
    {
      if (this.mStateArray == null) {
        break label44;
      }
      this.mStateArray.clear();
    }
    for (;;)
    {
      paramFragment.mInnerView.saveHierarchyState(this.mStateArray);
      if (this.mStateArray.size() > 0) {
        break;
      }
      return;
      return;
      label44:
      this.mStateArray = new SparseArray();
    }
    paramFragment.mSavedViewState = this.mStateArray;
    this.mStateArray = null;
  }
  
  public void setBackStackIndex(int paramInt, BackStackRecord paramBackStackRecord)
  {
    for (;;)
    {
      int i;
      try
      {
        if (this.mBackStackIndices != null)
        {
          i = this.mBackStackIndices.size();
          if (paramInt >= i)
          {
            if (i < paramInt) {
              break label122;
            }
            if (DEBUG) {
              break label206;
            }
            this.mBackStackIndices.add(paramBackStackRecord);
          }
        }
        else
        {
          this.mBackStackIndices = new ArrayList();
          continue;
        }
        if (DEBUG) {}
      }
      finally {}
      for (;;)
      {
        this.mBackStackIndices.set(paramInt, paramBackStackRecord);
        break;
        Log.v("FragmentManager", "Setting back stack index " + paramInt + " to " + paramBackStackRecord);
      }
      label122:
      this.mBackStackIndices.add(null);
      if (this.mAvailBackStackIndices != null) {
        label138:
        if (DEBUG) {
          break label177;
        }
      }
      for (;;)
      {
        this.mAvailBackStackIndices.add(Integer.valueOf(i));
        i += 1;
        break;
        this.mAvailBackStackIndices = new ArrayList();
        break label138;
        label177:
        Log.v("FragmentManager", "Adding available back stack index " + i);
      }
      label206:
      Log.v("FragmentManager", "Adding back stack index " + paramInt + " with " + paramBackStackRecord);
    }
  }
  
  public void showFragment(Fragment paramFragment, int paramInt1, int paramInt2)
  {
    if (!DEBUG) {}
    while (!paramFragment.mHidden)
    {
      return;
      Log.v("FragmentManager", "show: " + paramFragment);
    }
    paramFragment.mHidden = false;
    if (paramFragment.mView == null) {
      if (paramFragment.mAdded) {
        break label106;
      }
    }
    for (;;)
    {
      paramFragment.onHiddenChanged(false);
      return;
      Animation localAnimation = loadAnimation(paramFragment, paramInt1, true, paramInt2);
      if (localAnimation == null) {}
      for (;;)
      {
        paramFragment.mView.setVisibility(0);
        break;
        paramFragment.mView.startAnimation(localAnimation);
      }
      label106:
      if ((paramFragment.mHasMenu) && (paramFragment.mMenuVisible)) {
        this.mNeedMenuInvalidate = true;
      }
    }
  }
  
  void startPendingDeferredFragments()
  {
    int i;
    if (this.mActive != null)
    {
      i = 0;
      if (i < this.mActive.size()) {}
    }
    else
    {
      return;
    }
    Fragment localFragment = (Fragment)this.mActive.get(i);
    if (localFragment == null) {}
    for (;;)
    {
      i += 1;
      break;
      performPendingDeferredStart(localFragment);
    }
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    localStringBuilder.append("FragmentManager{");
    localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
    localStringBuilder.append(" in ");
    if (this.mParent == null) {
      DebugUtils.buildShortClassTag(this.mActivity, localStringBuilder);
    }
    for (;;)
    {
      localStringBuilder.append("}}");
      return localStringBuilder.toString();
      DebugUtils.buildShortClassTag(this.mParent, localStringBuilder);
    }
  }
  
  static class FragmentTag
  {
    public static final int[] Fragment = { 16842755, 16842960, 16842961 };
    public static final int Fragment_id = 1;
    public static final int Fragment_name = 0;
    public static final int Fragment_tag = 2;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/app/FragmentManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */