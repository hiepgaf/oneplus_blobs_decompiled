package android.accounts;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class AuthenticatorDescription
  implements Parcelable
{
  public static final Parcelable.Creator<AuthenticatorDescription> CREATOR = new Parcelable.Creator()
  {
    public AuthenticatorDescription createFromParcel(Parcel paramAnonymousParcel)
    {
      return new AuthenticatorDescription(paramAnonymousParcel, null);
    }
    
    public AuthenticatorDescription[] newArray(int paramAnonymousInt)
    {
      return new AuthenticatorDescription[paramAnonymousInt];
    }
  };
  public final int accountPreferencesId;
  public final boolean customTokens;
  public final int iconId;
  public final int labelId;
  public final String packageName;
  public final int smallIconId;
  public final String type;
  
  private AuthenticatorDescription(Parcel paramParcel)
  {
    this.type = paramParcel.readString();
    this.packageName = paramParcel.readString();
    this.labelId = paramParcel.readInt();
    this.iconId = paramParcel.readInt();
    this.smallIconId = paramParcel.readInt();
    this.accountPreferencesId = paramParcel.readInt();
    if (paramParcel.readByte() == 1) {}
    for (;;)
    {
      this.customTokens = bool;
      return;
      bool = false;
    }
  }
  
  private AuthenticatorDescription(String paramString)
  {
    this.type = paramString;
    this.packageName = null;
    this.labelId = 0;
    this.iconId = 0;
    this.smallIconId = 0;
    this.accountPreferencesId = 0;
    this.customTokens = false;
  }
  
  public AuthenticatorDescription(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this(paramString1, paramString2, paramInt1, paramInt2, paramInt3, paramInt4, false);
  }
  
  public AuthenticatorDescription(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
  {
    if (paramString1 == null) {
      throw new IllegalArgumentException("type cannot be null");
    }
    if (paramString2 == null) {
      throw new IllegalArgumentException("packageName cannot be null");
    }
    this.type = paramString1;
    this.packageName = paramString2;
    this.labelId = paramInt1;
    this.iconId = paramInt2;
    this.smallIconId = paramInt3;
    this.accountPreferencesId = paramInt4;
    this.customTokens = paramBoolean;
  }
  
  public static AuthenticatorDescription newKey(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("type cannot be null");
    }
    return new AuthenticatorDescription(paramString);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof AuthenticatorDescription)) {
      return false;
    }
    paramObject = (AuthenticatorDescription)paramObject;
    return this.type.equals(((AuthenticatorDescription)paramObject).type);
  }
  
  public int hashCode()
  {
    return this.type.hashCode();
  }
  
  public String toString()
  {
    return "AuthenticatorDescription {type=" + this.type + "}";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.type);
    paramParcel.writeString(this.packageName);
    paramParcel.writeInt(this.labelId);
    paramParcel.writeInt(this.iconId);
    paramParcel.writeInt(this.smallIconId);
    paramParcel.writeInt(this.accountPreferencesId);
    if (this.customTokens) {}
    for (paramInt = 1;; paramInt = 0)
    {
      paramParcel.writeByte((byte)paramInt);
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/accounts/AuthenticatorDescription.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */