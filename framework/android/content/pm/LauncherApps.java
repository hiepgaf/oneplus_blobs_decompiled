package android.content.pm;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class LauncherApps
{
  static final boolean DEBUG = false;
  static final String TAG = "LauncherApps";
  private IOnAppsChangedListener.Stub mAppsChangedListener = new IOnAppsChangedListener.Stub()
  {
    public void onPackageAdded(UserHandle paramAnonymousUserHandle, String paramAnonymousString)
      throws RemoteException
    {
      synchronized (LauncherApps.this)
      {
        Iterator localIterator = LauncherApps.-get0(LauncherApps.this).iterator();
        if (localIterator.hasNext()) {
          ((LauncherApps.CallbackMessageHandler)localIterator.next()).postOnPackageAdded(paramAnonymousString, paramAnonymousUserHandle);
        }
      }
    }
    
    public void onPackageChanged(UserHandle paramAnonymousUserHandle, String paramAnonymousString)
      throws RemoteException
    {
      synchronized (LauncherApps.this)
      {
        Iterator localIterator = LauncherApps.-get0(LauncherApps.this).iterator();
        if (localIterator.hasNext()) {
          ((LauncherApps.CallbackMessageHandler)localIterator.next()).postOnPackageChanged(paramAnonymousString, paramAnonymousUserHandle);
        }
      }
    }
    
    public void onPackageRemoved(UserHandle paramAnonymousUserHandle, String paramAnonymousString)
      throws RemoteException
    {
      synchronized (LauncherApps.this)
      {
        Iterator localIterator = LauncherApps.-get0(LauncherApps.this).iterator();
        if (localIterator.hasNext()) {
          ((LauncherApps.CallbackMessageHandler)localIterator.next()).postOnPackageRemoved(paramAnonymousString, paramAnonymousUserHandle);
        }
      }
    }
    
    public void onPackagesAvailable(UserHandle paramAnonymousUserHandle, String[] paramAnonymousArrayOfString, boolean paramAnonymousBoolean)
      throws RemoteException
    {
      synchronized (LauncherApps.this)
      {
        Iterator localIterator = LauncherApps.-get0(LauncherApps.this).iterator();
        if (localIterator.hasNext()) {
          ((LauncherApps.CallbackMessageHandler)localIterator.next()).postOnPackagesAvailable(paramAnonymousArrayOfString, paramAnonymousUserHandle, paramAnonymousBoolean);
        }
      }
    }
    
    public void onPackagesSuspended(UserHandle paramAnonymousUserHandle, String[] paramAnonymousArrayOfString)
      throws RemoteException
    {
      synchronized (LauncherApps.this)
      {
        Iterator localIterator = LauncherApps.-get0(LauncherApps.this).iterator();
        if (localIterator.hasNext()) {
          ((LauncherApps.CallbackMessageHandler)localIterator.next()).postOnPackagesSuspended(paramAnonymousArrayOfString, paramAnonymousUserHandle);
        }
      }
    }
    
    public void onPackagesUnavailable(UserHandle paramAnonymousUserHandle, String[] paramAnonymousArrayOfString, boolean paramAnonymousBoolean)
      throws RemoteException
    {
      synchronized (LauncherApps.this)
      {
        Iterator localIterator = LauncherApps.-get0(LauncherApps.this).iterator();
        if (localIterator.hasNext()) {
          ((LauncherApps.CallbackMessageHandler)localIterator.next()).postOnPackagesUnavailable(paramAnonymousArrayOfString, paramAnonymousUserHandle, paramAnonymousBoolean);
        }
      }
    }
    
    public void onPackagesUnsuspended(UserHandle paramAnonymousUserHandle, String[] paramAnonymousArrayOfString)
      throws RemoteException
    {
      synchronized (LauncherApps.this)
      {
        Iterator localIterator = LauncherApps.-get0(LauncherApps.this).iterator();
        if (localIterator.hasNext()) {
          ((LauncherApps.CallbackMessageHandler)localIterator.next()).postOnPackagesUnsuspended(paramAnonymousArrayOfString, paramAnonymousUserHandle);
        }
      }
    }
    
    public void onShortcutChanged(UserHandle paramAnonymousUserHandle, String paramAnonymousString, ParceledListSlice arg3)
    {
      List localList = ???.getList();
      synchronized (LauncherApps.this)
      {
        Iterator localIterator = LauncherApps.-get0(LauncherApps.this).iterator();
        if (localIterator.hasNext()) {
          ((LauncherApps.CallbackMessageHandler)localIterator.next()).postOnShortcutChanged(paramAnonymousString, paramAnonymousUserHandle, localList);
        }
      }
    }
  };
  private List<CallbackMessageHandler> mCallbacks = new ArrayList();
  private Context mContext;
  private PackageManager mPm;
  private ILauncherApps mService;
  
  public LauncherApps(Context paramContext)
  {
    this(paramContext, ILauncherApps.Stub.asInterface(ServiceManager.getService("launcherapps")));
  }
  
  public LauncherApps(Context paramContext, ILauncherApps paramILauncherApps)
  {
    this.mContext = paramContext;
    this.mService = paramILauncherApps;
    this.mPm = paramContext.getPackageManager();
  }
  
  private void addCallbackLocked(Callback paramCallback, Handler paramHandler)
  {
    removeCallbackLocked(paramCallback);
    Handler localHandler = paramHandler;
    if (paramHandler == null) {
      localHandler = new Handler();
    }
    paramCallback = new CallbackMessageHandler(localHandler.getLooper(), paramCallback);
    this.mCallbacks.add(paramCallback);
  }
  
  private int findCallbackLocked(Callback paramCallback)
  {
    if (paramCallback == null) {
      throw new IllegalArgumentException("Callback cannot be null");
    }
    int j = this.mCallbacks.size();
    int i = 0;
    while (i < j)
    {
      if (CallbackMessageHandler.-get0((CallbackMessageHandler)this.mCallbacks.get(i)) == paramCallback) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  private ParcelFileDescriptor getShortcutIconFd(String paramString1, String paramString2, int paramInt)
  {
    try
    {
      paramString1 = this.mService.getShortcutIconFd(this.mContext.getPackageName(), paramString1, paramString2, paramInt);
      return paramString1;
    }
    catch (RemoteException paramString1)
    {
      throw paramString1.rethrowFromSystemServer();
    }
  }
  
  private void removeCallbackLocked(Callback paramCallback)
  {
    int i = findCallbackLocked(paramCallback);
    if (i >= 0) {
      this.mCallbacks.remove(i);
    }
  }
  
  private void startShortcut(String paramString1, String paramString2, Rect paramRect, Bundle paramBundle, int paramInt)
  {
    try
    {
      if (!this.mService.startShortcut(this.mContext.getPackageName(), paramString1, paramString2, paramRect, paramBundle, paramInt)) {
        throw new ActivityNotFoundException("Shortcut could not be started");
      }
    }
    catch (RemoteException paramString1)
    {
      throw paramString1.rethrowFromSystemServer();
    }
  }
  
  public List<LauncherActivityInfo> getActivityList(String paramString, UserHandle paramUserHandle)
  {
    try
    {
      localObject = this.mService.getLauncherActivities(paramString, paramUserHandle);
      if (localObject == null) {
        return Collections.EMPTY_LIST;
      }
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
    paramString = new ArrayList();
    Object localObject = ((ParceledListSlice)localObject).getList().iterator();
    while (((Iterator)localObject).hasNext())
    {
      ResolveInfo localResolveInfo = (ResolveInfo)((Iterator)localObject).next();
      paramString.add(new LauncherActivityInfo(this.mContext, localResolveInfo.activityInfo, paramUserHandle));
    }
    return paramString;
  }
  
  public ApplicationInfo getApplicationInfo(String paramString, int paramInt, UserHandle paramUserHandle)
  {
    try
    {
      paramString = this.mService.getApplicationInfo(paramString, paramInt, paramUserHandle);
      return paramString;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public Drawable getShortcutBadgedIconDrawable(ShortcutInfo paramShortcutInfo, int paramInt)
  {
    Drawable localDrawable = getShortcutIconDrawable(paramShortcutInfo, paramInt);
    if (localDrawable == null) {
      return null;
    }
    return this.mContext.getPackageManager().getUserBadgedIcon(localDrawable, paramShortcutInfo.getUserHandle());
  }
  
  /* Error */
  public Drawable getShortcutIconDrawable(ShortcutInfo paramShortcutInfo, int paramInt)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aload_1
    //   4: invokevirtual 234	android/content/pm/ShortcutInfo:hasIconFile	()Z
    //   7: ifeq +76 -> 83
    //   10: aload_0
    //   11: aload_1
    //   12: invokevirtual 237	android/content/pm/LauncherApps:getShortcutIconFd	(Landroid/content/pm/ShortcutInfo;)Landroid/os/ParcelFileDescriptor;
    //   15: astore 5
    //   17: aload 5
    //   19: ifnonnull +5 -> 24
    //   22: aconst_null
    //   23: areturn
    //   24: aload 5
    //   26: invokevirtual 243	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
    //   29: invokestatic 249	android/graphics/BitmapFactory:decodeFileDescriptor	(Ljava/io/FileDescriptor;)Landroid/graphics/Bitmap;
    //   32: astore_1
    //   33: aload_1
    //   34: ifnonnull +13 -> 47
    //   37: aload 4
    //   39: astore_1
    //   40: aload 5
    //   42: invokevirtual 252	android/os/ParcelFileDescriptor:close	()V
    //   45: aload_1
    //   46: areturn
    //   47: new 254	android/graphics/drawable/BitmapDrawable
    //   50: dup
    //   51: aload_0
    //   52: getfield 73	android/content/pm/LauncherApps:mContext	Landroid/content/Context;
    //   55: invokevirtual 258	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   58: aload_1
    //   59: invokespecial 261	android/graphics/drawable/BitmapDrawable:<init>	(Landroid/content/res/Resources;Landroid/graphics/Bitmap;)V
    //   62: astore_1
    //   63: goto -23 -> 40
    //   66: astore_1
    //   67: aload 5
    //   69: invokevirtual 252	android/os/ParcelFileDescriptor:close	()V
    //   72: aload_1
    //   73: athrow
    //   74: astore 4
    //   76: aload_1
    //   77: areturn
    //   78: astore 4
    //   80: goto -8 -> 72
    //   83: aload_1
    //   84: invokevirtual 264	android/content/pm/ShortcutInfo:hasIconResource	()Z
    //   87: ifeq +50 -> 137
    //   90: aload_1
    //   91: invokevirtual 267	android/content/pm/ShortcutInfo:getIconResourceId	()I
    //   94: istore_3
    //   95: iload_3
    //   96: ifne +5 -> 101
    //   99: aconst_null
    //   100: areturn
    //   101: aload_0
    //   102: aload_1
    //   103: invokevirtual 270	android/content/pm/ShortcutInfo:getPackage	()Ljava/lang/String;
    //   106: iconst_0
    //   107: aload_1
    //   108: invokevirtual 219	android/content/pm/ShortcutInfo:getUserHandle	()Landroid/os/UserHandle;
    //   111: invokevirtual 271	android/content/pm/LauncherApps:getApplicationInfo	(Ljava/lang/String;ILandroid/os/UserHandle;)Landroid/content/pm/ApplicationInfo;
    //   114: astore_1
    //   115: aload_0
    //   116: getfield 73	android/content/pm/LauncherApps:mContext	Landroid/content/Context;
    //   119: invokevirtual 81	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
    //   122: aload_1
    //   123: invokevirtual 275	android/content/pm/PackageManager:getResourcesForApplication	(Landroid/content/pm/ApplicationInfo;)Landroid/content/res/Resources;
    //   126: iload_3
    //   127: iload_2
    //   128: invokevirtual 281	android/content/res/Resources:getDrawableForDensity	(II)Landroid/graphics/drawable/Drawable;
    //   131: astore_1
    //   132: aload_1
    //   133: areturn
    //   134: astore_1
    //   135: aconst_null
    //   136: areturn
    //   137: aconst_null
    //   138: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	139	0	this	LauncherApps
    //   0	139	1	paramShortcutInfo	ShortcutInfo
    //   0	139	2	paramInt	int
    //   94	33	3	i	int
    //   1	37	4	localObject	Object
    //   74	1	4	localIOException1	java.io.IOException
    //   78	1	4	localIOException2	java.io.IOException
    //   15	53	5	localParcelFileDescriptor	ParcelFileDescriptor
    // Exception table:
    //   from	to	target	type
    //   24	33	66	finally
    //   47	63	66	finally
    //   40	45	74	java/io/IOException
    //   67	72	78	java/io/IOException
    //   90	95	134	android/content/pm/PackageManager$NameNotFoundException
    //   90	95	134	android/content/res/Resources$NotFoundException
    //   101	132	134	android/content/pm/PackageManager$NameNotFoundException
    //   101	132	134	android/content/res/Resources$NotFoundException
  }
  
  public ParcelFileDescriptor getShortcutIconFd(ShortcutInfo paramShortcutInfo)
  {
    return getShortcutIconFd(paramShortcutInfo.getPackage(), paramShortcutInfo.getId(), paramShortcutInfo.getUserId());
  }
  
  public ParcelFileDescriptor getShortcutIconFd(String paramString1, String paramString2, UserHandle paramUserHandle)
  {
    return getShortcutIconFd(paramString1, paramString2, paramUserHandle.getIdentifier());
  }
  
  @Deprecated
  public int getShortcutIconResId(ShortcutInfo paramShortcutInfo)
  {
    return paramShortcutInfo.getIconResourceId();
  }
  
  @Deprecated
  public int getShortcutIconResId(String paramString1, String paramString2, UserHandle paramUserHandle)
  {
    int i = 0;
    ShortcutQuery localShortcutQuery = new ShortcutQuery();
    localShortcutQuery.setPackage(paramString1);
    localShortcutQuery.setShortcutIds(Arrays.asList(new String[] { paramString2 }));
    localShortcutQuery.setQueryFlags(11);
    paramString1 = getShortcuts(localShortcutQuery, paramUserHandle);
    if (paramString1.size() > 0) {
      i = ((ShortcutInfo)paramString1.get(0)).getIconResourceId();
    }
    return i;
  }
  
  @Deprecated
  public List<ShortcutInfo> getShortcutInfo(String paramString, List<String> paramList, UserHandle paramUserHandle)
  {
    ShortcutQuery localShortcutQuery = new ShortcutQuery();
    localShortcutQuery.setPackage(paramString);
    localShortcutQuery.setShortcutIds(paramList);
    localShortcutQuery.setQueryFlags(11);
    return getShortcuts(localShortcutQuery, paramUserHandle);
  }
  
  public List<ShortcutInfo> getShortcuts(ShortcutQuery paramShortcutQuery, UserHandle paramUserHandle)
  {
    try
    {
      paramShortcutQuery = this.mService.getShortcuts(this.mContext.getPackageName(), paramShortcutQuery.mChangedSince, paramShortcutQuery.mPackage, paramShortcutQuery.mShortcutIds, paramShortcutQuery.mActivity, paramShortcutQuery.mQueryFlags, paramUserHandle).getList();
      return paramShortcutQuery;
    }
    catch (RemoteException paramShortcutQuery)
    {
      throw paramShortcutQuery.rethrowFromSystemServer();
    }
  }
  
  public boolean hasShortcutHostPermission()
  {
    try
    {
      boolean bool = this.mService.hasShortcutHostPermission(this.mContext.getPackageName());
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isActivityEnabled(ComponentName paramComponentName, UserHandle paramUserHandle)
  {
    try
    {
      boolean bool = this.mService.isActivityEnabled(paramComponentName, paramUserHandle);
      return bool;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public boolean isPackageEnabled(String paramString, UserHandle paramUserHandle)
  {
    try
    {
      boolean bool = this.mService.isPackageEnabled(paramString, paramUserHandle);
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void pinShortcuts(String paramString, List<String> paramList, UserHandle paramUserHandle)
  {
    try
    {
      this.mService.pinShortcuts(this.mContext.getPackageName(), paramString, paramList, paramUserHandle);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void registerCallback(Callback paramCallback)
  {
    registerCallback(paramCallback, null);
  }
  
  /* Error */
  public void registerCallback(Callback paramCallback, Handler paramHandler)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_1
    //   3: ifnull +55 -> 58
    //   6: aload_0
    //   7: aload_1
    //   8: invokespecial 144	android/content/pm/LauncherApps:findCallbackLocked	(Landroid/content/pm/LauncherApps$Callback;)I
    //   11: ifge +47 -> 58
    //   14: aload_0
    //   15: getfield 40	android/content/pm/LauncherApps:mCallbacks	Ljava/util/List;
    //   18: invokeinterface 118 1 0
    //   23: ifne +38 -> 61
    //   26: iconst_1
    //   27: istore_3
    //   28: aload_0
    //   29: aload_1
    //   30: aload_2
    //   31: invokespecial 373	android/content/pm/LauncherApps:addCallbackLocked	(Landroid/content/pm/LauncherApps$Callback;Landroid/os/Handler;)V
    //   34: iload_3
    //   35: ifeq +23 -> 58
    //   38: aload_0
    //   39: getfield 75	android/content/pm/LauncherApps:mService	Landroid/content/pm/ILauncherApps;
    //   42: aload_0
    //   43: getfield 73	android/content/pm/LauncherApps:mContext	Landroid/content/Context;
    //   46: invokevirtual 133	android/content/Context:getPackageName	()Ljava/lang/String;
    //   49: aload_0
    //   50: getfield 71	android/content/pm/LauncherApps:mAppsChangedListener	Landroid/content/pm/IOnAppsChangedListener$Stub;
    //   53: invokeinterface 377 3 0
    //   58: aload_0
    //   59: monitorexit
    //   60: return
    //   61: iconst_0
    //   62: istore_3
    //   63: goto -35 -> 28
    //   66: astore_1
    //   67: aload_1
    //   68: invokevirtual 142	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
    //   71: athrow
    //   72: astore_1
    //   73: aload_0
    //   74: monitorexit
    //   75: aload_1
    //   76: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	77	0	this	LauncherApps
    //   0	77	1	paramCallback	Callback
    //   0	77	2	paramHandler	Handler
    //   27	36	3	i	int
    // Exception table:
    //   from	to	target	type
    //   38	58	66	android/os/RemoteException
    //   6	26	72	finally
    //   28	34	72	finally
    //   38	58	72	finally
    //   67	72	72	finally
  }
  
  public LauncherActivityInfo resolveActivity(Intent paramIntent, UserHandle paramUserHandle)
  {
    try
    {
      paramIntent = this.mService.resolveActivity(paramIntent.getComponent(), paramUserHandle);
      if (paramIntent != null)
      {
        paramIntent = new LauncherActivityInfo(this.mContext, paramIntent, paramUserHandle);
        return paramIntent;
      }
    }
    catch (RemoteException paramIntent)
    {
      throw paramIntent.rethrowFromSystemServer();
    }
    return null;
  }
  
  public void startAppDetailsActivity(ComponentName paramComponentName, UserHandle paramUserHandle, Rect paramRect, Bundle paramBundle)
  {
    try
    {
      this.mService.showAppDetailsAsUser(paramComponentName, paramRect, paramBundle, paramUserHandle);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void startMainActivity(ComponentName paramComponentName, UserHandle paramUserHandle, Rect paramRect, Bundle paramBundle)
  {
    try
    {
      this.mService.startActivityAsUser(paramComponentName, paramRect, paramBundle, paramUserHandle);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void startShortcut(ShortcutInfo paramShortcutInfo, Rect paramRect, Bundle paramBundle)
  {
    startShortcut(paramShortcutInfo.getPackage(), paramShortcutInfo.getId(), paramRect, paramBundle, paramShortcutInfo.getUserId());
  }
  
  public void startShortcut(String paramString1, String paramString2, Rect paramRect, Bundle paramBundle, UserHandle paramUserHandle)
  {
    startShortcut(paramString1, paramString2, paramRect, paramBundle, paramUserHandle.getIdentifier());
  }
  
  /* Error */
  public void unregisterCallback(Callback paramCallback)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: aload_1
    //   4: invokespecial 89	android/content/pm/LauncherApps:removeCallbackLocked	(Landroid/content/pm/LauncherApps$Callback;)V
    //   7: aload_0
    //   8: getfield 40	android/content/pm/LauncherApps:mCallbacks	Ljava/util/List;
    //   11: invokeinterface 118 1 0
    //   16: istore_2
    //   17: iload_2
    //   18: ifne +16 -> 34
    //   21: aload_0
    //   22: getfield 75	android/content/pm/LauncherApps:mService	Landroid/content/pm/ILauncherApps;
    //   25: aload_0
    //   26: getfield 71	android/content/pm/LauncherApps:mAppsChangedListener	Landroid/content/pm/IOnAppsChangedListener$Stub;
    //   29: invokeinterface 407 2 0
    //   34: aload_0
    //   35: monitorexit
    //   36: return
    //   37: astore_1
    //   38: aload_1
    //   39: invokevirtual 142	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
    //   42: athrow
    //   43: astore_1
    //   44: aload_0
    //   45: monitorexit
    //   46: aload_1
    //   47: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	48	0	this	LauncherApps
    //   0	48	1	paramCallback	Callback
    //   16	2	2	i	int
    // Exception table:
    //   from	to	target	type
    //   21	34	37	android/os/RemoteException
    //   2	17	43	finally
    //   21	34	43	finally
    //   38	43	43	finally
  }
  
  public static abstract class Callback
  {
    public abstract void onPackageAdded(String paramString, UserHandle paramUserHandle);
    
    public abstract void onPackageChanged(String paramString, UserHandle paramUserHandle);
    
    public abstract void onPackageRemoved(String paramString, UserHandle paramUserHandle);
    
    public abstract void onPackagesAvailable(String[] paramArrayOfString, UserHandle paramUserHandle, boolean paramBoolean);
    
    public void onPackagesSuspended(String[] paramArrayOfString, UserHandle paramUserHandle) {}
    
    public abstract void onPackagesUnavailable(String[] paramArrayOfString, UserHandle paramUserHandle, boolean paramBoolean);
    
    public void onPackagesUnsuspended(String[] paramArrayOfString, UserHandle paramUserHandle) {}
    
    public void onShortcutsChanged(String paramString, List<ShortcutInfo> paramList, UserHandle paramUserHandle) {}
  }
  
  private static class CallbackMessageHandler
    extends Handler
  {
    private static final int MSG_ADDED = 1;
    private static final int MSG_AVAILABLE = 4;
    private static final int MSG_CHANGED = 3;
    private static final int MSG_REMOVED = 2;
    private static final int MSG_SHORTCUT_CHANGED = 8;
    private static final int MSG_SUSPENDED = 6;
    private static final int MSG_UNAVAILABLE = 5;
    private static final int MSG_UNSUSPENDED = 7;
    private LauncherApps.Callback mCallback;
    
    public CallbackMessageHandler(Looper paramLooper, LauncherApps.Callback paramCallback)
    {
      super(null, true);
      this.mCallback = paramCallback;
    }
    
    public void handleMessage(Message paramMessage)
    {
      CallbackInfo localCallbackInfo;
      if ((this.mCallback != null) && ((paramMessage.obj instanceof CallbackInfo))) {
        localCallbackInfo = (CallbackInfo)paramMessage.obj;
      }
      switch (paramMessage.what)
      {
      default: 
        return;
        return;
      case 1: 
        this.mCallback.onPackageAdded(localCallbackInfo.packageName, localCallbackInfo.user);
        return;
      case 2: 
        this.mCallback.onPackageRemoved(localCallbackInfo.packageName, localCallbackInfo.user);
        return;
      case 3: 
        this.mCallback.onPackageChanged(localCallbackInfo.packageName, localCallbackInfo.user);
        return;
      case 4: 
        this.mCallback.onPackagesAvailable(localCallbackInfo.packageNames, localCallbackInfo.user, localCallbackInfo.replacing);
        return;
      case 5: 
        this.mCallback.onPackagesUnavailable(localCallbackInfo.packageNames, localCallbackInfo.user, localCallbackInfo.replacing);
        return;
      case 6: 
        this.mCallback.onPackagesSuspended(localCallbackInfo.packageNames, localCallbackInfo.user);
        return;
      case 7: 
        this.mCallback.onPackagesUnsuspended(localCallbackInfo.packageNames, localCallbackInfo.user);
        return;
      }
      this.mCallback.onShortcutsChanged(localCallbackInfo.packageName, localCallbackInfo.shortcuts, localCallbackInfo.user);
    }
    
    public void postOnPackageAdded(String paramString, UserHandle paramUserHandle)
    {
      CallbackInfo localCallbackInfo = new CallbackInfo(null);
      localCallbackInfo.packageName = paramString;
      localCallbackInfo.user = paramUserHandle;
      obtainMessage(1, localCallbackInfo).sendToTarget();
    }
    
    public void postOnPackageChanged(String paramString, UserHandle paramUserHandle)
    {
      CallbackInfo localCallbackInfo = new CallbackInfo(null);
      localCallbackInfo.packageName = paramString;
      localCallbackInfo.user = paramUserHandle;
      obtainMessage(3, localCallbackInfo).sendToTarget();
    }
    
    public void postOnPackageRemoved(String paramString, UserHandle paramUserHandle)
    {
      CallbackInfo localCallbackInfo = new CallbackInfo(null);
      localCallbackInfo.packageName = paramString;
      localCallbackInfo.user = paramUserHandle;
      obtainMessage(2, localCallbackInfo).sendToTarget();
    }
    
    public void postOnPackagesAvailable(String[] paramArrayOfString, UserHandle paramUserHandle, boolean paramBoolean)
    {
      CallbackInfo localCallbackInfo = new CallbackInfo(null);
      localCallbackInfo.packageNames = paramArrayOfString;
      localCallbackInfo.replacing = paramBoolean;
      localCallbackInfo.user = paramUserHandle;
      obtainMessage(4, localCallbackInfo).sendToTarget();
    }
    
    public void postOnPackagesSuspended(String[] paramArrayOfString, UserHandle paramUserHandle)
    {
      CallbackInfo localCallbackInfo = new CallbackInfo(null);
      localCallbackInfo.packageNames = paramArrayOfString;
      localCallbackInfo.user = paramUserHandle;
      obtainMessage(6, localCallbackInfo).sendToTarget();
    }
    
    public void postOnPackagesUnavailable(String[] paramArrayOfString, UserHandle paramUserHandle, boolean paramBoolean)
    {
      CallbackInfo localCallbackInfo = new CallbackInfo(null);
      localCallbackInfo.packageNames = paramArrayOfString;
      localCallbackInfo.replacing = paramBoolean;
      localCallbackInfo.user = paramUserHandle;
      obtainMessage(5, localCallbackInfo).sendToTarget();
    }
    
    public void postOnPackagesUnsuspended(String[] paramArrayOfString, UserHandle paramUserHandle)
    {
      CallbackInfo localCallbackInfo = new CallbackInfo(null);
      localCallbackInfo.packageNames = paramArrayOfString;
      localCallbackInfo.user = paramUserHandle;
      obtainMessage(7, localCallbackInfo).sendToTarget();
    }
    
    public void postOnShortcutChanged(String paramString, UserHandle paramUserHandle, List<ShortcutInfo> paramList)
    {
      CallbackInfo localCallbackInfo = new CallbackInfo(null);
      localCallbackInfo.packageName = paramString;
      localCallbackInfo.user = paramUserHandle;
      localCallbackInfo.shortcuts = paramList;
      obtainMessage(8, localCallbackInfo).sendToTarget();
    }
    
    private static class CallbackInfo
    {
      String packageName;
      String[] packageNames;
      boolean replacing;
      List<ShortcutInfo> shortcuts;
      UserHandle user;
    }
  }
  
  public static class ShortcutQuery
  {
    @Deprecated
    public static final int FLAG_GET_ALL_KINDS = 11;
    @Deprecated
    public static final int FLAG_GET_DYNAMIC = 1;
    public static final int FLAG_GET_KEY_FIELDS_ONLY = 4;
    @Deprecated
    public static final int FLAG_GET_MANIFEST = 8;
    @Deprecated
    public static final int FLAG_GET_PINNED = 2;
    public static final int FLAG_MATCH_ALL_KINDS = 11;
    public static final int FLAG_MATCH_DYNAMIC = 1;
    public static final int FLAG_MATCH_MANIFEST = 8;
    public static final int FLAG_MATCH_PINNED = 2;
    ComponentName mActivity;
    long mChangedSince;
    String mPackage;
    int mQueryFlags;
    List<String> mShortcutIds;
    
    public ShortcutQuery setActivity(ComponentName paramComponentName)
    {
      this.mActivity = paramComponentName;
      return this;
    }
    
    public ShortcutQuery setChangedSince(long paramLong)
    {
      this.mChangedSince = paramLong;
      return this;
    }
    
    public ShortcutQuery setPackage(String paramString)
    {
      this.mPackage = paramString;
      return this;
    }
    
    public ShortcutQuery setQueryFlags(int paramInt)
    {
      this.mQueryFlags = paramInt;
      return this;
    }
    
    public ShortcutQuery setShortcutIds(List<String> paramList)
    {
      this.mShortcutIds = paramList;
      return this;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/LauncherApps.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */