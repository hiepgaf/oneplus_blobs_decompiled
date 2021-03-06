package com.android.settings;

import android.content.Context;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class AppProgressPreference
  extends TintablePreference
{
  private int mProgress;
  
  public AppProgressPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setLayoutResource(2130968870);
    setWidgetLayoutResource(2130969089);
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    ((ProgressBar)paramPreferenceViewHolder.findViewById(16908301)).setProgress(this.mProgress);
  }
  
  public void setProgress(int paramInt)
  {
    this.mProgress = paramInt;
    notifyChanged();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/settings/AppProgressPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */