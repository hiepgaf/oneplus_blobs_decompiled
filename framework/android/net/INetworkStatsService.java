package android.net;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Messenger;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface INetworkStatsService
  extends IInterface
{
  public abstract void advisePersistThreshold(long paramLong)
    throws RemoteException;
  
  public abstract void forceUpdate()
    throws RemoteException;
  
  public abstract void forceUpdateIfaces()
    throws RemoteException;
  
  public abstract NetworkStats getDataLayerSnapshotForUid(int paramInt)
    throws RemoteException;
  
  public abstract String[] getMobileIfaces()
    throws RemoteException;
  
  public abstract long getNetworkTotalBytes(NetworkTemplate paramNetworkTemplate, long paramLong1, long paramLong2)
    throws RemoteException;
  
  public abstract void incrementOperationCount(int paramInt1, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public abstract INetworkStatsSession openSession()
    throws RemoteException;
  
  public abstract INetworkStatsSession openSessionForUsageStats(String paramString)
    throws RemoteException;
  
  public abstract NetworkStats peekTetherStats()
    throws RemoteException;
  
  public abstract void recordVideoCallData(String paramString, int paramInt, long paramLong1, long paramLong2)
    throws RemoteException;
  
  public abstract DataUsageRequest registerUsageCallback(String paramString, DataUsageRequest paramDataUsageRequest, Messenger paramMessenger, IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void setUidForeground(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void unregisterUsageRequest(DataUsageRequest paramDataUsageRequest)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements INetworkStatsService
  {
    private static final String DESCRIPTOR = "android.net.INetworkStatsService";
    static final int TRANSACTION_advisePersistThreshold = 10;
    static final int TRANSACTION_forceUpdate = 9;
    static final int TRANSACTION_forceUpdateIfaces = 8;
    static final int TRANSACTION_getDataLayerSnapshotForUid = 4;
    static final int TRANSACTION_getMobileIfaces = 5;
    static final int TRANSACTION_getNetworkTotalBytes = 3;
    static final int TRANSACTION_incrementOperationCount = 6;
    static final int TRANSACTION_openSession = 1;
    static final int TRANSACTION_openSessionForUsageStats = 2;
    static final int TRANSACTION_peekTetherStats = 14;
    static final int TRANSACTION_recordVideoCallData = 13;
    static final int TRANSACTION_registerUsageCallback = 11;
    static final int TRANSACTION_setUidForeground = 7;
    static final int TRANSACTION_unregisterUsageRequest = 12;
    
    public Stub()
    {
      attachInterface(this, "android.net.INetworkStatsService");
    }
    
    public static INetworkStatsService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.net.INetworkStatsService");
      if ((localIInterface != null) && ((localIInterface instanceof INetworkStatsService))) {
        return (INetworkStatsService)localIInterface;
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
      Object localObject;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.net.INetworkStatsService");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.net.INetworkStatsService");
        paramParcel1 = openSession();
        paramParcel2.writeNoException();
        if (paramParcel1 != null) {}
        for (paramParcel1 = paramParcel1.asBinder();; paramParcel1 = null)
        {
          paramParcel2.writeStrongBinder(paramParcel1);
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.net.INetworkStatsService");
        paramParcel1 = openSessionForUsageStats(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (paramParcel1 != null) {}
        for (paramParcel1 = paramParcel1.asBinder();; paramParcel1 = null)
        {
          paramParcel2.writeStrongBinder(paramParcel1);
          return true;
        }
      case 3: 
        paramParcel1.enforceInterface("android.net.INetworkStatsService");
        if (paramParcel1.readInt() != 0) {}
        for (localObject = (NetworkTemplate)NetworkTemplate.CREATOR.createFromParcel(paramParcel1);; localObject = null)
        {
          long l = getNetworkTotalBytes((NetworkTemplate)localObject, paramParcel1.readLong(), paramParcel1.readLong());
          paramParcel2.writeNoException();
          paramParcel2.writeLong(l);
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.net.INetworkStatsService");
        paramParcel1 = getDataLayerSnapshotForUid(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 5: 
        paramParcel1.enforceInterface("android.net.INetworkStatsService");
        paramParcel1 = getMobileIfaces();
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.net.INetworkStatsService");
        incrementOperationCount(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 7: 
        paramParcel1.enforceInterface("android.net.INetworkStatsService");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (boolean bool = true;; bool = false)
        {
          setUidForeground(paramInt1, bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 8: 
        paramParcel1.enforceInterface("android.net.INetworkStatsService");
        forceUpdateIfaces();
        paramParcel2.writeNoException();
        return true;
      case 9: 
        paramParcel1.enforceInterface("android.net.INetworkStatsService");
        forceUpdate();
        paramParcel2.writeNoException();
        return true;
      case 10: 
        paramParcel1.enforceInterface("android.net.INetworkStatsService");
        advisePersistThreshold(paramParcel1.readLong());
        paramParcel2.writeNoException();
        return true;
      case 11: 
        paramParcel1.enforceInterface("android.net.INetworkStatsService");
        String str = paramParcel1.readString();
        Messenger localMessenger;
        if (paramParcel1.readInt() != 0)
        {
          localObject = (DataUsageRequest)DataUsageRequest.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label573;
          }
          localMessenger = (Messenger)Messenger.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = registerUsageCallback(str, (DataUsageRequest)localObject, localMessenger, paramParcel1.readStrongBinder());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label579;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject = null;
          break;
          localMessenger = null;
          break label531;
          paramParcel2.writeInt(0);
        }
      case 12: 
        paramParcel1.enforceInterface("android.net.INetworkStatsService");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (DataUsageRequest)DataUsageRequest.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          unregisterUsageRequest(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 13: 
        label531:
        label573:
        label579:
        paramParcel1.enforceInterface("android.net.INetworkStatsService");
        recordVideoCallData(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readLong(), paramParcel1.readLong());
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.net.INetworkStatsService");
      paramParcel1 = peekTetherStats();
      paramParcel2.writeNoException();
      if (paramParcel1 != null)
      {
        paramParcel2.writeInt(1);
        paramParcel1.writeToParcel(paramParcel2, 1);
      }
      for (;;)
      {
        return true;
        paramParcel2.writeInt(0);
      }
    }
    
    private static class Proxy
      implements INetworkStatsService
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public void advisePersistThreshold(long paramLong)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkStatsService");
          localParcel1.writeLong(paramLong);
          this.mRemote.transact(10, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public void forceUpdate()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkStatsService");
          this.mRemote.transact(9, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void forceUpdateIfaces()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkStatsService");
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
      
      /* Error */
      public NetworkStats getDataLayerSnapshotForUid(int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 62	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/net/INetworkStatsService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: iconst_4
        //   25: aload_3
        //   26: aload 4
        //   28: iconst_0
        //   29: invokeinterface 45 5 0
        //   34: pop
        //   35: aload 4
        //   37: invokevirtual 48	android/os/Parcel:readException	()V
        //   40: aload 4
        //   42: invokevirtual 66	android/os/Parcel:readInt	()I
        //   45: ifeq +28 -> 73
        //   48: getstatic 72	android/net/NetworkStats:CREATOR	Landroid/os/Parcelable$Creator;
        //   51: aload 4
        //   53: invokeinterface 78 2 0
        //   58: checkcast 68	android/net/NetworkStats
        //   61: astore_2
        //   62: aload 4
        //   64: invokevirtual 51	android/os/Parcel:recycle	()V
        //   67: aload_3
        //   68: invokevirtual 51	android/os/Parcel:recycle	()V
        //   71: aload_2
        //   72: areturn
        //   73: aconst_null
        //   74: astore_2
        //   75: goto -13 -> 62
        //   78: astore_2
        //   79: aload 4
        //   81: invokevirtual 51	android/os/Parcel:recycle	()V
        //   84: aload_3
        //   85: invokevirtual 51	android/os/Parcel:recycle	()V
        //   88: aload_2
        //   89: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	90	0	this	Proxy
        //   0	90	1	paramInt	int
        //   61	14	2	localNetworkStats	NetworkStats
        //   78	11	2	localObject	Object
        //   3	82	3	localParcel1	Parcel
        //   7	73	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	62	78	finally
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.net.INetworkStatsService";
      }
      
      public String[] getMobileIfaces()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkStatsService");
          this.mRemote.transact(5, localParcel1, localParcel2, 0);
          localParcel2.readException();
          String[] arrayOfString = localParcel2.createStringArray();
          return arrayOfString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public long getNetworkTotalBytes(NetworkTemplate paramNetworkTemplate, long paramLong1, long paramLong2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 6
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 7
        //   10: aload 6
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload_1
        //   18: ifnull +68 -> 86
        //   21: aload 6
        //   23: iconst_1
        //   24: invokevirtual 62	android/os/Parcel:writeInt	(I)V
        //   27: aload_1
        //   28: aload 6
        //   30: iconst_0
        //   31: invokevirtual 93	android/net/NetworkTemplate:writeToParcel	(Landroid/os/Parcel;I)V
        //   34: aload 6
        //   36: lload_2
        //   37: invokevirtual 39	android/os/Parcel:writeLong	(J)V
        //   40: aload 6
        //   42: lload 4
        //   44: invokevirtual 39	android/os/Parcel:writeLong	(J)V
        //   47: aload_0
        //   48: getfield 19	android/net/INetworkStatsService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   51: iconst_3
        //   52: aload 6
        //   54: aload 7
        //   56: iconst_0
        //   57: invokeinterface 45 5 0
        //   62: pop
        //   63: aload 7
        //   65: invokevirtual 48	android/os/Parcel:readException	()V
        //   68: aload 7
        //   70: invokevirtual 97	android/os/Parcel:readLong	()J
        //   73: lstore_2
        //   74: aload 7
        //   76: invokevirtual 51	android/os/Parcel:recycle	()V
        //   79: aload 6
        //   81: invokevirtual 51	android/os/Parcel:recycle	()V
        //   84: lload_2
        //   85: lreturn
        //   86: aload 6
        //   88: iconst_0
        //   89: invokevirtual 62	android/os/Parcel:writeInt	(I)V
        //   92: goto -58 -> 34
        //   95: astore_1
        //   96: aload 7
        //   98: invokevirtual 51	android/os/Parcel:recycle	()V
        //   101: aload 6
        //   103: invokevirtual 51	android/os/Parcel:recycle	()V
        //   106: aload_1
        //   107: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	108	0	this	Proxy
        //   0	108	1	paramNetworkTemplate	NetworkTemplate
        //   0	108	2	paramLong1	long
        //   0	108	4	paramLong2	long
        //   3	99	6	localParcel1	Parcel
        //   8	89	7	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	17	95	finally
        //   21	34	95	finally
        //   34	74	95	finally
        //   86	92	95	finally
      }
      
      public void incrementOperationCount(int paramInt1, int paramInt2, int paramInt3)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkStatsService");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeInt(paramInt3);
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
      
      public INetworkStatsSession openSession()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkStatsService");
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
          localParcel2.readException();
          INetworkStatsSession localINetworkStatsSession = INetworkStatsSession.Stub.asInterface(localParcel2.readStrongBinder());
          return localINetworkStatsSession;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public INetworkStatsSession openSessionForUsageStats(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkStatsService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(2, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString = INetworkStatsSession.Stub.asInterface(localParcel2.readStrongBinder());
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public NetworkStats peekTetherStats()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 32
        //   11: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_0
        //   15: getfield 19	android/net/INetworkStatsService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: bipush 14
        //   20: aload_2
        //   21: aload_3
        //   22: iconst_0
        //   23: invokeinterface 45 5 0
        //   28: pop
        //   29: aload_3
        //   30: invokevirtual 48	android/os/Parcel:readException	()V
        //   33: aload_3
        //   34: invokevirtual 66	android/os/Parcel:readInt	()I
        //   37: ifeq +26 -> 63
        //   40: getstatic 72	android/net/NetworkStats:CREATOR	Landroid/os/Parcelable$Creator;
        //   43: aload_3
        //   44: invokeinterface 78 2 0
        //   49: checkcast 68	android/net/NetworkStats
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 51	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 51	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aconst_null
        //   64: astore_1
        //   65: goto -12 -> 53
        //   68: astore_1
        //   69: aload_3
        //   70: invokevirtual 51	android/os/Parcel:recycle	()V
        //   73: aload_2
        //   74: invokevirtual 51	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   52	13	1	localNetworkStats	NetworkStats
        //   68	10	1	localObject	Object
        //   3	71	2	localParcel1	Parcel
        //   7	63	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	53	68	finally
      }
      
      public void recordVideoCallData(String paramString, int paramInt, long paramLong1, long paramLong2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkStatsService");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          localParcel1.writeLong(paramLong1);
          localParcel1.writeLong(paramLong2);
          this.mRemote.transact(13, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public DataUsageRequest registerUsageCallback(String paramString, DataUsageRequest paramDataUsageRequest, Messenger paramMessenger, IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.net.INetworkStatsService");
            localParcel1.writeString(paramString);
            if (paramDataUsageRequest != null)
            {
              localParcel1.writeInt(1);
              paramDataUsageRequest.writeToParcel(localParcel1, 0);
              if (paramMessenger != null)
              {
                localParcel1.writeInt(1);
                paramMessenger.writeToParcel(localParcel1, 0);
                localParcel1.writeStrongBinder(paramIBinder);
                this.mRemote.transact(11, localParcel1, localParcel2, 0);
                localParcel2.readException();
                if (localParcel2.readInt() == 0) {
                  break label151;
                }
                paramString = (DataUsageRequest)DataUsageRequest.CREATOR.createFromParcel(localParcel2);
                return paramString;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            localParcel1.writeInt(0);
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          continue;
          label151:
          paramString = null;
        }
      }
      
      public void setUidForeground(int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkStatsService");
          localParcel1.writeInt(paramInt);
          paramInt = i;
          if (paramBoolean) {
            paramInt = 1;
          }
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(7, localParcel1, localParcel2, 0);
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
      public void unregisterUsageRequest(DataUsageRequest paramDataUsageRequest)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 32
        //   11: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +42 -> 57
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 62	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 124	android/net/DataUsageRequest:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/net/INetworkStatsService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 12
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 45 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 48	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 51	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 51	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 62	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 51	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 51	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	Proxy
        //   0	76	1	paramDataUsageRequest	DataUsageRequest
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/INetworkStatsService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */