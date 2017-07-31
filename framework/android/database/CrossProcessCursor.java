package android.database;

public abstract interface CrossProcessCursor
  extends Cursor
{
  public abstract void fillWindow(int paramInt, CursorWindow paramCursorWindow);
  
  public abstract CursorWindow getWindow();
  
  public abstract boolean onMove(int paramInt1, int paramInt2);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/database/CrossProcessCursor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */