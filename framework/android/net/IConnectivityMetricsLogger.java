package android.net;

import android.app.PendingIntent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IConnectivityMetricsLogger
  extends IInterface
{
  public abstract ConnectivityMetricsEvent[] getEvents(ConnectivityMetricsEvent.Reference paramReference)
    throws RemoteException;
  
  public abstract long logEvent(ConnectivityMetricsEvent paramConnectivityMetricsEvent)
    throws RemoteException;
  
  public abstract long logEvents(ConnectivityMetricsEvent[] paramArrayOfConnectivityMetricsEvent)
    throws RemoteException;
  
  public abstract boolean register(PendingIntent paramPendingIntent)
    throws RemoteException;
  
  public abstract void unregister(PendingIntent paramPendingIntent)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IConnectivityMetricsLogger
  {
    private static final String DESCRIPTOR = "android.net.IConnectivityMetricsLogger";
    static final int TRANSACTION_getEvents = 3;
    static final int TRANSACTION_logEvent = 1;
    static final int TRANSACTION_logEvents = 2;
    static final int TRANSACTION_register = 4;
    static final int TRANSACTION_unregister = 5;
    
    public Stub()
    {
      attachInterface(this, "android.net.IConnectivityMetricsLogger");
    }
    
    public static IConnectivityMetricsLogger asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.net.IConnectivityMetricsLogger");
      if ((localIInterface != null) && ((localIInterface instanceof IConnectivityMetricsLogger))) {
        return (IConnectivityMetricsLogger)localIInterface;
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
      int i = 0;
      long l;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.net.IConnectivityMetricsLogger");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.net.IConnectivityMetricsLogger");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ConnectivityMetricsEvent)ConnectivityMetricsEvent.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          l = logEvent(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeLong(l);
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.net.IConnectivityMetricsLogger");
        l = logEvents((ConnectivityMetricsEvent[])paramParcel1.createTypedArray(ConnectivityMetricsEvent.CREATOR));
        paramParcel2.writeNoException();
        paramParcel2.writeLong(l);
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.net.IConnectivityMetricsLogger");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ConnectivityMetricsEvent.Reference)ConnectivityMetricsEvent.Reference.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          ConnectivityMetricsEvent[] arrayOfConnectivityMetricsEvent = getEvents(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeTypedArray(arrayOfConnectivityMetricsEvent, 1);
          if (paramParcel1 == null) {
            break;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
          return true;
        }
        paramParcel2.writeInt(0);
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.net.IConnectivityMetricsLogger");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (PendingIntent)PendingIntent.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          boolean bool = register(paramParcel1);
          paramParcel2.writeNoException();
          paramInt1 = i;
          if (bool) {
            paramInt1 = 1;
          }
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      }
      paramParcel1.enforceInterface("android.net.IConnectivityMetricsLogger");
      if (paramParcel1.readInt() != 0) {}
      for (paramParcel1 = (PendingIntent)PendingIntent.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        unregister(paramParcel1);
        paramParcel2.writeNoException();
        return true;
      }
    }
    
    private static class Proxy
      implements IConnectivityMetricsLogger
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
      public ConnectivityMetricsEvent[] getEvents(ConnectivityMetricsEvent.Reference paramReference)
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
        //   15: ifnull +67 -> 82
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 48	android/net/ConnectivityMetricsEvent$Reference:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/net/IConnectivityMetricsLogger$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: iconst_3
        //   34: aload_2
        //   35: aload_3
        //   36: iconst_0
        //   37: invokeinterface 54 5 0
        //   42: pop
        //   43: aload_3
        //   44: invokevirtual 57	android/os/Parcel:readException	()V
        //   47: aload_3
        //   48: getstatic 63	android/net/ConnectivityMetricsEvent:CREATOR	Landroid/os/Parcelable$Creator;
        //   51: invokevirtual 67	android/os/Parcel:createTypedArray	(Landroid/os/Parcelable$Creator;)[Ljava/lang/Object;
        //   54: checkcast 69	[Landroid/net/ConnectivityMetricsEvent;
        //   57: astore 4
        //   59: aload_3
        //   60: invokevirtual 73	android/os/Parcel:readInt	()I
        //   63: ifeq +8 -> 71
        //   66: aload_1
        //   67: aload_3
        //   68: invokevirtual 77	android/net/ConnectivityMetricsEvent$Reference:readFromParcel	(Landroid/os/Parcel;)V
        //   71: aload_3
        //   72: invokevirtual 80	android/os/Parcel:recycle	()V
        //   75: aload_2
        //   76: invokevirtual 80	android/os/Parcel:recycle	()V
        //   79: aload 4
        //   81: areturn
        //   82: aload_2
        //   83: iconst_0
        //   84: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   87: goto -58 -> 29
        //   90: astore_1
        //   91: aload_3
        //   92: invokevirtual 80	android/os/Parcel:recycle	()V
        //   95: aload_2
        //   96: invokevirtual 80	android/os/Parcel:recycle	()V
        //   99: aload_1
        //   100: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	101	0	this	Proxy
        //   0	101	1	paramReference	ConnectivityMetricsEvent.Reference
        //   3	93	2	localParcel1	Parcel
        //   7	85	3	localParcel2	Parcel
        //   57	23	4	arrayOfConnectivityMetricsEvent	ConnectivityMetricsEvent[]
        // Exception table:
        //   from	to	target	type
        //   8	14	90	finally
        //   18	29	90	finally
        //   29	71	90	finally
        //   82	87	90	finally
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.net.IConnectivityMetricsLogger";
      }
      
      /* Error */
      public long logEvent(ConnectivityMetricsEvent paramConnectivityMetricsEvent)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload_1
        //   18: ifnull +55 -> 73
        //   21: aload 4
        //   23: iconst_1
        //   24: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   27: aload_1
        //   28: aload 4
        //   30: iconst_0
        //   31: invokevirtual 86	android/net/ConnectivityMetricsEvent:writeToParcel	(Landroid/os/Parcel;I)V
        //   34: aload_0
        //   35: getfield 19	android/net/IConnectivityMetricsLogger$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   38: iconst_1
        //   39: aload 4
        //   41: aload 5
        //   43: iconst_0
        //   44: invokeinterface 54 5 0
        //   49: pop
        //   50: aload 5
        //   52: invokevirtual 57	android/os/Parcel:readException	()V
        //   55: aload 5
        //   57: invokevirtual 90	android/os/Parcel:readLong	()J
        //   60: lstore_2
        //   61: aload 5
        //   63: invokevirtual 80	android/os/Parcel:recycle	()V
        //   66: aload 4
        //   68: invokevirtual 80	android/os/Parcel:recycle	()V
        //   71: lload_2
        //   72: lreturn
        //   73: aload 4
        //   75: iconst_0
        //   76: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   79: goto -45 -> 34
        //   82: astore_1
        //   83: aload 5
        //   85: invokevirtual 80	android/os/Parcel:recycle	()V
        //   88: aload 4
        //   90: invokevirtual 80	android/os/Parcel:recycle	()V
        //   93: aload_1
        //   94: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	95	0	this	Proxy
        //   0	95	1	paramConnectivityMetricsEvent	ConnectivityMetricsEvent
        //   60	12	2	l	long
        //   3	86	4	localParcel1	Parcel
        //   8	76	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	17	82	finally
        //   21	34	82	finally
        //   34	61	82	finally
        //   73	79	82	finally
      }
      
      public long logEvents(ConnectivityMetricsEvent[] paramArrayOfConnectivityMetricsEvent)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.IConnectivityMetricsLogger");
          localParcel1.writeTypedArray(paramArrayOfConnectivityMetricsEvent, 0);
          this.mRemote.transact(2, localParcel1, localParcel2, 0);
          localParcel2.readException();
          long l = localParcel2.readLong();
          return l;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public boolean register(PendingIntent paramPendingIntent)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.net.IConnectivityMetricsLogger");
            if (paramPendingIntent != null)
            {
              localParcel1.writeInt(1);
              paramPendingIntent.writeToParcel(localParcel1, 0);
              this.mRemote.transact(4, localParcel1, localParcel2, 0);
              localParcel2.readException();
              int i = localParcel2.readInt();
              if (i != 0)
              {
                bool = true;
                return bool;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            boolean bool = false;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      /* Error */
      public void unregister(PendingIntent paramPendingIntent)
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
        //   20: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 101	android/app/PendingIntent:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/net/IConnectivityMetricsLogger$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: iconst_5
        //   34: aload_2
        //   35: aload_3
        //   36: iconst_0
        //   37: invokeinterface 54 5 0
        //   42: pop
        //   43: aload_3
        //   44: invokevirtual 57	android/os/Parcel:readException	()V
        //   47: aload_3
        //   48: invokevirtual 80	android/os/Parcel:recycle	()V
        //   51: aload_2
        //   52: invokevirtual 80	android/os/Parcel:recycle	()V
        //   55: return
        //   56: aload_2
        //   57: iconst_0
        //   58: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   61: goto -32 -> 29
        //   64: astore_1
        //   65: aload_3
        //   66: invokevirtual 80	android/os/Parcel:recycle	()V
        //   69: aload_2
        //   70: invokevirtual 80	android/os/Parcel:recycle	()V
        //   73: aload_1
        //   74: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	75	0	this	Proxy
        //   0	75	1	paramPendingIntent	PendingIntent
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/IConnectivityMetricsLogger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */