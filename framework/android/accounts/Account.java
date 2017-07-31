package android.accounts;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import java.util.Set;

public class Account
  implements Parcelable
{
  public static final Parcelable.Creator<Account> CREATOR = new Parcelable.Creator()
  {
    public Account createFromParcel(Parcel paramAnonymousParcel)
    {
      return new Account(paramAnonymousParcel);
    }
    
    public Account[] newArray(int paramAnonymousInt)
    {
      return new Account[paramAnonymousInt];
    }
  };
  private static final String TAG = "Account";
  @GuardedBy("sAccessedAccounts")
  private static final Set<Account> sAccessedAccounts = new ArraySet();
  private final String accessId;
  public final String name;
  public final String type;
  
  public Account(Account paramAccount, String paramString)
  {
    this(paramAccount.name, paramAccount.type, paramString);
  }
  
  public Account(Parcel arg1)
  {
    this.name = ???.readString();
    this.type = ???.readString();
    this.accessId = ???.readString();
    if (this.accessId != null) {}
    synchronized (sAccessedAccounts)
    {
      boolean bool = sAccessedAccounts.add(this);
      if (bool) {}
      try
      {
        IAccountManager.Stub.asInterface(ServiceManager.getService("account")).onAccountAccessed(this.accessId);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Log.e("Account", "Error noting account access", localRemoteException);
        }
      }
    }
  }
  
  public Account(String paramString1, String paramString2)
  {
    this(paramString1, paramString2, null);
  }
  
  public Account(String paramString1, String paramString2, String paramString3)
  {
    if (TextUtils.isEmpty(paramString1)) {
      throw new IllegalArgumentException("the name must not be empty: " + paramString1);
    }
    if (TextUtils.isEmpty(paramString2)) {
      throw new IllegalArgumentException("the type must not be empty: " + paramString2);
    }
    this.name = paramString1;
    this.type = paramString2;
    this.accessId = paramString3;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = false;
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof Account)) {
      return false;
    }
    paramObject = (Account)paramObject;
    if (this.name.equals(((Account)paramObject).name)) {
      bool = this.type.equals(((Account)paramObject).type);
    }
    return bool;
  }
  
  public String getAccessId()
  {
    return this.accessId;
  }
  
  public int hashCode()
  {
    return (this.name.hashCode() + 527) * 31 + this.type.hashCode();
  }
  
  public String toString()
  {
    return "Account {name=" + this.name + ", type=" + this.type + "}";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.name);
    paramParcel.writeString(this.type);
    paramParcel.writeString(this.accessId);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/accounts/Account.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */