package android.os;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class UEventObserver
{
  private static final boolean DEBUG = false;
  private static final String TAG = "UEventObserver";
  private static UEventThread sThread;
  
  private static UEventThread getThread()
  {
    try
    {
      if (sThread == null)
      {
        sThread = new UEventThread();
        sThread.start();
      }
      UEventThread localUEventThread = sThread;
      return localUEventThread;
    }
    finally {}
  }
  
  private static native void nativeAddMatch(String paramString);
  
  private static native void nativeRemoveMatch(String paramString);
  
  private static native void nativeSetup();
  
  private static native String nativeWaitForNextEvent();
  
  private static UEventThread peekThread()
  {
    try
    {
      UEventThread localUEventThread = sThread;
      return localUEventThread;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      stopObserving();
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public abstract void onUEvent(UEvent paramUEvent);
  
  public final void startObserving(String paramString)
  {
    if ((paramString == null) || (paramString.isEmpty())) {
      throw new IllegalArgumentException("match substring must be non-empty");
    }
    getThread().addObserver(paramString, this);
  }
  
  public final void stopObserving()
  {
    UEventThread localUEventThread = getThread();
    if (localUEventThread != null) {
      localUEventThread.removeObserver(this);
    }
  }
  
  public static final class UEvent
  {
    private final HashMap<String, String> mMap = new HashMap();
    
    public UEvent(String paramString)
    {
      int i = 0;
      int j = paramString.length();
      for (;;)
      {
        int k;
        int m;
        if (i < j)
        {
          k = paramString.indexOf('=', i);
          m = paramString.indexOf(0, i);
          if (m >= 0) {}
        }
        else
        {
          return;
        }
        if ((k > i) && (k < m)) {
          this.mMap.put(paramString.substring(i, k), paramString.substring(k + 1, m));
        }
        i = m + 1;
      }
    }
    
    public String get(String paramString)
    {
      return (String)this.mMap.get(paramString);
    }
    
    public String get(String paramString1, String paramString2)
    {
      paramString1 = (String)this.mMap.get(paramString1);
      if (paramString1 == null) {
        return paramString2;
      }
      return paramString1;
    }
    
    public String toString()
    {
      return this.mMap.toString();
    }
  }
  
  private static final class UEventThread
    extends Thread
  {
    private final ArrayList<Object> mKeysAndObservers = new ArrayList();
    private final ArrayList<UEventObserver> mTempObserversToSignal = new ArrayList();
    
    public UEventThread()
    {
      super();
    }
    
    private void sendEvent(String paramString)
    {
      synchronized (this.mKeysAndObservers)
      {
        int j = this.mKeysAndObservers.size();
        int i = 0;
        while (i < j)
        {
          if (paramString.contains((String)this.mKeysAndObservers.get(i)))
          {
            UEventObserver localUEventObserver = (UEventObserver)this.mKeysAndObservers.get(i + 1);
            this.mTempObserversToSignal.add(localUEventObserver);
          }
          i += 2;
        }
        if (this.mTempObserversToSignal.isEmpty()) {
          return;
        }
        paramString = new UEventObserver.UEvent(paramString);
        j = this.mTempObserversToSignal.size();
        i = 0;
        if (i < j)
        {
          ((UEventObserver)this.mTempObserversToSignal.get(i)).onUEvent(paramString);
          i += 1;
        }
      }
      this.mTempObserversToSignal.clear();
    }
    
    public void addObserver(String paramString, UEventObserver paramUEventObserver)
    {
      synchronized (this.mKeysAndObservers)
      {
        this.mKeysAndObservers.add(paramString);
        this.mKeysAndObservers.add(paramUEventObserver);
        UEventObserver.-wrap1(paramString);
        return;
      }
    }
    
    public void removeObserver(UEventObserver paramUEventObserver)
    {
      ArrayList localArrayList = this.mKeysAndObservers;
      int i = 0;
      try
      {
        for (;;)
        {
          if (i >= this.mKeysAndObservers.size()) {
            break label74;
          }
          if (this.mKeysAndObservers.get(i + 1) != paramUEventObserver) {
            break;
          }
          this.mKeysAndObservers.remove(i + 1);
          UEventObserver.-wrap2((String)this.mKeysAndObservers.remove(i));
        }
      }
      finally {}
      label74:
    }
    
    public void run()
    {
      
      for (;;)
      {
        String str = UEventObserver.-wrap0();
        if (str != null) {
          sendEvent(str);
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/UEventObserver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */