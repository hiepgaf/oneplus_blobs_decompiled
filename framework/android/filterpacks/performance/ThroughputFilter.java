package android.filterpacks.performance;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.FrameManager;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.format.ObjectFormat;
import android.os.SystemClock;

public class ThroughputFilter
  extends Filter
{
  private long mLastTime = 0L;
  private FrameFormat mOutputFormat;
  @GenerateFieldPort(hasDefault=true, name="period")
  private int mPeriod = 5;
  private int mPeriodFrameCount = 0;
  private int mTotalFrameCount = 0;
  
  public ThroughputFilter(String paramString)
  {
    super(paramString);
  }
  
  public FrameFormat getOutputFormat(String paramString, FrameFormat paramFrameFormat)
  {
    return paramFrameFormat;
  }
  
  public void open(FilterContext paramFilterContext)
  {
    this.mTotalFrameCount = 0;
    this.mPeriodFrameCount = 0;
    this.mLastTime = 0L;
  }
  
  public void process(FilterContext paramFilterContext)
  {
    Object localObject = pullInput("frame");
    pushOutput("frame", (Frame)localObject);
    this.mTotalFrameCount += 1;
    this.mPeriodFrameCount += 1;
    if (this.mLastTime == 0L) {
      this.mLastTime = SystemClock.elapsedRealtime();
    }
    long l = SystemClock.elapsedRealtime();
    if (l - this.mLastTime >= this.mPeriod * 1000)
    {
      localObject = ((Frame)localObject).getFormat();
      int i = ((FrameFormat)localObject).getWidth();
      int j = ((FrameFormat)localObject).getHeight();
      localObject = new Throughput(this.mTotalFrameCount, this.mPeriodFrameCount, this.mPeriod, i * j);
      paramFilterContext = paramFilterContext.getFrameManager().newFrame(this.mOutputFormat);
      paramFilterContext.setObjectValue(localObject);
      pushOutput("throughput", paramFilterContext);
      this.mLastTime = l;
      this.mPeriodFrameCount = 0;
    }
  }
  
  public void setupPorts()
  {
    addInputPort("frame");
    this.mOutputFormat = ObjectFormat.fromClass(Throughput.class, 1);
    addOutputBasedOnInput("frame", "frame");
    addOutputPort("throughput", this.mOutputFormat);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/performance/ThroughputFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */