package com.android.server.notification;

import android.app.Notification;
import android.app.Notification.Action;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.media.AudioAttributes;
import android.os.Bundle;
import android.os.UserHandle;
import android.service.notification.NotificationListenerService.Ranking;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import com.android.server.EventLogTags;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

public final class NotificationRecord
{
  static final boolean DBG = Log.isLoggable("NotificationRecord", 3);
  static final String TAG = "NotificationRecord";
  boolean isCanceled;
  public boolean isUpdate;
  private int mAuthoritativeRank;
  private float mContactAffinity;
  private final Context mContext;
  private long mCreationTimeMs;
  private String mGlobalSortKey;
  private int mImportance = 64536;
  private CharSequence mImportanceExplanation = null;
  private boolean mIntercept;
  boolean mIsSeen;
  final int mOriginalFlags;
  private int mPackagePriority;
  private int mPackageVisibility;
  private String mPeopleExplanation;
  private long mRankingTimeMs;
  private boolean mRecentlyIntrusive;
  private int mSuppressedVisualEffects = 0;
  private long mUpdateTimeMs;
  private String mUserExplanation;
  private int mUserImportance = 64536;
  private long mVisibleSinceMs;
  final StatusBarNotification sbn;
  NotificationUsageStats.SingleNotificationStats stats;
  
  public NotificationRecord(Context paramContext, StatusBarNotification paramStatusBarNotification)
  {
    this.sbn = paramStatusBarNotification;
    this.mOriginalFlags = paramStatusBarNotification.getNotification().flags;
    this.mRankingTimeMs = calculateRankingTimeMs(0L);
    this.mCreationTimeMs = paramStatusBarNotification.getPostTime();
    this.mUpdateTimeMs = this.mCreationTimeMs;
    this.mContext = paramContext;
    this.stats = new NotificationUsageStats.SingleNotificationStats();
    this.mImportance = defaultImportance();
  }
  
  private void applyUserImportance()
  {
    if (this.mUserImportance != 64536)
    {
      this.mImportance = this.mUserImportance;
      this.mImportanceExplanation = getUserExplanation();
    }
  }
  
  private long calculateRankingTimeMs(long paramLong)
  {
    Notification localNotification = getNotification();
    if ((localNotification.when != 0L) && (localNotification.when <= this.sbn.getPostTime())) {
      return localNotification.when;
    }
    if (paramLong > 0L) {
      return paramLong;
    }
    return this.sbn.getPostTime();
  }
  
  private int defaultImportance()
  {
    Notification localNotification = this.sbn.getNotification();
    int i = 3;
    if ((localNotification.flags & 0x80) != 0) {
      localNotification.priority = 2;
    }
    label96:
    boolean bool;
    switch (localNotification.priority)
    {
    default: 
      this.stats.requestedImportance = i;
      if (((localNotification.defaults & 0x1) != 0) || ((localNotification.defaults & 0x2) != 0)) {
        bool = true;
      }
      break;
    }
    for (;;)
    {
      this.stats.isNoisy = bool;
      int j = i;
      if (!bool)
      {
        j = i;
        if (i > 2) {
          j = 2;
        }
      }
      i = j;
      if (bool)
      {
        i = j;
        if (j < 3) {
          i = 3;
        }
      }
      if (localNotification.fullScreenIntent != null) {
        i = 5;
      }
      this.stats.naturalImportance = i;
      return i;
      i = 1;
      break;
      i = 2;
      break;
      i = 3;
      break;
      i = 4;
      break;
      i = 5;
      break;
      if (localNotification.sound != null) {
        break label96;
      }
      if (localNotification.vibrate != null) {
        bool = true;
      } else {
        bool = false;
      }
    }
  }
  
  private String getPeopleExplanation()
  {
    if (this.mPeopleExplanation == null) {
      this.mPeopleExplanation = this.mContext.getString(17040882);
    }
    return this.mPeopleExplanation;
  }
  
  private String getUserExplanation()
  {
    if (this.mUserExplanation == null) {
      this.mUserExplanation = this.mContext.getString(17040881);
    }
    return this.mUserExplanation;
  }
  
  /* Error */
  static String idDebugString(Context paramContext, String paramString, int paramInt)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnull +29 -> 30
    //   4: aload_0
    //   5: aload_1
    //   6: iconst_0
    //   7: invokevirtual 163	android/content/Context:createPackageContext	(Ljava/lang/String;I)Landroid/content/Context;
    //   10: astore_1
    //   11: aload_1
    //   12: astore_0
    //   13: aload_0
    //   14: invokevirtual 167	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   17: astore_0
    //   18: aload_0
    //   19: iload_2
    //   20: invokevirtual 172	android/content/res/Resources:getResourceName	(I)Ljava/lang/String;
    //   23: astore_0
    //   24: aload_0
    //   25: areturn
    //   26: astore_1
    //   27: goto -14 -> 13
    //   30: goto -17 -> 13
    //   33: astore_0
    //   34: ldc -82
    //   36: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	37	0	paramContext	Context
    //   0	37	1	paramString	String
    //   0	37	2	paramInt	int
    // Exception table:
    //   from	to	target	type
    //   4	11	26	android/content/pm/PackageManager$NameNotFoundException
    //   18	24	33	android/content/res/Resources$NotFoundException
  }
  
  public void copyRankingInformation(NotificationRecord paramNotificationRecord)
  {
    this.mContactAffinity = paramNotificationRecord.mContactAffinity;
    this.mRecentlyIntrusive = paramNotificationRecord.mRecentlyIntrusive;
    this.mPackagePriority = paramNotificationRecord.mPackagePriority;
    this.mPackageVisibility = paramNotificationRecord.mPackageVisibility;
    this.mIntercept = paramNotificationRecord.mIntercept;
    this.mRankingTimeMs = calculateRankingTimeMs(paramNotificationRecord.getRankingTimeMs());
    this.mCreationTimeMs = paramNotificationRecord.mCreationTimeMs;
    this.mVisibleSinceMs = paramNotificationRecord.mVisibleSinceMs;
    if ((paramNotificationRecord.sbn.getOverrideGroupKey() == null) || (this.sbn.isAppGroup())) {
      return;
    }
    this.sbn.setOverrideGroupKey(paramNotificationRecord.sbn.getOverrideGroupKey());
  }
  
  void dump(PrintWriter paramPrintWriter, String paramString, Context paramContext, boolean paramBoolean)
  {
    Notification localNotification = this.sbn.getNotification();
    Icon localIcon = localNotification.getSmallIcon();
    String str = String.valueOf(localIcon);
    Object localObject = str;
    if (localIcon != null)
    {
      localObject = str;
      if (localIcon.getType() == 2) {
        localObject = str + " / " + idDebugString(paramContext, localIcon.getResPackage(), localIcon.getResId());
      }
    }
    paramPrintWriter.println(paramString + this);
    paramPrintWriter.println(paramString + "  uid=" + this.sbn.getUid() + " userId=" + this.sbn.getUserId());
    paramPrintWriter.println(paramString + "  icon=" + (String)localObject);
    paramPrintWriter.println(paramString + "  pri=" + localNotification.priority);
    paramPrintWriter.println(paramString + "  key=" + this.sbn.getKey());
    paramPrintWriter.println(paramString + "  seen=" + this.mIsSeen);
    paramPrintWriter.println(paramString + "  groupKey=" + getGroupKey());
    paramPrintWriter.println(paramString + "  contentIntent=" + localNotification.contentIntent);
    paramPrintWriter.println(paramString + "  deleteIntent=" + localNotification.deleteIntent);
    paramPrintWriter.println(paramString + "  tickerText=" + localNotification.tickerText);
    paramPrintWriter.println(paramString + "  contentView=" + localNotification.contentView);
    paramPrintWriter.println(paramString + String.format("  defaults=0x%08x flags=0x%08x", new Object[] { Integer.valueOf(localNotification.defaults), Integer.valueOf(localNotification.flags) }));
    paramPrintWriter.println(paramString + "  sound=" + localNotification.sound);
    paramPrintWriter.println(paramString + "  audioStreamType=" + localNotification.audioStreamType);
    paramPrintWriter.println(paramString + "  audioAttributes=" + localNotification.audioAttributes);
    paramPrintWriter.println(paramString + String.format("  color=0x%08x", new Object[] { Integer.valueOf(localNotification.color) }));
    paramPrintWriter.println(paramString + "  vibrate=" + Arrays.toString(localNotification.vibrate));
    paramPrintWriter.println(paramString + String.format("  led=0x%08x onMs=%d offMs=%d", new Object[] { Integer.valueOf(localNotification.ledARGB), Integer.valueOf(localNotification.ledOnMS), Integer.valueOf(localNotification.ledOffMS) }));
    int j;
    int i;
    if ((localNotification.actions != null) && (localNotification.actions.length > 0))
    {
      paramPrintWriter.println(paramString + "  actions={");
      j = localNotification.actions.length;
      i = 0;
      if (i < j)
      {
        paramContext = localNotification.actions[i];
        if (paramContext != null)
        {
          localObject = paramContext.title;
          if (paramContext.actionIntent != null) {
            break label863;
          }
        }
        label863:
        for (paramContext = "null";; paramContext = paramContext.actionIntent.toString())
        {
          paramPrintWriter.println(String.format("%s    [%d] \"%s\" -> %s", new Object[] { paramString, Integer.valueOf(i), localObject, paramContext }));
          i += 1;
          break;
        }
      }
      paramPrintWriter.println(paramString + "  }");
    }
    if ((localNotification.extras != null) && (localNotification.extras.size() > 0))
    {
      paramPrintWriter.println(paramString + "  extras={");
      paramContext = localNotification.extras.keySet().iterator();
      while (paramContext.hasNext())
      {
        localObject = (String)paramContext.next();
        paramPrintWriter.print(paramString + "    " + (String)localObject + "=");
        localObject = localNotification.extras.get((String)localObject);
        if (localObject == null)
        {
          paramPrintWriter.println("null");
        }
        else
        {
          paramPrintWriter.print(localObject.getClass().getSimpleName());
          if ((paramBoolean) && (((localObject instanceof CharSequence)) || ((localObject instanceof String)))) {}
          for (;;)
          {
            paramPrintWriter.println();
            break;
            if ((localObject instanceof Bitmap))
            {
              paramPrintWriter.print(String.format(" (%dx%d)", new Object[] { Integer.valueOf(((Bitmap)localObject).getWidth()), Integer.valueOf(((Bitmap)localObject).getHeight()) }));
            }
            else if (localObject.getClass().isArray())
            {
              j = Array.getLength(localObject);
              paramPrintWriter.print(" (" + j + ")");
              if (!paramBoolean)
              {
                i = 0;
                while (i < j)
                {
                  paramPrintWriter.println();
                  paramPrintWriter.print(String.format("%s      [%d] %s", new Object[] { paramString, Integer.valueOf(i), String.valueOf(Array.get(localObject, i)) }));
                  i += 1;
                }
              }
            }
            else
            {
              paramPrintWriter.print(" (" + String.valueOf(localObject) + ")");
            }
          }
        }
      }
      paramPrintWriter.println(paramString + "  }");
    }
    paramPrintWriter.println(paramString + "  stats=" + this.stats.toString());
    paramPrintWriter.println(paramString + "  mContactAffinity=" + this.mContactAffinity);
    paramPrintWriter.println(paramString + "  mRecentlyIntrusive=" + this.mRecentlyIntrusive);
    paramPrintWriter.println(paramString + "  mPackagePriority=" + this.mPackagePriority);
    paramPrintWriter.println(paramString + "  mPackageVisibility=" + this.mPackageVisibility);
    paramPrintWriter.println(paramString + "  mUserImportance=" + NotificationListenerService.Ranking.importanceToString(this.mUserImportance));
    paramPrintWriter.println(paramString + "  mImportance=" + NotificationListenerService.Ranking.importanceToString(this.mImportance));
    paramPrintWriter.println(paramString + "  mImportanceExplanation=" + this.mImportanceExplanation);
    paramPrintWriter.println(paramString + "  mIntercept=" + this.mIntercept);
    paramPrintWriter.println(paramString + "  mGlobalSortKey=" + this.mGlobalSortKey);
    paramPrintWriter.println(paramString + "  mRankingTimeMs=" + this.mRankingTimeMs);
    paramPrintWriter.println(paramString + "  mCreationTimeMs=" + this.mCreationTimeMs);
    paramPrintWriter.println(paramString + "  mVisibleSinceMs=" + this.mVisibleSinceMs);
    paramPrintWriter.println(paramString + "  mUpdateTimeMs=" + this.mUpdateTimeMs);
    paramPrintWriter.println(paramString + "  mSuppressedVisualEffects= " + this.mSuppressedVisualEffects);
  }
  
  public int getAuthoritativeRank()
  {
    return this.mAuthoritativeRank;
  }
  
  public float getContactAffinity()
  {
    return this.mContactAffinity;
  }
  
  public int getExposureMs(long paramLong)
  {
    if (this.mVisibleSinceMs == 0L) {
      return 0;
    }
    return (int)(paramLong - this.mVisibleSinceMs);
  }
  
  public int getFlags()
  {
    return this.sbn.getNotification().flags;
  }
  
  public int getFreshnessMs(long paramLong)
  {
    return (int)(paramLong - this.mUpdateTimeMs);
  }
  
  public String getGlobalSortKey()
  {
    return this.mGlobalSortKey;
  }
  
  public String getGroupKey()
  {
    return this.sbn.getGroupKey();
  }
  
  public int getImportance()
  {
    return this.mImportance;
  }
  
  public CharSequence getImportanceExplanation()
  {
    return this.mImportanceExplanation;
  }
  
  public String getKey()
  {
    return this.sbn.getKey();
  }
  
  public int getLifespanMs(long paramLong)
  {
    return (int)(paramLong - this.mCreationTimeMs);
  }
  
  public int getNaturaltImportance()
  {
    return this.stats.naturalImportance;
  }
  
  public Notification getNotification()
  {
    return this.sbn.getNotification();
  }
  
  public int getPackagePriority()
  {
    return this.mPackagePriority;
  }
  
  public int getPackageVisibilityOverride()
  {
    return this.mPackageVisibility;
  }
  
  public long getRankingTimeMs()
  {
    return this.mRankingTimeMs;
  }
  
  public int getSuppressedVisualEffects()
  {
    return this.mSuppressedVisualEffects;
  }
  
  public UserHandle getUser()
  {
    return this.sbn.getUser();
  }
  
  public int getUserId()
  {
    return this.sbn.getUserId();
  }
  
  public int getUserImportance()
  {
    return this.mUserImportance;
  }
  
  public boolean isAudioAttributesUsage(int paramInt)
  {
    boolean bool2 = false;
    AudioAttributes localAudioAttributes = getNotification().audioAttributes;
    boolean bool1 = bool2;
    if (localAudioAttributes != null)
    {
      bool1 = bool2;
      if (localAudioAttributes.getUsage() == paramInt) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public boolean isAudioStream(int paramInt)
  {
    return getNotification().audioStreamType == paramInt;
  }
  
  public boolean isCategory(String paramString)
  {
    return Objects.equals(getNotification().category, paramString);
  }
  
  public boolean isImportanceFromUser()
  {
    return this.mImportance == this.mUserImportance;
  }
  
  public boolean isIntercepted()
  {
    return this.mIntercept;
  }
  
  public boolean isRecentlyIntrusive()
  {
    return this.mRecentlyIntrusive;
  }
  
  public boolean isSeen()
  {
    return this.mIsSeen;
  }
  
  public void setAuthoritativeRank(int paramInt)
  {
    this.mAuthoritativeRank = paramInt;
  }
  
  public void setContactAffinity(float paramFloat)
  {
    this.mContactAffinity = paramFloat;
    if ((this.mImportance < 3) && (this.mContactAffinity > 0.5F)) {
      setImportance(3, getPeopleExplanation());
    }
  }
  
  public void setGlobalSortKey(String paramString)
  {
    this.mGlobalSortKey = paramString;
  }
  
  public void setImportance(int paramInt, CharSequence paramCharSequence)
  {
    if (paramInt != 64536)
    {
      this.mImportance = paramInt;
      this.mImportanceExplanation = paramCharSequence;
    }
    applyUserImportance();
  }
  
  public boolean setIntercepted(boolean paramBoolean)
  {
    this.mIntercept = paramBoolean;
    return this.mIntercept;
  }
  
  public void setPackagePriority(int paramInt)
  {
    this.mPackagePriority = paramInt;
  }
  
  public void setPackageVisibilityOverride(int paramInt)
  {
    this.mPackageVisibility = paramInt;
  }
  
  public void setRecentlyIntrusive(boolean paramBoolean)
  {
    this.mRecentlyIntrusive = paramBoolean;
  }
  
  public void setSeen()
  {
    this.mIsSeen = true;
  }
  
  public void setSuppressedVisualEffects(int paramInt)
  {
    this.mSuppressedVisualEffects = paramInt;
  }
  
  public void setUserImportance(int paramInt)
  {
    this.mUserImportance = paramInt;
    applyUserImportance();
  }
  
  public void setVisibility(boolean paramBoolean, int paramInt)
  {
    long l2 = System.currentTimeMillis();
    long l1;
    String str;
    if (paramBoolean)
    {
      l1 = l2;
      this.mVisibleSinceMs = l1;
      this.stats.onVisibilityChanged(paramBoolean);
      str = getKey();
      if (!paramBoolean) {
        break label73;
      }
    }
    label73:
    for (int i = 1;; i = 0)
    {
      EventLogTags.writeNotificationVisibility(str, i, (int)(l2 - this.mCreationTimeMs), (int)(l2 - this.mUpdateTimeMs), 0, paramInt);
      return;
      l1 = this.mVisibleSinceMs;
      break;
    }
  }
  
  public final String toString()
  {
    return String.format("NotificationRecord(0x%08x: pkg=%s user=%s id=%d tag=%s importance=%d key=%s: %s)", new Object[] { Integer.valueOf(System.identityHashCode(this)), this.sbn.getPackageName(), this.sbn.getUser(), Integer.valueOf(this.sbn.getId()), this.sbn.getTag(), Integer.valueOf(this.mImportance), this.sbn.getKey(), this.sbn.getNotification() });
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/notification/NotificationRecord.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */