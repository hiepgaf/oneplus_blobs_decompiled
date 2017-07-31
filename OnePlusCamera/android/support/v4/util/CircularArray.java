package android.support.v4.util;

public class CircularArray<E>
{
  private int mCapacityBitmask;
  private E[] mElements;
  private int mHead;
  private int mTail;
  
  public CircularArray()
  {
    this(8);
  }
  
  public CircularArray(int paramInt)
  {
    if (paramInt > 0) {
      if (Integer.bitCount(paramInt) != 1) {
        break label45;
      }
    }
    for (;;)
    {
      this.mCapacityBitmask = (paramInt - 1);
      this.mElements = ((Object[])new Object[paramInt]);
      return;
      throw new IllegalArgumentException("capacity must be positive");
      label45:
      paramInt = 1 << Integer.highestOneBit(paramInt) + 1;
    }
  }
  
  private void doubleCapacity()
  {
    int i = this.mElements.length;
    int j = i - this.mHead;
    int k = i << 1;
    if (k >= 0)
    {
      Object[] arrayOfObject = new Object[k];
      System.arraycopy(this.mElements, this.mHead, arrayOfObject, 0, j);
      System.arraycopy(this.mElements, 0, arrayOfObject, j, this.mHead);
      this.mElements = ((Object[])arrayOfObject);
      this.mHead = 0;
      this.mTail = i;
      this.mCapacityBitmask = (k - 1);
      return;
    }
    throw new RuntimeException("Too big");
  }
  
  public final void addFirst(E paramE)
  {
    this.mHead = (this.mHead - 1 & this.mCapacityBitmask);
    this.mElements[this.mHead] = paramE;
    if (this.mHead != this.mTail) {
      return;
    }
    doubleCapacity();
  }
  
  public final void addLast(E paramE)
  {
    this.mElements[this.mTail] = paramE;
    this.mTail = (this.mTail + 1 & this.mCapacityBitmask);
    if (this.mTail != this.mHead) {
      return;
    }
    doubleCapacity();
  }
  
  public final E get(int paramInt)
  {
    if (paramInt < 0) {}
    while (paramInt >= size()) {
      throw new ArrayIndexOutOfBoundsException();
    }
    int i = this.mHead;
    int j = this.mCapacityBitmask;
    return (E)this.mElements[(i + paramInt & j)];
  }
  
  public final E getFirst()
  {
    if (this.mHead != this.mTail) {
      return (E)this.mElements[this.mHead];
    }
    throw new ArrayIndexOutOfBoundsException();
  }
  
  public final E getLast()
  {
    if (this.mHead != this.mTail) {
      return (E)this.mElements[(this.mTail - 1 & this.mCapacityBitmask)];
    }
    throw new ArrayIndexOutOfBoundsException();
  }
  
  public final boolean isEmpty()
  {
    return this.mHead == this.mTail;
  }
  
  public final E popFirst()
  {
    if (this.mHead != this.mTail)
    {
      Object localObject = this.mElements[this.mHead];
      this.mElements[this.mHead] = null;
      this.mHead = (this.mHead + 1 & this.mCapacityBitmask);
      return (E)localObject;
    }
    throw new ArrayIndexOutOfBoundsException();
  }
  
  public final E popLast()
  {
    if (this.mHead != this.mTail)
    {
      int i = this.mTail - 1 & this.mCapacityBitmask;
      Object localObject = this.mElements[i];
      this.mElements[i] = null;
      this.mTail = i;
      return (E)localObject;
    }
    throw new ArrayIndexOutOfBoundsException();
  }
  
  public final int size()
  {
    return this.mTail - this.mHead & this.mCapacityBitmask;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/util/CircularArray.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */