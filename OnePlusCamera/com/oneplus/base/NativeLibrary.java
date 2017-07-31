package com.oneplus.base;

public final class NativeLibrary
{
  private static final String TAG = "OPBaseNativeLibrary";
  private static volatile boolean m_IsLoaded;
  
  /* Error */
  public static boolean load()
  {
    // Byte code:
    //   0: getstatic 21	com/oneplus/base/NativeLibrary:m_IsLoaded	Z
    //   3: ifeq +5 -> 8
    //   6: iconst_1
    //   7: ireturn
    //   8: ldc 2
    //   10: monitorenter
    //   11: getstatic 21	com/oneplus/base/NativeLibrary:m_IsLoaded	Z
    //   14: istore_0
    //   15: iload_0
    //   16: ifeq +8 -> 24
    //   19: ldc 2
    //   21: monitorexit
    //   22: iconst_1
    //   23: ireturn
    //   24: ldc 23
    //   26: invokestatic 29	java/lang/System:loadLibrary	(Ljava/lang/String;)V
    //   29: iconst_1
    //   30: putstatic 21	com/oneplus/base/NativeLibrary:m_IsLoaded	Z
    //   33: ldc 2
    //   35: monitorexit
    //   36: iconst_1
    //   37: ireturn
    //   38: astore_1
    //   39: ldc 8
    //   41: ldc 31
    //   43: aload_1
    //   44: invokestatic 37	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   47: ldc 2
    //   49: monitorexit
    //   50: iconst_0
    //   51: ireturn
    //   52: astore_1
    //   53: ldc 2
    //   55: monitorexit
    //   56: aload_1
    //   57: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   14	2	0	bool	boolean
    //   38	6	1	localThrowable	Throwable
    //   52	5	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   24	33	38	java/lang/Throwable
    //   11	15	52	finally
    //   24	33	52	finally
    //   39	47	52	finally
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/NativeLibrary.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */