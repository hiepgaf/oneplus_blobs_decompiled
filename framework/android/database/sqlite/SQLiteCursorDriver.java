package android.database.sqlite;

import android.database.Cursor;

public abstract interface SQLiteCursorDriver
{
  public abstract void cursorClosed();
  
  public abstract void cursorDeactivated();
  
  public abstract void cursorRequeried(Cursor paramCursor);
  
  public abstract Cursor query(SQLiteDatabase.CursorFactory paramCursorFactory, String[] paramArrayOfString);
  
  public abstract void setBindArguments(String[] paramArrayOfString);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/database/sqlite/SQLiteCursorDriver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */