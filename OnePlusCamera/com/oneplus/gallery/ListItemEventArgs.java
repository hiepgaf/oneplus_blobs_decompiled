package com.oneplus.gallery;

import com.oneplus.base.EventArgs;

public class ListItemEventArgs<T>
  extends EventArgs
{
  private final int m_Index;
  private final T m_Item;
  
  public ListItemEventArgs(int paramInt, T paramT)
  {
    this.m_Index = paramInt;
    this.m_Item = paramT;
  }
  
  public final int getIndex()
  {
    return this.m_Index;
  }
  
  public final T getItem()
  {
    return (T)this.m_Item;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery/ListItemEventArgs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */