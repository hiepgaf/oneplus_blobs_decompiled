package android.accounts;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.os.BaseBundle;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.HashMap;

public class ChooseAccountActivity
  extends Activity
{
  private static final String TAG = "AccountManager";
  private AccountManagerResponse mAccountManagerResponse = null;
  private Parcelable[] mAccounts = null;
  private Bundle mResult;
  private HashMap<String, AuthenticatorDescription> mTypeToAuthDescription = new HashMap();
  
  private void getAuthDescriptions()
  {
    AuthenticatorDescription[] arrayOfAuthenticatorDescription = AccountManager.get(this).getAuthenticatorTypes();
    int i = 0;
    int j = arrayOfAuthenticatorDescription.length;
    while (i < j)
    {
      AuthenticatorDescription localAuthenticatorDescription = arrayOfAuthenticatorDescription[i];
      this.mTypeToAuthDescription.put(localAuthenticatorDescription.type, localAuthenticatorDescription);
      i += 1;
    }
  }
  
  private Drawable getDrawableForType(String paramString)
  {
    Object localObject4 = null;
    Object localObject1 = localObject4;
    if (this.mTypeToAuthDescription.containsKey(paramString)) {}
    try
    {
      localObject1 = (AuthenticatorDescription)this.mTypeToAuthDescription.get(paramString);
      localObject1 = createPackageContext(((AuthenticatorDescription)localObject1).packageName, 0).getDrawable(((AuthenticatorDescription)localObject1).iconId);
      return (Drawable)localObject1;
    }
    catch (Resources.NotFoundException localNotFoundException)
    {
      do
      {
        Object localObject2 = localObject4;
      } while (!Log.isLoggable("AccountManager", 5));
      Log.w("AccountManager", "No icon resource for account type " + paramString);
      return null;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      do
      {
        Object localObject3 = localObject4;
      } while (!Log.isLoggable("AccountManager", 5));
      Log.w("AccountManager", "No icon name for account type " + paramString);
    }
    return null;
  }
  
  public void finish()
  {
    if (this.mAccountManagerResponse != null)
    {
      if (this.mResult == null) {
        break label30;
      }
      this.mAccountManagerResponse.onResult(this.mResult);
    }
    for (;;)
    {
      super.finish();
      return;
      label30:
      this.mAccountManagerResponse.onError(4, "canceled");
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mAccounts = getIntent().getParcelableArrayExtra("accounts");
    this.mAccountManagerResponse = ((AccountManagerResponse)getIntent().getParcelableExtra("accountManagerResponse"));
    if (this.mAccounts == null)
    {
      setResult(0);
      finish();
      return;
    }
    getAuthDescriptions();
    paramBundle = new AccountInfo[this.mAccounts.length];
    int i = 0;
    while (i < this.mAccounts.length)
    {
      paramBundle[i] = new AccountInfo(((Account)this.mAccounts[i]).name, getDrawableForType(((Account)this.mAccounts[i]).type));
      i += 1;
    }
    setContentView(17367104);
    ListView localListView = (ListView)findViewById(16908298);
    localListView.setAdapter(new AccountArrayAdapter(this, 17367043, paramBundle));
    localListView.setChoiceMode(1);
    localListView.setTextFilterEnabled(true);
    localListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        ChooseAccountActivity.this.onListItemClick((ListView)paramAnonymousAdapterView, paramAnonymousView, paramAnonymousInt, paramAnonymousLong);
      }
    });
  }
  
  protected void onListItemClick(ListView paramListView, View paramView, int paramInt, long paramLong)
  {
    paramListView = (Account)this.mAccounts[paramInt];
    Log.d("AccountManager", "selected account " + paramListView);
    paramView = new Bundle();
    paramView.putString("authAccount", paramListView.name);
    paramView.putString("accountType", paramListView.type);
    this.mResult = paramView;
    finish();
  }
  
  private static class AccountArrayAdapter
    extends ArrayAdapter<ChooseAccountActivity.AccountInfo>
  {
    private ChooseAccountActivity.AccountInfo[] mInfos;
    private LayoutInflater mLayoutInflater;
    
    public AccountArrayAdapter(Context paramContext, int paramInt, ChooseAccountActivity.AccountInfo[] paramArrayOfAccountInfo)
    {
      super(paramInt, paramArrayOfAccountInfo);
      this.mInfos = paramArrayOfAccountInfo;
      this.mLayoutInflater = ((LayoutInflater)paramContext.getSystemService("layout_inflater"));
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      if (paramView == null)
      {
        paramView = this.mLayoutInflater.inflate(17367105, null);
        paramViewGroup = new ChooseAccountActivity.ViewHolder(null);
        paramViewGroup.text = ((TextView)paramView.findViewById(16909134));
        paramViewGroup.icon = ((ImageView)paramView.findViewById(16909133));
        paramView.setTag(paramViewGroup);
      }
      for (;;)
      {
        paramViewGroup.text.setText(this.mInfos[paramInt].name);
        paramViewGroup.icon.setImageDrawable(this.mInfos[paramInt].drawable);
        return paramView;
        paramViewGroup = (ChooseAccountActivity.ViewHolder)paramView.getTag();
      }
    }
  }
  
  private static class AccountInfo
  {
    final Drawable drawable;
    final String name;
    
    AccountInfo(String paramString, Drawable paramDrawable)
    {
      this.name = paramString;
      this.drawable = paramDrawable;
    }
  }
  
  private static class ViewHolder
  {
    ImageView icon;
    TextView text;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/accounts/ChooseAccountActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */