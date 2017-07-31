package android.content;

import android.content.res.Configuration;

public abstract interface ComponentCallbacks
{
  public abstract void onConfigurationChanged(Configuration paramConfiguration);
  
  public abstract void onLowMemory();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/ComponentCallbacks.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */