package android.os.storage;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.UserHandle;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.DebugUtils;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Preconditions;
import java.io.CharArrayWriter;
import java.io.File;
import java.util.Comparator;
import java.util.Objects;

public class VolumeInfo
  implements Parcelable
{
  public static final String ACTION_VOLUME_STATE_CHANGED = "android.os.storage.action.VOLUME_STATE_CHANGED";
  public static final Parcelable.Creator<VolumeInfo> CREATOR = new Parcelable.Creator()
  {
    public VolumeInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new VolumeInfo(paramAnonymousParcel);
    }
    
    public VolumeInfo[] newArray(int paramAnonymousInt)
    {
      return new VolumeInfo[paramAnonymousInt];
    }
  };
  private static final String DOCUMENT_AUTHORITY = "com.android.externalstorage.documents";
  private static final String DOCUMENT_ROOT_PRIMARY_EMULATED = "primary";
  public static final String EXTRA_VOLUME_ID = "android.os.storage.extra.VOLUME_ID";
  public static final String EXTRA_VOLUME_STATE = "android.os.storage.extra.VOLUME_STATE";
  public static final String ID_EMULATED_INTERNAL = "emulated";
  public static final String ID_PRIVATE_INTERNAL = "private";
  public static final int MOUNT_FLAG_PRIMARY = 1;
  public static final int MOUNT_FLAG_VISIBLE = 2;
  public static final int STATE_BAD_REMOVAL = 8;
  public static final int STATE_CHECKING = 1;
  public static final int STATE_EJECTING = 5;
  public static final int STATE_FORMATTING = 4;
  public static final int STATE_MOUNTED = 2;
  public static final int STATE_MOUNTED_READ_ONLY = 3;
  public static final int STATE_REMOVED = 7;
  public static final int STATE_UNMOUNTABLE = 6;
  public static final int STATE_UNMOUNTED = 0;
  public static final int TYPE_ASEC = 3;
  public static final int TYPE_EMULATED = 2;
  public static final int TYPE_OBB = 4;
  public static final int TYPE_PRIVATE = 1;
  public static final int TYPE_PUBLIC = 0;
  private static final Comparator<VolumeInfo> sDescriptionComparator;
  private static ArrayMap<String, String> sEnvironmentToBroadcast;
  private static SparseIntArray sStateToDescrip;
  private static SparseArray<String> sStateToEnvironment = new SparseArray();
  public final DiskInfo disk;
  public String fsLabel;
  public String fsType;
  public String fsUuid;
  public final String id;
  public String internalPath;
  public int mountFlags = 0;
  public int mountUserId = -1;
  public final String partGuid;
  public String path;
  public int state = 0;
  public final int type;
  
  static
  {
    sEnvironmentToBroadcast = new ArrayMap();
    sStateToDescrip = new SparseIntArray();
    sDescriptionComparator = new Comparator()
    {
      public int compare(VolumeInfo paramAnonymousVolumeInfo1, VolumeInfo paramAnonymousVolumeInfo2)
      {
        if ("private".equals(paramAnonymousVolumeInfo1.getId())) {
          return -1;
        }
        if (paramAnonymousVolumeInfo1.getDescription() == null) {
          return 1;
        }
        if (paramAnonymousVolumeInfo2.getDescription() == null) {
          return -1;
        }
        return paramAnonymousVolumeInfo1.getDescription().compareTo(paramAnonymousVolumeInfo2.getDescription());
      }
    };
    sStateToEnvironment.put(0, "unmounted");
    sStateToEnvironment.put(1, "checking");
    sStateToEnvironment.put(2, "mounted");
    sStateToEnvironment.put(3, "mounted_ro");
    sStateToEnvironment.put(4, "unmounted");
    sStateToEnvironment.put(5, "ejecting");
    sStateToEnvironment.put(6, "unmountable");
    sStateToEnvironment.put(7, "removed");
    sStateToEnvironment.put(8, "bad_removal");
    sEnvironmentToBroadcast.put("unmounted", "android.intent.action.MEDIA_UNMOUNTED");
    sEnvironmentToBroadcast.put("checking", "android.intent.action.MEDIA_CHECKING");
    sEnvironmentToBroadcast.put("mounted", "android.intent.action.MEDIA_MOUNTED");
    sEnvironmentToBroadcast.put("mounted_ro", "android.intent.action.MEDIA_MOUNTED");
    sEnvironmentToBroadcast.put("ejecting", "android.intent.action.MEDIA_EJECT");
    sEnvironmentToBroadcast.put("unmountable", "android.intent.action.MEDIA_UNMOUNTABLE");
    sEnvironmentToBroadcast.put("removed", "android.intent.action.MEDIA_REMOVED");
    sEnvironmentToBroadcast.put("bad_removal", "android.intent.action.MEDIA_BAD_REMOVAL");
    sStateToDescrip.put(0, 17040475);
    sStateToDescrip.put(1, 17040476);
    sStateToDescrip.put(2, 17040477);
    sStateToDescrip.put(3, 17040478);
    sStateToDescrip.put(4, 17040483);
    sStateToDescrip.put(5, 17040482);
    sStateToDescrip.put(6, 17040480);
    sStateToDescrip.put(7, 17040474);
    sStateToDescrip.put(8, 17040479);
  }
  
  public VolumeInfo(Parcel paramParcel)
  {
    this.id = paramParcel.readString();
    this.type = paramParcel.readInt();
    if (paramParcel.readInt() != 0) {}
    for (this.disk = ((DiskInfo)DiskInfo.CREATOR.createFromParcel(paramParcel));; this.disk = null)
    {
      this.partGuid = paramParcel.readString();
      this.mountFlags = paramParcel.readInt();
      this.mountUserId = paramParcel.readInt();
      this.state = paramParcel.readInt();
      this.fsType = paramParcel.readString();
      this.fsUuid = paramParcel.readString();
      this.fsLabel = paramParcel.readString();
      this.path = paramParcel.readString();
      this.internalPath = paramParcel.readString();
      return;
    }
  }
  
  public VolumeInfo(String paramString1, int paramInt, DiskInfo paramDiskInfo, String paramString2)
  {
    this.id = ((String)Preconditions.checkNotNull(paramString1));
    this.type = paramInt;
    this.disk = paramDiskInfo;
    this.partGuid = paramString2;
  }
  
  public static int buildStableMtpStorageId(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return 0;
    }
    int j = 0;
    int i = 0;
    while (i < paramString.length())
    {
      j = j * 31 + paramString.charAt(i);
      i += 1;
    }
    j = (j << 16 ^ j) & 0xFFFF0000;
    i = j;
    if (j == 0) {
      i = 131072;
    }
    j = i;
    if (i == 65536) {
      j = 131072;
    }
    i = j;
    if (j == -65536) {
      i = -131072;
    }
    return i | 0x1;
  }
  
  public static String getBroadcastForEnvironment(String paramString)
  {
    return (String)sEnvironmentToBroadcast.get(paramString);
  }
  
  public static String getBroadcastForState(int paramInt)
  {
    return getBroadcastForEnvironment(getEnvironmentForState(paramInt));
  }
  
  public static Comparator<VolumeInfo> getDescriptionComparator()
  {
    return sDescriptionComparator;
  }
  
  public static String getEnvironmentForState(int paramInt)
  {
    String str = (String)sStateToEnvironment.get(paramInt);
    if (str != null) {
      return str;
    }
    return "unknown";
  }
  
  public Intent buildBrowseIntent()
  {
    if (this.type == 0) {}
    for (Uri localUri = DocumentsContract.buildRootUri("com.android.externalstorage.documents", this.fsUuid);; localUri = DocumentsContract.buildRootUri("com.android.externalstorage.documents", "primary"))
    {
      Intent localIntent = new Intent("android.provider.action.BROWSE");
      localIntent.addCategory("android.intent.category.DEFAULT");
      localIntent.setDataAndType(localUri, "vnd.android.document/root");
      localIntent.putExtra("android.content.extra.SHOW_ADVANCED", isPrimary());
      localIntent.putExtra("android.content.extra.FANCY", true);
      localIntent.putExtra("android.content.extra.SHOW_FILESIZE", true);
      return localIntent;
      if ((this.type != 2) || (!isPrimary())) {
        break;
      }
    }
    return null;
  }
  
  public StorageVolume buildStorageVolume(Context paramContext, int paramInt, boolean paramBoolean)
  {
    Object localObject4 = (StorageManager)paramContext.getSystemService(StorageManager.class);
    String str;
    Object localObject1;
    Object localObject3;
    Object localObject2;
    long l3;
    long l2;
    int i;
    boolean bool1;
    Object localObject5;
    long l1;
    int j;
    if (paramBoolean)
    {
      str = "unmounted";
      localObject1 = getPathForUser(paramInt);
      localObject3 = localObject1;
      if (localObject1 == null) {
        localObject3 = new File("/dev/null");
      }
      localObject1 = null;
      localObject2 = this.fsUuid;
      l3 = 0L;
      l2 = 0L;
      i = 0;
      if (this.type != 2) {
        break label234;
      }
      bool1 = true;
      localObject5 = ((StorageManager)localObject4).findPrivateForEmulated(this);
      if (localObject5 != null)
      {
        localObject1 = ((StorageManager)localObject4).getBestVolumeDescription((VolumeInfo)localObject5);
        localObject2 = ((VolumeInfo)localObject5).fsUuid;
      }
      if (isPrimary()) {
        i = 65537;
      }
      l1 = ((StorageManager)localObject4).getStorageLowBytes((File)localObject3);
      if (!"emulated".equals(this.id)) {
        break label221;
      }
      paramBoolean = false;
      localObject4 = localObject2;
      j = i;
    }
    for (;;)
    {
      localObject2 = localObject1;
      if (localObject1 == null) {
        localObject2 = paramContext.getString(17039374);
      }
      return new StorageVolume(this.id, j, (File)localObject3, (String)localObject2, isPrimary(), paramBoolean, bool1, l1, false, l2, new UserHandle(paramInt), (String)localObject4, str);
      str = getEnvironmentForState(this.state);
      break;
      label221:
      paramBoolean = true;
      j = i;
      localObject4 = localObject2;
    }
    label234:
    if (this.type == 0)
    {
      boolean bool2 = false;
      boolean bool3 = true;
      localObject5 = ((StorageManager)localObject4).getBestVolumeDescription(this);
      if (isPrimary()) {}
      for (i = 65537;; i = buildStableMtpStorageId(this.fsUuid))
      {
        j = i;
        localObject1 = localObject5;
        paramBoolean = bool3;
        bool1 = bool2;
        l1 = l3;
        localObject4 = localObject2;
        if (!"vfat".equals(this.fsType)) {
          break;
        }
        l2 = 4294967295L;
        j = i;
        localObject1 = localObject5;
        paramBoolean = bool3;
        bool1 = bool2;
        l1 = l3;
        localObject4 = localObject2;
        break;
      }
    }
    throw new IllegalStateException("Unexpected volume type " + this.type);
  }
  
  public VolumeInfo clone()
  {
    Parcel localParcel = Parcel.obtain();
    try
    {
      writeToParcel(localParcel, 0);
      localParcel.setDataPosition(0);
      VolumeInfo localVolumeInfo = (VolumeInfo)CREATOR.createFromParcel(localParcel);
      return localVolumeInfo;
    }
    finally
    {
      localParcel.recycle();
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void dump(IndentingPrintWriter paramIndentingPrintWriter)
  {
    paramIndentingPrintWriter.println("VolumeInfo{" + this.id + "}:");
    paramIndentingPrintWriter.increaseIndent();
    paramIndentingPrintWriter.printPair("type", DebugUtils.valueToString(getClass(), "TYPE_", this.type));
    paramIndentingPrintWriter.printPair("diskId", getDiskId());
    paramIndentingPrintWriter.printPair("partGuid", this.partGuid);
    paramIndentingPrintWriter.printPair("mountFlags", DebugUtils.flagsToString(getClass(), "MOUNT_FLAG_", this.mountFlags));
    paramIndentingPrintWriter.printPair("mountUserId", Integer.valueOf(this.mountUserId));
    paramIndentingPrintWriter.printPair("state", DebugUtils.valueToString(getClass(), "STATE_", this.state));
    paramIndentingPrintWriter.println();
    paramIndentingPrintWriter.printPair("fsType", this.fsType);
    paramIndentingPrintWriter.printPair("fsUuid", this.fsUuid);
    paramIndentingPrintWriter.printPair("fsLabel", this.fsLabel);
    paramIndentingPrintWriter.println();
    paramIndentingPrintWriter.printPair("path", this.path);
    paramIndentingPrintWriter.printPair("internalPath", this.internalPath);
    paramIndentingPrintWriter.decreaseIndent();
    paramIndentingPrintWriter.println();
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof VolumeInfo)) {
      return Objects.equals(this.id, ((VolumeInfo)paramObject).id);
    }
    return false;
  }
  
  public String getDescription()
  {
    if (("private".equals(this.id)) || ("emulated".equals(this.id))) {
      return Resources.getSystem().getString(17040605);
    }
    if (!TextUtils.isEmpty(this.fsLabel)) {
      return this.fsLabel;
    }
    return null;
  }
  
  public DiskInfo getDisk()
  {
    return this.disk;
  }
  
  public String getDiskId()
  {
    String str = null;
    if (this.disk != null) {
      str = this.disk.id;
    }
    return str;
  }
  
  public String getFsUuid()
  {
    return this.fsUuid;
  }
  
  public String getId()
  {
    return this.id;
  }
  
  public File getInternalPath()
  {
    File localFile = null;
    if (this.internalPath != null) {
      localFile = new File(this.internalPath);
    }
    return localFile;
  }
  
  public File getInternalPathForUser(int paramInt)
  {
    if (this.type == 0) {
      return new File(this.path.replace("/storage/", "/mnt/media_rw/"));
    }
    return getPathForUser(paramInt);
  }
  
  public int getMountUserId()
  {
    return this.mountUserId;
  }
  
  public File getPath()
  {
    File localFile = null;
    if (this.path != null) {
      localFile = new File(this.path);
    }
    return localFile;
  }
  
  public File getPathForUser(int paramInt)
  {
    if (this.path == null) {
      return null;
    }
    if (this.type == 0) {
      return new File(this.path);
    }
    if (this.type == 2) {
      return new File(this.path, Integer.toString(paramInt));
    }
    return null;
  }
  
  public int getState()
  {
    return this.state;
  }
  
  public int getStateDescription()
  {
    return sStateToDescrip.get(this.state, 0);
  }
  
  public int getType()
  {
    return this.type;
  }
  
  public int hashCode()
  {
    return this.id.hashCode();
  }
  
  public boolean isMountedReadable()
  {
    return (this.state == 2) || (this.state == 3);
  }
  
  public boolean isMountedWritable()
  {
    return this.state == 2;
  }
  
  public boolean isPrimary()
  {
    boolean bool = false;
    if ((this.mountFlags & 0x1) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isPrimaryPhysical()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (isPrimary())
    {
      bool1 = bool2;
      if (getType() == 0) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public boolean isVisible()
  {
    boolean bool = false;
    if ((this.mountFlags & 0x2) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isVisibleForRead(int paramInt)
  {
    if (this.type == 0)
    {
      if ((isPrimary()) && (this.mountUserId != paramInt)) {
        return false;
      }
      return isVisible();
    }
    if (this.type == 2) {
      return isVisible();
    }
    return false;
  }
  
  public boolean isVisibleForWrite(int paramInt)
  {
    if ((this.type == 0) && (this.mountUserId == paramInt)) {
      return isVisible();
    }
    if (this.type == 2) {
      return isVisible();
    }
    return false;
  }
  
  public String toString()
  {
    CharArrayWriter localCharArrayWriter = new CharArrayWriter();
    dump(new IndentingPrintWriter(localCharArrayWriter, "    ", 80));
    return localCharArrayWriter.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.id);
    paramParcel.writeInt(this.type);
    if (this.disk != null)
    {
      paramParcel.writeInt(1);
      this.disk.writeToParcel(paramParcel, paramInt);
    }
    for (;;)
    {
      paramParcel.writeString(this.partGuid);
      paramParcel.writeInt(this.mountFlags);
      paramParcel.writeInt(this.mountUserId);
      paramParcel.writeInt(this.state);
      paramParcel.writeString(this.fsType);
      paramParcel.writeString(this.fsUuid);
      paramParcel.writeString(this.fsLabel);
      paramParcel.writeString(this.path);
      paramParcel.writeString(this.internalPath);
      return;
      paramParcel.writeInt(0);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/storage/VolumeInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */