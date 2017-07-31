package com.android.server.am;

import android.app.AppGlobals;
import android.app.IApplicationThread;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.ArrayMap;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.Xml;
import com.android.internal.util.FastXmlSerializer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public final class CompatModePackages
{
  public static final int COMPAT_FLAG_DONT_ASK = 1;
  public static final int COMPAT_FLAG_ENABLED = 2;
  private static final int MSG_WRITE = 300;
  private static final String TAG = "ActivityManager";
  private static final String TAG_CONFIGURATION = TAG + ActivityManagerDebugConfig.POSTFIX_CONFIGURATION;
  public static final int UNSUPPORTED_ZOOM_FLAG_DONT_NOTIFY = 4;
  private final AtomicFile mFile;
  private final CompatHandler mHandler;
  private final HashMap<String, Integer> mPackages = new HashMap();
  private final ActivityManagerService mService;
  
  public CompatModePackages(ActivityManagerService paramActivityManagerService, File paramFile, Handler paramHandler)
  {
    this.mService = paramActivityManagerService;
    this.mFile = new AtomicFile(new File(paramFile, "packages-compat.xml"));
    this.mHandler = new CompatHandler(paramHandler.getLooper());
    paramHandler = null;
    paramActivityManagerService = null;
    paramFile = null;
    for (;;)
    {
      try
      {
        localFileInputStream = this.mFile.openRead();
        paramFile = localFileInputStream;
        paramHandler = localFileInputStream;
        paramActivityManagerService = localFileInputStream;
        localXmlPullParser = Xml.newPullParser();
        paramFile = localFileInputStream;
        paramHandler = localFileInputStream;
        paramActivityManagerService = localFileInputStream;
        localXmlPullParser.setInput(localFileInputStream, StandardCharsets.UTF_8.name());
        paramFile = localFileInputStream;
        paramHandler = localFileInputStream;
        paramActivityManagerService = localFileInputStream;
        i = localXmlPullParser.getEventType();
        if ((i != 2) && (i != 1))
        {
          paramFile = localFileInputStream;
          paramHandler = localFileInputStream;
          paramActivityManagerService = localFileInputStream;
          i = localXmlPullParser.next();
          continue;
        }
        if (i == 1)
        {
          if (localFileInputStream != null) {}
          try
          {
            localFileInputStream.close();
            return;
          }
          catch (IOException paramActivityManagerService)
          {
            return;
          }
        }
        paramFile = localFileInputStream;
        paramHandler = localFileInputStream;
        paramActivityManagerService = localFileInputStream;
        if ("compat-packages".equals(localXmlPullParser.getName()))
        {
          paramFile = localFileInputStream;
          paramHandler = localFileInputStream;
          paramActivityManagerService = localFileInputStream;
          i = localXmlPullParser.next();
          if (i == 2)
          {
            paramFile = localFileInputStream;
            paramHandler = localFileInputStream;
            paramActivityManagerService = localFileInputStream;
            str1 = localXmlPullParser.getName();
            paramFile = localFileInputStream;
            paramHandler = localFileInputStream;
            paramActivityManagerService = localFileInputStream;
            if (localXmlPullParser.getDepth() == 2)
            {
              paramFile = localFileInputStream;
              paramHandler = localFileInputStream;
              paramActivityManagerService = localFileInputStream;
              if ("pkg".equals(str1))
              {
                paramFile = localFileInputStream;
                paramHandler = localFileInputStream;
                paramActivityManagerService = localFileInputStream;
                str1 = localXmlPullParser.getAttributeValue(null, "name");
                if (str1 != null)
                {
                  paramFile = localFileInputStream;
                  paramHandler = localFileInputStream;
                  paramActivityManagerService = localFileInputStream;
                  str2 = localXmlPullParser.getAttributeValue(null, "mode");
                  j = 0;
                  i = j;
                  if (str2 != null)
                  {
                    paramFile = localFileInputStream;
                    paramHandler = localFileInputStream;
                    paramActivityManagerService = localFileInputStream;
                  }
                }
              }
            }
          }
        }
      }
      catch (IOException paramHandler)
      {
        FileInputStream localFileInputStream;
        XmlPullParser localXmlPullParser;
        int i;
        String str1;
        String str2;
        int j;
        if (paramFile == null) {
          continue;
        }
        paramActivityManagerService = paramFile;
        Slog.w(TAG, "Error reading compat-packages", paramHandler);
        if (paramFile == null) {
          continue;
        }
        try
        {
          paramFile.close();
          return;
        }
        catch (IOException paramActivityManagerService)
        {
          return;
        }
      }
      catch (XmlPullParserException paramFile)
      {
        paramActivityManagerService = paramHandler;
        Slog.w(TAG, "Error reading compat-packages", paramFile);
        if (paramHandler == null) {
          continue;
        }
        try
        {
          paramHandler.close();
          return;
        }
        catch (IOException paramActivityManagerService)
        {
          return;
        }
      }
      finally
      {
        if (paramActivityManagerService == null) {
          break label494;
        }
      }
      try
      {
        i = Integer.parseInt(str2);
        paramFile = localFileInputStream;
        paramHandler = localFileInputStream;
        paramActivityManagerService = localFileInputStream;
        this.mPackages.put(str1, Integer.valueOf(i));
        paramFile = localFileInputStream;
        paramHandler = localFileInputStream;
        paramActivityManagerService = localFileInputStream;
        j = localXmlPullParser.next();
        i = j;
        if (j == 1) {
          if (localFileInputStream == null) {}
        }
      }
      catch (NumberFormatException paramActivityManagerService)
      {
        try
        {
          localFileInputStream.close();
          return;
        }
        catch (IOException paramActivityManagerService)
        {
          return;
        }
        paramActivityManagerService = paramActivityManagerService;
        i = j;
      }
    }
    try
    {
      paramActivityManagerService.close();
      label494:
      throw paramFile;
    }
    catch (IOException paramActivityManagerService)
    {
      for (;;) {}
    }
  }
  
  private int getPackageFlags(String paramString)
  {
    paramString = (Integer)this.mPackages.get(paramString);
    if (paramString != null) {
      return paramString.intValue();
    }
    return 0;
  }
  
  private void removePackage(String paramString)
  {
    if (this.mPackages.containsKey(paramString))
    {
      this.mPackages.remove(paramString);
      scheduleWrite();
    }
  }
  
  private void scheduleWrite()
  {
    this.mHandler.removeMessages(300);
    Message localMessage = this.mHandler.obtainMessage(300);
    this.mHandler.sendMessageDelayed(localMessage, 10000L);
  }
  
  private void setPackageScreenCompatModeLocked(ApplicationInfo paramApplicationInfo, int paramInt)
  {
    String str = paramApplicationInfo.packageName;
    int i = getPackageFlags(str);
    label82:
    Object localObject;
    label196:
    ActivityRecord localActivityRecord;
    label237:
    ProcessRecord localProcessRecord;
    switch (paramInt)
    {
    default: 
      Slog.w(TAG, "Unknown screen compat mode req #" + paramInt + "; ignoring");
      return;
    case 0: 
      paramInt = 0;
      if (paramInt != 0)
      {
        paramInt = i | 0x2;
        localObject = compatibilityInfoForPackageLocked(paramApplicationInfo);
        if (((CompatibilityInfo)localObject).alwaysSupportsScreen())
        {
          Slog.w(TAG, "Ignoring compat mode change of " + str + "; compatibility never needed");
          paramInt = 0;
        }
        if (((CompatibilityInfo)localObject).neverSupportsScreen())
        {
          Slog.w(TAG, "Ignoring compat mode change of " + str + "; compatibility always needed");
          paramInt = 0;
        }
        if (paramInt == i) {
          return;
        }
        if (paramInt == 0) {
          break label306;
        }
        this.mPackages.put(str, Integer.valueOf(paramInt));
        paramApplicationInfo = compatibilityInfoForPackageLocked(paramApplicationInfo);
        scheduleWrite();
        localObject = this.mService.getFocusedStack();
        localActivityRecord = ((ActivityStack)localObject).restartPackage(str);
        paramInt = this.mService.mLruProcesses.size() - 1;
        if (paramInt < 0) {
          break label395;
        }
        localProcessRecord = (ProcessRecord)this.mService.mLruProcesses.get(paramInt);
        if (localProcessRecord.pkgList.containsKey(str)) {
          break label319;
        }
      }
      break;
    }
    for (;;)
    {
      paramInt -= 1;
      break label237;
      paramInt = 1;
      break;
      if ((i & 0x2) == 0)
      {
        paramInt = 1;
        break;
      }
      paramInt = 0;
      break;
      paramInt = i & 0xFFFFFFFD;
      break label82;
      label306:
      this.mPackages.remove(str);
      break label196;
      try
      {
        label319:
        if (localProcessRecord.thread != null)
        {
          if (ActivityManagerDebugConfig.DEBUG_CONFIGURATION) {
            Slog.v(TAG_CONFIGURATION, "Sending to proc " + localProcessRecord.processName + " new compat " + paramApplicationInfo);
          }
          localProcessRecord.thread.updatePackageCompatibilityInfo(str, paramApplicationInfo);
        }
      }
      catch (Exception localException) {}
    }
    label395:
    if (localActivityRecord != null)
    {
      ((ActivityStack)localObject).ensureActivityConfigurationLocked(localActivityRecord, 0, false);
      ((ActivityStack)localObject).ensureActivitiesVisibleLocked(localActivityRecord, 0, false);
    }
  }
  
  public CompatibilityInfo compatibilityInfoForPackageLocked(ApplicationInfo paramApplicationInfo)
  {
    boolean bool = false;
    int i = this.mService.mConfiguration.screenLayout;
    int j = this.mService.mConfiguration.smallestScreenWidthDp;
    if ((getPackageFlags(paramApplicationInfo.packageName) & 0x2) != 0) {
      bool = true;
    }
    return new CompatibilityInfo(paramApplicationInfo, i, j, bool);
  }
  
  public int computeCompatModeLocked(ApplicationInfo paramApplicationInfo)
  {
    int i = 0;
    if ((getPackageFlags(paramApplicationInfo.packageName) & 0x2) != 0) {}
    for (boolean bool = true;; bool = false)
    {
      paramApplicationInfo = new CompatibilityInfo(paramApplicationInfo, this.mService.mConfiguration.screenLayout, this.mService.mConfiguration.smallestScreenWidthDp, bool);
      if (!paramApplicationInfo.alwaysSupportsScreen()) {
        break;
      }
      return -2;
    }
    if (paramApplicationInfo.neverSupportsScreen()) {
      return -1;
    }
    if (bool) {
      i = 1;
    }
    return i;
  }
  
  public boolean getFrontActivityAskCompatModeLocked()
  {
    ActivityRecord localActivityRecord = this.mService.getFocusedStack().topRunningActivityLocked();
    if (localActivityRecord == null) {
      return false;
    }
    return getPackageAskCompatModeLocked(localActivityRecord.packageName);
  }
  
  public int getFrontActivityScreenCompatModeLocked()
  {
    ActivityRecord localActivityRecord = this.mService.getFocusedStack().topRunningActivityLocked();
    if (localActivityRecord == null) {
      return -3;
    }
    return computeCompatModeLocked(localActivityRecord.info.applicationInfo);
  }
  
  public boolean getPackageAskCompatModeLocked(String paramString)
  {
    boolean bool = false;
    if ((getPackageFlags(paramString) & 0x1) == 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean getPackageNotifyUnsupportedZoomLocked(String paramString)
  {
    boolean bool = false;
    if ((getPackageFlags(paramString) & 0x4) == 0) {
      bool = true;
    }
    return bool;
  }
  
  public int getPackageScreenCompatModeLocked(String paramString)
  {
    Object localObject = null;
    try
    {
      paramString = AppGlobals.getPackageManager().getApplicationInfo(paramString, 0, 0);
      if (paramString == null) {
        return -3;
      }
      return computeCompatModeLocked(paramString);
    }
    catch (RemoteException paramString)
    {
      for (;;)
      {
        paramString = (String)localObject;
      }
    }
  }
  
  public HashMap<String, Integer> getPackages()
  {
    return this.mPackages;
  }
  
  public void handlePackageAddedLocked(String paramString, boolean paramBoolean)
  {
    Object localObject = null;
    try
    {
      ApplicationInfo localApplicationInfo = AppGlobals.getPackageManager().getApplicationInfo(paramString, 0, 0);
      localObject = localApplicationInfo;
    }
    catch (RemoteException localRemoteException)
    {
      int i;
      for (;;) {}
    }
    if (localObject == null) {
      return;
    }
    localObject = compatibilityInfoForPackageLocked((ApplicationInfo)localObject);
    if (!((CompatibilityInfo)localObject).alwaysSupportsScreen()) {
      if (((CompatibilityInfo)localObject).neverSupportsScreen()) {
        i = 0;
      }
    }
    for (;;)
    {
      if ((paramBoolean) && (i == 0) && (this.mPackages.containsKey(paramString)))
      {
        this.mPackages.remove(paramString);
        scheduleWrite();
      }
      return;
      i = 1;
      continue;
      i = 0;
    }
  }
  
  public void handlePackageDataClearedLocked(String paramString)
  {
    removePackage(paramString);
  }
  
  public void handlePackageUninstalledLocked(String paramString)
  {
    removePackage(paramString);
  }
  
  void saveCompatModes()
  {
    FileOutputStream localFileOutputStream;
    FastXmlSerializer localFastXmlSerializer;
    synchronized (this.mService)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      Object localObject3 = new HashMap(this.mPackages);
      ActivityManagerService.resetPriorityAfterLockedSection();
      ??? = null;
      label268:
      do
      {
        do
        {
          do
          {
            try
            {
              localFileOutputStream = this.mFile.startWrite();
              ??? = localFileOutputStream;
              localFastXmlSerializer = new FastXmlSerializer();
              ??? = localFileOutputStream;
              localFastXmlSerializer.setOutput(localFileOutputStream, StandardCharsets.UTF_8.name());
              ??? = localFileOutputStream;
              localFastXmlSerializer.startDocument(null, Boolean.valueOf(true));
              ??? = localFileOutputStream;
              localFastXmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
              ??? = localFileOutputStream;
              localFastXmlSerializer.startTag(null, "compat-packages");
              ??? = localFileOutputStream;
              localIPackageManager = AppGlobals.getPackageManager();
              ??? = localFileOutputStream;
              i = this.mService.mConfiguration.screenLayout;
              ??? = localFileOutputStream;
              j = this.mService.mConfiguration.smallestScreenWidthDp;
              ??? = localFileOutputStream;
              Iterator localIterator = ((HashMap)localObject3).entrySet().iterator();
              do
              {
                ??? = localFileOutputStream;
                if (!localIterator.hasNext()) {
                  break;
                }
                ??? = localFileOutputStream;
                localObject3 = (Map.Entry)localIterator.next();
                ??? = localFileOutputStream;
                str = (String)((Map.Entry)localObject3).getKey();
                ??? = localFileOutputStream;
                k = ((Integer)((Map.Entry)localObject3).getValue()).intValue();
              } while (k == 0);
              localObject3 = null;
              ??? = localFileOutputStream;
            }
            catch (IOException localIOException)
            {
              IPackageManager localIPackageManager;
              int i;
              int j;
              String str;
              int k;
              ApplicationInfo localApplicationInfo;
              Slog.w(TAG, "Error writing compat packages", localIOException);
              if (??? != null) {
                this.mFile.failWrite((FileOutputStream)???);
              }
              return;
            }
            try
            {
              localApplicationInfo = localIPackageManager.getApplicationInfo(str, 0, 0);
              localObject3 = localApplicationInfo;
            }
            catch (RemoteException localRemoteException)
            {
              break label268;
            }
          } while (localObject3 == null);
          ??? = localFileOutputStream;
          localObject3 = new CompatibilityInfo((ApplicationInfo)localObject3, i, j, false);
          ??? = localFileOutputStream;
        } while (((CompatibilityInfo)localObject3).alwaysSupportsScreen());
        ??? = localFileOutputStream;
      } while (((CompatibilityInfo)localObject3).neverSupportsScreen());
      ??? = localFileOutputStream;
      localFastXmlSerializer.startTag(null, "pkg");
      ??? = localFileOutputStream;
      localFastXmlSerializer.attribute(null, "name", str);
      ??? = localFileOutputStream;
      localFastXmlSerializer.attribute(null, "mode", Integer.toString(k));
      ??? = localFileOutputStream;
      localFastXmlSerializer.endTag(null, "pkg");
    }
    Object localObject2 = localFileOutputStream;
    localFastXmlSerializer.endTag(null, "compat-packages");
    localObject2 = localFileOutputStream;
    localFastXmlSerializer.endDocument();
    localObject2 = localFileOutputStream;
    this.mFile.finishWrite(localFileOutputStream);
  }
  
  public void setFrontActivityAskCompatModeLocked(boolean paramBoolean)
  {
    ActivityRecord localActivityRecord = this.mService.getFocusedStack().topRunningActivityLocked();
    if (localActivityRecord != null) {
      setPackageAskCompatModeLocked(localActivityRecord.packageName, paramBoolean);
    }
  }
  
  public void setFrontActivityScreenCompatModeLocked(int paramInt)
  {
    ActivityRecord localActivityRecord = this.mService.getFocusedStack().topRunningActivityLocked();
    if (localActivityRecord == null)
    {
      Slog.w(TAG, "setFrontActivityScreenCompatMode failed: no top activity");
      return;
    }
    setPackageScreenCompatModeLocked(localActivityRecord.info.applicationInfo, paramInt);
  }
  
  public void setPackageAskCompatModeLocked(String paramString, boolean paramBoolean)
  {
    int j = getPackageFlags(paramString);
    int i;
    if (paramBoolean)
    {
      i = j & 0xFFFFFFFE;
      if (j != i)
      {
        if (i == 0) {
          break label53;
        }
        this.mPackages.put(paramString, Integer.valueOf(i));
      }
    }
    for (;;)
    {
      scheduleWrite();
      return;
      i = j | 0x1;
      break;
      label53:
      this.mPackages.remove(paramString);
    }
  }
  
  public void setPackageNotifyUnsupportedZoomLocked(String paramString, boolean paramBoolean)
  {
    int j = getPackageFlags(paramString);
    int i;
    if (paramBoolean)
    {
      i = j & 0xFFFFFFFB;
      if (j != i)
      {
        if (i == 0) {
          break label53;
        }
        this.mPackages.put(paramString, Integer.valueOf(i));
      }
    }
    for (;;)
    {
      scheduleWrite();
      return;
      i = j | 0x4;
      break;
      label53:
      this.mPackages.remove(paramString);
    }
  }
  
  public void setPackageScreenCompatModeLocked(String paramString, int paramInt)
  {
    Object localObject = null;
    try
    {
      ApplicationInfo localApplicationInfo = AppGlobals.getPackageManager().getApplicationInfo(paramString, 0, 0);
      localObject = localApplicationInfo;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;) {}
    }
    if (localObject == null)
    {
      Slog.w(TAG, "setPackageScreenCompatMode failed: unknown package " + paramString);
      return;
    }
    setPackageScreenCompatModeLocked((ApplicationInfo)localObject, paramInt);
  }
  
  private final class CompatHandler
    extends Handler
  {
    public CompatHandler(Looper paramLooper)
    {
      super(null, true);
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      }
      CompatModePackages.this.saveCompatModes();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/CompatModePackages.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */