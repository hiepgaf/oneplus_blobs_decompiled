package com.android.server.job.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.OnNetworkActiveListener;
import android.net.INetworkPolicyListener;
import android.net.INetworkPolicyListener.Stub;
import android.net.NetworkInfo;
import android.net.NetworkPolicyManager;
import android.os.UserHandle;
import com.android.internal.annotations.GuardedBy;
import com.android.server.job.JobSchedulerService;
import com.android.server.job.StateChangedListener;
import java.io.PrintWriter;
import java.util.ArrayList;

public class ConnectivityController
  extends StateController
  implements ConnectivityManager.OnNetworkActiveListener
{
  private static final String TAG = "JobScheduler.Conn";
  private static ConnectivityController mSingleton;
  private static Object sCreationLock = new Object();
  private final ConnectivityManager mConnManager = (ConnectivityManager)this.mContext.getSystemService(ConnectivityManager.class);
  private BroadcastReceiver mConnectivityReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      ConnectivityController.-wrap0(ConnectivityController.this, -1);
    }
  };
  private INetworkPolicyListener mNetPolicyListener = new INetworkPolicyListener.Stub()
  {
    public void onMeteredIfacesChanged(String[] paramAnonymousArrayOfString)
    {
      ConnectivityController.-wrap0(ConnectivityController.this, -1);
    }
    
    public void onRestrictBackgroundBlacklistChanged(int paramAnonymousInt, boolean paramAnonymousBoolean)
    {
      ConnectivityController.-wrap0(ConnectivityController.this, paramAnonymousInt);
    }
    
    public void onRestrictBackgroundChanged(boolean paramAnonymousBoolean)
    {
      ConnectivityController.-wrap0(ConnectivityController.this, -1);
    }
    
    public void onRestrictBackgroundWhitelistChanged(int paramAnonymousInt, boolean paramAnonymousBoolean)
    {
      ConnectivityController.-wrap0(ConnectivityController.this, paramAnonymousInt);
    }
    
    public void onUidRulesChanged(int paramAnonymousInt1, int paramAnonymousInt2)
    {
      ConnectivityController.-wrap0(ConnectivityController.this, paramAnonymousInt1);
    }
  };
  private final NetworkPolicyManager mNetPolicyManager = (NetworkPolicyManager)this.mContext.getSystemService(NetworkPolicyManager.class);
  @GuardedBy("mLock")
  private final ArrayList<JobStatus> mTrackedJobs = new ArrayList();
  
  private ConnectivityController(StateChangedListener paramStateChangedListener, Context paramContext, Object paramObject)
  {
    super(paramStateChangedListener, paramContext, paramObject);
    paramStateChangedListener = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
    this.mContext.registerReceiverAsUser(this.mConnectivityReceiver, UserHandle.SYSTEM, paramStateChangedListener, null, null);
    this.mNetPolicyManager.registerListener(this.mNetPolicyListener);
  }
  
  public static ConnectivityController get(JobSchedulerService paramJobSchedulerService)
  {
    synchronized (sCreationLock)
    {
      if (mSingleton == null) {
        mSingleton = new ConnectivityController(paramJobSchedulerService, paramJobSchedulerService.getContext(), paramJobSchedulerService.getLock());
      }
      paramJobSchedulerService = mSingleton;
      return paramJobSchedulerService;
    }
  }
  
  private boolean updateConstraintsSatisfied(JobStatus paramJobStatus)
  {
    boolean bool2 = false;
    boolean bool1;
    if ((paramJobStatus.getFlags() & 0x1) != 0)
    {
      bool1 = true;
      NetworkInfo localNetworkInfo = this.mConnManager.getActiveNetworkInfoForUid(paramJobStatus.getSourceUid(), bool1);
      bool1 = bool2;
      if (localNetworkInfo != null) {
        bool1 = localNetworkInfo.isConnected();
      }
      if ((bool1) && (!localNetworkInfo.isMetered())) {
        break label93;
      }
      bool2 = false;
      label54:
      if ((bool1) && (!localNetworkInfo.isRoaming())) {
        break label98;
      }
    }
    label93:
    label98:
    for (boolean bool3 = false;; bool3 = true)
    {
      return paramJobStatus.setConnectivityConstraintSatisfied(bool1) | paramJobStatus.setUnmeteredConstraintSatisfied(bool2) | paramJobStatus.setNotRoamingConstraintSatisfied(bool3);
      bool1 = false;
      break;
      bool2 = true;
      break label54;
    }
  }
  
  private void updateTrackedJobs(int paramInt)
  {
    Object localObject1 = this.mLock;
    boolean bool1 = false;
    int i = 0;
    for (;;)
    {
      boolean bool2;
      try
      {
        if (i < this.mTrackedJobs.size())
        {
          JobStatus localJobStatus = (JobStatus)this.mTrackedJobs.get(i);
          if (paramInt != -1)
          {
            bool2 = bool1;
            if (paramInt != localJobStatus.getSourceUid()) {}
          }
          else
          {
            bool2 = bool1 | updateConstraintsSatisfied(localJobStatus);
          }
        }
        else
        {
          if (bool1) {
            this.mStateChangedListener.onControllerStateChanged();
          }
          return;
        }
      }
      finally {}
      i += 1;
      bool1 = bool2;
    }
  }
  
  public void dumpControllerStateLocked(PrintWriter paramPrintWriter, int paramInt)
  {
    paramPrintWriter.println("Connectivity.");
    paramPrintWriter.print("Tracking ");
    paramPrintWriter.print(this.mTrackedJobs.size());
    paramPrintWriter.println(":");
    int i = 0;
    while (i < this.mTrackedJobs.size())
    {
      JobStatus localJobStatus = (JobStatus)this.mTrackedJobs.get(i);
      if (localJobStatus.shouldDump(paramInt))
      {
        paramPrintWriter.print("  #");
        localJobStatus.printUniqueId(paramPrintWriter);
        paramPrintWriter.print(" from ");
        UserHandle.formatUid(paramPrintWriter, localJobStatus.getSourceUid());
        paramPrintWriter.print(": C=");
        paramPrintWriter.print(localJobStatus.hasConnectivityConstraint());
        paramPrintWriter.print(": UM=");
        paramPrintWriter.print(localJobStatus.hasUnmeteredConstraint());
        paramPrintWriter.print(": NR=");
        paramPrintWriter.println(localJobStatus.hasNotRoamingConstraint());
      }
      i += 1;
    }
  }
  
  public void maybeStartTrackingJobLocked(JobStatus paramJobStatus1, JobStatus paramJobStatus2)
  {
    if ((paramJobStatus1.hasConnectivityConstraint()) || (paramJobStatus1.hasUnmeteredConstraint()) || (paramJobStatus1.hasNotRoamingConstraint()))
    {
      updateConstraintsSatisfied(paramJobStatus1);
      this.mTrackedJobs.add(paramJobStatus1);
    }
  }
  
  public void maybeStopTrackingJobLocked(JobStatus paramJobStatus1, JobStatus paramJobStatus2, boolean paramBoolean)
  {
    if ((paramJobStatus1.hasConnectivityConstraint()) || (paramJobStatus1.hasUnmeteredConstraint()) || (paramJobStatus1.hasNotRoamingConstraint())) {
      this.mTrackedJobs.remove(paramJobStatus1);
    }
  }
  
  /* Error */
  public void onNetworkActive()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 157	com/android/server/job/controllers/ConnectivityController:mLock	Ljava/lang/Object;
    //   6: astore_2
    //   7: aload_2
    //   8: monitorenter
    //   9: iconst_0
    //   10: istore_1
    //   11: iload_1
    //   12: aload_0
    //   13: getfield 56	com/android/server/job/controllers/ConnectivityController:mTrackedJobs	Ljava/util/ArrayList;
    //   16: invokevirtual 160	java/util/ArrayList:size	()I
    //   19: if_icmpge +39 -> 58
    //   22: aload_0
    //   23: getfield 56	com/android/server/job/controllers/ConnectivityController:mTrackedJobs	Ljava/util/ArrayList;
    //   26: iload_1
    //   27: invokevirtual 163	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   30: checkcast 122	com/android/server/job/controllers/JobStatus
    //   33: astore_3
    //   34: aload_3
    //   35: invokevirtual 242	com/android/server/job/controllers/JobStatus:isReady	()Z
    //   38: ifeq +13 -> 51
    //   41: aload_0
    //   42: getfield 169	com/android/server/job/controllers/ConnectivityController:mStateChangedListener	Lcom/android/server/job/StateChangedListener;
    //   45: aload_3
    //   46: invokeinterface 246 2 0
    //   51: iload_1
    //   52: iconst_1
    //   53: iadd
    //   54: istore_1
    //   55: goto -44 -> 11
    //   58: aload_2
    //   59: monitorexit
    //   60: aload_0
    //   61: monitorexit
    //   62: return
    //   63: astore_3
    //   64: aload_2
    //   65: monitorexit
    //   66: aload_3
    //   67: athrow
    //   68: astore_2
    //   69: aload_0
    //   70: monitorexit
    //   71: aload_2
    //   72: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	73	0	this	ConnectivityController
    //   10	45	1	i	int
    //   6	59	2	localObject1	Object
    //   68	4	2	localObject2	Object
    //   33	13	3	localJobStatus	JobStatus
    //   63	4	3	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   11	51	63	finally
    //   2	9	68	finally
    //   58	60	68	finally
    //   64	68	68	finally
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/job/controllers/ConnectivityController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */