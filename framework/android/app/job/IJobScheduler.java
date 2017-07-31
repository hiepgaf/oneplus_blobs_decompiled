package android.app.job;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import java.util.ArrayList;
import java.util.List;

public abstract interface IJobScheduler
  extends IInterface
{
  public abstract void cancel(int paramInt)
    throws RemoteException;
  
  public abstract void cancelAll()
    throws RemoteException;
  
  public abstract List<JobInfo> getAllPendingJobs()
    throws RemoteException;
  
  public abstract JobInfo getPendingJob(int paramInt)
    throws RemoteException;
  
  public abstract int schedule(JobInfo paramJobInfo)
    throws RemoteException;
  
  public abstract int scheduleAsPackage(JobInfo paramJobInfo, String paramString1, int paramInt, String paramString2)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IJobScheduler
  {
    private static final String DESCRIPTOR = "android.app.job.IJobScheduler";
    static final int TRANSACTION_cancel = 3;
    static final int TRANSACTION_cancelAll = 4;
    static final int TRANSACTION_getAllPendingJobs = 5;
    static final int TRANSACTION_getPendingJob = 6;
    static final int TRANSACTION_schedule = 1;
    static final int TRANSACTION_scheduleAsPackage = 2;
    
    public Stub()
    {
      attachInterface(this, "android.app.job.IJobScheduler");
    }
    
    public static IJobScheduler asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.app.job.IJobScheduler");
      if ((localIInterface != null) && ((localIInterface instanceof IJobScheduler))) {
        return (IJobScheduler)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public IBinder asBinder()
    {
      return this;
    }
    
    public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.app.job.IJobScheduler");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.app.job.IJobScheduler");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (JobInfo)JobInfo.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramInt1 = schedule(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.app.job.IJobScheduler");
        if (paramParcel1.readInt() != 0) {}
        for (JobInfo localJobInfo = (JobInfo)JobInfo.CREATOR.createFromParcel(paramParcel1);; localJobInfo = null)
        {
          paramInt1 = scheduleAsPackage(localJobInfo, paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readString());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 3: 
        paramParcel1.enforceInterface("android.app.job.IJobScheduler");
        cancel(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.app.job.IJobScheduler");
        cancelAll();
        paramParcel2.writeNoException();
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.app.job.IJobScheduler");
        paramParcel1 = getAllPendingJobs();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      }
      paramParcel1.enforceInterface("android.app.job.IJobScheduler");
      paramParcel1 = getPendingJob(paramParcel1.readInt());
      paramParcel2.writeNoException();
      if (paramParcel1 != null)
      {
        paramParcel2.writeInt(1);
        paramParcel1.writeToParcel(paramParcel2, 1);
        return true;
      }
      paramParcel2.writeInt(0);
      return true;
    }
    
    private static class Proxy
      implements IJobScheduler
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public void cancel(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.job.IJobScheduler");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(3, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void cancelAll()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.job.IJobScheduler");
          this.mRemote.transact(4, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public List<JobInfo> getAllPendingJobs()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.job.IJobScheduler");
          this.mRemote.transact(5, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ArrayList localArrayList = localParcel2.createTypedArrayList(JobInfo.CREATOR);
          return localArrayList;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.app.job.IJobScheduler";
      }
      
      /* Error */
      public JobInfo getPendingJob(int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 41	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/app/job/IJobScheduler$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 6
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 47 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 50	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 77	android/os/Parcel:readInt	()I
        //   46: ifeq +28 -> 74
        //   49: getstatic 63	android/app/job/JobInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   52: aload 4
        //   54: invokeinterface 83 2 0
        //   59: checkcast 59	android/app/job/JobInfo
        //   62: astore_2
        //   63: aload 4
        //   65: invokevirtual 53	android/os/Parcel:recycle	()V
        //   68: aload_3
        //   69: invokevirtual 53	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: areturn
        //   74: aconst_null
        //   75: astore_2
        //   76: goto -13 -> 63
        //   79: astore_2
        //   80: aload 4
        //   82: invokevirtual 53	android/os/Parcel:recycle	()V
        //   85: aload_3
        //   86: invokevirtual 53	android/os/Parcel:recycle	()V
        //   89: aload_2
        //   90: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	91	0	this	Proxy
        //   0	91	1	paramInt	int
        //   62	14	2	localJobInfo	JobInfo
        //   79	11	2	localObject	Object
        //   3	83	3	localParcel1	Parcel
        //   7	74	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	63	79	finally
      }
      
      /* Error */
      public int schedule(JobInfo paramJobInfo)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +51 -> 67
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 41	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 89	android/app/job/JobInfo:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_0
        //   31: getfield 19	android/app/job/IJobScheduler$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: iconst_1
        //   35: aload_3
        //   36: aload 4
        //   38: iconst_0
        //   39: invokeinterface 47 5 0
        //   44: pop
        //   45: aload 4
        //   47: invokevirtual 50	android/os/Parcel:readException	()V
        //   50: aload 4
        //   52: invokevirtual 77	android/os/Parcel:readInt	()I
        //   55: istore_2
        //   56: aload 4
        //   58: invokevirtual 53	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 53	android/os/Parcel:recycle	()V
        //   65: iload_2
        //   66: ireturn
        //   67: aload_3
        //   68: iconst_0
        //   69: invokevirtual 41	android/os/Parcel:writeInt	(I)V
        //   72: goto -42 -> 30
        //   75: astore_1
        //   76: aload 4
        //   78: invokevirtual 53	android/os/Parcel:recycle	()V
        //   81: aload_3
        //   82: invokevirtual 53	android/os/Parcel:recycle	()V
        //   85: aload_1
        //   86: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	87	0	this	Proxy
        //   0	87	1	paramJobInfo	JobInfo
        //   55	11	2	i	int
        //   3	79	3	localParcel1	Parcel
        //   7	70	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	75	finally
        //   19	30	75	finally
        //   30	56	75	finally
        //   67	72	75	finally
      }
      
      /* Error */
      public int scheduleAsPackage(JobInfo paramJobInfo, String paramString1, int paramInt, String paramString2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 5
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 6
        //   10: aload 5
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload_1
        //   18: ifnull +74 -> 92
        //   21: aload 5
        //   23: iconst_1
        //   24: invokevirtual 41	android/os/Parcel:writeInt	(I)V
        //   27: aload_1
        //   28: aload 5
        //   30: iconst_0
        //   31: invokevirtual 89	android/app/job/JobInfo:writeToParcel	(Landroid/os/Parcel;I)V
        //   34: aload 5
        //   36: aload_2
        //   37: invokevirtual 94	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   40: aload 5
        //   42: iload_3
        //   43: invokevirtual 41	android/os/Parcel:writeInt	(I)V
        //   46: aload 5
        //   48: aload 4
        //   50: invokevirtual 94	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   53: aload_0
        //   54: getfield 19	android/app/job/IJobScheduler$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   57: iconst_2
        //   58: aload 5
        //   60: aload 6
        //   62: iconst_0
        //   63: invokeinterface 47 5 0
        //   68: pop
        //   69: aload 6
        //   71: invokevirtual 50	android/os/Parcel:readException	()V
        //   74: aload 6
        //   76: invokevirtual 77	android/os/Parcel:readInt	()I
        //   79: istore_3
        //   80: aload 6
        //   82: invokevirtual 53	android/os/Parcel:recycle	()V
        //   85: aload 5
        //   87: invokevirtual 53	android/os/Parcel:recycle	()V
        //   90: iload_3
        //   91: ireturn
        //   92: aload 5
        //   94: iconst_0
        //   95: invokevirtual 41	android/os/Parcel:writeInt	(I)V
        //   98: goto -64 -> 34
        //   101: astore_1
        //   102: aload 6
        //   104: invokevirtual 53	android/os/Parcel:recycle	()V
        //   107: aload 5
        //   109: invokevirtual 53	android/os/Parcel:recycle	()V
        //   112: aload_1
        //   113: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	114	0	this	Proxy
        //   0	114	1	paramJobInfo	JobInfo
        //   0	114	2	paramString1	String
        //   0	114	3	paramInt	int
        //   0	114	4	paramString2	String
        //   3	105	5	localParcel1	Parcel
        //   8	95	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	17	101	finally
        //   21	34	101	finally
        //   34	80	101	finally
        //   92	98	101	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/job/IJobScheduler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */