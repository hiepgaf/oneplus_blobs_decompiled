package com.android.server.updates;

public class CarrierProvisioningUrlsInstallReceiver
  extends ConfigUpdateInstallReceiver
{
  public CarrierProvisioningUrlsInstallReceiver()
  {
    super("/data/misc/radio/", "provisioning_urls.xml", "metadata/", "version");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/updates/CarrierProvisioningUrlsInstallReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */