package android.preference;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

public class SeekBarDialogPreference
  extends DialogPreference
{
  private final Drawable mMyIcon;
  
  public SeekBarDialogPreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public SeekBarDialogPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 18219040);
  }
  
  public SeekBarDialogPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public SeekBarDialogPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    createActionButtons();
    this.mMyIcon = getDialogIcon();
    setDialogIcon(null);
  }
  
  protected static SeekBar getSeekBar(View paramView)
  {
    return (SeekBar)paramView.findViewById(16909273);
  }
  
  public void createActionButtons()
  {
    setPositiveButtonText(17039370);
    setNegativeButtonText(17039360);
  }
  
  protected void onBindDialogView(View paramView)
  {
    super.onBindDialogView(paramView);
    paramView = (ImageView)paramView.findViewById(16908294);
    if (this.mMyIcon != null)
    {
      paramView.setImageDrawable(this.mMyIcon);
      return;
    }
    paramView.setVisibility(8);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/preference/SeekBarDialogPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */