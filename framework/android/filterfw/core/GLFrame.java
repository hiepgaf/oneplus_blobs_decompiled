package android.filterfw.core;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Rect;
import android.opengl.GLES20;
import java.nio.ByteBuffer;

public class GLFrame
  extends Frame
{
  public static final int EXISTING_FBO_BINDING = 101;
  public static final int EXISTING_TEXTURE_BINDING = 100;
  public static final int EXTERNAL_TEXTURE = 104;
  public static final int NEW_FBO_BINDING = 103;
  public static final int NEW_TEXTURE_BINDING = 102;
  private int glFrameId = -1;
  private GLEnvironment mGLEnvironment;
  private boolean mOwnsTexture = true;
  
  static
  {
    System.loadLibrary("filterfw");
  }
  
  GLFrame(FrameFormat paramFrameFormat, FrameManager paramFrameManager)
  {
    super(paramFrameFormat, paramFrameManager);
  }
  
  GLFrame(FrameFormat paramFrameFormat, FrameManager paramFrameManager, int paramInt, long paramLong)
  {
    super(paramFrameFormat, paramFrameManager, paramInt, paramLong);
  }
  
  private void assertGLEnvValid()
  {
    if (!this.mGLEnvironment.isContextActive())
    {
      if (GLEnvironment.isAnyContextActive()) {
        throw new RuntimeException("Attempting to access " + this + " with foreign GL " + "context active!");
      }
      throw new RuntimeException("Attempting to access " + this + " with no GL context " + " active!");
    }
  }
  
  private native boolean generateNativeMipMap();
  
  private native boolean getNativeBitmap(Bitmap paramBitmap);
  
  private native byte[] getNativeData();
  
  private native int getNativeFboId();
  
  private native float[] getNativeFloats();
  
  private native int[] getNativeInts();
  
  private native int getNativeTextureId();
  
  private void initNew(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      if (!nativeAllocateExternal(this.mGLEnvironment)) {
        throw new RuntimeException("Could not allocate external GL frame!");
      }
    }
    else if (!nativeAllocate(this.mGLEnvironment, getFormat().getWidth(), getFormat().getHeight())) {
      throw new RuntimeException("Could not allocate GL frame!");
    }
  }
  
  private void initWithFbo(int paramInt)
  {
    int i = getFormat().getWidth();
    int j = getFormat().getHeight();
    if (!nativeAllocateWithFbo(this.mGLEnvironment, paramInt, i, j)) {
      throw new RuntimeException("Could not allocate FBO backed GL frame!");
    }
  }
  
  private void initWithTexture(int paramInt)
  {
    int i = getFormat().getWidth();
    int j = getFormat().getHeight();
    if (!nativeAllocateWithTexture(this.mGLEnvironment, paramInt, i, j)) {
      throw new RuntimeException("Could not allocate texture backed GL frame!");
    }
    this.mOwnsTexture = false;
    markReadOnly();
  }
  
  private native boolean nativeAllocate(GLEnvironment paramGLEnvironment, int paramInt1, int paramInt2);
  
  private native boolean nativeAllocateExternal(GLEnvironment paramGLEnvironment);
  
  private native boolean nativeAllocateWithFbo(GLEnvironment paramGLEnvironment, int paramInt1, int paramInt2, int paramInt3);
  
  private native boolean nativeAllocateWithTexture(GLEnvironment paramGLEnvironment, int paramInt1, int paramInt2, int paramInt3);
  
  private native boolean nativeCopyFromGL(GLFrame paramGLFrame);
  
  private native boolean nativeCopyFromNative(NativeFrame paramNativeFrame);
  
  private native boolean nativeDeallocate();
  
  private native boolean nativeDetachTexFromFbo();
  
  private native boolean nativeFocus();
  
  private native boolean nativeReattachTexToFbo();
  
  private native boolean nativeResetParams();
  
  private native boolean setNativeBitmap(Bitmap paramBitmap, int paramInt);
  
  private native boolean setNativeData(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  private native boolean setNativeFloats(float[] paramArrayOfFloat);
  
  private native boolean setNativeInts(int[] paramArrayOfInt);
  
  private native boolean setNativeTextureParam(int paramInt1, int paramInt2);
  
  private native boolean setNativeViewport(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  void flushGPU(String paramString)
  {
    StopWatchMap localStopWatchMap = GLFrameTimer.get();
    if (localStopWatchMap.LOG_MFF_RUNNING_TIMES)
    {
      localStopWatchMap.start("glFinish " + paramString);
      GLES20.glFinish();
      localStopWatchMap.stop("glFinish " + paramString);
    }
  }
  
  public void focus()
  {
    if (!nativeFocus()) {
      throw new RuntimeException("Could not focus on GLFrame for drawing!");
    }
  }
  
  public void generateMipMap()
  {
    assertFrameMutable();
    assertGLEnvValid();
    if (!generateNativeMipMap()) {
      throw new RuntimeException("Could not generate mip-map for GL frame!");
    }
  }
  
  public Bitmap getBitmap()
  {
    assertGLEnvValid();
    flushGPU("getBitmap");
    Bitmap localBitmap = Bitmap.createBitmap(getFormat().getWidth(), getFormat().getHeight(), Bitmap.Config.ARGB_8888);
    if (!getNativeBitmap(localBitmap)) {
      throw new RuntimeException("Could not get bitmap data from GL frame!");
    }
    return localBitmap;
  }
  
  public ByteBuffer getData()
  {
    assertGLEnvValid();
    flushGPU("getData");
    return ByteBuffer.wrap(getNativeData());
  }
  
  public int getFboId()
  {
    return getNativeFboId();
  }
  
  public float[] getFloats()
  {
    assertGLEnvValid();
    flushGPU("getFloats");
    return getNativeFloats();
  }
  
  public GLEnvironment getGLEnvironment()
  {
    return this.mGLEnvironment;
  }
  
  public int[] getInts()
  {
    assertGLEnvValid();
    flushGPU("getInts");
    return getNativeInts();
  }
  
  public Object getObjectValue()
  {
    assertGLEnvValid();
    return ByteBuffer.wrap(getNativeData());
  }
  
  public int getTextureId()
  {
    return getNativeTextureId();
  }
  
  /* Error */
  protected boolean hasNativeAllocation()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 37	android/filterfw/core/GLFrame:glFrameId	I
    //   6: istore_1
    //   7: iload_1
    //   8: iconst_m1
    //   9: if_icmpeq +9 -> 18
    //   12: iconst_1
    //   13: istore_2
    //   14: aload_0
    //   15: monitorexit
    //   16: iload_2
    //   17: ireturn
    //   18: iconst_0
    //   19: istore_2
    //   20: goto -6 -> 14
    //   23: astore_3
    //   24: aload_0
    //   25: monitorexit
    //   26: aload_3
    //   27: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	28	0	this	GLFrame
    //   6	4	1	i	int
    //   13	7	2	bool	boolean
    //   23	4	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	7	23	finally
  }
  
  void init(GLEnvironment paramGLEnvironment)
  {
    FrameFormat localFrameFormat = getFormat();
    this.mGLEnvironment = paramGLEnvironment;
    if (localFrameFormat.getBytesPerSample() != 4) {
      throw new IllegalArgumentException("GL frames must have 4 bytes per sample!");
    }
    if (localFrameFormat.getDimensionCount() != 2) {
      throw new IllegalArgumentException("GL frames must be 2-dimensional!");
    }
    if (getFormat().getSize() < 0) {
      throw new IllegalArgumentException("Initializing GL frame with zero size!");
    }
    int i = getBindingType();
    boolean bool = true;
    if (i == 0) {
      initNew(false);
    }
    for (;;)
    {
      setReusable(bool);
      return;
      if (i == 104)
      {
        initNew(true);
        bool = false;
      }
      else if (i == 100)
      {
        initWithTexture((int)getBindingId());
      }
      else if (i == 101)
      {
        initWithFbo((int)getBindingId());
      }
      else if (i == 102)
      {
        initWithTexture((int)getBindingId());
      }
      else
      {
        if (i != 103) {
          break;
        }
        initWithFbo((int)getBindingId());
      }
    }
    throw new RuntimeException("Attempting to create GL frame with unknown binding type " + i + "!");
  }
  
  protected void onFrameFetch()
  {
    if (!this.mOwnsTexture) {
      nativeReattachTexToFbo();
    }
  }
  
  protected void onFrameStore()
  {
    if (!this.mOwnsTexture) {
      nativeDetachTexFromFbo();
    }
  }
  
  protected void releaseNativeAllocation()
  {
    try
    {
      nativeDeallocate();
      this.glFrameId = -1;
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  protected void reset(FrameFormat paramFrameFormat)
  {
    if (!nativeResetParams()) {
      throw new RuntimeException("Could not reset GLFrame texture parameters!");
    }
    super.reset(paramFrameFormat);
  }
  
  public void setBitmap(Bitmap paramBitmap)
  {
    assertFrameMutable();
    assertGLEnvValid();
    if ((getFormat().getWidth() != paramBitmap.getWidth()) || (getFormat().getHeight() != paramBitmap.getHeight())) {
      throw new RuntimeException("Bitmap dimensions do not match GL frame dimensions!");
    }
    paramBitmap = convertBitmapToRGBA(paramBitmap);
    if (!setNativeBitmap(paramBitmap, paramBitmap.getByteCount())) {
      throw new RuntimeException("Could not set GL frame bitmap data!");
    }
  }
  
  public void setData(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2)
  {
    assertFrameMutable();
    assertGLEnvValid();
    paramByteBuffer = paramByteBuffer.array();
    if (getFormat().getSize() != paramByteBuffer.length) {
      throw new RuntimeException("Data size in setData does not match GL frame size!");
    }
    if (!setNativeData(paramByteBuffer, paramInt1, paramInt2)) {
      throw new RuntimeException("Could not set GL frame data!");
    }
  }
  
  public void setDataFromFrame(Frame paramFrame)
  {
    assertGLEnvValid();
    if (getFormat().getSize() < paramFrame.getFormat().getSize()) {
      throw new RuntimeException("Attempting to assign frame of size " + paramFrame.getFormat().getSize() + " to " + "smaller GL frame of size " + getFormat().getSize() + "!");
    }
    if ((paramFrame instanceof NativeFrame))
    {
      nativeCopyFromNative(paramFrame);
      return;
    }
    if ((paramFrame instanceof GLFrame))
    {
      nativeCopyFromGL(paramFrame);
      return;
    }
    if ((paramFrame instanceof SimpleFrame))
    {
      setObjectValue(paramFrame.getObjectValue());
      return;
    }
    super.setDataFromFrame(paramFrame);
  }
  
  public void setFloats(float[] paramArrayOfFloat)
  {
    assertFrameMutable();
    assertGLEnvValid();
    if (!setNativeFloats(paramArrayOfFloat)) {
      throw new RuntimeException("Could not set int values for GL frame!");
    }
  }
  
  public void setInts(int[] paramArrayOfInt)
  {
    assertFrameMutable();
    assertGLEnvValid();
    if (!setNativeInts(paramArrayOfInt)) {
      throw new RuntimeException("Could not set int values for GL frame!");
    }
  }
  
  public void setTextureParameter(int paramInt1, int paramInt2)
  {
    assertFrameMutable();
    assertGLEnvValid();
    if (!setNativeTextureParam(paramInt1, paramInt2)) {
      throw new RuntimeException("Could not set texture value " + paramInt1 + " = " + paramInt2 + " " + "for GLFrame!");
    }
  }
  
  public void setViewport(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    assertFrameMutable();
    setNativeViewport(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void setViewport(Rect paramRect)
  {
    assertFrameMutable();
    setNativeViewport(paramRect.left, paramRect.top, paramRect.right - paramRect.left, paramRect.bottom - paramRect.top);
  }
  
  public String toString()
  {
    return "GLFrame id: " + this.glFrameId + " (" + getFormat() + ") with texture ID " + getTextureId() + ", FBO ID " + getFboId();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/GLFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */