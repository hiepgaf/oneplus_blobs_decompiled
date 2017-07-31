package android.app.trust;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface ITrustListener
  extends IInterface
{
  public abstract void onTrustChanged(boolean paramBoolean, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void onTrustManagedChanged(boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ITrustListener
  {
    private static final String DESCRIPTOR = "android.app.trust.ITrustListener";
    static final int TRANSACTION_onTrustChanged = 1;
    static final int TRANSACTION_onTrustManagedChanged = 2;
    
    public Stub()
    {
      attachInterface(this, "android.app.trust.ITrustListener");
    }
    
    public static ITrustListener asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.app.trust.ITrustListener");
      if ((localIInterface != null) && ((localIInterface instanceof ITrustListener))) {
        return (ITrustListener)localIInterface;
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
        paramParcel2.writeString("android.app.trust.ITrustListener");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.app.trust.ITrustListener");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          onTrustChanged(bool, paramParcel1.readInt(), paramParcel1.readInt());
          return true;
        }
      }
      paramParcel1.enforceInterface("android.app.trust.ITrustListener");
      if (paramParcel1.readInt() != 0) {}
      for (boolean bool = true;; bool = false)
      {
        onTrustManagedChanged(bool, paramParcel1.readInt());
        return true;
      }
    }
    
    private static class Proxy
      implements ITrustListener
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
        return "android.app.trust.ITrustListener";
      }
      
      /* Error */
      public void onTrustChanged(boolean paramBoolean, int paramInt1, int paramInt2)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore 4
        //   3: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 5
        //   8: aload 5
        //   10: ldc 26
        //   12: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: iload_1
        //   16: ifeq +43 -> 59
        //   19: aload 5
        //   21: iload 4
        //   23: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   26: aload 5
        //   28: iload_2
        //   29: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   32: aload 5
        //   34: iload_3
        //   35: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   38: aload_0
        //   39: getfield 19	android/app/trust/ITrustListener$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   42: iconst_1
        //   43: aload 5
        //   45: aconst_null
        //   46: iconst_1
        //   47: invokeinterface 50 5 0
        //   52: pop
        //   53: aload 5
        //   55: invokevirtual 53	android/os/Parcel:recycle	()V
        //   58: return
        //   59: iconst_0
        //   60: istore 4
        //   62: goto -43 -> 19
        //   65: astore 6
        //   67: aload 5
        //   69: invokevirtual 53	android/os/Parcel:recycle	()V
        //   72: aload 6
        //   74: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	75	0	this	Proxy
        //   0	75	1	paramBoolean	boolean
        //   0	75	2	paramInt1	int
        //   0	75	3	paramInt2	int
        //   1	60	4	i	int
        //   6	62	5	localParcel	Parcel
        //   65	8	6	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   8	15	65	finally
        //   19	53	65	finally
      }
      
      /* Error */
      public void onTrustManagedChanged(boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore_3
        //   2: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore 4
        //   7: aload 4
        //   9: ldc 26
        //   11: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: iload_1
        //   15: ifeq +36 -> 51
        //   18: aload 4
        //   20: iload_3
        //   21: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   24: aload 4
        //   26: iload_2
        //   27: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   30: aload_0
        //   31: getfield 19	android/app/trust/ITrustListener$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: iconst_2
        //   35: aload 4
        //   37: aconst_null
        //   38: iconst_1
        //   39: invokeinterface 50 5 0
        //   44: pop
        //   45: aload 4
        //   47: invokevirtual 53	android/os/Parcel:recycle	()V
        //   50: return
        //   51: iconst_0
        //   52: istore_3
        //   53: goto -35 -> 18
        //   56: astore 5
        //   58: aload 4
        //   60: invokevirtual 53	android/os/Parcel:recycle	()V
        //   63: aload 5
        //   65: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	66	0	this	Proxy
        //   0	66	1	paramBoolean	boolean
        //   0	66	2	paramInt	int
        //   1	52	3	i	int
        //   5	54	4	localParcel	Parcel
        //   56	8	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   7	14	56	finally
        //   18	45	56	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/trust/ITrustListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */