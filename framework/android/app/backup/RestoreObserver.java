package android.app.backup;

public abstract class RestoreObserver
{
  public void onUpdate(int paramInt, String paramString) {}
  
  public void restoreFinished(int paramInt) {}
  
  public void restoreSetsAvailable(RestoreSet[] paramArrayOfRestoreSet) {}
  
  public void restoreStarting(int paramInt) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/backup/RestoreObserver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */