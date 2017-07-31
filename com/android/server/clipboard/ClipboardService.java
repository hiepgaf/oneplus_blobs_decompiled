package com.android.server.clipboard;

import android.app.ActivityManagerNative;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.IActivityManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipData.Item;
import android.content.ClipDescription;
import android.content.ContentProvider;
import android.content.Context;
import android.content.IClipboard.Stub;
import android.content.IOnPrimaryClipChangedListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IUserManager;
import android.os.Parcel;
import android.os.Process;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.Slog;
import android.util.SparseArray;
import java.util.HashSet;
import java.util.List;

public class ClipboardService
  extends IClipboard.Stub
{
  private static final String TAG = "ClipboardService";
  private final IActivityManager mAm;
  private final AppOpsManager mAppOps;
  private SparseArray<PerUserClipboard> mClipboards = new SparseArray();
  private final Context mContext;
  private final IBinder mPermissionOwner;
  private final PackageManager mPm;
  private final IUserManager mUm;
  
  public ClipboardService(Context paramContext)
  {
    this.mContext = paramContext;
    this.mAm = ActivityManagerNative.getDefault();
    this.mPm = paramContext.getPackageManager();
    this.mUm = ((IUserManager)ServiceManager.getService("user"));
    this.mAppOps = ((AppOpsManager)paramContext.getSystemService("appops"));
    paramContext = null;
    try
    {
      IBinder localIBinder = this.mAm.newUriPermissionOwner("clipboard");
      paramContext = localIBinder;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Slog.w("clipboard", "AM dead", localRemoteException);
      }
    }
    this.mPermissionOwner = paramContext;
    paramContext = new IntentFilter();
    paramContext.addAction("android.intent.action.USER_REMOVED");
    this.mContext.registerReceiver(new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        if ("android.intent.action.USER_REMOVED".equals(paramAnonymousIntent.getAction())) {
          ClipboardService.-wrap0(ClipboardService.this, paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", 0));
        }
      }
    }, paramContext);
  }
  
  private final void addActiveOwnerLocked(int paramInt, String paramString)
  {
    Object localObject = AppGlobals.getPackageManager();
    int i = UserHandle.getCallingUserId();
    long l = Binder.clearCallingIdentity();
    PerUserClipboard localPerUserClipboard;
    for (;;)
    {
      try
      {
        localObject = ((IPackageManager)localObject).getPackageInfo(paramString, 0, i);
        if (localObject == null) {
          throw new IllegalArgumentException("Unknown package " + paramString);
        }
      }
      catch (RemoteException localRemoteException)
      {
        Binder.restoreCallingIdentity(l);
        localPerUserClipboard = getClipboard();
        if ((localPerUserClipboard.primaryClip != null) && (!localPerUserClipboard.activePermissionOwners.contains(paramString))) {
          break;
        }
        return;
        if (!UserHandle.isSameApp(localPerUserClipboard.applicationInfo.uid, paramInt)) {
          throw new SecurityException("Calling uid " + paramInt + " does not own package " + paramString);
        }
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
      Binder.restoreCallingIdentity(l);
    }
    int j = localPerUserClipboard.primaryClip.getItemCount();
    i = 0;
    while (i < j)
    {
      grantItemLocked(localPerUserClipboard.primaryClip.getItemAt(i), paramString, UserHandle.getUserId(paramInt));
      i += 1;
    }
    localPerUserClipboard.activePermissionOwners.add(paramString);
  }
  
  private final void checkDataOwnerLocked(ClipData paramClipData, int paramInt)
  {
    int j = paramClipData.getItemCount();
    int i = 0;
    while (i < j)
    {
      checkItemOwnerLocked(paramClipData.getItemAt(i), paramInt);
      i += 1;
    }
  }
  
  private final void checkItemOwnerLocked(ClipData.Item paramItem, int paramInt)
  {
    if (paramItem.getUri() != null) {
      checkUriOwnerLocked(paramItem.getUri(), paramInt);
    }
    paramItem = paramItem.getIntent();
    if ((paramItem != null) && (paramItem.getData() != null)) {
      checkUriOwnerLocked(paramItem.getData(), paramInt);
    }
  }
  
  private final void checkUriOwnerLocked(Uri paramUri, int paramInt)
  {
    if (!"content".equals(paramUri.getScheme())) {
      return;
    }
    long l = Binder.clearCallingIdentity();
    try
    {
      this.mAm.checkGrantUriPermission(paramInt, null, ContentProvider.getUriWithoutUserId(paramUri), 1, ContentProvider.getUserIdFromUri(paramUri, UserHandle.getUserId(paramInt)));
      Binder.restoreCallingIdentity(l);
      return;
    }
    catch (RemoteException paramUri)
    {
      paramUri = paramUri;
      Binder.restoreCallingIdentity(l);
      return;
    }
    finally
    {
      paramUri = finally;
      Binder.restoreCallingIdentity(l);
      throw paramUri;
    }
  }
  
  private PerUserClipboard getClipboard()
  {
    return getClipboard(UserHandle.getCallingUserId());
  }
  
  private PerUserClipboard getClipboard(int paramInt)
  {
    synchronized (this.mClipboards)
    {
      PerUserClipboard localPerUserClipboard2 = (PerUserClipboard)this.mClipboards.get(paramInt);
      PerUserClipboard localPerUserClipboard1 = localPerUserClipboard2;
      if (localPerUserClipboard2 == null)
      {
        localPerUserClipboard1 = new PerUserClipboard(paramInt);
        this.mClipboards.put(paramInt, localPerUserClipboard1);
      }
      return localPerUserClipboard1;
    }
  }
  
  private final void grantItemLocked(ClipData.Item paramItem, String paramString, int paramInt)
  {
    if (paramItem.getUri() != null) {
      grantUriLocked(paramItem.getUri(), paramString, paramInt);
    }
    paramItem = paramItem.getIntent();
    if ((paramItem != null) && (paramItem.getData() != null)) {
      grantUriLocked(paramItem.getData(), paramString, paramInt);
    }
  }
  
  private final void grantUriLocked(Uri paramUri, String paramString, int paramInt)
  {
    long l = Binder.clearCallingIdentity();
    try
    {
      int i = ContentProvider.getUserIdFromUri(paramUri, paramInt);
      paramUri = ContentProvider.getUriWithoutUserId(paramUri);
      this.mAm.grantUriPermissionFromOwner(this.mPermissionOwner, Process.myUid(), paramString, paramUri, 1, i, paramInt);
      Binder.restoreCallingIdentity(l);
      return;
    }
    catch (RemoteException paramUri)
    {
      paramUri = paramUri;
      Binder.restoreCallingIdentity(l);
      return;
    }
    finally
    {
      paramUri = finally;
      Binder.restoreCallingIdentity(l);
      throw paramUri;
    }
  }
  
  private void removeClipboard(int paramInt)
  {
    synchronized (this.mClipboards)
    {
      this.mClipboards.remove(paramInt);
      return;
    }
  }
  
  private final void revokeItemLocked(ClipData.Item paramItem)
  {
    if (paramItem.getUri() != null) {
      revokeUriLocked(paramItem.getUri());
    }
    paramItem = paramItem.getIntent();
    if ((paramItem != null) && (paramItem.getData() != null)) {
      revokeUriLocked(paramItem.getData());
    }
  }
  
  private final void revokeUriLocked(Uri paramUri)
  {
    int i = ContentProvider.getUserIdFromUri(paramUri, UserHandle.getUserId(Binder.getCallingUid()));
    long l = Binder.clearCallingIdentity();
    try
    {
      paramUri = ContentProvider.getUriWithoutUserId(paramUri);
      this.mAm.revokeUriPermissionFromOwner(this.mPermissionOwner, paramUri, 3, i);
      Binder.restoreCallingIdentity(l);
      return;
    }
    catch (RemoteException paramUri)
    {
      paramUri = paramUri;
      Binder.restoreCallingIdentity(l);
      return;
    }
    finally
    {
      paramUri = finally;
      Binder.restoreCallingIdentity(l);
      throw paramUri;
    }
  }
  
  private final void revokeUris(PerUserClipboard paramPerUserClipboard)
  {
    if (paramPerUserClipboard.primaryClip == null) {
      return;
    }
    int j = paramPerUserClipboard.primaryClip.getItemCount();
    int i = 0;
    while (i < j)
    {
      revokeItemLocked(paramPerUserClipboard.primaryClip.getItemAt(i));
      i += 1;
    }
  }
  
  public void addPrimaryClipChangedListener(IOnPrimaryClipChangedListener paramIOnPrimaryClipChangedListener, String paramString)
  {
    try
    {
      getClipboard().primaryClipListeners.register(paramIOnPrimaryClipChangedListener, new ListenerInfo(Binder.getCallingUid(), paramString));
      return;
    }
    finally
    {
      paramIOnPrimaryClipChangedListener = finally;
      throw paramIOnPrimaryClipChangedListener;
    }
  }
  
  public ClipData getPrimaryClip(String paramString)
  {
    try
    {
      int i = this.mAppOps.noteOp(29, Binder.getCallingUid(), paramString);
      if (i != 0) {
        return null;
      }
      addActiveOwnerLocked(Binder.getCallingUid(), paramString);
      paramString = getClipboard().primaryClip;
      return paramString;
    }
    finally {}
  }
  
  public ClipDescription getPrimaryClipDescription(String paramString)
  {
    Object localObject = null;
    try
    {
      int i = this.mAppOps.checkOp(29, Binder.getCallingUid(), paramString);
      if (i != 0) {
        return null;
      }
      PerUserClipboard localPerUserClipboard = getClipboard();
      paramString = (String)localObject;
      if (localPerUserClipboard.primaryClip != null) {
        paramString = localPerUserClipboard.primaryClip.getDescription();
      }
      return paramString;
    }
    finally {}
  }
  
  List<UserInfo> getRelatedProfiles(int paramInt)
  {
    long l = Binder.clearCallingIdentity();
    try
    {
      List localList = this.mUm.getProfiles(paramInt, true);
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.e("ClipboardService", "Remote Exception calling UserManager: " + localRemoteException);
      return null;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public boolean hasClipboardText(String paramString)
  {
    boolean bool2 = false;
    try
    {
      int i = this.mAppOps.checkOp(29, Binder.getCallingUid(), paramString);
      if (i != 0) {
        return false;
      }
      paramString = getClipboard();
      if (paramString.primaryClip != null)
      {
        paramString = paramString.primaryClip.getItemAt(0).getText();
        boolean bool1 = bool2;
        if (paramString != null)
        {
          i = paramString.length();
          bool1 = bool2;
          if (i > 0) {
            bool1 = true;
          }
        }
        return bool1;
      }
      return false;
    }
    finally {}
  }
  
  public boolean hasPrimaryClip(String paramString)
  {
    boolean bool = false;
    try
    {
      int i = this.mAppOps.checkOp(29, Binder.getCallingUid(), paramString);
      if (i != 0) {
        return false;
      }
      paramString = getClipboard().primaryClip;
      if (paramString != null) {
        bool = true;
      }
      return bool;
    }
    finally {}
  }
  
  public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
    throws RemoteException
  {
    try
    {
      boolean bool = super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      return bool;
    }
    catch (RuntimeException paramParcel1)
    {
      if (!(paramParcel1 instanceof SecurityException)) {
        Slog.wtf("clipboard", "Exception: ", paramParcel1);
      }
      throw paramParcel1;
    }
  }
  
  public void removePrimaryClipChangedListener(IOnPrimaryClipChangedListener paramIOnPrimaryClipChangedListener)
  {
    try
    {
      getClipboard().primaryClipListeners.unregister(paramIOnPrimaryClipChangedListener);
      return;
    }
    finally
    {
      paramIOnPrimaryClipChangedListener = finally;
      throw paramIOnPrimaryClipChangedListener;
    }
  }
  
  public void setPrimaryClip(ClipData paramClipData, String paramString)
  {
    if (paramClipData != null)
    {
      try
      {
        if (paramClipData.getItemCount() > 0) {
          break label29;
        }
        throw new IllegalArgumentException("No items");
      }
      finally {}
      throw paramClipData;
    }
    label29:
    int i = Binder.getCallingUid();
    int j = this.mAppOps.noteOp(30, i, paramString);
    if (j != 0) {
      return;
    }
    checkDataOwnerLocked(paramClipData, i);
    j = UserHandle.getUserId(i);
    paramString = getClipboard(j);
    revokeUris(paramString);
    setPrimaryClipInternal(paramString, paramClipData);
    paramString = getRelatedProfiles(j);
    int k;
    if (paramString != null)
    {
      k = paramString.size();
      if (k <= 1) {}
    }
    for (i = 0;; i = 1)
    {
      try
      {
        boolean bool = this.mUm.getUserRestrictions(j).getBoolean("no_cross_profile_copy_paste");
        if (!bool) {
          continue;
        }
        i = 0;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          label143:
          Slog.e("ClipboardService", "Remote Exception calling UserManager: " + localRemoteException);
        }
        paramClipData = new ClipData(paramClipData);
      }
      if (i == 0)
      {
        paramClipData = null;
        i = 0;
      }
      for (;;)
      {
        if (i < k)
        {
          int m = ((UserInfo)paramString.get(i)).id;
          if (m != j) {
            setPrimaryClipInternal(getClipboard(m), paramClipData);
          }
        }
        else
        {
          try
          {
            i = paramClipData.getItemCount() - 1;
            while (i >= 0)
            {
              paramClipData.setItemAt(i, new ClipData.Item(paramClipData.getItemAt(i)));
              i -= 1;
            }
            paramClipData.fixUrisLight(j);
            break label143;
          }
          finally {}
          return;
          break;
        }
        i += 1;
      }
    }
  }
  
  /* Error */
  void setPrimaryClipInternal(PerUserClipboard paramPerUserClipboard, ClipData paramClipData)
  {
    // Byte code:
    //   0: aload_1
    //   1: getfield 181	com/android/server/clipboard/ClipboardService$PerUserClipboard:activePermissionOwners	Ljava/util/HashSet;
    //   4: invokevirtual 460	java/util/HashSet:clear	()V
    //   7: aload_2
    //   8: ifnonnull +11 -> 19
    //   11: aload_1
    //   12: getfield 177	com/android/server/clipboard/ClipboardService$PerUserClipboard:primaryClip	Landroid/content/ClipData;
    //   15: ifnonnull +4 -> 19
    //   18: return
    //   19: aload_1
    //   20: aload_2
    //   21: putfield 177	com/android/server/clipboard/ClipboardService$PerUserClipboard:primaryClip	Landroid/content/ClipData;
    //   24: invokestatic 142	android/os/Binder:clearCallingIdentity	()J
    //   27: lstore 5
    //   29: aload_1
    //   30: getfield 336	com/android/server/clipboard/ClipboardService$PerUserClipboard:primaryClipListeners	Landroid/os/RemoteCallbackList;
    //   33: invokevirtual 463	android/os/RemoteCallbackList:beginBroadcast	()I
    //   36: istore 4
    //   38: iconst_0
    //   39: istore_3
    //   40: iload_3
    //   41: iload 4
    //   43: if_icmpge +58 -> 101
    //   46: aload_1
    //   47: getfield 336	com/android/server/clipboard/ClipboardService$PerUserClipboard:primaryClipListeners	Landroid/os/RemoteCallbackList;
    //   50: iload_3
    //   51: invokevirtual 466	android/os/RemoteCallbackList:getBroadcastCookie	(I)Ljava/lang/Object;
    //   54: checkcast 8	com/android/server/clipboard/ClipboardService$ListenerInfo
    //   57: astore_2
    //   58: aload_0
    //   59: getfield 90	com/android/server/clipboard/ClipboardService:mAppOps	Landroid/app/AppOpsManager;
    //   62: bipush 29
    //   64: aload_2
    //   65: getfield 469	com/android/server/clipboard/ClipboardService$ListenerInfo:mUid	I
    //   68: aload_2
    //   69: getfield 472	com/android/server/clipboard/ClipboardService$ListenerInfo:mPackageName	Ljava/lang/String;
    //   72: invokevirtual 475	android/app/AppOpsManager:checkOpNoThrow	(IILjava/lang/String;)I
    //   75: ifne +19 -> 94
    //   78: aload_1
    //   79: getfield 336	com/android/server/clipboard/ClipboardService$PerUserClipboard:primaryClipListeners	Landroid/os/RemoteCallbackList;
    //   82: iload_3
    //   83: invokevirtual 479	android/os/RemoteCallbackList:getBroadcastItem	(I)Landroid/os/IInterface;
    //   86: checkcast 481	android/content/IOnPrimaryClipChangedListener
    //   89: invokeinterface 484 1 0
    //   94: iload_3
    //   95: iconst_1
    //   96: iadd
    //   97: istore_3
    //   98: goto -58 -> 40
    //   101: aload_1
    //   102: getfield 336	com/android/server/clipboard/ClipboardService$PerUserClipboard:primaryClipListeners	Landroid/os/RemoteCallbackList;
    //   105: invokevirtual 487	android/os/RemoteCallbackList:finishBroadcast	()V
    //   108: lload 5
    //   110: invokestatic 169	android/os/Binder:restoreCallingIdentity	(J)V
    //   113: return
    //   114: astore_2
    //   115: aload_1
    //   116: getfield 336	com/android/server/clipboard/ClipboardService$PerUserClipboard:primaryClipListeners	Landroid/os/RemoteCallbackList;
    //   119: invokevirtual 487	android/os/RemoteCallbackList:finishBroadcast	()V
    //   122: lload 5
    //   124: invokestatic 169	android/os/Binder:restoreCallingIdentity	(J)V
    //   127: aload_2
    //   128: athrow
    //   129: astore_2
    //   130: goto -36 -> 94
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	133	0	this	ClipboardService
    //   0	133	1	paramPerUserClipboard	PerUserClipboard
    //   0	133	2	paramClipData	ClipData
    //   39	59	3	i	int
    //   36	8	4	j	int
    //   27	96	5	l	long
    // Exception table:
    //   from	to	target	type
    //   46	94	114	finally
    //   46	94	129	android/os/RemoteException
  }
  
  private class ListenerInfo
  {
    final String mPackageName;
    final int mUid;
    
    ListenerInfo(int paramInt, String paramString)
    {
      this.mUid = paramInt;
      this.mPackageName = paramString;
    }
  }
  
  private class PerUserClipboard
  {
    final HashSet<String> activePermissionOwners = new HashSet();
    ClipData primaryClip;
    final RemoteCallbackList<IOnPrimaryClipChangedListener> primaryClipListeners = new RemoteCallbackList();
    final int userId;
    
    PerUserClipboard(int paramInt)
    {
      this.userId = paramInt;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/clipboard/ClipboardService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */