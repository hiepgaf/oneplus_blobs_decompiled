package com.fingerprints.extension.sensortest;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.fingerprints.extension.util.Logger;

public class SensorTestInput
  implements Parcelable
{
  public static final Parcelable.Creator<SensorTestInput> CREATOR = new Parcelable.Creator()
  {
    public SensorTestInput createFromParcel(Parcel paramAnonymousParcel)
    {
      return new SensorTestInput(paramAnonymousParcel, null);
    }
    
    public SensorTestInput[] newArray(int paramAnonymousInt)
    {
      return new SensorTestInput[paramAnonymousInt];
    }
  };
  private Logger mLogger = new Logger(getClass().getSimpleName());
  public String testLimitsKeyValuePair;
  
  private SensorTestInput(Parcel paramParcel)
  {
    this.mLogger.enter("SensorTestInput");
    try
    {
      this.testLimitsKeyValuePair = paramParcel.readString();
      this.mLogger.d("testLimitsKeyValuePair: " + this.testLimitsKeyValuePair);
      this.mLogger.exit("SensorTestInput");
      return;
    }
    catch (Exception paramParcel)
    {
      for (;;)
      {
        this.mLogger.e("Exception: " + paramParcel);
      }
    }
  }
  
  public SensorTestInput(String paramString)
  {
    this.testLimitsKeyValuePair = paramString;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    this.mLogger.enter("writeToParcel");
    this.mLogger.d("testLimitsKeyValuePair: " + this.testLimitsKeyValuePair);
    paramParcel.writeString(this.testLimitsKeyValuePair);
    this.mLogger.exit("writeToParcel");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/fingerprints/extension/sensortest/SensorTestInput.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */