package android.database.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.DatabaseUtils;
import android.database.DefaultDatabaseErrorHandler;
import android.database.SQLException;
import android.os.CancellationSignal;
import android.os.Looper;
import android.text.TextUtils;
import android.util.EventLog;
import android.util.Log;
import android.util.Pair;
import android.util.Printer;
import dalvik.system.CloseGuard;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

public final class SQLiteDatabase
  extends SQLiteClosable
{
  public static final int CONFLICT_ABORT = 2;
  public static final int CONFLICT_FAIL = 3;
  public static final int CONFLICT_IGNORE = 4;
  public static final int CONFLICT_NONE = 0;
  public static final int CONFLICT_REPLACE = 5;
  public static final int CONFLICT_ROLLBACK = 1;
  private static final String[] CONFLICT_VALUES;
  public static final int CREATE_IF_NECESSARY = 268435456;
  public static final int ENABLE_WRITE_AHEAD_LOGGING = 536870912;
  private static final int EVENT_DB_CORRUPT = 75004;
  public static final int MAX_SQL_CACHE_SIZE = 100;
  public static final int NO_LOCALIZED_COLLATORS = 16;
  public static final int OPEN_READONLY = 1;
  public static final int OPEN_READWRITE = 0;
  private static final int OPEN_READ_MASK = 1;
  public static final int SQLITE_MAX_LIKE_PATTERN_LENGTH = 50000;
  private static final String TAG = "SQLiteDatabase";
  private static WeakHashMap<SQLiteDatabase, Object> sActiveDatabases;
  private static boolean useWALMode;
  private final CloseGuard mCloseGuardLocked = CloseGuard.get();
  private final SQLiteDatabaseConfiguration mConfigurationLocked;
  private SQLiteConnectionPool mConnectionPoolLocked;
  private final CursorFactory mCursorFactory;
  private final DatabaseErrorHandler mErrorHandler;
  private boolean mHasAttachedDbsLocked;
  private final Object mLock = new Object();
  private final ThreadLocal<SQLiteSession> mThreadSession = new ThreadLocal()
  {
    protected SQLiteSession initialValue()
    {
      return SQLiteDatabase.this.createSession();
    }
  };
  
  static
  {
    if (SQLiteDatabase.class.desiredAssertionStatus()) {}
    for (boolean bool = false;; bool = true)
    {
      -assertionsDisabled = bool;
      sActiveDatabases = new WeakHashMap();
      CONFLICT_VALUES = new String[] { "", " OR ROLLBACK ", " OR ABORT ", " OR FAIL ", " OR IGNORE ", " OR REPLACE " };
      useWALMode = false;
      return;
    }
  }
  
  private SQLiteDatabase(String paramString, int paramInt, CursorFactory paramCursorFactory, DatabaseErrorHandler paramDatabaseErrorHandler)
  {
    this.mCursorFactory = paramCursorFactory;
    if (paramDatabaseErrorHandler != null) {}
    for (;;)
    {
      this.mErrorHandler = paramDatabaseErrorHandler;
      this.mConfigurationLocked = new SQLiteDatabaseConfiguration(paramString, paramInt);
      return;
      paramDatabaseErrorHandler = new DefaultDatabaseErrorHandler();
    }
  }
  
  /* Error */
  private void beginTransaction(SQLiteTransactionListener paramSQLiteTransactionListener, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 145	android/database/sqlite/SQLiteDatabase:acquireReference	()V
    //   4: aload_0
    //   5: invokevirtual 149	android/database/sqlite/SQLiteDatabase:getThreadSession	()Landroid/database/sqlite/SQLiteSession;
    //   8: astore 4
    //   10: iload_2
    //   11: ifeq +23 -> 34
    //   14: iconst_2
    //   15: istore_3
    //   16: aload 4
    //   18: iload_3
    //   19: aload_1
    //   20: aload_0
    //   21: iconst_0
    //   22: invokevirtual 153	android/database/sqlite/SQLiteDatabase:getThreadDefaultConnectionFlags	(Z)I
    //   25: aconst_null
    //   26: invokevirtual 158	android/database/sqlite/SQLiteSession:beginTransaction	(ILandroid/database/sqlite/SQLiteTransactionListener;ILandroid/os/CancellationSignal;)V
    //   29: aload_0
    //   30: invokevirtual 161	android/database/sqlite/SQLiteDatabase:releaseReference	()V
    //   33: return
    //   34: iconst_1
    //   35: istore_3
    //   36: goto -20 -> 16
    //   39: astore_1
    //   40: aload_0
    //   41: invokevirtual 161	android/database/sqlite/SQLiteDatabase:releaseReference	()V
    //   44: aload_1
    //   45: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	46	0	this	SQLiteDatabase
    //   0	46	1	paramSQLiteTransactionListener	SQLiteTransactionListener
    //   0	46	2	paramBoolean	boolean
    //   15	21	3	i	int
    //   8	9	4	localSQLiteSession	SQLiteSession
    // Exception table:
    //   from	to	target	type
    //   4	10	39	finally
    //   16	29	39	finally
  }
  
  private void collectDbStats(ArrayList<SQLiteDebug.DbStats> paramArrayList)
  {
    synchronized (this.mLock)
    {
      if (this.mConnectionPoolLocked != null) {
        this.mConnectionPoolLocked.collectDbStats(paramArrayList);
      }
      return;
    }
  }
  
  public static SQLiteDatabase create(CursorFactory paramCursorFactory)
  {
    return openDatabase(":memory:", paramCursorFactory, 268435456);
  }
  
  public static boolean deleteDatabase(File paramFile)
  {
    if (paramFile == null) {
      throw new IllegalArgumentException("file must not be null");
    }
    boolean bool1 = paramFile.delete() | new File(paramFile.getPath() + "-journal").delete() | new File(paramFile.getPath() + "-shm").delete() | new File(paramFile.getPath() + "-wal").delete();
    File localFile = paramFile.getParentFile();
    boolean bool2 = bool1;
    if (localFile != null)
    {
      paramFile = localFile.listFiles(new FileFilter()
      {
        public boolean accept(File paramAnonymousFile)
        {
          return paramAnonymousFile.getName().startsWith(this.val$prefix);
        }
      });
      bool2 = bool1;
      if (paramFile != null)
      {
        int i = 0;
        int j = paramFile.length;
        for (;;)
        {
          bool2 = bool1;
          if (i >= j) {
            break;
          }
          bool1 |= paramFile[i].delete();
          i += 1;
        }
      }
    }
    return bool2;
  }
  
  private void dispose(boolean paramBoolean)
  {
    SQLiteConnectionPool localSQLiteConnectionPool;
    synchronized (this.mLock)
    {
      if (this.mCloseGuardLocked != null)
      {
        if (paramBoolean) {
          this.mCloseGuardLocked.warnIfOpen();
        }
        this.mCloseGuardLocked.close();
      }
      localSQLiteConnectionPool = this.mConnectionPoolLocked;
      this.mConnectionPoolLocked = null;
      if (paramBoolean) {}
    }
    synchronized (sActiveDatabases)
    {
      sActiveDatabases.remove(this);
      if (localSQLiteConnectionPool != null) {
        localSQLiteConnectionPool.close();
      }
      return;
      localObject2 = finally;
      throw ((Throwable)localObject2);
    }
  }
  
  private void dump(Printer paramPrinter, boolean paramBoolean)
  {
    synchronized (this.mLock)
    {
      if (this.mConnectionPoolLocked != null)
      {
        paramPrinter.println("");
        this.mConnectionPoolLocked.dump(paramPrinter, paramBoolean);
      }
      return;
    }
  }
  
  static void dumpAll(Printer paramPrinter, boolean paramBoolean)
  {
    Iterator localIterator = getActiveDatabases().iterator();
    while (localIterator.hasNext()) {
      ((SQLiteDatabase)localIterator.next()).dump(paramPrinter, paramBoolean);
    }
  }
  
  public static void enableActivityWALMode()
  {
    useWALMode = true;
  }
  
  /* Error */
  private int executeSql(String paramString, Object[] paramArrayOfObject)
    throws SQLException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 145	android/database/sqlite/SQLiteDatabase:acquireReference	()V
    //   4: aload_1
    //   5: invokestatic 282	android/database/DatabaseUtils:getSqlStatementType	(Ljava/lang/String;)I
    //   8: iconst_3
    //   9: if_icmpne +39 -> 48
    //   12: iconst_0
    //   13: istore_3
    //   14: aload_0
    //   15: getfield 118	android/database/sqlite/SQLiteDatabase:mLock	Ljava/lang/Object;
    //   18: astore 4
    //   20: aload 4
    //   22: monitorenter
    //   23: aload_0
    //   24: getfield 284	android/database/sqlite/SQLiteDatabase:mHasAttachedDbsLocked	Z
    //   27: ifne +10 -> 37
    //   30: aload_0
    //   31: iconst_1
    //   32: putfield 284	android/database/sqlite/SQLiteDatabase:mHasAttachedDbsLocked	Z
    //   35: iconst_1
    //   36: istore_3
    //   37: aload 4
    //   39: monitorexit
    //   40: iload_3
    //   41: ifeq +7 -> 48
    //   44: aload_0
    //   45: invokevirtual 287	android/database/sqlite/SQLiteDatabase:disableWriteAheadLogging	()V
    //   48: new 289	android/database/sqlite/SQLiteStatement
    //   51: dup
    //   52: aload_0
    //   53: aload_1
    //   54: aload_2
    //   55: invokespecial 292	android/database/sqlite/SQLiteStatement:<init>	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;[Ljava/lang/Object;)V
    //   58: astore_1
    //   59: aload_1
    //   60: invokevirtual 296	android/database/sqlite/SQLiteStatement:executeUpdateDelete	()I
    //   63: istore_3
    //   64: aload_1
    //   65: invokevirtual 297	android/database/sqlite/SQLiteStatement:close	()V
    //   68: aload_0
    //   69: invokevirtual 161	android/database/sqlite/SQLiteDatabase:releaseReference	()V
    //   72: iload_3
    //   73: ireturn
    //   74: astore_1
    //   75: aload 4
    //   77: monitorexit
    //   78: aload_1
    //   79: athrow
    //   80: astore_1
    //   81: aload_0
    //   82: invokevirtual 161	android/database/sqlite/SQLiteDatabase:releaseReference	()V
    //   85: aload_1
    //   86: athrow
    //   87: astore_2
    //   88: aload_1
    //   89: invokevirtual 297	android/database/sqlite/SQLiteStatement:close	()V
    //   92: aload_2
    //   93: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	94	0	this	SQLiteDatabase
    //   0	94	1	paramString	String
    //   0	94	2	paramArrayOfObject	Object[]
    //   13	60	3	i	int
    // Exception table:
    //   from	to	target	type
    //   23	35	74	finally
    //   4	12	80	finally
    //   14	23	80	finally
    //   37	40	80	finally
    //   44	48	80	finally
    //   48	59	80	finally
    //   64	68	80	finally
    //   75	80	80	finally
    //   88	94	80	finally
    //   59	64	87	finally
  }
  
  public static String findEditTable(String paramString)
  {
    if (!TextUtils.isEmpty(paramString))
    {
      int i = paramString.indexOf(' ');
      int j = paramString.indexOf(',');
      if ((i > 0) && ((i < j) || (j < 0))) {
        return paramString.substring(0, i);
      }
      if ((j > 0) && ((j < i) || (i < 0))) {
        return paramString.substring(0, j);
      }
      return paramString;
    }
    throw new IllegalStateException("Invalid tables");
  }
  
  private static ArrayList<SQLiteDatabase> getActiveDatabases()
  {
    ArrayList localArrayList = new ArrayList();
    synchronized (sActiveDatabases)
    {
      localArrayList.addAll(sActiveDatabases.keySet());
      return localArrayList;
    }
  }
  
  public static boolean getActivityWALMode()
  {
    return useWALMode;
  }
  
  static ArrayList<SQLiteDebug.DbStats> getDbStats()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = getActiveDatabases().iterator();
    while (localIterator.hasNext()) {
      ((SQLiteDatabase)localIterator.next()).collectDbStats(localArrayList);
    }
    return localArrayList;
  }
  
  private static boolean isMainThread()
  {
    boolean bool2 = false;
    Looper localLooper = Looper.myLooper();
    boolean bool1 = bool2;
    if (localLooper != null)
    {
      bool1 = bool2;
      if (localLooper == Looper.getMainLooper()) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private boolean isReadOnlyLocked()
  {
    return (this.mConfigurationLocked.openFlags & 0x1) == 1;
  }
  
  private void open()
  {
    try
    {
      openInner();
      return;
    }
    catch (SQLiteDatabaseCorruptException localSQLiteDatabaseCorruptException)
    {
      onCorruption();
      openInner();
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      Log.e("SQLiteDatabase", "Failed to open database '" + getLabel() + "'.", localSQLiteException);
      close();
      throw localSQLiteException;
    }
  }
  
  public static SQLiteDatabase openDatabase(String paramString, CursorFactory paramCursorFactory, int paramInt)
  {
    return openDatabase(paramString, paramCursorFactory, paramInt, null);
  }
  
  public static SQLiteDatabase openDatabase(String paramString, CursorFactory paramCursorFactory, int paramInt, DatabaseErrorHandler paramDatabaseErrorHandler)
  {
    int i = paramInt;
    if (getActivityWALMode()) {
      i = paramInt | 0x20000000;
    }
    paramString = new SQLiteDatabase(paramString, i, paramCursorFactory, paramDatabaseErrorHandler);
    paramString.open();
    return paramString;
  }
  
  private void openInner()
  {
    for (;;)
    {
      synchronized (this.mLock)
      {
        if (-assertionsDisabled) {
          break;
        }
        if (this.mConnectionPoolLocked == null)
        {
          i = 1;
          if (i != 0) {
            break;
          }
          throw new AssertionError();
        }
      }
      int i = 0;
    }
    this.mConnectionPoolLocked = SQLiteConnectionPool.open(this.mConfigurationLocked);
    this.mCloseGuardLocked.open("close");
    synchronized (sActiveDatabases)
    {
      sActiveDatabases.put(this, null);
      return;
    }
  }
  
  public static SQLiteDatabase openOrCreateDatabase(File paramFile, CursorFactory paramCursorFactory)
  {
    return openOrCreateDatabase(paramFile.getPath(), paramCursorFactory);
  }
  
  public static SQLiteDatabase openOrCreateDatabase(String paramString, CursorFactory paramCursorFactory)
  {
    return openDatabase(paramString, paramCursorFactory, 268435456, null);
  }
  
  public static SQLiteDatabase openOrCreateDatabase(String paramString, CursorFactory paramCursorFactory, DatabaseErrorHandler paramDatabaseErrorHandler)
  {
    return openDatabase(paramString, paramCursorFactory, 268435456, paramDatabaseErrorHandler);
  }
  
  public static int releaseMemory()
  {
    return SQLiteGlobal.releaseMemory();
  }
  
  private void throwIfNotOpenLocked()
  {
    if (this.mConnectionPoolLocked == null) {
      throw new IllegalStateException("The database '" + this.mConfigurationLocked.label + "' is not open.");
    }
  }
  
  private boolean yieldIfContendedHelper(boolean paramBoolean, long paramLong)
  {
    acquireReference();
    try
    {
      paramBoolean = getThreadSession().yieldTransaction(paramLong, paramBoolean, null);
      return paramBoolean;
    }
    finally
    {
      releaseReference();
    }
  }
  
  public void addCustomFunction(String arg1, int paramInt, CustomFunction paramCustomFunction)
  {
    paramCustomFunction = new SQLiteCustomFunction(???, paramInt, paramCustomFunction);
    synchronized (this.mLock)
    {
      throwIfNotOpenLocked();
      this.mConfigurationLocked.customFunctions.add(paramCustomFunction);
      try
      {
        this.mConnectionPoolLocked.reconfigure(this.mConfigurationLocked);
        return;
      }
      catch (RuntimeException localRuntimeException)
      {
        this.mConfigurationLocked.customFunctions.remove(paramCustomFunction);
        throw localRuntimeException;
      }
    }
  }
  
  public void beginTransaction()
  {
    beginTransaction(null, true);
  }
  
  public void beginTransactionNonExclusive()
  {
    beginTransaction(null, false);
  }
  
  public void beginTransactionWithListener(SQLiteTransactionListener paramSQLiteTransactionListener)
  {
    beginTransaction(paramSQLiteTransactionListener, true);
  }
  
  public void beginTransactionWithListenerNonExclusive(SQLiteTransactionListener paramSQLiteTransactionListener)
  {
    beginTransaction(paramSQLiteTransactionListener, false);
  }
  
  public SQLiteStatement compileStatement(String paramString)
    throws SQLException
  {
    acquireReference();
    try
    {
      paramString = new SQLiteStatement(this, paramString, null);
      return paramString;
    }
    finally
    {
      releaseReference();
    }
  }
  
  SQLiteSession createSession()
  {
    synchronized (this.mLock)
    {
      throwIfNotOpenLocked();
      SQLiteConnectionPool localSQLiteConnectionPool = this.mConnectionPoolLocked;
      return new SQLiteSession(localSQLiteConnectionPool);
    }
  }
  
  /* Error */
  public int delete(String paramString1, String paramString2, String[] paramArrayOfString)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 145	android/database/sqlite/SQLiteDatabase:acquireReference	()V
    //   4: new 195	java/lang/StringBuilder
    //   7: dup
    //   8: invokespecial 196	java/lang/StringBuilder:<init>	()V
    //   11: ldc_w 460
    //   14: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   17: aload_1
    //   18: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   21: astore 5
    //   23: aload_2
    //   24: invokestatic 306	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   27: ifne +60 -> 87
    //   30: new 195	java/lang/StringBuilder
    //   33: dup
    //   34: invokespecial 196	java/lang/StringBuilder:<init>	()V
    //   37: ldc_w 462
    //   40: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   43: aload_2
    //   44: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   47: invokevirtual 209	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   50: astore_1
    //   51: new 289	android/database/sqlite/SQLiteStatement
    //   54: dup
    //   55: aload_0
    //   56: aload 5
    //   58: aload_1
    //   59: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   62: invokevirtual 209	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   65: aload_3
    //   66: invokespecial 292	android/database/sqlite/SQLiteStatement:<init>	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;[Ljava/lang/Object;)V
    //   69: astore_1
    //   70: aload_1
    //   71: invokevirtual 296	android/database/sqlite/SQLiteStatement:executeUpdateDelete	()I
    //   74: istore 4
    //   76: aload_1
    //   77: invokevirtual 297	android/database/sqlite/SQLiteStatement:close	()V
    //   80: aload_0
    //   81: invokevirtual 161	android/database/sqlite/SQLiteDatabase:releaseReference	()V
    //   84: iload 4
    //   86: ireturn
    //   87: ldc 91
    //   89: astore_1
    //   90: goto -39 -> 51
    //   93: astore_2
    //   94: aload_1
    //   95: invokevirtual 297	android/database/sqlite/SQLiteStatement:close	()V
    //   98: aload_2
    //   99: athrow
    //   100: astore_1
    //   101: aload_0
    //   102: invokevirtual 161	android/database/sqlite/SQLiteDatabase:releaseReference	()V
    //   105: aload_1
    //   106: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	107	0	this	SQLiteDatabase
    //   0	107	1	paramString1	String
    //   0	107	2	paramString2	String
    //   0	107	3	paramArrayOfString	String[]
    //   74	11	4	i	int
    //   21	36	5	localStringBuilder	StringBuilder
    // Exception table:
    //   from	to	target	type
    //   70	76	93	finally
    //   4	51	100	finally
    //   51	70	100	finally
    //   76	80	100	finally
    //   94	100	100	finally
  }
  
  public void disableWriteAheadLogging()
  {
    synchronized (this.mLock)
    {
      throwIfNotOpenLocked();
      int i = this.mConfigurationLocked.openFlags;
      if ((i & 0x20000000) == 0) {
        return;
      }
      SQLiteDatabaseConfiguration localSQLiteDatabaseConfiguration1 = this.mConfigurationLocked;
      localSQLiteDatabaseConfiguration1.openFlags &= 0xDFFFFFFF;
      try
      {
        this.mConnectionPoolLocked.reconfigure(this.mConfigurationLocked);
        return;
      }
      catch (RuntimeException localRuntimeException)
      {
        SQLiteDatabaseConfiguration localSQLiteDatabaseConfiguration2 = this.mConfigurationLocked;
        localSQLiteDatabaseConfiguration2.openFlags |= 0x20000000;
        throw localRuntimeException;
      }
    }
  }
  
  public boolean enableWriteAheadLogging()
  {
    synchronized (this.mLock)
    {
      throwIfNotOpenLocked();
      int i = this.mConfigurationLocked.openFlags;
      if ((i & 0x20000000) != 0) {
        return true;
      }
      boolean bool = isReadOnlyLocked();
      if (bool) {
        return false;
      }
      if (this.mConfigurationLocked.isInMemoryDb())
      {
        Log.i("SQLiteDatabase", "can't enable WAL for memory databases.");
        return false;
      }
      if (this.mHasAttachedDbsLocked)
      {
        if (Log.isLoggable("SQLiteDatabase", 3)) {
          Log.d("SQLiteDatabase", "this database: " + this.mConfigurationLocked.label + " has attached databases. can't  enable WAL.");
        }
        return false;
      }
      SQLiteDatabaseConfiguration localSQLiteDatabaseConfiguration1 = this.mConfigurationLocked;
      localSQLiteDatabaseConfiguration1.openFlags |= 0x20000000;
      try
      {
        this.mConnectionPoolLocked.reconfigure(this.mConfigurationLocked);
        return true;
      }
      catch (RuntimeException localRuntimeException)
      {
        SQLiteDatabaseConfiguration localSQLiteDatabaseConfiguration2 = this.mConfigurationLocked;
        localSQLiteDatabaseConfiguration2.openFlags &= 0xDFFFFFFF;
        throw localRuntimeException;
      }
    }
  }
  
  public void endTransaction()
  {
    acquireReference();
    try
    {
      getThreadSession().endTransaction(null);
      return;
    }
    finally
    {
      releaseReference();
    }
  }
  
  public void execSQL(String paramString)
    throws SQLException
  {
    executeSql(paramString, null);
  }
  
  public void execSQL(String paramString, Object[] paramArrayOfObject)
    throws SQLException
  {
    if (paramArrayOfObject == null) {
      throw new IllegalArgumentException("Empty bindArgs");
    }
    executeSql(paramString, paramArrayOfObject);
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      dispose(true);
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public List<Pair<String, String>> getAttachedDbs()
  {
    ArrayList localArrayList = new ArrayList();
    synchronized (this.mLock)
    {
      Object localObject3 = this.mConnectionPoolLocked;
      if (localObject3 == null) {
        return null;
      }
      if (!this.mHasAttachedDbsLocked)
      {
        localArrayList.add(new Pair("main", this.mConfigurationLocked.path));
        return localArrayList;
      }
      acquireReference();
      ??? = null;
      try
      {
        localObject3 = rawQuery("pragma database_list;", null);
        for (;;)
        {
          ??? = localObject3;
          if (!((Cursor)localObject3).moveToNext()) {
            break;
          }
          ??? = localObject3;
          localArrayList.add(new Pair(((Cursor)localObject3).getString(1), ((Cursor)localObject3).getString(2)));
        }
        try
        {
          ((Cursor)???).close();
          throw ((Throwable)localObject4);
        }
        finally
        {
          releaseReference();
          throw ((Throwable)localObject2);
          localObject5 = finally;
          throw ((Throwable)localObject5);
        }
      }
      finally
      {
        if (??? == null) {}
      }
    }
    return localArrayList;
  }
  
  String getLabel()
  {
    synchronized (this.mLock)
    {
      String str = this.mConfigurationLocked.label;
      return str;
    }
  }
  
  public long getMaximumSize()
  {
    long l = DatabaseUtils.longForQuery(this, "PRAGMA max_page_count;", null);
    return getPageSize() * l;
  }
  
  public long getPageSize()
  {
    return DatabaseUtils.longForQuery(this, "PRAGMA page_size;", null);
  }
  
  public final String getPath()
  {
    synchronized (this.mLock)
    {
      String str = this.mConfigurationLocked.path;
      return str;
    }
  }
  
  @Deprecated
  public Map<String, String> getSyncedTables()
  {
    return new HashMap(0);
  }
  
  int getThreadDefaultConnectionFlags(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 1;; i = 2)
    {
      int j = i;
      if (isMainThread()) {
        j = i | 0x4;
      }
      return j;
    }
  }
  
  SQLiteSession getThreadSession()
  {
    return (SQLiteSession)this.mThreadSession.get();
  }
  
  public int getVersion()
  {
    return Long.valueOf(DatabaseUtils.longForQuery(this, "PRAGMA user_version;", null)).intValue();
  }
  
  public boolean inTransaction()
  {
    acquireReference();
    try
    {
      boolean bool = getThreadSession().hasTransaction();
      return bool;
    }
    finally
    {
      releaseReference();
    }
  }
  
  public long insert(String paramString1, String paramString2, ContentValues paramContentValues)
  {
    try
    {
      long l = insertWithOnConflict(paramString1, paramString2, paramContentValues, 0);
      return l;
    }
    catch (SQLException paramString1)
    {
      Log.e("SQLiteDatabase", "Error inserting " + paramContentValues, paramString1);
    }
    return -1L;
  }
  
  public long insertOrThrow(String paramString1, String paramString2, ContentValues paramContentValues)
    throws SQLException
  {
    return insertWithOnConflict(paramString1, paramString2, paramContentValues, 0);
  }
  
  public long insertWithOnConflict(String paramString1, String paramString2, ContentValues paramContentValues, int paramInt)
  {
    int i = 0;
    acquireReference();
    for (;;)
    {
      try
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("INSERT");
        localStringBuilder.append(CONFLICT_VALUES[paramInt]);
        localStringBuilder.append(" INTO ");
        localStringBuilder.append(paramString1);
        localStringBuilder.append('(');
        paramString1 = null;
        paramInt = i;
        if (paramContentValues != null)
        {
          paramInt = i;
          if (paramContentValues.size() > 0) {
            paramInt = paramContentValues.size();
          }
        }
        if (paramInt > 0)
        {
          paramString2 = new Object[paramInt];
          Iterator localIterator = paramContentValues.keySet().iterator();
          i = 0;
          if (localIterator.hasNext())
          {
            String str = (String)localIterator.next();
            if (i > 0)
            {
              paramString1 = ",";
              localStringBuilder.append(paramString1);
              localStringBuilder.append(str);
              paramString2[i] = paramContentValues.get(str);
              i += 1;
            }
          }
          else
          {
            localStringBuilder.append(')');
            localStringBuilder.append(" VALUES (");
            i = 0;
            break label290;
            localStringBuilder.append(paramString1);
            i += 1;
            break label290;
          }
        }
        else
        {
          localStringBuilder.append(paramString2).append(") VALUES (NULL");
          localStringBuilder.append(')');
          paramString1 = new SQLiteStatement(this, localStringBuilder.toString(), paramString1);
          try
          {
            long l = paramString1.executeInsert();
            return l;
          }
          finally {}
        }
        paramString1 = "";
      }
      finally
      {
        releaseReference();
      }
      continue;
      label290:
      paramString1 = paramString2;
      if (i < paramInt) {
        if (i > 0) {
          paramString1 = ",?";
        } else {
          paramString1 = "?";
        }
      }
    }
  }
  
  public boolean isDatabaseIntegrityOk()
  {
    acquireReference();
    try
    {
      localObject5 = getAttachedDbs();
      Object localObject1 = localObject5;
      if (localObject5 == null) {
        throw new IllegalStateException("databaselist for: " + getPath() + " couldn't " + "be retrieved. probably because the database is closed");
      }
    }
    catch (SQLiteException localSQLiteException)
    {
      Object localObject5;
      ArrayList localArrayList = new ArrayList();
      try
      {
        localArrayList.add(new Pair("main", getPath()));
        i = 0;
      }
      finally
      {
        for (;;)
        {
          int i;
          Pair localPair;
          continue;
          i += 1;
        }
      }
      if (i < localArrayList.size())
      {
        localPair = (Pair)localArrayList.get(i);
        localObject5 = null;
        try
        {
          localSQLiteStatement = compileStatement("PRAGMA " + (String)localPair.first + ".integrity_check(1);");
          localObject5 = localSQLiteStatement;
          String str = localSQLiteStatement.simpleQueryForString();
          localObject5 = localSQLiteStatement;
          if (!str.equalsIgnoreCase("ok"))
          {
            localObject5 = localSQLiteStatement;
            Log.e("SQLiteDatabase", "PRAGMA integrity_check on " + (String)localPair.second + " returned: " + str);
            if (localSQLiteStatement != null) {
              localSQLiteStatement.close();
            }
            return false;
          }
          if (localSQLiteStatement == null) {
            break label281;
          }
        }
        finally
        {
          SQLiteStatement localSQLiteStatement;
          if (localObject5 != null) {
            ((SQLiteStatement)localObject5).close();
          }
        }
      }
    }
    finally
    {
      releaseReference();
    }
    releaseReference();
    return true;
  }
  
  public boolean isDbLockedByCurrentThread()
  {
    acquireReference();
    try
    {
      boolean bool = getThreadSession().hasConnection();
      return bool;
    }
    finally
    {
      releaseReference();
    }
  }
  
  @Deprecated
  public boolean isDbLockedByOtherThreads()
  {
    return false;
  }
  
  public boolean isInMemoryDatabase()
  {
    synchronized (this.mLock)
    {
      boolean bool = this.mConfigurationLocked.isInMemoryDb();
      return bool;
    }
  }
  
  public boolean isOpen()
  {
    synchronized (this.mLock)
    {
      SQLiteConnectionPool localSQLiteConnectionPool = this.mConnectionPoolLocked;
      if (localSQLiteConnectionPool != null)
      {
        bool = true;
        return bool;
      }
      boolean bool = false;
    }
  }
  
  public boolean isReadOnly()
  {
    synchronized (this.mLock)
    {
      boolean bool = isReadOnlyLocked();
      return bool;
    }
  }
  
  public boolean isWriteAheadLoggingEnabled()
  {
    boolean bool = false;
    synchronized (this.mLock)
    {
      throwIfNotOpenLocked();
      int i = this.mConfigurationLocked.openFlags;
      if ((i & 0x20000000) != 0) {
        bool = true;
      }
      return bool;
    }
  }
  
  @Deprecated
  public void markTableSyncable(String paramString1, String paramString2) {}
  
  @Deprecated
  public void markTableSyncable(String paramString1, String paramString2, String paramString3) {}
  
  public boolean needUpgrade(int paramInt)
  {
    return paramInt > getVersion();
  }
  
  protected void onAllReferencesReleased()
  {
    dispose(false);
  }
  
  void onCorruption()
  {
    EventLog.writeEvent(75004, getLabel());
    this.mErrorHandler.onCorruption(this);
  }
  
  public Cursor query(String paramString1, String[] paramArrayOfString1, String paramString2, String[] paramArrayOfString2, String paramString3, String paramString4, String paramString5)
  {
    return query(false, paramString1, paramArrayOfString1, paramString2, paramArrayOfString2, paramString3, paramString4, paramString5, null);
  }
  
  public Cursor query(String paramString1, String[] paramArrayOfString1, String paramString2, String[] paramArrayOfString2, String paramString3, String paramString4, String paramString5, String paramString6)
  {
    return query(false, paramString1, paramArrayOfString1, paramString2, paramArrayOfString2, paramString3, paramString4, paramString5, paramString6);
  }
  
  public Cursor query(boolean paramBoolean, String paramString1, String[] paramArrayOfString1, String paramString2, String[] paramArrayOfString2, String paramString3, String paramString4, String paramString5, String paramString6)
  {
    return queryWithFactory(null, paramBoolean, paramString1, paramArrayOfString1, paramString2, paramArrayOfString2, paramString3, paramString4, paramString5, paramString6, null);
  }
  
  public Cursor query(boolean paramBoolean, String paramString1, String[] paramArrayOfString1, String paramString2, String[] paramArrayOfString2, String paramString3, String paramString4, String paramString5, String paramString6, CancellationSignal paramCancellationSignal)
  {
    return queryWithFactory(null, paramBoolean, paramString1, paramArrayOfString1, paramString2, paramArrayOfString2, paramString3, paramString4, paramString5, paramString6, paramCancellationSignal);
  }
  
  public Cursor queryWithFactory(CursorFactory paramCursorFactory, boolean paramBoolean, String paramString1, String[] paramArrayOfString1, String paramString2, String[] paramArrayOfString2, String paramString3, String paramString4, String paramString5, String paramString6)
  {
    return queryWithFactory(paramCursorFactory, paramBoolean, paramString1, paramArrayOfString1, paramString2, paramArrayOfString2, paramString3, paramString4, paramString5, paramString6, null);
  }
  
  public Cursor queryWithFactory(CursorFactory paramCursorFactory, boolean paramBoolean, String paramString1, String[] paramArrayOfString1, String paramString2, String[] paramArrayOfString2, String paramString3, String paramString4, String paramString5, String paramString6, CancellationSignal paramCancellationSignal)
  {
    acquireReference();
    try
    {
      paramCursorFactory = rawQueryWithFactory(paramCursorFactory, SQLiteQueryBuilder.buildQueryString(paramBoolean, paramString1, paramArrayOfString1, paramString2, paramString3, paramString4, paramString5, paramString6), paramArrayOfString2, findEditTable(paramString1), paramCancellationSignal);
      return paramCursorFactory;
    }
    finally
    {
      releaseReference();
    }
  }
  
  public Cursor rawQuery(String paramString, String[] paramArrayOfString)
  {
    return rawQueryWithFactory(null, paramString, paramArrayOfString, null, null);
  }
  
  public Cursor rawQuery(String paramString, String[] paramArrayOfString, CancellationSignal paramCancellationSignal)
  {
    return rawQueryWithFactory(null, paramString, paramArrayOfString, null, paramCancellationSignal);
  }
  
  public Cursor rawQueryWithFactory(CursorFactory paramCursorFactory, String paramString1, String[] paramArrayOfString, String paramString2)
  {
    return rawQueryWithFactory(paramCursorFactory, paramString1, paramArrayOfString, paramString2, null);
  }
  
  /* Error */
  public Cursor rawQueryWithFactory(CursorFactory paramCursorFactory, String paramString1, String[] paramArrayOfString, String paramString2, CancellationSignal paramCancellationSignal)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 145	android/database/sqlite/SQLiteDatabase:acquireReference	()V
    //   4: new 719	android/database/sqlite/SQLiteDirectCursorDriver
    //   7: dup
    //   8: aload_0
    //   9: aload_2
    //   10: aload 4
    //   12: aload 5
    //   14: invokespecial 722	android/database/sqlite/SQLiteDirectCursorDriver:<init>	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Ljava/lang/String;Landroid/os/CancellationSignal;)V
    //   17: astore_2
    //   18: aload_1
    //   19: ifnull +18 -> 37
    //   22: aload_2
    //   23: aload_1
    //   24: aload_3
    //   25: invokeinterface 727 3 0
    //   30: astore_1
    //   31: aload_0
    //   32: invokevirtual 161	android/database/sqlite/SQLiteDatabase:releaseReference	()V
    //   35: aload_1
    //   36: areturn
    //   37: aload_0
    //   38: getfield 128	android/database/sqlite/SQLiteDatabase:mCursorFactory	Landroid/database/sqlite/SQLiteDatabase$CursorFactory;
    //   41: astore_1
    //   42: goto -20 -> 22
    //   45: astore_1
    //   46: aload_0
    //   47: invokevirtual 161	android/database/sqlite/SQLiteDatabase:releaseReference	()V
    //   50: aload_1
    //   51: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	52	0	this	SQLiteDatabase
    //   0	52	1	paramCursorFactory	CursorFactory
    //   0	52	2	paramString1	String
    //   0	52	3	paramArrayOfString	String[]
    //   0	52	4	paramString2	String
    //   0	52	5	paramCancellationSignal	CancellationSignal
    // Exception table:
    //   from	to	target	type
    //   4	18	45	finally
    //   22	31	45	finally
    //   37	42	45	finally
  }
  
  public void reopenReadWrite()
  {
    synchronized (this.mLock)
    {
      throwIfNotOpenLocked();
      boolean bool = isReadOnlyLocked();
      if (!bool) {
        return;
      }
      int i = this.mConfigurationLocked.openFlags;
      this.mConfigurationLocked.openFlags = (this.mConfigurationLocked.openFlags & 0xFFFFFFFE | 0x0);
      try
      {
        this.mConnectionPoolLocked.reconfigure(this.mConfigurationLocked);
        return;
      }
      catch (RuntimeException localRuntimeException)
      {
        this.mConfigurationLocked.openFlags = i;
        throw localRuntimeException;
      }
    }
  }
  
  public long replace(String paramString1, String paramString2, ContentValues paramContentValues)
  {
    try
    {
      long l = insertWithOnConflict(paramString1, paramString2, paramContentValues, 5);
      return l;
    }
    catch (SQLException paramString1)
    {
      Log.e("SQLiteDatabase", "Error inserting " + paramContentValues, paramString1);
    }
    return -1L;
  }
  
  public long replaceOrThrow(String paramString1, String paramString2, ContentValues paramContentValues)
    throws SQLException
  {
    return insertWithOnConflict(paramString1, paramString2, paramContentValues, 5);
  }
  
  public void setForeignKeyConstraintsEnabled(boolean paramBoolean)
  {
    for (;;)
    {
      synchronized (this.mLock)
      {
        throwIfNotOpenLocked();
        boolean bool = this.mConfigurationLocked.foreignKeyConstraintsEnabled;
        if (bool == paramBoolean) {
          return;
        }
        this.mConfigurationLocked.foreignKeyConstraintsEnabled = paramBoolean;
        SQLiteDatabaseConfiguration localSQLiteDatabaseConfiguration;
        try
        {
          this.mConnectionPoolLocked.reconfigure(this.mConfigurationLocked);
          return;
        }
        catch (RuntimeException localRuntimeException)
        {
          localSQLiteDatabaseConfiguration = this.mConfigurationLocked;
          if (!paramBoolean) {
            break label79;
          }
        }
        paramBoolean = false;
        localSQLiteDatabaseConfiguration.foreignKeyConstraintsEnabled = paramBoolean;
        throw localRuntimeException;
      }
      label79:
      paramBoolean = true;
    }
  }
  
  public void setLocale(Locale paramLocale)
  {
    if (paramLocale == null) {
      throw new IllegalArgumentException("locale must not be null.");
    }
    synchronized (this.mLock)
    {
      throwIfNotOpenLocked();
      Locale localLocale = this.mConfigurationLocked.locale;
      this.mConfigurationLocked.locale = paramLocale;
      try
      {
        this.mConnectionPoolLocked.reconfigure(this.mConfigurationLocked);
        return;
      }
      catch (RuntimeException paramLocale)
      {
        this.mConfigurationLocked.locale = localLocale;
        throw paramLocale;
      }
    }
  }
  
  @Deprecated
  public void setLockingEnabled(boolean paramBoolean) {}
  
  public void setMaxSqlCacheSize(int paramInt)
  {
    if ((paramInt > 100) || (paramInt < 0)) {
      throw new IllegalStateException("expected value between 0 and 100");
    }
    synchronized (this.mLock)
    {
      throwIfNotOpenLocked();
      int i = this.mConfigurationLocked.maxSqlCacheSize;
      this.mConfigurationLocked.maxSqlCacheSize = paramInt;
      try
      {
        this.mConnectionPoolLocked.reconfigure(this.mConfigurationLocked);
        return;
      }
      catch (RuntimeException localRuntimeException)
      {
        this.mConfigurationLocked.maxSqlCacheSize = i;
        throw localRuntimeException;
      }
    }
  }
  
  public long setMaximumSize(long paramLong)
  {
    long l3 = getPageSize();
    long l2 = paramLong / l3;
    long l1 = l2;
    if (paramLong % l3 != 0L) {
      l1 = l2 + 1L;
    }
    return DatabaseUtils.longForQuery(this, "PRAGMA max_page_count = " + l1, null) * l3;
  }
  
  public void setPageSize(long paramLong)
  {
    execSQL("PRAGMA page_size = " + paramLong);
  }
  
  public void setTransactionSuccessful()
  {
    acquireReference();
    try
    {
      getThreadSession().setTransactionSuccessful();
      return;
    }
    finally
    {
      releaseReference();
    }
  }
  
  public void setVersion(int paramInt)
  {
    execSQL("PRAGMA user_version = " + paramInt);
  }
  
  public String toString()
  {
    return "SQLiteDatabase: " + getPath();
  }
  
  public int update(String paramString1, ContentValues paramContentValues, String paramString2, String[] paramArrayOfString)
  {
    return updateWithOnConflict(paramString1, paramContentValues, paramString2, paramArrayOfString, 0);
  }
  
  public int updateWithOnConflict(String paramString1, ContentValues paramContentValues, String paramString2, String[] paramArrayOfString, int paramInt)
  {
    if ((paramContentValues == null) || (paramContentValues.size() == 0)) {
      throw new IllegalArgumentException("Empty values");
    }
    acquireReference();
    for (;;)
    {
      int i;
      Object[] arrayOfObject;
      int j;
      try
      {
        StringBuilder localStringBuilder = new StringBuilder(120);
        localStringBuilder.append("UPDATE ");
        localStringBuilder.append(CONFLICT_VALUES[paramInt]);
        localStringBuilder.append(paramString1);
        localStringBuilder.append(" SET ");
        paramInt = paramContentValues.size();
        if (paramArrayOfString == null)
        {
          i = paramInt;
          arrayOfObject = new Object[i];
          Iterator localIterator = paramContentValues.keySet().iterator();
          j = 0;
          if (!localIterator.hasNext()) {
            break label272;
          }
          String str = (String)localIterator.next();
          if (j > 0)
          {
            paramString1 = ",";
            localStringBuilder.append(paramString1);
            localStringBuilder.append(str);
            arrayOfObject[j] = paramContentValues.get(str);
            localStringBuilder.append("=?");
            j += 1;
            continue;
          }
        }
        else
        {
          i = paramInt + paramArrayOfString.length;
          continue;
          if (!TextUtils.isEmpty(paramString2))
          {
            localStringBuilder.append(" WHERE ");
            localStringBuilder.append(paramString2);
          }
          paramString1 = new SQLiteStatement(this, localStringBuilder.toString(), arrayOfObject);
          try
          {
            paramInt = paramString1.executeUpdateDelete();
            return paramInt;
          }
          finally {}
        }
        paramString1 = "";
      }
      finally
      {
        releaseReference();
      }
      continue;
      label272:
      if (paramArrayOfString != null)
      {
        j = paramInt;
        while (j < i)
        {
          arrayOfObject[j] = paramArrayOfString[(j - paramInt)];
          j += 1;
        }
      }
    }
  }
  
  public void validateSql(String paramString, CancellationSignal paramCancellationSignal)
  {
    getThreadSession().prepare(paramString, getThreadDefaultConnectionFlags(true), paramCancellationSignal, null);
  }
  
  @Deprecated
  public boolean yieldIfContended()
  {
    return yieldIfContendedHelper(false, -1L);
  }
  
  public boolean yieldIfContendedSafely()
  {
    return yieldIfContendedHelper(true, -1L);
  }
  
  public boolean yieldIfContendedSafely(long paramLong)
  {
    return yieldIfContendedHelper(true, paramLong);
  }
  
  public static abstract interface CursorFactory
  {
    public abstract Cursor newCursor(SQLiteDatabase paramSQLiteDatabase, SQLiteCursorDriver paramSQLiteCursorDriver, String paramString, SQLiteQuery paramSQLiteQuery);
  }
  
  public static abstract interface CustomFunction
  {
    public abstract void callback(String[] paramArrayOfString);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/database/sqlite/SQLiteDatabase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */