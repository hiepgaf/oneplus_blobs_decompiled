package android.nfc;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IAppCallback
  extends IInterface
{
  public abstract BeamShareData createBeamShareData(byte paramByte)
    throws RemoteException;
  
  public abstract void onNdefPushComplete(byte paramByte)
    throws RemoteException;
  
  public abstract void onTagDiscovered(Tag paramTag)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IAppCallback
  {
    private static final String DESCRIPTOR = "android.nfc.IAppCallback";
    static final int TRANSACTION_createBeamShareData = 1;
    static final int TRANSACTION_onNdefPushComplete = 2;
    static final int TRANSACTION_onTagDiscovered = 3;
    
    public Stub()
    {
      attachInterface(this, "android.nfc.IAppCallback");
    }
    
    public static IAppCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.nfc.IAppCallback");
      if ((localIInterface != null) && ((localIInterface instanceof IAppCallback))) {
        return (IAppCallback)localIInterface;
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
        paramParcel2.writeString("android.nfc.IAppCallback");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.nfc.IAppCallback");
        paramParcel1 = createBeamShareData(paramParcel1.readByte());
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
        paramParcel1.enforceInterface("android.nfc.IAppCallback");
        onNdefPushComplete(paramParcel1.readByte());
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.nfc.IAppCallback");
      if (paramParcel1.readInt() != 0) {}
      for (paramParcel1 = (Tag)Tag.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        onTagDiscovered(paramParcel1);
        paramParcel2.writeNoException();
        return true;
      }
    }
    
    private static class Proxy
      implements IAppCallback
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
      
      /* Error */
      public BeamShareData createBeamShareData(byte paramByte)
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
        //   17: invokevirtual 42	android/os/Parcel:writeByte	(B)V
        //   20: aload_0
        //   21: getfield 19	android/nfc/IAppCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: iconst_1
        //   25: aload_3
        //   26: aload 4
        //   28: iconst_0
        //   29: invokeinterface 48 5 0
        //   34: pop
        //   35: aload 4
        //   37: invokevirtual 51	android/os/Parcel:readException	()V
        //   40: aload 4
        //   42: invokevirtual 55	android/os/Parcel:readInt	()I
        //   45: ifeq +28 -> 73
        //   48: getstatic 61	android/nfc/BeamShareData:CREATOR	Landroid/os/Parcelable$Creator;
        //   51: aload 4
        //   53: invokeinterface 67 2 0
        //   58: checkcast 57	android/nfc/BeamShareData
        //   61: astore_2
        //   62: aload 4
        //   64: invokevirtual 70	android/os/Parcel:recycle	()V
        //   67: aload_3
        //   68: invokevirtual 70	android/os/Parcel:recycle	()V
        //   71: aload_2
        //   72: areturn
        //   73: aconst_null
        //   74: astore_2
        //   75: goto -13 -> 62
        //   78: astore_2
        //   79: aload 4
        //   81: invokevirtual 70	android/os/Parcel:recycle	()V
        //   84: aload_3
        //   85: invokevirtual 70	android/os/Parcel:recycle	()V
        //   88: aload_2
        //   89: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	90	0	this	Proxy
        //   0	90	1	paramByte	byte
        //   61	14	2	localBeamShareData	BeamShareData
        //   78	11	2	localObject	Object
        //   3	82	3	localParcel1	Parcel
        //   7	73	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	62	78	finally
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.nfc.IAppCallback";
      }
      
      public void onNdefPushComplete(byte paramByte)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.nfc.IAppCallback");
          localParcel1.writeByte(paramByte);
          this.mRemote.transact(2, localParcel1, localParcel2, 0);
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
      public void onTagDiscovered(Tag paramTag)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 34
        //   11: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +41 -> 56
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 80	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 86	android/nfc/Tag:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/nfc/IAppCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: iconst_3
        //   34: aload_2
        //   35: aload_3
        //   36: iconst_0
        //   37: invokeinterface 48 5 0
        //   42: pop
        //   43: aload_3
        //   44: invokevirtual 51	android/os/Parcel:readException	()V
        //   47: aload_3
        //   48: invokevirtual 70	android/os/Parcel:recycle	()V
        //   51: aload_2
        //   52: invokevirtual 70	android/os/Parcel:recycle	()V
        //   55: return
        //   56: aload_2
        //   57: iconst_0
        //   58: invokevirtual 80	android/os/Parcel:writeInt	(I)V
        //   61: goto -32 -> 29
        //   64: astore_1
        //   65: aload_3
        //   66: invokevirtual 70	android/os/Parcel:recycle	()V
        //   69: aload_2
        //   70: invokevirtual 70	android/os/Parcel:recycle	()V
        //   73: aload_1
        //   74: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	75	0	this	Proxy
        //   0	75	1	paramTag	Tag
        //   3	67	2	localParcel1	Parcel
        //   7	59	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	64	finally
        //   18	29	64	finally
        //   29	47	64	finally
        //   56	61	64	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/nfc/IAppCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */