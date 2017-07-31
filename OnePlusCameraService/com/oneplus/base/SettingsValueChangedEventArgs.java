package com.oneplus.base;

import java.util.ArrayDeque;
import java.util.Queue;

public class SettingsValueChangedEventArgs
  extends EventArgs
{
  private static final Queue<SettingsValueChangedEventArgs> POOL = new ArrayDeque(8);
  private static final int POOL_SIZE = 8;
  private volatile String m_Key;
  
  private SettingsValueChangedEventArgs(String paramString)
  {
    this.m_Key = paramString;
  }
  
  /* Error */
  static SettingsValueChangedEventArgs obtain(String paramString)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 22	com/oneplus/base/SettingsValueChangedEventArgs:POOL	Ljava/util/Queue;
    //   6: invokeinterface 36 1 0
    //   11: checkcast 2	com/oneplus/base/SettingsValueChangedEventArgs
    //   14: astore_1
    //   15: aload_1
    //   16: ifnull +15 -> 31
    //   19: aload_1
    //   20: aload_0
    //   21: putfield 28	com/oneplus/base/SettingsValueChangedEventArgs:m_Key	Ljava/lang/String;
    //   24: aload_1
    //   25: astore_0
    //   26: ldc 2
    //   28: monitorexit
    //   29: aload_0
    //   30: areturn
    //   31: new 2	com/oneplus/base/SettingsValueChangedEventArgs
    //   34: dup
    //   35: aload_0
    //   36: invokespecial 38	com/oneplus/base/SettingsValueChangedEventArgs:<init>	(Ljava/lang/String;)V
    //   39: astore_0
    //   40: goto -14 -> 26
    //   43: astore_0
    //   44: ldc 2
    //   46: monitorexit
    //   47: aload_0
    //   48: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	49	0	paramString	String
    //   14	11	1	localSettingsValueChangedEventArgs	SettingsValueChangedEventArgs
    // Exception table:
    //   from	to	target	type
    //   3	15	43	finally
    //   19	24	43	finally
    //   31	40	43	finally
  }
  
  public final String getKey()
  {
    return this.m_Key;
  }
  
  final void recycle()
  {
    try
    {
      if (POOL.size() < 8) {
        POOL.add(this);
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/SettingsValueChangedEventArgs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */