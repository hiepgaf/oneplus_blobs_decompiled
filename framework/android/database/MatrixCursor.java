package android.database;

import java.util.ArrayList;
import java.util.Iterator;

public class MatrixCursor
  extends AbstractCursor
{
  private final int columnCount;
  private final String[] columnNames;
  private Object[] data;
  private int rowCount = 0;
  
  public MatrixCursor(String[] paramArrayOfString)
  {
    this(paramArrayOfString, 16);
  }
  
  public MatrixCursor(String[] paramArrayOfString, int paramInt)
  {
    this.columnNames = paramArrayOfString;
    this.columnCount = paramArrayOfString.length;
    int i = paramInt;
    if (paramInt < 1) {
      i = 1;
    }
    this.data = new Object[this.columnCount * i];
  }
  
  private void addRow(ArrayList<?> paramArrayList, int paramInt)
  {
    int j = paramArrayList.size();
    if (j != this.columnCount) {
      throw new IllegalArgumentException("columnNames.length = " + this.columnCount + ", columnValues.size() = " + j);
    }
    this.rowCount += 1;
    Object[] arrayOfObject = this.data;
    int i = 0;
    while (i < j)
    {
      arrayOfObject[(paramInt + i)] = paramArrayList.get(i);
      i += 1;
    }
  }
  
  private void ensureCapacity(int paramInt)
  {
    if (paramInt > this.data.length)
    {
      Object[] arrayOfObject = this.data;
      int j = this.data.length * 2;
      int i = j;
      if (j < paramInt) {
        i = paramInt;
      }
      this.data = new Object[i];
      System.arraycopy(arrayOfObject, 0, this.data, 0, arrayOfObject.length);
    }
  }
  
  private Object get(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.columnCount)) {
      throw new CursorIndexOutOfBoundsException("Requested column: " + paramInt + ", # of columns: " + this.columnCount);
    }
    if (this.mPos < 0) {
      throw new CursorIndexOutOfBoundsException("Before first row.");
    }
    if (this.mPos >= this.rowCount) {
      throw new CursorIndexOutOfBoundsException("After last row.");
    }
    return this.data[(this.mPos * this.columnCount + paramInt)];
  }
  
  public void addRow(Iterable<?> paramIterable)
  {
    int i = this.rowCount * this.columnCount;
    int j = i + this.columnCount;
    ensureCapacity(j);
    if ((paramIterable instanceof ArrayList))
    {
      addRow((ArrayList)paramIterable, i);
      return;
    }
    Object[] arrayOfObject = this.data;
    paramIterable = paramIterable.iterator();
    while (paramIterable.hasNext())
    {
      Object localObject = paramIterable.next();
      if (i == j) {
        throw new IllegalArgumentException("columnValues.size() > columnNames.length");
      }
      arrayOfObject[i] = localObject;
      i += 1;
    }
    if (i != j) {
      throw new IllegalArgumentException("columnValues.size() < columnNames.length");
    }
    this.rowCount += 1;
  }
  
  public void addRow(Object[] paramArrayOfObject)
  {
    if (paramArrayOfObject.length != this.columnCount) {
      throw new IllegalArgumentException("columnNames.length = " + this.columnCount + ", columnValues.length = " + paramArrayOfObject.length);
    }
    int i = this.rowCount;
    this.rowCount = (i + 1);
    i *= this.columnCount;
    ensureCapacity(this.columnCount + i);
    System.arraycopy(paramArrayOfObject, 0, this.data, i, this.columnCount);
  }
  
  public byte[] getBlob(int paramInt)
  {
    return (byte[])get(paramInt);
  }
  
  public String[] getColumnNames()
  {
    return this.columnNames;
  }
  
  public int getCount()
  {
    return this.rowCount;
  }
  
  public double getDouble(int paramInt)
  {
    Object localObject = get(paramInt);
    if (localObject == null) {
      return 0.0D;
    }
    if ((localObject instanceof Number)) {
      return ((Number)localObject).doubleValue();
    }
    return Double.parseDouble(localObject.toString());
  }
  
  public float getFloat(int paramInt)
  {
    Object localObject = get(paramInt);
    if (localObject == null) {
      return 0.0F;
    }
    if ((localObject instanceof Number)) {
      return ((Number)localObject).floatValue();
    }
    return Float.parseFloat(localObject.toString());
  }
  
  public int getInt(int paramInt)
  {
    Object localObject = get(paramInt);
    if (localObject == null) {
      return 0;
    }
    if ((localObject instanceof Number)) {
      return ((Number)localObject).intValue();
    }
    return Integer.parseInt(localObject.toString());
  }
  
  public long getLong(int paramInt)
  {
    Object localObject = get(paramInt);
    if (localObject == null) {
      return 0L;
    }
    if ((localObject instanceof Number)) {
      return ((Number)localObject).longValue();
    }
    return Long.parseLong(localObject.toString());
  }
  
  public short getShort(int paramInt)
  {
    Object localObject = get(paramInt);
    if (localObject == null) {
      return 0;
    }
    if ((localObject instanceof Number)) {
      return ((Number)localObject).shortValue();
    }
    return Short.parseShort(localObject.toString());
  }
  
  public String getString(int paramInt)
  {
    Object localObject = get(paramInt);
    if (localObject == null) {
      return null;
    }
    return localObject.toString();
  }
  
  public int getType(int paramInt)
  {
    return DatabaseUtils.getTypeOfObject(get(paramInt));
  }
  
  public boolean isNull(int paramInt)
  {
    return get(paramInt) == null;
  }
  
  public RowBuilder newRow()
  {
    int i = this.rowCount;
    this.rowCount = (i + 1);
    ensureCapacity(this.rowCount * this.columnCount);
    return new RowBuilder(i);
  }
  
  public class RowBuilder
  {
    private final int endIndex;
    private int index;
    private final int row;
    
    RowBuilder(int paramInt)
    {
      this.row = paramInt;
      this.index = (MatrixCursor.-get0(MatrixCursor.this) * paramInt);
      this.endIndex = (this.index + MatrixCursor.-get0(MatrixCursor.this));
    }
    
    public RowBuilder add(Object paramObject)
    {
      if (this.index == this.endIndex) {
        throw new CursorIndexOutOfBoundsException("No more columns left.");
      }
      Object[] arrayOfObject = MatrixCursor.-get2(MatrixCursor.this);
      int i = this.index;
      this.index = (i + 1);
      arrayOfObject[i] = paramObject;
      return this;
    }
    
    public RowBuilder add(String paramString, Object paramObject)
    {
      int i = 0;
      while (i < MatrixCursor.-get1(MatrixCursor.this).length)
      {
        if (paramString.equals(MatrixCursor.-get1(MatrixCursor.this)[i])) {
          MatrixCursor.-get2(MatrixCursor.this)[(this.row * MatrixCursor.-get0(MatrixCursor.this) + i)] = paramObject;
        }
        i += 1;
      }
      return this;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/database/MatrixCursor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */