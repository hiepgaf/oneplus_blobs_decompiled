package android.speech.tts;

import android.media.AudioFormat;
import android.util.Log;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

class FileSynthesisCallback
  extends AbstractSynthesisCallback
{
  private static final boolean DBG = false;
  private static final int MAX_AUDIO_BUFFER_SIZE = 8192;
  private static final String TAG = "FileSynthesisRequest";
  private static final short WAV_FORMAT_PCM = 1;
  private static final int WAV_HEADER_LENGTH = 44;
  private int mAudioFormat;
  private int mChannelCount;
  private final TextToSpeechService.UtteranceProgressDispatcher mDispatcher;
  private boolean mDone = false;
  private FileChannel mFileChannel;
  private int mSampleRateInHz;
  private boolean mStarted = false;
  private final Object mStateLock = new Object();
  protected int mStatusCode;
  
  FileSynthesisCallback(FileChannel paramFileChannel, TextToSpeechService.UtteranceProgressDispatcher paramUtteranceProgressDispatcher, boolean paramBoolean)
  {
    super(paramBoolean);
    this.mFileChannel = paramFileChannel;
    this.mDispatcher = paramUtteranceProgressDispatcher;
    this.mStatusCode = 0;
  }
  
  private void cleanUp()
  {
    closeFile();
  }
  
  private void closeFile()
  {
    this.mFileChannel = null;
  }
  
  private ByteBuffer makeWavHeader(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramInt2 = AudioFormat.getBytesPerSample(paramInt2);
    short s1 = (short)(paramInt2 * paramInt3);
    short s2 = (short)(paramInt2 * 8);
    ByteBuffer localByteBuffer = ByteBuffer.wrap(new byte[44]);
    localByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    localByteBuffer.put(new byte[] { 82, 73, 70, 70 });
    localByteBuffer.putInt(paramInt4 + 44 - 8);
    localByteBuffer.put(new byte[] { 87, 65, 86, 69 });
    localByteBuffer.put(new byte[] { 102, 109, 116, 32 });
    localByteBuffer.putInt(16);
    localByteBuffer.putShort((short)1);
    localByteBuffer.putShort((short)paramInt3);
    localByteBuffer.putInt(paramInt1);
    localByteBuffer.putInt(paramInt1 * paramInt2 * paramInt3);
    localByteBuffer.putShort(s1);
    localByteBuffer.putShort(s2);
    localByteBuffer.put(new byte[] { 100, 97, 116, 97 });
    localByteBuffer.putInt(paramInt4);
    localByteBuffer.flip();
    return localByteBuffer;
  }
  
  public int audioAvailable(byte[] arg1, int paramInt1, int paramInt2)
  {
    FileChannel localFileChannel;
    synchronized (this.mStateLock)
    {
      if (this.mStatusCode == -2)
      {
        paramInt1 = errorCodeOnStop();
        return paramInt1;
      }
      int i = this.mStatusCode;
      if (i != 0) {
        return -1;
      }
      if (this.mFileChannel == null)
      {
        Log.e("FileSynthesisRequest", "File not open");
        this.mStatusCode = -5;
        return -1;
      }
      if (!this.mStarted)
      {
        Log.e("FileSynthesisRequest", "Start method was not called");
        return -1;
      }
      localFileChannel = this.mFileChannel;
      ??? = new byte[paramInt2];
      System.arraycopy(???, paramInt1, (byte[])???, 0, paramInt2);
      this.mDispatcher.dispatchOnAudioAvailable((byte[])???);
    }
  }
  
  /* Error */
  public int done()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 43	android/speech/tts/FileSynthesisCallback:mStateLock	Ljava/lang/Object;
    //   4: astore 4
    //   6: aload 4
    //   8: monitorenter
    //   9: aload_0
    //   10: getfield 47	android/speech/tts/FileSynthesisCallback:mDone	Z
    //   13: ifeq +16 -> 29
    //   16: ldc 14
    //   18: ldc -95
    //   20: invokestatic 164	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   23: pop
    //   24: aload 4
    //   26: monitorexit
    //   27: iconst_m1
    //   28: ireturn
    //   29: aload_0
    //   30: getfield 53	android/speech/tts/FileSynthesisCallback:mStatusCode	I
    //   33: bipush -2
    //   35: if_icmpne +13 -> 48
    //   38: aload_0
    //   39: invokevirtual 120	android/speech/tts/AbstractSynthesisCallback:errorCodeOnStop	()I
    //   42: istore_1
    //   43: aload 4
    //   45: monitorexit
    //   46: iload_1
    //   47: ireturn
    //   48: aload_0
    //   49: getfield 53	android/speech/tts/FileSynthesisCallback:mStatusCode	I
    //   52: ifeq +30 -> 82
    //   55: aload_0
    //   56: getfield 53	android/speech/tts/FileSynthesisCallback:mStatusCode	I
    //   59: bipush -2
    //   61: if_icmpeq +21 -> 82
    //   64: aload_0
    //   65: getfield 51	android/speech/tts/FileSynthesisCallback:mDispatcher	Landroid/speech/tts/TextToSpeechService$UtteranceProgressDispatcher;
    //   68: aload_0
    //   69: getfield 53	android/speech/tts/FileSynthesisCallback:mStatusCode	I
    //   72: invokeinterface 168 2 0
    //   77: aload 4
    //   79: monitorexit
    //   80: iconst_m1
    //   81: ireturn
    //   82: aload_0
    //   83: getfield 49	android/speech/tts/FileSynthesisCallback:mFileChannel	Ljava/nio/channels/FileChannel;
    //   86: ifnonnull +16 -> 102
    //   89: ldc 14
    //   91: ldc 122
    //   93: invokestatic 128	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   96: pop
    //   97: aload 4
    //   99: monitorexit
    //   100: iconst_m1
    //   101: ireturn
    //   102: aload_0
    //   103: iconst_1
    //   104: putfield 47	android/speech/tts/FileSynthesisCallback:mDone	Z
    //   107: aload_0
    //   108: getfield 49	android/speech/tts/FileSynthesisCallback:mFileChannel	Ljava/nio/channels/FileChannel;
    //   111: astore 5
    //   113: aload_0
    //   114: getfield 170	android/speech/tts/FileSynthesisCallback:mSampleRateInHz	I
    //   117: istore_1
    //   118: aload_0
    //   119: getfield 172	android/speech/tts/FileSynthesisCallback:mAudioFormat	I
    //   122: istore_2
    //   123: aload_0
    //   124: getfield 174	android/speech/tts/FileSynthesisCallback:mChannelCount	I
    //   127: istore_3
    //   128: aload 4
    //   130: monitorexit
    //   131: aload 5
    //   133: lconst_0
    //   134: invokevirtual 178	java/nio/channels/FileChannel:position	(J)Ljava/nio/channels/FileChannel;
    //   137: pop
    //   138: aload 5
    //   140: aload_0
    //   141: iload_1
    //   142: iload_2
    //   143: iload_3
    //   144: aload 5
    //   146: invokevirtual 182	java/nio/channels/FileChannel:size	()J
    //   149: ldc2_w 183
    //   152: lsub
    //   153: l2i
    //   154: invokespecial 186	android/speech/tts/FileSynthesisCallback:makeWavHeader	(IIII)Ljava/nio/ByteBuffer;
    //   157: invokevirtual 151	java/nio/channels/FileChannel:write	(Ljava/nio/ByteBuffer;)I
    //   160: pop
    //   161: aload_0
    //   162: getfield 43	android/speech/tts/FileSynthesisCallback:mStateLock	Ljava/lang/Object;
    //   165: astore 4
    //   167: aload 4
    //   169: monitorenter
    //   170: aload_0
    //   171: invokespecial 58	android/speech/tts/FileSynthesisCallback:closeFile	()V
    //   174: aload_0
    //   175: getfield 51	android/speech/tts/FileSynthesisCallback:mDispatcher	Landroid/speech/tts/TextToSpeechService$UtteranceProgressDispatcher;
    //   178: invokeinterface 189 1 0
    //   183: aload 4
    //   185: monitorexit
    //   186: iconst_0
    //   187: ireturn
    //   188: astore 5
    //   190: aload 4
    //   192: monitorexit
    //   193: aload 5
    //   195: athrow
    //   196: astore 5
    //   198: aload 4
    //   200: monitorexit
    //   201: aload 5
    //   203: athrow
    //   204: astore 4
    //   206: ldc 14
    //   208: ldc -103
    //   210: aload 4
    //   212: invokestatic 156	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   215: pop
    //   216: aload_0
    //   217: getfield 43	android/speech/tts/FileSynthesisCallback:mStateLock	Ljava/lang/Object;
    //   220: astore 4
    //   222: aload 4
    //   224: monitorenter
    //   225: aload_0
    //   226: invokespecial 158	android/speech/tts/FileSynthesisCallback:cleanUp	()V
    //   229: aload 4
    //   231: monitorexit
    //   232: iconst_m1
    //   233: ireturn
    //   234: astore 5
    //   236: aload 4
    //   238: monitorexit
    //   239: aload 5
    //   241: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	242	0	this	FileSynthesisCallback
    //   42	100	1	i	int
    //   122	21	2	j	int
    //   127	17	3	k	int
    //   204	7	4	localIOException	IOException
    //   111	34	5	localFileChannel	FileChannel
    //   188	6	5	localObject3	Object
    //   196	6	5	localObject4	Object
    //   234	6	5	localObject5	Object
    // Exception table:
    //   from	to	target	type
    //   9	24	188	finally
    //   29	43	188	finally
    //   48	77	188	finally
    //   82	97	188	finally
    //   102	128	188	finally
    //   170	183	196	finally
    //   131	170	204	java/io/IOException
    //   183	186	204	java/io/IOException
    //   198	204	204	java/io/IOException
    //   225	229	234	finally
  }
  
  public void error()
  {
    error(-3);
  }
  
  public void error(int paramInt)
  {
    synchronized (this.mStateLock)
    {
      boolean bool = this.mDone;
      if (bool) {
        return;
      }
      cleanUp();
      this.mStatusCode = paramInt;
      return;
    }
  }
  
  public int getMaxBufferSize()
  {
    return 8192;
  }
  
  public boolean hasFinished()
  {
    synchronized (this.mStateLock)
    {
      boolean bool = this.mDone;
      return bool;
    }
  }
  
  public boolean hasStarted()
  {
    synchronized (this.mStateLock)
    {
      boolean bool = this.mStarted;
      return bool;
    }
  }
  
  public int start(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramInt2 != 3) && (paramInt2 != 2) && (paramInt2 != 4)) {
      Log.e("FileSynthesisRequest", "Audio format encoding " + paramInt2 + " not supported. Please use one " + "of AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT or " + "AudioFormat.ENCODING_PCM_FLOAT");
    }
    this.mDispatcher.dispatchOnBeginSynthesis(paramInt1, paramInt2, paramInt3);
    FileChannel localFileChannel;
    synchronized (this.mStateLock)
    {
      if (this.mStatusCode == -2)
      {
        paramInt1 = errorCodeOnStop();
        return paramInt1;
      }
      int i = this.mStatusCode;
      if (i != 0) {
        return -1;
      }
      if (this.mStarted)
      {
        Log.e("FileSynthesisRequest", "Start called twice");
        return -1;
      }
      this.mStarted = true;
      this.mSampleRateInHz = paramInt1;
      this.mAudioFormat = paramInt2;
      this.mChannelCount = paramInt3;
      this.mDispatcher.dispatchOnStart();
      localFileChannel = this.mFileChannel;
    }
  }
  
  void stop()
  {
    synchronized (this.mStateLock)
    {
      boolean bool = this.mDone;
      if (bool) {
        return;
      }
      int i = this.mStatusCode;
      if (i == -2) {
        return;
      }
      this.mStatusCode = -2;
      cleanUp();
      this.mDispatcher.dispatchOnStop();
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/speech/tts/FileSynthesisCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */