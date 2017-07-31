package android.hardware.camera2.impl;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class CaptureResultExtras
  implements Parcelable
{
  public static final Parcelable.Creator<CaptureResultExtras> CREATOR = new Parcelable.Creator()
  {
    public CaptureResultExtras createFromParcel(Parcel paramAnonymousParcel)
    {
      return new CaptureResultExtras(paramAnonymousParcel, null);
    }
    
    public CaptureResultExtras[] newArray(int paramAnonymousInt)
    {
      return new CaptureResultExtras[paramAnonymousInt];
    }
  };
  private int afTriggerId;
  private int errorStreamId;
  private long frameNumber;
  private int partialResultCount;
  private int precaptureTriggerId;
  private int requestId;
  private int subsequenceId;
  
  public CaptureResultExtras(int paramInt1, int paramInt2, int paramInt3, int paramInt4, long paramLong, int paramInt5, int paramInt6)
  {
    this.requestId = paramInt1;
    this.subsequenceId = paramInt2;
    this.afTriggerId = paramInt3;
    this.precaptureTriggerId = paramInt4;
    this.frameNumber = paramLong;
    this.partialResultCount = paramInt5;
    this.errorStreamId = paramInt6;
  }
  
  private CaptureResultExtras(Parcel paramParcel)
  {
    readFromParcel(paramParcel);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getAfTriggerId()
  {
    return this.afTriggerId;
  }
  
  public int getErrorStreamId()
  {
    return this.errorStreamId;
  }
  
  public long getFrameNumber()
  {
    return this.frameNumber;
  }
  
  public int getPartialResultCount()
  {
    return this.partialResultCount;
  }
  
  public int getPrecaptureTriggerId()
  {
    return this.precaptureTriggerId;
  }
  
  public int getRequestId()
  {
    return this.requestId;
  }
  
  public int getSubsequenceId()
  {
    return this.subsequenceId;
  }
  
  public void readFromParcel(Parcel paramParcel)
  {
    this.requestId = paramParcel.readInt();
    this.subsequenceId = paramParcel.readInt();
    this.afTriggerId = paramParcel.readInt();
    this.precaptureTriggerId = paramParcel.readInt();
    this.frameNumber = paramParcel.readLong();
    this.partialResultCount = paramParcel.readInt();
    this.errorStreamId = paramParcel.readInt();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.requestId);
    paramParcel.writeInt(this.subsequenceId);
    paramParcel.writeInt(this.afTriggerId);
    paramParcel.writeInt(this.precaptureTriggerId);
    paramParcel.writeLong(this.frameNumber);
    paramParcel.writeInt(this.partialResultCount);
    paramParcel.writeInt(this.errorStreamId);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/impl/CaptureResultExtras.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */