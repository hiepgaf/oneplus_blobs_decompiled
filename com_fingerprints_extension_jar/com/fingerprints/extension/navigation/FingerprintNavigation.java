package com.fingerprints.extension.navigation;

import android.os.RemoteException;
import com.fingerprints.extension.common.FingerprintExtensionBase;
import com.fingerprints.extension.util.Logger;

public class FingerprintNavigation
  extends FingerprintExtensionBase
{
  private static final String NAVIGATION = "com.fingerprints.extension.navigation.IFingerprintNavigation";
  private IFingerprintNavigation mFingerprintNavigation;
  private Logger mLogger = new Logger(getClass().getSimpleName());
  
  public FingerprintNavigation()
    throws RemoteException
  {
    this.mLogger.enter("FingerprintNavigation");
    this.mFingerprintNavigation = IFingerprintNavigation.Stub.asInterface(getFingerprintExtension("com.fingerprints.extension.navigation.IFingerprintNavigation"));
    if (this.mFingerprintNavigation == null) {
      throw new RemoteException("Could not get com.fingerprints.extension.navigation.IFingerprintNavigation");
    }
    this.mLogger.exit("FingerprintNavigation");
  }
  
  public NavigationConfig getNavigationConfig()
  {
    this.mLogger.enter("getNavigationConfig");
    if (this.mFingerprintNavigation != null) {
      try
      {
        NavigationConfig localNavigationConfig = this.mFingerprintNavigation.getNavigationConfig();
        return localNavigationConfig;
      }
      catch (RemoteException localRemoteException)
      {
        this.mLogger.e("RemoteException: ", localRemoteException);
      }
    }
    this.mLogger.exit("getNavigationConfig");
    return null;
  }
  
  public boolean isEnabled()
  {
    this.mLogger.enter("isEnabled");
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mFingerprintNavigation != null) {}
    try
    {
      bool1 = this.mFingerprintNavigation.isEnabled();
      this.mLogger.exit("isEnabled");
      return bool1;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        this.mLogger.e("RemoteException: ", localRemoteException);
        bool1 = bool2;
      }
    }
  }
  
  public void setNavigation(boolean paramBoolean)
  {
    this.mLogger.enter("setNavigation");
    if (this.mFingerprintNavigation != null) {}
    try
    {
      this.mFingerprintNavigation.setNavigation(paramBoolean);
      this.mLogger.exit("setNavigation");
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        this.mLogger.e("RemoteException: ", localRemoteException);
      }
    }
  }
  
  public void setNavigationConfig(NavigationConfig paramNavigationConfig)
  {
    this.mLogger.enter("setNavigationConfig");
    if (this.mFingerprintNavigation != null) {}
    try
    {
      this.mFingerprintNavigation.setNavigationConfig(paramNavigationConfig);
      this.mLogger.exit("setNavigationConfig");
      return;
    }
    catch (RemoteException paramNavigationConfig)
    {
      for (;;)
      {
        this.mLogger.e("RemoteException: ", paramNavigationConfig);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/fingerprints/extension/navigation/FingerprintNavigation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */