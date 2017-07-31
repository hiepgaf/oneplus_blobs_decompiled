package android.print;

import android.content.Context;
import android.content.Loader;
import android.os.Handler;
import android.os.Message;
import android.printservice.PrintServiceInfo;
import com.android.internal.util.Preconditions;
import java.util.List;

public class PrintServicesLoader
  extends Loader<List<PrintServiceInfo>>
{
  private final Handler mHandler = new MyHandler();
  private PrintManager.PrintServicesChangeListener mListener;
  private final PrintManager mPrintManager;
  private final int mSelectionFlags;
  
  public PrintServicesLoader(PrintManager paramPrintManager, Context paramContext, int paramInt)
  {
    super((Context)Preconditions.checkNotNull(paramContext));
    this.mPrintManager = ((PrintManager)Preconditions.checkNotNull(paramPrintManager));
    this.mSelectionFlags = Preconditions.checkFlagsArgument(paramInt, 3);
  }
  
  private void queueNewResult()
  {
    Message localMessage = this.mHandler.obtainMessage(0);
    localMessage.obj = this.mPrintManager.getPrintServices(this.mSelectionFlags);
    this.mHandler.sendMessage(localMessage);
  }
  
  protected void onForceLoad()
  {
    queueNewResult();
  }
  
  protected void onReset()
  {
    onStopLoading();
  }
  
  protected void onStartLoading()
  {
    this.mListener = new PrintManager.PrintServicesChangeListener()
    {
      public void onPrintServicesChanged()
      {
        PrintServicesLoader.-wrap0(PrintServicesLoader.this);
      }
    };
    this.mPrintManager.addPrintServicesChangeListener(this.mListener);
    deliverResult(this.mPrintManager.getPrintServices(this.mSelectionFlags));
  }
  
  protected void onStopLoading()
  {
    if (this.mListener != null)
    {
      this.mPrintManager.removePrintServicesChangeListener(this.mListener);
      this.mListener = null;
    }
    this.mHandler.removeMessages(0);
  }
  
  private class MyHandler
    extends Handler
  {
    public MyHandler()
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      if (PrintServicesLoader.this.isStarted()) {
        PrintServicesLoader.this.deliverResult((List)paramMessage.obj);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/print/PrintServicesLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */