package com.android.server.pm;

import android.content.pm.PackageInfo;
import android.content.pm.ShortcutInfo;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Slog;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

class ShortcutLauncher
  extends ShortcutPackageItem
{
  private static final String ATTR_LAUNCHER_USER_ID = "launcher-user";
  private static final String ATTR_PACKAGE_NAME = "package-name";
  private static final String ATTR_PACKAGE_USER_ID = "package-user";
  private static final String ATTR_VALUE = "value";
  private static final String TAG = "ShortcutService";
  private static final String TAG_PACKAGE = "package";
  private static final String TAG_PIN = "pin";
  static final String TAG_ROOT = "launcher-pins";
  private final int mOwnerUserId;
  private final ArrayMap<ShortcutUser.PackageWithUser, ArraySet<String>> mPinnedShortcuts;
  
  public ShortcutLauncher(ShortcutUser paramShortcutUser, int paramInt1, String paramString, int paramInt2)
  {
    this(paramShortcutUser, paramInt1, paramString, paramInt2, null);
  }
  
  private ShortcutLauncher(ShortcutUser paramShortcutUser, int paramInt1, String paramString, int paramInt2, ShortcutPackageInfo paramShortcutPackageInfo) {}
  
  public static ShortcutLauncher loadFromXml(XmlPullParser paramXmlPullParser, ShortcutUser paramShortcutUser, int paramInt, boolean paramBoolean)
    throws IOException, XmlPullParserException
  {
    Object localObject = ShortcutService.parseStringAttribute(paramXmlPullParser, "package-name");
    int i;
    int j;
    if (paramBoolean)
    {
      i = paramInt;
      localObject = new ShortcutLauncher(paramShortcutUser, paramInt, (String)localObject, i);
      paramShortcutUser = null;
      j = paramXmlPullParser.getDepth();
    }
    for (;;)
    {
      i = paramXmlPullParser.next();
      if ((i == 1) || ((i == 3) && (paramXmlPullParser.getDepth() <= j))) {
        break label260;
      }
      if (i == 2)
      {
        i = paramXmlPullParser.getDepth();
        String str = paramXmlPullParser.getName();
        if (i == j + 1)
        {
          if (str.equals("package-info"))
          {
            ((ShortcutLauncher)localObject).getPackageInfo().loadFromXml(paramXmlPullParser, paramBoolean);
            continue;
            i = ShortcutService.parseIntAttribute(paramXmlPullParser, "launcher-user", paramInt);
            break;
          }
          if (str.equals("package"))
          {
            str = ShortcutService.parseStringAttribute(paramXmlPullParser, "package-name");
            if (paramBoolean) {}
            for (i = paramInt;; i = ShortcutService.parseIntAttribute(paramXmlPullParser, "package-user", paramInt))
            {
              paramShortcutUser = new ArraySet();
              ((ShortcutLauncher)localObject).mPinnedShortcuts.put(ShortcutUser.PackageWithUser.of(i, str), paramShortcutUser);
              break;
            }
          }
        }
        if ((i == j + 2) && (str.equals("pin")))
        {
          if (paramShortcutUser == null) {
            Slog.w("ShortcutService", "pin in invalid place");
          } else {
            paramShortcutUser.add(ShortcutService.parseStringAttribute(paramXmlPullParser, "value"));
          }
        }
        else {
          ShortcutService.warnForInvalidTag(i, str);
        }
      }
    }
    label260:
    return (ShortcutLauncher)localObject;
  }
  
  boolean cleanUpPackage(String paramString, int paramInt)
  {
    return this.mPinnedShortcuts.remove(ShortcutUser.PackageWithUser.of(paramInt, paramString)) != null;
  }
  
  public void dump(PrintWriter paramPrintWriter, String paramString)
  {
    paramPrintWriter.println();
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("Launcher: ");
    paramPrintWriter.print(getPackageName());
    paramPrintWriter.print("  Package user: ");
    paramPrintWriter.print(getPackageUserId());
    paramPrintWriter.print("  Owner user: ");
    paramPrintWriter.print(getOwnerUserId());
    paramPrintWriter.println();
    getPackageInfo().dump(paramPrintWriter, paramString + "  ");
    paramPrintWriter.println();
    int k = this.mPinnedShortcuts.size();
    int i = 0;
    while (i < k)
    {
      paramPrintWriter.println();
      Object localObject = (ShortcutUser.PackageWithUser)this.mPinnedShortcuts.keyAt(i);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("  ");
      paramPrintWriter.print("Package: ");
      paramPrintWriter.print(((ShortcutUser.PackageWithUser)localObject).packageName);
      paramPrintWriter.print("  User: ");
      paramPrintWriter.println(((ShortcutUser.PackageWithUser)localObject).userId);
      localObject = (ArraySet)this.mPinnedShortcuts.valueAt(i);
      int m = ((ArraySet)localObject).size();
      int j = 0;
      while (j < m)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("    Pinned: ");
        paramPrintWriter.print((String)((ArraySet)localObject).valueAt(j));
        paramPrintWriter.println();
        j += 1;
      }
      i += 1;
    }
  }
  
  public JSONObject dumpCheckin(boolean paramBoolean)
    throws JSONException
  {
    return super.dumpCheckin(paramBoolean);
  }
  
  public void ensureVersionInfo()
  {
    PackageInfo localPackageInfo = this.mShortcutUser.mService.getPackageInfoWithSignatures(getPackageName(), getPackageUserId());
    if (localPackageInfo == null)
    {
      Slog.w("ShortcutService", "Package not found: " + getPackageName());
      return;
    }
    getPackageInfo().updateVersionInfo(localPackageInfo);
  }
  
  ArraySet<String> getAllPinnedShortcutsForTest(String paramString, int paramInt)
  {
    return new ArraySet((ArraySet)this.mPinnedShortcuts.get(ShortcutUser.PackageWithUser.of(paramInt, paramString)));
  }
  
  public int getOwnerUserId()
  {
    return this.mOwnerUserId;
  }
  
  public ArraySet<String> getPinnedShortcutIds(String paramString, int paramInt)
  {
    return (ArraySet)this.mPinnedShortcuts.get(ShortcutUser.PackageWithUser.of(paramInt, paramString));
  }
  
  protected void onRestoreBlocked()
  {
    ArrayList localArrayList = new ArrayList(this.mPinnedShortcuts.keySet());
    this.mPinnedShortcuts.clear();
    int i = localArrayList.size() - 1;
    while (i >= 0)
    {
      Object localObject = (ShortcutUser.PackageWithUser)localArrayList.get(i);
      localObject = this.mShortcutUser.getPackageShortcutsIfExists(((ShortcutUser.PackageWithUser)localObject).packageName);
      if (localObject != null) {
        ((ShortcutPackage)localObject).refreshPinnedFlags();
      }
      i -= 1;
    }
  }
  
  protected void onRestored() {}
  
  public void pinShortcuts(int paramInt, String paramString, List<String> paramList)
  {
    ShortcutPackage localShortcutPackage = this.mShortcutUser.getPackageShortcutsIfExists(paramString);
    if (localShortcutPackage == null) {
      return;
    }
    paramString = ShortcutUser.PackageWithUser.of(paramInt, paramString);
    int i = paramList.size();
    if (i == 0) {
      this.mPinnedShortcuts.remove(paramString);
    }
    for (;;)
    {
      localShortcutPackage.refreshPinnedFlags();
      return;
      ArraySet localArraySet1 = (ArraySet)this.mPinnedShortcuts.get(paramString);
      ArraySet localArraySet2 = new ArraySet();
      paramInt = 0;
      if (paramInt < i)
      {
        String str = (String)paramList.get(paramInt);
        ShortcutInfo localShortcutInfo = localShortcutPackage.findShortcutById(str);
        if (localShortcutInfo == null) {}
        for (;;)
        {
          paramInt += 1;
          break;
          if ((localShortcutInfo.isDynamic()) || (localShortcutInfo.isManifestShortcut()) || ((localArraySet1 != null) && (localArraySet1.contains(str)))) {
            localArraySet2.add(str);
          }
        }
      }
      this.mPinnedShortcuts.put(paramString, localArraySet2);
    }
  }
  
  public void saveToXml(XmlSerializer paramXmlSerializer, boolean paramBoolean)
    throws IOException
  {
    int k = this.mPinnedShortcuts.size();
    if (k == 0) {
      return;
    }
    paramXmlSerializer.startTag(null, "launcher-pins");
    ShortcutService.writeAttr(paramXmlSerializer, "package-name", getPackageName());
    ShortcutService.writeAttr(paramXmlSerializer, "launcher-user", getPackageUserId());
    getPackageInfo().saveToXml(paramXmlSerializer);
    int i = 0;
    if (i < k)
    {
      Object localObject = (ShortcutUser.PackageWithUser)this.mPinnedShortcuts.keyAt(i);
      if ((paramBoolean) && (((ShortcutUser.PackageWithUser)localObject).userId != getOwnerUserId())) {}
      for (;;)
      {
        i += 1;
        break;
        paramXmlSerializer.startTag(null, "package");
        ShortcutService.writeAttr(paramXmlSerializer, "package-name", ((ShortcutUser.PackageWithUser)localObject).packageName);
        ShortcutService.writeAttr(paramXmlSerializer, "package-user", ((ShortcutUser.PackageWithUser)localObject).userId);
        localObject = (ArraySet)this.mPinnedShortcuts.valueAt(i);
        int m = ((ArraySet)localObject).size();
        int j = 0;
        while (j < m)
        {
          ShortcutService.writeTagValue(paramXmlSerializer, "pin", (String)((ArraySet)localObject).valueAt(j));
          j += 1;
        }
        paramXmlSerializer.endTag(null, "package");
      }
    }
    paramXmlSerializer.endTag(null, "launcher-pins");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/ShortcutLauncher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */