package android.database.sqlite;

import android.database.DatabaseUtils;
import android.os.CancellationSignal;
import android.os.CancellationSignal.OnCancelListener;
import android.os.SystemClock;
import android.os.Trace;
import android.util.Log;
import android.util.LruCache;
import android.util.Printer;
import dalvik.system.BlockGuard;
import dalvik.system.BlockGuard.Policy;
import dalvik.system.CloseGuard;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public final class SQLiteConnection
  implements CancellationSignal.OnCancelListener
{
  private static final boolean DEBUG = false;
  private static final byte[] EMPTY_BYTE_ARRAY;
  private static final String[] EMPTY_STRING_ARRAY;
  private static final String TAG = "SQLiteConnection";
  private int mCancellationSignalAttachCount;
  private final CloseGuard mCloseGuard = CloseGuard.get();
  private final SQLiteDatabaseConfiguration mConfiguration;
  private final int mConnectionId;
  private long mConnectionPtr;
  private final boolean mIsPrimaryConnection;
  private final boolean mIsReadOnlyConnection;
  private boolean mOnlyAllowReadOnlyOperations;
  private final SQLiteConnectionPool mPool;
  private final PreparedStatementCache mPreparedStatementCache;
  private PreparedStatement mPreparedStatementPool;
  private final OperationLog mRecentOperations = new OperationLog(null);
  
  static
  {
    if (SQLiteConnection.class.desiredAssertionStatus()) {}
    for (boolean bool = false;; bool = true)
    {
      -assertionsDisabled = bool;
      EMPTY_STRING_ARRAY = new String[0];
      EMPTY_BYTE_ARRAY = new byte[0];
      return;
    }
  }
  
  private SQLiteConnection(SQLiteConnectionPool paramSQLiteConnectionPool, SQLiteDatabaseConfiguration paramSQLiteDatabaseConfiguration, int paramInt, boolean paramBoolean)
  {
    this.mPool = paramSQLiteConnectionPool;
    this.mConfiguration = new SQLiteDatabaseConfiguration(paramSQLiteDatabaseConfiguration);
    this.mConnectionId = paramInt;
    this.mIsPrimaryConnection = paramBoolean;
    paramBoolean = bool;
    if ((paramSQLiteDatabaseConfiguration.openFlags & 0x1) != 0) {
      paramBoolean = true;
    }
    this.mIsReadOnlyConnection = paramBoolean;
    this.mPreparedStatementCache = new PreparedStatementCache(this.mConfiguration.maxSqlCacheSize);
    this.mCloseGuard.open("close");
  }
  
  private PreparedStatement acquirePreparedStatement(String paramString)
  {
    PreparedStatement localPreparedStatement2 = (PreparedStatement)this.mPreparedStatementCache.get(paramString);
    int i = 0;
    if (localPreparedStatement2 != null)
    {
      if (!localPreparedStatement2.mInUse) {
        return localPreparedStatement2;
      }
      i = 1;
    }
    long l = nativePrepareStatement(this.mConnectionPtr, paramString);
    PreparedStatement localPreparedStatement1 = localPreparedStatement2;
    try
    {
      int j = nativeGetParameterCount(this.mConnectionPtr, l);
      localPreparedStatement1 = localPreparedStatement2;
      int k = DatabaseUtils.getSqlStatementType(paramString);
      localPreparedStatement1 = localPreparedStatement2;
      localPreparedStatement2 = obtainPreparedStatement(paramString, l, j, k, nativeIsReadOnly(this.mConnectionPtr, l));
      if (i == 0)
      {
        localPreparedStatement1 = localPreparedStatement2;
        if (isCacheable(k))
        {
          localPreparedStatement1 = localPreparedStatement2;
          this.mPreparedStatementCache.put(paramString, localPreparedStatement2);
          localPreparedStatement1 = localPreparedStatement2;
          localPreparedStatement2.mInCache = true;
        }
      }
      localPreparedStatement2.mInUse = true;
      return localPreparedStatement2;
    }
    catch (RuntimeException paramString)
    {
      if (localPreparedStatement1 == null) {
        break label158;
      }
    }
    if (localPreparedStatement1.mInCache) {}
    for (;;)
    {
      throw paramString;
      label158:
      nativeFinalizeStatement(this.mConnectionPtr, l);
    }
  }
  
  private void applyBlockGuardPolicy(PreparedStatement paramPreparedStatement)
  {
    if (!this.mConfiguration.isInMemoryDb())
    {
      if (paramPreparedStatement.mReadOnly) {
        BlockGuard.getThreadPolicy().onReadFromDisk();
      }
    }
    else {
      return;
    }
    BlockGuard.getThreadPolicy().onWriteToDisk();
  }
  
  private void attachCancellationSignal(CancellationSignal paramCancellationSignal)
  {
    if (paramCancellationSignal != null)
    {
      paramCancellationSignal.throwIfCanceled();
      this.mCancellationSignalAttachCount += 1;
      if (this.mCancellationSignalAttachCount == 1)
      {
        nativeResetCancel(this.mConnectionPtr, true);
        paramCancellationSignal.setOnCancelListener(this);
      }
    }
  }
  
  private void bindArguments(PreparedStatement paramPreparedStatement, Object[] paramArrayOfObject)
  {
    if (paramArrayOfObject != null) {}
    for (int i = paramArrayOfObject.length; i != paramPreparedStatement.mNumParameters; i = 0) {
      throw new SQLiteBindOrColumnIndexOutOfRangeException("Expected " + paramPreparedStatement.mNumParameters + " bind arguments but " + i + " were provided.");
    }
    if (i == 0) {
      return;
    }
    long l1 = paramPreparedStatement.mStatementPtr;
    int j = 0;
    if (j < i)
    {
      paramPreparedStatement = paramArrayOfObject[j];
      int k;
      switch (DatabaseUtils.getTypeOfObject(paramPreparedStatement))
      {
      case 3: 
      default: 
        if ((paramPreparedStatement instanceof Boolean))
        {
          long l2 = this.mConnectionPtr;
          if (((Boolean)paramPreparedStatement).booleanValue())
          {
            k = 1;
            label154:
            nativeBindLong(l2, l1, j + 1, k);
          }
        }
        break;
      }
      for (;;)
      {
        j += 1;
        break;
        nativeBindNull(this.mConnectionPtr, l1, j + 1);
        continue;
        nativeBindLong(this.mConnectionPtr, l1, j + 1, ((Number)paramPreparedStatement).longValue());
        continue;
        nativeBindDouble(this.mConnectionPtr, l1, j + 1, ((Number)paramPreparedStatement).doubleValue());
        continue;
        nativeBindBlob(this.mConnectionPtr, l1, j + 1, (byte[])paramPreparedStatement);
        continue;
        k = 0;
        break label154;
        nativeBindString(this.mConnectionPtr, l1, j + 1, paramPreparedStatement.toString());
      }
    }
  }
  
  private static String canonicalizeSyncMode(String paramString)
  {
    if (paramString.equals("0")) {
      return "OFF";
    }
    if (paramString.equals("1")) {
      return "NORMAL";
    }
    if (paramString.equals("2")) {
      return "FULL";
    }
    return paramString;
  }
  
  private void detachCancellationSignal(CancellationSignal paramCancellationSignal)
  {
    if (paramCancellationSignal != null)
    {
      if (!-assertionsDisabled)
      {
        if (this.mCancellationSignalAttachCount > 0) {}
        for (int i = 1; i == 0; i = 0) {
          throw new AssertionError();
        }
      }
      this.mCancellationSignalAttachCount -= 1;
      if (this.mCancellationSignalAttachCount == 0)
      {
        paramCancellationSignal.setOnCancelListener(null);
        nativeResetCancel(this.mConnectionPtr, false);
      }
    }
  }
  
  private void dispose(boolean paramBoolean)
  {
    if (this.mCloseGuard != null)
    {
      if (paramBoolean) {
        this.mCloseGuard.warnIfOpen();
      }
      this.mCloseGuard.close();
    }
    int i;
    if (this.mConnectionPtr != 0L) {
      i = this.mRecentOperations.beginOperation("close", null, null);
    }
    try
    {
      this.mPreparedStatementCache.evictAll();
      nativeClose(this.mConnectionPtr);
      this.mConnectionPtr = 0L;
      return;
    }
    finally
    {
      this.mRecentOperations.endOperation(i);
    }
  }
  
  private void finalizePreparedStatement(PreparedStatement paramPreparedStatement)
  {
    nativeFinalizeStatement(this.mConnectionPtr, paramPreparedStatement.mStatementPtr);
    recyclePreparedStatement(paramPreparedStatement);
  }
  
  private SQLiteDebug.DbStats getMainDbStatsUnsafe(int paramInt, long paramLong1, long paramLong2)
  {
    String str2 = this.mConfiguration.path;
    String str1 = str2;
    if (!this.mIsPrimaryConnection) {
      str1 = str2 + " (" + this.mConnectionId + ")";
    }
    return new SQLiteDebug.DbStats(str1, paramLong1, paramLong2, paramInt, this.mPreparedStatementCache.hitCount(), this.mPreparedStatementCache.missCount(), this.mPreparedStatementCache.size());
  }
  
  private static boolean isCacheable(int paramInt)
  {
    return (paramInt == 2) || (paramInt == 1);
  }
  
  private static native void nativeBindBlob(long paramLong1, long paramLong2, int paramInt, byte[] paramArrayOfByte);
  
  private static native void nativeBindDouble(long paramLong1, long paramLong2, int paramInt, double paramDouble);
  
  private static native void nativeBindLong(long paramLong1, long paramLong2, int paramInt, long paramLong3);
  
  private static native void nativeBindNull(long paramLong1, long paramLong2, int paramInt);
  
  private static native void nativeBindString(long paramLong1, long paramLong2, int paramInt, String paramString);
  
  private static native void nativeCancel(long paramLong);
  
  private static native void nativeClose(long paramLong);
  
  private static native void nativeExecute(long paramLong1, long paramLong2);
  
  private static native int nativeExecuteForBlobFileDescriptor(long paramLong1, long paramLong2);
  
  private static native int nativeExecuteForChangedRowCount(long paramLong1, long paramLong2);
  
  private static native long nativeExecuteForCursorWindow(long paramLong1, long paramLong2, long paramLong3, int paramInt1, int paramInt2, boolean paramBoolean);
  
  private static native long nativeExecuteForLastInsertedRowId(long paramLong1, long paramLong2);
  
  private static native long nativeExecuteForLong(long paramLong1, long paramLong2);
  
  private static native String nativeExecuteForString(long paramLong1, long paramLong2);
  
  private static native void nativeFinalizeStatement(long paramLong1, long paramLong2);
  
  private static native int nativeGetColumnCount(long paramLong1, long paramLong2);
  
  private static native String nativeGetColumnName(long paramLong1, long paramLong2, int paramInt);
  
  private static native int nativeGetDbLookaside(long paramLong);
  
  private static native int nativeGetParameterCount(long paramLong1, long paramLong2);
  
  private static native boolean nativeIsReadOnly(long paramLong1, long paramLong2);
  
  private static native long nativeOpen(String paramString1, int paramInt, String paramString2, boolean paramBoolean1, boolean paramBoolean2);
  
  private static native long nativePrepareStatement(long paramLong, String paramString);
  
  private static native void nativeRegisterCustomFunction(long paramLong, SQLiteCustomFunction paramSQLiteCustomFunction);
  
  private static native void nativeRegisterLocalizedCollators(long paramLong, String paramString);
  
  private static native void nativeResetCancel(long paramLong, boolean paramBoolean);
  
  private static native void nativeResetStatementAndClearBindings(long paramLong1, long paramLong2);
  
  private PreparedStatement obtainPreparedStatement(String paramString, long paramLong, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    PreparedStatement localPreparedStatement = this.mPreparedStatementPool;
    if (localPreparedStatement != null)
    {
      this.mPreparedStatementPool = localPreparedStatement.mPoolNext;
      localPreparedStatement.mPoolNext = null;
      localPreparedStatement.mInCache = false;
    }
    for (;;)
    {
      localPreparedStatement.mSql = paramString;
      localPreparedStatement.mStatementPtr = paramLong;
      localPreparedStatement.mNumParameters = paramInt1;
      localPreparedStatement.mType = paramInt2;
      localPreparedStatement.mReadOnly = paramBoolean;
      return localPreparedStatement;
      localPreparedStatement = new PreparedStatement(null);
    }
  }
  
  static SQLiteConnection open(SQLiteConnectionPool paramSQLiteConnectionPool, SQLiteDatabaseConfiguration paramSQLiteDatabaseConfiguration, int paramInt, boolean paramBoolean)
  {
    paramSQLiteConnectionPool = new SQLiteConnection(paramSQLiteConnectionPool, paramSQLiteDatabaseConfiguration, paramInt, paramBoolean);
    try
    {
      paramSQLiteConnectionPool.open();
      return paramSQLiteConnectionPool;
    }
    catch (SQLiteException paramSQLiteDatabaseConfiguration)
    {
      paramSQLiteConnectionPool.dispose(false);
      throw paramSQLiteDatabaseConfiguration;
    }
  }
  
  private void open()
  {
    this.mConnectionPtr = nativeOpen(this.mConfiguration.path, this.mConfiguration.openFlags, this.mConfiguration.label, SQLiteDebug.DEBUG_SQL_STATEMENTS, SQLiteDebug.DEBUG_SQL_TIME);
    setPageSize();
    setForeignKeyModeFromConfiguration();
    setWalModeFromConfiguration();
    setJournalSizeLimit();
    setAutoCheckpointInterval();
    setLocaleFromConfiguration();
    int j = this.mConfiguration.customFunctions.size();
    int i = 0;
    while (i < j)
    {
      SQLiteCustomFunction localSQLiteCustomFunction = (SQLiteCustomFunction)this.mConfiguration.customFunctions.get(i);
      nativeRegisterCustomFunction(this.mConnectionPtr, localSQLiteCustomFunction);
      i += 1;
    }
  }
  
  private void recyclePreparedStatement(PreparedStatement paramPreparedStatement)
  {
    paramPreparedStatement.mSql = null;
    paramPreparedStatement.mPoolNext = this.mPreparedStatementPool;
    this.mPreparedStatementPool = paramPreparedStatement;
  }
  
  private void releasePreparedStatement(PreparedStatement paramPreparedStatement)
  {
    paramPreparedStatement.mInUse = false;
    if (paramPreparedStatement.mInCache) {
      try
      {
        nativeResetStatementAndClearBindings(this.mConnectionPtr, paramPreparedStatement.mStatementPtr);
        return;
      }
      catch (SQLiteException localSQLiteException)
      {
        this.mPreparedStatementCache.remove(paramPreparedStatement.mSql);
        return;
      }
    }
    finalizePreparedStatement(paramPreparedStatement);
  }
  
  private void setAutoCheckpointInterval()
  {
    if ((this.mConfiguration.isInMemoryDb()) || (this.mIsReadOnlyConnection)) {}
    long l;
    do
    {
      return;
      l = SQLiteGlobal.getWALAutoCheckpoint();
    } while (executeForLong("PRAGMA wal_autocheckpoint", null, null) == l);
    executeForLong("PRAGMA wal_autocheckpoint=" + l, null, null);
  }
  
  private void setForeignKeyModeFromConfiguration()
  {
    if (!this.mIsReadOnlyConnection) {
      if (!this.mConfiguration.foreignKeyConstraintsEnabled) {
        break label63;
      }
    }
    label63:
    for (int i = 1;; i = 0)
    {
      long l = i;
      if (executeForLong("PRAGMA foreign_keys", null, null) != l) {
        execute("PRAGMA foreign_keys=" + l, null, null);
      }
      return;
    }
  }
  
  private void setJournalMode(String paramString)
  {
    String str = executeForString("PRAGMA journal_mode", null, null);
    if (!str.equalsIgnoreCase(paramString)) {
      try
      {
        boolean bool = executeForString("PRAGMA journal_mode=" + paramString, null, null).equalsIgnoreCase(paramString);
        if (bool) {
          return;
        }
      }
      catch (SQLiteDatabaseLockedException localSQLiteDatabaseLockedException)
      {
        Log.w("SQLiteConnection", "Could not change the database journal mode of '" + this.mConfiguration.label + "' from '" + str + "' to '" + paramString + "' because the database is locked.  This usually means that " + "there are other open connections to the database which prevents " + "the database from enabling or disabling write-ahead logging mode.  " + "Proceeding without changing the journal mode.");
      }
    }
  }
  
  private void setJournalSizeLimit()
  {
    if ((this.mConfiguration.isInMemoryDb()) || (this.mIsReadOnlyConnection)) {}
    long l;
    do
    {
      return;
      l = SQLiteGlobal.getJournalSizeLimit();
    } while (executeForLong("PRAGMA journal_size_limit", null, null) == l);
    executeForLong("PRAGMA journal_size_limit=" + l, null, null);
  }
  
  /* Error */
  private void setLocaleFromConfiguration()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 106	android/database/sqlite/SQLiteConnection:mConfiguration	Landroid/database/sqlite/SQLiteDatabaseConfiguration;
    //   4: getfield 113	android/database/sqlite/SQLiteDatabaseConfiguration:openFlags	I
    //   7: bipush 16
    //   9: iand
    //   10: ifeq +4 -> 14
    //   13: return
    //   14: aload_0
    //   15: getfield 106	android/database/sqlite/SQLiteConnection:mConfiguration	Landroid/database/sqlite/SQLiteDatabaseConfiguration;
    //   18: getfield 528	android/database/sqlite/SQLiteDatabaseConfiguration:locale	Ljava/util/Locale;
    //   21: invokevirtual 531	java/util/Locale:toString	()Ljava/lang/String;
    //   24: astore_2
    //   25: aload_0
    //   26: getfield 141	android/database/sqlite/SQLiteConnection:mConnectionPtr	J
    //   29: aload_2
    //   30: invokestatic 533	android/database/sqlite/SQLiteConnection:nativeRegisterLocalizedCollators	(JLjava/lang/String;)V
    //   33: aload_0
    //   34: getfield 115	android/database/sqlite/SQLiteConnection:mIsReadOnlyConnection	Z
    //   37: ifeq +4 -> 41
    //   40: return
    //   41: aload_0
    //   42: ldc_w 535
    //   45: aconst_null
    //   46: aconst_null
    //   47: invokevirtual 482	android/database/sqlite/SQLiteConnection:execute	(Ljava/lang/String;[Ljava/lang/Object;Landroid/os/CancellationSignal;)V
    //   50: aload_0
    //   51: ldc_w 537
    //   54: aconst_null
    //   55: aconst_null
    //   56: invokevirtual 491	android/database/sqlite/SQLiteConnection:executeForString	(Ljava/lang/String;[Ljava/lang/Object;Landroid/os/CancellationSignal;)Ljava/lang/String;
    //   59: astore_1
    //   60: aload_1
    //   61: ifnull +12 -> 73
    //   64: aload_1
    //   65: aload_2
    //   66: invokevirtual 296	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   69: ifeq +4 -> 73
    //   72: return
    //   73: aload_0
    //   74: ldc_w 539
    //   77: aconst_null
    //   78: aconst_null
    //   79: invokevirtual 482	android/database/sqlite/SQLiteConnection:execute	(Ljava/lang/String;[Ljava/lang/Object;Landroid/os/CancellationSignal;)V
    //   82: aload_0
    //   83: ldc_w 541
    //   86: aconst_null
    //   87: aconst_null
    //   88: invokevirtual 482	android/database/sqlite/SQLiteConnection:execute	(Ljava/lang/String;[Ljava/lang/Object;Landroid/os/CancellationSignal;)V
    //   91: aload_0
    //   92: ldc_w 543
    //   95: iconst_1
    //   96: anewarray 4	java/lang/Object
    //   99: dup
    //   100: iconst_0
    //   101: aload_2
    //   102: aastore
    //   103: aconst_null
    //   104: invokevirtual 482	android/database/sqlite/SQLiteConnection:execute	(Ljava/lang/String;[Ljava/lang/Object;Landroid/os/CancellationSignal;)V
    //   107: aload_0
    //   108: ldc_w 545
    //   111: aconst_null
    //   112: aconst_null
    //   113: invokevirtual 482	android/database/sqlite/SQLiteConnection:execute	(Ljava/lang/String;[Ljava/lang/Object;Landroid/os/CancellationSignal;)V
    //   116: iconst_1
    //   117: ifeq +83 -> 200
    //   120: ldc_w 547
    //   123: astore_1
    //   124: aload_0
    //   125: aload_1
    //   126: aconst_null
    //   127: aconst_null
    //   128: invokevirtual 482	android/database/sqlite/SQLiteConnection:execute	(Ljava/lang/String;[Ljava/lang/Object;Landroid/os/CancellationSignal;)V
    //   131: return
    //   132: aload_0
    //   133: aload_1
    //   134: aconst_null
    //   135: aconst_null
    //   136: invokevirtual 482	android/database/sqlite/SQLiteConnection:execute	(Ljava/lang/String;[Ljava/lang/Object;Landroid/os/CancellationSignal;)V
    //   139: aload_3
    //   140: athrow
    //   141: astore_1
    //   142: new 398	android/database/sqlite/SQLiteException
    //   145: dup
    //   146: new 225	java/lang/StringBuilder
    //   149: dup
    //   150: invokespecial 226	java/lang/StringBuilder:<init>	()V
    //   153: ldc_w 549
    //   156: invokevirtual 232	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   159: aload_0
    //   160: getfield 106	android/database/sqlite/SQLiteConnection:mConfiguration	Landroid/database/sqlite/SQLiteDatabaseConfiguration;
    //   163: getfield 407	android/database/sqlite/SQLiteDatabaseConfiguration:label	Ljava/lang/String;
    //   166: invokevirtual 232	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   169: ldc_w 503
    //   172: invokevirtual 232	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   175: aload_2
    //   176: invokevirtual 232	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   179: ldc_w 551
    //   182: invokevirtual 232	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   185: invokevirtual 243	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   188: aload_1
    //   189: invokespecial 554	android/database/sqlite/SQLiteException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   192: athrow
    //   193: ldc_w 556
    //   196: astore_1
    //   197: goto -65 -> 132
    //   200: ldc_w 556
    //   203: astore_1
    //   204: goto -80 -> 124
    //   207: astore_3
    //   208: iconst_0
    //   209: ifeq -16 -> 193
    //   212: ldc_w 547
    //   215: astore_1
    //   216: goto -84 -> 132
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	219	0	this	SQLiteConnection
    //   59	75	1	str1	String
    //   141	48	1	localRuntimeException	RuntimeException
    //   196	20	1	str2	String
    //   24	152	2	str3	String
    //   139	1	3	localObject1	Object
    //   207	1	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   41	60	141	java/lang/RuntimeException
    //   64	72	141	java/lang/RuntimeException
    //   73	82	141	java/lang/RuntimeException
    //   124	131	141	java/lang/RuntimeException
    //   132	141	141	java/lang/RuntimeException
    //   82	116	207	finally
  }
  
  private void setPageSize()
  {
    if ((this.mConfiguration.isInMemoryDb()) || (this.mIsReadOnlyConnection)) {}
    long l;
    do
    {
      return;
      l = SQLiteGlobal.getDefaultPageSize();
    } while (executeForLong("PRAGMA page_size", null, null) == l);
    execute("PRAGMA page_size=" + l, null, null);
  }
  
  private void setSyncMode(String paramString)
  {
    if (!canonicalizeSyncMode(executeForString("PRAGMA synchronous", null, null)).equalsIgnoreCase(canonicalizeSyncMode(paramString))) {
      execute("PRAGMA synchronous=" + paramString, null, null);
    }
  }
  
  private void setWalModeFromConfiguration()
  {
    if ((this.mConfiguration.isInMemoryDb()) || (this.mIsReadOnlyConnection)) {
      return;
    }
    if ((this.mConfiguration.openFlags & 0x20000000) != 0)
    {
      setJournalMode("WAL");
      setSyncMode(SQLiteGlobal.getWALSyncMode());
      return;
    }
    setJournalMode(SQLiteGlobal.getDefaultJournalMode());
    setSyncMode(SQLiteGlobal.getDefaultSyncMode());
  }
  
  private void throwIfStatementForbidden(PreparedStatement paramPreparedStatement)
  {
    if ((!this.mOnlyAllowReadOnlyOperations) || (paramPreparedStatement.mReadOnly)) {
      return;
    }
    throw new SQLiteException("Cannot execute this statement because it might modify the database but the connection is read-only.");
  }
  
  private static String trimSqlForDisplay(String paramString)
  {
    return paramString.replaceAll("[\\s]*\\n+[\\s]*", " ");
  }
  
  void close()
  {
    dispose(false);
  }
  
  /* Error */
  void collectDbStats(ArrayList<SQLiteDebug.DbStats> paramArrayList)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 141	android/database/sqlite/SQLiteConnection:mConnectionPtr	J
    //   4: invokestatic 604	android/database/sqlite/SQLiteConnection:nativeGetDbLookaside	(J)I
    //   7: istore_2
    //   8: lconst_0
    //   9: lstore_3
    //   10: lconst_0
    //   11: lstore 7
    //   13: aload_0
    //   14: ldc_w 606
    //   17: aconst_null
    //   18: aconst_null
    //   19: invokevirtual 466	android/database/sqlite/SQLiteConnection:executeForLong	(Ljava/lang/String;[Ljava/lang/Object;Landroid/os/CancellationSignal;)J
    //   22: lstore 5
    //   24: lload 5
    //   26: lstore_3
    //   27: aload_0
    //   28: ldc_w 608
    //   31: aconst_null
    //   32: aconst_null
    //   33: invokevirtual 466	android/database/sqlite/SQLiteConnection:executeForLong	(Ljava/lang/String;[Ljava/lang/Object;Landroid/os/CancellationSignal;)J
    //   36: lstore 9
    //   38: lload 9
    //   40: lstore 7
    //   42: lload 5
    //   44: lstore_3
    //   45: aload_1
    //   46: aload_0
    //   47: iload_2
    //   48: lload_3
    //   49: lload 7
    //   51: invokespecial 610	android/database/sqlite/SQLiteConnection:getMainDbStatsUnsafe	(IJJ)Landroid/database/sqlite/SQLiteDebug$DbStats;
    //   54: invokevirtual 613	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   57: pop
    //   58: new 615	android/database/CursorWindow
    //   61: dup
    //   62: ldc_w 616
    //   65: invokespecial 617	android/database/CursorWindow:<init>	(Ljava/lang/String;)V
    //   68: astore 13
    //   70: aload_0
    //   71: ldc_w 619
    //   74: aconst_null
    //   75: aload 13
    //   77: iconst_0
    //   78: iconst_0
    //   79: iconst_0
    //   80: aconst_null
    //   81: invokevirtual 623	android/database/sqlite/SQLiteConnection:executeForCursorWindow	(Ljava/lang/String;[Ljava/lang/Object;Landroid/database/CursorWindow;IIZLandroid/os/CancellationSignal;)I
    //   84: pop
    //   85: iconst_1
    //   86: istore_2
    //   87: iload_2
    //   88: aload 13
    //   90: invokevirtual 626	android/database/CursorWindow:getNumRows	()I
    //   93: if_icmpge +197 -> 290
    //   96: aload 13
    //   98: iload_2
    //   99: iconst_1
    //   100: invokevirtual 630	android/database/CursorWindow:getString	(II)Ljava/lang/String;
    //   103: astore 11
    //   105: aload 13
    //   107: iload_2
    //   108: iconst_2
    //   109: invokevirtual 630	android/database/CursorWindow:getString	(II)Ljava/lang/String;
    //   112: astore 14
    //   114: lconst_0
    //   115: lstore_3
    //   116: lconst_0
    //   117: lstore 7
    //   119: aload_0
    //   120: new 225	java/lang/StringBuilder
    //   123: dup
    //   124: invokespecial 226	java/lang/StringBuilder:<init>	()V
    //   127: ldc_w 632
    //   130: invokevirtual 232	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   133: aload 11
    //   135: invokevirtual 232	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   138: ldc_w 634
    //   141: invokevirtual 232	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   144: invokevirtual 243	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   147: aconst_null
    //   148: aconst_null
    //   149: invokevirtual 466	android/database/sqlite/SQLiteConnection:executeForLong	(Ljava/lang/String;[Ljava/lang/Object;Landroid/os/CancellationSignal;)J
    //   152: lstore 5
    //   154: lload 5
    //   156: lstore_3
    //   157: aload_0
    //   158: new 225	java/lang/StringBuilder
    //   161: dup
    //   162: invokespecial 226	java/lang/StringBuilder:<init>	()V
    //   165: ldc_w 632
    //   168: invokevirtual 232	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   171: aload 11
    //   173: invokevirtual 232	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   176: ldc_w 636
    //   179: invokevirtual 232	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   182: invokevirtual 243	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   185: aconst_null
    //   186: aconst_null
    //   187: invokevirtual 466	android/database/sqlite/SQLiteConnection:executeForLong	(Ljava/lang/String;[Ljava/lang/Object;Landroid/os/CancellationSignal;)J
    //   190: lstore 9
    //   192: lload 9
    //   194: lstore 7
    //   196: lload 5
    //   198: lstore_3
    //   199: new 225	java/lang/StringBuilder
    //   202: dup
    //   203: invokespecial 226	java/lang/StringBuilder:<init>	()V
    //   206: ldc_w 638
    //   209: invokevirtual 232	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   212: aload 11
    //   214: invokevirtual 232	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   217: invokevirtual 243	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   220: astore 12
    //   222: aload 12
    //   224: astore 11
    //   226: aload 14
    //   228: invokevirtual 641	java/lang/String:isEmpty	()Z
    //   231: ifne +31 -> 262
    //   234: new 225	java/lang/StringBuilder
    //   237: dup
    //   238: invokespecial 226	java/lang/StringBuilder:<init>	()V
    //   241: aload 12
    //   243: invokevirtual 232	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   246: ldc_w 643
    //   249: invokevirtual 232	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   252: aload 14
    //   254: invokevirtual 232	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   257: invokevirtual 243	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   260: astore 11
    //   262: aload_1
    //   263: new 346	android/database/sqlite/SQLiteDebug$DbStats
    //   266: dup
    //   267: aload 11
    //   269: lload_3
    //   270: lload 7
    //   272: iconst_0
    //   273: iconst_0
    //   274: iconst_0
    //   275: iconst_0
    //   276: invokespecial 359	android/database/sqlite/SQLiteDebug$DbStats:<init>	(Ljava/lang/String;JJIIII)V
    //   279: invokevirtual 613	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   282: pop
    //   283: iload_2
    //   284: iconst_1
    //   285: iadd
    //   286: istore_2
    //   287: goto -200 -> 87
    //   290: aload 13
    //   292: invokevirtual 644	android/database/CursorWindow:close	()V
    //   295: return
    //   296: astore_1
    //   297: aload 13
    //   299: invokevirtual 644	android/database/CursorWindow:close	()V
    //   302: return
    //   303: astore_1
    //   304: aload 13
    //   306: invokevirtual 644	android/database/CursorWindow:close	()V
    //   309: aload_1
    //   310: athrow
    //   311: astore 12
    //   313: goto -114 -> 199
    //   316: astore 11
    //   318: goto -273 -> 45
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	321	0	this	SQLiteConnection
    //   0	321	1	paramArrayList	ArrayList<SQLiteDebug.DbStats>
    //   7	280	2	i	int
    //   9	261	3	l1	long
    //   22	175	5	l2	long
    //   11	260	7	l3	long
    //   36	157	9	l4	long
    //   103	165	11	localObject	Object
    //   316	1	11	localSQLiteException1	SQLiteException
    //   220	22	12	str1	String
    //   311	1	12	localSQLiteException2	SQLiteException
    //   68	237	13	localCursorWindow	android.database.CursorWindow
    //   112	141	14	str2	String
    // Exception table:
    //   from	to	target	type
    //   70	85	296	android/database/sqlite/SQLiteException
    //   87	114	296	android/database/sqlite/SQLiteException
    //   199	222	296	android/database/sqlite/SQLiteException
    //   226	262	296	android/database/sqlite/SQLiteException
    //   262	283	296	android/database/sqlite/SQLiteException
    //   70	85	303	finally
    //   87	114	303	finally
    //   119	154	303	finally
    //   157	192	303	finally
    //   199	222	303	finally
    //   226	262	303	finally
    //   262	283	303	finally
    //   119	154	311	android/database/sqlite/SQLiteException
    //   157	192	311	android/database/sqlite/SQLiteException
    //   13	24	316	android/database/sqlite/SQLiteException
    //   27	38	316	android/database/sqlite/SQLiteException
  }
  
  void collectDbStatsUnsafe(ArrayList<SQLiteDebug.DbStats> paramArrayList)
  {
    paramArrayList.add(getMainDbStatsUnsafe(0, 0L, 0L));
  }
  
  String describeCurrentOperationUnsafe()
  {
    return this.mRecentOperations.describeCurrentOperation();
  }
  
  public void dump(Printer paramPrinter, boolean paramBoolean)
  {
    dumpUnsafe(paramPrinter, paramBoolean);
  }
  
  void dumpUnsafe(Printer paramPrinter, boolean paramBoolean)
  {
    paramPrinter.println("Connection #" + this.mConnectionId + ":");
    if (paramBoolean) {
      paramPrinter.println("  connectionPtr: 0x" + Long.toHexString(this.mConnectionPtr));
    }
    paramPrinter.println("  isPrimaryConnection: " + this.mIsPrimaryConnection);
    paramPrinter.println("  onlyAllowReadOnlyOperations: " + this.mOnlyAllowReadOnlyOperations);
    this.mRecentOperations.dump(paramPrinter, paramBoolean);
    if (paramBoolean) {
      this.mPreparedStatementCache.dump(paramPrinter);
    }
  }
  
  /* Error */
  public void execute(String paramString, Object[] paramArrayOfObject, CancellationSignal paramCancellationSignal)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnonnull +14 -> 15
    //   4: new 687	java/lang/IllegalArgumentException
    //   7: dup
    //   8: ldc_w 689
    //   11: invokespecial 690	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   14: athrow
    //   15: aload_0
    //   16: getfield 97	android/database/sqlite/SQLiteConnection:mRecentOperations	Landroid/database/sqlite/SQLiteConnection$OperationLog;
    //   19: ldc_w 691
    //   22: aload_1
    //   23: aload_2
    //   24: invokevirtual 321	android/database/sqlite/SQLiteConnection$OperationLog:beginOperation	(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)I
    //   27: istore 4
    //   29: aload_0
    //   30: aload_1
    //   31: invokespecial 693	android/database/sqlite/SQLiteConnection:acquirePreparedStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteConnection$PreparedStatement;
    //   34: astore_1
    //   35: aload_0
    //   36: aload_1
    //   37: invokespecial 695	android/database/sqlite/SQLiteConnection:throwIfStatementForbidden	(Landroid/database/sqlite/SQLiteConnection$PreparedStatement;)V
    //   40: aload_0
    //   41: aload_1
    //   42: aload_2
    //   43: invokespecial 697	android/database/sqlite/SQLiteConnection:bindArguments	(Landroid/database/sqlite/SQLiteConnection$PreparedStatement;[Ljava/lang/Object;)V
    //   46: aload_0
    //   47: aload_1
    //   48: invokespecial 699	android/database/sqlite/SQLiteConnection:applyBlockGuardPolicy	(Landroid/database/sqlite/SQLiteConnection$PreparedStatement;)V
    //   51: aload_0
    //   52: aload_3
    //   53: invokespecial 701	android/database/sqlite/SQLiteConnection:attachCancellationSignal	(Landroid/os/CancellationSignal;)V
    //   56: aload_0
    //   57: getfield 141	android/database/sqlite/SQLiteConnection:mConnectionPtr	J
    //   60: aload_1
    //   61: getfield 248	android/database/sqlite/SQLiteConnection$PreparedStatement:mStatementPtr	J
    //   64: invokestatic 703	android/database/sqlite/SQLiteConnection:nativeExecute	(JJ)V
    //   67: aload_0
    //   68: aload_3
    //   69: invokespecial 705	android/database/sqlite/SQLiteConnection:detachCancellationSignal	(Landroid/os/CancellationSignal;)V
    //   72: aload_0
    //   73: aload_1
    //   74: invokespecial 707	android/database/sqlite/SQLiteConnection:releasePreparedStatement	(Landroid/database/sqlite/SQLiteConnection$PreparedStatement;)V
    //   77: aload_0
    //   78: getfield 97	android/database/sqlite/SQLiteConnection:mRecentOperations	Landroid/database/sqlite/SQLiteConnection$OperationLog;
    //   81: iload 4
    //   83: invokevirtual 332	android/database/sqlite/SQLiteConnection$OperationLog:endOperation	(I)V
    //   86: return
    //   87: astore_2
    //   88: aload_0
    //   89: aload_3
    //   90: invokespecial 705	android/database/sqlite/SQLiteConnection:detachCancellationSignal	(Landroid/os/CancellationSignal;)V
    //   93: aload_2
    //   94: athrow
    //   95: astore_2
    //   96: aload_0
    //   97: aload_1
    //   98: invokespecial 707	android/database/sqlite/SQLiteConnection:releasePreparedStatement	(Landroid/database/sqlite/SQLiteConnection$PreparedStatement;)V
    //   101: aload_2
    //   102: athrow
    //   103: astore_1
    //   104: aload_0
    //   105: getfield 97	android/database/sqlite/SQLiteConnection:mRecentOperations	Landroid/database/sqlite/SQLiteConnection$OperationLog;
    //   108: iload 4
    //   110: aload_1
    //   111: invokevirtual 711	android/database/sqlite/SQLiteConnection$OperationLog:failOperation	(ILjava/lang/Exception;)V
    //   114: aload_1
    //   115: athrow
    //   116: astore_1
    //   117: aload_0
    //   118: getfield 97	android/database/sqlite/SQLiteConnection:mRecentOperations	Landroid/database/sqlite/SQLiteConnection$OperationLog;
    //   121: iload 4
    //   123: invokevirtual 332	android/database/sqlite/SQLiteConnection$OperationLog:endOperation	(I)V
    //   126: aload_1
    //   127: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	128	0	this	SQLiteConnection
    //   0	128	1	paramString	String
    //   0	128	2	paramArrayOfObject	Object[]
    //   0	128	3	paramCancellationSignal	CancellationSignal
    //   27	95	4	i	int
    // Exception table:
    //   from	to	target	type
    //   56	67	87	finally
    //   35	56	95	finally
    //   67	72	95	finally
    //   88	95	95	finally
    //   29	35	103	java/lang/RuntimeException
    //   72	77	103	java/lang/RuntimeException
    //   96	103	103	java/lang/RuntimeException
    //   29	35	116	finally
    //   72	77	116	finally
    //   96	103	116	finally
    //   104	116	116	finally
  }
  
  /* Error */
  public android.os.ParcelFileDescriptor executeForBlobFileDescriptor(String paramString, Object[] paramArrayOfObject, CancellationSignal paramCancellationSignal)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 6
    //   3: aload_1
    //   4: ifnonnull +14 -> 18
    //   7: new 687	java/lang/IllegalArgumentException
    //   10: dup
    //   11: ldc_w 689
    //   14: invokespecial 690	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   17: athrow
    //   18: aload_0
    //   19: getfield 97	android/database/sqlite/SQLiteConnection:mRecentOperations	Landroid/database/sqlite/SQLiteConnection$OperationLog;
    //   22: ldc_w 714
    //   25: aload_1
    //   26: aload_2
    //   27: invokevirtual 321	android/database/sqlite/SQLiteConnection$OperationLog:beginOperation	(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)I
    //   30: istore 4
    //   32: aload_0
    //   33: aload_1
    //   34: invokespecial 693	android/database/sqlite/SQLiteConnection:acquirePreparedStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteConnection$PreparedStatement;
    //   37: astore 7
    //   39: aload_0
    //   40: aload 7
    //   42: invokespecial 695	android/database/sqlite/SQLiteConnection:throwIfStatementForbidden	(Landroid/database/sqlite/SQLiteConnection$PreparedStatement;)V
    //   45: aload_0
    //   46: aload 7
    //   48: aload_2
    //   49: invokespecial 697	android/database/sqlite/SQLiteConnection:bindArguments	(Landroid/database/sqlite/SQLiteConnection$PreparedStatement;[Ljava/lang/Object;)V
    //   52: aload_0
    //   53: aload 7
    //   55: invokespecial 699	android/database/sqlite/SQLiteConnection:applyBlockGuardPolicy	(Landroid/database/sqlite/SQLiteConnection$PreparedStatement;)V
    //   58: aload_0
    //   59: aload_3
    //   60: invokespecial 701	android/database/sqlite/SQLiteConnection:attachCancellationSignal	(Landroid/os/CancellationSignal;)V
    //   63: aload_0
    //   64: getfield 141	android/database/sqlite/SQLiteConnection:mConnectionPtr	J
    //   67: aload 7
    //   69: getfield 248	android/database/sqlite/SQLiteConnection$PreparedStatement:mStatementPtr	J
    //   72: invokestatic 716	android/database/sqlite/SQLiteConnection:nativeExecuteForBlobFileDescriptor	(JJ)I
    //   75: istore 5
    //   77: aload 6
    //   79: astore_1
    //   80: iload 5
    //   82: iflt +9 -> 91
    //   85: iload 5
    //   87: invokestatic 722	android/os/ParcelFileDescriptor:adoptFd	(I)Landroid/os/ParcelFileDescriptor;
    //   90: astore_1
    //   91: aload_0
    //   92: aload_3
    //   93: invokespecial 705	android/database/sqlite/SQLiteConnection:detachCancellationSignal	(Landroid/os/CancellationSignal;)V
    //   96: aload_0
    //   97: aload 7
    //   99: invokespecial 707	android/database/sqlite/SQLiteConnection:releasePreparedStatement	(Landroid/database/sqlite/SQLiteConnection$PreparedStatement;)V
    //   102: aload_0
    //   103: getfield 97	android/database/sqlite/SQLiteConnection:mRecentOperations	Landroid/database/sqlite/SQLiteConnection$OperationLog;
    //   106: iload 4
    //   108: invokevirtual 332	android/database/sqlite/SQLiteConnection$OperationLog:endOperation	(I)V
    //   111: aload_1
    //   112: areturn
    //   113: astore_1
    //   114: aload_0
    //   115: aload_3
    //   116: invokespecial 705	android/database/sqlite/SQLiteConnection:detachCancellationSignal	(Landroid/os/CancellationSignal;)V
    //   119: aload_1
    //   120: athrow
    //   121: astore_1
    //   122: aload_0
    //   123: aload 7
    //   125: invokespecial 707	android/database/sqlite/SQLiteConnection:releasePreparedStatement	(Landroid/database/sqlite/SQLiteConnection$PreparedStatement;)V
    //   128: aload_1
    //   129: athrow
    //   130: astore_1
    //   131: aload_0
    //   132: getfield 97	android/database/sqlite/SQLiteConnection:mRecentOperations	Landroid/database/sqlite/SQLiteConnection$OperationLog;
    //   135: iload 4
    //   137: aload_1
    //   138: invokevirtual 711	android/database/sqlite/SQLiteConnection$OperationLog:failOperation	(ILjava/lang/Exception;)V
    //   141: aload_1
    //   142: athrow
    //   143: astore_1
    //   144: aload_0
    //   145: getfield 97	android/database/sqlite/SQLiteConnection:mRecentOperations	Landroid/database/sqlite/SQLiteConnection$OperationLog;
    //   148: iload 4
    //   150: invokevirtual 332	android/database/sqlite/SQLiteConnection$OperationLog:endOperation	(I)V
    //   153: aload_1
    //   154: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	155	0	this	SQLiteConnection
    //   0	155	1	paramString	String
    //   0	155	2	paramArrayOfObject	Object[]
    //   0	155	3	paramCancellationSignal	CancellationSignal
    //   30	119	4	i	int
    //   75	11	5	j	int
    //   1	77	6	localObject	Object
    //   37	87	7	localPreparedStatement	PreparedStatement
    // Exception table:
    //   from	to	target	type
    //   63	77	113	finally
    //   85	91	113	finally
    //   39	63	121	finally
    //   91	96	121	finally
    //   114	121	121	finally
    //   32	39	130	java/lang/RuntimeException
    //   96	102	130	java/lang/RuntimeException
    //   122	130	130	java/lang/RuntimeException
    //   32	39	143	finally
    //   96	102	143	finally
    //   122	130	143	finally
    //   131	143	143	finally
  }
  
  /* Error */
  public int executeForChangedRowCount(String paramString, Object[] paramArrayOfObject, CancellationSignal paramCancellationSignal)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnonnull +14 -> 15
    //   4: new 687	java/lang/IllegalArgumentException
    //   7: dup
    //   8: ldc_w 689
    //   11: invokespecial 690	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   14: athrow
    //   15: iconst_0
    //   16: istore 4
    //   18: iconst_0
    //   19: istore 6
    //   21: iconst_0
    //   22: istore 7
    //   24: aload_0
    //   25: getfield 97	android/database/sqlite/SQLiteConnection:mRecentOperations	Landroid/database/sqlite/SQLiteConnection$OperationLog;
    //   28: ldc_w 725
    //   31: aload_1
    //   32: aload_2
    //   33: invokevirtual 321	android/database/sqlite/SQLiteConnection$OperationLog:beginOperation	(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)I
    //   36: istore 8
    //   38: aload_0
    //   39: aload_1
    //   40: invokespecial 693	android/database/sqlite/SQLiteConnection:acquirePreparedStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteConnection$PreparedStatement;
    //   43: astore_1
    //   44: iload 7
    //   46: istore 5
    //   48: aload_0
    //   49: aload_1
    //   50: invokespecial 695	android/database/sqlite/SQLiteConnection:throwIfStatementForbidden	(Landroid/database/sqlite/SQLiteConnection$PreparedStatement;)V
    //   53: iload 7
    //   55: istore 5
    //   57: aload_0
    //   58: aload_1
    //   59: aload_2
    //   60: invokespecial 697	android/database/sqlite/SQLiteConnection:bindArguments	(Landroid/database/sqlite/SQLiteConnection$PreparedStatement;[Ljava/lang/Object;)V
    //   63: iload 7
    //   65: istore 5
    //   67: aload_0
    //   68: aload_1
    //   69: invokespecial 699	android/database/sqlite/SQLiteConnection:applyBlockGuardPolicy	(Landroid/database/sqlite/SQLiteConnection$PreparedStatement;)V
    //   72: iload 7
    //   74: istore 5
    //   76: aload_0
    //   77: aload_3
    //   78: invokespecial 701	android/database/sqlite/SQLiteConnection:attachCancellationSignal	(Landroid/os/CancellationSignal;)V
    //   81: aload_0
    //   82: getfield 141	android/database/sqlite/SQLiteConnection:mConnectionPtr	J
    //   85: aload_1
    //   86: getfield 248	android/database/sqlite/SQLiteConnection$PreparedStatement:mStatementPtr	J
    //   89: invokestatic 727	android/database/sqlite/SQLiteConnection:nativeExecuteForChangedRowCount	(JJ)I
    //   92: istore 4
    //   94: iload 4
    //   96: istore 7
    //   98: iload 7
    //   100: istore 5
    //   102: aload_0
    //   103: aload_3
    //   104: invokespecial 705	android/database/sqlite/SQLiteConnection:detachCancellationSignal	(Landroid/os/CancellationSignal;)V
    //   107: iload 7
    //   109: istore 4
    //   111: iload 7
    //   113: istore 6
    //   115: aload_0
    //   116: aload_1
    //   117: invokespecial 707	android/database/sqlite/SQLiteConnection:releasePreparedStatement	(Landroid/database/sqlite/SQLiteConnection$PreparedStatement;)V
    //   120: aload_0
    //   121: getfield 97	android/database/sqlite/SQLiteConnection:mRecentOperations	Landroid/database/sqlite/SQLiteConnection$OperationLog;
    //   124: iload 8
    //   126: invokevirtual 730	android/database/sqlite/SQLiteConnection$OperationLog:endOperationDeferLog	(I)Z
    //   129: ifeq +33 -> 162
    //   132: aload_0
    //   133: getfield 97	android/database/sqlite/SQLiteConnection:mRecentOperations	Landroid/database/sqlite/SQLiteConnection$OperationLog;
    //   136: iload 8
    //   138: new 225	java/lang/StringBuilder
    //   141: dup
    //   142: invokespecial 226	java/lang/StringBuilder:<init>	()V
    //   145: ldc_w 732
    //   148: invokevirtual 232	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   151: iload 7
    //   153: invokevirtual 235	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   156: invokevirtual 243	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   159: invokevirtual 736	android/database/sqlite/SQLiteConnection$OperationLog:logOperation	(ILjava/lang/String;)V
    //   162: iload 7
    //   164: ireturn
    //   165: astore_2
    //   166: iload 7
    //   168: istore 5
    //   170: aload_0
    //   171: aload_3
    //   172: invokespecial 705	android/database/sqlite/SQLiteConnection:detachCancellationSignal	(Landroid/os/CancellationSignal;)V
    //   175: iload 7
    //   177: istore 5
    //   179: aload_2
    //   180: athrow
    //   181: astore_2
    //   182: iload 5
    //   184: istore 4
    //   186: iload 5
    //   188: istore 6
    //   190: aload_0
    //   191: aload_1
    //   192: invokespecial 707	android/database/sqlite/SQLiteConnection:releasePreparedStatement	(Landroid/database/sqlite/SQLiteConnection$PreparedStatement;)V
    //   195: iload 5
    //   197: istore 4
    //   199: iload 5
    //   201: istore 6
    //   203: aload_2
    //   204: athrow
    //   205: astore_1
    //   206: iload 4
    //   208: istore 6
    //   210: aload_0
    //   211: getfield 97	android/database/sqlite/SQLiteConnection:mRecentOperations	Landroid/database/sqlite/SQLiteConnection$OperationLog;
    //   214: iload 8
    //   216: aload_1
    //   217: invokevirtual 711	android/database/sqlite/SQLiteConnection$OperationLog:failOperation	(ILjava/lang/Exception;)V
    //   220: iload 4
    //   222: istore 6
    //   224: aload_1
    //   225: athrow
    //   226: astore_1
    //   227: aload_0
    //   228: getfield 97	android/database/sqlite/SQLiteConnection:mRecentOperations	Landroid/database/sqlite/SQLiteConnection$OperationLog;
    //   231: iload 8
    //   233: invokevirtual 730	android/database/sqlite/SQLiteConnection$OperationLog:endOperationDeferLog	(I)Z
    //   236: ifeq +33 -> 269
    //   239: aload_0
    //   240: getfield 97	android/database/sqlite/SQLiteConnection:mRecentOperations	Landroid/database/sqlite/SQLiteConnection$OperationLog;
    //   243: iload 8
    //   245: new 225	java/lang/StringBuilder
    //   248: dup
    //   249: invokespecial 226	java/lang/StringBuilder:<init>	()V
    //   252: ldc_w 732
    //   255: invokevirtual 232	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   258: iload 6
    //   260: invokevirtual 235	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   263: invokevirtual 243	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   266: invokevirtual 736	android/database/sqlite/SQLiteConnection$OperationLog:logOperation	(ILjava/lang/String;)V
    //   269: aload_1
    //   270: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	271	0	this	SQLiteConnection
    //   0	271	1	paramString	String
    //   0	271	2	paramArrayOfObject	Object[]
    //   0	271	3	paramCancellationSignal	CancellationSignal
    //   16	205	4	i	int
    //   46	154	5	j	int
    //   19	240	6	k	int
    //   22	154	7	m	int
    //   36	208	8	n	int
    // Exception table:
    //   from	to	target	type
    //   81	94	165	finally
    //   48	53	181	finally
    //   57	63	181	finally
    //   67	72	181	finally
    //   76	81	181	finally
    //   102	107	181	finally
    //   170	175	181	finally
    //   179	181	181	finally
    //   38	44	205	java/lang/RuntimeException
    //   115	120	205	java/lang/RuntimeException
    //   190	195	205	java/lang/RuntimeException
    //   203	205	205	java/lang/RuntimeException
    //   38	44	226	finally
    //   115	120	226	finally
    //   190	195	226	finally
    //   203	205	226	finally
    //   210	220	226	finally
    //   224	226	226	finally
  }
  
  /* Error */
  public int executeForCursorWindow(String paramString, Object[] paramArrayOfObject, android.database.CursorWindow paramCursorWindow, int paramInt1, int paramInt2, boolean paramBoolean, CancellationSignal paramCancellationSignal)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnonnull +14 -> 15
    //   4: new 687	java/lang/IllegalArgumentException
    //   7: dup
    //   8: ldc_w 689
    //   11: invokespecial 690	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   14: athrow
    //   15: aload_3
    //   16: ifnonnull +14 -> 30
    //   19: new 687	java/lang/IllegalArgumentException
    //   22: dup
    //   23: ldc_w 738
    //   26: invokespecial 690	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   29: athrow
    //   30: aload_3
    //   31: invokevirtual 741	android/database/CursorWindow:acquireReference	()V
    //   34: iconst_m1
    //   35: istore 19
    //   37: iconst_m1
    //   38: istore 12
    //   40: iconst_m1
    //   41: istore 11
    //   43: aload_0
    //   44: getfield 97	android/database/sqlite/SQLiteConnection:mRecentOperations	Landroid/database/sqlite/SQLiteConnection$OperationLog;
    //   47: ldc_w 742
    //   50: aload_1
    //   51: aload_2
    //   52: invokevirtual 321	android/database/sqlite/SQLiteConnection$OperationLog:beginOperation	(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)I
    //   55: istore 20
    //   57: iload 19
    //   59: istore 13
    //   61: iload 12
    //   63: istore 14
    //   65: iload 11
    //   67: istore 15
    //   69: iload 19
    //   71: istore 16
    //   73: iload 12
    //   75: istore 17
    //   77: iload 11
    //   79: istore 18
    //   81: aload_0
    //   82: aload_1
    //   83: invokespecial 693	android/database/sqlite/SQLiteConnection:acquirePreparedStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteConnection$PreparedStatement;
    //   86: astore_1
    //   87: iload 19
    //   89: istore 8
    //   91: iload 12
    //   93: istore 9
    //   95: iload 11
    //   97: istore 10
    //   99: aload_0
    //   100: aload_1
    //   101: invokespecial 695	android/database/sqlite/SQLiteConnection:throwIfStatementForbidden	(Landroid/database/sqlite/SQLiteConnection$PreparedStatement;)V
    //   104: iload 19
    //   106: istore 8
    //   108: iload 12
    //   110: istore 9
    //   112: iload 11
    //   114: istore 10
    //   116: aload_0
    //   117: aload_1
    //   118: aload_2
    //   119: invokespecial 697	android/database/sqlite/SQLiteConnection:bindArguments	(Landroid/database/sqlite/SQLiteConnection$PreparedStatement;[Ljava/lang/Object;)V
    //   122: iload 19
    //   124: istore 8
    //   126: iload 12
    //   128: istore 9
    //   130: iload 11
    //   132: istore 10
    //   134: aload_0
    //   135: aload_1
    //   136: invokespecial 699	android/database/sqlite/SQLiteConnection:applyBlockGuardPolicy	(Landroid/database/sqlite/SQLiteConnection$PreparedStatement;)V
    //   139: iload 19
    //   141: istore 8
    //   143: iload 12
    //   145: istore 9
    //   147: iload 11
    //   149: istore 10
    //   151: aload_0
    //   152: aload 7
    //   154: invokespecial 701	android/database/sqlite/SQLiteConnection:attachCancellationSignal	(Landroid/os/CancellationSignal;)V
    //   157: iload 12
    //   159: istore 13
    //   161: iload 11
    //   163: istore 14
    //   165: aload_0
    //   166: getfield 141	android/database/sqlite/SQLiteConnection:mConnectionPtr	J
    //   169: aload_1
    //   170: getfield 248	android/database/sqlite/SQLiteConnection$PreparedStatement:mStatementPtr	J
    //   173: aload_3
    //   174: getfield 745	android/database/CursorWindow:mWindowPtr	J
    //   177: iload 4
    //   179: iload 5
    //   181: iload 6
    //   183: invokestatic 747	android/database/sqlite/SQLiteConnection:nativeExecuteForCursorWindow	(JJJIIZ)J
    //   186: lstore 21
    //   188: lload 21
    //   190: bipush 32
    //   192: lshr
    //   193: l2i
    //   194: istore 5
    //   196: lload 21
    //   198: l2i
    //   199: istore 12
    //   201: iload 5
    //   203: istore 19
    //   205: iload 12
    //   207: istore 13
    //   209: iload 11
    //   211: istore 14
    //   213: aload_3
    //   214: invokevirtual 626	android/database/CursorWindow:getNumRows	()I
    //   217: istore 11
    //   219: iload 5
    //   221: istore 19
    //   223: iload 12
    //   225: istore 13
    //   227: iload 11
    //   229: istore 14
    //   231: aload_3
    //   232: iload 5
    //   234: invokevirtual 750	android/database/CursorWindow:setStartPosition	(I)V
    //   237: iload 5
    //   239: istore 8
    //   241: iload 12
    //   243: istore 9
    //   245: iload 11
    //   247: istore 10
    //   249: aload_0
    //   250: aload 7
    //   252: invokespecial 705	android/database/sqlite/SQLiteConnection:detachCancellationSignal	(Landroid/os/CancellationSignal;)V
    //   255: iload 5
    //   257: istore 13
    //   259: iload 12
    //   261: istore 14
    //   263: iload 11
    //   265: istore 15
    //   267: iload 5
    //   269: istore 16
    //   271: iload 12
    //   273: istore 17
    //   275: iload 11
    //   277: istore 18
    //   279: aload_0
    //   280: aload_1
    //   281: invokespecial 707	android/database/sqlite/SQLiteConnection:releasePreparedStatement	(Landroid/database/sqlite/SQLiteConnection$PreparedStatement;)V
    //   284: aload_0
    //   285: getfield 97	android/database/sqlite/SQLiteConnection:mRecentOperations	Landroid/database/sqlite/SQLiteConnection$OperationLog;
    //   288: iload 20
    //   290: invokevirtual 730	android/database/sqlite/SQLiteConnection$OperationLog:endOperationDeferLog	(I)Z
    //   293: ifeq +76 -> 369
    //   296: aload_0
    //   297: getfield 97	android/database/sqlite/SQLiteConnection:mRecentOperations	Landroid/database/sqlite/SQLiteConnection$OperationLog;
    //   300: iload 20
    //   302: new 225	java/lang/StringBuilder
    //   305: dup
    //   306: invokespecial 226	java/lang/StringBuilder:<init>	()V
    //   309: ldc_w 752
    //   312: invokevirtual 232	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   315: aload_3
    //   316: invokevirtual 755	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   319: ldc_w 757
    //   322: invokevirtual 232	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   325: iload 4
    //   327: invokevirtual 235	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   330: ldc_w 759
    //   333: invokevirtual 232	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   336: iload 5
    //   338: invokevirtual 235	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   341: ldc_w 761
    //   344: invokevirtual 232	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   347: iload 11
    //   349: invokevirtual 235	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   352: ldc_w 763
    //   355: invokevirtual 232	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   358: iload 12
    //   360: invokevirtual 235	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   363: invokevirtual 243	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   366: invokevirtual 736	android/database/sqlite/SQLiteConnection$OperationLog:logOperation	(ILjava/lang/String;)V
    //   369: aload_3
    //   370: invokevirtual 766	android/database/CursorWindow:releaseReference	()V
    //   373: iload 12
    //   375: ireturn
    //   376: astore_2
    //   377: iload 19
    //   379: istore 8
    //   381: iload 13
    //   383: istore 9
    //   385: iload 14
    //   387: istore 10
    //   389: aload_0
    //   390: aload 7
    //   392: invokespecial 705	android/database/sqlite/SQLiteConnection:detachCancellationSignal	(Landroid/os/CancellationSignal;)V
    //   395: iload 19
    //   397: istore 8
    //   399: iload 13
    //   401: istore 9
    //   403: iload 14
    //   405: istore 10
    //   407: aload_2
    //   408: athrow
    //   409: astore_2
    //   410: iload 8
    //   412: istore 13
    //   414: iload 9
    //   416: istore 14
    //   418: iload 10
    //   420: istore 15
    //   422: iload 8
    //   424: istore 16
    //   426: iload 9
    //   428: istore 17
    //   430: iload 10
    //   432: istore 18
    //   434: aload_0
    //   435: aload_1
    //   436: invokespecial 707	android/database/sqlite/SQLiteConnection:releasePreparedStatement	(Landroid/database/sqlite/SQLiteConnection$PreparedStatement;)V
    //   439: iload 8
    //   441: istore 13
    //   443: iload 9
    //   445: istore 14
    //   447: iload 10
    //   449: istore 15
    //   451: iload 8
    //   453: istore 16
    //   455: iload 9
    //   457: istore 17
    //   459: iload 10
    //   461: istore 18
    //   463: aload_2
    //   464: athrow
    //   465: astore_1
    //   466: iload 13
    //   468: istore 16
    //   470: iload 14
    //   472: istore 17
    //   474: iload 15
    //   476: istore 18
    //   478: aload_0
    //   479: getfield 97	android/database/sqlite/SQLiteConnection:mRecentOperations	Landroid/database/sqlite/SQLiteConnection$OperationLog;
    //   482: iload 20
    //   484: aload_1
    //   485: invokevirtual 711	android/database/sqlite/SQLiteConnection$OperationLog:failOperation	(ILjava/lang/Exception;)V
    //   488: iload 13
    //   490: istore 16
    //   492: iload 14
    //   494: istore 17
    //   496: iload 15
    //   498: istore 18
    //   500: aload_1
    //   501: athrow
    //   502: astore_1
    //   503: aload_0
    //   504: getfield 97	android/database/sqlite/SQLiteConnection:mRecentOperations	Landroid/database/sqlite/SQLiteConnection$OperationLog;
    //   507: iload 20
    //   509: invokevirtual 730	android/database/sqlite/SQLiteConnection$OperationLog:endOperationDeferLog	(I)Z
    //   512: ifeq +76 -> 588
    //   515: aload_0
    //   516: getfield 97	android/database/sqlite/SQLiteConnection:mRecentOperations	Landroid/database/sqlite/SQLiteConnection$OperationLog;
    //   519: iload 20
    //   521: new 225	java/lang/StringBuilder
    //   524: dup
    //   525: invokespecial 226	java/lang/StringBuilder:<init>	()V
    //   528: ldc_w 752
    //   531: invokevirtual 232	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   534: aload_3
    //   535: invokevirtual 755	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   538: ldc_w 757
    //   541: invokevirtual 232	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   544: iload 4
    //   546: invokevirtual 235	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   549: ldc_w 759
    //   552: invokevirtual 232	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   555: iload 16
    //   557: invokevirtual 235	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   560: ldc_w 761
    //   563: invokevirtual 232	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   566: iload 18
    //   568: invokevirtual 235	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   571: ldc_w 763
    //   574: invokevirtual 232	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   577: iload 17
    //   579: invokevirtual 235	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   582: invokevirtual 243	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   585: invokevirtual 736	android/database/sqlite/SQLiteConnection$OperationLog:logOperation	(ILjava/lang/String;)V
    //   588: aload_1
    //   589: athrow
    //   590: astore_1
    //   591: aload_3
    //   592: invokevirtual 766	android/database/CursorWindow:releaseReference	()V
    //   595: aload_1
    //   596: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	597	0	this	SQLiteConnection
    //   0	597	1	paramString	String
    //   0	597	2	paramArrayOfObject	Object[]
    //   0	597	3	paramCursorWindow	android.database.CursorWindow
    //   0	597	4	paramInt1	int
    //   0	597	5	paramInt2	int
    //   0	597	6	paramBoolean	boolean
    //   0	597	7	paramCancellationSignal	CancellationSignal
    //   89	363	8	i	int
    //   93	363	9	j	int
    //   97	363	10	k	int
    //   41	307	11	m	int
    //   38	336	12	n	int
    //   59	430	13	i1	int
    //   63	430	14	i2	int
    //   67	430	15	i3	int
    //   71	485	16	i4	int
    //   75	503	17	i5	int
    //   79	488	18	i6	int
    //   35	361	19	i7	int
    //   55	465	20	i8	int
    //   186	11	21	l	long
    // Exception table:
    //   from	to	target	type
    //   165	188	376	finally
    //   213	219	376	finally
    //   231	237	376	finally
    //   99	104	409	finally
    //   116	122	409	finally
    //   134	139	409	finally
    //   151	157	409	finally
    //   249	255	409	finally
    //   389	395	409	finally
    //   407	409	409	finally
    //   81	87	465	java/lang/RuntimeException
    //   279	284	465	java/lang/RuntimeException
    //   434	439	465	java/lang/RuntimeException
    //   463	465	465	java/lang/RuntimeException
    //   81	87	502	finally
    //   279	284	502	finally
    //   434	439	502	finally
    //   463	465	502	finally
    //   478	488	502	finally
    //   500	502	502	finally
    //   43	57	590	finally
    //   284	369	590	finally
    //   503	588	590	finally
    //   588	590	590	finally
  }
  
  /* Error */
  public long executeForLastInsertedRowId(String paramString, Object[] paramArrayOfObject, CancellationSignal paramCancellationSignal)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnonnull +14 -> 15
    //   4: new 687	java/lang/IllegalArgumentException
    //   7: dup
    //   8: ldc_w 689
    //   11: invokespecial 690	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   14: athrow
    //   15: aload_0
    //   16: getfield 97	android/database/sqlite/SQLiteConnection:mRecentOperations	Landroid/database/sqlite/SQLiteConnection$OperationLog;
    //   19: ldc_w 768
    //   22: aload_1
    //   23: aload_2
    //   24: invokevirtual 321	android/database/sqlite/SQLiteConnection$OperationLog:beginOperation	(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)I
    //   27: istore 4
    //   29: aload_0
    //   30: aload_1
    //   31: invokespecial 693	android/database/sqlite/SQLiteConnection:acquirePreparedStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteConnection$PreparedStatement;
    //   34: astore_1
    //   35: aload_0
    //   36: aload_1
    //   37: invokespecial 695	android/database/sqlite/SQLiteConnection:throwIfStatementForbidden	(Landroid/database/sqlite/SQLiteConnection$PreparedStatement;)V
    //   40: aload_0
    //   41: aload_1
    //   42: aload_2
    //   43: invokespecial 697	android/database/sqlite/SQLiteConnection:bindArguments	(Landroid/database/sqlite/SQLiteConnection$PreparedStatement;[Ljava/lang/Object;)V
    //   46: aload_0
    //   47: aload_1
    //   48: invokespecial 699	android/database/sqlite/SQLiteConnection:applyBlockGuardPolicy	(Landroid/database/sqlite/SQLiteConnection$PreparedStatement;)V
    //   51: aload_0
    //   52: aload_3
    //   53: invokespecial 701	android/database/sqlite/SQLiteConnection:attachCancellationSignal	(Landroid/os/CancellationSignal;)V
    //   56: aload_0
    //   57: getfield 141	android/database/sqlite/SQLiteConnection:mConnectionPtr	J
    //   60: aload_1
    //   61: getfield 248	android/database/sqlite/SQLiteConnection$PreparedStatement:mStatementPtr	J
    //   64: invokestatic 770	android/database/sqlite/SQLiteConnection:nativeExecuteForLastInsertedRowId	(JJ)J
    //   67: lstore 5
    //   69: aload_0
    //   70: aload_3
    //   71: invokespecial 705	android/database/sqlite/SQLiteConnection:detachCancellationSignal	(Landroid/os/CancellationSignal;)V
    //   74: aload_0
    //   75: aload_1
    //   76: invokespecial 707	android/database/sqlite/SQLiteConnection:releasePreparedStatement	(Landroid/database/sqlite/SQLiteConnection$PreparedStatement;)V
    //   79: aload_0
    //   80: getfield 97	android/database/sqlite/SQLiteConnection:mRecentOperations	Landroid/database/sqlite/SQLiteConnection$OperationLog;
    //   83: iload 4
    //   85: invokevirtual 332	android/database/sqlite/SQLiteConnection$OperationLog:endOperation	(I)V
    //   88: lload 5
    //   90: lreturn
    //   91: astore_2
    //   92: aload_0
    //   93: aload_3
    //   94: invokespecial 705	android/database/sqlite/SQLiteConnection:detachCancellationSignal	(Landroid/os/CancellationSignal;)V
    //   97: aload_2
    //   98: athrow
    //   99: astore_2
    //   100: aload_0
    //   101: aload_1
    //   102: invokespecial 707	android/database/sqlite/SQLiteConnection:releasePreparedStatement	(Landroid/database/sqlite/SQLiteConnection$PreparedStatement;)V
    //   105: aload_2
    //   106: athrow
    //   107: astore_1
    //   108: aload_0
    //   109: getfield 97	android/database/sqlite/SQLiteConnection:mRecentOperations	Landroid/database/sqlite/SQLiteConnection$OperationLog;
    //   112: iload 4
    //   114: aload_1
    //   115: invokevirtual 711	android/database/sqlite/SQLiteConnection$OperationLog:failOperation	(ILjava/lang/Exception;)V
    //   118: aload_1
    //   119: athrow
    //   120: astore_1
    //   121: aload_0
    //   122: getfield 97	android/database/sqlite/SQLiteConnection:mRecentOperations	Landroid/database/sqlite/SQLiteConnection$OperationLog;
    //   125: iload 4
    //   127: invokevirtual 332	android/database/sqlite/SQLiteConnection$OperationLog:endOperation	(I)V
    //   130: aload_1
    //   131: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	132	0	this	SQLiteConnection
    //   0	132	1	paramString	String
    //   0	132	2	paramArrayOfObject	Object[]
    //   0	132	3	paramCancellationSignal	CancellationSignal
    //   27	99	4	i	int
    //   67	22	5	l	long
    // Exception table:
    //   from	to	target	type
    //   56	69	91	finally
    //   35	56	99	finally
    //   69	74	99	finally
    //   92	99	99	finally
    //   29	35	107	java/lang/RuntimeException
    //   74	79	107	java/lang/RuntimeException
    //   100	107	107	java/lang/RuntimeException
    //   29	35	120	finally
    //   74	79	120	finally
    //   100	107	120	finally
    //   108	120	120	finally
  }
  
  /* Error */
  public long executeForLong(String paramString, Object[] paramArrayOfObject, CancellationSignal paramCancellationSignal)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnonnull +14 -> 15
    //   4: new 687	java/lang/IllegalArgumentException
    //   7: dup
    //   8: ldc_w 689
    //   11: invokespecial 690	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   14: athrow
    //   15: aload_0
    //   16: getfield 97	android/database/sqlite/SQLiteConnection:mRecentOperations	Landroid/database/sqlite/SQLiteConnection$OperationLog;
    //   19: ldc_w 771
    //   22: aload_1
    //   23: aload_2
    //   24: invokevirtual 321	android/database/sqlite/SQLiteConnection$OperationLog:beginOperation	(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)I
    //   27: istore 4
    //   29: aload_0
    //   30: aload_1
    //   31: invokespecial 693	android/database/sqlite/SQLiteConnection:acquirePreparedStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteConnection$PreparedStatement;
    //   34: astore_1
    //   35: aload_0
    //   36: aload_1
    //   37: invokespecial 695	android/database/sqlite/SQLiteConnection:throwIfStatementForbidden	(Landroid/database/sqlite/SQLiteConnection$PreparedStatement;)V
    //   40: aload_0
    //   41: aload_1
    //   42: aload_2
    //   43: invokespecial 697	android/database/sqlite/SQLiteConnection:bindArguments	(Landroid/database/sqlite/SQLiteConnection$PreparedStatement;[Ljava/lang/Object;)V
    //   46: aload_0
    //   47: aload_1
    //   48: invokespecial 699	android/database/sqlite/SQLiteConnection:applyBlockGuardPolicy	(Landroid/database/sqlite/SQLiteConnection$PreparedStatement;)V
    //   51: aload_0
    //   52: aload_3
    //   53: invokespecial 701	android/database/sqlite/SQLiteConnection:attachCancellationSignal	(Landroid/os/CancellationSignal;)V
    //   56: aload_0
    //   57: getfield 141	android/database/sqlite/SQLiteConnection:mConnectionPtr	J
    //   60: aload_1
    //   61: getfield 248	android/database/sqlite/SQLiteConnection$PreparedStatement:mStatementPtr	J
    //   64: invokestatic 773	android/database/sqlite/SQLiteConnection:nativeExecuteForLong	(JJ)J
    //   67: lstore 5
    //   69: aload_0
    //   70: aload_3
    //   71: invokespecial 705	android/database/sqlite/SQLiteConnection:detachCancellationSignal	(Landroid/os/CancellationSignal;)V
    //   74: aload_0
    //   75: aload_1
    //   76: invokespecial 707	android/database/sqlite/SQLiteConnection:releasePreparedStatement	(Landroid/database/sqlite/SQLiteConnection$PreparedStatement;)V
    //   79: aload_0
    //   80: getfield 97	android/database/sqlite/SQLiteConnection:mRecentOperations	Landroid/database/sqlite/SQLiteConnection$OperationLog;
    //   83: iload 4
    //   85: invokevirtual 332	android/database/sqlite/SQLiteConnection$OperationLog:endOperation	(I)V
    //   88: lload 5
    //   90: lreturn
    //   91: astore_2
    //   92: aload_0
    //   93: aload_3
    //   94: invokespecial 705	android/database/sqlite/SQLiteConnection:detachCancellationSignal	(Landroid/os/CancellationSignal;)V
    //   97: aload_2
    //   98: athrow
    //   99: astore_2
    //   100: aload_0
    //   101: aload_1
    //   102: invokespecial 707	android/database/sqlite/SQLiteConnection:releasePreparedStatement	(Landroid/database/sqlite/SQLiteConnection$PreparedStatement;)V
    //   105: aload_2
    //   106: athrow
    //   107: astore_1
    //   108: aload_0
    //   109: getfield 97	android/database/sqlite/SQLiteConnection:mRecentOperations	Landroid/database/sqlite/SQLiteConnection$OperationLog;
    //   112: iload 4
    //   114: aload_1
    //   115: invokevirtual 711	android/database/sqlite/SQLiteConnection$OperationLog:failOperation	(ILjava/lang/Exception;)V
    //   118: aload_1
    //   119: athrow
    //   120: astore_1
    //   121: aload_0
    //   122: getfield 97	android/database/sqlite/SQLiteConnection:mRecentOperations	Landroid/database/sqlite/SQLiteConnection$OperationLog;
    //   125: iload 4
    //   127: invokevirtual 332	android/database/sqlite/SQLiteConnection$OperationLog:endOperation	(I)V
    //   130: aload_1
    //   131: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	132	0	this	SQLiteConnection
    //   0	132	1	paramString	String
    //   0	132	2	paramArrayOfObject	Object[]
    //   0	132	3	paramCancellationSignal	CancellationSignal
    //   27	99	4	i	int
    //   67	22	5	l	long
    // Exception table:
    //   from	to	target	type
    //   56	69	91	finally
    //   35	56	99	finally
    //   69	74	99	finally
    //   92	99	99	finally
    //   29	35	107	java/lang/RuntimeException
    //   74	79	107	java/lang/RuntimeException
    //   100	107	107	java/lang/RuntimeException
    //   29	35	120	finally
    //   74	79	120	finally
    //   100	107	120	finally
    //   108	120	120	finally
  }
  
  /* Error */
  public String executeForString(String paramString, Object[] paramArrayOfObject, CancellationSignal paramCancellationSignal)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnonnull +14 -> 15
    //   4: new 687	java/lang/IllegalArgumentException
    //   7: dup
    //   8: ldc_w 689
    //   11: invokespecial 690	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   14: athrow
    //   15: aload_0
    //   16: getfield 97	android/database/sqlite/SQLiteConnection:mRecentOperations	Landroid/database/sqlite/SQLiteConnection$OperationLog;
    //   19: ldc_w 774
    //   22: aload_1
    //   23: aload_2
    //   24: invokevirtual 321	android/database/sqlite/SQLiteConnection$OperationLog:beginOperation	(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)I
    //   27: istore 4
    //   29: aload_0
    //   30: aload_1
    //   31: invokespecial 693	android/database/sqlite/SQLiteConnection:acquirePreparedStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteConnection$PreparedStatement;
    //   34: astore_1
    //   35: aload_0
    //   36: aload_1
    //   37: invokespecial 695	android/database/sqlite/SQLiteConnection:throwIfStatementForbidden	(Landroid/database/sqlite/SQLiteConnection$PreparedStatement;)V
    //   40: aload_0
    //   41: aload_1
    //   42: aload_2
    //   43: invokespecial 697	android/database/sqlite/SQLiteConnection:bindArguments	(Landroid/database/sqlite/SQLiteConnection$PreparedStatement;[Ljava/lang/Object;)V
    //   46: aload_0
    //   47: aload_1
    //   48: invokespecial 699	android/database/sqlite/SQLiteConnection:applyBlockGuardPolicy	(Landroid/database/sqlite/SQLiteConnection$PreparedStatement;)V
    //   51: aload_0
    //   52: aload_3
    //   53: invokespecial 701	android/database/sqlite/SQLiteConnection:attachCancellationSignal	(Landroid/os/CancellationSignal;)V
    //   56: aload_0
    //   57: getfield 141	android/database/sqlite/SQLiteConnection:mConnectionPtr	J
    //   60: aload_1
    //   61: getfield 248	android/database/sqlite/SQLiteConnection$PreparedStatement:mStatementPtr	J
    //   64: invokestatic 776	android/database/sqlite/SQLiteConnection:nativeExecuteForString	(JJ)Ljava/lang/String;
    //   67: astore_2
    //   68: aload_0
    //   69: aload_3
    //   70: invokespecial 705	android/database/sqlite/SQLiteConnection:detachCancellationSignal	(Landroid/os/CancellationSignal;)V
    //   73: aload_0
    //   74: aload_1
    //   75: invokespecial 707	android/database/sqlite/SQLiteConnection:releasePreparedStatement	(Landroid/database/sqlite/SQLiteConnection$PreparedStatement;)V
    //   78: aload_0
    //   79: getfield 97	android/database/sqlite/SQLiteConnection:mRecentOperations	Landroid/database/sqlite/SQLiteConnection$OperationLog;
    //   82: iload 4
    //   84: invokevirtual 332	android/database/sqlite/SQLiteConnection$OperationLog:endOperation	(I)V
    //   87: aload_2
    //   88: areturn
    //   89: astore_2
    //   90: aload_0
    //   91: aload_3
    //   92: invokespecial 705	android/database/sqlite/SQLiteConnection:detachCancellationSignal	(Landroid/os/CancellationSignal;)V
    //   95: aload_2
    //   96: athrow
    //   97: astore_2
    //   98: aload_0
    //   99: aload_1
    //   100: invokespecial 707	android/database/sqlite/SQLiteConnection:releasePreparedStatement	(Landroid/database/sqlite/SQLiteConnection$PreparedStatement;)V
    //   103: aload_2
    //   104: athrow
    //   105: astore_1
    //   106: aload_0
    //   107: getfield 97	android/database/sqlite/SQLiteConnection:mRecentOperations	Landroid/database/sqlite/SQLiteConnection$OperationLog;
    //   110: iload 4
    //   112: aload_1
    //   113: invokevirtual 711	android/database/sqlite/SQLiteConnection$OperationLog:failOperation	(ILjava/lang/Exception;)V
    //   116: aload_1
    //   117: athrow
    //   118: astore_1
    //   119: aload_0
    //   120: getfield 97	android/database/sqlite/SQLiteConnection:mRecentOperations	Landroid/database/sqlite/SQLiteConnection$OperationLog;
    //   123: iload 4
    //   125: invokevirtual 332	android/database/sqlite/SQLiteConnection$OperationLog:endOperation	(I)V
    //   128: aload_1
    //   129: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	130	0	this	SQLiteConnection
    //   0	130	1	paramString	String
    //   0	130	2	paramArrayOfObject	Object[]
    //   0	130	3	paramCancellationSignal	CancellationSignal
    //   27	97	4	i	int
    // Exception table:
    //   from	to	target	type
    //   56	68	89	finally
    //   35	56	97	finally
    //   68	73	97	finally
    //   90	97	97	finally
    //   29	35	105	java/lang/RuntimeException
    //   73	78	105	java/lang/RuntimeException
    //   98	105	105	java/lang/RuntimeException
    //   29	35	118	finally
    //   73	78	118	finally
    //   98	105	118	finally
    //   106	118	118	finally
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      if ((this.mPool != null) && (this.mConnectionPtr != 0L)) {
        this.mPool.onConnectionLeaked();
      }
      dispose(true);
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public int getConnectionId()
  {
    return this.mConnectionId;
  }
  
  boolean isPreparedStatementInCache(String paramString)
  {
    return this.mPreparedStatementCache.get(paramString) != null;
  }
  
  public boolean isPrimaryConnection()
  {
    return this.mIsPrimaryConnection;
  }
  
  public void onCancel()
  {
    nativeCancel(this.mConnectionPtr);
  }
  
  /* Error */
  public void prepare(String paramString, SQLiteStatementInfo paramSQLiteStatementInfo)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnonnull +14 -> 15
    //   4: new 687	java/lang/IllegalArgumentException
    //   7: dup
    //   8: ldc_w 689
    //   11: invokespecial 690	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   14: athrow
    //   15: aload_0
    //   16: getfield 97	android/database/sqlite/SQLiteConnection:mRecentOperations	Landroid/database/sqlite/SQLiteConnection$OperationLog;
    //   19: ldc_w 796
    //   22: aload_1
    //   23: aconst_null
    //   24: invokevirtual 321	android/database/sqlite/SQLiteConnection$OperationLog:beginOperation	(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)I
    //   27: istore 4
    //   29: aload_0
    //   30: aload_1
    //   31: invokespecial 693	android/database/sqlite/SQLiteConnection:acquirePreparedStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteConnection$PreparedStatement;
    //   34: astore_1
    //   35: aload_2
    //   36: ifnull +44 -> 80
    //   39: aload_2
    //   40: aload_1
    //   41: getfield 221	android/database/sqlite/SQLiteConnection$PreparedStatement:mNumParameters	I
    //   44: putfield 801	android/database/sqlite/SQLiteStatementInfo:numParameters	I
    //   47: aload_2
    //   48: aload_1
    //   49: getfield 185	android/database/sqlite/SQLiteConnection$PreparedStatement:mReadOnly	Z
    //   52: putfield 804	android/database/sqlite/SQLiteStatementInfo:readOnly	Z
    //   55: aload_0
    //   56: getfield 141	android/database/sqlite/SQLiteConnection:mConnectionPtr	J
    //   59: aload_1
    //   60: getfield 248	android/database/sqlite/SQLiteConnection$PreparedStatement:mStatementPtr	J
    //   63: invokestatic 806	android/database/sqlite/SQLiteConnection:nativeGetColumnCount	(JJ)I
    //   66: istore 5
    //   68: iload 5
    //   70: ifne +25 -> 95
    //   73: aload_2
    //   74: getstatic 80	android/database/sqlite/SQLiteConnection:EMPTY_STRING_ARRAY	[Ljava/lang/String;
    //   77: putfield 809	android/database/sqlite/SQLiteStatementInfo:columnNames	[Ljava/lang/String;
    //   80: aload_0
    //   81: aload_1
    //   82: invokespecial 707	android/database/sqlite/SQLiteConnection:releasePreparedStatement	(Landroid/database/sqlite/SQLiteConnection$PreparedStatement;)V
    //   85: aload_0
    //   86: getfield 97	android/database/sqlite/SQLiteConnection:mRecentOperations	Landroid/database/sqlite/SQLiteConnection$OperationLog;
    //   89: iload 4
    //   91: invokevirtual 332	android/database/sqlite/SQLiteConnection$OperationLog:endOperation	(I)V
    //   94: return
    //   95: aload_2
    //   96: iload 5
    //   98: anewarray 78	java/lang/String
    //   101: putfield 809	android/database/sqlite/SQLiteStatementInfo:columnNames	[Ljava/lang/String;
    //   104: iconst_0
    //   105: istore_3
    //   106: iload_3
    //   107: iload 5
    //   109: if_icmpge -29 -> 80
    //   112: aload_2
    //   113: getfield 809	android/database/sqlite/SQLiteStatementInfo:columnNames	[Ljava/lang/String;
    //   116: iload_3
    //   117: aload_0
    //   118: getfield 141	android/database/sqlite/SQLiteConnection:mConnectionPtr	J
    //   121: aload_1
    //   122: getfield 248	android/database/sqlite/SQLiteConnection$PreparedStatement:mStatementPtr	J
    //   125: iload_3
    //   126: invokestatic 811	android/database/sqlite/SQLiteConnection:nativeGetColumnName	(JJI)Ljava/lang/String;
    //   129: aastore
    //   130: iload_3
    //   131: iconst_1
    //   132: iadd
    //   133: istore_3
    //   134: goto -28 -> 106
    //   137: astore_2
    //   138: aload_0
    //   139: aload_1
    //   140: invokespecial 707	android/database/sqlite/SQLiteConnection:releasePreparedStatement	(Landroid/database/sqlite/SQLiteConnection$PreparedStatement;)V
    //   143: aload_2
    //   144: athrow
    //   145: astore_1
    //   146: aload_0
    //   147: getfield 97	android/database/sqlite/SQLiteConnection:mRecentOperations	Landroid/database/sqlite/SQLiteConnection$OperationLog;
    //   150: iload 4
    //   152: aload_1
    //   153: invokevirtual 711	android/database/sqlite/SQLiteConnection$OperationLog:failOperation	(ILjava/lang/Exception;)V
    //   156: aload_1
    //   157: athrow
    //   158: astore_1
    //   159: aload_0
    //   160: getfield 97	android/database/sqlite/SQLiteConnection:mRecentOperations	Landroid/database/sqlite/SQLiteConnection$OperationLog;
    //   163: iload 4
    //   165: invokevirtual 332	android/database/sqlite/SQLiteConnection$OperationLog:endOperation	(I)V
    //   168: aload_1
    //   169: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	170	0	this	SQLiteConnection
    //   0	170	1	paramString	String
    //   0	170	2	paramSQLiteStatementInfo	SQLiteStatementInfo
    //   105	29	3	i	int
    //   27	137	4	j	int
    //   66	44	5	k	int
    // Exception table:
    //   from	to	target	type
    //   39	68	137	finally
    //   73	80	137	finally
    //   95	104	137	finally
    //   112	130	137	finally
    //   29	35	145	java/lang/RuntimeException
    //   80	85	145	java/lang/RuntimeException
    //   138	145	145	java/lang/RuntimeException
    //   29	35	158	finally
    //   80	85	158	finally
    //   138	145	158	finally
    //   146	158	158	finally
  }
  
  void reconfigure(SQLiteDatabaseConfiguration paramSQLiteDatabaseConfiguration)
  {
    this.mOnlyAllowReadOnlyOperations = false;
    int j = paramSQLiteDatabaseConfiguration.customFunctions.size();
    int i = 0;
    while (i < j)
    {
      SQLiteCustomFunction localSQLiteCustomFunction = (SQLiteCustomFunction)paramSQLiteDatabaseConfiguration.customFunctions.get(i);
      if (!this.mConfiguration.customFunctions.contains(localSQLiteCustomFunction)) {
        nativeRegisterCustomFunction(this.mConnectionPtr, localSQLiteCustomFunction);
      }
      i += 1;
    }
    if (paramSQLiteDatabaseConfiguration.foreignKeyConstraintsEnabled != this.mConfiguration.foreignKeyConstraintsEnabled)
    {
      i = 1;
      if (((paramSQLiteDatabaseConfiguration.openFlags ^ this.mConfiguration.openFlags) & 0x20000000) == 0) {
        break label171;
      }
      j = 1;
      label101:
      if (!paramSQLiteDatabaseConfiguration.locale.equals(this.mConfiguration.locale)) {
        break label176;
      }
    }
    label171:
    label176:
    for (int k = 0;; k = 1)
    {
      this.mConfiguration.updateParametersFrom(paramSQLiteDatabaseConfiguration);
      this.mPreparedStatementCache.resize(paramSQLiteDatabaseConfiguration.maxSqlCacheSize);
      if (i != 0) {
        setForeignKeyModeFromConfiguration();
      }
      if (j != 0) {
        setWalModeFromConfiguration();
      }
      if (k != 0) {
        setLocaleFromConfiguration();
      }
      return;
      i = 0;
      break;
      j = 0;
      break label101;
    }
  }
  
  void setOnlyAllowReadOnlyOperations(boolean paramBoolean)
  {
    this.mOnlyAllowReadOnlyOperations = paramBoolean;
  }
  
  public String toString()
  {
    return "SQLiteConnection: " + this.mConfiguration.path + " (" + this.mConnectionId + ")";
  }
  
  private static final class Operation
  {
    private static final int MAX_TRACE_METHOD_NAME_LEN = 256;
    public ArrayList<Object> mBindArgs;
    public int mCookie;
    public long mEndTime;
    public Exception mException;
    public boolean mFinished;
    public String mKind;
    public String mSql;
    public long mStartTime;
    public long mStartWallTime;
    
    private String getFormattedStartTime()
    {
      return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(this.mStartWallTime));
    }
    
    private String getStatus()
    {
      if (!this.mFinished) {
        return "running";
      }
      if (this.mException != null) {
        return "failed";
      }
      return "succeeded";
    }
    
    private String getTraceMethodName()
    {
      String str = this.mKind + " " + this.mSql;
      if (str.length() > 256) {
        return str.substring(0, 256);
      }
      return str;
    }
    
    public void describe(StringBuilder paramStringBuilder, boolean paramBoolean)
    {
      paramStringBuilder.append(this.mKind);
      int i;
      label122:
      Object localObject;
      if (this.mFinished)
      {
        paramStringBuilder.append(" took ").append(this.mEndTime - this.mStartTime).append("ms");
        paramStringBuilder.append(" - ").append(getStatus());
        if (this.mSql != null) {
          paramStringBuilder.append(", sql=\"").append(SQLiteConnection.-wrap0(this.mSql)).append("\"");
        }
        if ((!paramBoolean) || (this.mBindArgs == null) || (this.mBindArgs.size() == 0)) {
          break label260;
        }
        paramStringBuilder.append(", bindArgs=[");
        int j = this.mBindArgs.size();
        i = 0;
        if (i >= j) {
          break label253;
        }
        localObject = this.mBindArgs.get(i);
        if (i != 0) {
          paramStringBuilder.append(", ");
        }
        if (localObject != null) {
          break label194;
        }
        paramStringBuilder.append("null");
      }
      for (;;)
      {
        i += 1;
        break label122;
        paramStringBuilder.append(" started ").append(System.currentTimeMillis() - this.mStartWallTime).append("ms ago");
        break;
        label194:
        if ((localObject instanceof byte[])) {
          paramStringBuilder.append("<byte[]>");
        } else if ((localObject instanceof String)) {
          paramStringBuilder.append("\"").append((String)localObject).append("\"");
        } else {
          paramStringBuilder.append(localObject);
        }
      }
      label253:
      paramStringBuilder.append("]");
      label260:
      if (this.mException != null) {
        paramStringBuilder.append(", exception=\"").append(this.mException.getMessage()).append("\"");
      }
    }
  }
  
  private static final class OperationLog
  {
    private static final int COOKIE_GENERATION_SHIFT = 8;
    private static final int COOKIE_INDEX_MASK = 255;
    private static final int MAX_RECENT_OPERATIONS = 20;
    private int mGeneration;
    private int mIndex;
    private final SQLiteConnection.Operation[] mOperations = new SQLiteConnection.Operation[20];
    
    private boolean endOperationDeferLogLocked(int paramInt)
    {
      boolean bool = false;
      SQLiteConnection.Operation localOperation = getOperationLocked(paramInt);
      if (localOperation != null)
      {
        if (Trace.isTagEnabled(1048576L)) {
          Trace.asyncTraceEnd(1048576L, SQLiteConnection.Operation.-wrap1(localOperation), localOperation.mCookie);
        }
        localOperation.mEndTime = SystemClock.uptimeMillis();
        localOperation.mFinished = true;
        if (SQLiteDebug.DEBUG_LOG_SLOW_QUERIES) {
          bool = SQLiteDebug.shouldLogSlowQuery(localOperation.mEndTime - localOperation.mStartTime);
        }
        return bool;
      }
      return false;
    }
    
    private SQLiteConnection.Operation getOperationLocked(int paramInt)
    {
      SQLiteConnection.Operation localOperation = this.mOperations[(paramInt & 0xFF)];
      if (localOperation.mCookie == paramInt) {
        return localOperation;
      }
      return null;
    }
    
    private void logOperationLocked(int paramInt, String paramString)
    {
      SQLiteConnection.Operation localOperation = getOperationLocked(paramInt);
      StringBuilder localStringBuilder = new StringBuilder();
      localOperation.describe(localStringBuilder, false);
      if (paramString != null) {
        localStringBuilder.append(", ").append(paramString);
      }
      Log.d("SQLiteConnection", localStringBuilder.toString());
    }
    
    private int newOperationCookieLocked(int paramInt)
    {
      int i = this.mGeneration;
      this.mGeneration = (i + 1);
      return i << 8 | paramInt;
    }
    
    public int beginOperation(String paramString1, String paramString2, Object[] paramArrayOfObject)
    {
      for (;;)
      {
        int j;
        SQLiteConnection.Operation localOperation1;
        synchronized (this.mOperations)
        {
          j = (this.mIndex + 1) % 20;
          SQLiteConnection.Operation localOperation2 = this.mOperations[j];
          if (localOperation2 == null)
          {
            localOperation1 = new SQLiteConnection.Operation(null);
            this.mOperations[j] = localOperation1;
            localOperation1.mStartWallTime = System.currentTimeMillis();
            localOperation1.mStartTime = SystemClock.uptimeMillis();
            localOperation1.mKind = paramString1;
            localOperation1.mSql = paramString2;
            if (paramArrayOfObject == null) {
              break label215;
            }
            if (localOperation1.mBindArgs == null)
            {
              localOperation1.mBindArgs = new ArrayList();
              break label270;
              if (i >= paramArrayOfObject.length) {
                break label215;
              }
              paramString1 = paramArrayOfObject[i];
              if ((paramString1 == null) || (!(paramString1 instanceof byte[]))) {
                break label202;
              }
              localOperation1.mBindArgs.add(SQLiteConnection.-get0());
              break label276;
            }
          }
          else
          {
            localOperation2.mFinished = false;
            localOperation2.mException = null;
            localOperation1 = localOperation2;
            if (localOperation2.mBindArgs == null) {
              continue;
            }
            localOperation2.mBindArgs.clear();
            localOperation1 = localOperation2;
          }
        }
        localOperation1.mBindArgs.clear();
        break label270;
        label202:
        localOperation1.mBindArgs.add(paramString1);
        break label276;
        label215:
        localOperation1.mCookie = newOperationCookieLocked(j);
        if (Trace.isTagEnabled(1048576L)) {
          Trace.asyncTraceBegin(1048576L, SQLiteConnection.Operation.-wrap1(localOperation1), localOperation1.mCookie);
        }
        this.mIndex = j;
        int i = localOperation1.mCookie;
        return i;
        label270:
        i = 0;
        continue;
        label276:
        i += 1;
      }
    }
    
    public String describeCurrentOperation()
    {
      synchronized (this.mOperations)
      {
        Object localObject1 = this.mOperations[this.mIndex];
        if (localObject1 != null)
        {
          boolean bool = ((SQLiteConnection.Operation)localObject1).mFinished;
          if (!bool) {}
        }
        else
        {
          return null;
        }
        StringBuilder localStringBuilder = new StringBuilder();
        ((SQLiteConnection.Operation)localObject1).describe(localStringBuilder, false);
        localObject1 = localStringBuilder.toString();
        return (String)localObject1;
      }
    }
    
    public void dump(Printer paramPrinter, boolean paramBoolean)
    {
      synchronized (this.mOperations)
      {
        paramPrinter.println("  Most recently executed operations:");
        int i = this.mIndex;
        SQLiteConnection.Operation localOperation = this.mOperations[i];
        if (localOperation != null)
        {
          int j = 0;
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append("    ").append(j).append(": [");
          localStringBuilder.append(SQLiteConnection.Operation.-wrap0(localOperation));
          localStringBuilder.append("] ");
          localOperation.describe(localStringBuilder, paramBoolean);
          paramPrinter.println(localStringBuilder.toString());
          if (i > 0) {
            i -= 1;
          }
          for (;;)
          {
            int k = j + 1;
            localOperation = this.mOperations[i];
            if (localOperation != null)
            {
              j = k;
              if (k < 20) {
                break;
              }
            }
            return;
            i = 19;
          }
        }
        paramPrinter.println("    <none>");
      }
    }
    
    public void endOperation(int paramInt)
    {
      synchronized (this.mOperations)
      {
        if (endOperationDeferLogLocked(paramInt)) {
          logOperationLocked(paramInt, null);
        }
        return;
      }
    }
    
    public boolean endOperationDeferLog(int paramInt)
    {
      synchronized (this.mOperations)
      {
        boolean bool = endOperationDeferLogLocked(paramInt);
        return bool;
      }
    }
    
    public void failOperation(int paramInt, Exception paramException)
    {
      synchronized (this.mOperations)
      {
        SQLiteConnection.Operation localOperation = getOperationLocked(paramInt);
        if (localOperation != null) {
          localOperation.mException = paramException;
        }
        return;
      }
    }
    
    public void logOperation(int paramInt, String paramString)
    {
      synchronized (this.mOperations)
      {
        logOperationLocked(paramInt, paramString);
        return;
      }
    }
  }
  
  private static final class PreparedStatement
  {
    public boolean mInCache;
    public boolean mInUse;
    public int mNumParameters;
    public PreparedStatement mPoolNext;
    public boolean mReadOnly;
    public String mSql;
    public long mStatementPtr;
    public int mType;
  }
  
  private final class PreparedStatementCache
    extends LruCache<String, SQLiteConnection.PreparedStatement>
  {
    public PreparedStatementCache(int paramInt)
    {
      super();
    }
    
    public void dump(Printer paramPrinter)
    {
      paramPrinter.println("  Prepared statement cache:");
      Object localObject1 = snapshot();
      if (!((Map)localObject1).isEmpty())
      {
        int i = 0;
        localObject1 = ((Map)localObject1).entrySet().iterator();
        while (((Iterator)localObject1).hasNext())
        {
          Object localObject2 = (Map.Entry)((Iterator)localObject1).next();
          SQLiteConnection.PreparedStatement localPreparedStatement = (SQLiteConnection.PreparedStatement)((Map.Entry)localObject2).getValue();
          if (localPreparedStatement.mInCache)
          {
            localObject2 = (String)((Map.Entry)localObject2).getKey();
            paramPrinter.println("    " + i + ": statementPtr=0x" + Long.toHexString(localPreparedStatement.mStatementPtr) + ", numParameters=" + localPreparedStatement.mNumParameters + ", type=" + localPreparedStatement.mType + ", readOnly=" + localPreparedStatement.mReadOnly + ", sql=\"" + SQLiteConnection.-wrap0((String)localObject2) + "\"");
          }
          i += 1;
        }
      }
      paramPrinter.println("    <none>");
    }
    
    protected void entryRemoved(boolean paramBoolean, String paramString, SQLiteConnection.PreparedStatement paramPreparedStatement1, SQLiteConnection.PreparedStatement paramPreparedStatement2)
    {
      paramPreparedStatement1.mInCache = false;
      if (!paramPreparedStatement1.mInUse) {
        SQLiteConnection.-wrap1(SQLiteConnection.this, paramPreparedStatement1);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/database/sqlite/SQLiteConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */