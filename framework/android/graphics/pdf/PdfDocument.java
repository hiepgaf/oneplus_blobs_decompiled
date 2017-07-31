package android.graphics.pdf;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import dalvik.system.CloseGuard;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PdfDocument
{
  private final byte[] mChunk = new byte['က'];
  private final CloseGuard mCloseGuard = CloseGuard.get();
  private Page mCurrentPage;
  private long mNativeDocument = nativeCreateDocument();
  private final List<PageInfo> mPages = new ArrayList();
  
  public PdfDocument()
  {
    this.mCloseGuard.open("close");
  }
  
  private void dispose()
  {
    if (this.mNativeDocument != 0L)
    {
      nativeClose(this.mNativeDocument);
      this.mCloseGuard.close();
      this.mNativeDocument = 0L;
    }
  }
  
  private native void nativeClose(long paramLong);
  
  private native long nativeCreateDocument();
  
  private native void nativeFinishPage(long paramLong);
  
  private static native long nativeStartPage(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
  
  private native void nativeWriteTo(long paramLong, OutputStream paramOutputStream, byte[] paramArrayOfByte);
  
  private void throwIfClosed()
  {
    if (this.mNativeDocument == 0L) {
      throw new IllegalStateException("document is closed!");
    }
  }
  
  private void throwIfCurrentPageNotFinished()
  {
    if (this.mCurrentPage != null) {
      throw new IllegalStateException("Current page not finished!");
    }
  }
  
  public void close()
  {
    throwIfCurrentPageNotFinished();
    dispose();
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      this.mCloseGuard.warnIfOpen();
      dispose();
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public void finishPage(Page paramPage)
  {
    throwIfClosed();
    if (paramPage == null) {
      throw new IllegalArgumentException("page cannot be null");
    }
    if (paramPage != this.mCurrentPage) {
      throw new IllegalStateException("invalid page");
    }
    if (paramPage.isFinished()) {
      throw new IllegalStateException("page already finished");
    }
    this.mPages.add(paramPage.getInfo());
    this.mCurrentPage = null;
    nativeFinishPage(this.mNativeDocument);
    Page.-wrap0(paramPage);
  }
  
  public List<PageInfo> getPages()
  {
    return Collections.unmodifiableList(this.mPages);
  }
  
  public Page startPage(PageInfo paramPageInfo)
  {
    throwIfClosed();
    throwIfCurrentPageNotFinished();
    if (paramPageInfo == null) {
      throw new IllegalArgumentException("page cannot be null");
    }
    this.mCurrentPage = new Page(new PdfCanvas(nativeStartPage(this.mNativeDocument, PageInfo.-get2(paramPageInfo), PageInfo.-get1(paramPageInfo), PageInfo.-get0(paramPageInfo).left, PageInfo.-get0(paramPageInfo).top, PageInfo.-get0(paramPageInfo).right, PageInfo.-get0(paramPageInfo).bottom)), paramPageInfo, null);
    return this.mCurrentPage;
  }
  
  public void writeTo(OutputStream paramOutputStream)
    throws IOException
  {
    throwIfClosed();
    throwIfCurrentPageNotFinished();
    if (paramOutputStream == null) {
      throw new IllegalArgumentException("out cannot be null!");
    }
    nativeWriteTo(this.mNativeDocument, paramOutputStream, this.mChunk);
  }
  
  public static final class Page
  {
    private Canvas mCanvas;
    private final PdfDocument.PageInfo mPageInfo;
    
    private Page(Canvas paramCanvas, PdfDocument.PageInfo paramPageInfo)
    {
      this.mCanvas = paramCanvas;
      this.mPageInfo = paramPageInfo;
    }
    
    private void finish()
    {
      if (this.mCanvas != null)
      {
        this.mCanvas.release();
        this.mCanvas = null;
      }
    }
    
    public Canvas getCanvas()
    {
      return this.mCanvas;
    }
    
    public PdfDocument.PageInfo getInfo()
    {
      return this.mPageInfo;
    }
    
    boolean isFinished()
    {
      return this.mCanvas == null;
    }
  }
  
  public static final class PageInfo
  {
    private Rect mContentRect;
    private int mPageHeight;
    private int mPageNumber;
    private int mPageWidth;
    
    public Rect getContentRect()
    {
      return this.mContentRect;
    }
    
    public int getPageHeight()
    {
      return this.mPageHeight;
    }
    
    public int getPageNumber()
    {
      return this.mPageNumber;
    }
    
    public int getPageWidth()
    {
      return this.mPageWidth;
    }
    
    public static final class Builder
    {
      private final PdfDocument.PageInfo mPageInfo = new PdfDocument.PageInfo(null);
      
      public Builder(int paramInt1, int paramInt2, int paramInt3)
      {
        if (paramInt1 <= 0) {
          throw new IllegalArgumentException("page width must be positive");
        }
        if (paramInt2 <= 0) {
          throw new IllegalArgumentException("page width must be positive");
        }
        if (paramInt3 < 0) {
          throw new IllegalArgumentException("pageNumber must be non negative");
        }
        PdfDocument.PageInfo.-set3(this.mPageInfo, paramInt1);
        PdfDocument.PageInfo.-set1(this.mPageInfo, paramInt2);
        PdfDocument.PageInfo.-set2(this.mPageInfo, paramInt3);
      }
      
      public PdfDocument.PageInfo create()
      {
        if (PdfDocument.PageInfo.-get0(this.mPageInfo) == null) {
          PdfDocument.PageInfo.-set0(this.mPageInfo, new Rect(0, 0, PdfDocument.PageInfo.-get2(this.mPageInfo), PdfDocument.PageInfo.-get1(this.mPageInfo)));
        }
        return this.mPageInfo;
      }
      
      public Builder setContentRect(Rect paramRect)
      {
        if (paramRect != null)
        {
          if ((paramRect.left < 0) || (paramRect.top < 0)) {}
          while ((paramRect.right > PdfDocument.PageInfo.-get2(this.mPageInfo)) || (paramRect.bottom > PdfDocument.PageInfo.-get1(this.mPageInfo))) {
            throw new IllegalArgumentException("contentRect does not fit the page");
          }
        }
        PdfDocument.PageInfo.-set0(this.mPageInfo, paramRect);
        return this;
      }
    }
  }
  
  private final class PdfCanvas
    extends Canvas
  {
    public PdfCanvas(long paramLong)
    {
      super();
    }
    
    public void setBitmap(Bitmap paramBitmap)
    {
      throw new UnsupportedOperationException();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/pdf/PdfDocument.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */