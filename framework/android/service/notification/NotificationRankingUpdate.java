package android.service.notification;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class NotificationRankingUpdate
  implements Parcelable
{
  public static final Parcelable.Creator<NotificationRankingUpdate> CREATOR = new Parcelable.Creator()
  {
    public NotificationRankingUpdate createFromParcel(Parcel paramAnonymousParcel)
    {
      return new NotificationRankingUpdate(paramAnonymousParcel);
    }
    
    public NotificationRankingUpdate[] newArray(int paramAnonymousInt)
    {
      return new NotificationRankingUpdate[paramAnonymousInt];
    }
  };
  private final int[] mImportance;
  private final Bundle mImportanceExplanation;
  private final String[] mInterceptedKeys;
  private final String[] mKeys;
  private final Bundle mOverrideGroupKeys;
  private final Bundle mSuppressedVisualEffects;
  private final Bundle mVisibilityOverrides;
  
  public NotificationRankingUpdate(Parcel paramParcel)
  {
    this.mKeys = paramParcel.readStringArray();
    this.mInterceptedKeys = paramParcel.readStringArray();
    this.mVisibilityOverrides = paramParcel.readBundle();
    this.mSuppressedVisualEffects = paramParcel.readBundle();
    this.mImportance = new int[this.mKeys.length];
    paramParcel.readIntArray(this.mImportance);
    this.mImportanceExplanation = paramParcel.readBundle();
    this.mOverrideGroupKeys = paramParcel.readBundle();
  }
  
  public NotificationRankingUpdate(String[] paramArrayOfString1, String[] paramArrayOfString2, Bundle paramBundle1, Bundle paramBundle2, int[] paramArrayOfInt, Bundle paramBundle3, Bundle paramBundle4)
  {
    this.mKeys = paramArrayOfString1;
    this.mInterceptedKeys = paramArrayOfString2;
    this.mVisibilityOverrides = paramBundle1;
    this.mSuppressedVisualEffects = paramBundle2;
    this.mImportance = paramArrayOfInt;
    this.mImportanceExplanation = paramBundle3;
    this.mOverrideGroupKeys = paramBundle4;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int[] getImportance()
  {
    return this.mImportance;
  }
  
  public Bundle getImportanceExplanation()
  {
    return this.mImportanceExplanation;
  }
  
  public String[] getInterceptedKeys()
  {
    return this.mInterceptedKeys;
  }
  
  public String[] getOrderedKeys()
  {
    return this.mKeys;
  }
  
  public Bundle getOverrideGroupKeys()
  {
    return this.mOverrideGroupKeys;
  }
  
  public Bundle getSuppressedVisualEffects()
  {
    return this.mSuppressedVisualEffects;
  }
  
  public Bundle getVisibilityOverrides()
  {
    return this.mVisibilityOverrides;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeStringArray(this.mKeys);
    paramParcel.writeStringArray(this.mInterceptedKeys);
    paramParcel.writeBundle(this.mVisibilityOverrides);
    paramParcel.writeBundle(this.mSuppressedVisualEffects);
    paramParcel.writeIntArray(this.mImportance);
    paramParcel.writeBundle(this.mImportanceExplanation);
    paramParcel.writeBundle(this.mOverrideGroupKeys);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/notification/NotificationRankingUpdate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */