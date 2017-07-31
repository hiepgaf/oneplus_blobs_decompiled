package com.android.server.webkit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.Process;
import android.os.ResultReceiver;
import android.os.UserHandle;
import android.util.Slog;
import android.webkit.IWebViewUpdateService.Stub;
import android.webkit.WebViewProviderInfo;
import android.webkit.WebViewProviderResponse;
import com.android.server.SystemService;
import java.io.FileDescriptor;
import java.util.Arrays;
import java.util.List;

public class WebViewUpdateService
  extends SystemService
{
  static final int PACKAGE_ADDED = 1;
  static final int PACKAGE_ADDED_REPLACED = 2;
  static final int PACKAGE_CHANGED = 0;
  static final int PACKAGE_REMOVED = 3;
  private static final String TAG = "WebViewUpdateService";
  private WebViewUpdateServiceImpl mImpl;
  private BroadcastReceiver mWebViewUpdatedReceiver;
  
  public WebViewUpdateService(Context paramContext)
  {
    super(paramContext);
    this.mImpl = new WebViewUpdateServiceImpl(paramContext, SystemImpl.getInstance());
  }
  
  public static boolean entirePackageChanged(Intent paramIntent)
  {
    return Arrays.asList(paramIntent.getStringArrayExtra("android.intent.extra.changed_component_name_list")).contains(paramIntent.getDataString().substring("package:".length()));
  }
  
  private static String packageNameFromIntent(Intent paramIntent)
  {
    return paramIntent.getDataString().substring("package:".length());
  }
  
  public void onStart()
  {
    this.mWebViewUpdatedReceiver = new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        int j = paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", 55536);
        paramAnonymousContext = paramAnonymousIntent.getAction();
        String str;
        if (paramAnonymousContext.equals("android.intent.action.PACKAGE_REMOVED"))
        {
          if (!paramAnonymousIntent.getExtras().getBoolean("android.intent.extra.REPLACING")) {}
        }
        else
        {
          if (paramAnonymousContext.equals("android.intent.action.PACKAGE_CHANGED"))
          {
            if (WebViewUpdateService.entirePackageChanged(paramAnonymousIntent)) {
              WebViewUpdateService.-get0(WebViewUpdateService.this).packageStateChanged(WebViewUpdateService.-wrap0(paramAnonymousIntent), 0, j);
            }
            return;
          }
          if (paramAnonymousContext.equals("android.intent.action.PACKAGE_ADDED"))
          {
            paramAnonymousContext = WebViewUpdateService.-get0(WebViewUpdateService.this);
            str = WebViewUpdateService.-wrap0(paramAnonymousIntent);
            if (!paramAnonymousIntent.getExtras().getBoolean("android.intent.extra.REPLACING")) {
              break label159;
            }
          }
        }
        label159:
        for (int i = 2;; i = 1)
        {
          paramAnonymousContext.packageStateChanged(str, i, j);
          return;
          if (!paramAnonymousContext.equals("android.intent.action.USER_ADDED")) {
            break;
          }
          WebViewUpdateService.-get0(WebViewUpdateService.this).handleNewUser(j);
          return;
          WebViewUpdateService.-get0(WebViewUpdateService.this).packageStateChanged(WebViewUpdateService.-wrap0(paramAnonymousIntent), 3, j);
          return;
        }
      }
    };
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.PACKAGE_ADDED");
    localIntentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
    localIntentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
    localIntentFilter.addDataScheme("package");
    WebViewProviderInfo[] arrayOfWebViewProviderInfo = this.mImpl.getWebViewPackages();
    int j = arrayOfWebViewProviderInfo.length;
    int i = 0;
    while (i < j)
    {
      localIntentFilter.addDataSchemeSpecificPart(arrayOfWebViewProviderInfo[i].packageName, 0);
      i += 1;
    }
    getContext().registerReceiverAsUser(this.mWebViewUpdatedReceiver, UserHandle.ALL, localIntentFilter, null, null);
    localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.USER_ADDED");
    getContext().registerReceiverAsUser(this.mWebViewUpdatedReceiver, UserHandle.ALL, localIntentFilter, null, null);
    publishBinderService("webviewupdate", new BinderService(null), true);
  }
  
  public void prepareWebViewInSystemServer()
  {
    this.mImpl.prepareWebViewInSystemServer();
  }
  
  private class BinderService
    extends IWebViewUpdateService.Stub
  {
    private BinderService() {}
    
    public String changeProviderAndSetting(String paramString)
    {
      if (WebViewUpdateService.this.getContext().checkCallingPermission("android.permission.WRITE_SECURE_SETTINGS") != 0)
      {
        paramString = "Permission Denial: changeProviderAndSetting() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires " + "android.permission.WRITE_SECURE_SETTINGS";
        Slog.w("WebViewUpdateService", paramString);
        throw new SecurityException(paramString);
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        paramString = WebViewUpdateService.-get0(WebViewUpdateService.this).changeProviderAndSetting(paramString);
        return paramString;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void enableFallbackLogic(boolean paramBoolean)
    {
      if (WebViewUpdateService.this.getContext().checkCallingPermission("android.permission.WRITE_SECURE_SETTINGS") != 0)
      {
        String str = "Permission Denial: enableFallbackLogic() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires " + "android.permission.WRITE_SECURE_SETTINGS";
        Slog.w("WebViewUpdateService", str);
        throw new SecurityException(str);
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        WebViewUpdateService.-get0(WebViewUpdateService.this).enableFallbackLogic(paramBoolean);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public WebViewProviderInfo[] getAllWebViewPackages()
    {
      return WebViewUpdateService.-get0(WebViewUpdateService.this).getWebViewPackages();
    }
    
    public String getCurrentWebViewPackageName()
    {
      return WebViewUpdateService.-get0(WebViewUpdateService.this).getCurrentWebViewPackageName();
    }
    
    public WebViewProviderInfo[] getValidWebViewPackages()
    {
      return WebViewUpdateService.-get0(WebViewUpdateService.this).getValidWebViewPackages();
    }
    
    public boolean isFallbackPackage(String paramString)
    {
      return WebViewUpdateService.-get0(WebViewUpdateService.this).isFallbackPackage(paramString);
    }
    
    public void notifyRelroCreationCompleted()
    {
      if ((Binder.getCallingUid() != 1037) && (Binder.getCallingUid() != 1000)) {
        return;
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        WebViewUpdateService.-get0(WebViewUpdateService.this).notifyRelroCreationCompleted();
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void onShellCommand(FileDescriptor paramFileDescriptor1, FileDescriptor paramFileDescriptor2, FileDescriptor paramFileDescriptor3, String[] paramArrayOfString, ResultReceiver paramResultReceiver)
    {
      new WebViewUpdateServiceShellCommand(this).exec(this, paramFileDescriptor1, paramFileDescriptor2, paramFileDescriptor3, paramArrayOfString, paramResultReceiver);
    }
    
    public WebViewProviderResponse waitForAndGetProvider()
    {
      if (Binder.getCallingPid() == Process.myPid()) {
        throw new IllegalStateException("Cannot create a WebView from the SystemServer");
      }
      return WebViewUpdateService.-get0(WebViewUpdateService.this).waitForAndGetProvider();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/webkit/WebViewUpdateService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */