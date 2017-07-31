package android.content;

import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.Process;
import android.os.ShellCommand;
import android.os.StrictMode;
import android.os.UserHandle;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.Log;
import com.android.internal.R.styleable;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class Intent
  implements Parcelable, Cloneable
{
  public static final String ACTION_ADVANCED_SETTINGS_CHANGED = "android.intent.action.ADVANCED_SETTINGS";
  public static final String ACTION_AIRPLANE_MODE_CHANGED = "android.intent.action.AIRPLANE_MODE";
  public static final String ACTION_ALARM_CHANGED = "android.intent.action.ALARM_CHANGED";
  public static final String ACTION_ALL_APPS = "android.intent.action.ALL_APPS";
  public static final String ACTION_ANSWER = "android.intent.action.ANSWER";
  public static final String ACTION_APPLICATION_PREFERENCES = "android.intent.action.APPLICATION_PREFERENCES";
  public static final String ACTION_APPLICATION_RESTRICTIONS_CHANGED = "android.intent.action.APPLICATION_RESTRICTIONS_CHANGED";
  public static final String ACTION_APP_ERROR = "android.intent.action.APP_ERROR";
  public static final String ACTION_ASSIST = "android.intent.action.ASSIST";
  public static final String ACTION_ATTACH_DATA = "android.intent.action.ATTACH_DATA";
  public static final String ACTION_BATTERY_CHANGED = "android.intent.action.BATTERY_CHANGED";
  public static final String ACTION_BATTERY_LOW = "android.intent.action.BATTERY_LOW";
  public static final String ACTION_BATTERY_OKAY = "android.intent.action.BATTERY_OKAY";
  public static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
  public static final String ACTION_BUG_REPORT = "android.intent.action.BUG_REPORT";
  public static final String ACTION_CALL = "android.intent.action.CALL";
  public static final String ACTION_CALL_BUTTON = "android.intent.action.CALL_BUTTON";
  public static final String ACTION_CALL_EMERGENCY = "android.intent.action.CALL_EMERGENCY";
  public static final String ACTION_CALL_PRIVILEGED = "android.intent.action.CALL_PRIVILEGED";
  public static final String ACTION_CAMERA_BUTTON = "android.intent.action.CAMERA_BUTTON";
  public static final String ACTION_CHOOSER = "android.intent.action.CHOOSER";
  public static final String ACTION_CLEAR_DNS_CACHE = "android.intent.action.CLEAR_DNS_CACHE";
  public static final String ACTION_CLOSE_SYSTEM_DIALOGS = "android.intent.action.CLOSE_SYSTEM_DIALOGS";
  public static final String ACTION_CONFIGURATION_CHANGED = "android.intent.action.CONFIGURATION_CHANGED";
  public static final String ACTION_CREATE_DOCUMENT = "android.intent.action.CREATE_DOCUMENT";
  public static final String ACTION_CREATE_SHORTCUT = "android.intent.action.CREATE_SHORTCUT";
  public static final String ACTION_DATE_CHANGED = "android.intent.action.DATE_CHANGED";
  public static final String ACTION_DEFAULT = "android.intent.action.VIEW";
  public static final String ACTION_DELETE = "android.intent.action.DELETE";
  public static final String ACTION_DEVICE_STORAGE_FULL = "android.intent.action.DEVICE_STORAGE_FULL";
  public static final String ACTION_DEVICE_STORAGE_LOW = "android.intent.action.DEVICE_STORAGE_LOW";
  public static final String ACTION_DEVICE_STORAGE_NOT_FULL = "android.intent.action.DEVICE_STORAGE_NOT_FULL";
  public static final String ACTION_DEVICE_STORAGE_OK = "android.intent.action.DEVICE_STORAGE_OK";
  public static final String ACTION_DIAL = "android.intent.action.DIAL";
  public static final String ACTION_DISMISS_KEYBOARD_SHORTCUTS = "android.intent.action.DISMISS_KEYBOARD_SHORTCUTS";
  public static final String ACTION_DOCK_EVENT = "android.intent.action.DOCK_EVENT";
  public static final String ACTION_DREAMING_STARTED = "android.intent.action.DREAMING_STARTED";
  public static final String ACTION_DREAMING_STOPPED = "android.intent.action.DREAMING_STOPPED";
  public static final String ACTION_DYNAMIC_SENSOR_CHANGED = "android.intent.action.DYNAMIC_SENSOR_CHANGED";
  public static final String ACTION_EDIT = "android.intent.action.EDIT";
  public static final String ACTION_EXTERNAL_APPLICATIONS_AVAILABLE = "android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE";
  public static final String ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE = "android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE";
  public static final String ACTION_FACTORY_TEST = "android.intent.action.FACTORY_TEST";
  public static final String ACTION_GET_CONTENT = "android.intent.action.GET_CONTENT";
  public static final String ACTION_GET_RESTRICTION_ENTRIES = "android.intent.action.GET_RESTRICTION_ENTRIES";
  public static final String ACTION_GLOBAL_BUTTON = "android.intent.action.GLOBAL_BUTTON";
  public static final String ACTION_GTALK_SERVICE_CONNECTED = "android.intent.action.GTALK_CONNECTED";
  public static final String ACTION_GTALK_SERVICE_DISCONNECTED = "android.intent.action.GTALK_DISCONNECTED";
  public static final String ACTION_HEADSET_PLUG = "android.intent.action.HEADSET_PLUG";
  public static final String ACTION_IDLE_MAINTENANCE_END = "android.intent.action.ACTION_IDLE_MAINTENANCE_END";
  public static final String ACTION_IDLE_MAINTENANCE_START = "android.intent.action.ACTION_IDLE_MAINTENANCE_START";
  public static final String ACTION_INPUT_METHOD_CHANGED = "android.intent.action.INPUT_METHOD_CHANGED";
  public static final String ACTION_INSERT = "android.intent.action.INSERT";
  public static final String ACTION_INSERT_OR_EDIT = "android.intent.action.INSERT_OR_EDIT";
  public static final String ACTION_INSTALL_EPHEMERAL_PACKAGE = "android.intent.action.INSTALL_EPHEMERAL_PACKAGE";
  public static final String ACTION_INSTALL_PACKAGE = "android.intent.action.INSTALL_PACKAGE";
  public static final String ACTION_INTENT_FILTER_NEEDS_VERIFICATION = "android.intent.action.INTENT_FILTER_NEEDS_VERIFICATION";
  public static final String ACTION_LOCALE_CHANGED = "android.intent.action.LOCALE_CHANGED";
  public static final String ACTION_LOCKED_BOOT_COMPLETED = "android.intent.action.LOCKED_BOOT_COMPLETED";
  public static final String ACTION_MAIN = "android.intent.action.MAIN";
  public static final String ACTION_MANAGED_PROFILE_ADDED = "android.intent.action.MANAGED_PROFILE_ADDED";
  public static final String ACTION_MANAGED_PROFILE_AVAILABLE = "android.intent.action.MANAGED_PROFILE_AVAILABLE";
  public static final String ACTION_MANAGED_PROFILE_REMOVED = "android.intent.action.MANAGED_PROFILE_REMOVED";
  public static final String ACTION_MANAGED_PROFILE_UNAVAILABLE = "android.intent.action.MANAGED_PROFILE_UNAVAILABLE";
  public static final String ACTION_MANAGED_PROFILE_UNLOCKED = "android.intent.action.MANAGED_PROFILE_UNLOCKED";
  public static final String ACTION_MANAGE_APP_PERMISSIONS = "android.intent.action.MANAGE_APP_PERMISSIONS";
  public static final String ACTION_MANAGE_NETWORK_USAGE = "android.intent.action.MANAGE_NETWORK_USAGE";
  public static final String ACTION_MANAGE_PACKAGE_STORAGE = "android.intent.action.MANAGE_PACKAGE_STORAGE";
  public static final String ACTION_MANAGE_PERMISSIONS = "android.intent.action.MANAGE_PERMISSIONS";
  public static final String ACTION_MANAGE_PERMISSION_APPS = "android.intent.action.MANAGE_PERMISSION_APPS";
  public static final String ACTION_MASTER_CLEAR = "android.intent.action.MASTER_CLEAR";
  public static final String ACTION_MEDIA_BAD_REMOVAL = "android.intent.action.MEDIA_BAD_REMOVAL";
  public static final String ACTION_MEDIA_BUTTON = "android.intent.action.MEDIA_BUTTON";
  public static final String ACTION_MEDIA_CHECKING = "android.intent.action.MEDIA_CHECKING";
  public static final String ACTION_MEDIA_EJECT = "android.intent.action.MEDIA_EJECT";
  public static final String ACTION_MEDIA_MOUNTED = "android.intent.action.MEDIA_MOUNTED";
  public static final String ACTION_MEDIA_NOFS = "android.intent.action.MEDIA_NOFS";
  public static final String ACTION_MEDIA_REMOVED = "android.intent.action.MEDIA_REMOVED";
  public static final String ACTION_MEDIA_RESOURCE_GRANTED = "android.intent.action.MEDIA_RESOURCE_GRANTED";
  public static final String ACTION_MEDIA_SCANNER_FINISHED = "android.intent.action.MEDIA_SCANNER_FINISHED";
  public static final String ACTION_MEDIA_SCANNER_SCAN_FILE = "android.intent.action.MEDIA_SCANNER_SCAN_FILE";
  public static final String ACTION_MEDIA_SCANNER_STARTED = "android.intent.action.MEDIA_SCANNER_STARTED";
  public static final String ACTION_MEDIA_SHARED = "android.intent.action.MEDIA_SHARED";
  public static final String ACTION_MEDIA_UNMOUNTABLE = "android.intent.action.MEDIA_UNMOUNTABLE";
  public static final String ACTION_MEDIA_UNMOUNTED = "android.intent.action.MEDIA_UNMOUNTED";
  public static final String ACTION_MEDIA_UNSHARED = "android.intent.action.MEDIA_UNSHARED";
  public static final String ACTION_MY_PACKAGE_REPLACED = "android.intent.action.MY_PACKAGE_REPLACED";
  public static final String ACTION_NEW_OUTGOING_CALL = "android.intent.action.NEW_OUTGOING_CALL";
  public static final String ACTION_OPEN_DOCUMENT = "android.intent.action.OPEN_DOCUMENT";
  public static final String ACTION_OPEN_DOCUMENT_TREE = "android.intent.action.OPEN_DOCUMENT_TREE";
  public static final String ACTION_PACKAGES_SUSPENDED = "android.intent.action.PACKAGES_SUSPENDED";
  public static final String ACTION_PACKAGES_UNSUSPENDED = "android.intent.action.PACKAGES_UNSUSPENDED";
  public static final String ACTION_PACKAGE_ADDED = "android.intent.action.PACKAGE_ADDED";
  public static final String ACTION_PACKAGE_CHANGED = "android.intent.action.PACKAGE_CHANGED";
  public static final String ACTION_PACKAGE_DATA_CLEARED = "android.intent.action.PACKAGE_DATA_CLEARED";
  public static final String ACTION_PACKAGE_FIRST_LAUNCH = "android.intent.action.PACKAGE_FIRST_LAUNCH";
  public static final String ACTION_PACKAGE_FULLY_REMOVED = "android.intent.action.PACKAGE_FULLY_REMOVED";
  @Deprecated
  public static final String ACTION_PACKAGE_INSTALL = "android.intent.action.PACKAGE_INSTALL";
  public static final String ACTION_PACKAGE_NEEDS_VERIFICATION = "android.intent.action.PACKAGE_NEEDS_VERIFICATION";
  public static final String ACTION_PACKAGE_REMOVED = "android.intent.action.PACKAGE_REMOVED";
  public static final String ACTION_PACKAGE_REPLACED = "android.intent.action.PACKAGE_REPLACED";
  public static final String ACTION_PACKAGE_RESTARTED = "android.intent.action.PACKAGE_RESTARTED";
  public static final String ACTION_PACKAGE_VERIFIED = "android.intent.action.PACKAGE_VERIFIED";
  public static final String ACTION_PASTE = "android.intent.action.PASTE";
  public static final String ACTION_PICK = "android.intent.action.PICK";
  public static final String ACTION_PICK_ACTIVITY = "android.intent.action.PICK_ACTIVITY";
  public static final String ACTION_POWER_CONNECTED = "android.intent.action.ACTION_POWER_CONNECTED";
  public static final String ACTION_POWER_DISCONNECTED = "android.intent.action.ACTION_POWER_DISCONNECTED";
  public static final String ACTION_POWER_USAGE_SUMMARY = "android.intent.action.POWER_USAGE_SUMMARY";
  public static final String ACTION_PREFERRED_ACTIVITY_CHANGED = "android.intent.action.ACTION_PREFERRED_ACTIVITY_CHANGED";
  public static final String ACTION_PRE_BOOT_COMPLETED = "android.intent.action.PRE_BOOT_COMPLETED";
  public static final String ACTION_PROCESS_TEXT = "android.intent.action.PROCESS_TEXT";
  public static final String ACTION_PROVIDER_CHANGED = "android.intent.action.PROVIDER_CHANGED";
  public static final String ACTION_QUERY_PACKAGE_RESTART = "android.intent.action.QUERY_PACKAGE_RESTART";
  public static final String ACTION_QUICK_CLOCK = "android.intent.action.QUICK_CLOCK";
  public static final String ACTION_QUICK_VIEW = "android.intent.action.QUICK_VIEW";
  public static final String ACTION_REBOOT = "android.intent.action.REBOOT";
  public static final String ACTION_REMOTE_INTENT = "com.google.android.c2dm.intent.RECEIVE";
  public static final String ACTION_REQUEST_SHUTDOWN = "android.intent.action.ACTION_REQUEST_SHUTDOWN";
  public static final String ACTION_RESOLVE_EPHEMERAL_PACKAGE = "android.intent.action.RESOLVE_EPHEMERAL_PACKAGE";
  public static final String ACTION_REVIEW_PERMISSIONS = "android.intent.action.REVIEW_PERMISSIONS";
  public static final String ACTION_RUN = "android.intent.action.RUN";
  public static final String ACTION_SCREEN_OFF = "android.intent.action.SCREEN_OFF";
  public static final String ACTION_SCREEN_ON = "android.intent.action.SCREEN_ON";
  public static final String ACTION_SEARCH = "android.intent.action.SEARCH";
  public static final String ACTION_SEARCH_LONG_PRESS = "android.intent.action.SEARCH_LONG_PRESS";
  public static final String ACTION_SEND = "android.intent.action.SEND";
  public static final String ACTION_SENDTO = "android.intent.action.SENDTO";
  public static final String ACTION_SEND_MULTIPLE = "android.intent.action.SEND_MULTIPLE";
  public static final String ACTION_SETTING_RESTORED = "android.os.action.SETTING_RESTORED";
  public static final String ACTION_SET_WALLPAPER = "android.intent.action.SET_WALLPAPER";
  public static final String ACTION_SHOW_APP_INFO = "android.intent.action.SHOW_APP_INFO";
  public static final String ACTION_SHOW_BRIGHTNESS_DIALOG = "android.intent.action.SHOW_BRIGHTNESS_DIALOG";
  public static final String ACTION_SHOW_KEYBOARD_SHORTCUTS = "android.intent.action.SHOW_KEYBOARD_SHORTCUTS";
  public static final String ACTION_SHUTDOWN = "android.intent.action.ACTION_SHUTDOWN";
  public static final String ACTION_SIM_ACTIVATION_REQUEST = "android.intent.action.SIM_ACTIVATION_REQUEST";
  public static final String ACTION_SYNC = "android.intent.action.SYNC";
  public static final String ACTION_SYSTEM_TUTORIAL = "android.intent.action.SYSTEM_TUTORIAL";
  public static final String ACTION_THERMAL_EVENT = "android.intent.action.THERMAL_EVENT";
  public static final String ACTION_TIMEZONE_CHANGED = "android.intent.action.TIMEZONE_CHANGED";
  public static final String ACTION_TIME_CHANGED = "android.intent.action.TIME_SET";
  public static final String ACTION_TIME_TICK = "android.intent.action.TIME_TICK";
  public static final String ACTION_UID_REMOVED = "android.intent.action.UID_REMOVED";
  @Deprecated
  public static final String ACTION_UMS_CONNECTED = "android.intent.action.UMS_CONNECTED";
  @Deprecated
  public static final String ACTION_UMS_DISCONNECTED = "android.intent.action.UMS_DISCONNECTED";
  public static final String ACTION_UNINSTALL_PACKAGE = "android.intent.action.UNINSTALL_PACKAGE";
  public static final String ACTION_UPGRADE_SETUP = "android.intent.action.UPGRADE_SETUP";
  public static final String ACTION_USER_ADDED = "android.intent.action.USER_ADDED";
  public static final String ACTION_USER_BACKGROUND = "android.intent.action.USER_BACKGROUND";
  public static final String ACTION_USER_FOREGROUND = "android.intent.action.USER_FOREGROUND";
  public static final String ACTION_USER_INFO_CHANGED = "android.intent.action.USER_INFO_CHANGED";
  public static final String ACTION_USER_INITIALIZE = "android.intent.action.USER_INITIALIZE";
  public static final String ACTION_USER_PRESENT = "android.intent.action.USER_PRESENT";
  public static final String ACTION_USER_REMOVED = "android.intent.action.USER_REMOVED";
  public static final String ACTION_USER_STARTED = "android.intent.action.USER_STARTED";
  public static final String ACTION_USER_STARTING = "android.intent.action.USER_STARTING";
  public static final String ACTION_USER_STOPPED = "android.intent.action.USER_STOPPED";
  public static final String ACTION_USER_STOPPING = "android.intent.action.USER_STOPPING";
  public static final String ACTION_USER_SWITCHED = "android.intent.action.USER_SWITCHED";
  public static final String ACTION_USER_UNLOCKED = "android.intent.action.USER_UNLOCKED";
  public static final String ACTION_VIEW = "android.intent.action.VIEW";
  public static final String ACTION_VOICE_ASSIST = "android.intent.action.VOICE_ASSIST";
  public static final String ACTION_VOICE_COMMAND = "android.intent.action.VOICE_COMMAND";
  @Deprecated
  public static final String ACTION_WALLPAPER_CHANGED = "android.intent.action.WALLPAPER_CHANGED";
  public static final String ACTION_WEB_SEARCH = "android.intent.action.WEB_SEARCH";
  private static final String ATTR_ACTION = "action";
  private static final String ATTR_CATEGORY = "category";
  private static final String ATTR_COMPONENT = "component";
  private static final String ATTR_DATA = "data";
  private static final String ATTR_FLAGS = "flags";
  private static final String ATTR_TYPE = "type";
  public static final String CATEGORY_ALTERNATIVE = "android.intent.category.ALTERNATIVE";
  public static final String CATEGORY_APP_BROWSER = "android.intent.category.APP_BROWSER";
  public static final String CATEGORY_APP_CALCULATOR = "android.intent.category.APP_CALCULATOR";
  public static final String CATEGORY_APP_CALENDAR = "android.intent.category.APP_CALENDAR";
  public static final String CATEGORY_APP_CONTACTS = "android.intent.category.APP_CONTACTS";
  public static final String CATEGORY_APP_EMAIL = "android.intent.category.APP_EMAIL";
  public static final String CATEGORY_APP_GALLERY = "android.intent.category.APP_GALLERY";
  public static final String CATEGORY_APP_MAPS = "android.intent.category.APP_MAPS";
  public static final String CATEGORY_APP_MARKET = "android.intent.category.APP_MARKET";
  public static final String CATEGORY_APP_MESSAGING = "android.intent.category.APP_MESSAGING";
  public static final String CATEGORY_APP_MUSIC = "android.intent.category.APP_MUSIC";
  public static final String CATEGORY_BROWSABLE = "android.intent.category.BROWSABLE";
  public static final String CATEGORY_CAR_DOCK = "android.intent.category.CAR_DOCK";
  public static final String CATEGORY_CAR_MODE = "android.intent.category.CAR_MODE";
  public static final String CATEGORY_DEFAULT = "android.intent.category.DEFAULT";
  public static final String CATEGORY_DESK_DOCK = "android.intent.category.DESK_DOCK";
  public static final String CATEGORY_DEVELOPMENT_PREFERENCE = "android.intent.category.DEVELOPMENT_PREFERENCE";
  public static final String CATEGORY_EMBED = "android.intent.category.EMBED";
  public static final String CATEGORY_FRAMEWORK_INSTRUMENTATION_TEST = "android.intent.category.FRAMEWORK_INSTRUMENTATION_TEST";
  public static final String CATEGORY_HE_DESK_DOCK = "android.intent.category.HE_DESK_DOCK";
  public static final String CATEGORY_HOME = "android.intent.category.HOME";
  public static final String CATEGORY_HOME_MAIN = "android.intent.category.HOME_MAIN";
  public static final String CATEGORY_INFO = "android.intent.category.INFO";
  public static final String CATEGORY_LAUNCHER = "android.intent.category.LAUNCHER";
  public static final String CATEGORY_LEANBACK_LAUNCHER = "android.intent.category.LEANBACK_LAUNCHER";
  public static final String CATEGORY_LEANBACK_SETTINGS = "android.intent.category.LEANBACK_SETTINGS";
  public static final String CATEGORY_LE_DESK_DOCK = "android.intent.category.LE_DESK_DOCK";
  public static final String CATEGORY_MONKEY = "android.intent.category.MONKEY";
  public static final String CATEGORY_OPENABLE = "android.intent.category.OPENABLE";
  public static final String CATEGORY_PREFERENCE = "android.intent.category.PREFERENCE";
  public static final String CATEGORY_SAMPLE_CODE = "android.intent.category.SAMPLE_CODE";
  public static final String CATEGORY_SELECTED_ALTERNATIVE = "android.intent.category.SELECTED_ALTERNATIVE";
  public static final String CATEGORY_SETUP_WIZARD = "android.intent.category.SETUP_WIZARD";
  public static final String CATEGORY_TAB = "android.intent.category.TAB";
  public static final String CATEGORY_TEST = "android.intent.category.TEST";
  public static final String CATEGORY_UNIT_TEST = "android.intent.category.UNIT_TEST";
  public static final String CATEGORY_VOICE = "android.intent.category.VOICE";
  public static final Parcelable.Creator<Intent> CREATOR = new Parcelable.Creator()
  {
    public Intent createFromParcel(Parcel paramAnonymousParcel)
    {
      return new Intent(paramAnonymousParcel);
    }
    
    public Intent[] newArray(int paramAnonymousInt)
    {
      return new Intent[paramAnonymousInt];
    }
  };
  public static final String EXTRA_ALARM_COUNT = "android.intent.extra.ALARM_COUNT";
  public static final String EXTRA_ALLOW_MULTIPLE = "android.intent.extra.ALLOW_MULTIPLE";
  @Deprecated
  public static final String EXTRA_ALLOW_REPLACE = "android.intent.extra.ALLOW_REPLACE";
  public static final String EXTRA_ALTERNATE_INTENTS = "android.intent.extra.ALTERNATE_INTENTS";
  public static final String EXTRA_ASSIST_CONTEXT = "android.intent.extra.ASSIST_CONTEXT";
  public static final String EXTRA_ASSIST_INPUT_DEVICE_ID = "android.intent.extra.ASSIST_INPUT_DEVICE_ID";
  public static final String EXTRA_ASSIST_INPUT_HINT_KEYBOARD = "android.intent.extra.ASSIST_INPUT_HINT_KEYBOARD";
  public static final String EXTRA_ASSIST_PACKAGE = "android.intent.extra.ASSIST_PACKAGE";
  public static final String EXTRA_ASSIST_UID = "android.intent.extra.ASSIST_UID";
  public static final String EXTRA_BCC = "android.intent.extra.BCC";
  public static final String EXTRA_BUG_REPORT = "android.intent.extra.BUG_REPORT";
  public static final String EXTRA_CC = "android.intent.extra.CC";
  @Deprecated
  public static final String EXTRA_CHANGED_COMPONENT_NAME = "android.intent.extra.changed_component_name";
  public static final String EXTRA_CHANGED_COMPONENT_NAME_LIST = "android.intent.extra.changed_component_name_list";
  public static final String EXTRA_CHANGED_PACKAGE_LIST = "android.intent.extra.changed_package_list";
  public static final String EXTRA_CHANGED_UID_LIST = "android.intent.extra.changed_uid_list";
  public static final String EXTRA_CHOOSER_REFINEMENT_INTENT_SENDER = "android.intent.extra.CHOOSER_REFINEMENT_INTENT_SENDER";
  public static final String EXTRA_CHOOSER_TARGETS = "android.intent.extra.CHOOSER_TARGETS";
  public static final String EXTRA_CHOSEN_COMPONENT = "android.intent.extra.CHOSEN_COMPONENT";
  public static final String EXTRA_CHOSEN_COMPONENT_INTENT_SENDER = "android.intent.extra.CHOSEN_COMPONENT_INTENT_SENDER";
  public static final String EXTRA_CLIENT_INTENT = "android.intent.extra.client_intent";
  public static final String EXTRA_CLIENT_LABEL = "android.intent.extra.client_label";
  public static final String EXTRA_DATA_REMOVED = "android.intent.extra.DATA_REMOVED";
  public static final String EXTRA_DOCK_STATE = "android.intent.extra.DOCK_STATE";
  public static final int EXTRA_DOCK_STATE_CAR = 2;
  public static final int EXTRA_DOCK_STATE_DESK = 1;
  public static final int EXTRA_DOCK_STATE_HE_DESK = 4;
  public static final int EXTRA_DOCK_STATE_LE_DESK = 3;
  public static final int EXTRA_DOCK_STATE_UNDOCKED = 0;
  public static final String EXTRA_DONT_KILL_APP = "android.intent.extra.DONT_KILL_APP";
  public static final String EXTRA_EMAIL = "android.intent.extra.EMAIL";
  public static final String EXTRA_EPHEMERAL_FAILURE = "android.intent.extra.EPHEMERAL_FAILURE";
  public static final String EXTRA_EPHEMERAL_SUCCESS = "android.intent.extra.EPHEMERAL_SUCCESS";
  public static final String EXTRA_EXCLUDE_COMPONENTS = "android.intent.extra.EXCLUDE_COMPONENTS";
  public static final String EXTRA_FORCE_MASTER_CLEAR = "android.intent.extra.FORCE_MASTER_CLEAR";
  public static final String EXTRA_HTML_TEXT = "android.intent.extra.HTML_TEXT";
  public static final String EXTRA_INDEX = "android.intent.extra.INDEX";
  public static final String EXTRA_INITIAL_INTENTS = "android.intent.extra.INITIAL_INTENTS";
  public static final String EXTRA_INSTALLER_PACKAGE_NAME = "android.intent.extra.INSTALLER_PACKAGE_NAME";
  public static final String EXTRA_INSTALL_RESULT = "android.intent.extra.INSTALL_RESULT";
  public static final String EXTRA_INTENT = "android.intent.extra.INTENT";
  public static final String EXTRA_KEY_CONFIRM = "android.intent.extra.KEY_CONFIRM";
  public static final String EXTRA_KEY_EVENT = "android.intent.extra.KEY_EVENT";
  public static final String EXTRA_LOCAL_ONLY = "android.intent.extra.LOCAL_ONLY";
  public static final String EXTRA_MEDIA_RESOURCE_TYPE = "android.intent.extra.MEDIA_RESOURCE_TYPE";
  public static final int EXTRA_MEDIA_RESOURCE_TYPE_AUDIO_CODEC = 1;
  public static final int EXTRA_MEDIA_RESOURCE_TYPE_VIDEO_CODEC = 0;
  public static final String EXTRA_MIME_TYPES = "android.intent.extra.MIME_TYPES";
  public static final String EXTRA_NOT_UNKNOWN_SOURCE = "android.intent.extra.NOT_UNKNOWN_SOURCE";
  public static final String EXTRA_ORIGINATING_UID = "android.intent.extra.ORIGINATING_UID";
  public static final String EXTRA_ORIGINATING_URI = "android.intent.extra.ORIGINATING_URI";
  public static final String EXTRA_PACKAGES = "android.intent.extra.PACKAGES";
  public static final String EXTRA_PACKAGE_NAME = "android.intent.extra.PACKAGE_NAME";
  public static final String EXTRA_PERMISSION_NAME = "android.intent.extra.PERMISSION_NAME";
  public static final String EXTRA_PHONE_NUMBER = "android.intent.extra.PHONE_NUMBER";
  public static final String EXTRA_PROCESS_TEXT = "android.intent.extra.PROCESS_TEXT";
  public static final String EXTRA_PROCESS_TEXT_READONLY = "android.intent.extra.PROCESS_TEXT_READONLY";
  public static final String EXTRA_QUIET_MODE = "android.intent.extra.QUIET_MODE";
  public static final String EXTRA_REASON = "android.intent.extra.REASON";
  public static final String EXTRA_REFERRER = "android.intent.extra.REFERRER";
  public static final String EXTRA_REFERRER_NAME = "android.intent.extra.REFERRER_NAME";
  public static final String EXTRA_REMOTE_CALLBACK = "android.intent.extra.REMOTE_CALLBACK";
  public static final String EXTRA_REMOTE_INTENT_TOKEN = "android.intent.extra.remote_intent_token";
  public static final String EXTRA_REMOVED_FOR_ALL_USERS = "android.intent.extra.REMOVED_FOR_ALL_USERS";
  public static final String EXTRA_REPLACEMENT_EXTRAS = "android.intent.extra.REPLACEMENT_EXTRAS";
  public static final String EXTRA_REPLACING = "android.intent.extra.REPLACING";
  public static final String EXTRA_RESTRICTIONS_BUNDLE = "android.intent.extra.restrictions_bundle";
  public static final String EXTRA_RESTRICTIONS_INTENT = "android.intent.extra.restrictions_intent";
  public static final String EXTRA_RESTRICTIONS_LIST = "android.intent.extra.restrictions_list";
  public static final String EXTRA_RESULT_NEEDED = "android.intent.extra.RESULT_NEEDED";
  public static final String EXTRA_RESULT_RECEIVER = "android.intent.extra.RESULT_RECEIVER";
  public static final String EXTRA_RETURN_RESULT = "android.intent.extra.RETURN_RESULT";
  public static final String EXTRA_SETTING_NAME = "setting_name";
  public static final String EXTRA_SETTING_NEW_VALUE = "new_value";
  public static final String EXTRA_SETTING_PREVIOUS_VALUE = "previous_value";
  public static final String EXTRA_SHORTCUT_ICON = "android.intent.extra.shortcut.ICON";
  public static final String EXTRA_SHORTCUT_ICON_RESOURCE = "android.intent.extra.shortcut.ICON_RESOURCE";
  public static final String EXTRA_SHORTCUT_INTENT = "android.intent.extra.shortcut.INTENT";
  public static final String EXTRA_SHORTCUT_NAME = "android.intent.extra.shortcut.NAME";
  public static final String EXTRA_SHUTDOWN_USERSPACE_ONLY = "android.intent.extra.SHUTDOWN_USERSPACE_ONLY";
  public static final String EXTRA_SIM_ACTIVATION_RESPONSE = "android.intent.extra.SIM_ACTIVATION_RESPONSE";
  public static final String EXTRA_STREAM = "android.intent.extra.STREAM";
  public static final String EXTRA_SUBJECT = "android.intent.extra.SUBJECT";
  public static final String EXTRA_TASK_ID = "android.intent.extra.TASK_ID";
  public static final String EXTRA_TEMPLATE = "android.intent.extra.TEMPLATE";
  public static final String EXTRA_TEXT = "android.intent.extra.TEXT";
  public static final String EXTRA_THERMAL_STATE = "android.intent.extra.THERMAL_STATE";
  public static final int EXTRA_THERMAL_STATE_EXCEEDED = 2;
  public static final int EXTRA_THERMAL_STATE_NORMAL = 0;
  public static final int EXTRA_THERMAL_STATE_WARNING = 1;
  public static final String EXTRA_TIME_PREF_24_HOUR_FORMAT = "android.intent.extra.TIME_PREF_24_HOUR_FORMAT";
  public static final String EXTRA_TITLE = "android.intent.extra.TITLE";
  public static final String EXTRA_UID = "android.intent.extra.UID";
  public static final String EXTRA_UNINSTALL_ALL_USERS = "android.intent.extra.UNINSTALL_ALL_USERS";
  public static final String EXTRA_USER = "android.intent.extra.USER";
  public static final String EXTRA_USER_HANDLE = "android.intent.extra.user_handle";
  public static final String EXTRA_USER_ID = "android.intent.extra.USER_ID";
  public static final String EXTRA_USER_REQUESTED_SHUTDOWN = "android.intent.extra.USER_REQUESTED_SHUTDOWN";
  public static final String EXTRA_WIPE_EXTERNAL_STORAGE = "android.intent.extra.WIPE_EXTERNAL_STORAGE";
  public static final int FILL_IN_ACTION = 1;
  public static final int FILL_IN_CATEGORIES = 4;
  public static final int FILL_IN_CLIP_DATA = 128;
  public static final int FILL_IN_COMPONENT = 8;
  public static final int FILL_IN_DATA = 2;
  public static final int FILL_IN_PACKAGE = 16;
  public static final int FILL_IN_SELECTOR = 64;
  public static final int FILL_IN_SOURCE_BOUNDS = 32;
  public static final int FLAG_ACTIVITY_BROUGHT_TO_FRONT = 4194304;
  public static final int FLAG_ACTIVITY_CLEAR_TASK = 32768;
  public static final int FLAG_ACTIVITY_CLEAR_TOP = 67108864;
  public static final int FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET = 524288;
  public static final int FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS = 8388608;
  public static final int FLAG_ACTIVITY_FORWARD_RESULT = 33554432;
  public static final int FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY = 1048576;
  public static final int FLAG_ACTIVITY_LAUNCH_ADJACENT = 4096;
  public static final int FLAG_ACTIVITY_MULTIPLE_TASK = 134217728;
  public static final int FLAG_ACTIVITY_NEW_DOCUMENT = 524288;
  public static final int FLAG_ACTIVITY_NEW_TASK = 268435456;
  public static final int FLAG_ACTIVITY_NO_ANIMATION = 65536;
  public static final int FLAG_ACTIVITY_NO_HISTORY = 1073741824;
  public static final int FLAG_ACTIVITY_NO_USER_ACTION = 262144;
  public static final int FLAG_ACTIVITY_PREVIOUS_IS_TOP = 16777216;
  public static final int FLAG_ACTIVITY_REORDER_TO_FRONT = 131072;
  public static final int FLAG_ACTIVITY_RESET_TASK_IF_NEEDED = 2097152;
  public static final int FLAG_ACTIVITY_RETAIN_IN_RECENTS = 8192;
  public static final int FLAG_ACTIVITY_SINGLE_TOP = 536870912;
  public static final int FLAG_ACTIVITY_TASK_ON_HOME = 16384;
  public static final int FLAG_DEBUG_LOG_RESOLUTION = 8;
  public static final int FLAG_DEBUG_TRIAGED_MISSING = 256;
  public static final int FLAG_EXCLUDE_STOPPED_PACKAGES = 16;
  public static final int FLAG_FROM_BACKGROUND = 4;
  public static final int FLAG_GRANT_PERSISTABLE_URI_PERMISSION = 64;
  public static final int FLAG_GRANT_PREFIX_URI_PERMISSION = 128;
  public static final int FLAG_GRANT_READ_URI_PERMISSION = 1;
  public static final int FLAG_GRANT_WRITE_URI_PERMISSION = 2;
  public static final int FLAG_IGNORE_EPHEMERAL = 512;
  public static final int FLAG_INCLUDE_STOPPED_PACKAGES = 32;
  public static final int FLAG_RECEIVER_BOOT_UPGRADE = 33554432;
  public static final int FLAG_RECEIVER_EXCLUDE_BACKGROUND = 8388608;
  public static final int FLAG_RECEIVER_FOREGROUND = 268435456;
  public static final int FLAG_RECEIVER_INCLUDE_BACKGROUND = 16777216;
  public static final int FLAG_RECEIVER_NO_ABORT = 134217728;
  public static final int FLAG_RECEIVER_REGISTERED_ONLY = 1073741824;
  public static final int FLAG_RECEIVER_REGISTERED_ONLY_BEFORE_BOOT = 67108864;
  public static final int FLAG_RECEIVER_REPLACE_PENDING = 536870912;
  public static final int IMMUTABLE_FLAGS = 195;
  public static final String METADATA_DOCK_HOME = "android.dock_home";
  public static final String METADATA_SETUP_VERSION = "android.SETUP_VERSION";
  private static final String TAG_CATEGORIES = "categories";
  private static final String TAG_EXTRA = "extra";
  public static final int URI_ALLOW_UNSAFE = 4;
  public static final int URI_ANDROID_APP_SCHEME = 2;
  public static final int URI_INTENT_SCHEME = 1;
  private String mAction;
  private ArraySet<String> mCategories;
  private ClipData mClipData;
  private ComponentName mComponent;
  private int mContentUserHint = -2;
  private Uri mData;
  private Bundle mExtras;
  private int mFlags;
  private String mPackage;
  private Intent mSelector;
  private Rect mSourceBounds;
  private String mType;
  
  public Intent() {}
  
  public Intent(Context paramContext, Class<?> paramClass)
  {
    this.mComponent = new ComponentName(paramContext, paramClass);
  }
  
  public Intent(Intent paramIntent)
  {
    this.mAction = paramIntent.mAction;
    this.mData = paramIntent.mData;
    this.mType = paramIntent.mType;
    this.mPackage = paramIntent.mPackage;
    this.mComponent = paramIntent.mComponent;
    this.mFlags = paramIntent.mFlags;
    this.mContentUserHint = paramIntent.mContentUserHint;
    if (paramIntent.mCategories != null) {
      this.mCategories = new ArraySet(paramIntent.mCategories);
    }
    if (paramIntent.mExtras != null) {
      this.mExtras = new Bundle(paramIntent.mExtras);
    }
    if (paramIntent.mSourceBounds != null) {
      this.mSourceBounds = new Rect(paramIntent.mSourceBounds);
    }
    if (paramIntent.mSelector != null) {
      this.mSelector = new Intent(paramIntent.mSelector);
    }
    if (paramIntent.mClipData != null) {
      this.mClipData = new ClipData(paramIntent.mClipData);
    }
  }
  
  private Intent(Intent paramIntent, boolean paramBoolean)
  {
    this.mAction = paramIntent.mAction;
    this.mData = paramIntent.mData;
    this.mType = paramIntent.mType;
    this.mPackage = paramIntent.mPackage;
    this.mComponent = paramIntent.mComponent;
    if (paramIntent.mCategories != null) {
      this.mCategories = new ArraySet(paramIntent.mCategories);
    }
  }
  
  protected Intent(Parcel paramParcel)
  {
    readFromParcel(paramParcel);
  }
  
  public Intent(String paramString)
  {
    setAction(paramString);
  }
  
  public Intent(String paramString, Uri paramUri)
  {
    setAction(paramString);
    this.mData = paramUri;
  }
  
  public Intent(String paramString, Uri paramUri, Context paramContext, Class<?> paramClass)
  {
    setAction(paramString);
    this.mData = paramUri;
    this.mComponent = new ComponentName(paramContext, paramClass);
  }
  
  public static Intent createChooser(Intent paramIntent, CharSequence paramCharSequence)
  {
    return createChooser(paramIntent, paramCharSequence, null);
  }
  
  public static Intent createChooser(Intent paramIntent, CharSequence paramCharSequence, IntentSender paramIntentSender)
  {
    Intent localIntent = new Intent("android.intent.action.CHOOSER");
    localIntent.putExtra("android.intent.extra.INTENT", paramIntent);
    if (paramCharSequence != null) {
      localIntent.putExtra("android.intent.extra.TITLE", paramCharSequence);
    }
    if (paramIntentSender != null) {
      localIntent.putExtra("android.intent.extra.CHOSEN_COMPONENT_INTENT_SENDER", paramIntentSender);
    }
    int i = paramIntent.getFlags() & 0xC3;
    if (i != 0)
    {
      paramIntentSender = paramIntent.getClipData();
      paramCharSequence = paramIntentSender;
      if (paramIntentSender == null)
      {
        paramCharSequence = paramIntentSender;
        if (paramIntent.getData() != null)
        {
          paramIntentSender = new ClipData.Item(paramIntent.getData());
          if (paramIntent.getType() == null) {
            break label146;
          }
          paramCharSequence = new String[1];
          paramCharSequence[0] = paramIntent.getType();
        }
      }
    }
    label146:
    for (paramIntent = paramCharSequence;; paramIntent = new String[0])
    {
      paramCharSequence = new ClipData(null, paramIntent, paramIntentSender);
      if (paramCharSequence != null)
      {
        localIntent.setClipData(paramCharSequence);
        localIntent.addFlags(i);
      }
      return localIntent;
    }
  }
  
  @Deprecated
  public static Intent getIntent(String paramString)
    throws URISyntaxException
  {
    return parseUri(paramString, 0);
  }
  
  public static Intent getIntentOld(String paramString)
    throws URISyntaxException
  {
    return getIntentOld(paramString, 0);
  }
  
  private static Intent getIntentOld(String paramString, int paramInt)
    throws URISyntaxException
  {
    int i1 = paramString.lastIndexOf('#');
    if (i1 >= 0)
    {
      Object localObject = null;
      int k = 0;
      int j = i1 + 1;
      int i = j;
      if (paramString.regionMatches(j, "action(", 0, 7))
      {
        k = 1;
        i = j + 7;
        j = paramString.indexOf(')', i);
        localObject = paramString.substring(i, j);
        i = j + 1;
      }
      localObject = new Intent((String)localObject);
      j = i;
      int m;
      int n;
      if (paramString.regionMatches(i, "categories(", 0, 11))
      {
        k = 1;
        i += 11;
        m = paramString.indexOf(')', i);
        while (i < m)
        {
          n = paramString.indexOf('!', i);
          if (n >= 0)
          {
            j = n;
            if (n <= m) {}
          }
          else
          {
            j = m;
          }
          if (i < j) {
            ((Intent)localObject).addCategory(paramString.substring(i, j));
          }
          i = j + 1;
        }
        j = m + 1;
      }
      i = j;
      if (paramString.regionMatches(j, "type(", 0, 5))
      {
        k = 1;
        i = j + 5;
        j = paramString.indexOf(')', i);
        ((Intent)localObject).mType = paramString.substring(i, j);
        i = j + 1;
      }
      j = i;
      if (paramString.regionMatches(i, "launchFlags(", 0, 12))
      {
        k = 1;
        i += 12;
        j = paramString.indexOf(')', i);
        ((Intent)localObject).mFlags = Integer.decode(paramString.substring(i, j)).intValue();
        if ((paramInt & 0x4) == 0) {
          ((Intent)localObject).mFlags &= 0xFF3C;
        }
        j += 1;
      }
      paramInt = j;
      if (paramString.regionMatches(j, "component(", 0, 10))
      {
        k = 1;
        paramInt = j + 10;
        i = paramString.indexOf(')', paramInt);
        j = paramString.indexOf('!', paramInt);
        if ((j >= 0) && (j < i)) {
          ((Intent)localObject).mComponent = new ComponentName(paramString.substring(paramInt, j), paramString.substring(j + 1, i));
        }
        paramInt = i + 1;
      }
      String str1;
      String str2;
      if (paramString.regionMatches(paramInt, "extras(", 0, 7))
      {
        i = 1;
        k = paramInt + 7;
        j = paramString.indexOf(')', k);
        paramInt = k;
        if (j == -1)
        {
          throw new URISyntaxException(paramString, "EXTRA missing trailing ')'", k);
          paramInt += 1;
        }
        k = i;
        if (paramInt < j)
        {
          k = paramString.indexOf('=', paramInt);
          if ((k <= paramInt + 1) || (paramInt >= j)) {
            throw new URISyntaxException(paramString, "EXTRA missing '='", paramInt);
          }
          m = paramString.charAt(paramInt);
          str1 = paramString.substring(paramInt + 1, k);
          n = k + 1;
          k = paramString.indexOf('!', n);
          if (k != -1)
          {
            paramInt = k;
            if (k < j) {}
          }
          else
          {
            paramInt = j;
          }
          if (n >= paramInt) {
            throw new URISyntaxException(paramString, "EXTRA missing '!'", n);
          }
          str2 = paramString.substring(n, paramInt);
          if (((Intent)localObject).mExtras == null) {
            ((Intent)localObject).mExtras = new Bundle();
          }
        }
      }
      switch (m)
      {
      default: 
        try
        {
          throw new URISyntaxException(paramString, "EXTRA has unknown type", paramInt);
        }
        catch (NumberFormatException localNumberFormatException)
        {
          throw new URISyntaxException(paramString, "EXTRA value can't be parsed", paramInt);
        }
      case 83: 
        localNumberFormatException.mExtras.putString(str1, Uri.decode(str2));
        label699:
        k = paramString.charAt(paramInt);
        if (k == 41)
        {
          k = i;
          if (k == 0) {
            break label922;
          }
        }
        break;
      }
      label922:
      for (localNumberFormatException.mData = Uri.parse(paramString.substring(0, i1));; localNumberFormatException.mData = Uri.parse(paramString))
      {
        if (localNumberFormatException.mAction == null) {
          localNumberFormatException.mAction = "android.intent.action.VIEW";
        }
        return localNumberFormatException;
        localNumberFormatException.mExtras.putBoolean(str1, Boolean.parseBoolean(str2));
        break label699;
        localNumberFormatException.mExtras.putByte(str1, Byte.parseByte(str2));
        break label699;
        localNumberFormatException.mExtras.putChar(str1, Uri.decode(str2).charAt(0));
        break label699;
        localNumberFormatException.mExtras.putDouble(str1, Double.parseDouble(str2));
        break label699;
        localNumberFormatException.mExtras.putFloat(str1, Float.parseFloat(str2));
        break label699;
        localNumberFormatException.mExtras.putInt(str1, Integer.parseInt(str2));
        break label699;
        localNumberFormatException.mExtras.putLong(str1, Long.parseLong(str2));
        break label699;
        localNumberFormatException.mExtras.putShort(str1, Short.parseShort(str2));
        break label699;
        if (k == 33) {
          break;
        }
        throw new URISyntaxException(paramString, "EXTRA missing '!'", paramInt);
      }
    }
    return new Intent("android.intent.action.VIEW", Uri.parse(paramString));
  }
  
  public static boolean isAccessUriMode(int paramInt)
  {
    boolean bool = false;
    if ((paramInt & 0x3) != 0) {
      bool = true;
    }
    return bool;
  }
  
  private static ClipData.Item makeClipItem(ArrayList<Uri> paramArrayList, ArrayList<CharSequence> paramArrayList1, ArrayList<String> paramArrayList2, int paramInt)
  {
    if (paramArrayList != null)
    {
      paramArrayList = (Uri)paramArrayList.get(paramInt);
      if (paramArrayList1 == null) {
        break label56;
      }
      paramArrayList1 = (CharSequence)paramArrayList1.get(paramInt);
      label26:
      if (paramArrayList2 == null) {
        break label61;
      }
    }
    label56:
    label61:
    for (paramArrayList2 = (String)paramArrayList2.get(paramInt);; paramArrayList2 = null)
    {
      return new ClipData.Item(paramArrayList1, paramArrayList2, null, paramArrayList);
      paramArrayList = null;
      break;
      paramArrayList1 = null;
      break label26;
    }
  }
  
  public static Intent makeMainActivity(ComponentName paramComponentName)
  {
    Intent localIntent = new Intent("android.intent.action.MAIN");
    localIntent.setComponent(paramComponentName);
    localIntent.addCategory("android.intent.category.LAUNCHER");
    return localIntent;
  }
  
  public static Intent makeMainSelectorActivity(String paramString1, String paramString2)
  {
    Intent localIntent1 = new Intent("android.intent.action.MAIN");
    localIntent1.addCategory("android.intent.category.LAUNCHER");
    Intent localIntent2 = new Intent();
    localIntent2.setAction(paramString1);
    localIntent2.addCategory(paramString2);
    localIntent1.setSelector(localIntent2);
    return localIntent1;
  }
  
  public static Intent makeRestartActivityTask(ComponentName paramComponentName)
  {
    paramComponentName = makeMainActivity(paramComponentName);
    paramComponentName.addFlags(268468224);
    return paramComponentName;
  }
  
  public static String normalizeMimeType(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    String str = paramString.trim().toLowerCase(Locale.ROOT);
    int i = str.indexOf(';');
    paramString = str;
    if (i != -1) {
      paramString = str.substring(0, i);
    }
    return paramString;
  }
  
  public static Intent parseCommandArgs(ShellCommand paramShellCommand, CommandOptionHandler paramCommandOptionHandler)
    throws URISyntaxException
  {
    Object localObject2 = new Intent();
    int i = 0;
    Object localObject4 = null;
    Object localObject3 = null;
    Object localObject1 = localObject2;
    Object localObject5;
    int j;
    label1842:
    label1945:
    do
    {
      for (;;)
      {
        localObject5 = paramShellCommand.getNextOption();
        if (localObject5 == null) {
          break label1990;
        }
        if (((String)localObject5).equals("-a"))
        {
          ((Intent)localObject1).setAction(paramShellCommand.getNextArgRequired());
          if (localObject1 == localObject2) {
            i = 1;
          }
        }
        else if (((String)localObject5).equals("-d"))
        {
          localObject5 = Uri.parse(paramShellCommand.getNextArgRequired());
          localObject4 = localObject5;
          if (localObject1 == localObject2)
          {
            i = 1;
            localObject4 = localObject5;
          }
        }
        else if (((String)localObject5).equals("-t"))
        {
          localObject5 = paramShellCommand.getNextArgRequired();
          localObject3 = localObject5;
          if (localObject1 == localObject2)
          {
            i = 1;
            localObject3 = localObject5;
          }
        }
        else if (((String)localObject5).equals("-c"))
        {
          ((Intent)localObject1).addCategory(paramShellCommand.getNextArgRequired());
          if (localObject1 == localObject2) {
            i = 1;
          }
        }
        else
        {
          if (((String)localObject5).equals("-e")) {}
          while (((String)localObject5).equals("--es"))
          {
            ((Intent)localObject1).putExtra(paramShellCommand.getNextArgRequired(), paramShellCommand.getNextArgRequired());
            break;
          }
          if (((String)localObject5).equals("--esn"))
          {
            ((Intent)localObject1).putExtra(paramShellCommand.getNextArgRequired(), (String)null);
          }
          else if (((String)localObject5).equals("--ei"))
          {
            ((Intent)localObject1).putExtra(paramShellCommand.getNextArgRequired(), Integer.decode(paramShellCommand.getNextArgRequired()));
          }
          else if (((String)localObject5).equals("--eu"))
          {
            ((Intent)localObject1).putExtra(paramShellCommand.getNextArgRequired(), Uri.parse(paramShellCommand.getNextArgRequired()));
          }
          else
          {
            Object localObject6;
            Object localObject7;
            boolean bool;
            if (((String)localObject5).equals("--ecn"))
            {
              localObject5 = paramShellCommand.getNextArgRequired();
              localObject6 = paramShellCommand.getNextArgRequired();
              localObject7 = ComponentName.unflattenFromString((String)localObject6);
              if (localObject7 == null) {
                throw new IllegalArgumentException("Bad component name: " + (String)localObject6);
              }
            }
            else
            {
              if (((String)localObject5).equals("--eia"))
              {
                localObject5 = paramShellCommand.getNextArgRequired();
                localObject6 = paramShellCommand.getNextArgRequired().split(",");
                localObject7 = new int[localObject6.length];
                j = 0;
                while (j < localObject6.length)
                {
                  localObject7[j] = Integer.decode(localObject6[j]).intValue();
                  j += 1;
                }
              }
              if (((String)localObject5).equals("--eial"))
              {
                localObject5 = paramShellCommand.getNextArgRequired();
                localObject6 = paramShellCommand.getNextArgRequired().split(",");
                localObject7 = new ArrayList(localObject6.length);
                j = 0;
                while (j < localObject6.length)
                {
                  ((ArrayList)localObject7).add(Integer.decode(localObject6[j]));
                  j += 1;
                }
              }
              if (((String)localObject5).equals("--el"))
              {
                ((Intent)localObject1).putExtra(paramShellCommand.getNextArgRequired(), Long.valueOf(paramShellCommand.getNextArgRequired()));
                continue;
              }
              if (((String)localObject5).equals("--ela"))
              {
                localObject5 = paramShellCommand.getNextArgRequired();
                localObject6 = paramShellCommand.getNextArgRequired().split(",");
                localObject7 = new long[localObject6.length];
                i = 0;
                while (i < localObject6.length)
                {
                  localObject7[i] = Long.valueOf(localObject6[i]).longValue();
                  i += 1;
                }
              }
              if (((String)localObject5).equals("--elal"))
              {
                localObject5 = paramShellCommand.getNextArgRequired();
                localObject6 = paramShellCommand.getNextArgRequired().split(",");
                localObject7 = new ArrayList(localObject6.length);
                i = 0;
                while (i < localObject6.length)
                {
                  ((ArrayList)localObject7).add(Long.valueOf(localObject6[i]));
                  i += 1;
                }
              }
              if (((String)localObject5).equals("--ef"))
              {
                ((Intent)localObject1).putExtra(paramShellCommand.getNextArgRequired(), Float.valueOf(paramShellCommand.getNextArgRequired()));
                i = 1;
                continue;
              }
              if (((String)localObject5).equals("--efa"))
              {
                localObject5 = paramShellCommand.getNextArgRequired();
                localObject6 = paramShellCommand.getNextArgRequired().split(",");
                localObject7 = new float[localObject6.length];
                i = 0;
                while (i < localObject6.length)
                {
                  localObject7[i] = Float.valueOf(localObject6[i]).floatValue();
                  i += 1;
                }
              }
              if (((String)localObject5).equals("--efal"))
              {
                localObject5 = paramShellCommand.getNextArgRequired();
                localObject6 = paramShellCommand.getNextArgRequired().split(",");
                localObject7 = new ArrayList(localObject6.length);
                i = 0;
                while (i < localObject6.length)
                {
                  ((ArrayList)localObject7).add(Float.valueOf(localObject6[i]));
                  i += 1;
                }
              }
              if (((String)localObject5).equals("--esa"))
              {
                ((Intent)localObject1).putExtra(paramShellCommand.getNextArgRequired(), paramShellCommand.getNextArgRequired().split("(?<!\\\\),"));
                i = 1;
                continue;
              }
              if (((String)localObject5).equals("--esal"))
              {
                localObject5 = paramShellCommand.getNextArgRequired();
                localObject6 = paramShellCommand.getNextArgRequired().split("(?<!\\\\),");
                localObject7 = new ArrayList(localObject6.length);
                i = 0;
                while (i < localObject6.length)
                {
                  ((ArrayList)localObject7).add(localObject6[i]);
                  i += 1;
                }
              }
              if (((String)localObject5).equals("--ez"))
              {
                localObject6 = paramShellCommand.getNextArgRequired();
                localObject5 = paramShellCommand.getNextArgRequired().toLowerCase();
                if ((!"true".equals(localObject5)) && (!"t".equals(localObject5))) {
                  break label1842;
                }
                bool = true;
              }
            }
            for (;;)
            {
              ((Intent)localObject1).putExtra((String)localObject6, bool);
              break;
              if (((String)localObject5).equals("-n"))
              {
                localObject5 = paramShellCommand.getNextArgRequired();
                localObject6 = ComponentName.unflattenFromString((String)localObject5);
                if (localObject6 == null) {
                  throw new IllegalArgumentException("Bad component name: " + (String)localObject5);
                }
              }
              else
              {
                if (((String)localObject5).equals("-p"))
                {
                  ((Intent)localObject1).setPackage(paramShellCommand.getNextArgRequired());
                  if (localObject1 != localObject2) {
                    break;
                  }
                  i = 1;
                  break;
                }
                if (((String)localObject5).equals("-f"))
                {
                  ((Intent)localObject1).setFlags(Integer.decode(paramShellCommand.getNextArgRequired()).intValue());
                  break;
                }
                if (((String)localObject5).equals("--grant-read-uri-permission"))
                {
                  ((Intent)localObject1).addFlags(1);
                  break;
                }
                if (((String)localObject5).equals("--grant-write-uri-permission"))
                {
                  ((Intent)localObject1).addFlags(2);
                  break;
                }
                if (((String)localObject5).equals("--grant-persistable-uri-permission"))
                {
                  ((Intent)localObject1).addFlags(64);
                  break;
                }
                if (((String)localObject5).equals("--grant-prefix-uri-permission"))
                {
                  ((Intent)localObject1).addFlags(128);
                  break;
                }
                if (((String)localObject5).equals("--exclude-stopped-packages"))
                {
                  ((Intent)localObject1).addFlags(16);
                  break;
                }
                if (((String)localObject5).equals("--include-stopped-packages"))
                {
                  ((Intent)localObject1).addFlags(32);
                  break;
                }
                if (((String)localObject5).equals("--debug-log-resolution"))
                {
                  ((Intent)localObject1).addFlags(8);
                  break;
                }
                if (((String)localObject5).equals("--activity-brought-to-front"))
                {
                  ((Intent)localObject1).addFlags(4194304);
                  break;
                }
                if (((String)localObject5).equals("--activity-clear-top"))
                {
                  ((Intent)localObject1).addFlags(67108864);
                  break;
                }
                if (((String)localObject5).equals("--activity-clear-when-task-reset"))
                {
                  ((Intent)localObject1).addFlags(524288);
                  break;
                }
                if (((String)localObject5).equals("--activity-exclude-from-recents"))
                {
                  ((Intent)localObject1).addFlags(8388608);
                  break;
                }
                if (((String)localObject5).equals("--activity-launched-from-history"))
                {
                  ((Intent)localObject1).addFlags(1048576);
                  break;
                }
                if (((String)localObject5).equals("--activity-multiple-task"))
                {
                  ((Intent)localObject1).addFlags(134217728);
                  break;
                }
                if (((String)localObject5).equals("--activity-no-animation"))
                {
                  ((Intent)localObject1).addFlags(65536);
                  break;
                }
                if (((String)localObject5).equals("--activity-no-history"))
                {
                  ((Intent)localObject1).addFlags(1073741824);
                  break;
                }
                if (((String)localObject5).equals("--activity-no-user-action"))
                {
                  ((Intent)localObject1).addFlags(262144);
                  break;
                }
                if (((String)localObject5).equals("--activity-previous-is-top"))
                {
                  ((Intent)localObject1).addFlags(16777216);
                  break;
                }
                if (((String)localObject5).equals("--activity-reorder-to-front"))
                {
                  ((Intent)localObject1).addFlags(131072);
                  break;
                }
                if (((String)localObject5).equals("--activity-reset-task-if-needed"))
                {
                  ((Intent)localObject1).addFlags(2097152);
                  break;
                }
                if (((String)localObject5).equals("--activity-single-top"))
                {
                  ((Intent)localObject1).addFlags(536870912);
                  break;
                }
                if (((String)localObject5).equals("--activity-clear-task"))
                {
                  ((Intent)localObject1).addFlags(32768);
                  break;
                }
                if (((String)localObject5).equals("--activity-task-on-home"))
                {
                  ((Intent)localObject1).addFlags(16384);
                  break;
                }
                if (((String)localObject5).equals("--receiver-registered-only"))
                {
                  ((Intent)localObject1).addFlags(1073741824);
                  break;
                }
                if (((String)localObject5).equals("--receiver-replace-pending"))
                {
                  ((Intent)localObject1).addFlags(536870912);
                  break;
                }
                if (((String)localObject5).equals("--receiver-foreground"))
                {
                  ((Intent)localObject1).addFlags(268435456);
                  break;
                }
                if (!((String)localObject5).equals("--selector")) {
                  break label1945;
                }
                ((Intent)localObject1).setDataAndType((Uri)localObject4, (String)localObject3);
                localObject1 = new Intent();
                break;
                ((Intent)localObject1).putExtra((String)localObject5, (Parcelable)localObject7);
                break;
                ((Intent)localObject1).putExtra((String)localObject5, (int[])localObject7);
                break;
                ((Intent)localObject1).putExtra((String)localObject5, (Serializable)localObject7);
                break;
                ((Intent)localObject1).putExtra((String)localObject5, (long[])localObject7);
                i = 1;
                break;
                ((Intent)localObject1).putExtra((String)localObject5, (Serializable)localObject7);
                i = 1;
                break;
                ((Intent)localObject1).putExtra((String)localObject5, (float[])localObject7);
                i = 1;
                break;
                ((Intent)localObject1).putExtra((String)localObject5, (Serializable)localObject7);
                i = 1;
                break;
                ((Intent)localObject1).putExtra((String)localObject5, (Serializable)localObject7);
                i = 1;
                break;
                if (("false".equals(localObject5)) || ("f".equals(localObject5)))
                {
                  bool = false;
                  continue;
                }
                try
                {
                  j = Integer.decode((String)localObject5).intValue();
                  if (j != 0) {
                    bool = true;
                  } else {
                    bool = false;
                  }
                }
                catch (NumberFormatException paramShellCommand)
                {
                  throw new IllegalArgumentException("Invalid boolean value: " + (String)localObject5);
                }
              }
            }
            ((Intent)localObject1).setComponent((ComponentName)localObject6);
            if (localObject1 == localObject2) {
              i = 1;
            }
          }
        }
      }
    } while ((paramCommandOptionHandler != null) && (paramCommandOptionHandler.handleOption((String)localObject5, paramShellCommand)));
    throw new IllegalArgumentException("Unknown option: " + (String)localObject5);
    label1990:
    ((Intent)localObject1).setDataAndType((Uri)localObject4, (String)localObject3);
    if (localObject1 != localObject2)
    {
      j = 1;
      if (j == 0) {
        break label2299;
      }
      ((Intent)localObject2).setSelector((Intent)localObject1);
    }
    for (;;)
    {
      paramCommandOptionHandler = paramShellCommand.getNextArg();
      paramShellCommand = null;
      if (paramCommandOptionHandler == null) {
        if (j != 0)
        {
          paramShellCommand = new Intent("android.intent.action.MAIN");
          paramShellCommand.addCategory("android.intent.category.LAUNCHER");
        }
      }
      for (;;)
      {
        label2053:
        if (paramShellCommand != null)
        {
          localObject1 = ((Intent)localObject2).getExtras();
          ((Intent)localObject2).replaceExtras((Bundle)null);
          paramCommandOptionHandler = paramShellCommand.getExtras();
          paramShellCommand.replaceExtras((Bundle)null);
          if ((((Intent)localObject2).getAction() != null) && (paramShellCommand.getCategories() != null))
          {
            localObject3 = new HashSet(paramShellCommand.getCategories()).iterator();
            for (;;)
            {
              if (((Iterator)localObject3).hasNext())
              {
                paramShellCommand.removeCategory((String)((Iterator)localObject3).next());
                continue;
                j = 0;
                break;
                if (paramCommandOptionHandler.indexOf(':') >= 0)
                {
                  paramShellCommand = parseUri(paramCommandOptionHandler, 7);
                  break label2053;
                }
                if (paramCommandOptionHandler.indexOf('/') >= 0)
                {
                  paramShellCommand = new Intent("android.intent.action.MAIN");
                  paramShellCommand.addCategory("android.intent.category.LAUNCHER");
                  paramShellCommand.setComponent(ComponentName.unflattenFromString(paramCommandOptionHandler));
                  break label2053;
                }
                paramShellCommand = new Intent("android.intent.action.MAIN");
                paramShellCommand.addCategory("android.intent.category.LAUNCHER");
                paramShellCommand.setPackage(paramCommandOptionHandler);
                break label2053;
              }
            }
          }
          ((Intent)localObject2).fillIn(paramShellCommand, 72);
          if (localObject1 != null) {
            break label2278;
          }
          paramShellCommand = paramCommandOptionHandler;
        }
      }
      for (;;)
      {
        ((Intent)localObject2).replaceExtras(paramShellCommand);
        i = 1;
        if (i != 0) {
          break;
        }
        throw new IllegalArgumentException("No intent supplied");
        label2278:
        paramShellCommand = (ShellCommand)localObject1;
        if (paramCommandOptionHandler != null)
        {
          paramCommandOptionHandler.putAll((Bundle)localObject1);
          paramShellCommand = paramCommandOptionHandler;
        }
      }
      return (Intent)localObject2;
      label2299:
      localObject2 = localObject1;
    }
  }
  
  public static Intent parseIntent(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet)
    throws XmlPullParserException, IOException
  {
    Intent localIntent = new Intent();
    Object localObject2 = paramResources.obtainAttributes(paramAttributeSet, R.styleable.Intent);
    localIntent.setAction(((TypedArray)localObject2).getString(2));
    Object localObject1 = ((TypedArray)localObject2).getString(3);
    String str = ((TypedArray)localObject2).getString(1);
    int i;
    if (localObject1 != null)
    {
      localObject1 = Uri.parse((String)localObject1);
      localIntent.setDataAndType((Uri)localObject1, str);
      localObject1 = ((TypedArray)localObject2).getString(0);
      str = ((TypedArray)localObject2).getString(4);
      if ((localObject1 != null) && (str != null)) {
        localIntent.setComponent(new ComponentName((String)localObject1, str));
      }
      ((TypedArray)localObject2).recycle();
      i = paramXmlPullParser.getDepth();
    }
    for (;;)
    {
      int j = paramXmlPullParser.next();
      if ((j == 1) || ((j == 3) && (paramXmlPullParser.getDepth() <= i))) {
        break label292;
      }
      if ((j != 3) && (j != 4))
      {
        localObject1 = paramXmlPullParser.getName();
        if (((String)localObject1).equals("categories"))
        {
          localObject1 = paramResources.obtainAttributes(paramAttributeSet, R.styleable.IntentCategory);
          localObject2 = ((TypedArray)localObject1).getString(0);
          ((TypedArray)localObject1).recycle();
          if (localObject2 != null) {
            localIntent.addCategory((String)localObject2);
          }
          XmlUtils.skipCurrentTag(paramXmlPullParser);
          continue;
          localObject1 = null;
          break;
        }
        if (((String)localObject1).equals("extra"))
        {
          if (localIntent.mExtras == null) {
            localIntent.mExtras = new Bundle();
          }
          paramResources.parseBundleExtra("extra", paramAttributeSet, localIntent.mExtras);
          XmlUtils.skipCurrentTag(paramXmlPullParser);
        }
        else
        {
          XmlUtils.skipCurrentTag(paramXmlPullParser);
        }
      }
    }
    label292:
    return localIntent;
  }
  
  public static Intent parseUri(String paramString, int paramInt)
    throws URISyntaxException
  {
    int i = 0;
    int j = i;
    boolean bool;
    int k;
    try
    {
      bool = paramString.startsWith("android-app:");
      if ((paramInt & 0x3) != 0)
      {
        j = i;
        if ((!paramString.startsWith("intent:")) && (!bool)) {}
      }
      else
      {
        j = i;
        k = paramString.lastIndexOf("#");
        if (k != -1) {
          break label139;
        }
        i = k;
        if (bool) {
          break label171;
        }
        j = k;
        return new Intent("android.intent.action.VIEW", Uri.parse(paramString));
      }
      j = i;
      Intent localIntent1 = new Intent("android.intent.action.VIEW");
      j = i;
      try
      {
        localIntent1.setData(Uri.parse(paramString));
        return localIntent1;
      }
      catch (IllegalArgumentException localIllegalArgumentException1)
      {
        j = i;
        throw new URISyntaxException(paramString, localIllegalArgumentException1.getMessage());
      }
      j = k;
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
    {
      throw new URISyntaxException(paramString, "illegal Intent URI format", j);
    }
    label139:
    i = k;
    label171:
    Intent localIntent2;
    int m;
    Object localObject3;
    String str;
    Object localObject1;
    label216:
    Object localObject4;
    if (!paramString.startsWith("#Intent;", k))
    {
      if (!bool)
      {
        j = k;
        return getIntentOld(paramString, paramInt);
      }
    }
    else
    {
      j = i;
      localIntent2 = new Intent("android.intent.action.VIEW");
      k = 0;
      m = 0;
      localObject3 = null;
      if (i >= 0)
      {
        j = i;
        str = paramString.substring(0, i);
        i += 8;
        localObject1 = localIntent2;
        if (i >= 0)
        {
          j = i;
          if (!paramString.startsWith("end", i)) {
            break label369;
          }
        }
        if (m == 0) {
          break label1747;
        }
        j = i;
        localObject4 = localIntent2;
        if (localIntent2.mPackage == null)
        {
          j = i;
          localIntent2.setSelector((Intent)localObject1);
          localObject4 = localIntent2;
        }
        label265:
        if (str != null)
        {
          j = i;
          if (!str.startsWith("intent:")) {
            break label1358;
          }
          j = i;
          str = str.substring(7);
          localObject1 = str;
          if (localObject3 != null)
          {
            j = i;
            localObject1 = (String)localObject3 + ':' + str;
          }
        }
      }
    }
    for (;;)
    {
      j = i;
      paramInt = ((String)localObject1).length();
      if (paramInt > 0) {
        j = i;
      }
      try
      {
        ((Intent)localObject4).mData = Uri.parse((String)localObject1);
        return (Intent)localObject4;
      }
      catch (IllegalArgumentException localIllegalArgumentException2)
      {
        Bundle localBundle;
        j = i;
        throw new URISyntaxException(paramString, localIllegalArgumentException2.getMessage());
      }
      str = paramString;
      localObject1 = localIntent2;
      break label216;
      label369:
      j = i;
      int i1 = paramString.indexOf('=', i);
      int n = i1;
      if (i1 < 0) {
        n = i - 1;
      }
      j = i;
      i1 = paramString.indexOf(';', i);
      label429:
      Object localObject5;
      if (n < i1)
      {
        j = i;
        localObject4 = Uri.decode(paramString.substring(n + 1, i1));
        j = i;
        if (paramString.startsWith("action=", i))
        {
          j = i;
          ((Intent)localObject1).setAction((String)localObject4);
          j = k;
          n = m;
          localObject4 = localObject1;
          localObject5 = localObject3;
          if (m == 0)
          {
            j = 1;
            n = m;
            localObject4 = localObject1;
            localObject5 = localObject3;
          }
        }
        else
        {
          j = i;
          if (paramString.startsWith("category=", i))
          {
            j = i;
            ((Intent)localObject1).addCategory((String)localObject4);
            j = k;
            n = m;
            localObject4 = localObject1;
            localObject5 = localObject3;
          }
          else
          {
            j = i;
            if (paramString.startsWith("type=", i))
            {
              j = i;
              ((Intent)localObject1).mType = ((String)localObject4);
              j = k;
              n = m;
              localObject4 = localObject1;
              localObject5 = localObject3;
            }
            else
            {
              j = i;
              if (paramString.startsWith("launchFlags=", i))
              {
                j = i;
                ((Intent)localObject1).mFlags = Integer.decode((String)localObject4).intValue();
                j = k;
                n = m;
                localObject4 = localObject1;
                localObject5 = localObject3;
                if ((paramInt & 0x4) == 0)
                {
                  j = i;
                  ((Intent)localObject1).mFlags &= 0xFF3C;
                  j = k;
                  n = m;
                  localObject4 = localObject1;
                  localObject5 = localObject3;
                }
              }
              else
              {
                j = i;
                if (paramString.startsWith("package=", i))
                {
                  j = i;
                  ((Intent)localObject1).mPackage = ((String)localObject4);
                  j = k;
                  n = m;
                  localObject4 = localObject1;
                  localObject5 = localObject3;
                }
                else
                {
                  j = i;
                  if (paramString.startsWith("component=", i))
                  {
                    j = i;
                    ((Intent)localObject1).mComponent = ComponentName.unflattenFromString((String)localObject4);
                    j = k;
                    n = m;
                    localObject4 = localObject1;
                    localObject5 = localObject3;
                  }
                  else
                  {
                    j = i;
                    if (paramString.startsWith("scheme=", i))
                    {
                      if (m == 0) {
                        break label1790;
                      }
                      j = i;
                      ((Intent)localObject1).mData = Uri.parse((String)localObject4 + ":");
                      j = k;
                      n = m;
                      localObject4 = localObject1;
                      localObject5 = localObject3;
                    }
                    else
                    {
                      j = i;
                      if (paramString.startsWith("sourceBounds=", i))
                      {
                        j = i;
                        ((Intent)localObject1).mSourceBounds = Rect.unflattenFromString((String)localObject4);
                        j = k;
                        n = m;
                        localObject4 = localObject1;
                        localObject5 = localObject3;
                      }
                      else
                      {
                        if (i1 == i + 3)
                        {
                          j = i;
                          if (paramString.startsWith("SEL", i))
                          {
                            j = i;
                            localObject4 = new Intent();
                            n = 1;
                            j = k;
                            localObject5 = localObject3;
                            break label1759;
                          }
                        }
                        j = i;
                        localObject5 = Uri.decode(paramString.substring(i + 2, n));
                        j = i;
                        if (((Intent)localObject1).mExtras == null)
                        {
                          j = i;
                          ((Intent)localObject1).mExtras = new Bundle();
                        }
                        j = i;
                        localBundle = ((Intent)localObject1).mExtras;
                        j = i;
                        if (paramString.startsWith("S.", i))
                        {
                          j = i;
                          localBundle.putString((String)localObject5, (String)localObject4);
                          j = k;
                          n = m;
                          localObject4 = localObject1;
                          localObject5 = localObject3;
                        }
                        else
                        {
                          j = i;
                          if (paramString.startsWith("B.", i))
                          {
                            j = i;
                            localBundle.putBoolean((String)localObject5, Boolean.parseBoolean((String)localObject4));
                            j = k;
                            n = m;
                            localObject4 = localObject1;
                            localObject5 = localObject3;
                          }
                          else
                          {
                            j = i;
                            if (paramString.startsWith("b.", i))
                            {
                              j = i;
                              localBundle.putByte((String)localObject5, Byte.parseByte((String)localObject4));
                              j = k;
                              n = m;
                              localObject4 = localObject1;
                              localObject5 = localObject3;
                            }
                            else
                            {
                              j = i;
                              if (paramString.startsWith("c.", i))
                              {
                                j = i;
                                localBundle.putChar((String)localObject5, ((String)localObject4).charAt(0));
                                j = k;
                                n = m;
                                localObject4 = localObject1;
                                localObject5 = localObject3;
                              }
                              else
                              {
                                j = i;
                                if (paramString.startsWith("d.", i))
                                {
                                  j = i;
                                  localBundle.putDouble((String)localObject5, Double.parseDouble((String)localObject4));
                                  j = k;
                                  n = m;
                                  localObject4 = localObject1;
                                  localObject5 = localObject3;
                                }
                                else
                                {
                                  j = i;
                                  if (paramString.startsWith("f.", i))
                                  {
                                    j = i;
                                    localBundle.putFloat((String)localObject5, Float.parseFloat((String)localObject4));
                                    j = k;
                                    n = m;
                                    localObject4 = localObject1;
                                    localObject5 = localObject3;
                                  }
                                  else
                                  {
                                    j = i;
                                    if (paramString.startsWith("i.", i))
                                    {
                                      j = i;
                                      localBundle.putInt((String)localObject5, Integer.parseInt((String)localObject4));
                                      j = k;
                                      n = m;
                                      localObject4 = localObject1;
                                      localObject5 = localObject3;
                                    }
                                    else
                                    {
                                      j = i;
                                      if (paramString.startsWith("l.", i))
                                      {
                                        j = i;
                                        localBundle.putLong((String)localObject5, Long.parseLong((String)localObject4));
                                        j = k;
                                        n = m;
                                        localObject4 = localObject1;
                                        localObject5 = localObject3;
                                      }
                                      else
                                      {
                                        j = i;
                                        if (paramString.startsWith("s.", i))
                                        {
                                          j = i;
                                          localBundle.putShort((String)localObject5, Short.parseShort((String)localObject4));
                                          j = k;
                                          n = m;
                                          localObject4 = localObject1;
                                          localObject5 = localObject3;
                                        }
                                        else
                                        {
                                          j = i;
                                          throw new URISyntaxException(paramString, "unknown EXTRA type", i);
                                          label1358:
                                          j = i;
                                          localObject1 = str;
                                          if (!str.startsWith("android-app:")) {
                                            continue;
                                          }
                                          j = i;
                                          if (str.charAt(12) != '/') {
                                            break label1824;
                                          }
                                          j = i;
                                          if (str.charAt(13) != '/') {
                                            break label1824;
                                          }
                                          j = i;
                                          m = str.indexOf('/', 14);
                                          if (m < 0)
                                          {
                                            j = i;
                                            ((Intent)localObject4).mPackage = str.substring(14);
                                            if (k != 0) {
                                              break label1808;
                                            }
                                            j = i;
                                            ((Intent)localObject4).setAction("android.intent.action.MAIN");
                                            break label1808;
                                          }
                                          localIntent2 = null;
                                          j = i;
                                          ((Intent)localObject4).mPackage = str.substring(14, m);
                                          j = i;
                                          localObject1 = localIntent2;
                                          paramInt = m;
                                          if (m + 1 < str.length())
                                          {
                                            j = i;
                                            n = str.indexOf('/', m + 1);
                                            if (n < 0) {
                                              break label1631;
                                            }
                                            j = i;
                                            localObject5 = str.substring(m + 1, n);
                                            m = n;
                                            j = i;
                                            localObject1 = localIntent2;
                                            paramInt = m;
                                            localObject3 = localObject5;
                                            if (n < str.length())
                                            {
                                              j = i;
                                              i1 = str.indexOf('/', n + 1);
                                              localObject1 = localIntent2;
                                              paramInt = m;
                                              localObject3 = localObject5;
                                              if (i1 >= 0)
                                              {
                                                j = i;
                                                localObject1 = str.substring(n + 1, i1);
                                                paramInt = i1;
                                                localObject3 = localObject5;
                                              }
                                            }
                                          }
                                          while (localObject3 == null)
                                          {
                                            if (k != 0) {
                                              break label1816;
                                            }
                                            j = i;
                                            ((Intent)localObject4).setAction("android.intent.action.MAIN");
                                            break label1816;
                                            label1631:
                                            j = i;
                                            localObject3 = str.substring(m + 1);
                                            localObject1 = localIntent2;
                                            paramInt = m;
                                          }
                                          if (localObject1 == null)
                                          {
                                            j = i;
                                            localObject1 = (String)localObject3 + ":";
                                            continue;
                                          }
                                          j = i;
                                          localObject1 = (String)localObject3 + "://" + (String)localObject1 + str.substring(paramInt);
                                          continue;
                                          label1747:
                                          localObject4 = localIllegalArgumentException2;
                                          break label265;
                                          i = -1;
                                          break;
                                        }
                                      }
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
      for (;;)
      {
        label1759:
        i = i1 + 1;
        k = j;
        m = n;
        localObject2 = localObject4;
        localObject3 = localObject5;
        break;
        localObject4 = "";
        break label429;
        label1790:
        localObject5 = localObject4;
        j = k;
        n = m;
        localObject4 = localObject2;
      }
      label1808:
      Object localObject2 = "";
      continue;
      label1816:
      localObject2 = "";
      continue;
      label1824:
      localObject2 = "";
    }
  }
  
  public static void printIntentArgsHelp(PrintWriter paramPrintWriter, String paramString)
  {
    int i = 0;
    String[] arrayOfString = new String[46];
    arrayOfString[0] = "<INTENT> specifications include these flags and arguments:";
    arrayOfString[1] = "    [-a <ACTION>] [-d <DATA_URI>] [-t <MIME_TYPE>]";
    arrayOfString[2] = "    [-c <CATEGORY> [-c <CATEGORY>] ...]";
    arrayOfString[3] = "    [-e|--es <EXTRA_KEY> <EXTRA_STRING_VALUE> ...]";
    arrayOfString[4] = "    [--esn <EXTRA_KEY> ...]";
    arrayOfString[5] = "    [--ez <EXTRA_KEY> <EXTRA_BOOLEAN_VALUE> ...]";
    arrayOfString[6] = "    [--ei <EXTRA_KEY> <EXTRA_INT_VALUE> ...]";
    arrayOfString[7] = "    [--el <EXTRA_KEY> <EXTRA_LONG_VALUE> ...]";
    arrayOfString[8] = "    [--ef <EXTRA_KEY> <EXTRA_FLOAT_VALUE> ...]";
    arrayOfString[9] = "    [--eu <EXTRA_KEY> <EXTRA_URI_VALUE> ...]";
    arrayOfString[10] = "    [--ecn <EXTRA_KEY> <EXTRA_COMPONENT_NAME_VALUE>]";
    arrayOfString[11] = "    [--eia <EXTRA_KEY> <EXTRA_INT_VALUE>[,<EXTRA_INT_VALUE...]]";
    arrayOfString[12] = "        (mutiple extras passed as Integer[])";
    arrayOfString[13] = "    [--eial <EXTRA_KEY> <EXTRA_INT_VALUE>[,<EXTRA_INT_VALUE...]]";
    arrayOfString[14] = "        (mutiple extras passed as List<Integer>)";
    arrayOfString[15] = "    [--ela <EXTRA_KEY> <EXTRA_LONG_VALUE>[,<EXTRA_LONG_VALUE...]]";
    arrayOfString[16] = "        (mutiple extras passed as Long[])";
    arrayOfString[17] = "    [--elal <EXTRA_KEY> <EXTRA_LONG_VALUE>[,<EXTRA_LONG_VALUE...]]";
    arrayOfString[18] = "        (mutiple extras passed as List<Long>)";
    arrayOfString[19] = "    [--efa <EXTRA_KEY> <EXTRA_FLOAT_VALUE>[,<EXTRA_FLOAT_VALUE...]]";
    arrayOfString[20] = "        (mutiple extras passed as Float[])";
    arrayOfString[21] = "    [--efal <EXTRA_KEY> <EXTRA_FLOAT_VALUE>[,<EXTRA_FLOAT_VALUE...]]";
    arrayOfString[22] = "        (mutiple extras passed as List<Float>)";
    arrayOfString[23] = "    [--esa <EXTRA_KEY> <EXTRA_STRING_VALUE>[,<EXTRA_STRING_VALUE...]]";
    arrayOfString[24] = "        (mutiple extras passed as String[]; to embed a comma into a string,";
    arrayOfString[25] = "         escape it using \"\\,\")";
    arrayOfString[26] = "    [--esal <EXTRA_KEY> <EXTRA_STRING_VALUE>[,<EXTRA_STRING_VALUE...]]";
    arrayOfString[27] = "        (mutiple extras passed as List<String>; to embed a comma into a string,";
    arrayOfString[28] = "         escape it using \"\\,\")";
    arrayOfString[29] = "    [--f <FLAG>]";
    arrayOfString[30] = "    [--grant-read-uri-permission] [--grant-write-uri-permission]";
    arrayOfString[31] = "    [--grant-persistable-uri-permission] [--grant-prefix-uri-permission]";
    arrayOfString[32] = "    [--debug-log-resolution] [--exclude-stopped-packages]";
    arrayOfString[33] = "    [--include-stopped-packages]";
    arrayOfString[34] = "    [--activity-brought-to-front] [--activity-clear-top]";
    arrayOfString[35] = "    [--activity-clear-when-task-reset] [--activity-exclude-from-recents]";
    arrayOfString[36] = "    [--activity-launched-from-history] [--activity-multiple-task]";
    arrayOfString[37] = "    [--activity-no-animation] [--activity-no-history]";
    arrayOfString[38] = "    [--activity-no-user-action] [--activity-previous-is-top]";
    arrayOfString[39] = "    [--activity-reorder-to-front] [--activity-reset-task-if-needed]";
    arrayOfString[40] = "    [--activity-single-top] [--activity-clear-task]";
    arrayOfString[41] = "    [--activity-task-on-home]";
    arrayOfString[42] = "    [--receiver-registered-only] [--receiver-replace-pending]";
    arrayOfString[43] = "    [--receiver-foreground]";
    arrayOfString[44] = "    [--selector]";
    arrayOfString[45] = "    [<URI> | <PACKAGE> | <COMPONENT>]";
    int j = arrayOfString.length;
    while (i < j)
    {
      String str = arrayOfString[i];
      paramPrintWriter.print(paramString);
      paramPrintWriter.println(str);
      i += 1;
    }
  }
  
  public static Intent restoreFromXml(XmlPullParser paramXmlPullParser)
    throws IOException, XmlPullParserException
  {
    Intent localIntent = new Intent();
    int j = paramXmlPullParser.getDepth();
    int i = paramXmlPullParser.getAttributeCount() - 1;
    String str1;
    if (i >= 0)
    {
      str1 = paramXmlPullParser.getAttributeName(i);
      String str2 = paramXmlPullParser.getAttributeValue(i);
      if ("action".equals(str1)) {
        localIntent.setAction(str2);
      }
      for (;;)
      {
        i -= 1;
        break;
        if ("data".equals(str1)) {
          localIntent.setData(Uri.parse(str2));
        } else if ("type".equals(str1)) {
          localIntent.setType(str2);
        } else if ("component".equals(str1)) {
          localIntent.setComponent(ComponentName.unflattenFromString(str2));
        } else if ("flags".equals(str1)) {
          localIntent.setFlags(Integer.parseInt(str2, 16));
        } else {
          Log.e("Intent", "restoreFromXml: unknown attribute=" + str1);
        }
      }
      Log.w("Intent", "restoreFromXml: unknown name=" + str1);
      XmlUtils.skipCurrentTag(paramXmlPullParser);
    }
    for (;;)
    {
      i = paramXmlPullParser.next();
      if ((i == 1) || ((i == 3) && (paramXmlPullParser.getDepth() >= j))) {
        return localIntent;
      }
      if (i == 2)
      {
        str1 = paramXmlPullParser.getName();
        if (!"categories".equals(str1)) {
          break;
        }
        i = paramXmlPullParser.getAttributeCount() - 1;
        while (i >= 0)
        {
          localIntent.addCategory(paramXmlPullParser.getAttributeValue(i));
          i -= 1;
        }
      }
    }
    return localIntent;
  }
  
  private void toUriFragment(StringBuilder paramStringBuilder, String paramString1, String paramString2, String paramString3, int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    toUriInner(localStringBuilder, paramString1, paramString2, paramString3, paramInt);
    if (this.mSelector != null)
    {
      localStringBuilder.append("SEL;");
      paramString2 = this.mSelector;
      if (this.mSelector.mData == null) {
        break label109;
      }
    }
    label109:
    for (paramString1 = this.mSelector.mData.getScheme();; paramString1 = null)
    {
      paramString2.toUriInner(localStringBuilder, paramString1, null, null, paramInt);
      if (localStringBuilder.length() > 0)
      {
        paramStringBuilder.append("#Intent;");
        paramStringBuilder.append(localStringBuilder);
        paramStringBuilder.append("end");
      }
      return;
    }
  }
  
  private void toUriInner(StringBuilder paramStringBuilder, String paramString1, String paramString2, String paramString3, int paramInt)
  {
    if (paramString1 != null) {
      paramStringBuilder.append("scheme=").append(paramString1).append(';');
    }
    if ((this.mAction == null) || (this.mAction.equals(paramString2))) {}
    while (this.mCategories != null)
    {
      paramInt = 0;
      while (paramInt < this.mCategories.size())
      {
        paramStringBuilder.append("category=").append(Uri.encode((String)this.mCategories.valueAt(paramInt))).append(';');
        paramInt += 1;
      }
      paramStringBuilder.append("action=").append(Uri.encode(this.mAction)).append(';');
    }
    if (this.mType != null) {
      paramStringBuilder.append("type=").append(Uri.encode(this.mType, "/")).append(';');
    }
    if (this.mFlags != 0) {
      paramStringBuilder.append("launchFlags=0x").append(Integer.toHexString(this.mFlags)).append(';');
    }
    if ((this.mPackage == null) || (this.mPackage.equals(paramString3)))
    {
      if (this.mComponent != null) {
        paramStringBuilder.append("component=").append(Uri.encode(this.mComponent.flattenToShortString(), "/")).append(';');
      }
      if (this.mSourceBounds != null) {
        paramStringBuilder.append("sourceBounds=").append(Uri.encode(this.mSourceBounds.flattenToString())).append(';');
      }
      if (this.mExtras != null) {
        paramString1 = this.mExtras.keySet().iterator();
      }
    }
    else
    {
      label298:
      label547:
      for (;;)
      {
        if (!paramString1.hasNext()) {
          return;
        }
        paramString2 = (String)paramString1.next();
        paramString3 = this.mExtras.get(paramString2);
        char c;
        if ((paramString3 instanceof String)) {
          c = 'S';
        }
        for (;;)
        {
          if (c == 0) {
            break label547;
          }
          paramStringBuilder.append(c);
          paramStringBuilder.append('.');
          paramStringBuilder.append(Uri.encode(paramString2));
          paramStringBuilder.append('=');
          paramStringBuilder.append(Uri.encode(paramString3.toString()));
          paramStringBuilder.append(';');
          break label298;
          paramStringBuilder.append("package=").append(Uri.encode(this.mPackage)).append(';');
          break;
          if ((paramString3 instanceof Boolean)) {
            c = 'B';
          } else if ((paramString3 instanceof Byte)) {
            c = 'b';
          } else if ((paramString3 instanceof Character)) {
            c = 'c';
          } else if ((paramString3 instanceof Double)) {
            c = 'd';
          } else if ((paramString3 instanceof Float)) {
            c = 'f';
          } else if ((paramString3 instanceof Integer)) {
            c = 'i';
          } else if ((paramString3 instanceof Long)) {
            c = 'l';
          } else if ((paramString3 instanceof Short)) {
            c = 's';
          } else {
            c = '\000';
          }
        }
      }
    }
  }
  
  public Intent addCategory(String paramString)
  {
    if (this.mCategories == null) {
      this.mCategories = new ArraySet();
    }
    this.mCategories.add(paramString.intern());
    return this;
  }
  
  public Intent addFlags(int paramInt)
  {
    this.mFlags |= paramInt;
    return this;
  }
  
  public Object clone()
  {
    return new Intent(this);
  }
  
  public Intent cloneFilter()
  {
    return new Intent(this, false);
  }
  
  public int describeContents()
  {
    if (this.mExtras != null) {
      return this.mExtras.describeContents();
    }
    return 0;
  }
  
  public int fillIn(Intent paramIntent, int paramInt)
  {
    int i = 0;
    int m = 0;
    int j = i;
    if (paramIntent.mAction != null) {
      if (this.mAction != null)
      {
        j = i;
        if ((paramInt & 0x1) == 0) {}
      }
      else
      {
        this.mAction = paramIntent.mAction;
        j = 1;
      }
    }
    if (paramIntent.mData == null)
    {
      k = j;
      i = m;
      if (paramIntent.mType == null) {}
    }
    else
    {
      if ((this.mData != null) || (this.mType != null)) {
        break label489;
      }
      this.mData = paramIntent.mData;
      this.mType = paramIntent.mType;
      k = j | 0x2;
      i = 1;
    }
    label101:
    j = k;
    if (paramIntent.mCategories != null) {
      if (this.mCategories != null)
      {
        j = k;
        if ((paramInt & 0x4) == 0) {}
      }
      else
      {
        if (paramIntent.mCategories != null) {
          this.mCategories = new ArraySet(paramIntent.mCategories);
        }
        j = k | 0x4;
      }
    }
    m = j;
    if (paramIntent.mPackage != null) {
      if (this.mPackage != null)
      {
        m = j;
        if ((paramInt & 0x10) == 0) {}
      }
      else
      {
        m = j;
        if (this.mSelector == null)
        {
          this.mPackage = paramIntent.mPackage;
          m = j | 0x10;
        }
      }
    }
    int k = m;
    if (paramIntent.mSelector != null)
    {
      k = m;
      if ((paramInt & 0x40) != 0)
      {
        k = m;
        if (this.mPackage == null)
        {
          this.mSelector = new Intent(paramIntent.mSelector);
          this.mPackage = null;
          k = m | 0x40;
        }
      }
    }
    m = k;
    j = i;
    if (paramIntent.mClipData != null) {
      if (this.mClipData != null)
      {
        m = k;
        j = i;
        if ((paramInt & 0x80) == 0) {}
      }
      else
      {
        this.mClipData = paramIntent.mClipData;
        m = k | 0x80;
        j = 1;
      }
    }
    i = m;
    if (paramIntent.mComponent != null)
    {
      i = m;
      if ((paramInt & 0x8) != 0)
      {
        this.mComponent = paramIntent.mComponent;
        i = m | 0x8;
      }
    }
    this.mFlags |= paramIntent.mFlags;
    k = i;
    if (paramIntent.mSourceBounds != null) {
      if (this.mSourceBounds != null)
      {
        k = i;
        if ((paramInt & 0x20) == 0) {}
      }
      else
      {
        this.mSourceBounds = new Rect(paramIntent.mSourceBounds);
        k = i | 0x20;
      }
    }
    if (this.mExtras == null)
    {
      paramInt = j;
      if (paramIntent.mExtras != null)
      {
        this.mExtras = new Bundle(paramIntent.mExtras);
        paramInt = 1;
      }
    }
    for (;;)
    {
      if ((paramInt != 0) && (this.mContentUserHint == -2) && (paramIntent.mContentUserHint != -2)) {
        this.mContentUserHint = paramIntent.mContentUserHint;
      }
      return k;
      label489:
      k = j;
      i = m;
      if ((paramInt & 0x2) == 0) {
        break label101;
      }
      break;
      paramInt = j;
      if (paramIntent.mExtras != null) {
        try
        {
          Bundle localBundle = new Bundle(paramIntent.mExtras);
          localBundle.putAll(this.mExtras);
          this.mExtras = localBundle;
          paramInt = 1;
        }
        catch (RuntimeException localRuntimeException)
        {
          Log.w("Intent", "Failure filling in extras", localRuntimeException);
          paramInt = j;
        }
      }
    }
  }
  
  public boolean filterEquals(Intent paramIntent)
  {
    if (paramIntent == null) {
      return false;
    }
    if (!Objects.equals(this.mAction, paramIntent.mAction)) {
      return false;
    }
    if (!Objects.equals(this.mData, paramIntent.mData)) {
      return false;
    }
    if (!Objects.equals(this.mType, paramIntent.mType)) {
      return false;
    }
    if (!Objects.equals(this.mPackage, paramIntent.mPackage)) {
      return false;
    }
    if (!Objects.equals(this.mComponent, paramIntent.mComponent)) {
      return false;
    }
    return Objects.equals(this.mCategories, paramIntent.mCategories);
  }
  
  public int filterHashCode()
  {
    int j = 0;
    if (this.mAction != null) {
      j = this.mAction.hashCode() + 0;
    }
    int i = j;
    if (this.mData != null) {
      i = j + this.mData.hashCode();
    }
    j = i;
    if (this.mType != null) {
      j = i + this.mType.hashCode();
    }
    i = j;
    if (this.mPackage != null) {
      i = j + this.mPackage.hashCode();
    }
    j = i;
    if (this.mComponent != null) {
      j = i + this.mComponent.hashCode();
    }
    i = j;
    if (this.mCategories != null) {
      i = j + this.mCategories.hashCode();
    }
    return i;
  }
  
  public void fixUris(int paramInt)
  {
    Object localObject = getData();
    if (localObject != null) {
      this.mData = ContentProvider.maybeAddUserId((Uri)localObject, paramInt);
    }
    if (this.mClipData != null) {
      this.mClipData.fixUris(paramInt);
    }
    localObject = getAction();
    if ("android.intent.action.SEND".equals(localObject))
    {
      localObject = (Uri)getParcelableExtra("android.intent.extra.STREAM");
      if (localObject != null) {
        putExtra("android.intent.extra.STREAM", ContentProvider.maybeAddUserId((Uri)localObject, paramInt));
      }
    }
    do
    {
      do
      {
        do
        {
          return;
          if (!"android.intent.action.SEND_MULTIPLE".equals(localObject)) {
            break;
          }
          localObject = getParcelableArrayListExtra("android.intent.extra.STREAM");
        } while (localObject == null);
        ArrayList localArrayList = new ArrayList();
        int i = 0;
        while (i < ((ArrayList)localObject).size())
        {
          localArrayList.add(ContentProvider.maybeAddUserId((Uri)((ArrayList)localObject).get(i), paramInt));
          i += 1;
        }
        putParcelableArrayListExtra("android.intent.extra.STREAM", localArrayList);
        return;
      } while ((!"android.media.action.IMAGE_CAPTURE".equals(localObject)) && (!"android.media.action.IMAGE_CAPTURE_SECURE".equals(localObject)) && (!"android.media.action.VIDEO_CAPTURE".equals(localObject)));
      localObject = (Uri)getParcelableExtra("output");
    } while (localObject == null);
    putExtra("output", ContentProvider.maybeAddUserId((Uri)localObject, paramInt));
  }
  
  public String getAction()
  {
    return this.mAction;
  }
  
  public boolean[] getBooleanArrayExtra(String paramString)
  {
    if (this.mExtras == null) {
      return null;
    }
    return this.mExtras.getBooleanArray(paramString);
  }
  
  public boolean getBooleanExtra(String paramString, boolean paramBoolean)
  {
    if (this.mExtras == null) {
      return paramBoolean;
    }
    return this.mExtras.getBoolean(paramString, paramBoolean);
  }
  
  public Bundle getBundleExtra(String paramString)
  {
    if (this.mExtras == null) {
      return null;
    }
    return this.mExtras.getBundle(paramString);
  }
  
  public byte[] getByteArrayExtra(String paramString)
  {
    if (this.mExtras == null) {
      return null;
    }
    return this.mExtras.getByteArray(paramString);
  }
  
  public byte getByteExtra(String paramString, byte paramByte)
  {
    if (this.mExtras == null) {
      return paramByte;
    }
    return this.mExtras.getByte(paramString, paramByte).byteValue();
  }
  
  public Set<String> getCategories()
  {
    return this.mCategories;
  }
  
  public char[] getCharArrayExtra(String paramString)
  {
    if (this.mExtras == null) {
      return null;
    }
    return this.mExtras.getCharArray(paramString);
  }
  
  public char getCharExtra(String paramString, char paramChar)
  {
    if (this.mExtras == null) {
      return paramChar;
    }
    return this.mExtras.getChar(paramString, paramChar);
  }
  
  public CharSequence[] getCharSequenceArrayExtra(String paramString)
  {
    if (this.mExtras == null) {
      return null;
    }
    return this.mExtras.getCharSequenceArray(paramString);
  }
  
  public ArrayList<CharSequence> getCharSequenceArrayListExtra(String paramString)
  {
    if (this.mExtras == null) {
      return null;
    }
    return this.mExtras.getCharSequenceArrayList(paramString);
  }
  
  public CharSequence getCharSequenceExtra(String paramString)
  {
    if (this.mExtras == null) {
      return null;
    }
    return this.mExtras.getCharSequence(paramString);
  }
  
  public ClipData getClipData()
  {
    return this.mClipData;
  }
  
  public ComponentName getComponent()
  {
    return this.mComponent;
  }
  
  public int getContentUserHint()
  {
    return this.mContentUserHint;
  }
  
  public Uri getData()
  {
    return this.mData;
  }
  
  public String getDataString()
  {
    String str = null;
    if (this.mData != null) {
      str = this.mData.toString();
    }
    return str;
  }
  
  public double[] getDoubleArrayExtra(String paramString)
  {
    if (this.mExtras == null) {
      return null;
    }
    return this.mExtras.getDoubleArray(paramString);
  }
  
  public double getDoubleExtra(String paramString, double paramDouble)
  {
    if (this.mExtras == null) {
      return paramDouble;
    }
    return this.mExtras.getDouble(paramString, paramDouble);
  }
  
  @Deprecated
  public Object getExtra(String paramString)
  {
    return getExtra(paramString, null);
  }
  
  @Deprecated
  public Object getExtra(String paramString, Object paramObject)
  {
    Object localObject = paramObject;
    if (this.mExtras != null)
    {
      paramString = this.mExtras.get(paramString);
      localObject = paramObject;
      if (paramString != null) {
        localObject = paramString;
      }
    }
    return localObject;
  }
  
  public Bundle getExtras()
  {
    Bundle localBundle = null;
    if (this.mExtras != null) {
      localBundle = new Bundle(this.mExtras);
    }
    return localBundle;
  }
  
  public int getFlags()
  {
    return this.mFlags;
  }
  
  public float[] getFloatArrayExtra(String paramString)
  {
    if (this.mExtras == null) {
      return null;
    }
    return this.mExtras.getFloatArray(paramString);
  }
  
  public float getFloatExtra(String paramString, float paramFloat)
  {
    if (this.mExtras == null) {
      return paramFloat;
    }
    return this.mExtras.getFloat(paramString, paramFloat);
  }
  
  @Deprecated
  public IBinder getIBinderExtra(String paramString)
  {
    if (this.mExtras == null) {
      return null;
    }
    return this.mExtras.getIBinder(paramString);
  }
  
  public int[] getIntArrayExtra(String paramString)
  {
    if (this.mExtras == null) {
      return null;
    }
    return this.mExtras.getIntArray(paramString);
  }
  
  public int getIntExtra(String paramString, int paramInt)
  {
    if (this.mExtras == null) {
      return paramInt;
    }
    return this.mExtras.getInt(paramString, paramInt);
  }
  
  public ArrayList<Integer> getIntegerArrayListExtra(String paramString)
  {
    if (this.mExtras == null) {
      return null;
    }
    return this.mExtras.getIntegerArrayList(paramString);
  }
  
  public long[] getLongArrayExtra(String paramString)
  {
    if (this.mExtras == null) {
      return null;
    }
    return this.mExtras.getLongArray(paramString);
  }
  
  public long getLongExtra(String paramString, long paramLong)
  {
    if (this.mExtras == null) {
      return paramLong;
    }
    return this.mExtras.getLong(paramString, paramLong);
  }
  
  public String getPackage()
  {
    return this.mPackage;
  }
  
  public Parcelable[] getParcelableArrayExtra(String paramString)
  {
    if (this.mExtras == null) {
      return null;
    }
    return this.mExtras.getParcelableArray(paramString);
  }
  
  public <T extends Parcelable> ArrayList<T> getParcelableArrayListExtra(String paramString)
  {
    if (this.mExtras == null) {
      return null;
    }
    return this.mExtras.getParcelableArrayList(paramString);
  }
  
  public <T extends Parcelable> T getParcelableExtra(String paramString)
  {
    if (this.mExtras == null) {
      return null;
    }
    return this.mExtras.getParcelable(paramString);
  }
  
  public String getScheme()
  {
    String str = null;
    if (this.mData != null) {
      str = this.mData.getScheme();
    }
    return str;
  }
  
  public Intent getSelector()
  {
    return this.mSelector;
  }
  
  public Serializable getSerializableExtra(String paramString)
  {
    if (this.mExtras == null) {
      return null;
    }
    return this.mExtras.getSerializable(paramString);
  }
  
  public short[] getShortArrayExtra(String paramString)
  {
    if (this.mExtras == null) {
      return null;
    }
    return this.mExtras.getShortArray(paramString);
  }
  
  public short getShortExtra(String paramString, short paramShort)
  {
    if (this.mExtras == null) {
      return paramShort;
    }
    return this.mExtras.getShort(paramString, paramShort);
  }
  
  public Rect getSourceBounds()
  {
    return this.mSourceBounds;
  }
  
  public String[] getStringArrayExtra(String paramString)
  {
    if (this.mExtras == null) {
      return null;
    }
    return this.mExtras.getStringArray(paramString);
  }
  
  public ArrayList<String> getStringArrayListExtra(String paramString)
  {
    if (this.mExtras == null) {
      return null;
    }
    return this.mExtras.getStringArrayList(paramString);
  }
  
  public String getStringExtra(String paramString)
  {
    if (this.mExtras == null) {
      return null;
    }
    return this.mExtras.getString(paramString);
  }
  
  public String getType()
  {
    return this.mType;
  }
  
  public boolean hasCategory(String paramString)
  {
    if (this.mCategories != null) {
      return this.mCategories.contains(paramString);
    }
    return false;
  }
  
  public boolean hasExtra(String paramString)
  {
    if (this.mExtras != null) {
      return this.mExtras.containsKey(paramString);
    }
    return false;
  }
  
  public boolean hasFileDescriptors()
  {
    if (this.mExtras != null) {
      return this.mExtras.hasFileDescriptors();
    }
    return false;
  }
  
  public boolean isDocument()
  {
    return (this.mFlags & 0x80000) == 524288;
  }
  
  public boolean isExcludingStopped()
  {
    return (this.mFlags & 0x30) == 16;
  }
  
  public boolean migrateExtraStreamToClipData()
  {
    if ((this.mExtras != null) && (this.mExtras.isParcelled())) {
      return false;
    }
    if (getClipData() != null) {
      return false;
    }
    Object localObject1 = getAction();
    if ("android.intent.action.CHOOSER".equals(localObject1)) {
      bool2 = false;
    }
    try
    {
      localObject1 = (Intent)getParcelableExtra("android.intent.extra.INTENT");
      bool1 = bool2;
      if (localObject1 != null) {
        bool1 = ((Intent)localObject1).migrateExtraStreamToClipData();
      }
    }
    catch (ClassCastException localClassCastException5)
    {
      for (;;)
      {
        int i;
        Object localObject3;
        boolean bool1 = bool2;
      }
    }
    bool2 = bool1;
    try
    {
      localObject1 = getParcelableArrayExtra("android.intent.extra.INITIAL_INTENTS");
      boolean bool3 = bool1;
      if (localObject1 != null)
      {
        i = 0;
        for (;;)
        {
          bool2 = bool1;
          bool3 = bool1;
          if (i >= localObject1.length) {
            break;
          }
          bool2 = bool1;
          localObject3 = (Intent)localObject1[i];
          bool2 = bool1;
          if (localObject3 != null)
          {
            bool2 = bool1;
            bool3 = ((Intent)localObject3).migrateExtraStreamToClipData();
            bool2 = bool1 | bool3;
          }
          i += 1;
          bool1 = bool2;
        }
      }
      return bool3;
    }
    catch (ClassCastException localClassCastException1)
    {
      bool3 = bool2;
    }
    if ("android.intent.action.SEND".equals(localClassCastException1)) {}
    for (;;)
    {
      try
      {
        localObject3 = (Uri)getParcelableExtra("android.intent.extra.STREAM");
        localObject4 = getCharSequenceExtra("android.intent.extra.TEXT");
        localObject5 = getStringExtra("android.intent.extra.HTML_TEXT");
        if ((localObject3 != null) || (localObject4 != null))
        {
          localObject2 = getType();
          localObject3 = new ClipData.Item((CharSequence)localObject4, (String)localObject5, null, (Uri)localObject3);
          setClipData(new ClipData(null, new String[] { localObject2 }, (ClipData.Item)localObject3));
          addFlags(1);
          return true;
        }
        if (localObject5 != null) {
          continue;
        }
      }
      catch (ClassCastException localClassCastException4)
      {
        Object localObject4;
        Object localObject5;
        Object localObject2;
        continue;
      }
      return false;
      if ("android.intent.action.SEND_MULTIPLE".equals(localObject2)) {}
      try
      {
        localObject2 = getParcelableArrayListExtra("android.intent.extra.STREAM");
        localObject3 = getCharSequenceArrayListExtra("android.intent.extra.TEXT");
        localObject4 = getStringArrayListExtra("android.intent.extra.HTML_TEXT");
        int j = -1;
        if (localObject2 != null) {
          j = ((ArrayList)localObject2).size();
        }
        i = j;
        if (localObject3 != null)
        {
          if ((j >= 0) && (j != ((ArrayList)localObject3).size())) {
            return false;
          }
          i = ((ArrayList)localObject3).size();
        }
        j = i;
        if (localObject4 != null)
        {
          if ((i >= 0) && (i != ((ArrayList)localObject4).size())) {
            return false;
          }
          j = ((ArrayList)localObject4).size();
        }
        if (j <= 0) {
          continue;
        }
        localObject5 = getType();
        ClipData.Item localItem = makeClipItem((ArrayList)localObject2, (ArrayList)localObject3, (ArrayList)localObject4, 0);
        localObject5 = new ClipData(null, new String[] { localObject5 }, localItem);
        i = 1;
        if (i < j)
        {
          ((ClipData)localObject5).addItem(makeClipItem((ArrayList)localObject2, (ArrayList)localObject3, (ArrayList)localObject4, i));
          i += 1;
          continue;
        }
        setClipData((ClipData)localObject5);
        addFlags(1);
        return true;
      }
      catch (ClassCastException localClassCastException3) {}
      if (("android.media.action.IMAGE_CAPTURE".equals(localObject2)) || ("android.media.action.IMAGE_CAPTURE_SECURE".equals(localObject2)) || ("android.media.action.VIDEO_CAPTURE".equals(localObject2))) {
        try
        {
          localObject2 = (Uri)getParcelableExtra("output");
          if (localObject2 != null)
          {
            setClipData(ClipData.newRawUri("", (Uri)localObject2));
            addFlags(3);
            return true;
          }
        }
        catch (ClassCastException localClassCastException2)
        {
          return false;
        }
      }
    }
  }
  
  public void prepareToEnterProcess()
  {
    setDefusable(true);
    if (this.mSelector != null) {
      this.mSelector.prepareToEnterProcess();
    }
    if (this.mClipData != null) {
      this.mClipData.prepareToEnterProcess();
    }
    if ((this.mContentUserHint != -2) && (UserHandle.getAppId(Process.myUid()) != 1000))
    {
      fixUris(this.mContentUserHint);
      this.mContentUserHint = -2;
    }
  }
  
  public void prepareToLeaveProcess(Context paramContext)
  {
    boolean bool2 = true;
    boolean bool1 = bool2;
    if (this.mComponent != null)
    {
      bool1 = bool2;
      if (Objects.equals(this.mComponent.getPackageName(), paramContext.getPackageName())) {
        bool1 = false;
      }
    }
    prepareToLeaveProcess(bool1);
  }
  
  public void prepareToLeaveProcess(boolean paramBoolean)
  {
    setAllowFds(false);
    if (this.mSelector != null) {
      this.mSelector.prepareToLeaveProcess(paramBoolean);
    }
    if (this.mClipData != null) {
      this.mClipData.prepareToLeaveProcess(paramBoolean);
    }
    String str;
    if ((this.mAction != null) && (this.mData != null) && (StrictMode.vmFileUriExposureEnabled()) && (paramBoolean))
    {
      str = this.mAction;
      if (!str.equals("android.intent.action.MEDIA_REMOVED")) {
        break label75;
      }
    }
    label75:
    while ((str.equals("android.intent.action.MEDIA_UNMOUNTED")) || (str.equals("android.intent.action.MEDIA_CHECKING")) || (str.equals("android.intent.action.MEDIA_NOFS")) || (str.equals("android.intent.action.MEDIA_MOUNTED")) || (str.equals("android.intent.action.MEDIA_SHARED")) || (str.equals("android.intent.action.MEDIA_UNSHARED")) || (str.equals("android.intent.action.MEDIA_BAD_REMOVAL")) || (str.equals("android.intent.action.MEDIA_UNMOUNTABLE")) || (str.equals("android.intent.action.MEDIA_EJECT")) || (str.equals("android.intent.action.MEDIA_SCANNER_STARTED")) || (str.equals("android.intent.action.MEDIA_SCANNER_FINISHED")) || (str.equals("android.intent.action.MEDIA_SCANNER_SCAN_FILE")) || (str.equals("android.intent.action.PACKAGE_NEEDS_VERIFICATION")) || (str.equals("android.intent.action.PACKAGE_VERIFIED"))) {
      return;
    }
    this.mData.checkFileUriExposed("Intent.getData()");
  }
  
  public void prepareToLeaveUser(int paramInt)
  {
    if (this.mContentUserHint == -2) {
      this.mContentUserHint = paramInt;
    }
  }
  
  public Intent putCharSequenceArrayListExtra(String paramString, ArrayList<CharSequence> paramArrayList)
  {
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    this.mExtras.putCharSequenceArrayList(paramString, paramArrayList);
    return this;
  }
  
  public Intent putExtra(String paramString, byte paramByte)
  {
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    this.mExtras.putByte(paramString, paramByte);
    return this;
  }
  
  public Intent putExtra(String paramString, char paramChar)
  {
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    this.mExtras.putChar(paramString, paramChar);
    return this;
  }
  
  public Intent putExtra(String paramString, double paramDouble)
  {
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    this.mExtras.putDouble(paramString, paramDouble);
    return this;
  }
  
  public Intent putExtra(String paramString, float paramFloat)
  {
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    this.mExtras.putFloat(paramString, paramFloat);
    return this;
  }
  
  public Intent putExtra(String paramString, int paramInt)
  {
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    this.mExtras.putInt(paramString, paramInt);
    return this;
  }
  
  public Intent putExtra(String paramString, long paramLong)
  {
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    this.mExtras.putLong(paramString, paramLong);
    return this;
  }
  
  public Intent putExtra(String paramString, Bundle paramBundle)
  {
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    this.mExtras.putBundle(paramString, paramBundle);
    return this;
  }
  
  @Deprecated
  public Intent putExtra(String paramString, IBinder paramIBinder)
  {
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    this.mExtras.putIBinder(paramString, paramIBinder);
    return this;
  }
  
  public Intent putExtra(String paramString, Parcelable paramParcelable)
  {
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    this.mExtras.putParcelable(paramString, paramParcelable);
    return this;
  }
  
  public Intent putExtra(String paramString, Serializable paramSerializable)
  {
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    this.mExtras.putSerializable(paramString, paramSerializable);
    return this;
  }
  
  public Intent putExtra(String paramString, CharSequence paramCharSequence)
  {
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    this.mExtras.putCharSequence(paramString, paramCharSequence);
    return this;
  }
  
  public Intent putExtra(String paramString1, String paramString2)
  {
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    this.mExtras.putString(paramString1, paramString2);
    return this;
  }
  
  public Intent putExtra(String paramString, short paramShort)
  {
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    this.mExtras.putShort(paramString, paramShort);
    return this;
  }
  
  public Intent putExtra(String paramString, boolean paramBoolean)
  {
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    this.mExtras.putBoolean(paramString, paramBoolean);
    return this;
  }
  
  public Intent putExtra(String paramString, byte[] paramArrayOfByte)
  {
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    this.mExtras.putByteArray(paramString, paramArrayOfByte);
    return this;
  }
  
  public Intent putExtra(String paramString, char[] paramArrayOfChar)
  {
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    this.mExtras.putCharArray(paramString, paramArrayOfChar);
    return this;
  }
  
  public Intent putExtra(String paramString, double[] paramArrayOfDouble)
  {
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    this.mExtras.putDoubleArray(paramString, paramArrayOfDouble);
    return this;
  }
  
  public Intent putExtra(String paramString, float[] paramArrayOfFloat)
  {
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    this.mExtras.putFloatArray(paramString, paramArrayOfFloat);
    return this;
  }
  
  public Intent putExtra(String paramString, int[] paramArrayOfInt)
  {
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    this.mExtras.putIntArray(paramString, paramArrayOfInt);
    return this;
  }
  
  public Intent putExtra(String paramString, long[] paramArrayOfLong)
  {
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    this.mExtras.putLongArray(paramString, paramArrayOfLong);
    return this;
  }
  
  public Intent putExtra(String paramString, Parcelable[] paramArrayOfParcelable)
  {
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    this.mExtras.putParcelableArray(paramString, paramArrayOfParcelable);
    return this;
  }
  
  public Intent putExtra(String paramString, CharSequence[] paramArrayOfCharSequence)
  {
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    this.mExtras.putCharSequenceArray(paramString, paramArrayOfCharSequence);
    return this;
  }
  
  public Intent putExtra(String paramString, String[] paramArrayOfString)
  {
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    this.mExtras.putStringArray(paramString, paramArrayOfString);
    return this;
  }
  
  public Intent putExtra(String paramString, short[] paramArrayOfShort)
  {
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    this.mExtras.putShortArray(paramString, paramArrayOfShort);
    return this;
  }
  
  public Intent putExtra(String paramString, boolean[] paramArrayOfBoolean)
  {
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    this.mExtras.putBooleanArray(paramString, paramArrayOfBoolean);
    return this;
  }
  
  public Intent putExtras(Intent paramIntent)
  {
    if (paramIntent.mExtras != null)
    {
      if (this.mExtras == null) {
        this.mExtras = new Bundle(paramIntent.mExtras);
      }
    }
    else {
      return this;
    }
    this.mExtras.putAll(paramIntent.mExtras);
    return this;
  }
  
  public Intent putExtras(Bundle paramBundle)
  {
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    this.mExtras.putAll(paramBundle);
    return this;
  }
  
  public Intent putIntegerArrayListExtra(String paramString, ArrayList<Integer> paramArrayList)
  {
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    this.mExtras.putIntegerArrayList(paramString, paramArrayList);
    return this;
  }
  
  public Intent putParcelableArrayListExtra(String paramString, ArrayList<? extends Parcelable> paramArrayList)
  {
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    this.mExtras.putParcelableArrayList(paramString, paramArrayList);
    return this;
  }
  
  public Intent putStringArrayListExtra(String paramString, ArrayList<String> paramArrayList)
  {
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    this.mExtras.putStringArrayList(paramString, paramArrayList);
    return this;
  }
  
  public void readFromParcel(Parcel paramParcel)
  {
    setAction(paramParcel.readString());
    this.mData = ((Uri)Uri.CREATOR.createFromParcel(paramParcel));
    this.mType = paramParcel.readString();
    this.mFlags = paramParcel.readInt();
    this.mPackage = paramParcel.readString();
    this.mComponent = ComponentName.readFromParcel(paramParcel);
    if (paramParcel.readInt() != 0) {
      this.mSourceBounds = ((Rect)Rect.CREATOR.createFromParcel(paramParcel));
    }
    int j = paramParcel.readInt();
    if (j > 0)
    {
      this.mCategories = new ArraySet();
      int i = 0;
      while (i < j)
      {
        this.mCategories.add(paramParcel.readString().intern());
        i += 1;
      }
    }
    this.mCategories = null;
    if (paramParcel.readInt() != 0) {
      this.mSelector = new Intent(paramParcel);
    }
    if (paramParcel.readInt() != 0) {
      this.mClipData = new ClipData(paramParcel);
    }
    this.mContentUserHint = paramParcel.readInt();
    this.mExtras = paramParcel.readBundle();
  }
  
  public void removeCategory(String paramString)
  {
    if (this.mCategories != null)
    {
      this.mCategories.remove(paramString);
      if (this.mCategories.size() == 0) {
        this.mCategories = null;
      }
    }
  }
  
  public void removeExtra(String paramString)
  {
    if (this.mExtras != null)
    {
      this.mExtras.remove(paramString);
      if (this.mExtras.size() == 0) {
        this.mExtras = null;
      }
    }
  }
  
  public void removeUnsafeExtras()
  {
    if (this.mExtras != null) {
      this.mExtras = this.mExtras.filterValues();
    }
  }
  
  public Intent replaceExtras(Intent paramIntent)
  {
    Bundle localBundle = null;
    if (paramIntent.mExtras != null) {
      localBundle = new Bundle(paramIntent.mExtras);
    }
    this.mExtras = localBundle;
    return this;
  }
  
  public Intent replaceExtras(Bundle paramBundle)
  {
    Bundle localBundle = null;
    if (paramBundle != null) {
      localBundle = new Bundle(paramBundle);
    }
    this.mExtras = localBundle;
    return this;
  }
  
  public ComponentName resolveActivity(PackageManager paramPackageManager)
  {
    if (this.mComponent != null) {
      return this.mComponent;
    }
    paramPackageManager = paramPackageManager.resolveActivity(this, 65536);
    if (paramPackageManager != null) {
      return new ComponentName(paramPackageManager.activityInfo.applicationInfo.packageName, paramPackageManager.activityInfo.name);
    }
    return null;
  }
  
  public ActivityInfo resolveActivityInfo(PackageManager paramPackageManager, int paramInt)
  {
    Object localObject = null;
    if (this.mComponent != null) {}
    for (;;)
    {
      try
      {
        paramPackageManager = paramPackageManager.getActivityInfo(this.mComponent, paramInt);
        return paramPackageManager;
      }
      catch (PackageManager.NameNotFoundException paramPackageManager) {}
      ResolveInfo localResolveInfo = paramPackageManager.resolveActivity(this, 0x10000 | paramInt);
      paramPackageManager = (PackageManager)localObject;
      if (localResolveInfo != null) {
        return localResolveInfo.activityInfo;
      }
    }
    return null;
  }
  
  public ComponentName resolveSystemService(PackageManager paramPackageManager, int paramInt)
  {
    if (this.mComponent != null) {
      return this.mComponent;
    }
    List localList = paramPackageManager.queryIntentServices(this, paramInt);
    if (localList == null) {
      return null;
    }
    paramPackageManager = null;
    paramInt = 0;
    if (paramInt < localList.size())
    {
      Object localObject = (ResolveInfo)localList.get(paramInt);
      if ((((ResolveInfo)localObject).serviceInfo.applicationInfo.flags & 0x1) == 0) {}
      for (;;)
      {
        paramInt += 1;
        break;
        localObject = new ComponentName(((ResolveInfo)localObject).serviceInfo.applicationInfo.packageName, ((ResolveInfo)localObject).serviceInfo.name);
        if (paramPackageManager != null) {
          throw new IllegalStateException("Multiple system services handle " + this + ": " + paramPackageManager + ", " + localObject);
        }
        paramPackageManager = (PackageManager)localObject;
      }
    }
    return paramPackageManager;
  }
  
  public String resolveType(ContentResolver paramContentResolver)
  {
    if (this.mType != null) {
      return this.mType;
    }
    if ((this.mData != null) && ("content".equals(this.mData.getScheme()))) {
      return paramContentResolver.getType(this.mData);
    }
    return null;
  }
  
  public String resolveType(Context paramContext)
  {
    return resolveType(paramContext.getContentResolver());
  }
  
  public String resolveTypeIfNeeded(ContentResolver paramContentResolver)
  {
    if (this.mComponent != null) {
      return this.mType;
    }
    return resolveType(paramContentResolver);
  }
  
  public void saveToXml(XmlSerializer paramXmlSerializer)
    throws IOException
  {
    if (this.mAction != null) {
      paramXmlSerializer.attribute(null, "action", this.mAction);
    }
    if (this.mData != null) {
      paramXmlSerializer.attribute(null, "data", this.mData.toString());
    }
    if (this.mType != null) {
      paramXmlSerializer.attribute(null, "type", this.mType);
    }
    if (this.mComponent != null) {
      paramXmlSerializer.attribute(null, "component", this.mComponent.flattenToShortString());
    }
    paramXmlSerializer.attribute(null, "flags", Integer.toHexString(getFlags()));
    if (this.mCategories != null)
    {
      paramXmlSerializer.startTag(null, "categories");
      int i = this.mCategories.size() - 1;
      while (i >= 0)
      {
        paramXmlSerializer.attribute(null, "category", (String)this.mCategories.valueAt(i));
        i -= 1;
      }
      paramXmlSerializer.endTag(null, "categories");
    }
  }
  
  public Intent setAction(String paramString)
  {
    String str = null;
    if (paramString != null) {
      str = paramString.intern();
    }
    this.mAction = str;
    return this;
  }
  
  public void setAllowFds(boolean paramBoolean)
  {
    if (this.mExtras != null) {
      this.mExtras.setAllowFds(paramBoolean);
    }
  }
  
  public Intent setClass(Context paramContext, Class<?> paramClass)
  {
    this.mComponent = new ComponentName(paramContext, paramClass);
    return this;
  }
  
  public Intent setClassName(Context paramContext, String paramString)
  {
    this.mComponent = new ComponentName(paramContext, paramString);
    return this;
  }
  
  public Intent setClassName(String paramString1, String paramString2)
  {
    this.mComponent = new ComponentName(paramString1, paramString2);
    return this;
  }
  
  public void setClipData(ClipData paramClipData)
  {
    this.mClipData = paramClipData;
  }
  
  public Intent setComponent(ComponentName paramComponentName)
  {
    this.mComponent = paramComponentName;
    return this;
  }
  
  public Intent setData(Uri paramUri)
  {
    this.mData = paramUri;
    this.mType = null;
    return this;
  }
  
  public Intent setDataAndNormalize(Uri paramUri)
  {
    return setData(paramUri.normalizeScheme());
  }
  
  public Intent setDataAndType(Uri paramUri, String paramString)
  {
    this.mData = paramUri;
    this.mType = paramString;
    return this;
  }
  
  public Intent setDataAndTypeAndNormalize(Uri paramUri, String paramString)
  {
    return setDataAndType(paramUri.normalizeScheme(), normalizeMimeType(paramString));
  }
  
  public void setDefusable(boolean paramBoolean)
  {
    if (this.mExtras != null) {
      this.mExtras.setDefusable(paramBoolean);
    }
  }
  
  public void setExtrasClassLoader(ClassLoader paramClassLoader)
  {
    if (this.mExtras != null) {
      this.mExtras.setClassLoader(paramClassLoader);
    }
  }
  
  public Intent setFlags(int paramInt)
  {
    this.mFlags = paramInt;
    return this;
  }
  
  public Intent setPackage(String paramString)
  {
    if ((paramString != null) && (this.mSelector != null)) {
      throw new IllegalArgumentException("Can't set package name when selector is already set");
    }
    this.mPackage = paramString;
    return this;
  }
  
  public void setSelector(Intent paramIntent)
  {
    if (paramIntent == this) {
      throw new IllegalArgumentException("Intent being set as a selector of itself");
    }
    if ((paramIntent != null) && (this.mPackage != null)) {
      throw new IllegalArgumentException("Can't set selector when package name is already set");
    }
    this.mSelector = paramIntent;
  }
  
  public void setSourceBounds(Rect paramRect)
  {
    if (paramRect != null)
    {
      this.mSourceBounds = new Rect(paramRect);
      return;
    }
    this.mSourceBounds = null;
  }
  
  public Intent setType(String paramString)
  {
    this.mData = null;
    this.mType = paramString;
    return this;
  }
  
  public Intent setTypeAndNormalize(String paramString)
  {
    return setType(normalizeMimeType(paramString));
  }
  
  public String toInsecureString()
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    localStringBuilder.append("Intent { ");
    toShortString(localStringBuilder, false, true, true, false);
    localStringBuilder.append(" }");
    return localStringBuilder.toString();
  }
  
  public String toInsecureStringWithClip()
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    localStringBuilder.append("Intent { ");
    toShortString(localStringBuilder, false, true, true, true);
    localStringBuilder.append(" }");
    return localStringBuilder.toString();
  }
  
  public String toShortString(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    toShortString(localStringBuilder, paramBoolean1, paramBoolean2, paramBoolean3, paramBoolean4);
    return localStringBuilder.toString();
  }
  
  public void toShortString(StringBuilder paramStringBuilder, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
  {
    int j = 1;
    if (this.mAction != null)
    {
      paramStringBuilder.append("act=").append(this.mAction);
      j = 0;
    }
    int i = j;
    if (this.mCategories != null)
    {
      if (j == 0) {
        paramStringBuilder.append(' ');
      }
      j = 0;
      paramStringBuilder.append("cat=[");
      i = 0;
      while (i < this.mCategories.size())
      {
        if (i > 0) {
          paramStringBuilder.append(',');
        }
        paramStringBuilder.append((String)this.mCategories.valueAt(i));
        i += 1;
      }
      paramStringBuilder.append("]");
      i = j;
    }
    j = i;
    if (this.mData != null)
    {
      if (i == 0) {
        paramStringBuilder.append(' ');
      }
      j = 0;
      paramStringBuilder.append("dat=");
      if (!paramBoolean1) {
        break label570;
      }
      paramStringBuilder.append(this.mData.toSafeString());
    }
    for (;;)
    {
      i = j;
      if (this.mType != null)
      {
        if (j == 0) {
          paramStringBuilder.append(' ');
        }
        i = 0;
        paramStringBuilder.append("typ=").append(this.mType);
      }
      j = i;
      if (this.mFlags != 0)
      {
        if (i == 0) {
          paramStringBuilder.append(' ');
        }
        j = 0;
        paramStringBuilder.append("flg=0x").append(Integer.toHexString(this.mFlags));
      }
      i = j;
      if (this.mPackage != null)
      {
        if (j == 0) {
          paramStringBuilder.append(' ');
        }
        i = 0;
        paramStringBuilder.append("pkg=").append(this.mPackage);
      }
      j = i;
      if (paramBoolean2)
      {
        j = i;
        if (this.mComponent != null)
        {
          if (i == 0) {
            paramStringBuilder.append(' ');
          }
          j = 0;
          paramStringBuilder.append("cmp=").append(this.mComponent.flattenToShortString());
        }
      }
      i = j;
      if (this.mSourceBounds != null)
      {
        if (j == 0) {
          paramStringBuilder.append(' ');
        }
        i = 0;
        paramStringBuilder.append("bnds=").append(this.mSourceBounds.toShortString());
      }
      j = i;
      if (this.mClipData != null)
      {
        if (i == 0) {
          paramStringBuilder.append(' ');
        }
        paramStringBuilder.append("clip={");
        if (!paramBoolean4) {
          break;
        }
        this.mClipData.toShortString(paramStringBuilder);
        j = 0;
        paramStringBuilder.append('}');
      }
      i = j;
      if (paramBoolean3)
      {
        i = j;
        if (this.mExtras != null)
        {
          if (j == 0) {
            paramStringBuilder.append(' ');
          }
          i = 0;
          paramStringBuilder.append("(has extras)");
        }
      }
      if (this.mContentUserHint != -2)
      {
        if (i == 0) {
          paramStringBuilder.append(' ');
        }
        paramStringBuilder.append("u=").append(this.mContentUserHint);
      }
      if (this.mSelector != null)
      {
        paramStringBuilder.append(" sel=");
        this.mSelector.toShortString(paramStringBuilder, paramBoolean1, paramBoolean2, paramBoolean3, paramBoolean4);
        paramStringBuilder.append("}");
      }
      return;
      label570:
      paramStringBuilder.append(this.mData);
    }
    boolean bool;
    if (this.mClipData.getDescription() != null) {
      if (this.mClipData.getDescription().toShortStringTypesOnly(paramStringBuilder)) {
        bool = false;
      }
    }
    for (;;)
    {
      this.mClipData.toShortStringShortItems(paramStringBuilder, bool);
      break;
      bool = true;
      continue;
      bool = true;
    }
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    localStringBuilder.append("Intent { ");
    toShortString(localStringBuilder, true, true, true, false);
    localStringBuilder.append(" }");
    return localStringBuilder.toString();
  }
  
  @Deprecated
  public String toURI()
  {
    return toUri(0);
  }
  
  public String toUri(int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    Object localObject1;
    if ((paramInt & 0x2) != 0)
    {
      if (this.mPackage == null) {
        throw new IllegalArgumentException("Intent must include an explicit package name to build an android-app: " + this);
      }
      localStringBuilder.append("android-app://");
      localStringBuilder.append(this.mPackage);
      localObject1 = null;
      if (this.mData != null)
      {
        localObject2 = this.mData.getScheme();
        localObject1 = localObject2;
        if (localObject2 != null)
        {
          localStringBuilder.append('/');
          localStringBuilder.append((String)localObject2);
          str1 = this.mData.getEncodedAuthority();
          localObject1 = localObject2;
          if (str1 != null)
          {
            localStringBuilder.append('/');
            localStringBuilder.append(str1);
            localObject1 = this.mData.getEncodedPath();
            if (localObject1 != null) {
              localStringBuilder.append((String)localObject1);
            }
            localObject1 = this.mData.getEncodedQuery();
            if (localObject1 != null)
            {
              localStringBuilder.append('?');
              localStringBuilder.append((String)localObject1);
            }
            str1 = this.mData.getEncodedFragment();
            localObject1 = localObject2;
            if (str1 != null)
            {
              localStringBuilder.append('#');
              localStringBuilder.append(str1);
              localObject1 = localObject2;
            }
          }
        }
      }
      if (localObject1 == null) {}
      for (localObject1 = "android.intent.action.MAIN";; localObject1 = "android.intent.action.VIEW")
      {
        toUriFragment(localStringBuilder, null, (String)localObject1, this.mPackage, paramInt);
        return localStringBuilder.toString();
      }
    }
    Object localObject2 = null;
    String str1 = null;
    if (this.mData != null)
    {
      String str2 = this.mData.toString();
      localObject1 = str1;
      localObject2 = str2;
      if ((paramInt & 0x1) != 0)
      {
        int j = str2.length();
        int i = 0;
        localObject1 = str1;
        localObject2 = str2;
        if (i < j)
        {
          int k = str2.charAt(i);
          if ((k >= 97) && (k <= 122)) {}
          while (((k >= 65) && (k <= 90)) || (k == 46) || (k == 45))
          {
            i += 1;
            break;
          }
          localObject1 = str1;
          localObject2 = str2;
          if (k == 58)
          {
            localObject1 = str1;
            localObject2 = str2;
            if (i > 0)
            {
              localObject1 = str2.substring(0, i);
              localStringBuilder.append("intent:");
              localObject2 = str2.substring(i + 1);
            }
          }
        }
      }
      localStringBuilder.append((String)localObject2);
    }
    for (;;)
    {
      toUriFragment(localStringBuilder, (String)localObject1, "android.intent.action.VIEW", null, paramInt);
      return localStringBuilder.toString();
      localObject1 = localObject2;
      if ((paramInt & 0x1) != 0)
      {
        localStringBuilder.append("intent:");
        localObject1 = localObject2;
      }
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mAction);
    Uri.writeToParcel(paramParcel, this.mData);
    paramParcel.writeString(this.mType);
    paramParcel.writeInt(this.mFlags);
    paramParcel.writeString(this.mPackage);
    ComponentName.writeToParcel(this.mComponent, paramParcel);
    if (this.mSourceBounds != null)
    {
      paramParcel.writeInt(1);
      this.mSourceBounds.writeToParcel(paramParcel, paramInt);
    }
    while (this.mCategories != null)
    {
      int j = this.mCategories.size();
      paramParcel.writeInt(j);
      int i = 0;
      while (i < j)
      {
        paramParcel.writeString((String)this.mCategories.valueAt(i));
        i += 1;
      }
      paramParcel.writeInt(0);
    }
    paramParcel.writeInt(0);
    if (this.mSelector != null)
    {
      paramParcel.writeInt(1);
      this.mSelector.writeToParcel(paramParcel, paramInt);
      if (this.mClipData == null) {
        break label201;
      }
      paramParcel.writeInt(1);
      this.mClipData.writeToParcel(paramParcel, paramInt);
    }
    for (;;)
    {
      paramParcel.writeInt(this.mContentUserHint);
      paramParcel.writeBundle(this.mExtras);
      return;
      paramParcel.writeInt(0);
      break;
      label201:
      paramParcel.writeInt(0);
    }
  }
  
  public static abstract interface CommandOptionHandler
  {
    public abstract boolean handleOption(String paramString, ShellCommand paramShellCommand);
  }
  
  public static final class FilterComparison
  {
    private final int mHashCode;
    private final Intent mIntent;
    
    public FilterComparison(Intent paramIntent)
    {
      this.mIntent = paramIntent;
      this.mHashCode = paramIntent.filterHashCode();
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof FilterComparison))
      {
        paramObject = ((FilterComparison)paramObject).mIntent;
        return this.mIntent.filterEquals((Intent)paramObject);
      }
      return false;
    }
    
    public Intent getIntent()
    {
      return this.mIntent;
    }
    
    public int hashCode()
    {
      return this.mHashCode;
    }
  }
  
  public static class ShortcutIconResource
    implements Parcelable
  {
    public static final Parcelable.Creator<ShortcutIconResource> CREATOR = new Parcelable.Creator()
    {
      public Intent.ShortcutIconResource createFromParcel(Parcel paramAnonymousParcel)
      {
        Intent.ShortcutIconResource localShortcutIconResource = new Intent.ShortcutIconResource();
        localShortcutIconResource.packageName = paramAnonymousParcel.readString();
        localShortcutIconResource.resourceName = paramAnonymousParcel.readString();
        return localShortcutIconResource;
      }
      
      public Intent.ShortcutIconResource[] newArray(int paramAnonymousInt)
      {
        return new Intent.ShortcutIconResource[paramAnonymousInt];
      }
    };
    public String packageName;
    public String resourceName;
    
    public static ShortcutIconResource fromContext(Context paramContext, int paramInt)
    {
      ShortcutIconResource localShortcutIconResource = new ShortcutIconResource();
      localShortcutIconResource.packageName = paramContext.getPackageName();
      localShortcutIconResource.resourceName = paramContext.getResources().getResourceName(paramInt);
      return localShortcutIconResource;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public String toString()
    {
      return this.resourceName;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeString(this.packageName);
      paramParcel.writeString(this.resourceName);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/Intent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */