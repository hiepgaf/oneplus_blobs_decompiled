package android.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import java.util.ArrayList;

public class TaskStackBuilder
{
  private static final String TAG = "TaskStackBuilder";
  private final ArrayList<Intent> mIntents = new ArrayList();
  private final Context mSourceContext;
  
  private TaskStackBuilder(Context paramContext)
  {
    this.mSourceContext = paramContext;
  }
  
  public static TaskStackBuilder create(Context paramContext)
  {
    return new TaskStackBuilder(paramContext);
  }
  
  public TaskStackBuilder addNextIntent(Intent paramIntent)
  {
    this.mIntents.add(paramIntent);
    return this;
  }
  
  public TaskStackBuilder addNextIntentWithParentStack(Intent paramIntent)
  {
    ComponentName localComponentName2 = paramIntent.getComponent();
    ComponentName localComponentName1 = localComponentName2;
    if (localComponentName2 == null) {
      localComponentName1 = paramIntent.resolveActivity(this.mSourceContext.getPackageManager());
    }
    if (localComponentName1 != null) {
      addParentStack(localComponentName1);
    }
    addNextIntent(paramIntent);
    return this;
  }
  
  public TaskStackBuilder addParentStack(Activity paramActivity)
  {
    Intent localIntent = paramActivity.getParentActivityIntent();
    if (localIntent != null)
    {
      ComponentName localComponentName = localIntent.getComponent();
      paramActivity = localComponentName;
      if (localComponentName == null) {
        paramActivity = localIntent.resolveActivity(this.mSourceContext.getPackageManager());
      }
      addParentStack(paramActivity);
      addNextIntent(localIntent);
    }
    return this;
  }
  
  public TaskStackBuilder addParentStack(ComponentName paramComponentName)
  {
    int i = this.mIntents.size();
    PackageManager localPackageManager = this.mSourceContext.getPackageManager();
    for (;;)
    {
      try
      {
        paramComponentName = localPackageManager.getActivityInfo(paramComponentName, 0);
        String str = paramComponentName.parentActivityName;
        if (str == null) {
          break;
        }
        paramComponentName = new ComponentName(paramComponentName.packageName, str);
        ActivityInfo localActivityInfo = localPackageManager.getActivityInfo(paramComponentName, 0);
        str = localActivityInfo.parentActivityName;
        if ((str == null) && (i == 0))
        {
          paramComponentName = Intent.makeMainActivity(paramComponentName);
          this.mIntents.add(i, paramComponentName);
          paramComponentName = localActivityInfo;
        }
        else
        {
          paramComponentName = new Intent().setComponent(paramComponentName);
        }
      }
      catch (PackageManager.NameNotFoundException paramComponentName)
      {
        Log.e("TaskStackBuilder", "Bad ComponentName while traversing activity parent metadata");
        throw new IllegalArgumentException(paramComponentName);
      }
    }
    return this;
  }
  
  public TaskStackBuilder addParentStack(Class<?> paramClass)
  {
    return addParentStack(new ComponentName(this.mSourceContext, paramClass));
  }
  
  public Intent editIntentAt(int paramInt)
  {
    return (Intent)this.mIntents.get(paramInt);
  }
  
  public int getIntentCount()
  {
    return this.mIntents.size();
  }
  
  public Intent[] getIntents()
  {
    Intent[] arrayOfIntent = new Intent[this.mIntents.size()];
    if (arrayOfIntent.length == 0) {
      return arrayOfIntent;
    }
    arrayOfIntent[0] = new Intent((Intent)this.mIntents.get(0)).addFlags(268484608);
    int i = 1;
    while (i < arrayOfIntent.length)
    {
      arrayOfIntent[i] = new Intent((Intent)this.mIntents.get(i));
      i += 1;
    }
    return arrayOfIntent;
  }
  
  public PendingIntent getPendingIntent(int paramInt1, int paramInt2)
  {
    return getPendingIntent(paramInt1, paramInt2, null);
  }
  
  public PendingIntent getPendingIntent(int paramInt1, int paramInt2, Bundle paramBundle)
  {
    if (this.mIntents.isEmpty()) {
      throw new IllegalStateException("No intents added to TaskStackBuilder; cannot getPendingIntent");
    }
    return PendingIntent.getActivities(this.mSourceContext, paramInt1, getIntents(), paramInt2, paramBundle);
  }
  
  public PendingIntent getPendingIntent(int paramInt1, int paramInt2, Bundle paramBundle, UserHandle paramUserHandle)
  {
    if (this.mIntents.isEmpty()) {
      throw new IllegalStateException("No intents added to TaskStackBuilder; cannot getPendingIntent");
    }
    return PendingIntent.getActivitiesAsUser(this.mSourceContext, paramInt1, getIntents(), paramInt2, paramBundle, paramUserHandle);
  }
  
  public void startActivities()
  {
    startActivities(null);
  }
  
  public void startActivities(Bundle paramBundle)
  {
    startActivities(paramBundle, new UserHandle(UserHandle.myUserId()));
  }
  
  public void startActivities(Bundle paramBundle, UserHandle paramUserHandle)
  {
    if (this.mIntents.isEmpty()) {
      throw new IllegalStateException("No intents added to TaskStackBuilder; cannot startActivities");
    }
    this.mSourceContext.startActivitiesAsUser(getIntents(), paramBundle, paramUserHandle);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/TaskStackBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */