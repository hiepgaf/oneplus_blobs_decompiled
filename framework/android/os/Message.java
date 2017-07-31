package android.os;

import android.util.TimeUtils;

public final class Message
  implements Parcelable
{
  public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator()
  {
    public Message createFromParcel(Parcel paramAnonymousParcel)
    {
      Message localMessage = Message.obtain();
      Message.-wrap0(localMessage, paramAnonymousParcel);
      return localMessage;
    }
    
    public Message[] newArray(int paramAnonymousInt)
    {
      return new Message[paramAnonymousInt];
    }
  };
  static final int FLAGS_TO_CLEAR_ON_COPY_FROM = 1;
  static final int FLAG_ASYNCHRONOUS = 2;
  static final int FLAG_IN_USE = 1;
  private static final int MAX_POOL_SIZE = 50;
  private static boolean gCheckRecycle;
  private static Message sPool;
  private static int sPoolSize;
  private static final Object sPoolSync = new Object();
  public int arg1;
  public int arg2;
  Runnable callback;
  Bundle data;
  int flags;
  Message next;
  public Object obj;
  public Messenger replyTo;
  public int sendingUid = -1;
  Handler target;
  public int what;
  long when;
  
  static
  {
    sPoolSize = 0;
    gCheckRecycle = true;
  }
  
  public static Message obtain()
  {
    synchronized (sPoolSync)
    {
      if (sPool != null)
      {
        Message localMessage = sPool;
        sPool = localMessage.next;
        localMessage.next = null;
        localMessage.flags = 0;
        sPoolSize -= 1;
        return localMessage;
      }
      return new Message();
    }
  }
  
  public static Message obtain(Handler paramHandler)
  {
    Message localMessage = obtain();
    localMessage.target = paramHandler;
    return localMessage;
  }
  
  public static Message obtain(Handler paramHandler, int paramInt)
  {
    Message localMessage = obtain();
    localMessage.target = paramHandler;
    localMessage.what = paramInt;
    return localMessage;
  }
  
  public static Message obtain(Handler paramHandler, int paramInt1, int paramInt2, int paramInt3)
  {
    Message localMessage = obtain();
    localMessage.target = paramHandler;
    localMessage.what = paramInt1;
    localMessage.arg1 = paramInt2;
    localMessage.arg2 = paramInt3;
    return localMessage;
  }
  
  public static Message obtain(Handler paramHandler, int paramInt1, int paramInt2, int paramInt3, Object paramObject)
  {
    Message localMessage = obtain();
    localMessage.target = paramHandler;
    localMessage.what = paramInt1;
    localMessage.arg1 = paramInt2;
    localMessage.arg2 = paramInt3;
    localMessage.obj = paramObject;
    return localMessage;
  }
  
  public static Message obtain(Handler paramHandler, int paramInt, Object paramObject)
  {
    Message localMessage = obtain();
    localMessage.target = paramHandler;
    localMessage.what = paramInt;
    localMessage.obj = paramObject;
    return localMessage;
  }
  
  public static Message obtain(Handler paramHandler, Runnable paramRunnable)
  {
    Message localMessage = obtain();
    localMessage.target = paramHandler;
    localMessage.callback = paramRunnable;
    return localMessage;
  }
  
  public static Message obtain(Message paramMessage)
  {
    Message localMessage = obtain();
    localMessage.what = paramMessage.what;
    localMessage.arg1 = paramMessage.arg1;
    localMessage.arg2 = paramMessage.arg2;
    localMessage.obj = paramMessage.obj;
    localMessage.replyTo = paramMessage.replyTo;
    localMessage.sendingUid = paramMessage.sendingUid;
    if (paramMessage.data != null) {
      localMessage.data = new Bundle(paramMessage.data);
    }
    localMessage.target = paramMessage.target;
    localMessage.callback = paramMessage.callback;
    return localMessage;
  }
  
  private void readFromParcel(Parcel paramParcel)
  {
    this.what = paramParcel.readInt();
    this.arg1 = paramParcel.readInt();
    this.arg2 = paramParcel.readInt();
    if (paramParcel.readInt() != 0) {
      this.obj = paramParcel.readParcelable(getClass().getClassLoader());
    }
    this.when = paramParcel.readLong();
    this.data = paramParcel.readBundle();
    this.replyTo = Messenger.readMessengerOrNullFromParcel(paramParcel);
    this.sendingUid = paramParcel.readInt();
  }
  
  public static void updateCheckRecycle(int paramInt)
  {
    if (paramInt < 21) {
      gCheckRecycle = false;
    }
  }
  
  public void copyFrom(Message paramMessage)
  {
    paramMessage.flags &= 0xFFFFFFFE;
    this.what = paramMessage.what;
    this.arg1 = paramMessage.arg1;
    this.arg2 = paramMessage.arg2;
    this.obj = paramMessage.obj;
    this.replyTo = paramMessage.replyTo;
    this.sendingUid = paramMessage.sendingUid;
    if (paramMessage.data != null)
    {
      this.data = ((Bundle)paramMessage.data.clone());
      return;
    }
    this.data = null;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public Runnable getCallback()
  {
    return this.callback;
  }
  
  public Bundle getData()
  {
    if (this.data == null) {
      this.data = new Bundle();
    }
    return this.data;
  }
  
  public Handler getTarget()
  {
    return this.target;
  }
  
  public long getWhen()
  {
    return this.when;
  }
  
  public boolean isAsynchronous()
  {
    boolean bool = false;
    if ((this.flags & 0x2) != 0) {
      bool = true;
    }
    return bool;
  }
  
  boolean isInUse()
  {
    return (this.flags & 0x1) == 1;
  }
  
  void markInUse()
  {
    this.flags |= 0x1;
  }
  
  public Bundle peekData()
  {
    return this.data;
  }
  
  public void recycle()
  {
    if (isInUse())
    {
      if (gCheckRecycle) {
        throw new IllegalStateException("This message cannot be recycled because it is still in use.");
      }
      return;
    }
    recycleUnchecked();
  }
  
  void recycleUnchecked()
  {
    this.flags = 1;
    this.what = 0;
    this.arg1 = 0;
    this.arg2 = 0;
    this.obj = null;
    this.replyTo = null;
    this.sendingUid = -1;
    this.when = 0L;
    this.target = null;
    this.callback = null;
    this.data = null;
    synchronized (sPoolSync)
    {
      if (sPoolSize < 50)
      {
        this.next = sPool;
        sPool = this;
        sPoolSize += 1;
      }
      return;
    }
  }
  
  public void sendToTarget()
  {
    this.target.sendMessage(this);
  }
  
  public void setAsynchronous(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.flags |= 0x2;
      return;
    }
    this.flags &= 0xFFFFFFFD;
  }
  
  public void setData(Bundle paramBundle)
  {
    this.data = paramBundle;
  }
  
  public void setTarget(Handler paramHandler)
  {
    this.target = paramHandler;
  }
  
  public String toString()
  {
    return toString(SystemClock.uptimeMillis());
  }
  
  String toString(long paramLong)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("{ when=");
    TimeUtils.formatDuration(this.when - paramLong, localStringBuilder);
    if (this.target != null) {
      if (this.callback != null)
      {
        localStringBuilder.append(" callback=");
        localStringBuilder.append(this.callback.getClass().getName());
        if (this.arg1 != 0)
        {
          localStringBuilder.append(" arg1=");
          localStringBuilder.append(this.arg1);
        }
        if (this.arg2 != 0)
        {
          localStringBuilder.append(" arg2=");
          localStringBuilder.append(this.arg2);
        }
        if (this.obj != null)
        {
          localStringBuilder.append(" obj=");
          localStringBuilder.append(this.obj);
        }
        localStringBuilder.append(" target=");
        localStringBuilder.append(this.target.getClass().getName());
      }
    }
    for (;;)
    {
      localStringBuilder.append(" }");
      return localStringBuilder.toString();
      localStringBuilder.append(" what=");
      localStringBuilder.append(this.what);
      break;
      localStringBuilder.append(" barrier=");
      localStringBuilder.append(this.arg1);
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    if (this.callback != null) {
      throw new RuntimeException("Can't marshal callbacks across processes.");
    }
    paramParcel.writeInt(this.what);
    paramParcel.writeInt(this.arg1);
    paramParcel.writeInt(this.arg2);
    if (this.obj != null) {}
    for (;;)
    {
      try
      {
        Parcelable localParcelable = (Parcelable)this.obj;
        paramParcel.writeInt(1);
        paramParcel.writeParcelable(localParcelable, paramInt);
        paramParcel.writeLong(this.when);
        paramParcel.writeBundle(this.data);
        Messenger.writeMessengerOrNullToParcel(this.replyTo, paramParcel);
        paramParcel.writeInt(this.sendingUid);
        return;
      }
      catch (ClassCastException paramParcel)
      {
        throw new RuntimeException("Can't marshal non-Parcelable objects across processes.");
      }
      paramParcel.writeInt(0);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/Message.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */