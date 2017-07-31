package com.android.server.pm;

import android.content.Context;
import android.content.pm.PackageStats;
import android.os.Build;
import android.util.Slog;
import com.android.internal.os.InstallerConnection;
import com.android.internal.os.InstallerConnection.InstallerException;
import com.android.server.SystemService;
import dalvik.system.VMRuntime;
import java.util.Arrays;

public final class Installer
  extends SystemService
{
  public static final int DEXOPT_BOOTCOMPLETE = 16;
  public static final int DEXOPT_DEBUGGABLE = 8;
  public static final int DEXOPT_OTA = 64;
  public static final int DEXOPT_PROFILE_GUIDED = 32;
  public static final int DEXOPT_PUBLIC = 2;
  public static final int DEXOPT_SAFEMODE = 4;
  public static final int DEXOPT_SPEED = 128;
  public static final int FLAG_CLEAR_CACHE_ONLY = 256;
  public static final int FLAG_CLEAR_CODE_CACHE_ONLY = 512;
  private static final String TAG = "Installer";
  private final InstallerConnection mInstaller;
  
  public Installer(Context paramContext)
  {
    super(paramContext);
    this.mInstaller = new InstallerConnection();
  }
  
  Installer(Context paramContext, InstallerConnection paramInstallerConnection)
  {
    super(paramContext);
    this.mInstaller = paramInstallerConnection;
  }
  
  private static void assertValidInstructionSet(String paramString)
    throws InstallerConnection.InstallerException
  {
    String[] arrayOfString = Build.SUPPORTED_ABIS;
    int i = 0;
    int j = arrayOfString.length;
    while (i < j)
    {
      if (VMRuntime.getInstructionSet(arrayOfString[i]).equals(paramString)) {
        return;
      }
      i += 1;
    }
    throw new InstallerConnection.InstallerException("Invalid instruction set: " + paramString);
  }
  
  public void clearAppData(String paramString1, String paramString2, int paramInt1, int paramInt2, long paramLong)
    throws InstallerConnection.InstallerException
  {
    this.mInstaller.execute("clear_app_data", new Object[] { paramString1, paramString2, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Long.valueOf(paramLong) });
  }
  
  public void clearAppProfiles(String paramString)
    throws InstallerConnection.InstallerException
  {
    this.mInstaller.execute("clear_app_profiles", new Object[] { paramString });
  }
  
  public void createAppData(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3, String paramString3, int paramInt4)
    throws InstallerConnection.InstallerException
  {
    this.mInstaller.execute("create_app_data", new Object[] { paramString1, paramString2, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), paramString3, Integer.valueOf(paramInt4) });
  }
  
  public void createOatDir(String paramString1, String paramString2)
    throws InstallerConnection.InstallerException
  {
    this.mInstaller.execute("createoatdir", new Object[] { paramString1, paramString2 });
  }
  
  public void createUserData(String paramString, int paramInt1, int paramInt2, int paramInt3)
    throws InstallerConnection.InstallerException
  {
    this.mInstaller.execute("create_user_data", new Object[] { paramString, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3) });
  }
  
  public void deleteOdex(String paramString1, String paramString2, String paramString3)
    throws InstallerConnection.InstallerException
  {
    this.mInstaller.execute("delete_odex", new Object[] { paramString1, paramString2, paramString3 });
  }
  
  public void destroyAppData(String paramString1, String paramString2, int paramInt1, int paramInt2, long paramLong)
    throws InstallerConnection.InstallerException
  {
    this.mInstaller.execute("destroy_app_data", new Object[] { paramString1, paramString2, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Long.valueOf(paramLong) });
  }
  
  public void destroyAppProfiles(String paramString)
    throws InstallerConnection.InstallerException
  {
    this.mInstaller.execute("destroy_app_profiles", new Object[] { paramString });
  }
  
  public void destroyUserData(String paramString, int paramInt1, int paramInt2)
    throws InstallerConnection.InstallerException
  {
    this.mInstaller.execute("destroy_user_data", new Object[] { paramString, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) });
  }
  
  public void dexopt(String paramString1, int paramInt1, String paramString2, int paramInt2, int paramInt3, String paramString3, String paramString4, String paramString5)
    throws InstallerConnection.InstallerException
  {
    assertValidInstructionSet(paramString2);
    this.mInstaller.dexopt(paramString1, paramInt1, paramString2, paramInt2, paramInt3, paramString3, paramString4, paramString5);
  }
  
  public void dexopt(String paramString1, int paramInt1, String paramString2, String paramString3, int paramInt2, String paramString4, int paramInt3, String paramString5, String paramString6, String paramString7)
    throws InstallerConnection.InstallerException
  {
    assertValidInstructionSet(paramString3);
    this.mInstaller.dexopt(paramString1, paramInt1, paramString2, paramString3, paramInt2, paramString4, paramInt3, paramString5, paramString6, paramString7);
  }
  
  public boolean dumpProfiles(String paramString1, String paramString2, String paramString3)
    throws InstallerConnection.InstallerException
  {
    return this.mInstaller.dumpProfiles(paramString1, paramString2, paramString3);
  }
  
  public void freeCache(String paramString, long paramLong)
    throws InstallerConnection.InstallerException
  {
    this.mInstaller.execute("freecache", new Object[] { paramString, Long.valueOf(paramLong) });
  }
  
  public long getAppDataInode(String paramString1, String paramString2, int paramInt1, int paramInt2)
    throws InstallerConnection.InstallerException
  {
    paramString1 = this.mInstaller.execute("get_app_data_inode", new Object[] { paramString1, paramString2, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) });
    try
    {
      long l = Long.parseLong(paramString1[1]);
      return l;
    }
    catch (ArrayIndexOutOfBoundsException|NumberFormatException paramString2)
    {
      throw new InstallerConnection.InstallerException("Invalid inode result: " + Arrays.toString(paramString1));
    }
  }
  
  public void getAppSize(String paramString1, String paramString2, int paramInt1, int paramInt2, long paramLong, String paramString3, PackageStats paramPackageStats)
    throws InstallerConnection.InstallerException
  {
    paramString1 = this.mInstaller.execute("get_app_size", new Object[] { paramString1, paramString2, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Long.valueOf(paramLong), paramString3 });
    try
    {
      paramPackageStats.codeSize += Long.parseLong(paramString1[1]);
      paramPackageStats.dataSize += Long.parseLong(paramString1[2]);
      paramPackageStats.cacheSize += Long.parseLong(paramString1[3]);
      return;
    }
    catch (ArrayIndexOutOfBoundsException|NumberFormatException paramString2)
    {
      throw new InstallerConnection.InstallerException("Invalid size result: " + Arrays.toString(paramString1));
    }
  }
  
  public void idmap(String paramString1, String paramString2, int paramInt)
    throws InstallerConnection.InstallerException
  {
    this.mInstaller.execute("idmap", new Object[] { paramString1, paramString2, Integer.valueOf(paramInt) });
  }
  
  public void initPreloadFiles()
    throws InstallerConnection.InstallerException
  {
    this.mInstaller.execute("init_preload_files", new Object[0]);
  }
  
  public void linkFile(String paramString1, String paramString2, String paramString3)
    throws InstallerConnection.InstallerException
  {
    this.mInstaller.execute("linkfile", new Object[] { paramString1, paramString2, paramString3 });
  }
  
  public void linkNativeLibraryDirectory(String paramString1, String paramString2, String paramString3, int paramInt)
    throws InstallerConnection.InstallerException
  {
    this.mInstaller.execute("linklib", new Object[] { paramString1, paramString2, paramString3, Integer.valueOf(paramInt) });
  }
  
  public void markBootComplete(String paramString)
    throws InstallerConnection.InstallerException
  {
    assertValidInstructionSet(paramString);
    this.mInstaller.execute("markbootcomplete", new Object[] { paramString });
  }
  
  public boolean mergeProfiles(int paramInt, String paramString)
    throws InstallerConnection.InstallerException
  {
    return this.mInstaller.mergeProfiles(paramInt, paramString);
  }
  
  public void migrateAppData(String paramString1, String paramString2, int paramInt1, int paramInt2)
    throws InstallerConnection.InstallerException
  {
    this.mInstaller.execute("migrate_app_data", new Object[] { paramString1, paramString2, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) });
  }
  
  public void moveAb(String paramString1, String paramString2, String paramString3)
    throws InstallerConnection.InstallerException
  {
    this.mInstaller.execute("move_ab", new Object[] { paramString1, paramString2, paramString3 });
  }
  
  public void moveCompleteApp(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, String paramString5, int paramInt2)
    throws InstallerConnection.InstallerException
  {
    this.mInstaller.execute("move_complete_app", new Object[] { paramString1, paramString2, paramString3, paramString4, Integer.valueOf(paramInt1), paramString5, Integer.valueOf(paramInt2) });
  }
  
  public void onStart()
  {
    Slog.i("Installer", "Waiting for installd to be ready.");
    this.mInstaller.waitForConnection();
  }
  
  public void restoreconAppData(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3, String paramString3)
    throws InstallerConnection.InstallerException
  {
    this.mInstaller.execute("restorecon_app_data", new Object[] { paramString1, paramString2, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), paramString3 });
  }
  
  public void rmPackageDir(String paramString)
    throws InstallerConnection.InstallerException
  {
    this.mInstaller.execute("rmpackagedir", new Object[] { paramString });
  }
  
  public void rmdex(String paramString1, String paramString2)
    throws InstallerConnection.InstallerException
  {
    assertValidInstructionSet(paramString2);
    this.mInstaller.execute("rmdex", new Object[] { paramString1, paramString2 });
  }
  
  public void setWarnIfHeld(Object paramObject)
  {
    this.mInstaller.setWarnIfHeld(paramObject);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/Installer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */