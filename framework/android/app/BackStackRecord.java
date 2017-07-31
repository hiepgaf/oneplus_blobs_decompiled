package android.app;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Rect;
import android.transition.Transition;
import android.transition.Transition.EpicenterCallback;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.ArrayMap;
import android.util.Log;
import android.util.LogWriter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import com.android.internal.util.FastPrintWriter;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

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
  
  public static void addTargets(Transition paramTransition, ArrayList<View> paramArrayList)
  {
    int j;
    int i;
    if ((paramTransition instanceof TransitionSet))
    {
      paramTransition = (TransitionSet)paramTransition;
      j = paramTransition.getTransitionCount();
      i = 0;
      while (i < j)
      {
        addTargets(paramTransition.getTransitionAt(i), paramArrayList);
        i += 1;
      }
    }
    if ((!hasSimpleTarget(paramTransition)) && (isNullOrEmpty(paramTransition.getTargets())))
    {
      j = paramArrayList.size();
      i = 0;
      while (i < j)
      {
        paramTransition.addTarget((View)paramArrayList.get(i));
        i += 1;
      }
    }
  }
  
  private ArrayList<View> addTransitionTargets(final TransitionState paramTransitionState, final Transition paramTransition1, final TransitionSet paramTransitionSet, final Transition paramTransition2, final Transition paramTransition3, final View paramView, final Fragment paramFragment1, final Fragment paramFragment2, final ArrayList<View> paramArrayList1, final boolean paramBoolean, final ArrayList<View> paramArrayList2)
  {
    if ((paramTransition1 == null) && (paramTransitionSet == null) && (paramTransition3 == null)) {
      return null;
    }
    final ArrayList localArrayList = new ArrayList();
    paramView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
    {
      public boolean onPreDraw()
      {
        paramView.getViewTreeObserver().removeOnPreDrawListener(this);
        if (paramFragment1 != null) {
          BackStackRecord.-wrap2(BackStackRecord.this, paramArrayList1, paramFragment1.mContainerId, paramTransition3);
        }
        ArrayMap localArrayMap = null;
        if (paramTransitionSet != null)
        {
          localArrayMap = BackStackRecord.-wrap0(BackStackRecord.this, paramTransitionState, paramBoolean, paramFragment1);
          BackStackRecord.removeTargets(paramTransitionSet, paramArrayList2);
          paramArrayList2.remove(paramTransitionState.nonExistentView);
          BackStackRecord.-wrap3(paramTransition2, paramTransitionSet, paramArrayList2, false);
          BackStackRecord.-wrap3(paramTransition1, paramTransitionSet, paramArrayList2, false);
          BackStackRecord.-wrap6(paramTransitionSet, paramTransitionState.nonExistentView, localArrayMap, paramArrayList2);
          BackStackRecord.-wrap4(BackStackRecord.this, localArrayMap, paramTransitionState);
          BackStackRecord.-wrap1(BackStackRecord.this, paramTransitionState, paramFragment1, paramFragment2, paramBoolean, localArrayMap);
        }
        if (paramTransition1 != null)
        {
          paramTransition1.removeTarget(paramTransitionState.nonExistentView);
          View localView = paramFragment1.getView();
          if (localView != null)
          {
            localView.captureTransitioningViews(localArrayList);
            if (localArrayMap != null) {
              localArrayList.removeAll(localArrayMap.values());
            }
            localArrayList.add(paramTransitionState.nonExistentView);
            BackStackRecord.addTargets(paramTransition1, localArrayList);
          }
          BackStackRecord.-wrap5(BackStackRecord.this, paramTransition1, paramTransitionState);
        }
        BackStackRecord.-wrap3(paramTransition2, paramTransition1, localArrayList, true);
        BackStackRecord.-wrap3(paramTransition2, paramTransitionSet, paramArrayList2, true);
        BackStackRecord.-wrap3(paramTransition1, paramTransitionSet, paramArrayList2, true);
        return true;
      }
    });
    return localArrayList;
  }
  
  private TransitionState beginTransition(SparseArray<Fragment> paramSparseArray1, SparseArray<Fragment> paramSparseArray2, boolean paramBoolean)
  {
    TransitionState localTransitionState = new TransitionState();
    localTransitionState.nonExistentView = new View(this.mManager.mHost.getContext());
    int i = 0;
    while (i < paramSparseArray1.size())
    {
      configureTransitions(paramSparseArray1.keyAt(i), localTransitionState, paramBoolean, paramSparseArray1, paramSparseArray2);
      i += 1;
    }
    i = 0;
    while (i < paramSparseArray2.size())
    {
      int j = paramSparseArray2.keyAt(i);
      if (paramSparseArray1.get(j) == null) {
        configureTransitions(j, localTransitionState, paramBoolean, paramSparseArray1, paramSparseArray2);
      }
      i += 1;
    }
    return localTransitionState;
  }
  
  private static void bfsAddViewChildren(List<View> paramList, View paramView)
  {
    int k = paramList.size();
    if (containedBeforeIndex(paramList, paramView, k)) {
      return;
    }
    paramList.add(paramView);
    int i = k;
    while (i < paramList.size())
    {
      paramView = (View)paramList.get(i);
      if ((paramView instanceof ViewGroup))
      {
        paramView = (ViewGroup)paramView;
        int m = paramView.getChildCount();
        int j = 0;
        while (j < m)
        {
          View localView = paramView.getChildAt(j);
          if (!containedBeforeIndex(paramList, localView, k)) {
            paramList.add(localView);
          }
          j += 1;
        }
      }
      i += 1;
    }
  }
  
  private void calculateFragments(SparseArray<Fragment> paramSparseArray1, SparseArray<Fragment> paramSparseArray2)
  {
    if (!this.mManager.mContainer.onHasView()) {
      return;
    }
    Op localOp = this.mHead;
    if (localOp != null)
    {
      switch (localOp.cmd)
      {
      }
      for (;;)
      {
        localOp = localOp.next;
        break;
        setLastIn(paramSparseArray1, paramSparseArray2, localOp.fragment);
        continue;
        Object localObject1 = localOp.fragment;
        if (this.mManager.mAdded != null)
        {
          int i = 0;
          if (i < this.mManager.mAdded.size())
          {
            Fragment localFragment = (Fragment)this.mManager.mAdded.get(i);
            Object localObject2;
            if (localObject1 != null)
            {
              localObject2 = localObject1;
              if (localFragment.mContainerId != ((Fragment)localObject1).mContainerId) {}
            }
            else
            {
              if (localFragment != localObject1) {
                break label197;
              }
              localObject2 = null;
              paramSparseArray2.remove(localFragment.mContainerId);
            }
            for (;;)
            {
              i += 1;
              localObject1 = localObject2;
              break;
              label197:
              setFirstOut(paramSparseArray1, paramSparseArray2, localFragment);
              localObject2 = localObject1;
            }
          }
        }
        setLastIn(paramSparseArray1, paramSparseArray2, localOp.fragment);
        continue;
        setFirstOut(paramSparseArray1, paramSparseArray2, localOp.fragment);
        continue;
        setFirstOut(paramSparseArray1, paramSparseArray2, localOp.fragment);
        continue;
        setLastIn(paramSparseArray1, paramSparseArray2, localOp.fragment);
        continue;
        setFirstOut(paramSparseArray1, paramSparseArray2, localOp.fragment);
        continue;
        setLastIn(paramSparseArray1, paramSparseArray2, localOp.fragment);
      }
    }
  }
  
  private void callSharedElementEnd(TransitionState paramTransitionState, Fragment paramFragment1, Fragment paramFragment2, boolean paramBoolean, ArrayMap<String, View> paramArrayMap)
  {
    if (paramBoolean) {}
    for (paramTransitionState = paramFragment2.mEnterTransitionCallback;; paramTransitionState = paramFragment1.mEnterTransitionCallback)
    {
      paramTransitionState.onSharedElementEnd(new ArrayList(paramArrayMap.keySet()), new ArrayList(paramArrayMap.values()), null);
      return;
    }
  }
  
  private static ArrayList<View> captureExitingViews(Transition paramTransition, Fragment paramFragment, ArrayMap<String, View> paramArrayMap, View paramView)
  {
    Object localObject = null;
    if (paramTransition != null)
    {
      ArrayList localArrayList = new ArrayList();
      paramFragment.getView().captureTransitioningViews(localArrayList);
      if (paramArrayMap != null) {
        localArrayList.removeAll(paramArrayMap.values());
      }
      localObject = localArrayList;
      if (!localArrayList.isEmpty())
      {
        localArrayList.add(paramView);
        addTargets(paramTransition, localArrayList);
        localObject = localArrayList;
      }
    }
    return (ArrayList<View>)localObject;
  }
  
  private static Transition cloneTransition(Transition paramTransition)
  {
    Transition localTransition = paramTransition;
    if (paramTransition != null) {
      localTransition = paramTransition.clone();
    }
    return localTransition;
  }
  
  private void configureTransitions(int paramInt, TransitionState paramTransitionState, boolean paramBoolean, SparseArray<Fragment> paramSparseArray1, SparseArray<Fragment> paramSparseArray2)
  {
    ViewGroup localViewGroup = (ViewGroup)this.mManager.mContainer.onFindViewById(paramInt);
    Object localObject2;
    Fragment localFragment;
    Transition localTransition;
    TransitionSet localTransitionSet;
    Object localObject1;
    ArrayList localArrayList1;
    if (localViewGroup != null)
    {
      localObject2 = (Fragment)paramSparseArray2.get(paramInt);
      localFragment = (Fragment)paramSparseArray1.get(paramInt);
      localTransition = getEnterTransition((Fragment)localObject2, paramBoolean);
      localTransitionSet = getSharedElementTransition((Fragment)localObject2, localFragment, paramBoolean);
      localObject1 = getExitTransition(localFragment, paramBoolean);
      if ((localTransition == null) && (localTransitionSet == null) && (localObject1 == null)) {
        return;
      }
      if (localTransition != null) {
        localTransition.addTarget(paramTransitionState.nonExistentView);
      }
      paramSparseArray2 = null;
      localArrayList1 = new ArrayList();
      if (localTransitionSet != null)
      {
        paramSparseArray2 = remapSharedElements(paramTransitionState, localFragment, paramBoolean);
        setSharedElementTargets(localTransitionSet, paramTransitionState.nonExistentView, paramSparseArray2, localArrayList1);
        if (!paramBoolean) {
          break label422;
        }
      }
    }
    label422:
    for (paramSparseArray1 = localFragment.mEnterTransitionCallback;; paramSparseArray1 = ((Fragment)localObject2).mEnterTransitionCallback)
    {
      paramSparseArray1.onSharedElementStart(new ArrayList(paramSparseArray2.keySet()), new ArrayList(paramSparseArray2.values()), null);
      ArrayList localArrayList2 = captureExitingViews((Transition)localObject1, localFragment, paramSparseArray2, paramTransitionState.nonExistentView);
      if (localArrayList2 != null)
      {
        paramSparseArray1 = (SparseArray<Fragment>)localObject1;
        if (!localArrayList2.isEmpty()) {}
      }
      else
      {
        paramSparseArray1 = null;
      }
      excludeViews(localTransition, paramSparseArray1, localArrayList2, true);
      excludeViews(localTransition, localTransitionSet, localArrayList1, true);
      excludeViews(paramSparseArray1, localTransitionSet, localArrayList1, true);
      if ((this.mSharedElementTargetNames != null) && (paramSparseArray2 != null))
      {
        paramSparseArray2 = (View)paramSparseArray2.get(this.mSharedElementTargetNames.get(0));
        if (paramSparseArray2 != null)
        {
          if (paramSparseArray1 != null) {
            setEpicenter(paramSparseArray1, paramSparseArray2);
          }
          if (localTransitionSet != null) {
            setEpicenter(localTransitionSet, paramSparseArray2);
          }
        }
      }
      paramSparseArray2 = mergeTransitions(localTransition, paramSparseArray1, localTransitionSet, (Fragment)localObject2, paramBoolean);
      if (paramSparseArray2 != null)
      {
        localObject1 = new ArrayList();
        localObject2 = addTransitionTargets(paramTransitionState, localTransition, localTransitionSet, paramSparseArray1, paramSparseArray2, localViewGroup, (Fragment)localObject2, localFragment, (ArrayList)localObject1, paramBoolean, localArrayList1);
        paramSparseArray2.setNameOverrides(paramTransitionState.nameOverrides);
        paramSparseArray2.excludeTarget(paramTransitionState.nonExistentView, true);
        excludeHiddenFragments((ArrayList)localObject1, paramInt, paramSparseArray2);
        TransitionManager.beginDelayedTransition(localViewGroup, paramSparseArray2);
        removeTargetedViewsFromTransitions(localViewGroup, paramTransitionState.nonExistentView, localTransition, (ArrayList)localObject2, paramSparseArray1, localArrayList2, localTransitionSet, localArrayList1, paramSparseArray2, (ArrayList)localObject1);
      }
      return;
    }
  }
  
  private static boolean containedBeforeIndex(List<View> paramList, View paramView, int paramInt)
  {
    int i = 0;
    while (i < paramInt)
    {
      if (paramList.get(i) == paramView) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private void doAddOp(int paramInt1, Fragment paramFragment, String paramString, int paramInt2)
  {
    paramFragment.mFragmentManager = this.mManager;
    if (paramString != null)
    {
      if ((paramFragment.mTag == null) || (paramString.equals(paramFragment.mTag))) {
        paramFragment.mTag = paramString;
      }
    }
    else
    {
      if (paramInt1 == 0) {
        break label215;
      }
      if (paramInt1 != -1) {
        break label139;
      }
      throw new IllegalArgumentException("Can't add fragment " + paramFragment + " with tag " + paramString + " to container view with no id");
    }
    throw new IllegalStateException("Can't change tag of fragment " + paramFragment + ": was " + paramFragment.mTag + " now " + paramString);
    label139:
    if ((paramFragment.mFragmentId != 0) && (paramFragment.mFragmentId != paramInt1)) {
      throw new IllegalStateException("Can't change container ID of fragment " + paramFragment + ": was " + paramFragment.mFragmentId + " now " + paramInt1);
    }
    paramFragment.mFragmentId = paramInt1;
    paramFragment.mContainerId = paramInt1;
    label215:
    paramString = new Op();
    paramString.cmd = paramInt2;
    paramString.fragment = paramFragment;
    addOp(paramString);
  }
  
  private void excludeHiddenFragments(ArrayList<View> paramArrayList, int paramInt, Transition paramTransition)
  {
    if (this.mManager.mAdded != null)
    {
      int i = 0;
      if (i < this.mManager.mAdded.size())
      {
        Fragment localFragment = (Fragment)this.mManager.mAdded.get(i);
        if ((localFragment.mView != null) && (localFragment.mContainer != null) && (localFragment.mContainerId == paramInt))
        {
          if (!localFragment.mHidden) {
            break label120;
          }
          if (!paramArrayList.contains(localFragment.mView))
          {
            paramTransition.excludeTarget(localFragment.mView, true);
            paramArrayList.add(localFragment.mView);
          }
        }
        for (;;)
        {
          i += 1;
          break;
          label120:
          paramTransition.excludeTarget(localFragment.mView, false);
          paramArrayList.remove(localFragment.mView);
        }
      }
    }
  }
  
  private static void excludeViews(Transition paramTransition1, Transition paramTransition2, ArrayList<View> paramArrayList, boolean paramBoolean)
  {
    if (paramTransition1 != null)
    {
      if (paramTransition2 == null) {}
      for (int i = 0;; i = paramArrayList.size())
      {
        int j = 0;
        while (j < i)
        {
          paramTransition1.excludeTarget((View)paramArrayList.get(j), paramBoolean);
          j += 1;
        }
      }
    }
  }
  
  private static Transition getEnterTransition(Fragment paramFragment, boolean paramBoolean)
  {
    if (paramFragment == null) {
      return null;
    }
    if (paramBoolean) {}
    for (paramFragment = paramFragment.getReenterTransition();; paramFragment = paramFragment.getEnterTransition()) {
      return cloneTransition(paramFragment);
    }
  }
  
  private static Transition getExitTransition(Fragment paramFragment, boolean paramBoolean)
  {
    if (paramFragment == null) {
      return null;
    }
    if (paramBoolean) {}
    for (paramFragment = paramFragment.getReturnTransition();; paramFragment = paramFragment.getExitTransition()) {
      return cloneTransition(paramFragment);
    }
  }
  
  private static TransitionSet getSharedElementTransition(Fragment paramFragment1, Fragment paramFragment2, boolean paramBoolean)
  {
    if ((paramFragment1 == null) || (paramFragment2 == null)) {
      return null;
    }
    if (paramBoolean) {}
    for (paramFragment1 = paramFragment2.getSharedElementReturnTransition();; paramFragment1 = paramFragment1.getSharedElementEnterTransition())
    {
      paramFragment1 = cloneTransition(paramFragment1);
      if (paramFragment1 != null) {
        break;
      }
      return null;
    }
    paramFragment2 = new TransitionSet();
    paramFragment2.addTransition(paramFragment1);
    return paramFragment2;
  }
  
  private static boolean hasSimpleTarget(Transition paramTransition)
  {
    boolean bool2 = true;
    boolean bool1 = bool2;
    if (isNullOrEmpty(paramTransition.getTargetIds()))
    {
      bool1 = bool2;
      if (isNullOrEmpty(paramTransition.getTargetNames()))
      {
        bool1 = bool2;
        if (isNullOrEmpty(paramTransition.getTargetTypes())) {
          bool1 = false;
        }
      }
    }
    return bool1;
  }
  
  private static boolean isNullOrEmpty(List paramList)
  {
    if (paramList != null) {
      return paramList.isEmpty();
    }
    return true;
  }
  
  private ArrayMap<String, View> mapEnteringSharedElements(TransitionState paramTransitionState, Fragment paramFragment, boolean paramBoolean)
  {
    ArrayMap localArrayMap = new ArrayMap();
    paramFragment = paramFragment.getView();
    paramTransitionState = localArrayMap;
    if (paramFragment != null)
    {
      paramTransitionState = localArrayMap;
      if (this.mSharedElementSourceNames != null)
      {
        paramFragment.findNamedViews(localArrayMap);
        if (!paramBoolean) {
          break label57;
        }
        paramTransitionState = remapNames(this.mSharedElementSourceNames, this.mSharedElementTargetNames, localArrayMap);
      }
    }
    return paramTransitionState;
    label57:
    localArrayMap.retainAll(this.mSharedElementTargetNames);
    return localArrayMap;
  }
  
  private ArrayMap<String, View> mapSharedElementsIn(TransitionState paramTransitionState, boolean paramBoolean, Fragment paramFragment)
  {
    ArrayMap localArrayMap = mapEnteringSharedElements(paramTransitionState, paramFragment, paramBoolean);
    if (paramBoolean)
    {
      paramFragment.mExitTransitionCallback.onMapSharedElements(this.mSharedElementTargetNames, localArrayMap);
      setBackNameOverrides(paramTransitionState, localArrayMap, true);
      return localArrayMap;
    }
    paramFragment.mEnterTransitionCallback.onMapSharedElements(this.mSharedElementTargetNames, localArrayMap);
    setNameOverrides(paramTransitionState, localArrayMap, true);
    return localArrayMap;
  }
  
  private static Transition mergeTransitions(Transition paramTransition1, Transition paramTransition2, Transition paramTransition3, Fragment paramFragment, boolean paramBoolean)
  {
    boolean bool2 = true;
    boolean bool1 = bool2;
    if (paramTransition1 != null)
    {
      bool1 = bool2;
      if (paramTransition2 != null)
      {
        bool1 = bool2;
        if (paramFragment != null) {
          if (!paramBoolean) {
            break label83;
          }
        }
      }
    }
    label83:
    for (bool1 = paramFragment.getAllowReturnTransitionOverlap(); bool1; bool1 = paramFragment.getAllowEnterTransitionOverlap())
    {
      paramFragment = new TransitionSet();
      if (paramTransition1 != null) {
        paramFragment.addTransition(paramTransition1);
      }
      if (paramTransition2 != null) {
        paramFragment.addTransition(paramTransition2);
      }
      if (paramTransition3 != null) {
        paramFragment.addTransition(paramTransition3);
      }
      return paramFragment;
    }
    paramFragment = null;
    if ((paramTransition2 != null) && (paramTransition1 != null)) {
      paramTransition2 = new TransitionSet().addTransition(paramTransition2).addTransition(paramTransition1).setOrdering(1);
    }
    while (paramTransition3 != null)
    {
      paramTransition1 = new TransitionSet();
      if (paramTransition2 != null) {
        paramTransition1.addTransition(paramTransition2);
      }
      paramTransition1.addTransition(paramTransition3);
      return paramTransition1;
      if (paramTransition2 == null)
      {
        paramTransition2 = paramFragment;
        if (paramTransition1 != null) {
          paramTransition2 = paramTransition1;
        }
      }
    }
    return paramTransition2;
  }
  
  private static ArrayMap<String, View> remapNames(ArrayList<String> paramArrayList1, ArrayList<String> paramArrayList2, ArrayMap<String, View> paramArrayMap)
  {
    ArrayMap localArrayMap = new ArrayMap();
    if (!paramArrayMap.isEmpty())
    {
      int j = paramArrayList1.size();
      int i = 0;
      while (i < j)
      {
        View localView = (View)paramArrayMap.get(paramArrayList1.get(i));
        if (localView != null) {
          localArrayMap.put((String)paramArrayList2.get(i), localView);
        }
        i += 1;
      }
    }
    return localArrayMap;
  }
  
  private ArrayMap<String, View> remapSharedElements(TransitionState paramTransitionState, Fragment paramFragment, boolean paramBoolean)
  {
    ArrayMap localArrayMap2 = new ArrayMap();
    ArrayMap localArrayMap1 = localArrayMap2;
    if (this.mSharedElementSourceNames != null)
    {
      paramFragment.getView().findNamedViews(localArrayMap2);
      if (!paramBoolean) {
        break label75;
      }
      localArrayMap2.retainAll(this.mSharedElementTargetNames);
    }
    label75:
    for (localArrayMap1 = localArrayMap2; paramBoolean; localArrayMap1 = remapNames(this.mSharedElementSourceNames, this.mSharedElementTargetNames, localArrayMap2))
    {
      paramFragment.mEnterTransitionCallback.onMapSharedElements(this.mSharedElementTargetNames, localArrayMap1);
      setBackNameOverrides(paramTransitionState, localArrayMap1, false);
      return localArrayMap1;
    }
    paramFragment.mExitTransitionCallback.onMapSharedElements(this.mSharedElementTargetNames, localArrayMap1);
    setNameOverrides(paramTransitionState, localArrayMap1, false);
    return localArrayMap1;
  }
  
  private void removeTargetedViewsFromTransitions(final ViewGroup paramViewGroup, final View paramView, final Transition paramTransition1, final ArrayList<View> paramArrayList1, final Transition paramTransition2, final ArrayList<View> paramArrayList2, final Transition paramTransition3, final ArrayList<View> paramArrayList3, final Transition paramTransition4, final ArrayList<View> paramArrayList4)
  {
    if (paramTransition4 != null) {
      paramViewGroup.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
      {
        public boolean onPreDraw()
        {
          paramViewGroup.getViewTreeObserver().removeOnPreDrawListener(this);
          if (paramTransition1 != null)
          {
            BackStackRecord.removeTargets(paramTransition1, paramArrayList1);
            BackStackRecord.-wrap3(paramTransition1, paramTransition2, paramArrayList2, false);
            BackStackRecord.-wrap3(paramTransition1, paramTransition3, paramArrayList3, false);
          }
          if (paramTransition2 != null)
          {
            BackStackRecord.removeTargets(paramTransition2, paramArrayList2);
            BackStackRecord.-wrap3(paramTransition2, paramTransition1, paramArrayList1, false);
            BackStackRecord.-wrap3(paramTransition2, paramTransition3, paramArrayList3, false);
          }
          if (paramTransition3 != null) {
            BackStackRecord.removeTargets(paramTransition3, paramArrayList3);
          }
          int j = paramArrayList4.size();
          int i = 0;
          while (i < j)
          {
            paramTransition4.excludeTarget((View)paramArrayList4.get(i), false);
            i += 1;
          }
          paramTransition4.excludeTarget(paramView, false);
          return true;
        }
      });
    }
  }
  
  public static void removeTargets(Transition paramTransition, ArrayList<View> paramArrayList)
  {
    int i;
    if ((paramTransition instanceof TransitionSet))
    {
      paramTransition = (TransitionSet)paramTransition;
      int j = paramTransition.getTransitionCount();
      i = 0;
      while (i < j)
      {
        removeTargets(paramTransition.getTransitionAt(i), paramArrayList);
        i += 1;
      }
    }
    if (!hasSimpleTarget(paramTransition))
    {
      List localList = paramTransition.getTargets();
      if ((localList != null) && (localList.size() == paramArrayList.size()) && (localList.containsAll(paramArrayList)))
      {
        i = paramArrayList.size() - 1;
        while (i >= 0)
        {
          paramTransition.removeTarget((View)paramArrayList.get(i));
          i -= 1;
        }
      }
    }
  }
  
  private void setBackNameOverrides(TransitionState paramTransitionState, ArrayMap<String, View> paramArrayMap, boolean paramBoolean)
  {
    int i;
    int j;
    label20:
    label32:
    String str;
    Object localObject;
    if (this.mSharedElementTargetNames == null)
    {
      i = 0;
      if (this.mSharedElementSourceNames != null) {
        break label122;
      }
      j = 0;
      j = Math.min(i, j);
      i = 0;
      if (i >= j) {
        return;
      }
      str = (String)this.mSharedElementSourceNames.get(i);
      localObject = (View)paramArrayMap.get((String)this.mSharedElementTargetNames.get(i));
      if (localObject != null)
      {
        localObject = ((View)localObject).getTransitionName();
        if (!paramBoolean) {
          break label134;
        }
        setNameOverride(paramTransitionState.nameOverrides, str, (String)localObject);
      }
    }
    for (;;)
    {
      i += 1;
      break label32;
      i = this.mSharedElementTargetNames.size();
      break;
      label122:
      j = this.mSharedElementSourceNames.size();
      break label20;
      label134:
      setNameOverride(paramTransitionState.nameOverrides, (String)localObject, str);
    }
  }
  
  private static void setEpicenter(Transition paramTransition, View paramView)
  {
    Rect localRect = new Rect();
    paramView.getBoundsOnScreen(localRect);
    paramTransition.setEpicenterCallback(new Transition.EpicenterCallback()
    {
      public Rect onGetEpicenter(Transition paramAnonymousTransition)
      {
        return this.val$epicenter;
      }
    });
  }
  
  private void setEpicenterIn(ArrayMap<String, View> paramArrayMap, TransitionState paramTransitionState)
  {
    if ((this.mSharedElementTargetNames == null) || (paramArrayMap.isEmpty())) {}
    do
    {
      return;
      paramArrayMap = (View)paramArrayMap.get(this.mSharedElementTargetNames.get(0));
    } while (paramArrayMap == null);
    paramTransitionState.enteringEpicenterView = paramArrayMap;
  }
  
  private static void setFirstOut(SparseArray<Fragment> paramSparseArray1, SparseArray<Fragment> paramSparseArray2, Fragment paramFragment)
  {
    int i;
    if (paramFragment != null)
    {
      i = paramFragment.mContainerId;
      if ((i != 0) && (!paramFragment.isHidden())) {
        break label21;
      }
    }
    label21:
    do
    {
      return;
      if ((paramFragment.isAdded()) && (paramFragment.getView() != null) && (paramSparseArray1.get(i) == null)) {
        paramSparseArray1.put(i, paramFragment);
      }
    } while (paramSparseArray2.get(i) != paramFragment);
    paramSparseArray2.remove(i);
  }
  
  private void setLastIn(SparseArray<Fragment> paramSparseArray1, SparseArray<Fragment> paramSparseArray2, Fragment paramFragment)
  {
    if (paramFragment != null)
    {
      int i = paramFragment.mContainerId;
      if (i != 0)
      {
        if (!paramFragment.isAdded()) {
          paramSparseArray2.put(i, paramFragment);
        }
        if (paramSparseArray1.get(i) == paramFragment) {
          paramSparseArray1.remove(i);
        }
      }
      if ((paramFragment.mState < 1) && (this.mManager.mCurState >= 1) && (this.mManager.mHost.getContext().getApplicationInfo().targetSdkVersion >= 24))
      {
        this.mManager.makeActive(paramFragment);
        this.mManager.moveToState(paramFragment, 1, 0, 0, false);
      }
    }
  }
  
  private static void setNameOverride(ArrayMap<String, String> paramArrayMap, String paramString1, String paramString2)
  {
    if ((paramString1 == null) || (paramString2 == null) || (paramString1.equals(paramString2))) {
      return;
    }
    int i = 0;
    while (i < paramArrayMap.size())
    {
      if (paramString1.equals(paramArrayMap.valueAt(i)))
      {
        paramArrayMap.setValueAt(i, paramString2);
        return;
      }
      i += 1;
    }
    paramArrayMap.put(paramString1, paramString2);
  }
  
  private void setNameOverrides(TransitionState paramTransitionState, ArrayMap<String, View> paramArrayMap, boolean paramBoolean)
  {
    int i;
    int j;
    label10:
    String str1;
    String str2;
    if (paramArrayMap == null)
    {
      i = 0;
      j = 0;
      if (j >= i) {
        return;
      }
      str1 = (String)paramArrayMap.keyAt(j);
      str2 = ((View)paramArrayMap.valueAt(j)).getTransitionName();
      if (!paramBoolean) {
        break label75;
      }
      setNameOverride(paramTransitionState.nameOverrides, str1, str2);
    }
    for (;;)
    {
      j += 1;
      break label10;
      i = paramArrayMap.size();
      break;
      label75:
      setNameOverride(paramTransitionState.nameOverrides, str2, str1);
    }
  }
  
  private static void setNameOverrides(TransitionState paramTransitionState, ArrayList<String> paramArrayList1, ArrayList<String> paramArrayList2)
  {
    if ((paramArrayList1 != null) && (paramArrayList2 != null))
    {
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
  
  private void setSharedElementEpicenter(Transition paramTransition, final TransitionState paramTransitionState)
  {
    paramTransition.setEpicenterCallback(new Transition.EpicenterCallback()
    {
      private Rect mEpicenter;
      
      public Rect onGetEpicenter(Transition paramAnonymousTransition)
      {
        if ((this.mEpicenter == null) && (paramTransitionState.enteringEpicenterView != null))
        {
          this.mEpicenter = new Rect();
          paramTransitionState.enteringEpicenterView.getBoundsOnScreen(this.mEpicenter);
        }
        return this.mEpicenter;
      }
    });
  }
  
  private static void setSharedElementTargets(TransitionSet paramTransitionSet, View paramView, ArrayMap<String, View> paramArrayMap, ArrayList<View> paramArrayList)
  {
    paramArrayList.clear();
    paramArrayList.addAll(paramArrayMap.values());
    paramArrayMap = paramTransitionSet.getTargets();
    paramArrayMap.clear();
    int j = paramArrayList.size();
    int i = 0;
    while (i < j)
    {
      bfsAddViewChildren(paramArrayMap, (View)paramArrayList.get(i));
      i += 1;
    }
    paramArrayList.add(paramView);
    addTargets(paramTransitionSet, paramArrayList);
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
    if (this.mHead == null)
    {
      this.mTail = paramOp;
      this.mHead = paramOp;
    }
    for (;;)
    {
      paramOp.enterAnim = this.mEnterAnim;
      paramOp.exitAnim = this.mExitAnim;
      paramOp.popEnterAnim = this.mPopEnterAnim;
      paramOp.popExitAnim = this.mPopExitAnim;
      this.mNumOp += 1;
      return;
      paramOp.prev = this.mTail;
      this.mTail.next = paramOp;
      this.mTail = paramOp;
    }
  }
  
  public FragmentTransaction addSharedElement(View paramView, String paramString)
  {
    paramView = paramView.getTransitionName();
    if (paramView == null) {
      throw new IllegalArgumentException("Unique transitionNames are required for all sharedElements");
    }
    if (this.mSharedElementSourceNames == null)
    {
      this.mSharedElementSourceNames = new ArrayList();
      this.mSharedElementTargetNames = new ArrayList();
    }
    this.mSharedElementSourceNames.add(paramView);
    this.mSharedElementTargetNames.add(paramString);
    return this;
  }
  
  public FragmentTransaction addToBackStack(String paramString)
  {
    if (!this.mAllowAddToBackStack) {
      throw new IllegalStateException("This FragmentTransaction is not allowed to be added to the back stack.");
    }
    this.mAddToBackStack = true;
    this.mName = paramString;
    return this;
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
    if (!this.mAddToBackStack) {
      return;
    }
    if (FragmentManagerImpl.DEBUG) {
      Log.v("FragmentManager", "Bump nesting in " + this + " by " + paramInt);
    }
    for (Op localOp = this.mHead; localOp != null; localOp = localOp.next)
    {
      Fragment localFragment;
      if (localOp.fragment != null)
      {
        localFragment = localOp.fragment;
        localFragment.mBackStackNesting += paramInt;
        if (FragmentManagerImpl.DEBUG) {
          Log.v("FragmentManager", "Bump nesting of " + localOp.fragment + " to " + localOp.fragment.mBackStackNesting);
        }
      }
      if (localOp.removed != null)
      {
        int i = localOp.removed.size() - 1;
        while (i >= 0)
        {
          localFragment = (Fragment)localOp.removed.get(i);
          localFragment.mBackStackNesting += paramInt;
          if (FragmentManagerImpl.DEBUG) {
            Log.v("FragmentManager", "Bump nesting of " + localFragment + " to " + localFragment.mBackStackNesting);
          }
          i -= 1;
        }
      }
    }
  }
  
  public void calculateBackFragments(SparseArray<Fragment> paramSparseArray1, SparseArray<Fragment> paramSparseArray2)
  {
    if (!this.mManager.mContainer.onHasView()) {
      return;
    }
    Op localOp = this.mTail;
    if (localOp != null)
    {
      switch (localOp.cmd)
      {
      }
      for (;;)
      {
        localOp = localOp.prev;
        break;
        setFirstOut(paramSparseArray1, paramSparseArray2, localOp.fragment);
        continue;
        if (localOp.removed != null)
        {
          int i = localOp.removed.size() - 1;
          while (i >= 0)
          {
            setLastIn(paramSparseArray1, paramSparseArray2, (Fragment)localOp.removed.get(i));
            i -= 1;
          }
        }
        setFirstOut(paramSparseArray1, paramSparseArray2, localOp.fragment);
        continue;
        setLastIn(paramSparseArray1, paramSparseArray2, localOp.fragment);
        continue;
        setLastIn(paramSparseArray1, paramSparseArray2, localOp.fragment);
        continue;
        setFirstOut(paramSparseArray1, paramSparseArray2, localOp.fragment);
        continue;
        setLastIn(paramSparseArray1, paramSparseArray2, localOp.fragment);
        continue;
        setFirstOut(paramSparseArray1, paramSparseArray2, localOp.fragment);
      }
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
    if (this.mCommitted) {
      throw new IllegalStateException("commit already called");
    }
    if (FragmentManagerImpl.DEBUG)
    {
      Log.v("FragmentManager", "Commit: " + this);
      FastPrintWriter localFastPrintWriter = new FastPrintWriter(new LogWriter(2, "FragmentManager"), false, 1024);
      dump("  ", null, localFastPrintWriter, null);
      localFastPrintWriter.flush();
    }
    this.mCommitted = true;
    if (this.mAddToBackStack) {}
    for (this.mIndex = this.mManager.allocBackStackIndex(this);; this.mIndex = -1)
    {
      this.mManager.enqueueAction(this, paramBoolean);
      return this.mIndex;
    }
  }
  
  public void commitNow()
  {
    disallowAddToBackStack();
    this.mManager.execSingleAction(this, false);
  }
  
  public void commitNowAllowingStateLoss()
  {
    disallowAddToBackStack();
    this.mManager.execSingleAction(this, true);
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
    if (this.mAddToBackStack) {
      throw new IllegalStateException("This transaction is already being added to the back stack");
    }
    this.mAllowAddToBackStack = false;
    return this;
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    dump(paramString, paramPrintWriter, true);
  }
  
  void dump(String paramString, PrintWriter paramPrintWriter, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mName=");
      paramPrintWriter.print(this.mName);
      paramPrintWriter.print(" mIndex=");
      paramPrintWriter.print(this.mIndex);
      paramPrintWriter.print(" mCommitted=");
      paramPrintWriter.println(this.mCommitted);
      if (this.mTransition != 0)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mTransition=#");
        paramPrintWriter.print(Integer.toHexString(this.mTransition));
        paramPrintWriter.print(" mTransitionStyle=#");
        paramPrintWriter.println(Integer.toHexString(this.mTransitionStyle));
      }
      if ((this.mEnterAnim != 0) || (this.mExitAnim != 0))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mEnterAnim=#");
        paramPrintWriter.print(Integer.toHexString(this.mEnterAnim));
        paramPrintWriter.print(" mExitAnim=#");
        paramPrintWriter.println(Integer.toHexString(this.mExitAnim));
      }
      if ((this.mPopEnterAnim != 0) || (this.mPopExitAnim != 0))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mPopEnterAnim=#");
        paramPrintWriter.print(Integer.toHexString(this.mPopEnterAnim));
        paramPrintWriter.print(" mPopExitAnim=#");
        paramPrintWriter.println(Integer.toHexString(this.mPopExitAnim));
      }
      if ((this.mBreadCrumbTitleRes != 0) || (this.mBreadCrumbTitleText != null))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mBreadCrumbTitleRes=#");
        paramPrintWriter.print(Integer.toHexString(this.mBreadCrumbTitleRes));
        paramPrintWriter.print(" mBreadCrumbTitleText=");
        paramPrintWriter.println(this.mBreadCrumbTitleText);
      }
      if ((this.mBreadCrumbShortTitleRes != 0) || (this.mBreadCrumbShortTitleText != null))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mBreadCrumbShortTitleRes=#");
        paramPrintWriter.print(Integer.toHexString(this.mBreadCrumbShortTitleRes));
        paramPrintWriter.print(" mBreadCrumbShortTitleText=");
        paramPrintWriter.println(this.mBreadCrumbShortTitleText);
      }
    }
    if (this.mHead != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("Operations:");
      String str2 = paramString + "    ";
      Op localOp = this.mHead;
      int i = 0;
      while (localOp != null)
      {
        String str1;
        int j;
        switch (localOp.cmd)
        {
        default: 
          str1 = "cmd=" + localOp.cmd;
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  Op #");
          paramPrintWriter.print(i);
          paramPrintWriter.print(": ");
          paramPrintWriter.print(str1);
          paramPrintWriter.print(" ");
          paramPrintWriter.println(localOp.fragment);
          if (paramBoolean)
          {
            if ((localOp.enterAnim != 0) || (localOp.exitAnim != 0))
            {
              paramPrintWriter.print(str2);
              paramPrintWriter.print("enterAnim=#");
              paramPrintWriter.print(Integer.toHexString(localOp.enterAnim));
              paramPrintWriter.print(" exitAnim=#");
              paramPrintWriter.println(Integer.toHexString(localOp.exitAnim));
            }
            if ((localOp.popEnterAnim != 0) || (localOp.popExitAnim != 0))
            {
              paramPrintWriter.print(str2);
              paramPrintWriter.print("popEnterAnim=#");
              paramPrintWriter.print(Integer.toHexString(localOp.popEnterAnim));
              paramPrintWriter.print(" popExitAnim=#");
              paramPrintWriter.println(Integer.toHexString(localOp.popExitAnim));
            }
          }
          if ((localOp.removed == null) || (localOp.removed.size() <= 0)) {
            break label809;
          }
          j = 0;
          label643:
          if (j >= localOp.removed.size()) {
            break label809;
          }
          paramPrintWriter.print(str2);
          if (localOp.removed.size() == 1) {
            paramPrintWriter.print("Removed: ");
          }
          break;
        }
        for (;;)
        {
          paramPrintWriter.println(localOp.removed.get(j));
          j += 1;
          break label643;
          str1 = "NULL";
          break;
          str1 = "ADD";
          break;
          str1 = "REPLACE";
          break;
          str1 = "REMOVE";
          break;
          str1 = "HIDE";
          break;
          str1 = "SHOW";
          break;
          str1 = "DETACH";
          break;
          str1 = "ATTACH";
          break;
          if (j == 0) {
            paramPrintWriter.println("Removed:");
          }
          paramPrintWriter.print(str2);
          paramPrintWriter.print("  #");
          paramPrintWriter.print(j);
          paramPrintWriter.print(": ");
        }
        label809:
        localOp = localOp.next;
        i += 1;
      }
    }
  }
  
  public CharSequence getBreadCrumbShortTitle()
  {
    if (this.mBreadCrumbShortTitleRes != 0) {
      return this.mManager.mHost.getContext().getText(this.mBreadCrumbShortTitleRes);
    }
    return this.mBreadCrumbShortTitleText;
  }
  
  public int getBreadCrumbShortTitleRes()
  {
    return this.mBreadCrumbShortTitleRes;
  }
  
  public CharSequence getBreadCrumbTitle()
  {
    if (this.mBreadCrumbTitleRes != 0) {
      return this.mManager.mHost.getContext().getText(this.mBreadCrumbTitleRes);
    }
    return this.mBreadCrumbTitleText;
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
    boolean bool = false;
    if (this.mNumOp == 0) {
      bool = true;
    }
    return bool;
  }
  
  public TransitionState popFromBackStack(boolean paramBoolean, TransitionState paramTransitionState, SparseArray<Fragment> paramSparseArray1, SparseArray<Fragment> paramSparseArray2)
  {
    if (FragmentManagerImpl.DEBUG)
    {
      Log.v("FragmentManager", "popFromBackStack: " + this);
      localObject = new FastPrintWriter(new LogWriter(2, "FragmentManager"), false, 1024);
      dump("  ", null, (PrintWriter)localObject, null);
      ((PrintWriter)localObject).flush();
    }
    Object localObject = paramTransitionState;
    if (this.mManager.mCurState >= 1)
    {
      if (paramTransitionState != null) {
        break label207;
      }
      if (paramSparseArray1.size() == 0)
      {
        localObject = paramTransitionState;
        if (paramSparseArray2.size() == 0) {}
      }
      else
      {
        localObject = beginTransition(paramSparseArray1, paramSparseArray2, true);
      }
    }
    for (;;)
    {
      bumpBackStackNesting(-1);
      paramTransitionState = this.mTail;
      if (paramTransitionState == null) {
        break;
      }
      switch (paramTransitionState.cmd)
      {
      default: 
        throw new IllegalArgumentException("Unknown cmd: " + paramTransitionState.cmd);
        label207:
        localObject = paramTransitionState;
        if (!paramBoolean)
        {
          setNameOverrides(paramTransitionState, this.mSharedElementTargetNames, this.mSharedElementSourceNames);
          localObject = paramTransitionState;
        }
        break;
      }
    }
    paramSparseArray1 = paramTransitionState.fragment;
    paramSparseArray1.mNextAnim = paramTransitionState.popExitAnim;
    this.mManager.removeFragment(paramSparseArray1, FragmentManagerImpl.reverseTransit(this.mTransition), this.mTransitionStyle);
    for (;;)
    {
      paramTransitionState = paramTransitionState.prev;
      break;
      paramSparseArray1 = paramTransitionState.fragment;
      if (paramSparseArray1 != null)
      {
        paramSparseArray1.mNextAnim = paramTransitionState.popExitAnim;
        this.mManager.removeFragment(paramSparseArray1, FragmentManagerImpl.reverseTransit(this.mTransition), this.mTransitionStyle);
      }
      if (paramTransitionState.removed != null)
      {
        int i = 0;
        while (i < paramTransitionState.removed.size())
        {
          paramSparseArray1 = (Fragment)paramTransitionState.removed.get(i);
          paramSparseArray1.mNextAnim = paramTransitionState.popEnterAnim;
          this.mManager.addFragment(paramSparseArray1, false);
          i += 1;
        }
        paramSparseArray1 = paramTransitionState.fragment;
        paramSparseArray1.mNextAnim = paramTransitionState.popEnterAnim;
        this.mManager.addFragment(paramSparseArray1, false);
        continue;
        paramSparseArray1 = paramTransitionState.fragment;
        paramSparseArray1.mNextAnim = paramTransitionState.popEnterAnim;
        this.mManager.showFragment(paramSparseArray1, FragmentManagerImpl.reverseTransit(this.mTransition), this.mTransitionStyle);
        continue;
        paramSparseArray1 = paramTransitionState.fragment;
        paramSparseArray1.mNextAnim = paramTransitionState.popExitAnim;
        this.mManager.hideFragment(paramSparseArray1, FragmentManagerImpl.reverseTransit(this.mTransition), this.mTransitionStyle);
        continue;
        paramSparseArray1 = paramTransitionState.fragment;
        paramSparseArray1.mNextAnim = paramTransitionState.popEnterAnim;
        this.mManager.attachFragment(paramSparseArray1, FragmentManagerImpl.reverseTransit(this.mTransition), this.mTransitionStyle);
        continue;
        paramSparseArray1 = paramTransitionState.fragment;
        paramSparseArray1.mNextAnim = paramTransitionState.popExitAnim;
        this.mManager.detachFragment(paramSparseArray1, FragmentManagerImpl.reverseTransit(this.mTransition), this.mTransitionStyle);
      }
    }
    if (paramBoolean)
    {
      this.mManager.moveToState(this.mManager.mCurState, FragmentManagerImpl.reverseTransit(this.mTransition), this.mTransitionStyle, true);
      localObject = null;
    }
    if (this.mIndex >= 0)
    {
      this.mManager.freeBackStackIndex(this.mIndex);
      this.mIndex = -1;
    }
    return (TransitionState)localObject;
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
    if (paramInt == 0) {
      throw new IllegalArgumentException("Must use non-zero containerViewId");
    }
    doAddOp(paramInt, paramFragment, paramString, 2);
    return this;
  }
  
  public void run()
  {
    if (FragmentManagerImpl.DEBUG) {
      Log.v("FragmentManager", "Run: " + this);
    }
    if ((this.mAddToBackStack) && (this.mIndex < 0)) {
      throw new IllegalStateException("addToBackStack() called after commit()");
    }
    bumpBackStackNesting(1);
    Object localObject1;
    if (this.mManager.mCurState >= 1)
    {
      localObject1 = new SparseArray();
      localObject2 = new SparseArray();
      calculateFragments((SparseArray)localObject1, (SparseArray)localObject2);
      beginTransition((SparseArray)localObject1, (SparseArray)localObject2, false);
    }
    Object localObject2 = this.mHead;
    if (localObject2 != null)
    {
      switch (((Op)localObject2).cmd)
      {
      default: 
        throw new IllegalArgumentException("Unknown cmd: " + ((Op)localObject2).cmd);
      case 1: 
        localObject1 = ((Op)localObject2).fragment;
        ((Fragment)localObject1).mNextAnim = ((Op)localObject2).enterAnim;
        this.mManager.addFragment((Fragment)localObject1, false);
      }
      for (;;)
      {
        localObject2 = ((Op)localObject2).next;
        break;
        localObject1 = ((Op)localObject2).fragment;
        int j = ((Fragment)localObject1).mContainerId;
        Object localObject3 = localObject1;
        if (this.mManager.mAdded != null)
        {
          int i = this.mManager.mAdded.size() - 1;
          localObject3 = localObject1;
          if (i >= 0)
          {
            Fragment localFragment = (Fragment)this.mManager.mAdded.get(i);
            if (FragmentManagerImpl.DEBUG) {
              Log.v("FragmentManager", "OP_REPLACE: adding=" + localObject1 + " old=" + localFragment);
            }
            localObject3 = localObject1;
            if (localFragment.mContainerId == j)
            {
              if (localFragment != localObject1) {
                break label370;
              }
              localObject3 = null;
              ((Op)localObject2).fragment = null;
            }
            for (;;)
            {
              i -= 1;
              localObject1 = localObject3;
              break;
              label370:
              if (((Op)localObject2).removed == null) {
                ((Op)localObject2).removed = new ArrayList();
              }
              ((Op)localObject2).removed.add(localFragment);
              localFragment.mNextAnim = ((Op)localObject2).exitAnim;
              if (this.mAddToBackStack)
              {
                localFragment.mBackStackNesting += 1;
                if (FragmentManagerImpl.DEBUG) {
                  Log.v("FragmentManager", "Bump nesting of " + localFragment + " to " + localFragment.mBackStackNesting);
                }
              }
              this.mManager.removeFragment(localFragment, this.mTransition, this.mTransitionStyle);
              localObject3 = localObject1;
            }
          }
        }
        if (localObject3 != null)
        {
          ((Fragment)localObject3).mNextAnim = ((Op)localObject2).enterAnim;
          this.mManager.addFragment((Fragment)localObject3, false);
          continue;
          localObject1 = ((Op)localObject2).fragment;
          ((Fragment)localObject1).mNextAnim = ((Op)localObject2).exitAnim;
          this.mManager.removeFragment((Fragment)localObject1, this.mTransition, this.mTransitionStyle);
          continue;
          localObject1 = ((Op)localObject2).fragment;
          ((Fragment)localObject1).mNextAnim = ((Op)localObject2).exitAnim;
          this.mManager.hideFragment((Fragment)localObject1, this.mTransition, this.mTransitionStyle);
          continue;
          localObject1 = ((Op)localObject2).fragment;
          ((Fragment)localObject1).mNextAnim = ((Op)localObject2).enterAnim;
          this.mManager.showFragment((Fragment)localObject1, this.mTransition, this.mTransitionStyle);
          continue;
          localObject1 = ((Op)localObject2).fragment;
          ((Fragment)localObject1).mNextAnim = ((Op)localObject2).exitAnim;
          this.mManager.detachFragment((Fragment)localObject1, this.mTransition, this.mTransitionStyle);
          continue;
          localObject1 = ((Op)localObject2).fragment;
          ((Fragment)localObject1).mNextAnim = ((Op)localObject2).enterAnim;
          this.mManager.attachFragment((Fragment)localObject1, this.mTransition, this.mTransitionStyle);
        }
      }
    }
    this.mManager.moveToState(this.mManager.mCurState, this.mTransition, this.mTransitionStyle, true);
    if (this.mAddToBackStack) {
      this.mManager.addBackStackState(this);
    }
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
    if (this.mIndex >= 0)
    {
      localStringBuilder.append(" #");
      localStringBuilder.append(this.mIndex);
    }
    if (this.mName != null)
    {
      localStringBuilder.append(" ");
      localStringBuilder.append(this.mName);
    }
    localStringBuilder.append("}");
    return localStringBuilder.toString();
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
    public View enteringEpicenterView;
    public ArrayMap<String, String> nameOverrides = new ArrayMap();
    public View nonExistentView;
    
    public TransitionState() {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/BackStackRecord.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */