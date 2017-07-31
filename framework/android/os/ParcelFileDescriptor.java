package android.os;

import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.system.StructStat;
import android.util.Log;
import dalvik.system.CloseGuard;
import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramSocket;
import java.net.Socket;
import java.nio.ByteOrder;
import libcore.io.IoUtils;
import libcore.io.Memory;

public class ParcelFileDescriptor
  implements Parcelable, Closeable
{
  public static final Parcelable.Creator<ParcelFileDescriptor> CREATOR = new Parcelable.Creator()
  {
    public ParcelFileDescriptor createFromParcel(Parcel paramAnonymousParcel)
    {
      int i = paramAnonymousParcel.readInt();
      FileDescriptor localFileDescriptor2 = paramAnonymousParcel.readRawFileDescriptor();
      FileDescriptor localFileDescriptor1 = null;
      if (i != 0) {
        localFileDescriptor1 = paramAnonymousParcel.readRawFileDescriptor();
      }
      return new ParcelFileDescriptor(localFileDescriptor2, localFileDescriptor1);
    }
    
    public ParcelFileDescriptor[] newArray(int paramAnonymousInt)
    {
      return new ParcelFileDescriptor[paramAnonymousInt];
    }
  };
  private static final int MAX_STATUS = 1024;
  public static final int MODE_APPEND = 33554432;
  public static final int MODE_CREATE = 134217728;
  public static final int MODE_READ_ONLY = 268435456;
  public static final int MODE_READ_WRITE = 805306368;
  public static final int MODE_TRUNCATE = 67108864;
  @Deprecated
  public static final int MODE_WORLD_READABLE = 1;
  @Deprecated
  public static final int MODE_WORLD_WRITEABLE = 2;
  public static final int MODE_WRITE_ONLY = 536870912;
  private static final String TAG = "ParcelFileDescriptor";
  private volatile boolean mClosed;
  private FileDescriptor mCommFd;
  private final FileDescriptor mFd;
  private final CloseGuard mGuard = CloseGuard.get();
  private Status mStatus;
  private byte[] mStatusBuf;
  private final ParcelFileDescriptor mWrapped;
  
  public ParcelFileDescriptor(ParcelFileDescriptor paramParcelFileDescriptor)
  {
    this.mWrapped = paramParcelFileDescriptor;
    this.mFd = null;
    this.mCommFd = null;
    this.mClosed = true;
  }
  
  public ParcelFileDescriptor(FileDescriptor paramFileDescriptor)
  {
    this(paramFileDescriptor, null);
  }
  
  public ParcelFileDescriptor(FileDescriptor paramFileDescriptor1, FileDescriptor paramFileDescriptor2)
  {
    if (paramFileDescriptor1 == null) {
      throw new NullPointerException("FileDescriptor must not be null");
    }
    this.mWrapped = null;
    this.mFd = paramFileDescriptor1;
    this.mCommFd = paramFileDescriptor2;
    this.mGuard.open("close");
  }
  
  public static ParcelFileDescriptor adoptFd(int paramInt)
  {
    FileDescriptor localFileDescriptor = new FileDescriptor();
    localFileDescriptor.setInt$(paramInt);
    return new ParcelFileDescriptor(localFileDescriptor);
  }
  
  private void closeWithStatus(int paramInt, String paramString)
  {
    if (this.mClosed) {
      return;
    }
    this.mClosed = true;
    this.mGuard.close();
    writeCommStatusAndClose(paramInt, paramString);
    IoUtils.closeQuietly(this.mFd);
    releaseResources();
  }
  
  private static FileDescriptor[] createCommSocketPair()
    throws IOException
  {
    try
    {
      FileDescriptor localFileDescriptor1 = new FileDescriptor();
      FileDescriptor localFileDescriptor2 = new FileDescriptor();
      Os.socketpair(OsConstants.AF_UNIX, OsConstants.SOCK_SEQPACKET, 0, localFileDescriptor1, localFileDescriptor2);
      IoUtils.setBlocking(localFileDescriptor1, false);
      IoUtils.setBlocking(localFileDescriptor2, false);
      return new FileDescriptor[] { localFileDescriptor1, localFileDescriptor2 };
    }
    catch (ErrnoException localErrnoException)
    {
      throw localErrnoException.rethrowAsIOException();
    }
  }
  
  public static ParcelFileDescriptor[] createPipe()
    throws IOException
  {
    try
    {
      Object localObject = Os.pipe();
      ParcelFileDescriptor localParcelFileDescriptor = new ParcelFileDescriptor(localObject[0]);
      localObject = new ParcelFileDescriptor(localObject[1]);
      return new ParcelFileDescriptor[] { localParcelFileDescriptor, localObject };
    }
    catch (ErrnoException localErrnoException)
    {
      throw localErrnoException.rethrowAsIOException();
    }
  }
  
  public static ParcelFileDescriptor[] createReliablePipe()
    throws IOException
  {
    try
    {
      Object localObject = createCommSocketPair();
      FileDescriptor[] arrayOfFileDescriptor = Os.pipe();
      ParcelFileDescriptor localParcelFileDescriptor = new ParcelFileDescriptor(arrayOfFileDescriptor[0], localObject[0]);
      localObject = new ParcelFileDescriptor(arrayOfFileDescriptor[1], localObject[1]);
      return new ParcelFileDescriptor[] { localParcelFileDescriptor, localObject };
    }
    catch (ErrnoException localErrnoException)
    {
      throw localErrnoException.rethrowAsIOException();
    }
  }
  
  public static ParcelFileDescriptor[] createReliableSocketPair()
    throws IOException
  {
    return createReliableSocketPair(OsConstants.SOCK_STREAM);
  }
  
  public static ParcelFileDescriptor[] createReliableSocketPair(int paramInt)
    throws IOException
  {
    try
    {
      Object localObject1 = createCommSocketPair();
      Object localObject2 = new FileDescriptor();
      FileDescriptor localFileDescriptor = new FileDescriptor();
      Os.socketpair(OsConstants.AF_UNIX, paramInt, 0, (FileDescriptor)localObject2, localFileDescriptor);
      localObject2 = new ParcelFileDescriptor((FileDescriptor)localObject2, localObject1[0]);
      localObject1 = new ParcelFileDescriptor(localFileDescriptor, localObject1[1]);
      return new ParcelFileDescriptor[] { localObject2, localObject1 };
    }
    catch (ErrnoException localErrnoException)
    {
      throw localErrnoException.rethrowAsIOException();
    }
  }
  
  public static ParcelFileDescriptor[] createSocketPair()
    throws IOException
  {
    return createSocketPair(OsConstants.SOCK_STREAM);
  }
  
  public static ParcelFileDescriptor[] createSocketPair(int paramInt)
    throws IOException
  {
    try
    {
      Object localObject2 = new FileDescriptor();
      Object localObject1 = new FileDescriptor();
      Os.socketpair(OsConstants.AF_UNIX, paramInt, 0, (FileDescriptor)localObject2, (FileDescriptor)localObject1);
      localObject2 = new ParcelFileDescriptor((FileDescriptor)localObject2);
      localObject1 = new ParcelFileDescriptor((FileDescriptor)localObject1);
      return new ParcelFileDescriptor[] { localObject2, localObject1 };
    }
    catch (ErrnoException localErrnoException)
    {
      throw localErrnoException.rethrowAsIOException();
    }
  }
  
  public static ParcelFileDescriptor dup(FileDescriptor paramFileDescriptor)
    throws IOException
  {
    try
    {
      paramFileDescriptor = new ParcelFileDescriptor(Os.dup(paramFileDescriptor));
      return paramFileDescriptor;
    }
    catch (ErrnoException paramFileDescriptor)
    {
      throw paramFileDescriptor.rethrowAsIOException();
    }
  }
  
  @Deprecated
  public static ParcelFileDescriptor fromData(byte[] paramArrayOfByte, String paramString)
    throws IOException
  {
    Object localObject = null;
    if (paramArrayOfByte == null) {
      return null;
    }
    paramString = new MemoryFile(paramString, paramArrayOfByte.length);
    if (paramArrayOfByte.length > 0) {
      paramString.writeBytes(paramArrayOfByte, 0, 0, paramArrayOfByte.length);
    }
    paramString.deactivate();
    paramString = paramString.getFileDescriptor();
    paramArrayOfByte = (byte[])localObject;
    if (paramString != null) {
      paramArrayOfByte = new ParcelFileDescriptor(paramString);
    }
    return paramArrayOfByte;
  }
  
  public static ParcelFileDescriptor fromDatagramSocket(DatagramSocket paramDatagramSocket)
  {
    Object localObject = null;
    FileDescriptor localFileDescriptor = paramDatagramSocket.getFileDescriptor$();
    paramDatagramSocket = (DatagramSocket)localObject;
    if (localFileDescriptor != null) {
      paramDatagramSocket = new ParcelFileDescriptor(localFileDescriptor);
    }
    return paramDatagramSocket;
  }
  
  public static ParcelFileDescriptor fromFd(int paramInt)
    throws IOException
  {
    Object localObject = new FileDescriptor();
    ((FileDescriptor)localObject).setInt$(paramInt);
    try
    {
      localObject = new ParcelFileDescriptor(Os.dup((FileDescriptor)localObject));
      return (ParcelFileDescriptor)localObject;
    }
    catch (ErrnoException localErrnoException)
    {
      throw localErrnoException.rethrowAsIOException();
    }
  }
  
  public static ParcelFileDescriptor fromFd(FileDescriptor paramFileDescriptor, Handler paramHandler, final OnCloseListener paramOnCloseListener)
    throws IOException
  {
    if (paramHandler == null) {
      throw new IllegalArgumentException("Handler must not be null");
    }
    if (paramOnCloseListener == null) {
      throw new IllegalArgumentException("Listener must not be null");
    }
    FileDescriptor[] arrayOfFileDescriptor = createCommSocketPair();
    paramFileDescriptor = new ParcelFileDescriptor(paramFileDescriptor, arrayOfFileDescriptor[0]);
    paramHandler = paramHandler.getLooper().getQueue();
    paramHandler.addOnFileDescriptorEventListener(arrayOfFileDescriptor[1], 1, new MessageQueue.OnFileDescriptorEventListener()
    {
      public int onFileDescriptorEvents(FileDescriptor paramAnonymousFileDescriptor, int paramAnonymousInt)
      {
        ParcelFileDescriptor.Status localStatus = null;
        if ((paramAnonymousInt & 0x1) != 0) {
          localStatus = ParcelFileDescriptor.-wrap0(paramAnonymousFileDescriptor, new byte['Ѐ']);
        }
        while (localStatus != null)
        {
          this.val$queue.removeOnFileDescriptorEventListener(paramAnonymousFileDescriptor);
          IoUtils.closeQuietly(paramAnonymousFileDescriptor);
          paramOnCloseListener.onClose(localStatus.asIOException());
          return 0;
          if ((paramAnonymousInt & 0x4) != 0) {
            localStatus = new ParcelFileDescriptor.Status(-2);
          }
        }
        return 1;
      }
    });
    return paramFileDescriptor;
  }
  
  public static ParcelFileDescriptor fromSocket(Socket paramSocket)
  {
    Object localObject = null;
    FileDescriptor localFileDescriptor = paramSocket.getFileDescriptor$();
    paramSocket = (Socket)localObject;
    if (localFileDescriptor != null) {
      paramSocket = new ParcelFileDescriptor(localFileDescriptor);
    }
    return paramSocket;
  }
  
  private byte[] getOrCreateStatusBuffer()
  {
    if (this.mStatusBuf == null) {
      this.mStatusBuf = new byte['Ѐ'];
    }
    return this.mStatusBuf;
  }
  
  public static ParcelFileDescriptor open(File paramFile, int paramInt)
    throws FileNotFoundException
  {
    paramFile = openInternal(paramFile, paramInt);
    if (paramFile == null) {
      return null;
    }
    return new ParcelFileDescriptor(paramFile);
  }
  
  public static ParcelFileDescriptor open(File paramFile, int paramInt, Handler paramHandler, OnCloseListener paramOnCloseListener)
    throws IOException
  {
    if (paramHandler == null) {
      throw new IllegalArgumentException("Handler must not be null");
    }
    if (paramOnCloseListener == null) {
      throw new IllegalArgumentException("Listener must not be null");
    }
    paramFile = openInternal(paramFile, paramInt);
    if (paramFile == null) {
      return null;
    }
    return fromFd(paramFile, paramHandler, paramOnCloseListener);
  }
  
  private static FileDescriptor openInternal(File paramFile, int paramInt)
    throws FileNotFoundException
  {
    if ((0x30000000 & paramInt) == 0) {
      throw new IllegalArgumentException("Must specify MODE_READ_ONLY, MODE_WRITE_ONLY, or MODE_READ_WRITE");
    }
    return Parcel.openFileDescriptor(paramFile.getPath(), paramInt);
  }
  
  public static int parseMode(String paramString)
  {
    if ("r".equals(paramString)) {
      return 268435456;
    }
    if (("w".equals(paramString)) || ("wt".equals(paramString))) {
      return 738197504;
    }
    if ("wa".equals(paramString)) {
      return 704643072;
    }
    if ("rw".equals(paramString)) {
      return 939524096;
    }
    if ("rwt".equals(paramString)) {
      return 1006632960;
    }
    throw new IllegalArgumentException("Bad mode '" + paramString + "'");
  }
  
  private static Status readCommStatus(FileDescriptor paramFileDescriptor, byte[] paramArrayOfByte)
  {
    try
    {
      int i = Os.read(paramFileDescriptor, paramArrayOfByte, 0, paramArrayOfByte.length);
      if (i == 0) {
        return new Status(-2);
      }
      int j = Memory.peekInt(paramArrayOfByte, 0, ByteOrder.BIG_ENDIAN);
      if (j == 1) {
        return new Status(j, new String(paramArrayOfByte, 4, i - 4));
      }
      paramFileDescriptor = new Status(j);
      return paramFileDescriptor;
    }
    catch (InterruptedIOException paramFileDescriptor)
    {
      Log.d("ParcelFileDescriptor", "Failed to read status; assuming dead: " + paramFileDescriptor);
      return new Status(-2);
    }
    catch (ErrnoException paramFileDescriptor)
    {
      if (paramFileDescriptor.errno == OsConstants.EAGAIN) {
        return null;
      }
      Log.d("ParcelFileDescriptor", "Failed to read status; assuming dead: " + paramFileDescriptor);
    }
    return new Status(-2);
  }
  
  /* Error */
  private void writeCommStatusAndClose(int paramInt, String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 96	android/os/ParcelFileDescriptor:mCommFd	Ljava/io/FileDescriptor;
    //   4: ifnonnull +34 -> 38
    //   7: aload_2
    //   8: ifnull +29 -> 37
    //   11: ldc 54
    //   13: new 307	java/lang/StringBuilder
    //   16: dup
    //   17: invokespecial 308	java/lang/StringBuilder:<init>	()V
    //   20: ldc_w 363
    //   23: invokevirtual 314	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   26: aload_2
    //   27: invokevirtual 314	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   30: invokevirtual 319	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   33: invokestatic 365	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   36: pop
    //   37: return
    //   38: iload_1
    //   39: iconst_2
    //   40: if_icmpne +12 -> 52
    //   43: ldc 54
    //   45: ldc_w 367
    //   48: invokestatic 365	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   51: pop
    //   52: iload_1
    //   53: iconst_m1
    //   54: if_icmpne +16 -> 70
    //   57: aload_0
    //   58: getfield 96	android/os/ParcelFileDescriptor:mCommFd	Ljava/io/FileDescriptor;
    //   61: invokestatic 137	libcore/io/IoUtils:closeQuietly	(Ljava/io/FileDescriptor;)V
    //   64: aload_0
    //   65: aconst_null
    //   66: putfield 96	android/os/ParcelFileDescriptor:mCommFd	Ljava/io/FileDescriptor;
    //   69: return
    //   70: aload_0
    //   71: aload_0
    //   72: getfield 96	android/os/ParcelFileDescriptor:mCommFd	Ljava/io/FileDescriptor;
    //   75: aload_0
    //   76: invokespecial 369	android/os/ParcelFileDescriptor:getOrCreateStatusBuffer	()[B
    //   79: invokestatic 72	android/os/ParcelFileDescriptor:readCommStatus	(Ljava/io/FileDescriptor;[B)Landroid/os/ParcelFileDescriptor$Status;
    //   82: putfield 371	android/os/ParcelFileDescriptor:mStatus	Landroid/os/ParcelFileDescriptor$Status;
    //   85: aload_0
    //   86: getfield 371	android/os/ParcelFileDescriptor:mStatus	Landroid/os/ParcelFileDescriptor$Status;
    //   89: astore_3
    //   90: aload_3
    //   91: ifnull +16 -> 107
    //   94: aload_0
    //   95: getfield 96	android/os/ParcelFileDescriptor:mCommFd	Ljava/io/FileDescriptor;
    //   98: invokestatic 137	libcore/io/IoUtils:closeQuietly	(Ljava/io/FileDescriptor;)V
    //   101: aload_0
    //   102: aconst_null
    //   103: putfield 96	android/os/ParcelFileDescriptor:mCommFd	Ljava/io/FileDescriptor;
    //   106: return
    //   107: aload_0
    //   108: invokespecial 369	android/os/ParcelFileDescriptor:getOrCreateStatusBuffer	()[B
    //   111: astore_3
    //   112: aload_3
    //   113: iconst_0
    //   114: iload_1
    //   115: getstatic 333	java/nio/ByteOrder:BIG_ENDIAN	Ljava/nio/ByteOrder;
    //   118: invokestatic 375	libcore/io/Memory:pokeInt	([BIILjava/nio/ByteOrder;)V
    //   121: iconst_4
    //   122: istore_1
    //   123: aload_2
    //   124: ifnull +30 -> 154
    //   127: aload_2
    //   128: invokevirtual 378	java/lang/String:getBytes	()[B
    //   131: astore_2
    //   132: aload_2
    //   133: arraylength
    //   134: aload_3
    //   135: arraylength
    //   136: iconst_4
    //   137: isub
    //   138: invokestatic 384	java/lang/Math:min	(II)I
    //   141: istore_1
    //   142: aload_2
    //   143: iconst_0
    //   144: aload_3
    //   145: iconst_4
    //   146: iload_1
    //   147: invokestatic 390	java/lang/System:arraycopy	([BI[BII)V
    //   150: iload_1
    //   151: iconst_4
    //   152: iadd
    //   153: istore_1
    //   154: aload_0
    //   155: getfield 96	android/os/ParcelFileDescriptor:mCommFd	Ljava/io/FileDescriptor;
    //   158: aload_3
    //   159: iconst_0
    //   160: iload_1
    //   161: invokestatic 393	android/system/Os:write	(Ljava/io/FileDescriptor;[BII)I
    //   164: pop
    //   165: aload_0
    //   166: getfield 96	android/os/ParcelFileDescriptor:mCommFd	Ljava/io/FileDescriptor;
    //   169: invokestatic 137	libcore/io/IoUtils:closeQuietly	(Ljava/io/FileDescriptor;)V
    //   172: aload_0
    //   173: aconst_null
    //   174: putfield 96	android/os/ParcelFileDescriptor:mCommFd	Ljava/io/FileDescriptor;
    //   177: return
    //   178: astore_2
    //   179: ldc 54
    //   181: new 307	java/lang/StringBuilder
    //   184: dup
    //   185: invokespecial 308	java/lang/StringBuilder:<init>	()V
    //   188: ldc_w 395
    //   191: invokevirtual 314	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   194: aload_2
    //   195: invokevirtual 349	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   198: invokevirtual 319	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   201: invokestatic 365	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   204: pop
    //   205: goto -40 -> 165
    //   208: astore_2
    //   209: aload_0
    //   210: getfield 96	android/os/ParcelFileDescriptor:mCommFd	Ljava/io/FileDescriptor;
    //   213: invokestatic 137	libcore/io/IoUtils:closeQuietly	(Ljava/io/FileDescriptor;)V
    //   216: aload_0
    //   217: aconst_null
    //   218: putfield 96	android/os/ParcelFileDescriptor:mCommFd	Ljava/io/FileDescriptor;
    //   221: aload_2
    //   222: athrow
    //   223: astore_2
    //   224: ldc 54
    //   226: new 307	java/lang/StringBuilder
    //   229: dup
    //   230: invokespecial 308	java/lang/StringBuilder:<init>	()V
    //   233: ldc_w 395
    //   236: invokevirtual 314	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   239: aload_2
    //   240: invokevirtual 349	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   243: invokevirtual 319	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   246: invokestatic 365	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   249: pop
    //   250: goto -85 -> 165
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	253	0	this	ParcelFileDescriptor
    //   0	253	1	paramInt	int
    //   0	253	2	paramString	String
    //   89	70	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   107	121	178	java/io/InterruptedIOException
    //   127	150	178	java/io/InterruptedIOException
    //   154	165	178	java/io/InterruptedIOException
    //   70	90	208	finally
    //   107	121	208	finally
    //   127	150	208	finally
    //   154	165	208	finally
    //   179	205	208	finally
    //   224	250	208	finally
    //   107	121	223	android/system/ErrnoException
    //   127	150	223	android/system/ErrnoException
    //   154	165	223	android/system/ErrnoException
  }
  
  public boolean canDetectErrors()
  {
    if (this.mWrapped != null) {
      return this.mWrapped.canDetectErrors();
    }
    return this.mCommFd != null;
  }
  
  public void checkError()
    throws IOException
  {
    if (this.mWrapped != null)
    {
      this.mWrapped.checkError();
      return;
    }
    if (this.mStatus == null)
    {
      if (this.mCommFd == null)
      {
        Log.w("ParcelFileDescriptor", "Peer didn't provide a comm channel; unable to check for errors");
        return;
      }
      this.mStatus = readCommStatus(this.mCommFd, getOrCreateStatusBuffer());
    }
    if ((this.mStatus == null) || (this.mStatus.status == 0)) {
      return;
    }
    throw this.mStatus.asIOException();
  }
  
  public void close()
    throws IOException
  {
    if (this.mWrapped != null) {
      try
      {
        this.mWrapped.close();
        return;
      }
      finally
      {
        releaseResources();
      }
    }
    closeWithStatus(0, null);
  }
  
  public void closeWithError(String paramString)
    throws IOException
  {
    if (this.mWrapped != null) {
      try
      {
        this.mWrapped.closeWithError(paramString);
        return;
      }
      finally
      {
        releaseResources();
      }
    }
    if (paramString == null) {
      throw new IllegalArgumentException("Message must not be null");
    }
    closeWithStatus(1, paramString);
  }
  
  public int describeContents()
  {
    if (this.mWrapped != null) {
      return this.mWrapped.describeContents();
    }
    return 1;
  }
  
  public int detachFd()
  {
    if (this.mWrapped != null) {
      return this.mWrapped.detachFd();
    }
    if (this.mClosed) {
      throw new IllegalStateException("Already closed");
    }
    int i = getFd();
    Parcel.clearFileDescriptor(this.mFd);
    writeCommStatusAndClose(2, null);
    this.mClosed = true;
    this.mGuard.close();
    releaseResources();
    return i;
  }
  
  public ParcelFileDescriptor dup()
    throws IOException
  {
    if (this.mWrapped != null) {
      return this.mWrapped.dup();
    }
    return dup(getFileDescriptor());
  }
  
  protected void finalize()
    throws Throwable
  {
    if (this.mWrapped != null) {
      releaseResources();
    }
    if (this.mGuard != null) {
      this.mGuard.warnIfOpen();
    }
    try
    {
      if (!this.mClosed) {
        closeWithStatus(3, null);
      }
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public int getFd()
  {
    if (this.mWrapped != null) {
      return this.mWrapped.getFd();
    }
    if (this.mClosed) {
      throw new IllegalStateException("Already closed");
    }
    return this.mFd.getInt$();
  }
  
  public FileDescriptor getFileDescriptor()
  {
    if (this.mWrapped != null) {
      return this.mWrapped.getFileDescriptor();
    }
    return this.mFd;
  }
  
  public long getStatSize()
  {
    if (this.mWrapped != null) {
      return this.mWrapped.getStatSize();
    }
    try
    {
      StructStat localStructStat = Os.fstat(this.mFd);
      if ((OsConstants.S_ISREG(localStructStat.st_mode)) || (OsConstants.S_ISLNK(localStructStat.st_mode)))
      {
        long l = localStructStat.st_size;
        return l;
      }
      return -1L;
    }
    catch (ErrnoException localErrnoException)
    {
      Log.w("ParcelFileDescriptor", "fstat() failed: " + localErrnoException);
    }
    return -1L;
  }
  
  public void releaseResources() {}
  
  public long seekTo(long paramLong)
    throws IOException
  {
    if (this.mWrapped != null) {
      return this.mWrapped.seekTo(paramLong);
    }
    try
    {
      paramLong = Os.lseek(this.mFd, paramLong, OsConstants.SEEK_SET);
      return paramLong;
    }
    catch (ErrnoException localErrnoException)
    {
      throw localErrnoException.rethrowAsIOException();
    }
  }
  
  public String toString()
  {
    if (this.mWrapped != null) {
      return this.mWrapped.toString();
    }
    return "{ParcelFileDescriptor: " + this.mFd + "}";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    if (this.mWrapped != null) {}
    for (;;)
    {
      try
      {
        this.mWrapped.writeToParcel(paramParcel, paramInt);
        return;
      }
      finally
      {
        releaseResources();
      }
      if (this.mCommFd != null)
      {
        paramParcel.writeInt(1);
        paramParcel.writeFileDescriptor(this.mFd);
        paramParcel.writeFileDescriptor(this.mCommFd);
      }
      while (((paramInt & 0x1) != 0) && (!this.mClosed))
      {
        closeWithStatus(-1, null);
        return;
        paramParcel.writeInt(0);
        paramParcel.writeFileDescriptor(this.mFd);
      }
    }
  }
  
  public static class AutoCloseInputStream
    extends FileInputStream
  {
    private final ParcelFileDescriptor mPfd;
    
    public AutoCloseInputStream(ParcelFileDescriptor paramParcelFileDescriptor)
    {
      super();
      this.mPfd = paramParcelFileDescriptor;
    }
    
    public void close()
      throws IOException
    {
      try
      {
        this.mPfd.close();
        return;
      }
      finally
      {
        super.close();
      }
    }
    
    public int read()
      throws IOException
    {
      int i = super.read();
      if ((i == -1) && (this.mPfd.canDetectErrors())) {
        this.mPfd.checkError();
      }
      return i;
    }
    
    public int read(byte[] paramArrayOfByte)
      throws IOException
    {
      int i = super.read(paramArrayOfByte);
      if ((i == -1) && (this.mPfd.canDetectErrors())) {
        this.mPfd.checkError();
      }
      return i;
    }
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      paramInt1 = super.read(paramArrayOfByte, paramInt1, paramInt2);
      if ((paramInt1 == -1) && (this.mPfd.canDetectErrors())) {
        this.mPfd.checkError();
      }
      return paramInt1;
    }
  }
  
  public static class AutoCloseOutputStream
    extends FileOutputStream
  {
    private final ParcelFileDescriptor mPfd;
    
    public AutoCloseOutputStream(ParcelFileDescriptor paramParcelFileDescriptor)
    {
      super();
      this.mPfd = paramParcelFileDescriptor;
    }
    
    public void close()
      throws IOException
    {
      try
      {
        this.mPfd.close();
        return;
      }
      finally
      {
        super.close();
      }
    }
  }
  
  public static class FileDescriptorDetachedException
    extends IOException
  {
    private static final long serialVersionUID = 955542466045L;
    
    public FileDescriptorDetachedException()
    {
      super();
    }
  }
  
  public static abstract interface OnCloseListener
  {
    public abstract void onClose(IOException paramIOException);
  }
  
  private static class Status
  {
    public static final int DEAD = -2;
    public static final int DETACHED = 2;
    public static final int ERROR = 1;
    public static final int LEAKED = 3;
    public static final int OK = 0;
    public static final int SILENCE = -1;
    public final String msg;
    public final int status;
    
    public Status(int paramInt)
    {
      this(paramInt, null);
    }
    
    public Status(int paramInt, String paramString)
    {
      this.status = paramInt;
      this.msg = paramString;
    }
    
    public IOException asIOException()
    {
      switch (this.status)
      {
      case -1: 
      default: 
        return new IOException("Unknown status: " + this.status);
      case -2: 
        return new IOException("Remote side is dead");
      case 0: 
        return null;
      case 1: 
        return new IOException("Remote error: " + this.msg);
      case 2: 
        return new ParcelFileDescriptor.FileDescriptorDetachedException();
      }
      return new IOException("Remote side was leaked");
    }
    
    public String toString()
    {
      return "{" + this.status + ": " + this.msg + "}";
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/ParcelFileDescriptor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */