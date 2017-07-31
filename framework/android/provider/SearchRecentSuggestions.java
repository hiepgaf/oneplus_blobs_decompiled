package android.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import java.util.concurrent.Semaphore;

public class SearchRecentSuggestions
{
  private static final String LOG_TAG = "SearchSuggestions";
  private static final int MAX_HISTORY_COUNT = 250;
  public static final String[] QUERIES_PROJECTION_1LINE = { "_id", "date", "query", "display1" };
  public static final String[] QUERIES_PROJECTION_2LINE = { "_id", "date", "query", "display1", "display2" };
  public static final int QUERIES_PROJECTION_DATE_INDEX = 1;
  public static final int QUERIES_PROJECTION_DISPLAY1_INDEX = 3;
  public static final int QUERIES_PROJECTION_DISPLAY2_INDEX = 4;
  public static final int QUERIES_PROJECTION_QUERY_INDEX = 2;
  private static final Semaphore sWritesInProgress = new Semaphore(0);
  private final String mAuthority;
  private final Context mContext;
  private final Uri mSuggestionsUri;
  private final boolean mTwoLineDisplay;
  
  public SearchRecentSuggestions(Context paramContext, String paramString, int paramInt)
  {
    if ((TextUtils.isEmpty(paramString)) || ((paramInt & 0x1) == 0)) {
      throw new IllegalArgumentException();
    }
    if ((paramInt & 0x2) != 0) {
      bool = true;
    }
    this.mTwoLineDisplay = bool;
    this.mContext = paramContext;
    this.mAuthority = new String(paramString);
    this.mSuggestionsUri = Uri.parse("content://" + this.mAuthority + "/suggestions");
  }
  
  private void saveRecentQueryBlocking(String paramString1, String paramString2)
  {
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    long l = System.currentTimeMillis();
    try
    {
      ContentValues localContentValues = new ContentValues();
      localContentValues.put("display1", paramString1);
      if (this.mTwoLineDisplay) {
        localContentValues.put("display2", paramString2);
      }
      localContentValues.put("query", paramString1);
      localContentValues.put("date", Long.valueOf(l));
      localContentResolver.insert(this.mSuggestionsUri, localContentValues);
    }
    catch (RuntimeException paramString1)
    {
      for (;;)
      {
        Log.e("SearchSuggestions", "saveRecentQuery", paramString1);
      }
    }
    truncateHistory(localContentResolver, 250);
  }
  
  public void clearHistory()
  {
    truncateHistory(this.mContext.getContentResolver(), 0);
  }
  
  public void saveRecentQuery(final String paramString1, final String paramString2)
  {
    if (TextUtils.isEmpty(paramString1)) {
      return;
    }
    if ((this.mTwoLineDisplay) || (TextUtils.isEmpty(paramString2)))
    {
      new Thread("saveRecentQuery")
      {
        public void run()
        {
          SearchRecentSuggestions.-wrap0(SearchRecentSuggestions.this, paramString1, paramString2);
          SearchRecentSuggestions.-get0().release();
        }
      }.start();
      return;
    }
    throw new IllegalArgumentException();
  }
  
  protected void truncateHistory(ContentResolver paramContentResolver, int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException();
    }
    String str = null;
    if (paramInt > 0) {}
    try
    {
      str = "_id IN (SELECT _id FROM suggestions ORDER BY date DESC LIMIT -1 OFFSET " + String.valueOf(paramInt) + ")";
      paramContentResolver.delete(this.mSuggestionsUri, str, null);
      return;
    }
    catch (RuntimeException paramContentResolver)
    {
      Log.e("SearchSuggestions", "truncateHistory", paramContentResolver);
    }
  }
  
  void waitForSave()
  {
    do
    {
      sWritesInProgress.acquireUninterruptibly();
    } while (sWritesInProgress.availablePermits() > 0);
  }
  
  private static class SuggestionColumns
    implements BaseColumns
  {
    public static final String DATE = "date";
    public static final String DISPLAY1 = "display1";
    public static final String DISPLAY2 = "display2";
    public static final String QUERY = "query";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/provider/SearchRecentSuggestions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */