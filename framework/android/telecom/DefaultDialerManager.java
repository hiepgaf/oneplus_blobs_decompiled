package android.telecom;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Process;
import android.os.UserHandle;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefaultDialerManager
{
  private static final String TAG = "DefaultDialerManager";
  
  private static List<String> filterByIntent(Context paramContext, List<String> paramList, Intent paramIntent)
  {
    if ((paramList == null) || (paramList.isEmpty())) {
      return new ArrayList();
    }
    ArrayList localArrayList = new ArrayList();
    paramContext = paramContext.getPackageManager().queryIntentActivities(paramIntent, 0);
    int j = paramContext.size();
    int i = 0;
    if (i < j)
    {
      paramIntent = ((ResolveInfo)paramContext.get(i)).activityInfo;
      if ((paramIntent == null) || (!paramList.contains(paramIntent.packageName)) || (localArrayList.contains(paramIntent.packageName))) {}
      for (;;)
      {
        i += 1;
        break;
        localArrayList.add(paramIntent.packageName);
      }
    }
    return localArrayList;
  }
  
  public static String getDefaultDialerApplication(Context paramContext)
  {
    return getDefaultDialerApplication(paramContext, paramContext.getUserId());
  }
  
  public static String getDefaultDialerApplication(Context paramContext, int paramInt)
  {
    String str = Settings.Secure.getStringForUser(paramContext.getContentResolver(), "dialer_default_application", paramInt);
    List localList = getInstalledDialerApplications(paramContext);
    if (localList.contains(str)) {
      return str;
    }
    paramContext = getTelecomManager(paramContext).getSystemDialerPackage();
    if (TextUtils.isEmpty(paramContext)) {
      return null;
    }
    if (localList.contains(paramContext)) {
      return paramContext;
    }
    return null;
  }
  
  public static List<String> getInstalledDialerApplications(Context paramContext)
  {
    return getInstalledDialerApplications(paramContext, Process.myUserHandle().getIdentifier());
  }
  
  public static List<String> getInstalledDialerApplications(Context paramContext, int paramInt)
  {
    Object localObject = paramContext.getPackageManager().queryIntentActivitiesAsUser(new Intent("android.intent.action.DIAL"), 0, paramInt);
    ArrayList localArrayList = new ArrayList();
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      ActivityInfo localActivityInfo = ((ResolveInfo)((Iterator)localObject).next()).activityInfo;
      if ((localActivityInfo != null) && (!localArrayList.contains(localActivityInfo.packageName))) {
        localArrayList.add(localActivityInfo.packageName);
      }
    }
    localObject = new Intent("android.intent.action.DIAL");
    ((Intent)localObject).setData(Uri.fromParts("tel", "", null));
    return filterByIntent(paramContext, localArrayList, (Intent)localObject);
  }
  
  private static TelecomManager getTelecomManager(Context paramContext)
  {
    return (TelecomManager)paramContext.getSystemService("telecom");
  }
  
  public static boolean isDefaultOrSystemDialer(Context paramContext, String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return false;
    }
    paramContext = getTelecomManager(paramContext);
    if (!paramString.equals(paramContext.getDefaultDialerPackage())) {
      return paramString.equals(paramContext.getSystemDialerPackage());
    }
    return true;
  }
  
  public static boolean setDefaultDialerApplication(Context paramContext, String paramString)
  {
    return setDefaultDialerApplication(paramContext, paramString, ActivityManager.getCurrentUser());
  }
  
  public static boolean setDefaultDialerApplication(Context paramContext, String paramString, int paramInt)
  {
    String str = Settings.Secure.getStringForUser(paramContext.getContentResolver(), "dialer_default_application", paramInt);
    if ((paramString != null) && (str != null) && (paramString.equals(str))) {
      return false;
    }
    if (getInstalledDialerApplications(paramContext).contains(paramString))
    {
      Settings.Secure.putStringForUser(paramContext.getContentResolver(), "dialer_default_application", paramString, paramInt);
      return true;
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/telecom/DefaultDialerManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */