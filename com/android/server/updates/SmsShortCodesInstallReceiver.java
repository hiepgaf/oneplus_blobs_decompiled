package com.android.server.updates;

public class SmsShortCodesInstallReceiver
  extends ConfigUpdateInstallReceiver
{
  public SmsShortCodesInstallReceiver()
  {
    super("/data/misc/sms/", "codes", "metadata/", "version");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/updates/SmsShortCodesInstallReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */