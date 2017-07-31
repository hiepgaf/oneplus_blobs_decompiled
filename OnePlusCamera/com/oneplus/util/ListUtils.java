package com.oneplus.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public final class ListUtils
{
  public static List<Integer> asList(int... paramVarArgs)
  {
    int j = paramVarArgs.length;
    ArrayList localArrayList = new ArrayList(j);
    int i = 0;
    while (i < j)
    {
      localArrayList.add(Integer.valueOf(paramVarArgs[i]));
      i += 1;
    }
    return Collections.unmodifiableList(localArrayList);
  }
  
  public static <T extends Comparable<T>> T findNearestObject(List<T> paramList, T paramT, SearchMode paramSearchMode)
  {
    if (paramList == null) {
      return null;
    }
    Object localObject2 = null;
    int i = paramList.size() - 1;
    if (i >= 0)
    {
      Comparable localComparable = (Comparable)paramList.get(i);
      int j = localComparable.compareTo(paramT);
      if (j == 0) {
        return localComparable;
      }
      Object localObject1;
      if (j < 0) {
        if (paramSearchMode == SearchMode.NEAREST_ABOVE_OR_EQUALS) {
          localObject1 = localObject2;
        }
      }
      for (;;)
      {
        i -= 1;
        localObject2 = localObject1;
        break;
        if (localObject2 != null)
        {
          localObject1 = localObject2;
          if (localComparable.compareTo(localObject2) <= 0) {}
        }
        else
        {
          localObject1 = localComparable;
          continue;
          localObject1 = localObject2;
          if (paramSearchMode != SearchMode.NEAREST_BELOW_OR_EQUALS) {
            if (localObject2 != null)
            {
              localObject1 = localObject2;
              if (localComparable.compareTo(localObject2) >= 0) {}
            }
            else
            {
              localObject1 = localComparable;
            }
          }
        }
      }
    }
    return (T)localObject2;
  }
  
  public static <T> T findNearestObject(List<T> paramList, T paramT, Comparator<T> paramComparator, SearchMode paramSearchMode)
  {
    if (paramList == null) {
      return null;
    }
    Object localObject2 = null;
    int i = paramList.size() - 1;
    if (i >= 0)
    {
      Object localObject3 = paramList.get(i);
      int j = paramComparator.compare(localObject3, paramT);
      if (j == 0) {
        return (T)localObject3;
      }
      Object localObject1;
      if (j < 0) {
        if (paramSearchMode == SearchMode.NEAREST_ABOVE_OR_EQUALS) {
          localObject1 = localObject2;
        }
      }
      for (;;)
      {
        i -= 1;
        localObject2 = localObject1;
        break;
        if (localObject2 != null)
        {
          localObject1 = localObject2;
          if (paramComparator.compare(localObject3, localObject2) <= 0) {}
        }
        else
        {
          localObject1 = localObject3;
          continue;
          localObject1 = localObject2;
          if (paramSearchMode != SearchMode.NEAREST_BELOW_OR_EQUALS) {
            if (localObject2 != null)
            {
              localObject1 = localObject2;
              if (paramComparator.compare(localObject3, localObject2) >= 0) {}
            }
            else
            {
              localObject1 = localObject3;
            }
          }
        }
      }
    }
    return (T)localObject2;
  }
  
  public static <T> boolean isLastObject(LinkedList<T> paramLinkedList, T paramT)
  {
    boolean bool = false;
    if (paramLinkedList == null) {
      return false;
    }
    if (paramLinkedList.isEmpty()) {
      return false;
    }
    paramLinkedList = paramLinkedList.getLast();
    if (paramLinkedList != null) {
      return paramLinkedList.equals(paramT);
    }
    if (paramT == null) {
      bool = true;
    }
    return bool;
  }
  
  public static <T> boolean isLastObject(List<T> paramList, T paramT)
  {
    boolean bool = false;
    if (paramList == null) {
      return false;
    }
    int i = paramList.size();
    if (i <= 0) {
      return false;
    }
    paramList = paramList.get(i - 1);
    if (paramList != null) {
      return paramList.equals(paramT);
    }
    if (paramT == null) {
      bool = true;
    }
    return bool;
  }
  
  public static int sumOfIntList(List<Integer> paramList)
  {
    if (paramList == null) {
      return 0;
    }
    int j = 0;
    int i = 0;
    while (i < paramList.size())
    {
      j += ((Integer)paramList.get(i)).intValue();
      i += 1;
    }
    return j;
  }
  
  public static Set<String> toStringSet(List<Integer> paramList)
  {
    if (paramList == null) {
      return null;
    }
    HashSet localHashSet = new HashSet();
    int i = 0;
    while (i < paramList.size())
    {
      localHashSet.add(String.valueOf(paramList.get(i)));
      i += 1;
    }
    return localHashSet;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/util/ListUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */