package com.android.settings;

import android.content.Context;
import android.content.Intent;

public class ManagedLockPasswordProvider
{
  static ManagedLockPasswordProvider get(Context paramContext, int paramInt)
  {
    return new ManagedLockPasswordProvider();
  }
  
  Intent createIntent(boolean paramBoolean, String paramString)
  {
    return null;
  }
  
  String getPickerOptionTitle(boolean paramBoolean)
  {
    return "";
  }
  
  int getResIdForLockUnlockScreen(boolean paramBoolean)
  {
    if (paramBoolean) {
      return 2131230838;
    }
    return 2131230837;
  }
  
  int getResIdForLockUnlockSubScreen()
  {
    return 2131230839;
  }
  
  boolean isManagedPasswordChoosable()
  {
    return false;
  }
  
  boolean isSettingManagedPasswordSupported()
  {
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/settings/ManagedLockPasswordProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */