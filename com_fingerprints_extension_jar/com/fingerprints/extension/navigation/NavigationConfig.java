package com.fingerprints.extension.navigation;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.fingerprints.extension.util.Logger;

public class NavigationConfig
  implements Parcelable
{
  public static final Parcelable.Creator<NavigationConfig> CREATOR = new Parcelable.Creator()
  {
    public NavigationConfig createFromParcel(Parcel paramAnonymousParcel)
    {
      return new NavigationConfig(paramAnonymousParcel, null);
    }
    
    public NavigationConfig[] newArray(int paramAnonymousInt)
    {
      return new NavigationConfig[paramAnonymousInt];
    }
  };
  public int doubleClickTimeInterval;
  public int fastMoveTolerance;
  public int fastSwipeDownThreshold;
  public int fastSwipeLeftThreshold;
  public int fastSwipeRightThreshold;
  public int fastSwipeUpThreshold;
  public int holdClickTimeThreshold;
  private Logger mLogger = new Logger(getClass().getSimpleName());
  public int singleClickMinTimeThreshold;
  public int slowSwipeDownThreshold;
  public int slowSwipeLeftThreshold;
  public int slowSwipeRightThreshold;
  public int slowSwipeUpThreshold;
  
  public NavigationConfig() {}
  
  private NavigationConfig(Parcel paramParcel)
  {
    this.singleClickMinTimeThreshold = paramParcel.readInt();
    this.holdClickTimeThreshold = paramParcel.readInt();
    this.doubleClickTimeInterval = paramParcel.readInt();
    this.fastMoveTolerance = paramParcel.readInt();
    this.slowSwipeUpThreshold = paramParcel.readInt();
    this.slowSwipeDownThreshold = paramParcel.readInt();
    this.slowSwipeLeftThreshold = paramParcel.readInt();
    this.slowSwipeRightThreshold = paramParcel.readInt();
    this.fastSwipeUpThreshold = paramParcel.readInt();
    this.fastSwipeDownThreshold = paramParcel.readInt();
    this.fastSwipeLeftThreshold = paramParcel.readInt();
    this.fastSwipeRightThreshold = paramParcel.readInt();
  }
  
  public NavigationConfig(NavigationConfig paramNavigationConfig)
  {
    this.singleClickMinTimeThreshold = paramNavigationConfig.singleClickMinTimeThreshold;
    this.holdClickTimeThreshold = paramNavigationConfig.holdClickTimeThreshold;
    this.doubleClickTimeInterval = paramNavigationConfig.doubleClickTimeInterval;
    this.fastMoveTolerance = paramNavigationConfig.fastMoveTolerance;
    this.slowSwipeUpThreshold = paramNavigationConfig.slowSwipeUpThreshold;
    this.slowSwipeDownThreshold = paramNavigationConfig.slowSwipeDownThreshold;
    this.slowSwipeLeftThreshold = paramNavigationConfig.slowSwipeLeftThreshold;
    this.slowSwipeRightThreshold = paramNavigationConfig.slowSwipeRightThreshold;
    this.fastSwipeUpThreshold = paramNavigationConfig.fastSwipeUpThreshold;
    this.fastSwipeDownThreshold = paramNavigationConfig.fastSwipeDownThreshold;
    this.fastSwipeLeftThreshold = paramNavigationConfig.fastSwipeLeftThreshold;
    this.fastSwipeRightThreshold = paramNavigationConfig.fastSwipeRightThreshold;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void print()
  {
    this.mLogger.d("singleClickMinTimeThreshold: " + this.singleClickMinTimeThreshold + " holdClickTimeThreshold: " + this.holdClickTimeThreshold + " doubleClickTimeInterval: " + this.doubleClickTimeInterval + " fastMoveTolerance: " + this.fastMoveTolerance + " slowSwipeUpThreshold: " + this.slowSwipeUpThreshold + " slowSwipeDownThreshold: " + this.slowSwipeDownThreshold + " slowSwipeLeftThreshold: " + this.slowSwipeLeftThreshold + " slowSwipeRightThreshold: " + this.slowSwipeRightThreshold + " fastSwipeUpThreshold: " + this.fastSwipeUpThreshold + " fastSwipeDownThreshold: " + this.fastSwipeDownThreshold + " fastSwipeLeftThreshold: " + this.fastSwipeLeftThreshold + " fastSwipeRightThreshold: " + this.fastSwipeRightThreshold);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.singleClickMinTimeThreshold);
    paramParcel.writeInt(this.holdClickTimeThreshold);
    paramParcel.writeInt(this.doubleClickTimeInterval);
    paramParcel.writeInt(this.fastMoveTolerance);
    paramParcel.writeInt(this.slowSwipeUpThreshold);
    paramParcel.writeInt(this.slowSwipeDownThreshold);
    paramParcel.writeInt(this.slowSwipeLeftThreshold);
    paramParcel.writeInt(this.slowSwipeRightThreshold);
    paramParcel.writeInt(this.fastSwipeUpThreshold);
    paramParcel.writeInt(this.fastSwipeDownThreshold);
    paramParcel.writeInt(this.fastSwipeLeftThreshold);
    paramParcel.writeInt(this.fastSwipeRightThreshold);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/fingerprints/extension/navigation/NavigationConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */