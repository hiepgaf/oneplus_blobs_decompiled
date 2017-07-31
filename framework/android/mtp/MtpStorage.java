package android.mtp;

import android.content.Context;
import android.os.storage.StorageVolume;

public class MtpStorage
{
  private final String mDescription;
  private final long mMaxFileSize;
  private final String mPath;
  private final boolean mRemovable;
  private final long mReserveSpace;
  private final int mStorageId;
  
  public MtpStorage(StorageVolume paramStorageVolume, Context paramContext)
  {
    this.mStorageId = paramStorageVolume.getStorageId();
    this.mPath = paramStorageVolume.getPath();
    this.mDescription = paramStorageVolume.getDescription(paramContext);
    this.mReserveSpace = (paramStorageVolume.getMtpReserveSpace() * 1024L * 1024L);
    this.mRemovable = paramStorageVolume.isRemovable();
    this.mMaxFileSize = paramStorageVolume.getMaxFileSize();
  }
  
  public final String getDescription()
  {
    return this.mDescription;
  }
  
  public long getMaxFileSize()
  {
    return this.mMaxFileSize;
  }
  
  public final String getPath()
  {
    return this.mPath;
  }
  
  public final long getReserveSpace()
  {
    return this.mReserveSpace;
  }
  
  public final int getStorageId()
  {
    return this.mStorageId;
  }
  
  public final boolean isRemovable()
  {
    return this.mRemovable;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/mtp/MtpStorage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */