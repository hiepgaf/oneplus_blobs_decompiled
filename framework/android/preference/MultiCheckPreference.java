package android.preference;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.view.AbsSavedState;
import com.android.internal.R.styleable;
import java.util.Arrays;

public class MultiCheckPreference
  extends DialogPreference
{
  private CharSequence[] mEntries;
  private String[] mEntryValues;
  private boolean[] mOrigValues;
  private boolean[] mSetValues;
  private String mSummary;
  
  public MultiCheckPreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public MultiCheckPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842897);
  }
  
  public MultiCheckPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public MultiCheckPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ListPreference, paramInt1, paramInt2);
    this.mEntries = localTypedArray.getTextArray(0);
    if (this.mEntries != null) {
      setEntries(this.mEntries);
    }
    setEntryValuesCS(localTypedArray.getTextArray(1));
    localTypedArray.recycle();
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.Preference, 0, 0);
    this.mSummary = paramContext.getString(7);
    paramContext.recycle();
  }
  
  private void setEntryValuesCS(CharSequence[] paramArrayOfCharSequence)
  {
    setValues(null);
    if (paramArrayOfCharSequence != null)
    {
      this.mEntryValues = new String[paramArrayOfCharSequence.length];
      int i = 0;
      while (i < paramArrayOfCharSequence.length)
      {
        this.mEntryValues[i] = paramArrayOfCharSequence[i].toString();
        i += 1;
      }
    }
  }
  
  public int findIndexOfValue(String paramString)
  {
    if ((paramString != null) && (this.mEntryValues != null))
    {
      int i = this.mEntryValues.length - 1;
      while (i >= 0)
      {
        if (this.mEntryValues[i].equals(paramString)) {
          return i;
        }
        i -= 1;
      }
    }
    return -1;
  }
  
  public CharSequence[] getEntries()
  {
    return this.mEntries;
  }
  
  public String[] getEntryValues()
  {
    return this.mEntryValues;
  }
  
  public CharSequence getSummary()
  {
    if (this.mSummary == null) {
      return super.getSummary();
    }
    return this.mSummary;
  }
  
  public boolean getValue(int paramInt)
  {
    return this.mSetValues[paramInt];
  }
  
  public boolean[] getValues()
  {
    return this.mSetValues;
  }
  
  protected void onDialogClosed(boolean paramBoolean)
  {
    super.onDialogClosed(paramBoolean);
    if ((paramBoolean) && (callChangeListener(getValues()))) {
      return;
    }
    System.arraycopy(this.mOrigValues, 0, this.mSetValues, 0, this.mSetValues.length);
  }
  
  protected Object onGetDefaultValue(TypedArray paramTypedArray, int paramInt)
  {
    return paramTypedArray.getString(paramInt);
  }
  
  protected void onPrepareDialogBuilder(AlertDialog.Builder paramBuilder)
  {
    super.onPrepareDialogBuilder(paramBuilder);
    if ((this.mEntries == null) || (this.mEntryValues == null)) {
      throw new IllegalStateException("ListPreference requires an entries array and an entryValues array.");
    }
    this.mOrigValues = Arrays.copyOf(this.mSetValues, this.mSetValues.length);
    paramBuilder.setMultiChoiceItems(this.mEntries, this.mSetValues, new DialogInterface.OnMultiChoiceClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt, boolean paramAnonymousBoolean)
      {
        MultiCheckPreference.-get0(MultiCheckPreference.this)[paramAnonymousInt] = paramAnonymousBoolean;
      }
    });
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if ((paramParcelable != null) && (paramParcelable.getClass().equals(SavedState.class)))
    {
      paramParcelable = (SavedState)paramParcelable;
      super.onRestoreInstanceState(paramParcelable.getSuperState());
      setValues(paramParcelable.values);
      return;
    }
    super.onRestoreInstanceState(paramParcelable);
  }
  
  protected Parcelable onSaveInstanceState()
  {
    Object localObject = super.onSaveInstanceState();
    if (isPersistent()) {
      return (Parcelable)localObject;
    }
    localObject = new SavedState((Parcelable)localObject);
    ((SavedState)localObject).values = getValues();
    return (Parcelable)localObject;
  }
  
  protected void onSetInitialValue(boolean paramBoolean, Object paramObject) {}
  
  public void setEntries(int paramInt)
  {
    setEntries(getContext().getResources().getTextArray(paramInt));
  }
  
  public void setEntries(CharSequence[] paramArrayOfCharSequence)
  {
    this.mEntries = paramArrayOfCharSequence;
    this.mSetValues = new boolean[paramArrayOfCharSequence.length];
    this.mOrigValues = new boolean[paramArrayOfCharSequence.length];
  }
  
  public void setEntryValues(int paramInt)
  {
    setEntryValuesCS(getContext().getResources().getTextArray(paramInt));
  }
  
  public void setEntryValues(String[] paramArrayOfString)
  {
    this.mEntryValues = paramArrayOfString;
    Arrays.fill(this.mSetValues, false);
    Arrays.fill(this.mOrigValues, false);
  }
  
  public void setSummary(CharSequence paramCharSequence)
  {
    super.setSummary(paramCharSequence);
    if ((paramCharSequence == null) && (this.mSummary != null)) {
      this.mSummary = null;
    }
    while ((paramCharSequence == null) || (paramCharSequence.equals(this.mSummary))) {
      return;
    }
    this.mSummary = paramCharSequence.toString();
  }
  
  public void setValue(int paramInt, boolean paramBoolean)
  {
    this.mSetValues[paramInt] = paramBoolean;
  }
  
  public void setValues(boolean[] paramArrayOfBoolean)
  {
    boolean[] arrayOfBoolean;
    if (this.mSetValues != null)
    {
      Arrays.fill(this.mSetValues, false);
      Arrays.fill(this.mOrigValues, false);
      if (paramArrayOfBoolean != null)
      {
        arrayOfBoolean = this.mSetValues;
        if (paramArrayOfBoolean.length >= this.mSetValues.length) {
          break label54;
        }
      }
    }
    label54:
    for (int i = paramArrayOfBoolean.length;; i = this.mSetValues.length)
    {
      System.arraycopy(paramArrayOfBoolean, 0, arrayOfBoolean, 0, i);
      return;
    }
  }
  
  private static class SavedState
    extends Preference.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public MultiCheckPreference.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new MultiCheckPreference.SavedState(paramAnonymousParcel);
      }
      
      public MultiCheckPreference.SavedState[] newArray(int paramAnonymousInt)
      {
        return new MultiCheckPreference.SavedState[paramAnonymousInt];
      }
    };
    boolean[] values;
    
    public SavedState(Parcel paramParcel)
    {
      super();
      this.values = paramParcel.createBooleanArray();
    }
    
    public SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeBooleanArray(this.values);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/preference/MultiCheckPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */