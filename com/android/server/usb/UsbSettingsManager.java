package com.android.server.usb;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.XmlResourceParser;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.os.Binder;
import android.os.Parcelable;
import android.os.UserHandle;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.SparseBooleanArray;
import android.util.Xml;
import com.android.internal.content.PackageMonitor;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.XmlUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

class UsbSettingsManager
{
  private static final boolean DEBUG = false;
  private static final String TAG = "UsbSettingsManager";
  private static final File sSingleUserSettingsFile = new File("/data/system/usb_device_manager.xml");
  private final HashMap<UsbAccessory, SparseBooleanArray> mAccessoryPermissionMap;
  private final HashMap<AccessoryFilter, String> mAccessoryPreferenceMap;
  private final Context mContext;
  private final HashMap<String, SparseBooleanArray> mDevicePermissionMap;
  private final HashMap<DeviceFilter, String> mDevicePreferenceMap;
  private final boolean mDisablePermissionDialogs;
  private final Object mLock;
  private final MtpNotificationManager mMtpNotificationManager;
  private final PackageManager mPackageManager;
  MyPackageMonitor mPackageMonitor;
  private final AtomicFile mSettingsFile;
  private final UserHandle mUser;
  private final Context mUserContext;
  
  /* Error */
  public UsbSettingsManager(Context paramContext, UserHandle paramUserHandle)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 83	java/lang/Object:<init>	()V
    //   4: aload_0
    //   5: new 85	java/util/HashMap
    //   8: dup
    //   9: invokespecial 86	java/util/HashMap:<init>	()V
    //   12: putfield 88	com/android/server/usb/UsbSettingsManager:mDevicePermissionMap	Ljava/util/HashMap;
    //   15: aload_0
    //   16: new 85	java/util/HashMap
    //   19: dup
    //   20: invokespecial 86	java/util/HashMap:<init>	()V
    //   23: putfield 90	com/android/server/usb/UsbSettingsManager:mAccessoryPermissionMap	Ljava/util/HashMap;
    //   26: aload_0
    //   27: new 85	java/util/HashMap
    //   30: dup
    //   31: invokespecial 86	java/util/HashMap:<init>	()V
    //   34: putfield 92	com/android/server/usb/UsbSettingsManager:mDevicePreferenceMap	Ljava/util/HashMap;
    //   37: aload_0
    //   38: new 85	java/util/HashMap
    //   41: dup
    //   42: invokespecial 86	java/util/HashMap:<init>	()V
    //   45: putfield 94	com/android/server/usb/UsbSettingsManager:mAccessoryPreferenceMap	Ljava/util/HashMap;
    //   48: aload_0
    //   49: new 4	java/lang/Object
    //   52: dup
    //   53: invokespecial 83	java/lang/Object:<init>	()V
    //   56: putfield 96	com/android/server/usb/UsbSettingsManager:mLock	Ljava/lang/Object;
    //   59: aload_0
    //   60: new 14	com/android/server/usb/UsbSettingsManager$MyPackageMonitor
    //   63: dup
    //   64: aload_0
    //   65: aconst_null
    //   66: invokespecial 99	com/android/server/usb/UsbSettingsManager$MyPackageMonitor:<init>	(Lcom/android/server/usb/UsbSettingsManager;Lcom/android/server/usb/UsbSettingsManager$MyPackageMonitor;)V
    //   69: putfield 101	com/android/server/usb/UsbSettingsManager:mPackageMonitor	Lcom/android/server/usb/UsbSettingsManager$MyPackageMonitor;
    //   72: aload_0
    //   73: aload_1
    //   74: ldc 103
    //   76: iconst_0
    //   77: aload_2
    //   78: invokevirtual 109	android/content/Context:createPackageContextAsUser	(Ljava/lang/String;ILandroid/os/UserHandle;)Landroid/content/Context;
    //   81: putfield 111	com/android/server/usb/UsbSettingsManager:mUserContext	Landroid/content/Context;
    //   84: aload_0
    //   85: aload_1
    //   86: putfield 113	com/android/server/usb/UsbSettingsManager:mContext	Landroid/content/Context;
    //   89: aload_0
    //   90: aload_0
    //   91: getfield 111	com/android/server/usb/UsbSettingsManager:mUserContext	Landroid/content/Context;
    //   94: invokevirtual 117	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
    //   97: putfield 119	com/android/server/usb/UsbSettingsManager:mPackageManager	Landroid/content/pm/PackageManager;
    //   100: aload_0
    //   101: aload_2
    //   102: putfield 121	com/android/server/usb/UsbSettingsManager:mUser	Landroid/os/UserHandle;
    //   105: aload_0
    //   106: new 123	android/util/AtomicFile
    //   109: dup
    //   110: new 71	java/io/File
    //   113: dup
    //   114: aload_2
    //   115: invokevirtual 129	android/os/UserHandle:getIdentifier	()I
    //   118: invokestatic 135	android/os/Environment:getUserSystemDirectory	(I)Ljava/io/File;
    //   121: ldc -119
    //   123: invokespecial 140	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   126: invokespecial 143	android/util/AtomicFile:<init>	(Ljava/io/File;)V
    //   129: putfield 145	com/android/server/usb/UsbSettingsManager:mSettingsFile	Landroid/util/AtomicFile;
    //   132: aload_0
    //   133: aload_1
    //   134: invokevirtual 149	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   137: ldc -106
    //   139: invokevirtual 156	android/content/res/Resources:getBoolean	(I)Z
    //   142: putfield 158	com/android/server/usb/UsbSettingsManager:mDisablePermissionDialogs	Z
    //   145: aload_0
    //   146: getfield 96	com/android/server/usb/UsbSettingsManager:mLock	Ljava/lang/Object;
    //   149: astore_3
    //   150: aload_3
    //   151: monitorenter
    //   152: getstatic 161	android/os/UserHandle:SYSTEM	Landroid/os/UserHandle;
    //   155: aload_2
    //   156: invokevirtual 165	android/os/UserHandle:equals	(Ljava/lang/Object;)Z
    //   159: ifeq +7 -> 166
    //   162: aload_0
    //   163: invokespecial 168	com/android/server/usb/UsbSettingsManager:upgradeSingleUserLocked	()V
    //   166: aload_0
    //   167: invokespecial 171	com/android/server/usb/UsbSettingsManager:readSettingsLocked	()V
    //   170: aload_3
    //   171: monitorexit
    //   172: aload_0
    //   173: getfield 101	com/android/server/usb/UsbSettingsManager:mPackageMonitor	Lcom/android/server/usb/UsbSettingsManager$MyPackageMonitor;
    //   176: aload_0
    //   177: getfield 111	com/android/server/usb/UsbSettingsManager:mUserContext	Landroid/content/Context;
    //   180: aconst_null
    //   181: iconst_1
    //   182: invokevirtual 175	com/android/server/usb/UsbSettingsManager$MyPackageMonitor:register	(Landroid/content/Context;Landroid/os/Looper;Z)V
    //   185: aload_0
    //   186: new 177	com/android/server/usb/MtpNotificationManager
    //   189: dup
    //   190: aload_1
    //   191: new 6	com/android/server/usb/UsbSettingsManager$1
    //   194: dup
    //   195: aload_0
    //   196: invokespecial 180	com/android/server/usb/UsbSettingsManager$1:<init>	(Lcom/android/server/usb/UsbSettingsManager;)V
    //   199: invokespecial 183	com/android/server/usb/MtpNotificationManager:<init>	(Landroid/content/Context;Lcom/android/server/usb/MtpNotificationManager$OnOpenInAppListener;)V
    //   202: putfield 185	com/android/server/usb/UsbSettingsManager:mMtpNotificationManager	Lcom/android/server/usb/MtpNotificationManager;
    //   205: return
    //   206: astore_1
    //   207: new 187	java/lang/RuntimeException
    //   210: dup
    //   211: ldc -67
    //   213: invokespecial 190	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   216: athrow
    //   217: astore_1
    //   218: aload_3
    //   219: monitorexit
    //   220: aload_1
    //   221: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	222	0	this	UsbSettingsManager
    //   0	222	1	paramContext	Context
    //   0	222	2	paramUserHandle	UserHandle
    // Exception table:
    //   from	to	target	type
    //   72	84	206	android/content/pm/PackageManager$NameNotFoundException
    //   152	166	217	finally
    //   166	170	217	finally
  }
  
  private boolean clearCompatibleMatchesLocked(String paramString, AccessoryFilter paramAccessoryFilter)
  {
    boolean bool = false;
    paramString = this.mAccessoryPreferenceMap.keySet().iterator();
    while (paramString.hasNext())
    {
      AccessoryFilter localAccessoryFilter = (AccessoryFilter)paramString.next();
      if (paramAccessoryFilter.matches(localAccessoryFilter))
      {
        this.mAccessoryPreferenceMap.remove(localAccessoryFilter);
        bool = true;
      }
    }
    return bool;
  }
  
  private boolean clearCompatibleMatchesLocked(String paramString, DeviceFilter paramDeviceFilter)
  {
    boolean bool = false;
    paramString = this.mDevicePreferenceMap.keySet().iterator();
    while (paramString.hasNext())
    {
      DeviceFilter localDeviceFilter = (DeviceFilter)paramString.next();
      if (paramDeviceFilter.matches(localDeviceFilter))
      {
        this.mDevicePreferenceMap.remove(localDeviceFilter);
        bool = true;
      }
    }
    return bool;
  }
  
  private boolean clearPackageDefaultsLocked(String paramString)
  {
    boolean bool1 = false;
    boolean bool2 = false;
    for (;;)
    {
      int i;
      synchronized (this.mLock)
      {
        Object[] arrayOfObject;
        Object localObject2;
        if (this.mDevicePreferenceMap.containsValue(paramString))
        {
          arrayOfObject = this.mDevicePreferenceMap.keySet().toArray();
          i = 0;
          bool1 = bool2;
          if (i < arrayOfObject.length)
          {
            localObject2 = arrayOfObject[i];
            if (!paramString.equals(this.mDevicePreferenceMap.get(localObject2))) {
              break label182;
            }
            this.mDevicePreferenceMap.remove(localObject2);
            bool2 = true;
            break label182;
          }
        }
        bool2 = bool1;
        if (this.mAccessoryPreferenceMap.containsValue(paramString))
        {
          arrayOfObject = this.mAccessoryPreferenceMap.keySet().toArray();
          i = 0;
          bool2 = bool1;
          if (i < arrayOfObject.length)
          {
            localObject2 = arrayOfObject[i];
            if (paramString.equals(this.mAccessoryPreferenceMap.get(localObject2)))
            {
              this.mAccessoryPreferenceMap.remove(localObject2);
              bool1 = true;
            }
            i += 1;
            continue;
          }
        }
        return bool2;
      }
      label182:
      i += 1;
    }
  }
  
  private static Intent createDeviceAttachedIntent(UsbDevice paramUsbDevice)
  {
    Intent localIntent = new Intent("android.hardware.usb.action.USB_DEVICE_ATTACHED");
    localIntent.putExtra("device", paramUsbDevice);
    localIntent.addFlags(268435456);
    return localIntent;
  }
  
  private final ArrayList<ResolveInfo> getAccessoryMatchesLocked(UsbAccessory paramUsbAccessory, Intent paramIntent)
  {
    ArrayList localArrayList = new ArrayList();
    List localList = this.mPackageManager.queryIntentActivities(paramIntent, 128);
    int j = localList.size();
    int i = 0;
    while (i < j)
    {
      ResolveInfo localResolveInfo = (ResolveInfo)localList.get(i);
      if (packageMatchesLocked(localResolveInfo, paramIntent.getAction(), null, paramUsbAccessory)) {
        localArrayList.add(localResolveInfo);
      }
      i += 1;
    }
    return localArrayList;
  }
  
  private final ArrayList<ResolveInfo> getDeviceMatchesLocked(UsbDevice paramUsbDevice, Intent paramIntent)
  {
    ArrayList localArrayList = new ArrayList();
    List localList = this.mPackageManager.queryIntentActivities(paramIntent, 128);
    int j = localList.size();
    int i = 0;
    while (i < j)
    {
      ResolveInfo localResolveInfo = (ResolveInfo)localList.get(i);
      if (packageMatchesLocked(localResolveInfo, paramIntent.getAction(), paramUsbDevice, null)) {
        localArrayList.add(localResolveInfo);
      }
      i += 1;
    }
    return localArrayList;
  }
  
  private void handlePackageUpdate(String paramString)
  {
    Object localObject1 = this.mLock;
    int i = 0;
    for (;;)
    {
      int j;
      try
      {
        Object localObject2 = this.mPackageManager.getPackageInfo(paramString, 129);
        localObject2 = ((PackageInfo)localObject2).activities;
        if (localObject2 == null) {
          return;
        }
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        Slog.e("UsbSettingsManager", "handlePackageUpdate could not find package " + paramString, localNameNotFoundException);
        return;
        j = 0;
        if (j < localNameNotFoundException.length)
        {
          if (handlePackageUpdateLocked(paramString, localNameNotFoundException[j], "android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
            i = 1;
          }
          if (handlePackageUpdateLocked(paramString, localNameNotFoundException[j], "android.hardware.usb.action.USB_ACCESSORY_ATTACHED")) {
            i = 1;
          }
        }
        else
        {
          if (i != 0) {
            writeSettingsLocked();
          }
          return;
        }
      }
      finally {}
      j += 1;
    }
  }
  
  private boolean handlePackageUpdateLocked(String paramString1, ActivityInfo paramActivityInfo, String paramString2)
  {
    localObject = null;
    str1 = null;
    bool3 = false;
    bool2 = false;
    bool1 = bool3;
    for (;;)
    {
      try
      {
        paramString2 = paramActivityInfo.loadXmlMetaData(this.mPackageManager, paramString2);
        if (paramString2 == null)
        {
          if (paramString2 != null) {
            paramString2.close();
          }
          return false;
        }
        bool1 = bool3;
        str1 = paramString2;
        localObject = paramString2;
        XmlUtils.nextElement(paramString2);
        bool1 = bool2;
        str1 = paramString2;
        localObject = paramString2;
        if (paramString2.getEventType() == 1) {
          continue;
        }
        bool1 = bool2;
        str1 = paramString2;
        localObject = paramString2;
        str2 = paramString2.getName();
        bool1 = bool2;
        str1 = paramString2;
        localObject = paramString2;
        if (!"usb-device".equals(str2)) {
          continue;
        }
        bool3 = bool2;
        bool1 = bool2;
        str1 = paramString2;
        localObject = paramString2;
        if (clearCompatibleMatchesLocked(paramString1, DeviceFilter.read(paramString2))) {
          bool3 = true;
        }
      }
      catch (Exception paramString1)
      {
        String str2;
        localObject = str1;
        Slog.w("UsbSettingsManager", "Unable to load component info " + paramActivityInfo.toString(), paramString1);
        bool3 = bool1;
        if (str1 == null) {
          continue;
        }
        str1.close();
        bool3 = bool1;
        return bool3;
        bool3 = bool2;
        bool1 = bool2;
        str1 = paramString2;
        localObject = paramString2;
        if (!"usb-accessory".equals(str2)) {
          continue;
        }
        bool1 = bool2;
        str1 = paramString2;
        localObject = paramString2;
        boolean bool4 = clearCompatibleMatchesLocked(paramString1, AccessoryFilter.read(paramString2));
        bool3 = bool2;
        if (!bool4) {
          continue;
        }
        bool3 = true;
        continue;
        bool3 = bool2;
        if (paramString2 == null) {
          continue;
        }
        paramString2.close();
        return bool2;
      }
      finally
      {
        if (localObject == null) {
          continue;
        }
        ((XmlResourceParser)localObject).close();
      }
      bool1 = bool3;
      str1 = paramString2;
      localObject = paramString2;
      XmlUtils.nextElement(paramString2);
      bool2 = bool3;
    }
  }
  
  /* Error */
  private boolean packageMatchesLocked(ResolveInfo paramResolveInfo, String paramString, UsbDevice paramUsbDevice, UsbAccessory paramUsbAccessory)
  {
    // Byte code:
    //   0: aload_1
    //   1: getfield 381	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   4: astore 8
    //   6: aconst_null
    //   7: astore 7
    //   9: aconst_null
    //   10: astore 6
    //   12: aload 8
    //   14: aload_0
    //   15: getfield 119	com/android/server/usb/UsbSettingsManager:mPackageManager	Landroid/content/pm/PackageManager;
    //   18: aload_2
    //   19: invokevirtual 339	android/content/pm/ActivityInfo:loadXmlMetaData	(Landroid/content/pm/PackageManager;Ljava/lang/String;)Landroid/content/res/XmlResourceParser;
    //   22: astore_2
    //   23: aload_2
    //   24: ifnonnull +47 -> 71
    //   27: aload_2
    //   28: astore 6
    //   30: aload_2
    //   31: astore 7
    //   33: ldc 22
    //   35: new 306	java/lang/StringBuilder
    //   38: dup
    //   39: invokespecial 307	java/lang/StringBuilder:<init>	()V
    //   42: ldc_w 383
    //   45: invokevirtual 313	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   48: aload_1
    //   49: invokevirtual 386	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   52: invokevirtual 316	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   55: invokestatic 389	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   58: pop
    //   59: aload_2
    //   60: ifnull +9 -> 69
    //   63: aload_2
    //   64: invokeinterface 344 1 0
    //   69: iconst_0
    //   70: ireturn
    //   71: aload_2
    //   72: astore 6
    //   74: aload_2
    //   75: astore 7
    //   77: aload_2
    //   78: invokestatic 350	com/android/internal/util/XmlUtils:nextElement	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   81: aload_2
    //   82: astore 6
    //   84: aload_2
    //   85: astore 7
    //   87: aload_2
    //   88: invokeinterface 353 1 0
    //   93: iconst_1
    //   94: if_icmpeq +189 -> 283
    //   97: aload_2
    //   98: astore 6
    //   100: aload_2
    //   101: astore 7
    //   103: aload_2
    //   104: invokeinterface 356 1 0
    //   109: astore 8
    //   111: aload_3
    //   112: ifnull +53 -> 165
    //   115: aload_2
    //   116: astore 6
    //   118: aload_2
    //   119: astore 7
    //   121: ldc_w 358
    //   124: aload 8
    //   126: invokevirtual 241	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   129: ifeq +36 -> 165
    //   132: aload_2
    //   133: astore 6
    //   135: aload_2
    //   136: astore 7
    //   138: aload_2
    //   139: invokestatic 362	com/android/server/usb/UsbSettingsManager$DeviceFilter:read	(Lorg/xmlpull/v1/XmlPullParser;)Lcom/android/server/usb/UsbSettingsManager$DeviceFilter;
    //   142: aload_3
    //   143: invokevirtual 392	com/android/server/usb/UsbSettingsManager$DeviceFilter:matches	(Landroid/hardware/usb/UsbDevice;)Z
    //   146: istore 5
    //   148: iload 5
    //   150: ifeq +71 -> 221
    //   153: aload_2
    //   154: ifnull +9 -> 163
    //   157: aload_2
    //   158: invokeinterface 344 1 0
    //   163: iconst_1
    //   164: ireturn
    //   165: aload 4
    //   167: ifnull +54 -> 221
    //   170: aload_2
    //   171: astore 6
    //   173: aload_2
    //   174: astore 7
    //   176: ldc_w 372
    //   179: aload 8
    //   181: invokevirtual 241	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   184: ifeq +37 -> 221
    //   187: aload_2
    //   188: astore 6
    //   190: aload_2
    //   191: astore 7
    //   193: aload_2
    //   194: invokestatic 375	com/android/server/usb/UsbSettingsManager$AccessoryFilter:read	(Lorg/xmlpull/v1/XmlPullParser;)Lcom/android/server/usb/UsbSettingsManager$AccessoryFilter;
    //   197: aload 4
    //   199: invokevirtual 395	com/android/server/usb/UsbSettingsManager$AccessoryFilter:matches	(Landroid/hardware/usb/UsbAccessory;)Z
    //   202: istore 5
    //   204: iload 5
    //   206: ifeq +15 -> 221
    //   209: aload_2
    //   210: ifnull +9 -> 219
    //   213: aload_2
    //   214: invokeinterface 344 1 0
    //   219: iconst_1
    //   220: ireturn
    //   221: aload_2
    //   222: astore 6
    //   224: aload_2
    //   225: astore 7
    //   227: aload_2
    //   228: invokestatic 350	com/android/internal/util/XmlUtils:nextElement	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   231: goto -150 -> 81
    //   234: astore_2
    //   235: aload 6
    //   237: astore 7
    //   239: ldc 22
    //   241: new 306	java/lang/StringBuilder
    //   244: dup
    //   245: invokespecial 307	java/lang/StringBuilder:<init>	()V
    //   248: ldc_w 366
    //   251: invokevirtual 313	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   254: aload_1
    //   255: invokevirtual 396	android/content/pm/ResolveInfo:toString	()Ljava/lang/String;
    //   258: invokevirtual 313	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   261: invokevirtual 316	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   264: aload_2
    //   265: invokestatic 370	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   268: pop
    //   269: aload 6
    //   271: ifnull +10 -> 281
    //   274: aload 6
    //   276: invokeinterface 344 1 0
    //   281: iconst_0
    //   282: ireturn
    //   283: aload_2
    //   284: ifnull -3 -> 281
    //   287: aload_2
    //   288: invokeinterface 344 1 0
    //   293: iconst_0
    //   294: ireturn
    //   295: astore_1
    //   296: aload 7
    //   298: ifnull +10 -> 308
    //   301: aload 7
    //   303: invokeinterface 344 1 0
    //   308: aload_1
    //   309: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	310	0	this	UsbSettingsManager
    //   0	310	1	paramResolveInfo	ResolveInfo
    //   0	310	2	paramString	String
    //   0	310	3	paramUsbDevice	UsbDevice
    //   0	310	4	paramUsbAccessory	UsbAccessory
    //   146	59	5	bool	boolean
    //   10	265	6	str	String
    //   7	295	7	localObject1	Object
    //   4	176	8	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   12	23	234	java/lang/Exception
    //   33	59	234	java/lang/Exception
    //   77	81	234	java/lang/Exception
    //   87	97	234	java/lang/Exception
    //   103	111	234	java/lang/Exception
    //   121	132	234	java/lang/Exception
    //   138	148	234	java/lang/Exception
    //   176	187	234	java/lang/Exception
    //   193	204	234	java/lang/Exception
    //   227	231	234	java/lang/Exception
    //   12	23	295	finally
    //   33	59	295	finally
    //   77	81	295	finally
    //   87	97	295	finally
    //   103	111	295	finally
    //   121	132	295	finally
    //   138	148	295	finally
    //   176	187	295	finally
    //   193	204	295	finally
    //   227	231	295	finally
    //   239	269	295	finally
  }
  
  private void readPreference(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    Object localObject2 = null;
    int j = paramXmlPullParser.getAttributeCount();
    int i = 0;
    Object localObject1 = localObject2;
    if (i < j)
    {
      if ("package".equals(paramXmlPullParser.getAttributeName(i))) {
        localObject1 = paramXmlPullParser.getAttributeValue(i);
      }
    }
    else
    {
      XmlUtils.nextElement(paramXmlPullParser);
      if (!"usb-device".equals(paramXmlPullParser.getName())) {
        break label95;
      }
      localObject2 = DeviceFilter.read(paramXmlPullParser);
      this.mDevicePreferenceMap.put(localObject2, localObject1);
    }
    for (;;)
    {
      XmlUtils.nextElement(paramXmlPullParser);
      return;
      i += 1;
      break;
      label95:
      if ("usb-accessory".equals(paramXmlPullParser.getName()))
      {
        localObject2 = AccessoryFilter.read(paramXmlPullParser);
        this.mAccessoryPreferenceMap.put(localObject2, localObject1);
      }
    }
  }
  
  private void readSettingsLocked()
  {
    this.mDevicePreferenceMap.clear();
    this.mAccessoryPreferenceMap.clear();
    Object localObject1 = null;
    localObject4 = null;
    localObject5 = null;
    for (;;)
    {
      try
      {
        localFileInputStream = this.mSettingsFile.openRead();
        localObject5 = localFileInputStream;
        localObject1 = localFileInputStream;
        localObject4 = localFileInputStream;
        localXmlPullParser = Xml.newPullParser();
        localObject5 = localFileInputStream;
        localObject1 = localFileInputStream;
        localObject4 = localFileInputStream;
        localXmlPullParser.setInput(localFileInputStream, StandardCharsets.UTF_8.name());
        localObject5 = localFileInputStream;
        localObject1 = localFileInputStream;
        localObject4 = localFileInputStream;
        XmlUtils.nextElement(localXmlPullParser);
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        FileInputStream localFileInputStream;
        XmlPullParser localXmlPullParser;
        return;
        localObject5 = localFileInputStream;
        localObject2 = localFileInputStream;
        localObject4 = localFileInputStream;
        XmlUtils.nextElement(localXmlPullParser);
        continue;
      }
      catch (Exception localException)
      {
        Object localObject2;
        localObject4 = localObject2;
        Slog.e("UsbSettingsManager", "error reading settings file, deleting to start fresh", localException);
        localObject4 = localObject2;
        this.mSettingsFile.delete();
        return;
        IoUtils.closeQuietly(localException);
        return;
      }
      finally
      {
        IoUtils.closeQuietly((AutoCloseable)localObject4);
      }
      localObject5 = localFileInputStream;
      localObject1 = localFileInputStream;
      localObject4 = localFileInputStream;
      if (localXmlPullParser.getEventType() == 1) {
        continue;
      }
      localObject5 = localFileInputStream;
      localObject1 = localFileInputStream;
      localObject4 = localFileInputStream;
      if (!"preference".equals(localXmlPullParser.getName())) {
        continue;
      }
      localObject5 = localFileInputStream;
      localObject1 = localFileInputStream;
      localObject4 = localFileInputStream;
      readPreference(localXmlPullParser);
    }
  }
  
  private void requestPermissionDialog(Intent paramIntent, String paramString, PendingIntent paramPendingIntent)
  {
    int i = Binder.getCallingUid();
    try
    {
      if (this.mPackageManager.getApplicationInfo(paramString, 0).uid != i) {
        throw new IllegalArgumentException("package " + paramString + " does not match caller's uid " + i);
      }
    }
    catch (PackageManager.NameNotFoundException paramIntent)
    {
      throw new IllegalArgumentException("package " + paramString + " not found");
    }
    long l = Binder.clearCallingIdentity();
    paramIntent.setClassName("com.android.systemui", "com.android.systemui.usb.UsbPermissionActivity");
    paramIntent.addFlags(268435456);
    paramIntent.putExtra("android.intent.extra.INTENT", paramPendingIntent);
    paramIntent.putExtra("package", paramString);
    paramIntent.putExtra("android.intent.extra.UID", i);
    try
    {
      this.mUserContext.startActivityAsUser(paramIntent, this.mUser);
      return;
    }
    catch (ActivityNotFoundException paramIntent)
    {
      Slog.e("UsbSettingsManager", "unable to start UsbPermissionActivity");
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private void resolveActivity(Intent paramIntent, UsbDevice paramUsbDevice)
  {
    synchronized (this.mLock)
    {
      ArrayList localArrayList = getDeviceMatchesLocked(paramUsbDevice, paramIntent);
      String str = (String)this.mDevicePreferenceMap.get(new DeviceFilter(paramUsbDevice));
      resolveActivity(paramIntent, localArrayList, str, paramUsbDevice, null);
      return;
    }
  }
  
  private void resolveActivity(Intent paramIntent, ArrayList<ResolveInfo> paramArrayList, String paramString, UsbDevice paramUsbDevice, UsbAccessory paramUsbAccessory)
  {
    int j = paramArrayList.size();
    if (j == 0)
    {
      if (paramUsbAccessory != null)
      {
        paramIntent = paramUsbAccessory.getUri();
        if ((paramIntent != null) && (paramIntent.length() > 0))
        {
          paramArrayList = new Intent();
          paramArrayList.setClassName("com.android.systemui", "com.android.systemui.usb.UsbAccessoryUriActivity");
          paramArrayList.addFlags(268435456);
          paramArrayList.putExtra("accessory", paramUsbAccessory);
          paramArrayList.putExtra("uri", paramIntent);
        }
      }
      try
      {
        this.mUserContext.startActivityAsUser(paramArrayList, this.mUser);
        return;
      }
      catch (ActivityNotFoundException paramIntent)
      {
        Slog.e("UsbSettingsManager", "unable to start UsbAccessoryUriActivity");
        return;
      }
    }
    Object localObject2 = null;
    ResolveInfo localResolveInfo = null;
    Object localObject1 = localObject2;
    String str = paramString;
    if (j == 1)
    {
      localObject1 = localObject2;
      str = paramString;
      if (paramString == null)
      {
        localObject1 = (ResolveInfo)paramArrayList.get(0);
        localObject2 = localResolveInfo;
        if (((ResolveInfo)localObject1).activityInfo != null)
        {
          localObject2 = localResolveInfo;
          if (((ResolveInfo)localObject1).activityInfo.applicationInfo != null)
          {
            localObject2 = localResolveInfo;
            if ((((ResolveInfo)localObject1).activityInfo.applicationInfo.flags & 0x1) != 0) {
              localObject2 = localObject1;
            }
          }
        }
        localObject1 = localObject2;
        str = paramString;
        if (this.mDisablePermissionDialogs)
        {
          localResolveInfo = (ResolveInfo)paramArrayList.get(0);
          localObject1 = localObject2;
          str = paramString;
          if (localResolveInfo.activityInfo != null)
          {
            str = localResolveInfo.activityInfo.packageName;
            localObject1 = localObject2;
          }
        }
      }
    }
    paramString = (String)localObject1;
    int i;
    if (localObject1 == null)
    {
      paramString = (String)localObject1;
      if (str != null)
      {
        i = 0;
        paramString = (String)localObject1;
        if (i < j)
        {
          paramString = (ResolveInfo)paramArrayList.get(i);
          if ((paramString.activityInfo == null) || (!str.equals(paramString.activityInfo.packageName))) {
            break label371;
          }
        }
      }
    }
    if (paramString != null)
    {
      if (paramUsbDevice != null) {
        grantDevicePermission(paramUsbDevice, paramString.activityInfo.applicationInfo.uid);
      }
      for (;;)
      {
        try
        {
          paramIntent.setComponent(new ComponentName(paramString.activityInfo.packageName, paramString.activityInfo.name));
          this.mUserContext.startActivityAsUser(paramIntent, this.mUser);
          return;
        }
        catch (ActivityNotFoundException paramIntent)
        {
          label371:
          Slog.e("UsbSettingsManager", "startActivity failed", paramIntent);
          return;
        }
        i += 1;
        break;
        if (paramUsbAccessory != null) {
          grantAccessoryPermission(paramUsbAccessory, paramString.activityInfo.applicationInfo.uid);
        }
      }
    }
    paramString = new Intent();
    paramString.addFlags(268435456);
    if (j == 1)
    {
      paramString.setClassName("com.android.systemui", "com.android.systemui.usb.UsbConfirmActivity");
      paramString.putExtra("rinfo", (Parcelable)paramArrayList.get(0));
      if (paramUsbDevice != null) {
        paramString.putExtra("device", paramUsbDevice);
      }
    }
    for (;;)
    {
      try
      {
        this.mUserContext.startActivityAsUser(paramString, this.mUser);
        return;
      }
      catch (ActivityNotFoundException paramIntent)
      {
        Slog.e("UsbSettingsManager", "unable to start activity " + paramString);
        return;
      }
      paramString.putExtra("accessory", paramUsbAccessory);
      continue;
      paramString.setClassName("com.android.systemui", "com.android.systemui.usb.UsbResolverActivity");
      paramString.putParcelableArrayListExtra("rlist", paramArrayList);
      paramString.putExtra("android.intent.extra.INTENT", paramIntent);
    }
  }
  
  /* Error */
  private void upgradeSingleUserLocked()
  {
    // Byte code:
    //   0: getstatic 78	com/android/server/usb/UsbSettingsManager:sSingleUserSettingsFile	Ljava/io/File;
    //   3: invokevirtual 609	java/io/File:exists	()Z
    //   6: ifeq +118 -> 124
    //   9: aload_0
    //   10: getfield 92	com/android/server/usb/UsbSettingsManager:mDevicePreferenceMap	Ljava/util/HashMap;
    //   13: invokevirtual 426	java/util/HashMap:clear	()V
    //   16: aload_0
    //   17: getfield 94	com/android/server/usb/UsbSettingsManager:mAccessoryPreferenceMap	Ljava/util/HashMap;
    //   20: invokevirtual 426	java/util/HashMap:clear	()V
    //   23: aconst_null
    //   24: astore 5
    //   26: aconst_null
    //   27: astore_1
    //   28: aconst_null
    //   29: astore 4
    //   31: new 611	java/io/FileInputStream
    //   34: dup
    //   35: getstatic 78	com/android/server/usb/UsbSettingsManager:sSingleUserSettingsFile	Ljava/io/File;
    //   38: invokespecial 612	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   41: astore_2
    //   42: invokestatic 436	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   45: astore_1
    //   46: aload_1
    //   47: aload_2
    //   48: getstatic 442	java/nio/charset/StandardCharsets:UTF_8	Ljava/nio/charset/Charset;
    //   51: invokevirtual 447	java/nio/charset/Charset:name	()Ljava/lang/String;
    //   54: invokeinterface 451 3 0
    //   59: aload_1
    //   60: invokestatic 350	com/android/internal/util/XmlUtils:nextElement	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   63: aload_1
    //   64: invokeinterface 452 1 0
    //   69: iconst_1
    //   70: if_icmpeq +82 -> 152
    //   73: ldc_w 454
    //   76: aload_1
    //   77: invokeinterface 416 1 0
    //   82: invokevirtual 241	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   85: ifeq +40 -> 125
    //   88: aload_0
    //   89: aload_1
    //   90: invokespecial 456	com/android/server/usb/UsbSettingsManager:readPreference	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   93: goto -30 -> 63
    //   96: astore_3
    //   97: aload_2
    //   98: astore_1
    //   99: ldc 22
    //   101: ldc_w 614
    //   104: aload_3
    //   105: invokestatic 619	android/util/Log:wtf	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   108: pop
    //   109: aload_2
    //   110: invokestatic 462	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   113: aload_0
    //   114: invokespecial 331	com/android/server/usb/UsbSettingsManager:writeSettingsLocked	()V
    //   117: getstatic 78	com/android/server/usb/UsbSettingsManager:sSingleUserSettingsFile	Ljava/io/File;
    //   120: invokevirtual 621	java/io/File:delete	()Z
    //   123: pop
    //   124: return
    //   125: aload_1
    //   126: invokestatic 350	com/android/internal/util/XmlUtils:nextElement	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   129: goto -66 -> 63
    //   132: astore_3
    //   133: aload_2
    //   134: astore_1
    //   135: ldc 22
    //   137: ldc_w 614
    //   140: aload_3
    //   141: invokestatic 619	android/util/Log:wtf	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   144: pop
    //   145: aload_2
    //   146: invokestatic 462	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   149: goto -36 -> 113
    //   152: aload_2
    //   153: invokestatic 462	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   156: goto -43 -> 113
    //   159: astore_2
    //   160: aload_1
    //   161: invokestatic 462	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   164: aload_2
    //   165: athrow
    //   166: astore_3
    //   167: aload_2
    //   168: astore_1
    //   169: aload_3
    //   170: astore_2
    //   171: goto -11 -> 160
    //   174: astore_3
    //   175: aload 4
    //   177: astore_2
    //   178: goto -81 -> 97
    //   181: astore_3
    //   182: aload 5
    //   184: astore_2
    //   185: goto -52 -> 133
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	188	0	this	UsbSettingsManager
    //   27	142	1	localObject1	Object
    //   41	112	2	localFileInputStream	FileInputStream
    //   159	9	2	localObject2	Object
    //   170	15	2	localObject3	Object
    //   96	9	3	localIOException1	IOException
    //   132	9	3	localXmlPullParserException1	XmlPullParserException
    //   166	4	3	localObject4	Object
    //   174	1	3	localIOException2	IOException
    //   181	1	3	localXmlPullParserException2	XmlPullParserException
    //   29	147	4	localObject5	Object
    //   24	159	5	localObject6	Object
    // Exception table:
    //   from	to	target	type
    //   42	63	96	java/io/IOException
    //   63	93	96	java/io/IOException
    //   125	129	96	java/io/IOException
    //   42	63	132	org/xmlpull/v1/XmlPullParserException
    //   63	93	132	org/xmlpull/v1/XmlPullParserException
    //   125	129	132	org/xmlpull/v1/XmlPullParserException
    //   31	42	159	finally
    //   99	109	159	finally
    //   135	145	159	finally
    //   42	63	166	finally
    //   63	93	166	finally
    //   125	129	166	finally
    //   31	42	174	java/io/IOException
    //   31	42	181	org/xmlpull/v1/XmlPullParserException
  }
  
  private void writeSettingsLocked()
  {
    Object localObject1 = null;
    FastXmlSerializer localFastXmlSerializer;
    Object localObject2;
    try
    {
      FileOutputStream localFileOutputStream = this.mSettingsFile.startWrite();
      localObject1 = localFileOutputStream;
      localFastXmlSerializer = new FastXmlSerializer();
      localObject1 = localFileOutputStream;
      localFastXmlSerializer.setOutput(localFileOutputStream, StandardCharsets.UTF_8.name());
      localObject1 = localFileOutputStream;
      localFastXmlSerializer.startDocument(null, Boolean.valueOf(true));
      localObject1 = localFileOutputStream;
      localFastXmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
      localObject1 = localFileOutputStream;
      localFastXmlSerializer.startTag(null, "settings");
      localObject1 = localFileOutputStream;
      localIterator = this.mDevicePreferenceMap.keySet().iterator();
      for (;;)
      {
        localObject1 = localFileOutputStream;
        if (!localIterator.hasNext()) {
          break;
        }
        localObject1 = localFileOutputStream;
        localObject2 = (DeviceFilter)localIterator.next();
        localObject1 = localFileOutputStream;
        localFastXmlSerializer.startTag(null, "preference");
        localObject1 = localFileOutputStream;
        localFastXmlSerializer.attribute(null, "package", (String)this.mDevicePreferenceMap.get(localObject2));
        localObject1 = localFileOutputStream;
        ((DeviceFilter)localObject2).write(localFastXmlSerializer);
        localObject1 = localFileOutputStream;
        localFastXmlSerializer.endTag(null, "preference");
      }
      localObject1 = localIOException;
    }
    catch (IOException localIOException)
    {
      Slog.e("UsbSettingsManager", "Failed to write settings", localIOException);
      if (localObject1 != null) {
        this.mSettingsFile.failWrite((FileOutputStream)localObject1);
      }
      return;
    }
    Iterator localIterator = this.mAccessoryPreferenceMap.keySet().iterator();
    for (;;)
    {
      localObject1 = localIOException;
      if (!localIterator.hasNext()) {
        break;
      }
      localObject1 = localIOException;
      localObject2 = (AccessoryFilter)localIterator.next();
      localObject1 = localIOException;
      localFastXmlSerializer.startTag(null, "preference");
      localObject1 = localIOException;
      localFastXmlSerializer.attribute(null, "package", (String)this.mAccessoryPreferenceMap.get(localObject2));
      localObject1 = localIOException;
      ((AccessoryFilter)localObject2).write(localFastXmlSerializer);
      localObject1 = localIOException;
      localFastXmlSerializer.endTag(null, "preference");
    }
    localObject1 = localIOException;
    localFastXmlSerializer.endTag(null, "settings");
    localObject1 = localIOException;
    localFastXmlSerializer.endDocument();
    localObject1 = localIOException;
    this.mSettingsFile.finishWrite(localIOException);
  }
  
  public void accessoryAttached(UsbAccessory paramUsbAccessory)
  {
    Intent localIntent = new Intent("android.hardware.usb.action.USB_ACCESSORY_ATTACHED");
    localIntent.putExtra("accessory", paramUsbAccessory);
    localIntent.addFlags(268435456);
    synchronized (this.mLock)
    {
      ArrayList localArrayList = getAccessoryMatchesLocked(paramUsbAccessory, localIntent);
      String str = (String)this.mAccessoryPreferenceMap.get(new AccessoryFilter(paramUsbAccessory));
      resolveActivity(localIntent, localArrayList, str, null, paramUsbAccessory);
      return;
    }
  }
  
  public void accessoryDetached(UsbAccessory paramUsbAccessory)
  {
    this.mAccessoryPermissionMap.remove(paramUsbAccessory);
    Intent localIntent = new Intent("android.hardware.usb.action.USB_ACCESSORY_DETACHED");
    localIntent.putExtra("accessory", paramUsbAccessory);
    this.mContext.sendBroadcastAsUser(localIntent, UserHandle.ALL);
  }
  
  public void checkPermission(UsbAccessory paramUsbAccessory)
  {
    if (!hasPermission(paramUsbAccessory)) {
      throw new SecurityException("User has not given permission to accessory " + paramUsbAccessory);
    }
  }
  
  public void checkPermission(UsbDevice paramUsbDevice)
  {
    if (!hasPermission(paramUsbDevice)) {
      throw new SecurityException("User has not given permission to device " + paramUsbDevice);
    }
  }
  
  public void clearDefaults(String paramString)
  {
    synchronized (this.mLock)
    {
      if (clearPackageDefaultsLocked(paramString)) {
        writeSettingsLocked();
      }
      return;
    }
  }
  
  public void deviceAttached(UsbDevice paramUsbDevice)
  {
    Intent localIntent = createDeviceAttachedIntent(paramUsbDevice);
    this.mUserContext.sendBroadcast(localIntent);
    if (MtpNotificationManager.shouldShowNotification(this.mPackageManager, paramUsbDevice))
    {
      this.mMtpNotificationManager.showNotification(paramUsbDevice);
      return;
    }
    resolveActivity(localIntent, paramUsbDevice);
  }
  
  public void deviceDetached(UsbDevice paramUsbDevice)
  {
    this.mDevicePermissionMap.remove(paramUsbDevice.getDeviceName());
    Intent localIntent = new Intent("android.hardware.usb.action.USB_DEVICE_DETACHED");
    localIntent.putExtra("device", paramUsbDevice);
    this.mContext.sendBroadcastAsUser(localIntent, UserHandle.ALL);
    this.mMtpNotificationManager.hideNotification(paramUsbDevice.getDeviceId());
  }
  
  public void dump(IndentingPrintWriter paramIndentingPrintWriter)
  {
    Object localObject2;
    int j;
    int i;
    synchronized (this.mLock)
    {
      paramIndentingPrintWriter.println("Device permissions:");
      localIterator = this.mDevicePermissionMap.keySet().iterator();
      if (localIterator.hasNext())
      {
        localObject2 = (String)localIterator.next();
        paramIndentingPrintWriter.print("  " + (String)localObject2 + ": ");
        localObject2 = (SparseBooleanArray)this.mDevicePermissionMap.get(localObject2);
        j = ((SparseBooleanArray)localObject2).size();
        i = 0;
        while (i < j)
        {
          paramIndentingPrintWriter.print(Integer.toString(((SparseBooleanArray)localObject2).keyAt(i)) + " ");
          i += 1;
        }
        paramIndentingPrintWriter.println();
      }
    }
    paramIndentingPrintWriter.println("Accessory permissions:");
    Iterator localIterator = this.mAccessoryPermissionMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      localObject2 = (UsbAccessory)localIterator.next();
      paramIndentingPrintWriter.print("  " + localObject2 + ": ");
      localObject2 = (SparseBooleanArray)this.mAccessoryPermissionMap.get(localObject2);
      j = ((SparseBooleanArray)localObject2).size();
      i = 0;
      while (i < j)
      {
        paramIndentingPrintWriter.print(Integer.toString(((SparseBooleanArray)localObject2).keyAt(i)) + " ");
        i += 1;
      }
      paramIndentingPrintWriter.println();
    }
    paramIndentingPrintWriter.println("Device preferences:");
    localIterator = this.mDevicePreferenceMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      localObject2 = (DeviceFilter)localIterator.next();
      paramIndentingPrintWriter.println("  " + localObject2 + ": " + (String)this.mDevicePreferenceMap.get(localObject2));
    }
    paramIndentingPrintWriter.println("Accessory preferences:");
    localIterator = this.mAccessoryPreferenceMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      localObject2 = (AccessoryFilter)localIterator.next();
      paramIndentingPrintWriter.println("  " + localObject2 + ": " + (String)this.mAccessoryPreferenceMap.get(localObject2));
    }
  }
  
  public void grantAccessoryPermission(UsbAccessory paramUsbAccessory, int paramInt)
  {
    synchronized (this.mLock)
    {
      SparseBooleanArray localSparseBooleanArray2 = (SparseBooleanArray)this.mAccessoryPermissionMap.get(paramUsbAccessory);
      SparseBooleanArray localSparseBooleanArray1 = localSparseBooleanArray2;
      if (localSparseBooleanArray2 == null)
      {
        localSparseBooleanArray1 = new SparseBooleanArray(1);
        this.mAccessoryPermissionMap.put(paramUsbAccessory, localSparseBooleanArray1);
      }
      localSparseBooleanArray1.put(paramInt, true);
      return;
    }
  }
  
  public void grantDevicePermission(UsbDevice paramUsbDevice, int paramInt)
  {
    synchronized (this.mLock)
    {
      String str = paramUsbDevice.getDeviceName();
      SparseBooleanArray localSparseBooleanArray = (SparseBooleanArray)this.mDevicePermissionMap.get(str);
      paramUsbDevice = localSparseBooleanArray;
      if (localSparseBooleanArray == null)
      {
        paramUsbDevice = new SparseBooleanArray(1);
        this.mDevicePermissionMap.put(str, paramUsbDevice);
      }
      paramUsbDevice.put(paramInt, true);
      return;
    }
  }
  
  public boolean hasDefaults(String paramString)
  {
    synchronized (this.mLock)
    {
      boolean bool = this.mDevicePreferenceMap.values().contains(paramString);
      if (bool) {
        return true;
      }
      bool = this.mAccessoryPreferenceMap.values().contains(paramString);
      return bool;
    }
  }
  
  public boolean hasPermission(UsbAccessory paramUsbAccessory)
  {
    synchronized (this.mLock)
    {
      int i = Binder.getCallingUid();
      if (i != 1000)
      {
        bool = this.mDisablePermissionDialogs;
        if (!bool) {}
      }
      else
      {
        return true;
      }
      paramUsbAccessory = (SparseBooleanArray)this.mAccessoryPermissionMap.get(paramUsbAccessory);
      if (paramUsbAccessory == null) {
        return false;
      }
      boolean bool = paramUsbAccessory.get(i);
      return bool;
    }
  }
  
  public boolean hasPermission(UsbDevice paramUsbDevice)
  {
    synchronized (this.mLock)
    {
      int i = Binder.getCallingUid();
      if (i != 1000)
      {
        bool = this.mDisablePermissionDialogs;
        if (!bool) {}
      }
      else
      {
        return true;
      }
      paramUsbDevice = (SparseBooleanArray)this.mDevicePermissionMap.get(paramUsbDevice.getDeviceName());
      if (paramUsbDevice == null) {
        return false;
      }
      boolean bool = paramUsbDevice.get(i);
      return bool;
    }
  }
  
  public void requestPermission(UsbAccessory paramUsbAccessory, String paramString, PendingIntent paramPendingIntent)
  {
    Intent localIntent = new Intent();
    if (hasPermission(paramUsbAccessory))
    {
      localIntent.putExtra("accessory", paramUsbAccessory);
      localIntent.putExtra("permission", true);
    }
    try
    {
      paramPendingIntent.send(this.mUserContext, 0, localIntent);
      return;
    }
    catch (PendingIntent.CanceledException paramUsbAccessory) {}
    localIntent.putExtra("accessory", paramUsbAccessory);
    requestPermissionDialog(localIntent, paramString, paramPendingIntent);
    return;
  }
  
  public void requestPermission(UsbDevice paramUsbDevice, String paramString, PendingIntent paramPendingIntent)
  {
    Intent localIntent = new Intent();
    if (hasPermission(paramUsbDevice))
    {
      localIntent.putExtra("device", paramUsbDevice);
      localIntent.putExtra("permission", true);
    }
    try
    {
      paramPendingIntent.send(this.mUserContext, 0, localIntent);
      return;
    }
    catch (PendingIntent.CanceledException paramUsbDevice) {}
    localIntent.putExtra("device", paramUsbDevice);
    requestPermissionDialog(localIntent, paramString, paramPendingIntent);
    return;
  }
  
  public void setAccessoryPackage(UsbAccessory paramUsbAccessory, String paramString)
  {
    AccessoryFilter localAccessoryFilter = new AccessoryFilter(paramUsbAccessory);
    paramUsbAccessory = this.mLock;
    if (paramString == null) {}
    for (;;)
    {
      try
      {
        if (this.mAccessoryPreferenceMap.remove(localAccessoryFilter) != null)
        {
          i = 1;
          if (i != 0) {
            writeSettingsLocked();
          }
          return;
        }
        int i = 0;
        continue;
        int j;
        if (paramString.equals(this.mAccessoryPreferenceMap.get(localAccessoryFilter)))
        {
          j = 0;
          i = j;
          if (j != 0)
          {
            this.mAccessoryPreferenceMap.put(localAccessoryFilter, paramString);
            i = j;
          }
        }
        else
        {
          j = 1;
        }
      }
      finally {}
    }
  }
  
  public void setDevicePackage(UsbDevice paramUsbDevice, String paramString)
  {
    DeviceFilter localDeviceFilter = new DeviceFilter(paramUsbDevice);
    paramUsbDevice = this.mLock;
    if (paramString == null) {}
    for (;;)
    {
      try
      {
        if (this.mDevicePreferenceMap.remove(localDeviceFilter) != null)
        {
          i = 1;
          if (i != 0) {
            writeSettingsLocked();
          }
          return;
        }
        int i = 0;
        continue;
        int j;
        if (paramString.equals(this.mDevicePreferenceMap.get(localDeviceFilter)))
        {
          j = 0;
          i = j;
          if (j != 0)
          {
            this.mDevicePreferenceMap.put(localDeviceFilter, paramString);
            i = j;
          }
        }
        else
        {
          j = 1;
        }
      }
      finally {}
    }
  }
  
  private static class AccessoryFilter
  {
    public final String mManufacturer;
    public final String mModel;
    public final String mVersion;
    
    public AccessoryFilter(UsbAccessory paramUsbAccessory)
    {
      this.mManufacturer = paramUsbAccessory.getManufacturer();
      this.mModel = paramUsbAccessory.getModel();
      this.mVersion = paramUsbAccessory.getVersion();
    }
    
    public AccessoryFilter(String paramString1, String paramString2, String paramString3)
    {
      this.mManufacturer = paramString1;
      this.mModel = paramString2;
      this.mVersion = paramString3;
    }
    
    public static AccessoryFilter read(XmlPullParser paramXmlPullParser)
      throws XmlPullParserException, IOException
    {
      Object localObject3 = null;
      Object localObject1 = null;
      Object localObject2 = null;
      int j = paramXmlPullParser.getAttributeCount();
      int i = 0;
      if (i < j)
      {
        String str2 = paramXmlPullParser.getAttributeName(i);
        String str1 = paramXmlPullParser.getAttributeValue(i);
        Object localObject5;
        Object localObject4;
        if ("manufacturer".equals(str2))
        {
          localObject5 = localObject1;
          localObject4 = str1;
        }
        for (;;)
        {
          i += 1;
          localObject3 = localObject4;
          localObject1 = localObject5;
          break;
          if ("model".equals(str2))
          {
            localObject4 = localObject3;
            localObject5 = str1;
          }
          else
          {
            localObject4 = localObject3;
            localObject5 = localObject1;
            if ("version".equals(str2))
            {
              localObject4 = localObject3;
              localObject5 = localObject1;
              localObject2 = str1;
            }
          }
        }
      }
      return new AccessoryFilter((String)localObject3, (String)localObject1, (String)localObject2);
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool3 = false;
      boolean bool2 = false;
      if ((this.mManufacturer == null) || (this.mModel == null)) {}
      while (this.mVersion == null) {
        return false;
      }
      boolean bool1;
      if ((paramObject instanceof AccessoryFilter))
      {
        paramObject = (AccessoryFilter)paramObject;
        bool1 = bool2;
        if (this.mManufacturer.equals(((AccessoryFilter)paramObject).mManufacturer))
        {
          bool1 = bool2;
          if (this.mModel.equals(((AccessoryFilter)paramObject).mModel)) {
            bool1 = this.mVersion.equals(((AccessoryFilter)paramObject).mVersion);
          }
        }
        return bool1;
      }
      if ((paramObject instanceof UsbAccessory))
      {
        paramObject = (UsbAccessory)paramObject;
        bool1 = bool3;
        if (this.mManufacturer.equals(((UsbAccessory)paramObject).getManufacturer()))
        {
          bool1 = bool3;
          if (this.mModel.equals(((UsbAccessory)paramObject).getModel())) {
            bool1 = this.mVersion.equals(((UsbAccessory)paramObject).getVersion());
          }
        }
        return bool1;
      }
      return false;
    }
    
    public int hashCode()
    {
      int k = 0;
      int i;
      int j;
      if (this.mManufacturer == null)
      {
        i = 0;
        if (this.mModel != null) {
          break label44;
        }
        j = 0;
        label20:
        if (this.mVersion != null) {
          break label55;
        }
      }
      for (;;)
      {
        return i ^ j ^ k;
        i = this.mManufacturer.hashCode();
        break;
        label44:
        j = this.mModel.hashCode();
        break label20;
        label55:
        k = this.mVersion.hashCode();
      }
    }
    
    public boolean matches(UsbAccessory paramUsbAccessory)
    {
      if ((this.mManufacturer == null) || (paramUsbAccessory.getManufacturer().equals(this.mManufacturer)))
      {
        if ((this.mModel == null) || (paramUsbAccessory.getModel().equals(this.mModel)))
        {
          if ((this.mVersion != null) && (!paramUsbAccessory.getVersion().equals(this.mVersion))) {
            break label69;
          }
          return true;
        }
      }
      else {
        return false;
      }
      return false;
      label69:
      return false;
    }
    
    public boolean matches(AccessoryFilter paramAccessoryFilter)
    {
      if ((this.mManufacturer == null) || (paramAccessoryFilter.mManufacturer.equals(this.mManufacturer)))
      {
        if ((this.mModel == null) || (paramAccessoryFilter.mModel.equals(this.mModel)))
        {
          if ((this.mVersion != null) && (!paramAccessoryFilter.mVersion.equals(this.mVersion))) {
            break label69;
          }
          return true;
        }
      }
      else {
        return false;
      }
      return false;
      label69:
      return false;
    }
    
    public String toString()
    {
      return "AccessoryFilter[mManufacturer=\"" + this.mManufacturer + "\", mModel=\"" + this.mModel + "\", mVersion=\"" + this.mVersion + "\"]";
    }
    
    public void write(XmlSerializer paramXmlSerializer)
      throws IOException
    {
      paramXmlSerializer.startTag(null, "usb-accessory");
      if (this.mManufacturer != null) {
        paramXmlSerializer.attribute(null, "manufacturer", this.mManufacturer);
      }
      if (this.mModel != null) {
        paramXmlSerializer.attribute(null, "model", this.mModel);
      }
      if (this.mVersion != null) {
        paramXmlSerializer.attribute(null, "version", this.mVersion);
      }
      paramXmlSerializer.endTag(null, "usb-accessory");
    }
  }
  
  private static class DeviceFilter
  {
    public final int mClass;
    public final String mManufacturerName;
    public final int mProductId;
    public final String mProductName;
    public final int mProtocol;
    public final String mSerialNumber;
    public final int mSubclass;
    public final int mVendorId;
    
    public DeviceFilter(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, String paramString1, String paramString2, String paramString3)
    {
      this.mVendorId = paramInt1;
      this.mProductId = paramInt2;
      this.mClass = paramInt3;
      this.mSubclass = paramInt4;
      this.mProtocol = paramInt5;
      this.mManufacturerName = paramString1;
      this.mProductName = paramString2;
      this.mSerialNumber = paramString3;
    }
    
    public DeviceFilter(UsbDevice paramUsbDevice)
    {
      this.mVendorId = paramUsbDevice.getVendorId();
      this.mProductId = paramUsbDevice.getProductId();
      this.mClass = paramUsbDevice.getDeviceClass();
      this.mSubclass = paramUsbDevice.getDeviceSubclass();
      this.mProtocol = paramUsbDevice.getDeviceProtocol();
      this.mManufacturerName = paramUsbDevice.getManufacturerName();
      this.mProductName = paramUsbDevice.getProductName();
      this.mSerialNumber = paramUsbDevice.getSerialNumber();
    }
    
    private boolean matches(int paramInt1, int paramInt2, int paramInt3)
    {
      if (((this.mClass == -1) || (paramInt1 == this.mClass)) && ((this.mSubclass == -1) || (paramInt2 == this.mSubclass))) {
        return (this.mProtocol == -1) || (paramInt3 == this.mProtocol);
      }
      return false;
    }
    
    public static DeviceFilter read(XmlPullParser paramXmlPullParser)
      throws XmlPullParserException, IOException
    {
      int i2 = -1;
      int i1 = -1;
      int n = -1;
      int k = -1;
      int m = -1;
      Object localObject5 = null;
      Object localObject4 = null;
      Object localObject3 = null;
      int i7 = paramXmlPullParser.getAttributeCount();
      int j = 0;
      if (j < i7)
      {
        String str = paramXmlPullParser.getAttributeName(j);
        Object localObject1 = paramXmlPullParser.getAttributeValue(j);
        Object localObject7;
        Object localObject6;
        int i6;
        int i5;
        int i4;
        int i3;
        if ("manufacturer-name".equals(str))
        {
          localObject7 = localObject3;
          localObject6 = localObject4;
          i6 = k;
          i5 = n;
          i4 = i1;
          i3 = i2;
        }
        for (;;)
        {
          j += 1;
          i2 = i3;
          i1 = i4;
          n = i5;
          k = i6;
          localObject5 = localObject1;
          localObject4 = localObject6;
          localObject3 = localObject7;
          break;
          if ("product-name".equals(str))
          {
            localObject6 = localObject1;
            i3 = i2;
            i4 = i1;
            i5 = n;
            i6 = k;
            localObject1 = localObject5;
            localObject7 = localObject3;
          }
          else if ("serial-number".equals(str))
          {
            localObject7 = localObject1;
            i3 = i2;
            i4 = i1;
            i5 = n;
            i6 = k;
            localObject1 = localObject5;
            localObject6 = localObject4;
          }
          else
          {
            i3 = 10;
            int i = i3;
            localObject6 = localObject1;
            if (localObject1 != null)
            {
              i = i3;
              localObject6 = localObject1;
              if (((String)localObject1).length() > 2)
              {
                i = i3;
                localObject6 = localObject1;
                if (((String)localObject1).charAt(0) == '0') {
                  if (((String)localObject1).charAt(1) != 'x')
                  {
                    i = i3;
                    localObject6 = localObject1;
                    if (((String)localObject1).charAt(1) != 'X') {}
                  }
                  else
                  {
                    i = 16;
                    localObject6 = ((String)localObject1).substring(2);
                  }
                }
              }
            }
            Object localObject2;
            try
            {
              i = Integer.parseInt((String)localObject6, i);
              if (!"vendor-id".equals(str)) {
                break label400;
              }
              i3 = i;
              i4 = i1;
              i5 = n;
              i6 = k;
              localObject1 = localObject5;
              localObject6 = localObject4;
              localObject7 = localObject3;
            }
            catch (NumberFormatException localNumberFormatException)
            {
              Slog.e("UsbSettingsManager", "invalid number for field " + str, localNumberFormatException);
              i3 = i2;
              i4 = i1;
              i5 = n;
              i6 = k;
              localObject2 = localObject5;
              localObject6 = localObject4;
              localObject7 = localObject3;
            }
            continue;
            label400:
            if ("product-id".equals(str))
            {
              i3 = i2;
              i4 = i;
              i5 = n;
              i6 = k;
              localObject2 = localObject5;
              localObject6 = localObject4;
              localObject7 = localObject3;
            }
            else if ("class".equals(str))
            {
              i3 = i2;
              i4 = i1;
              i5 = i;
              i6 = k;
              localObject2 = localObject5;
              localObject6 = localObject4;
              localObject7 = localObject3;
            }
            else if ("subclass".equals(str))
            {
              i3 = i2;
              i4 = i1;
              i5 = n;
              i6 = i;
              localObject2 = localObject5;
              localObject6 = localObject4;
              localObject7 = localObject3;
            }
            else
            {
              i3 = i2;
              i4 = i1;
              i5 = n;
              i6 = k;
              localObject2 = localObject5;
              localObject6 = localObject4;
              localObject7 = localObject3;
              if ("protocol".equals(str))
              {
                i3 = i2;
                i4 = i1;
                i5 = n;
                i6 = k;
                m = i;
                localObject2 = localObject5;
                localObject6 = localObject4;
                localObject7 = localObject3;
              }
            }
          }
        }
      }
      return new DeviceFilter(i2, i1, n, k, m, (String)localObject5, (String)localObject4, (String)localObject3);
    }
    
    public boolean equals(Object paramObject)
    {
      if ((this.mVendorId == -1) || (this.mProductId == -1)) {}
      while ((this.mClass == -1) || (this.mSubclass == -1) || (this.mProtocol == -1)) {
        return false;
      }
      if ((paramObject instanceof DeviceFilter))
      {
        paramObject = (DeviceFilter)paramObject;
        if ((((DeviceFilter)paramObject).mVendorId != this.mVendorId) || (((DeviceFilter)paramObject).mProductId != this.mProductId)) {}
        while ((((DeviceFilter)paramObject).mClass != this.mClass) || (((DeviceFilter)paramObject).mSubclass != this.mSubclass) || (((DeviceFilter)paramObject).mProtocol != this.mProtocol)) {
          return false;
        }
        if ((((DeviceFilter)paramObject).mManufacturerName != null) && (this.mManufacturerName == null)) {}
        while (((((DeviceFilter)paramObject).mManufacturerName == null) && (this.mManufacturerName != null)) || ((((DeviceFilter)paramObject).mProductName != null) && (this.mProductName == null)) || ((((DeviceFilter)paramObject).mProductName == null) && (this.mProductName != null)) || ((((DeviceFilter)paramObject).mSerialNumber != null) && (this.mSerialNumber == null)) || ((((DeviceFilter)paramObject).mSerialNumber == null) && (this.mSerialNumber != null))) {
          return false;
        }
        return ((((DeviceFilter)paramObject).mManufacturerName == null) || (this.mManufacturerName == null) || (this.mManufacturerName.equals(((DeviceFilter)paramObject).mManufacturerName))) && ((((DeviceFilter)paramObject).mProductName == null) || (this.mProductName == null) || (this.mProductName.equals(((DeviceFilter)paramObject).mProductName))) && ((((DeviceFilter)paramObject).mSerialNumber == null) || (this.mSerialNumber == null) || (this.mSerialNumber.equals(((DeviceFilter)paramObject).mSerialNumber)));
      }
      if ((paramObject instanceof UsbDevice))
      {
        paramObject = (UsbDevice)paramObject;
        if ((((UsbDevice)paramObject).getVendorId() != this.mVendorId) || (((UsbDevice)paramObject).getProductId() != this.mProductId)) {}
        while ((((UsbDevice)paramObject).getDeviceClass() != this.mClass) || (((UsbDevice)paramObject).getDeviceSubclass() != this.mSubclass) || (((UsbDevice)paramObject).getDeviceProtocol() != this.mProtocol)) {
          return false;
        }
        if ((this.mManufacturerName != null) && (((UsbDevice)paramObject).getManufacturerName() == null)) {}
        while (((this.mManufacturerName == null) && (((UsbDevice)paramObject).getManufacturerName() != null)) || ((this.mProductName != null) && (((UsbDevice)paramObject).getProductName() == null)) || ((this.mProductName == null) && (((UsbDevice)paramObject).getProductName() != null)) || ((this.mSerialNumber != null) && (((UsbDevice)paramObject).getSerialNumber() == null)) || ((this.mSerialNumber == null) && (((UsbDevice)paramObject).getSerialNumber() != null))) {
          return false;
        }
        return ((((UsbDevice)paramObject).getManufacturerName() == null) || (this.mManufacturerName.equals(((UsbDevice)paramObject).getManufacturerName()))) && ((((UsbDevice)paramObject).getProductName() == null) || (this.mProductName.equals(((UsbDevice)paramObject).getProductName()))) && ((((UsbDevice)paramObject).getSerialNumber() == null) || (this.mSerialNumber.equals(((UsbDevice)paramObject).getSerialNumber())));
      }
      return false;
    }
    
    public int hashCode()
    {
      return (this.mVendorId << 16 | this.mProductId) ^ (this.mClass << 16 | this.mSubclass << 8 | this.mProtocol);
    }
    
    public boolean matches(UsbDevice paramUsbDevice)
    {
      if ((this.mVendorId != -1) && (paramUsbDevice.getVendorId() != this.mVendorId)) {
        return false;
      }
      if ((this.mProductId != -1) && (paramUsbDevice.getProductId() != this.mProductId)) {
        return false;
      }
      if ((this.mManufacturerName != null) && (paramUsbDevice.getManufacturerName() == null)) {
        return false;
      }
      if ((this.mProductName != null) && (paramUsbDevice.getProductName() == null)) {
        return false;
      }
      if ((this.mSerialNumber != null) && (paramUsbDevice.getSerialNumber() == null)) {
        return false;
      }
      if ((this.mManufacturerName == null) || (paramUsbDevice.getManufacturerName() == null) || (this.mManufacturerName.equals(paramUsbDevice.getManufacturerName())))
      {
        if ((this.mProductName == null) || (paramUsbDevice.getProductName() == null) || (this.mProductName.equals(paramUsbDevice.getProductName())))
        {
          if ((this.mSerialNumber != null) && (paramUsbDevice.getSerialNumber() != null) && (!this.mSerialNumber.equals(paramUsbDevice.getSerialNumber()))) {
            break label199;
          }
          if (!matches(paramUsbDevice.getDeviceClass(), paramUsbDevice.getDeviceSubclass(), paramUsbDevice.getDeviceProtocol())) {
            break label201;
          }
          return true;
        }
      }
      else {
        return false;
      }
      return false;
      label199:
      return false;
      label201:
      int j = paramUsbDevice.getInterfaceCount();
      int i = 0;
      while (i < j)
      {
        UsbInterface localUsbInterface = paramUsbDevice.getInterface(i);
        if (matches(localUsbInterface.getInterfaceClass(), localUsbInterface.getInterfaceSubclass(), localUsbInterface.getInterfaceProtocol())) {
          return true;
        }
        i += 1;
      }
      return false;
    }
    
    public boolean matches(DeviceFilter paramDeviceFilter)
    {
      if ((this.mVendorId != -1) && (paramDeviceFilter.mVendorId != this.mVendorId)) {
        return false;
      }
      if ((this.mProductId != -1) && (paramDeviceFilter.mProductId != this.mProductId)) {
        return false;
      }
      if ((paramDeviceFilter.mManufacturerName != null) && (this.mManufacturerName == null)) {
        return false;
      }
      if ((paramDeviceFilter.mProductName != null) && (this.mProductName == null)) {
        return false;
      }
      if ((paramDeviceFilter.mSerialNumber != null) && (this.mSerialNumber == null)) {
        return false;
      }
      if ((this.mManufacturerName == null) || (paramDeviceFilter.mManufacturerName == null) || (this.mManufacturerName.equals(paramDeviceFilter.mManufacturerName)))
      {
        if ((this.mProductName == null) || (paramDeviceFilter.mProductName == null) || (this.mProductName.equals(paramDeviceFilter.mProductName)))
        {
          if ((this.mSerialNumber != null) && (paramDeviceFilter.mSerialNumber != null) && (!this.mSerialNumber.equals(paramDeviceFilter.mSerialNumber))) {
            break label195;
          }
          return matches(paramDeviceFilter.mClass, paramDeviceFilter.mSubclass, paramDeviceFilter.mProtocol);
        }
      }
      else {
        return false;
      }
      return false;
      label195:
      return false;
    }
    
    public String toString()
    {
      return "DeviceFilter[mVendorId=" + this.mVendorId + ",mProductId=" + this.mProductId + ",mClass=" + this.mClass + ",mSubclass=" + this.mSubclass + ",mProtocol=" + this.mProtocol + ",mManufacturerName=" + this.mManufacturerName + ",mProductName=" + this.mProductName + ",mSerialNumber=" + this.mSerialNumber + "]";
    }
    
    public void write(XmlSerializer paramXmlSerializer)
      throws IOException
    {
      paramXmlSerializer.startTag(null, "usb-device");
      if (this.mVendorId != -1) {
        paramXmlSerializer.attribute(null, "vendor-id", Integer.toString(this.mVendorId));
      }
      if (this.mProductId != -1) {
        paramXmlSerializer.attribute(null, "product-id", Integer.toString(this.mProductId));
      }
      if (this.mClass != -1) {
        paramXmlSerializer.attribute(null, "class", Integer.toString(this.mClass));
      }
      if (this.mSubclass != -1) {
        paramXmlSerializer.attribute(null, "subclass", Integer.toString(this.mSubclass));
      }
      if (this.mProtocol != -1) {
        paramXmlSerializer.attribute(null, "protocol", Integer.toString(this.mProtocol));
      }
      if (this.mManufacturerName != null) {
        paramXmlSerializer.attribute(null, "manufacturer-name", this.mManufacturerName);
      }
      if (this.mProductName != null) {
        paramXmlSerializer.attribute(null, "product-name", this.mProductName);
      }
      if (this.mSerialNumber != null) {
        paramXmlSerializer.attribute(null, "serial-number", this.mSerialNumber);
      }
      paramXmlSerializer.endTag(null, "usb-device");
    }
  }
  
  private class MyPackageMonitor
    extends PackageMonitor
  {
    private MyPackageMonitor() {}
    
    public void onPackageAdded(String paramString, int paramInt)
    {
      UsbSettingsManager.-wrap1(UsbSettingsManager.this, paramString);
    }
    
    public boolean onPackageChanged(String paramString, int paramInt, String[] paramArrayOfString)
    {
      UsbSettingsManager.-wrap1(UsbSettingsManager.this, paramString);
      return false;
    }
    
    public void onPackageRemoved(String paramString, int paramInt)
    {
      UsbSettingsManager.this.clearDefaults(paramString);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/usb/UsbSettingsManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */