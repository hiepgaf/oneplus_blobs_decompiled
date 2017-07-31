package android.app;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.BaseBundle;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.SearchView.OnSuggestionListener;
import android.widget.TextView;

public class SearchDialog
  extends Dialog
{
  private static final boolean DBG = false;
  private static final String IME_OPTION_NO_MICROPHONE = "nm";
  private static final String INSTANCE_KEY_APPDATA = "data";
  private static final String INSTANCE_KEY_COMPONENT = "comp";
  private static final String INSTANCE_KEY_USER_QUERY = "uQry";
  private static final String LOG_TAG = "SearchDialog";
  private static final int SEARCH_PLATE_LEFT_PADDING_NON_GLOBAL = 7;
  private Context mActivityContext;
  private ImageView mAppIcon;
  private Bundle mAppSearchData;
  private TextView mBadgeLabel;
  private View mCloseSearch;
  private BroadcastReceiver mConfChangeListener = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (paramAnonymousIntent.getAction().equals("android.intent.action.CONFIGURATION_CHANGED")) {
        SearchDialog.this.onConfigurationChanged();
      }
    }
  };
  private ComponentName mLaunchComponent;
  private final SearchView.OnCloseListener mOnCloseListener = new SearchView.OnCloseListener()
  {
    public boolean onClose()
    {
      return SearchDialog.-wrap0(SearchDialog.this);
    }
  };
  private final SearchView.OnQueryTextListener mOnQueryChangeListener = new SearchView.OnQueryTextListener()
  {
    public boolean onQueryTextChange(String paramAnonymousString)
    {
      return false;
    }
    
    public boolean onQueryTextSubmit(String paramAnonymousString)
    {
      SearchDialog.this.dismiss();
      return false;
    }
  };
  private final SearchView.OnSuggestionListener mOnSuggestionSelectionListener = new SearchView.OnSuggestionListener()
  {
    public boolean onSuggestionClick(int paramAnonymousInt)
    {
      SearchDialog.this.dismiss();
      return false;
    }
    
    public boolean onSuggestionSelect(int paramAnonymousInt)
    {
      return false;
    }
  };
  private AutoCompleteTextView mSearchAutoComplete;
  private int mSearchAutoCompleteImeOptions;
  private View mSearchPlate;
  private SearchView mSearchView;
  private SearchableInfo mSearchable;
  private String mUserQuery;
  private final Intent mVoiceAppSearchIntent;
  private final Intent mVoiceWebSearchIntent = new Intent("android.speech.action.WEB_SEARCH");
  private Drawable mWorkingSpinner;
  
  public SearchDialog(Context paramContext, SearchManager paramSearchManager)
  {
    super(paramContext, resolveDialogTheme(paramContext));
    this.mVoiceWebSearchIntent.addFlags(268435456);
    this.mVoiceWebSearchIntent.putExtra("android.speech.extra.LANGUAGE_MODEL", "web_search");
    this.mVoiceAppSearchIntent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
    this.mVoiceAppSearchIntent.addFlags(268435456);
  }
  
  private void createContentView()
  {
    setContentView(17367256);
    this.mSearchView = ((SearchView)findViewById(16909312));
    this.mSearchView.setIconified(false);
    this.mSearchView.setOnCloseListener(this.mOnCloseListener);
    this.mSearchView.setOnQueryTextListener(this.mOnQueryChangeListener);
    this.mSearchView.setOnSuggestionListener(this.mOnSuggestionSelectionListener);
    this.mSearchView.onActionViewExpanded();
    this.mCloseSearch = findViewById(16908327);
    this.mCloseSearch.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        SearchDialog.this.dismiss();
      }
    });
    this.mBadgeLabel = ((TextView)this.mSearchView.findViewById(16909314));
    this.mSearchAutoComplete = ((AutoCompleteTextView)this.mSearchView.findViewById(16909319));
    this.mAppIcon = ((ImageView)findViewById(16909311));
    this.mSearchPlate = this.mSearchView.findViewById(16909318);
    this.mWorkingSpinner = getContext().getDrawable(17303167);
    setWorking(false);
    this.mBadgeLabel.setVisibility(8);
    this.mSearchAutoCompleteImeOptions = this.mSearchAutoComplete.getImeOptions();
  }
  
  private Intent createIntent(String paramString1, Uri paramUri, String paramString2, String paramString3, int paramInt, String paramString4)
  {
    paramString1 = new Intent(paramString1);
    paramString1.addFlags(268435456);
    if (paramUri != null) {
      paramString1.setData(paramUri);
    }
    paramString1.putExtra("user_query", this.mUserQuery);
    if (paramString3 != null) {
      paramString1.putExtra("query", paramString3);
    }
    if (paramString2 != null) {
      paramString1.putExtra("intent_extra_data_key", paramString2);
    }
    if (this.mAppSearchData != null) {
      paramString1.putExtra("app_data", this.mAppSearchData);
    }
    if (paramInt != 0)
    {
      paramString1.putExtra("action_key", paramInt);
      paramString1.putExtra("action_msg", paramString4);
    }
    paramString1.setComponent(this.mSearchable.getSearchActivity());
    return paramString1;
  }
  
  private boolean doShow(String paramString, boolean paramBoolean, ComponentName paramComponentName, Bundle paramBundle)
  {
    if (!show(paramComponentName, paramBundle)) {
      return false;
    }
    setUserQuery(paramString);
    if (paramBoolean) {
      this.mSearchAutoComplete.selectAll();
    }
    return true;
  }
  
  private boolean isEmpty(AutoCompleteTextView paramAutoCompleteTextView)
  {
    boolean bool = false;
    if (TextUtils.getTrimmedLength(paramAutoCompleteTextView.getText()) == 0) {
      bool = true;
    }
    return bool;
  }
  
  static boolean isLandscapeMode(Context paramContext)
  {
    return paramContext.getResources().getConfiguration().orientation == 2;
  }
  
  private boolean isOutOfBounds(View paramView, MotionEvent paramMotionEvent)
  {
    int i = (int)paramMotionEvent.getX();
    int j = (int)paramMotionEvent.getY();
    int k = ViewConfiguration.get(this.mContext).getScaledWindowTouchSlop();
    if ((i < -k) || (j < -k)) {}
    while ((i > paramView.getWidth() + k) || (j > paramView.getHeight() + k)) {
      return true;
    }
    return false;
  }
  
  private void launchIntent(Intent paramIntent)
  {
    if (paramIntent == null) {
      return;
    }
    Log.d("SearchDialog", "launching " + paramIntent);
    try
    {
      getContext().startActivity(paramIntent);
      dismiss();
      return;
    }
    catch (RuntimeException localRuntimeException)
    {
      Log.e("SearchDialog", "Failed launch activity: " + paramIntent, localRuntimeException);
    }
  }
  
  private boolean onClosePressed()
  {
    if (isEmpty(this.mSearchAutoComplete))
    {
      dismiss();
      return true;
    }
    return false;
  }
  
  static int resolveDialogTheme(Context paramContext)
  {
    TypedValue localTypedValue = new TypedValue();
    paramContext.getTheme().resolveAttribute(18219053, localTypedValue, true);
    return localTypedValue.resourceId;
  }
  
  private void setUserQuery(String paramString)
  {
    String str = paramString;
    if (paramString == null) {
      str = "";
    }
    this.mUserQuery = str;
    this.mSearchAutoComplete.setText(str);
    this.mSearchAutoComplete.setSelection(str.length());
  }
  
  private boolean show(ComponentName paramComponentName, Bundle paramBundle)
  {
    this.mSearchable = ((SearchManager)this.mContext.getSystemService("search")).getSearchableInfo(paramComponentName);
    if (this.mSearchable == null) {
      return false;
    }
    this.mLaunchComponent = paramComponentName;
    this.mAppSearchData = paramBundle;
    this.mActivityContext = this.mSearchable.getActivityContext(getContext());
    if (!isShowing())
    {
      createContentView();
      this.mSearchView.setSearchableInfo(this.mSearchable);
      this.mSearchView.setAppSearchData(this.mAppSearchData);
      show();
    }
    updateUI();
    return true;
  }
  
  private void updateSearchAppIcon()
  {
    PackageManager localPackageManager = getContext().getPackageManager();
    try
    {
      Drawable localDrawable1 = localPackageManager.getApplicationIcon(localPackageManager.getActivityInfo(this.mLaunchComponent, 0).applicationInfo);
      this.mAppIcon.setImageDrawable(localDrawable1);
      this.mAppIcon.setVisibility(0);
      this.mSearchPlate.setPadding(7, this.mSearchPlate.getPaddingTop(), this.mSearchPlate.getPaddingRight(), this.mSearchPlate.getPaddingBottom());
      return;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      for (;;)
      {
        Drawable localDrawable2 = localPackageManager.getDefaultActivityIcon();
        Log.w("SearchDialog", this.mLaunchComponent + " not found, using generic app icon");
      }
    }
  }
  
  private void updateSearchAutoComplete()
  {
    this.mSearchAutoComplete.setDropDownDismissedOnCompletion(false);
    this.mSearchAutoComplete.setForceIgnoreOutsideTouch(false);
  }
  
  private void updateSearchBadge()
  {
    int i = 8;
    Object localObject3 = null;
    Object localObject2 = null;
    Object localObject1;
    if (this.mSearchable.useBadgeIcon())
    {
      localObject1 = this.mActivityContext.getDrawable(this.mSearchable.getIconId());
      i = 0;
    }
    for (;;)
    {
      this.mBadgeLabel.setCompoundDrawablesWithIntrinsicBounds((Drawable)localObject1, null, null, null);
      this.mBadgeLabel.setText((CharSequence)localObject2);
      this.mBadgeLabel.setVisibility(i);
      return;
      localObject1 = localObject3;
      if (this.mSearchable.useBadgeLabel())
      {
        localObject2 = this.mActivityContext.getResources().getText(this.mSearchable.getLabelId()).toString();
        i = 0;
        localObject1 = localObject3;
      }
    }
  }
  
  private void updateUI()
  {
    if (this.mSearchable != null)
    {
      this.mDecor.setVisibility(0);
      updateSearchAutoComplete();
      updateSearchAppIcon();
      updateSearchBadge();
      int j = this.mSearchable.getInputType();
      int i = j;
      if ((j & 0xF) == 1)
      {
        j &= 0xFFFEFFFF;
        i = j;
        if (this.mSearchable.getSuggestAuthority() != null) {
          i = j | 0x10000;
        }
      }
      this.mSearchAutoComplete.setInputType(i);
      this.mSearchAutoCompleteImeOptions = this.mSearchable.getImeOptions();
      this.mSearchAutoComplete.setImeOptions(this.mSearchAutoCompleteImeOptions);
      if (this.mSearchable.getVoiceSearchEnabled()) {
        this.mSearchAutoComplete.setPrivateImeOptions("nm");
      }
    }
    else
    {
      return;
    }
    this.mSearchAutoComplete.setPrivateImeOptions(null);
  }
  
  public void hide()
  {
    if (!isShowing()) {
      return;
    }
    InputMethodManager localInputMethodManager = (InputMethodManager)getContext().getSystemService(InputMethodManager.class);
    if (localInputMethodManager != null) {
      localInputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }
    super.hide();
  }
  
  public void launchQuerySearch()
  {
    launchQuerySearch(0, null);
  }
  
  protected void launchQuerySearch(int paramInt, String paramString)
  {
    launchIntent(createIntent("android.intent.action.SEARCH", null, null, this.mSearchAutoComplete.getText().toString(), paramInt, paramString));
  }
  
  public void onBackPressed()
  {
    InputMethodManager localInputMethodManager = (InputMethodManager)getContext().getSystemService(InputMethodManager.class);
    if ((localInputMethodManager != null) && (localInputMethodManager.isFullscreenMode()) && (localInputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0))) {
      return;
    }
    cancel();
  }
  
  public void onConfigurationChanged()
  {
    if ((this.mSearchable != null) && (isShowing()))
    {
      updateSearchAppIcon();
      updateSearchBadge();
      if (isLandscapeMode(getContext())) {
        this.mSearchAutoComplete.ensureImeVisible(true);
      }
    }
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = getWindow();
    WindowManager.LayoutParams localLayoutParams = paramBundle.getAttributes();
    localLayoutParams.width = -1;
    localLayoutParams.height = -1;
    localLayoutParams.gravity = 55;
    localLayoutParams.softInputMode = 16;
    paramBundle.setAttributes(localLayoutParams);
    setCanceledOnTouchOutside(true);
  }
  
  public void onRestoreInstanceState(Bundle paramBundle)
  {
    if (paramBundle == null) {
      return;
    }
    ComponentName localComponentName = (ComponentName)paramBundle.getParcelable("comp");
    Bundle localBundle = paramBundle.getBundle("data");
    if (!doShow(paramBundle.getString("uQry"), false, localComponentName, localBundle)) {}
  }
  
  public Bundle onSaveInstanceState()
  {
    if (!isShowing()) {
      return null;
    }
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("comp", this.mLaunchComponent);
    localBundle.putBundle("data", this.mAppSearchData);
    localBundle.putString("uQry", this.mUserQuery);
    return localBundle;
  }
  
  public void onStart()
  {
    super.onStart();
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.CONFIGURATION_CHANGED");
    getContext().registerReceiver(this.mConfChangeListener, localIntentFilter);
  }
  
  public void onStop()
  {
    super.onStop();
    getContext().unregisterReceiver(this.mConfChangeListener);
    this.mLaunchComponent = null;
    this.mAppSearchData = null;
    this.mSearchable = null;
    this.mUserQuery = null;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((!this.mSearchAutoComplete.isPopupShowing()) && (isOutOfBounds(this.mSearchPlate, paramMotionEvent)))
    {
      cancel();
      return true;
    }
    return super.onTouchEvent(paramMotionEvent);
  }
  
  public void setListSelection(int paramInt)
  {
    this.mSearchAutoComplete.setListSelection(paramInt);
  }
  
  public void setWorking(boolean paramBoolean)
  {
    Drawable localDrawable = this.mWorkingSpinner;
    if (paramBoolean) {}
    for (int i = 255;; i = 0)
    {
      localDrawable.setAlpha(i);
      this.mWorkingSpinner.setVisible(paramBoolean, false);
      this.mWorkingSpinner.invalidateSelf();
      return;
    }
  }
  
  public boolean show(String paramString, boolean paramBoolean, ComponentName paramComponentName, Bundle paramBundle)
  {
    paramBoolean = doShow(paramString, paramBoolean, paramComponentName, paramBundle);
    if (paramBoolean) {
      this.mSearchAutoComplete.showDropDownAfterLayout();
    }
    return paramBoolean;
  }
  
  public static class SearchBar
    extends LinearLayout
  {
    public SearchBar(Context paramContext)
    {
      super();
    }
    
    public SearchBar(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
    
    public ActionMode startActionModeForChild(View paramView, ActionMode.Callback paramCallback, int paramInt)
    {
      if (paramInt != 0) {
        return super.startActionModeForChild(paramView, paramCallback, paramInt);
      }
      return null;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/SearchDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */