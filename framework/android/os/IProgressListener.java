package android.os;

public abstract interface IProgressListener
  extends IInterface
{
  public abstract void onFinished(int paramInt, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void onProgress(int paramInt1, int paramInt2, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void onStarted(int paramInt, Bundle paramBundle)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IProgressListener
  {
    private static final String DESCRIPTOR = "android.os.IProgressListener";
    static final int TRANSACTION_onFinished = 3;
    static final int TRANSACTION_onProgress = 2;
    static final int TRANSACTION_onStarted = 1;
    
    public Stub()
    {
      attachInterface(this, "android.os.IProgressListener");
    }
    
    public static IProgressListener asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.os.IProgressListener");
      if ((localIInterface != null) && ((localIInterface instanceof IProgressListener))) {
        return (IProgressListener)localIInterface;
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
        paramParcel2.writeString("android.os.IProgressListener");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.os.IProgressListener");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onStarted(paramInt1, paramParcel1);
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.os.IProgressListener");
        paramInt1 = paramParcel1.readInt();
        paramInt2 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onProgress(paramInt1, paramInt2, paramParcel1);
          return true;
        }
      }
      paramParcel1.enforceInterface("android.os.IProgressListener");
      paramInt1 = paramParcel1.readInt();
      if (paramParcel1.readInt() != 0) {}
      for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        onFinished(paramInt1, paramParcel1);
        return true;
      }
    }
    
    private static class Proxy
      implements IProgressListener
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
        return "android.os.IProgressListener";
      }
      
      /* Error */
      public void onFinished(int paramInt, Bundle paramBundle)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: aload_3
        //   5: ldc 26
        //   7: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_3
        //   11: iload_1
        //   12: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   15: aload_2
        //   16: ifnull +33 -> 49
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   24: aload_2
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 50	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_0
        //   31: getfield 19	android/os/IProgressListener$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: iconst_3
        //   35: aload_3
        //   36: aconst_null
        //   37: iconst_1
        //   38: invokeinterface 56 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 59	android/os/Parcel:recycle	()V
        //   48: return
        //   49: aload_3
        //   50: iconst_0
        //   51: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   54: goto -24 -> 30
        //   57: astore_2
        //   58: aload_3
        //   59: invokevirtual 59	android/os/Parcel:recycle	()V
        //   62: aload_2
        //   63: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	64	0	this	Proxy
        //   0	64	1	paramInt	int
        //   0	64	2	paramBundle	Bundle
        //   3	56	3	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	15	57	finally
        //   19	30	57	finally
        //   30	44	57	finally
        //   49	54	57	finally
      }
      
      /* Error */
      public void onProgress(int paramInt1, int paramInt2, Bundle paramBundle)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: aload 4
        //   7: ldc 26
        //   9: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   12: aload 4
        //   14: iload_1
        //   15: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   18: aload 4
        //   20: iload_2
        //   21: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   24: aload_3
        //   25: ifnull +37 -> 62
        //   28: aload 4
        //   30: iconst_1
        //   31: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   34: aload_3
        //   35: aload 4
        //   37: iconst_0
        //   38: invokevirtual 50	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   41: aload_0
        //   42: getfield 19	android/os/IProgressListener$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   45: iconst_2
        //   46: aload 4
        //   48: aconst_null
        //   49: iconst_1
        //   50: invokeinterface 56 5 0
        //   55: pop
        //   56: aload 4
        //   58: invokevirtual 59	android/os/Parcel:recycle	()V
        //   61: return
        //   62: aload 4
        //   64: iconst_0
        //   65: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   68: goto -27 -> 41
        //   71: astore_3
        //   72: aload 4
        //   74: invokevirtual 59	android/os/Parcel:recycle	()V
        //   77: aload_3
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   0	79	1	paramInt1	int
        //   0	79	2	paramInt2	int
        //   0	79	3	paramBundle	Bundle
        //   3	70	4	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   5	24	71	finally
        //   28	41	71	finally
        //   41	56	71	finally
        //   62	68	71	finally
      }
      
      /* Error */
      public void onStarted(int paramInt, Bundle paramBundle)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: aload_3
        //   5: ldc 26
        //   7: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_3
        //   11: iload_1
        //   12: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   15: aload_2
        //   16: ifnull +33 -> 49
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   24: aload_2
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 50	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_0
        //   31: getfield 19	android/os/IProgressListener$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: iconst_1
        //   35: aload_3
        //   36: aconst_null
        //   37: iconst_1
        //   38: invokeinterface 56 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 59	android/os/Parcel:recycle	()V
        //   48: return
        //   49: aload_3
        //   50: iconst_0
        //   51: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   54: goto -24 -> 30
        //   57: astore_2
        //   58: aload_3
        //   59: invokevirtual 59	android/os/Parcel:recycle	()V
        //   62: aload_2
        //   63: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	64	0	this	Proxy
        //   0	64	1	paramInt	int
        //   0	64	2	paramBundle	Bundle
        //   3	56	3	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	15	57	finally
        //   19	30	57	finally
        //   30	44	57	finally
        //   49	54	57	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/IProgressListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */