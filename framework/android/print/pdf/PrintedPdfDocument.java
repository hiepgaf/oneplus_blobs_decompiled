package android.print.pdf;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfDocument.Page;
import android.graphics.pdf.PdfDocument.PageInfo.Builder;
import android.print.PrintAttributes;
import android.print.PrintAttributes.Margins;
import android.print.PrintAttributes.MediaSize;

public class PrintedPdfDocument
  extends PdfDocument
{
  private static final int MILS_PER_INCH = 1000;
  private static final int POINTS_IN_INCH = 72;
  private final Rect mContentRect;
  private final int mPageHeight;
  private final int mPageWidth;
  
  public PrintedPdfDocument(Context paramContext, PrintAttributes paramPrintAttributes)
  {
    paramContext = paramPrintAttributes.getMediaSize();
    this.mPageWidth = ((int)(paramContext.getWidthMils() / 1000.0F * 72.0F));
    this.mPageHeight = ((int)(paramContext.getHeightMils() / 1000.0F * 72.0F));
    paramContext = paramPrintAttributes.getMinMargins();
    int i = (int)(paramContext.getLeftMils() / 1000.0F * 72.0F);
    int j = (int)(paramContext.getTopMils() / 1000.0F * 72.0F);
    int k = (int)(paramContext.getRightMils() / 1000.0F * 72.0F);
    int m = (int)(paramContext.getBottomMils() / 1000.0F * 72.0F);
    this.mContentRect = new Rect(i, j, this.mPageWidth - k, this.mPageHeight - m);
  }
  
  public Rect getPageContentRect()
  {
    return this.mContentRect;
  }
  
  public int getPageHeight()
  {
    return this.mPageHeight;
  }
  
  public int getPageWidth()
  {
    return this.mPageWidth;
  }
  
  public PdfDocument.Page startPage(int paramInt)
  {
    return startPage(new PdfDocument.PageInfo.Builder(this.mPageWidth, this.mPageHeight, paramInt).setContentRect(this.mContentRect).create());
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/print/pdf/PrintedPdfDocument.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */