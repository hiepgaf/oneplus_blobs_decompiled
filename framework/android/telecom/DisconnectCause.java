package android.telecom;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.util.Objects;

public final class DisconnectCause
  implements Parcelable
{
  public static final int ANSWERED_ELSEWHERE = 11;
  public static final int BUSY = 7;
  public static final int CALL_PULLED = 12;
  public static final int CANCELED = 4;
  public static final int CONNECTION_MANAGER_NOT_SUPPORTED = 10;
  public static final Parcelable.Creator<DisconnectCause> CREATOR = new Parcelable.Creator()
  {
    public DisconnectCause createFromParcel(Parcel paramAnonymousParcel)
    {
      return new DisconnectCause(paramAnonymousParcel.readInt(), (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramAnonymousParcel), (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramAnonymousParcel), paramAnonymousParcel.readString(), paramAnonymousParcel.readInt());
    }
    
    public DisconnectCause[] newArray(int paramAnonymousInt)
    {
      return new DisconnectCause[paramAnonymousInt];
    }
  };
  public static final int ERROR = 1;
  public static final int LOCAL = 2;
  public static final int MISSED = 5;
  public static final int OTHER = 9;
  public static final int REJECTED = 6;
  public static final int REMOTE = 3;
  public static final int RESTRICTED = 8;
  public static final int UNKNOWN = 0;
  private int mDisconnectCode;
  private CharSequence mDisconnectDescription;
  private CharSequence mDisconnectLabel;
  private String mDisconnectReason;
  private int mToneToPlay;
  
  public DisconnectCause(int paramInt)
  {
    this(paramInt, null, null, null, -1);
  }
  
  public DisconnectCause(int paramInt, CharSequence paramCharSequence1, CharSequence paramCharSequence2, String paramString)
  {
    this(paramInt, paramCharSequence1, paramCharSequence2, paramString, -1);
  }
  
  public DisconnectCause(int paramInt1, CharSequence paramCharSequence1, CharSequence paramCharSequence2, String paramString, int paramInt2)
  {
    this.mDisconnectCode = paramInt1;
    this.mDisconnectLabel = paramCharSequence1;
    this.mDisconnectDescription = paramCharSequence2;
    this.mDisconnectReason = paramString;
    this.mToneToPlay = paramInt2;
  }
  
  public DisconnectCause(int paramInt, String paramString)
  {
    this(paramInt, null, null, paramString, -1);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    if ((paramObject instanceof DisconnectCause))
    {
      paramObject = (DisconnectCause)paramObject;
      boolean bool1 = bool2;
      if (Objects.equals(Integer.valueOf(this.mDisconnectCode), Integer.valueOf(((DisconnectCause)paramObject).getCode())))
      {
        bool1 = bool2;
        if (Objects.equals(this.mDisconnectLabel, ((DisconnectCause)paramObject).getLabel()))
        {
          bool1 = bool2;
          if (Objects.equals(this.mDisconnectDescription, ((DisconnectCause)paramObject).getDescription()))
          {
            bool1 = bool2;
            if (Objects.equals(this.mDisconnectReason, ((DisconnectCause)paramObject).getReason())) {
              bool1 = Objects.equals(Integer.valueOf(this.mToneToPlay), Integer.valueOf(((DisconnectCause)paramObject).getTone()));
            }
          }
        }
      }
      return bool1;
    }
    return false;
  }
  
  public int getCode()
  {
    return this.mDisconnectCode;
  }
  
  public CharSequence getDescription()
  {
    return this.mDisconnectDescription;
  }
  
  public CharSequence getLabel()
  {
    return this.mDisconnectLabel;
  }
  
  public String getReason()
  {
    return this.mDisconnectReason;
  }
  
  public int getTone()
  {
    return this.mToneToPlay;
  }
  
  public int hashCode()
  {
    return Objects.hashCode(Integer.valueOf(this.mDisconnectCode)) + Objects.hashCode(this.mDisconnectLabel) + Objects.hashCode(this.mDisconnectDescription) + Objects.hashCode(this.mDisconnectReason) + Objects.hashCode(Integer.valueOf(this.mToneToPlay));
  }
  
  public String toString()
  {
    String str1;
    String str2;
    label105:
    String str3;
    switch (this.mDisconnectCode)
    {
    default: 
      str1 = "invalid code: " + this.mDisconnectCode;
      if (this.mDisconnectLabel == null)
      {
        str2 = "";
        if (this.mDisconnectDescription != null) {
          break label302;
        }
        str3 = "";
        label115:
        if (this.mDisconnectReason != null) {
          break label315;
        }
      }
      break;
    }
    label302:
    label315:
    for (String str4 = "";; str4 = this.mDisconnectReason)
    {
      return "DisconnectCause [ Code: (" + str1 + ")" + " Label: (" + str2 + ")" + " Description: (" + str3 + ")" + " Reason: (" + str4 + ")" + " Tone: (" + this.mToneToPlay + ") ]";
      str1 = "UNKNOWN";
      break;
      str1 = "ERROR";
      break;
      str1 = "LOCAL";
      break;
      str1 = "REMOTE";
      break;
      str1 = "CANCELED";
      break;
      str1 = "MISSED";
      break;
      str1 = "REJECTED";
      break;
      str1 = "BUSY";
      break;
      str1 = "RESTRICTED";
      break;
      str1 = "OTHER";
      break;
      str1 = "CONNECTION_MANAGER_NOT_SUPPORTED";
      break;
      str1 = "CALL_PULLED";
      break;
      str1 = "ANSWERED_ELSEWHERE";
      break;
      str2 = this.mDisconnectLabel.toString();
      break label105;
      str3 = this.mDisconnectDescription.toString();
      break label115;
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mDisconnectCode);
    TextUtils.writeToParcel(this.mDisconnectLabel, paramParcel, paramInt);
    TextUtils.writeToParcel(this.mDisconnectDescription, paramParcel, paramInt);
    paramParcel.writeString(this.mDisconnectReason);
    paramParcel.writeInt(this.mToneToPlay);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/telecom/DisconnectCause.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */