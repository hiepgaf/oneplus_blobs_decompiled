package com.oneplus.io;

import com.oneplus.base.PropertyKey;
import com.oneplus.base.component.Component;
import java.util.Collections;
import java.util.List;

public abstract interface StorageManager
  extends Component
{
  public static final PropertyKey<List<Storage>> PROP_STORAGE_LIST = new PropertyKey("StorageList", List.class, StorageManager.class, Collections.EMPTY_LIST);
  public static final String SETTINGS_KEY_STORAGE_TYPE = "StorageType";
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/io/StorageManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */