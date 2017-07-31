package com.android.server.pm;

import android.content.pm.IOtaDexopt;
import android.os.RemoteException;
import android.os.ShellCommand;
import java.io.PrintWriter;

class OtaDexoptShellCommand
  extends ShellCommand
{
  final IOtaDexopt mInterface;
  
  OtaDexoptShellCommand(OtaDexoptService paramOtaDexoptService)
  {
    this.mInterface = paramOtaDexoptService;
  }
  
  private int runOtaCleanup()
    throws RemoteException
  {
    this.mInterface.cleanup();
    return 0;
  }
  
  private int runOtaDone()
    throws RemoteException
  {
    PrintWriter localPrintWriter = getOutPrintWriter();
    if (this.mInterface.isDone()) {
      localPrintWriter.println("OTA complete.");
    }
    for (;;)
    {
      return 0;
      localPrintWriter.println("OTA incomplete.");
    }
  }
  
  private int runOtaNext()
    throws RemoteException
  {
    getOutPrintWriter().println(this.mInterface.nextDexoptCommand());
    return 0;
  }
  
  private int runOtaPrepare()
    throws RemoteException
  {
    this.mInterface.prepare();
    getOutPrintWriter().println("Success");
    return 0;
  }
  
  private int runOtaProgress()
    throws RemoteException
  {
    float f = this.mInterface.getProgress();
    getOutPrintWriter().format("%.2f", new Object[] { Float.valueOf(f) });
    return 0;
  }
  
  private int runOtaStep()
    throws RemoteException
  {
    this.mInterface.dexoptNextPackage();
    return 0;
  }
  
  public int onCommand(String paramString)
  {
    if (paramString == null) {
      return handleDefaultCommands(null);
    }
    PrintWriter localPrintWriter = getOutPrintWriter();
    try
    {
      if (paramString.equals("prepare")) {
        return runOtaPrepare();
      }
      if (paramString.equals("cleanup")) {
        return runOtaCleanup();
      }
      if (paramString.equals("done")) {
        return runOtaDone();
      }
      if (paramString.equals("step")) {
        return runOtaStep();
      }
      if (paramString.equals("next")) {
        return runOtaNext();
      }
      if (paramString.equals("progress")) {
        return runOtaProgress();
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
    localPrintWriter.println("OTA Dexopt (ota) commands:");
    localPrintWriter.println("  help");
    localPrintWriter.println("    Print this help text.");
    localPrintWriter.println("");
    localPrintWriter.println("  prepare");
    localPrintWriter.println("    Prepare an OTA dexopt pass, collecting all packages.");
    localPrintWriter.println("  done");
    localPrintWriter.println("    Replies whether the OTA is complete or not.");
    localPrintWriter.println("  step");
    localPrintWriter.println("    OTA dexopt the next package.");
    localPrintWriter.println("  next");
    localPrintWriter.println("    Get parameters for OTA dexopt of the next package.");
    localPrintWriter.println("  cleanup");
    localPrintWriter.println("    Clean up internal states. Ends an OTA session.");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/OtaDexoptShellCommand.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */