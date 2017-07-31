package android.support.v4.app;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;

final class BackStackState
  implements Parcelable
{
  public static final Parcelable.Creator<BackStackState> CREATOR = new Parcelable.Creator()
  {
    public BackStackState createFromParcel(Parcel paramAnonymousParcel)
    {
      return new BackStackState(paramAnonymousParcel);
    }
    
    public BackStackState[] newArray(int paramAnonymousInt)
    {
      return new BackStackState[paramAnonymousInt];
    }
  };
  final int mBreadCrumbShortTitleRes;
  final CharSequence mBreadCrumbShortTitleText;
  final int mBreadCrumbTitleRes;
  final CharSequence mBreadCrumbTitleText;
  final int mIndex;
  final String mName;
  final int[] mOps;
  final ArrayList<String> mSharedElementSourceNames;
  final ArrayList<String> mSharedElementTargetNames;
  final int mTransition;
  final int mTransitionStyle;
  
  public BackStackState(Parcel paramParcel)
  {
    this.mOps = paramParcel.createIntArray();
    this.mTransition = paramParcel.readInt();
    this.mTransitionStyle = paramParcel.readInt();
    this.mName = paramParcel.readString();
    this.mIndex = paramParcel.readInt();
    this.mBreadCrumbTitleRes = paramParcel.readInt();
    this.mBreadCrumbTitleText = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
    this.mBreadCrumbShortTitleRes = paramParcel.readInt();
    this.mBreadCrumbShortTitleText = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
    this.mSharedElementSourceNames = paramParcel.createStringArrayList();
    this.mSharedElementTargetNames = paramParcel.createStringArrayList();
  }
  
  public BackStackState(FragmentManagerImpl paramFragmentManagerImpl, BackStackRecord paramBackStackRecord)
  {
    paramFragmentManagerImpl = paramBackStackRecord.mHead;
    int i = 0;
    if (paramFragmentManagerImpl == null)
    {
      this.mOps = new int[i + paramBackStackRecord.mNumOp * 7];
      if (paramBackStackRecord.mAddToBackStack)
      {
        paramFragmentManagerImpl = paramBackStackRecord.mHead;
        i = 0;
        if (paramFragmentManagerImpl != null) {
          break label167;
        }
        this.mTransition = paramBackStackRecord.mTransition;
        this.mTransitionStyle = paramBackStackRecord.mTransitionStyle;
        this.mName = paramBackStackRecord.mName;
        this.mIndex = paramBackStackRecord.mIndex;
        this.mBreadCrumbTitleRes = paramBackStackRecord.mBreadCrumbTitleRes;
        this.mBreadCrumbTitleText = paramBackStackRecord.mBreadCrumbTitleText;
        this.mBreadCrumbShortTitleRes = paramBackStackRecord.mBreadCrumbShortTitleRes;
        this.mBreadCrumbShortTitleText = paramBackStackRecord.mBreadCrumbShortTitleText;
        this.mSharedElementSourceNames = paramBackStackRecord.mSharedElementSourceNames;
        this.mSharedElementTargetNames = paramBackStackRecord.mSharedElementTargetNames;
      }
    }
    else
    {
      if (paramFragmentManagerImpl.removed == null) {}
      for (;;)
      {
        paramFragmentManagerImpl = paramFragmentManagerImpl.next;
        break;
        i += paramFragmentManagerImpl.removed.size();
      }
    }
    throw new IllegalStateException("Not on back stack");
    label167:
    int[] arrayOfInt = this.mOps;
    int j = i + 1;
    arrayOfInt[i] = paramFragmentManagerImpl.cmd;
    arrayOfInt = this.mOps;
    int k = j + 1;
    if (paramFragmentManagerImpl.fragment == null)
    {
      i = -1;
      label207:
      arrayOfInt[j] = i;
      arrayOfInt = this.mOps;
      i = k + 1;
      arrayOfInt[k] = paramFragmentManagerImpl.enterAnim;
      arrayOfInt = this.mOps;
      j = i + 1;
      arrayOfInt[i] = paramFragmentManagerImpl.exitAnim;
      arrayOfInt = this.mOps;
      i = j + 1;
      arrayOfInt[j] = paramFragmentManagerImpl.popEnterAnim;
      arrayOfInt = this.mOps;
      j = i + 1;
      arrayOfInt[i] = paramFragmentManagerImpl.popExitAnim;
      if (paramFragmentManagerImpl.removed != null) {
        break label334;
      }
      arrayOfInt = this.mOps;
      i = j + 1;
      arrayOfInt[j] = 0;
    }
    for (;;)
    {
      paramFragmentManagerImpl = paramFragmentManagerImpl.next;
      break;
      i = paramFragmentManagerImpl.fragment.mIndex;
      break label207;
      label334:
      k = paramFragmentManagerImpl.removed.size();
      arrayOfInt = this.mOps;
      i = j + 1;
      arrayOfInt[j] = k;
      j = 0;
      while (j < k)
      {
        this.mOps[i] = ((Fragment)paramFragmentManagerImpl.removed.get(j)).mIndex;
        j += 1;
        i += 1;
      }
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public BackStackRecord instantiate(FragmentManagerImpl paramFragmentManagerImpl)
  {
    BackStackRecord localBackStackRecord = new BackStackRecord(paramFragmentManagerImpl);
    int k = 0;
    int i = 0;
    if (i >= this.mOps.length)
    {
      localBackStackRecord.mTransition = this.mTransition;
      localBackStackRecord.mTransitionStyle = this.mTransitionStyle;
      localBackStackRecord.mName = this.mName;
      localBackStackRecord.mIndex = this.mIndex;
      localBackStackRecord.mAddToBackStack = true;
      localBackStackRecord.mBreadCrumbTitleRes = this.mBreadCrumbTitleRes;
      localBackStackRecord.mBreadCrumbTitleText = this.mBreadCrumbTitleText;
      localBackStackRecord.mBreadCrumbShortTitleRes = this.mBreadCrumbShortTitleRes;
      localBackStackRecord.mBreadCrumbShortTitleText = this.mBreadCrumbShortTitleText;
      localBackStackRecord.mSharedElementSourceNames = this.mSharedElementSourceNames;
      localBackStackRecord.mSharedElementTargetNames = this.mSharedElementTargetNames;
      localBackStackRecord.bumpBackStackNesting(1);
      return localBackStackRecord;
    }
    BackStackRecord.Op localOp = new BackStackRecord.Op();
    Object localObject = this.mOps;
    int j = i + 1;
    localOp.cmd = localObject[i];
    label163:
    label188:
    int m;
    int n;
    if (!FragmentManagerImpl.DEBUG)
    {
      localObject = this.mOps;
      i = j + 1;
      j = localObject[j];
      if (j >= 0) {
        break label357;
      }
      localOp.fragment = null;
      localObject = this.mOps;
      j = i + 1;
      localOp.enterAnim = localObject[i];
      localObject = this.mOps;
      i = j + 1;
      localOp.exitAnim = localObject[j];
      localObject = this.mOps;
      j = i + 1;
      localOp.popEnterAnim = localObject[i];
      localObject = this.mOps;
      m = j + 1;
      localOp.popExitAnim = localObject[j];
      localObject = this.mOps;
      i = m + 1;
      n = localObject[m];
      if (n > 0) {
        break label376;
      }
    }
    label357:
    label376:
    do
    {
      localBackStackRecord.addOp(localOp);
      k += 1;
      break;
      Log.v("FragmentManager", "Instantiate " + localBackStackRecord + " op #" + k + " base fragment #" + this.mOps[j]);
      break label163;
      localOp.fragment = ((Fragment)paramFragmentManagerImpl.mActive.get(j));
      break label188;
      localOp.removed = new ArrayList(n);
      m = 0;
      j = i;
      i = j;
    } while (m >= n);
    if (!FragmentManagerImpl.DEBUG) {}
    for (;;)
    {
      localObject = (Fragment)paramFragmentManagerImpl.mActive.get(this.mOps[j]);
      localOp.removed.add(localObject);
      m += 1;
      j += 1;
      break;
      Log.v("FragmentManager", "Instantiate " + localBackStackRecord + " set remove fragment #" + this.mOps[j]);
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeIntArray(this.mOps);
    paramParcel.writeInt(this.mTransition);
    paramParcel.writeInt(this.mTransitionStyle);
    paramParcel.writeString(this.mName);
    paramParcel.writeInt(this.mIndex);
    paramParcel.writeInt(this.mBreadCrumbTitleRes);
    TextUtils.writeToParcel(this.mBreadCrumbTitleText, paramParcel, 0);
    paramParcel.writeInt(this.mBreadCrumbShortTitleRes);
    TextUtils.writeToParcel(this.mBreadCrumbShortTitleText, paramParcel, 0);
    paramParcel.writeStringList(this.mSharedElementSourceNames);
    paramParcel.writeStringList(this.mSharedElementTargetNames);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/app/BackStackState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */