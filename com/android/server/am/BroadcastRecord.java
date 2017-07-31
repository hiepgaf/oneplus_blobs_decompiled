package com.android.server.am;

import android.app.BroadcastOptions;
import android.content.ComponentName;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.PrintWriterPrinter;
import android.util.Printer;
import android.util.TimeUtils;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

final class BroadcastRecord
  extends Binder
{
  static final int APP_RECEIVE = 1;
  static final int CALL_DONE_RECEIVE = 3;
  static final int CALL_IN_RECEIVE = 2;
  static final int DELIVERY_DELIVERED = 1;
  static final int DELIVERY_PENDING = 0;
  static final int DELIVERY_SKIPPED = 2;
  static final int DELIVERY_TIMEOUT = 3;
  static final int IDLE = 0;
  static final int WAITING_SERVICES = 4;
  int anrCount;
  final int appOp;
  final ProcessRecord callerApp;
  final String callerPackage;
  final int callingPid;
  final int callingUid;
  ProcessRecord curApp;
  ComponentName curComponent;
  BroadcastFilter curFilter;
  ActivityInfo curReceiver;
  final int[] delivery;
  long dispatchClockTime;
  long dispatchTime;
  long enqueueClockTime;
  long finishTime;
  final boolean initialSticky;
  final Intent intent;
  int manifestCount;
  int manifestSkipCount;
  int nextReceiver;
  final BroadcastOptions options;
  final boolean ordered;
  BroadcastQueue queue;
  IBinder receiver;
  long receiverTime;
  final List receivers;
  final String[] requiredPermissions;
  final String resolvedType;
  boolean resultAbort;
  int resultCode;
  String resultData;
  Bundle resultExtras;
  IIntentReceiver resultTo;
  int state;
  final boolean sticky;
  final ComponentName targetComp;
  final int userId;
  
  BroadcastRecord(BroadcastQueue paramBroadcastQueue, Intent paramIntent, ProcessRecord paramProcessRecord, String paramString1, int paramInt1, int paramInt2, String paramString2, String[] paramArrayOfString, int paramInt3, BroadcastOptions paramBroadcastOptions, List paramList, IIntentReceiver paramIIntentReceiver, int paramInt4, String paramString3, Bundle paramBundle, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt5)
  {
    if (paramIntent == null) {
      throw new NullPointerException("Can't construct with a null intent");
    }
    this.queue = paramBroadcastQueue;
    this.intent = paramIntent;
    this.targetComp = paramIntent.getComponent();
    this.callerApp = paramProcessRecord;
    this.callerPackage = paramString1;
    this.callingPid = paramInt1;
    this.callingUid = paramInt2;
    this.resolvedType = paramString2;
    this.requiredPermissions = paramArrayOfString;
    this.appOp = paramInt3;
    this.options = paramBroadcastOptions;
    this.receivers = paramList;
    if (paramList != null) {}
    for (paramInt1 = paramList.size();; paramInt1 = 0)
    {
      this.delivery = new int[paramInt1];
      this.resultTo = paramIIntentReceiver;
      this.resultCode = paramInt4;
      this.resultData = paramString3;
      this.resultExtras = paramBundle;
      this.ordered = paramBoolean1;
      this.sticky = paramBoolean2;
      this.initialSticky = paramBoolean3;
      this.userId = paramInt5;
      this.nextReceiver = 0;
      this.state = 0;
      return;
    }
  }
  
  boolean cleanupDisabledPackageReceiversLocked(String paramString, Set<String> paramSet, int paramInt, boolean paramBoolean)
  {
    if (paramInt != -1) {}
    for (;;)
    {
      boolean bool2;
      try
      {
        int i = this.userId;
        if (i != paramInt) {
          return false;
        }
        if (this.receivers == null) {
          continue;
        }
        bool2 = false;
        paramInt = this.receivers.size() - 1;
        if (paramInt >= 0)
        {
          Object localObject = this.receivers.get(paramInt);
          if (!(localObject instanceof ResolveInfo)) {
            break label223;
          }
          localObject = ((ResolveInfo)localObject).activityInfo;
          if (paramString != null)
          {
            if (!((ActivityInfo)localObject).applicationInfo.packageName.equals(paramString)) {
              continue;
            }
            if (paramSet != null)
            {
              bool1 = paramSet.contains(((ActivityInfo)localObject).name);
              if (!bool1) {
                break label223;
              }
              if (paramBoolean) {
                continue;
              }
              return true;
            }
          }
          else
          {
            bool1 = true;
            continue;
          }
          boolean bool1 = true;
          continue;
          bool1 = false;
          continue;
          bool1 = true;
          this.receivers.remove(paramInt);
          bool2 = bool1;
          if (paramInt >= this.nextReceiver) {
            break label223;
          }
          this.nextReceiver -= 1;
          bool2 = bool1;
        }
      }
      finally {}
      this.nextReceiver = Math.min(this.nextReceiver, this.receivers.size());
      return bool2;
      label223:
      paramInt -= 1;
    }
  }
  
  void dump(PrintWriter paramPrintWriter, String paramString, SimpleDateFormat paramSimpleDateFormat)
  {
    long l = SystemClock.uptimeMillis();
    paramPrintWriter.print(paramString);
    paramPrintWriter.print(this);
    paramPrintWriter.print(" to user ");
    paramPrintWriter.println(this.userId);
    paramPrintWriter.print(paramString);
    paramPrintWriter.println(this.intent.toInsecureString());
    if ((this.targetComp != null) && (this.targetComp != this.intent.getComponent()))
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("  targetComp: ");
      paramPrintWriter.println(this.targetComp.toShortString());
    }
    Object localObject1 = this.intent.getExtras();
    if (localObject1 != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("  extras: ");
      paramPrintWriter.println(((Bundle)localObject1).toString());
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("caller=");
    paramPrintWriter.print(this.callerPackage);
    paramPrintWriter.print(" ");
    label212:
    label249:
    label438:
    label487:
    label537:
    label814:
    label908:
    int i;
    label951:
    int j;
    label985:
    Object localObject2;
    if (this.callerApp != null)
    {
      localObject1 = this.callerApp.toShortString();
      paramPrintWriter.print((String)localObject1);
      paramPrintWriter.print(" pid=");
      paramPrintWriter.print(this.callingPid);
      paramPrintWriter.print(" uid=");
      paramPrintWriter.println(this.callingUid);
      if ((this.requiredPermissions == null) || (this.requiredPermissions.length <= 0)) {
        break label1116;
      }
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("requiredPermissions=");
      paramPrintWriter.print(Arrays.toString(this.requiredPermissions));
      paramPrintWriter.print("  appOp=");
      paramPrintWriter.println(this.appOp);
      if (this.options != null)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("options=");
        paramPrintWriter.println(this.options.toBundle());
      }
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("enqueueClockTime=");
      paramPrintWriter.print(paramSimpleDateFormat.format(new Date(this.enqueueClockTime)));
      paramPrintWriter.print(" dispatchClockTime=");
      paramPrintWriter.println(paramSimpleDateFormat.format(new Date(this.dispatchClockTime)));
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("dispatchTime=");
      TimeUtils.formatDuration(this.dispatchTime, l, paramPrintWriter);
      paramPrintWriter.print(" (");
      TimeUtils.formatDuration(this.dispatchClockTime - this.enqueueClockTime, paramPrintWriter);
      paramPrintWriter.print(" since enq)");
      if (this.finishTime == 0L) {
        break label1127;
      }
      paramPrintWriter.print(" finishTime=");
      TimeUtils.formatDuration(this.finishTime, l, paramPrintWriter);
      paramPrintWriter.print(" (");
      TimeUtils.formatDuration(this.finishTime - this.dispatchTime, paramPrintWriter);
      paramPrintWriter.print(" since disp)");
      paramPrintWriter.println("");
      if (this.anrCount != 0)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("anrCount=");
        paramPrintWriter.println(this.anrCount);
      }
      if ((this.resultTo == null) && (this.resultCode == -1)) {
        break label1147;
      }
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("resultTo=");
      paramPrintWriter.print(this.resultTo);
      paramPrintWriter.print(" resultCode=");
      paramPrintWriter.print(this.resultCode);
      paramPrintWriter.print(" resultData=");
      paramPrintWriter.println(this.resultData);
      if (this.resultExtras != null)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("resultExtras=");
        paramPrintWriter.println(this.resultExtras);
      }
      if ((this.resultAbort) || (this.ordered) || (this.sticky) || (this.initialSticky))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("resultAbort=");
        paramPrintWriter.print(this.resultAbort);
        paramPrintWriter.print(" ordered=");
        paramPrintWriter.print(this.ordered);
        paramPrintWriter.print(" sticky=");
        paramPrintWriter.print(this.sticky);
        paramPrintWriter.print(" initialSticky=");
        paramPrintWriter.println(this.initialSticky);
      }
      if ((this.nextReceiver != 0) || (this.receiver != null))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("nextReceiver=");
        paramPrintWriter.print(this.nextReceiver);
        paramPrintWriter.print(" receiver=");
        paramPrintWriter.println(this.receiver);
      }
      if (this.curFilter != null)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("curFilter=");
        paramPrintWriter.println(this.curFilter);
      }
      if (this.curReceiver != null)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("curReceiver=");
        paramPrintWriter.println(this.curReceiver);
      }
      if (this.curApp != null)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("curApp=");
        paramPrintWriter.println(this.curApp);
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("curComponent=");
        if (this.curComponent == null) {
          break label1157;
        }
        paramSimpleDateFormat = this.curComponent.toShortString();
        paramPrintWriter.println(paramSimpleDateFormat);
        if ((this.curReceiver != null) && (this.curReceiver.applicationInfo != null))
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("curSourceDir=");
          paramPrintWriter.println(this.curReceiver.applicationInfo.sourceDir);
        }
      }
      if (this.state != 0) {
        paramSimpleDateFormat = " (?)";
      }
      switch (this.state)
      {
      default: 
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("state=");
        paramPrintWriter.print(this.state);
        paramPrintWriter.println(paramSimpleDateFormat);
        if (this.receivers != null)
        {
          i = this.receivers.size();
          paramSimpleDateFormat = paramString + "  ";
          localObject1 = new PrintWriterPrinter(paramPrintWriter);
          j = 0;
          if (j >= i) {
            return;
          }
          localObject2 = this.receivers.get(j);
          paramPrintWriter.print(paramString);
          switch (this.delivery[j])
          {
          default: 
            paramPrintWriter.print("???????");
            label1055:
            paramPrintWriter.print(" #");
            paramPrintWriter.print(j);
            paramPrintWriter.print(": ");
            if ((localObject2 instanceof BroadcastFilter))
            {
              paramPrintWriter.println(localObject2);
              ((BroadcastFilter)localObject2).dumpBrief(paramPrintWriter, paramSimpleDateFormat);
            }
            break;
          }
        }
        break;
      }
    }
    for (;;)
    {
      j += 1;
      break label985;
      localObject1 = "null";
      break;
      label1116:
      if (this.appOp == -1) {
        break label249;
      }
      break label212;
      label1127:
      paramPrintWriter.print(" receiverTime=");
      TimeUtils.formatDuration(this.receiverTime, l, paramPrintWriter);
      break label438;
      label1147:
      if (this.resultData == null) {
        break label537;
      }
      break label487;
      label1157:
      paramSimpleDateFormat = "--";
      break label814;
      paramSimpleDateFormat = " (APP_RECEIVE)";
      break label908;
      paramSimpleDateFormat = " (CALL_IN_RECEIVE)";
      break label908;
      paramSimpleDateFormat = " (CALL_DONE_RECEIVE)";
      break label908;
      paramSimpleDateFormat = " (WAITING_SERVICES)";
      break label908;
      i = 0;
      break label951;
      paramPrintWriter.print("Pending");
      break label1055;
      paramPrintWriter.print("Deliver");
      break label1055;
      paramPrintWriter.print("Skipped");
      break label1055;
      paramPrintWriter.print("Timeout");
      break label1055;
      if ((localObject2 instanceof ResolveInfo))
      {
        paramPrintWriter.println("(manifest)");
        ((ResolveInfo)localObject2).dump((Printer)localObject1, paramSimpleDateFormat, 0);
      }
      else
      {
        paramPrintWriter.println(localObject2);
      }
    }
  }
  
  public String toString()
  {
    if (OnePlusAppBootManager.DEBUG) {
      return "BroadcastRecord{" + Integer.toHexString(System.identityHashCode(this)) + " u" + this.userId + " " + this.intent.getAction() + ", cuid=" + this.callingUid + ", cpid=" + this.callingPid + ", callerApp=" + this.callerApp + "}";
    }
    return "BroadcastRecord{" + Integer.toHexString(System.identityHashCode(this)) + " u" + this.userId + " " + this.intent.getAction() + "}";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/BroadcastRecord.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */