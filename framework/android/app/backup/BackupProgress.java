package android.app.backup;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class BackupProgress
  implements Parcelable
{
  public static final Parcelable.Creator<BackupProgress> CREATOR = new Parcelable.Creator()
  {
    public BackupProgress createFromParcel(Parcel paramAnonymousParcel)
    {
      return new BackupProgress(paramAnonymousParcel, null);
    }
    
    public BackupProgress[] newArray(int paramAnonymousInt)
    {
      return new BackupProgress[paramAnonymousInt];
    }
  };
  public final long bytesExpected;
  public final long bytesTransferred;
  
  public BackupProgress(long paramLong1, long paramLong2)
  {
    this.bytesExpected = paramLong1;
    this.bytesTransferred = paramLong2;
  }
  
  private BackupProgress(Parcel paramParcel)
  {
    this.bytesExpected = paramParcel.readLong();
    this.bytesTransferred = paramParcel.readLong();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeLong(this.bytesExpected);
    paramParcel.writeLong(this.bytesTransferred);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/backup/BackupProgress.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */