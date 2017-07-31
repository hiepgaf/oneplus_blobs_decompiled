package android.os.storage;

public abstract class MountServiceListener
{
  void onStorageStateChange(String paramString1, String paramString2, String paramString3) {}
  
  void onUsbMassStorageConnectionChanged(boolean paramBoolean) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/storage/MountServiceListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */