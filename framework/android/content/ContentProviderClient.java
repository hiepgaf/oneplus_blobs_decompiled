package android.content;

import android.content.res.AssetFileDescriptor;
import android.database.CrossProcessCursorWrapper;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.ICancellationSignal;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.Preconditions;
import dalvik.system.CloseGuard;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class ContentProviderClient
  implements AutoCloseable
{
  private static final String TAG = "ContentProviderClient";
  @GuardedBy("ContentProviderClient.class")
  private static Handler sAnrHandler;
  private NotRespondingRunnable mAnrRunnable;
  private long mAnrTimeout;
  private final CloseGuard mCloseGuard = CloseGuard.get();
  private final AtomicBoolean mClosed = new AtomicBoolean();
  private final IContentProvider mContentProvider;
  private final ContentResolver mContentResolver;
  private final String mPackageName;
  private final boolean mStable;
  
  public ContentProviderClient(ContentResolver paramContentResolver, IContentProvider paramIContentProvider, boolean paramBoolean)
  {
    this.mContentResolver = paramContentResolver;
    this.mContentProvider = paramIContentProvider;
    this.mPackageName = paramContentResolver.mPackageName;
    this.mStable = paramBoolean;
    this.mCloseGuard.open("close");
  }
  
  private void afterRemote()
  {
    if (this.mAnrRunnable != null) {
      sAnrHandler.removeCallbacks(this.mAnrRunnable);
    }
  }
  
  private void beforeRemote()
  {
    if (this.mAnrRunnable != null) {
      sAnrHandler.postDelayed(this.mAnrRunnable, this.mAnrTimeout);
    }
  }
  
  private boolean closeInternal()
  {
    this.mCloseGuard.close();
    if (this.mClosed.compareAndSet(false, true))
    {
      if (this.mStable) {
        return this.mContentResolver.releaseProvider(this.mContentProvider);
      }
      return this.mContentResolver.releaseUnstableProvider(this.mContentProvider);
    }
    return false;
  }
  
  public static void releaseQuietly(ContentProviderClient paramContentProviderClient)
  {
    if (paramContentProviderClient != null) {}
    try
    {
      paramContentProviderClient.release();
      return;
    }
    catch (Exception paramContentProviderClient) {}
  }
  
  public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> paramArrayList)
    throws RemoteException, OperationApplicationException
  {
    Preconditions.checkNotNull(paramArrayList, "operations");
    beforeRemote();
    try
    {
      paramArrayList = this.mContentProvider.applyBatch(this.mPackageName, paramArrayList);
      return paramArrayList;
    }
    catch (DeadObjectException paramArrayList)
    {
      if (!this.mStable) {
        this.mContentResolver.unstableProviderDied(this.mContentProvider);
      }
      throw paramArrayList;
    }
    finally
    {
      afterRemote();
    }
  }
  
  public int bulkInsert(Uri paramUri, ContentValues[] paramArrayOfContentValues)
    throws RemoteException
  {
    Preconditions.checkNotNull(paramUri, "url");
    Preconditions.checkNotNull(paramArrayOfContentValues, "initialValues");
    beforeRemote();
    try
    {
      int i = this.mContentProvider.bulkInsert(this.mPackageName, paramUri, paramArrayOfContentValues);
      return i;
    }
    catch (DeadObjectException paramUri)
    {
      if (!this.mStable) {
        this.mContentResolver.unstableProviderDied(this.mContentProvider);
      }
      throw paramUri;
    }
    finally
    {
      afterRemote();
    }
  }
  
  public Bundle call(String paramString1, String paramString2, Bundle paramBundle)
    throws RemoteException
  {
    Preconditions.checkNotNull(paramString1, "method");
    beforeRemote();
    try
    {
      paramString1 = this.mContentProvider.call(this.mPackageName, paramString1, paramString2, paramBundle);
      return paramString1;
    }
    catch (DeadObjectException paramString1)
    {
      if (!this.mStable) {
        this.mContentResolver.unstableProviderDied(this.mContentProvider);
      }
      throw paramString1;
    }
    finally
    {
      afterRemote();
    }
  }
  
  public final Uri canonicalize(Uri paramUri)
    throws RemoteException
  {
    Preconditions.checkNotNull(paramUri, "url");
    beforeRemote();
    try
    {
      paramUri = this.mContentProvider.canonicalize(this.mPackageName, paramUri);
      return paramUri;
    }
    catch (DeadObjectException paramUri)
    {
      if (!this.mStable) {
        this.mContentResolver.unstableProviderDied(this.mContentProvider);
      }
      throw paramUri;
    }
    finally
    {
      afterRemote();
    }
  }
  
  public void close()
  {
    closeInternal();
  }
  
  public int delete(Uri paramUri, String paramString, String[] paramArrayOfString)
    throws RemoteException
  {
    Preconditions.checkNotNull(paramUri, "url");
    beforeRemote();
    try
    {
      int i = this.mContentProvider.delete(this.mPackageName, paramUri, paramString, paramArrayOfString);
      return i;
    }
    catch (DeadObjectException paramUri)
    {
      if (!this.mStable) {
        this.mContentResolver.unstableProviderDied(this.mContentProvider);
      }
      throw paramUri;
    }
    finally
    {
      afterRemote();
    }
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      this.mCloseGuard.warnIfOpen();
      close();
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public ContentProvider getLocalContentProvider()
  {
    return ContentProvider.coerceToLocalContentProvider(this.mContentProvider);
  }
  
  public String[] getStreamTypes(Uri paramUri, String paramString)
    throws RemoteException
  {
    Preconditions.checkNotNull(paramUri, "url");
    Preconditions.checkNotNull(paramString, "mimeTypeFilter");
    beforeRemote();
    try
    {
      paramUri = this.mContentProvider.getStreamTypes(paramUri, paramString);
      return paramUri;
    }
    catch (DeadObjectException paramUri)
    {
      if (!this.mStable) {
        this.mContentResolver.unstableProviderDied(this.mContentProvider);
      }
      throw paramUri;
    }
    finally
    {
      afterRemote();
    }
  }
  
  public String getType(Uri paramUri)
    throws RemoteException
  {
    Preconditions.checkNotNull(paramUri, "url");
    beforeRemote();
    try
    {
      paramUri = this.mContentProvider.getType(paramUri);
      return paramUri;
    }
    catch (DeadObjectException paramUri)
    {
      if (!this.mStable) {
        this.mContentResolver.unstableProviderDied(this.mContentProvider);
      }
      throw paramUri;
    }
    finally
    {
      afterRemote();
    }
  }
  
  public Uri insert(Uri paramUri, ContentValues paramContentValues)
    throws RemoteException
  {
    Preconditions.checkNotNull(paramUri, "url");
    beforeRemote();
    try
    {
      paramUri = this.mContentProvider.insert(this.mPackageName, paramUri, paramContentValues);
      return paramUri;
    }
    catch (DeadObjectException paramUri)
    {
      if (!this.mStable) {
        this.mContentResolver.unstableProviderDied(this.mContentProvider);
      }
      throw paramUri;
    }
    finally
    {
      afterRemote();
    }
  }
  
  public AssetFileDescriptor openAssetFile(Uri paramUri, String paramString)
    throws RemoteException, FileNotFoundException
  {
    return openAssetFile(paramUri, paramString, null);
  }
  
  public AssetFileDescriptor openAssetFile(Uri paramUri, String paramString, CancellationSignal paramCancellationSignal)
    throws RemoteException, FileNotFoundException
  {
    Preconditions.checkNotNull(paramUri, "url");
    Preconditions.checkNotNull(paramString, "mode");
    beforeRemote();
    ICancellationSignal localICancellationSignal = null;
    if (paramCancellationSignal != null) {}
    try
    {
      paramCancellationSignal.throwIfCanceled();
      localICancellationSignal = this.mContentProvider.createCancellationSignal();
      paramCancellationSignal.setRemote(localICancellationSignal);
      paramUri = this.mContentProvider.openAssetFile(this.mPackageName, paramUri, paramString, localICancellationSignal);
      return paramUri;
    }
    catch (DeadObjectException paramUri)
    {
      if (!this.mStable) {
        this.mContentResolver.unstableProviderDied(this.mContentProvider);
      }
      throw paramUri;
    }
    finally
    {
      afterRemote();
    }
  }
  
  public ParcelFileDescriptor openFile(Uri paramUri, String paramString)
    throws RemoteException, FileNotFoundException
  {
    return openFile(paramUri, paramString, null);
  }
  
  public ParcelFileDescriptor openFile(Uri paramUri, String paramString, CancellationSignal paramCancellationSignal)
    throws RemoteException, FileNotFoundException
  {
    Preconditions.checkNotNull(paramUri, "url");
    Preconditions.checkNotNull(paramString, "mode");
    beforeRemote();
    ICancellationSignal localICancellationSignal = null;
    if (paramCancellationSignal != null) {}
    try
    {
      paramCancellationSignal.throwIfCanceled();
      localICancellationSignal = this.mContentProvider.createCancellationSignal();
      paramCancellationSignal.setRemote(localICancellationSignal);
      paramUri = this.mContentProvider.openFile(this.mPackageName, paramUri, paramString, localICancellationSignal, null);
      return paramUri;
    }
    catch (DeadObjectException paramUri)
    {
      if (!this.mStable) {
        this.mContentResolver.unstableProviderDied(this.mContentProvider);
      }
      throw paramUri;
    }
    finally
    {
      afterRemote();
    }
  }
  
  public final AssetFileDescriptor openTypedAssetFileDescriptor(Uri paramUri, String paramString, Bundle paramBundle)
    throws RemoteException, FileNotFoundException
  {
    return openTypedAssetFileDescriptor(paramUri, paramString, paramBundle, null);
  }
  
  public final AssetFileDescriptor openTypedAssetFileDescriptor(Uri paramUri, String paramString, Bundle paramBundle, CancellationSignal paramCancellationSignal)
    throws RemoteException, FileNotFoundException
  {
    Preconditions.checkNotNull(paramUri, "uri");
    Preconditions.checkNotNull(paramString, "mimeType");
    beforeRemote();
    ICancellationSignal localICancellationSignal = null;
    if (paramCancellationSignal != null) {}
    try
    {
      paramCancellationSignal.throwIfCanceled();
      localICancellationSignal = this.mContentProvider.createCancellationSignal();
      paramCancellationSignal.setRemote(localICancellationSignal);
      paramUri = this.mContentProvider.openTypedAssetFile(this.mPackageName, paramUri, paramString, paramBundle, localICancellationSignal);
      return paramUri;
    }
    catch (DeadObjectException paramUri)
    {
      if (!this.mStable) {
        this.mContentResolver.unstableProviderDied(this.mContentProvider);
      }
      throw paramUri;
    }
    finally
    {
      afterRemote();
    }
  }
  
  public Cursor query(Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2)
    throws RemoteException
  {
    return query(paramUri, paramArrayOfString1, paramString1, paramArrayOfString2, paramString2, null);
  }
  
  public Cursor query(Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2, CancellationSignal paramCancellationSignal)
    throws RemoteException
  {
    Preconditions.checkNotNull(paramUri, "url");
    beforeRemote();
    ICancellationSignal localICancellationSignal = null;
    if (paramCancellationSignal != null) {}
    try
    {
      paramCancellationSignal.throwIfCanceled();
      localICancellationSignal = this.mContentProvider.createCancellationSignal();
      paramCancellationSignal.setRemote(localICancellationSignal);
      paramUri = this.mContentProvider.query(this.mPackageName, paramUri, paramArrayOfString1, paramString1, paramArrayOfString2, paramString2, localICancellationSignal);
      if (paramUri == null) {
        return null;
      }
      boolean bool = "com.google.android.gms".equals(this.mPackageName);
      if (bool) {
        return paramUri;
      }
      paramUri = new CursorWrapperInner(paramUri);
      return paramUri;
    }
    catch (DeadObjectException paramUri)
    {
      if (!this.mStable) {
        this.mContentResolver.unstableProviderDied(this.mContentProvider);
      }
      throw paramUri;
    }
    finally
    {
      afterRemote();
    }
  }
  
  @Deprecated
  public boolean release()
  {
    return closeInternal();
  }
  
  /* Error */
  public void setDetectNotResponding(long paramLong)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: aload_0
    //   4: lload_1
    //   5: putfield 90	android/content/ContentProviderClient:mAnrTimeout	J
    //   8: lload_1
    //   9: lconst_0
    //   10: lcmp
    //   11: ifle +48 -> 59
    //   14: aload_0
    //   15: getfield 79	android/content/ContentProviderClient:mAnrRunnable	Landroid/content/ContentProviderClient$NotRespondingRunnable;
    //   18: ifnonnull +16 -> 34
    //   21: aload_0
    //   22: new 11	android/content/ContentProviderClient$NotRespondingRunnable
    //   25: dup
    //   26: aload_0
    //   27: aconst_null
    //   28: invokespecial 280	android/content/ContentProviderClient$NotRespondingRunnable:<init>	(Landroid/content/ContentProviderClient;Landroid/content/ContentProviderClient$NotRespondingRunnable;)V
    //   31: putfield 79	android/content/ContentProviderClient:mAnrRunnable	Landroid/content/ContentProviderClient$NotRespondingRunnable;
    //   34: getstatic 81	android/content/ContentProviderClient:sAnrHandler	Landroid/os/Handler;
    //   37: ifnonnull +18 -> 55
    //   40: new 83	android/os/Handler
    //   43: dup
    //   44: invokestatic 286	android/os/Looper:getMainLooper	()Landroid/os/Looper;
    //   47: aconst_null
    //   48: iconst_1
    //   49: invokespecial 289	android/os/Handler:<init>	(Landroid/os/Looper;Landroid/os/Handler$Callback;Z)V
    //   52: putstatic 81	android/content/ContentProviderClient:sAnrHandler	Landroid/os/Handler;
    //   55: ldc 2
    //   57: monitorexit
    //   58: return
    //   59: aload_0
    //   60: aconst_null
    //   61: putfield 79	android/content/ContentProviderClient:mAnrRunnable	Landroid/content/ContentProviderClient$NotRespondingRunnable;
    //   64: goto -9 -> 55
    //   67: astore_3
    //   68: ldc 2
    //   70: monitorexit
    //   71: aload_3
    //   72: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	73	0	this	ContentProviderClient
    //   0	73	1	paramLong	long
    //   67	5	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   3	8	67	finally
    //   14	34	67	finally
    //   34	55	67	finally
    //   59	64	67	finally
  }
  
  public final Uri uncanonicalize(Uri paramUri)
    throws RemoteException
  {
    Preconditions.checkNotNull(paramUri, "url");
    beforeRemote();
    try
    {
      paramUri = this.mContentProvider.uncanonicalize(this.mPackageName, paramUri);
      return paramUri;
    }
    catch (DeadObjectException paramUri)
    {
      if (!this.mStable) {
        this.mContentResolver.unstableProviderDied(this.mContentProvider);
      }
      throw paramUri;
    }
    finally
    {
      afterRemote();
    }
  }
  
  public int update(Uri paramUri, ContentValues paramContentValues, String paramString, String[] paramArrayOfString)
    throws RemoteException
  {
    Preconditions.checkNotNull(paramUri, "url");
    beforeRemote();
    try
    {
      int i = this.mContentProvider.update(this.mPackageName, paramUri, paramContentValues, paramString, paramArrayOfString);
      return i;
    }
    catch (DeadObjectException paramUri)
    {
      if (!this.mStable) {
        this.mContentResolver.unstableProviderDied(this.mContentProvider);
      }
      throw paramUri;
    }
    finally
    {
      afterRemote();
    }
  }
  
  private final class CursorWrapperInner
    extends CrossProcessCursorWrapper
  {
    private final CloseGuard mCloseGuard = CloseGuard.get();
    
    CursorWrapperInner(Cursor paramCursor)
    {
      super();
      this.mCloseGuard.open("close");
    }
    
    public void close()
    {
      this.mCloseGuard.close();
      super.close();
    }
    
    protected void finalize()
      throws Throwable
    {
      try
      {
        this.mCloseGuard.warnIfOpen();
        close();
        return;
      }
      finally
      {
        super.finalize();
      }
    }
  }
  
  private class NotRespondingRunnable
    implements Runnable
  {
    private NotRespondingRunnable() {}
    
    public void run()
    {
      Log.w("ContentProviderClient", "Detected provider not responding: " + ContentProviderClient.-get0(ContentProviderClient.this));
      ContentProviderClient.-get1(ContentProviderClient.this).appNotRespondingViaProvider(ContentProviderClient.-get0(ContentProviderClient.this));
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/ContentProviderClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */