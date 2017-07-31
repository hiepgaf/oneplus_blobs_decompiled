package android.os;

import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.text.TextUtils;
import android.util.Log;
import java.io.File;

public class Environment
{
  public static String DIRECTORY_ALARMS;
  @Deprecated
  public static final String DIRECTORY_ANDROID = "Android";
  public static String DIRECTORY_DCIM = "DCIM";
  public static String DIRECTORY_DOCUMENTS = "Documents";
  public static String DIRECTORY_DOWNLOADS;
  public static String DIRECTORY_MOVIES;
  public static String DIRECTORY_MUSIC;
  public static String DIRECTORY_NOTIFICATIONS;
  public static String DIRECTORY_PICTURES;
  public static String DIRECTORY_PODCASTS;
  public static String DIRECTORY_RINGTONES;
  public static final String DIR_ANDROID = "Android";
  private static final File DIR_ANDROID_DATA;
  private static final File DIR_ANDROID_EXPAND;
  private static final File DIR_ANDROID_ROOT = getDirectory("ANDROID_ROOT", "/system");
  private static final File DIR_ANDROID_STORAGE;
  private static final String DIR_CACHE = "cache";
  private static final String DIR_DATA = "data";
  private static final File DIR_DOWNLOAD_CACHE;
  private static final String DIR_FILES = "files";
  private static final String DIR_MEDIA = "media";
  private static final String DIR_OBB = "obb";
  private static final File DIR_ODM_ROOT;
  private static final File DIR_OEM_ROOT;
  private static final File DIR_VENDOR_ROOT;
  private static final String ENV_ANDROID_DATA = "ANDROID_DATA";
  private static final String ENV_ANDROID_EXPAND = "ANDROID_EXPAND";
  private static final String ENV_ANDROID_ROOT = "ANDROID_ROOT";
  private static final String ENV_ANDROID_STORAGE = "ANDROID_STORAGE";
  private static final String ENV_DOWNLOAD_CACHE = "DOWNLOAD_CACHE";
  private static final String ENV_EXTERNAL_STORAGE = "EXTERNAL_STORAGE";
  private static final String ENV_ODM_ROOT = "ODM_ROOT";
  private static final String ENV_OEM_ROOT = "OEM_ROOT";
  private static final String ENV_VENDOR_ROOT = "VENDOR_ROOT";
  public static final String MEDIA_BAD_REMOVAL = "bad_removal";
  public static final String MEDIA_CHECKING = "checking";
  public static final String MEDIA_EJECTING = "ejecting";
  public static final String MEDIA_MOUNTED = "mounted";
  public static final String MEDIA_MOUNTED_READ_ONLY = "mounted_ro";
  public static final String MEDIA_NOFS = "nofs";
  public static final String MEDIA_REMOVED = "removed";
  public static final String MEDIA_SHARED = "shared";
  public static final String MEDIA_UNKNOWN = "unknown";
  public static final String MEDIA_UNMOUNTABLE = "unmountable";
  public static final String MEDIA_UNMOUNTED = "unmounted";
  public static final String[] STANDARD_DIRECTORIES = { DIRECTORY_MUSIC, DIRECTORY_PODCASTS, DIRECTORY_RINGTONES, DIRECTORY_ALARMS, DIRECTORY_NOTIFICATIONS, DIRECTORY_PICTURES, DIRECTORY_MOVIES, DIRECTORY_DOWNLOADS, DIRECTORY_DCIM, DIRECTORY_DOCUMENTS };
  private static final String TAG = "Environment";
  private static UserEnvironment sCurrentUser;
  private static boolean sUserRequired;
  
  static
  {
    DIR_ANDROID_DATA = getDirectory("ANDROID_DATA", "/data");
    DIR_ANDROID_EXPAND = getDirectory("ANDROID_EXPAND", "/mnt/expand");
    DIR_ANDROID_STORAGE = getDirectory("ANDROID_STORAGE", "/storage");
    DIR_DOWNLOAD_CACHE = getDirectory("DOWNLOAD_CACHE", "/cache");
    DIR_OEM_ROOT = getDirectory("OEM_ROOT", "/oem");
    DIR_ODM_ROOT = getDirectory("ODM_ROOT", "/odm");
    DIR_VENDOR_ROOT = getDirectory("VENDOR_ROOT", "/vendor");
    initForCurrentUser();
    DIRECTORY_MUSIC = "Music";
    DIRECTORY_PODCASTS = "Podcasts";
    DIRECTORY_RINGTONES = "Ringtones";
    DIRECTORY_ALARMS = "Alarms";
    DIRECTORY_NOTIFICATIONS = "Notifications";
    DIRECTORY_PICTURES = "Pictures";
    DIRECTORY_MOVIES = "Movies";
    DIRECTORY_DOWNLOADS = "Download";
  }
  
  public static File[] buildExternalStorageAndroidDataDirs()
  {
    throwIfUserRequired();
    return sCurrentUser.buildExternalStorageAndroidDataDirs();
  }
  
  public static File[] buildExternalStorageAppCacheDirs(String paramString)
  {
    throwIfUserRequired();
    return sCurrentUser.buildExternalStorageAppCacheDirs(paramString);
  }
  
  public static File[] buildExternalStorageAppDataDirs(String paramString)
  {
    throwIfUserRequired();
    return sCurrentUser.buildExternalStorageAppDataDirs(paramString);
  }
  
  public static File[] buildExternalStorageAppFilesDirs(String paramString)
  {
    throwIfUserRequired();
    return sCurrentUser.buildExternalStorageAppFilesDirs(paramString);
  }
  
  public static File[] buildExternalStorageAppMediaDirs(String paramString)
  {
    throwIfUserRequired();
    return sCurrentUser.buildExternalStorageAppMediaDirs(paramString);
  }
  
  public static File[] buildExternalStorageAppObbDirs(String paramString)
  {
    throwIfUserRequired();
    return sCurrentUser.buildExternalStorageAppObbDirs(paramString);
  }
  
  public static File buildPath(File paramFile, String... paramVarArgs)
  {
    int i = 0;
    int j = paramVarArgs.length;
    if (i < j)
    {
      String str = paramVarArgs[i];
      if (paramFile == null) {}
      for (paramFile = new File(str);; paramFile = new File(paramFile, str))
      {
        i += 1;
        break;
      }
    }
    return paramFile;
  }
  
  public static File[] buildPaths(File[] paramArrayOfFile, String... paramVarArgs)
  {
    File[] arrayOfFile = new File[paramArrayOfFile.length];
    int i = 0;
    while (i < paramArrayOfFile.length)
    {
      arrayOfFile[i] = buildPath(paramArrayOfFile[i], paramVarArgs);
      i += 1;
    }
    return arrayOfFile;
  }
  
  public static File getDataAppDirectory(String paramString)
  {
    return new File(getDataDirectory(paramString), "app");
  }
  
  public static File getDataAppEphemeralDirectory(String paramString)
  {
    return new File(getDataDirectory(paramString), "app-ephemeral");
  }
  
  public static File getDataDirectory()
  {
    return DIR_ANDROID_DATA;
  }
  
  public static File getDataDirectory(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return DIR_ANDROID_DATA;
    }
    return new File("/mnt/expand/" + paramString);
  }
  
  public static File getDataMiscCeDirectory(int paramInt)
  {
    return buildPath(getDataDirectory(), new String[] { "misc_ce", String.valueOf(paramInt) });
  }
  
  public static File getDataMiscDeDirectory(int paramInt)
  {
    return buildPath(getDataDirectory(), new String[] { "misc_de", String.valueOf(paramInt) });
  }
  
  public static File getDataMiscDirectory()
  {
    return new File(getDataDirectory(), "misc");
  }
  
  public static File getDataPreloadsAppsDirectory()
  {
    return new File(getDataPreloadsDirectory(), "apps");
  }
  
  public static File getDataPreloadsDemoDirectory()
  {
    return new File(getDataPreloadsDirectory(), "demo");
  }
  
  public static File getDataPreloadsDirectory()
  {
    return new File(getDataDirectory(), "preloads");
  }
  
  public static File getDataPreloadsMediaDirectory()
  {
    return new File(getDataPreloadsDirectory(), "media");
  }
  
  private static File getDataProfilesDeDirectory(int paramInt)
  {
    return buildPath(getDataDirectory(), new String[] { "misc", "profiles", "cur", String.valueOf(paramInt) });
  }
  
  public static File getDataProfilesDeForeignDexDirectory(int paramInt)
  {
    return buildPath(getDataProfilesDeDirectory(paramInt), new String[] { "foreign-dex" });
  }
  
  public static File getDataProfilesDePackageDirectory(int paramInt, String paramString)
  {
    return buildPath(getDataProfilesDeDirectory(paramInt), new String[] { paramString });
  }
  
  public static File getDataSystemCeDirectory()
  {
    return buildPath(getDataDirectory(), new String[] { "system_ce" });
  }
  
  public static File getDataSystemCeDirectory(int paramInt)
  {
    return buildPath(getDataDirectory(), new String[] { "system_ce", String.valueOf(paramInt) });
  }
  
  public static File getDataSystemDeDirectory()
  {
    return buildPath(getDataDirectory(), new String[] { "system_de" });
  }
  
  public static File getDataSystemDeDirectory(int paramInt)
  {
    return buildPath(getDataDirectory(), new String[] { "system_de", String.valueOf(paramInt) });
  }
  
  public static File getDataSystemDirectory()
  {
    return new File(getDataDirectory(), "system");
  }
  
  public static File getDataUserCeDirectory(String paramString)
  {
    return new File(getDataDirectory(paramString), "user");
  }
  
  public static File getDataUserCeDirectory(String paramString, int paramInt)
  {
    return new File(getDataUserCeDirectory(paramString), String.valueOf(paramInt));
  }
  
  public static File getDataUserCePackageDirectory(String paramString1, int paramInt, String paramString2)
  {
    return new File(getDataUserCeDirectory(paramString1, paramInt), paramString2);
  }
  
  public static File getDataUserDeDirectory(String paramString)
  {
    return new File(getDataDirectory(paramString), "user_de");
  }
  
  public static File getDataUserDeDirectory(String paramString, int paramInt)
  {
    return new File(getDataUserDeDirectory(paramString), String.valueOf(paramInt));
  }
  
  public static File getDataUserDePackageDirectory(String paramString1, int paramInt, String paramString2)
  {
    return new File(getDataUserDeDirectory(paramString1, paramInt), paramString2);
  }
  
  static File getDirectory(String paramString1, String paramString2)
  {
    paramString1 = System.getenv(paramString1);
    if (paramString1 == null) {
      return new File(paramString2);
    }
    return new File(paramString1);
  }
  
  public static File getDownloadCacheDirectory()
  {
    return DIR_DOWNLOAD_CACHE;
  }
  
  public static File getExpandDirectory()
  {
    return DIR_ANDROID_EXPAND;
  }
  
  public static File getExternalStorageDirectory()
  {
    throwIfUserRequired();
    return sCurrentUser.getExternalDirs()[0];
  }
  
  public static File getExternalStoragePublicDirectory(String paramString)
  {
    throwIfUserRequired();
    return sCurrentUser.buildExternalStoragePublicDirs(paramString)[0];
  }
  
  public static String getExternalStorageState()
  {
    return getExternalStorageState(sCurrentUser.getExternalDirs()[0]);
  }
  
  public static String getExternalStorageState(File paramFile)
  {
    paramFile = StorageManager.getStorageVolume(paramFile, UserHandle.myUserId());
    if (paramFile != null) {
      return paramFile.getState();
    }
    return "unknown";
  }
  
  public static File getLegacyExternalStorageDirectory()
  {
    return new File(System.getenv("EXTERNAL_STORAGE"));
  }
  
  public static File getLegacyExternalStorageObbDirectory()
  {
    return buildPath(getLegacyExternalStorageDirectory(), new String[] { "Android", "obb" });
  }
  
  public static File getOdmDirectory()
  {
    return DIR_ODM_ROOT;
  }
  
  public static File getOemDirectory()
  {
    return DIR_OEM_ROOT;
  }
  
  public static File getReferenceProfile(String paramString)
  {
    return buildPath(getDataDirectory(), new String[] { "misc", "profiles", "ref", paramString });
  }
  
  public static File getRootDirectory()
  {
    return DIR_ANDROID_ROOT;
  }
  
  public static File getStorageDirectory()
  {
    return DIR_ANDROID_STORAGE;
  }
  
  @Deprecated
  public static String getStorageState(File paramFile)
  {
    return getExternalStorageState(paramFile);
  }
  
  @Deprecated
  public static File getUserConfigDirectory(int paramInt)
  {
    return new File(new File(new File(getDataDirectory(), "misc"), "user"), Integer.toString(paramInt));
  }
  
  @Deprecated
  public static File getUserSystemDirectory(int paramInt)
  {
    return new File(new File(getDataSystemDirectory(), "users"), Integer.toString(paramInt));
  }
  
  public static File getVendorDirectory()
  {
    return DIR_VENDOR_ROOT;
  }
  
  public static void initForCurrentUser()
  {
    sCurrentUser = new UserEnvironment(UserHandle.myUserId());
  }
  
  public static boolean isExternalStorageEmulated()
  {
    if (isStorageDisabled()) {
      return false;
    }
    return isExternalStorageEmulated(sCurrentUser.getExternalDirs()[0]);
  }
  
  public static boolean isExternalStorageEmulated(File paramFile)
  {
    StorageVolume localStorageVolume = StorageManager.getStorageVolume(paramFile, UserHandle.myUserId());
    if (localStorageVolume != null) {
      return localStorageVolume.isEmulated();
    }
    throw new IllegalArgumentException("Failed to find storage device at " + paramFile);
  }
  
  public static boolean isExternalStorageRemovable()
  {
    if (isStorageDisabled()) {
      return false;
    }
    return isExternalStorageRemovable(sCurrentUser.getExternalDirs()[0]);
  }
  
  public static boolean isExternalStorageRemovable(File paramFile)
  {
    StorageVolume localStorageVolume = StorageManager.getStorageVolume(paramFile, UserHandle.myUserId());
    if (localStorageVolume != null) {
      return localStorageVolume.isRemovable();
    }
    throw new IllegalArgumentException("Failed to find storage device at " + paramFile);
  }
  
  public static boolean isStandardDirectory(String paramString)
  {
    String[] arrayOfString = STANDARD_DIRECTORIES;
    int j = arrayOfString.length;
    int i = 0;
    while (i < j)
    {
      if (arrayOfString[i].equals(paramString)) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private static boolean isStorageDisabled()
  {
    return SystemProperties.getBoolean("config.disable_storage", false);
  }
  
  public static File maybeTranslateEmulatedPathToInternal(File paramFile)
  {
    return StorageManager.maybeTranslateEmulatedPathToInternal(paramFile);
  }
  
  public static void setUserRequired(boolean paramBoolean)
  {
    sUserRequired = paramBoolean;
  }
  
  private static void throwIfUserRequired()
  {
    if (sUserRequired) {
      Log.wtf("Environment", "Path requests must specify a user by using UserEnvironment", new Throwable());
    }
  }
  
  public static class UserEnvironment
  {
    private final int mUserId;
    
    public UserEnvironment(int paramInt)
    {
      this.mUserId = paramInt;
    }
    
    public File[] buildExternalStorageAndroidDataDirs()
    {
      return Environment.buildPaths(getExternalDirs(), new String[] { "Android", "data" });
    }
    
    public File[] buildExternalStorageAndroidObbDirs()
    {
      return Environment.buildPaths(getExternalDirs(), new String[] { "Android", "obb" });
    }
    
    public File[] buildExternalStorageAppCacheDirs(String paramString)
    {
      return Environment.buildPaths(getExternalDirs(), new String[] { "Android", "data", paramString, "cache" });
    }
    
    public File[] buildExternalStorageAppDataDirs(String paramString)
    {
      return Environment.buildPaths(getExternalDirs(), new String[] { "Android", "data", paramString });
    }
    
    public File[] buildExternalStorageAppFilesDirs(String paramString)
    {
      return Environment.buildPaths(getExternalDirs(), new String[] { "Android", "data", paramString, "files" });
    }
    
    public File[] buildExternalStorageAppMediaDirs(String paramString)
    {
      return Environment.buildPaths(getExternalDirs(), new String[] { "Android", "media", paramString });
    }
    
    public File[] buildExternalStorageAppObbDirs(String paramString)
    {
      return Environment.buildPaths(getExternalDirs(), new String[] { "Android", "obb", paramString });
    }
    
    public File[] buildExternalStoragePublicDirs(String paramString)
    {
      return Environment.buildPaths(getExternalDirs(), new String[] { paramString });
    }
    
    public File[] getExternalDirs()
    {
      StorageVolume[] arrayOfStorageVolume = StorageManager.getVolumeList(this.mUserId, 256);
      File[] arrayOfFile = new File[arrayOfStorageVolume.length];
      int i = 0;
      while (i < arrayOfStorageVolume.length)
      {
        arrayOfFile[i] = arrayOfStorageVolume[i].getPathFile();
        i += 1;
      }
      return arrayOfFile;
    }
    
    @Deprecated
    public File getExternalStorageDirectory()
    {
      return getExternalDirs()[0];
    }
    
    @Deprecated
    public File getExternalStoragePublicDirectory(String paramString)
    {
      return buildExternalStoragePublicDirs(paramString)[0];
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/Environment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */