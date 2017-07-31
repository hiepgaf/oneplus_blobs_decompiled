package android.app;

import android.content.Intent;
import android.os.Bundle;
import java.util.HashMap;

@Deprecated
public class ActivityGroup
  extends Activity
{
  static final String PARENT_NON_CONFIG_INSTANCE_KEY = "android:parent_non_config_instance";
  private static final String STATES_KEY = "android:states";
  protected LocalActivityManager mLocalActivityManager;
  
  public ActivityGroup()
  {
    this(true);
  }
  
  public ActivityGroup(boolean paramBoolean)
  {
    this.mLocalActivityManager = new LocalActivityManager(this, paramBoolean);
  }
  
  void dispatchActivityResult(String paramString, int paramInt1, int paramInt2, Intent paramIntent)
  {
    if (paramString != null)
    {
      Activity localActivity = this.mLocalActivityManager.getActivity(paramString);
      if (localActivity != null)
      {
        localActivity.onActivityResult(paramInt1, paramInt2, paramIntent);
        return;
      }
    }
    super.dispatchActivityResult(paramString, paramInt1, paramInt2, paramIntent);
  }
  
  public Activity getCurrentActivity()
  {
    return this.mLocalActivityManager.getCurrentActivity();
  }
  
  public final LocalActivityManager getLocalActivityManager()
  {
    return this.mLocalActivityManager;
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (paramBundle != null) {}
    for (paramBundle = paramBundle.getBundle("android:states");; paramBundle = null)
    {
      this.mLocalActivityManager.dispatchCreate(paramBundle);
      return;
    }
  }
  
  protected void onDestroy()
  {
    super.onDestroy();
    this.mLocalActivityManager.dispatchDestroy(isFinishing());
  }
  
  protected void onPause()
  {
    super.onPause();
    this.mLocalActivityManager.dispatchPause(isFinishing());
  }
  
  protected void onResume()
  {
    super.onResume();
    this.mLocalActivityManager.dispatchResume();
  }
  
  public HashMap<String, Object> onRetainNonConfigurationChildInstances()
  {
    return this.mLocalActivityManager.dispatchRetainNonConfigurationInstance();
  }
  
  protected void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    Bundle localBundle = this.mLocalActivityManager.saveInstanceState();
    if (localBundle != null) {
      paramBundle.putBundle("android:states", localBundle);
    }
  }
  
  protected void onStop()
  {
    super.onStop();
    this.mLocalActivityManager.dispatchStop();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/ActivityGroup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */