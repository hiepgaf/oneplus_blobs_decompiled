package com.android.server;

import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.LinkProperties;
import android.net.NetworkCapabilities;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.PreciseCallState;
import android.telephony.PreciseDataConnectionState;
import android.telephony.Rlog;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.VoLteServiceState;
import android.text.TextUtils;
import android.text.format.Time;
import com.android.internal.app.IBatteryStats;
import com.android.internal.telephony.DefaultPhoneNotifier;
import com.android.internal.telephony.IOnSubscriptionsChangedListener;
import com.android.internal.telephony.IPhoneStateListener;
import com.android.internal.telephony.ITelephonyRegistry.Stub;
import com.android.internal.telephony.PhoneConstants.DataState;
import com.android.internal.telephony.PhoneConstants.State;
import com.android.server.am.BatteryStatsService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class TelephonyRegistry
  extends ITelephonyRegistry.Stub
{
  static final int CHECK_PHONE_STATE_PERMISSION_MASK = 224;
  private static final boolean DBG = false;
  private static final boolean DBG_LOC = false;
  static final int ENFORCE_PHONE_STATE_PERMISSION_MASK = 16396;
  private static final int MSG_UPDATE_DEFAULT_SUB = 2;
  private static final int MSG_USER_SWITCHED = 1;
  static final int PRECISE_PHONE_STATE_PERMISSION_MASK = 6144;
  private static final String TAG = "TelephonyRegistry";
  private static final boolean VDBG = false;
  private boolean hasNotifySubscriptionInfoChangedOccurred = false;
  private LogSSC[] logSSC = new LogSSC[10];
  private final AppOpsManager mAppOps;
  private int mBackgroundCallState = 0;
  private final IBatteryStats mBatteryStats;
  private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getAction();
      int i;
      if ("android.intent.action.USER_SWITCHED".equals(paramAnonymousContext)) {
        i = paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", 0);
      }
      do
      {
        TelephonyRegistry.-get3(TelephonyRegistry.this).sendMessage(TelephonyRegistry.-get3(TelephonyRegistry.this).obtainMessage(1, i, 0));
        do
        {
          return;
        } while (!paramAnonymousContext.equals("android.intent.action.ACTION_DEFAULT_SUBSCRIPTION_CHANGED"));
        paramAnonymousContext = new Integer(paramAnonymousIntent.getIntExtra("subscription", SubscriptionManager.getDefaultSubscriptionId()));
        i = paramAnonymousIntent.getIntExtra("slot", SubscriptionManager.getPhoneId(TelephonyRegistry.-get2(TelephonyRegistry.this)));
      } while ((!TelephonyRegistry.-wrap0(TelephonyRegistry.this, i)) || ((paramAnonymousContext.equals(Integer.valueOf(TelephonyRegistry.-get2(TelephonyRegistry.this)))) && (i == TelephonyRegistry.-get1(TelephonyRegistry.this))));
      TelephonyRegistry.-get3(TelephonyRegistry.this).sendMessage(TelephonyRegistry.-get3(TelephonyRegistry.this).obtainMessage(2, i, 0, paramAnonymousContext));
    }
  };
  private boolean[] mCallForwarding;
  private String[] mCallIncomingNumber;
  private int[] mCallState;
  private boolean mCarrierNetworkChangeState = false;
  private ArrayList<List<CellInfo>> mCellInfo = null;
  private Bundle[] mCellLocation;
  private ArrayList<String>[] mConnectedApns;
  private final Context mContext;
  private int[] mDataActivity;
  private String[] mDataConnectionApn;
  private LinkProperties[] mDataConnectionLinkProperties;
  private NetworkCapabilities[] mDataConnectionNetworkCapabilities;
  private int[] mDataConnectionNetworkType;
  private boolean[] mDataConnectionPossible;
  private String[] mDataConnectionReason;
  private int[] mDataConnectionState;
  private int mDefaultPhoneId = -1;
  private int mDefaultSubId = -1;
  private int mForegroundCallState = 0;
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message arg1)
    {
      switch (???.what)
      {
      default: 
        return;
      case 1: 
        j = TelephonyManager.getDefault().getPhoneCount();
        i = 0;
        while (i < j)
        {
          TelephonyRegistry.this.notifyCellLocationForSubscriber(i, TelephonyRegistry.-get0(TelephonyRegistry.this)[i]);
          i += 1;
        }
      }
      int i = ???.arg1;
      int j = ((Integer)???.obj).intValue();
      synchronized (TelephonyRegistry.-get4(TelephonyRegistry.this))
      {
        Iterator localIterator = TelephonyRegistry.-get4(TelephonyRegistry.this).iterator();
        while (localIterator.hasNext())
        {
          TelephonyRegistry.Record localRecord = (TelephonyRegistry.Record)localIterator.next();
          if (localRecord.subId == Integer.MAX_VALUE) {
            TelephonyRegistry.-wrap1(TelephonyRegistry.this, localRecord, i);
          }
        }
      }
      TelephonyRegistry.-wrap2(TelephonyRegistry.this);
      TelephonyRegistry.-set1(TelephonyRegistry.this, j);
      TelephonyRegistry.-set0(TelephonyRegistry.this, i);
    }
  };
  private boolean[] mImsCapabilityStatus = { 0, 0, 0, 0, 0, 0 };
  private boolean[] mMessageWaiting;
  private int mNumPhones;
  private int mOtaspMode = 1;
  private PreciseCallState mPreciseCallState = new PreciseCallState();
  private PreciseDataConnectionState mPreciseDataConnectionState = new PreciseDataConnectionState();
  private final ArrayList<Record> mRecords = new ArrayList();
  private final ArrayList<IBinder> mRemoveList = new ArrayList();
  private int mRingingCallState = 0;
  private ServiceState[] mServiceState;
  private SignalStrength[] mSignalStrength;
  private VoLteServiceState mVoLteServiceState = new VoLteServiceState();
  private int next = 0;
  
  TelephonyRegistry(Context paramContext)
  {
    CellLocation localCellLocation = CellLocation.getEmpty();
    this.mContext = paramContext;
    this.mBatteryStats = BatteryStatsService.getService();
    int j = TelephonyManager.getDefault().getPhoneCount();
    this.mNumPhones = j;
    this.mConnectedApns = new ArrayList[j];
    this.mCallState = new int[j];
    this.mDataActivity = new int[j];
    this.mDataConnectionState = new int[j];
    this.mDataConnectionNetworkType = new int[j];
    this.mCallIncomingNumber = new String[j];
    this.mServiceState = new ServiceState[j];
    this.mSignalStrength = new SignalStrength[j];
    this.mMessageWaiting = new boolean[j];
    this.mDataConnectionPossible = new boolean[j];
    this.mDataConnectionReason = new String[j];
    this.mDataConnectionApn = new String[j];
    this.mCallForwarding = new boolean[j];
    this.mCellLocation = new Bundle[j];
    this.mDataConnectionLinkProperties = new LinkProperties[j];
    this.mDataConnectionNetworkCapabilities = new NetworkCapabilities[j];
    this.mCellInfo = new ArrayList();
    int i = 0;
    while (i < j)
    {
      this.mConnectedApns[i] = new ArrayList();
      this.mCallState[i] = 0;
      this.mDataActivity[i] = 0;
      this.mDataConnectionState[i] = -1;
      this.mCallIncomingNumber[i] = "";
      this.mServiceState[i] = new ServiceState();
      this.mSignalStrength[i] = new SignalStrength();
      this.mMessageWaiting[i] = false;
      this.mCallForwarding[i] = false;
      this.mDataConnectionPossible[i] = false;
      this.mDataConnectionReason[i] = "";
      this.mDataConnectionApn[i] = "";
      this.mCellLocation[i] = new Bundle();
      this.mCellInfo.add(i, null);
      i += 1;
    }
    if (localCellLocation != null)
    {
      i = 0;
      while (i < j)
      {
        localCellLocation.fillInNotifierBundle(this.mCellLocation[i]);
        i += 1;
      }
    }
    this.mAppOps = ((AppOpsManager)this.mContext.getSystemService(AppOpsManager.class));
  }
  
  private void broadcastCallStateChanged(int paramInt1, String paramString, int paramInt2, int paramInt3)
  {
    l = Binder.clearCallingIdentity();
    if (paramInt1 == 0) {}
    for (;;)
    {
      try
      {
        this.mBatteryStats.notePhoneOff();
      }
      catch (RemoteException localRemoteException)
      {
        Intent localIntent;
        Binder.restoreCallingIdentity(l);
        continue;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
      localIntent = new Intent("android.intent.action.PHONE_STATE");
      localIntent.putExtra("state", DefaultPhoneNotifier.convertCallState(paramInt1).toString());
      if (!TextUtils.isEmpty(paramString)) {
        localIntent.putExtra("incoming_number", paramString);
      }
      if (paramInt3 != -1)
      {
        localIntent.setAction("android.intent.action.SUBSCRIPTION_PHONE_STATE");
        localIntent.putExtra("subscription", paramInt3);
      }
      if (paramInt2 != -1) {
        localIntent.putExtra("slot", paramInt2);
      }
      this.mContext.sendBroadcastAsUser(localIntent, UserHandle.ALL, "android.permission.READ_PRIVILEGED_PHONE_STATE");
      this.mContext.sendBroadcastAsUser(localIntent, UserHandle.ALL, "android.permission.READ_PHONE_STATE", 51);
      return;
      this.mBatteryStats.notePhoneOn();
    }
  }
  
  private void broadcastDataConnectionFailed(String paramString1, String paramString2, int paramInt)
  {
    Intent localIntent = new Intent("android.intent.action.DATA_CONNECTION_FAILED");
    localIntent.putExtra("reason", paramString1);
    localIntent.putExtra("apnType", paramString2);
    localIntent.putExtra("subscription", paramInt);
    this.mContext.sendStickyBroadcastAsUser(localIntent, UserHandle.ALL);
  }
  
  private void broadcastDataConnectionStateChanged(int paramInt1, boolean paramBoolean1, String paramString1, String paramString2, String paramString3, LinkProperties paramLinkProperties, NetworkCapabilities paramNetworkCapabilities, boolean paramBoolean2, int paramInt2)
  {
    Intent localIntent = new Intent("android.intent.action.ANY_DATA_STATE");
    localIntent.putExtra("state", DefaultPhoneNotifier.convertDataState(paramInt1).toString());
    if (!paramBoolean1) {
      localIntent.putExtra("networkUnvailable", true);
    }
    if (paramString1 != null) {
      localIntent.putExtra("reason", paramString1);
    }
    if (paramLinkProperties != null)
    {
      localIntent.putExtra("linkProperties", paramLinkProperties);
      paramString1 = paramLinkProperties.getInterfaceName();
      if (paramString1 != null) {
        localIntent.putExtra("iface", paramString1);
      }
    }
    if (paramNetworkCapabilities != null) {
      localIntent.putExtra("networkCapabilities", paramNetworkCapabilities);
    }
    if (paramBoolean2) {
      localIntent.putExtra("networkRoaming", true);
    }
    localIntent.putExtra("apn", paramString2);
    localIntent.putExtra("apnType", paramString3);
    localIntent.putExtra("subscription", paramInt2);
    this.mContext.sendStickyBroadcastAsUser(localIntent, UserHandle.ALL);
  }
  
  private void broadcastPreciseCallStateChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    Intent localIntent = new Intent("android.intent.action.PRECISE_CALL_STATE");
    localIntent.putExtra("ringing_state", paramInt1);
    localIntent.putExtra("foreground_state", paramInt2);
    localIntent.putExtra("background_state", paramInt3);
    localIntent.putExtra("disconnect_cause", paramInt4);
    localIntent.putExtra("precise_disconnect_cause", paramInt5);
    this.mContext.sendBroadcastAsUser(localIntent, UserHandle.ALL, "android.permission.READ_PRECISE_PHONE_STATE");
  }
  
  private void broadcastPreciseDataConnectionStateChanged(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, LinkProperties paramLinkProperties, String paramString4)
  {
    Intent localIntent = new Intent("android.intent.action.PRECISE_DATA_CONNECTION_STATE_CHANGED");
    localIntent.putExtra("state", paramInt1);
    localIntent.putExtra("networkType", paramInt2);
    if (paramString3 != null) {
      localIntent.putExtra("reason", paramString3);
    }
    if (paramString1 != null) {
      localIntent.putExtra("apnType", paramString1);
    }
    if (paramString2 != null) {
      localIntent.putExtra("apn", paramString2);
    }
    if (paramLinkProperties != null) {
      localIntent.putExtra("linkProperties", paramLinkProperties);
    }
    if (paramString4 != null) {
      localIntent.putExtra("failCause", paramString4);
    }
    this.mContext.sendBroadcastAsUser(localIntent, UserHandle.ALL, "android.permission.READ_PRECISE_PHONE_STATE");
  }
  
  private void broadcastServiceStateChanged(ServiceState paramServiceState, int paramInt1, int paramInt2)
  {
    l = Binder.clearCallingIdentity();
    try
    {
      this.mBatteryStats.notePhoneState(paramServiceState.getState());
      Binder.restoreCallingIdentity(l);
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Intent localIntent;
        Bundle localBundle;
        localRemoteException = localRemoteException;
        Binder.restoreCallingIdentity(l);
      }
    }
    finally
    {
      paramServiceState = finally;
      Binder.restoreCallingIdentity(l);
      throw paramServiceState;
    }
    localIntent = new Intent("android.intent.action.SERVICE_STATE");
    localBundle = new Bundle();
    paramServiceState.fillInNotifierBundle(localBundle);
    localIntent.putExtras(localBundle);
    localIntent.putExtra("subscription", paramInt2);
    localIntent.putExtra("slot", paramInt1);
    this.mContext.sendStickyBroadcastAsUser(localIntent, UserHandle.ALL);
  }
  
  private void broadcastSignalStrengthChanged(SignalStrength paramSignalStrength, int paramInt1, int paramInt2)
  {
    l = Binder.clearCallingIdentity();
    try
    {
      this.mBatteryStats.notePhoneSignalStrength(paramSignalStrength);
      Binder.restoreCallingIdentity(l);
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Intent localIntent;
        Bundle localBundle;
        localRemoteException = localRemoteException;
        Binder.restoreCallingIdentity(l);
      }
    }
    finally
    {
      paramSignalStrength = finally;
      Binder.restoreCallingIdentity(l);
      throw paramSignalStrength;
    }
    localIntent = new Intent("android.intent.action.SIG_STR");
    localIntent.addFlags(536870912);
    localBundle = new Bundle();
    paramSignalStrength.fillInNotifierBundle(localBundle);
    localIntent.putExtras(localBundle);
    localIntent.putExtra("subscription", paramInt2);
    localIntent.putExtra("slot", paramInt1);
    this.mContext.sendStickyBroadcastAsUser(localIntent, UserHandle.ALL);
  }
  
  private boolean canReadPhoneState(String paramString)
  {
    boolean bool = true;
    if (this.mContext.checkCallingOrSelfPermission("android.permission.READ_PRIVILEGED_PHONE_STATE") == 0) {
      return true;
    }
    if (this.mContext.checkCallingOrSelfPermission("android.permission.READ_PHONE_STATE") == 0) {}
    while ((bool) && (this.mAppOps.noteOp(51, Binder.getCallingUid(), paramString) != 0))
    {
      return false;
      bool = false;
    }
    return bool;
  }
  
  private void checkListenerPermission(int paramInt)
  {
    if ((paramInt & 0x10) != 0) {
      this.mContext.enforceCallingOrSelfPermission("android.permission.ACCESS_COARSE_LOCATION", null);
    }
    if ((paramInt & 0x400) != 0) {
      this.mContext.enforceCallingOrSelfPermission("android.permission.ACCESS_COARSE_LOCATION", null);
    }
    if ((paramInt & 0x400C) != 0) {}
    try
    {
      this.mContext.enforceCallingOrSelfPermission("android.permission.READ_PRIVILEGED_PHONE_STATE", null);
      if ((paramInt & 0x1800) != 0) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_PRECISE_PHONE_STATE", null);
      }
      if ((0x8000 & paramInt) != 0) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_PRIVILEGED_PHONE_STATE", null);
      }
      return;
    }
    catch (SecurityException localSecurityException)
    {
      for (;;)
      {
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_PHONE_STATE", null);
      }
    }
  }
  
  private boolean checkNotifyPermission()
  {
    boolean bool = false;
    if (this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_PHONE_STATE") == 0) {
      bool = true;
    }
    return bool;
  }
  
  private boolean checkNotifyPermission(String paramString)
  {
    if (checkNotifyPermission()) {
      return true;
    }
    new StringBuilder().append("Modify Phone State Permission Denial: ").append(paramString).append(" from pid=").append(Binder.getCallingPid()).append(", uid=").append(Binder.getCallingUid()).toString();
    return false;
  }
  
  private void checkPossibleMissNotify(Record paramRecord, int paramInt)
  {
    int k = paramRecord.events;
    if ((k & 0x1) != 0) {}
    try
    {
      paramRecord.callback.onServiceStateChanged(new ServiceState(this.mServiceState[paramInt]));
      if ((k & 0x100) == 0) {}
    }
    catch (RemoteException localRemoteException6)
    {
      try
      {
        localObject = this.mSignalStrength[paramInt];
        paramRecord.callback.onSignalStrengthsChanged(new SignalStrength((SignalStrength)localObject));
        if ((k & 0x2) == 0) {}
      }
      catch (RemoteException localRemoteException6)
      {
        try
        {
          int j = this.mSignalStrength[paramInt].getGsmSignalStrength();
          Object localObject = paramRecord.callback;
          int i = j;
          if (j == 99) {
            i = -1;
          }
          ((IPhoneStateListener)localObject).onSignalStrengthChanged(i);
          if (!validateEventsAndUserLocked(paramRecord, 1024)) {}
        }
        catch (RemoteException localRemoteException6)
        {
          try
          {
            paramRecord.callback.onCellInfoChanged((List)this.mCellInfo.get(paramInt));
            if ((k & 0x4) == 0) {}
          }
          catch (RemoteException localRemoteException6)
          {
            try
            {
              paramRecord.callback.onMessageWaitingIndicatorChanged(this.mMessageWaiting[paramInt]);
              if ((k & 0x8) == 0) {}
            }
            catch (RemoteException localRemoteException6)
            {
              try
              {
                paramRecord.callback.onCallForwardingIndicatorChanged(this.mCallForwarding[paramInt]);
                if (!validateEventsAndUserLocked(paramRecord, 16)) {}
              }
              catch (RemoteException localRemoteException6)
              {
                try
                {
                  for (;;)
                  {
                    paramRecord.callback.onCellLocationChanged(new Bundle(this.mCellLocation[paramInt]));
                    if ((k & 0x40) != 0) {}
                    try
                    {
                      paramRecord.callback.onDataConnectionStateChanged(this.mDataConnectionState[paramInt], this.mDataConnectionNetworkType[paramInt]);
                      return;
                    }
                    catch (RemoteException localRemoteException8)
                    {
                      this.mRemoveList.add(paramRecord.binder);
                    }
                    localRemoteException1 = localRemoteException1;
                    this.mRemoveList.add(paramRecord.binder);
                    continue;
                    localRemoteException2 = localRemoteException2;
                    this.mRemoveList.add(paramRecord.binder);
                    continue;
                    localRemoteException3 = localRemoteException3;
                    this.mRemoveList.add(paramRecord.binder);
                    continue;
                    localRemoteException4 = localRemoteException4;
                    this.mRemoveList.add(paramRecord.binder);
                    continue;
                    localRemoteException5 = localRemoteException5;
                    this.mRemoveList.add(paramRecord.binder);
                  }
                  localRemoteException6 = localRemoteException6;
                  this.mRemoveList.add(paramRecord.binder);
                }
                catch (RemoteException localRemoteException7)
                {
                  for (;;)
                  {
                    this.mRemoveList.add(paramRecord.binder);
                  }
                }
              }
            }
          }
        }
      }
    }
  }
  
  private void enforceCarrierPrivilege()
  {
    TelephonyManager localTelephonyManager = TelephonyManager.getDefault();
    String[] arrayOfString = this.mContext.getPackageManager().getPackagesForUid(Binder.getCallingUid());
    int i = 0;
    int j = arrayOfString.length;
    while (i < j)
    {
      if (localTelephonyManager.checkCarrierPrivilegesForPackage(arrayOfString[i]) == 1) {
        return;
      }
      i += 1;
    }
    throw new SecurityException("Carrier Privilege Permission Denial: from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
  }
  
  private void enforceNotifyPermissionOrCarrierPrivilege(String paramString)
  {
    if (checkNotifyPermission()) {
      return;
    }
    enforceCarrierPrivilege();
  }
  
  private String getCallIncomingNumber(Record paramRecord, int paramInt)
  {
    if (paramRecord.canReadPhoneState) {
      return this.mCallIncomingNumber[paramInt];
    }
    return "";
  }
  
  private void handleRemoveListLocked()
  {
    if (this.mRemoveList.size() > 0)
    {
      Iterator localIterator = this.mRemoveList.iterator();
      while (localIterator.hasNext()) {
        remove((IBinder)localIterator.next());
      }
      this.mRemoveList.clear();
    }
  }
  
  /* Error */
  private void listen(String paramString, IPhoneStateListener paramIPhoneStateListener, int paramInt1, boolean paramBoolean, int paramInt2)
  {
    // Byte code:
    //   0: invokestatic 629	android/os/UserHandle:getCallingUserId	()I
    //   3: istore 7
    //   5: iload_3
    //   6: ifeq +904 -> 910
    //   9: aload_0
    //   10: iload_3
    //   11: invokespecial 631	com/android/server/TelephonyRegistry:checkListenerPermission	(I)V
    //   14: iload_3
    //   15: sipush 16396
    //   18: iand
    //   19: ifeq +14 -> 33
    //   22: aload_0
    //   23: getfield 194	com/android/server/TelephonyRegistry:mContext	Landroid/content/Context;
    //   26: ldc_w 352
    //   29: aconst_null
    //   30: invokevirtual 484	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   33: aload_0
    //   34: getfield 116	com/android/server/TelephonyRegistry:mRecords	Ljava/util/ArrayList;
    //   37: astore 11
    //   39: aload 11
    //   41: monitorenter
    //   42: aload_2
    //   43: invokeinterface 635 1 0
    //   48: astore 12
    //   50: aload_0
    //   51: getfield 116	com/android/server/TelephonyRegistry:mRecords	Ljava/util/ArrayList;
    //   54: invokevirtual 601	java/util/ArrayList:size	()I
    //   57: istore 8
    //   59: iconst_0
    //   60: istore 6
    //   62: iload 6
    //   64: iload 8
    //   66: if_icmpge +591 -> 657
    //   69: aload_0
    //   70: getfield 116	com/android/server/TelephonyRegistry:mRecords	Ljava/util/ArrayList;
    //   73: iload 6
    //   75: invokevirtual 545	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   78: checkcast 13	com/android/server/TelephonyRegistry$Record
    //   81: astore 10
    //   83: aload 12
    //   85: aload 10
    //   87: getfield 571	com/android/server/TelephonyRegistry$Record:binder	Landroid/os/IBinder;
    //   90: if_acmpne +558 -> 648
    //   93: aload 10
    //   95: aload_2
    //   96: putfield 518	com/android/server/TelephonyRegistry$Record:callback	Lcom/android/internal/telephony/IPhoneStateListener;
    //   99: aload 10
    //   101: aload_1
    //   102: putfield 638	com/android/server/TelephonyRegistry$Record:callingPackage	Ljava/lang/String;
    //   105: aload 10
    //   107: iload 7
    //   109: putfield 641	com/android/server/TelephonyRegistry$Record:callerUserId	I
    //   112: iload_3
    //   113: sipush 16620
    //   116: iand
    //   117: ifeq +576 -> 693
    //   120: iconst_1
    //   121: istore 6
    //   123: iload 6
    //   125: ifeq +574 -> 699
    //   128: aload_0
    //   129: aload_1
    //   130: invokespecial 643	com/android/server/TelephonyRegistry:canReadPhoneState	(Ljava/lang/String;)Z
    //   133: istore 9
    //   135: aload 10
    //   137: iload 9
    //   139: putfield 598	com/android/server/TelephonyRegistry$Record:canReadPhoneState	Z
    //   142: iload 5
    //   144: invokestatic 648	android/telephony/SubscriptionManager:isValidSubscriptionId	(I)Z
    //   147: ifne +558 -> 705
    //   150: aload 10
    //   152: ldc_w 649
    //   155: putfield 652	com/android/server/TelephonyRegistry$Record:subId	I
    //   158: aload 10
    //   160: aload 10
    //   162: getfield 652	com/android/server/TelephonyRegistry$Record:subId	I
    //   165: invokestatic 656	android/telephony/SubscriptionManager:getPhoneId	(I)I
    //   168: putfield 659	com/android/server/TelephonyRegistry$Record:phoneId	I
    //   171: aload 10
    //   173: getfield 659	com/android/server/TelephonyRegistry$Record:phoneId	I
    //   176: istore 7
    //   178: aload 10
    //   180: iload_3
    //   181: putfield 514	com/android/server/TelephonyRegistry$Record:events	I
    //   184: iload 4
    //   186: ifeq +439 -> 625
    //   189: aload_0
    //   190: iload 7
    //   192: invokespecial 125	com/android/server/TelephonyRegistry:validatePhoneId	(I)Z
    //   195: istore 4
    //   197: iload 4
    //   199: ifeq +426 -> 625
    //   202: iload_3
    //   203: iconst_1
    //   204: iand
    //   205: ifeq +27 -> 232
    //   208: aload 10
    //   210: getfield 518	com/android/server/TelephonyRegistry$Record:callback	Lcom/android/internal/telephony/IPhoneStateListener;
    //   213: new 230	android/telephony/ServiceState
    //   216: dup
    //   217: aload_0
    //   218: getfield 232	com/android/server/TelephonyRegistry:mServiceState	[Landroid/telephony/ServiceState;
    //   221: iload 7
    //   223: aaload
    //   224: invokespecial 521	android/telephony/ServiceState:<init>	(Landroid/telephony/ServiceState;)V
    //   227: invokeinterface 526 2 0
    //   232: iload_3
    //   233: iconst_2
    //   234: iand
    //   235: ifeq +43 -> 278
    //   238: aload_0
    //   239: getfield 236	com/android/server/TelephonyRegistry:mSignalStrength	[Landroid/telephony/SignalStrength;
    //   242: iload 7
    //   244: aaload
    //   245: invokevirtual 534	android/telephony/SignalStrength:getGsmSignalStrength	()I
    //   248: istore 6
    //   250: aload 10
    //   252: getfield 518	com/android/server/TelephonyRegistry$Record:callback	Lcom/android/internal/telephony/IPhoneStateListener;
    //   255: astore_1
    //   256: iload 6
    //   258: istore 5
    //   260: iload 6
    //   262: bipush 99
    //   264: if_icmpne +6 -> 270
    //   267: iconst_m1
    //   268: istore 5
    //   270: aload_1
    //   271: iload 5
    //   273: invokeinterface 537 2 0
    //   278: iload_3
    //   279: iconst_4
    //   280: iand
    //   281: ifeq +20 -> 301
    //   284: aload 10
    //   286: getfield 518	com/android/server/TelephonyRegistry$Record:callback	Lcom/android/internal/telephony/IPhoneStateListener;
    //   289: aload_0
    //   290: getfield 238	com/android/server/TelephonyRegistry:mMessageWaiting	[Z
    //   293: iload 7
    //   295: baload
    //   296: invokeinterface 555 2 0
    //   301: iload_3
    //   302: bipush 8
    //   304: iand
    //   305: ifeq +20 -> 325
    //   308: aload 10
    //   310: getfield 518	com/android/server/TelephonyRegistry$Record:callback	Lcom/android/internal/telephony/IPhoneStateListener;
    //   313: aload_0
    //   314: getfield 246	com/android/server/TelephonyRegistry:mCallForwarding	[Z
    //   317: iload 7
    //   319: baload
    //   320: invokeinterface 558 2 0
    //   325: aload_0
    //   326: aload 10
    //   328: bipush 16
    //   330: invokespecial 541	com/android/server/TelephonyRegistry:validateEventsAndUserLocked	(Lcom/android/server/TelephonyRegistry$Record;I)Z
    //   333: istore 4
    //   335: iload 4
    //   337: ifeq +27 -> 364
    //   340: aload 10
    //   342: getfield 518	com/android/server/TelephonyRegistry$Record:callback	Lcom/android/internal/telephony/IPhoneStateListener;
    //   345: new 248	android/os/Bundle
    //   348: dup
    //   349: aload_0
    //   350: getfield 100	com/android/server/TelephonyRegistry:mCellLocation	[Landroid/os/Bundle;
    //   353: iload 7
    //   355: aaload
    //   356: invokespecial 560	android/os/Bundle:<init>	(Landroid/os/Bundle;)V
    //   359: invokeinterface 563 2 0
    //   364: iload_3
    //   365: bipush 32
    //   367: iand
    //   368: ifeq +28 -> 396
    //   371: aload 10
    //   373: getfield 518	com/android/server/TelephonyRegistry$Record:callback	Lcom/android/internal/telephony/IPhoneStateListener;
    //   376: aload_0
    //   377: getfield 218	com/android/server/TelephonyRegistry:mCallState	[I
    //   380: iload 7
    //   382: iaload
    //   383: aload_0
    //   384: aload 10
    //   386: iload 7
    //   388: invokespecial 661	com/android/server/TelephonyRegistry:getCallIncomingNumber	(Lcom/android/server/TelephonyRegistry$Record;I)Ljava/lang/String;
    //   391: invokeinterface 665 3 0
    //   396: iload_3
    //   397: bipush 64
    //   399: iand
    //   400: ifeq +27 -> 427
    //   403: aload 10
    //   405: getfield 518	com/android/server/TelephonyRegistry$Record:callback	Lcom/android/internal/telephony/IPhoneStateListener;
    //   408: aload_0
    //   409: getfield 222	com/android/server/TelephonyRegistry:mDataConnectionState	[I
    //   412: iload 7
    //   414: iaload
    //   415: aload_0
    //   416: getfield 224	com/android/server/TelephonyRegistry:mDataConnectionNetworkType	[I
    //   419: iload 7
    //   421: iaload
    //   422: invokeinterface 567 3 0
    //   427: iload_3
    //   428: sipush 128
    //   431: iand
    //   432: ifeq +20 -> 452
    //   435: aload 10
    //   437: getfield 518	com/android/server/TelephonyRegistry$Record:callback	Lcom/android/internal/telephony/IPhoneStateListener;
    //   440: aload_0
    //   441: getfield 220	com/android/server/TelephonyRegistry:mDataActivity	[I
    //   444: iload 7
    //   446: iaload
    //   447: invokeinterface 668 2 0
    //   452: iload_3
    //   453: sipush 256
    //   456: iand
    //   457: ifeq +20 -> 477
    //   460: aload 10
    //   462: getfield 518	com/android/server/TelephonyRegistry$Record:callback	Lcom/android/internal/telephony/IPhoneStateListener;
    //   465: aload_0
    //   466: getfield 236	com/android/server/TelephonyRegistry:mSignalStrength	[Landroid/telephony/SignalStrength;
    //   469: iload 7
    //   471: aaload
    //   472: invokeinterface 531 2 0
    //   477: iload_3
    //   478: sipush 512
    //   481: iand
    //   482: ifeq +17 -> 499
    //   485: aload 10
    //   487: getfield 518	com/android/server/TelephonyRegistry$Record:callback	Lcom/android/internal/telephony/IPhoneStateListener;
    //   490: aload_0
    //   491: getfield 150	com/android/server/TelephonyRegistry:mOtaspMode	I
    //   494: invokeinterface 671 2 0
    //   499: aload_0
    //   500: aload 10
    //   502: sipush 1024
    //   505: invokespecial 541	com/android/server/TelephonyRegistry:validateEventsAndUserLocked	(Lcom/android/server/TelephonyRegistry$Record;I)Z
    //   508: istore 4
    //   510: iload 4
    //   512: ifeq +25 -> 537
    //   515: aload 10
    //   517: getfield 518	com/android/server/TelephonyRegistry$Record:callback	Lcom/android/internal/telephony/IPhoneStateListener;
    //   520: aload_0
    //   521: getfield 152	com/android/server/TelephonyRegistry:mCellInfo	Ljava/util/ArrayList;
    //   524: iload 7
    //   526: invokevirtual 545	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   529: checkcast 547	java/util/List
    //   532: invokeinterface 551 2 0
    //   537: iload_3
    //   538: sipush 2048
    //   541: iand
    //   542: ifeq +17 -> 559
    //   545: aload 10
    //   547: getfield 518	com/android/server/TelephonyRegistry$Record:callback	Lcom/android/internal/telephony/IPhoneStateListener;
    //   550: aload_0
    //   551: getfield 168	com/android/server/TelephonyRegistry:mPreciseCallState	Landroid/telephony/PreciseCallState;
    //   554: invokeinterface 675 2 0
    //   559: iload_3
    //   560: sipush 4096
    //   563: iand
    //   564: ifeq +17 -> 581
    //   567: aload 10
    //   569: getfield 518	com/android/server/TelephonyRegistry$Record:callback	Lcom/android/internal/telephony/IPhoneStateListener;
    //   572: aload_0
    //   573: getfield 177	com/android/server/TelephonyRegistry:mPreciseDataConnectionState	Landroid/telephony/PreciseDataConnectionState;
    //   576: invokeinterface 679 2 0
    //   581: ldc_w 680
    //   584: iload_3
    //   585: iand
    //   586: ifeq +17 -> 603
    //   589: aload 10
    //   591: getfield 518	com/android/server/TelephonyRegistry$Record:callback	Lcom/android/internal/telephony/IPhoneStateListener;
    //   594: aload_0
    //   595: getfield 170	com/android/server/TelephonyRegistry:mCarrierNetworkChangeState	Z
    //   598: invokeinterface 683 2 0
    //   603: ldc_w 684
    //   606: iload_3
    //   607: iand
    //   608: ifeq +17 -> 625
    //   611: aload 10
    //   613: getfield 518	com/android/server/TelephonyRegistry$Record:callback	Lcom/android/internal/telephony/IPhoneStateListener;
    //   616: aload_0
    //   617: getfield 172	com/android/server/TelephonyRegistry:mImsCapabilityStatus	[Z
    //   620: invokeinterface 688 2 0
    //   625: aload 11
    //   627: monitorexit
    //   628: return
    //   629: astore 10
    //   631: aload_0
    //   632: getfield 279	com/android/server/TelephonyRegistry:mAppOps	Landroid/app/AppOpsManager;
    //   635: bipush 51
    //   637: invokestatic 471	android/os/Binder:getCallingUid	()I
    //   640: aload_1
    //   641: invokevirtual 475	android/app/AppOpsManager:noteOp	(IILjava/lang/String;)I
    //   644: ifeq -611 -> 33
    //   647: return
    //   648: iload 6
    //   650: iconst_1
    //   651: iadd
    //   652: istore 6
    //   654: goto -592 -> 62
    //   657: new 13	com/android/server/TelephonyRegistry$Record
    //   660: dup
    //   661: aconst_null
    //   662: invokespecial 691	com/android/server/TelephonyRegistry$Record:<init>	(Lcom/android/server/TelephonyRegistry$Record;)V
    //   665: astore 10
    //   667: aload 10
    //   669: aload 12
    //   671: putfield 571	com/android/server/TelephonyRegistry$Record:binder	Landroid/os/IBinder;
    //   674: aload_0
    //   675: getfield 116	com/android/server/TelephonyRegistry:mRecords	Ljava/util/ArrayList;
    //   678: aload 10
    //   680: invokevirtual 574	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   683: pop
    //   684: goto -591 -> 93
    //   687: astore_1
    //   688: aload 11
    //   690: monitorexit
    //   691: aload_1
    //   692: athrow
    //   693: iconst_0
    //   694: istore 6
    //   696: goto -573 -> 123
    //   699: iconst_0
    //   700: istore 9
    //   702: goto -567 -> 135
    //   705: aload 10
    //   707: iload 5
    //   709: putfield 652	com/android/server/TelephonyRegistry$Record:subId	I
    //   712: goto -554 -> 158
    //   715: astore_1
    //   716: aload_0
    //   717: aload 10
    //   719: getfield 571	com/android/server/TelephonyRegistry$Record:binder	Landroid/os/IBinder;
    //   722: invokespecial 621	com/android/server/TelephonyRegistry:remove	(Landroid/os/IBinder;)V
    //   725: goto -493 -> 232
    //   728: astore_1
    //   729: aload_0
    //   730: aload 10
    //   732: getfield 571	com/android/server/TelephonyRegistry$Record:binder	Landroid/os/IBinder;
    //   735: invokespecial 621	com/android/server/TelephonyRegistry:remove	(Landroid/os/IBinder;)V
    //   738: goto -460 -> 278
    //   741: astore_1
    //   742: aload_0
    //   743: aload 10
    //   745: getfield 571	com/android/server/TelephonyRegistry$Record:binder	Landroid/os/IBinder;
    //   748: invokespecial 621	com/android/server/TelephonyRegistry:remove	(Landroid/os/IBinder;)V
    //   751: goto -450 -> 301
    //   754: astore_1
    //   755: aload_0
    //   756: aload 10
    //   758: getfield 571	com/android/server/TelephonyRegistry$Record:binder	Landroid/os/IBinder;
    //   761: invokespecial 621	com/android/server/TelephonyRegistry:remove	(Landroid/os/IBinder;)V
    //   764: goto -439 -> 325
    //   767: astore_1
    //   768: aload_0
    //   769: aload 10
    //   771: getfield 571	com/android/server/TelephonyRegistry$Record:binder	Landroid/os/IBinder;
    //   774: invokespecial 621	com/android/server/TelephonyRegistry:remove	(Landroid/os/IBinder;)V
    //   777: goto -413 -> 364
    //   780: astore_1
    //   781: aload_0
    //   782: aload 10
    //   784: getfield 571	com/android/server/TelephonyRegistry$Record:binder	Landroid/os/IBinder;
    //   787: invokespecial 621	com/android/server/TelephonyRegistry:remove	(Landroid/os/IBinder;)V
    //   790: goto -394 -> 396
    //   793: astore_1
    //   794: aload_0
    //   795: aload 10
    //   797: getfield 571	com/android/server/TelephonyRegistry$Record:binder	Landroid/os/IBinder;
    //   800: invokespecial 621	com/android/server/TelephonyRegistry:remove	(Landroid/os/IBinder;)V
    //   803: goto -376 -> 427
    //   806: astore_1
    //   807: aload_0
    //   808: aload 10
    //   810: getfield 571	com/android/server/TelephonyRegistry$Record:binder	Landroid/os/IBinder;
    //   813: invokespecial 621	com/android/server/TelephonyRegistry:remove	(Landroid/os/IBinder;)V
    //   816: goto -364 -> 452
    //   819: astore_1
    //   820: aload_0
    //   821: aload 10
    //   823: getfield 571	com/android/server/TelephonyRegistry$Record:binder	Landroid/os/IBinder;
    //   826: invokespecial 621	com/android/server/TelephonyRegistry:remove	(Landroid/os/IBinder;)V
    //   829: goto -352 -> 477
    //   832: astore_1
    //   833: aload_0
    //   834: aload 10
    //   836: getfield 571	com/android/server/TelephonyRegistry$Record:binder	Landroid/os/IBinder;
    //   839: invokespecial 621	com/android/server/TelephonyRegistry:remove	(Landroid/os/IBinder;)V
    //   842: goto -343 -> 499
    //   845: astore_1
    //   846: aload_0
    //   847: aload 10
    //   849: getfield 571	com/android/server/TelephonyRegistry$Record:binder	Landroid/os/IBinder;
    //   852: invokespecial 621	com/android/server/TelephonyRegistry:remove	(Landroid/os/IBinder;)V
    //   855: goto -318 -> 537
    //   858: astore_1
    //   859: aload_0
    //   860: aload 10
    //   862: getfield 571	com/android/server/TelephonyRegistry$Record:binder	Landroid/os/IBinder;
    //   865: invokespecial 621	com/android/server/TelephonyRegistry:remove	(Landroid/os/IBinder;)V
    //   868: goto -309 -> 559
    //   871: astore_1
    //   872: aload_0
    //   873: aload 10
    //   875: getfield 571	com/android/server/TelephonyRegistry$Record:binder	Landroid/os/IBinder;
    //   878: invokespecial 621	com/android/server/TelephonyRegistry:remove	(Landroid/os/IBinder;)V
    //   881: goto -300 -> 581
    //   884: astore_1
    //   885: aload_0
    //   886: aload 10
    //   888: getfield 571	com/android/server/TelephonyRegistry$Record:binder	Landroid/os/IBinder;
    //   891: invokespecial 621	com/android/server/TelephonyRegistry:remove	(Landroid/os/IBinder;)V
    //   894: goto -291 -> 603
    //   897: astore_1
    //   898: aload_0
    //   899: aload 10
    //   901: getfield 571	com/android/server/TelephonyRegistry$Record:binder	Landroid/os/IBinder;
    //   904: invokespecial 621	com/android/server/TelephonyRegistry:remove	(Landroid/os/IBinder;)V
    //   907: goto -282 -> 625
    //   910: aload_0
    //   911: aload_2
    //   912: invokeinterface 635 1 0
    //   917: invokespecial 621	com/android/server/TelephonyRegistry:remove	(Landroid/os/IBinder;)V
    //   920: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	921	0	this	TelephonyRegistry
    //   0	921	1	paramString	String
    //   0	921	2	paramIPhoneStateListener	IPhoneStateListener
    //   0	921	3	paramInt1	int
    //   0	921	4	paramBoolean	boolean
    //   0	921	5	paramInt2	int
    //   60	635	6	i	int
    //   3	522	7	j	int
    //   57	10	8	k	int
    //   133	568	9	bool	boolean
    //   81	531	10	localRecord1	Record
    //   629	1	10	localSecurityException	SecurityException
    //   665	235	10	localRecord2	Record
    //   48	622	12	localIBinder	IBinder
    // Exception table:
    //   from	to	target	type
    //   22	33	629	java/lang/SecurityException
    //   42	59	687	finally
    //   69	93	687	finally
    //   93	112	687	finally
    //   128	135	687	finally
    //   135	158	687	finally
    //   158	184	687	finally
    //   189	197	687	finally
    //   208	232	687	finally
    //   238	256	687	finally
    //   270	278	687	finally
    //   284	301	687	finally
    //   308	325	687	finally
    //   325	335	687	finally
    //   340	364	687	finally
    //   371	396	687	finally
    //   403	427	687	finally
    //   435	452	687	finally
    //   460	477	687	finally
    //   485	499	687	finally
    //   499	510	687	finally
    //   515	537	687	finally
    //   545	559	687	finally
    //   567	581	687	finally
    //   589	603	687	finally
    //   611	625	687	finally
    //   657	684	687	finally
    //   705	712	687	finally
    //   716	725	687	finally
    //   729	738	687	finally
    //   742	751	687	finally
    //   755	764	687	finally
    //   768	777	687	finally
    //   781	790	687	finally
    //   794	803	687	finally
    //   807	816	687	finally
    //   820	829	687	finally
    //   833	842	687	finally
    //   846	855	687	finally
    //   859	868	687	finally
    //   872	881	687	finally
    //   885	894	687	finally
    //   898	907	687	finally
    //   208	232	715	android/os/RemoteException
    //   238	256	728	android/os/RemoteException
    //   270	278	728	android/os/RemoteException
    //   284	301	741	android/os/RemoteException
    //   308	325	754	android/os/RemoteException
    //   340	364	767	android/os/RemoteException
    //   371	396	780	android/os/RemoteException
    //   403	427	793	android/os/RemoteException
    //   435	452	806	android/os/RemoteException
    //   460	477	819	android/os/RemoteException
    //   485	499	832	android/os/RemoteException
    //   515	537	845	android/os/RemoteException
    //   545	559	858	android/os/RemoteException
    //   567	581	871	android/os/RemoteException
    //   589	603	884	android/os/RemoteException
    //   611	625	897	android/os/RemoteException
  }
  
  private static void log(String paramString)
  {
    Rlog.d("TelephonyRegistry", paramString);
  }
  
  private void logServiceStateChanged(String paramString, int paramInt1, int paramInt2, ServiceState paramServiceState)
  {
    if ((this.logSSC == null) || (this.logSSC.length == 0)) {
      return;
    }
    if (this.logSSC[this.next] == null) {
      this.logSSC[this.next] = new LogSSC(null);
    }
    Time localTime = new Time();
    localTime.setToNow();
    this.logSSC[this.next].set(localTime, paramString, paramInt1, paramInt2, paramServiceState);
    paramInt1 = this.next + 1;
    this.next = paramInt1;
    if (paramInt1 >= this.logSSC.length) {
      this.next = 0;
    }
  }
  
  private void remove(IBinder paramIBinder)
  {
    synchronized (this.mRecords)
    {
      int j = this.mRecords.size();
      int i = 0;
      while (i < j)
      {
        if (((Record)this.mRecords.get(i)).binder == paramIBinder)
        {
          this.mRecords.remove(i);
          return;
        }
        i += 1;
      }
      return;
    }
  }
  
  private void toStringLogSSC(String paramString)
  {
    if ((this.logSSC == null) || (this.logSSC.length == 0)) {}
    while ((this.next == 0) && (this.logSSC[this.next] == null))
    {
      log(paramString + ": logSSC is empty");
      return;
    }
    log(paramString + ": logSSC.length=" + this.logSSC.length + " next=" + this.next);
    int j = this.next;
    int i = j;
    if (this.logSSC[j] == null) {
      i = 0;
    }
    do
    {
      log(this.logSSC[i].toString());
      i += 1;
      j = i;
      if (i >= this.logSSC.length) {
        j = 0;
      }
      i = j;
    } while (j != this.next);
    log(paramString + ": ----------------");
  }
  
  /* Error */
  private boolean validateEventsAndUserLocked(Record paramRecord, int paramInt)
  {
    // Byte code:
    //   0: invokestatic 289	android/os/Binder:clearCallingIdentity	()J
    //   3: lstore 4
    //   5: invokestatic 732	android/app/ActivityManager:getCurrentUser	()I
    //   8: istore_3
    //   9: aload_1
    //   10: getfield 641	com/android/server/TelephonyRegistry$Record:callerUserId	I
    //   13: iload_3
    //   14: if_icmpne +18 -> 32
    //   17: aload_1
    //   18: iload_2
    //   19: invokevirtual 735	com/android/server/TelephonyRegistry$Record:matchPhoneStateListenerEvent	(I)Z
    //   22: istore 6
    //   24: lload 4
    //   26: invokestatic 298	android/os/Binder:restoreCallingIdentity	(J)V
    //   29: iload 6
    //   31: ireturn
    //   32: iconst_0
    //   33: istore 6
    //   35: goto -11 -> 24
    //   38: astore_1
    //   39: lload 4
    //   41: invokestatic 298	android/os/Binder:restoreCallingIdentity	(J)V
    //   44: aload_1
    //   45: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	46	0	this	TelephonyRegistry
    //   0	46	1	paramRecord	Record
    //   0	46	2	paramInt	int
    //   8	7	3	i	int
    //   3	37	4	l	long
    //   22	12	6	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   5	24	38	finally
  }
  
  private boolean validatePhoneId(int paramInt)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramInt >= 0)
    {
      bool1 = bool2;
      if (paramInt < this.mNumPhones) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  /* Error */
  public void addOnSubscriptionsChangedListener(String paramString, IOnSubscriptionsChangedListener paramIOnSubscriptionsChangedListener)
  {
    // Byte code:
    //   0: invokestatic 629	android/os/UserHandle:getCallingUserId	()I
    //   3: istore 4
    //   5: aload_0
    //   6: getfield 194	com/android/server/TelephonyRegistry:mContext	Landroid/content/Context;
    //   9: ldc_w 352
    //   12: ldc_w 738
    //   15: invokevirtual 484	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   18: aload_0
    //   19: getfield 116	com/android/server/TelephonyRegistry:mRecords	Ljava/util/ArrayList;
    //   22: astore 8
    //   24: aload 8
    //   26: monitorenter
    //   27: aload_2
    //   28: invokeinterface 741 1 0
    //   33: astore 9
    //   35: aload_0
    //   36: getfield 116	com/android/server/TelephonyRegistry:mRecords	Ljava/util/ArrayList;
    //   39: invokevirtual 601	java/util/ArrayList:size	()I
    //   42: istore 5
    //   44: iconst_0
    //   45: istore_3
    //   46: iload_3
    //   47: iload 5
    //   49: if_icmpge +121 -> 170
    //   52: aload_0
    //   53: getfield 116	com/android/server/TelephonyRegistry:mRecords	Ljava/util/ArrayList;
    //   56: iload_3
    //   57: invokevirtual 545	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   60: checkcast 13	com/android/server/TelephonyRegistry$Record
    //   63: astore 7
    //   65: aload 9
    //   67: aload 7
    //   69: getfield 571	com/android/server/TelephonyRegistry$Record:binder	Landroid/os/IBinder;
    //   72: if_acmpne +91 -> 163
    //   75: aload 7
    //   77: aload_2
    //   78: putfield 745	com/android/server/TelephonyRegistry$Record:onSubscriptionsChangedListenerCallback	Lcom/android/internal/telephony/IOnSubscriptionsChangedListener;
    //   81: aload 7
    //   83: aload_1
    //   84: putfield 638	com/android/server/TelephonyRegistry$Record:callingPackage	Ljava/lang/String;
    //   87: aload 7
    //   89: iload 4
    //   91: putfield 641	com/android/server/TelephonyRegistry$Record:callerUserId	I
    //   94: aload 7
    //   96: iconst_0
    //   97: putfield 514	com/android/server/TelephonyRegistry$Record:events	I
    //   100: aload 7
    //   102: iconst_1
    //   103: putfield 598	com/android/server/TelephonyRegistry$Record:canReadPhoneState	Z
    //   106: aload_0
    //   107: getfield 148	com/android/server/TelephonyRegistry:hasNotifySubscriptionInfoChangedOccurred	Z
    //   110: istore 6
    //   112: iload 6
    //   114: ifeq +105 -> 219
    //   117: aload 7
    //   119: getfield 745	com/android/server/TelephonyRegistry$Record:onSubscriptionsChangedListenerCallback	Lcom/android/internal/telephony/IOnSubscriptionsChangedListener;
    //   122: invokeinterface 748 1 0
    //   127: aload 8
    //   129: monitorexit
    //   130: return
    //   131: astore 7
    //   133: aload_0
    //   134: getfield 194	com/android/server/TelephonyRegistry:mContext	Landroid/content/Context;
    //   137: ldc_w 358
    //   140: ldc_w 738
    //   143: invokevirtual 484	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   146: aload_0
    //   147: getfield 279	com/android/server/TelephonyRegistry:mAppOps	Landroid/app/AppOpsManager;
    //   150: bipush 51
    //   152: invokestatic 471	android/os/Binder:getCallingUid	()I
    //   155: aload_1
    //   156: invokevirtual 475	android/app/AppOpsManager:noteOp	(IILjava/lang/String;)I
    //   159: ifeq -141 -> 18
    //   162: return
    //   163: iload_3
    //   164: iconst_1
    //   165: iadd
    //   166: istore_3
    //   167: goto -121 -> 46
    //   170: new 13	com/android/server/TelephonyRegistry$Record
    //   173: dup
    //   174: aconst_null
    //   175: invokespecial 691	com/android/server/TelephonyRegistry$Record:<init>	(Lcom/android/server/TelephonyRegistry$Record;)V
    //   178: astore 7
    //   180: aload 7
    //   182: aload 9
    //   184: putfield 571	com/android/server/TelephonyRegistry$Record:binder	Landroid/os/IBinder;
    //   187: aload_0
    //   188: getfield 116	com/android/server/TelephonyRegistry:mRecords	Ljava/util/ArrayList;
    //   191: aload 7
    //   193: invokevirtual 574	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   196: pop
    //   197: goto -122 -> 75
    //   200: astore_1
    //   201: aload 8
    //   203: monitorexit
    //   204: aload_1
    //   205: athrow
    //   206: astore_1
    //   207: aload_0
    //   208: aload 7
    //   210: getfield 571	com/android/server/TelephonyRegistry$Record:binder	Landroid/os/IBinder;
    //   213: invokespecial 621	com/android/server/TelephonyRegistry:remove	(Landroid/os/IBinder;)V
    //   216: goto -89 -> 127
    //   219: ldc_w 750
    //   222: invokestatic 720	com/android/server/TelephonyRegistry:log	(Ljava/lang/String;)V
    //   225: goto -98 -> 127
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	228	0	this	TelephonyRegistry
    //   0	228	1	paramString	String
    //   0	228	2	paramIOnSubscriptionsChangedListener	IOnSubscriptionsChangedListener
    //   45	122	3	i	int
    //   3	87	4	j	int
    //   42	8	5	k	int
    //   110	3	6	bool	boolean
    //   63	55	7	localRecord1	Record
    //   131	1	7	localSecurityException	SecurityException
    //   178	31	7	localRecord2	Record
    //   33	150	9	localIBinder	IBinder
    // Exception table:
    //   from	to	target	type
    //   5	18	131	java/lang/SecurityException
    //   27	44	200	finally
    //   52	75	200	finally
    //   75	112	200	finally
    //   117	127	200	finally
    //   170	197	200	finally
    //   207	216	200	finally
    //   219	225	200	finally
    //   117	127	206	android/os/RemoteException
  }
  
  public void dump(FileDescriptor arg1, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    if (this.mContext.checkCallingOrSelfPermission("android.permission.DUMP") != 0)
    {
      paramPrintWriter.println("Permission Denial: can't dump telephony.registry from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
      return;
    }
    synchronized (this.mRecords)
    {
      int j = this.mRecords.size();
      paramPrintWriter.println("last known state:");
      int i = 0;
      while (i < TelephonyManager.getDefault().getPhoneCount())
      {
        paramPrintWriter.println("  Phone Id=" + i);
        paramPrintWriter.println("  mCallState=" + this.mCallState[i]);
        paramPrintWriter.println("  mCallIncomingNumber=" + this.mCallIncomingNumber[i]);
        paramPrintWriter.println("  mServiceState=" + this.mServiceState[i]);
        paramPrintWriter.println("  mSignalStrength=" + this.mSignalStrength[i]);
        paramPrintWriter.println("  mMessageWaiting=" + this.mMessageWaiting[i]);
        paramPrintWriter.println("  mCallForwarding=" + this.mCallForwarding[i]);
        paramPrintWriter.println("  mDataActivity=" + this.mDataActivity[i]);
        paramPrintWriter.println("  mDataConnectionState=" + this.mDataConnectionState[i]);
        paramPrintWriter.println("  mDataConnectionPossible=" + this.mDataConnectionPossible[i]);
        paramPrintWriter.println("  mDataConnectionReason=" + this.mDataConnectionReason[i]);
        paramPrintWriter.println("  mDataConnectionApn=" + this.mDataConnectionApn[i]);
        paramPrintWriter.println("  mDataConnectionLinkProperties=" + this.mDataConnectionLinkProperties[i]);
        paramPrintWriter.println("  mDataConnectionNetworkCapabilities=" + this.mDataConnectionNetworkCapabilities[i]);
        paramPrintWriter.println("  mCellLocation=" + this.mCellLocation[i]);
        paramPrintWriter.println("  mCellInfo=" + this.mCellInfo.get(i));
        i += 1;
      }
      paramPrintWriter.println("registrations: count=" + j);
      paramArrayOfString = this.mRecords.iterator();
      if (paramArrayOfString.hasNext())
      {
        Record localRecord = (Record)paramArrayOfString.next();
        paramPrintWriter.println("  " + localRecord);
      }
    }
  }
  
  boolean idMatch(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt2 < 0) {
      return this.mDefaultPhoneId == paramInt3;
    }
    if (paramInt1 == Integer.MAX_VALUE) {
      return paramInt2 == this.mDefaultSubId;
    }
    return paramInt1 == paramInt2;
  }
  
  public void listen(String paramString, IPhoneStateListener paramIPhoneStateListener, int paramInt, boolean paramBoolean)
  {
    listenForSubscriber(Integer.MAX_VALUE, paramString, paramIPhoneStateListener, paramInt, paramBoolean);
  }
  
  public void listenForSubscriber(int paramInt1, String paramString, IPhoneStateListener paramIPhoneStateListener, int paramInt2, boolean paramBoolean)
  {
    listen(paramString, paramIPhoneStateListener, paramInt2, paramBoolean, paramInt1);
  }
  
  public void notifyCallForwardingChanged(boolean paramBoolean)
  {
    notifyCallForwardingChangedForSubscriber(Integer.MAX_VALUE, paramBoolean);
  }
  
  public void notifyCallForwardingChangedForSubscriber(int paramInt, boolean paramBoolean)
  {
    if (!checkNotifyPermission("notifyCallForwardingChanged()")) {
      return;
    }
    synchronized (this.mRecords)
    {
      int i = SubscriptionManager.getPhoneId(paramInt);
      if (validatePhoneId(i))
      {
        this.mCallForwarding[i] = paramBoolean;
        Iterator localIterator = this.mRecords.iterator();
        for (;;)
        {
          if (localIterator.hasNext())
          {
            Record localRecord = (Record)localIterator.next();
            if (!localRecord.matchPhoneStateListenerEvent(8)) {
              continue;
            }
            boolean bool = idMatch(localRecord.subId, paramInt, i);
            if (!bool) {
              continue;
            }
            try
            {
              localRecord.callback.onCallForwardingIndicatorChanged(paramBoolean);
            }
            catch (RemoteException localRemoteException)
            {
              this.mRemoveList.add(localRecord.binder);
            }
          }
        }
      }
    }
    handleRemoveListLocked();
  }
  
  public void notifyCallState(int paramInt, String paramString)
  {
    if (!checkNotifyPermission("notifyCallState()")) {
      return;
    }
    for (;;)
    {
      synchronized (this.mRecords)
      {
        Iterator localIterator = this.mRecords.iterator();
        if (!localIterator.hasNext()) {
          break;
        }
        Record localRecord = (Record)localIterator.next();
        if (!localRecord.matchPhoneStateListenerEvent(32)) {
          continue;
        }
        int i = localRecord.subId;
        if (i != Integer.MAX_VALUE) {
          continue;
        }
        try
        {
          if (localRecord.canReadPhoneState)
          {
            String str1 = paramString;
            localRecord.callback.onCallStateChanged(paramInt, str1);
          }
        }
        catch (RemoteException localRemoteException)
        {
          this.mRemoveList.add(localRecord.binder);
        }
      }
      String str2 = "";
    }
    handleRemoveListLocked();
    broadcastCallStateChanged(paramInt, paramString, -1, -1);
  }
  
  public void notifyCallStateForPhoneId(int paramInt1, int paramInt2, int paramInt3, String paramString)
  {
    if (!checkNotifyPermission("notifyCallState()")) {
      return;
    }
    synchronized (this.mRecords)
    {
      if (validatePhoneId(paramInt1))
      {
        this.mCallState[paramInt1] = paramInt3;
        this.mCallIncomingNumber[paramInt1] = paramString;
        Iterator localIterator = this.mRecords.iterator();
        for (;;)
        {
          if (localIterator.hasNext())
          {
            Record localRecord = (Record)localIterator.next();
            if ((!localRecord.matchPhoneStateListenerEvent(32)) || (localRecord.subId != paramInt2)) {
              continue;
            }
            int i = localRecord.subId;
            if (i == Integer.MAX_VALUE) {
              continue;
            }
            try
            {
              String str = getCallIncomingNumber(localRecord, paramInt1);
              localRecord.callback.onCallStateChanged(paramInt3, str);
            }
            catch (RemoteException localRemoteException)
            {
              this.mRemoveList.add(localRecord.binder);
            }
          }
        }
      }
    }
    handleRemoveListLocked();
    broadcastCallStateChanged(paramInt3, paramString, paramInt1, paramInt2);
  }
  
  public void notifyCarrierNetworkChange(boolean paramBoolean)
  {
    enforceNotifyPermissionOrCarrierPrivilege("notifyCarrierNetworkChange()");
    synchronized (this.mRecords)
    {
      this.mCarrierNetworkChangeState = paramBoolean;
      Iterator localIterator = this.mRecords.iterator();
      for (;;)
      {
        if (localIterator.hasNext())
        {
          Record localRecord = (Record)localIterator.next();
          boolean bool = localRecord.matchPhoneStateListenerEvent(65536);
          if (!bool) {
            continue;
          }
          try
          {
            localRecord.callback.onCarrierNetworkChange(paramBoolean);
          }
          catch (RemoteException localRemoteException)
          {
            this.mRemoveList.add(localRecord.binder);
          }
        }
      }
    }
    handleRemoveListLocked();
  }
  
  public void notifyCellInfo(List<CellInfo> paramList)
  {
    notifyCellInfoForSubscriber(Integer.MAX_VALUE, paramList);
  }
  
  public void notifyCellInfoForSubscriber(int paramInt, List<CellInfo> paramList)
  {
    if (!checkNotifyPermission("notifyCellInfo()")) {
      return;
    }
    synchronized (this.mRecords)
    {
      int i = SubscriptionManager.getPhoneId(paramInt);
      if (validatePhoneId(i))
      {
        this.mCellInfo.set(i, paramList);
        Iterator localIterator = this.mRecords.iterator();
        for (;;)
        {
          if (localIterator.hasNext())
          {
            Record localRecord = (Record)localIterator.next();
            if (!validateEventsAndUserLocked(localRecord, 1024)) {
              continue;
            }
            boolean bool = idMatch(localRecord.subId, paramInt, i);
            if (!bool) {
              continue;
            }
            try
            {
              localRecord.callback.onCellInfoChanged(paramList);
            }
            catch (RemoteException localRemoteException)
            {
              this.mRemoveList.add(localRecord.binder);
            }
          }
        }
      }
    }
    handleRemoveListLocked();
  }
  
  public void notifyCellLocation(Bundle paramBundle)
  {
    notifyCellLocationForSubscriber(Integer.MAX_VALUE, paramBundle);
  }
  
  public void notifyCellLocationForSubscriber(int paramInt, Bundle paramBundle)
  {
    log("notifyCellLocationForSubscriber: subId=" + paramInt + " cellLocation=" + paramBundle);
    if (!checkNotifyPermission("notifyCellLocation()")) {
      return;
    }
    synchronized (this.mRecords)
    {
      int i = SubscriptionManager.getPhoneId(paramInt);
      if (validatePhoneId(i))
      {
        this.mCellLocation[i] = paramBundle;
        Iterator localIterator = this.mRecords.iterator();
        for (;;)
        {
          if (localIterator.hasNext())
          {
            Record localRecord = (Record)localIterator.next();
            if (!validateEventsAndUserLocked(localRecord, 16)) {
              continue;
            }
            boolean bool = idMatch(localRecord.subId, paramInt, i);
            if (!bool) {
              continue;
            }
            try
            {
              localRecord.callback.onCellLocationChanged(new Bundle(paramBundle));
            }
            catch (RemoteException localRemoteException)
            {
              this.mRemoveList.add(localRecord.binder);
            }
          }
        }
      }
    }
    handleRemoveListLocked();
  }
  
  public void notifyDataActivity(int paramInt)
  {
    notifyDataActivityForSubscriber(Integer.MAX_VALUE, paramInt);
  }
  
  public void notifyDataActivityForSubscriber(int paramInt1, int paramInt2)
  {
    if (!checkNotifyPermission("notifyDataActivity()")) {
      return;
    }
    synchronized (this.mRecords)
    {
      int i = SubscriptionManager.getPhoneId(paramInt1);
      if (validatePhoneId(i))
      {
        this.mDataActivity[i] = paramInt2;
        Iterator localIterator = this.mRecords.iterator();
        for (;;)
        {
          if (localIterator.hasNext())
          {
            Record localRecord = (Record)localIterator.next();
            if (!localRecord.matchPhoneStateListenerEvent(128)) {
              continue;
            }
            boolean bool = idMatch(localRecord.subId, paramInt1, i);
            if (!bool) {
              continue;
            }
            try
            {
              localRecord.callback.onDataActivity(paramInt2);
            }
            catch (RemoteException localRemoteException)
            {
              this.mRemoveList.add(localRecord.binder);
            }
          }
        }
      }
    }
    handleRemoveListLocked();
  }
  
  public void notifyDataConnection(int paramInt1, boolean paramBoolean1, String paramString1, String paramString2, String paramString3, LinkProperties paramLinkProperties, NetworkCapabilities paramNetworkCapabilities, int paramInt2, boolean paramBoolean2)
  {
    notifyDataConnectionForSubscriber(Integer.MAX_VALUE, paramInt1, paramBoolean1, paramString1, paramString2, paramString3, paramLinkProperties, paramNetworkCapabilities, paramInt2, paramBoolean2);
  }
  
  public void notifyDataConnectionFailed(String paramString1, String paramString2)
  {
    notifyDataConnectionFailedForSubscriber(Integer.MAX_VALUE, paramString1, paramString2);
  }
  
  public void notifyDataConnectionFailedForSubscriber(int paramInt, String paramString1, String paramString2)
  {
    if (!checkNotifyPermission("notifyDataConnectionFailed()")) {
      return;
    }
    synchronized (this.mRecords)
    {
      this.mPreciseDataConnectionState = new PreciseDataConnectionState(-1, 0, paramString2, "", paramString1, null, "");
      Iterator localIterator = this.mRecords.iterator();
      for (;;)
      {
        if (localIterator.hasNext())
        {
          Record localRecord = (Record)localIterator.next();
          boolean bool = localRecord.matchPhoneStateListenerEvent(4096);
          if (!bool) {
            continue;
          }
          try
          {
            localRecord.callback.onPreciseDataConnectionStateChanged(this.mPreciseDataConnectionState);
          }
          catch (RemoteException localRemoteException)
          {
            this.mRemoveList.add(localRecord.binder);
          }
        }
      }
    }
    handleRemoveListLocked();
    broadcastDataConnectionFailed(paramString1, paramString2, paramInt);
    broadcastPreciseDataConnectionStateChanged(-1, 0, paramString2, "", paramString1, null, "");
  }
  
  public void notifyDataConnectionForSubscriber(int paramInt1, int paramInt2, boolean paramBoolean1, String paramString1, String paramString2, String paramString3, LinkProperties paramLinkProperties, NetworkCapabilities paramNetworkCapabilities, int paramInt3, boolean paramBoolean2)
  {
    if (!checkNotifyPermission("notifyDataConnection()")) {
      return;
    }
    Record localRecord;
    boolean bool;
    for (;;)
    {
      int k;
      int j;
      synchronized (this.mRecords)
      {
        k = SubscriptionManager.getPhoneId(paramInt1);
        if (!validatePhoneId(k)) {
          break label483;
        }
        j = 0;
        if (paramInt2 != 2) {
          break label321;
        }
        i = j;
        if (!this.mConnectedApns[k].contains(paramString3))
        {
          if (paramString3.equals("ims")) {
            i = j;
          }
        }
        else
        {
          this.mDataConnectionPossible[k] = paramBoolean1;
          this.mDataConnectionReason[k] = paramString1;
          this.mDataConnectionLinkProperties[k] = paramLinkProperties;
          this.mDataConnectionNetworkCapabilities[k] = paramNetworkCapabilities;
          if (this.mDataConnectionNetworkType[k] != paramInt3)
          {
            this.mDataConnectionNetworkType[k] = paramInt3;
            i = 1;
          }
          if (i == 0) {
            break label375;
          }
          localIterator = this.mRecords.iterator();
          if (!localIterator.hasNext()) {
            break;
          }
          localRecord = (Record)localIterator.next();
          if (!localRecord.matchPhoneStateListenerEvent(64)) {
            continue;
          }
          bool = idMatch(localRecord.subId, paramInt1, k);
          if (!bool) {
            continue;
          }
          try
          {
            log("Notify data connection state changed on sub: " + paramInt1);
            localRecord.callback.onDataConnectionStateChanged(this.mDataConnectionState[k], this.mDataConnectionNetworkType[k]);
          }
          catch (RemoteException localRemoteException1)
          {
            this.mRemoveList.add(localRecord.binder);
          }
        }
      }
      this.mConnectedApns[k].add(paramString3);
      int i = j;
      if (this.mDataConnectionState[k] != paramInt2)
      {
        this.mDataConnectionState[k] = paramInt2;
        i = 1;
        continue;
        label321:
        i = j;
        if (this.mConnectedApns[k].remove(paramString3))
        {
          i = j;
          if (this.mConnectedApns[k].isEmpty())
          {
            this.mDataConnectionState[k] = paramInt2;
            i = 1;
          }
        }
      }
    }
    handleRemoveListLocked();
    label375:
    this.mPreciseDataConnectionState = new PreciseDataConnectionState(paramInt2, paramInt3, paramString3, paramString2, paramString1, paramLinkProperties, "");
    Iterator localIterator = this.mRecords.iterator();
    while (localIterator.hasNext())
    {
      localRecord = (Record)localIterator.next();
      bool = localRecord.matchPhoneStateListenerEvent(4096);
      if (bool) {
        try
        {
          localRecord.callback.onPreciseDataConnectionStateChanged(this.mPreciseDataConnectionState);
        }
        catch (RemoteException localRemoteException2)
        {
          this.mRemoveList.add(localRecord.binder);
        }
      }
    }
    label483:
    handleRemoveListLocked();
    broadcastDataConnectionStateChanged(paramInt2, paramBoolean1, paramString1, paramString2, paramString3, paramLinkProperties, paramNetworkCapabilities, paramBoolean2, paramInt1);
    broadcastPreciseDataConnectionStateChanged(paramInt2, paramInt3, paramString3, paramString2, paramString1, paramLinkProperties, "");
  }
  
  public void notifyDisconnectCause(int paramInt1, int paramInt2)
  {
    if (!checkNotifyPermission("notifyDisconnectCause()")) {
      return;
    }
    synchronized (this.mRecords)
    {
      this.mPreciseCallState = new PreciseCallState(this.mRingingCallState, this.mForegroundCallState, this.mBackgroundCallState, paramInt1, paramInt2);
      Iterator localIterator = this.mRecords.iterator();
      for (;;)
      {
        if (localIterator.hasNext())
        {
          Record localRecord = (Record)localIterator.next();
          boolean bool = localRecord.matchPhoneStateListenerEvent(2048);
          if (!bool) {
            continue;
          }
          try
          {
            localRecord.callback.onPreciseCallStateChanged(this.mPreciseCallState);
          }
          catch (RemoteException localRemoteException)
          {
            this.mRemoveList.add(localRecord.binder);
          }
        }
      }
    }
    handleRemoveListLocked();
    broadcastPreciseCallStateChanged(this.mRingingCallState, this.mForegroundCallState, this.mBackgroundCallState, paramInt1, paramInt2);
  }
  
  public void notifyImsCapabilityStatusChange(boolean[] arg1)
  {
    this.mImsCapabilityStatus = ???;
    if (!checkNotifyPermission("notifyImsCapabilityStatusChange()")) {
      return;
    }
    synchronized (this.mRecords)
    {
      Iterator localIterator = this.mRecords.iterator();
      for (;;)
      {
        if (localIterator.hasNext())
        {
          Record localRecord = (Record)localIterator.next();
          boolean bool = localRecord.matchPhoneStateListenerEvent(131072);
          if (!bool) {
            continue;
          }
          try
          {
            localRecord.callback.onImsCapabilityStatusChange(this.mImsCapabilityStatus);
          }
          catch (RemoteException localRemoteException)
          {
            this.mRemoveList.add(localRecord.binder);
          }
        }
      }
    }
    handleRemoveListLocked();
  }
  
  public void notifyMessageWaitingChangedForPhoneId(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if (!checkNotifyPermission("notifyMessageWaitingChanged()")) {
      return;
    }
    synchronized (this.mRecords)
    {
      if (validatePhoneId(paramInt1))
      {
        this.mMessageWaiting[paramInt1] = paramBoolean;
        Iterator localIterator = this.mRecords.iterator();
        for (;;)
        {
          if (localIterator.hasNext())
          {
            Record localRecord = (Record)localIterator.next();
            if (!localRecord.matchPhoneStateListenerEvent(4)) {
              continue;
            }
            boolean bool = idMatch(localRecord.subId, paramInt2, paramInt1);
            if (!bool) {
              continue;
            }
            try
            {
              localRecord.callback.onMessageWaitingIndicatorChanged(paramBoolean);
            }
            catch (RemoteException localRemoteException)
            {
              this.mRemoveList.add(localRecord.binder);
            }
          }
        }
      }
    }
    handleRemoveListLocked();
  }
  
  public void notifyOemHookRawEventForSubscriber(int paramInt, byte[] paramArrayOfByte)
  {
    if (!checkNotifyPermission("notifyOemHookRawEventForSubscriber")) {
      return;
    }
    synchronized (this.mRecords)
    {
      Iterator localIterator = this.mRecords.iterator();
      for (;;)
      {
        if (localIterator.hasNext())
        {
          Record localRecord = (Record)localIterator.next();
          if (!localRecord.matchPhoneStateListenerEvent(32768)) {
            continue;
          }
          if (localRecord.subId != paramInt)
          {
            int i = localRecord.subId;
            if (i != Integer.MAX_VALUE) {
              continue;
            }
          }
          try
          {
            localRecord.callback.onOemHookRawEvent(paramArrayOfByte);
          }
          catch (RemoteException localRemoteException)
          {
            this.mRemoveList.add(localRecord.binder);
          }
        }
      }
    }
    handleRemoveListLocked();
  }
  
  public void notifyOtaspChanged(int paramInt)
  {
    if (!checkNotifyPermission("notifyOtaspChanged()")) {
      return;
    }
    synchronized (this.mRecords)
    {
      this.mOtaspMode = paramInt;
      Iterator localIterator = this.mRecords.iterator();
      for (;;)
      {
        if (localIterator.hasNext())
        {
          Record localRecord = (Record)localIterator.next();
          boolean bool = localRecord.matchPhoneStateListenerEvent(512);
          if (!bool) {
            continue;
          }
          try
          {
            localRecord.callback.onOtaspChanged(paramInt);
          }
          catch (RemoteException localRemoteException)
          {
            this.mRemoveList.add(localRecord.binder);
          }
        }
      }
    }
    handleRemoveListLocked();
  }
  
  public void notifyPreciseCallState(int paramInt1, int paramInt2, int paramInt3)
  {
    if (!checkNotifyPermission("notifyPreciseCallState()")) {
      return;
    }
    synchronized (this.mRecords)
    {
      this.mRingingCallState = paramInt1;
      this.mForegroundCallState = paramInt2;
      this.mBackgroundCallState = paramInt3;
      this.mPreciseCallState = new PreciseCallState(paramInt1, paramInt2, paramInt3, -1, -1);
      Iterator localIterator = this.mRecords.iterator();
      for (;;)
      {
        if (localIterator.hasNext())
        {
          Record localRecord = (Record)localIterator.next();
          boolean bool = localRecord.matchPhoneStateListenerEvent(2048);
          if (!bool) {
            continue;
          }
          try
          {
            localRecord.callback.onPreciseCallStateChanged(this.mPreciseCallState);
          }
          catch (RemoteException localRemoteException)
          {
            this.mRemoveList.add(localRecord.binder);
          }
        }
      }
    }
    handleRemoveListLocked();
    broadcastPreciseCallStateChanged(paramInt1, paramInt2, paramInt3, -1, -1);
  }
  
  public void notifyPreciseDataConnectionFailed(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    if (!checkNotifyPermission("notifyPreciseDataConnectionFailed()")) {
      return;
    }
    synchronized (this.mRecords)
    {
      this.mPreciseDataConnectionState = new PreciseDataConnectionState(-1, 0, paramString2, paramString3, paramString1, null, paramString4);
      Iterator localIterator = this.mRecords.iterator();
      for (;;)
      {
        if (localIterator.hasNext())
        {
          Record localRecord = (Record)localIterator.next();
          boolean bool = localRecord.matchPhoneStateListenerEvent(4096);
          if (!bool) {
            continue;
          }
          try
          {
            localRecord.callback.onPreciseDataConnectionStateChanged(this.mPreciseDataConnectionState);
          }
          catch (RemoteException localRemoteException)
          {
            this.mRemoveList.add(localRecord.binder);
          }
        }
      }
    }
    handleRemoveListLocked();
    broadcastPreciseDataConnectionStateChanged(-1, 0, paramString2, paramString3, paramString1, null, paramString4);
  }
  
  public void notifyServiceStateForPhoneId(int paramInt1, int paramInt2, ServiceState paramServiceState)
  {
    if (!checkNotifyPermission("notifyServiceState()")) {
      return;
    }
    synchronized (this.mRecords)
    {
      if (validatePhoneId(paramInt1))
      {
        this.mServiceState[paramInt1] = paramServiceState;
        logServiceStateChanged("notifyServiceStateForSubscriber", paramInt2, paramInt1, paramServiceState);
        Iterator localIterator = this.mRecords.iterator();
        for (;;)
        {
          if (!localIterator.hasNext()) {
            break label173;
          }
          Record localRecord = (Record)localIterator.next();
          if (localRecord.matchPhoneStateListenerEvent(1))
          {
            boolean bool = idMatch(localRecord.subId, paramInt2, paramInt1);
            if (bool) {
              try
              {
                localRecord.callback.onServiceStateChanged(new ServiceState(paramServiceState));
              }
              catch (RemoteException localRemoteException)
              {
                this.mRemoveList.add(localRecord.binder);
              }
            }
          }
        }
      }
    }
    log("notifyServiceStateForSubscriber: INVALID phoneId=" + paramInt1);
    label173:
    handleRemoveListLocked();
    broadcastServiceStateChanged(paramServiceState, paramInt1, paramInt2);
  }
  
  public void notifySignalStrengthForPhoneId(int paramInt1, int paramInt2, SignalStrength paramSignalStrength)
  {
    if (!checkNotifyPermission("notifySignalStrength()")) {
      return;
    }
    Record localRecord;
    boolean bool;
    synchronized (this.mRecords)
    {
      if (validatePhoneId(paramInt1))
      {
        this.mSignalStrength[paramInt1] = paramSignalStrength;
        Iterator localIterator = this.mRecords.iterator();
        if (!localIterator.hasNext()) {
          break label238;
        }
        localRecord = (Record)localIterator.next();
        if (localRecord.matchPhoneStateListenerEvent(256))
        {
          bool = idMatch(localRecord.subId, paramInt2, paramInt1);
          if (!bool) {}
        }
      }
    }
    label238:
    label253:
    for (;;)
    {
      try
      {
        localRecord.callback.onSignalStrengthsChanged(new SignalStrength(paramSignalStrength));
        if (!localRecord.matchPhoneStateListenerEvent(2)) {
          break;
        }
        bool = idMatch(localRecord.subId, paramInt2, paramInt1);
        if (!bool) {
          break;
        }
        try
        {
          int i = paramSignalStrength.getGsmSignalStrength();
          if (i != 99) {
            break label253;
          }
          i = -1;
          localRecord.callback.onSignalStrengthChanged(i);
        }
        catch (RemoteException localRemoteException1)
        {
          this.mRemoveList.add(localRecord.binder);
        }
        break;
        paramSignalStrength = finally;
        throw paramSignalStrength;
      }
      catch (RemoteException localRemoteException2)
      {
        this.mRemoveList.add(localRecord.binder);
        continue;
      }
      log("notifySignalStrengthForPhoneId: invalid phoneId=" + paramInt1);
      handleRemoveListLocked();
      broadcastSignalStrengthChanged(paramSignalStrength, paramInt1, paramInt2);
      return;
    }
  }
  
  public void notifySubscriptionInfoChanged()
  {
    synchronized (this.mRecords)
    {
      if (!this.hasNotifySubscriptionInfoChangedOccurred) {
        log("notifySubscriptionInfoChanged: first invocation mRecords.size=" + this.mRecords.size());
      }
      this.hasNotifySubscriptionInfoChangedOccurred = true;
      this.mRemoveList.clear();
      Iterator localIterator = this.mRecords.iterator();
      for (;;)
      {
        if (localIterator.hasNext())
        {
          Record localRecord = (Record)localIterator.next();
          boolean bool = localRecord.matchOnSubscriptionsChangedListener();
          if (!bool) {
            continue;
          }
          try
          {
            localRecord.onSubscriptionsChangedListenerCallback.onSubscriptionsChanged();
          }
          catch (RemoteException localRemoteException)
          {
            this.mRemoveList.add(localRecord.binder);
          }
        }
      }
    }
    handleRemoveListLocked();
  }
  
  public void notifyVoLteServiceStateChanged(VoLteServiceState paramVoLteServiceState)
  {
    if (!checkNotifyPermission("notifyVoLteServiceStateChanged()")) {
      return;
    }
    synchronized (this.mRecords)
    {
      this.mVoLteServiceState = paramVoLteServiceState;
      paramVoLteServiceState = this.mRecords.iterator();
      for (;;)
      {
        if (paramVoLteServiceState.hasNext())
        {
          Record localRecord = (Record)paramVoLteServiceState.next();
          boolean bool = localRecord.matchPhoneStateListenerEvent(16384);
          if (!bool) {
            continue;
          }
          try
          {
            localRecord.callback.onVoLteServiceStateChanged(new VoLteServiceState(this.mVoLteServiceState));
          }
          catch (RemoteException localRemoteException)
          {
            this.mRemoveList.add(localRecord.binder);
          }
        }
      }
    }
    handleRemoveListLocked();
  }
  
  public void removeOnSubscriptionsChangedListener(String paramString, IOnSubscriptionsChangedListener paramIOnSubscriptionsChangedListener)
  {
    remove(paramIOnSubscriptionsChangedListener.asBinder());
  }
  
  public void systemRunning()
  {
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.USER_SWITCHED");
    localIntentFilter.addAction("android.intent.action.USER_REMOVED");
    localIntentFilter.addAction("android.intent.action.ACTION_DEFAULT_SUBSCRIPTION_CHANGED");
    log("systemRunning register for intents");
    this.mContext.registerReceiver(this.mBroadcastReceiver, localIntentFilter);
  }
  
  private static class LogSSC
  {
    private int mPhoneId;
    private String mS;
    private ServiceState mState;
    private int mSubId;
    private Time mTime;
    
    public void set(Time paramTime, String paramString, int paramInt1, int paramInt2, ServiceState paramServiceState)
    {
      this.mTime = paramTime;
      this.mS = paramString;
      this.mSubId = paramInt1;
      this.mPhoneId = paramInt2;
      this.mState = paramServiceState;
    }
    
    public String toString()
    {
      return this.mS + " Time " + this.mTime.toString() + " mSubId " + this.mSubId + " mPhoneId " + this.mPhoneId + "  mState " + this.mState;
    }
  }
  
  private static class Record
  {
    IBinder binder;
    IPhoneStateListener callback;
    int callerUserId;
    String callingPackage;
    boolean canReadPhoneState;
    int events;
    IOnSubscriptionsChangedListener onSubscriptionsChangedListenerCallback;
    int phoneId = -1;
    int subId = -1;
    
    boolean matchOnSubscriptionsChangedListener()
    {
      return this.onSubscriptionsChangedListenerCallback != null;
    }
    
    boolean matchPhoneStateListenerEvent(int paramInt)
    {
      boolean bool2 = false;
      boolean bool1 = bool2;
      if (this.callback != null)
      {
        bool1 = bool2;
        if ((this.events & paramInt) != 0) {
          bool1 = true;
        }
      }
      return bool1;
    }
    
    public String toString()
    {
      return "{callingPackage=" + this.callingPackage + " binder=" + this.binder + " callback=" + this.callback + " onSubscriptionsChangedListenererCallback=" + this.onSubscriptionsChangedListenerCallback + " callerUserId=" + this.callerUserId + " subId=" + this.subId + " phoneId=" + this.phoneId + " events=" + Integer.toHexString(this.events) + " canReadPhoneState=" + this.canReadPhoneState + "}";
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/TelephonyRegistry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */