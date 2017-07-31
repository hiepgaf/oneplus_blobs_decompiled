package android.accounts;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.os.BaseBundle;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.IOException;

public class GrantCredentialsPermissionActivity
  extends Activity
  implements View.OnClickListener
{
  public static final String EXTRAS_ACCOUNT = "account";
  public static final String EXTRAS_AUTH_TOKEN_TYPE = "authTokenType";
  public static final String EXTRAS_REQUESTING_UID = "uid";
  public static final String EXTRAS_RESPONSE = "response";
  private Account mAccount;
  private String mAuthTokenType;
  protected LayoutInflater mInflater;
  private Bundle mResultBundle = null;
  private int mUid;
  
  private String getAccountLabel(Account paramAccount)
  {
    Object localObject1 = AccountManager.get(this).getAuthenticatorTypes();
    int i = 0;
    int j = localObject1.length;
    while (i < j)
    {
      Object localObject2 = localObject1[i];
      if (((AuthenticatorDescription)localObject2).type.equals(paramAccount.type)) {
        try
        {
          localObject1 = createPackageContext(((AuthenticatorDescription)localObject2).packageName, 0).getString(((AuthenticatorDescription)localObject2).labelId);
          return (String)localObject1;
        }
        catch (Resources.NotFoundException localNotFoundException)
        {
          return paramAccount.type;
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {
          return paramAccount.type;
        }
      }
      i += 1;
    }
    return paramAccount.type;
  }
  
  private View newPackageView(String paramString)
  {
    View localView = this.mInflater.inflate(17367201, null);
    ((TextView)localView.findViewById(16909267)).setText(paramString);
    return localView;
  }
  
  public void finish()
  {
    AccountAuthenticatorResponse localAccountAuthenticatorResponse = (AccountAuthenticatorResponse)getIntent().getParcelableExtra("response");
    if (localAccountAuthenticatorResponse != null)
    {
      if (this.mResultBundle == null) {
        break label37;
      }
      localAccountAuthenticatorResponse.onResult(this.mResultBundle);
    }
    for (;;)
    {
      super.finish();
      return;
      label37:
      localAccountAuthenticatorResponse.onError(4, "canceled");
    }
  }
  
  public void onClick(View paramView)
  {
    switch (paramView.getId())
    {
    }
    for (;;)
    {
      finish();
      return;
      AccountManager.get(this).updateAppPermission(this.mAccount, this.mAuthTokenType, this.mUid, true);
      paramView = new Intent();
      paramView.putExtra("retry", true);
      setResult(-1, paramView);
      setAccountAuthenticatorResult(paramView.getExtras());
      continue;
      AccountManager.get(this).updateAppPermission(this.mAccount, this.mAuthTokenType, this.mUid, false);
      setResult(0);
    }
  }
  
  protected void onCreate(final Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(17367144);
    setTitle(17040505);
    this.mInflater = ((LayoutInflater)getSystemService("layout_inflater"));
    paramBundle = getIntent().getExtras();
    if (paramBundle == null)
    {
      setResult(0);
      finish();
      return;
    }
    this.mAccount = ((Account)paramBundle.getParcelable("account"));
    this.mAuthTokenType = paramBundle.getString("authTokenType");
    this.mUid = paramBundle.getInt("uid");
    PackageManager localPackageManager = getPackageManager();
    String[] arrayOfString = localPackageManager.getPackagesForUid(this.mUid);
    if ((this.mAccount == null) || (this.mAuthTokenType == null)) {}
    while (arrayOfString == null)
    {
      setResult(0);
      finish();
      return;
    }
    String str2;
    for (;;)
    {
      try
      {
        str2 = getAccountLabel(this.mAccount);
        paramBundle = (TextView)findViewById(16909171);
        paramBundle.setVisibility(8);
        paramBundle = new AccountManagerCallback()
        {
          public void run(final AccountManagerFuture<String> paramAnonymousAccountManagerFuture)
          {
            try
            {
              paramAnonymousAccountManagerFuture = (String)paramAnonymousAccountManagerFuture.getResult();
              if (!TextUtils.isEmpty(paramAnonymousAccountManagerFuture)) {
                GrantCredentialsPermissionActivity.this.runOnUiThread(new Runnable()
                {
                  public void run()
                  {
                    if (!GrantCredentialsPermissionActivity.this.isFinishing())
                    {
                      this.val$authTokenTypeView.setText(paramAnonymousAccountManagerFuture);
                      this.val$authTokenTypeView.setVisibility(0);
                    }
                  }
                });
              }
              return;
            }
            catch (OperationCanceledException paramAnonymousAccountManagerFuture) {}catch (IOException paramAnonymousAccountManagerFuture) {}catch (AuthenticatorException paramAnonymousAccountManagerFuture) {}
          }
        };
        if (!"com.android.AccountManager.ACCOUNT_ACCESS_TOKEN_TYPE".equals(this.mAuthTokenType)) {
          AccountManager.get(this).getAuthTokenLabel(this.mAccount.type, this.mAuthTokenType, paramBundle, null);
        }
        findViewById(16909175).setOnClickListener(this);
        findViewById(16909174).setOnClickListener(this);
        localLinearLayout = (LinearLayout)findViewById(16909167);
        i = 0;
        int j = arrayOfString.length;
        if (i >= j) {
          break label302;
        }
        paramBundle = arrayOfString[i];
      }
      catch (IllegalArgumentException paramBundle)
      {
        LinearLayout localLinearLayout;
        int i;
        String str1;
        setResult(0);
        finish();
        return;
      }
      try
      {
        str1 = localPackageManager.getApplicationLabel(localPackageManager.getApplicationInfo(paramBundle, 0)).toString();
        paramBundle = str1;
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        continue;
      }
      localLinearLayout.addView(newPackageView(paramBundle));
      i += 1;
    }
    label302:
    ((TextView)findViewById(16909170)).setText(this.mAccount.name);
    ((TextView)findViewById(16909169)).setText(str2);
  }
  
  public final void setAccountAuthenticatorResult(Bundle paramBundle)
  {
    this.mResultBundle = paramBundle;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/accounts/GrantCredentialsPermissionActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */