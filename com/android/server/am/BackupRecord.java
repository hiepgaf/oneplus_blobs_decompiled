package com.android.server.am;

import android.content.pm.ApplicationInfo;
import com.android.internal.os.BatteryStatsImpl.Uid.Pkg.Serv;

final class BackupRecord
{
  public static final int BACKUP_FULL = 1;
  public static final int BACKUP_NORMAL = 0;
  public static final int RESTORE = 2;
  public static final int RESTORE_FULL = 3;
  ProcessRecord app;
  final ApplicationInfo appInfo;
  final int backupMode;
  final BatteryStatsImpl.Uid.Pkg.Serv stats;
  String stringName;
  
  BackupRecord(BatteryStatsImpl.Uid.Pkg.Serv paramServ, ApplicationInfo paramApplicationInfo, int paramInt)
  {
    this.stats = paramServ;
    this.appInfo = paramApplicationInfo;
    this.backupMode = paramInt;
  }
  
  public String toString()
  {
    if (this.stringName != null) {
      return this.stringName;
    }
    Object localObject = new StringBuilder(128);
    ((StringBuilder)localObject).append("BackupRecord{").append(Integer.toHexString(System.identityHashCode(this))).append(' ').append(this.appInfo.packageName).append(' ').append(this.appInfo.name).append(' ').append(this.appInfo.backupAgentName).append('}');
    localObject = ((StringBuilder)localObject).toString();
    this.stringName = ((String)localObject);
    return (String)localObject;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/BackupRecord.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */