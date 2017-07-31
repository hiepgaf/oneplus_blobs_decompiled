package android.filterpacks.base;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.FilterContext.OnFrameReceivedListener;
import android.filterfw.core.Frame;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.GenerateFinalPort;
import android.os.Handler;
import android.os.Looper;

public class CallbackFilter
  extends Filter
{
  @GenerateFinalPort(hasDefault=true, name="callUiThread")
  private boolean mCallbacksOnUiThread = true;
  @GenerateFieldPort(hasDefault=true, name="listener")
  private FilterContext.OnFrameReceivedListener mListener;
  private Handler mUiThreadHandler;
  @GenerateFieldPort(hasDefault=true, name="userData")
  private Object mUserData;
  
  public CallbackFilter(String paramString)
  {
    super(paramString);
  }
  
  public void prepare(FilterContext paramFilterContext)
  {
    if (this.mCallbacksOnUiThread) {
      this.mUiThreadHandler = new Handler(Looper.getMainLooper());
    }
  }
  
  public void process(FilterContext paramFilterContext)
  {
    paramFilterContext = pullInput("frame");
    if (this.mListener != null)
    {
      if (this.mCallbacksOnUiThread)
      {
        paramFilterContext.retain();
        paramFilterContext = new CallbackRunnable(this.mListener, this, paramFilterContext, this.mUserData);
        if (!this.mUiThreadHandler.post(paramFilterContext)) {
          throw new RuntimeException("Unable to send callback to UI thread!");
        }
      }
      else
      {
        this.mListener.onFrameReceived(this, paramFilterContext, this.mUserData);
      }
      return;
    }
    throw new RuntimeException("CallbackFilter received frame, but no listener set!");
  }
  
  public void setupPorts()
  {
    addInputPort("frame");
  }
  
  private class CallbackRunnable
    implements Runnable
  {
    private Filter mFilter;
    private Frame mFrame;
    private FilterContext.OnFrameReceivedListener mListener;
    private Object mUserData;
    
    public CallbackRunnable(FilterContext.OnFrameReceivedListener paramOnFrameReceivedListener, Filter paramFilter, Frame paramFrame, Object paramObject)
    {
      this.mListener = paramOnFrameReceivedListener;
      this.mFilter = paramFilter;
      this.mFrame = paramFrame;
      this.mUserData = paramObject;
    }
    
    public void run()
    {
      this.mListener.onFrameReceived(this.mFilter, this.mFrame, this.mUserData);
      this.mFrame.release();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/base/CallbackFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */