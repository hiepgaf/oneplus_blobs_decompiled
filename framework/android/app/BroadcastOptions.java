package android.app;

import android.os.Bundle;

public class BroadcastOptions
{
  static final String KEY_MAX_MANIFEST_RECEIVER_API_LEVEL = "android:broadcast.maxManifestReceiverApiLevel";
  static final String KEY_MIN_MANIFEST_RECEIVER_API_LEVEL = "android:broadcast.minManifestReceiverApiLevel";
  static final String KEY_TEMPORARY_APP_WHITELIST_DURATION = "android:broadcast.temporaryAppWhitelistDuration";
  private int mMaxManifestReceiverApiLevel = 10000;
  private int mMinManifestReceiverApiLevel = 0;
  private long mTemporaryAppWhitelistDuration;
  
  private BroadcastOptions() {}
  
  public BroadcastOptions(Bundle paramBundle)
  {
    this.mTemporaryAppWhitelistDuration = paramBundle.getLong("android:broadcast.temporaryAppWhitelistDuration");
    this.mMinManifestReceiverApiLevel = paramBundle.getInt("android:broadcast.minManifestReceiverApiLevel", 0);
    this.mMaxManifestReceiverApiLevel = paramBundle.getInt("android:broadcast.maxManifestReceiverApiLevel", 10000);
  }
  
  public static BroadcastOptions makeBasic()
  {
    return new BroadcastOptions();
  }
  
  public int getMaxManifestReceiverApiLevel()
  {
    return this.mMaxManifestReceiverApiLevel;
  }
  
  public int getMinManifestReceiverApiLevel()
  {
    return this.mMinManifestReceiverApiLevel;
  }
  
  public long getTemporaryAppWhitelistDuration()
  {
    return this.mTemporaryAppWhitelistDuration;
  }
  
  public void setMaxManifestReceiverApiLevel(int paramInt)
  {
    this.mMaxManifestReceiverApiLevel = paramInt;
  }
  
  public void setMinManifestReceiverApiLevel(int paramInt)
  {
    this.mMinManifestReceiverApiLevel = paramInt;
  }
  
  public void setTemporaryAppWhitelistDuration(long paramLong)
  {
    this.mTemporaryAppWhitelistDuration = paramLong;
  }
  
  public Bundle toBundle()
  {
    Bundle localBundle2 = new Bundle();
    if (this.mTemporaryAppWhitelistDuration > 0L) {
      localBundle2.putLong("android:broadcast.temporaryAppWhitelistDuration", this.mTemporaryAppWhitelistDuration);
    }
    if (this.mMinManifestReceiverApiLevel != 0) {
      localBundle2.putInt("android:broadcast.minManifestReceiverApiLevel", this.mMinManifestReceiverApiLevel);
    }
    if (this.mMaxManifestReceiverApiLevel != 10000) {
      localBundle2.putInt("android:broadcast.maxManifestReceiverApiLevel", this.mMaxManifestReceiverApiLevel);
    }
    Bundle localBundle1 = localBundle2;
    if (localBundle2.isEmpty()) {
      localBundle1 = null;
    }
    return localBundle1;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/BroadcastOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */