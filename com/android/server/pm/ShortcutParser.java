package com.android.server.pm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Slog;
import android.util.Xml;
import com.android.internal.R.styleable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.xmlpull.v1.XmlPullParserException;

public class ShortcutParser
{
  private static final boolean DEBUG;
  static final String METADATA_KEY = "android.app.shortcuts";
  private static final String TAG = "ShortcutService";
  private static final String TAG_CATEGORIES = "categories";
  private static final String TAG_INTENT = "intent";
  private static final String TAG_SHORTCUT = "shortcut";
  private static final String TAG_SHORTCUTS = "shortcuts";
  
  static
  {
    if (!ShortcutService.DEBUG) {}
    for (boolean bool = false;; bool = true)
    {
      DEBUG = bool;
      return;
    }
  }
  
  private static ShortcutInfo createShortcutFromManifest(ShortcutService paramShortcutService, int paramInt1, String paramString1, String paramString2, ComponentName paramComponentName, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean)
  {
    int i;
    if (paramBoolean)
    {
      i = 32;
      if (paramInt6 == 0) {
        break label72;
      }
    }
    label72:
    for (int j = 4;; j = 0)
    {
      return new ShortcutInfo(paramInt1, paramString1, paramString2, paramComponentName, null, null, paramInt2, null, null, paramInt3, null, null, paramInt4, null, null, null, paramInt5, null, paramShortcutService.injectCurrentTimeMillis(), i | 0x100 | j, paramInt6, null, null);
      i = 64;
      break;
    }
  }
  
  private static String parseCategories(ShortcutService paramShortcutService, AttributeSet paramAttributeSet)
  {
    paramShortcutService = paramShortcutService.mContext.getResources().obtainAttributes(paramAttributeSet, R.styleable.ShortcutCategories);
    try
    {
      if (paramShortcutService.getType(0) == 3)
      {
        paramAttributeSet = paramShortcutService.getNonResourceString(0);
        return paramAttributeSet;
      }
      Log.w("ShortcutService", "android:name for shortcut category must be string literal.");
      return null;
    }
    finally
    {
      paramShortcutService.recycle();
    }
  }
  
  private static ShortcutInfo parseShortcutAttributes(ShortcutService paramShortcutService, AttributeSet paramAttributeSet, String paramString, ComponentName paramComponentName, int paramInt1, int paramInt2)
  {
    paramAttributeSet = paramShortcutService.mContext.getResources().obtainAttributes(paramAttributeSet, R.styleable.Shortcut);
    try
    {
      if (paramAttributeSet.getType(2) != 3)
      {
        Log.w("ShortcutService", "android:shortcutId must be string literal. activity=" + paramComponentName);
        return null;
      }
      String str = paramAttributeSet.getNonResourceString(2);
      boolean bool = paramAttributeSet.getBoolean(1, true);
      int i = paramAttributeSet.getResourceId(0, 0);
      int j = paramAttributeSet.getResourceId(3, 0);
      int k = paramAttributeSet.getResourceId(4, 0);
      int m = paramAttributeSet.getResourceId(5, 0);
      if (TextUtils.isEmpty(str))
      {
        Log.w("ShortcutService", "android:shortcutId must be provided. activity=" + paramComponentName);
        return null;
      }
      if (j == 0)
      {
        Log.w("ShortcutService", "android:shortcutShortLabel must be provided. activity=" + paramComponentName);
        return null;
      }
      paramShortcutService = createShortcutFromManifest(paramShortcutService, paramInt1, str, paramString, paramComponentName, j, k, m, paramInt2, i, bool);
      return paramShortcutService;
    }
    finally
    {
      paramAttributeSet.recycle();
    }
  }
  
  public static List<ShortcutInfo> parseShortcuts(ShortcutService paramShortcutService, String paramString, int paramInt)
    throws IOException, XmlPullParserException
  {
    if (ShortcutService.DEBUG) {
      Slog.d("ShortcutService", String.format("Scanning package %s for manifest shortcuts on user %d", new Object[] { paramString, Integer.valueOf(paramInt) }));
    }
    List localList2 = paramShortcutService.injectGetMainActivities(paramString, paramInt);
    if ((localList2 == null) || (localList2.size() == 0)) {
      return null;
    }
    Object localObject2;
    Object localObject1;
    for (List localList1 = null;; localObject1 = localObject2)
    {
      int i;
      try
      {
        int j = localList2.size();
        i = 0;
        if (i < j)
        {
          localObject2 = ((ResolveInfo)localList2.get(i)).activityInfo;
          if (localObject2 == null)
          {
            localObject2 = localList1;
          }
          else
          {
            ActivityInfo localActivityInfo = paramShortcutService.getActivityInfoWithMetadata(((ActivityInfo)localObject2).getComponentName(), paramInt);
            localObject2 = localList1;
            if (localActivityInfo != null) {
              localObject2 = parseShortcutsOneFile(paramShortcutService, localActivityInfo, paramString, paramInt, localList1);
            }
          }
        }
      }
      catch (RuntimeException localRuntimeException)
      {
        paramShortcutService.wtf("Exception caught while parsing shortcut XML for package=" + paramString, localRuntimeException);
        return null;
      }
      return localRuntimeException;
      i += 1;
    }
  }
  
  private static List<ShortcutInfo> parseShortcutsOneFile(ShortcutService paramShortcutService, ActivityInfo paramActivityInfo, String paramString, int paramInt, List<ShortcutInfo> paramList)
    throws IOException, XmlPullParserException
  {
    if (ShortcutService.DEBUG) {
      Slog.d("ShortcutService", String.format("Checking main activity %s", new Object[] { paramActivityInfo.getComponentName() }));
    }
    localObject1 = null;
    for (;;)
    {
      try
      {
        localXmlResourceParser = paramShortcutService.injectXmlMetaData(paramActivityInfo, "android.app.shortcuts");
        if (localXmlResourceParser == null)
        {
          if (localXmlResourceParser != null) {
            localXmlResourceParser.close();
          }
          return paramList;
        }
        localObject1 = localXmlResourceParser;
        localComponentName = new ComponentName(paramString, paramActivityInfo.name);
        localObject1 = localXmlResourceParser;
        localAttributeSet = Xml.asAttributeSet(localXmlResourceParser);
        i = 0;
        localObject1 = localXmlResourceParser;
        m = paramShortcutService.getMaxActivityShortcuts();
        j = 0;
        localList2 = null;
        localObject2 = null;
        localObject1 = localXmlResourceParser;
        localArrayList = new ArrayList();
        localList1 = paramList;
        paramList = localList2;
        localObject1 = localObject2;
      }
      finally
      {
        XmlResourceParser localXmlResourceParser;
        ComponentName localComponentName;
        AttributeSet localAttributeSet;
        int i;
        int m;
        int j;
        List<ShortcutInfo> localList2;
        Object localObject2;
        ArrayList localArrayList;
        List<ShortcutInfo> localList1;
        int k;
        int n;
        Object localObject3;
        continue;
        paramList = localList1;
        continue;
        k -= 1;
        continue;
        paramList = (List<ShortcutInfo>)localObject2;
        localObject1 = null;
        continue;
      }
      localList2 = paramList;
      try
      {
        k = localXmlResourceParser.next();
        if ((k == 1) || ((k == 3) && (localXmlResourceParser.getDepth() <= 0))) {
          continue;
        }
        n = localXmlResourceParser.getDepth();
        localObject2 = localXmlResourceParser.getName();
        if ((k != 3) || (n != 2) || (!"shortcut".equals(localObject2))) {
          continue;
        }
        paramList = localList2;
        if (localList2 == null) {
          continue;
        }
        localObject3 = null;
        if (localList2.isEnabled())
        {
          if (localArrayList.size() != 0) {
            continue;
          }
          Log.e("ShortcutService", "Shortcut " + localList2.getId() + " has no intent. Skipping it.");
          paramList = (List<ShortcutInfo>)localObject3;
          continue;
        }
      }
      finally
      {
        localObject1 = localXmlResourceParser;
        if (localObject1 != null) {
          ((XmlResourceParser)localObject1).close();
        }
      }
      localArrayList.clear();
      localArrayList.add(new Intent("android.intent.action.VIEW"));
      if (j >= m)
      {
        Log.e("ShortcutService", "More than " + m + " shortcuts found for " + paramActivityInfo.getComponentName() + ". Skipping the rest.");
        if (localXmlResourceParser != null) {
          localXmlResourceParser.close();
        }
        return localList1;
      }
      ((Intent)localArrayList.get(0)).addFlags(268484608);
      try
      {
        localList2.setIntents((Intent[])localArrayList.toArray(new Intent[localArrayList.size()]));
        localArrayList.clear();
        localObject2 = localObject1;
        if (localObject1 != null)
        {
          localList2.setCategories((Set)localObject1);
          localObject2 = null;
        }
        if (localList1 != null) {
          continue;
        }
        paramList = new ArrayList();
        localObject1 = localXmlResourceParser;
        paramList.add(localList2);
        j += 1;
        i += 1;
        localObject1 = localXmlResourceParser;
        if (ShortcutService.DEBUG)
        {
          localObject1 = localXmlResourceParser;
          Slog.d("ShortcutService", "Shortcut added: " + localList2.toInsecureString());
        }
        localList1 = paramList;
        localObject1 = localObject2;
        paramList = (List<ShortcutInfo>)localObject3;
      }
      catch (RuntimeException paramList)
      {
        Log.e("ShortcutService", "Shortcut's extras contain un-persistable values. Skipping it.");
        paramList = (List<ShortcutInfo>)localObject3;
      }
      continue;
      paramList = localList2;
      if (k == 2) {
        if (n == 1)
        {
          paramList = localList2;
          if ("shortcuts".equals(localObject2)) {}
        }
        else if ((n == 2) && ("shortcut".equals(localObject2)))
        {
          localObject2 = parseShortcutAttributes(paramShortcutService, localAttributeSet, paramString, localComponentName, paramInt, i);
          paramList = localList2;
          if (localObject2 != null)
          {
            if (ShortcutService.DEBUG) {
              Slog.d("ShortcutService", "Shortcut found: " + ((ShortcutInfo)localObject2).toInsecureString());
            }
            if (localList1 == null) {
              continue;
            }
            k = localList1.size() - 1;
            if (k < 0) {
              continue;
            }
            if (!((ShortcutInfo)localObject2).getId().equals(((ShortcutInfo)localList1.get(k)).getId())) {
              continue;
            }
            Log.e("ShortcutService", "Duplicate shortcut ID detected. Skipping it.");
            paramList = localList2;
          }
        }
        else if ((n == 3) && ("intent".equals(localObject2)))
        {
          if ((localList2 != null) && (localList2.isEnabled()))
          {
            paramList = Intent.parseIntent(paramShortcutService.mContext.getResources(), localXmlResourceParser, localAttributeSet);
            if (TextUtils.isEmpty(paramList.getAction()))
            {
              Log.e("ShortcutService", "Shortcut intent action must be provided. activity=" + localComponentName);
              paramList = null;
            }
          }
          else
          {
            Log.e("ShortcutService", "Ignoring excessive intent tag.");
            paramList = localList2;
            continue;
          }
          localArrayList.add(paramList);
          paramList = localList2;
        }
        else if ((n == 3) && ("categories".equals(localObject2)))
        {
          paramList = localList2;
          if (localList2 != null)
          {
            paramList = localList2;
            if (localList2.getCategories() == null)
            {
              localObject2 = parseCategories(paramShortcutService, localAttributeSet);
              if (TextUtils.isEmpty((CharSequence)localObject2))
              {
                Log.e("ShortcutService", "Empty category found. activity=" + localComponentName);
                paramList = localList2;
              }
              else
              {
                paramList = (List<ShortcutInfo>)localObject1;
                if (localObject1 == null) {
                  paramList = new ArraySet();
                }
                paramList.add(localObject2);
                localObject1 = paramList;
                paramList = localList2;
              }
            }
          }
        }
        else
        {
          Log.w("ShortcutService", String.format("Invalid tag '%s' found at depth %d", new Object[] { localObject2, Integer.valueOf(n) }));
          paramList = localList2;
        }
      }
    }
    if (localXmlResourceParser != null) {
      localXmlResourceParser.close();
    }
    return localList1;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/ShortcutParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */