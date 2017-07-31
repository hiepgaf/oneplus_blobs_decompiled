package android.net;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;

public abstract interface INetworkScoreCache
  extends IInterface
{
  public abstract void clearScores()
    throws RemoteException;
  
  public abstract void updateScores(List<ScoredNetwork> paramList)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements INetworkScoreCache
  {
    private static final String DESCRIPTOR = "android.net.INetworkScoreCache";
    static final int TRANSACTION_clearScores = 2;
    static final int TRANSACTION_updateScores = 1;
    
    public Stub()
    {
      attachInterface(this, "android.net.INetworkScoreCache");
    }
    
    public static INetworkScoreCache asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.net.INetworkScoreCache");
      if ((localIInterface != null) && ((localIInterface instanceof INetworkScoreCache))) {
        return (INetworkScoreCache)localIInterface;
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
        paramParcel2.writeString("android.net.INetworkScoreCache");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.net.INetworkScoreCache");
        updateScores(paramParcel1.createTypedArrayList(ScoredNetwork.CREATOR));
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.net.INetworkScoreCache");
      clearScores();
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements INetworkScoreCache
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
      
      public void clearScores()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkScoreCache");
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
      
      public String getInterfaceDescriptor()
      {
        return "android.net.INetworkScoreCache";
      }
      
      public void updateScores(List<ScoredNetwork> paramList)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkScoreCache");
          localParcel1.writeTypedList(paramList);
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/INetworkScoreCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */