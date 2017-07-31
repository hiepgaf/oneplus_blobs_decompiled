package android.os.storage;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.UserHandle;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Preconditions;
import java.io.CharArrayWriter;
import java.io.File;

public final class StorageVolume
  implements Parcelable
{
  private static final String ACTION_OPEN_EXTERNAL_DIRECTORY = "android.os.storage.action.OPEN_EXTERNAL_DIRECTORY";
  public static final Parcelable.Creator<StorageVolume> CREATOR = new Parcelable.Creator()
  {
    public StorageVolume createFromParcel(Parcel paramAnonymousParcel)
    {
      return new StorageVolume(paramAnonymousParcel, null);
    }
    
    public StorageVolume[] newArray(int paramAnonymousInt)
    {
      return new StorageVolume[paramAnonymousInt];
    }
  };
  public static final String EXTRA_DIRECTORY_NAME = "android.os.storage.extra.DIRECTORY_NAME";
  public static final String EXTRA_STORAGE_VOLUME = "android.os.storage.extra.STORAGE_VOLUME";
  public static final int STORAGE_ID_INVALID = 0;
  public static final int STORAGE_ID_PRIMARY = 65537;
  private final boolean mAllowMassStorage;
  private final String mDescription;
  private final boolean mEmulated;
  private final String mFsUuid;
  private final String mId;
  private final long mMaxFileSize;
  private final long mMtpReserveSize;
  private final UserHandle mOwner;
  private final File mPath;
  private final boolean mPrimary;
  private final boolean mRemovable;
  private final String mState;
  private final int mStorageId;
  
  private StorageVolume(Parcel paramParcel)
  {
    this.mId = paramParcel.readString();
    this.mStorageId = paramParcel.readInt();
    this.mPath = new File(paramParcel.readString());
    this.mDescription = paramParcel.readString();
    if (paramParcel.readInt() != 0)
    {
      bool1 = true;
      this.mPrimary = bool1;
      if (paramParcel.readInt() == 0) {
        break label151;
      }
      bool1 = true;
      label68:
      this.mRemovable = bool1;
      if (paramParcel.readInt() == 0) {
        break label156;
      }
      bool1 = true;
      label82:
      this.mEmulated = bool1;
      this.mMtpReserveSize = paramParcel.readLong();
      if (paramParcel.readInt() == 0) {
        break label161;
      }
    }
    label151:
    label156:
    label161:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.mAllowMassStorage = bool1;
      this.mMaxFileSize = paramParcel.readLong();
      this.mOwner = ((UserHandle)paramParcel.readParcelable(null));
      this.mFsUuid = paramParcel.readString();
      this.mState = paramParcel.readString();
      return;
      bool1 = false;
      break;
      bool1 = false;
      break label68;
      bool1 = false;
      break label82;
    }
  }
  
  public StorageVolume(String paramString1, int paramInt, File paramFile, String paramString2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, long paramLong1, boolean paramBoolean4, long paramLong2, UserHandle paramUserHandle, String paramString3, String paramString4)
  {
    this.mId = ((String)Preconditions.checkNotNull(paramString1));
    this.mStorageId = paramInt;
    this.mPath = ((File)Preconditions.checkNotNull(paramFile));
    this.mDescription = ((String)Preconditions.checkNotNull(paramString2));
    this.mPrimary = paramBoolean1;
    this.mRemovable = paramBoolean2;
    this.mEmulated = paramBoolean3;
    this.mMtpReserveSize = paramLong1;
    this.mAllowMassStorage = paramBoolean4;
    this.mMaxFileSize = paramLong2;
    this.mOwner = ((UserHandle)Preconditions.checkNotNull(paramUserHandle));
    this.mFsUuid = paramString3;
    this.mState = ((String)Preconditions.checkNotNull(paramString4));
  }
  
  public boolean allowMassStorage()
  {
    return this.mAllowMassStorage;
  }
  
  public Intent createAccessIntent(String paramString)
  {
    if ((isPrimary()) && (paramString == null)) {}
    while ((paramString != null) && (!Environment.isStandardDirectory(paramString))) {
      return null;
    }
    Intent localIntent = new Intent("android.os.storage.action.OPEN_EXTERNAL_DIRECTORY");
    localIntent.putExtra("android.os.storage.extra.STORAGE_VOLUME", this);
    localIntent.putExtra("android.os.storage.extra.DIRECTORY_NAME", paramString);
    return localIntent;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String dump()
  {
    CharArrayWriter localCharArrayWriter = new CharArrayWriter();
    dump(new IndentingPrintWriter(localCharArrayWriter, "    ", 80));
    return localCharArrayWriter.toString();
  }
  
  public void dump(IndentingPrintWriter paramIndentingPrintWriter)
  {
    paramIndentingPrintWriter.println("StorageVolume:");
    paramIndentingPrintWriter.increaseIndent();
    paramIndentingPrintWriter.printPair("mId", this.mId);
    paramIndentingPrintWriter.printPair("mStorageId", Integer.valueOf(this.mStorageId));
    paramIndentingPrintWriter.printPair("mPath", this.mPath);
    paramIndentingPrintWriter.printPair("mDescription", this.mDescription);
    paramIndentingPrintWriter.printPair("mPrimary", Boolean.valueOf(this.mPrimary));
    paramIndentingPrintWriter.printPair("mRemovable", Boolean.valueOf(this.mRemovable));
    paramIndentingPrintWriter.printPair("mEmulated", Boolean.valueOf(this.mEmulated));
    paramIndentingPrintWriter.printPair("mMtpReserveSize", Long.valueOf(this.mMtpReserveSize));
    paramIndentingPrintWriter.printPair("mAllowMassStorage", Boolean.valueOf(this.mAllowMassStorage));
    paramIndentingPrintWriter.printPair("mMaxFileSize", Long.valueOf(this.mMaxFileSize));
    paramIndentingPrintWriter.printPair("mOwner", this.mOwner);
    paramIndentingPrintWriter.printPair("mFsUuid", this.mFsUuid);
    paramIndentingPrintWriter.printPair("mState", this.mState);
    paramIndentingPrintWriter.decreaseIndent();
  }
  
  public boolean equals(Object paramObject)
  {
    if (((paramObject instanceof StorageVolume)) && (this.mPath != null))
    {
      paramObject = (StorageVolume)paramObject;
      return this.mPath.equals(((StorageVolume)paramObject).mPath);
    }
    return false;
  }
  
  public String getDescription(Context paramContext)
  {
    return this.mDescription;
  }
  
  public int getFatVolumeId()
  {
    if ((this.mFsUuid == null) || (this.mFsUuid.length() != 9)) {
      return -1;
    }
    try
    {
      long l = Long.parseLong(this.mFsUuid.replace("-", ""), 16);
      return (int)l;
    }
    catch (NumberFormatException localNumberFormatException) {}
    return -1;
  }
  
  public String getId()
  {
    return this.mId;
  }
  
  public long getMaxFileSize()
  {
    return this.mMaxFileSize;
  }
  
  public int getMtpReserveSpace()
  {
    return (int)(this.mMtpReserveSize / 1048576L);
  }
  
  public UserHandle getOwner()
  {
    return this.mOwner;
  }
  
  public String getPath()
  {
    return this.mPath.toString();
  }
  
  public File getPathFile()
  {
    return this.mPath;
  }
  
  public String getState()
  {
    return this.mState;
  }
  
  public int getStorageId()
  {
    return this.mStorageId;
  }
  
  public String getUserLabel()
  {
    return this.mDescription;
  }
  
  public String getUuid()
  {
    return this.mFsUuid;
  }
  
  public int hashCode()
  {
    return this.mPath.hashCode();
  }
  
  public boolean isEmulated()
  {
    return this.mEmulated;
  }
  
  public boolean isPrimary()
  {
    return this.mPrimary;
  }
  
  public boolean isRemovable()
  {
    return this.mRemovable;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("StorageVolume: ").append(this.mDescription);
    if (this.mFsUuid != null) {
      localStringBuilder.append(" (").append(this.mFsUuid).append(")");
    }
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int j = 1;
    paramParcel.writeString(this.mId);
    paramParcel.writeInt(this.mStorageId);
    paramParcel.writeString(this.mPath.toString());
    paramParcel.writeString(this.mDescription);
    if (this.mPrimary)
    {
      i = 1;
      paramParcel.writeInt(i);
      if (!this.mRemovable) {
        break label142;
      }
      i = 1;
      label61:
      paramParcel.writeInt(i);
      if (!this.mEmulated) {
        break label147;
      }
      i = 1;
      label75:
      paramParcel.writeInt(i);
      paramParcel.writeLong(this.mMtpReserveSize);
      if (!this.mAllowMassStorage) {
        break label152;
      }
    }
    label142:
    label147:
    label152:
    for (int i = j;; i = 0)
    {
      paramParcel.writeInt(i);
      paramParcel.writeLong(this.mMaxFileSize);
      paramParcel.writeParcelable(this.mOwner, paramInt);
      paramParcel.writeString(this.mFsUuid);
      paramParcel.writeString(this.mState);
      return;
      i = 0;
      break;
      i = 0;
      break label61;
      i = 0;
      break label75;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/storage/StorageVolume.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */