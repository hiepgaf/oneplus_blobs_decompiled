package android.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;

public abstract class SearchIndexablesProvider
  extends ContentProvider
{
  private static final int MATCH_NON_INDEXABLE_KEYS_CODE = 3;
  private static final int MATCH_RAW_CODE = 2;
  private static final int MATCH_RES_CODE = 1;
  private static final String TAG = "IndexablesProvider";
  private String mAuthority;
  private UriMatcher mMatcher;
  
  public void attachInfo(Context paramContext, ProviderInfo paramProviderInfo)
  {
    this.mAuthority = paramProviderInfo.authority;
    this.mMatcher = new UriMatcher(-1);
    this.mMatcher.addURI(this.mAuthority, "settings/indexables_xml_res", 1);
    this.mMatcher.addURI(this.mAuthority, "settings/indexables_raw", 2);
    this.mMatcher.addURI(this.mAuthority, "settings/non_indexables_key", 3);
    if (!paramProviderInfo.exported) {
      throw new SecurityException("Provider must be exported");
    }
    if (!paramProviderInfo.grantUriPermissions) {
      throw new SecurityException("Provider must grantUriPermissions");
    }
    if (!"android.permission.READ_SEARCH_INDEXABLES".equals(paramProviderInfo.readPermission)) {
      throw new SecurityException("Provider must be protected by READ_SEARCH_INDEXABLES");
    }
    super.attachInfo(paramContext, paramProviderInfo);
  }
  
  public final int delete(Uri paramUri, String paramString, String[] paramArrayOfString)
  {
    throw new UnsupportedOperationException("Delete not supported");
  }
  
  public String getType(Uri paramUri)
  {
    switch (this.mMatcher.match(paramUri))
    {
    default: 
      throw new IllegalArgumentException("Unknown URI " + paramUri);
    case 1: 
      return "vnd.android.cursor.dir/indexables_xml_res";
    case 2: 
      return "vnd.android.cursor.dir/indexables_raw";
    }
    return "vnd.android.cursor.dir/non_indexables_key";
  }
  
  public final Uri insert(Uri paramUri, ContentValues paramContentValues)
  {
    throw new UnsupportedOperationException("Insert not supported");
  }
  
  public Cursor query(Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2)
  {
    switch (this.mMatcher.match(paramUri))
    {
    default: 
      throw new UnsupportedOperationException("Unknown Uri " + paramUri);
    case 1: 
      return queryXmlResources(null);
    case 2: 
      return queryRawData(null);
    }
    return queryNonIndexableKeys(null);
  }
  
  public abstract Cursor queryNonIndexableKeys(String[] paramArrayOfString);
  
  public abstract Cursor queryRawData(String[] paramArrayOfString);
  
  public abstract Cursor queryXmlResources(String[] paramArrayOfString);
  
  public final int update(Uri paramUri, ContentValues paramContentValues, String paramString, String[] paramArrayOfString)
  {
    throw new UnsupportedOperationException("Update not supported");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/provider/SearchIndexablesProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */