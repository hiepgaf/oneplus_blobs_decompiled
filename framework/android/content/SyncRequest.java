package android.content;

import android.accounts.Account;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class SyncRequest
  implements Parcelable
{
  public static final Parcelable.Creator<SyncRequest> CREATOR = new Parcelable.Creator()
  {
    public SyncRequest createFromParcel(Parcel paramAnonymousParcel)
    {
      return new SyncRequest(paramAnonymousParcel, null);
    }
    
    public SyncRequest[] newArray(int paramAnonymousInt)
    {
      return new SyncRequest[paramAnonymousInt];
    }
  };
  private static final String TAG = "SyncRequest";
  private final Account mAccountToSync;
  private final String mAuthority;
  private final boolean mDisallowMetered;
  private final Bundle mExtras;
  private final boolean mIsAuthority;
  private final boolean mIsExpedited;
  private final boolean mIsPeriodic;
  private final long mSyncFlexTimeSecs;
  private final long mSyncRunTimeSecs;
  
  protected SyncRequest(Builder paramBuilder)
  {
    this.mSyncFlexTimeSecs = Builder.-get6(paramBuilder);
    this.mSyncRunTimeSecs = Builder.-get7(paramBuilder);
    this.mAccountToSync = Builder.-get0(paramBuilder);
    this.mAuthority = Builder.-get1(paramBuilder);
    if (Builder.-get9(paramBuilder) == 1)
    {
      bool1 = true;
      this.mIsPeriodic = bool1;
      if (Builder.-get8(paramBuilder) != 2) {
        break label116;
      }
    }
    label116:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.mIsAuthority = bool1;
      this.mIsExpedited = Builder.-get4(paramBuilder);
      this.mExtras = new Bundle(Builder.-get2(paramBuilder));
      this.mExtras.putAll(Builder.-get5(paramBuilder));
      this.mDisallowMetered = Builder.-get3(paramBuilder);
      return;
      bool1 = false;
      break;
    }
  }
  
  private SyncRequest(Parcel paramParcel)
  {
    this.mExtras = Bundle.setDefusable(paramParcel.readBundle(), true);
    this.mSyncFlexTimeSecs = paramParcel.readLong();
    this.mSyncRunTimeSecs = paramParcel.readLong();
    if (paramParcel.readInt() != 0)
    {
      bool1 = true;
      this.mIsPeriodic = bool1;
      if (paramParcel.readInt() == 0) {
        break label116;
      }
      bool1 = true;
      label57:
      this.mDisallowMetered = bool1;
      if (paramParcel.readInt() == 0) {
        break label121;
      }
      bool1 = true;
      label71:
      this.mIsAuthority = bool1;
      if (paramParcel.readInt() == 0) {
        break label126;
      }
    }
    label116:
    label121:
    label126:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.mIsExpedited = bool1;
      this.mAccountToSync = ((Account)paramParcel.readParcelable(null));
      this.mAuthority = paramParcel.readString();
      return;
      bool1 = false;
      break;
      bool1 = false;
      break label57;
      bool1 = false;
      break label71;
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public Account getAccount()
  {
    return this.mAccountToSync;
  }
  
  public Bundle getBundle()
  {
    return this.mExtras;
  }
  
  public String getProvider()
  {
    return this.mAuthority;
  }
  
  public long getSyncFlexTime()
  {
    return this.mSyncFlexTimeSecs;
  }
  
  public long getSyncRunTime()
  {
    return this.mSyncRunTimeSecs;
  }
  
  public boolean isExpedited()
  {
    return this.mIsExpedited;
  }
  
  public boolean isPeriodic()
  {
    return this.mIsPeriodic;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int j = 1;
    paramParcel.writeBundle(this.mExtras);
    paramParcel.writeLong(this.mSyncFlexTimeSecs);
    paramParcel.writeLong(this.mSyncRunTimeSecs);
    if (this.mIsPeriodic)
    {
      i = 1;
      paramParcel.writeInt(i);
      if (!this.mDisallowMetered) {
        break label107;
      }
      i = 1;
      label50:
      paramParcel.writeInt(i);
      if (!this.mIsAuthority) {
        break label112;
      }
      i = 1;
      label64:
      paramParcel.writeInt(i);
      if (!this.mIsExpedited) {
        break label117;
      }
    }
    label107:
    label112:
    label117:
    for (int i = j;; i = 0)
    {
      paramParcel.writeInt(i);
      paramParcel.writeParcelable(this.mAccountToSync, paramInt);
      paramParcel.writeString(this.mAuthority);
      return;
      i = 0;
      break;
      i = 0;
      break label50;
      i = 0;
      break label64;
    }
  }
  
  public static class Builder
  {
    private static final int SYNC_TARGET_ADAPTER = 2;
    private static final int SYNC_TARGET_UNKNOWN = 0;
    private static final int SYNC_TYPE_ONCE = 2;
    private static final int SYNC_TYPE_PERIODIC = 1;
    private static final int SYNC_TYPE_UNKNOWN = 0;
    private Account mAccount;
    private String mAuthority;
    private Bundle mCustomExtras;
    private boolean mDisallowMetered;
    private boolean mExpedited;
    private boolean mIgnoreBackoff;
    private boolean mIgnoreSettings;
    private boolean mIsManual;
    private boolean mNoRetry;
    private boolean mRequiresCharging;
    private Bundle mSyncConfigExtras;
    private long mSyncFlexTimeSecs;
    private long mSyncRunTimeSecs;
    private int mSyncTarget = 0;
    private int mSyncType = 0;
    
    private void setupInterval(long paramLong1, long paramLong2)
    {
      if (paramLong2 > paramLong1) {
        throw new IllegalArgumentException("Specified run time for the sync must be after the specified flex time.");
      }
      this.mSyncRunTimeSecs = paramLong1;
      this.mSyncFlexTimeSecs = paramLong2;
    }
    
    public SyncRequest build()
    {
      ContentResolver.validateSyncExtrasBundle(this.mCustomExtras);
      if (this.mCustomExtras == null) {
        this.mCustomExtras = new Bundle();
      }
      this.mSyncConfigExtras = new Bundle();
      if (this.mIgnoreBackoff) {
        this.mSyncConfigExtras.putBoolean("ignore_backoff", true);
      }
      if (this.mDisallowMetered) {
        this.mSyncConfigExtras.putBoolean("allow_metered", true);
      }
      if (this.mRequiresCharging) {
        this.mSyncConfigExtras.putBoolean("require_charging", true);
      }
      if (this.mIgnoreSettings) {
        this.mSyncConfigExtras.putBoolean("ignore_settings", true);
      }
      if (this.mNoRetry) {
        this.mSyncConfigExtras.putBoolean("do_not_retry", true);
      }
      if (this.mExpedited) {
        this.mSyncConfigExtras.putBoolean("expedited", true);
      }
      if (this.mIsManual)
      {
        this.mSyncConfigExtras.putBoolean("ignore_backoff", true);
        this.mSyncConfigExtras.putBoolean("ignore_settings", true);
      }
      if ((this.mSyncType == 1) && ((ContentResolver.invalidPeriodicExtras(this.mCustomExtras)) || (ContentResolver.invalidPeriodicExtras(this.mSyncConfigExtras)))) {
        throw new IllegalArgumentException("Illegal extras were set");
      }
      if (this.mSyncTarget == 0) {
        throw new IllegalArgumentException("Must specify an adapter with setSyncAdapter(Account, String");
      }
      return new SyncRequest(this);
    }
    
    public Builder setDisallowMetered(boolean paramBoolean)
    {
      if ((this.mIgnoreSettings) && (paramBoolean)) {
        throw new IllegalArgumentException("setDisallowMetered(true) after having specified that settings are ignored.");
      }
      this.mDisallowMetered = paramBoolean;
      return this;
    }
    
    public Builder setExpedited(boolean paramBoolean)
    {
      this.mExpedited = paramBoolean;
      return this;
    }
    
    public Builder setExtras(Bundle paramBundle)
    {
      this.mCustomExtras = paramBundle;
      return this;
    }
    
    public Builder setIgnoreBackoff(boolean paramBoolean)
    {
      this.mIgnoreBackoff = paramBoolean;
      return this;
    }
    
    public Builder setIgnoreSettings(boolean paramBoolean)
    {
      if ((this.mDisallowMetered) && (paramBoolean)) {
        throw new IllegalArgumentException("setIgnoreSettings(true) after having specified sync settings with this builder.");
      }
      this.mIgnoreSettings = paramBoolean;
      return this;
    }
    
    public Builder setManual(boolean paramBoolean)
    {
      this.mIsManual = paramBoolean;
      return this;
    }
    
    public Builder setNoRetry(boolean paramBoolean)
    {
      this.mNoRetry = paramBoolean;
      return this;
    }
    
    public Builder setRequiresCharging(boolean paramBoolean)
    {
      this.mRequiresCharging = true;
      return this;
    }
    
    public Builder setSyncAdapter(Account paramAccount, String paramString)
    {
      if (this.mSyncTarget != 0) {
        throw new IllegalArgumentException("Sync target has already been defined.");
      }
      if ((paramString != null) && (paramString.length() == 0)) {
        throw new IllegalArgumentException("Authority must be non-empty");
      }
      this.mSyncTarget = 2;
      this.mAccount = paramAccount;
      this.mAuthority = paramString;
      return this;
    }
    
    public Builder syncOnce()
    {
      if (this.mSyncType != 0) {
        throw new IllegalArgumentException("Sync type has already been defined.");
      }
      this.mSyncType = 2;
      setupInterval(0L, 0L);
      return this;
    }
    
    public Builder syncPeriodic(long paramLong1, long paramLong2)
    {
      if (this.mSyncType != 0) {
        throw new IllegalArgumentException("Sync type has already been defined.");
      }
      this.mSyncType = 1;
      setupInterval(paramLong1, paramLong2);
      return this;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/SyncRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */