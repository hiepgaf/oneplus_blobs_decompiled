package android.app;

import android.content.IIntentSender;
import android.content.IIntentSender.Stub;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.view.InputEvent;
import android.view.Surface;

public abstract interface IActivityContainer
  extends IInterface
{
  public abstract void attachToDisplay(int paramInt)
    throws RemoteException;
  
  public abstract int getDisplayId()
    throws RemoteException;
  
  public abstract int getStackId()
    throws RemoteException;
  
  public abstract boolean injectEvent(InputEvent paramInputEvent)
    throws RemoteException;
  
  public abstract void release()
    throws RemoteException;
  
  public abstract void setSurface(Surface paramSurface, int paramInt1, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public abstract int startActivity(Intent paramIntent)
    throws RemoteException;
  
  public abstract int startActivityIntentSender(IIntentSender paramIIntentSender)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IActivityContainer
  {
    private static final String DESCRIPTOR = "android.app.IActivityContainer";
    static final int TRANSACTION_attachToDisplay = 1;
    static final int TRANSACTION_getDisplayId = 5;
    static final int TRANSACTION_getStackId = 6;
    static final int TRANSACTION_injectEvent = 7;
    static final int TRANSACTION_release = 8;
    static final int TRANSACTION_setSurface = 2;
    static final int TRANSACTION_startActivity = 3;
    static final int TRANSACTION_startActivityIntentSender = 4;
    
    public Stub()
    {
      attachInterface(this, "android.app.IActivityContainer");
    }
    
    public static IActivityContainer asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.app.IActivityContainer");
      if ((localIInterface != null) && ((localIInterface instanceof IActivityContainer))) {
        return (IActivityContainer)localIInterface;
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
        paramParcel2.writeString("android.app.IActivityContainer");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.app.IActivityContainer");
        attachToDisplay(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.app.IActivityContainer");
        if (paramParcel1.readInt() != 0) {}
        for (Surface localSurface = (Surface)Surface.CREATOR.createFromParcel(paramParcel1);; localSurface = null)
        {
          setSurface(localSurface, paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 3: 
        paramParcel1.enforceInterface("android.app.IActivityContainer");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramInt1 = startActivity(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.app.IActivityContainer");
        paramInt1 = startActivityIntentSender(IIntentSender.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.app.IActivityContainer");
        paramInt1 = getDisplayId();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.app.IActivityContainer");
        paramInt1 = getStackId();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 7: 
        paramParcel1.enforceInterface("android.app.IActivityContainer");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (InputEvent)InputEvent.CREATOR.createFromParcel(paramParcel1);
          boolean bool = injectEvent(paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label356;
          }
        }
        label356:
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      }
      paramParcel1.enforceInterface("android.app.IActivityContainer");
      release();
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IActivityContainer
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
      
      public void attachToDisplay(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.IActivityContainer");
          localParcel1.writeInt(paramInt);
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
      
      public int getDisplayId()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.IActivityContainer");
          this.mRemote.transact(5, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.app.IActivityContainer";
      }
      
      public int getStackId()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.IActivityContainer");
          this.mRemote.transact(6, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public boolean injectEvent(InputEvent paramInputEvent)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.IActivityContainer");
            if (paramInputEvent != null)
            {
              localParcel1.writeInt(1);
              paramInputEvent.writeToParcel(localParcel1, 0);
              this.mRemote.transact(7, localParcel1, localParcel2, 0);
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
      
      public void release()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.IActivityContainer");
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
      public void setSurface(Surface paramSurface, int paramInt1, int paramInt2, int paramInt3)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 5
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 6
        //   10: aload 5
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload_1
        //   18: ifnull +67 -> 85
        //   21: aload 5
        //   23: iconst_1
        //   24: invokevirtual 41	android/os/Parcel:writeInt	(I)V
        //   27: aload_1
        //   28: aload 5
        //   30: iconst_0
        //   31: invokevirtual 76	android/view/Surface:writeToParcel	(Landroid/os/Parcel;I)V
        //   34: aload 5
        //   36: iload_2
        //   37: invokevirtual 41	android/os/Parcel:writeInt	(I)V
        //   40: aload 5
        //   42: iload_3
        //   43: invokevirtual 41	android/os/Parcel:writeInt	(I)V
        //   46: aload 5
        //   48: iload 4
        //   50: invokevirtual 41	android/os/Parcel:writeInt	(I)V
        //   53: aload_0
        //   54: getfield 19	android/app/IActivityContainer$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   57: iconst_2
        //   58: aload 5
        //   60: aload 6
        //   62: iconst_0
        //   63: invokeinterface 47 5 0
        //   68: pop
        //   69: aload 6
        //   71: invokevirtual 50	android/os/Parcel:readException	()V
        //   74: aload 6
        //   76: invokevirtual 53	android/os/Parcel:recycle	()V
        //   79: aload 5
        //   81: invokevirtual 53	android/os/Parcel:recycle	()V
        //   84: return
        //   85: aload 5
        //   87: iconst_0
        //   88: invokevirtual 41	android/os/Parcel:writeInt	(I)V
        //   91: goto -57 -> 34
        //   94: astore_1
        //   95: aload 6
        //   97: invokevirtual 53	android/os/Parcel:recycle	()V
        //   100: aload 5
        //   102: invokevirtual 53	android/os/Parcel:recycle	()V
        //   105: aload_1
        //   106: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	107	0	this	Proxy
        //   0	107	1	paramSurface	Surface
        //   0	107	2	paramInt1	int
        //   0	107	3	paramInt2	int
        //   0	107	4	paramInt3	int
        //   3	98	5	localParcel1	Parcel
        //   8	88	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	17	94	finally
        //   21	34	94	finally
        //   34	74	94	finally
        //   85	91	94	finally
      }
      
      /* Error */
      public int startActivity(Intent paramIntent)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +51 -> 67
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 41	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 81	android/content/Intent:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_0
        //   31: getfield 19	android/app/IActivityContainer$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: iconst_3
        //   35: aload_3
        //   36: aload 4
        //   38: iconst_0
        //   39: invokeinterface 47 5 0
        //   44: pop
        //   45: aload 4
        //   47: invokevirtual 50	android/os/Parcel:readException	()V
        //   50: aload 4
        //   52: invokevirtual 59	android/os/Parcel:readInt	()I
        //   55: istore_2
        //   56: aload 4
        //   58: invokevirtual 53	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 53	android/os/Parcel:recycle	()V
        //   65: iload_2
        //   66: ireturn
        //   67: aload_3
        //   68: iconst_0
        //   69: invokevirtual 41	android/os/Parcel:writeInt	(I)V
        //   72: goto -42 -> 30
        //   75: astore_1
        //   76: aload 4
        //   78: invokevirtual 53	android/os/Parcel:recycle	()V
        //   81: aload_3
        //   82: invokevirtual 53	android/os/Parcel:recycle	()V
        //   85: aload_1
        //   86: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	87	0	this	Proxy
        //   0	87	1	paramIntent	Intent
        //   55	11	2	i	int
        //   3	79	3	localParcel1	Parcel
        //   7	70	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	75	finally
        //   19	30	75	finally
        //   30	56	75	finally
        //   67	72	75	finally
      }
      
      public int startActivityIntentSender(IIntentSender paramIIntentSender)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.IActivityContainer");
          if (paramIIntentSender != null) {
            localIBinder = paramIIntentSender.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(4, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/IActivityContainer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */