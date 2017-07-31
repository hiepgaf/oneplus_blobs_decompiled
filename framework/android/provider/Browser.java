package android.provider;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.SeempLog;
import android.webkit.WebIconDatabase.IconListener;

public class Browser
{
  public static final Uri BOOKMARKS_URI = Uri.parse("content://browser/bookmarks");
  public static final String EXTRA_APPLICATION_ID = "com.android.browser.application_id";
  public static final String EXTRA_CREATE_NEW_TAB = "create_new_tab";
  public static final String EXTRA_HEADERS = "com.android.browser.headers";
  public static final String EXTRA_SHARE_FAVICON = "share_favicon";
  public static final String EXTRA_SHARE_SCREENSHOT = "share_screenshot";
  public static final String[] HISTORY_PROJECTION = { "_id", "url", "visits", "date", "bookmark", "title", "favicon", "thumbnail", "touch_icon", "user_entered" };
  public static final int HISTORY_PROJECTION_BOOKMARK_INDEX = 4;
  public static final int HISTORY_PROJECTION_DATE_INDEX = 3;
  public static final int HISTORY_PROJECTION_FAVICON_INDEX = 6;
  public static final int HISTORY_PROJECTION_ID_INDEX = 0;
  public static final int HISTORY_PROJECTION_THUMBNAIL_INDEX = 7;
  public static final int HISTORY_PROJECTION_TITLE_INDEX = 5;
  public static final int HISTORY_PROJECTION_TOUCH_ICON_INDEX = 8;
  public static final int HISTORY_PROJECTION_URL_INDEX = 1;
  public static final int HISTORY_PROJECTION_VISITS_INDEX = 2;
  public static final String INITIAL_ZOOM_LEVEL = "browser.initialZoomLevel";
  private static final String LOGTAG = "browser";
  private static final int MAX_HISTORY_COUNT = 250;
  public static final String[] SEARCHES_PROJECTION = { "_id", "search", "date" };
  public static final int SEARCHES_PROJECTION_DATE_INDEX = 2;
  public static final int SEARCHES_PROJECTION_SEARCH_INDEX = 1;
  public static final Uri SEARCHES_URI;
  public static final String[] TRUNCATE_HISTORY_PROJECTION = { "_id", "date" };
  public static final int TRUNCATE_HISTORY_PROJECTION_ID_INDEX = 0;
  public static final int TRUNCATE_N_OLDEST = 5;
  
  static
  {
    SEARCHES_URI = Uri.parse("content://browser/searches");
  }
  
  private static final void addOrUrlEquals(StringBuilder paramStringBuilder)
  {
    paramStringBuilder.append(" OR url = ");
  }
  
  public static final void addSearchUrl(ContentResolver paramContentResolver, String paramString) {}
  
  public static final boolean canClearHistory(ContentResolver paramContentResolver)
  {
    return false;
  }
  
  public static final void clearHistory(ContentResolver paramContentResolver)
  {
    SeempLog.record(37);
  }
  
  public static final void clearSearches(ContentResolver paramContentResolver) {}
  
  public static final void deleteFromHistory(ContentResolver paramContentResolver, String paramString) {}
  
  public static final void deleteHistoryTimeFrame(ContentResolver paramContentResolver, long paramLong1, long paramLong2) {}
  
  public static final Cursor getAllBookmarks(ContentResolver paramContentResolver)
    throws IllegalStateException
  {
    SeempLog.record(32);
    return new MatrixCursor(new String[] { "url" }, 0);
  }
  
  public static final Cursor getAllVisitedUrls(ContentResolver paramContentResolver)
    throws IllegalStateException
  {
    SeempLog.record(33);
    return new MatrixCursor(new String[] { "url" }, 0);
  }
  
  @Deprecated
  public static final String[] getVisitedHistory(ContentResolver paramContentResolver)
  {
    SeempLog.record(35);
    return new String[0];
  }
  
  private static final Cursor getVisitedLike(ContentResolver paramContentResolver, String paramString)
  {
    SeempLog.record(34);
    int i = 0;
    Object localObject = paramString;
    if (paramString.startsWith("http://"))
    {
      localObject = paramString.substring(7);
      paramString = (String)localObject;
      if (((String)localObject).startsWith("www.")) {
        paramString = ((String)localObject).substring(4);
      }
      if (i == 0) {
        break label162;
      }
      localObject = new StringBuilder("url = ");
      DatabaseUtils.appendEscapedSQLString((StringBuilder)localObject, "https://" + paramString);
      addOrUrlEquals((StringBuilder)localObject);
      DatabaseUtils.appendEscapedSQLString((StringBuilder)localObject, "https://www." + paramString);
    }
    for (paramString = (String)localObject;; paramString = (String)localObject)
    {
      localObject = BrowserContract.History.CONTENT_URI;
      paramString = paramString.toString();
      return paramContentResolver.query((Uri)localObject, new String[] { "_id", "visits" }, paramString, null, null);
      if (!paramString.startsWith("https://")) {
        break;
      }
      localObject = paramString.substring(8);
      i = 1;
      break;
      label162:
      localObject = new StringBuilder("url = ");
      DatabaseUtils.appendEscapedSQLString((StringBuilder)localObject, paramString);
      addOrUrlEquals((StringBuilder)localObject);
      String str = "www." + paramString;
      DatabaseUtils.appendEscapedSQLString((StringBuilder)localObject, str);
      addOrUrlEquals((StringBuilder)localObject);
      DatabaseUtils.appendEscapedSQLString((StringBuilder)localObject, "http://" + paramString);
      addOrUrlEquals((StringBuilder)localObject);
      DatabaseUtils.appendEscapedSQLString((StringBuilder)localObject, "http://" + str);
    }
  }
  
  public static final void requestAllIcons(ContentResolver paramContentResolver, String paramString, WebIconDatabase.IconListener paramIconListener)
  {
    SeempLog.record(36);
  }
  
  public static final void saveBookmark(Context paramContext, String paramString1, String paramString2) {}
  
  public static final void sendString(Context paramContext, String paramString)
  {
    sendString(paramContext, paramString, paramContext.getString(17040343));
  }
  
  public static final void sendString(Context paramContext, String paramString1, String paramString2)
  {
    Intent localIntent = new Intent("android.intent.action.SEND");
    localIntent.setType("text/plain");
    localIntent.putExtra("android.intent.extra.TEXT", paramString1);
    try
    {
      paramString1 = Intent.createChooser(localIntent, paramString2);
      paramString1.setFlags(268435456);
      paramContext.startActivity(paramString1);
      return;
    }
    catch (ActivityNotFoundException paramContext) {}
  }
  
  public static final void truncateHistory(ContentResolver paramContentResolver) {}
  
  public static final void updateVisitedHistory(ContentResolver paramContentResolver, String paramString, boolean paramBoolean) {}
  
  public static class BookmarkColumns
    implements BaseColumns
  {
    public static final String BOOKMARK = "bookmark";
    public static final String CREATED = "created";
    public static final String DATE = "date";
    public static final String FAVICON = "favicon";
    public static final String THUMBNAIL = "thumbnail";
    public static final String TITLE = "title";
    public static final String TOUCH_ICON = "touch_icon";
    public static final String URL = "url";
    public static final String USER_ENTERED = "user_entered";
    public static final String VISITS = "visits";
  }
  
  public static class SearchColumns
    implements BaseColumns
  {
    public static final String DATE = "date";
    public static final String SEARCH = "search";
    @Deprecated
    public static final String URL = "url";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/provider/Browser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */