package android.media.midi;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IMidiDeviceOpenCallback
  extends IInterface
{
  public abstract void onDeviceOpened(IMidiDeviceServer paramIMidiDeviceServer, IBinder paramIBinder)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IMidiDeviceOpenCallback
  {
    private static final String DESCRIPTOR = "android.media.midi.IMidiDeviceOpenCallback";
    static final int TRANSACTION_onDeviceOpened = 1;
    
    public Stub()
    {
      attachInterface(this, "android.media.midi.IMidiDeviceOpenCallback");
    }
    
    public static IMidiDeviceOpenCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.media.midi.IMidiDeviceOpenCallback");
      if ((localIInterface != null) && ((localIInterface instanceof IMidiDeviceOpenCallback))) {
        return (IMidiDeviceOpenCallback)localIInterface;
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
        paramParcel2.writeString("android.media.midi.IMidiDeviceOpenCallback");
        return true;
      }
      paramParcel1.enforceInterface("android.media.midi.IMidiDeviceOpenCallback");
      onDeviceOpened(IMidiDeviceServer.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readStrongBinder());
      return true;
    }
    
    private static class Proxy
      implements IMidiDeviceOpenCallback
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
        return "android.media.midi.IMidiDeviceOpenCallback";
      }
      
      public void onDeviceOpened(IMidiDeviceServer paramIMidiDeviceServer, IBinder paramIBinder)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.midi.IMidiDeviceOpenCallback");
          if (paramIMidiDeviceServer != null) {
            localIBinder = paramIMidiDeviceServer.asBinder();
          }
          localParcel.writeStrongBinder(localIBinder);
          localParcel.writeStrongBinder(paramIBinder);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/midi/IMidiDeviceOpenCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */