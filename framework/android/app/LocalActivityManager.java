package android.app;

import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageItemInfo;
import android.os.BaseBundle;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import com.android.internal.content.ReferrerIntent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Deprecated
public class LocalActivityManager
{
  static final int CREATED = 2;
  static final int DESTROYED = 5;
  static final int INITIALIZING = 1;
  static final int RESTORED = 0;
  static final int RESUMED = 4;
  static final int STARTED = 3;
  private static final String TAG = "LocalActivityManager";
  private static final boolean localLOGV = false;
  private final Map<String, LocalActivityRecord> mActivities = new HashMap();
  private final ArrayList<LocalActivityRecord> mActivityArray = new ArrayList();
  private final ActivityThread mActivityThread = ActivityThread.currentActivityThread();
  private int mCurState = 1;
  private boolean mFinishing;
  private final Activity mParent;
  private LocalActivityRecord mResumed;
  private boolean mSingleMode;
  
  public LocalActivityManager(Activity paramActivity, boolean paramBoolean)
  {
    this.mParent = paramActivity;
    this.mSingleMode = paramBoolean;
  }
  
  private void moveToState(LocalActivityRecord paramLocalActivityRecord, int paramInt)
  {
    if ((paramLocalActivityRecord.curState == 0) || (paramLocalActivityRecord.curState == 5)) {
      return;
    }
    if (paramLocalActivityRecord.curState == 1)
    {
      Object localObject2 = this.mParent.getLastNonConfigurationChildInstances();
      Object localObject1 = null;
      if (localObject2 != null) {
        localObject1 = ((HashMap)localObject2).get(paramLocalActivityRecord.id);
      }
      localObject2 = null;
      if (localObject1 != null)
      {
        localObject2 = new Activity.NonConfigurationInstances();
        ((Activity.NonConfigurationInstances)localObject2).activity = localObject1;
      }
      if (paramLocalActivityRecord.activityInfo == null) {
        paramLocalActivityRecord.activityInfo = this.mActivityThread.resolveActivityInfo(paramLocalActivityRecord.intent);
      }
      paramLocalActivityRecord.activity = this.mActivityThread.startActivityNow(this.mParent, paramLocalActivityRecord.id, paramLocalActivityRecord.intent, paramLocalActivityRecord.activityInfo, paramLocalActivityRecord, paramLocalActivityRecord.instanceState, (Activity.NonConfigurationInstances)localObject2);
      if (paramLocalActivityRecord.activity == null) {
        return;
      }
      paramLocalActivityRecord.window = paramLocalActivityRecord.activity.getWindow();
      paramLocalActivityRecord.instanceState = null;
      paramLocalActivityRecord.curState = 3;
      if (paramInt == 4)
      {
        this.mActivityThread.performResumeActivity(paramLocalActivityRecord, true, "moveToState-INITIALIZING");
        paramLocalActivityRecord.curState = 4;
      }
      return;
    }
    switch (paramLocalActivityRecord.curState)
    {
    default: 
      return;
    case 2: 
      if (paramInt == 3)
      {
        this.mActivityThread.performRestartActivity(paramLocalActivityRecord);
        paramLocalActivityRecord.curState = 3;
      }
      if (paramInt == 4)
      {
        this.mActivityThread.performRestartActivity(paramLocalActivityRecord);
        this.mActivityThread.performResumeActivity(paramLocalActivityRecord, true, "moveToState-CREATED");
        paramLocalActivityRecord.curState = 4;
      }
      return;
    case 3: 
      if (paramInt == 4)
      {
        this.mActivityThread.performResumeActivity(paramLocalActivityRecord, true, "moveToState-STARTED");
        paramLocalActivityRecord.instanceState = null;
        paramLocalActivityRecord.curState = 4;
      }
      if (paramInt == 2)
      {
        this.mActivityThread.performStopActivity(paramLocalActivityRecord, false, "moveToState-STARTED");
        paramLocalActivityRecord.curState = 2;
      }
      return;
    }
    if (paramInt == 3)
    {
      performPause(paramLocalActivityRecord, this.mFinishing);
      paramLocalActivityRecord.curState = 3;
    }
    if (paramInt == 2)
    {
      performPause(paramLocalActivityRecord, this.mFinishing);
      this.mActivityThread.performStopActivity(paramLocalActivityRecord, false, "moveToState-RESUMED");
      paramLocalActivityRecord.curState = 2;
    }
  }
  
  private Window performDestroy(LocalActivityRecord paramLocalActivityRecord, boolean paramBoolean)
  {
    Window localWindow = paramLocalActivityRecord.window;
    if ((paramLocalActivityRecord.curState != 4) || (paramBoolean)) {}
    for (;;)
    {
      this.mActivityThread.performDestroyActivity(paramLocalActivityRecord, paramBoolean);
      paramLocalActivityRecord.activity = null;
      paramLocalActivityRecord.window = null;
      if (paramBoolean) {
        paramLocalActivityRecord.instanceState = null;
      }
      paramLocalActivityRecord.curState = 5;
      return localWindow;
      performPause(paramLocalActivityRecord, paramBoolean);
    }
  }
  
  private void performPause(LocalActivityRecord paramLocalActivityRecord, boolean paramBoolean)
  {
    if (paramLocalActivityRecord.instanceState == null) {}
    for (boolean bool = true;; bool = false)
    {
      Bundle localBundle = this.mActivityThread.performPauseActivity(paramLocalActivityRecord, paramBoolean, bool, "performPause");
      if (bool) {
        paramLocalActivityRecord.instanceState = localBundle;
      }
      return;
    }
  }
  
  public Window destroyActivity(String paramString, boolean paramBoolean)
  {
    LocalActivityRecord localLocalActivityRecord = (LocalActivityRecord)this.mActivities.get(paramString);
    Object localObject = null;
    if (localLocalActivityRecord != null)
    {
      Window localWindow = performDestroy(localLocalActivityRecord, paramBoolean);
      localObject = localWindow;
      if (paramBoolean)
      {
        this.mActivities.remove(paramString);
        this.mActivityArray.remove(localLocalActivityRecord);
        localObject = localWindow;
      }
    }
    return (Window)localObject;
  }
  
  public void dispatchCreate(Bundle paramBundle)
  {
    if (paramBundle != null)
    {
      Iterator localIterator = paramBundle.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        Bundle localBundle;
        try
        {
          localBundle = paramBundle.getBundle(str);
          localLocalActivityRecord = (LocalActivityRecord)this.mActivities.get(str);
          if (localLocalActivityRecord == null) {
            break label83;
          }
          localLocalActivityRecord.instanceState = localBundle;
        }
        catch (Exception localException)
        {
          Log.e("LocalActivityManager", "Exception thrown when restoring LocalActivityManager state", localException);
        }
        continue;
        label83:
        LocalActivityRecord localLocalActivityRecord = new LocalActivityRecord(localException, null);
        localLocalActivityRecord.instanceState = localBundle;
        this.mActivities.put(localException, localLocalActivityRecord);
        this.mActivityArray.add(localLocalActivityRecord);
      }
    }
    this.mCurState = 2;
  }
  
  public void dispatchDestroy(boolean paramBoolean)
  {
    int j = this.mActivityArray.size();
    int i = 0;
    while (i < j)
    {
      LocalActivityRecord localLocalActivityRecord = (LocalActivityRecord)this.mActivityArray.get(i);
      this.mActivityThread.performDestroyActivity(localLocalActivityRecord, paramBoolean);
      i += 1;
    }
    this.mActivities.clear();
    this.mActivityArray.clear();
  }
  
  public void dispatchPause(boolean paramBoolean)
  {
    if (paramBoolean) {
      this.mFinishing = true;
    }
    this.mCurState = 3;
    if (this.mSingleMode) {
      if (this.mResumed != null) {
        moveToState(this.mResumed, 3);
      }
    }
    for (;;)
    {
      return;
      int j = this.mActivityArray.size();
      int i = 0;
      while (i < j)
      {
        LocalActivityRecord localLocalActivityRecord = (LocalActivityRecord)this.mActivityArray.get(i);
        if (localLocalActivityRecord.curState == 4) {
          moveToState(localLocalActivityRecord, 3);
        }
        i += 1;
      }
    }
  }
  
  public void dispatchResume()
  {
    this.mCurState = 4;
    if (this.mSingleMode) {
      if (this.mResumed != null) {
        moveToState(this.mResumed, 4);
      }
    }
    for (;;)
    {
      return;
      int j = this.mActivityArray.size();
      int i = 0;
      while (i < j)
      {
        moveToState((LocalActivityRecord)this.mActivityArray.get(i), 4);
        i += 1;
      }
    }
  }
  
  public HashMap<String, Object> dispatchRetainNonConfigurationInstance()
  {
    Object localObject1 = null;
    int j = this.mActivityArray.size();
    int i = 0;
    while (i < j)
    {
      LocalActivityRecord localLocalActivityRecord = (LocalActivityRecord)this.mActivityArray.get(i);
      Object localObject2 = localObject1;
      if (localLocalActivityRecord != null)
      {
        localObject2 = localObject1;
        if (localLocalActivityRecord.activity != null)
        {
          Object localObject3 = localLocalActivityRecord.activity.onRetainNonConfigurationInstance();
          localObject2 = localObject1;
          if (localObject3 != null)
          {
            localObject2 = localObject1;
            if (localObject1 == null) {
              localObject2 = new HashMap();
            }
            ((HashMap)localObject2).put(localLocalActivityRecord.id, localObject3);
          }
        }
      }
      i += 1;
      localObject1 = localObject2;
    }
    return (HashMap<String, Object>)localObject1;
  }
  
  public void dispatchStop()
  {
    this.mCurState = 2;
    int j = this.mActivityArray.size();
    int i = 0;
    while (i < j)
    {
      moveToState((LocalActivityRecord)this.mActivityArray.get(i), 2);
      i += 1;
    }
  }
  
  public Activity getActivity(String paramString)
  {
    Object localObject = null;
    LocalActivityRecord localLocalActivityRecord = (LocalActivityRecord)this.mActivities.get(paramString);
    paramString = (String)localObject;
    if (localLocalActivityRecord != null) {
      paramString = localLocalActivityRecord.activity;
    }
    return paramString;
  }
  
  public Activity getCurrentActivity()
  {
    Activity localActivity = null;
    if (this.mResumed != null) {
      localActivity = this.mResumed.activity;
    }
    return localActivity;
  }
  
  public String getCurrentId()
  {
    String str = null;
    if (this.mResumed != null) {
      str = this.mResumed.id;
    }
    return str;
  }
  
  public void removeAllActivities()
  {
    dispatchDestroy(true);
  }
  
  public Bundle saveInstanceState()
  {
    Object localObject2 = null;
    int j = this.mActivityArray.size();
    int i = 0;
    while (i < j)
    {
      LocalActivityRecord localLocalActivityRecord = (LocalActivityRecord)this.mActivityArray.get(i);
      Object localObject1 = localObject2;
      if (localObject2 == null) {
        localObject1 = new Bundle();
      }
      if (((localLocalActivityRecord.instanceState != null) || (localLocalActivityRecord.curState == 4)) && (localLocalActivityRecord.activity != null))
      {
        localObject2 = new Bundle();
        localLocalActivityRecord.activity.performSaveInstanceState((Bundle)localObject2);
        localLocalActivityRecord.instanceState = ((Bundle)localObject2);
      }
      if (localLocalActivityRecord.instanceState != null) {
        ((Bundle)localObject1).putBundle(localLocalActivityRecord.id, localLocalActivityRecord.instanceState);
      }
      i += 1;
      localObject2 = localObject1;
    }
    return (Bundle)localObject2;
  }
  
  public Window startActivity(String paramString, Intent paramIntent)
  {
    if (this.mCurState == 1) {
      throw new IllegalStateException("Activities can't be added until the containing group has been created.");
    }
    int j = 0;
    int k = 0;
    Object localObject2 = null;
    LocalActivityRecord localLocalActivityRecord2 = (LocalActivityRecord)this.mActivities.get(paramString);
    LocalActivityRecord localLocalActivityRecord1;
    int i;
    Object localObject1;
    if (localLocalActivityRecord2 == null)
    {
      localLocalActivityRecord1 = new LocalActivityRecord(paramString, paramIntent);
      i = 1;
      localObject1 = localObject2;
      localObject2 = localObject1;
      if (localObject1 == null) {
        localObject2 = this.mActivityThread.resolveActivityInfo(paramIntent);
      }
      if (this.mSingleMode)
      {
        localObject1 = this.mResumed;
        if ((localObject1 != null) && (localObject1 != localLocalActivityRecord1) && (this.mCurState == 4)) {
          moveToState((LocalActivityRecord)localObject1, 3);
        }
      }
      if (i == 0) {
        break label270;
      }
      this.mActivities.put(paramString, localLocalActivityRecord1);
      this.mActivityArray.add(localLocalActivityRecord1);
    }
    for (;;)
    {
      localLocalActivityRecord1.intent = paramIntent;
      localLocalActivityRecord1.curState = 1;
      localLocalActivityRecord1.activityInfo = ((ActivityInfo)localObject2);
      moveToState(localLocalActivityRecord1, this.mCurState);
      if (this.mSingleMode) {
        this.mResumed = localLocalActivityRecord1;
      }
      return localLocalActivityRecord1.window;
      localObject1 = localObject2;
      i = j;
      localLocalActivityRecord1 = localLocalActivityRecord2;
      if (localLocalActivityRecord2.intent == null) {
        break;
      }
      boolean bool = localLocalActivityRecord2.intent.filterEquals(paramIntent);
      localObject1 = localObject2;
      i = j;
      localLocalActivityRecord1 = localLocalActivityRecord2;
      k = bool;
      if (!bool) {
        break;
      }
      localObject1 = localLocalActivityRecord2.activityInfo;
      i = j;
      localLocalActivityRecord1 = localLocalActivityRecord2;
      k = bool;
      break;
      label270:
      if (localLocalActivityRecord1.activityInfo != null)
      {
        if ((localObject2 == localLocalActivityRecord1.activityInfo) || ((((PackageItemInfo)localObject2).name.equals(localLocalActivityRecord1.activityInfo.name)) && (((PackageItemInfo)localObject2).packageName.equals(localLocalActivityRecord1.activityInfo.packageName))))
        {
          if ((((ActivityInfo)localObject2).launchMode != 0) || ((paramIntent.getFlags() & 0x20000000) != 0))
          {
            paramString = new ArrayList(1);
            paramString.add(new ReferrerIntent(paramIntent, this.mParent.getPackageName()));
            this.mActivityThread.performNewIntents(localLocalActivityRecord1, paramString, false);
            localLocalActivityRecord1.intent = paramIntent;
            moveToState(localLocalActivityRecord1, this.mCurState);
            if (this.mSingleMode) {
              this.mResumed = localLocalActivityRecord1;
            }
            return localLocalActivityRecord1.window;
          }
          if ((k != 0) && ((paramIntent.getFlags() & 0x4000000) == 0))
          {
            localLocalActivityRecord1.intent = paramIntent;
            moveToState(localLocalActivityRecord1, this.mCurState);
            if (this.mSingleMode) {
              this.mResumed = localLocalActivityRecord1;
            }
            return localLocalActivityRecord1.window;
          }
        }
        performDestroy(localLocalActivityRecord1, true);
      }
    }
  }
  
  private static class LocalActivityRecord
    extends Binder
  {
    Activity activity;
    ActivityInfo activityInfo;
    int curState = 0;
    final String id;
    Bundle instanceState;
    Intent intent;
    Window window;
    
    LocalActivityRecord(String paramString, Intent paramIntent)
    {
      this.id = paramString;
      this.intent = paramIntent;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/LocalActivityManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */