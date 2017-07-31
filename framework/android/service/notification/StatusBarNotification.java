package android.service.notification;

import android.app.Notification;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.UserHandle;

public class StatusBarNotification
  implements Parcelable
{
  public static final Parcelable.Creator<StatusBarNotification> CREATOR = new Parcelable.Creator()
  {
    public StatusBarNotification createFromParcel(Parcel paramAnonymousParcel)
    {
      return new StatusBarNotification(paramAnonymousParcel);
    }
    
    public StatusBarNotification[] newArray(int paramAnonymousInt)
    {
      return new StatusBarNotification[paramAnonymousInt];
    }
  };
  private String groupKey;
  private final int id;
  private final int initialPid;
  private final String key;
  private Context mContext;
  private boolean newPosted;
  private final Notification notification;
  private final String opPkg;
  private String overrideGroupKey;
  private final String pkg;
  private final long postTime;
  private final String tag;
  private final int uid;
  private final UserHandle user;
  
  public StatusBarNotification(Parcel paramParcel)
  {
    this.pkg = paramParcel.readString();
    this.opPkg = paramParcel.readString();
    this.id = paramParcel.readInt();
    if (paramParcel.readInt() != 0)
    {
      this.tag = paramParcel.readString();
      this.uid = paramParcel.readInt();
      this.initialPid = paramParcel.readInt();
      this.notification = new Notification(paramParcel);
      this.user = UserHandle.readFromParcel(paramParcel);
      this.postTime = paramParcel.readLong();
      if (paramParcel.readInt() == 0) {
        break label127;
      }
    }
    label127:
    for (this.overrideGroupKey = paramParcel.readString();; this.overrideGroupKey = null)
    {
      this.key = key();
      this.groupKey = groupKey();
      return;
      this.tag = null;
      break;
    }
  }
  
  public StatusBarNotification(String paramString1, String paramString2, int paramInt1, String paramString3, int paramInt2, int paramInt3, int paramInt4, Notification paramNotification, UserHandle paramUserHandle)
  {
    this(paramString1, paramString2, paramInt1, paramString3, paramInt2, paramInt3, paramInt4, paramNotification, paramUserHandle, System.currentTimeMillis());
  }
  
  public StatusBarNotification(String paramString1, String paramString2, int paramInt1, String paramString3, int paramInt2, int paramInt3, int paramInt4, Notification paramNotification, UserHandle paramUserHandle, long paramLong)
  {
    if (paramString1 == null) {
      throw new NullPointerException();
    }
    if (paramNotification == null) {
      throw new NullPointerException();
    }
    this.pkg = paramString1;
    this.opPkg = paramString2;
    this.id = paramInt1;
    this.tag = paramString3;
    this.uid = paramInt2;
    this.initialPid = paramInt3;
    this.notification = paramNotification;
    this.user = paramUserHandle;
    this.postTime = paramLong;
    this.key = key();
    this.groupKey = groupKey();
  }
  
  public StatusBarNotification(String paramString1, String paramString2, int paramInt1, String paramString3, int paramInt2, int paramInt3, Notification paramNotification, UserHandle paramUserHandle, String paramString4, long paramLong)
  {
    if (paramString1 == null) {
      throw new NullPointerException();
    }
    if (paramNotification == null) {
      throw new NullPointerException();
    }
    this.pkg = paramString1;
    this.opPkg = paramString2;
    this.id = paramInt1;
    this.tag = paramString3;
    this.uid = paramInt2;
    this.initialPid = paramInt3;
    this.notification = paramNotification;
    this.user = paramUserHandle;
    this.postTime = paramLong;
    this.overrideGroupKey = paramString4;
    this.key = key();
    this.groupKey = groupKey();
  }
  
  private String groupKey()
  {
    if (this.overrideGroupKey != null) {
      return this.user.getIdentifier() + "|" + this.pkg + "|" + "g:" + this.overrideGroupKey;
    }
    String str = getNotification().getGroup();
    Object localObject = getNotification().getSortKey();
    if ((str == null) && (localObject == null)) {
      return this.key;
    }
    localObject = new StringBuilder().append(this.user.getIdentifier()).append("|").append(this.pkg).append("|");
    if (str == null) {}
    for (str = "p:" + this.notification.priority;; str = "g:" + str) {
      return str;
    }
  }
  
  private String key()
  {
    String str2 = this.user.getIdentifier() + "|" + this.pkg + "|" + this.id + "|" + this.tag + "|" + this.uid;
    String str1 = str2;
    if (this.overrideGroupKey != null)
    {
      str1 = str2;
      if (getNotification().isGroupSummary()) {
        str1 = str2 + "|" + this.overrideGroupKey;
      }
    }
    return str1;
  }
  
  public StatusBarNotification clone()
  {
    return new StatusBarNotification(this.pkg, this.opPkg, this.id, this.tag, this.uid, this.initialPid, this.notification.clone(), this.user, this.overrideGroupKey, this.postTime);
  }
  
  public StatusBarNotification cloneLight()
  {
    Notification localNotification = new Notification();
    this.notification.cloneInto(localNotification, false);
    return new StatusBarNotification(this.pkg, this.opPkg, this.id, this.tag, this.uid, this.initialPid, localNotification, this.user, this.overrideGroupKey, this.postTime);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String getGroupKey()
  {
    return this.groupKey;
  }
  
  public int getId()
  {
    return this.id;
  }
  
  public int getInitialPid()
  {
    return this.initialPid;
  }
  
  public String getKey()
  {
    return this.key;
  }
  
  public Notification getNotification()
  {
    return this.notification;
  }
  
  public String getOpPkg()
  {
    return this.opPkg;
  }
  
  public String getOverrideGroupKey()
  {
    return this.overrideGroupKey;
  }
  
  public Context getPackageContext(Context paramContext)
  {
    if (this.mContext == null) {}
    try
    {
      this.mContext = paramContext.createApplicationContext(paramContext.getPackageManager().getApplicationInfo(this.pkg, 8192), 4);
      if (this.mContext == null) {
        this.mContext = paramContext;
      }
      return this.mContext;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      for (;;)
      {
        this.mContext = null;
      }
    }
  }
  
  public String getPackageName()
  {
    return this.pkg;
  }
  
  public long getPostTime()
  {
    return this.postTime;
  }
  
  public String getTag()
  {
    return this.tag;
  }
  
  public int getUid()
  {
    return this.uid;
  }
  
  public UserHandle getUser()
  {
    return this.user;
  }
  
  public int getUserId()
  {
    return this.user.getIdentifier();
  }
  
  public boolean isAppGroup()
  {
    return (getNotification().getGroup() != null) || (getNotification().getSortKey() != null);
  }
  
  public boolean isClearable()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if ((this.notification.flags & 0x2) == 0)
    {
      bool1 = bool2;
      if ((this.notification.flags & 0x20) == 0) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public boolean isGroup()
  {
    return (this.overrideGroupKey != null) || (isAppGroup());
  }
  
  public boolean isNewPosted()
  {
    return this.newPosted;
  }
  
  public boolean isOngoing()
  {
    boolean bool = false;
    if ((this.notification.flags & 0x2) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public void setNewPosted(boolean paramBoolean)
  {
    this.newPosted = paramBoolean;
  }
  
  public void setOverrideGroupKey(String paramString)
  {
    this.overrideGroupKey = paramString;
    this.groupKey = groupKey();
  }
  
  public String toString()
  {
    return String.format("StatusBarNotification(pkg=%s user=%s id=%d tag=%s key=%s: %s)", new Object[] { this.pkg, this.user, Integer.valueOf(this.id), this.tag, this.key, this.notification });
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.pkg);
    paramParcel.writeString(this.opPkg);
    paramParcel.writeInt(this.id);
    if (this.tag != null)
    {
      paramParcel.writeInt(1);
      paramParcel.writeString(this.tag);
    }
    for (;;)
    {
      paramParcel.writeInt(this.uid);
      paramParcel.writeInt(this.initialPid);
      this.notification.writeToParcel(paramParcel, paramInt);
      this.user.writeToParcel(paramParcel, paramInt);
      paramParcel.writeLong(this.postTime);
      if (this.overrideGroupKey == null) {
        break;
      }
      paramParcel.writeInt(1);
      paramParcel.writeString(this.overrideGroupKey);
      return;
      paramParcel.writeInt(0);
    }
    paramParcel.writeInt(0);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/notification/StatusBarNotification.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */