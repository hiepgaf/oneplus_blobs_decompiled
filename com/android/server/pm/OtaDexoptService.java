package com.android.server.pm;

import android.content.Context;
import android.content.pm.IOtaDexopt.Stub;
import android.content.pm.PackageParser.Package;
import android.os.Environment;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.storage.StorageManager;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Slog;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.os.InstallerConnection;
import com.android.internal.os.InstallerConnection.InstallerException;
import java.io.File;
import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OtaDexoptService
  extends IOtaDexopt.Stub
{
  private static final long BULK_DELETE_THRESHOLD = 1073741824L;
  private static final boolean DEBUG_DEXOPT = true;
  private static final String[] NO_LIBRARIES = { "&" };
  private static final String TAG = "OTADexopt";
  private long availableSpaceAfterBulkDelete;
  private long availableSpaceAfterDexopt;
  private long availableSpaceBefore;
  private int completeSize;
  private int dexoptCommandCountExecuted;
  private int dexoptCommandCountTotal;
  private int importantPackageCount;
  private final Context mContext;
  private List<String> mDexoptCommands;
  private final PackageManagerService mPackageManagerService;
  private long otaDexoptTimeStart;
  private int otherPackageCount;
  
  public OtaDexoptService(Context paramContext, PackageManagerService paramPackageManagerService)
  {
    this.mContext = paramContext;
    this.mPackageManagerService = paramPackageManagerService;
    moveAbArtifacts(paramPackageManagerService.mInstaller);
  }
  
  private void deleteOatArtifactsOfPackage(PackageParser.Package paramPackage)
  {
    String[] arrayOfString = InstructionSets.getAppDexInstructionSets(paramPackage.applicationInfo);
    Iterator localIterator = paramPackage.getAllCodePaths().iterator();
    if (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      int i = 0;
      int j = arrayOfString.length;
      while (i < j)
      {
        String str2 = arrayOfString[i];
        try
        {
          this.mPackageManagerService.mInstaller.deleteOdex(str1, str2, getOatDir(paramPackage));
          i += 1;
        }
        catch (InstallerConnection.InstallerException localInstallerException)
        {
          for (;;)
          {
            Log.e("OTADexopt", "Failed deleting oat files for " + str1, localInstallerException);
          }
        }
      }
    }
  }
  
  private List<String> generatePackageDexopts(PackageParser.Package paramPackage, int paramInt)
  {
    try
    {
      RecordingInstallerConnection localRecordingInstallerConnection = new RecordingInstallerConnection(null);
      OTADexoptPackageDexOptimizer localOTADexoptPackageDexOptimizer = new OTADexoptPackageDexOptimizer(new Installer(this.mContext, localRecordingInstallerConnection), this.mPackageManagerService.mInstallLock, this.mContext);
      String[] arrayOfString = paramPackage.usesLibraryFiles;
      if (paramPackage.isSystemApp()) {
        arrayOfString = NO_LIBRARIES;
      }
      localOTADexoptPackageDexOptimizer.performDexOpt(paramPackage, arrayOfString, null, false, PackageManagerServiceCompilerMapping.getCompilerFilterForReason(paramInt), null);
      paramPackage = localRecordingInstallerConnection.commands;
      return paramPackage;
    }
    finally {}
  }
  
  private long getAvailableSpace()
  {
    long l = getMainLowSpaceThreshold();
    return Environment.getDataDirectory().getUsableSpace() - l;
  }
  
  private long getMainLowSpaceThreshold()
  {
    File localFile = Environment.getDataDirectory();
    long l = StorageManager.from(this.mContext).getStorageLowBytes(localFile);
    if (l == 0L) {
      throw new IllegalStateException("Invalid low memory threshold");
    }
    return l;
  }
  
  private static String getOatDir(PackageParser.Package paramPackage)
  {
    if (!paramPackage.canHaveOatDir()) {
      return null;
    }
    paramPackage = new File(paramPackage.codePath);
    if (paramPackage.isDirectory()) {
      return PackageDexOptimizer.getOatDir(paramPackage).getAbsolutePath();
    }
    return null;
  }
  
  private static int inMegabytes(long paramLong)
  {
    paramLong /= 1048576L;
    if (paramLong > 2147483647L)
    {
      Log.w("OTADexopt", "Recording " + paramLong + "MB of free space, overflowing range");
      return Integer.MAX_VALUE;
    }
    return (int)paramLong;
  }
  
  public static OtaDexoptService main(Context paramContext, PackageManagerService paramPackageManagerService)
  {
    paramContext = new OtaDexoptService(paramContext, paramPackageManagerService);
    ServiceManager.addService("otadexopt", paramContext);
    return paramContext;
  }
  
  private void moveAbArtifacts(Installer paramInstaller)
  {
    if (this.mDexoptCommands != null) {
      throw new IllegalStateException("Should not be ota-dexopting when trying to move.");
    }
    Iterator localIterator1 = this.mPackageManagerService.getPackages().iterator();
    while (localIterator1.hasNext())
    {
      PackageParser.Package localPackage = (PackageParser.Package)localIterator1.next();
      if ((localPackage != null) && (PackageDexOptimizer.canOptimizePackage(localPackage))) {
        if (localPackage.codePath == null)
        {
          Slog.w("OTADexopt", "Package " + localPackage + " can be optimized but has null codePath");
        }
        else if ((!localPackage.codePath.startsWith("/system")) && (!localPackage.codePath.startsWith("/vendor")))
        {
          String[] arrayOfString = InstructionSets.getAppDexInstructionSets(localPackage.applicationInfo);
          List localList = localPackage.getAllCodePathsExcludingResourceOnly();
          arrayOfString = InstructionSets.getDexCodeInstructionSets(arrayOfString);
          int i = 0;
          int j = arrayOfString.length;
          while (i < j)
          {
            String str1 = arrayOfString[i];
            Iterator localIterator2 = localList.iterator();
            while (localIterator2.hasNext())
            {
              String str2 = (String)localIterator2.next();
              String str3 = PackageDexOptimizer.getOatDir(new File(localPackage.codePath)).getAbsolutePath();
              try
              {
                paramInstaller.moveAb(str2, str1, str3);
              }
              catch (InstallerConnection.InstallerException localInstallerException) {}
            }
            i += 1;
          }
        }
      }
    }
  }
  
  private void performMetricsLogging()
  {
    long l = System.nanoTime();
    MetricsLogger.histogram(this.mContext, "ota_dexopt_available_space_before_mb", inMegabytes(this.availableSpaceBefore));
    MetricsLogger.histogram(this.mContext, "ota_dexopt_available_space_after_bulk_delete_mb", inMegabytes(this.availableSpaceAfterBulkDelete));
    MetricsLogger.histogram(this.mContext, "ota_dexopt_available_space_after_dexopt_mb", inMegabytes(this.availableSpaceAfterDexopt));
    MetricsLogger.histogram(this.mContext, "ota_dexopt_num_important_packages", this.importantPackageCount);
    MetricsLogger.histogram(this.mContext, "ota_dexopt_num_other_packages", this.otherPackageCount);
    MetricsLogger.histogram(this.mContext, "ota_dexopt_num_commands", this.dexoptCommandCountTotal);
    MetricsLogger.histogram(this.mContext, "ota_dexopt_num_commands_executed", this.dexoptCommandCountExecuted);
    int i = (int)TimeUnit.NANOSECONDS.toSeconds(l - this.otaDexoptTimeStart);
    MetricsLogger.histogram(this.mContext, "ota_dexopt_time_s", i);
  }
  
  private void prepareMetricsLogging(int paramInt1, int paramInt2, long paramLong1, long paramLong2)
  {
    this.availableSpaceBefore = paramLong1;
    this.availableSpaceAfterBulkDelete = paramLong2;
    this.availableSpaceAfterDexopt = 0L;
    this.importantPackageCount = paramInt1;
    this.otherPackageCount = paramInt2;
    this.dexoptCommandCountTotal = this.mDexoptCommands.size();
    this.dexoptCommandCountExecuted = 0;
    this.otaDexoptTimeStart = System.nanoTime();
  }
  
  public void cleanup()
    throws RemoteException
  {
    try
    {
      Log.i("OTADexopt", "Cleaning up OTA Dexopt state.");
      this.mDexoptCommands = null;
      this.availableSpaceAfterDexopt = getAvailableSpace();
      performMetricsLogging();
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void dexoptNextPackage()
    throws RemoteException
  {
    try
    {
      throw new UnsupportedOperationException();
    }
    finally {}
  }
  
  public float getProgress()
    throws RemoteException
  {
    try
    {
      int i = this.completeSize;
      if (i == 0) {
        return 1.0F;
      }
      i = this.mDexoptCommands.size();
      float f = this.completeSize - i;
      i = this.completeSize;
      f /= i;
      return f;
    }
    finally {}
  }
  
  public boolean isDone()
    throws RemoteException
  {
    try
    {
      if (this.mDexoptCommands == null) {
        throw new IllegalStateException("done() called before prepare()");
      }
    }
    finally {}
    boolean bool = this.mDexoptCommands.isEmpty();
    return bool;
  }
  
  public String nextDexoptCommand()
    throws RemoteException
  {
    try
    {
      if (this.mDexoptCommands == null) {
        throw new IllegalStateException("dexoptNextPackage() called before prepare()");
      }
    }
    finally {}
    if (this.mDexoptCommands.isEmpty()) {
      return "(all done)";
    }
    String str = (String)this.mDexoptCommands.remove(0);
    if (getAvailableSpace() > 0L)
    {
      this.dexoptCommandCountExecuted += 1;
      return str;
    }
    Log.w("OTADexopt", "Not enough space for OTA dexopt, stopping with " + (this.mDexoptCommands.size() + 1) + " commands left.");
    this.mDexoptCommands.clear();
    return "(no free space)";
  }
  
  public void onShellCommand(FileDescriptor paramFileDescriptor1, FileDescriptor paramFileDescriptor2, FileDescriptor paramFileDescriptor3, String[] paramArrayOfString, ResultReceiver paramResultReceiver)
    throws RemoteException
  {
    new OtaDexoptShellCommand(this).exec(this, paramFileDescriptor1, paramFileDescriptor2, paramFileDescriptor3, paramArrayOfString, paramResultReceiver);
  }
  
  public void prepare()
    throws RemoteException
  {
    try
    {
      if (this.mDexoptCommands != null) {
        throw new IllegalStateException("already called prepare()");
      }
    }
    finally {}
    ArrayList localArrayList;
    PackageParser.Package localPackage;
    int i;
    long l1;
    long l2;
    label377:
    synchronized (this.mPackageManagerService.mPackages)
    {
      List localList = PackageManagerServiceUtils.getPackagesForDexopt(this.mPackageManagerService.mPackages.values(), this.mPackageManagerService);
      localArrayList = new ArrayList(this.mPackageManagerService.mPackages.values());
      localArrayList.removeAll(localList);
      this.mDexoptCommands = new ArrayList(this.mPackageManagerService.mPackages.size() * 3 / 2);
      ??? = localList.iterator();
      if (((Iterator)???).hasNext())
      {
        localPackage = (PackageParser.Package)((Iterator)???).next();
        if (!localPackage.coreApp) {
          break label377;
        }
        i = 8;
        this.mDexoptCommands.addAll(generatePackageDexopts(localPackage, i));
      }
    }
  }
  
  private static class OTADexoptPackageDexOptimizer
    extends PackageDexOptimizer.ForcedUpdatePackageDexOptimizer
  {
    public OTADexoptPackageDexOptimizer(Installer paramInstaller, Object paramObject, Context paramContext)
    {
      super(paramObject, paramContext, "*otadexopt*");
    }
    
    protected int adjustDexoptFlags(int paramInt)
    {
      return paramInt | 0x40;
    }
  }
  
  private static class RecordingInstallerConnection
    extends InstallerConnection
  {
    public List<String> commands = new ArrayList(1);
    
    public void disconnect()
    {
      throw new IllegalStateException("Should not reach here");
    }
    
    public boolean dumpProfiles(String paramString1, String paramString2, String paramString3)
      throws InstallerConnection.InstallerException
    {
      throw new IllegalStateException("Should not reach here");
    }
    
    public boolean mergeProfiles(int paramInt, String paramString)
      throws InstallerConnection.InstallerException
    {
      throw new IllegalStateException("Should not reach here");
    }
    
    public void setWarnIfHeld(Object paramObject)
    {
      throw new IllegalStateException("Should not reach here");
    }
    
    public String transact(String paramString)
    {
      try
      {
        this.commands.add(paramString);
        return "0";
      }
      finally
      {
        paramString = finally;
        throw paramString;
      }
    }
    
    public void waitForConnection()
    {
      throw new IllegalStateException("Should not reach here");
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/OtaDexoptService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */