package com.oneplus.camera.ui.menu;

import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.Settings;
import com.oneplus.io.Storage;
import com.oneplus.io.Storage.Type;
import com.oneplus.io.StorageManager;
import com.oneplus.io.StorageUtils;

public class StorageMenuItem
  extends MenuItem
{
  private final String m_Key;
  private final Settings m_Settings;
  private final StorageManager m_StorageManager;
  
  public StorageMenuItem(Settings paramSettings, String paramString, StorageManager paramStorageManager)
  {
    this.m_Settings = paramSettings;
    this.m_Key = paramString;
    this.m_StorageManager = paramStorageManager;
    addCallback(PROP_IS_CHECKED, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        if ((((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()) && (StorageMenuItem.-get2(StorageMenuItem.this) != null))
        {
          paramAnonymousPropertySource = StorageUtils.findStorage(StorageMenuItem.-get2(StorageMenuItem.this), Storage.Type.SD_CARD);
          if ((paramAnonymousPropertySource != null) && (paramAnonymousPropertySource.isReady()))
          {
            StorageMenuItem.-get1(StorageMenuItem.this).set(StorageMenuItem.-get0(StorageMenuItem.this), Storage.Type.SD_CARD);
            return;
          }
        }
        if (((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()) {
          StorageMenuItem.this.set(StorageMenuItem.PROP_IS_CHECKED, Boolean.valueOf(false));
        }
        StorageMenuItem.-get1(StorageMenuItem.this).set(StorageMenuItem.-get0(StorageMenuItem.this), Storage.Type.INTERNAL);
      }
    });
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/menu/StorageMenuItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */