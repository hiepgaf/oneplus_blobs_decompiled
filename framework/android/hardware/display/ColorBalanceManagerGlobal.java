package android.hardware.display;

public final class ColorBalanceManagerGlobal
{
  private static final boolean DEBUG = false;
  private static final String TAG = "ColorBalanceManagerGlobal";
  private static ColorBalanceManagerGlobal sInstance;
  private final IColorBalanceManager mCBM;
  private final Object mLock = new Object();
  
  private ColorBalanceManagerGlobal(IColorBalanceManager paramIColorBalanceManager)
  {
    this.mCBM = paramIColorBalanceManager;
  }
  
  /* Error */
  public static ColorBalanceManagerGlobal getInstance()
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 31	android/hardware/display/ColorBalanceManagerGlobal:sInstance	Landroid/hardware/display/ColorBalanceManagerGlobal;
    //   6: ifnonnull +27 -> 33
    //   9: ldc 33
    //   11: invokestatic 39	android/os/ServiceManager:getService	(Ljava/lang/String;)Landroid/os/IBinder;
    //   14: astore_0
    //   15: aload_0
    //   16: ifnull +26 -> 42
    //   19: new 2	android/hardware/display/ColorBalanceManagerGlobal
    //   22: dup
    //   23: aload_0
    //   24: invokestatic 45	android/hardware/display/IColorBalanceManager$Stub:asInterface	(Landroid/os/IBinder;)Landroid/hardware/display/IColorBalanceManager;
    //   27: invokespecial 47	android/hardware/display/ColorBalanceManagerGlobal:<init>	(Landroid/hardware/display/IColorBalanceManager;)V
    //   30: putstatic 31	android/hardware/display/ColorBalanceManagerGlobal:sInstance	Landroid/hardware/display/ColorBalanceManagerGlobal;
    //   33: getstatic 31	android/hardware/display/ColorBalanceManagerGlobal:sInstance	Landroid/hardware/display/ColorBalanceManagerGlobal;
    //   36: astore_0
    //   37: ldc 2
    //   39: monitorexit
    //   40: aload_0
    //   41: areturn
    //   42: ldc 11
    //   44: ldc 49
    //   46: invokestatic 55	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   49: pop
    //   50: goto -17 -> 33
    //   53: astore_0
    //   54: ldc 2
    //   56: monitorexit
    //   57: aload_0
    //   58: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   14	27	0	localObject1	Object
    //   53	5	0	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   3	15	53	finally
    //   19	33	53	finally
    //   33	37	53	finally
    //   42	50	53	finally
  }
  
  /* Error */
  public void sendMsg(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 24	android/hardware/display/ColorBalanceManagerGlobal:mLock	Ljava/lang/Object;
    //   4: astore_2
    //   5: aload_2
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 26	android/hardware/display/ColorBalanceManagerGlobal:mCBM	Landroid/hardware/display/IColorBalanceManager;
    //   11: ifnull +16 -> 27
    //   14: aload_0
    //   15: getfield 26	android/hardware/display/ColorBalanceManagerGlobal:mCBM	Landroid/hardware/display/IColorBalanceManager;
    //   18: iload_1
    //   19: invokeinterface 63 2 0
    //   24: aload_2
    //   25: monitorexit
    //   26: return
    //   27: ldc 11
    //   29: ldc 65
    //   31: invokestatic 55	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   34: pop
    //   35: goto -11 -> 24
    //   38: astore_3
    //   39: aload_2
    //   40: monitorexit
    //   41: aload_3
    //   42: athrow
    //   43: astore_2
    //   44: aload_2
    //   45: invokevirtual 69	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
    //   48: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	49	0	this	ColorBalanceManagerGlobal
    //   0	49	1	paramInt	int
    //   43	2	2	localRemoteException	android.os.RemoteException
    //   38	4	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   7	24	38	finally
    //   27	35	38	finally
    //   0	7	43	android/os/RemoteException
    //   24	26	43	android/os/RemoteException
    //   39	43	43	android/os/RemoteException
  }
  
  /* Error */
  public void setActiveMode(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 24	android/hardware/display/ColorBalanceManagerGlobal:mLock	Ljava/lang/Object;
    //   4: astore_2
    //   5: aload_2
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 26	android/hardware/display/ColorBalanceManagerGlobal:mCBM	Landroid/hardware/display/IColorBalanceManager;
    //   11: ifnull +16 -> 27
    //   14: aload_0
    //   15: getfield 26	android/hardware/display/ColorBalanceManagerGlobal:mCBM	Landroid/hardware/display/IColorBalanceManager;
    //   18: iload_1
    //   19: invokeinterface 72 2 0
    //   24: aload_2
    //   25: monitorexit
    //   26: return
    //   27: ldc 11
    //   29: ldc 65
    //   31: invokestatic 55	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   34: pop
    //   35: goto -11 -> 24
    //   38: astore_3
    //   39: aload_2
    //   40: monitorexit
    //   41: aload_3
    //   42: athrow
    //   43: astore_2
    //   44: aload_2
    //   45: invokevirtual 69	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
    //   48: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	49	0	this	ColorBalanceManagerGlobal
    //   0	49	1	paramInt	int
    //   43	2	2	localRemoteException	android.os.RemoteException
    //   38	4	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   7	24	38	finally
    //   27	35	38	finally
    //   0	7	43	android/os/RemoteException
    //   24	26	43	android/os/RemoteException
    //   39	43	43	android/os/RemoteException
  }
  
  /* Error */
  public void setColorBalance(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 24	android/hardware/display/ColorBalanceManagerGlobal:mLock	Ljava/lang/Object;
    //   4: astore_2
    //   5: aload_2
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 26	android/hardware/display/ColorBalanceManagerGlobal:mCBM	Landroid/hardware/display/IColorBalanceManager;
    //   11: ifnull +16 -> 27
    //   14: aload_0
    //   15: getfield 26	android/hardware/display/ColorBalanceManagerGlobal:mCBM	Landroid/hardware/display/IColorBalanceManager;
    //   18: iload_1
    //   19: invokeinterface 75 2 0
    //   24: aload_2
    //   25: monitorexit
    //   26: return
    //   27: ldc 11
    //   29: ldc 65
    //   31: invokestatic 55	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   34: pop
    //   35: goto -11 -> 24
    //   38: astore_3
    //   39: aload_2
    //   40: monitorexit
    //   41: aload_3
    //   42: athrow
    //   43: astore_2
    //   44: aload_2
    //   45: invokevirtual 69	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
    //   48: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	49	0	this	ColorBalanceManagerGlobal
    //   0	49	1	paramInt	int
    //   43	2	2	localRemoteException	android.os.RemoteException
    //   38	4	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   7	24	38	finally
    //   27	35	38	finally
    //   0	7	43	android/os/RemoteException
    //   24	26	43	android/os/RemoteException
    //   39	43	43	android/os/RemoteException
  }
  
  /* Error */
  public void setDefaultMode(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 24	android/hardware/display/ColorBalanceManagerGlobal:mLock	Ljava/lang/Object;
    //   4: astore_2
    //   5: aload_2
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 26	android/hardware/display/ColorBalanceManagerGlobal:mCBM	Landroid/hardware/display/IColorBalanceManager;
    //   11: ifnull +16 -> 27
    //   14: aload_0
    //   15: getfield 26	android/hardware/display/ColorBalanceManagerGlobal:mCBM	Landroid/hardware/display/IColorBalanceManager;
    //   18: iload_1
    //   19: invokeinterface 78 2 0
    //   24: aload_2
    //   25: monitorexit
    //   26: return
    //   27: ldc 11
    //   29: ldc 65
    //   31: invokestatic 55	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   34: pop
    //   35: goto -11 -> 24
    //   38: astore_3
    //   39: aload_2
    //   40: monitorexit
    //   41: aload_3
    //   42: athrow
    //   43: astore_2
    //   44: aload_2
    //   45: invokevirtual 69	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
    //   48: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	49	0	this	ColorBalanceManagerGlobal
    //   0	49	1	paramInt	int
    //   43	2	2	localRemoteException	android.os.RemoteException
    //   38	4	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   7	24	38	finally
    //   27	35	38	finally
    //   0	7	43	android/os/RemoteException
    //   24	26	43	android/os/RemoteException
    //   39	43	43	android/os/RemoteException
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/display/ColorBalanceManagerGlobal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */