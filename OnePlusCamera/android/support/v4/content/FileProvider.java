package android.support.v4.content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import org.xmlpull.v1.XmlPullParserException;

public class FileProvider
  extends ContentProvider
{
  private static final String ATTR_NAME = "name";
  private static final String ATTR_PATH = "path";
  private static final String[] COLUMNS = { "_display_name", "_size" };
  private static final File DEVICE_ROOT = new File("/");
  private static final String META_DATA_FILE_PROVIDER_PATHS = "android.support.FILE_PROVIDER_PATHS";
  private static final String TAG_CACHE_PATH = "cache-path";
  private static final String TAG_EXTERNAL = "external-path";
  private static final String TAG_FILES_PATH = "files-path";
  private static final String TAG_ROOT_PATH = "root-path";
  private static HashMap<String, PathStrategy> sCache = new HashMap();
  private PathStrategy mStrategy;
  
  private static File buildPath(File paramFile, String... paramVarArgs)
  {
    int j = paramVarArgs.length;
    int i = 0;
    if (i >= j) {
      return paramFile;
    }
    String str = paramVarArgs[i];
    if (str == null) {}
    for (;;)
    {
      i += 1;
      break;
      paramFile = new File(paramFile, str);
    }
  }
  
  private static Object[] copyOf(Object[] paramArrayOfObject, int paramInt)
  {
    Object[] arrayOfObject = new Object[paramInt];
    System.arraycopy(paramArrayOfObject, 0, arrayOfObject, 0, paramInt);
    return arrayOfObject;
  }
  
  private static String[] copyOf(String[] paramArrayOfString, int paramInt)
  {
    String[] arrayOfString = new String[paramInt];
    System.arraycopy(paramArrayOfString, 0, arrayOfString, 0, paramInt);
    return arrayOfString;
  }
  
  private static PathStrategy getPathStrategy(Context paramContext, String paramString)
  {
    synchronized (sCache)
    {
      PathStrategy localPathStrategy = (PathStrategy)sCache.get(paramString);
      if (localPathStrategy != null)
      {
        paramContext = localPathStrategy;
        return paramContext;
      }
    }
  }
  
  public static Uri getUriForFile(Context paramContext, String paramString, File paramFile)
  {
    return getPathStrategy(paramContext, paramString).getUriForFile(paramFile);
  }
  
  private static int modeToMode(String paramString)
  {
    if (!"r".equals(paramString)) {
      if (!"w".equals(paramString)) {
        break label24;
      }
    }
    label24:
    while ("wt".equals(paramString))
    {
      return 738197504;
      return 268435456;
    }
    if (!"wa".equals(paramString))
    {
      if (!"rw".equals(paramString))
      {
        if ("rwt".equals(paramString)) {
          break label93;
        }
        throw new IllegalArgumentException("Invalid mode: " + paramString);
      }
    }
    else {
      return 704643072;
    }
    return 939524096;
    label93:
    return 1006632960;
  }
  
  private static PathStrategy parsePathStrategy(Context paramContext, String paramString)
    throws IOException, XmlPullParserException
  {
    SimplePathStrategy localSimplePathStrategy = new SimplePathStrategy(paramString);
    XmlResourceParser localXmlResourceParser = paramContext.getPackageManager().resolveContentProvider(paramString, 128).loadXmlMetaData(paramContext.getPackageManager(), "android.support.FILE_PROVIDER_PATHS");
    if (localXmlResourceParser != null) {}
    label169:
    label189:
    label209:
    label226:
    for (;;)
    {
      int i = localXmlResourceParser.next();
      if (i == 1)
      {
        return localSimplePathStrategy;
        throw new IllegalArgumentException("Missing android.support.FILE_PROVIDER_PATHS meta-data");
      }
      if (i == 2)
      {
        paramString = localXmlResourceParser.getName();
        String str1 = localXmlResourceParser.getAttributeValue(null, "name");
        String str2 = localXmlResourceParser.getAttributeValue(null, "path");
        if (!"root-path".equals(paramString))
        {
          if ("files-path".equals(paramString)) {
            break label169;
          }
          if ("cache-path".equals(paramString)) {
            break label189;
          }
          if ("external-path".equals(paramString)) {
            break label209;
          }
          paramString = null;
        }
        for (;;)
        {
          if (paramString == null) {
            break label226;
          }
          localSimplePathStrategy.addRoot(str1, paramString);
          break;
          paramString = buildPath(DEVICE_ROOT, new String[] { str2 });
          continue;
          paramString = buildPath(paramContext.getFilesDir(), new String[] { str2 });
          continue;
          paramString = buildPath(paramContext.getCacheDir(), new String[] { str2 });
          continue;
          paramString = buildPath(Environment.getExternalStorageDirectory(), new String[] { str2 });
        }
      }
    }
  }
  
  public void attachInfo(Context paramContext, ProviderInfo paramProviderInfo)
  {
    super.attachInfo(paramContext, paramProviderInfo);
    if (!paramProviderInfo.exported)
    {
      if (paramProviderInfo.grantUriPermissions) {
        this.mStrategy = getPathStrategy(paramContext, paramProviderInfo.authority);
      }
    }
    else {
      throw new SecurityException("Provider must not be exported");
    }
    throw new SecurityException("Provider must grant uri permissions");
  }
  
  public int delete(Uri paramUri, String paramString, String[] paramArrayOfString)
  {
    if (!this.mStrategy.getFileForUri(paramUri).delete()) {
      return 0;
    }
    return 1;
  }
  
  public String getType(Uri paramUri)
  {
    paramUri = this.mStrategy.getFileForUri(paramUri);
    int i = paramUri.getName().lastIndexOf('.');
    if (i < 0) {}
    do
    {
      return "application/octet-stream";
      paramUri = paramUri.getName().substring(i + 1);
      paramUri = MimeTypeMap.getSingleton().getMimeTypeFromExtension(paramUri);
    } while (paramUri == null);
    return paramUri;
  }
  
  public Uri insert(Uri paramUri, ContentValues paramContentValues)
  {
    throw new UnsupportedOperationException("No external inserts");
  }
  
  public boolean onCreate()
  {
    return true;
  }
  
  public ParcelFileDescriptor openFile(Uri paramUri, String paramString)
    throws FileNotFoundException
  {
    return ParcelFileDescriptor.open(this.mStrategy.getFileForUri(paramUri), modeToMode(paramString));
  }
  
  public Cursor query(Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2)
  {
    int i = 0;
    paramUri = this.mStrategy.getFileForUri(paramUri);
    if (paramArrayOfString1 != null) {}
    int j;
    for (;;)
    {
      paramArrayOfString2 = new String[paramArrayOfString1.length];
      paramString1 = new Object[paramArrayOfString1.length];
      int k = paramArrayOfString1.length;
      j = 0;
      if (j < k) {
        break;
      }
      paramUri = copyOf(paramArrayOfString2, i);
      paramArrayOfString1 = copyOf(paramString1, i);
      paramUri = new MatrixCursor(paramUri, 1);
      paramUri.addRow(paramArrayOfString1);
      return paramUri;
      paramArrayOfString1 = COLUMNS;
    }
    paramString2 = paramArrayOfString1[j];
    if (!"_display_name".equals(paramString2)) {
      if ("_size".equals(paramString2)) {
        break label143;
      }
    }
    for (;;)
    {
      j += 1;
      break;
      paramArrayOfString2[i] = "_display_name";
      paramString1[i] = paramUri.getName();
      i += 1;
      continue;
      label143:
      paramArrayOfString2[i] = "_size";
      paramString1[i] = Long.valueOf(paramUri.length());
      i += 1;
    }
  }
  
  public int update(Uri paramUri, ContentValues paramContentValues, String paramString, String[] paramArrayOfString)
  {
    throw new UnsupportedOperationException("No external updates");
  }
  
  static abstract interface PathStrategy
  {
    public abstract File getFileForUri(Uri paramUri);
    
    public abstract Uri getUriForFile(File paramFile);
  }
  
  static class SimplePathStrategy
    implements FileProvider.PathStrategy
  {
    private final String mAuthority;
    private final HashMap<String, File> mRoots = new HashMap();
    
    public SimplePathStrategy(String paramString)
    {
      this.mAuthority = paramString;
    }
    
    public void addRoot(String paramString, File paramFile)
    {
      if (!TextUtils.isEmpty(paramString)) {}
      try
      {
        File localFile = paramFile.getCanonicalFile();
        this.mRoots.put(paramString, localFile);
        return;
      }
      catch (IOException paramString)
      {
        throw new IllegalArgumentException("Failed to resolve canonical path for " + paramFile, paramString);
      }
      throw new IllegalArgumentException("Name must not be empty");
    }
    
    public File getFileForUri(Uri paramUri)
    {
      Object localObject2 = paramUri.getEncodedPath();
      int i = ((String)localObject2).indexOf('/', 1);
      Object localObject1 = Uri.decode(((String)localObject2).substring(1, i));
      localObject2 = Uri.decode(((String)localObject2).substring(i + 1));
      localObject1 = (File)this.mRoots.get(localObject1);
      if (localObject1 != null) {
        paramUri = new File((File)localObject1, (String)localObject2);
      }
      try
      {
        localObject2 = paramUri.getCanonicalFile();
        if (!((File)localObject2).getPath().startsWith(((File)localObject1).getPath())) {
          break label145;
        }
        return (File)localObject2;
      }
      catch (IOException localIOException)
      {
        throw new IllegalArgumentException("Failed to resolve canonical path for " + paramUri);
      }
      throw new IllegalArgumentException("Unable to find configured root for " + paramUri);
      label145:
      throw new SecurityException("Resolved path jumped beyond configured root");
    }
    
    public Uri getUriForFile(File paramFile)
    {
      for (;;)
      {
        String str2;
        Iterator localIterator;
        try
        {
          str2 = paramFile.getCanonicalPath();
          localIterator = this.mRoots.entrySet().iterator();
          paramFile = null;
          if (!localIterator.hasNext())
          {
            if (paramFile == null) {
              break label241;
            }
            String str1 = ((File)paramFile.getValue()).getPath();
            if (str1.endsWith("/")) {
              break label269;
            }
            str1 = str2.substring(str1.length() + 1);
            paramFile = Uri.encode((String)paramFile.getKey()) + '/' + Uri.encode(str1, "/");
            return new Uri.Builder().scheme("content").authority(this.mAuthority).encodedPath(paramFile).build();
          }
        }
        catch (IOException localIOException)
        {
          throw new IllegalArgumentException("Failed to resolve canonical path for " + paramFile);
        }
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        String str3 = ((File)localEntry.getValue()).getPath();
        if (!str2.startsWith(str3)) {}
        for (;;)
        {
          localObject = paramFile;
          do
          {
            do
            {
              paramFile = (File)localObject;
              break;
              localObject = localEntry;
            } while (paramFile == null);
            localObject = localEntry;
          } while (str3.length() > ((File)paramFile.getValue()).getPath().length());
        }
        label241:
        throw new IllegalArgumentException("Failed to find configured root that contains " + str2);
        label269:
        Object localObject = str2.substring(((String)localObject).length());
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/content/FileProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */