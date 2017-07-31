package android.os.storage;

public abstract class MountServiceInternal
{
  public abstract void addExternalStoragePolicy(ExternalStorageMountPolicy paramExternalStorageMountPolicy);
  
  public abstract int getExternalStorageMountMode(int paramInt, String paramString);
  
  public abstract void onExternalStoragePolicyChanged(int paramInt, String paramString);
  
  public static abstract interface ExternalStorageMountPolicy
  {
    public abstract int getMountMode(int paramInt, String paramString);
    
    public abstract boolean hasExternalStorage(int paramInt, String paramString);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/storage/MountServiceInternal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */