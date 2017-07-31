package android.os;

import android.util.Log;
import java.lang.ref.WeakReference;
import java.util.HashMap;

public abstract class FileObserver
{
  public static final int ACCESS = 1;
  public static final int ALL_EVENTS = 4095;
  public static final int ATTRIB = 4;
  public static final int CLOSE_NOWRITE = 16;
  public static final int CLOSE_WRITE = 8;
  public static final int CREATE = 256;
  public static final int DELETE = 512;
  public static final int DELETE_SELF = 1024;
  private static final String LOG_TAG = "FileObserver";
  public static final int MODIFY = 2;
  public static final int MOVED_FROM = 64;
  public static final int MOVED_TO = 128;
  public static final int MOVE_SELF = 2048;
  public static final int OPEN = 32;
  private static ObserverThread s_observerThread = new ObserverThread();
  private Integer m_descriptor;
  private int m_mask;
  private String m_path;
  
  static
  {
    s_observerThread.start();
  }
  
  public FileObserver(String paramString)
  {
    this(paramString, 4095);
  }
  
  public FileObserver(String paramString, int paramInt)
  {
    this.m_path = paramString;
    this.m_mask = paramInt;
    this.m_descriptor = Integer.valueOf(-1);
  }
  
  protected void finalize()
  {
    stopWatching();
  }
  
  public abstract void onEvent(int paramInt, String paramString);
  
  public void startWatching()
  {
    if (this.m_descriptor.intValue() < 0) {
      this.m_descriptor = Integer.valueOf(s_observerThread.startWatching(this.m_path, this.m_mask, this));
    }
  }
  
  public void stopWatching()
  {
    if (this.m_descriptor.intValue() >= 0)
    {
      s_observerThread.stopWatching(this.m_descriptor.intValue());
      this.m_descriptor = Integer.valueOf(-1);
    }
  }
  
  private static class ObserverThread
    extends Thread
  {
    private int m_fd = init();
    private HashMap<Integer, WeakReference> m_observers = new HashMap();
    
    public ObserverThread()
    {
      super();
    }
    
    private native int init();
    
    private native void observe(int paramInt);
    
    private native int startWatching(int paramInt1, String paramString, int paramInt2);
    
    private native void stopWatching(int paramInt1, int paramInt2);
    
    public void onEvent(int paramInt1, int paramInt2, String paramString)
    {
      Object localObject1 = null;
      synchronized (this.m_observers)
      {
        Object localObject2 = (WeakReference)this.m_observers.get(Integer.valueOf(paramInt1));
        if (localObject2 != null)
        {
          localObject2 = (FileObserver)((WeakReference)localObject2).get();
          localObject1 = localObject2;
          if (localObject2 == null)
          {
            this.m_observers.remove(Integer.valueOf(paramInt1));
            localObject1 = localObject2;
          }
        }
        if (localObject1 == null) {}
      }
      try
      {
        ((FileObserver)localObject1).onEvent(paramInt2, paramString);
        return;
      }
      catch (Throwable paramString)
      {
        Log.wtf("FileObserver", "Unhandled exception in FileObserver " + localObject1, paramString);
      }
      paramString = finally;
      throw paramString;
    }
    
    public void run()
    {
      observe(this.m_fd);
    }
    
    public int startWatching(String arg1, int paramInt, FileObserver paramFileObserver)
    {
      paramInt = startWatching(this.m_fd, ???, paramInt);
      Integer localInteger = new Integer(paramInt);
      if (paramInt >= 0) {}
      synchronized (this.m_observers)
      {
        this.m_observers.put(localInteger, new WeakReference(paramFileObserver));
        return localInteger.intValue();
      }
    }
    
    public void stopWatching(int paramInt)
    {
      stopWatching(this.m_fd, paramInt);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/FileObserver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */