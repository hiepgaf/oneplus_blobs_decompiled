package com.android.server.webkit;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.webkit.WebViewProviderInfo;

public abstract interface SystemInterface
{
  public abstract void enableFallbackLogic(boolean paramBoolean);
  
  public abstract void enablePackageForAllUsers(Context paramContext, String paramString, boolean paramBoolean);
  
  public abstract void enablePackageForUser(String paramString, boolean paramBoolean, int paramInt);
  
  public abstract int getFactoryPackageVersion(String paramString)
    throws PackageManager.NameNotFoundException;
  
  public abstract PackageInfo getPackageInfoForProvider(WebViewProviderInfo paramWebViewProviderInfo)
    throws PackageManager.NameNotFoundException;
  
  public abstract String getUserChosenWebViewProvider(Context paramContext);
  
  public abstract WebViewProviderInfo[] getWebViewPackages();
  
  public abstract boolean isFallbackLogicEnabled();
  
  public abstract void killPackageDependents(String paramString);
  
  public abstract int onWebViewProviderChanged(PackageInfo paramPackageInfo);
  
  public abstract boolean systemIsDebuggable();
  
  public abstract void uninstallAndDisablePackageForAllUsers(Context paramContext, String paramString);
  
  public abstract void updateUserSetting(Context paramContext, String paramString);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/webkit/SystemInterface.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */