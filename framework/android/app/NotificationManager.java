package android.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.ParceledListSlice;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.StrictMode;
import android.os.UserHandle;
import android.service.notification.StatusBarNotification;
import android.service.notification.ZenModeConfig;
import android.service.notification.ZenModeConfig.ZenRule;
import android.util.ArraySet;
import android.util.Log;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NotificationManager
{
  public static final String ACTION_EFFECTS_SUPPRESSOR_CHANGED = "android.os.action.ACTION_EFFECTS_SUPPRESSOR_CHANGED";
  public static final String ACTION_INTERRUPTION_FILTER_CHANGED = "android.app.action.INTERRUPTION_FILTER_CHANGED";
  public static final String ACTION_INTERRUPTION_FILTER_CHANGED_INTERNAL = "android.app.action.INTERRUPTION_FILTER_CHANGED_INTERNAL";
  public static final String ACTION_NOTIFICATION_POLICY_ACCESS_GRANTED_CHANGED = "android.app.action.NOTIFICATION_POLICY_ACCESS_GRANTED_CHANGED";
  public static final String ACTION_NOTIFICATION_POLICY_CHANGED = "android.app.action.NOTIFICATION_POLICY_CHANGED";
  public static final int IMPORTANCE_DEFAULT = 3;
  public static final int IMPORTANCE_HIGH = 4;
  public static final int IMPORTANCE_LOW = 2;
  public static final int IMPORTANCE_MAX = 5;
  public static final int IMPORTANCE_MIN = 1;
  public static final int IMPORTANCE_NONE = 0;
  public static final int IMPORTANCE_UNSPECIFIED = -1000;
  public static final int INTERRUPTION_FILTER_ALARMS = 4;
  public static final int INTERRUPTION_FILTER_ALL = 1;
  public static final int INTERRUPTION_FILTER_NONE = 3;
  public static final int INTERRUPTION_FILTER_PRIORITY = 2;
  public static final int INTERRUPTION_FILTER_UNKNOWN = 0;
  private static String TAG = "NotificationManager";
  public static final int VISIBILITY_NO_OVERRIDE = -1000;
  private static boolean localLOGV = false;
  private static INotificationManager sService;
  private Context mContext;
  
  NotificationManager(Context paramContext, Handler paramHandler)
  {
    this.mContext = paramContext;
  }
  
  private static void checkRequired(String paramString, Object paramObject)
  {
    if (paramObject == null) {
      throw new IllegalArgumentException(paramString + " is required");
    }
  }
  
  private void fixLegacySmallIcon(Notification paramNotification, String paramString)
  {
    if ((paramNotification.getSmallIcon() == null) && (paramNotification.icon != 0)) {
      paramNotification.setSmallIcon(Icon.createWithResource(paramString, paramNotification.icon));
    }
  }
  
  public static NotificationManager from(Context paramContext)
  {
    return (NotificationManager)paramContext.getSystemService("notification");
  }
  
  public static INotificationManager getService()
  {
    if (sService != null) {
      return sService;
    }
    sService = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
    return sService;
  }
  
  public static int zenModeFromInterruptionFilter(int paramInt1, int paramInt2)
  {
    switch (paramInt1)
    {
    default: 
      return paramInt2;
    case 1: 
      return 0;
    case 2: 
      return 1;
    case 4: 
      return 3;
    }
    return 2;
  }
  
  public static int zenModeToInterruptionFilter(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return 0;
    case 0: 
      return 1;
    case 1: 
      return 2;
    }
    return 3;
  }
  
  public String addAutomaticZenRule(AutomaticZenRule paramAutomaticZenRule)
  {
    INotificationManager localINotificationManager = getService();
    try
    {
      paramAutomaticZenRule = localINotificationManager.addAutomaticZenRule(paramAutomaticZenRule);
      return paramAutomaticZenRule;
    }
    catch (RemoteException paramAutomaticZenRule)
    {
      throw paramAutomaticZenRule.rethrowFromSystemServer();
    }
  }
  
  public boolean areNotificationsEnabled()
  {
    INotificationManager localINotificationManager = getService();
    try
    {
      boolean bool = localINotificationManager.areNotificationsEnabled(this.mContext.getPackageName());
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void cancel(int paramInt)
  {
    cancel(null, paramInt);
  }
  
  public void cancel(String paramString, int paramInt)
  {
    cancelAsUser(paramString, paramInt, new UserHandle(UserHandle.myUserId()));
  }
  
  public void cancelAll()
  {
    INotificationManager localINotificationManager = getService();
    String str = this.mContext.getPackageName();
    if (localLOGV) {
      Log.v(TAG, str + ": cancelAll()");
    }
    try
    {
      localINotificationManager.cancelAllNotifications(str, UserHandle.myUserId());
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void cancelAsUser(String paramString, int paramInt, UserHandle paramUserHandle)
  {
    INotificationManager localINotificationManager = getService();
    String str = this.mContext.getPackageName();
    if (localLOGV) {
      Log.v(TAG, str + ": cancel(" + paramInt + ")");
    }
    try
    {
      localINotificationManager.cancelNotificationWithTag(str, paramString, paramInt, paramUserHandle.getIdentifier());
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public StatusBarNotification[] getActiveNotifications()
  {
    Object localObject = getService();
    String str = this.mContext.getPackageName();
    try
    {
      localObject = ((INotificationManager)localObject).getAppActiveNotifications(str, UserHandle.myUserId()).getList();
      localObject = (StatusBarNotification[])((List)localObject).toArray(new StatusBarNotification[((List)localObject).size()]);
      return (StatusBarNotification[])localObject;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public AutomaticZenRule getAutomaticZenRule(String paramString)
  {
    INotificationManager localINotificationManager = getService();
    try
    {
      paramString = localINotificationManager.getAutomaticZenRule(paramString);
      return paramString;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public Map<String, AutomaticZenRule> getAutomaticZenRules()
  {
    Object localObject1 = getService();
    try
    {
      Object localObject2 = ((INotificationManager)localObject1).getZenRules();
      localObject1 = new HashMap();
      localObject2 = ((Iterable)localObject2).iterator();
      while (((Iterator)localObject2).hasNext())
      {
        ZenModeConfig.ZenRule localZenRule = (ZenModeConfig.ZenRule)((Iterator)localObject2).next();
        ((Map)localObject1).put(localZenRule.id, new AutomaticZenRule(localZenRule.name, localZenRule.component, localZenRule.conditionId, zenModeToInterruptionFilter(localZenRule.zenMode), localZenRule.enabled, localZenRule.creationTime));
      }
      return localRemoteException;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public final int getCurrentInterruptionFilter()
  {
    INotificationManager localINotificationManager = getService();
    try
    {
      int i = zenModeToInterruptionFilter(localINotificationManager.getZenMode());
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public ComponentName getEffectsSuppressor()
  {
    Object localObject = getService();
    try
    {
      localObject = ((INotificationManager)localObject).getEffectsSuppressor();
      return (ComponentName)localObject;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int getImportance()
  {
    INotificationManager localINotificationManager = getService();
    try
    {
      int i = localINotificationManager.getPackageImportance(this.mContext.getPackageName());
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public Policy getNotificationPolicy()
  {
    Object localObject = getService();
    try
    {
      localObject = ((INotificationManager)localObject).getNotificationPolicy(this.mContext.getOpPackageName());
      return (Policy)localObject;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public ArraySet<String> getPackagesRequestingNotificationPolicyAccess()
  {
    Object localObject = getService();
    try
    {
      localObject = ((INotificationManager)localObject).getPackagesRequestingNotificationPolicyAccess();
      if ((localObject != null) && (localObject.length > 0))
      {
        ArraySet localArraySet = new ArraySet(localObject.length);
        int i = 0;
        while (i < localObject.length)
        {
          localArraySet.add(localObject[i]);
          i += 1;
        }
        return localArraySet;
      }
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
    return new ArraySet();
  }
  
  public int getRuleInstanceCount(ComponentName paramComponentName)
  {
    INotificationManager localINotificationManager = getService();
    try
    {
      int i = localINotificationManager.getRuleInstanceCount(paramComponentName);
      return i;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public int getZenMode()
  {
    INotificationManager localINotificationManager = getService();
    try
    {
      int i = localINotificationManager.getZenMode();
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public ZenModeConfig getZenModeConfig()
  {
    Object localObject = getService();
    try
    {
      localObject = ((INotificationManager)localObject).getZenModeConfig();
      return (ZenModeConfig)localObject;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isNotificationPolicyAccessGranted()
  {
    INotificationManager localINotificationManager = getService();
    try
    {
      boolean bool = localINotificationManager.isNotificationPolicyAccessGranted(this.mContext.getOpPackageName());
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isNotificationPolicyAccessGrantedForPackage(String paramString)
  {
    INotificationManager localINotificationManager = getService();
    try
    {
      boolean bool = localINotificationManager.isNotificationPolicyAccessGrantedForPackage(paramString);
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public boolean isSystemConditionProviderEnabled(String paramString)
  {
    INotificationManager localINotificationManager = getService();
    try
    {
      boolean bool = localINotificationManager.isSystemConditionProviderEnabled(paramString);
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public boolean matchesCallFilter(Bundle paramBundle)
  {
    INotificationManager localINotificationManager = getService();
    try
    {
      boolean bool = localINotificationManager.matchesCallFilter(paramBundle);
      return bool;
    }
    catch (RemoteException paramBundle)
    {
      throw paramBundle.rethrowFromSystemServer();
    }
  }
  
  public void notify(int paramInt, Notification paramNotification)
  {
    notify(null, paramInt, paramNotification);
  }
  
  public void notify(String paramString, int paramInt, Notification paramNotification)
  {
    notifyAsUser(paramString, paramInt, paramNotification, new UserHandle(UserHandle.myUserId()));
  }
  
  public void notifyAsUser(String paramString, int paramInt, Notification paramNotification, UserHandle paramUserHandle)
  {
    int[] arrayOfInt = new int[1];
    INotificationManager localINotificationManager = getService();
    String str = this.mContext.getPackageName();
    Notification.addFieldsFromContext(this.mContext, paramNotification);
    if (paramNotification.sound != null)
    {
      paramNotification.sound = paramNotification.sound.getCanonicalUri();
      if (StrictMode.vmFileUriExposureEnabled()) {
        paramNotification.sound.checkFileUriExposed("Notification.sound");
      }
    }
    fixLegacySmallIcon(paramNotification, str);
    if ((this.mContext.getApplicationInfo().targetSdkVersion > 22) && (paramNotification.getSmallIcon() == null)) {
      throw new IllegalArgumentException("Invalid notification (no valid small icon): " + paramNotification);
    }
    if (localLOGV) {
      Log.v(TAG, str + ": notify(" + paramInt + ", " + paramNotification + ")");
    }
    paramNotification = Notification.Builder.maybeCloneStrippedForDelivery(paramNotification);
    try
    {
      localINotificationManager.enqueueNotificationWithTag(str, this.mContext.getOpPackageName(), paramString, paramInt, paramNotification, arrayOfInt, paramUserHandle.getIdentifier());
      if ((localLOGV) && (paramInt != arrayOfInt[0])) {
        Log.v(TAG, "notify: id corrupted: sent " + paramInt + ", got back " + arrayOfInt[0]);
      }
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public boolean removeAutomaticZenRule(String paramString)
  {
    INotificationManager localINotificationManager = getService();
    try
    {
      boolean bool = localINotificationManager.removeAutomaticZenRule(paramString);
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public boolean removeAutomaticZenRules(String paramString)
  {
    INotificationManager localINotificationManager = getService();
    try
    {
      boolean bool = localINotificationManager.removeAutomaticZenRules(paramString);
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public final void setInterruptionFilter(int paramInt)
  {
    INotificationManager localINotificationManager = getService();
    try
    {
      localINotificationManager.setInterruptionFilter(this.mContext.getOpPackageName(), paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void setNotificationPolicy(Policy paramPolicy)
  {
    checkRequired("policy", paramPolicy);
    INotificationManager localINotificationManager = getService();
    try
    {
      localINotificationManager.setNotificationPolicy(this.mContext.getOpPackageName(), paramPolicy);
      return;
    }
    catch (RemoteException paramPolicy)
    {
      throw paramPolicy.rethrowFromSystemServer();
    }
  }
  
  public void setNotificationPolicyAccessGranted(String paramString, boolean paramBoolean)
  {
    INotificationManager localINotificationManager = getService();
    try
    {
      localINotificationManager.setNotificationPolicyAccessGranted(paramString, paramBoolean);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void setOnePlusVibrateInSilentMode(boolean paramBoolean)
  {
    INotificationManager localINotificationManager = getService();
    try
    {
      localINotificationManager.setOnePlusVibrateInSilentMode(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public void setZenMode(int paramInt, Uri paramUri, String paramString)
  {
    INotificationManager localINotificationManager = getService();
    try
    {
      localINotificationManager.setZenMode(paramInt, paramUri, paramString);
      return;
    }
    catch (RemoteException paramUri)
    {
      throw paramUri.rethrowFromSystemServer();
    }
  }
  
  public boolean updateAutomaticZenRule(String paramString, AutomaticZenRule paramAutomaticZenRule)
  {
    INotificationManager localINotificationManager = getService();
    try
    {
      boolean bool = localINotificationManager.updateAutomaticZenRule(paramString, paramAutomaticZenRule);
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public static class Policy
    implements Parcelable
  {
    private static final int[] ALL_PRIORITY_CATEGORIES = { 1, 2, 4, 8, 16 };
    private static final int[] ALL_SUPPRESSED_EFFECTS = { 1, 2 };
    public static final Parcelable.Creator<Policy> CREATOR = new Parcelable.Creator()
    {
      public NotificationManager.Policy createFromParcel(Parcel paramAnonymousParcel)
      {
        return new NotificationManager.Policy(paramAnonymousParcel);
      }
      
      public NotificationManager.Policy[] newArray(int paramAnonymousInt)
      {
        return new NotificationManager.Policy[paramAnonymousInt];
      }
    };
    public static final int PRIORITY_CATEGORY_CALLS = 8;
    public static final int PRIORITY_CATEGORY_EVENTS = 2;
    public static final int PRIORITY_CATEGORY_MESSAGES = 4;
    public static final int PRIORITY_CATEGORY_REMINDERS = 1;
    public static final int PRIORITY_CATEGORY_REPEAT_CALLERS = 16;
    public static final int PRIORITY_SENDERS_ANY = 0;
    public static final int PRIORITY_SENDERS_CONTACTS = 1;
    public static final int PRIORITY_SENDERS_STARRED = 2;
    public static final int SUPPRESSED_EFFECTS_UNSET = -1;
    public static final int SUPPRESSED_EFFECT_SCREEN_OFF = 1;
    public static final int SUPPRESSED_EFFECT_SCREEN_ON = 2;
    public final int priorityCallSenders;
    public final int priorityCategories;
    public final int priorityMessageSenders;
    public final int suppressedVisualEffects;
    
    public Policy(int paramInt1, int paramInt2, int paramInt3)
    {
      this(paramInt1, paramInt2, paramInt3, -1);
    }
    
    public Policy(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.priorityCategories = paramInt1;
      this.priorityCallSenders = paramInt2;
      this.priorityMessageSenders = paramInt3;
      this.suppressedVisualEffects = paramInt4;
    }
    
    public Policy(Parcel paramParcel)
    {
      this(paramParcel.readInt(), paramParcel.readInt(), paramParcel.readInt(), paramParcel.readInt());
    }
    
    private static String effectToString(int paramInt)
    {
      switch (paramInt)
      {
      case 0: 
      default: 
        return "UNKNOWN_" + paramInt;
      case 1: 
        return "SUPPRESSED_EFFECT_SCREEN_OFF";
      case 2: 
        return "SUPPRESSED_EFFECT_SCREEN_ON";
      }
      return "SUPPRESSED_EFFECTS_UNSET";
    }
    
    public static String priorityCategoriesToString(int paramInt)
    {
      if (paramInt == 0) {
        return "";
      }
      StringBuilder localStringBuilder = new StringBuilder();
      int j = 0;
      int i = paramInt;
      paramInt = j;
      while (paramInt < ALL_PRIORITY_CATEGORIES.length)
      {
        j = ALL_PRIORITY_CATEGORIES[paramInt];
        if ((i & j) != 0)
        {
          if (localStringBuilder.length() > 0) {
            localStringBuilder.append(',');
          }
          localStringBuilder.append(priorityCategoryToString(j));
        }
        i &= j;
        paramInt += 1;
      }
      if (i != 0)
      {
        if (localStringBuilder.length() > 0) {
          localStringBuilder.append(',');
        }
        localStringBuilder.append("PRIORITY_CATEGORY_UNKNOWN_").append(i);
      }
      return localStringBuilder.toString();
    }
    
    private static String priorityCategoryToString(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return "PRIORITY_CATEGORY_UNKNOWN_" + paramInt;
      case 1: 
        return "PRIORITY_CATEGORY_REMINDERS";
      case 2: 
        return "PRIORITY_CATEGORY_EVENTS";
      case 4: 
        return "PRIORITY_CATEGORY_MESSAGES";
      case 8: 
        return "PRIORITY_CATEGORY_CALLS";
      }
      return "PRIORITY_CATEGORY_REPEAT_CALLERS";
    }
    
    public static String prioritySendersToString(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return "PRIORITY_SENDERS_UNKNOWN_" + paramInt;
      case 0: 
        return "PRIORITY_SENDERS_ANY";
      case 1: 
        return "PRIORITY_SENDERS_CONTACTS";
      }
      return "PRIORITY_SENDERS_STARRED";
    }
    
    public static String suppressedEffectsToString(int paramInt)
    {
      if (paramInt <= 0) {
        return "";
      }
      StringBuilder localStringBuilder = new StringBuilder();
      int j = 0;
      int i = paramInt;
      paramInt = j;
      while (paramInt < ALL_SUPPRESSED_EFFECTS.length)
      {
        j = ALL_SUPPRESSED_EFFECTS[paramInt];
        if ((i & j) != 0)
        {
          if (localStringBuilder.length() > 0) {
            localStringBuilder.append(',');
          }
          localStringBuilder.append(effectToString(j));
        }
        i &= j;
        paramInt += 1;
      }
      if (i != 0)
      {
        if (localStringBuilder.length() > 0) {
          localStringBuilder.append(',');
        }
        localStringBuilder.append("UNKNOWN_").append(i);
      }
      return localStringBuilder.toString();
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof Policy)) {
        return false;
      }
      if (paramObject == this) {
        return true;
      }
      paramObject = (Policy)paramObject;
      if ((((Policy)paramObject).priorityCategories == this.priorityCategories) && (((Policy)paramObject).priorityCallSenders == this.priorityCallSenders) && (((Policy)paramObject).priorityMessageSenders == this.priorityMessageSenders)) {
        return ((Policy)paramObject).suppressedVisualEffects == this.suppressedVisualEffects;
      }
      return false;
    }
    
    public int hashCode()
    {
      return Objects.hash(new Object[] { Integer.valueOf(this.priorityCategories), Integer.valueOf(this.priorityCallSenders), Integer.valueOf(this.priorityMessageSenders), Integer.valueOf(this.suppressedVisualEffects) });
    }
    
    public String toString()
    {
      return "NotificationManager.Policy[priorityCategories=" + priorityCategoriesToString(this.priorityCategories) + ",priorityCallSenders=" + prioritySendersToString(this.priorityCallSenders) + ",priorityMessageSenders=" + prioritySendersToString(this.priorityMessageSenders) + ",suppressedVisualEffects=" + suppressedEffectsToString(this.suppressedVisualEffects) + "]";
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.priorityCategories);
      paramParcel.writeInt(this.priorityCallSenders);
      paramParcel.writeInt(this.priorityMessageSenders);
      paramParcel.writeInt(this.suppressedVisualEffects);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/NotificationManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */