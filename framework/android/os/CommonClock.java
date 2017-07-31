package android.os;

import java.net.InetSocketAddress;
import java.util.NoSuchElementException;

public class CommonClock
{
  public static final int ERROR_ESTIMATE_UNKNOWN = Integer.MAX_VALUE;
  public static final long INVALID_TIMELINE_ID = 0L;
  private static final int METHOD_CBK_ON_TIMELINE_CHANGED = 1;
  private static final int METHOD_COMMON_TIME_TO_LOCAL_TIME = 2;
  private static final int METHOD_GET_COMMON_FREQ = 5;
  private static final int METHOD_GET_COMMON_TIME = 4;
  private static final int METHOD_GET_ESTIMATED_ERROR = 8;
  private static final int METHOD_GET_LOCAL_FREQ = 7;
  private static final int METHOD_GET_LOCAL_TIME = 6;
  private static final int METHOD_GET_MASTER_ADDRESS = 11;
  private static final int METHOD_GET_STATE = 10;
  private static final int METHOD_GET_TIMELINE_ID = 9;
  private static final int METHOD_IS_COMMON_TIME_VALID = 1;
  private static final int METHOD_LOCAL_TIME_TO_COMMON_TIME = 3;
  private static final int METHOD_REGISTER_LISTENER = 12;
  private static final int METHOD_UNREGISTER_LISTENER = 13;
  public static final String SERVICE_NAME = "common_time.clock";
  public static final int STATE_CLIENT = 1;
  public static final int STATE_INITIAL = 0;
  public static final int STATE_INVALID = -1;
  public static final int STATE_MASTER = 2;
  public static final int STATE_RONIN = 3;
  public static final int STATE_WAIT_FOR_ELECTION = 4;
  public static final long TIME_NOT_SYNCED = -1L;
  private TimelineChangedListener mCallbackTgt = null;
  private IBinder.DeathRecipient mDeathHandler = new IBinder.DeathRecipient()
  {
    public void binderDied()
    {
      synchronized (CommonClock.-get0(CommonClock.this))
      {
        if (CommonClock.-get1(CommonClock.this) != null) {
          CommonClock.-get1(CommonClock.this).onServerDied();
        }
        return;
      }
    }
  };
  private String mInterfaceDesc = "";
  private final Object mListenerLock = new Object();
  private IBinder mRemote = null;
  private OnServerDiedListener mServerDiedListener = null;
  private OnTimelineChangedListener mTimelineChangedListener = null;
  private CommonTimeUtils mUtils;
  
  public CommonClock()
    throws RemoteException
  {
    if (this.mRemote == null) {
      throw new RemoteException();
    }
    this.mInterfaceDesc = this.mRemote.getInterfaceDescriptor();
    this.mUtils = new CommonTimeUtils(this.mRemote, this.mInterfaceDesc);
    this.mRemote.linkToDeath(this.mDeathHandler, 0);
    registerTimelineChangeListener();
  }
  
  public static CommonClock create()
  {
    try
    {
      CommonClock localCommonClock = new CommonClock();
      return localCommonClock;
    }
    catch (RemoteException localRemoteException) {}
    return null;
  }
  
  /* Error */
  private void registerTimelineChangeListener()
    throws RemoteException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 111	android/os/CommonClock:mCallbackTgt	Landroid/os/CommonClock$TimelineChangedListener;
    //   4: ifnull +4 -> 8
    //   7: return
    //   8: invokestatic 148	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   11: astore_2
    //   12: invokestatic 148	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   15: astore_3
    //   16: aload_0
    //   17: new 14	android/os/CommonClock$TimelineChangedListener
    //   20: dup
    //   21: aload_0
    //   22: aconst_null
    //   23: invokespecial 151	android/os/CommonClock$TimelineChangedListener:<init>	(Landroid/os/CommonClock;Landroid/os/CommonClock$TimelineChangedListener;)V
    //   26: putfield 111	android/os/CommonClock:mCallbackTgt	Landroid/os/CommonClock$TimelineChangedListener;
    //   29: aload_2
    //   30: aload_0
    //   31: getfield 104	android/os/CommonClock:mInterfaceDesc	Ljava/lang/String;
    //   34: invokevirtual 155	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   37: aload_2
    //   38: aload_0
    //   39: getfield 111	android/os/CommonClock:mCallbackTgt	Landroid/os/CommonClock$TimelineChangedListener;
    //   42: invokevirtual 159	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
    //   45: aload_0
    //   46: getfield 100	android/os/CommonClock:mRemote	Landroid/os/IBinder;
    //   49: bipush 12
    //   51: aload_2
    //   52: aload_3
    //   53: iconst_0
    //   54: invokeinterface 163 5 0
    //   59: pop
    //   60: aload_3
    //   61: invokevirtual 167	android/os/Parcel:readInt	()I
    //   64: istore_1
    //   65: iload_1
    //   66: ifne +33 -> 99
    //   69: iconst_1
    //   70: istore_1
    //   71: aload_3
    //   72: invokevirtual 170	android/os/Parcel:recycle	()V
    //   75: aload_2
    //   76: invokevirtual 170	android/os/Parcel:recycle	()V
    //   79: iload_1
    //   80: ifne +18 -> 98
    //   83: aload_0
    //   84: aconst_null
    //   85: putfield 111	android/os/CommonClock:mCallbackTgt	Landroid/os/CommonClock$TimelineChangedListener;
    //   88: aload_0
    //   89: aconst_null
    //   90: putfield 100	android/os/CommonClock:mRemote	Landroid/os/IBinder;
    //   93: aload_0
    //   94: aconst_null
    //   95: putfield 131	android/os/CommonClock:mUtils	Landroid/os/CommonTimeUtils;
    //   98: return
    //   99: iconst_0
    //   100: istore_1
    //   101: goto -30 -> 71
    //   104: astore 4
    //   106: iconst_0
    //   107: istore_1
    //   108: aload_3
    //   109: invokevirtual 170	android/os/Parcel:recycle	()V
    //   112: aload_2
    //   113: invokevirtual 170	android/os/Parcel:recycle	()V
    //   116: goto -37 -> 79
    //   119: astore 4
    //   121: aload_3
    //   122: invokevirtual 170	android/os/Parcel:recycle	()V
    //   125: aload_2
    //   126: invokevirtual 170	android/os/Parcel:recycle	()V
    //   129: aload 4
    //   131: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	132	0	this	CommonClock
    //   64	44	1	i	int
    //   11	115	2	localParcel1	Parcel
    //   15	107	3	localParcel2	Parcel
    //   104	1	4	localRemoteException	RemoteException
    //   119	11	4	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   29	65	104	android/os/RemoteException
    //   29	65	119	finally
  }
  
  private void throwOnDeadServer()
    throws RemoteException
  {
    if ((this.mRemote == null) || (this.mUtils == null)) {
      throw new RemoteException();
    }
  }
  
  private void unregisterTimelineChangeListener()
  {
    if (this.mCallbackTgt == null) {
      return;
    }
    localParcel1 = Parcel.obtain();
    localParcel2 = Parcel.obtain();
    try
    {
      localParcel1.writeInterfaceToken(this.mInterfaceDesc);
      localParcel1.writeStrongBinder(this.mCallbackTgt);
      this.mRemote.transact(13, localParcel1, localParcel2, 0);
      localParcel2.recycle();
      localParcel1.recycle();
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        localRemoteException = localRemoteException;
        localParcel2.recycle();
        localParcel1.recycle();
      }
    }
    finally
    {
      localObject = finally;
      localParcel2.recycle();
      localParcel1.recycle();
      this.mCallbackTgt = null;
      throw ((Throwable)localObject);
    }
    this.mCallbackTgt = null;
  }
  
  protected void finalize()
    throws Throwable
  {
    release();
  }
  
  public int getEstimatedError()
    throws RemoteException
  {
    throwOnDeadServer();
    return this.mUtils.transactGetInt(8, Integer.MAX_VALUE);
  }
  
  public InetSocketAddress getMasterAddr()
    throws RemoteException
  {
    throwOnDeadServer();
    return this.mUtils.transactGetSockaddr(11);
  }
  
  public int getState()
    throws RemoteException
  {
    throwOnDeadServer();
    return this.mUtils.transactGetInt(10, -1);
  }
  
  public long getTime()
    throws RemoteException
  {
    throwOnDeadServer();
    return this.mUtils.transactGetLong(4, -1L);
  }
  
  public long getTimelineId()
    throws RemoteException
  {
    throwOnDeadServer();
    return this.mUtils.transactGetLong(9, 0L);
  }
  
  public void release()
  {
    unregisterTimelineChangeListener();
    if (this.mRemote != null) {}
    try
    {
      this.mRemote.unlinkToDeath(this.mDeathHandler, 0);
      this.mRemote = null;
      this.mUtils = null;
      return;
    }
    catch (NoSuchElementException localNoSuchElementException)
    {
      for (;;) {}
    }
  }
  
  public void setServerDiedListener(OnServerDiedListener paramOnServerDiedListener)
  {
    synchronized (this.mListenerLock)
    {
      this.mServerDiedListener = paramOnServerDiedListener;
      return;
    }
  }
  
  public void setTimelineChangedListener(OnTimelineChangedListener paramOnTimelineChangedListener)
  {
    synchronized (this.mListenerLock)
    {
      this.mTimelineChangedListener = paramOnTimelineChangedListener;
      return;
    }
  }
  
  public static abstract interface OnServerDiedListener
  {
    public abstract void onServerDied();
  }
  
  public static abstract interface OnTimelineChangedListener
  {
    public abstract void onTimelineChanged(long paramLong);
  }
  
  private class TimelineChangedListener
    extends Binder
  {
    private static final String DESCRIPTOR = "android.os.ICommonClockListener";
    
    private TimelineChangedListener() {}
    
    protected boolean onTransact(int paramInt1, Parcel arg2, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, ???, paramParcel2, paramInt2);
      }
      ???.enforceInterface("android.os.ICommonClockListener");
      long l = ???.readLong();
      synchronized (CommonClock.-get0(CommonClock.this))
      {
        if (CommonClock.-get2(CommonClock.this) != null) {
          CommonClock.-get2(CommonClock.this).onTimelineChanged(l);
        }
        return true;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/CommonClock.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */