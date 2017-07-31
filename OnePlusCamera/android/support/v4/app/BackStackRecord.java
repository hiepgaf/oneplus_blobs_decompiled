package android.support.v4.app;

import android.os.Build.VERSION;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.LogWriter;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;

final class BackStackRecord
  extends FragmentTransaction
  implements FragmentManager.BackStackEntry, Runnable
{
  static final int OP_ADD = 1;
  static final int OP_ATTACH = 7;
  static final int OP_DETACH = 6;
  static final int OP_HIDE = 4;
  static final int OP_NULL = 0;
  static final int OP_REMOVE = 3;
  static final int OP_REPLACE = 2;
  static final int OP_SHOW = 5;
  static final String TAG = "FragmentManager";
  boolean mAddToBackStack;
  boolean mAllowAddToBackStack = true;
  int mBreadCrumbShortTitleRes;
  CharSequence mBreadCrumbShortTitleText;
  int mBreadCrumbTitleRes;
  CharSequence mBreadCrumbTitleText;
  boolean mCommitted;
  int mEnterAnim;
  int mExitAnim;
  Op mHead;
  int mIndex = -1;
  final FragmentManagerImpl mManager;
  String mName;
  int mNumOp;
  int mPopEnterAnim;
  int mPopExitAnim;
  ArrayList<String> mSharedElementSourceNames;
  ArrayList<String> mSharedElementTargetNames;
  Op mTail;
  int mTransition;
  int mTransitionStyle;
  
  public BackStackRecord(FragmentManagerImpl paramFragmentManagerImpl)
  {
    this.mManager = paramFragmentManagerImpl;
  }
  
  private TransitionState beginTransition(SparseArray<Fragment> paramSparseArray1, SparseArray<Fragment> paramSparseArray2, boolean paramBoolean)
  {
    int k = 0;
    TransitionState localTransitionState = new TransitionState();
    localTransitionState.nonExistentView = new View(this.mManager.mActivity);
    int j = 0;
    int i = 0;
    if (j >= paramSparseArray1.size())
    {
      j = i;
      i = k;
      if (i >= paramSparseArray2.size())
      {
        if (j == 0) {
          break label151;
        }
        return localTransitionState;
      }
    }
    else
    {
      if (!configureTransitions(paramSparseArray1.keyAt(j), localTransitionState, paramBoolean, paramSparseArray1, paramSparseArray2)) {}
      for (;;)
      {
        j += 1;
        break;
        i = 1;
      }
    }
    k = paramSparseArray2.keyAt(i);
    if (paramSparseArray1.get(k) != null) {}
    for (;;)
    {
      i += 1;
      break;
      if (configureTransitions(k, localTransitionState, paramBoolean, paramSparseArray1, paramSparseArray2)) {
        j = 1;
      }
    }
    label151:
    return null;
  }
  
  private void calculateFragments(SparseArray<Fragment> paramSparseArray1, SparseArray<Fragment> paramSparseArray2)
  {
    Op localOp;
    if (this.mManager.mContainer.hasView())
    {
      localOp = this.mHead;
      if (localOp != null) {}
    }
    else
    {
      return;
    }
    switch (localOp.cmd)
    {
    }
    for (;;)
    {
      localOp = localOp.next;
      break;
      setLastIn(paramSparseArray2, localOp.fragment);
      continue;
      Fragment localFragment1 = localOp.fragment;
      if (this.mManager.mAdded == null)
      {
        setLastIn(paramSparseArray2, localFragment1);
      }
      else
      {
        int i = 0;
        label128:
        Fragment localFragment2;
        if (i < this.mManager.mAdded.size())
        {
          localFragment2 = (Fragment)this.mManager.mAdded.get(i);
          if (localFragment1 != null) {
            break label186;
          }
          label166:
          if (localFragment2 == localFragment1) {
            break label202;
          }
          setFirstOut(paramSparseArray1, localFragment2);
        }
        for (;;)
        {
          i += 1;
          break label128;
          break;
          label186:
          if (localFragment2.mContainerId == localFragment1.mContainerId) {
            break label166;
          }
          continue;
          label202:
          localFragment1 = null;
        }
        setFirstOut(paramSparseArray1, localOp.fragment);
        continue;
        setFirstOut(paramSparseArray1, localOp.fragment);
        continue;
        setLastIn(paramSparseArray2, localOp.fragment);
        continue;
        setFirstOut(paramSparseArray1, localOp.fragment);
        continue;
        setLastIn(paramSparseArray2, localOp.fragment);
      }
    }
  }
  
  private void callSharedElementEnd(TransitionState paramTransitionState, Fragment paramFragment1, Fragment paramFragment2, boolean paramBoolean, ArrayMap<String, View> paramArrayMap)
  {
    if (!paramBoolean) {}
    for (paramTransitionState = paramFragment1.mEnterTransitionCallback; paramTransitionState == null; paramTransitionState = paramFragment2.mEnterTransitionCallback) {
      return;
    }
    paramTransitionState.onSharedElementEnd(new ArrayList(paramArrayMap.keySet()), new ArrayList(paramArrayMap.values()), null);
  }
  
  private static Object captureExitingViews(Object paramObject, Fragment paramFragment, ArrayList<View> paramArrayList, ArrayMap<String, View> paramArrayMap, View paramView)
  {
    if (paramObject == null) {
      return paramObject;
    }
    return FragmentTransitionCompat21.captureExitingViews(paramObject, paramFragment.getView(), paramArrayList, paramArrayMap, paramView);
  }
  
  private boolean configureTransitions(int paramInt, TransitionState paramTransitionState, boolean paramBoolean, SparseArray<Fragment> paramSparseArray1, SparseArray<Fragment> paramSparseArray2)
  {
    ViewGroup localViewGroup = (ViewGroup)this.mManager.mContainer.findViewById(paramInt);
    final Object localObject1;
    Object localObject4;
    Object localObject2;
    Object localObject3;
    Object localObject5;
    ArrayList localArrayList;
    label93:
    label126:
    label143:
    ArrayMap localArrayMap;
    if (localViewGroup != null)
    {
      localObject1 = (Fragment)paramSparseArray2.get(paramInt);
      localObject4 = (Fragment)paramSparseArray1.get(paramInt);
      localObject2 = getEnterTransition((Fragment)localObject1, paramBoolean);
      localObject3 = getSharedElementTransition((Fragment)localObject1, (Fragment)localObject4, paramBoolean);
      localObject5 = getExitTransition((Fragment)localObject4, paramBoolean);
      if (localObject2 == null) {
        break label197;
      }
      paramSparseArray1 = null;
      localArrayList = new ArrayList();
      if (localObject3 != null) {
        break label209;
      }
      paramSparseArray2 = new ArrayList();
      localObject5 = captureExitingViews(localObject5, (Fragment)localObject4, paramSparseArray2, paramSparseArray1, paramTransitionState.nonExistentView);
      if (this.mSharedElementTargetNames != null) {
        break label302;
      }
      paramSparseArray1 = new FragmentTransitionCompat21.ViewRetriever()
      {
        public View getView()
        {
          return localObject1.getView();
        }
      };
      if (localObject3 != null) {
        break label360;
      }
      localObject4 = new ArrayList();
      localArrayMap = new ArrayMap();
      if (paramBoolean) {
        break label379;
      }
      paramBoolean = ((Fragment)localObject1).getAllowEnterTransitionOverlap();
      label171:
      localObject1 = FragmentTransitionCompat21.mergeTransitions(localObject2, localObject5, localObject3, paramBoolean);
      if (localObject1 != null) {
        break label388;
      }
    }
    for (;;)
    {
      if (localObject1 != null) {
        break label484;
      }
      return false;
      return false;
      label197:
      if ((localObject3 != null) || (localObject5 != null)) {
        break;
      }
      return false;
      label209:
      paramSparseArray1 = remapSharedElements(paramTransitionState, (Fragment)localObject4, paramBoolean);
      localArrayList.add(paramTransitionState.nonExistentView);
      localArrayList.addAll(paramSparseArray1.values());
      if (!paramBoolean) {}
      for (paramSparseArray2 = ((Fragment)localObject1).mEnterTransitionCallback;; paramSparseArray2 = ((Fragment)localObject4).mEnterTransitionCallback)
      {
        if (paramSparseArray2 != null) {
          break label269;
        }
        break;
      }
      label269:
      paramSparseArray2.onSharedElementStart(new ArrayList(paramSparseArray1.keySet()), new ArrayList(paramSparseArray1.values()), null);
      break label93;
      label302:
      if (paramSparseArray1 == null) {
        break label126;
      }
      paramSparseArray1 = (View)paramSparseArray1.get(this.mSharedElementTargetNames.get(0));
      if (paramSparseArray1 == null) {
        break label126;
      }
      if (localObject5 == null) {}
      for (;;)
      {
        if (localObject3 == null) {
          break label358;
        }
        FragmentTransitionCompat21.setEpicenter(localObject3, paramSparseArray1);
        break;
        FragmentTransitionCompat21.setEpicenter(localObject5, paramSparseArray1);
      }
      label358:
      break label126;
      label360:
      prepareSharedElementTransition(paramTransitionState, localViewGroup, localObject3, (Fragment)localObject1, (Fragment)localObject4, paramBoolean, localArrayList);
      break label143;
      label379:
      paramBoolean = ((Fragment)localObject1).getAllowReturnTransitionOverlap();
      break label171;
      label388:
      FragmentTransitionCompat21.addTransitionTargets(localObject2, localObject3, localViewGroup, paramSparseArray1, paramTransitionState.nonExistentView, paramTransitionState.enteringEpicenterView, paramTransitionState.nameOverrides, (ArrayList)localObject4, localArrayMap, localArrayList);
      excludeHiddenFragmentsAfterEnter(localViewGroup, paramTransitionState, paramInt, localObject1);
      FragmentTransitionCompat21.excludeTarget(localObject1, paramTransitionState.nonExistentView, true);
      excludeHiddenFragments(paramTransitionState, paramInt, localObject1);
      FragmentTransitionCompat21.beginDelayedTransition(localViewGroup, localObject1);
      FragmentTransitionCompat21.cleanupTransitions(localViewGroup, paramTransitionState.nonExistentView, localObject2, (ArrayList)localObject4, localObject5, paramSparseArray2, localObject3, localArrayList, localObject1, paramTransitionState.hiddenFragmentViews, localArrayMap);
    }
    label484:
    return true;
  }
  
  private void doAddOp(int paramInt1, Fragment paramFragment, String paramString, int paramInt2)
  {
    paramFragment.mFragmentManager = this.mManager;
    if (paramString == null)
    {
      if (paramInt1 == 0)
      {
        paramString = new Op();
        paramString.cmd = paramInt2;
        paramString.fragment = paramFragment;
        addOp(paramString);
      }
    }
    else
    {
      if (paramFragment.mTag == null) {}
      while (paramString.equals(paramFragment.mTag))
      {
        paramFragment.mTag = paramString;
        break;
      }
      throw new IllegalStateException("Can't change tag of fragment " + paramFragment + ": was " + paramFragment.mTag + " now " + paramString);
    }
    if (paramFragment.mFragmentId == 0) {}
    while (paramFragment.mFragmentId == paramInt1)
    {
      paramFragment.mFragmentId = paramInt1;
      paramFragment.mContainerId = paramInt1;
      break;
    }
    throw new IllegalStateException("Can't change container ID of fragment " + paramFragment + ": was " + paramFragment.mFragmentId + " now " + paramInt1);
  }
  
  private void excludeHiddenFragments(TransitionState paramTransitionState, int paramInt, Object paramObject)
  {
    if (this.mManager.mAdded == null) {
      return;
    }
    int i = 0;
    label14:
    Fragment localFragment;
    if (i < this.mManager.mAdded.size())
    {
      localFragment = (Fragment)this.mManager.mAdded.get(i);
      if (localFragment.mView != null) {
        break label63;
      }
    }
    for (;;)
    {
      i += 1;
      break label14;
      break;
      label63:
      if ((localFragment.mContainer != null) && (localFragment.mContainerId == paramInt)) {
        if (!localFragment.mHidden)
        {
          FragmentTransitionCompat21.excludeTarget(paramObject, localFragment.mView, false);
          paramTransitionState.hiddenFragmentViews.remove(localFragment.mView);
        }
        else if (!paramTransitionState.hiddenFragmentViews.contains(localFragment.mView))
        {
          FragmentTransitionCompat21.excludeTarget(paramObject, localFragment.mView, true);
          paramTransitionState.hiddenFragmentViews.add(localFragment.mView);
        }
      }
    }
  }
  
  private void excludeHiddenFragmentsAfterEnter(final View paramView, final TransitionState paramTransitionState, final int paramInt, final Object paramObject)
  {
    paramView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
    {
      public boolean onPreDraw()
      {
        paramView.getViewTreeObserver().removeOnPreDrawListener(this);
        BackStackRecord.this.excludeHiddenFragments(paramTransitionState, paramInt, paramObject);
        return true;
      }
    });
  }
  
  private static Object getEnterTransition(Fragment paramFragment, boolean paramBoolean)
  {
    if (paramFragment != null) {
      if (paramBoolean) {
        break label20;
      }
    }
    label20:
    for (paramFragment = paramFragment.getEnterTransition();; paramFragment = paramFragment.getReenterTransition())
    {
      return FragmentTransitionCompat21.cloneTransition(paramFragment);
      return null;
    }
  }
  
  private static Object getExitTransition(Fragment paramFragment, boolean paramBoolean)
  {
    if (paramFragment != null) {
      if (paramBoolean) {
        break label20;
      }
    }
    label20:
    for (paramFragment = paramFragment.getExitTransition();; paramFragment = paramFragment.getReturnTransition())
    {
      return FragmentTransitionCompat21.cloneTransition(paramFragment);
      return null;
    }
  }
  
  private static Object getSharedElementTransition(Fragment paramFragment1, Fragment paramFragment2, boolean paramBoolean)
  {
    if (paramFragment1 == null) {}
    while (paramFragment2 == null) {
      return null;
    }
    if (!paramBoolean) {}
    for (paramFragment1 = paramFragment1.getSharedElementEnterTransition();; paramFragment1 = paramFragment2.getSharedElementReturnTransition()) {
      return FragmentTransitionCompat21.cloneTransition(paramFragment1);
    }
  }
  
  private ArrayMap<String, View> mapEnteringSharedElements(TransitionState paramTransitionState, Fragment paramFragment, boolean paramBoolean)
  {
    paramTransitionState = new ArrayMap();
    paramFragment = paramFragment.getView();
    if (paramFragment == null) {}
    while (this.mSharedElementSourceNames == null) {
      return paramTransitionState;
    }
    FragmentTransitionCompat21.findNamedViews(paramTransitionState, paramFragment);
    if (!paramBoolean)
    {
      paramTransitionState.retainAll(this.mSharedElementTargetNames);
      return paramTransitionState;
    }
    return remapNames(this.mSharedElementSourceNames, this.mSharedElementTargetNames, paramTransitionState);
  }
  
  private ArrayMap<String, View> mapSharedElementsIn(TransitionState paramTransitionState, boolean paramBoolean, Fragment paramFragment)
  {
    ArrayMap localArrayMap = mapEnteringSharedElements(paramTransitionState, paramFragment, paramBoolean);
    if (!paramBoolean) {
      if (paramFragment.mEnterTransitionCallback != null) {
        break label65;
      }
    }
    for (;;)
    {
      setNameOverrides(paramTransitionState, localArrayMap, true);
      return localArrayMap;
      if (paramFragment.mExitTransitionCallback == null) {}
      for (;;)
      {
        setBackNameOverrides(paramTransitionState, localArrayMap, true);
        return localArrayMap;
        paramFragment.mExitTransitionCallback.onMapSharedElements(this.mSharedElementTargetNames, localArrayMap);
      }
      label65:
      paramFragment.mEnterTransitionCallback.onMapSharedElements(this.mSharedElementTargetNames, localArrayMap);
    }
  }
  
  private void prepareSharedElementTransition(final TransitionState paramTransitionState, final View paramView, final Object paramObject, final Fragment paramFragment1, final Fragment paramFragment2, final boolean paramBoolean, final ArrayList<View> paramArrayList)
  {
    paramView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
    {
      public boolean onPreDraw()
      {
        paramView.getViewTreeObserver().removeOnPreDrawListener(this);
        if (paramObject == null) {}
        for (;;)
        {
          return true;
          FragmentTransitionCompat21.removeTargets(paramObject, paramArrayList);
          paramArrayList.clear();
          ArrayMap localArrayMap = BackStackRecord.this.mapSharedElementsIn(paramTransitionState, paramBoolean, paramFragment1);
          paramArrayList.add(paramTransitionState.nonExistentView);
          paramArrayList.addAll(localArrayMap.values());
          FragmentTransitionCompat21.addTargets(paramObject, paramArrayList);
          BackStackRecord.this.setEpicenterIn(localArrayMap, paramTransitionState);
          BackStackRecord.this.callSharedElementEnd(paramTransitionState, paramFragment1, paramFragment2, paramBoolean, localArrayMap);
        }
      }
    });
  }
  
  private static ArrayMap<String, View> remapNames(ArrayList<String> paramArrayList1, ArrayList<String> paramArrayList2, ArrayMap<String, View> paramArrayMap)
  {
    ArrayMap localArrayMap;
    int i;
    if (!paramArrayMap.isEmpty())
    {
      localArrayMap = new ArrayMap();
      int j = paramArrayList1.size();
      i = 0;
      if (i >= j) {
        return localArrayMap;
      }
    }
    else
    {
      return paramArrayMap;
    }
    View localView = (View)paramArrayMap.get(paramArrayList1.get(i));
    if (localView == null) {}
    for (;;)
    {
      i += 1;
      break;
      localArrayMap.put(paramArrayList2.get(i), localView);
    }
  }
  
  private ArrayMap<String, View> remapSharedElements(TransitionState paramTransitionState, Fragment paramFragment, boolean paramBoolean)
  {
    ArrayMap localArrayMap = new ArrayMap();
    if (this.mSharedElementSourceNames == null)
    {
      if (paramBoolean) {
        break label82;
      }
      if (paramFragment.mExitTransitionCallback != null) {
        break label116;
      }
    }
    for (;;)
    {
      setNameOverrides(paramTransitionState, localArrayMap, false);
      return localArrayMap;
      FragmentTransitionCompat21.findNamedViews(localArrayMap, paramFragment.getView());
      if (!paramBoolean)
      {
        localArrayMap = remapNames(this.mSharedElementSourceNames, this.mSharedElementTargetNames, localArrayMap);
        break;
      }
      localArrayMap.retainAll(this.mSharedElementTargetNames);
      break;
      label82:
      if (paramFragment.mEnterTransitionCallback == null) {}
      for (;;)
      {
        setBackNameOverrides(paramTransitionState, localArrayMap, false);
        return localArrayMap;
        paramFragment.mEnterTransitionCallback.onMapSharedElements(this.mSharedElementTargetNames, localArrayMap);
      }
      label116:
      paramFragment.mExitTransitionCallback.onMapSharedElements(this.mSharedElementTargetNames, localArrayMap);
    }
  }
  
  private void setBackNameOverrides(TransitionState paramTransitionState, ArrayMap<String, View> paramArrayMap, boolean paramBoolean)
  {
    if (this.mSharedElementTargetNames != null) {}
    int j;
    for (int i = this.mSharedElementTargetNames.size();; i = 0)
    {
      j = 0;
      if (j < i) {
        break;
      }
      return;
    }
    String str = (String)this.mSharedElementSourceNames.get(j);
    Object localObject = (View)paramArrayMap.get((String)this.mSharedElementTargetNames.get(j));
    if (localObject == null) {}
    for (;;)
    {
      j += 1;
      break;
      localObject = FragmentTransitionCompat21.getTransitionName((View)localObject);
      if (!paramBoolean) {
        setNameOverride(paramTransitionState.nameOverrides, (String)localObject, str);
      } else {
        setNameOverride(paramTransitionState.nameOverrides, str, (String)localObject);
      }
    }
  }
  
  private void setEpicenterIn(ArrayMap<String, View> paramArrayMap, TransitionState paramTransitionState)
  {
    if (this.mSharedElementTargetNames == null) {}
    do
    {
      do
      {
        return;
      } while (paramArrayMap.isEmpty());
      paramArrayMap = (View)paramArrayMap.get(this.mSharedElementTargetNames.get(0));
    } while (paramArrayMap == null);
    paramTransitionState.enteringEpicenterView.epicenter = paramArrayMap;
  }
  
  private static void setFirstOut(SparseArray<Fragment> paramSparseArray, Fragment paramFragment)
  {
    if (paramFragment == null) {}
    int i;
    do
    {
      return;
      i = paramFragment.mContainerId;
    } while ((i == 0) || (paramFragment.isHidden()) || (!paramFragment.isAdded()) || (paramFragment.getView() == null) || (paramSparseArray.get(i) != null));
    paramSparseArray.put(i, paramFragment);
  }
  
  private void setLastIn(SparseArray<Fragment> paramSparseArray, Fragment paramFragment)
  {
    if (paramFragment == null) {}
    int i;
    do
    {
      return;
      i = paramFragment.mContainerId;
    } while (i == 0);
    paramSparseArray.put(i, paramFragment);
  }
  
  private static void setNameOverride(ArrayMap<String, String> paramArrayMap, String paramString1, String paramString2)
  {
    int i = 0;
    if (paramString1 == null) {}
    while ((paramString2 == null) || (paramString1.equals(paramString2))) {
      return;
    }
    for (;;)
    {
      if (i >= paramArrayMap.size())
      {
        paramArrayMap.put(paramString1, paramString2);
        return;
      }
      if (paramString1.equals(paramArrayMap.valueAt(i))) {
        break;
      }
      i += 1;
    }
    paramArrayMap.setValueAt(i, paramString2);
  }
  
  private void setNameOverrides(TransitionState paramTransitionState, ArrayMap<String, View> paramArrayMap, boolean paramBoolean)
  {
    int j = paramArrayMap.size();
    int i = 0;
    if (i >= j) {
      return;
    }
    String str1 = (String)paramArrayMap.keyAt(i);
    String str2 = FragmentTransitionCompat21.getTransitionName((View)paramArrayMap.valueAt(i));
    if (!paramBoolean) {
      setNameOverride(paramTransitionState.nameOverrides, str2, str1);
    }
    for (;;)
    {
      i += 1;
      break;
      setNameOverride(paramTransitionState.nameOverrides, str1, str2);
    }
  }
  
  private static void setNameOverrides(TransitionState paramTransitionState, ArrayList<String> paramArrayList1, ArrayList<String> paramArrayList2)
  {
    if (paramArrayList1 == null) {}
    for (;;)
    {
      return;
      int i = 0;
      while (i < paramArrayList1.size())
      {
        String str1 = (String)paramArrayList1.get(i);
        String str2 = (String)paramArrayList2.get(i);
        setNameOverride(paramTransitionState.nameOverrides, str1, str2);
        i += 1;
      }
    }
  }
  
  public FragmentTransaction add(int paramInt, Fragment paramFragment)
  {
    doAddOp(paramInt, paramFragment, null, 1);
    return this;
  }
  
  public FragmentTransaction add(int paramInt, Fragment paramFragment, String paramString)
  {
    doAddOp(paramInt, paramFragment, paramString, 1);
    return this;
  }
  
  public FragmentTransaction add(Fragment paramFragment, String paramString)
  {
    doAddOp(0, paramFragment, paramString, 1);
    return this;
  }
  
  void addOp(Op paramOp)
  {
    if (this.mHead != null)
    {
      paramOp.prev = this.mTail;
      this.mTail.next = paramOp;
      this.mTail = paramOp;
    }
    for (;;)
    {
      paramOp.enterAnim = this.mEnterAnim;
      paramOp.exitAnim = this.mExitAnim;
      paramOp.popEnterAnim = this.mPopEnterAnim;
      paramOp.popExitAnim = this.mPopExitAnim;
      this.mNumOp += 1;
      return;
      this.mTail = paramOp;
      this.mHead = paramOp;
    }
  }
  
  public FragmentTransaction addSharedElement(View paramView, String paramString)
  {
    if (Build.VERSION.SDK_INT < 21) {
      return this;
    }
    paramView = FragmentTransitionCompat21.getTransitionName(paramView);
    if (paramView != null) {
      if (this.mSharedElementSourceNames == null) {
        break label57;
      }
    }
    for (;;)
    {
      this.mSharedElementSourceNames.add(paramView);
      this.mSharedElementTargetNames.add(paramString);
      return this;
      throw new IllegalArgumentException("Unique transitionNames are required for all sharedElements");
      label57:
      this.mSharedElementSourceNames = new ArrayList();
      this.mSharedElementTargetNames = new ArrayList();
    }
  }
  
  public FragmentTransaction addToBackStack(String paramString)
  {
    if (this.mAllowAddToBackStack)
    {
      this.mAddToBackStack = true;
      this.mName = paramString;
      return this;
    }
    throw new IllegalStateException("This FragmentTransaction is not allowed to be added to the back stack.");
  }
  
  public FragmentTransaction attach(Fragment paramFragment)
  {
    Op localOp = new Op();
    localOp.cmd = 7;
    localOp.fragment = paramFragment;
    addOp(localOp);
    return this;
  }
  
  void bumpBackStackNesting(int paramInt)
  {
    if (this.mAddToBackStack) {
      if (FragmentManagerImpl.DEBUG) {
        break label24;
      }
    }
    Op localOp;
    for (;;)
    {
      localOp = this.mHead;
      if (localOp != null) {
        break;
      }
      return;
      return;
      label24:
      Log.v("FragmentManager", "Bump nesting in " + this + " by " + paramInt);
    }
    if (localOp.fragment == null) {}
    Fragment localFragment;
    for (;;)
    {
      if (localOp.removed != null) {
        break label157;
      }
      localOp = localOp.next;
      break;
      localFragment = localOp.fragment;
      localFragment.mBackStackNesting += paramInt;
      if (FragmentManagerImpl.DEBUG) {
        Log.v("FragmentManager", "Bump nesting of " + localOp.fragment + " to " + localOp.fragment.mBackStackNesting);
      }
    }
    label157:
    int i = localOp.removed.size() - 1;
    label167:
    if (i >= 0)
    {
      localFragment = (Fragment)localOp.removed.get(i);
      localFragment.mBackStackNesting += paramInt;
      if (FragmentManagerImpl.DEBUG) {
        break label209;
      }
    }
    for (;;)
    {
      i -= 1;
      break label167;
      break;
      label209:
      Log.v("FragmentManager", "Bump nesting of " + localFragment + " to " + localFragment.mBackStackNesting);
    }
  }
  
  public void calculateBackFragments(SparseArray<Fragment> paramSparseArray1, SparseArray<Fragment> paramSparseArray2)
  {
    Op localOp;
    if (this.mManager.mContainer.hasView())
    {
      localOp = this.mHead;
      if (localOp != null) {}
    }
    else
    {
      return;
    }
    switch (localOp.cmd)
    {
    }
    for (;;)
    {
      localOp = localOp.next;
      break;
      setFirstOut(paramSparseArray1, localOp.fragment);
      continue;
      if (localOp.removed == null) {}
      for (;;)
      {
        setFirstOut(paramSparseArray1, localOp.fragment);
        break;
        int i = localOp.removed.size() - 1;
        while (i >= 0)
        {
          setLastIn(paramSparseArray2, (Fragment)localOp.removed.get(i));
          i -= 1;
        }
      }
      setLastIn(paramSparseArray2, localOp.fragment);
      continue;
      setLastIn(paramSparseArray2, localOp.fragment);
      continue;
      setFirstOut(paramSparseArray1, localOp.fragment);
      continue;
      setLastIn(paramSparseArray2, localOp.fragment);
      continue;
      setFirstOut(paramSparseArray1, localOp.fragment);
    }
  }
  
  public int commit()
  {
    return commitInternal(false);
  }
  
  public int commitAllowingStateLoss()
  {
    return commitInternal(true);
  }
  
  int commitInternal(boolean paramBoolean)
  {
    if (!this.mCommitted)
    {
      if (FragmentManagerImpl.DEBUG) {
        break label55;
      }
      this.mCommitted = true;
      if (this.mAddToBackStack) {
        break label109;
      }
    }
    label55:
    label109:
    for (this.mIndex = -1;; this.mIndex = this.mManager.allocBackStackIndex(this))
    {
      this.mManager.enqueueAction(this, paramBoolean);
      return this.mIndex;
      throw new IllegalStateException("commit already called");
      Log.v("FragmentManager", "Commit: " + this);
      dump("  ", null, new PrintWriter(new LogWriter("FragmentManager")), null);
      break;
    }
  }
  
  public FragmentTransaction detach(Fragment paramFragment)
  {
    Op localOp = new Op();
    localOp.cmd = 6;
    localOp.fragment = paramFragment;
    addOp(localOp);
    return this;
  }
  
  public FragmentTransaction disallowAddToBackStack()
  {
    if (!this.mAddToBackStack)
    {
      this.mAllowAddToBackStack = false;
      return this;
    }
    throw new IllegalStateException("This transaction is already being added to the back stack");
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    dump(paramString, paramPrintWriter, true);
  }
  
  public void dump(String paramString, PrintWriter paramPrintWriter, boolean paramBoolean)
  {
    if (!paramBoolean) {
      if (this.mHead != null) {
        break label342;
      }
    }
    label69:
    label76:
    label117:
    label124:
    label165:
    label172:
    label210:
    label302:
    label312:
    label322:
    label332:
    label342:
    String str2;
    Op localOp;
    int i;
    do
    {
      return;
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mName=");
      paramPrintWriter.print(this.mName);
      paramPrintWriter.print(" mIndex=");
      paramPrintWriter.print(this.mIndex);
      paramPrintWriter.print(" mCommitted=");
      paramPrintWriter.println(this.mCommitted);
      if (this.mTransition == 0)
      {
        if (this.mEnterAnim == 0) {
          break label302;
        }
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mEnterAnim=#");
        paramPrintWriter.print(Integer.toHexString(this.mEnterAnim));
        paramPrintWriter.print(" mExitAnim=#");
        paramPrintWriter.println(Integer.toHexString(this.mExitAnim));
        if (this.mPopEnterAnim == 0) {
          break label312;
        }
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mPopEnterAnim=#");
        paramPrintWriter.print(Integer.toHexString(this.mPopEnterAnim));
        paramPrintWriter.print(" mPopExitAnim=#");
        paramPrintWriter.println(Integer.toHexString(this.mPopExitAnim));
        if (this.mBreadCrumbTitleRes == 0) {
          break label322;
        }
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mBreadCrumbTitleRes=#");
        paramPrintWriter.print(Integer.toHexString(this.mBreadCrumbTitleRes));
        paramPrintWriter.print(" mBreadCrumbTitleText=");
        paramPrintWriter.println(this.mBreadCrumbTitleText);
        if (this.mBreadCrumbShortTitleRes == 0) {
          break label332;
        }
      }
      while (this.mBreadCrumbShortTitleText != null)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mBreadCrumbShortTitleRes=#");
        paramPrintWriter.print(Integer.toHexString(this.mBreadCrumbShortTitleRes));
        paramPrintWriter.print(" mBreadCrumbShortTitleText=");
        paramPrintWriter.println(this.mBreadCrumbShortTitleText);
        break;
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mTransition=#");
        paramPrintWriter.print(Integer.toHexString(this.mTransition));
        paramPrintWriter.print(" mTransitionStyle=#");
        paramPrintWriter.println(Integer.toHexString(this.mTransitionStyle));
        break label69;
        if (this.mExitAnim != 0) {
          break label76;
        }
        break label117;
        if (this.mPopExitAnim != 0) {
          break label124;
        }
        break label165;
        if (this.mBreadCrumbTitleText != null) {
          break label172;
        }
        break label210;
      }
      break;
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("Operations:");
      str2 = paramString + "    ";
      localOp = this.mHead;
      i = 0;
    } while (localOp == null);
    String str1;
    switch (localOp.cmd)
    {
    default: 
      str1 = "cmd=" + localOp.cmd;
      label466:
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("  Op #");
      paramPrintWriter.print(i);
      paramPrintWriter.print(": ");
      paramPrintWriter.print(str1);
      paramPrintWriter.print(" ");
      paramPrintWriter.println(localOp.fragment);
      if (!paramBoolean) {
        label517:
        if (localOp.removed != null) {
          break label732;
        }
      }
      break;
    }
    label613:
    label656:
    label721:
    label732:
    while (localOp.removed.size() <= 0)
    {
      localOp = localOp.next;
      i += 1;
      break;
      str1 = "NULL";
      break label466;
      str1 = "ADD";
      break label466;
      str1 = "REPLACE";
      break label466;
      str1 = "REMOVE";
      break label466;
      str1 = "HIDE";
      break label466;
      str1 = "SHOW";
      break label466;
      str1 = "DETACH";
      break label466;
      str1 = "ATTACH";
      break label466;
      if (localOp.enterAnim != 0)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("enterAnim=#");
        paramPrintWriter.print(Integer.toHexString(localOp.enterAnim));
        paramPrintWriter.print(" exitAnim=#");
        paramPrintWriter.println(Integer.toHexString(localOp.exitAnim));
        if (localOp.popEnterAnim == 0) {
          break label721;
        }
      }
      while (localOp.popExitAnim != 0)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("popEnterAnim=#");
        paramPrintWriter.print(Integer.toHexString(localOp.popEnterAnim));
        paramPrintWriter.print(" popExitAnim=#");
        paramPrintWriter.println(Integer.toHexString(localOp.popExitAnim));
        break;
        if (localOp.exitAnim != 0) {
          break label613;
        }
        break label656;
      }
      break label517;
    }
    int j = 0;
    label746:
    if (j < localOp.removed.size())
    {
      paramPrintWriter.print(str2);
      if (localOp.removed.size() == 1) {
        break label831;
      }
      if (j == 0) {
        break label841;
      }
    }
    for (;;)
    {
      paramPrintWriter.print(str2);
      paramPrintWriter.print("  #");
      paramPrintWriter.print(j);
      paramPrintWriter.print(": ");
      for (;;)
      {
        paramPrintWriter.println(localOp.removed.get(j));
        j += 1;
        break label746;
        break;
        label831:
        paramPrintWriter.print("Removed: ");
      }
      label841:
      paramPrintWriter.println("Removed:");
    }
  }
  
  public CharSequence getBreadCrumbShortTitle()
  {
    if (this.mBreadCrumbShortTitleRes == 0) {
      return this.mBreadCrumbShortTitleText;
    }
    return this.mManager.mActivity.getText(this.mBreadCrumbShortTitleRes);
  }
  
  public int getBreadCrumbShortTitleRes()
  {
    return this.mBreadCrumbShortTitleRes;
  }
  
  public CharSequence getBreadCrumbTitle()
  {
    if (this.mBreadCrumbTitleRes == 0) {
      return this.mBreadCrumbTitleText;
    }
    return this.mManager.mActivity.getText(this.mBreadCrumbTitleRes);
  }
  
  public int getBreadCrumbTitleRes()
  {
    return this.mBreadCrumbTitleRes;
  }
  
  public int getId()
  {
    return this.mIndex;
  }
  
  public String getName()
  {
    return this.mName;
  }
  
  public int getTransition()
  {
    return this.mTransition;
  }
  
  public int getTransitionStyle()
  {
    return this.mTransitionStyle;
  }
  
  public FragmentTransaction hide(Fragment paramFragment)
  {
    Op localOp = new Op();
    localOp.cmd = 4;
    localOp.fragment = paramFragment;
    addOp(localOp);
    return this;
  }
  
  public boolean isAddToBackStackAllowed()
  {
    return this.mAllowAddToBackStack;
  }
  
  public boolean isEmpty()
  {
    return this.mNumOp == 0;
  }
  
  public TransitionState popFromBackStack(boolean paramBoolean, TransitionState paramTransitionState, SparseArray<Fragment> paramSparseArray1, SparseArray<Fragment> paramSparseArray2)
  {
    label14:
    int i;
    label29:
    int j;
    if (!FragmentManagerImpl.DEBUG)
    {
      if (paramTransitionState == null) {
        break label115;
      }
      if (!paramBoolean) {
        break label145;
      }
      bumpBackStackNesting(-1);
      if (paramTransitionState != null) {
        break label160;
      }
      i = this.mTransitionStyle;
      if (paramTransitionState != null) {
        break label166;
      }
      j = this.mTransition;
      label39:
      paramSparseArray1 = this.mTail;
      if (paramSparseArray1 != null) {
        break label172;
      }
      if (paramBoolean) {
        break label573;
      }
    }
    for (;;)
    {
      if (this.mIndex >= 0) {
        break label600;
      }
      return paramTransitionState;
      Log.v("FragmentManager", "popFromBackStack: " + this);
      dump("  ", null, new PrintWriter(new LogWriter("FragmentManager")), null);
      break;
      label115:
      if (paramSparseArray1.size() != 0) {}
      while (paramSparseArray2.size() != 0)
      {
        paramTransitionState = beginTransition(paramSparseArray1, paramSparseArray2, true);
        break;
      }
      break label14;
      label145:
      setNameOverrides(paramTransitionState, this.mSharedElementTargetNames, this.mSharedElementSourceNames);
      break label14;
      label160:
      i = 0;
      break label29;
      label166:
      j = 0;
      break label39;
      label172:
      int k;
      if (paramTransitionState == null)
      {
        k = paramSparseArray1.popEnterAnim;
        label182:
        if (paramTransitionState != null) {
          break label277;
        }
      }
      label277:
      for (int m = paramSparseArray1.popExitAnim;; m = 0) {
        switch (paramSparseArray1.cmd)
        {
        default: 
          throw new IllegalArgumentException("Unknown cmd: " + paramSparseArray1.cmd);
          k = 0;
          break label182;
        }
      }
      paramSparseArray2 = paramSparseArray1.fragment;
      paramSparseArray2.mNextAnim = m;
      this.mManager.removeFragment(paramSparseArray2, FragmentManagerImpl.reverseTransit(j), i);
      for (;;)
      {
        paramSparseArray1 = paramSparseArray1.prev;
        break;
        paramSparseArray2 = paramSparseArray1.fragment;
        if (paramSparseArray2 == null) {}
        while (paramSparseArray1.removed != null)
        {
          m = 0;
          while (m < paramSparseArray1.removed.size())
          {
            paramSparseArray2 = (Fragment)paramSparseArray1.removed.get(m);
            paramSparseArray2.mNextAnim = k;
            this.mManager.addFragment(paramSparseArray2, false);
            m += 1;
          }
          paramSparseArray2.mNextAnim = m;
          this.mManager.removeFragment(paramSparseArray2, FragmentManagerImpl.reverseTransit(j), i);
        }
        paramSparseArray2 = paramSparseArray1.fragment;
        paramSparseArray2.mNextAnim = k;
        this.mManager.addFragment(paramSparseArray2, false);
        continue;
        paramSparseArray2 = paramSparseArray1.fragment;
        paramSparseArray2.mNextAnim = k;
        this.mManager.showFragment(paramSparseArray2, FragmentManagerImpl.reverseTransit(j), i);
        continue;
        paramSparseArray2 = paramSparseArray1.fragment;
        paramSparseArray2.mNextAnim = m;
        this.mManager.hideFragment(paramSparseArray2, FragmentManagerImpl.reverseTransit(j), i);
        continue;
        paramSparseArray2 = paramSparseArray1.fragment;
        paramSparseArray2.mNextAnim = k;
        this.mManager.attachFragment(paramSparseArray2, FragmentManagerImpl.reverseTransit(j), i);
        continue;
        paramSparseArray2 = paramSparseArray1.fragment;
        paramSparseArray2.mNextAnim = k;
        this.mManager.detachFragment(paramSparseArray2, FragmentManagerImpl.reverseTransit(j), i);
      }
      label573:
      this.mManager.moveToState(this.mManager.mCurState, FragmentManagerImpl.reverseTransit(j), i, true);
      paramTransitionState = null;
    }
    label600:
    this.mManager.freeBackStackIndex(this.mIndex);
    this.mIndex = -1;
    return paramTransitionState;
  }
  
  public FragmentTransaction remove(Fragment paramFragment)
  {
    Op localOp = new Op();
    localOp.cmd = 3;
    localOp.fragment = paramFragment;
    addOp(localOp);
    return this;
  }
  
  public FragmentTransaction replace(int paramInt, Fragment paramFragment)
  {
    return replace(paramInt, paramFragment, null);
  }
  
  public FragmentTransaction replace(int paramInt, Fragment paramFragment, String paramString)
  {
    if (paramInt != 0)
    {
      doAddOp(paramInt, paramFragment, paramString, 2);
      return this;
    }
    throw new IllegalArgumentException("Must use non-zero containerViewId");
  }
  
  public void run()
  {
    label13:
    Object localObject2;
    label29:
    int i;
    if (!FragmentManagerImpl.DEBUG)
    {
      if (this.mAddToBackStack) {
        break label114;
      }
      bumpBackStackNesting(1);
      if (Build.VERSION.SDK_INT >= 21) {
        break label132;
      }
      localObject2 = null;
      if (localObject2 != null) {
        break label172;
      }
      i = this.mTransitionStyle;
      label39:
      if (localObject2 != null) {
        break label177;
      }
    }
    Op localOp;
    label114:
    label132:
    label172:
    label177:
    for (int j = this.mTransition;; j = 0)
    {
      localOp = this.mHead;
      if (localOp != null) {
        break label182;
      }
      this.mManager.moveToState(this.mManager.mCurState, j, i, true);
      if (this.mAddToBackStack) {
        break label762;
      }
      return;
      Log.v("FragmentManager", "Run: " + this);
      break;
      if (this.mIndex >= 0) {
        break label13;
      }
      throw new IllegalStateException("addToBackStack() called after commit()");
      localObject1 = new SparseArray();
      localObject2 = new SparseArray();
      calculateFragments((SparseArray)localObject1, (SparseArray)localObject2);
      localObject2 = beginTransition((SparseArray)localObject1, (SparseArray)localObject2, false);
      break label29;
      i = 0;
      break label39;
    }
    label182:
    int k;
    if (localObject2 == null)
    {
      k = localOp.enterAnim;
      label193:
      if (localObject2 != null) {
        break label289;
      }
    }
    label289:
    for (int m = localOp.exitAnim;; m = 0) {
      switch (localOp.cmd)
      {
      default: 
        throw new IllegalArgumentException("Unknown cmd: " + localOp.cmd);
        k = 0;
        break label193;
      }
    }
    Object localObject1 = localOp.fragment;
    ((Fragment)localObject1).mNextAnim = k;
    this.mManager.addFragment((Fragment)localObject1, false);
    for (;;)
    {
      localOp = localOp.next;
      break;
      localObject1 = localOp.fragment;
      if (this.mManager.mAdded == null)
      {
        if (localObject1 != null)
        {
          ((Fragment)localObject1).mNextAnim = k;
          this.mManager.addFragment((Fragment)localObject1, false);
        }
      }
      else
      {
        int n = 0;
        label372:
        Fragment localFragment;
        if (n < this.mManager.mAdded.size())
        {
          localFragment = (Fragment)this.mManager.mAdded.get(n);
          if (FragmentManagerImpl.DEBUG) {
            break label478;
          }
          label413:
          if (localObject1 != null) {
            break label519;
          }
          label418:
          if (localFragment == localObject1) {
            break label535;
          }
          if (localOp.removed == null) {
            break label547;
          }
          label433:
          localOp.removed.add(localFragment);
          localFragment.mNextAnim = m;
          if (this.mAddToBackStack) {
            break label562;
          }
        }
        for (;;)
        {
          this.mManager.removeFragment(localFragment, j, i);
          for (;;)
          {
            n += 1;
            break label372;
            break;
            label478:
            Log.v("FragmentManager", "OP_REPLACE: adding=" + localObject1 + " old=" + localFragment);
            break label413;
            label519:
            if (localFragment.mContainerId == ((Fragment)localObject1).mContainerId) {
              break label418;
            }
            continue;
            label535:
            localOp.fragment = null;
            localObject1 = null;
          }
          label547:
          localOp.removed = new ArrayList();
          break label433;
          label562:
          localFragment.mBackStackNesting += 1;
          if (FragmentManagerImpl.DEBUG) {
            Log.v("FragmentManager", "Bump nesting of " + localFragment + " to " + localFragment.mBackStackNesting);
          }
        }
        localObject1 = localOp.fragment;
        ((Fragment)localObject1).mNextAnim = m;
        this.mManager.removeFragment((Fragment)localObject1, j, i);
        continue;
        localObject1 = localOp.fragment;
        ((Fragment)localObject1).mNextAnim = m;
        this.mManager.hideFragment((Fragment)localObject1, j, i);
        continue;
        localObject1 = localOp.fragment;
        ((Fragment)localObject1).mNextAnim = k;
        this.mManager.showFragment((Fragment)localObject1, j, i);
        continue;
        localObject1 = localOp.fragment;
        ((Fragment)localObject1).mNextAnim = m;
        this.mManager.detachFragment((Fragment)localObject1, j, i);
        continue;
        localObject1 = localOp.fragment;
        ((Fragment)localObject1).mNextAnim = k;
        this.mManager.attachFragment((Fragment)localObject1, j, i);
      }
    }
    label762:
    this.mManager.addBackStackState(this);
  }
  
  public FragmentTransaction setBreadCrumbShortTitle(int paramInt)
  {
    this.mBreadCrumbShortTitleRes = paramInt;
    this.mBreadCrumbShortTitleText = null;
    return this;
  }
  
  public FragmentTransaction setBreadCrumbShortTitle(CharSequence paramCharSequence)
  {
    this.mBreadCrumbShortTitleRes = 0;
    this.mBreadCrumbShortTitleText = paramCharSequence;
    return this;
  }
  
  public FragmentTransaction setBreadCrumbTitle(int paramInt)
  {
    this.mBreadCrumbTitleRes = paramInt;
    this.mBreadCrumbTitleText = null;
    return this;
  }
  
  public FragmentTransaction setBreadCrumbTitle(CharSequence paramCharSequence)
  {
    this.mBreadCrumbTitleRes = 0;
    this.mBreadCrumbTitleText = paramCharSequence;
    return this;
  }
  
  public FragmentTransaction setCustomAnimations(int paramInt1, int paramInt2)
  {
    return setCustomAnimations(paramInt1, paramInt2, 0, 0);
  }
  
  public FragmentTransaction setCustomAnimations(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mEnterAnim = paramInt1;
    this.mExitAnim = paramInt2;
    this.mPopEnterAnim = paramInt3;
    this.mPopExitAnim = paramInt4;
    return this;
  }
  
  public FragmentTransaction setTransition(int paramInt)
  {
    this.mTransition = paramInt;
    return this;
  }
  
  public FragmentTransaction setTransitionStyle(int paramInt)
  {
    this.mTransitionStyle = paramInt;
    return this;
  }
  
  public FragmentTransaction show(Fragment paramFragment)
  {
    Op localOp = new Op();
    localOp.cmd = 5;
    localOp.fragment = paramFragment;
    addOp(localOp);
    return this;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    localStringBuilder.append("BackStackEntry{");
    localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
    if (this.mIndex < 0) {
      if (this.mName != null) {
        break label78;
      }
    }
    for (;;)
    {
      localStringBuilder.append("}");
      return localStringBuilder.toString();
      localStringBuilder.append(" #");
      localStringBuilder.append(this.mIndex);
      break;
      label78:
      localStringBuilder.append(" ");
      localStringBuilder.append(this.mName);
    }
  }
  
  static final class Op
  {
    int cmd;
    int enterAnim;
    int exitAnim;
    Fragment fragment;
    Op next;
    int popEnterAnim;
    int popExitAnim;
    Op prev;
    ArrayList<Fragment> removed;
  }
  
  public class TransitionState
  {
    public FragmentTransitionCompat21.EpicenterView enteringEpicenterView = new FragmentTransitionCompat21.EpicenterView();
    public ArrayList<View> hiddenFragmentViews = new ArrayList();
    public ArrayMap<String, String> nameOverrides = new ArrayMap();
    public View nonExistentView;
    
    public TransitionState() {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/app/BackStackRecord.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */