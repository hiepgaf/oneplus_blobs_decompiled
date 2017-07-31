package android.appwidget;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.UserHandle;
import android.util.DisplayMetrics;

public class AppWidgetProviderInfo
  implements Parcelable
{
  public static final Parcelable.Creator<AppWidgetProviderInfo> CREATOR = new Parcelable.Creator()
  {
    public AppWidgetProviderInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new AppWidgetProviderInfo(paramAnonymousParcel);
    }
    
    public AppWidgetProviderInfo[] newArray(int paramAnonymousInt)
    {
      return new AppWidgetProviderInfo[paramAnonymousInt];
    }
  };
  public static final int RESIZE_BOTH = 3;
  public static final int RESIZE_HORIZONTAL = 1;
  public static final int RESIZE_NONE = 0;
  public static final int RESIZE_VERTICAL = 2;
  public static final int WIDGET_CATEGORY_HOME_SCREEN = 1;
  public static final int WIDGET_CATEGORY_KEYGUARD = 2;
  public static final int WIDGET_CATEGORY_SEARCHBOX = 4;
  public int autoAdvanceViewId;
  public ComponentName configure;
  public int icon;
  public int initialKeyguardLayout;
  public int initialLayout;
  @Deprecated
  public String label;
  public int minHeight;
  public int minResizeHeight;
  public int minResizeWidth;
  public int minWidth;
  public int previewImage;
  public ComponentName provider;
  public ActivityInfo providerInfo;
  public int resizeMode;
  public int updatePeriodMillis;
  public int widgetCategory;
  
  public AppWidgetProviderInfo() {}
  
  public AppWidgetProviderInfo(Parcel paramParcel)
  {
    if (paramParcel.readInt() != 0) {
      this.provider = new ComponentName(paramParcel);
    }
    this.minWidth = paramParcel.readInt();
    this.minHeight = paramParcel.readInt();
    this.minResizeWidth = paramParcel.readInt();
    this.minResizeHeight = paramParcel.readInt();
    this.updatePeriodMillis = paramParcel.readInt();
    this.initialLayout = paramParcel.readInt();
    this.initialKeyguardLayout = paramParcel.readInt();
    if (paramParcel.readInt() != 0) {
      this.configure = new ComponentName(paramParcel);
    }
    this.label = paramParcel.readString();
    this.icon = paramParcel.readInt();
    this.previewImage = paramParcel.readInt();
    this.autoAdvanceViewId = paramParcel.readInt();
    this.resizeMode = paramParcel.readInt();
    this.widgetCategory = paramParcel.readInt();
    this.providerInfo = ((ActivityInfo)paramParcel.readParcelable(null));
  }
  
  private Drawable loadDrawable(Context paramContext, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    try
    {
      Object localObject = paramContext.getPackageManager().getResourcesForApplication(this.providerInfo.applicationInfo);
      if (paramInt2 > 0)
      {
        int i = paramInt1;
        if (paramInt1 <= 0) {
          i = paramContext.getResources().getDisplayMetrics().densityDpi;
        }
        localObject = ((Resources)localObject).getDrawableForDensity(paramInt2, i);
        return (Drawable)localObject;
      }
    }
    catch (PackageManager.NameNotFoundException|Resources.NotFoundException localNameNotFoundException)
    {
      if (paramBoolean) {
        return this.providerInfo.loadIcon(paramContext.getPackageManager());
      }
    }
    return null;
  }
  
  public AppWidgetProviderInfo clone()
  {
    Object localObject2 = null;
    AppWidgetProviderInfo localAppWidgetProviderInfo = new AppWidgetProviderInfo();
    if (this.provider == null)
    {
      localObject1 = null;
      localAppWidgetProviderInfo.provider = ((ComponentName)localObject1);
      localAppWidgetProviderInfo.minWidth = this.minWidth;
      localAppWidgetProviderInfo.minHeight = this.minHeight;
      localAppWidgetProviderInfo.minResizeWidth = this.minResizeHeight;
      localAppWidgetProviderInfo.minResizeHeight = this.minResizeHeight;
      localAppWidgetProviderInfo.updatePeriodMillis = this.updatePeriodMillis;
      localAppWidgetProviderInfo.initialLayout = this.initialLayout;
      localAppWidgetProviderInfo.initialKeyguardLayout = this.initialKeyguardLayout;
      if (this.configure != null) {
        break label169;
      }
      localObject1 = null;
      label89:
      localAppWidgetProviderInfo.configure = ((ComponentName)localObject1);
      if (this.label != null) {
        break label180;
      }
    }
    label169:
    label180:
    for (Object localObject1 = localObject2;; localObject1 = this.label.substring(0))
    {
      localAppWidgetProviderInfo.label = ((String)localObject1);
      localAppWidgetProviderInfo.icon = this.icon;
      localAppWidgetProviderInfo.previewImage = this.previewImage;
      localAppWidgetProviderInfo.autoAdvanceViewId = this.autoAdvanceViewId;
      localAppWidgetProviderInfo.resizeMode = this.resizeMode;
      localAppWidgetProviderInfo.widgetCategory = this.widgetCategory;
      localAppWidgetProviderInfo.providerInfo = this.providerInfo;
      return localAppWidgetProviderInfo;
      localObject1 = this.provider.clone();
      break;
      localObject1 = this.configure.clone();
      break label89;
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public final UserHandle getProfile()
  {
    return new UserHandle(UserHandle.getUserId(this.providerInfo.applicationInfo.uid));
  }
  
  public final Drawable loadIcon(Context paramContext, int paramInt)
  {
    return loadDrawable(paramContext, paramInt, this.providerInfo.getIconResource(), true);
  }
  
  public final String loadLabel(PackageManager paramPackageManager)
  {
    paramPackageManager = this.providerInfo.loadLabel(paramPackageManager);
    if (paramPackageManager != null) {
      return paramPackageManager.toString().trim();
    }
    return null;
  }
  
  public final Drawable loadPreviewImage(Context paramContext, int paramInt)
  {
    return loadDrawable(paramContext, paramInt, this.previewImage, false);
  }
  
  public String toString()
  {
    return "AppWidgetProviderInfo(" + getProfile() + '/' + this.provider + ')';
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    if (this.provider != null)
    {
      paramParcel.writeInt(1);
      this.provider.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(this.minWidth);
      paramParcel.writeInt(this.minHeight);
      paramParcel.writeInt(this.minResizeWidth);
      paramParcel.writeInt(this.minResizeHeight);
      paramParcel.writeInt(this.updatePeriodMillis);
      paramParcel.writeInt(this.initialLayout);
      paramParcel.writeInt(this.initialKeyguardLayout);
      if (this.configure == null) {
        break label164;
      }
      paramParcel.writeInt(1);
      this.configure.writeToParcel(paramParcel, paramInt);
    }
    for (;;)
    {
      paramParcel.writeString(this.label);
      paramParcel.writeInt(this.icon);
      paramParcel.writeInt(this.previewImage);
      paramParcel.writeInt(this.autoAdvanceViewId);
      paramParcel.writeInt(this.resizeMode);
      paramParcel.writeInt(this.widgetCategory);
      paramParcel.writeParcelable(this.providerInfo, paramInt);
      return;
      paramParcel.writeInt(0);
      break;
      label164:
      paramParcel.writeInt(0);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/appwidget/AppWidgetProviderInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */