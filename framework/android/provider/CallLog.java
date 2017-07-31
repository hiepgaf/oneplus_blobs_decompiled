package android.provider;

import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.UserInfo;
import android.database.Cursor;
import android.location.Country;
import android.location.CountryDetector;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.UserHandle;
import android.os.UserManager;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.telephony.CallerInfo;
import java.util.List;

public class CallLog
{
  public static final String AUTHORITY = "call_log";
  public static final Uri CONTENT_URI = Uri.parse("content://call_log");
  private static final String LOG_TAG = "CallLog";
  public static final String SHADOW_AUTHORITY = "call_log_shadow";
  private static final boolean VERBOSE_LOG = false;
  
  public static class Calls
    implements BaseColumns
  {
    public static final String ADD_FOR_ALL_USERS = "add_for_all_users";
    public static final String ALLOW_VOICEMAILS_PARAM_KEY = "allow_voicemails";
    public static final int ANSWERED_EXTERNALLY_TYPE = 7;
    public static final int BLOCKED_TYPE = 6;
    public static final String CACHED_FORMATTED_NUMBER = "formatted_number";
    public static final String CACHED_LOOKUP_URI = "lookup_uri";
    public static final String CACHED_MATCHED_NUMBER = "matched_number";
    public static final String CACHED_NAME = "name";
    public static final String CACHED_NORMALIZED_NUMBER = "normalized_number";
    public static final String CACHED_NUMBER_LABEL = "numberlabel";
    public static final String CACHED_NUMBER_TYPE = "numbertype";
    public static final String CACHED_PHOTO_ID = "photo_id";
    public static final String CACHED_PHOTO_URI = "photo_uri";
    public static final Uri CONTENT_FILTER_URI = Uri.parse("content://call_log/calls/filter");
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/calls";
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/calls";
    public static final Uri CONTENT_URI = Uri.parse("content://call_log/calls");
    public static final Uri CONTENT_URI_WITH_VOICEMAIL = CONTENT_URI.buildUpon().appendQueryParameter("allow_voicemails", "true").build();
    public static final String COUNTRY_ISO = "countryiso";
    public static final String DATA_USAGE = "data_usage";
    public static final String DATE = "date";
    public static final String DEFAULT_SORT_ORDER = "date DESC";
    public static final String DURATION = "duration";
    public static final String EXTRA_CALL_TYPE_FILTER = "android.provider.extra.CALL_TYPE_FILTER";
    public static final String FEATURES = "features";
    public static final int FEATURES_PULLED_EXTERNALLY = 2;
    public static final int FEATURES_VIDEO = 1;
    public static final String GEOCODED_LOCATION = "geocoded_location";
    public static final int INCOMING_IMS_TYPE = 1000;
    public static final int INCOMING_TYPE = 1;
    public static final int INCOMING_WIFI_TYPE = 1003;
    public static final String IS_READ = "is_read";
    public static final String LAST_MODIFIED = "last_modified";
    public static final String LIMIT_PARAM_KEY = "limit";
    private static final int MIN_DURATION_FOR_NORMALIZED_NUMBER_UPDATE_MS = 10000;
    public static final int MISSED_IMS_TYPE = 1002;
    public static final int MISSED_TYPE = 3;
    public static final int MISSED_WIFI_TYPE = 1005;
    public static final String NEW = "new";
    public static final String NUMBER = "number";
    public static final String NUMBER_PRESENTATION = "presentation";
    public static final String OFFSET_PARAM_KEY = "offset";
    public static final int OUTGOING_IMS_TYPE = 1001;
    public static final int OUTGOING_TYPE = 2;
    public static final int OUTGOING_WIFI_TYPE = 1004;
    public static final String PHONE_ACCOUNT_ADDRESS = "phone_account_address";
    public static final String PHONE_ACCOUNT_COMPONENT_NAME = "subscription_component_name";
    public static final String PHONE_ACCOUNT_HIDDEN = "phone_account_hidden";
    public static final String PHONE_ACCOUNT_ID = "subscription_id";
    public static final String POST_DIAL_DIGITS = "post_dial_digits";
    public static final int PRESENTATION_ALLOWED = 1;
    public static final int PRESENTATION_PAYPHONE = 4;
    public static final int PRESENTATION_RESTRICTED = 2;
    public static final int PRESENTATION_UNKNOWN = 3;
    public static final int REJECTED_TYPE = 5;
    public static final Uri SHADOW_CONTENT_URI = Uri.parse("content://call_log_shadow/calls");
    public static final String SUB_ID = "sub_id";
    public static final String TRANSCRIPTION = "transcription";
    public static final String TYPE = "type";
    public static final String VIA_NUMBER = "via_number";
    public static final int VOICEMAIL_TYPE = 4;
    public static final String VOICEMAIL_URI = "voicemail_uri";
    
    public static Uri addCall(CallerInfo paramCallerInfo, Context paramContext, String paramString, int paramInt1, int paramInt2, int paramInt3, PhoneAccountHandle paramPhoneAccountHandle, long paramLong, int paramInt4, Long paramLong1)
    {
      return addCall(paramCallerInfo, paramContext, paramString, "", "", paramInt1, paramInt2, paramInt3, paramPhoneAccountHandle, paramLong, paramInt4, paramLong1, false, null, false);
    }
    
    public static Uri addCall(CallerInfo paramCallerInfo, Context paramContext, String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, int paramInt3, PhoneAccountHandle paramPhoneAccountHandle, long paramLong, int paramInt4, Long paramLong1, boolean paramBoolean, UserHandle paramUserHandle)
    {
      return addCall(paramCallerInfo, paramContext, paramString1, paramString2, paramString3, paramInt1, paramInt2, paramInt3, paramPhoneAccountHandle, paramLong, paramInt4, paramLong1, paramBoolean, paramUserHandle, false);
    }
    
    public static Uri addCall(CallerInfo paramCallerInfo, Context paramContext, String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, int paramInt3, PhoneAccountHandle paramPhoneAccountHandle, long paramLong, int paramInt4, Long paramLong1, boolean paramBoolean1, UserHandle paramUserHandle, boolean paramBoolean2)
    {
      ContentResolver localContentResolver = paramContext.getContentResolver();
      int i = 1;
      Object localObject2 = null;
      try
      {
        localObject1 = TelecomManager.from(paramContext);
        localObject2 = localObject1;
      }
      catch (UnsupportedOperationException localUnsupportedOperationException)
      {
        for (;;)
        {
          try
          {
            Object localObject1;
            String str;
            Uri localUri;
            if ((paramString2.getCount() > 0) && (paramString2.moveToFirst()))
            {
              paramString3 = paramString2.getString(0);
              updateDataUsageStatForData(localContentResolver, paramString3);
              if ((paramInt4 >= 10000) && (paramInt2 == 2) && (TextUtils.isEmpty(paramCallerInfo.normalizedNumber))) {
                updateNormalizedNumber(paramContext, localContentResolver, paramString3, paramString1);
              }
            }
            paramString2.close();
            paramCallerInfo = null;
            paramString3 = (UserManager)paramContext.getSystemService(UserManager.class);
            paramInt2 = paramString3.getUserHandle();
            if (!paramBoolean1) {
              break label929;
            }
            paramString1 = addEntryAndRemoveExpiredEntries(paramContext, paramString3, UserHandle.SYSTEM, paramPhoneAccountHandle);
            if ((paramString1 != null) && (!"call_log_shadow".equals(paramString1.getAuthority()))) {
              break;
            }
            return null;
          }
          finally
          {
            paramString2.close();
          }
          localUnsupportedOperationException = localUnsupportedOperationException;
          continue;
          if (paramInt1 == 4)
          {
            i = 4;
          }
          else if ((TextUtils.isEmpty(paramString1)) || (paramInt1 == 3))
          {
            i = 3;
            continue;
            paramInt1 = 0;
            continue;
            if (paramInt2 == 1005)
            {
              continue;
              paramInt1 = 0;
            }
          }
        }
        if (paramCallerInfo.phoneNumber == null) {
          break label778;
        }
        for (paramString2 = paramCallerInfo.phoneNumber;; paramString2 = paramString1)
        {
          paramString2 = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Callable.CONTENT_FILTER_URI, Uri.encode(paramString2));
          paramLong = paramCallerInfo.contactIdOrZero;
          paramString2 = localContentResolver.query(paramString2, new String[] { "_id" }, "contact_id =?", new String[] { String.valueOf(paramLong) }, null);
          break;
        }
      }
      str = null;
      localObject1 = str;
      if (localObject2 != null)
      {
        localObject1 = str;
        if (paramPhoneAccountHandle != null)
        {
          localObject2 = ((TelecomManager)localObject2).getPhoneAccount(paramPhoneAccountHandle);
          localObject1 = str;
          if (localObject2 != null)
          {
            localObject2 = ((PhoneAccount)localObject2).getSubscriptionAddress();
            localObject1 = str;
            if (localObject2 != null) {
              localObject1 = ((Uri)localObject2).getSchemeSpecificPart();
            }
          }
        }
      }
      if (paramInt1 == 2)
      {
        i = 2;
        if (i != 1)
        {
          localObject2 = "";
          paramString1 = (String)localObject2;
          if (paramCallerInfo != null)
          {
            paramCallerInfo.name = "";
            paramString1 = (String)localObject2;
          }
        }
        localObject2 = null;
        str = null;
        if (paramPhoneAccountHandle != null)
        {
          localObject2 = paramPhoneAccountHandle.getComponentName().flattenToString();
          str = paramPhoneAccountHandle.getId();
        }
        paramPhoneAccountHandle = new ContentValues(6);
        if (paramCallerInfo != null)
        {
          paramPhoneAccountHandle.put("name", paramCallerInfo.name);
          if (paramCallerInfo.contactDisplayPhotoUri != null)
          {
            paramPhoneAccountHandle.put("photo_uri", paramCallerInfo.contactDisplayPhotoUri.toString());
            paramPhoneAccountHandle.put("photo_id", Integer.valueOf(paramCallerInfo.photoResource));
          }
          if ((paramCallerInfo.lookupKey != null) && (paramCallerInfo.contactIdOrZero > 0L))
          {
            localUri = ContactsContract.Contacts.getLookupUri(paramCallerInfo.contactIdOrZero, paramCallerInfo.lookupKey);
            if (localUri != null) {
              paramPhoneAccountHandle.put("lookup_uri", localUri.toString());
            }
          }
        }
        paramPhoneAccountHandle.put("number", paramString1);
        paramPhoneAccountHandle.put("post_dial_digits", paramString2);
        paramPhoneAccountHandle.put("via_number", paramString3);
        paramPhoneAccountHandle.put("presentation", Integer.valueOf(i));
        paramPhoneAccountHandle.put("type", Integer.valueOf(paramInt2));
        paramPhoneAccountHandle.put("features", Integer.valueOf(paramInt3));
        paramPhoneAccountHandle.put("date", Long.valueOf(paramLong));
        paramPhoneAccountHandle.put("duration", Long.valueOf(paramInt4));
        if (paramLong1 != null) {
          paramPhoneAccountHandle.put("data_usage", paramLong1);
        }
        paramPhoneAccountHandle.put("subscription_component_name", (String)localObject2);
        paramPhoneAccountHandle.put("subscription_id", str);
        paramPhoneAccountHandle.put("phone_account_address", (String)localObject1);
        paramPhoneAccountHandle.put("new", Integer.valueOf(1));
        if (!paramBoolean1) {
          break label690;
        }
        paramInt1 = 1;
        paramPhoneAccountHandle.put("add_for_all_users", Integer.valueOf(paramInt1));
        if ((paramInt2 != 3) && (paramInt2 != 1002)) {
          break label696;
        }
        if (!paramBoolean2) {
          break label707;
        }
        paramInt1 = 1;
        paramPhoneAccountHandle.put("is_read", Integer.valueOf(paramInt1));
        if ((paramCallerInfo != null) && (paramCallerInfo.contactIdOrZero > 0L))
        {
          if (paramCallerInfo.normalizedNumber == null) {
            break label713;
          }
          paramString2 = paramCallerInfo.normalizedNumber;
          paramString3 = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
          paramLong = paramCallerInfo.contactIdOrZero;
          paramString2 = localContentResolver.query(paramString3, new String[] { "_id" }, "contact_id =? AND data4 =?", new String[] { String.valueOf(paramLong), paramString2 }, null);
          if (paramString2 == null) {}
        }
      }
      label690:
      label696:
      label707:
      label713:
      label778:
      if (paramInt2 == 0) {
        paramCallerInfo = paramString1;
      }
      paramLong1 = paramString3.getUsers(true);
      paramInt3 = paramLong1.size();
      paramInt1 = 0;
      paramString1 = paramCallerInfo;
      if (paramInt1 < paramInt3)
      {
        paramString2 = ((UserInfo)paramLong1.get(paramInt1)).getUserHandle();
        paramInt4 = paramString2.getIdentifier();
        if (paramString2.isSystem()) {
          paramString1 = paramCallerInfo;
        }
        for (;;)
        {
          paramInt1 += 1;
          paramCallerInfo = paramString1;
          break;
          paramString1 = paramCallerInfo;
          if (shouldHaveSharedCallLogEntries(paramContext, paramString3, paramInt4))
          {
            paramString1 = paramCallerInfo;
            if (paramString3.isUserRunning(paramString2))
            {
              paramString1 = paramCallerInfo;
              if (paramString3.isUserUnlocked(paramString2))
              {
                paramString2 = addEntryAndRemoveExpiredEntries(paramContext, paramString3, paramString2, paramPhoneAccountHandle);
                paramString1 = paramCallerInfo;
                if (paramInt4 == paramInt2) {
                  paramString1 = paramString2;
                }
              }
            }
          }
        }
        label929:
        if (paramUserHandle == null) {
          break label947;
        }
      }
      for (;;)
      {
        paramString1 = addEntryAndRemoveExpiredEntries(paramContext, paramString3, paramUserHandle, paramPhoneAccountHandle);
        return paramString1;
        label947:
        paramUserHandle = UserHandle.of(paramInt2);
      }
    }
    
    private static Uri addEntryAndRemoveExpiredEntries(Context paramContext, UserManager paramUserManager, UserHandle paramUserHandle, ContentValues paramContentValues)
    {
      ContentResolver localContentResolver = paramContext.getContentResolver();
      if (paramUserManager.isUserUnlocked(paramUserHandle)) {}
      for (paramContext = CONTENT_URI;; paramContext = SHADOW_CONTENT_URI)
      {
        paramContext = ContentProvider.maybeAddUserId(paramContext, paramUserHandle.getIdentifier());
        try
        {
          paramUserManager = localContentResolver.insert(paramContext, paramContentValues);
          localContentResolver.delete(paramContext, "_id IN (SELECT _id FROM calls ORDER BY date DESC LIMIT -1 OFFSET 500)", null);
          return paramUserManager;
        }
        catch (IllegalArgumentException paramContext)
        {
          Log.w("CallLog", "Failed to insert calllog", paramContext);
        }
      }
      return null;
    }
    
    private static String getCurrentCountryIso(Context paramContext)
    {
      Object localObject1 = null;
      Object localObject2 = (CountryDetector)paramContext.getSystemService("country_detector");
      paramContext = (Context)localObject1;
      if (localObject2 != null)
      {
        localObject2 = ((CountryDetector)localObject2).detectCountry();
        paramContext = (Context)localObject1;
        if (localObject2 != null) {
          paramContext = ((Country)localObject2).getCountryIso();
        }
      }
      return paramContext;
    }
    
    public static String getLastOutgoingCall(Context paramContext)
    {
      Object localObject1 = paramContext.getContentResolver();
      paramContext = null;
      try
      {
        localObject1 = ((ContentResolver)localObject1).query(CONTENT_URI, new String[] { "number" }, "type = 2 OR type = 1001 OR type = 1004", null, "date DESC LIMIT 1");
        if (localObject1 != null)
        {
          paramContext = (Context)localObject1;
          if (((Cursor)localObject1).moveToFirst())
          {
            paramContext = (Context)localObject1;
            String str = ((Cursor)localObject1).getString(0);
            if (localObject1 != null) {
              ((Cursor)localObject1).close();
            }
            return str;
          }
        }
        if (localObject1 != null) {
          ((Cursor)localObject1).close();
        }
        return "";
      }
      finally
      {
        if (paramContext != null) {
          paramContext.close();
        }
      }
    }
    
    public static boolean shouldHaveSharedCallLogEntries(Context paramContext, UserManager paramUserManager, int paramInt)
    {
      if (paramUserManager.hasUserRestriction("no_outgoing_calls", UserHandle.of(paramInt))) {
        return false;
      }
      paramContext = paramUserManager.getUserInfo(paramInt);
      return (paramContext != null) && (!paramContext.isManagedProfile());
    }
    
    private static void updateDataUsageStatForData(ContentResolver paramContentResolver, String paramString)
    {
      paramContentResolver.update(ContactsContract.DataUsageFeedback.FEEDBACK_URI.buildUpon().appendPath(paramString).appendQueryParameter("type", "call").build(), new ContentValues(), null, null);
    }
    
    private static void updateNormalizedNumber(Context paramContext, ContentResolver paramContentResolver, String paramString1, String paramString2)
    {
      if ((TextUtils.isEmpty(paramString2)) || (TextUtils.isEmpty(paramString1))) {
        return;
      }
      if (TextUtils.isEmpty(getCurrentCountryIso(paramContext))) {
        return;
      }
      paramContext = PhoneNumberUtils.formatNumberToE164(paramString2, getCurrentCountryIso(paramContext));
      if (TextUtils.isEmpty(paramContext)) {
        return;
      }
      paramString2 = new ContentValues();
      paramString2.put("data4", paramContext);
      paramContentResolver.update(ContactsContract.Data.CONTENT_URI, paramString2, "_id=?", new String[] { paramString1 });
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/provider/CallLog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */