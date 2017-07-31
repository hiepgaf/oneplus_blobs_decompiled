package com.oneplus.io;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Build.VERSION;
import android.os.Environment;
import com.oneplus.base.Log;
import com.oneplus.base.component.BasicComponent;
import com.oneplus.base.component.ComponentOwner;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StorageManagerImpl
  extends BasicComponent
  implements StorageManager
{
  private static int STORAGE_INTERNAL_L = Resources.getSystem().getIdentifier("storage_internal", "string", "android");
  private static int STORAGE_SD_CARD_L = Resources.getSystem().getIdentifier("storage_sd_card", "string", "android");
  private static int STORAGE_USB_L = Resources.getSystem().getIdentifier("storage_usb", "string", "android");
  private BroadcastReceiver m_BroadcastReceiver = null;
  private Context m_Context = null;
  private Class<?> m_DiskInfoClass;
  private Method m_GetDescriptionId = null;
  private Method m_GetDisk;
  private Method m_GetPath = null;
  private Method m_GetStorageVolumes = null;
  private Method m_GetVolumes;
  private Method m_IsDefaultPrimary;
  private Method m_IsSd;
  private Method m_IsUsb;
  private IntentFilter m_MediaMounted = null;
  private IntentFilter m_ShutterDown;
  private BroadcastReceiver m_ShutterDownReceiver;
  private android.os.storage.StorageManager m_StorageManager = null;
  private Class<?> m_StorageVolumeClass = null;
  private Object[] m_StorageVolumes = null;
  private Class<?> m_VolumeInfo;
  private List<?> m_VolumeInfos;
  
  public StorageManagerImpl(ComponentOwner paramComponentOwner, Context paramContext)
  {
    super("StorageManager", paramComponentOwner, false);
    this.m_Context = paramContext;
  }
  
  private Storage instanceStorage(Storage.Type paramType, String paramString)
  {
    switch (-getcom-oneplus-io-Storage$TypeSwitchesValues()[paramType.ordinal()])
    {
    default: 
      return new StorageImpl(Storage.Type.UNKNOWN, paramString);
    case 1: 
      return new StorageImpl(Storage.Type.INTERNAL, paramString);
    case 2: 
      return new StorageImpl(Storage.Type.SD_CARD, paramString);
    }
    return new StorageImpl(Storage.Type.USB, paramString);
  }
  
  private boolean is_L_SDKVersion()
  {
    return Build.VERSION.SDK_INT <= 22;
  }
  
  private void registerReceivers()
  {
    this.m_MediaMounted = new IntentFilter();
    this.m_MediaMounted.addAction("android.intent.action.MEDIA_MOUNTED");
    this.m_MediaMounted.addAction("android.intent.action.MEDIA_UNMOUNTED");
    this.m_MediaMounted.addDataScheme("file");
    this.m_BroadcastReceiver = new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        paramAnonymousContext = paramAnonymousIntent.getAction();
        Log.d(StorageManagerImpl.-get0(StorageManagerImpl.this), "onReceive [" + paramAnonymousContext + "]");
        if (("android.intent.action.MEDIA_MOUNTED".equals(paramAnonymousContext)) || ("android.intent.action.MEDIA_UNMOUNTED".equals(paramAnonymousContext)))
        {
          if (StorageManagerImpl.-wrap0(StorageManagerImpl.this)) {
            StorageManagerImpl.-wrap1(StorageManagerImpl.this);
          }
        }
        else {
          return;
        }
        StorageManagerImpl.-wrap2(StorageManagerImpl.this);
      }
    };
    this.m_Context.registerReceiver(this.m_BroadcastReceiver, this.m_MediaMounted);
    this.m_ShutterDown = new IntentFilter();
    this.m_ShutterDown.addAction("android.intent.action.ACTION_SHUTDOWN");
    this.m_ShutterDownReceiver = new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        paramAnonymousContext = paramAnonymousIntent.getAction();
        Log.d(StorageManagerImpl.-get0(StorageManagerImpl.this), "onReceive [" + paramAnonymousContext + "]");
        if ("android.intent.action.ACTION_SHUTDOWN".equals(paramAnonymousContext))
        {
          StorageManagerImpl.-get2(StorageManagerImpl.this).unregisterReceiver(StorageManagerImpl.-get1(StorageManagerImpl.this));
          StorageManagerImpl.-get2(StorageManagerImpl.this).unregisterReceiver(StorageManagerImpl.-get3(StorageManagerImpl.this));
          StorageManagerImpl.-set0(StorageManagerImpl.this, null);
          StorageManagerImpl.-set3(StorageManagerImpl.this, null);
          StorageManagerImpl.-set1(StorageManagerImpl.this, null);
          StorageManagerImpl.-set2(StorageManagerImpl.this, null);
        }
      }
    };
    this.m_Context.registerReceiver(this.m_ShutterDownReceiver, this.m_ShutterDown);
  }
  
  private void scans_L_Storages()
  {
    int i = 0;
    for (;;)
    {
      ArrayList localArrayList;
      try
      {
        if (this.m_StorageManager == null)
        {
          Log.e(this.TAG, "scans_L_Storages - StorageManager is null");
          return;
        }
        this.m_StorageVolumes = ((Object[])this.m_GetStorageVolumes.invoke(this.m_StorageManager, new Object[0]));
        localArrayList = new ArrayList();
        Object[] arrayOfObject = this.m_StorageVolumes;
        int j = arrayOfObject.length;
        if (i < j)
        {
          Object localObject = arrayOfObject[i];
          String str = (String)this.m_GetPath.invoke(localObject, new Object[0]);
          int k = ((Integer)this.m_GetDescriptionId.invoke(localObject, new Object[0])).intValue();
          localObject = Storage.Type.UNKNOWN;
          if (k == STORAGE_INTERNAL_L)
          {
            localObject = Storage.Type.INTERNAL;
            if (!Environment.getExternalStorageState(new File(str)).equals("mounted")) {
              break label281;
            }
          }
          else
          {
            if (k == STORAGE_SD_CARD_L)
            {
              localObject = Storage.Type.SD_CARD;
              continue;
            }
            if (k == STORAGE_USB_L)
            {
              localObject = Storage.Type.USB;
              continue;
            }
            localObject = Storage.Type.UNKNOWN;
            continue;
          }
          Log.d(this.TAG, "Path: " + str + " ,Type: " + localObject + " ,decrip: " + k);
          localArrayList.add(instanceStorage((Storage.Type)localObject, str));
        }
      }
      catch (Throwable localThrowable)
      {
        Log.e(this.TAG, "failed to scans_L_Storages", localThrowable);
        return;
      }
      setReadOnly(PROP_STORAGE_LIST, localArrayList);
      return;
      label281:
      i += 1;
    }
  }
  
  private void scans_M_Storages()
  {
    ArrayList localArrayList;
    for (;;)
    {
      Object localObject2;
      try
      {
        if (this.m_StorageManager == null) {
          return;
        }
        if (this.m_GetVolumes == null) {
          return;
        }
        this.m_VolumeInfos = ((List)this.m_GetVolumes.invoke(this.m_StorageManager, new Object[0]));
        localArrayList = new ArrayList();
        Iterator localIterator = this.m_VolumeInfos.iterator();
        if (!localIterator.hasNext()) {
          break;
        }
        Object localObject1 = localIterator.next();
        String str1 = ((File)this.m_GetPath.invoke(localObject1, new Object[0])).getPath();
        localObject2 = this.m_GetDisk.invoke(localObject1, new Object[0]);
        localObject1 = Storage.Type.UNKNOWN;
        if (localObject2 == null)
        {
          localObject1 = Storage.Type.INTERNAL;
          if (Environment.getExternalStorageState(new File(str1)).equals("unmounted")) {
            continue;
          }
          localObject2 = str1;
          if (localObject1 == Storage.Type.INTERNAL)
          {
            String str2 = Environment.getExternalStorageDirectory().getPath();
            localObject2 = str1;
            if (str2.startsWith(str1)) {
              localObject2 = str2;
            }
          }
          Log.d(this.TAG, "Path: " + (String)localObject2 + " ,Type: " + localObject1);
          localArrayList.add(instanceStorage((Storage.Type)localObject1, (String)localObject2));
          continue;
        }
        if (!((Boolean)this.m_IsSd.invoke(localObject2, new Object[0])).booleanValue()) {
          break label265;
        }
      }
      catch (Throwable localThrowable)
      {
        Log.e(this.TAG, "failed to scans_M_Storages", localThrowable);
        return;
      }
      Storage.Type localType = Storage.Type.SD_CARD;
      continue;
      label265:
      if (((Boolean)this.m_IsUsb.invoke(localObject2, new Object[0])).booleanValue()) {
        localType = Storage.Type.USB;
      }
    }
    setReadOnly(PROP_STORAGE_LIST, localArrayList);
    return;
  }
  
  protected void onDeinitialize()
  {
    this.m_Context.unregisterReceiver(this.m_BroadcastReceiver);
    this.m_MediaMounted = null;
    this.m_BroadcastReceiver = null;
    this.m_Context.unregisterReceiver(this.m_ShutterDownReceiver);
    this.m_ShutterDown = null;
    this.m_ShutterDownReceiver = null;
    this.m_Context = null;
  }
  
  protected void onInitialize()
  {
    if (this.m_Context == null) {
      return;
    }
    Log.v(this.TAG, "onInitialize");
    registerReceivers();
    this.m_StorageManager = ((android.os.storage.StorageManager)this.m_Context.getSystemService("storage"));
    if (is_L_SDKVersion()) {
      try
      {
        this.m_GetStorageVolumes = this.m_StorageManager.getClass().getMethod("getVolumeList", new Class[0]);
        this.m_StorageVolumeClass = Class.forName("android.os.storage.StorageVolume");
        this.m_GetDescriptionId = this.m_StorageVolumeClass.getMethod("getDescriptionId", new Class[0]);
        this.m_GetPath = this.m_StorageVolumeClass.getMethod("getPath", new Class[0]);
        scans_L_Storages();
        return;
      }
      catch (Throwable localThrowable1)
      {
        Log.e(this.TAG, "onInitialize failed", localThrowable1);
        return;
      }
    }
    try
    {
      this.m_GetVolumes = this.m_StorageManager.getClass().getMethod("getVolumes", new Class[0]);
      this.m_VolumeInfo = Class.forName("android.os.storage.VolumeInfo");
      this.m_GetPath = this.m_VolumeInfo.getMethod("getPath", new Class[0]);
      this.m_GetDisk = this.m_VolumeInfo.getMethod("getDisk", new Class[0]);
      this.m_DiskInfoClass = Class.forName("android.os.storage.DiskInfo");
      this.m_IsDefaultPrimary = this.m_DiskInfoClass.getMethod("isDefaultPrimary", new Class[0]);
      this.m_IsSd = this.m_DiskInfoClass.getMethod("isSd", new Class[0]);
      this.m_IsUsb = this.m_DiskInfoClass.getMethod("isUsb", new Class[0]);
      scans_M_Storages();
      return;
    }
    catch (Throwable localThrowable2)
    {
      Log.e(this.TAG, "onInitialize failed", localThrowable2);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/io/StorageManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */