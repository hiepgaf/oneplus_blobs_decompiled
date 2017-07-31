package com.android.server.connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.internal.app.IBatteryStats;
import com.android.internal.telephony.IccCardConstants.State;
import com.android.server.am.BatteryStatsService;

public class DataConnectionStats
  extends BroadcastReceiver
{
  private static final boolean DEBUG = false;
  private static final String TAG = "DataConnectionStats";
  private final IBatteryStats mBatteryStats;
  private final Context mContext;
  private int mDataState = 0;
  private final PhoneStateListener mPhoneStateListener = new PhoneStateListener()
  {
    public void onDataActivity(int paramAnonymousInt)
    {
      DataConnectionStats.-wrap0(DataConnectionStats.this);
    }
    
    public void onDataConnectionStateChanged(int paramAnonymousInt1, int paramAnonymousInt2)
    {
      DataConnectionStats.-set0(DataConnectionStats.this, paramAnonymousInt1);
      DataConnectionStats.-wrap0(DataConnectionStats.this);
    }
    
    public void onServiceStateChanged(ServiceState paramAnonymousServiceState)
    {
      DataConnectionStats.-set1(DataConnectionStats.this, paramAnonymousServiceState);
      DataConnectionStats.-wrap0(DataConnectionStats.this);
    }
    
    public void onSignalStrengthsChanged(SignalStrength paramAnonymousSignalStrength)
    {
      DataConnectionStats.-set2(DataConnectionStats.this, paramAnonymousSignalStrength);
    }
  };
  private ServiceState mServiceState;
  private SignalStrength mSignalStrength;
  private IccCardConstants.State mSimState = IccCardConstants.State.READY;
  
  public DataConnectionStats(Context paramContext)
  {
    this.mContext = paramContext;
    this.mBatteryStats = BatteryStatsService.getService();
  }
  
  private boolean hasService()
  {
    if ((this.mServiceState != null) && (this.mServiceState.getState() != 1)) {
      return this.mServiceState.getState() != 3;
    }
    return false;
  }
  
  private boolean isCdma()
  {
    return (this.mSignalStrength != null) && (!this.mSignalStrength.isGsm());
  }
  
  private void notePhoneDataConnectionState()
  {
    int j = 1;
    if (this.mServiceState == null) {
      return;
    }
    int i = j;
    boolean bool;
    if (this.mSimState != IccCardConstants.State.READY)
    {
      if (this.mSimState == IccCardConstants.State.UNKNOWN) {
        i = j;
      }
    }
    else
    {
      if (((i == 0) && (!isCdma())) || (!hasService())) {
        break label92;
      }
      if (this.mDataState != 2) {
        break label87;
      }
      bool = true;
    }
    for (;;)
    {
      i = this.mServiceState.getDataNetworkType();
      try
      {
        this.mBatteryStats.notePhoneDataConnectionState(i, bool);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.w("DataConnectionStats", "Error noting data connection state", localRemoteException);
      }
      i = 0;
      break;
      label87:
      bool = false;
      continue;
      label92:
      bool = false;
    }
  }
  
  private final void updateSimState(Intent paramIntent)
  {
    String str = paramIntent.getStringExtra("ss");
    if ("ABSENT".equals(str))
    {
      this.mSimState = IccCardConstants.State.ABSENT;
      return;
    }
    if ("READY".equals(str))
    {
      this.mSimState = IccCardConstants.State.READY;
      return;
    }
    if ("LOCKED".equals(str))
    {
      paramIntent = paramIntent.getStringExtra("reason");
      if ("PIN".equals(paramIntent))
      {
        this.mSimState = IccCardConstants.State.PIN_REQUIRED;
        return;
      }
      if ("PUK".equals(paramIntent))
      {
        this.mSimState = IccCardConstants.State.PUK_REQUIRED;
        return;
      }
      this.mSimState = IccCardConstants.State.NETWORK_LOCKED;
      return;
    }
    this.mSimState = IccCardConstants.State.UNKNOWN;
  }
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    paramContext = paramIntent.getAction();
    if (paramContext.equals("android.intent.action.SIM_STATE_CHANGED"))
    {
      updateSimState(paramIntent);
      notePhoneDataConnectionState();
    }
    while ((!paramContext.equals("android.net.conn.CONNECTIVITY_CHANGE")) && (!paramContext.equals("android.net.conn.INET_CONDITION_ACTION"))) {
      return;
    }
    notePhoneDataConnectionState();
  }
  
  public void startMonitoring()
  {
    ((TelephonyManager)this.mContext.getSystemService("phone")).listen(this.mPhoneStateListener, 449);
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.SIM_STATE_CHANGED");
    localIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
    localIntentFilter.addAction("android.net.conn.INET_CONDITION_ACTION");
    this.mContext.registerReceiver(this, localIntentFilter);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/connectivity/DataConnectionStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */