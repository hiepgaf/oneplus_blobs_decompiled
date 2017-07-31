package android.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.MiniThumbFile;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public final class MediaStore
{
  public static final String ACTION_IMAGE_CAPTURE = "android.media.action.IMAGE_CAPTURE";
  public static final String ACTION_IMAGE_CAPTURE_SECURE = "android.media.action.IMAGE_CAPTURE_SECURE";
  public static final String ACTION_MTP_SESSION_END = "android.provider.action.MTP_SESSION_END";
  public static final String ACTION_VIDEO_CAPTURE = "android.media.action.VIDEO_CAPTURE";
  public static final String AUTHORITY = "media";
  private static final String CONTENT_AUTHORITY_SLASH = "content://media/";
  public static final String EXTRA_DURATION_LIMIT = "android.intent.extra.durationLimit";
  public static final String EXTRA_FINISH_ON_COMPLETION = "android.intent.extra.finishOnCompletion";
  public static final String EXTRA_FULL_SCREEN = "android.intent.extra.fullScreen";
  public static final String EXTRA_MEDIA_ALBUM = "android.intent.extra.album";
  public static final String EXTRA_MEDIA_ARTIST = "android.intent.extra.artist";
  public static final String EXTRA_MEDIA_FOCUS = "android.intent.extra.focus";
  public static final String EXTRA_MEDIA_GENRE = "android.intent.extra.genre";
  public static final String EXTRA_MEDIA_PLAYLIST = "android.intent.extra.playlist";
  public static final String EXTRA_MEDIA_RADIO_CHANNEL = "android.intent.extra.radio_channel";
  public static final String EXTRA_MEDIA_TITLE = "android.intent.extra.title";
  public static final String EXTRA_OUTPUT = "output";
  public static final String EXTRA_SCREEN_ORIENTATION = "android.intent.extra.screenOrientation";
  public static final String EXTRA_SHOW_ACTION_ICONS = "android.intent.extra.showActionIcons";
  public static final String EXTRA_SIZE_LIMIT = "android.intent.extra.sizeLimit";
  public static final String EXTRA_VIDEO_QUALITY = "android.intent.extra.videoQuality";
  public static final String INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH = "android.media.action.MEDIA_PLAY_FROM_SEARCH";
  public static final String INTENT_ACTION_MEDIA_SEARCH = "android.intent.action.MEDIA_SEARCH";
  @Deprecated
  public static final String INTENT_ACTION_MUSIC_PLAYER = "android.intent.action.MUSIC_PLAYER";
  public static final String INTENT_ACTION_STILL_IMAGE_CAMERA = "android.media.action.STILL_IMAGE_CAMERA";
  public static final String INTENT_ACTION_STILL_IMAGE_CAMERA_SECURE = "android.media.action.STILL_IMAGE_CAMERA_SECURE";
  public static final String INTENT_ACTION_TEXT_OPEN_FROM_SEARCH = "android.media.action.TEXT_OPEN_FROM_SEARCH";
  public static final String INTENT_ACTION_VIDEO_CAMERA = "android.media.action.VIDEO_CAMERA";
  public static final String INTENT_ACTION_VIDEO_PLAY_FROM_SEARCH = "android.media.action.VIDEO_PLAY_FROM_SEARCH";
  public static final String MEDIA_IGNORE_FILENAME = ".nomedia";
  public static final String MEDIA_SCANNER_VOLUME = "volume";
  public static final String META_DATA_STILL_IMAGE_CAMERA_PREWARM_SERVICE = "android.media.still_image_camera_preview_service";
  public static final String PARAM_DELETE_DATA = "deletedata";
  private static final String TAG = "MediaStore";
  public static final String UNHIDE_CALL = "unhide";
  public static final String UNKNOWN_STRING = "<unknown>";
  
  public static Uri getMediaScannerUri()
  {
    return Uri.parse("content://media/none/media_scanner");
  }
  
  public static String getVersion(Context paramContext)
  {
    paramContext = paramContext.getContentResolver().query(Uri.parse("content://media/none/version"), null, null, null, null);
    if (paramContext != null) {}
    try
    {
      if (paramContext.moveToFirst())
      {
        String str = paramContext.getString(0);
        return str;
      }
      return null;
    }
    finally
    {
      paramContext.close();
    }
  }
  
  public static final class Audio
  {
    public static String keyFor(String paramString)
    {
      if (paramString != null)
      {
        int i = 0;
        if (paramString.equals("<unknown>")) {
          return "\001";
        }
        if (paramString.startsWith("\001")) {
          i = 1;
        }
        Object localObject = paramString.trim().toLowerCase();
        paramString = (String)localObject;
        if (((String)localObject).startsWith("the ")) {
          paramString = ((String)localObject).substring(4);
        }
        localObject = paramString;
        if (paramString.startsWith("an ")) {
          localObject = paramString.substring(3);
        }
        paramString = (String)localObject;
        if (((String)localObject).startsWith("a ")) {
          paramString = ((String)localObject).substring(2);
        }
        if ((!paramString.endsWith(", the")) && (!paramString.endsWith(",the")) && (!paramString.endsWith(", an")) && (!paramString.endsWith(",an")) && (!paramString.endsWith(", a")))
        {
          localObject = paramString;
          if (!paramString.endsWith(",a")) {}
        }
        else
        {
          localObject = paramString.substring(0, paramString.lastIndexOf(','));
        }
        paramString = ((String)localObject).replaceAll("[\\[\\]\\(\\)\"'.,?!]", "").trim();
        if (paramString.length() > 0)
        {
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append('.');
          int k = paramString.length();
          int j = 0;
          while (j < k)
          {
            ((StringBuilder)localObject).append(paramString.charAt(j));
            ((StringBuilder)localObject).append('.');
            j += 1;
          }
          localObject = DatabaseUtils.getCollationKey(((StringBuilder)localObject).toString());
          paramString = (String)localObject;
          if (i != 0) {
            paramString = "\001" + (String)localObject;
          }
          return paramString;
        }
        return "";
      }
      return null;
    }
    
    public static abstract interface AlbumColumns
    {
      public static final String ALBUM = "album";
      public static final String ALBUM_ART = "album_art";
      public static final String ALBUM_ID = "album_id";
      public static final String ALBUM_KEY = "album_key";
      public static final String ARTIST = "artist";
      public static final String FIRST_YEAR = "minyear";
      public static final String LAST_YEAR = "maxyear";
      public static final String NUMBER_OF_SONGS = "numsongs";
      public static final String NUMBER_OF_SONGS_FOR_ARTIST = "numsongs_by_artist";
    }
    
    public static final class Albums
      implements BaseColumns, MediaStore.Audio.AlbumColumns
    {
      public static final String CONTENT_TYPE = "vnd.android.cursor.dir/albums";
      public static final String DEFAULT_SORT_ORDER = "album_key";
      public static final String ENTRY_CONTENT_TYPE = "vnd.android.cursor.item/album";
      public static final Uri EXTERNAL_CONTENT_URI = getContentUri("external");
      public static final Uri INTERNAL_CONTENT_URI = getContentUri("internal");
      
      public static Uri getContentUri(String paramString)
      {
        return Uri.parse("content://media/" + paramString + "/audio/albums");
      }
    }
    
    public static abstract interface ArtistColumns
    {
      public static final String ARTIST = "artist";
      public static final String ARTIST_KEY = "artist_key";
      public static final String NUMBER_OF_ALBUMS = "number_of_albums";
      public static final String NUMBER_OF_TRACKS = "number_of_tracks";
    }
    
    public static final class Artists
      implements BaseColumns, MediaStore.Audio.ArtistColumns
    {
      public static final String CONTENT_TYPE = "vnd.android.cursor.dir/artists";
      public static final String DEFAULT_SORT_ORDER = "artist_key";
      public static final String ENTRY_CONTENT_TYPE = "vnd.android.cursor.item/artist";
      public static final Uri EXTERNAL_CONTENT_URI = getContentUri("external");
      public static final Uri INTERNAL_CONTENT_URI = getContentUri("internal");
      
      public static Uri getContentUri(String paramString)
      {
        return Uri.parse("content://media/" + paramString + "/audio/artists");
      }
      
      public static final class Albums
        implements MediaStore.Audio.AlbumColumns
      {
        public static final Uri getContentUri(String paramString, long paramLong)
        {
          return Uri.parse("content://media/" + paramString + "/audio/artists/" + paramLong + "/albums");
        }
      }
    }
    
    public static abstract interface AudioColumns
      extends MediaStore.MediaColumns
    {
      public static final String ALBUM = "album";
      public static final String ALBUM_ARTIST = "album_artist";
      public static final String ALBUM_ID = "album_id";
      public static final String ALBUM_KEY = "album_key";
      public static final String ARTIST = "artist";
      public static final String ARTIST_ID = "artist_id";
      public static final String ARTIST_KEY = "artist_key";
      public static final String BOOKMARK = "bookmark";
      public static final String COMPILATION = "compilation";
      public static final String COMPOSER = "composer";
      public static final String DURATION = "duration";
      public static final String GENRE = "genre";
      public static final String IS_ALARM = "is_alarm";
      public static final String IS_MUSIC = "is_music";
      public static final String IS_NOTIFICATION = "is_notification";
      public static final String IS_PODCAST = "is_podcast";
      public static final String IS_RINGTONE = "is_ringtone";
      public static final String TITLE_KEY = "title_key";
      public static final String TRACK = "track";
      public static final String YEAR = "year";
    }
    
    public static final class Genres
      implements BaseColumns, MediaStore.Audio.GenresColumns
    {
      public static final String CONTENT_TYPE = "vnd.android.cursor.dir/genre";
      public static final String DEFAULT_SORT_ORDER = "name";
      public static final String ENTRY_CONTENT_TYPE = "vnd.android.cursor.item/genre";
      public static final Uri EXTERNAL_CONTENT_URI = getContentUri("external");
      public static final Uri INTERNAL_CONTENT_URI = getContentUri("internal");
      
      public static Uri getContentUri(String paramString)
      {
        return Uri.parse("content://media/" + paramString + "/audio/genres");
      }
      
      public static Uri getContentUriForAudioId(String paramString, int paramInt)
      {
        return Uri.parse("content://media/" + paramString + "/audio/media/" + paramInt + "/genres");
      }
      
      public static final class Members
        implements MediaStore.Audio.AudioColumns
      {
        public static final String AUDIO_ID = "audio_id";
        public static final String CONTENT_DIRECTORY = "members";
        public static final String DEFAULT_SORT_ORDER = "title_key";
        public static final String GENRE_ID = "genre_id";
        
        public static final Uri getContentUri(String paramString, long paramLong)
        {
          return Uri.parse("content://media/" + paramString + "/audio/genres/" + paramLong + "/members");
        }
      }
    }
    
    public static abstract interface GenresColumns
    {
      public static final String NAME = "name";
    }
    
    public static final class Media
      implements MediaStore.Audio.AudioColumns
    {
      public static final String CONTENT_TYPE = "vnd.android.cursor.dir/audio";
      public static final String DEFAULT_SORT_ORDER = "title_key";
      public static final String ENTRY_CONTENT_TYPE = "vnd.android.cursor.item/audio";
      public static final Uri EXTERNAL_CONTENT_URI;
      private static final String[] EXTERNAL_PATHS;
      public static final String EXTRA_MAX_BYTES = "android.provider.MediaStore.extra.MAX_BYTES";
      public static final Uri INTERNAL_CONTENT_URI;
      public static final String RECORD_SOUND_ACTION = "android.provider.MediaStore.RECORD_SOUND";
      
      static
      {
        String str = System.getenv("SECONDARY_STORAGE");
        if (str != null) {}
        for (EXTERNAL_PATHS = str.split(":");; EXTERNAL_PATHS = new String[0])
        {
          INTERNAL_CONTENT_URI = getContentUri("internal");
          EXTERNAL_CONTENT_URI = getContentUri("external");
          return;
        }
      }
      
      public static Uri getContentUri(String paramString)
      {
        return Uri.parse("content://media/" + paramString + "/audio/media");
      }
      
      public static Uri getContentUriForPath(String paramString)
      {
        String[] arrayOfString = EXTERNAL_PATHS;
        int i = 0;
        int j = arrayOfString.length;
        while (i < j)
        {
          if (paramString.startsWith(arrayOfString[i])) {
            return EXTERNAL_CONTENT_URI;
          }
          i += 1;
        }
        if (paramString.startsWith(Environment.getExternalStorageDirectory().getPath())) {
          return EXTERNAL_CONTENT_URI;
        }
        return INTERNAL_CONTENT_URI;
      }
    }
    
    public static final class Playlists
      implements BaseColumns, MediaStore.Audio.PlaylistsColumns
    {
      public static final String CONTENT_TYPE = "vnd.android.cursor.dir/playlist";
      public static final String DEFAULT_SORT_ORDER = "name";
      public static final String ENTRY_CONTENT_TYPE = "vnd.android.cursor.item/playlist";
      public static final Uri EXTERNAL_CONTENT_URI = getContentUri("external");
      public static final Uri INTERNAL_CONTENT_URI = getContentUri("internal");
      
      public static Uri getContentUri(String paramString)
      {
        return Uri.parse("content://media/" + paramString + "/audio/playlists");
      }
      
      public static final class Members
        implements MediaStore.Audio.AudioColumns
      {
        public static final String AUDIO_ID = "audio_id";
        public static final String CONTENT_DIRECTORY = "members";
        public static final String DEFAULT_SORT_ORDER = "play_order";
        public static final String PLAYLIST_ID = "playlist_id";
        public static final String PLAY_ORDER = "play_order";
        public static final String _ID = "_id";
        
        public static final Uri getContentUri(String paramString, long paramLong)
        {
          return Uri.parse("content://media/" + paramString + "/audio/playlists/" + paramLong + "/members");
        }
        
        public static final boolean moveItem(ContentResolver paramContentResolver, long paramLong, int paramInt1, int paramInt2)
        {
          boolean bool = false;
          Uri localUri = getContentUri("external", paramLong).buildUpon().appendEncodedPath(String.valueOf(paramInt1)).appendQueryParameter("move", "true").build();
          ContentValues localContentValues = new ContentValues();
          localContentValues.put("play_order", Integer.valueOf(paramInt2));
          if (paramContentResolver.update(localUri, localContentValues, null, null) != 0) {
            bool = true;
          }
          return bool;
        }
      }
    }
    
    public static abstract interface PlaylistsColumns
    {
      public static final String DATA = "_data";
      public static final String DATE_ADDED = "date_added";
      public static final String DATE_MODIFIED = "date_modified";
      public static final String NAME = "name";
    }
    
    public static final class Radio
    {
      public static final String ENTRY_CONTENT_TYPE = "vnd.android.cursor.item/radio";
    }
  }
  
  public static final class Files
  {
    public static Uri getContentUri(String paramString)
    {
      return Uri.parse("content://media/" + paramString + "/file");
    }
    
    public static final Uri getContentUri(String paramString, long paramLong)
    {
      return Uri.parse("content://media/" + paramString + "/file/" + paramLong);
    }
    
    public static Uri getMtpObjectsUri(String paramString)
    {
      return Uri.parse("content://media/" + paramString + "/object");
    }
    
    public static final Uri getMtpObjectsUri(String paramString, long paramLong)
    {
      return Uri.parse("content://media/" + paramString + "/object/" + paramLong);
    }
    
    public static final Uri getMtpReferencesUri(String paramString, long paramLong)
    {
      return Uri.parse("content://media/" + paramString + "/object/" + paramLong + "/references");
    }
    
    public static abstract interface FileColumns
      extends MediaStore.MediaColumns
    {
      public static final String FORMAT = "format";
      public static final String MEDIA_TYPE = "media_type";
      public static final int MEDIA_TYPE_AUDIO = 2;
      public static final int MEDIA_TYPE_IMAGE = 1;
      public static final int MEDIA_TYPE_NONE = 0;
      public static final int MEDIA_TYPE_PLAYLIST = 4;
      public static final int MEDIA_TYPE_VIDEO = 3;
      public static final String MIME_TYPE = "mime_type";
      public static final String PARENT = "parent";
      public static final String STORAGE_ID = "storage_id";
      public static final String TITLE = "title";
    }
  }
  
  public static final class Images
  {
    public static abstract interface ImageColumns
      extends MediaStore.MediaColumns
    {
      public static final String BUCKET_DISPLAY_NAME = "bucket_display_name";
      public static final String BUCKET_ID = "bucket_id";
      public static final String DATE_TAKEN = "datetaken";
      public static final String DESCRIPTION = "description";
      public static final String IS_PRIVATE = "isprivate";
      public static final String LATITUDE = "latitude";
      public static final String LONGITUDE = "longitude";
      public static final String MINI_THUMB_MAGIC = "mini_thumb_magic";
      public static final String ORIENTATION = "orientation";
      public static final String PICASA_ID = "picasa_id";
    }
    
    public static final class Media
      implements MediaStore.Images.ImageColumns
    {
      public static final String CONTENT_TYPE = "vnd.android.cursor.dir/image";
      public static final String DEFAULT_SORT_ORDER = "bucket_display_name";
      public static final Uri EXTERNAL_CONTENT_URI = getContentUri("external");
      public static final Uri INTERNAL_CONTENT_URI = getContentUri("internal");
      
      private static final Bitmap StoreThumbnail(ContentResolver paramContentResolver, Bitmap paramBitmap, long paramLong, float paramFloat1, float paramFloat2, int paramInt)
      {
        Object localObject = new Matrix();
        ((Matrix)localObject).setScale(paramFloat1 / paramBitmap.getWidth(), paramFloat2 / paramBitmap.getHeight());
        paramBitmap = Bitmap.createBitmap(paramBitmap, 0, 0, paramBitmap.getWidth(), paramBitmap.getHeight(), (Matrix)localObject, true);
        localObject = new ContentValues(4);
        ((ContentValues)localObject).put("kind", Integer.valueOf(paramInt));
        ((ContentValues)localObject).put("image_id", Integer.valueOf((int)paramLong));
        ((ContentValues)localObject).put("height", Integer.valueOf(paramBitmap.getHeight()));
        ((ContentValues)localObject).put("width", Integer.valueOf(paramBitmap.getWidth()));
        localObject = paramContentResolver.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, (ContentValues)localObject);
        try
        {
          paramContentResolver = paramContentResolver.openOutputStream((Uri)localObject);
          paramBitmap.compress(Bitmap.CompressFormat.JPEG, 100, paramContentResolver);
          paramContentResolver.close();
          return paramBitmap;
        }
        catch (IOException paramContentResolver)
        {
          return null;
        }
        catch (FileNotFoundException paramContentResolver) {}
        return null;
      }
      
      public static final Bitmap getBitmap(ContentResolver paramContentResolver, Uri paramUri)
        throws FileNotFoundException, IOException
      {
        paramContentResolver = paramContentResolver.openInputStream(paramUri);
        paramUri = BitmapFactory.decodeStream(paramContentResolver);
        paramContentResolver.close();
        return paramUri;
      }
      
      public static Uri getContentUri(String paramString)
      {
        return Uri.parse("content://media/" + paramString + "/images/media");
      }
      
      public static final String insertImage(ContentResolver paramContentResolver, Bitmap paramBitmap, String paramString1, String paramString2)
      {
        Object localObject2 = new ContentValues();
        ((ContentValues)localObject2).put("title", paramString1);
        ((ContentValues)localObject2).put("description", paramString2);
        ((ContentValues)localObject2).put("mime_type", "image/jpeg");
        paramString1 = null;
        Object localObject1 = null;
        for (;;)
        {
          try
          {
            paramString2 = paramContentResolver.insert(EXTERNAL_CONTENT_URI, (ContentValues)localObject2);
            if (paramBitmap == null) {
              continue;
            }
            paramString1 = paramString2;
            localObject2 = paramContentResolver.openOutputStream(paramString2);
          }
          catch (Exception paramBitmap)
          {
            long l;
            Log.e("MediaStore", "Failed to insert image", paramBitmap);
            paramBitmap = paramString1;
            if (paramString1 == null) {
              continue;
            }
            paramContentResolver.delete(paramString1, null, null);
            paramBitmap = null;
            continue;
            paramString1 = paramString2;
            Log.e("MediaStore", "Failed to create thumbnail, removing original");
            paramString1 = paramString2;
            paramContentResolver.delete(paramString2, null, null);
            paramBitmap = null;
            continue;
          }
          try
          {
            paramBitmap.compress(Bitmap.CompressFormat.JPEG, 50, (OutputStream)localObject2);
            paramString1 = paramString2;
            ((OutputStream)localObject2).close();
            paramString1 = paramString2;
            l = ContentUris.parseId(paramString2);
            paramString1 = paramString2;
            StoreThumbnail(paramContentResolver, MediaStore.Images.Thumbnails.getThumbnail(paramContentResolver, l, 1, null), l, 50.0F, 50.0F, 3);
            paramBitmap = paramString2;
            paramContentResolver = (ContentResolver)localObject1;
            if (paramBitmap != null) {
              paramContentResolver = paramBitmap.toString();
            }
            return paramContentResolver;
          }
          finally
          {
            paramString1 = paramString2;
            ((OutputStream)localObject2).close();
            paramString1 = paramString2;
          }
        }
      }
      
      public static final String insertImage(ContentResolver paramContentResolver, String paramString1, String paramString2, String paramString3)
        throws FileNotFoundException
      {
        localFileInputStream = new FileInputStream(paramString1);
        try
        {
          paramString1 = BitmapFactory.decodeFile(paramString1);
          paramContentResolver = insertImage(paramContentResolver, paramString1, paramString2, paramString3);
          paramString1.recycle();
          try
          {
            localFileInputStream.close();
            return paramContentResolver;
          }
          catch (IOException paramString1)
          {
            return paramContentResolver;
          }
          try
          {
            localFileInputStream.close();
            throw paramContentResolver;
          }
          catch (IOException paramString1)
          {
            for (;;) {}
          }
        }
        finally {}
      }
      
      public static final Cursor query(ContentResolver paramContentResolver, Uri paramUri, String[] paramArrayOfString)
      {
        return paramContentResolver.query(paramUri, paramArrayOfString, null, null, "bucket_display_name");
      }
      
      public static final Cursor query(ContentResolver paramContentResolver, Uri paramUri, String[] paramArrayOfString, String paramString1, String paramString2)
      {
        if (paramString2 == null) {
          paramString2 = "bucket_display_name";
        }
        for (;;)
        {
          return paramContentResolver.query(paramUri, paramArrayOfString, paramString1, null, paramString2);
        }
      }
      
      public static final Cursor query(ContentResolver paramContentResolver, Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2)
      {
        if (paramString2 == null) {
          paramString2 = "bucket_display_name";
        }
        for (;;)
        {
          return paramContentResolver.query(paramUri, paramArrayOfString1, paramString1, paramArrayOfString2, paramString2);
        }
      }
    }
    
    public static class Thumbnails
      implements BaseColumns
    {
      public static final String DATA = "_data";
      public static final String DEFAULT_SORT_ORDER = "image_id ASC";
      public static final Uri EXTERNAL_CONTENT_URI = getContentUri("external");
      public static final int FULL_SCREEN_KIND = 2;
      public static final String HEIGHT = "height";
      public static final String IMAGE_ID = "image_id";
      public static final Uri INTERNAL_CONTENT_URI = getContentUri("internal");
      public static final String KIND = "kind";
      public static final int MICRO_KIND = 3;
      public static final int MINI_KIND = 1;
      public static final String THUMB_DATA = "thumb_data";
      public static final String WIDTH = "width";
      
      public static void cancelThumbnailRequest(ContentResolver paramContentResolver, long paramLong)
      {
        MediaStore.InternalThumbnails.cancelThumbnailRequest(paramContentResolver, paramLong, EXTERNAL_CONTENT_URI, 0L);
      }
      
      public static void cancelThumbnailRequest(ContentResolver paramContentResolver, long paramLong1, long paramLong2)
      {
        MediaStore.InternalThumbnails.cancelThumbnailRequest(paramContentResolver, paramLong1, EXTERNAL_CONTENT_URI, paramLong2);
      }
      
      public static Uri getContentUri(String paramString)
      {
        return Uri.parse("content://media/" + paramString + "/images/thumbnails");
      }
      
      public static Bitmap getThumbnail(ContentResolver paramContentResolver, long paramLong, int paramInt, BitmapFactory.Options paramOptions)
      {
        return MediaStore.InternalThumbnails.getThumbnail(paramContentResolver, paramLong, 0L, paramInt, paramOptions, EXTERNAL_CONTENT_URI, false);
      }
      
      public static Bitmap getThumbnail(ContentResolver paramContentResolver, long paramLong1, long paramLong2, int paramInt, BitmapFactory.Options paramOptions)
      {
        return MediaStore.InternalThumbnails.getThumbnail(paramContentResolver, paramLong1, paramLong2, paramInt, paramOptions, EXTERNAL_CONTENT_URI, false);
      }
      
      public static final Cursor query(ContentResolver paramContentResolver, Uri paramUri, String[] paramArrayOfString)
      {
        return paramContentResolver.query(paramUri, paramArrayOfString, null, null, "image_id ASC");
      }
      
      public static final Cursor queryMiniThumbnail(ContentResolver paramContentResolver, long paramLong, int paramInt, String[] paramArrayOfString)
      {
        return paramContentResolver.query(EXTERNAL_CONTENT_URI, paramArrayOfString, "image_id = " + paramLong + " AND " + "kind" + " = " + paramInt, null, null);
      }
      
      public static final Cursor queryMiniThumbnails(ContentResolver paramContentResolver, Uri paramUri, int paramInt, String[] paramArrayOfString)
      {
        return paramContentResolver.query(paramUri, paramArrayOfString, "kind = " + paramInt, null, "image_id ASC");
      }
    }
  }
  
  private static class InternalThumbnails
    implements BaseColumns
  {
    static final int DEFAULT_GROUP_ID = 0;
    private static final int FULL_SCREEN_KIND = 2;
    private static final int MICRO_KIND = 3;
    private static final int MINI_KIND = 1;
    private static final String[] PROJECTION = { "_id", "_data" };
    private static byte[] sThumbBuf;
    private static final Object sThumbBufLock = new Object();
    
    static void cancelThumbnailRequest(ContentResolver paramContentResolver, long paramLong1, Uri paramUri, long paramLong2)
    {
      paramUri = paramUri.buildUpon().appendQueryParameter("cancel", "1").appendQueryParameter("orig_id", String.valueOf(paramLong1)).appendQueryParameter("group_id", String.valueOf(paramLong2)).build();
      try
      {
        paramContentResolver = paramContentResolver.query(paramUri, PROJECTION, null, null, null);
        if (paramContentResolver != null) {
          paramContentResolver.close();
        }
        return;
      }
      finally {}
    }
    
    private static Bitmap getMiniThumbFromFile(Cursor paramCursor, Uri paramUri, ContentResolver paramContentResolver, BitmapFactory.Options paramOptions)
    {
      Object localObject8 = null;
      Object localObject9 = null;
      Object localObject7 = null;
      Object localObject11 = null;
      Object localObject12 = null;
      Object localObject10 = null;
      Object localObject1 = localObject7;
      Object localObject2 = localObject10;
      Object localObject3 = localObject8;
      Object localObject4 = localObject11;
      Object localObject5 = localObject9;
      Object localObject6 = localObject12;
      try
      {
        long l = paramCursor.getLong(0);
        localObject1 = localObject7;
        localObject2 = localObject10;
        localObject3 = localObject8;
        localObject4 = localObject11;
        localObject5 = localObject9;
        localObject6 = localObject12;
        paramCursor.getString(1);
        localObject1 = localObject7;
        localObject2 = localObject10;
        localObject3 = localObject8;
        localObject4 = localObject11;
        localObject5 = localObject9;
        localObject6 = localObject12;
        paramCursor = ContentUris.withAppendedId(paramUri, l);
        localObject1 = localObject7;
        localObject2 = paramCursor;
        localObject3 = localObject8;
        localObject4 = paramCursor;
        localObject5 = localObject9;
        localObject6 = paramCursor;
        paramContentResolver = paramContentResolver.openFileDescriptor(paramCursor, "r");
        localObject1 = localObject7;
        localObject2 = paramCursor;
        localObject3 = localObject8;
        localObject4 = paramCursor;
        localObject5 = localObject9;
        localObject6 = paramCursor;
        paramUri = BitmapFactory.decodeFileDescriptor(paramContentResolver.getFileDescriptor(), null, paramOptions);
        localObject1 = paramUri;
        localObject2 = paramCursor;
        localObject3 = paramUri;
        localObject4 = paramCursor;
        localObject5 = paramUri;
        localObject6 = paramCursor;
        paramContentResolver.close();
        return paramUri;
      }
      catch (OutOfMemoryError paramCursor)
      {
        Log.e("MediaStore", "failed to allocate memory for thumbnail " + localObject2 + "; " + paramCursor);
        return (Bitmap)localObject1;
      }
      catch (IOException paramCursor)
      {
        Log.e("MediaStore", "couldn't open thumbnail " + localObject4 + "; " + paramCursor);
        return (Bitmap)localObject3;
      }
      catch (FileNotFoundException paramCursor)
      {
        Log.e("MediaStore", "couldn't open thumbnail " + localObject6 + "; " + paramCursor);
      }
      return (Bitmap)localObject5;
    }
    
    static Bitmap getThumbnail(ContentResolver paramContentResolver, long paramLong1, long paramLong2, int paramInt, BitmapFactory.Options paramOptions, Uri paramUri, boolean paramBoolean)
    {
      Object localObject10 = null;
      Object localObject7 = null;
      Object localObject9 = null;
      Object localObject11 = null;
      Object localObject1;
      MiniThumbFile localMiniThumbFile;
      Object localObject12;
      Object localObject8;
      Object localObject6;
      Object localObject3;
      Object localObject4;
      Object localObject5;
      Object localObject2;
      if (paramBoolean)
      {
        localObject1 = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        localMiniThumbFile = new MiniThumbFile((Uri)localObject1);
        localObject12 = null;
        localObject8 = null;
        localObject6 = null;
        localObject1 = localObject7;
        localObject3 = localObject6;
        localObject4 = localObject9;
        localObject5 = localObject12;
        localObject2 = localObject8;
      }
      for (;;)
      {
        try
        {
          if (localMiniThumbFile.getMagic(paramLong1) != 0L) {
            if (paramInt == 3)
            {
              localObject1 = localObject7;
              localObject3 = localObject6;
              localObject2 = localObject8;
              localObject4 = sThumbBufLock;
              localObject1 = localObject7;
              localObject3 = localObject6;
              localObject2 = localObject8;
              paramContentResolver = (ContentResolver)localObject10;
              try
              {
                if (sThumbBuf == null)
                {
                  paramContentResolver = (ContentResolver)localObject10;
                  sThumbBuf = new byte['✐'];
                }
                paramOptions = (BitmapFactory.Options)localObject11;
                paramContentResolver = (ContentResolver)localObject10;
                if (localMiniThumbFile.getMiniThumbFromFile(paramLong1, sThumbBuf) != null)
                {
                  paramContentResolver = (ContentResolver)localObject10;
                  paramUri = BitmapFactory.decodeByteArray(sThumbBuf, 0, sThumbBuf.length);
                  paramOptions = paramUri;
                  if (paramUri == null)
                  {
                    paramContentResolver = paramUri;
                    Log.w("MediaStore", "couldn't decode byte array.");
                    paramOptions = paramUri;
                  }
                }
                localObject1 = paramOptions;
                localObject3 = localObject6;
                localObject2 = localObject8;
                localMiniThumbFile.deactivate();
                return paramOptions;
              }
              finally
              {
                localObject1 = paramContentResolver;
                localObject3 = localObject6;
                localObject2 = localObject8;
                localObject1 = paramContentResolver;
                localObject3 = localObject6;
                localObject2 = localObject8;
              }
              localObject1 = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
              break;
            }
          }
        }
        catch (SQLiteException paramContentResolver)
        {
          localObject2 = localObject3;
          Log.w("MediaStore", paramContentResolver);
          if (localObject3 != null) {
            ((Cursor)localObject3).close();
          }
          localMiniThumbFile.deactivate();
          paramOptions = (BitmapFactory.Options)localObject1;
          return paramOptions;
          localObject4 = localObject9;
          localObject5 = localObject12;
          if (paramInt == 1) {
            if (paramBoolean)
            {
              localObject4 = "video_id=";
              localObject1 = localObject7;
              localObject3 = localObject6;
              localObject2 = localObject8;
              localObject6 = paramContentResolver.query(paramUri, PROJECTION, (String)localObject4 + paramLong1, null, null);
              localObject4 = localObject9;
              localObject5 = localObject6;
              if (localObject6 != null)
              {
                localObject1 = localObject7;
                localObject3 = localObject6;
                localObject4 = localObject9;
                localObject5 = localObject6;
                localObject2 = localObject6;
                if (((Cursor)localObject6).moveToFirst())
                {
                  localObject1 = localObject7;
                  localObject3 = localObject6;
                  localObject2 = localObject6;
                  localObject4 = getMiniThumbFromFile((Cursor)localObject6, paramUri, paramContentResolver, paramOptions);
                  localObject1 = localObject4;
                  localObject4 = localObject1;
                  localObject5 = localObject6;
                  if (localObject1 != null) {
                    return (Bitmap)localObject1;
                  }
                }
              }
            }
            else
            {
              localObject4 = "image_id=";
              continue;
            }
          }
          localObject1 = localObject4;
          localObject3 = localObject5;
          localObject2 = localObject5;
          localObject6 = paramUri.buildUpon().appendQueryParameter("blocking", "1").appendQueryParameter("orig_id", String.valueOf(paramLong1)).appendQueryParameter("group_id", String.valueOf(paramLong2)).build();
          if (localObject5 != null)
          {
            localObject1 = localObject4;
            localObject3 = localObject5;
            localObject2 = localObject5;
            ((Cursor)localObject5).close();
          }
          localObject1 = localObject4;
          localObject3 = localObject5;
          localObject2 = localObject5;
          localObject6 = paramContentResolver.query((Uri)localObject6, PROJECTION, null, null, null);
          if (localObject6 == null) {
            return null;
          }
          if (paramInt == 3)
          {
            localObject1 = localObject4;
            localObject3 = localObject6;
            localObject2 = localObject6;
            localObject7 = sThumbBufLock;
            localObject1 = localObject4;
            localObject3 = localObject6;
            localObject2 = localObject6;
            paramOptions = (BitmapFactory.Options)localObject4;
            try
            {
              if (sThumbBuf == null)
              {
                paramOptions = (BitmapFactory.Options)localObject4;
                sThumbBuf = new byte['✐'];
              }
              paramOptions = (BitmapFactory.Options)localObject4;
              Arrays.fill(sThumbBuf, (byte)0);
              localObject5 = localObject4;
              paramOptions = (BitmapFactory.Options)localObject4;
              if (localMiniThumbFile.getMiniThumbFromFile(paramLong1, sThumbBuf) != null)
              {
                paramOptions = (BitmapFactory.Options)localObject4;
                localObject1 = BitmapFactory.decodeByteArray(sThumbBuf, 0, sThumbBuf.length);
                localObject5 = localObject1;
                if (localObject1 == null)
                {
                  paramOptions = (BitmapFactory.Options)localObject1;
                  Log.w("MediaStore", "couldn't decode byte array.");
                  localObject5 = localObject1;
                }
              }
              localObject1 = localObject5;
              localObject3 = localObject6;
              localObject2 = localObject6;
              paramOptions = (BitmapFactory.Options)localObject5;
              localObject1 = localObject6;
              if (localObject5 == null)
              {
                localObject1 = localObject5;
                localObject3 = localObject6;
                localObject2 = localObject6;
                Log.v("MediaStore", "Create the thumbnail in memory: origId=" + paramLong1 + ", kind=" + paramInt + ", isVideo=" + paramBoolean);
                localObject1 = localObject5;
                localObject3 = localObject6;
                localObject2 = localObject6;
                paramOptions = Uri.parse(paramUri.buildUpon().appendPath(String.valueOf(paramLong1)).toString().replaceFirst("thumbnails", "media"));
                if (localObject6 != null)
                {
                  localObject1 = localObject5;
                  localObject3 = localObject6;
                  localObject2 = localObject6;
                  ((Cursor)localObject6).close();
                }
                localObject1 = localObject5;
                localObject3 = localObject6;
                localObject2 = localObject6;
                paramContentResolver = paramContentResolver.query(paramOptions, PROJECTION, null, null, null);
                if (paramContentResolver == null) {
                  break label1142;
                }
                localObject1 = localObject5;
                localObject3 = paramContentResolver;
                localObject2 = paramContentResolver;
                if (!paramContentResolver.moveToFirst()) {
                  break label1142;
                }
                localObject1 = localObject5;
                localObject3 = paramContentResolver;
                localObject2 = paramContentResolver;
                paramUri = paramContentResolver.getString(1);
                paramOptions = (BitmapFactory.Options)localObject5;
                localObject1 = paramContentResolver;
                if (paramUri != null)
                {
                  if (!paramBoolean) {
                    break label1159;
                  }
                  localObject1 = localObject5;
                  localObject3 = paramContentResolver;
                  localObject2 = paramContentResolver;
                  paramOptions = ThumbnailUtils.createVideoThumbnail(paramUri, paramInt);
                  localObject1 = paramContentResolver;
                }
              }
              if (localObject1 != null) {
                ((Cursor)localObject1).close();
              }
              localMiniThumbFile.deactivate();
              continue;
            }
            finally
            {
              localObject1 = paramOptions;
              localObject3 = localObject6;
              localObject2 = localObject6;
              localObject1 = paramOptions;
              localObject3 = localObject6;
              localObject2 = localObject6;
            }
          }
        }
        finally
        {
          if (localObject2 != null) {
            ((Cursor)localObject2).close();
          }
          localMiniThumbFile.deactivate();
        }
        if (paramInt == 1)
        {
          localObject1 = localObject4;
          localObject3 = localObject6;
          localObject5 = localObject4;
          localObject2 = localObject6;
          if (((Cursor)localObject6).moveToFirst())
          {
            localObject1 = localObject4;
            localObject3 = localObject6;
            localObject2 = localObject6;
            localObject5 = getMiniThumbFromFile((Cursor)localObject6, paramUri, paramContentResolver, paramOptions);
          }
        }
        else
        {
          localObject1 = localObject4;
          localObject3 = localObject6;
          localObject2 = localObject6;
          throw new IllegalArgumentException("Unsupported kind: " + paramInt);
          label1142:
          if (paramContentResolver != null) {
            paramContentResolver.close();
          }
          localMiniThumbFile.deactivate();
          return null;
          label1159:
          localObject1 = localObject5;
          localObject3 = paramContentResolver;
          localObject2 = paramContentResolver;
          paramOptions = ThumbnailUtils.createImageThumbnail(paramUri, paramInt);
          localObject1 = paramContentResolver;
        }
      }
    }
  }
  
  public static abstract interface MediaColumns
    extends BaseColumns
  {
    public static final String DATA = "_data";
    public static final String DATE_ADDED = "date_added";
    public static final String DATE_MODIFIED = "date_modified";
    public static final String DISPLAY_NAME = "_display_name";
    public static final String HEIGHT = "height";
    public static final String IS_DRM = "is_drm";
    public static final String MEDIA_SCANNER_NEW_OBJECT_ID = "media_scanner_new_object_id";
    public static final String MIME_TYPE = "mime_type";
    public static final String SIZE = "_size";
    public static final String TITLE = "title";
    public static final String WIDTH = "width";
  }
  
  public static final class Video
  {
    public static final String DEFAULT_SORT_ORDER = "_display_name";
    
    public static final Cursor query(ContentResolver paramContentResolver, Uri paramUri, String[] paramArrayOfString)
    {
      return paramContentResolver.query(paramUri, paramArrayOfString, null, null, "_display_name");
    }
    
    public static final class Media
      implements MediaStore.Video.VideoColumns
    {
      public static final String CONTENT_TYPE = "vnd.android.cursor.dir/video";
      public static final String DEFAULT_SORT_ORDER = "title";
      public static final Uri EXTERNAL_CONTENT_URI = getContentUri("external");
      public static final Uri INTERNAL_CONTENT_URI = getContentUri("internal");
      
      public static Uri getContentUri(String paramString)
      {
        return Uri.parse("content://media/" + paramString + "/video/media");
      }
    }
    
    public static class Thumbnails
      implements BaseColumns
    {
      public static final String DATA = "_data";
      public static final String DEFAULT_SORT_ORDER = "video_id ASC";
      public static final Uri EXTERNAL_CONTENT_URI = getContentUri("external");
      public static final int FULL_SCREEN_KIND = 2;
      public static final String HEIGHT = "height";
      public static final Uri INTERNAL_CONTENT_URI = getContentUri("internal");
      public static final String KIND = "kind";
      public static final int MICRO_KIND = 3;
      public static final int MINI_KIND = 1;
      public static final String VIDEO_ID = "video_id";
      public static final String WIDTH = "width";
      
      public static void cancelThumbnailRequest(ContentResolver paramContentResolver, long paramLong)
      {
        MediaStore.InternalThumbnails.cancelThumbnailRequest(paramContentResolver, paramLong, EXTERNAL_CONTENT_URI, 0L);
      }
      
      public static void cancelThumbnailRequest(ContentResolver paramContentResolver, long paramLong1, long paramLong2)
      {
        MediaStore.InternalThumbnails.cancelThumbnailRequest(paramContentResolver, paramLong1, EXTERNAL_CONTENT_URI, paramLong2);
      }
      
      public static Uri getContentUri(String paramString)
      {
        return Uri.parse("content://media/" + paramString + "/video/thumbnails");
      }
      
      public static Bitmap getThumbnail(ContentResolver paramContentResolver, long paramLong, int paramInt, BitmapFactory.Options paramOptions)
      {
        return MediaStore.InternalThumbnails.getThumbnail(paramContentResolver, paramLong, 0L, paramInt, paramOptions, EXTERNAL_CONTENT_URI, true);
      }
      
      public static Bitmap getThumbnail(ContentResolver paramContentResolver, long paramLong1, long paramLong2, int paramInt, BitmapFactory.Options paramOptions)
      {
        return MediaStore.InternalThumbnails.getThumbnail(paramContentResolver, paramLong1, paramLong2, paramInt, paramOptions, EXTERNAL_CONTENT_URI, true);
      }
    }
    
    public static abstract interface VideoColumns
      extends MediaStore.MediaColumns
    {
      public static final String ALBUM = "album";
      public static final String ARTIST = "artist";
      public static final String BOOKMARK = "bookmark";
      public static final String BUCKET_DISPLAY_NAME = "bucket_display_name";
      public static final String BUCKET_ID = "bucket_id";
      public static final String CATEGORY = "category";
      public static final String DATE_TAKEN = "datetaken";
      public static final String DESCRIPTION = "description";
      public static final String DURATION = "duration";
      public static final String IS_PRIVATE = "isprivate";
      public static final String LANGUAGE = "language";
      public static final String LATITUDE = "latitude";
      public static final String LONGITUDE = "longitude";
      public static final String MINI_THUMB_MAGIC = "mini_thumb_magic";
      public static final String RESOLUTION = "resolution";
      public static final String TAGS = "tags";
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/provider/MediaStore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */