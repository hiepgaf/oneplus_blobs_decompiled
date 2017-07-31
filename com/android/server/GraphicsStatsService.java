package com.android.server;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.MemoryFile;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;
import android.view.IGraphicsStats.Stub;
import android.view.ThreadedRenderer;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class GraphicsStatsService
  extends IGraphicsStats.Stub
{
  private static final int ASHMEM_SIZE = 464;
  public static final String GRAPHICS_STATS_SERVICE = "graphicsstats";
  private static final int HISTORY_SIZE = 20;
  private static final String TAG = "GraphicsStatsService";
  private ArrayList<ActiveBuffer> mActive = new ArrayList();
  private final AppOpsManager mAppOps;
  private final Context mContext;
  private HistoricalData[] mHistoricalLog = new HistoricalData[20];
  private final Object mLock = new Object();
  private int mNextHistoricalSlot = 0;
  private byte[] mTempBuffer = new byte['ǐ'];
  
  public GraphicsStatsService(Context paramContext)
  {
    this.mContext = paramContext;
    this.mAppOps = ((AppOpsManager)paramContext.getSystemService(AppOpsManager.class));
  }
  
  private ActiveBuffer fetchActiveBuffersLocked(IBinder paramIBinder, int paramInt1, int paramInt2, String paramString)
    throws RemoteException
  {
    int j = this.mActive.size();
    int i = 0;
    while (i < j)
    {
      ActiveBuffer localActiveBuffer = (ActiveBuffer)this.mActive.get(i);
      if ((localActiveBuffer.mPid == paramInt2) && (localActiveBuffer.mUid == paramInt1)) {
        return localActiveBuffer;
      }
      i += 1;
    }
    try
    {
      paramIBinder = new ActiveBuffer(paramIBinder, paramInt1, paramInt2, paramString);
      this.mActive.add(paramIBinder);
      return paramIBinder;
    }
    catch (IOException paramIBinder)
    {
      throw new RemoteException("Failed to allocate space");
    }
  }
  
  private ParcelFileDescriptor getPfd(MemoryFile paramMemoryFile)
  {
    try
    {
      paramMemoryFile = new ParcelFileDescriptor(paramMemoryFile.getFileDescriptor());
      return paramMemoryFile;
    }
    catch (IOException paramMemoryFile)
    {
      throw new IllegalStateException("Failed to get PFD from memory file", paramMemoryFile);
    }
  }
  
  private void processDied(ActiveBuffer paramActiveBuffer)
  {
    synchronized (this.mLock)
    {
      this.mActive.remove(paramActiveBuffer);
      Log.d("GraphicsStats", "Buffer count: " + this.mActive.size());
      HistoricalData localHistoricalData = paramActiveBuffer.mPreviousData;
      paramActiveBuffer.mPreviousData = null;
      ??? = localHistoricalData;
      if (localHistoricalData == null)
      {
        localHistoricalData = this.mHistoricalLog[this.mNextHistoricalSlot];
        ??? = localHistoricalData;
        if (localHistoricalData == null) {
          ??? = new HistoricalData(null);
        }
      }
      ((HistoricalData)???).update(paramActiveBuffer.mPackageName, paramActiveBuffer.mUid, paramActiveBuffer.mProcessBuffer);
      paramActiveBuffer.closeAllBuffers();
      this.mHistoricalLog[this.mNextHistoricalSlot] = ???;
      this.mNextHistoricalSlot = ((this.mNextHistoricalSlot + 1) % this.mHistoricalLog.length);
      return;
    }
  }
  
  private HistoricalData removeHistoricalDataLocked(int paramInt, String paramString)
  {
    int i = 0;
    while (i < this.mHistoricalLog.length)
    {
      HistoricalData localHistoricalData = this.mHistoricalLog[i];
      if ((localHistoricalData != null) && (localHistoricalData.mUid == paramInt) && (localHistoricalData.mPackageName.equals(paramString)))
      {
        if (i == this.mNextHistoricalSlot)
        {
          this.mHistoricalLog[i] = null;
          return localHistoricalData;
        }
        this.mHistoricalLog[i] = this.mHistoricalLog[this.mNextHistoricalSlot];
        this.mHistoricalLog[this.mNextHistoricalSlot] = null;
        return localHistoricalData;
      }
      i += 1;
    }
    return null;
  }
  
  private ParcelFileDescriptor requestBufferForProcessLocked(IBinder paramIBinder, int paramInt1, int paramInt2, String paramString)
    throws RemoteException
  {
    return getPfd(fetchActiveBuffersLocked(paramIBinder, paramInt1, paramInt2, paramString).mProcessBuffer);
  }
  
  protected void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    int j = 0;
    this.mContext.enforceCallingOrSelfPermission("android.permission.DUMP", "GraphicsStatsService");
    paramArrayOfString = this.mLock;
    int i = 0;
    try
    {
      for (;;)
      {
        if (i < this.mActive.size())
        {
          ActiveBuffer localActiveBuffer = (ActiveBuffer)this.mActive.get(i);
          paramPrintWriter.print("Package: ");
          paramPrintWriter.print(localActiveBuffer.mPackageName);
          paramPrintWriter.flush();
          try
          {
            localActiveBuffer.mProcessBuffer.readBytes(this.mTempBuffer, 0, 0, 464);
            ThreadedRenderer.dumpProfileData(this.mTempBuffer, paramFileDescriptor);
            paramPrintWriter.println();
            i += 1;
          }
          catch (IOException localIOException)
          {
            for (;;)
            {
              paramPrintWriter.println("Failed to dump");
            }
          }
        }
      }
      arrayOfHistoricalData = this.mHistoricalLog;
    }
    finally {}
    HistoricalData[] arrayOfHistoricalData;
    int k = arrayOfHistoricalData.length;
    i = j;
    break label180;
    paramPrintWriter.print("Package: ");
    HistoricalData localHistoricalData;
    paramPrintWriter.print(localHistoricalData.mPackageName);
    paramPrintWriter.flush();
    ThreadedRenderer.dumpProfileData(localHistoricalData.mBuffer, paramFileDescriptor);
    paramPrintWriter.println();
    label180:
    label206:
    for (;;)
    {
      return;
      for (;;)
      {
        if (i >= k) {
          break label206;
        }
        localHistoricalData = arrayOfHistoricalData[i];
        if (localHistoricalData != null) {
          break;
        }
        i += 1;
      }
    }
  }
  
  /* Error */
  public ParcelFileDescriptor requestBufferForProcess(String paramString, IBinder paramIBinder)
    throws RemoteException
  {
    // Byte code:
    //   0: invokestatic 241	android/os/Binder:getCallingUid	()I
    //   3: istore_3
    //   4: invokestatic 244	android/os/Binder:getCallingPid	()I
    //   7: istore 4
    //   9: invokestatic 248	android/os/Binder:clearCallingIdentity	()J
    //   12: lstore 5
    //   14: aload_0
    //   15: getfield 82	com/android/server/GraphicsStatsService:mAppOps	Landroid/app/AppOpsManager;
    //   18: iload_3
    //   19: aload_1
    //   20: invokevirtual 252	android/app/AppOpsManager:checkPackage	(ILjava/lang/String;)V
    //   23: aload_0
    //   24: getfield 59	com/android/server/GraphicsStatsService:mLock	Ljava/lang/Object;
    //   27: astore 7
    //   29: aload 7
    //   31: monitorenter
    //   32: aload_0
    //   33: aload_2
    //   34: iload_3
    //   35: iload 4
    //   37: aload_1
    //   38: invokespecial 254	com/android/server/GraphicsStatsService:requestBufferForProcessLocked	(Landroid/os/IBinder;IILjava/lang/String;)Landroid/os/ParcelFileDescriptor;
    //   41: astore_1
    //   42: aload 7
    //   44: monitorexit
    //   45: lload 5
    //   47: invokestatic 258	android/os/Binder:restoreCallingIdentity	(J)V
    //   50: aload_1
    //   51: areturn
    //   52: astore_1
    //   53: aload 7
    //   55: monitorexit
    //   56: aload_1
    //   57: athrow
    //   58: astore_1
    //   59: lload 5
    //   61: invokestatic 258	android/os/Binder:restoreCallingIdentity	(J)V
    //   64: aload_1
    //   65: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	66	0	this	GraphicsStatsService
    //   0	66	1	paramString	String
    //   0	66	2	paramIBinder	IBinder
    //   3	32	3	i	int
    //   7	29	4	j	int
    //   12	48	5	l	long
    // Exception table:
    //   from	to	target	type
    //   32	42	52	finally
    //   14	32	58	finally
    //   42	45	58	finally
    //   53	58	58	finally
  }
  
  private final class ActiveBuffer
    implements IBinder.DeathRecipient
  {
    final String mPackageName;
    final int mPid;
    GraphicsStatsService.HistoricalData mPreviousData;
    MemoryFile mProcessBuffer;
    final IBinder mToken;
    final int mUid;
    
    ActiveBuffer(IBinder paramIBinder, int paramInt1, int paramInt2, String paramString)
      throws RemoteException, IOException
    {
      this.mUid = paramInt1;
      this.mPid = paramInt2;
      this.mPackageName = paramString;
      this.mToken = paramIBinder;
      this.mToken.linkToDeath(this, 0);
      this.mProcessBuffer = new MemoryFile("GFXStats-" + paramInt1, 464);
      this.mPreviousData = GraphicsStatsService.-wrap0(GraphicsStatsService.this, this.mUid, this.mPackageName);
      if (this.mPreviousData != null) {
        this.mProcessBuffer.writeBytes(this.mPreviousData.mBuffer, 0, 0, 464);
      }
    }
    
    public void binderDied()
    {
      this.mToken.unlinkToDeath(this, 0);
      GraphicsStatsService.-wrap1(GraphicsStatsService.this, this);
    }
    
    void closeAllBuffers()
    {
      if (this.mProcessBuffer != null)
      {
        this.mProcessBuffer.close();
        this.mProcessBuffer = null;
      }
    }
  }
  
  private static final class HistoricalData
  {
    final byte[] mBuffer = new byte['ǐ'];
    String mPackageName;
    int mUid;
    
    void update(String paramString, int paramInt, MemoryFile paramMemoryFile)
    {
      this.mUid = paramInt;
      this.mPackageName = paramString;
      try
      {
        paramMemoryFile.readBytes(this.mBuffer, 0, 0, 464);
        return;
      }
      catch (IOException paramString) {}
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/GraphicsStatsService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */