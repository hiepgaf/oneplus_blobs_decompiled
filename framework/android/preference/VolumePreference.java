package android.preference;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.view.AbsSavedState;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.SeekBar;
import com.android.internal.R.styleable;

public class VolumePreference
  extends SeekBarDialogPreference
  implements PreferenceManager.OnActivityStopListener, View.OnKeyListener, SeekBarVolumizer.Callback
{
  private SeekBarVolumizer mSeekBarVolumizer;
  private int mStreamType;
  
  public VolumePreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public VolumePreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 18219040);
  }
  
  public VolumePreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public VolumePreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.VolumePreference, paramInt1, paramInt2);
    this.mStreamType = paramContext.getInt(0, 0);
    paramContext.recycle();
  }
  
  private void cleanup()
  {
    getPreferenceManager().unregisterOnActivityStopListener(this);
    if (this.mSeekBarVolumizer != null)
    {
      Object localObject = getDialog();
      if ((localObject != null) && (((Dialog)localObject).isShowing()))
      {
        localObject = ((Dialog)localObject).getWindow().getDecorView().findViewById(16909273);
        if (localObject != null) {
          ((View)localObject).setOnKeyListener(null);
        }
        this.mSeekBarVolumizer.revertVolume();
      }
      this.mSeekBarVolumizer.stop();
      this.mSeekBarVolumizer = null;
    }
  }
  
  public void onActivityStop()
  {
    if (this.mSeekBarVolumizer != null) {
      this.mSeekBarVolumizer.stopSample();
    }
  }
  
  protected void onBindDialogView(View paramView)
  {
    super.onBindDialogView(paramView);
    SeekBar localSeekBar = (SeekBar)paramView.findViewById(16909273);
    this.mSeekBarVolumizer = new SeekBarVolumizer(getContext(), this.mStreamType, null, this);
    this.mSeekBarVolumizer.start();
    this.mSeekBarVolumizer.setSeekBar(localSeekBar);
    getPreferenceManager().registerOnActivityStopListener(this);
    paramView.setOnKeyListener(this);
    paramView.setFocusableInTouchMode(true);
    paramView.requestFocus();
  }
  
  protected void onDialogClosed(boolean paramBoolean)
  {
    super.onDialogClosed(paramBoolean);
    if ((!paramBoolean) && (this.mSeekBarVolumizer != null)) {
      this.mSeekBarVolumizer.revertVolume();
    }
    cleanup();
  }
  
  public boolean onKey(View paramView, int paramInt, KeyEvent paramKeyEvent)
  {
    if (this.mSeekBarVolumizer == null) {
      return true;
    }
    if (paramKeyEvent.getAction() == 0) {}
    for (int i = 1;; i = 0) {
      switch (paramInt)
      {
      default: 
        return false;
      }
    }
    if (i != 0) {
      this.mSeekBarVolumizer.changeVolumeBy(-1);
    }
    return true;
    if (i != 0) {
      this.mSeekBarVolumizer.changeVolumeBy(1);
    }
    return true;
    if (i != 0) {
      this.mSeekBarVolumizer.muteVolume();
    }
    return true;
  }
  
  public void onMuted(boolean paramBoolean1, boolean paramBoolean2) {}
  
  public void onProgressChanged(SeekBar paramSeekBar, int paramInt, boolean paramBoolean) {}
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if ((paramParcelable != null) && (paramParcelable.getClass().equals(SavedState.class)))
    {
      paramParcelable = (SavedState)paramParcelable;
      super.onRestoreInstanceState(paramParcelable.getSuperState());
      if (this.mSeekBarVolumizer != null) {
        this.mSeekBarVolumizer.onRestoreInstanceState(paramParcelable.getVolumeStore());
      }
      return;
    }
    super.onRestoreInstanceState(paramParcelable);
  }
  
  public void onSampleStarting(SeekBarVolumizer paramSeekBarVolumizer)
  {
    if ((this.mSeekBarVolumizer != null) && (paramSeekBarVolumizer != this.mSeekBarVolumizer)) {
      this.mSeekBarVolumizer.stopSample();
    }
  }
  
  protected Parcelable onSaveInstanceState()
  {
    Object localObject = super.onSaveInstanceState();
    if (isPersistent()) {
      return (Parcelable)localObject;
    }
    localObject = new SavedState((Parcelable)localObject);
    if (this.mSeekBarVolumizer != null) {
      this.mSeekBarVolumizer.onSaveInstanceState(((SavedState)localObject).getVolumeStore());
    }
    return (Parcelable)localObject;
  }
  
  public void setStreamType(int paramInt)
  {
    this.mStreamType = paramInt;
  }
  
  private static class SavedState
    extends Preference.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public VolumePreference.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new VolumePreference.SavedState(paramAnonymousParcel);
      }
      
      public VolumePreference.SavedState[] newArray(int paramAnonymousInt)
      {
        return new VolumePreference.SavedState[paramAnonymousInt];
      }
    };
    VolumePreference.VolumeStore mVolumeStore = new VolumePreference.VolumeStore();
    
    public SavedState(Parcel paramParcel)
    {
      super();
      this.mVolumeStore.volume = paramParcel.readInt();
      this.mVolumeStore.originalVolume = paramParcel.readInt();
    }
    
    public SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    VolumePreference.VolumeStore getVolumeStore()
    {
      return this.mVolumeStore;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(this.mVolumeStore.volume);
      paramParcel.writeInt(this.mVolumeStore.originalVolume);
    }
  }
  
  public static class VolumeStore
  {
    public int originalVolume = -1;
    public int volume = -1;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/preference/VolumePreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */