package com.android.server.pm;

import android.os.Environment;
import android.os.SystemClock;
import android.util.AtomicFile;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractStatsBase<T>
{
  private static final int WRITE_INTERVAL_MS;
  private final String mBackgroundThreadName;
  private final AtomicBoolean mBackgroundWriteRunning = new AtomicBoolean(false);
  private final Object mFileLock = new Object();
  private final String mFileName;
  private final AtomicLong mLastTimeWritten = new AtomicLong(0L);
  private final boolean mLock;
  
  static
  {
    if (PackageManagerService.DEBUG_DEXOPT) {}
    for (int i = 0;; i = 1800000)
    {
      WRITE_INTERVAL_MS = i;
      return;
    }
  }
  
  protected AbstractStatsBase(String paramString1, String paramString2, boolean paramBoolean)
  {
    this.mFileName = paramString1;
    this.mBackgroundThreadName = paramString2;
    this.mLock = paramBoolean;
  }
  
  private void writeImpl(T paramT)
  {
    if (this.mLock) {
      try
      {
        synchronized (this.mFileLock)
        {
          writeInternal(paramT);
          return;
        }
        synchronized (this.mFileLock)
        {
          writeInternal(paramT);
          return;
        }
      }
      finally {}
    }
  }
  
  protected AtomicFile getFile()
  {
    return new AtomicFile(new File(new File(Environment.getDataDirectory(), "system"), this.mFileName));
  }
  
  boolean maybeWriteAsync(final T paramT)
  {
    if ((SystemClock.elapsedRealtime() - this.mLastTimeWritten.get() >= WRITE_INTERVAL_MS) || (PackageManagerService.DEBUG_DEXOPT))
    {
      if (this.mBackgroundWriteRunning.compareAndSet(false, true))
      {
        new Thread(this.mBackgroundThreadName)
        {
          public void run()
          {
            try
            {
              AbstractStatsBase.-wrap0(AbstractStatsBase.this, paramT);
              AbstractStatsBase.-get1(AbstractStatsBase.this).set(SystemClock.elapsedRealtime());
              return;
            }
            finally
            {
              AbstractStatsBase.-get0(AbstractStatsBase.this).set(false);
            }
          }
        }.start();
        return true;
      }
    }
    else {
      return false;
    }
    return false;
  }
  
  void read(T paramT)
  {
    if (this.mLock) {
      try
      {
        for (;;)
        {
          synchronized (this.mFileLock)
          {
            readInternal(paramT);
            this.mLastTimeWritten.set(SystemClock.elapsedRealtime());
            return;
          }
          synchronized (this.mFileLock)
          {
            readInternal(paramT);
          }
        }
      }
      finally {}
    }
  }
  
  protected abstract void readInternal(T paramT);
  
  protected abstract void writeInternal(T paramT);
  
  void writeNow(T paramT)
  {
    writeImpl(paramT);
    this.mLastTimeWritten.set(SystemClock.elapsedRealtime());
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/AbstractStatsBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */