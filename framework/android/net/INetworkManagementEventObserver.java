package android.net;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface INetworkManagementEventObserver
  extends IInterface
{
  public abstract void addressRemoved(String paramString, LinkAddress paramLinkAddress)
    throws RemoteException;
  
  public abstract void addressUpdated(String paramString, LinkAddress paramLinkAddress)
    throws RemoteException;
  
  public abstract void interfaceAdded(String paramString)
    throws RemoteException;
  
  public abstract void interfaceClassDataActivityChanged(String paramString, boolean paramBoolean, long paramLong)
    throws RemoteException;
  
  public abstract void interfaceDnsServerInfo(String paramString, long paramLong, String[] paramArrayOfString)
    throws RemoteException;
  
  public abstract void interfaceLinkStateChanged(String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void interfaceMessageRecevied(String paramString)
    throws RemoteException;
  
  public abstract void interfaceRemoved(String paramString)
    throws RemoteException;
  
  public abstract void interfaceStatusChanged(String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void limitReached(String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract void routeRemoved(RouteInfo paramRouteInfo)
    throws RemoteException;
  
  public abstract void routeUpdated(RouteInfo paramRouteInfo)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements INetworkManagementEventObserver
  {
    private static final String DESCRIPTOR = "android.net.INetworkManagementEventObserver";
    static final int TRANSACTION_addressRemoved = 6;
    static final int TRANSACTION_addressUpdated = 5;
    static final int TRANSACTION_interfaceAdded = 3;
    static final int TRANSACTION_interfaceClassDataActivityChanged = 8;
    static final int TRANSACTION_interfaceDnsServerInfo = 10;
    static final int TRANSACTION_interfaceLinkStateChanged = 2;
    static final int TRANSACTION_interfaceMessageRecevied = 9;
    static final int TRANSACTION_interfaceRemoved = 4;
    static final int TRANSACTION_interfaceStatusChanged = 1;
    static final int TRANSACTION_limitReached = 7;
    static final int TRANSACTION_routeRemoved = 12;
    static final int TRANSACTION_routeUpdated = 11;
    
    public Stub()
    {
      attachInterface(this, "android.net.INetworkManagementEventObserver");
    }
    
    public static INetworkManagementEventObserver asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.net.INetworkManagementEventObserver");
      if ((localIInterface != null) && ((localIInterface instanceof INetworkManagementEventObserver))) {
        return (INetworkManagementEventObserver)localIInterface;
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
      String str;
      boolean bool;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.net.INetworkManagementEventObserver");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.net.INetworkManagementEventObserver");
        str = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          interfaceStatusChanged(str, bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.net.INetworkManagementEventObserver");
        str = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          interfaceLinkStateChanged(str, bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 3: 
        paramParcel1.enforceInterface("android.net.INetworkManagementEventObserver");
        interfaceAdded(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.net.INetworkManagementEventObserver");
        interfaceRemoved(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.net.INetworkManagementEventObserver");
        str = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (LinkAddress)LinkAddress.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          addressUpdated(str, paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 6: 
        paramParcel1.enforceInterface("android.net.INetworkManagementEventObserver");
        str = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (LinkAddress)LinkAddress.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          addressRemoved(str, paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 7: 
        paramParcel1.enforceInterface("android.net.INetworkManagementEventObserver");
        limitReached(paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 8: 
        paramParcel1.enforceInterface("android.net.INetworkManagementEventObserver");
        str = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          interfaceClassDataActivityChanged(str, bool, paramParcel1.readLong());
          paramParcel2.writeNoException();
          return true;
        }
      case 9: 
        paramParcel1.enforceInterface("android.net.INetworkManagementEventObserver");
        interfaceMessageRecevied(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 10: 
        paramParcel1.enforceInterface("android.net.INetworkManagementEventObserver");
        interfaceDnsServerInfo(paramParcel1.readString(), paramParcel1.readLong(), paramParcel1.createStringArray());
        paramParcel2.writeNoException();
        return true;
      case 11: 
        paramParcel1.enforceInterface("android.net.INetworkManagementEventObserver");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (RouteInfo)RouteInfo.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          routeUpdated(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      }
      paramParcel1.enforceInterface("android.net.INetworkManagementEventObserver");
      if (paramParcel1.readInt() != 0) {}
      for (paramParcel1 = (RouteInfo)RouteInfo.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        routeRemoved(paramParcel1);
        paramParcel2.writeNoException();
        return true;
      }
    }
    
    private static class Proxy
      implements INetworkManagementEventObserver
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      /* Error */
      public void addressRemoved(String paramString, LinkAddress paramLinkAddress)
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
        //   16: aload_1
        //   17: invokevirtual 39	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   20: aload_2
        //   21: ifnull +45 -> 66
        //   24: aload_3
        //   25: iconst_1
        //   26: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   29: aload_2
        //   30: aload_3
        //   31: iconst_0
        //   32: invokevirtual 49	android/net/LinkAddress:writeToParcel	(Landroid/os/Parcel;I)V
        //   35: aload_0
        //   36: getfield 19	android/net/INetworkManagementEventObserver$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 6
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 55 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 58	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 61	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 61	android/os/Parcel:recycle	()V
        //   65: return
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   71: goto -36 -> 35
        //   74: astore_1
        //   75: aload 4
        //   77: invokevirtual 61	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 61	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramString	String
        //   0	86	2	paramLinkAddress	LinkAddress
        //   3	78	3	localParcel1	Parcel
        //   7	69	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	20	74	finally
        //   24	35	74	finally
        //   35	56	74	finally
        //   66	71	74	finally
      }
      
      /* Error */
      public void addressUpdated(String paramString, LinkAddress paramLinkAddress)
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
        //   16: aload_1
        //   17: invokevirtual 39	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   20: aload_2
        //   21: ifnull +44 -> 65
        //   24: aload_3
        //   25: iconst_1
        //   26: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   29: aload_2
        //   30: aload_3
        //   31: iconst_0
        //   32: invokevirtual 49	android/net/LinkAddress:writeToParcel	(Landroid/os/Parcel;I)V
        //   35: aload_0
        //   36: getfield 19	android/net/INetworkManagementEventObserver$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: iconst_5
        //   40: aload_3
        //   41: aload 4
        //   43: iconst_0
        //   44: invokeinterface 55 5 0
        //   49: pop
        //   50: aload 4
        //   52: invokevirtual 58	android/os/Parcel:readException	()V
        //   55: aload 4
        //   57: invokevirtual 61	android/os/Parcel:recycle	()V
        //   60: aload_3
        //   61: invokevirtual 61	android/os/Parcel:recycle	()V
        //   64: return
        //   65: aload_3
        //   66: iconst_0
        //   67: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   70: goto -35 -> 35
        //   73: astore_1
        //   74: aload 4
        //   76: invokevirtual 61	android/os/Parcel:recycle	()V
        //   79: aload_3
        //   80: invokevirtual 61	android/os/Parcel:recycle	()V
        //   83: aload_1
        //   84: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	85	0	this	Proxy
        //   0	85	1	paramString	String
        //   0	85	2	paramLinkAddress	LinkAddress
        //   3	77	3	localParcel1	Parcel
        //   7	68	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	20	73	finally
        //   24	35	73	finally
        //   35	55	73	finally
        //   65	70	73	finally
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.net.INetworkManagementEventObserver";
      }
      
      public void interfaceAdded(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkManagementEventObserver");
          localParcel1.writeString(paramString);
          this.mRemote.transact(3, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void interfaceClassDataActivityChanged(String paramString, boolean paramBoolean, long paramLong)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkManagementEventObserver");
          localParcel1.writeString(paramString);
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          localParcel1.writeLong(paramLong);
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
      
      public void interfaceDnsServerInfo(String paramString, long paramLong, String[] paramArrayOfString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkManagementEventObserver");
          localParcel1.writeString(paramString);
          localParcel1.writeLong(paramLong);
          localParcel1.writeStringArray(paramArrayOfString);
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
      
      public void interfaceLinkStateChanged(String paramString, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkManagementEventObserver");
          localParcel1.writeString(paramString);
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
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
      
      public void interfaceMessageRecevied(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkManagementEventObserver");
          localParcel1.writeString(paramString);
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
      
      public void interfaceRemoved(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkManagementEventObserver");
          localParcel1.writeString(paramString);
          this.mRemote.transact(4, localParcel1, localParcel2, 0);
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
      public void interfaceStatusChanged(String paramString, boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore_3
        //   2: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore 4
        //   7: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   10: astore 5
        //   12: aload 4
        //   14: ldc 32
        //   16: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   19: aload 4
        //   21: aload_1
        //   22: invokevirtual 39	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   25: iload_2
        //   26: ifeq +41 -> 67
        //   29: aload 4
        //   31: iload_3
        //   32: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   35: aload_0
        //   36: getfield 19	android/net/INetworkManagementEventObserver$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: iconst_1
        //   40: aload 4
        //   42: aload 5
        //   44: iconst_0
        //   45: invokeinterface 55 5 0
        //   50: pop
        //   51: aload 5
        //   53: invokevirtual 58	android/os/Parcel:readException	()V
        //   56: aload 5
        //   58: invokevirtual 61	android/os/Parcel:recycle	()V
        //   61: aload 4
        //   63: invokevirtual 61	android/os/Parcel:recycle	()V
        //   66: return
        //   67: iconst_0
        //   68: istore_3
        //   69: goto -40 -> 29
        //   72: astore_1
        //   73: aload 5
        //   75: invokevirtual 61	android/os/Parcel:recycle	()V
        //   78: aload 4
        //   80: invokevirtual 61	android/os/Parcel:recycle	()V
        //   83: aload_1
        //   84: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	85	0	this	Proxy
        //   0	85	1	paramString	String
        //   0	85	2	paramBoolean	boolean
        //   1	68	3	i	int
        //   5	74	4	localParcel1	Parcel
        //   10	64	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   12	25	72	finally
        //   29	56	72	finally
      }
      
      public void limitReached(String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkManagementEventObserver");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
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
      public void routeRemoved(RouteInfo paramRouteInfo)
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
        //   20: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 92	android/net/RouteInfo:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/net/INetworkManagementEventObserver$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 12
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 55 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 58	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 61	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 61	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 61	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 61	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	Proxy
        //   0	76	1	paramRouteInfo	RouteInfo
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
      
      /* Error */
      public void routeUpdated(RouteInfo paramRouteInfo)
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
        //   20: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 92	android/net/RouteInfo:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/net/INetworkManagementEventObserver$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 11
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 55 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 58	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 61	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 61	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 61	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 61	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	Proxy
        //   0	76	1	paramRouteInfo	RouteInfo
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/INetworkManagementEventObserver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */