package android.print;

import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;

public abstract class PrintDocumentAdapter
{
  public static final String EXTRA_PRINT_PREVIEW = "EXTRA_PRINT_PREVIEW";
  
  public void onFinish() {}
  
  public abstract void onLayout(PrintAttributes paramPrintAttributes1, PrintAttributes paramPrintAttributes2, CancellationSignal paramCancellationSignal, LayoutResultCallback paramLayoutResultCallback, Bundle paramBundle);
  
  public void onStart() {}
  
  public abstract void onWrite(PageRange[] paramArrayOfPageRange, ParcelFileDescriptor paramParcelFileDescriptor, CancellationSignal paramCancellationSignal, WriteResultCallback paramWriteResultCallback);
  
  public static abstract class LayoutResultCallback
  {
    public void onLayoutCancelled() {}
    
    public void onLayoutFailed(CharSequence paramCharSequence) {}
    
    public void onLayoutFinished(PrintDocumentInfo paramPrintDocumentInfo, boolean paramBoolean) {}
  }
  
  public static abstract class WriteResultCallback
  {
    public void onWriteCancelled() {}
    
    public void onWriteFailed(CharSequence paramCharSequence) {}
    
    public void onWriteFinished(PageRange[] paramArrayOfPageRange) {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/print/PrintDocumentAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */