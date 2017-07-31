package com.android.server.emergency;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.provider.Settings.Global;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.SubscriptionManager.OnSubscriptionsChangedListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.server.SystemService;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EmergencyAffordanceService
  extends SystemService
{
  private static final int CELL_INFO_STATE_CHANGED = 2;
  private static final String EMERGENCY_SIM_INSERTED_SETTING = "emergency_sim_inserted_before";
  private static final int INITIALIZE_STATE = 1;
  private static final int NUM_POLLING_UNTIL_ABORT = 6;
  private static final int NUM_SCANS_UNTIL_ABORT = 10;
  private static final int POLLING_CELL_INFO_INTERVAL = 10000;
  private static final int SUBSCRIPTION_CHANGED = 3;
  private static final String TAG = "EmergencyAffordanceService";
  private BroadcastReceiver mAirplaneModeReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (Settings.Global.getInt(paramAnonymousContext.getContentResolver(), "airplane_mode_on", 0) == 0)
      {
        EmergencyAffordanceService.-wrap5(EmergencyAffordanceService.this);
        EmergencyAffordanceService.-wrap4(EmergencyAffordanceService.this);
        Log.d("EmergencyAffordanceService", "reset mPollingComplete, sent INITIALIZE_STATE msg");
        EmergencyAffordanceService.-set0(EmergencyAffordanceService.this, 0);
        EmergencyAffordanceService.-get0(EmergencyAffordanceService.this).obtainMessage(1).sendToTarget();
      }
    }
  };
  private final Context mContext;
  private boolean mEmergencyAffordanceNeeded;
  private final ArrayList<Integer> mEmergencyCallMccNumbers;
  private MyHandler mHandler;
  private final Object mLock = new Object();
  private boolean mNetworkNeedsEmergencyAffordance;
  private PhoneStateListener mPhoneStateListener = new PhoneStateListener()
  {
    public void onCellInfoChanged(List<CellInfo> paramAnonymousList)
    {
      Log.d("EmergencyAffordanceService", "onCellInfoChanged  cellInfo " + paramAnonymousList);
      if (!EmergencyAffordanceService.-wrap2(EmergencyAffordanceService.this)) {
        EmergencyAffordanceService.-wrap4(EmergencyAffordanceService.this);
      }
    }
    
    public void onCellLocationChanged(CellLocation paramAnonymousCellLocation)
    {
      Log.d("EmergencyAffordanceService", "onCellLocationChanged  location " + paramAnonymousCellLocation);
      if (!EmergencyAffordanceService.-wrap2(EmergencyAffordanceService.this)) {
        EmergencyAffordanceService.-wrap4(EmergencyAffordanceService.this);
      }
    }
  };
  private int mPollingComplete;
  private int mScansCompleted;
  private boolean mSimNeedsEmergencyAffordance;
  private SubscriptionManager.OnSubscriptionsChangedListener mSubscriptionChangedListener = new SubscriptionManager.OnSubscriptionsChangedListener()
  {
    public void onSubscriptionsChanged()
    {
      EmergencyAffordanceService.-get0(EmergencyAffordanceService.this).obtainMessage(3).sendToTarget();
    }
  };
  private SubscriptionManager mSubscriptionManager;
  private TelephonyManager mTelephonyManager;
  private boolean mVoiceCapable;
  
  public EmergencyAffordanceService(Context paramContext)
  {
    super(paramContext);
    this.mContext = paramContext;
    paramContext = paramContext.getResources().getIntArray(84344843);
    int i;
    if (!TextUtils.isEmpty(SystemProperties.get("ecc.test.mcc", null)))
    {
      i = -1;
      try
      {
        j = Integer.valueOf(SystemProperties.get("ecc.test.mcc")).intValue();
        i = j;
      }
      catch (Exception localException)
      {
        for (;;)
        {
          int j;
          Log.d("EmergencyAffordanceService", "mcc should be a number");
        }
        this.mEmergencyCallMccNumbers.add(Integer.valueOf(i));
      }
      Log.d("EmergencyAffordanceService", "add a test mcc : " + i);
      this.mEmergencyCallMccNumbers = new ArrayList(paramContext.length + 1);
      j = 0;
      while (j < paramContext.length)
      {
        this.mEmergencyCallMccNumbers.add(Integer.valueOf(paramContext[j]));
        j += 1;
      }
    }
    for (;;)
    {
      return;
      this.mEmergencyCallMccNumbers = new ArrayList(paramContext.length);
      i = 0;
      while (i < paramContext.length)
      {
        this.mEmergencyCallMccNumbers.add(Integer.valueOf(paramContext[i]));
        i += 1;
      }
    }
  }
  
  private void handleInitializeState()
  {
    Log.d("EmergencyAffordanceService", "handleInitializeState");
    if (handleUpdateSimSubscriptionInfo()) {
      return;
    }
    if (handleUpdateCellInfo()) {
      return;
    }
    onStartPollingCellInfo();
    updateEmergencyAffordanceNeeded();
  }
  
  private boolean handleUpdateCellInfo()
  {
    Object localObject = this.mTelephonyManager.getAllCellInfo();
    Log.d("EmergencyAffordanceService", "handleUpdateCellInfo  cellInfos " + localObject);
    if (localObject == null) {
      return false;
    }
    int j = 0;
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      CellInfo localCellInfo = (CellInfo)((Iterator)localObject).next();
      int i = 0;
      if ((localCellInfo instanceof CellInfoGsm)) {
        i = ((CellInfoGsm)localCellInfo).getCellIdentity().getMcc();
      }
      while (mccRequiresEmergencyAffordance(i))
      {
        setNetworkNeedsEmergencyAffordance(true);
        return true;
        if ((localCellInfo instanceof CellInfoLte)) {
          i = ((CellInfoLte)localCellInfo).getCellIdentity().getMcc();
        } else if ((localCellInfo instanceof CellInfoWcdma)) {
          i = ((CellInfoWcdma)localCellInfo).getCellIdentity().getMcc();
        }
      }
      if ((i != 0) && (i != Integer.MAX_VALUE)) {
        j = 1;
      }
    }
    if (j != 0) {
      stopScanning();
    }
    for (;;)
    {
      setNetworkNeedsEmergencyAffordance(false);
      return false;
      onCellScanFinishedUnsuccessful();
    }
  }
  
  private boolean handleUpdateSimSubscriptionInfo()
  {
    boolean bool3 = simNeededAffordanceBefore();
    boolean bool1 = bool3;
    Object localObject1 = this.mSubscriptionManager.getActiveSubscriptionInfoList();
    if (localObject1 == null)
    {
      Log.d("EmergencyAffordanceService", "activeSubscriptionInfoList is null, return : " + bool3);
      return bool3;
    }
    localObject1 = ((Iterable)localObject1).iterator();
    for (;;)
    {
      boolean bool2 = bool1;
      Object localObject2;
      int i;
      if (((Iterator)localObject1).hasNext())
      {
        localObject2 = (SubscriptionInfo)((Iterator)localObject1).next();
        Log.d("EmergencyAffordanceService", "subinfo = " + localObject2);
        i = ((SubscriptionInfo)localObject2).getMcc();
        if (!mccRequiresEmergencyAffordance(i)) {
          break label178;
        }
      }
      for (bool2 = true;; bool2 = true)
      {
        Log.d("EmergencyAffordanceService", "neededNow = " + bool2 + ", neededBefore = " + bool3);
        setSimNeedsEmergencyAffordance(bool2);
        return bool2;
        label178:
        bool2 = bool1;
        if (i != 0)
        {
          bool2 = bool1;
          if (i != Integer.MAX_VALUE) {
            bool2 = false;
          }
        }
        localObject2 = this.mTelephonyManager.getSimOperator(((SubscriptionInfo)localObject2).getSubscriptionId());
        int j = 0;
        Log.d("EmergencyAffordanceService", "simOperator = " + (String)localObject2);
        i = j;
        if (localObject2 != null)
        {
          i = j;
          if (((String)localObject2).length() >= 3) {
            i = Integer.parseInt(((String)localObject2).substring(0, 3));
          }
        }
        bool1 = bool2;
        if (i == 0) {
          break;
        }
        if (!mccRequiresEmergencyAffordance(i)) {
          break label291;
        }
      }
      label291:
      bool1 = false;
    }
  }
  
  private boolean isEmergencyAffordanceNeeded()
  {
    synchronized (this.mLock)
    {
      boolean bool = this.mEmergencyAffordanceNeeded;
      return bool;
    }
  }
  
  private boolean mccRequiresEmergencyAffordance(int paramInt)
  {
    return this.mEmergencyCallMccNumbers.contains(Integer.valueOf(paramInt));
  }
  
  private void onCellScanFinishedUnsuccessful()
  {
    synchronized (this.mLock)
    {
      this.mScansCompleted += 1;
      Log.d("EmergencyAffordanceService", "mScansCompleted : " + this.mScansCompleted);
      if (this.mScansCompleted >= 10) {
        stopScanning();
      }
      return;
    }
  }
  
  private void onStartPollingCellInfo()
  {
    synchronized (this.mLock)
    {
      this.mPollingComplete += 1;
      Log.d("EmergencyAffordanceService", "polling mPollingComplete : " + this.mPollingComplete);
      if (this.mPollingComplete <= 6)
      {
        this.mHandler.removeMessages(1);
        Message localMessage = this.mHandler.obtainMessage(1);
        this.mHandler.sendMessageDelayed(localMessage, 10000L);
        return;
      }
      onStopPollingCellInfo();
    }
  }
  
  private void onStopPollingCellInfo()
  {
    Log.d("EmergencyAffordanceService", "onStopPollingCellInfo");
    this.mPollingComplete = 0;
    this.mHandler.removeMessages(1);
  }
  
  private void requestCellScan()
  {
    this.mHandler.obtainMessage(2).sendToTarget();
  }
  
  private void setNetworkNeedsEmergencyAffordance(boolean paramBoolean)
  {
    synchronized (this.mLock)
    {
      this.mNetworkNeedsEmergencyAffordance = paramBoolean;
      updateEmergencyAffordanceNeeded();
      return;
    }
  }
  
  private void setSimNeedsEmergencyAffordance(boolean paramBoolean)
  {
    this.mSimNeedsEmergencyAffordance = paramBoolean;
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      Settings.Global.putInt(localContentResolver, "emergency_sim_inserted_before", i);
      updateEmergencyAffordanceNeeded();
      return;
    }
  }
  
  private boolean simNeededAffordanceBefore()
  {
    boolean bool = false;
    if (Settings.Global.getInt(this.mContext.getContentResolver(), "emergency_sim_inserted_before", 0) != 0) {
      bool = true;
    }
    return bool;
  }
  
  private void startScanning()
  {
    this.mTelephonyManager.listen(this.mPhoneStateListener, 1040);
  }
  
  private void stopScanning()
  {
    synchronized (this.mLock)
    {
      Log.d("EmergencyAffordanceService", "stopScanning");
      this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
      this.mScansCompleted = 0;
      return;
    }
  }
  
  private void updateEmergencyAffordanceNeeded()
  {
    int i = 1;
    synchronized (this.mLock)
    {
      Log.d("EmergencyAffordanceService", "mSimNeedsEmergencyAffordance : " + this.mSimNeedsEmergencyAffordance + ", mNetworkNeedsEmergencyAffordance : " + this.mNetworkNeedsEmergencyAffordance);
      boolean bool;
      if (this.mVoiceCapable) {
        if (!this.mSimNeedsEmergencyAffordance) {
          bool = this.mNetworkNeedsEmergencyAffordance;
        }
      }
      for (;;)
      {
        this.mEmergencyAffordanceNeeded = bool;
        ContentResolver localContentResolver = this.mContext.getContentResolver();
        if (!this.mEmergencyAffordanceNeeded) {
          break;
        }
        Settings.Global.putInt(localContentResolver, "emergency_affordance_needed", i);
        if (this.mNetworkNeedsEmergencyAffordance) {
          onStopPollingCellInfo();
        }
        if (this.mEmergencyAffordanceNeeded) {
          stopScanning();
        }
        return;
        bool = true;
        continue;
        bool = false;
      }
      i = 0;
    }
  }
  
  public void onBootPhase(int paramInt)
  {
    if (paramInt == 600)
    {
      this.mTelephonyManager = ((TelephonyManager)this.mContext.getSystemService(TelephonyManager.class));
      this.mVoiceCapable = this.mTelephonyManager.isVoiceCapable();
      if (!this.mVoiceCapable)
      {
        updateEmergencyAffordanceNeeded();
        return;
      }
      this.mSubscriptionManager = SubscriptionManager.from(this.mContext);
      Object localObject = new HandlerThread("EmergencyAffordanceService");
      ((HandlerThread)localObject).start();
      this.mHandler = new MyHandler(((HandlerThread)localObject).getLooper());
      this.mHandler.obtainMessage(1).sendToTarget();
      startScanning();
      localObject = new IntentFilter("android.intent.action.AIRPLANE_MODE");
      this.mContext.registerReceiver(this.mAirplaneModeReceiver, (IntentFilter)localObject);
      this.mSubscriptionManager.addOnSubscriptionsChangedListener(this.mSubscriptionChangedListener);
    }
  }
  
  public void onStart() {}
  
  private class MyHandler
    extends Handler
  {
    public MyHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      Log.d("EmergencyAffordanceService", "receive msg.what = " + paramMessage.what);
      switch (paramMessage.what)
      {
      default: 
        return;
      case 1: 
        EmergencyAffordanceService.-wrap3(EmergencyAffordanceService.this);
        return;
      case 2: 
        EmergencyAffordanceService.-wrap0(EmergencyAffordanceService.this);
        return;
      }
      EmergencyAffordanceService.-wrap1(EmergencyAffordanceService.this);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/emergency/EmergencyAffordanceService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */