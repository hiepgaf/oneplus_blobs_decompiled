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
import com.android.internal.R.styleable;
import java.util.HashSet;
import java.util.Set;

public class MultiSelectListPreference
  extends DialogPreference
{
  private CharSequence[] mEntries;
  private CharSequence[] mEntryValues;
  private Set<String> mNewValues = new HashSet();
  private boolean mPreferenceChanged;
  private Set<String> mValues = new HashSet();
  
  public MultiSelectListPreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public MultiSelectListPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842897);
  }
  
  public MultiSelectListPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public MultiSelectListPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.MultiSelectListPreference, paramInt1, paramInt2);
    this.mEntries = paramContext.getTextArray(0);
    this.mEntryValues = paramContext.getTextArray(1);
    paramContext.recycle();
  }
  
  private boolean[] getSelectedItems()
  {
    CharSequence[] arrayOfCharSequence = this.mEntryValues;
    int j = arrayOfCharSequence.length;
    Set localSet = this.mValues;
    boolean[] arrayOfBoolean = new boolean[j];
    int i = 0;
    while (i < j)
    {
      arrayOfBoolean[i] = localSet.contains(arrayOfCharSequence[i].toString());
      i += 1;
    }
    return arrayOfBoolean;
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
  
  public CharSequence[] getEntryValues()
  {
    return this.mEntryValues;
  }
  
  public Set<String> getValues()
  {
    return this.mValues;
  }
  
  protected void onDialogClosed(boolean paramBoolean)
  {
    super.onDialogClosed(paramBoolean);
    if ((paramBoolean) && (this.mPreferenceChanged))
    {
      Set localSet = this.mNewValues;
      if (callChangeListener(localSet)) {
        setValues(localSet);
      }
    }
    this.mPreferenceChanged = false;
  }
  
  protected Object onGetDefaultValue(TypedArray paramTypedArray, int paramInt)
  {
    paramTypedArray = paramTypedArray.getTextArray(paramInt);
    int i = paramTypedArray.length;
    HashSet localHashSet = new HashSet();
    paramInt = 0;
    while (paramInt < i)
    {
      localHashSet.add(paramTypedArray[paramInt].toString());
      paramInt += 1;
    }
    return localHashSet;
  }
  
  protected void onPrepareDialogBuilder(AlertDialog.Builder paramBuilder)
  {
    super.onPrepareDialogBuilder(paramBuilder);
    if ((this.mEntries == null) || (this.mEntryValues == null)) {
      throw new IllegalStateException("MultiSelectListPreference requires an entries array and an entryValues array.");
    }
    boolean[] arrayOfBoolean = getSelectedItems();
    paramBuilder.setMultiChoiceItems(this.mEntries, arrayOfBoolean, new DialogInterface.OnMultiChoiceClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt, boolean paramAnonymousBoolean)
      {
        if (paramAnonymousBoolean)
        {
          paramAnonymousDialogInterface = MultiSelectListPreference.this;
          MultiSelectListPreference.-set0(paramAnonymousDialogInterface, MultiSelectListPreference.-get2(paramAnonymousDialogInterface) | MultiSelectListPreference.-get1(MultiSelectListPreference.this).add(MultiSelectListPreference.-get0(MultiSelectListPreference.this)[paramAnonymousInt].toString()));
          return;
        }
        paramAnonymousDialogInterface = MultiSelectListPreference.this;
        MultiSelectListPreference.-set0(paramAnonymousDialogInterface, MultiSelectListPreference.-get2(paramAnonymousDialogInterface) | MultiSelectListPreference.-get1(MultiSelectListPreference.this).remove(MultiSelectListPreference.-get0(MultiSelectListPreference.this)[paramAnonymousInt].toString()));
      }
    });
    this.mNewValues.clear();
    this.mNewValues.addAll(this.mValues);
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
  
  protected void onSetInitialValue(boolean paramBoolean, Object paramObject)
  {
    if (paramBoolean) {}
    for (paramObject = getPersistedStringSet(this.mValues);; paramObject = (Set)paramObject)
    {
      setValues((Set)paramObject);
      return;
    }
  }
  
  public void setEntries(int paramInt)
  {
    setEntries(getContext().getResources().getTextArray(paramInt));
  }
  
  public void setEntries(CharSequence[] paramArrayOfCharSequence)
  {
    this.mEntries = paramArrayOfCharSequence;
  }
  
  public void setEntryValues(int paramInt)
  {
    setEntryValues(getContext().getResources().getTextArray(paramInt));
  }
  
  public void setEntryValues(CharSequence[] paramArrayOfCharSequence)
  {
    this.mEntryValues = paramArrayOfCharSequence;
  }
  
  public void setValues(Set<String> paramSet)
  {
    this.mValues.clear();
    this.mValues.addAll(paramSet);
    persistStringSet(paramSet);
  }
  
  private static class SavedState
    extends Preference.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public MultiSelectListPreference.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new MultiSelectListPreference.SavedState(paramAnonymousParcel);
      }
      
      public MultiSelectListPreference.SavedState[] newArray(int paramAnonymousInt)
      {
        return new MultiSelectListPreference.SavedState[paramAnonymousInt];
      }
    };
    Set<String> values;
    
    public SavedState(Parcel paramParcel)
    {
      super();
      this.values = new HashSet();
      paramParcel = paramParcel.readStringArray();
      int j = paramParcel.length;
      int i = 0;
      while (i < j)
      {
        this.values.add(paramParcel[i]);
        i += 1;
      }
    }
    
    public SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeStringArray((String[])this.values.toArray(new String[0]));
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/preference/MultiSelectListPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */