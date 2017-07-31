package com.android.server.webkit;

import android.app.ActivityManagerNative;
import android.app.AppGlobals;
import android.app.Application;
import android.app.IActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDeleteObserver.Stub;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.os.RemoteException;
import android.os.UserManager;
import android.provider.Settings.Global;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.webkit.WebViewFactory;
import android.webkit.WebViewProviderInfo;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

public class SystemImpl
  implements SystemInterface
{
  private static final int PACKAGE_FLAGS = 268443840;
  private static final String TAG = SystemImpl.class.getSimpleName();
  private static final String TAG_AVAILABILITY = "availableByDefault";
  private static final String TAG_DESCRIPTION = "description";
  private static final String TAG_FALLBACK = "isFallback";
  private static final String TAG_PACKAGE_NAME = "packageName";
  private static final String TAG_SIGNATURE = "signature";
  private static final String TAG_START = "webviewproviders";
  private static final String TAG_WEBVIEW_PROVIDER = "webviewprovider";
  private final WebViewProviderInfo[] mWebViewProviderPackages;
  
  private SystemImpl()
  {
    int m = 0;
    int k = 0;
    int j = 0;
    Object localObject4 = null;
    Object localObject1 = null;
    ArrayList localArrayList = new ArrayList();
    for (;;)
    {
      try
      {
        XmlResourceParser localXmlResourceParser = AppGlobals.getInitialApplication().getResources().getXml(17891332);
        localObject1 = localXmlResourceParser;
        localObject4 = localXmlResourceParser;
        XmlUtils.beginDocument(localXmlResourceParser, "webviewproviders");
        localObject1 = localXmlResourceParser;
        localObject4 = localXmlResourceParser;
        XmlUtils.nextElement(localXmlResourceParser);
        localObject1 = localXmlResourceParser;
        localObject4 = localXmlResourceParser;
        localObject5 = localXmlResourceParser.getName();
        if (localObject5 == null)
        {
          if (localXmlResourceParser != null) {
            localXmlResourceParser.close();
          }
          if (k != 0) {
            break;
          }
          throw new AndroidRuntimeException("There must be at least one WebView package that is available by default");
        }
        localObject1 = localXmlResourceParser;
        localObject4 = localXmlResourceParser;
        if (!((String)localObject5).equals("webviewprovider")) {
          break label498;
        }
        localObject1 = localXmlResourceParser;
        localObject4 = localXmlResourceParser;
        localObject5 = localXmlResourceParser.getAttributeValue(null, "packageName");
        if (localObject5 == null)
        {
          localObject1 = localXmlResourceParser;
          localObject4 = localXmlResourceParser;
          throw new AndroidRuntimeException("WebView provider in framework resources missing package name");
        }
      }
      catch (XmlPullParserException|IOException localXmlPullParserException)
      {
        localObject4 = localObject1;
        throw new AndroidRuntimeException("Error when parsing WebView config " + localXmlPullParserException);
      }
      finally
      {
        if (localObject4 != null) {
          ((XmlResourceParser)localObject4).close();
        }
      }
      Object localObject3 = localXmlPullParserException;
      localObject4 = localXmlPullParserException;
      String str = localXmlPullParserException.getAttributeValue(null, "description");
      if (str == null)
      {
        localObject3 = localXmlPullParserException;
        localObject4 = localXmlPullParserException;
        throw new AndroidRuntimeException("WebView provider in framework resources missing description");
      }
      localObject3 = localXmlPullParserException;
      localObject4 = localXmlPullParserException;
      Object localObject5 = new WebViewProviderInfo((String)localObject5, str, "true".equals(localXmlPullParserException.getAttributeValue(null, "availableByDefault")), "true".equals(localXmlPullParserException.getAttributeValue(null, "isFallback")), readSignatures(localXmlPullParserException));
      localObject3 = localXmlPullParserException;
      localObject4 = localXmlPullParserException;
      int i = m;
      if (((WebViewProviderInfo)localObject5).isFallback)
      {
        m += 1;
        localObject3 = localXmlPullParserException;
        localObject4 = localXmlPullParserException;
        if (!((WebViewProviderInfo)localObject5).availableByDefault)
        {
          localObject3 = localXmlPullParserException;
          localObject4 = localXmlPullParserException;
          throw new AndroidRuntimeException("Each WebView fallback package must be available by default.");
        }
        i = m;
        if (m > 1)
        {
          localObject3 = localXmlPullParserException;
          localObject4 = localXmlPullParserException;
          throw new AndroidRuntimeException("There can be at most one WebView fallback package.");
        }
      }
      localObject3 = localXmlPullParserException;
      localObject4 = localXmlPullParserException;
      int n = j;
      m = k;
      if (((WebViewProviderInfo)localObject5).availableByDefault)
      {
        k += 1;
        localObject3 = localXmlPullParserException;
        localObject4 = localXmlPullParserException;
        n = j;
        m = k;
        if (!((WebViewProviderInfo)localObject5).isFallback)
        {
          n = j + 1;
          m = k;
        }
      }
      localObject3 = localXmlPullParserException;
      localObject4 = localXmlPullParserException;
      localArrayList.add(localObject5);
      j = n;
      k = m;
      m = i;
      continue;
      label498:
      localObject3 = localXmlPullParserException;
      localObject4 = localXmlPullParserException;
      Log.e(TAG, "Found an element that is not a WebView provider");
    }
    if (j == 0) {
      throw new AndroidRuntimeException("There must be at least one WebView package that is available by default and not a fallback");
    }
    this.mWebViewProviderPackages = ((WebViewProviderInfo[])localArrayList.toArray(new WebViewProviderInfo[localArrayList.size()]));
  }
  
  public static SystemImpl getInstance()
  {
    return LazyHolder.-get0();
  }
  
  private static String[] readSignatures(XmlResourceParser paramXmlResourceParser)
    throws IOException, XmlPullParserException
  {
    ArrayList localArrayList = new ArrayList();
    int i = paramXmlResourceParser.getDepth();
    while (XmlUtils.nextElementWithin(paramXmlResourceParser, i)) {
      if (paramXmlResourceParser.getName().equals("signature")) {
        localArrayList.add(paramXmlResourceParser.nextText());
      } else {
        Log.e(TAG, "Found an element in a webview provider that is not a signature");
      }
    }
    return (String[])localArrayList.toArray(new String[localArrayList.size()]);
  }
  
  public void enableFallbackLogic(boolean paramBoolean)
  {
    ContentResolver localContentResolver = AppGlobals.getInitialApplication().getContentResolver();
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      Settings.Global.putInt(localContentResolver, "webview_fallback_logic_enabled", i);
      return;
    }
  }
  
  public void enablePackageForAllUsers(Context paramContext, String paramString, boolean paramBoolean)
  {
    paramContext = ((UserManager)paramContext.getSystemService("user")).getUsers().iterator();
    while (paramContext.hasNext()) {
      enablePackageForUser(paramString, paramBoolean, ((UserInfo)paramContext.next()).id);
    }
  }
  
  public void enablePackageForUser(String paramString, boolean paramBoolean, int paramInt)
  {
    int i = 0;
    Object localObject;
    String str;
    StringBuilder localStringBuilder;
    try
    {
      localObject = AppGlobals.getPackageManager();
      if (paramBoolean) {}
      for (;;)
      {
        ((IPackageManager)localObject).setApplicationEnabledSetting(paramString, i, 0, paramInt, null);
        return;
        i = 3;
      }
      localObject = "enable ";
    }
    catch (RemoteException|IllegalArgumentException localRemoteException)
    {
      str = TAG;
      localStringBuilder = new StringBuilder().append("Tried to ");
      if (!paramBoolean) {}
    }
    for (;;)
    {
      Log.w(str, (String)localObject + paramString + " for user " + paramInt + ": " + localRemoteException);
      return;
      localObject = "disable ";
    }
  }
  
  public int getFactoryPackageVersion(String paramString)
    throws PackageManager.NameNotFoundException
  {
    return AppGlobals.getInitialApplication().getPackageManager().getPackageInfo(paramString, 2097152).versionCode;
  }
  
  public PackageInfo getPackageInfoForProvider(WebViewProviderInfo paramWebViewProviderInfo)
    throws PackageManager.NameNotFoundException
  {
    return AppGlobals.getInitialApplication().getPackageManager().getPackageInfo(paramWebViewProviderInfo.packageName, 268443840);
  }
  
  public String getUserChosenWebViewProvider(Context paramContext)
  {
    return Settings.Global.getString(paramContext.getContentResolver(), "webview_provider");
  }
  
  public WebViewProviderInfo[] getWebViewPackages()
  {
    return this.mWebViewProviderPackages;
  }
  
  public boolean isFallbackLogicEnabled()
  {
    return Settings.Global.getInt(AppGlobals.getInitialApplication().getContentResolver(), "webview_fallback_logic_enabled", 1) == 1;
  }
  
  public void killPackageDependents(String paramString)
  {
    try
    {
      ActivityManagerNative.getDefault().killPackageDependents(paramString, -1);
      return;
    }
    catch (RemoteException paramString) {}
  }
  
  public int onWebViewProviderChanged(PackageInfo paramPackageInfo)
  {
    return WebViewFactory.onWebViewProviderChanged(paramPackageInfo);
  }
  
  public boolean systemIsDebuggable()
  {
    return Build.IS_DEBUGGABLE;
  }
  
  public void uninstallAndDisablePackageForAllUsers(final Context paramContext, String paramString)
  {
    enablePackageForAllUsers(paramContext, paramString, false);
    try
    {
      PackageManager localPackageManager = AppGlobals.getInitialApplication().getPackageManager();
      ApplicationInfo localApplicationInfo = localPackageManager.getApplicationInfo(paramString, 0);
      if ((localApplicationInfo != null) && (localApplicationInfo.isUpdatedSystemApp())) {
        localPackageManager.deletePackage(paramString, new IPackageDeleteObserver.Stub()
        {
          public void packageDeleted(String paramAnonymousString, int paramAnonymousInt)
          {
            SystemImpl.this.enablePackageForAllUsers(paramContext, paramAnonymousString, false);
          }
        }, 6);
      }
      return;
    }
    catch (PackageManager.NameNotFoundException paramContext) {}
  }
  
  public void updateUserSetting(Context paramContext, String paramString)
  {
    ContentResolver localContentResolver = paramContext.getContentResolver();
    paramContext = paramString;
    if (paramString == null) {
      paramContext = "";
    }
    Settings.Global.putString(localContentResolver, "webview_provider", paramContext);
  }
  
  private static class LazyHolder
  {
    private static final SystemImpl INSTANCE = new SystemImpl(null);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/webkit/SystemImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */