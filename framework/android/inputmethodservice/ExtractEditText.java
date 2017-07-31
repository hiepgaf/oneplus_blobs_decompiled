package android.inputmethodservice;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class ExtractEditText
  extends EditText
{
  private InputMethodService mIME;
  private int mSettingExtractedText;
  
  public ExtractEditText(Context paramContext)
  {
    super(paramContext, null);
  }
  
  public ExtractEditText(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet, 16842862);
  }
  
  public ExtractEditText(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public ExtractEditText(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  protected void deleteText_internal(int paramInt1, int paramInt2)
  {
    this.mIME.onExtractedDeleteText(paramInt1, paramInt2);
  }
  
  public void finishInternalChanges()
  {
    this.mSettingExtractedText -= 1;
  }
  
  public boolean hasFocus()
  {
    return isEnabled();
  }
  
  public boolean hasVerticalScrollBar()
  {
    return computeVerticalScrollRange() > computeVerticalScrollExtent();
  }
  
  public boolean hasWindowFocus()
  {
    return isEnabled();
  }
  
  public boolean isFocused()
  {
    return isEnabled();
  }
  
  public boolean isInExtractedMode()
  {
    return true;
  }
  
  public boolean isInputMethodTarget()
  {
    return true;
  }
  
  protected void onSelectionChanged(int paramInt1, int paramInt2)
  {
    if ((this.mSettingExtractedText == 0) && (this.mIME != null) && (paramInt1 >= 0) && (paramInt2 >= 0)) {
      this.mIME.onExtractedSelectionChanged(paramInt1, paramInt2);
    }
  }
  
  public boolean onTextContextMenuItem(int paramInt)
  {
    if ((paramInt == 16908319) || (paramInt == 16908340)) {
      return super.onTextContextMenuItem(paramInt);
    }
    if ((this.mIME != null) && (this.mIME.onExtractTextContextMenuItem(paramInt)))
    {
      if ((paramInt == 16908321) || (paramInt == 16908322)) {
        stopTextActionMode();
      }
      return true;
    }
    return super.onTextContextMenuItem(paramInt);
  }
  
  public boolean performClick()
  {
    if ((!super.performClick()) && (this.mIME != null))
    {
      this.mIME.onExtractedTextClicked();
      return true;
    }
    return false;
  }
  
  protected void replaceText_internal(int paramInt1, int paramInt2, CharSequence paramCharSequence)
  {
    this.mIME.onExtractedReplaceText(paramInt1, paramInt2, paramCharSequence);
  }
  
  protected void setCursorPosition_internal(int paramInt1, int paramInt2)
  {
    this.mIME.onExtractedSelectionChanged(paramInt1, paramInt2);
  }
  
  public void setExtractedText(ExtractedText paramExtractedText)
  {
    try
    {
      this.mSettingExtractedText += 1;
      super.setExtractedText(paramExtractedText);
      return;
    }
    finally
    {
      this.mSettingExtractedText -= 1;
    }
  }
  
  void setIME(InputMethodService paramInputMethodService)
  {
    this.mIME = paramInputMethodService;
  }
  
  protected void setSpan_internal(Object paramObject, int paramInt1, int paramInt2, int paramInt3)
  {
    this.mIME.onExtractedSetSpan(paramObject, paramInt1, paramInt2, paramInt3);
  }
  
  public void startInternalChanges()
  {
    this.mSettingExtractedText += 1;
  }
  
  protected void viewClicked(InputMethodManager paramInputMethodManager)
  {
    if (this.mIME != null) {
      this.mIME.onViewClicked(false);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/inputmethodservice/ExtractEditText.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */