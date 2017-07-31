package com.android.server.pm;

import android.content.ComponentName;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.ArrayMap;
import android.util.Slog;
import com.android.internal.util.Preconditions;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.function.Consumer;
import libcore.util.Objects;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

class ShortcutUser
{
  private static final String ATTR_KNOWN_LOCALES = "locales";
  private static final String ATTR_LAST_APP_SCAN_OS_FINGERPRINT = "last-app-scan-fp";
  private static final String ATTR_LAST_APP_SCAN_TIME = "last-app-scan-time2";
  private static final String ATTR_VALUE = "value";
  private static final String KEY_LAUNCHERS = "launchers";
  private static final String KEY_PACKAGES = "packages";
  private static final String KEY_USER_ID = "userId";
  private static final String TAG = "ShortcutService";
  private static final String TAG_LAUNCHER = "launcher";
  static final String TAG_ROOT = "user";
  private ComponentName mCachedLauncher;
  private String mKnownLocales;
  private String mLastAppScanOsFingerprint;
  private long mLastAppScanTime;
  private ComponentName mLastKnownLauncher;
  private final ArrayMap<PackageWithUser, ShortcutLauncher> mLaunchers = new ArrayMap();
  private final ArrayMap<String, ShortcutPackage> mPackages = new ArrayMap();
  final ShortcutService mService;
  private final int mUserId;
  
  public ShortcutUser(ShortcutService paramShortcutService, int paramInt)
  {
    this.mService = paramShortcutService;
    this.mUserId = paramInt;
  }
  
  private void addLauncher(ShortcutLauncher paramShortcutLauncher)
  {
    paramShortcutLauncher.replaceUser(this);
    this.mLaunchers.put(PackageWithUser.of(paramShortcutLauncher.getPackageUserId(), paramShortcutLauncher.getPackageName()), paramShortcutLauncher);
  }
  
  private void addPackage(ShortcutPackage paramShortcutPackage)
  {
    paramShortcutPackage.replaceUser(this);
    this.mPackages.put(paramShortcutPackage.getPackageName(), paramShortcutPackage);
  }
  
  private void dumpDirectorySize(PrintWriter paramPrintWriter, String paramString, File paramFile)
  {
    int k = 0;
    int i = 0;
    long l1 = 0L;
    long l2 = l1;
    if (paramFile.listFiles() != null)
    {
      File[] arrayOfFile = paramFile.listFiles();
      int j = 0;
      int m = arrayOfFile.length;
      k = i;
      l2 = l1;
      if (j < m)
      {
        File localFile = arrayOfFile[j];
        if (localFile.isFile())
        {
          k = i + 1;
          l2 = l1 + localFile.length();
        }
        for (;;)
        {
          j += 1;
          i = k;
          l1 = l2;
          break;
          k = i;
          l2 = l1;
          if (localFile.isDirectory())
          {
            dumpDirectorySize(paramPrintWriter, paramString + "  ", localFile);
            k = i;
            l2 = l1;
          }
        }
      }
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("Path: ");
    paramPrintWriter.print(paramFile.getName());
    paramPrintWriter.print("/ has ");
    paramPrintWriter.print(k);
    paramPrintWriter.print(" files, size=");
    paramPrintWriter.print(l2);
    paramPrintWriter.print(" (");
    paramPrintWriter.print(Formatter.formatFileSize(this.mService.mContext, l2));
    paramPrintWriter.println(")");
  }
  
  private String getKnownLocales()
  {
    if (TextUtils.isEmpty(this.mKnownLocales))
    {
      this.mKnownLocales = this.mService.injectGetLocaleTagsForUser(this.mUserId);
      this.mService.scheduleSaveUser(this.mUserId);
    }
    return this.mKnownLocales;
  }
  
  public static ShortcutUser loadFromXml(ShortcutService paramShortcutService, XmlPullParser paramXmlPullParser, int paramInt, boolean paramBoolean)
    throws IOException, XmlPullParserException, ShortcutService.InvalidFileFormatException
  {
    ShortcutUser localShortcutUser = new ShortcutUser(paramShortcutService, paramInt);
    for (;;)
    {
      int j;
      Object localObject;
      try
      {
        localShortcutUser.mKnownLocales = ShortcutService.parseStringAttribute(paramXmlPullParser, "locales");
        long l = ShortcutService.parseLongAttribute(paramXmlPullParser, "last-app-scan-time2");
        if (l < paramShortcutService.injectCurrentTimeMillis())
        {
          localShortcutUser.mLastAppScanTime = l;
          localShortcutUser.mLastAppScanOsFingerprint = ShortcutService.parseStringAttribute(paramXmlPullParser, "last-app-scan-fp");
          int i = paramXmlPullParser.getDepth();
          j = paramXmlPullParser.next();
          if ((j == 1) || ((j == 3) && (paramXmlPullParser.getDepth() <= i))) {
            break;
          }
          if (j != 2) {
            continue;
          }
          j = paramXmlPullParser.getDepth();
          localObject = paramXmlPullParser.getName();
          if (j != i + 1) {
            break label238;
          }
          if (!((String)localObject).equals("launcher")) {
            break label171;
          }
          localShortcutUser.mLastKnownLauncher = ShortcutService.parseComponentNameAttribute(paramXmlPullParser, "value");
          continue;
        }
        l = 0L;
      }
      catch (RuntimeException paramShortcutService)
      {
        throw new ShortcutService.InvalidFileFormatException("Unable to parse file", paramShortcutService);
      }
      continue;
      label171:
      if (((String)localObject).equals("package"))
      {
        localObject = ShortcutPackage.loadFromXml(paramShortcutService, localShortcutUser, paramXmlPullParser, paramBoolean);
        localShortcutUser.mPackages.put(((ShortcutPackage)localObject).getPackageName(), localObject);
      }
      else if (((String)localObject).equals("launcher-pins"))
      {
        localShortcutUser.addLauncher(ShortcutLauncher.loadFromXml(paramXmlPullParser, localShortcutUser, paramInt, paramBoolean));
      }
      else
      {
        label238:
        ShortcutService.warnForInvalidTag(j, (String)localObject);
      }
    }
    return localShortcutUser;
  }
  
  private void saveShortcutPackageItem(XmlSerializer paramXmlSerializer, ShortcutPackageItem paramShortcutPackageItem, boolean paramBoolean)
    throws IOException, XmlPullParserException
  {
    if (paramBoolean)
    {
      if (!this.mService.shouldBackupApp(paramShortcutPackageItem.getPackageName(), paramShortcutPackageItem.getPackageUserId())) {
        return;
      }
      if (paramShortcutPackageItem.getPackageUserId() != paramShortcutPackageItem.getOwnerUserId()) {
        return;
      }
    }
    paramShortcutPackageItem.saveToXml(paramXmlSerializer, paramBoolean);
  }
  
  private void setLauncher(ComponentName paramComponentName, boolean paramBoolean)
  {
    this.mCachedLauncher = paramComponentName;
    if (Objects.equal(this.mLastKnownLauncher, paramComponentName)) {
      return;
    }
    if ((!paramBoolean) && (paramComponentName == null)) {
      return;
    }
    this.mLastKnownLauncher = paramComponentName;
    this.mService.scheduleSaveUser(this.mUserId);
  }
  
  public void attemptToRestoreIfNeededAndSave(ShortcutService paramShortcutService, String paramString, int paramInt)
  {
    forPackageItem(paramString, paramInt, new -void_attemptToRestoreIfNeededAndSave_com_android_server_pm_ShortcutService_s_java_lang_String_packageName_int_packageUserId_LambdaImpl0());
  }
  
  public void clearLauncher()
  {
    setLauncher(null);
  }
  
  public void detectLocaleChange()
  {
    String str = this.mService.injectGetLocaleTagsForUser(this.mUserId);
    if (getKnownLocales().equals(str)) {
      return;
    }
    if (ShortcutService.DEBUG) {
      Slog.d("ShortcutService", "Locale changed from " + str + " to " + this.mKnownLocales + " for user " + this.mUserId);
    }
    this.mKnownLocales = str;
    forAllPackages(new -void_detectLocaleChange__LambdaImpl0());
    this.mService.scheduleSaveUser(this.mUserId);
  }
  
  public void dump(PrintWriter paramPrintWriter, String paramString)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("User: ");
    paramPrintWriter.print(this.mUserId);
    paramPrintWriter.print("  Known locales: ");
    paramPrintWriter.print(this.mKnownLocales);
    paramPrintWriter.print("  Last app scan: [");
    paramPrintWriter.print(this.mLastAppScanTime);
    paramPrintWriter.print("] ");
    paramPrintWriter.print(ShortcutService.formatTime(this.mLastAppScanTime));
    paramPrintWriter.print("  Last app scan FP: ");
    paramPrintWriter.print(this.mLastAppScanOsFingerprint);
    paramPrintWriter.println();
    paramString = paramString + paramString + "  ";
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("Cached launcher: ");
    paramPrintWriter.print(this.mCachedLauncher);
    paramPrintWriter.println();
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("Last known launcher: ");
    paramPrintWriter.print(this.mLastKnownLauncher);
    paramPrintWriter.println();
    int i = 0;
    while (i < this.mLaunchers.size())
    {
      ((ShortcutLauncher)this.mLaunchers.valueAt(i)).dump(paramPrintWriter, paramString);
      i += 1;
    }
    i = 0;
    while (i < this.mPackages.size())
    {
      ((ShortcutPackage)this.mPackages.valueAt(i)).dump(paramPrintWriter, paramString);
      i += 1;
    }
    paramPrintWriter.println();
    paramPrintWriter.print(paramString);
    paramPrintWriter.println("Bitmap directories: ");
    dumpDirectorySize(paramPrintWriter, paramString + "  ", this.mService.getUserBitmapFilePath(this.mUserId));
  }
  
  public JSONObject dumpCheckin(boolean paramBoolean)
    throws JSONException
  {
    JSONObject localJSONObject = new JSONObject();
    localJSONObject.put("userId", this.mUserId);
    JSONArray localJSONArray = new JSONArray();
    int i = 0;
    while (i < this.mLaunchers.size())
    {
      localJSONArray.put(((ShortcutLauncher)this.mLaunchers.valueAt(i)).dumpCheckin(paramBoolean));
      i += 1;
    }
    localJSONObject.put("launchers", localJSONArray);
    localJSONArray = new JSONArray();
    i = 0;
    while (i < this.mPackages.size())
    {
      localJSONArray.put(((ShortcutPackage)this.mPackages.valueAt(i)).dumpCheckin(paramBoolean));
      i += 1;
    }
    localJSONObject.put("packages", localJSONArray);
    return localJSONObject;
  }
  
  public void forAllLaunchers(Consumer<? super ShortcutLauncher> paramConsumer)
  {
    int j = this.mLaunchers.size();
    int i = 0;
    while (i < j)
    {
      paramConsumer.accept(this.mLaunchers.valueAt(i));
      i += 1;
    }
  }
  
  public void forAllPackageItems(Consumer<? super ShortcutPackageItem> paramConsumer)
  {
    forAllLaunchers(paramConsumer);
    forAllPackages(paramConsumer);
  }
  
  public void forAllPackages(Consumer<? super ShortcutPackage> paramConsumer)
  {
    int j = this.mPackages.size();
    int i = 0;
    while (i < j)
    {
      paramConsumer.accept(this.mPackages.valueAt(i));
      i += 1;
    }
  }
  
  public void forPackageItem(String paramString, int paramInt, Consumer<ShortcutPackageItem> paramConsumer)
  {
    forAllPackageItems(new -void_forPackageItem_java_lang_String_packageName_int_packageUserId_java_util_function_Consumer_callback_LambdaImpl0(paramInt, paramString, paramConsumer));
  }
  
  public void forceClearLauncher()
  {
    setLauncher(null, true);
  }
  
  ArrayMap<PackageWithUser, ShortcutLauncher> getAllLaunchersForTest()
  {
    return this.mLaunchers;
  }
  
  ArrayMap<String, ShortcutPackage> getAllPackagesForTest()
  {
    return this.mPackages;
  }
  
  public ComponentName getCachedLauncher()
  {
    return this.mCachedLauncher;
  }
  
  public String getLastAppScanOsFingerprint()
  {
    return this.mLastAppScanOsFingerprint;
  }
  
  public long getLastAppScanTime()
  {
    return this.mLastAppScanTime;
  }
  
  public ComponentName getLastKnownLauncher()
  {
    return this.mLastKnownLauncher;
  }
  
  public ShortcutLauncher getLauncherShortcuts(String paramString, int paramInt)
  {
    PackageWithUser localPackageWithUser = PackageWithUser.of(paramInt, paramString);
    ShortcutLauncher localShortcutLauncher = (ShortcutLauncher)this.mLaunchers.get(localPackageWithUser);
    if (localShortcutLauncher == null)
    {
      paramString = new ShortcutLauncher(this, this.mUserId, paramString, paramInt);
      this.mLaunchers.put(localPackageWithUser, paramString);
      return paramString;
    }
    localShortcutLauncher.attemptToRestoreIfNeededAndSave();
    return localShortcutLauncher;
  }
  
  public ShortcutPackage getPackageShortcuts(String paramString)
  {
    ShortcutPackage localShortcutPackage2 = getPackageShortcutsIfExists(paramString);
    ShortcutPackage localShortcutPackage1 = localShortcutPackage2;
    if (localShortcutPackage2 == null)
    {
      localShortcutPackage1 = new ShortcutPackage(this, this.mUserId, paramString);
      this.mPackages.put(paramString, localShortcutPackage1);
    }
    return localShortcutPackage1;
  }
  
  public ShortcutPackage getPackageShortcutsIfExists(String paramString)
  {
    paramString = (ShortcutPackage)this.mPackages.get(paramString);
    if (paramString != null) {
      paramString.attemptToRestoreIfNeededAndSave();
    }
    return paramString;
  }
  
  public int getUserId()
  {
    return this.mUserId;
  }
  
  public boolean hasPackage(String paramString)
  {
    return this.mPackages.containsKey(paramString);
  }
  
  public void mergeRestoredFile(ShortcutUser paramShortcutUser)
  {
    ShortcutService localShortcutService = this.mService;
    this.mLaunchers.clear();
    paramShortcutUser.forAllLaunchers(new -void_mergeRestoredFile_com_android_server_pm_ShortcutUser_restored_LambdaImpl0(localShortcutService));
    paramShortcutUser.forAllPackages(new -void_mergeRestoredFile_com_android_server_pm_ShortcutUser_restored_LambdaImpl1(localShortcutService));
    paramShortcutUser.mLaunchers.clear();
    paramShortcutUser.mPackages.clear();
  }
  
  public void onCalledByPublisher(String paramString)
  {
    detectLocaleChange();
    rescanPackageIfNeeded(paramString, false);
  }
  
  public ShortcutLauncher removeLauncher(int paramInt, String paramString)
  {
    return (ShortcutLauncher)this.mLaunchers.remove(PackageWithUser.of(paramInt, paramString));
  }
  
  public ShortcutPackage removePackage(String paramString)
  {
    ShortcutPackage localShortcutPackage = (ShortcutPackage)this.mPackages.remove(paramString);
    this.mService.cleanupBitmapsForPackage(this.mUserId, paramString);
    return localShortcutPackage;
  }
  
  public void rescanPackageIfNeeded(String paramString, boolean paramBoolean)
  {
    if (this.mPackages.containsKey(paramString)) {}
    for (boolean bool = false;; bool = true)
    {
      if ((!getPackageShortcuts(paramString).rescanPackageIfNeeded(bool, paramBoolean)) && (bool)) {
        this.mPackages.remove(paramString);
      }
      return;
    }
  }
  
  public void resetThrottling()
  {
    int i = this.mPackages.size() - 1;
    while (i >= 0)
    {
      ((ShortcutPackage)this.mPackages.valueAt(i)).resetThrottling();
      i -= 1;
    }
  }
  
  public void saveToXml(XmlSerializer paramXmlSerializer, boolean paramBoolean)
    throws IOException, XmlPullParserException
  {
    paramXmlSerializer.startTag(null, "user");
    if (!paramBoolean)
    {
      ShortcutService.writeAttr(paramXmlSerializer, "locales", this.mKnownLocales);
      ShortcutService.writeAttr(paramXmlSerializer, "last-app-scan-time2", this.mLastAppScanTime);
      ShortcutService.writeAttr(paramXmlSerializer, "last-app-scan-fp", this.mLastAppScanOsFingerprint);
      ShortcutService.writeTagValue(paramXmlSerializer, "launcher", this.mLastKnownLauncher);
    }
    int j = this.mLaunchers.size();
    int i = 0;
    while (i < j)
    {
      saveShortcutPackageItem(paramXmlSerializer, (ShortcutPackageItem)this.mLaunchers.valueAt(i), paramBoolean);
      i += 1;
    }
    j = this.mPackages.size();
    i = 0;
    while (i < j)
    {
      saveShortcutPackageItem(paramXmlSerializer, (ShortcutPackageItem)this.mPackages.valueAt(i), paramBoolean);
      i += 1;
    }
    paramXmlSerializer.endTag(null, "user");
  }
  
  public void setLastAppScanOsFingerprint(String paramString)
  {
    this.mLastAppScanOsFingerprint = paramString;
  }
  
  public void setLastAppScanTime(long paramLong)
  {
    this.mLastAppScanTime = paramLong;
  }
  
  public void setLauncher(ComponentName paramComponentName)
  {
    setLauncher(paramComponentName, false);
  }
  
  static final class PackageWithUser
  {
    final String packageName;
    final int userId;
    
    private PackageWithUser(int paramInt, String paramString)
    {
      this.userId = paramInt;
      this.packageName = ((String)Preconditions.checkNotNull(paramString));
    }
    
    public static PackageWithUser of(int paramInt, String paramString)
    {
      return new PackageWithUser(paramInt, paramString);
    }
    
    public static PackageWithUser of(ShortcutPackageItem paramShortcutPackageItem)
    {
      return new PackageWithUser(paramShortcutPackageItem.getPackageUserId(), paramShortcutPackageItem.getPackageName());
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool = false;
      if (!(paramObject instanceof PackageWithUser)) {
        return false;
      }
      paramObject = (PackageWithUser)paramObject;
      if (this.userId == ((PackageWithUser)paramObject).userId) {
        bool = this.packageName.equals(((PackageWithUser)paramObject).packageName);
      }
      return bool;
    }
    
    public int hashCode()
    {
      return this.packageName.hashCode() ^ this.userId;
    }
    
    public String toString()
    {
      return String.format("[Package: %d, %s]", new Object[] { Integer.valueOf(this.userId), this.packageName });
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/ShortcutUser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */