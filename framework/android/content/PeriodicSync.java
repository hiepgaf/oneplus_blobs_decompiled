package android.content;

import android.accounts.Account;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Iterator;
import java.util.Objects;

public class PeriodicSync
  implements Parcelable
{
  public static final Parcelable.Creator<PeriodicSync> CREATOR = new Parcelable.Creator()
  {
    public PeriodicSync createFromParcel(Parcel paramAnonymousParcel)
    {
      return new PeriodicSync(paramAnonymousParcel, null);
    }
    
    public PeriodicSync[] newArray(int paramAnonymousInt)
    {
      return new PeriodicSync[paramAnonymousInt];
    }
  };
  public final Account account;
  public final String authority;
  public final Bundle extras;
  public final long flexTime;
  public final long period;
  
  public PeriodicSync(Account paramAccount, String paramString, Bundle paramBundle, long paramLong)
  {
    this.account = paramAccount;
    this.authority = paramString;
    if (paramBundle == null) {}
    for (this.extras = new Bundle();; this.extras = new Bundle(paramBundle))
    {
      this.period = paramLong;
      this.flexTime = 0L;
      return;
    }
  }
  
  public PeriodicSync(Account paramAccount, String paramString, Bundle paramBundle, long paramLong1, long paramLong2)
  {
    this.account = paramAccount;
    this.authority = paramString;
    this.extras = new Bundle(paramBundle);
    this.period = paramLong1;
    this.flexTime = paramLong2;
  }
  
  public PeriodicSync(PeriodicSync paramPeriodicSync)
  {
    this.account = paramPeriodicSync.account;
    this.authority = paramPeriodicSync.authority;
    this.extras = new Bundle(paramPeriodicSync.extras);
    this.period = paramPeriodicSync.period;
    this.flexTime = paramPeriodicSync.flexTime;
  }
  
  private PeriodicSync(Parcel paramParcel)
  {
    this.account = ((Account)paramParcel.readParcelable(null));
    this.authority = paramParcel.readString();
    this.extras = paramParcel.readBundle();
    this.period = paramParcel.readLong();
    this.flexTime = paramParcel.readLong();
  }
  
  public static boolean syncExtrasEquals(Bundle paramBundle1, Bundle paramBundle2)
  {
    if (paramBundle1.size() != paramBundle2.size()) {
      return false;
    }
    if (paramBundle1.isEmpty()) {
      return true;
    }
    Iterator localIterator = paramBundle1.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (!paramBundle2.containsKey(str)) {
        return false;
      }
      if (!Objects.equals(paramBundle1.get(str), paramBundle2.get(str))) {
        return false;
      }
    }
    return true;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof PeriodicSync)) {
      return false;
    }
    paramObject = (PeriodicSync)paramObject;
    boolean bool1 = bool2;
    if (this.account.equals(((PeriodicSync)paramObject).account))
    {
      bool1 = bool2;
      if (this.authority.equals(((PeriodicSync)paramObject).authority))
      {
        bool1 = bool2;
        if (this.period == ((PeriodicSync)paramObject).period) {
          bool1 = syncExtrasEquals(this.extras, ((PeriodicSync)paramObject).extras);
        }
      }
    }
    return bool1;
  }
  
  public String toString()
  {
    return "account: " + this.account + ", authority: " + this.authority + ". period: " + this.period + "s " + ", flex: " + this.flexTime;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeParcelable(this.account, paramInt);
    paramParcel.writeString(this.authority);
    paramParcel.writeBundle(this.extras);
    paramParcel.writeLong(this.period);
    paramParcel.writeLong(this.flexTime);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/PeriodicSync.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */