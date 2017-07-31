package android.content.pm;

import android.content.ComponentName;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.Printer;

public class ComponentInfo
  extends PackageItemInfo
{
  public ApplicationInfo applicationInfo;
  public int descriptionRes;
  public boolean directBootAware = false;
  public boolean enabled = true;
  @Deprecated
  public boolean encryptionAware = false;
  public boolean exported = false;
  public String processName;
  
  public ComponentInfo() {}
  
  public ComponentInfo(ComponentInfo paramComponentInfo)
  {
    super(paramComponentInfo);
    this.applicationInfo = paramComponentInfo.applicationInfo;
    this.processName = paramComponentInfo.processName;
    this.descriptionRes = paramComponentInfo.descriptionRes;
    this.enabled = paramComponentInfo.enabled;
    this.exported = paramComponentInfo.exported;
    boolean bool = paramComponentInfo.directBootAware;
    this.directBootAware = bool;
    this.encryptionAware = bool;
  }
  
  protected ComponentInfo(Parcel paramParcel)
  {
    super(paramParcel);
    int i;
    if (paramParcel.readInt() != 0)
    {
      i = 1;
      if (i != 0) {
        this.applicationInfo = ((ApplicationInfo)ApplicationInfo.CREATOR.createFromParcel(paramParcel));
      }
      this.processName = paramParcel.readString();
      this.descriptionRes = paramParcel.readInt();
      if (paramParcel.readInt() == 0) {
        break label127;
      }
      bool1 = true;
      label82:
      this.enabled = bool1;
      if (paramParcel.readInt() == 0) {
        break label132;
      }
      bool1 = true;
      label96:
      this.exported = bool1;
      if (paramParcel.readInt() == 0) {
        break label137;
      }
    }
    label127:
    label132:
    label137:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.directBootAware = bool1;
      this.encryptionAware = bool1;
      return;
      i = 0;
      break;
      bool1 = false;
      break label82;
      bool1 = false;
      break label96;
    }
  }
  
  protected void dumpBack(Printer paramPrinter, String paramString)
  {
    dumpBack(paramPrinter, paramString, 3);
  }
  
  void dumpBack(Printer paramPrinter, String paramString, int paramInt)
  {
    if ((paramInt & 0x2) != 0)
    {
      if (this.applicationInfo == null) {
        break label73;
      }
      paramPrinter.println(paramString + "ApplicationInfo:");
      this.applicationInfo.dump(paramPrinter, paramString + "  ", paramInt);
    }
    for (;;)
    {
      super.dumpBack(paramPrinter, paramString);
      return;
      label73:
      paramPrinter.println(paramString + "ApplicationInfo: null");
    }
  }
  
  protected void dumpFront(Printer paramPrinter, String paramString)
  {
    super.dumpFront(paramPrinter, paramString);
    if ((this.processName == null) || (this.packageName.equals(this.processName))) {}
    for (;;)
    {
      paramPrinter.println(paramString + "enabled=" + this.enabled + " exported=" + this.exported + " directBootAware=" + this.directBootAware);
      if (this.descriptionRes != 0) {
        paramPrinter.println(paramString + "description=" + this.descriptionRes);
      }
      return;
      paramPrinter.println(paramString + "processName=" + this.processName);
    }
  }
  
  protected ApplicationInfo getApplicationInfo()
  {
    return this.applicationInfo;
  }
  
  public final int getBannerResource()
  {
    if (this.banner != 0) {
      return this.banner;
    }
    return this.applicationInfo.banner;
  }
  
  public ComponentName getComponentName()
  {
    return new ComponentName(this.packageName, this.name);
  }
  
  public final int getIconResource()
  {
    if (this.icon != 0) {
      return this.icon;
    }
    return this.applicationInfo.icon;
  }
  
  public final int getLogoResource()
  {
    if (this.logo != 0) {
      return this.logo;
    }
    return this.applicationInfo.logo;
  }
  
  public boolean isEnabled()
  {
    if (this.enabled) {
      return this.applicationInfo.enabled;
    }
    return false;
  }
  
  protected Drawable loadDefaultBanner(PackageManager paramPackageManager)
  {
    return this.applicationInfo.loadBanner(paramPackageManager);
  }
  
  public Drawable loadDefaultIcon(PackageManager paramPackageManager)
  {
    return this.applicationInfo.loadIcon(paramPackageManager);
  }
  
  protected Drawable loadDefaultLogo(PackageManager paramPackageManager)
  {
    return this.applicationInfo.loadLogo(paramPackageManager);
  }
  
  public CharSequence loadLabel(PackageManager paramPackageManager)
  {
    if (this.nonLocalizedLabel != null) {
      return this.nonLocalizedLabel;
    }
    ApplicationInfo localApplicationInfo = this.applicationInfo;
    if (this.labelRes != 0)
    {
      CharSequence localCharSequence = paramPackageManager.getText(this.packageName, this.labelRes, localApplicationInfo);
      if (localCharSequence != null) {
        return localCharSequence;
      }
    }
    if (localApplicationInfo.nonLocalizedLabel != null) {
      return localApplicationInfo.nonLocalizedLabel;
    }
    if (localApplicationInfo.labelRes != 0)
    {
      paramPackageManager = paramPackageManager.getText(this.packageName, localApplicationInfo.labelRes, localApplicationInfo);
      if (paramPackageManager != null) {
        return paramPackageManager;
      }
    }
    return this.name;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = 1;
    super.writeToParcel(paramParcel, paramInt);
    if ((paramInt & 0x2) != 0)
    {
      paramParcel.writeInt(0);
      paramParcel.writeString(this.processName);
      paramParcel.writeInt(this.descriptionRes);
      if (!this.enabled) {
        break label95;
      }
      paramInt = 1;
      label44:
      paramParcel.writeInt(paramInt);
      if (!this.exported) {
        break label100;
      }
      paramInt = 1;
      label58:
      paramParcel.writeInt(paramInt);
      if (!this.directBootAware) {
        break label105;
      }
    }
    label95:
    label100:
    label105:
    for (paramInt = i;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      return;
      paramParcel.writeInt(1);
      this.applicationInfo.writeToParcel(paramParcel, paramInt);
      break;
      paramInt = 0;
      break label44;
      paramInt = 0;
      break label58;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/ComponentInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */