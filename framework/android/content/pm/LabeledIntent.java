package android.content.pm;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;

public class LabeledIntent
  extends Intent
{
  public static final Parcelable.Creator<LabeledIntent> CREATOR = new Parcelable.Creator()
  {
    public LabeledIntent createFromParcel(Parcel paramAnonymousParcel)
    {
      return new LabeledIntent(paramAnonymousParcel);
    }
    
    public LabeledIntent[] newArray(int paramAnonymousInt)
    {
      return new LabeledIntent[paramAnonymousInt];
    }
  };
  private int mIcon;
  private int mLabelRes;
  private CharSequence mNonLocalizedLabel;
  private String mSourcePackage;
  
  public LabeledIntent(Intent paramIntent, String paramString, int paramInt1, int paramInt2)
  {
    super(paramIntent);
    this.mSourcePackage = paramString;
    this.mLabelRes = paramInt1;
    this.mNonLocalizedLabel = null;
    this.mIcon = paramInt2;
  }
  
  public LabeledIntent(Intent paramIntent, String paramString, CharSequence paramCharSequence, int paramInt)
  {
    super(paramIntent);
    this.mSourcePackage = paramString;
    this.mLabelRes = 0;
    this.mNonLocalizedLabel = paramCharSequence;
    this.mIcon = paramInt;
  }
  
  protected LabeledIntent(Parcel paramParcel)
  {
    readFromParcel(paramParcel);
  }
  
  public LabeledIntent(String paramString, int paramInt1, int paramInt2)
  {
    this.mSourcePackage = paramString;
    this.mLabelRes = paramInt1;
    this.mNonLocalizedLabel = null;
    this.mIcon = paramInt2;
  }
  
  public LabeledIntent(String paramString, CharSequence paramCharSequence, int paramInt)
  {
    this.mSourcePackage = paramString;
    this.mLabelRes = 0;
    this.mNonLocalizedLabel = paramCharSequence;
    this.mIcon = paramInt;
  }
  
  public int getIconResource()
  {
    return this.mIcon;
  }
  
  public int getLabelResource()
  {
    return this.mLabelRes;
  }
  
  public CharSequence getNonLocalizedLabel()
  {
    return this.mNonLocalizedLabel;
  }
  
  public String getSourcePackage()
  {
    return this.mSourcePackage;
  }
  
  public Drawable loadIcon(PackageManager paramPackageManager)
  {
    if ((this.mIcon != 0) && (this.mSourcePackage != null))
    {
      paramPackageManager = paramPackageManager.getDrawable(this.mSourcePackage, this.mIcon, null);
      if (paramPackageManager != null) {
        return paramPackageManager;
      }
    }
    return null;
  }
  
  public CharSequence loadLabel(PackageManager paramPackageManager)
  {
    if (this.mNonLocalizedLabel != null) {
      return this.mNonLocalizedLabel;
    }
    if ((this.mLabelRes != 0) && (this.mSourcePackage != null))
    {
      paramPackageManager = paramPackageManager.getText(this.mSourcePackage, this.mLabelRes, null);
      if (paramPackageManager != null) {
        return paramPackageManager;
      }
    }
    return null;
  }
  
  public void readFromParcel(Parcel paramParcel)
  {
    super.readFromParcel(paramParcel);
    this.mSourcePackage = paramParcel.readString();
    this.mLabelRes = paramParcel.readInt();
    this.mNonLocalizedLabel = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
    this.mIcon = paramParcel.readInt();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    super.writeToParcel(paramParcel, paramInt);
    paramParcel.writeString(this.mSourcePackage);
    paramParcel.writeInt(this.mLabelRes);
    TextUtils.writeToParcel(this.mNonLocalizedLabel, paramParcel, paramInt);
    paramParcel.writeInt(this.mIcon);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/LabeledIntent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */