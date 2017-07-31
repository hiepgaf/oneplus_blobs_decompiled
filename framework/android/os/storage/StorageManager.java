package android.os.storage;

import android.app.ActivityThread;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.IPackageManager;
import android.content.pm.IPackageMoveObserver;
import android.os.Binder;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings.Global;
import android.text.TextUtils;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.os.SomeArgs;
import com.android.internal.util.Preconditions;
import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class StorageManager
{
  public static final String ACTION_MANAGE_STORAGE = "android.os.storage.action.MANAGE_STORAGE";
  public static final int CRYPT_TYPE_DEFAULT = 1;
  public static final int CRYPT_TYPE_PASSWORD = 0;
  public static final int CRYPT_TYPE_PATTERN = 2;
  public static final int CRYPT_TYPE_PIN = 3;
  public static final int DEBUG_EMULATE_FBE = 2;
  public static final int DEBUG_FORCE_ADOPTABLE = 1;
  public static final int DEBUG_SDCARDFS_FORCE_OFF = 8;
  public static final int DEBUG_SDCARDFS_FORCE_ON = 4;
  private static final long DEFAULT_FULL_THRESHOLD_BYTES = 1048576L;
  private static final long DEFAULT_THRESHOLD_MAX_BYTES = 524288000L;
  private static final int DEFAULT_THRESHOLD_PERCENTAGE = 10;
  public static final int FLAG_FOR_WRITE = 256;
  public static final int FLAG_INCLUDE_INVISIBLE = 1024;
  public static final int FLAG_REAL_STATE = 512;
  public static final int FLAG_STORAGE_CE = 2;
  public static final int FLAG_STORAGE_DE = 1;
  private static final int INTERNAL_STORAGE_SECTOR_SIZE = 512;
  private static final String[] INTERNAL_STORAGE_SIZE_PATHS = { "/sys/block/mmcblk0/size", "/sys/block/sda/size" };
  public static final String OWNER_INFO_KEY = "OwnerInfo";
  public static final String PASSWORD_VISIBLE_KEY = "PasswordVisible";
  public static final String PATTERN_VISIBLE_KEY = "PatternVisible";
  public static final String PROP_EMULATE_FBE = "persist.sys.emulate_fbe";
  public static final String PROP_FORCE_ADOPTABLE = "persist.fw.force_adoptable";
  public static final String PROP_HAS_ADOPTABLE = "vold.has_adoptable";
  public static final String PROP_PRIMARY_PHYSICAL = "ro.vold.primary_physical";
  public static final String PROP_SDCARDFS = "persist.sys.sdcardfs";
  public static final String SYSTEM_LOCALE_KEY = "SystemLocale";
  private static final String TAG = "StorageManager";
  public static final String UUID_PRIMARY_PHYSICAL = "primary_physical";
  public static final String UUID_PRIVATE_INTERNAL;
  private static volatile IMountService sMountService = null;
  private final Context mContext;
  private final ArrayList<StorageEventListenerDelegate> mDelegates = new ArrayList();
  private final Looper mLooper;
  private final IMountService mMountService;
  private final AtomicInteger mNextNonce = new AtomicInteger(0);
  private final ObbActionListener mObbActionListener = new ObbActionListener(null);
  private final ContentResolver mResolver;
  
  public StorageManager(Context paramContext, Looper paramLooper)
  {
    this.mContext = paramContext;
    this.mResolver = paramContext.getContentResolver();
    this.mLooper = paramLooper;
    this.mMountService = IMountService.Stub.asInterface(ServiceManager.getService("mount"));
    if (this.mMountService == null) {
      throw new IllegalStateException("Failed to find running mount service");
    }
  }
  
  @Deprecated
  public static StorageManager from(Context paramContext)
  {
    return (StorageManager)paramContext.getSystemService(StorageManager.class);
  }
  
  private int getNextNonce()
  {
    return this.mNextNonce.getAndIncrement();
  }
  
  public static StorageVolume getPrimaryVolume(StorageVolume[] paramArrayOfStorageVolume)
  {
    int i = 0;
    int j = paramArrayOfStorageVolume.length;
    while (i < j)
    {
      StorageVolume localStorageVolume = paramArrayOfStorageVolume[i];
      if (localStorageVolume.isPrimary()) {
        return localStorageVolume;
      }
      i += 1;
    }
    throw new IllegalStateException("Missing primary storage");
  }
  
  public static StorageVolume getStorageVolume(File paramFile, int paramInt)
  {
    return getStorageVolume(getVolumeList(paramInt, 0), paramFile);
  }
  
  private static StorageVolume getStorageVolume(StorageVolume[] paramArrayOfStorageVolume, File paramFile)
  {
    if (paramFile == null) {
      return null;
    }
    try
    {
      File localFile1 = paramFile.getCanonicalFile();
      int i = 0;
      int j = paramArrayOfStorageVolume.length;
      File localFile2;
      while (i < j)
      {
        paramFile = paramArrayOfStorageVolume[i];
        localFile2 = paramFile.getPathFile();
      }
    }
    catch (IOException paramArrayOfStorageVolume)
    {
      try
      {
        localFile2 = localFile2.getCanonicalFile();
        if (!FileUtils.contains(localFile2, localFile1)) {
          break label80;
        }
        return paramFile;
      }
      catch (IOException paramFile)
      {
        i += 1;
      }
      paramArrayOfStorageVolume = paramArrayOfStorageVolume;
      Slog.d("StorageManager", "Could not get canonical path for " + paramFile);
      return null;
    }
    label80:
    return null;
  }
  
  public static StorageVolume[] getVolumeList(int paramInt1, int paramInt2)
  {
    IMountService localIMountService = IMountService.Stub.asInterface(ServiceManager.getService("mount"));
    for (;;)
    {
      try
      {
        String str = ActivityThread.currentOpPackageName();
        Object localObject1 = str;
        if (str == null)
        {
          localObject1 = ActivityThread.getPackageManager().getPackagesForUid(Process.myUid());
          if ((localObject1 == null) || (localObject1.length <= 0)) {
            return new StorageVolume[0];
          }
        }
        else
        {
          paramInt1 = ActivityThread.getPackageManager().getPackageUid((String)localObject1, 268435456, paramInt1);
          if (paramInt1 <= 0) {
            return new StorageVolume[0];
          }
          localObject1 = localIMountService.getVolumeList(paramInt1, (String)localObject1, paramInt2);
          return (StorageVolume[])localObject1;
        }
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
      Object localObject2 = localRemoteException[0];
    }
  }
  
  public static boolean inCryptKeeperBounce()
  {
    return "trigger_restart_min_framework".equals(SystemProperties.get("vold.decrypt"));
  }
  
  public static boolean isBlockEncrypted()
  {
    if (!isEncrypted()) {
      return false;
    }
    return "block".equalsIgnoreCase(SystemProperties.get("ro.crypto.type", ""));
  }
  
  public static boolean isBlockEncrypting()
  {
    return !"".equalsIgnoreCase(SystemProperties.get("vold.encrypt_progress", ""));
  }
  
  public static boolean isEncryptable()
  {
    return !"unsupported".equalsIgnoreCase(SystemProperties.get("ro.crypto.state", "unsupported"));
  }
  
  public static boolean isEncrypted()
  {
    return "encrypted".equalsIgnoreCase(SystemProperties.get("ro.crypto.state", ""));
  }
  
  public static boolean isFileEncryptedEmulatedOnly()
  {
    return SystemProperties.getBoolean("persist.sys.emulate_fbe", false);
  }
  
  public static boolean isFileEncryptedNativeOnly()
  {
    if (!isEncrypted()) {
      return false;
    }
    return "file".equalsIgnoreCase(SystemProperties.get("ro.crypto.type", ""));
  }
  
  public static boolean isFileEncryptedNativeOrEmulated()
  {
    if (!isFileEncryptedNativeOnly()) {
      return isFileEncryptedEmulatedOnly();
    }
    return true;
  }
  
  public static boolean isNonDefaultBlockEncrypted()
  {
    if (!isBlockEncrypted()) {
      return false;
    }
    try
    {
      int i = IMountService.Stub.asInterface(ServiceManager.getService("mount")).getPasswordType();
      return i != 1;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("StorageManager", "Error getting encryption type");
    }
    return false;
  }
  
  public static boolean isUserKeyUnlocked(int paramInt)
  {
    if (sMountService == null) {
      sMountService = IMountService.Stub.asInterface(ServiceManager.getService("mount"));
    }
    if (sMountService == null)
    {
      Slog.w("StorageManager", "Early during boot, assuming locked");
      return false;
    }
    long l = Binder.clearCallingIdentity();
    try
    {
      boolean bool = sMountService.isUserKeyUnlocked(paramInt);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowAsRuntimeException();
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public static File maybeTranslateEmulatedPathToInternal(File paramFile)
  {
    int i = 0;
    Object localObject = IMountService.Stub.asInterface(ServiceManager.getService("mount"));
    try
    {
      localObject = ((IMountService)localObject).getVolumes(0);
      int j = localObject.length;
      while (i < j)
      {
        File localFile = localObject[i];
        if (((localFile.getType() == 2) || (localFile.getType() == 0)) && (localFile.isMountedReadable()))
        {
          localFile = FileUtils.rewriteAfterRename(localFile.getPath(), localFile.getInternalPath(), paramFile);
          if (localFile != null)
          {
            boolean bool = localFile.exists();
            if (bool) {
              return localFile;
            }
          }
        }
        i += 1;
      }
      return paramFile;
    }
    catch (RemoteException paramFile)
    {
      throw paramFile.rethrowFromSystemServer();
    }
  }
  
  /* Error */
  private long readLong(String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 7
    //   3: aconst_null
    //   4: astore 9
    //   6: aconst_null
    //   7: astore 10
    //   9: aconst_null
    //   10: astore 6
    //   12: aconst_null
    //   13: astore 5
    //   15: aconst_null
    //   16: astore 8
    //   18: new 411	java/io/FileInputStream
    //   21: dup
    //   22: aload_1
    //   23: invokespecial 412	java/io/FileInputStream:<init>	(Ljava/lang/String;)V
    //   26: astore 4
    //   28: new 414	java/io/BufferedReader
    //   31: dup
    //   32: new 416	java/io/InputStreamReader
    //   35: dup
    //   36: aload 4
    //   38: invokespecial 419	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;)V
    //   41: invokespecial 422	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   44: astore 6
    //   46: aload 6
    //   48: invokevirtual 425	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   51: invokestatic 430	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   54: lstore_2
    //   55: aload 9
    //   57: astore 5
    //   59: aload 6
    //   61: ifnull +12 -> 73
    //   64: aload 6
    //   66: invokevirtual 433	java/io/BufferedReader:close	()V
    //   69: aload 9
    //   71: astore 5
    //   73: aload 4
    //   75: ifnull +8 -> 83
    //   78: aload 4
    //   80: invokevirtual 434	java/io/FileInputStream:close	()V
    //   83: aload 5
    //   85: astore 4
    //   87: aload 4
    //   89: ifnull +75 -> 164
    //   92: aload 4
    //   94: athrow
    //   95: astore 4
    //   97: ldc 84
    //   99: new 230	java/lang/StringBuilder
    //   102: dup
    //   103: invokespecial 231	java/lang/StringBuilder:<init>	()V
    //   106: ldc_w 436
    //   109: invokevirtual 237	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   112: aload_1
    //   113: invokevirtual 237	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   116: invokevirtual 244	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   119: aload 4
    //   121: invokestatic 439	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   124: pop
    //   125: lconst_0
    //   126: lreturn
    //   127: astore 5
    //   129: goto -56 -> 73
    //   132: astore 6
    //   134: aload 6
    //   136: astore 4
    //   138: aload 5
    //   140: ifnull -53 -> 87
    //   143: aload 5
    //   145: aload 6
    //   147: if_acmpeq -64 -> 83
    //   150: aload 5
    //   152: aload 6
    //   154: invokevirtual 443	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   157: aload 5
    //   159: astore 4
    //   161: goto -74 -> 87
    //   164: lload_2
    //   165: lreturn
    //   166: astore 4
    //   168: aload 4
    //   170: athrow
    //   171: astore 5
    //   173: aload 4
    //   175: astore 7
    //   177: aload 8
    //   179: ifnull +8 -> 187
    //   182: aload 8
    //   184: invokevirtual 433	java/io/BufferedReader:close	()V
    //   187: aload 7
    //   189: astore 4
    //   191: aload 6
    //   193: ifnull +8 -> 201
    //   196: aload 6
    //   198: invokevirtual 434	java/io/FileInputStream:close	()V
    //   201: aload 4
    //   203: astore 6
    //   205: aload 6
    //   207: ifnull +70 -> 277
    //   210: aload 6
    //   212: athrow
    //   213: astore 8
    //   215: aload 8
    //   217: astore 4
    //   219: aload 7
    //   221: ifnull -30 -> 191
    //   224: aload 7
    //   226: aload 8
    //   228: if_acmpeq -41 -> 187
    //   231: aload 7
    //   233: aload 8
    //   235: invokevirtual 443	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   238: aload 7
    //   240: astore 4
    //   242: goto -51 -> 191
    //   245: astore 7
    //   247: aload 7
    //   249: astore 6
    //   251: aload 4
    //   253: ifnull -48 -> 205
    //   256: aload 4
    //   258: aload 7
    //   260: if_acmpeq -59 -> 201
    //   263: aload 4
    //   265: aload 7
    //   267: invokevirtual 443	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   270: aload 4
    //   272: astore 6
    //   274: goto -69 -> 205
    //   277: aload 5
    //   279: athrow
    //   280: astore 4
    //   282: aload 10
    //   284: astore 6
    //   286: aload 5
    //   288: astore 8
    //   290: aload 4
    //   292: astore 5
    //   294: goto -117 -> 177
    //   297: astore 9
    //   299: aload 4
    //   301: astore 6
    //   303: aload 5
    //   305: astore 8
    //   307: aload 9
    //   309: astore 5
    //   311: goto -134 -> 177
    //   314: astore 5
    //   316: aload 6
    //   318: astore 8
    //   320: aload 4
    //   322: astore 6
    //   324: goto -147 -> 177
    //   327: astore 5
    //   329: aload 4
    //   331: astore 6
    //   333: aload 5
    //   335: astore 4
    //   337: goto -169 -> 168
    //   340: astore 5
    //   342: aload 6
    //   344: astore 8
    //   346: aload 4
    //   348: astore 6
    //   350: aload 5
    //   352: astore 4
    //   354: goto -186 -> 168
    //   357: astore 4
    //   359: goto -262 -> 97
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	362	0	this	StorageManager
    //   0	362	1	paramString	String
    //   54	111	2	l	long
    //   26	67	4	localObject1	Object
    //   95	25	4	localException1	Exception
    //   136	24	4	localThrowable1	Throwable
    //   166	8	4	localThrowable2	Throwable
    //   189	82	4	localObject2	Object
    //   280	50	4	localObject3	Object
    //   335	18	4	localThrowable3	Throwable
    //   357	1	4	localException2	Exception
    //   13	71	5	localObject4	Object
    //   127	31	5	localThrowable4	Throwable
    //   171	116	5	localObject5	Object
    //   292	18	5	localObject6	Object
    //   314	1	5	localObject7	Object
    //   327	7	5	localThrowable5	Throwable
    //   340	11	5	localThrowable6	Throwable
    //   10	55	6	localBufferedReader	java.io.BufferedReader
    //   132	65	6	localThrowable7	Throwable
    //   203	146	6	localObject8	Object
    //   1	238	7	localObject9	Object
    //   245	21	7	localThrowable8	Throwable
    //   16	167	8	localObject10	Object
    //   213	21	8	localThrowable9	Throwable
    //   288	57	8	localObject11	Object
    //   4	66	9	localObject12	Object
    //   297	11	9	localObject13	Object
    //   7	276	10	localObject14	Object
    // Exception table:
    //   from	to	target	type
    //   64	69	95	java/lang/Exception
    //   78	83	95	java/lang/Exception
    //   92	95	95	java/lang/Exception
    //   150	157	95	java/lang/Exception
    //   64	69	127	java/lang/Throwable
    //   78	83	132	java/lang/Throwable
    //   18	28	166	java/lang/Throwable
    //   168	171	171	finally
    //   182	187	213	java/lang/Throwable
    //   196	201	245	java/lang/Throwable
    //   18	28	280	finally
    //   28	46	297	finally
    //   46	55	314	finally
    //   28	46	327	java/lang/Throwable
    //   46	55	340	java/lang/Throwable
    //   182	187	357	java/lang/Exception
    //   196	201	357	java/lang/Exception
    //   210	213	357	java/lang/Exception
    //   231	238	357	java/lang/Exception
    //   263	270	357	java/lang/Exception
    //   277	280	357	java/lang/Exception
  }
  
  public long benchmark(String paramString)
  {
    try
    {
      long l = this.mMountService.benchmark(paramString);
      return l;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void createUserKey(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    try
    {
      this.mMountService.createUserKey(paramInt1, paramInt2, paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void destroyUserKey(int paramInt)
  {
    try
    {
      this.mMountService.destroyUserKey(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void destroyUserStorage(String paramString, int paramInt1, int paramInt2)
  {
    try
    {
      this.mMountService.destroyUserStorage(paramString, paramInt1, paramInt2);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  @Deprecated
  public void disableUsbMassStorage() {}
  
  @Deprecated
  public void enableUsbMassStorage() {}
  
  public DiskInfo findDiskById(String paramString)
  {
    Preconditions.checkNotNull(paramString);
    Iterator localIterator = getDisks().iterator();
    while (localIterator.hasNext())
    {
      DiskInfo localDiskInfo = (DiskInfo)localIterator.next();
      if (Objects.equals(localDiskInfo.id, paramString)) {
        return localDiskInfo;
      }
    }
    return null;
  }
  
  public VolumeInfo findEmulatedForPrivate(VolumeInfo paramVolumeInfo)
  {
    if (paramVolumeInfo != null) {
      return findVolumeById(paramVolumeInfo.getId().replace("private", "emulated"));
    }
    return null;
  }
  
  public VolumeInfo findPrivateForEmulated(VolumeInfo paramVolumeInfo)
  {
    if (paramVolumeInfo != null) {
      return findVolumeById(paramVolumeInfo.getId().replace("emulated", "private"));
    }
    return null;
  }
  
  public VolumeRecord findRecordByUuid(String paramString)
  {
    Preconditions.checkNotNull(paramString);
    Iterator localIterator = getVolumeRecords().iterator();
    while (localIterator.hasNext())
    {
      VolumeRecord localVolumeRecord = (VolumeRecord)localIterator.next();
      if (Objects.equals(localVolumeRecord.fsUuid, paramString)) {
        return localVolumeRecord;
      }
    }
    return null;
  }
  
  public VolumeInfo findVolumeById(String paramString)
  {
    Preconditions.checkNotNull(paramString);
    Iterator localIterator = getVolumes().iterator();
    while (localIterator.hasNext())
    {
      VolumeInfo localVolumeInfo = (VolumeInfo)localIterator.next();
      if (Objects.equals(localVolumeInfo.id, paramString)) {
        return localVolumeInfo;
      }
    }
    return null;
  }
  
  public VolumeInfo findVolumeByQualifiedUuid(String paramString)
  {
    if (Objects.equals(UUID_PRIVATE_INTERNAL, paramString)) {
      return findVolumeById("private");
    }
    if (Objects.equals("primary_physical", paramString)) {
      return getPrimaryPhysicalVolume();
    }
    return findVolumeByUuid(paramString);
  }
  
  public VolumeInfo findVolumeByUuid(String paramString)
  {
    Preconditions.checkNotNull(paramString);
    Iterator localIterator = getVolumes().iterator();
    while (localIterator.hasNext())
    {
      VolumeInfo localVolumeInfo = (VolumeInfo)localIterator.next();
      if (Objects.equals(localVolumeInfo.fsUuid, paramString)) {
        return localVolumeInfo;
      }
    }
    return null;
  }
  
  public void forgetVolume(String paramString)
  {
    try
    {
      this.mMountService.forgetVolume(paramString);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void format(String paramString)
  {
    try
    {
      this.mMountService.format(paramString);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public String getBestVolumeDescription(VolumeInfo paramVolumeInfo)
  {
    if (paramVolumeInfo == null) {
      return null;
    }
    VolumeRecord localVolumeRecord;
    if (!TextUtils.isEmpty(paramVolumeInfo.fsUuid))
    {
      localVolumeRecord = findRecordByUuid(paramVolumeInfo.fsUuid);
      if ((localVolumeRecord != null) && (!TextUtils.isEmpty(localVolumeRecord.nickname))) {}
    }
    else
    {
      if (TextUtils.isEmpty(paramVolumeInfo.getDescription())) {
        break label59;
      }
      return paramVolumeInfo.getDescription();
    }
    return localVolumeRecord.nickname;
    label59:
    if (paramVolumeInfo.disk != null) {
      return paramVolumeInfo.disk.getDescription();
    }
    return null;
  }
  
  public List<DiskInfo> getDisks()
  {
    try
    {
      List localList = Arrays.asList(this.mMountService.getDisks());
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public String getMountedObbPath(String paramString)
  {
    Preconditions.checkNotNull(paramString, "rawPath cannot be null");
    try
    {
      paramString = this.mMountService.getMountedObbPath(paramString);
      return paramString;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public VolumeInfo getPrimaryPhysicalVolume()
  {
    Iterator localIterator = getVolumes().iterator();
    while (localIterator.hasNext())
    {
      VolumeInfo localVolumeInfo = (VolumeInfo)localIterator.next();
      if (localVolumeInfo.isPrimaryPhysical()) {
        return localVolumeInfo;
      }
    }
    return null;
  }
  
  public long getPrimaryStorageSize()
  {
    String[] arrayOfString = INTERNAL_STORAGE_SIZE_PATHS;
    int i = 0;
    int j = arrayOfString.length;
    while (i < j)
    {
      long l = readLong(arrayOfString[i]);
      if (l > 0L) {
        return 512L * l;
      }
      i += 1;
    }
    return 0L;
  }
  
  public String getPrimaryStorageUuid()
  {
    try
    {
      String str = this.mMountService.getPrimaryStorageUuid();
      return str;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public StorageVolume getPrimaryStorageVolume()
  {
    return getVolumeList(UserHandle.myUserId(), 1536)[0];
  }
  
  public StorageVolume getPrimaryVolume()
  {
    return getPrimaryVolume(getVolumeList());
  }
  
  public long getStorageBytesUntilLow(File paramFile)
  {
    return paramFile.getUsableSpace() - getStorageFullBytes(paramFile);
  }
  
  public long getStorageFullBytes(File paramFile)
  {
    return Settings.Global.getLong(this.mResolver, "sys_storage_full_threshold_bytes", 1048576L);
  }
  
  public long getStorageLowBytes(File paramFile)
  {
    long l = Settings.Global.getInt(this.mResolver, "sys_storage_threshold_percentage", 10);
    return Math.min(paramFile.getTotalSpace() * l / 100L, Settings.Global.getLong(this.mResolver, "sys_storage_threshold_max_bytes", 524288000L));
  }
  
  public StorageVolume getStorageVolume(File paramFile)
  {
    return getStorageVolume(getVolumeList(), paramFile);
  }
  
  public List<StorageVolume> getStorageVolumes()
  {
    ArrayList localArrayList = new ArrayList();
    Collections.addAll(localArrayList, getVolumeList(UserHandle.myUserId(), 1536));
    return localArrayList;
  }
  
  public StorageVolume[] getVolumeList()
  {
    return getVolumeList(this.mContext.getUserId(), 0);
  }
  
  @Deprecated
  public String[] getVolumePaths()
  {
    StorageVolume[] arrayOfStorageVolume = getVolumeList();
    int j = arrayOfStorageVolume.length;
    String[] arrayOfString = new String[j];
    int i = 0;
    while (i < j)
    {
      arrayOfString[i] = arrayOfStorageVolume[i].getPath();
      i += 1;
    }
    return arrayOfString;
  }
  
  public List<VolumeRecord> getVolumeRecords()
  {
    try
    {
      List localList = Arrays.asList(this.mMountService.getVolumeRecords(0));
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  @Deprecated
  public String getVolumeState(String paramString)
  {
    paramString = getStorageVolume(new File(paramString));
    if (paramString != null) {
      return paramString.getState();
    }
    return "unknown";
  }
  
  public List<VolumeInfo> getVolumes()
  {
    try
    {
      List localList = Arrays.asList(this.mMountService.getVolumes(0));
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public List<VolumeInfo> getWritablePrivateVolumes()
  {
    int i = 0;
    try
    {
      ArrayList localArrayList = new ArrayList();
      VolumeInfo[] arrayOfVolumeInfo = this.mMountService.getVolumes(0);
      int j = arrayOfVolumeInfo.length;
      while (i < j)
      {
        VolumeInfo localVolumeInfo = arrayOfVolumeInfo[i];
        if ((localVolumeInfo.getType() == 1) && (localVolumeInfo.isMountedWritable())) {
          localArrayList.add(localVolumeInfo);
        }
        i += 1;
      }
      return localArrayList;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isEncrypted(File paramFile)
  {
    if (FileUtils.contains(Environment.getDataDirectory(), paramFile)) {
      return isEncrypted();
    }
    return FileUtils.contains(Environment.getExpandDirectory(), paramFile);
  }
  
  public boolean isObbMounted(String paramString)
  {
    Preconditions.checkNotNull(paramString, "rawPath cannot be null");
    try
    {
      boolean bool = this.mMountService.isObbMounted(paramString);
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  @Deprecated
  public boolean isUsbMassStorageConnected()
  {
    return false;
  }
  
  @Deprecated
  public boolean isUsbMassStorageEnabled()
  {
    return false;
  }
  
  public void lockUserKey(int paramInt)
  {
    try
    {
      this.mMountService.lockUserKey(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void mount(String paramString)
  {
    try
    {
      this.mMountService.mount(paramString);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public ParcelFileDescriptor mountAppFuse(String paramString)
  {
    try
    {
      paramString = this.mMountService.mountAppFuse(paramString);
      return paramString;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public boolean mountObb(String paramString1, String paramString2, OnObbStateChangeListener paramOnObbStateChangeListener)
  {
    Preconditions.checkNotNull(paramString1, "rawPath cannot be null");
    Preconditions.checkNotNull(paramOnObbStateChangeListener, "listener cannot be null");
    try
    {
      String str = new File(paramString1).getCanonicalPath();
      int i = this.mObbActionListener.addListener(paramOnObbStateChangeListener);
      this.mMountService.mountObb(paramString1, str, paramString2, this.mObbActionListener, i);
      return true;
    }
    catch (RemoteException paramString1)
    {
      throw paramString1.rethrowFromSystemServer();
    }
    catch (IOException paramString2)
    {
      throw new IllegalArgumentException("Failed to resolve path: " + paramString1, paramString2);
    }
  }
  
  public void partitionMixed(String paramString, int paramInt)
  {
    try
    {
      this.mMountService.partitionMixed(paramString, paramInt);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void partitionPrivate(String paramString)
  {
    try
    {
      this.mMountService.partitionPrivate(paramString);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void partitionPublic(String paramString)
  {
    try
    {
      this.mMountService.partitionPublic(paramString);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void prepareUserStorage(String paramString, int paramInt1, int paramInt2, int paramInt3)
  {
    try
    {
      this.mMountService.prepareUserStorage(paramString, paramInt1, paramInt2, paramInt3);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void registerListener(StorageEventListener paramStorageEventListener)
  {
    synchronized (this.mDelegates)
    {
      paramStorageEventListener = new StorageEventListenerDelegate(paramStorageEventListener, this.mLooper);
      try
      {
        this.mMountService.registerListener(paramStorageEventListener);
        this.mDelegates.add(paramStorageEventListener);
        return;
      }
      catch (RemoteException paramStorageEventListener)
      {
        throw paramStorageEventListener.rethrowFromSystemServer();
      }
    }
  }
  
  public void setPrimaryStorageUuid(String paramString, IPackageMoveObserver paramIPackageMoveObserver)
  {
    try
    {
      this.mMountService.setPrimaryStorageUuid(paramString, paramIPackageMoveObserver);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  /* Error */
  public void setVolumeInited(String paramString, boolean paramBoolean)
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_3
    //   2: aload_0
    //   3: getfield 174	android/os/storage/StorageManager:mMountService	Landroid/os/storage/IMountService;
    //   6: astore 4
    //   8: iload_2
    //   9: ifeq +14 -> 23
    //   12: aload 4
    //   14: aload_1
    //   15: iload_3
    //   16: iconst_1
    //   17: invokeinterface 755 4 0
    //   22: return
    //   23: iconst_0
    //   24: istore_3
    //   25: goto -13 -> 12
    //   28: astore_1
    //   29: aload_1
    //   30: invokevirtual 286	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
    //   33: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	34	0	this	StorageManager
    //   0	34	1	paramString	String
    //   0	34	2	paramBoolean	boolean
    //   1	24	3	i	int
    //   6	7	4	localIMountService	IMountService
    // Exception table:
    //   from	to	target	type
    //   2	8	28	android/os/RemoteException
    //   12	22	28	android/os/RemoteException
  }
  
  public void setVolumeNickname(String paramString1, String paramString2)
  {
    try
    {
      this.mMountService.setVolumeNickname(paramString1, paramString2);
      return;
    }
    catch (RemoteException paramString1)
    {
      throw paramString1.rethrowFromSystemServer();
    }
  }
  
  /* Error */
  public void setVolumeSnoozed(String paramString, boolean paramBoolean)
  {
    // Byte code:
    //   0: iconst_2
    //   1: istore_3
    //   2: aload_0
    //   3: getfield 174	android/os/storage/StorageManager:mMountService	Landroid/os/storage/IMountService;
    //   6: astore 4
    //   8: iload_2
    //   9: ifeq +14 -> 23
    //   12: aload 4
    //   14: aload_1
    //   15: iload_3
    //   16: iconst_2
    //   17: invokeinterface 755 4 0
    //   22: return
    //   23: iconst_0
    //   24: istore_3
    //   25: goto -13 -> 12
    //   28: astore_1
    //   29: aload_1
    //   30: invokevirtual 286	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
    //   33: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	34	0	this	StorageManager
    //   0	34	1	paramString	String
    //   0	34	2	paramBoolean	boolean
    //   1	24	3	i	int
    //   6	7	4	localIMountService	IMountService
    // Exception table:
    //   from	to	target	type
    //   2	8	28	android/os/RemoteException
    //   12	22	28	android/os/RemoteException
  }
  
  public void unlockUserKey(int paramInt1, int paramInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    try
    {
      this.mMountService.unlockUserKey(paramInt1, paramInt2, paramArrayOfByte1, paramArrayOfByte2);
      return;
    }
    catch (RemoteException paramArrayOfByte1)
    {
      throw paramArrayOfByte1.rethrowFromSystemServer();
    }
  }
  
  public void unmount(String paramString)
  {
    try
    {
      this.mMountService.unmount(paramString);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public boolean unmountObb(String paramString, boolean paramBoolean, OnObbStateChangeListener paramOnObbStateChangeListener)
  {
    Preconditions.checkNotNull(paramString, "rawPath cannot be null");
    Preconditions.checkNotNull(paramOnObbStateChangeListener, "listener cannot be null");
    try
    {
      int i = this.mObbActionListener.addListener(paramOnObbStateChangeListener);
      this.mMountService.unmountObb(paramString, paramBoolean, this.mObbActionListener, i);
      return true;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void unregisterListener(StorageEventListener paramStorageEventListener)
  {
    Iterator localIterator;
    StorageEventListenerDelegate localStorageEventListenerDelegate;
    synchronized (this.mDelegates)
    {
      localIterator = this.mDelegates.iterator();
      StorageEventListener localStorageEventListener;
      do
      {
        if (!localIterator.hasNext()) {
          break;
        }
        localStorageEventListenerDelegate = (StorageEventListenerDelegate)localIterator.next();
        localStorageEventListener = localStorageEventListenerDelegate.mCallback;
      } while (localStorageEventListener != paramStorageEventListener);
    }
  }
  
  public void wipeAdoptableDisks()
  {
    Iterator localIterator = getDisks().iterator();
    while (localIterator.hasNext())
    {
      DiskInfo localDiskInfo = (DiskInfo)localIterator.next();
      String str = localDiskInfo.getId();
      if (localDiskInfo.isAdoptable())
      {
        Slog.d("StorageManager", "Found adoptable " + str + "; wiping");
        try
        {
          this.mMountService.partitionPublic(str);
        }
        catch (Exception localException)
        {
          Slog.w("StorageManager", "Failed to wipe " + str + ", but soldiering onward", localException);
        }
      }
      else
      {
        Slog.d("StorageManager", "Ignorning non-adoptable disk " + localException.getId());
      }
    }
  }
  
  private class ObbActionListener
    extends IObbActionListener.Stub
  {
    private SparseArray<StorageManager.ObbListenerDelegate> mListeners = new SparseArray();
    
    private ObbActionListener() {}
    
    public int addListener(OnObbStateChangeListener arg1)
    {
      StorageManager.ObbListenerDelegate localObbListenerDelegate = new StorageManager.ObbListenerDelegate(StorageManager.this, ???);
      synchronized (this.mListeners)
      {
        this.mListeners.put(StorageManager.ObbListenerDelegate.-get0(localObbListenerDelegate), localObbListenerDelegate);
        return StorageManager.ObbListenerDelegate.-get0(localObbListenerDelegate);
      }
    }
    
    public void onObbResult(String paramString, int paramInt1, int paramInt2)
    {
      synchronized (this.mListeners)
      {
        StorageManager.ObbListenerDelegate localObbListenerDelegate = (StorageManager.ObbListenerDelegate)this.mListeners.get(paramInt1);
        if (localObbListenerDelegate != null) {
          this.mListeners.remove(paramInt1);
        }
        if (localObbListenerDelegate != null) {
          localObbListenerDelegate.sendObbStateChanged(paramString, paramInt2);
        }
        return;
      }
    }
  }
  
  private class ObbListenerDelegate
  {
    private final Handler mHandler;
    private final WeakReference<OnObbStateChangeListener> mObbEventListenerRef;
    private final int nonce = StorageManager.-wrap0(StorageManager.this);
    
    ObbListenerDelegate(OnObbStateChangeListener paramOnObbStateChangeListener)
    {
      this.mObbEventListenerRef = new WeakReference(paramOnObbStateChangeListener);
      this.mHandler = new Handler(StorageManager.-get0(StorageManager.this))
      {
        public void handleMessage(Message paramAnonymousMessage)
        {
          OnObbStateChangeListener localOnObbStateChangeListener = StorageManager.ObbListenerDelegate.this.getListener();
          if (localOnObbStateChangeListener == null) {
            return;
          }
          localOnObbStateChangeListener.onObbStateChange((String)paramAnonymousMessage.obj, paramAnonymousMessage.arg1);
        }
      };
    }
    
    OnObbStateChangeListener getListener()
    {
      if (this.mObbEventListenerRef == null) {
        return null;
      }
      return (OnObbStateChangeListener)this.mObbEventListenerRef.get();
    }
    
    void sendObbStateChanged(String paramString, int paramInt)
    {
      this.mHandler.obtainMessage(0, paramInt, 0, paramString).sendToTarget();
    }
  }
  
  private static class StorageEventListenerDelegate
    extends IMountServiceListener.Stub
    implements Handler.Callback
  {
    private static final int MSG_DISK_DESTROYED = 6;
    private static final int MSG_DISK_SCANNED = 5;
    private static final int MSG_STORAGE_STATE_CHANGED = 1;
    private static final int MSG_VOLUME_FORGOTTEN = 4;
    private static final int MSG_VOLUME_RECORD_CHANGED = 3;
    private static final int MSG_VOLUME_STATE_CHANGED = 2;
    final StorageEventListener mCallback;
    final Handler mHandler;
    
    public StorageEventListenerDelegate(StorageEventListener paramStorageEventListener, Looper paramLooper)
    {
      this.mCallback = paramStorageEventListener;
      this.mHandler = new Handler(paramLooper, this);
    }
    
    public boolean handleMessage(Message paramMessage)
    {
      SomeArgs localSomeArgs = (SomeArgs)paramMessage.obj;
      switch (paramMessage.what)
      {
      default: 
        localSomeArgs.recycle();
        return false;
      case 1: 
        this.mCallback.onStorageStateChanged((String)localSomeArgs.arg1, (String)localSomeArgs.arg2, (String)localSomeArgs.arg3);
        localSomeArgs.recycle();
        return true;
      case 2: 
        this.mCallback.onVolumeStateChanged((VolumeInfo)localSomeArgs.arg1, localSomeArgs.argi2, localSomeArgs.argi3);
        localSomeArgs.recycle();
        return true;
      case 3: 
        this.mCallback.onVolumeRecordChanged((VolumeRecord)localSomeArgs.arg1);
        localSomeArgs.recycle();
        return true;
      case 4: 
        this.mCallback.onVolumeForgotten((String)localSomeArgs.arg1);
        localSomeArgs.recycle();
        return true;
      case 5: 
        this.mCallback.onDiskScanned((DiskInfo)localSomeArgs.arg1, localSomeArgs.argi2);
        localSomeArgs.recycle();
        return true;
      }
      this.mCallback.onDiskDestroyed((DiskInfo)localSomeArgs.arg1);
      localSomeArgs.recycle();
      return true;
    }
    
    public void onDiskDestroyed(DiskInfo paramDiskInfo)
      throws RemoteException
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramDiskInfo;
      this.mHandler.obtainMessage(6, localSomeArgs).sendToTarget();
    }
    
    public void onDiskScanned(DiskInfo paramDiskInfo, int paramInt)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramDiskInfo;
      localSomeArgs.argi2 = paramInt;
      this.mHandler.obtainMessage(5, localSomeArgs).sendToTarget();
    }
    
    public void onStorageStateChanged(String paramString1, String paramString2, String paramString3)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramString1;
      localSomeArgs.arg2 = paramString2;
      localSomeArgs.arg3 = paramString3;
      this.mHandler.obtainMessage(1, localSomeArgs).sendToTarget();
    }
    
    public void onUsbMassStorageConnectionChanged(boolean paramBoolean)
      throws RemoteException
    {}
    
    public void onVolumeForgotten(String paramString)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramString;
      this.mHandler.obtainMessage(4, localSomeArgs).sendToTarget();
    }
    
    public void onVolumeRecordChanged(VolumeRecord paramVolumeRecord)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramVolumeRecord;
      this.mHandler.obtainMessage(3, localSomeArgs).sendToTarget();
    }
    
    public void onVolumeStateChanged(VolumeInfo paramVolumeInfo, int paramInt1, int paramInt2)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramVolumeInfo;
      localSomeArgs.argi2 = paramInt1;
      localSomeArgs.argi3 = paramInt2;
      this.mHandler.obtainMessage(2, localSomeArgs).sendToTarget();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/storage/StorageManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */