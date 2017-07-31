package android.print;

import android.content.Context;
import android.content.Loader;
import android.os.Handler;
import android.os.Message;
import android.printservice.recommendation.RecommendationInfo;
import com.android.internal.util.Preconditions;
import java.util.List;

public class PrintServiceRecommendationsLoader
  extends Loader<List<RecommendationInfo>>
{
  private final Handler mHandler = new MyHandler();
  private PrintManager.PrintServiceRecommendationsChangeListener mListener;
  private final PrintManager mPrintManager;
  
  public PrintServiceRecommendationsLoader(PrintManager paramPrintManager, Context paramContext)
  {
    super((Context)Preconditions.checkNotNull(paramContext));
    this.mPrintManager = ((PrintManager)Preconditions.checkNotNull(paramPrintManager));
  }
  
  private void queueNewResult()
  {
    Message localMessage = this.mHandler.obtainMessage(0);
    localMessage.obj = this.mPrintManager.getPrintServiceRecommendations();
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
    this.mListener = new PrintManager.PrintServiceRecommendationsChangeListener()
    {
      public void onPrintServiceRecommendationsChanged()
      {
        PrintServiceRecommendationsLoader.-wrap0(PrintServiceRecommendationsLoader.this);
      }
    };
    this.mPrintManager.addPrintServiceRecommendationsChangeListener(this.mListener);
    deliverResult(this.mPrintManager.getPrintServiceRecommendations());
  }
  
  protected void onStopLoading()
  {
    if (this.mListener != null)
    {
      this.mPrintManager.removePrintServiceRecommendationsChangeListener(this.mListener);
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
      if (PrintServiceRecommendationsLoader.this.isStarted()) {
        PrintServiceRecommendationsLoader.this.deliverResult((List)paramMessage.obj);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/print/PrintServiceRecommendationsLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */