package android.hardware.camera2.legacy;

import android.hardware.camera2.impl.CameraMetadataNative;
import android.os.Handler;
import android.util.Log;

public class CameraDeviceState
{
  private static final boolean DEBUG = false;
  public static final int NO_CAPTURE_ERROR = -1;
  private static final int STATE_CAPTURING = 4;
  private static final int STATE_CONFIGURING = 2;
  private static final int STATE_ERROR = 0;
  private static final int STATE_IDLE = 3;
  private static final int STATE_UNCONFIGURED = 1;
  private static final String TAG = "CameraDeviceState";
  private static final String[] sStateNames = { "ERROR", "UNCONFIGURED", "CONFIGURING", "IDLE", "CAPTURING" };
  private int mCurrentError = -1;
  private Handler mCurrentHandler = null;
  private CameraDeviceStateListener mCurrentListener = null;
  private RequestHolder mCurrentRequest = null;
  private int mCurrentState = 1;
  
  private void doStateTransition(int paramInt)
  {
    doStateTransition(paramInt, 0L, -1);
  }
  
  private void doStateTransition(int paramInt1, final long paramLong, final int paramInt2)
  {
    if (paramInt1 != this.mCurrentState)
    {
      String str2 = "UNKNOWN";
      String str1 = str2;
      if (paramInt1 >= 0)
      {
        str1 = str2;
        if (paramInt1 < sStateNames.length) {
          str1 = sStateNames[paramInt1];
        }
      }
      Log.i("CameraDeviceState", "Legacy camera service transitioning to state " + str1);
    }
    if ((paramInt1 != 0) && (paramInt1 != 3) && (this.mCurrentState != paramInt1) && (this.mCurrentHandler != null) && (this.mCurrentListener != null)) {
      this.mCurrentHandler.post(new Runnable()
      {
        public void run()
        {
          CameraDeviceState.-get1(CameraDeviceState.this).onBusy();
        }
      });
    }
    switch (paramInt1)
    {
    case 1: 
    default: 
      throw new IllegalStateException("Transition to unknown state: " + paramInt1);
    case 0: 
      if ((this.mCurrentState != 0) && (this.mCurrentHandler != null) && (this.mCurrentListener != null)) {
        this.mCurrentHandler.post(new Runnable()
        {
          public void run()
          {
            CameraDeviceState.-get1(CameraDeviceState.this).onError(CameraDeviceState.-get0(CameraDeviceState.this), null, CameraDeviceState.-get2(CameraDeviceState.this));
          }
        });
      }
      this.mCurrentState = 0;
    case 2: 
    case 3: 
      do
      {
        return;
        if ((this.mCurrentState != 1) && (this.mCurrentState != 3))
        {
          Log.e("CameraDeviceState", "Cannot call configure while in state: " + this.mCurrentState);
          this.mCurrentError = 1;
          doStateTransition(0);
          return;
        }
        if ((this.mCurrentState != 2) && (this.mCurrentHandler != null) && (this.mCurrentListener != null)) {
          this.mCurrentHandler.post(new Runnable()
          {
            public void run()
            {
              CameraDeviceState.-get1(CameraDeviceState.this).onConfiguring();
            }
          });
        }
        this.mCurrentState = 2;
        return;
      } while (this.mCurrentState == 3);
      if ((this.mCurrentState != 2) && (this.mCurrentState != 4))
      {
        Log.e("CameraDeviceState", "Cannot call idle while in state: " + this.mCurrentState);
        this.mCurrentError = 1;
        doStateTransition(0);
        return;
      }
      if ((this.mCurrentState != 3) && (this.mCurrentHandler != null) && (this.mCurrentListener != null)) {
        this.mCurrentHandler.post(new Runnable()
        {
          public void run()
          {
            CameraDeviceState.-get1(CameraDeviceState.this).onIdle();
          }
        });
      }
      this.mCurrentState = 3;
      return;
    }
    if ((this.mCurrentState != 3) && (this.mCurrentState != 4))
    {
      Log.e("CameraDeviceState", "Cannot call capture while in state: " + this.mCurrentState);
      this.mCurrentError = 1;
      doStateTransition(0);
      return;
    }
    if ((this.mCurrentHandler != null) && (this.mCurrentListener != null))
    {
      if (paramInt2 == -1) {
        break label523;
      }
      this.mCurrentHandler.post(new Runnable()
      {
        public void run()
        {
          CameraDeviceState.-get1(CameraDeviceState.this).onError(paramInt2, null, CameraDeviceState.-get2(CameraDeviceState.this));
        }
      });
    }
    for (;;)
    {
      this.mCurrentState = 4;
      return;
      label523:
      this.mCurrentHandler.post(new Runnable()
      {
        public void run()
        {
          CameraDeviceState.-get1(CameraDeviceState.this).onCaptureStarted(CameraDeviceState.-get2(CameraDeviceState.this), paramLong);
        }
      });
    }
  }
  
  public void setCameraDeviceCallbacks(Handler paramHandler, CameraDeviceStateListener paramCameraDeviceStateListener)
  {
    try
    {
      this.mCurrentHandler = paramHandler;
      this.mCurrentListener = paramCameraDeviceStateListener;
      return;
    }
    finally
    {
      paramHandler = finally;
      throw paramHandler;
    }
  }
  
  public boolean setCaptureResult(RequestHolder paramRequestHolder, CameraMetadataNative paramCameraMetadataNative)
  {
    try
    {
      boolean bool = setCaptureResult(paramRequestHolder, paramCameraMetadataNative, -1, null);
      return bool;
    }
    finally
    {
      paramRequestHolder = finally;
      throw paramRequestHolder;
    }
  }
  
  public boolean setCaptureResult(final RequestHolder paramRequestHolder, final CameraMetadataNative paramCameraMetadataNative, final int paramInt, final Object paramObject)
  {
    boolean bool2 = true;
    boolean bool1 = true;
    label159:
    for (;;)
    {
      try
      {
        if (this.mCurrentState != 4)
        {
          Log.e("CameraDeviceState", "Cannot receive result while in state: " + this.mCurrentState);
          this.mCurrentError = 1;
          doStateTransition(0);
          paramInt = this.mCurrentError;
          if (paramInt == -1) {
            return bool1;
          }
          bool1 = false;
          continue;
        }
        if ((this.mCurrentHandler != null) && (this.mCurrentListener != null))
        {
          if (paramInt != -1) {
            this.mCurrentHandler.post(new Runnable()
            {
              public void run()
              {
                CameraDeviceState.-get1(CameraDeviceState.this).onError(paramInt, paramObject, paramRequestHolder);
              }
            });
          }
        }
        else
        {
          paramInt = this.mCurrentError;
          if (paramInt != -1) {
            break label159;
          }
          bool1 = bool2;
          return bool1;
        }
        this.mCurrentHandler.post(new Runnable()
        {
          public void run()
          {
            CameraDeviceState.-get1(CameraDeviceState.this).onCaptureResult(paramCameraMetadataNative, paramRequestHolder);
          }
        });
        continue;
        bool1 = false;
      }
      finally {}
    }
  }
  
  /* Error */
  public boolean setCaptureStart(RequestHolder paramRequestHolder, long paramLong, int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: aload_1
    //   4: putfield 67	android/hardware/camera2/legacy/CameraDeviceState:mCurrentRequest	Landroid/hardware/camera2/legacy/RequestHolder;
    //   7: aload_0
    //   8: iconst_4
    //   9: lload_2
    //   10: iload 4
    //   12: invokespecial 95	android/hardware/camera2/legacy/CameraDeviceState:doStateTransition	(IJI)V
    //   15: aload_0
    //   16: getfield 58	android/hardware/camera2/legacy/CameraDeviceState:mCurrentError	I
    //   19: istore 4
    //   21: iload 4
    //   23: iconst_m1
    //   24: if_icmpne +11 -> 35
    //   27: iconst_1
    //   28: istore 5
    //   30: aload_0
    //   31: monitorexit
    //   32: iload 5
    //   34: ireturn
    //   35: iconst_0
    //   36: istore 5
    //   38: goto -8 -> 30
    //   41: astore_1
    //   42: aload_0
    //   43: monitorexit
    //   44: aload_1
    //   45: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	46	0	this	CameraDeviceState
    //   0	46	1	paramRequestHolder	RequestHolder
    //   0	46	2	paramLong	long
    //   0	46	4	paramInt	int
    //   28	9	5	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   2	21	41	finally
  }
  
  /* Error */
  public boolean setConfiguring()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: iconst_2
    //   4: invokespecial 143	android/hardware/camera2/legacy/CameraDeviceState:doStateTransition	(I)V
    //   7: aload_0
    //   8: getfield 58	android/hardware/camera2/legacy/CameraDeviceState:mCurrentError	I
    //   11: istore_1
    //   12: iload_1
    //   13: iconst_m1
    //   14: if_icmpne +9 -> 23
    //   17: iconst_1
    //   18: istore_2
    //   19: aload_0
    //   20: monitorexit
    //   21: iload_2
    //   22: ireturn
    //   23: iconst_0
    //   24: istore_2
    //   25: goto -6 -> 19
    //   28: astore_3
    //   29: aload_0
    //   30: monitorexit
    //   31: aload_3
    //   32: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	33	0	this	CameraDeviceState
    //   11	4	1	i	int
    //   18	7	2	bool	boolean
    //   28	4	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	12	28	finally
  }
  
  public void setError(int paramInt)
  {
    try
    {
      this.mCurrentError = paramInt;
      doStateTransition(0);
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  /* Error */
  public boolean setIdle()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: iconst_3
    //   4: invokespecial 143	android/hardware/camera2/legacy/CameraDeviceState:doStateTransition	(I)V
    //   7: aload_0
    //   8: getfield 58	android/hardware/camera2/legacy/CameraDeviceState:mCurrentError	I
    //   11: istore_1
    //   12: iload_1
    //   13: iconst_m1
    //   14: if_icmpne +9 -> 23
    //   17: iconst_1
    //   18: istore_2
    //   19: aload_0
    //   20: monitorexit
    //   21: iload_2
    //   22: ireturn
    //   23: iconst_0
    //   24: istore_2
    //   25: goto -6 -> 19
    //   28: astore_3
    //   29: aload_0
    //   30: monitorexit
    //   31: aload_3
    //   32: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	33	0	this	CameraDeviceState
    //   11	4	1	i	int
    //   18	7	2	bool	boolean
    //   28	4	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	12	28	finally
  }
  
  public void setRepeatingRequestError(final long paramLong)
  {
    try
    {
      this.mCurrentHandler.post(new Runnable()
      {
        public void run()
        {
          CameraDeviceState.-get1(CameraDeviceState.this).onRepeatingRequestError(paramLong);
        }
      });
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public static abstract interface CameraDeviceStateListener
  {
    public abstract void onBusy();
    
    public abstract void onCaptureResult(CameraMetadataNative paramCameraMetadataNative, RequestHolder paramRequestHolder);
    
    public abstract void onCaptureStarted(RequestHolder paramRequestHolder, long paramLong);
    
    public abstract void onConfiguring();
    
    public abstract void onError(int paramInt, Object paramObject, RequestHolder paramRequestHolder);
    
    public abstract void onIdle();
    
    public abstract void onRepeatingRequestError(long paramLong);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/legacy/CameraDeviceState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */