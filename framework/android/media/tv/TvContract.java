package android.media.tv;

import android.content.ComponentName;
import android.content.ContentUris;
import android.net.Uri;
import android.net.Uri.Builder;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.ArraySet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TvContract
{
  public static final String AUTHORITY = "android.media.tv";
  public static final String PARAM_BROWSABLE_ONLY = "browsable_only";
  public static final String PARAM_CANONICAL_GENRE = "canonical_genre";
  public static final String PARAM_CHANNEL = "channel";
  public static final String PARAM_END_TIME = "end_time";
  public static final String PARAM_INPUT = "input";
  public static final String PARAM_START_TIME = "start_time";
  private static final String PATH_CHANNEL = "channel";
  private static final String PATH_PASSTHROUGH = "passthrough";
  private static final String PATH_PROGRAM = "program";
  private static final String PATH_RECORDED_PROGRAM = "recorded_program";
  public static final String PERMISSION_READ_TV_LISTINGS = "android.permission.READ_TV_LISTINGS";
  
  public static final Uri buildChannelLogoUri(long paramLong)
  {
    return buildChannelLogoUri(buildChannelUri(paramLong));
  }
  
  public static final Uri buildChannelLogoUri(Uri paramUri)
  {
    if (!isChannelUriForTunerInput(paramUri)) {
      throw new IllegalArgumentException("Not a channel: " + paramUri);
    }
    return Uri.withAppendedPath(paramUri, "logo");
  }
  
  public static final Uri buildChannelUri(long paramLong)
  {
    return ContentUris.withAppendedId(Channels.CONTENT_URI, paramLong);
  }
  
  public static final Uri buildChannelUriForPassthroughInput(String paramString)
  {
    return new Uri.Builder().scheme("content").authority("android.media.tv").appendPath("passthrough").appendPath(paramString).build();
  }
  
  public static final Uri buildChannelsUriForInput(String paramString)
  {
    return buildChannelsUriForInput(paramString, false);
  }
  
  public static final Uri buildChannelsUriForInput(String paramString1, String paramString2, boolean paramBoolean)
  {
    if (paramString2 == null) {
      return buildChannelsUriForInput(paramString1, paramBoolean);
    }
    if (!TvContract.Programs.Genres.isCanonical(paramString2)) {
      throw new IllegalArgumentException("Not a canonical genre: '" + paramString2 + "'");
    }
    return buildChannelsUriForInput(paramString1, paramBoolean).buildUpon().appendQueryParameter("canonical_genre", paramString2).build();
  }
  
  public static final Uri buildChannelsUriForInput(String paramString, boolean paramBoolean)
  {
    Uri.Builder localBuilder = Channels.CONTENT_URI.buildUpon();
    if (paramString != null) {
      localBuilder.appendQueryParameter("input", paramString);
    }
    return localBuilder.appendQueryParameter("browsable_only", String.valueOf(paramBoolean)).build();
  }
  
  public static final String buildInputId(ComponentName paramComponentName)
  {
    return paramComponentName.flattenToShortString();
  }
  
  public static final Uri buildProgramUri(long paramLong)
  {
    return ContentUris.withAppendedId(Programs.CONTENT_URI, paramLong);
  }
  
  public static final Uri buildProgramsUriForChannel(long paramLong)
  {
    return Programs.CONTENT_URI.buildUpon().appendQueryParameter("channel", String.valueOf(paramLong)).build();
  }
  
  public static final Uri buildProgramsUriForChannel(long paramLong1, long paramLong2, long paramLong3)
  {
    return buildProgramsUriForChannel(paramLong1).buildUpon().appendQueryParameter("start_time", String.valueOf(paramLong2)).appendQueryParameter("end_time", String.valueOf(paramLong3)).build();
  }
  
  public static final Uri buildProgramsUriForChannel(Uri paramUri)
  {
    if (!isChannelUriForTunerInput(paramUri)) {
      throw new IllegalArgumentException("Not a channel: " + paramUri);
    }
    return buildProgramsUriForChannel(ContentUris.parseId(paramUri));
  }
  
  public static final Uri buildProgramsUriForChannel(Uri paramUri, long paramLong1, long paramLong2)
  {
    if (!isChannelUriForTunerInput(paramUri)) {
      throw new IllegalArgumentException("Not a channel: " + paramUri);
    }
    return buildProgramsUriForChannel(ContentUris.parseId(paramUri), paramLong1, paramLong2);
  }
  
  public static final Uri buildRecordedProgramUri(long paramLong)
  {
    return ContentUris.withAppendedId(RecordedPrograms.CONTENT_URI, paramLong);
  }
  
  public static final Uri buildWatchedProgramUri(long paramLong)
  {
    return ContentUris.withAppendedId(WatchedPrograms.CONTENT_URI, paramLong);
  }
  
  public static final boolean isChannelUri(Uri paramUri)
  {
    if (!isChannelUriForTunerInput(paramUri)) {
      return isChannelUriForPassthroughInput(paramUri);
    }
    return true;
  }
  
  public static final boolean isChannelUriForPassthroughInput(Uri paramUri)
  {
    if (isTvUri(paramUri)) {
      return isTwoSegmentUriStartingWith(paramUri, "passthrough");
    }
    return false;
  }
  
  public static final boolean isChannelUriForTunerInput(Uri paramUri)
  {
    if (isTvUri(paramUri)) {
      return isTwoSegmentUriStartingWith(paramUri, "channel");
    }
    return false;
  }
  
  public static final boolean isProgramUri(Uri paramUri)
  {
    if (isTvUri(paramUri)) {
      return isTwoSegmentUriStartingWith(paramUri, "program");
    }
    return false;
  }
  
  private static boolean isTvUri(Uri paramUri)
  {
    if ((paramUri != null) && ("content".equals(paramUri.getScheme()))) {
      return "android.media.tv".equals(paramUri.getAuthority());
    }
    return false;
  }
  
  private static boolean isTwoSegmentUriStartingWith(Uri paramUri, String paramString)
  {
    boolean bool = false;
    paramUri = paramUri.getPathSegments();
    if (paramUri.size() == 2) {
      bool = paramString.equals(paramUri.get(0));
    }
    return bool;
  }
  
  public static abstract interface BaseTvColumns
    extends BaseColumns
  {
    public static final String COLUMN_PACKAGE_NAME = "package_name";
  }
  
  public static final class Channels
    implements TvContract.BaseTvColumns
  {
    public static final String COLUMN_APP_LINK_COLOR = "app_link_color";
    public static final String COLUMN_APP_LINK_ICON_URI = "app_link_icon_uri";
    public static final String COLUMN_APP_LINK_INTENT_URI = "app_link_intent_uri";
    public static final String COLUMN_APP_LINK_POSTER_ART_URI = "app_link_poster_art_uri";
    public static final String COLUMN_APP_LINK_TEXT = "app_link_text";
    public static final String COLUMN_BROWSABLE = "browsable";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_DISPLAY_NAME = "display_name";
    public static final String COLUMN_DISPLAY_NUMBER = "display_number";
    public static final String COLUMN_INPUT_ID = "input_id";
    public static final String COLUMN_INTERNAL_PROVIDER_DATA = "internal_provider_data";
    public static final String COLUMN_INTERNAL_PROVIDER_FLAG1 = "internal_provider_flag1";
    public static final String COLUMN_INTERNAL_PROVIDER_FLAG2 = "internal_provider_flag2";
    public static final String COLUMN_INTERNAL_PROVIDER_FLAG3 = "internal_provider_flag3";
    public static final String COLUMN_INTERNAL_PROVIDER_FLAG4 = "internal_provider_flag4";
    public static final String COLUMN_LOCKED = "locked";
    public static final String COLUMN_NETWORK_AFFILIATION = "network_affiliation";
    public static final String COLUMN_ORIGINAL_NETWORK_ID = "original_network_id";
    public static final String COLUMN_SEARCHABLE = "searchable";
    public static final String COLUMN_SERVICE_ID = "service_id";
    public static final String COLUMN_SERVICE_TYPE = "service_type";
    public static final String COLUMN_TRANSPORT_STREAM_ID = "transport_stream_id";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_VERSION_NUMBER = "version_number";
    public static final String COLUMN_VIDEO_FORMAT = "video_format";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/channel";
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/channel";
    public static final Uri CONTENT_URI = Uri.parse("content://android.media.tv/channel");
    public static final String SERVICE_TYPE_AUDIO = "SERVICE_TYPE_AUDIO";
    public static final String SERVICE_TYPE_AUDIO_VIDEO = "SERVICE_TYPE_AUDIO_VIDEO";
    public static final String SERVICE_TYPE_OTHER = "SERVICE_TYPE_OTHER";
    public static final String TYPE_1SEG = "TYPE_1SEG";
    public static final String TYPE_ATSC_C = "TYPE_ATSC_C";
    public static final String TYPE_ATSC_M_H = "TYPE_ATSC_M_H";
    public static final String TYPE_ATSC_T = "TYPE_ATSC_T";
    public static final String TYPE_CMMB = "TYPE_CMMB";
    public static final String TYPE_DTMB = "TYPE_DTMB";
    public static final String TYPE_DVB_C = "TYPE_DVB_C";
    public static final String TYPE_DVB_C2 = "TYPE_DVB_C2";
    public static final String TYPE_DVB_H = "TYPE_DVB_H";
    public static final String TYPE_DVB_S = "TYPE_DVB_S";
    public static final String TYPE_DVB_S2 = "TYPE_DVB_S2";
    public static final String TYPE_DVB_SH = "TYPE_DVB_SH";
    public static final String TYPE_DVB_T = "TYPE_DVB_T";
    public static final String TYPE_DVB_T2 = "TYPE_DVB_T2";
    public static final String TYPE_ISDB_C = "TYPE_ISDB_C";
    public static final String TYPE_ISDB_S = "TYPE_ISDB_S";
    public static final String TYPE_ISDB_T = "TYPE_ISDB_T";
    public static final String TYPE_ISDB_TB = "TYPE_ISDB_TB";
    public static final String TYPE_NTSC = "TYPE_NTSC";
    public static final String TYPE_OTHER = "TYPE_OTHER";
    public static final String TYPE_PAL = "TYPE_PAL";
    public static final String TYPE_SECAM = "TYPE_SECAM";
    public static final String TYPE_S_DMB = "TYPE_S_DMB";
    public static final String TYPE_T_DMB = "TYPE_T_DMB";
    public static final String VIDEO_FORMAT_1080I = "VIDEO_FORMAT_1080I";
    public static final String VIDEO_FORMAT_1080P = "VIDEO_FORMAT_1080P";
    public static final String VIDEO_FORMAT_2160P = "VIDEO_FORMAT_2160P";
    public static final String VIDEO_FORMAT_240P = "VIDEO_FORMAT_240P";
    public static final String VIDEO_FORMAT_360P = "VIDEO_FORMAT_360P";
    public static final String VIDEO_FORMAT_4320P = "VIDEO_FORMAT_4320P";
    public static final String VIDEO_FORMAT_480I = "VIDEO_FORMAT_480I";
    public static final String VIDEO_FORMAT_480P = "VIDEO_FORMAT_480P";
    public static final String VIDEO_FORMAT_576I = "VIDEO_FORMAT_576I";
    public static final String VIDEO_FORMAT_576P = "VIDEO_FORMAT_576P";
    public static final String VIDEO_FORMAT_720P = "VIDEO_FORMAT_720P";
    private static final Map<String, String> VIDEO_FORMAT_TO_RESOLUTION_MAP = new HashMap();
    public static final String VIDEO_RESOLUTION_ED = "VIDEO_RESOLUTION_ED";
    public static final String VIDEO_RESOLUTION_FHD = "VIDEO_RESOLUTION_FHD";
    public static final String VIDEO_RESOLUTION_HD = "VIDEO_RESOLUTION_HD";
    public static final String VIDEO_RESOLUTION_SD = "VIDEO_RESOLUTION_SD";
    public static final String VIDEO_RESOLUTION_UHD = "VIDEO_RESOLUTION_UHD";
    
    static
    {
      VIDEO_FORMAT_TO_RESOLUTION_MAP.put("VIDEO_FORMAT_480I", "VIDEO_RESOLUTION_SD");
      VIDEO_FORMAT_TO_RESOLUTION_MAP.put("VIDEO_FORMAT_480P", "VIDEO_RESOLUTION_ED");
      VIDEO_FORMAT_TO_RESOLUTION_MAP.put("VIDEO_FORMAT_576I", "VIDEO_RESOLUTION_SD");
      VIDEO_FORMAT_TO_RESOLUTION_MAP.put("VIDEO_FORMAT_576P", "VIDEO_RESOLUTION_ED");
      VIDEO_FORMAT_TO_RESOLUTION_MAP.put("VIDEO_FORMAT_720P", "VIDEO_RESOLUTION_HD");
      VIDEO_FORMAT_TO_RESOLUTION_MAP.put("VIDEO_FORMAT_1080I", "VIDEO_RESOLUTION_HD");
      VIDEO_FORMAT_TO_RESOLUTION_MAP.put("VIDEO_FORMAT_1080P", "VIDEO_RESOLUTION_FHD");
      VIDEO_FORMAT_TO_RESOLUTION_MAP.put("VIDEO_FORMAT_2160P", "VIDEO_RESOLUTION_UHD");
      VIDEO_FORMAT_TO_RESOLUTION_MAP.put("VIDEO_FORMAT_4320P", "VIDEO_RESOLUTION_UHD");
    }
    
    public static final String getVideoResolution(String paramString)
    {
      return (String)VIDEO_FORMAT_TO_RESOLUTION_MAP.get(paramString);
    }
    
    public static final class Logo
    {
      public static final String CONTENT_DIRECTORY = "logo";
    }
  }
  
  public static final class Programs
    implements TvContract.BaseTvColumns
  {
    public static final String COLUMN_AUDIO_LANGUAGE = "audio_language";
    public static final String COLUMN_BROADCAST_GENRE = "broadcast_genre";
    public static final String COLUMN_CANONICAL_GENRE = "canonical_genre";
    public static final String COLUMN_CHANNEL_ID = "channel_id";
    public static final String COLUMN_CONTENT_RATING = "content_rating";
    public static final String COLUMN_END_TIME_UTC_MILLIS = "end_time_utc_millis";
    public static final String COLUMN_EPISODE_DISPLAY_NUMBER = "episode_display_number";
    @Deprecated
    public static final String COLUMN_EPISODE_NUMBER = "episode_number";
    public static final String COLUMN_EPISODE_TITLE = "episode_title";
    public static final String COLUMN_INTERNAL_PROVIDER_DATA = "internal_provider_data";
    public static final String COLUMN_INTERNAL_PROVIDER_FLAG1 = "internal_provider_flag1";
    public static final String COLUMN_INTERNAL_PROVIDER_FLAG2 = "internal_provider_flag2";
    public static final String COLUMN_INTERNAL_PROVIDER_FLAG3 = "internal_provider_flag3";
    public static final String COLUMN_INTERNAL_PROVIDER_FLAG4 = "internal_provider_flag4";
    public static final String COLUMN_LONG_DESCRIPTION = "long_description";
    public static final String COLUMN_POSTER_ART_URI = "poster_art_uri";
    public static final String COLUMN_RECORDING_PROHIBITED = "recording_prohibited";
    public static final String COLUMN_SEARCHABLE = "searchable";
    public static final String COLUMN_SEASON_DISPLAY_NUMBER = "season_display_number";
    @Deprecated
    public static final String COLUMN_SEASON_NUMBER = "season_number";
    public static final String COLUMN_SEASON_TITLE = "season_title";
    public static final String COLUMN_SHORT_DESCRIPTION = "short_description";
    public static final String COLUMN_START_TIME_UTC_MILLIS = "start_time_utc_millis";
    public static final String COLUMN_THUMBNAIL_URI = "thumbnail_uri";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_VERSION_NUMBER = "version_number";
    public static final String COLUMN_VIDEO_HEIGHT = "video_height";
    public static final String COLUMN_VIDEO_WIDTH = "video_width";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/program";
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/program";
    public static final Uri CONTENT_URI = Uri.parse("content://android.media.tv/program");
    
    public static final class Genres
    {
      public static final String ANIMAL_WILDLIFE = "ANIMAL_WILDLIFE";
      public static final String ARTS = "ARTS";
      private static final ArraySet<String> CANONICAL_GENRES = new ArraySet();
      public static final String COMEDY = "COMEDY";
      private static final char COMMA = ',';
      private static final String DELIMITER = ",";
      private static final char DOUBLE_QUOTE = '"';
      public static final String DRAMA = "DRAMA";
      public static final String EDUCATION = "EDUCATION";
      private static final String[] EMPTY_STRING_ARRAY = new String[0];
      public static final String ENTERTAINMENT = "ENTERTAINMENT";
      public static final String FAMILY_KIDS = "FAMILY_KIDS";
      public static final String GAMING = "GAMING";
      public static final String LIFE_STYLE = "LIFE_STYLE";
      public static final String MOVIES = "MOVIES";
      public static final String MUSIC = "MUSIC";
      public static final String NEWS = "NEWS";
      public static final String PREMIER = "PREMIER";
      public static final String SHOPPING = "SHOPPING";
      public static final String SPORTS = "SPORTS";
      public static final String TECH_SCIENCE = "TECH_SCIENCE";
      public static final String TRAVEL = "TRAVEL";
      
      static
      {
        CANONICAL_GENRES.add("FAMILY_KIDS");
        CANONICAL_GENRES.add("SPORTS");
        CANONICAL_GENRES.add("SHOPPING");
        CANONICAL_GENRES.add("MOVIES");
        CANONICAL_GENRES.add("COMEDY");
        CANONICAL_GENRES.add("TRAVEL");
        CANONICAL_GENRES.add("DRAMA");
        CANONICAL_GENRES.add("EDUCATION");
        CANONICAL_GENRES.add("ANIMAL_WILDLIFE");
        CANONICAL_GENRES.add("NEWS");
        CANONICAL_GENRES.add("GAMING");
        CANONICAL_GENRES.add("ARTS");
        CANONICAL_GENRES.add("ENTERTAINMENT");
        CANONICAL_GENRES.add("LIFE_STYLE");
        CANONICAL_GENRES.add("MUSIC");
        CANONICAL_GENRES.add("PREMIER");
        CANONICAL_GENRES.add("TECH_SCIENCE");
      }
      
      public static String[] decode(String paramString)
      {
        if (TextUtils.isEmpty(paramString)) {
          return EMPTY_STRING_ARRAY;
        }
        if ((paramString.indexOf(',') == -1) && (paramString.indexOf('"') == -1)) {
          return new String[] { paramString.trim() };
        }
        Object localObject = new StringBuilder();
        ArrayList localArrayList = new ArrayList();
        int k = paramString.length();
        int i = 0;
        int j = 0;
        if (j < k)
        {
          char c = paramString.charAt(j);
          switch (c)
          {
          default: 
            label112:
            ((StringBuilder)localObject).append(c);
            i = 0;
          }
          for (;;)
          {
            j += 1;
            break;
            if (i != 0) {
              break label112;
            }
            i = 1;
            continue;
            if (i != 0) {
              break label112;
            }
            localObject = ((StringBuilder)localObject).toString().trim();
            if (((String)localObject).length() > 0) {
              localArrayList.add(localObject);
            }
            localObject = new StringBuilder();
          }
        }
        paramString = ((StringBuilder)localObject).toString().trim();
        if (paramString.length() > 0) {
          localArrayList.add(paramString);
        }
        return (String[])localArrayList.toArray(new String[localArrayList.size()]);
      }
      
      public static String encode(String... paramVarArgs)
      {
        if (paramVarArgs == null) {
          return null;
        }
        StringBuilder localStringBuilder = new StringBuilder();
        String str1 = "";
        int i = 0;
        int j = paramVarArgs.length;
        while (i < j)
        {
          String str2 = paramVarArgs[i];
          localStringBuilder.append(str1).append(encodeToCsv(str2));
          str1 = ",";
          i += 1;
        }
        return localStringBuilder.toString();
      }
      
      private static String encodeToCsv(String paramString)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        int j = paramString.length();
        int i = 0;
        if (i < j)
        {
          char c = paramString.charAt(i);
          switch (c)
          {
          }
          for (;;)
          {
            localStringBuilder.append(c);
            i += 1;
            break;
            localStringBuilder.append('"');
            continue;
            localStringBuilder.append('"');
          }
        }
        return localStringBuilder.toString();
      }
      
      public static boolean isCanonical(String paramString)
      {
        return CANONICAL_GENRES.contains(paramString);
      }
    }
  }
  
  public static final class RecordedPrograms
    implements TvContract.BaseTvColumns
  {
    public static final String COLUMN_AUDIO_LANGUAGE = "audio_language";
    public static final String COLUMN_BROADCAST_GENRE = "broadcast_genre";
    public static final String COLUMN_CANONICAL_GENRE = "canonical_genre";
    public static final String COLUMN_CHANNEL_ID = "channel_id";
    public static final String COLUMN_CONTENT_RATING = "content_rating";
    public static final String COLUMN_END_TIME_UTC_MILLIS = "end_time_utc_millis";
    public static final String COLUMN_EPISODE_DISPLAY_NUMBER = "episode_display_number";
    public static final String COLUMN_EPISODE_TITLE = "episode_title";
    public static final String COLUMN_INPUT_ID = "input_id";
    public static final String COLUMN_INTERNAL_PROVIDER_DATA = "internal_provider_data";
    public static final String COLUMN_INTERNAL_PROVIDER_FLAG1 = "internal_provider_flag1";
    public static final String COLUMN_INTERNAL_PROVIDER_FLAG2 = "internal_provider_flag2";
    public static final String COLUMN_INTERNAL_PROVIDER_FLAG3 = "internal_provider_flag3";
    public static final String COLUMN_INTERNAL_PROVIDER_FLAG4 = "internal_provider_flag4";
    public static final String COLUMN_LONG_DESCRIPTION = "long_description";
    public static final String COLUMN_POSTER_ART_URI = "poster_art_uri";
    public static final String COLUMN_RECORDING_DATA_BYTES = "recording_data_bytes";
    public static final String COLUMN_RECORDING_DATA_URI = "recording_data_uri";
    public static final String COLUMN_RECORDING_DURATION_MILLIS = "recording_duration_millis";
    public static final String COLUMN_RECORDING_EXPIRE_TIME_UTC_MILLIS = "recording_expire_time_utc_millis";
    public static final String COLUMN_SEARCHABLE = "searchable";
    public static final String COLUMN_SEASON_DISPLAY_NUMBER = "season_display_number";
    public static final String COLUMN_SEASON_TITLE = "season_title";
    public static final String COLUMN_SHORT_DESCRIPTION = "short_description";
    public static final String COLUMN_START_TIME_UTC_MILLIS = "start_time_utc_millis";
    public static final String COLUMN_THUMBNAIL_URI = "thumbnail_uri";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_VERSION_NUMBER = "version_number";
    public static final String COLUMN_VIDEO_HEIGHT = "video_height";
    public static final String COLUMN_VIDEO_WIDTH = "video_width";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/recorded_program";
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/recorded_program";
    public static final Uri CONTENT_URI = Uri.parse("content://android.media.tv/recorded_program");
  }
  
  public static final class WatchedPrograms
    implements TvContract.BaseTvColumns
  {
    public static final String COLUMN_CHANNEL_ID = "channel_id";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_END_TIME_UTC_MILLIS = "end_time_utc_millis";
    public static final String COLUMN_INTERNAL_SESSION_TOKEN = "session_token";
    public static final String COLUMN_INTERNAL_TUNE_PARAMS = "tune_params";
    public static final String COLUMN_START_TIME_UTC_MILLIS = "start_time_utc_millis";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_WATCH_END_TIME_UTC_MILLIS = "watch_end_time_utc_millis";
    public static final String COLUMN_WATCH_START_TIME_UTC_MILLIS = "watch_start_time_utc_millis";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/watched_program";
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/watched_program";
    public static final Uri CONTENT_URI = Uri.parse("content://android.media.tv/watched_program");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/tv/TvContract.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */