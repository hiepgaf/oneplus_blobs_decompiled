package android.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.view.AbsSavedState;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsSeekBar;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import com.android.internal.R.styleable;

public class SeekBarPreference
  extends Preference
  implements SeekBar.OnSeekBarChangeListener
{
  private int mMax;
  private int mProgress;
  private boolean mTrackingTouch;
  
  public SeekBarPreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public SeekBarPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 18219046);
  }
  
  public SeekBarPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public SeekBarPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ProgressBar, paramInt1, paramInt2);
    setMax(localTypedArray.getInt(2, this.mMax));
    localTypedArray.recycle();
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.SeekBarPreference, paramInt1, paramInt2);
    paramInt1 = paramContext.getResourceId(0, 17367228);
    paramContext.recycle();
    setLayoutResource(paramInt1);
  }
  
  private void setProgress(int paramInt, boolean paramBoolean)
  {
    int i = paramInt;
    if (paramInt > this.mMax) {
      i = this.mMax;
    }
    paramInt = i;
    if (i < 0) {
      paramInt = 0;
    }
    if (paramInt != this.mProgress)
    {
      this.mProgress = paramInt;
      persistInt(paramInt);
      if (paramBoolean) {
        notifyChanged();
      }
    }
  }
  
  public int getProgress()
  {
    return this.mProgress;
  }
  
  public CharSequence getSummary()
  {
    return null;
  }
  
  protected void onBindView(View paramView)
  {
    super.onBindView(paramView);
    paramView = (SeekBar)paramView.findViewById(16909273);
    paramView.setOnSeekBarChangeListener(this);
    paramView.setMax(this.mMax);
    paramView.setProgress(this.mProgress);
    paramView.setEnabled(isEnabled());
  }
  
  protected Object onGetDefaultValue(TypedArray paramTypedArray, int paramInt)
  {
    return Integer.valueOf(paramTypedArray.getInt(paramInt, 0));
  }
  
  public boolean onKey(View paramView, int paramInt, KeyEvent paramKeyEvent)
  {
    if (paramKeyEvent.getAction() != 0) {
      return false;
    }
    paramView = (SeekBar)paramView.findViewById(16909273);
    if (paramView == null) {
      return false;
    }
    return paramView.onKeyDown(paramInt, paramKeyEvent);
  }
  
  public void onProgressChanged(SeekBar paramSeekBar, int paramInt, boolean paramBoolean)
  {
    if ((!paramBoolean) || (this.mTrackingTouch)) {
      return;
    }
    syncProgress(paramSeekBar);
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if (!paramParcelable.getClass().equals(SavedState.class))
    {
      super.onRestoreInstanceState(paramParcelable);
      return;
    }
    paramParcelable = (SavedState)paramParcelable;
    super.onRestoreInstanceState(paramParcelable.getSuperState());
    this.mProgress = paramParcelable.progress;
    this.mMax = paramParcelable.max;
    notifyChanged();
  }
  
  protected Parcelable onSaveInstanceState()
  {
    Object localObject = super.onSaveInstanceState();
    if (isPersistent()) {
      return (Parcelable)localObject;
    }
    localObject = new SavedState((Parcelable)localObject);
    ((SavedState)localObject).progress = this.mProgress;
    ((SavedState)localObject).max = this.mMax;
    return (Parcelable)localObject;
  }
  
  protected void onSetInitialValue(boolean paramBoolean, Object paramObject)
  {
    if (paramBoolean) {}
    for (int i = getPersistedInt(this.mProgress);; i = ((Integer)paramObject).intValue())
    {
      setProgress(i);
      return;
    }
  }
  
  public void onStartTrackingTouch(SeekBar paramSeekBar)
  {
    this.mTrackingTouch = true;
  }
  
  public void onStopTrackingTouch(SeekBar paramSeekBar)
  {
    this.mTrackingTouch = false;
    if (paramSeekBar.getProgress() != this.mProgress) {
      syncProgress(paramSeekBar);
    }
  }
  
  public void setMax(int paramInt)
  {
    if (paramInt != this.mMax)
    {
      this.mMax = paramInt;
      notifyChanged();
    }
  }
  
  public void setProgress(int paramInt)
  {
    setProgress(paramInt, true);
  }
  
  void syncProgress(SeekBar paramSeekBar)
  {
    int i = paramSeekBar.getProgress();
    if (i != this.mProgress)
    {
      if (callChangeListener(Integer.valueOf(i))) {
        setProgress(i, false);
      }
    }
    else {
      return;
    }
    paramSeekBar.setProgress(this.mProgress);
  }
  
  private static class SavedState
    extends Preference.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public SeekBarPreference.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new SeekBarPreference.SavedState(paramAnonymousParcel);
      }
      
      public SeekBarPreference.SavedState[] newArray(int paramAnonymousInt)
      {
        return new SeekBarPreference.SavedState[paramAnonymousInt];
      }
    };
    int max;
    int progress;
    
    public SavedState(Parcel paramParcel)
    {
      super();
      this.progress = paramParcel.readInt();
      this.max = paramParcel.readInt();
    }
    
    public SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(this.progress);
      paramParcel.writeInt(this.max);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/preference/SeekBarPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */