package com.android.server.accounts;

import android.accounts.AuthenticatorDescription;
import android.content.Context;
import android.content.pm.RegisteredServicesCache;
import android.content.pm.XmlSerializerAndParser;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import com.android.internal.R.styleable;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

class AccountAuthenticatorCache
  extends RegisteredServicesCache<AuthenticatorDescription>
  implements IAccountAuthenticatorCache
{
  private static final String TAG = "Account";
  private static final MySerializer sSerializer = new MySerializer(null);
  
  public AccountAuthenticatorCache(Context paramContext)
  {
    super(paramContext, "android.accounts.AccountAuthenticator", "android.accounts.AccountAuthenticator", "account-authenticator", sSerializer);
  }
  
  public AuthenticatorDescription parseServiceAttributes(Resources paramResources, String paramString, AttributeSet paramAttributeSet)
  {
    paramResources = paramResources.obtainAttributes(paramAttributeSet, R.styleable.AccountAuthenticator);
    try
    {
      paramAttributeSet = paramResources.getString(2);
      int i = paramResources.getResourceId(0, 0);
      int j = paramResources.getResourceId(1, 0);
      int k = paramResources.getResourceId(3, 0);
      int m = paramResources.getResourceId(4, 0);
      boolean bool1 = paramResources.getBoolean(5, false);
      boolean bool2 = TextUtils.isEmpty(paramAttributeSet);
      if (bool2) {
        return null;
      }
      paramString = new AuthenticatorDescription(paramAttributeSet, paramString, i, j, k, m, bool1);
      return paramString;
    }
    finally
    {
      paramResources.recycle();
    }
  }
  
  private static class MySerializer
    implements XmlSerializerAndParser<AuthenticatorDescription>
  {
    public AuthenticatorDescription createFromXml(XmlPullParser paramXmlPullParser)
      throws IOException, XmlPullParserException
    {
      return AuthenticatorDescription.newKey(paramXmlPullParser.getAttributeValue(null, "type"));
    }
    
    public void writeAsXml(AuthenticatorDescription paramAuthenticatorDescription, XmlSerializer paramXmlSerializer)
      throws IOException
    {
      paramXmlSerializer.attribute(null, "type", paramAuthenticatorDescription.type);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/accounts/AccountAuthenticatorCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */