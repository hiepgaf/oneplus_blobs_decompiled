package android.content.pm;

import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.Html;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.Printer;
import java.text.Collator;
import java.util.Comparator;

public class PackageItemInfo
{
  public static final int DUMP_FLAG_ALL = 3;
  public static final int DUMP_FLAG_APPLICATION = 2;
  public static final int DUMP_FLAG_DETAILS = 1;
  private static final float MAX_LABEL_SIZE_PX = 500.0F;
  public int banner;
  public int icon;
  public int labelRes;
  public int logo;
  public Bundle metaData;
  public String name;
  public CharSequence nonLocalizedLabel;
  public String packageName;
  public int showUserIcon;
  
  public PackageItemInfo()
  {
    this.showUserIcon = 55536;
  }
  
  public PackageItemInfo(PackageItemInfo paramPackageItemInfo)
  {
    this.name = paramPackageItemInfo.name;
    if (this.name != null) {
      this.name = this.name.trim();
    }
    this.packageName = paramPackageItemInfo.packageName;
    this.labelRes = paramPackageItemInfo.labelRes;
    this.nonLocalizedLabel = paramPackageItemInfo.nonLocalizedLabel;
    if (this.nonLocalizedLabel != null) {
      this.nonLocalizedLabel = this.nonLocalizedLabel.toString().trim();
    }
    this.icon = paramPackageItemInfo.icon;
    this.banner = paramPackageItemInfo.banner;
    this.logo = paramPackageItemInfo.logo;
    this.metaData = paramPackageItemInfo.metaData;
    this.showUserIcon = paramPackageItemInfo.showUserIcon;
  }
  
  protected PackageItemInfo(Parcel paramParcel)
  {
    this.name = paramParcel.readString();
    this.packageName = paramParcel.readString();
    this.labelRes = paramParcel.readInt();
    this.nonLocalizedLabel = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
    this.icon = paramParcel.readInt();
    this.logo = paramParcel.readInt();
    this.metaData = paramParcel.readBundle();
    this.banner = paramParcel.readInt();
    this.showUserIcon = paramParcel.readInt();
  }
  
  protected void dumpBack(Printer paramPrinter, String paramString) {}
  
  protected void dumpFront(Printer paramPrinter, String paramString)
  {
    if (this.name != null) {
      paramPrinter.println(paramString + "name=" + this.name);
    }
    paramPrinter.println(paramString + "packageName=" + this.packageName);
    if ((this.labelRes != 0) || (this.nonLocalizedLabel != null)) {
      break label162;
    }
    for (;;)
    {
      paramPrinter.println(paramString + "labelRes=0x" + Integer.toHexString(this.labelRes) + " nonLocalizedLabel=" + this.nonLocalizedLabel + " icon=0x" + Integer.toHexString(this.icon) + " banner=0x" + Integer.toHexString(this.banner));
      label162:
      return;
      if (this.icon == 0) {
        if (this.banner == 0) {
          break;
        }
      }
    }
  }
  
  protected ApplicationInfo getApplicationInfo()
  {
    return null;
  }
  
  public Drawable loadBanner(PackageManager paramPackageManager)
  {
    if (this.banner != 0)
    {
      Drawable localDrawable = paramPackageManager.getDrawable(this.packageName, this.banner, getApplicationInfo());
      if (localDrawable != null) {
        return localDrawable;
      }
    }
    return loadDefaultBanner(paramPackageManager);
  }
  
  protected Drawable loadDefaultBanner(PackageManager paramPackageManager)
  {
    return null;
  }
  
  public Drawable loadDefaultIcon(PackageManager paramPackageManager)
  {
    return paramPackageManager.getDefaultActivityIcon();
  }
  
  protected Drawable loadDefaultLogo(PackageManager paramPackageManager)
  {
    return null;
  }
  
  public Drawable loadIcon(PackageManager paramPackageManager)
  {
    return paramPackageManager.loadItemIcon(this, getApplicationInfo());
  }
  
  public CharSequence loadLabel(PackageManager paramPackageManager)
  {
    if (this.nonLocalizedLabel != null) {
      return this.nonLocalizedLabel;
    }
    if (this.labelRes != 0)
    {
      paramPackageManager = paramPackageManager.getText(this.packageName, this.labelRes, getApplicationInfo());
      if (paramPackageManager != null) {
        return paramPackageManager.toString().trim();
      }
    }
    if (this.name != null) {
      return this.name;
    }
    return this.packageName;
  }
  
  public Drawable loadLogo(PackageManager paramPackageManager)
  {
    if (this.logo != 0)
    {
      Drawable localDrawable = paramPackageManager.getDrawable(this.packageName, this.logo, getApplicationInfo());
      if (localDrawable != null) {
        return localDrawable;
      }
    }
    return loadDefaultLogo(paramPackageManager);
  }
  
  public CharSequence loadSafeLabel(PackageManager paramPackageManager)
  {
    paramPackageManager = Html.fromHtml(loadLabel(paramPackageManager).toString()).toString();
    int j = paramPackageManager.length();
    int i = 0;
    for (;;)
    {
      localObject = paramPackageManager;
      int k;
      int m;
      if (i < j)
      {
        k = paramPackageManager.codePointAt(i);
        m = Character.getType(k);
        if ((m != 13) && (m != 15)) {
          break label88;
        }
      }
      label88:
      while (m == 14)
      {
        localObject = paramPackageManager.substring(0, i);
        paramPackageManager = ((String)localObject).trim();
        if (!paramPackageManager.isEmpty()) {
          break;
        }
        return this.packageName;
      }
      localObject = paramPackageManager;
      if (m == 12) {
        localObject = paramPackageManager.substring(0, i) + " " + paramPackageManager.substring(Character.charCount(k) + i);
      }
      i += Character.charCount(k);
      paramPackageManager = (PackageManager)localObject;
    }
    Object localObject = new TextPaint();
    ((TextPaint)localObject).setTextSize(42.0F);
    return TextUtils.ellipsize(paramPackageManager, (TextPaint)localObject, 500.0F, TextUtils.TruncateAt.END);
  }
  
  public Drawable loadUnbadgedIcon(PackageManager paramPackageManager)
  {
    return paramPackageManager.loadUnbadgedItemIcon(this, getApplicationInfo());
  }
  
  public XmlResourceParser loadXmlMetaData(PackageManager paramPackageManager, String paramString)
  {
    if (this.metaData != null)
    {
      int i = this.metaData.getInt(paramString);
      if (i != 0) {
        return paramPackageManager.getXml(this.packageName, i, getApplicationInfo());
      }
    }
    return null;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.name);
    paramParcel.writeString(this.packageName);
    paramParcel.writeInt(this.labelRes);
    TextUtils.writeToParcel(this.nonLocalizedLabel, paramParcel, paramInt);
    paramParcel.writeInt(this.icon);
    paramParcel.writeInt(this.logo);
    paramParcel.writeBundle(this.metaData);
    paramParcel.writeInt(this.banner);
    paramParcel.writeInt(this.showUserIcon);
  }
  
  public static class DisplayNameComparator
    implements Comparator<PackageItemInfo>
  {
    private PackageManager mPM;
    private final Collator sCollator = Collator.getInstance();
    
    public DisplayNameComparator(PackageManager paramPackageManager)
    {
      this.mPM = paramPackageManager;
    }
    
    public final int compare(PackageItemInfo paramPackageItemInfo1, PackageItemInfo paramPackageItemInfo2)
    {
      CharSequence localCharSequence = paramPackageItemInfo1.loadLabel(this.mPM);
      Object localObject = localCharSequence;
      if (localCharSequence == null) {
        localObject = paramPackageItemInfo1.name;
      }
      localCharSequence = paramPackageItemInfo2.loadLabel(this.mPM);
      paramPackageItemInfo1 = localCharSequence;
      if (localCharSequence == null) {
        paramPackageItemInfo1 = paramPackageItemInfo2.name;
      }
      return this.sCollator.compare(((CharSequence)localObject).toString(), paramPackageItemInfo1.toString());
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/PackageItemInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */