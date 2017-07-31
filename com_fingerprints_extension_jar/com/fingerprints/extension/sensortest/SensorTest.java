package com.fingerprints.extension.sensortest;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.fingerprints.extension.util.Logger;

public class SensorTest
  implements Parcelable
{
  public static final Parcelable.Creator<SensorTest> CREATOR = new Parcelable.Creator()
  {
    public SensorTest createFromParcel(Parcel paramAnonymousParcel)
    {
      return new SensorTest(paramAnonymousParcel, null);
    }
    
    public SensorTest[] newArray(int paramAnonymousInt)
    {
      return new SensorTest[paramAnonymousInt];
    }
  };
  public String description;
  private Logger mLogger = new Logger(getClass().getSimpleName());
  public String name;
  public String rubberStampType;
  public boolean waitForFingerDown;
  
  private SensorTest(Parcel paramParcel)
  {
    this.mLogger.enter("SensorTest");
    try
    {
      this.name = paramParcel.readString();
      this.description = paramParcel.readString();
      if (paramParcel.readInt() != 0) {
        bool = true;
      }
      this.waitForFingerDown = bool;
      this.rubberStampType = paramParcel.readString();
      this.mLogger.d("name: " + this.name);
      this.mLogger.d("description: " + this.description);
      this.mLogger.d("waitForFingerDown: " + this.waitForFingerDown);
      this.mLogger.d("rubberStampType: " + this.rubberStampType);
    }
    catch (Exception paramParcel)
    {
      for (;;)
      {
        this.mLogger.e("Exception: " + paramParcel);
      }
    }
    this.mLogger.exit("SensorTest");
  }
  
  public SensorTest(String paramString1, String paramString2, boolean paramBoolean, String paramString3)
  {
    this.mLogger.enter("SensorTest");
    this.name = paramString1;
    this.description = paramString2;
    this.waitForFingerDown = paramBoolean;
    this.rubberStampType = paramString3;
    this.mLogger.exit("SensorTest");
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    this.mLogger.enter("writeToParcel");
    this.mLogger.d("name: " + this.name);
    this.mLogger.d("description: " + this.description);
    this.mLogger.d("waitForFingerDown: " + this.waitForFingerDown);
    this.mLogger.d("rubberStampType: " + this.rubberStampType);
    paramParcel.writeString(this.name);
    paramParcel.writeString(this.description);
    if (this.waitForFingerDown) {}
    for (paramInt = 1;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      paramParcel.writeString(this.rubberStampType);
      this.mLogger.exit("writeToParcel");
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/fingerprints/extension/sensortest/SensorTest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */