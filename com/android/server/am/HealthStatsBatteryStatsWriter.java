package com.android.server.am;

import android.os.BatteryStats;
import android.os.BatteryStats.ControllerActivityCounter;
import android.os.BatteryStats.Counter;
import android.os.BatteryStats.LongCounter;
import android.os.BatteryStats.Timer;
import android.os.BatteryStats.Uid;
import android.os.BatteryStats.Uid.Pid;
import android.os.BatteryStats.Uid.Pkg;
import android.os.BatteryStats.Uid.Pkg.Serv;
import android.os.BatteryStats.Uid.Proc;
import android.os.BatteryStats.Uid.Sensor;
import android.os.BatteryStats.Uid.Wakelock;
import android.os.SystemClock;
import android.os.health.HealthStatsWriter;
import android.os.health.PackageHealthStats;
import android.os.health.PidHealthStats;
import android.os.health.ProcessHealthStats;
import android.os.health.ServiceHealthStats;
import android.os.health.TimerStat;
import android.util.ArrayMap;
import android.util.SparseArray;
import java.util.Iterator;
import java.util.Map.Entry;

public class HealthStatsBatteryStatsWriter
{
  private final long mNowRealtimeMs = SystemClock.elapsedRealtime();
  private final long mNowUptimeMs = SystemClock.uptimeMillis();
  
  private void addTimer(HealthStatsWriter paramHealthStatsWriter, int paramInt, BatteryStats.Timer paramTimer)
  {
    if (paramTimer != null) {
      paramHealthStatsWriter.addTimer(paramInt, paramTimer.getCountLocked(2), paramTimer.getTotalTimeLocked(this.mNowRealtimeMs * 1000L, 2) / 1000L);
    }
  }
  
  private void addTimers(HealthStatsWriter paramHealthStatsWriter, int paramInt, String paramString, BatteryStats.Timer paramTimer)
  {
    if (paramTimer != null) {
      paramHealthStatsWriter.addTimers(paramInt, paramString, new TimerStat(paramTimer.getCountLocked(2), paramTimer.getTotalTimeLocked(this.mNowRealtimeMs * 1000L, 2) / 1000L));
    }
  }
  
  public void writePid(HealthStatsWriter paramHealthStatsWriter, BatteryStats.Uid.Pid paramPid)
  {
    if (paramPid == null) {
      return;
    }
    paramHealthStatsWriter.addMeasurement(20001, paramPid.mWakeNesting);
    paramHealthStatsWriter.addMeasurement(20002, paramPid.mWakeSumMs);
    paramHealthStatsWriter.addMeasurement(20002, paramPid.mWakeStartMs);
  }
  
  public void writePkg(HealthStatsWriter paramHealthStatsWriter, BatteryStats.Uid.Pkg paramPkg)
  {
    Object localObject1 = paramPkg.getServiceStats().entrySet().iterator();
    Object localObject2;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Map.Entry)((Iterator)localObject1).next();
      HealthStatsWriter localHealthStatsWriter = new HealthStatsWriter(ServiceHealthStats.CONSTANTS);
      writeServ(localHealthStatsWriter, (BatteryStats.Uid.Pkg.Serv)((Map.Entry)localObject2).getValue());
      paramHealthStatsWriter.addStats(40001, (String)((Map.Entry)localObject2).getKey(), localHealthStatsWriter);
    }
    paramPkg = paramPkg.getWakeupAlarmStats().entrySet().iterator();
    while (paramPkg.hasNext())
    {
      localObject1 = (Map.Entry)paramPkg.next();
      localObject2 = (BatteryStats.Counter)((Map.Entry)localObject1).getValue();
      if (localObject2 != null) {
        paramHealthStatsWriter.addMeasurements(40002, (String)((Map.Entry)localObject1).getKey(), ((BatteryStats.Counter)localObject2).getCountLocked(2));
      }
    }
  }
  
  public void writeProc(HealthStatsWriter paramHealthStatsWriter, BatteryStats.Uid.Proc paramProc)
  {
    paramHealthStatsWriter.addMeasurement(30001, paramProc.getUserTime(2));
    paramHealthStatsWriter.addMeasurement(30002, paramProc.getSystemTime(2));
    paramHealthStatsWriter.addMeasurement(30003, paramProc.getStarts(2));
    paramHealthStatsWriter.addMeasurement(30004, paramProc.getNumCrashes(2));
    paramHealthStatsWriter.addMeasurement(30005, paramProc.getNumAnrs(2));
    paramHealthStatsWriter.addMeasurement(30006, paramProc.getForegroundTime(2));
  }
  
  public void writeServ(HealthStatsWriter paramHealthStatsWriter, BatteryStats.Uid.Pkg.Serv paramServ)
  {
    paramHealthStatsWriter.addMeasurement(50001, paramServ.getStarts(2));
    paramHealthStatsWriter.addMeasurement(50002, paramServ.getLaunches(2));
  }
  
  public void writeUid(HealthStatsWriter paramHealthStatsWriter, BatteryStats paramBatteryStats, BatteryStats.Uid paramUid)
  {
    paramHealthStatsWriter.addMeasurement(10001, paramBatteryStats.computeBatteryRealtime(this.mNowRealtimeMs * 1000L, 2) / 1000L);
    paramHealthStatsWriter.addMeasurement(10002, paramBatteryStats.computeBatteryUptime(this.mNowUptimeMs * 1000L, 2) / 1000L);
    paramHealthStatsWriter.addMeasurement(10003, paramBatteryStats.computeBatteryScreenOffRealtime(this.mNowRealtimeMs * 1000L, 2) / 1000L);
    paramHealthStatsWriter.addMeasurement(10004, paramBatteryStats.computeBatteryScreenOffUptime(this.mNowUptimeMs * 1000L, 2) / 1000L);
    paramBatteryStats = paramUid.getWakelockStats().entrySet().iterator();
    Object localObject2;
    Object localObject1;
    while (paramBatteryStats.hasNext())
    {
      localObject2 = (Map.Entry)paramBatteryStats.next();
      localObject1 = (String)((Map.Entry)localObject2).getKey();
      localObject2 = (BatteryStats.Uid.Wakelock)((Map.Entry)localObject2).getValue();
      addTimers(paramHealthStatsWriter, 10005, (String)localObject1, ((BatteryStats.Uid.Wakelock)localObject2).getWakeTime(1));
      addTimers(paramHealthStatsWriter, 10006, (String)localObject1, ((BatteryStats.Uid.Wakelock)localObject2).getWakeTime(0));
      addTimers(paramHealthStatsWriter, 10007, (String)localObject1, ((BatteryStats.Uid.Wakelock)localObject2).getWakeTime(2));
      addTimers(paramHealthStatsWriter, 10008, (String)localObject1, ((BatteryStats.Uid.Wakelock)localObject2).getWakeTime(18));
    }
    paramBatteryStats = paramUid.getSyncStats().entrySet().iterator();
    while (paramBatteryStats.hasNext())
    {
      localObject1 = (Map.Entry)paramBatteryStats.next();
      addTimers(paramHealthStatsWriter, 10009, (String)((Map.Entry)localObject1).getKey(), (BatteryStats.Timer)((Map.Entry)localObject1).getValue());
    }
    paramBatteryStats = paramUid.getJobStats().entrySet().iterator();
    while (paramBatteryStats.hasNext())
    {
      localObject1 = (Map.Entry)paramBatteryStats.next();
      addTimers(paramHealthStatsWriter, 10010, (String)((Map.Entry)localObject1).getKey(), (BatteryStats.Timer)((Map.Entry)localObject1).getValue());
    }
    paramBatteryStats = paramUid.getSensorStats();
    int j = paramBatteryStats.size();
    int i = 0;
    if (i < j)
    {
      int k = paramBatteryStats.keyAt(i);
      if (k == 55536) {
        addTimer(paramHealthStatsWriter, 10011, ((BatteryStats.Uid.Sensor)paramBatteryStats.valueAt(i)).getSensorTime());
      }
      for (;;)
      {
        i += 1;
        break;
        addTimers(paramHealthStatsWriter, 10012, Integer.toString(k), ((BatteryStats.Uid.Sensor)paramBatteryStats.valueAt(i)).getSensorTime());
      }
    }
    paramBatteryStats = paramUid.getPidStats();
    j = paramBatteryStats.size();
    i = 0;
    while (i < j)
    {
      localObject1 = new HealthStatsWriter(PidHealthStats.CONSTANTS);
      writePid((HealthStatsWriter)localObject1, (BatteryStats.Uid.Pid)paramBatteryStats.valueAt(i));
      paramHealthStatsWriter.addStats(10013, Integer.toString(paramBatteryStats.keyAt(i)), (HealthStatsWriter)localObject1);
      i += 1;
    }
    paramBatteryStats = paramUid.getProcessStats().entrySet().iterator();
    while (paramBatteryStats.hasNext())
    {
      localObject1 = (Map.Entry)paramBatteryStats.next();
      localObject2 = new HealthStatsWriter(ProcessHealthStats.CONSTANTS);
      writeProc((HealthStatsWriter)localObject2, (BatteryStats.Uid.Proc)((Map.Entry)localObject1).getValue());
      paramHealthStatsWriter.addStats(10014, (String)((Map.Entry)localObject1).getKey(), (HealthStatsWriter)localObject2);
    }
    paramBatteryStats = paramUid.getPackageStats().entrySet().iterator();
    while (paramBatteryStats.hasNext())
    {
      localObject1 = (Map.Entry)paramBatteryStats.next();
      localObject2 = new HealthStatsWriter(PackageHealthStats.CONSTANTS);
      writePkg((HealthStatsWriter)localObject2, (BatteryStats.Uid.Pkg)((Map.Entry)localObject1).getValue());
      paramHealthStatsWriter.addStats(10015, (String)((Map.Entry)localObject1).getKey(), (HealthStatsWriter)localObject2);
    }
    paramBatteryStats = paramUid.getWifiControllerActivity();
    long l;
    if (paramBatteryStats != null)
    {
      paramHealthStatsWriter.addMeasurement(10016, paramBatteryStats.getIdleTimeCounter().getCountLocked(2));
      paramHealthStatsWriter.addMeasurement(10017, paramBatteryStats.getRxTimeCounter().getCountLocked(2));
      l = 0L;
      localObject1 = paramBatteryStats.getTxTimeCounters();
      i = 0;
      j = localObject1.length;
      while (i < j)
      {
        l += localObject1[i].getCountLocked(2);
        i += 1;
      }
      paramHealthStatsWriter.addMeasurement(10018, l);
      paramHealthStatsWriter.addMeasurement(10019, paramBatteryStats.getPowerCounter().getCountLocked(2));
    }
    paramBatteryStats = paramUid.getBluetoothControllerActivity();
    if (paramBatteryStats != null)
    {
      paramHealthStatsWriter.addMeasurement(10020, paramBatteryStats.getIdleTimeCounter().getCountLocked(2));
      paramHealthStatsWriter.addMeasurement(10021, paramBatteryStats.getRxTimeCounter().getCountLocked(2));
      l = 0L;
      localObject1 = paramBatteryStats.getTxTimeCounters();
      i = 0;
      j = localObject1.length;
      while (i < j)
      {
        l += localObject1[i].getCountLocked(2);
        i += 1;
      }
      paramHealthStatsWriter.addMeasurement(10022, l);
      paramHealthStatsWriter.addMeasurement(10023, paramBatteryStats.getPowerCounter().getCountLocked(2));
    }
    paramBatteryStats = paramUid.getModemControllerActivity();
    if (paramBatteryStats != null)
    {
      paramHealthStatsWriter.addMeasurement(10024, paramBatteryStats.getIdleTimeCounter().getCountLocked(2));
      paramHealthStatsWriter.addMeasurement(10025, paramBatteryStats.getRxTimeCounter().getCountLocked(2));
      l = 0L;
      localObject1 = paramBatteryStats.getTxTimeCounters();
      i = 0;
      j = localObject1.length;
      while (i < j)
      {
        l += localObject1[i].getCountLocked(2);
        i += 1;
      }
      paramHealthStatsWriter.addMeasurement(10026, l);
      paramHealthStatsWriter.addMeasurement(10027, paramBatteryStats.getPowerCounter().getCountLocked(2));
    }
    paramHealthStatsWriter.addMeasurement(10028, paramUid.getWifiRunningTime(this.mNowRealtimeMs * 1000L, 2) / 1000L);
    paramHealthStatsWriter.addMeasurement(10029, paramUid.getFullWifiLockTime(this.mNowRealtimeMs * 1000L, 2) / 1000L);
    paramHealthStatsWriter.addTimer(10030, paramUid.getWifiScanCount(2), paramUid.getWifiScanTime(this.mNowRealtimeMs * 1000L, 2) / 1000L);
    paramHealthStatsWriter.addMeasurement(10031, paramUid.getWifiMulticastTime(this.mNowRealtimeMs * 1000L, 2) / 1000L);
    addTimer(paramHealthStatsWriter, 10032, paramUid.getAudioTurnedOnTimer());
    addTimer(paramHealthStatsWriter, 10033, paramUid.getVideoTurnedOnTimer());
    addTimer(paramHealthStatsWriter, 10034, paramUid.getFlashlightTurnedOnTimer());
    addTimer(paramHealthStatsWriter, 10035, paramUid.getCameraTurnedOnTimer());
    addTimer(paramHealthStatsWriter, 10036, paramUid.getForegroundActivityTimer());
    addTimer(paramHealthStatsWriter, 10037, paramUid.getBluetoothScanTimer());
    addTimer(paramHealthStatsWriter, 10038, paramUid.getProcessStateTimer(0));
    addTimer(paramHealthStatsWriter, 10039, paramUid.getProcessStateTimer(1));
    addTimer(paramHealthStatsWriter, 10040, paramUid.getProcessStateTimer(2));
    addTimer(paramHealthStatsWriter, 10041, paramUid.getProcessStateTimer(3));
    addTimer(paramHealthStatsWriter, 10042, paramUid.getProcessStateTimer(4));
    addTimer(paramHealthStatsWriter, 10043, paramUid.getProcessStateTimer(5));
    addTimer(paramHealthStatsWriter, 10044, paramUid.getVibratorOnTimer());
    paramHealthStatsWriter.addMeasurement(10045, paramUid.getUserActivityCount(0, 2));
    paramHealthStatsWriter.addMeasurement(10046, paramUid.getUserActivityCount(1, 2));
    paramHealthStatsWriter.addMeasurement(10047, paramUid.getUserActivityCount(2, 2));
    paramHealthStatsWriter.addMeasurement(10048, paramUid.getNetworkActivityBytes(0, 2));
    paramHealthStatsWriter.addMeasurement(10049, paramUid.getNetworkActivityBytes(1, 2));
    paramHealthStatsWriter.addMeasurement(10050, paramUid.getNetworkActivityBytes(2, 2));
    paramHealthStatsWriter.addMeasurement(10051, paramUid.getNetworkActivityBytes(3, 2));
    paramHealthStatsWriter.addMeasurement(10052, paramUid.getNetworkActivityBytes(4, 2));
    paramHealthStatsWriter.addMeasurement(10053, paramUid.getNetworkActivityBytes(5, 2));
    paramHealthStatsWriter.addMeasurement(10054, paramUid.getNetworkActivityPackets(0, 2));
    paramHealthStatsWriter.addMeasurement(10055, paramUid.getNetworkActivityPackets(1, 2));
    paramHealthStatsWriter.addMeasurement(10056, paramUid.getNetworkActivityPackets(2, 2));
    paramHealthStatsWriter.addMeasurement(10057, paramUid.getNetworkActivityPackets(3, 2));
    paramHealthStatsWriter.addMeasurement(10058, paramUid.getNetworkActivityPackets(4, 2));
    paramHealthStatsWriter.addMeasurement(10059, paramUid.getNetworkActivityPackets(5, 2));
    paramHealthStatsWriter.addTimer(10061, paramUid.getMobileRadioActiveCount(2), paramUid.getMobileRadioActiveTime(2));
    paramHealthStatsWriter.addMeasurement(10062, paramUid.getUserCpuTimeUs(2) / 1000L);
    paramHealthStatsWriter.addMeasurement(10063, paramUid.getSystemCpuTimeUs(2) / 1000L);
    paramHealthStatsWriter.addMeasurement(10064, paramUid.getCpuPowerMaUs(2) / 1000L);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/HealthStatsBatteryStatsWriter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */