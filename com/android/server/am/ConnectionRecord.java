package com.android.server.am;

import android.app.IServiceConnection;
import android.app.PendingIntent;
import java.io.PrintWriter;

final class ConnectionRecord
{
  final ActivityRecord activity;
  final AppBindRecord binding;
  final PendingIntent clientIntent;
  final int clientLabel;
  final IServiceConnection conn;
  final int flags;
  boolean serviceDead;
  String stringName;
  
  ConnectionRecord(AppBindRecord paramAppBindRecord, ActivityRecord paramActivityRecord, IServiceConnection paramIServiceConnection, int paramInt1, int paramInt2, PendingIntent paramPendingIntent)
  {
    this.binding = paramAppBindRecord;
    this.activity = paramActivityRecord;
    this.conn = paramIServiceConnection;
    this.flags = paramInt1;
    this.clientLabel = paramInt2;
    this.clientIntent = paramPendingIntent;
  }
  
  void dump(PrintWriter paramPrintWriter, String paramString)
  {
    paramPrintWriter.println(paramString + "binding=" + this.binding);
    if (this.activity != null) {
      paramPrintWriter.println(paramString + "activity=" + this.activity);
    }
    paramPrintWriter.println(paramString + "conn=" + this.conn.asBinder() + " flags=0x" + Integer.toHexString(this.flags));
  }
  
  public String toString()
  {
    if (this.stringName != null) {
      return this.stringName;
    }
    Object localObject = new StringBuilder(128);
    ((StringBuilder)localObject).append("ConnectionRecord{");
    ((StringBuilder)localObject).append(Integer.toHexString(System.identityHashCode(this)));
    ((StringBuilder)localObject).append(" u");
    ((StringBuilder)localObject).append(this.binding.client.userId);
    ((StringBuilder)localObject).append(' ');
    if ((this.flags & 0x1) != 0) {
      ((StringBuilder)localObject).append("CR ");
    }
    if ((this.flags & 0x2) != 0) {
      ((StringBuilder)localObject).append("DBG ");
    }
    if ((this.flags & 0x4) != 0) {
      ((StringBuilder)localObject).append("!FG ");
    }
    if ((this.flags & 0x8) != 0) {
      ((StringBuilder)localObject).append("ABCLT ");
    }
    if ((this.flags & 0x10) != 0) {
      ((StringBuilder)localObject).append("OOM ");
    }
    if ((this.flags & 0x20) != 0) {
      ((StringBuilder)localObject).append("WPRI ");
    }
    if ((this.flags & 0x40) != 0) {
      ((StringBuilder)localObject).append("IMP ");
    }
    if ((this.flags & 0x80) != 0) {
      ((StringBuilder)localObject).append("WACT ");
    }
    if ((this.flags & 0x2000000) != 0) {
      ((StringBuilder)localObject).append("FGSA ");
    }
    if ((this.flags & 0x4000000) != 0) {
      ((StringBuilder)localObject).append("FGS ");
    }
    if ((this.flags & 0x8000000) != 0) {
      ((StringBuilder)localObject).append("LACT ");
    }
    if ((this.flags & 0x10000000) != 0) {
      ((StringBuilder)localObject).append("VIS ");
    }
    if ((this.flags & 0x20000000) != 0) {
      ((StringBuilder)localObject).append("UI ");
    }
    if ((this.flags & 0x40000000) != 0) {
      ((StringBuilder)localObject).append("!VIS ");
    }
    if (this.serviceDead) {
      ((StringBuilder)localObject).append("DEAD ");
    }
    ((StringBuilder)localObject).append(this.binding.service.shortName);
    ((StringBuilder)localObject).append(":@");
    ((StringBuilder)localObject).append(Integer.toHexString(System.identityHashCode(this.conn.asBinder())));
    ((StringBuilder)localObject).append('}');
    localObject = ((StringBuilder)localObject).toString();
    this.stringName = ((String)localObject);
    return (String)localObject;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/ConnectionRecord.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */