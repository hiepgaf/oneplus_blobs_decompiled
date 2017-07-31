package android.os;

public final class RemoteCallback
  implements Parcelable
{
  public static final Parcelable.Creator<RemoteCallback> CREATOR = new Parcelable.Creator()
  {
    public RemoteCallback createFromParcel(Parcel paramAnonymousParcel)
    {
      return new RemoteCallback(paramAnonymousParcel);
    }
    
    public RemoteCallback[] newArray(int paramAnonymousInt)
    {
      return new RemoteCallback[paramAnonymousInt];
    }
  };
  private final IRemoteCallback mCallback;
  private final Handler mHandler;
  private final OnResultListener mListener;
  
  RemoteCallback(Parcel paramParcel)
  {
    this.mListener = null;
    this.mHandler = null;
    this.mCallback = IRemoteCallback.Stub.asInterface(paramParcel.readStrongBinder());
  }
  
  public RemoteCallback(OnResultListener paramOnResultListener)
  {
    this(paramOnResultListener, null);
  }
  
  public RemoteCallback(OnResultListener paramOnResultListener, Handler paramHandler)
  {
    if (paramOnResultListener == null) {
      throw new NullPointerException("listener cannot be null");
    }
    this.mListener = paramOnResultListener;
    this.mHandler = paramHandler;
    this.mCallback = new IRemoteCallback.Stub()
    {
      public void sendResult(Bundle paramAnonymousBundle)
      {
        RemoteCallback.this.sendResult(paramAnonymousBundle);
      }
    };
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void sendResult(final Bundle paramBundle)
  {
    if (this.mListener != null)
    {
      if (this.mHandler != null)
      {
        this.mHandler.post(new Runnable()
        {
          public void run()
          {
            RemoteCallback.-get0(RemoteCallback.this).onResult(paramBundle);
          }
        });
        return;
      }
      this.mListener.onResult(paramBundle);
      return;
    }
    try
    {
      this.mCallback.sendResult(paramBundle);
      return;
    }
    catch (RemoteException paramBundle) {}
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeStrongBinder(this.mCallback.asBinder());
  }
  
  public static abstract interface OnResultListener
  {
    public abstract void onResult(Bundle paramBundle);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/RemoteCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */