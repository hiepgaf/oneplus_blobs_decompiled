package android.support.v4.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

abstract class MapCollections<K, V>
{
  MapCollections<K, V>.EntrySet mEntrySet;
  MapCollections<K, V>.KeySet mKeySet;
  MapCollections<K, V>.ValuesCollection mValues;
  
  public static <K, V> boolean containsAllHelper(Map<K, V> paramMap, Collection<?> paramCollection)
  {
    paramCollection = paramCollection.iterator();
    do
    {
      if (!paramCollection.hasNext()) {
        return true;
      }
    } while (paramMap.containsKey(paramCollection.next()));
    return false;
  }
  
  public static <T> boolean equalsSetHelper(Set<T> paramSet, Object paramObject)
  {
    boolean bool1 = true;
    if (paramSet != paramObject)
    {
      if (!(paramObject instanceof Set)) {
        return false;
      }
    }
    else {
      return true;
    }
    paramObject = (Set)paramObject;
    try
    {
      if (paramSet.size() == ((Set)paramObject).size())
      {
        boolean bool2 = paramSet.containsAll((Collection)paramObject);
        if (bool2) {
          return bool1;
        }
      }
    }
    catch (NullPointerException paramSet)
    {
      return false;
    }
    catch (ClassCastException paramSet)
    {
      return false;
    }
    bool1 = false;
    return bool1;
  }
  
  public static <K, V> boolean removeAllHelper(Map<K, V> paramMap, Collection<?> paramCollection)
  {
    int i = paramMap.size();
    paramCollection = paramCollection.iterator();
    for (;;)
    {
      if (!paramCollection.hasNext())
      {
        if (i != paramMap.size()) {
          break;
        }
        return false;
      }
      paramMap.remove(paramCollection.next());
    }
    return true;
  }
  
  public static <K, V> boolean retainAllHelper(Map<K, V> paramMap, Collection<?> paramCollection)
  {
    int i = paramMap.size();
    Iterator localIterator = paramMap.keySet().iterator();
    for (;;)
    {
      if (!localIterator.hasNext())
      {
        if (i != paramMap.size()) {
          break;
        }
        return false;
      }
      if (!paramCollection.contains(localIterator.next())) {
        localIterator.remove();
      }
    }
    return true;
  }
  
  protected abstract void colClear();
  
  protected abstract Object colGetEntry(int paramInt1, int paramInt2);
  
  protected abstract Map<K, V> colGetMap();
  
  protected abstract int colGetSize();
  
  protected abstract int colIndexOfKey(Object paramObject);
  
  protected abstract int colIndexOfValue(Object paramObject);
  
  protected abstract void colPut(K paramK, V paramV);
  
  protected abstract void colRemoveAt(int paramInt);
  
  protected abstract V colSetValue(int paramInt, V paramV);
  
  public Set<Map.Entry<K, V>> getEntrySet()
  {
    if (this.mEntrySet != null) {}
    for (;;)
    {
      return this.mEntrySet;
      this.mEntrySet = new EntrySet();
    }
  }
  
  public Set<K> getKeySet()
  {
    if (this.mKeySet != null) {}
    for (;;)
    {
      return this.mKeySet;
      this.mKeySet = new KeySet();
    }
  }
  
  public Collection<V> getValues()
  {
    if (this.mValues != null) {}
    for (;;)
    {
      return this.mValues;
      this.mValues = new ValuesCollection();
    }
  }
  
  public Object[] toArrayHelper(int paramInt)
  {
    int j = colGetSize();
    Object[] arrayOfObject = new Object[j];
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        return arrayOfObject;
      }
      arrayOfObject[i] = colGetEntry(i, paramInt);
      i += 1;
    }
  }
  
  public <T> T[] toArrayHelper(T[] paramArrayOfT, int paramInt)
  {
    int j = colGetSize();
    int i;
    if (paramArrayOfT.length >= j) {
      i = 0;
    }
    for (;;)
    {
      if (i >= j)
      {
        if (paramArrayOfT.length > j) {
          break label68;
        }
        return paramArrayOfT;
        paramArrayOfT = (Object[])Array.newInstance(paramArrayOfT.getClass().getComponentType(), j);
        break;
      }
      paramArrayOfT[i] = colGetEntry(i, paramInt);
      i += 1;
    }
    label68:
    paramArrayOfT[j] = null;
    return paramArrayOfT;
  }
  
  final class ArrayIterator<T>
    implements Iterator<T>
  {
    boolean mCanRemove = false;
    int mIndex;
    final int mOffset;
    int mSize;
    
    ArrayIterator(int paramInt)
    {
      this.mOffset = paramInt;
      this.mSize = MapCollections.this.colGetSize();
    }
    
    public boolean hasNext()
    {
      return this.mIndex < this.mSize;
    }
    
    public T next()
    {
      Object localObject = MapCollections.this.colGetEntry(this.mIndex, this.mOffset);
      this.mIndex += 1;
      this.mCanRemove = true;
      return (T)localObject;
    }
    
    public void remove()
    {
      if (this.mCanRemove)
      {
        this.mIndex -= 1;
        this.mSize -= 1;
        this.mCanRemove = false;
        MapCollections.this.colRemoveAt(this.mIndex);
        return;
      }
      throw new IllegalStateException();
    }
  }
  
  final class EntrySet
    implements Set<Map.Entry<K, V>>
  {
    EntrySet() {}
    
    public boolean add(Map.Entry<K, V> paramEntry)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(Collection<? extends Map.Entry<K, V>> paramCollection)
    {
      int i = MapCollections.this.colGetSize();
      paramCollection = paramCollection.iterator();
      for (;;)
      {
        if (!paramCollection.hasNext())
        {
          if (i != MapCollections.this.colGetSize()) {
            break;
          }
          return false;
        }
        Map.Entry localEntry = (Map.Entry)paramCollection.next();
        MapCollections.this.colPut(localEntry.getKey(), localEntry.getValue());
      }
      return true;
    }
    
    public void clear()
    {
      MapCollections.this.colClear();
    }
    
    public boolean contains(Object paramObject)
    {
      if ((paramObject instanceof Map.Entry))
      {
        paramObject = (Map.Entry)paramObject;
        int i = MapCollections.this.colIndexOfKey(((Map.Entry)paramObject).getKey());
        if (i >= 0) {
          return ContainerHelpers.equal(MapCollections.this.colGetEntry(i, 1), ((Map.Entry)paramObject).getValue());
        }
      }
      else
      {
        return false;
      }
      return false;
    }
    
    public boolean containsAll(Collection<?> paramCollection)
    {
      paramCollection = paramCollection.iterator();
      do
      {
        if (!paramCollection.hasNext()) {
          return true;
        }
      } while (contains(paramCollection.next()));
      return false;
    }
    
    public boolean equals(Object paramObject)
    {
      return MapCollections.equalsSetHelper(this, paramObject);
    }
    
    public int hashCode()
    {
      int j = MapCollections.this.colGetSize() - 1;
      int i = 0;
      if (j < 0) {
        return i;
      }
      Object localObject1 = MapCollections.this.colGetEntry(j, 0);
      Object localObject2 = MapCollections.this.colGetEntry(j, 1);
      int k;
      if (localObject1 != null)
      {
        k = localObject1.hashCode();
        label51:
        if (localObject2 == null) {
          break label82;
        }
      }
      label82:
      for (int m = localObject2.hashCode();; m = 0)
      {
        j -= 1;
        i += (m ^ k);
        break;
        k = 0;
        break label51;
      }
    }
    
    public boolean isEmpty()
    {
      return MapCollections.this.colGetSize() == 0;
    }
    
    public Iterator<Map.Entry<K, V>> iterator()
    {
      return new MapCollections.MapIterator(MapCollections.this);
    }
    
    public boolean remove(Object paramObject)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean removeAll(Collection<?> paramCollection)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean retainAll(Collection<?> paramCollection)
    {
      throw new UnsupportedOperationException();
    }
    
    public int size()
    {
      return MapCollections.this.colGetSize();
    }
    
    public Object[] toArray()
    {
      throw new UnsupportedOperationException();
    }
    
    public <T> T[] toArray(T[] paramArrayOfT)
    {
      throw new UnsupportedOperationException();
    }
  }
  
  final class KeySet
    implements Set<K>
  {
    KeySet() {}
    
    public boolean add(K paramK)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(Collection<? extends K> paramCollection)
    {
      throw new UnsupportedOperationException();
    }
    
    public void clear()
    {
      MapCollections.this.colClear();
    }
    
    public boolean contains(Object paramObject)
    {
      return MapCollections.this.colIndexOfKey(paramObject) >= 0;
    }
    
    public boolean containsAll(Collection<?> paramCollection)
    {
      return MapCollections.containsAllHelper(MapCollections.this.colGetMap(), paramCollection);
    }
    
    public boolean equals(Object paramObject)
    {
      return MapCollections.equalsSetHelper(this, paramObject);
    }
    
    public int hashCode()
    {
      int i = MapCollections.this.colGetSize() - 1;
      int j = 0;
      if (i < 0) {
        return j;
      }
      Object localObject = MapCollections.this.colGetEntry(i, 0);
      if (localObject != null) {}
      for (int k = localObject.hashCode();; k = 0)
      {
        j += k;
        i -= 1;
        break;
      }
    }
    
    public boolean isEmpty()
    {
      return MapCollections.this.colGetSize() == 0;
    }
    
    public Iterator<K> iterator()
    {
      return new MapCollections.ArrayIterator(MapCollections.this, 0);
    }
    
    public boolean remove(Object paramObject)
    {
      int i = MapCollections.this.colIndexOfKey(paramObject);
      if (i < 0) {
        return false;
      }
      MapCollections.this.colRemoveAt(i);
      return true;
    }
    
    public boolean removeAll(Collection<?> paramCollection)
    {
      return MapCollections.removeAllHelper(MapCollections.this.colGetMap(), paramCollection);
    }
    
    public boolean retainAll(Collection<?> paramCollection)
    {
      return MapCollections.retainAllHelper(MapCollections.this.colGetMap(), paramCollection);
    }
    
    public int size()
    {
      return MapCollections.this.colGetSize();
    }
    
    public Object[] toArray()
    {
      return MapCollections.this.toArrayHelper(0);
    }
    
    public <T> T[] toArray(T[] paramArrayOfT)
    {
      return MapCollections.this.toArrayHelper(paramArrayOfT, 0);
    }
  }
  
  final class MapIterator
    implements Iterator<Map.Entry<K, V>>, Map.Entry<K, V>
  {
    int mEnd = MapCollections.this.colGetSize() - 1;
    boolean mEntryValid = false;
    int mIndex = -1;
    
    MapIterator() {}
    
    public final boolean equals(Object paramObject)
    {
      boolean bool = true;
      if (this.mEntryValid)
      {
        if (!(paramObject instanceof Map.Entry)) {
          break label59;
        }
        paramObject = (Map.Entry)paramObject;
        if (ContainerHelpers.equal(((Map.Entry)paramObject).getKey(), MapCollections.this.colGetEntry(this.mIndex, 0))) {
          break label61;
        }
      }
      for (;;)
      {
        bool = false;
        label59:
        label61:
        do
        {
          return bool;
          throw new IllegalStateException("This container does not support retaining Map.Entry objects");
          return false;
        } while (ContainerHelpers.equal(((Map.Entry)paramObject).getValue(), MapCollections.this.colGetEntry(this.mIndex, 1)));
      }
    }
    
    public K getKey()
    {
      if (this.mEntryValid) {
        return (K)MapCollections.this.colGetEntry(this.mIndex, 0);
      }
      throw new IllegalStateException("This container does not support retaining Map.Entry objects");
    }
    
    public V getValue()
    {
      if (this.mEntryValid) {
        return (V)MapCollections.this.colGetEntry(this.mIndex, 1);
      }
      throw new IllegalStateException("This container does not support retaining Map.Entry objects");
    }
    
    public boolean hasNext()
    {
      return this.mIndex < this.mEnd;
    }
    
    public final int hashCode()
    {
      int j = 0;
      Object localObject1;
      Object localObject2;
      if (this.mEntryValid)
      {
        localObject1 = MapCollections.this.colGetEntry(this.mIndex, 0);
        localObject2 = MapCollections.this.colGetEntry(this.mIndex, 1);
        if (localObject1 == null) {
          break label70;
        }
      }
      label70:
      for (int i = localObject1.hashCode();; i = 0)
      {
        if (localObject2 != null) {
          j = localObject2.hashCode();
        }
        return j ^ i;
        throw new IllegalStateException("This container does not support retaining Map.Entry objects");
      }
    }
    
    public Map.Entry<K, V> next()
    {
      this.mIndex += 1;
      this.mEntryValid = true;
      return this;
    }
    
    public void remove()
    {
      if (this.mEntryValid)
      {
        MapCollections.this.colRemoveAt(this.mIndex);
        this.mIndex -= 1;
        this.mEnd -= 1;
        this.mEntryValid = false;
        return;
      }
      throw new IllegalStateException();
    }
    
    public V setValue(V paramV)
    {
      if (this.mEntryValid) {
        return (V)MapCollections.this.colSetValue(this.mIndex, paramV);
      }
      throw new IllegalStateException("This container does not support retaining Map.Entry objects");
    }
    
    public final String toString()
    {
      return getKey() + "=" + getValue();
    }
  }
  
  final class ValuesCollection
    implements Collection<V>
  {
    ValuesCollection() {}
    
    public boolean add(V paramV)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(Collection<? extends V> paramCollection)
    {
      throw new UnsupportedOperationException();
    }
    
    public void clear()
    {
      MapCollections.this.colClear();
    }
    
    public boolean contains(Object paramObject)
    {
      return MapCollections.this.colIndexOfValue(paramObject) >= 0;
    }
    
    public boolean containsAll(Collection<?> paramCollection)
    {
      paramCollection = paramCollection.iterator();
      do
      {
        if (!paramCollection.hasNext()) {
          return true;
        }
      } while (contains(paramCollection.next()));
      return false;
    }
    
    public boolean isEmpty()
    {
      return MapCollections.this.colGetSize() == 0;
    }
    
    public Iterator<V> iterator()
    {
      return new MapCollections.ArrayIterator(MapCollections.this, 1);
    }
    
    public boolean remove(Object paramObject)
    {
      int i = MapCollections.this.colIndexOfValue(paramObject);
      if (i < 0) {
        return false;
      }
      MapCollections.this.colRemoveAt(i);
      return true;
    }
    
    public boolean removeAll(Collection<?> paramCollection)
    {
      int i = 0;
      int j = MapCollections.this.colGetSize();
      boolean bool = false;
      if (i >= j) {
        return bool;
      }
      if (!paramCollection.contains(MapCollections.this.colGetEntry(i, 1))) {}
      for (;;)
      {
        i += 1;
        break;
        MapCollections.this.colRemoveAt(i);
        i -= 1;
        j -= 1;
        bool = true;
      }
    }
    
    public boolean retainAll(Collection<?> paramCollection)
    {
      int i = 0;
      int j = MapCollections.this.colGetSize();
      boolean bool = false;
      if (i >= j) {
        return bool;
      }
      if (paramCollection.contains(MapCollections.this.colGetEntry(i, 1))) {}
      for (;;)
      {
        i += 1;
        break;
        MapCollections.this.colRemoveAt(i);
        i -= 1;
        j -= 1;
        bool = true;
      }
    }
    
    public int size()
    {
      return MapCollections.this.colGetSize();
    }
    
    public Object[] toArray()
    {
      return MapCollections.this.toArrayHelper(1);
    }
    
    public <T> T[] toArray(T[] paramArrayOfT)
    {
      return MapCollections.this.toArrayHelper(paramArrayOfT, 1);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/util/MapCollections.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */