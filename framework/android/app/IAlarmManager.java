package android.app;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.os.WorkSource;
import java.util.List;

public abstract interface IAlarmManager
  extends IInterface
{
  public abstract AlarmManager.AlarmClockInfo getNextAlarmClock(int paramInt)
    throws RemoteException;
  
  public abstract long getNextWakeFromIdleTime()
    throws RemoteException;
  
  public abstract void remove(PendingIntent paramPendingIntent, IAlarmListener paramIAlarmListener)
    throws RemoteException;
  
  public abstract void set(String paramString1, int paramInt1, long paramLong1, long paramLong2, long paramLong3, int paramInt2, PendingIntent paramPendingIntent, IAlarmListener paramIAlarmListener, String paramString2, WorkSource paramWorkSource, AlarmManager.AlarmClockInfo paramAlarmClockInfo)
    throws RemoteException;
  
  public abstract void setBlackAlarm(List<String> paramList)
    throws RemoteException;
  
  public abstract void setBlockAlarmUid(String paramString, boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public abstract boolean setTime(long paramLong)
    throws RemoteException;
  
  public abstract void setTimeZone(String paramString)
    throws RemoteException;
  
  public abstract void updateBlockedUids(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IAlarmManager
  {
    private static final String DESCRIPTOR = "android.app.IAlarmManager";
    static final int TRANSACTION_getNextAlarmClock = 6;
    static final int TRANSACTION_getNextWakeFromIdleTime = 5;
    static final int TRANSACTION_remove = 4;
    static final int TRANSACTION_set = 1;
    static final int TRANSACTION_setBlackAlarm = 9;
    static final int TRANSACTION_setBlockAlarmUid = 8;
    static final int TRANSACTION_setTime = 2;
    static final int TRANSACTION_setTimeZone = 3;
    static final int TRANSACTION_updateBlockedUids = 7;
    
    public Stub()
    {
      attachInterface(this, "android.app.IAlarmManager");
    }
    
    public static IAlarmManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.app.IAlarmManager");
      if ((localIInterface != null) && ((localIInterface instanceof IAlarmManager))) {
        return (IAlarmManager)localIInterface;
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
      long l1;
      Object localObject;
      label208:
      label264:
      label270:
      boolean bool;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.app.IAlarmManager");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.app.IAlarmManager");
        String str1 = paramParcel1.readString();
        paramInt1 = paramParcel1.readInt();
        l1 = paramParcel1.readLong();
        long l2 = paramParcel1.readLong();
        long l3 = paramParcel1.readLong();
        paramInt2 = paramParcel1.readInt();
        IAlarmListener localIAlarmListener;
        String str2;
        WorkSource localWorkSource;
        if (paramParcel1.readInt() != 0)
        {
          localObject = (PendingIntent)PendingIntent.CREATOR.createFromParcel(paramParcel1);
          localIAlarmListener = IAlarmListener.Stub.asInterface(paramParcel1.readStrongBinder());
          str2 = paramParcel1.readString();
          if (paramParcel1.readInt() == 0) {
            break label264;
          }
          localWorkSource = (WorkSource)WorkSource.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label270;
          }
        }
        for (paramParcel1 = (AlarmManager.AlarmClockInfo)AlarmManager.AlarmClockInfo.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          set(str1, paramInt1, l1, l2, l3, paramInt2, (PendingIntent)localObject, localIAlarmListener, str2, localWorkSource, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject = null;
          break;
          localWorkSource = null;
          break label208;
        }
      case 2: 
        paramParcel1.enforceInterface("android.app.IAlarmManager");
        bool = setTime(paramParcel1.readLong());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 3: 
        paramParcel1.enforceInterface("android.app.IAlarmManager");
        setTimeZone(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.app.IAlarmManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject = (PendingIntent)PendingIntent.CREATOR.createFromParcel(paramParcel1);; localObject = null)
        {
          remove((PendingIntent)localObject, IAlarmListener.Stub.asInterface(paramParcel1.readStrongBinder()));
          paramParcel2.writeNoException();
          return true;
        }
      case 5: 
        paramParcel1.enforceInterface("android.app.IAlarmManager");
        l1 = getNextWakeFromIdleTime();
        paramParcel2.writeNoException();
        paramParcel2.writeLong(l1);
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.app.IAlarmManager");
        paramParcel1 = getNextAlarmClock(paramParcel1.readInt());
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
      case 7: 
        paramParcel1.enforceInterface("android.app.IAlarmManager");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          updateBlockedUids(paramInt1, bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 8: 
        paramParcel1.enforceInterface("android.app.IAlarmManager");
        localObject = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          setBlockAlarmUid((String)localObject, bool, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      }
      paramParcel1.enforceInterface("android.app.IAlarmManager");
      setBlackAlarm(paramParcel1.createStringArrayList());
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IAlarmManager
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
        return "android.app.IAlarmManager";
      }
      
      /* Error */
      public AlarmManager.AlarmClockInfo getNextAlarmClock(int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 26
        //   12: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/app/IAlarmManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 6
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 50 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 53	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 57	android/os/Parcel:readInt	()I
        //   46: ifeq +28 -> 74
        //   49: getstatic 63	android/app/AlarmManager$AlarmClockInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   52: aload 4
        //   54: invokeinterface 69 2 0
        //   59: checkcast 59	android/app/AlarmManager$AlarmClockInfo
        //   62: astore_2
        //   63: aload 4
        //   65: invokevirtual 72	android/os/Parcel:recycle	()V
        //   68: aload_3
        //   69: invokevirtual 72	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: areturn
        //   74: aconst_null
        //   75: astore_2
        //   76: goto -13 -> 63
        //   79: astore_2
        //   80: aload 4
        //   82: invokevirtual 72	android/os/Parcel:recycle	()V
        //   85: aload_3
        //   86: invokevirtual 72	android/os/Parcel:recycle	()V
        //   89: aload_2
        //   90: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	91	0	this	Proxy
        //   0	91	1	paramInt	int
        //   62	14	2	localAlarmClockInfo	AlarmManager.AlarmClockInfo
        //   79	11	2	localObject	Object
        //   3	83	3	localParcel1	Parcel
        //   7	74	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	63	79	finally
      }
      
      public long getNextWakeFromIdleTime()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.IAlarmManager");
          this.mRemote.transact(5, localParcel1, localParcel2, 0);
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
      
      /* Error */
      public void remove(PendingIntent paramPendingIntent, IAlarmListener paramIAlarmListener)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore_3
        //   2: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore 4
        //   7: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   10: astore 5
        //   12: aload 4
        //   14: ldc 26
        //   16: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   19: aload_1
        //   20: ifnull +67 -> 87
        //   23: aload 4
        //   25: iconst_1
        //   26: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   29: aload_1
        //   30: aload 4
        //   32: iconst_0
        //   33: invokevirtual 86	android/app/PendingIntent:writeToParcel	(Landroid/os/Parcel;I)V
        //   36: aload_3
        //   37: astore_1
        //   38: aload_2
        //   39: ifnull +10 -> 49
        //   42: aload_2
        //   43: invokeinterface 90 1 0
        //   48: astore_1
        //   49: aload 4
        //   51: aload_1
        //   52: invokevirtual 93	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   55: aload_0
        //   56: getfield 19	android/app/IAlarmManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   59: iconst_4
        //   60: aload 4
        //   62: aload 5
        //   64: iconst_0
        //   65: invokeinterface 50 5 0
        //   70: pop
        //   71: aload 5
        //   73: invokevirtual 53	android/os/Parcel:readException	()V
        //   76: aload 5
        //   78: invokevirtual 72	android/os/Parcel:recycle	()V
        //   81: aload 4
        //   83: invokevirtual 72	android/os/Parcel:recycle	()V
        //   86: return
        //   87: aload 4
        //   89: iconst_0
        //   90: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   93: goto -57 -> 36
        //   96: astore_1
        //   97: aload 5
        //   99: invokevirtual 72	android/os/Parcel:recycle	()V
        //   102: aload 4
        //   104: invokevirtual 72	android/os/Parcel:recycle	()V
        //   107: aload_1
        //   108: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	109	0	this	Proxy
        //   0	109	1	paramPendingIntent	PendingIntent
        //   0	109	2	paramIAlarmListener	IAlarmListener
        //   1	36	3	localObject	Object
        //   5	98	4	localParcel1	Parcel
        //   10	88	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   12	19	96	finally
        //   23	36	96	finally
        //   42	49	96	finally
        //   49	76	96	finally
        //   87	93	96	finally
      }
      
      public void set(String paramString1, int paramInt1, long paramLong1, long paramLong2, long paramLong3, int paramInt2, PendingIntent paramPendingIntent, IAlarmListener paramIAlarmListener, String paramString2, WorkSource paramWorkSource, AlarmManager.AlarmClockInfo paramAlarmClockInfo)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.IAlarmManager");
            localParcel1.writeString(paramString1);
            localParcel1.writeInt(paramInt1);
            localParcel1.writeLong(paramLong1);
            localParcel1.writeLong(paramLong2);
            localParcel1.writeLong(paramLong3);
            localParcel1.writeInt(paramInt2);
            if (paramPendingIntent != null)
            {
              localParcel1.writeInt(1);
              paramPendingIntent.writeToParcel(localParcel1, 0);
              if (paramIAlarmListener != null)
              {
                paramString1 = paramIAlarmListener.asBinder();
                localParcel1.writeStrongBinder(paramString1);
                localParcel1.writeString(paramString2);
                if (paramWorkSource == null) {
                  break label198;
                }
                localParcel1.writeInt(1);
                paramWorkSource.writeToParcel(localParcel1, 0);
                if (paramAlarmClockInfo == null) {
                  break label207;
                }
                localParcel1.writeInt(1);
                paramAlarmClockInfo.writeToParcel(localParcel1, 0);
                this.mRemote.transact(1, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramString1 = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          continue;
          label198:
          localParcel1.writeInt(0);
          continue;
          label207:
          localParcel1.writeInt(0);
        }
      }
      
      public void setBlackAlarm(List<String> paramList)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.IAlarmManager");
          localParcel1.writeStringList(paramList);
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
      
      public void setBlockAlarmUid(String paramString, boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.IAlarmManager");
          localParcel1.writeString(paramString);
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          localParcel1.writeInt(paramInt);
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
      public boolean setTime(long paramLong)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 5
        //   5: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 6
        //   10: aload 5
        //   12: ldc 26
        //   14: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 5
        //   19: lload_1
        //   20: invokevirtual 102	android/os/Parcel:writeLong	(J)V
        //   23: aload_0
        //   24: getfield 19	android/app/IAlarmManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: iconst_2
        //   28: aload 5
        //   30: aload 6
        //   32: iconst_0
        //   33: invokeinterface 50 5 0
        //   38: pop
        //   39: aload 6
        //   41: invokevirtual 53	android/os/Parcel:readException	()V
        //   44: aload 6
        //   46: invokevirtual 57	android/os/Parcel:readInt	()I
        //   49: istore_3
        //   50: iload_3
        //   51: ifeq +19 -> 70
        //   54: iconst_1
        //   55: istore 4
        //   57: aload 6
        //   59: invokevirtual 72	android/os/Parcel:recycle	()V
        //   62: aload 5
        //   64: invokevirtual 72	android/os/Parcel:recycle	()V
        //   67: iload 4
        //   69: ireturn
        //   70: iconst_0
        //   71: istore 4
        //   73: goto -16 -> 57
        //   76: astore 7
        //   78: aload 6
        //   80: invokevirtual 72	android/os/Parcel:recycle	()V
        //   83: aload 5
        //   85: invokevirtual 72	android/os/Parcel:recycle	()V
        //   88: aload 7
        //   90: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	91	0	this	Proxy
        //   0	91	1	paramLong	long
        //   49	2	3	i	int
        //   55	17	4	bool	boolean
        //   3	81	5	localParcel1	Parcel
        //   8	71	6	localParcel2	Parcel
        //   76	13	7	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   10	50	76	finally
      }
      
      public void setTimeZone(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.IAlarmManager");
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
      
      public void updateBlockedUids(int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.IAlarmManager");
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/IAlarmManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */