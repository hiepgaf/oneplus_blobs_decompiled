package android.content;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import java.util.List;

public class SearchRecentSuggestionsProvider
  extends ContentProvider
{
  public static final int DATABASE_MODE_2LINES = 2;
  public static final int DATABASE_MODE_QUERIES = 1;
  private static final int DATABASE_VERSION = 512;
  private static final String NULL_COLUMN = "query";
  private static final String ORDER_BY = "date DESC";
  private static final String TAG = "SuggestionsProvider";
  private static final int URI_MATCH_SUGGEST = 1;
  private static final String sDatabaseName = "suggestions.db";
  private static final String sSuggestions = "suggestions";
  private String mAuthority;
  private int mMode;
  private SQLiteOpenHelper mOpenHelper;
  private String mSuggestSuggestionClause;
  private String[] mSuggestionProjection;
  private Uri mSuggestionsUri;
  private boolean mTwoLineDisplay;
  private UriMatcher mUriMatcher;
  
  public int delete(Uri paramUri, String paramString, String[] paramArrayOfString)
  {
    SQLiteDatabase localSQLiteDatabase = this.mOpenHelper.getWritableDatabase();
    if (paramUri.getPathSegments().size() != 1) {
      throw new IllegalArgumentException("Unknown Uri");
    }
    if (((String)paramUri.getPathSegments().get(0)).equals("suggestions"))
    {
      int i = localSQLiteDatabase.delete("suggestions", paramString, paramArrayOfString);
      getContext().getContentResolver().notifyChange(paramUri, null);
      return i;
    }
    throw new IllegalArgumentException("Unknown Uri");
  }
  
  public String getType(Uri paramUri)
  {
    if (this.mUriMatcher.match(paramUri) == 1) {
      return "vnd.android.cursor.dir/vnd.android.search.suggest";
    }
    int i = paramUri.getPathSegments().size();
    if ((i >= 1) && (((String)paramUri.getPathSegments().get(0)).equals("suggestions")))
    {
      if (i == 1) {
        return "vnd.android.cursor.dir/suggestion";
      }
      if (i == 2) {
        return "vnd.android.cursor.item/suggestion";
      }
    }
    throw new IllegalArgumentException("Unknown Uri");
  }
  
  public Uri insert(Uri paramUri, ContentValues paramContentValues)
  {
    SQLiteDatabase localSQLiteDatabase = this.mOpenHelper.getWritableDatabase();
    int i = paramUri.getPathSegments().size();
    if (i < 1) {
      throw new IllegalArgumentException("Unknown Uri");
    }
    long l2 = -1L;
    String str = (String)paramUri.getPathSegments().get(0);
    Object localObject = null;
    paramUri = (Uri)localObject;
    long l1 = l2;
    if (str.equals("suggestions"))
    {
      paramUri = (Uri)localObject;
      l1 = l2;
      if (i == 1)
      {
        l2 = localSQLiteDatabase.insert("suggestions", "query", paramContentValues);
        paramUri = (Uri)localObject;
        l1 = l2;
        if (l2 > 0L)
        {
          paramUri = Uri.withAppendedPath(this.mSuggestionsUri, String.valueOf(l2));
          l1 = l2;
        }
      }
    }
    if (l1 < 0L) {
      throw new IllegalArgumentException("Unknown Uri");
    }
    getContext().getContentResolver().notifyChange(paramUri, null);
    return paramUri;
  }
  
  public boolean onCreate()
  {
    if ((this.mAuthority == null) || (this.mMode == 0)) {
      throw new IllegalArgumentException("Provider not configured");
    }
    int i = this.mMode;
    this.mOpenHelper = new DatabaseHelper(getContext(), i + 512);
    return true;
  }
  
  public Cursor query(Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2)
  {
    SQLiteDatabase localSQLiteDatabase = this.mOpenHelper.getReadableDatabase();
    if (this.mUriMatcher.match(paramUri) == 1)
    {
      if (TextUtils.isEmpty(paramArrayOfString2[0]))
      {
        paramString1 = null;
        paramArrayOfString1 = null;
        paramArrayOfString1 = localSQLiteDatabase.query("suggestions", this.mSuggestionProjection, paramString1, paramArrayOfString1, null, null, "date DESC", null);
        paramArrayOfString1.setNotificationUri(getContext().getContentResolver(), paramUri);
        return paramArrayOfString1;
      }
      paramString1 = "%" + paramArrayOfString2[0] + "%";
      if (this.mTwoLineDisplay)
      {
        paramArrayOfString1 = new String[2];
        paramArrayOfString1[0] = paramString1;
        paramArrayOfString1[1] = paramString1;
      }
      for (;;)
      {
        paramString1 = this.mSuggestSuggestionClause;
        break;
        paramArrayOfString1 = new String[1];
        paramArrayOfString1[0] = paramString1;
      }
    }
    int i = paramUri.getPathSegments().size();
    if ((i != 1) && (i != 2)) {
      throw new IllegalArgumentException("Unknown Uri");
    }
    String str = (String)paramUri.getPathSegments().get(0);
    if (!str.equals("suggestions")) {
      throw new IllegalArgumentException("Unknown Uri");
    }
    Object localObject2 = null;
    Object localObject1 = localObject2;
    if (paramArrayOfString1 != null)
    {
      localObject1 = localObject2;
      if (paramArrayOfString1.length > 0)
      {
        localObject1 = new String[paramArrayOfString1.length + 1];
        System.arraycopy(paramArrayOfString1, 0, localObject1, 0, paramArrayOfString1.length);
        localObject1[paramArrayOfString1.length] = "_id AS _id";
      }
    }
    paramArrayOfString1 = new StringBuilder(256);
    if (i == 2) {
      paramArrayOfString1.append("(_id = ").append((String)paramUri.getPathSegments().get(1)).append(")");
    }
    if ((paramString1 != null) && (paramString1.length() > 0))
    {
      if (paramArrayOfString1.length() > 0) {
        paramArrayOfString1.append(" AND ");
      }
      paramArrayOfString1.append('(');
      paramArrayOfString1.append(paramString1);
      paramArrayOfString1.append(')');
    }
    paramArrayOfString1 = localSQLiteDatabase.query(str, (String[])localObject1, paramArrayOfString1.toString(), paramArrayOfString2, null, null, paramString2, null);
    paramArrayOfString1.setNotificationUri(getContext().getContentResolver(), paramUri);
    return paramArrayOfString1;
  }
  
  protected void setupSuggestions(String paramString, int paramInt)
  {
    if ((TextUtils.isEmpty(paramString)) || ((paramInt & 0x1) == 0)) {
      throw new IllegalArgumentException();
    }
    if ((paramInt & 0x2) != 0) {}
    for (boolean bool = true;; bool = false)
    {
      this.mTwoLineDisplay = bool;
      this.mAuthority = new String(paramString);
      this.mMode = paramInt;
      this.mSuggestionsUri = Uri.parse("content://" + this.mAuthority + "/suggestions");
      this.mUriMatcher = new UriMatcher(-1);
      this.mUriMatcher.addURI(this.mAuthority, "search_suggest_query", 1);
      if (!this.mTwoLineDisplay) {
        break;
      }
      this.mSuggestSuggestionClause = "display1 LIKE ? OR display2 LIKE ?";
      this.mSuggestionProjection = new String[] { "0 AS suggest_format", "'android.resource://system/17301578' AS suggest_icon_1", "display1 AS suggest_text_1", "display2 AS suggest_text_2", "query AS suggest_intent_query", "_id" };
      return;
    }
    this.mSuggestSuggestionClause = "display1 LIKE ?";
    this.mSuggestionProjection = new String[] { "0 AS suggest_format", "'android.resource://system/17301578' AS suggest_icon_1", "display1 AS suggest_text_1", "query AS suggest_intent_query", "_id" };
  }
  
  public int update(Uri paramUri, ContentValues paramContentValues, String paramString, String[] paramArrayOfString)
  {
    throw new UnsupportedOperationException("Not implemented");
  }
  
  private static class DatabaseHelper
    extends SQLiteOpenHelper
  {
    private int mNewVersion;
    
    public DatabaseHelper(Context paramContext, int paramInt)
    {
      super("suggestions.db", null, paramInt);
      this.mNewVersion = paramInt;
    }
    
    public void onCreate(SQLiteDatabase paramSQLiteDatabase)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("CREATE TABLE suggestions (_id INTEGER PRIMARY KEY,display1 TEXT UNIQUE ON CONFLICT REPLACE");
      if ((this.mNewVersion & 0x2) != 0) {
        localStringBuilder.append(",display2 TEXT");
      }
      localStringBuilder.append(",query TEXT,date LONG);");
      paramSQLiteDatabase.execSQL(localStringBuilder.toString());
    }
    
    public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
    {
      Log.w("SuggestionsProvider", "Upgrading database from version " + paramInt1 + " to " + paramInt2 + ", which will destroy all old data");
      paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS suggestions");
      onCreate(paramSQLiteDatabase);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/SearchRecentSuggestionsProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */