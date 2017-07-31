package android.net;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface INetworkStatsSession
  extends IInterface
{
  public abstract void close()
    throws RemoteException;
  
  public abstract NetworkStats getDeviceSummaryForNetwork(NetworkTemplate paramNetworkTemplate, long paramLong1, long paramLong2)
    throws RemoteException;
  
  public abstract NetworkStatsHistory getHistoryForNetwork(NetworkTemplate paramNetworkTemplate, int paramInt)
    throws RemoteException;
  
  public abstract NetworkStatsHistory getHistoryForUid(NetworkTemplate paramNetworkTemplate, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws RemoteException;
  
  public abstract NetworkStatsHistory getHistoryIntervalForUid(NetworkTemplate paramNetworkTemplate, int paramInt1, int paramInt2, int paramInt3, int paramInt4, long paramLong1, long paramLong2)
    throws RemoteException;
  
  public abstract int[] getRelevantUids()
    throws RemoteException;
  
  public abstract NetworkStats getSummaryForAllUid(NetworkTemplate paramNetworkTemplate, long paramLong1, long paramLong2, boolean paramBoolean)
    throws RemoteException;
  
  public abstract NetworkStats getSummaryForNetwork(NetworkTemplate paramNetworkTemplate, long paramLong1, long paramLong2)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements INetworkStatsSession
  {
    private static final String DESCRIPTOR = "android.net.INetworkStatsSession";
    static final int TRANSACTION_close = 8;
    static final int TRANSACTION_getDeviceSummaryForNetwork = 1;
    static final int TRANSACTION_getHistoryForNetwork = 3;
    static final int TRANSACTION_getHistoryForUid = 5;
    static final int TRANSACTION_getHistoryIntervalForUid = 6;
    static final int TRANSACTION_getRelevantUids = 7;
    static final int TRANSACTION_getSummaryForAllUid = 4;
    static final int TRANSACTION_getSummaryForNetwork = 2;
    
    public Stub()
    {
      attachInterface(this, "android.net.INetworkStatsSession");
    }
    
    public static INetworkStatsSession asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.net.INetworkStatsSession");
      if ((localIInterface != null) && ((localIInterface instanceof INetworkStatsSession))) {
        return (INetworkStatsSession)localIInterface;
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
      NetworkTemplate localNetworkTemplate;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.net.INetworkStatsSession");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.net.INetworkStatsSession");
        if (paramParcel1.readInt() != 0)
        {
          localNetworkTemplate = (NetworkTemplate)NetworkTemplate.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getDeviceSummaryForNetwork(localNetworkTemplate, paramParcel1.readLong(), paramParcel1.readLong());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label171;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localNetworkTemplate = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 2: 
        paramParcel1.enforceInterface("android.net.INetworkStatsSession");
        if (paramParcel1.readInt() != 0)
        {
          localNetworkTemplate = (NetworkTemplate)NetworkTemplate.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getSummaryForNetwork(localNetworkTemplate, paramParcel1.readLong(), paramParcel1.readLong());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label248;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localNetworkTemplate = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 3: 
        paramParcel1.enforceInterface("android.net.INetworkStatsSession");
        if (paramParcel1.readInt() != 0)
        {
          localNetworkTemplate = (NetworkTemplate)NetworkTemplate.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getHistoryForNetwork(localNetworkTemplate, paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label321;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localNetworkTemplate = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 4: 
        paramParcel1.enforceInterface("android.net.INetworkStatsSession");
        boolean bool;
        if (paramParcel1.readInt() != 0)
        {
          localNetworkTemplate = (NetworkTemplate)NetworkTemplate.CREATOR.createFromParcel(paramParcel1);
          long l1 = paramParcel1.readLong();
          long l2 = paramParcel1.readLong();
          if (paramParcel1.readInt() == 0) {
            break label418;
          }
          bool = true;
          paramParcel1 = getSummaryForAllUid(localNetworkTemplate, l1, l2, bool);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label424;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localNetworkTemplate = null;
          break;
          bool = false;
          break label378;
          paramParcel2.writeInt(0);
        }
      case 5: 
        paramParcel1.enforceInterface("android.net.INetworkStatsSession");
        if (paramParcel1.readInt() != 0)
        {
          localNetworkTemplate = (NetworkTemplate)NetworkTemplate.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getHistoryForUid(localNetworkTemplate, paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label509;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localNetworkTemplate = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 6: 
        paramParcel1.enforceInterface("android.net.INetworkStatsSession");
        if (paramParcel1.readInt() != 0)
        {
          localNetworkTemplate = (NetworkTemplate)NetworkTemplate.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getHistoryIntervalForUid(localNetworkTemplate, paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readLong(), paramParcel1.readLong());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label602;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localNetworkTemplate = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 7: 
        label171:
        label248:
        label321:
        label378:
        label418:
        label424:
        label509:
        label602:
        paramParcel1.enforceInterface("android.net.INetworkStatsSession");
        paramParcel1 = getRelevantUids();
        paramParcel2.writeNoException();
        paramParcel2.writeIntArray(paramParcel1);
        return true;
      }
      paramParcel1.enforceInterface("android.net.INetworkStatsSession");
      close();
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements INetworkStatsSession
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
      
      public void close()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkStatsSession");
          this.mRemote.transact(8, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public NetworkStats getDeviceSummaryForNetwork(NetworkTemplate paramNetworkTemplate, long paramLong1, long paramLong2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.net.INetworkStatsSession");
            if (paramNetworkTemplate != null)
            {
              localParcel1.writeInt(1);
              paramNetworkTemplate.writeToParcel(localParcel1, 0);
              localParcel1.writeLong(paramLong1);
              localParcel1.writeLong(paramLong2);
              this.mRemote.transact(1, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramNetworkTemplate = (NetworkStats)NetworkStats.CREATOR.createFromParcel(localParcel2);
                return paramNetworkTemplate;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramNetworkTemplate = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public NetworkStatsHistory getHistoryForNetwork(NetworkTemplate paramNetworkTemplate, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.net.INetworkStatsSession");
            if (paramNetworkTemplate != null)
            {
              localParcel1.writeInt(1);
              paramNetworkTemplate.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(3, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramNetworkTemplate = (NetworkStatsHistory)NetworkStatsHistory.CREATOR.createFromParcel(localParcel2);
                return paramNetworkTemplate;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramNetworkTemplate = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public NetworkStatsHistory getHistoryForUid(NetworkTemplate paramNetworkTemplate, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.net.INetworkStatsSession");
            if (paramNetworkTemplate != null)
            {
              localParcel1.writeInt(1);
              paramNetworkTemplate.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt1);
              localParcel1.writeInt(paramInt2);
              localParcel1.writeInt(paramInt3);
              localParcel1.writeInt(paramInt4);
              this.mRemote.transact(5, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramNetworkTemplate = (NetworkStatsHistory)NetworkStatsHistory.CREATOR.createFromParcel(localParcel2);
                return paramNetworkTemplate;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramNetworkTemplate = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public NetworkStatsHistory getHistoryIntervalForUid(NetworkTemplate paramNetworkTemplate, int paramInt1, int paramInt2, int paramInt3, int paramInt4, long paramLong1, long paramLong2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.net.INetworkStatsSession");
            if (paramNetworkTemplate != null)
            {
              localParcel1.writeInt(1);
              paramNetworkTemplate.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt1);
              localParcel1.writeInt(paramInt2);
              localParcel1.writeInt(paramInt3);
              localParcel1.writeInt(paramInt4);
              localParcel1.writeLong(paramLong1);
              localParcel1.writeLong(paramLong2);
              this.mRemote.transact(6, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramNetworkTemplate = (NetworkStatsHistory)NetworkStatsHistory.CREATOR.createFromParcel(localParcel2);
                return paramNetworkTemplate;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramNetworkTemplate = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.net.INetworkStatsSession";
      }
      
      public int[] getRelevantUids()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkStatsSession");
          this.mRemote.transact(7, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int[] arrayOfInt = localParcel2.createIntArray();
          return arrayOfInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public NetworkStats getSummaryForAllUid(NetworkTemplate paramNetworkTemplate, long paramLong1, long paramLong2, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.net.INetworkStatsSession");
            if (paramNetworkTemplate != null)
            {
              localParcel1.writeInt(1);
              paramNetworkTemplate.writeToParcel(localParcel1, 0);
              localParcel1.writeLong(paramLong1);
              localParcel1.writeLong(paramLong2);
              if (paramBoolean)
              {
                localParcel1.writeInt(i);
                this.mRemote.transact(4, localParcel1, localParcel2, 0);
                localParcel2.readException();
                if (localParcel2.readInt() == 0) {
                  break label145;
                }
                paramNetworkTemplate = (NetworkStats)NetworkStats.CREATOR.createFromParcel(localParcel2);
                return paramNetworkTemplate;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            i = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          continue;
          label145:
          paramNetworkTemplate = null;
        }
      }
      
      public NetworkStats getSummaryForNetwork(NetworkTemplate paramNetworkTemplate, long paramLong1, long paramLong2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.net.INetworkStatsSession");
            if (paramNetworkTemplate != null)
            {
              localParcel1.writeInt(1);
              paramNetworkTemplate.writeToParcel(localParcel1, 0);
              localParcel1.writeLong(paramLong1);
              localParcel1.writeLong(paramLong2);
              this.mRemote.transact(2, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramNetworkTemplate = (NetworkStats)NetworkStats.CREATOR.createFromParcel(localParcel2);
                return paramNetworkTemplate;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramNetworkTemplate = null;
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
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/INetworkStatsSession.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */