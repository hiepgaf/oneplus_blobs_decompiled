package com.android.server.updates;

import com.android.server.firewall.IntentFirewall;
import java.io.File;

public class IntentFirewallInstallReceiver
  extends ConfigUpdateInstallReceiver
{
  public IntentFirewallInstallReceiver()
  {
    super(IntentFirewall.getRulesDir().getAbsolutePath(), "ifw.xml", "metadata/", "gservices.version");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/updates/IntentFirewallInstallReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */