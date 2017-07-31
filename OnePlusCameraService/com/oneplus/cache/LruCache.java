package com.oneplus.cache;

import com.oneplus.base.SimpleRef;
import java.util.HashMap;
import java.util.Iterator;

public abstract class LruCache<TKey, TValue>
  implements Cache<TKey, TValue>
{
  private static final int MAX_FREE_ENTRIES = 128;
  protected final String TAG = getClass().getSimpleName();
  private volatile long m_Capacity;
  private volatile LruCache<TKey, TValue>.CacheEntry m_EntryHead;
  private final HashMap<TKey, LruCache<TKey, TValue>.CacheEntry> m_EntryTable = new HashMap();
  private volatile LruCache<TKey, TValue>.CacheEntry m_EntryTail;
  private volatile LruCache<TKey, TValue>.CacheEntry m_FreeEntries;
  private volatile int m_FreeEntryCount;
  private volatile boolean m_IsClosed;
  private volatile long m_TotalValueSize;
  protected final Object syncLock = new Object();
  
  protected LruCache(long paramLong)
  {
    this.m_Capacity = paramLong;
  }
  
  private void addEntryBefore(LruCache<TKey, TValue>.CacheEntry paramLruCache1, LruCache<TKey, TValue>.CacheEntry paramLruCache2)
  {
    if (paramLruCache1 != null)
    {
      if (paramLruCache1.previous != null) {
        paramLruCache1.previous.next = paramLruCache2;
      }
      paramLruCache2.previous = paramLruCache1.previous;
      paramLruCache1.previous = paramLruCache2;
    }
    paramLruCache2.next = paramLruCache1;
  }
  
  private boolean checkCapacity(long paramLong)
  {
    return checkCapacity(this.m_Capacity, paramLong);
  }
  
  private boolean checkCapacity(long paramLong1, long paramLong2)
  {
    paramLong2 = this.m_TotalValueSize + paramLong2;
    if (paramLong2 <= paramLong1) {
      return true;
    }
    Object localObject1 = this.m_EntryTail;
    while ((paramLong2 > paramLong1) && (localObject1 != null))
    {
      CacheEntry localCacheEntry = ((CacheEntry)localObject1).previous;
      try
      {
        long l = paramLong2 - ((CacheEntry)localObject1).valueSize;
        paramLong2 = l;
        if (!remove((CacheEntry)localObject1, true))
        {
          paramLong2 = ((CacheEntry)localObject1).valueSize;
          paramLong2 = l + paramLong2;
        }
        localObject1 = localCacheEntry;
      }
      finally {}
    }
    return paramLong2 <= paramLong1;
  }
  
  private LruCache<TKey, TValue>.CacheEntry obtainEntry(TKey paramTKey)
  {
    CacheEntry localCacheEntry = this.m_FreeEntries;
    if (localCacheEntry != null)
    {
      this.m_FreeEntries = localCacheEntry.next;
      removeEntry(localCacheEntry);
    }
    for (;;)
    {
      localCacheEntry.key = paramTKey;
      localCacheEntry.previous = null;
      localCacheEntry.next = null;
      return localCacheEntry;
      localCacheEntry = new CacheEntry(null);
    }
  }
  
  private void releaseEntry(LruCache<TKey, TValue>.CacheEntry paramLruCache)
  {
    paramLruCache.key = null;
    paramLruCache.info = null;
    if (this.m_FreeEntryCount >= 128) {
      return;
    }
    this.m_FreeEntryCount += 1;
    if (this.m_FreeEntries != null)
    {
      this.m_FreeEntries.previous = paramLruCache;
      paramLruCache.next = this.m_FreeEntries;
    }
    this.m_FreeEntries = paramLruCache;
  }
  
  private boolean remove(LruCache<TKey, TValue>.CacheEntry paramLruCache, boolean paramBoolean)
  {
    if ((!onEntryRemoving(paramLruCache.key, paramLruCache.info)) && (paramBoolean)) {
      return false;
    }
    if (this.m_EntryHead == paramLruCache) {
      this.m_EntryHead = paramLruCache.next;
    }
    if (this.m_EntryTail == paramLruCache) {
      this.m_EntryTail = paramLruCache.previous;
    }
    removeEntry(paramLruCache);
    this.m_EntryTable.remove(paramLruCache.key);
    this.m_TotalValueSize -= paramLruCache.valueSize;
    removeEntry(paramLruCache.key, paramLruCache.info);
    onEntryRemoved(paramLruCache.key, paramLruCache.info);
    releaseEntry(paramLruCache);
    return true;
  }
  
  private void removeEntry(LruCache<TKey, TValue>.CacheEntry paramLruCache)
  {
    if (paramLruCache.previous != null) {
      paramLruCache.previous.next = paramLruCache.next;
    }
    if (paramLruCache.next != null) {
      paramLruCache.next.previous = paramLruCache.previous;
    }
    paramLruCache.previous = null;
    paramLruCache.next = null;
  }
  
  public boolean add(TKey paramTKey, TValue paramTValue)
  {
    long l = getSizeInBytes(paramTKey, paramTValue);
    if (l < 0L) {
      return false;
    }
    synchronized (this.syncLock)
    {
      CacheEntry localCacheEntry = (CacheEntry)this.m_EntryTable.get(paramTKey);
      if ((localCacheEntry == null) || (remove(localCacheEntry, true)))
      {
        addEntryDirectly(paramTKey, addEntry(paramTKey, paramTValue), l);
        return true;
      }
      return false;
    }
  }
  
  protected abstract Object addEntry(TKey paramTKey, TValue paramTValue);
  
  protected final void addEntryDirectly(TKey paramTKey, Object paramObject, long paramLong)
  {
    synchronized (this.syncLock)
    {
      CacheEntry localCacheEntry = (CacheEntry)this.m_EntryTable.get(paramTKey);
      if ((localCacheEntry == null) || (remove(localCacheEntry, true)))
      {
        localCacheEntry = obtainEntry(paramTKey);
        localCacheEntry.valueSize = paramLong;
        if (!checkCapacity(localCacheEntry.valueSize)) {
          releaseEntry(localCacheEntry);
        }
      }
      else
      {
        return;
      }
      localCacheEntry.info = paramObject;
      addEntryBefore(this.m_EntryHead, localCacheEntry);
      this.m_EntryHead = localCacheEntry;
      if (this.m_EntryTail == null) {
        this.m_EntryTail = localCacheEntry;
      }
      this.m_EntryTable.put(paramTKey, localCacheEntry);
      this.m_TotalValueSize += localCacheEntry.valueSize;
      return;
    }
  }
  
  /* Error */
  public void clear()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 46	com/oneplus/cache/LruCache:syncLock	Ljava/lang/Object;
    //   4: astore_3
    //   5: aload_3
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 38	com/oneplus/cache/LruCache:m_EntryHead	Lcom/oneplus/cache/LruCache$CacheEntry;
    //   11: astore_1
    //   12: aload_1
    //   13: ifnull +28 -> 41
    //   16: aload_1
    //   17: getfield 73	com/oneplus/cache/LruCache$CacheEntry:next	Lcom/oneplus/cache/LruCache$CacheEntry;
    //   20: astore_2
    //   21: aload_0
    //   22: aload_1
    //   23: iconst_0
    //   24: invokespecial 91	com/oneplus/cache/LruCache:remove	(Lcom/oneplus/cache/LruCache$CacheEntry;Z)Z
    //   27: pop
    //   28: aload_2
    //   29: astore_1
    //   30: goto -18 -> 12
    //   33: astore_1
    //   34: aload_1
    //   35: athrow
    //   36: astore_1
    //   37: aload_3
    //   38: monitorexit
    //   39: aload_1
    //   40: athrow
    //   41: aload_0
    //   42: getfield 51	com/oneplus/cache/LruCache:m_EntryTable	Ljava/util/HashMap;
    //   45: invokevirtual 159	java/util/HashMap:clear	()V
    //   48: aload_0
    //   49: aconst_null
    //   50: putfield 38	com/oneplus/cache/LruCache:m_EntryHead	Lcom/oneplus/cache/LruCache$CacheEntry;
    //   53: aload_0
    //   54: aconst_null
    //   55: putfield 84	com/oneplus/cache/LruCache:m_EntryTail	Lcom/oneplus/cache/LruCache$CacheEntry;
    //   58: aload_0
    //   59: lconst_0
    //   60: putfield 82	com/oneplus/cache/LruCache:m_TotalValueSize	J
    //   63: aload_3
    //   64: monitorexit
    //   65: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	66	0	this	LruCache
    //   11	19	1	localObject1	Object
    //   33	2	1	localObject2	Object
    //   36	4	1	localObject3	Object
    //   20	9	2	localCacheEntry	CacheEntry
    //   4	60	3	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   21	28	33	finally
    //   7	12	36	finally
    //   16	21	36	finally
    //   34	36	36	finally
    //   41	63	36	finally
  }
  
  public void close()
  {
    this.m_IsClosed = true;
  }
  
  public TValue get(TKey paramTKey, TValue paramTValue, long paramLong)
  {
    synchronized (this.syncLock)
    {
      CacheEntry localCacheEntry = (CacheEntry)this.m_EntryTable.get(paramTKey);
      if (localCacheEntry == null) {
        return paramTValue;
      }
      if (this.m_EntryHead != localCacheEntry)
      {
        if (this.m_EntryTail == localCacheEntry) {
          this.m_EntryTail = localCacheEntry.previous;
        }
        removeEntry(localCacheEntry);
        addEntryBefore(this.m_EntryHead, localCacheEntry);
        this.m_EntryHead = localCacheEntry;
      }
      paramTKey = get(paramTKey, localCacheEntry.info, paramTValue, paramLong);
      return paramTKey;
    }
  }
  
  protected abstract TValue get(TKey paramTKey, Object paramObject, TValue paramTValue, long paramLong);
  
  public final long getCapacity()
  {
    return this.m_Capacity;
  }
  
  protected abstract long getSizeInBytes(TKey paramTKey, TValue paramTValue);
  
  public final boolean isClosed()
  {
    return this.m_IsClosed;
  }
  
  protected final Iterator<TKey> listKeys()
  {
    return new KeyIterator();
  }
  
  protected void onEntryAdded(TKey paramTKey, TValue paramTValue, Object paramObject) {}
  
  protected void onEntryRemoved(TKey paramTKey, Object paramObject) {}
  
  protected boolean onEntryRemoving(TKey paramTKey, Object paramObject)
  {
    return true;
  }
  
  protected TValue peek(TKey paramTKey)
  {
    Object localObject1 = null;
    if (paramTKey == null) {
      return null;
    }
    synchronized (this.syncLock)
    {
      CacheEntry localCacheEntry = (CacheEntry)this.m_EntryTable.get(paramTKey);
      if (localCacheEntry != null) {
        localObject1 = get(paramTKey, localCacheEntry.info, null, 0L);
      }
      return (TValue)localObject1;
    }
  }
  
  public void remove(Cache.RemovingPredication<TKey> paramRemovingPredication)
  {
    if (paramRemovingPredication == null) {
      return;
    }
    synchronized (this.syncLock)
    {
      SimpleRef localSimpleRef = new SimpleRef(Boolean.valueOf(false));
      Object localObject1 = this.m_EntryHead;
      for (;;)
      {
        CacheEntry localCacheEntry;
        if (localObject1 != null) {
          localCacheEntry = ((CacheEntry)localObject1).next;
        }
        try
        {
          if (paramRemovingPredication.canRemove(((CacheEntry)localObject1).key, localSimpleRef)) {
            remove((CacheEntry)localObject1, true);
          }
          boolean bool = ((Boolean)localSimpleRef.get()).booleanValue();
          if (bool) {
            return;
          }
          localObject1 = localCacheEntry;
        }
        finally {}
      }
    }
  }
  
  public boolean remove(TKey paramTKey)
  {
    synchronized (this.syncLock)
    {
      paramTKey = (CacheEntry)this.m_EntryTable.get(paramTKey);
      if (paramTKey == null) {
        return false;
      }
      boolean bool = remove(paramTKey, true);
      return bool;
    }
  }
  
  protected abstract void removeEntry(TKey paramTKey, Object paramObject);
  
  public void setCapacity(long paramLong)
  {
    if (paramLong <= 0L) {
      throw new IllegalArgumentException("Invalid capacity : " + paramLong);
    }
    synchronized (this.syncLock)
    {
      this.m_Capacity = paramLong;
      checkCapacity(paramLong, 0L);
      return;
    }
  }
  
  protected final void throwIfClosed()
  {
    if (this.m_IsClosed) {
      throw new RuntimeException("Cache is closed.");
    }
  }
  
  public boolean trim(long paramLong)
  {
    synchronized (this.syncLock)
    {
      boolean bool = checkCapacity(Math.min(paramLong, this.m_Capacity), 0L);
      return bool;
    }
  }
  
  private final class CacheEntry
  {
    public volatile Object info;
    public volatile TKey key;
    public volatile LruCache<TKey, TValue>.CacheEntry next;
    public volatile LruCache<TKey, TValue>.CacheEntry previous;
    public volatile long valueSize;
    
    private CacheEntry() {}
  }
  
  private final class KeyIterator
    implements Iterator<TKey>
  {
    private LruCache<TKey, TValue>.CacheEntry m_Entry = LruCache.-get0(LruCache.this);
    private boolean m_IsFirstEntry = true;
    
    public KeyIterator() {}
    
    public boolean hasNext()
    {
      if (!this.m_IsFirstEntry) {
        return this.m_Entry.next != null;
      }
      return this.m_Entry != null;
    }
    
    public TKey next()
    {
      if (!this.m_IsFirstEntry) {
        this.m_Entry = this.m_Entry.next;
      }
      for (;;)
      {
        return (TKey)this.m_Entry.key;
        this.m_IsFirstEntry = false;
      }
    }
    
    public void remove()
    {
      throw new RuntimeException("Cannot remove key from cache");
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/cache/LruCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */