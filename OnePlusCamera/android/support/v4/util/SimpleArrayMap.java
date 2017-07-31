package android.support.v4.util;

import java.util.Map;

public class SimpleArrayMap<K, V>
{
  private static final int BASE_SIZE = 4;
  private static final int CACHE_SIZE = 10;
  private static final boolean DEBUG = false;
  private static final String TAG = "ArrayMap";
  static Object[] mBaseCache;
  static int mBaseCacheSize;
  static Object[] mTwiceBaseCache;
  static int mTwiceBaseCacheSize;
  Object[] mArray;
  int[] mHashes;
  int mSize;
  
  public SimpleArrayMap()
  {
    this.mHashes = ContainerHelpers.EMPTY_INTS;
    this.mArray = ContainerHelpers.EMPTY_OBJECTS;
    this.mSize = 0;
  }
  
  public SimpleArrayMap(int paramInt)
  {
    if (paramInt != 0) {
      allocArrays(paramInt);
    }
    for (;;)
    {
      this.mSize = 0;
      return;
      this.mHashes = ContainerHelpers.EMPTY_INTS;
      this.mArray = ContainerHelpers.EMPTY_OBJECTS;
    }
  }
  
  public SimpleArrayMap(SimpleArrayMap paramSimpleArrayMap)
  {
    this();
    if (paramSimpleArrayMap == null) {
      return;
    }
    putAll(paramSimpleArrayMap);
  }
  
  private void allocArrays(int paramInt)
  {
    if (paramInt != 8) {
      if (paramInt == 4) {
        break label104;
      }
    }
    for (;;)
    {
      this.mHashes = new int[paramInt];
      this.mArray = new Object[paramInt << 1];
      return;
      try
      {
        if (mTwiceBaseCache == null) {
          continue;
        }
      }
      finally {}
      Object[] arrayOfObject1 = mTwiceBaseCache;
      this.mArray = arrayOfObject1;
      mTwiceBaseCache = (Object[])arrayOfObject1[0];
      this.mHashes = ((int[])arrayOfObject1[1]);
      arrayOfObject1[1] = null;
      arrayOfObject1[0] = null;
      mTwiceBaseCacheSize -= 1;
      return;
      try
      {
        label104:
        if (mBaseCache == null) {}
      }
      finally {}
    }
    Object[] arrayOfObject2 = mBaseCache;
    this.mArray = arrayOfObject2;
    mBaseCache = (Object[])arrayOfObject2[0];
    this.mHashes = ((int[])arrayOfObject2[1]);
    arrayOfObject2[1] = null;
    arrayOfObject2[0] = null;
    mBaseCacheSize -= 1;
  }
  
  private static void freeArrays(int[] paramArrayOfInt, Object[] paramArrayOfObject, int paramInt)
  {
    if (paramArrayOfInt.length != 8)
    {
      if (paramArrayOfInt.length == 4) {}
    }
    else
    {
      try
      {
        if (mTwiceBaseCacheSize >= 10) {
          return;
        }
      }
      finally {}
      paramArrayOfObject[0] = mTwiceBaseCache;
      paramArrayOfObject[1] = paramArrayOfInt;
      paramInt = (paramInt << 1) - 1;
      for (;;)
      {
        if (paramInt < 2)
        {
          mTwiceBaseCache = paramArrayOfObject;
          mTwiceBaseCacheSize += 1;
          break;
        }
        paramArrayOfObject[paramInt] = null;
        paramInt -= 1;
      }
    }
    try
    {
      if (mBaseCacheSize >= 10) {
        return;
      }
    }
    finally {}
    paramArrayOfObject[0] = mBaseCache;
    paramArrayOfObject[1] = paramArrayOfInt;
    paramInt = (paramInt << 1) - 1;
    for (;;)
    {
      if (paramInt < 2)
      {
        mBaseCache = paramArrayOfObject;
        mBaseCacheSize += 1;
        break;
      }
      paramArrayOfObject[paramInt] = null;
      paramInt -= 1;
    }
  }
  
  public void clear()
  {
    if (this.mSize == 0) {
      return;
    }
    freeArrays(this.mHashes, this.mArray, this.mSize);
    this.mHashes = ContainerHelpers.EMPTY_INTS;
    this.mArray = ContainerHelpers.EMPTY_OBJECTS;
    this.mSize = 0;
  }
  
  public boolean containsKey(Object paramObject)
  {
    return indexOfKey(paramObject) >= 0;
  }
  
  public boolean containsValue(Object paramObject)
  {
    return indexOfValue(paramObject) >= 0;
  }
  
  public void ensureCapacity(int paramInt)
  {
    if (this.mHashes.length >= paramInt) {
      return;
    }
    int[] arrayOfInt = this.mHashes;
    Object[] arrayOfObject = this.mArray;
    allocArrays(paramInt);
    if (this.mSize <= 0) {}
    for (;;)
    {
      freeArrays(arrayOfInt, arrayOfObject, this.mSize);
      return;
      System.arraycopy(arrayOfInt, 0, this.mHashes, 0, this.mSize);
      System.arraycopy(arrayOfObject, 0, this.mArray, 0, this.mSize << 1);
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (this != paramObject)
    {
      if (!(paramObject instanceof Map)) {
        return false;
      }
    }
    else {
      return true;
    }
    paramObject = (Map)paramObject;
    int i;
    if (size() == ((Map)paramObject).size()) {
      i = 0;
    }
    Object localObject3;
    label114:
    do
    {
      for (;;)
      {
        try
        {
          if (i >= this.mSize) {
            return true;
          }
          Object localObject1 = keyAt(i);
          Object localObject2 = valueAt(i);
          localObject3 = ((Map)paramObject).get(localObject1);
          if (localObject2 == null) {
            break;
          }
          if (localObject2.equals(localObject3))
          {
            break label114;
            boolean bool = ((Map)paramObject).containsKey(localObject1);
            if (!bool) {
              break label126;
            }
            break label114;
          }
          return false;
        }
        catch (NullPointerException paramObject)
        {
          return false;
        }
        catch (ClassCastException paramObject)
        {
          return false;
        }
        return false;
        i += 1;
      }
    } while (localObject3 == null);
    label126:
    return false;
  }
  
  public V get(Object paramObject)
  {
    int i = indexOfKey(paramObject);
    if (i < 0) {
      return null;
    }
    return (V)this.mArray[((i << 1) + 1)];
  }
  
  public int hashCode()
  {
    int[] arrayOfInt = this.mHashes;
    Object[] arrayOfObject = this.mArray;
    int n = this.mSize;
    int i = 1;
    int j = 0;
    int k = 0;
    if (j >= n) {
      return k;
    }
    Object localObject = arrayOfObject[i];
    int i1 = arrayOfInt[j];
    if (localObject != null) {}
    for (int m = localObject.hashCode();; m = 0)
    {
      k += (m ^ i1);
      j += 1;
      i += 2;
      break;
    }
  }
  
  int indexOf(Object paramObject, int paramInt)
  {
    int j = this.mSize;
    int k;
    int i;
    if (j != 0)
    {
      k = ContainerHelpers.binarySearch(this.mHashes, j, paramInt);
      if (k < 0) {
        break label72;
      }
      if (paramObject.equals(this.mArray[(k << 1)])) {
        break label75;
      }
      i = k + 1;
      if (i < j) {
        break label78;
      }
      label55:
      j = k - 1;
    }
    for (;;)
    {
      if (j < 0) {}
      label72:
      label75:
      label78:
      while (this.mHashes[j] != paramInt)
      {
        return i ^ 0xFFFFFFFF;
        return -1;
        return k;
        return k;
        if (this.mHashes[i] != paramInt) {
          break label55;
        }
        if (!paramObject.equals(this.mArray[(i << 1)]))
        {
          i += 1;
          break;
        }
        return i;
      }
      if (paramObject.equals(this.mArray[(j << 1)])) {
        break label148;
      }
      j -= 1;
    }
    label148:
    return j;
  }
  
  public int indexOfKey(Object paramObject)
  {
    if (paramObject != null) {
      return indexOf(paramObject, paramObject.hashCode());
    }
    return indexOfNull();
  }
  
  int indexOfNull()
  {
    int j = this.mSize;
    int k;
    int i;
    if (j != 0)
    {
      k = ContainerHelpers.binarySearch(this.mHashes, j, 0);
      if (k < 0) {
        break label57;
      }
      if (this.mArray[(k << 1)] == null) {
        break label59;
      }
      i = k + 1;
      if (i < j) {
        break label61;
      }
      label43:
      j = k - 1;
    }
    for (;;)
    {
      if (j < 0) {}
      label57:
      label59:
      label61:
      while (this.mHashes[j] != 0)
      {
        return i ^ 0xFFFFFFFF;
        return -1;
        return k;
        return k;
        if (this.mHashes[i] != 0) {
          break label43;
        }
        if (this.mArray[(i << 1)] != null)
        {
          i += 1;
          break;
        }
        return i;
      }
      if (this.mArray[(j << 1)] == null) {
        return j;
      }
      j -= 1;
    }
    return j;
  }
  
  int indexOfValue(Object paramObject)
  {
    int i = 1;
    int j = 1;
    int k = this.mSize * 2;
    Object[] arrayOfObject = this.mArray;
    if (paramObject != null) {
      i = j;
    }
    for (;;)
    {
      if (i >= k)
      {
        for (;;)
        {
          return -1;
          do
          {
            if (arrayOfObject[i] == null) {
              break;
            }
            i += 2;
          } while (i < k);
        }
        return i >> 1;
      }
      if (paramObject.equals(arrayOfObject[i])) {
        break;
      }
      i += 2;
    }
    return i >> 1;
  }
  
  public boolean isEmpty()
  {
    return this.mSize <= 0;
  }
  
  public K keyAt(int paramInt)
  {
    return (K)this.mArray[(paramInt << 1)];
  }
  
  public V put(K paramK, V paramV)
  {
    int k = 4;
    int j;
    int i;
    int m;
    if (paramK != null)
    {
      j = paramK.hashCode();
      i = indexOf(paramK, j);
      if (i >= 0) {
        break label105;
      }
      m = i ^ 0xFFFFFFFF;
      if (this.mSize >= this.mHashes.length) {
        break label127;
      }
      if (m < this.mSize) {
        break label238;
      }
    }
    for (;;)
    {
      this.mHashes[m] = j;
      this.mArray[(m << 1)] = paramK;
      this.mArray[((m << 1) + 1)] = paramV;
      this.mSize += 1;
      return null;
      i = indexOfNull();
      j = 0;
      break;
      label105:
      i = (i << 1) + 1;
      paramK = this.mArray[i];
      this.mArray[i] = paramV;
      return paramK;
      label127:
      label147:
      int[] arrayOfInt;
      Object[] arrayOfObject;
      if (this.mSize < 8)
      {
        if (this.mSize >= 4) {
          break label201;
        }
        i = k;
        arrayOfInt = this.mHashes;
        arrayOfObject = this.mArray;
        allocArrays(i);
        if (this.mHashes.length > 0) {
          break label207;
        }
      }
      for (;;)
      {
        freeArrays(arrayOfInt, arrayOfObject, this.mSize);
        break;
        i = this.mSize + (this.mSize >> 1);
        break label147;
        label201:
        i = 8;
        break label147;
        label207:
        System.arraycopy(arrayOfInt, 0, this.mHashes, 0, arrayOfInt.length);
        System.arraycopy(arrayOfObject, 0, this.mArray, 0, arrayOfObject.length);
      }
      label238:
      System.arraycopy(this.mHashes, m, this.mHashes, m + 1, this.mSize - m);
      System.arraycopy(this.mArray, m << 1, this.mArray, m + 1 << 1, this.mSize - m << 1);
    }
  }
  
  public void putAll(SimpleArrayMap<? extends K, ? extends V> paramSimpleArrayMap)
  {
    int i = 0;
    int j = paramSimpleArrayMap.mSize;
    ensureCapacity(this.mSize + j);
    if (this.mSize != 0) {}
    for (;;)
    {
      if (i >= j)
      {
        do
        {
          return;
        } while (j <= 0);
        System.arraycopy(paramSimpleArrayMap.mHashes, 0, this.mHashes, 0, j);
        System.arraycopy(paramSimpleArrayMap.mArray, 0, this.mArray, 0, j << 1);
        this.mSize = j;
        return;
      }
      put(paramSimpleArrayMap.keyAt(i), paramSimpleArrayMap.valueAt(i));
      i += 1;
    }
  }
  
  public V remove(Object paramObject)
  {
    int i = indexOfKey(paramObject);
    if (i < 0) {
      return null;
    }
    return (V)removeAt(i);
  }
  
  public V removeAt(int paramInt)
  {
    int i = 8;
    Object localObject = this.mArray[((paramInt << 1) + 1)];
    if (this.mSize > 1)
    {
      if (this.mHashes.length > 8) {
        break label114;
      }
      this.mSize -= 1;
      if (paramInt < this.mSize) {
        break label266;
      }
    }
    for (;;)
    {
      this.mArray[(this.mSize << 1)] = null;
      this.mArray[((this.mSize << 1) + 1)] = null;
      label114:
      label237:
      label264:
      for (;;)
      {
        return (V)localObject;
        freeArrays(this.mHashes, this.mArray, this.mSize);
        this.mHashes = ContainerHelpers.EMPTY_INTS;
        this.mArray = ContainerHelpers.EMPTY_OBJECTS;
        this.mSize = 0;
        return (V)localObject;
        if (this.mSize >= this.mHashes.length / 3) {
          break;
        }
        int[] arrayOfInt;
        Object[] arrayOfObject;
        if (this.mSize <= 8)
        {
          arrayOfInt = this.mHashes;
          arrayOfObject = this.mArray;
          allocArrays(i);
          this.mSize -= 1;
          if (paramInt > 0) {
            break label237;
          }
        }
        for (;;)
        {
          if (paramInt >= this.mSize) {
            break label264;
          }
          System.arraycopy(arrayOfInt, paramInt + 1, this.mHashes, paramInt, this.mSize - paramInt);
          System.arraycopy(arrayOfObject, paramInt + 1 << 1, this.mArray, paramInt << 1, this.mSize - paramInt << 1);
          return (V)localObject;
          i = this.mSize + (this.mSize >> 1);
          break;
          System.arraycopy(arrayOfInt, 0, this.mHashes, 0, paramInt);
          System.arraycopy(arrayOfObject, 0, this.mArray, 0, paramInt << 1);
        }
      }
      label266:
      System.arraycopy(this.mHashes, paramInt + 1, this.mHashes, paramInt, this.mSize - paramInt);
      System.arraycopy(this.mArray, paramInt + 1 << 1, this.mArray, paramInt << 1, this.mSize - paramInt << 1);
    }
  }
  
  public V setValueAt(int paramInt, V paramV)
  {
    paramInt = (paramInt << 1) + 1;
    Object localObject = this.mArray[paramInt];
    this.mArray[paramInt] = paramV;
    return (V)localObject;
  }
  
  public int size()
  {
    return this.mSize;
  }
  
  public String toString()
  {
    int i = 0;
    StringBuilder localStringBuilder;
    if (!isEmpty())
    {
      localStringBuilder = new StringBuilder(this.mSize * 28);
      localStringBuilder.append('{');
      if (i >= this.mSize)
      {
        localStringBuilder.append('}');
        return localStringBuilder.toString();
      }
    }
    else
    {
      return "{}";
    }
    label58:
    Object localObject;
    if (i <= 0)
    {
      localObject = keyAt(i);
      if (localObject != this) {
        break label118;
      }
      localStringBuilder.append("(this Map)");
      label76:
      localStringBuilder.append('=');
      localObject = valueAt(i);
      if (localObject != this) {
        break label127;
      }
      localStringBuilder.append("(this Map)");
    }
    for (;;)
    {
      i += 1;
      break;
      localStringBuilder.append(", ");
      break label58;
      label118:
      localStringBuilder.append(localObject);
      break label76;
      label127:
      localStringBuilder.append(localObject);
    }
  }
  
  public V valueAt(int paramInt)
  {
    return (V)this.mArray[((paramInt << 1) + 1)];
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/util/SimpleArrayMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */