package android.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.util.Pair;
import java.io.File;
import java.util.Iterator;
import java.util.List;

public final class DefaultDatabaseErrorHandler
  implements DatabaseErrorHandler
{
  private static final String TAG = "DefaultDatabaseErrorHandler";
  
  private void deleteDatabaseFile(String paramString)
  {
    if ((paramString.equalsIgnoreCase(":memory:")) || (paramString.trim().length() == 0)) {
      return;
    }
    Log.e("DefaultDatabaseErrorHandler", "deleting the database file: " + paramString);
    try
    {
      SQLiteDatabase.deleteDatabase(new File(paramString));
      return;
    }
    catch (Exception paramString)
    {
      Log.w("DefaultDatabaseErrorHandler", "delete failed: " + paramString.getMessage());
    }
  }
  
  public void onCorruption(SQLiteDatabase paramSQLiteDatabase)
  {
    Log.e("DefaultDatabaseErrorHandler", "Corruption reported by sqlite on database: " + paramSQLiteDatabase.getPath());
    if (!paramSQLiteDatabase.isOpen())
    {
      deleteDatabaseFile(paramSQLiteDatabase.getPath());
      return;
    }
    Object localObject3 = null;
    localObject1 = null;
    try
    {
      List localList = paramSQLiteDatabase.getAttachedDbs();
      localObject1 = localList;
    }
    catch (SQLiteException localSQLiteException1)
    {
      return;
    }
    finally
    {
      if (localSQLiteException2 == null) {
        break label165;
      }
      paramSQLiteDatabase = localSQLiteException2.iterator();
      while (paramSQLiteDatabase.hasNext()) {
        deleteDatabaseFile((String)((Pair)paramSQLiteDatabase.next()).second);
      }
      deleteDatabaseFile(paramSQLiteDatabase.getPath());
    }
    localObject3 = localObject1;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/database/DefaultDatabaseErrorHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */