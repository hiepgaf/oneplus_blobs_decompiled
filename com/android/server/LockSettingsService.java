package com.android.server;

import android.app.KeyguardManager;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.app.backup.BackupManager;
import android.app.trust.IStrongAuthTracker;
import android.app.trust.TrustManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.IProgressListener.Stub;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.IMountService;
import android.os.storage.IMountService.Stub;
import android.os.storage.StorageManager;
import android.provider.Settings.Secure;
import android.provider.Settings.SettingNotFoundException;
import android.security.keystore.AndroidKeyStoreProvider;
import android.service.gatekeeper.GateKeeperResponse;
import android.service.gatekeeper.IGateKeeperService;
import android.service.gatekeeper.IGateKeeperService.Stub;
import android.text.TextUtils;
import android.util.Log;
import android.util.Slog;
import com.android.internal.util.ArrayUtils;
import com.android.internal.widget.ICheckCredentialProgressCallback;
import com.android.internal.widget.ILockSettings.Stub;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.LockPatternUtils.StrongAuthTracker;
import com.android.internal.widget.VerifyCredentialResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import libcore.util.HexEncoding;

public class LockSettingsService
  extends ILockSettings.Stub
{
  private static final Intent ACTION_NULL;
  private static final boolean DEBUG = false;
  private static final String DEFAULT_PASSWORD = "default_password";
  private static final int FBE_ENCRYPTED_NOTIFICATION = 0;
  private static final String PERMISSION = "android.permission.ACCESS_KEYGUARD_SECURE_STORAGE";
  private static final int PROFILE_KEY_IV_SIZE = 12;
  private static final String[] READ_CONTACTS_PROTECTED_SETTINGS = { "lock_screen_owner_info_enabled", "lock_screen_owner_info" };
  private static final String[] READ_PASSWORD_PROTECTED_SETTINGS = { "lockscreen.password_salt", "lockscreen.passwordhistory", "lockscreen.password_type", "lockscreen.profilechallenge" };
  private static final String SEPARATE_PROFILE_CHALLENGE_KEY = "lockscreen.profilechallenge";
  private static final String[] SETTINGS_TO_BACKUP = { "lock_screen_owner_info_enabled", "lock_screen_owner_info" };
  private static final int[] SYSTEM_CREDENTIAL_UIDS;
  private static final String TAG = "LockSettingsService";
  private static final String[] VALID_SETTINGS;
  private static String mSavePassword = "default_password";
  private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      int i;
      if ("android.intent.action.USER_ADDED".equals(paramAnonymousIntent.getAction()))
      {
        int j = paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", 0);
        if (j > 0) {
          LockSettingsService.-wrap1(LockSettingsService.this, j, true);
        }
        paramAnonymousContext = android.security.KeyStore.getInstance();
        paramAnonymousIntent = LockSettingsService.-get5(LockSettingsService.this).getProfileParent(j);
        if (paramAnonymousIntent != null)
        {
          i = paramAnonymousIntent.id;
          paramAnonymousContext.onUserAdded(j, i);
        }
      }
      do
      {
        do
        {
          return;
          i = -1;
          break;
          if ("android.intent.action.USER_STARTING".equals(paramAnonymousIntent.getAction()))
          {
            i = paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", 0);
            LockSettingsService.-get3(LockSettingsService.this).prefetchUser(i);
            return;
          }
        } while (!"android.intent.action.USER_REMOVED".equals(paramAnonymousIntent.getAction()));
        i = paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", 0);
      } while (i <= 0);
      LockSettingsService.-wrap1(LockSettingsService.this, i, false);
    }
  };
  private final Context mContext;
  private boolean mFirstCallToVold;
  private IGateKeeperService mGateKeeperService;
  private final Handler mHandler;
  private final android.security.KeyStore mKeyStore = android.security.KeyStore.getInstance();
  private LockPatternUtils mLockPatternUtils;
  private NotificationManager mNotificationManager;
  private final Object mSeparateChallengeLock = new Object();
  private final LockSettingsStorage mStorage;
  private final LockSettingsStrongAuth mStrongAuth;
  private final SynchronizedStrongAuthTracker mStrongAuthTracker;
  private UserManager mUserManager;
  
  static
  {
    SYSTEM_CREDENTIAL_UIDS = new int[] { 1010, 1016, 0, 1000 };
    ACTION_NULL = new Intent("android.intent.action.MAIN");
    ACTION_NULL.addCategory("android.intent.category.HOME");
    VALID_SETTINGS = new String[] { "lockscreen.lockedoutpermanently", "lockscreen.lockoutattemptdeadline", "lockscreen.patterneverchosen", "lockscreen.password_type", "lockscreen.password_type_alternate", "lockscreen.password_salt", "lockscreen.disabled", "lockscreen.options", "lockscreen.biometric_weak_fallback", "lockscreen.biometricweakeverchosen", "lockscreen.power_button_instantly_locks", "lockscreen.passwordhistory", "lock_pattern_autolock", "lock_biometric_weak_flags", "lock_pattern_visible_pattern", "lock_pattern_tactile_feedback_enabled" };
  }
  
  public LockSettingsService(Context paramContext)
  {
    this.mContext = paramContext;
    this.mHandler = new Handler();
    this.mStrongAuth = new LockSettingsStrongAuth(paramContext);
    this.mLockPatternUtils = new LockPatternUtils(paramContext);
    this.mFirstCallToVold = true;
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.USER_ADDED");
    localIntentFilter.addAction("android.intent.action.USER_STARTING");
    localIntentFilter.addAction("android.intent.action.USER_REMOVED");
    this.mContext.registerReceiverAsUser(this.mBroadcastReceiver, UserHandle.ALL, localIntentFilter, null, null);
    this.mStorage = new LockSettingsStorage(paramContext, new LockSettingsStorage.Callback()
    {
      public void initialize(SQLiteDatabase paramAnonymousSQLiteDatabase)
      {
        if (SystemProperties.getBoolean("ro.lockscreen.disable.default", false)) {
          LockSettingsService.-get3(LockSettingsService.this).writeKeyValue(paramAnonymousSQLiteDatabase, "lockscreen.disabled", "1", 0);
        }
      }
    });
    this.mNotificationManager = ((NotificationManager)this.mContext.getSystemService("notification"));
    this.mUserManager = ((UserManager)this.mContext.getSystemService("user"));
    this.mStrongAuthTracker = new SynchronizedStrongAuthTracker(this.mContext);
    this.mStrongAuthTracker.register();
  }
  
  private void addUserKeyAuth(int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws RemoteException
  {
    UserInfo localUserInfo = UserManager.get(this.mContext).getUserInfo(paramInt);
    IMountService localIMountService = getMountService();
    long l = Binder.clearCallingIdentity();
    try
    {
      localIMountService.addUserKeyAuth(paramInt, localUserInfo.serialNumber, paramArrayOfByte1, paramArrayOfByte2);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private boolean checkCryptKeeperPermissions()
  {
    try
    {
      this.mContext.enforceCallingOrSelfPermission("android.permission.CRYPT_KEEPER", "no permission to get the password");
      return false;
    }
    catch (SecurityException localSecurityException) {}
    return true;
  }
  
  private final void checkPasswordReadPermission(int paramInt)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.ACCESS_KEYGUARD_SECURE_STORAGE", "LockSettingsRead");
  }
  
  private final void checkReadPermission(String paramString, int paramInt)
  {
    int j = Binder.getCallingUid();
    int i = 0;
    while (i < READ_CONTACTS_PROTECTED_SETTINGS.length)
    {
      if ((READ_CONTACTS_PROTECTED_SETTINGS[i].equals(paramString)) && (this.mContext.checkCallingOrSelfPermission("android.permission.READ_CONTACTS") != 0)) {
        throw new SecurityException("uid=" + j + " needs permission " + "android.permission.READ_CONTACTS" + " to read " + paramString + " for user " + paramInt);
      }
      i += 1;
    }
    i = 0;
    while (i < READ_PASSWORD_PROTECTED_SETTINGS.length)
    {
      if ((READ_PASSWORD_PROTECTED_SETTINGS[i].equals(paramString)) && (this.mContext.checkCallingOrSelfPermission("android.permission.ACCESS_KEYGUARD_SECURE_STORAGE") != 0)) {
        throw new SecurityException("uid=" + j + " needs permission " + "android.permission.ACCESS_KEYGUARD_SECURE_STORAGE" + " to read " + paramString + " for user " + paramInt);
      }
      i += 1;
    }
  }
  
  private final void checkWritePermission(int paramInt)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.ACCESS_KEYGUARD_SECURE_STORAGE", "LockSettingsWrite");
  }
  
  private void clearUserKeyProtection(int paramInt)
    throws RemoteException
  {
    addUserKeyAuth(paramInt, null, null);
  }
  
  private VerifyCredentialResponse doVerifyPassword(String paramString, LockSettingsStorage.CredentialHash paramCredentialHash, boolean paramBoolean, long paramLong, int paramInt, ICheckCredentialProgressCallback paramICheckCredentialProgressCallback)
    throws RemoteException
  {
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("Password can't be null or empty");
    }
    verifyCredential(paramInt, paramCredentialHash, paramString, paramBoolean, paramLong, new CredentialUtil()
    {
      public String adjustForKeystore(String paramAnonymousString)
      {
        return paramAnonymousString;
      }
      
      public void setCredential(String paramAnonymousString1, String paramAnonymousString2, int paramAnonymousInt)
        throws RemoteException
      {
        LockSettingsService.-wrap2(LockSettingsService.this, paramAnonymousString1, paramAnonymousString2, paramAnonymousInt);
      }
      
      public byte[] toHash(String paramAnonymousString, int paramAnonymousInt)
      {
        return LockSettingsService.-get2(LockSettingsService.this).passwordToHash(paramAnonymousString, paramAnonymousInt);
      }
    }, paramICheckCredentialProgressCallback);
  }
  
  private VerifyCredentialResponse doVerifyPassword(String paramString, boolean paramBoolean, long paramLong, int paramInt, ICheckCredentialProgressCallback paramICheckCredentialProgressCallback)
    throws RemoteException
  {
    checkPasswordReadPermission(paramInt);
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("Password can't be null or empty");
    }
    return doVerifyPassword(paramString, this.mStorage.readPasswordHash(paramInt), paramBoolean, paramLong, paramInt, paramICheckCredentialProgressCallback);
  }
  
  private VerifyCredentialResponse doVerifyPattern(String paramString, LockSettingsStorage.CredentialHash paramCredentialHash, boolean paramBoolean, long paramLong, int paramInt, ICheckCredentialProgressCallback paramICheckCredentialProgressCallback)
    throws RemoteException
  {
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("Pattern can't be null or empty");
    }
    boolean bool;
    if (paramCredentialHash != null)
    {
      bool = paramCredentialHash.isBaseZeroPattern;
      if (!bool) {
        break label91;
      }
    }
    label91:
    for (String str = LockPatternUtils.patternStringToBaseZero(paramString);; str = paramString)
    {
      paramCredentialHash = verifyCredential(paramInt, paramCredentialHash, str, paramBoolean, paramLong, new CredentialUtil()
      {
        public String adjustForKeystore(String paramAnonymousString)
        {
          return LockPatternUtils.patternStringToBaseZero(paramAnonymousString);
        }
        
        public void setCredential(String paramAnonymousString1, String paramAnonymousString2, int paramAnonymousInt)
          throws RemoteException
        {
          LockSettingsService.-wrap3(LockSettingsService.this, paramAnonymousString1, paramAnonymousString2, paramAnonymousInt);
        }
        
        public byte[] toHash(String paramAnonymousString, int paramAnonymousInt)
        {
          return LockPatternUtils.patternToHash(LockPatternUtils.stringToPattern(paramAnonymousString));
        }
      }, paramICheckCredentialProgressCallback);
      if ((paramCredentialHash.getResponseCode() == 0) && (bool)) {
        setLockPatternInternal(paramString, str, paramInt);
      }
      return paramCredentialHash;
      bool = false;
      break;
    }
  }
  
  private VerifyCredentialResponse doVerifyPattern(String paramString, boolean paramBoolean, long paramLong, int paramInt, ICheckCredentialProgressCallback paramICheckCredentialProgressCallback)
    throws RemoteException
  {
    checkPasswordReadPermission(paramInt);
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("Pattern can't be null or empty");
    }
    return doVerifyPattern(paramString, this.mStorage.readPatternHash(paramInt), paramBoolean, paramLong, paramInt, paramICheckCredentialProgressCallback);
  }
  
  private byte[] enrollCredential(byte[] paramArrayOfByte, String paramString1, String paramString2, int paramInt)
    throws RemoteException
  {
    checkWritePermission(paramInt);
    if (paramString1 == null)
    {
      paramString1 = null;
      if (paramString2 != null) {
        break label49;
      }
    }
    label49:
    for (byte[] arrayOfByte = null;; arrayOfByte = paramString2.getBytes())
    {
      paramArrayOfByte = getGateKeeperService().enroll(paramInt, paramArrayOfByte, paramString1, arrayOfByte);
      if (paramArrayOfByte != null) {
        break label58;
      }
      return null;
      paramString1 = paramString1.getBytes();
      break;
    }
    label58:
    paramArrayOfByte = paramArrayOfByte.getPayload();
    if (paramArrayOfByte != null)
    {
      setKeystorePassword(paramString2, paramInt);
      return paramArrayOfByte;
    }
    Slog.e("LockSettingsService", "Throttled while enrolling a password");
    return paramArrayOfByte;
  }
  
  private void fixateNewestUserKeyAuth(int paramInt)
    throws RemoteException
  {
    IMountService localIMountService = getMountService();
    long l = Binder.clearCallingIdentity();
    try
    {
      localIMountService.fixateNewestUserKeyAuth(paramInt);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private byte[] getCurrentHandle(int paramInt)
  {
    int i = this.mStorage.getStoredCredentialType(paramInt);
    Object localObject;
    switch (i)
    {
    default: 
      localObject = null;
    }
    for (;;)
    {
      if ((i != -1) && (localObject == null)) {
        Slog.e("LockSettingsService", "Stored handle type [" + i + "] but no handle available");
      }
      return (byte[])localObject;
      localObject = this.mStorage.readPatternHash(paramInt);
      if (localObject != null)
      {
        localObject = ((LockSettingsStorage.CredentialHash)localObject).hash;
      }
      else
      {
        localObject = null;
        continue;
        localObject = this.mStorage.readPasswordHash(paramInt);
        if (localObject != null) {
          localObject = ((LockSettingsStorage.CredentialHash)localObject).hash;
        } else {
          localObject = null;
        }
      }
    }
  }
  
  private String getDecryptedPasswordForTiedProfile(int paramInt)
    throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, CertificateException, IOException
  {
    byte[] arrayOfByte2 = this.mStorage.readChildProfileLock(paramInt);
    if (arrayOfByte2 == null) {
      throw new FileNotFoundException("Child profile lock file not found");
    }
    byte[] arrayOfByte1 = Arrays.copyOfRange(arrayOfByte2, 0, 12);
    arrayOfByte2 = Arrays.copyOfRange(arrayOfByte2, 12, arrayOfByte2.length);
    Object localObject = java.security.KeyStore.getInstance("AndroidKeyStore");
    ((java.security.KeyStore)localObject).load(null);
    localObject = (SecretKey)((java.security.KeyStore)localObject).getKey("profile_key_name_decrypt_" + paramInt, null);
    Cipher localCipher = Cipher.getInstance("AES/GCM/NoPadding");
    localCipher.init(2, (Key)localObject, new GCMParameterSpec(128, arrayOfByte1));
    return new String(localCipher.doFinal(arrayOfByte2), StandardCharsets.UTF_8);
  }
  
  private IGateKeeperService getGateKeeperService()
    throws RemoteException
  {
    try
    {
      if (this.mGateKeeperService != null)
      {
        localObject1 = this.mGateKeeperService;
        return (IGateKeeperService)localObject1;
      }
      Object localObject1 = ServiceManager.getService("android.service.gatekeeper.IGateKeeperService");
      if (localObject1 != null)
      {
        ((IBinder)localObject1).linkToDeath(new GateKeeperDiedRecipient(null), 0);
        this.mGateKeeperService = IGateKeeperService.Stub.asInterface((IBinder)localObject1);
        localObject1 = this.mGateKeeperService;
        return (IGateKeeperService)localObject1;
      }
      Slog.e("LockSettingsService", "Unable to acquire GateKeeperService");
      return null;
    }
    finally {}
  }
  
  private IMountService getMountService()
  {
    IBinder localIBinder = ServiceManager.getService("mount");
    if (localIBinder != null) {
      return IMountService.Stub.asInterface(localIBinder);
    }
    return null;
  }
  
  private boolean isManagedProfileWithSeparatedLock(int paramInt)
  {
    if (this.mUserManager.getUserInfo(paramInt).isManagedProfile()) {
      return this.mLockPatternUtils.isSeparateProfileChallengeEnabled(paramInt);
    }
    return false;
  }
  
  private boolean isManagedProfileWithUnifiedLock(int paramInt)
  {
    return (this.mUserManager.getUserInfo(paramInt).isManagedProfile()) && (!this.mLockPatternUtils.isSeparateProfileChallengeEnabled(paramInt));
  }
  
  private void maybeShowEncryptionNotifications()
  {
    List localList = this.mUserManager.getUsers();
    int i = 0;
    if (i < localList.size())
    {
      UserInfo localUserInfo = (UserInfo)localList.get(i);
      UserHandle localUserHandle = localUserInfo.getUserHandle();
      boolean bool;
      if (!this.mStorage.hasPassword(localUserInfo.id))
      {
        bool = this.mStorage.hasPattern(localUserInfo.id);
        label67:
        if ((bool) && (!this.mUserManager.isUserUnlockingOrUnlocked(localUserHandle))) {
          break label95;
        }
      }
      for (;;)
      {
        i += 1;
        break;
        bool = true;
        break label67;
        label95:
        if (localUserInfo.isManagedProfile())
        {
          localUserInfo = this.mUserManager.getProfileParent(localUserInfo.id);
          if ((localUserInfo != null) && (this.mUserManager.isUserUnlockingOrUnlocked(localUserInfo.getUserHandle())) && (!this.mUserManager.isQuietModeEnabled(localUserHandle))) {
            showEncryptionNotificationForProfile(localUserHandle);
          }
        }
      }
    }
  }
  
  private void migrateOldData()
  {
    Object localObject1;
    int i;
    int j;
    Object localObject2;
    int k;
    try
    {
      if (getString("migrated", null, 0) == null)
      {
        localContentResolver = this.mContext.getContentResolver();
        localObject1 = VALID_SETTINGS;
        i = 0;
        j = localObject1.length;
        if (i < j)
        {
          localObject2 = localObject1[i];
          String str = Settings.Secure.getString(localContentResolver, (String)localObject2);
          if (str == null) {
            break label755;
          }
          setString((String)localObject2, str, 0);
          break label755;
        }
        setString("migrated", "true", 0);
        Slog.i("LockSettingsService", "Migrated lock settings to new location");
      }
      if (getString("migrated_user_specific", null, 0) != null) {
        break label283;
      }
      ContentResolver localContentResolver = this.mContext.getContentResolver();
      localObject1 = this.mUserManager.getUsers();
      i = 0;
      while (i < ((List)localObject1).size())
      {
        k = ((UserInfo)((List)localObject1).get(i)).id;
        localObject2 = Settings.Secure.getStringForUser(localContentResolver, "lock_screen_owner_info", k);
        if (!TextUtils.isEmpty((CharSequence)localObject2))
        {
          setString("lock_screen_owner_info", (String)localObject2, k);
          Settings.Secure.putStringForUser(localContentResolver, "lock_screen_owner_info", "", k);
        }
        try
        {
          if (Settings.Secure.getIntForUser(localContentResolver, "lock_screen_owner_info_enabled", k) == 0) {
            break label771;
          }
          j = 1;
        }
        catch (Settings.SettingNotFoundException localSettingNotFoundException)
        {
          for (;;)
          {
            label202:
            if (!TextUtils.isEmpty((CharSequence)localObject2)) {
              setLong("lock_screen_owner_info_enabled", 1L, k);
            }
          }
        }
        setLong("lock_screen_owner_info_enabled", j, k);
        Settings.Secure.putIntForUser(localContentResolver, "lock_screen_owner_info_enabled", 0, k);
        i += 1;
        continue;
        return;
      }
    }
    catch (RemoteException localRemoteException)
    {
      Slog.e("LockSettingsService", "Unable to migrate old data", localRemoteException);
    }
    setString("migrated_user_specific", "true", 0);
    Slog.i("LockSettingsService", "Migrated per-user lock settings to new location");
    label283:
    List localList;
    long l1;
    if (getString("migrated_biometric_weak", null, 0) == null)
    {
      localList = this.mUserManager.getUsers();
      i = 0;
      while (i < localList.size())
      {
        j = ((UserInfo)localList.get(i)).id;
        l1 = getLong("lockscreen.password_type", 0L, j);
        long l2 = getLong("lockscreen.password_type_alternate", 0L, j);
        if (l1 == 32768L) {
          setLong("lockscreen.password_type", l2, j);
        }
        setLong("lockscreen.password_type_alternate", 0L, j);
        i += 1;
      }
      setString("migrated_biometric_weak", "true", 0);
      Slog.i("LockSettingsService", "Migrated biometric weak to use the fallback instead");
    }
    int m;
    if (getString("migrated_lockscreen_disabled", null, 0) == null)
    {
      localList = this.mUserManager.getUsers();
      m = localList.size();
      j = 0;
      i = 0;
      label439:
      if (i >= m) {
        break label790;
      }
      k = j;
      if (!((UserInfo)localList.get(i)).supportsSwitchTo()) {
        break label781;
      }
      k = j + 1;
    }
    for (;;)
    {
      if (i < m)
      {
        j = ((UserInfo)localList.get(i)).id;
        if (getBoolean("lockscreen.disabled", false, j)) {
          setBoolean("lockscreen.disabled", false, j);
        }
      }
      else
      {
        label700:
        label755:
        label771:
        label776:
        label781:
        label790:
        do
        {
          setString("migrated_lockscreen_disabled", "true", 0);
          Slog.i("LockSettingsService", "Migrated lockscreen disabled flag");
          localList = this.mUserManager.getUsers();
          i = 0;
          while (i < localList.size())
          {
            localObject1 = (UserInfo)localList.get(i);
            if ((((UserInfo)localObject1).isManagedProfile()) && (this.mStorage.hasChildProfileLock(((UserInfo)localObject1).id)))
            {
              l1 = getLong("lockscreen.password_type", 0L, ((UserInfo)localObject1).id);
              if (l1 != 0L) {
                break label700;
              }
              Slog.i("LockSettingsService", "Migrated tied profile lock type");
              setLong("lockscreen.password_type", 327680L, ((UserInfo)localObject1).id);
            }
            try
            {
              for (;;)
              {
                localObject1 = "profile_key_name_encrypt_" + ((UserInfo)localObject1).id;
                localObject2 = java.security.KeyStore.getInstance("AndroidKeyStore");
                ((java.security.KeyStore)localObject2).load(null);
                if (((java.security.KeyStore)localObject2).containsAlias((String)localObject1)) {
                  ((java.security.KeyStore)localObject2).deleteEntry((String)localObject1);
                }
                i += 1;
                break;
                if (l1 != 327680L) {
                  Slog.e("LockSettingsService", "Invalid tied profile lock type: " + l1);
                }
              }
            }
            catch (KeyStoreException|NoSuchAlgorithmException|CertificateException|IOException localKeyStoreException)
            {
              for (;;)
              {
                Slog.e("LockSettingsService", "Unable to remove tied profile key", localKeyStoreException);
              }
            }
          }
          i += 1;
          break;
          for (;;)
          {
            if (j == 0) {
              break label776;
            }
            j = 1;
            break;
            j = 0;
          }
          j = 0;
          break label202;
          i += 1;
          j = k;
          break label439;
        } while (j <= 1);
        i = 0;
        continue;
      }
      i += 1;
    }
  }
  
  private void notifyActivePasswordMetricsAvailable(final String paramString, final int paramInt)
  {
    final int i = this.mLockPatternUtils.getKeyguardStoredPasswordQuality(paramInt);
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        int i3 = 0;
        int i4 = 0;
        int i1 = 0;
        int i5 = 0;
        int n = 0;
        int i6 = 0;
        int k = 0;
        int i7 = 0;
        int m = 0;
        int i8 = 0;
        int j = 0;
        int i9 = 0;
        int i = 0;
        if (paramString != null)
        {
          int i10 = paramString.length();
          int i2 = 0;
          i3 = i10;
          i4 = i1;
          i5 = n;
          i6 = k;
          i7 = m;
          i8 = j;
          i9 = i;
          if (i2 < i10)
          {
            i3 = paramString.charAt(i2);
            if ((i3 >= 65) && (i3 <= 90))
            {
              i1 += 1;
              n += 1;
            }
            for (;;)
            {
              i2 += 1;
              break;
              if ((i3 >= 97) && (i3 <= 122))
              {
                i1 += 1;
                k += 1;
              }
              else if ((i3 >= 48) && (i3 <= 57))
              {
                m += 1;
                i += 1;
              }
              else
              {
                j += 1;
                i += 1;
              }
            }
          }
        }
        ((DevicePolicyManager)LockSettingsService.-get0(LockSettingsService.this).getSystemService("device_policy")).setActivePasswordState(i, i3, i4, i5, i6, i7, i8, i9, paramInt);
      }
    });
  }
  
  private void notifyPasswordChanged(int paramInt)
  {
    this.mHandler.post(new -void_notifyPasswordChanged_int_userId_LambdaImpl0(paramInt));
  }
  
  private void onUserLockChanged(int paramInt)
    throws RemoteException
  {
    if (this.mUserManager.getUserInfo(paramInt).isManagedProfile()) {
      return;
    }
    boolean bool;
    label56:
    int j;
    if (!this.mStorage.hasPassword(paramInt))
    {
      bool = this.mStorage.hasPattern(paramInt);
      List localList = this.mUserManager.getProfiles(paramInt);
      int i = localList.size();
      paramInt = 0;
      if (paramInt >= i) {
        return;
      }
      UserInfo localUserInfo = (UserInfo)localList.get(paramInt);
      if (localUserInfo.isManagedProfile())
      {
        j = localUserInfo.id;
        if (!this.mLockPatternUtils.isSeparateProfileChallengeEnabled(j)) {
          break label112;
        }
      }
    }
    for (;;)
    {
      paramInt += 1;
      break label56;
      bool = true;
      break;
      label112:
      if (bool)
      {
        tieManagedProfileLockIfNecessary(j, null);
      }
      else
      {
        clearUserKeyProtection(j);
        getGateKeeperService().clearSecureUserId(j);
        this.mStorage.writePatternHash(null, j);
        setKeystorePassword(null, j);
        fixateNewestUserKeyAuth(j);
        this.mStorage.removeChildProfileLock(j);
        removeKeystoreProfileKey(j);
      }
    }
  }
  
  private void removeKeystoreProfileKey(int paramInt)
  {
    try
    {
      java.security.KeyStore localKeyStore = java.security.KeyStore.getInstance("AndroidKeyStore");
      localKeyStore.load(null);
      localKeyStore.deleteEntry("profile_key_name_encrypt_" + paramInt);
      localKeyStore.deleteEntry("profile_key_name_decrypt_" + paramInt);
      return;
    }
    catch (KeyStoreException|NoSuchAlgorithmException|CertificateException|IOException localKeyStoreException)
    {
      Slog.e("LockSettingsService", "Unable to remove keystore profile key for user:" + paramInt, localKeyStoreException);
    }
  }
  
  private void removeUser(int paramInt, boolean paramBoolean)
  {
    this.mStorage.removeUser(paramInt);
    this.mStrongAuth.removeUser(paramInt);
    android.security.KeyStore.getInstance().onUserRemoved(paramInt);
    try
    {
      IGateKeeperService localIGateKeeperService = getGateKeeperService();
      if (localIGateKeeperService != null) {
        localIGateKeeperService.clearSecureUserId(paramInt);
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Slog.w("LockSettingsService", "unable to clear GK secure user id");
      }
    }
    if ((paramBoolean) || (this.mUserManager.getUserInfo(paramInt).isManagedProfile())) {
      removeKeystoreProfileKey(paramInt);
    }
  }
  
  private static byte[] secretFromCredential(String paramString)
    throws RemoteException
  {
    try
    {
      MessageDigest localMessageDigest = MessageDigest.getInstance("SHA-512");
      localMessageDigest.update(Arrays.copyOf("Android FBE credential hash".getBytes(StandardCharsets.UTF_8), 128));
      localMessageDigest.update(paramString.getBytes(StandardCharsets.UTF_8));
      paramString = localMessageDigest.digest();
      return paramString;
    }
    catch (NoSuchAlgorithmException paramString)
    {
      throw new RuntimeException("NoSuchAlgorithmException for SHA-512");
    }
  }
  
  private void setKeystorePassword(String paramString, int paramInt)
  {
    android.security.KeyStore.getInstance().onUserPasswordChanged(paramInt, paramString);
  }
  
  private void setLockPasswordInternal(String paramString1, String paramString2, int paramInt)
    throws RemoteException
  {
    byte[] arrayOfByte = getCurrentHandle(paramInt);
    if (paramString1 == null)
    {
      clearUserKeyProtection(paramInt);
      getGateKeeperService().clearSecureUserId(paramInt);
      this.mStorage.writePasswordHash(null, paramInt);
      setKeystorePassword(null, paramInt);
      fixateNewestUserKeyAuth(paramInt);
      onUserLockChanged(paramInt);
      notifyActivePasswordMetricsAvailable(null, paramInt);
      return;
    }
    if (isManagedProfileWithUnifiedLock(paramInt)) {}
    for (;;)
    {
      try
      {
        String str1 = getDecryptedPasswordForTiedProfile(paramInt);
        paramString2 = enrollCredential(arrayOfByte, str1, paramString1, paramInt);
        if (paramString2 == null) {
          break;
        }
        setUserKeyProtection(paramInt, paramString1, doVerifyPassword(paramString1, new LockSettingsStorage.CredentialHash(paramString2, 1), true, 0L, paramInt, null));
        this.mStorage.writePasswordHash(paramString2, paramInt);
        fixateNewestUserKeyAuth(paramInt);
        onUserLockChanged(paramInt);
        return;
      }
      catch (UnrecoverableKeyException|InvalidKeyException|KeyStoreException|NoSuchAlgorithmException|NoSuchPaddingException|InvalidAlgorithmParameterException|IllegalBlockSizeException|BadPaddingException|CertificateException|IOException localUnrecoverableKeyException)
      {
        Slog.e("LockSettingsService", "Failed to decrypt child profile key", localUnrecoverableKeyException);
        String str2 = paramString2;
        continue;
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        Slog.i("LockSettingsService", "Child profile key not found");
        str3 = paramString2;
        continue;
      }
      String str3 = paramString2;
      if (arrayOfByte == null)
      {
        if (paramString2 != null) {
          Slog.w("LockSettingsService", "Saved credential provided, but none stored");
        }
        str3 = null;
      }
    }
    throw new RemoteException("Failed to enroll password");
  }
  
  private void setLockPatternInternal(String paramString1, String paramString2, int paramInt)
    throws RemoteException
  {
    byte[] arrayOfByte = getCurrentHandle(paramInt);
    if (paramString1 == null)
    {
      clearUserKeyProtection(paramInt);
      getGateKeeperService().clearSecureUserId(paramInt);
      this.mStorage.writePatternHash(null, paramInt);
      setKeystorePassword(null, paramInt);
      fixateNewestUserKeyAuth(paramInt);
      onUserLockChanged(paramInt);
      notifyActivePasswordMetricsAvailable(null, paramInt);
      return;
    }
    if (isManagedProfileWithUnifiedLock(paramInt)) {}
    for (;;)
    {
      try
      {
        String str1 = getDecryptedPasswordForTiedProfile(paramInt);
        paramString2 = enrollCredential(arrayOfByte, str1, paramString1, paramInt);
        if (paramString2 == null) {
          break;
        }
        setUserKeyProtection(paramInt, paramString1, doVerifyPattern(paramString1, new LockSettingsStorage.CredentialHash(paramString2, 1), true, 0L, paramInt, null));
        this.mStorage.writePatternHash(paramString2, paramInt);
        fixateNewestUserKeyAuth(paramInt);
        onUserLockChanged(paramInt);
        return;
      }
      catch (UnrecoverableKeyException|InvalidKeyException|KeyStoreException|NoSuchAlgorithmException|NoSuchPaddingException|InvalidAlgorithmParameterException|IllegalBlockSizeException|BadPaddingException|CertificateException|IOException localUnrecoverableKeyException)
      {
        if ((localUnrecoverableKeyException instanceof FileNotFoundException))
        {
          Slog.i("LockSettingsService", "Child profile key not found");
          str2 = paramString2;
          continue;
        }
        Slog.e("LockSettingsService", "Failed to decrypt child profile key", str2);
        str2 = paramString2;
        continue;
      }
      String str2 = paramString2;
      if (arrayOfByte == null)
      {
        if (paramString2 != null) {
          Slog.w("LockSettingsService", "Saved credential provided, but none stored");
        }
        str2 = null;
      }
    }
    throw new RemoteException("Failed to enroll pattern");
  }
  
  private void setStringUnchecked(String paramString1, int paramInt, String paramString2)
  {
    this.mStorage.writeKeyValue(paramString1, paramString2, paramInt);
    if (ArrayUtils.contains(SETTINGS_TO_BACKUP, paramString1)) {
      BackupManager.dataChanged("com.android.providers.settings");
    }
  }
  
  private void setUserKeyProtection(int paramInt, String paramString, VerifyCredentialResponse paramVerifyCredentialResponse)
    throws RemoteException
  {
    if (paramVerifyCredentialResponse == null) {
      throw new RemoteException("Null response verifying a credential we just set");
    }
    if (paramVerifyCredentialResponse.getResponseCode() != 0) {
      throw new RemoteException("Non-OK response verifying a credential we just set: " + paramVerifyCredentialResponse.getResponseCode());
    }
    paramVerifyCredentialResponse = paramVerifyCredentialResponse.getPayload();
    if (paramVerifyCredentialResponse == null) {
      throw new RemoteException("Empty payload verifying a credential we just set");
    }
    addUserKeyAuth(paramInt, paramVerifyCredentialResponse, secretFromCredential(paramString));
  }
  
  private void showEncryptionNotification(UserHandle paramUserHandle, CharSequence paramCharSequence1, CharSequence paramCharSequence2, CharSequence paramCharSequence3, PendingIntent paramPendingIntent)
  {
    if (!StorageManager.isFileEncryptedNativeOrEmulated()) {
      return;
    }
    paramCharSequence1 = new Notification.Builder(this.mContext).setSmallIcon(17302608).setWhen(0L).setOngoing(true).setTicker(paramCharSequence1).setDefaults(0).setPriority(2).setColor(this.mContext.getColor(17170523)).setContentTitle(paramCharSequence1).setContentText(paramCharSequence2).setSubText(paramCharSequence3).setVisibility(1).setContentIntent(paramPendingIntent).build();
    this.mNotificationManager.notifyAsUser(null, 0, paramCharSequence1, paramUserHandle);
  }
  
  private void showEncryptionNotificationForProfile(UserHandle paramUserHandle)
  {
    Object localObject = this.mContext.getResources();
    CharSequence localCharSequence1 = ((Resources)localObject).getText(17040897);
    CharSequence localCharSequence2 = ((Resources)localObject).getText(17040901);
    localObject = ((Resources)localObject).getText(17040900);
    Intent localIntent = ((KeyguardManager)this.mContext.getSystemService("keyguard")).createConfirmDeviceCredentialIntent(null, null, paramUserHandle.getIdentifier());
    if (localIntent == null) {
      return;
    }
    localIntent.setFlags(276824064);
    showEncryptionNotification(paramUserHandle, localCharSequence1, localCharSequence2, (CharSequence)localObject, PendingIntent.getActivity(this.mContext, 0, localIntent, 134217728));
  }
  
  /* Error */
  private void tieProfileLockToParent(int paramInt, String paramString)
  {
    // Byte code:
    //   0: aload_2
    //   1: getstatic 570	java/nio/charset/StandardCharsets:UTF_8	Ljava/nio/charset/Charset;
    //   4: invokevirtual 827	java/lang/String:getBytes	(Ljava/nio/charset/Charset;)[B
    //   7: astore_3
    //   8: ldc_w 1011
    //   11: invokestatic 1016	javax/crypto/KeyGenerator:getInstance	(Ljava/lang/String;)Ljavax/crypto/KeyGenerator;
    //   14: astore_2
    //   15: aload_2
    //   16: new 1018	java/security/SecureRandom
    //   19: dup
    //   20: invokespecial 1019	java/security/SecureRandom:<init>	()V
    //   23: invokevirtual 1022	javax/crypto/KeyGenerator:init	(Ljava/security/SecureRandom;)V
    //   26: aload_2
    //   27: invokevirtual 1026	javax/crypto/KeyGenerator:generateKey	()Ljavax/crypto/SecretKey;
    //   30: astore 4
    //   32: ldc_w 527
    //   35: invokestatic 532	java/security/KeyStore:getInstance	(Ljava/lang/String;)Ljava/security/KeyStore;
    //   38: astore_2
    //   39: aload_2
    //   40: aconst_null
    //   41: invokevirtual 536	java/security/KeyStore:load	(Ljava/security/KeyStore$LoadStoreParameter;)V
    //   44: aload_2
    //   45: new 361	java/lang/StringBuilder
    //   48: dup
    //   49: invokespecial 362	java/lang/StringBuilder:<init>	()V
    //   52: ldc_w 746
    //   55: invokevirtual 368	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   58: iload_1
    //   59: invokevirtual 371	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   62: invokevirtual 381	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   65: new 1028	java/security/KeyStore$SecretKeyEntry
    //   68: dup
    //   69: aload 4
    //   71: invokespecial 1031	java/security/KeyStore$SecretKeyEntry:<init>	(Ljavax/crypto/SecretKey;)V
    //   74: new 1033	android/security/keystore/KeyProtection$Builder
    //   77: dup
    //   78: iconst_1
    //   79: invokespecial 1035	android/security/keystore/KeyProtection$Builder:<init>	(I)V
    //   82: iconst_1
    //   83: anewarray 160	java/lang/String
    //   86: dup
    //   87: iconst_0
    //   88: ldc_w 1037
    //   91: aastore
    //   92: invokevirtual 1041	android/security/keystore/KeyProtection$Builder:setBlockModes	([Ljava/lang/String;)Landroid/security/keystore/KeyProtection$Builder;
    //   95: iconst_1
    //   96: anewarray 160	java/lang/String
    //   99: dup
    //   100: iconst_0
    //   101: ldc_w 1043
    //   104: aastore
    //   105: invokevirtual 1046	android/security/keystore/KeyProtection$Builder:setEncryptionPaddings	([Ljava/lang/String;)Landroid/security/keystore/KeyProtection$Builder;
    //   108: invokevirtual 1049	android/security/keystore/KeyProtection$Builder:build	()Landroid/security/keystore/KeyProtection;
    //   111: invokevirtual 1053	java/security/KeyStore:setEntry	(Ljava/lang/String;Ljava/security/KeyStore$Entry;Ljava/security/KeyStore$ProtectionParameter;)V
    //   114: aload_2
    //   115: new 361	java/lang/StringBuilder
    //   118: dup
    //   119: invokespecial 362	java/lang/StringBuilder:<init>	()V
    //   122: ldc_w 538
    //   125: invokevirtual 368	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   128: iload_1
    //   129: invokevirtual 371	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   132: invokevirtual 381	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   135: new 1028	java/security/KeyStore$SecretKeyEntry
    //   138: dup
    //   139: aload 4
    //   141: invokespecial 1031	java/security/KeyStore$SecretKeyEntry:<init>	(Ljavax/crypto/SecretKey;)V
    //   144: new 1033	android/security/keystore/KeyProtection$Builder
    //   147: dup
    //   148: iconst_2
    //   149: invokespecial 1035	android/security/keystore/KeyProtection$Builder:<init>	(I)V
    //   152: iconst_1
    //   153: anewarray 160	java/lang/String
    //   156: dup
    //   157: iconst_0
    //   158: ldc_w 1037
    //   161: aastore
    //   162: invokevirtual 1041	android/security/keystore/KeyProtection$Builder:setBlockModes	([Ljava/lang/String;)Landroid/security/keystore/KeyProtection$Builder;
    //   165: iconst_1
    //   166: anewarray 160	java/lang/String
    //   169: dup
    //   170: iconst_0
    //   171: ldc_w 1043
    //   174: aastore
    //   175: invokevirtual 1046	android/security/keystore/KeyProtection$Builder:setEncryptionPaddings	([Ljava/lang/String;)Landroid/security/keystore/KeyProtection$Builder;
    //   178: iconst_1
    //   179: invokevirtual 1057	android/security/keystore/KeyProtection$Builder:setUserAuthenticationRequired	(Z)Landroid/security/keystore/KeyProtection$Builder;
    //   182: bipush 30
    //   184: invokevirtual 1061	android/security/keystore/KeyProtection$Builder:setUserAuthenticationValidityDurationSeconds	(I)Landroid/security/keystore/KeyProtection$Builder;
    //   187: invokevirtual 1049	android/security/keystore/KeyProtection$Builder:build	()Landroid/security/keystore/KeyProtection;
    //   190: invokevirtual 1053	java/security/KeyStore:setEntry	(Ljava/lang/String;Ljava/security/KeyStore$Entry;Ljava/security/KeyStore$ProtectionParameter;)V
    //   193: aload_2
    //   194: new 361	java/lang/StringBuilder
    //   197: dup
    //   198: invokespecial 362	java/lang/StringBuilder:<init>	()V
    //   201: ldc_w 746
    //   204: invokevirtual 368	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   207: iload_1
    //   208: invokevirtual 371	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   211: invokevirtual 381	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   214: aconst_null
    //   215: invokevirtual 542	java/security/KeyStore:getKey	(Ljava/lang/String;[C)Ljava/security/Key;
    //   218: checkcast 544	javax/crypto/SecretKey
    //   221: astore 5
    //   223: ldc_w 546
    //   226: invokestatic 551	javax/crypto/Cipher:getInstance	(Ljava/lang/String;)Ljavax/crypto/Cipher;
    //   229: astore 4
    //   231: aload 4
    //   233: iconst_1
    //   234: aload 5
    //   236: invokevirtual 1064	javax/crypto/Cipher:init	(ILjava/security/Key;)V
    //   239: aload 4
    //   241: aload_3
    //   242: invokevirtual 564	javax/crypto/Cipher:doFinal	([B)[B
    //   245: astore_3
    //   246: aload 4
    //   248: invokevirtual 1067	javax/crypto/Cipher:getIV	()[B
    //   251: astore 4
    //   253: aload_2
    //   254: new 361	java/lang/StringBuilder
    //   257: dup
    //   258: invokespecial 362	java/lang/StringBuilder:<init>	()V
    //   261: ldc_w 746
    //   264: invokevirtual 368	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   267: iload_1
    //   268: invokevirtual 371	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   271: invokevirtual 381	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   274: invokevirtual 753	java/security/KeyStore:deleteEntry	(Ljava/lang/String;)V
    //   277: new 1069	java/io/ByteArrayOutputStream
    //   280: dup
    //   281: invokespecial 1070	java/io/ByteArrayOutputStream:<init>	()V
    //   284: astore_2
    //   285: aload 4
    //   287: arraylength
    //   288: bipush 12
    //   290: if_icmpeq +86 -> 376
    //   293: new 840	java/lang/RuntimeException
    //   296: dup
    //   297: new 361	java/lang/StringBuilder
    //   300: dup
    //   301: invokespecial 362	java/lang/StringBuilder:<init>	()V
    //   304: ldc_w 1072
    //   307: invokevirtual 368	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   310: aload 4
    //   312: arraylength
    //   313: invokevirtual 371	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   316: invokevirtual 381	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   319: invokespecial 843	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   322: athrow
    //   323: astore_2
    //   324: new 840	java/lang/RuntimeException
    //   327: dup
    //   328: ldc_w 1074
    //   331: aload_2
    //   332: invokespecial 1077	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   335: athrow
    //   336: astore_3
    //   337: aload_2
    //   338: new 361	java/lang/StringBuilder
    //   341: dup
    //   342: invokespecial 362	java/lang/StringBuilder:<init>	()V
    //   345: ldc_w 746
    //   348: invokevirtual 368	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   351: iload_1
    //   352: invokevirtual 371	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   355: invokevirtual 381	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   358: invokevirtual 753	java/security/KeyStore:deleteEntry	(Ljava/lang/String;)V
    //   361: aload_3
    //   362: athrow
    //   363: astore_2
    //   364: new 840	java/lang/RuntimeException
    //   367: dup
    //   368: ldc_w 1079
    //   371: aload_2
    //   372: invokespecial 1077	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   375: athrow
    //   376: aload_2
    //   377: aload 4
    //   379: invokevirtual 1082	java/io/ByteArrayOutputStream:write	([B)V
    //   382: aload_2
    //   383: aload_3
    //   384: invokevirtual 1082	java/io/ByteArrayOutputStream:write	([B)V
    //   387: aload_0
    //   388: getfield 105	com/android/server/LockSettingsService:mStorage	Lcom/android/server/LockSettingsStorage;
    //   391: iload_1
    //   392: aload_2
    //   393: invokevirtual 1085	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   396: invokevirtual 1088	com/android/server/LockSettingsStorage:writeChildProfileLock	(I[B)V
    //   399: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	400	0	this	LockSettingsService
    //   0	400	1	paramInt	int
    //   0	400	2	paramString	String
    //   7	239	3	arrayOfByte1	byte[]
    //   336	48	3	arrayOfByte2	byte[]
    //   30	348	4	localObject	Object
    //   221	14	5	localSecretKey	SecretKey
    // Exception table:
    //   from	to	target	type
    //   285	323	323	java/io/IOException
    //   376	387	323	java/io/IOException
    //   44	253	336	finally
    //   8	44	363	java/security/cert/CertificateException
    //   8	44	363	java/security/UnrecoverableKeyException
    //   8	44	363	java/io/IOException
    //   8	44	363	javax/crypto/BadPaddingException
    //   8	44	363	javax/crypto/IllegalBlockSizeException
    //   8	44	363	java/security/KeyStoreException
    //   8	44	363	javax/crypto/NoSuchPaddingException
    //   8	44	363	java/security/NoSuchAlgorithmException
    //   8	44	363	java/security/InvalidKeyException
    //   253	277	363	java/security/cert/CertificateException
    //   253	277	363	java/security/UnrecoverableKeyException
    //   253	277	363	java/io/IOException
    //   253	277	363	javax/crypto/BadPaddingException
    //   253	277	363	javax/crypto/IllegalBlockSizeException
    //   253	277	363	java/security/KeyStoreException
    //   253	277	363	javax/crypto/NoSuchPaddingException
    //   253	277	363	java/security/NoSuchAlgorithmException
    //   253	277	363	java/security/InvalidKeyException
    //   337	363	363	java/security/cert/CertificateException
    //   337	363	363	java/security/UnrecoverableKeyException
    //   337	363	363	java/io/IOException
    //   337	363	363	javax/crypto/BadPaddingException
    //   337	363	363	javax/crypto/IllegalBlockSizeException
    //   337	363	363	java/security/KeyStoreException
    //   337	363	363	javax/crypto/NoSuchPaddingException
    //   337	363	363	java/security/NoSuchAlgorithmException
    //   337	363	363	java/security/InvalidKeyException
  }
  
  private void unlockChildProfile(int paramInt)
    throws RemoteException
  {
    try
    {
      doVerifyPassword(getDecryptedPasswordForTiedProfile(paramInt), false, 0L, paramInt, null);
      return;
    }
    catch (UnrecoverableKeyException|InvalidKeyException|KeyStoreException|NoSuchAlgorithmException|NoSuchPaddingException|InvalidAlgorithmParameterException|IllegalBlockSizeException|BadPaddingException|CertificateException|IOException localUnrecoverableKeyException)
    {
      if ((localUnrecoverableKeyException instanceof FileNotFoundException))
      {
        Slog.i("LockSettingsService", "Child profile key not found");
        return;
      }
      Slog.e("LockSettingsService", "Failed to decrypt child profile key", localUnrecoverableKeyException);
    }
  }
  
  private void unlockKeystore(String paramString, int paramInt)
  {
    android.security.KeyStore.getInstance().unlock(paramInt, paramString);
  }
  
  /* Error */
  private void unlockUser(int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    // Byte code:
    //   0: new 1100	java/util/concurrent/CountDownLatch
    //   3: dup
    //   4: iconst_1
    //   5: invokespecial 1101	java/util/concurrent/CountDownLatch:<init>	(I)V
    //   8: astore 4
    //   10: new 15	com/android/server/LockSettingsService$4
    //   13: dup
    //   14: aload_0
    //   15: aload 4
    //   17: invokespecial 1104	com/android/server/LockSettingsService$4:<init>	(Lcom/android/server/LockSettingsService;Ljava/util/concurrent/CountDownLatch;)V
    //   20: astore 5
    //   22: invokestatic 1110	android/app/ActivityManagerNative:getDefault	()Landroid/app/IActivityManager;
    //   25: iload_1
    //   26: aload_2
    //   27: aload_3
    //   28: aload 5
    //   30: invokeinterface 1115 5 0
    //   35: pop
    //   36: aload 4
    //   38: ldc2_w 1116
    //   41: getstatic 1123	java/util/concurrent/TimeUnit:SECONDS	Ljava/util/concurrent/TimeUnit;
    //   44: invokevirtual 1127	java/util/concurrent/CountDownLatch:await	(JLjava/util/concurrent/TimeUnit;)Z
    //   47: pop
    //   48: aload_0
    //   49: getfield 113	com/android/server/LockSettingsService:mUserManager	Landroid/os/UserManager;
    //   52: iload_1
    //   53: invokevirtual 302	android/os/UserManager:getUserInfo	(I)Landroid/content/pm/UserInfo;
    //   56: invokevirtual 610	android/content/pm/UserInfo:isManagedProfile	()Z
    //   59: ifne +93 -> 152
    //   62: aload_0
    //   63: getfield 113	com/android/server/LockSettingsService:mUserManager	Landroid/os/UserManager;
    //   66: iload_1
    //   67: invokevirtual 780	android/os/UserManager:getProfiles	(I)Ljava/util/List;
    //   70: invokeinterface 1133 1 0
    //   75: astore_2
    //   76: aload_2
    //   77: invokeinterface 1138 1 0
    //   82: ifeq +70 -> 152
    //   85: aload_2
    //   86: invokeinterface 1142 1 0
    //   91: checkcast 314	android/content/pm/UserInfo
    //   94: astore_3
    //   95: aload_3
    //   96: invokevirtual 610	android/content/pm/UserInfo:isManagedProfile	()Z
    //   99: ifeq -23 -> 76
    //   102: aload_0
    //   103: getfield 101	com/android/server/LockSettingsService:mLockPatternUtils	Lcom/android/internal/widget/LockPatternUtils;
    //   106: aload_3
    //   107: getfield 633	android/content/pm/UserInfo:id	I
    //   110: invokevirtual 613	com/android/internal/widget/LockPatternUtils:isSeparateProfileChallengeEnabled	(I)Z
    //   113: ifne -37 -> 76
    //   116: aload_0
    //   117: getfield 105	com/android/server/LockSettingsService:mStorage	Lcom/android/server/LockSettingsStorage;
    //   120: aload_3
    //   121: getfield 633	android/content/pm/UserInfo:id	I
    //   124: invokevirtual 740	com/android/server/LockSettingsStorage:hasChildProfileLock	(I)Z
    //   127: ifeq -51 -> 76
    //   130: aload_0
    //   131: aload_3
    //   132: getfield 633	android/content/pm/UserInfo:id	I
    //   135: invokespecial 1144	com/android/server/LockSettingsService:unlockChildProfile	(I)V
    //   138: goto -62 -> 76
    //   141: astore_2
    //   142: ldc 61
    //   144: ldc_w 1146
    //   147: aload_2
    //   148: invokestatic 1151	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   151: pop
    //   152: return
    //   153: astore_2
    //   154: aload_2
    //   155: invokevirtual 1155	android/os/RemoteException:rethrowAsRuntimeException	()Ljava/lang/RuntimeException;
    //   158: athrow
    //   159: astore_2
    //   160: invokestatic 1161	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   163: invokevirtual 1164	java/lang/Thread:interrupt	()V
    //   166: goto -118 -> 48
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	169	0	this	LockSettingsService
    //   0	169	1	paramInt	int
    //   0	169	2	paramArrayOfByte1	byte[]
    //   0	169	3	paramArrayOfByte2	byte[]
    //   8	29	4	localCountDownLatch	CountDownLatch
    //   20	9	5	local4	4
    // Exception table:
    //   from	to	target	type
    //   48	76	141	android/os/RemoteException
    //   76	138	141	android/os/RemoteException
    //   22	36	153	android/os/RemoteException
    //   36	48	159	java/lang/InterruptedException
  }
  
  private VerifyCredentialResponse verifyCredential(int paramInt, LockSettingsStorage.CredentialHash paramCredentialHash, String paramString, boolean paramBoolean, long paramLong, CredentialUtil paramCredentialUtil, ICheckCredentialProgressCallback paramICheckCredentialProgressCallback)
    throws RemoteException
  {
    if (((paramCredentialHash == null) || (paramCredentialHash.hash.length == 0)) && (TextUtils.isEmpty(paramString))) {
      return VerifyCredentialResponse.OK;
    }
    if (TextUtils.isEmpty(paramString)) {
      return VerifyCredentialResponse.ERROR;
    }
    byte[] arrayOfByte;
    if (paramCredentialHash.version == 0) {
      if (Arrays.equals(paramCredentialUtil.toHash(paramString, paramInt), paramCredentialHash.hash))
      {
        unlockKeystore(paramCredentialUtil.adjustForKeystore(paramString), paramInt);
        Slog.i("LockSettingsService", "Unlocking user with fake token: " + paramInt);
        arrayOfByte = String.valueOf(paramInt).getBytes();
        unlockUser(paramInt, arrayOfByte, arrayOfByte);
        paramCredentialUtil.setCredential(paramString, null, paramInt);
        if (!paramBoolean)
        {
          notifyActivePasswordMetricsAvailable(paramString, paramInt);
          return VerifyCredentialResponse.OK;
        }
      }
      else
      {
        return VerifyCredentialResponse.ERROR;
      }
    }
    paramBoolean = false;
    paramCredentialHash = getGateKeeperService().verifyChallenge(paramInt, paramLong, paramCredentialHash.hash, paramString.getBytes());
    int i = paramCredentialHash.getResponseCode();
    if (i == 1)
    {
      paramCredentialHash = new VerifyCredentialResponse(paramCredentialHash.getTimeout());
      if (paramCredentialHash.getResponseCode() != 0) {
        break label379;
      }
      if (paramICheckCredentialProgressCallback != null) {
        paramICheckCredentialProgressCallback.onCredentialVerified();
      }
      notifyActivePasswordMetricsAvailable(paramString, paramInt);
      unlockKeystore(paramString, paramInt);
      Slog.i("LockSettingsService", "Unlocking user " + paramInt + " with token length " + paramCredentialHash.getPayload().length);
      unlockUser(paramInt, paramCredentialHash.getPayload(), secretFromCredential(paramString));
      if (isManagedProfileWithSeparatedLock(paramInt)) {
        ((TrustManager)this.mContext.getSystemService("trust")).setDeviceLockedForUser(paramInt, false);
      }
      if (paramBoolean) {
        paramCredentialUtil.setCredential(paramString, paramString, paramInt);
      }
    }
    label379:
    while ((paramCredentialHash.getResponseCode() != 1) || (paramCredentialHash.getTimeout() <= 0))
    {
      return paramCredentialHash;
      if (i == 0)
      {
        arrayOfByte = paramCredentialHash.getPayload();
        if (arrayOfByte == null)
        {
          Slog.e("LockSettingsService", "verifyChallenge response had no associated payload");
          paramCredentialHash = VerifyCredentialResponse.ERROR;
          break;
        }
        paramBoolean = paramCredentialHash.getShouldReEnroll();
        paramCredentialHash = new VerifyCredentialResponse(arrayOfByte);
        break;
      }
      paramCredentialHash = VerifyCredentialResponse.ERROR;
      break;
    }
    requireStrongAuth(8, paramInt);
    return paramCredentialHash;
  }
  
  public VerifyCredentialResponse checkPassword(String paramString, int paramInt, ICheckCredentialProgressCallback paramICheckCredentialProgressCallback)
    throws RemoteException
  {
    paramICheckCredentialProgressCallback = doVerifyPassword(paramString, false, 0L, paramInt, paramICheckCredentialProgressCallback);
    if (paramICheckCredentialProgressCallback.getResponseCode() == 0) {
      retainPassword(paramString);
    }
    return paramICheckCredentialProgressCallback;
  }
  
  public VerifyCredentialResponse checkPattern(String paramString, int paramInt, ICheckCredentialProgressCallback paramICheckCredentialProgressCallback)
    throws RemoteException
  {
    paramICheckCredentialProgressCallback = doVerifyPattern(paramString, false, 0L, paramInt, paramICheckCredentialProgressCallback);
    if (paramICheckCredentialProgressCallback.getResponseCode() == 0) {
      retainPassword(paramString);
    }
    return paramICheckCredentialProgressCallback;
  }
  
  public boolean checkVoldPassword(int paramInt)
    throws RemoteException
  {
    if (!this.mFirstCallToVold) {
      return false;
    }
    this.mFirstCallToVold = false;
    checkPasswordReadPermission(paramInt);
    IMountService localIMountService = getMountService();
    long l = Binder.clearCallingIdentity();
    try
    {
      String str1 = localIMountService.getPassword();
      localIMountService.clearPassword();
      Binder.restoreCallingIdentity(l);
      if (str1 == null) {
        return false;
      }
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
    try
    {
      if (this.mLockPatternUtils.isLockPatternEnabled(paramInt))
      {
        int i = checkPattern(str2, paramInt, null).getResponseCode();
        if (i == 0) {
          return true;
        }
      }
    }
    catch (Exception localException2)
    {
      try
      {
        if (this.mLockPatternUtils.isLockPasswordEnabled(paramInt))
        {
          paramInt = checkPassword(str2, paramInt, null).getResponseCode();
          if (paramInt == 0) {
            return true;
          }
        }
      }
      catch (Exception localException1) {}
    }
    return false;
  }
  
  public boolean getBoolean(String paramString, boolean paramBoolean, int paramInt)
    throws RemoteException
  {
    checkReadPermission(paramString, paramInt);
    paramString = getStringUnchecked(paramString, null, paramInt);
    if (TextUtils.isEmpty(paramString)) {
      return paramBoolean;
    }
    if (!paramString.equals("1")) {
      return paramString.equals("true");
    }
    return true;
  }
  
  public long getLong(String paramString, long paramLong, int paramInt)
    throws RemoteException
  {
    checkReadPermission(paramString, paramInt);
    paramString = getStringUnchecked(paramString, null, paramInt);
    if (TextUtils.isEmpty(paramString)) {
      return paramLong;
    }
    return Long.parseLong(paramString);
  }
  
  public String getPassword()
  {
    if (checkCryptKeeperPermissions()) {
      this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_DEVICE_ADMINS", "no crypt_keeper or admin permission to get the password");
    }
    return mSavePassword;
  }
  
  public boolean getSeparateProfileChallengeEnabled(int paramInt)
    throws RemoteException
  {
    checkReadPermission("lockscreen.profilechallenge", paramInt);
    synchronized (this.mSeparateChallengeLock)
    {
      boolean bool = getBoolean("lockscreen.profilechallenge", false, paramInt);
      return bool;
    }
  }
  
  public String getString(String paramString1, String paramString2, int paramInt)
    throws RemoteException
  {
    checkReadPermission(paramString1, paramInt);
    return getStringUnchecked(paramString1, paramString2, paramInt);
  }
  
  public String getStringUnchecked(String paramString1, String paramString2, int paramInt)
  {
    long l;
    if ("lock_pattern_autolock".equals(paramString1)) {
      l = Binder.clearCallingIdentity();
    }
    try
    {
      if (this.mLockPatternUtils.isLockPatternEnabled(paramInt)) {}
      for (paramString1 = "1";; paramString1 = "0") {
        return paramString1;
      }
      if (!"legacy_lock_pattern_enabled".equals(paramString1)) {
        break label68;
      }
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
    String str = "lock_pattern_autolock";
    label68:
    return this.mStorage.readKeyValue(str, paramString2, paramInt);
  }
  
  public int getStrongAuthForUser(int paramInt)
  {
    checkPasswordReadPermission(paramInt);
    return this.mStrongAuthTracker.getStrongAuthForUser(paramInt);
  }
  
  public boolean havePassword(int paramInt)
    throws RemoteException
  {
    return this.mStorage.hasPassword(paramInt);
  }
  
  public boolean havePattern(int paramInt)
    throws RemoteException
  {
    return this.mStorage.hasPattern(paramInt);
  }
  
  public void hideEncryptionNotification(UserHandle paramUserHandle)
  {
    this.mNotificationManager.cancelAsUser(null, 0, paramUserHandle);
  }
  
  public void onCleanupUser(int paramInt)
  {
    hideEncryptionNotification(new UserHandle(paramInt));
  }
  
  public void onUnlockUser(final int paramInt)
  {
    hideEncryptionNotification(new UserHandle(paramInt));
    if (this.mUserManager.getUserInfo(paramInt).isManagedProfile()) {
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          LockSettingsService.this.tieManagedProfileLockIfNecessary(paramInt, null);
        }
      });
    }
    List localList = this.mUserManager.getProfiles(paramInt);
    paramInt = 0;
    if (paramInt < localList.size())
    {
      Object localObject = (UserInfo)localList.get(paramInt);
      boolean bool;
      if (!this.mStorage.hasPassword(((UserInfo)localObject).id))
      {
        bool = this.mStorage.hasPattern(((UserInfo)localObject).id);
        label104:
        if ((bool) && (((UserInfo)localObject).isManagedProfile()))
        {
          localObject = ((UserInfo)localObject).getUserHandle();
          if ((!this.mUserManager.isUserUnlockingOrUnlocked((UserHandle)localObject)) && (!this.mUserManager.isQuietModeEnabled((UserHandle)localObject))) {
            break label159;
          }
        }
      }
      for (;;)
      {
        paramInt += 1;
        break;
        bool = true;
        break label104;
        label159:
        showEncryptionNotificationForProfile((UserHandle)localObject);
      }
    }
  }
  
  public void registerStrongAuthTracker(IStrongAuthTracker paramIStrongAuthTracker)
  {
    checkPasswordReadPermission(-1);
    this.mStrongAuth.registerStrongAuthTracker(paramIStrongAuthTracker);
  }
  
  public void requireStrongAuth(int paramInt1, int paramInt2)
  {
    checkWritePermission(paramInt2);
    this.mStrongAuth.requireStrongAuth(paramInt1, paramInt2);
  }
  
  public void resetKeyStore(int paramInt)
    throws RemoteException
  {
    checkWritePermission(paramInt);
    int i = -1;
    String str = null;
    Object localObject4 = this.mUserManager.getProfiles(paramInt).iterator();
    int j;
    Object localObject2;
    Object localObject1;
    for (;;)
    {
      j = i;
      if (!((Iterator)localObject4).hasNext()) {
        break;
      }
      UserInfo localUserInfo = (UserInfo)((Iterator)localObject4).next();
      i = j;
      if (localUserInfo.isManagedProfile())
      {
        i = j;
        if (!this.mLockPatternUtils.isSeparateProfileChallengeEnabled(localUserInfo.id))
        {
          i = j;
          if (this.mStorage.hasChildProfileLock(localUserInfo.id))
          {
            if (j == -1) {
              localObject2 = str;
            }
            try
            {
              str = getDecryptedPasswordForTiedProfile(localUserInfo.id);
              localObject2 = str;
              i = localUserInfo.id;
            }
            catch (UnrecoverableKeyException|InvalidKeyException|KeyStoreException|NoSuchAlgorithmException|NoSuchPaddingException|InvalidAlgorithmParameterException|IllegalBlockSizeException|BadPaddingException|CertificateException|IOException localUnrecoverableKeyException)
            {
              Slog.e("LockSettingsService", "Failed to decrypt child profile key", localUnrecoverableKeyException);
              localObject1 = localObject2;
              i = j;
            }
            localObject2 = str;
            Slog.e("LockSettingsService", "More than one managed profile, uid1:" + j + ", uid2:" + localUserInfo.id);
            i = j;
          }
        }
      }
    }
    try
    {
      localObject2 = this.mUserManager.getProfileIdsWithDisabled(paramInt);
      int k = localObject2.length;
      paramInt = 0;
      while (paramInt < k)
      {
        int m = localObject2[paramInt];
        localObject4 = SYSTEM_CREDENTIAL_UIDS;
        i = 0;
        int n = localObject4.length;
        while (i < n)
        {
          int i1 = localObject4[i];
          this.mKeyStore.clearUid(UserHandle.getUid(m, i1));
          i += 1;
        }
        paramInt += 1;
      }
      return;
    }
    finally
    {
      if ((j != -1) && (localObject1 != null)) {
        tieProfileLockToParent(j, (String)localObject1);
      }
    }
  }
  
  public void retainPassword(String paramString)
  {
    if (LockPatternUtils.isDeviceEncryptionEnabled())
    {
      if (paramString != null) {
        mSavePassword = paramString;
      }
    }
    else {
      return;
    }
    mSavePassword = "default_password";
  }
  
  public void sanitizePassword()
  {
    if (LockPatternUtils.isDeviceEncryptionEnabled()) {
      mSavePassword = "default_password";
    }
  }
  
  public void setBoolean(String paramString, boolean paramBoolean, int paramInt)
    throws RemoteException
  {
    checkWritePermission(paramInt);
    if (paramBoolean) {}
    for (String str = "1";; str = "0")
    {
      setStringUnchecked(paramString, paramInt, str);
      return;
    }
  }
  
  public void setLockPassword(String paramString1, String paramString2, int paramInt)
    throws RemoteException
  {
    checkWritePermission(paramInt);
    synchronized (this.mSeparateChallengeLock)
    {
      setLockPasswordInternal(paramString1, paramString2, paramInt);
      setSeparateProfileChallengeEnabled(paramInt, true, null);
      notifyPasswordChanged(paramInt);
      return;
    }
  }
  
  public void setLockPattern(String paramString1, String paramString2, int paramInt)
    throws RemoteException
  {
    checkWritePermission(paramInt);
    synchronized (this.mSeparateChallengeLock)
    {
      setLockPatternInternal(paramString1, paramString2, paramInt);
      setSeparateProfileChallengeEnabled(paramInt, true, null);
      notifyPasswordChanged(paramInt);
      return;
    }
  }
  
  public void setLong(String paramString, long paramLong, int paramInt)
    throws RemoteException
  {
    checkWritePermission(paramInt);
    setStringUnchecked(paramString, paramInt, Long.toString(paramLong));
  }
  
  public void setSeparateProfileChallengeEnabled(int paramInt, boolean paramBoolean, String paramString)
    throws RemoteException
  {
    checkWritePermission(paramInt);
    synchronized (this.mSeparateChallengeLock)
    {
      setBoolean("lockscreen.profilechallenge", paramBoolean, paramInt);
      if (paramBoolean)
      {
        this.mStorage.removeChildProfileLock(paramInt);
        removeKeystoreProfileKey(paramInt);
        return;
      }
      tieManagedProfileLockIfNecessary(paramInt, paramString);
    }
  }
  
  public void setString(String paramString1, String paramString2, int paramInt)
    throws RemoteException
  {
    checkWritePermission(paramInt);
    setStringUnchecked(paramString1, paramInt, paramString2);
  }
  
  public void systemReady()
  {
    migrateOldData();
    try
    {
      getGateKeeperService();
      this.mStorage.prefetchUser(0);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Slog.e("LockSettingsService", "Failure retrieving IGateKeeperService", localRemoteException);
      }
    }
  }
  
  public void tieManagedProfileLockIfNecessary(int paramInt, String paramString)
  {
    if (!UserManager.get(this.mContext).getUserInfo(paramInt).isManagedProfile()) {
      return;
    }
    if (this.mLockPatternUtils.isSeparateProfileChallengeEnabled(paramInt)) {
      return;
    }
    if (this.mStorage.hasChildProfileLock(paramInt)) {
      return;
    }
    int i = this.mUserManager.getProfileParent(paramInt).id;
    if ((this.mStorage.hasPassword(i)) || (this.mStorage.hasPattern(i))) {}
    try
    {
      String str = String.valueOf(HexEncoding.encode(SecureRandom.getInstance("SHA1PRNG").generateSeed(40)));
      setLockPasswordInternal(str, paramString, paramInt);
      setLong("lockscreen.password_type", 327680L, paramInt);
      tieProfileLockToParent(paramInt, str);
      return;
    }
    catch (NoSuchAlgorithmException|RemoteException paramString)
    {
      Slog.e("LockSettingsService", "Fail to tie managed profile", paramString);
    }
    return;
  }
  
  public void unregisterStrongAuthTracker(IStrongAuthTracker paramIStrongAuthTracker)
  {
    checkPasswordReadPermission(-1);
    this.mStrongAuth.unregisterStrongAuthTracker(paramIStrongAuthTracker);
  }
  
  public void userPresent(int paramInt)
  {
    checkWritePermission(paramInt);
    this.mStrongAuth.reportUnlock(paramInt);
  }
  
  public VerifyCredentialResponse verifyPassword(String paramString, long paramLong, int paramInt)
    throws RemoteException
  {
    return doVerifyPassword(paramString, true, paramLong, paramInt, null);
  }
  
  public VerifyCredentialResponse verifyPattern(String paramString, long paramLong, int paramInt)
    throws RemoteException
  {
    return doVerifyPattern(paramString, true, paramLong, paramInt, null);
  }
  
  public VerifyCredentialResponse verifyTiedProfileChallenge(String paramString, boolean paramBoolean, long paramLong, int paramInt)
    throws RemoteException
  {
    checkPasswordReadPermission(paramInt);
    if (!isManagedProfileWithUnifiedLock(paramInt)) {
      throw new RemoteException("User id must be managed profile with unified lock");
    }
    int i = this.mUserManager.getProfileParent(paramInt).id;
    if (paramBoolean) {}
    for (paramString = doVerifyPattern(paramString, true, paramLong, i, null); paramString.getResponseCode() != 0; paramString = doVerifyPassword(paramString, true, paramLong, i, null)) {
      return paramString;
    }
    try
    {
      paramString = doVerifyPassword(getDecryptedPasswordForTiedProfile(paramInt), true, paramLong, paramInt, null);
      return paramString;
    }
    catch (UnrecoverableKeyException|InvalidKeyException|KeyStoreException|NoSuchAlgorithmException|NoSuchPaddingException|InvalidAlgorithmParameterException|IllegalBlockSizeException|BadPaddingException|CertificateException|IOException paramString)
    {
      Slog.e("LockSettingsService", "Failed to decrypt child profile key", paramString);
      throw new RemoteException("Unable to get tied profile token");
    }
  }
  
  private static abstract interface CredentialUtil
  {
    public abstract String adjustForKeystore(String paramString);
    
    public abstract void setCredential(String paramString1, String paramString2, int paramInt)
      throws RemoteException;
    
    public abstract byte[] toHash(String paramString, int paramInt);
  }
  
  private class GateKeeperDiedRecipient
    implements IBinder.DeathRecipient
  {
    private GateKeeperDiedRecipient() {}
    
    public void binderDied()
    {
      LockSettingsService.-get1(LockSettingsService.this).asBinder().unlinkToDeath(this, 0);
      LockSettingsService.-set0(LockSettingsService.this, null);
    }
  }
  
  public static final class Lifecycle
    extends SystemService
  {
    private LockSettingsService mLockSettingsService;
    
    public Lifecycle(Context paramContext)
    {
      super();
    }
    
    public void onBootPhase(int paramInt)
    {
      if (paramInt == 550) {
        LockSettingsService.-wrap0(this.mLockSettingsService);
      }
      while (paramInt != 1000) {
        return;
      }
    }
    
    public void onCleanupUser(int paramInt)
    {
      this.mLockSettingsService.onCleanupUser(paramInt);
    }
    
    public void onStart()
    {
      AndroidKeyStoreProvider.install();
      this.mLockSettingsService = new LockSettingsService(getContext());
      publishBinderService("lock_settings", this.mLockSettingsService);
    }
    
    public void onUnlockUser(int paramInt)
    {
      this.mLockSettingsService.onUnlockUser(paramInt);
    }
  }
  
  private class SynchronizedStrongAuthTracker
    extends LockPatternUtils.StrongAuthTracker
  {
    public SynchronizedStrongAuthTracker(Context paramContext)
    {
      super();
    }
    
    public int getStrongAuthForUser(int paramInt)
    {
      try
      {
        paramInt = super.getStrongAuthForUser(paramInt);
        return paramInt;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    protected void handleStrongAuthRequiredChanged(int paramInt1, int paramInt2)
    {
      try
      {
        super.handleStrongAuthRequiredChanged(paramInt1, paramInt2);
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    void register()
    {
      LockSettingsService.-get4(LockSettingsService.this).registerStrongAuthTracker(this.mStub);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/LockSettingsService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */