package android.preference;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.android.internal.R.styleable;

public final class PreferenceScreen
  extends PreferenceGroup
  implements AdapterView.OnItemClickListener, DialogInterface.OnDismissListener
{
  private Dialog mDialog;
  private Drawable mDividerDrawable;
  private boolean mDividerSpecified;
  private int mLayoutResId = 17367224;
  private ListView mListView;
  private ListAdapter mRootAdapter;
  
  public PreferenceScreen(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet, 16842891);
    paramContext = paramContext.obtainStyledAttributes(null, R.styleable.PreferenceScreen, 16842891, 0);
    this.mLayoutResId = paramContext.getResourceId(1, this.mLayoutResId);
    if (paramContext.hasValueOrEmpty(0))
    {
      this.mDividerDrawable = paramContext.getDrawable(0);
      this.mDividerSpecified = true;
    }
    paramContext.recycle();
  }
  
  private void showDialog(Bundle paramBundle)
  {
    Object localObject = getContext();
    if (this.mListView != null) {
      this.mListView.setAdapter(null);
    }
    View localView1 = ((LayoutInflater)((Context)localObject).getSystemService("layout_inflater")).inflate(this.mLayoutResId, null);
    View localView2 = localView1.findViewById(16908310);
    this.mListView = ((ListView)localView1.findViewById(16908298));
    if (this.mDividerSpecified) {
      this.mListView.setDivider(this.mDividerDrawable);
    }
    bind(this.mListView);
    CharSequence localCharSequence = getTitle();
    localObject = new Dialog((Context)localObject, ((Context)localObject).getThemeResId());
    this.mDialog = ((Dialog)localObject);
    if (TextUtils.isEmpty(localCharSequence))
    {
      if (localView2 != null) {
        localView2.setVisibility(8);
      }
      ((Dialog)localObject).getWindow().requestFeature(1);
    }
    for (;;)
    {
      ((Dialog)localObject).setContentView(localView1);
      ((Dialog)localObject).setOnDismissListener(this);
      if (paramBundle != null) {
        ((Dialog)localObject).onRestoreInstanceState(paramBundle);
      }
      getPreferenceManager().addPreferencesScreen((DialogInterface)localObject);
      ((Dialog)localObject).show();
      return;
      if ((localView2 instanceof TextView))
      {
        ((TextView)localView2).setText(localCharSequence);
        localView2.setVisibility(0);
      }
      else
      {
        ((Dialog)localObject).setTitle(localCharSequence);
      }
    }
  }
  
  public void bind(ListView paramListView)
  {
    paramListView.setOnItemClickListener(this);
    paramListView.setAdapter(getRootAdapter());
    onAttachedToActivity();
  }
  
  public Dialog getDialog()
  {
    return this.mDialog;
  }
  
  public ListAdapter getRootAdapter()
  {
    if (this.mRootAdapter == null) {
      this.mRootAdapter = onCreateRootAdapter();
    }
    return this.mRootAdapter;
  }
  
  protected boolean isOnSameScreenAsChildren()
  {
    return false;
  }
  
  protected void onClick()
  {
    if ((getIntent() != null) || (getFragment() != null)) {}
    while (getPreferenceCount() == 0) {
      return;
    }
    showDialog(null);
  }
  
  protected ListAdapter onCreateRootAdapter()
  {
    return new PreferenceGroupAdapter(this);
  }
  
  public void onDismiss(DialogInterface paramDialogInterface)
  {
    this.mDialog = null;
    getPreferenceManager().removePreferencesScreen(paramDialogInterface);
  }
  
  public void onItemClick(AdapterView paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    int i = paramInt;
    if ((paramAdapterView instanceof ListView)) {
      i = paramInt - ((ListView)paramAdapterView).getHeaderViewsCount();
    }
    paramAdapterView = getRootAdapter().getItem(i);
    if (!(paramAdapterView instanceof Preference)) {
      return;
    }
    ((Preference)paramAdapterView).performClick(this);
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if ((paramParcelable != null) && (paramParcelable.getClass().equals(SavedState.class)))
    {
      paramParcelable = (SavedState)paramParcelable;
      super.onRestoreInstanceState(paramParcelable.getSuperState());
      if (paramParcelable.isDialogShowing) {
        showDialog(paramParcelable.dialogBundle);
      }
      return;
    }
    super.onRestoreInstanceState(paramParcelable);
  }
  
  protected Parcelable onSaveInstanceState()
  {
    Object localObject = super.onSaveInstanceState();
    Dialog localDialog = this.mDialog;
    if ((localDialog != null) && (localDialog.isShowing()))
    {
      localObject = new SavedState((Parcelable)localObject);
      ((SavedState)localObject).isDialogShowing = true;
      ((SavedState)localObject).dialogBundle = localDialog.onSaveInstanceState();
      return (Parcelable)localObject;
    }
    return (Parcelable)localObject;
  }
  
  private static class SavedState
    extends Preference.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public PreferenceScreen.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new PreferenceScreen.SavedState(paramAnonymousParcel);
      }
      
      public PreferenceScreen.SavedState[] newArray(int paramAnonymousInt)
      {
        return new PreferenceScreen.SavedState[paramAnonymousInt];
      }
    };
    Bundle dialogBundle;
    boolean isDialogShowing;
    
    public SavedState(Parcel paramParcel)
    {
      super();
      if (paramParcel.readInt() == 1) {}
      for (;;)
      {
        this.isDialogShowing = bool;
        this.dialogBundle = paramParcel.readBundle();
        return;
        bool = false;
      }
    }
    
    public SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      if (this.isDialogShowing) {}
      for (paramInt = 1;; paramInt = 0)
      {
        paramParcel.writeInt(paramInt);
        paramParcel.writeBundle(this.dialogBundle);
        return;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/preference/PreferenceScreen.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */