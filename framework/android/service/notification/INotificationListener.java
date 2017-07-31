package android.service.notification;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface INotificationListener
  extends IInterface
{
  public abstract void onInterruptionFilterChanged(int paramInt)
    throws RemoteException;
  
  public abstract void onListenerConnected(NotificationRankingUpdate paramNotificationRankingUpdate)
    throws RemoteException;
  
  public abstract void onListenerHintsChanged(int paramInt)
    throws RemoteException;
  
  public abstract void onNotificationActionClick(String paramString, long paramLong, int paramInt)
    throws RemoteException;
  
  public abstract void onNotificationClick(String paramString, long paramLong)
    throws RemoteException;
  
  public abstract void onNotificationEnqueued(IStatusBarNotificationHolder paramIStatusBarNotificationHolder, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void onNotificationPosted(IStatusBarNotificationHolder paramIStatusBarNotificationHolder, NotificationRankingUpdate paramNotificationRankingUpdate)
    throws RemoteException;
  
  public abstract void onNotificationRankingUpdate(NotificationRankingUpdate paramNotificationRankingUpdate)
    throws RemoteException;
  
  public abstract void onNotificationRemoved(IStatusBarNotificationHolder paramIStatusBarNotificationHolder, NotificationRankingUpdate paramNotificationRankingUpdate)
    throws RemoteException;
  
  public abstract void onNotificationRemovedReason(String paramString, long paramLong, int paramInt)
    throws RemoteException;
  
  public abstract void onNotificationVisibilityChanged(String paramString, long paramLong, boolean paramBoolean)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements INotificationListener
  {
    private static final String DESCRIPTOR = "android.service.notification.INotificationListener";
    static final int TRANSACTION_onInterruptionFilterChanged = 6;
    static final int TRANSACTION_onListenerConnected = 1;
    static final int TRANSACTION_onListenerHintsChanged = 5;
    static final int TRANSACTION_onNotificationActionClick = 10;
    static final int TRANSACTION_onNotificationClick = 9;
    static final int TRANSACTION_onNotificationEnqueued = 7;
    static final int TRANSACTION_onNotificationPosted = 2;
    static final int TRANSACTION_onNotificationRankingUpdate = 4;
    static final int TRANSACTION_onNotificationRemoved = 3;
    static final int TRANSACTION_onNotificationRemovedReason = 11;
    static final int TRANSACTION_onNotificationVisibilityChanged = 8;
    
    public Stub()
    {
      attachInterface(this, "android.service.notification.INotificationListener");
    }
    
    public static INotificationListener asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.service.notification.INotificationListener");
      if ((localIInterface != null) && ((localIInterface instanceof INotificationListener))) {
        return (INotificationListener)localIInterface;
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
      boolean bool;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.service.notification.INotificationListener");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.service.notification.INotificationListener");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (NotificationRankingUpdate)NotificationRankingUpdate.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onListenerConnected(paramParcel1);
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.service.notification.INotificationListener");
        paramParcel2 = IStatusBarNotificationHolder.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (NotificationRankingUpdate)NotificationRankingUpdate.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onNotificationPosted(paramParcel2, paramParcel1);
          return true;
        }
      case 3: 
        paramParcel1.enforceInterface("android.service.notification.INotificationListener");
        paramParcel2 = IStatusBarNotificationHolder.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (NotificationRankingUpdate)NotificationRankingUpdate.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onNotificationRemoved(paramParcel2, paramParcel1);
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.service.notification.INotificationListener");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (NotificationRankingUpdate)NotificationRankingUpdate.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onNotificationRankingUpdate(paramParcel1);
          return true;
        }
      case 5: 
        paramParcel1.enforceInterface("android.service.notification.INotificationListener");
        onListenerHintsChanged(paramParcel1.readInt());
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.service.notification.INotificationListener");
        onInterruptionFilterChanged(paramParcel1.readInt());
        return true;
      case 7: 
        paramParcel1.enforceInterface("android.service.notification.INotificationListener");
        paramParcel2 = IStatusBarNotificationHolder.Stub.asInterface(paramParcel1.readStrongBinder());
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          onNotificationEnqueued(paramParcel2, paramInt1, bool);
          return true;
        }
      case 8: 
        paramParcel1.enforceInterface("android.service.notification.INotificationListener");
        paramParcel2 = paramParcel1.readString();
        long l = paramParcel1.readLong();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          onNotificationVisibilityChanged(paramParcel2, l, bool);
          return true;
        }
      case 9: 
        paramParcel1.enforceInterface("android.service.notification.INotificationListener");
        onNotificationClick(paramParcel1.readString(), paramParcel1.readLong());
        return true;
      case 10: 
        paramParcel1.enforceInterface("android.service.notification.INotificationListener");
        onNotificationActionClick(paramParcel1.readString(), paramParcel1.readLong(), paramParcel1.readInt());
        return true;
      }
      paramParcel1.enforceInterface("android.service.notification.INotificationListener");
      onNotificationRemovedReason(paramParcel1.readString(), paramParcel1.readLong(), paramParcel1.readInt());
      return true;
    }
    
    private static class Proxy
      implements INotificationListener
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
        return "android.service.notification.INotificationListener";
      }
      
      public void onInterruptionFilterChanged(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.service.notification.INotificationListener");
          localParcel.writeInt(paramInt);
          this.mRemote.transact(6, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void onListenerConnected(NotificationRankingUpdate paramNotificationRankingUpdate)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 26
        //   7: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +33 -> 44
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 61	android/service/notification/NotificationRankingUpdate:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/service/notification/INotificationListener$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: iconst_1
        //   30: aload_2
        //   31: aconst_null
        //   32: iconst_1
        //   33: invokeinterface 49 5 0
        //   38: pop
        //   39: aload_2
        //   40: invokevirtual 52	android/os/Parcel:recycle	()V
        //   43: return
        //   44: aload_2
        //   45: iconst_0
        //   46: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   49: goto -24 -> 25
        //   52: astore_1
        //   53: aload_2
        //   54: invokevirtual 52	android/os/Parcel:recycle	()V
        //   57: aload_1
        //   58: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	59	0	this	Proxy
        //   0	59	1	paramNotificationRankingUpdate	NotificationRankingUpdate
        //   3	51	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	52	finally
        //   14	25	52	finally
        //   25	39	52	finally
        //   44	49	52	finally
      }
      
      public void onListenerHintsChanged(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.service.notification.INotificationListener");
          localParcel.writeInt(paramInt);
          this.mRemote.transact(5, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onNotificationActionClick(String paramString, long paramLong, int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.service.notification.INotificationListener");
          localParcel.writeString(paramString);
          localParcel.writeLong(paramLong);
          localParcel.writeInt(paramInt);
          this.mRemote.transact(10, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onNotificationClick(String paramString, long paramLong)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.service.notification.INotificationListener");
          localParcel.writeString(paramString);
          localParcel.writeLong(paramLong);
          this.mRemote.transact(9, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void onNotificationEnqueued(IStatusBarNotificationHolder paramIStatusBarNotificationHolder, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore 4
        //   3: aconst_null
        //   4: astore 5
        //   6: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   9: astore 6
        //   11: aload 6
        //   13: ldc 26
        //   15: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   18: aload_1
        //   19: ifnull +11 -> 30
        //   22: aload_1
        //   23: invokeinterface 79 1 0
        //   28: astore 5
        //   30: aload 6
        //   32: aload 5
        //   34: invokevirtual 82	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   37: aload 6
        //   39: iload_2
        //   40: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   43: iload_3
        //   44: ifeq +34 -> 78
        //   47: iload 4
        //   49: istore_2
        //   50: aload 6
        //   52: iload_2
        //   53: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   56: aload_0
        //   57: getfield 19	android/service/notification/INotificationListener$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   60: bipush 7
        //   62: aload 6
        //   64: aconst_null
        //   65: iconst_1
        //   66: invokeinterface 49 5 0
        //   71: pop
        //   72: aload 6
        //   74: invokevirtual 52	android/os/Parcel:recycle	()V
        //   77: return
        //   78: iconst_0
        //   79: istore_2
        //   80: goto -30 -> 50
        //   83: astore_1
        //   84: aload 6
        //   86: invokevirtual 52	android/os/Parcel:recycle	()V
        //   89: aload_1
        //   90: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	91	0	this	Proxy
        //   0	91	1	paramIStatusBarNotificationHolder	IStatusBarNotificationHolder
        //   0	91	2	paramInt	int
        //   0	91	3	paramBoolean	boolean
        //   1	47	4	i	int
        //   4	29	5	localIBinder	IBinder
        //   9	76	6	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   11	18	83	finally
        //   22	30	83	finally
        //   30	43	83	finally
        //   50	72	83	finally
      }
      
      /* Error */
      public void onNotificationPosted(IStatusBarNotificationHolder paramIStatusBarNotificationHolder, NotificationRankingUpdate paramNotificationRankingUpdate)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore_3
        //   2: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore 4
        //   7: aload 4
        //   9: ldc 26
        //   11: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +10 -> 25
        //   18: aload_1
        //   19: invokeinterface 79 1 0
        //   24: astore_3
        //   25: aload 4
        //   27: aload_3
        //   28: invokevirtual 82	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   31: aload_2
        //   32: ifnull +37 -> 69
        //   35: aload 4
        //   37: iconst_1
        //   38: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   41: aload_2
        //   42: aload 4
        //   44: iconst_0
        //   45: invokevirtual 61	android/service/notification/NotificationRankingUpdate:writeToParcel	(Landroid/os/Parcel;I)V
        //   48: aload_0
        //   49: getfield 19	android/service/notification/INotificationListener$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   52: iconst_2
        //   53: aload 4
        //   55: aconst_null
        //   56: iconst_1
        //   57: invokeinterface 49 5 0
        //   62: pop
        //   63: aload 4
        //   65: invokevirtual 52	android/os/Parcel:recycle	()V
        //   68: return
        //   69: aload 4
        //   71: iconst_0
        //   72: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   75: goto -27 -> 48
        //   78: astore_1
        //   79: aload 4
        //   81: invokevirtual 52	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramIStatusBarNotificationHolder	IStatusBarNotificationHolder
        //   0	86	2	paramNotificationRankingUpdate	NotificationRankingUpdate
        //   1	27	3	localIBinder	IBinder
        //   5	75	4	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   7	14	78	finally
        //   18	25	78	finally
        //   25	31	78	finally
        //   35	48	78	finally
        //   48	63	78	finally
        //   69	75	78	finally
      }
      
      /* Error */
      public void onNotificationRankingUpdate(NotificationRankingUpdate paramNotificationRankingUpdate)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 26
        //   7: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +33 -> 44
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 61	android/service/notification/NotificationRankingUpdate:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/service/notification/INotificationListener$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: iconst_4
        //   30: aload_2
        //   31: aconst_null
        //   32: iconst_1
        //   33: invokeinterface 49 5 0
        //   38: pop
        //   39: aload_2
        //   40: invokevirtual 52	android/os/Parcel:recycle	()V
        //   43: return
        //   44: aload_2
        //   45: iconst_0
        //   46: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   49: goto -24 -> 25
        //   52: astore_1
        //   53: aload_2
        //   54: invokevirtual 52	android/os/Parcel:recycle	()V
        //   57: aload_1
        //   58: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	59	0	this	Proxy
        //   0	59	1	paramNotificationRankingUpdate	NotificationRankingUpdate
        //   3	51	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	52	finally
        //   14	25	52	finally
        //   25	39	52	finally
        //   44	49	52	finally
      }
      
      /* Error */
      public void onNotificationRemoved(IStatusBarNotificationHolder paramIStatusBarNotificationHolder, NotificationRankingUpdate paramNotificationRankingUpdate)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore_3
        //   2: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore 4
        //   7: aload 4
        //   9: ldc 26
        //   11: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +10 -> 25
        //   18: aload_1
        //   19: invokeinterface 79 1 0
        //   24: astore_3
        //   25: aload 4
        //   27: aload_3
        //   28: invokevirtual 82	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   31: aload_2
        //   32: ifnull +37 -> 69
        //   35: aload 4
        //   37: iconst_1
        //   38: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   41: aload_2
        //   42: aload 4
        //   44: iconst_0
        //   45: invokevirtual 61	android/service/notification/NotificationRankingUpdate:writeToParcel	(Landroid/os/Parcel;I)V
        //   48: aload_0
        //   49: getfield 19	android/service/notification/INotificationListener$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   52: iconst_3
        //   53: aload 4
        //   55: aconst_null
        //   56: iconst_1
        //   57: invokeinterface 49 5 0
        //   62: pop
        //   63: aload 4
        //   65: invokevirtual 52	android/os/Parcel:recycle	()V
        //   68: return
        //   69: aload 4
        //   71: iconst_0
        //   72: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   75: goto -27 -> 48
        //   78: astore_1
        //   79: aload 4
        //   81: invokevirtual 52	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramIStatusBarNotificationHolder	IStatusBarNotificationHolder
        //   0	86	2	paramNotificationRankingUpdate	NotificationRankingUpdate
        //   1	27	3	localIBinder	IBinder
        //   5	75	4	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   7	14	78	finally
        //   18	25	78	finally
        //   25	31	78	finally
        //   35	48	78	finally
        //   48	63	78	finally
        //   69	75	78	finally
      }
      
      public void onNotificationRemovedReason(String paramString, long paramLong, int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.service.notification.INotificationListener");
          localParcel.writeString(paramString);
          localParcel.writeLong(paramLong);
          localParcel.writeInt(paramInt);
          this.mRemote.transact(11, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void onNotificationVisibilityChanged(String paramString, long paramLong, boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore 5
        //   3: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 6
        //   8: aload 6
        //   10: ldc 26
        //   12: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload 6
        //   17: aload_1
        //   18: invokevirtual 67	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   21: aload 6
        //   23: lload_2
        //   24: invokevirtual 71	android/os/Parcel:writeLong	(J)V
        //   27: iload 4
        //   29: ifeq +32 -> 61
        //   32: aload 6
        //   34: iload 5
        //   36: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   39: aload_0
        //   40: getfield 19	android/service/notification/INotificationListener$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   43: bipush 8
        //   45: aload 6
        //   47: aconst_null
        //   48: iconst_1
        //   49: invokeinterface 49 5 0
        //   54: pop
        //   55: aload 6
        //   57: invokevirtual 52	android/os/Parcel:recycle	()V
        //   60: return
        //   61: iconst_0
        //   62: istore 5
        //   64: goto -32 -> 32
        //   67: astore_1
        //   68: aload 6
        //   70: invokevirtual 52	android/os/Parcel:recycle	()V
        //   73: aload_1
        //   74: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	75	0	this	Proxy
        //   0	75	1	paramString	String
        //   0	75	2	paramLong	long
        //   0	75	4	paramBoolean	boolean
        //   1	62	5	i	int
        //   6	63	6	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	27	67	finally
        //   32	55	67	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/notification/INotificationListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */