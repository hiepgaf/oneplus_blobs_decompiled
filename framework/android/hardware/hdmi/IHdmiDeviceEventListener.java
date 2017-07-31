package android.hardware.hdmi;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IHdmiDeviceEventListener
  extends IInterface
{
  public abstract void onStatusChanged(HdmiDeviceInfo paramHdmiDeviceInfo, int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IHdmiDeviceEventListener
  {
    private static final String DESCRIPTOR = "android.hardware.hdmi.IHdmiDeviceEventListener";
    static final int TRANSACTION_onStatusChanged = 1;
    
    public Stub()
    {
      attachInterface(this, "android.hardware.hdmi.IHdmiDeviceEventListener");
    }
    
    public static IHdmiDeviceEventListener asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.hardware.hdmi.IHdmiDeviceEventListener");
      if ((localIInterface != null) && ((localIInterface instanceof IHdmiDeviceEventListener))) {
        return (IHdmiDeviceEventListener)localIInterface;
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
        paramParcel2.writeString("android.hardware.hdmi.IHdmiDeviceEventListener");
        return true;
      }
      paramParcel1.enforceInterface("android.hardware.hdmi.IHdmiDeviceEventListener");
      if (paramParcel1.readInt() != 0) {}
      for (paramParcel2 = (HdmiDeviceInfo)HdmiDeviceInfo.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
      {
        onStatusChanged(paramParcel2, paramParcel1.readInt());
        return true;
      }
    }
    
    private static class Proxy
      implements IHdmiDeviceEventListener
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
        return "android.hardware.hdmi.IHdmiDeviceEventListener";
      }
      
      /* Error */
      public void onStatusChanged(HdmiDeviceInfo paramHdmiDeviceInfo, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: aload_3
        //   5: ldc 26
        //   7: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +38 -> 49
        //   14: aload_3
        //   15: iconst_1
        //   16: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_3
        //   21: iconst_0
        //   22: invokevirtual 50	android/hardware/hdmi/HdmiDeviceInfo:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_3
        //   26: iload_2
        //   27: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   30: aload_0
        //   31: getfield 19	android/hardware/hdmi/IHdmiDeviceEventListener$Stub$Proxy:mRemote	Landroid/os/IBinder;
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
        //   54: goto -29 -> 25
        //   57: astore_1
        //   58: aload_3
        //   59: invokevirtual 59	android/os/Parcel:recycle	()V
        //   62: aload_1
        //   63: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	64	0	this	Proxy
        //   0	64	1	paramHdmiDeviceInfo	HdmiDeviceInfo
        //   0	64	2	paramInt	int
        //   3	56	3	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	57	finally
        //   14	25	57	finally
        //   25	44	57	finally
        //   49	54	57	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/hdmi/IHdmiDeviceEventListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */