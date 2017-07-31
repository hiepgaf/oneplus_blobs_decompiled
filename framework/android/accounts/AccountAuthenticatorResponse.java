package android.accounts;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.util.Log;

public class AccountAuthenticatorResponse
  implements Parcelable
{
  public static final Parcelable.Creator<AccountAuthenticatorResponse> CREATOR = new Parcelable.Creator()
  {
    public AccountAuthenticatorResponse createFromParcel(Parcel paramAnonymousParcel)
    {
      return new AccountAuthenticatorResponse(paramAnonymousParcel);
    }
    
    public AccountAuthenticatorResponse[] newArray(int paramAnonymousInt)
    {
      return new AccountAuthenticatorResponse[paramAnonymousInt];
    }
  };
  private static final String TAG = "AccountAuthenticator";
  private IAccountAuthenticatorResponse mAccountAuthenticatorResponse;
  
  public AccountAuthenticatorResponse(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse)
  {
    this.mAccountAuthenticatorResponse = paramIAccountAuthenticatorResponse;
  }
  
  public AccountAuthenticatorResponse(Parcel paramParcel)
  {
    this.mAccountAuthenticatorResponse = IAccountAuthenticatorResponse.Stub.asInterface(paramParcel.readStrongBinder());
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void onError(int paramInt, String paramString)
  {
    if (Log.isLoggable("AccountAuthenticator", 2)) {
      Log.v("AccountAuthenticator", "AccountAuthenticatorResponse.onError: " + paramInt + ", " + paramString);
    }
    try
    {
      this.mAccountAuthenticatorResponse.onError(paramInt, paramString);
      return;
    }
    catch (RemoteException paramString) {}
  }
  
  public void onRequestContinued()
  {
    if (Log.isLoggable("AccountAuthenticator", 2)) {
      Log.v("AccountAuthenticator", "AccountAuthenticatorResponse.onRequestContinued");
    }
    try
    {
      this.mAccountAuthenticatorResponse.onRequestContinued();
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public void onResult(Bundle paramBundle)
  {
    if (Log.isLoggable("AccountAuthenticator", 2))
    {
      paramBundle.keySet();
      Log.v("AccountAuthenticator", "AccountAuthenticatorResponse.onResult: " + AccountManager.sanitizeResult(paramBundle));
    }
    try
    {
      this.mAccountAuthenticatorResponse.onResult(paramBundle);
      return;
    }
    catch (RemoteException paramBundle) {}
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeStrongBinder(this.mAccountAuthenticatorResponse.asBinder());
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/accounts/AccountAuthenticatorResponse.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */