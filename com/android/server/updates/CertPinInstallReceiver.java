package com.android.server.updates;

public class CertPinInstallReceiver
  extends ConfigUpdateInstallReceiver
{
  public CertPinInstallReceiver()
  {
    super("/data/misc/keychain/", "pins", "metadata/", "version");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/updates/CertPinInstallReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */