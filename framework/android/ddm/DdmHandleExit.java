package android.ddm;

import java.nio.ByteBuffer;
import org.apache.harmony.dalvik.ddmc.Chunk;
import org.apache.harmony.dalvik.ddmc.ChunkHandler;
import org.apache.harmony.dalvik.ddmc.DdmServer;

public class DdmHandleExit
  extends ChunkHandler
{
  public static final int CHUNK_EXIT = type("EXIT");
  private static DdmHandleExit mInstance = new DdmHandleExit();
  
  public static void register()
  {
    DdmServer.registerHandler(CHUNK_EXIT, mInstance);
  }
  
  public void connected() {}
  
  public void disconnected() {}
  
  public Chunk handleChunk(Chunk paramChunk)
  {
    int i = wrapChunk(paramChunk).getInt();
    Runtime.getRuntime().halt(i);
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/ddm/DdmHandleExit.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */