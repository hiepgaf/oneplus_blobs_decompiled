package android.graphics.pdf;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.ParcelFileDescriptor;
import dalvik.system.CloseGuard;
import java.io.IOException;

public final class PdfRenderer
  implements AutoCloseable
{
  static final Object sPdfiumLock = new Object();
  private final CloseGuard mCloseGuard;
  private Page mCurrentPage;
  private ParcelFileDescriptor mInput;
  private final long mNativeDocument;
  private final int mPageCount;
  private final Point mTempPoint;
  
  /* Error */
  public PdfRenderer(ParcelFileDescriptor arg1)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 56	java/lang/Object:<init>	()V
    //   4: aload_0
    //   5: invokestatic 69	dalvik/system/CloseGuard:get	()Ldalvik/system/CloseGuard;
    //   8: putfield 71	android/graphics/pdf/PdfRenderer:mCloseGuard	Ldalvik/system/CloseGuard;
    //   11: aload_0
    //   12: new 73	android/graphics/Point
    //   15: dup
    //   16: invokespecial 74	android/graphics/Point:<init>	()V
    //   19: putfield 32	android/graphics/pdf/PdfRenderer:mTempPoint	Landroid/graphics/Point;
    //   22: aload_1
    //   23: ifnonnull +13 -> 36
    //   26: new 76	java/lang/NullPointerException
    //   29: dup
    //   30: ldc 78
    //   32: invokespecial 81	java/lang/NullPointerException:<init>	(Ljava/lang/String;)V
    //   35: athrow
    //   36: getstatic 87	libcore/io/Libcore:os	Llibcore/io/Os;
    //   39: aload_1
    //   40: invokevirtual 93	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
    //   43: lconst_0
    //   44: getstatic 98	android/system/OsConstants:SEEK_SET	I
    //   47: invokeinterface 104 5 0
    //   52: pop2
    //   53: getstatic 87	libcore/io/Libcore:os	Llibcore/io/Os;
    //   56: aload_1
    //   57: invokevirtual 93	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
    //   60: invokeinterface 108 2 0
    //   65: getfield 113	android/system/StructStat:st_size	J
    //   68: lstore_2
    //   69: aload_0
    //   70: aload_1
    //   71: putfield 115	android/graphics/pdf/PdfRenderer:mInput	Landroid/os/ParcelFileDescriptor;
    //   74: getstatic 58	android/graphics/pdf/PdfRenderer:sPdfiumLock	Ljava/lang/Object;
    //   77: astore_1
    //   78: aload_1
    //   79: monitorenter
    //   80: aload_0
    //   81: aload_0
    //   82: getfield 115	android/graphics/pdf/PdfRenderer:mInput	Landroid/os/ParcelFileDescriptor;
    //   85: invokevirtual 119	android/os/ParcelFileDescriptor:getFd	()I
    //   88: lload_2
    //   89: invokestatic 123	android/graphics/pdf/PdfRenderer:nativeCreate	(IJ)J
    //   92: putfield 27	android/graphics/pdf/PdfRenderer:mNativeDocument	J
    //   95: aload_0
    //   96: aload_0
    //   97: getfield 27	android/graphics/pdf/PdfRenderer:mNativeDocument	J
    //   100: invokestatic 127	android/graphics/pdf/PdfRenderer:nativeGetPageCount	(J)I
    //   103: putfield 129	android/graphics/pdf/PdfRenderer:mPageCount	I
    //   106: aload_1
    //   107: monitorexit
    //   108: aload_0
    //   109: getfield 71	android/graphics/pdf/PdfRenderer:mCloseGuard	Ldalvik/system/CloseGuard;
    //   112: ldc -125
    //   114: invokevirtual 134	dalvik/system/CloseGuard:open	(Ljava/lang/String;)V
    //   117: return
    //   118: astore_1
    //   119: new 136	java/lang/IllegalArgumentException
    //   122: dup
    //   123: ldc -118
    //   125: invokespecial 139	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   128: athrow
    //   129: astore 4
    //   131: aload_1
    //   132: monitorexit
    //   133: aload 4
    //   135: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	136	0	this	PdfRenderer
    //   68	21	2	l	long
    //   129	5	4	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   36	69	118	android/system/ErrnoException
    //   80	106	129	finally
  }
  
  private void doClose()
  {
    if (this.mCurrentPage != null) {
      this.mCurrentPage.close();
    }
    synchronized (sPdfiumLock)
    {
      nativeClose(this.mNativeDocument);
    }
    try
    {
      this.mInput.close();
      this.mInput = null;
      this.mCloseGuard.close();
      return;
      localObject2 = finally;
      throw ((Throwable)localObject2);
    }
    catch (IOException localIOException)
    {
      for (;;) {}
    }
  }
  
  private static native void nativeClose(long paramLong);
  
  private static native void nativeClosePage(long paramLong);
  
  private static native long nativeCreate(int paramInt, long paramLong);
  
  private static native int nativeGetPageCount(long paramLong);
  
  private static native long nativeOpenPageAndGetSize(long paramLong, int paramInt, Point paramPoint);
  
  private static native void nativeRenderPage(long paramLong1, long paramLong2, Bitmap paramBitmap, int paramInt1, int paramInt2, int paramInt3, int paramInt4, long paramLong3, int paramInt5);
  
  private static native boolean nativeScaleForPrinting(long paramLong);
  
  private void throwIfClosed()
  {
    if (this.mInput == null) {
      throw new IllegalStateException("Already closed");
    }
  }
  
  private void throwIfPageNotInDocument(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.mPageCount)) {
      throw new IllegalArgumentException("Invalid page index");
    }
  }
  
  private void throwIfPageOpened()
  {
    if (this.mCurrentPage != null) {
      throw new IllegalStateException("Current page not closed");
    }
  }
  
  public void close()
  {
    throwIfClosed();
    throwIfPageOpened();
    doClose();
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      this.mCloseGuard.warnIfOpen();
      if (this.mInput != null) {
        doClose();
      }
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public int getPageCount()
  {
    throwIfClosed();
    return this.mPageCount;
  }
  
  public Page openPage(int paramInt)
  {
    throwIfClosed();
    throwIfPageOpened();
    throwIfPageNotInDocument(paramInt);
    this.mCurrentPage = new Page(paramInt, null);
    return this.mCurrentPage;
  }
  
  public boolean shouldScaleForPrinting()
  {
    throwIfClosed();
    synchronized (sPdfiumLock)
    {
      boolean bool = nativeScaleForPrinting(this.mNativeDocument);
      return bool;
    }
  }
  
  public final class Page
    implements AutoCloseable
  {
    public static final int RENDER_MODE_FOR_DISPLAY = 1;
    public static final int RENDER_MODE_FOR_PRINT = 2;
    private final CloseGuard mCloseGuard = CloseGuard.get();
    private final int mHeight;
    private final int mIndex;
    private long mNativePage;
    private final int mWidth;
    
    private Page(int paramInt)
    {
      Point localPoint = PdfRenderer.-get1(PdfRenderer.this);
      synchronized (PdfRenderer.sPdfiumLock)
      {
        this.mNativePage = PdfRenderer.-wrap0(PdfRenderer.-get0(PdfRenderer.this), paramInt, localPoint);
        this.mIndex = paramInt;
        this.mWidth = localPoint.x;
        this.mHeight = localPoint.y;
        this.mCloseGuard.open("close");
        return;
      }
    }
    
    private void doClose()
    {
      synchronized (PdfRenderer.sPdfiumLock)
      {
        PdfRenderer.-wrap1(this.mNativePage);
        this.mNativePage = 0L;
        this.mCloseGuard.close();
        PdfRenderer.-set0(PdfRenderer.this, null);
        return;
      }
    }
    
    private void throwIfClosed()
    {
      if (this.mNativePage == 0L) {
        throw new IllegalStateException("Already closed");
      }
    }
    
    public void close()
    {
      throwIfClosed();
      doClose();
    }
    
    protected void finalize()
      throws Throwable
    {
      try
      {
        this.mCloseGuard.warnIfOpen();
        if (this.mNativePage != 0L) {
          doClose();
        }
        return;
      }
      finally
      {
        super.finalize();
      }
    }
    
    public int getHeight()
    {
      return this.mHeight;
    }
    
    public int getIndex()
    {
      return this.mIndex;
    }
    
    public int getWidth()
    {
      return this.mWidth;
    }
    
    public void render(Bitmap paramBitmap, Rect arg2, Matrix paramMatrix, int paramInt)
    {
      if (paramBitmap.getConfig() != Bitmap.Config.ARGB_8888) {
        throw new IllegalArgumentException("Unsupported pixel format");
      }
      if (??? != null)
      {
        if ((???.left < 0) || (???.top < 0)) {}
        while ((???.right > paramBitmap.getWidth()) || (???.bottom > paramBitmap.getHeight())) {
          throw new IllegalArgumentException("destBounds not in destination");
        }
      }
      if ((paramMatrix == null) || (paramMatrix.isAffine()))
      {
        if ((paramInt != 2) && (paramInt != 1)) {
          throw new IllegalArgumentException("Unsupported render mode");
        }
      }
      else {
        throw new IllegalArgumentException("transform not affine");
      }
      if ((paramInt == 2) && (paramInt == 1)) {
        throw new IllegalArgumentException("Only single render mode supported");
      }
      int i;
      if (??? != null) {
        i = ???.left;
      }
      for (;;)
      {
        int j;
        label155:
        int k;
        label165:
        int m;
        label175:
        long l;
        if (??? != null)
        {
          j = ???.top;
          if (??? == null) {
            break label233;
          }
          k = ???.right;
          if (??? == null) {
            break label242;
          }
          m = ???.bottom;
          if (paramMatrix == null) {
            break label251;
          }
          l = paramMatrix.native_instance;
        }
        synchronized (PdfRenderer.sPdfiumLock)
        {
          PdfRenderer.-wrap2(PdfRenderer.-get0(PdfRenderer.this), this.mNativePage, paramBitmap, i, j, k, m, l, paramInt);
          return;
          i = 0;
          continue;
          j = 0;
          break label155;
          label233:
          k = paramBitmap.getWidth();
          break label165;
          label242:
          m = paramBitmap.getHeight();
          break label175;
          label251:
          l = 0L;
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/pdf/PdfRenderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */