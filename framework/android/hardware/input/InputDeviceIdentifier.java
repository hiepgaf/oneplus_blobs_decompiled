package android.hardware.input;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.util.Objects;

public final class InputDeviceIdentifier
  implements Parcelable
{
  public static final Parcelable.Creator<InputDeviceIdentifier> CREATOR = new Parcelable.Creator()
  {
    public InputDeviceIdentifier createFromParcel(Parcel paramAnonymousParcel)
    {
      return new InputDeviceIdentifier(paramAnonymousParcel, null);
    }
    
    public InputDeviceIdentifier[] newArray(int paramAnonymousInt)
    {
      return new InputDeviceIdentifier[paramAnonymousInt];
    }
  };
  private final String mDescriptor;
  private final int mProductId;
  private final int mVendorId;
  
  private InputDeviceIdentifier(Parcel paramParcel)
  {
    this.mDescriptor = paramParcel.readString();
    this.mVendorId = paramParcel.readInt();
    this.mProductId = paramParcel.readInt();
  }
  
  public InputDeviceIdentifier(String paramString, int paramInt1, int paramInt2)
  {
    this.mDescriptor = paramString;
    this.mVendorId = paramInt1;
    this.mProductId = paramInt2;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    if (this == paramObject) {
      return true;
    }
    if ((paramObject != null) && ((paramObject instanceof InputDeviceIdentifier)))
    {
      paramObject = (InputDeviceIdentifier)paramObject;
      boolean bool1 = bool2;
      if (this.mVendorId == ((InputDeviceIdentifier)paramObject).mVendorId)
      {
        bool1 = bool2;
        if (this.mProductId == ((InputDeviceIdentifier)paramObject).mProductId) {
          bool1 = TextUtils.equals(this.mDescriptor, ((InputDeviceIdentifier)paramObject).mDescriptor);
        }
      }
      return bool1;
    }
    return false;
  }
  
  public String getDescriptor()
  {
    return this.mDescriptor;
  }
  
  public int getProductId()
  {
    return this.mProductId;
  }
  
  public int getVendorId()
  {
    return this.mVendorId;
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { this.mDescriptor, Integer.valueOf(this.mVendorId), Integer.valueOf(this.mProductId) });
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mDescriptor);
    paramParcel.writeInt(this.mVendorId);
    paramParcel.writeInt(this.mProductId);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/input/InputDeviceIdentifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */