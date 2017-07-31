package com.oneplus.gallery2;

public class ListMoveEventArgs
  extends ListChangeEventArgs
{
  private volatile int m_OldEndIndex;
  private volatile int m_OldStartIndex;
  
  public ListMoveEventArgs(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super(paramInt3, paramInt4);
    this.m_OldStartIndex = paramInt1;
    this.m_OldEndIndex = paramInt2;
  }
  
  public final int getOldEndIndex()
  {
    return this.m_OldEndIndex;
  }
  
  public final int getOldStartIndex()
  {
    return this.m_OldStartIndex;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/ListMoveEventArgs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */