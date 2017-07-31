package android.support.v4.app;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class FragmentStatePagerAdapter
  extends PagerAdapter
{
  private static final boolean DEBUG = false;
  private static final String TAG = "FragmentStatePagerAdapter";
  private FragmentTransaction mCurTransaction = null;
  private Fragment mCurrentPrimaryItem = null;
  private final FragmentManager mFragmentManager;
  private ArrayList<Fragment> mFragments = new ArrayList();
  private ArrayList<Fragment.SavedState> mSavedState = new ArrayList();
  
  public FragmentStatePagerAdapter(FragmentManager paramFragmentManager)
  {
    this.mFragmentManager = paramFragmentManager;
  }
  
  public void destroyItem(ViewGroup paramViewGroup, int paramInt, Object paramObject)
  {
    paramViewGroup = (Fragment)paramObject;
    if (this.mCurTransaction != null) {}
    for (;;)
    {
      if (this.mSavedState.size() > paramInt)
      {
        this.mSavedState.set(paramInt, this.mFragmentManager.saveFragmentInstanceState(paramViewGroup));
        this.mFragments.set(paramInt, null);
        this.mCurTransaction.remove(paramViewGroup);
        return;
        this.mCurTransaction = this.mFragmentManager.beginTransaction();
      }
      else
      {
        this.mSavedState.add(null);
      }
    }
  }
  
  public void finishUpdate(ViewGroup paramViewGroup)
  {
    if (this.mCurTransaction == null) {
      return;
    }
    this.mCurTransaction.commitAllowingStateLoss();
    this.mCurTransaction = null;
    this.mFragmentManager.executePendingTransactions();
  }
  
  public abstract Fragment getItem(int paramInt);
  
  public Object instantiateItem(ViewGroup paramViewGroup, int paramInt)
  {
    label18:
    Fragment localFragment;
    if (this.mFragments.size() <= paramInt)
    {
      if (this.mCurTransaction == null) {
        break label99;
      }
      localFragment = getItem(paramInt);
      if (this.mSavedState.size() > paramInt) {
        break label113;
      }
    }
    for (;;)
    {
      if (this.mFragments.size() > paramInt)
      {
        localFragment.setMenuVisibility(false);
        localFragment.setUserVisibleHint(false);
        this.mFragments.set(paramInt, localFragment);
        this.mCurTransaction.add(paramViewGroup.getId(), localFragment);
        return localFragment;
        localFragment = (Fragment)this.mFragments.get(paramInt);
        if (localFragment == null) {
          break;
        }
        return localFragment;
        label99:
        this.mCurTransaction = this.mFragmentManager.beginTransaction();
        break label18;
        label113:
        Fragment.SavedState localSavedState = (Fragment.SavedState)this.mSavedState.get(paramInt);
        if (localSavedState == null) {
          continue;
        }
        localFragment.setInitialSavedState(localSavedState);
        continue;
      }
      this.mFragments.add(null);
    }
  }
  
  public boolean isViewFromObject(View paramView, Object paramObject)
  {
    return ((Fragment)paramObject).getView() == paramView;
  }
  
  public void restoreState(Parcelable paramParcelable, ClassLoader paramClassLoader)
  {
    if (paramParcelable == null) {}
    label203:
    for (;;)
    {
      return;
      paramParcelable = (Bundle)paramParcelable;
      paramParcelable.setClassLoader(paramClassLoader);
      paramClassLoader = paramParcelable.getParcelableArray("states");
      this.mSavedState.clear();
      this.mFragments.clear();
      if (paramClassLoader == null) {
        paramClassLoader = paramParcelable.keySet().iterator();
      }
      for (;;)
      {
        if (!paramClassLoader.hasNext()) {
          break label203;
        }
        String str = (String)paramClassLoader.next();
        if (str.startsWith("f"))
        {
          int i = Integer.parseInt(str.substring(1));
          Fragment localFragment = this.mFragmentManager.getFragment(paramParcelable, str);
          if (localFragment == null)
          {
            Log.w("FragmentStatePagerAdapter", "Bad fragment at key " + str);
            continue;
            i = 0;
            while (i < paramClassLoader.length)
            {
              this.mSavedState.add((Fragment.SavedState)paramClassLoader[i]);
              i += 1;
            }
            break;
          }
          while (this.mFragments.size() <= i) {
            this.mFragments.add(null);
          }
          localFragment.setMenuVisibility(false);
          this.mFragments.set(i, localFragment);
        }
      }
    }
  }
  
  public Parcelable saveState()
  {
    int i = 0;
    Object localObject1 = null;
    if (this.mSavedState.size() <= 0) {}
    Object localObject2;
    for (;;)
    {
      if (i >= this.mFragments.size())
      {
        return (Parcelable)localObject1;
        localObject1 = new Bundle();
        localObject2 = new Fragment.SavedState[this.mSavedState.size()];
        this.mSavedState.toArray((Object[])localObject2);
        ((Bundle)localObject1).putParcelableArray("states", (Parcelable[])localObject2);
      }
      else
      {
        localObject2 = (Fragment)this.mFragments.get(i);
        if (localObject2 != null) {
          break;
        }
        i += 1;
      }
    }
    if (localObject1 != null) {}
    for (;;)
    {
      String str = "f" + i;
      this.mFragmentManager.putFragment((Bundle)localObject1, str, (Fragment)localObject2);
      break;
      localObject1 = new Bundle();
    }
  }
  
  public void setPrimaryItem(ViewGroup paramViewGroup, int paramInt, Object paramObject)
  {
    paramViewGroup = (Fragment)paramObject;
    if (paramViewGroup == this.mCurrentPrimaryItem) {
      return;
    }
    if (this.mCurrentPrimaryItem == null) {
      if (paramViewGroup != null) {
        break label50;
      }
    }
    for (;;)
    {
      this.mCurrentPrimaryItem = paramViewGroup;
      return;
      this.mCurrentPrimaryItem.setMenuVisibility(false);
      this.mCurrentPrimaryItem.setUserVisibleHint(false);
      break;
      label50:
      paramViewGroup.setMenuVisibility(true);
      paramViewGroup.setUserVisibleHint(true);
    }
  }
  
  public void startUpdate(ViewGroup paramViewGroup) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/app/FragmentStatePagerAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */