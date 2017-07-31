package android.support.v4.app;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v4.content.Loader.OnLoadCompleteListener;
import android.support.v4.util.DebugUtils;
import android.support.v4.util.SparseArrayCompat;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;

class LoaderManagerImpl
  extends LoaderManager
{
  static boolean DEBUG = false;
  static final String TAG = "LoaderManager";
  FragmentActivity mActivity;
  boolean mCreatingLoader;
  final SparseArrayCompat<LoaderInfo> mInactiveLoaders = new SparseArrayCompat();
  final SparseArrayCompat<LoaderInfo> mLoaders = new SparseArrayCompat();
  boolean mRetaining;
  boolean mRetainingStarted;
  boolean mStarted;
  final String mWho;
  
  LoaderManagerImpl(String paramString, FragmentActivity paramFragmentActivity, boolean paramBoolean)
  {
    this.mWho = paramString;
    this.mActivity = paramFragmentActivity;
    this.mStarted = paramBoolean;
  }
  
  private LoaderInfo createAndInstallLoader(int paramInt, Bundle paramBundle, LoaderManager.LoaderCallbacks<Object> paramLoaderCallbacks)
  {
    try
    {
      this.mCreatingLoader = true;
      paramBundle = createLoader(paramInt, paramBundle, paramLoaderCallbacks);
      installLoader(paramBundle);
      return paramBundle;
    }
    finally
    {
      this.mCreatingLoader = false;
    }
  }
  
  private LoaderInfo createLoader(int paramInt, Bundle paramBundle, LoaderManager.LoaderCallbacks<Object> paramLoaderCallbacks)
  {
    LoaderInfo localLoaderInfo = new LoaderInfo(paramInt, paramBundle, paramLoaderCallbacks);
    localLoaderInfo.mLoader = paramLoaderCallbacks.onCreateLoader(paramInt, paramBundle);
    return localLoaderInfo;
  }
  
  public void destroyLoader(int paramInt)
  {
    int i;
    if (!this.mCreatingLoader)
    {
      if (DEBUG) {
        break label57;
      }
      i = this.mLoaders.indexOfKey(paramInt);
      if (i >= 0) {
        break label94;
      }
      label26:
      paramInt = this.mInactiveLoaders.indexOfKey(paramInt);
      if (paramInt >= 0) {
        break label121;
      }
      label39:
      if (this.mActivity != null) {
        break label148;
      }
    }
    label57:
    label94:
    label121:
    label148:
    while (hasRunningLoaders())
    {
      return;
      throw new IllegalStateException("Called while creating a loader");
      Log.v("LoaderManager", "destroyLoader in " + this + " of " + paramInt);
      break;
      LoaderInfo localLoaderInfo = (LoaderInfo)this.mLoaders.valueAt(i);
      this.mLoaders.removeAt(i);
      localLoaderInfo.destroy();
      break label26;
      localLoaderInfo = (LoaderInfo)this.mInactiveLoaders.valueAt(paramInt);
      this.mInactiveLoaders.removeAt(paramInt);
      localLoaderInfo.destroy();
      break label39;
    }
    this.mActivity.mFragments.startPendingDeferredFragments();
  }
  
  void doDestroy()
  {
    label13:
    int i;
    if (this.mRetaining)
    {
      if (DEBUG) {
        break label114;
      }
      i = this.mInactiveLoaders.size() - 1;
    }
    for (;;)
    {
      if (i < 0)
      {
        this.mInactiveLoaders.clear();
        return;
        if (!DEBUG) {
          label41:
          i = this.mLoaders.size() - 1;
        }
        for (;;)
        {
          if (i < 0)
          {
            this.mLoaders.clear();
            break;
            Log.v("LoaderManager", "Destroying Active in " + this);
            break label41;
          }
          ((LoaderInfo)this.mLoaders.valueAt(i)).destroy();
          i -= 1;
        }
        label114:
        Log.v("LoaderManager", "Destroying Inactive in " + this);
        break label13;
      }
      ((LoaderInfo)this.mInactiveLoaders.valueAt(i)).destroy();
      i -= 1;
    }
  }
  
  void doReportNextStart()
  {
    int i = this.mLoaders.size() - 1;
    for (;;)
    {
      if (i < 0) {
        return;
      }
      ((LoaderInfo)this.mLoaders.valueAt(i)).mReportNextStart = true;
      i -= 1;
    }
  }
  
  void doReportStart()
  {
    int i = this.mLoaders.size() - 1;
    for (;;)
    {
      if (i < 0) {
        return;
      }
      ((LoaderInfo)this.mLoaders.valueAt(i)).reportStart();
      i -= 1;
    }
  }
  
  void doRetain()
  {
    int i;
    if (!DEBUG)
    {
      if (!this.mStarted) {
        break label66;
      }
      this.mRetaining = true;
      this.mStarted = false;
      i = this.mLoaders.size() - 1;
    }
    for (;;)
    {
      if (i < 0)
      {
        return;
        Log.v("LoaderManager", "Retaining in " + this);
        break;
        label66:
        RuntimeException localRuntimeException = new RuntimeException("here");
        localRuntimeException.fillInStackTrace();
        Log.w("LoaderManager", "Called doRetain when not started: " + this, localRuntimeException);
        return;
      }
      ((LoaderInfo)this.mLoaders.valueAt(i)).retain();
      i -= 1;
    }
  }
  
  void doStart()
  {
    int i;
    if (!DEBUG)
    {
      if (this.mStarted) {
        break label61;
      }
      this.mStarted = true;
      i = this.mLoaders.size() - 1;
    }
    for (;;)
    {
      if (i < 0)
      {
        return;
        Log.v("LoaderManager", "Starting in " + this);
        break;
        label61:
        RuntimeException localRuntimeException = new RuntimeException("here");
        localRuntimeException.fillInStackTrace();
        Log.w("LoaderManager", "Called doStart when already started: " + this, localRuntimeException);
        return;
      }
      ((LoaderInfo)this.mLoaders.valueAt(i)).start();
      i -= 1;
    }
  }
  
  void doStop()
  {
    int i;
    if (!DEBUG)
    {
      if (!this.mStarted) {
        break label61;
      }
      i = this.mLoaders.size() - 1;
    }
    for (;;)
    {
      if (i < 0)
      {
        this.mStarted = false;
        return;
        Log.v("LoaderManager", "Stopping in " + this);
        break;
        label61:
        RuntimeException localRuntimeException = new RuntimeException("here");
        localRuntimeException.fillInStackTrace();
        Log.w("LoaderManager", "Called doStop when not started: " + this, localRuntimeException);
        return;
      }
      ((LoaderInfo)this.mLoaders.valueAt(i)).stop();
      i -= 1;
    }
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    int j = 0;
    if (this.mLoaders.size() <= 0) {
      if (this.mInactiveLoaders.size() > 0) {
        break label144;
      }
    }
    for (;;)
    {
      return;
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("Active Loaders:");
      String str = paramString + "    ";
      int i = 0;
      LoaderInfo localLoaderInfo;
      while (i < this.mLoaders.size())
      {
        localLoaderInfo = (LoaderInfo)this.mLoaders.valueAt(i);
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("  #");
        paramPrintWriter.print(this.mLoaders.keyAt(i));
        paramPrintWriter.print(": ");
        paramPrintWriter.println(localLoaderInfo.toString());
        localLoaderInfo.dump(str, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
        i += 1;
      }
      break;
      label144:
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("Inactive Loaders:");
      str = paramString + "    ";
      i = j;
      while (i < this.mInactiveLoaders.size())
      {
        localLoaderInfo = (LoaderInfo)this.mInactiveLoaders.valueAt(i);
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("  #");
        paramPrintWriter.print(this.mInactiveLoaders.keyAt(i));
        paramPrintWriter.print(": ");
        paramPrintWriter.println(localLoaderInfo.toString());
        localLoaderInfo.dump(str, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
        i += 1;
      }
    }
  }
  
  void finishRetain()
  {
    if (!this.mRetaining) {
      return;
    }
    if (!DEBUG) {}
    for (;;)
    {
      this.mRetaining = false;
      int i = this.mLoaders.size() - 1;
      while (i >= 0)
      {
        ((LoaderInfo)this.mLoaders.valueAt(i)).finishRetain();
        i -= 1;
      }
      break;
      Log.v("LoaderManager", "Finished Retaining in " + this);
    }
  }
  
  public <D> Loader<D> getLoader(int paramInt)
  {
    LoaderInfo localLoaderInfo;
    if (!this.mCreatingLoader)
    {
      localLoaderInfo = (LoaderInfo)this.mLoaders.get(paramInt);
      if (localLoaderInfo == null) {
        return null;
      }
    }
    else
    {
      throw new IllegalStateException("Called while creating a loader");
    }
    if (localLoaderInfo.mPendingLoader == null) {
      return localLoaderInfo.mLoader;
    }
    return localLoaderInfo.mPendingLoader.mLoader;
  }
  
  public boolean hasRunningLoaders()
  {
    int j = this.mLoaders.size();
    int i = 0;
    boolean bool2 = false;
    if (i >= j) {
      return bool2;
    }
    LoaderInfo localLoaderInfo = (LoaderInfo)this.mLoaders.valueAt(i);
    if (!localLoaderInfo.mStarted) {}
    label42:
    for (boolean bool1 = false;; bool1 = true)
    {
      bool2 |= bool1;
      i += 1;
      break;
      if (localLoaderInfo.mDeliveredData) {
        break label42;
      }
    }
  }
  
  public <D> Loader<D> initLoader(int paramInt, Bundle paramBundle, LoaderManager.LoaderCallbacks<D> paramLoaderCallbacks)
  {
    LoaderInfo localLoaderInfo;
    if (!this.mCreatingLoader)
    {
      localLoaderInfo = (LoaderInfo)this.mLoaders.get(paramInt);
      if (DEBUG) {
        break label68;
      }
      if (localLoaderInfo == null) {
        break label106;
      }
      if (DEBUG) {
        break label153;
      }
      label37:
      localLoaderInfo.mCallbacks = paramLoaderCallbacks;
      paramBundle = localLoaderInfo;
      label46:
      if (paramBundle.mHaveData) {
        break label183;
      }
    }
    for (;;)
    {
      return paramBundle.mLoader;
      throw new IllegalStateException("Called while creating a loader");
      label68:
      Log.v("LoaderManager", "initLoader in " + this + ": args=" + paramBundle);
      break;
      label106:
      paramLoaderCallbacks = createAndInstallLoader(paramInt, paramBundle, paramLoaderCallbacks);
      paramBundle = paramLoaderCallbacks;
      if (!DEBUG) {
        break label46;
      }
      Log.v("LoaderManager", "  Created new loader " + paramLoaderCallbacks);
      paramBundle = paramLoaderCallbacks;
      break label46;
      label153:
      Log.v("LoaderManager", "  Re-using existing loader " + localLoaderInfo);
      break label37;
      label183:
      if (this.mStarted) {
        paramBundle.callOnLoadFinished(paramBundle.mLoader, paramBundle.mData);
      }
    }
  }
  
  void installLoader(LoaderInfo paramLoaderInfo)
  {
    this.mLoaders.put(paramLoaderInfo.mId, paramLoaderInfo);
    if (!this.mStarted) {
      return;
    }
    paramLoaderInfo.start();
  }
  
  public <D> Loader<D> restartLoader(int paramInt, Bundle paramBundle, LoaderManager.LoaderCallbacks<D> paramLoaderCallbacks)
  {
    LoaderInfo localLoaderInfo1;
    if (!this.mCreatingLoader)
    {
      localLoaderInfo1 = (LoaderInfo)this.mLoaders.get(paramInt);
      if (DEBUG) {
        break label52;
      }
    }
    while (localLoaderInfo1 == null)
    {
      return createAndInstallLoader(paramInt, paramBundle, paramLoaderCallbacks).mLoader;
      throw new IllegalStateException("Called while creating a loader");
      label52:
      Log.v("LoaderManager", "restartLoader in " + this + ": args=" + paramBundle);
    }
    LoaderInfo localLoaderInfo2 = (LoaderInfo)this.mInactiveLoaders.get(paramInt);
    if (localLoaderInfo2 == null) {
      if (DEBUG) {
        break label358;
      }
    }
    for (;;)
    {
      localLoaderInfo1.mLoader.abandon();
      this.mInactiveLoaders.put(paramInt, localLoaderInfo1);
      break;
      if (!localLoaderInfo1.mHaveData)
      {
        if (!localLoaderInfo1.mStarted) {
          break label255;
        }
        if (localLoaderInfo1.mPendingLoader != null) {
          break label290;
        }
        if (DEBUG) {
          break label346;
        }
      }
      for (;;)
      {
        localLoaderInfo1.mPendingLoader = createLoader(paramInt, paramBundle, paramLoaderCallbacks);
        return localLoaderInfo1.mPendingLoader.mLoader;
        if (!DEBUG) {}
        for (;;)
        {
          localLoaderInfo2.mDeliveredData = false;
          localLoaderInfo2.destroy();
          localLoaderInfo1.mLoader.abandon();
          this.mInactiveLoaders.put(paramInt, localLoaderInfo1);
          break;
          Log.v("LoaderManager", "  Removing last inactive loader: " + localLoaderInfo1);
        }
        label255:
        if (!DEBUG) {}
        for (;;)
        {
          this.mLoaders.put(paramInt, null);
          localLoaderInfo1.destroy();
          break;
          Log.v("LoaderManager", "  Current loader is stopped; replacing");
        }
        label290:
        if (!DEBUG) {}
        for (;;)
        {
          localLoaderInfo1.mPendingLoader.destroy();
          localLoaderInfo1.mPendingLoader = null;
          break;
          Log.v("LoaderManager", "  Removing pending loader: " + localLoaderInfo1.mPendingLoader);
        }
        label346:
        Log.v("LoaderManager", "  Enqueuing as new pending loader");
      }
      label358:
      Log.v("LoaderManager", "  Making last loader inactive: " + localLoaderInfo1);
    }
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    localStringBuilder.append("LoaderManager{");
    localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
    localStringBuilder.append(" in ");
    DebugUtils.buildShortClassTag(this.mActivity, localStringBuilder);
    localStringBuilder.append("}}");
    return localStringBuilder.toString();
  }
  
  void updateActivity(FragmentActivity paramFragmentActivity)
  {
    this.mActivity = paramFragmentActivity;
  }
  
  final class LoaderInfo
    implements Loader.OnLoadCompleteListener<Object>
  {
    final Bundle mArgs;
    LoaderManager.LoaderCallbacks<Object> mCallbacks;
    Object mData;
    boolean mDeliveredData;
    boolean mDestroyed;
    boolean mHaveData;
    final int mId;
    boolean mListenerRegistered;
    Loader<Object> mLoader;
    LoaderInfo mPendingLoader;
    boolean mReportNextStart;
    boolean mRetaining;
    boolean mRetainingStarted;
    boolean mStarted;
    
    public LoaderInfo(Bundle paramBundle, LoaderManager.LoaderCallbacks<Object> paramLoaderCallbacks)
    {
      this.mId = paramBundle;
      this.mArgs = paramLoaderCallbacks;
      LoaderManager.LoaderCallbacks localLoaderCallbacks;
      this.mCallbacks = localLoaderCallbacks;
    }
    
    void callOnLoadFinished(Loader<Object> paramLoader, Object paramObject)
    {
      if (this.mCallbacks == null) {
        return;
      }
      String str;
      if (LoaderManagerImpl.this.mActivity == null) {
        str = null;
      }
      try
      {
        if (!LoaderManagerImpl.DEBUG) {}
        for (;;)
        {
          this.mCallbacks.onLoadFinished(paramLoader, paramObject);
          if (LoaderManagerImpl.this.mActivity != null) {
            break label139;
          }
          this.mDeliveredData = true;
          return;
          str = LoaderManagerImpl.this.mActivity.mFragments.mNoTransactionsBecause;
          LoaderManagerImpl.this.mActivity.mFragments.mNoTransactionsBecause = "onLoadFinished";
          break;
          Log.v("LoaderManager", "  onLoadFinished in " + paramLoader + ": " + paramLoader.dataToString(paramObject));
        }
        throw paramLoader;
      }
      finally
      {
        if (LoaderManagerImpl.this.mActivity != null) {}
      }
      for (;;)
      {
        label139:
        LoaderManagerImpl.this.mActivity.mFragments.mNoTransactionsBecause = str;
        break;
        LoaderManagerImpl.this.mActivity.mFragments.mNoTransactionsBecause = str;
      }
    }
    
    /* Error */
    void destroy()
    {
      // Byte code:
      //   0: getstatic 60	android/support/v4/app/LoaderManagerImpl:DEBUG	Z
      //   3: ifne +55 -> 58
      //   6: aload_0
      //   7: iconst_1
      //   8: putfield 116	android/support/v4/app/LoaderManagerImpl$LoaderInfo:mDestroyed	Z
      //   11: aload_0
      //   12: getfield 67	android/support/v4/app/LoaderManagerImpl$LoaderInfo:mDeliveredData	Z
      //   15: istore_1
      //   16: aload_0
      //   17: iconst_0
      //   18: putfield 67	android/support/v4/app/LoaderManagerImpl$LoaderInfo:mDeliveredData	Z
      //   21: aload_0
      //   22: getfield 48	android/support/v4/app/LoaderManagerImpl$LoaderInfo:mCallbacks	Landroid/support/v4/app/LoaderManager$LoaderCallbacks;
      //   25: ifnonnull +61 -> 86
      //   28: aload_0
      //   29: aconst_null
      //   30: putfield 48	android/support/v4/app/LoaderManagerImpl$LoaderInfo:mCallbacks	Landroid/support/v4/app/LoaderManager$LoaderCallbacks;
      //   33: aload_0
      //   34: aconst_null
      //   35: putfield 118	android/support/v4/app/LoaderManagerImpl$LoaderInfo:mData	Ljava/lang/Object;
      //   38: aload_0
      //   39: iconst_0
      //   40: putfield 120	android/support/v4/app/LoaderManagerImpl$LoaderInfo:mHaveData	Z
      //   43: aload_0
      //   44: getfield 122	android/support/v4/app/LoaderManagerImpl$LoaderInfo:mLoader	Landroid/support/v4/content/Loader;
      //   47: ifnonnull +205 -> 252
      //   50: aload_0
      //   51: getfield 124	android/support/v4/app/LoaderManagerImpl$LoaderInfo:mPendingLoader	Landroid/support/v4/app/LoaderManagerImpl$LoaderInfo;
      //   54: ifnonnull +231 -> 285
      //   57: return
      //   58: ldc 82
      //   60: new 84	java/lang/StringBuilder
      //   63: dup
      //   64: invokespecial 85	java/lang/StringBuilder:<init>	()V
      //   67: ldc 126
      //   69: invokevirtual 91	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   72: aload_0
      //   73: invokevirtual 94	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   76: invokevirtual 106	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   79: invokestatic 112	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
      //   82: pop
      //   83: goto -77 -> 6
      //   86: aload_0
      //   87: getfield 122	android/support/v4/app/LoaderManagerImpl$LoaderInfo:mLoader	Landroid/support/v4/content/Loader;
      //   90: ifnull -62 -> 28
      //   93: aload_0
      //   94: getfield 120	android/support/v4/app/LoaderManagerImpl$LoaderInfo:mHaveData	Z
      //   97: ifeq -69 -> 28
      //   100: iload_1
      //   101: ifeq -73 -> 28
      //   104: getstatic 60	android/support/v4/app/LoaderManagerImpl:DEBUG	Z
      //   107: ifne +55 -> 162
      //   110: aload_0
      //   111: getfield 39	android/support/v4/app/LoaderManagerImpl$LoaderInfo:this$0	Landroid/support/v4/app/LoaderManagerImpl;
      //   114: getfield 57	android/support/v4/app/LoaderManagerImpl:mActivity	Landroid/support/v4/app/FragmentActivity;
      //   117: ifnonnull +73 -> 190
      //   120: aconst_null
      //   121: astore_2
      //   122: aload_0
      //   123: getfield 48	android/support/v4/app/LoaderManagerImpl$LoaderInfo:mCallbacks	Landroid/support/v4/app/LoaderManager$LoaderCallbacks;
      //   126: aload_0
      //   127: getfield 122	android/support/v4/app/LoaderManagerImpl$LoaderInfo:mLoader	Landroid/support/v4/content/Loader;
      //   130: invokeinterface 130 2 0
      //   135: aload_0
      //   136: getfield 39	android/support/v4/app/LoaderManagerImpl$LoaderInfo:this$0	Landroid/support/v4/app/LoaderManagerImpl;
      //   139: getfield 57	android/support/v4/app/LoaderManagerImpl:mActivity	Landroid/support/v4/app/FragmentActivity;
      //   142: ifnull -114 -> 28
      //   145: aload_0
      //   146: getfield 39	android/support/v4/app/LoaderManagerImpl$LoaderInfo:this$0	Landroid/support/v4/app/LoaderManagerImpl;
      //   149: getfield 57	android/support/v4/app/LoaderManagerImpl:mActivity	Landroid/support/v4/app/FragmentActivity;
      //   152: getfield 73	android/support/v4/app/FragmentActivity:mFragments	Landroid/support/v4/app/FragmentManagerImpl;
      //   155: aload_2
      //   156: putfield 79	android/support/v4/app/FragmentManagerImpl:mNoTransactionsBecause	Ljava/lang/String;
      //   159: goto -131 -> 28
      //   162: ldc 82
      //   164: new 84	java/lang/StringBuilder
      //   167: dup
      //   168: invokespecial 85	java/lang/StringBuilder:<init>	()V
      //   171: ldc -124
      //   173: invokevirtual 91	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   176: aload_0
      //   177: invokevirtual 94	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   180: invokevirtual 106	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   183: invokestatic 112	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
      //   186: pop
      //   187: goto -77 -> 110
      //   190: aload_0
      //   191: getfield 39	android/support/v4/app/LoaderManagerImpl$LoaderInfo:this$0	Landroid/support/v4/app/LoaderManagerImpl;
      //   194: getfield 57	android/support/v4/app/LoaderManagerImpl:mActivity	Landroid/support/v4/app/FragmentActivity;
      //   197: getfield 73	android/support/v4/app/FragmentActivity:mFragments	Landroid/support/v4/app/FragmentManagerImpl;
      //   200: getfield 79	android/support/v4/app/FragmentManagerImpl:mNoTransactionsBecause	Ljava/lang/String;
      //   203: astore_2
      //   204: aload_0
      //   205: getfield 39	android/support/v4/app/LoaderManagerImpl$LoaderInfo:this$0	Landroid/support/v4/app/LoaderManagerImpl;
      //   208: getfield 57	android/support/v4/app/LoaderManagerImpl:mActivity	Landroid/support/v4/app/FragmentActivity;
      //   211: getfield 73	android/support/v4/app/FragmentActivity:mFragments	Landroid/support/v4/app/FragmentManagerImpl;
      //   214: ldc -123
      //   216: putfield 79	android/support/v4/app/FragmentManagerImpl:mNoTransactionsBecause	Ljava/lang/String;
      //   219: goto -97 -> 122
      //   222: astore_3
      //   223: aload_0
      //   224: getfield 39	android/support/v4/app/LoaderManagerImpl$LoaderInfo:this$0	Landroid/support/v4/app/LoaderManagerImpl;
      //   227: getfield 57	android/support/v4/app/LoaderManagerImpl:mActivity	Landroid/support/v4/app/FragmentActivity;
      //   230: ifnonnull +5 -> 235
      //   233: aload_3
      //   234: athrow
      //   235: aload_0
      //   236: getfield 39	android/support/v4/app/LoaderManagerImpl$LoaderInfo:this$0	Landroid/support/v4/app/LoaderManagerImpl;
      //   239: getfield 57	android/support/v4/app/LoaderManagerImpl:mActivity	Landroid/support/v4/app/FragmentActivity;
      //   242: getfield 73	android/support/v4/app/FragmentActivity:mFragments	Landroid/support/v4/app/FragmentManagerImpl;
      //   245: aload_2
      //   246: putfield 79	android/support/v4/app/FragmentManagerImpl:mNoTransactionsBecause	Ljava/lang/String;
      //   249: goto -16 -> 233
      //   252: aload_0
      //   253: getfield 135	android/support/v4/app/LoaderManagerImpl$LoaderInfo:mListenerRegistered	Z
      //   256: ifne +13 -> 269
      //   259: aload_0
      //   260: getfield 122	android/support/v4/app/LoaderManagerImpl$LoaderInfo:mLoader	Landroid/support/v4/content/Loader;
      //   263: invokevirtual 138	android/support/v4/content/Loader:reset	()V
      //   266: goto -216 -> 50
      //   269: aload_0
      //   270: iconst_0
      //   271: putfield 135	android/support/v4/app/LoaderManagerImpl$LoaderInfo:mListenerRegistered	Z
      //   274: aload_0
      //   275: getfield 122	android/support/v4/app/LoaderManagerImpl$LoaderInfo:mLoader	Landroid/support/v4/content/Loader;
      //   278: aload_0
      //   279: invokevirtual 142	android/support/v4/content/Loader:unregisterListener	(Landroid/support/v4/content/Loader$OnLoadCompleteListener;)V
      //   282: goto -23 -> 259
      //   285: aload_0
      //   286: getfield 124	android/support/v4/app/LoaderManagerImpl$LoaderInfo:mPendingLoader	Landroid/support/v4/app/LoaderManagerImpl$LoaderInfo;
      //   289: invokevirtual 144	android/support/v4/app/LoaderManagerImpl$LoaderInfo:destroy	()V
      //   292: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	293	0	this	LoaderInfo
      //   15	86	1	bool	boolean
      //   121	125	2	str	String
      //   222	12	3	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   122	135	222	finally
    }
    
    public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mId=");
      paramPrintWriter.print(this.mId);
      paramPrintWriter.print(" mArgs=");
      paramPrintWriter.println(this.mArgs);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mCallbacks=");
      paramPrintWriter.println(this.mCallbacks);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mLoader=");
      paramPrintWriter.println(this.mLoader);
      if (this.mLoader == null)
      {
        if (!this.mHaveData) {
          break label272;
        }
        label85:
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mHaveData=");
        paramPrintWriter.print(this.mHaveData);
        paramPrintWriter.print("  mDeliveredData=");
        paramPrintWriter.println(this.mDeliveredData);
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mData=");
        paramPrintWriter.println(this.mData);
      }
      for (;;)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mStarted=");
        paramPrintWriter.print(this.mStarted);
        paramPrintWriter.print(" mReportNextStart=");
        paramPrintWriter.print(this.mReportNextStart);
        paramPrintWriter.print(" mDestroyed=");
        paramPrintWriter.println(this.mDestroyed);
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mRetaining=");
        paramPrintWriter.print(this.mRetaining);
        paramPrintWriter.print(" mRetainingStarted=");
        paramPrintWriter.print(this.mRetainingStarted);
        paramPrintWriter.print(" mListenerRegistered=");
        paramPrintWriter.println(this.mListenerRegistered);
        if (this.mPendingLoader != null) {
          break label282;
        }
        return;
        this.mLoader.dump(paramString + "  ", paramFileDescriptor, paramPrintWriter, paramArrayOfString);
        break;
        label272:
        if (this.mDeliveredData) {
          break label85;
        }
      }
      label282:
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("Pending Loader ");
      paramPrintWriter.print(this.mPendingLoader);
      paramPrintWriter.println(":");
      this.mPendingLoader.dump(paramString + "  ", paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
    
    void finishRetain()
    {
      if (!this.mRetaining) {
        if (this.mStarted) {
          break label79;
        }
      }
      label79:
      while ((!this.mHaveData) || (this.mReportNextStart))
      {
        return;
        if (!LoaderManagerImpl.DEBUG) {}
        for (;;)
        {
          this.mRetaining = false;
          if ((this.mStarted == this.mRetainingStarted) || (this.mStarted)) {
            break;
          }
          stop();
          break;
          Log.v("LoaderManager", "  Finished Retaining: " + this);
        }
      }
      callOnLoadFinished(this.mLoader, this.mData);
    }
    
    public void onLoadComplete(Loader<Object> paramLoader, Object paramObject)
    {
      LoaderInfo localLoaderInfo;
      if (!LoaderManagerImpl.DEBUG)
      {
        if (this.mDestroyed) {
          break label126;
        }
        if (LoaderManagerImpl.this.mLoaders.get(this.mId) != this) {
          break label142;
        }
        localLoaderInfo = this.mPendingLoader;
        if (localLoaderInfo != null) {
          break label158;
        }
        if (this.mData == paramObject) {
          break label225;
        }
        label48:
        this.mData = paramObject;
        this.mHaveData = true;
        if (this.mStarted) {
          break label235;
        }
        label65:
        paramLoader = (LoaderInfo)LoaderManagerImpl.this.mInactiveLoaders.get(this.mId);
        if (paramLoader != null) {
          break label244;
        }
        label87:
        if (LoaderManagerImpl.this.mActivity != null) {
          break label275;
        }
      }
      label126:
      label142:
      label158:
      label225:
      label235:
      label244:
      label275:
      while (LoaderManagerImpl.this.hasRunningLoaders())
      {
        return;
        Log.v("LoaderManager", "onLoadComplete: " + this);
        break;
        if (!LoaderManagerImpl.DEBUG) {
          return;
        }
        Log.v("LoaderManager", "  Ignoring load complete -- destroyed");
        return;
        if (!LoaderManagerImpl.DEBUG) {
          return;
        }
        Log.v("LoaderManager", "  Ignoring load complete -- not active");
        return;
        if (!LoaderManagerImpl.DEBUG) {}
        for (;;)
        {
          this.mPendingLoader = null;
          LoaderManagerImpl.this.mLoaders.put(this.mId, null);
          destroy();
          LoaderManagerImpl.this.installLoader(localLoaderInfo);
          return;
          Log.v("LoaderManager", "  Switching to pending loader: " + localLoaderInfo);
        }
        if (!this.mHaveData) {
          break label48;
        }
        break label65;
        callOnLoadFinished(paramLoader, paramObject);
        break label65;
        if (paramLoader == this) {
          break label87;
        }
        paramLoader.mDeliveredData = false;
        paramLoader.destroy();
        LoaderManagerImpl.this.mInactiveLoaders.remove(this.mId);
        break label87;
      }
      LoaderManagerImpl.this.mActivity.mFragments.startPendingDeferredFragments();
    }
    
    void reportStart()
    {
      if (!this.mStarted) {}
      do
      {
        do
        {
          return;
        } while (!this.mReportNextStart);
        this.mReportNextStart = false;
      } while (!this.mHaveData);
      callOnLoadFinished(this.mLoader, this.mData);
    }
    
    void retain()
    {
      if (!LoaderManagerImpl.DEBUG) {}
      for (;;)
      {
        this.mRetaining = true;
        this.mRetainingStarted = this.mStarted;
        this.mStarted = false;
        this.mCallbacks = null;
        return;
        Log.v("LoaderManager", "  Retaining: " + this);
      }
    }
    
    void start()
    {
      if (!this.mRetaining)
      {
        if (this.mStarted) {
          break label53;
        }
        this.mStarted = true;
        if (LoaderManagerImpl.DEBUG) {
          break label54;
        }
        label25:
        if (this.mLoader == null) {
          break label83;
        }
      }
      for (;;)
      {
        if (this.mLoader != null) {
          break label114;
        }
        return;
        if (!this.mRetainingStarted) {
          break;
        }
        this.mStarted = true;
        return;
        label53:
        return;
        label54:
        Log.v("LoaderManager", "  Starting: " + this);
        break label25;
        label83:
        if (this.mCallbacks != null) {
          this.mLoader = this.mCallbacks.onCreateLoader(this.mId, this.mArgs);
        }
      }
      label114:
      if (!this.mLoader.getClass().isMemberClass()) {
        if (!this.mListenerRegistered) {
          break label189;
        }
      }
      for (;;)
      {
        this.mLoader.startLoading();
        return;
        if (Modifier.isStatic(this.mLoader.getClass().getModifiers())) {
          break;
        }
        throw new IllegalArgumentException("Object returned from onCreateLoader must not be a non-static inner member class: " + this.mLoader);
        label189:
        this.mLoader.registerListener(this.mId, this);
        this.mListenerRegistered = true;
      }
    }
    
    void stop()
    {
      if (!LoaderManagerImpl.DEBUG)
      {
        this.mStarted = false;
        if (!this.mRetaining) {
          break label48;
        }
      }
      label48:
      while ((this.mLoader == null) || (!this.mListenerRegistered))
      {
        return;
        Log.v("LoaderManager", "  Stopping: " + this);
        break;
      }
      this.mListenerRegistered = false;
      this.mLoader.unregisterListener(this);
      this.mLoader.stopLoading();
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder(64);
      localStringBuilder.append("LoaderInfo{");
      localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
      localStringBuilder.append(" #");
      localStringBuilder.append(this.mId);
      localStringBuilder.append(" : ");
      DebugUtils.buildShortClassTag(this.mLoader, localStringBuilder);
      localStringBuilder.append("}}");
      return localStringBuilder.toString();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/app/LoaderManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */