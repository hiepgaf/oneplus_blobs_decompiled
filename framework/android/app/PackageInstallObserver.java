package android.app;

import android.content.Intent;
import android.content.pm.IPackageInstallObserver2;
import android.content.pm.IPackageInstallObserver2.Stub;
import android.os.Bundle;

public class PackageInstallObserver
{
  private final IPackageInstallObserver2.Stub mBinder = new IPackageInstallObserver2.Stub()
  {
    public void onPackageInstalled(String paramAnonymousString1, int paramAnonymousInt, String paramAnonymousString2, Bundle paramAnonymousBundle)
    {
      PackageInstallObserver.this.onPackageInstalled(paramAnonymousString1, paramAnonymousInt, paramAnonymousString2, paramAnonymousBundle);
    }
    
    public void onUserActionRequired(Intent paramAnonymousIntent)
    {
      PackageInstallObserver.this.onUserActionRequired(paramAnonymousIntent);
    }
  };
  
  public IPackageInstallObserver2 getBinder()
  {
    return this.mBinder;
  }
  
  public void onPackageInstalled(String paramString1, int paramInt, String paramString2, Bundle paramBundle) {}
  
  public void onUserActionRequired(Intent paramIntent) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/PackageInstallObserver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */