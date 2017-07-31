package com.android.server.am;

import android.app.ActivityManager;
import android.app.IActivityManager;
import android.os.RemoteException;
import android.os.ShellCommand;
import android.os.UserHandle;
import android.util.DebugUtils;
import android.util.SparseArray;
import java.io.PrintWriter;

class ActivityManagerShellCommand
  extends ShellCommand
{
  final boolean mDumping;
  final IActivityManager mInterface;
  final ActivityManagerService mInternal;
  
  ActivityManagerShellCommand(ActivityManagerService paramActivityManagerService, boolean paramBoolean)
  {
    this.mInterface = paramActivityManagerService;
    this.mInternal = paramActivityManagerService;
    this.mDumping = paramBoolean;
  }
  
  static void dumpHelp(PrintWriter paramPrintWriter, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      paramPrintWriter.println("Activity manager dump options:");
      paramPrintWriter.println("  [-a] [-c] [-p PACKAGE] [-h] [WHAT] ...");
      paramPrintWriter.println("  WHAT may be one of:");
      paramPrintWriter.println("    a[ctivities]: activity stack state");
      paramPrintWriter.println("    r[recents]: recent activities state");
      paramPrintWriter.println("    b[roadcasts] [PACKAGE_NAME] [history [-s]]: broadcast state");
      paramPrintWriter.println("    broadcast-stats [PACKAGE_NAME]: aggregated broadcast statistics");
      paramPrintWriter.println("    i[ntents] [PACKAGE_NAME]: pending intent state");
      paramPrintWriter.println("    p[rocesses] [PACKAGE_NAME]: process state");
      paramPrintWriter.println("    o[om]: out of memory management");
      paramPrintWriter.println("    perm[issions]: URI permission grant state");
      paramPrintWriter.println("    prov[iders] [COMP_SPEC ...]: content provider state");
      paramPrintWriter.println("    provider [COMP_SPEC]: provider client-side state");
      paramPrintWriter.println("    s[ervices] [COMP_SPEC ...]: service state");
      paramPrintWriter.println("    as[sociations]: tracked app associations");
      paramPrintWriter.println("    service [COMP_SPEC]: service client-side state");
      paramPrintWriter.println("    package [PACKAGE_NAME]: all state related to given package");
      paramPrintWriter.println("    all: dump all activities");
      paramPrintWriter.println("    top: dump the top activity");
      paramPrintWriter.println("  WHAT may also be a COMP_SPEC to dump activities.");
      paramPrintWriter.println("  COMP_SPEC may be a component name (com.foo/.myApp),");
      paramPrintWriter.println("    a partial substring in a component name, a");
      paramPrintWriter.println("    hex object identifier.");
      paramPrintWriter.println("  -a: include all available server state.");
      paramPrintWriter.println("  -c: include client state.");
      paramPrintWriter.println("  -p: limit output to given package.");
      paramPrintWriter.println("  --checkin: output checkin format, resetting data.");
      paramPrintWriter.println("  --C: output checkin format, not resetting data.");
      return;
    }
    paramPrintWriter.println("Activity manager (activity) commands:");
    paramPrintWriter.println("  help");
    paramPrintWriter.println("    Print this help text.");
    paramPrintWriter.println("  force-stop [--user <USER_ID> | all | current] <PACKAGE>");
    paramPrintWriter.println("    Completely stop the given application package.");
    paramPrintWriter.println("  kill [--user <USER_ID> | all | current] <PACKAGE>");
    paramPrintWriter.println("    Kill all processes associated with the given application.");
    paramPrintWriter.println("  kill-all");
    paramPrintWriter.println("    Kill all processes that are safe to kill (cached, etc).");
    paramPrintWriter.println("  write");
    paramPrintWriter.println("    Write all pending state to storage.");
    paramPrintWriter.println("  track-associations");
    paramPrintWriter.println("    Enable association tracking.");
    paramPrintWriter.println("  untrack-associations");
    paramPrintWriter.println("    Disable and clear association tracking.");
    paramPrintWriter.println("  is-user-stopped <USER_ID>");
    paramPrintWriter.println("    Returns whether <USER_ID> has been stopped or not.");
    paramPrintWriter.println("  lenient-background-check [<true|false>]");
    paramPrintWriter.println("    Optionally controls lenient background check mode, returns current mode.");
    paramPrintWriter.println("  get-uid-state <UID>");
    paramPrintWriter.println("    Gets the process state of an app given its <UID>.");
  }
  
  int getUidState(PrintWriter paramPrintWriter)
    throws RemoteException
  {
    this.mInternal.enforceCallingPermission("android.permission.DUMP", "getUidState()");
    int i = this.mInternal.getUidState(Integer.parseInt(getNextArgRequired()));
    paramPrintWriter.print(i);
    paramPrintWriter.print(" (");
    paramPrintWriter.printf(DebugUtils.valueToString(ActivityManager.class, "PROCESS_STATE_", i), new Object[0]);
    paramPrintWriter.println(")");
    return 0;
  }
  
  public int onCommand(String paramString)
  {
    if (paramString == null) {
      return handleDefaultCommands(paramString);
    }
    PrintWriter localPrintWriter = getOutPrintWriter();
    try
    {
      if (paramString.equals("force-stop")) {
        return runForceStop(localPrintWriter);
      }
      if (paramString.equals("kill")) {
        return runKill(localPrintWriter);
      }
      if (paramString.equals("kill-all")) {
        return runKillAll(localPrintWriter);
      }
      if (paramString.equals("write")) {
        return runWrite(localPrintWriter);
      }
      if (paramString.equals("track-associations")) {
        return runTrackAssociations(localPrintWriter);
      }
      if (paramString.equals("untrack-associations")) {
        return runUntrackAssociations(localPrintWriter);
      }
      if (paramString.equals("is-user-stopped")) {
        return runIsUserStopped(localPrintWriter);
      }
      if (paramString.equals("lenient-background-check")) {
        return runLenientBackgroundCheck(localPrintWriter);
      }
      if (paramString.equals("get-uid-state")) {
        return getUidState(localPrintWriter);
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
    dumpHelp(getOutPrintWriter(), this.mDumping);
  }
  
  int runForceStop(PrintWriter paramPrintWriter)
    throws RemoteException
  {
    String str;
    for (int i = -1;; i = UserHandle.parseUserArg(getNextArgRequired()))
    {
      str = getNextOption();
      if (str == null) {
        break label58;
      }
      if (!str.equals("--user")) {
        break;
      }
    }
    paramPrintWriter.println("Error: Unknown option: " + str);
    return -1;
    label58:
    this.mInterface.forceStopPackage(getNextArgRequired(), i);
    return 0;
  }
  
  int runIsUserStopped(PrintWriter paramPrintWriter)
  {
    int i = UserHandle.parseUserArg(getNextArgRequired());
    paramPrintWriter.println(this.mInternal.isUserStopped(i));
    return 0;
  }
  
  int runKill(PrintWriter paramPrintWriter)
    throws RemoteException
  {
    String str;
    for (int i = -1;; i = UserHandle.parseUserArg(getNextArgRequired()))
    {
      str = getNextOption();
      if (str == null) {
        break label58;
      }
      if (!str.equals("--user")) {
        break;
      }
    }
    paramPrintWriter.println("Error: Unknown option: " + str);
    return -1;
    label58:
    this.mInterface.killBackgroundProcesses(getNextArgRequired(), i);
    return 0;
  }
  
  int runKillAll(PrintWriter paramPrintWriter)
    throws RemoteException
  {
    this.mInterface.killAllBackgroundProcesses();
    return 0;
  }
  
  int runLenientBackgroundCheck(PrintWriter paramPrintWriter)
    throws RemoteException
  {
    ??? = getNextArg();
    boolean bool;
    if (??? != null)
    {
      if (Boolean.valueOf((String)???).booleanValue()) {
        break label71;
      }
      bool = "1".equals(???);
    }
    for (;;)
    {
      this.mInterface.setLenientBackgroundCheck(bool);
      synchronized (this.mInternal)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        if (this.mInternal.mLenientBackgroundCheck)
        {
          paramPrintWriter.println("Lenient background check enabled");
          ActivityManagerService.resetPriorityAfterLockedSection();
          return 0;
          label71:
          bool = true;
          continue;
        }
        paramPrintWriter.println("Lenient background check disabled");
      }
    }
  }
  
  int runTrackAssociations(PrintWriter paramPrintWriter)
  {
    this.mInternal.enforceCallingPermission("android.permission.SET_ACTIVITY_WATCHER", "registerUidObserver()");
    synchronized (this.mInternal)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      if (!this.mInternal.mTrackingAssociations)
      {
        this.mInternal.mTrackingAssociations = true;
        paramPrintWriter.println("Association tracking started.");
        ActivityManagerService.resetPriorityAfterLockedSection();
        return 0;
      }
      paramPrintWriter.println("Association tracking already enabled.");
    }
  }
  
  int runUntrackAssociations(PrintWriter paramPrintWriter)
  {
    this.mInternal.enforceCallingPermission("android.permission.SET_ACTIVITY_WATCHER", "registerUidObserver()");
    synchronized (this.mInternal)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      if (this.mInternal.mTrackingAssociations)
      {
        this.mInternal.mTrackingAssociations = false;
        this.mInternal.mAssociations.clear();
        paramPrintWriter.println("Association tracking stopped.");
        ActivityManagerService.resetPriorityAfterLockedSection();
        return 0;
      }
      paramPrintWriter.println("Association tracking not running.");
    }
  }
  
  int runWrite(PrintWriter paramPrintWriter)
  {
    this.mInternal.enforceCallingPermission("android.permission.SET_ACTIVITY_WATCHER", "registerUidObserver()");
    this.mInternal.mRecentTasks.flush();
    paramPrintWriter.println("All tasks persisted.");
    return 0;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/ActivityManagerShellCommand.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */