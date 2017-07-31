package android.inputmethodservice;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class ExtractEditLayout
  extends LinearLayout
{
  Button mExtractActionButton;
  
  public ExtractEditLayout(Context paramContext)
  {
    super(paramContext);
  }
  
  public ExtractEditLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public void onFinishInflate()
  {
    super.onFinishInflate();
    this.mExtractActionButton = ((Button)findViewById(16909194));
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/inputmethodservice/ExtractEditLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */