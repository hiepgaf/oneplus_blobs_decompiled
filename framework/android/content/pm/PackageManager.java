package android.content.pm;

import android.app.PackageDeleteObserver;
import android.app.PackageInstallObserver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.storage.VolumeInfo;
import android.util.AndroidException;
import android.util.Log;
import com.android.internal.util.ArrayUtils;
import java.io.File;
import java.util.List;

public abstract class PackageManager
{
  public static final String ACTION_CLEAN_EXTERNAL_STORAGE = "android.content.pm.CLEAN_EXTERNAL_STORAGE";
  public static final String ACTION_REQUEST_PERMISSIONS = "android.content.pm.action.REQUEST_PERMISSIONS";
  public static final boolean APPLY_DEFAULT_TO_DEVICE_PROTECTED_STORAGE = true;
  public static final int COMPONENT_ENABLED_STATE_DEFAULT = 0;
  public static final int COMPONENT_ENABLED_STATE_DISABLED = 2;
  public static final int COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED = 4;
  public static final int COMPONENT_ENABLED_STATE_DISABLED_USER = 3;
  public static final int COMPONENT_ENABLED_STATE_ENABLED = 1;
  public static final int DELETE_ALL_USERS = 2;
  public static final int DELETE_DONT_KILL_APP = 8;
  public static final int DELETE_FAILED_ABORTED = -5;
  public static final int DELETE_FAILED_DEVICE_POLICY_MANAGER = -2;
  public static final int DELETE_FAILED_INTERNAL_ERROR = -1;
  public static final int DELETE_FAILED_OWNER_BLOCKED = -4;
  public static final int DELETE_FAILED_USER_RESTRICTED = -3;
  public static final int DELETE_KEEP_DATA = 1;
  public static final int DELETE_SUCCEEDED = 1;
  public static final int DELETE_SYSTEM_APP = 4;
  public static final int DONT_KILL_APP = 1;
  public static final String EXTRA_FAILURE_EXISTING_PACKAGE = "android.content.pm.extra.FAILURE_EXISTING_PACKAGE";
  public static final String EXTRA_FAILURE_EXISTING_PERMISSION = "android.content.pm.extra.FAILURE_EXISTING_PERMISSION";
  public static final String EXTRA_INTENT_FILTER_VERIFICATION_HOSTS = "android.content.pm.extra.INTENT_FILTER_VERIFICATION_HOSTS";
  public static final String EXTRA_INTENT_FILTER_VERIFICATION_ID = "android.content.pm.extra.INTENT_FILTER_VERIFICATION_ID";
  public static final String EXTRA_INTENT_FILTER_VERIFICATION_PACKAGE_NAME = "android.content.pm.extra.INTENT_FILTER_VERIFICATION_PACKAGE_NAME";
  public static final String EXTRA_INTENT_FILTER_VERIFICATION_URI_SCHEME = "android.content.pm.extra.INTENT_FILTER_VERIFICATION_URI_SCHEME";
  public static final String EXTRA_MOVE_ID = "android.content.pm.extra.MOVE_ID";
  public static final String EXTRA_REQUEST_PERMISSIONS_NAMES = "android.content.pm.extra.REQUEST_PERMISSIONS_NAMES";
  public static final String EXTRA_REQUEST_PERMISSIONS_RESULTS = "android.content.pm.extra.REQUEST_PERMISSIONS_RESULTS";
  public static final String EXTRA_VERIFICATION_ID = "android.content.pm.extra.VERIFICATION_ID";
  public static final String EXTRA_VERIFICATION_INSTALLER_PACKAGE = "android.content.pm.extra.VERIFICATION_INSTALLER_PACKAGE";
  public static final String EXTRA_VERIFICATION_INSTALLER_UID = "android.content.pm.extra.VERIFICATION_INSTALLER_UID";
  public static final String EXTRA_VERIFICATION_INSTALL_FLAGS = "android.content.pm.extra.VERIFICATION_INSTALL_FLAGS";
  public static final String EXTRA_VERIFICATION_PACKAGE_NAME = "android.content.pm.extra.VERIFICATION_PACKAGE_NAME";
  public static final String EXTRA_VERIFICATION_RESULT = "android.content.pm.extra.VERIFICATION_RESULT";
  public static final String EXTRA_VERIFICATION_URI = "android.content.pm.extra.VERIFICATION_URI";
  public static final String EXTRA_VERIFICATION_VERSION_CODE = "android.content.pm.extra.VERIFICATION_VERSION_CODE";
  public static final String FEATURE_APP_WIDGETS = "android.software.app_widgets";
  public static final String FEATURE_AUDIO_LOW_LATENCY = "android.hardware.audio.low_latency";
  public static final String FEATURE_AUDIO_OUTPUT = "android.hardware.audio.output";
  public static final String FEATURE_AUDIO_PRO = "android.hardware.audio.pro";
  public static final String FEATURE_AUTOMOTIVE = "android.hardware.type.automotive";
  public static final String FEATURE_BACKUP = "android.software.backup";
  public static final String FEATURE_BLUETOOTH = "android.hardware.bluetooth";
  public static final String FEATURE_BLUETOOTH_LE = "android.hardware.bluetooth_le";
  public static final String FEATURE_CAMERA = "android.hardware.camera";
  public static final String FEATURE_CAMERA_ANY = "android.hardware.camera.any";
  public static final String FEATURE_CAMERA_AUTOFOCUS = "android.hardware.camera.autofocus";
  public static final String FEATURE_CAMERA_CAPABILITY_MANUAL_POST_PROCESSING = "android.hardware.camera.capability.manual_post_processing";
  public static final String FEATURE_CAMERA_CAPABILITY_MANUAL_SENSOR = "android.hardware.camera.capability.manual_sensor";
  public static final String FEATURE_CAMERA_CAPABILITY_RAW = "android.hardware.camera.capability.raw";
  public static final String FEATURE_CAMERA_EXTERNAL = "android.hardware.camera.external";
  public static final String FEATURE_CAMERA_FLASH = "android.hardware.camera.flash";
  public static final String FEATURE_CAMERA_FRONT = "android.hardware.camera.front";
  public static final String FEATURE_CAMERA_LEVEL_FULL = "android.hardware.camera.level.full";
  public static final String FEATURE_CONNECTION_SERVICE = "android.software.connectionservice";
  public static final String FEATURE_CONSUMER_IR = "android.hardware.consumerir";
  public static final String FEATURE_DEVICE_ADMIN = "android.software.device_admin";
  public static final String FEATURE_ETHERNET = "android.hardware.ethernet";
  public static final String FEATURE_FAKETOUCH = "android.hardware.faketouch";
  public static final String FEATURE_FAKETOUCH_MULTITOUCH_DISTINCT = "android.hardware.faketouch.multitouch.distinct";
  public static final String FEATURE_FAKETOUCH_MULTITOUCH_JAZZHAND = "android.hardware.faketouch.multitouch.jazzhand";
  public static final String FEATURE_FILE_BASED_ENCRYPTION = "android.software.file_based_encryption";
  public static final String FEATURE_FINGERPRINT = "android.hardware.fingerprint";
  public static final String FEATURE_FREEFORM_WINDOW_MANAGEMENT = "android.software.freeform_window_management";
  public static final String FEATURE_GAMEPAD = "android.hardware.gamepad";
  public static final String FEATURE_HDMI_CEC = "android.hardware.hdmi.cec";
  public static final String FEATURE_HIFI_SENSORS = "android.hardware.sensor.hifi_sensors";
  public static final String FEATURE_HOME_SCREEN = "android.software.home_screen";
  public static final String FEATURE_INPUT_METHODS = "android.software.input_methods";
  public static final String FEATURE_LEANBACK = "android.software.leanback";
  public static final String FEATURE_LEANBACK_ONLY = "android.software.leanback_only";
  public static final String FEATURE_LIVE_TV = "android.software.live_tv";
  public static final String FEATURE_LIVE_WALLPAPER = "android.software.live_wallpaper";
  public static final String FEATURE_LOCATION = "android.hardware.location";
  public static final String FEATURE_LOCATION_GPS = "android.hardware.location.gps";
  public static final String FEATURE_LOCATION_NETWORK = "android.hardware.location.network";
  public static final String FEATURE_MANAGED_PROFILES = "android.software.managed_users";
  public static final String FEATURE_MANAGED_USERS = "android.software.managed_users";
  public static final String FEATURE_MICROPHONE = "android.hardware.microphone";
  public static final String FEATURE_MIDI = "android.software.midi";
  public static final String FEATURE_NFC = "android.hardware.nfc";
  @Deprecated
  public static final String FEATURE_NFC_HCE = "android.hardware.nfc.hce";
  public static final String FEATURE_NFC_HOST_CARD_EMULATION = "android.hardware.nfc.hce";
  public static final String FEATURE_NFC_HOST_CARD_EMULATION_NFCF = "android.hardware.nfc.hcef";
  public static final String FEATURE_OPENGLES_EXTENSION_PACK = "android.hardware.opengles.aep";
  public static final String FEATURE_PICTURE_IN_PICTURE = "android.software.picture_in_picture";
  public static final String FEATURE_PRINTING = "android.software.print";
  public static final String FEATURE_SCREEN_LANDSCAPE = "android.hardware.screen.landscape";
  public static final String FEATURE_SCREEN_PORTRAIT = "android.hardware.screen.portrait";
  public static final String FEATURE_SECURELY_REMOVES_USERS = "android.software.securely_removes_users";
  public static final String FEATURE_SENSOR_ACCELEROMETER = "android.hardware.sensor.accelerometer";
  public static final String FEATURE_SENSOR_AMBIENT_TEMPERATURE = "android.hardware.sensor.ambient_temperature";
  public static final String FEATURE_SENSOR_BAROMETER = "android.hardware.sensor.barometer";
  public static final String FEATURE_SENSOR_COMPASS = "android.hardware.sensor.compass";
  public static final String FEATURE_SENSOR_GYROSCOPE = "android.hardware.sensor.gyroscope";
  public static final String FEATURE_SENSOR_HEART_RATE = "android.hardware.sensor.heartrate";
  public static final String FEATURE_SENSOR_HEART_RATE_ECG = "android.hardware.sensor.heartrate.ecg";
  public static final String FEATURE_SENSOR_LIGHT = "android.hardware.sensor.light";
  public static final String FEATURE_SENSOR_PROXIMITY = "android.hardware.sensor.proximity";
  public static final String FEATURE_SENSOR_RELATIVE_HUMIDITY = "android.hardware.sensor.relative_humidity";
  public static final String FEATURE_SENSOR_STEP_COUNTER = "android.hardware.sensor.stepcounter";
  public static final String FEATURE_SENSOR_STEP_DETECTOR = "android.hardware.sensor.stepdetector";
  public static final String FEATURE_SIP = "android.software.sip";
  public static final String FEATURE_SIP_VOIP = "android.software.sip.voip";
  public static final String FEATURE_TELEPHONY = "android.hardware.telephony";
  public static final String FEATURE_TELEPHONY_CDMA = "android.hardware.telephony.cdma";
  public static final String FEATURE_TELEPHONY_GSM = "android.hardware.telephony.gsm";
  @Deprecated
  public static final String FEATURE_TELEVISION = "android.hardware.type.television";
  public static final String FEATURE_TOUCHSCREEN = "android.hardware.touchscreen";
  public static final String FEATURE_TOUCHSCREEN_MULTITOUCH = "android.hardware.touchscreen.multitouch";
  public static final String FEATURE_TOUCHSCREEN_MULTITOUCH_DISTINCT = "android.hardware.touchscreen.multitouch.distinct";
  public static final String FEATURE_TOUCHSCREEN_MULTITOUCH_JAZZHAND = "android.hardware.touchscreen.multitouch.jazzhand";
  public static final String FEATURE_USB_ACCESSORY = "android.hardware.usb.accessory";
  public static final String FEATURE_USB_HOST = "android.hardware.usb.host";
  public static final String FEATURE_VERIFIED_BOOT = "android.software.verified_boot";
  public static final String FEATURE_VOICE_RECOGNIZERS = "android.software.voice_recognizers";
  public static final String FEATURE_VR_MODE = "android.software.vr.mode";
  public static final String FEATURE_VR_MODE_HIGH_PERFORMANCE = "android.hardware.vr.high_performance";
  public static final String FEATURE_VULKAN_HARDWARE_LEVEL = "android.hardware.vulkan.level";
  public static final String FEATURE_VULKAN_HARDWARE_VERSION = "android.hardware.vulkan.version";
  public static final String FEATURE_WATCH = "android.hardware.type.watch";
  public static final String FEATURE_WEBVIEW = "android.software.webview";
  public static final String FEATURE_WIFI = "android.hardware.wifi";
  public static final String FEATURE_WIFI_DIRECT = "android.hardware.wifi.direct";
  public static final String FEATURE_WIFI_NAN = "android.hardware.wifi.nan";
  public static final int FLAG_PERMISSION_GRANTED_BY_DEFAULT = 32;
  public static final int FLAG_PERMISSION_POLICY_FIXED = 4;
  public static final int FLAG_PERMISSION_REVIEW_REQUIRED = 64;
  public static final int FLAG_PERMISSION_REVOKE_ON_UPGRADE = 8;
  public static final int FLAG_PERMISSION_SYSTEM_FIXED = 16;
  public static final int FLAG_PERMISSION_USER_FIXED = 2;
  public static final int FLAG_PERMISSION_USER_SET = 1;
  public static final int GET_ACTIVITIES = 1;
  public static final int GET_CONFIGURATIONS = 16384;
  @Deprecated
  public static final int GET_DISABLED_COMPONENTS = 512;
  @Deprecated
  public static final int GET_DISABLED_UNTIL_USED_COMPONENTS = 32768;
  public static final int GET_GIDS = 256;
  public static final int GET_INSTRUMENTATION = 16;
  public static final int GET_INTENT_FILTERS = 32;
  public static final int GET_META_DATA = 128;
  public static final int GET_PERMISSIONS = 4096;
  public static final int GET_PROVIDERS = 8;
  public static final int GET_RECEIVERS = 2;
  public static final int GET_RESOLVED_FILTER = 64;
  public static final int GET_SERVICES = 4;
  public static final int GET_SHARED_LIBRARY_FILES = 1024;
  public static final int GET_SIGNATURES = 64;
  @Deprecated
  public static final int GET_UNINSTALLED_PACKAGES = 8192;
  public static final int GET_URI_PERMISSION_PATTERNS = 2048;
  public static final int INSTALL_ALLOW_DOWNGRADE = 128;
  public static final int INSTALL_ALLOW_TEST = 4;
  public static final int INSTALL_ALL_USERS = 64;
  public static final int INSTALL_DONT_KILL_APP = 4096;
  public static final int INSTALL_EPHEMERAL = 2048;
  public static final int INSTALL_EXTERNAL = 8;
  public static final int INSTALL_FAILED_ABORTED = -115;
  public static final int INSTALL_FAILED_ALREADY_EXISTS = -1;
  public static final int INSTALL_FAILED_CONFLICTING_PROVIDER = -13;
  public static final int INSTALL_FAILED_CONTAINER_ERROR = -18;
  public static final int INSTALL_FAILED_CPU_ABI_INCOMPATIBLE = -16;
  public static final int INSTALL_FAILED_DEXOPT = -11;
  public static final int INSTALL_FAILED_DUPLICATE_PACKAGE = -5;
  public static final int INSTALL_FAILED_DUPLICATE_PERMISSION = -112;
  public static final int INSTALL_FAILED_EPHEMERAL_INVALID = -116;
  public static final int INSTALL_FAILED_INSUFFICIENT_STORAGE = -4;
  public static final int INSTALL_FAILED_INTERNAL_ERROR = -110;
  public static final int INSTALL_FAILED_INVALID_APK = -2;
  public static final int INSTALL_FAILED_INVALID_INSTALL_LOCATION = -19;
  public static final int INSTALL_FAILED_INVALID_URI = -3;
  public static final int INSTALL_FAILED_MEDIA_UNAVAILABLE = -20;
  public static final int INSTALL_FAILED_MISSING_FEATURE = -17;
  public static final int INSTALL_FAILED_MISSING_SHARED_LIBRARY = -9;
  public static final int INSTALL_FAILED_NEWER_SDK = -14;
  public static final int INSTALL_FAILED_NO_MATCHING_ABIS = -113;
  public static final int INSTALL_FAILED_NO_SHARED_USER = -6;
  public static final int INSTALL_FAILED_OLDER_SDK = -12;
  public static final int INSTALL_FAILED_PACKAGE_CHANGED = -23;
  public static final int INSTALL_FAILED_PERMISSION_MODEL_DOWNGRADE = -26;
  public static final int INSTALL_FAILED_REPLACE_COULDNT_DELETE = -10;
  public static final int INSTALL_FAILED_SHARED_USER_INCOMPATIBLE = -8;
  public static final int INSTALL_FAILED_TEST_ONLY = -15;
  public static final int INSTALL_FAILED_UID_CHANGED = -24;
  public static final int INSTALL_FAILED_UPDATE_INCOMPATIBLE = -7;
  public static final int INSTALL_FAILED_USER_RESTRICTED = -111;
  public static final int INSTALL_FAILED_VERIFICATION_FAILURE = -22;
  public static final int INSTALL_FAILED_VERIFICATION_TIMEOUT = -21;
  public static final int INSTALL_FAILED_VERSION_DOWNGRADE = -25;
  public static final int INSTALL_FORCE_PERMISSION_PROMPT = 1024;
  public static final int INSTALL_FORCE_SDK = 8192;
  public static final int INSTALL_FORCE_VOLUME_UUID = 512;
  public static final int INSTALL_FORWARD_LOCK = 1;
  public static final int INSTALL_FROM_ADB = 32;
  public static final int INSTALL_GRANT_RUNTIME_PERMISSIONS = 256;
  public static final int INSTALL_INTERNAL = 16;
  public static final int INSTALL_PARSE_FAILED_BAD_MANIFEST = -101;
  public static final int INSTALL_PARSE_FAILED_BAD_PACKAGE_NAME = -106;
  public static final int INSTALL_PARSE_FAILED_BAD_SHARED_USER_ID = -107;
  public static final int INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING = -105;
  public static final int INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES = -104;
  public static final int INSTALL_PARSE_FAILED_MANIFEST_EMPTY = -109;
  public static final int INSTALL_PARSE_FAILED_MANIFEST_MALFORMED = -108;
  public static final int INSTALL_PARSE_FAILED_NOT_APK = -100;
  public static final int INSTALL_PARSE_FAILED_NO_CERTIFICATES = -103;
  public static final int INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION = -102;
  public static final int INSTALL_REPLACE_EXISTING = 2;
  public static final int INSTALL_SUCCEEDED = 1;
  public static final int INTENT_FILTER_DOMAIN_VERIFICATION_STATUS_ALWAYS = 2;
  public static final int INTENT_FILTER_DOMAIN_VERIFICATION_STATUS_ALWAYS_ASK = 4;
  public static final int INTENT_FILTER_DOMAIN_VERIFICATION_STATUS_ASK = 1;
  public static final int INTENT_FILTER_DOMAIN_VERIFICATION_STATUS_NEVER = 3;
  public static final int INTENT_FILTER_DOMAIN_VERIFICATION_STATUS_UNDEFINED = 0;
  public static final int INTENT_FILTER_VERIFICATION_FAILURE = -1;
  public static final int INTENT_FILTER_VERIFICATION_SUCCESS = 1;
  public static final int MASK_PERMISSION_FLAGS = 255;
  public static final int MATCH_ALL = 131072;
  public static final int MATCH_DEBUG_TRIAGED_MISSING = 268435456;
  public static final int MATCH_DEFAULT_ONLY = 65536;
  public static final int MATCH_DIRECT_BOOT_AWARE = 524288;
  public static final int MATCH_DIRECT_BOOT_UNAWARE = 262144;
  public static final int MATCH_DISABLED_COMPONENTS = 512;
  public static final int MATCH_DISABLED_UNTIL_USED_COMPONENTS = 32768;
  @Deprecated
  public static final int MATCH_ENCRYPTION_AWARE = 524288;
  @Deprecated
  public static final int MATCH_ENCRYPTION_AWARE_AND_UNAWARE = 786432;
  @Deprecated
  public static final int MATCH_ENCRYPTION_UNAWARE = 262144;
  public static final int MATCH_FACTORY_ONLY = 2097152;
  public static final int MATCH_SYSTEM_ONLY = 1048576;
  public static final int MATCH_UNINSTALLED_PACKAGES = 8192;
  public static final long MAXIMUM_VERIFICATION_TIMEOUT = 3600000L;
  @Deprecated
  public static final int MOVE_EXTERNAL_MEDIA = 2;
  public static final int MOVE_FAILED_DEVICE_ADMIN = -8;
  public static final int MOVE_FAILED_DOESNT_EXIST = -2;
  public static final int MOVE_FAILED_FORWARD_LOCKED = -4;
  public static final int MOVE_FAILED_INSUFFICIENT_STORAGE = -1;
  public static final int MOVE_FAILED_INTERNAL_ERROR = -6;
  public static final int MOVE_FAILED_INVALID_LOCATION = -5;
  public static final int MOVE_FAILED_OPERATION_PENDING = -7;
  public static final int MOVE_FAILED_SYSTEM_PACKAGE = -3;
  @Deprecated
  public static final int MOVE_INTERNAL = 1;
  public static final int MOVE_SUCCEEDED = -100;
  public static final int NOTIFY_PACKAGE_USE_ACTIVITY = 0;
  public static final int NOTIFY_PACKAGE_USE_BACKUP = 5;
  public static final int NOTIFY_PACKAGE_USE_BROADCAST_RECEIVER = 3;
  public static final int NOTIFY_PACKAGE_USE_CONTENT_PROVIDER = 4;
  public static final int NOTIFY_PACKAGE_USE_CROSS_PACKAGE = 6;
  public static final int NOTIFY_PACKAGE_USE_FOREGROUND_SERVICE = 2;
  public static final int NOTIFY_PACKAGE_USE_INSTRUMENTATION = 7;
  public static final int NOTIFY_PACKAGE_USE_REASONS_COUNT = 8;
  public static final int NOTIFY_PACKAGE_USE_SERVICE = 1;
  public static final int NO_NATIVE_LIBRARIES = -114;
  public static final int ONLY_IF_NO_MATCH_FOUND = 4;
  public static final int PERMISSION_DENIED = -1;
  public static final int PERMISSION_GRANTED = 0;
  public static final int SIGNATURE_FIRST_NOT_SIGNED = -1;
  public static final int SIGNATURE_MATCH = 0;
  public static final int SIGNATURE_NEITHER_SIGNED = 1;
  public static final int SIGNATURE_NO_MATCH = -3;
  public static final int SIGNATURE_SECOND_NOT_SIGNED = -2;
  public static final int SIGNATURE_UNKNOWN_PACKAGE = -4;
  public static final int SKIP_CURRENT_PROFILE = 2;
  public static final String SYSTEM_SHARED_LIBRARY_SERVICES = "android.ext.services";
  public static final String SYSTEM_SHARED_LIBRARY_SHARED = "android.ext.shared";
  private static final String TAG = "PackageManager";
  public static final int VERIFICATION_ALLOW = 1;
  public static final int VERIFICATION_ALLOW_WITHOUT_SUFFICIENT = 2;
  public static final int VERIFICATION_REJECT = -1;
  
  public static int deleteStatusToPublicStatus(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
    default: 
      return 1;
    case 1: 
      return 0;
    case -1: 
      return 1;
    case -2: 
      return 2;
    case -3: 
      return 2;
    case -4: 
      return 2;
    }
    return 3;
  }
  
  public static String deleteStatusToString(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
    default: 
      return Integer.toString(paramInt);
    case 1: 
      return "DELETE_SUCCEEDED";
    case -1: 
      return "DELETE_FAILED_INTERNAL_ERROR";
    case -2: 
      return "DELETE_FAILED_DEVICE_POLICY_MANAGER";
    case -3: 
      return "DELETE_FAILED_USER_RESTRICTED";
    case -4: 
      return "DELETE_FAILED_OWNER_BLOCKED";
    }
    return "DELETE_FAILED_ABORTED";
  }
  
  public static String deleteStatusToString(int paramInt, String paramString)
  {
    String str = deleteStatusToString(paramInt);
    if (paramString != null) {
      return str + ": " + paramString;
    }
    return str;
  }
  
  public static int installStatusToPublicStatus(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return 1;
    case 1: 
      return 0;
    case -1: 
      return 5;
    case -2: 
      return 4;
    case -3: 
      return 4;
    case -4: 
      return 6;
    case -5: 
      return 5;
    case -6: 
      return 5;
    case -7: 
      return 5;
    case -8: 
      return 5;
    case -9: 
      return 7;
    case -10: 
      return 5;
    case -11: 
      return 4;
    case -12: 
      return 7;
    case -13: 
      return 5;
    case -14: 
      return 7;
    case -15: 
      return 4;
    case -16: 
      return 7;
    case -17: 
      return 7;
    case -18: 
      return 6;
    case -19: 
      return 6;
    case -20: 
      return 6;
    case -21: 
      return 3;
    case -22: 
      return 3;
    case -23: 
      return 4;
    case -24: 
      return 4;
    case -25: 
      return 4;
    case -26: 
      return 4;
    case -100: 
      return 4;
    case -101: 
      return 4;
    case -102: 
      return 4;
    case -103: 
      return 4;
    case -104: 
      return 4;
    case -105: 
      return 4;
    case -106: 
      return 4;
    case -107: 
      return 4;
    case -108: 
      return 4;
    case -109: 
      return 4;
    case -110: 
      return 1;
    case -111: 
      return 7;
    case -112: 
      return 5;
    case -113: 
      return 7;
    }
    return 3;
  }
  
  public static String installStatusToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return Integer.toString(paramInt);
    case 1: 
      return "INSTALL_SUCCEEDED";
    case -1: 
      return "INSTALL_FAILED_ALREADY_EXISTS";
    case -2: 
      return "INSTALL_FAILED_INVALID_APK";
    case -3: 
      return "INSTALL_FAILED_INVALID_URI";
    case -4: 
      return "INSTALL_FAILED_INSUFFICIENT_STORAGE";
    case -5: 
      return "INSTALL_FAILED_DUPLICATE_PACKAGE";
    case -6: 
      return "INSTALL_FAILED_NO_SHARED_USER";
    case -7: 
      return "INSTALL_FAILED_UPDATE_INCOMPATIBLE";
    case -8: 
      return "INSTALL_FAILED_SHARED_USER_INCOMPATIBLE";
    case -9: 
      return "INSTALL_FAILED_MISSING_SHARED_LIBRARY";
    case -10: 
      return "INSTALL_FAILED_REPLACE_COULDNT_DELETE";
    case -11: 
      return "INSTALL_FAILED_DEXOPT";
    case -12: 
      return "INSTALL_FAILED_OLDER_SDK";
    case -13: 
      return "INSTALL_FAILED_CONFLICTING_PROVIDER";
    case -14: 
      return "INSTALL_FAILED_NEWER_SDK";
    case -15: 
      return "INSTALL_FAILED_TEST_ONLY";
    case -16: 
      return "INSTALL_FAILED_CPU_ABI_INCOMPATIBLE";
    case -17: 
      return "INSTALL_FAILED_MISSING_FEATURE";
    case -18: 
      return "INSTALL_FAILED_CONTAINER_ERROR";
    case -19: 
      return "INSTALL_FAILED_INVALID_INSTALL_LOCATION";
    case -20: 
      return "INSTALL_FAILED_MEDIA_UNAVAILABLE";
    case -21: 
      return "INSTALL_FAILED_VERIFICATION_TIMEOUT";
    case -22: 
      return "INSTALL_FAILED_VERIFICATION_FAILURE";
    case -23: 
      return "INSTALL_FAILED_PACKAGE_CHANGED";
    case -24: 
      return "INSTALL_FAILED_UID_CHANGED";
    case -25: 
      return "INSTALL_FAILED_VERSION_DOWNGRADE";
    case -100: 
      return "INSTALL_PARSE_FAILED_NOT_APK";
    case -101: 
      return "INSTALL_PARSE_FAILED_BAD_MANIFEST";
    case -102: 
      return "INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION";
    case -103: 
      return "INSTALL_PARSE_FAILED_NO_CERTIFICATES";
    case -104: 
      return "INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES";
    case -105: 
      return "INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING";
    case -106: 
      return "INSTALL_PARSE_FAILED_BAD_PACKAGE_NAME";
    case -107: 
      return "INSTALL_PARSE_FAILED_BAD_SHARED_USER_ID";
    case -108: 
      return "INSTALL_PARSE_FAILED_MANIFEST_MALFORMED";
    case -109: 
      return "INSTALL_PARSE_FAILED_MANIFEST_EMPTY";
    case -110: 
      return "INSTALL_FAILED_INTERNAL_ERROR";
    case -111: 
      return "INSTALL_FAILED_USER_RESTRICTED";
    case -112: 
      return "INSTALL_FAILED_DUPLICATE_PERMISSION";
    case -113: 
      return "INSTALL_FAILED_NO_MATCHING_ABIS";
    }
    return "INSTALL_FAILED_ABORTED";
  }
  
  public static String installStatusToString(int paramInt, String paramString)
  {
    String str = installStatusToString(paramInt);
    if (paramString != null) {
      return str + ": " + paramString;
    }
    return str;
  }
  
  public static boolean isMoveStatusFinished(int paramInt)
  {
    return (paramInt < 0) || (paramInt > 100);
  }
  
  public static String permissionFlagToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return Integer.toString(paramInt);
    case 32: 
      return "GRANTED_BY_DEFAULT";
    case 4: 
      return "POLICY_FIXED";
    case 16: 
      return "SYSTEM_FIXED";
    case 1: 
      return "USER_SET";
    case 8: 
      return "REVOKE_ON_UPGRADE";
    case 2: 
      return "USER_FIXED";
    }
    return "REVIEW_REQUIRED";
  }
  
  public abstract void addCrossProfileIntentFilter(IntentFilter paramIntentFilter, int paramInt1, int paramInt2, int paramInt3);
  
  public abstract void addOnPermissionsChangeListener(OnPermissionsChangedListener paramOnPermissionsChangedListener);
  
  @Deprecated
  public abstract void addPackageToPreferred(String paramString);
  
  public abstract boolean addPermission(PermissionInfo paramPermissionInfo);
  
  public abstract boolean addPermissionAsync(PermissionInfo paramPermissionInfo);
  
  @Deprecated
  public abstract void addPreferredActivity(IntentFilter paramIntentFilter, int paramInt, ComponentName[] paramArrayOfComponentName, ComponentName paramComponentName);
  
  public void addPreferredActivityAsUser(IntentFilter paramIntentFilter, int paramInt1, ComponentName[] paramArrayOfComponentName, ComponentName paramComponentName, int paramInt2)
  {
    throw new RuntimeException("Not implemented. Must override in a subclass.");
  }
  
  public Intent buildRequestPermissionsIntent(String[] paramArrayOfString)
  {
    if (ArrayUtils.isEmpty(paramArrayOfString)) {
      throw new IllegalArgumentException("permission cannot be null or empty");
    }
    Intent localIntent = new Intent("android.content.pm.action.REQUEST_PERMISSIONS");
    localIntent.putExtra("android.content.pm.extra.REQUEST_PERMISSIONS_NAMES", paramArrayOfString);
    localIntent.setPackage(getPermissionControllerPackageName());
    return localIntent;
  }
  
  public abstract String[] canonicalToCurrentPackageNames(String[] paramArrayOfString);
  
  public abstract int checkPermission(String paramString1, String paramString2);
  
  public abstract int checkPermissionByUserId(String paramString1, String paramString2, int paramInt);
  
  public abstract int checkSignatures(int paramInt1, int paramInt2);
  
  public abstract int checkSignatures(String paramString1, String paramString2);
  
  public abstract void clearApplicationUserData(String paramString, IPackageDataObserver paramIPackageDataObserver);
  
  public abstract void clearCrossProfileIntentFilters(int paramInt);
  
  public abstract void clearPackagePreferredActivities(String paramString);
  
  public abstract String[] currentToCanonicalPackageNames(String[] paramArrayOfString);
  
  public abstract void deleteApplicationCacheFiles(String paramString, IPackageDataObserver paramIPackageDataObserver);
  
  public abstract void deleteApplicationCacheFilesAsUser(String paramString, int paramInt, IPackageDataObserver paramIPackageDataObserver);
  
  public abstract void deletePackage(String paramString, IPackageDeleteObserver paramIPackageDeleteObserver, int paramInt);
  
  public abstract void deletePackageAsUser(String paramString, IPackageDeleteObserver paramIPackageDeleteObserver, int paramInt1, int paramInt2);
  
  public abstract void extendVerificationTimeout(int paramInt1, int paramInt2, long paramLong);
  
  public abstract void flushPackageRestrictionsAsUser(int paramInt);
  
  public void freeStorage(long paramLong, IntentSender paramIntentSender)
  {
    freeStorage(null, paramLong, paramIntentSender);
  }
  
  public abstract void freeStorage(String paramString, long paramLong, IntentSender paramIntentSender);
  
  public void freeStorageAndNotify(long paramLong, IPackageDataObserver paramIPackageDataObserver)
  {
    freeStorageAndNotify(null, paramLong, paramIPackageDataObserver);
  }
  
  public abstract void freeStorageAndNotify(String paramString, long paramLong, IPackageDataObserver paramIPackageDataObserver);
  
  public abstract Drawable getActivityBanner(ComponentName paramComponentName)
    throws PackageManager.NameNotFoundException;
  
  public abstract Drawable getActivityBanner(Intent paramIntent)
    throws PackageManager.NameNotFoundException;
  
  public abstract Drawable getActivityIcon(ComponentName paramComponentName)
    throws PackageManager.NameNotFoundException;
  
  public abstract Drawable getActivityIcon(Intent paramIntent)
    throws PackageManager.NameNotFoundException;
  
  public abstract ActivityInfo getActivityInfo(ComponentName paramComponentName, int paramInt)
    throws PackageManager.NameNotFoundException;
  
  public abstract Drawable getActivityLogo(ComponentName paramComponentName)
    throws PackageManager.NameNotFoundException;
  
  public abstract Drawable getActivityLogo(Intent paramIntent)
    throws PackageManager.NameNotFoundException;
  
  public abstract List<IntentFilter> getAllIntentFilters(String paramString);
  
  public abstract List<PermissionGroupInfo> getAllPermissionGroups(int paramInt);
  
  public abstract Drawable getApplicationBanner(ApplicationInfo paramApplicationInfo);
  
  public abstract Drawable getApplicationBanner(String paramString)
    throws PackageManager.NameNotFoundException;
  
  public abstract int getApplicationEnabledSetting(String paramString);
  
  public abstract boolean getApplicationHiddenSettingAsUser(String paramString, UserHandle paramUserHandle);
  
  public abstract Drawable getApplicationIcon(ApplicationInfo paramApplicationInfo);
  
  public abstract Drawable getApplicationIcon(String paramString)
    throws PackageManager.NameNotFoundException;
  
  public abstract ApplicationInfo getApplicationInfo(String paramString, int paramInt)
    throws PackageManager.NameNotFoundException;
  
  public abstract ApplicationInfo getApplicationInfoAsUser(String paramString, int paramInt1, int paramInt2)
    throws PackageManager.NameNotFoundException;
  
  public abstract ApplicationInfo getApplicationInfoByUserId(String paramString, int paramInt1, int paramInt2)
    throws PackageManager.NameNotFoundException;
  
  public abstract CharSequence getApplicationLabel(ApplicationInfo paramApplicationInfo);
  
  public abstract Drawable getApplicationLogo(ApplicationInfo paramApplicationInfo);
  
  public abstract Drawable getApplicationLogo(String paramString)
    throws PackageManager.NameNotFoundException;
  
  public abstract int getComponentEnabledSetting(ComponentName paramComponentName);
  
  public abstract Drawable getDefaultActivityIcon();
  
  public abstract String getDefaultBrowserPackageNameAsUser(int paramInt);
  
  public abstract Drawable getDrawable(String paramString, int paramInt, ApplicationInfo paramApplicationInfo);
  
  public abstract Drawable getEphemeralApplicationIcon(String paramString);
  
  public abstract List<EphemeralApplicationInfo> getEphemeralApplications();
  
  public abstract byte[] getEphemeralCookie();
  
  public abstract int getEphemeralCookieMaxSizeBytes();
  
  public abstract ComponentName getHomeActivities(List<ResolveInfo> paramList);
  
  public abstract List<ApplicationInfo> getInstalledApplications(int paramInt);
  
  public abstract List<PackageInfo> getInstalledPackages(int paramInt);
  
  public abstract List<PackageInfo> getInstalledPackagesAsUser(int paramInt1, int paramInt2);
  
  public abstract String getInstallerPackageName(String paramString);
  
  public abstract InstrumentationInfo getInstrumentationInfo(ComponentName paramComponentName, int paramInt)
    throws PackageManager.NameNotFoundException;
  
  public abstract List<IntentFilterVerificationInfo> getIntentFilterVerifications(String paramString);
  
  public abstract int getIntentVerificationStatusAsUser(String paramString, int paramInt);
  
  public abstract KeySet getKeySetByAlias(String paramString1, String paramString2);
  
  public abstract Intent getLaunchIntentForPackage(String paramString);
  
  public abstract Intent getLeanbackLaunchIntentForPackage(String paramString);
  
  public abstract Drawable getManagedUserBadgedDrawable(Drawable paramDrawable, Rect paramRect, int paramInt);
  
  public abstract int getMoveStatus(int paramInt);
  
  public abstract String getNameForUid(int paramInt);
  
  public PackageInfo getPackageArchiveInfo(String paramString, int paramInt)
  {
    PackageParser localPackageParser = new PackageParser();
    paramString = new File(paramString);
    if ((paramInt & 0xC0000) != 0) {}
    for (;;)
    {
      try
      {
        paramString = localPackageParser.parseMonolithicPackage(paramString, 0);
        if ((paramInt & 0x40) != 0) {
          PackageParser.collectCertificates(paramString, 0);
        }
        paramString = PackageParser.generatePackageInfo(paramString, null, paramInt, 0L, 0L, null, new PackageUserState());
        return paramString;
      }
      catch (PackageParser.PackageParserException paramString) {}
      paramInt |= 0xC0000;
    }
    return null;
  }
  
  public abstract List<VolumeInfo> getPackageCandidateVolumes(ApplicationInfo paramApplicationInfo);
  
  public abstract VolumeInfo getPackageCurrentVolume(ApplicationInfo paramApplicationInfo);
  
  public abstract int[] getPackageGids(String paramString)
    throws PackageManager.NameNotFoundException;
  
  public abstract int[] getPackageGids(String paramString, int paramInt)
    throws PackageManager.NameNotFoundException;
  
  public abstract PackageInfo getPackageInfo(String paramString, int paramInt)
    throws PackageManager.NameNotFoundException;
  
  public abstract PackageInfo getPackageInfoAsUser(String paramString, int paramInt1, int paramInt2)
    throws PackageManager.NameNotFoundException;
  
  public abstract PackageInstaller getPackageInstaller();
  
  public void getPackageSizeInfo(String paramString, IPackageStatsObserver paramIPackageStatsObserver)
  {
    getPackageSizeInfoAsUser(paramString, UserHandle.myUserId(), paramIPackageStatsObserver);
  }
  
  public abstract void getPackageSizeInfoAsUser(String paramString, int paramInt, IPackageStatsObserver paramIPackageStatsObserver);
  
  public abstract int getPackageUid(String paramString, int paramInt)
    throws PackageManager.NameNotFoundException;
  
  public abstract int getPackageUidAsUser(String paramString, int paramInt)
    throws PackageManager.NameNotFoundException;
  
  public abstract int getPackageUidAsUser(String paramString, int paramInt1, int paramInt2)
    throws PackageManager.NameNotFoundException;
  
  public abstract String[] getPackagesForUid(int paramInt);
  
  public abstract List<PackageInfo> getPackagesHoldingPermissions(String[] paramArrayOfString, int paramInt);
  
  public abstract String getPermissionControllerPackageName();
  
  public abstract int getPermissionFlags(String paramString1, String paramString2, UserHandle paramUserHandle);
  
  public abstract PermissionGroupInfo getPermissionGroupInfo(String paramString, int paramInt)
    throws PackageManager.NameNotFoundException;
  
  public abstract PermissionInfo getPermissionInfo(String paramString, int paramInt)
    throws PackageManager.NameNotFoundException;
  
  public abstract int getPreferredActivities(List<IntentFilter> paramList, List<ComponentName> paramList1, String paramString);
  
  public abstract List<PackageInfo> getPreferredPackages(int paramInt);
  
  public abstract List<VolumeInfo> getPrimaryStorageCandidateVolumes();
  
  public abstract VolumeInfo getPrimaryStorageCurrentVolume();
  
  public abstract ProviderInfo getProviderInfo(ComponentName paramComponentName, int paramInt)
    throws PackageManager.NameNotFoundException;
  
  public abstract ActivityInfo getReceiverInfo(ComponentName paramComponentName, int paramInt)
    throws PackageManager.NameNotFoundException;
  
  public abstract Resources getResourcesForActivity(ComponentName paramComponentName)
    throws PackageManager.NameNotFoundException;
  
  public abstract Resources getResourcesForApplication(ApplicationInfo paramApplicationInfo)
    throws PackageManager.NameNotFoundException;
  
  public abstract Resources getResourcesForApplication(String paramString)
    throws PackageManager.NameNotFoundException;
  
  public abstract Resources getResourcesForApplicationAsUser(String paramString, int paramInt)
    throws PackageManager.NameNotFoundException;
  
  public abstract ServiceInfo getServiceInfo(ComponentName paramComponentName, int paramInt)
    throws PackageManager.NameNotFoundException;
  
  public abstract String getServicesSystemSharedLibraryPackageName();
  
  public abstract String getSharedSystemSharedLibraryPackageName();
  
  public abstract KeySet getSigningKeySet(String paramString);
  
  public abstract FeatureInfo[] getSystemAvailableFeatures();
  
  public abstract String[] getSystemSharedLibraryNames();
  
  public abstract CharSequence getText(String paramString, int paramInt, ApplicationInfo paramApplicationInfo);
  
  public abstract int getUidForSharedUser(String paramString)
    throws PackageManager.NameNotFoundException;
  
  public abstract Drawable getUserBadgeForDensity(UserHandle paramUserHandle, int paramInt);
  
  public abstract Drawable getUserBadgeForDensityNoBackground(UserHandle paramUserHandle, int paramInt);
  
  public abstract Drawable getUserBadgedDrawableForDensity(Drawable paramDrawable, UserHandle paramUserHandle, Rect paramRect, int paramInt);
  
  public abstract Drawable getUserBadgedIcon(Drawable paramDrawable, UserHandle paramUserHandle);
  
  public abstract CharSequence getUserBadgedLabel(CharSequence paramCharSequence, UserHandle paramUserHandle);
  
  public abstract VerifierDeviceIdentity getVerifierDeviceIdentity();
  
  public abstract XmlResourceParser getXml(String paramString, int paramInt, ApplicationInfo paramApplicationInfo);
  
  public abstract void grantRuntimePermission(String paramString1, String paramString2, UserHandle paramUserHandle);
  
  public abstract boolean hasSystemFeature(String paramString);
  
  public abstract boolean hasSystemFeature(String paramString, int paramInt);
  
  public abstract int installExistingPackage(String paramString)
    throws PackageManager.NameNotFoundException;
  
  public abstract int installExistingPackageAsUser(String paramString, int paramInt)
    throws PackageManager.NameNotFoundException;
  
  @Deprecated
  public abstract void installPackage(Uri paramUri, PackageInstallObserver paramPackageInstallObserver, int paramInt, String paramString);
  
  @Deprecated
  public abstract void installPackage(Uri paramUri, IPackageInstallObserver paramIPackageInstallObserver, int paramInt, String paramString);
  
  public abstract boolean isEphemeralApplication();
  
  public abstract boolean isPackageAvailable(String paramString);
  
  public abstract boolean isPackageSuspendedForUser(String paramString, int paramInt);
  
  public abstract boolean isPermissionRevokedByPolicy(String paramString1, String paramString2);
  
  public abstract boolean isSafeMode();
  
  public abstract boolean isSignedBy(String paramString, KeySet paramKeySet);
  
  public abstract boolean isSignedByExactly(String paramString, KeySet paramKeySet);
  
  public abstract boolean isUpgrade();
  
  public abstract Drawable loadItemIcon(PackageItemInfo paramPackageItemInfo, ApplicationInfo paramApplicationInfo);
  
  public abstract Drawable loadUnbadgedItemIcon(PackageItemInfo paramPackageItemInfo, ApplicationInfo paramApplicationInfo);
  
  public abstract int movePackage(String paramString, VolumeInfo paramVolumeInfo);
  
  public abstract int movePrimaryStorage(VolumeInfo paramVolumeInfo);
  
  public abstract List<ResolveInfo> queryBroadcastReceivers(Intent paramIntent, int paramInt);
  
  @Deprecated
  public List<ResolveInfo> queryBroadcastReceivers(Intent paramIntent, int paramInt1, int paramInt2)
  {
    Log.w("PackageManager", "STAHP USING HIDDEN APIS KTHX");
    return queryBroadcastReceiversAsUser(paramIntent, paramInt1, paramInt2);
  }
  
  public abstract List<ResolveInfo> queryBroadcastReceiversAsUser(Intent paramIntent, int paramInt1, int paramInt2);
  
  public List<ResolveInfo> queryBroadcastReceiversAsUser(Intent paramIntent, int paramInt, UserHandle paramUserHandle)
  {
    return queryBroadcastReceiversAsUser(paramIntent, paramInt, paramUserHandle.getIdentifier());
  }
  
  public abstract List<ProviderInfo> queryContentProviders(String paramString, int paramInt1, int paramInt2);
  
  public abstract List<InstrumentationInfo> queryInstrumentation(String paramString, int paramInt);
  
  public abstract List<ResolveInfo> queryIntentActivities(Intent paramIntent, int paramInt);
  
  public abstract List<ResolveInfo> queryIntentActivitiesAsUser(Intent paramIntent, int paramInt1, int paramInt2);
  
  public abstract List<ResolveInfo> queryIntentActivityOptions(ComponentName paramComponentName, Intent[] paramArrayOfIntent, Intent paramIntent, int paramInt);
  
  public abstract List<ResolveInfo> queryIntentContentProviders(Intent paramIntent, int paramInt);
  
  public abstract List<ResolveInfo> queryIntentContentProvidersAsUser(Intent paramIntent, int paramInt1, int paramInt2);
  
  public abstract List<ResolveInfo> queryIntentServices(Intent paramIntent, int paramInt);
  
  public abstract List<ResolveInfo> queryIntentServicesAsUser(Intent paramIntent, int paramInt1, int paramInt2);
  
  public abstract List<PermissionInfo> queryPermissionsByGroup(String paramString, int paramInt)
    throws PackageManager.NameNotFoundException;
  
  public abstract void registerMoveCallback(MoveCallback paramMoveCallback, Handler paramHandler);
  
  public abstract void removeOnPermissionsChangeListener(OnPermissionsChangedListener paramOnPermissionsChangedListener);
  
  @Deprecated
  public abstract void removePackageFromPreferred(String paramString);
  
  public abstract void removePermission(String paramString);
  
  @Deprecated
  public abstract void replacePreferredActivity(IntentFilter paramIntentFilter, int paramInt, ComponentName[] paramArrayOfComponentName, ComponentName paramComponentName);
  
  @Deprecated
  public void replacePreferredActivityAsUser(IntentFilter paramIntentFilter, int paramInt1, ComponentName[] paramArrayOfComponentName, ComponentName paramComponentName, int paramInt2)
  {
    throw new RuntimeException("Not implemented. Must override in a subclass.");
  }
  
  public abstract void resetApplicationPermissions();
  
  public abstract ResolveInfo resolveActivity(Intent paramIntent, int paramInt);
  
  public abstract ResolveInfo resolveActivityAsUser(Intent paramIntent, int paramInt1, int paramInt2);
  
  public abstract ProviderInfo resolveContentProvider(String paramString, int paramInt);
  
  public abstract ProviderInfo resolveContentProviderAsUser(String paramString, int paramInt1, int paramInt2);
  
  public abstract ResolveInfo resolveService(Intent paramIntent, int paramInt);
  
  public abstract void revokeRuntimePermission(String paramString1, String paramString2, UserHandle paramUserHandle);
  
  public abstract void setApplicationEnabledSetting(String paramString, int paramInt1, int paramInt2);
  
  public abstract boolean setApplicationHiddenSettingAsUser(String paramString, boolean paramBoolean, UserHandle paramUserHandle);
  
  public abstract void setComponentEnabledSetting(ComponentName paramComponentName, int paramInt1, int paramInt2);
  
  public abstract boolean setDefaultBrowserPackageNameAsUser(String paramString, int paramInt);
  
  public abstract boolean setEphemeralCookie(byte[] paramArrayOfByte);
  
  public abstract void setInstallerPackageName(String paramString1, String paramString2);
  
  public abstract String[] setPackagesSuspendedAsUser(String[] paramArrayOfString, boolean paramBoolean, int paramInt);
  
  public abstract boolean shouldShowRequestPermissionRationale(String paramString);
  
  public abstract void unregisterMoveCallback(MoveCallback paramMoveCallback);
  
  public abstract boolean updateIntentVerificationStatusAsUser(String paramString, int paramInt1, int paramInt2);
  
  public abstract void updatePermissionFlags(String paramString1, String paramString2, int paramInt1, int paramInt2, UserHandle paramUserHandle);
  
  public abstract void verifyIntentFilter(int paramInt1, int paramInt2, List<String> paramList);
  
  public abstract void verifyPendingInstall(int paramInt1, int paramInt2);
  
  public static class LegacyPackageDeleteObserver
    extends PackageDeleteObserver
  {
    private final IPackageDeleteObserver mLegacy;
    
    public LegacyPackageDeleteObserver(IPackageDeleteObserver paramIPackageDeleteObserver)
    {
      this.mLegacy = paramIPackageDeleteObserver;
    }
    
    public void onPackageDeleted(String paramString1, int paramInt, String paramString2)
    {
      if (this.mLegacy == null) {
        return;
      }
      try
      {
        this.mLegacy.packageDeleted(paramString1, paramInt);
        return;
      }
      catch (RemoteException paramString1) {}
    }
  }
  
  public static class LegacyPackageInstallObserver
    extends PackageInstallObserver
  {
    private final IPackageInstallObserver mLegacy;
    
    public LegacyPackageInstallObserver(IPackageInstallObserver paramIPackageInstallObserver)
    {
      this.mLegacy = paramIPackageInstallObserver;
    }
    
    public void onPackageInstalled(String paramString1, int paramInt, String paramString2, Bundle paramBundle)
    {
      if (this.mLegacy == null) {
        return;
      }
      try
      {
        this.mLegacy.packageInstalled(paramString1, paramInt);
        return;
      }
      catch (RemoteException paramString1) {}
    }
  }
  
  public static abstract class MoveCallback
  {
    public void onCreated(int paramInt, Bundle paramBundle) {}
    
    public abstract void onStatusChanged(int paramInt1, int paramInt2, long paramLong);
  }
  
  public static class NameNotFoundException
    extends AndroidException
  {
    public NameNotFoundException() {}
    
    public NameNotFoundException(String paramString)
    {
      super();
    }
  }
  
  public static abstract interface OnPermissionsChangedListener
  {
    public abstract void onPermissionsChanged(int paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/PackageManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */