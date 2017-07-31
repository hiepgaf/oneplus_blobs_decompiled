package android.accounts;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.os.BaseBundle;
import android.os.Bundle;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class ChooseAccountTypeActivity
  extends Activity
{
  private static final String TAG = "AccountChooser";
  private ArrayList<AuthInfo> mAuthenticatorInfosToDisplay;
  private HashMap<String, AuthInfo> mTypeToAuthenticatorInfo = new HashMap();
  
  private void buildTypeToAuthDescriptionMap()
  {
    int i = 0;
    AuthenticatorDescription[] arrayOfAuthenticatorDescription = AccountManager.get(this).getAuthenticatorTypes();
    int j = arrayOfAuthenticatorDescription.length;
    AuthenticatorDescription localAuthenticatorDescription;
    Object localObject6;
    Object localObject5;
    Object localObject2;
    Object localObject3;
    Object localObject4;
    while (i < j)
    {
      localAuthenticatorDescription = arrayOfAuthenticatorDescription[i];
      localObject6 = null;
      Object localObject7 = null;
      Object localObject8 = null;
      CharSequence localCharSequence = null;
      localObject5 = null;
      localObject2 = localObject5;
      localObject3 = localObject6;
      localObject4 = localCharSequence;
      Object localObject1 = localObject7;
      try
      {
        Context localContext = createPackageContext(localAuthenticatorDescription.packageName, 0);
        localObject2 = localObject5;
        localObject3 = localObject6;
        localObject4 = localCharSequence;
        localObject1 = localObject7;
        localObject5 = localContext.getDrawable(localAuthenticatorDescription.iconId);
        localObject2 = localObject5;
        localObject3 = localObject6;
        localObject4 = localObject5;
        localObject1 = localObject7;
        localCharSequence = localContext.getResources().getText(localAuthenticatorDescription.labelId);
        localObject1 = localObject8;
        if (localCharSequence != null)
        {
          localObject2 = localObject5;
          localObject3 = localObject6;
          localObject4 = localObject5;
          localObject1 = localObject7;
          localObject6 = localCharSequence.toString();
          localObject1 = localObject6;
        }
        localObject2 = localObject5;
        localObject3 = localObject1;
        localObject4 = localObject5;
        localObject6 = localCharSequence.toString();
      }
      catch (Resources.NotFoundException localNotFoundException)
      {
        for (;;)
        {
          localObject5 = localObject2;
          localObject6 = localObject3;
          if (Log.isLoggable("AccountChooser", 5))
          {
            Log.w("AccountChooser", "No icon resource for account type " + localAuthenticatorDescription.type);
            localObject5 = localObject2;
            localObject6 = localObject3;
          }
        }
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        for (;;)
        {
          localObject5 = localObject4;
          localObject6 = localNotFoundException;
          if (Log.isLoggable("AccountChooser", 5))
          {
            Log.w("AccountChooser", "No icon name for account type " + localAuthenticatorDescription.type);
            localObject5 = localObject4;
            localObject6 = localNotFoundException;
          }
        }
      }
      localObject1 = new AuthInfo(localAuthenticatorDescription, (String)localObject6, (Drawable)localObject5);
      this.mTypeToAuthenticatorInfo.put(localAuthenticatorDescription.type, localObject1);
      i += 1;
    }
  }
  
  private void setResultAndFinish(String paramString)
  {
    Bundle localBundle = new Bundle();
    localBundle.putString("accountType", paramString);
    setResult(-1, new Intent().putExtras(localBundle));
    if (Log.isLoggable("AccountChooser", 2)) {
      Log.v("AccountChooser", "ChooseAccountTypeActivity.setResultAndFinish: selected account type " + paramString);
    }
    finish();
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (Log.isLoggable("AccountChooser", 2)) {
      Log.v("AccountChooser", "ChooseAccountTypeActivity.onCreate(savedInstanceState=" + paramBundle + ")");
    }
    paramBundle = null;
    Object localObject2 = getIntent().getStringArrayExtra("allowableAccountTypes");
    if (localObject2 != null)
    {
      localObject1 = new HashSet(localObject2.length);
      int j = localObject2.length;
      int i = 0;
      for (;;)
      {
        paramBundle = (Bundle)localObject1;
        if (i >= j) {
          break;
        }
        ((Set)localObject1).add(localObject2[i]);
        i += 1;
      }
    }
    buildTypeToAuthDescriptionMap();
    this.mAuthenticatorInfosToDisplay = new ArrayList(this.mTypeToAuthenticatorInfo.size());
    Object localObject1 = this.mTypeToAuthenticatorInfo.entrySet().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      Object localObject3 = (Map.Entry)((Iterator)localObject1).next();
      localObject2 = (String)((Map.Entry)localObject3).getKey();
      localObject3 = (AuthInfo)((Map.Entry)localObject3).getValue();
      if ((paramBundle == null) || (paramBundle.contains(localObject2))) {
        this.mAuthenticatorInfosToDisplay.add(localObject3);
      }
    }
    if (this.mAuthenticatorInfosToDisplay.isEmpty())
    {
      paramBundle = new Bundle();
      paramBundle.putString("errorMessage", "no allowable account types");
      setResult(-1, new Intent().putExtras(paramBundle));
      finish();
      return;
    }
    if (this.mAuthenticatorInfosToDisplay.size() == 1)
    {
      setResultAndFinish(((AuthInfo)this.mAuthenticatorInfosToDisplay.get(0)).desc.type);
      return;
    }
    setContentView(17367106);
    paramBundle = (ListView)findViewById(16908298);
    paramBundle.setAdapter(new AccountArrayAdapter(this, 17367043, this.mAuthenticatorInfosToDisplay));
    paramBundle.setChoiceMode(0);
    paramBundle.setTextFilterEnabled(false);
    paramBundle.setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        ChooseAccountTypeActivity.-wrap0(ChooseAccountTypeActivity.this, ((ChooseAccountTypeActivity.AuthInfo)ChooseAccountTypeActivity.-get0(ChooseAccountTypeActivity.this).get(paramAnonymousInt)).desc.type);
      }
    });
  }
  
  private static class AccountArrayAdapter
    extends ArrayAdapter<ChooseAccountTypeActivity.AuthInfo>
  {
    private ArrayList<ChooseAccountTypeActivity.AuthInfo> mInfos;
    private LayoutInflater mLayoutInflater;
    
    public AccountArrayAdapter(Context paramContext, int paramInt, ArrayList<ChooseAccountTypeActivity.AuthInfo> paramArrayList)
    {
      super(paramInt, paramArrayList);
      this.mInfos = paramArrayList;
      this.mLayoutInflater = ((LayoutInflater)paramContext.getSystemService("layout_inflater"));
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      if (paramView == null)
      {
        paramView = this.mLayoutInflater.inflate(17367105, null);
        paramViewGroup = new ChooseAccountTypeActivity.ViewHolder(null);
        paramViewGroup.text = ((TextView)paramView.findViewById(16909134));
        paramViewGroup.icon = ((ImageView)paramView.findViewById(16909133));
        paramView.setTag(paramViewGroup);
      }
      for (;;)
      {
        paramViewGroup.text.setText(((ChooseAccountTypeActivity.AuthInfo)this.mInfos.get(paramInt)).name);
        paramViewGroup.icon.setImageDrawable(((ChooseAccountTypeActivity.AuthInfo)this.mInfos.get(paramInt)).drawable);
        return paramView;
        paramViewGroup = (ChooseAccountTypeActivity.ViewHolder)paramView.getTag();
      }
    }
  }
  
  private static class AuthInfo
  {
    final AuthenticatorDescription desc;
    final Drawable drawable;
    final String name;
    
    AuthInfo(AuthenticatorDescription paramAuthenticatorDescription, String paramString, Drawable paramDrawable)
    {
      this.desc = paramAuthenticatorDescription;
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/accounts/ChooseAccountTypeActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */