package android.media.session;

import android.content.ComponentName;
import android.media.IRemoteVolumeController;
import android.media.IRemoteVolumeController.Stub;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.view.KeyEvent;
import java.util.List;

public abstract interface ISessionManager
  extends IInterface
{
  public abstract void addSessionsListener(IActiveSessionsListener paramIActiveSessionsListener, ComponentName paramComponentName, int paramInt)
    throws RemoteException;
  
  public abstract ISession createSession(String paramString1, ISessionCallback paramISessionCallback, String paramString2, int paramInt)
    throws RemoteException;
  
  public abstract void dispatchAdjustVolume(int paramInt1, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public abstract void dispatchMediaKeyEvent(KeyEvent paramKeyEvent, boolean paramBoolean)
    throws RemoteException;
  
  public abstract List<IBinder> getSessions(ComponentName paramComponentName, int paramInt)
    throws RemoteException;
  
  public abstract boolean isGlobalPriorityActive()
    throws RemoteException;
  
  public abstract void removeSessionsListener(IActiveSessionsListener paramIActiveSessionsListener)
    throws RemoteException;
  
  public abstract void setRemoteVolumeController(IRemoteVolumeController paramIRemoteVolumeController)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ISessionManager
  {
    private static final String DESCRIPTOR = "android.media.session.ISessionManager";
    static final int TRANSACTION_addSessionsListener = 5;
    static final int TRANSACTION_createSession = 1;
    static final int TRANSACTION_dispatchAdjustVolume = 4;
    static final int TRANSACTION_dispatchMediaKeyEvent = 3;
    static final int TRANSACTION_getSessions = 2;
    static final int TRANSACTION_isGlobalPriorityActive = 8;
    static final int TRANSACTION_removeSessionsListener = 6;
    static final int TRANSACTION_setRemoteVolumeController = 7;
    
    public Stub()
    {
      attachInterface(this, "android.media.session.ISessionManager");
    }
    
    public static ISessionManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.media.session.ISessionManager");
      if ((localIInterface != null) && ((localIInterface instanceof ISessionManager))) {
        return (ISessionManager)localIInterface;
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
        paramParcel2.writeString("android.media.session.ISessionManager");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.media.session.ISessionManager");
        paramParcel1 = createSession(paramParcel1.readString(), ISessionCallback.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 != null) {}
        for (paramParcel1 = paramParcel1.asBinder();; paramParcel1 = null)
        {
          paramParcel2.writeStrongBinder(paramParcel1);
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.media.session.ISessionManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; localObject = null)
        {
          paramParcel1 = getSessions((ComponentName)localObject, paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeBinderList(paramParcel1);
          return true;
        }
      case 3: 
        paramParcel1.enforceInterface("android.media.session.ISessionManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject = (KeyEvent)KeyEvent.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label271;
          }
        }
        for (bool = true;; bool = false)
        {
          dispatchMediaKeyEvent((KeyEvent)localObject, bool);
          paramParcel2.writeNoException();
          return true;
          localObject = null;
          break;
        }
      case 4: 
        paramParcel1.enforceInterface("android.media.session.ISessionManager");
        dispatchAdjustVolume(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.media.session.ISessionManager");
        IActiveSessionsListener localIActiveSessionsListener = IActiveSessionsListener.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0) {}
        for (localObject = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; localObject = null)
        {
          addSessionsListener(localIActiveSessionsListener, (ComponentName)localObject, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 6: 
        paramParcel1.enforceInterface("android.media.session.ISessionManager");
        removeSessionsListener(IActiveSessionsListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 7: 
        label271:
        paramParcel1.enforceInterface("android.media.session.ISessionManager");
        setRemoteVolumeController(IRemoteVolumeController.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.media.session.ISessionManager");
      boolean bool = isGlobalPriorityActive();
      paramParcel2.writeNoException();
      if (bool) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    }
    
    private static class Proxy
      implements ISessionManager
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      /* Error */
      public void addSessionsListener(IActiveSessionsListener paramIActiveSessionsListener, ComponentName paramComponentName, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 4
        //   3: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 5
        //   8: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 6
        //   13: aload 5
        //   15: ldc 32
        //   17: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload_1
        //   21: ifnull +11 -> 32
        //   24: aload_1
        //   25: invokeinterface 42 1 0
        //   30: astore 4
        //   32: aload 5
        //   34: aload 4
        //   36: invokevirtual 45	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   39: aload_2
        //   40: ifnull +54 -> 94
        //   43: aload 5
        //   45: iconst_1
        //   46: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   49: aload_2
        //   50: aload 5
        //   52: iconst_0
        //   53: invokevirtual 55	android/content/ComponentName:writeToParcel	(Landroid/os/Parcel;I)V
        //   56: aload 5
        //   58: iload_3
        //   59: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   62: aload_0
        //   63: getfield 19	android/media/session/ISessionManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   66: iconst_5
        //   67: aload 5
        //   69: aload 6
        //   71: iconst_0
        //   72: invokeinterface 61 5 0
        //   77: pop
        //   78: aload 6
        //   80: invokevirtual 64	android/os/Parcel:readException	()V
        //   83: aload 6
        //   85: invokevirtual 67	android/os/Parcel:recycle	()V
        //   88: aload 5
        //   90: invokevirtual 67	android/os/Parcel:recycle	()V
        //   93: return
        //   94: aload 5
        //   96: iconst_0
        //   97: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   100: goto -44 -> 56
        //   103: astore_1
        //   104: aload 6
        //   106: invokevirtual 67	android/os/Parcel:recycle	()V
        //   109: aload 5
        //   111: invokevirtual 67	android/os/Parcel:recycle	()V
        //   114: aload_1
        //   115: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	116	0	this	Proxy
        //   0	116	1	paramIActiveSessionsListener	IActiveSessionsListener
        //   0	116	2	paramComponentName	ComponentName
        //   0	116	3	paramInt	int
        //   1	34	4	localIBinder	IBinder
        //   6	104	5	localParcel1	Parcel
        //   11	94	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	20	103	finally
        //   24	32	103	finally
        //   32	39	103	finally
        //   43	56	103	finally
        //   56	83	103	finally
        //   94	100	103	finally
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public ISession createSession(String paramString1, ISessionCallback paramISessionCallback, String paramString2, int paramInt)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.session.ISessionManager");
          localParcel1.writeString(paramString1);
          paramString1 = (String)localObject;
          if (paramISessionCallback != null) {
            paramString1 = paramISessionCallback.asBinder();
          }
          localParcel1.writeStrongBinder(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString1 = ISession.Stub.asInterface(localParcel2.readStrongBinder());
          return paramString1;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void dispatchAdjustVolume(int paramInt1, int paramInt2, int paramInt3)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.session.ISessionManager");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeInt(paramInt3);
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
      
      public void dispatchMediaKeyEvent(KeyEvent paramKeyEvent, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.media.session.ISessionManager");
            if (paramKeyEvent != null)
            {
              localParcel1.writeInt(1);
              paramKeyEvent.writeToParcel(localParcel1, 0);
              break label104;
              localParcel1.writeInt(i);
              this.mRemote.transact(3, localParcel1, localParcel2, 0);
              localParcel2.readException();
            }
            else
            {
              localParcel1.writeInt(0);
            }
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          label104:
          do
          {
            i = 0;
            break;
          } while (!paramBoolean);
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.media.session.ISessionManager";
      }
      
      /* Error */
      public List<IBinder> getSessions(ComponentName paramComponentName, int paramInt)
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
        //   15: aload_1
        //   16: ifnull +56 -> 72
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 55	android/content/ComponentName:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_3
        //   31: iload_2
        //   32: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   35: aload_0
        //   36: getfield 19	android/media/session/ISessionManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: iconst_2
        //   40: aload_3
        //   41: aload 4
        //   43: iconst_0
        //   44: invokeinterface 61 5 0
        //   49: pop
        //   50: aload 4
        //   52: invokevirtual 64	android/os/Parcel:readException	()V
        //   55: aload 4
        //   57: invokevirtual 100	android/os/Parcel:createBinderArrayList	()Ljava/util/ArrayList;
        //   60: astore_1
        //   61: aload 4
        //   63: invokevirtual 67	android/os/Parcel:recycle	()V
        //   66: aload_3
        //   67: invokevirtual 67	android/os/Parcel:recycle	()V
        //   70: aload_1
        //   71: areturn
        //   72: aload_3
        //   73: iconst_0
        //   74: invokevirtual 49	android/os/Parcel:writeInt	(I)V
        //   77: goto -47 -> 30
        //   80: astore_1
        //   81: aload 4
        //   83: invokevirtual 67	android/os/Parcel:recycle	()V
        //   86: aload_3
        //   87: invokevirtual 67	android/os/Parcel:recycle	()V
        //   90: aload_1
        //   91: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	92	0	this	Proxy
        //   0	92	1	paramComponentName	ComponentName
        //   0	92	2	paramInt	int
        //   3	84	3	localParcel1	Parcel
        //   7	75	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	80	finally
        //   19	30	80	finally
        //   30	61	80	finally
        //   72	77	80	finally
      }
      
      /* Error */
      public boolean isGlobalPriorityActive()
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
        //   15: aload_0
        //   16: getfield 19	android/media/session/ISessionManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 8
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 61 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 64	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 108	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 67	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 67	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 67	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 67	android/os/Parcel:recycle	()V
        //   75: aload 5
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   41	2	1	i	int
        //   47	14	2	bool	boolean
        //   3	69	3	localParcel1	Parcel
        //   7	60	4	localParcel2	Parcel
        //   64	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	42	64	finally
      }
      
      public void removeSessionsListener(IActiveSessionsListener paramIActiveSessionsListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.session.ISessionManager");
          if (paramIActiveSessionsListener != null) {
            localIBinder = paramIActiveSessionsListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
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
      
      public void setRemoteVolumeController(IRemoteVolumeController paramIRemoteVolumeController)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.session.ISessionManager");
          if (paramIRemoteVolumeController != null) {
            localIBinder = paramIRemoteVolumeController.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/session/ISessionManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */