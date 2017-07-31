package android.database.sqlite;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.util.Log;
import java.io.File;

public abstract class SQLiteOpenHelper
{
  private static final boolean DEBUG_STRICT_READONLY = false;
  private static final String TAG = SQLiteOpenHelper.class.getSimpleName();
  private final Context mContext;
  private SQLiteDatabase mDatabase;
  private boolean mEnableWriteAheadLogging;
  private final DatabaseErrorHandler mErrorHandler;
  private final SQLiteDatabase.CursorFactory mFactory;
  private boolean mIsInitializing;
  private final String mName;
  private final int mNewVersion;
  
  public SQLiteOpenHelper(Context paramContext, String paramString, SQLiteDatabase.CursorFactory paramCursorFactory, int paramInt)
  {
    this(paramContext, paramString, paramCursorFactory, paramInt, null);
  }
  
  public SQLiteOpenHelper(Context paramContext, String paramString, SQLiteDatabase.CursorFactory paramCursorFactory, int paramInt, DatabaseErrorHandler paramDatabaseErrorHandler)
  {
    if (paramInt < 1) {
      throw new IllegalArgumentException("Version must be >= 1, was " + paramInt);
    }
    this.mContext = paramContext;
    this.mName = paramString;
    this.mFactory = paramCursorFactory;
    this.mNewVersion = paramInt;
    this.mErrorHandler = paramDatabaseErrorHandler;
  }
  
  private SQLiteDatabase getDatabaseLocked(boolean paramBoolean)
  {
    if (this.mDatabase != null)
    {
      if (this.mDatabase.isOpen()) {
        break label39;
      }
      this.mDatabase = null;
    }
    while (this.mIsInitializing)
    {
      throw new IllegalStateException("getDatabase called recursively");
      label39:
      if ((!paramBoolean) || (!this.mDatabase.isReadOnly())) {
        return this.mDatabase;
      }
    }
    SQLiteDatabase localSQLiteDatabase3 = this.mDatabase;
    Object localObject3 = localSQLiteDatabase3;
    int i;
    SQLiteDatabase localSQLiteDatabase2;
    for (;;)
    {
      try
      {
        this.mIsInitializing = true;
        if (localSQLiteDatabase3 != null)
        {
          SQLiteDatabase localSQLiteDatabase1 = localSQLiteDatabase3;
          if (paramBoolean)
          {
            localSQLiteDatabase1 = localSQLiteDatabase3;
            localObject3 = localSQLiteDatabase3;
            if (localSQLiteDatabase3.isReadOnly())
            {
              localObject3 = localSQLiteDatabase3;
              localSQLiteDatabase3.reopenReadWrite();
              localSQLiteDatabase1 = localSQLiteDatabase3;
            }
          }
          localObject3 = localSQLiteDatabase1;
          onConfigure(localSQLiteDatabase1);
          localObject3 = localSQLiteDatabase1;
          i = localSQLiteDatabase1.getVersion();
          localObject3 = localSQLiteDatabase1;
          if (i == this.mNewVersion) {
            break label436;
          }
          localObject3 = localSQLiteDatabase1;
          if (!localSQLiteDatabase1.isReadOnly()) {
            break label401;
          }
          localObject3 = localSQLiteDatabase1;
          throw new SQLiteException("Can't upgrade read-only database from version " + localSQLiteDatabase1.getVersion() + " to " + this.mNewVersion + ": " + this.mName);
        }
      }
      finally
      {
        this.mIsInitializing = false;
        if ((localObject3 != null) && (localObject3 != this.mDatabase)) {
          ((SQLiteDatabase)localObject3).close();
        }
      }
      localObject3 = localSQLiteDatabase3;
      Object localObject2;
      if (this.mName == null)
      {
        localObject3 = localSQLiteDatabase3;
        localObject2 = SQLiteDatabase.create(null);
      }
      else
      {
        localObject3 = localSQLiteDatabase3;
        try
        {
          localObject2 = this.mContext;
          localObject3 = localSQLiteDatabase3;
          String str = this.mName;
          localObject3 = localSQLiteDatabase3;
          if (this.mEnableWriteAheadLogging) {}
          for (i = 8;; i = 0)
          {
            localObject3 = localSQLiteDatabase3;
            localObject2 = ((Context)localObject2).openOrCreateDatabase(str, i, this.mFactory, this.mErrorHandler);
            break;
          }
        }
        catch (SQLiteException localSQLiteException)
        {
          if (paramBoolean)
          {
            localObject3 = localSQLiteDatabase3;
            throw localSQLiteException;
          }
          localObject3 = localSQLiteDatabase3;
          Log.e(TAG, "Couldn't open " + this.mName + " for writing (will try read-only):", localSQLiteException);
          localObject3 = localSQLiteDatabase3;
          localSQLiteDatabase2 = SQLiteDatabase.openDatabase(this.mContext.getDatabasePath(this.mName).getPath(), this.mFactory, 1, this.mErrorHandler);
        }
      }
    }
    label401:
    localObject3 = localSQLiteDatabase2;
    localSQLiteDatabase2.beginTransaction();
    if (i == 0) {}
    for (;;)
    {
      try
      {
        onCreate(localSQLiteDatabase2);
        localSQLiteDatabase2.setVersion(this.mNewVersion);
        localSQLiteDatabase2.setTransactionSuccessful();
        localObject3 = localSQLiteDatabase2;
        localSQLiteDatabase2.endTransaction();
        label436:
        localObject3 = localSQLiteDatabase2;
        onOpen(localSQLiteDatabase2);
        localObject3 = localSQLiteDatabase2;
        if (localSQLiteDatabase2.isReadOnly())
        {
          localObject3 = localSQLiteDatabase2;
          Log.w(TAG, "Opened " + this.mName + " in read-only mode");
        }
        localObject3 = localSQLiteDatabase2;
        this.mDatabase = localSQLiteDatabase2;
        this.mIsInitializing = false;
        if ((localSQLiteDatabase2 != null) && (localSQLiteDatabase2 != this.mDatabase)) {
          localSQLiteDatabase2.close();
        }
        return localSQLiteDatabase2;
      }
      finally
      {
        localObject3 = localSQLiteDatabase2;
        localSQLiteDatabase2.endTransaction();
        localObject3 = localSQLiteDatabase2;
      }
      if (i > this.mNewVersion) {
        onDowngrade(localSQLiteDatabase2, i, this.mNewVersion);
      } else {
        onUpgrade(localSQLiteDatabase2, i, this.mNewVersion);
      }
    }
  }
  
  public void close()
  {
    try
    {
      if (this.mIsInitializing) {
        throw new IllegalStateException("Closed during initialization");
      }
    }
    finally {}
    if ((this.mDatabase != null) && (this.mDatabase.isOpen()))
    {
      this.mDatabase.close();
      this.mDatabase = null;
    }
  }
  
  public String getDatabaseName()
  {
    return this.mName;
  }
  
  public SQLiteDatabase getReadableDatabase()
  {
    try
    {
      SQLiteDatabase localSQLiteDatabase = getDatabaseLocked(false);
      return localSQLiteDatabase;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public SQLiteDatabase getWritableDatabase()
  {
    try
    {
      SQLiteDatabase localSQLiteDatabase = getDatabaseLocked(true);
      return localSQLiteDatabase;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void onConfigure(SQLiteDatabase paramSQLiteDatabase) {}
  
  public abstract void onCreate(SQLiteDatabase paramSQLiteDatabase);
  
  public void onDowngrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
  {
    throw new SQLiteException("Can't downgrade database from version " + paramInt1 + " to " + paramInt2);
  }
  
  public void onOpen(SQLiteDatabase paramSQLiteDatabase) {}
  
  public abstract void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2);
  
  public void setWriteAheadLoggingEnabled(boolean paramBoolean)
  {
    for (;;)
    {
      try
      {
        if (this.mEnableWriteAheadLogging != paramBoolean)
        {
          if ((this.mDatabase == null) || (!this.mDatabase.isOpen()) || (this.mDatabase.isReadOnly())) {
            this.mEnableWriteAheadLogging = paramBoolean;
          }
        }
        else {
          return;
        }
        if (paramBoolean) {
          this.mDatabase.enableWriteAheadLogging();
        } else {
          this.mDatabase.disableWriteAheadLogging();
        }
      }
      finally {}
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/database/sqlite/SQLiteOpenHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */