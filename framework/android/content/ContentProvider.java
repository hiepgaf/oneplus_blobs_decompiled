package android.content;

import android.app.AppOpsManager;
import android.content.pm.PathPermission;
import android.content.pm.ProviderInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.IBinder;
import android.os.ICancellationSignal;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class ContentProvider
  implements ComponentCallbacks2
{
  private static final boolean DBG = Build.DEBUG_ONEPLUS | DBG_ALL;
  private static final boolean DBG_ALL = ContentDebugUtils.DBG_ALL;
  private static final boolean DBG_DUMP_STACK = ContentDebugUtils.DBG_DUMP_STACK;
  private static final boolean SAVE_DBG_MSG = ContentDebugUtils.SAVE_DBG_MSG;
  private static final String TAG = "ContentProvider";
  private String[] mAuthorities;
  private String mAuthority;
  private final ThreadLocal<String> mCallingPackage = new ThreadLocal();
  private Context mContext = null;
  private boolean mExported;
  private int mMyUid;
  private boolean mNoPerms;
  private PathPermission[] mPathPermissions;
  private String mReadPermission;
  private boolean mSingleUser;
  private Transport mTransport = new Transport();
  private String mWritePermission;
  
  public ContentProvider() {}
  
  public ContentProvider(Context paramContext, String paramString1, String paramString2, PathPermission[] paramArrayOfPathPermission)
  {
    this.mContext = paramContext;
    this.mReadPermission = paramString1;
    this.mWritePermission = paramString2;
    this.mPathPermissions = paramArrayOfPathPermission;
  }
  
  private void attachInfo(Context paramContext, ProviderInfo paramProviderInfo, boolean paramBoolean)
  {
    this.mNoPerms = paramBoolean;
    if (this.mContext == null)
    {
      this.mContext = paramContext;
      if (paramContext != null) {
        this.mTransport.mAppOpsManager = ((AppOpsManager)paramContext.getSystemService("appops"));
      }
      this.mMyUid = Process.myUid();
      if (paramProviderInfo != null)
      {
        setReadPermission(paramProviderInfo.readPermission);
        setWritePermission(paramProviderInfo.writePermission);
        setPathPermissions(paramProviderInfo.pathPermissions);
        this.mExported = paramProviderInfo.exported;
        if ((paramProviderInfo.flags & 0x40000000) == 0) {
          break label111;
        }
      }
    }
    label111:
    for (paramBoolean = true;; paramBoolean = false)
    {
      this.mSingleUser = paramBoolean;
      setAuthorities(paramProviderInfo.authority);
      onCreate();
      return;
    }
  }
  
  private int checkPermissionAndAppOp(String paramString1, String paramString2, IBinder paramIBinder)
  {
    if (getContext().checkPermission(paramString1, Binder.getCallingPid(), Binder.getCallingUid(), paramIBinder) != 0) {
      return 2;
    }
    int i = AppOpsManager.permissionToOpCode(paramString1);
    if (i != -1) {
      return this.mTransport.mAppOpsManager.noteProxyOp(i, paramString2);
    }
    return 0;
  }
  
  public static ContentProvider coerceToLocalContentProvider(IContentProvider paramIContentProvider)
  {
    if ((paramIContentProvider instanceof Transport)) {
      return ((Transport)paramIContentProvider).getContentProvider();
    }
    return null;
  }
  
  public static String getAuthorityWithoutUserId(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    return paramString.substring(paramString.lastIndexOf('@') + 1);
  }
  
  public static Uri getUriWithoutUserId(Uri paramUri)
  {
    if (paramUri == null) {
      return null;
    }
    Uri.Builder localBuilder = paramUri.buildUpon();
    localBuilder.authority(getAuthorityWithoutUserId(paramUri.getAuthority()));
    return localBuilder.build();
  }
  
  public static int getUserIdFromAuthority(String paramString)
  {
    return getUserIdFromAuthority(paramString, -2);
  }
  
  public static int getUserIdFromAuthority(String paramString, int paramInt)
  {
    if (paramString == null) {
      return paramInt;
    }
    int i = paramString.lastIndexOf('@');
    if (i == -1) {
      return paramInt;
    }
    paramString = paramString.substring(0, i);
    try
    {
      paramInt = Integer.parseInt(paramString);
      return paramInt;
    }
    catch (NumberFormatException paramString)
    {
      Log.w("ContentProvider", "Error parsing userId.", paramString);
    }
    return 55536;
  }
  
  public static int getUserIdFromUri(Uri paramUri)
  {
    return getUserIdFromUri(paramUri, -2);
  }
  
  public static int getUserIdFromUri(Uri paramUri, int paramInt)
  {
    if (paramUri == null) {
      return paramInt;
    }
    return getUserIdFromAuthority(paramUri.getAuthority(), paramInt);
  }
  
  public static Uri maybeAddUserId(Uri paramUri, int paramInt)
  {
    if (paramUri == null) {
      return null;
    }
    if ((paramInt != -2) && ("content".equals(paramUri.getScheme())) && (!uriHasUserId(paramUri)))
    {
      Uri.Builder localBuilder = paramUri.buildUpon();
      localBuilder.encodedAuthority("" + paramInt + "@" + paramUri.getEncodedAuthority());
      return localBuilder.build();
    }
    return paramUri;
  }
  
  private String setCallingPackage(String paramString)
  {
    String str = (String)this.mCallingPackage.get();
    this.mCallingPackage.set(paramString);
    return str;
  }
  
  public static boolean uriHasUserId(Uri paramUri)
  {
    if (paramUri == null) {
      return false;
    }
    return !TextUtils.isEmpty(paramUri.getUserInfo());
  }
  
  private void validateIncomingUri(Uri paramUri)
    throws SecurityException
  {
    String str = paramUri.getAuthority();
    int i = getUserIdFromAuthority(str, -2);
    if ((i != -2) && (i != this.mContext.getUserId())) {
      throw new SecurityException("trying to query a ContentProvider in user " + this.mContext.getUserId() + " with a uri belonging to user " + i);
    }
    if (!matchesOurAuthorities(getAuthorityWithoutUserId(str)))
    {
      paramUri = "The authority of the uri " + paramUri + " does not match the one of the " + "contentProvider: ";
      if (this.mAuthority != null) {}
      for (paramUri = paramUri + this.mAuthority;; paramUri = paramUri + Arrays.toString(this.mAuthorities)) {
        throw new SecurityException(paramUri);
      }
    }
  }
  
  public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> paramArrayList)
    throws OperationApplicationException
  {
    int j = paramArrayList.size();
    ContentProviderResult[] arrayOfContentProviderResult = new ContentProviderResult[j];
    int i = 0;
    while (i < j)
    {
      arrayOfContentProviderResult[i] = ((ContentProviderOperation)paramArrayList.get(i)).apply(this, arrayOfContentProviderResult, i);
      i += 1;
    }
    return arrayOfContentProviderResult;
  }
  
  public void attachInfo(Context paramContext, ProviderInfo paramProviderInfo)
  {
    attachInfo(paramContext, paramProviderInfo, false);
  }
  
  public void attachInfoForTesting(Context paramContext, ProviderInfo paramProviderInfo)
  {
    attachInfo(paramContext, paramProviderInfo, true);
  }
  
  public int bulkInsert(Uri paramUri, ContentValues[] paramArrayOfContentValues)
  {
    int j = paramArrayOfContentValues.length;
    int i = 0;
    while (i < j)
    {
      insert(paramUri, paramArrayOfContentValues[i]);
      i += 1;
    }
    return j;
  }
  
  public Bundle call(String paramString1, String paramString2, Bundle paramBundle)
  {
    return null;
  }
  
  public Uri canonicalize(Uri paramUri)
  {
    return null;
  }
  
  boolean checkUser(int paramInt1, int paramInt2, Context paramContext)
  {
    return (UserHandle.getUserId(paramInt2) == paramContext.getUserId()) || (this.mSingleUser) || (paramContext.checkPermission("android.permission.INTERACT_ACROSS_USERS", paramInt1, paramInt2) == 0);
  }
  
  public abstract int delete(Uri paramUri, String paramString, String[] paramArrayOfString);
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.println("nothing to dump");
  }
  
  protected int enforceReadPermissionInner(Uri paramUri, String paramString, IBinder paramIBinder)
    throws SecurityException
  {
    Context localContext = getContext();
    int i1 = Binder.getCallingPid();
    int i2 = Binder.getCallingUid();
    String str1 = null;
    Object localObject1 = null;
    int j = 0;
    int i = 0;
    if (UserHandle.isSameApp(i2, this.mMyUid)) {
      return 0;
    }
    Object localObject2 = str1;
    int k = j;
    if (this.mExported)
    {
      localObject2 = str1;
      k = j;
      if (checkUser(i1, i2, localContext))
      {
        localObject2 = getReadPermission();
        if (localObject2 != null)
        {
          i = checkPermissionAndAppOp((String)localObject2, paramString, paramIBinder);
          if (i == 0) {
            return 0;
          }
          localObject1 = localObject2;
          i = Math.max(0, i);
        }
        PathPermission[] arrayOfPathPermission;
        int n;
        String str2;
        int m;
        int i3;
        if (localObject2 == null)
        {
          j = 1;
          arrayOfPathPermission = getPathPermissions();
          n = j;
          localObject2 = localObject1;
          k = i;
          if (arrayOfPathPermission != null)
          {
            str2 = paramUri.getPath();
            m = 0;
            i3 = arrayOfPathPermission.length;
          }
        }
        else
        {
          for (;;)
          {
            n = j;
            localObject2 = localObject1;
            k = i;
            if (m >= i3) {
              break label295;
            }
            PathPermission localPathPermission = arrayOfPathPermission[m];
            str1 = localPathPermission.getReadPermission();
            n = j;
            localObject2 = localObject1;
            k = i;
            if (str1 != null)
            {
              n = j;
              localObject2 = localObject1;
              k = i;
              if (localPathPermission.match(str2))
              {
                j = checkPermissionAndAppOp(str1, paramString, paramIBinder);
                if (j == 0)
                {
                  return 0;
                  j = 0;
                  break;
                }
                n = 0;
                localObject2 = str1;
                k = Math.max(i, j);
              }
            }
            m += 1;
            j = n;
            localObject1 = localObject2;
            i = k;
          }
        }
        label295:
        if (n != 0) {
          return 0;
        }
      }
    }
    i = UserHandle.getUserId(i2);
    if ((!this.mSingleUser) || (UserHandle.isSameUser(this.mMyUid, i2))) {}
    for (paramString = paramUri; localContext.checkUriPermission(paramString, i1, i2, 1, paramIBinder) == 0; paramString = maybeAddUserId(paramUri, i)) {
      return 0;
    }
    if (k == 1) {
      return 1;
    }
    if (this.mExported) {}
    for (paramString = " requires " + (String)localObject2 + ", or grantUriPermission()";; paramString = " requires the provider be exported, or grantUriPermission()") {
      throw new SecurityException("Permission Denial: reading " + getClass().getName() + " uri " + paramUri + " from pid=" + i1 + ", uid=" + i2 + paramString);
    }
  }
  
  protected int enforceWritePermissionInner(Uri paramUri, String paramString, IBinder paramIBinder)
    throws SecurityException
  {
    Context localContext = getContext();
    int i1 = Binder.getCallingPid();
    int i2 = Binder.getCallingUid();
    String str1 = null;
    Object localObject1 = null;
    int j = 0;
    int i = 0;
    if (UserHandle.isSameApp(i2, this.mMyUid)) {
      return 0;
    }
    Object localObject2 = str1;
    int k = j;
    if (this.mExported)
    {
      localObject2 = str1;
      k = j;
      if (checkUser(i1, i2, localContext))
      {
        localObject2 = getWritePermission();
        if (localObject2 != null)
        {
          i = checkPermissionAndAppOp((String)localObject2, paramString, paramIBinder);
          if (i == 0) {
            return 0;
          }
          localObject1 = localObject2;
          i = Math.max(0, i);
        }
        PathPermission[] arrayOfPathPermission;
        int n;
        String str2;
        int m;
        int i3;
        if (localObject2 == null)
        {
          j = 1;
          arrayOfPathPermission = getPathPermissions();
          n = j;
          localObject2 = localObject1;
          k = i;
          if (arrayOfPathPermission != null)
          {
            str2 = paramUri.getPath();
            m = 0;
            i3 = arrayOfPathPermission.length;
          }
        }
        else
        {
          for (;;)
          {
            n = j;
            localObject2 = localObject1;
            k = i;
            if (m >= i3) {
              break label295;
            }
            PathPermission localPathPermission = arrayOfPathPermission[m];
            str1 = localPathPermission.getWritePermission();
            n = j;
            localObject2 = localObject1;
            k = i;
            if (str1 != null)
            {
              n = j;
              localObject2 = localObject1;
              k = i;
              if (localPathPermission.match(str2))
              {
                j = checkPermissionAndAppOp(str1, paramString, paramIBinder);
                if (j == 0)
                {
                  return 0;
                  j = 0;
                  break;
                }
                n = 0;
                localObject2 = str1;
                k = Math.max(i, j);
              }
            }
            m += 1;
            j = n;
            localObject1 = localObject2;
            i = k;
          }
        }
        label295:
        if (n != 0) {
          return 0;
        }
      }
    }
    if (localContext.checkUriPermission(paramUri, i1, i2, 2, paramIBinder) == 0) {
      return 0;
    }
    if (k == 1) {
      return 1;
    }
    if (this.mExported) {}
    for (paramString = " requires " + (String)localObject2 + ", or grantUriPermission()";; paramString = " requires the provider be exported, or grantUriPermission()") {
      throw new SecurityException("Permission Denial: writing " + getClass().getName() + " uri " + paramUri + " from pid=" + i1 + ", uid=" + i2 + paramString);
    }
  }
  
  public AppOpsManager getAppOpsManager()
  {
    return this.mTransport.mAppOpsManager;
  }
  
  public final String getCallingPackage()
  {
    String str = (String)this.mCallingPackage.get();
    if (str != null) {
      this.mTransport.mAppOpsManager.checkPackage(Binder.getCallingUid(), str);
    }
    return str;
  }
  
  public final Context getContext()
  {
    return this.mContext;
  }
  
  public IContentProvider getIContentProvider()
  {
    return this.mTransport;
  }
  
  public final PathPermission[] getPathPermissions()
  {
    return this.mPathPermissions;
  }
  
  public final String getReadPermission()
  {
    return this.mReadPermission;
  }
  
  public String[] getStreamTypes(Uri paramUri, String paramString)
  {
    return null;
  }
  
  public abstract String getType(Uri paramUri);
  
  public final String getWritePermission()
  {
    return this.mWritePermission;
  }
  
  public abstract Uri insert(Uri paramUri, ContentValues paramContentValues);
  
  protected boolean isTemporary()
  {
    return false;
  }
  
  protected final boolean matchesOurAuthorities(String paramString)
  {
    if (this.mAuthority != null) {
      return this.mAuthority.equals(paramString);
    }
    if (this.mAuthorities != null)
    {
      int j = this.mAuthorities.length;
      int i = 0;
      while (i < j)
      {
        if (this.mAuthorities[i].equals(paramString)) {
          return true;
        }
        i += 1;
      }
    }
    return false;
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration) {}
  
  public abstract boolean onCreate();
  
  public void onLowMemory() {}
  
  public void onTrimMemory(int paramInt) {}
  
  public AssetFileDescriptor openAssetFile(Uri paramUri, String paramString)
    throws FileNotFoundException
  {
    Object localObject = null;
    paramString = openFile(paramUri, paramString);
    paramUri = (Uri)localObject;
    if (paramString != null) {
      paramUri = new AssetFileDescriptor(paramString, 0L, -1L);
    }
    return paramUri;
  }
  
  public AssetFileDescriptor openAssetFile(Uri paramUri, String paramString, CancellationSignal paramCancellationSignal)
    throws FileNotFoundException
  {
    return openAssetFile(paramUri, paramString);
  }
  
  public ParcelFileDescriptor openFile(Uri paramUri, String paramString)
    throws FileNotFoundException
  {
    throw new FileNotFoundException("No files supported by provider at " + paramUri);
  }
  
  public ParcelFileDescriptor openFile(Uri paramUri, String paramString, CancellationSignal paramCancellationSignal)
    throws FileNotFoundException
  {
    return openFile(paramUri, paramString);
  }
  
  protected final ParcelFileDescriptor openFileHelper(Uri paramUri, String paramString)
    throws FileNotFoundException
  {
    int i = 0;
    Cursor localCursor = query(paramUri, new String[] { "_data" }, null, null, null);
    if (localCursor != null) {
      i = localCursor.getCount();
    }
    if (i != 1)
    {
      if (localCursor != null) {
        localCursor.close();
      }
      if (i == 0) {
        throw new FileNotFoundException("No entry for " + paramUri);
      }
      throw new FileNotFoundException("Multiple items at " + paramUri);
    }
    localCursor.moveToFirst();
    i = localCursor.getColumnIndex("_data");
    if (i >= 0) {}
    for (paramUri = localCursor.getString(i);; paramUri = null)
    {
      localCursor.close();
      if (paramUri != null) {
        break;
      }
      throw new FileNotFoundException("Column _data not found.");
    }
    i = ParcelFileDescriptor.parseMode(paramString);
    return ParcelFileDescriptor.open(new File(paramUri), i);
  }
  
  public <T> ParcelFileDescriptor openPipeHelper(final Uri paramUri, final String paramString, final Bundle paramBundle, final T paramT, final PipeDataWriter<T> paramPipeDataWriter)
    throws FileNotFoundException
  {
    try
    {
      final ParcelFileDescriptor[] arrayOfParcelFileDescriptor = ParcelFileDescriptor.createPipe();
      new AsyncTask()
      {
        protected Object doInBackground(Object... paramAnonymousVarArgs)
        {
          paramPipeDataWriter.writeDataToPipe(arrayOfParcelFileDescriptor[1], paramUri, paramString, paramBundle, paramT);
          try
          {
            arrayOfParcelFileDescriptor[1].close();
            return null;
          }
          catch (IOException paramAnonymousVarArgs)
          {
            for (;;)
            {
              Log.w("ContentProvider", "Failure closing pipe", paramAnonymousVarArgs);
            }
          }
        }
      }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object[])null);
      paramUri = arrayOfParcelFileDescriptor[0];
      return paramUri;
    }
    catch (IOException paramUri)
    {
      throw new FileNotFoundException("failure making pipe");
    }
  }
  
  public AssetFileDescriptor openTypedAssetFile(Uri paramUri, String paramString, Bundle paramBundle)
    throws FileNotFoundException
  {
    if ("*/*".equals(paramString)) {
      return openAssetFile(paramUri, "r");
    }
    paramBundle = getType(paramUri);
    if ((paramBundle != null) && (ClipDescription.compareMimeTypes(paramBundle, paramString))) {
      return openAssetFile(paramUri, "r");
    }
    throw new FileNotFoundException("Can't open " + paramUri + " as type " + paramString);
  }
  
  public AssetFileDescriptor openTypedAssetFile(Uri paramUri, String paramString, Bundle paramBundle, CancellationSignal paramCancellationSignal)
    throws FileNotFoundException
  {
    return openTypedAssetFile(paramUri, paramString, paramBundle);
  }
  
  public abstract Cursor query(Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2);
  
  public Cursor query(Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2, CancellationSignal paramCancellationSignal)
  {
    return query(paramUri, paramArrayOfString1, paramString1, paramArrayOfString2, paramString2);
  }
  
  public Uri rejectInsert(Uri paramUri, ContentValues paramContentValues)
  {
    return paramUri.buildUpon().appendPath("0").build();
  }
  
  public final void setAppOps(int paramInt1, int paramInt2)
  {
    if (!this.mNoPerms)
    {
      this.mTransport.mReadOp = paramInt1;
      this.mTransport.mWriteOp = paramInt2;
    }
  }
  
  protected final void setAuthorities(String paramString)
  {
    if (paramString != null)
    {
      if (paramString.indexOf(';') == -1)
      {
        this.mAuthority = paramString;
        this.mAuthorities = null;
      }
    }
    else {
      return;
    }
    this.mAuthority = null;
    this.mAuthorities = paramString.split(";");
  }
  
  protected final void setPathPermissions(PathPermission[] paramArrayOfPathPermission)
  {
    this.mPathPermissions = paramArrayOfPathPermission;
  }
  
  protected final void setReadPermission(String paramString)
  {
    this.mReadPermission = paramString;
  }
  
  protected final void setWritePermission(String paramString)
  {
    this.mWritePermission = paramString;
  }
  
  public void shutdown()
  {
    Log.w("ContentProvider", "implement ContentProvider shutdown() to make sure all database connections are gracefully shutdown");
  }
  
  public Uri uncanonicalize(Uri paramUri)
  {
    return paramUri;
  }
  
  public abstract int update(Uri paramUri, ContentValues paramContentValues, String paramString, String[] paramArrayOfString);
  
  public static abstract interface PipeDataWriter<T>
  {
    public abstract void writeDataToPipe(ParcelFileDescriptor paramParcelFileDescriptor, Uri paramUri, String paramString, Bundle paramBundle, T paramT);
  }
  
  class Transport
    extends ContentProviderNative
  {
    AppOpsManager mAppOpsManager = null;
    int mReadOp = -1;
    int mWriteOp = -1;
    
    Transport() {}
    
    private void enforceFilePermission(String paramString1, Uri paramUri, String paramString2, IBinder paramIBinder)
      throws FileNotFoundException, SecurityException
    {
      if ((paramString2 != null) && (paramString2.indexOf('w') != -1))
      {
        if (enforceWritePermission(paramString1, paramUri, paramIBinder) != 0) {
          throw new FileNotFoundException("App op not allowed");
        }
      }
      else if (enforceReadPermission(paramString1, paramUri, paramIBinder) != 0) {
        throw new FileNotFoundException("App op not allowed");
      }
    }
    
    private int enforceReadPermission(String paramString, Uri paramUri, IBinder paramIBinder)
      throws SecurityException
    {
      int i = ContentProvider.this.enforceReadPermissionInner(paramUri, paramString, paramIBinder);
      if (i != 0) {
        return i;
      }
      if (this.mReadOp != -1) {
        return this.mAppOpsManager.noteProxyOp(this.mReadOp, paramString);
      }
      return 0;
    }
    
    private int enforceWritePermission(String paramString, Uri paramUri, IBinder paramIBinder)
      throws SecurityException
    {
      int i = ContentProvider.this.enforceWritePermissionInner(paramUri, paramString, paramIBinder);
      if (i != 0) {
        return i;
      }
      if (this.mWriteOp != -1) {
        return this.mAppOpsManager.noteProxyOp(this.mWriteOp, paramString);
      }
      return 0;
    }
    
    public ContentProviderResult[] applyBatch(String paramString, ArrayList<ContentProviderOperation> paramArrayList)
      throws OperationApplicationException
    {
      int j = paramArrayList.size();
      int[] arrayOfInt = new int[j];
      int i = 0;
      while (i < j)
      {
        ContentProviderOperation localContentProviderOperation2 = (ContentProviderOperation)paramArrayList.get(i);
        Uri localUri = localContentProviderOperation2.getUri();
        ContentProvider.-wrap1(ContentProvider.this, localUri);
        arrayOfInt[i] = ContentProvider.getUserIdFromUri(localUri);
        ContentProviderOperation localContentProviderOperation1 = localContentProviderOperation2;
        if (arrayOfInt[i] != -2)
        {
          localContentProviderOperation1 = new ContentProviderOperation(localContentProviderOperation2, true);
          paramArrayList.set(i, localContentProviderOperation1);
        }
        if ((localContentProviderOperation1.isReadOperation()) && (enforceReadPermission(paramString, localUri, null) != 0)) {
          throw new OperationApplicationException("App op not allowed", 0);
        }
        if ((localContentProviderOperation1.isWriteOperation()) && (enforceWritePermission(paramString, localUri, null) != 0)) {
          throw new OperationApplicationException("App op not allowed", 0);
        }
        i += 1;
      }
      paramString = ContentProvider.-wrap0(ContentProvider.this, paramString);
      try
      {
        paramArrayList = ContentProvider.this.applyBatch(paramArrayList);
        if (paramArrayList != null)
        {
          i = 0;
          while (i < paramArrayList.length)
          {
            if (arrayOfInt[i] != -2) {
              paramArrayList[i] = new ContentProviderResult(paramArrayList[i], arrayOfInt[i]);
            }
            i += 1;
          }
        }
        return paramArrayList;
      }
      finally
      {
        ContentProvider.-wrap0(ContentProvider.this, paramString);
      }
    }
    
    public int bulkInsert(String paramString, Uri paramUri, ContentValues[] paramArrayOfContentValues)
    {
      ContentProvider.-wrap1(ContentProvider.this, paramUri);
      paramUri = ContentProvider.getUriWithoutUserId(paramUri);
      if (enforceWritePermission(paramString, paramUri, null) != 0) {
        return 0;
      }
      paramString = ContentProvider.-wrap0(ContentProvider.this, paramString);
      try
      {
        int i = ContentProvider.this.bulkInsert(paramUri, paramArrayOfContentValues);
        return i;
      }
      finally
      {
        ContentProvider.-wrap0(ContentProvider.this, paramString);
      }
    }
    
    public Bundle call(String paramString1, String paramString2, String paramString3, Bundle paramBundle)
    {
      Bundle.setDefusable(paramBundle, true);
      paramString1 = ContentProvider.-wrap0(ContentProvider.this, paramString1);
      try
      {
        paramString2 = ContentProvider.this.call(paramString2, paramString3, paramBundle);
        return paramString2;
      }
      finally
      {
        ContentProvider.-wrap0(ContentProvider.this, paramString1);
      }
    }
    
    public Uri canonicalize(String paramString, Uri paramUri)
    {
      ContentProvider.-wrap1(ContentProvider.this, paramUri);
      int i = ContentProvider.getUserIdFromUri(paramUri);
      paramUri = ContentProvider.getUriWithoutUserId(paramUri);
      if (enforceReadPermission(paramString, paramUri, null) != 0) {
        return null;
      }
      paramString = ContentProvider.-wrap0(ContentProvider.this, paramString);
      try
      {
        paramUri = ContentProvider.maybeAddUserId(ContentProvider.this.canonicalize(paramUri), i);
        return paramUri;
      }
      finally
      {
        ContentProvider.-wrap0(ContentProvider.this, paramString);
      }
    }
    
    public ICancellationSignal createCancellationSignal()
    {
      return CancellationSignal.createTransport();
    }
    
    public int delete(String paramString1, Uri paramUri, String paramString2, String[] paramArrayOfString)
    {
      ContentProvider.-wrap1(ContentProvider.this, paramUri);
      Uri localUri = ContentProvider.getUriWithoutUserId(paramUri);
      if (enforceWritePermission(paramString1, localUri, null) != 0) {
        return 0;
      }
      paramUri = ContentProvider.-wrap0(ContentProvider.this, paramString1);
      try
      {
        int i = ContentProvider.this.delete(localUri, paramString2, paramArrayOfString);
        if ((ContentProvider.-get1()) && (i > 0) && (ContentDebugUtils.isExternalMediaUri(localUri))) {
          ContentDebugUtils.saveDbgMsg(ContentProvider.this, "ContentProvider", "delete", localUri, paramString2, paramArrayOfString, paramString1);
        }
        return i;
      }
      finally
      {
        ContentProvider.-wrap0(ContentProvider.this, paramUri);
      }
    }
    
    ContentProvider getContentProvider()
    {
      return ContentProvider.this;
    }
    
    public String getProviderName()
    {
      return getContentProvider().getClass().getName();
    }
    
    public String[] getStreamTypes(Uri paramUri, String paramString)
    {
      ContentProvider.-wrap1(ContentProvider.this, paramUri);
      paramUri = ContentProvider.getUriWithoutUserId(paramUri);
      return ContentProvider.this.getStreamTypes(paramUri, paramString);
    }
    
    public String getType(Uri paramUri)
    {
      ContentProvider.-wrap1(ContentProvider.this, paramUri);
      paramUri = ContentProvider.getUriWithoutUserId(paramUri);
      return ContentProvider.this.getType(paramUri);
    }
    
    public Uri insert(String paramString, Uri paramUri, ContentValues paramContentValues)
    {
      ContentProvider.-wrap1(ContentProvider.this, paramUri);
      int i = ContentProvider.getUserIdFromUri(paramUri);
      Uri localUri = ContentProvider.getUriWithoutUserId(paramUri);
      if (enforceWritePermission(paramString, localUri, null) != 0) {
        return ContentProvider.this.rejectInsert(localUri, paramContentValues);
      }
      paramUri = ContentProvider.-wrap0(ContentProvider.this, paramString);
      try
      {
        localUri = ContentProvider.this.insert(localUri, paramContentValues);
        if ((ContentProvider.-get1()) && (ContentDebugUtils.isExternalMediaUri(localUri)))
        {
          paramContentValues = paramContentValues.getAsString("_data");
          if (((paramContentValues != null) && (paramContentValues.toLowerCase().endsWith(".nomedia"))) || (ContentProvider.-get0())) {
            ContentDebugUtils.saveDbgMsg(ContentProvider.this, "ContentProvider", "insert", localUri, paramContentValues, null, paramString);
          }
        }
        paramString = ContentProvider.maybeAddUserId(localUri, i);
        return paramString;
      }
      finally
      {
        ContentProvider.-wrap0(ContentProvider.this, paramUri);
      }
    }
    
    public AssetFileDescriptor openAssetFile(String paramString1, Uri paramUri, String paramString2, ICancellationSignal paramICancellationSignal)
      throws FileNotFoundException
    {
      ContentProvider.-wrap1(ContentProvider.this, paramUri);
      paramUri = ContentProvider.getUriWithoutUserId(paramUri);
      enforceFilePermission(paramString1, paramUri, paramString2, null);
      paramString1 = ContentProvider.-wrap0(ContentProvider.this, paramString1);
      try
      {
        paramUri = ContentProvider.this.openAssetFile(paramUri, paramString2, CancellationSignal.fromTransport(paramICancellationSignal));
        return paramUri;
      }
      finally
      {
        ContentProvider.-wrap0(ContentProvider.this, paramString1);
      }
    }
    
    public ParcelFileDescriptor openFile(String paramString1, Uri paramUri, String paramString2, ICancellationSignal paramICancellationSignal, IBinder paramIBinder)
      throws FileNotFoundException
    {
      ContentProvider.-wrap1(ContentProvider.this, paramUri);
      paramUri = ContentProvider.getUriWithoutUserId(paramUri);
      enforceFilePermission(paramString1, paramUri, paramString2, paramIBinder);
      paramString1 = ContentProvider.-wrap0(ContentProvider.this, paramString1);
      try
      {
        paramUri = ContentProvider.this.openFile(paramUri, paramString2, CancellationSignal.fromTransport(paramICancellationSignal));
        return paramUri;
      }
      finally
      {
        ContentProvider.-wrap0(ContentProvider.this, paramString1);
      }
    }
    
    public AssetFileDescriptor openTypedAssetFile(String paramString1, Uri paramUri, String paramString2, Bundle paramBundle, ICancellationSignal paramICancellationSignal)
      throws FileNotFoundException
    {
      Bundle.setDefusable(paramBundle, true);
      ContentProvider.-wrap1(ContentProvider.this, paramUri);
      paramUri = ContentProvider.getUriWithoutUserId(paramUri);
      enforceFilePermission(paramString1, paramUri, "r", null);
      paramString1 = ContentProvider.-wrap0(ContentProvider.this, paramString1);
      try
      {
        paramUri = ContentProvider.this.openTypedAssetFile(paramUri, paramString2, paramBundle, CancellationSignal.fromTransport(paramICancellationSignal));
        return paramUri;
      }
      finally
      {
        ContentProvider.-wrap0(ContentProvider.this, paramString1);
      }
    }
    
    public Cursor query(String paramString1, Uri paramUri, String[] paramArrayOfString1, String paramString2, String[] paramArrayOfString2, String paramString3, ICancellationSignal paramICancellationSignal)
    {
      ContentProvider.-wrap1(ContentProvider.this, paramUri);
      paramUri = ContentProvider.getUriWithoutUserId(paramUri);
      if (enforceReadPermission(paramString1, paramUri, null) != 0)
      {
        if (paramArrayOfString1 != null) {
          return new MatrixCursor(paramArrayOfString1, 0);
        }
        paramString1 = ContentProvider.this.query(paramUri, paramArrayOfString1, paramString2, paramArrayOfString2, paramString3, CancellationSignal.fromTransport(paramICancellationSignal));
        if (paramString1 == null) {
          return null;
        }
        return new MatrixCursor(paramString1.getColumnNames(), 0);
      }
      paramString1 = ContentProvider.-wrap0(ContentProvider.this, paramString1);
      try
      {
        paramUri = ContentProvider.this.query(paramUri, paramArrayOfString1, paramString2, paramArrayOfString2, paramString3, CancellationSignal.fromTransport(paramICancellationSignal));
        return paramUri;
      }
      finally
      {
        ContentProvider.-wrap0(ContentProvider.this, paramString1);
      }
    }
    
    public Uri uncanonicalize(String paramString, Uri paramUri)
    {
      ContentProvider.-wrap1(ContentProvider.this, paramUri);
      int i = ContentProvider.getUserIdFromUri(paramUri);
      paramUri = ContentProvider.getUriWithoutUserId(paramUri);
      if (enforceReadPermission(paramString, paramUri, null) != 0) {
        return null;
      }
      paramString = ContentProvider.-wrap0(ContentProvider.this, paramString);
      try
      {
        paramUri = ContentProvider.maybeAddUserId(ContentProvider.this.uncanonicalize(paramUri), i);
        return paramUri;
      }
      finally
      {
        ContentProvider.-wrap0(ContentProvider.this, paramString);
      }
    }
    
    public int update(String paramString1, Uri paramUri, ContentValues paramContentValues, String paramString2, String[] paramArrayOfString)
    {
      ContentProvider.-wrap1(ContentProvider.this, paramUri);
      paramUri = ContentProvider.getUriWithoutUserId(paramUri);
      if (enforceWritePermission(paramString1, paramUri, null) != 0) {
        return 0;
      }
      paramString1 = ContentProvider.-wrap0(ContentProvider.this, paramString1);
      try
      {
        int i = ContentProvider.this.update(paramUri, paramContentValues, paramString2, paramArrayOfString);
        return i;
      }
      finally
      {
        ContentProvider.-wrap0(ContentProvider.this, paramString1);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/ContentProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */