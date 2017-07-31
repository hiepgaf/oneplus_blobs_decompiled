package android.os;

import android.util.Log;
import com.android.internal.util.FastPrintWriter;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import libcore.io.IoUtils;

public class Binder
  implements IBinder
{
  private static final boolean CHECK_PARCEL_SIZE = false;
  private static final boolean FIND_POTENTIAL_LEAKS = false;
  public static boolean LOG_RUNTIME_EXCEPTION = false;
  static final String TAG = "Binder";
  private static String sDumpDisabled = null;
  private static boolean sTracingEnabled = false;
  private static TransactionTracker sTransactionTracker = null;
  private String mDescriptor;
  private long mObject;
  private IInterface mOwner;
  
  public Binder()
  {
    init();
  }
  
  public static final native void blockUntilThreadAvailable();
  
  static void checkParcel(IBinder paramIBinder, int paramInt, Parcel paramParcel, String paramString) {}
  
  public static final native long clearCallingIdentity();
  
  private final native void destroy();
  
  public static void disableTracing()
  {
    sTracingEnabled = false;
  }
  
  public static void enableTracing()
  {
    sTracingEnabled = true;
  }
  
  private boolean execTransact(int paramInt1, long paramLong1, long paramLong2, int paramInt2)
  {
    Parcel localParcel1 = Parcel.obtain(paramLong1);
    Parcel localParcel2 = Parcel.obtain(paramLong2);
    boolean bool;
    try
    {
      bool = onTransact(paramInt1, localParcel1, localParcel2, paramInt2);
      checkParcel(this, paramInt1, localParcel2, "Unreasonably large binder reply buffer");
      localParcel2.recycle();
      localParcel1.recycle();
      StrictMode.clearGatheredViolations();
      return bool;
    }
    catch (OutOfMemoryError localOutOfMemoryError)
    {
      for (;;)
      {
        Log.e("Binder", "Caught an OutOfMemoryError from the binder stub implementation.", localOutOfMemoryError);
        RuntimeException localRuntimeException = new RuntimeException("Out of memory", localOutOfMemoryError);
        localParcel2.setDataPosition(0);
        localParcel2.writeException(localRuntimeException);
        bool = true;
      }
    }
    catch (RemoteException|RuntimeException localRemoteException)
    {
      if (LOG_RUNTIME_EXCEPTION) {
        Log.w("Binder", "Caught a RuntimeException from the binder stub implementation.", localRemoteException);
      }
      if ((paramInt2 & 0x1) == 0) {
        break label157;
      }
    }
    if ((localRemoteException instanceof RemoteException)) {
      Log.w("Binder", "Binder call failed.", localRemoteException);
    }
    for (;;)
    {
      bool = true;
      break;
      Log.w("Binder", "Caught a RuntimeException from the binder stub implementation.", localRemoteException);
      continue;
      label157:
      localParcel2.setDataPosition(0);
      localParcel2.writeException(localRemoteException);
    }
  }
  
  public static final native void flushPendingCommands();
  
  public static final native int getCallingPid();
  
  public static final native int getCallingUid();
  
  public static final UserHandle getCallingUserHandle()
  {
    return UserHandle.of(UserHandle.getUserId(getCallingUid()));
  }
  
  public static final native int getThreadStrictModePolicy();
  
  public static TransactionTracker getTransactionTracker()
  {
    try
    {
      if (sTransactionTracker == null) {
        sTransactionTracker = new TransactionTracker();
      }
      TransactionTracker localTransactionTracker = sTransactionTracker;
      return localTransactionTracker;
    }
    finally {}
  }
  
  private final native void init();
  
  public static final boolean isProxy(IInterface paramIInterface)
  {
    return paramIInterface.asBinder() != paramIInterface;
  }
  
  public static boolean isTracingEnabled()
  {
    return sTracingEnabled;
  }
  
  public static final native void joinThreadPool();
  
  public static final native void restoreCallingIdentity(long paramLong);
  
  public static final native void setBlockUid(int paramInt, boolean paramBoolean);
  
  public static void setDumpDisabled(String paramString)
  {
    try
    {
      sDumpDisabled = paramString;
      return;
    }
    finally
    {
      paramString = finally;
      throw paramString;
    }
  }
  
  public static final native void setThreadStrictModePolicy(int paramInt);
  
  public void attachInterface(IInterface paramIInterface, String paramString)
  {
    this.mOwner = paramIInterface;
    this.mDescriptor = paramString;
  }
  
  /* Error */
  void doDump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 32	android/os/Binder:sDumpDisabled	Ljava/lang/String;
    //   6: astore 4
    //   8: ldc 2
    //   10: monitorexit
    //   11: aload 4
    //   13: ifnonnull +63 -> 76
    //   16: aload_0
    //   17: aload_1
    //   18: aload_2
    //   19: aload_3
    //   20: invokevirtual 166	android/os/Binder:dump	(Ljava/io/FileDescriptor;Ljava/io/PrintWriter;[Ljava/lang/String;)V
    //   23: return
    //   24: astore_1
    //   25: ldc 2
    //   27: monitorexit
    //   28: aload_1
    //   29: athrow
    //   30: astore_1
    //   31: aload_2
    //   32: invokevirtual 171	java/io/PrintWriter:println	()V
    //   35: aload_2
    //   36: ldc -83
    //   38: invokevirtual 175	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   41: aload_1
    //   42: aload_2
    //   43: invokevirtual 179	java/lang/Throwable:printStackTrace	(Ljava/io/PrintWriter;)V
    //   46: return
    //   47: astore_1
    //   48: aload_2
    //   49: new 181	java/lang/StringBuilder
    //   52: dup
    //   53: invokespecial 182	java/lang/StringBuilder:<init>	()V
    //   56: ldc -72
    //   58: invokevirtual 188	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   61: aload_1
    //   62: invokevirtual 192	java/lang/SecurityException:getMessage	()Ljava/lang/String;
    //   65: invokevirtual 188	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   68: invokevirtual 195	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   71: invokevirtual 175	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   74: aload_1
    //   75: athrow
    //   76: aload_2
    //   77: getstatic 32	android/os/Binder:sDumpDisabled	Ljava/lang/String;
    //   80: invokevirtual 175	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   83: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	84	0	this	Binder
    //   0	84	1	paramFileDescriptor	FileDescriptor
    //   0	84	2	paramPrintWriter	PrintWriter
    //   0	84	3	paramArrayOfString	String[]
    //   6	6	4	str	String
    // Exception table:
    //   from	to	target	type
    //   3	8	24	finally
    //   16	23	30	java/lang/Throwable
    //   16	23	47	java/lang/SecurityException
  }
  
  protected void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString) {}
  
  public void dump(FileDescriptor paramFileDescriptor, String[] paramArrayOfString)
  {
    FastPrintWriter localFastPrintWriter = new FastPrintWriter(new FileOutputStream(paramFileDescriptor));
    try
    {
      doDump(paramFileDescriptor, localFastPrintWriter, paramArrayOfString);
      return;
    }
    finally
    {
      localFastPrintWriter.flush();
    }
  }
  
  public void dumpAsync(final FileDescriptor paramFileDescriptor, final String[] paramArrayOfString)
  {
    new Thread("Binder.dumpAsync")
    {
      public void run()
      {
        try
        {
          Binder.this.dump(paramFileDescriptor, this.val$pw, paramArrayOfString);
          return;
        }
        finally
        {
          this.val$pw.flush();
        }
      }
    }.start();
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      destroy();
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public String getInterfaceDescriptor()
  {
    return this.mDescriptor;
  }
  
  public boolean isBinderAlive()
  {
    return true;
  }
  
  public void linkToDeath(IBinder.DeathRecipient paramDeathRecipient, int paramInt) {}
  
  public void onShellCommand(FileDescriptor paramFileDescriptor1, FileDescriptor paramFileDescriptor2, FileDescriptor paramFileDescriptor3, String[] paramArrayOfString, ResultReceiver paramResultReceiver)
    throws RemoteException
  {
    if (paramFileDescriptor3 != null) {}
    for (;;)
    {
      paramFileDescriptor1 = new FastPrintWriter(new FileOutputStream(paramFileDescriptor3));
      paramFileDescriptor1.println("No shell command implementation.");
      paramFileDescriptor1.flush();
      paramResultReceiver.send(0, null);
      return;
      paramFileDescriptor3 = paramFileDescriptor2;
    }
  }
  
  protected boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
    throws RemoteException
  {
    Object localObject = null;
    if (paramInt1 == 1598968902)
    {
      paramParcel2.writeString(getInterfaceDescriptor());
      return true;
    }
    if (paramInt1 == 1598311760)
    {
      localObject = paramParcel1.readFileDescriptor();
      paramParcel1 = paramParcel1.readStringArray();
      if (localObject != null) {}
      try
      {
        dump(((ParcelFileDescriptor)localObject).getFileDescriptor(), paramParcel1);
        IoUtils.closeQuietly((AutoCloseable)localObject);
        if (paramParcel2 != null)
        {
          paramParcel2.writeNoException();
          return true;
        }
      }
      finally
      {
        IoUtils.closeQuietly((AutoCloseable)localObject);
      }
      StrictMode.clearGatheredViolations();
      return true;
    }
    if (paramInt1 == 1598246212)
    {
      ParcelFileDescriptor localParcelFileDescriptor1 = paramParcel1.readFileDescriptor();
      ParcelFileDescriptor localParcelFileDescriptor2 = paramParcel1.readFileDescriptor();
      ParcelFileDescriptor localParcelFileDescriptor3 = paramParcel1.readFileDescriptor();
      String[] arrayOfString = paramParcel1.readStringArray();
      ResultReceiver localResultReceiver = (ResultReceiver)ResultReceiver.CREATOR.createFromParcel(paramParcel1);
      if (localParcelFileDescriptor2 != null)
      {
        paramParcel1 = (Parcel)localObject;
        if (localParcelFileDescriptor1 == null) {}
      }
      try
      {
        paramParcel1 = localParcelFileDescriptor1.getFileDescriptor();
        FileDescriptor localFileDescriptor = localParcelFileDescriptor2.getFileDescriptor();
        if (localParcelFileDescriptor3 != null) {}
        for (localObject = localParcelFileDescriptor3.getFileDescriptor();; localObject = localParcelFileDescriptor2.getFileDescriptor())
        {
          shellCommand(paramParcel1, localFileDescriptor, (FileDescriptor)localObject, arrayOfString, localResultReceiver);
          IoUtils.closeQuietly(localParcelFileDescriptor1);
          IoUtils.closeQuietly(localParcelFileDescriptor2);
          IoUtils.closeQuietly(localParcelFileDescriptor3);
          if (paramParcel2 == null) {
            break;
          }
          paramParcel2.writeNoException();
          return true;
        }
        StrictMode.clearGatheredViolations();
        return true;
      }
      finally
      {
        IoUtils.closeQuietly(localParcelFileDescriptor1);
        IoUtils.closeQuietly(localParcelFileDescriptor2);
        IoUtils.closeQuietly(localParcelFileDescriptor3);
        if (paramParcel2 == null) {
          break label241;
        }
      }
      paramParcel2.writeNoException();
      for (;;)
      {
        throw paramParcel1;
        label241:
        StrictMode.clearGatheredViolations();
      }
    }
    return false;
  }
  
  public boolean pingBinder()
  {
    return true;
  }
  
  public IInterface queryLocalInterface(String paramString)
  {
    if (this.mDescriptor.equals(paramString)) {
      return this.mOwner;
    }
    return null;
  }
  
  public void shellCommand(FileDescriptor paramFileDescriptor1, FileDescriptor paramFileDescriptor2, FileDescriptor paramFileDescriptor3, String[] paramArrayOfString, ResultReceiver paramResultReceiver)
    throws RemoteException
  {
    onShellCommand(paramFileDescriptor1, paramFileDescriptor2, paramFileDescriptor3, paramArrayOfString, paramResultReceiver);
  }
  
  public final boolean transact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
    throws RemoteException
  {
    if (paramParcel1 != null) {
      paramParcel1.setDataPosition(0);
    }
    boolean bool = onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
    if (paramParcel2 != null) {
      paramParcel2.setDataPosition(0);
    }
    return bool;
  }
  
  public boolean unlinkToDeath(IBinder.DeathRecipient paramDeathRecipient, int paramInt)
  {
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/Binder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */