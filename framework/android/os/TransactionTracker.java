package android.os;

import com.android.internal.util.FastPrintWriter;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TransactionTracker
{
  private Map<String, Long> mTraces;
  
  TransactionTracker()
  {
    resetTraces();
  }
  
  private void resetTraces()
  {
    try
    {
      this.mTraces = new HashMap();
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  /* Error */
  public void addTrace()
  {
    // Byte code:
    //   0: new 23	java/lang/Throwable
    //   3: dup
    //   4: invokespecial 24	java/lang/Throwable:<init>	()V
    //   7: invokestatic 30	android/util/Log:getStackTraceString	(Ljava/lang/Throwable;)Ljava/lang/String;
    //   10: astore_1
    //   11: aload_0
    //   12: monitorenter
    //   13: aload_0
    //   14: getfield 20	android/os/TransactionTracker:mTraces	Ljava/util/Map;
    //   17: aload_1
    //   18: invokeinterface 36 2 0
    //   23: ifeq +38 -> 61
    //   26: aload_0
    //   27: getfield 20	android/os/TransactionTracker:mTraces	Ljava/util/Map;
    //   30: aload_1
    //   31: aload_0
    //   32: getfield 20	android/os/TransactionTracker:mTraces	Ljava/util/Map;
    //   35: aload_1
    //   36: invokeinterface 40 2 0
    //   41: checkcast 42	java/lang/Long
    //   44: invokevirtual 46	java/lang/Long:longValue	()J
    //   47: lconst_1
    //   48: ladd
    //   49: invokestatic 50	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   52: invokeinterface 54 3 0
    //   57: pop
    //   58: aload_0
    //   59: monitorexit
    //   60: return
    //   61: aload_0
    //   62: getfield 20	android/os/TransactionTracker:mTraces	Ljava/util/Map;
    //   65: aload_1
    //   66: lconst_1
    //   67: invokestatic 50	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   70: invokeinterface 54 3 0
    //   75: pop
    //   76: goto -18 -> 58
    //   79: astore_1
    //   80: aload_0
    //   81: monitorexit
    //   82: aload_1
    //   83: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	84	0	this	TransactionTracker
    //   10	56	1	str	String
    //   79	4	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   13	58	79	finally
    //   61	76	79	finally
  }
  
  public void clearTraces()
  {
    resetTraces();
  }
  
  public void writeTracesToFile(ParcelFileDescriptor paramParcelFileDescriptor)
  {
    if (this.mTraces.isEmpty()) {
      return;
    }
    paramParcelFileDescriptor = new FastPrintWriter(new FileOutputStream(paramParcelFileDescriptor.getFileDescriptor()));
    try
    {
      Iterator localIterator = this.mTraces.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        paramParcelFileDescriptor.println("Count: " + this.mTraces.get(str));
        paramParcelFileDescriptor.println("Trace: " + str);
        paramParcelFileDescriptor.println();
      }
    }
    finally {}
    paramParcelFileDescriptor.flush();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/TransactionTracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */