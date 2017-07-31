package com.android.server.updates;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Telephony.Carriers;

public class ApnDbInstallReceiver
  extends ConfigUpdateInstallReceiver
{
  private static final Uri UPDATE_APN_DB = Uri.withAppendedPath(Telephony.Carriers.CONTENT_URI, "update_db");
  
  public ApnDbInstallReceiver()
  {
    super("/data/misc/", "apns-conf.xml", "metadata/", "version");
  }
  
  protected void postInstall(Context paramContext, Intent paramIntent)
  {
    paramContext.getContentResolver().delete(UPDATE_APN_DB, null, null);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/updates/ApnDbInstallReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */