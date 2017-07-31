package com.android.server.pm;

import android.content.ComponentName;
import com.android.server.IntentResolver;

public class PersistentPreferredIntentResolver
  extends IntentResolver<PersistentPreferredActivity, PersistentPreferredActivity>
{
  protected boolean isPackageForFilter(String paramString, PersistentPreferredActivity paramPersistentPreferredActivity)
  {
    return paramString.equals(paramPersistentPreferredActivity.mComponent.getPackageName());
  }
  
  protected PersistentPreferredActivity[] newArray(int paramInt)
  {
    return new PersistentPreferredActivity[paramInt];
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/PersistentPreferredIntentResolver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */