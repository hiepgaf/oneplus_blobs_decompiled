package android.support.v4.app;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;

public class TaskStackBuilder
  implements Iterable<Intent>
{
  private static final TaskStackBuilderImpl IMPL = new TaskStackBuilderImplHoneycomb();
  private static final String TAG = "TaskStackBuilder";
  private final ArrayList<Intent> mIntents = new ArrayList();
  private final Context mSourceContext;
  
  static
  {
    if (Build.VERSION.SDK_INT < 11)
    {
      IMPL = new TaskStackBuilderImplBase();
      return;
    }
  }
  
  private TaskStackBuilder(Context paramContext)
  {
    this.mSourceContext = paramContext;
  }
  
  public static TaskStackBuilder create(Context paramContext)
  {
    return new TaskStackBuilder(paramContext);
  }
  
  public static TaskStackBuilder from(Context paramContext)
  {
    return create(paramContext);
  }
  
  public TaskStackBuilder addNextIntent(Intent paramIntent)
  {
    this.mIntents.add(paramIntent);
    return this;
  }
  
  public TaskStackBuilder addNextIntentWithParentStack(Intent paramIntent)
  {
    ComponentName localComponentName = paramIntent.getComponent();
    if (localComponentName != null) {
      if (localComponentName != null) {
        break label36;
      }
    }
    for (;;)
    {
      addNextIntent(paramIntent);
      return this;
      localComponentName = paramIntent.resolveActivity(this.mSourceContext.getPackageManager());
      break;
      label36:
      addParentStack(localComponentName);
    }
  }
  
  public TaskStackBuilder addParentStack(Activity paramActivity)
  {
    Object localObject = null;
    if (!(paramActivity instanceof SupportParentable)) {
      if (localObject == null) {
        break label34;
      }
    }
    label34:
    for (paramActivity = (Activity)localObject;; paramActivity = NavUtils.getParentActivityIntent(paramActivity))
    {
      if (paramActivity != null) {
        break label42;
      }
      return this;
      localObject = ((SupportParentable)paramActivity).getSupportParentActivityIntent();
      break;
    }
    label42:
    localObject = paramActivity.getComponent();
    if (localObject != null) {}
    for (;;)
    {
      addParentStack((ComponentName)localObject);
      addNextIntent(paramActivity);
      return this;
      localObject = paramActivity.resolveActivity(this.mSourceContext.getPackageManager());
    }
  }
  
  public TaskStackBuilder addParentStack(ComponentName paramComponentName)
  {
    int i = this.mIntents.size();
    do
    {
      try
      {
        paramComponentName = NavUtils.getParentActivityIntent(this.mSourceContext, paramComponentName);
      }
      catch (PackageManager.NameNotFoundException paramComponentName)
      {
        Log.e("TaskStackBuilder", "Bad ComponentName while traversing activity parent metadata");
        throw new IllegalArgumentException(paramComponentName);
      }
      this.mIntents.add(i, paramComponentName);
      paramComponentName = NavUtils.getParentActivityIntent(this.mSourceContext, paramComponentName.getComponent());
    } while (paramComponentName != null);
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
  
  public Intent getIntent(int paramInt)
  {
    return editIntentAt(paramInt);
  }
  
  public int getIntentCount()
  {
    return this.mIntents.size();
  }
  
  public Intent[] getIntents()
  {
    Intent[] arrayOfIntent = new Intent[this.mIntents.size()];
    int i;
    if (arrayOfIntent.length != 0)
    {
      arrayOfIntent[0] = new Intent((Intent)this.mIntents.get(0)).addFlags(268484608);
      i = 1;
    }
    for (;;)
    {
      if (i >= arrayOfIntent.length)
      {
        return arrayOfIntent;
        return arrayOfIntent;
      }
      arrayOfIntent[i] = new Intent((Intent)this.mIntents.get(i));
      i += 1;
    }
  }
  
  public PendingIntent getPendingIntent(int paramInt1, int paramInt2)
  {
    return getPendingIntent(paramInt1, paramInt2, null);
  }
  
  public PendingIntent getPendingIntent(int paramInt1, int paramInt2, Bundle paramBundle)
  {
    if (!this.mIntents.isEmpty())
    {
      Intent[] arrayOfIntent = (Intent[])this.mIntents.toArray(new Intent[this.mIntents.size()]);
      arrayOfIntent[0] = new Intent(arrayOfIntent[0]).addFlags(268484608);
      return IMPL.getPendingIntent(this.mSourceContext, arrayOfIntent, paramInt1, paramInt2, paramBundle);
    }
    throw new IllegalStateException("No intents added to TaskStackBuilder; cannot getPendingIntent");
  }
  
  public Iterator<Intent> iterator()
  {
    return this.mIntents.iterator();
  }
  
  public void startActivities()
  {
    startActivities(null);
  }
  
  public void startActivities(Bundle paramBundle)
  {
    Intent[] arrayOfIntent;
    if (!this.mIntents.isEmpty())
    {
      arrayOfIntent = (Intent[])this.mIntents.toArray(new Intent[this.mIntents.size()]);
      arrayOfIntent[0] = new Intent(arrayOfIntent[0]).addFlags(268484608);
      if (!ContextCompat.startActivities(this.mSourceContext, arrayOfIntent, paramBundle)) {}
    }
    else
    {
      throw new IllegalStateException("No intents added to TaskStackBuilder; cannot startActivities");
    }
    paramBundle = new Intent(arrayOfIntent[(arrayOfIntent.length - 1)]);
    paramBundle.addFlags(268435456);
    this.mSourceContext.startActivity(paramBundle);
  }
  
  public static abstract interface SupportParentable
  {
    public abstract Intent getSupportParentActivityIntent();
  }
  
  static abstract interface TaskStackBuilderImpl
  {
    public abstract PendingIntent getPendingIntent(Context paramContext, Intent[] paramArrayOfIntent, int paramInt1, int paramInt2, Bundle paramBundle);
  }
  
  static class TaskStackBuilderImplBase
    implements TaskStackBuilder.TaskStackBuilderImpl
  {
    public PendingIntent getPendingIntent(Context paramContext, Intent[] paramArrayOfIntent, int paramInt1, int paramInt2, Bundle paramBundle)
    {
      paramArrayOfIntent = new Intent(paramArrayOfIntent[(paramArrayOfIntent.length - 1)]);
      paramArrayOfIntent.addFlags(268435456);
      return PendingIntent.getActivity(paramContext, paramInt1, paramArrayOfIntent, paramInt2);
    }
  }
  
  static class TaskStackBuilderImplHoneycomb
    implements TaskStackBuilder.TaskStackBuilderImpl
  {
    public PendingIntent getPendingIntent(Context paramContext, Intent[] paramArrayOfIntent, int paramInt1, int paramInt2, Bundle paramBundle)
    {
      paramArrayOfIntent[0] = new Intent(paramArrayOfIntent[0]).addFlags(268484608);
      return TaskStackBuilderHoneycomb.getActivitiesPendingIntent(paramContext, paramInt1, paramArrayOfIntent, paramInt2);
    }
  }
  
  static class TaskStackBuilderImplJellybean
    implements TaskStackBuilder.TaskStackBuilderImpl
  {
    public PendingIntent getPendingIntent(Context paramContext, Intent[] paramArrayOfIntent, int paramInt1, int paramInt2, Bundle paramBundle)
    {
      paramArrayOfIntent[0] = new Intent(paramArrayOfIntent[0]).addFlags(268484608);
      return TaskStackBuilderJellybean.getActivitiesPendingIntent(paramContext, paramInt1, paramArrayOfIntent, paramInt2, paramBundle);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/app/TaskStackBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */