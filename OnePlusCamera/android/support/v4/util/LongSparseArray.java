package android.support.v4.util;

public class LongSparseArray<E>
  implements Cloneable
{
  private static final Object DELETED = new Object();
  private boolean mGarbage = false;
  private long[] mKeys;
  private int mSize;
  private Object[] mValues;
  
  public LongSparseArray()
  {
    this(10);
  }
  
  public LongSparseArray(int paramInt)
  {
    if (paramInt != 0)
    {
      paramInt = ContainerHelpers.idealLongArraySize(paramInt);
      this.mKeys = new long[paramInt];
    }
    for (this.mValues = new Object[paramInt];; this.mValues = ContainerHelpers.EMPTY_OBJECTS)
    {
      this.mSize = 0;
      return;
      this.mKeys = ContainerHelpers.EMPTY_LONGS;
    }
  }
  
  private void gc()
  {
    int k = this.mSize;
    long[] arrayOfLong = this.mKeys;
    Object[] arrayOfObject = this.mValues;
    int i = 0;
    int j = 0;
    Object localObject;
    for (;;)
    {
      if (i >= k)
      {
        this.mGarbage = false;
        this.mSize = j;
        return;
      }
      localObject = arrayOfObject[i];
      if (localObject != DELETED) {
        break;
      }
      i += 1;
    }
    if (i == j) {}
    for (;;)
    {
      j += 1;
      break;
      arrayOfLong[j] = arrayOfLong[i];
      arrayOfObject[j] = localObject;
      arrayOfObject[i] = null;
    }
  }
  
  public void append(long paramLong, E paramE)
  {
    label14:
    int i;
    if (this.mSize == 0)
    {
      if (this.mGarbage) {
        break label92;
      }
      i = this.mSize;
      if (i >= this.mKeys.length) {
        break label111;
      }
    }
    for (;;)
    {
      this.mKeys[i] = paramLong;
      this.mValues[i] = paramE;
      this.mSize = (i + 1);
      return;
      if (paramLong > this.mKeys[(this.mSize - 1)]) {}
      for (i = 1; i == 0; i = 0)
      {
        put(paramLong, paramE);
        return;
      }
      break;
      label92:
      if (this.mSize < this.mKeys.length) {
        break label14;
      }
      gc();
      break label14;
      label111:
      int j = ContainerHelpers.idealLongArraySize(i + 1);
      long[] arrayOfLong = new long[j];
      Object[] arrayOfObject = new Object[j];
      System.arraycopy(this.mKeys, 0, arrayOfLong, 0, this.mKeys.length);
      System.arraycopy(this.mValues, 0, arrayOfObject, 0, this.mValues.length);
      this.mKeys = arrayOfLong;
      this.mValues = arrayOfObject;
    }
  }
  
  public void clear()
  {
    int j = this.mSize;
    Object[] arrayOfObject = this.mValues;
    int i = 0;
    for (;;)
    {
      if (i >= j)
      {
        this.mSize = 0;
        this.mGarbage = false;
        return;
      }
      arrayOfObject[i] = null;
      i += 1;
    }
  }
  
  public LongSparseArray<E> clone()
  {
    try
    {
      LongSparseArray localLongSparseArray = (LongSparseArray)super.clone();
      return localCloneNotSupportedException1;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException1)
    {
      try
      {
        localLongSparseArray.mKeys = ((long[])this.mKeys.clone());
        localLongSparseArray.mValues = ((Object[])this.mValues.clone());
        return localLongSparseArray;
      }
      catch (CloneNotSupportedException localCloneNotSupportedException2) {}
      localCloneNotSupportedException1 = localCloneNotSupportedException1;
      return null;
    }
  }
  
  public void delete(long paramLong)
  {
    int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramLong);
    if (i < 0) {}
    while (this.mValues[i] == DELETED) {
      return;
    }
    this.mValues[i] = DELETED;
    this.mGarbage = true;
  }
  
  public E get(long paramLong)
  {
    return (E)get(paramLong, null);
  }
  
  public E get(long paramLong, E paramE)
  {
    int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramLong);
    if (i < 0) {}
    while (this.mValues[i] == DELETED) {
      return paramE;
    }
    return (E)this.mValues[i];
  }
  
  public int indexOfKey(long paramLong)
  {
    if (!this.mGarbage) {}
    for (;;)
    {
      return ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramLong);
      gc();
    }
  }
  
  public int indexOfValue(E paramE)
  {
    int i = 0;
    if (!this.mGarbage) {}
    for (;;)
    {
      if (i >= this.mSize)
      {
        return -1;
        gc();
      }
      else
      {
        if (this.mValues[i] == paramE) {
          break;
        }
        i += 1;
      }
    }
    return i;
  }
  
  public long keyAt(int paramInt)
  {
    if (!this.mGarbage) {}
    for (;;)
    {
      return this.mKeys[paramInt];
      gc();
    }
  }
  
  public void put(long paramLong, E paramE)
  {
    int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramLong);
    if (i < 0)
    {
      i ^= 0xFFFFFFFF;
      if (i < this.mSize) {
        break label99;
      }
      if (this.mGarbage) {
        break label129;
      }
      label41:
      if (this.mSize >= this.mKeys.length) {
        break label164;
      }
      label53:
      if (this.mSize - i != 0) {
        break label235;
      }
    }
    for (;;)
    {
      this.mKeys[i] = paramLong;
      this.mValues[i] = paramE;
      this.mSize += 1;
      return;
      this.mValues[i] = paramE;
      return;
      label99:
      if (this.mValues[i] != DELETED) {
        break;
      }
      this.mKeys[i] = paramLong;
      this.mValues[i] = paramE;
      return;
      label129:
      if (this.mSize < this.mKeys.length) {
        break label41;
      }
      gc();
      i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramLong) ^ 0xFFFFFFFF;
      break label41;
      label164:
      int j = ContainerHelpers.idealLongArraySize(this.mSize + 1);
      long[] arrayOfLong = new long[j];
      Object[] arrayOfObject = new Object[j];
      System.arraycopy(this.mKeys, 0, arrayOfLong, 0, this.mKeys.length);
      System.arraycopy(this.mValues, 0, arrayOfObject, 0, this.mValues.length);
      this.mKeys = arrayOfLong;
      this.mValues = arrayOfObject;
      break label53;
      label235:
      System.arraycopy(this.mKeys, i, this.mKeys, i + 1, this.mSize - i);
      System.arraycopy(this.mValues, i, this.mValues, i + 1, this.mSize - i);
    }
  }
  
  public void remove(long paramLong)
  {
    delete(paramLong);
  }
  
  public void removeAt(int paramInt)
  {
    if (this.mValues[paramInt] == DELETED) {
      return;
    }
    this.mValues[paramInt] = DELETED;
    this.mGarbage = true;
  }
  
  public void setValueAt(int paramInt, E paramE)
  {
    if (!this.mGarbage) {}
    for (;;)
    {
      this.mValues[paramInt] = paramE;
      return;
      gc();
    }
  }
  
  public int size()
  {
    if (!this.mGarbage) {}
    for (;;)
    {
      return this.mSize;
      gc();
    }
  }
  
  public String toString()
  {
    int i = 0;
    StringBuilder localStringBuilder;
    if (size() > 0)
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
      localStringBuilder.append(keyAt(i));
      localStringBuilder.append('=');
      localObject = valueAt(i);
      if (localObject != this) {
        break label110;
      }
      localStringBuilder.append("(this Map)");
    }
    for (;;)
    {
      i += 1;
      break;
      localStringBuilder.append(", ");
      break label58;
      label110:
      localStringBuilder.append(localObject);
    }
  }
  
  public E valueAt(int paramInt)
  {
    if (!this.mGarbage) {}
    for (;;)
    {
      return (E)this.mValues[paramInt];
      gc();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/util/LongSparseArray.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */