package android.support.v4.util;

public final class Pools
{
  public static abstract interface Pool<T>
  {
    public abstract T acquire();
    
    public abstract boolean release(T paramT);
  }
  
  public static class SimplePool<T>
    implements Pools.Pool<T>
  {
    private final Object[] mPool;
    private int mPoolSize;
    
    public SimplePool(int paramInt)
    {
      if (paramInt > 0)
      {
        this.mPool = new Object[paramInt];
        return;
      }
      throw new IllegalArgumentException("The max pool size must be > 0");
    }
    
    private boolean isInPool(T paramT)
    {
      int i = 0;
      for (;;)
      {
        if (i >= this.mPoolSize) {
          return false;
        }
        if (this.mPool[i] == paramT) {
          break;
        }
        i += 1;
      }
      return true;
    }
    
    public T acquire()
    {
      if (this.mPoolSize <= 0) {
        return null;
      }
      int i = this.mPoolSize - 1;
      Object localObject = this.mPool[i];
      this.mPool[i] = null;
      this.mPoolSize -= 1;
      return (T)localObject;
    }
    
    public boolean release(T paramT)
    {
      if (!isInPool(paramT))
      {
        if (this.mPoolSize >= this.mPool.length) {
          return false;
        }
      }
      else {
        throw new IllegalStateException("Already in the pool!");
      }
      this.mPool[this.mPoolSize] = paramT;
      this.mPoolSize += 1;
      return true;
    }
  }
  
  public static class SynchronizedPool<T>
    extends Pools.SimplePool<T>
  {
    private final Object mLock = new Object();
    
    public SynchronizedPool(int paramInt)
    {
      super();
    }
    
    public T acquire()
    {
      synchronized (this.mLock)
      {
        Object localObject2 = super.acquire();
        return (T)localObject2;
      }
    }
    
    public boolean release(T paramT)
    {
      synchronized (this.mLock)
      {
        boolean bool = super.release(paramT);
        return bool;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/util/Pools.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */