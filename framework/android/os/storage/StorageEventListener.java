package android.os.storage;

public class StorageEventListener
{
  public void onDiskDestroyed(DiskInfo paramDiskInfo) {}
  
  public void onDiskScanned(DiskInfo paramDiskInfo, int paramInt) {}
  
  public void onStorageStateChanged(String paramString1, String paramString2, String paramString3) {}
  
  public void onUsbMassStorageConnectionChanged(boolean paramBoolean) {}
  
  public void onVolumeForgotten(String paramString) {}
  
  public void onVolumeRecordChanged(VolumeRecord paramVolumeRecord) {}
  
  public void onVolumeStateChanged(VolumeInfo paramVolumeInfo, int paramInt1, int paramInt2) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/storage/StorageEventListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */