package android.app;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.os.Handler;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public abstract class FragmentHostCallback<E>
  extends FragmentContainer
{
  private final Activity mActivity;
  private ArrayMap<String, LoaderManager> mAllLoaderManagers;
  private boolean mCheckedForLoaderManager;
  final Context mContext;
  final FragmentManagerImpl mFragmentManager = new FragmentManagerImpl();
  private final Handler mHandler;
  private LoaderManagerImpl mLoaderManager;
  private boolean mLoadersStarted;
  private boolean mRetainLoaders;
  final int mWindowAnimations;
  
  FragmentHostCallback(Activity paramActivity)
  {
    this(paramActivity, paramActivity, paramActivity.mHandler, 0);
  }
  
  FragmentHostCallback(Activity paramActivity, Context paramContext, Handler paramHandler, int paramInt)
  {
    this.mActivity = paramActivity;
    this.mContext = paramContext;
    this.mHandler = paramHandler;
    this.mWindowAnimations = paramInt;
  }
  
  public FragmentHostCallback(Context paramContext, Handler paramHandler, int paramInt)
  {
    this(null, paramContext, paramHandler, paramInt);
  }
  
  void doLoaderDestroy()
  {
    if (this.mLoaderManager == null) {
      return;
    }
    this.mLoaderManager.doDestroy();
  }
  
  void doLoaderRetain()
  {
    if (this.mLoaderManager == null) {
      return;
    }
    this.mLoaderManager.doRetain();
  }
  
  void doLoaderStart()
  {
    if (this.mLoadersStarted) {
      return;
    }
    this.mLoadersStarted = true;
    if (this.mLoaderManager != null) {
      this.mLoaderManager.doStart();
    }
    for (;;)
    {
      this.mCheckedForLoaderManager = true;
      return;
      if (!this.mCheckedForLoaderManager) {
        this.mLoaderManager = getLoaderManager("(root)", this.mLoadersStarted, false);
      }
    }
  }
  
  void doLoaderStop(boolean paramBoolean)
  {
    this.mRetainLoaders = paramBoolean;
    if (this.mLoaderManager == null) {
      return;
    }
    if (!this.mLoadersStarted) {
      return;
    }
    this.mLoadersStarted = false;
    if (paramBoolean)
    {
      this.mLoaderManager.doRetain();
      return;
    }
    this.mLoaderManager.doStop();
  }
  
  void dumpLoaders(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mLoadersStarted=");
    paramPrintWriter.println(this.mLoadersStarted);
    if (this.mLoaderManager != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("Loader Manager ");
      paramPrintWriter.print(Integer.toHexString(System.identityHashCode(this.mLoaderManager)));
      paramPrintWriter.println(":");
      this.mLoaderManager.dump(paramString + "  ", paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
  }
  
  Activity getActivity()
  {
    return this.mActivity;
  }
  
  Context getContext()
  {
    return this.mContext;
  }
  
  FragmentManagerImpl getFragmentManagerImpl()
  {
    return this.mFragmentManager;
  }
  
  Handler getHandler()
  {
    return this.mHandler;
  }
  
  LoaderManagerImpl getLoaderManager(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.mAllLoaderManagers == null) {
      this.mAllLoaderManagers = new ArrayMap();
    }
    LoaderManagerImpl localLoaderManagerImpl = (LoaderManagerImpl)this.mAllLoaderManagers.get(paramString);
    if (localLoaderManagerImpl == null)
    {
      if (paramBoolean2)
      {
        localLoaderManagerImpl = new LoaderManagerImpl(paramString, this, paramBoolean1);
        this.mAllLoaderManagers.put(paramString, localLoaderManagerImpl);
      }
      return localLoaderManagerImpl;
    }
    localLoaderManagerImpl.updateHostController(this);
    return localLoaderManagerImpl;
  }
  
  LoaderManagerImpl getLoaderManagerImpl()
  {
    if (this.mLoaderManager != null) {
      return this.mLoaderManager;
    }
    this.mCheckedForLoaderManager = true;
    this.mLoaderManager = getLoaderManager("(root)", this.mLoadersStarted, true);
    return this.mLoaderManager;
  }
  
  boolean getRetainLoaders()
  {
    return this.mRetainLoaders;
  }
  
  void inactivateFragment(String paramString)
  {
    LoaderManagerImpl localLoaderManagerImpl;
    if (this.mAllLoaderManagers != null)
    {
      localLoaderManagerImpl = (LoaderManagerImpl)this.mAllLoaderManagers.get(paramString);
      if ((localLoaderManagerImpl != null) && (!localLoaderManagerImpl.mRetaining)) {}
    }
    else
    {
      return;
    }
    localLoaderManagerImpl.doDestroy();
    this.mAllLoaderManagers.remove(paramString);
  }
  
  public void onAttachFragment(Fragment paramFragment) {}
  
  public void onDump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString) {}
  
  public View onFindViewById(int paramInt)
  {
    return null;
  }
  
  public abstract E onGetHost();
  
  public LayoutInflater onGetLayoutInflater()
  {
    return (LayoutInflater)this.mContext.getSystemService("layout_inflater");
  }
  
  public int onGetWindowAnimations()
  {
    return this.mWindowAnimations;
  }
  
  public boolean onHasView()
  {
    return true;
  }
  
  public boolean onHasWindowAnimations()
  {
    return true;
  }
  
  public void onInvalidateOptionsMenu() {}
  
  public void onRequestPermissionsFromFragment(Fragment paramFragment, String[] paramArrayOfString, int paramInt) {}
  
  public boolean onShouldSaveFragmentState(Fragment paramFragment)
  {
    return true;
  }
  
  public void onStartActivityFromFragment(Fragment paramFragment, Intent paramIntent, int paramInt, Bundle paramBundle)
  {
    if (paramInt != -1) {
      throw new IllegalStateException("Starting activity with a requestCode requires a FragmentActivity host");
    }
    this.mContext.startActivity(paramIntent);
  }
  
  public void onStartIntentSenderFromFragment(Fragment paramFragment, IntentSender paramIntentSender, int paramInt1, Intent paramIntent, int paramInt2, int paramInt3, int paramInt4, Bundle paramBundle)
    throws IntentSender.SendIntentException
  {
    if (paramInt1 != -1) {
      throw new IllegalStateException("Starting intent sender with a requestCode requires a FragmentActivity host");
    }
    this.mContext.startIntentSender(paramIntentSender, paramIntent, paramInt2, paramInt3, paramInt4, paramBundle);
  }
  
  public boolean onUseFragmentManagerInflaterFactory()
  {
    return false;
  }
  
  void reportLoaderStart()
  {
    if (this.mAllLoaderManagers != null)
    {
      int j = this.mAllLoaderManagers.size();
      LoaderManagerImpl[] arrayOfLoaderManagerImpl = new LoaderManagerImpl[j];
      int i = j - 1;
      while (i >= 0)
      {
        arrayOfLoaderManagerImpl[i] = ((LoaderManagerImpl)this.mAllLoaderManagers.valueAt(i));
        i -= 1;
      }
      i = 0;
      while (i < j)
      {
        LoaderManagerImpl localLoaderManagerImpl = arrayOfLoaderManagerImpl[i];
        localLoaderManagerImpl.finishRetain();
        localLoaderManagerImpl.doReportStart();
        i += 1;
      }
    }
  }
  
  void restoreLoaderNonConfig(ArrayMap<String, LoaderManager> paramArrayMap)
  {
    if (paramArrayMap != null)
    {
      int i = 0;
      int j = paramArrayMap.size();
      while (i < j)
      {
        ((LoaderManagerImpl)paramArrayMap.valueAt(i)).updateHostController(this);
        i += 1;
      }
    }
    this.mAllLoaderManagers = paramArrayMap;
  }
  
  ArrayMap<String, LoaderManager> retainLoaderNonConfig()
  {
    int k = 0;
    int j = 0;
    if (this.mAllLoaderManagers != null)
    {
      int m = this.mAllLoaderManagers.size();
      LoaderManagerImpl[] arrayOfLoaderManagerImpl = new LoaderManagerImpl[m];
      int i = m - 1;
      while (i >= 0)
      {
        arrayOfLoaderManagerImpl[i] = ((LoaderManagerImpl)this.mAllLoaderManagers.valueAt(i));
        i -= 1;
      }
      boolean bool = getRetainLoaders();
      k = 0;
      i = j;
      j = k;
      k = i;
      if (j < m)
      {
        LoaderManagerImpl localLoaderManagerImpl = arrayOfLoaderManagerImpl[j];
        if ((!localLoaderManagerImpl.mRetaining) && (bool))
        {
          if (!localLoaderManagerImpl.mStarted) {
            localLoaderManagerImpl.doStart();
          }
          localLoaderManagerImpl.doRetain();
        }
        if (localLoaderManagerImpl.mRetaining) {
          i = 1;
        }
        for (;;)
        {
          j += 1;
          break;
          localLoaderManagerImpl.doDestroy();
          this.mAllLoaderManagers.remove(localLoaderManagerImpl.mWho);
        }
      }
    }
    if (k != 0) {
      return this.mAllLoaderManagers;
    }
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/FragmentHostCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */