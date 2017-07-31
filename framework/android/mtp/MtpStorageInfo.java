package android.mtp;

public final class MtpStorageInfo
{
  private String mDescription;
  private long mFreeSpace;
  private long mMaxCapacity;
  private int mStorageId;
  private String mVolumeIdentifier;
  
  public final String getDescription()
  {
    return this.mDescription;
  }
  
  public final long getFreeSpace()
  {
    return this.mFreeSpace;
  }
  
  public final long getMaxCapacity()
  {
    return this.mMaxCapacity;
  }
  
  public final int getStorageId()
  {
    return this.mStorageId;
  }
  
  public final String getVolumeIdentifier()
  {
    return this.mVolumeIdentifier;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/mtp/MtpStorageInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */