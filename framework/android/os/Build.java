package android.os;

import android.text.TextUtils;
import android.util.Slog;
import dalvik.system.VMRuntime;
import java.util.Objects;

public class Build
{
  public static final boolean AUTO_TEST_ONEPLUS;
  public static final String BOARD;
  public static final String BOOTLOADER;
  public static final String BRAND;
  @Deprecated
  public static final String CPU_ABI;
  @Deprecated
  public static final String CPU_ABI2;
  public static final boolean DEBUG_ONEPLUS;
  public static final String DEVICE;
  public static final String DISPLAY;
  public static final String FINGERPRINT;
  public static final String HARDWARE;
  public static final String HOST;
  public static final String ID;
  public static final boolean IS_BETA_ROM;
  public static final boolean IS_DEBUGGABLE;
  public static final boolean IS_EMULATOR;
  public static final String MANUFACTURER;
  public static final String MODEL;
  public static final boolean PERMISSIONS_REVIEW_REQUIRED;
  public static final String PRODUCT;
  @Deprecated
  public static final String RADIO;
  public static final String REGION;
  public static final String SERIAL;
  public static final String SOFT_VERSION;
  public static final String[] SUPPORTED_32_BIT_ABIS;
  public static final String[] SUPPORTED_64_BIT_ABIS;
  public static final String[] SUPPORTED_ABIS;
  private static final String TAG = "Build";
  public static final String TAGS;
  public static final long TIME;
  public static final String TYPE;
  public static final String UNKNOWN = "unknown";
  public static final String USER;
  
  static
  {
    boolean bool2 = true;
    ID = getString("ro.build.id");
    DISPLAY = getString("ro.build.display.id");
    PRODUCT = getString("ro.product.name");
    DEVICE = getString("ro.product.device");
    BOARD = getString("ro.product.board");
    REGION = getString("persist.sys.oem.region");
    SOFT_VERSION = getString("ro.build.soft.version");
    MANUFACTURER = getString("ro.product.manufacturer");
    BRAND = getString("ro.product.brand");
    MODEL = getString("ro.product.model");
    BOOTLOADER = getString("ro.bootloader");
    RADIO = getString("gsm.version.baseband");
    HARDWARE = getString("ro.hardware");
    IS_EMULATOR = getString("ro.kernel.qemu").equals("1");
    SERIAL = getString("ro.serialno");
    SUPPORTED_ABIS = getStringList("ro.product.cpu.abilist", ",");
    SUPPORTED_32_BIT_ABIS = getStringList("ro.product.cpu.abilist32", ",");
    SUPPORTED_64_BIT_ABIS = getStringList("ro.product.cpu.abilist64", ",");
    String[] arrayOfString;
    if (VMRuntime.getRuntime().is64Bit())
    {
      arrayOfString = SUPPORTED_64_BIT_ABIS;
      CPU_ABI = arrayOfString[0];
      if (arrayOfString.length <= 1) {
        break label317;
      }
      CPU_ABI2 = arrayOfString[1];
      label188:
      TYPE = getString("ro.build.type");
      TAGS = getString("ro.build.tags");
      FINGERPRINT = deriveFingerprint();
      IS_BETA_ROM = getString("ro.build.beta").equals("1");
      TIME = getLong("ro.build.date.utc") * 1000L;
      USER = getString("ro.build.user");
      HOST = getString("ro.build.host");
      if (SystemProperties.getInt("ro.debuggable", 0) != 1) {
        break label325;
      }
      bool1 = true;
      label263:
      IS_DEBUGGABLE = bool1;
      DEBUG_ONEPLUS = SystemProperties.getBoolean("persist.sys.assert.panic", false);
      if (DEBUG_ONEPLUS) {
        break label330;
      }
      bool1 = SystemProperties.getBoolean("persist.oneplus.auto_test", false);
      label289:
      AUTO_TEST_ONEPLUS = bool1;
      if (SystemProperties.getInt("ro.permission_review_required", 0) != 1) {
        break label335;
      }
    }
    label317:
    label325:
    label330:
    label335:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      PERMISSIONS_REVIEW_REQUIRED = bool1;
      return;
      arrayOfString = SUPPORTED_32_BIT_ABIS;
      break;
      CPU_ABI2 = "";
      break label188;
      bool1 = false;
      break label263;
      bool1 = true;
      break label289;
    }
  }
  
  private static String deriveFingerprint()
  {
    String str2 = SystemProperties.get("ro.build.fingerprint");
    String str1 = str2;
    if (TextUtils.isEmpty(str2)) {
      str1 = getString("ro.product.brand") + '/' + getString("ro.product.name") + '/' + getString("ro.product.device") + ':' + getString("ro.build.version.release") + '/' + getString("ro.build.id") + '/' + getString("ro.build.version.incremental") + ':' + getString("ro.build.type") + '/' + getString("ro.build.tags");
    }
    return str1;
  }
  
  public static void ensureFingerprintProperty()
  {
    if (TextUtils.isEmpty(SystemProperties.get("ro.build.fingerprint"))) {}
    try
    {
      SystemProperties.set("ro.build.fingerprint", FINGERPRINT);
      return;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      Slog.e("Build", "Failed to set fingerprint property", localIllegalArgumentException);
    }
  }
  
  private static long getLong(String paramString)
  {
    try
    {
      long l = Long.parseLong(SystemProperties.get(paramString));
      return l;
    }
    catch (NumberFormatException paramString) {}
    return -1L;
  }
  
  public static String getRadioVersion()
  {
    return SystemProperties.get("gsm.version.baseband", null);
  }
  
  private static String getString(String paramString)
  {
    return SystemProperties.get(paramString, "unknown");
  }
  
  private static String[] getStringList(String paramString1, String paramString2)
  {
    paramString1 = SystemProperties.get(paramString1);
    if (paramString1.isEmpty()) {
      return new String[0];
    }
    return paramString1.split(paramString2);
  }
  
  public static boolean isBuildConsistent()
  {
    if ("eng".equals(TYPE)) {
      return true;
    }
    String str1 = SystemProperties.get("ro.build.fingerprint");
    String str2 = SystemProperties.get("ro.vendor.build.fingerprint");
    SystemProperties.get("ro.bootimage.build.fingerprint");
    SystemProperties.get("ro.build.expect.bootloader");
    SystemProperties.get("ro.bootloader");
    SystemProperties.get("ro.build.expect.baseband");
    SystemProperties.get("gsm.version.baseband");
    if (TextUtils.isEmpty(str1))
    {
      Slog.e("Build", "Required ro.build.fingerprint is empty!");
      return false;
    }
    if ((!TextUtils.isEmpty(str2)) && (!Objects.equals(str1, str2)))
    {
      Slog.e("Build", "Mismatched fingerprints; system reported " + str1 + " but vendor reported " + str2);
      return false;
    }
    return true;
  }
  
  public static class REGION_CODES
  {
    public static final String CHINA = "CN";
    public static final String GLOBAL = "OverSeas";
    public static final boolean IS_CHINA = "CN".equals(Build.REGION);
    public static final boolean IS_GLOBAL = "OverSeas".equals(Build.REGION);
  }
  
  public static class VERSION
  {
    public static final String[] ACTIVE_CODENAMES;
    private static final String[] ALL_CODENAMES;
    public static final String BASE_OS;
    public static final String CODENAME;
    public static final String INCREMENTAL = Build.-wrap1("ro.build.version.incremental");
    public static final boolean IS_CTA_BUILD;
    public static final int PREVIEW_SDK_INT;
    public static final String RELEASE = Build.-wrap1("ro.build.version.release");
    public static final int RESOURCES_SDK_INT;
    @Deprecated
    public static final String SDK;
    public static final int SDK_INT;
    public static final String SECURITY_PATCH;
    
    static
    {
      BASE_OS = SystemProperties.get("ro.build.version.base_os", "");
      SECURITY_PATCH = SystemProperties.get("ro.build.version.security_patch", "");
      SDK = Build.-wrap1("ro.build.version.sdk");
      SDK_INT = SystemProperties.getInt("ro.build.version.sdk", 0);
      PREVIEW_SDK_INT = SystemProperties.getInt("ro.build.version.preview_sdk", 0);
      CODENAME = Build.-wrap1("ro.build.version.codename");
      ALL_CODENAMES = Build.-wrap0("ro.build.version.all_codenames", ",");
      if ("REL".equals(ALL_CODENAMES[0])) {}
      for (String[] arrayOfString = new String[0];; arrayOfString = ALL_CODENAMES)
      {
        ACTIVE_CODENAMES = arrayOfString;
        RESOURCES_SDK_INT = SDK_INT + ACTIVE_CODENAMES.length;
        IS_CTA_BUILD = Build.REGION.contains("CN");
        return;
      }
    }
  }
  
  public static class VERSION_CODES
  {
    public static final int BASE = 1;
    public static final int BASE_1_1 = 2;
    public static final int CUPCAKE = 3;
    public static final int CUR_DEVELOPMENT = 10000;
    public static final int DONUT = 4;
    public static final int ECLAIR = 5;
    public static final int ECLAIR_0_1 = 6;
    public static final int ECLAIR_MR1 = 7;
    public static final int FROYO = 8;
    public static final int GINGERBREAD = 9;
    public static final int GINGERBREAD_MR1 = 10;
    public static final int HONEYCOMB = 11;
    public static final int HONEYCOMB_MR1 = 12;
    public static final int HONEYCOMB_MR2 = 13;
    public static final int ICE_CREAM_SANDWICH = 14;
    public static final int ICE_CREAM_SANDWICH_MR1 = 15;
    public static final int JELLY_BEAN = 16;
    public static final int JELLY_BEAN_MR1 = 17;
    public static final int JELLY_BEAN_MR2 = 18;
    public static final int KITKAT = 19;
    public static final int KITKAT_WATCH = 20;
    public static final int L = 21;
    public static final int LOLLIPOP = 21;
    public static final int LOLLIPOP_MR1 = 22;
    public static final int M = 23;
    public static final int N = 24;
    public static final int N_MR1 = 25;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/Build.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */