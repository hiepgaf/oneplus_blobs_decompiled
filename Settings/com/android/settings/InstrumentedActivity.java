package com.android.settings;

import android.app.Activity;
import com.android.internal.logging.MetricsLogger;

public abstract class InstrumentedActivity
  extends Activity
{
  protected abstract int getMetricsCategory();
  
  public void onPause()
  {
    super.onPause();
    MetricsLogger.hidden(this, getMetricsCategory());
  }
  
  public void onResume()
  {
    super.onResume();
    MetricsLogger.visible(this, getMetricsCategory());
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/settings/InstrumentedActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */