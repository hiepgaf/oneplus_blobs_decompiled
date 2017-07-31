package android.os.health;

import android.content.Context;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import com.android.internal.app.IBatteryStats;
import com.android.internal.app.IBatteryStats.Stub;

public class SystemHealthManager
{
  private final IBatteryStats mBatteryStats = IBatteryStats.Stub.asInterface(ServiceManager.getService("batterystats"));
  
  public static SystemHealthManager from(Context paramContext)
  {
    return (SystemHealthManager)paramContext.getSystemService("systemhealth");
  }
  
  public HealthStats takeMyUidSnapshot()
  {
    return takeUidSnapshot(Process.myUid());
  }
  
  public HealthStats takeUidSnapshot(int paramInt)
  {
    try
    {
      HealthStats localHealthStats = this.mBatteryStats.takeUidSnapshot(paramInt).getHealthStats();
      return localHealthStats;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException(localRemoteException);
    }
  }
  
  public HealthStats[] takeUidSnapshots(int[] paramArrayOfInt)
  {
    try
    {
      HealthStatsParceler[] arrayOfHealthStatsParceler = this.mBatteryStats.takeUidSnapshots(paramArrayOfInt);
      HealthStats[] arrayOfHealthStats = new HealthStats[paramArrayOfInt.length];
      int j = paramArrayOfInt.length;
      int i = 0;
      while (i < j)
      {
        arrayOfHealthStats[i] = arrayOfHealthStatsParceler[i].getHealthStats();
        i += 1;
      }
      return arrayOfHealthStats;
    }
    catch (RemoteException paramArrayOfInt)
    {
      throw new RuntimeException(paramArrayOfInt);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/health/SystemHealthManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */