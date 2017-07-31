package android.nfc;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface INfcAdapterExtras
  extends IInterface
{
  public abstract void authenticate(String paramString, byte[] paramArrayOfByte)
    throws RemoteException;
  
  public abstract Bundle close(String paramString, IBinder paramIBinder)
    throws RemoteException;
  
  public abstract int getCardEmulationRoute(String paramString)
    throws RemoteException;
  
  public abstract String getDriverName(String paramString)
    throws RemoteException;
  
  public abstract Bundle open(String paramString, IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void setCardEmulationRoute(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract Bundle transceive(String paramString, byte[] paramArrayOfByte)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements INfcAdapterExtras
  {
    private static final String DESCRIPTOR = "android.nfc.INfcAdapterExtras";
    static final int TRANSACTION_authenticate = 6;
    static final int TRANSACTION_close = 2;
    static final int TRANSACTION_getCardEmulationRoute = 4;
    static final int TRANSACTION_getDriverName = 7;
    static final int TRANSACTION_open = 1;
    static final int TRANSACTION_setCardEmulationRoute = 5;
    static final int TRANSACTION_transceive = 3;
    
    public Stub()
    {
      attachInterface(this, "android.nfc.INfcAdapterExtras");
    }
    
    public static INfcAdapterExtras asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.nfc.INfcAdapterExtras");
      if ((localIInterface != null) && ((localIInterface instanceof INfcAdapterExtras))) {
        return (INfcAdapterExtras)localIInterface;
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
        paramParcel2.writeString("android.nfc.INfcAdapterExtras");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.nfc.INfcAdapterExtras");
        paramParcel1 = open(paramParcel1.readString(), paramParcel1.readStrongBinder());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
          return true;
        }
        paramParcel2.writeInt(0);
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.nfc.INfcAdapterExtras");
        paramParcel1 = close(paramParcel1.readString(), paramParcel1.readStrongBinder());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
          return true;
        }
        paramParcel2.writeInt(0);
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.nfc.INfcAdapterExtras");
        paramParcel1 = transceive(paramParcel1.readString(), paramParcel1.createByteArray());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
          return true;
        }
        paramParcel2.writeInt(0);
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.nfc.INfcAdapterExtras");
        paramInt1 = getCardEmulationRoute(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.nfc.INfcAdapterExtras");
        setCardEmulationRoute(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.nfc.INfcAdapterExtras");
        authenticate(paramParcel1.readString(), paramParcel1.createByteArray());
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.nfc.INfcAdapterExtras");
      paramParcel1 = getDriverName(paramParcel1.readString());
      paramParcel2.writeNoException();
      paramParcel2.writeString(paramParcel1);
      return true;
    }
    
    private static class Proxy
      implements INfcAdapterExtras
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
      
      public void authenticate(String paramString, byte[] paramArrayOfByte)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.nfc.INfcAdapterExtras");
          localParcel1.writeString(paramString);
          localParcel1.writeByteArray(paramArrayOfByte);
          this.mRemote.transact(6, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public Bundle close(String paramString, IBinder paramIBinder)
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
        //   16: aload_1
        //   17: invokevirtual 41	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   20: aload_3
        //   21: aload_2
        //   22: invokevirtual 63	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   25: aload_0
        //   26: getfield 19	android/nfc/INfcAdapterExtras$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: iconst_2
        //   30: aload_3
        //   31: aload 4
        //   33: iconst_0
        //   34: invokeinterface 51 5 0
        //   39: pop
        //   40: aload 4
        //   42: invokevirtual 54	android/os/Parcel:readException	()V
        //   45: aload 4
        //   47: invokevirtual 67	android/os/Parcel:readInt	()I
        //   50: ifeq +28 -> 78
        //   53: getstatic 73	android/os/Bundle:CREATOR	Landroid/os/Parcelable$Creator;
        //   56: aload 4
        //   58: invokeinterface 79 2 0
        //   63: checkcast 69	android/os/Bundle
        //   66: astore_1
        //   67: aload 4
        //   69: invokevirtual 57	android/os/Parcel:recycle	()V
        //   72: aload_3
        //   73: invokevirtual 57	android/os/Parcel:recycle	()V
        //   76: aload_1
        //   77: areturn
        //   78: aconst_null
        //   79: astore_1
        //   80: goto -13 -> 67
        //   83: astore_1
        //   84: aload 4
        //   86: invokevirtual 57	android/os/Parcel:recycle	()V
        //   89: aload_3
        //   90: invokevirtual 57	android/os/Parcel:recycle	()V
        //   93: aload_1
        //   94: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	95	0	this	Proxy
        //   0	95	1	paramString	String
        //   0	95	2	paramIBinder	IBinder
        //   3	87	3	localParcel1	Parcel
        //   7	78	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	67	83	finally
      }
      
      public int getCardEmulationRoute(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.nfc.INfcAdapterExtras");
          localParcel1.writeString(paramString);
          this.mRemote.transact(4, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getDriverName(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.nfc.INfcAdapterExtras");
          localParcel1.writeString(paramString);
          this.mRemote.transact(7, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString = localParcel2.readString();
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.nfc.INfcAdapterExtras";
      }
      
      /* Error */
      public Bundle open(String paramString, IBinder paramIBinder)
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
        //   16: aload_1
        //   17: invokevirtual 41	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   20: aload_3
        //   21: aload_2
        //   22: invokevirtual 63	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   25: aload_0
        //   26: getfield 19	android/nfc/INfcAdapterExtras$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: iconst_1
        //   30: aload_3
        //   31: aload 4
        //   33: iconst_0
        //   34: invokeinterface 51 5 0
        //   39: pop
        //   40: aload 4
        //   42: invokevirtual 54	android/os/Parcel:readException	()V
        //   45: aload 4
        //   47: invokevirtual 67	android/os/Parcel:readInt	()I
        //   50: ifeq +28 -> 78
        //   53: getstatic 73	android/os/Bundle:CREATOR	Landroid/os/Parcelable$Creator;
        //   56: aload 4
        //   58: invokeinterface 79 2 0
        //   63: checkcast 69	android/os/Bundle
        //   66: astore_1
        //   67: aload 4
        //   69: invokevirtual 57	android/os/Parcel:recycle	()V
        //   72: aload_3
        //   73: invokevirtual 57	android/os/Parcel:recycle	()V
        //   76: aload_1
        //   77: areturn
        //   78: aconst_null
        //   79: astore_1
        //   80: goto -13 -> 67
        //   83: astore_1
        //   84: aload 4
        //   86: invokevirtual 57	android/os/Parcel:recycle	()V
        //   89: aload_3
        //   90: invokevirtual 57	android/os/Parcel:recycle	()V
        //   93: aload_1
        //   94: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	95	0	this	Proxy
        //   0	95	1	paramString	String
        //   0	95	2	paramIBinder	IBinder
        //   3	87	3	localParcel1	Parcel
        //   7	78	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	67	83	finally
      }
      
      public void setCardEmulationRoute(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.nfc.INfcAdapterExtras");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(5, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public Bundle transceive(String paramString, byte[] paramArrayOfByte)
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
        //   16: aload_1
        //   17: invokevirtual 41	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   20: aload_3
        //   21: aload_2
        //   22: invokevirtual 45	android/os/Parcel:writeByteArray	([B)V
        //   25: aload_0
        //   26: getfield 19	android/nfc/INfcAdapterExtras$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: iconst_3
        //   30: aload_3
        //   31: aload 4
        //   33: iconst_0
        //   34: invokeinterface 51 5 0
        //   39: pop
        //   40: aload 4
        //   42: invokevirtual 54	android/os/Parcel:readException	()V
        //   45: aload 4
        //   47: invokevirtual 67	android/os/Parcel:readInt	()I
        //   50: ifeq +28 -> 78
        //   53: getstatic 73	android/os/Bundle:CREATOR	Landroid/os/Parcelable$Creator;
        //   56: aload 4
        //   58: invokeinterface 79 2 0
        //   63: checkcast 69	android/os/Bundle
        //   66: astore_1
        //   67: aload 4
        //   69: invokevirtual 57	android/os/Parcel:recycle	()V
        //   72: aload_3
        //   73: invokevirtual 57	android/os/Parcel:recycle	()V
        //   76: aload_1
        //   77: areturn
        //   78: aconst_null
        //   79: astore_1
        //   80: goto -13 -> 67
        //   83: astore_1
        //   84: aload 4
        //   86: invokevirtual 57	android/os/Parcel:recycle	()V
        //   89: aload_3
        //   90: invokevirtual 57	android/os/Parcel:recycle	()V
        //   93: aload_1
        //   94: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	95	0	this	Proxy
        //   0	95	1	paramString	String
        //   0	95	2	paramArrayOfByte	byte[]
        //   3	87	3	localParcel1	Parcel
        //   7	78	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	67	83	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/nfc/INfcAdapterExtras.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */