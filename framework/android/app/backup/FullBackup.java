package android.app.backup;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.system.ErrnoException;
import android.system.Os;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class FullBackup
{
  public static final String APK_TREE_TOKEN = "a";
  public static final String APPS_PREFIX = "apps/";
  public static final String CACHE_TREE_TOKEN = "c";
  public static final String CONF_TOKEN_INTENT_EXTRA = "conftoken";
  public static final String DATABASE_TREE_TOKEN = "db";
  public static final String DEVICE_CACHE_TREE_TOKEN = "d_c";
  public static final String DEVICE_DATABASE_TREE_TOKEN = "d_db";
  public static final String DEVICE_FILES_TREE_TOKEN = "d_f";
  public static final String DEVICE_NO_BACKUP_TREE_TOKEN = "d_nb";
  public static final String DEVICE_ROOT_TREE_TOKEN = "d_r";
  public static final String DEVICE_SHAREDPREFS_TREE_TOKEN = "d_sp";
  public static final String FILES_TREE_TOKEN = "f";
  public static final String FULL_BACKUP_INTENT_ACTION = "fullback";
  public static final String FULL_RESTORE_INTENT_ACTION = "fullrest";
  public static final String MANAGED_EXTERNAL_TREE_TOKEN = "ef";
  public static final String NO_BACKUP_TREE_TOKEN = "nb";
  public static final String OBB_TREE_TOKEN = "obb";
  public static final String ROOT_TREE_TOKEN = "r";
  public static final String SHAREDPREFS_TREE_TOKEN = "sp";
  public static final String SHARED_PREFIX = "shared/";
  public static final String SHARED_STORAGE_TOKEN = "shared";
  static final String TAG = "FullBackup";
  static final String TAG_XML_PARSER = "BackupXmlParserLogging";
  private static final Map<String, BackupScheme> kPackageBackupSchemeMap = new ArrayMap();
  
  public static native int backupToTar(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, FullBackupDataOutput paramFullBackupDataOutput);
  
  static BackupScheme getBackupScheme(Context paramContext)
  {
    try
    {
      BackupScheme localBackupScheme2 = (BackupScheme)kPackageBackupSchemeMap.get(paramContext.getPackageName());
      BackupScheme localBackupScheme1 = localBackupScheme2;
      if (localBackupScheme2 == null)
      {
        localBackupScheme1 = new BackupScheme(paramContext);
        kPackageBackupSchemeMap.put(paramContext.getPackageName(), localBackupScheme1);
      }
      return localBackupScheme1;
    }
    finally {}
  }
  
  public static BackupScheme getBackupSchemeForTest(Context paramContext)
  {
    paramContext = new BackupScheme(paramContext);
    paramContext.mExcludes = new ArraySet();
    paramContext.mIncludes = new ArrayMap();
    return paramContext;
  }
  
  public static void restoreFile(ParcelFileDescriptor paramParcelFileDescriptor, long paramLong1, int paramInt, long paramLong2, long paramLong3, File paramFile)
    throws IOException
  {
    if (paramInt == 2) {
      if (paramFile != null) {
        paramFile.mkdirs();
      }
    }
    for (;;)
    {
      if ((paramLong2 >= 0L) && (paramFile != null)) {}
      try
      {
        Os.chmod(paramFile.getPath(), (int)(paramLong2 & 0x1C0));
        paramFile.setLastModified(paramLong3);
        return;
        arrayOfByte = null;
        Object localObject1 = arrayOfByte;
        if (paramFile != null) {}
        try
        {
          localObject1 = paramFile.getParentFile();
          if (!((File)localObject1).exists()) {
            ((File)localObject1).mkdirs();
          }
          localObject1 = new FileOutputStream(paramFile);
        }
        catch (IOException localIOException)
        {
          for (;;)
          {
            FileInputStream localFileInputStream;
            long l;
            Log.e("FullBackup", "Unable to create/open file " + paramFile.getPath(), localIOException);
            localObject2 = arrayOfByte;
            continue;
            paramInt = (int)l;
            continue;
            paramParcelFileDescriptor = (ParcelFileDescriptor)localObject2;
            if (localObject2 != null) {}
            try
            {
              ((FileOutputStream)localObject2).write(arrayOfByte, 0, paramInt);
              paramParcelFileDescriptor = (ParcelFileDescriptor)localObject2;
            }
            catch (IOException paramParcelFileDescriptor)
            {
              for (;;)
              {
                Log.e("FullBackup", "Unable to write to file " + paramFile.getPath(), paramParcelFileDescriptor);
                ((FileOutputStream)localObject2).close();
                paramParcelFileDescriptor = null;
                paramFile.delete();
              }
            }
            l -= paramInt;
            localObject2 = paramParcelFileDescriptor;
          }
        }
        arrayOfByte = new byte[32768];
        localFileInputStream = new FileInputStream(paramParcelFileDescriptor.getFileDescriptor());
        l = paramLong1;
        if (l > 0L)
        {
          if (l > arrayOfByte.length)
          {
            paramInt = arrayOfByte.length;
            paramInt = localFileInputStream.read(arrayOfByte, 0, paramInt);
            if (paramInt > 0) {
              break label251;
            }
            Log.w("FullBackup", "Incomplete read: expected " + l + " but got " + (paramLong1 - l));
          }
        }
        else
        {
          if (localObject1 == null) {
            continue;
          }
          ((FileOutputStream)localObject1).close();
        }
      }
      catch (ErrnoException paramParcelFileDescriptor)
      {
        for (;;)
        {
          byte[] arrayOfByte;
          Object localObject2;
          label251:
          paramParcelFileDescriptor.rethrowAsIOException();
        }
      }
    }
  }
  
  public static class BackupScheme
  {
    private final File CACHE_DIR;
    private final File DATABASE_DIR;
    private final File DEVICE_CACHE_DIR;
    private final File DEVICE_DATABASE_DIR;
    private final File DEVICE_FILES_DIR;
    private final File DEVICE_NOBACKUP_DIR;
    private final File DEVICE_ROOT_DIR;
    private final File DEVICE_SHAREDPREF_DIR;
    private final File EXTERNAL_DIR;
    private final File FILES_DIR;
    private final File NOBACKUP_DIR;
    private final File ROOT_DIR;
    private final File SHAREDPREF_DIR;
    ArraySet<String> mExcludes;
    final int mFullBackupContent;
    Map<String, Set<String>> mIncludes;
    final PackageManager mPackageManager;
    final String mPackageName;
    final StorageManager mStorageManager;
    private StorageVolume[] mVolumes = null;
    
    BackupScheme(Context paramContext)
    {
      this.mFullBackupContent = paramContext.getApplicationInfo().fullBackupContent;
      this.mStorageManager = ((StorageManager)paramContext.getSystemService("storage"));
      this.mPackageManager = paramContext.getPackageManager();
      this.mPackageName = paramContext.getPackageName();
      Context localContext = paramContext.createCredentialProtectedStorageContext();
      this.FILES_DIR = localContext.getFilesDir();
      this.DATABASE_DIR = localContext.getDatabasePath("foo").getParentFile();
      this.ROOT_DIR = localContext.getDataDir();
      this.SHAREDPREF_DIR = localContext.getSharedPreferencesPath("foo").getParentFile();
      this.CACHE_DIR = localContext.getCacheDir();
      this.NOBACKUP_DIR = localContext.getNoBackupFilesDir();
      localContext = paramContext.createDeviceProtectedStorageContext();
      this.DEVICE_FILES_DIR = localContext.getFilesDir();
      this.DEVICE_DATABASE_DIR = localContext.getDatabasePath("foo").getParentFile();
      this.DEVICE_ROOT_DIR = localContext.getDataDir();
      this.DEVICE_SHAREDPREF_DIR = localContext.getSharedPreferencesPath("foo").getParentFile();
      this.DEVICE_CACHE_DIR = localContext.getCacheDir();
      this.DEVICE_NOBACKUP_DIR = localContext.getNoBackupFilesDir();
      if (Process.myUid() != 1000)
      {
        this.EXTERNAL_DIR = paramContext.getExternalFilesDir(null);
        return;
      }
      this.EXTERNAL_DIR = null;
    }
    
    private File extractCanonicalFile(File paramFile, String paramString)
    {
      String str = paramString;
      if (paramString == null) {
        str = "";
      }
      if (str.contains(".."))
      {
        if (Log.isLoggable("BackupXmlParserLogging", 2)) {
          Log.v("BackupXmlParserLogging", "...resolved \"" + paramFile.getPath() + " " + str + "\", but the \"..\" path is not permitted; skipping.");
        }
        return null;
      }
      if (str.contains("//"))
      {
        if (Log.isLoggable("BackupXmlParserLogging", 2)) {
          Log.v("BackupXmlParserLogging", "...resolved \"" + paramFile.getPath() + " " + str + "\", which contains the invalid \"//\" sequence; skipping.");
        }
        return null;
      }
      return new File(paramFile, str);
    }
    
    private File getDirectoryForCriteriaDomain(String paramString)
    {
      if (TextUtils.isEmpty(paramString)) {
        return null;
      }
      if ("file".equals(paramString)) {
        return this.FILES_DIR;
      }
      if ("database".equals(paramString)) {
        return this.DATABASE_DIR;
      }
      if ("root".equals(paramString)) {
        return this.ROOT_DIR;
      }
      if ("sharedpref".equals(paramString)) {
        return this.SHAREDPREF_DIR;
      }
      if ("device_file".equals(paramString)) {
        return this.DEVICE_FILES_DIR;
      }
      if ("device_database".equals(paramString)) {
        return this.DEVICE_DATABASE_DIR;
      }
      if ("device_root".equals(paramString)) {
        return this.DEVICE_ROOT_DIR;
      }
      if ("device_sharedpref".equals(paramString)) {
        return this.DEVICE_SHAREDPREF_DIR;
      }
      if ("external".equals(paramString)) {
        return this.EXTERNAL_DIR;
      }
      return null;
    }
    
    private String getTokenForXmlDomain(String paramString)
    {
      if ("root".equals(paramString)) {
        return "r";
      }
      if ("file".equals(paramString)) {
        return "f";
      }
      if ("database".equals(paramString)) {
        return "db";
      }
      if ("sharedpref".equals(paramString)) {
        return "sp";
      }
      if ("device_root".equals(paramString)) {
        return "d_r";
      }
      if ("device_file".equals(paramString)) {
        return "d_f";
      }
      if ("device_database".equals(paramString)) {
        return "d_db";
      }
      if ("device_sharedpref".equals(paramString)) {
        return "d_sp";
      }
      if ("external".equals(paramString)) {
        return "ef";
      }
      return null;
    }
    
    private StorageVolume[] getVolumeList()
    {
      if (this.mStorageManager != null) {
        if (this.mVolumes == null) {
          this.mVolumes = this.mStorageManager.getVolumeList();
        }
      }
      for (;;)
      {
        return this.mVolumes;
        Log.e("FullBackup", "Unable to access Storage Manager");
      }
    }
    
    private void maybeParseBackupSchemeLocked()
      throws IOException, XmlPullParserException
    {
      this.mIncludes = new ArrayMap();
      this.mExcludes = new ArraySet();
      if (this.mFullBackupContent == 0) {
        if (Log.isLoggable("BackupXmlParserLogging", 2)) {
          Log.v("BackupXmlParserLogging", "android:fullBackupContent - \"true\"");
        }
      }
      for (;;)
      {
        return;
        if (Log.isLoggable("BackupXmlParserLogging", 2)) {
          Log.v("BackupXmlParserLogging", "android:fullBackupContent - found xml resource");
        }
        Object localObject3 = null;
        Object localObject1 = null;
        try
        {
          XmlResourceParser localXmlResourceParser = this.mPackageManager.getResourcesForApplication(this.mPackageName).getXml(this.mFullBackupContent);
          localObject1 = localXmlResourceParser;
          localObject3 = localXmlResourceParser;
          parseBackupSchemeFromXmlLocked(localXmlResourceParser, this.mExcludes, this.mIncludes);
          if (localXmlResourceParser == null) {
            continue;
          }
          localXmlResourceParser.close();
          return;
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {
          localObject3 = localObject1;
          throw new IOException(localNameNotFoundException);
        }
        finally
        {
          if (localObject3 != null) {
            ((XmlResourceParser)localObject3).close();
          }
        }
      }
    }
    
    private Set<String> parseCurrentTagForDomain(XmlPullParser paramXmlPullParser, Set<String> paramSet, Map<String, Set<String>> paramMap, String paramString)
      throws XmlPullParserException
    {
      if ("include".equals(paramXmlPullParser.getName()))
      {
        paramString = getTokenForXmlDomain(paramString);
        paramSet = (Set)paramMap.get(paramString);
        paramXmlPullParser = paramSet;
        if (paramSet == null)
        {
          paramXmlPullParser = new ArraySet();
          paramMap.put(paramString, paramXmlPullParser);
        }
        return paramXmlPullParser;
      }
      if ("exclude".equals(paramXmlPullParser.getName())) {
        return paramSet;
      }
      if (Log.isLoggable("BackupXmlParserLogging", 2)) {
        Log.v("BackupXmlParserLogging", "Invalid tag found in xml \"" + paramXmlPullParser.getName() + "\"; aborting operation.");
      }
      throw new XmlPullParserException("Unrecognised tag in backup criteria xml (" + paramXmlPullParser.getName() + ")");
    }
    
    private String sharedDomainToPath(String paramString)
      throws IOException
    {
      paramString = paramString.substring("shared/".length());
      StorageVolume[] arrayOfStorageVolume = getVolumeList();
      int i = Integer.parseInt(paramString);
      if (i < this.mVolumes.length) {
        return arrayOfStorageVolume[i].getPathFile().getCanonicalPath();
      }
      return null;
    }
    
    private void validateInnerTagContents(XmlPullParser paramXmlPullParser)
      throws XmlPullParserException
    {
      if (paramXmlPullParser.getAttributeCount() > 2) {
        throw new XmlPullParserException("At most 2 tag attributes allowed for \"" + paramXmlPullParser.getName() + "\" tag (\"domain\" & \"path\".");
      }
      if (("include".equals(paramXmlPullParser.getName())) || ("exclude".equals(paramXmlPullParser.getName()))) {
        return;
      }
      throw new XmlPullParserException("A valid tag is one of \"<include/>\" or \"<exclude/>. You provided \"" + paramXmlPullParser.getName() + "\"");
    }
    
    boolean isFullBackupContentEnabled()
    {
      if (this.mFullBackupContent < 0)
      {
        if (Log.isLoggable("BackupXmlParserLogging", 2)) {
          Log.v("BackupXmlParserLogging", "android:fullBackupContent - \"false\"");
        }
        return false;
      }
      return true;
    }
    
    public ArraySet<String> maybeParseAndGetCanonicalExcludePaths()
      throws IOException, XmlPullParserException
    {
      try
      {
        if (this.mExcludes == null) {
          maybeParseBackupSchemeLocked();
        }
        ArraySet localArraySet = this.mExcludes;
        return localArraySet;
      }
      finally {}
    }
    
    public Map<String, Set<String>> maybeParseAndGetCanonicalIncludePaths()
      throws IOException, XmlPullParserException
    {
      try
      {
        if (this.mIncludes == null) {
          maybeParseBackupSchemeLocked();
        }
        Map localMap = this.mIncludes;
        return localMap;
      }
      finally {}
    }
    
    public void parseBackupSchemeFromXmlLocked(XmlPullParser paramXmlPullParser, Set<String> paramSet, Map<String, Set<String>> paramMap)
      throws IOException, XmlPullParserException
    {
      for (int i = paramXmlPullParser.getEventType(); i != 2; i = paramXmlPullParser.next()) {}
      if (!"full-backup-content".equals(paramXmlPullParser.getName())) {
        throw new XmlPullParserException("Xml file didn't start with correct tag (<full-backup-content>). Found \"" + paramXmlPullParser.getName() + "\"");
      }
      if (Log.isLoggable("BackupXmlParserLogging", 2))
      {
        Log.v("BackupXmlParserLogging", "\n");
        Log.v("BackupXmlParserLogging", "====================================================");
        Log.v("BackupXmlParserLogging", "Found valid fullBackupContent; parsing xml resource.");
        Log.v("BackupXmlParserLogging", "====================================================");
        Log.v("BackupXmlParserLogging", "");
      }
      String str1;
      label658:
      for (;;)
      {
        i = paramXmlPullParser.next();
        if (i == 1) {
          break;
        }
        switch (i)
        {
        default: 
          break;
        case 2: 
          validateInnerTagContents(paramXmlPullParser);
          str1 = paramXmlPullParser.getAttributeValue(null, "domain");
          Object localObject = getDirectoryForCriteriaDomain(str1);
          if (localObject == null)
          {
            if (Log.isLoggable("BackupXmlParserLogging", 2)) {
              Log.v("BackupXmlParserLogging", "...parsing \"" + paramXmlPullParser.getName() + "\": " + "domain=\"" + str1 + "\" invalid; skipping");
            }
          }
          else
          {
            File localFile = extractCanonicalFile((File)localObject, paramXmlPullParser.getAttributeValue(null, "path"));
            if (localFile != null)
            {
              localObject = parseCurrentTagForDomain(paramXmlPullParser, paramSet, paramMap, str1);
              ((Set)localObject).add(localFile.getCanonicalPath());
              if (Log.isLoggable("BackupXmlParserLogging", 2)) {
                Log.v("BackupXmlParserLogging", "...parsed " + localFile.getCanonicalPath() + " for domain \"" + str1 + "\"");
              }
              if ((!"database".equals(str1)) || (localFile.isDirectory())) {}
              for (;;)
              {
                if ((!"sharedpref".equals(str1)) || (localFile.isDirectory()) || (localFile.getCanonicalPath().endsWith(".xml"))) {
                  break label658;
                }
                str1 = localFile.getCanonicalPath() + ".xml";
                ((Set)localObject).add(str1);
                if (!Log.isLoggable("BackupXmlParserLogging", 2)) {
                  break;
                }
                Log.v("BackupXmlParserLogging", "...automatically generated " + str1 + ". Ignore if nonexistent.");
                break;
                String str2 = localFile.getCanonicalPath() + "-journal";
                ((Set)localObject).add(str2);
                if (Log.isLoggable("BackupXmlParserLogging", 2)) {
                  Log.v("BackupXmlParserLogging", "...automatically generated " + str2 + ". Ignore if nonexistent.");
                }
                str2 = localFile.getCanonicalPath() + "-wal";
                ((Set)localObject).add(str2);
                if (Log.isLoggable("BackupXmlParserLogging", 2)) {
                  Log.v("BackupXmlParserLogging", "...automatically generated " + str2 + ". Ignore if nonexistent.");
                }
              }
            }
          }
          break;
        }
      }
      if (Log.isLoggable("BackupXmlParserLogging", 2))
      {
        Log.v("BackupXmlParserLogging", "\n");
        Log.v("BackupXmlParserLogging", "Xml resource parsing complete.");
        Log.v("BackupXmlParserLogging", "Final tally.");
        Log.v("BackupXmlParserLogging", "Includes:");
        if (!paramMap.isEmpty()) {
          break label778;
        }
        Log.v("BackupXmlParserLogging", "  ...nothing specified (This means the entirety of app data minus excludes)");
        Log.v("BackupXmlParserLogging", "Excludes:");
        if (!paramSet.isEmpty()) {
          break label908;
        }
        Log.v("BackupXmlParserLogging", "  ...nothing to exclude.");
      }
      for (;;)
      {
        Log.v("BackupXmlParserLogging", "  ");
        Log.v("BackupXmlParserLogging", "====================================================");
        Log.v("BackupXmlParserLogging", "\n");
        return;
        label778:
        paramXmlPullParser = paramMap.entrySet().iterator();
        while (paramXmlPullParser.hasNext())
        {
          paramMap = (Map.Entry)paramXmlPullParser.next();
          Log.v("BackupXmlParserLogging", "  domain=" + (String)paramMap.getKey());
          paramMap = ((Set)paramMap.getValue()).iterator();
          while (paramMap.hasNext())
          {
            str1 = (String)paramMap.next();
            Log.v("BackupXmlParserLogging", "  " + str1);
          }
        }
        label908:
        paramXmlPullParser = paramSet.iterator();
        while (paramXmlPullParser.hasNext())
        {
          paramSet = (String)paramXmlPullParser.next();
          Log.v("BackupXmlParserLogging", "  " + paramSet);
        }
      }
    }
    
    String tokenToDirectoryPath(String paramString)
    {
      try
      {
        if (paramString.equals("f")) {
          return this.FILES_DIR.getCanonicalPath();
        }
        if (paramString.equals("db")) {
          return this.DATABASE_DIR.getCanonicalPath();
        }
        if (paramString.equals("r")) {
          return this.ROOT_DIR.getCanonicalPath();
        }
        if (paramString.equals("sp")) {
          return this.SHAREDPREF_DIR.getCanonicalPath();
        }
        if (paramString.equals("c")) {
          return this.CACHE_DIR.getCanonicalPath();
        }
        if (paramString.equals("nb")) {
          return this.NOBACKUP_DIR.getCanonicalPath();
        }
        if (paramString.equals("d_f")) {
          return this.DEVICE_FILES_DIR.getCanonicalPath();
        }
        if (paramString.equals("d_db")) {
          return this.DEVICE_DATABASE_DIR.getCanonicalPath();
        }
        if (paramString.equals("d_r")) {
          return this.DEVICE_ROOT_DIR.getCanonicalPath();
        }
        if (paramString.equals("d_sp")) {
          return this.DEVICE_SHAREDPREF_DIR.getCanonicalPath();
        }
        if (paramString.equals("d_c")) {
          return this.DEVICE_CACHE_DIR.getCanonicalPath();
        }
        if (paramString.equals("d_nb")) {
          return this.DEVICE_NOBACKUP_DIR.getCanonicalPath();
        }
        if (paramString.equals("ef"))
        {
          if (this.EXTERNAL_DIR != null) {
            return this.EXTERNAL_DIR.getCanonicalPath();
          }
        }
        else
        {
          if (paramString.startsWith("shared/")) {
            return sharedDomainToPath(paramString);
          }
          Log.i("FullBackup", "Unrecognized domain " + paramString);
          return null;
        }
      }
      catch (Exception localException)
      {
        Log.i("FullBackup", "Error reading directory for domain: " + paramString);
        return null;
      }
      return null;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/backup/FullBackup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */