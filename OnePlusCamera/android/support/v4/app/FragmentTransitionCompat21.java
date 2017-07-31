package android.support.v4.app;

import android.graphics.Rect;
import android.transition.Transition;
import android.transition.Transition.EpicenterCallback;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

class FragmentTransitionCompat21
{
  public static void addTargets(Object paramObject, ArrayList<View> paramArrayList)
  {
    int i = 0;
    paramObject = (Transition)paramObject;
    if (!(paramObject instanceof TransitionSet)) {
      if (!hasSimpleTarget((Transition)paramObject)) {
        break label53;
      }
    }
    for (;;)
    {
      return;
      paramObject = (TransitionSet)paramObject;
      int j = ((TransitionSet)paramObject).getTransitionCount();
      while (i < j)
      {
        addTargets(((TransitionSet)paramObject).getTransitionAt(i), paramArrayList);
        i += 1;
      }
      continue;
      label53:
      if (isNullOrEmpty(((Transition)paramObject).getTargets()))
      {
        j = paramArrayList.size();
        i = 0;
        while (i < j)
        {
          ((Transition)paramObject).addTarget((View)paramArrayList.get(i));
          i += 1;
        }
      }
    }
  }
  
  public static void addTransitionTargets(final Object paramObject1, Object paramObject2, View paramView1, final ViewRetriever paramViewRetriever, final View paramView2, EpicenterView paramEpicenterView, final Map<String, String> paramMap, final ArrayList<View> paramArrayList1, final Map<String, View> paramMap1, ArrayList<View> paramArrayList2)
  {
    if (paramObject1 != null)
    {
      paramObject1 = (Transition)paramObject1;
      if (paramObject1 != null) {
        break label33;
      }
      label13:
      if (paramObject2 != null) {
        break label43;
      }
      label17:
      if (paramViewRetriever != null) {
        break label55;
      }
    }
    for (;;)
    {
      setSharedElementEpicenter((Transition)paramObject1, paramEpicenterView);
      return;
      if (paramObject2 != null) {
        break;
      }
      return;
      label33:
      ((Transition)paramObject1).addTarget(paramView2);
      break label13;
      label43:
      addTargets((Transition)paramObject2, paramArrayList2);
      break label17;
      label55:
      paramView1.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
      {
        public boolean onPreDraw()
        {
          this.val$container.getViewTreeObserver().removeOnPreDrawListener(this);
          View localView = paramViewRetriever.getView();
          if (localView == null) {
            return true;
          }
          if (paramMap.isEmpty()) {}
          while (paramObject1 != null)
          {
            FragmentTransitionCompat21.captureTransitioningViews(paramArrayList1, localView);
            paramArrayList1.removeAll(paramMap1.values());
            paramArrayList1.add(paramView2);
            paramObject1.removeTarget(paramView2);
            FragmentTransitionCompat21.addTargets(paramObject1, paramArrayList1);
            break;
            FragmentTransitionCompat21.findNamedViews(paramMap1, localView);
            paramMap1.keySet().retainAll(paramMap.values());
            Iterator localIterator = paramMap.entrySet().iterator();
            while (localIterator.hasNext())
            {
              Map.Entry localEntry = (Map.Entry)localIterator.next();
              Object localObject = (String)localEntry.getValue();
              localObject = (View)paramMap1.get(localObject);
              if (localObject != null) {
                ((View)localObject).setTransitionName((String)localEntry.getKey());
              }
            }
          }
        }
      });
    }
  }
  
  public static void beginDelayedTransition(ViewGroup paramViewGroup, Object paramObject)
  {
    TransitionManager.beginDelayedTransition(paramViewGroup, (Transition)paramObject);
  }
  
  public static Object captureExitingViews(Object paramObject, View paramView1, ArrayList<View> paramArrayList, Map<String, View> paramMap, View paramView2)
  {
    if (paramObject == null) {
      return paramObject;
    }
    captureTransitioningViews(paramArrayList, paramView1);
    if (paramMap == null) {}
    while (!paramArrayList.isEmpty())
    {
      paramArrayList.add(paramView2);
      addTargets((Transition)paramObject, paramArrayList);
      return paramObject;
      paramArrayList.removeAll(paramMap.values());
    }
    return null;
  }
  
  private static void captureTransitioningViews(ArrayList<View> paramArrayList, View paramView)
  {
    int i = 0;
    if (paramView.getVisibility() != 0) {}
    for (;;)
    {
      return;
      if (!(paramView instanceof ViewGroup))
      {
        paramArrayList.add(paramView);
        return;
      }
      paramView = (ViewGroup)paramView;
      if (paramView.isTransitionGroup()) {
        break;
      }
      int j = paramView.getChildCount();
      while (i < j)
      {
        captureTransitioningViews(paramArrayList, paramView.getChildAt(i));
        i += 1;
      }
    }
    paramArrayList.add(paramView);
  }
  
  public static void cleanupTransitions(View paramView1, final View paramView2, final Object paramObject1, final ArrayList<View> paramArrayList1, final Object paramObject2, final ArrayList<View> paramArrayList2, final Object paramObject3, final ArrayList<View> paramArrayList3, final Object paramObject4, final ArrayList<View> paramArrayList4, final Map<String, View> paramMap)
  {
    paramObject1 = (Transition)paramObject1;
    paramObject2 = (Transition)paramObject2;
    paramObject3 = (Transition)paramObject3;
    paramObject4 = (Transition)paramObject4;
    if (paramObject4 == null) {
      return;
    }
    paramView1.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
    {
      public boolean onPreDraw()
      {
        this.val$sceneRoot.getViewTreeObserver().removeOnPreDrawListener(this);
        label25:
        label32:
        Iterator localIterator;
        label47:
        int j;
        int i;
        if (paramObject1 == null)
        {
          if (paramObject2 != null) {
            break label112;
          }
          if (paramObject3 != null) {
            break label126;
          }
          localIterator = paramMap.entrySet().iterator();
          if (localIterator.hasNext()) {
            break label140;
          }
          j = paramArrayList4.size();
          i = 0;
        }
        for (;;)
        {
          if (i >= j)
          {
            paramObject4.excludeTarget(paramView2, false);
            return true;
            paramObject1.removeTarget(paramView2);
            FragmentTransitionCompat21.removeTargets(paramObject1, paramArrayList1);
            break;
            label112:
            FragmentTransitionCompat21.removeTargets(paramObject2, paramArrayList2);
            break label25;
            label126:
            FragmentTransitionCompat21.removeTargets(paramObject3, paramArrayList3);
            break label32;
            label140:
            Map.Entry localEntry = (Map.Entry)localIterator.next();
            ((View)localEntry.getValue()).setTransitionName((String)localEntry.getKey());
            break label47;
          }
          paramObject4.excludeTarget((View)paramArrayList4.get(i), false);
          i += 1;
        }
      }
    });
  }
  
  public static Object cloneTransition(Object paramObject)
  {
    if (paramObject == null) {
      return paramObject;
    }
    return ((Transition)paramObject).clone();
  }
  
  public static void excludeTarget(Object paramObject, View paramView, boolean paramBoolean)
  {
    ((Transition)paramObject).excludeTarget(paramView, paramBoolean);
  }
  
  public static void findNamedViews(Map<String, View> paramMap, View paramView)
  {
    int i = 0;
    if (paramView.getVisibility() != 0) {}
    label70:
    for (;;)
    {
      return;
      String str = paramView.getTransitionName();
      if (str == null) {}
      for (;;)
      {
        if (!(paramView instanceof ViewGroup)) {
          break label70;
        }
        paramView = (ViewGroup)paramView;
        int j = paramView.getChildCount();
        while (i < j)
        {
          findNamedViews(paramMap, paramView.getChildAt(i));
          i += 1;
        }
        break;
        paramMap.put(str, paramView);
      }
    }
  }
  
  private static Rect getBoundsOnScreen(View paramView)
  {
    Rect localRect = new Rect();
    int[] arrayOfInt = new int[2];
    paramView.getLocationOnScreen(arrayOfInt);
    localRect.set(arrayOfInt[0], arrayOfInt[1], arrayOfInt[0] + paramView.getWidth(), arrayOfInt[1] + paramView.getHeight());
    return localRect;
  }
  
  public static String getTransitionName(View paramView)
  {
    return paramView.getTransitionName();
  }
  
  private static boolean hasSimpleTarget(Transition paramTransition)
  {
    if (!isNullOrEmpty(paramTransition.getTargetIds())) {}
    while ((!isNullOrEmpty(paramTransition.getTargetNames())) || (!isNullOrEmpty(paramTransition.getTargetTypes()))) {
      return true;
    }
    return false;
  }
  
  private static boolean isNullOrEmpty(List paramList)
  {
    if (paramList == null) {}
    while (paramList.isEmpty()) {
      return true;
    }
    return false;
  }
  
  public static Object mergeTransitions(Object paramObject1, Object paramObject2, Object paramObject3, boolean paramBoolean)
  {
    Object localObject = null;
    paramObject1 = (Transition)paramObject1;
    paramObject2 = (Transition)paramObject2;
    Transition localTransition = (Transition)paramObject3;
    if (paramObject1 == null)
    {
      paramBoolean = true;
      label25:
      if (paramBoolean) {
        break label58;
      }
      if (paramObject2 != null) {
        break label108;
      }
      label33:
      if (paramObject2 != null) {
        break label135;
      }
      if (paramObject1 != null) {
        break label140;
      }
      paramObject1 = localObject;
    }
    label58:
    label70:
    label99:
    label108:
    label135:
    label140:
    for (;;)
    {
      if (localTransition != null) {
        break label143;
      }
      return paramObject1;
      if (paramObject2 != null) {
        break label25;
      }
      break;
      paramObject3 = new TransitionSet();
      if (paramObject1 == null) {
        if (paramObject2 != null) {
          break label99;
        }
      }
      for (;;)
      {
        paramObject1 = paramObject3;
        if (localTransition == null) {
          break;
        }
        ((TransitionSet)paramObject3).addTransition(localTransition);
        return paramObject3;
        ((TransitionSet)paramObject3).addTransition((Transition)paramObject1);
        break label70;
        ((TransitionSet)paramObject3).addTransition((Transition)paramObject2);
      }
      if (paramObject1 == null) {
        break label33;
      }
      paramObject1 = new TransitionSet().addTransition((Transition)paramObject2).addTransition((Transition)paramObject1).setOrdering(1);
      continue;
      paramObject1 = paramObject2;
    }
    label143:
    paramObject2 = new TransitionSet();
    if (paramObject1 == null) {}
    for (;;)
    {
      ((TransitionSet)paramObject2).addTransition(localTransition);
      return paramObject2;
      ((TransitionSet)paramObject2).addTransition((Transition)paramObject1);
    }
  }
  
  public static void removeTargets(Object paramObject, ArrayList<View> paramArrayList)
  {
    int i = 0;
    paramObject = (Transition)paramObject;
    if (!(paramObject instanceof TransitionSet)) {
      if (!hasSimpleTarget((Transition)paramObject)) {
        break label53;
      }
    }
    for (;;)
    {
      return;
      paramObject = (TransitionSet)paramObject;
      int j = ((TransitionSet)paramObject).getTransitionCount();
      while (i < j)
      {
        removeTargets(((TransitionSet)paramObject).getTransitionAt(i), paramArrayList);
        i += 1;
      }
      continue;
      label53:
      List localList = ((Transition)paramObject).getTargets();
      if ((localList != null) && (localList.size() == paramArrayList.size()) && (localList.containsAll(paramArrayList)))
      {
        i = paramArrayList.size() - 1;
        while (i >= 0)
        {
          ((Transition)paramObject).removeTarget((View)paramArrayList.get(i));
          i -= 1;
        }
      }
    }
  }
  
  public static void setEpicenter(Object paramObject, View paramView)
  {
    ((Transition)paramObject).setEpicenterCallback(new Transition.EpicenterCallback()
    {
      public Rect onGetEpicenter(Transition paramAnonymousTransition)
      {
        return this.val$epicenter;
      }
    });
  }
  
  private static void setSharedElementEpicenter(Transition paramTransition, EpicenterView paramEpicenterView)
  {
    if (paramTransition == null) {
      return;
    }
    paramTransition.setEpicenterCallback(new Transition.EpicenterCallback()
    {
      private Rect mEpicenter;
      
      public Rect onGetEpicenter(Transition paramAnonymousTransition)
      {
        if (this.mEpicenter != null) {}
        for (;;)
        {
          return this.mEpicenter;
          if (this.val$epicenterView.epicenter != null) {
            this.mEpicenter = FragmentTransitionCompat21.getBoundsOnScreen(this.val$epicenterView.epicenter);
          }
        }
      }
    });
  }
  
  public static class EpicenterView
  {
    public View epicenter;
  }
  
  public static abstract interface ViewRetriever
  {
    public abstract View getView();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/app/FragmentTransitionCompat21.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */