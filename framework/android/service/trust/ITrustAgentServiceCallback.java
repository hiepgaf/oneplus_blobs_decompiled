package android.service.trust;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.text.TextUtils;

public abstract interface ITrustAgentServiceCallback
  extends IInterface
{
  public abstract void grantTrust(CharSequence paramCharSequence, long paramLong, int paramInt)
    throws RemoteException;
  
  public abstract void onConfigureCompleted(boolean paramBoolean, IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void revokeTrust()
    throws RemoteException;
  
  public abstract void setManagingTrust(boolean paramBoolean)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ITrustAgentServiceCallback
  {
    private static final String DESCRIPTOR = "android.service.trust.ITrustAgentServiceCallback";
    static final int TRANSACTION_grantTrust = 1;
    static final int TRANSACTION_onConfigureCompleted = 4;
    static final int TRANSACTION_revokeTrust = 2;
    static final int TRANSACTION_setManagingTrust = 3;
    
    public Stub()
    {
      attachInterface(this, "android.service.trust.ITrustAgentServiceCallback");
    }
    
    public static ITrustAgentServiceCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.service.trust.ITrustAgentServiceCallback");
      if ((localIInterface != null) && ((localIInterface instanceof ITrustAgentServiceCallback))) {
        return (ITrustAgentServiceCallback)localIInterface;
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
      boolean bool = false;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.service.trust.ITrustAgentServiceCallback");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.service.trust.ITrustAgentServiceCallback");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          grantTrust(paramParcel2, paramParcel1.readLong(), paramParcel1.readInt());
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.service.trust.ITrustAgentServiceCallback");
        revokeTrust();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.service.trust.ITrustAgentServiceCallback");
        if (paramParcel1.readInt() != 0) {
          bool = true;
        }
        setManagingTrust(bool);
        return true;
      }
      paramParcel1.enforceInterface("android.service.trust.ITrustAgentServiceCallback");
      if (paramParcel1.readInt() != 0) {}
      for (bool = true;; bool = false)
      {
        onConfigureCompleted(bool, paramParcel1.readStrongBinder());
        return true;
      }
    }
    
    private static class Proxy
      implements ITrustAgentServiceCallback
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
        return "android.service.trust.ITrustAgentServiceCallback";
      }
      
      /* Error */
      public void grantTrust(CharSequence paramCharSequence, long paramLong, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 5
        //   5: aload 5
        //   7: ldc 26
        //   9: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   12: aload_1
        //   13: ifnull +50 -> 63
        //   16: aload 5
        //   18: iconst_1
        //   19: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   22: aload_1
        //   23: aload 5
        //   25: iconst_0
        //   26: invokestatic 50	android/text/TextUtils:writeToParcel	(Ljava/lang/CharSequence;Landroid/os/Parcel;I)V
        //   29: aload 5
        //   31: lload_2
        //   32: invokevirtual 54	android/os/Parcel:writeLong	(J)V
        //   35: aload 5
        //   37: iload 4
        //   39: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   42: aload_0
        //   43: getfield 19	android/service/trust/ITrustAgentServiceCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   46: iconst_1
        //   47: aload 5
        //   49: aconst_null
        //   50: iconst_1
        //   51: invokeinterface 60 5 0
        //   56: pop
        //   57: aload 5
        //   59: invokevirtual 63	android/os/Parcel:recycle	()V
        //   62: return
        //   63: aload 5
        //   65: iconst_0
        //   66: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   69: goto -40 -> 29
        //   72: astore_1
        //   73: aload 5
        //   75: invokevirtual 63	android/os/Parcel:recycle	()V
        //   78: aload_1
        //   79: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	80	0	this	Proxy
        //   0	80	1	paramCharSequence	CharSequence
        //   0	80	2	paramLong	long
        //   0	80	4	paramInt	int
        //   3	71	5	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   5	12	72	finally
        //   16	29	72	finally
        //   29	57	72	finally
        //   63	69	72	finally
      }
      
      /* Error */
      public void onConfigureCompleted(boolean paramBoolean, IBinder paramIBinder)
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
        //   26: aload_2
        //   27: invokevirtual 69	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   30: aload_0
        //   31: getfield 19	android/service/trust/ITrustAgentServiceCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: iconst_4
        //   35: aload 4
        //   37: aconst_null
        //   38: iconst_1
        //   39: invokeinterface 60 5 0
        //   44: pop
        //   45: aload 4
        //   47: invokevirtual 63	android/os/Parcel:recycle	()V
        //   50: return
        //   51: iconst_0
        //   52: istore_3
        //   53: goto -35 -> 18
        //   56: astore_2
        //   57: aload 4
        //   59: invokevirtual 63	android/os/Parcel:recycle	()V
        //   62: aload_2
        //   63: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	64	0	this	Proxy
        //   0	64	1	paramBoolean	boolean
        //   0	64	2	paramIBinder	IBinder
        //   1	52	3	i	int
        //   5	53	4	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   7	14	56	finally
        //   18	45	56	finally
      }
      
      public void revokeTrust()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.service.trust.ITrustAgentServiceCallback");
          this.mRemote.transact(2, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void setManagingTrust(boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore_2
        //   2: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore_3
        //   6: aload_3
        //   7: ldc 26
        //   9: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   12: iload_1
        //   13: ifeq +27 -> 40
        //   16: aload_3
        //   17: iload_2
        //   18: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   21: aload_0
        //   22: getfield 19	android/service/trust/ITrustAgentServiceCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   25: iconst_3
        //   26: aload_3
        //   27: aconst_null
        //   28: iconst_1
        //   29: invokeinterface 60 5 0
        //   34: pop
        //   35: aload_3
        //   36: invokevirtual 63	android/os/Parcel:recycle	()V
        //   39: return
        //   40: iconst_0
        //   41: istore_2
        //   42: goto -26 -> 16
        //   45: astore 4
        //   47: aload_3
        //   48: invokevirtual 63	android/os/Parcel:recycle	()V
        //   51: aload 4
        //   53: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	54	0	this	Proxy
        //   0	54	1	paramBoolean	boolean
        //   1	41	2	i	int
        //   5	43	3	localParcel	Parcel
        //   45	7	4	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   6	12	45	finally
        //   16	35	45	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/trust/ITrustAgentServiceCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */