package com.android.server.am;

import android.util.ArraySet;
import java.io.PrintWriter;

final class AppBindRecord
{
  final ProcessRecord client;
  final ArraySet<ConnectionRecord> connections = new ArraySet();
  final IntentBindRecord intent;
  final ServiceRecord service;
  
  AppBindRecord(ServiceRecord paramServiceRecord, IntentBindRecord paramIntentBindRecord, ProcessRecord paramProcessRecord)
  {
    this.service = paramServiceRecord;
    this.intent = paramIntentBindRecord;
    this.client = paramProcessRecord;
  }
  
  void dump(PrintWriter paramPrintWriter, String paramString)
  {
    paramPrintWriter.println(paramString + "service=" + this.service);
    paramPrintWriter.println(paramString + "client=" + this.client);
    dumpInIntentBind(paramPrintWriter, paramString);
  }
  
  void dumpInIntentBind(PrintWriter paramPrintWriter, String paramString)
  {
    int j = this.connections.size();
    if (j > 0)
    {
      paramPrintWriter.println(paramString + "Per-process Connections:");
      int i = 0;
      while (i < j)
      {
        ConnectionRecord localConnectionRecord = (ConnectionRecord)this.connections.valueAt(i);
        paramPrintWriter.println(paramString + "  " + localConnectionRecord);
        i += 1;
      }
    }
  }
  
  public String toString()
  {
    return "AppBindRecord{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.service.shortName + ":" + this.client.processName + "}";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/AppBindRecord.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */