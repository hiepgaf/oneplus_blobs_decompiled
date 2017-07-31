package android.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

public class TimePickerDialog
  extends AlertDialog
  implements DialogInterface.OnClickListener, TimePicker.OnTimeChangedListener
{
  private static final String HOUR = "hour";
  private static final String IS_24_HOUR = "is24hour";
  private static final String MINUTE = "minute";
  private final int mInitialHourOfDay;
  private final int mInitialMinute;
  private final boolean mIs24HourView;
  private final TimePicker mTimePicker;
  private final OnTimeSetListener mTimeSetListener;
  
  public TimePickerDialog(Context paramContext, int paramInt1, OnTimeSetListener paramOnTimeSetListener, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    super(paramContext, resolveDialogTheme(paramContext, paramInt1));
    this.mTimeSetListener = paramOnTimeSetListener;
    this.mInitialHourOfDay = paramInt2;
    this.mInitialMinute = paramInt3;
    this.mIs24HourView = paramBoolean;
    paramContext = getContext();
    paramOnTimeSetListener = LayoutInflater.from(paramContext).inflate(17367292, null);
    setView(paramOnTimeSetListener);
    setButton(-1, paramContext.getString(17039370), this);
    setButton(-2, paramContext.getString(17039360), this);
    setButtonPanelLayoutHint(1);
    this.mTimePicker = ((TimePicker)paramOnTimeSetListener.findViewById(16909367));
    this.mTimePicker.setIs24HourView(Boolean.valueOf(this.mIs24HourView));
    this.mTimePicker.setCurrentHour(Integer.valueOf(this.mInitialHourOfDay));
    this.mTimePicker.setCurrentMinute(Integer.valueOf(this.mInitialMinute));
    this.mTimePicker.setOnTimeChangedListener(this);
  }
  
  public TimePickerDialog(Context paramContext, OnTimeSetListener paramOnTimeSetListener, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    this(paramContext, 0, paramOnTimeSetListener, paramInt1, paramInt2, paramBoolean);
  }
  
  static int resolveDialogTheme(Context paramContext, int paramInt)
  {
    if (paramInt == 0)
    {
      TypedValue localTypedValue = new TypedValue();
      paramContext.getTheme().resolveAttribute(16843934, localTypedValue, true);
      return localTypedValue.resourceId;
    }
    return paramInt;
  }
  
  public TimePicker getTimePicker()
  {
    return this.mTimePicker;
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    switch (paramInt)
    {
    default: 
    case -1: 
      do
      {
        return;
      } while (this.mTimeSetListener == null);
      this.mTimeSetListener.onTimeSet(this.mTimePicker, this.mTimePicker.getCurrentHour().intValue(), this.mTimePicker.getCurrentMinute().intValue());
      return;
    }
    cancel();
  }
  
  public void onRestoreInstanceState(Bundle paramBundle)
  {
    super.onRestoreInstanceState(paramBundle);
    int i = paramBundle.getInt("hour");
    int j = paramBundle.getInt("minute");
    this.mTimePicker.setIs24HourView(Boolean.valueOf(paramBundle.getBoolean("is24hour")));
    this.mTimePicker.setCurrentHour(Integer.valueOf(i));
    this.mTimePicker.setCurrentMinute(Integer.valueOf(j));
  }
  
  public Bundle onSaveInstanceState()
  {
    Bundle localBundle = super.onSaveInstanceState();
    localBundle.putInt("hour", this.mTimePicker.getCurrentHour().intValue());
    localBundle.putInt("minute", this.mTimePicker.getCurrentMinute().intValue());
    localBundle.putBoolean("is24hour", this.mTimePicker.is24HourView());
    return localBundle;
  }
  
  public void onTimeChanged(TimePicker paramTimePicker, int paramInt1, int paramInt2) {}
  
  public void updateTime(int paramInt1, int paramInt2)
  {
    this.mTimePicker.setCurrentHour(Integer.valueOf(paramInt1));
    this.mTimePicker.setCurrentMinute(Integer.valueOf(paramInt2));
  }
  
  public static abstract interface OnTimeSetListener
  {
    public abstract void onTimeSet(TimePicker paramTimePicker, int paramInt1, int paramInt2);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/TimePickerDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */