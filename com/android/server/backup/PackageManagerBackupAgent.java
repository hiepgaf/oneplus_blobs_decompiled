package com.android.server.backup;

import android.app.backup.BackupAgent;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.content.ComponentName;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.os.ParcelFileDescriptor;
import android.util.Slog;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class PackageManagerBackupAgent
  extends BackupAgent
{
  private static final int ANCESTRAL_RECORD_VERSION = 1;
  private static final boolean DEBUG = false;
  private static final String DEFAULT_HOME_KEY = "@home@";
  private static final String GLOBAL_METADATA_KEY = "@meta@";
  private static final String STATE_FILE_HEADER = "=state=";
  private static final int STATE_FILE_VERSION = 2;
  private static final String TAG = "PMBA";
  private List<PackageInfo> mAllPackages;
  private final HashSet<String> mExisting = new HashSet();
  private boolean mHasMetadata;
  private PackageManager mPackageManager;
  private ComponentName mRestoredHome;
  private String mRestoredHomeInstaller;
  private ArrayList<byte[]> mRestoredHomeSigHashes;
  private long mRestoredHomeVersion;
  private HashMap<String, Metadata> mRestoredSignatures;
  private HashMap<String, Metadata> mStateVersions = new HashMap();
  private ComponentName mStoredHomeComponent;
  private ArrayList<byte[]> mStoredHomeSigHashes;
  private long mStoredHomeVersion;
  private String mStoredIncrementalVersion;
  private int mStoredSdkVersion;
  
  PackageManagerBackupAgent(PackageManager paramPackageManager)
  {
    init(paramPackageManager, null);
    evaluateStorablePackages();
  }
  
  PackageManagerBackupAgent(PackageManager paramPackageManager, List<PackageInfo> paramList)
  {
    init(paramPackageManager, paramList);
  }
  
  private ComponentName getPreferredHomeComponent()
  {
    return this.mPackageManager.getHomeActivities(new ArrayList());
  }
  
  public static List<PackageInfo> getStorableApplications(PackageManager paramPackageManager)
  {
    paramPackageManager = paramPackageManager.getInstalledPackages(64);
    int i = paramPackageManager.size() - 1;
    while (i >= 0)
    {
      if (!BackupManagerService.appIsEligibleForBackup(((PackageInfo)paramPackageManager.get(i)).applicationInfo)) {
        paramPackageManager.remove(i);
      }
      i -= 1;
    }
    return paramPackageManager;
  }
  
  private void init(PackageManager paramPackageManager, List<PackageInfo> paramList)
  {
    this.mPackageManager = paramPackageManager;
    this.mAllPackages = paramList;
    this.mRestoredSignatures = null;
    this.mHasMetadata = false;
    this.mStoredSdkVersion = Build.VERSION.SDK_INT;
    this.mStoredIncrementalVersion = Build.VERSION.INCREMENTAL;
  }
  
  private void parseStateFile(ParcelFileDescriptor paramParcelFileDescriptor)
  {
    this.mExisting.clear();
    this.mStateVersions.clear();
    this.mStoredSdkVersion = 0;
    this.mStoredIncrementalVersion = null;
    this.mStoredHomeComponent = null;
    this.mStoredHomeVersion = 0L;
    this.mStoredHomeSigHashes = null;
    DataInputStream localDataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(paramParcelFileDescriptor.getFileDescriptor())));
    int i = 0;
    try
    {
      paramParcelFileDescriptor = localDataInputStream.readUTF();
      int j;
      if (paramParcelFileDescriptor.equals("=state="))
      {
        j = localDataInputStream.readInt();
        if (j > 2)
        {
          Slog.w("PMBA", "Unsupported state file version " + j + ", redoing from start");
          return;
        }
        paramParcelFileDescriptor = localDataInputStream.readUTF();
      }
      for (;;)
      {
        Object localObject = paramParcelFileDescriptor;
        if (paramParcelFileDescriptor.equals("@home@"))
        {
          this.mStoredHomeComponent = ComponentName.unflattenFromString(localDataInputStream.readUTF());
          this.mStoredHomeVersion = localDataInputStream.readLong();
          this.mStoredHomeSigHashes = readSignatureHashArray(localDataInputStream);
          localObject = localDataInputStream.readUTF();
        }
        if (!((String)localObject).equals("@meta@")) {
          break;
        }
        this.mStoredSdkVersion = localDataInputStream.readInt();
        this.mStoredIncrementalVersion = localDataInputStream.readUTF();
        if (i == 0) {
          this.mExisting.add("@meta@");
        }
        for (;;)
        {
          paramParcelFileDescriptor = localDataInputStream.readUTF();
          j = localDataInputStream.readInt();
          if (i == 0) {
            this.mExisting.add(paramParcelFileDescriptor);
          }
          this.mStateVersions.put(paramParcelFileDescriptor, new Metadata(j, null));
        }
        Slog.i("PMBA", "Older version of saved state - rewriting");
        i = 1;
      }
      Slog.e("PMBA", "No global metadata in state file!");
      return;
    }
    catch (IOException paramParcelFileDescriptor)
    {
      Slog.e("PMBA", "Unable to read Package Manager state file: " + paramParcelFileDescriptor);
      return;
    }
    catch (EOFException paramParcelFileDescriptor) {}
  }
  
  private static ArrayList<byte[]> readSignatureHashArray(DataInputStream paramDataInputStream)
  {
    int k;
    try
    {
      k = paramDataInputStream.readInt();
      if (k > 20)
      {
        Slog.e("PMBA", "Suspiciously large sig count in restore data; aborting");
        throw new IllegalStateException("Bad restore state");
      }
    }
    catch (IOException paramDataInputStream)
    {
      Slog.e("PMBA", "Unable to read signatures");
      return null;
    }
    catch (EOFException paramDataInputStream)
    {
      Slog.w("PMBA", "Read empty signature block");
      return null;
    }
    int j = 0;
    ArrayList localArrayList = new ArrayList(k);
    int i = 0;
    for (;;)
    {
      if (i < k)
      {
        int m = paramDataInputStream.readInt();
        byte[] arrayOfByte = new byte[m];
        paramDataInputStream.read(arrayOfByte);
        localArrayList.add(arrayOfByte);
        if (m != 32) {
          j = 1;
        }
      }
      else
      {
        paramDataInputStream = localArrayList;
        if (j != 0) {
          paramDataInputStream = BackupUtils.hashSignatureArray(localArrayList);
        }
        return paramDataInputStream;
      }
      i += 1;
    }
  }
  
  private static void writeEntity(BackupDataOutput paramBackupDataOutput, String paramString, byte[] paramArrayOfByte)
    throws IOException
  {
    paramBackupDataOutput.writeEntityHeader(paramString, paramArrayOfByte.length);
    paramBackupDataOutput.writeEntityData(paramArrayOfByte, paramArrayOfByte.length);
  }
  
  private static void writeSignatureHashArray(DataOutputStream paramDataOutputStream, ArrayList<byte[]> paramArrayList)
    throws IOException
  {
    paramDataOutputStream.writeInt(paramArrayList.size());
    paramArrayList = paramArrayList.iterator();
    while (paramArrayList.hasNext())
    {
      byte[] arrayOfByte = (byte[])paramArrayList.next();
      paramDataOutputStream.writeInt(arrayOfByte.length);
      paramDataOutputStream.write(arrayOfByte);
    }
  }
  
  private void writeStateFile(List<PackageInfo> paramList, ComponentName paramComponentName, long paramLong, ArrayList<byte[]> paramArrayList, ParcelFileDescriptor paramParcelFileDescriptor)
  {
    paramParcelFileDescriptor = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(paramParcelFileDescriptor.getFileDescriptor())));
    try
    {
      paramParcelFileDescriptor.writeUTF("=state=");
      paramParcelFileDescriptor.writeInt(2);
      if (paramComponentName != null)
      {
        paramParcelFileDescriptor.writeUTF("@home@");
        paramParcelFileDescriptor.writeUTF(paramComponentName.flattenToString());
        paramParcelFileDescriptor.writeLong(paramLong);
        writeSignatureHashArray(paramParcelFileDescriptor, paramArrayList);
      }
      paramParcelFileDescriptor.writeUTF("@meta@");
      paramParcelFileDescriptor.writeInt(Build.VERSION.SDK_INT);
      paramParcelFileDescriptor.writeUTF(Build.VERSION.INCREMENTAL);
      paramList = paramList.iterator();
      while (paramList.hasNext())
      {
        paramComponentName = (PackageInfo)paramList.next();
        paramParcelFileDescriptor.writeUTF(paramComponentName.packageName);
        paramParcelFileDescriptor.writeInt(paramComponentName.versionCode);
      }
      paramParcelFileDescriptor.flush();
    }
    catch (IOException paramList)
    {
      Slog.e("PMBA", "Unable to write package manager state file!");
      return;
    }
  }
  
  public void evaluateStorablePackages()
  {
    this.mAllPackages = getStorableApplications(this.mPackageManager);
  }
  
  public Metadata getRestoredMetadata(String paramString)
  {
    if (this.mRestoredSignatures == null)
    {
      Slog.w("PMBA", "getRestoredMetadata() before metadata read!");
      return null;
    }
    return (Metadata)this.mRestoredSignatures.get(paramString);
  }
  
  public Set<String> getRestoredPackages()
  {
    if (this.mRestoredSignatures == null)
    {
      Slog.w("PMBA", "getRestoredPackages() before metadata read!");
      return null;
    }
    return this.mRestoredSignatures.keySet();
  }
  
  public boolean hasMetadata()
  {
    return this.mHasMetadata;
  }
  
  public void onBackup(ParcelFileDescriptor paramParcelFileDescriptor1, BackupDataOutput paramBackupDataOutput, ParcelFileDescriptor paramParcelFileDescriptor2)
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    DataOutputStream localDataOutputStream = new DataOutputStream(localByteArrayOutputStream);
    parseStateFile(paramParcelFileDescriptor1);
    if ((this.mStoredIncrementalVersion != null) && (this.mStoredIncrementalVersion.equals(Build.VERSION.INCREMENTAL))) {}
    Object localObject4;
    Object localObject1;
    long l1;
    Object localObject3;
    for (;;)
    {
      long l2 = 0L;
      localObject4 = null;
      Object localObject5 = null;
      localObject1 = null;
      ArrayList localArrayList = null;
      paramParcelFileDescriptor1 = null;
      ComponentName localComponentName2 = getPreferredHomeComponent();
      Object localObject2 = localComponentName2;
      l1 = l2;
      localObject3 = localObject4;
      if (localComponentName2 != null)
      {
        l1 = l2;
        localObject1 = localObject5;
        paramParcelFileDescriptor1 = localArrayList;
      }
      do
      {
        for (;;)
        {
          try
          {
            localObject2 = this.mPackageManager.getPackageInfo(localComponentName2.getPackageName(), 64);
            l1 = l2;
            localObject1 = localObject2;
            paramParcelFileDescriptor1 = localArrayList;
            localObject3 = this.mPackageManager.getInstallerPackageName(localComponentName2.getPackageName());
            l1 = l2;
            localObject1 = localObject2;
            paramParcelFileDescriptor1 = (ParcelFileDescriptor)localObject3;
            l2 = ((PackageInfo)localObject2).versionCode;
            l1 = l2;
            localObject1 = localObject2;
            paramParcelFileDescriptor1 = (ParcelFileDescriptor)localObject3;
            localArrayList = BackupUtils.hashSignatureArray(((PackageInfo)localObject2).signatures);
            localObject4 = localArrayList;
            paramParcelFileDescriptor1 = (ParcelFileDescriptor)localObject3;
            localObject1 = localObject2;
            localObject3 = localObject4;
            l1 = l2;
            localObject2 = localComponentName2;
          }
          catch (PackageManager.NameNotFoundException localNameNotFoundException1)
          {
            boolean bool;
            Slog.w("PMBA", "Can't access preferred home info");
            ComponentName localComponentName1 = null;
            localObject3 = localObject4;
            continue;
            int i = 1;
            continue;
            i = 1;
            continue;
            i = 0;
            continue;
            paramParcelFileDescriptor1 = "";
            continue;
            paramBackupDataOutput.writeEntityHeader("@home@", -1);
            continue;
            this.mExisting.remove("@meta@");
            continue;
            continue;
            localByteArrayOutputStream.reset();
            localDataOutputStream.writeInt(localNameNotFoundException2.versionCode);
            writeSignatureHashArray(localDataOutputStream, BackupUtils.hashSignatureArray(localNameNotFoundException2.signatures));
            writeEntity(paramBackupDataOutput, (String)localObject1, localByteArrayOutputStream.toByteArray());
            continue;
            writeStateFile(this.mAllPackages, localComponentName1, l1, (ArrayList)localObject3, paramParcelFileDescriptor2);
          }
          try
          {
            if ((l1 != this.mStoredHomeVersion) || (!Objects.equals(localObject2, this.mStoredHomeComponent))) {
              continue;
            }
            if (localObject2 == null) {
              continue;
            }
            if (!BackupUtils.signaturesMatch(this.mStoredHomeSigHashes, (PackageInfo)localObject1)) {
              continue;
            }
            i = 0;
            if (i != 0)
            {
              if (localObject2 == null) {
                continue;
              }
              localDataOutputStream.writeUTF(((ComponentName)localObject2).flattenToString());
              localDataOutputStream.writeLong(l1);
              if (paramParcelFileDescriptor1 == null) {
                continue;
              }
              localDataOutputStream.writeUTF(paramParcelFileDescriptor1);
              writeSignatureHashArray(localDataOutputStream, (ArrayList)localObject3);
              writeEntity(paramBackupDataOutput, "@home@", localByteArrayOutputStream.toByteArray());
            }
            localByteArrayOutputStream.reset();
            if (this.mExisting.contains("@meta@")) {
              continue;
            }
            localDataOutputStream.writeInt(Build.VERSION.SDK_INT);
            localDataOutputStream.writeUTF(Build.VERSION.INCREMENTAL);
            writeEntity(paramBackupDataOutput, "@meta@", localByteArrayOutputStream.toByteArray());
            paramParcelFileDescriptor1 = this.mAllPackages.iterator();
          }
          catch (IOException paramParcelFileDescriptor1)
          {
            try
            {
              localObject4 = this.mPackageManager.getPackageInfo((String)localObject1, 64);
              if (this.mExisting.contains(localObject1))
              {
                this.mExisting.remove(localObject1);
                if (((PackageInfo)localObject4).versionCode == ((Metadata)this.mStateVersions.get(localObject1)).versionCode) {
                  continue;
                }
              }
              if ((((PackageInfo)localObject4).signatures != null) && (((PackageInfo)localObject4).signatures.length != 0)) {
                continue;
              }
              Slog.w("PMBA", "Not backing up package " + (String)localObject1 + " since it appears to have no signatures.");
            }
            catch (PackageManager.NameNotFoundException localNameNotFoundException2)
            {
              this.mExisting.add(localObject1);
            }
            paramParcelFileDescriptor1 = paramParcelFileDescriptor1;
            Slog.e("PMBA", "Unable to write package backup data file!");
            return;
          }
        }
        if (!paramParcelFileDescriptor1.hasNext()) {
          break label694;
        }
        localObject1 = ((PackageInfo)paramParcelFileDescriptor1.next()).packageName;
        bool = ((String)localObject1).equals("@meta@");
      } while (bool);
      Slog.i("PMBA", "Previous metadata " + this.mStoredIncrementalVersion + " mismatch vs " + Build.VERSION.INCREMENTAL + " - rewriting");
      this.mExisting.clear();
    }
    label694:
  }
  
  public void onRestore(BackupDataInput paramBackupDataInput, int paramInt, ParcelFileDescriptor paramParcelFileDescriptor)
    throws IOException
  {
    paramParcelFileDescriptor = new ArrayList();
    HashMap localHashMap = new HashMap();
    while (paramBackupDataInput.readNextHeader())
    {
      String str = paramBackupDataInput.getKey();
      paramInt = paramBackupDataInput.getDataSize();
      Object localObject = new byte[paramInt];
      paramBackupDataInput.readEntityData((byte[])localObject, 0, paramInt);
      localObject = new DataInputStream(new ByteArrayInputStream((byte[])localObject));
      if (str.equals("@meta@"))
      {
        paramInt = ((DataInputStream)localObject).readInt();
        if (-1 > Build.VERSION.SDK_INT)
        {
          Slog.w("PMBA", "Restore set was from a later version of Android; not restoring");
          return;
        }
        this.mStoredSdkVersion = paramInt;
        this.mStoredIncrementalVersion = ((DataInputStream)localObject).readUTF();
        this.mHasMetadata = true;
      }
      else if (str.equals("@home@"))
      {
        this.mRestoredHome = ComponentName.unflattenFromString(((DataInputStream)localObject).readUTF());
        this.mRestoredHomeVersion = ((DataInputStream)localObject).readLong();
        this.mRestoredHomeInstaller = ((DataInputStream)localObject).readUTF();
        this.mRestoredHomeSigHashes = readSignatureHashArray((DataInputStream)localObject);
      }
      else
      {
        paramInt = ((DataInputStream)localObject).readInt();
        localObject = readSignatureHashArray((DataInputStream)localObject);
        if ((localObject == null) || (((ArrayList)localObject).size() == 0))
        {
          Slog.w("PMBA", "Not restoring package " + str + " since it appears to have no signatures.");
        }
        else
        {
          ApplicationInfo localApplicationInfo = new ApplicationInfo();
          localApplicationInfo.packageName = str;
          paramParcelFileDescriptor.add(localApplicationInfo);
          localHashMap.put(str, new Metadata(paramInt, (ArrayList)localObject));
        }
      }
    }
    this.mRestoredSignatures = localHashMap;
  }
  
  public class Metadata
  {
    public ArrayList<byte[]> sigHashes;
    public int versionCode;
    
    Metadata(ArrayList<byte[]> paramArrayList)
    {
      this.versionCode = paramArrayList;
      ArrayList localArrayList;
      this.sigHashes = localArrayList;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/backup/PackageManagerBackupAgent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */