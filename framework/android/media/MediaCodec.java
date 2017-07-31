package android.media;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Surface;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.NioUtils;
import java.nio.ReadOnlyBufferException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public final class MediaCodec
{
  public static final int BUFFER_FLAG_CODEC_CONFIG = 2;
  public static final int BUFFER_FLAG_END_OF_STREAM = 4;
  public static final int BUFFER_FLAG_KEY_FRAME = 1;
  public static final int BUFFER_FLAG_SYNC_FRAME = 1;
  private static final int CB_ERROR = 3;
  private static final int CB_INPUT_AVAILABLE = 1;
  private static final int CB_OUTPUT_AVAILABLE = 2;
  private static final int CB_OUTPUT_FORMAT_CHANGE = 4;
  public static final int CONFIGURE_FLAG_ENCODE = 1;
  public static final int CRYPTO_MODE_AES_CBC = 2;
  public static final int CRYPTO_MODE_AES_CTR = 1;
  public static final int CRYPTO_MODE_UNENCRYPTED = 0;
  private static final int EVENT_CALLBACK = 1;
  private static final int EVENT_FRAME_RENDERED = 3;
  private static final int EVENT_SET_CALLBACK = 2;
  public static final int INFO_OUTPUT_BUFFERS_CHANGED = -3;
  public static final int INFO_OUTPUT_FORMAT_CHANGED = -2;
  public static final int INFO_TRY_AGAIN_LATER = -1;
  public static final String PARAMETER_KEY_REQUEST_SYNC_FRAME = "request-sync";
  public static final String PARAMETER_KEY_SUSPEND = "drop-input-frames";
  public static final String PARAMETER_KEY_VIDEO_BITRATE = "video-bitrate";
  public static final int VIDEO_SCALING_MODE_SCALE_TO_FIT = 1;
  public static final int VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING = 2;
  private final Object mBufferLock;
  private ByteBuffer[] mCachedInputBuffers;
  private ByteBuffer[] mCachedOutputBuffers;
  private Callback mCallback;
  private EventHandler mCallbackHandler;
  private final BufferMap mDequeuedInputBuffers = new BufferMap(null);
  private final BufferMap mDequeuedOutputBuffers = new BufferMap(null);
  private final Map<Integer, BufferInfo> mDequeuedOutputInfos = new HashMap();
  private EventHandler mEventHandler;
  private boolean mHasSurface = false;
  private Object mListenerLock = new Object();
  private long mNativeContext;
  private EventHandler mOnFrameRenderedHandler;
  private OnFrameRenderedListener mOnFrameRenderedListener;
  
  static
  {
    System.loadLibrary("media_jni");
    native_init();
  }
  
  private MediaCodec(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    Looper localLooper = Looper.myLooper();
    if (localLooper != null) {
      this.mEventHandler = new EventHandler(this, localLooper);
    }
    for (;;)
    {
      this.mCallbackHandler = this.mEventHandler;
      this.mOnFrameRenderedHandler = this.mEventHandler;
      this.mBufferLock = new Object();
      native_setup(paramString, paramBoolean1, paramBoolean2);
      return;
      localLooper = Looper.getMainLooper();
      if (localLooper != null) {
        this.mEventHandler = new EventHandler(this, localLooper);
      } else {
        this.mEventHandler = null;
      }
    }
  }
  
  private final void cacheBuffers(boolean paramBoolean)
  {
    Object localObject = null;
    try
    {
      ByteBuffer[] arrayOfByteBuffer = getBuffers(paramBoolean);
      localObject = arrayOfByteBuffer;
      invalidateByteBuffers(arrayOfByteBuffer);
      localObject = arrayOfByteBuffer;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      for (;;) {}
    }
    if (paramBoolean)
    {
      this.mCachedInputBuffers = ((ByteBuffer[])localObject);
      return;
    }
    this.mCachedOutputBuffers = ((ByteBuffer[])localObject);
  }
  
  public static MediaCodec createByCodecName(String paramString)
    throws IOException
  {
    return new MediaCodec(paramString, false, false);
  }
  
  public static MediaCodec createDecoderByType(String paramString)
    throws IOException
  {
    return new MediaCodec(paramString, true, false);
  }
  
  public static MediaCodec createEncoderByType(String paramString)
    throws IOException
  {
    return new MediaCodec(paramString, true, true);
  }
  
  public static Surface createPersistentInputSurface()
  {
    return native_createPersistentInputSurface();
  }
  
  private final void freeAllTrackedBuffers()
  {
    synchronized (this.mBufferLock)
    {
      freeByteBuffers(this.mCachedInputBuffers);
      freeByteBuffers(this.mCachedOutputBuffers);
      this.mCachedInputBuffers = null;
      this.mCachedOutputBuffers = null;
      this.mDequeuedInputBuffers.clear();
      this.mDequeuedOutputBuffers.clear();
      return;
    }
  }
  
  private final void freeByteBuffer(ByteBuffer paramByteBuffer)
  {
    if (paramByteBuffer != null) {
      NioUtils.freeDirectBuffer(paramByteBuffer);
    }
  }
  
  private final void freeByteBuffers(ByteBuffer[] paramArrayOfByteBuffer)
  {
    if (paramArrayOfByteBuffer != null)
    {
      int i = 0;
      int j = paramArrayOfByteBuffer.length;
      while (i < j)
      {
        freeByteBuffer(paramArrayOfByteBuffer[i]);
        i += 1;
      }
    }
  }
  
  private final native ByteBuffer getBuffer(boolean paramBoolean, int paramInt);
  
  private final native ByteBuffer[] getBuffers(boolean paramBoolean);
  
  private EventHandler getEventHandlerOn(Handler paramHandler, EventHandler paramEventHandler)
  {
    if (paramHandler == null) {
      return this.mEventHandler;
    }
    paramHandler = paramHandler.getLooper();
    if (paramEventHandler.getLooper() == paramHandler) {
      return paramEventHandler;
    }
    return new EventHandler(this, paramHandler);
  }
  
  private final native Map<String, Object> getFormatNative(boolean paramBoolean);
  
  private final native Image getImage(boolean paramBoolean, int paramInt);
  
  private final native Map<String, Object> getOutputFormatNative(int paramInt);
  
  private final void invalidateByteBuffer(ByteBuffer[] paramArrayOfByteBuffer, int paramInt)
  {
    if ((paramArrayOfByteBuffer != null) && (paramInt >= 0) && (paramInt < paramArrayOfByteBuffer.length))
    {
      paramArrayOfByteBuffer = paramArrayOfByteBuffer[paramInt];
      if (paramArrayOfByteBuffer != null) {
        paramArrayOfByteBuffer.setAccessible(false);
      }
    }
  }
  
  private final void invalidateByteBuffers(ByteBuffer[] paramArrayOfByteBuffer)
  {
    if (paramArrayOfByteBuffer != null)
    {
      int j = paramArrayOfByteBuffer.length;
      int i = 0;
      while (i < j)
      {
        ByteBuffer localByteBuffer = paramArrayOfByteBuffer[i];
        if (localByteBuffer != null) {
          localByteBuffer.setAccessible(false);
        }
        i += 1;
      }
    }
  }
  
  private final native void native_configure(String[] paramArrayOfString, Object[] paramArrayOfObject, Surface paramSurface, MediaCrypto paramMediaCrypto, int paramInt);
  
  private static final native PersistentSurface native_createPersistentInputSurface();
  
  private final native int native_dequeueInputBuffer(long paramLong);
  
  private final native int native_dequeueOutputBuffer(BufferInfo paramBufferInfo, long paramLong);
  
  private native void native_enableOnFrameRenderedListener(boolean paramBoolean);
  
  private final native void native_finalize();
  
  private final native void native_flush();
  
  private static final native void native_init();
  
  private final native void native_queueInputBuffer(int paramInt1, int paramInt2, int paramInt3, long paramLong, int paramInt4)
    throws MediaCodec.CryptoException;
  
  private final native void native_queueSecureInputBuffer(int paramInt1, int paramInt2, CryptoInfo paramCryptoInfo, long paramLong, int paramInt3)
    throws MediaCodec.CryptoException;
  
  private final native void native_release();
  
  private static final native void native_releasePersistentInputSurface(Surface paramSurface);
  
  private final native void native_reset();
  
  private final native void native_setCallback(Callback paramCallback);
  
  private final native void native_setInputSurface(Surface paramSurface);
  
  private native void native_setSurface(Surface paramSurface);
  
  private final native void native_setup(String paramString, boolean paramBoolean1, boolean paramBoolean2);
  
  private final native void native_start();
  
  private final native void native_stop();
  
  private void postEventFromNative(int paramInt1, int paramInt2, int paramInt3, Object paramObject)
  {
    synchronized (this.mListenerLock)
    {
      EventHandler localEventHandler = this.mEventHandler;
      if (paramInt1 == 1) {
        localEventHandler = this.mCallbackHandler;
      }
      while (paramInt1 != 3)
      {
        if (localEventHandler != null) {
          localEventHandler.sendMessage(localEventHandler.obtainMessage(paramInt1, paramInt2, paramInt3, paramObject));
        }
        return;
      }
      localEventHandler = this.mOnFrameRenderedHandler;
    }
  }
  
  private final native void releaseOutputBuffer(int paramInt, boolean paramBoolean1, boolean paramBoolean2, long paramLong);
  
  private final void revalidateByteBuffer(ByteBuffer[] paramArrayOfByteBuffer, int paramInt)
  {
    Object localObject = this.mBufferLock;
    if ((paramArrayOfByteBuffer != null) && (paramInt >= 0)) {}
    try
    {
      if (paramInt < paramArrayOfByteBuffer.length)
      {
        paramArrayOfByteBuffer = paramArrayOfByteBuffer[paramInt];
        if (paramArrayOfByteBuffer != null) {
          paramArrayOfByteBuffer.setAccessible(true);
        }
      }
      return;
    }
    finally {}
  }
  
  private final native void setParameters(String[] paramArrayOfString, Object[] paramArrayOfObject);
  
  private final void validateInputByteBuffer(ByteBuffer[] paramArrayOfByteBuffer, int paramInt)
  {
    if ((paramArrayOfByteBuffer != null) && (paramInt >= 0) && (paramInt < paramArrayOfByteBuffer.length))
    {
      paramArrayOfByteBuffer = paramArrayOfByteBuffer[paramInt];
      if (paramArrayOfByteBuffer != null)
      {
        paramArrayOfByteBuffer.setAccessible(true);
        paramArrayOfByteBuffer.clear();
      }
    }
  }
  
  private final void validateOutputByteBuffer(ByteBuffer[] paramArrayOfByteBuffer, int paramInt, BufferInfo paramBufferInfo)
  {
    if ((paramArrayOfByteBuffer != null) && (paramInt >= 0) && (paramInt < paramArrayOfByteBuffer.length))
    {
      paramArrayOfByteBuffer = paramArrayOfByteBuffer[paramInt];
      if (paramArrayOfByteBuffer != null)
      {
        paramArrayOfByteBuffer.setAccessible(true);
        paramArrayOfByteBuffer.limit(paramBufferInfo.offset + paramBufferInfo.size).position(paramBufferInfo.offset);
      }
    }
  }
  
  public void configure(MediaFormat paramMediaFormat, Surface paramSurface, MediaCrypto paramMediaCrypto, int paramInt)
  {
    Object localObject1 = null;
    Object localObject2 = null;
    if (paramMediaFormat != null)
    {
      localObject1 = paramMediaFormat.getMap();
      paramMediaFormat = new String[((Map)localObject1).size()];
      Object[] arrayOfObject = new Object[((Map)localObject1).size()];
      int i = 0;
      Iterator localIterator = ((Map)localObject1).entrySet().iterator();
      localObject1 = paramMediaFormat;
      localObject2 = arrayOfObject;
      if (localIterator.hasNext())
      {
        localObject1 = (Map.Entry)localIterator.next();
        if (((String)((Map.Entry)localObject1).getKey()).equals("audio-session-id")) {}
        for (;;)
        {
          try
          {
            int j = ((Integer)((Map.Entry)localObject1).getValue()).intValue();
            paramMediaFormat[i] = "audio-hw-sync";
            arrayOfObject[i] = Integer.valueOf(AudioSystem.getAudioHwSyncForSession(j));
            i += 1;
          }
          catch (Exception paramMediaFormat)
          {
            throw new IllegalArgumentException("Wrong Session ID Parameter!");
          }
          paramMediaFormat[i] = ((String)((Map.Entry)localObject1).getKey());
          arrayOfObject[i] = ((Map.Entry)localObject1).getValue();
        }
      }
    }
    if (paramSurface != null) {}
    for (boolean bool = true;; bool = false)
    {
      this.mHasSurface = bool;
      native_configure((String[])localObject1, (Object[])localObject2, paramSurface, paramMediaCrypto, paramInt);
      return;
    }
  }
  
  public final native Surface createInputSurface();
  
  public final int dequeueInputBuffer(long paramLong)
  {
    int i = native_dequeueInputBuffer(paramLong);
    if (i >= 0) {}
    synchronized (this.mBufferLock)
    {
      validateInputByteBuffer(this.mCachedInputBuffers, i);
      return i;
    }
  }
  
  public final int dequeueOutputBuffer(BufferInfo paramBufferInfo, long paramLong)
  {
    int i = native_dequeueOutputBuffer(paramBufferInfo, paramLong);
    localObject = this.mBufferLock;
    if (i == -3) {}
    for (;;)
    {
      try
      {
        cacheBuffers(false);
        return i;
      }
      finally {}
      if (i >= 0)
      {
        validateOutputByteBuffer(this.mCachedOutputBuffers, i, paramBufferInfo);
        if (this.mHasSurface) {
          this.mDequeuedOutputInfos.put(Integer.valueOf(i), paramBufferInfo.dup());
        }
      }
    }
  }
  
  protected void finalize()
  {
    native_finalize();
  }
  
  public final void flush()
  {
    synchronized (this.mBufferLock)
    {
      invalidateByteBuffers(this.mCachedInputBuffers);
      invalidateByteBuffers(this.mCachedOutputBuffers);
      this.mDequeuedInputBuffers.clear();
      this.mDequeuedOutputBuffers.clear();
      native_flush();
      return;
    }
  }
  
  public MediaCodecInfo getCodecInfo()
  {
    return MediaCodecList.getInfoFor(getName());
  }
  
  public ByteBuffer getInputBuffer(int paramInt)
  {
    ByteBuffer localByteBuffer = getBuffer(true, paramInt);
    synchronized (this.mBufferLock)
    {
      invalidateByteBuffer(this.mCachedInputBuffers, paramInt);
      this.mDequeuedInputBuffers.put(paramInt, localByteBuffer);
      return localByteBuffer;
    }
  }
  
  public ByteBuffer[] getInputBuffers()
  {
    if (this.mCachedInputBuffers == null) {
      throw new IllegalStateException();
    }
    return this.mCachedInputBuffers;
  }
  
  public final MediaFormat getInputFormat()
  {
    return new MediaFormat(getFormatNative(true));
  }
  
  public Image getInputImage(int paramInt)
  {
    Image localImage = getImage(true, paramInt);
    synchronized (this.mBufferLock)
    {
      invalidateByteBuffer(this.mCachedInputBuffers, paramInt);
      this.mDequeuedInputBuffers.put(paramInt, localImage);
      return localImage;
    }
  }
  
  public final native String getName();
  
  public ByteBuffer getOutputBuffer(int paramInt)
  {
    ByteBuffer localByteBuffer = getBuffer(false, paramInt);
    synchronized (this.mBufferLock)
    {
      invalidateByteBuffer(this.mCachedOutputBuffers, paramInt);
      this.mDequeuedOutputBuffers.put(paramInt, localByteBuffer);
      return localByteBuffer;
    }
  }
  
  public ByteBuffer[] getOutputBuffers()
  {
    if (this.mCachedOutputBuffers == null) {
      throw new IllegalStateException();
    }
    return this.mCachedOutputBuffers;
  }
  
  public final MediaFormat getOutputFormat()
  {
    return new MediaFormat(getFormatNative(false));
  }
  
  public final MediaFormat getOutputFormat(int paramInt)
  {
    return new MediaFormat(getOutputFormatNative(paramInt));
  }
  
  public Image getOutputImage(int paramInt)
  {
    Image localImage = getImage(false, paramInt);
    synchronized (this.mBufferLock)
    {
      invalidateByteBuffer(this.mCachedOutputBuffers, paramInt);
      this.mDequeuedOutputBuffers.put(paramInt, localImage);
      return localImage;
    }
  }
  
  public final void queueInputBuffer(int paramInt1, int paramInt2, int paramInt3, long paramLong, int paramInt4)
    throws MediaCodec.CryptoException
  {
    synchronized (this.mBufferLock)
    {
      invalidateByteBuffer(this.mCachedInputBuffers, paramInt1);
      this.mDequeuedInputBuffers.remove(paramInt1);
    }
  }
  
  public final void queueSecureInputBuffer(int paramInt1, int paramInt2, CryptoInfo paramCryptoInfo, long paramLong, int paramInt3)
    throws MediaCodec.CryptoException
  {
    synchronized (this.mBufferLock)
    {
      invalidateByteBuffer(this.mCachedInputBuffers, paramInt1);
      this.mDequeuedInputBuffers.remove(paramInt1);
    }
  }
  
  public final void release()
  {
    freeAllTrackedBuffers();
    native_release();
  }
  
  public final void releaseOutputBuffer(int paramInt, long paramLong)
  {
    synchronized (this.mBufferLock)
    {
      invalidateByteBuffer(this.mCachedOutputBuffers, paramInt);
      this.mDequeuedOutputBuffers.remove(paramInt);
      if (this.mHasSurface) {
        BufferInfo localBufferInfo = (BufferInfo)this.mDequeuedOutputInfos.remove(Integer.valueOf(paramInt));
      }
      releaseOutputBuffer(paramInt, true, true, paramLong);
      return;
    }
  }
  
  public final void releaseOutputBuffer(int paramInt, boolean paramBoolean)
  {
    synchronized (this.mBufferLock)
    {
      invalidateByteBuffer(this.mCachedOutputBuffers, paramInt);
      this.mDequeuedOutputBuffers.remove(paramInt);
      if (this.mHasSurface) {
        BufferInfo localBufferInfo = (BufferInfo)this.mDequeuedOutputInfos.remove(Integer.valueOf(paramInt));
      }
      releaseOutputBuffer(paramInt, paramBoolean, false, 0L);
      return;
    }
  }
  
  public final void reset()
  {
    freeAllTrackedBuffers();
    native_reset();
  }
  
  public void setCallback(Callback paramCallback)
  {
    setCallback(paramCallback, null);
  }
  
  public void setCallback(Callback paramCallback, Handler paramHandler)
  {
    if (paramCallback != null) {}
    for (;;)
    {
      synchronized (this.mListenerLock)
      {
        paramHandler = getEventHandlerOn(paramHandler, this.mCallbackHandler);
        if (paramHandler != this.mCallbackHandler)
        {
          this.mCallbackHandler.removeMessages(2);
          this.mCallbackHandler.removeMessages(1);
          this.mCallbackHandler = paramHandler;
        }
        if (this.mCallbackHandler != null)
        {
          paramHandler = this.mCallbackHandler.obtainMessage(2, 0, 0, paramCallback);
          this.mCallbackHandler.sendMessage(paramHandler);
          native_setCallback(paramCallback);
        }
        return;
      }
      if (this.mCallbackHandler != null)
      {
        this.mCallbackHandler.removeMessages(2);
        this.mCallbackHandler.removeMessages(1);
      }
    }
  }
  
  public void setInputSurface(Surface paramSurface)
  {
    if (!(paramSurface instanceof PersistentSurface)) {
      throw new IllegalArgumentException("not a PersistentSurface");
    }
    native_setInputSurface(paramSurface);
  }
  
  public void setOnFrameRenderedListener(OnFrameRenderedListener paramOnFrameRenderedListener, Handler paramHandler)
  {
    for (;;)
    {
      synchronized (this.mListenerLock)
      {
        this.mOnFrameRenderedListener = paramOnFrameRenderedListener;
        if (paramOnFrameRenderedListener != null)
        {
          paramHandler = getEventHandlerOn(paramHandler, this.mOnFrameRenderedHandler);
          if (paramHandler != this.mOnFrameRenderedHandler) {
            this.mOnFrameRenderedHandler.removeMessages(3);
          }
          this.mOnFrameRenderedHandler = paramHandler;
          break label90;
          native_enableOnFrameRenderedListener(bool);
        }
        else if (this.mOnFrameRenderedHandler != null)
        {
          this.mOnFrameRenderedHandler.removeMessages(3);
        }
      }
      label90:
      while (paramOnFrameRenderedListener == null)
      {
        bool = false;
        break;
      }
      boolean bool = true;
    }
  }
  
  public void setOutputSurface(Surface paramSurface)
  {
    if (!this.mHasSurface) {
      throw new IllegalStateException("codec was not configured for an output surface");
    }
    native_setSurface(paramSurface);
  }
  
  public final void setParameters(Bundle paramBundle)
  {
    if (paramBundle == null) {
      return;
    }
    String[] arrayOfString = new String[paramBundle.size()];
    Object[] arrayOfObject = new Object[paramBundle.size()];
    int i = 0;
    Iterator localIterator = paramBundle.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      arrayOfString[i] = str;
      arrayOfObject[i] = paramBundle.get(str);
      i += 1;
    }
    setParameters(arrayOfString, arrayOfObject);
  }
  
  public final native void setVideoScalingMode(int paramInt);
  
  public final native void signalEndOfInputStream();
  
  public final void start()
  {
    native_start();
    synchronized (this.mBufferLock)
    {
      cacheBuffers(true);
      cacheBuffers(false);
      return;
    }
  }
  
  public final void stop()
  {
    native_stop();
    freeAllTrackedBuffers();
    synchronized (this.mListenerLock)
    {
      if (this.mCallbackHandler != null)
      {
        this.mCallbackHandler.removeMessages(2);
        this.mCallbackHandler.removeMessages(1);
      }
      if (this.mOnFrameRenderedHandler != null) {
        this.mOnFrameRenderedHandler.removeMessages(3);
      }
      return;
    }
  }
  
  public static final class BufferInfo
  {
    public int flags;
    public int offset;
    public long presentationTimeUs;
    public int size;
    
    public BufferInfo dup()
    {
      BufferInfo localBufferInfo = new BufferInfo();
      localBufferInfo.set(this.offset, this.size, this.presentationTimeUs, this.flags);
      return localBufferInfo;
    }
    
    public void set(int paramInt1, int paramInt2, long paramLong, int paramInt3)
    {
      this.offset = paramInt1;
      this.size = paramInt2;
      this.presentationTimeUs = paramLong;
      this.flags = paramInt3;
    }
  }
  
  private static class BufferMap
  {
    private final Map<Integer, CodecBuffer> mMap = new HashMap();
    
    public void clear()
    {
      Iterator localIterator = this.mMap.values().iterator();
      while (localIterator.hasNext()) {
        ((CodecBuffer)localIterator.next()).free();
      }
      this.mMap.clear();
    }
    
    public void put(int paramInt, Image paramImage)
    {
      CodecBuffer localCodecBuffer2 = (CodecBuffer)this.mMap.get(Integer.valueOf(paramInt));
      CodecBuffer localCodecBuffer1 = localCodecBuffer2;
      if (localCodecBuffer2 == null)
      {
        localCodecBuffer1 = new CodecBuffer(null);
        this.mMap.put(Integer.valueOf(paramInt), localCodecBuffer1);
      }
      localCodecBuffer1.setImage(paramImage);
    }
    
    public void put(int paramInt, ByteBuffer paramByteBuffer)
    {
      CodecBuffer localCodecBuffer2 = (CodecBuffer)this.mMap.get(Integer.valueOf(paramInt));
      CodecBuffer localCodecBuffer1 = localCodecBuffer2;
      if (localCodecBuffer2 == null)
      {
        localCodecBuffer1 = new CodecBuffer(null);
        this.mMap.put(Integer.valueOf(paramInt), localCodecBuffer1);
      }
      localCodecBuffer1.setByteBuffer(paramByteBuffer);
    }
    
    public void remove(int paramInt)
    {
      CodecBuffer localCodecBuffer = (CodecBuffer)this.mMap.get(Integer.valueOf(paramInt));
      if (localCodecBuffer != null)
      {
        localCodecBuffer.free();
        this.mMap.remove(Integer.valueOf(paramInt));
      }
    }
    
    private static class CodecBuffer
    {
      private ByteBuffer mByteBuffer;
      private Image mImage;
      
      public void free()
      {
        if (this.mByteBuffer != null)
        {
          NioUtils.freeDirectBuffer(this.mByteBuffer);
          this.mByteBuffer = null;
        }
        if (this.mImage != null)
        {
          this.mImage.close();
          this.mImage = null;
        }
      }
      
      public void setByteBuffer(ByteBuffer paramByteBuffer)
      {
        free();
        this.mByteBuffer = paramByteBuffer;
      }
      
      public void setImage(Image paramImage)
      {
        free();
        this.mImage = paramImage;
      }
    }
  }
  
  public static abstract class Callback
  {
    public abstract void onError(MediaCodec paramMediaCodec, MediaCodec.CodecException paramCodecException);
    
    public abstract void onInputBufferAvailable(MediaCodec paramMediaCodec, int paramInt);
    
    public abstract void onOutputBufferAvailable(MediaCodec paramMediaCodec, int paramInt, MediaCodec.BufferInfo paramBufferInfo);
    
    public abstract void onOutputFormatChanged(MediaCodec paramMediaCodec, MediaFormat paramMediaFormat);
  }
  
  public static final class CodecException
    extends IllegalStateException
  {
    private static final int ACTION_RECOVERABLE = 2;
    private static final int ACTION_TRANSIENT = 1;
    public static final int ERROR_INSUFFICIENT_RESOURCE = 1100;
    public static final int ERROR_RECLAIMED = 1101;
    private final int mActionCode;
    private final String mDiagnosticInfo;
    private final int mErrorCode;
    
    CodecException(int paramInt1, int paramInt2, String paramString)
    {
      super();
      this.mErrorCode = paramInt1;
      this.mActionCode = paramInt2;
      if (paramInt1 < 0) {}
      for (paramString = "neg_";; paramString = "")
      {
        this.mDiagnosticInfo = ("android.media.MediaCodec.error_" + paramString + Math.abs(paramInt1));
        return;
      }
    }
    
    public String getDiagnosticInfo()
    {
      return this.mDiagnosticInfo;
    }
    
    public int getErrorCode()
    {
      return this.mErrorCode;
    }
    
    public boolean isRecoverable()
    {
      return this.mActionCode == 2;
    }
    
    public boolean isTransient()
    {
      return this.mActionCode == 1;
    }
  }
  
  public static final class CryptoException
    extends RuntimeException
  {
    public static final int ERROR_INSUFFICIENT_OUTPUT_PROTECTION = 4;
    public static final int ERROR_KEY_EXPIRED = 2;
    public static final int ERROR_NO_KEY = 1;
    public static final int ERROR_RESOURCE_BUSY = 3;
    public static final int ERROR_SESSION_NOT_OPENED = 5;
    public static final int ERROR_UNSUPPORTED_OPERATION = 6;
    private int mErrorCode;
    
    public CryptoException(int paramInt, String paramString)
    {
      super();
      this.mErrorCode = paramInt;
    }
    
    public int getErrorCode()
    {
      return this.mErrorCode;
    }
  }
  
  public static final class CryptoInfo
  {
    public byte[] iv;
    public byte[] key;
    public int mode;
    public int[] numBytesOfClearData;
    public int[] numBytesOfEncryptedData;
    public int numSubSamples;
    private Pattern pattern;
    
    public void set(int paramInt1, int[] paramArrayOfInt1, int[] paramArrayOfInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt2)
    {
      this.numSubSamples = paramInt1;
      this.numBytesOfClearData = paramArrayOfInt1;
      this.numBytesOfEncryptedData = paramArrayOfInt2;
      this.key = paramArrayOfByte1;
      this.iv = paramArrayOfByte2;
      this.mode = paramInt2;
      this.pattern = new Pattern(0, 0);
    }
    
    public void setPattern(Pattern paramPattern)
    {
      this.pattern = paramPattern;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(this.numSubSamples).append(" subsamples, key [");
      int i = 0;
      while (i < this.key.length)
      {
        localStringBuilder.append("0123456789abcdef".charAt((this.key[i] & 0xF0) >> 4));
        localStringBuilder.append("0123456789abcdef".charAt(this.key[i] & 0xF));
        i += 1;
      }
      localStringBuilder.append("], iv [");
      i = 0;
      while (i < this.key.length)
      {
        localStringBuilder.append("0123456789abcdef".charAt((this.iv[i] & 0xF0) >> 4));
        localStringBuilder.append("0123456789abcdef".charAt(this.iv[i] & 0xF));
        i += 1;
      }
      localStringBuilder.append("], clear ");
      localStringBuilder.append(Arrays.toString(this.numBytesOfClearData));
      localStringBuilder.append(", encrypted ");
      localStringBuilder.append(Arrays.toString(this.numBytesOfEncryptedData));
      return localStringBuilder.toString();
    }
    
    public static final class Pattern
    {
      private int mEncryptBlocks;
      private int mSkipBlocks;
      
      public Pattern(int paramInt1, int paramInt2)
      {
        set(paramInt1, paramInt2);
      }
      
      public int getEncryptBlocks()
      {
        return this.mEncryptBlocks;
      }
      
      public int getSkipBlocks()
      {
        return this.mSkipBlocks;
      }
      
      public void set(int paramInt1, int paramInt2)
      {
        this.mEncryptBlocks = paramInt1;
        this.mSkipBlocks = paramInt2;
      }
    }
  }
  
  private class EventHandler
    extends Handler
  {
    private MediaCodec mCodec;
    
    public EventHandler(MediaCodec paramMediaCodec, Looper paramLooper)
    {
      super();
      this.mCodec = paramMediaCodec;
    }
    
    private void handleCallback(Message arg1)
    {
      if (MediaCodec.-get3(MediaCodec.this) == null) {
        return;
      }
      int i;
      switch (???.arg1)
      {
      default: 
        return;
      case 1: 
        i = ???.arg2;
        synchronized (MediaCodec.-get0(MediaCodec.this))
        {
          MediaCodec.-wrap1(MediaCodec.this, MediaCodec.-get1(MediaCodec.this), i);
          MediaCodec.-get3(MediaCodec.this).onInputBufferAvailable(this.mCodec, i);
          return;
        }
      case 2: 
        i = ???.arg2;
        MediaCodec.BufferInfo localBufferInfo = (MediaCodec.BufferInfo)???.obj;
        synchronized (MediaCodec.-get0(MediaCodec.this))
        {
          MediaCodec.-wrap2(MediaCodec.this, MediaCodec.-get2(MediaCodec.this), i, localBufferInfo);
          MediaCodec.-get3(MediaCodec.this).onOutputBufferAvailable(this.mCodec, i, localBufferInfo);
          return;
        }
      case 3: 
        MediaCodec.-get3(MediaCodec.this).onError(this.mCodec, (MediaCodec.CodecException)???.obj);
        return;
      }
      MediaCodec.-get3(MediaCodec.this).onOutputFormatChanged(this.mCodec, new MediaFormat((Map)???.obj));
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      case 1: 
        handleCallback(paramMessage);
        return;
      case 2: 
        MediaCodec.-set0(MediaCodec.this, (MediaCodec.Callback)paramMessage.obj);
        return;
      }
      synchronized (MediaCodec.-get4(MediaCodec.this))
      {
        paramMessage = (Map)paramMessage.obj;
        int i = 0;
        Object localObject2 = paramMessage.get(i + "-media-time-us");
        Object localObject3 = paramMessage.get(i + "-system-nano");
        if ((localObject2 == null) || (localObject3 == null)) {}
        while (MediaCodec.-get5(MediaCodec.this) == null) {
          return;
        }
        MediaCodec.-get5(MediaCodec.this).onFrameRendered(this.mCodec, ((Long)localObject2).longValue(), ((Long)localObject3).longValue());
        i += 1;
      }
    }
  }
  
  public static class MediaImage
    extends Image
  {
    private static final int TYPE_YUV = 1;
    private final ByteBuffer mBuffer;
    private final int mFormat = 35;
    private final int mHeight;
    private final ByteBuffer mInfo;
    private final boolean mIsReadOnly;
    private final Image.Plane[] mPlanes;
    private long mTimestamp;
    private final int mWidth;
    private final int mXOffset;
    private final int mYOffset;
    
    public MediaImage(ByteBuffer paramByteBuffer1, ByteBuffer paramByteBuffer2, boolean paramBoolean, long paramLong, int paramInt1, int paramInt2, Rect paramRect)
    {
      this.mTimestamp = paramLong;
      this.mIsImageValid = true;
      this.mIsReadOnly = paramByteBuffer1.isReadOnly();
      this.mBuffer = paramByteBuffer1.duplicate();
      this.mXOffset = paramInt1;
      this.mYOffset = paramInt2;
      this.mInfo = paramByteBuffer2;
      if (paramByteBuffer2.remaining() == 104)
      {
        int i = paramByteBuffer2.getInt();
        if (i != 1) {
          throw new UnsupportedOperationException("unsupported type: " + i);
        }
        int k = paramByteBuffer2.getInt();
        if (k != 3) {
          throw new RuntimeException("unexpected number of planes: " + k);
        }
        this.mWidth = paramByteBuffer2.getInt();
        this.mHeight = paramByteBuffer2.getInt();
        if ((this.mWidth < 1) || (this.mHeight < 1)) {
          throw new UnsupportedOperationException("unsupported size: " + this.mWidth + "x" + this.mHeight);
        }
        int m = paramByteBuffer2.getInt();
        if (m != 8) {
          throw new UnsupportedOperationException("unsupported bit depth: " + m);
        }
        i = paramByteBuffer2.getInt();
        if (i != 8) {
          throw new UnsupportedOperationException("unsupported allocated bit depth: " + i);
        }
        this.mPlanes = new MediaPlane[k];
        i = 0;
        while (i < k)
        {
          int n = paramByteBuffer2.getInt();
          int i1 = paramByteBuffer2.getInt();
          int i2 = paramByteBuffer2.getInt();
          int i3 = paramByteBuffer2.getInt();
          int i4 = paramByteBuffer2.getInt();
          if (i3 == i4) {
            if (i != 0) {
              break label418;
            }
          }
          label418:
          for (int j = 1; i3 != j; j = 2) {
            throw new UnsupportedOperationException("unexpected subsampling: " + i3 + "x" + i4 + " on plane " + i);
          }
          if ((i1 < 1) || (i2 < 1)) {
            throw new UnsupportedOperationException("unexpected strides: " + i1 + " pixel, " + i2 + " row on plane " + i);
          }
          paramByteBuffer1.clear();
          paramByteBuffer1.position(this.mBuffer.position() + n + paramInt1 / i3 * i1 + paramInt2 / i4 * i2);
          paramByteBuffer1.limit(paramByteBuffer1.position() + Utils.divUp(m, 8) + (this.mHeight / i4 - 1) * i2 + (this.mWidth / i3 - 1) * i1);
          this.mPlanes[i] = new MediaPlane(paramByteBuffer1.slice(), i2, i1);
          i += 1;
        }
      }
      throw new UnsupportedOperationException("unsupported info length: " + paramByteBuffer2.remaining());
      paramByteBuffer1 = paramRect;
      if (paramRect == null) {
        paramByteBuffer1 = new Rect(0, 0, this.mWidth, this.mHeight);
      }
      paramByteBuffer1.offset(-paramInt1, -paramInt2);
      super.setCropRect(paramByteBuffer1);
    }
    
    public void close()
    {
      if (this.mIsImageValid)
      {
        NioUtils.freeDirectBuffer(this.mBuffer);
        this.mIsImageValid = false;
      }
    }
    
    public int getFormat()
    {
      throwISEIfImageIsInvalid();
      return this.mFormat;
    }
    
    public int getHeight()
    {
      throwISEIfImageIsInvalid();
      return this.mHeight;
    }
    
    public Image.Plane[] getPlanes()
    {
      throwISEIfImageIsInvalid();
      return (Image.Plane[])Arrays.copyOf(this.mPlanes, this.mPlanes.length);
    }
    
    public long getTimestamp()
    {
      throwISEIfImageIsInvalid();
      return this.mTimestamp;
    }
    
    public int getWidth()
    {
      throwISEIfImageIsInvalid();
      return this.mWidth;
    }
    
    public void setCropRect(Rect paramRect)
    {
      if (this.mIsReadOnly) {
        throw new ReadOnlyBufferException();
      }
      super.setCropRect(paramRect);
    }
    
    private class MediaPlane
      extends Image.Plane
    {
      private final int mColInc;
      private final ByteBuffer mData;
      private final int mRowInc;
      
      public MediaPlane(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2)
      {
        this.mData = paramByteBuffer;
        this.mRowInc = paramInt1;
        this.mColInc = paramInt2;
      }
      
      public ByteBuffer getBuffer()
      {
        MediaCodec.MediaImage.this.throwISEIfImageIsInvalid();
        return this.mData;
      }
      
      public int getPixelStride()
      {
        MediaCodec.MediaImage.this.throwISEIfImageIsInvalid();
        return this.mColInc;
      }
      
      public int getRowStride()
      {
        MediaCodec.MediaImage.this.throwISEIfImageIsInvalid();
        return this.mRowInc;
      }
    }
  }
  
  public static abstract interface OnFrameRenderedListener
  {
    public abstract void onFrameRendered(MediaCodec paramMediaCodec, long paramLong1, long paramLong2);
  }
  
  static class PersistentSurface
    extends Surface
  {
    private long mPersistentObject;
    
    public void release()
    {
      MediaCodec.-wrap0(this);
      super.release();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/MediaCodec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */