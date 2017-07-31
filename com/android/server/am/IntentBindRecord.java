package com.android.server.am;

import android.content.Intent;
import android.content.Intent.FilterComparison;
import android.os.IBinder;
import android.util.ArrayMap;
import android.util.ArraySet;
import java.io.PrintWriter;

final class IntentBindRecord
{
  final ArrayMap<ProcessRecord, AppBindRecord> apps = new ArrayMap();
  IBinder binder;
  boolean doRebind;
  boolean hasBound;
  final Intent.FilterComparison intent;
  boolean received;
  boolean requested;
  final ServiceRecord service;
  String stringName;
  
  IntentBindRecord(ServiceRecord paramServiceRecord, Intent.FilterComparison paramFilterComparison)
  {
    this.service = paramServiceRecord;
    this.intent = paramFilterComparison;
  }
  
  int collectFlags()
  {
    int j = 0;
    int i = this.apps.size() - 1;
    while (i >= 0)
    {
      ArraySet localArraySet = ((AppBindRecord)this.apps.valueAt(i)).connections;
      int k = localArraySet.size() - 1;
      while (k >= 0)
      {
        j |= ((ConnectionRecord)localArraySet.valueAt(k)).flags;
        k -= 1;
      }
      i -= 1;
    }
    return j;
  }
  
  void dump(PrintWriter paramPrintWriter, String paramString)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("service=");
    paramPrintWriter.println(this.service);
    dumpInService(paramPrintWriter, paramString);
  }
  
  void dumpInService(PrintWriter paramPrintWriter, String paramString)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("intent={");
    paramPrintWriter.print(this.intent.getIntent().toShortString(false, true, false, false));
    paramPrintWriter.println('}');
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("binder=");
    paramPrintWriter.println(this.binder);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("requested=");
    paramPrintWriter.print(this.requested);
    paramPrintWriter.print(" received=");
    paramPrintWriter.print(this.received);
    paramPrintWriter.print(" hasBound=");
    paramPrintWriter.print(this.hasBound);
    paramPrintWriter.print(" doRebind=");
    paramPrintWriter.println(this.doRebind);
    int i = 0;
    while (i < this.apps.size())
    {
      AppBindRecord localAppBindRecord = (AppBindRecord)this.apps.valueAt(i);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("* Client AppBindRecord{");
      paramPrintWriter.print(Integer.toHexString(System.identityHashCode(localAppBindRecord)));
      paramPrintWriter.print(' ');
      paramPrintWriter.print(localAppBindRecord.client);
      paramPrintWriter.println('}');
      localAppBindRecord.dumpInIntentBind(paramPrintWriter, paramString + "  ");
      i += 1;
    }
  }
  
  public String toString()
  {
    if (this.stringName != null) {
      return this.stringName;
    }
    Object localObject = new StringBuilder(128);
    ((StringBuilder)localObject).append("IntentBindRecord{");
    ((StringBuilder)localObject).append(Integer.toHexString(System.identityHashCode(this)));
    ((StringBuilder)localObject).append(' ');
    if ((collectFlags() & 0x1) != 0) {
      ((StringBuilder)localObject).append("CR ");
    }
    ((StringBuilder)localObject).append(this.service.shortName);
    ((StringBuilder)localObject).append(':');
    if (this.intent != null) {
      this.intent.getIntent().toShortString((StringBuilder)localObject, false, false, false, false);
    }
    ((StringBuilder)localObject).append('}');
    localObject = ((StringBuilder)localObject).toString();
    this.stringName = ((String)localObject);
    return (String)localObject;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/IntentBindRecord.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */