package com.android.server.job;

import android.app.AppGlobals;
import android.content.pm.IPackageManager;
import android.os.Binder;
import android.os.RemoteException;
import android.os.ShellCommand;
import java.io.PrintWriter;

public class JobSchedulerShellCommand
  extends ShellCommand
{
  public static final int CMD_ERR_CONSTRAINTS = -1002;
  public static final int CMD_ERR_NO_JOB = -1001;
  public static final int CMD_ERR_NO_PACKAGE = -1000;
  JobSchedulerService mInternal;
  IPackageManager mPM;
  
  JobSchedulerShellCommand(JobSchedulerService paramJobSchedulerService)
  {
    this.mInternal = paramJobSchedulerService;
    this.mPM = AppGlobals.getPackageManager();
  }
  
  private int runJob()
  {
    int i;
    PrintWriter localPrintWriter;
    String str;
    int j;
    int k;
    try
    {
      i = Binder.getCallingUid();
      if (this.mPM.checkUidPermission("android.permission.CHANGE_APP_IDLE_STATE", i) != 0) {
        throw new SecurityException("Uid " + i + " not permitted to force scheduled jobs");
      }
    }
    catch (RemoteException localRemoteException)
    {
      localPrintWriter = getOutPrintWriter();
      boolean bool = false;
      i = 0;
      str = getNextOption();
      if (str != null)
      {
        if (str.equals("-f")) {}
        while (str.equals("--force"))
        {
          bool = true;
          break;
        }
        if (str.equals("-u")) {}
        while (str.equals("--user"))
        {
          i = Integer.parseInt(getNextArgRequired());
          break;
        }
        localPrintWriter.println("Error: unknown option '" + str + "'");
        return -1;
      }
      str = getNextArgRequired();
      j = Integer.parseInt(getNextArgRequired());
      k = this.mInternal.executeRunCommand(str, i, j, bool);
      switch (k)
      {
      default: 
        localPrintWriter.print("Running job");
        if (bool) {
          localPrintWriter.print(" [FORCED]");
        }
        localPrintWriter.println();
        return k;
      }
    }
    localPrintWriter.print("Package not found: ");
    localPrintWriter.print(str);
    localPrintWriter.print(" / user ");
    localPrintWriter.println(i);
    return k;
    localPrintWriter.print("Could not find job ");
    localPrintWriter.print(j);
    localPrintWriter.print(" in package ");
    localPrintWriter.print(str);
    localPrintWriter.print(" / user ");
    localPrintWriter.println(i);
    return k;
    localPrintWriter.print("Job ");
    localPrintWriter.print(j);
    localPrintWriter.print(" in package ");
    localPrintWriter.print(str);
    localPrintWriter.print(" / user ");
    localPrintWriter.print(i);
    localPrintWriter.println(" has functional constraints but --force not specified");
    return k;
  }
  
  public int onCommand(String paramString)
  {
    PrintWriter localPrintWriter = getOutPrintWriter();
    try
    {
      if ("run".equals(paramString)) {
        return runJob();
      }
      int i = handleDefaultCommands(paramString);
      return i;
    }
    catch (Exception paramString)
    {
      localPrintWriter.println("Exception: " + paramString);
    }
    return -1;
  }
  
  public void onHelp()
  {
    PrintWriter localPrintWriter = getOutPrintWriter();
    localPrintWriter.println("Job scheduler (jobscheduler) commands:");
    localPrintWriter.println("  help");
    localPrintWriter.println("    Print this help text.");
    localPrintWriter.println();
    localPrintWriter.println("  run [-f | --force] [-u | --user USER_ID] PACKAGE JOB_ID");
    localPrintWriter.println("    Trigger immediate execution of a specific scheduled job.");
    localPrintWriter.println("    Options:");
    localPrintWriter.println("      -f or --force: run the job even if technical constraints such as");
    localPrintWriter.println("         connectivity are not currently met");
    localPrintWriter.println("      -u or --user: specify which user's job is to be run; the default is");
    localPrintWriter.println("         the primary or system user");
    localPrintWriter.println();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/job/JobSchedulerShellCommand.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */