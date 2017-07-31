package android.app;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IActivityController
  extends IInterface
{
  public abstract boolean activityResuming(String paramString)
    throws RemoteException;
  
  public abstract boolean activityStarting(Intent paramIntent, String paramString)
    throws RemoteException;
  
  public abstract boolean appCrashed(String paramString1, int paramInt, String paramString2, String paramString3, long paramLong, String paramString4)
    throws RemoteException;
  
  public abstract int appEarlyNotResponding(String paramString1, int paramInt, String paramString2)
    throws RemoteException;
  
  public abstract int appNotResponding(String paramString1, int paramInt, String paramString2)
    throws RemoteException;
  
  public abstract int systemNotResponding(String paramString)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IActivityController
  {
    private static final String DESCRIPTOR = "android.app.IActivityController";
    static final int TRANSACTION_activityResuming = 2;
    static final int TRANSACTION_activityStarting = 1;
    static final int TRANSACTION_appCrashed = 3;
    static final int TRANSACTION_appEarlyNotResponding = 4;
    static final int TRANSACTION_appNotResponding = 5;
    static final int TRANSACTION_systemNotResponding = 6;
    
    public Stub()
    {
      attachInterface(this, "android.app.IActivityController");
    }
    
    public static IActivityController asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.app.IActivityController");
      if ((localIInterface != null) && ((localIInterface instanceof IActivityController))) {
        return (IActivityController)localIInterface;
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
        paramParcel2.writeString("android.app.IActivityController");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.app.IActivityController");
        Intent localIntent;
        if (paramParcel1.readInt() != 0)
        {
          localIntent = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
          bool = activityStarting(localIntent, paramParcel1.readString());
          paramParcel2.writeNoException();
          if (!bool) {
            break label149;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localIntent = null;
          break;
        }
      case 2: 
        paramParcel1.enforceInterface("android.app.IActivityController");
        bool = activityResuming(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 3: 
        paramParcel1.enforceInterface("android.app.IActivityController");
        bool = appCrashed(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readLong(), paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.app.IActivityController");
        paramInt1 = appEarlyNotResponding(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 5: 
        label149:
        paramParcel1.enforceInterface("android.app.IActivityController");
        paramInt1 = appNotResponding(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      }
      paramParcel1.enforceInterface("android.app.IActivityController");
      paramInt1 = systemNotResponding(paramParcel1.readString());
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    }
    
    private static class Proxy
      implements IActivityController
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      /* Error */
      public boolean activityResuming(String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 39	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_0
        //   24: getfield 19	android/app/IActivityController$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: iconst_2
        //   28: aload 4
        //   30: aload 5
        //   32: iconst_0
        //   33: invokeinterface 45 5 0
        //   38: pop
        //   39: aload 5
        //   41: invokevirtual 48	android/os/Parcel:readException	()V
        //   44: aload 5
        //   46: invokevirtual 52	android/os/Parcel:readInt	()I
        //   49: istore_2
        //   50: iload_2
        //   51: ifeq +17 -> 68
        //   54: iconst_1
        //   55: istore_3
        //   56: aload 5
        //   58: invokevirtual 55	android/os/Parcel:recycle	()V
        //   61: aload 4
        //   63: invokevirtual 55	android/os/Parcel:recycle	()V
        //   66: iload_3
        //   67: ireturn
        //   68: iconst_0
        //   69: istore_3
        //   70: goto -14 -> 56
        //   73: astore_1
        //   74: aload 5
        //   76: invokevirtual 55	android/os/Parcel:recycle	()V
        //   79: aload 4
        //   81: invokevirtual 55	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramString	String
        //   49	2	2	i	int
        //   55	15	3	bool	boolean
        //   3	77	4	localParcel1	Parcel
        //   8	67	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	50	73	finally
      }
      
      public boolean activityStarting(Intent paramIntent, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.IActivityController");
            if (paramIntent != null)
            {
              localParcel1.writeInt(1);
              paramIntent.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              this.mRemote.transact(1, localParcel1, localParcel2, 0);
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
      public boolean appCrashed(String paramString1, int paramInt, String paramString2, String paramString3, long paramLong, String paramString4)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 9
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 10
        //   10: aload 9
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 9
        //   19: aload_1
        //   20: invokevirtual 39	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 9
        //   25: iload_2
        //   26: invokevirtual 62	android/os/Parcel:writeInt	(I)V
        //   29: aload 9
        //   31: aload_3
        //   32: invokevirtual 39	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   35: aload 9
        //   37: aload 4
        //   39: invokevirtual 39	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   42: aload 9
        //   44: lload 5
        //   46: invokevirtual 74	android/os/Parcel:writeLong	(J)V
        //   49: aload 9
        //   51: aload 7
        //   53: invokevirtual 39	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   56: aload_0
        //   57: getfield 19	android/app/IActivityController$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   60: iconst_3
        //   61: aload 9
        //   63: aload 10
        //   65: iconst_0
        //   66: invokeinterface 45 5 0
        //   71: pop
        //   72: aload 10
        //   74: invokevirtual 48	android/os/Parcel:readException	()V
        //   77: aload 10
        //   79: invokevirtual 52	android/os/Parcel:readInt	()I
        //   82: istore_2
        //   83: iload_2
        //   84: ifeq +19 -> 103
        //   87: iconst_1
        //   88: istore 8
        //   90: aload 10
        //   92: invokevirtual 55	android/os/Parcel:recycle	()V
        //   95: aload 9
        //   97: invokevirtual 55	android/os/Parcel:recycle	()V
        //   100: iload 8
        //   102: ireturn
        //   103: iconst_0
        //   104: istore 8
        //   106: goto -16 -> 90
        //   109: astore_1
        //   110: aload 10
        //   112: invokevirtual 55	android/os/Parcel:recycle	()V
        //   115: aload 9
        //   117: invokevirtual 55	android/os/Parcel:recycle	()V
        //   120: aload_1
        //   121: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	122	0	this	Proxy
        //   0	122	1	paramString1	String
        //   0	122	2	paramInt	int
        //   0	122	3	paramString2	String
        //   0	122	4	paramString3	String
        //   0	122	5	paramLong	long
        //   0	122	7	paramString4	String
        //   88	17	8	bool	boolean
        //   3	113	9	localParcel1	Parcel
        //   8	103	10	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	83	109	finally
      }
      
      public int appEarlyNotResponding(String paramString1, int paramInt, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.IActivityController");
          localParcel1.writeString(paramString1);
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString2);
          this.mRemote.transact(4, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int appNotResponding(String paramString1, int paramInt, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.IActivityController");
          localParcel1.writeString(paramString1);
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString2);
          this.mRemote.transact(5, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
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
      
      public String getInterfaceDescriptor()
      {
        return "android.app.IActivityController";
      }
      
      public int systemNotResponding(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.IActivityController");
          localParcel1.writeString(paramString);
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/IActivityController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */