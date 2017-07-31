package android.media;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.drm.DrmManagerClient;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.mtp.MtpConstants;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Build;
import android.os.Environment;
import android.os.Environment.UserEnvironment;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.MediaStore.Audio.Media;
import android.provider.MediaStore.Audio.Playlists;
import android.provider.MediaStore.Files;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Images.Thumbnails;
import android.provider.MediaStore.Video.Media;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.sax.Element;
import android.sax.ElementListener;
import android.sax.RootElement;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.text.TextUtils;
import android.util.Log;
import android.util.OpFeatures;
import dalvik.system.CloseGuard;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;

public class MediaScanner
  implements AutoCloseable
{
  private static final String ALARMS_DIR = "/alarms/";
  private static boolean CONFIG_NO_MEDIA_FOR_SIZE_ZERO = false;
  private static boolean CONFIG_PROTECT_EXTERNAL_ROOT = false;
  private static boolean CONFIG_PROTECT_MEDIA = false;
  private static boolean CONFIG_REBUILD_MEDIA_VIEW = false;
  private static final int DATE_MODIFIED_PLAYLISTS_COLUMN_INDEX = 2;
  private static final boolean DBG;
  private static final boolean DBG_LOGV;
  private static final String[] DEFAULT_RINGTONES;
  private static final int DEFAULT_RINGTONE_COUNT;
  private static final String DEFAULT_RINGTONE_PROPERTY_PREFIX = "ro.config.";
  private static final int DEFAULT_SIM_INDEX = 0;
  private static final boolean ENABLE_BULK_INSERTS = true;
  private static final int FILES_PRESCAN_DATE_MODIFIED_COLUMN_INDEX = 3;
  private static final int FILES_PRESCAN_FORMAT_COLUMN_INDEX = 2;
  private static final int FILES_PRESCAN_ID_COLUMN_INDEX = 0;
  private static final int FILES_PRESCAN_PATH_COLUMN_INDEX = 1;
  private static final String[] FILES_PRESCAN_PROJECTION;
  private static final String[] ID3_GENRES;
  private static final int ID_PLAYLISTS_COLUMN_INDEX = 0;
  private static final String[] ID_PROJECTION;
  public static final String LAST_INTERNAL_SCAN_FINGERPRINT = "lastScanFingerprint";
  private static final String MUSIC_DIR = "/music/";
  private static final String NOTIFICATIONS_DIR = "/notifications/";
  private static final int PATH_PLAYLISTS_COLUMN_INDEX = 1;
  private static final String[] PLAYLIST_MEMBERS_PROJECTION;
  private static final String PODCAST_DIR = "/podcasts/";
  private static String[] PROTECTED_MEDIA_PATH_ARRAY;
  private static final String RINGTONES_DIR = "/ringtones/";
  public static final String SCANNED_BUILD_PREFS_NAME = "MediaScanBuild";
  private static final String SYSTEM_SOUNDS_DIR = "/system/media/audio";
  private static final String TAG = "MediaScanner";
  private static String mExternalPath;
  private static HashMap<String, String> mMediaPaths = new HashMap();
  private static HashMap<String, String> mNoMediaPaths;
  private static String sLastInternalScanFingerprint;
  private final Uri mAudioUri;
  private final BitmapFactory.Options mBitmapOptions = new BitmapFactory.Options();
  private boolean mCheck_All_Again = SystemProperties.getBoolean("persist.debug.mp.check_all", false);
  private final MyMediaScannerClient mClient = new MyMediaScannerClient();
  private final CloseGuard mCloseGuard = CloseGuard.get();
  private final AtomicBoolean mClosed = new AtomicBoolean();
  private final Context mContext;
  private String mDefaultAlarmAlertFilename;
  private boolean mDefaultAlarmSet;
  private String mDefaultMmsNotificationFilename;
  private boolean mDefaultMmsNotificationSet;
  private String mDefaultNotificationFilename;
  private boolean mDefaultNotificationSet;
  private String[] mDefaultRingtoneFilenames;
  private boolean[] mDefaultRingtonesSet;
  private DrmManagerClient mDrmManagerClient = null;
  private final Uri mFilesUri;
  private final Uri mFilesUriNoNotify;
  private final Uri mImagesUri;
  private MediaInserter mMediaInserter;
  private final ContentProviderClient mMediaProvider;
  private int mMtpObjectHandle;
  private long mNativeContext;
  private int mOriginalCount;
  private final String mPackageName;
  private final ArrayList<FileEntry> mPlayLists = new ArrayList();
  private final ArrayList<PlaylistEntry> mPlaylistEntries = new ArrayList();
  private final Uri mPlaylistsUri;
  private final boolean mProcessGenres;
  private final boolean mProcessPlaylists;
  private final Uri mThumbsUri;
  private final Uri mVideoUri;
  private final String mVolumeName;
  
  static
  {
    System.loadLibrary("media_jni");
    native_init();
    DBG_LOGV = SystemProperties.getBoolean("persist.debug.mp.all", false);
    DBG = Build.DEBUG_ONEPLUS | DBG_LOGV;
    CONFIG_NO_MEDIA_FOR_SIZE_ZERO = true;
    CONFIG_PROTECT_EXTERNAL_ROOT = OpFeatures.isSupport(new int[] { 0 });
    CONFIG_PROTECT_MEDIA = OpFeatures.isSupport(new int[] { 0 });
    CONFIG_REBUILD_MEDIA_VIEW = false;
    PROTECTED_MEDIA_PATH_ARRAY = new String[] { "/DCIM/" };
    FILES_PRESCAN_PROJECTION = new String[] { "_id", "_data", "format", "date_modified" };
    ID_PROJECTION = new String[] { "_id" };
    PLAYLIST_MEMBERS_PROJECTION = new String[] { "playlist_id" };
    ID3_GENRES = new String[] { "Blues", "Classic Rock", "Country", "Dance", "Disco", "Funk", "Grunge", "Hip-Hop", "Jazz", "Metal", "New Age", "Oldies", "Other", "Pop", "R&B", "Rap", "Reggae", "Rock", "Techno", "Industrial", "Alternative", "Ska", "Death Metal", "Pranks", "Soundtrack", "Euro-Techno", "Ambient", "Trip-Hop", "Vocal", "Jazz+Funk", "Fusion", "Trance", "Classical", "Instrumental", "Acid", "House", "Game", "Sound Clip", "Gospel", "Noise", "AlternRock", "Bass", "Soul", "Punk", "Space", "Meditative", "Instrumental Pop", "Instrumental Rock", "Ethnic", "Gothic", "Darkwave", "Techno-Industrial", "Electronic", "Pop-Folk", "Eurodance", "Dream", "Southern Rock", "Comedy", "Cult", "Gangsta", "Top 40", "Christian Rap", "Pop/Funk", "Jungle", "Native American", "Cabaret", "New Wave", "Psychadelic", "Rave", "Showtunes", "Trailer", "Lo-Fi", "Tribal", "Acid Punk", "Acid Jazz", "Polka", "Retro", "Musical", "Rock & Roll", "Hard Rock", "Folk", "Folk-Rock", "National Folk", "Swing", "Fast Fusion", "Bebob", "Latin", "Revival", "Celtic", "Bluegrass", "Avantgarde", "Gothic Rock", "Progressive Rock", "Psychedelic Rock", "Symphonic Rock", "Slow Rock", "Big Band", "Chorus", "Easy Listening", "Acoustic", "Humour", "Speech", "Chanson", "Opera", "Chamber Music", "Sonata", "Symphony", "Booty Bass", "Primus", "Porn Groove", "Satire", "Slow Jam", "Club", "Tango", "Samba", "Folklore", "Ballad", "Power Ballad", "Rhythmic Soul", "Freestyle", "Duet", "Punk Rock", "Drum Solo", "A capella", "Euro-House", "Dance Hall", "Goa", "Drum & Bass", "Club-House", "Hardcore", "Terror", "Indie", "Britpop", null, "Polsk Punk", "Beat", "Christian Gangsta", "Heavy Metal", "Black Metal", "Crossover", "Contemporary Christian", "Christian Rock", "Merengue", "Salsa", "Thrash Metal", "Anime", "JPop", "Synthpop" };
    DEFAULT_RINGTONES = new String[] { "ringtone_default", "ringtone", "ringtone_2" };
    DEFAULT_RINGTONE_COUNT = DEFAULT_RINGTONES.length;
    mNoMediaPaths = new HashMap();
  }
  
  public MediaScanner(Context paramContext, String paramString)
  {
    native_setup();
    this.mContext = paramContext;
    this.mPackageName = paramContext.getPackageName();
    this.mVolumeName = paramString;
    this.mBitmapOptions.inSampleSize = 1;
    this.mBitmapOptions.inJustDecodeBounds = true;
    setDefaultRingtoneFileNames();
    this.mMediaProvider = this.mContext.getContentResolver().acquireContentProviderClient("media");
    if (sLastInternalScanFingerprint == null) {
      sLastInternalScanFingerprint = this.mContext.getSharedPreferences("MediaScanBuild", 0).getString("lastScanFingerprint", new String());
    }
    this.mAudioUri = MediaStore.Audio.Media.getContentUri(paramString);
    this.mVideoUri = MediaStore.Video.Media.getContentUri(paramString);
    this.mImagesUri = MediaStore.Images.Media.getContentUri(paramString);
    this.mThumbsUri = MediaStore.Images.Thumbnails.getContentUri(paramString);
    this.mFilesUri = MediaStore.Files.getContentUri(paramString);
    this.mFilesUriNoNotify = this.mFilesUri.buildUpon().appendQueryParameter("nonotify", "1").build();
    mExternalPath = getExternalPath();
    if (!paramString.equals("internal"))
    {
      this.mProcessPlaylists = true;
      this.mProcessGenres = true;
      this.mPlaylistsUri = MediaStore.Audio.Playlists.getContentUri(paramString);
      paramString = this.mContext.getResources().getConfiguration().locale;
      if (paramString != null)
      {
        paramContext = paramString.getLanguage();
        paramString = paramString.getCountry();
        if (paramContext != null)
        {
          if (paramString == null) {
            break label365;
          }
          setLocale(paramContext + "_" + paramString);
        }
      }
    }
    for (;;)
    {
      this.mCloseGuard.open("close");
      return;
      this.mProcessPlaylists = false;
      this.mProcessGenres = false;
      this.mPlaylistsUri = null;
      break;
      label365:
      setLocale(paramContext);
    }
  }
  
  private void cachePlaylistEntry(String paramString1, String paramString2)
  {
    int j = 1;
    PlaylistEntry localPlaylistEntry = new PlaylistEntry(null);
    int i = paramString1.length();
    while ((i > 0) && (Character.isWhitespace(paramString1.charAt(i - 1)))) {
      i -= 1;
    }
    if (i < 3) {
      return;
    }
    String str = paramString1;
    if (i < paramString1.length()) {
      str = paramString1.substring(0, i);
    }
    char c = str.charAt(0);
    if (c != '/')
    {
      if ((!Character.isLetter(c)) || (str.charAt(1) != ':')) {
        break label178;
      }
      if (str.charAt(2) != '\\') {
        break label172;
      }
      i = j;
    }
    for (;;)
    {
      paramString1 = str;
      if (i == 0) {
        paramString1 = paramString2 + str;
      }
      localPlaylistEntry.path = paramString1;
      this.mPlaylistEntries.add(localPlaylistEntry);
      return;
      i = 1;
      continue;
      label172:
      i = 0;
      continue;
      label178:
      i = 0;
    }
  }
  
  public static void clearMediaPathCache(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1) {}
    try
    {
      mMediaPaths.clear();
      if (paramBoolean2) {
        mNoMediaPaths.clear();
      }
      return;
    }
    finally {}
  }
  
  private boolean deleteNoMediaInProtectedPath()
  {
    boolean bool2 = false;
    Object localObject = new File(mExternalPath + "/.nomedia");
    boolean bool1 = bool2;
    if (localObject != null)
    {
      bool1 = bool2;
      if (((File)localObject).exists())
      {
        if (!((File)localObject).delete()) {
          break label227;
        }
        if (DBG) {
          Log.w("MediaScanner", "deleteNoMediaInProtectedPath: " + localObject + " was protected.");
        }
        bool1 = true;
      }
    }
    int i = 0;
    label101:
    if (i < PROTECTED_MEDIA_PATH_ARRAY.length)
    {
      localObject = mExternalPath + PROTECTED_MEDIA_PATH_ARRAY[i] + ".nomedia";
      File localFile = new File((String)localObject);
      bool2 = bool1;
      if (localFile != null)
      {
        bool2 = bool1;
        if (localFile.exists())
        {
          if (!localFile.delete()) {
            break label259;
          }
          if (DBG) {
            Log.w("MediaScanner", "deleteNoMediaInProtectedPath: " + (String)localObject + " was protected.");
          }
        }
      }
      for (bool2 = true;; bool2 = bool1)
      {
        i += 1;
        bool1 = bool2;
        break label101;
        label227:
        Log.e("MediaScanner", "deleteNoMediaInProtectedPath: failed on removing " + localObject);
        bool1 = bool2;
        break;
        label259:
        Log.e("MediaScanner", "deleteNoMediaInProtectedPath: failed on removing " + (String)localObject);
      }
    }
    return bool1;
  }
  
  private static String getExternalPath()
  {
    return new Environment.UserEnvironment(UserHandle.myUserId()).getExternalStorageDirectory().getAbsolutePath();
  }
  
  private boolean isDrmEnabled()
  {
    String str = SystemProperties.get("drm.service.enabled");
    if (str != null) {
      return str.equals("true");
    }
    return false;
  }
  
  private static boolean isNoMediaFile(String paramString)
  {
    if (new File(paramString).isDirectory()) {
      return false;
    }
    int i = paramString.lastIndexOf('/');
    if ((i >= 0) && (i + 2 < paramString.length()))
    {
      if (paramString.regionMatches(i + 1, "._", 0, 2)) {
        return true;
      }
      if (paramString.regionMatches(true, paramString.length() - 4, ".jpg", 0, 4))
      {
        if ((paramString.regionMatches(true, i + 1, "AlbumArt_{", 0, 10)) || (paramString.regionMatches(true, i + 1, "AlbumArt.", 0, 9))) {
          return true;
        }
        int j = paramString.length() - i - 1;
        if (((j == 17) && (paramString.regionMatches(true, i + 1, "AlbumArtSmall", 0, 13))) || ((j == 10) && (paramString.regionMatches(true, i + 1, "Folder", 0, 6)))) {
          return true;
        }
      }
    }
    return false;
  }
  
  public static boolean isNoMediaPath(String paramString)
  {
    if (paramString == null) {
      return false;
    }
    Object localObject = new File(paramString);
    if ((!CONFIG_NO_MEDIA_FOR_SIZE_ZERO) || (paramString.contains(".nomedia"))) {}
    while (paramString.indexOf("/.") >= 0)
    {
      return true;
      if ((localObject != null) && (((File)localObject).exists()) && (((File)localObject).isFile()) && (((File)localObject).length() == 0L))
      {
        if (DBG_LOGV) {
          Log.v("MediaScanner", "Skip the media type of size 0 file: " + paramString);
        }
        return true;
      }
    }
    int i = paramString.lastIndexOf('/');
    if (i <= 0) {
      return false;
    }
    localObject = paramString.substring(0, i);
    if ((CONFIG_PROTECT_MEDIA) && ((mExternalPath.equals(localObject)) || (isProtectedMediaPath(paramString)))) {
      return false;
    }
    for (;;)
    {
      try
      {
        boolean bool = mNoMediaPaths.containsKey(localObject);
        if (bool) {
          return true;
        }
        if (!mMediaPaths.containsKey(localObject))
        {
          j = 1;
          if (j >= 0)
          {
            int k = paramString.indexOf('/', j);
            i = k;
            if (k > j)
            {
              j = k + 1;
              String str = paramString.substring(0, j) + ".nomedia";
              i = j;
              if (new File(str).exists()) {
                if ((CONFIG_PROTECT_EXTERNAL_ROOT) && ((mExternalPath + "/.nomedia").equals(str)))
                {
                  i = j;
                }
                else
                {
                  mNoMediaPaths.put(localObject, "");
                  return true;
                }
              }
            }
          }
          else
          {
            mMediaPaths.put(localObject, "");
          }
        }
        else
        {
          return isNoMediaFile(paramString);
        }
      }
      finally {}
      int j = i;
    }
  }
  
  public static boolean isProtectedMediaPath(String paramString)
  {
    boolean bool2 = false;
    boolean bool1;
    if ((paramString != null) && (CONFIG_PROTECT_MEDIA))
    {
      if ((!CONFIG_PROTECT_EXTERNAL_ROOT) || ((!paramString.equals(mExternalPath)) && (!paramString.equals(mExternalPath + "/.nomedia")))) {
        break label148;
      }
      if (DBG_LOGV) {
        Log.d("MediaScanner", "isNoMediaPath: we plan to protect root dir " + mExternalPath + "/.nomedia");
      }
      bool1 = true;
    }
    label148:
    do
    {
      for (;;)
      {
        if ((DBG_LOGV) && (bool1)) {
          Log.d("MediaScanner", "isProtectedMediaPath: " + paramString + " is protected.");
        }
        return bool1;
        bool1 = false;
      }
      bool1 = bool2;
    } while (PROTECTED_MEDIA_PATH_ARRAY == null);
    int i = 0;
    for (;;)
    {
      bool1 = bool2;
      if (i >= PROTECTED_MEDIA_PATH_ARRAY.length) {
        break;
      }
      if (paramString.contains(PROTECTED_MEDIA_PATH_ARRAY[i]))
      {
        if ((paramString.indexOf("/.") < 0) || (paramString.endsWith(".nomedia")))
        {
          bool1 = true;
          break;
        }
        bool1 = false;
        break;
      }
      i += 1;
    }
  }
  
  private static boolean isSystemSoundWithMetadata(String paramString)
  {
    return (paramString.startsWith("/system/media/audio/alarms/")) || (paramString.startsWith("/system/media/audio/ringtones/")) || (paramString.startsWith("/system/media/audio/notifications/"));
  }
  
  private boolean matchEntries(long paramLong, String paramString)
  {
    int j = this.mPlaylistEntries.size();
    boolean bool1 = true;
    int i = 0;
    if (i < j)
    {
      PlaylistEntry localPlaylistEntry = (PlaylistEntry)this.mPlaylistEntries.get(i);
      if (localPlaylistEntry.bestmatchlevel == Integer.MAX_VALUE) {}
      for (;;)
      {
        i += 1;
        break;
        boolean bool2 = false;
        if (paramString.equalsIgnoreCase(localPlaylistEntry.path))
        {
          localPlaylistEntry.bestmatchid = paramLong;
          localPlaylistEntry.bestmatchlevel = Integer.MAX_VALUE;
          bool1 = bool2;
        }
        else
        {
          int k = matchPaths(paramString, localPlaylistEntry.path);
          bool1 = bool2;
          if (k > localPlaylistEntry.bestmatchlevel)
          {
            localPlaylistEntry.bestmatchid = paramLong;
            localPlaylistEntry.bestmatchlevel = k;
            bool1 = bool2;
          }
        }
      }
    }
    return bool1;
  }
  
  private int matchPaths(String paramString1, String paramString2)
  {
    int j = 0;
    int k = paramString1.length();
    int n;
    for (int i = paramString2.length();; i = n - 1)
    {
      int m;
      int i2;
      int i1;
      if ((k > 0) && (i > 0))
      {
        m = paramString1.lastIndexOf('/', k - 1);
        n = paramString2.lastIndexOf('/', i - 1);
        i2 = paramString1.lastIndexOf('\\', k - 1);
        i1 = paramString2.lastIndexOf('\\', i - 1);
        if (m <= i2) {
          break label118;
        }
        if (n <= i1) {
          break label125;
        }
        label83:
        if (m >= 0) {
          break label132;
        }
        m = 0;
        label91:
        if (n >= 0) {
          break label141;
        }
        n = 0;
        label99:
        k -= m;
        if (i - n == k) {
          break label150;
        }
      }
      label118:
      label125:
      label132:
      label141:
      label150:
      while (!paramString1.regionMatches(true, m, paramString2, n, k))
      {
        return j;
        m = i2;
        break;
        n = i1;
        break label83;
        m += 1;
        break label91;
        n += 1;
        break label99;
      }
      j += 1;
      k = m - 1;
    }
  }
  
  private final native void native_finalize();
  
  private static final native void native_init();
  
  private final native void native_setup();
  
  private void postscan(String[] paramArrayOfString)
    throws RemoteException
  {
    if (this.mProcessPlaylists) {
      processPlayLists();
    }
    if ((this.mOriginalCount == 0) && (this.mImagesUri.equals(MediaStore.Images.Media.getContentUri("external")))) {
      pruneDeadThumbnailFiles();
    }
    this.mPlayLists.clear();
  }
  
  private void prescan(String paramString, boolean paramBoolean)
    throws RemoteException
  {
    Object localObject2 = null;
    Uri localUri = null;
    Cursor localCursor = null;
    this.mPlayLists.clear();
    String str;
    String[] arrayOfString;
    if (paramString != null)
    {
      str = "_id>? AND _data=?";
      arrayOfString = new String[2];
      arrayOfString[0] = "";
      arrayOfString[1] = paramString;
    }
    for (;;)
    {
      i = 0;
      while (i < DEFAULT_RINGTONE_COUNT)
      {
        this.mDefaultRingtonesSet[i] = wasRingtoneAlreadySet(DEFAULT_RINGTONES[i]);
        i += 1;
      }
      str = "_id>?";
      arrayOfString = new String[1];
      arrayOfString[0] = "";
    }
    this.mDefaultNotificationSet = wasRingtoneAlreadySet("notification_sound");
    this.mDefaultAlarmSet = wasRingtoneAlreadySet("alarm_alert");
    paramString = this.mFilesUri.buildUpon();
    paramString.appendQueryParameter("deletedata", "false");
    MediaBulkDeleter localMediaBulkDeleter = new MediaBulkDeleter(this.mMediaProvider, paramString.build());
    long l1;
    if (paramBoolean)
    {
      l1 = Long.MIN_VALUE;
      paramString = localUri;
    }
    label466:
    do
    {
      do
      {
        try
        {
          localUri = this.mFilesUri.buildUpon().appendQueryParameter("limit", "1000").build();
          localObject2 = localCursor;
          paramString = (String)localObject2;
          arrayOfString[0] = ("" + l1);
          paramString = (String)localObject2;
          if (localObject2 != null)
          {
            paramString = (String)localObject2;
            ((Cursor)localObject2).close();
            paramString = null;
          }
          localCursor = this.mMediaProvider.query(localUri, FILES_PRESCAN_PROJECTION, str, arrayOfString, "_id", null);
          if (localCursor == null) {
            localObject2 = localCursor;
          }
          do
          {
            if (localObject2 != null) {
              ((Cursor)localObject2).close();
            }
            localMediaBulkDeleter.flush();
            this.mOriginalCount = 0;
            paramString = this.mMediaProvider.query(this.mImagesUri, ID_PROJECTION, null, null, null, null);
            if (paramString != null)
            {
              this.mOriginalCount = paramString.getCount();
              paramString.close();
            }
            return;
            localObject2 = localCursor;
            paramString = localCursor;
          } while (localCursor.getCount() == 0);
          do
          {
            do
            {
              localObject2 = localCursor;
              paramString = localCursor;
              if (!localCursor.moveToNext()) {
                break;
              }
              paramString = localCursor;
              l3 = localCursor.getLong(0);
              paramString = localCursor;
              localObject2 = localCursor.getString(1);
              paramString = localCursor;
              i = localCursor.getInt(2);
              paramString = localCursor;
              localCursor.getLong(3);
              l2 = l3;
              l1 = l2;
            } while (localObject2 == null);
            paramString = localCursor;
            paramBoolean = ((String)localObject2).startsWith("/");
            l1 = l2;
          } while (!paramBoolean);
          paramBoolean = false;
          paramString = localCursor;
        }
        finally
        {
          long l3;
          long l2;
          boolean bool;
          if (paramString != null) {
            paramString.close();
          }
          localMediaBulkDeleter.flush();
        }
        try
        {
          bool = Os.access((String)localObject2, OsConstants.F_OK);
          paramBoolean = bool;
        }
        catch (ErrnoException paramString)
        {
          break label466;
        }
        l1 = l2;
      } while (paramBoolean);
      l1 = l2;
      paramString = localCursor;
    } while (MtpConstants.isAbstractObject(i));
    paramString = localCursor;
    MediaFile.MediaFileType localMediaFileType = MediaFile.getFileType((String)localObject2);
    if (localMediaFileType == null) {}
    for (int i = 0;; i = localMediaFileType.fileType)
    {
      l1 = l2;
      paramString = localCursor;
      if (MediaFile.isPlayListFileType(i)) {
        break;
      }
      paramString = localCursor;
      localMediaBulkDeleter.delete(l3);
      l1 = l2;
      paramString = localCursor;
      if (!((String)localObject2).toLowerCase(Locale.US).endsWith("/.nomedia")) {
        break;
      }
      paramString = localCursor;
      localMediaBulkDeleter.flush();
      paramString = localCursor;
      localObject2 = new File((String)localObject2).getParent();
      paramString = localCursor;
      this.mMediaProvider.call("unhide", (String)localObject2, null);
      l1 = l2;
      break;
      paramString = (String)localObject1;
    }
  }
  
  private void processCachedPlaylist(Cursor paramCursor, ContentValues paramContentValues, Uri paramUri)
  {
    paramCursor.moveToPosition(-1);
    while ((paramCursor.moveToNext()) && (!matchEntries(paramCursor.getLong(0), paramCursor.getString(1)))) {}
    int m = this.mPlaylistEntries.size();
    int j = 0;
    int i = 0;
    while (i < m)
    {
      paramCursor = (PlaylistEntry)this.mPlaylistEntries.get(i);
      int k = j;
      if (paramCursor.bestmatchlevel > 0) {}
      try
      {
        paramContentValues.clear();
        paramContentValues.put("play_order", Integer.valueOf(j));
        paramContentValues.put("audio_id", Long.valueOf(paramCursor.bestmatchid));
        this.mMediaProvider.insert(paramUri, paramContentValues);
        k = j + 1;
        i += 1;
        j = k;
      }
      catch (RemoteException paramCursor)
      {
        Log.e("MediaScanner", "RemoteException in MediaScanner.processCachedPlaylist()", paramCursor);
        return;
      }
    }
    this.mPlaylistEntries.clear();
  }
  
  private native void processDirectory(String paramString, MediaScannerClient paramMediaScannerClient);
  
  private native void processFile(String paramString1, String paramString2, MediaScannerClient paramMediaScannerClient);
  
  /* Error */
  private void processM3uPlayList(String paramString1, String paramString2, Uri paramUri, ContentValues paramContentValues, Cursor paramCursor)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 7
    //   3: aconst_null
    //   4: astore 8
    //   6: aconst_null
    //   7: astore 9
    //   9: aload 8
    //   11: astore 6
    //   13: new 861	java/io/File
    //   16: dup
    //   17: aload_1
    //   18: invokespecial 865	java/io/File:<init>	(Ljava/lang/String;)V
    //   21: astore 10
    //   23: aload 9
    //   25: astore_1
    //   26: aload 8
    //   28: astore 6
    //   30: aload 10
    //   32: invokevirtual 868	java/io/File:exists	()Z
    //   35: ifeq +96 -> 131
    //   38: aload 8
    //   40: astore 6
    //   42: new 1196	java/io/BufferedReader
    //   45: dup
    //   46: new 1198	java/io/InputStreamReader
    //   49: dup
    //   50: new 1200	java/io/FileInputStream
    //   53: dup
    //   54: aload 10
    //   56: invokespecial 1203	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   59: invokespecial 1206	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;)V
    //   62: sipush 8192
    //   65: invokespecial 1209	java/io/BufferedReader:<init>	(Ljava/io/Reader;I)V
    //   68: astore_1
    //   69: aload_1
    //   70: invokevirtual 1212	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   73: astore 6
    //   75: aload_0
    //   76: getfield 675	android/media/MediaScanner:mPlaylistEntries	Ljava/util/ArrayList;
    //   79: invokevirtual 1038	java/util/ArrayList:clear	()V
    //   82: aload 6
    //   84: ifnull +38 -> 122
    //   87: aload 6
    //   89: invokevirtual 830	java/lang/String:length	()I
    //   92: ifle +21 -> 113
    //   95: aload 6
    //   97: iconst_0
    //   98: invokevirtual 834	java/lang/String:charAt	(I)C
    //   101: bipush 35
    //   103: if_icmpeq +10 -> 113
    //   106: aload_0
    //   107: aload 6
    //   109: aload_2
    //   110: invokespecial 271	android/media/MediaScanner:cachePlaylistEntry	(Ljava/lang/String;Ljava/lang/String;)V
    //   113: aload_1
    //   114: invokevirtual 1212	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   117: astore 6
    //   119: goto -37 -> 82
    //   122: aload_0
    //   123: aload 5
    //   125: aload 4
    //   127: aload_3
    //   128: invokespecial 1214	android/media/MediaScanner:processCachedPlaylist	(Landroid/database/Cursor;Landroid/content/ContentValues;Landroid/net/Uri;)V
    //   131: aload_1
    //   132: ifnull +7 -> 139
    //   135: aload_1
    //   136: invokevirtual 1215	java/io/BufferedReader:close	()V
    //   139: return
    //   140: astore_1
    //   141: ldc 81
    //   143: ldc_w 1217
    //   146: aload_1
    //   147: invokestatic 1188	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   150: pop
    //   151: return
    //   152: astore_2
    //   153: aload 7
    //   155: astore_1
    //   156: aload_1
    //   157: astore 6
    //   159: ldc 81
    //   161: ldc_w 1217
    //   164: aload_2
    //   165: invokestatic 1188	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   168: pop
    //   169: aload_1
    //   170: ifnull -31 -> 139
    //   173: aload_1
    //   174: invokevirtual 1215	java/io/BufferedReader:close	()V
    //   177: return
    //   178: astore_1
    //   179: ldc 81
    //   181: ldc_w 1217
    //   184: aload_1
    //   185: invokestatic 1188	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   188: pop
    //   189: return
    //   190: astore_1
    //   191: aload 6
    //   193: ifnull +8 -> 201
    //   196: aload 6
    //   198: invokevirtual 1215	java/io/BufferedReader:close	()V
    //   201: aload_1
    //   202: athrow
    //   203: astore_2
    //   204: ldc 81
    //   206: ldc_w 1217
    //   209: aload_2
    //   210: invokestatic 1188	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   213: pop
    //   214: goto -13 -> 201
    //   217: astore_2
    //   218: aload_1
    //   219: astore 6
    //   221: aload_2
    //   222: astore_1
    //   223: goto -32 -> 191
    //   226: astore_2
    //   227: goto -71 -> 156
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	230	0	this	MediaScanner
    //   0	230	1	paramString1	String
    //   0	230	2	paramString2	String
    //   0	230	3	paramUri	Uri
    //   0	230	4	paramContentValues	ContentValues
    //   0	230	5	paramCursor	Cursor
    //   11	209	6	localObject1	Object
    //   1	153	7	localObject2	Object
    //   4	35	8	localObject3	Object
    //   7	17	9	localObject4	Object
    //   21	34	10	localFile	File
    // Exception table:
    //   from	to	target	type
    //   135	139	140	java/io/IOException
    //   13	23	152	java/io/IOException
    //   30	38	152	java/io/IOException
    //   42	69	152	java/io/IOException
    //   173	177	178	java/io/IOException
    //   13	23	190	finally
    //   30	38	190	finally
    //   42	69	190	finally
    //   159	169	190	finally
    //   196	201	203	java/io/IOException
    //   69	82	217	finally
    //   87	113	217	finally
    //   113	119	217	finally
    //   122	131	217	finally
    //   69	82	226	java/io/IOException
    //   87	113	226	java/io/IOException
    //   113	119	226	java/io/IOException
    //   122	131	226	java/io/IOException
  }
  
  private void processPlayList(FileEntry paramFileEntry, Cursor paramCursor)
    throws RemoteException
  {
    String str = paramFileEntry.mPath;
    ContentValues localContentValues = new ContentValues();
    int i = str.lastIndexOf('/');
    if (i < 0) {
      throw new IllegalArgumentException("bad path " + str);
    }
    long l = paramFileEntry.mRowId;
    Object localObject2 = localContentValues.getAsString("name");
    Object localObject1 = localObject2;
    int j;
    if (localObject2 == null)
    {
      localObject2 = localContentValues.getAsString("title");
      localObject1 = localObject2;
      if (localObject2 == null)
      {
        j = str.lastIndexOf('.');
        if (j >= 0) {
          break label237;
        }
        localObject1 = str.substring(i + 1);
      }
    }
    localContentValues.put("name", (String)localObject1);
    localContentValues.put("date_modified", Long.valueOf(paramFileEntry.mLastModified));
    if (l == 0L)
    {
      localContentValues.put("_data", str);
      paramFileEntry = this.mMediaProvider.insert(this.mPlaylistsUri, localContentValues);
      ContentUris.parseId(paramFileEntry);
      paramFileEntry = Uri.withAppendedPath(paramFileEntry, "members");
      label193:
      localObject1 = str.substring(0, i + 1);
      localObject2 = MediaFile.getFileType(str);
      if (localObject2 != null) {
        break label297;
      }
      i = 0;
      label218:
      if (i != 41) {
        break label306;
      }
      processM3uPlayList(str, (String)localObject1, paramFileEntry, localContentValues, paramCursor);
    }
    label237:
    label297:
    label306:
    do
    {
      return;
      localObject1 = str.substring(i + 1, j);
      break;
      paramFileEntry = ContentUris.withAppendedId(this.mPlaylistsUri, l);
      this.mMediaProvider.update(paramFileEntry, localContentValues, null, null);
      paramFileEntry = Uri.withAppendedPath(paramFileEntry, "members");
      this.mMediaProvider.delete(paramFileEntry, null, null);
      break label193;
      i = ((MediaFile.MediaFileType)localObject2).fileType;
      break label218;
      if (i == 42)
      {
        processPlsPlayList(str, (String)localObject1, paramFileEntry, localContentValues, paramCursor);
        return;
      }
    } while (i != 43);
    processWplPlayList(str, (String)localObject1, paramFileEntry, localContentValues, paramCursor);
  }
  
  /* Error */
  private void processPlayLists()
    throws RemoteException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 200	android/media/MediaScanner:mPlayLists	Ljava/util/ArrayList;
    //   4: invokevirtual 1280	java/util/ArrayList:iterator	()Ljava/util/Iterator;
    //   7: astore 4
    //   9: aconst_null
    //   10: astore_2
    //   11: aconst_null
    //   12: astore_1
    //   13: aload_0
    //   14: getfield 192	android/media/MediaScanner:mMediaProvider	Landroid/content/ContentProviderClient;
    //   17: aload_0
    //   18: getfield 177	android/media/MediaScanner:mFilesUri	Landroid/net/Uri;
    //   21: getstatic 335	android/media/MediaScanner:FILES_PRESCAN_PROJECTION	[Ljava/lang/String;
    //   24: ldc_w 1282
    //   27: aconst_null
    //   28: aconst_null
    //   29: aconst_null
    //   30: invokevirtual 1077	android/content/ContentProviderClient:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Landroid/os/CancellationSignal;)Landroid/database/Cursor;
    //   33: astore_3
    //   34: aload_3
    //   35: astore_1
    //   36: aload_3
    //   37: astore_2
    //   38: aload 4
    //   40: invokeinterface 1287 1 0
    //   45: ifeq +57 -> 102
    //   48: aload_3
    //   49: astore_1
    //   50: aload_3
    //   51: astore_2
    //   52: aload 4
    //   54: invokeinterface 1291 1 0
    //   59: checkcast 8	android/media/MediaScanner$FileEntry
    //   62: astore 5
    //   64: aload_3
    //   65: astore_1
    //   66: aload_3
    //   67: astore_2
    //   68: aload 5
    //   70: getfield 1294	android/media/MediaScanner$FileEntry:mLastModifiedChanged	Z
    //   73: ifeq -39 -> 34
    //   76: aload_3
    //   77: astore_1
    //   78: aload_3
    //   79: astore_2
    //   80: aload_0
    //   81: aload 5
    //   83: aload_3
    //   84: invokespecial 1296	android/media/MediaScanner:processPlayList	(Landroid/media/MediaScanner$FileEntry;Landroid/database/Cursor;)V
    //   87: goto -53 -> 34
    //   90: astore_2
    //   91: aload_1
    //   92: ifnull +9 -> 101
    //   95: aload_1
    //   96: invokeinterface 1071 1 0
    //   101: return
    //   102: aload_3
    //   103: ifnull -2 -> 101
    //   106: aload_3
    //   107: invokeinterface 1071 1 0
    //   112: return
    //   113: astore_1
    //   114: aload_2
    //   115: ifnull +9 -> 124
    //   118: aload_2
    //   119: invokeinterface 1071 1 0
    //   124: aload_1
    //   125: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	126	0	this	MediaScanner
    //   12	84	1	localObject1	Object
    //   113	12	1	localObject2	Object
    //   10	70	2	localObject3	Object
    //   90	29	2	localRemoteException	RemoteException
    //   33	74	3	localCursor	Cursor
    //   7	46	4	localIterator	Iterator
    //   62	20	5	localFileEntry	FileEntry
    // Exception table:
    //   from	to	target	type
    //   13	34	90	android/os/RemoteException
    //   38	48	90	android/os/RemoteException
    //   52	64	90	android/os/RemoteException
    //   68	76	90	android/os/RemoteException
    //   80	87	90	android/os/RemoteException
    //   13	34	113	finally
    //   38	48	113	finally
    //   52	64	113	finally
    //   68	76	113	finally
    //   80	87	113	finally
  }
  
  /* Error */
  private void processPlsPlayList(String paramString1, String paramString2, Uri paramUri, ContentValues paramContentValues, Cursor paramCursor)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 8
    //   3: aconst_null
    //   4: astore 9
    //   6: aconst_null
    //   7: astore 10
    //   9: aload 9
    //   11: astore 7
    //   13: new 861	java/io/File
    //   16: dup
    //   17: aload_1
    //   18: invokespecial 865	java/io/File:<init>	(Ljava/lang/String;)V
    //   21: astore 11
    //   23: aload 10
    //   25: astore_1
    //   26: aload 9
    //   28: astore 7
    //   30: aload 11
    //   32: invokevirtual 868	java/io/File:exists	()Z
    //   35: ifeq +109 -> 144
    //   38: aload 9
    //   40: astore 7
    //   42: new 1196	java/io/BufferedReader
    //   45: dup
    //   46: new 1198	java/io/InputStreamReader
    //   49: dup
    //   50: new 1200	java/io/FileInputStream
    //   53: dup
    //   54: aload 11
    //   56: invokespecial 1203	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   59: invokespecial 1206	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;)V
    //   62: sipush 8192
    //   65: invokespecial 1209	java/io/BufferedReader:<init>	(Ljava/io/Reader;I)V
    //   68: astore_1
    //   69: aload_1
    //   70: invokevirtual 1212	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   73: astore 7
    //   75: aload_0
    //   76: getfield 675	android/media/MediaScanner:mPlaylistEntries	Ljava/util/ArrayList;
    //   79: invokevirtual 1038	java/util/ArrayList:clear	()V
    //   82: aload 7
    //   84: ifnull +51 -> 135
    //   87: aload 7
    //   89: ldc_w 1298
    //   92: invokevirtual 994	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   95: ifeq +31 -> 126
    //   98: aload 7
    //   100: bipush 61
    //   102: invokevirtual 1300	java/lang/String:indexOf	(I)I
    //   105: istore 6
    //   107: iload 6
    //   109: ifle +17 -> 126
    //   112: aload_0
    //   113: aload 7
    //   115: iload 6
    //   117: iconst_1
    //   118: iadd
    //   119: invokevirtual 1240	java/lang/String:substring	(I)Ljava/lang/String;
    //   122: aload_2
    //   123: invokespecial 271	android/media/MediaScanner:cachePlaylistEntry	(Ljava/lang/String;Ljava/lang/String;)V
    //   126: aload_1
    //   127: invokevirtual 1212	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   130: astore 7
    //   132: goto -50 -> 82
    //   135: aload_0
    //   136: aload 5
    //   138: aload 4
    //   140: aload_3
    //   141: invokespecial 1214	android/media/MediaScanner:processCachedPlaylist	(Landroid/database/Cursor;Landroid/content/ContentValues;Landroid/net/Uri;)V
    //   144: aload_1
    //   145: ifnull +7 -> 152
    //   148: aload_1
    //   149: invokevirtual 1215	java/io/BufferedReader:close	()V
    //   152: return
    //   153: astore_1
    //   154: ldc 81
    //   156: ldc_w 1302
    //   159: aload_1
    //   160: invokestatic 1188	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   163: pop
    //   164: return
    //   165: astore_2
    //   166: aload 8
    //   168: astore_1
    //   169: aload_1
    //   170: astore 7
    //   172: ldc 81
    //   174: ldc_w 1302
    //   177: aload_2
    //   178: invokestatic 1188	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   181: pop
    //   182: aload_1
    //   183: ifnull -31 -> 152
    //   186: aload_1
    //   187: invokevirtual 1215	java/io/BufferedReader:close	()V
    //   190: return
    //   191: astore_1
    //   192: ldc 81
    //   194: ldc_w 1302
    //   197: aload_1
    //   198: invokestatic 1188	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   201: pop
    //   202: return
    //   203: astore_1
    //   204: aload 7
    //   206: ifnull +8 -> 214
    //   209: aload 7
    //   211: invokevirtual 1215	java/io/BufferedReader:close	()V
    //   214: aload_1
    //   215: athrow
    //   216: astore_2
    //   217: ldc 81
    //   219: ldc_w 1302
    //   222: aload_2
    //   223: invokestatic 1188	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   226: pop
    //   227: goto -13 -> 214
    //   230: astore_2
    //   231: aload_1
    //   232: astore 7
    //   234: aload_2
    //   235: astore_1
    //   236: goto -32 -> 204
    //   239: astore_2
    //   240: goto -71 -> 169
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	243	0	this	MediaScanner
    //   0	243	1	paramString1	String
    //   0	243	2	paramString2	String
    //   0	243	3	paramUri	Uri
    //   0	243	4	paramContentValues	ContentValues
    //   0	243	5	paramCursor	Cursor
    //   105	14	6	i	int
    //   11	222	7	localObject1	Object
    //   1	166	8	localObject2	Object
    //   4	35	9	localObject3	Object
    //   7	17	10	localObject4	Object
    //   21	34	11	localFile	File
    // Exception table:
    //   from	to	target	type
    //   148	152	153	java/io/IOException
    //   13	23	165	java/io/IOException
    //   30	38	165	java/io/IOException
    //   42	69	165	java/io/IOException
    //   186	190	191	java/io/IOException
    //   13	23	203	finally
    //   30	38	203	finally
    //   42	69	203	finally
    //   172	182	203	finally
    //   209	214	216	java/io/IOException
    //   69	82	230	finally
    //   87	107	230	finally
    //   112	126	230	finally
    //   126	132	230	finally
    //   135	144	230	finally
    //   69	82	239	java/io/IOException
    //   87	107	239	java/io/IOException
    //   112	126	239	java/io/IOException
    //   126	132	239	java/io/IOException
    //   135	144	239	java/io/IOException
  }
  
  /* Error */
  private void processWplPlayList(String paramString1, String paramString2, Uri paramUri, ContentValues paramContentValues, Cursor paramCursor)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 7
    //   3: aconst_null
    //   4: astore 8
    //   6: aconst_null
    //   7: astore 9
    //   9: aconst_null
    //   10: astore 10
    //   12: aload 9
    //   14: astore 6
    //   16: new 861	java/io/File
    //   19: dup
    //   20: aload_1
    //   21: invokespecial 865	java/io/File:<init>	(Ljava/lang/String;)V
    //   24: astore 11
    //   26: aload 10
    //   28: astore_1
    //   29: aload 9
    //   31: astore 6
    //   33: aload 11
    //   35: invokevirtual 868	java/io/File:exists	()Z
    //   38: ifeq +58 -> 96
    //   41: aload 9
    //   43: astore 6
    //   45: new 1200	java/io/FileInputStream
    //   48: dup
    //   49: aload 11
    //   51: invokespecial 1203	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   54: astore_1
    //   55: aload_0
    //   56: getfield 675	android/media/MediaScanner:mPlaylistEntries	Ljava/util/ArrayList;
    //   59: invokevirtual 1038	java/util/ArrayList:clear	()V
    //   62: aload_1
    //   63: ldc_w 1306
    //   66: invokestatic 1312	android/util/Xml:findEncodingByName	(Ljava/lang/String;)Landroid/util/Xml$Encoding;
    //   69: new 20	android/media/MediaScanner$WplHandler
    //   72: dup
    //   73: aload_0
    //   74: aload_2
    //   75: aload_3
    //   76: aload 5
    //   78: invokespecial 1315	android/media/MediaScanner$WplHandler:<init>	(Landroid/media/MediaScanner;Ljava/lang/String;Landroid/net/Uri;Landroid/database/Cursor;)V
    //   81: invokevirtual 1319	android/media/MediaScanner$WplHandler:getContentHandler	()Lorg/xml/sax/ContentHandler;
    //   84: invokestatic 1323	android/util/Xml:parse	(Ljava/io/InputStream;Landroid/util/Xml$Encoding;Lorg/xml/sax/ContentHandler;)V
    //   87: aload_0
    //   88: aload 5
    //   90: aload 4
    //   92: aload_3
    //   93: invokespecial 1214	android/media/MediaScanner:processCachedPlaylist	(Landroid/database/Cursor;Landroid/content/ContentValues;Landroid/net/Uri;)V
    //   96: aload_1
    //   97: ifnull +7 -> 104
    //   100: aload_1
    //   101: invokevirtual 1324	java/io/FileInputStream:close	()V
    //   104: return
    //   105: astore_1
    //   106: ldc 81
    //   108: ldc_w 1326
    //   111: aload_1
    //   112: invokestatic 1188	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   115: pop
    //   116: return
    //   117: astore_2
    //   118: aload 7
    //   120: astore_1
    //   121: aload_1
    //   122: astore 6
    //   124: aload_2
    //   125: invokevirtual 1329	java/io/IOException:printStackTrace	()V
    //   128: aload_1
    //   129: ifnull -25 -> 104
    //   132: aload_1
    //   133: invokevirtual 1324	java/io/FileInputStream:close	()V
    //   136: return
    //   137: astore_1
    //   138: ldc 81
    //   140: ldc_w 1326
    //   143: aload_1
    //   144: invokestatic 1188	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   147: pop
    //   148: return
    //   149: astore_2
    //   150: aload 8
    //   152: astore_1
    //   153: aload_1
    //   154: astore 6
    //   156: aload_2
    //   157: invokevirtual 1330	org/xml/sax/SAXException:printStackTrace	()V
    //   160: aload_1
    //   161: ifnull -57 -> 104
    //   164: aload_1
    //   165: invokevirtual 1324	java/io/FileInputStream:close	()V
    //   168: return
    //   169: astore_1
    //   170: ldc 81
    //   172: ldc_w 1326
    //   175: aload_1
    //   176: invokestatic 1188	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   179: pop
    //   180: return
    //   181: astore_1
    //   182: aload 6
    //   184: ifnull +8 -> 192
    //   187: aload 6
    //   189: invokevirtual 1324	java/io/FileInputStream:close	()V
    //   192: aload_1
    //   193: athrow
    //   194: astore_2
    //   195: ldc 81
    //   197: ldc_w 1326
    //   200: aload_2
    //   201: invokestatic 1188	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   204: pop
    //   205: goto -13 -> 192
    //   208: astore_2
    //   209: aload_1
    //   210: astore 6
    //   212: aload_2
    //   213: astore_1
    //   214: goto -32 -> 182
    //   217: astore_2
    //   218: goto -65 -> 153
    //   221: astore_2
    //   222: goto -101 -> 121
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	225	0	this	MediaScanner
    //   0	225	1	paramString1	String
    //   0	225	2	paramString2	String
    //   0	225	3	paramUri	Uri
    //   0	225	4	paramContentValues	ContentValues
    //   0	225	5	paramCursor	Cursor
    //   14	197	6	localObject1	Object
    //   1	118	7	localObject2	Object
    //   4	147	8	localObject3	Object
    //   7	35	9	localObject4	Object
    //   10	17	10	localObject5	Object
    //   24	26	11	localFile	File
    // Exception table:
    //   from	to	target	type
    //   100	104	105	java/io/IOException
    //   16	26	117	java/io/IOException
    //   33	41	117	java/io/IOException
    //   45	55	117	java/io/IOException
    //   132	136	137	java/io/IOException
    //   16	26	149	org/xml/sax/SAXException
    //   33	41	149	org/xml/sax/SAXException
    //   45	55	149	org/xml/sax/SAXException
    //   164	168	169	java/io/IOException
    //   16	26	181	finally
    //   33	41	181	finally
    //   45	55	181	finally
    //   124	128	181	finally
    //   156	160	181	finally
    //   187	192	194	java/io/IOException
    //   55	96	208	finally
    //   55	96	217	org/xml/sax/SAXException
    //   55	96	221	java/io/IOException
  }
  
  private void pruneDeadThumbnailFiles()
  {
    Object localObject5 = new HashSet();
    Object localObject4 = new File("/sdcard/DCIM/.thumbnails").list();
    Object localObject3 = null;
    Cursor localCursor2 = null;
    Object localObject1 = localObject4;
    if (localObject4 == null) {
      localObject1 = new String[0];
    }
    int i = 0;
    while (i < localObject1.length)
    {
      ((HashSet)localObject5).add("/sdcard/DCIM/.thumbnails" + "/" + localObject1[i]);
      i += 1;
    }
    localObject1 = localCursor2;
    try
    {
      localCursor2 = this.mMediaProvider.query(this.mThumbsUri, new String[] { "_data" }, null, null, null, null);
      localObject1 = localCursor2;
      localObject3 = localCursor2;
      Log.v("MediaScanner", "pruneDeadThumbnailFiles... " + localCursor2);
      if (localCursor2 != null)
      {
        localObject1 = localCursor2;
        localObject3 = localCursor2;
        if (localCursor2.moveToFirst()) {
          do
          {
            localObject1 = localCursor2;
            localObject3 = localCursor2;
            ((HashSet)localObject5).remove(localCursor2.getString(0));
            localObject1 = localCursor2;
            localObject3 = localCursor2;
          } while (localCursor2.moveToNext());
        }
      }
      localObject1 = localCursor2;
      localObject3 = localCursor2;
      localObject4 = ((Iterable)localObject5).iterator();
      for (;;)
      {
        localObject1 = localCursor2;
        localObject3 = localCursor2;
        if (!((Iterator)localObject4).hasNext()) {
          break;
        }
        localObject1 = localCursor2;
        localObject3 = localCursor2;
        localObject5 = (String)((Iterator)localObject4).next();
        localObject1 = localCursor2;
        localObject3 = localCursor2;
        if (DBG_LOGV)
        {
          localObject1 = localCursor2;
          localObject3 = localCursor2;
          Log.v("MediaScanner", "fileToDelete is " + (String)localObject5);
        }
        localObject1 = localCursor2;
        localObject3 = localCursor2;
        try
        {
          new File((String)localObject5).delete();
        }
        catch (SecurityException localSecurityException) {}
      }
      localCursor1 = localCursor2;
      localObject3 = localCursor2;
      Log.v("MediaScanner", "/pruneDeadThumbnailFiles... " + localCursor2);
      if (localCursor2 != null) {
        localCursor2.close();
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Cursor localCursor1;
      while (localCursor1 == null) {}
      localCursor1.close();
      return;
    }
    finally
    {
      if (localObject3 != null) {
        ((Cursor)localObject3).close();
      }
    }
  }
  
  private void releaseResources()
  {
    if (this.mDrmManagerClient != null)
    {
      this.mDrmManagerClient.close();
      this.mDrmManagerClient = null;
    }
  }
  
  private void setDefaultRingtoneFileNames()
  {
    String str1 = SystemProperties.get("ro.config.ringtone");
    this.mDefaultRingtoneFilenames = new String[DEFAULT_RINGTONE_COUNT];
    this.mDefaultRingtonesSet = new boolean[DEFAULT_RINGTONE_COUNT];
    int i = 0;
    while (i < DEFAULT_RINGTONE_COUNT)
    {
      String str2 = SystemProperties.get("ro.config." + DEFAULT_RINGTONES[i], str1);
      this.mDefaultRingtoneFilenames[i] = str2;
      if (DBG_LOGV) {
        Log.v("MediaScanner", "setDefaultRingtoneFileNames(" + i + "/" + DEFAULT_RINGTONE_COUNT + "): " + DEFAULT_RINGTONES[i] + " = " + str2);
      }
      i += 1;
    }
    this.mDefaultNotificationFilename = SystemProperties.get("ro.config.notification_sound");
    this.mDefaultAlarmAlertFilename = SystemProperties.get("ro.config.alarm_alert");
    if (OpFeatures.isSupport(new int[] { 6 })) {
      this.mDefaultMmsNotificationFilename = SystemProperties.get("ro.config.mms_notification", this.mDefaultNotificationFilename);
    }
  }
  
  private native void setLocale(String paramString);
  
  private String settingSetIndicatorName(String paramString)
  {
    return paramString + "_set";
  }
  
  private boolean wasRingtoneAlreadySet(String paramString)
  {
    boolean bool = false;
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    paramString = settingSetIndicatorName(paramString);
    try
    {
      int i = Settings.System.getInt(localContentResolver, paramString);
      if (i != 0) {
        bool = true;
      }
      return bool;
    }
    catch (Settings.SettingNotFoundException paramString) {}
    return false;
  }
  
  public void close()
  {
    this.mCloseGuard.close();
    if (this.mClosed.compareAndSet(false, true))
    {
      this.mMediaProvider.close();
      native_finalize();
    }
  }
  
  public native byte[] extractAlbumArt(FileDescriptor paramFileDescriptor);
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      this.mCloseGuard.warnIfOpen();
      close();
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  FileEntry makeEntryFor(String paramString)
  {
    localObject2 = null;
    localObject1 = null;
    try
    {
      Cursor localCursor = this.mMediaProvider.query(this.mFilesUriNoNotify, FILES_PRESCAN_PROJECTION, "_data=?", new String[] { paramString }, null, null);
      localObject1 = localCursor;
      localObject2 = localCursor;
      if (localCursor.moveToFirst())
      {
        localObject1 = localCursor;
        localObject2 = localCursor;
        long l = localCursor.getLong(0);
        localObject1 = localCursor;
        localObject2 = localCursor;
        int i = localCursor.getInt(2);
        localObject1 = localCursor;
        localObject2 = localCursor;
        paramString = new FileEntry(l, paramString, localCursor.getLong(3), i);
        if (localCursor != null) {
          localCursor.close();
        }
        return paramString;
      }
      if (localCursor != null) {
        localCursor.close();
      }
    }
    catch (RemoteException paramString)
    {
      for (;;)
      {
        if (localObject1 != null) {
          ((Cursor)localObject1).close();
        }
      }
    }
    finally
    {
      if (localObject2 == null) {
        break label171;
      }
      ((Cursor)localObject2).close();
    }
    return null;
  }
  
  /* Error */
  public void scanDirectories(String[] paramArrayOfString)
  {
    // Byte code:
    //   0: getstatic 317	android/media/MediaScanner:CONFIG_PROTECT_MEDIA	Z
    //   3: ifeq +50 -> 53
    //   6: aload_0
    //   7: invokespecial 1421	android/media/MediaScanner:deleteNoMediaInProtectedPath	()Z
    //   10: ifne +10 -> 20
    //   13: aload_0
    //   14: getfield 225	android/media/MediaScanner:mCheck_All_Again	Z
    //   17: ifeq +36 -> 53
    //   20: getstatic 173	android/media/MediaScanner:mExternalPath	Ljava/lang/String;
    //   23: aload_1
    //   24: iconst_0
    //   25: aaload
    //   26: invokevirtual 775	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   29: ifeq +83 -> 112
    //   32: aload_0
    //   33: iconst_1
    //   34: putfield 225	android/media/MediaScanner:mCheck_All_Again	Z
    //   37: aload_0
    //   38: getfield 225	android/media/MediaScanner:mCheck_All_Again	Z
    //   41: ifeq +12 -> 53
    //   44: ldc 81
    //   46: ldc_w 1423
    //   49: invokestatic 1426	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   52: pop
    //   53: invokestatic 1429	java/lang/System:currentTimeMillis	()J
    //   56: lstore_3
    //   57: aload_0
    //   58: aconst_null
    //   59: iconst_1
    //   60: invokespecial 1431	android/media/MediaScanner:prescan	(Ljava/lang/String;Z)V
    //   63: invokestatic 1429	java/lang/System:currentTimeMillis	()J
    //   66: lstore 5
    //   68: aload_0
    //   69: new 1433	android/media/MediaInserter
    //   72: dup
    //   73: aload_0
    //   74: getfield 192	android/media/MediaScanner:mMediaProvider	Landroid/content/ContentProviderClient;
    //   77: sipush 500
    //   80: invokespecial 1436	android/media/MediaInserter:<init>	(Landroid/content/ContentProviderClient;I)V
    //   83: putfield 188	android/media/MediaScanner:mMediaInserter	Landroid/media/MediaInserter;
    //   86: iconst_0
    //   87: istore_2
    //   88: iload_2
    //   89: aload_1
    //   90: arraylength
    //   91: if_icmpge +45 -> 136
    //   94: aload_0
    //   95: aload_1
    //   96: iload_2
    //   97: aaload
    //   98: aload_0
    //   99: getfield 680	android/media/MediaScanner:mClient	Landroid/media/MediaScanner$MyMediaScannerClient;
    //   102: invokespecial 1438	android/media/MediaScanner:processDirectory	(Ljava/lang/String;Landroid/media/MediaScannerClient;)V
    //   105: iload_2
    //   106: iconst_1
    //   107: iadd
    //   108: istore_2
    //   109: goto -21 -> 88
    //   112: aload_0
    //   113: iconst_0
    //   114: putfield 225	android/media/MediaScanner:mCheck_All_Again	Z
    //   117: goto -80 -> 37
    //   120: astore_1
    //   121: ldc 81
    //   123: ldc_w 1440
    //   126: aload_1
    //   127: invokestatic 1188	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   130: pop
    //   131: aload_0
    //   132: invokespecial 1442	android/media/MediaScanner:releaseResources	()V
    //   135: return
    //   136: aload_0
    //   137: getfield 188	android/media/MediaScanner:mMediaInserter	Landroid/media/MediaInserter;
    //   140: invokevirtual 1445	android/media/MediaInserter:flushAll	()V
    //   143: aload_0
    //   144: aconst_null
    //   145: putfield 188	android/media/MediaScanner:mMediaInserter	Landroid/media/MediaInserter;
    //   148: invokestatic 1429	java/lang/System:currentTimeMillis	()J
    //   151: lstore 7
    //   153: aload_0
    //   154: aload_1
    //   155: invokespecial 1447	android/media/MediaScanner:postscan	([Ljava/lang/String;)V
    //   158: invokestatic 1429	java/lang/System:currentTimeMillis	()J
    //   161: lstore 9
    //   163: getstatic 300	android/media/MediaScanner:DBG_LOGV	Z
    //   166: ifeq +145 -> 311
    //   169: ldc 81
    //   171: new 806	java/lang/StringBuilder
    //   174: dup
    //   175: invokespecial 807	java/lang/StringBuilder:<init>	()V
    //   178: ldc_w 1449
    //   181: invokevirtual 811	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   184: lload 5
    //   186: lload_3
    //   187: lsub
    //   188: invokevirtual 1067	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   191: ldc_w 1451
    //   194: invokevirtual 811	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   197: invokevirtual 816	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   200: invokestatic 982	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   203: pop
    //   204: ldc 81
    //   206: new 806	java/lang/StringBuilder
    //   209: dup
    //   210: invokespecial 807	java/lang/StringBuilder:<init>	()V
    //   213: ldc_w 1453
    //   216: invokevirtual 811	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   219: lload 7
    //   221: lload 5
    //   223: lsub
    //   224: invokevirtual 1067	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   227: ldc_w 1451
    //   230: invokevirtual 811	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   233: invokevirtual 816	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   236: invokestatic 982	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   239: pop
    //   240: ldc 81
    //   242: new 806	java/lang/StringBuilder
    //   245: dup
    //   246: invokespecial 807	java/lang/StringBuilder:<init>	()V
    //   249: ldc_w 1455
    //   252: invokevirtual 811	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   255: lload 9
    //   257: lload 7
    //   259: lsub
    //   260: invokevirtual 1067	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   263: ldc_w 1451
    //   266: invokevirtual 811	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   269: invokevirtual 816	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   272: invokestatic 982	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   275: pop
    //   276: ldc 81
    //   278: new 806	java/lang/StringBuilder
    //   281: dup
    //   282: invokespecial 807	java/lang/StringBuilder:<init>	()V
    //   285: ldc_w 1457
    //   288: invokevirtual 811	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   291: lload 9
    //   293: lload_3
    //   294: lsub
    //   295: invokevirtual 1067	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   298: ldc_w 1451
    //   301: invokevirtual 811	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   304: invokevirtual 816	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   307: invokestatic 982	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   310: pop
    //   311: aload_0
    //   312: invokespecial 1442	android/media/MediaScanner:releaseResources	()V
    //   315: return
    //   316: astore_1
    //   317: ldc 81
    //   319: ldc_w 1459
    //   322: aload_1
    //   323: invokestatic 1188	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   326: pop
    //   327: aload_0
    //   328: invokespecial 1442	android/media/MediaScanner:releaseResources	()V
    //   331: return
    //   332: astore_1
    //   333: ldc 81
    //   335: ldc_w 1461
    //   338: aload_1
    //   339: invokestatic 1188	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   342: pop
    //   343: aload_0
    //   344: invokespecial 1442	android/media/MediaScanner:releaseResources	()V
    //   347: return
    //   348: astore_1
    //   349: aload_0
    //   350: invokespecial 1442	android/media/MediaScanner:releaseResources	()V
    //   353: aload_1
    //   354: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	355	0	this	MediaScanner
    //   0	355	1	paramArrayOfString	String[]
    //   87	22	2	i	int
    //   56	238	3	l1	long
    //   66	156	5	l2	long
    //   151	107	7	l3	long
    //   161	131	9	l4	long
    // Exception table:
    //   from	to	target	type
    //   0	20	120	android/database/SQLException
    //   20	37	120	android/database/SQLException
    //   37	53	120	android/database/SQLException
    //   53	86	120	android/database/SQLException
    //   88	105	120	android/database/SQLException
    //   112	117	120	android/database/SQLException
    //   136	311	120	android/database/SQLException
    //   0	20	316	android/os/RemoteException
    //   20	37	316	android/os/RemoteException
    //   37	53	316	android/os/RemoteException
    //   53	86	316	android/os/RemoteException
    //   88	105	316	android/os/RemoteException
    //   112	117	316	android/os/RemoteException
    //   136	311	316	android/os/RemoteException
    //   0	20	332	java/lang/UnsupportedOperationException
    //   20	37	332	java/lang/UnsupportedOperationException
    //   37	53	332	java/lang/UnsupportedOperationException
    //   53	86	332	java/lang/UnsupportedOperationException
    //   88	105	332	java/lang/UnsupportedOperationException
    //   112	117	332	java/lang/UnsupportedOperationException
    //   136	311	332	java/lang/UnsupportedOperationException
    //   0	20	348	finally
    //   20	37	348	finally
    //   37	53	348	finally
    //   53	86	348	finally
    //   88	105	348	finally
    //   112	117	348	finally
    //   121	131	348	finally
    //   136	311	348	finally
    //   317	327	348	finally
    //   333	343	348	finally
  }
  
  public void scanMtpFile(String paramString, int paramInt1, int paramInt2)
  {
    Object localObject7 = MediaFile.getFileType(paramString);
    int i;
    Object localObject6;
    long l1;
    label51:
    Object localObject3;
    Object localObject4;
    Object localObject5;
    if (localObject7 == null)
    {
      i = 0;
      localObject6 = new File(paramString);
      l1 = ((File)localObject6).lastModified() / 1000L;
      if ((!MediaFile.isAudioFileType(i)) && (!MediaFile.isVideoFileType(i))) {
        break label188;
      }
      this.mMtpObjectHandle = paramInt1;
      localObject3 = null;
      localObject4 = null;
      localObject5 = null;
      localObject2 = localObject3;
      localObject1 = localObject4;
    }
    for (;;)
    {
      try
      {
        if (MediaFile.isPlayListFileType(i))
        {
          localObject2 = localObject3;
          localObject1 = localObject4;
          prescan(null, true);
          localObject2 = localObject3;
          localObject1 = localObject4;
          localObject6 = makeEntryFor(paramString);
          paramString = (String)localObject5;
          if (localObject6 != null)
          {
            localObject2 = localObject3;
            localObject1 = localObject4;
            paramString = this.mMediaProvider.query(this.mFilesUri, FILES_PRESCAN_PROJECTION, null, null, null, null);
            localObject2 = paramString;
            localObject1 = paramString;
            processPlayList((FileEntry)localObject6, paramString);
          }
          this.mMtpObjectHandle = 0;
          if (paramString != null) {
            paramString.close();
          }
          releaseResources();
          return;
          i = ((MediaFile.MediaFileType)localObject7).fileType;
          break;
          label188:
          if ((MediaFile.isImageFileType(i)) || (MediaFile.isPlayListFileType(i)) || (MediaFile.isDrmFileType(i))) {
            break label51;
          }
          paramString = new ContentValues();
          paramString.put("_size", Long.valueOf(((File)localObject6).length()));
          paramString.put("date_modified", Long.valueOf(l1));
          try
          {
            localObject1 = Integer.toString(paramInt1);
            this.mMediaProvider.update(MediaStore.Files.getMtpObjectsUri(this.mVolumeName), paramString, "_id=?", new String[] { localObject1 });
            return;
          }
          catch (RemoteException paramString)
          {
            Log.e("MediaScanner", "RemoteException in scanMtpFile", paramString);
            return;
          }
        }
        localObject2 = localObject3;
        localObject1 = localObject4;
        prescan(paramString, false);
        localObject2 = localObject3;
        localObject1 = localObject4;
        localMyMediaScannerClient = this.mClient;
        localObject2 = localObject3;
        localObject1 = localObject4;
        localObject7 = ((MediaFile.MediaFileType)localObject7).mimeType;
        localObject2 = localObject3;
        localObject1 = localObject4;
        l2 = ((File)localObject6).length();
        if (paramInt2 != 12289) {
          continue;
        }
        bool = true;
      }
      catch (RemoteException paramString)
      {
        MyMediaScannerClient localMyMediaScannerClient;
        long l2;
        localObject1 = localObject2;
        Log.e("MediaScanner", "RemoteException in MediaScanner.scanFile()", paramString);
        return;
        boolean bool = false;
        continue;
      }
      finally
      {
        this.mMtpObjectHandle = 0;
        if (localObject1 == null) {
          continue;
        }
        ((Cursor)localObject1).close();
        releaseResources();
      }
      localObject2 = localObject3;
      localObject1 = localObject4;
      localMyMediaScannerClient.doScanFile(paramString, (String)localObject7, l1, l2, bool, true, isNoMediaPath(paramString));
      paramString = (String)localObject5;
    }
  }
  
  public Uri scanSingleFile(String paramString1, String paramString2)
  {
    try
    {
      prescan(paramString1, true);
      File localFile = new File(paramString1);
      boolean bool = localFile.exists();
      if (!bool) {
        return null;
      }
      long l = localFile.lastModified() / 1000L;
      paramString1 = this.mClient.doScanFile(paramString1, paramString2, l, localFile.length(), false, true, isNoMediaPath(paramString1));
      return paramString1;
    }
    catch (RemoteException paramString1)
    {
      Log.e("MediaScanner", "RemoteException in MediaScanner.scanFile()", paramString1);
      return null;
    }
    finally
    {
      releaseResources();
    }
  }
  
  public void updateOnlineConfigs(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, Object[] paramArrayOfObject)
  {
    int j = 1;
    CONFIG_NO_MEDIA_FOR_SIZE_ZERO = paramBoolean1;
    CONFIG_PROTECT_EXTERNAL_ROOT = paramBoolean2;
    CONFIG_PROTECT_MEDIA = paramBoolean3;
    CONFIG_REBUILD_MEDIA_VIEW = paramBoolean4;
    PROTECTED_MEDIA_PATH_ARRAY = (String[])Arrays.copyOf(paramArrayOfObject, paramArrayOfObject.length, String[].class);
    if (DBG)
    {
      paramArrayOfObject = new StringBuilder().append("updateOnlineConfigs : {(");
      if (!CONFIG_NO_MEDIA_FOR_SIZE_ZERO) {
        break label178;
      }
      i = 1;
      paramArrayOfObject = paramArrayOfObject.append(i).append(", ");
      if (!CONFIG_PROTECT_EXTERNAL_ROOT) {
        break label184;
      }
      i = 1;
      label91:
      paramArrayOfObject = paramArrayOfObject.append(i).append(", ");
      if (!CONFIG_PROTECT_MEDIA) {
        break label190;
      }
      i = 1;
      label115:
      paramArrayOfObject = paramArrayOfObject.append(i).append(", ");
      if (!CONFIG_REBUILD_MEDIA_VIEW) {
        break label196;
      }
    }
    label178:
    label184:
    label190:
    label196:
    for (int i = j;; i = 0)
    {
      Log.d("MediaScanner", i + ") , " + Arrays.toString(PROTECTED_MEDIA_PATH_ARRAY) + "}");
      return;
      i = 0;
      break;
      i = 0;
      break label91;
      i = 0;
      break label115;
    }
  }
  
  private static class FileEntry
  {
    int mFormat;
    long mLastModified;
    boolean mLastModifiedChanged;
    String mPath;
    long mRowId;
    
    FileEntry(long paramLong1, String paramString, long paramLong2, int paramInt)
    {
      this.mRowId = paramLong1;
      this.mPath = paramString;
      this.mLastModified = paramLong2;
      this.mFormat = paramInt;
      this.mLastModifiedChanged = false;
    }
    
    public String toString()
    {
      return this.mPath + " mRowId: " + this.mRowId;
    }
  }
  
  static class MediaBulkDeleter
  {
    final Uri mBaseUri;
    final ContentProviderClient mProvider;
    ArrayList<String> whereArgs = new ArrayList(100);
    StringBuilder whereClause = new StringBuilder();
    
    public MediaBulkDeleter(ContentProviderClient paramContentProviderClient, Uri paramUri)
    {
      this.mProvider = paramContentProviderClient;
      this.mBaseUri = paramUri;
    }
    
    public void delete(long paramLong)
      throws RemoteException
    {
      if (this.whereClause.length() != 0) {
        this.whereClause.append(",");
      }
      this.whereClause.append("?");
      this.whereArgs.add("" + paramLong);
      if (this.whereArgs.size() > 100) {
        flush();
      }
    }
    
    public void flush()
      throws RemoteException
    {
      int i = this.whereArgs.size();
      if (i > 0)
      {
        String[] arrayOfString = new String[i];
        arrayOfString = (String[])this.whereArgs.toArray(arrayOfString);
        this.mProvider.delete(this.mBaseUri, "_id IN (" + this.whereClause.toString() + ")", arrayOfString);
        this.whereClause.setLength(0);
        this.whereArgs.clear();
      }
    }
  }
  
  private class MyMediaScannerClient
    implements MediaScannerClient
  {
    private String mAlbum;
    private String mAlbumArtist;
    private String mArtist;
    private int mCompilation;
    private String mComposer;
    private long mDate;
    private final SimpleDateFormat mDateFormatter = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
    private int mDuration;
    private long mFileSize;
    private int mFileType;
    private String mGenre;
    private int mHeight;
    private boolean mIsDrm;
    private long mLastModified;
    private String mMimeType;
    private boolean mNoMedia;
    private String mPath;
    private String mTitle;
    private int mTrack;
    private int mWidth;
    private String mWriter;
    private int mYear;
    
    public MyMediaScannerClient()
    {
      this.mDateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
    
    private boolean convertGenreCode(String paramString1, String paramString2)
    {
      String str = getGenreName(paramString1);
      if (str.equals(paramString2)) {
        return true;
      }
      Log.d("MediaScanner", "'" + paramString1 + "' -> '" + str + "', expected '" + paramString2 + "'");
      return false;
    }
    
    private boolean doesPathHaveFilename(String paramString1, String paramString2)
    {
      boolean bool2 = false;
      int i = paramString1.lastIndexOf(File.separatorChar) + 1;
      int j = paramString2.length();
      boolean bool1 = bool2;
      if (paramString1.regionMatches(i, paramString2, 0, j))
      {
        bool1 = bool2;
        if (i + j == paramString1.length()) {
          bool1 = true;
        }
      }
      return bool1;
    }
    
    private boolean doesRingtonesPathHaveFilename(String paramString, String[] paramArrayOfString)
    {
      boolean bool = false;
      int i = 0;
      for (;;)
      {
        if (i < MediaScanner.-get2())
        {
          bool = doesPathHaveFilename(paramString, paramArrayOfString[i]);
          if (!bool) {}
        }
        else
        {
          return bool;
        }
        i += 1;
      }
    }
    
    private Uri endFile(MediaScanner.FileEntry paramFileEntry, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5)
      throws RemoteException
    {
      if ((this.mArtist == null) || (this.mArtist.length() == 0)) {
        this.mArtist = this.mAlbumArtist;
      }
      ContentValues localContentValues = toValues();
      Object localObject1 = localContentValues.getAsString("title");
      if ((localObject1 == null) || (TextUtils.isEmpty(((String)localObject1).trim()))) {
        localContentValues.put("title", MediaFile.getFileTitle(localContentValues.getAsString("_data")));
      }
      int k;
      int i;
      int j;
      if ("<unknown>".equals(localContentValues.getAsString("album")))
      {
        localObject1 = localContentValues.getAsString("_data");
        k = ((String)localObject1).lastIndexOf('/');
        if (k >= 0)
        {
          i = 0;
          j = ((String)localObject1).indexOf('/', i + 1);
          if ((j >= 0) && (j < k)) {
            break label599;
          }
          if (i != 0) {
            localContentValues.put("album", ((String)localObject1).substring(i + 1, k));
          }
        }
      }
      long l1 = paramFileEntry.mRowId;
      label253:
      Object localObject2;
      Object localObject3;
      label301:
      int n;
      int m;
      if ((MediaFile.isAudioFileType(this.mFileType)) && ((l1 == 0L) || (MediaScanner.-get22(MediaScanner.this) != 0)))
      {
        localContentValues.put("is_ringtone", Boolean.valueOf(paramBoolean1));
        localContentValues.put("is_notification", Boolean.valueOf(paramBoolean2));
        localContentValues.put("is_alarm", Boolean.valueOf(paramBoolean3));
        localContentValues.put("is_music", Boolean.valueOf(paramBoolean4));
        localContentValues.put("is_podcast", Boolean.valueOf(paramBoolean5));
        localObject2 = MediaScanner.-get18(MediaScanner.this);
        localObject3 = MediaScanner.-get20(MediaScanner.this);
        localObject1 = localObject2;
        if (!this.mNoMedia)
        {
          if (!MediaFile.isVideoFileType(this.mFileType)) {
            break label883;
          }
          localObject1 = MediaScanner.-get26(MediaScanner.this);
        }
        localObject2 = null;
        n = 0;
        i = 0;
        m = 0;
        if (!paramBoolean2) {
          break label979;
        }
        j = i;
        if (!MediaScanner.-get13(MediaScanner.this)) {
          if (!TextUtils.isEmpty(MediaScanner.-get12(MediaScanner.this)))
          {
            j = i;
            if (!doesPathHaveFilename(paramFileEntry.mPath, MediaScanner.-get12(MediaScanner.this))) {}
          }
          else
          {
            j = 1;
          }
        }
        m = 0;
        k = m;
        i = j;
        if (OpFeatures.isSupport(new int[] { 6 }))
        {
          if (!MediaScanner.-get11(MediaScanner.this)) {
            break label931;
          }
          i = j;
          k = m;
        }
        label412:
        if (l1 != 0L) {
          break label1179;
        }
        if (MediaScanner.-get22(MediaScanner.this) != 0) {
          localContentValues.put("media_scanner_new_object_id", Integer.valueOf(MediaScanner.-get22(MediaScanner.this)));
        }
        if (localObject1 == MediaScanner.-get18(MediaScanner.this))
        {
          m = paramFileEntry.mFormat;
          j = m;
          if (m == 0) {
            j = MediaFile.getFormatCode(paramFileEntry.mPath, this.mMimeType);
          }
          localContentValues.put("format", Integer.valueOf(j));
        }
        if ((localObject3 != null) && (i == 0)) {
          break label1145;
        }
        if (localObject3 != null) {
          ((MediaInserter)localObject3).flushAll();
        }
        localObject2 = MediaScanner.-get21(MediaScanner.this).insert((Uri)localObject1, localContentValues);
        label536:
        localObject3 = localObject2;
        if (localObject2 != null)
        {
          l1 = ContentUris.parseId((Uri)localObject2);
          paramFileEntry.mRowId = l1;
          localObject3 = localObject2;
        }
        if (i != 0)
        {
          if (!paramBoolean2) {
            break label1326;
          }
          if (k == 0) {
            break label1303;
          }
          setRingtoneIfNotSet("mms_notification", (Uri)localObject1, l1);
          MediaScanner.-set1(MediaScanner.this, true);
        }
      }
      label599:
      label731:
      label883:
      label931:
      label979:
      label1145:
      label1179:
      label1261:
      label1303:
      label1326:
      label1333:
      label1363:
      do
      {
        return (Uri)localObject3;
        i = j;
        break;
        if (((this.mFileType != 31) && (!MediaFile.isRawImageFileType(this.mFileType))) || (this.mNoMedia)) {
          break label253;
        }
        localObject1 = null;
        try
        {
          localObject2 = new ExifInterface(paramFileEntry.mPath);
          localObject1 = localObject2;
        }
        catch (IOException localIOException)
        {
          long l2;
          for (;;) {}
        }
        if (localObject1 == null) {
          break label253;
        }
        localObject2 = new float[2];
        if (((ExifInterface)localObject1).getLatLong((float[])localObject2))
        {
          localContentValues.put("latitude", Float.valueOf(localObject2[0]));
          localContentValues.put("longitude", Float.valueOf(localObject2[1]));
        }
        l2 = ((ExifInterface)localObject1).getGpsDateTime();
        if (l2 != -1L)
        {
          localContentValues.put("datetaken", Long.valueOf(l2));
          i = ((ExifInterface)localObject1).getAttributeInt("Orientation", -1);
          if (i == -1) {
            break label253;
          }
          switch (i)
          {
          case 4: 
          case 5: 
          case 7: 
          default: 
            i = 0;
          }
        }
        for (;;)
        {
          localContentValues.put("orientation", Integer.valueOf(i));
          break;
          l2 = ((ExifInterface)localObject1).getDateTime();
          if ((l2 == -1L) || (Math.abs(this.mLastModified * 1000L - l2) < 86400000L)) {
            break label731;
          }
          localContentValues.put("datetaken", Long.valueOf(l2));
          break label731;
          i = 90;
          continue;
          i = 180;
          continue;
          i = 270;
        }
        if (MediaFile.isImageFileType(this.mFileType))
        {
          localObject1 = MediaScanner.-get19(MediaScanner.this);
          break label301;
        }
        localObject1 = localObject2;
        if (!MediaFile.isAudioFileType(this.mFileType)) {
          break label301;
        }
        localObject1 = MediaScanner.-get4(MediaScanner.this);
        break label301;
        if (!TextUtils.isEmpty(MediaScanner.-get10(MediaScanner.this)))
        {
          k = m;
          i = j;
          if (!doesPathHaveFilename(paramFileEntry.mPath, MediaScanner.-get10(MediaScanner.this))) {
            break label412;
          }
        }
        i = 1;
        k = 1;
        break label412;
        if ((!paramBoolean1) || (isDefaultRingtonesSet()))
        {
          k = m;
          i = n;
          if (!paramBoolean3) {
            break label412;
          }
          k = m;
          i = n;
          if (MediaScanner.-get9(MediaScanner.this)) {
            break label412;
          }
          if (!TextUtils.isEmpty(MediaScanner.-get8(MediaScanner.this)))
          {
            k = m;
            i = n;
            if (!doesPathHaveFilename(paramFileEntry.mPath, MediaScanner.-get8(MediaScanner.this))) {
              break label412;
            }
          }
          i = 1;
          k = m;
          break label412;
        }
        j = 0;
        for (;;)
        {
          k = m;
          i = n;
          if (j >= MediaScanner.-get2()) {
            break;
          }
          if ((TextUtils.isEmpty(MediaScanner.-get14(MediaScanner.this)[j])) || (doesPathHaveFilename(paramFileEntry.mPath, MediaScanner.-get14(MediaScanner.this)[j])))
          {
            i = 1;
            k = m;
            break;
          }
          j += 1;
        }
        if (paramFileEntry.mFormat == 12289)
        {
          ((MediaInserter)localObject3).insertwithPriority((Uri)localObject1, localContentValues);
          break label536;
        }
        ((MediaInserter)localObject3).insert((Uri)localObject1, localContentValues);
        break label536;
        localObject3 = ContentUris.withAppendedId((Uri)localObject1, l1);
        localContentValues.remove("_data");
        j = 0;
        if (!MediaScanner.isNoMediaPath(paramFileEntry.mPath))
        {
          m = MediaFile.getFileTypeForMimeType(this.mMimeType);
          if (!MediaFile.isAudioFileType(m)) {
            break label1261;
          }
          j = 2;
        }
        for (;;)
        {
          localContentValues.put("media_type", Integer.valueOf(j));
          MediaScanner.-get21(MediaScanner.this).update((Uri)localObject3, localContentValues, null, null);
          break;
          if (MediaFile.isVideoFileType(m)) {
            j = 3;
          } else if (MediaFile.isImageFileType(m)) {
            j = 1;
          } else if (MediaFile.isPlayListFileType(m)) {
            j = 4;
          }
        }
        setRingtoneIfNotSet("notification_sound", (Uri)localObject1, l1);
        MediaScanner.-set2(MediaScanner.this, true);
        return (Uri)localObject3;
        if (paramBoolean1)
        {
          i = 0;
          if (i < MediaScanner.-get2()) {
            if (MediaScanner.-get15(MediaScanner.this)[i] == 0) {
              break label1363;
            }
          }
          for (;;)
          {
            i += 1;
            break label1333;
            break;
            if ((TextUtils.isEmpty(MediaScanner.-get14(MediaScanner.this)[i])) || (doesPathHaveFilename(paramFileEntry.mPath, MediaScanner.-get14(MediaScanner.this)[i])))
            {
              setRingtoneIfNotSet(MediaScanner.-get1()[i], (Uri)localObject1, l1);
              MediaScanner.-get15(MediaScanner.this)[i] = 1;
            }
          }
        }
      } while (!paramBoolean3);
      setRingtoneIfNotSet("alarm_alert", (Uri)localObject1, l1);
      MediaScanner.-set0(MediaScanner.this, true);
      return (Uri)localObject3;
    }
    
    private int getFileTypeFromDrm(String paramString)
    {
      if (!MediaScanner.-wrap0(MediaScanner.this)) {
        return 0;
      }
      int j = 0;
      if (MediaScanner.-get16(MediaScanner.this) == null) {
        MediaScanner.-set3(MediaScanner.this, new DrmManagerClient(MediaScanner.-get7(MediaScanner.this)));
      }
      int i = j;
      if (MediaScanner.-get16(MediaScanner.this).canHandle(paramString, null))
      {
        this.mIsDrm = true;
        paramString = MediaScanner.-get16(MediaScanner.this).getOriginalMimeType(paramString);
        i = j;
        if (paramString != null)
        {
          this.mMimeType = paramString;
          i = MediaFile.getFileTypeForMimeType(paramString);
        }
      }
      return i;
    }
    
    private boolean isDefaultRingtonesSet()
    {
      boolean[] arrayOfBoolean = MediaScanner.-get15(MediaScanner.this);
      int j = arrayOfBoolean.length;
      int i = 0;
      while (i < j)
      {
        if (arrayOfBoolean[i] == 0) {
          return false;
        }
        i += 1;
      }
      return true;
    }
    
    private long parseDate(String paramString)
    {
      try
      {
        long l = this.mDateFormatter.parse(paramString).getTime();
        return l;
      }
      catch (ParseException paramString) {}
      return 0L;
    }
    
    private int parseSubstring(String paramString, int paramInt1, int paramInt2)
    {
      int j = paramString.length();
      if (paramInt1 == j) {
        return paramInt2;
      }
      int i = paramInt1 + 1;
      paramInt1 = paramString.charAt(paramInt1);
      if ((paramInt1 < 48) || (paramInt1 > 57)) {
        return paramInt2;
      }
      paramInt2 = paramInt1 - 48;
      paramInt1 = i;
      while (paramInt1 < j)
      {
        i = paramString.charAt(paramInt1);
        if ((i < 48) || (i > 57)) {
          return paramInt2;
        }
        paramInt2 = paramInt2 * 10 + (i - 48);
        paramInt1 += 1;
      }
      return paramInt2;
    }
    
    private void processImageFile(String paramString)
    {
      try
      {
        MediaScanner.-get5(MediaScanner.this).outWidth = 0;
        MediaScanner.-get5(MediaScanner.this).outHeight = 0;
        BitmapFactory.decodeFile(paramString, MediaScanner.-get5(MediaScanner.this));
        this.mWidth = MediaScanner.-get5(MediaScanner.this).outWidth;
        this.mHeight = MediaScanner.-get5(MediaScanner.this).outHeight;
        return;
      }
      catch (Throwable paramString) {}
    }
    
    private void setRingtoneIfNotSet(String paramString, Uri paramUri, long paramLong)
    {
      if (MediaScanner.-get0()) {
        Log.d("MediaScanner", "setRingtoneIfNotSet: [" + paramString + "] = " + paramUri + "(" + paramLong + ")");
      }
      if (MediaScanner.-wrap3(MediaScanner.this, paramString)) {
        return;
      }
      ContentResolver localContentResolver = MediaScanner.-get7(MediaScanner.this).getContentResolver();
      if (TextUtils.isEmpty(Settings.System.getString(localContentResolver, paramString)))
      {
        Uri localUri = Settings.System.getUriFor(paramString);
        paramUri = ContentUris.withAppendedId(paramUri, paramLong);
        RingtoneManager.setActualDefaultRingtoneUri(MediaScanner.-get7(MediaScanner.this), RingtoneManager.getDefaultType(localUri), paramUri);
      }
      Settings.System.putInt(localContentResolver, MediaScanner.-wrap4(MediaScanner.this, paramString), 1);
    }
    
    private void testGenreNameConverter()
    {
      convertGenreCode("2", "Country");
      convertGenreCode("(2)", "Country");
      convertGenreCode("(2", "(2");
      convertGenreCode("2 Foo", "Country");
      convertGenreCode("(2) Foo", "Country");
      convertGenreCode("(2 Foo", "(2 Foo");
      convertGenreCode("2Foo", "2Foo");
      convertGenreCode("(2)Foo", "Country");
      convertGenreCode("200 Foo", "Foo");
      convertGenreCode("(200) Foo", "Foo");
      convertGenreCode("200Foo", "200Foo");
      convertGenreCode("(200)Foo", "Foo");
      convertGenreCode("200)Foo", "200)Foo");
      convertGenreCode("200) Foo", "200) Foo");
    }
    
    private ContentValues toValues()
    {
      ContentValues localContentValues = new ContentValues();
      localContentValues.put("_data", this.mPath);
      localContentValues.put("title", this.mTitle);
      localContentValues.put("date_modified", Long.valueOf(this.mLastModified));
      localContentValues.put("_size", Long.valueOf(this.mFileSize));
      localContentValues.put("mime_type", this.mMimeType);
      localContentValues.put("is_drm", Boolean.valueOf(this.mIsDrm));
      String str2 = null;
      String str1 = str2;
      if (this.mWidth > 0)
      {
        str1 = str2;
        if (this.mHeight > 0)
        {
          localContentValues.put("width", Integer.valueOf(this.mWidth));
          localContentValues.put("height", Integer.valueOf(this.mHeight));
          str1 = this.mWidth + "x" + this.mHeight;
        }
      }
      if (!this.mNoMedia)
      {
        if (!MediaFile.isVideoFileType(this.mFileType)) {
          break label299;
        }
        if ((this.mArtist == null) || (this.mArtist.length() <= 0)) {
          break label287;
        }
        str2 = this.mArtist;
        localContentValues.put("artist", str2);
        if ((this.mAlbum == null) || (this.mAlbum.length() <= 0)) {
          break label293;
        }
        str2 = this.mAlbum;
        localContentValues.put("album", str2);
        localContentValues.put("duration", Integer.valueOf(this.mDuration));
        if (str1 != null) {
          localContentValues.put("resolution", str1);
        }
        if (this.mDate > 0L) {
          localContentValues.put("datetaken", Long.valueOf(this.mDate));
        }
      }
      label287:
      label293:
      label299:
      while ((MediaFile.isImageFileType(this.mFileType)) || (!MediaFile.isAudioFileType(this.mFileType))) {
        for (;;)
        {
          return localContentValues;
          str2 = "<unknown>";
          continue;
          str2 = "<unknown>";
        }
      }
      if ((this.mArtist != null) && (this.mArtist.length() > 0))
      {
        str1 = this.mArtist;
        localContentValues.put("artist", str1);
        if ((this.mAlbumArtist == null) || (this.mAlbumArtist.length() <= 0)) {
          break label501;
        }
        str1 = this.mAlbumArtist;
        label371:
        localContentValues.put("album_artist", str1);
        if ((this.mAlbum == null) || (this.mAlbum.length() <= 0)) {
          break label506;
        }
      }
      label501:
      label506:
      for (str1 = this.mAlbum;; str1 = "<unknown>")
      {
        localContentValues.put("album", str1);
        localContentValues.put("composer", this.mComposer);
        localContentValues.put("genre", this.mGenre);
        if (this.mYear != 0) {
          localContentValues.put("year", Integer.valueOf(this.mYear));
        }
        localContentValues.put("track", Integer.valueOf(this.mTrack));
        localContentValues.put("duration", Integer.valueOf(this.mDuration));
        localContentValues.put("compilation", Integer.valueOf(this.mCompilation));
        return localContentValues;
        str1 = "<unknown>";
        break;
        str1 = null;
        break label371;
      }
    }
    
    public MediaScanner.FileEntry beginFile(String paramString1, String paramString2, long paramLong1, long paramLong2, boolean paramBoolean1, boolean paramBoolean2)
    {
      this.mMimeType = paramString2;
      this.mFileType = 0;
      this.mFileSize = paramLong2;
      this.mIsDrm = false;
      if (!paramBoolean1)
      {
        boolean bool = paramBoolean2;
        if (!paramBoolean2)
        {
          bool = paramBoolean2;
          if (MediaScanner.-wrap1(paramString1)) {
            bool = true;
          }
        }
        this.mNoMedia = bool;
        if (paramString2 != null) {
          this.mFileType = MediaFile.getFileTypeForMimeType(paramString2);
        }
        if (this.mFileType == 0)
        {
          paramString2 = MediaFile.getFileType(paramString1);
          if (paramString2 != null)
          {
            this.mFileType = paramString2.fileType;
            if (this.mMimeType == null) {
              this.mMimeType = paramString2.mimeType;
            }
          }
        }
        if ((MediaScanner.-wrap0(MediaScanner.this)) && (MediaFile.isDrmFileType(this.mFileType))) {
          this.mFileType = getFileTypeFromDrm(paramString1);
        }
      }
      paramString2 = MediaScanner.this.makeEntryFor(paramString1);
      if (paramString2 != null)
      {
        paramLong2 = paramLong1 - paramString2.mLastModified;
        if ((paramLong2 <= 1L) && (paramLong2 >= -1L)) {
          break label246;
        }
      }
      String str;
      label246:
      for (int i = 1;; i = 0)
      {
        if (paramString2 != null)
        {
          str = paramString2;
          if (i == 0) {}
        }
        else
        {
          if (i == 0) {
            break label252;
          }
          paramString2.mLastModified = paramLong1;
          paramString2.mLastModifiedChanged = true;
          str = paramString2;
        }
        if ((!MediaScanner.-get25(MediaScanner.this)) || (!MediaFile.isPlayListFileType(this.mFileType))) {
          break label284;
        }
        MediaScanner.-get23(MediaScanner.this).add(str);
        return null;
        paramLong2 = 0L;
        break;
      }
      label252:
      if (paramBoolean1) {}
      for (i = 12289;; i = 0)
      {
        paramString2 = new MediaScanner.FileEntry(0L, paramString1, paramLong1, i);
        break;
      }
      label284:
      this.mArtist = null;
      this.mAlbumArtist = null;
      this.mAlbum = null;
      this.mTitle = null;
      this.mComposer = null;
      this.mGenre = null;
      this.mTrack = 0;
      this.mYear = 0;
      this.mDuration = 0;
      this.mPath = paramString1;
      this.mDate = 0L;
      this.mLastModified = paramLong1;
      this.mWriter = null;
      this.mCompilation = 0;
      this.mWidth = 0;
      this.mHeight = 0;
      return str;
    }
    
    public Uri doScanFile(String paramString1, String paramString2, long paramLong1, long paramLong2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
    {
      long l = paramLong2;
      if (!paramBoolean1)
      {
        l = paramLong2;
        if (paramLong2 != 0L) {}
      }
      for (;;)
      {
        boolean bool3;
        boolean bool4;
        try
        {
          Object localObject = new File(paramString1);
          l = paramLong2;
          if (((File)localObject).exists()) {
            l = ((File)localObject).length();
          }
          MediaScanner.FileEntry localFileEntry = beginFile(paramString1, paramString2, paramLong1, l, paramBoolean1, paramBoolean3);
          if (localFileEntry == null) {
            return null;
          }
          if (MediaScanner.-get22(MediaScanner.this) != 0) {
            localFileEntry.mRowId = 0L;
          }
          paramBoolean1 = paramBoolean2;
          if (localFileEntry.mPath != null)
          {
            if (((!MediaScanner.-get13(MediaScanner.this)) && (doesPathHaveFilename(localFileEntry.mPath, MediaScanner.-get12(MediaScanner.this)))) || ((!isDefaultRingtonesSet()) && (doesRingtonesPathHaveFilename(localFileEntry.mPath, MediaScanner.-get14(MediaScanner.this)))) || ((!MediaScanner.-get9(MediaScanner.this)) && (doesPathHaveFilename(localFileEntry.mPath, MediaScanner.-get8(MediaScanner.this)))))
            {
              Log.w("MediaScanner", "forcing rescan of " + localFileEntry.mPath + "since ringtone setting didn't finish");
              paramBoolean1 = true;
            }
          }
          else
          {
            paramBoolean2 = paramBoolean1;
            if (MediaScanner.-get6(MediaScanner.this))
            {
              paramBoolean2 = paramBoolean1;
              if (localFileEntry.mPath != null)
              {
                paramBoolean2 = paramBoolean1;
                if (localFileEntry.mPath.startsWith(MediaScanner.-get17()))
                {
                  if (!MediaScanner.-get0()) {
                    break label672;
                  }
                  Log.d("MediaScanner", "Forcing rescan of " + localFileEntry.mPath + " to confirm media_type definition.");
                  break label672;
                }
              }
            }
            if ((localFileEntry == null) || ((!localFileEntry.mLastModifiedChanged) && (!paramBoolean2))) {
              break label678;
            }
            if (!paramBoolean3) {
              continue;
            }
            return endFile(localFileEntry, false, false, false, false, false);
          }
          paramBoolean1 = paramBoolean2;
          if (!MediaScanner.-wrap2(localFileEntry.mPath)) {
            continue;
          }
          paramBoolean1 = paramBoolean2;
          if (Build.FINGERPRINT.equals(MediaScanner.-get27())) {
            continue;
          }
          Log.i("MediaScanner", "forcing rescan of " + localFileEntry.mPath + " since build fingerprint changed");
          paramBoolean1 = true;
          continue;
          localObject = paramString1.toLowerCase(Locale.ROOT);
          if (((String)localObject).indexOf("/ringtones/") > 0)
          {
            paramBoolean2 = true;
            if (((String)localObject).indexOf("/notifications/") > 0)
            {
              paramBoolean3 = true;
              if (((String)localObject).indexOf("/alarms/") <= 0) {
                continue;
              }
              bool1 = true;
              if (((String)localObject).indexOf("/podcasts/") <= 0) {
                continue;
              }
              bool2 = true;
              if (((String)localObject).indexOf("/music/") > 0) {
                continue;
              }
              if (paramBoolean2) {
                break label680;
              }
              if (!paramBoolean3) {
                continue;
              }
              break label680;
              bool3 = MediaFile.isAudioFileType(this.mFileType);
              bool4 = MediaFile.isVideoFileType(this.mFileType);
              boolean bool5 = MediaFile.isImageFileType(this.mFileType);
              if ((!bool3) && (!bool4))
              {
                localObject = paramString1;
                if (!bool5) {
                  break label686;
                }
              }
              localObject = Environment.maybeTranslateEmulatedPathToInternal(new File(paramString1)).getAbsolutePath();
              break label686;
              MediaScanner.-wrap6(MediaScanner.this, (String)localObject, paramString2, this);
              if (bool5) {
                processImageFile((String)localObject);
              }
              paramString1 = endFile(localFileEntry, paramBoolean2, paramBoolean3, bool1, paramBoolean1, bool2);
              return paramString1;
            }
          }
          else
          {
            paramBoolean2 = false;
            continue;
          }
          paramBoolean3 = false;
          continue;
          boolean bool1 = false;
          continue;
          boolean bool2 = false;
          continue;
          paramBoolean1 = true;
          continue;
          if ((bool1) || (bool2)) {
            break label680;
          }
          paramBoolean1 = true;
          continue;
          paramBoolean2 = true;
        }
        catch (RemoteException paramString1)
        {
          Log.e("MediaScanner", "RemoteException in MediaScanner.scanFile()", paramString1);
          return null;
        }
        label672:
        continue;
        label678:
        return null;
        label680:
        paramBoolean1 = false;
        continue;
        label686:
        if (!bool3) {
          if (!bool4) {}
        }
      }
    }
    
    public String getGenreName(String paramString)
    {
      if (paramString == null) {
        return null;
      }
      int k = paramString.length();
      int j;
      Object localObject;
      int i;
      char c;
      if (k > 0)
      {
        j = 0;
        localObject = new StringBuffer();
        i = 0;
        if (i < k)
        {
          c = paramString.charAt(i);
          if ((i == 0) && (c == '(')) {
            j = 1;
          }
          for (;;)
          {
            i += 1;
            break;
            if (!Character.isDigit(c)) {
              break label80;
            }
            ((StringBuffer)localObject).append(c);
          }
        }
        label80:
        if (i < k) {
          c = paramString.charAt(i);
        }
      }
      for (;;)
      {
        if ((j != 0) && (c == ')')) {}
        try
        {
          int m;
          do
          {
            m = Short.parseShort(((StringBuffer)localObject).toString());
            if (m < 0) {
              break label165;
            }
            if ((m >= MediaScanner.-get3().length) || (MediaScanner.-get3()[m] == null)) {
              break label167;
            }
            localObject = MediaScanner.-get3()[m];
            return (String)localObject;
            c = ' ';
            break;
          } while ((j == 0) && (Character.isWhitespace(c)));
          label165:
          label167:
          do
          {
            return paramString;
            if (m == 255) {
              return null;
            }
            if ((m >= 255) || (i + 1 >= k)) {
              break;
            }
            k = i;
            if (j != 0)
            {
              k = i;
              if (c == ')') {
                k = i + 1;
              }
            }
            localObject = paramString.substring(k).trim();
          } while (((String)localObject).length() == 0);
          return (String)localObject;
          localObject = ((StringBuffer)localObject).toString();
          return (String)localObject;
        }
        catch (NumberFormatException localNumberFormatException) {}
      }
      return paramString;
    }
    
    public void handleStringTag(String paramString1, String paramString2)
    {
      boolean bool = true;
      if ((paramString1.equalsIgnoreCase("title")) || (paramString1.startsWith("title;"))) {
        this.mTitle = paramString2;
      }
      do
      {
        return;
        if ((paramString1.equalsIgnoreCase("artist")) || (paramString1.startsWith("artist;")))
        {
          this.mArtist = paramString2.trim();
          return;
        }
        if ((paramString1.equalsIgnoreCase("albumartist")) || (paramString1.startsWith("albumartist;")) || (paramString1.equalsIgnoreCase("band")) || (paramString1.startsWith("band;")))
        {
          this.mAlbumArtist = paramString2.trim();
          return;
        }
        if ((paramString1.equalsIgnoreCase("album")) || (paramString1.startsWith("album;")))
        {
          this.mAlbum = paramString2.trim();
          return;
        }
        if ((paramString1.equalsIgnoreCase("composer")) || (paramString1.startsWith("composer;")))
        {
          this.mComposer = paramString2.trim();
          return;
        }
        if ((MediaScanner.-get24(MediaScanner.this)) && ((paramString1.equalsIgnoreCase("genre")) || (paramString1.startsWith("genre;"))))
        {
          this.mGenre = getGenreName(paramString2);
          return;
        }
        if ((paramString1.equalsIgnoreCase("year")) || (paramString1.startsWith("year;")))
        {
          this.mYear = parseSubstring(paramString2, 0, 0);
          return;
        }
        if ((paramString1.equalsIgnoreCase("tracknumber")) || (paramString1.startsWith("tracknumber;")))
        {
          int i = parseSubstring(paramString2, 0, 0);
          this.mTrack = (this.mTrack / 1000 * 1000 + i);
          return;
        }
        if ((paramString1.equalsIgnoreCase("discnumber")) || (paramString1.equals("set")) || (paramString1.startsWith("set;")))
        {
          this.mTrack = (parseSubstring(paramString2, 0, 0) * 1000 + this.mTrack % 1000);
          return;
        }
        if (paramString1.equalsIgnoreCase("duration"))
        {
          this.mDuration = parseSubstring(paramString2, 0, 0);
          return;
        }
        if ((paramString1.equalsIgnoreCase("writer")) || (paramString1.startsWith("writer;")))
        {
          this.mWriter = paramString2.trim();
          return;
        }
        if (paramString1.equalsIgnoreCase("compilation"))
        {
          this.mCompilation = parseSubstring(paramString2, 0, 0);
          return;
        }
        if (paramString1.equalsIgnoreCase("isdrm"))
        {
          if (parseSubstring(paramString2, 0, 0) == 1) {}
          for (;;)
          {
            this.mIsDrm = bool;
            return;
            bool = false;
          }
        }
        if (paramString1.equalsIgnoreCase("date"))
        {
          this.mDate = parseDate(paramString2);
          return;
        }
        if (paramString1.equalsIgnoreCase("width"))
        {
          this.mWidth = parseSubstring(paramString2, 0, 0);
          return;
        }
      } while (!paramString1.equalsIgnoreCase("height"));
      this.mHeight = parseSubstring(paramString2, 0, 0);
    }
    
    public void scanFile(String paramString, long paramLong1, long paramLong2, boolean paramBoolean1, boolean paramBoolean2)
    {
      if (MediaScanner.isProtectedMediaPath(paramString)) {
        paramBoolean2 = false;
      }
      doScanFile(paramString, null, paramLong1, paramLong2, paramBoolean1, false, paramBoolean2);
    }
    
    public void setMimeType(String paramString)
    {
      if (("audio/mp4".equals(this.mMimeType)) && (paramString.startsWith("video"))) {
        return;
      }
      this.mMimeType = paramString;
      this.mFileType = MediaFile.getFileTypeForMimeType(paramString);
    }
  }
  
  private static class PlaylistEntry
  {
    long bestmatchid;
    int bestmatchlevel;
    String path;
  }
  
  class WplHandler
    implements ElementListener
  {
    final ContentHandler handler;
    String playListDirectory;
    
    public WplHandler(String paramString, Uri paramUri, Cursor paramCursor)
    {
      this.playListDirectory = paramString;
      this$1 = new RootElement("smil");
      MediaScanner.this.getChild("body").getChild("seq").getChild("media").setElementListener(this);
      this.handler = MediaScanner.this.getContentHandler();
    }
    
    public void end() {}
    
    ContentHandler getContentHandler()
    {
      return this.handler;
    }
    
    public void start(Attributes paramAttributes)
    {
      paramAttributes = paramAttributes.getValue("", "src");
      if (paramAttributes != null) {
        MediaScanner.-wrap5(MediaScanner.this, paramAttributes, this.playListDirectory);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/MediaScanner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */