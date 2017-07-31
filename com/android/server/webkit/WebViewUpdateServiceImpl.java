package com.android.server.webkit;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Slog;
import android.webkit.WebViewFactory;
import android.webkit.WebViewFactory.MissingWebViewPackageException;
import android.webkit.WebViewProviderInfo;
import android.webkit.WebViewProviderResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WebViewUpdateServiceImpl
{
  private static final String TAG = WebViewUpdateServiceImpl.class.getSimpleName();
  private Context mContext;
  private SystemInterface mSystemInterface;
  private WebViewUpdater mWebViewUpdater;
  
  public WebViewUpdateServiceImpl(Context paramContext, SystemInterface paramSystemInterface)
  {
    this.mContext = paramContext;
    this.mSystemInterface = paramSystemInterface;
    this.mWebViewUpdater = new WebViewUpdater(this.mContext, this.mSystemInterface);
  }
  
  private boolean existsValidNonFallbackProvider(WebViewProviderInfo[] paramArrayOfWebViewProviderInfo)
  {
    int j = paramArrayOfWebViewProviderInfo.length;
    int i = 0;
    WebViewProviderInfo localWebViewProviderInfo;
    if (i < j)
    {
      localWebViewProviderInfo = paramArrayOfWebViewProviderInfo[i];
      if ((localWebViewProviderInfo.availableByDefault) && (!localWebViewProviderInfo.isFallback)) {}
    }
    for (;;)
    {
      i += 1;
      break;
      try
      {
        PackageInfo localPackageInfo = this.mSystemInterface.getPackageInfoForProvider(localWebViewProviderInfo);
        if ((!isInstalledPackage(localPackageInfo)) || (!isEnabledPackage(localPackageInfo))) {
          continue;
        }
        boolean bool = this.mWebViewUpdater.isValidProvider(localWebViewProviderInfo, localPackageInfo);
        if (!bool) {
          continue;
        }
        return true;
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException) {}
      return false;
    }
  }
  
  private static WebViewProviderInfo getFallbackProvider(WebViewProviderInfo[] paramArrayOfWebViewProviderInfo)
  {
    int i = 0;
    int j = paramArrayOfWebViewProviderInfo.length;
    while (i < j)
    {
      WebViewProviderInfo localWebViewProviderInfo = paramArrayOfWebViewProviderInfo[i];
      if (localWebViewProviderInfo.isFallback) {
        return localWebViewProviderInfo;
      }
      i += 1;
    }
    return null;
  }
  
  private static boolean isEnabledPackage(PackageInfo paramPackageInfo)
  {
    return paramPackageInfo.applicationInfo.enabled;
  }
  
  private static boolean isInstalledPackage(PackageInfo paramPackageInfo)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if ((paramPackageInfo.applicationInfo.flags & 0x800000) != 0)
    {
      bool1 = bool2;
      if ((paramPackageInfo.applicationInfo.privateFlags & 0x1) == 0) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private static boolean providerHasValidSignature(WebViewProviderInfo paramWebViewProviderInfo, PackageInfo paramPackageInfo, SystemInterface paramSystemInterface)
  {
    if (paramSystemInterface.systemIsDebuggable()) {
      return true;
    }
    if ((paramWebViewProviderInfo.signatures == null) || (paramWebViewProviderInfo.signatures.length == 0)) {
      return paramPackageInfo.applicationInfo.isSystemApp();
    }
    paramPackageInfo = paramPackageInfo.signatures;
    if (paramPackageInfo.length != 1) {
      return false;
    }
    paramPackageInfo = paramPackageInfo[0].toByteArray();
    paramWebViewProviderInfo = paramWebViewProviderInfo.signatures;
    int j = paramWebViewProviderInfo.length;
    int i = 0;
    while (i < j)
    {
      if (Arrays.equals(paramPackageInfo, Base64.decode(paramWebViewProviderInfo[i], 0))) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private void updateFallbackState(WebViewProviderInfo[] paramArrayOfWebViewProviderInfo, boolean paramBoolean)
  {
    WebViewProviderInfo localWebViewProviderInfo = getFallbackProvider(paramArrayOfWebViewProviderInfo);
    if (localWebViewProviderInfo == null) {
      return;
    }
    boolean bool1 = existsValidNonFallbackProvider(paramArrayOfWebViewProviderInfo);
    boolean bool2;
    do
    {
      try
      {
        bool2 = isEnabledPackage(this.mSystemInterface.getPackageInfoForProvider(localWebViewProviderInfo));
        if ((bool1) && ((bool2) || (paramBoolean)))
        {
          this.mSystemInterface.uninstallAndDisablePackageForAllUsers(this.mContext, localWebViewProviderInfo.packageName);
          return;
        }
      }
      catch (PackageManager.NameNotFoundException paramArrayOfWebViewProviderInfo)
      {
        return;
      }
    } while ((bool1) || ((bool2) && (!paramBoolean)));
    this.mSystemInterface.enablePackageForAllUsers(this.mContext, localWebViewProviderInfo.packageName, true);
  }
  
  private void updateFallbackStateOnBoot()
  {
    if (!this.mSystemInterface.isFallbackLogicEnabled()) {
      return;
    }
    updateFallbackState(this.mSystemInterface.getWebViewPackages(), true);
  }
  
  private void updateFallbackStateOnPackageChange(String paramString, int paramInt)
  {
    if (!this.mSystemInterface.isFallbackLogicEnabled()) {
      return;
    }
    WebViewProviderInfo[] arrayOfWebViewProviderInfo = this.mSystemInterface.getWebViewPackages();
    int j = 0;
    int k = arrayOfWebViewProviderInfo.length;
    paramInt = 0;
    for (;;)
    {
      int i = j;
      if (paramInt < k)
      {
        WebViewProviderInfo localWebViewProviderInfo = arrayOfWebViewProviderInfo[paramInt];
        if (!localWebViewProviderInfo.packageName.equals(paramString)) {
          break label79;
        }
        i = j;
        if (localWebViewProviderInfo.availableByDefault) {
          i = 1;
        }
      }
      if (i != 0) {
        break;
      }
      return;
      label79:
      paramInt += 1;
    }
    updateFallbackState(arrayOfWebViewProviderInfo, false);
  }
  
  String changeProviderAndSetting(String paramString)
  {
    return this.mWebViewUpdater.changeProviderAndSetting(paramString);
  }
  
  void enableFallbackLogic(boolean paramBoolean)
  {
    this.mSystemInterface.enableFallbackLogic(paramBoolean);
  }
  
  String getCurrentWebViewPackageName()
  {
    return this.mWebViewUpdater.getCurrentWebViewPackageName();
  }
  
  WebViewProviderInfo[] getValidWebViewPackages()
  {
    return this.mWebViewUpdater.getValidAndInstalledWebViewPackages();
  }
  
  WebViewProviderInfo[] getWebViewPackages()
  {
    return this.mSystemInterface.getWebViewPackages();
  }
  
  void handleNewUser(int paramInt)
  {
    if (!this.mSystemInterface.isFallbackLogicEnabled()) {
      return;
    }
    WebViewProviderInfo[] arrayOfWebViewProviderInfo = this.mSystemInterface.getWebViewPackages();
    Object localObject = getFallbackProvider(arrayOfWebViewProviderInfo);
    if (localObject == null) {
      return;
    }
    SystemInterface localSystemInterface = this.mSystemInterface;
    localObject = ((WebViewProviderInfo)localObject).packageName;
    if (existsValidNonFallbackProvider(arrayOfWebViewProviderInfo)) {}
    for (boolean bool = false;; bool = true)
    {
      localSystemInterface.enablePackageForUser((String)localObject, bool, paramInt);
      return;
    }
  }
  
  boolean isFallbackPackage(String paramString)
  {
    boolean bool = false;
    if ((paramString != null) && (this.mSystemInterface.isFallbackLogicEnabled()))
    {
      WebViewProviderInfo localWebViewProviderInfo = getFallbackProvider(this.mSystemInterface.getWebViewPackages());
      if (localWebViewProviderInfo != null) {
        bool = paramString.equals(localWebViewProviderInfo.packageName);
      }
      return bool;
    }
    return false;
  }
  
  void notifyRelroCreationCompleted()
  {
    this.mWebViewUpdater.notifyRelroCreationCompleted();
  }
  
  void packageStateChanged(String paramString, int paramInt1, int paramInt2)
  {
    updateFallbackStateOnPackageChange(paramString, paramInt1);
    this.mWebViewUpdater.packageStateChanged(paramString, paramInt1);
  }
  
  void prepareWebViewInSystemServer()
  {
    updateFallbackStateOnBoot();
    this.mWebViewUpdater.prepareWebViewInSystemServer();
  }
  
  WebViewProviderResponse waitForAndGetProvider()
  {
    return this.mWebViewUpdater.waitForAndGetProvider();
  }
  
  private static class WebViewUpdater
  {
    private static final int WAIT_TIMEOUT_MS = 1000;
    private int NUMBER_OF_RELROS_UNKNOWN = Integer.MAX_VALUE;
    private boolean mAnyWebViewInstalled = false;
    private Context mContext;
    private PackageInfo mCurrentWebViewPackage = null;
    private Object mLock = new Object();
    private int mMinimumVersionCode = -1;
    private int mNumRelroCreationsFinished = 0;
    private int mNumRelroCreationsStarted = 0;
    private SystemInterface mSystemInterface;
    private boolean mWebViewPackageDirty = false;
    
    public WebViewUpdater(Context paramContext, SystemInterface paramSystemInterface)
    {
      this.mContext = paramContext;
      this.mSystemInterface = paramSystemInterface;
    }
    
    private void checkIfRelrosDoneLocked()
    {
      if (this.mNumRelroCreationsStarted == this.mNumRelroCreationsFinished)
      {
        if (!this.mWebViewPackageDirty) {
          break label32;
        }
        this.mWebViewPackageDirty = false;
      }
      label32:
      try
      {
        onWebViewProviderChanged(findPreferredWebViewPackage());
        return;
      }
      catch (WebViewFactory.MissingWebViewPackageException localMissingWebViewPackageException) {}
      this.mLock.notifyAll();
      return;
    }
    
    private PackageInfo findPreferredWebViewPackage()
    {
      ProviderAndPackageInfo[] arrayOfProviderAndPackageInfo = getValidWebViewPackagesAndInfos(false);
      Object localObject = this.mSystemInterface.getUserChosenWebViewProvider(this.mContext);
      int j = arrayOfProviderAndPackageInfo.length;
      int i = 0;
      while (i < j)
      {
        ProviderAndPackageInfo localProviderAndPackageInfo = arrayOfProviderAndPackageInfo[i];
        if ((localProviderAndPackageInfo.provider.packageName.equals(localObject)) && (WebViewUpdateServiceImpl.-wrap1(localProviderAndPackageInfo.packageInfo)) && (WebViewUpdateServiceImpl.-wrap0(localProviderAndPackageInfo.packageInfo))) {
          return localProviderAndPackageInfo.packageInfo;
        }
        i += 1;
      }
      j = arrayOfProviderAndPackageInfo.length;
      i = 0;
      while (i < j)
      {
        localObject = arrayOfProviderAndPackageInfo[i];
        if ((((ProviderAndPackageInfo)localObject).provider.availableByDefault) && (WebViewUpdateServiceImpl.-wrap1(((ProviderAndPackageInfo)localObject).packageInfo)) && (WebViewUpdateServiceImpl.-wrap0(((ProviderAndPackageInfo)localObject).packageInfo))) {
          return ((ProviderAndPackageInfo)localObject).packageInfo;
        }
        i += 1;
      }
      j = arrayOfProviderAndPackageInfo.length;
      i = 0;
      while (i < j)
      {
        localObject = arrayOfProviderAndPackageInfo[i];
        if (((ProviderAndPackageInfo)localObject).provider.availableByDefault) {
          return ((ProviderAndPackageInfo)localObject).packageInfo;
        }
        i += 1;
      }
      this.mAnyWebViewInstalled = false;
      throw new WebViewFactory.MissingWebViewPackageException("Could not find a loadable WebView package");
    }
    
    private int getMinimumVersionCode()
    {
      int i = 0;
      if (this.mMinimumVersionCode > 0) {
        return this.mMinimumVersionCode;
      }
      WebViewProviderInfo[] arrayOfWebViewProviderInfo = this.mSystemInterface.getWebViewPackages();
      int j = arrayOfWebViewProviderInfo.length;
      if (i < j)
      {
        WebViewProviderInfo localWebViewProviderInfo = arrayOfWebViewProviderInfo[i];
        if ((!localWebViewProviderInfo.availableByDefault) || (localWebViewProviderInfo.isFallback)) {}
        for (;;)
        {
          i += 1;
          break;
          try
          {
            int k = this.mSystemInterface.getFactoryPackageVersion(localWebViewProviderInfo.packageName);
            if ((this.mMinimumVersionCode < 0) || (k < this.mMinimumVersionCode)) {
              this.mMinimumVersionCode = k;
            }
          }
          catch (PackageManager.NameNotFoundException localNameNotFoundException) {}
        }
      }
      return this.mMinimumVersionCode;
    }
    
    private ProviderAndPackageInfo[] getValidWebViewPackagesAndInfos(boolean paramBoolean)
    {
      WebViewProviderInfo[] arrayOfWebViewProviderInfo = this.mSystemInterface.getWebViewPackages();
      ArrayList localArrayList = new ArrayList();
      int i = 0;
      while (i < arrayOfWebViewProviderInfo.length)
      {
        try
        {
          PackageInfo localPackageInfo = this.mSystemInterface.getPackageInfoForProvider(arrayOfWebViewProviderInfo[i]);
          if (((!paramBoolean) || (WebViewUpdateServiceImpl.-wrap1(localPackageInfo))) && (isValidProvider(arrayOfWebViewProviderInfo[i], localPackageInfo))) {
            localArrayList.add(new ProviderAndPackageInfo(arrayOfWebViewProviderInfo[i], localPackageInfo));
          }
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {
          for (;;) {}
        }
        i += 1;
      }
      return (ProviderAndPackageInfo[])localArrayList.toArray(new ProviderAndPackageInfo[localArrayList.size()]);
    }
    
    private void onWebViewProviderChanged(PackageInfo paramPackageInfo)
    {
      synchronized (this.mLock)
      {
        this.mAnyWebViewInstalled = true;
        if (this.mNumRelroCreationsStarted == this.mNumRelroCreationsFinished)
        {
          this.mCurrentWebViewPackage = paramPackageInfo;
          this.mNumRelroCreationsStarted = this.NUMBER_OF_RELROS_UNKNOWN;
          this.mNumRelroCreationsFinished = 0;
          this.mNumRelroCreationsStarted = this.mSystemInterface.onWebViewProviderChanged(paramPackageInfo);
          checkIfRelrosDoneLocked();
          return;
        }
        this.mWebViewPackageDirty = true;
      }
    }
    
    private static boolean versionCodeGE(int paramInt1, int paramInt2)
    {
      return paramInt1 / 100000 >= paramInt2 / 100000;
    }
    
    private boolean webViewIsReadyLocked()
    {
      if ((!this.mWebViewPackageDirty) && (this.mNumRelroCreationsStarted == this.mNumRelroCreationsFinished)) {
        return this.mAnyWebViewInstalled;
      }
      return false;
    }
    
    public String changeProviderAndSetting(String paramString)
    {
      synchronized (this.mLock)
      {
        PackageInfo localPackageInfo = this.mCurrentWebViewPackage;
        this.mSystemInterface.updateUserSetting(this.mContext, paramString);
        try
        {
          paramString = findPreferredWebViewPackage();
          int i;
          if (localPackageInfo != null)
          {
            boolean bool = paramString.packageName.equals(localPackageInfo.packageName);
            if (!bool) {
              break label103;
            }
            i = 0;
          }
          for (;;)
          {
            if (i != 0) {
              onWebViewProviderChanged(paramString);
            }
            if ((i != 0) && (localPackageInfo != null)) {
              this.mSystemInterface.killPackageDependents(localPackageInfo.packageName);
            }
            return paramString.packageName;
            i = 1;
            continue;
            label103:
            i = 1;
          }
          paramString = finally;
        }
        catch (WebViewFactory.MissingWebViewPackageException paramString)
        {
          Slog.e(WebViewUpdateServiceImpl.-get0(), "Tried to change WebView provider but failed to fetch WebView package " + paramString);
          return "";
        }
      }
    }
    
    public String getCurrentWebViewPackageName()
    {
      synchronized (this.mLock)
      {
        Object localObject2 = this.mCurrentWebViewPackage;
        if (localObject2 == null) {
          return null;
        }
        localObject2 = this.mCurrentWebViewPackage.packageName;
        return (String)localObject2;
      }
    }
    
    public WebViewProviderInfo[] getValidAndInstalledWebViewPackages()
    {
      ProviderAndPackageInfo[] arrayOfProviderAndPackageInfo = getValidWebViewPackagesAndInfos(true);
      WebViewProviderInfo[] arrayOfWebViewProviderInfo = new WebViewProviderInfo[arrayOfProviderAndPackageInfo.length];
      int i = 0;
      while (i < arrayOfProviderAndPackageInfo.length)
      {
        arrayOfWebViewProviderInfo[i] = arrayOfProviderAndPackageInfo[i].provider;
        i += 1;
      }
      return arrayOfWebViewProviderInfo;
    }
    
    public boolean isValidProvider(WebViewProviderInfo paramWebViewProviderInfo, PackageInfo paramPackageInfo)
    {
      if ((versionCodeGE(paramPackageInfo.versionCode, getMinimumVersionCode())) || (this.mSystemInterface.systemIsDebuggable()))
      {
        if ((WebViewUpdateServiceImpl.-wrap2(paramWebViewProviderInfo, paramPackageInfo, this.mSystemInterface)) && (WebViewFactory.getWebViewLibrary(paramPackageInfo.applicationInfo) != null)) {
          return true;
        }
      }
      else {
        return false;
      }
      return false;
    }
    
    public void notifyRelroCreationCompleted()
    {
      synchronized (this.mLock)
      {
        this.mNumRelroCreationsFinished += 1;
        checkIfRelrosDoneLocked();
        return;
      }
    }
    
    public void packageStateChanged(String paramString, int paramInt)
    {
      Object localObject1 = this.mSystemInterface.getWebViewPackages();
      int i = 0;
      int j = localObject1.length;
      while (i < j)
      {
        Object localObject3 = localObject1[i];
        if (((WebViewProviderInfo)localObject3).packageName.equals(paramString))
        {
          j = 0;
          boolean bool2 = false;
          String str = null;
          localObject1 = null;
          Object localObject2 = this.mLock;
          paramString = str;
          boolean bool1 = bool2;
          i = j;
          try
          {
            PackageInfo localPackageInfo = findPreferredWebViewPackage();
            paramString = str;
            bool1 = bool2;
            i = j;
            if (this.mCurrentWebViewPackage != null)
            {
              paramString = str;
              bool1 = bool2;
              i = j;
              str = this.mCurrentWebViewPackage.packageName;
              if (paramInt == 0)
              {
                paramString = str;
                bool1 = bool2;
                i = j;
                boolean bool3 = localPackageInfo.packageName.equals(str);
                if (bool3) {
                  return;
                }
              }
              localObject1 = str;
              paramString = str;
              bool1 = bool2;
              i = j;
              if (localPackageInfo.packageName.equals(str))
              {
                paramString = str;
                bool1 = bool2;
                i = j;
                long l1 = localPackageInfo.lastUpdateTime;
                paramString = str;
                bool1 = bool2;
                i = j;
                long l2 = this.mCurrentWebViewPackage.lastUpdateTime;
                localObject1 = str;
                if (l1 == l2) {
                  return;
                }
              }
            }
            paramString = (String)localObject1;
            bool1 = bool2;
            i = j;
            if (((WebViewProviderInfo)localObject3).packageName.equals(localPackageInfo.packageName)) {
              break label368;
            }
            paramString = (String)localObject1;
            bool1 = bool2;
            i = j;
            if (((WebViewProviderInfo)localObject3).packageName.equals(localObject1)) {
              break label368;
            }
            paramString = (String)localObject1;
            bool1 = bool2;
            i = j;
            if (this.mCurrentWebViewPackage != null) {
              break label373;
            }
            paramInt = 1;
            paramString = (String)localObject1;
            bool1 = bool2;
            i = paramInt;
            bool2 = ((WebViewProviderInfo)localObject3).packageName.equals(localObject1);
            paramString = (String)localObject1;
            bool1 = bool2;
            i = paramInt;
            if (paramInt != 0)
            {
              paramString = (String)localObject1;
              bool1 = bool2;
              i = paramInt;
              onWebViewProviderChanged(localPackageInfo);
              i = paramInt;
              bool1 = bool2;
              paramString = (String)localObject1;
            }
          }
          catch (WebViewFactory.MissingWebViewPackageException localMissingWebViewPackageException)
          {
            for (;;)
            {
              Slog.e(WebViewUpdateServiceImpl.-get0(), "Could not find valid WebView package to create relro with " + localMissingWebViewPackageException);
            }
          }
          finally {}
          if ((i == 0) || (bool1)) {}
          label368:
          label373:
          while (paramString == null)
          {
            return;
            paramInt = 1;
            break;
            paramInt = 0;
            break;
          }
          this.mSystemInterface.killPackageDependents(paramString);
          return;
        }
        i += 1;
      }
    }
    
    public void prepareWebViewInSystemServer()
    {
      try
      {
        synchronized (this.mLock)
        {
          this.mCurrentWebViewPackage = findPreferredWebViewPackage();
          this.mSystemInterface.updateUserSetting(this.mContext, this.mCurrentWebViewPackage.packageName);
          onWebViewProviderChanged(this.mCurrentWebViewPackage);
          return;
        }
        return;
      }
      catch (Throwable localThrowable)
      {
        Slog.e(WebViewUpdateServiceImpl.-get0(), "error preparing webview provider from system server", localThrowable);
      }
    }
    
    public WebViewProviderResponse waitForAndGetProvider()
    {
      long l1 = System.nanoTime() / 1000000L + 1000L;
      int i = 0;
      for (;;)
      {
        boolean bool;
        long l2;
        synchronized (this.mLock)
        {
          bool = webViewIsReadyLocked();
          if (!bool)
          {
            l2 = System.nanoTime() / 1000000L;
            if (l2 < l1) {}
          }
          else
          {
            PackageInfo localPackageInfo = this.mCurrentWebViewPackage;
            if (!bool) {
              break label110;
            }
            if (!bool) {
              Slog.w(WebViewUpdateServiceImpl.-get0(), "creating relro file timed out");
            }
            return new WebViewProviderResponse(localPackageInfo, i);
          }
        }
        try
        {
          this.mLock.wait(l1 - l2);
          bool = webViewIsReadyLocked();
          continue;
          label110:
          if (!this.mAnyWebViewInstalled)
          {
            i = 4;
            continue;
          }
          i = 3;
          Slog.e(WebViewUpdateServiceImpl.-get0(), "Timed out waiting for relro creation, relros started " + this.mNumRelroCreationsStarted + " relros finished " + this.mNumRelroCreationsFinished + " package dirty? " + this.mWebViewPackageDirty);
          continue;
          localObject2 = finally;
          throw ((Throwable)localObject2);
        }
        catch (InterruptedException localInterruptedException)
        {
          for (;;) {}
        }
      }
    }
    
    private static class ProviderAndPackageInfo
    {
      public final PackageInfo packageInfo;
      public final WebViewProviderInfo provider;
      
      public ProviderAndPackageInfo(WebViewProviderInfo paramWebViewProviderInfo, PackageInfo paramPackageInfo)
      {
        this.provider = paramWebViewProviderInfo;
        this.packageInfo = paramPackageInfo;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/webkit/WebViewUpdateServiceImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */