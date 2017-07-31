package android.content.pm;

import android.content.ComponentName;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Printer;
import android.util.Slog;
import java.text.Collator;
import java.util.Comparator;

public class ResolveInfo
  implements Parcelable
{
  public static final Parcelable.Creator<ResolveInfo> CREATOR = new Parcelable.Creator()
  {
    public ResolveInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ResolveInfo(paramAnonymousParcel, null);
    }
    
    public ResolveInfo[] newArray(int paramAnonymousInt)
    {
      return new ResolveInfo[paramAnonymousInt];
    }
  };
  private static final String TAG = "ResolveInfo";
  public ActivityInfo activityInfo;
  public EphemeralResolveInfo ephemeralResolveInfo;
  public IntentFilter filter;
  public boolean handleAllWebDataURI;
  public int icon;
  public int iconResourceId;
  public boolean isDefault;
  public int labelRes;
  public int match;
  public boolean noResourceId;
  public CharSequence nonLocalizedLabel;
  public int preferredOrder;
  public int priority;
  public ProviderInfo providerInfo;
  public String resolvePackageName;
  public ServiceInfo serviceInfo;
  public int specificIndex = -1;
  public boolean system;
  public int targetUserId;
  
  public ResolveInfo()
  {
    this.targetUserId = -2;
  }
  
  public ResolveInfo(ResolveInfo paramResolveInfo)
  {
    this.activityInfo = paramResolveInfo.activityInfo;
    this.serviceInfo = paramResolveInfo.serviceInfo;
    this.providerInfo = paramResolveInfo.providerInfo;
    this.filter = paramResolveInfo.filter;
    this.priority = paramResolveInfo.priority;
    this.preferredOrder = paramResolveInfo.preferredOrder;
    this.match = paramResolveInfo.match;
    this.specificIndex = paramResolveInfo.specificIndex;
    this.labelRes = paramResolveInfo.labelRes;
    this.nonLocalizedLabel = paramResolveInfo.nonLocalizedLabel;
    this.icon = paramResolveInfo.icon;
    this.resolvePackageName = paramResolveInfo.resolvePackageName;
    this.noResourceId = paramResolveInfo.noResourceId;
    this.iconResourceId = paramResolveInfo.iconResourceId;
    this.system = paramResolveInfo.system;
    this.targetUserId = paramResolveInfo.targetUserId;
    this.handleAllWebDataURI = paramResolveInfo.handleAllWebDataURI;
  }
  
  private ResolveInfo(Parcel paramParcel)
  {
    this.activityInfo = null;
    this.serviceInfo = null;
    this.providerInfo = null;
    switch (paramParcel.readInt())
    {
    default: 
      Slog.w("ResolveInfo", "Missing ComponentInfo!");
      if (paramParcel.readInt() != 0) {
        this.filter = ((IntentFilter)IntentFilter.CREATOR.createFromParcel(paramParcel));
      }
      this.priority = paramParcel.readInt();
      this.preferredOrder = paramParcel.readInt();
      this.match = paramParcel.readInt();
      this.specificIndex = paramParcel.readInt();
      this.labelRes = paramParcel.readInt();
      this.nonLocalizedLabel = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
      this.icon = paramParcel.readInt();
      this.resolvePackageName = paramParcel.readString();
      this.targetUserId = paramParcel.readInt();
      if (paramParcel.readInt() != 0)
      {
        bool1 = true;
        label176:
        this.system = bool1;
        if (paramParcel.readInt() == 0) {
          break label280;
        }
        bool1 = true;
        label190:
        this.noResourceId = bool1;
        this.iconResourceId = paramParcel.readInt();
        if (paramParcel.readInt() == 0) {
          break label285;
        }
      }
      break;
    }
    label280:
    label285:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.handleAllWebDataURI = bool1;
      return;
      this.activityInfo = ((ActivityInfo)ActivityInfo.CREATOR.createFromParcel(paramParcel));
      break;
      this.serviceInfo = ((ServiceInfo)ServiceInfo.CREATOR.createFromParcel(paramParcel));
      break;
      this.providerInfo = ((ProviderInfo)ProviderInfo.CREATOR.createFromParcel(paramParcel));
      break;
      bool1 = false;
      break label176;
      bool1 = false;
      break label190;
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void dump(Printer paramPrinter, String paramString)
  {
    dump(paramPrinter, paramString, 3);
  }
  
  public void dump(Printer paramPrinter, String paramString, int paramInt)
  {
    if (this.filter != null)
    {
      paramPrinter.println(paramString + "Filter:");
      this.filter.dump(paramPrinter, paramString + "  ");
    }
    paramPrinter.println(paramString + "priority=" + this.priority + " preferredOrder=" + this.preferredOrder + " match=0x" + Integer.toHexString(this.match) + " specificIndex=" + this.specificIndex + " isDefault=" + this.isDefault);
    if (this.resolvePackageName != null) {
      paramPrinter.println(paramString + "resolvePackageName=" + this.resolvePackageName);
    }
    if ((this.labelRes != 0) || (this.nonLocalizedLabel != null)) {
      paramPrinter.println(paramString + "labelRes=0x" + Integer.toHexString(this.labelRes) + " nonLocalizedLabel=" + this.nonLocalizedLabel + " icon=0x" + Integer.toHexString(this.icon));
    }
    do
    {
      for (;;)
      {
        if (this.activityInfo != null)
        {
          paramPrinter.println(paramString + "ActivityInfo:");
          this.activityInfo.dump(paramPrinter, paramString + "  ", paramInt);
          return;
          if (this.icon != 0) {
            break;
          }
        }
      }
      if (this.serviceInfo != null)
      {
        paramPrinter.println(paramString + "ServiceInfo:");
        this.serviceInfo.dump(paramPrinter, paramString + "  ", paramInt);
        return;
      }
    } while (this.providerInfo == null);
    paramPrinter.println(paramString + "ProviderInfo:");
    this.providerInfo.dump(paramPrinter, paramString + "  ", paramInt);
  }
  
  public ComponentInfo getComponentInfo()
  {
    if (this.activityInfo != null) {
      return this.activityInfo;
    }
    if (this.serviceInfo != null) {
      return this.serviceInfo;
    }
    if (this.providerInfo != null) {
      return this.providerInfo;
    }
    throw new IllegalStateException("Missing ComponentInfo!");
  }
  
  public final int getIconResource()
  {
    if (this.noResourceId) {
      return 0;
    }
    return getIconResourceInternal();
  }
  
  final int getIconResourceInternal()
  {
    if (this.iconResourceId != 0) {
      return this.iconResourceId;
    }
    ComponentInfo localComponentInfo = getComponentInfo();
    if (localComponentInfo != null) {
      return localComponentInfo.getIconResource();
    }
    return 0;
  }
  
  public Drawable loadIcon(PackageManager paramPackageManager)
  {
    Object localObject2 = null;
    Object localObject1 = localObject2;
    if (this.resolvePackageName != null)
    {
      localObject1 = localObject2;
      if (this.iconResourceId != 0) {
        localObject1 = paramPackageManager.getDrawable(this.resolvePackageName, this.iconResourceId, null);
      }
    }
    ComponentInfo localComponentInfo = getComponentInfo();
    localObject2 = localObject1;
    if (localObject1 == null)
    {
      localObject2 = localObject1;
      if (this.iconResourceId != 0)
      {
        localObject1 = localComponentInfo.applicationInfo;
        localObject2 = paramPackageManager.getDrawable(localComponentInfo.packageName, this.iconResourceId, (ApplicationInfo)localObject1);
      }
    }
    if (localObject2 != null) {
      return paramPackageManager.getUserBadgedIcon((Drawable)localObject2, new UserHandle(UserHandle.myUserId()));
    }
    return localComponentInfo.loadIcon(paramPackageManager);
  }
  
  public CharSequence loadLabel(PackageManager paramPackageManager)
  {
    if (this.nonLocalizedLabel != null) {
      return this.nonLocalizedLabel;
    }
    if ((this.resolvePackageName != null) && (this.labelRes != 0))
    {
      localObject1 = paramPackageManager.getText(this.resolvePackageName, this.labelRes, null);
      if (localObject1 != null) {
        return ((CharSequence)localObject1).toString().trim();
      }
    }
    Object localObject1 = getComponentInfo();
    Object localObject2 = ((ComponentInfo)localObject1).applicationInfo;
    if (this.labelRes != 0)
    {
      localObject2 = paramPackageManager.getText(((ComponentInfo)localObject1).packageName, this.labelRes, (ApplicationInfo)localObject2);
      if (localObject2 != null) {
        return ((CharSequence)localObject2).toString().trim();
      }
    }
    localObject1 = ((ComponentInfo)localObject1).loadLabel(paramPackageManager);
    paramPackageManager = (PackageManager)localObject1;
    if (localObject1 != null) {
      paramPackageManager = ((CharSequence)localObject1).toString().trim();
    }
    return paramPackageManager;
  }
  
  public String toString()
  {
    ComponentInfo localComponentInfo = getComponentInfo();
    StringBuilder localStringBuilder = new StringBuilder(128);
    localStringBuilder.append("ResolveInfo{");
    localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
    localStringBuilder.append(' ');
    ComponentName.appendShortString(localStringBuilder, localComponentInfo.packageName, localComponentInfo.name);
    if (this.priority != 0)
    {
      localStringBuilder.append(" p=");
      localStringBuilder.append(this.priority);
    }
    if (this.preferredOrder != 0)
    {
      localStringBuilder.append(" o=");
      localStringBuilder.append(this.preferredOrder);
    }
    localStringBuilder.append(" m=0x");
    localStringBuilder.append(Integer.toHexString(this.match));
    if (this.targetUserId != -2)
    {
      localStringBuilder.append(" targetUserId=");
      localStringBuilder.append(this.targetUserId);
    }
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = 1;
    if (this.activityInfo != null)
    {
      paramParcel.writeInt(1);
      this.activityInfo.writeToParcel(paramParcel, paramInt);
      if (this.filter == null) {
        break label224;
      }
      paramParcel.writeInt(1);
      this.filter.writeToParcel(paramParcel, paramInt);
      label44:
      paramParcel.writeInt(this.priority);
      paramParcel.writeInt(this.preferredOrder);
      paramParcel.writeInt(this.match);
      paramParcel.writeInt(this.specificIndex);
      paramParcel.writeInt(this.labelRes);
      TextUtils.writeToParcel(this.nonLocalizedLabel, paramParcel, paramInt);
      paramParcel.writeInt(this.icon);
      paramParcel.writeString(this.resolvePackageName);
      paramParcel.writeInt(this.targetUserId);
      if (!this.system) {
        break label232;
      }
      paramInt = 1;
      label126:
      paramParcel.writeInt(paramInt);
      if (!this.noResourceId) {
        break label237;
      }
      paramInt = 1;
      label140:
      paramParcel.writeInt(paramInt);
      paramParcel.writeInt(this.iconResourceId);
      if (!this.handleAllWebDataURI) {
        break label242;
      }
    }
    label224:
    label232:
    label237:
    label242:
    for (paramInt = i;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      return;
      if (this.serviceInfo != null)
      {
        paramParcel.writeInt(2);
        this.serviceInfo.writeToParcel(paramParcel, paramInt);
        break;
      }
      if (this.providerInfo != null)
      {
        paramParcel.writeInt(3);
        this.providerInfo.writeToParcel(paramParcel, paramInt);
        break;
      }
      paramParcel.writeInt(0);
      break;
      paramParcel.writeInt(0);
      break label44;
      paramInt = 0;
      break label126;
      paramInt = 0;
      break label140;
    }
  }
  
  public static class DisplayNameComparator
    implements Comparator<ResolveInfo>
  {
    private final Collator mCollator = Collator.getInstance();
    private PackageManager mPM;
    
    public DisplayNameComparator(PackageManager paramPackageManager)
    {
      this.mPM = paramPackageManager;
      this.mCollator.setStrength(0);
    }
    
    public final int compare(ResolveInfo paramResolveInfo1, ResolveInfo paramResolveInfo2)
    {
      if (paramResolveInfo1.targetUserId != -2) {
        return 1;
      }
      if (paramResolveInfo2.targetUserId != -2) {
        return -1;
      }
      CharSequence localCharSequence = paramResolveInfo1.loadLabel(this.mPM);
      Object localObject = localCharSequence;
      if (localCharSequence == null) {
        localObject = paramResolveInfo1.activityInfo.name;
      }
      localCharSequence = paramResolveInfo2.loadLabel(this.mPM);
      paramResolveInfo1 = localCharSequence;
      if (localCharSequence == null) {
        paramResolveInfo1 = paramResolveInfo2.activityInfo.name;
      }
      return this.mCollator.compare(((CharSequence)localObject).toString(), paramResolveInfo1.toString());
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/ResolveInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */