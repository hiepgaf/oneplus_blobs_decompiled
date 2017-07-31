package com.android.server.webkit;

import android.os.RemoteException;
import android.os.ShellCommand;
import android.webkit.IWebViewUpdateService;
import java.io.PrintWriter;

class WebViewUpdateServiceShellCommand
  extends ShellCommand
{
  final IWebViewUpdateService mInterface;
  
  WebViewUpdateServiceShellCommand(IWebViewUpdateService paramIWebViewUpdateService)
  {
    this.mInterface = paramIWebViewUpdateService;
  }
  
  private int enableFallbackLogic(boolean paramBoolean)
    throws RemoteException
  {
    PrintWriter localPrintWriter = getOutPrintWriter();
    this.mInterface.enableFallbackLogic(paramBoolean);
    localPrintWriter.println("Success");
    return 0;
  }
  
  private int setWebViewImplementation()
    throws RemoteException
  {
    PrintWriter localPrintWriter = getOutPrintWriter();
    String str1 = getNextArg();
    String str2 = this.mInterface.changeProviderAndSetting(str1);
    if (str1.equals(str2))
    {
      localPrintWriter.println("Success");
      return 0;
    }
    localPrintWriter.println(String.format("Failed to switch to %s, the WebView implementation is now provided by %s.", new Object[] { str1, str2 }));
    return 1;
  }
  
  public int onCommand(String paramString)
  {
    if (paramString == null) {
      return handleDefaultCommands(paramString);
    }
    PrintWriter localPrintWriter = getOutPrintWriter();
    try
    {
      if (paramString.equals("enable-redundant-packages")) {
        return enableFallbackLogic(false);
      }
      if (paramString.equals("disable-redundant-packages")) {
        return enableFallbackLogic(true);
      }
      if (paramString.equals("set-webview-implementation")) {
        return setWebViewImplementation();
      }
      int i = handleDefaultCommands(paramString);
      return i;
    }
    catch (RemoteException paramString)
    {
      localPrintWriter.println("Remote exception: " + paramString);
    }
    return -1;
  }
  
  public void onHelp()
  {
    PrintWriter localPrintWriter = getOutPrintWriter();
    localPrintWriter.println("WebView updater commands:");
    localPrintWriter.println("  help");
    localPrintWriter.println("    Print this help text.");
    localPrintWriter.println("");
    localPrintWriter.println("  enable-redundant-packages");
    localPrintWriter.println("    Allow a fallback package to be installed and enabled even when a");
    localPrintWriter.println("    more-preferred package is available. This command is useful when testing");
    localPrintWriter.println("    fallback packages.");
    localPrintWriter.println("  disable-redundant-packages");
    localPrintWriter.println("    Disallow installing and enabling fallback packages when a more-preferred");
    localPrintWriter.println("    package is available.");
    localPrintWriter.println("  set-webview-implementation PACKAGE");
    localPrintWriter.println("    Set the WebView implementation to the specified package.");
    localPrintWriter.println();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/webkit/WebViewUpdateServiceShellCommand.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */