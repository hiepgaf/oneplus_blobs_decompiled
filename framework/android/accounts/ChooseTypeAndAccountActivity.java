package android.accounts;

import android.app.Activity;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.Intent;
import android.content.res.Resources;
import android.os.BaseBundle;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.collect.Sets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ChooseTypeAndAccountActivity
  extends Activity
  implements AccountManagerCallback<Bundle>
{
  public static final String EXTRA_ADD_ACCOUNT_AUTH_TOKEN_TYPE_STRING = "authTokenType";
  public static final String EXTRA_ADD_ACCOUNT_OPTIONS_BUNDLE = "addAccountOptions";
  public static final String EXTRA_ADD_ACCOUNT_REQUIRED_FEATURES_STRING_ARRAY = "addAccountRequiredFeatures";
  public static final String EXTRA_ALLOWABLE_ACCOUNTS_ARRAYLIST = "allowableAccounts";
  public static final String EXTRA_ALLOWABLE_ACCOUNT_TYPES_STRING_ARRAY = "allowableAccountTypes";
  @Deprecated
  public static final String EXTRA_ALWAYS_PROMPT_FOR_ACCOUNT = "alwaysPromptForAccount";
  public static final String EXTRA_DESCRIPTION_TEXT_OVERRIDE = "descriptionTextOverride";
  public static final String EXTRA_SELECTED_ACCOUNT = "selectedAccount";
  private static final String KEY_INSTANCE_STATE_ACCOUNT_LIST = "accountList";
  private static final String KEY_INSTANCE_STATE_EXISTING_ACCOUNTS = "existingAccounts";
  private static final String KEY_INSTANCE_STATE_PENDING_REQUEST = "pendingRequest";
  private static final String KEY_INSTANCE_STATE_SELECTED_ACCOUNT_NAME = "selectedAccountName";
  private static final String KEY_INSTANCE_STATE_SELECTED_ADD_ACCOUNT = "selectedAddAccount";
  public static final int REQUEST_ADD_ACCOUNT = 2;
  public static final int REQUEST_CHOOSE_TYPE = 1;
  public static final int REQUEST_NULL = 0;
  private static final int SELECTED_ITEM_NONE = -1;
  private static final String TAG = "AccountChooser";
  private ArrayList<Account> mAccounts;
  private String mCallingPackage;
  private int mCallingUid;
  private String mDescriptionOverride;
  private boolean mDisallowAddAccounts;
  private boolean mDontShowPicker;
  private Parcelable[] mExistingAccounts = null;
  private Button mOkButton;
  private int mPendingRequest = 0;
  private String mSelectedAccountName = null;
  private boolean mSelectedAddNewAccount = false;
  private int mSelectedItemIndex;
  private Set<Account> mSetOfAllowableAccounts;
  private Set<String> mSetOfRelevantAccountTypes;
  
  private ArrayList<Account> getAcceptableAccountChoices(AccountManager paramAccountManager)
  {
    paramAccountManager = paramAccountManager.getAccountsForPackage(this.mCallingPackage, this.mCallingUid);
    ArrayList localArrayList = new ArrayList(paramAccountManager.length);
    int i = 0;
    int j = paramAccountManager.length;
    while (i < j)
    {
      Object localObject = paramAccountManager[i];
      if (((this.mSetOfAllowableAccounts == null) || (this.mSetOfAllowableAccounts.contains(localObject))) && ((this.mSetOfRelevantAccountTypes == null) || (this.mSetOfRelevantAccountTypes.contains(((Account)localObject).type)))) {
        localArrayList.add(localObject);
      }
      i += 1;
    }
    return localArrayList;
  }
  
  private Set<Account> getAllowableAccountSet(Intent paramIntent)
  {
    HashSet localHashSet = null;
    Object localObject = paramIntent.getParcelableArrayListExtra("allowableAccounts");
    paramIntent = localHashSet;
    if (localObject != null)
    {
      localHashSet = new HashSet(((ArrayList)localObject).size());
      localObject = ((Iterable)localObject).iterator();
      for (;;)
      {
        paramIntent = localHashSet;
        if (!((Iterator)localObject).hasNext()) {
          break;
        }
        localHashSet.add((Account)((Iterator)localObject).next());
      }
    }
    return paramIntent;
  }
  
  private int getItemIndexToSelect(ArrayList<Account> paramArrayList, String paramString, boolean paramBoolean)
  {
    if (paramBoolean) {
      return paramArrayList.size();
    }
    int i = 0;
    while (i < paramArrayList.size())
    {
      if (((Account)paramArrayList.get(i)).name.equals(paramString)) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  private String[] getListOfDisplayableOptions(ArrayList<Account> paramArrayList)
  {
    int j = paramArrayList.size();
    if (this.mDisallowAddAccounts) {}
    String[] arrayOfString;
    for (int i = 0;; i = 1)
    {
      arrayOfString = new String[i + j];
      i = 0;
      while (i < paramArrayList.size())
      {
        arrayOfString[i] = ((Account)paramArrayList.get(i)).name;
        i += 1;
      }
    }
    if (!this.mDisallowAddAccounts) {
      arrayOfString[paramArrayList.size()] = getResources().getString(17040566);
    }
    return arrayOfString;
  }
  
  private Set<String> getReleventAccountTypes(Intent paramIntent)
  {
    Object localObject = paramIntent.getStringArrayExtra("allowableAccountTypes");
    AuthenticatorDescription[] arrayOfAuthenticatorDescription = AccountManager.get(this).getAuthenticatorTypes();
    paramIntent = new HashSet(arrayOfAuthenticatorDescription.length);
    int i = 0;
    int j = arrayOfAuthenticatorDescription.length;
    while (i < j)
    {
      paramIntent.add(arrayOfAuthenticatorDescription[i].type);
      i += 1;
    }
    if (localObject != null)
    {
      localObject = Sets.newHashSet((Object[])localObject);
      ((Set)localObject).retainAll(paramIntent);
      return (Set<String>)localObject;
    }
    return paramIntent;
  }
  
  private void onAccountSelected(Account paramAccount)
  {
    Log.d("AccountChooser", "selected account " + paramAccount);
    setResultAndFinish(paramAccount.name, paramAccount.type);
  }
  
  private void overrideDescriptionIfSupplied(String paramString)
  {
    TextView localTextView = (TextView)findViewById(16909119);
    if (!TextUtils.isEmpty(paramString))
    {
      localTextView.setText(paramString);
      return;
    }
    localTextView.setVisibility(8);
  }
  
  private final void populateUIAccountList(String[] paramArrayOfString)
  {
    ListView localListView = (ListView)findViewById(16908298);
    localListView.setAdapter(new ArrayAdapter(this, 17367055, paramArrayOfString));
    localListView.setChoiceMode(1);
    localListView.setItemsCanFocus(false);
    localListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        ChooseTypeAndAccountActivity.-set0(ChooseTypeAndAccountActivity.this, paramAnonymousInt);
        ChooseTypeAndAccountActivity.-get0(ChooseTypeAndAccountActivity.this).setEnabled(true);
      }
    });
    if (this.mSelectedItemIndex != -1)
    {
      localListView.setItemChecked(this.mSelectedItemIndex, true);
      if (Log.isLoggable("AccountChooser", 2)) {
        Log.v("AccountChooser", "List item " + this.mSelectedItemIndex + " should be selected");
      }
    }
  }
  
  private void setNonLabelThemeAndCallSuperCreate(Bundle paramBundle)
  {
    setTheme(16974132);
    super.onCreate(paramBundle);
  }
  
  private void setResultAndFinish(String paramString1, String paramString2)
  {
    Bundle localBundle = new Bundle();
    localBundle.putString("authAccount", paramString1);
    localBundle.putString("accountType", paramString2);
    setResult(-1, new Intent().putExtras(localBundle));
    if (Log.isLoggable("AccountChooser", 2)) {
      Log.v("AccountChooser", "ChooseTypeAndAccountActivity.setResultAndFinish: selected account " + paramString1 + ", " + paramString2);
    }
    finish();
  }
  
  private void startChooseAccountTypeActivity()
  {
    if (Log.isLoggable("AccountChooser", 2)) {
      Log.v("AccountChooser", "ChooseAccountTypeActivity.startChooseAccountTypeActivity()");
    }
    Intent localIntent = new Intent(this, ChooseAccountTypeActivity.class);
    localIntent.setFlags(524288);
    localIntent.putExtra("allowableAccountTypes", getIntent().getStringArrayExtra("allowableAccountTypes"));
    localIntent.putExtra("addAccountOptions", getIntent().getBundleExtra("addAccountOptions"));
    localIntent.putExtra("addAccountRequiredFeatures", getIntent().getStringArrayExtra("addAccountRequiredFeatures"));
    localIntent.putExtra("authTokenType", getIntent().getStringExtra("authTokenType"));
    startActivityForResult(localIntent, 1);
    this.mPendingRequest = 1;
  }
  
  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if (Log.isLoggable("AccountChooser", 2))
    {
      if ((paramIntent != null) && (paramIntent.getExtras() != null)) {
        paramIntent.getExtras().keySet();
      }
      if (paramIntent == null) {
        break label120;
      }
    }
    label120:
    for (Object localObject1 = paramIntent.getExtras();; localObject1 = null)
    {
      Log.v("AccountChooser", "ChooseTypeAndAccountActivity.onActivityResult(reqCode=" + paramInt1 + ", resCode=" + paramInt2 + ", extras=" + localObject1 + ")");
      this.mPendingRequest = 0;
      if (paramInt2 != 0) {
        break;
      }
      if (this.mAccounts.isEmpty())
      {
        setResult(0);
        finish();
      }
      return;
    }
    if (paramInt2 == -1)
    {
      if (paramInt1 != 1) {
        break label204;
      }
      if (paramIntent != null)
      {
        paramIntent = paramIntent.getStringExtra("accountType");
        if (paramIntent != null)
        {
          runAddAccountForAuthenticator(paramIntent);
          return;
        }
      }
      Log.d("AccountChooser", "ChooseTypeAndAccountActivity.onActivityResult: unable to find account type, pretending the request was canceled");
    }
    label204:
    while (paramInt1 != 2)
    {
      Log.d("AccountChooser", "ChooseTypeAndAccountActivity.onActivityResult: unable to find added account, pretending the request was canceled");
      if (Log.isLoggable("AccountChooser", 2)) {
        Log.v("AccountChooser", "ChooseTypeAndAccountActivity.onActivityResult: canceled");
      }
      setResult(0);
      finish();
      return;
    }
    localObject1 = null;
    String str = null;
    if (paramIntent != null)
    {
      localObject1 = paramIntent.getStringExtra("authAccount");
      str = paramIntent.getStringExtra("accountType");
    }
    Object localObject2;
    Account[] arrayOfAccount;
    HashSet localHashSet;
    if (localObject1 != null)
    {
      localObject2 = localObject1;
      paramIntent = str;
      if (str != null) {}
    }
    else
    {
      arrayOfAccount = AccountManager.get(this).getAccountsForPackage(this.mCallingPackage, this.mCallingUid);
      localHashSet = new HashSet();
      paramIntent = this.mExistingAccounts;
      paramInt2 = paramIntent.length;
      paramInt1 = 0;
      while (paramInt1 < paramInt2)
      {
        localHashSet.add((Account)paramIntent[paramInt1]);
        paramInt1 += 1;
      }
      paramInt2 = arrayOfAccount.length;
      paramInt1 = 0;
    }
    for (;;)
    {
      localObject2 = localObject1;
      paramIntent = str;
      if (paramInt1 < paramInt2)
      {
        paramIntent = arrayOfAccount[paramInt1];
        if (!localHashSet.contains(paramIntent))
        {
          localObject2 = paramIntent.name;
          paramIntent = paramIntent.type;
        }
      }
      else
      {
        if ((localObject2 == null) && (paramIntent == null)) {
          break;
        }
        setResultAndFinish((String)localObject2, paramIntent);
        return;
      }
      paramInt1 += 1;
    }
  }
  
  public void onCancelButtonClicked(View paramView)
  {
    onBackPressed();
  }
  
  public void onCreate(Bundle paramBundle)
  {
    if (Log.isLoggable("AccountChooser", 2)) {
      Log.v("AccountChooser", "ChooseTypeAndAccountActivity.onCreate(savedInstanceState=" + paramBundle + ")");
    }
    try
    {
      Object localObject = getActivityToken();
      this.mCallingUid = ActivityManagerNative.getDefault().getLaunchedFromUid((IBinder)localObject);
      this.mCallingPackage = ActivityManagerNative.getDefault().getLaunchedFromPackage((IBinder)localObject);
      if ((this.mCallingUid != 0) && (this.mCallingPackage != null)) {
        this.mDisallowAddAccounts = UserManager.get(this).getUserRestrictions(new UserHandle(UserHandle.getUserId(this.mCallingUid))).getBoolean("no_modify_accounts", false);
      }
      localObject = getIntent();
      if (paramBundle != null)
      {
        this.mPendingRequest = paramBundle.getInt("pendingRequest");
        this.mExistingAccounts = paramBundle.getParcelableArray("existingAccounts");
        this.mSelectedAccountName = paramBundle.getString("selectedAccountName");
        this.mSelectedAddNewAccount = paramBundle.getBoolean("selectedAddAccount", false);
        this.mAccounts = paramBundle.getParcelableArrayList("accountList");
        if (Log.isLoggable("AccountChooser", 2)) {
          Log.v("AccountChooser", "selected account name is " + this.mSelectedAccountName);
        }
        this.mSetOfAllowableAccounts = getAllowableAccountSet((Intent)localObject);
        this.mSetOfRelevantAccountTypes = getReleventAccountTypes((Intent)localObject);
        this.mDescriptionOverride = ((Intent)localObject).getStringExtra("descriptionTextOverride");
        this.mAccounts = getAcceptableAccountChoices(AccountManager.get(this));
        if ((this.mAccounts.isEmpty()) && (this.mDisallowAddAccounts))
        {
          requestWindowFeature(1);
          setContentView(17367093);
          this.mDontShowPicker = true;
        }
        if (!this.mDontShowPicker) {
          break label377;
        }
        super.onCreate(paramBundle);
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.w(getClass().getSimpleName(), "Unable to get caller identity \n" + localRemoteException);
        continue;
        this.mPendingRequest = 0;
        this.mExistingAccounts = null;
        Account localAccount = (Account)localRemoteException.getParcelableExtra("selectedAccount");
        if (localAccount != null) {
          this.mSelectedAccountName = localAccount.name;
        }
      }
      label377:
      if ((this.mPendingRequest == 0) && (this.mAccounts.isEmpty()))
      {
        setNonLabelThemeAndCallSuperCreate(paramBundle);
        if (this.mSetOfRelevantAccountTypes.size() != 1) {
          break label522;
        }
        runAddAccountForAuthenticator((String)this.mSetOfRelevantAccountTypes.iterator().next());
      }
    }
    String[] arrayOfString = getListOfDisplayableOptions(this.mAccounts);
    this.mSelectedItemIndex = getItemIndexToSelect(this.mAccounts, this.mSelectedAccountName, this.mSelectedAddNewAccount);
    super.onCreate(paramBundle);
    setContentView(17367107);
    overrideDescriptionIfSupplied(this.mDescriptionOverride);
    populateUIAccountList(arrayOfString);
    this.mOkButton = ((Button)findViewById(16908314));
    paramBundle = this.mOkButton;
    if (this.mSelectedItemIndex != -1) {}
    for (boolean bool = true;; bool = false)
    {
      paramBundle.setEnabled(bool);
      return;
      label522:
      startChooseAccountTypeActivity();
      break;
    }
  }
  
  protected void onDestroy()
  {
    if (Log.isLoggable("AccountChooser", 2)) {
      Log.v("AccountChooser", "ChooseTypeAndAccountActivity.onDestroy()");
    }
    super.onDestroy();
  }
  
  public void onOkButtonClicked(View paramView)
  {
    if (this.mSelectedItemIndex == this.mAccounts.size()) {
      startChooseAccountTypeActivity();
    }
    while (this.mSelectedItemIndex == -1) {
      return;
    }
    onAccountSelected((Account)this.mAccounts.get(this.mSelectedItemIndex));
  }
  
  protected void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putInt("pendingRequest", this.mPendingRequest);
    if (this.mPendingRequest == 2) {
      paramBundle.putParcelableArray("existingAccounts", this.mExistingAccounts);
    }
    if (this.mSelectedItemIndex != -1)
    {
      if (this.mSelectedItemIndex != this.mAccounts.size()) {
        break label73;
      }
      paramBundle.putBoolean("selectedAddAccount", true);
    }
    for (;;)
    {
      paramBundle.putParcelableArrayList("accountList", this.mAccounts);
      return;
      label73:
      paramBundle.putBoolean("selectedAddAccount", false);
      paramBundle.putString("selectedAccountName", ((Account)this.mAccounts.get(this.mSelectedItemIndex)).name);
    }
  }
  
  public void run(AccountManagerFuture<Bundle> paramAccountManagerFuture)
  {
    try
    {
      paramAccountManagerFuture = (Intent)((Bundle)paramAccountManagerFuture.getResult()).getParcelable("intent");
      if (paramAccountManagerFuture != null)
      {
        this.mPendingRequest = 2;
        this.mExistingAccounts = AccountManager.get(this).getAccountsForPackage(this.mCallingPackage, this.mCallingUid);
        paramAccountManagerFuture.setFlags(paramAccountManagerFuture.getFlags() & 0xEFFFFFFF);
        startActivityForResult(paramAccountManagerFuture, 2);
        return;
      }
    }
    catch (OperationCanceledException paramAccountManagerFuture)
    {
      setResult(0);
      finish();
      return;
    }
    catch (IOException paramAccountManagerFuture)
    {
      paramAccountManagerFuture = new Bundle();
      paramAccountManagerFuture.putString("errorMessage", "error communicating with server");
      setResult(-1, new Intent().putExtras(paramAccountManagerFuture));
      finish();
      return;
    }
    catch (AuthenticatorException paramAccountManagerFuture)
    {
      for (;;) {}
    }
  }
  
  protected void runAddAccountForAuthenticator(String paramString)
  {
    if (Log.isLoggable("AccountChooser", 2)) {
      Log.v("AccountChooser", "runAddAccountForAuthenticator: " + paramString);
    }
    Bundle localBundle = getIntent().getBundleExtra("addAccountOptions");
    String[] arrayOfString = getIntent().getStringArrayExtra("addAccountRequiredFeatures");
    String str = getIntent().getStringExtra("authTokenType");
    AccountManager.get(this).addAccount(paramString, str, arrayOfString, localBundle, null, this, null);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/accounts/ChooseTypeAndAccountActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */