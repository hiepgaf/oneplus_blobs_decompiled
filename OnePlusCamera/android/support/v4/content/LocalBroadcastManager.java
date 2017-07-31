package android.support.v4.content;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class LocalBroadcastManager
{
  private static final boolean DEBUG = false;
  static final int MSG_EXEC_PENDING_BROADCASTS = 1;
  private static final String TAG = "LocalBroadcastManager";
  private static LocalBroadcastManager mInstance;
  private static final Object mLock = new Object();
  private final HashMap<String, ArrayList<ReceiverRecord>> mActions = new HashMap();
  private final Context mAppContext;
  private final Handler mHandler;
  private final ArrayList<BroadcastRecord> mPendingBroadcasts = new ArrayList();
  private final HashMap<BroadcastReceiver, ArrayList<IntentFilter>> mReceivers = new HashMap();
  
  private LocalBroadcastManager(Context paramContext)
  {
    this.mAppContext = paramContext;
    this.mHandler = new Handler(paramContext.getMainLooper())
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        switch (paramAnonymousMessage.what)
        {
        default: 
          super.handleMessage(paramAnonymousMessage);
          return;
        }
        LocalBroadcastManager.this.executePendingBroadcasts();
      }
    };
  }
  
  private void executePendingBroadcasts()
  {
    for (;;)
    {
      int j;
      synchronized (this.mReceivers)
      {
        int i = this.mPendingBroadcasts.size();
        if (i > 0)
        {
          BroadcastRecord[] arrayOfBroadcastRecord = new BroadcastRecord[i];
          this.mPendingBroadcasts.toArray(arrayOfBroadcastRecord);
          this.mPendingBroadcasts.clear();
          i = 0;
          if (i >= arrayOfBroadcastRecord.length) {
            continue;
          }
          ??? = arrayOfBroadcastRecord[i];
          j = 0;
          if (j >= ???.receivers.size())
          {
            i += 1;
            continue;
          }
        }
        else
        {
          return;
        }
      }
      ((ReceiverRecord)???.receivers.get(j)).receiver.onReceive(this.mAppContext, ???.intent);
      j += 1;
    }
  }
  
  public static LocalBroadcastManager getInstance(Context paramContext)
  {
    synchronized (mLock)
    {
      if (mInstance != null)
      {
        paramContext = mInstance;
        return paramContext;
      }
      mInstance = new LocalBroadcastManager(paramContext.getApplicationContext());
    }
  }
  
  public void registerReceiver(BroadcastReceiver paramBroadcastReceiver, IntentFilter paramIntentFilter)
  {
    ReceiverRecord localReceiverRecord;
    int i;
    synchronized (this.mReceivers)
    {
      localReceiverRecord = new ReceiverRecord(paramIntentFilter, paramBroadcastReceiver);
      localObject = (ArrayList)this.mReceivers.get(paramBroadcastReceiver);
      if (localObject != null)
      {
        paramBroadcastReceiver = (BroadcastReceiver)localObject;
        paramBroadcastReceiver.add(paramIntentFilter);
        i = 0;
        if (i < paramIntentFilter.countActions()) {}
      }
      else
      {
        localObject = new ArrayList(1);
        this.mReceivers.put(paramBroadcastReceiver, localObject);
        paramBroadcastReceiver = (BroadcastReceiver)localObject;
      }
    }
    Object localObject = paramIntentFilter.getAction(i);
    paramBroadcastReceiver = (ArrayList)this.mActions.get(localObject);
    if (paramBroadcastReceiver != null) {}
    for (;;)
    {
      paramBroadcastReceiver.add(localReceiverRecord);
      i += 1;
      break;
      paramBroadcastReceiver = new ArrayList(1);
      this.mActions.put(localObject, paramBroadcastReceiver);
    }
  }
  
  public boolean sendBroadcast(Intent paramIntent)
  {
    String str1;
    String str2;
    Uri localUri;
    String str3;
    Set localSet;
    int i;
    ArrayList localArrayList2;
    synchronized (this.mReceivers)
    {
      str1 = paramIntent.getAction();
      str2 = paramIntent.resolveTypeIfNeeded(this.mAppContext.getContentResolver());
      localUri = paramIntent.getData();
      str3 = paramIntent.getScheme();
      localSet = paramIntent.getCategories();
      if ((paramIntent.getFlags() & 0x8) != 0) {
        break label467;
      }
      i = 0;
      break label460;
      localArrayList2 = (ArrayList)this.mActions.get(paramIntent.getAction());
      if (localArrayList2 == null)
      {
        return false;
        label87:
        Log.v("LocalBroadcastManager", "Resolving type " + str2 + " scheme " + str3 + " of intent " + paramIntent);
      }
    }
    if (i == 0) {}
    ArrayList localArrayList1;
    int j;
    for (;;)
    {
      localArrayList1 = null;
      j = 0;
      if (j < localArrayList2.size()) {
        break label238;
      }
      if (localArrayList1 == null) {
        break;
      }
      i = 0;
      label166:
      if (i < localArrayList1.size()) {
        break label428;
      }
      this.mPendingBroadcasts.add(new BroadcastRecord(paramIntent, localArrayList1));
      if (!this.mHandler.hasMessages(1)) {
        break label448;
      }
      label204:
      return true;
      Log.v("LocalBroadcastManager", "Action list: " + localArrayList2);
    }
    label238:
    Object localObject = (ReceiverRecord)localArrayList2.get(j);
    if (i == 0) {}
    int k;
    for (;;)
    {
      if (!((ReceiverRecord)localObject).broadcasting)
      {
        k = ((ReceiverRecord)localObject).filter.match(str1, str2, str3, localUri, localSet, "LocalBroadcastManager");
        if (k >= 0) {
          break label486;
        }
        if (i != 0) {
          break label498;
        }
        break;
        Log.v("LocalBroadcastManager", "Matching against filter " + ((ReceiverRecord)localObject).filter);
        continue;
        label327:
        Log.v("LocalBroadcastManager", "  Filter's target already added");
        break;
        for (;;)
        {
          label338:
          localArrayList1.add(localObject);
          ((ReceiverRecord)localObject).broadcasting = true;
          break;
          label355:
          Log.v("LocalBroadcastManager", "  Filter matched!  match=0x" + Integer.toHexString(k));
          break label490;
          label387:
          localArrayList1 = new ArrayList();
        }
      }
    }
    for (;;)
    {
      Log.v("LocalBroadcastManager", "  Filter did not match: " + (String)localObject);
      break label472;
      label428:
      ((ReceiverRecord)localArrayList1.get(i)).broadcasting = false;
      i += 1;
      break label166;
      label448:
      this.mHandler.sendEmptyMessage(1);
      break label204;
      for (;;)
      {
        label460:
        if (i != 0) {
          break label87;
        }
        break;
        label467:
        i = 1;
      }
      for (;;)
      {
        label472:
        j += 1;
        break;
        if (i != 0) {
          break label327;
        }
      }
      label486:
      if (i != 0) {
        break label355;
      }
      label490:
      if (localArrayList1 == null) {
        break label387;
      }
      break label338;
      switch (k)
      {
      default: 
        localObject = "unknown reason";
        break;
      case -3: 
        localObject = "action";
        break;
      case -4: 
        localObject = "category";
        break;
      case -2: 
        localObject = "data";
        break;
      case -1: 
        label498:
        localObject = "type";
      }
    }
  }
  
  public void sendBroadcastSync(Intent paramIntent)
  {
    if (!sendBroadcast(paramIntent)) {
      return;
    }
    executePendingBroadcasts();
  }
  
  public void unregisterReceiver(BroadcastReceiver paramBroadcastReceiver)
  {
    for (;;)
    {
      int k;
      ArrayList localArrayList2;
      int i;
      synchronized (this.mReceivers)
      {
        ArrayList localArrayList1 = (ArrayList)this.mReceivers.remove(paramBroadcastReceiver);
        int j;
        if (localArrayList1 != null)
        {
          j = 0;
          if (j < localArrayList1.size()) {}
        }
        else
        {
          return;
        }
        IntentFilter localIntentFilter = (IntentFilter)localArrayList1.get(j);
        k = 0;
        if (k >= localIntentFilter.countActions())
        {
          j += 1;
          continue;
        }
        String str = localIntentFilter.getAction(k);
        localArrayList2 = (ArrayList)this.mActions.get(str);
        if (localArrayList2 != null) {
          break label186;
        }
        break label177;
        if (i >= localArrayList2.size())
        {
          if (localArrayList2.size() > 0) {
            break label177;
          }
          this.mActions.remove(str);
        }
      }
      if (((ReceiverRecord)localArrayList2.get(i)).receiver == paramBroadcastReceiver)
      {
        localArrayList2.remove(i);
        i -= 1;
        break label191;
        label177:
        k += 1;
        continue;
        label186:
        i = 0;
        continue;
      }
      label191:
      i += 1;
    }
  }
  
  private static class BroadcastRecord
  {
    final Intent intent;
    final ArrayList<LocalBroadcastManager.ReceiverRecord> receivers;
    
    BroadcastRecord(Intent paramIntent, ArrayList<LocalBroadcastManager.ReceiverRecord> paramArrayList)
    {
      this.intent = paramIntent;
      this.receivers = paramArrayList;
    }
  }
  
  private static class ReceiverRecord
  {
    boolean broadcasting;
    final IntentFilter filter;
    final BroadcastReceiver receiver;
    
    ReceiverRecord(IntentFilter paramIntentFilter, BroadcastReceiver paramBroadcastReceiver)
    {
      this.filter = paramIntentFilter;
      this.receiver = paramBroadcastReceiver;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder(128);
      localStringBuilder.append("Receiver{");
      localStringBuilder.append(this.receiver);
      localStringBuilder.append(" filter=");
      localStringBuilder.append(this.filter);
      localStringBuilder.append("}");
      return localStringBuilder.toString();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/content/LocalBroadcastManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */