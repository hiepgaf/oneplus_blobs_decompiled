package android.location;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface ILocationListener
  extends IInterface
{
  public abstract void onLocationChanged(Location paramLocation)
    throws RemoteException;
  
  public abstract void onProviderDisabled(String paramString)
    throws RemoteException;
  
  public abstract void onProviderEnabled(String paramString)
    throws RemoteException;
  
  public abstract void onStatusChanged(String paramString, int paramInt, Bundle paramBundle)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ILocationListener
  {
    private static final String DESCRIPTOR = "android.location.ILocationListener";
    static final int TRANSACTION_onLocationChanged = 1;
    static final int TRANSACTION_onProviderDisabled = 4;
    static final int TRANSACTION_onProviderEnabled = 3;
    static final int TRANSACTION_onStatusChanged = 2;
    
    public Stub()
    {
      attachInterface(this, "android.location.ILocationListener");
    }
    
    public static ILocationListener asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.location.ILocationListener");
      if ((localIInterface != null) && ((localIInterface instanceof ILocationListener))) {
        return (ILocationListener)localIInterface;
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
        paramParcel2.writeString("android.location.ILocationListener");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.location.ILocationListener");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Location)Location.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onLocationChanged(paramParcel1);
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.location.ILocationListener");
        paramParcel2 = paramParcel1.readString();
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onStatusChanged(paramParcel2, paramInt1, paramParcel1);
          return true;
        }
      case 3: 
        paramParcel1.enforceInterface("android.location.ILocationListener");
        onProviderEnabled(paramParcel1.readString());
        return true;
      }
      paramParcel1.enforceInterface("android.location.ILocationListener");
      onProviderDisabled(paramParcel1.readString());
      return true;
    }
    
    private static class Proxy
      implements ILocationListener
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
        return "android.location.ILocationListener";
      }
      
      /* Error */
      public void onLocationChanged(Location paramLocation)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 26
        //   7: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +33 -> 44
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 50	android/location/Location:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/location/ILocationListener$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: iconst_1
        //   30: aload_2
        //   31: aconst_null
        //   32: iconst_1
        //   33: invokeinterface 56 5 0
        //   38: pop
        //   39: aload_2
        //   40: invokevirtual 59	android/os/Parcel:recycle	()V
        //   43: return
        //   44: aload_2
        //   45: iconst_0
        //   46: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   49: goto -24 -> 25
        //   52: astore_1
        //   53: aload_2
        //   54: invokevirtual 59	android/os/Parcel:recycle	()V
        //   57: aload_1
        //   58: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	59	0	this	Proxy
        //   0	59	1	paramLocation	Location
        //   3	51	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	52	finally
        //   14	25	52	finally
        //   25	39	52	finally
        //   44	49	52	finally
      }
      
      public void onProviderDisabled(String paramString)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.location.ILocationListener");
          localParcel.writeString(paramString);
          this.mRemote.transact(4, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onProviderEnabled(String paramString)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.location.ILocationListener");
          localParcel.writeString(paramString);
          this.mRemote.transact(3, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void onStatusChanged(String paramString, int paramInt, Bundle paramBundle)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: aload 4
        //   7: ldc 26
        //   9: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   12: aload 4
        //   14: aload_1
        //   15: invokevirtual 64	android/os/Parcel:writeString	(Ljava/lang/String;)V
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
        //   38: invokevirtual 70	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   41: aload_0
        //   42: getfield 19	android/location/ILocationListener$Stub$Proxy:mRemote	Landroid/os/IBinder;
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
        //   71: astore_1
        //   72: aload 4
        //   74: invokevirtual 59	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   0	79	1	paramString	String
        //   0	79	2	paramInt	int
        //   0	79	3	paramBundle	Bundle
        //   3	70	4	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   5	24	71	finally
        //   28	41	71	finally
        //   41	56	71	finally
        //   62	68	71	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/ILocationListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */