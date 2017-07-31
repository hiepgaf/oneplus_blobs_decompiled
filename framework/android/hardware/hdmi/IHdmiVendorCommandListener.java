package android.hardware.hdmi;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IHdmiVendorCommandListener
  extends IInterface
{
  public abstract void onControlStateChanged(boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public abstract void onReceived(int paramInt1, int paramInt2, byte[] paramArrayOfByte, boolean paramBoolean)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IHdmiVendorCommandListener
  {
    private static final String DESCRIPTOR = "android.hardware.hdmi.IHdmiVendorCommandListener";
    static final int TRANSACTION_onControlStateChanged = 2;
    static final int TRANSACTION_onReceived = 1;
    
    public Stub()
    {
      attachInterface(this, "android.hardware.hdmi.IHdmiVendorCommandListener");
    }
    
    public static IHdmiVendorCommandListener asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.hardware.hdmi.IHdmiVendorCommandListener");
      if ((localIInterface != null) && ((localIInterface instanceof IHdmiVendorCommandListener))) {
        return (IHdmiVendorCommandListener)localIInterface;
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
        paramParcel2.writeString("android.hardware.hdmi.IHdmiVendorCommandListener");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.hardware.hdmi.IHdmiVendorCommandListener");
        paramInt1 = paramParcel1.readInt();
        paramInt2 = paramParcel1.readInt();
        byte[] arrayOfByte = paramParcel1.createByteArray();
        if (paramParcel1.readInt() != 0) {
          bool = true;
        }
        onReceived(paramInt1, paramInt2, arrayOfByte, bool);
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.hardware.hdmi.IHdmiVendorCommandListener");
      if (paramParcel1.readInt() != 0) {}
      for (bool = true;; bool = false)
      {
        onControlStateChanged(bool, paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      }
    }
    
    private static class Proxy
      implements IHdmiVendorCommandListener
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
        return "android.hardware.hdmi.IHdmiVendorCommandListener";
      }
      
      public void onControlStateChanged(boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.hdmi.IHdmiVendorCommandListener");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          localParcel1.writeInt(paramInt);
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
      public void onReceived(int paramInt1, int paramInt2, byte[] paramArrayOfByte, boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore 5
        //   3: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 6
        //   8: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 7
        //   13: aload 6
        //   15: ldc 26
        //   17: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload 6
        //   22: iload_1
        //   23: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   26: aload 6
        //   28: iload_2
        //   29: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   32: aload 6
        //   34: aload_3
        //   35: invokevirtual 63	android/os/Parcel:writeByteArray	([B)V
        //   38: iload 4
        //   40: ifeq +44 -> 84
        //   43: iload 5
        //   45: istore_1
        //   46: aload 6
        //   48: iload_1
        //   49: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   52: aload_0
        //   53: getfield 19	android/hardware/hdmi/IHdmiVendorCommandListener$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   56: iconst_1
        //   57: aload 6
        //   59: aload 7
        //   61: iconst_0
        //   62: invokeinterface 50 5 0
        //   67: pop
        //   68: aload 7
        //   70: invokevirtual 53	android/os/Parcel:readException	()V
        //   73: aload 7
        //   75: invokevirtual 56	android/os/Parcel:recycle	()V
        //   78: aload 6
        //   80: invokevirtual 56	android/os/Parcel:recycle	()V
        //   83: return
        //   84: iconst_0
        //   85: istore_1
        //   86: goto -40 -> 46
        //   89: astore_3
        //   90: aload 7
        //   92: invokevirtual 56	android/os/Parcel:recycle	()V
        //   95: aload 6
        //   97: invokevirtual 56	android/os/Parcel:recycle	()V
        //   100: aload_3
        //   101: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	102	0	this	Proxy
        //   0	102	1	paramInt1	int
        //   0	102	2	paramInt2	int
        //   0	102	3	paramArrayOfByte	byte[]
        //   0	102	4	paramBoolean	boolean
        //   1	43	5	i	int
        //   6	90	6	localParcel1	Parcel
        //   11	80	7	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	38	89	finally
        //   46	73	89	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/hdmi/IHdmiVendorCommandListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */