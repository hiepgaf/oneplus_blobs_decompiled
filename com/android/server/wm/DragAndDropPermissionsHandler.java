package com.android.server.wm;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.ClipData;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import com.android.internal.view.IDragAndDropPermissions.Stub;
import java.util.ArrayList;

class DragAndDropPermissionsHandler
  extends IDragAndDropPermissions.Stub
  implements IBinder.DeathRecipient
{
  private IBinder mActivityToken = null;
  private final int mMode;
  private IBinder mPermissionOwnerToken = null;
  private final int mSourceUid;
  private final int mSourceUserId;
  private final String mTargetPackage;
  private final int mTargetUserId;
  private final ArrayList<Uri> mUris = new ArrayList();
  
  DragAndDropPermissionsHandler(ClipData paramClipData, int paramInt1, String paramString, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mSourceUid = paramInt1;
    this.mTargetPackage = paramString;
    this.mMode = paramInt2;
    this.mSourceUserId = paramInt3;
    this.mTargetUserId = paramInt4;
    paramClipData.collectUris(this.mUris);
  }
  
  private void doTake(IBinder paramIBinder)
    throws RemoteException
  {
    long l = Binder.clearCallingIdentity();
    int i = 0;
    try
    {
      while (i < this.mUris.size())
      {
        ActivityManagerNative.getDefault().grantUriPermissionFromOwner(paramIBinder, this.mSourceUid, this.mTargetPackage, (Uri)this.mUris.get(i), this.mMode, this.mSourceUserId, this.mTargetUserId);
        i += 1;
      }
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public void binderDied()
  {
    try
    {
      release();
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  /* Error */
  public void release()
    throws RemoteException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 31	com/android/server/wm/DragAndDropPermissionsHandler:mActivityToken	Landroid/os/IBinder;
    //   4: ifnonnull +11 -> 15
    //   7: aload_0
    //   8: getfield 33	com/android/server/wm/DragAndDropPermissionsHandler:mPermissionOwnerToken	Landroid/os/IBinder;
    //   11: ifnonnull +4 -> 15
    //   14: return
    //   15: aload_0
    //   16: getfield 31	com/android/server/wm/DragAndDropPermissionsHandler:mActivityToken	Landroid/os/IBinder;
    //   19: ifnull +84 -> 103
    //   22: invokestatic 70	android/app/ActivityManagerNative:getDefault	()Landroid/app/IActivityManager;
    //   25: aload_0
    //   26: getfield 31	com/android/server/wm/DragAndDropPermissionsHandler:mActivityToken	Landroid/os/IBinder;
    //   29: invokeinterface 97 2 0
    //   34: astore_2
    //   35: aload_0
    //   36: aconst_null
    //   37: putfield 31	com/android/server/wm/DragAndDropPermissionsHandler:mActivityToken	Landroid/os/IBinder;
    //   40: iconst_0
    //   41: istore_1
    //   42: iload_1
    //   43: aload_0
    //   44: getfield 29	com/android/server/wm/DragAndDropPermissionsHandler:mUris	Ljava/util/ArrayList;
    //   47: invokevirtual 64	java/util/ArrayList:size	()I
    //   50: if_icmpge +78 -> 128
    //   53: invokestatic 70	android/app/ActivityManagerNative:getDefault	()Landroid/app/IActivityManager;
    //   56: aload_2
    //   57: aload_0
    //   58: getfield 29	com/android/server/wm/DragAndDropPermissionsHandler:mUris	Ljava/util/ArrayList;
    //   61: iload_1
    //   62: invokevirtual 74	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   65: checkcast 76	android/net/Uri
    //   68: aload_0
    //   69: getfield 39	com/android/server/wm/DragAndDropPermissionsHandler:mMode	I
    //   72: aload_0
    //   73: getfield 41	com/android/server/wm/DragAndDropPermissionsHandler:mSourceUserId	I
    //   76: invokeinterface 101 5 0
    //   81: iload_1
    //   82: iconst_1
    //   83: iadd
    //   84: istore_1
    //   85: goto -43 -> 42
    //   88: astore_2
    //   89: aload_0
    //   90: aconst_null
    //   91: putfield 31	com/android/server/wm/DragAndDropPermissionsHandler:mActivityToken	Landroid/os/IBinder;
    //   94: return
    //   95: astore_2
    //   96: aload_0
    //   97: aconst_null
    //   98: putfield 31	com/android/server/wm/DragAndDropPermissionsHandler:mActivityToken	Landroid/os/IBinder;
    //   101: aload_2
    //   102: athrow
    //   103: aload_0
    //   104: getfield 33	com/android/server/wm/DragAndDropPermissionsHandler:mPermissionOwnerToken	Landroid/os/IBinder;
    //   107: astore_2
    //   108: aload_0
    //   109: getfield 33	com/android/server/wm/DragAndDropPermissionsHandler:mPermissionOwnerToken	Landroid/os/IBinder;
    //   112: aload_0
    //   113: iconst_0
    //   114: invokeinterface 107 3 0
    //   119: pop
    //   120: aload_0
    //   121: aconst_null
    //   122: putfield 33	com/android/server/wm/DragAndDropPermissionsHandler:mPermissionOwnerToken	Landroid/os/IBinder;
    //   125: goto -85 -> 40
    //   128: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	129	0	this	DragAndDropPermissionsHandler
    //   41	44	1	i	int
    //   34	23	2	localIBinder1	IBinder
    //   88	1	2	localException	Exception
    //   95	7	2	localObject	Object
    //   107	1	2	localIBinder2	IBinder
    // Exception table:
    //   from	to	target	type
    //   22	35	88	java/lang/Exception
    //   22	35	95	finally
  }
  
  public void take(IBinder paramIBinder)
    throws RemoteException
  {
    if ((this.mActivityToken != null) || (this.mPermissionOwnerToken != null)) {
      return;
    }
    this.mActivityToken = paramIBinder;
    doTake(ActivityManagerNative.getDefault().getUriPermissionOwnerForActivity(this.mActivityToken));
  }
  
  public void takeTransient(IBinder paramIBinder)
    throws RemoteException
  {
    if ((this.mActivityToken != null) || (this.mPermissionOwnerToken != null)) {
      return;
    }
    this.mPermissionOwnerToken = paramIBinder;
    this.mPermissionOwnerToken.linkToDeath(this, 0);
    doTake(this.mPermissionOwnerToken);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/DragAndDropPermissionsHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */