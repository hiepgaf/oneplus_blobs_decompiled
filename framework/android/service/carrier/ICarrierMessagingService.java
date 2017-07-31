package android.service.carrier;

import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import java.util.List;

public abstract interface ICarrierMessagingService
  extends IInterface
{
  public abstract void downloadMms(Uri paramUri1, int paramInt, Uri paramUri2, ICarrierMessagingCallback paramICarrierMessagingCallback)
    throws RemoteException;
  
  public abstract void filterSms(MessagePdu paramMessagePdu, String paramString, int paramInt1, int paramInt2, ICarrierMessagingCallback paramICarrierMessagingCallback)
    throws RemoteException;
  
  public abstract void sendDataSms(byte[] paramArrayOfByte, int paramInt1, String paramString, int paramInt2, int paramInt3, ICarrierMessagingCallback paramICarrierMessagingCallback)
    throws RemoteException;
  
  public abstract void sendMms(Uri paramUri1, int paramInt, Uri paramUri2, ICarrierMessagingCallback paramICarrierMessagingCallback)
    throws RemoteException;
  
  public abstract void sendMultipartTextSms(List<String> paramList, int paramInt1, String paramString, int paramInt2, ICarrierMessagingCallback paramICarrierMessagingCallback)
    throws RemoteException;
  
  public abstract void sendTextSms(String paramString1, int paramInt1, String paramString2, int paramInt2, ICarrierMessagingCallback paramICarrierMessagingCallback)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ICarrierMessagingService
  {
    private static final String DESCRIPTOR = "android.service.carrier.ICarrierMessagingService";
    static final int TRANSACTION_downloadMms = 6;
    static final int TRANSACTION_filterSms = 1;
    static final int TRANSACTION_sendDataSms = 3;
    static final int TRANSACTION_sendMms = 5;
    static final int TRANSACTION_sendMultipartTextSms = 4;
    static final int TRANSACTION_sendTextSms = 2;
    
    public Stub()
    {
      attachInterface(this, "android.service.carrier.ICarrierMessagingService");
    }
    
    public static ICarrierMessagingService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.service.carrier.ICarrierMessagingService");
      if ((localIInterface != null) && ((localIInterface instanceof ICarrierMessagingService))) {
        return (ICarrierMessagingService)localIInterface;
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
        paramParcel2.writeString("android.service.carrier.ICarrierMessagingService");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.service.carrier.ICarrierMessagingService");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (MessagePdu)MessagePdu.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          filterSms(paramParcel2, paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt(), ICarrierMessagingCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.service.carrier.ICarrierMessagingService");
        sendTextSms(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.readInt(), ICarrierMessagingCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.service.carrier.ICarrierMessagingService");
        sendDataSms(paramParcel1.createByteArray(), paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt(), ICarrierMessagingCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.service.carrier.ICarrierMessagingService");
        sendMultipartTextSms(paramParcel1.createStringArrayList(), paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.readInt(), ICarrierMessagingCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.service.carrier.ICarrierMessagingService");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel2 = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label326;
          }
        }
        label326:
        for (localUri = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);; localUri = null)
        {
          sendMms(paramParcel2, paramInt1, localUri, ICarrierMessagingCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
          return true;
          paramParcel2 = null;
          break;
        }
      }
      paramParcel1.enforceInterface("android.service.carrier.ICarrierMessagingService");
      if (paramParcel1.readInt() != 0)
      {
        paramParcel2 = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() == 0) {
          break label406;
        }
      }
      label406:
      for (Uri localUri = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);; localUri = null)
      {
        downloadMms(paramParcel2, paramInt1, localUri, ICarrierMessagingCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        return true;
        paramParcel2 = null;
        break;
      }
    }
    
    private static class Proxy
      implements ICarrierMessagingService
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
      
      public void downloadMms(Uri paramUri1, int paramInt, Uri paramUri2, ICarrierMessagingCallback paramICarrierMessagingCallback)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel.writeInterfaceToken("android.service.carrier.ICarrierMessagingService");
            if (paramUri1 != null)
            {
              localParcel.writeInt(1);
              paramUri1.writeToParcel(localParcel, 0);
              localParcel.writeInt(paramInt);
              if (paramUri2 != null)
              {
                localParcel.writeInt(1);
                paramUri2.writeToParcel(localParcel, 0);
                paramUri1 = (Uri)localObject;
                if (paramICarrierMessagingCallback != null) {
                  paramUri1 = paramICarrierMessagingCallback.asBinder();
                }
                localParcel.writeStrongBinder(paramUri1);
                this.mRemote.transact(6, localParcel, null, 1);
              }
            }
            else
            {
              localParcel.writeInt(0);
              continue;
            }
            localParcel.writeInt(0);
          }
          finally
          {
            localParcel.recycle();
          }
        }
      }
      
      /* Error */
      public void filterSms(MessagePdu paramMessagePdu, String paramString, int paramInt1, int paramInt2, ICarrierMessagingCallback paramICarrierMessagingCallback)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 6
        //   3: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 7
        //   8: aload 7
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +78 -> 94
        //   19: aload 7
        //   21: iconst_1
        //   22: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   25: aload_1
        //   26: aload 7
        //   28: iconst_0
        //   29: invokevirtual 70	android/service/carrier/MessagePdu:writeToParcel	(Landroid/os/Parcel;I)V
        //   32: aload 7
        //   34: aload_2
        //   35: invokevirtual 73	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   38: aload 7
        //   40: iload_3
        //   41: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   44: aload 7
        //   46: iload 4
        //   48: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   51: aload 6
        //   53: astore_1
        //   54: aload 5
        //   56: ifnull +11 -> 67
        //   59: aload 5
        //   61: invokeinterface 52 1 0
        //   66: astore_1
        //   67: aload 7
        //   69: aload_1
        //   70: invokevirtual 55	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   73: aload_0
        //   74: getfield 19	android/service/carrier/ICarrierMessagingService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   77: iconst_1
        //   78: aload 7
        //   80: aconst_null
        //   81: iconst_1
        //   82: invokeinterface 61 5 0
        //   87: pop
        //   88: aload 7
        //   90: invokevirtual 64	android/os/Parcel:recycle	()V
        //   93: return
        //   94: aload 7
        //   96: iconst_0
        //   97: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   100: goto -68 -> 32
        //   103: astore_1
        //   104: aload 7
        //   106: invokevirtual 64	android/os/Parcel:recycle	()V
        //   109: aload_1
        //   110: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	111	0	this	Proxy
        //   0	111	1	paramMessagePdu	MessagePdu
        //   0	111	2	paramString	String
        //   0	111	3	paramInt1	int
        //   0	111	4	paramInt2	int
        //   0	111	5	paramICarrierMessagingCallback	ICarrierMessagingCallback
        //   1	51	6	localObject	Object
        //   6	99	7	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	15	103	finally
        //   19	32	103	finally
        //   32	51	103	finally
        //   59	67	103	finally
        //   67	88	103	finally
        //   94	100	103	finally
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.service.carrier.ICarrierMessagingService";
      }
      
      public void sendDataSms(byte[] paramArrayOfByte, int paramInt1, String paramString, int paramInt2, int paramInt3, ICarrierMessagingCallback paramICarrierMessagingCallback)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.service.carrier.ICarrierMessagingService");
          localParcel.writeByteArray(paramArrayOfByte);
          localParcel.writeInt(paramInt1);
          localParcel.writeString(paramString);
          localParcel.writeInt(paramInt2);
          localParcel.writeInt(paramInt3);
          paramArrayOfByte = (byte[])localObject;
          if (paramICarrierMessagingCallback != null) {
            paramArrayOfByte = paramICarrierMessagingCallback.asBinder();
          }
          localParcel.writeStrongBinder(paramArrayOfByte);
          this.mRemote.transact(3, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void sendMms(Uri paramUri1, int paramInt, Uri paramUri2, ICarrierMessagingCallback paramICarrierMessagingCallback)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel.writeInterfaceToken("android.service.carrier.ICarrierMessagingService");
            if (paramUri1 != null)
            {
              localParcel.writeInt(1);
              paramUri1.writeToParcel(localParcel, 0);
              localParcel.writeInt(paramInt);
              if (paramUri2 != null)
              {
                localParcel.writeInt(1);
                paramUri2.writeToParcel(localParcel, 0);
                paramUri1 = (Uri)localObject;
                if (paramICarrierMessagingCallback != null) {
                  paramUri1 = paramICarrierMessagingCallback.asBinder();
                }
                localParcel.writeStrongBinder(paramUri1);
                this.mRemote.transact(5, localParcel, null, 1);
              }
            }
            else
            {
              localParcel.writeInt(0);
              continue;
            }
            localParcel.writeInt(0);
          }
          finally
          {
            localParcel.recycle();
          }
        }
      }
      
      public void sendMultipartTextSms(List<String> paramList, int paramInt1, String paramString, int paramInt2, ICarrierMessagingCallback paramICarrierMessagingCallback)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.service.carrier.ICarrierMessagingService");
          localParcel.writeStringList(paramList);
          localParcel.writeInt(paramInt1);
          localParcel.writeString(paramString);
          localParcel.writeInt(paramInt2);
          paramList = (List<String>)localObject;
          if (paramICarrierMessagingCallback != null) {
            paramList = paramICarrierMessagingCallback.asBinder();
          }
          localParcel.writeStrongBinder(paramList);
          this.mRemote.transact(4, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void sendTextSms(String paramString1, int paramInt1, String paramString2, int paramInt2, ICarrierMessagingCallback paramICarrierMessagingCallback)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.service.carrier.ICarrierMessagingService");
          localParcel.writeString(paramString1);
          localParcel.writeInt(paramInt1);
          localParcel.writeString(paramString2);
          localParcel.writeInt(paramInt2);
          paramString1 = (String)localObject;
          if (paramICarrierMessagingCallback != null) {
            paramString1 = paramICarrierMessagingCallback.asBinder();
          }
          localParcel.writeStrongBinder(paramString1);
          this.mRemote.transact(2, localParcel, null, 1);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/carrier/ICarrierMessagingService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */