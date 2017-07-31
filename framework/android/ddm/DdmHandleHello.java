package android.ddm;

import android.os.Debug;
import android.os.Process;
import android.os.UserHandle;
import dalvik.system.VMRuntime;
import java.nio.ByteBuffer;
import org.apache.harmony.dalvik.ddmc.Chunk;
import org.apache.harmony.dalvik.ddmc.ChunkHandler;
import org.apache.harmony.dalvik.ddmc.DdmServer;

public class DdmHandleHello
  extends ChunkHandler
{
  public static final int CHUNK_FEAT;
  public static final int CHUNK_HELO = type("HELO");
  public static final int CHUNK_WAIT = type("WAIT");
  private static final String[] FRAMEWORK_FEATURES = { "opengl-tracing", "view-hierarchy" };
  private static DdmHandleHello mInstance;
  
  static
  {
    CHUNK_FEAT = type("FEAT");
    mInstance = new DdmHandleHello();
  }
  
  private Chunk handleFEAT(Chunk paramChunk)
  {
    paramChunk = Debug.getVmFeatureList();
    int i = (paramChunk.length + FRAMEWORK_FEATURES.length) * 4 + 4;
    int j = paramChunk.length - 1;
    while (j >= 0)
    {
      i += paramChunk[j].length() * 2;
      j -= 1;
    }
    int k = FRAMEWORK_FEATURES.length - 1;
    j = i;
    i = k;
    while (i >= 0)
    {
      j += FRAMEWORK_FEATURES[i].length() * 2;
      i -= 1;
    }
    ByteBuffer localByteBuffer = ByteBuffer.allocate(j);
    localByteBuffer.order(ChunkHandler.CHUNK_ORDER);
    localByteBuffer.putInt(paramChunk.length + FRAMEWORK_FEATURES.length);
    i = paramChunk.length - 1;
    while (i >= 0)
    {
      localByteBuffer.putInt(paramChunk[i].length());
      putString(localByteBuffer, paramChunk[i]);
      i -= 1;
    }
    i = FRAMEWORK_FEATURES.length - 1;
    while (i >= 0)
    {
      localByteBuffer.putInt(FRAMEWORK_FEATURES[i].length());
      putString(localByteBuffer, FRAMEWORK_FEATURES[i]);
      i -= 1;
    }
    return new Chunk(CHUNK_FEAT, localByteBuffer);
  }
  
  private Chunk handleHELO(Chunk paramChunk)
  {
    wrapChunk(paramChunk).getInt();
    paramChunk = System.getProperty("java.vm.name", "?");
    Object localObject1 = System.getProperty("java.vm.version", "?");
    String str1 = paramChunk + " v" + (String)localObject1;
    String str2 = DdmHandleAppName.getAppName();
    Object localObject2 = VMRuntime.getRuntime();
    if (((VMRuntime)localObject2).is64Bit())
    {
      paramChunk = "64-bit";
      Object localObject3 = ((VMRuntime)localObject2).vmInstructionSet();
      localObject1 = paramChunk;
      if (localObject3 != null)
      {
        localObject1 = paramChunk;
        if (((String)localObject3).length() > 0) {
          localObject1 = paramChunk + " (" + (String)localObject3 + ")";
        }
      }
      localObject3 = new StringBuilder().append("CheckJNI=");
      if (!((VMRuntime)localObject2).isCheckJniEnabled()) {
        break label359;
      }
      paramChunk = "true";
      label154:
      paramChunk = paramChunk;
      boolean bool = ((VMRuntime)localObject2).isNativeDebuggable();
      localObject2 = ByteBuffer.allocate(str1.length() * 2 + 28 + str2.length() * 2 + ((String)localObject1).length() * 2 + paramChunk.length() * 2 + 1);
      ((ByteBuffer)localObject2).order(ChunkHandler.CHUNK_ORDER);
      ((ByteBuffer)localObject2).putInt(1);
      ((ByteBuffer)localObject2).putInt(Process.myPid());
      ((ByteBuffer)localObject2).putInt(str1.length());
      ((ByteBuffer)localObject2).putInt(str2.length());
      putString((ByteBuffer)localObject2, str1);
      putString((ByteBuffer)localObject2, str2);
      ((ByteBuffer)localObject2).putInt(UserHandle.myUserId());
      ((ByteBuffer)localObject2).putInt(((String)localObject1).length());
      putString((ByteBuffer)localObject2, (String)localObject1);
      ((ByteBuffer)localObject2).putInt(paramChunk.length());
      putString((ByteBuffer)localObject2, paramChunk);
      if (!bool) {
        break label365;
      }
    }
    label359:
    label365:
    for (int i = 1;; i = 0)
    {
      ((ByteBuffer)localObject2).put((byte)i);
      paramChunk = new Chunk(CHUNK_HELO, (ByteBuffer)localObject2);
      if (Debug.waitingForDebugger()) {
        sendWAIT(0);
      }
      return paramChunk;
      paramChunk = "32-bit";
      break;
      paramChunk = "false";
      break label154;
    }
  }
  
  public static void register()
  {
    DdmServer.registerHandler(CHUNK_HELO, mInstance);
    DdmServer.registerHandler(CHUNK_FEAT, mInstance);
  }
  
  public static void sendWAIT(int paramInt)
  {
    int i = (byte)paramInt;
    DdmServer.sendChunk(new Chunk(CHUNK_WAIT, new byte[] { i }, 0, 1));
  }
  
  public void connected() {}
  
  public void disconnected() {}
  
  public Chunk handleChunk(Chunk paramChunk)
  {
    int i = paramChunk.type;
    if (i == CHUNK_HELO) {
      return handleHELO(paramChunk);
    }
    if (i == CHUNK_FEAT) {
      return handleFEAT(paramChunk);
    }
    throw new RuntimeException("Unknown packet " + ChunkHandler.name(i));
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/ddm/DdmHandleHello.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */