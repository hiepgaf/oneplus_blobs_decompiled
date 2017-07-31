package android.database.sqlite;

import android.os.Build;
import android.os.SystemProperties;
import android.util.Log;
import android.util.Printer;
import java.util.ArrayList;

public final class SQLiteDebug
{
  public static final boolean DEBUG_LOG_SLOW_QUERIES = Build.IS_DEBUGGABLE;
  public static final boolean DEBUG_SQL_LOG = Log.isLoggable("SQLiteLog", 2);
  public static final boolean DEBUG_SQL_STATEMENTS = Log.isLoggable("SQLiteStatements", 2);
  public static final boolean DEBUG_SQL_TIME = Log.isLoggable("SQLiteTime", 2);
  
  public static void dump(Printer paramPrinter, String[] paramArrayOfString)
  {
    boolean bool = false;
    int i = 0;
    int j = paramArrayOfString.length;
    while (i < j)
    {
      if (paramArrayOfString[i].equals("-v")) {
        bool = true;
      }
      i += 1;
    }
    SQLiteDatabase.dumpAll(paramPrinter, bool);
  }
  
  public static PagerStats getDatabaseInfo()
  {
    PagerStats localPagerStats = new PagerStats();
    nativeGetPagerStats(localPagerStats);
    localPagerStats.dbStats = SQLiteDatabase.getDbStats();
    return localPagerStats;
  }
  
  private static native void nativeGetPagerStats(PagerStats paramPagerStats);
  
  public static final boolean shouldLogSlowQuery(long paramLong)
  {
    boolean bool2 = false;
    int i = SystemProperties.getInt("db.log.slow_query_threshold", -1);
    boolean bool1 = bool2;
    if (i >= 0)
    {
      bool1 = bool2;
      if (paramLong >= i) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public static class DbStats
  {
    public String cache;
    public String dbName;
    public long dbSize;
    public int lookaside;
    public long pageSize;
    
    public DbStats(String paramString, long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.dbName = paramString;
      this.pageSize = (paramLong2 / 1024L);
      this.dbSize = (paramLong1 * paramLong2 / 1024L);
      this.lookaside = paramInt1;
      this.cache = (paramInt2 + "/" + paramInt3 + "/" + paramInt4);
    }
  }
  
  public static class PagerStats
  {
    public ArrayList<SQLiteDebug.DbStats> dbStats;
    public int largestMemAlloc;
    public int memoryUsed;
    public int pageCacheOverflow;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/database/sqlite/SQLiteDebug.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */