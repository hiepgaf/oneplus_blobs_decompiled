package android.os;

import android.accounts.AccountManager;
import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UserManager
{
  private static final String ACTION_CREATE_USER = "android.os.action.CREATE_USER";
  public static final String ALLOW_PARENT_PROFILE_APP_LINKING = "allow_parent_profile_app_linking";
  public static final String DISALLLOW_UNMUTE_DEVICE = "disallow_unmute_device";
  public static final String DISALLOW_ADD_USER = "no_add_user";
  public static final String DISALLOW_ADJUST_VOLUME = "no_adjust_volume";
  public static final String DISALLOW_APPS_CONTROL = "no_control_apps";
  public static final String DISALLOW_CAMERA = "no_camera";
  public static final String DISALLOW_CONFIG_BLUETOOTH = "no_config_bluetooth";
  public static final String DISALLOW_CONFIG_CELL_BROADCASTS = "no_config_cell_broadcasts";
  public static final String DISALLOW_CONFIG_CREDENTIALS = "no_config_credentials";
  public static final String DISALLOW_CONFIG_MOBILE_NETWORKS = "no_config_mobile_networks";
  public static final String DISALLOW_CONFIG_TETHERING = "no_config_tethering";
  public static final String DISALLOW_CONFIG_VPN = "no_config_vpn";
  public static final String DISALLOW_CONFIG_WIFI = "no_config_wifi";
  public static final String DISALLOW_CREATE_WINDOWS = "no_create_windows";
  public static final String DISALLOW_CROSS_PROFILE_COPY_PASTE = "no_cross_profile_copy_paste";
  public static final String DISALLOW_DATA_ROAMING = "no_data_roaming";
  public static final String DISALLOW_DEBUGGING_FEATURES = "no_debugging_features";
  public static final String DISALLOW_FACTORY_RESET = "no_factory_reset";
  public static final String DISALLOW_FUN = "no_fun";
  public static final String DISALLOW_INSTALL_APPS = "no_install_apps";
  public static final String DISALLOW_INSTALL_UNKNOWN_SOURCES = "no_install_unknown_sources";
  public static final String DISALLOW_MODIFY_ACCOUNTS = "no_modify_accounts";
  public static final String DISALLOW_MOUNT_PHYSICAL_MEDIA = "no_physical_media";
  public static final String DISALLOW_NETWORK_RESET = "no_network_reset";
  public static final String DISALLOW_OEM_UNLOCK = "no_oem_unlock";
  public static final String DISALLOW_OUTGOING_BEAM = "no_outgoing_beam";
  public static final String DISALLOW_OUTGOING_CALLS = "no_outgoing_calls";
  public static final String DISALLOW_RECORD_AUDIO = "no_record_audio";
  public static final String DISALLOW_REMOVE_USER = "no_remove_user";
  public static final String DISALLOW_RUN_IN_BACKGROUND = "no_run_in_background";
  public static final String DISALLOW_SAFE_BOOT = "no_safe_boot";
  public static final String DISALLOW_SET_USER_ICON = "no_set_user_icon";
  public static final String DISALLOW_SET_WALLPAPER = "no_set_wallpaper";
  public static final String DISALLOW_SHARE_LOCATION = "no_share_location";
  public static final String DISALLOW_SMS = "no_sms";
  public static final String DISALLOW_UNINSTALL_APPS = "no_uninstall_apps";
  public static final String DISALLOW_UNMUTE_MICROPHONE = "no_unmute_microphone";
  public static final String DISALLOW_USB_FILE_TRANSFER = "no_usb_file_transfer";
  public static final String DISALLOW_WALLPAPER = "no_wallpaper";
  public static final String ENSURE_VERIFY_APPS = "ensure_verify_apps";
  public static final String EXTRA_USER_ACCOUNT_NAME = "android.os.extra.USER_ACCOUNT_NAME";
  public static final String EXTRA_USER_ACCOUNT_OPTIONS = "android.os.extra.USER_ACCOUNT_OPTIONS";
  public static final String EXTRA_USER_ACCOUNT_TYPE = "android.os.extra.USER_ACCOUNT_TYPE";
  public static final String EXTRA_USER_NAME = "android.os.extra.USER_NAME";
  public static final String KEY_RESTRICTIONS_PENDING = "restrictions_pending";
  public static final int PIN_VERIFICATION_FAILED_INCORRECT = -3;
  public static final int PIN_VERIFICATION_FAILED_NOT_SET = -2;
  public static final int PIN_VERIFICATION_SUCCESS = -1;
  public static final int RESTRICTION_NOT_SET = 0;
  public static final int RESTRICTION_SOURCE_DEVICE_OWNER = 2;
  public static final int RESTRICTION_SOURCE_PROFILE_OWNER = 4;
  public static final int RESTRICTION_SOURCE_SYSTEM = 1;
  private static String TAG = "UserManager";
  public static final int USER_CREATION_FAILED_NOT_PERMITTED = 1;
  public static final int USER_CREATION_FAILED_NO_MORE_USERS = 2;
  private final Context mContext;
  private final IUserManager mService;
  
  public UserManager(Context paramContext, IUserManager paramIUserManager)
  {
    this.mService = paramIUserManager;
    this.mContext = paramContext;
  }
  
  public static Intent createUserCreationIntent(String paramString1, String paramString2, String paramString3, PersistableBundle paramPersistableBundle)
  {
    Intent localIntent = new Intent("android.os.action.CREATE_USER");
    if (paramString1 != null) {
      localIntent.putExtra("android.os.extra.USER_NAME", paramString1);
    }
    if ((paramString2 != null) && (paramString3 == null)) {
      throw new IllegalArgumentException("accountType must be specified if accountName is specified");
    }
    if (paramString2 != null) {
      localIntent.putExtra("android.os.extra.USER_ACCOUNT_NAME", paramString2);
    }
    if (paramString3 != null) {
      localIntent.putExtra("android.os.extra.USER_ACCOUNT_TYPE", paramString3);
    }
    if (paramPersistableBundle != null) {
      localIntent.putExtra("android.os.extra.USER_ACCOUNT_OPTIONS", paramPersistableBundle);
    }
    return localIntent;
  }
  
  public static UserManager get(Context paramContext)
  {
    return (UserManager)paramContext.getSystemService("user");
  }
  
  public static int getMaxSupportedUsers()
  {
    if (Build.ID.startsWith("JVP")) {
      return 1;
    }
    if (ActivityManager.isLowRamDeviceStatic()) {
      return 1;
    }
    return SystemProperties.getInt("fw.max_users", Resources.getSystem().getInteger(17694863));
  }
  
  public static boolean isDeviceInDemoMode(Context paramContext)
  {
    boolean bool = false;
    if (Settings.Global.getInt(paramContext.getContentResolver(), "device_demo_mode", 0) > 0) {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isSplitSystemUser()
  {
    return SystemProperties.getBoolean("ro.fw.system_user_split", false);
  }
  
  public static boolean supportsMultipleUsers()
  {
    if (getMaxSupportedUsers() > 1) {
      return SystemProperties.getBoolean("fw.show_multiuserui", Resources.getSystem().getBoolean(17956985));
    }
    return false;
  }
  
  public boolean canAddMoreManagedProfiles(int paramInt, boolean paramBoolean)
  {
    try
    {
      paramBoolean = this.mService.canAddMoreManagedProfiles(paramInt, paramBoolean);
      return paramBoolean;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean canAddMoreUsers()
  {
    List localList = getUsers(true);
    int m = localList.size();
    int j = 0;
    int i = 0;
    while (i < m)
    {
      int k = j;
      if (!((UserInfo)localList.get(i)).isGuest()) {
        k = j + 1;
      }
      i += 1;
      j = k;
    }
    return j < getMaxSupportedUsers();
  }
  
  public boolean canHaveRestrictedProfile(int paramInt)
  {
    try
    {
      boolean bool = this.mService.canHaveRestrictedProfile(paramInt);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean canSwitchUsers()
  {
    int i;
    boolean bool;
    if (Settings.Global.getInt(this.mContext.getContentResolver(), "allow_user_switching_when_system_user_locked", 0) != 0)
    {
      i = 1;
      bool = isUserUnlocked(UserHandle.SYSTEM);
      if (TelephonyManager.getDefault().getCallState() == 0) {
        break label57;
      }
    }
    label57:
    for (int j = 1;; j = 0)
    {
      if (((i != 0) || (bool)) && (j == 0)) {
        break label62;
      }
      return false;
      i = 0;
      break;
    }
    label62:
    return true;
  }
  
  public void clearSeedAccountData()
  {
    try
    {
      this.mService.clearSeedAccountData();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public UserInfo createGuest(Context paramContext, String paramString)
  {
    try
    {
      paramString = this.mService.createUser(paramString, 4);
      if (paramString != null) {
        Settings.Secure.putStringForUser(paramContext.getContentResolver(), "skip_first_use_hints", "1", paramString.id);
      }
      return paramString;
    }
    catch (RemoteException paramContext)
    {
      throw paramContext.rethrowFromSystemServer();
    }
  }
  
  public UserInfo createProfileForUser(String paramString, int paramInt1, int paramInt2)
  {
    try
    {
      paramString = this.mService.createProfileForUser(paramString, paramInt1, paramInt2);
      return paramString;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public UserInfo createRestrictedProfile(String paramString)
  {
    try
    {
      UserHandle localUserHandle = Process.myUserHandle();
      paramString = this.mService.createRestrictedProfile(paramString, localUserHandle.getIdentifier());
      if (paramString != null) {
        AccountManager.get(this.mContext).addSharedAccountsFromParentUser(localUserHandle, UserHandle.of(paramString.id));
      }
      return paramString;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public UserInfo createUser(String paramString, int paramInt)
  {
    try
    {
      paramString = this.mService.createUser(paramString, paramInt);
      if (paramString != null)
      {
        if (paramString.isAdmin()) {
          return paramString;
        }
        this.mService.setUserRestriction("no_sms", true, paramString.id);
        this.mService.setUserRestriction("no_outgoing_calls", true, paramString.id);
        return paramString;
      }
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
    return paramString;
  }
  
  public Bundle getApplicationRestrictions(String paramString)
  {
    try
    {
      paramString = this.mService.getApplicationRestrictions(paramString);
      return paramString;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public Bundle getApplicationRestrictions(String paramString, UserHandle paramUserHandle)
  {
    try
    {
      paramString = this.mService.getApplicationRestrictionsForUser(paramString, paramUserHandle.getIdentifier());
      return paramString;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public Drawable getBadgedDrawableForUser(Drawable paramDrawable, UserHandle paramUserHandle, Rect paramRect, int paramInt)
  {
    return this.mContext.getPackageManager().getUserBadgedDrawableForDensity(paramDrawable, paramUserHandle, paramRect, paramInt);
  }
  
  public Drawable getBadgedIconForUser(Drawable paramDrawable, UserHandle paramUserHandle)
  {
    return this.mContext.getPackageManager().getUserBadgedIcon(paramDrawable, paramUserHandle);
  }
  
  public CharSequence getBadgedLabelForUser(CharSequence paramCharSequence, UserHandle paramUserHandle)
  {
    return this.mContext.getPackageManager().getUserBadgedLabel(paramCharSequence, paramUserHandle);
  }
  
  public int getCredentialOwnerProfile(int paramInt)
  {
    try
    {
      paramInt = this.mService.getCredentialOwnerProfile(paramInt);
      return paramInt;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public Bundle getDefaultGuestRestrictions()
  {
    try
    {
      Bundle localBundle = this.mService.getDefaultGuestRestrictions();
      return localBundle;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int[] getEnabledProfileIds(int paramInt)
  {
    return getProfileIds(paramInt, true);
  }
  
  public List<UserInfo> getEnabledProfiles(int paramInt)
  {
    try
    {
      List localList = this.mService.getProfiles(paramInt, true);
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public UserInfo getPrimaryUser()
  {
    try
    {
      UserInfo localUserInfo = this.mService.getPrimaryUser();
      return localUserInfo;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int[] getProfileIds(int paramInt, boolean paramBoolean)
  {
    try
    {
      int[] arrayOfInt = this.mService.getProfileIds(paramInt, paramBoolean);
      return arrayOfInt;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int[] getProfileIdsWithDisabled(int paramInt)
  {
    return getProfileIds(paramInt, false);
  }
  
  public UserInfo getProfileParent(int paramInt)
  {
    try
    {
      UserInfo localUserInfo = this.mService.getProfileParent(paramInt);
      return localUserInfo;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public List<UserInfo> getProfiles(int paramInt)
  {
    try
    {
      List localList = this.mService.getProfiles(paramInt, false);
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public String getSeedAccountName()
  {
    try
    {
      String str = this.mService.getSeedAccountName();
      return str;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public PersistableBundle getSeedAccountOptions()
  {
    try
    {
      PersistableBundle localPersistableBundle = this.mService.getSeedAccountOptions();
      return localPersistableBundle;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public String getSeedAccountType()
  {
    try
    {
      String str = this.mService.getSeedAccountType();
      return str;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public long getSerialNumberForUser(UserHandle paramUserHandle)
  {
    return getUserSerialNumber(paramUserHandle.getIdentifier());
  }
  
  public long[] getSerialNumbersOfUsers(boolean paramBoolean)
  {
    try
    {
      List localList = this.mService.getUsers(paramBoolean);
      long[] arrayOfLong = new long[localList.size()];
      int i = 0;
      while (i < arrayOfLong.length)
      {
        arrayOfLong[i] = ((UserInfo)localList.get(i)).serialNumber;
        i += 1;
      }
      return arrayOfLong;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public String getUserAccount(int paramInt)
  {
    try
    {
      String str = this.mService.getUserAccount(paramInt);
      return str;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int getUserCount()
  {
    List localList = getUsers();
    if (localList != null) {
      return localList.size();
    }
    return 1;
  }
  
  public long getUserCreationTime(UserHandle paramUserHandle)
  {
    try
    {
      long l = this.mService.getUserCreationTime(paramUserHandle.getIdentifier());
      return l;
    }
    catch (RemoteException paramUserHandle)
    {
      throw paramUserHandle.rethrowFromSystemServer();
    }
  }
  
  public UserHandle getUserForSerialNumber(long paramLong)
  {
    int i = getUserHandle((int)paramLong);
    if (i >= 0) {
      return new UserHandle(i);
    }
    return null;
  }
  
  public int getUserHandle()
  {
    return UserHandle.myUserId();
  }
  
  public int getUserHandle(int paramInt)
  {
    try
    {
      paramInt = this.mService.getUserHandle(paramInt);
      return paramInt;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  /* Error */
  public Bitmap getUserIcon(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 178	android/os/UserManager:mService	Landroid/os/IUserManager;
    //   4: iload_1
    //   5: invokeinterface 506 2 0
    //   10: astore_2
    //   11: aload_2
    //   12: ifnull +37 -> 49
    //   15: aload_2
    //   16: invokevirtual 512	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
    //   19: invokestatic 518	android/graphics/BitmapFactory:decodeFileDescriptor	(Ljava/io/FileDescriptor;)Landroid/graphics/Bitmap;
    //   22: astore_3
    //   23: aload_2
    //   24: invokevirtual 521	android/os/ParcelFileDescriptor:close	()V
    //   27: aload_3
    //   28: areturn
    //   29: astore_2
    //   30: aload_3
    //   31: areturn
    //   32: astore_3
    //   33: aload_2
    //   34: invokevirtual 521	android/os/ParcelFileDescriptor:close	()V
    //   37: aload_3
    //   38: athrow
    //   39: astore_2
    //   40: aload_2
    //   41: invokevirtual 290	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
    //   44: athrow
    //   45: astore_2
    //   46: goto -9 -> 37
    //   49: aconst_null
    //   50: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	51	0	this	UserManager
    //   0	51	1	paramInt	int
    //   10	14	2	localParcelFileDescriptor	ParcelFileDescriptor
    //   29	5	2	localIOException1	java.io.IOException
    //   39	2	2	localRemoteException	RemoteException
    //   45	1	2	localIOException2	java.io.IOException
    //   22	9	3	localBitmap	Bitmap
    //   32	6	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   23	27	29	java/io/IOException
    //   15	23	32	finally
    //   0	11	39	android/os/RemoteException
    //   23	27	39	android/os/RemoteException
    //   33	37	39	android/os/RemoteException
    //   37	39	39	android/os/RemoteException
    //   33	37	45	java/io/IOException
  }
  
  public UserInfo getUserInfo(int paramInt)
  {
    try
    {
      UserInfo localUserInfo = this.mService.getUserInfo(paramInt);
      return localUserInfo;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public String getUserName()
  {
    try
    {
      String str = this.mService.getUserInfo(getUserHandle()).name;
      return str;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public List<UserHandle> getUserProfiles()
  {
    int[] arrayOfInt = getProfileIds(UserHandle.myUserId(), true);
    ArrayList localArrayList = new ArrayList(arrayOfInt.length);
    int i = 0;
    int j = arrayOfInt.length;
    while (i < j)
    {
      localArrayList.add(UserHandle.of(arrayOfInt[i]));
      i += 1;
    }
    return localArrayList;
  }
  
  public int getUserRestrictionSource(String paramString, UserHandle paramUserHandle)
  {
    try
    {
      int i = this.mService.getUserRestrictionSource(paramString, paramUserHandle.getIdentifier());
      return i;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public Bundle getUserRestrictions()
  {
    return getUserRestrictions(Process.myUserHandle());
  }
  
  public Bundle getUserRestrictions(UserHandle paramUserHandle)
  {
    try
    {
      paramUserHandle = this.mService.getUserRestrictions(paramUserHandle.getIdentifier());
      return paramUserHandle;
    }
    catch (RemoteException paramUserHandle)
    {
      throw paramUserHandle.rethrowFromSystemServer();
    }
  }
  
  public int getUserSerialNumber(int paramInt)
  {
    try
    {
      paramInt = this.mService.getUserSerialNumber(paramInt);
      return paramInt;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public List<UserInfo> getUsers()
  {
    try
    {
      List localList = this.mService.getUsers(false);
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public List<UserInfo> getUsers(boolean paramBoolean)
  {
    try
    {
      List localList = this.mService.getUsers(paramBoolean);
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean hasBaseUserRestriction(String paramString, UserHandle paramUserHandle)
  {
    try
    {
      boolean bool = this.mService.hasBaseUserRestriction(paramString, paramUserHandle.getIdentifier());
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public boolean hasUserRestriction(String paramString)
  {
    return hasUserRestriction(paramString, Process.myUserHandle());
  }
  
  public boolean hasUserRestriction(String paramString, UserHandle paramUserHandle)
  {
    try
    {
      boolean bool = this.mService.hasUserRestriction(paramString, paramUserHandle.getIdentifier());
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public boolean isAdminUser()
  {
    return isUserAdmin(UserHandle.myUserId());
  }
  
  public boolean isDemoUser()
  {
    try
    {
      boolean bool = this.mService.isDemoUser(UserHandle.myUserId());
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isEphemeralUser()
  {
    return isUserEphemeral(UserHandle.myUserId());
  }
  
  public boolean isGuestUser()
  {
    UserInfo localUserInfo = getUserInfo(UserHandle.myUserId());
    if (localUserInfo != null) {
      return localUserInfo.isGuest();
    }
    return false;
  }
  
  public boolean isLinkedUser()
  {
    try
    {
      boolean bool = this.mService.isRestricted();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isManagedProfile()
  {
    try
    {
      boolean bool = this.mService.isManagedProfile(UserHandle.myUserId());
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isManagedProfile(int paramInt)
  {
    try
    {
      boolean bool = this.mService.isManagedProfile(paramInt);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isPrimaryUser()
  {
    UserInfo localUserInfo = getUserInfo(UserHandle.myUserId());
    if (localUserInfo != null) {
      return localUserInfo.isPrimary();
    }
    return false;
  }
  
  public boolean isQuietModeEnabled(UserHandle paramUserHandle)
  {
    try
    {
      boolean bool = this.mService.isQuietModeEnabled(paramUserHandle.getIdentifier());
      return bool;
    }
    catch (RemoteException paramUserHandle)
    {
      throw paramUserHandle.rethrowFromSystemServer();
    }
  }
  
  public boolean isSameProfileGroup(int paramInt1, int paramInt2)
  {
    try
    {
      boolean bool = this.mService.isSameProfileGroup(paramInt1, paramInt2);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isSystemUser()
  {
    boolean bool = false;
    if (UserHandle.myUserId() == 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isUserAGoat()
  {
    return this.mContext.getPackageManager().isPackageAvailable("com.coffeestainstudios.goatsimulator");
  }
  
  public boolean isUserAdmin(int paramInt)
  {
    UserInfo localUserInfo = getUserInfo(paramInt);
    if (localUserInfo != null) {
      return localUserInfo.isAdmin();
    }
    return false;
  }
  
  public boolean isUserEphemeral(int paramInt)
  {
    UserInfo localUserInfo = getUserInfo(paramInt);
    if (localUserInfo != null) {
      return localUserInfo.isEphemeral();
    }
    return false;
  }
  
  public boolean isUserRunning(int paramInt)
  {
    try
    {
      boolean bool = ActivityManagerNative.getDefault().isUserRunning(paramInt, 0);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isUserRunning(UserHandle paramUserHandle)
  {
    return isUserRunning(paramUserHandle.getIdentifier());
  }
  
  @Deprecated
  public boolean isUserRunningAndLocked()
  {
    return isUserRunningAndLocked(Process.myUserHandle());
  }
  
  @Deprecated
  public boolean isUserRunningAndLocked(UserHandle paramUserHandle)
  {
    try
    {
      boolean bool = ActivityManagerNative.getDefault().isUserRunning(paramUserHandle.getIdentifier(), 2);
      return bool;
    }
    catch (RemoteException paramUserHandle)
    {
      throw paramUserHandle.rethrowFromSystemServer();
    }
  }
  
  @Deprecated
  public boolean isUserRunningAndUnlocked()
  {
    return isUserRunningAndUnlocked(Process.myUserHandle());
  }
  
  @Deprecated
  public boolean isUserRunningAndUnlocked(UserHandle paramUserHandle)
  {
    try
    {
      boolean bool = ActivityManagerNative.getDefault().isUserRunning(paramUserHandle.getIdentifier(), 4);
      return bool;
    }
    catch (RemoteException paramUserHandle)
    {
      throw paramUserHandle.rethrowFromSystemServer();
    }
  }
  
  public boolean isUserRunningOrStopping(UserHandle paramUserHandle)
  {
    try
    {
      boolean bool = ActivityManagerNative.getDefault().isUserRunning(paramUserHandle.getIdentifier(), 1);
      return bool;
    }
    catch (RemoteException paramUserHandle)
    {
      throw paramUserHandle.rethrowFromSystemServer();
    }
  }
  
  public boolean isUserSwitcherEnabled()
  {
    if (!supportsMultipleUsers()) {
      return false;
    }
    if (isDeviceInDemoMode(this.mContext)) {
      return false;
    }
    Object localObject = getUsers(true);
    if (localObject == null) {
      return false;
    }
    int i = 0;
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext()) {
      if (((UserInfo)((Iterator)localObject).next()).supportsSwitchToByUser()) {
        i += 1;
      }
    }
    if (((DevicePolicyManager)this.mContext.getSystemService(DevicePolicyManager.class)).getGuestUserDisabled(null)) {}
    for (boolean bool = false; i <= 1; bool = true) {
      return bool;
    }
    return true;
  }
  
  public boolean isUserUnlocked()
  {
    return isUserUnlocked(Process.myUserHandle());
  }
  
  public boolean isUserUnlocked(int paramInt)
  {
    try
    {
      boolean bool = ActivityManagerNative.getDefault().isUserRunning(paramInt, 4);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isUserUnlocked(UserHandle paramUserHandle)
  {
    return isUserUnlocked(paramUserHandle.getIdentifier());
  }
  
  public boolean isUserUnlockingOrUnlocked(int paramInt)
  {
    try
    {
      boolean bool = ActivityManagerNative.getDefault().isUserRunning(paramInt, 8);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isUserUnlockingOrUnlocked(UserHandle paramUserHandle)
  {
    return isUserUnlockingOrUnlocked(paramUserHandle.getIdentifier());
  }
  
  public boolean markGuestForDeletion(int paramInt)
  {
    try
    {
      boolean bool = this.mService.markGuestForDeletion(paramInt);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean removeUser(int paramInt)
  {
    try
    {
      boolean bool = this.mService.removeUser(paramInt);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void setApplicationRestrictions(String paramString, Bundle paramBundle, UserHandle paramUserHandle)
  {
    try
    {
      this.mService.setApplicationRestrictions(paramString, paramBundle, paramUserHandle.getIdentifier());
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void setDefaultGuestRestrictions(Bundle paramBundle)
  {
    try
    {
      this.mService.setDefaultGuestRestrictions(paramBundle);
      return;
    }
    catch (RemoteException paramBundle)
    {
      throw paramBundle.rethrowFromSystemServer();
    }
  }
  
  public void setQuietModeEnabled(int paramInt, boolean paramBoolean)
  {
    try
    {
      this.mService.setQuietModeEnabled(paramInt, paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean setRestrictionsChallenge(String paramString)
  {
    return false;
  }
  
  public void setSeedAccountData(int paramInt, String paramString1, String paramString2, PersistableBundle paramPersistableBundle)
  {
    try
    {
      this.mService.setSeedAccountData(paramInt, paramString1, paramString2, paramPersistableBundle, true);
      return;
    }
    catch (RemoteException paramString1)
    {
      throw paramString1.rethrowFromSystemServer();
    }
  }
  
  public void setUserAccount(int paramInt, String paramString)
  {
    try
    {
      this.mService.setUserAccount(paramInt, paramString);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void setUserEnabled(int paramInt)
  {
    try
    {
      this.mService.setUserEnabled(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void setUserIcon(int paramInt, Bitmap paramBitmap)
  {
    try
    {
      this.mService.setUserIcon(paramInt, paramBitmap);
      return;
    }
    catch (RemoteException paramBitmap)
    {
      throw paramBitmap.rethrowFromSystemServer();
    }
  }
  
  public void setUserName(int paramInt, String paramString)
  {
    try
    {
      this.mService.setUserName(paramInt, paramString);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  @Deprecated
  public void setUserRestriction(String paramString, boolean paramBoolean)
  {
    setUserRestriction(paramString, paramBoolean, Process.myUserHandle());
  }
  
  @Deprecated
  public void setUserRestriction(String paramString, boolean paramBoolean, UserHandle paramUserHandle)
  {
    try
    {
      this.mService.setUserRestriction(paramString, paramBoolean, paramUserHandle.getIdentifier());
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  @Deprecated
  public void setUserRestrictions(Bundle paramBundle)
  {
    throw new UnsupportedOperationException("This method is no longer supported");
  }
  
  @Deprecated
  public void setUserRestrictions(Bundle paramBundle, UserHandle paramUserHandle)
  {
    throw new UnsupportedOperationException("This method is no longer supported");
  }
  
  public boolean someUserHasSeedAccount(String paramString1, String paramString2)
  {
    try
    {
      boolean bool = this.mService.someUserHasSeedAccount(paramString1, paramString2);
      return bool;
    }
    catch (RemoteException paramString1)
    {
      throw paramString1.rethrowFromSystemServer();
    }
  }
  
  public boolean trySetQuietModeDisabled(int paramInt, IntentSender paramIntentSender)
  {
    try
    {
      boolean bool = this.mService.trySetQuietModeDisabled(paramInt, paramIntentSender);
      return bool;
    }
    catch (RemoteException paramIntentSender)
    {
      throw paramIntentSender.rethrowFromSystemServer();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/UserManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */