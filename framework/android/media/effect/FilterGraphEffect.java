package android.media.effect;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterGraph;
import android.filterfw.core.GraphRunner;
import android.filterfw.core.SyncRunner;
import android.filterfw.io.GraphIOException;
import android.filterfw.io.TextGraphReader;

public class FilterGraphEffect
  extends FilterEffect
{
  private static final String TAG = "FilterGraphEffect";
  protected FilterGraph mGraph;
  protected String mInputName;
  protected String mOutputName;
  protected GraphRunner mRunner;
  protected Class mSchedulerClass;
  
  public FilterGraphEffect(EffectContext paramEffectContext, String paramString1, String paramString2, String paramString3, String paramString4, Class paramClass)
  {
    super(paramEffectContext, paramString1);
    this.mInputName = paramString3;
    this.mOutputName = paramString4;
    this.mSchedulerClass = paramClass;
    createGraph(paramString2);
  }
  
  private void createGraph(String paramString)
  {
    TextGraphReader localTextGraphReader = new TextGraphReader();
    try
    {
      this.mGraph = localTextGraphReader.readGraphString(paramString);
      if (this.mGraph == null) {
        throw new RuntimeException("Could not setup effect");
      }
    }
    catch (GraphIOException paramString)
    {
      throw new RuntimeException("Could not setup effect", paramString);
    }
    this.mRunner = new SyncRunner(getFilterContext(), this.mGraph, this.mSchedulerClass);
  }
  
  public void apply(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    beginGLEffect();
    Filter localFilter = this.mGraph.getFilter(this.mInputName);
    if (localFilter != null)
    {
      localFilter.setInputValue("texId", Integer.valueOf(paramInt1));
      localFilter.setInputValue("width", Integer.valueOf(paramInt2));
      localFilter.setInputValue("height", Integer.valueOf(paramInt3));
      localFilter = this.mGraph.getFilter(this.mOutputName);
      if (localFilter == null) {
        break label107;
      }
      localFilter.setInputValue("texId", Integer.valueOf(paramInt4));
    }
    try
    {
      this.mRunner.run();
      endGLEffect();
      return;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw new RuntimeException("Internal error applying effect: ", localRuntimeException);
    }
    throw new RuntimeException("Internal error applying effect");
    label107:
    throw new RuntimeException("Internal error applying effect");
  }
  
  public void release()
  {
    this.mGraph.tearDown(getFilterContext());
    this.mGraph = null;
  }
  
  public void setParameter(String paramString, Object paramObject) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/effect/FilterGraphEffect.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */