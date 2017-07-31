package com.android.server.wm;

import android.os.IBinder;
import android.util.Slog;
import java.io.PrintWriter;

class WindowToken
{
  AppWindowToken appWindowToken;
  final boolean explicit;
  boolean hasVisible;
  boolean hidden;
  boolean paused = false;
  boolean sendingToBottom;
  final WindowManagerService service;
  String stringName;
  final IBinder token;
  boolean waitingToShow;
  final int windowType;
  final WindowList windows = new WindowList();
  
  WindowToken(WindowManagerService paramWindowManagerService, IBinder paramIBinder, int paramInt, boolean paramBoolean)
  {
    this.service = paramWindowManagerService;
    this.token = paramIBinder;
    this.windowType = paramInt;
    this.explicit = paramBoolean;
  }
  
  void dump(PrintWriter paramPrintWriter, String paramString)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("windows=");
    paramPrintWriter.println(this.windows);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("windowType=");
    paramPrintWriter.print(this.windowType);
    paramPrintWriter.print(" hidden=");
    paramPrintWriter.print(this.hidden);
    paramPrintWriter.print(" hasVisible=");
    paramPrintWriter.println(this.hasVisible);
    if ((this.waitingToShow) || (this.sendingToBottom))
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("waitingToShow=");
      paramPrintWriter.print(this.waitingToShow);
      paramPrintWriter.print(" sendingToBottom=");
      paramPrintWriter.print(this.sendingToBottom);
    }
  }
  
  void removeAllWindows()
  {
    int i = this.windows.size() - 1;
    while (i >= 0)
    {
      WindowState localWindowState = (WindowState)this.windows.get(i);
      if (WindowManagerDebugConfig.DEBUG_WINDOW_MOVEMENT) {
        Slog.w("WindowManager", "removeAllWindows: removing win=" + localWindowState);
      }
      localWindowState.mService.removeWindowLocked(localWindowState);
      i -= 1;
    }
    this.windows.clear();
  }
  
  public String toString()
  {
    if (this.stringName == null)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("WindowToken{");
      localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
      localStringBuilder.append(" ");
      localStringBuilder.append(this.token);
      localStringBuilder.append('}');
      this.stringName = localStringBuilder.toString();
    }
    return this.stringName;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/WindowToken.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */