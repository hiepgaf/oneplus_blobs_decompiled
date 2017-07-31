package android.os;

public final class CancellationSignal
{
  private boolean mCancelInProgress;
  private boolean mIsCanceled;
  private OnCancelListener mOnCancelListener;
  private ICancellationSignal mRemote;
  
  public static ICancellationSignal createTransport()
  {
    return new Transport(null);
  }
  
  public static CancellationSignal fromTransport(ICancellationSignal paramICancellationSignal)
  {
    if ((paramICancellationSignal instanceof Transport)) {
      return ((Transport)paramICancellationSignal).mCancellationSignal;
    }
    return null;
  }
  
  private void waitForCancelFinishedLocked()
  {
    while (this.mCancelInProgress) {
      try
      {
        wait();
      }
      catch (InterruptedException localInterruptedException) {}
    }
  }
  
  /* Error */
  public void cancel()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 46	android/os/CancellationSignal:mIsCanceled	Z
    //   6: istore_1
    //   7: iload_1
    //   8: ifeq +6 -> 14
    //   11: aload_0
    //   12: monitorexit
    //   13: return
    //   14: aload_0
    //   15: iconst_1
    //   16: putfield 46	android/os/CancellationSignal:mIsCanceled	Z
    //   19: aload_0
    //   20: iconst_1
    //   21: putfield 38	android/os/CancellationSignal:mCancelInProgress	Z
    //   24: aload_0
    //   25: getfield 48	android/os/CancellationSignal:mOnCancelListener	Landroid/os/CancellationSignal$OnCancelListener;
    //   28: astore_2
    //   29: aload_0
    //   30: getfield 50	android/os/CancellationSignal:mRemote	Landroid/os/ICancellationSignal;
    //   33: astore_3
    //   34: aload_0
    //   35: monitorexit
    //   36: aload_2
    //   37: ifnull +9 -> 46
    //   40: aload_2
    //   41: invokeinterface 53 1 0
    //   46: aload_3
    //   47: ifnull +9 -> 56
    //   50: aload_3
    //   51: invokeinterface 57 1 0
    //   56: aload_0
    //   57: monitorenter
    //   58: aload_0
    //   59: iconst_0
    //   60: putfield 38	android/os/CancellationSignal:mCancelInProgress	Z
    //   63: aload_0
    //   64: invokevirtual 60	android/os/CancellationSignal:notifyAll	()V
    //   67: aload_0
    //   68: monitorexit
    //   69: return
    //   70: astore_2
    //   71: aload_0
    //   72: monitorexit
    //   73: aload_2
    //   74: athrow
    //   75: astore_2
    //   76: goto -20 -> 56
    //   79: astore_2
    //   80: aload_0
    //   81: monitorexit
    //   82: aload_2
    //   83: athrow
    //   84: astore_2
    //   85: aload_0
    //   86: monitorenter
    //   87: aload_0
    //   88: iconst_0
    //   89: putfield 38	android/os/CancellationSignal:mCancelInProgress	Z
    //   92: aload_0
    //   93: invokevirtual 60	android/os/CancellationSignal:notifyAll	()V
    //   96: aload_0
    //   97: monitorexit
    //   98: aload_2
    //   99: athrow
    //   100: astore_2
    //   101: aload_0
    //   102: monitorexit
    //   103: aload_2
    //   104: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	105	0	this	CancellationSignal
    //   6	2	1	bool	boolean
    //   28	13	2	localOnCancelListener	OnCancelListener
    //   70	4	2	localObject1	Object
    //   75	1	2	localRemoteException	RemoteException
    //   79	4	2	localObject2	Object
    //   84	15	2	localObject3	Object
    //   100	4	2	localObject4	Object
    //   33	18	3	localICancellationSignal	ICancellationSignal
    // Exception table:
    //   from	to	target	type
    //   2	7	70	finally
    //   14	34	70	finally
    //   50	56	75	android/os/RemoteException
    //   58	67	79	finally
    //   40	46	84	finally
    //   50	56	84	finally
    //   87	96	100	finally
  }
  
  public boolean isCanceled()
  {
    try
    {
      boolean bool = this.mIsCanceled;
      return bool;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void setOnCancelListener(OnCancelListener paramOnCancelListener)
  {
    try
    {
      waitForCancelFinishedLocked();
      OnCancelListener localOnCancelListener = this.mOnCancelListener;
      if (localOnCancelListener == paramOnCancelListener) {
        return;
      }
      this.mOnCancelListener = paramOnCancelListener;
      boolean bool = this.mIsCanceled;
      if ((!bool) || (paramOnCancelListener == null)) {
        return;
      }
      paramOnCancelListener.onCancel();
      return;
    }
    finally {}
  }
  
  /* Error */
  public void setRemote(ICancellationSignal paramICancellationSignal)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: invokespecial 66	android/os/CancellationSignal:waitForCancelFinishedLocked	()V
    //   6: aload_0
    //   7: getfield 50	android/os/CancellationSignal:mRemote	Landroid/os/ICancellationSignal;
    //   10: astore_3
    //   11: aload_3
    //   12: aload_1
    //   13: if_acmpne +6 -> 19
    //   16: aload_0
    //   17: monitorexit
    //   18: return
    //   19: aload_0
    //   20: aload_1
    //   21: putfield 50	android/os/CancellationSignal:mRemote	Landroid/os/ICancellationSignal;
    //   24: aload_0
    //   25: getfield 46	android/os/CancellationSignal:mIsCanceled	Z
    //   28: istore_2
    //   29: iload_2
    //   30: ifeq +7 -> 37
    //   33: aload_1
    //   34: ifnonnull +6 -> 40
    //   37: aload_0
    //   38: monitorexit
    //   39: return
    //   40: aload_0
    //   41: monitorexit
    //   42: aload_1
    //   43: invokeinterface 57 1 0
    //   48: return
    //   49: astore_1
    //   50: aload_0
    //   51: monitorexit
    //   52: aload_1
    //   53: athrow
    //   54: astore_1
    //   55: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	56	0	this	CancellationSignal
    //   0	56	1	paramICancellationSignal	ICancellationSignal
    //   28	2	2	bool	boolean
    //   10	2	3	localICancellationSignal	ICancellationSignal
    // Exception table:
    //   from	to	target	type
    //   2	11	49	finally
    //   19	29	49	finally
    //   42	48	54	android/os/RemoteException
  }
  
  public void throwIfCanceled()
  {
    if (isCanceled()) {
      throw new OperationCanceledException();
    }
  }
  
  public static abstract interface OnCancelListener
  {
    public abstract void onCancel();
  }
  
  private static final class Transport
    extends ICancellationSignal.Stub
  {
    final CancellationSignal mCancellationSignal = new CancellationSignal();
    
    public void cancel()
      throws RemoteException
    {
      this.mCancellationSignal.cancel();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/CancellationSignal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */