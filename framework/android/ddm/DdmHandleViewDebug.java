package android.ddm;

import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.view.WindowManagerGlobal;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import org.apache.harmony.dalvik.ddmc.Chunk;
import org.apache.harmony.dalvik.ddmc.ChunkHandler;
import org.apache.harmony.dalvik.ddmc.DdmServer;

public class DdmHandleViewDebug
  extends ChunkHandler
{
  private static final int CHUNK_VULW = type("VULW");
  private static final int CHUNK_VUOP = type("VUOP");
  private static final int CHUNK_VURT = type("VURT");
  private static final int ERR_EXCEPTION = -3;
  private static final int ERR_INVALID_OP = -1;
  private static final int ERR_INVALID_PARAM = -2;
  private static final String TAG = "DdmViewDebug";
  private static final int VUOP_CAPTURE_VIEW = 1;
  private static final int VUOP_DUMP_DISPLAYLIST = 2;
  private static final int VUOP_INVOKE_VIEW_METHOD = 4;
  private static final int VUOP_PROFILE_VIEW = 3;
  private static final int VUOP_SET_LAYOUT_PARAMETER = 5;
  private static final int VURT_CAPTURE_LAYERS = 2;
  private static final int VURT_DUMP_HIERARCHY = 1;
  private static final int VURT_DUMP_THEME = 3;
  private static final DdmHandleViewDebug sInstance = new DdmHandleViewDebug();
  
  private Chunk captureLayers(View paramView)
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(1024);
    DataOutputStream localDataOutputStream = new DataOutputStream(localByteArrayOutputStream);
    try
    {
      ViewDebug.captureLayers(paramView, localDataOutputStream);
      try
      {
        localDataOutputStream.close();
        paramView = localByteArrayOutputStream.toByteArray();
        return new Chunk(CHUNK_VURT, paramView, 0, paramView.length);
      }
      catch (IOException paramView)
      {
        for (;;) {}
      }
      try
      {
        localIOException1.close();
        throw paramView;
      }
      catch (IOException localIOException2)
      {
        for (;;) {}
      }
    }
    catch (IOException paramView)
    {
      paramView = paramView;
      paramView = createFailChunk(1, "Unexpected error while obtaining view hierarchy: " + paramView.getMessage());
      try
      {
        localDataOutputStream.close();
        return paramView;
      }
      catch (IOException localIOException1)
      {
        return paramView;
      }
    }
    finally {}
  }
  
  private Chunk captureView(View paramView1, View paramView2)
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(1024);
    try
    {
      ViewDebug.capture(paramView1, localByteArrayOutputStream, paramView2);
      paramView1 = localByteArrayOutputStream.toByteArray();
      return new Chunk(CHUNK_VUOP, paramView1, 0, paramView1.length);
    }
    catch (IOException paramView1) {}
    return createFailChunk(1, "Unexpected error while capturing view: " + paramView1.getMessage());
  }
  
  private Chunk dumpDisplayLists(final View paramView1, final View paramView2)
  {
    paramView1.post(new Runnable()
    {
      public void run()
      {
        ViewDebug.outputDisplayList(paramView1, paramView2);
      }
    });
    return null;
  }
  
  private Chunk dumpHierarchy(View paramView, ByteBuffer paramByteBuffer)
  {
    boolean bool1;
    boolean bool2;
    label20:
    int i;
    label36:
    long l1;
    if (paramByteBuffer.getInt() > 0)
    {
      bool1 = true;
      if (paramByteBuffer.getInt() <= 0) {
        break label120;
      }
      bool2 = true;
      if ((!paramByteBuffer.hasRemaining()) || (paramByteBuffer.getInt() <= 0)) {
        break label126;
      }
      i = 1;
      l1 = System.currentTimeMillis();
      paramByteBuffer = new ByteArrayOutputStream(2097152);
      if (i == 0) {
        break label131;
      }
    }
    for (;;)
    {
      label120:
      label126:
      label131:
      try
      {
        ViewDebug.dumpv2(paramView, paramByteBuffer);
        long l2 = System.currentTimeMillis();
        Log.d("DdmViewDebug", "Time to obtain view hierarchy (ms): " + (l2 - l1));
        paramView = paramByteBuffer.toByteArray();
        return new Chunk(CHUNK_VURT, paramView, 0, paramView.length);
      }
      catch (IOException|InterruptedException paramView) {}
      bool1 = false;
      break;
      bool2 = false;
      break label20;
      i = 0;
      break label36;
      ViewDebug.dump(paramView, bool1, bool2, paramByteBuffer);
    }
    return createFailChunk(1, "Unexpected error while obtaining view hierarchy: " + paramView.getMessage());
  }
  
  private Chunk dumpTheme(View paramView)
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(1024);
    try
    {
      ViewDebug.dumpTheme(paramView, localByteArrayOutputStream);
      paramView = localByteArrayOutputStream.toByteArray();
      return new Chunk(CHUNK_VURT, paramView, 0, paramView.length);
    }
    catch (IOException paramView) {}
    return createFailChunk(1, "Unexpected error while dumping the theme: " + paramView.getMessage());
  }
  
  private View getRootView(ByteBuffer paramByteBuffer)
  {
    try
    {
      paramByteBuffer = getString(paramByteBuffer, paramByteBuffer.getInt());
      paramByteBuffer = WindowManagerGlobal.getInstance().getRootView(paramByteBuffer);
      return paramByteBuffer;
    }
    catch (BufferUnderflowException paramByteBuffer) {}
    return null;
  }
  
  private View getTargetView(View paramView, ByteBuffer paramByteBuffer)
  {
    try
    {
      paramByteBuffer = getString(paramByteBuffer, paramByteBuffer.getInt());
      return ViewDebug.findView(paramView, paramByteBuffer);
    }
    catch (BufferUnderflowException paramView) {}
    return null;
  }
  
  private Chunk invokeViewMethod(View paramView1, View paramView2, ByteBuffer paramByteBuffer)
  {
    String str = getString(paramByteBuffer, paramByteBuffer.getInt());
    Object localObject;
    if (!paramByteBuffer.hasRemaining())
    {
      localObject = new Class[0];
      paramView1 = new Object[0];
    }
    for (;;)
    {
      try
      {
        paramByteBuffer = paramView2.getClass().getMethod(str, (Class[])localObject);
      }
      catch (NoSuchMethodException paramView1)
      {
        int j;
        Class[] arrayOfClass;
        Object[] arrayOfObject;
        int i;
        char c;
        boolean bool;
        label253:
        Log.e("DdmViewDebug", "No such method: " + paramView1.getMessage());
        return createFailChunk(-2, "No such method: " + paramView1.getMessage());
      }
      try
      {
        ViewDebug.invokeViewMethod(paramView2, paramByteBuffer, paramView1);
        return null;
      }
      catch (Exception paramByteBuffer)
      {
        Log.e("DdmViewDebug", "Exception while invoking method: " + paramByteBuffer.getCause().getMessage());
        paramView2 = paramByteBuffer.getCause().getMessage();
        paramView1 = paramView2;
        if (paramView2 != null) {
          break label553;
        }
        paramView1 = paramByteBuffer.getCause().toString();
      }
      j = paramByteBuffer.getInt();
      arrayOfClass = new Class[j];
      arrayOfObject = new Object[j];
      i = 0;
      localObject = arrayOfClass;
      paramView1 = arrayOfObject;
      if (i < j)
      {
        c = paramByteBuffer.getChar();
        switch (c)
        {
        default: 
          Log.e("DdmViewDebug", "arg " + i + ", unrecognized type: " + c);
          return createFailChunk(-2, "Unsupported parameter type (" + c + ") to invoke view method.");
        case 'Z': 
          arrayOfClass[i] = Boolean.TYPE;
          if (paramByteBuffer.get() == 0)
          {
            bool = false;
            arrayOfObject[i] = Boolean.valueOf(bool);
          }
          break;
        }
        for (;;)
        {
          i += 1;
          break;
          bool = true;
          break label253;
          arrayOfClass[i] = Byte.TYPE;
          arrayOfObject[i] = Byte.valueOf(paramByteBuffer.get());
          continue;
          arrayOfClass[i] = Character.TYPE;
          arrayOfObject[i] = Character.valueOf(paramByteBuffer.getChar());
          continue;
          arrayOfClass[i] = Short.TYPE;
          arrayOfObject[i] = Short.valueOf(paramByteBuffer.getShort());
          continue;
          arrayOfClass[i] = Integer.TYPE;
          arrayOfObject[i] = Integer.valueOf(paramByteBuffer.getInt());
          continue;
          arrayOfClass[i] = Long.TYPE;
          arrayOfObject[i] = Long.valueOf(paramByteBuffer.getLong());
          continue;
          arrayOfClass[i] = Float.TYPE;
          arrayOfObject[i] = Float.valueOf(paramByteBuffer.getFloat());
          continue;
          arrayOfClass[i] = Double.TYPE;
          arrayOfObject[i] = Double.valueOf(paramByteBuffer.getDouble());
        }
      }
    }
    label553:
    return createFailChunk(-3, paramView1);
  }
  
  private Chunk listWindows()
  {
    int k = 0;
    String[] arrayOfString = WindowManagerGlobal.getInstance().getViewRootNames();
    int j = 4;
    int m = arrayOfString.length;
    int i = 0;
    while (i < m)
    {
      j = j + 4 + arrayOfString[i].length() * 2;
      i += 1;
    }
    ByteBuffer localByteBuffer = ByteBuffer.allocate(j);
    localByteBuffer.order(ChunkHandler.CHUNK_ORDER);
    localByteBuffer.putInt(arrayOfString.length);
    j = arrayOfString.length;
    i = k;
    while (i < j)
    {
      String str = arrayOfString[i];
      localByteBuffer.putInt(str.length());
      putString(localByteBuffer, str);
      i += 1;
    }
    return new Chunk(CHUNK_VULW, localByteBuffer);
  }
  
  private Chunk profileView(View paramView1, View paramView2)
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(32768);
    paramView1 = new BufferedWriter(new OutputStreamWriter(localByteArrayOutputStream), 32768);
    try
    {
      ViewDebug.profileViewAndChildren(paramView2, paramView1);
      try
      {
        paramView1.close();
        paramView1 = localByteArrayOutputStream.toByteArray();
        return new Chunk(CHUNK_VUOP, paramView1, 0, paramView1.length);
      }
      catch (IOException paramView1)
      {
        for (;;) {}
      }
      try
      {
        paramView1.close();
        throw paramView2;
      }
      catch (IOException paramView1)
      {
        for (;;) {}
      }
    }
    catch (IOException paramView2)
    {
      paramView2 = paramView2;
      paramView2 = createFailChunk(1, "Unexpected error while profiling view: " + paramView2.getMessage());
      try
      {
        paramView1.close();
        return paramView2;
      }
      catch (IOException paramView1)
      {
        return paramView2;
      }
    }
    finally {}
  }
  
  public static void register()
  {
    DdmServer.registerHandler(CHUNK_VULW, sInstance);
    DdmServer.registerHandler(CHUNK_VURT, sInstance);
    DdmServer.registerHandler(CHUNK_VUOP, sInstance);
  }
  
  private Chunk setLayoutParameter(View paramView1, View paramView2, ByteBuffer paramByteBuffer)
  {
    paramView1 = getString(paramByteBuffer, paramByteBuffer.getInt());
    int i = paramByteBuffer.getInt();
    try
    {
      ViewDebug.setLayoutParameter(paramView2, paramView1, i);
      return null;
    }
    catch (Exception paramView2)
    {
      Log.e("DdmViewDebug", "Exception setting layout parameter: " + paramView2);
    }
    return createFailChunk(-3, "Error accessing field " + paramView1 + ":" + paramView2.getMessage());
  }
  
  public void connected() {}
  
  public void disconnected() {}
  
  public Chunk handleChunk(Chunk paramChunk)
  {
    int i = paramChunk.type;
    if (i == CHUNK_VULW) {
      return listWindows();
    }
    paramChunk = wrapChunk(paramChunk);
    int j = paramChunk.getInt();
    View localView1 = getRootView(paramChunk);
    if (localView1 == null) {
      return createFailChunk(-2, "Invalid View Root");
    }
    if (i == CHUNK_VURT)
    {
      if (j == 1) {
        return dumpHierarchy(localView1, paramChunk);
      }
      if (j == 2) {
        return captureLayers(localView1);
      }
      if (j == 3) {
        return dumpTheme(localView1);
      }
      return createFailChunk(-1, "Unknown view root operation: " + j);
    }
    View localView2 = getTargetView(localView1, paramChunk);
    if (localView2 == null) {
      return createFailChunk(-2, "Invalid target view");
    }
    if (i == CHUNK_VUOP)
    {
      switch (j)
      {
      default: 
        return createFailChunk(-1, "Unknown view operation: " + j);
      case 1: 
        return captureView(localView1, localView2);
      case 2: 
        return dumpDisplayLists(localView1, localView2);
      case 3: 
        return profileView(localView1, localView2);
      case 4: 
        return invokeViewMethod(localView1, localView2, paramChunk);
      }
      return setLayoutParameter(localView1, localView2, paramChunk);
    }
    throw new RuntimeException("Unknown packet " + ChunkHandler.name(i));
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/ddm/DdmHandleViewDebug.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */