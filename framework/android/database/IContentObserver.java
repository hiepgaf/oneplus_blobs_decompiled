package android.database;

import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IContentObserver
  extends IInterface
{
  public abstract void onChange(boolean paramBoolean, Uri paramUri, int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IContentObserver
  {
    private static final String DESCRIPTOR = "android.database.IContentObserver";
    static final int TRANSACTION_onChange = 1;
    
    public Stub()
    {
      attachInterface(this, "android.database.IContentObserver");
    }
    
    public static IContentObserver asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.database.IContentObserver");
      if ((localIInterface != null) && ((localIInterface instanceof IContentObserver))) {
        return (IContentObserver)localIInterface;
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
        paramParcel2.writeString("android.database.IContentObserver");
        return true;
      }
      paramParcel1.enforceInterface("android.database.IContentObserver");
      boolean bool;
      if (paramParcel1.readInt() != 0)
      {
        bool = true;
        if (paramParcel1.readInt() == 0) {
          break label101;
        }
      }
      label101:
      for (paramParcel2 = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
      {
        onChange(bool, paramParcel2, paramParcel1.readInt());
        return true;
        bool = false;
        break;
      }
    }
    
    private static class Proxy
      implements IContentObserver
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
        return "android.database.IContentObserver";
      }
      
      /* Error */
      public void onChange(boolean paramBoolean, Uri paramUri, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore 4
        //   3: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 5
        //   8: aload 5
        //   10: ldc 26
        //   12: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: iload_1
        //   16: ifeq +54 -> 70
        //   19: aload 5
        //   21: iload 4
        //   23: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   26: aload_2
        //   27: ifnull +49 -> 76
        //   30: aload 5
        //   32: iconst_1
        //   33: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   36: aload_2
        //   37: aload 5
        //   39: iconst_0
        //   40: invokevirtual 50	android/net/Uri:writeToParcel	(Landroid/os/Parcel;I)V
        //   43: aload 5
        //   45: iload_3
        //   46: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   49: aload_0
        //   50: getfield 19	android/database/IContentObserver$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   53: iconst_1
        //   54: aload 5
        //   56: aconst_null
        //   57: iconst_1
        //   58: invokeinterface 56 5 0
        //   63: pop
        //   64: aload 5
        //   66: invokevirtual 59	android/os/Parcel:recycle	()V
        //   69: return
        //   70: iconst_0
        //   71: istore 4
        //   73: goto -54 -> 19
        //   76: aload 5
        //   78: iconst_0
        //   79: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   82: goto -39 -> 43
        //   85: astore_2
        //   86: aload 5
        //   88: invokevirtual 59	android/os/Parcel:recycle	()V
        //   91: aload_2
        //   92: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	93	0	this	Proxy
        //   0	93	1	paramBoolean	boolean
        //   0	93	2	paramUri	Uri
        //   0	93	3	paramInt	int
        //   1	71	4	i	int
        //   6	81	5	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	15	85	finally
        //   19	26	85	finally
        //   30	43	85	finally
        //   43	64	85	finally
        //   76	82	85	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/database/IContentObserver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */