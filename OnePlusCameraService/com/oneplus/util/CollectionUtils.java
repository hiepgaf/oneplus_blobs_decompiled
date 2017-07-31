package com.oneplus.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CollectionUtils
{
  public static <T> T[] toArray(Iterable<T> paramIterable, Class<T> paramClass)
  {
    if ((paramIterable instanceof Collection))
    {
      paramIterable = (Collection)paramIterable;
      return paramIterable.toArray((Object[])Array.newInstance(paramClass, paramIterable.size()));
    }
    ArrayList localArrayList = new ArrayList();
    paramIterable = paramIterable.iterator();
    while (paramIterable.hasNext()) {
      localArrayList.add(paramIterable.next());
    }
    return localArrayList.toArray((Object[])Array.newInstance(paramClass, localArrayList.size()));
  }
  
  public static long[] toLongArray(Collection<Long> paramCollection)
  {
    long[] arrayOfLong = new long[paramCollection.size()];
    int i = 0;
    paramCollection = paramCollection.iterator();
    while (paramCollection.hasNext())
    {
      arrayOfLong[i] = ((Long)paramCollection.next()).longValue();
      i += 1;
    }
    return arrayOfLong;
  }
  
  public static long[] toLongArray(Long[] paramArrayOfLong)
  {
    long[] arrayOfLong = new long[paramArrayOfLong.length];
    int j = 0;
    int i = 0;
    int k = paramArrayOfLong.length;
    while (i < k)
    {
      arrayOfLong[j] = paramArrayOfLong[i].longValue();
      j += 1;
      i += 1;
    }
    return arrayOfLong;
  }
  
  public static Set<Long> toLongSet(long[] paramArrayOfLong)
  {
    HashSet localHashSet = new HashSet();
    int i = paramArrayOfLong.length - 1;
    while (i >= 0)
    {
      localHashSet.add(Long.valueOf(paramArrayOfLong[i]));
      i -= 1;
    }
    return localHashSet;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/util/CollectionUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */