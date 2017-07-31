package com.android.settings;

import android.os.Bundle;

public abstract class OptionsMenuFragment
  extends InstrumentedFragment
{
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    setHasOptionsMenu(true);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/settings/OptionsMenuFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */