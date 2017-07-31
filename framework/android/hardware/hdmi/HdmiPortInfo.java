package android.hardware.hdmi;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class HdmiPortInfo
  implements Parcelable
{
  public static final Parcelable.Creator<HdmiPortInfo> CREATOR = new Parcelable.Creator()
  {
    public HdmiPortInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      int i = paramAnonymousParcel.readInt();
      int j = paramAnonymousParcel.readInt();
      int k = paramAnonymousParcel.readInt();
      boolean bool1;
      boolean bool2;
      if (paramAnonymousParcel.readInt() == 1)
      {
        bool1 = true;
        if (paramAnonymousParcel.readInt() != 1) {
          break label73;
        }
        bool2 = true;
        label38:
        if (paramAnonymousParcel.readInt() != 1) {
          break label79;
        }
      }
      label73:
      label79:
      for (boolean bool3 = true;; bool3 = false)
      {
        return new HdmiPortInfo(i, j, k, bool1, bool3, bool2);
        bool1 = false;
        break;
        bool2 = false;
        break label38;
      }
    }
    
    public HdmiPortInfo[] newArray(int paramAnonymousInt)
    {
      return new HdmiPortInfo[paramAnonymousInt];
    }
  };
  public static final int PORT_INPUT = 0;
  public static final int PORT_OUTPUT = 1;
  private final int mAddress;
  private final boolean mArcSupported;
  private final boolean mCecSupported;
  private final int mId;
  private final boolean mMhlSupported;
  private final int mType;
  
  public HdmiPortInfo(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    this.mId = paramInt1;
    this.mType = paramInt2;
    this.mAddress = paramInt3;
    this.mCecSupported = paramBoolean1;
    this.mArcSupported = paramBoolean3;
    this.mMhlSupported = paramBoolean2;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    if (!(paramObject instanceof HdmiPortInfo)) {
      return false;
    }
    boolean bool1 = bool2;
    if (this.mId == ((HdmiPortInfo)paramObject).mId)
    {
      bool1 = bool2;
      if (this.mType == ((HdmiPortInfo)paramObject).mType)
      {
        bool1 = bool2;
        if (this.mAddress == ((HdmiPortInfo)paramObject).mAddress)
        {
          bool1 = bool2;
          if (this.mCecSupported == ((HdmiPortInfo)paramObject).mCecSupported)
          {
            bool1 = bool2;
            if (this.mArcSupported == ((HdmiPortInfo)paramObject).mArcSupported)
            {
              bool1 = bool2;
              if (this.mMhlSupported == ((HdmiPortInfo)paramObject).mMhlSupported) {
                bool1 = true;
              }
            }
          }
        }
      }
    }
    return bool1;
  }
  
  public int getAddress()
  {
    return this.mAddress;
  }
  
  public int getId()
  {
    return this.mId;
  }
  
  public int getType()
  {
    return this.mType;
  }
  
  public boolean isArcSupported()
  {
    return this.mArcSupported;
  }
  
  public boolean isCecSupported()
  {
    return this.mCecSupported;
  }
  
  public boolean isMhlSupported()
  {
    return this.mMhlSupported;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("port_id: ").append(this.mId).append(", ");
    localStringBuffer.append("address: ").append(String.format("0x%04x", new Object[] { Integer.valueOf(this.mAddress) })).append(", ");
    localStringBuffer.append("cec: ").append(this.mCecSupported).append(", ");
    localStringBuffer.append("arc: ").append(this.mArcSupported).append(", ");
    localStringBuffer.append("mhl: ").append(this.mMhlSupported);
    return localStringBuffer.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = 1;
    paramParcel.writeInt(this.mId);
    paramParcel.writeInt(this.mType);
    paramParcel.writeInt(this.mAddress);
    if (this.mCecSupported)
    {
      paramInt = 1;
      paramParcel.writeInt(paramInt);
      if (!this.mArcSupported) {
        break label74;
      }
      paramInt = 1;
      label49:
      paramParcel.writeInt(paramInt);
      if (!this.mMhlSupported) {
        break label79;
      }
    }
    label74:
    label79:
    for (paramInt = i;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      return;
      paramInt = 0;
      break;
      paramInt = 0;
      break label49;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/hdmi/HdmiPortInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */