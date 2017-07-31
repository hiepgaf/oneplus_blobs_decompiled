package android.app;

import android.content.Intent;
import android.content.pm.IPackageDeleteObserver2;
import android.content.pm.IPackageDeleteObserver2.Stub;

public class PackageDeleteObserver
{
  private final IPackageDeleteObserver2.Stub mBinder = new IPackageDeleteObserver2.Stub()
  {
    public void onPackageDeleted(String paramAnonymousString1, int paramAnonymousInt, String paramAnonymousString2)
    {
      PackageDeleteObserver.this.onPackageDeleted(paramAnonymousString1, paramAnonymousInt, paramAnonymousString2);
    }
    
    public void onUserActionRequired(Intent paramAnonymousIntent)
    {
      PackageDeleteObserver.this.onUserActionRequired(paramAnonymousIntent);
    }
  };
  
  public IPackageDeleteObserver2 getBinder()
  {
    return this.mBinder;
  }
  
  public void onPackageDeleted(String paramString1, int paramInt, String paramString2) {}
  
  public void onUserActionRequired(Intent paramIntent) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/PackageDeleteObserver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */