package android.net;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface INetworkPolicyListener
  extends IInterface
{
  public abstract void onMeteredIfacesChanged(String[] paramArrayOfString)
    throws RemoteException;
  
  public abstract void onRestrictBackgroundBlacklistChanged(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void onRestrictBackgroundChanged(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void onRestrictBackgroundWhitelistChanged(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void onUidRulesChanged(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements INetworkPolicyListener
  {
    private static final String DESCRIPTOR = "android.net.INetworkPolicyListener";
    static final int TRANSACTION_onMeteredIfacesChanged = 2;
    static final int TRANSACTION_onRestrictBackgroundBlacklistChanged = 5;
    static final int TRANSACTION_onRestrictBackgroundChanged = 3;
    static final int TRANSACTION_onRestrictBackgroundWhitelistChanged = 4;
    static final int TRANSACTION_onUidRulesChanged = 1;
    
    public Stub()
    {
      attachInterface(this, "android.net.INetworkPolicyListener");
    }
    
    public static INetworkPolicyListener asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.net.INetworkPolicyListener");
      if ((localIInterface != null) && ((localIInterface instanceof INetworkPolicyListener))) {
        return (INetworkPolicyListener)localIInterface;
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
        paramParcel2.writeString("android.net.INetworkPolicyListener");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.net.INetworkPolicyListener");
        onUidRulesChanged(paramParcel1.readInt(), paramParcel1.readInt());
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.net.INetworkPolicyListener");
        onMeteredIfacesChanged(paramParcel1.createStringArray());
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.net.INetworkPolicyListener");
        if (paramParcel1.readInt() != 0) {
          bool = true;
        }
        onRestrictBackgroundChanged(bool);
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.net.INetworkPolicyListener");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          onRestrictBackgroundWhitelistChanged(paramInt1, bool);
          return true;
        }
      }
      paramParcel1.enforceInterface("android.net.INetworkPolicyListener");
      paramInt1 = paramParcel1.readInt();
      if (paramParcel1.readInt() != 0) {}
      for (bool = true;; bool = false)
      {
        onRestrictBackgroundBlacklistChanged(paramInt1, bool);
        return true;
      }
    }
    
    private static class Proxy
      implements INetworkPolicyListener
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
        return "android.net.INetworkPolicyListener";
      }
      
      public void onMeteredIfacesChanged(String[] paramArrayOfString)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.net.INetworkPolicyListener");
          localParcel.writeStringArray(paramArrayOfString);
          this.mRemote.transact(2, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void onRestrictBackgroundBlacklistChanged(int paramInt, boolean paramBoolean)
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
        //   14: aload 4
        //   16: iload_1
        //   17: invokevirtual 59	android/os/Parcel:writeInt	(I)V
        //   20: iload_2
        //   21: ifeq +32 -> 53
        //   24: iload_3
        //   25: istore_1
        //   26: aload 4
        //   28: iload_1
        //   29: invokevirtual 59	android/os/Parcel:writeInt	(I)V
        //   32: aload_0
        //   33: getfield 19	android/net/INetworkPolicyListener$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   36: iconst_5
        //   37: aload 4
        //   39: aconst_null
        //   40: iconst_1
        //   41: invokeinterface 49 5 0
        //   46: pop
        //   47: aload 4
        //   49: invokevirtual 52	android/os/Parcel:recycle	()V
        //   52: return
        //   53: iconst_0
        //   54: istore_1
        //   55: goto -29 -> 26
        //   58: astore 5
        //   60: aload 4
        //   62: invokevirtual 52	android/os/Parcel:recycle	()V
        //   65: aload 5
        //   67: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	68	0	this	Proxy
        //   0	68	1	paramInt	int
        //   0	68	2	paramBoolean	boolean
        //   1	24	3	i	int
        //   5	56	4	localParcel	Parcel
        //   58	8	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   7	20	58	finally
        //   26	47	58	finally
      }
      
      /* Error */
      public void onRestrictBackgroundChanged(boolean paramBoolean)
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
        //   18: invokevirtual 59	android/os/Parcel:writeInt	(I)V
        //   21: aload_0
        //   22: getfield 19	android/net/INetworkPolicyListener$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   25: iconst_3
        //   26: aload_3
        //   27: aconst_null
        //   28: iconst_1
        //   29: invokeinterface 49 5 0
        //   34: pop
        //   35: aload_3
        //   36: invokevirtual 52	android/os/Parcel:recycle	()V
        //   39: return
        //   40: iconst_0
        //   41: istore_2
        //   42: goto -26 -> 16
        //   45: astore 4
        //   47: aload_3
        //   48: invokevirtual 52	android/os/Parcel:recycle	()V
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
      
      /* Error */
      public void onRestrictBackgroundWhitelistChanged(int paramInt, boolean paramBoolean)
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
        //   14: aload 4
        //   16: iload_1
        //   17: invokevirtual 59	android/os/Parcel:writeInt	(I)V
        //   20: iload_2
        //   21: ifeq +32 -> 53
        //   24: iload_3
        //   25: istore_1
        //   26: aload 4
        //   28: iload_1
        //   29: invokevirtual 59	android/os/Parcel:writeInt	(I)V
        //   32: aload_0
        //   33: getfield 19	android/net/INetworkPolicyListener$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   36: iconst_4
        //   37: aload 4
        //   39: aconst_null
        //   40: iconst_1
        //   41: invokeinterface 49 5 0
        //   46: pop
        //   47: aload 4
        //   49: invokevirtual 52	android/os/Parcel:recycle	()V
        //   52: return
        //   53: iconst_0
        //   54: istore_1
        //   55: goto -29 -> 26
        //   58: astore 5
        //   60: aload 4
        //   62: invokevirtual 52	android/os/Parcel:recycle	()V
        //   65: aload 5
        //   67: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	68	0	this	Proxy
        //   0	68	1	paramInt	int
        //   0	68	2	paramBoolean	boolean
        //   1	24	3	i	int
        //   5	56	4	localParcel	Parcel
        //   58	8	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   7	20	58	finally
        //   26	47	58	finally
      }
      
      public void onUidRulesChanged(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.net.INetworkPolicyListener");
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
          this.mRemote.transact(1, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/INetworkPolicyListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */