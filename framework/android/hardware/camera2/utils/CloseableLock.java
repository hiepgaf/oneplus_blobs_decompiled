package android.hardware.camera2.utils;

import android.util.Log;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CloseableLock
  implements AutoCloseable
{
  private static final boolean VERBOSE = false;
  private final String TAG = "CloseableLock";
  private volatile boolean mClosed = false;
  private final Condition mCondition = this.mLock.newCondition();
  private boolean mExclusive = false;
  private final ReentrantLock mLock = new ReentrantLock();
  private final ThreadLocal<Integer> mLockCount = new ThreadLocal()
  {
    protected Integer initialValue()
    {
      return Integer.valueOf(0);
    }
  };
  private final String mName;
  private int mSharedLocks = 0;
  
  public CloseableLock()
  {
    this.mName = "";
  }
  
  public CloseableLock(String paramString)
  {
    this.mName = paramString;
  }
  
  private void log(String paramString)
  {
    Log.v("CloseableLock[" + this.mName + "]", paramString);
  }
  
  public ScopedLock acquireExclusiveLock()
  {
    boolean bool;
    try
    {
      this.mLock.lock();
      bool = this.mClosed;
      if (bool) {
        return null;
      }
      i = ((Integer)this.mLockCount.get()).intValue();
      if ((!this.mExclusive) && (i > 0)) {
        throw new IllegalStateException("Cannot acquire exclusive lock while holding shared lock");
      }
    }
    finally
    {
      this.mLock.unlock();
    }
    while ((i == 0) && ((this.mExclusive) || (this.mSharedLocks > 0)))
    {
      this.mCondition.awaitUninterruptibly();
      bool = this.mClosed;
      if (bool)
      {
        this.mLock.unlock();
        return null;
      }
    }
    this.mExclusive = true;
    int i = ((Integer)this.mLockCount.get()).intValue();
    this.mLockCount.set(Integer.valueOf(i + 1));
    this.mLock.unlock();
    return new ScopedLock(null);
  }
  
  public ScopedLock acquireLock()
  {
    boolean bool;
    try
    {
      this.mLock.lock();
      bool = this.mClosed;
      if (bool) {
        return null;
      }
      i = ((Integer)this.mLockCount.get()).intValue();
      if ((this.mExclusive) && (i > 0)) {
        throw new IllegalStateException("Cannot acquire shared lock while holding exclusive lock");
      }
    }
    finally
    {
      this.mLock.unlock();
    }
    while (this.mExclusive)
    {
      this.mCondition.awaitUninterruptibly();
      bool = this.mClosed;
      if (bool)
      {
        this.mLock.unlock();
        return null;
      }
    }
    this.mSharedLocks += 1;
    int i = ((Integer)this.mLockCount.get()).intValue();
    this.mLockCount.set(Integer.valueOf(i + 1));
    this.mLock.unlock();
    return new ScopedLock(null);
  }
  
  public void close()
  {
    if (this.mClosed) {
      return;
    }
    if (acquireExclusiveLock() == null) {
      return;
    }
    if (((Integer)this.mLockCount.get()).intValue() != 1) {
      throw new IllegalStateException("Cannot close while one or more acquired locks are being held by this thread; release all other locks first");
    }
    try
    {
      this.mLock.lock();
      this.mClosed = true;
      this.mExclusive = false;
      this.mSharedLocks = 0;
      this.mLockCount.remove();
      this.mCondition.signalAll();
      return;
    }
    finally
    {
      this.mLock.unlock();
    }
  }
  
  public void releaseLock()
  {
    if (((Integer)this.mLockCount.get()).intValue() <= 0) {
      throw new IllegalStateException("Cannot release lock that was not acquired by this thread");
    }
    try
    {
      this.mLock.lock();
      if (this.mClosed) {
        throw new IllegalStateException("Do not release after the lock has been closed");
      }
    }
    finally
    {
      this.mLock.unlock();
    }
    int i;
    if (!this.mExclusive)
    {
      this.mSharedLocks -= 1;
      i = ((Integer)this.mLockCount.get()).intValue() - 1;
      this.mLockCount.set(Integer.valueOf(i));
      if ((i != 0) || (!this.mExclusive)) {
        break label174;
      }
      this.mExclusive = false;
      this.mCondition.signalAll();
    }
    for (;;)
    {
      this.mLock.unlock();
      return;
      if (this.mSharedLocks == 0) {
        break;
      }
      throw new AssertionError("Too many shared locks " + this.mSharedLocks);
      label174:
      if ((i == 0) && (this.mSharedLocks == 0)) {
        this.mCondition.signalAll();
      }
    }
  }
  
  public class ScopedLock
    implements AutoCloseable
  {
    private ScopedLock() {}
    
    public void close()
    {
      CloseableLock.this.releaseLock();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/utils/CloseableLock.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */