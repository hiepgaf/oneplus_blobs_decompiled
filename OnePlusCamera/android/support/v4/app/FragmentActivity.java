package android.support.v4.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.util.SimpleArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;

public class FragmentActivity
  extends Activity
{
  static final String FRAGMENTS_TAG = "android:support:fragments";
  private static final int HONEYCOMB = 11;
  static final int MSG_REALLY_STOPPED = 1;
  static final int MSG_RESUME_PENDING = 2;
  private static final String TAG = "FragmentActivity";
  SimpleArrayMap<String, LoaderManagerImpl> mAllLoaderManagers;
  boolean mCheckedForLoaderManager;
  final FragmentContainer mContainer = new FragmentContainer()
  {
    public View findViewById(int paramAnonymousInt)
    {
      return FragmentActivity.this.findViewById(paramAnonymousInt);
    }
    
    public boolean hasView()
    {
      Window localWindow = FragmentActivity.this.getWindow();
      if (localWindow == null) {}
      while (localWindow.peekDecorView() == null) {
        return false;
      }
      return true;
    }
  };
  boolean mCreated;
  final FragmentManagerImpl mFragments = new FragmentManagerImpl();
  final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default: 
        super.handleMessage(paramAnonymousMessage);
      case 1: 
        do
        {
          return;
        } while (!FragmentActivity.this.mStopped);
        FragmentActivity.this.doReallyStop(false);
        return;
      }
      FragmentActivity.this.onResumeFragments();
      FragmentActivity.this.mFragments.execPendingActions();
    }
  };
  LoaderManagerImpl mLoaderManager;
  boolean mLoadersStarted;
  boolean mOptionsMenuInvalidated;
  boolean mReallyStopped;
  boolean mResumed;
  boolean mRetaining;
  boolean mStopped;
  
  private void dumpViewHierarchy(String paramString, PrintWriter paramPrintWriter, View paramView)
  {
    int i = 0;
    paramPrintWriter.print(paramString);
    int j;
    if (paramView != null)
    {
      paramPrintWriter.println(viewToString(paramView));
      if (!(paramView instanceof ViewGroup)) {
        break label78;
      }
      paramView = (ViewGroup)paramView;
      j = paramView.getChildCount();
      if (j <= 0) {
        break label79;
      }
      paramString = paramString + "  ";
    }
    for (;;)
    {
      if (i >= j)
      {
        return;
        paramPrintWriter.println("null");
        return;
        label78:
        return;
        label79:
        return;
      }
      dumpViewHierarchy(paramString, paramPrintWriter, paramView.getChildAt(i));
      i += 1;
    }
  }
  
  private static String viewToString(View paramView)
  {
    char c3 = 'F';
    char c2 = '.';
    StringBuilder localStringBuilder = new StringBuilder(128);
    localStringBuilder.append(paramView.getClass().getName());
    localStringBuilder.append('{');
    localStringBuilder.append(Integer.toHexString(System.identityHashCode(paramView)));
    localStringBuilder.append(' ');
    char c1;
    label118:
    label135:
    label152:
    label169:
    label186:
    label203:
    label220:
    label264:
    label280:
    int i;
    switch (paramView.getVisibility())
    {
    default: 
      localStringBuilder.append('.');
      if (!paramView.isFocusable())
      {
        c1 = '.';
        localStringBuilder.append(c1);
        if (paramView.isEnabled()) {
          break label424;
        }
        c1 = '.';
        localStringBuilder.append(c1);
        if (paramView.willNotDraw()) {
          break label430;
        }
        c1 = 'D';
        localStringBuilder.append(c1);
        if (paramView.isHorizontalScrollBarEnabled()) {
          break label436;
        }
        c1 = '.';
        localStringBuilder.append(c1);
        if (paramView.isVerticalScrollBarEnabled()) {
          break label442;
        }
        c1 = '.';
        localStringBuilder.append(c1);
        if (paramView.isClickable()) {
          break label448;
        }
        c1 = '.';
        localStringBuilder.append(c1);
        if (paramView.isLongClickable()) {
          break label454;
        }
        c1 = '.';
        localStringBuilder.append(c1);
        localStringBuilder.append(' ');
        c1 = c3;
        if (!paramView.isFocused()) {
          c1 = '.';
        }
        localStringBuilder.append(c1);
        if (paramView.isSelected()) {
          break label460;
        }
        c1 = '.';
        localStringBuilder.append(c1);
        if (paramView.isPressed()) {
          break label466;
        }
        c1 = c2;
        localStringBuilder.append(c1);
        localStringBuilder.append(' ');
        localStringBuilder.append(paramView.getLeft());
        localStringBuilder.append(',');
        localStringBuilder.append(paramView.getTop());
        localStringBuilder.append('-');
        localStringBuilder.append(paramView.getRight());
        localStringBuilder.append(',');
        localStringBuilder.append(paramView.getBottom());
        i = paramView.getId();
        if (i != -1) {
          break label472;
        }
      }
      break;
    }
    label424:
    label430:
    label436:
    label442:
    label448:
    label454:
    label460:
    label466:
    label472:
    Object localObject;
    do
    {
      localStringBuilder.append("}");
      return localStringBuilder.toString();
      localStringBuilder.append('V');
      break;
      localStringBuilder.append('I');
      break;
      localStringBuilder.append('G');
      break;
      c1 = 'F';
      break label118;
      c1 = 'E';
      break label135;
      c1 = '.';
      break label152;
      c1 = 'H';
      break label169;
      c1 = 'V';
      break label186;
      c1 = 'C';
      break label203;
      c1 = 'L';
      break label220;
      c1 = 'S';
      break label264;
      c1 = 'P';
      break label280;
      localStringBuilder.append(" #");
      localStringBuilder.append(Integer.toHexString(i));
      localObject = paramView.getResources();
    } while ((i == 0) || (localObject == null));
    switch (0xFF000000 & i)
    {
    }
    for (;;)
    {
      try
      {
        paramView = ((Resources)localObject).getResourcePackageName(i);
        String str = ((Resources)localObject).getResourceTypeName(i);
        localObject = ((Resources)localObject).getResourceEntryName(i);
        localStringBuilder.append(" ");
        localStringBuilder.append(paramView);
        localStringBuilder.append(":");
        localStringBuilder.append(str);
        localStringBuilder.append("/");
        localStringBuilder.append((String)localObject);
      }
      catch (Resources.NotFoundException paramView) {}
      paramView = "android";
      continue;
      break;
      paramView = "app";
    }
  }
  
  void doReallyStop(boolean paramBoolean)
  {
    if (this.mReallyStopped) {
      return;
    }
    this.mReallyStopped = true;
    this.mRetaining = paramBoolean;
    this.mHandler.removeMessages(1);
    onReallyStop();
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    if (Build.VERSION.SDK_INT < 11) {}
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("Local FragmentActivity ");
    paramPrintWriter.print(Integer.toHexString(System.identityHashCode(this)));
    paramPrintWriter.println(" State:");
    String str = paramString + "  ";
    paramPrintWriter.print(str);
    paramPrintWriter.print("mCreated=");
    paramPrintWriter.print(this.mCreated);
    paramPrintWriter.print("mResumed=");
    paramPrintWriter.print(this.mResumed);
    paramPrintWriter.print(" mStopped=");
    paramPrintWriter.print(this.mStopped);
    paramPrintWriter.print(" mReallyStopped=");
    paramPrintWriter.println(this.mReallyStopped);
    paramPrintWriter.print(str);
    paramPrintWriter.print("mLoadersStarted=");
    paramPrintWriter.println(this.mLoadersStarted);
    if (this.mLoaderManager == null) {}
    for (;;)
    {
      this.mFragments.dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("View Hierarchy:");
      dumpViewHierarchy(paramString + "  ", paramPrintWriter, getWindow().getDecorView());
      return;
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("Loader Manager ");
      paramPrintWriter.print(Integer.toHexString(System.identityHashCode(this.mLoaderManager)));
      paramPrintWriter.println(":");
      this.mLoaderManager.dump(paramString + "  ", paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
  }
  
  public Object getLastCustomNonConfigurationInstance()
  {
    NonConfigurationInstances localNonConfigurationInstances = (NonConfigurationInstances)getLastNonConfigurationInstance();
    if (localNonConfigurationInstances == null) {
      return null;
    }
    return localNonConfigurationInstances.custom;
  }
  
  LoaderManagerImpl getLoaderManager(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.mAllLoaderManagers != null)
    {
      localLoaderManagerImpl = (LoaderManagerImpl)this.mAllLoaderManagers.get(paramString);
      if (localLoaderManagerImpl == null) {
        break label48;
      }
      localLoaderManagerImpl.updateActivity(this);
    }
    label48:
    while (!paramBoolean2)
    {
      return localLoaderManagerImpl;
      this.mAllLoaderManagers = new SimpleArrayMap();
      break;
    }
    LoaderManagerImpl localLoaderManagerImpl = new LoaderManagerImpl(paramString, this, paramBoolean1);
    this.mAllLoaderManagers.put(paramString, localLoaderManagerImpl);
    return localLoaderManagerImpl;
  }
  
  public FragmentManager getSupportFragmentManager()
  {
    return this.mFragments;
  }
  
  public LoaderManager getSupportLoaderManager()
  {
    if (this.mLoaderManager == null)
    {
      this.mCheckedForLoaderManager = true;
      this.mLoaderManager = getLoaderManager("(root)", this.mLoadersStarted, true);
      return this.mLoaderManager;
    }
    return this.mLoaderManager;
  }
  
  void invalidateSupportFragment(String paramString)
  {
    if (this.mAllLoaderManagers == null) {}
    LoaderManagerImpl localLoaderManagerImpl;
    do
    {
      return;
      localLoaderManagerImpl = (LoaderManagerImpl)this.mAllLoaderManagers.get(paramString);
    } while ((localLoaderManagerImpl == null) || (localLoaderManagerImpl.mRetaining));
    localLoaderManagerImpl.doDestroy();
    this.mAllLoaderManagers.remove(paramString);
  }
  
  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    this.mFragments.noteStateNotSaved();
    int i = paramInt1 >> 16;
    if (i == 0)
    {
      super.onActivityResult(paramInt1, paramInt2, paramIntent);
      return;
    }
    i -= 1;
    if (this.mFragments.mActive == null) {}
    while ((i < 0) || (i >= this.mFragments.mActive.size()))
    {
      Log.w("FragmentActivity", "Activity result fragment index out of range: 0x" + Integer.toHexString(paramInt1));
      return;
    }
    Fragment localFragment = (Fragment)this.mFragments.mActive.get(i);
    if (localFragment != null)
    {
      localFragment.onActivityResult(0xFFFF & paramInt1, paramInt2, paramIntent);
      return;
    }
    Log.w("FragmentActivity", "Activity result no fragment exists for index: 0x" + Integer.toHexString(paramInt1));
  }
  
  public void onAttachFragment(Fragment paramFragment) {}
  
  public void onBackPressed()
  {
    if (this.mFragments.popBackStackImmediate()) {
      return;
    }
    supportFinishAfterTransition();
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    this.mFragments.dispatchConfigurationChanged(paramConfiguration);
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    this.mFragments.attachActivity(this, this.mContainer, null);
    NonConfigurationInstances localNonConfigurationInstances;
    if (getLayoutInflater().getFactory() != null)
    {
      super.onCreate(paramBundle);
      localNonConfigurationInstances = (NonConfigurationInstances)getLastNonConfigurationInstance();
      if (localNonConfigurationInstances != null) {
        break label65;
      }
    }
    for (;;)
    {
      if (paramBundle != null) {
        break label77;
      }
      this.mFragments.dispatchCreate();
      return;
      getLayoutInflater().setFactory(this);
      break;
      label65:
      this.mAllLoaderManagers = localNonConfigurationInstances.loaders;
    }
    label77:
    Parcelable localParcelable = paramBundle.getParcelable("android:support:fragments");
    FragmentManagerImpl localFragmentManagerImpl = this.mFragments;
    if (localNonConfigurationInstances == null) {}
    for (paramBundle = null;; paramBundle = localNonConfigurationInstances.fragments)
    {
      localFragmentManagerImpl.restoreAllState(localParcelable, paramBundle);
      break;
    }
  }
  
  public boolean onCreatePanelMenu(int paramInt, Menu paramMenu)
  {
    if (paramInt != 0) {
      return super.onCreatePanelMenu(paramInt, paramMenu);
    }
    boolean bool1 = super.onCreatePanelMenu(paramInt, paramMenu);
    boolean bool2 = this.mFragments.dispatchCreateOptionsMenu(paramMenu, getMenuInflater());
    if (Build.VERSION.SDK_INT < 11) {
      return true;
    }
    return bool1 | bool2;
  }
  
  public View onCreateView(String paramString, @NonNull Context paramContext, @NonNull AttributeSet paramAttributeSet)
  {
    if ("fragment".equals(paramString))
    {
      View localView = this.mFragments.onCreateView(paramString, paramContext, paramAttributeSet);
      if (localView != null) {
        return localView;
      }
    }
    else
    {
      return super.onCreateView(paramString, paramContext, paramAttributeSet);
    }
    return super.onCreateView(paramString, paramContext, paramAttributeSet);
  }
  
  protected void onDestroy()
  {
    super.onDestroy();
    doReallyStop(false);
    this.mFragments.dispatchDestroy();
    if (this.mLoaderManager == null) {
      return;
    }
    this.mLoaderManager.doDestroy();
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if (Build.VERSION.SDK_INT >= 5) {}
    while ((paramInt != 4) || (paramKeyEvent.getRepeatCount() != 0)) {
      return super.onKeyDown(paramInt, paramKeyEvent);
    }
    onBackPressed();
    return true;
  }
  
  public void onLowMemory()
  {
    super.onLowMemory();
    this.mFragments.dispatchLowMemory();
  }
  
  public boolean onMenuItemSelected(int paramInt, MenuItem paramMenuItem)
  {
    if (!super.onMenuItemSelected(paramInt, paramMenuItem)) {}
    switch (paramInt)
    {
    default: 
      return false;
      return true;
    case 0: 
      return this.mFragments.dispatchOptionsItemSelected(paramMenuItem);
    }
    return this.mFragments.dispatchContextItemSelected(paramMenuItem);
  }
  
  protected void onNewIntent(Intent paramIntent)
  {
    super.onNewIntent(paramIntent);
    this.mFragments.noteStateNotSaved();
  }
  
  public void onPanelClosed(int paramInt, Menu paramMenu)
  {
    switch (paramInt)
    {
    }
    for (;;)
    {
      super.onPanelClosed(paramInt, paramMenu);
      return;
      this.mFragments.dispatchOptionsMenuClosed(paramMenu);
    }
  }
  
  protected void onPause()
  {
    super.onPause();
    this.mResumed = false;
    if (!this.mHandler.hasMessages(2)) {}
    for (;;)
    {
      this.mFragments.dispatchPause();
      return;
      this.mHandler.removeMessages(2);
      onResumeFragments();
    }
  }
  
  protected void onPostResume()
  {
    super.onPostResume();
    this.mHandler.removeMessages(2);
    onResumeFragments();
    this.mFragments.execPendingActions();
  }
  
  protected boolean onPrepareOptionsPanel(View paramView, Menu paramMenu)
  {
    return super.onPreparePanel(0, paramView, paramMenu);
  }
  
  public boolean onPreparePanel(int paramInt, View paramView, Menu paramMenu)
  {
    if (paramInt != 0) {}
    while (paramMenu == null) {
      return super.onPreparePanel(paramInt, paramView, paramMenu);
    }
    if (!this.mOptionsMenuInvalidated) {}
    for (;;)
    {
      return onPrepareOptionsPanel(paramView, paramMenu) | this.mFragments.dispatchPrepareOptionsMenu(paramMenu);
      this.mOptionsMenuInvalidated = false;
      paramMenu.clear();
      onCreatePanelMenu(paramInt, paramMenu);
    }
  }
  
  void onReallyStop()
  {
    if (!this.mLoadersStarted) {}
    for (;;)
    {
      this.mFragments.dispatchReallyStop();
      return;
      this.mLoadersStarted = false;
      if (this.mLoaderManager != null) {
        if (this.mRetaining) {
          this.mLoaderManager.doRetain();
        } else {
          this.mLoaderManager.doStop();
        }
      }
    }
  }
  
  protected void onResume()
  {
    super.onResume();
    this.mHandler.sendEmptyMessage(2);
    this.mResumed = true;
    this.mFragments.execPendingActions();
  }
  
  protected void onResumeFragments()
  {
    this.mFragments.dispatchResume();
  }
  
  public Object onRetainCustomNonConfigurationInstance()
  {
    return null;
  }
  
  public final Object onRetainNonConfigurationInstance()
  {
    int j = 0;
    Object localObject1;
    ArrayList localArrayList;
    int k;
    if (!this.mStopped)
    {
      localObject1 = onRetainCustomNonConfigurationInstance();
      localArrayList = this.mFragments.retainNonConfig();
      if (this.mAllLoaderManagers != null) {
        break label93;
      }
      k = 0;
      label33:
      if (localArrayList == null) {
        break label194;
      }
    }
    label93:
    label120:
    label189:
    label194:
    while ((k != 0) || (localObject1 != null))
    {
      Object localObject2 = new NonConfigurationInstances();
      ((NonConfigurationInstances)localObject2).activity = null;
      ((NonConfigurationInstances)localObject2).custom = localObject1;
      ((NonConfigurationInstances)localObject2).children = null;
      ((NonConfigurationInstances)localObject2).fragments = localArrayList;
      ((NonConfigurationInstances)localObject2).loaders = this.mAllLoaderManagers;
      return localObject2;
      doReallyStop(true);
      break;
      int m = this.mAllLoaderManagers.size();
      localObject2 = new LoaderManagerImpl[m];
      int i = m - 1;
      if (i < 0)
      {
        i = 0;
        k = i;
        if (j >= m) {
          break label33;
        }
        Object localObject3 = localObject2[j];
        if (((LoaderManagerImpl)localObject3).mRetaining) {
          break label189;
        }
        ((LoaderManagerImpl)localObject3).doDestroy();
        this.mAllLoaderManagers.remove(((LoaderManagerImpl)localObject3).mWho);
      }
      for (;;)
      {
        j += 1;
        break label120;
        localObject2[i] = ((LoaderManagerImpl)this.mAllLoaderManagers.valueAt(i));
        i -= 1;
        break;
        i = 1;
      }
    }
    return null;
  }
  
  protected void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    Parcelable localParcelable = this.mFragments.saveAllState();
    if (localParcelable == null) {
      return;
    }
    paramBundle.putParcelable("android:support:fragments", localParcelable);
  }
  
  protected void onStart()
  {
    super.onStart();
    this.mStopped = false;
    this.mReallyStopped = false;
    this.mHandler.removeMessages(1);
    if (this.mCreated) {}
    for (;;)
    {
      this.mFragments.noteStateNotSaved();
      this.mFragments.execPendingActions();
      if (!this.mLoadersStarted) {
        break;
      }
      this.mFragments.dispatchStart();
      if (this.mAllLoaderManagers != null) {
        break label161;
      }
      return;
      this.mCreated = true;
      this.mFragments.dispatchActivityCreated();
    }
    this.mLoadersStarted = true;
    if (this.mLoaderManager == null) {
      if (!this.mCheckedForLoaderManager) {
        break label118;
      }
    }
    for (;;)
    {
      this.mCheckedForLoaderManager = true;
      break;
      this.mLoaderManager.doStart();
      continue;
      label118:
      this.mLoaderManager = getLoaderManager("(root)", this.mLoadersStarted, false);
      if ((this.mLoaderManager != null) && (!this.mLoaderManager.mStarted)) {
        this.mLoaderManager.doStart();
      }
    }
    label161:
    int j = this.mAllLoaderManagers.size();
    LoaderManagerImpl[] arrayOfLoaderManagerImpl = new LoaderManagerImpl[j];
    int i = j - 1;
    for (;;)
    {
      if (i < 0)
      {
        i = 0;
        while (i < j)
        {
          LoaderManagerImpl localLoaderManagerImpl = arrayOfLoaderManagerImpl[i];
          localLoaderManagerImpl.finishRetain();
          localLoaderManagerImpl.doReportStart();
          i += 1;
        }
        break;
      }
      arrayOfLoaderManagerImpl[i] = ((LoaderManagerImpl)this.mAllLoaderManagers.valueAt(i));
      i -= 1;
    }
  }
  
  protected void onStop()
  {
    super.onStop();
    this.mStopped = true;
    this.mHandler.sendEmptyMessage(1);
    this.mFragments.dispatchStop();
  }
  
  public void setEnterSharedElementCallback(SharedElementCallback paramSharedElementCallback)
  {
    ActivityCompat.setEnterSharedElementCallback(this, paramSharedElementCallback);
  }
  
  public void setExitSharedElementCallback(SharedElementCallback paramSharedElementCallback)
  {
    ActivityCompat.setExitSharedElementCallback(this, paramSharedElementCallback);
  }
  
  public void startActivityForResult(Intent paramIntent, int paramInt)
  {
    if (paramInt == -1) {}
    while ((0xFFFF0000 & paramInt) == 0)
    {
      super.startActivityForResult(paramIntent, paramInt);
      return;
    }
    throw new IllegalArgumentException("Can only use lower 16 bits for requestCode");
  }
  
  public void startActivityFromFragment(Fragment paramFragment, Intent paramIntent, int paramInt)
  {
    if (paramInt != -1)
    {
      if ((0xFFFF0000 & paramInt) == 0) {
        super.startActivityForResult(paramIntent, (paramFragment.mIndex + 1 << 16) + (0xFFFF & paramInt));
      }
    }
    else
    {
      super.startActivityForResult(paramIntent, -1);
      return;
    }
    throw new IllegalArgumentException("Can only use lower 16 bits for requestCode");
  }
  
  public void supportFinishAfterTransition()
  {
    ActivityCompat.finishAfterTransition(this);
  }
  
  public void supportInvalidateOptionsMenu()
  {
    if (Build.VERSION.SDK_INT < 11)
    {
      this.mOptionsMenuInvalidated = true;
      return;
    }
    ActivityCompatHoneycomb.invalidateOptionsMenu(this);
  }
  
  public void supportPostponeEnterTransition()
  {
    ActivityCompat.postponeEnterTransition(this);
  }
  
  public void supportStartPostponedEnterTransition()
  {
    ActivityCompat.startPostponedEnterTransition(this);
  }
  
  static final class NonConfigurationInstances
  {
    Object activity;
    SimpleArrayMap<String, Object> children;
    Object custom;
    ArrayList<Fragment> fragments;
    SimpleArrayMap<String, LoaderManagerImpl> loaders;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/app/FragmentActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */