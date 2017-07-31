package com.android.server.pm;

import android.content.ComponentName;
import com.android.server.IntentResolver;
import java.io.PrintWriter;

public class PreferredIntentResolver
  extends IntentResolver<PreferredActivity, PreferredActivity>
{
  protected void dumpFilter(PrintWriter paramPrintWriter, String paramString, PreferredActivity paramPreferredActivity)
  {
    paramPreferredActivity.mPref.dump(paramPrintWriter, paramString, paramPreferredActivity);
  }
  
  protected boolean isPackageForFilter(String paramString, PreferredActivity paramPreferredActivity)
  {
    return paramString.equals(paramPreferredActivity.mPref.mComponent.getPackageName());
  }
  
  protected PreferredActivity[] newArray(int paramInt)
  {
    return new PreferredActivity[paramInt];
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/PreferredIntentResolver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */