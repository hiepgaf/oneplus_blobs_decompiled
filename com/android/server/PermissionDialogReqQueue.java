package com.android.server;

import java.util.ArrayList;
import java.util.List;

public class PermissionDialogReqQueue
{
  private PermissionDialog mDialog = null;
  private List<PermissionDialogReq> mResultList = new ArrayList();
  
  public PermissionDialog getDialog()
  {
    return this.mDialog;
  }
  
  /* Error */
  public void notifyAll(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 23	com/android/server/PermissionDialogReqQueue:mResultList	Ljava/util/List;
    //   6: invokeinterface 34 1 0
    //   11: ifeq +39 -> 50
    //   14: aload_0
    //   15: getfield 23	com/android/server/PermissionDialogReqQueue:mResultList	Ljava/util/List;
    //   18: iconst_0
    //   19: invokeinterface 38 2 0
    //   24: checkcast 6	com/android/server/PermissionDialogReqQueue$PermissionDialogReq
    //   27: iload_1
    //   28: invokevirtual 41	com/android/server/PermissionDialogReqQueue$PermissionDialogReq:set	(I)V
    //   31: aload_0
    //   32: getfield 23	com/android/server/PermissionDialogReqQueue:mResultList	Ljava/util/List;
    //   35: iconst_0
    //   36: invokeinterface 44 2 0
    //   41: pop
    //   42: goto -40 -> 2
    //   45: astore_2
    //   46: aload_0
    //   47: monitorexit
    //   48: aload_2
    //   49: athrow
    //   50: aload_0
    //   51: monitorexit
    //   52: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	53	0	this	PermissionDialogReqQueue
    //   0	53	1	paramInt	int
    //   45	4	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	42	45	finally
  }
  
  public void register(PermissionDialogReq paramPermissionDialogReq)
  {
    try
    {
      this.mResultList.add(paramPermissionDialogReq);
      return;
    }
    finally
    {
      paramPermissionDialogReq = finally;
      throw paramPermissionDialogReq;
    }
  }
  
  public void setDialog(PermissionDialog paramPermissionDialog)
  {
    this.mDialog = paramPermissionDialog;
  }
  
  public static final class PermissionDialogReq
  {
    boolean mHasResult = false;
    int mResult;
    
    public int get()
    {
      try
      {
        for (;;)
        {
          boolean bool = this.mHasResult;
          if (bool) {
            break;
          }
          try
          {
            wait();
          }
          catch (InterruptedException localInterruptedException) {}
        }
        return this.mResult;
      }
      finally {}
    }
    
    public void set(int paramInt)
    {
      try
      {
        this.mHasResult = true;
        this.mResult = paramInt;
        notifyAll();
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/PermissionDialogReqQueue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */