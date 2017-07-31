package com.android.server.firewall;

import android.content.ComponentName;
import android.content.Intent;

abstract interface Filter
{
  public abstract boolean matches(IntentFirewall paramIntentFirewall, ComponentName paramComponentName, Intent paramIntent, int paramInt1, int paramInt2, String paramString, int paramInt3);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/firewall/Filter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */