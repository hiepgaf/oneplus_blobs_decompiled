package com.oneplus.media;

public class LookupTable
{
  private final int[] m_Table;
  
  public LookupTable(int paramInt)
  {
    this.m_Table = new int[paramInt];
    int i = 0;
    while (i < paramInt)
    {
      this.m_Table[i] = i;
      i += 1;
    }
  }
  
  public LookupTable(LookupTable paramLookupTable)
  {
    this.m_Table = new int[paramLookupTable.size()];
    paramLookupTable = paramLookupTable.m_Table;
    int i = 0;
    while (i < this.m_Table.length)
    {
      this.m_Table[i] = paramLookupTable[i];
      i += 1;
    }
  }
  
  public int[] array()
  {
    return this.m_Table;
  }
  
  public LookupTable concat(LookupTable paramLookupTable)
  {
    if ((paramLookupTable == null) || (this.m_Table.length != paramLookupTable.size())) {
      return this;
    }
    paramLookupTable = paramLookupTable.m_Table;
    int j = 0;
    if (j < this.m_Table.length)
    {
      int k = this.m_Table[j];
      int i;
      if (k < 0)
      {
        i = 0;
        label49:
        k = paramLookupTable[i];
        if (k >= 0) {
          break label95;
        }
        i = 0;
      }
      for (;;)
      {
        this.m_Table[j] = i;
        j += 1;
        break;
        i = k;
        if (k <= paramLookupTable.length - 1) {
          break label49;
        }
        i = paramLookupTable.length - 1;
        break label49;
        label95:
        i = k;
        if (k > this.m_Table.length - 1) {
          i = this.m_Table.length - 1;
        }
      }
    }
    return this;
  }
  
  public boolean isIdentity()
  {
    int i = 0;
    while (i < this.m_Table.length)
    {
      if (i != this.m_Table[i]) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  public void reset()
  {
    int i = 0;
    while (i < this.m_Table.length)
    {
      this.m_Table[i] = i;
      i += 1;
    }
  }
  
  public int size()
  {
    return this.m_Table.length;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/media/LookupTable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */