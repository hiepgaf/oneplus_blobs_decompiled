package android.graphics.pdf;

import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.ParcelFileDescriptor;
import dalvik.system.CloseGuard;
import libcore.io.IoUtils;

public final class PdfEditor
{
  private final CloseGuard mCloseGuard;
  private ParcelFileDescriptor mInput;
  private final long mNativeDocument;
  private int mPageCount;
  
  /* Error */
  public PdfEditor(ParcelFileDescriptor arg1)
    throws java.io.IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 21	java/lang/Object:<init>	()V
    //   4: aload_0
    //   5: invokestatic 27	dalvik/system/CloseGuard:get	()Ldalvik/system/CloseGuard;
    //   8: putfield 29	android/graphics/pdf/PdfEditor:mCloseGuard	Ldalvik/system/CloseGuard;
    //   11: aload_1
    //   12: ifnonnull +13 -> 25
    //   15: new 31	java/lang/NullPointerException
    //   18: dup
    //   19: ldc 33
    //   21: invokespecial 36	java/lang/NullPointerException:<init>	(Ljava/lang/String;)V
    //   24: athrow
    //   25: getstatic 42	libcore/io/Libcore:os	Llibcore/io/Os;
    //   28: aload_1
    //   29: invokevirtual 48	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
    //   32: lconst_0
    //   33: getstatic 53	android/system/OsConstants:SEEK_SET	I
    //   36: invokeinterface 59 5 0
    //   41: pop2
    //   42: getstatic 42	libcore/io/Libcore:os	Llibcore/io/Os;
    //   45: aload_1
    //   46: invokevirtual 48	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
    //   49: invokeinterface 63 2 0
    //   54: getfield 68	android/system/StructStat:st_size	J
    //   57: lstore_2
    //   58: aload_0
    //   59: aload_1
    //   60: putfield 70	android/graphics/pdf/PdfEditor:mInput	Landroid/os/ParcelFileDescriptor;
    //   63: getstatic 76	android/graphics/pdf/PdfRenderer:sPdfiumLock	Ljava/lang/Object;
    //   66: astore_1
    //   67: aload_1
    //   68: monitorenter
    //   69: aload_0
    //   70: aload_0
    //   71: getfield 70	android/graphics/pdf/PdfEditor:mInput	Landroid/os/ParcelFileDescriptor;
    //   74: invokevirtual 80	android/os/ParcelFileDescriptor:getFd	()I
    //   77: lload_2
    //   78: invokestatic 84	android/graphics/pdf/PdfEditor:nativeOpen	(IJ)J
    //   81: putfield 86	android/graphics/pdf/PdfEditor:mNativeDocument	J
    //   84: aload_0
    //   85: aload_0
    //   86: getfield 86	android/graphics/pdf/PdfEditor:mNativeDocument	J
    //   89: invokestatic 90	android/graphics/pdf/PdfEditor:nativeGetPageCount	(J)I
    //   92: putfield 92	android/graphics/pdf/PdfEditor:mPageCount	I
    //   95: aload_1
    //   96: monitorexit
    //   97: aload_0
    //   98: getfield 29	android/graphics/pdf/PdfEditor:mCloseGuard	Ldalvik/system/CloseGuard;
    //   101: ldc 94
    //   103: invokevirtual 97	dalvik/system/CloseGuard:open	(Ljava/lang/String;)V
    //   106: return
    //   107: astore_1
    //   108: new 99	java/lang/IllegalArgumentException
    //   111: dup
    //   112: ldc 101
    //   114: invokespecial 102	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   117: athrow
    //   118: astore 4
    //   120: aload_1
    //   121: monitorexit
    //   122: aload 4
    //   124: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	125	0	this	PdfEditor
    //   57	21	2	l	long
    //   118	5	4	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   25	58	107	android/system/ErrnoException
    //   69	95	118	finally
  }
  
  private void doClose()
  {
    synchronized (PdfRenderer.sPdfiumLock)
    {
      nativeClose(this.mNativeDocument);
      IoUtils.closeQuietly(this.mInput);
      this.mInput = null;
      this.mCloseGuard.close();
      return;
    }
  }
  
  private static native void nativeClose(long paramLong);
  
  private static native int nativeGetPageCount(long paramLong);
  
  private static native boolean nativeGetPageCropBox(long paramLong, int paramInt, Rect paramRect);
  
  private static native boolean nativeGetPageMediaBox(long paramLong, int paramInt, Rect paramRect);
  
  private static native void nativeGetPageSize(long paramLong, int paramInt, Point paramPoint);
  
  private static native long nativeOpen(int paramInt, long paramLong);
  
  private static native int nativeRemovePage(long paramLong, int paramInt);
  
  private static native boolean nativeScaleForPrinting(long paramLong);
  
  private static native void nativeSetPageCropBox(long paramLong, int paramInt, Rect paramRect);
  
  private static native void nativeSetPageMediaBox(long paramLong, int paramInt, Rect paramRect);
  
  private static native void nativeSetTransformAndClip(long paramLong1, int paramInt1, long paramLong2, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
  
  private static native void nativeWrite(long paramLong, int paramInt);
  
  private void throwIfClosed()
  {
    if (this.mInput == null) {
      throw new IllegalStateException("Already closed");
    }
  }
  
  private void throwIfCropBoxNull(Rect paramRect)
  {
    if (paramRect == null) {
      throw new NullPointerException("cropBox cannot be null");
    }
  }
  
  private void throwIfMediaBoxNull(Rect paramRect)
  {
    if (paramRect == null) {
      throw new NullPointerException("mediaBox cannot be null");
    }
  }
  
  private void throwIfNotNullAndNotAfine(Matrix paramMatrix)
  {
    if ((paramMatrix == null) || (paramMatrix.isAffine())) {
      return;
    }
    throw new IllegalStateException("Matrix must be afine");
  }
  
  private void throwIfOutCropBoxNull(Rect paramRect)
  {
    if (paramRect == null) {
      throw new NullPointerException("outCropBox cannot be null");
    }
  }
  
  private void throwIfOutMediaBoxNull(Rect paramRect)
  {
    if (paramRect == null) {
      throw new NullPointerException("outMediaBox cannot be null");
    }
  }
  
  private void throwIfOutSizeNull(Point paramPoint)
  {
    if (paramPoint == null) {
      throw new NullPointerException("outSize cannot be null");
    }
  }
  
  private void throwIfPageNotInDocument(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.mPageCount)) {
      throw new IllegalArgumentException("Invalid page index");
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
  
  public boolean getPageCropBox(int paramInt, Rect paramRect)
  {
    throwIfClosed();
    throwIfOutCropBoxNull(paramRect);
    throwIfPageNotInDocument(paramInt);
    synchronized (PdfRenderer.sPdfiumLock)
    {
      boolean bool = nativeGetPageCropBox(this.mNativeDocument, paramInt, paramRect);
      return bool;
    }
  }
  
  public boolean getPageMediaBox(int paramInt, Rect paramRect)
  {
    throwIfClosed();
    throwIfOutMediaBoxNull(paramRect);
    throwIfPageNotInDocument(paramInt);
    synchronized (PdfRenderer.sPdfiumLock)
    {
      boolean bool = nativeGetPageMediaBox(this.mNativeDocument, paramInt, paramRect);
      return bool;
    }
  }
  
  public void getPageSize(int paramInt, Point paramPoint)
  {
    throwIfClosed();
    throwIfOutSizeNull(paramPoint);
    throwIfPageNotInDocument(paramInt);
    synchronized (PdfRenderer.sPdfiumLock)
    {
      nativeGetPageSize(this.mNativeDocument, paramInt, paramPoint);
      return;
    }
  }
  
  public void removePage(int paramInt)
  {
    throwIfClosed();
    throwIfPageNotInDocument(paramInt);
    synchronized (PdfRenderer.sPdfiumLock)
    {
      this.mPageCount = nativeRemovePage(this.mNativeDocument, paramInt);
      return;
    }
  }
  
  public void setPageCropBox(int paramInt, Rect paramRect)
  {
    throwIfClosed();
    throwIfCropBoxNull(paramRect);
    throwIfPageNotInDocument(paramInt);
    synchronized (PdfRenderer.sPdfiumLock)
    {
      nativeSetPageCropBox(this.mNativeDocument, paramInt, paramRect);
      return;
    }
  }
  
  public void setPageMediaBox(int paramInt, Rect paramRect)
  {
    throwIfClosed();
    throwIfMediaBoxNull(paramRect);
    throwIfPageNotInDocument(paramInt);
    synchronized (PdfRenderer.sPdfiumLock)
    {
      nativeSetPageMediaBox(this.mNativeDocument, paramInt, paramRect);
      return;
    }
  }
  
  public void setTransformAndClip(int paramInt, Matrix arg2, Rect paramRect)
  {
    throwIfClosed();
    throwIfPageNotInDocument(paramInt);
    throwIfNotNullAndNotAfine(???);
    Matrix localMatrix = ???;
    if (??? == null) {
      localMatrix = Matrix.IDENTITY_MATRIX;
    }
    if (paramRect == null)
    {
      paramRect = new Point();
      getPageSize(paramInt, paramRect);
    }
    for (;;)
    {
      synchronized (PdfRenderer.sPdfiumLock)
      {
        nativeSetTransformAndClip(this.mNativeDocument, paramInt, localMatrix.native_instance, 0, 0, paramRect.x, paramRect.y);
        return;
      }
      synchronized (PdfRenderer.sPdfiumLock)
      {
        nativeSetTransformAndClip(this.mNativeDocument, paramInt, localMatrix.native_instance, paramRect.left, paramRect.top, paramRect.right, paramRect.bottom);
      }
    }
  }
  
  public boolean shouldScaleForPrinting()
  {
    throwIfClosed();
    synchronized (PdfRenderer.sPdfiumLock)
    {
      boolean bool = nativeScaleForPrinting(this.mNativeDocument);
      return bool;
    }
  }
  
  /* Error */
  public void write(ParcelFileDescriptor paramParcelFileDescriptor)
    throws java.io.IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 172	android/graphics/pdf/PdfEditor:throwIfClosed	()V
    //   4: getstatic 76	android/graphics/pdf/PdfRenderer:sPdfiumLock	Ljava/lang/Object;
    //   7: astore_2
    //   8: aload_2
    //   9: monitorenter
    //   10: aload_0
    //   11: getfield 86	android/graphics/pdf/PdfEditor:mNativeDocument	J
    //   14: aload_1
    //   15: invokevirtual 80	android/os/ParcelFileDescriptor:getFd	()I
    //   18: invokestatic 260	android/graphics/pdf/PdfEditor:nativeWrite	(JI)V
    //   21: aload_2
    //   22: monitorexit
    //   23: aload_1
    //   24: invokestatic 115	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   27: return
    //   28: astore_3
    //   29: aload_2
    //   30: monitorexit
    //   31: aload_3
    //   32: athrow
    //   33: astore_2
    //   34: aload_1
    //   35: invokestatic 115	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   38: aload_2
    //   39: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	40	0	this	PdfEditor
    //   0	40	1	paramParcelFileDescriptor	ParcelFileDescriptor
    //   33	6	2	localObject2	Object
    //   28	4	3	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   10	21	28	finally
    //   0	10	33	finally
    //   21	23	33	finally
    //   29	33	33	finally
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/pdf/PdfEditor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */