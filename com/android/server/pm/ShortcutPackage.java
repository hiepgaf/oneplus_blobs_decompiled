package com.android.server.pm;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ShortcutInfo;
import android.content.res.Resources;
import android.os.PersistableBundle;
import android.text.format.Formatter;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.Slog;
import com.android.internal.util.Preconditions;
import com.android.internal.util.XmlUtils;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

class ShortcutPackage
  extends ShortcutPackageItem
{
  private static final String ATTR_ACTIVITY = "activity";
  private static final String ATTR_BITMAP_PATH = "bitmap-path";
  private static final String ATTR_CALL_COUNT = "call-count";
  private static final String ATTR_DISABLED_MESSAGE = "dmessage";
  private static final String ATTR_DISABLED_MESSAGE_RES_ID = "dmessageid";
  private static final String ATTR_DISABLED_MESSAGE_RES_NAME = "dmessagename";
  private static final String ATTR_FLAGS = "flags";
  private static final String ATTR_ICON_RES_ID = "icon-res";
  private static final String ATTR_ICON_RES_NAME = "icon-resname";
  private static final String ATTR_ID = "id";
  private static final String ATTR_INTENT_LEGACY = "intent";
  private static final String ATTR_INTENT_NO_EXTRA = "intent-base";
  private static final String ATTR_LAST_RESET = "last-reset";
  private static final String ATTR_NAME = "name";
  private static final String ATTR_NAME_XMLUTILS = "name";
  private static final String ATTR_RANK = "rank";
  private static final String ATTR_TEXT = "text";
  private static final String ATTR_TEXT_RES_ID = "textid";
  private static final String ATTR_TEXT_RES_NAME = "textname";
  private static final String ATTR_TIMESTAMP = "timestamp";
  private static final String ATTR_TITLE = "title";
  private static final String ATTR_TITLE_RES_ID = "titleid";
  private static final String ATTR_TITLE_RES_NAME = "titlename";
  private static final String KEY_BITMAPS = "bitmaps";
  private static final String KEY_BITMAP_BYTES = "bitmapBytes";
  private static final String KEY_DYNAMIC = "dynamic";
  private static final String KEY_MANIFEST = "manifest";
  private static final String KEY_PINNED = "pinned";
  private static final String NAME_CATEGORIES = "categories";
  private static final String TAG = "ShortcutService";
  private static final String TAG_CATEGORIES = "categories";
  private static final String TAG_EXTRAS = "extras";
  private static final String TAG_INTENT = "intent";
  private static final String TAG_INTENT_EXTRAS_LEGACY = "intent-extras";
  static final String TAG_ROOT = "package";
  private static final String TAG_SHORTCUT = "shortcut";
  private static final String TAG_STRING_ARRAY_XMLUTILS = "string-array";
  private static final String TAG_VERIFY = "ShortcutService.verify";
  private int mApiCallCount;
  private long mLastKnownForegroundElapsedTime;
  private long mLastResetTime;
  private final int mPackageUid;
  final Comparator<ShortcutInfo> mShortcutRankComparator;
  final Comparator<ShortcutInfo> mShortcutTypeAndRankComparator;
  private final ArrayMap<String, ShortcutInfo> mShortcuts;
  
  public ShortcutPackage(ShortcutUser paramShortcutUser, int paramInt, String paramString)
  {
    this(paramShortcutUser, paramInt, paramString, null);
  }
  
  private ShortcutPackage(ShortcutUser paramShortcutUser, int paramInt, String paramString, ShortcutPackageInfo paramShortcutPackageInfo) {}
  
  private void addShortcutInner(ShortcutInfo paramShortcutInfo)
  {
    ShortcutService localShortcutService = this.mShortcutUser.mService;
    deleteShortcutInner(paramShortcutInfo.getId());
    localShortcutService.saveIconAndFixUpShortcut(getPackageUserId(), paramShortcutInfo);
    localShortcutService.fixUpShortcutResourceNamesAndValues(paramShortcutInfo);
    this.mShortcuts.put(paramShortcutInfo.getId(), paramShortcutInfo);
  }
  
  private boolean areAllActivitiesStillEnabled()
  {
    if (this.mShortcuts.size() == 0) {
      return true;
    }
    ShortcutService localShortcutService = this.mShortcutUser.mService;
    ArrayList localArrayList = new ArrayList(4);
    int i = this.mShortcuts.size() - 1;
    if (i >= 0)
    {
      ComponentName localComponentName = ((ShortcutInfo)this.mShortcuts.valueAt(i)).getActivity();
      if (localArrayList.contains(localComponentName)) {}
      do
      {
        i -= 1;
        break;
        localArrayList.add(localComponentName);
      } while (localShortcutService.injectIsActivityEnabledAndExported(localComponentName, getOwnerUserId()));
      return false;
    }
    return true;
  }
  
  private ShortcutInfo deleteOrDisableWithId(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    ShortcutInfo localShortcutInfo = (ShortcutInfo)this.mShortcuts.get(paramString);
    if ((localShortcutInfo != null) && (localShortcutInfo.isEnabled()))
    {
      if (!paramBoolean2) {
        ensureNotImmutable(localShortcutInfo);
      }
      if (localShortcutInfo.isPinned())
      {
        localShortcutInfo.setRank(0);
        localShortcutInfo.clearFlags(33);
        if (paramBoolean1) {
          localShortcutInfo.addFlags(64);
        }
        localShortcutInfo.setTimestamp(this.mShortcutUser.mService.injectCurrentTimeMillis());
        return localShortcutInfo;
      }
    }
    else
    {
      return null;
    }
    deleteShortcutInner(paramString);
    return null;
  }
  
  private ShortcutInfo deleteShortcutInner(String paramString)
  {
    paramString = (ShortcutInfo)this.mShortcuts.remove(paramString);
    if (paramString != null)
    {
      this.mShortcutUser.mService.removeIcon(getPackageUserId(), paramString);
      paramString.clearFlags(35);
    }
    return paramString;
  }
  
  private boolean disableDynamicWithId(String paramString)
  {
    return deleteOrDisableWithId(paramString, true, false) == null;
  }
  
  private void ensureNotImmutable(ShortcutInfo paramShortcutInfo)
  {
    if ((paramShortcutInfo != null) && (paramShortcutInfo.isImmutable())) {
      throw new IllegalArgumentException("Manifest shortcut ID=" + paramShortcutInfo.getId() + " may not be manipulated via APIs");
    }
  }
  
  private void ensureNotImmutable(String paramString)
  {
    ensureNotImmutable((ShortcutInfo)this.mShortcuts.get(paramString));
  }
  
  private static String getFileName(String paramString)
  {
    int i = paramString.lastIndexOf(File.separatorChar);
    if (i == -1) {
      return paramString;
    }
    return paramString.substring(i + 1);
  }
  
  private void incrementCountForActivity(ArrayMap<ComponentName, Integer> paramArrayMap, ComponentName paramComponentName, int paramInt)
  {
    Integer localInteger2 = (Integer)paramArrayMap.get(paramComponentName);
    Integer localInteger1 = localInteger2;
    if (localInteger2 == null) {
      localInteger1 = Integer.valueOf(0);
    }
    paramArrayMap.put(paramComponentName, Integer.valueOf(localInteger1.intValue() + paramInt));
  }
  
  public static ShortcutPackage loadFromXml(ShortcutService paramShortcutService, ShortcutUser paramShortcutUser, XmlPullParser paramXmlPullParser, boolean paramBoolean)
    throws IOException, XmlPullParserException
  {
    paramShortcutService = ShortcutService.parseStringAttribute(paramXmlPullParser, "name");
    ShortcutPackage localShortcutPackage = new ShortcutPackage(paramShortcutUser, paramShortcutUser.getUserId(), paramShortcutService);
    localShortcutPackage.mApiCallCount = ShortcutService.parseIntAttribute(paramXmlPullParser, "call-count");
    localShortcutPackage.mLastResetTime = ShortcutService.parseLongAttribute(paramXmlPullParser, "last-reset");
    int i = paramXmlPullParser.getDepth();
    for (;;)
    {
      int j = paramXmlPullParser.next();
      if ((j == 1) || ((j == 3) && (paramXmlPullParser.getDepth() <= i))) {
        break;
      }
      if (j == 2)
      {
        j = paramXmlPullParser.getDepth();
        Object localObject = paramXmlPullParser.getName();
        if (j == i + 1)
        {
          if (((String)localObject).equals("package-info"))
          {
            localShortcutPackage.getPackageInfo().loadFromXml(paramXmlPullParser, paramBoolean);
            continue;
          }
          if (((String)localObject).equals("shortcut"))
          {
            localObject = parseShortcut(paramXmlPullParser, paramShortcutService, paramShortcutUser.getUserId());
            localShortcutPackage.mShortcuts.put(((ShortcutInfo)localObject).getId(), localObject);
            continue;
          }
        }
        ShortcutService.warnForInvalidTag(j, (String)localObject);
      }
    }
    return localShortcutPackage;
  }
  
  private static Intent parseIntent(XmlPullParser paramXmlPullParser)
    throws IOException, XmlPullParserException
  {
    Intent localIntent = ShortcutService.parseIntentAttribute(paramXmlPullParser, "intent-base");
    int i = paramXmlPullParser.getDepth();
    int k;
    String str;
    for (;;)
    {
      int j = paramXmlPullParser.next();
      if ((j == 1) || ((j == 3) && (paramXmlPullParser.getDepth() <= i))) {
        break label133;
      }
      if (j == 2)
      {
        k = paramXmlPullParser.getDepth();
        str = paramXmlPullParser.getName();
        if (ShortcutService.DEBUG_LOAD) {
          Slog.d("ShortcutService", String.format("  depth=%d type=%d name=%s", new Object[] { Integer.valueOf(k), Integer.valueOf(j), str }));
        }
        if (!str.equals("extras")) {
          break;
        }
        ShortcutInfo.setIntentExtras(localIntent, PersistableBundle.restoreFromXml(paramXmlPullParser));
      }
    }
    throw ShortcutService.throwForInvalidTag(k, str);
    label133:
    return localIntent;
  }
  
  private static ShortcutInfo parseShortcut(XmlPullParser paramXmlPullParser, String paramString, int paramInt)
    throws IOException, XmlPullParserException
  {
    PersistableBundle localPersistableBundle1 = null;
    ArrayList localArrayList = new ArrayList();
    PersistableBundle localPersistableBundle2 = null;
    Object localObject1 = null;
    String str1 = ShortcutService.parseStringAttribute(paramXmlPullParser, "id");
    ComponentName localComponentName = ShortcutService.parseComponentNameAttribute(paramXmlPullParser, "activity");
    String str2 = ShortcutService.parseStringAttribute(paramXmlPullParser, "title");
    int j = ShortcutService.parseIntAttribute(paramXmlPullParser, "titleid");
    String str3 = ShortcutService.parseStringAttribute(paramXmlPullParser, "titlename");
    String str4 = ShortcutService.parseStringAttribute(paramXmlPullParser, "text");
    int k = ShortcutService.parseIntAttribute(paramXmlPullParser, "textid");
    String str5 = ShortcutService.parseStringAttribute(paramXmlPullParser, "textname");
    String str6 = ShortcutService.parseStringAttribute(paramXmlPullParser, "dmessage");
    int m = ShortcutService.parseIntAttribute(paramXmlPullParser, "dmessageid");
    String str7 = ShortcutService.parseStringAttribute(paramXmlPullParser, "dmessagename");
    Intent localIntent = ShortcutService.parseIntentAttributeNoDefault(paramXmlPullParser, "intent");
    int n = (int)ShortcutService.parseLongAttribute(paramXmlPullParser, "rank");
    long l = ShortcutService.parseLongAttribute(paramXmlPullParser, "timestamp");
    int i1 = (int)ShortcutService.parseLongAttribute(paramXmlPullParser, "flags");
    int i2 = (int)ShortcutService.parseLongAttribute(paramXmlPullParser, "icon-res");
    String str8 = ShortcutService.parseStringAttribute(paramXmlPullParser, "icon-resname");
    String str9 = ShortcutService.parseStringAttribute(paramXmlPullParser, "bitmap-path");
    int i3 = paramXmlPullParser.getDepth();
    int i4;
    do
    {
      do
      {
        for (;;)
        {
          i = paramXmlPullParser.next();
          if ((i == 1) || ((i == 3) && (paramXmlPullParser.getDepth() <= i3))) {
            break label418;
          }
          if (i == 2)
          {
            i4 = paramXmlPullParser.getDepth();
            localObject2 = paramXmlPullParser.getName();
            if (ShortcutService.DEBUG_LOAD) {
              Slog.d("ShortcutService", String.format("  depth=%d type=%d name=%s", new Object[] { Integer.valueOf(i4), Integer.valueOf(i), localObject2 }));
            }
            if (((String)localObject2).equals("intent-extras"))
            {
              localPersistableBundle1 = PersistableBundle.restoreFromXml(paramXmlPullParser);
            }
            else if (((String)localObject2).equals("intent"))
            {
              localArrayList.add(parseIntent(paramXmlPullParser));
            }
            else
            {
              if (!((String)localObject2).equals("extras")) {
                break;
              }
              localPersistableBundle2 = PersistableBundle.restoreFromXml(paramXmlPullParser);
            }
          }
        }
      } while (((String)localObject2).equals("categories"));
      if (!((String)localObject2).equals("string-array")) {
        break;
      }
    } while (!"categories".equals(ShortcutService.parseStringAttribute(paramXmlPullParser, "name")));
    String[] arrayOfString = XmlUtils.readThisStringArrayXml(paramXmlPullParser, "string-array", null);
    Object localObject2 = new ArraySet(arrayOfString.length);
    int i = 0;
    for (;;)
    {
      localObject1 = localObject2;
      if (i >= arrayOfString.length) {
        break;
      }
      ((ArraySet)localObject2).add(arrayOfString[i]);
      i += 1;
    }
    throw ShortcutService.throwForInvalidTag(i4, (String)localObject2);
    label418:
    if (localIntent != null)
    {
      ShortcutInfo.setIntentExtras(localIntent, localPersistableBundle1);
      localArrayList.clear();
      localArrayList.add(localIntent);
    }
    return new ShortcutInfo(paramInt, str1, paramString, localComponentName, null, str2, j, str3, str4, k, str5, str6, m, str7, (Set)localObject1, (Intent[])localArrayList.toArray(new Intent[localArrayList.size()]), n, localPersistableBundle2, l, i1, i2, str8, str9);
  }
  
  private boolean publishManifestShortcuts(List<ShortcutInfo> paramList)
  {
    if (ShortcutService.DEBUG) {
      Slog.d("ShortcutService", String.format("Package %s: publishing manifest shortcuts", new Object[] { getPackageName() }));
    }
    boolean bool1 = false;
    boolean bool2 = false;
    Object localObject1 = null;
    int i = this.mShortcuts.size() - 1;
    Object localObject3;
    Object localObject2;
    while (i >= 0)
    {
      localObject3 = (ShortcutInfo)this.mShortcuts.valueAt(i);
      localObject2 = localObject1;
      if (((ShortcutInfo)localObject3).isManifestShortcut())
      {
        localObject2 = localObject1;
        if (localObject1 == null) {
          localObject2 = new ArraySet();
        }
        ((ArraySet)localObject2).add(((ShortcutInfo)localObject3).getId());
      }
      i -= 1;
      localObject1 = localObject2;
    }
    if (paramList != null)
    {
      int n = paramList.size();
      i = 0;
      bool1 = bool2;
      if (i < n)
      {
        bool1 = true;
        localObject2 = (ShortcutInfo)paramList.get(i);
        int j;
        label166:
        ShortcutInfo localShortcutInfo;
        int m;
        int k;
        if (((ShortcutInfo)localObject2).isEnabled())
        {
          j = 0;
          localObject3 = ((ShortcutInfo)localObject2).getId();
          localShortcutInfo = (ShortcutInfo)this.mShortcuts.get(localObject3);
          m = 0;
          k = m;
          if (localShortcutInfo == null) {
            break label276;
          }
          if (localShortcutInfo.isOriginallyFromManifest()) {
            break label255;
          }
          Slog.e("ShortcutService", "Shortcut with ID=" + ((ShortcutInfo)localObject2).getId() + " exists but is not from AndroidManifest.xml, not updating.");
        }
        for (;;)
        {
          i += 1;
          break;
          j = 1;
          break label166;
          label255:
          k = m;
          if (localShortcutInfo.isPinned())
          {
            k = 1;
            ((ShortcutInfo)localObject2).addFlags(2);
          }
          label276:
          if ((j == 0) || (k != 0))
          {
            addShortcutInner((ShortcutInfo)localObject2);
            if ((j == 0) && (localObject1 != null)) {
              ((ArraySet)localObject1).remove(localObject3);
            }
          }
        }
      }
    }
    bool2 = bool1;
    if (localObject1 != null)
    {
      if (ShortcutService.DEBUG) {
        Slog.d("ShortcutService", String.format("Package %s: disabling %d stale shortcuts", new Object[] { getPackageName(), Integer.valueOf(((ArraySet)localObject1).size()) }));
      }
      i = ((ArraySet)localObject1).size() - 1;
      while (i >= 0)
      {
        bool1 = true;
        disableWithId((String)((ArraySet)localObject1).valueAt(i), null, 0, true);
        i -= 1;
      }
      removeOrphans();
      bool2 = bool1;
    }
    adjustRanks();
    return bool2;
  }
  
  private boolean pushOutExcessShortcuts()
  {
    ShortcutService localShortcutService = this.mShortcutUser.mService;
    int k = localShortcutService.getMaxActivityShortcuts();
    ArrayMap localArrayMap = sortShortcutsToActivities();
    int i = localArrayMap.size() - 1;
    while (i >= 0)
    {
      ArrayList localArrayList = (ArrayList)localArrayMap.valueAt(i);
      if (localArrayList.size() <= k)
      {
        i -= 1;
      }
      else
      {
        Collections.sort(localArrayList, this.mShortcutTypeAndRankComparator);
        int j = localArrayList.size() - 1;
        label77:
        ShortcutInfo localShortcutInfo;
        if (j >= k)
        {
          localShortcutInfo = (ShortcutInfo)localArrayList.get(j);
          if (!localShortcutInfo.isManifestShortcut()) {
            break label116;
          }
          localShortcutService.wtf("Found manifest shortcuts in excess list.");
        }
        for (;;)
        {
          j -= 1;
          break label77;
          break;
          label116:
          deleteDynamicWithId(localShortcutInfo.getId());
        }
      }
    }
    return false;
  }
  
  private void removeOrphans()
  {
    Object localObject1 = null;
    int i = this.mShortcuts.size() - 1;
    if (i >= 0)
    {
      ShortcutInfo localShortcutInfo = (ShortcutInfo)this.mShortcuts.valueAt(i);
      if (localShortcutInfo.isAlive()) {}
      for (;;)
      {
        i -= 1;
        break;
        Object localObject2 = localObject1;
        if (localObject1 == null) {
          localObject2 = new ArrayList();
        }
        ((ArrayList)localObject2).add(localShortcutInfo.getId());
        localObject1 = localObject2;
      }
    }
    if (localObject1 != null)
    {
      i = ((ArrayList)localObject1).size() - 1;
      while (i >= 0)
      {
        deleteShortcutInner((String)((ArrayList)localObject1).get(i));
        i -= 1;
      }
    }
  }
  
  private static void saveShortcut(XmlSerializer paramXmlSerializer, ShortcutInfo paramShortcutInfo, boolean paramBoolean)
    throws IOException, XmlPullParserException
  {
    boolean bool = false;
    if (paramBoolean)
    {
      if (paramShortcutInfo.isPinned()) {
        bool = paramShortcutInfo.isEnabled();
      }
      if (!bool) {
        return;
      }
    }
    paramXmlSerializer.startTag(null, "shortcut");
    ShortcutService.writeAttr(paramXmlSerializer, "id", paramShortcutInfo.getId());
    ShortcutService.writeAttr(paramXmlSerializer, "activity", paramShortcutInfo.getActivity());
    ShortcutService.writeAttr(paramXmlSerializer, "title", paramShortcutInfo.getTitle());
    ShortcutService.writeAttr(paramXmlSerializer, "titleid", paramShortcutInfo.getTitleResId());
    ShortcutService.writeAttr(paramXmlSerializer, "titlename", paramShortcutInfo.getTitleResName());
    ShortcutService.writeAttr(paramXmlSerializer, "text", paramShortcutInfo.getText());
    ShortcutService.writeAttr(paramXmlSerializer, "textid", paramShortcutInfo.getTextResId());
    ShortcutService.writeAttr(paramXmlSerializer, "textname", paramShortcutInfo.getTextResName());
    ShortcutService.writeAttr(paramXmlSerializer, "dmessage", paramShortcutInfo.getDisabledMessage());
    ShortcutService.writeAttr(paramXmlSerializer, "dmessageid", paramShortcutInfo.getDisabledMessageResourceId());
    ShortcutService.writeAttr(paramXmlSerializer, "dmessagename", paramShortcutInfo.getDisabledMessageResName());
    ShortcutService.writeAttr(paramXmlSerializer, "timestamp", paramShortcutInfo.getLastChangedTimestamp());
    if (paramBoolean) {
      ShortcutService.writeAttr(paramXmlSerializer, "flags", paramShortcutInfo.getFlags() & 0xFFFFFFF2);
    }
    for (;;)
    {
      Object localObject = paramShortcutInfo.getCategories();
      if ((localObject != null) && (((Set)localObject).size() > 0))
      {
        paramXmlSerializer.startTag(null, "categories");
        XmlUtils.writeStringArrayXml((String[])((Set)localObject).toArray(new String[((Set)localObject).size()]), "categories", paramXmlSerializer);
        paramXmlSerializer.endTag(null, "categories");
      }
      localObject = paramShortcutInfo.getIntentsNoExtras();
      PersistableBundle[] arrayOfPersistableBundle = paramShortcutInfo.getIntentPersistableExtrases();
      int j = localObject.length;
      int i = 0;
      while (i < j)
      {
        paramXmlSerializer.startTag(null, "intent");
        ShortcutService.writeAttr(paramXmlSerializer, "intent-base", localObject[i]);
        ShortcutService.writeTagExtra(paramXmlSerializer, "extras", arrayOfPersistableBundle[i]);
        paramXmlSerializer.endTag(null, "intent");
        i += 1;
      }
      ShortcutService.writeAttr(paramXmlSerializer, "rank", paramShortcutInfo.getRank());
      ShortcutService.writeAttr(paramXmlSerializer, "flags", paramShortcutInfo.getFlags());
      ShortcutService.writeAttr(paramXmlSerializer, "icon-res", paramShortcutInfo.getIconResourceId());
      ShortcutService.writeAttr(paramXmlSerializer, "icon-resname", paramShortcutInfo.getIconResName());
      ShortcutService.writeAttr(paramXmlSerializer, "bitmap-path", paramShortcutInfo.getBitmapPath());
    }
    ShortcutService.writeTagExtra(paramXmlSerializer, "extras", paramShortcutInfo.getExtras());
    paramXmlSerializer.endTag(null, "shortcut");
  }
  
  private ArrayMap<ComponentName, ArrayList<ShortcutInfo>> sortShortcutsToActivities()
  {
    ArrayMap localArrayMap = new ArrayMap();
    int i = this.mShortcuts.size() - 1;
    if (i >= 0)
    {
      ShortcutInfo localShortcutInfo = (ShortcutInfo)this.mShortcuts.valueAt(i);
      if (localShortcutInfo.isFloating()) {}
      for (;;)
      {
        i -= 1;
        break;
        ComponentName localComponentName = localShortcutInfo.getActivity();
        ArrayList localArrayList2 = (ArrayList)localArrayMap.get(localComponentName);
        ArrayList localArrayList1 = localArrayList2;
        if (localArrayList2 == null)
        {
          localArrayList1 = new ArrayList();
          localArrayMap.put(localComponentName, localArrayList1);
        }
        localArrayList1.add(localShortcutInfo);
      }
    }
    return localArrayMap;
  }
  
  private boolean verifyRanksSequential(List<ShortcutInfo> paramList)
  {
    boolean bool = false;
    int i = 0;
    while (i < paramList.size())
    {
      ShortcutInfo localShortcutInfo = (ShortcutInfo)paramList.get(i);
      if (localShortcutInfo.getRank() != i)
      {
        bool = true;
        Log.e("ShortcutService.verify", "Package " + getPackageName() + ": shortcut " + localShortcutInfo.getId() + " rank=" + localShortcutInfo.getRank() + " but expected to be " + i);
      }
      i += 1;
    }
    return bool;
  }
  
  public void addOrUpdateDynamicShortcut(ShortcutInfo paramShortcutInfo)
  {
    Preconditions.checkArgument(paramShortcutInfo.isEnabled(), "add/setDynamicShortcuts() cannot publish disabled shortcuts");
    paramShortcutInfo.addFlags(1);
    ShortcutInfo localShortcutInfo = (ShortcutInfo)this.mShortcuts.get(paramShortcutInfo.getId());
    if (localShortcutInfo == null) {}
    for (boolean bool = false;; bool = localShortcutInfo.isPinned())
    {
      if (bool) {
        paramShortcutInfo.addFlags(2);
      }
      addShortcutInner(paramShortcutInfo);
      return;
      localShortcutInfo.ensureUpdatableWith(paramShortcutInfo);
    }
  }
  
  public void adjustRanks()
  {
    ShortcutService localShortcutService = this.mShortcutUser.mService;
    long l = localShortcutService.injectCurrentTimeMillis();
    int i = this.mShortcuts.size() - 1;
    while (i >= 0)
    {
      localObject = (ShortcutInfo)this.mShortcuts.valueAt(i);
      if ((((ShortcutInfo)localObject).isFloating()) && (((ShortcutInfo)localObject).getRank() != 0))
      {
        ((ShortcutInfo)localObject).setTimestamp(l);
        ((ShortcutInfo)localObject).setRank(0);
      }
      i -= 1;
    }
    Object localObject = sortShortcutsToActivities();
    int k = ((ArrayMap)localObject).size() - 1;
    while (k >= 0)
    {
      ArrayList localArrayList = (ArrayList)((ArrayMap)localObject).valueAt(k);
      Collections.sort(localArrayList, this.mShortcutRankComparator);
      int i1 = localArrayList.size();
      int m = 0;
      i = 0;
      if (m < i1)
      {
        ShortcutInfo localShortcutInfo = (ShortcutInfo)localArrayList.get(m);
        int j;
        if (localShortcutInfo.isManifestShortcut()) {
          j = i;
        }
        for (;;)
        {
          m += 1;
          i = j;
          break;
          if (!localShortcutInfo.isDynamic())
          {
            localShortcutService.wtf("Non-dynamic shortcut found.");
            j = i;
          }
          else
          {
            int n = i + 1;
            j = n;
            if (localShortcutInfo.getRank() != i)
            {
              localShortcutInfo.setTimestamp(l);
              localShortcutInfo.setRank(i);
              j = n;
            }
          }
        }
      }
      k -= 1;
    }
  }
  
  public void clearAllImplicitRanks()
  {
    int i = this.mShortcuts.size() - 1;
    while (i >= 0)
    {
      ((ShortcutInfo)this.mShortcuts.valueAt(i)).clearImplicitRankAndRankChangedFlag();
      i -= 1;
    }
  }
  
  public void deleteAllDynamicShortcuts()
  {
    long l = this.mShortcutUser.mService.injectCurrentTimeMillis();
    int j = 0;
    int i = this.mShortcuts.size() - 1;
    while (i >= 0)
    {
      ShortcutInfo localShortcutInfo = (ShortcutInfo)this.mShortcuts.valueAt(i);
      if (localShortcutInfo.isDynamic())
      {
        j = 1;
        localShortcutInfo.setTimestamp(l);
        localShortcutInfo.clearFlags(1);
        localShortcutInfo.setRank(0);
      }
      i -= 1;
    }
    if (j != 0) {
      removeOrphans();
    }
  }
  
  public boolean deleteDynamicWithId(String paramString)
  {
    boolean bool = false;
    if (deleteOrDisableWithId(paramString, false, false) == null) {
      bool = true;
    }
    return bool;
  }
  
  public void disableWithId(String paramString1, String paramString2, int paramInt, boolean paramBoolean)
  {
    paramString1 = deleteOrDisableWithId(paramString1, true, paramBoolean);
    if (paramString1 != null)
    {
      if (paramString2 == null) {
        break label23;
      }
      paramString1.setDisabledMessage(paramString2);
    }
    label23:
    while (paramInt == 0) {
      return;
    }
    paramString1.setDisabledMessageResId(paramInt);
    this.mShortcutUser.mService.fixUpShortcutResourceNamesAndValues(paramString1);
  }
  
  public void dump(PrintWriter paramPrintWriter, String paramString)
  {
    paramPrintWriter.println();
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("Package: ");
    paramPrintWriter.print(getPackageName());
    paramPrintWriter.print("  UID: ");
    paramPrintWriter.print(this.mPackageUid);
    paramPrintWriter.println();
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("  ");
    paramPrintWriter.print("Calls: ");
    paramPrintWriter.print(getApiCallCount());
    paramPrintWriter.println();
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("  ");
    paramPrintWriter.print("Last known FG: ");
    paramPrintWriter.print(this.mLastKnownForegroundElapsedTime);
    paramPrintWriter.println();
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("  ");
    paramPrintWriter.print("Last reset: [");
    paramPrintWriter.print(this.mLastResetTime);
    paramPrintWriter.print("] ");
    paramPrintWriter.print(ShortcutService.formatTime(this.mLastResetTime));
    paramPrintWriter.println();
    getPackageInfo().dump(paramPrintWriter, paramString + "  ");
    paramPrintWriter.println();
    paramPrintWriter.print(paramString);
    paramPrintWriter.println("  Shortcuts:");
    long l1 = 0L;
    ArrayMap localArrayMap = this.mShortcuts;
    int j = localArrayMap.size();
    int i = 0;
    while (i < j)
    {
      ShortcutInfo localShortcutInfo = (ShortcutInfo)localArrayMap.valueAt(i);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("    ");
      paramPrintWriter.println(localShortcutInfo.toInsecureString());
      long l2 = l1;
      if (localShortcutInfo.getBitmapPath() != null)
      {
        l2 = new File(localShortcutInfo.getBitmapPath()).length();
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("      ");
        paramPrintWriter.print("bitmap size=");
        paramPrintWriter.println(l2);
        l2 = l1 + l2;
      }
      i += 1;
      l1 = l2;
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("  ");
    paramPrintWriter.print("Total bitmap size: ");
    paramPrintWriter.print(l1);
    paramPrintWriter.print(" (");
    paramPrintWriter.print(Formatter.formatFileSize(this.mShortcutUser.mService.mContext, l1));
    paramPrintWriter.println(")");
  }
  
  public JSONObject dumpCheckin(boolean paramBoolean)
    throws JSONException
  {
    JSONObject localJSONObject = super.dumpCheckin(paramBoolean);
    int i3 = 0;
    int i = 0;
    int n = 0;
    int k = 0;
    long l1 = 0L;
    ArrayMap localArrayMap = this.mShortcuts;
    int i4 = localArrayMap.size();
    int j = 0;
    while (j < i4)
    {
      ShortcutInfo localShortcutInfo = (ShortcutInfo)localArrayMap.valueAt(j);
      int m = i3;
      if (localShortcutInfo.isDynamic()) {
        m = i3 + 1;
      }
      int i1 = n;
      if (localShortcutInfo.isDeclaredInManifest()) {
        i1 = n + 1;
      }
      int i2 = i;
      if (localShortcutInfo.isPinned()) {
        i2 = i + 1;
      }
      i = k;
      long l2 = l1;
      if (localShortcutInfo.getBitmapPath() != null)
      {
        i = k + 1;
        l2 = l1 + new File(localShortcutInfo.getBitmapPath()).length();
      }
      j += 1;
      k = i;
      i3 = m;
      n = i1;
      i = i2;
      l1 = l2;
    }
    localJSONObject.put("dynamic", i3);
    localJSONObject.put("manifest", n);
    localJSONObject.put("pinned", i);
    localJSONObject.put("bitmaps", k);
    localJSONObject.put("bitmapBytes", l1);
    return localJSONObject;
  }
  
  public void enableWithId(String paramString)
  {
    paramString = (ShortcutInfo)this.mShortcuts.get(paramString);
    if (paramString != null)
    {
      ensureNotImmutable(paramString);
      paramString.clearFlags(64);
    }
  }
  
  public void enforceShortcutCountsBeforeOperation(List<ShortcutInfo> paramList, int paramInt)
  {
    ShortcutService localShortcutService = this.mShortcutUser.mService;
    ArrayMap localArrayMap = new ArrayMap(4);
    int i = this.mShortcuts.size() - 1;
    Object localObject;
    if (i >= 0)
    {
      localObject = (ShortcutInfo)this.mShortcuts.valueAt(i);
      if (((ShortcutInfo)localObject).isManifestShortcut()) {
        incrementCountForActivity(localArrayMap, ((ShortcutInfo)localObject).getActivity(), 1);
      }
      for (;;)
      {
        i -= 1;
        break;
        if ((((ShortcutInfo)localObject).isDynamic()) && (paramInt != 0)) {
          incrementCountForActivity(localArrayMap, ((ShortcutInfo)localObject).getActivity(), 1);
        }
      }
    }
    i = paramList.size() - 1;
    if (i >= 0)
    {
      ShortcutInfo localShortcutInfo = (ShortcutInfo)paramList.get(i);
      localObject = localShortcutInfo.getActivity();
      if (localObject == null) {
        if (paramInt != 2) {
          localShortcutService.wtf("Activity must not be null at this point");
        }
      }
      for (;;)
      {
        i -= 1;
        break;
        localShortcutInfo = (ShortcutInfo)this.mShortcuts.get(localShortcutInfo.getId());
        if (localShortcutInfo == null)
        {
          if (paramInt != 2) {
            incrementCountForActivity(localArrayMap, (ComponentName)localObject, 1);
          }
        }
        else if ((!localShortcutInfo.isFloating()) || (paramInt != 2))
        {
          if (paramInt != 0)
          {
            ComponentName localComponentName = localShortcutInfo.getActivity();
            if (!localShortcutInfo.isFloating()) {
              incrementCountForActivity(localArrayMap, localComponentName, -1);
            }
          }
          incrementCountForActivity(localArrayMap, (ComponentName)localObject, 1);
        }
      }
    }
    paramInt = localArrayMap.size() - 1;
    while (paramInt >= 0)
    {
      localShortcutService.enforceMaxActivityShortcuts(((Integer)localArrayMap.valueAt(paramInt)).intValue());
      paramInt -= 1;
    }
  }
  
  public void ensureImmutableShortcutsNotIncluded(List<ShortcutInfo> paramList)
  {
    int i = paramList.size() - 1;
    while (i >= 0)
    {
      ensureNotImmutable(((ShortcutInfo)paramList.get(i)).getId());
      i -= 1;
    }
  }
  
  public void ensureImmutableShortcutsNotIncludedWithIds(List<String> paramList)
  {
    int i = paramList.size() - 1;
    while (i >= 0)
    {
      ensureNotImmutable((String)paramList.get(i));
      i -= 1;
    }
  }
  
  public void findAll(List<ShortcutInfo> paramList, Predicate<ShortcutInfo> paramPredicate, int paramInt)
  {
    findAll(paramList, paramPredicate, paramInt, null, 0);
  }
  
  public void findAll(List<ShortcutInfo> paramList, Predicate<ShortcutInfo> paramPredicate, int paramInt1, String paramString, int paramInt2)
  {
    if (getPackageInfo().isShadow()) {
      return;
    }
    Object localObject = this.mShortcutUser.mService;
    label31:
    ShortcutInfo localShortcutInfo;
    boolean bool;
    if (paramString == null)
    {
      localObject = null;
      paramInt2 = 0;
      if (paramInt2 >= this.mShortcuts.size()) {
        return;
      }
      localShortcutInfo = (ShortcutInfo)this.mShortcuts.valueAt(paramInt2);
      if (paramString == null) {
        break label130;
      }
      if (localObject == null) {
        break label136;
      }
      bool = ((ArraySet)localObject).contains(localShortcutInfo.getId());
      label79:
      if ((!localShortcutInfo.isFloating()) || (bool)) {
        break label142;
      }
    }
    for (;;)
    {
      paramInt2 += 1;
      break label31;
      localObject = ((ShortcutService)localObject).getLauncherShortcutsLocked(paramString, getPackageUserId(), paramInt2).getPinnedShortcutIds(getPackageName(), getPackageUserId());
      break;
      label130:
      bool = true;
      break label79;
      label136:
      bool = false;
      break label79;
      label142:
      localShortcutInfo = localShortcutInfo.clone(paramInt1);
      if (!bool) {
        localShortcutInfo.clearFlags(2);
      }
      if ((paramPredicate == null) || (paramPredicate.test(localShortcutInfo))) {
        paramList.add(localShortcutInfo);
      }
    }
  }
  
  public ShortcutInfo findShortcutById(String paramString)
  {
    return (ShortcutInfo)this.mShortcuts.get(paramString);
  }
  
  List<ShortcutInfo> getAllShortcutsForTest()
  {
    return new ArrayList(this.mShortcuts.values());
  }
  
  public int getApiCallCount()
  {
    ShortcutService localShortcutService = this.mShortcutUser.mService;
    if ((localShortcutService.isUidForegroundLocked(this.mPackageUid)) || (this.mLastKnownForegroundElapsedTime < localShortcutService.getUidLastForegroundElapsedTimeLocked(this.mPackageUid)))
    {
      this.mLastKnownForegroundElapsedTime = localShortcutService.injectElapsedRealtime();
      resetRateLimiting();
    }
    long l1 = localShortcutService.getLastResetTimeLocked();
    long l2 = localShortcutService.injectCurrentTimeMillis();
    if ((ShortcutService.isClockValid(l2)) && (this.mLastResetTime > l2))
    {
      Slog.w("ShortcutService", "Clock rewound");
      this.mLastResetTime = l2;
      this.mApiCallCount = 0;
      return this.mApiCallCount;
    }
    if (this.mLastResetTime < l1)
    {
      if (ShortcutService.DEBUG) {
        Slog.d("ShortcutService", String.format("%s: last reset=%d, now=%d, last=%d: resetting", new Object[] { getPackageName(), Long.valueOf(this.mLastResetTime), Long.valueOf(l2), Long.valueOf(l1) }));
      }
      this.mApiCallCount = 0;
      this.mLastResetTime = l1;
    }
    return this.mApiCallCount;
  }
  
  public int getOwnerUserId()
  {
    return getPackageUserId();
  }
  
  public Resources getPackageResources()
  {
    return this.mShortcutUser.mService.injectGetResourcesForApplicationAsUser(getPackageName(), getPackageUserId());
  }
  
  public int getPackageUid()
  {
    return this.mPackageUid;
  }
  
  public ArraySet<String> getUsedBitmapFiles()
  {
    ArraySet localArraySet = new ArraySet(this.mShortcuts.size());
    int i = this.mShortcuts.size() - 1;
    while (i >= 0)
    {
      ShortcutInfo localShortcutInfo = (ShortcutInfo)this.mShortcuts.valueAt(i);
      if (localShortcutInfo.getBitmapPath() != null) {
        localArraySet.add(getFileName(localShortcutInfo.getBitmapPath()));
      }
      i -= 1;
    }
    return localArraySet;
  }
  
  public boolean hasNonManifestShortcuts()
  {
    int i = this.mShortcuts.size() - 1;
    while (i >= 0)
    {
      if (!((ShortcutInfo)this.mShortcuts.valueAt(i)).isDeclaredInManifest()) {
        return true;
      }
      i -= 1;
    }
    return false;
  }
  
  protected void onRestoreBlocked()
  {
    this.mShortcuts.clear();
  }
  
  protected void onRestored()
  {
    refreshPinnedFlags();
  }
  
  public void refreshPinnedFlags()
  {
    int i = this.mShortcuts.size() - 1;
    while (i >= 0)
    {
      ((ShortcutInfo)this.mShortcuts.valueAt(i)).clearFlags(2);
      i -= 1;
    }
    this.mShortcutUser.mService.getUserShortcutsLocked(getPackageUserId()).forAllLaunchers(new -void_refreshPinnedFlags__LambdaImpl0());
    removeOrphans();
  }
  
  public boolean rescanPackageIfNeeded(boolean paramBoolean1, boolean paramBoolean2)
  {
    ShortcutService localShortcutService = this.mShortcutUser.mService;
    long l = localShortcutService.injectElapsedRealtime();
    Object localObject3;
    Object localObject4;
    label138:
    int i;
    label145:
    do
    {
      do
      {
        try
        {
          localObject3 = this.mShortcutUser.mService.getPackageInfo(getPackageName(), getPackageUserId());
          if (localObject3 == null) {
            return false;
          }
          bool2 = ((PackageInfo)localObject3).applicationInfo.isSystemApp();
          bool1 = "com.android.settings".equals(getPackageName());
          if ((paramBoolean1) || (paramBoolean2))
          {
            if ((bool1) && (ShortcutService.DEBUG)) {
              Slog.d("ShortcutService", "Always scan settings.");
            }
            localShortcutService.logDurationStat(14, l);
            localObject4 = null;
          }
        }
        finally
        {
          boolean bool2;
          boolean bool1;
          List localList;
          localShortcutService.logDurationStat(14, l);
        }
        try
        {
          localList = ShortcutParser.parseShortcuts(this.mShortcutUser.mService, getPackageName(), getPackageUserId());
          localObject4 = localList;
        }
        catch (IOException|XmlPullParserException localIOException)
        {
          Slog.e("ShortcutService", "Failed to load shortcuts from AndroidManifest.xml.", localIOException);
          break label138;
          i = ((List)localObject4).size();
          break label145;
          if (!ShortcutService.DEBUG) {
            break label364;
          }
          Object localObject5 = getPackageName();
          if (!paramBoolean1) {
            break label506;
          }
          Object localObject2 = "added";
          Slog.d("ShortcutService", String.format("Package %s %s, version %d -> %d", new Object[] { localObject5, localObject2, Integer.valueOf(getPackageInfo().getVersionCode()), Integer.valueOf(((PackageInfo)localObject3).versionCode) }));
          getPackageInfo().updateVersionInfo((PackageInfo)localObject3);
          int j = 0;
          i = 0;
          if (paramBoolean1) {
            break label568;
          }
          localObject2 = null;
          int k = this.mShortcuts.size() - 1;
          j = i;
          if (k < 0) {
            break label568;
          }
          localObject5 = (ShortcutInfo)this.mShortcuts.valueAt(k);
          j = i;
          if (!((ShortcutInfo)localObject5).isDynamic()) {
            break label517;
          }
          if (localShortcutService.injectIsMainActivity(((ShortcutInfo)localObject5).getActivity(), getPackageUserId())) {
            break label514;
          }
          Slog.w("ShortcutService", String.format("%s is no longer main activity. Disabling shorcut %s.", new Object[] { getPackageName(), ((ShortcutInfo)localObject5).getId() }));
          if (!disableDynamicWithId(((ShortcutInfo)localObject5).getId())) {
            break label514;
          }
          localObject3 = localObject2;
          for (;;)
          {
            k -= 1;
            localObject2 = localObject3;
            break label396;
            localObject2 = "updated";
            break;
            j = 1;
            i = j;
            localObject3 = localObject2;
            if (((ShortcutInfo)localObject5).hasAnyResources())
            {
              localObject3 = localObject2;
              if (!((ShortcutInfo)localObject5).isOriginallyFromManifest())
              {
                localObject3 = localObject2;
                if (localObject2 == null)
                {
                  localObject2 = getPackageResources();
                  localObject3 = localObject2;
                  if (localObject2 == null)
                  {
                    paramBoolean2 = j | publishManifestShortcuts((List)localObject4);
                    paramBoolean1 = paramBoolean2;
                    if (localObject4 != null) {
                      paramBoolean1 = paramBoolean2 | pushOutExcessShortcuts();
                    }
                    localShortcutService.verifyStates();
                    if (!paramBoolean1) {
                      break label638;
                    }
                    localShortcutService.packageShortcutsChanged(getPackageName(), getPackageUserId());
                    return paramBoolean1;
                  }
                }
                ((ShortcutInfo)localObject5).lookupAndFillInResourceIds((Resources)localObject3);
              }
              i = 1;
              ((ShortcutInfo)localObject5).setTimestamp(localShortcutService.injectCurrentTimeMillis());
            }
          }
          localShortcutService.scheduleSaveUser(getPackageUserId());
        }
        if (localObject4 != null) {
          break;
        }
        i = 0;
        if (ShortcutService.DEBUG) {
          Slog.d("ShortcutService", String.format("Package %s has %d manifest shortcut(s)", new Object[] { getPackageName(), Integer.valueOf(i) }));
        }
        if ((!paramBoolean1) || (i != 0)) {
          break label293;
        }
        return false;
      } while ((bool1) || (bool2) || (getPackageInfo().getVersionCode() != ((PackageInfo)localObject3).versionCode) || (getPackageInfo().getLastUpdateTime() != ((PackageInfo)localObject3).lastUpdateTime));
      paramBoolean2 = areAllActivitiesStillEnabled();
    } while (!paramBoolean2);
    localShortcutService.logDurationStat(14, l);
    return false;
    label293:
    label364:
    label396:
    label506:
    label514:
    label517:
    label568:
    label638:
    return paramBoolean1;
  }
  
  public void resetRateLimiting()
  {
    if (ShortcutService.DEBUG) {
      Slog.d("ShortcutService", "resetRateLimiting: " + getPackageName());
    }
    if (this.mApiCallCount > 0)
    {
      this.mApiCallCount = 0;
      this.mShortcutUser.mService.scheduleSaveUser(getOwnerUserId());
    }
  }
  
  public void resetRateLimitingForCommandLineNoSaving()
  {
    this.mApiCallCount = 0;
    this.mLastResetTime = 0L;
  }
  
  public void resetThrottling()
  {
    this.mApiCallCount = 0;
  }
  
  public void resolveResourceStrings()
  {
    ShortcutService localShortcutService = this.mShortcutUser.mService;
    int i = 0;
    Object localObject1 = null;
    int j = this.mShortcuts.size() - 1;
    for (;;)
    {
      int k = i;
      ShortcutInfo localShortcutInfo;
      Object localObject2;
      if (j >= 0)
      {
        localShortcutInfo = (ShortcutInfo)this.mShortcuts.valueAt(j);
        localObject2 = localObject1;
        if (!localShortcutInfo.hasStringResources()) {
          break label118;
        }
        i = 1;
        k = 1;
        localObject2 = localObject1;
        if (localObject1 == null)
        {
          localObject1 = getPackageResources();
          localObject2 = localObject1;
          if (localObject1 != null) {}
        }
      }
      else
      {
        if (k != 0) {
          localShortcutService.packageShortcutsChanged(getPackageName(), getPackageUserId());
        }
        return;
      }
      localShortcutInfo.resolveResourceStrings((Resources)localObject2);
      localShortcutInfo.setTimestamp(localShortcutService.injectCurrentTimeMillis());
      label118:
      j -= 1;
      localObject1 = localObject2;
    }
  }
  
  public void saveToXml(XmlSerializer paramXmlSerializer, boolean paramBoolean)
    throws IOException, XmlPullParserException
  {
    int j = this.mShortcuts.size();
    if ((j == 0) && (this.mApiCallCount == 0)) {
      return;
    }
    paramXmlSerializer.startTag(null, "package");
    ShortcutService.writeAttr(paramXmlSerializer, "name", getPackageName());
    ShortcutService.writeAttr(paramXmlSerializer, "call-count", this.mApiCallCount);
    ShortcutService.writeAttr(paramXmlSerializer, "last-reset", this.mLastResetTime);
    getPackageInfo().saveToXml(paramXmlSerializer);
    int i = 0;
    while (i < j)
    {
      saveShortcut(paramXmlSerializer, (ShortcutInfo)this.mShortcuts.valueAt(i), paramBoolean);
      i += 1;
    }
    paramXmlSerializer.endTag(null, "package");
  }
  
  public boolean tryApiCall()
  {
    ShortcutService localShortcutService = this.mShortcutUser.mService;
    if (getApiCallCount() >= localShortcutService.mMaxUpdatesPerInterval) {
      return false;
    }
    this.mApiCallCount += 1;
    localShortcutService.scheduleSaveUser(getOwnerUserId());
    return true;
  }
  
  public void verifyStates()
  {
    super.verifyStates();
    int i = 0;
    Object localObject = sortShortcutsToActivities();
    int j = ((ArrayMap)localObject).size() - 1;
    while (j >= 0)
    {
      ArrayList localArrayList2 = (ArrayList)((ArrayMap)localObject).valueAt(j);
      if (localArrayList2.size() > this.mShortcutUser.mService.getMaxActivityShortcuts())
      {
        i = 1;
        Log.e("ShortcutService.verify", "Package " + getPackageName() + ": activity " + ((ArrayMap)localObject).keyAt(j) + " has " + ((ArrayList)((ArrayMap)localObject).valueAt(j)).size() + " shortcuts.");
      }
      Collections.sort(localArrayList2, new -void_verifyStates__LambdaImpl0());
      ArrayList localArrayList1 = new ArrayList(localArrayList2);
      localArrayList1.removeIf(new -void_verifyStates__LambdaImpl1());
      localArrayList2 = new ArrayList(localArrayList2);
      localArrayList1.removeIf(new -void_verifyStates__LambdaImpl2());
      verifyRanksSequential(localArrayList1);
      verifyRanksSequential(localArrayList2);
      j -= 1;
    }
    int k = this.mShortcuts.size() - 1;
    if (k >= 0)
    {
      localObject = (ShortcutInfo)this.mShortcuts.valueAt(k);
      boolean bool;
      if ((!((ShortcutInfo)localObject).isDeclaredInManifest()) && (!((ShortcutInfo)localObject).isDynamic()))
      {
        bool = ((ShortcutInfo)localObject).isPinned();
        label257:
        if (!bool)
        {
          i = 1;
          Log.e("ShortcutService.verify", "Package " + getPackageName() + ": shortcut " + ((ShortcutInfo)localObject).getId() + " is not manifest, dynamic or pinned.");
        }
        j = i;
        if (((ShortcutInfo)localObject).isDeclaredInManifest())
        {
          j = i;
          if (((ShortcutInfo)localObject).isDynamic())
          {
            j = 1;
            Log.e("ShortcutService.verify", "Package " + getPackageName() + ": shortcut " + ((ShortcutInfo)localObject).getId() + " is both dynamic and manifest at the same time.");
          }
        }
        if (((ShortcutInfo)localObject).getActivity() == null)
        {
          j = 1;
          Log.e("ShortcutService.verify", "Package " + getPackageName() + ": shortcut " + ((ShortcutInfo)localObject).getId() + " has null activity.");
        }
        if (!((ShortcutInfo)localObject).isDynamic())
        {
          i = j;
          if (!((ShortcutInfo)localObject).isManifestShortcut()) {}
        }
        else
        {
          if (!((ShortcutInfo)localObject).isEnabled()) {
            break label693;
          }
          i = j;
        }
      }
      for (;;)
      {
        j = i;
        if (((ShortcutInfo)localObject).isFloating())
        {
          j = i;
          if (((ShortcutInfo)localObject).getRank() != 0)
          {
            j = 1;
            Log.e("ShortcutService.verify", "Package " + getPackageName() + ": shortcut " + ((ShortcutInfo)localObject).getId() + " is floating, but has rank=" + ((ShortcutInfo)localObject).getRank());
          }
        }
        if (((ShortcutInfo)localObject).getIcon() != null)
        {
          j = 1;
          Log.e("ShortcutService.verify", "Package " + getPackageName() + ": shortcut " + ((ShortcutInfo)localObject).getId() + " still has an icon");
        }
        i = j;
        if (((ShortcutInfo)localObject).hasIconFile())
        {
          i = j;
          if (((ShortcutInfo)localObject).hasIconResource())
          {
            i = 1;
            Log.e("ShortcutService.verify", "Package " + getPackageName() + ": shortcut " + ((ShortcutInfo)localObject).getId() + " has both resource and bitmap icons");
          }
        }
        k -= 1;
        break;
        bool = true;
        break label257;
        label693:
        i = 1;
        Log.e("ShortcutService.verify", "Package " + getPackageName() + ": shortcut " + ((ShortcutInfo)localObject).getId() + " is not floating, but is disabled.");
      }
    }
    if (i != 0) {
      throw new IllegalStateException("See logcat for errors");
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/ShortcutPackage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */