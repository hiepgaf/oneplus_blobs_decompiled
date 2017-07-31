package android.service.chooser;

import android.content.ComponentName;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class ChooserTarget
  implements Parcelable
{
  public static final Parcelable.Creator<ChooserTarget> CREATOR = new Parcelable.Creator()
  {
    public ChooserTarget createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ChooserTarget(paramAnonymousParcel);
    }
    
    public ChooserTarget[] newArray(int paramAnonymousInt)
    {
      return new ChooserTarget[paramAnonymousInt];
    }
  };
  private static final String TAG = "ChooserTarget";
  private ComponentName mComponentName;
  private Icon mIcon;
  private Bundle mIntentExtras;
  private float mScore;
  private CharSequence mTitle;
  
  ChooserTarget(Parcel paramParcel)
  {
    this.mTitle = paramParcel.readCharSequence();
    if (paramParcel.readInt() != 0) {}
    for (this.mIcon = ((Icon)Icon.CREATOR.createFromParcel(paramParcel));; this.mIcon = null)
    {
      this.mScore = paramParcel.readFloat();
      this.mComponentName = ComponentName.readFromParcel(paramParcel);
      this.mIntentExtras = paramParcel.readBundle();
      return;
    }
  }
  
  public ChooserTarget(CharSequence paramCharSequence, Icon paramIcon, float paramFloat, ComponentName paramComponentName, Bundle paramBundle)
  {
    this.mTitle = paramCharSequence;
    this.mIcon = paramIcon;
    if ((paramFloat > 1.0F) || (paramFloat < 0.0F)) {
      throw new IllegalArgumentException("Score " + paramFloat + " out of range; " + "must be between 0.0f and 1.0f");
    }
    this.mScore = paramFloat;
    this.mComponentName = paramComponentName;
    this.mIntentExtras = paramBundle;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public ComponentName getComponentName()
  {
    return this.mComponentName;
  }
  
  public Icon getIcon()
  {
    return this.mIcon;
  }
  
  public Bundle getIntentExtras()
  {
    return this.mIntentExtras;
  }
  
  public float getScore()
  {
    return this.mScore;
  }
  
  public CharSequence getTitle()
  {
    return this.mTitle;
  }
  
  public String toString()
  {
    return "ChooserTarget{" + this.mComponentName + ", " + this.mIntentExtras + ", '" + this.mTitle + "', " + this.mScore + "}";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeCharSequence(this.mTitle);
    if (this.mIcon != null)
    {
      paramParcel.writeInt(1);
      this.mIcon.writeToParcel(paramParcel, 0);
    }
    for (;;)
    {
      paramParcel.writeFloat(this.mScore);
      ComponentName.writeToParcel(this.mComponentName, paramParcel);
      paramParcel.writeBundle(this.mIntentExtras);
      return;
      paramParcel.writeInt(0);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/chooser/ChooserTarget.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */