package android.accounts;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public class AccountManagerResponse
  implements Parcelable
{
  public static final Parcelable.Creator<AccountManagerResponse> CREATOR = new Parcelable.Creator()
  {
    public AccountManagerResponse createFromParcel(Parcel paramAnonymousParcel)
    {
      return new AccountManagerResponse(paramAnonymousParcel);
    }
    
    public AccountManagerResponse[] newArray(int paramAnonymousInt)
    {
      return new AccountManagerResponse[paramAnonymousInt];
    }
  };
  private IAccountManagerResponse mResponse;
  
  public AccountManagerResponse(IAccountManagerResponse paramIAccountManagerResponse)
  {
    this.mResponse = paramIAccountManagerResponse;
  }
  
  public AccountManagerResponse(Parcel paramParcel)
  {
    this.mResponse = IAccountManagerResponse.Stub.asInterface(paramParcel.readStrongBinder());
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void onError(int paramInt, String paramString)
  {
    try
    {
      this.mResponse.onError(paramInt, paramString);
      return;
    }
    catch (RemoteException paramString) {}
  }
  
  public void onResult(Bundle paramBundle)
  {
    try
    {
      this.mResponse.onResult(paramBundle);
      return;
    }
    catch (RemoteException paramBundle) {}
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeStrongBinder(this.mResponse.asBinder());
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/accounts/AccountManagerResponse.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */