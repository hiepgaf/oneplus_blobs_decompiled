package android.preference;

import android.content.Context;
import android.util.AttributeSet;

public class PreferenceCategory
  extends PreferenceGroup
{
  private static final String TAG = "PreferenceCategory";
  
  public PreferenceCategory(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public PreferenceCategory(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842892);
  }
  
  public PreferenceCategory(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public PreferenceCategory(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  public boolean isEnabled()
  {
    return false;
  }
  
  protected boolean onPrepareAddPreference(Preference paramPreference)
  {
    if ((paramPreference instanceof PreferenceCategory)) {
      throw new IllegalArgumentException("Cannot add a PreferenceCategory directly to a PreferenceCategory");
    }
    return super.onPrepareAddPreference(paramPreference);
  }
  
  public boolean shouldDisableDependents()
  {
    return !super.isEnabled();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/preference/PreferenceCategory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */