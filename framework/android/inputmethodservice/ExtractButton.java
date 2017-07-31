package android.inputmethodservice;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

class ExtractButton
  extends Button
{
  public ExtractButton(Context paramContext)
  {
    super(paramContext, null);
  }
  
  public ExtractButton(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet, 16842824);
  }
  
  public ExtractButton(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public ExtractButton(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  public boolean hasWindowFocus()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (isEnabled())
    {
      bool1 = bool2;
      if (getVisibility() == 0) {
        bool1 = true;
      }
    }
    return bool1;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/inputmethodservice/ExtractButton.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */