package android.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import com.android.internal.R.styleable;

public class SwitchPreference
  extends TwoStatePreference
{
  private final Listener mListener = new Listener(null);
  private CharSequence mSwitchOff;
  private CharSequence mSwitchOn;
  
  public SwitchPreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public SwitchPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16843629);
  }
  
  public SwitchPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public SwitchPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.SwitchPreference, paramInt1, paramInt2);
    setSummaryOn(paramContext.getString(0));
    setSummaryOff(paramContext.getString(1));
    setSwitchTextOn(paramContext.getString(3));
    setSwitchTextOff(paramContext.getString(4));
    setDisableDependentsState(paramContext.getBoolean(2, false));
    paramContext.recycle();
  }
  
  public CharSequence getSwitchTextOff()
  {
    return this.mSwitchOff;
  }
  
  public CharSequence getSwitchTextOn()
  {
    return this.mSwitchOn;
  }
  
  protected void onBindView(View paramView)
  {
    super.onBindView(paramView);
    Object localObject = paramView.findViewById(16908352);
    if ((localObject != null) && ((localObject instanceof Checkable)))
    {
      if ((localObject instanceof Switch)) {
        ((Switch)localObject).setOnCheckedChangeListener(null);
      }
      ((Checkable)localObject).setChecked(this.mChecked);
      if ((localObject instanceof Switch))
      {
        localObject = (Switch)localObject;
        ((Switch)localObject).setTextOn(this.mSwitchOn);
        ((Switch)localObject).setTextOff(this.mSwitchOff);
        ((Switch)localObject).setOnCheckedChangeListener(this.mListener);
      }
    }
    syncSummaryView(paramView);
  }
  
  public void setSwitchTextOff(int paramInt)
  {
    setSwitchTextOff(getContext().getString(paramInt));
  }
  
  public void setSwitchTextOff(CharSequence paramCharSequence)
  {
    this.mSwitchOff = paramCharSequence;
    notifyChanged();
  }
  
  public void setSwitchTextOn(int paramInt)
  {
    setSwitchTextOn(getContext().getString(paramInt));
  }
  
  public void setSwitchTextOn(CharSequence paramCharSequence)
  {
    this.mSwitchOn = paramCharSequence;
    notifyChanged();
  }
  
  private class Listener
    implements CompoundButton.OnCheckedChangeListener
  {
    private Listener() {}
    
    public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean)
    {
      if (!SwitchPreference.this.callChangeListener(Boolean.valueOf(paramBoolean)))
      {
        if (paramBoolean) {}
        for (paramBoolean = false;; paramBoolean = true)
        {
          paramCompoundButton.setChecked(paramBoolean);
          return;
        }
      }
      SwitchPreference.this.setChecked(paramBoolean);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/preference/SwitchPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */