package com.android.server.connectivity;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.net.ConnectivityMetricsEvent;
import android.net.ConnectivityMetricsEvent.Reference;
import android.net.IConnectivityMetricsLogger.Stub;
import android.os.Binder;
import android.os.Parcel;
import android.text.format.DateUtils;
import android.util.Log;
import com.android.server.SystemService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;

public class MetricsLoggerService
  extends SystemService
{
  private static final boolean DBG = true;
  private static String TAG = "ConnectivityMetricsLoggerService";
  private static final boolean VDBG = false;
  private final int EVENTS_NOTIFICATION_THRESHOLD = 300;
  private final int MAX_NUMBER_OF_EVENTS = 1000;
  private final int THROTTLING_MAX_NUMBER_OF_MESSAGES_PER_COMPONENT = 1000;
  private final long THROTTLING_TIME_INTERVAL_MILLIS = 3600000L;
  final MetricsLoggerImpl mBinder = new MetricsLoggerImpl();
  private int mEventCounter = 0;
  private final ArrayDeque<ConnectivityMetricsEvent> mEvents = new ArrayDeque();
  private long mLastEventReference = 0L;
  private final int[] mThrottlingCounters = new int[5];
  private long mThrottlingIntervalBoundaryMillis;
  
  public MetricsLoggerService(Context paramContext)
  {
    super(paramContext);
  }
  
  private void addEvent(ConnectivityMetricsEvent paramConnectivityMetricsEvent)
  {
    while (this.mEvents.size() >= 1000) {
      this.mEvents.removeFirst();
    }
    this.mEvents.addLast(paramConnectivityMetricsEvent);
  }
  
  private void enforceConnectivityInternalPermission()
  {
    getContext().enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "MetricsLoggerService");
  }
  
  private void enforceDumpPermission()
  {
    getContext().enforceCallingOrSelfPermission("android.permission.DUMP", "MetricsLoggerService");
  }
  
  private void resetThrottlingCounters(long paramLong)
  {
    int[] arrayOfInt = this.mThrottlingCounters;
    int i = 0;
    try
    {
      while (i < this.mThrottlingCounters.length)
      {
        this.mThrottlingCounters[i] = 0;
        i += 1;
      }
      this.mThrottlingIntervalBoundaryMillis = (3600000L + paramLong);
      return;
    }
    finally {}
  }
  
  public void onBootPhase(int paramInt)
  {
    if (paramInt == 500)
    {
      Log.d(TAG, "onBootPhase: PHASE_SYSTEM_SERVICES_READY");
      publishBinderService("connectivity_metrics_logger", this.mBinder);
    }
  }
  
  public void onStart()
  {
    resetThrottlingCounters(System.currentTimeMillis());
  }
  
  final class MetricsLoggerImpl
    extends IConnectivityMetricsLogger.Stub
  {
    private final ArrayList<PendingIntent> mPendingIntents = new ArrayList();
    
    MetricsLoggerImpl() {}
    
    protected void dump(FileDescriptor arg1, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      if (MetricsLoggerService.this.getContext().checkCallingOrSelfPermission("android.permission.DUMP") != 0)
      {
        paramPrintWriter.println("Permission Denial: can't dump ConnectivityMetricsLoggerService from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
        return;
      }
      int k = 0;
      int j = 0;
      int i = 0;
      int m = 0;
      int n = paramArrayOfString.length;
      if (m < n)
      {
        ??? = paramArrayOfString[m];
        if (???.equals("--debug")) {
          i = 1;
        }
        for (;;)
        {
          m += 1;
          break;
          if (???.equals("--events"))
          {
            j = 1;
          }
          else if (???.equals("--size"))
          {
            k = 1;
          }
          else if (???.equals("--all"))
          {
            i = 1;
            j = 1;
            k = 1;
          }
        }
      }
      synchronized (MetricsLoggerService.-get2(MetricsLoggerService.this))
      {
        paramPrintWriter.println("Number of events: " + MetricsLoggerService.-get2(MetricsLoggerService.this).size());
        paramPrintWriter.println("Counter: " + MetricsLoggerService.-get1(MetricsLoggerService.this));
        if (MetricsLoggerService.-get2(MetricsLoggerService.this).size() > 0) {
          paramPrintWriter.println("Time span: " + DateUtils.formatElapsedTime((System.currentTimeMillis() - ((ConnectivityMetricsEvent)MetricsLoggerService.-get2(MetricsLoggerService.this).peekFirst()).timestamp) / 1000L));
        }
        if (k == 0) {
          break label371;
        }
        paramArrayOfString = Parcel.obtain();
        Iterator localIterator = MetricsLoggerService.-get2(MetricsLoggerService.this).iterator();
        if (localIterator.hasNext()) {
          paramArrayOfString.writeParcelable((ConnectivityMetricsEvent)localIterator.next(), 0);
        }
      }
      paramPrintWriter.println("Serialized data size: " + paramArrayOfString.dataSize());
      paramArrayOfString.recycle();
      label371:
      if (j != 0)
      {
        paramPrintWriter.println();
        paramPrintWriter.println("Events:");
        paramArrayOfString = MetricsLoggerService.-get2(MetricsLoggerService.this).iterator();
        while (paramArrayOfString.hasNext()) {
          paramPrintWriter.println(((ConnectivityMetricsEvent)paramArrayOfString.next()).toString());
        }
      }
      if (i != 0) {}
      for (;;)
      {
        synchronized (MetricsLoggerService.-get4(MetricsLoggerService.this))
        {
          paramPrintWriter.println();
          i = 0;
          if (i < 5)
          {
            if (MetricsLoggerService.-get4(MetricsLoggerService.this)[i] <= 0) {
              break label635;
            }
            paramPrintWriter.println("Throttling Counter #" + i + ": " + MetricsLoggerService.-get4(MetricsLoggerService.this)[i]);
            break label635;
          }
          paramPrintWriter.println("Throttling Time Remaining: " + DateUtils.formatElapsedTime((MetricsLoggerService.-get5(MetricsLoggerService.this) - System.currentTimeMillis()) / 1000L));
          synchronized (this.mPendingIntents)
          {
            if (!this.mPendingIntents.isEmpty())
            {
              paramPrintWriter.println();
              paramPrintWriter.println("Pending intents:");
              paramArrayOfString = this.mPendingIntents.iterator();
              if (paramArrayOfString.hasNext()) {
                paramPrintWriter.println(((PendingIntent)paramArrayOfString.next()).toString());
              }
            }
          }
        }
        return;
        label635:
        i += 1;
      }
    }
    
    public ConnectivityMetricsEvent[] getEvents(ConnectivityMetricsEvent.Reference paramReference)
    {
      MetricsLoggerService.-wrap2(MetricsLoggerService.this);
      long l2 = paramReference.getValue();
      for (;;)
      {
        ConnectivityMetricsEvent[] arrayOfConnectivityMetricsEvent;
        synchronized (MetricsLoggerService.-get2(MetricsLoggerService.this))
        {
          if (l2 > MetricsLoggerService.-get3(MetricsLoggerService.this))
          {
            Log.e(MetricsLoggerService.-get0(), "Invalid reference");
            paramReference.setValue(MetricsLoggerService.-get3(MetricsLoggerService.this));
            return null;
          }
          long l1 = l2;
          if (l2 < MetricsLoggerService.-get3(MetricsLoggerService.this) - MetricsLoggerService.-get2(MetricsLoggerService.this).size()) {
            l1 = MetricsLoggerService.-get3(MetricsLoggerService.this) - MetricsLoggerService.-get2(MetricsLoggerService.this).size();
          }
          int j = MetricsLoggerService.-get2(MetricsLoggerService.this).size() - (int)(MetricsLoggerService.-get3(MetricsLoggerService.this) - l1);
          arrayOfConnectivityMetricsEvent = new ConnectivityMetricsEvent[MetricsLoggerService.-get2(MetricsLoggerService.this).size() - j];
          Iterator localIterator = MetricsLoggerService.-get2(MetricsLoggerService.this).iterator();
          int i = 0;
          if (localIterator.hasNext())
          {
            ConnectivityMetricsEvent localConnectivityMetricsEvent = (ConnectivityMetricsEvent)localIterator.next();
            if (j > 0)
            {
              j -= 1;
            }
            else
            {
              int k = i + 1;
              arrayOfConnectivityMetricsEvent[i] = localConnectivityMetricsEvent;
              i = k;
            }
          }
        }
        paramReference.setValue(MetricsLoggerService.-get3(MetricsLoggerService.this));
        return arrayOfConnectivityMetricsEvent;
      }
    }
    
    public long logEvent(ConnectivityMetricsEvent paramConnectivityMetricsEvent)
    {
      return logEvents(new ConnectivityMetricsEvent[] { paramConnectivityMetricsEvent });
    }
    
    public long logEvents(ConnectivityMetricsEvent[] arg1)
    {
      MetricsLoggerService.-wrap1(MetricsLoggerService.this);
      if ((??? == null) || (???.length == 0))
      {
        Log.wtf(MetricsLoggerService.-get0(), "No events passed to logEvents()");
        return -1L;
      }
      int k = ???[0].componentTag;
      if ((k < 0) || (k >= 5))
      {
        Log.wtf(MetricsLoggerService.-get0(), "Unexpected tag: " + k);
        return -1L;
      }
      Object localObject3;
      int j;
      int i;
      synchronized (MetricsLoggerService.-get4(MetricsLoggerService.this))
      {
        long l = System.currentTimeMillis();
        if (l > MetricsLoggerService.-get5(MetricsLoggerService.this)) {
          MetricsLoggerService.-wrap3(MetricsLoggerService.this, l);
        }
        localObject3 = MetricsLoggerService.-get4(MetricsLoggerService.this);
        localObject3[k] += ???.length;
        if (MetricsLoggerService.-get4(MetricsLoggerService.this)[k] > 1000)
        {
          Log.w(MetricsLoggerService.-get0(), "Too many events from #" + k + ". Block until " + MetricsLoggerService.-get5(MetricsLoggerService.this));
          l = MetricsLoggerService.-get5(MetricsLoggerService.this);
          return l;
        }
        j = 0;
        ??? = MetricsLoggerService.-get2(MetricsLoggerService.this);
        i = 0;
      }
      try
      {
        int m = ???.length;
        while (i < m)
        {
          localObject3 = ???[i];
          if (((ConnectivityMetricsEvent)localObject3).componentTag != k)
          {
            Log.wtf(MetricsLoggerService.-get0(), "Unexpected tag: " + ((ConnectivityMetricsEvent)localObject3).componentTag);
            return -1L;
            ??? = finally;
            throw ???;
          }
          MetricsLoggerService.-wrap0(MetricsLoggerService.this, (ConnectivityMetricsEvent)localObject3);
          i += 1;
        }
        localObject3 = MetricsLoggerService.this;
        MetricsLoggerService.-set1((MetricsLoggerService)localObject3, MetricsLoggerService.-get3((MetricsLoggerService)localObject3) + ???.length);
        localObject3 = MetricsLoggerService.this;
        MetricsLoggerService.-set0((MetricsLoggerService)localObject3, MetricsLoggerService.-get1((MetricsLoggerService)localObject3) + ???.length);
        i = j;
        if (MetricsLoggerService.-get1(MetricsLoggerService.this) >= 300)
        {
          MetricsLoggerService.-set0(MetricsLoggerService.this, 0);
          i = 1;
        }
        if (i != 0) {
          synchronized (this.mPendingIntents)
          {
            ??? = this.mPendingIntents.iterator();
            for (;;)
            {
              if (((Iterator)???).hasNext())
              {
                localObject3 = (PendingIntent)((Iterator)???).next();
                try
                {
                  ((PendingIntent)localObject3).send(MetricsLoggerService.this.getContext(), 0, null, null, null);
                }
                catch (PendingIntent.CanceledException localCanceledException)
                {
                  Log.e(MetricsLoggerService.-get0(), "Pending intent canceled: " + localObject3);
                  this.mPendingIntents.remove(localObject3);
                }
              }
            }
          }
        }
      }
      finally {}
      return 0L;
    }
    
    public boolean register(PendingIntent paramPendingIntent)
    {
      MetricsLoggerService.-wrap2(MetricsLoggerService.this);
      synchronized (this.mPendingIntents)
      {
        if (this.mPendingIntents.remove(paramPendingIntent)) {
          Log.w(MetricsLoggerService.-get0(), "Replacing registered pending intent");
        }
        this.mPendingIntents.add(paramPendingIntent);
        return true;
      }
    }
    
    public void unregister(PendingIntent paramPendingIntent)
    {
      MetricsLoggerService.-wrap2(MetricsLoggerService.this);
      synchronized (this.mPendingIntents)
      {
        if (!this.mPendingIntents.remove(paramPendingIntent)) {
          Log.e(MetricsLoggerService.-get0(), "Pending intent is not registered");
        }
        return;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/connectivity/MetricsLoggerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */