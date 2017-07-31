package android.database.sqlite;

import android.content.res.Resources;
import android.os.StatFs;
import android.os.SystemProperties;

public final class SQLiteGlobal
{
  private static final String TAG = "SQLiteGlobal";
  private static int sDefaultPageSize;
  private static final Object sLock = new Object();
  
  public static String getDefaultJournalMode()
  {
    return SystemProperties.get("debug.sqlite.journalmode", Resources.getSystem().getString(17039437));
  }
  
  public static int getDefaultPageSize()
  {
    synchronized (sLock)
    {
      if (sDefaultPageSize == 0) {
        sDefaultPageSize = new StatFs("/data").getBlockSize();
      }
      int i = SystemProperties.getInt("debug.sqlite.pagesize", sDefaultPageSize);
      return i;
    }
  }
  
  public static String getDefaultSyncMode()
  {
    return SystemProperties.get("debug.sqlite.syncmode", Resources.getSystem().getString(17039438));
  }
  
  public static int getJournalSizeLimit()
  {
    return SystemProperties.getInt("debug.sqlite.journalsizelimit", Resources.getSystem().getInteger(17694844));
  }
  
  public static int getWALAutoCheckpoint()
  {
    return Math.max(1, SystemProperties.getInt("debug.sqlite.wal.autocheckpoint", Resources.getSystem().getInteger(17694845)));
  }
  
  public static int getWALConnectionPoolSize()
  {
    return Math.max(2, SystemProperties.getInt("debug.sqlite.wal.poolsize", Resources.getSystem().getInteger(17694843)));
  }
  
  public static String getWALSyncMode()
  {
    return SystemProperties.get("debug.sqlite.wal.syncmode", Resources.getSystem().getString(17039439));
  }
  
  private static native int nativeReleaseMemory();
  
  public static int releaseMemory()
  {
    return nativeReleaseMemory();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/database/sqlite/SQLiteGlobal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */