package com.android.server.connectivity;

import android.content.Context;
import android.net.ConnectivityMetricsEvent;
import android.net.IIpConnectivityMetrics.Stub;
import android.text.TextUtils;
import com.android.internal.annotations.GuardedBy;
import com.android.server.SystemService;
import com.android.server.connectivity.metrics.IpConnectivityLogClass.IpConnectivityEvent;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

public final class IpConnectivityMetrics
  extends SystemService
{
  private static final boolean DBG = false;
  private static final int DEFAULT_BUFFER_SIZE = 2000;
  private static final String SERVICE_NAME = "connmetrics";
  private static final String TAG = IpConnectivityMetrics.class.getSimpleName();
  public final Impl impl = new Impl();
  @GuardedBy("mLock")
  private ArrayList<ConnectivityMetricsEvent> mBuffer;
  @GuardedBy("mLock")
  private int mCapacity;
  @GuardedBy("mLock")
  private int mDropped;
  private final Object mLock = new Object();
  private NetdEventListenerService mNetdListener;
  
  public IpConnectivityMetrics(Context paramContext)
  {
    super(paramContext);
    initBuffer();
  }
  
  private int append(ConnectivityMetricsEvent paramConnectivityMetricsEvent)
  {
    synchronized (this.mLock)
    {
      int i = this.mCapacity;
      int j = this.mBuffer.size();
      i -= j;
      if (paramConnectivityMetricsEvent == null) {
        return i;
      }
      if (i == 0)
      {
        this.mDropped += 1;
        return 0;
      }
      this.mBuffer.add(paramConnectivityMetricsEvent);
      return i - 1;
    }
  }
  
  private void cmdDefault(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    if (paramArrayOfString.length == 0)
    {
      paramPrintWriter.println("No command");
      return;
    }
    paramPrintWriter.println("Unknown command " + TextUtils.join(" ", paramArrayOfString));
  }
  
  private void cmdFlush(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.print(flushEncodedOutput());
  }
  
  private void cmdList(FileDescriptor arg1, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    ArrayList localArrayList;
    synchronized (this.mLock)
    {
      localArrayList = new ArrayList(this.mBuffer);
      if ((paramArrayOfString.length > 1) && (paramArrayOfString[1].equals("proto")))
      {
        ??? = IpConnectivityEventBuilder.toProto(localArrayList);
        int i = 0;
        int j = ???.length;
        if (i < j)
        {
          paramPrintWriter.print(???[i].toString());
          i += 1;
        }
      }
    }
    ??? = localArrayList.iterator();
    while (???.hasNext()) {
      paramPrintWriter.println(((ConnectivityMetricsEvent)???.next()).toString());
    }
  }
  
  private void cmdStats(FileDescriptor arg1, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    synchronized (this.mLock)
    {
      paramPrintWriter.println("Buffered events: " + this.mBuffer.size());
      paramPrintWriter.println("Buffer capacity: " + this.mCapacity);
      paramPrintWriter.println("Dropped events: " + this.mDropped);
      if (this.mNetdListener != null) {
        this.mNetdListener.dump(paramPrintWriter);
      }
      return;
    }
  }
  
  private String flushEncodedOutput()
  {
    ArrayList localArrayList;
    int i;
    synchronized (this.mLock)
    {
      localArrayList = this.mBuffer;
      i = this.mDropped;
      initBuffer();
    }
    return "";
  }
  
  private void initBuffer()
  {
    synchronized (this.mLock)
    {
      this.mDropped = 0;
      this.mCapacity = bufferCapacity();
      this.mBuffer = new ArrayList(this.mCapacity);
      return;
    }
  }
  
  public int bufferCapacity()
  {
    return 2000;
  }
  
  public void onBootPhase(int paramInt)
  {
    if (paramInt == 500)
    {
      this.mNetdListener = new NetdEventListenerService(getContext());
      publishBinderService("connmetrics", this.impl);
      publishBinderService("netd_listener", this.mNetdListener);
    }
  }
  
  public void onStart() {}
  
  public final class Impl
    extends IIpConnectivityMetrics.Stub
  {
    static final String CMD_DEFAULT = "stats";
    static final String CMD_FLUSH = "flush";
    static final String CMD_LIST = "list";
    static final String CMD_STATS = "stats";
    
    public Impl() {}
    
    private void enforceConnectivityInternalPermission()
    {
      enforcePermission("android.permission.CONNECTIVITY_INTERNAL");
    }
    
    private void enforceDumpPermission()
    {
      enforcePermission("android.permission.DUMP");
    }
    
    private void enforcePermission(String paramString)
    {
      IpConnectivityMetrics.this.getContext().enforceCallingOrSelfPermission(paramString, "IpConnectivityMetrics");
    }
    
    public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      enforceDumpPermission();
      if (paramArrayOfString.length > 0) {}
      for (String str = paramArrayOfString[0]; str.equals("flush"); str = "stats")
      {
        IpConnectivityMetrics.-wrap2(IpConnectivityMetrics.this, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
        return;
      }
      if (str.equals("list"))
      {
        IpConnectivityMetrics.-wrap3(IpConnectivityMetrics.this, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
        return;
      }
      if (str.equals("stats"))
      {
        IpConnectivityMetrics.-wrap4(IpConnectivityMetrics.this, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
        return;
      }
      IpConnectivityMetrics.-wrap1(IpConnectivityMetrics.this, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
    
    public int logEvent(ConnectivityMetricsEvent paramConnectivityMetricsEvent)
    {
      enforceConnectivityInternalPermission();
      return IpConnectivityMetrics.-wrap0(IpConnectivityMetrics.this, paramConnectivityMetricsEvent);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/connectivity/IpConnectivityMetrics.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */