package com.amap.api.mapcore2d;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

class bk<T>
  implements List<T>
{
  private LinkedList<T> a = new LinkedList();
  
  public void a(T paramT)
  {
    try
    {
      add(paramT);
      return;
    }
    finally
    {
      paramT = finally;
      throw paramT;
    }
  }
  
  public void add(int paramInt, T paramT)
  {
    try
    {
      this.a.add(paramInt, paramT);
      return;
    }
    finally
    {
      paramT = finally;
      throw paramT;
    }
  }
  
  public boolean add(T paramT)
  {
    try
    {
      boolean bool = this.a.add(paramT);
      return bool;
    }
    finally
    {
      paramT = finally;
      throw paramT;
    }
  }
  
  public boolean addAll(int paramInt, Collection<? extends T> paramCollection)
  {
    try
    {
      boolean bool = this.a.addAll(paramInt, paramCollection);
      return bool;
    }
    finally
    {
      paramCollection = finally;
      throw paramCollection;
    }
  }
  
  public boolean addAll(Collection<? extends T> paramCollection)
  {
    try
    {
      boolean bool = this.a.addAll(paramCollection);
      return bool;
    }
    finally
    {
      paramCollection = finally;
      throw paramCollection;
    }
  }
  
  public void clear()
  {
    try
    {
      this.a.clear();
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public boolean contains(Object paramObject)
  {
    try
    {
      boolean bool = this.a.contains(paramObject);
      return bool;
    }
    finally
    {
      paramObject = finally;
      throw ((Throwable)paramObject);
    }
  }
  
  public boolean containsAll(Collection<?> paramCollection)
  {
    try
    {
      boolean bool = this.a.containsAll(paramCollection);
      return bool;
    }
    finally
    {
      paramCollection = finally;
      throw paramCollection;
    }
  }
  
  public T get(int paramInt)
  {
    try
    {
      Object localObject1 = this.a.get(paramInt);
      return (T)localObject1;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        cj.a(localException, "SyncList", "get");
        Object localObject2 = null;
      }
    }
    finally {}
  }
  
  public int indexOf(Object paramObject)
  {
    try
    {
      int i = this.a.indexOf(paramObject);
      return i;
    }
    finally
    {
      paramObject = finally;
      throw ((Throwable)paramObject);
    }
  }
  
  public boolean isEmpty()
  {
    try
    {
      boolean bool = this.a.isEmpty();
      return bool;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public Iterator<T> iterator()
  {
    try
    {
      ListIterator localListIterator = this.a.listIterator();
      return localListIterator;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public int lastIndexOf(Object paramObject)
  {
    try
    {
      int i = this.a.lastIndexOf(paramObject);
      return i;
    }
    finally
    {
      paramObject = finally;
      throw ((Throwable)paramObject);
    }
  }
  
  public ListIterator<T> listIterator()
  {
    try
    {
      ListIterator localListIterator = this.a.listIterator();
      return localListIterator;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public ListIterator<T> listIterator(int paramInt)
  {
    try
    {
      ListIterator localListIterator = this.a.listIterator(paramInt);
      return localListIterator;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public T remove(int paramInt)
  {
    try
    {
      Object localObject1 = this.a.remove(paramInt);
      return (T)localObject1;
    }
    finally
    {
      localObject2 = finally;
      throw ((Throwable)localObject2);
    }
  }
  
  public boolean remove(Object paramObject)
  {
    try
    {
      boolean bool = this.a.remove(paramObject);
      return bool;
    }
    finally
    {
      paramObject = finally;
      throw ((Throwable)paramObject);
    }
  }
  
  public boolean removeAll(Collection<?> paramCollection)
  {
    try
    {
      boolean bool = this.a.removeAll(paramCollection);
      return bool;
    }
    finally
    {
      paramCollection = finally;
      throw paramCollection;
    }
  }
  
  public boolean retainAll(Collection<?> paramCollection)
  {
    try
    {
      boolean bool = this.a.retainAll(paramCollection);
      return bool;
    }
    finally
    {
      paramCollection = finally;
      throw paramCollection;
    }
  }
  
  public T set(int paramInt, T paramT)
  {
    try
    {
      paramT = this.a.set(paramInt, paramT);
      return paramT;
    }
    finally
    {
      paramT = finally;
      throw paramT;
    }
  }
  
  public int size()
  {
    try
    {
      int i = this.a.size();
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public List<T> subList(int paramInt1, int paramInt2)
  {
    try
    {
      List localList = this.a.subList(paramInt1, paramInt2);
      return localList;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public Object[] toArray()
  {
    try
    {
      Object[] arrayOfObject = this.a.toArray();
      return arrayOfObject;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public <V> V[] toArray(V[] paramArrayOfV)
  {
    try
    {
      paramArrayOfV = this.a.toArray(paramArrayOfV);
      return paramArrayOfV;
    }
    finally
    {
      paramArrayOfV = finally;
      throw paramArrayOfV;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/bk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */