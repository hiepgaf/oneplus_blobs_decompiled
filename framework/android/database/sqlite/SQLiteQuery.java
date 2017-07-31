package android.database.sqlite;

import android.os.CancellationSignal;

public final class SQLiteQuery
  extends SQLiteProgram
{
  private static final String TAG = "SQLiteQuery";
  private final CancellationSignal mCancellationSignal;
  
  SQLiteQuery(SQLiteDatabase paramSQLiteDatabase, String paramString, CancellationSignal paramCancellationSignal)
  {
    super(paramSQLiteDatabase, paramString, null, paramCancellationSignal);
    this.mCancellationSignal = paramCancellationSignal;
  }
  
  /* Error */
  int fillWindow(android.database.CursorWindow paramCursorWindow, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 28	android/database/sqlite/SQLiteQuery:acquireReference	()V
    //   4: aload_1
    //   5: invokevirtual 31	android/database/CursorWindow:acquireReference	()V
    //   8: aload_0
    //   9: invokevirtual 35	android/database/sqlite/SQLiteQuery:getSession	()Landroid/database/sqlite/SQLiteSession;
    //   12: aload_0
    //   13: invokevirtual 39	android/database/sqlite/SQLiteQuery:getSql	()Ljava/lang/String;
    //   16: aload_0
    //   17: invokevirtual 43	android/database/sqlite/SQLiteQuery:getBindArgs	()[Ljava/lang/Object;
    //   20: aload_1
    //   21: iload_2
    //   22: iload_3
    //   23: iload 4
    //   25: aload_0
    //   26: invokevirtual 47	android/database/sqlite/SQLiteQuery:getConnectionFlags	()I
    //   29: aload_0
    //   30: getfield 17	android/database/sqlite/SQLiteQuery:mCancellationSignal	Landroid/os/CancellationSignal;
    //   33: invokevirtual 53	android/database/sqlite/SQLiteSession:executeForCursorWindow	(Ljava/lang/String;[Ljava/lang/Object;Landroid/database/CursorWindow;IIZILandroid/os/CancellationSignal;)I
    //   36: istore_2
    //   37: aload_1
    //   38: invokevirtual 56	android/database/CursorWindow:releaseReference	()V
    //   41: aload_0
    //   42: invokevirtual 57	android/database/sqlite/SQLiteQuery:releaseReference	()V
    //   45: iload_2
    //   46: ireturn
    //   47: astore 5
    //   49: ldc 8
    //   51: new 59	java/lang/StringBuilder
    //   54: dup
    //   55: invokespecial 61	java/lang/StringBuilder:<init>	()V
    //   58: ldc 63
    //   60: invokevirtual 67	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   63: aload 5
    //   65: invokevirtual 70	android/database/sqlite/SQLiteException:getMessage	()Ljava/lang/String;
    //   68: invokevirtual 67	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   71: ldc 72
    //   73: invokevirtual 67	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   76: aload_0
    //   77: invokevirtual 39	android/database/sqlite/SQLiteQuery:getSql	()Ljava/lang/String;
    //   80: invokevirtual 67	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   83: invokevirtual 75	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   86: invokestatic 81	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   89: pop
    //   90: aload 5
    //   92: athrow
    //   93: astore 5
    //   95: aload_1
    //   96: invokevirtual 56	android/database/CursorWindow:releaseReference	()V
    //   99: aload 5
    //   101: athrow
    //   102: astore_1
    //   103: aload_0
    //   104: invokevirtual 57	android/database/sqlite/SQLiteQuery:releaseReference	()V
    //   107: aload_1
    //   108: athrow
    //   109: astore 5
    //   111: aload_0
    //   112: invokevirtual 84	android/database/sqlite/SQLiteQuery:onCorruption	()V
    //   115: aload 5
    //   117: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	118	0	this	SQLiteQuery
    //   0	118	1	paramCursorWindow	android.database.CursorWindow
    //   0	118	2	paramInt1	int
    //   0	118	3	paramInt2	int
    //   0	118	4	paramBoolean	boolean
    //   47	44	5	localSQLiteException	SQLiteException
    //   93	7	5	localObject	Object
    //   109	7	5	localSQLiteDatabaseCorruptException	SQLiteDatabaseCorruptException
    // Exception table:
    //   from	to	target	type
    //   8	37	47	android/database/sqlite/SQLiteException
    //   8	37	93	finally
    //   49	93	93	finally
    //   111	118	93	finally
    //   4	8	102	finally
    //   37	41	102	finally
    //   95	102	102	finally
    //   8	37	109	android/database/sqlite/SQLiteDatabaseCorruptException
  }
  
  public String toString()
  {
    return "SQLiteQuery: " + getSql();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/database/sqlite/SQLiteQuery.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */