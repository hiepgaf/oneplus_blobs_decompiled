package android.accounts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class AccountAuthenticatorActivity
  extends Activity
{
  private AccountAuthenticatorResponse mAccountAuthenticatorResponse = null;
  private Bundle mResultBundle = null;
  
  public void finish()
  {
    if (this.mAccountAuthenticatorResponse != null)
    {
      if (this.mResultBundle == null) {
        break label35;
      }
      this.mAccountAuthenticatorResponse.onResult(this.mResultBundle);
    }
    for (;;)
    {
      this.mAccountAuthenticatorResponse = null;
      super.finish();
      return;
      label35:
      this.mAccountAuthenticatorResponse.onError(4, "canceled");
    }
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mAccountAuthenticatorResponse = ((AccountAuthenticatorResponse)getIntent().getParcelableExtra("accountAuthenticatorResponse"));
    if (this.mAccountAuthenticatorResponse != null) {
      this.mAccountAuthenticatorResponse.onRequestContinued();
    }
  }
  
  public final void setAccountAuthenticatorResult(Bundle paramBundle)
  {
    this.mResultBundle = paramBundle;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/accounts/AccountAuthenticatorActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */