package android.os;

public final class Messenger
  implements Parcelable
{
  public static final Parcelable.Creator<Messenger> CREATOR = new Parcelable.Creator()
  {
    public Messenger createFromParcel(Parcel paramAnonymousParcel)
    {
      Object localObject = null;
      IBinder localIBinder = paramAnonymousParcel.readStrongBinder();
      paramAnonymousParcel = (Parcel)localObject;
      if (localIBinder != null) {
        paramAnonymousParcel = new Messenger(localIBinder);
      }
      return paramAnonymousParcel;
    }
    
    public Messenger[] newArray(int paramAnonymousInt)
    {
      return new Messenger[paramAnonymousInt];
    }
  };
  private final IMessenger mTarget;
  
  public Messenger(Handler paramHandler)
  {
    this.mTarget = paramHandler.getIMessenger();
  }
  
  public Messenger(IBinder paramIBinder)
  {
    this.mTarget = IMessenger.Stub.asInterface(paramIBinder);
  }
  
  public static Messenger readMessengerOrNullFromParcel(Parcel paramParcel)
  {
    Object localObject = null;
    IBinder localIBinder = paramParcel.readStrongBinder();
    paramParcel = (Parcel)localObject;
    if (localIBinder != null) {
      paramParcel = new Messenger(localIBinder);
    }
    return paramParcel;
  }
  
  public static void writeMessengerOrNullToParcel(Messenger paramMessenger, Parcel paramParcel)
  {
    IBinder localIBinder = null;
    if (paramMessenger != null) {
      localIBinder = paramMessenger.mTarget.asBinder();
    }
    paramParcel.writeStrongBinder(localIBinder);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    try
    {
      boolean bool = this.mTarget.asBinder().equals(((Messenger)paramObject).mTarget.asBinder());
      return bool;
    }
    catch (ClassCastException paramObject) {}
    return false;
  }
  
  public IBinder getBinder()
  {
    return this.mTarget.asBinder();
  }
  
  public int hashCode()
  {
    return this.mTarget.asBinder().hashCode();
  }
  
  public void send(Message paramMessage)
    throws RemoteException
  {
    this.mTarget.send(paramMessage);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeStrongBinder(this.mTarget.asBinder());
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/Messenger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */