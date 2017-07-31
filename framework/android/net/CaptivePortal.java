package android.net;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public class CaptivePortal
  implements Parcelable
{
  public static final int APP_RETURN_DISMISSED = 0;
  public static final int APP_RETURN_UNWANTED = 1;
  public static final int APP_RETURN_WANTED_AS_IS = 2;
  public static final Parcelable.Creator<CaptivePortal> CREATOR = new Parcelable.Creator()
  {
    public CaptivePortal createFromParcel(Parcel paramAnonymousParcel)
    {
      return new CaptivePortal(paramAnonymousParcel.readStrongBinder());
    }
    
    public CaptivePortal[] newArray(int paramAnonymousInt)
    {
      return new CaptivePortal[paramAnonymousInt];
    }
  };
  private final IBinder mBinder;
  
  public CaptivePortal(IBinder paramIBinder)
  {
    this.mBinder = paramIBinder;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void ignoreNetwork()
  {
    try
    {
      ICaptivePortal.Stub.asInterface(this.mBinder).appResponse(1);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public void reportCaptivePortalDismissed()
  {
    try
    {
      ICaptivePortal.Stub.asInterface(this.mBinder).appResponse(0);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public void useNetwork()
  {
    try
    {
      ICaptivePortal.Stub.asInterface(this.mBinder).appResponse(2);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeStrongBinder(this.mBinder);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/CaptivePortal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */