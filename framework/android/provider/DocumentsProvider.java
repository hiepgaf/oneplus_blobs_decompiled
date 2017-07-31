package android.provider;

import android.content.ClipDescription;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.ProviderInfo;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import java.io.FileNotFoundException;
import java.util.Objects;
import libcore.io.IoUtils;

public abstract class DocumentsProvider
  extends ContentProvider
{
  private static final int MATCH_CHILDREN = 6;
  private static final int MATCH_CHILDREN_TREE = 8;
  private static final int MATCH_DOCUMENT = 5;
  private static final int MATCH_DOCUMENT_TREE = 7;
  private static final int MATCH_RECENT = 3;
  private static final int MATCH_ROOT = 2;
  private static final int MATCH_ROOTS = 1;
  private static final int MATCH_SEARCH = 4;
  private static final String TAG = "DocumentsProvider";
  private String mAuthority;
  private UriMatcher mMatcher;
  
  private Bundle callUnchecked(String paramString1, String paramString2, Bundle paramBundle)
    throws FileNotFoundException
  {
    Context localContext = getContext();
    Uri localUri = (Uri)paramBundle.getParcelable("uri");
    paramString2 = localUri.getAuthority();
    String str1 = DocumentsContract.getDocumentId(localUri);
    if (!this.mAuthority.equals(paramString2)) {
      throw new SecurityException("Requested authority " + paramString2 + " doesn't match provider " + this.mAuthority);
    }
    paramString2 = new Bundle();
    enforceTree(localUri);
    boolean bool;
    if ("android:isChildDocument".equals(paramString1))
    {
      enforceReadPermissionInner(localUri, getCallingPackage(), null);
      paramBundle = (Uri)paramBundle.getParcelable("android.content.extra.TARGET_URI");
      paramString1 = paramBundle.getAuthority();
      paramBundle = DocumentsContract.getDocumentId(paramBundle);
      if (this.mAuthority.equals(paramString1))
      {
        bool = isChildDocument(str1, paramBundle);
        paramString2.putBoolean("result", bool);
      }
    }
    int i;
    label311:
    label449:
    do
    {
      do
      {
        do
        {
          return paramString2;
          bool = false;
          break;
          if ("android:createDocument".equals(paramString1))
          {
            enforceWritePermissionInner(localUri, getCallingPackage(), null);
            paramString2.putParcelable("uri", DocumentsContract.buildDocumentUriMaybeUsingTree(localUri, createDocument(str1, paramBundle.getString("mime_type"), paramBundle.getString("_display_name"))));
            return paramString2;
          }
          if (!"android:renameDocument".equals(paramString1)) {
            break label311;
          }
          enforceWritePermissionInner(localUri, getCallingPackage(), null);
          paramString1 = renameDocument(str1, paramBundle.getString("_display_name"));
        } while (paramString1 == null);
        paramString1 = DocumentsContract.buildDocumentUriMaybeUsingTree(localUri, paramString1);
        if (!DocumentsContract.isTreeUri(paramString1))
        {
          i = getCallingOrSelfUriPermissionModeFlags(localContext, localUri);
          localContext.grantUriPermission(getCallingPackage(), paramString1, i);
        }
        paramString2.putParcelable("uri", paramString1);
        revokeDocumentPermission(str1);
        return paramString2;
        if ("android:deleteDocument".equals(paramString1))
        {
          enforceWritePermissionInner(localUri, getCallingPackage(), null);
          deleteDocument(str1);
          revokeDocumentPermission(str1);
          return paramString2;
        }
        if (!"android:copyDocument".equals(paramString1)) {
          break label449;
        }
        paramString1 = (Uri)paramBundle.getParcelable("android.content.extra.TARGET_URI");
        paramBundle = DocumentsContract.getDocumentId(paramString1);
        enforceReadPermissionInner(localUri, getCallingPackage(), null);
        enforceWritePermissionInner(paramString1, getCallingPackage(), null);
        paramString1 = copyDocument(str1, paramBundle);
      } while (paramString1 == null);
      paramString1 = DocumentsContract.buildDocumentUriMaybeUsingTree(localUri, paramString1);
      if (!DocumentsContract.isTreeUri(paramString1))
      {
        i = getCallingOrSelfUriPermissionModeFlags(localContext, localUri);
        localContext.grantUriPermission(getCallingPackage(), paramString1, i);
      }
      paramString2.putParcelable("uri", paramString1);
      return paramString2;
      if (!"android:moveDocument".equals(paramString1)) {
        break label583;
      }
      paramString1 = (Uri)paramBundle.getParcelable("parentUri");
      String str2 = DocumentsContract.getDocumentId(paramString1);
      paramBundle = (Uri)paramBundle.getParcelable("android.content.extra.TARGET_URI");
      String str3 = DocumentsContract.getDocumentId(paramBundle);
      enforceWritePermissionInner(localUri, getCallingPackage(), null);
      enforceReadPermissionInner(paramString1, getCallingPackage(), null);
      enforceWritePermissionInner(paramBundle, getCallingPackage(), null);
      paramString1 = moveDocument(str1, str2, str3);
    } while (paramString1 == null);
    paramString1 = DocumentsContract.buildDocumentUriMaybeUsingTree(localUri, paramString1);
    if (!DocumentsContract.isTreeUri(paramString1))
    {
      i = getCallingOrSelfUriPermissionModeFlags(localContext, localUri);
      localContext.grantUriPermission(getCallingPackage(), paramString1, i);
    }
    paramString2.putParcelable("uri", paramString1);
    return paramString2;
    label583:
    if ("android:removeDocument".equals(paramString1))
    {
      paramString1 = (Uri)paramBundle.getParcelable("parentUri");
      paramBundle = DocumentsContract.getDocumentId(paramString1);
      enforceReadPermissionInner(paramString1, getCallingPackage(), null);
      enforceWritePermissionInner(localUri, getCallingPackage(), null);
      removeDocument(str1, paramBundle);
      return paramString2;
    }
    if ("android:getPathDocument".equals(paramString1))
    {
      enforceWritePermissionInner(localUri, getCallingPackage(), null);
      paramString2.putString("path", getPathDocument(str1));
      return paramString2;
    }
    throw new UnsupportedOperationException("Method not supported " + paramString1);
  }
  
  private void enforceTree(Uri paramUri)
  {
    if (DocumentsContract.isTreeUri(paramUri))
    {
      String str = DocumentsContract.getTreeDocumentId(paramUri);
      paramUri = DocumentsContract.getDocumentId(paramUri);
      if (Objects.equals(str, paramUri)) {
        return;
      }
      if (!isChildDocument(str, paramUri)) {
        throw new SecurityException("Document " + paramUri + " is not a descendant of " + str);
      }
    }
  }
  
  private static int getCallingOrSelfUriPermissionModeFlags(Context paramContext, Uri paramUri)
  {
    int j = 0;
    if (paramContext.checkCallingOrSelfUriPermission(paramUri, 1) == 0) {
      j = 1;
    }
    int i = j;
    if (paramContext.checkCallingOrSelfUriPermission(paramUri, 2) == 0) {
      i = j | 0x2;
    }
    j = i;
    if (paramContext.checkCallingOrSelfUriPermission(paramUri, 65) == 0) {
      j = i | 0x40;
    }
    return j;
  }
  
  public static boolean mimeTypeMatches(String paramString1, String paramString2)
  {
    if (paramString2 == null) {
      return false;
    }
    if ((paramString1 == null) || ("*/*".equals(paramString1))) {
      return true;
    }
    if (paramString1.equals(paramString2)) {
      return true;
    }
    if (paramString1.endsWith("/*")) {
      return paramString1.regionMatches(0, paramString2, 0, paramString1.indexOf('/'));
    }
    return false;
  }
  
  private final AssetFileDescriptor openTypedAssetFileImpl(Uri paramUri, String paramString, Bundle paramBundle, CancellationSignal paramCancellationSignal)
    throws FileNotFoundException
  {
    enforceTree(paramUri);
    String str1 = DocumentsContract.getDocumentId(paramUri);
    if ((paramBundle != null) && (paramBundle.containsKey("android.content.extra.SIZE"))) {
      return openDocumentThumbnail(str1, (Point)paramBundle.getParcelable("android.content.extra.SIZE"), paramCancellationSignal);
    }
    if ("*/*".equals(paramString)) {
      return openAssetFile(paramUri, "r");
    }
    String str2 = getType(paramUri);
    if ((str2 != null) && (ClipDescription.compareMimeTypes(str2, paramString))) {
      return openAssetFile(paramUri, "r");
    }
    return openTypedDocument(str1, paramString, paramBundle, paramCancellationSignal);
  }
  
  public void attachInfo(Context paramContext, ProviderInfo paramProviderInfo)
  {
    this.mAuthority = paramProviderInfo.authority;
    this.mMatcher = new UriMatcher(-1);
    this.mMatcher.addURI(this.mAuthority, "root", 1);
    this.mMatcher.addURI(this.mAuthority, "root/*", 2);
    this.mMatcher.addURI(this.mAuthority, "root/*/recent", 3);
    this.mMatcher.addURI(this.mAuthority, "root/*/search", 4);
    this.mMatcher.addURI(this.mAuthority, "document/*", 5);
    this.mMatcher.addURI(this.mAuthority, "document/*/children", 6);
    this.mMatcher.addURI(this.mAuthority, "tree/*/document/*", 7);
    this.mMatcher.addURI(this.mAuthority, "tree/*/document/*/children", 8);
    if (!paramProviderInfo.exported) {
      throw new SecurityException("Provider must be exported");
    }
    if (!paramProviderInfo.grantUriPermissions) {
      throw new SecurityException("Provider must grantUriPermissions");
    }
    if (("android.permission.MANAGE_DOCUMENTS".equals(paramProviderInfo.readPermission)) && ("android.permission.MANAGE_DOCUMENTS".equals(paramProviderInfo.writePermission)))
    {
      super.attachInfo(paramContext, paramProviderInfo);
      return;
    }
    throw new SecurityException("Provider must be protected by MANAGE_DOCUMENTS");
  }
  
  public Bundle call(String paramString1, String paramString2, Bundle paramBundle)
  {
    if (!paramString1.startsWith("android:")) {
      return super.call(paramString1, paramString2, paramBundle);
    }
    try
    {
      paramString2 = callUnchecked(paramString1, paramString2, paramBundle);
      return paramString2;
    }
    catch (FileNotFoundException paramString2)
    {
      throw new IllegalStateException("Failed call " + paramString1, paramString2);
    }
  }
  
  public Uri canonicalize(Uri paramUri)
  {
    Context localContext = getContext();
    switch (this.mMatcher.match(paramUri))
    {
    default: 
      return null;
    }
    enforceTree(paramUri);
    Uri localUri = DocumentsContract.buildDocumentUri(paramUri.getAuthority(), DocumentsContract.getDocumentId(paramUri));
    int i = getCallingOrSelfUriPermissionModeFlags(localContext, paramUri);
    localContext.grantUriPermission(getCallingPackage(), localUri, i);
    return localUri;
  }
  
  public String copyDocument(String paramString1, String paramString2)
    throws FileNotFoundException
  {
    throw new UnsupportedOperationException("Copy not supported");
  }
  
  public String createDocument(String paramString1, String paramString2, String paramString3)
    throws FileNotFoundException
  {
    throw new UnsupportedOperationException("Create not supported");
  }
  
  public final int delete(Uri paramUri, String paramString, String[] paramArrayOfString)
  {
    throw new UnsupportedOperationException("Delete not supported");
  }
  
  public void deleteDocument(String paramString)
    throws FileNotFoundException
  {
    throw new UnsupportedOperationException("Delete not supported");
  }
  
  public String[] getDocumentStreamTypes(String paramString1, String paramString2)
  {
    String str2 = null;
    String str1 = null;
    try
    {
      paramString1 = queryDocument(paramString1, null);
      str1 = paramString1;
      str2 = paramString1;
      if (paramString1.moveToFirst())
      {
        str1 = paramString1;
        str2 = paramString1;
        String str3 = paramString1.getString(paramString1.getColumnIndexOrThrow("mime_type"));
        str1 = paramString1;
        str2 = paramString1;
        if (((0x200 & paramString1.getLong(paramString1.getColumnIndexOrThrow("flags"))) == 0L) && (str3 != null))
        {
          str1 = paramString1;
          str2 = paramString1;
          if (mimeTypeMatches(paramString2, str3))
          {
            IoUtils.closeQuietly(paramString1);
            return new String[] { str3 };
          }
        }
      }
      return null;
    }
    catch (FileNotFoundException paramString1)
    {
      return null;
    }
    finally
    {
      IoUtils.closeQuietly(str2);
    }
  }
  
  public String getDocumentType(String paramString)
    throws FileNotFoundException
  {
    paramString = queryDocument(paramString, null);
    try
    {
      if (paramString.moveToFirst())
      {
        String str = paramString.getString(paramString.getColumnIndexOrThrow("mime_type"));
        return str;
      }
      return null;
    }
    finally
    {
      IoUtils.closeQuietly(paramString);
    }
  }
  
  public String getPathDocument(String paramString)
  {
    return null;
  }
  
  public String[] getStreamTypes(Uri paramUri, String paramString)
  {
    enforceTree(paramUri);
    return getDocumentStreamTypes(DocumentsContract.getDocumentId(paramUri), paramString);
  }
  
  public final String getType(Uri paramUri)
  {
    try
    {
      switch (this.mMatcher.match(paramUri))
      {
      case 5: 
      case 7: 
        enforceTree(paramUri);
        paramUri = getDocumentType(DocumentsContract.getDocumentId(paramUri));
        return paramUri;
      }
    }
    catch (FileNotFoundException paramUri)
    {
      Log.w("DocumentsProvider", "Failed during getType", paramUri);
      return null;
    }
    return null;
    return "vnd.android.document/root";
  }
  
  public final Uri insert(Uri paramUri, ContentValues paramContentValues)
  {
    throw new UnsupportedOperationException("Insert not supported");
  }
  
  public boolean isChildDocument(String paramString1, String paramString2)
  {
    return false;
  }
  
  public String moveDocument(String paramString1, String paramString2, String paramString3)
    throws FileNotFoundException
  {
    throw new UnsupportedOperationException("Move not supported");
  }
  
  public final AssetFileDescriptor openAssetFile(Uri paramUri, String paramString)
    throws FileNotFoundException
  {
    Object localObject = null;
    enforceTree(paramUri);
    paramString = openDocument(DocumentsContract.getDocumentId(paramUri), paramString, null);
    paramUri = (Uri)localObject;
    if (paramString != null) {
      paramUri = new AssetFileDescriptor(paramString, 0L, -1L);
    }
    return paramUri;
  }
  
  public final AssetFileDescriptor openAssetFile(Uri paramUri, String paramString, CancellationSignal paramCancellationSignal)
    throws FileNotFoundException
  {
    Object localObject = null;
    enforceTree(paramUri);
    paramString = openDocument(DocumentsContract.getDocumentId(paramUri), paramString, paramCancellationSignal);
    paramUri = (Uri)localObject;
    if (paramString != null) {
      paramUri = new AssetFileDescriptor(paramString, 0L, -1L);
    }
    return paramUri;
  }
  
  public abstract ParcelFileDescriptor openDocument(String paramString1, String paramString2, CancellationSignal paramCancellationSignal)
    throws FileNotFoundException;
  
  public AssetFileDescriptor openDocumentThumbnail(String paramString, Point paramPoint, CancellationSignal paramCancellationSignal)
    throws FileNotFoundException
  {
    throw new UnsupportedOperationException("Thumbnails not supported");
  }
  
  public final ParcelFileDescriptor openFile(Uri paramUri, String paramString)
    throws FileNotFoundException
  {
    enforceTree(paramUri);
    return openDocument(DocumentsContract.getDocumentId(paramUri), paramString, null);
  }
  
  public final ParcelFileDescriptor openFile(Uri paramUri, String paramString, CancellationSignal paramCancellationSignal)
    throws FileNotFoundException
  {
    enforceTree(paramUri);
    return openDocument(DocumentsContract.getDocumentId(paramUri), paramString, paramCancellationSignal);
  }
  
  public final AssetFileDescriptor openTypedAssetFile(Uri paramUri, String paramString, Bundle paramBundle)
    throws FileNotFoundException
  {
    return openTypedAssetFileImpl(paramUri, paramString, paramBundle, null);
  }
  
  public final AssetFileDescriptor openTypedAssetFile(Uri paramUri, String paramString, Bundle paramBundle, CancellationSignal paramCancellationSignal)
    throws FileNotFoundException
  {
    return openTypedAssetFileImpl(paramUri, paramString, paramBundle, paramCancellationSignal);
  }
  
  public AssetFileDescriptor openTypedDocument(String paramString1, String paramString2, Bundle paramBundle, CancellationSignal paramCancellationSignal)
    throws FileNotFoundException
  {
    throw new FileNotFoundException("The requested MIME type is not supported.");
  }
  
  public final Cursor query(Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2)
  {
    for (;;)
    {
      try
      {
        switch (this.mMatcher.match(paramUri))
        {
        case 2: 
          throw new UnsupportedOperationException("Unsupported Uri " + paramUri);
        }
      }
      catch (FileNotFoundException paramUri)
      {
        Log.w("DocumentsProvider", "Failed during query", paramUri);
        return null;
      }
      return queryRoots(paramArrayOfString1);
      return queryRecentDocuments(DocumentsContract.getRootId(paramUri), paramArrayOfString1);
      return querySearchDocuments(DocumentsContract.getRootId(paramUri), DocumentsContract.getSearchDocumentsQuery(paramUri), paramArrayOfString1);
      enforceTree(paramUri);
      return queryDocument(DocumentsContract.getDocumentId(paramUri), paramArrayOfString1);
      enforceTree(paramUri);
      if (DocumentsContract.isManageMode(paramUri)) {
        return queryChildDocumentsForManage(DocumentsContract.getDocumentId(paramUri), paramArrayOfString1, paramString2);
      }
      paramUri = queryChildDocuments(DocumentsContract.getDocumentId(paramUri), paramArrayOfString1, paramString2);
      return paramUri;
    }
  }
  
  public abstract Cursor queryChildDocuments(String paramString1, String[] paramArrayOfString, String paramString2)
    throws FileNotFoundException;
  
  public Cursor queryChildDocumentsForManage(String paramString1, String[] paramArrayOfString, String paramString2)
    throws FileNotFoundException
  {
    throw new UnsupportedOperationException("Manage not supported");
  }
  
  public abstract Cursor queryDocument(String paramString, String[] paramArrayOfString)
    throws FileNotFoundException;
  
  public Cursor queryRecentDocuments(String paramString, String[] paramArrayOfString)
    throws FileNotFoundException
  {
    throw new UnsupportedOperationException("Recent not supported");
  }
  
  public abstract Cursor queryRoots(String[] paramArrayOfString)
    throws FileNotFoundException;
  
  public Cursor querySearchDocuments(String paramString1, String paramString2, String[] paramArrayOfString)
    throws FileNotFoundException
  {
    throw new UnsupportedOperationException("Search not supported");
  }
  
  public void removeDocument(String paramString1, String paramString2)
    throws FileNotFoundException
  {
    throw new UnsupportedOperationException("Remove not supported");
  }
  
  public String renameDocument(String paramString1, String paramString2)
    throws FileNotFoundException
  {
    throw new UnsupportedOperationException("Rename not supported");
  }
  
  public final void revokeDocumentPermission(String paramString)
  {
    Context localContext = getContext();
    localContext.revokeUriPermission(DocumentsContract.buildDocumentUri(this.mAuthority, paramString), -1);
    localContext.revokeUriPermission(DocumentsContract.buildTreeDocumentUri(this.mAuthority, paramString), -1);
  }
  
  public final int update(Uri paramUri, ContentValues paramContentValues, String paramString, String[] paramArrayOfString)
  {
    throw new UnsupportedOperationException("Update not supported");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/provider/DocumentsProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */