package com.android.server.notification;

import android.app.Notification;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.Settings.Global;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.LruCache;
import android.util.Slog;
import com.android.internal.logging.MetricsLogger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class ValidateNotificationPeople
  implements NotificationSignalExtractor
{
  private static final boolean DEBUG = Log.isLoggable("ValidateNoPeople", 3);
  private static final boolean ENABLE_PEOPLE_VALIDATOR = true;
  private static final String[] LOOKUP_PROJECTION = { "_id", "starred" };
  private static final int MAX_PEOPLE = 10;
  static final float NONE = 0.0F;
  private static final int PEOPLE_CACHE_SIZE = 200;
  private static final String SETTING_ENABLE_PEOPLE_VALIDATOR = "validate_notification_people_enabled";
  static final float STARRED_CONTACT = 1.0F;
  private static final String TAG = "ValidateNoPeople";
  static final float VALID_CONTACT = 0.5F;
  private static final boolean VERBOSE = Log.isLoggable("ValidateNoPeople", 2);
  private Context mBaseContext;
  protected boolean mEnabled;
  private int mEvictionCount;
  private Handler mHandler;
  private ContentObserver mObserver;
  private LruCache<String, LookupResult> mPeopleCache;
  private NotificationUsageStats mUsageStats;
  private Map<Integer, Context> mUserToContextMap;
  
  private String getCacheKey(int paramInt, String paramString)
  {
    return Integer.toString(paramInt) + ":" + paramString;
  }
  
  private Context getContextAsUser(UserHandle paramUserHandle)
  {
    Object localObject1 = (Context)this.mUserToContextMap.get(Integer.valueOf(paramUserHandle.getIdentifier()));
    Object localObject2 = localObject1;
    if (localObject1 == null) {}
    try
    {
      localObject2 = this.mBaseContext.createPackageContextAsUser("android", 0, paramUserHandle);
      localObject1 = localObject2;
      this.mUserToContextMap.put(Integer.valueOf(paramUserHandle.getIdentifier()), localObject2);
      return (Context)localObject2;
    }
    catch (PackageManager.NameNotFoundException paramUserHandle)
    {
      Log.e("ValidateNoPeople", "failed to create package context for lookups", paramUserHandle);
    }
    return (Context)localObject1;
  }
  
  public static String[] getExtraPeople(Bundle paramBundle)
  {
    paramBundle = paramBundle.get("android.people");
    if ((paramBundle instanceof String[])) {
      return (String[])paramBundle;
    }
    int j;
    String[] arrayOfString;
    int i;
    if ((paramBundle instanceof ArrayList))
    {
      paramBundle = (ArrayList)paramBundle;
      if (paramBundle.isEmpty()) {
        return null;
      }
      if ((paramBundle.get(0) instanceof String)) {
        return (String[])paramBundle.toArray(new String[paramBundle.size()]);
      }
      if ((paramBundle.get(0) instanceof CharSequence))
      {
        j = paramBundle.size();
        arrayOfString = new String[j];
        i = 0;
        while (i < j)
        {
          arrayOfString[i] = ((CharSequence)paramBundle.get(i)).toString();
          i += 1;
        }
        return arrayOfString;
      }
      return null;
    }
    if ((paramBundle instanceof String)) {
      return new String[] { (String)paramBundle };
    }
    if ((paramBundle instanceof char[])) {
      return new String[] { new String((char[])paramBundle) };
    }
    if ((paramBundle instanceof CharSequence)) {
      return new String[] { ((CharSequence)paramBundle).toString() };
    }
    if ((paramBundle instanceof CharSequence[]))
    {
      paramBundle = (CharSequence[])paramBundle;
      j = paramBundle.length;
      arrayOfString = new String[j];
      i = 0;
      while (i < j)
      {
        arrayOfString[i] = paramBundle[i].toString();
        i += 1;
      }
      return arrayOfString;
    }
    return null;
  }
  
  private LookupResult resolveEmailContact(Context paramContext, String paramString)
  {
    return searchContacts(paramContext, Uri.withAppendedPath(ContactsContract.CommonDataKinds.Email.CONTENT_LOOKUP_URI, Uri.encode(paramString)));
  }
  
  private LookupResult resolvePhoneContact(Context paramContext, String paramString)
  {
    return searchContacts(paramContext, Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(paramString)));
  }
  
  /* Error */
  private LookupResult searchContacts(Context paramContext, Uri paramUri)
  {
    // Byte code:
    //   0: new 12	com/android/server/notification/ValidateNotificationPeople$LookupResult
    //   3: dup
    //   4: invokespecial 242	com/android/server/notification/ValidateNotificationPeople$LookupResult:<init>	()V
    //   7: astore 5
    //   9: aconst_null
    //   10: astore 4
    //   12: aconst_null
    //   13: astore_3
    //   14: aload_1
    //   15: invokevirtual 246	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   18: aload_2
    //   19: getstatic 122	com/android/server/notification/ValidateNotificationPeople:LOOKUP_PROJECTION	[Ljava/lang/String;
    //   22: aconst_null
    //   23: aconst_null
    //   24: aconst_null
    //   25: invokevirtual 252	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   28: astore_1
    //   29: aload_1
    //   30: ifnonnull +29 -> 59
    //   33: aload_1
    //   34: astore_3
    //   35: aload_1
    //   36: astore 4
    //   38: ldc 39
    //   40: ldc -2
    //   42: invokestatic 260	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   45: pop
    //   46: aload_1
    //   47: ifnull +9 -> 56
    //   50: aload_1
    //   51: invokeinterface 265 1 0
    //   56: aload 5
    //   58: areturn
    //   59: aload_1
    //   60: astore_3
    //   61: aload_1
    //   62: astore 4
    //   64: aload_1
    //   65: invokeinterface 268 1 0
    //   70: ifeq +44 -> 114
    //   73: aload_1
    //   74: astore_3
    //   75: aload_1
    //   76: astore 4
    //   78: aload 5
    //   80: aload_1
    //   81: invokevirtual 272	com/android/server/notification/ValidateNotificationPeople$LookupResult:mergeContact	(Landroid/database/Cursor;)V
    //   84: goto -25 -> 59
    //   87: astore_1
    //   88: aload_3
    //   89: astore 4
    //   91: ldc 39
    //   93: ldc_w 274
    //   96: aload_1
    //   97: invokestatic 276	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   100: pop
    //   101: aload_3
    //   102: ifnull +9 -> 111
    //   105: aload_3
    //   106: invokeinterface 265 1 0
    //   111: aload 5
    //   113: areturn
    //   114: aload_1
    //   115: ifnull -4 -> 111
    //   118: aload_1
    //   119: invokeinterface 265 1 0
    //   124: aload 5
    //   126: areturn
    //   127: astore_1
    //   128: aload 4
    //   130: ifnull +10 -> 140
    //   133: aload 4
    //   135: invokeinterface 265 1 0
    //   140: aload_1
    //   141: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	142	0	this	ValidateNotificationPeople
    //   0	142	1	paramContext	Context
    //   0	142	2	paramUri	Uri
    //   13	93	3	localContext	Context
    //   10	124	4	localObject	Object
    //   7	118	5	localLookupResult	LookupResult
    // Exception table:
    //   from	to	target	type
    //   14	29	87	java/lang/Throwable
    //   38	46	87	java/lang/Throwable
    //   64	73	87	java/lang/Throwable
    //   78	84	87	java/lang/Throwable
    //   14	29	127	finally
    //   38	46	127	finally
    //   64	73	127	finally
    //   78	84	127	finally
    //   91	101	127	finally
  }
  
  private RankingReconsideration validatePeople(Context paramContext, NotificationRecord paramNotificationRecord)
  {
    boolean bool2 = false;
    String str = paramNotificationRecord.getKey();
    Bundle localBundle = paramNotificationRecord.getNotification().extras;
    Object localObject = new float[1];
    paramContext = validatePeople(paramContext, str, localBundle, (float[])localObject);
    float f = localObject[0];
    paramNotificationRecord.setContactAffinity(f);
    if (paramContext == null)
    {
      localObject = this.mUsageStats;
      if (f > 0.0F) {}
      for (boolean bool1 = true;; bool1 = false)
      {
        if (f == 1.0F) {
          bool2 = true;
        }
        ((NotificationUsageStats)localObject).registerPeopleAffinity(paramNotificationRecord, bool1, bool2, true);
        return paramContext;
      }
    }
    paramContext.setRecord(paramNotificationRecord);
    return paramContext;
  }
  
  private PeopleRankingReconsideration validatePeople(Context paramContext, String paramString, Bundle arg3, float[] paramArrayOfFloat)
  {
    long l = SystemClock.elapsedRealtime();
    float f1 = 0.0F;
    if (??? == null) {
      return null;
    }
    String[] arrayOfString = getExtraPeople(???);
    if ((arrayOfString == null) || (arrayOfString.length == 0)) {
      return null;
    }
    if (VERBOSE) {
      Slog.i("ValidateNoPeople", "Validating: " + paramString + " for " + paramContext.getUserId());
    }
    LinkedList localLinkedList = new LinkedList();
    int i = 0;
    if ((i < arrayOfString.length) && (i < 10))
    {
      String str = arrayOfString[i];
      if (TextUtils.isEmpty(str)) {}
      for (;;)
      {
        i += 1;
        break;
        synchronized (this.mPeopleCache)
        {
          Object localObject = getCacheKey(paramContext.getUserId(), str);
          localObject = (LookupResult)this.mPeopleCache.get(localObject);
          if ((localObject == null) || (LookupResult.-wrap0((LookupResult)localObject))) {
            localLinkedList.add(str);
          }
          while (!DEBUG)
          {
            float f2 = f1;
            if (localObject != null) {
              f2 = Math.max(f1, ((LookupResult)localObject).getAffinity());
            }
            f1 = f2;
            break;
          }
          Slog.d("ValidateNoPeople", "using cached lookupResult");
        }
      }
    }
    paramArrayOfFloat[0] = f1;
    MetricsLogger.histogram(this.mBaseContext, "validate_people_cache_latency", (int)(SystemClock.elapsedRealtime() - l));
    if (localLinkedList.isEmpty())
    {
      if (VERBOSE) {
        Slog.i("ValidateNoPeople", "final affinity: " + f1);
      }
      return null;
    }
    if (DEBUG) {
      Slog.d("ValidateNoPeople", "Pending: future work scheduled for: " + paramString);
    }
    return new PeopleRankingReconsideration(paramContext, paramString, localLinkedList, null);
  }
  
  public float getContactAffinity(final UserHandle paramUserHandle, final Bundle paramBundle, int paramInt, float paramFloat)
  {
    if (DEBUG) {
      Slog.d("ValidateNoPeople", "checking affinity for " + paramUserHandle);
    }
    if (paramBundle == null) {
      return 0.0F;
    }
    String str = Long.toString(System.nanoTime());
    float[] arrayOfFloat = new float[1];
    paramUserHandle = getContextAsUser(paramUserHandle);
    if (paramUserHandle == null) {
      return 0.0F;
    }
    paramUserHandle = validatePeople(paramUserHandle, str, paramBundle, arrayOfFloat);
    float f2 = arrayOfFloat[0];
    float f1 = f2;
    if (paramUserHandle != null)
    {
      paramBundle = new Semaphore(0);
      AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable()
      {
        public void run()
        {
          paramUserHandle.work();
          paramBundle.release();
        }
      });
      long l = paramInt;
      try
      {
        if (!paramBundle.tryAcquire(l, TimeUnit.MILLISECONDS))
        {
          Slog.w("ValidateNoPeople", "Timeout while waiting for affinity: " + str + ". " + "Returning timeoutAffinity=" + paramFloat);
          return paramFloat;
        }
      }
      catch (InterruptedException paramUserHandle)
      {
        Slog.w("ValidateNoPeople", "InterruptedException while waiting for affinity: " + str + ". " + "Returning affinity=" + f2, paramUserHandle);
        return f2;
      }
      f1 = Math.max(paramUserHandle.getContactAffinity(), f2);
    }
    return f1;
  }
  
  public void initialize(Context paramContext, NotificationUsageStats paramNotificationUsageStats)
  {
    if (DEBUG) {
      Slog.d("ValidateNoPeople", "Initializing  " + getClass().getSimpleName() + ".");
    }
    this.mUserToContextMap = new ArrayMap();
    this.mBaseContext = paramContext;
    this.mUsageStats = paramNotificationUsageStats;
    this.mPeopleCache = new LruCache(200);
    if (1 == Settings.Global.getInt(this.mBaseContext.getContentResolver(), "validate_notification_people_enabled", 1)) {}
    for (boolean bool = true;; bool = false)
    {
      this.mEnabled = bool;
      if (this.mEnabled)
      {
        this.mHandler = new Handler();
        this.mObserver = new ContentObserver(this.mHandler)
        {
          public void onChange(boolean paramAnonymousBoolean, Uri paramAnonymousUri, int paramAnonymousInt)
          {
            super.onChange(paramAnonymousBoolean, paramAnonymousUri, paramAnonymousInt);
            if (((ValidateNotificationPeople.-get0()) || (ValidateNotificationPeople.-get3(ValidateNotificationPeople.this) % 100 == 0)) && (ValidateNotificationPeople.-get1())) {
              Slog.i("ValidateNoPeople", "mEvictionCount: " + ValidateNotificationPeople.-get3(ValidateNotificationPeople.this));
            }
            ValidateNotificationPeople.-get4(ValidateNotificationPeople.this).evictAll();
            paramAnonymousUri = ValidateNotificationPeople.this;
            ValidateNotificationPeople.-set0(paramAnonymousUri, ValidateNotificationPeople.-get3(paramAnonymousUri) + 1);
          }
        };
        this.mBaseContext.getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, this.mObserver, -1);
      }
      return;
    }
  }
  
  public RankingReconsideration process(NotificationRecord paramNotificationRecord)
  {
    if (!this.mEnabled)
    {
      if (VERBOSE) {
        Slog.i("ValidateNoPeople", "disabled");
      }
      return null;
    }
    if ((paramNotificationRecord == null) || (paramNotificationRecord.getNotification() == null))
    {
      if (VERBOSE) {
        Slog.i("ValidateNoPeople", "skipping empty notification");
      }
      return null;
    }
    if (paramNotificationRecord.getUserId() == -1)
    {
      if (VERBOSE) {
        Slog.i("ValidateNoPeople", "skipping global notification");
      }
      return null;
    }
    Context localContext = getContextAsUser(paramNotificationRecord.getUser());
    if (localContext == null)
    {
      if (VERBOSE) {
        Slog.i("ValidateNoPeople", "skipping notification that lacks a context");
      }
      return null;
    }
    return validatePeople(localContext, paramNotificationRecord);
  }
  
  public void setConfig(RankingConfig paramRankingConfig) {}
  
  private static class LookupResult
  {
    private static final long CONTACT_REFRESH_MILLIS = 3600000L;
    private float mAffinity = 0.0F;
    private final long mExpireMillis = System.currentTimeMillis() + 3600000L;
    
    private boolean isExpired()
    {
      return this.mExpireMillis < System.currentTimeMillis();
    }
    
    private boolean isInvalid()
    {
      if (this.mAffinity != 0.0F) {
        return isExpired();
      }
      return true;
    }
    
    public float getAffinity()
    {
      if (isInvalid()) {
        return 0.0F;
      }
      return this.mAffinity;
    }
    
    public void mergeContact(Cursor paramCursor)
    {
      boolean bool = false;
      this.mAffinity = Math.max(this.mAffinity, 0.5F);
      int i = paramCursor.getColumnIndex("_id");
      if (i >= 0)
      {
        i = paramCursor.getInt(i);
        if (ValidateNotificationPeople.-get0()) {
          Slog.d("ValidateNoPeople", "contact _ID is: " + i);
        }
        i = paramCursor.getColumnIndex("starred");
        if (i < 0) {
          break label151;
        }
        if (paramCursor.getInt(i) != 0) {
          bool = true;
        }
        if (bool) {
          this.mAffinity = Math.max(this.mAffinity, 1.0F);
        }
        if (ValidateNotificationPeople.-get0()) {
          Slog.d("ValidateNoPeople", "contact STARRED is: " + bool);
        }
      }
      label151:
      while (!ValidateNotificationPeople.-get0())
      {
        return;
        Slog.i("ValidateNoPeople", "invalid cursor: no _ID");
        break;
      }
      Slog.d("ValidateNoPeople", "invalid cursor: no STARRED");
    }
  }
  
  private class PeopleRankingReconsideration
    extends RankingReconsideration
  {
    private float mContactAffinity = 0.0F;
    private final Context mContext;
    private final LinkedList<String> mPendingLookups;
    private NotificationRecord mRecord;
    
    private PeopleRankingReconsideration(String paramString, LinkedList<String> paramLinkedList)
    {
      super();
      this.mContext = paramString;
      LinkedList localLinkedList;
      this.mPendingLookups = localLinkedList;
    }
    
    public void applyChangesLocked(NotificationRecord paramNotificationRecord)
    {
      float f = paramNotificationRecord.getContactAffinity();
      paramNotificationRecord.setContactAffinity(Math.max(this.mContactAffinity, f));
      if (ValidateNotificationPeople.-get1()) {
        Slog.i("ValidateNoPeople", "final affinity: " + paramNotificationRecord.getContactAffinity());
      }
    }
    
    public float getContactAffinity()
    {
      return this.mContactAffinity;
    }
    
    public void setRecord(NotificationRecord paramNotificationRecord)
    {
      this.mRecord = paramNotificationRecord;
    }
    
    public void work()
    {
      long l1 = SystemClock.elapsedRealtime();
      if (ValidateNotificationPeople.-get1()) {
        Slog.i("ValidateNoPeople", "Executing: validation for: " + this.mKey);
      }
      long l2 = System.currentTimeMillis();
      Object localObject3 = this.mPendingLookups.iterator();
      for (;;)
      {
        String str;
        Object localObject1;
        if (((Iterator)localObject3).hasNext())
        {
          str = (String)((Iterator)localObject3).next();
          localObject1 = Uri.parse(str);
          if ("tel".equals(((Uri)localObject1).getScheme()))
          {
            if (ValidateNotificationPeople.-get0()) {
              Slog.d("ValidateNoPeople", "checking telephone URI: " + str);
            }
            localObject1 = ValidateNotificationPeople.-wrap1(ValidateNotificationPeople.this, this.mContext, ((Uri)localObject1).getSchemeSpecificPart());
            label145:
            if (localObject1 == null) {
              break label428;
            }
          }
        }
        label428:
        NotificationUsageStats localNotificationUsageStats;
        boolean bool1;
        boolean bool2;
        synchronized (ValidateNotificationPeople.-get4(ValidateNotificationPeople.this))
        {
          str = ValidateNotificationPeople.-wrap3(ValidateNotificationPeople.this, this.mContext.getUserId(), str);
          ValidateNotificationPeople.-get4(ValidateNotificationPeople.this).put(str, localObject1);
          if (ValidateNotificationPeople.-get0()) {
            Slog.d("ValidateNoPeople", "lookup contactAffinity is " + ((ValidateNotificationPeople.LookupResult)localObject1).getAffinity());
          }
          this.mContactAffinity = Math.max(this.mContactAffinity, ((ValidateNotificationPeople.LookupResult)localObject1).getAffinity());
          continue;
          if ("mailto".equals(((Uri)localObject1).getScheme()))
          {
            if (ValidateNotificationPeople.-get0()) {
              Slog.d("ValidateNoPeople", "checking mailto URI: " + str);
            }
            localObject1 = ValidateNotificationPeople.-wrap0(ValidateNotificationPeople.this, this.mContext, ((Uri)localObject1).getSchemeSpecificPart());
            break label145;
          }
          if (str.startsWith(ContactsContract.Contacts.CONTENT_LOOKUP_URI.toString()))
          {
            if (ValidateNotificationPeople.-get0()) {
              Slog.d("ValidateNoPeople", "checking lookup URI: " + str);
            }
            localObject1 = ValidateNotificationPeople.-wrap2(ValidateNotificationPeople.this, this.mContext, (Uri)localObject1);
            break label145;
          }
          localObject1 = new ValidateNotificationPeople.LookupResult();
          Slog.w("ValidateNoPeople", "unsupported URI " + str);
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/notification/ValidateNotificationPeople.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */