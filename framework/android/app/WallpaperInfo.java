package android.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.util.Printer;
import android.util.Xml;
import com.android.internal.R.styleable;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

public final class WallpaperInfo
  implements Parcelable
{
  public static final Parcelable.Creator<WallpaperInfo> CREATOR = new Parcelable.Creator()
  {
    public WallpaperInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new WallpaperInfo(paramAnonymousParcel);
    }
    
    public WallpaperInfo[] newArray(int paramAnonymousInt)
    {
      return new WallpaperInfo[paramAnonymousInt];
    }
  };
  static final String TAG = "WallpaperInfo";
  final int mAuthorResource;
  final int mContextDescriptionResource;
  final int mContextUriResource;
  final int mDescriptionResource;
  final ResolveInfo mService;
  final String mSettingsActivityName;
  final boolean mShowMetadataInPreview;
  final int mThumbnailResource;
  
  public WallpaperInfo(Context paramContext, ResolveInfo paramResolveInfo)
    throws XmlPullParserException, IOException
  {
    this.mService = paramResolveInfo;
    ServiceInfo localServiceInfo = paramResolveInfo.serviceInfo;
    Object localObject1 = paramContext.getPackageManager();
    paramResolveInfo = null;
    paramContext = null;
    XmlResourceParser localXmlResourceParser;
    try
    {
      localXmlResourceParser = localServiceInfo.loadXmlMetaData((PackageManager)localObject1, "android.service.wallpaper");
      if (localXmlResourceParser == null)
      {
        paramContext = localXmlResourceParser;
        paramResolveInfo = localXmlResourceParser;
        throw new XmlPullParserException("No android.service.wallpaper meta-data");
      }
    }
    catch (PackageManager.NameNotFoundException paramResolveInfo)
    {
      paramResolveInfo = paramContext;
      throw new XmlPullParserException("Unable to create context for: " + localServiceInfo.packageName);
    }
    finally
    {
      if (paramResolveInfo != null) {
        paramResolveInfo.close();
      }
    }
    paramContext = localXmlResourceParser;
    paramResolveInfo = localXmlResourceParser;
    localObject1 = ((PackageManager)localObject1).getResourcesForApplication(localServiceInfo.applicationInfo);
    paramContext = localXmlResourceParser;
    paramResolveInfo = localXmlResourceParser;
    Object localObject2 = Xml.asAttributeSet(localXmlResourceParser);
    do
    {
      paramContext = localXmlResourceParser;
      paramResolveInfo = localXmlResourceParser;
      i = localXmlResourceParser.next();
    } while ((i != 1) && (i != 2));
    paramContext = localXmlResourceParser;
    paramResolveInfo = localXmlResourceParser;
    if (!"wallpaper".equals(localXmlResourceParser.getName()))
    {
      paramContext = localXmlResourceParser;
      paramResolveInfo = localXmlResourceParser;
      throw new XmlPullParserException("Meta-data does not start with wallpaper tag");
    }
    paramContext = localXmlResourceParser;
    paramResolveInfo = localXmlResourceParser;
    localObject1 = ((Resources)localObject1).obtainAttributes((AttributeSet)localObject2, R.styleable.Wallpaper);
    paramContext = localXmlResourceParser;
    paramResolveInfo = localXmlResourceParser;
    localObject2 = ((TypedArray)localObject1).getString(1);
    paramContext = localXmlResourceParser;
    paramResolveInfo = localXmlResourceParser;
    int i = ((TypedArray)localObject1).getResourceId(2, -1);
    paramContext = localXmlResourceParser;
    paramResolveInfo = localXmlResourceParser;
    int j = ((TypedArray)localObject1).getResourceId(3, -1);
    paramContext = localXmlResourceParser;
    paramResolveInfo = localXmlResourceParser;
    int k = ((TypedArray)localObject1).getResourceId(0, -1);
    paramContext = localXmlResourceParser;
    paramResolveInfo = localXmlResourceParser;
    int m = ((TypedArray)localObject1).getResourceId(4, -1);
    paramContext = localXmlResourceParser;
    paramResolveInfo = localXmlResourceParser;
    int n = ((TypedArray)localObject1).getResourceId(5, -1);
    paramContext = localXmlResourceParser;
    paramResolveInfo = localXmlResourceParser;
    boolean bool = ((TypedArray)localObject1).getBoolean(6, false);
    paramContext = localXmlResourceParser;
    paramResolveInfo = localXmlResourceParser;
    ((TypedArray)localObject1).recycle();
    if (localXmlResourceParser != null) {
      localXmlResourceParser.close();
    }
    this.mSettingsActivityName = ((String)localObject2);
    this.mThumbnailResource = i;
    this.mAuthorResource = j;
    this.mDescriptionResource = k;
    this.mContextUriResource = m;
    this.mContextDescriptionResource = n;
    this.mShowMetadataInPreview = bool;
  }
  
  WallpaperInfo(Parcel paramParcel)
  {
    this.mSettingsActivityName = paramParcel.readString();
    this.mThumbnailResource = paramParcel.readInt();
    this.mAuthorResource = paramParcel.readInt();
    this.mDescriptionResource = paramParcel.readInt();
    this.mContextUriResource = paramParcel.readInt();
    this.mContextDescriptionResource = paramParcel.readInt();
    if (paramParcel.readInt() != 0) {
      bool = true;
    }
    this.mShowMetadataInPreview = bool;
    this.mService = ((ResolveInfo)ResolveInfo.CREATOR.createFromParcel(paramParcel));
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void dump(Printer paramPrinter, String paramString)
  {
    paramPrinter.println(paramString + "Service:");
    this.mService.dump(paramPrinter, paramString + "  ");
    paramPrinter.println(paramString + "mSettingsActivityName=" + this.mSettingsActivityName);
  }
  
  public ComponentName getComponent()
  {
    return new ComponentName(this.mService.serviceInfo.packageName, this.mService.serviceInfo.name);
  }
  
  public String getPackageName()
  {
    return this.mService.serviceInfo.packageName;
  }
  
  public ServiceInfo getServiceInfo()
  {
    return this.mService.serviceInfo;
  }
  
  public String getServiceName()
  {
    return this.mService.serviceInfo.name;
  }
  
  public String getSettingsActivity()
  {
    return this.mSettingsActivityName;
  }
  
  public boolean getShowMetadataInPreview()
  {
    return this.mShowMetadataInPreview;
  }
  
  public CharSequence loadAuthor(PackageManager paramPackageManager)
    throws Resources.NotFoundException
  {
    if (this.mAuthorResource <= 0) {
      throw new Resources.NotFoundException();
    }
    String str2 = this.mService.resolvePackageName;
    ApplicationInfo localApplicationInfo = null;
    String str1 = str2;
    if (str2 == null)
    {
      str1 = this.mService.serviceInfo.packageName;
      localApplicationInfo = this.mService.serviceInfo.applicationInfo;
    }
    return paramPackageManager.getText(str1, this.mAuthorResource, localApplicationInfo);
  }
  
  public CharSequence loadContextDescription(PackageManager paramPackageManager)
    throws Resources.NotFoundException
  {
    if (this.mContextDescriptionResource <= 0) {
      throw new Resources.NotFoundException();
    }
    String str2 = this.mService.resolvePackageName;
    ApplicationInfo localApplicationInfo = null;
    String str1 = str2;
    if (str2 == null)
    {
      str1 = this.mService.serviceInfo.packageName;
      localApplicationInfo = this.mService.serviceInfo.applicationInfo;
    }
    return paramPackageManager.getText(str1, this.mContextDescriptionResource, localApplicationInfo).toString();
  }
  
  public Uri loadContextUri(PackageManager paramPackageManager)
    throws Resources.NotFoundException
  {
    if (this.mContextUriResource <= 0) {
      throw new Resources.NotFoundException();
    }
    String str2 = this.mService.resolvePackageName;
    ApplicationInfo localApplicationInfo = null;
    String str1 = str2;
    if (str2 == null)
    {
      str1 = this.mService.serviceInfo.packageName;
      localApplicationInfo = this.mService.serviceInfo.applicationInfo;
    }
    paramPackageManager = paramPackageManager.getText(str1, this.mContextUriResource, localApplicationInfo).toString();
    if (paramPackageManager == null) {
      return null;
    }
    return Uri.parse(paramPackageManager);
  }
  
  public CharSequence loadDescription(PackageManager paramPackageManager)
    throws Resources.NotFoundException
  {
    String str2 = this.mService.resolvePackageName;
    ApplicationInfo localApplicationInfo = null;
    String str1 = str2;
    if (str2 == null)
    {
      str1 = this.mService.serviceInfo.packageName;
      localApplicationInfo = this.mService.serviceInfo.applicationInfo;
    }
    if (this.mService.serviceInfo.descriptionRes != 0) {
      return paramPackageManager.getText(str1, this.mService.serviceInfo.descriptionRes, localApplicationInfo);
    }
    if (this.mDescriptionResource <= 0) {
      throw new Resources.NotFoundException();
    }
    return paramPackageManager.getText(str1, this.mDescriptionResource, this.mService.serviceInfo.applicationInfo);
  }
  
  public Drawable loadIcon(PackageManager paramPackageManager)
  {
    return this.mService.loadIcon(paramPackageManager);
  }
  
  public CharSequence loadLabel(PackageManager paramPackageManager)
  {
    return this.mService.loadLabel(paramPackageManager);
  }
  
  public Drawable loadThumbnail(PackageManager paramPackageManager)
  {
    if (this.mThumbnailResource < 0) {
      return null;
    }
    return paramPackageManager.getDrawable(this.mService.serviceInfo.packageName, this.mThumbnailResource, this.mService.serviceInfo.applicationInfo);
  }
  
  public String toString()
  {
    return "WallpaperInfo{" + this.mService.serviceInfo.name + ", settings: " + this.mSettingsActivityName + "}";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mSettingsActivityName);
    paramParcel.writeInt(this.mThumbnailResource);
    paramParcel.writeInt(this.mAuthorResource);
    paramParcel.writeInt(this.mDescriptionResource);
    paramParcel.writeInt(this.mContextUriResource);
    paramParcel.writeInt(this.mContextDescriptionResource);
    if (this.mShowMetadataInPreview) {}
    for (int i = 1;; i = 0)
    {
      paramParcel.writeInt(i);
      this.mService.writeToParcel(paramParcel, paramInt);
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/WallpaperInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */