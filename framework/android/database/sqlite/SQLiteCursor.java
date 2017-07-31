package android.database.sqlite;

import android.database.AbstractWindowedCursor;
import android.database.CursorWindow;
import android.database.DatabaseUtils;
import android.os.StrictMode;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;

public class SQLiteCursor
  extends AbstractWindowedCursor
{
  static final int NO_COUNT = -1;
  static final String TAG = "SQLiteCursor";
  private Map<String, Integer> mColumnNameMap;
  private final String[] mColumns;
  private int mCount = -1;
  private int mCursorWindowCapacity;
  private final SQLiteCursorDriver mDriver;
  private final String mEditTable;
  private final SQLiteQuery mQuery;
  private final Throwable mStackTrace;
  
  public SQLiteCursor(SQLiteCursorDriver paramSQLiteCursorDriver, String paramString, SQLiteQuery paramSQLiteQuery)
  {
    if (paramSQLiteQuery == null) {
      throw new IllegalArgumentException("query object cannot be null");
    }
    if (StrictMode.vmSqliteObjectLeaksEnabled()) {}
    for (this.mStackTrace = new DatabaseObjectNotClosedException().fillInStackTrace();; this.mStackTrace = null)
    {
      this.mDriver = paramSQLiteCursorDriver;
      this.mEditTable = paramString;
      this.mColumnNameMap = null;
      this.mQuery = paramSQLiteQuery;
      this.mColumns = paramSQLiteQuery.getColumnNames();
      return;
    }
  }
  
  @Deprecated
  public SQLiteCursor(SQLiteDatabase paramSQLiteDatabase, SQLiteCursorDriver paramSQLiteCursorDriver, String paramString, SQLiteQuery paramSQLiteQuery)
  {
    this(paramSQLiteCursorDriver, paramString, paramSQLiteQuery);
  }
  
  private void fillWindow(int paramInt)
  {
    clearOrCreateWindow(getDatabase().getPath());
    try
    {
      int i;
      if (this.mCount == -1)
      {
        i = DatabaseUtils.cursorPickFillWindowStartPosition(paramInt, 0);
        this.mCount = this.mQuery.fillWindow(this.mWindow, i, paramInt, true);
        this.mCursorWindowCapacity = this.mWindow.getNumRows();
        if (Log.isLoggable("SQLiteCursor", 3)) {
          Log.d("SQLiteCursor", "received count(*) from native_fill_window: " + this.mCount);
        }
      }
      else
      {
        i = DatabaseUtils.cursorPickFillWindowStartPosition(paramInt, this.mCursorWindowCapacity);
        this.mQuery.fillWindow(this.mWindow, i, paramInt, false);
        return;
      }
    }
    catch (RuntimeException localRuntimeException)
    {
      closeWindow();
      throw localRuntimeException;
    }
  }
  
  public void close()
  {
    super.close();
    try
    {
      this.mQuery.close();
      this.mDriver.cursorClosed();
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void deactivate()
  {
    super.deactivate();
    this.mDriver.cursorDeactivated();
  }
  
  protected void finalize()
  {
    try
    {
      if (this.mWindow != null)
      {
        if (this.mStackTrace != null)
        {
          String str = this.mQuery.getSql();
          int j = str.length();
          StringBuilder localStringBuilder = new StringBuilder().append("Finalizing a Cursor that has not been deactivated or closed. database = ").append(this.mQuery.getDatabase().getLabel()).append(", table = ").append(this.mEditTable).append(", query = ");
          int i = j;
          if (j > 1000) {
            i = 1000;
          }
          StrictMode.onSqliteObjectLeaked(str.substring(0, i), this.mStackTrace);
        }
        close();
      }
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public int getColumnIndex(String paramString)
  {
    if (this.mColumnNameMap == null)
    {
      localObject = this.mColumns;
      int j = localObject.length;
      HashMap localHashMap = new HashMap(j, 1.0F);
      i = 0;
      while (i < j)
      {
        localHashMap.put(localObject[i], Integer.valueOf(i));
        i += 1;
      }
      this.mColumnNameMap = localHashMap;
    }
    int i = paramString.lastIndexOf('.');
    Object localObject = paramString;
    if (i != -1)
    {
      localObject = new Exception();
      Log.e("SQLiteCursor", "requesting column name with table name -- " + paramString, (Throwable)localObject);
      localObject = paramString.substring(i + 1);
    }
    paramString = (Integer)this.mColumnNameMap.get(localObject);
    if (paramString != null) {
      return paramString.intValue();
    }
    return -1;
  }
  
  public String[] getColumnNames()
  {
    return this.mColumns;
  }
  
  public int getCount()
  {
    if (this.mCount == -1) {
      fillWindow(0);
    }
    return this.mCount;
  }
  
  public SQLiteDatabase getDatabase()
  {
    return this.mQuery.getDatabase();
  }
  
  public boolean onMove(int paramInt1, int paramInt2)
  {
    if ((this.mWindow == null) || (paramInt2 < this.mWindow.getStartPosition())) {}
    for (;;)
    {
      fillWindow(paramInt2);
      do
      {
        return true;
      } while (paramInt2 < this.mWindow.getStartPosition() + this.mWindow.getNumRows());
    }
  }
  
  /* Error */
  public boolean requery()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 243	android/database/sqlite/SQLiteCursor:isClosed	()Z
    //   4: ifeq +5 -> 9
    //   7: iconst_0
    //   8: ireturn
    //   9: aload_0
    //   10: monitorenter
    //   11: aload_0
    //   12: getfield 62	android/database/sqlite/SQLiteCursor:mQuery	Landroid/database/sqlite/SQLiteQuery;
    //   15: invokevirtual 169	android/database/sqlite/SQLiteQuery:getDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   18: invokevirtual 246	android/database/sqlite/SQLiteDatabase:isOpen	()Z
    //   21: istore_1
    //   22: iload_1
    //   23: ifne +7 -> 30
    //   26: aload_0
    //   27: monitorexit
    //   28: iconst_0
    //   29: ireturn
    //   30: aload_0
    //   31: getfield 103	android/database/sqlite/SQLiteCursor:mWindow	Landroid/database/CursorWindow;
    //   34: ifnull +10 -> 44
    //   37: aload_0
    //   38: getfield 103	android/database/sqlite/SQLiteCursor:mWindow	Landroid/database/CursorWindow;
    //   41: invokevirtual 249	android/database/CursorWindow:clear	()V
    //   44: aload_0
    //   45: iconst_m1
    //   46: putfield 252	android/database/sqlite/SQLiteCursor:mPos	I
    //   49: aload_0
    //   50: iconst_m1
    //   51: putfield 32	android/database/sqlite/SQLiteCursor:mCount	I
    //   54: aload_0
    //   55: getfield 56	android/database/sqlite/SQLiteCursor:mDriver	Landroid/database/sqlite/SQLiteCursorDriver;
    //   58: aload_0
    //   59: invokeinterface 256 2 0
    //   64: aload_0
    //   65: monitorexit
    //   66: aload_0
    //   67: invokespecial 258	android/database/AbstractWindowedCursor:requery	()Z
    //   70: istore_1
    //   71: iload_1
    //   72: ireturn
    //   73: astore_2
    //   74: aload_0
    //   75: monitorexit
    //   76: aload_2
    //   77: athrow
    //   78: astore_2
    //   79: ldc 11
    //   81: new 122	java/lang/StringBuilder
    //   84: dup
    //   85: invokespecial 123	java/lang/StringBuilder:<init>	()V
    //   88: ldc_w 260
    //   91: invokevirtual 129	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   94: aload_2
    //   95: invokevirtual 263	java/lang/IllegalStateException:getMessage	()Ljava/lang/String;
    //   98: invokevirtual 129	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   101: invokevirtual 135	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   104: aload_2
    //   105: invokestatic 266	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   108: pop
    //   109: iconst_0
    //   110: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	111	0	this	SQLiteCursor
    //   21	51	1	bool	boolean
    //   73	4	2	localObject	Object
    //   78	27	2	localIllegalStateException	IllegalStateException
    // Exception table:
    //   from	to	target	type
    //   11	22	73	finally
    //   30	44	73	finally
    //   44	64	73	finally
    //   66	71	78	java/lang/IllegalStateException
  }
  
  public void setSelectionArguments(String[] paramArrayOfString)
  {
    this.mDriver.setBindArguments(paramArrayOfString);
  }
  
  public void setWindow(CursorWindow paramCursorWindow)
  {
    super.setWindow(paramCursorWindow);
    this.mCount = -1;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/database/sqlite/SQLiteCursor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */