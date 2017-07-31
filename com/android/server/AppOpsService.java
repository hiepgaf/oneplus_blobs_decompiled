package com.android.server;

import android.app.ActivityManager;
import android.app.ActivityThread;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.AppOpsManager.OpEntry;
import android.app.AppOpsManager.PackageOps;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Looper;
import android.os.Process;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.ShellCommand;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.storage.MountServiceInternal;
import android.os.storage.MountServiceInternal.ExternalStorageMountPolicy;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TimeUtils;
import com.android.internal.app.IAppOpsCallback;
import com.android.internal.app.IAppOpsService;
import com.android.internal.app.IAppOpsService.Stub;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.Preconditions;
import com.android.internal.util.XmlUtils;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import libcore.util.EmptyArray;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class AppOpsService
  extends IAppOpsService.Stub
{
  static final boolean DEBUG = false;
  static final String TAG = "AppOps";
  static final long WRITE_DELAY = 1800000L;
  final SparseArray<SparseArray<Restriction>> mAudioRestrictions = new SparseArray();
  final ArrayMap<IBinder, ClientState> mClients = new ArrayMap();
  Context mContext;
  boolean mFastWriteScheduled;
  final AtomicFile mFile;
  final Handler mHandler;
  final Looper mLooper;
  final ArrayMap<IBinder, Callback> mModeWatchers = new ArrayMap();
  final SparseArray<ArrayList<Callback>> mOpModeWatchers = new SparseArray();
  private final ArrayMap<IBinder, ClientRestrictionState> mOpUserRestrictions = new ArrayMap();
  final ArrayMap<String, ArrayList<Callback>> mPackageModeWatchers = new ArrayMap();
  private final SparseArray<UidState> mUidStates = new SparseArray();
  final Runnable mWriteRunner = new Runnable()
  {
    public void run()
    {
      synchronized (AppOpsService.this)
      {
        AppOpsService.this.mWriteScheduled = false;
        AppOpsService.this.mFastWriteScheduled = false;
        new AsyncTask()
        {
          protected Void doInBackground(Void... paramAnonymous2VarArgs)
          {
            AppOpsService.this.writeState();
            return null;
          }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
        return;
      }
    }
  };
  boolean mWriteScheduled;
  
  public AppOpsService(File paramFile, Handler paramHandler)
  {
    this.mFile = new AtomicFile(paramFile);
    this.mHandler = paramHandler;
    this.mLooper = Looper.myLooper();
    readState();
  }
  
  private static HashMap<Callback, ArrayList<ChangeRec>> addCallbacks(HashMap<Callback, ArrayList<ChangeRec>> paramHashMap, int paramInt1, int paramInt2, String paramString, ArrayList<Callback> paramArrayList)
  {
    if (paramArrayList == null) {
      return paramHashMap;
    }
    Object localObject = paramHashMap;
    if (paramHashMap == null) {
      localObject = new HashMap();
    }
    int j = 0;
    int i = 0;
    while (i < paramArrayList.size())
    {
      Callback localCallback = (Callback)paramArrayList.get(i);
      ArrayList localArrayList = (ArrayList)((HashMap)localObject).get(localCallback);
      int k;
      if (localArrayList == null)
      {
        paramHashMap = new ArrayList();
        ((HashMap)localObject).put(localCallback, paramHashMap);
        k = j;
        if (k == 0) {
          paramHashMap.add(new ChangeRec(paramInt1, paramInt2, paramString));
        }
        i += 1;
        j = k;
      }
      else
      {
        int n = localArrayList.size();
        int m = 0;
        for (;;)
        {
          k = j;
          paramHashMap = localArrayList;
          if (m >= n) {
            break;
          }
          paramHashMap = (ChangeRec)localArrayList.get(m);
          if ((paramHashMap.op == paramInt1) && (paramHashMap.pkg.equals(paramString)))
          {
            k = 1;
            paramHashMap = localArrayList;
            break;
          }
          m += 1;
        }
      }
    }
    return (HashMap<Callback, ArrayList<ChangeRec>>)localObject;
  }
  
  private PermissionDialogReqQueue.PermissionDialogReq askOperationLocked(int paramInt1, int paramInt2, String paramString, Op paramOp)
  {
    PermissionDialogReqQueue.PermissionDialogReq localPermissionDialogReq = new PermissionDialogReqQueue.PermissionDialogReq();
    this.mHandler.post(new AskRunnable(paramInt1, paramInt2, paramString, paramOp, localPermissionDialogReq));
    return localPermissionDialogReq;
  }
  
  private int checkRestrictionLocked(int paramInt1, int paramInt2, int paramInt3, String paramString)
  {
    Object localObject = (SparseArray)this.mAudioRestrictions.get(paramInt1);
    if (localObject != null)
    {
      localObject = (Restriction)((SparseArray)localObject).get(paramInt2);
      if ((localObject != null) && (!((Restriction)localObject).exceptionPackages.contains(paramString))) {}
    }
    else
    {
      return 0;
    }
    return ((Restriction)localObject).mode;
  }
  
  private void checkSystemUid(String paramString)
  {
    if (Binder.getCallingUid() != 1000) {
      throw new SecurityException(paramString + " must by called by the system");
    }
  }
  
  private ArrayList<AppOpsManager.OpEntry> collectOps(Ops paramOps, int[] paramArrayOfInt)
  {
    Object localObject1 = null;
    Object localObject2;
    if (paramArrayOfInt == null)
    {
      paramArrayOfInt = new ArrayList();
      i = 0;
      for (;;)
      {
        localObject2 = paramArrayOfInt;
        if (i >= paramOps.size()) {
          break;
        }
        localObject1 = (Op)paramOps.valueAt(i);
        paramArrayOfInt.add(new AppOpsManager.OpEntry(((Op)localObject1).op, ((Op)localObject1).mode, ((Op)localObject1).time, ((Op)localObject1).rejectTime, ((Op)localObject1).duration, ((Op)localObject1).proxyUid, ((Op)localObject1).proxyPackageName));
        i += 1;
      }
    }
    int i = 0;
    for (;;)
    {
      localObject2 = localObject1;
      if (i >= paramArrayOfInt.length) {
        break;
      }
      Op localOp = (Op)paramOps.get(paramArrayOfInt[i]);
      localObject2 = localObject1;
      if (localOp != null)
      {
        localObject2 = localObject1;
        if (localObject1 == null) {
          localObject2 = new ArrayList();
        }
        ((ArrayList)localObject2).add(new AppOpsManager.OpEntry(localOp.op, localOp.mode, localOp.time, localOp.rejectTime, localOp.duration, localOp.proxyUid, localOp.proxyPackageName));
      }
      i += 1;
      localObject1 = localObject2;
    }
    return (ArrayList<AppOpsManager.OpEntry>)localObject2;
  }
  
  static void dumpCommandHelp(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("AppOps service (appops) commands:");
    paramPrintWriter.println("  help");
    paramPrintWriter.println("    Print this help text.");
    paramPrintWriter.println("  set [--user <USER_ID>] <PACKAGE> <OP> <MODE>");
    paramPrintWriter.println("    Set the mode for a particular application and operation.");
    paramPrintWriter.println("  get [--user <USER_ID>] <PACKAGE> [<OP>]");
    paramPrintWriter.println("    Return the mode for a particular application and optional operation.");
    paramPrintWriter.println("  query-op [--user <USER_ID>] <OP> [<MODE>]");
    paramPrintWriter.println("    Print all packages that currently have the given op in the given mode.");
    paramPrintWriter.println("  reset [--user <USER_ID>] [<PACKAGE>]");
    paramPrintWriter.println("    Reset the given application or all applications to default modes.");
    paramPrintWriter.println("  write-settings");
    paramPrintWriter.println("    Immediately write pending changes to storage.");
    paramPrintWriter.println("  read-settings");
    paramPrintWriter.println("    Read the last written settings, replacing current state in RAM.");
    paramPrintWriter.println("  options:");
    paramPrintWriter.println("    <PACKAGE> an Android package name.");
    paramPrintWriter.println("    <OP>      an AppOps operation.");
    paramPrintWriter.println("    <MODE>    one of allow, ignore, deny, or default");
    paramPrintWriter.println("    <USER_ID> the user id under which the package is installed. If --user is not");
    paramPrintWriter.println("              specified, the current user is assumed.");
  }
  
  private void dumpHelp(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("AppOps service (appops) dump options:");
    paramPrintWriter.println("  none");
  }
  
  private Op getOpLocked(int paramInt1, int paramInt2, String paramString, boolean paramBoolean)
  {
    paramString = getOpsRawLocked(paramInt2, paramString, paramBoolean);
    if (paramString == null) {
      return null;
    }
    return getOpLocked(paramString, paramInt1, paramBoolean);
  }
  
  private Op getOpLocked(Ops paramOps, int paramInt, boolean paramBoolean)
  {
    Op localOp2 = (Op)paramOps.get(paramInt);
    Op localOp1 = localOp2;
    if (localOp2 == null)
    {
      if (!paramBoolean) {
        return null;
      }
      localOp1 = new Op(paramOps.uidState.uid, paramOps.packageName, paramInt);
      paramOps.put(paramInt, localOp1);
    }
    if (paramBoolean) {
      scheduleWriteLocked();
    }
    return localOp1;
  }
  
  private Ops getOpsRawLocked(int paramInt, String paramString, boolean paramBoolean)
  {
    UidState localUidState = getUidStateLocked(paramInt, paramBoolean);
    if (localUidState == null) {
      return null;
    }
    if (localUidState.pkgOps == null)
    {
      if (!paramBoolean) {
        return null;
      }
      localUidState.pkgOps = new ArrayMap();
    }
    Ops localOps2 = (Ops)localUidState.pkgOps.get(paramString);
    Object localObject = localOps2;
    Ops localOps1;
    if (localOps2 == null)
    {
      if (!paramBoolean) {
        return null;
      }
      paramBoolean = false;
      boolean bool1 = false;
      if (paramInt != 0)
      {
        long l = Binder.clearCallingIdentity();
        int j = -1;
        int i = j;
        for (;;)
        {
          try
          {
            localObject = ActivityThread.getPackageManager().getApplicationInfo(paramString, 268435456, UserHandle.getUserId(paramInt));
            if (localObject == null) {
              continue;
            }
            i = j;
            j = ((ApplicationInfo)localObject).uid;
            i = j;
            int k = ((ApplicationInfo)localObject).privateFlags;
            if ((k & 0x8) == 0) {
              continue;
            }
            paramBoolean = true;
            i = j;
          }
          catch (RemoteException localRemoteException)
          {
            boolean bool2;
            Slog.w("AppOps", "Could not contact PackageManager", localRemoteException);
            paramBoolean = bool1;
            continue;
          }
          finally
          {
            Binder.restoreCallingIdentity(l);
          }
          if (i == paramInt) {
            break;
          }
          localObject = new RuntimeException("here");
          ((RuntimeException)localObject).fillInStackTrace();
          Slog.w("AppOps", "Bad call: specified package " + paramString + " under uid " + paramInt + " but it is really " + i, (Throwable)localObject);
          Binder.restoreCallingIdentity(l);
          return null;
          paramBoolean = false;
          i = j;
          continue;
          i = j;
          if ("media".equals(paramString))
          {
            i = 1013;
            paramBoolean = false;
          }
          else
          {
            i = j;
            if ("audioserver".equals(paramString))
            {
              i = 1041;
              paramBoolean = false;
            }
            else
            {
              i = j;
              bool2 = "cameraserver".equals(paramString);
              paramBoolean = bool1;
              i = j;
              if (bool2)
              {
                i = 1047;
                paramBoolean = false;
              }
            }
          }
        }
        Binder.restoreCallingIdentity(l);
      }
      localOps1 = new Ops(paramString, localUidState, paramBoolean);
      localUidState.pkgOps.put(paramString, localOps1);
    }
    return localOps1;
  }
  
  private static String[] getPackagesForUid(int paramInt)
  {
    Object localObject = null;
    try
    {
      String[] arrayOfString = AppGlobals.getPackageManager().getPackagesForUid(paramInt);
      localObject = arrayOfString;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;) {}
    }
    if (localObject == null) {
      return EmptyArray.STRING;
    }
    return (String[])localObject;
  }
  
  private UidState getUidStateLocked(int paramInt, boolean paramBoolean)
  {
    UidState localUidState2 = (UidState)this.mUidStates.get(paramInt);
    UidState localUidState1 = localUidState2;
    if (localUidState2 == null)
    {
      if (!paramBoolean) {
        return null;
      }
      localUidState1 = new UidState(paramInt);
      this.mUidStates.put(paramInt, localUidState1);
    }
    return localUidState1;
  }
  
  private boolean isOpRestrictedLocked(int paramInt1, int paramInt2, String paramString)
  {
    int j = UserHandle.getUserId(paramInt1);
    int k = this.mOpUserRestrictions.size();
    int i = 0;
    while (i < k)
    {
      if (((ClientRestrictionState)this.mOpUserRestrictions.valueAt(i)).hasRestriction(paramInt2, paramString, j))
      {
        if (AppOpsManager.opAllowSystemBypassRestriction(paramInt2)) {}
        try
        {
          paramString = getOpsRawLocked(paramInt1, paramString, true);
          if (paramString != null)
          {
            boolean bool = paramString.isPrivileged;
            if (bool) {
              return false;
            }
          }
          return true;
        }
        finally {}
      }
      i += 1;
    }
    return false;
  }
  
  private boolean isPackageSuspendedForUser(String paramString, int paramInt)
  {
    try
    {
      boolean bool = AppGlobals.getPackageManager().isPackageSuspendedForUser(paramString, UserHandle.getUserId(paramInt));
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw new SecurityException("Could not talk to package manager service");
    }
  }
  
  private boolean isStrictOpEnable()
  {
    return SystemProperties.getBoolean("persist.sys.strict_op_enable", false);
  }
  
  private int noteOperationUnchecked(int paramInt1, int paramInt2, String paramString1, int paramInt3, String paramString2)
  {
    new PermissionDialogReqQueue.PermissionDialogReq();
    try
    {
      Ops localOps = getOpsRawLocked(paramInt2, paramString1, true);
      int i = AppOpsManager.opToDefaultMode(paramInt1);
      if (localOps == null) {
        return 2;
      }
      Object localObject = getOpLocked(localOps, paramInt1, true);
      boolean bool = isOpRestrictedLocked(paramInt2, paramInt1, paramString1);
      if (bool) {
        return 1;
      }
      if (((Op)localObject).duration == -1) {
        Slog.w("AppOps", "Noting op not finished: uid " + paramInt2 + " pkg " + paramString1 + " code " + paramInt1 + " time=" + ((Op)localObject).time + " duration=" + ((Op)localObject).duration);
      }
      ((Op)localObject).duration = 0;
      int j = AppOpsManager.opToSwitch(paramInt1);
      UidState localUidState = localOps.uidState;
      if ((isStrictOpEnable()) && ((paramInt1 == 65) || (paramInt1 == 66)))
      {
        if (j != paramInt1) {}
        for (paramString2 = getOpLocked(localOps, j, true); Looper.myLooper() == this.mLooper; paramString2 = (String)localObject)
        {
          paramInt1 = paramString2.mode;
          return paramInt1;
        }
        if (Op.-get0((Op)localObject))
        {
          localObject = localUidState.opModes;
          if (localObject == null) {
            return i;
          }
          if (localUidState.opModes.get(paramInt1) == 0)
          {
            paramInt1 = localUidState.opModes.get(paramInt1);
            return paramInt1;
          }
        }
        for (paramString1 = askOperationLocked(paramInt1, paramInt2, paramString1, paramString2);; paramString1 = askOperationLocked(paramInt1, paramInt2, paramString1, paramString2)) {
          return paramString1.get();
        }
      }
      if ((localUidState.opModes != null) && (localUidState.opModes.indexOfKey(j) >= 0))
      {
        paramInt1 = localUidState.opModes.get(j);
        if (paramInt1 != 0)
        {
          ((Op)localObject).rejectTime = System.currentTimeMillis();
          return paramInt1;
        }
      }
      else
      {
        if (j != paramInt1) {}
        for (paramString1 = getOpLocked(localOps, j, true); paramString1.mode != 0; paramString1 = (String)localObject)
        {
          ((Op)localObject).rejectTime = System.currentTimeMillis();
          paramInt1 = paramString1.mode;
          return paramInt1;
        }
      }
      ((Op)localObject).time = System.currentTimeMillis();
      ((Op)localObject).rejectTime = 0L;
      ((Op)localObject).proxyUid = paramInt3;
      ((Op)localObject).proxyPackageName = paramString2;
      return 0;
    }
    finally {}
  }
  
  /* Error */
  private void notifyWatchersOfChange(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 113	com/android/server/AppOpsService:mOpModeWatchers	Landroid/util/SparseArray;
    //   6: iload_1
    //   7: invokevirtual 202	android/util/SparseArray:get	(I)Ljava/lang/Object;
    //   10: checkcast 148	java/util/ArrayList
    //   13: astore 6
    //   15: aload 6
    //   17: ifnonnull +6 -> 23
    //   20: aload_0
    //   21: monitorexit
    //   22: return
    //   23: new 148	java/util/ArrayList
    //   26: dup
    //   27: aload 6
    //   29: invokespecial 532	java/util/ArrayList:<init>	(Ljava/util/Collection;)V
    //   32: astore 6
    //   34: aload_0
    //   35: monitorexit
    //   36: invokestatic 364	android/os/Binder:clearCallingIdentity	()J
    //   39: lstore 4
    //   41: aload 6
    //   43: invokevirtual 152	java/util/ArrayList:size	()I
    //   46: istore_3
    //   47: iconst_0
    //   48: istore_2
    //   49: iload_2
    //   50: iload_3
    //   51: if_icmpge +67 -> 118
    //   54: aload 6
    //   56: iload_2
    //   57: invokevirtual 156	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   60: checkcast 15	com/android/server/AppOpsService$Callback
    //   63: astore 7
    //   65: aload 7
    //   67: getfield 536	com/android/server/AppOpsService$Callback:mCallback	Lcom/android/internal/app/IAppOpsCallback;
    //   70: iload_1
    //   71: iconst_m1
    //   72: aconst_null
    //   73: invokeinterface 541 4 0
    //   78: iload_2
    //   79: iconst_1
    //   80: iadd
    //   81: istore_2
    //   82: goto -33 -> 49
    //   85: astore 6
    //   87: aload_0
    //   88: monitorexit
    //   89: aload 6
    //   91: athrow
    //   92: astore 7
    //   94: ldc 50
    //   96: ldc_w 543
    //   99: aload 7
    //   101: invokestatic 546	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   104: pop
    //   105: goto -27 -> 78
    //   108: astore 6
    //   110: lload 4
    //   112: invokestatic 417	android/os/Binder:restoreCallingIdentity	(J)V
    //   115: aload 6
    //   117: athrow
    //   118: lload 4
    //   120: invokestatic 417	android/os/Binder:restoreCallingIdentity	(J)V
    //   123: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	124	0	this	AppOpsService
    //   0	124	1	paramInt	int
    //   48	34	2	i	int
    //   46	6	3	j	int
    //   39	80	4	l	long
    //   13	42	6	localArrayList	ArrayList
    //   85	5	6	localObject1	Object
    //   108	8	6	localObject2	Object
    //   63	3	7	localCallback	Callback
    //   92	8	7	localRemoteException	RemoteException
    // Exception table:
    //   from	to	target	type
    //   2	15	85	finally
    //   23	34	85	finally
    //   65	78	92	android/os/RemoteException
    //   41	47	108	finally
    //   54	65	108	finally
    //   65	78	108	finally
    //   94	105	108	finally
  }
  
  /* Error */
  static int onShellCommand(Shell paramShell, String arg1)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnonnull +9 -> 10
    //   4: aload_0
    //   5: aload_1
    //   6: invokevirtual 552	com/android/server/AppOpsService$Shell:handleDefaultCommands	(Ljava/lang/String;)I
    //   9: ireturn
    //   10: aload_0
    //   11: invokevirtual 556	com/android/server/AppOpsService$Shell:getOutPrintWriter	()Ljava/io/PrintWriter;
    //   14: astore 9
    //   16: aload_0
    //   17: invokevirtual 559	com/android/server/AppOpsService$Shell:getErrPrintWriter	()Ljava/io/PrintWriter;
    //   20: astore 10
    //   22: aload_1
    //   23: ldc_w 561
    //   26: invokevirtual 183	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   29: ifeq +17 -> 46
    //   32: aload_0
    //   33: iconst_1
    //   34: aload 10
    //   36: invokevirtual 565	com/android/server/AppOpsService$Shell:parseUserPackageOp	(ZLjava/io/PrintWriter;)I
    //   39: istore_2
    //   40: iload_2
    //   41: ifge +241 -> 282
    //   44: iload_2
    //   45: ireturn
    //   46: aload_1
    //   47: ldc_w 566
    //   50: invokevirtual 183	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   53: ifeq +17 -> 70
    //   56: aload_0
    //   57: iconst_0
    //   58: aload 10
    //   60: invokevirtual 565	com/android/server/AppOpsService$Shell:parseUserPackageOp	(ZLjava/io/PrintWriter;)I
    //   63: istore_2
    //   64: iload_2
    //   65: ifge +274 -> 339
    //   68: iload_2
    //   69: ireturn
    //   70: aload_1
    //   71: ldc_w 568
    //   74: invokevirtual 183	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   77: ifeq +17 -> 94
    //   80: aload_0
    //   81: iconst_1
    //   82: aload 10
    //   84: invokevirtual 572	com/android/server/AppOpsService$Shell:parseUserOpMode	(ILjava/io/PrintWriter;)I
    //   87: istore_2
    //   88: iload_2
    //   89: ifge +662 -> 751
    //   92: iload_2
    //   93: ireturn
    //   94: aload_1
    //   95: ldc_w 574
    //   98: invokevirtual 183	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   101: ifeq +41 -> 142
    //   104: aconst_null
    //   105: astore_1
    //   106: bipush -2
    //   108: istore_2
    //   109: aload_0
    //   110: invokevirtual 577	com/android/server/AppOpsService$Shell:getNextArg	()Ljava/lang/String;
    //   113: astore 8
    //   115: aload 8
    //   117: ifnull +820 -> 937
    //   120: ldc_w 579
    //   123: aload 8
    //   125: invokevirtual 183	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   128: ifeq +977 -> 1105
    //   131: aload_0
    //   132: invokevirtual 582	com/android/server/AppOpsService$Shell:getNextArgRequired	()Ljava/lang/String;
    //   135: invokestatic 585	android/os/UserHandle:parseUserArg	(Ljava/lang/String;)I
    //   138: istore_2
    //   139: goto -30 -> 109
    //   142: aload_1
    //   143: ldc_w 587
    //   146: invokevirtual 183	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   149: ifeq +76 -> 225
    //   152: aload_0
    //   153: getfield 591	com/android/server/AppOpsService$Shell:mInternal	Lcom/android/server/AppOpsService;
    //   156: getfield 593	com/android/server/AppOpsService:mContext	Landroid/content/Context;
    //   159: ldc_w 595
    //   162: invokestatic 598	android/os/Binder:getCallingPid	()I
    //   165: invokestatic 221	android/os/Binder:getCallingUid	()I
    //   168: aconst_null
    //   169: invokevirtual 604	android/content/Context:enforcePermission	(Ljava/lang/String;IILjava/lang/String;)V
    //   172: invokestatic 364	android/os/Binder:clearCallingIdentity	()J
    //   175: lstore 6
    //   177: aload_0
    //   178: getfield 591	com/android/server/AppOpsService$Shell:mInternal	Lcom/android/server/AppOpsService;
    //   181: astore_1
    //   182: aload_1
    //   183: monitorenter
    //   184: aload_0
    //   185: getfield 591	com/android/server/AppOpsService$Shell:mInternal	Lcom/android/server/AppOpsService;
    //   188: getfield 130	com/android/server/AppOpsService:mHandler	Landroid/os/Handler;
    //   191: aload_0
    //   192: getfield 591	com/android/server/AppOpsService$Shell:mInternal	Lcom/android/server/AppOpsService;
    //   195: getfield 103	com/android/server/AppOpsService:mWriteRunner	Ljava/lang/Runnable;
    //   198: invokevirtual 608	android/os/Handler:removeCallbacks	(Ljava/lang/Runnable;)V
    //   201: aload_1
    //   202: monitorexit
    //   203: aload_0
    //   204: getfield 591	com/android/server/AppOpsService$Shell:mInternal	Lcom/android/server/AppOpsService;
    //   207: invokevirtual 611	com/android/server/AppOpsService:writeState	()V
    //   210: aload 9
    //   212: ldc_w 613
    //   215: invokevirtual 277	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   218: lload 6
    //   220: invokestatic 417	android/os/Binder:restoreCallingIdentity	(J)V
    //   223: iconst_0
    //   224: ireturn
    //   225: aload_1
    //   226: ldc_w 615
    //   229: invokevirtual 183	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   232: ifeq +827 -> 1059
    //   235: aload_0
    //   236: getfield 591	com/android/server/AppOpsService$Shell:mInternal	Lcom/android/server/AppOpsService;
    //   239: getfield 593	com/android/server/AppOpsService:mContext	Landroid/content/Context;
    //   242: ldc_w 595
    //   245: invokestatic 598	android/os/Binder:getCallingPid	()I
    //   248: invokestatic 221	android/os/Binder:getCallingUid	()I
    //   251: aconst_null
    //   252: invokevirtual 604	android/content/Context:enforcePermission	(Ljava/lang/String;IILjava/lang/String;)V
    //   255: invokestatic 364	android/os/Binder:clearCallingIdentity	()J
    //   258: lstore 6
    //   260: aload_0
    //   261: getfield 591	com/android/server/AppOpsService$Shell:mInternal	Lcom/android/server/AppOpsService;
    //   264: invokevirtual 141	com/android/server/AppOpsService:readState	()V
    //   267: aload 9
    //   269: ldc_w 617
    //   272: invokevirtual 277	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   275: lload 6
    //   277: invokestatic 417	android/os/Binder:restoreCallingIdentity	(J)V
    //   280: iconst_0
    //   281: ireturn
    //   282: aload_0
    //   283: invokevirtual 577	com/android/server/AppOpsService$Shell:getNextArg	()Ljava/lang/String;
    //   286: astore_1
    //   287: aload_1
    //   288: ifnonnull +13 -> 301
    //   291: aload 10
    //   293: ldc_w 619
    //   296: invokevirtual 277	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   299: iconst_m1
    //   300: ireturn
    //   301: aload_0
    //   302: aload_1
    //   303: aload 10
    //   305: invokevirtual 623	com/android/server/AppOpsService$Shell:strModeToMode	(Ljava/lang/String;Ljava/io/PrintWriter;)I
    //   308: istore_2
    //   309: iload_2
    //   310: ifge +5 -> 315
    //   313: iconst_m1
    //   314: ireturn
    //   315: aload_0
    //   316: getfield 627	com/android/server/AppOpsService$Shell:mInterface	Lcom/android/internal/app/IAppOpsService;
    //   319: aload_0
    //   320: getfield 628	com/android/server/AppOpsService$Shell:op	I
    //   323: aload_0
    //   324: getfield 631	com/android/server/AppOpsService$Shell:packageUid	I
    //   327: aload_0
    //   328: getfield 632	com/android/server/AppOpsService$Shell:packageName	Ljava/lang/String;
    //   331: iload_2
    //   332: invokeinterface 638 5 0
    //   337: iconst_0
    //   338: ireturn
    //   339: aload_0
    //   340: getfield 627	com/android/server/AppOpsService$Shell:mInterface	Lcom/android/internal/app/IAppOpsService;
    //   343: astore 8
    //   345: aload_0
    //   346: getfield 631	com/android/server/AppOpsService$Shell:packageUid	I
    //   349: istore_2
    //   350: aload_0
    //   351: getfield 632	com/android/server/AppOpsService$Shell:packageName	Ljava/lang/String;
    //   354: astore 10
    //   356: aload_0
    //   357: getfield 628	com/android/server/AppOpsService$Shell:op	I
    //   360: iconst_m1
    //   361: if_icmpeq +706 -> 1067
    //   364: iconst_1
    //   365: newarray <illegal type>
    //   367: astore_1
    //   368: aload_1
    //   369: iconst_0
    //   370: aload_0
    //   371: getfield 628	com/android/server/AppOpsService$Shell:op	I
    //   374: iastore
    //   375: aload_1
    //   376: astore_0
    //   377: aload 8
    //   379: iload_2
    //   380: aload 10
    //   382: aload_0
    //   383: invokeinterface 642 4 0
    //   388: astore_0
    //   389: aload_0
    //   390: ifnull +12 -> 402
    //   393: aload_0
    //   394: invokeinterface 645 1 0
    //   399: ifgt +13 -> 412
    //   402: aload 9
    //   404: ldc_w 647
    //   407: invokevirtual 277	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   410: iconst_0
    //   411: ireturn
    //   412: invokestatic 529	java/lang/System:currentTimeMillis	()J
    //   415: lstore 6
    //   417: iconst_0
    //   418: istore_2
    //   419: iload_2
    //   420: aload_0
    //   421: invokeinterface 645 1 0
    //   426: if_icmpge +656 -> 1082
    //   429: aload_0
    //   430: iload_2
    //   431: invokeinterface 648 2 0
    //   436: checkcast 650	android/app/AppOpsManager$PackageOps
    //   439: invokevirtual 654	android/app/AppOpsManager$PackageOps:getOps	()Ljava/util/List;
    //   442: astore_1
    //   443: iconst_0
    //   444: istore_3
    //   445: iload_3
    //   446: aload_1
    //   447: invokeinterface 645 1 0
    //   452: if_icmpge +623 -> 1075
    //   455: aload_1
    //   456: iload_3
    //   457: invokeinterface 648 2 0
    //   462: checkcast 246	android/app/AppOpsManager$OpEntry
    //   465: astore 8
    //   467: aload 9
    //   469: aload 8
    //   471: invokevirtual 657	android/app/AppOpsManager$OpEntry:getOp	()I
    //   474: invokestatic 661	android/app/AppOpsManager:opToName	(I)Ljava/lang/String;
    //   477: invokevirtual 664	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   480: aload 9
    //   482: ldc_w 666
    //   485: invokevirtual 664	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   488: aload 8
    //   490: invokevirtual 669	android/app/AppOpsManager$OpEntry:getMode	()I
    //   493: tableswitch	default:+579->1072, 0:+156->649, 1:+195->688, 2:+206->699, 3:+217->710
    //   524: aload 9
    //   526: ldc_w 671
    //   529: invokevirtual 664	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   532: aload 9
    //   534: aload 8
    //   536: invokevirtual 669	android/app/AppOpsManager$OpEntry:getMode	()I
    //   539: invokevirtual 673	java/io/PrintWriter:print	(I)V
    //   542: aload 8
    //   544: invokevirtual 676	android/app/AppOpsManager$OpEntry:getTime	()J
    //   547: lconst_0
    //   548: lcmp
    //   549: ifeq +32 -> 581
    //   552: aload 9
    //   554: ldc_w 678
    //   557: invokevirtual 664	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   560: lload 6
    //   562: aload 8
    //   564: invokevirtual 676	android/app/AppOpsManager$OpEntry:getTime	()J
    //   567: lsub
    //   568: aload 9
    //   570: invokestatic 684	android/util/TimeUtils:formatDuration	(JLjava/io/PrintWriter;)V
    //   573: aload 9
    //   575: ldc_w 686
    //   578: invokevirtual 664	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   581: aload 8
    //   583: invokevirtual 689	android/app/AppOpsManager$OpEntry:getRejectTime	()J
    //   586: lconst_0
    //   587: lcmp
    //   588: ifeq +32 -> 620
    //   591: aload 9
    //   593: ldc_w 691
    //   596: invokevirtual 664	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   599: lload 6
    //   601: aload 8
    //   603: invokevirtual 689	android/app/AppOpsManager$OpEntry:getRejectTime	()J
    //   606: lsub
    //   607: aload 9
    //   609: invokestatic 684	android/util/TimeUtils:formatDuration	(JLjava/io/PrintWriter;)V
    //   612: aload 9
    //   614: ldc_w 686
    //   617: invokevirtual 664	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   620: aload 8
    //   622: invokevirtual 694	android/app/AppOpsManager$OpEntry:getDuration	()I
    //   625: iconst_m1
    //   626: if_icmpne +95 -> 721
    //   629: aload 9
    //   631: ldc_w 696
    //   634: invokevirtual 664	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   637: aload 9
    //   639: invokevirtual 698	java/io/PrintWriter:println	()V
    //   642: iload_3
    //   643: iconst_1
    //   644: iadd
    //   645: istore_3
    //   646: goto -201 -> 445
    //   649: aload 9
    //   651: ldc_w 700
    //   654: invokevirtual 664	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   657: goto -115 -> 542
    //   660: astore_0
    //   661: aload 9
    //   663: new 225	java/lang/StringBuilder
    //   666: dup
    //   667: invokespecial 226	java/lang/StringBuilder:<init>	()V
    //   670: ldc_w 702
    //   673: invokevirtual 230	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   676: aload_0
    //   677: invokevirtual 705	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   680: invokevirtual 236	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   683: invokevirtual 277	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   686: iconst_m1
    //   687: ireturn
    //   688: aload 9
    //   690: ldc_w 707
    //   693: invokevirtual 664	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   696: goto -154 -> 542
    //   699: aload 9
    //   701: ldc_w 709
    //   704: invokevirtual 664	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   707: goto -165 -> 542
    //   710: aload 9
    //   712: ldc_w 711
    //   715: invokevirtual 664	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   718: goto -176 -> 542
    //   721: aload 8
    //   723: invokevirtual 694	android/app/AppOpsManager$OpEntry:getDuration	()I
    //   726: ifeq -89 -> 637
    //   729: aload 9
    //   731: ldc_w 713
    //   734: invokevirtual 664	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   737: aload 8
    //   739: invokevirtual 694	android/app/AppOpsManager$OpEntry:getDuration	()I
    //   742: i2l
    //   743: aload 9
    //   745: invokestatic 684	android/util/TimeUtils:formatDuration	(JLjava/io/PrintWriter;)V
    //   748: goto -111 -> 637
    //   751: aload_0
    //   752: getfield 627	com/android/server/AppOpsService$Shell:mInterface	Lcom/android/internal/app/IAppOpsService;
    //   755: iconst_1
    //   756: newarray <illegal type>
    //   758: dup
    //   759: iconst_0
    //   760: aload_0
    //   761: getfield 628	com/android/server/AppOpsService$Shell:op	I
    //   764: iastore
    //   765: invokeinterface 717 2 0
    //   770: astore_1
    //   771: aload_1
    //   772: ifnull +12 -> 784
    //   775: aload_1
    //   776: invokeinterface 645 1 0
    //   781: ifgt +303 -> 1084
    //   784: aload 9
    //   786: ldc_w 647
    //   789: invokevirtual 277	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   792: iconst_0
    //   793: ireturn
    //   794: iload_2
    //   795: aload_1
    //   796: invokeinterface 645 1 0
    //   801: if_icmpge +302 -> 1103
    //   804: aload_1
    //   805: iload_2
    //   806: invokeinterface 648 2 0
    //   811: checkcast 650	android/app/AppOpsManager$PackageOps
    //   814: astore 8
    //   816: iconst_0
    //   817: istore 5
    //   819: aload_1
    //   820: iload_2
    //   821: invokeinterface 648 2 0
    //   826: checkcast 650	android/app/AppOpsManager$PackageOps
    //   829: invokevirtual 654	android/app/AppOpsManager$PackageOps:getOps	()Ljava/util/List;
    //   832: astore 10
    //   834: iconst_0
    //   835: istore_3
    //   836: iload 5
    //   838: istore 4
    //   840: iload_3
    //   841: aload 10
    //   843: invokeinterface 645 1 0
    //   848: if_icmpge +43 -> 891
    //   851: aload 10
    //   853: iload_3
    //   854: invokeinterface 648 2 0
    //   859: checkcast 246	android/app/AppOpsManager$OpEntry
    //   862: astore 11
    //   864: aload 11
    //   866: invokevirtual 657	android/app/AppOpsManager$OpEntry:getOp	()I
    //   869: aload_0
    //   870: getfield 628	com/android/server/AppOpsService$Shell:op	I
    //   873: if_icmpne +223 -> 1096
    //   876: aload 11
    //   878: invokevirtual 669	android/app/AppOpsManager$OpEntry:getMode	()I
    //   881: aload_0
    //   882: getfield 718	com/android/server/AppOpsService$Shell:mode	I
    //   885: if_icmpne +211 -> 1096
    //   888: iconst_1
    //   889: istore 4
    //   891: iload 4
    //   893: ifeq +196 -> 1089
    //   896: aload 9
    //   898: aload 8
    //   900: invokevirtual 721	android/app/AppOpsManager$PackageOps:getPackageName	()Ljava/lang/String;
    //   903: invokevirtual 277	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   906: goto +183 -> 1089
    //   909: aload 10
    //   911: new 225	java/lang/StringBuilder
    //   914: dup
    //   915: invokespecial 226	java/lang/StringBuilder:<init>	()V
    //   918: ldc_w 723
    //   921: invokevirtual 230	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   924: aload 8
    //   926: invokevirtual 230	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   929: invokevirtual 236	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   932: invokevirtual 277	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   935: iconst_m1
    //   936: ireturn
    //   937: iload_2
    //   938: istore_3
    //   939: iload_2
    //   940: bipush -2
    //   942: if_icmpne +7 -> 949
    //   945: invokestatic 728	android/app/ActivityManager:getCurrentUser	()I
    //   948: istore_3
    //   949: aload_0
    //   950: getfield 627	com/android/server/AppOpsService$Shell:mInterface	Lcom/android/internal/app/IAppOpsService;
    //   953: iload_3
    //   954: aload_1
    //   955: invokeinterface 732 3 0
    //   960: aload 9
    //   962: ldc_w 734
    //   965: invokevirtual 664	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   968: iload_3
    //   969: iconst_m1
    //   970: if_icmpne +34 -> 1004
    //   973: aload 9
    //   975: ldc_w 736
    //   978: invokevirtual 664	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   981: aload 9
    //   983: ldc_w 738
    //   986: invokevirtual 664	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   989: aload_1
    //   990: ifnonnull +31 -> 1021
    //   993: aload 9
    //   995: ldc_w 740
    //   998: invokevirtual 277	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   1001: goto +114 -> 1115
    //   1004: aload 9
    //   1006: ldc_w 742
    //   1009: invokevirtual 664	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   1012: aload 9
    //   1014: iload_3
    //   1015: invokevirtual 673	java/io/PrintWriter:print	(I)V
    //   1018: goto -37 -> 981
    //   1021: aload 9
    //   1023: ldc_w 744
    //   1026: invokevirtual 664	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   1029: aload 9
    //   1031: aload_1
    //   1032: invokevirtual 277	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   1035: goto +80 -> 1115
    //   1038: astore_0
    //   1039: aload_1
    //   1040: monitorexit
    //   1041: aload_0
    //   1042: athrow
    //   1043: astore_0
    //   1044: lload 6
    //   1046: invokestatic 417	android/os/Binder:restoreCallingIdentity	(J)V
    //   1049: aload_0
    //   1050: athrow
    //   1051: astore_0
    //   1052: lload 6
    //   1054: invokestatic 417	android/os/Binder:restoreCallingIdentity	(J)V
    //   1057: aload_0
    //   1058: athrow
    //   1059: aload_0
    //   1060: aload_1
    //   1061: invokevirtual 552	com/android/server/AppOpsService$Shell:handleDefaultCommands	(Ljava/lang/String;)I
    //   1064: istore_2
    //   1065: iload_2
    //   1066: ireturn
    //   1067: aconst_null
    //   1068: astore_0
    //   1069: goto -692 -> 377
    //   1072: goto -548 -> 524
    //   1075: iload_2
    //   1076: iconst_1
    //   1077: iadd
    //   1078: istore_2
    //   1079: goto -660 -> 419
    //   1082: iconst_0
    //   1083: ireturn
    //   1084: iconst_0
    //   1085: istore_2
    //   1086: goto -292 -> 794
    //   1089: iload_2
    //   1090: iconst_1
    //   1091: iadd
    //   1092: istore_2
    //   1093: goto -299 -> 794
    //   1096: iload_3
    //   1097: iconst_1
    //   1098: iadd
    //   1099: istore_3
    //   1100: goto -264 -> 836
    //   1103: iconst_0
    //   1104: ireturn
    //   1105: aload_1
    //   1106: ifnonnull -197 -> 909
    //   1109: aload 8
    //   1111: astore_1
    //   1112: goto -1003 -> 109
    //   1115: iconst_0
    //   1116: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1117	0	paramShell	Shell
    //   39	1054	2	i	int
    //   444	656	3	j	int
    //   838	54	4	k	int
    //   817	20	5	m	int
    //   175	878	6	l	long
    //   113	997	8	localObject1	Object
    //   14	1016	9	localPrintWriter	PrintWriter
    //   20	890	10	localObject2	Object
    //   862	15	11	localOpEntry	AppOpsManager.OpEntry
    // Exception table:
    //   from	to	target	type
    //   22	40	660	android/os/RemoteException
    //   46	64	660	android/os/RemoteException
    //   70	88	660	android/os/RemoteException
    //   94	104	660	android/os/RemoteException
    //   109	115	660	android/os/RemoteException
    //   120	139	660	android/os/RemoteException
    //   142	177	660	android/os/RemoteException
    //   218	223	660	android/os/RemoteException
    //   225	260	660	android/os/RemoteException
    //   275	280	660	android/os/RemoteException
    //   282	287	660	android/os/RemoteException
    //   291	299	660	android/os/RemoteException
    //   301	309	660	android/os/RemoteException
    //   315	337	660	android/os/RemoteException
    //   339	375	660	android/os/RemoteException
    //   377	389	660	android/os/RemoteException
    //   393	402	660	android/os/RemoteException
    //   402	410	660	android/os/RemoteException
    //   412	417	660	android/os/RemoteException
    //   419	443	660	android/os/RemoteException
    //   445	524	660	android/os/RemoteException
    //   524	542	660	android/os/RemoteException
    //   542	581	660	android/os/RemoteException
    //   581	620	660	android/os/RemoteException
    //   620	637	660	android/os/RemoteException
    //   637	642	660	android/os/RemoteException
    //   649	657	660	android/os/RemoteException
    //   688	696	660	android/os/RemoteException
    //   699	707	660	android/os/RemoteException
    //   710	718	660	android/os/RemoteException
    //   721	748	660	android/os/RemoteException
    //   751	771	660	android/os/RemoteException
    //   775	784	660	android/os/RemoteException
    //   784	792	660	android/os/RemoteException
    //   794	816	660	android/os/RemoteException
    //   819	834	660	android/os/RemoteException
    //   840	888	660	android/os/RemoteException
    //   896	906	660	android/os/RemoteException
    //   909	935	660	android/os/RemoteException
    //   945	949	660	android/os/RemoteException
    //   949	968	660	android/os/RemoteException
    //   973	981	660	android/os/RemoteException
    //   981	989	660	android/os/RemoteException
    //   993	1001	660	android/os/RemoteException
    //   1004	1018	660	android/os/RemoteException
    //   1021	1035	660	android/os/RemoteException
    //   1044	1051	660	android/os/RemoteException
    //   1052	1059	660	android/os/RemoteException
    //   1059	1065	660	android/os/RemoteException
    //   184	201	1038	finally
    //   177	184	1043	finally
    //   201	218	1043	finally
    //   1039	1043	1043	finally
    //   260	275	1051	finally
  }
  
  private void printOperationLocked(Op paramOp, int paramInt, String paramString)
  {
    if (paramOp != null)
    {
      AppOpsManager.opToSwitch(paramOp.op);
      if (paramInt != 1) {
        break label18;
      }
    }
    label18:
    while (paramInt != 0) {
      return;
    }
  }
  
  private void pruneOp(Op paramOp, int paramInt, String paramString)
  {
    if ((paramOp.time == 0L) && (paramOp.rejectTime == 0L))
    {
      paramString = getOpsRawLocked(paramInt, paramString, false);
      if (paramString != null)
      {
        paramString.remove(paramOp.op);
        if (paramString.size() <= 0)
        {
          paramOp = paramString.uidState;
          ArrayMap localArrayMap = paramOp.pkgOps;
          if (localArrayMap != null)
          {
            localArrayMap.remove(paramString.packageName);
            if (localArrayMap.isEmpty()) {
              paramOp.pkgOps = null;
            }
            if (paramOp.isDefault()) {
              this.mUidStates.remove(paramInt);
            }
          }
        }
      }
    }
  }
  
  private void recordOperationLocked(int paramInt1, int paramInt2, String paramString, int paramInt3)
  {
    paramString = getOpLocked(paramInt1, paramInt2, paramString, false);
    if (paramString != null)
    {
      printOperationLocked(paramString, paramInt3, "noteOperartion");
      if (paramInt3 != 1) {
        break label37;
      }
      paramString.rejectTime = System.currentTimeMillis();
    }
    label37:
    while (paramInt3 != 0) {
      return;
    }
    paramString.time = System.currentTimeMillis();
    paramString.rejectTime = 0L;
  }
  
  private static String resolvePackageName(int paramInt, String paramString)
  {
    if (paramInt == 0) {
      return "root";
    }
    if (paramInt == 2000) {
      return "com.android.shell";
    }
    if ((paramInt == 1000) && (paramString == null)) {
      return "android";
    }
    return paramString;
  }
  
  private void scheduleFastWriteLocked()
  {
    if (!this.mFastWriteScheduled)
    {
      this.mWriteScheduled = true;
      this.mFastWriteScheduled = true;
      this.mHandler.removeCallbacks(this.mWriteRunner);
      this.mHandler.postDelayed(this.mWriteRunner, 10000L);
    }
  }
  
  private void scheduleWriteLocked()
  {
    if (!this.mWriteScheduled)
    {
      this.mWriteScheduled = true;
      this.mHandler.postDelayed(this.mWriteRunner, 1800000L);
    }
  }
  
  private void scheduleWriteNowLocked()
  {
    if (!this.mWriteScheduled) {
      this.mWriteScheduled = true;
    }
    this.mHandler.removeCallbacks(this.mWriteRunner);
    this.mHandler.post(this.mWriteRunner);
  }
  
  /* Error */
  private void setUserRestrictionNoCheck(int paramInt1, boolean paramBoolean, IBinder paramIBinder, int paramInt2, String[] paramArrayOfString)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore 6
    //   3: aload_0
    //   4: monitorenter
    //   5: aload_0
    //   6: getfield 86	com/android/server/AppOpsService:mOpUserRestrictions	Landroid/util/ArrayMap;
    //   9: aload_3
    //   10: invokevirtual 360	android/util/ArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   13: checkcast 21	com/android/server/AppOpsService$ClientRestrictionState
    //   16: astore 8
    //   18: aload 8
    //   20: astore 7
    //   22: aload 8
    //   24: ifnonnull +25 -> 49
    //   27: new 21	com/android/server/AppOpsService$ClientRestrictionState
    //   30: dup
    //   31: aload_0
    //   32: aload_3
    //   33: invokespecial 791	com/android/server/AppOpsService$ClientRestrictionState:<init>	(Lcom/android/server/AppOpsService;Landroid/os/IBinder;)V
    //   36: astore 7
    //   38: aload_0
    //   39: getfield 86	com/android/server/AppOpsService:mOpUserRestrictions	Landroid/util/ArrayMap;
    //   42: aload_3
    //   43: aload 7
    //   45: invokevirtual 429	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   48: pop
    //   49: aload 7
    //   51: iload_1
    //   52: iload_2
    //   53: aload 5
    //   55: iload 4
    //   57: invokevirtual 795	com/android/server/AppOpsService$ClientRestrictionState:setRestriction	(IZ[Ljava/lang/String;I)Z
    //   60: ifeq +6 -> 66
    //   63: iconst_1
    //   64: istore 6
    //   66: aload 7
    //   68: invokevirtual 796	com/android/server/AppOpsService$ClientRestrictionState:isDefault	()Z
    //   71: ifeq +17 -> 88
    //   74: aload_0
    //   75: getfield 86	com/android/server/AppOpsService:mOpUserRestrictions	Landroid/util/ArrayMap;
    //   78: aload_3
    //   79: invokevirtual 752	android/util/ArrayMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
    //   82: pop
    //   83: aload 7
    //   85: invokevirtual 799	com/android/server/AppOpsService$ClientRestrictionState:destroy	()V
    //   88: aload_0
    //   89: monitorexit
    //   90: iload 6
    //   92: ifeq +8 -> 100
    //   95: aload_0
    //   96: iload_1
    //   97: invokespecial 93	com/android/server/AppOpsService:notifyWatchersOfChange	(I)V
    //   100: return
    //   101: astore_3
    //   102: aload_0
    //   103: monitorexit
    //   104: return
    //   105: astore_3
    //   106: aload_0
    //   107: monitorexit
    //   108: aload_3
    //   109: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	110	0	this	AppOpsService
    //   0	110	1	paramInt1	int
    //   0	110	2	paramBoolean	boolean
    //   0	110	3	paramIBinder	IBinder
    //   0	110	4	paramInt2	int
    //   0	110	5	paramArrayOfString	String[]
    //   1	90	6	i	int
    //   20	64	7	localClientRestrictionState1	ClientRestrictionState
    //   16	7	8	localClientRestrictionState2	ClientRestrictionState
    // Exception table:
    //   from	to	target	type
    //   27	38	101	android/os/RemoteException
    //   5	18	105	finally
    //   27	38	105	finally
    //   38	49	105	finally
    //   49	63	105	finally
    //   66	88	105	finally
  }
  
  private void verifyIncomingOp(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < 69)) {
      return;
    }
    throw new IllegalArgumentException("Bad operation #" + paramInt);
  }
  
  private void verifyIncomingUid(int paramInt)
  {
    if (paramInt == Binder.getCallingUid()) {
      return;
    }
    if (Binder.getCallingPid() == Process.myPid()) {
      return;
    }
    this.mContext.enforcePermission("android.permission.UPDATE_APP_OPS_STATS", Binder.getCallingPid(), Binder.getCallingUid(), null);
  }
  
  private void writeUidStateMode(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = AppOpsManager.opToDefaultMode(paramInt1);
    UidState localUidState = getUidStateLocked(paramInt2, false);
    if (localUidState == null)
    {
      if (paramInt3 == i) {
        return;
      }
      localUidState = new UidState(paramInt2);
      localUidState.opModes = new SparseIntArray();
      localUidState.opModes.put(paramInt1, paramInt3);
      this.mUidStates.put(paramInt2, localUidState);
      scheduleWriteLocked();
    }
    do
    {
      return;
      if (localUidState.opModes != null) {
        break;
      }
    } while (paramInt3 == i);
    localUidState.opModes = new SparseIntArray();
    localUidState.opModes.put(paramInt1, paramInt3);
    scheduleWriteLocked();
    return;
    if (localUidState.opModes.get(paramInt1) == paramInt3) {
      return;
    }
    if (paramInt3 == i)
    {
      localUidState.opModes.delete(paramInt1);
      if (localUidState.opModes.size() <= 0) {
        localUidState.opModes = null;
      }
    }
    for (;;)
    {
      scheduleWriteLocked();
      return;
      localUidState.opModes.put(paramInt1, paramInt3);
    }
  }
  
  public int checkAudioOperation(int paramInt1, int paramInt2, int paramInt3, String paramString)
  {
    try
    {
      bool = isPackageSuspendedForUser(paramString, paramInt3);
      if (bool)
      {
        Log.i("AppOps", "Audio disabled for suspended package=" + paramString + " for uid=" + paramInt3);
        return 1;
      }
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      for (;;)
      {
        boolean bool = false;
      }
      try
      {
        paramInt2 = checkRestrictionLocked(paramInt1, paramInt2, paramInt3, paramString);
        if (paramInt2 != 0) {
          return paramInt2;
        }
        return checkOperation(paramInt1, paramInt3, paramString);
      }
      finally
      {
        paramString = finally;
        throw paramString;
      }
    }
  }
  
  public int checkOperation(int paramInt1, int paramInt2, String paramString)
  {
    verifyIncomingUid(paramInt2);
    verifyIncomingOp(paramInt1);
    paramString = resolvePackageName(paramInt2, paramString);
    if (paramString == null) {
      return 1;
    }
    try
    {
      boolean bool = isOpRestrictedLocked(paramInt2, paramInt1, paramString);
      if (bool) {
        return 1;
      }
      paramInt1 = AppOpsManager.opToSwitch(paramInt1);
      UidState localUidState = getUidStateLocked(paramInt2, false);
      if ((localUidState != null) && (localUidState.opModes != null))
      {
        int i = localUidState.opModes.get(paramInt1);
        if (i != 0) {
          return i;
        }
      }
      paramString = getOpLocked(paramInt1, paramInt2, paramString, false);
      if (paramString == null)
      {
        paramInt1 = AppOpsManager.opToDefaultMode(paramInt1);
        return paramInt1;
      }
      paramInt1 = paramString.mode;
      return paramInt1;
    }
    finally {}
  }
  
  public int checkPackage(int paramInt, String paramString)
  {
    Preconditions.checkNotNull(paramString);
    try
    {
      paramString = getOpsRawLocked(paramInt, paramString, true);
      if (paramString != null) {
        return 0;
      }
      return 2;
    }
    finally
    {
      paramString = finally;
      throw paramString;
    }
  }
  
  protected void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    if (this.mContext.checkCallingOrSelfPermission("android.permission.DUMP") != 0)
    {
      paramPrintWriter.println("Permission Denial: can't dump ApOps service from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
      return;
    }
    int i;
    if (paramArrayOfString != null)
    {
      i = 0;
      while (i < paramArrayOfString.length)
      {
        paramFileDescriptor = paramArrayOfString[i];
        if ("-h".equals(paramFileDescriptor))
        {
          dumpHelp(paramPrintWriter);
          return;
        }
        if ("-a".equals(paramFileDescriptor))
        {
          i += 1;
        }
        else
        {
          if ((paramFileDescriptor.length() > 0) && (paramFileDescriptor.charAt(0) == '-'))
          {
            paramPrintWriter.println("Unknown option: " + paramFileDescriptor);
            return;
          }
          paramPrintWriter.println("Unknown command: " + paramFileDescriptor);
          return;
        }
      }
    }
    for (;;)
    {
      long l;
      int k;
      int j;
      int n;
      int m;
      Object localObject;
      try
      {
        paramPrintWriter.println("Current AppOps Service state:");
        l = System.currentTimeMillis();
        i = 0;
        if (this.mOpModeWatchers.size() > 0)
        {
          k = 1;
          paramPrintWriter.println("  Op mode watchers:");
          j = 0;
          i = k;
          if (j < this.mOpModeWatchers.size())
          {
            paramPrintWriter.print("    Op ");
            paramPrintWriter.print(AppOpsManager.opToName(this.mOpModeWatchers.keyAt(j)));
            paramPrintWriter.println(":");
            paramFileDescriptor = (ArrayList)this.mOpModeWatchers.valueAt(j);
            i = 0;
            if (i >= paramFileDescriptor.size()) {
              break label1381;
            }
            paramPrintWriter.print("      #");
            paramPrintWriter.print(i);
            paramPrintWriter.print(": ");
            paramPrintWriter.println(paramFileDescriptor.get(i));
            i += 1;
            continue;
          }
        }
        if (this.mPackageModeWatchers.size() > 0)
        {
          k = 1;
          paramPrintWriter.println("  Package mode watchers:");
          j = 0;
          i = k;
          if (j < this.mPackageModeWatchers.size())
          {
            paramPrintWriter.print("    Pkg ");
            paramPrintWriter.print((String)this.mPackageModeWatchers.keyAt(j));
            paramPrintWriter.println(":");
            paramFileDescriptor = (ArrayList)this.mPackageModeWatchers.valueAt(j);
            i = 0;
            if (i >= paramFileDescriptor.size()) {
              break label1390;
            }
            paramPrintWriter.print("      #");
            paramPrintWriter.print(i);
            paramPrintWriter.print(": ");
            paramPrintWriter.println(paramFileDescriptor.get(i));
            i += 1;
            continue;
          }
        }
        if (this.mModeWatchers.size() > 0)
        {
          k = 1;
          paramPrintWriter.println("  All mode watchers:");
          j = 0;
          i = k;
          if (j < this.mModeWatchers.size())
          {
            paramPrintWriter.print("    ");
            paramPrintWriter.print(this.mModeWatchers.keyAt(j));
            paramPrintWriter.print(" -> ");
            paramPrintWriter.println(this.mModeWatchers.valueAt(j));
            j += 1;
            continue;
          }
        }
        if (this.mClients.size() > 0)
        {
          k = 1;
          paramPrintWriter.println("  Clients:");
          j = 0;
          i = k;
          if (j < this.mClients.size())
          {
            paramPrintWriter.print("    ");
            paramPrintWriter.print(this.mClients.keyAt(j));
            paramPrintWriter.println(":");
            paramFileDescriptor = (ClientState)this.mClients.valueAt(j);
            paramPrintWriter.print("      ");
            paramPrintWriter.println(paramFileDescriptor);
            if ((paramFileDescriptor.mStartedOps == null) || (paramFileDescriptor.mStartedOps.size() <= 0)) {
              break label1399;
            }
            paramPrintWriter.println("      Started ops:");
            i = 0;
            if (i >= paramFileDescriptor.mStartedOps.size()) {
              break label1399;
            }
            paramArrayOfString = (Op)paramFileDescriptor.mStartedOps.get(i);
            paramPrintWriter.print("        ");
            paramPrintWriter.print("uid=");
            paramPrintWriter.print(paramArrayOfString.uid);
            paramPrintWriter.print(" pkg=");
            paramPrintWriter.print(paramArrayOfString.packageName);
            paramPrintWriter.print(" op=");
            paramPrintWriter.println(AppOpsManager.opToName(paramArrayOfString.op));
            i += 1;
            continue;
          }
        }
        k = i;
        if (this.mAudioRestrictions.size() > 0)
        {
          n = 0;
          j = 0;
          k = i;
          if (j < this.mAudioRestrictions.size())
          {
            paramFileDescriptor = AppOpsManager.opToName(this.mAudioRestrictions.keyAt(j));
            paramArrayOfString = (SparseArray)this.mAudioRestrictions.valueAt(j);
            k = 0;
            m = i;
            i = k;
            if (i >= paramArrayOfString.size()) {
              break label1421;
            }
            k = n;
            if (n == 0)
            {
              paramPrintWriter.println("  Audio Restrictions:");
              k = 1;
              m = 1;
            }
            n = paramArrayOfString.keyAt(i);
            paramPrintWriter.print("    ");
            paramPrintWriter.print(paramFileDescriptor);
            paramPrintWriter.print(" usage=");
            paramPrintWriter.print(AudioAttributes.usageToString(n));
            localObject = (Restriction)paramArrayOfString.valueAt(i);
            paramPrintWriter.print(": mode=");
            paramPrintWriter.println(((Restriction)localObject).mode);
            if (((Restriction)localObject).exceptionPackages.isEmpty()) {
              break label1408;
            }
            paramPrintWriter.println("      Exceptions:");
            n = 0;
            if (n >= ((Restriction)localObject).exceptionPackages.size()) {
              break label1408;
            }
            paramPrintWriter.print("        ");
            paramPrintWriter.println((String)((Restriction)localObject).exceptionPackages.valueAt(n));
            n += 1;
            continue;
          }
        }
        if (k == 0) {
          break label1434;
        }
        paramPrintWriter.println();
      }
      finally {}
      if (i < this.mUidStates.size())
      {
        paramFileDescriptor = (UidState)this.mUidStates.valueAt(i);
        paramPrintWriter.print("  Uid ");
        UserHandle.formatUid(paramPrintWriter, paramFileDescriptor.uid);
        paramPrintWriter.println(":");
        paramArrayOfString = paramFileDescriptor.opModes;
        if (paramArrayOfString != null)
        {
          k = paramArrayOfString.size();
          j = 0;
          if (j < k)
          {
            m = paramArrayOfString.keyAt(j);
            n = paramArrayOfString.valueAt(j);
            paramPrintWriter.print("      ");
            paramPrintWriter.print(AppOpsManager.opToName(m));
            paramPrintWriter.print(": mode=");
            paramPrintWriter.println(n);
            j += 1;
            continue;
          }
        }
        paramFileDescriptor = paramFileDescriptor.pkgOps;
        if (paramFileDescriptor != null)
        {
          paramFileDescriptor = paramFileDescriptor.values().iterator();
          if (paramFileDescriptor.hasNext())
          {
            paramArrayOfString = (Ops)paramFileDescriptor.next();
            paramPrintWriter.print("    Package ");
            paramPrintWriter.print(paramArrayOfString.packageName);
            paramPrintWriter.println(":");
            j = 0;
            if (j < paramArrayOfString.size())
            {
              localObject = (Op)paramArrayOfString.valueAt(j);
              paramPrintWriter.print("      ");
              paramPrintWriter.print(AppOpsManager.opToName(((Op)localObject).op));
              paramPrintWriter.print(": mode=");
              paramPrintWriter.print(((Op)localObject).mode);
              if (((Op)localObject).time != 0L)
              {
                paramPrintWriter.print("; time=");
                TimeUtils.formatDuration(l - ((Op)localObject).time, paramPrintWriter);
                paramPrintWriter.print(" ago");
              }
              if (((Op)localObject).rejectTime != 0L)
              {
                paramPrintWriter.print("; rejectTime=");
                TimeUtils.formatDuration(l - ((Op)localObject).rejectTime, paramPrintWriter);
                paramPrintWriter.print(" ago");
              }
              if (((Op)localObject).duration == -1)
              {
                paramPrintWriter.print(" (running)");
                paramPrintWriter.println();
                j += 1;
              }
            }
            else
            {
              continue;
            }
            if (((Op)localObject).duration == 0) {
              continue;
            }
            paramPrintWriter.print("; duration=");
            TimeUtils.formatDuration(((Op)localObject).duration, paramPrintWriter);
            continue;
          }
        }
      }
      else
      {
        return;
        label1381:
        j += 1;
        continue;
        label1390:
        j += 1;
        continue;
        label1399:
        j += 1;
        continue;
        label1408:
        i += 1;
        n = k;
        continue;
        label1421:
        j += 1;
        i = m;
        continue;
        label1434:
        i = 0;
        continue;
      }
      i += 1;
    }
  }
  
  public void finishOperation(IBinder paramIBinder, int paramInt1, int paramInt2, String paramString)
  {
    verifyIncomingUid(paramInt2);
    verifyIncomingOp(paramInt1);
    paramString = resolvePackageName(paramInt2, paramString);
    if (paramString == null) {
      return;
    }
    if (!(paramIBinder instanceof ClientState)) {
      return;
    }
    paramIBinder = (ClientState)paramIBinder;
    try
    {
      paramString = getOpLocked(paramInt1, paramInt2, paramString, true);
      if (paramString == null) {
        return;
      }
      if ((paramIBinder.mStartedOps != null) && (!paramIBinder.mStartedOps.remove(paramString))) {
        throw new IllegalStateException("Operation not started: uid" + paramString.uid + " pkg=" + paramString.packageName + " op=" + paramString.op);
      }
    }
    finally {}
    finishOperationLocked(paramString);
  }
  
  void finishOperationLocked(Op paramOp)
  {
    if (paramOp.nesting <= 1)
    {
      if (paramOp.nesting == 1)
      {
        paramOp.duration = ((int)(System.currentTimeMillis() - paramOp.time));
        paramOp.time += paramOp.duration;
      }
      for (;;)
      {
        paramOp.nesting = 0;
        return;
        Slog.w("AppOps", "Finishing op nesting under-run: uid " + paramOp.uid + " pkg " + paramOp.packageName + " code " + paramOp.op + " time=" + paramOp.time + " duration=" + paramOp.duration + " nesting=" + paramOp.nesting);
      }
    }
    paramOp.nesting -= 1;
  }
  
  public List<AppOpsManager.PackageOps> getOpsForPackage(int paramInt, String paramString, int[] paramArrayOfInt)
  {
    this.mContext.enforcePermission("android.permission.GET_APP_OPS_STATS", Binder.getCallingPid(), Binder.getCallingUid(), null);
    paramString = resolvePackageName(paramInt, paramString);
    if (paramString == null) {
      return Collections.emptyList();
    }
    try
    {
      paramString = getOpsRawLocked(paramInt, paramString, false);
      if (paramString == null) {
        return null;
      }
      paramArrayOfInt = collectOps(paramString, paramArrayOfInt);
      if (paramArrayOfInt == null) {
        return null;
      }
      ArrayList localArrayList = new ArrayList();
      localArrayList.add(new AppOpsManager.PackageOps(paramString.packageName, paramString.uidState.uid, paramArrayOfInt));
      return localArrayList;
    }
    finally {}
  }
  
  public List<AppOpsManager.PackageOps> getPackagesForOps(int[] paramArrayOfInt)
  {
    this.mContext.enforcePermission("android.permission.GET_APP_OPS_STATS", Binder.getCallingPid(), Binder.getCallingUid(), null);
    Object localObject1 = null;
    for (;;)
    {
      int i;
      Object localObject3;
      int j;
      ArrayList localArrayList;
      try
      {
        int k = this.mUidStates.size();
        i = 0;
        if (i < k)
        {
          localObject3 = (UidState)this.mUidStates.valueAt(i);
          localObject2 = localObject1;
          if (((UidState)localObject3).pkgOps == null) {
            break label202;
          }
          if (((UidState)localObject3).pkgOps.isEmpty())
          {
            localObject2 = localObject1;
            break label202;
          }
          localObject2 = ((UidState)localObject3).pkgOps;
          int m = ((ArrayMap)localObject2).size();
          j = 0;
          if (j >= m) {
            break label198;
          }
        }
      }
      finally {}
      try
      {
        localObject3 = (Ops)((ArrayMap)localObject2).valueAt(j);
        localArrayList = collectOps((Ops)localObject3, paramArrayOfInt);
        if (localArrayList == null) {
          break label195;
        }
        if (localObject1 != null) {
          break label192;
        }
        localObject1 = new ArrayList();
      }
      finally
      {
        continue;
        continue;
        continue;
      }
      ((ArrayList)localObject1).add(new AppOpsManager.PackageOps(((Ops)localObject3).packageName, ((Ops)localObject3).uidState.uid, localArrayList));
      j += 1;
      continue;
      return (List<AppOpsManager.PackageOps>)localObject1;
      label192:
      label195:
      label198:
      Object localObject2 = localObject1;
      label202:
      i += 1;
      localObject1 = localObject2;
    }
  }
  
  public IBinder getToken(IBinder paramIBinder)
  {
    try
    {
      ClientState localClientState2 = (ClientState)this.mClients.get(paramIBinder);
      ClientState localClientState1 = localClientState2;
      if (localClientState2 == null)
      {
        localClientState1 = new ClientState(paramIBinder);
        this.mClients.put(paramIBinder, localClientState1);
      }
      return localClientState1;
    }
    finally {}
  }
  
  public int noteOperation(int paramInt1, int paramInt2, String paramString)
  {
    verifyIncomingUid(paramInt2);
    verifyIncomingOp(paramInt1);
    paramString = resolvePackageName(paramInt2, paramString);
    if (paramString == null) {
      return 1;
    }
    return noteOperationUnchecked(paramInt1, paramInt2, paramString, 0, null);
  }
  
  public int noteProxyOperation(int paramInt1, String paramString1, int paramInt2, String paramString2)
  {
    verifyIncomingOp(paramInt1);
    int i = Binder.getCallingUid();
    paramString1 = resolvePackageName(i, paramString1);
    if (paramString1 == null) {
      return 1;
    }
    i = noteOperationUnchecked(paramInt1, i, paramString1, -1, null);
    if ((i != 0) || (Binder.getCallingUid() == paramInt2)) {
      return i;
    }
    paramString2 = resolvePackageName(paramInt2, paramString2);
    if (paramString2 == null) {
      return 1;
    }
    return noteOperationUnchecked(paramInt1, paramInt2, paramString2, i, paramString1);
  }
  
  /* Error */
  public void notifyOperation(int paramInt1, int paramInt2, String paramString, int paramInt3, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: iload_2
    //   2: invokespecial 838	com/android/server/AppOpsService:verifyIncomingUid	(I)V
    //   5: aload_0
    //   6: iload_1
    //   7: invokespecial 840	com/android/server/AppOpsService:verifyIncomingOp	(I)V
    //   10: aconst_null
    //   11: astore 8
    //   13: iload_1
    //   14: invokestatic 504	android/app/AppOpsManager:opToSwitch	(I)I
    //   17: istore 6
    //   19: aload_0
    //   20: monitorenter
    //   21: aload_0
    //   22: iload_1
    //   23: iload_2
    //   24: aload_3
    //   25: iload 4
    //   27: invokespecial 1023	com/android/server/AppOpsService:recordOperationLocked	(IILjava/lang/String;I)V
    //   30: aload_0
    //   31: iload 6
    //   33: iload_2
    //   34: aload_3
    //   35: iconst_1
    //   36: invokespecial 762	com/android/server/AppOpsService:getOpLocked	(IILjava/lang/String;Z)Lcom/android/server/AppOpsService$Op;
    //   39: astore 9
    //   41: aload 8
    //   43: astore 7
    //   45: aload 9
    //   47: ifnull +149 -> 196
    //   50: aload 9
    //   52: getfield 1027	com/android/server/AppOpsService$Op:dialogReqQueue	Lcom/android/server/PermissionDialogReqQueue;
    //   55: invokevirtual 1033	com/android/server/PermissionDialogReqQueue:getDialog	()Lcom/android/server/PermissionDialog;
    //   58: ifnull +22 -> 80
    //   61: aload 9
    //   63: getfield 1027	com/android/server/AppOpsService$Op:dialogReqQueue	Lcom/android/server/PermissionDialogReqQueue;
    //   66: iload 4
    //   68: invokevirtual 1036	com/android/server/PermissionDialogReqQueue:notifyAll	(I)V
    //   71: aload 9
    //   73: getfield 1027	com/android/server/AppOpsService$Op:dialogReqQueue	Lcom/android/server/PermissionDialogReqQueue;
    //   76: aconst_null
    //   77: invokevirtual 1040	com/android/server/PermissionDialogReqQueue:setDialog	(Lcom/android/server/PermissionDialog;)V
    //   80: aload 8
    //   82: astore 7
    //   84: iload 5
    //   86: ifeq +110 -> 196
    //   89: aload 9
    //   91: iconst_1
    //   92: invokestatic 1044	com/android/server/AppOpsService$Op:-set0	(Lcom/android/server/AppOpsService$Op;Z)Z
    //   95: pop
    //   96: aload_0
    //   97: iload_1
    //   98: iload_2
    //   99: iload 4
    //   101: invokespecial 1046	com/android/server/AppOpsService:writeUidStateMode	(III)V
    //   104: aload_0
    //   105: getfield 113	com/android/server/AppOpsService:mOpModeWatchers	Landroid/util/SparseArray;
    //   108: iload 6
    //   110: invokevirtual 202	android/util/SparseArray:get	(I)Ljava/lang/Object;
    //   113: checkcast 148	java/util/ArrayList
    //   116: astore 9
    //   118: aload 9
    //   120: ifnull +154 -> 274
    //   123: new 148	java/util/ArrayList
    //   126: dup
    //   127: invokespecial 160	java/util/ArrayList:<init>	()V
    //   130: astore 7
    //   132: aload 7
    //   134: astore 8
    //   136: aload 7
    //   138: aload 9
    //   140: invokevirtual 1050	java/util/ArrayList:addAll	(Ljava/util/Collection;)Z
    //   143: pop
    //   144: aload 7
    //   146: astore 8
    //   148: aload_0
    //   149: getfield 115	com/android/server/AppOpsService:mPackageModeWatchers	Landroid/util/ArrayMap;
    //   152: aload_3
    //   153: invokevirtual 360	android/util/ArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   156: checkcast 148	java/util/ArrayList
    //   159: astore 9
    //   161: aload 9
    //   163: ifnull +108 -> 271
    //   166: aload 7
    //   168: ifnonnull +100 -> 268
    //   171: aload 7
    //   173: astore 8
    //   175: new 148	java/util/ArrayList
    //   178: dup
    //   179: invokespecial 160	java/util/ArrayList:<init>	()V
    //   182: astore 7
    //   184: aload 7
    //   186: aload 9
    //   188: invokevirtual 1050	java/util/ArrayList:addAll	(Ljava/util/Collection;)Z
    //   191: pop
    //   192: aload_0
    //   193: invokespecial 1052	com/android/server/AppOpsService:scheduleWriteNowLocked	()V
    //   196: aload_0
    //   197: monitorexit
    //   198: aload 7
    //   200: ifnull +63 -> 263
    //   203: iconst_0
    //   204: istore_1
    //   205: iload_1
    //   206: aload 7
    //   208: invokevirtual 152	java/util/ArrayList:size	()I
    //   211: if_icmpge +52 -> 263
    //   214: aload 7
    //   216: iload_1
    //   217: invokevirtual 156	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   220: checkcast 15	com/android/server/AppOpsService$Callback
    //   223: getfield 536	com/android/server/AppOpsService$Callback:mCallback	Lcom/android/internal/app/IAppOpsCallback;
    //   226: iload 6
    //   228: iload_2
    //   229: aload_3
    //   230: invokeinterface 541 4 0
    //   235: iload_1
    //   236: iconst_1
    //   237: iadd
    //   238: istore_1
    //   239: goto -34 -> 205
    //   242: astore_3
    //   243: aload_0
    //   244: monitorexit
    //   245: aload_3
    //   246: athrow
    //   247: astore 8
    //   249: ldc 50
    //   251: ldc_w 1054
    //   254: aload 8
    //   256: invokestatic 546	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   259: pop
    //   260: goto -25 -> 235
    //   263: return
    //   264: astore_3
    //   265: goto -22 -> 243
    //   268: goto -84 -> 184
    //   271: goto -79 -> 192
    //   274: aconst_null
    //   275: astore 7
    //   277: goto -133 -> 144
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	280	0	this	AppOpsService
    //   0	280	1	paramInt1	int
    //   0	280	2	paramInt2	int
    //   0	280	3	paramString	String
    //   0	280	4	paramInt3	int
    //   0	280	5	paramBoolean	boolean
    //   17	210	6	i	int
    //   43	233	7	localObject1	Object
    //   11	163	8	localObject2	Object
    //   247	8	8	localRemoteException	RemoteException
    //   39	148	9	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   21	41	242	finally
    //   50	80	242	finally
    //   89	118	242	finally
    //   123	132	242	finally
    //   184	192	242	finally
    //   192	196	242	finally
    //   214	235	247	android/os/RemoteException
    //   136	144	264	finally
    //   148	161	264	finally
    //   175	184	264	finally
  }
  
  public void onShellCommand(FileDescriptor paramFileDescriptor1, FileDescriptor paramFileDescriptor2, FileDescriptor paramFileDescriptor3, String[] paramArrayOfString, ResultReceiver paramResultReceiver)
  {
    new Shell(this, this).exec(this, paramFileDescriptor1, paramFileDescriptor2, paramFileDescriptor3, paramArrayOfString, paramResultReceiver);
  }
  
  public void packageRemoved(int paramInt, String paramString)
  {
    try
    {
      UidState localUidState = (UidState)this.mUidStates.get(paramInt);
      if (localUidState == null) {
        return;
      }
      int j = 0;
      int i = j;
      if (localUidState.pkgOps != null)
      {
        i = j;
        if (localUidState.pkgOps.remove(paramString) != null) {
          i = 1;
        }
      }
      if ((i != 0) && (localUidState.pkgOps.isEmpty()) && (getPackagesForUid(paramInt).length <= 0)) {
        this.mUidStates.remove(paramInt);
      }
      if (i != 0) {
        scheduleFastWriteLocked();
      }
      return;
    }
    finally {}
  }
  
  public int permissionToOpCode(String paramString)
  {
    if (paramString == null) {
      return -1;
    }
    return AppOpsManager.permissionToOpCode(paramString);
  }
  
  public void publish(Context paramContext)
  {
    this.mContext = paramContext;
    ServiceManager.addService("appops", asBinder());
  }
  
  void readPackage(XmlPullParser paramXmlPullParser)
    throws NumberFormatException, XmlPullParserException, IOException
  {
    String str = paramXmlPullParser.getAttributeValue(null, "n");
    int i = paramXmlPullParser.getDepth();
    for (;;)
    {
      int j = paramXmlPullParser.next();
      if ((j == 1) || ((j == 3) && (paramXmlPullParser.getDepth() <= i))) {
        break;
      }
      if ((j != 3) && (j != 4)) {
        if (paramXmlPullParser.getName().equals("uid"))
        {
          readUid(paramXmlPullParser, str);
        }
        else
        {
          Slog.w("AppOps", "Unknown element under <pkg>: " + paramXmlPullParser.getName());
          XmlUtils.skipCurrentTag(paramXmlPullParser);
        }
      }
    }
  }
  
  /* Error */
  void readState()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 128	com/android/server/AppOpsService:mFile	Landroid/util/AtomicFile;
    //   4: astore_3
    //   5: aload_3
    //   6: monitorenter
    //   7: aload_0
    //   8: monitorenter
    //   9: aload_0
    //   10: getfield 128	com/android/server/AppOpsService:mFile	Landroid/util/AtomicFile;
    //   13: invokevirtual 1130	android/util/AtomicFile:openRead	()Ljava/io/FileInputStream;
    //   16: astore 4
    //   18: aload_0
    //   19: getfield 108	com/android/server/AppOpsService:mUidStates	Landroid/util/SparseArray;
    //   22: invokevirtual 1133	android/util/SparseArray:clear	()V
    //   25: invokestatic 1139	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   28: astore 5
    //   30: aload 5
    //   32: aload 4
    //   34: getstatic 1145	java/nio/charset/StandardCharsets:UTF_8	Ljava/nio/charset/Charset;
    //   37: invokevirtual 1150	java/nio/charset/Charset:name	()Ljava/lang/String;
    //   40: invokeinterface 1154 3 0
    //   45: aload 5
    //   47: invokeinterface 1104 1 0
    //   52: istore_1
    //   53: iload_1
    //   54: iconst_2
    //   55: if_icmpeq +8 -> 63
    //   58: iload_1
    //   59: iconst_1
    //   60: if_icmpne -15 -> 45
    //   63: iload_1
    //   64: iconst_2
    //   65: if_icmpeq +109 -> 174
    //   68: new 983	java/lang/IllegalStateException
    //   71: dup
    //   72: ldc_w 1156
    //   75: invokespecial 986	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
    //   78: athrow
    //   79: astore 5
    //   81: ldc 50
    //   83: new 225	java/lang/StringBuilder
    //   86: dup
    //   87: invokespecial 226	java/lang/StringBuilder:<init>	()V
    //   90: ldc_w 1158
    //   93: invokevirtual 230	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   96: aload 5
    //   98: invokevirtual 705	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   101: invokevirtual 236	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   104: invokestatic 501	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   107: pop
    //   108: iconst_0
    //   109: ifne +10 -> 119
    //   112: aload_0
    //   113: getfield 108	com/android/server/AppOpsService:mUidStates	Landroid/util/SparseArray;
    //   116: invokevirtual 1133	android/util/SparseArray:clear	()V
    //   119: aload 4
    //   121: invokevirtual 1163	java/io/FileInputStream:close	()V
    //   124: aload_0
    //   125: monitorexit
    //   126: aload_3
    //   127: monitorexit
    //   128: return
    //   129: astore 4
    //   131: ldc 50
    //   133: new 225	java/lang/StringBuilder
    //   136: dup
    //   137: invokespecial 226	java/lang/StringBuilder:<init>	()V
    //   140: ldc_w 1165
    //   143: invokevirtual 230	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   146: aload_0
    //   147: getfield 128	com/android/server/AppOpsService:mFile	Landroid/util/AtomicFile;
    //   150: invokevirtual 1169	android/util/AtomicFile:getBaseFile	()Ljava/io/File;
    //   153: invokevirtual 705	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   156: ldc_w 1171
    //   159: invokevirtual 230	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   162: invokevirtual 236	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   165: invokestatic 1172	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   168: pop
    //   169: aload_0
    //   170: monitorexit
    //   171: aload_3
    //   172: monitorexit
    //   173: return
    //   174: aload 5
    //   176: invokeinterface 1102 1 0
    //   181: istore_1
    //   182: aload 5
    //   184: invokeinterface 1104 1 0
    //   189: istore_2
    //   190: iload_2
    //   191: iconst_1
    //   192: if_icmpeq +277 -> 469
    //   195: iload_2
    //   196: iconst_3
    //   197: if_icmpne +14 -> 211
    //   200: aload 5
    //   202: invokeinterface 1102 1 0
    //   207: iload_1
    //   208: if_icmple +261 -> 469
    //   211: iload_2
    //   212: iconst_3
    //   213: if_icmpeq -31 -> 182
    //   216: iload_2
    //   217: iconst_4
    //   218: if_icmpeq -36 -> 182
    //   221: aload 5
    //   223: invokeinterface 1107 1 0
    //   228: astore 6
    //   230: aload 6
    //   232: ldc_w 1173
    //   235: invokevirtual 183	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   238: ifeq +65 -> 303
    //   241: aload_0
    //   242: aload 5
    //   244: invokevirtual 1175	com/android/server/AppOpsService:readPackage	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   247: goto -65 -> 182
    //   250: astore 5
    //   252: ldc 50
    //   254: new 225	java/lang/StringBuilder
    //   257: dup
    //   258: invokespecial 226	java/lang/StringBuilder:<init>	()V
    //   261: ldc_w 1158
    //   264: invokevirtual 230	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   267: aload 5
    //   269: invokevirtual 705	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   272: invokevirtual 236	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   275: invokestatic 501	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   278: pop
    //   279: iconst_0
    //   280: ifne +10 -> 290
    //   283: aload_0
    //   284: getfield 108	com/android/server/AppOpsService:mUidStates	Landroid/util/SparseArray;
    //   287: invokevirtual 1133	android/util/SparseArray:clear	()V
    //   290: aload 4
    //   292: invokevirtual 1163	java/io/FileInputStream:close	()V
    //   295: goto -171 -> 124
    //   298: astore 4
    //   300: goto -176 -> 124
    //   303: aload 6
    //   305: ldc_w 1108
    //   308: invokevirtual 183	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   311: ifeq +65 -> 376
    //   314: aload_0
    //   315: aload 5
    //   317: invokevirtual 1178	com/android/server/AppOpsService:readUidOps	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   320: goto -138 -> 182
    //   323: astore 5
    //   325: ldc 50
    //   327: new 225	java/lang/StringBuilder
    //   330: dup
    //   331: invokespecial 226	java/lang/StringBuilder:<init>	()V
    //   334: ldc_w 1158
    //   337: invokevirtual 230	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   340: aload 5
    //   342: invokevirtual 705	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   345: invokevirtual 236	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   348: invokestatic 501	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   351: pop
    //   352: iconst_0
    //   353: ifne +10 -> 363
    //   356: aload_0
    //   357: getfield 108	com/android/server/AppOpsService:mUidStates	Landroid/util/SparseArray;
    //   360: invokevirtual 1133	android/util/SparseArray:clear	()V
    //   363: aload 4
    //   365: invokevirtual 1163	java/io/FileInputStream:close	()V
    //   368: goto -244 -> 124
    //   371: astore 4
    //   373: goto -249 -> 124
    //   376: ldc 50
    //   378: new 225	java/lang/StringBuilder
    //   381: dup
    //   382: invokespecial 226	java/lang/StringBuilder:<init>	()V
    //   385: ldc_w 1180
    //   388: invokevirtual 230	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   391: aload 5
    //   393: invokeinterface 1107 1 0
    //   398: invokevirtual 230	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   401: invokevirtual 236	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   404: invokestatic 501	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   407: pop
    //   408: aload 5
    //   410: invokestatic 1119	com/android/internal/util/XmlUtils:skipCurrentTag	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   413: goto -231 -> 182
    //   416: astore 5
    //   418: ldc 50
    //   420: new 225	java/lang/StringBuilder
    //   423: dup
    //   424: invokespecial 226	java/lang/StringBuilder:<init>	()V
    //   427: ldc_w 1158
    //   430: invokevirtual 230	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   433: aload 5
    //   435: invokevirtual 705	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   438: invokevirtual 236	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   441: invokestatic 501	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   444: pop
    //   445: iconst_0
    //   446: ifne +10 -> 456
    //   449: aload_0
    //   450: getfield 108	com/android/server/AppOpsService:mUidStates	Landroid/util/SparseArray;
    //   453: invokevirtual 1133	android/util/SparseArray:clear	()V
    //   456: aload 4
    //   458: invokevirtual 1163	java/io/FileInputStream:close	()V
    //   461: goto -337 -> 124
    //   464: astore 4
    //   466: goto -342 -> 124
    //   469: iconst_1
    //   470: ifne +10 -> 480
    //   473: aload_0
    //   474: getfield 108	com/android/server/AppOpsService:mUidStates	Landroid/util/SparseArray;
    //   477: invokevirtual 1133	android/util/SparseArray:clear	()V
    //   480: aload 4
    //   482: invokevirtual 1163	java/io/FileInputStream:close	()V
    //   485: goto -361 -> 124
    //   488: astore 4
    //   490: goto -366 -> 124
    //   493: astore 5
    //   495: ldc 50
    //   497: new 225	java/lang/StringBuilder
    //   500: dup
    //   501: invokespecial 226	java/lang/StringBuilder:<init>	()V
    //   504: ldc_w 1158
    //   507: invokevirtual 230	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   510: aload 5
    //   512: invokevirtual 705	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   515: invokevirtual 236	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   518: invokestatic 501	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   521: pop
    //   522: iconst_0
    //   523: ifne +10 -> 533
    //   526: aload_0
    //   527: getfield 108	com/android/server/AppOpsService:mUidStates	Landroid/util/SparseArray;
    //   530: invokevirtual 1133	android/util/SparseArray:clear	()V
    //   533: aload 4
    //   535: invokevirtual 1163	java/io/FileInputStream:close	()V
    //   538: goto -414 -> 124
    //   541: astore 4
    //   543: goto -419 -> 124
    //   546: astore 5
    //   548: ldc 50
    //   550: new 225	java/lang/StringBuilder
    //   553: dup
    //   554: invokespecial 226	java/lang/StringBuilder:<init>	()V
    //   557: ldc_w 1158
    //   560: invokevirtual 230	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   563: aload 5
    //   565: invokevirtual 705	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   568: invokevirtual 236	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   571: invokestatic 501	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   574: pop
    //   575: iconst_0
    //   576: ifne +10 -> 586
    //   579: aload_0
    //   580: getfield 108	com/android/server/AppOpsService:mUidStates	Landroid/util/SparseArray;
    //   583: invokevirtual 1133	android/util/SparseArray:clear	()V
    //   586: aload 4
    //   588: invokevirtual 1163	java/io/FileInputStream:close	()V
    //   591: goto -467 -> 124
    //   594: astore 4
    //   596: goto -472 -> 124
    //   599: astore 4
    //   601: goto -477 -> 124
    //   604: astore 5
    //   606: iconst_0
    //   607: ifne +10 -> 617
    //   610: aload_0
    //   611: getfield 108	com/android/server/AppOpsService:mUidStates	Landroid/util/SparseArray;
    //   614: invokevirtual 1133	android/util/SparseArray:clear	()V
    //   617: aload 4
    //   619: invokevirtual 1163	java/io/FileInputStream:close	()V
    //   622: aload 5
    //   624: athrow
    //   625: astore 4
    //   627: aload_0
    //   628: monitorexit
    //   629: aload 4
    //   631: athrow
    //   632: astore 4
    //   634: aload_3
    //   635: monitorexit
    //   636: aload 4
    //   638: athrow
    //   639: astore 4
    //   641: goto -19 -> 622
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	644	0	this	AppOpsService
    //   52	157	1	i	int
    //   189	30	2	j	int
    //   4	631	3	localAtomicFile	AtomicFile
    //   16	104	4	localFileInputStream	java.io.FileInputStream
    //   129	162	4	localFileNotFoundException	java.io.FileNotFoundException
    //   298	66	4	localIOException1	IOException
    //   371	86	4	localIOException2	IOException
    //   464	17	4	localIOException3	IOException
    //   488	46	4	localIOException4	IOException
    //   541	46	4	localIOException5	IOException
    //   594	1	4	localIOException6	IOException
    //   599	19	4	localIOException7	IOException
    //   625	5	4	localObject1	Object
    //   632	5	4	localObject2	Object
    //   639	1	4	localIOException8	IOException
    //   28	18	5	localXmlPullParser	XmlPullParser
    //   79	164	5	localIllegalStateException	IllegalStateException
    //   250	66	5	localNullPointerException	NullPointerException
    //   323	86	5	localNumberFormatException	NumberFormatException
    //   416	18	5	localXmlPullParserException	XmlPullParserException
    //   493	18	5	localIndexOutOfBoundsException	IndexOutOfBoundsException
    //   546	18	5	localIOException9	IOException
    //   604	19	5	localObject3	Object
    //   228	76	6	str	String
    // Exception table:
    //   from	to	target	type
    //   25	45	79	java/lang/IllegalStateException
    //   45	53	79	java/lang/IllegalStateException
    //   68	79	79	java/lang/IllegalStateException
    //   174	182	79	java/lang/IllegalStateException
    //   182	190	79	java/lang/IllegalStateException
    //   200	211	79	java/lang/IllegalStateException
    //   221	247	79	java/lang/IllegalStateException
    //   303	320	79	java/lang/IllegalStateException
    //   376	413	79	java/lang/IllegalStateException
    //   9	18	129	java/io/FileNotFoundException
    //   25	45	250	java/lang/NullPointerException
    //   45	53	250	java/lang/NullPointerException
    //   68	79	250	java/lang/NullPointerException
    //   174	182	250	java/lang/NullPointerException
    //   182	190	250	java/lang/NullPointerException
    //   200	211	250	java/lang/NullPointerException
    //   221	247	250	java/lang/NullPointerException
    //   303	320	250	java/lang/NullPointerException
    //   376	413	250	java/lang/NullPointerException
    //   290	295	298	java/io/IOException
    //   25	45	323	java/lang/NumberFormatException
    //   45	53	323	java/lang/NumberFormatException
    //   68	79	323	java/lang/NumberFormatException
    //   174	182	323	java/lang/NumberFormatException
    //   182	190	323	java/lang/NumberFormatException
    //   200	211	323	java/lang/NumberFormatException
    //   221	247	323	java/lang/NumberFormatException
    //   303	320	323	java/lang/NumberFormatException
    //   376	413	323	java/lang/NumberFormatException
    //   363	368	371	java/io/IOException
    //   25	45	416	org/xmlpull/v1/XmlPullParserException
    //   45	53	416	org/xmlpull/v1/XmlPullParserException
    //   68	79	416	org/xmlpull/v1/XmlPullParserException
    //   174	182	416	org/xmlpull/v1/XmlPullParserException
    //   182	190	416	org/xmlpull/v1/XmlPullParserException
    //   200	211	416	org/xmlpull/v1/XmlPullParserException
    //   221	247	416	org/xmlpull/v1/XmlPullParserException
    //   303	320	416	org/xmlpull/v1/XmlPullParserException
    //   376	413	416	org/xmlpull/v1/XmlPullParserException
    //   456	461	464	java/io/IOException
    //   480	485	488	java/io/IOException
    //   25	45	493	java/lang/IndexOutOfBoundsException
    //   45	53	493	java/lang/IndexOutOfBoundsException
    //   68	79	493	java/lang/IndexOutOfBoundsException
    //   174	182	493	java/lang/IndexOutOfBoundsException
    //   182	190	493	java/lang/IndexOutOfBoundsException
    //   200	211	493	java/lang/IndexOutOfBoundsException
    //   221	247	493	java/lang/IndexOutOfBoundsException
    //   303	320	493	java/lang/IndexOutOfBoundsException
    //   376	413	493	java/lang/IndexOutOfBoundsException
    //   533	538	541	java/io/IOException
    //   25	45	546	java/io/IOException
    //   45	53	546	java/io/IOException
    //   68	79	546	java/io/IOException
    //   174	182	546	java/io/IOException
    //   182	190	546	java/io/IOException
    //   200	211	546	java/io/IOException
    //   221	247	546	java/io/IOException
    //   303	320	546	java/io/IOException
    //   376	413	546	java/io/IOException
    //   586	591	594	java/io/IOException
    //   119	124	599	java/io/IOException
    //   25	45	604	finally
    //   45	53	604	finally
    //   68	79	604	finally
    //   81	108	604	finally
    //   174	182	604	finally
    //   182	190	604	finally
    //   200	211	604	finally
    //   221	247	604	finally
    //   252	279	604	finally
    //   303	320	604	finally
    //   325	352	604	finally
    //   376	413	604	finally
    //   418	445	604	finally
    //   495	522	604	finally
    //   548	575	604	finally
    //   9	18	625	finally
    //   18	25	625	finally
    //   112	119	625	finally
    //   119	124	625	finally
    //   131	169	625	finally
    //   283	290	625	finally
    //   290	295	625	finally
    //   356	363	625	finally
    //   363	368	625	finally
    //   449	456	625	finally
    //   456	461	625	finally
    //   473	480	625	finally
    //   480	485	625	finally
    //   526	533	625	finally
    //   533	538	625	finally
    //   579	586	625	finally
    //   586	591	625	finally
    //   610	617	625	finally
    //   617	622	625	finally
    //   622	625	625	finally
    //   7	9	632	finally
    //   124	126	632	finally
    //   169	171	632	finally
    //   627	632	632	finally
    //   617	622	639	java/io/IOException
  }
  
  void readUid(XmlPullParser paramXmlPullParser, String paramString)
    throws NumberFormatException, XmlPullParserException, IOException
  {
    int i = Integer.parseInt(paramXmlPullParser.getAttributeValue(null, "n"));
    Object localObject = paramXmlPullParser.getAttributeValue(null, "p");
    boolean bool2 = false;
    boolean bool1;
    if (localObject == null)
    {
      try
      {
        if (ActivityThread.getPackageManager() == null) {
          break label430;
        }
        localObject = ActivityThread.getPackageManager().getApplicationInfo(paramString, 0, UserHandle.getUserId(i));
        bool1 = bool2;
        if (localObject != null)
        {
          j = ((ApplicationInfo)localObject).privateFlags;
          if ((j & 0x8) == 0) {
            break label424;
          }
          bool1 = true;
        }
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          int j;
          Slog.w("AppOps", "Could not contact PackageManager", localRemoteException);
          bool1 = bool2;
        }
      }
      j = paramXmlPullParser.getDepth();
    }
    for (;;)
    {
      int k = paramXmlPullParser.next();
      if ((k == 1) || ((k == 3) && (paramXmlPullParser.getDepth() <= j))) {
        return;
      }
      if ((k != 3) && (k != 4))
      {
        if (paramXmlPullParser.getName().equals("op"))
        {
          Op localOp = new Op(i, paramString, Integer.parseInt(paramXmlPullParser.getAttributeValue(null, "n")));
          localObject = paramXmlPullParser.getAttributeValue(null, "m");
          if (localObject != null) {
            localOp.mode = Integer.parseInt((String)localObject);
          }
          localObject = paramXmlPullParser.getAttributeValue(null, "t");
          if (localObject != null) {
            localOp.time = Long.parseLong((String)localObject);
          }
          localObject = paramXmlPullParser.getAttributeValue(null, "r");
          if (localObject != null) {
            localOp.rejectTime = Long.parseLong((String)localObject);
          }
          localObject = paramXmlPullParser.getAttributeValue(null, "d");
          if (localObject != null) {
            localOp.duration = Integer.parseInt((String)localObject);
          }
          localObject = paramXmlPullParser.getAttributeValue(null, "pu");
          if (localObject != null) {
            localOp.proxyUid = Integer.parseInt((String)localObject);
          }
          localObject = paramXmlPullParser.getAttributeValue(null, "pp");
          if (localObject != null) {
            localOp.proxyPackageName = ((String)localObject);
          }
          UidState localUidState = getUidStateLocked(i, true);
          if (localUidState.pkgOps == null) {
            localUidState.pkgOps = new ArrayMap();
          }
          Ops localOps = (Ops)localUidState.pkgOps.get(paramString);
          localObject = localOps;
          if (localOps == null)
          {
            localObject = new Ops(paramString, localUidState, bool1);
            localUidState.pkgOps.put(paramString, localObject);
          }
          ((Ops)localObject).put(localOp.op, localOp);
          continue;
          label424:
          bool1 = false;
          break;
          label430:
          return;
          bool1 = Boolean.parseBoolean(localRemoteException);
          break;
        }
        Slog.w("AppOps", "Unknown element under <pkg>: " + paramXmlPullParser.getName());
        XmlUtils.skipCurrentTag(paramXmlPullParser);
      }
    }
  }
  
  void readUidOps(XmlPullParser paramXmlPullParser)
    throws NumberFormatException, XmlPullParserException, IOException
  {
    int i = Integer.parseInt(paramXmlPullParser.getAttributeValue(null, "n"));
    int j = paramXmlPullParser.getDepth();
    for (;;)
    {
      int k = paramXmlPullParser.next();
      if ((k == 1) || ((k == 3) && (paramXmlPullParser.getDepth() <= j))) {
        break;
      }
      if ((k != 3) && (k != 4)) {
        if (paramXmlPullParser.getName().equals("op"))
        {
          k = Integer.parseInt(paramXmlPullParser.getAttributeValue(null, "n"));
          int m = Integer.parseInt(paramXmlPullParser.getAttributeValue(null, "m"));
          UidState localUidState = getUidStateLocked(i, true);
          if (localUidState.opModes == null) {
            localUidState.opModes = new SparseIntArray();
          }
          localUidState.opModes.put(k, m);
        }
        else
        {
          Slog.w("AppOps", "Unknown element under <uid-ops>: " + paramXmlPullParser.getName());
          XmlUtils.skipCurrentTag(paramXmlPullParser);
        }
      }
    }
  }
  
  public void removeUser(int paramInt)
    throws RemoteException
  {
    checkSystemUid("removeUser");
    try
    {
      int i = this.mOpUserRestrictions.size() - 1;
      while (i >= 0)
      {
        ((ClientRestrictionState)this.mOpUserRestrictions.valueAt(i)).removeUser(paramInt);
        i -= 1;
      }
      return;
    }
    finally {}
  }
  
  public void resetAllModes(int paramInt, String paramString)
  {
    int i = Binder.getCallingPid();
    int j = Binder.getCallingUid();
    this.mContext.enforcePermission("android.permission.UPDATE_APP_OPS_STATS", i, j, null);
    int n = ActivityManager.handleIncomingUser(i, j, paramInt, true, true, "resetAllModes", null);
    paramInt = -1;
    i = paramInt;
    if (paramString != null) {}
    try
    {
      i = AppGlobals.getPackageManager().getPackageUid(paramString, 8192, n);
      localObject2 = null;
      paramInt = 0;
      Object localObject3;
      Object localObject1;
      try
      {
        j = this.mUidStates.size() - 1;
        if (j < 0) {
          break label685;
        }
        localObject3 = (UidState)this.mUidStates.valueAt(j);
        Object localObject4 = ((UidState)localObject3).opModes;
        localObject1 = localObject2;
        Object localObject5;
        if (localObject4 != null) {
          if (((UidState)localObject3).uid != i)
          {
            localObject1 = localObject2;
            if (i != -1) {}
          }
          else
          {
            k = ((SparseIntArray)localObject4).size() - 1;
            localObject1 = localObject2;
            if (k >= 0)
            {
              int i1 = ((SparseIntArray)localObject4).keyAt(k);
              localObject1 = localObject2;
              if (!AppOpsManager.opAllowsReset(i1)) {
                break label822;
              }
              ((SparseIntArray)localObject4).removeAt(k);
              if (((SparseIntArray)localObject4).size() <= 0) {
                ((UidState)localObject3).opModes = null;
              }
              localObject5 = getPackagesForUid(((UidState)localObject3).uid);
              int i2 = localObject5.length;
              m = 0;
              for (;;)
              {
                localObject1 = localObject2;
                if (m >= i2) {
                  break;
                }
                localObject1 = localObject5[m];
                localObject2 = addCallbacks(addCallbacks((HashMap)localObject2, i1, ((UidState)localObject3).uid, (String)localObject1, (ArrayList)this.mOpModeWatchers.get(i1)), i1, ((UidState)localObject3).uid, (String)localObject1, (ArrayList)this.mPackageModeWatchers.get(localObject1));
                m += 1;
              }
            }
          }
        }
        if (((UidState)localObject3).pkgOps == null)
        {
          localObject2 = localObject1;
          k = paramInt;
          break label835;
        }
        if (n != -1)
        {
          localObject2 = localObject1;
          k = paramInt;
          if (n != UserHandle.getUserId(((UidState)localObject3).uid)) {
            break label835;
          }
        }
        localObject4 = ((UidState)localObject3).pkgOps.entrySet().iterator();
        while (((Iterator)localObject4).hasNext())
        {
          localObject2 = (Map.Entry)((Iterator)localObject4).next();
          localObject5 = (String)((Map.Entry)localObject2).getKey();
          if ((paramString == null) || (paramString.equals(localObject5)))
          {
            Ops localOps = (Ops)((Map.Entry)localObject2).getValue();
            m = localOps.size() - 1;
            k = paramInt;
            localObject2 = localObject1;
            if (m >= 0)
            {
              Op localOp = (Op)localOps.valueAt(m);
              localObject1 = localObject2;
              paramInt = k;
              if (!AppOpsManager.opAllowsReset(localOp.op)) {
                break label847;
              }
              localObject1 = localObject2;
              paramInt = k;
              if (localOp.mode == AppOpsManager.opToDefaultMode(localOp.op)) {
                break label847;
              }
              localOp.mode = AppOpsManager.opToDefaultMode(localOp.op);
              k = 1;
              localObject2 = addCallbacks(addCallbacks((HashMap)localObject2, localOp.op, localOp.uid, (String)localObject5, (ArrayList)this.mOpModeWatchers.get(localOp.op)), localOp.op, localOp.uid, (String)localObject5, (ArrayList)this.mPackageModeWatchers.get(localObject5));
              localObject1 = localObject2;
              paramInt = k;
              if (localOp.time != 0L) {
                break label847;
              }
              localObject1 = localObject2;
              paramInt = k;
              if (localOp.rejectTime != 0L) {
                break label847;
              }
              localOps.removeAt(m);
              localObject1 = localObject2;
              paramInt = k;
              break label847;
            }
            localObject1 = localObject2;
            paramInt = k;
            if (localOps.size() == 0)
            {
              ((Iterator)localObject4).remove();
              localObject1 = localObject2;
              paramInt = k;
            }
          }
        }
        localObject2 = localObject1;
      }
      finally {}
      k = paramInt;
      if (((UidState)localObject3).isDefault())
      {
        this.mUidStates.remove(((UidState)localObject3).uid);
        localObject2 = localObject1;
        k = paramInt;
        break label835;
        label685:
        if (paramInt != 0) {
          scheduleFastWriteLocked();
        }
        if (localObject2 != null)
        {
          paramString = ((HashMap)localObject2).entrySet().iterator();
          if (paramString.hasNext())
          {
            localObject2 = (Map.Entry)paramString.next();
            localObject1 = (Callback)((Map.Entry)localObject2).getKey();
            localObject2 = (ArrayList)((Map.Entry)localObject2).getValue();
            paramInt = 0;
          }
        }
        while (paramInt < ((ArrayList)localObject2).size())
        {
          localObject3 = (ChangeRec)((ArrayList)localObject2).get(paramInt);
          try
          {
            ((Callback)localObject1).mCallback.opChanged(((ChangeRec)localObject3).op, ((ChangeRec)localObject3).uid, ((ChangeRec)localObject3).pkg);
            paramInt += 1;
            continue;
            return;
          }
          catch (RemoteException localRemoteException2)
          {
            for (;;) {}
          }
        }
      }
    }
    catch (RemoteException localRemoteException1)
    {
      for (;;)
      {
        int m;
        i = paramInt;
        continue;
        label822:
        k -= 1;
        Object localObject2 = localRemoteException1;
        continue;
        label835:
        j -= 1;
        paramInt = k;
        continue;
        label847:
        m -= 1;
        localObject2 = localRemoteException1;
        int k = paramInt;
      }
    }
  }
  
  public void setAudioRestriction(int paramInt1, int paramInt2, int paramInt3, int paramInt4, String[] paramArrayOfString)
  {
    verifyIncomingUid(paramInt3);
    verifyIncomingOp(paramInt1);
    for (;;)
    {
      try
      {
        Object localObject2 = (SparseArray)this.mAudioRestrictions.get(paramInt1);
        Object localObject1 = localObject2;
        if (localObject2 == null)
        {
          localObject1 = new SparseArray();
          this.mAudioRestrictions.put(paramInt1, localObject1);
        }
        ((SparseArray)localObject1).remove(paramInt2);
        if (paramInt4 != 0)
        {
          localObject2 = new Restriction(null);
          ((Restriction)localObject2).mode = paramInt4;
          if (paramArrayOfString != null)
          {
            paramInt4 = paramArrayOfString.length;
            ((Restriction)localObject2).exceptionPackages = new ArraySet(paramInt4);
            paramInt3 = 0;
            if (paramInt3 < paramInt4)
            {
              String str = paramArrayOfString[paramInt3];
              if (str == null) {
                break label164;
              }
              ((Restriction)localObject2).exceptionPackages.add(str.trim());
              break label164;
            }
          }
          ((SparseArray)localObject1).put(paramInt2, localObject2);
        }
        else
        {
          notifyWatchersOfChange(paramInt1);
          return;
        }
      }
      finally {}
      label164:
      paramInt3 += 1;
    }
  }
  
  /* Error */
  public void setMode(int paramInt1, int paramInt2, String paramString, int paramInt3)
  {
    // Byte code:
    //   0: invokestatic 598	android/os/Binder:getCallingPid	()I
    //   3: invokestatic 811	android/os/Process:myPid	()I
    //   6: if_icmpeq +20 -> 26
    //   9: aload_0
    //   10: getfield 593	com/android/server/AppOpsService:mContext	Landroid/content/Context;
    //   13: ldc_w 595
    //   16: invokestatic 598	android/os/Binder:getCallingPid	()I
    //   19: invokestatic 221	android/os/Binder:getCallingUid	()I
    //   22: aconst_null
    //   23: invokevirtual 604	android/content/Context:enforcePermission	(Ljava/lang/String;IILjava/lang/String;)V
    //   26: aload_0
    //   27: iload_1
    //   28: invokespecial 840	com/android/server/AppOpsService:verifyIncomingOp	(I)V
    //   31: aconst_null
    //   32: astore 9
    //   34: iload_1
    //   35: invokestatic 504	android/app/AppOpsManager:opToSwitch	(I)I
    //   38: istore 5
    //   40: aload_0
    //   41: monitorenter
    //   42: aload_0
    //   43: iload_2
    //   44: iconst_0
    //   45: invokespecial 356	com/android/server/AppOpsService:getUidStateLocked	(IZ)Lcom/android/server/AppOpsService$UidState;
    //   48: pop
    //   49: aload_0
    //   50: iload 5
    //   52: iload_2
    //   53: aload_3
    //   54: iconst_1
    //   55: invokespecial 762	com/android/server/AppOpsService:getOpLocked	(IILjava/lang/String;Z)Lcom/android/server/AppOpsService$Op;
    //   58: astore 10
    //   60: aload 9
    //   62: astore 8
    //   64: aload 10
    //   66: ifnull +137 -> 203
    //   69: aload 9
    //   71: astore 8
    //   73: aload 10
    //   75: getfield 248	com/android/server/AppOpsService$Op:mode	I
    //   78: iload 4
    //   80: if_icmpeq +123 -> 203
    //   83: aload 10
    //   85: iload 4
    //   87: putfield 248	com/android/server/AppOpsService$Op:mode	I
    //   90: aload_0
    //   91: getfield 113	com/android/server/AppOpsService:mOpModeWatchers	Landroid/util/SparseArray;
    //   94: iload 5
    //   96: invokevirtual 202	android/util/SparseArray:get	(I)Ljava/lang/Object;
    //   99: checkcast 148	java/util/ArrayList
    //   102: astore 11
    //   104: aload 11
    //   106: ifnull +186 -> 292
    //   109: new 148	java/util/ArrayList
    //   112: dup
    //   113: invokespecial 160	java/util/ArrayList:<init>	()V
    //   116: astore 8
    //   118: aload 8
    //   120: astore 9
    //   122: aload 8
    //   124: aload 11
    //   126: invokevirtual 1050	java/util/ArrayList:addAll	(Ljava/util/Collection;)Z
    //   129: pop
    //   130: aload 8
    //   132: astore 9
    //   134: aload_0
    //   135: getfield 115	com/android/server/AppOpsService:mPackageModeWatchers	Landroid/util/ArrayMap;
    //   138: aload_3
    //   139: invokevirtual 360	android/util/ArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   142: checkcast 148	java/util/ArrayList
    //   145: astore 11
    //   147: aload 11
    //   149: ifnull +140 -> 289
    //   152: aload 8
    //   154: ifnonnull +132 -> 286
    //   157: aload 8
    //   159: astore 9
    //   161: new 148	java/util/ArrayList
    //   164: dup
    //   165: invokespecial 160	java/util/ArrayList:<init>	()V
    //   168: astore 8
    //   170: aload 8
    //   172: aload 11
    //   174: invokevirtual 1050	java/util/ArrayList:addAll	(Ljava/util/Collection;)Z
    //   177: pop
    //   178: iload 4
    //   180: aload 10
    //   182: getfield 247	com/android/server/AppOpsService$Op:op	I
    //   185: invokestatic 483	android/app/AppOpsManager:opToDefaultMode	(I)I
    //   188: if_icmpne +11 -> 199
    //   191: aload_0
    //   192: aload 10
    //   194: iload_2
    //   195: aload_3
    //   196: invokespecial 1271	com/android/server/AppOpsService:pruneOp	(Lcom/android/server/AppOpsService$Op;ILjava/lang/String;)V
    //   199: aload_0
    //   200: invokespecial 1066	com/android/server/AppOpsService:scheduleFastWriteLocked	()V
    //   203: aload_0
    //   204: monitorexit
    //   205: aload 8
    //   207: ifnull +61 -> 268
    //   210: invokestatic 364	android/os/Binder:clearCallingIdentity	()J
    //   213: lstore 6
    //   215: iconst_0
    //   216: istore_1
    //   217: aload 8
    //   219: invokevirtual 152	java/util/ArrayList:size	()I
    //   222: istore 4
    //   224: iload_1
    //   225: iload 4
    //   227: if_icmpge +36 -> 263
    //   230: aload 8
    //   232: iload_1
    //   233: invokevirtual 156	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   236: checkcast 15	com/android/server/AppOpsService$Callback
    //   239: getfield 536	com/android/server/AppOpsService$Callback:mCallback	Lcom/android/internal/app/IAppOpsCallback;
    //   242: iload 5
    //   244: iload_2
    //   245: aload_3
    //   246: invokeinterface 541 4 0
    //   251: iload_1
    //   252: iconst_1
    //   253: iadd
    //   254: istore_1
    //   255: goto -38 -> 217
    //   258: astore_3
    //   259: aload_0
    //   260: monitorexit
    //   261: aload_3
    //   262: athrow
    //   263: lload 6
    //   265: invokestatic 417	android/os/Binder:restoreCallingIdentity	(J)V
    //   268: return
    //   269: astore_3
    //   270: lload 6
    //   272: invokestatic 417	android/os/Binder:restoreCallingIdentity	(J)V
    //   275: aload_3
    //   276: athrow
    //   277: astore 9
    //   279: goto -28 -> 251
    //   282: astore_3
    //   283: goto -24 -> 259
    //   286: goto -116 -> 170
    //   289: goto -111 -> 178
    //   292: aconst_null
    //   293: astore 8
    //   295: goto -165 -> 130
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	298	0	this	AppOpsService
    //   0	298	1	paramInt1	int
    //   0	298	2	paramInt2	int
    //   0	298	3	paramString	String
    //   0	298	4	paramInt3	int
    //   38	205	5	i	int
    //   213	58	6	l	long
    //   62	232	8	localObject1	Object
    //   32	128	9	localObject2	Object
    //   277	1	9	localRemoteException	RemoteException
    //   58	135	10	localOp	Op
    //   102	71	11	localArrayList	ArrayList
    // Exception table:
    //   from	to	target	type
    //   42	60	258	finally
    //   73	104	258	finally
    //   109	118	258	finally
    //   170	178	258	finally
    //   178	199	258	finally
    //   199	203	258	finally
    //   217	224	269	finally
    //   230	251	269	finally
    //   230	251	277	android/os/RemoteException
    //   122	130	282	finally
    //   134	147	282	finally
    //   161	170	282	finally
  }
  
  public void setUidMode(int paramInt1, int paramInt2, int paramInt3)
  {
    if (Binder.getCallingPid() != Process.myPid()) {
      this.mContext.enforcePermission("android.permission.UPDATE_APP_OPS_STATS", Binder.getCallingPid(), Binder.getCallingUid(), null);
    }
    verifyIncomingOp(paramInt1);
    int i = AppOpsManager.opToSwitch(paramInt1);
    Object localObject9;
    Object localObject8;
    Object localObject7;
    Callback localCallback1;
    Object localObject10;
    for (;;)
    {
      Object localObject1;
      try
      {
        paramInt1 = AppOpsManager.opToDefaultMode(i);
        if ((isStrictOpEnable()) && (paramInt3 == 1)) {
          Op.-set0(getOpLocked(i, paramInt2, this.mContext.getPackageManager().getNameForUid(paramInt2), true), false);
        }
        localObject1 = getUidStateLocked(paramInt2, false);
        if (localObject1 == null)
        {
          if (paramInt3 == paramInt1) {
            return;
          }
          localObject1 = new UidState(paramInt2);
          ((UidState)localObject1).opModes = new SparseIntArray();
          ((UidState)localObject1).opModes.put(i, paramInt3);
          this.mUidStates.put(paramInt2, localObject1);
          scheduleWriteLocked();
          localObject9 = getPackagesForUid(paramInt2);
          localObject1 = null;
        }
      }
      finally {}
      try
      {
        localObject8 = (ArrayList)this.mOpModeWatchers.get(i);
        if (localObject8 != null)
        {
          paramInt3 = ((ArrayList)localObject8).size();
          paramInt1 = 0;
          localObject1 = null;
          if (paramInt1 < paramInt3) {
            localObject7 = localObject1;
          }
        }
      }
      finally {}
      try
      {
        localCallback1 = (Callback)((ArrayList)localObject8).get(paramInt1);
        localObject7 = localObject1;
        localObject10 = new ArraySet();
        localObject7 = localObject1;
        Collections.addAll((Collection)localObject10, (Object[])localObject9);
        localObject7 = localObject1;
        localObject1 = new ArrayMap();
        ((ArrayMap)localObject1).put(localCallback1, localObject10);
        paramInt1 += 1;
        continue;
      }
      finally
      {
        for (;;)
        {
          long l;
          continue;
          break;
          paramInt1 += 1;
        }
      }
      if (((UidState)localObject1).opModes != null) {
        break;
      }
      if (paramInt3 != paramInt1)
      {
        ((UidState)localObject1).opModes = new SparseIntArray();
        ((UidState)localObject1).opModes.put(i, paramInt3);
        scheduleWriteLocked();
      }
    }
    int j = ((UidState)localObject2).opModes.get(i);
    if (j == paramInt3) {
      return;
    }
    if (paramInt3 == paramInt1)
    {
      ((UidState)localObject2).opModes.delete(i);
      if (((UidState)localObject2).opModes.size() <= 0) {
        ((UidState)localObject2).opModes = null;
      }
    }
    for (;;)
    {
      scheduleWriteLocked();
      break;
      ((UidState)localObject2).opModes.put(i, paramInt3);
    }
    paramInt1 = 0;
    j = localObject9.length;
    Object localObject3;
    while (paramInt1 < j)
    {
      localCallback1 = localObject9[paramInt1];
      localObject7 = localObject2;
      localObject10 = (ArrayList)this.mPackageModeWatchers.get(localCallback1);
      if (localObject10 != null)
      {
        if (localObject2 != null) {
          break label711;
        }
        localObject7 = localObject2;
        localObject3 = new ArrayMap();
        int k = ((ArrayList)localObject10).size();
        paramInt3 = 0;
        for (;;)
        {
          localObject7 = localObject3;
          if (paramInt3 >= k) {
            break;
          }
          Callback localCallback2 = (Callback)((ArrayList)localObject10).get(paramInt3);
          localObject8 = (ArraySet)((ArrayMap)localObject3).get(localCallback2);
          localObject7 = localObject8;
          if (localObject8 == null)
          {
            localObject7 = new ArraySet();
            ((ArrayMap)localObject3).put(localCallback2, localObject7);
          }
          ((ArraySet)localObject7).add(localCallback1);
          paramInt3 += 1;
        }
      }
      localObject7 = localObject3;
      paramInt1 += 1;
      localObject3 = localObject7;
    }
    if (localObject3 == null)
    {
      return;
      throw ((Throwable)localObject4);
    }
    l = Binder.clearCallingIdentity();
    paramInt1 = 0;
    try
    {
      if (paramInt1 < ((ArrayMap)localObject4).size())
      {
        localObject7 = (Callback)((ArrayMap)localObject4).keyAt(paramInt1);
        localObject8 = (ArraySet)((ArrayMap)localObject4).valueAt(paramInt1);
        if (localObject8 == null) {}
        try
        {
          ((Callback)localObject7).mCallback.opChanged(i, paramInt2, null);
        }
        catch (RemoteException localRemoteException)
        {
          Log.w("AppOps", "Error dispatching op op change", localRemoteException);
        }
        j = ((ArraySet)localObject8).size();
        paramInt3 = 0;
        while (paramInt3 < j)
        {
          localObject9 = (String)((ArraySet)localObject8).valueAt(paramInt3);
          ((Callback)localObject7).mCallback.opChanged(i, paramInt2, (String)localObject9);
          paramInt3 += 1;
        }
      }
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public void setUserRestriction(int paramInt1, boolean paramBoolean, IBinder paramIBinder, int paramInt2, String[] paramArrayOfString)
  {
    if (Binder.getCallingPid() != Process.myPid()) {
      this.mContext.enforcePermission("android.permission.MANAGE_APP_OPS_RESTRICTIONS", Binder.getCallingPid(), Binder.getCallingUid(), null);
    }
    if ((paramInt2 != UserHandle.getCallingUserId()) && (this.mContext.checkCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL") != 0) && (this.mContext.checkCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS") != 0)) {
      throw new SecurityException("Need INTERACT_ACROSS_USERS_FULL or INTERACT_ACROSS_USERS to interact cross user ");
    }
    verifyIncomingOp(paramInt1);
    Preconditions.checkNotNull(paramIBinder);
    setUserRestrictionNoCheck(paramInt1, paramBoolean, paramIBinder, paramInt2, paramArrayOfString);
  }
  
  public void setUserRestrictions(Bundle paramBundle, IBinder paramIBinder, int paramInt)
  {
    checkSystemUid("setUserRestrictions");
    Preconditions.checkNotNull(paramBundle);
    Preconditions.checkNotNull(paramIBinder);
    int i = 0;
    while (i < 69)
    {
      String str = AppOpsManager.opToRestriction(i);
      if (str != null) {
        setUserRestrictionNoCheck(i, paramBundle.getBoolean(str, false), paramIBinder, paramInt, null);
      }
      i += 1;
    }
  }
  
  public void shutdown()
  {
    Slog.w("AppOps", "Writing app ops before shutdown...");
    int i = 0;
    try
    {
      if (this.mWriteScheduled)
      {
        this.mWriteScheduled = false;
        i = 1;
      }
      if (i != 0) {
        writeState();
      }
      return;
    }
    finally {}
  }
  
  public int startOperation(IBinder paramIBinder, int paramInt1, int paramInt2, String paramString)
  {
    verifyIncomingUid(paramInt2);
    verifyIncomingOp(paramInt1);
    Object localObject = resolvePackageName(paramInt2, paramString);
    if (localObject == null) {
      return 1;
    }
    ClientState localClientState = (ClientState)paramIBinder;
    try
    {
      paramIBinder = getOpsRawLocked(paramInt2, (String)localObject, true);
      if (paramIBinder == null) {
        return 2;
      }
      paramString = getOpLocked(paramIBinder, paramInt1, true);
      boolean bool = isOpRestrictedLocked(paramInt2, paramInt1, (String)localObject);
      if (bool) {
        return 1;
      }
      paramInt2 = AppOpsManager.opToSwitch(paramInt1);
      localObject = paramIBinder.uidState;
      if (((UidState)localObject).opModes != null)
      {
        int i = ((UidState)localObject).opModes.get(paramInt2);
        if (i != 0)
        {
          paramString.rejectTime = System.currentTimeMillis();
          return i;
        }
      }
      if (paramInt2 != paramInt1) {}
      for (paramIBinder = getOpLocked(paramIBinder, paramInt2, true); paramIBinder.mode != 0; paramIBinder = paramString)
      {
        paramString.rejectTime = System.currentTimeMillis();
        paramInt1 = paramIBinder.mode;
        return paramInt1;
      }
      if (paramString.nesting == 0)
      {
        paramString.time = System.currentTimeMillis();
        paramString.rejectTime = 0L;
        paramString.duration = -1;
      }
      paramString.nesting += 1;
      if (localClientState.mStartedOps != null) {
        localClientState.mStartedOps.add(paramString);
      }
      return 0;
    }
    finally {}
  }
  
  public void startWatchingMode(int paramInt, String paramString, IAppOpsCallback paramIAppOpsCallback)
  {
    if (paramIAppOpsCallback == null) {
      return;
    }
    int i = paramInt;
    if (paramInt != -1) {}
    try
    {
      i = AppOpsManager.opToSwitch(paramInt);
      Object localObject2 = (Callback)this.mModeWatchers.get(paramIAppOpsCallback.asBinder());
      Object localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject1 = new Callback(paramIAppOpsCallback);
        this.mModeWatchers.put(paramIAppOpsCallback.asBinder(), localObject1);
      }
      if (i != -1)
      {
        localObject2 = (ArrayList)this.mOpModeWatchers.get(i);
        paramIAppOpsCallback = (IAppOpsCallback)localObject2;
        if (localObject2 == null)
        {
          paramIAppOpsCallback = new ArrayList();
          this.mOpModeWatchers.put(i, paramIAppOpsCallback);
        }
        paramIAppOpsCallback.add(localObject1);
      }
      if (paramString != null)
      {
        localObject2 = (ArrayList)this.mPackageModeWatchers.get(paramString);
        paramIAppOpsCallback = (IAppOpsCallback)localObject2;
        if (localObject2 == null)
        {
          paramIAppOpsCallback = new ArrayList();
          this.mPackageModeWatchers.put(paramString, paramIAppOpsCallback);
        }
        paramIAppOpsCallback.add(localObject1);
      }
      return;
    }
    finally {}
  }
  
  public void stopWatchingMode(IAppOpsCallback paramIAppOpsCallback)
  {
    if (paramIAppOpsCallback == null) {
      return;
    }
    for (;;)
    {
      int i;
      try
      {
        paramIAppOpsCallback = (Callback)this.mModeWatchers.remove(paramIAppOpsCallback.asBinder());
        if (paramIAppOpsCallback != null)
        {
          paramIAppOpsCallback.unlinkToDeath();
          i = this.mOpModeWatchers.size() - 1;
          ArrayList localArrayList;
          if (i >= 0)
          {
            localArrayList = (ArrayList)this.mOpModeWatchers.valueAt(i);
            localArrayList.remove(paramIAppOpsCallback);
            if (localArrayList.size() > 0) {
              break label145;
            }
            this.mOpModeWatchers.removeAt(i);
            break label145;
          }
          i = this.mPackageModeWatchers.size() - 1;
          if (i >= 0)
          {
            localArrayList = (ArrayList)this.mPackageModeWatchers.valueAt(i);
            localArrayList.remove(paramIAppOpsCallback);
            if (localArrayList.size() <= 0) {
              this.mPackageModeWatchers.removeAt(i);
            }
            i -= 1;
            continue;
          }
        }
        return;
      }
      finally {}
      label145:
      i -= 1;
    }
  }
  
  public void systemReady()
  {
    int j;
    for (int i = 0;; i = j)
    {
      int k;
      UidState localUidState;
      for (;;)
      {
        Object localObject2;
        Ops localOps;
        int m;
        try
        {
          k = this.mUidStates.size() - 1;
          if (k >= 0)
          {
            localUidState = (UidState)this.mUidStates.valueAt(k);
            if (ArrayUtils.isEmpty(getPackagesForUid(localUidState.uid)))
            {
              localUidState.clear();
              this.mUidStates.removeAt(k);
              j = 1;
              break label289;
            }
            localObject2 = localUidState.pkgOps;
            j = i;
            if (localObject2 == null) {
              break label289;
            }
            localObject2 = ((ArrayMap)localObject2).values().iterator();
            if (((Iterator)localObject2).hasNext())
            {
              localOps = (Ops)((Iterator)localObject2).next();
              j = -1;
            }
          }
        }
        finally {}
        try
        {
          m = AppGlobals.getPackageManager().getPackageUid(localOps.packageName, 8192, UserHandle.getUserId(localOps.uidState.uid));
          j = m;
        }
        catch (RemoteException localRemoteException)
        {
          continue;
        }
        if (j != localOps.uidState.uid)
        {
          Slog.i("AppOps", "Pruning old package " + localOps.packageName + "/" + localOps.uidState + ": new uid=" + j);
          ((Iterator)localObject2).remove();
          i = 1;
        }
      }
      j = i;
      if (localUidState.isDefault())
      {
        this.mUidStates.removeAt(k);
        j = i;
        break label289;
        if (i != 0) {
          scheduleFastWriteLocked();
        }
        ((MountServiceInternal)LocalServices.getService(MountServiceInternal.class)).addExternalStoragePolicy(new MountServiceInternal.ExternalStorageMountPolicy()
        {
          public int getMountMode(int paramAnonymousInt, String paramAnonymousString)
          {
            if (Process.isIsolated(paramAnonymousInt)) {
              return 0;
            }
            if (AppOpsService.this.noteOperation(59, paramAnonymousInt, paramAnonymousString) != 0) {
              return 0;
            }
            if (AppOpsService.this.noteOperation(60, paramAnonymousInt, paramAnonymousString) != 0) {
              return 2;
            }
            return 3;
          }
          
          public boolean hasExternalStorage(int paramAnonymousInt, String paramAnonymousString)
          {
            paramAnonymousInt = getMountMode(paramAnonymousInt, paramAnonymousString);
            return (paramAnonymousInt == 2) || (paramAnonymousInt == 3);
          }
        });
        return;
      }
      label289:
      k -= 1;
    }
  }
  
  public void uidRemoved(int paramInt)
  {
    try
    {
      if (this.mUidStates.indexOfKey(paramInt) >= 0)
      {
        this.mUidStates.remove(paramInt);
        scheduleFastWriteLocked();
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  /* Error */
  void writeState()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 128	com/android/server/AppOpsService:mFile	Landroid/util/AtomicFile;
    //   4: astore 11
    //   6: aload 11
    //   8: monitorenter
    //   9: aload_0
    //   10: aconst_null
    //   11: invokevirtual 1358	com/android/server/AppOpsService:getPackagesForOps	([I)Ljava/util/List;
    //   14: astore 13
    //   16: aload_0
    //   17: getfield 128	com/android/server/AppOpsService:mFile	Landroid/util/AtomicFile;
    //   20: invokevirtual 1362	android/util/AtomicFile:startWrite	()Ljava/io/FileOutputStream;
    //   23: astore 12
    //   25: new 1364	com/android/internal/util/FastXmlSerializer
    //   28: dup
    //   29: invokespecial 1365	com/android/internal/util/FastXmlSerializer:<init>	()V
    //   32: astore 14
    //   34: aload 14
    //   36: aload 12
    //   38: getstatic 1145	java/nio/charset/StandardCharsets:UTF_8	Ljava/nio/charset/Charset;
    //   41: invokevirtual 1150	java/nio/charset/Charset:name	()Ljava/lang/String;
    //   44: invokeinterface 1371 3 0
    //   49: aload 14
    //   51: aconst_null
    //   52: iconst_1
    //   53: invokestatic 1375	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   56: invokeinterface 1379 3 0
    //   61: aload 14
    //   63: aconst_null
    //   64: ldc_w 1381
    //   67: invokeinterface 1385 3 0
    //   72: pop
    //   73: aload_0
    //   74: getfield 108	com/android/server/AppOpsService:mUidStates	Landroid/util/SparseArray;
    //   77: invokevirtual 880	android/util/SparseArray:size	()I
    //   80: istore_3
    //   81: iconst_0
    //   82: istore_1
    //   83: iload_1
    //   84: iload_3
    //   85: if_icmpge +786 -> 871
    //   88: aload_0
    //   89: getfield 108	com/android/server/AppOpsService:mUidStates	Landroid/util/SparseArray;
    //   92: iload_1
    //   93: invokevirtual 890	android/util/SparseArray:valueAt	(I)Ljava/lang/Object;
    //   96: checkcast 42	com/android/server/AppOpsService$UidState
    //   99: astore 9
    //   101: aload 9
    //   103: getfield 513	com/android/server/AppOpsService$UidState:opModes	Landroid/util/SparseIntArray;
    //   106: ifnull +758 -> 864
    //   109: aload 9
    //   111: getfield 513	com/android/server/AppOpsService$UidState:opModes	Landroid/util/SparseIntArray;
    //   114: invokevirtual 821	android/util/SparseIntArray:size	()I
    //   117: ifle +747 -> 864
    //   120: aload 14
    //   122: aconst_null
    //   123: ldc_w 1108
    //   126: invokeinterface 1385 3 0
    //   131: pop
    //   132: aload 14
    //   134: aconst_null
    //   135: ldc_w 1093
    //   138: aload 9
    //   140: getfield 338	com/android/server/AppOpsService$UidState:uid	I
    //   143: invokestatic 1387	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   146: invokeinterface 1391 4 0
    //   151: pop
    //   152: aload 9
    //   154: getfield 513	com/android/server/AppOpsService$UidState:opModes	Landroid/util/SparseIntArray;
    //   157: astore 9
    //   159: aload 9
    //   161: invokevirtual 821	android/util/SparseIntArray:size	()I
    //   164: istore 4
    //   166: iconst_0
    //   167: istore_2
    //   168: iload_2
    //   169: iload 4
    //   171: if_icmpge +117 -> 288
    //   174: aload 9
    //   176: iload_2
    //   177: invokevirtual 954	android/util/SparseIntArray:keyAt	(I)I
    //   180: istore 5
    //   182: aload 9
    //   184: iload_2
    //   185: invokevirtual 956	android/util/SparseIntArray:valueAt	(I)I
    //   188: istore 6
    //   190: aload 14
    //   192: aconst_null
    //   193: ldc_w 1188
    //   196: invokeinterface 1385 3 0
    //   201: pop
    //   202: aload 14
    //   204: aconst_null
    //   205: ldc_w 1093
    //   208: iload 5
    //   210: invokestatic 1387	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   213: invokeinterface 1391 4 0
    //   218: pop
    //   219: aload 14
    //   221: aconst_null
    //   222: ldc_w 1190
    //   225: iload 6
    //   227: invokestatic 1387	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   230: invokeinterface 1391 4 0
    //   235: pop
    //   236: aload 14
    //   238: aconst_null
    //   239: ldc_w 1188
    //   242: invokeinterface 1394 3 0
    //   247: pop
    //   248: iload_2
    //   249: iconst_1
    //   250: iadd
    //   251: istore_2
    //   252: goto -84 -> 168
    //   255: astore 9
    //   257: ldc 50
    //   259: new 225	java/lang/StringBuilder
    //   262: dup
    //   263: invokespecial 226	java/lang/StringBuilder:<init>	()V
    //   266: ldc_w 1396
    //   269: invokevirtual 230	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   272: aload 9
    //   274: invokevirtual 705	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   277: invokevirtual 236	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   280: invokestatic 501	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   283: pop
    //   284: aload 11
    //   286: monitorexit
    //   287: return
    //   288: aload 14
    //   290: aconst_null
    //   291: ldc_w 1108
    //   294: invokeinterface 1394 3 0
    //   299: pop
    //   300: goto +564 -> 864
    //   303: iload_1
    //   304: aload 13
    //   306: invokeinterface 645 1 0
    //   311: if_icmpge +497 -> 808
    //   314: aload 13
    //   316: iload_1
    //   317: invokeinterface 648 2 0
    //   322: checkcast 650	android/app/AppOpsManager$PackageOps
    //   325: astore 15
    //   327: aload 10
    //   329: astore 9
    //   331: aload 15
    //   333: invokevirtual 721	android/app/AppOpsManager$PackageOps:getPackageName	()Ljava/lang/String;
    //   336: aload 10
    //   338: invokevirtual 183	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   341: ifne +53 -> 394
    //   344: aload 10
    //   346: ifnull +15 -> 361
    //   349: aload 14
    //   351: aconst_null
    //   352: ldc_w 1173
    //   355: invokeinterface 1394 3 0
    //   360: pop
    //   361: aload 15
    //   363: invokevirtual 721	android/app/AppOpsManager$PackageOps:getPackageName	()Ljava/lang/String;
    //   366: astore 9
    //   368: aload 14
    //   370: aconst_null
    //   371: ldc_w 1173
    //   374: invokeinterface 1385 3 0
    //   379: pop
    //   380: aload 14
    //   382: aconst_null
    //   383: ldc_w 1093
    //   386: aload 9
    //   388: invokeinterface 1391 4 0
    //   393: pop
    //   394: aload 14
    //   396: aconst_null
    //   397: ldc_w 1108
    //   400: invokeinterface 1385 3 0
    //   405: pop
    //   406: aload 14
    //   408: aconst_null
    //   409: ldc_w 1093
    //   412: aload 15
    //   414: invokevirtual 1399	android/app/AppOpsManager$PackageOps:getUid	()I
    //   417: invokestatic 1387	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   420: invokeinterface 1391 4 0
    //   425: pop
    //   426: aload_0
    //   427: monitorenter
    //   428: aload_0
    //   429: aload 15
    //   431: invokevirtual 1399	android/app/AppOpsManager$PackageOps:getUid	()I
    //   434: aload 15
    //   436: invokevirtual 721	android/app/AppOpsManager$PackageOps:getPackageName	()Ljava/lang/String;
    //   439: iconst_0
    //   440: invokespecial 328	com/android/server/AppOpsService:getOpsRawLocked	(ILjava/lang/String;Z)Lcom/android/server/AppOpsService$Ops;
    //   443: astore 10
    //   445: aload 10
    //   447: ifnull +286 -> 733
    //   450: aload 14
    //   452: aconst_null
    //   453: ldc_w 1187
    //   456: aload 10
    //   458: getfield 462	com/android/server/AppOpsService$Ops:isPrivileged	Z
    //   461: invokestatic 1402	java/lang/Boolean:toString	(Z)Ljava/lang/String;
    //   464: invokeinterface 1391 4 0
    //   469: pop
    //   470: aload_0
    //   471: monitorexit
    //   472: aload 15
    //   474: invokevirtual 654	android/app/AppOpsManager$PackageOps:getOps	()Ljava/util/List;
    //   477: astore 10
    //   479: iconst_0
    //   480: istore_2
    //   481: iload_2
    //   482: aload 10
    //   484: invokeinterface 645 1 0
    //   489: if_icmpge +296 -> 785
    //   492: aload 10
    //   494: iload_2
    //   495: invokeinterface 648 2 0
    //   500: checkcast 246	android/app/AppOpsManager$OpEntry
    //   503: astore 15
    //   505: aload 14
    //   507: aconst_null
    //   508: ldc_w 1188
    //   511: invokeinterface 1385 3 0
    //   516: pop
    //   517: aload 14
    //   519: aconst_null
    //   520: ldc_w 1093
    //   523: aload 15
    //   525: invokevirtual 657	android/app/AppOpsManager$OpEntry:getOp	()I
    //   528: invokestatic 1387	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   531: invokeinterface 1391 4 0
    //   536: pop
    //   537: aload 15
    //   539: invokevirtual 669	android/app/AppOpsManager$OpEntry:getMode	()I
    //   542: aload 15
    //   544: invokevirtual 657	android/app/AppOpsManager$OpEntry:getOp	()I
    //   547: invokestatic 483	android/app/AppOpsManager:opToDefaultMode	(I)I
    //   550: if_icmpeq +23 -> 573
    //   553: aload 14
    //   555: aconst_null
    //   556: ldc_w 1190
    //   559: aload 15
    //   561: invokevirtual 669	android/app/AppOpsManager$OpEntry:getMode	()I
    //   564: invokestatic 1387	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   567: invokeinterface 1391 4 0
    //   572: pop
    //   573: aload 15
    //   575: invokevirtual 676	android/app/AppOpsManager$OpEntry:getTime	()J
    //   578: lstore 7
    //   580: lload 7
    //   582: lconst_0
    //   583: lcmp
    //   584: ifeq +20 -> 604
    //   587: aload 14
    //   589: aconst_null
    //   590: ldc_w 1192
    //   593: lload 7
    //   595: invokestatic 1405	java/lang/Long:toString	(J)Ljava/lang/String;
    //   598: invokeinterface 1391 4 0
    //   603: pop
    //   604: aload 15
    //   606: invokevirtual 689	android/app/AppOpsManager$OpEntry:getRejectTime	()J
    //   609: lstore 7
    //   611: lload 7
    //   613: lconst_0
    //   614: lcmp
    //   615: ifeq +20 -> 635
    //   618: aload 14
    //   620: aconst_null
    //   621: ldc_w 1200
    //   624: lload 7
    //   626: invokestatic 1405	java/lang/Long:toString	(J)Ljava/lang/String;
    //   629: invokeinterface 1391 4 0
    //   634: pop
    //   635: aload 15
    //   637: invokevirtual 694	android/app/AppOpsManager$OpEntry:getDuration	()I
    //   640: istore_3
    //   641: iload_3
    //   642: ifeq +19 -> 661
    //   645: aload 14
    //   647: aconst_null
    //   648: ldc_w 1202
    //   651: iload_3
    //   652: invokestatic 1387	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   655: invokeinterface 1391 4 0
    //   660: pop
    //   661: aload 15
    //   663: invokevirtual 1408	android/app/AppOpsManager$OpEntry:getProxyUid	()I
    //   666: istore_3
    //   667: iload_3
    //   668: iconst_m1
    //   669: if_icmpeq +19 -> 688
    //   672: aload 14
    //   674: aconst_null
    //   675: ldc_w 1204
    //   678: iload_3
    //   679: invokestatic 1387	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   682: invokeinterface 1391 4 0
    //   687: pop
    //   688: aload 15
    //   690: invokevirtual 1411	android/app/AppOpsManager$OpEntry:getProxyPackageName	()Ljava/lang/String;
    //   693: astore 15
    //   695: aload 15
    //   697: ifnull +17 -> 714
    //   700: aload 14
    //   702: aconst_null
    //   703: ldc_w 1206
    //   706: aload 15
    //   708: invokeinterface 1391 4 0
    //   713: pop
    //   714: aload 14
    //   716: aconst_null
    //   717: ldc_w 1188
    //   720: invokeinterface 1394 3 0
    //   725: pop
    //   726: iload_2
    //   727: iconst_1
    //   728: iadd
    //   729: istore_2
    //   730: goto -249 -> 481
    //   733: aload 14
    //   735: aconst_null
    //   736: ldc_w 1187
    //   739: iconst_0
    //   740: invokestatic 1402	java/lang/Boolean:toString	(Z)Ljava/lang/String;
    //   743: invokeinterface 1391 4 0
    //   748: pop
    //   749: goto -279 -> 470
    //   752: astore 9
    //   754: aload_0
    //   755: monitorexit
    //   756: aload 9
    //   758: athrow
    //   759: astore 9
    //   761: ldc 50
    //   763: ldc_w 1413
    //   766: aload 9
    //   768: invokestatic 413	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   771: pop
    //   772: aload_0
    //   773: getfield 128	com/android/server/AppOpsService:mFile	Landroid/util/AtomicFile;
    //   776: aload 12
    //   778: invokevirtual 1417	android/util/AtomicFile:failWrite	(Ljava/io/FileOutputStream;)V
    //   781: aload 11
    //   783: monitorexit
    //   784: return
    //   785: aload 14
    //   787: aconst_null
    //   788: ldc_w 1108
    //   791: invokeinterface 1394 3 0
    //   796: pop
    //   797: iload_1
    //   798: iconst_1
    //   799: iadd
    //   800: istore_1
    //   801: aload 9
    //   803: astore 10
    //   805: goto -502 -> 303
    //   808: aload 10
    //   810: ifnull +15 -> 825
    //   813: aload 14
    //   815: aconst_null
    //   816: ldc_w 1173
    //   819: invokeinterface 1394 3 0
    //   824: pop
    //   825: aload 14
    //   827: aconst_null
    //   828: ldc_w 1381
    //   831: invokeinterface 1394 3 0
    //   836: pop
    //   837: aload 14
    //   839: invokeinterface 1420 1 0
    //   844: aload_0
    //   845: getfield 128	com/android/server/AppOpsService:mFile	Landroid/util/AtomicFile;
    //   848: aload 12
    //   850: invokevirtual 1423	android/util/AtomicFile:finishWrite	(Ljava/io/FileOutputStream;)V
    //   853: goto -72 -> 781
    //   856: astore 9
    //   858: aload 11
    //   860: monitorexit
    //   861: aload 9
    //   863: athrow
    //   864: iload_1
    //   865: iconst_1
    //   866: iadd
    //   867: istore_1
    //   868: goto -785 -> 83
    //   871: aload 13
    //   873: ifnull -48 -> 825
    //   876: aconst_null
    //   877: astore 10
    //   879: iconst_0
    //   880: istore_1
    //   881: goto -578 -> 303
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	884	0	this	AppOpsService
    //   82	799	1	i	int
    //   167	563	2	j	int
    //   80	599	3	k	int
    //   164	8	4	m	int
    //   180	29	5	n	int
    //   188	38	6	i1	int
    //   578	47	7	l	long
    //   99	84	9	localObject1	Object
    //   255	18	9	localIOException1	IOException
    //   329	58	9	localObject2	Object
    //   752	5	9	localObject3	Object
    //   759	43	9	localIOException2	IOException
    //   856	6	9	localObject4	Object
    //   327	551	10	localObject5	Object
    //   4	855	11	localAtomicFile	AtomicFile
    //   23	826	12	localFileOutputStream	java.io.FileOutputStream
    //   14	858	13	localList	List
    //   32	806	14	localFastXmlSerializer	com.android.internal.util.FastXmlSerializer
    //   325	382	15	localObject6	Object
    // Exception table:
    //   from	to	target	type
    //   16	25	255	java/io/IOException
    //   428	445	752	finally
    //   450	470	752	finally
    //   733	749	752	finally
    //   25	81	759	java/io/IOException
    //   88	166	759	java/io/IOException
    //   174	248	759	java/io/IOException
    //   288	300	759	java/io/IOException
    //   303	327	759	java/io/IOException
    //   331	344	759	java/io/IOException
    //   349	361	759	java/io/IOException
    //   361	394	759	java/io/IOException
    //   394	428	759	java/io/IOException
    //   470	479	759	java/io/IOException
    //   481	573	759	java/io/IOException
    //   573	580	759	java/io/IOException
    //   587	604	759	java/io/IOException
    //   604	611	759	java/io/IOException
    //   618	635	759	java/io/IOException
    //   635	641	759	java/io/IOException
    //   645	661	759	java/io/IOException
    //   661	667	759	java/io/IOException
    //   672	688	759	java/io/IOException
    //   688	695	759	java/io/IOException
    //   700	714	759	java/io/IOException
    //   714	726	759	java/io/IOException
    //   754	759	759	java/io/IOException
    //   785	797	759	java/io/IOException
    //   813	825	759	java/io/IOException
    //   825	853	759	java/io/IOException
    //   9	16	856	finally
    //   16	25	856	finally
    //   25	81	856	finally
    //   88	166	856	finally
    //   174	248	856	finally
    //   257	284	856	finally
    //   288	300	856	finally
    //   303	327	856	finally
    //   331	344	856	finally
    //   349	361	856	finally
    //   361	394	856	finally
    //   394	428	856	finally
    //   470	479	856	finally
    //   481	573	856	finally
    //   573	580	856	finally
    //   587	604	856	finally
    //   604	611	856	finally
    //   618	635	856	finally
    //   635	641	856	finally
    //   645	661	856	finally
    //   661	667	856	finally
    //   672	688	856	finally
    //   688	695	856	finally
    //   700	714	856	finally
    //   714	726	856	finally
    //   754	759	856	finally
    //   761	781	856	finally
    //   785	797	856	finally
    //   813	825	856	finally
    //   825	853	856	finally
  }
  
  final class AskRunnable
    implements Runnable
  {
    final int code;
    final AppOpsService.Op op;
    final String packageName;
    final PermissionDialogReqQueue.PermissionDialogReq request;
    final int uid;
    
    public AskRunnable(int paramInt1, int paramInt2, String paramString, AppOpsService.Op paramOp, PermissionDialogReqQueue.PermissionDialogReq paramPermissionDialogReq)
    {
      this.code = paramInt1;
      this.uid = paramInt2;
      this.packageName = paramString;
      this.op = paramOp;
      this.request = paramPermissionDialogReq;
    }
    
    public void run()
    {
      PermissionDialog localPermissionDialog;
      synchronized (AppOpsService.this)
      {
        this.op.dialogReqQueue.register(this.request);
        if (this.op.dialogReqQueue.getDialog() == null) {
          localPermissionDialog = new PermissionDialog(AppOpsService.this.mContext, AppOpsService.this, this.code, this.uid, this.packageName);
        }
      }
      for (;;)
      {
        try
        {
          this.op.dialogReqQueue.setDialog(localPermissionDialog);
          if (localPermissionDialog != null) {
            localPermissionDialog.show();
          }
          return;
        }
        finally {}
        localObject1 = finally;
        throw ((Throwable)localObject1);
        Object localObject3 = null;
      }
    }
  }
  
  public final class Callback
    implements IBinder.DeathRecipient
  {
    final IAppOpsCallback mCallback;
    
    public Callback(IAppOpsCallback paramIAppOpsCallback)
    {
      this.mCallback = paramIAppOpsCallback;
      try
      {
        this.mCallback.asBinder().linkToDeath(this, 0);
        return;
      }
      catch (RemoteException this$1) {}
    }
    
    public void binderDied()
    {
      AppOpsService.this.stopWatchingMode(this.mCallback);
    }
    
    public void unlinkToDeath()
    {
      this.mCallback.asBinder().unlinkToDeath(this, 0);
    }
  }
  
  static final class ChangeRec
  {
    final int op;
    final String pkg;
    final int uid;
    
    ChangeRec(int paramInt1, int paramInt2, String paramString)
    {
      this.op = paramInt1;
      this.uid = paramInt2;
      this.pkg = paramString;
    }
  }
  
  private final class ClientRestrictionState
    implements IBinder.DeathRecipient
  {
    SparseArray<String[]> perUserExcludedPackages;
    SparseArray<boolean[]> perUserRestrictions;
    private final IBinder token;
    
    public ClientRestrictionState(IBinder paramIBinder)
      throws RemoteException
    {
      paramIBinder.linkToDeath(this, 0);
      this.token = paramIBinder;
    }
    
    private boolean isDefault(boolean[] paramArrayOfBoolean)
    {
      if (ArrayUtils.isEmpty(paramArrayOfBoolean)) {
        return true;
      }
      int j = paramArrayOfBoolean.length;
      int i = 0;
      while (i < j)
      {
        if (paramArrayOfBoolean[i] != 0) {
          return false;
        }
        i += 1;
      }
      return true;
    }
    
    public void binderDied()
    {
      for (;;)
      {
        int i;
        int j;
        synchronized (AppOpsService.this)
        {
          AppOpsService.-get0(AppOpsService.this).remove(this.token);
          Object localObject1 = this.perUserRestrictions;
          if (localObject1 == null) {
            return;
          }
          int k = this.perUserRestrictions.size();
          i = 0;
          if (i < k)
          {
            localObject1 = (boolean[])this.perUserRestrictions.valueAt(i);
            int m = localObject1.length;
            j = 0;
            if (j >= m) {
              break label133;
            }
            if (localObject1[j] != 0) {
              AppOpsService.this.mHandler.post(new -void_binderDied__LambdaImpl0(j));
            }
          }
          else
          {
            destroy();
            return;
          }
        }
        j += 1;
        continue;
        label133:
        i += 1;
      }
    }
    
    public void destroy()
    {
      this.token.unlinkToDeath(this, 0);
    }
    
    public boolean hasRestriction(int paramInt1, String paramString, int paramInt2)
    {
      if (this.perUserRestrictions == null) {
        return false;
      }
      Object localObject = (boolean[])this.perUserRestrictions.get(paramInt2);
      if (localObject == null) {
        return false;
      }
      if (localObject[paramInt1] == 0) {
        return false;
      }
      if (this.perUserExcludedPackages == null) {
        return true;
      }
      localObject = (String[])this.perUserExcludedPackages.get(paramInt2);
      if (localObject == null) {
        return true;
      }
      return !ArrayUtils.contains((Object[])localObject, paramString);
    }
    
    public boolean isDefault()
    {
      return (this.perUserRestrictions == null) || (this.perUserRestrictions.size() <= 0);
    }
    
    public void removeUser(int paramInt)
    {
      if (this.perUserExcludedPackages != null)
      {
        this.perUserExcludedPackages.remove(paramInt);
        if (this.perUserExcludedPackages.size() <= 0) {
          this.perUserExcludedPackages = null;
        }
      }
    }
    
    public boolean setRestriction(int paramInt1, boolean paramBoolean, String[] paramArrayOfString, int paramInt2)
    {
      boolean bool2 = false;
      boolean bool3 = false;
      if ((this.perUserRestrictions == null) && (paramBoolean)) {
        this.perUserRestrictions = new SparseArray();
      }
      boolean bool1;
      if (this.perUserRestrictions != null)
      {
        Object localObject2 = (boolean[])this.perUserRestrictions.get(paramInt2);
        Object localObject1 = localObject2;
        if (localObject2 == null)
        {
          localObject1 = localObject2;
          if (paramBoolean)
          {
            localObject1 = new boolean[69];
            this.perUserRestrictions.put(paramInt2, localObject1);
          }
        }
        bool1 = bool3;
        localObject2 = localObject1;
        if (localObject1 != null)
        {
          bool1 = bool3;
          localObject2 = localObject1;
          if (localObject1[paramInt1] != paramBoolean)
          {
            localObject1[paramInt1] = paramBoolean;
            localObject2 = localObject1;
            if (!paramBoolean)
            {
              localObject2 = localObject1;
              if (isDefault((boolean[])localObject1))
              {
                this.perUserRestrictions.remove(paramInt2);
                localObject2 = null;
              }
            }
            bool1 = true;
          }
        }
        bool2 = bool1;
        if (localObject2 != null)
        {
          paramBoolean = ArrayUtils.isEmpty(paramArrayOfString);
          if ((this.perUserExcludedPackages == null) && (!paramBoolean)) {
            break label215;
          }
        }
      }
      for (;;)
      {
        bool2 = bool1;
        if (this.perUserExcludedPackages != null)
        {
          if (!Arrays.equals(paramArrayOfString, (Object[])this.perUserExcludedPackages.get(paramInt2))) {
            break;
          }
          bool2 = bool1;
        }
        return bool2;
        label215:
        this.perUserExcludedPackages = new SparseArray();
      }
      if (paramBoolean)
      {
        this.perUserExcludedPackages.remove(paramInt2);
        if (this.perUserExcludedPackages.size() <= 0) {
          this.perUserExcludedPackages = null;
        }
      }
      for (;;)
      {
        return true;
        this.perUserExcludedPackages.put(paramInt2, paramArrayOfString);
      }
    }
  }
  
  public final class ClientState
    extends Binder
    implements IBinder.DeathRecipient
  {
    final IBinder mAppToken;
    final int mPid;
    final ArrayList<AppOpsService.Op> mStartedOps;
    
    public ClientState(IBinder paramIBinder)
    {
      this.mAppToken = paramIBinder;
      this.mPid = Binder.getCallingPid();
      if ((paramIBinder instanceof Binder))
      {
        this.mStartedOps = null;
        return;
      }
      this.mStartedOps = new ArrayList();
      try
      {
        this.mAppToken.linkToDeath(this, 0);
        return;
      }
      catch (RemoteException this$1) {}
    }
    
    public void binderDied()
    {
      synchronized (AppOpsService.this)
      {
        int i = this.mStartedOps.size() - 1;
        while (i >= 0)
        {
          AppOpsService.this.finishOperationLocked((AppOpsService.Op)this.mStartedOps.get(i));
          i -= 1;
        }
        AppOpsService.this.mClients.remove(this.mAppToken);
        return;
      }
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder().append("ClientState{mAppToken=").append(this.mAppToken).append(", ");
      if (this.mStartedOps != null) {}
      for (String str = "pid=" + this.mPid;; str = "local") {
        return str + '}';
      }
    }
  }
  
  public static final class Op
  {
    public PermissionDialogReqQueue dialogReqQueue;
    public int duration;
    public int mode;
    public int nesting;
    public final int op;
    public final String packageName;
    public String proxyPackageName;
    public int proxyUid = -1;
    public long rejectTime;
    private boolean remember;
    public long time;
    public final int uid;
    
    public Op(int paramInt1, String paramString, int paramInt2)
    {
      this.uid = paramInt1;
      this.packageName = paramString;
      this.op = paramInt2;
      this.mode = AppOpsManager.opToDefaultMode(this.op);
      this.dialogReqQueue = new PermissionDialogReqQueue();
      this.remember = false;
    }
  }
  
  public static final class Ops
    extends SparseArray<AppOpsService.Op>
  {
    public final boolean isPrivileged;
    public final String packageName;
    public final AppOpsService.UidState uidState;
    
    public Ops(String paramString, AppOpsService.UidState paramUidState, boolean paramBoolean)
    {
      this.packageName = paramString;
      this.uidState = paramUidState;
      this.isPrivileged = paramBoolean;
    }
  }
  
  private static final class Restriction
  {
    private static final ArraySet<String> NO_EXCEPTIONS = new ArraySet();
    ArraySet<String> exceptionPackages = NO_EXCEPTIONS;
    int mode;
  }
  
  static class Shell
    extends ShellCommand
  {
    final IAppOpsService mInterface;
    final AppOpsService mInternal;
    int mode;
    String modeStr;
    int op;
    String opStr;
    String packageName;
    int packageUid;
    int userId = 0;
    
    Shell(IAppOpsService paramIAppOpsService, AppOpsService paramAppOpsService)
    {
      this.mInterface = paramIAppOpsService;
      this.mInternal = paramAppOpsService;
    }
    
    private int strOpToOp(String paramString, PrintWriter paramPrintWriter)
    {
      try
      {
        i = AppOpsManager.strOpToOp(paramString);
        return i;
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        try
        {
          i = Integer.parseInt(paramString);
          return i;
        }
        catch (NumberFormatException localNumberFormatException)
        {
          try
          {
            int i = AppOpsManager.strDebugOpToOp(paramString);
            return i;
          }
          catch (IllegalArgumentException paramString)
          {
            paramPrintWriter.println("Error: " + paramString.getMessage());
          }
        }
      }
      return -1;
    }
    
    public int onCommand(String paramString)
    {
      return AppOpsService.onShellCommand(this, paramString);
    }
    
    public void onHelp()
    {
      AppOpsService.dumpCommandHelp(getOutPrintWriter());
    }
    
    int parseUserOpMode(int paramInt, PrintWriter paramPrintWriter)
      throws RemoteException
    {
      this.userId = -2;
      this.opStr = null;
      this.modeStr = null;
      String str;
      do
      {
        for (;;)
        {
          str = getNextArg();
          if (str == null) {
            break label75;
          }
          if ("--user".equals(str))
          {
            this.userId = UserHandle.parseUserArg(getNextArgRequired());
          }
          else
          {
            if (this.opStr != null) {
              break;
            }
            this.opStr = str;
          }
        }
      } while (this.modeStr != null);
      this.modeStr = str;
      label75:
      if (this.opStr == null)
      {
        paramPrintWriter.println("Error: Operation not specified.");
        return -1;
      }
      this.op = strOpToOp(this.opStr, paramPrintWriter);
      if (this.op < 0) {
        return -1;
      }
      if (this.modeStr != null)
      {
        paramInt = strModeToMode(this.modeStr, paramPrintWriter);
        this.mode = paramInt;
        if (paramInt < 0) {
          return -1;
        }
      }
      else
      {
        this.mode = paramInt;
      }
      return 0;
    }
    
    int parseUserPackageOp(boolean paramBoolean, PrintWriter paramPrintWriter)
      throws RemoteException
    {
      this.userId = -2;
      this.packageName = null;
      this.opStr = null;
      String str;
      do
      {
        for (;;)
        {
          str = getNextArg();
          if (str == null) {
            break label75;
          }
          if ("--user".equals(str))
          {
            this.userId = UserHandle.parseUserArg(getNextArgRequired());
          }
          else
          {
            if (this.packageName != null) {
              break;
            }
            this.packageName = str;
          }
        }
      } while (this.opStr != null);
      this.opStr = str;
      label75:
      if (this.packageName == null)
      {
        paramPrintWriter.println("Error: Package name not specified.");
        return -1;
      }
      if ((this.opStr == null) && (paramBoolean))
      {
        paramPrintWriter.println("Error: Operation not specified.");
        return -1;
      }
      if (this.opStr != null)
      {
        this.op = strOpToOp(this.opStr, paramPrintWriter);
        if (this.op < 0) {
          return -1;
        }
      }
      else
      {
        this.op = -1;
      }
      if (this.userId == -2) {
        this.userId = ActivityManager.getCurrentUser();
      }
      if ("root".equals(this.packageName)) {}
      for (this.packageUid = 0; this.packageUid < 0; this.packageUid = AppGlobals.getPackageManager().getPackageUid(this.packageName, 8192, this.userId))
      {
        paramPrintWriter.println("Error: No UID for " + this.packageName + " in user " + this.userId);
        return -1;
      }
      return 0;
    }
    
    int strModeToMode(String paramString, PrintWriter paramPrintWriter)
    {
      if (paramString.equals("allow")) {
        return 0;
      }
      if (paramString.equals("deny")) {
        return 2;
      }
      if (paramString.equals("ignore")) {
        return 1;
      }
      if (paramString.equals("default")) {
        return 3;
      }
      try
      {
        int i = Integer.parseInt(paramString);
        return i;
      }
      catch (NumberFormatException localNumberFormatException)
      {
        paramPrintWriter.println("Error: Mode " + paramString + " is not valid");
      }
      return -1;
    }
  }
  
  private static final class UidState
  {
    public SparseIntArray opModes;
    public ArrayMap<String, AppOpsService.Ops> pkgOps;
    public final int uid;
    
    public UidState(int paramInt)
    {
      this.uid = paramInt;
    }
    
    public void clear()
    {
      this.pkgOps = null;
      this.opModes = null;
    }
    
    public boolean isDefault()
    {
      boolean bool2 = false;
      boolean bool1;
      if (this.pkgOps != null)
      {
        bool1 = bool2;
        if (!this.pkgOps.isEmpty()) {}
      }
      else if (this.opModes != null)
      {
        bool1 = bool2;
        if (this.opModes.size() > 0) {}
      }
      else
      {
        bool1 = true;
      }
      return bool1;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/AppOpsService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */