package android.ddm;

import android.os.Debug;
import android.util.Log;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.harmony.dalvik.ddmc.Chunk;
import org.apache.harmony.dalvik.ddmc.ChunkHandler;
import org.apache.harmony.dalvik.ddmc.DdmServer;
import org.apache.harmony.dalvik.ddmc.DdmVmInternal;

public class DdmHandleHeap
  extends ChunkHandler
{
  public static final int CHUNK_HPDS;
  public static final int CHUNK_HPDU;
  public static final int CHUNK_HPGC;
  public static final int CHUNK_HPIF = type("HPIF");
  public static final int CHUNK_HPSG = type("HPSG");
  public static final int CHUNK_NHSG;
  public static final int CHUNK_REAE;
  public static final int CHUNK_REAL = type("REAL");
  public static final int CHUNK_REAQ;
  private static DdmHandleHeap mInstance = new DdmHandleHeap();
  
  static
  {
    CHUNK_HPDU = type("HPDU");
    CHUNK_HPDS = type("HPDS");
    CHUNK_NHSG = type("NHSG");
    CHUNK_HPGC = type("HPGC");
    CHUNK_REAE = type("REAE");
    CHUNK_REAQ = type("REAQ");
  }
  
  private Chunk handleHPDS(Chunk paramChunk)
  {
    wrapChunk(paramChunk);
    paramChunk = null;
    try
    {
      Debug.dumpHprofDataDdms();
      if (paramChunk != null)
      {
        Log.w("ddm-heap", paramChunk);
        return createFailChunk(1, paramChunk);
      }
    }
    catch (RuntimeException paramChunk)
    {
      for (;;)
      {
        paramChunk = "Exception: " + paramChunk.getMessage();
      }
    }
    catch (UnsupportedOperationException paramChunk)
    {
      for (;;)
      {
        paramChunk = "hprof dumps not supported in this VM";
      }
    }
    return null;
  }
  
  private Chunk handleHPDU(Chunk paramChunk)
  {
    paramChunk = wrapChunk(paramChunk);
    paramChunk = getString(paramChunk, paramChunk.getInt());
    try
    {
      Debug.dumpHprofData(paramChunk);
      i = 0;
    }
    catch (RuntimeException paramChunk)
    {
      for (;;)
      {
        i = -1;
      }
    }
    catch (IOException paramChunk)
    {
      for (;;)
      {
        i = -1;
      }
    }
    catch (UnsupportedOperationException paramChunk)
    {
      for (;;)
      {
        Log.w("ddm-heap", "hprof dumps not supported in this VM");
        int i = -1;
      }
    }
    paramChunk = new byte[1];
    paramChunk[0] = i;
    return new Chunk(CHUNK_HPDU, paramChunk, 0, paramChunk.length);
  }
  
  private Chunk handleHPGC(Chunk paramChunk)
  {
    Runtime.getRuntime().gc();
    return null;
  }
  
  private Chunk handleHPIF(Chunk paramChunk)
  {
    if (!DdmVmInternal.heapInfoNotify(wrapChunk(paramChunk).get())) {
      return createFailChunk(1, "Unsupported HPIF what");
    }
    return null;
  }
  
  private Chunk handleHPSGNHSG(Chunk paramChunk, boolean paramBoolean)
  {
    paramChunk = wrapChunk(paramChunk);
    if (!DdmVmInternal.heapSegmentNotify(paramChunk.get(), paramChunk.get(), paramBoolean)) {
      return createFailChunk(1, "Unsupported HPSG what/when");
    }
    return null;
  }
  
  private Chunk handleREAE(Chunk paramChunk)
  {
    boolean bool = false;
    if (wrapChunk(paramChunk).get() != 0) {
      bool = true;
    }
    DdmVmInternal.enableRecentAllocations(bool);
    return null;
  }
  
  private Chunk handleREAL(Chunk paramChunk)
  {
    paramChunk = DdmVmInternal.getRecentAllocations();
    return new Chunk(CHUNK_REAL, paramChunk, 0, paramChunk.length);
  }
  
  private Chunk handleREAQ(Chunk paramChunk)
  {
    int i = 1;
    paramChunk = new byte[1];
    if (DdmVmInternal.getRecentAllocationStatus()) {}
    for (;;)
    {
      paramChunk[0] = i;
      return new Chunk(CHUNK_REAQ, paramChunk, 0, paramChunk.length);
      i = 0;
    }
  }
  
  public static void register()
  {
    DdmServer.registerHandler(CHUNK_HPIF, mInstance);
    DdmServer.registerHandler(CHUNK_HPSG, mInstance);
    DdmServer.registerHandler(CHUNK_HPDU, mInstance);
    DdmServer.registerHandler(CHUNK_HPDS, mInstance);
    DdmServer.registerHandler(CHUNK_NHSG, mInstance);
    DdmServer.registerHandler(CHUNK_HPGC, mInstance);
    DdmServer.registerHandler(CHUNK_REAE, mInstance);
    DdmServer.registerHandler(CHUNK_REAQ, mInstance);
    DdmServer.registerHandler(CHUNK_REAL, mInstance);
  }
  
  public void connected() {}
  
  public void disconnected() {}
  
  public Chunk handleChunk(Chunk paramChunk)
  {
    int i = paramChunk.type;
    if (i == CHUNK_HPIF) {
      return handleHPIF(paramChunk);
    }
    if (i == CHUNK_HPSG) {
      return handleHPSGNHSG(paramChunk, false);
    }
    if (i == CHUNK_HPDU) {
      return handleHPDU(paramChunk);
    }
    if (i == CHUNK_HPDS) {
      return handleHPDS(paramChunk);
    }
    if (i == CHUNK_NHSG) {
      return handleHPSGNHSG(paramChunk, true);
    }
    if (i == CHUNK_HPGC) {
      return handleHPGC(paramChunk);
    }
    if (i == CHUNK_REAE) {
      return handleREAE(paramChunk);
    }
    if (i == CHUNK_REAQ) {
      return handleREAQ(paramChunk);
    }
    if (i == CHUNK_REAL) {
      return handleREAL(paramChunk);
    }
    throw new RuntimeException("Unknown packet " + ChunkHandler.name(i));
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/ddm/DdmHandleHeap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */