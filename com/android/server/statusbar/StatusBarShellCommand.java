package com.android.server.statusbar;

import android.content.ComponentName;
import android.os.RemoteException;
import android.os.ShellCommand;
import com.android.internal.statusbar.IStatusBarService;
import java.io.PrintWriter;

public class StatusBarShellCommand
  extends ShellCommand
{
  private final IStatusBarService mInterface;
  
  public StatusBarShellCommand(StatusBarManagerService paramStatusBarManagerService)
  {
    this.mInterface = paramStatusBarManagerService;
  }
  
  private int runAddTile()
    throws RemoteException
  {
    this.mInterface.addTile(ComponentName.unflattenFromString(getNextArgRequired()));
    return 0;
  }
  
  private int runClickTile()
    throws RemoteException
  {
    this.mInterface.clickTile(ComponentName.unflattenFromString(getNextArgRequired()));
    return 0;
  }
  
  private int runCollapse()
    throws RemoteException
  {
    this.mInterface.collapsePanels();
    return 0;
  }
  
  private int runExpandNotifications()
    throws RemoteException
  {
    this.mInterface.expandNotificationsPanel(0);
    return 0;
  }
  
  private int runExpandSettings()
    throws RemoteException
  {
    this.mInterface.expandSettingsPanel(null);
    return 0;
  }
  
  private int runRemoveTile()
    throws RemoteException
  {
    this.mInterface.remTile(ComponentName.unflattenFromString(getNextArgRequired()));
    return 0;
  }
  
  public int onCommand(String paramString)
  {
    if (paramString == null) {
      return handleDefaultCommands(paramString);
    }
    try
    {
      if (paramString.equals("expand-notifications")) {
        return runExpandNotifications();
      }
      if (paramString.equals("expand-settings")) {
        return runExpandSettings();
      }
      if (paramString.equals("collapse")) {
        return runCollapse();
      }
      if (paramString.equals("add-tile")) {
        return runAddTile();
      }
      if (paramString.equals("remove-tile")) {
        return runRemoveTile();
      }
      if (paramString.equals("click-tile")) {
        return runClickTile();
      }
      int i = handleDefaultCommands(paramString);
      return i;
    }
    catch (RemoteException paramString)
    {
      getOutPrintWriter().println("Remote exception: " + paramString);
    }
    return -1;
  }
  
  public void onHelp()
  {
    PrintWriter localPrintWriter = getOutPrintWriter();
    localPrintWriter.println("Status bar commands:");
    localPrintWriter.println("  help");
    localPrintWriter.println("    Print this help text.");
    localPrintWriter.println("");
    localPrintWriter.println("  expand-notifications");
    localPrintWriter.println("    Open the notifications panel.");
    localPrintWriter.println("");
    localPrintWriter.println("  expand-settings");
    localPrintWriter.println("    Open the notifications panel and expand quick settings if present.");
    localPrintWriter.println("");
    localPrintWriter.println("  collapse");
    localPrintWriter.println("    Collapse the notifications and settings panel.");
    localPrintWriter.println("");
    localPrintWriter.println("  add-tile COMPONENT");
    localPrintWriter.println("    Add a TileService of the specified component");
    localPrintWriter.println("");
    localPrintWriter.println("  remove-tile COMPONENT");
    localPrintWriter.println("    Remove a TileService of the specified component");
    localPrintWriter.println("");
    localPrintWriter.println("  click-tile COMPONENT");
    localPrintWriter.println("    Click on a TileService of the specified component");
    localPrintWriter.println("");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/statusbar/StatusBarShellCommand.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */