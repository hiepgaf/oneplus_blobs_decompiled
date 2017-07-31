package com.oneplus.camera.ui.menu;

import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.Settings;

public class BooleanSettingsMenuItem
  extends MenuItem
{
  private final String m_Key;
  private final Settings m_Settings;
  
  public BooleanSettingsMenuItem(Settings paramSettings, String paramString)
  {
    this.m_Settings = paramSettings;
    this.m_Key = paramString;
    addCallback(PROP_IS_CHECKED, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        BooleanSettingsMenuItem.-get1(BooleanSettingsMenuItem.this).set(BooleanSettingsMenuItem.-get0(BooleanSettingsMenuItem.this), paramAnonymousPropertyChangeEventArgs.getNewValue());
      }
    });
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/menu/BooleanSettingsMenuItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */