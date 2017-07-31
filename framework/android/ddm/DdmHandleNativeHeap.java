package android.ddm;

import android.util.Log;
import org.apache.harmony.dalvik.ddmc.Chunk;
import org.apache.harmony.dalvik.ddmc.ChunkHandler;
import org.apache.harmony.dalvik.ddmc.DdmServer;

public class DdmHandleNativeHeap
  extends ChunkHandler
{
  public static final int CHUNK_NHGT = type("NHGT");
  private static DdmHandleNativeHeap mInstance = new DdmHandleNativeHeap();
  
  private native byte[] getLeakInfo();
  
  private Chunk handleNHGT(Chunk paramChunk)
  {
    paramChunk = getLeakInfo();
    if (paramChunk != null)
    {
      Log.i("ddm-nativeheap", "Sending " + paramChunk.length + " bytes");
      return new Chunk(ChunkHandler.type("NHGT"), paramChunk, 0, paramChunk.length);
    }
    return createFailChunk(1, "Something went wrong");
  }
  
  public static void register()
  {
    DdmServer.registerHandler(CHUNK_NHGT, mInstance);
  }
  
  public void connected() {}
  
  public void disconnected() {}
  
  public Chunk handleChunk(Chunk paramChunk)
  {
    Log.i("ddm-nativeheap", "Handling " + name(paramChunk.type) + " chunk");
    int i = paramChunk.type;
    if (i == CHUNK_NHGT) {
      return handleNHGT(paramChunk);
    }
    throw new RuntimeException("Unknown packet " + ChunkHandler.name(i));
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/ddm/DdmHandleNativeHeap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */