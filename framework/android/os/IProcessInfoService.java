package android.os;

public abstract interface IProcessInfoService
  extends IInterface
{
  public abstract void getProcessStatesAndOomScoresFromPids(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int[] paramArrayOfInt3)
    throws RemoteException;
  
  public abstract void getProcessStatesFromPids(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IProcessInfoService
  {
    private static final String DESCRIPTOR = "android.os.IProcessInfoService";
    static final int TRANSACTION_getProcessStatesAndOomScoresFromPids = 2;
    static final int TRANSACTION_getProcessStatesFromPids = 1;
    
    public Stub()
    {
      attachInterface(this, "android.os.IProcessInfoService");
    }
    
    public static IProcessInfoService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.os.IProcessInfoService");
      if ((localIInterface != null) && ((localIInterface instanceof IProcessInfoService))) {
        return (IProcessInfoService)localIInterface;
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
      int[] arrayOfInt1;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.os.IProcessInfoService");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.os.IProcessInfoService");
        arrayOfInt1 = paramParcel1.createIntArray();
        paramInt1 = paramParcel1.readInt();
        if (paramInt1 < 0) {}
        for (paramParcel1 = null;; paramParcel1 = new int[paramInt1])
        {
          getProcessStatesFromPids(arrayOfInt1, paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeIntArray(paramParcel1);
          return true;
        }
      }
      paramParcel1.enforceInterface("android.os.IProcessInfoService");
      int[] arrayOfInt2 = paramParcel1.createIntArray();
      paramInt1 = paramParcel1.readInt();
      if (paramInt1 < 0)
      {
        arrayOfInt1 = null;
        paramInt1 = paramParcel1.readInt();
        if (paramInt1 >= 0) {
          break label171;
        }
      }
      label171:
      for (paramParcel1 = null;; paramParcel1 = new int[paramInt1])
      {
        getProcessStatesAndOomScoresFromPids(arrayOfInt2, arrayOfInt1, paramParcel1);
        paramParcel2.writeNoException();
        paramParcel2.writeIntArray(arrayOfInt1);
        paramParcel2.writeIntArray(paramParcel1);
        return true;
        arrayOfInt1 = new int[paramInt1];
        break;
      }
    }
    
    private static class Proxy
      implements IProcessInfoService
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
      
      public String getInterfaceDescriptor()
      {
        return "android.os.IProcessInfoService";
      }
      
      public void getProcessStatesAndOomScoresFromPids(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int[] paramArrayOfInt3)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.os.IProcessInfoService");
            localParcel1.writeIntArray(paramArrayOfInt1);
            if (paramArrayOfInt2 == null)
            {
              localParcel1.writeInt(-1);
              if (paramArrayOfInt3 == null)
              {
                localParcel1.writeInt(-1);
                this.mRemote.transact(2, localParcel1, localParcel2, 0);
                localParcel2.readException();
                localParcel2.readIntArray(paramArrayOfInt2);
                localParcel2.readIntArray(paramArrayOfInt3);
              }
            }
            else
            {
              localParcel1.writeInt(paramArrayOfInt2.length);
              continue;
            }
            localParcel1.writeInt(paramArrayOfInt3.length);
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      /* Error */
      public void getProcessStatesFromPids(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 26
        //   12: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: aload_1
        //   17: invokevirtual 44	android/os/Parcel:writeIntArray	([I)V
        //   20: aload_2
        //   21: ifnonnull +44 -> 65
        //   24: aload_3
        //   25: iconst_m1
        //   26: invokevirtual 48	android/os/Parcel:writeInt	(I)V
        //   29: aload_0
        //   30: getfield 19	android/os/IProcessInfoService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: iconst_1
        //   34: aload_3
        //   35: aload 4
        //   37: iconst_0
        //   38: invokeinterface 54 5 0
        //   43: pop
        //   44: aload 4
        //   46: invokevirtual 57	android/os/Parcel:readException	()V
        //   49: aload 4
        //   51: aload_2
        //   52: invokevirtual 60	android/os/Parcel:readIntArray	([I)V
        //   55: aload 4
        //   57: invokevirtual 63	android/os/Parcel:recycle	()V
        //   60: aload_3
        //   61: invokevirtual 63	android/os/Parcel:recycle	()V
        //   64: return
        //   65: aload_3
        //   66: aload_2
        //   67: arraylength
        //   68: invokevirtual 48	android/os/Parcel:writeInt	(I)V
        //   71: goto -42 -> 29
        //   74: astore_1
        //   75: aload 4
        //   77: invokevirtual 63	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 63	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramArrayOfInt1	int[]
        //   0	86	2	paramArrayOfInt2	int[]
        //   3	78	3	localParcel1	Parcel
        //   7	69	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	20	74	finally
        //   24	29	74	finally
        //   29	55	74	finally
        //   65	71	74	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/IProcessInfoService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */