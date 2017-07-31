package android.support.v4.util;

public class SparseArrayCompat<E>
  implements Cloneable
{
  private static final Object DELETED = new Object();
  private boolean mGarbage = false;
  private int[] mKeys;
  private int mSize;
  private Object[] mValues;
  
  public SparseArrayCompat()
  {
    this(10);
  }
  
  public SparseArrayCompat(int paramInt)
  {
    if (paramInt != 0)
    {
      paramInt = ContainerHelpers.idealIntArraySize(paramInt);
      this.mKeys = new int[paramInt];
    }
    for (this.mValues = new Object[paramInt];; this.mValues = ContainerHelpers.EMPTY_OBJECTS)
    {
      this.mSize = 0;
      return;
      this.mKeys = ContainerHelpers.EMPTY_INTS;
    }
  }
  
  private void gc()
  {
    int k = this.mSize;
    int[] arrayOfInt = this.mKeys;
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
      arrayOfInt[j] = arrayOfInt[i];
      arrayOfObject[j] = localObject;
      arrayOfObject[i] = null;
    }
  }
  
  public void append(int paramInt, E paramE)
  {
    label14:
    int i;
    if (this.mSize == 0)
    {
      if (this.mGarbage) {
        break label72;
      }
      i = this.mSize;
      if (i >= this.mKeys.length) {
        break label91;
      }
    }
    for (;;)
    {
      this.mKeys[i] = paramInt;
      this.mValues[i] = paramE;
      this.mSize = (i + 1);
      return;
      if (paramInt > this.mKeys[(this.mSize - 1)]) {
        break;
      }
      put(paramInt, paramE);
      return;
      label72:
      if (this.mSize < this.mKeys.length) {
        break label14;
      }
      gc();
      break label14;
      label91:
      int j = ContainerHelpers.idealIntArraySize(i + 1);
      int[] arrayOfInt = new int[j];
      Object[] arrayOfObject = new Object[j];
      System.arraycopy(this.mKeys, 0, arrayOfInt, 0, this.mKeys.length);
      System.arraycopy(this.mValues, 0, arrayOfObject, 0, this.mValues.length);
      this.mKeys = arrayOfInt;
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
  
  public SparseArrayCompat<E> clone()
  {
    try
    {
      SparseArrayCompat localSparseArrayCompat = (SparseArrayCompat)super.clone();
      return localCloneNotSupportedException1;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException1)
    {
      try
      {
        localSparseArrayCompat.mKeys = ((int[])this.mKeys.clone());
        localSparseArrayCompat.mValues = ((Object[])this.mValues.clone());
        return localSparseArrayCompat;
      }
      catch (CloneNotSupportedException localCloneNotSupportedException2) {}
      localCloneNotSupportedException1 = localCloneNotSupportedException1;
      return null;
    }
  }
  
  public void delete(int paramInt)
  {
    paramInt = ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramInt);
    if (paramInt < 0) {}
    while (this.mValues[paramInt] == DELETED) {
      return;
    }
    this.mValues[paramInt] = DELETED;
    this.mGarbage = true;
  }
  
  public E get(int paramInt)
  {
    return (E)get(paramInt, null);
  }
  
  public E get(int paramInt, E paramE)
  {
    paramInt = ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramInt);
    if (paramInt < 0) {}
    while (this.mValues[paramInt] == DELETED) {
      return paramE;
    }
    return (E)this.mValues[paramInt];
  }
  
  public int indexOfKey(int paramInt)
  {
    if (!this.mGarbage) {}
    for (;;)
    {
      return ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramInt);
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
  
  public int keyAt(int paramInt)
  {
    if (!this.mGarbage) {}
    for (;;)
    {
      return this.mKeys[paramInt];
      gc();
    }
  }
  
  public void put(int paramInt, E paramE)
  {
    int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramInt);
    if (i < 0)
    {
      i ^= 0xFFFFFFFF;
      if (i < this.mSize) {
        break label90;
      }
      if (this.mGarbage) {
        break label117;
      }
      label36:
      if (this.mSize >= this.mKeys.length) {
        break label151;
      }
      label48:
      if (this.mSize - i != 0) {
        break label222;
      }
    }
    for (;;)
    {
      this.mKeys[i] = paramInt;
      this.mValues[i] = paramE;
      this.mSize += 1;
      return;
      this.mValues[i] = paramE;
      return;
      label90:
      if (this.mValues[i] != DELETED) {
        break;
      }
      this.mKeys[i] = paramInt;
      this.mValues[i] = paramE;
      return;
      label117:
      if (this.mSize < this.mKeys.length) {
        break label36;
      }
      gc();
      i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramInt) ^ 0xFFFFFFFF;
      break label36;
      label151:
      int j = ContainerHelpers.idealIntArraySize(this.mSize + 1);
      int[] arrayOfInt = new int[j];
      Object[] arrayOfObject = new Object[j];
      System.arraycopy(this.mKeys, 0, arrayOfInt, 0, this.mKeys.length);
      System.arraycopy(this.mValues, 0, arrayOfObject, 0, this.mValues.length);
      this.mKeys = arrayOfInt;
      this.mValues = arrayOfObject;
      break label48;
      label222:
      System.arraycopy(this.mKeys, i, this.mKeys, i + 1, this.mSize - i);
      System.arraycopy(this.mValues, i, this.mValues, i + 1, this.mSize - i);
    }
  }
  
  public void remove(int paramInt)
  {
    delete(paramInt);
  }
  
  public void removeAt(int paramInt)
  {
    if (this.mValues[paramInt] == DELETED) {
      return;
    }
    this.mValues[paramInt] = DELETED;
    this.mGarbage = true;
  }
  
  public void removeAtRange(int paramInt1, int paramInt2)
  {
    paramInt2 = Math.min(this.mSize, paramInt1 + paramInt2);
    for (;;)
    {
      if (paramInt1 >= paramInt2) {
        return;
      }
      removeAt(paramInt1);
      paramInt1 += 1;
    }
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/util/SparseArrayCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */