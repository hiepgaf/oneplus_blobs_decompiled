package com.oneplus.io;

import android.os.Environment;
import com.oneplus.base.Settings;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StorageUtils
{
  public static Storage findStorage(StorageManager paramStorageManager, Storage.Type paramType)
  {
    if (paramStorageManager != null) {
      return findStorage((List)paramStorageManager.get(StorageManager.PROP_STORAGE_LIST), paramType);
    }
    return null;
  }
  
  public static Storage findStorage(List<Storage> paramList, Storage.Type paramType)
  {
    if (paramList != null)
    {
      int i = paramList.size() - 1;
      while (i >= 0)
      {
        Storage localStorage = (Storage)paramList.get(i);
        if ((localStorage != null) && (localStorage.getType() == paramType)) {
          return localStorage;
        }
        i -= 1;
      }
    }
    return null;
  }
  
  public static Storage findStorageFromSettings(StorageManager paramStorageManager, Settings paramSettings, Storage.Type paramType)
  {
    return findStorageFromSettings(paramStorageManager, paramSettings, "StorageType", paramType);
  }
  
  public static Storage findStorageFromSettings(StorageManager paramStorageManager, Settings paramSettings, String paramString, Storage.Type paramType)
  {
    if (paramStorageManager != null) {
      return findStorageFromSettings((List)paramStorageManager.get(StorageManager.PROP_STORAGE_LIST), paramSettings, paramString, paramType);
    }
    return null;
  }
  
  public static Storage findStorageFromSettings(List<Storage> paramList, Settings paramSettings, Storage.Type paramType)
  {
    return findStorageFromSettings(paramList, paramSettings, "StorageType", paramType);
  }
  
  public static Storage findStorageFromSettings(List<Storage> paramList, Settings paramSettings, String paramString, Storage.Type paramType)
  {
    if ((paramSettings == null) || (paramString == null)) {
      return null;
    }
    paramSettings = (Storage.Type)paramSettings.getEnum(paramString, Storage.Type.class, paramType);
    if (paramSettings != null) {
      return findStorage(paramList, paramSettings);
    }
    return null;
  }
  
  public static List<String> getAllDcimPath(StorageManager paramStorageManager)
  {
    List localList = null;
    if (paramStorageManager != null) {
      localList = (List)paramStorageManager.get(StorageManager.PROP_STORAGE_LIST);
    }
    return getAllDcimPath(localList);
  }
  
  public static List<String> getAllDcimPath(List<Storage> paramList)
  {
    ArrayList localArrayList = new ArrayList();
    if (paramList != null)
    {
      int i = paramList.size() - 1;
      while (i >= 0)
      {
        String str = getDcimPath((Storage)paramList.get(i));
        if (str != null) {
          localArrayList.add(str);
        }
        i -= 1;
      }
    }
    if (localArrayList.isEmpty()) {
      localArrayList.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath());
    }
    return localArrayList;
  }
  
  public static String getDcimPath(Storage paramStorage)
  {
    if ((paramStorage != null) && (paramStorage.isReady())) {
      return paramStorage.getDirectoryPath() + "/DCIM";
    }
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/io/StorageUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */