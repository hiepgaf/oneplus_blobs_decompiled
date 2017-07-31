package android.os;

public class ConditionVariable
{
  private volatile boolean mCondition;
  
  public ConditionVariable()
  {
    this.mCondition = false;
  }
  
  public ConditionVariable(boolean paramBoolean)
  {
    this.mCondition = paramBoolean;
  }
  
  public void block()
  {
    try
    {
      for (;;)
      {
        boolean bool = this.mCondition;
        if (bool) {
          break;
        }
        try
        {
          wait();
        }
        catch (InterruptedException localInterruptedException) {}
      }
      return;
    }
    finally {}
  }
  
  /* Error */
  public boolean block(long paramLong)
  {
    // Byte code:
    //   0: lload_1
    //   1: lconst_0
    //   2: lcmp
    //   3: ifeq +67 -> 70
    //   6: aload_0
    //   7: monitorenter
    //   8: invokestatic 27	java/lang/System:currentTimeMillis	()J
    //   11: lstore_3
    //   12: lload_3
    //   13: lload_1
    //   14: ladd
    //   15: lstore 5
    //   17: lload_3
    //   18: lstore_1
    //   19: aload_0
    //   20: getfield 12	android/os/ConditionVariable:mCondition	Z
    //   23: istore 7
    //   25: iload 7
    //   27: ifne +25 -> 52
    //   30: lload_1
    //   31: lload 5
    //   33: lcmp
    //   34: ifge +18 -> 52
    //   37: aload_0
    //   38: lload 5
    //   40: lload_1
    //   41: lsub
    //   42: invokevirtual 30	android/os/ConditionVariable:wait	(J)V
    //   45: invokestatic 27	java/lang/System:currentTimeMillis	()J
    //   48: lstore_1
    //   49: goto -30 -> 19
    //   52: aload_0
    //   53: getfield 12	android/os/ConditionVariable:mCondition	Z
    //   56: istore 7
    //   58: aload_0
    //   59: monitorexit
    //   60: iload 7
    //   62: ireturn
    //   63: astore 8
    //   65: aload_0
    //   66: monitorexit
    //   67: aload 8
    //   69: athrow
    //   70: aload_0
    //   71: invokevirtual 32	android/os/ConditionVariable:block	()V
    //   74: iconst_1
    //   75: ireturn
    //   76: astore 8
    //   78: goto -33 -> 45
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	81	0	this	ConditionVariable
    //   0	81	1	paramLong	long
    //   11	7	3	l1	long
    //   15	24	5	l2	long
    //   23	38	7	bool	boolean
    //   63	5	8	localObject	Object
    //   76	1	8	localInterruptedException	InterruptedException
    // Exception table:
    //   from	to	target	type
    //   8	12	63	finally
    //   19	25	63	finally
    //   37	45	63	finally
    //   45	49	63	finally
    //   52	58	63	finally
    //   37	45	76	java/lang/InterruptedException
  }
  
  public void close()
  {
    try
    {
      this.mCondition = false;
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void open()
  {
    try
    {
      boolean bool = this.mCondition;
      this.mCondition = true;
      if (!bool) {
        notifyAll();
      }
      return;
    }
    finally {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/ConditionVariable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */