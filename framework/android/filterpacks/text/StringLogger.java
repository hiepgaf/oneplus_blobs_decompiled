package android.filterpacks.text;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.format.ObjectFormat;
import android.util.Log;

public class StringLogger
  extends Filter
{
  public StringLogger(String paramString)
  {
    super(paramString);
  }
  
  public void process(FilterContext paramFilterContext)
  {
    Log.i("StringLogger", pullInput("string").getObjectValue().toString());
  }
  
  public void setupPorts()
  {
    addMaskedInputPort("string", ObjectFormat.fromClass(Object.class, 1));
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/text/StringLogger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */