package android.database;

public class MergeCursor
  extends AbstractCursor
{
  private Cursor mCursor;
  private Cursor[] mCursors;
  private DataSetObserver mObserver = new DataSetObserver()
  {
    public void onChanged()
    {
      MergeCursor.this.mPos = -1;
    }
    
    public void onInvalidated()
    {
      MergeCursor.this.mPos = -1;
    }
  };
  
  public MergeCursor(Cursor[] paramArrayOfCursor)
  {
    this.mCursors = paramArrayOfCursor;
    this.mCursor = paramArrayOfCursor[0];
    int i = 0;
    if (i < this.mCursors.length)
    {
      if (this.mCursors[i] == null) {}
      for (;;)
      {
        i += 1;
        break;
        this.mCursors[i].registerDataSetObserver(this.mObserver);
      }
    }
  }
  
  public void close()
  {
    int j = this.mCursors.length;
    int i = 0;
    if (i < j)
    {
      if (this.mCursors[i] == null) {}
      for (;;)
      {
        i += 1;
        break;
        this.mCursors[i].close();
      }
    }
    super.close();
  }
  
  public void deactivate()
  {
    int j = this.mCursors.length;
    int i = 0;
    while (i < j)
    {
      if (this.mCursors[i] != null) {
        this.mCursors[i].deactivate();
      }
      i += 1;
    }
    super.deactivate();
  }
  
  public byte[] getBlob(int paramInt)
  {
    return this.mCursor.getBlob(paramInt);
  }
  
  public String[] getColumnNames()
  {
    if (this.mCursor != null) {
      return this.mCursor.getColumnNames();
    }
    return new String[0];
  }
  
  public int getCount()
  {
    int j = 0;
    int m = this.mCursors.length;
    int i = 0;
    while (i < m)
    {
      int k = j;
      if (this.mCursors[i] != null) {
        k = j + this.mCursors[i].getCount();
      }
      i += 1;
      j = k;
    }
    return j;
  }
  
  public double getDouble(int paramInt)
  {
    return this.mCursor.getDouble(paramInt);
  }
  
  public float getFloat(int paramInt)
  {
    return this.mCursor.getFloat(paramInt);
  }
  
  public int getInt(int paramInt)
  {
    return this.mCursor.getInt(paramInt);
  }
  
  public long getLong(int paramInt)
  {
    return this.mCursor.getLong(paramInt);
  }
  
  public short getShort(int paramInt)
  {
    return this.mCursor.getShort(paramInt);
  }
  
  public String getString(int paramInt)
  {
    return this.mCursor.getString(paramInt);
  }
  
  public int getType(int paramInt)
  {
    return this.mCursor.getType(paramInt);
  }
  
  public boolean isNull(int paramInt)
  {
    return this.mCursor.isNull(paramInt);
  }
  
  public boolean onMove(int paramInt1, int paramInt2)
  {
    this.mCursor = null;
    int i = 0;
    int j = this.mCursors.length;
    paramInt1 = 0;
    if (paramInt1 < j) {
      if (this.mCursors[paramInt1] != null) {}
    }
    for (;;)
    {
      paramInt1 += 1;
      break;
      if (paramInt2 < this.mCursors[paramInt1].getCount() + i)
      {
        this.mCursor = this.mCursors[paramInt1];
        if (this.mCursor == null) {
          break label102;
        }
        return this.mCursor.moveToPosition(paramInt2 - i);
      }
      i += this.mCursors[paramInt1].getCount();
    }
    label102:
    return false;
  }
  
  public void registerContentObserver(ContentObserver paramContentObserver)
  {
    int j = this.mCursors.length;
    int i = 0;
    while (i < j)
    {
      if (this.mCursors[i] != null) {
        this.mCursors[i].registerContentObserver(paramContentObserver);
      }
      i += 1;
    }
  }
  
  public void registerDataSetObserver(DataSetObserver paramDataSetObserver)
  {
    int j = this.mCursors.length;
    int i = 0;
    while (i < j)
    {
      if (this.mCursors[i] != null) {
        this.mCursors[i].registerDataSetObserver(paramDataSetObserver);
      }
      i += 1;
    }
  }
  
  public boolean requery()
  {
    int j = this.mCursors.length;
    int i = 0;
    if (i < j)
    {
      if (this.mCursors[i] == null) {}
      while (this.mCursors[i].requery())
      {
        i += 1;
        break;
      }
      return false;
    }
    return true;
  }
  
  public void unregisterContentObserver(ContentObserver paramContentObserver)
  {
    int j = this.mCursors.length;
    int i = 0;
    while (i < j)
    {
      if (this.mCursors[i] != null) {
        this.mCursors[i].unregisterContentObserver(paramContentObserver);
      }
      i += 1;
    }
  }
  
  public void unregisterDataSetObserver(DataSetObserver paramDataSetObserver)
  {
    int j = this.mCursors.length;
    int i = 0;
    while (i < j)
    {
      if (this.mCursors[i] != null) {
        this.mCursors[i].unregisterDataSetObserver(paramDataSetObserver);
      }
      i += 1;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/database/MergeCursor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */