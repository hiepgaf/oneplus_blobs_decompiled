package android.service.quicksettings;

import android.graphics.drawable.Icon;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

public final class Tile
  implements Parcelable
{
  public static final Parcelable.Creator<Tile> CREATOR = new Parcelable.Creator()
  {
    public Tile createFromParcel(Parcel paramAnonymousParcel)
    {
      return new Tile(paramAnonymousParcel);
    }
    
    public Tile[] newArray(int paramAnonymousInt)
    {
      return new Tile[paramAnonymousInt];
    }
  };
  public static final int STATE_ACTIVE = 2;
  public static final int STATE_INACTIVE = 1;
  public static final int STATE_UNAVAILABLE = 0;
  private static final String TAG = "Tile";
  private CharSequence mContentDescription;
  private Icon mIcon;
  private CharSequence mLabel;
  private IQSService mService;
  private int mState = 2;
  private IBinder mToken;
  
  public Tile() {}
  
  public Tile(Parcel paramParcel)
  {
    readFromParcel(paramParcel);
  }
  
  private void readFromParcel(Parcel paramParcel)
  {
    if (paramParcel.readByte() != 0) {}
    for (this.mIcon = ((Icon)Icon.CREATOR.createFromParcel(paramParcel));; this.mIcon = null)
    {
      this.mState = paramParcel.readInt();
      this.mLabel = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
      this.mContentDescription = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
      return;
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public CharSequence getContentDescription()
  {
    return this.mContentDescription;
  }
  
  public Icon getIcon()
  {
    return this.mIcon;
  }
  
  public CharSequence getLabel()
  {
    return this.mLabel;
  }
  
  public int getState()
  {
    return this.mState;
  }
  
  public void setContentDescription(CharSequence paramCharSequence)
  {
    this.mContentDescription = paramCharSequence;
  }
  
  public void setIcon(Icon paramIcon)
  {
    this.mIcon = paramIcon;
  }
  
  public void setLabel(CharSequence paramCharSequence)
  {
    this.mLabel = paramCharSequence;
  }
  
  public void setService(IQSService paramIQSService, IBinder paramIBinder)
  {
    this.mService = paramIQSService;
    this.mToken = paramIBinder;
  }
  
  public void setState(int paramInt)
  {
    this.mState = paramInt;
  }
  
  public void updateTile()
  {
    try
    {
      this.mService.updateQsTile(this, this.mToken);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("Tile", "Couldn't update tile");
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    if (this.mIcon != null)
    {
      paramParcel.writeByte((byte)1);
      this.mIcon.writeToParcel(paramParcel, paramInt);
    }
    for (;;)
    {
      paramParcel.writeInt(this.mState);
      TextUtils.writeToParcel(this.mLabel, paramParcel, paramInt);
      TextUtils.writeToParcel(this.mContentDescription, paramParcel, paramInt);
      return;
      paramParcel.writeByte((byte)0);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/quicksettings/Tile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */