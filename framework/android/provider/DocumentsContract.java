package android.provider;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.OperationCanceledException;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import libcore.io.IoUtils;

public final class DocumentsContract
{
  public static final String ACTION_BROWSE = "android.provider.action.BROWSE";
  public static final String ACTION_DOCUMENT_ROOT_SETTINGS = "android.provider.action.DOCUMENT_ROOT_SETTINGS";
  public static final String ACTION_MANAGE_DOCUMENT = "android.provider.action.MANAGE_DOCUMENT";
  public static final String EXTRA_ERROR = "error";
  public static final String EXTRA_EXCLUDE_SELF = "android.provider.extra.EXCLUDE_SELF";
  public static final String EXTRA_FANCY_FEATURES = "android.content.extra.FANCY";
  public static final String EXTRA_INFO = "info";
  public static final String EXTRA_LOADING = "loading";
  public static final String EXTRA_ORIENTATION = "android.provider.extra.ORIENTATION";
  public static final String EXTRA_PACKAGE_NAME = "android.content.extra.PACKAGE_NAME";
  public static final String EXTRA_PARENT_URI = "parentUri";
  public static final String EXTRA_PATH = "path";
  public static final String EXTRA_PROMPT = "android.provider.extra.PROMPT";
  public static final String EXTRA_RESULT = "result";
  public static final String EXTRA_SHOW_ADVANCED = "android.content.extra.SHOW_ADVANCED";
  public static final String EXTRA_SHOW_FILESIZE = "android.content.extra.SHOW_FILESIZE";
  public static final String EXTRA_TARGET_URI = "android.content.extra.TARGET_URI";
  public static final String EXTRA_URI = "uri";
  public static final String METHOD_COPY_DOCUMENT = "android:copyDocument";
  public static final String METHOD_CREATE_DOCUMENT = "android:createDocument";
  public static final String METHOD_DELETE_DOCUMENT = "android:deleteDocument";
  public static final String METHOD_IS_CHILD_DOCUMENT = "android:isChildDocument";
  public static final String METHOD_MOVE_DOCUMENT = "android:moveDocument";
  public static final String METHOD_PATH_DOCUMENT = "android:getPathDocument";
  public static final String METHOD_REMOVE_DOCUMENT = "android:removeDocument";
  public static final String METHOD_RENAME_DOCUMENT = "android:renameDocument";
  public static final String PACKAGE_DOCUMENTS_UI = "com.android.documentsui";
  private static final String PARAM_MANAGE = "manage";
  private static final String PARAM_QUERY = "query";
  private static final String PATH_CHILDREN = "children";
  private static final String PATH_DOCUMENT = "document";
  private static final String PATH_RECENT = "recent";
  private static final String PATH_ROOT = "root";
  private static final String PATH_SEARCH = "search";
  private static final String PATH_TREE = "tree";
  public static final String PROVIDER_INTERFACE = "android.content.action.DOCUMENTS_PROVIDER";
  private static final String TAG = "DocumentsContract";
  private static final int THUMBNAIL_BUFFER_SIZE = 131072;
  
  public static Uri buildChildDocumentsUri(String paramString1, String paramString2)
  {
    return new Uri.Builder().scheme("content").authority(paramString1).appendPath("document").appendPath(paramString2).appendPath("children").build();
  }
  
  public static Uri buildChildDocumentsUriUsingTree(Uri paramUri, String paramString)
  {
    return new Uri.Builder().scheme("content").authority(paramUri.getAuthority()).appendPath("tree").appendPath(getTreeDocumentId(paramUri)).appendPath("document").appendPath(paramString).appendPath("children").build();
  }
  
  public static Uri buildDocumentUri(String paramString1, String paramString2)
  {
    return new Uri.Builder().scheme("content").authority(paramString1).appendPath("document").appendPath(paramString2).build();
  }
  
  public static Uri buildDocumentUriMaybeUsingTree(Uri paramUri, String paramString)
  {
    if (isTreeUri(paramUri)) {
      return buildDocumentUriUsingTree(paramUri, paramString);
    }
    return buildDocumentUri(paramUri.getAuthority(), paramString);
  }
  
  public static Uri buildDocumentUriUsingTree(Uri paramUri, String paramString)
  {
    return new Uri.Builder().scheme("content").authority(paramUri.getAuthority()).appendPath("tree").appendPath(getTreeDocumentId(paramUri)).appendPath("document").appendPath(paramString).build();
  }
  
  public static Uri buildHomeUri()
  {
    return buildRootUri("com.android.externalstorage.documents", "home");
  }
  
  public static Uri buildRecentDocumentsUri(String paramString1, String paramString2)
  {
    return new Uri.Builder().scheme("content").authority(paramString1).appendPath("root").appendPath(paramString2).appendPath("recent").build();
  }
  
  public static Uri buildRootUri(String paramString1, String paramString2)
  {
    return new Uri.Builder().scheme("content").authority(paramString1).appendPath("root").appendPath(paramString2).build();
  }
  
  public static Uri buildRootsUri(String paramString)
  {
    return new Uri.Builder().scheme("content").authority(paramString).appendPath("root").build();
  }
  
  public static Uri buildSearchDocumentsUri(String paramString1, String paramString2, String paramString3)
  {
    return new Uri.Builder().scheme("content").authority(paramString1).appendPath("root").appendPath(paramString2).appendPath("search").appendQueryParameter("query", paramString3).build();
  }
  
  public static Uri buildTreeDocumentUri(String paramString1, String paramString2)
  {
    return new Uri.Builder().scheme("content").authority(paramString1).appendPath("tree").appendPath(paramString2).build();
  }
  
  public static Uri copyDocument(ContentProviderClient paramContentProviderClient, Uri paramUri1, Uri paramUri2)
    throws RemoteException
  {
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("uri", paramUri1);
    localBundle.putParcelable("android.content.extra.TARGET_URI", paramUri2);
    return (Uri)paramContentProviderClient.call("android:copyDocument", null, localBundle).getParcelable("uri");
  }
  
  public static Uri copyDocument(ContentResolver paramContentResolver, Uri paramUri1, Uri paramUri2)
  {
    paramContentResolver = paramContentResolver.acquireUnstableContentProviderClient(paramUri1.getAuthority());
    try
    {
      paramUri1 = copyDocument(paramContentResolver, paramUri1, paramUri2);
      return paramUri1;
    }
    catch (Exception paramUri1)
    {
      Log.w("DocumentsContract", "Failed to copy document", paramUri1);
      return null;
    }
    finally
    {
      ContentProviderClient.releaseQuietly(paramContentResolver);
    }
  }
  
  public static Uri createDocument(ContentProviderClient paramContentProviderClient, Uri paramUri, String paramString1, String paramString2)
    throws RemoteException
  {
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("uri", paramUri);
    localBundle.putString("mime_type", paramString1);
    localBundle.putString("_display_name", paramString2);
    return (Uri)paramContentProviderClient.call("android:createDocument", null, localBundle).getParcelable("uri");
  }
  
  public static Uri createDocument(ContentResolver paramContentResolver, Uri paramUri, String paramString1, String paramString2)
  {
    paramContentResolver = paramContentResolver.acquireUnstableContentProviderClient(paramUri.getAuthority());
    try
    {
      paramUri = createDocument(paramContentResolver, paramUri, paramString1, paramString2);
      return paramUri;
    }
    catch (Exception paramUri)
    {
      Log.w("DocumentsContract", "Failed to create document", paramUri);
      return null;
    }
    finally
    {
      ContentProviderClient.releaseQuietly(paramContentResolver);
    }
  }
  
  public static void deleteDocument(ContentProviderClient paramContentProviderClient, Uri paramUri)
    throws RemoteException
  {
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("uri", paramUri);
    paramContentProviderClient.call("android:deleteDocument", null, localBundle);
  }
  
  public static boolean deleteDocument(ContentResolver paramContentResolver, Uri paramUri)
  {
    paramContentResolver = paramContentResolver.acquireUnstableContentProviderClient(paramUri.getAuthority());
    try
    {
      deleteDocument(paramContentResolver, paramUri);
      return true;
    }
    catch (Exception paramUri)
    {
      Log.w("DocumentsContract", "Failed to delete document", paramUri);
      return false;
    }
    finally
    {
      ContentProviderClient.releaseQuietly(paramContentResolver);
    }
  }
  
  public static String getDocumentId(Uri paramUri)
  {
    List localList = paramUri.getPathSegments();
    if ((localList.size() >= 2) && ("document".equals(localList.get(0)))) {
      return (String)localList.get(1);
    }
    if ((localList.size() >= 4) && ("tree".equals(localList.get(0))) && ("document".equals(localList.get(2)))) {
      return (String)localList.get(3);
    }
    throw new IllegalArgumentException("Invalid URI: " + paramUri);
  }
  
  public static Bitmap getDocumentThumbnail(ContentProviderClient paramContentProviderClient, Uri paramUri, Point paramPoint, CancellationSignal paramCancellationSignal)
    throws RemoteException, IOException
  {
    Object localObject = new Bundle();
    ((Bundle)localObject).putParcelable("android.content.extra.SIZE", paramPoint);
    CancellationSignal localCancellationSignal = null;
    for (;;)
    {
      long l;
      try
      {
        paramCancellationSignal = paramContentProviderClient.openTypedAssetFileDescriptor(paramUri, "image/*", (Bundle)localObject, paramCancellationSignal);
        localCancellationSignal = paramCancellationSignal;
        paramUri = paramCancellationSignal.getFileDescriptor();
        localCancellationSignal = paramCancellationSignal;
        l = paramCancellationSignal.getStartOffset();
        paramContentProviderClient = null;
        localCancellationSignal = paramCancellationSignal;
        try
        {
          Os.lseek(paramUri, l, OsConstants.SEEK_SET);
          localCancellationSignal = paramCancellationSignal;
          localObject = new BitmapFactory.Options();
          localCancellationSignal = paramCancellationSignal;
          ((BitmapFactory.Options)localObject).inJustDecodeBounds = true;
          if (paramContentProviderClient != null)
          {
            localCancellationSignal = paramCancellationSignal;
            BitmapFactory.decodeStream(paramContentProviderClient, null, (BitmapFactory.Options)localObject);
            localCancellationSignal = paramCancellationSignal;
            i = ((BitmapFactory.Options)localObject).outWidth / paramPoint.x;
            localCancellationSignal = paramCancellationSignal;
            int j = ((BitmapFactory.Options)localObject).outHeight / paramPoint.y;
            localCancellationSignal = paramCancellationSignal;
            ((BitmapFactory.Options)localObject).inJustDecodeBounds = false;
            localCancellationSignal = paramCancellationSignal;
            ((BitmapFactory.Options)localObject).inSampleSize = Math.min(i, j);
            if (paramContentProviderClient == null) {
              break label335;
            }
            localCancellationSignal = paramCancellationSignal;
            paramContentProviderClient.reset();
            localCancellationSignal = paramCancellationSignal;
            paramContentProviderClient = BitmapFactory.decodeStream(paramContentProviderClient, null, (BitmapFactory.Options)localObject);
            localCancellationSignal = paramCancellationSignal;
            paramUri = paramCancellationSignal.getExtras();
            if (paramUri == null) {
              break label374;
            }
            localCancellationSignal = paramCancellationSignal;
            i = paramUri.getInt("android.provider.extra.ORIENTATION", 0);
            paramUri = paramContentProviderClient;
            if (i != 0)
            {
              localCancellationSignal = paramCancellationSignal;
              j = paramContentProviderClient.getWidth();
              localCancellationSignal = paramCancellationSignal;
              int k = paramContentProviderClient.getHeight();
              localCancellationSignal = paramCancellationSignal;
              paramUri = new Matrix();
              localCancellationSignal = paramCancellationSignal;
              paramUri.setRotate(i, j / 2, k / 2);
              localCancellationSignal = paramCancellationSignal;
              paramUri = Bitmap.createBitmap(paramContentProviderClient, 0, 0, j, k, paramUri, false);
            }
            return paramUri;
          }
        }
        catch (ErrnoException paramContentProviderClient)
        {
          localCancellationSignal = paramCancellationSignal;
          paramContentProviderClient = new BufferedInputStream(new FileInputStream(paramUri), 131072);
          localCancellationSignal = paramCancellationSignal;
          paramContentProviderClient.mark(131072);
          continue;
        }
        localCancellationSignal = paramCancellationSignal;
      }
      finally
      {
        IoUtils.closeQuietly(localCancellationSignal);
      }
      BitmapFactory.decodeFileDescriptor(paramUri, null, (BitmapFactory.Options)localObject);
      continue;
      label335:
      localCancellationSignal = paramCancellationSignal;
      try
      {
        Os.lseek(paramUri, l, OsConstants.SEEK_SET);
        localCancellationSignal = paramCancellationSignal;
        paramContentProviderClient = BitmapFactory.decodeFileDescriptor(paramUri, null, (BitmapFactory.Options)localObject);
      }
      catch (ErrnoException paramContentProviderClient)
      {
        for (;;)
        {
          localCancellationSignal = paramCancellationSignal;
          paramContentProviderClient.rethrowAsIOException();
        }
      }
      label374:
      int i = 0;
    }
  }
  
  public static Bitmap getDocumentThumbnail(ContentResolver paramContentResolver, Uri paramUri, Point paramPoint, CancellationSignal paramCancellationSignal)
  {
    paramContentResolver = paramContentResolver.acquireUnstableContentProviderClient(paramUri.getAuthority());
    try
    {
      paramPoint = getDocumentThumbnail(paramContentResolver, paramUri, paramPoint, paramCancellationSignal);
      return paramPoint;
    }
    catch (Exception paramPoint)
    {
      if (!(paramPoint instanceof OperationCanceledException)) {
        Log.w("DocumentsContract", "Failed to load thumbnail for " + paramUri + ": " + paramPoint);
      }
      return null;
    }
    finally
    {
      ContentProviderClient.releaseQuietly(paramContentResolver);
    }
  }
  
  public static String getPathDocument(ContentProviderClient paramContentProviderClient, Uri paramUri)
    throws RemoteException
  {
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("uri", paramUri);
    return paramContentProviderClient.call("android:getPathDocument", null, localBundle).getString("path");
  }
  
  public static String getPathDocument(ContentResolver paramContentResolver, Uri paramUri)
  {
    paramContentResolver = paramContentResolver.acquireUnstableContentProviderClient(paramUri.getAuthority());
    try
    {
      paramUri = getPathDocument(paramContentResolver, paramUri);
      return paramUri;
    }
    catch (Exception paramUri)
    {
      Log.w("DocumentsContract", "Failed to return the document path", paramUri);
      return null;
    }
    finally
    {
      ContentProviderClient.releaseQuietly(paramContentResolver);
    }
  }
  
  public static String getRootId(Uri paramUri)
  {
    List localList = paramUri.getPathSegments();
    if ((localList.size() >= 2) && ("root".equals(localList.get(0)))) {
      return (String)localList.get(1);
    }
    throw new IllegalArgumentException("Invalid URI: " + paramUri);
  }
  
  public static String getSearchDocumentsQuery(Uri paramUri)
  {
    return paramUri.getQueryParameter("query");
  }
  
  public static String getTreeDocumentId(Uri paramUri)
  {
    List localList = paramUri.getPathSegments();
    if ((localList.size() >= 2) && ("tree".equals(localList.get(0)))) {
      return (String)localList.get(1);
    }
    throw new IllegalArgumentException("Invalid URI: " + paramUri);
  }
  
  public static boolean isChildDocument(ContentProviderClient paramContentProviderClient, Uri paramUri1, Uri paramUri2)
    throws RemoteException
  {
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("uri", paramUri1);
    localBundle.putParcelable("android.content.extra.TARGET_URI", paramUri2);
    paramContentProviderClient = paramContentProviderClient.call("android:isChildDocument", null, localBundle);
    if (paramContentProviderClient == null) {
      throw new RemoteException("Failed to get a reponse from isChildDocument query.");
    }
    if (!paramContentProviderClient.containsKey("result")) {
      throw new RemoteException("Response did not include result field..");
    }
    return paramContentProviderClient.getBoolean("result");
  }
  
  public static boolean isContentUri(Uri paramUri)
  {
    if (paramUri != null) {
      return "content".equals(paramUri.getScheme());
    }
    return false;
  }
  
  public static boolean isDocumentUri(Context paramContext, Uri paramUri)
  {
    boolean bool = false;
    if ((isContentUri(paramUri)) && (isDocumentsProvider(paramContext, paramUri.getAuthority())))
    {
      paramContext = paramUri.getPathSegments();
      if (paramContext.size() == 2) {
        return "document".equals(paramContext.get(0));
      }
      if (paramContext.size() == 4)
      {
        if ("tree".equals(paramContext.get(0))) {
          bool = "document".equals(paramContext.get(2));
        }
        return bool;
      }
    }
    return false;
  }
  
  private static boolean isDocumentsProvider(Context paramContext, String paramString)
  {
    Intent localIntent = new Intent("android.content.action.DOCUMENTS_PROVIDER");
    paramContext = paramContext.getPackageManager().queryIntentContentProviders(localIntent, 0).iterator();
    while (paramContext.hasNext()) {
      if (paramString.equals(((ResolveInfo)paramContext.next()).providerInfo.authority)) {
        return true;
      }
    }
    return false;
  }
  
  public static boolean isManageMode(Uri paramUri)
  {
    return paramUri.getBooleanQueryParameter("manage", false);
  }
  
  public static boolean isRootUri(Context paramContext, Uri paramUri)
  {
    boolean bool = false;
    if ((isContentUri(paramUri)) && (isDocumentsProvider(paramContext, paramUri.getAuthority())))
    {
      paramContext = paramUri.getPathSegments();
      if (paramContext.size() == 2) {
        bool = "root".equals(paramContext.get(0));
      }
      return bool;
    }
    return false;
  }
  
  public static boolean isTreeUri(Uri paramUri)
  {
    boolean bool = false;
    paramUri = paramUri.getPathSegments();
    if (paramUri.size() >= 2) {
      bool = "tree".equals(paramUri.get(0));
    }
    return bool;
  }
  
  public static Uri moveDocument(ContentProviderClient paramContentProviderClient, Uri paramUri1, Uri paramUri2, Uri paramUri3)
    throws RemoteException
  {
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("uri", paramUri1);
    localBundle.putParcelable("parentUri", paramUri2);
    localBundle.putParcelable("android.content.extra.TARGET_URI", paramUri3);
    return (Uri)paramContentProviderClient.call("android:moveDocument", null, localBundle).getParcelable("uri");
  }
  
  public static Uri moveDocument(ContentResolver paramContentResolver, Uri paramUri1, Uri paramUri2, Uri paramUri3)
  {
    paramContentResolver = paramContentResolver.acquireUnstableContentProviderClient(paramUri1.getAuthority());
    try
    {
      paramUri1 = moveDocument(paramContentResolver, paramUri1, paramUri2, paramUri3);
      return paramUri1;
    }
    catch (Exception paramUri1)
    {
      Log.w("DocumentsContract", "Failed to move document", paramUri1);
      return null;
    }
    finally
    {
      ContentProviderClient.releaseQuietly(paramContentResolver);
    }
  }
  
  public static AssetFileDescriptor openImageThumbnail(File paramFile)
    throws FileNotFoundException
  {
    ParcelFileDescriptor localParcelFileDescriptor = ParcelFileDescriptor.open(paramFile, 268435456);
    Object localObject3 = null;
    Object localObject2 = null;
    Object localObject1 = localObject3;
    for (;;)
    {
      try
      {
        ExifInterface localExifInterface = new ExifInterface(paramFile.getAbsolutePath());
        paramFile = (File)localObject2;
        localObject1 = localObject3;
        switch (localExifInterface.getAttributeInt("Orientation", -1))
        {
        case 4: 
        case 5: 
        case 7: 
          localObject1 = paramFile;
          localObject2 = localExifInterface.getThumbnailRange();
          localObject1 = paramFile;
          if (localObject2 == null) {
            break label194;
          }
          localObject1 = paramFile;
          return new AssetFileDescriptor(localParcelFileDescriptor, localObject2[0], localObject2[1], paramFile);
        }
      }
      catch (IOException paramFile) {}
      localObject1 = localObject3;
      localObject2 = new Bundle(1);
      paramFile = (File)localObject2;
      try
      {
        ((Bundle)localObject2).putInt("android.provider.extra.ORIENTATION", 90);
        paramFile = (File)localObject2;
      }
      catch (IOException localIOException)
      {
        for (;;)
        {
          File localFile = paramFile;
        }
        paramFile = (File)localObject2;
      }
      localObject1 = localObject3;
      localObject2 = new Bundle(1);
      paramFile = (File)localObject2;
      ((Bundle)localObject2).putInt("android.provider.extra.ORIENTATION", 180);
      paramFile = (File)localObject2;
      continue;
      localObject1 = localObject3;
      localObject2 = new Bundle(1);
      paramFile = (File)localObject2;
      ((Bundle)localObject2).putInt("android.provider.extra.ORIENTATION", 270);
      paramFile = (File)localObject2;
      continue;
      label194:
      return new AssetFileDescriptor(localParcelFileDescriptor, 0L, -1L, (Bundle)localObject1);
    }
  }
  
  public static void removeDocument(ContentProviderClient paramContentProviderClient, Uri paramUri1, Uri paramUri2)
    throws RemoteException
  {
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("uri", paramUri1);
    localBundle.putParcelable("parentUri", paramUri2);
    paramContentProviderClient.call("android:removeDocument", null, localBundle);
  }
  
  public static boolean removeDocument(ContentResolver paramContentResolver, Uri paramUri1, Uri paramUri2)
  {
    paramContentResolver = paramContentResolver.acquireUnstableContentProviderClient(paramUri1.getAuthority());
    try
    {
      removeDocument(paramContentResolver, paramUri1, paramUri2);
      return true;
    }
    catch (Exception paramUri1)
    {
      Log.w("DocumentsContract", "Failed to remove document", paramUri1);
      return false;
    }
    finally
    {
      ContentProviderClient.releaseQuietly(paramContentResolver);
    }
  }
  
  public static Uri renameDocument(ContentProviderClient paramContentProviderClient, Uri paramUri, String paramString)
    throws RemoteException
  {
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("uri", paramUri);
    localBundle.putString("_display_name", paramString);
    paramContentProviderClient = (Uri)paramContentProviderClient.call("android:renameDocument", null, localBundle).getParcelable("uri");
    if (paramContentProviderClient != null) {
      return paramContentProviderClient;
    }
    return paramUri;
  }
  
  public static Uri renameDocument(ContentResolver paramContentResolver, Uri paramUri, String paramString)
  {
    paramContentResolver = paramContentResolver.acquireUnstableContentProviderClient(paramUri.getAuthority());
    try
    {
      paramUri = renameDocument(paramContentResolver, paramUri, paramString);
      return paramUri;
    }
    catch (Exception paramUri)
    {
      Log.w("DocumentsContract", "Failed to rename document", paramUri);
      return null;
    }
    finally
    {
      ContentProviderClient.releaseQuietly(paramContentResolver);
    }
  }
  
  public static Uri setManageMode(Uri paramUri)
  {
    return paramUri.buildUpon().appendQueryParameter("manage", "true").build();
  }
  
  public static final class Document
  {
    public static final String COLUMN_DISPLAY_NAME = "_display_name";
    public static final String COLUMN_DOCUMENT_ID = "document_id";
    public static final String COLUMN_FLAGS = "flags";
    public static final String COLUMN_ICON = "icon";
    public static final String COLUMN_LAST_MODIFIED = "last_modified";
    public static final String COLUMN_MIME_TYPE = "mime_type";
    public static final String COLUMN_SIZE = "_size";
    public static final String COLUMN_SUMMARY = "summary";
    public static final int FLAG_ARCHIVE = 32768;
    public static final int FLAG_DIR_PREFERS_GRID = 16;
    public static final int FLAG_DIR_PREFERS_LAST_MODIFIED = 32;
    public static final int FLAG_DIR_SUPPORTS_CREATE = 8;
    public static final int FLAG_PARTIAL = 65536;
    public static final int FLAG_SUPPORTS_COPY = 128;
    public static final int FLAG_SUPPORTS_DELETE = 4;
    public static final int FLAG_SUPPORTS_MOVE = 256;
    public static final int FLAG_SUPPORTS_REMOVE = 1024;
    public static final int FLAG_SUPPORTS_RENAME = 64;
    public static final int FLAG_SUPPORTS_THUMBNAIL = 1;
    public static final int FLAG_SUPPORTS_WRITE = 2;
    public static final int FLAG_VIRTUAL_DOCUMENT = 512;
    public static final String MIME_TYPE_DIR = "vnd.android.document/directory";
  }
  
  public static final class Root
  {
    public static final String COLUMN_AVAILABLE_BYTES = "available_bytes";
    public static final String COLUMN_CAPACITY_BYTES = "capacity_bytes";
    public static final String COLUMN_DOCUMENT_ID = "document_id";
    public static final String COLUMN_FLAGS = "flags";
    public static final String COLUMN_ICON = "icon";
    public static final String COLUMN_MIME_TYPES = "mime_types";
    public static final String COLUMN_ROOT_ID = "root_id";
    public static final String COLUMN_SUMMARY = "summary";
    public static final String COLUMN_TITLE = "title";
    public static final int FLAG_ADVANCED = 131072;
    public static final int FLAG_EMPTY = 65536;
    public static final int FLAG_HAS_SETTINGS = 262144;
    public static final int FLAG_LOCAL_ONLY = 2;
    public static final int FLAG_REMOVABLE_SD = 524288;
    public static final int FLAG_REMOVABLE_USB = 1048576;
    public static final int FLAG_SUPPORTS_CREATE = 1;
    public static final int FLAG_SUPPORTS_IS_CHILD = 16;
    public static final int FLAG_SUPPORTS_RECENTS = 4;
    public static final int FLAG_SUPPORTS_SEARCH = 8;
    public static final String MIME_TYPE_ITEM = "vnd.android.document/root";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/provider/DocumentsContract.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */