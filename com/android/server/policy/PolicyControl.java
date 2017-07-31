package com.android.server.policy;

import android.app.ActivityManager;
import android.content.Context;
import android.provider.Settings.Global;
import android.util.ArraySet;
import android.util.Slog;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManagerPolicy.WindowState;
import java.io.PrintWriter;
import java.io.StringWriter;

public class PolicyControl
{
  private static boolean DEBUG = false;
  private static final String NAME_IMMERSIVE_FULL = "immersive.full";
  private static final String NAME_IMMERSIVE_NAVIGATION = "immersive.navigation";
  private static final String NAME_IMMERSIVE_PRECONFIRMATIONS = "immersive.preconfirms";
  private static final String NAME_IMMERSIVE_STATUS = "immersive.status";
  private static String TAG = "PolicyControl";
  private static Filter sImmersiveNavigationFilter;
  private static Filter sImmersivePreconfirmationsFilter;
  private static Filter sImmersiveStatusFilter;
  private static String sSettingValue;
  
  public static int adjustClearableFlags(WindowManagerPolicy.WindowState paramWindowState, int paramInt)
  {
    if (paramWindowState != null) {}
    for (paramWindowState = paramWindowState.getAttrs();; paramWindowState = null)
    {
      int i = paramInt;
      if (sImmersiveStatusFilter != null)
      {
        i = paramInt;
        if (sImmersiveStatusFilter.matches(paramWindowState)) {
          i = paramInt & 0xFFFFFFFB;
        }
      }
      return i;
    }
  }
  
  public static boolean disableImmersiveConfirmation(String paramString)
  {
    if (((sImmersivePreconfirmationsFilter != null) && (sImmersivePreconfirmationsFilter.matches(paramString))) || (OpGlobalActions.getActionState().ordinal() != OpGlobalActions.ActionState.INIT.ordinal())) {
      return true;
    }
    return ActivityManager.isRunningInTestHarness();
  }
  
  private static void dump(String paramString1, Filter paramFilter, String paramString2, PrintWriter paramPrintWriter)
  {
    paramPrintWriter.print(paramString2);
    paramPrintWriter.print("PolicyControl.");
    paramPrintWriter.print(paramString1);
    paramPrintWriter.print('=');
    if (paramFilter == null)
    {
      paramPrintWriter.println("null");
      return;
    }
    paramFilter.dump(paramPrintWriter);
    paramPrintWriter.println();
  }
  
  public static void dump(String paramString, PrintWriter paramPrintWriter)
  {
    dump("sImmersiveStatusFilter", sImmersiveStatusFilter, paramString, paramPrintWriter);
    dump("sImmersiveNavigationFilter", sImmersiveNavigationFilter, paramString, paramPrintWriter);
    dump("sImmersivePreconfirmationsFilter", sImmersivePreconfirmationsFilter, paramString, paramPrintWriter);
  }
  
  public static int getSystemUiVisibility(WindowManagerPolicy.WindowState paramWindowState, WindowManager.LayoutParams paramLayoutParams)
  {
    if (paramLayoutParams != null) {
      if (paramWindowState == null) {
        break label85;
      }
    }
    label85:
    for (int j = paramWindowState.getSystemUiVisibility();; j = paramLayoutParams.systemUiVisibility)
    {
      int i = j;
      if (sImmersiveStatusFilter != null)
      {
        i = j;
        if (sImmersiveStatusFilter.matches(paramLayoutParams)) {
          i = (j | 0x1404) & 0xBFFFFEFF;
        }
      }
      j = i;
      if (sImmersiveNavigationFilter != null)
      {
        j = i;
        if (sImmersiveNavigationFilter.matches(paramLayoutParams)) {
          j = (i | 0x1202) & 0x7FFFFEFF;
        }
      }
      return j;
      paramLayoutParams = paramWindowState.getAttrs();
      break;
    }
  }
  
  public static int getWindowFlags(WindowManagerPolicy.WindowState paramWindowState, WindowManager.LayoutParams paramLayoutParams)
  {
    if (paramLayoutParams != null) {}
    for (;;)
    {
      int j = paramLayoutParams.flags;
      int i = j;
      if (sImmersiveStatusFilter != null)
      {
        i = j;
        if (sImmersiveStatusFilter.matches(paramLayoutParams)) {
          i = (j | 0x400) & 0xFBFFF7FF;
        }
      }
      j = i;
      if (sImmersiveNavigationFilter != null)
      {
        j = i;
        if (sImmersiveNavigationFilter.matches(paramLayoutParams)) {
          j = i & 0xF7FFFFFF;
        }
      }
      return j;
      paramLayoutParams = paramWindowState.getAttrs();
    }
  }
  
  public static void reloadFromSetting(Context paramContext)
  {
    if (DEBUG) {
      Slog.d(TAG, "reloadFromSetting()");
    }
    Context localContext = null;
    try
    {
      paramContext = Settings.Global.getStringForUser(paramContext.getContentResolver(), "policy_control", -2);
      localContext = paramContext;
      if (sSettingValue != null)
      {
        localContext = paramContext;
        if (sSettingValue.equals(paramContext)) {
          return;
        }
      }
      localContext = paramContext;
      setFilters(paramContext);
      localContext = paramContext;
      sSettingValue = paramContext;
      return;
    }
    catch (Throwable paramContext)
    {
      Slog.w(TAG, "Error loading policy control, value=" + localContext, paramContext);
    }
  }
  
  private static void setFilters(String paramString)
  {
    if (DEBUG) {
      Slog.d(TAG, "setFilters: " + paramString);
    }
    sImmersiveStatusFilter = null;
    sImmersiveNavigationFilter = null;
    sImmersivePreconfirmationsFilter = null;
    if (paramString != null)
    {
      paramString = paramString.split(":");
      int j = paramString.length;
      int i = 0;
      if (i < j)
      {
        String str = paramString[i];
        int k = str.indexOf('=');
        if (k == -1) {}
        for (;;)
        {
          i += 1;
          break;
          Object localObject = str.substring(0, k);
          str = str.substring(k + 1);
          if (((String)localObject).equals("immersive.full"))
          {
            localObject = Filter.parse(str);
            sImmersiveNavigationFilter = (Filter)localObject;
            sImmersiveStatusFilter = (Filter)localObject;
            if (sImmersivePreconfirmationsFilter == null) {
              sImmersivePreconfirmationsFilter = (Filter)localObject;
            }
          }
          else if (((String)localObject).equals("immersive.status"))
          {
            sImmersiveStatusFilter = Filter.parse(str);
          }
          else if (((String)localObject).equals("immersive.navigation"))
          {
            localObject = Filter.parse(str);
            sImmersiveNavigationFilter = (Filter)localObject;
            if (sImmersivePreconfirmationsFilter == null) {
              sImmersivePreconfirmationsFilter = (Filter)localObject;
            }
          }
          else if (((String)localObject).equals("immersive.preconfirms"))
          {
            sImmersivePreconfirmationsFilter = Filter.parse(str);
          }
        }
      }
    }
    if (DEBUG)
    {
      Slog.d(TAG, "immersiveStatusFilter: " + sImmersiveStatusFilter);
      Slog.d(TAG, "immersiveNavigationFilter: " + sImmersiveNavigationFilter);
      Slog.d(TAG, "immersivePreconfirmationsFilter: " + sImmersivePreconfirmationsFilter);
    }
  }
  
  private static class Filter
  {
    private static final String ALL = "*";
    private static final String APPS = "apps";
    private final ArraySet<String> mBlacklist;
    private final ArraySet<String> mWhitelist;
    
    private Filter(ArraySet<String> paramArraySet1, ArraySet<String> paramArraySet2)
    {
      this.mWhitelist = paramArraySet1;
      this.mBlacklist = paramArraySet2;
    }
    
    private void dump(String paramString, ArraySet<String> paramArraySet, PrintWriter paramPrintWriter)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("=(");
      int j = paramArraySet.size();
      int i = 0;
      while (i < j)
      {
        if (i > 0) {
          paramPrintWriter.print(',');
        }
        paramPrintWriter.print((String)paramArraySet.valueAt(i));
        i += 1;
      }
      paramPrintWriter.print(')');
    }
    
    private boolean onBlacklist(String paramString)
    {
      if (!this.mBlacklist.contains(paramString)) {
        return this.mBlacklist.contains("*");
      }
      return true;
    }
    
    private boolean onWhitelist(String paramString)
    {
      if (!this.mWhitelist.contains("*")) {
        return this.mWhitelist.contains(paramString);
      }
      return true;
    }
    
    static Filter parse(String paramString)
    {
      if (paramString == null) {
        return null;
      }
      ArraySet localArraySet1 = new ArraySet();
      ArraySet localArraySet2 = new ArraySet();
      paramString = paramString.split(",");
      int i = 0;
      int j = paramString.length;
      if (i < j)
      {
        String str = paramString[i].trim();
        if ((str.startsWith("-")) && (str.length() > 1)) {
          localArraySet2.add(str.substring(1));
        }
        for (;;)
        {
          i += 1;
          break;
          localArraySet1.add(str);
        }
      }
      return new Filter(localArraySet1, localArraySet2);
    }
    
    void dump(PrintWriter paramPrintWriter)
    {
      paramPrintWriter.print("Filter[");
      dump("whitelist", this.mWhitelist, paramPrintWriter);
      paramPrintWriter.print(',');
      dump("blacklist", this.mBlacklist, paramPrintWriter);
      paramPrintWriter.print(']');
    }
    
    boolean matches(WindowManager.LayoutParams paramLayoutParams)
    {
      if (paramLayoutParams == null) {
        return false;
      }
      int i;
      if (paramLayoutParams.type >= 1) {
        if (paramLayoutParams.type <= 99) {
          i = 1;
        }
      }
      while ((i != 0) && (this.mBlacklist.contains("apps")))
      {
        return false;
        i = 0;
        continue;
        i = 0;
      }
      if (onBlacklist(paramLayoutParams.packageName)) {
        return false;
      }
      if ((i != 0) && (this.mWhitelist.contains("apps"))) {
        return true;
      }
      return onWhitelist(paramLayoutParams.packageName);
    }
    
    boolean matches(String paramString)
    {
      if (!onBlacklist(paramString)) {
        return onWhitelist(paramString);
      }
      return false;
    }
    
    public String toString()
    {
      StringWriter localStringWriter = new StringWriter();
      dump(new PrintWriter(localStringWriter, true));
      return localStringWriter.toString();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/policy/PolicyControl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */