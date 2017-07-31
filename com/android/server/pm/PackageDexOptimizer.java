package com.android.server.pm;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageParser.Package;
import android.os.Environment;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.UserHandle;
import android.os.WorkSource;
import android.util.Log;
import android.util.OpFeatures;
import android.util.Slog;
import com.android.internal.os.InstallerConnection.InstallerException;
import com.android.internal.util.IndentingPrintWriter;
import dalvik.system.DexFile;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

class PackageDexOptimizer
{
  static final int DEX_OPT_FAILED = -1;
  static final int DEX_OPT_PERFORMED = 1;
  static final int DEX_OPT_SKIPPED = 0;
  static final String OAT_DIR_NAME = "oat";
  private static final String TAG = "PackageManager.DexOptimizer";
  private final PowerManager.WakeLock mDexoptWakeLock;
  private final Object mInstallLock;
  private final Installer mInstaller;
  private volatile boolean mSystemReady;
  
  PackageDexOptimizer(Installer paramInstaller, Object paramObject, Context paramContext, String paramString)
  {
    this.mInstaller = paramInstaller;
    this.mInstallLock = paramObject;
    this.mDexoptWakeLock = ((PowerManager)paramContext.getSystemService("power")).newWakeLock(1, paramString);
  }
  
  protected PackageDexOptimizer(PackageDexOptimizer paramPackageDexOptimizer)
  {
    this.mInstaller = paramPackageDexOptimizer.mInstaller;
    this.mInstallLock = paramPackageDexOptimizer.mInstallLock;
    this.mDexoptWakeLock = paramPackageDexOptimizer.mDexoptWakeLock;
    this.mSystemReady = paramPackageDexOptimizer.mSystemReady;
  }
  
  static boolean canOptimizePackage(PackageParser.Package paramPackage)
  {
    boolean bool = false;
    if ((paramPackage.applicationInfo.flags & 0x4) != 0) {
      bool = true;
    }
    return bool;
  }
  
  private String createOatDirIfSupported(PackageParser.Package paramPackage, String paramString)
  {
    if ((OpFeatures.isSupport(new int[] { 18 })) && (isReserveApp(paramPackage))) {
      return null;
    }
    if (!paramPackage.canHaveOatDir()) {
      return null;
    }
    paramPackage = new File(paramPackage.codePath);
    if (paramPackage.isDirectory())
    {
      paramPackage = getOatDir(paramPackage);
      try
      {
        this.mInstaller.createOatDir(paramPackage.getAbsolutePath(), paramString);
        return paramPackage.getAbsolutePath();
      }
      catch (InstallerConnection.InstallerException paramPackage)
      {
        Slog.w("PackageManager.DexOptimizer", "Failed to create oat dir", paramPackage);
        return null;
      }
    }
    return null;
  }
  
  static File getOatDir(File paramFile)
  {
    return new File(paramFile, "oat");
  }
  
  private static boolean isReserveApp(PackageParser.Package paramPackage)
  {
    if (paramPackage.applicationInfo.getCodePath() != null) {
      return paramPackage.applicationInfo.getCodePath().startsWith("/system/reserve");
    }
    return false;
  }
  
  public static boolean isUsedByOtherApps(PackageParser.Package paramPackage)
  {
    if (paramPackage.isForwardLocked()) {
      return false;
    }
    paramPackage = paramPackage.getAllCodePathsExcludingResourceOnly().iterator();
    if (paramPackage.hasNext())
    {
      String str = (String)paramPackage.next();
      for (;;)
      {
        int i;
        try
        {
          str = PackageManagerServiceUtils.realpath(new File(str));
          str = str.replace('/', '@');
          int[] arrayOfInt = UserManagerService.getInstance().getUserIds();
          i = 0;
          if (i >= arrayOfInt.length) {
            break;
          }
          if (!new File(Environment.getDataProfilesDeForeignDexDirectory(arrayOfInt[i]), str).exists()) {
            break label109;
          }
          return true;
        }
        catch (IOException localIOException)
        {
          Slog.w("PackageManager.DexOptimizer", "Failed to get canonical path", localIOException);
        }
        break;
        label109:
        i += 1;
      }
    }
    return false;
  }
  
  private int performDexOptLI(PackageParser.Package paramPackage, String[] paramArrayOfString1, String[] paramArrayOfString2, boolean paramBoolean, String paramString, CompilerStats.PackageStats paramPackageStats)
  {
    if (paramArrayOfString2 != null) {}
    while (!canOptimizePackage(paramPackage))
    {
      return 0;
      paramArrayOfString2 = InstructionSets.getAppDexInstructionSets(paramPackage.applicationInfo);
    }
    List localList = paramPackage.getAllCodePathsExcludingResourceOnly();
    int i4 = UserHandle.getSharedAppGid(paramPackage.applicationInfo.uid);
    boolean bool3 = DexFile.isProfileGuidedCompilerFilter(paramString);
    boolean bool1 = bool3;
    boolean bool2 = paramBoolean;
    String str1 = paramString;
    if (bool3)
    {
      bool1 = bool3;
      bool2 = paramBoolean;
      str1 = paramString;
      if (isUsedByOtherApps(paramPackage))
      {
        bool2 = false;
        str1 = PackageManagerServiceCompilerMapping.getNonProfileGuidedCompilerFilter(paramString);
        if (DexFile.isProfileGuidedCompilerFilter(str1)) {
          throw new IllegalStateException(str1);
        }
        bool1 = false;
      }
    }
    bool3 = false;
    paramBoolean = bool3;
    if (bool2)
    {
      paramBoolean = bool3;
      if (!bool1) {}
    }
    int i;
    int k;
    for (;;)
    {
      int j;
      String str3;
      String str4;
      int m;
      int i6;
      try
      {
        paramBoolean = this.mInstaller.mergeProfiles(i4, paramPackage.packageName);
        if ((paramPackage.applicationInfo.flags & 0x4000) != 0)
        {
          bool2 = true;
          if ((paramPackage.applicationInfo.flags & 0x2) == 0) {
            continue;
          }
          bool3 = true;
          i = 0;
          k = 1;
          String[] arrayOfString = InstructionSets.getDexCodeInstructionSets(paramArrayOfString2);
          int i5 = arrayOfString.length;
          j = 0;
          if (j >= i5) {
            break;
          }
          str3 = arrayOfString[j];
          Iterator localIterator = localList.iterator();
          if (!localIterator.hasNext()) {
            break label914;
          }
          str4 = (String)localIterator.next();
        }
      }
      catch (InstallerConnection.InstallerException paramString)
      {
        try
        {
          m = DexFile.getDexOptNeeded(str4, str3, str1, paramBoolean);
          i6 = adjustDexoptNeeded(m);
          if (PackageManagerService.DEBUG_DEXOPT) {
            Log.i("PackageManager.DexOptimizer", "DexoptNeeded for " + str4 + "@" + str1 + " is " + i6);
          }
          paramString = null;
          switch (i6)
          {
          case 0: 
          default: 
            throw new IllegalStateException("Invalid dexopt:" + i6);
          }
        }
        catch (IOException paramPackage)
        {
          Slog.w("PackageManager.DexOptimizer", "IOException reading apk: " + str4, paramPackage);
          return -1;
        }
        paramString = paramString;
        Slog.w("PackageManager.DexOptimizer", "Failed to merge profiles", paramString);
        paramBoolean = bool3;
        continue;
        bool2 = false;
        continue;
        bool3 = false;
        continue;
      }
      paramArrayOfString2 = "dex2oat";
      paramString = createOatDirIfSupported(paramPackage, str3);
      int n;
      for (;;)
      {
        String str2 = null;
        localObject = str2;
        if (paramArrayOfString1 == null) {
          break label569;
        }
        localObject = str2;
        if (paramArrayOfString1.length == 0) {
          break label569;
        }
        localObject = new StringBuilder();
        m = 0;
        n = paramArrayOfString1.length;
        while (m < n)
        {
          str2 = paramArrayOfString1[m];
          if (((StringBuilder)localObject).length() != 0) {
            ((StringBuilder)localObject).append(":");
          }
          ((StringBuilder)localObject).append(str2);
          m += 1;
        }
        paramArrayOfString2 = "patchoat";
        continue;
        paramArrayOfString2 = "self patchoat";
      }
      Object localObject = ((StringBuilder)localObject).toString();
      label569:
      Log.i("PackageManager.DexOptimizer", "Running dexopt (" + paramArrayOfString2 + ") on: " + str4 + " pkg=" + paramPackage.applicationInfo.packageName + " isa=" + str3 + " vmSafeMode=" + bool2 + " debuggable=" + bool3 + " target-filter=" + str1 + " oatDir = " + paramString + " sharedLibraries=" + (String)localObject);
      int i1;
      label703:
      label712:
      label728:
      label736:
      int i2;
      if ((paramPackage.isForwardLocked()) || (bool1))
      {
        i1 = 0;
        if (!bool1) {
          break label884;
        }
        m = 32;
        if (!str1.equals("speed")) {
          break label890;
        }
        n = 128;
        if (i1 == 0) {
          break label896;
        }
        i1 = 2;
        if (!bool2) {
          break label902;
        }
        i2 = 4;
        label744:
        if (!bool3) {
          break label908;
        }
      }
      label884:
      label890:
      label896:
      label902:
      label908:
      for (int i3 = 8;; i3 = 0)
      {
        n = adjustDexoptFlags(i3 | i1 | i2 | m | 0x10 | n);
        m = i;
        try
        {
          long l = System.currentTimeMillis();
          m = i;
          this.mInstaller.dexopt(str4, i4, paramPackage.packageName, str3, i6, paramString, n, str1, paramPackage.volumeUuid, (String)localObject);
          m = 1;
          n = 1;
          i = n;
          if (paramPackageStats == null) {
            break;
          }
          paramPackageStats.setCompileTime(str4, (int)(System.currentTimeMillis() - l));
          i = n;
        }
        catch (InstallerConnection.InstallerException paramArrayOfString2)
        {
          Slog.w("PackageManager.DexOptimizer", "Failed to dexopt", paramArrayOfString2);
          k = 0;
          i = m;
        }
        break;
        i1 = 1;
        break label703;
        m = 0;
        break label712;
        n = 0;
        break label728;
        i1 = 0;
        break label736;
        i2 = 0;
        break label744;
      }
      label914:
      j += 1;
    }
    if (k != 0)
    {
      if (i != 0) {
        return 1;
      }
      return 0;
    }
    return -1;
  }
  
  protected int adjustDexoptFlags(int paramInt)
  {
    return paramInt;
  }
  
  protected int adjustDexoptNeeded(int paramInt)
  {
    return paramInt;
  }
  
  void dumpDexoptState(IndentingPrintWriter paramIndentingPrintWriter, PackageParser.Package paramPackage)
  {
    String[] arrayOfString = InstructionSets.getDexCodeInstructionSets(InstructionSets.getAppDexInstructionSets(paramPackage.applicationInfo));
    List localList = paramPackage.getAllCodePathsExcludingResourceOnly();
    int i = 0;
    int j = arrayOfString.length;
    while (i < j)
    {
      String str1 = arrayOfString[i];
      paramIndentingPrintWriter.println("Instruction Set: " + str1);
      paramIndentingPrintWriter.increaseIndent();
      Iterator localIterator = localList.iterator();
      for (;;)
      {
        if (localIterator.hasNext())
        {
          String str2 = (String)localIterator.next();
          try
          {
            paramPackage = DexFile.getDexFileStatus(str2, str1);
            paramIndentingPrintWriter.println("path: " + str2);
            paramIndentingPrintWriter.println("status: " + paramPackage);
          }
          catch (IOException paramPackage)
          {
            for (;;)
            {
              paramPackage = "[Exception]: " + paramPackage.getMessage();
            }
          }
        }
      }
      paramIndentingPrintWriter.decreaseIndent();
      i += 1;
    }
  }
  
  String getOatFileCompilerFilter(PackageParser.Package paramPackage)
  {
    Object localObject = InstructionSets.getDexCodeInstructionSets(InstructionSets.getAppDexInstructionSets(paramPackage.applicationInfo));
    if ((paramPackage.applicationInfo.flags & 0x4) != 0)
    {
      localObject = localObject[0];
      try
      {
        paramPackage = DexFile.getOatFileCompilerFilter(paramPackage.baseCodePath, (String)localObject);
        return paramPackage;
      }
      catch (IOException paramPackage)
      {
        Slog.d("TAG", "Failed to getOatFileCompilerFilter", paramPackage);
      }
    }
    return null;
  }
  
  int performDexOpt(PackageParser.Package paramPackage, String[] paramArrayOfString1, String[] paramArrayOfString2, boolean paramBoolean, String paramString, CompilerStats.PackageStats paramPackageStats)
  {
    synchronized (this.mInstallLock)
    {
      boolean bool = this.mSystemReady;
      if (bool)
      {
        this.mDexoptWakeLock.setWorkSource(new WorkSource(paramPackage.applicationInfo.uid));
        this.mDexoptWakeLock.acquire();
      }
      try
      {
        int i = performDexOptLI(paramPackage, paramArrayOfString1, paramArrayOfString2, paramBoolean, paramString, paramPackageStats);
        if (bool) {
          this.mDexoptWakeLock.release();
        }
        return i;
      }
      finally
      {
        paramPackage = finally;
        if (bool) {
          this.mDexoptWakeLock.release();
        }
        throw paramPackage;
      }
    }
  }
  
  void systemReady()
  {
    this.mSystemReady = true;
  }
  
  public static class ForcedUpdatePackageDexOptimizer
    extends PackageDexOptimizer
  {
    public ForcedUpdatePackageDexOptimizer(Installer paramInstaller, Object paramObject, Context paramContext, String paramString)
    {
      super(paramObject, paramContext, paramString);
    }
    
    public ForcedUpdatePackageDexOptimizer(PackageDexOptimizer paramPackageDexOptimizer)
    {
      super();
    }
    
    protected int adjustDexoptNeeded(int paramInt)
    {
      return 1;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/PackageDexOptimizer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */