package android.support.v4.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class LruCache<K, V>
{
  private int createCount;
  private int evictionCount;
  private int hitCount;
  private final LinkedHashMap<K, V> map;
  private int maxSize;
  private int missCount;
  private int putCount;
  private int size;
  
  public LruCache(int paramInt)
  {
    if (paramInt > 0)
    {
      this.maxSize = paramInt;
      this.map = new LinkedHashMap(0, 0.75F, true);
      return;
    }
    throw new IllegalArgumentException("maxSize <= 0");
  }
  
  private int safeSizeOf(K paramK, V paramV)
  {
    int i = sizeOf(paramK, paramV);
    if (i >= 0) {
      return i;
    }
    throw new IllegalStateException("Negative size: " + paramK + "=" + paramV);
  }
  
  protected V create(K paramK)
  {
    return null;
  }
  
  public final int createCount()
  {
    try
    {
      int i = this.createCount;
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  protected void entryRemoved(boolean paramBoolean, K paramK, V paramV1, V paramV2) {}
  
  public final void evictAll()
  {
    trimToSize(-1);
  }
  
  public final int evictionCount()
  {
    try
    {
      int i = this.evictionCount;
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  /* Error */
  public final V get(K paramK)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnull +97 -> 98
    //   4: aload_0
    //   5: monitorenter
    //   6: aload_0
    //   7: getfield 31	android/support/v4/util/LruCache:map	Ljava/util/LinkedHashMap;
    //   10: aload_1
    //   11: invokevirtual 85	java/util/LinkedHashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   14: astore_2
    //   15: aload_2
    //   16: ifnonnull +92 -> 108
    //   19: aload_0
    //   20: aload_0
    //   21: getfield 87	android/support/v4/util/LruCache:missCount	I
    //   24: iconst_1
    //   25: iadd
    //   26: putfield 87	android/support/v4/util/LruCache:missCount	I
    //   29: aload_0
    //   30: monitorexit
    //   31: aload_0
    //   32: aload_1
    //   33: invokevirtual 89	android/support/v4/util/LruCache:create	(Ljava/lang/Object;)Ljava/lang/Object;
    //   36: astore_2
    //   37: aload_2
    //   38: ifnull +89 -> 127
    //   41: aload_0
    //   42: monitorenter
    //   43: aload_0
    //   44: aload_0
    //   45: getfield 73	android/support/v4/util/LruCache:createCount	I
    //   48: iconst_1
    //   49: iadd
    //   50: putfield 73	android/support/v4/util/LruCache:createCount	I
    //   53: aload_0
    //   54: getfield 31	android/support/v4/util/LruCache:map	Ljava/util/LinkedHashMap;
    //   57: aload_1
    //   58: aload_2
    //   59: invokevirtual 93	java/util/LinkedHashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   62: astore_3
    //   63: aload_3
    //   64: ifnonnull +65 -> 129
    //   67: aload_0
    //   68: aload_0
    //   69: getfield 95	android/support/v4/util/LruCache:size	I
    //   72: aload_0
    //   73: aload_1
    //   74: aload_2
    //   75: invokespecial 97	android/support/v4/util/LruCache:safeSizeOf	(Ljava/lang/Object;Ljava/lang/Object;)I
    //   78: iadd
    //   79: putfield 95	android/support/v4/util/LruCache:size	I
    //   82: aload_0
    //   83: monitorexit
    //   84: aload_3
    //   85: ifnonnull +62 -> 147
    //   88: aload_0
    //   89: aload_0
    //   90: getfield 23	android/support/v4/util/LruCache:maxSize	I
    //   93: invokevirtual 80	android/support/v4/util/LruCache:trimToSize	(I)V
    //   96: aload_2
    //   97: areturn
    //   98: new 99	java/lang/NullPointerException
    //   101: dup
    //   102: ldc 101
    //   104: invokespecial 102	java/lang/NullPointerException:<init>	(Ljava/lang/String;)V
    //   107: athrow
    //   108: aload_0
    //   109: aload_0
    //   110: getfield 104	android/support/v4/util/LruCache:hitCount	I
    //   113: iconst_1
    //   114: iadd
    //   115: putfield 104	android/support/v4/util/LruCache:hitCount	I
    //   118: aload_0
    //   119: monitorexit
    //   120: aload_2
    //   121: areturn
    //   122: astore_1
    //   123: aload_0
    //   124: monitorexit
    //   125: aload_1
    //   126: athrow
    //   127: aconst_null
    //   128: areturn
    //   129: aload_0
    //   130: getfield 31	android/support/v4/util/LruCache:map	Ljava/util/LinkedHashMap;
    //   133: aload_1
    //   134: aload_3
    //   135: invokevirtual 93	java/util/LinkedHashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   138: pop
    //   139: goto -57 -> 82
    //   142: astore_1
    //   143: aload_0
    //   144: monitorexit
    //   145: aload_1
    //   146: athrow
    //   147: aload_0
    //   148: iconst_0
    //   149: aload_1
    //   150: aload_2
    //   151: aload_3
    //   152: invokevirtual 106	android/support/v4/util/LruCache:entryRemoved	(ZLjava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   155: aload_3
    //   156: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	157	0	this	LruCache
    //   0	157	1	paramK	K
    //   14	137	2	localObject1	Object
    //   62	94	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   6	15	122	finally
    //   19	31	122	finally
    //   108	120	122	finally
    //   123	125	122	finally
    //   43	63	142	finally
    //   67	82	142	finally
    //   82	84	142	finally
    //   129	139	142	finally
    //   143	145	142	finally
  }
  
  public final int hitCount()
  {
    try
    {
      int i = this.hitCount;
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public final int maxSize()
  {
    try
    {
      int i = this.maxSize;
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public final int missCount()
  {
    try
    {
      int i = this.missCount;
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public final V put(K paramK, V paramV)
  {
    if (paramK == null) {}
    while (paramV == null) {
      throw new NullPointerException("key == null || value == null");
    }
    for (;;)
    {
      try
      {
        this.putCount += 1;
        this.size += safeSizeOf(paramK, paramV);
        Object localObject = this.map.put(paramK, paramV);
        if (localObject == null)
        {
          if (localObject == null)
          {
            trimToSize(this.maxSize);
            return (V)localObject;
          }
        }
        else
        {
          this.size -= safeSizeOf(paramK, localObject);
          continue;
        }
        entryRemoved(false, paramK, localObject, paramV);
      }
      finally {}
    }
  }
  
  public final int putCount()
  {
    try
    {
      int i = this.putCount;
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public final V remove(K paramK)
  {
    if (paramK != null) {}
    Object localObject;
    try
    {
      localObject = this.map.remove(paramK);
      if (localObject == null) {}
      for (;;)
      {
        if (localObject != null) {
          break;
        }
        return (V)localObject;
        throw new NullPointerException("key == null");
        this.size -= safeSizeOf(paramK, localObject);
      }
      entryRemoved(false, paramK, localObject, null);
    }
    finally {}
    return (V)localObject;
  }
  
  public void resize(int paramInt)
  {
    if (paramInt > 0) {}
    try
    {
      this.maxSize = paramInt;
      trimToSize(paramInt);
      return;
    }
    finally {}
    throw new IllegalArgumentException("maxSize <= 0");
  }
  
  public final int size()
  {
    try
    {
      int i = this.size;
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  protected int sizeOf(K paramK, V paramV)
  {
    return 1;
  }
  
  public final Map<K, V> snapshot()
  {
    try
    {
      LinkedHashMap localLinkedHashMap = new LinkedHashMap(this.map);
      return localLinkedHashMap;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  /* Error */
  public final String toString()
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_1
    //   2: aload_0
    //   3: monitorenter
    //   4: aload_0
    //   5: getfield 104	android/support/v4/util/LruCache:hitCount	I
    //   8: aload_0
    //   9: getfield 87	android/support/v4/util/LruCache:missCount	I
    //   12: iadd
    //   13: istore_2
    //   14: iload_2
    //   15: ifne +54 -> 69
    //   18: ldc 123
    //   20: iconst_4
    //   21: anewarray 5	java/lang/Object
    //   24: dup
    //   25: iconst_0
    //   26: aload_0
    //   27: getfield 23	android/support/v4/util/LruCache:maxSize	I
    //   30: invokestatic 129	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   33: aastore
    //   34: dup
    //   35: iconst_1
    //   36: aload_0
    //   37: getfield 104	android/support/v4/util/LruCache:hitCount	I
    //   40: invokestatic 129	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   43: aastore
    //   44: dup
    //   45: iconst_2
    //   46: aload_0
    //   47: getfield 87	android/support/v4/util/LruCache:missCount	I
    //   50: invokestatic 129	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   53: aastore
    //   54: dup
    //   55: iconst_3
    //   56: iload_1
    //   57: invokestatic 129	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   60: aastore
    //   61: invokestatic 135	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   64: astore_3
    //   65: aload_0
    //   66: monitorexit
    //   67: aload_3
    //   68: areturn
    //   69: aload_0
    //   70: getfield 104	android/support/v4/util/LruCache:hitCount	I
    //   73: bipush 100
    //   75: imul
    //   76: iload_2
    //   77: idiv
    //   78: istore_1
    //   79: goto -61 -> 18
    //   82: astore_3
    //   83: aload_0
    //   84: monitorexit
    //   85: aload_3
    //   86: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	87	0	this	LruCache
    //   1	78	1	i	int
    //   13	65	2	j	int
    //   64	4	3	str	String
    //   82	4	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   4	14	82	finally
    //   18	65	82	finally
    //   69	79	82	finally
  }
  
  public void trimToSize(int paramInt)
  {
    label57:
    label78:
    Object localObject3;
    Object localObject2;
    try
    {
      if (this.size < 0) {
        throw new IllegalStateException(getClass().getName() + ".sizeOf() is reporting inconsistent results!");
      }
    }
    finally
    {
      throw ((Throwable)localObject1);
      if (!this.map.isEmpty()) {
        if (this.size > paramInt) {
          break label78;
        }
      }
      while (this.map.isEmpty())
      {
        return;
        if (this.size != 0) {
          break;
        }
        break label57;
      }
      localObject3 = (Map.Entry)this.map.entrySet().iterator().next();
      localObject2 = ((Map.Entry)localObject3).getKey();
      localObject3 = ((Map.Entry)localObject3).getValue();
      this.map.remove(localObject2);
      this.size -= safeSizeOf(localObject2, localObject3);
      this.evictionCount += 1;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/util/LruCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */