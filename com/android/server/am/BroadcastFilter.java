package com.android.server.am;

import android.content.IntentFilter;
import android.util.PrintWriterPrinter;
import android.util.Printer;
import java.io.PrintWriter;

final class BroadcastFilter
  extends IntentFilter
{
  final int owningUid;
  final int owningUserId;
  final String packageName;
  final ReceiverList receiverList;
  final String requiredPermission;
  
  BroadcastFilter(IntentFilter paramIntentFilter, ReceiverList paramReceiverList, String paramString1, String paramString2, int paramInt1, int paramInt2)
  {
    super(paramIntentFilter);
    this.receiverList = paramReceiverList;
    this.packageName = paramString1;
    this.requiredPermission = paramString2;
    this.owningUid = paramInt1;
    this.owningUserId = paramInt2;
  }
  
  public void dump(PrintWriter paramPrintWriter, String paramString)
  {
    dumpInReceiverList(paramPrintWriter, new PrintWriterPrinter(paramPrintWriter), paramString);
    this.receiverList.dumpLocal(paramPrintWriter, paramString);
  }
  
  public void dumpBrief(PrintWriter paramPrintWriter, String paramString)
  {
    dumpBroadcastFilterState(paramPrintWriter, paramString);
  }
  
  void dumpBroadcastFilterState(PrintWriter paramPrintWriter, String paramString)
  {
    if (this.requiredPermission != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("requiredPermission=");
      paramPrintWriter.println(this.requiredPermission);
    }
  }
  
  public void dumpInReceiverList(PrintWriter paramPrintWriter, Printer paramPrinter, String paramString)
  {
    super.dump(paramPrinter, paramString);
    dumpBroadcastFilterState(paramPrintWriter, paramString);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("BroadcastFilter{");
    localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
    localStringBuilder.append(" u");
    localStringBuilder.append(this.owningUserId);
    localStringBuilder.append(' ');
    localStringBuilder.append(this.receiverList);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/BroadcastFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */