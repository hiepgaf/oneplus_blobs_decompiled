package com.android.server.am;

import android.content.IIntentReceiver;
import android.os.Binder;
import android.os.IBinder.DeathRecipient;
import android.util.PrintWriterPrinter;
import java.io.PrintWriter;
import java.util.ArrayList;

final class ReceiverList
  extends ArrayList<BroadcastFilter>
  implements IBinder.DeathRecipient
{
  public final ProcessRecord app;
  BroadcastRecord curBroadcast = null;
  boolean linkedToDeath = false;
  final ActivityManagerService owner;
  public final int pid;
  public final IIntentReceiver receiver;
  String stringName;
  public final int uid;
  public final int userId;
  
  ReceiverList(ActivityManagerService paramActivityManagerService, ProcessRecord paramProcessRecord, int paramInt1, int paramInt2, int paramInt3, IIntentReceiver paramIIntentReceiver)
  {
    this.owner = paramActivityManagerService;
    this.receiver = paramIIntentReceiver;
    this.app = paramProcessRecord;
    this.pid = paramInt1;
    this.uid = paramInt2;
    this.userId = paramInt3;
  }
  
  public void binderDied()
  {
    this.linkedToDeath = false;
    this.owner.unregisterReceiver(this.receiver);
  }
  
  void dump(PrintWriter paramPrintWriter, String paramString)
  {
    PrintWriterPrinter localPrintWriterPrinter = new PrintWriterPrinter(paramPrintWriter);
    dumpLocal(paramPrintWriter, paramString);
    String str = paramString + "  ";
    int j = size();
    int i = 0;
    while (i < j)
    {
      BroadcastFilter localBroadcastFilter = (BroadcastFilter)get(i);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("Filter #");
      paramPrintWriter.print(i);
      paramPrintWriter.print(": BroadcastFilter{");
      paramPrintWriter.print(Integer.toHexString(System.identityHashCode(localBroadcastFilter)));
      paramPrintWriter.println('}');
      localBroadcastFilter.dumpInReceiverList(paramPrintWriter, localPrintWriterPrinter, str);
      i += 1;
    }
  }
  
  void dumpLocal(PrintWriter paramPrintWriter, String paramString)
  {
    String str = null;
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("app=");
    if (this.app != null) {
      str = this.app.toShortString();
    }
    paramPrintWriter.print(str);
    paramPrintWriter.print(" pid=");
    paramPrintWriter.print(this.pid);
    paramPrintWriter.print(" uid=");
    paramPrintWriter.print(this.uid);
    paramPrintWriter.print(" user=");
    paramPrintWriter.println(this.userId);
    if ((this.curBroadcast != null) || (this.linkedToDeath))
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("curBroadcast=");
      paramPrintWriter.print(this.curBroadcast);
      paramPrintWriter.print(" linkedToDeath=");
      paramPrintWriter.println(this.linkedToDeath);
    }
  }
  
  public boolean equals(Object paramObject)
  {
    return this == paramObject;
  }
  
  public int hashCode()
  {
    return System.identityHashCode(this);
  }
  
  public String toString()
  {
    if (this.stringName != null) {
      return this.stringName;
    }
    StringBuilder localStringBuilder = new StringBuilder(128);
    localStringBuilder.append("ReceiverList{");
    localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
    localStringBuilder.append(' ');
    localStringBuilder.append(this.pid);
    localStringBuilder.append(' ');
    if (this.app != null)
    {
      str = this.app.processName;
      localStringBuilder.append(str);
      localStringBuilder.append('/');
      localStringBuilder.append(this.uid);
      localStringBuilder.append("/u");
      localStringBuilder.append(this.userId);
      if (!(this.receiver.asBinder() instanceof Binder)) {
        break label187;
      }
    }
    label187:
    for (String str = " local:";; str = " remote:")
    {
      localStringBuilder.append(str);
      localStringBuilder.append(Integer.toHexString(System.identityHashCode(this.receiver.asBinder())));
      localStringBuilder.append('}');
      str = localStringBuilder.toString();
      this.stringName = str;
      return str;
      str = "(unknown name)";
      break;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/ReceiverList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */