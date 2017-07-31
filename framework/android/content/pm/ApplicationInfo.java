package android.content.pm;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Printer;
import com.android.internal.util.ArrayUtils;
import java.io.File;
import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class ApplicationInfo
  extends PackageItemInfo
  implements Parcelable
{
  public static final Parcelable.Creator<ApplicationInfo> CREATOR = new Parcelable.Creator()
  {
    public ApplicationInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ApplicationInfo(paramAnonymousParcel, null);
    }
    
    public ApplicationInfo[] newArray(int paramAnonymousInt)
    {
      return new ApplicationInfo[paramAnonymousInt];
    }
  };
  public static final int FLAG_ALLOW_BACKUP = 32768;
  public static final int FLAG_ALLOW_CLEAR_USER_DATA = 64;
  public static final int FLAG_ALLOW_TASK_REPARENTING = 32;
  public static final int FLAG_DEBUGGABLE = 2;
  public static final int FLAG_EXTERNAL_STORAGE = 262144;
  public static final int FLAG_EXTRACT_NATIVE_LIBS = 268435456;
  public static final int FLAG_FACTORY_TEST = 16;
  public static final int FLAG_FULL_BACKUP_ONLY = 67108864;
  public static final int FLAG_HARDWARE_ACCELERATED = 536870912;
  public static final int FLAG_HAS_CODE = 4;
  public static final int FLAG_INSTALLED = 8388608;
  public static final int FLAG_IS_DATA_ONLY = 16777216;
  public static final int FLAG_IS_GAME = 33554432;
  public static final int FLAG_KILL_AFTER_RESTORE = 65536;
  public static final int FLAG_LARGE_HEAP = 1048576;
  public static final int FLAG_MULTIARCH = Integer.MIN_VALUE;
  public static final int FLAG_PERSISTENT = 8;
  public static final int FLAG_RESIZEABLE_FOR_SCREENS = 4096;
  public static final int FLAG_RESTORE_ANY_VERSION = 131072;
  public static final int FLAG_STOPPED = 2097152;
  public static final int FLAG_SUPPORTS_LARGE_SCREENS = 2048;
  public static final int FLAG_SUPPORTS_NORMAL_SCREENS = 1024;
  public static final int FLAG_SUPPORTS_RTL = 4194304;
  public static final int FLAG_SUPPORTS_SCREEN_DENSITIES = 8192;
  public static final int FLAG_SUPPORTS_SMALL_SCREENS = 512;
  public static final int FLAG_SUPPORTS_XLARGE_SCREENS = 524288;
  public static final int FLAG_SUSPENDED = 1073741824;
  public static final int FLAG_SYSTEM = 1;
  public static final int FLAG_TEST_ONLY = 256;
  public static final int FLAG_UPDATED_SYSTEM_APP = 128;
  public static final int FLAG_USES_CLEARTEXT_TRAFFIC = 134217728;
  public static final int FLAG_VM_SAFE_MODE = 16384;
  public static final int PRIVATE_FLAG_AUTOPLAY = 128;
  public static final int PRIVATE_FLAG_BACKUP_IN_FOREGROUND = 4096;
  public static final int PRIVATE_FLAG_CANT_SAVE_STATE = 2;
  public static final int PRIVATE_FLAG_DEFAULT_TO_DEVICE_PROTECTED_STORAGE = 32;
  public static final int PRIVATE_FLAG_DEX2OAT_ROLLBACK = 32768;
  public static final int PRIVATE_FLAG_DIRECT_BOOT_AWARE = 64;
  public static final int PRIVATE_FLAG_ENABLE_DEBUGGER = 16384;
  public static final int PRIVATE_FLAG_EPHEMERAL = 512;
  public static final int PRIVATE_FLAG_FORWARD_LOCK = 4;
  public static final int PRIVATE_FLAG_HAS_DOMAIN_URLS = 16;
  public static final int PRIVATE_FLAG_HIDDEN = 1;
  public static final int PRIVATE_FLAG_MINIMAL_TRIM_MEMORY = 8192;
  public static final int PRIVATE_FLAG_PARTIALLY_DIRECT_BOOT_AWARE = 256;
  public static final int PRIVATE_FLAG_PRIVILEGED = 8;
  public static final int PRIVATE_FLAG_REQUIRED_FOR_SYSTEM_USER = 1024;
  public static final int PRIVATE_FLAG_RESIZEABLE_ACTIVITIES = 2048;
  public String backupAgentName;
  public String className;
  public int compatibleWidthLimitDp = 0;
  @Deprecated
  public String credentialEncryptedDataDir;
  public String credentialProtectedDataDir;
  public String dataDir;
  public int descriptionRes;
  @Deprecated
  public String deviceEncryptedDataDir;
  public String deviceProtectedDataDir;
  public boolean enabled = true;
  public int enabledSetting = 0;
  public int flags = 0;
  public int fullBackupContent = 0;
  public int installLocation = -1;
  public int largestWidthLimitDp = 0;
  public String manageSpaceActivityName;
  public int minSdkVersion;
  public String nativeLibraryDir;
  public String nativeLibraryRootDir;
  public boolean nativeLibraryRootRequiresIsa;
  public int networkSecurityConfigRes;
  public int overrideDensity = 0;
  public int overrideRes = 0;
  public String permission;
  public String primaryCpuAbi;
  public int privateFlags;
  public String processName;
  public String publicSourceDir;
  public int requiresSmallestWidthDp = 0;
  public String[] resourceDirs;
  public String scanPublicSourceDir;
  public String scanSourceDir;
  public String secondaryCpuAbi;
  public String secondaryNativeLibraryDir;
  public String seinfo = "default";
  public String[] sharedLibraryFiles;
  public String sourceDir;
  public String[] splitPublicSourceDirs;
  public String[] splitSourceDirs;
  public int targetSdkVersion;
  public String taskAffinity;
  public int theme;
  public int uiOptions = 0;
  public int uid;
  public int versionCode;
  public String volumeUuid;
  public int whiteListed = 0;
  
  public ApplicationInfo() {}
  
  public ApplicationInfo(ApplicationInfo paramApplicationInfo)
  {
    super(paramApplicationInfo);
    this.taskAffinity = paramApplicationInfo.taskAffinity;
    this.permission = paramApplicationInfo.permission;
    this.processName = paramApplicationInfo.processName;
    this.className = paramApplicationInfo.className;
    this.theme = paramApplicationInfo.theme;
    this.flags = paramApplicationInfo.flags;
    this.privateFlags = paramApplicationInfo.privateFlags;
    this.overrideRes = paramApplicationInfo.overrideRes;
    this.overrideDensity = paramApplicationInfo.overrideDensity;
    this.whiteListed = paramApplicationInfo.whiteListed;
    this.requiresSmallestWidthDp = paramApplicationInfo.requiresSmallestWidthDp;
    this.compatibleWidthLimitDp = paramApplicationInfo.compatibleWidthLimitDp;
    this.largestWidthLimitDp = paramApplicationInfo.largestWidthLimitDp;
    this.volumeUuid = paramApplicationInfo.volumeUuid;
    this.scanSourceDir = paramApplicationInfo.scanSourceDir;
    this.scanPublicSourceDir = paramApplicationInfo.scanPublicSourceDir;
    this.sourceDir = paramApplicationInfo.sourceDir;
    this.publicSourceDir = paramApplicationInfo.publicSourceDir;
    this.splitSourceDirs = paramApplicationInfo.splitSourceDirs;
    this.splitPublicSourceDirs = paramApplicationInfo.splitPublicSourceDirs;
    this.nativeLibraryDir = paramApplicationInfo.nativeLibraryDir;
    this.secondaryNativeLibraryDir = paramApplicationInfo.secondaryNativeLibraryDir;
    this.nativeLibraryRootDir = paramApplicationInfo.nativeLibraryRootDir;
    this.nativeLibraryRootRequiresIsa = paramApplicationInfo.nativeLibraryRootRequiresIsa;
    this.primaryCpuAbi = paramApplicationInfo.primaryCpuAbi;
    this.secondaryCpuAbi = paramApplicationInfo.secondaryCpuAbi;
    this.resourceDirs = paramApplicationInfo.resourceDirs;
    this.seinfo = paramApplicationInfo.seinfo;
    this.sharedLibraryFiles = paramApplicationInfo.sharedLibraryFiles;
    this.dataDir = paramApplicationInfo.dataDir;
    String str = paramApplicationInfo.deviceProtectedDataDir;
    this.deviceProtectedDataDir = str;
    this.deviceEncryptedDataDir = str;
    str = paramApplicationInfo.credentialProtectedDataDir;
    this.credentialProtectedDataDir = str;
    this.credentialEncryptedDataDir = str;
    this.uid = paramApplicationInfo.uid;
    this.minSdkVersion = paramApplicationInfo.minSdkVersion;
    this.targetSdkVersion = paramApplicationInfo.targetSdkVersion;
    this.versionCode = paramApplicationInfo.versionCode;
    this.enabled = paramApplicationInfo.enabled;
    this.enabledSetting = paramApplicationInfo.enabledSetting;
    this.installLocation = paramApplicationInfo.installLocation;
    this.manageSpaceActivityName = paramApplicationInfo.manageSpaceActivityName;
    this.descriptionRes = paramApplicationInfo.descriptionRes;
    this.uiOptions = paramApplicationInfo.uiOptions;
    this.backupAgentName = paramApplicationInfo.backupAgentName;
    this.fullBackupContent = paramApplicationInfo.fullBackupContent;
    this.networkSecurityConfigRes = paramApplicationInfo.networkSecurityConfigRes;
  }
  
  private ApplicationInfo(Parcel paramParcel)
  {
    super(paramParcel);
    this.taskAffinity = paramParcel.readString();
    this.permission = paramParcel.readString();
    this.processName = paramParcel.readString();
    this.className = paramParcel.readString();
    this.theme = paramParcel.readInt();
    this.flags = paramParcel.readInt();
    this.privateFlags = paramParcel.readInt();
    this.overrideRes = paramParcel.readInt();
    this.overrideDensity = paramParcel.readInt();
    this.whiteListed = paramParcel.readInt();
    this.requiresSmallestWidthDp = paramParcel.readInt();
    this.compatibleWidthLimitDp = paramParcel.readInt();
    this.largestWidthLimitDp = paramParcel.readInt();
    this.volumeUuid = paramParcel.readString();
    this.scanSourceDir = paramParcel.readString();
    this.scanPublicSourceDir = paramParcel.readString();
    this.sourceDir = paramParcel.readString();
    this.publicSourceDir = paramParcel.readString();
    this.splitSourceDirs = paramParcel.readStringArray();
    this.splitPublicSourceDirs = paramParcel.readStringArray();
    this.nativeLibraryDir = paramParcel.readString();
    this.secondaryNativeLibraryDir = paramParcel.readString();
    this.nativeLibraryRootDir = paramParcel.readString();
    if (paramParcel.readInt() != 0)
    {
      bool1 = true;
      this.nativeLibraryRootRequiresIsa = bool1;
      this.primaryCpuAbi = paramParcel.readString();
      this.secondaryCpuAbi = paramParcel.readString();
      this.resourceDirs = paramParcel.readStringArray();
      this.seinfo = paramParcel.readString();
      this.sharedLibraryFiles = paramParcel.readStringArray();
      this.dataDir = paramParcel.readString();
      String str = paramParcel.readString();
      this.deviceProtectedDataDir = str;
      this.deviceEncryptedDataDir = str;
      str = paramParcel.readString();
      this.credentialProtectedDataDir = str;
      this.credentialEncryptedDataDir = str;
      this.uid = paramParcel.readInt();
      this.minSdkVersion = paramParcel.readInt();
      this.targetSdkVersion = paramParcel.readInt();
      this.versionCode = paramParcel.readInt();
      if (paramParcel.readInt() == 0) {
        break label471;
      }
    }
    label471:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.enabled = bool1;
      this.enabledSetting = paramParcel.readInt();
      this.installLocation = paramParcel.readInt();
      this.manageSpaceActivityName = paramParcel.readString();
      this.backupAgentName = paramParcel.readString();
      this.descriptionRes = paramParcel.readInt();
      this.uiOptions = paramParcel.readInt();
      this.fullBackupContent = paramParcel.readInt();
      this.networkSecurityConfigRes = paramParcel.readInt();
      return;
      bool1 = false;
      break;
    }
  }
  
  private boolean isPackageUnavailable(PackageManager paramPackageManager)
  {
    try
    {
      paramPackageManager = paramPackageManager.getPackageInfo(this.packageName, 0);
      return paramPackageManager == null;
    }
    catch (PackageManager.NameNotFoundException paramPackageManager) {}
    return true;
  }
  
  public int canOverrideRes()
  {
    return this.overrideRes;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void disableCompatibilityMode()
  {
    this.flags |= 0x83E00;
  }
  
  public void dump(Printer paramPrinter, String paramString)
  {
    dump(paramPrinter, paramString, 3);
  }
  
  public void dump(Printer paramPrinter, String paramString, int paramInt)
  {
    super.dumpFront(paramPrinter, paramString);
    if (((paramInt & 0x1) != 0) && (this.className != null)) {
      paramPrinter.println(paramString + "className=" + this.className);
    }
    if (this.permission != null) {
      paramPrinter.println(paramString + "permission=" + this.permission);
    }
    paramPrinter.println(paramString + "processName=" + this.processName);
    if ((paramInt & 0x1) != 0) {
      paramPrinter.println(paramString + "taskAffinity=" + this.taskAffinity);
    }
    paramPrinter.println(paramString + "uid=" + this.uid + " flags=0x" + Integer.toHexString(paramInt) + " privateFlags=0x" + Integer.toHexString(this.privateFlags) + " theme=0x" + Integer.toHexString(this.theme));
    if ((paramInt & 0x1) != 0) {
      paramPrinter.println(paramString + "requiresSmallestWidthDp=" + this.requiresSmallestWidthDp + " compatibleWidthLimitDp=" + this.compatibleWidthLimitDp + " largestWidthLimitDp=" + this.largestWidthLimitDp);
    }
    paramPrinter.println(paramString + "sourceDir=" + this.sourceDir);
    if (!Objects.equals(this.sourceDir, this.publicSourceDir)) {
      paramPrinter.println(paramString + "publicSourceDir=" + this.publicSourceDir);
    }
    if (!ArrayUtils.isEmpty(this.splitSourceDirs)) {
      paramPrinter.println(paramString + "splitSourceDirs=" + Arrays.toString(this.splitSourceDirs));
    }
    if ((ArrayUtils.isEmpty(this.splitPublicSourceDirs)) || (Arrays.equals(this.splitSourceDirs, this.splitPublicSourceDirs)))
    {
      if (this.resourceDirs != null) {
        paramPrinter.println(paramString + "resourceDirs=" + Arrays.toString(this.resourceDirs));
      }
      if (((paramInt & 0x1) != 0) && (this.seinfo != null)) {
        paramPrinter.println(paramString + "seinfo=" + this.seinfo);
      }
      paramPrinter.println(paramString + "dataDir=" + this.dataDir);
      if ((paramInt & 0x1) != 0)
      {
        paramPrinter.println(paramString + "deviceProtectedDataDir=" + this.deviceProtectedDataDir);
        paramPrinter.println(paramString + "credentialProtectedDataDir=" + this.credentialProtectedDataDir);
        if (this.sharedLibraryFiles != null) {
          paramPrinter.println(paramString + "sharedLibraryFiles=" + Arrays.toString(this.sharedLibraryFiles));
        }
      }
      paramPrinter.println(paramString + "enabled=" + this.enabled + " minSdkVersion=" + this.minSdkVersion + " targetSdkVersion=" + this.targetSdkVersion + " versionCode=" + this.versionCode);
      if ((paramInt & 0x1) != 0)
      {
        if (this.manageSpaceActivityName != null) {
          paramPrinter.println(paramString + "manageSpaceActivityName=" + this.manageSpaceActivityName);
        }
        if (this.descriptionRes != 0) {
          paramPrinter.println(paramString + "description=0x" + Integer.toHexString(this.descriptionRes));
        }
        if (this.uiOptions != 0) {
          paramPrinter.println(paramString + "uiOptions=0x" + Integer.toHexString(this.uiOptions));
        }
        localStringBuilder = new StringBuilder().append(paramString).append("supportsRtl=");
        if (!hasRtlSupport()) {
          break label1074;
        }
      }
    }
    label1074:
    for (String str = "true";; str = "false")
    {
      paramPrinter.println(str);
      if (this.fullBackupContent <= 0) {
        break label1082;
      }
      paramPrinter.println(paramString + "fullBackupContent=@xml/" + this.fullBackupContent);
      if (this.networkSecurityConfigRes != 0) {
        paramPrinter.println(paramString + "networkSecurityConfigRes=0x" + Integer.toHexString(this.networkSecurityConfigRes));
      }
      super.dumpBack(paramPrinter, paramString);
      return;
      paramPrinter.println(paramString + "splitPublicSourceDirs=" + Arrays.toString(this.splitPublicSourceDirs));
      break;
    }
    label1082:
    StringBuilder localStringBuilder = new StringBuilder().append(paramString).append("fullBackupContent=");
    if (this.fullBackupContent < 0) {}
    for (str = "false";; str = "true")
    {
      paramPrinter.println(str);
      break;
    }
  }
  
  protected ApplicationInfo getApplicationInfo()
  {
    return this;
  }
  
  public String getBaseCodePath()
  {
    return this.sourceDir;
  }
  
  public String getBaseResourcePath()
  {
    return this.publicSourceDir;
  }
  
  public String getCodePath()
  {
    return this.scanSourceDir;
  }
  
  public int getOverrideDensity()
  {
    return this.overrideDensity;
  }
  
  public String getResourcePath()
  {
    return this.scanPublicSourceDir;
  }
  
  public String[] getSplitCodePaths()
  {
    return this.splitSourceDirs;
  }
  
  public String[] getSplitResourcePaths()
  {
    return this.splitSourceDirs;
  }
  
  public boolean hasCode()
  {
    boolean bool = false;
    if ((this.flags & 0x4) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean hasRtlSupport()
  {
    return (this.flags & 0x400000) == 4194304;
  }
  
  public void initForUser(int paramInt)
  {
    this.uid = UserHandle.getUid(paramInt, UserHandle.getAppId(this.uid));
    if ("android".equals(this.packageName))
    {
      this.dataDir = Environment.getDataSystemDirectory().getAbsolutePath();
      return;
    }
    String str = Environment.getDataUserDePackageDirectory(this.volumeUuid, paramInt, this.packageName).getAbsolutePath();
    this.deviceProtectedDataDir = str;
    this.deviceEncryptedDataDir = str;
    str = Environment.getDataUserCePackageDirectory(this.volumeUuid, paramInt, this.packageName).getAbsolutePath();
    this.credentialProtectedDataDir = str;
    this.credentialEncryptedDataDir = str;
    if ((this.privateFlags & 0x20) != 0)
    {
      this.dataDir = this.deviceProtectedDataDir;
      return;
    }
    this.dataDir = this.credentialProtectedDataDir;
  }
  
  public boolean isAppWhiteListed()
  {
    return this.whiteListed == 1;
  }
  
  public boolean isAutoPlayApp()
  {
    boolean bool = false;
    if ((this.privateFlags & 0x80) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isDefaultToDeviceProtectedStorage()
  {
    boolean bool = false;
    if ((this.privateFlags & 0x20) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isDirectBootAware()
  {
    boolean bool = false;
    if ((this.privateFlags & 0x40) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isEphemeralApp()
  {
    boolean bool = false;
    if ((this.privateFlags & 0x200) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isExternalAsec()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (TextUtils.isEmpty(this.volumeUuid))
    {
      bool1 = bool2;
      if ((this.flags & 0x40000) != 0) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public boolean isForwardLocked()
  {
    boolean bool = false;
    if ((this.privateFlags & 0x4) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isInternal()
  {
    boolean bool = false;
    if ((this.flags & 0x40000) == 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isPartiallyDirectBootAware()
  {
    boolean bool = false;
    if ((this.privateFlags & 0x100) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isPrivilegedApp()
  {
    boolean bool = false;
    if ((this.privateFlags & 0x8) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isRequiredForSystemUser()
  {
    boolean bool = false;
    if ((this.privateFlags & 0x400) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isSystemApp()
  {
    boolean bool = false;
    if ((this.flags & 0x1) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isUpdatedSystemApp()
  {
    boolean bool = false;
    if ((this.flags & 0x80) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public Drawable loadDefaultIcon(PackageManager paramPackageManager)
  {
    if (((this.flags & 0x40000) != 0) && (isPackageUnavailable(paramPackageManager))) {
      return Resources.getSystem().getDrawable(17303367);
    }
    return paramPackageManager.getDefaultActivityIcon();
  }
  
  public CharSequence loadDescription(PackageManager paramPackageManager)
  {
    if (this.descriptionRes != 0)
    {
      paramPackageManager = paramPackageManager.getText(this.packageName, this.descriptionRes, this);
      if (paramPackageManager != null) {
        return paramPackageManager;
      }
    }
    return null;
  }
  
  public void setAppOverrideDensity()
  {
    int i = 0;
    String str = SystemProperties.get("persist.debug.appdensity");
    if (str != null)
    {
      int j = Integer.parseInt(str);
      if (j >= 120)
      {
        i = j;
        if (j <= 480) {}
      }
      else
      {
        i = 0;
      }
    }
    setOverrideDensity(i);
  }
  
  public void setAppWhiteListed(int paramInt)
  {
    this.whiteListed = paramInt;
  }
  
  public void setBaseCodePath(String paramString)
  {
    this.sourceDir = paramString;
  }
  
  public void setBaseResourcePath(String paramString)
  {
    this.publicSourceDir = paramString;
  }
  
  public void setCodePath(String paramString)
  {
    this.scanSourceDir = paramString;
  }
  
  public void setOverrideDensity(int paramInt)
  {
    this.overrideDensity = paramInt;
  }
  
  public void setOverrideRes(int paramInt)
  {
    this.overrideRes = paramInt;
  }
  
  public void setResourcePath(String paramString)
  {
    this.scanPublicSourceDir = paramString;
  }
  
  public void setSplitCodePaths(String[] paramArrayOfString)
  {
    this.splitSourceDirs = paramArrayOfString;
  }
  
  public void setSplitResourcePaths(String[] paramArrayOfString)
  {
    this.splitPublicSourceDirs = paramArrayOfString;
  }
  
  public String toString()
  {
    return "ApplicationInfo{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.packageName + "}";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = 1;
    super.writeToParcel(paramParcel, paramInt);
    paramParcel.writeString(this.taskAffinity);
    paramParcel.writeString(this.permission);
    paramParcel.writeString(this.processName);
    paramParcel.writeString(this.className);
    paramParcel.writeInt(this.theme);
    paramParcel.writeInt(this.flags);
    paramParcel.writeInt(this.privateFlags);
    paramParcel.writeInt(this.overrideRes);
    paramParcel.writeInt(this.overrideDensity);
    paramParcel.writeInt(this.whiteListed);
    paramParcel.writeInt(this.requiresSmallestWidthDp);
    paramParcel.writeInt(this.compatibleWidthLimitDp);
    paramParcel.writeInt(this.largestWidthLimitDp);
    paramParcel.writeString(this.volumeUuid);
    paramParcel.writeString(this.scanSourceDir);
    paramParcel.writeString(this.scanPublicSourceDir);
    paramParcel.writeString(this.sourceDir);
    paramParcel.writeString(this.publicSourceDir);
    paramParcel.writeStringArray(this.splitSourceDirs);
    paramParcel.writeStringArray(this.splitPublicSourceDirs);
    paramParcel.writeString(this.nativeLibraryDir);
    paramParcel.writeString(this.secondaryNativeLibraryDir);
    paramParcel.writeString(this.nativeLibraryRootDir);
    if (this.nativeLibraryRootRequiresIsa)
    {
      paramInt = 1;
      paramParcel.writeInt(paramInt);
      paramParcel.writeString(this.primaryCpuAbi);
      paramParcel.writeString(this.secondaryCpuAbi);
      paramParcel.writeStringArray(this.resourceDirs);
      paramParcel.writeString(this.seinfo);
      paramParcel.writeStringArray(this.sharedLibraryFiles);
      paramParcel.writeString(this.dataDir);
      paramParcel.writeString(this.deviceProtectedDataDir);
      paramParcel.writeString(this.credentialProtectedDataDir);
      paramParcel.writeInt(this.uid);
      paramParcel.writeInt(this.minSdkVersion);
      paramParcel.writeInt(this.targetSdkVersion);
      paramParcel.writeInt(this.versionCode);
      if (!this.enabled) {
        break label386;
      }
    }
    label386:
    for (paramInt = i;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      paramParcel.writeInt(this.enabledSetting);
      paramParcel.writeInt(this.installLocation);
      paramParcel.writeString(this.manageSpaceActivityName);
      paramParcel.writeString(this.backupAgentName);
      paramParcel.writeInt(this.descriptionRes);
      paramParcel.writeInt(this.uiOptions);
      paramParcel.writeInt(this.fullBackupContent);
      paramParcel.writeInt(this.networkSecurityConfigRes);
      return;
      paramInt = 0;
      break;
    }
  }
  
  public static class DisplayNameComparator
    implements Comparator<ApplicationInfo>
  {
    private PackageManager mPM;
    private final Collator sCollator = Collator.getInstance();
    
    public DisplayNameComparator(PackageManager paramPackageManager)
    {
      this.mPM = paramPackageManager;
    }
    
    public final int compare(ApplicationInfo paramApplicationInfo1, ApplicationInfo paramApplicationInfo2)
    {
      CharSequence localCharSequence = this.mPM.getApplicationLabel(paramApplicationInfo1);
      Object localObject = localCharSequence;
      if (localCharSequence == null) {
        localObject = paramApplicationInfo1.packageName;
      }
      localCharSequence = this.mPM.getApplicationLabel(paramApplicationInfo2);
      paramApplicationInfo1 = localCharSequence;
      if (localCharSequence == null) {
        paramApplicationInfo1 = paramApplicationInfo2.packageName;
      }
      return this.sCollator.compare(((CharSequence)localObject).toString(), paramApplicationInfo1.toString());
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/ApplicationInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */