package android.content;

import android.net.Uri;
import android.os.Build;
import android.os.SystemProperties;
import android.provider.MediaStore.Files;
import android.util.Log;
import android.util.OpFeatures;
import java.util.Arrays;
import java.util.List;

public class ContentDebugUtils
{
  public static final boolean DBG = Build.DEBUG_ONEPLUS | DBG_ALL;
  public static final boolean DBG_ALL;
  public static final boolean DBG_DUMP_STACK;
  private static final Uri DBG_URI = Uri.parse("content://media/external/op_debug");
  public static final boolean SAVE_DBG_MSG = SystemProperties.getBoolean("persist.debug.content.savemsg", OpFeatures.isSupport(new int[] { 0 }));
  private static final String TAG = "ContentDebugUtils";
  
  static
  {
    DBG_ALL = SystemProperties.getBoolean("persist.debug.content.all", false);
    DBG_DUMP_STACK = SystemProperties.getBoolean("persist.debug.content.dumpstack", false);
  }
  
  private static String getUriVolumeName(Uri paramUri)
  {
    paramUri = paramUri.getPathSegments();
    if ((paramUri != null) && (paramUri.size() > 0)) {
      return (String)paramUri.get(0);
    }
    return null;
  }
  
  public static boolean isExternalMediaUri(Uri paramUri)
  {
    return (paramUri != null) && ("media".equals(paramUri.getAuthority())) && ("external".equals(getUriVolumeName(paramUri))) && (paramUri != DBG_URI);
  }
  
  private static ContentValues packContentValues(String paramString1, String paramString2, Uri paramUri, String paramString3, String[] paramArrayOfString, String paramString4)
  {
    Object localObject = null;
    String str = null;
    ContentValues localContentValues = new ContentValues();
    if (paramArrayOfString != null) {
      str = Arrays.toString(paramArrayOfString);
    }
    paramArrayOfString = (String[])localObject;
    if (paramUri != null) {
      paramArrayOfString = paramUri.toString();
    }
    localContentValues.put("_tag", paramString1);
    localContentValues.put("_action", paramString2);
    localContentValues.put("_uri", paramArrayOfString);
    localContentValues.put("sel", paramString3);
    localContentValues.put("sel_arg", str);
    localContentValues.put("_pkg_name", paramString4);
    if (DBG) {
      Log.d("ContentDebugUtils", paramString1 + ": " + localContentValues);
    }
    return localContentValues;
  }
  
  public static void saveDbgMsg(ContentProvider paramContentProvider, String paramString1, String paramString2, Uri paramUri, String paramString3, String[] paramArrayOfString, String paramString4)
  {
    if (paramUri != null) {}
    try
    {
      if (!paramUri.toString().endsWith("deletedata=false"))
      {
        paramString1 = packContentValues(paramString1, paramString2, paramUri, paramString3, paramArrayOfString, paramString4);
        if (isExternalMediaUri(paramUri)) {
          paramContentProvider.insert(DBG_URI, paramString1);
        }
      }
      if (DBG_DUMP_STACK) {
        Log.d("ContentDebugUtils", "DbgMsg Stack:", new Throwable());
      }
      return;
    }
    catch (Exception paramContentProvider)
    {
      do
      {
        paramContentProvider = paramContentProvider;
      } while (!DBG_DUMP_STACK);
      Log.d("ContentDebugUtils", "DbgMsg Stack:", new Throwable());
      return;
    }
    finally
    {
      paramContentProvider = finally;
      if (DBG_DUMP_STACK) {
        Log.d("ContentDebugUtils", "DbgMsg Stack:", new Throwable());
      }
      throw paramContentProvider;
    }
  }
  
  public static void saveDbgMsg(Context paramContext, String paramString1, String paramString2, String paramString3, String paramString4)
  {
    paramContext = paramContext.getContentResolver().acquireUnstableContentProviderClient(MediaStore.Files.getContentUri("external"));
    if (paramContext != null) {}
    try
    {
      paramString1 = packContentValues(paramString1, paramString2, null, paramString3, null, paramString4);
      paramContext.insert(DBG_URI, paramString1);
      if (DBG_DUMP_STACK) {
        Log.d("ContentDebugUtils", "DbgMsg Stack:", new Throwable());
      }
      if (paramContext != null) {
        paramContext.release();
      }
      return;
    }
    catch (Exception paramString1)
    {
      do
      {
        paramString1 = paramString1;
        if (DBG_DUMP_STACK) {
          Log.d("ContentDebugUtils", "DbgMsg Stack:", new Throwable());
        }
      } while (paramContext == null);
      paramContext.release();
      return;
    }
    finally
    {
      paramString1 = finally;
      if (DBG_DUMP_STACK) {
        Log.d("ContentDebugUtils", "DbgMsg Stack:", new Throwable());
      }
      if (paramContext != null) {
        paramContext.release();
      }
      throw paramString1;
    }
  }
  
  public static void saveDbgMsg(IContentProvider paramIContentProvider, String paramString1, String paramString2, Uri paramUri, String paramString3, String[] paramArrayOfString, String paramString4)
  {
    if ((paramUri != null) && (paramIContentProvider != null)) {}
    try
    {
      if (!paramUri.toString().endsWith("deletedata=false"))
      {
        paramString1 = packContentValues(paramString1, paramString2, paramUri, paramString3, paramArrayOfString, paramString4);
        if (isExternalMediaUri(paramUri)) {
          paramIContentProvider.insert(paramString4, DBG_URI, paramString1);
        }
      }
      if (DBG_DUMP_STACK) {
        Log.d("ContentDebugUtils", "DbgMsg Stack:", new Throwable());
      }
      return;
    }
    catch (Exception paramIContentProvider)
    {
      do
      {
        paramIContentProvider = paramIContentProvider;
      } while (!DBG_DUMP_STACK);
      Log.d("ContentDebugUtils", "DbgMsg Stack:", new Throwable());
      return;
    }
    finally
    {
      paramIContentProvider = finally;
      if (DBG_DUMP_STACK) {
        Log.d("ContentDebugUtils", "DbgMsg Stack:", new Throwable());
      }
      throw paramIContentProvider;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/ContentDebugUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */