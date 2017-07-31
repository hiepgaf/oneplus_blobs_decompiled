package com.android.server.notification;

import android.content.ComponentName;
import android.net.Uri;
import android.service.notification.Condition;
import android.service.notification.IConditionProvider;
import android.service.notification.ZenModeConfig;
import android.service.notification.ZenModeConfig.ZenRule;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Objects;

public class ZenModeConditions
  implements ConditionProviders.Callback
{
  private static final boolean DEBUG = ZenModeHelper.DEBUG;
  private static final String TAG = "ZenModeHelper";
  private final ConditionProviders mConditionProviders;
  private boolean mFirstEvaluation = true;
  private final ZenModeHelper mHelper;
  private final ArrayMap<Uri, ComponentName> mSubscriptions = new ArrayMap();
  
  public ZenModeConditions(ZenModeHelper paramZenModeHelper, ConditionProviders paramConditionProviders)
  {
    this.mHelper = paramZenModeHelper;
    this.mConditionProviders = paramConditionProviders;
    if (this.mConditionProviders.isSystemProviderEnabled("countdown")) {
      this.mConditionProviders.addSystemProvider(new CountdownConditionProvider());
    }
    if (this.mConditionProviders.isSystemProviderEnabled("schedule")) {
      this.mConditionProviders.addSystemProvider(new ScheduleConditionProvider());
    }
    if (this.mConditionProviders.isSystemProviderEnabled("event")) {
      this.mConditionProviders.addSystemProvider(new EventConditionProvider());
    }
    this.mConditionProviders.setCallback(this);
  }
  
  private void evaluateRule(ZenModeConfig.ZenRule paramZenRule, ArraySet<Uri> paramArraySet, boolean paramBoolean)
  {
    if ((paramZenRule == null) || (paramZenRule.conditionId == null)) {
      return;
    }
    Uri localUri = paramZenRule.conditionId;
    int i = 0;
    Object localObject1 = this.mConditionProviders.getSystemProviders().iterator();
    Object localObject2;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (SystemConditionProviderService)((Iterator)localObject1).next();
      if (((SystemConditionProviderService)localObject2).isValidConditionId(localUri))
      {
        this.mConditionProviders.ensureRecordExists(((SystemConditionProviderService)localObject2).getComponent(), localUri, ((SystemConditionProviderService)localObject2).asInterface());
        paramZenRule.component = ((SystemConditionProviderService)localObject2).getComponent();
        i = 1;
      }
    }
    if (i == 0)
    {
      localObject1 = this.mConditionProviders.findConditionProvider(paramZenRule.component);
      if (DEBUG)
      {
        localObject2 = new StringBuilder().append("Ensure external rule exists: ");
        if (localObject1 == null) {
          break label234;
        }
      }
    }
    label234:
    for (boolean bool = true;; bool = false)
    {
      Log.d("ZenModeHelper", bool + " for " + localUri);
      if (localObject1 != null) {
        this.mConditionProviders.ensureRecordExists(paramZenRule.component, localUri, (IConditionProvider)localObject1);
      }
      if (paramZenRule.component != null) {
        break;
      }
      Log.w("ZenModeHelper", "No component found for automatic rule: " + paramZenRule.conditionId);
      paramZenRule.enabled = false;
      return;
    }
    if (paramArraySet != null) {
      paramArraySet.add(localUri);
    }
    if (paramBoolean)
    {
      if (!this.mConditionProviders.subscribeIfNecessary(paramZenRule.component, paramZenRule.conditionId)) {
        break label357;
      }
      this.mSubscriptions.put(paramZenRule.conditionId, paramZenRule.component);
    }
    for (;;)
    {
      if (paramZenRule.condition == null)
      {
        paramZenRule.condition = this.mConditionProviders.findCondition(paramZenRule.component, paramZenRule.conditionId);
        if ((paramZenRule.condition != null) && (DEBUG)) {
          Log.d("ZenModeHelper", "Found existing condition for: " + paramZenRule.conditionId);
        }
      }
      return;
      label357:
      paramZenRule.condition = null;
      if (DEBUG) {
        Log.d("ZenModeHelper", "zmc failed to subscribe");
      }
    }
  }
  
  private boolean isAutomaticActive(ComponentName paramComponentName)
  {
    if (paramComponentName == null) {
      return false;
    }
    Object localObject = this.mHelper.getConfig();
    if (localObject == null) {
      return false;
    }
    localObject = ((ZenModeConfig)localObject).automaticRules.values().iterator();
    while (((Iterator)localObject).hasNext())
    {
      ZenModeConfig.ZenRule localZenRule = (ZenModeConfig.ZenRule)((Iterator)localObject).next();
      if ((paramComponentName.equals(localZenRule.component)) && (localZenRule.isAutomaticActive())) {
        return true;
      }
    }
    return false;
  }
  
  private boolean updateCondition(Uri paramUri, Condition paramCondition, ZenModeConfig.ZenRule paramZenRule)
  {
    if ((paramUri == null) || (paramZenRule == null)) {}
    while (paramZenRule.conditionId == null) {
      return false;
    }
    if (!paramZenRule.conditionId.equals(paramUri)) {
      return false;
    }
    if (Objects.equals(paramCondition, paramZenRule.condition)) {
      return false;
    }
    paramZenRule.condition = paramCondition;
    return true;
  }
  
  private boolean updateSnoozing(ZenModeConfig.ZenRule paramZenRule)
  {
    if ((paramZenRule == null) || (!paramZenRule.snoozing) || ((!this.mFirstEvaluation) && (paramZenRule.isTrueOrUnknown()))) {
      return false;
    }
    paramZenRule.snoozing = false;
    if (DEBUG) {
      Log.d("ZenModeHelper", "Snoozing reset for " + paramZenRule.conditionId);
    }
    return true;
  }
  
  public void dump(PrintWriter paramPrintWriter, String paramString)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mSubscriptions=");
    paramPrintWriter.println(this.mSubscriptions);
  }
  
  public void evaluateConfig(ZenModeConfig paramZenModeConfig, boolean paramBoolean)
  {
    if (paramZenModeConfig == null) {
      return;
    }
    if ((paramZenModeConfig.manualRule == null) || (paramZenModeConfig.manualRule.condition == null) || (paramZenModeConfig.manualRule.isTrueOrUnknown())) {}
    ArraySet localArraySet;
    Object localObject;
    for (;;)
    {
      localArraySet = new ArraySet();
      evaluateRule(paramZenModeConfig.manualRule, localArraySet, paramBoolean);
      paramZenModeConfig = paramZenModeConfig.automaticRules.values().iterator();
      while (paramZenModeConfig.hasNext())
      {
        localObject = (ZenModeConfig.ZenRule)paramZenModeConfig.next();
        evaluateRule((ZenModeConfig.ZenRule)localObject, localArraySet, paramBoolean);
        updateSnoozing((ZenModeConfig.ZenRule)localObject);
      }
      if (DEBUG) {
        Log.d("ZenModeHelper", "evaluateConfig: clearing manual rule");
      }
      paramZenModeConfig.manualRule = null;
    }
    int i = this.mSubscriptions.size() - 1;
    while (i >= 0)
    {
      paramZenModeConfig = (Uri)this.mSubscriptions.keyAt(i);
      localObject = (ComponentName)this.mSubscriptions.valueAt(i);
      if ((paramBoolean) && (!localArraySet.contains(paramZenModeConfig)))
      {
        this.mConditionProviders.unsubscribeIfNecessary((ComponentName)localObject, paramZenModeConfig);
        this.mSubscriptions.removeAt(i);
      }
      i -= 1;
    }
    this.mFirstEvaluation = false;
  }
  
  public void onBootComplete() {}
  
  public void onConditionChanged(Uri paramUri, Condition paramCondition)
  {
    if (DEBUG) {
      Log.d("ZenModeHelper", "onConditionChanged " + paramUri + " " + paramCondition);
    }
    ZenModeConfig localZenModeConfig = this.mHelper.getConfig();
    if (localZenModeConfig == null) {
      return;
    }
    boolean bool = updateCondition(paramUri, paramCondition, localZenModeConfig.manualRule);
    Iterator localIterator = localZenModeConfig.automaticRules.values().iterator();
    while (localIterator.hasNext())
    {
      ZenModeConfig.ZenRule localZenRule = (ZenModeConfig.ZenRule)localIterator.next();
      bool = bool | updateCondition(paramUri, paramCondition, localZenRule) | updateSnoozing(localZenRule);
    }
    if (bool) {
      this.mHelper.setConfig(localZenModeConfig, "conditionChanged");
    }
  }
  
  public void onServiceAdded(ComponentName paramComponentName)
  {
    if (DEBUG) {
      Log.d("ZenModeHelper", "onServiceAdded " + paramComponentName);
    }
    this.mHelper.setConfig(this.mHelper.getConfig(), "zmc.onServiceAdded");
  }
  
  public void onUserSwitched() {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/notification/ZenModeConditions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */