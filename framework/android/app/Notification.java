package android.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.media.AudioAttributes;
import android.media.AudioAttributes.Builder;
import android.media.session.MediaSession.Token;
import android.net.Uri;
import android.os.BadParcelableException;
import android.os.BaseBundle;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import android.os.UserHandle;
import android.text.BidiFormatter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.TextAppearanceSpan;
import android.util.ArraySet;
import android.util.Log;
import android.util.SparseArray;
import android.widget.RemoteViews;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.NotificationColorUtil;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Notification
  implements Parcelable
{
  public static final AudioAttributes AUDIO_ATTRIBUTES_DEFAULT = new AudioAttributes.Builder().setContentType(4).setUsage(5).build();
  public static final String CATEGORY_ALARM = "alarm";
  public static final String CATEGORY_CALL = "call";
  public static final String CATEGORY_EMAIL = "email";
  public static final String CATEGORY_ERROR = "err";
  public static final String CATEGORY_EVENT = "event";
  public static final String CATEGORY_MESSAGE = "msg";
  public static final String CATEGORY_PROGRESS = "progress";
  public static final String CATEGORY_PROMO = "promo";
  public static final String CATEGORY_RECOMMENDATION = "recommendation";
  public static final String CATEGORY_REMINDER = "reminder";
  public static final String CATEGORY_SERVICE = "service";
  public static final String CATEGORY_SOCIAL = "social";
  public static final String CATEGORY_STATUS = "status";
  public static final String CATEGORY_SYSTEM = "sys";
  public static final String CATEGORY_TRANSPORT = "transport";
  public static final int COLOR_DEFAULT = 0;
  private static final int COLOR_INVALID = 1;
  public static final Parcelable.Creator<Notification> CREATOR = new Parcelable.Creator()
  {
    public Notification createFromParcel(Parcel paramAnonymousParcel)
    {
      return new Notification(paramAnonymousParcel);
    }
    
    public Notification[] newArray(int paramAnonymousInt)
    {
      return new Notification[paramAnonymousInt];
    }
  };
  public static final int DEFAULT_ALL = -1;
  public static final int DEFAULT_LIGHTS = 4;
  public static final int DEFAULT_SOUND = 1;
  public static final int DEFAULT_VIBRATE = 2;
  public static final String EXTRA_ALLOW_DURING_SETUP = "android.allowDuringSetup";
  public static final String EXTRA_BACKGROUND_IMAGE_URI = "android.backgroundImageUri";
  public static final String EXTRA_BIG_TEXT = "android.bigText";
  public static final String EXTRA_BUILDER_APPLICATION_INFO = "android.appInfo";
  public static final String EXTRA_CHRONOMETER_COUNT_DOWN = "android.chronometerCountDown";
  public static final String EXTRA_COMPACT_ACTIONS = "android.compactActions";
  public static final String EXTRA_CONTAINS_CUSTOM_VIEW = "android.contains.customView";
  public static final String EXTRA_CONVERSATION_TITLE = "android.conversationTitle";
  public static final String EXTRA_INFO_TEXT = "android.infoText";
  public static final String EXTRA_LARGE_ICON = "android.largeIcon";
  public static final String EXTRA_LARGE_ICON_BIG = "android.largeIcon.big";
  public static final String EXTRA_MEDIA_SESSION = "android.mediaSession";
  public static final String EXTRA_MESSAGES = "android.messages";
  public static final String EXTRA_ORIGINATING_USERID = "android.originatingUserId";
  public static final String EXTRA_PEOPLE = "android.people";
  public static final String EXTRA_PICTURE = "android.picture";
  public static final String EXTRA_PROGRESS = "android.progress";
  public static final String EXTRA_PROGRESS_INDETERMINATE = "android.progressIndeterminate";
  public static final String EXTRA_PROGRESS_MAX = "android.progressMax";
  public static final String EXTRA_REMOTE_INPUT_HISTORY = "android.remoteInputHistory";
  public static final String EXTRA_SELF_DISPLAY_NAME = "android.selfDisplayName";
  public static final String EXTRA_SHOW_CHRONOMETER = "android.showChronometer";
  public static final String EXTRA_SHOW_WHEN = "android.showWhen";
  public static final String EXTRA_SMALL_ICON = "android.icon";
  public static final String EXTRA_SUBSTITUTE_APP_NAME = "android.substName";
  public static final String EXTRA_SUB_TEXT = "android.subText";
  public static final String EXTRA_SUMMARY_TEXT = "android.summaryText";
  public static final String EXTRA_TEMPLATE = "android.template";
  public static final String EXTRA_TEXT = "android.text";
  public static final String EXTRA_TEXT_LINES = "android.textLines";
  public static final String EXTRA_TITLE = "android.title";
  public static final String EXTRA_TITLE_BIG = "android.title.big";
  public static final int FLAG_AUTOGROUP_SUMMARY = 1024;
  public static final int FLAG_AUTO_CANCEL = 16;
  public static final int FLAG_FOREGROUND_SERVICE = 64;
  public static final int FLAG_GROUP_SUMMARY = 512;
  public static final int FLAG_HIGH_PRIORITY = 128;
  public static final int FLAG_INSISTENT = 4;
  public static final int FLAG_LOCAL_ONLY = 256;
  public static final int FLAG_NO_CLEAR = 32;
  public static final int FLAG_ONGOING_EVENT = 2;
  public static final int FLAG_ONLY_ALERT_ONCE = 8;
  public static final int FLAG_SHOW_LIGHTS = 1;
  public static final int HIGHLIGHT_HINT_CHRONOMETER_START = 0;
  public static final int HIGHLIGHT_HINT_CHRONOMETER_STOP = 1;
  public static final String INTENT_CATEGORY_NOTIFICATION_PREFERENCES = "android.intent.category.NOTIFICATION_PREFERENCES";
  private static final int MAX_CHARSEQUENCE_LENGTH = 5120;
  private static final int MAX_REPLY_HISTORY = 5;
  public static final int PRIORITY_DEFAULT = 0;
  public static final int PRIORITY_HIGH = 1;
  public static final int PRIORITY_LOW = -1;
  public static final int PRIORITY_MAX = 2;
  public static final int PRIORITY_MIN = -2;
  @Deprecated
  public static final int STREAM_DEFAULT = -1;
  private static final String TAG = "Notification";
  public static final int VISIBILITY_PRIVATE = 0;
  public static final int VISIBILITY_PUBLIC = 1;
  public static final int VISIBILITY_SECRET = -1;
  public Action[] actions;
  public ArraySet<PendingIntent> allPendingIntents;
  public AudioAttributes audioAttributes = AUDIO_ATTRIBUTES_DEFAULT;
  @Deprecated
  public int audioStreamType = -1;
  @Deprecated
  public RemoteViews bigContentView;
  public String category;
  public int color = 0;
  public PendingIntent contentIntent;
  @Deprecated
  public RemoteViews contentView;
  private long creationTime;
  public int defaults;
  public PendingIntent deleteIntent;
  public Bundle extras = new Bundle();
  public int flags;
  public PendingIntent fullScreenIntent;
  @Deprecated
  public RemoteViews headsUpContentView;
  @Deprecated
  public int icon;
  public int iconLevel;
  @Deprecated
  public Bitmap largeIcon;
  public int ledARGB;
  public int ledOffMS;
  public int ledOnMS;
  private Intent mActionIntentOnStatusBar;
  private int mBackgroundColorOnStatusBar;
  private long mChronometerBase = 0L;
  private int mChronometerState = 0;
  private String mGroupKey;
  private Icon mLargeIcon;
  private int mPriorityOnStatusBar = 0;
  private boolean mShowChronometerOnStatusBar = false;
  private boolean mShowOnStatusBar = false;
  private Icon mSmallIcon;
  private String mSortKey;
  private int mStatusBarIcon = -1;
  private int mTextOnStatusBar;
  public int number;
  public int priority;
  public Notification publicVersion;
  public Uri sound;
  public CharSequence tickerText;
  @Deprecated
  public RemoteViews tickerView;
  public long[] vibrate;
  public int visibility;
  public long when;
  
  public Notification()
  {
    this.when = System.currentTimeMillis();
    this.creationTime = System.currentTimeMillis();
    this.priority = 0;
  }
  
  @Deprecated
  public Notification(int paramInt, CharSequence paramCharSequence, long paramLong)
  {
    this.icon = paramInt;
    this.tickerText = paramCharSequence;
    this.when = paramLong;
    this.creationTime = System.currentTimeMillis();
  }
  
  public Notification(Context paramContext, int paramInt, CharSequence paramCharSequence1, long paramLong, CharSequence paramCharSequence2, CharSequence paramCharSequence3, Intent paramIntent)
  {
    new Builder(paramContext).setWhen(paramLong).setSmallIcon(paramInt).setTicker(paramCharSequence1).setContentTitle(paramCharSequence2).setContentText(paramCharSequence3).setContentIntent(PendingIntent.getActivity(paramContext, 0, paramIntent, 0)).buildInto(this);
  }
  
  public Notification(Parcel paramParcel)
  {
    readFromParcelImpl(paramParcel);
    this.allPendingIntents = paramParcel.readArraySet(null);
  }
  
  public static void addFieldsFromContext(Context paramContext, Notification paramNotification)
  {
    addFieldsFromContext(paramContext.getApplicationInfo(), paramContext.getUserId(), paramNotification);
  }
  
  public static void addFieldsFromContext(ApplicationInfo paramApplicationInfo, int paramInt, Notification paramNotification)
  {
    paramNotification.extras.putParcelable("android.appInfo", paramApplicationInfo);
    paramNotification.extras.putInt("android.originatingUserId", paramInt);
  }
  
  private static Notification[] getNotificationArrayFromBundle(Bundle paramBundle, String paramString)
  {
    Object localObject = paramBundle.getParcelableArray(paramString);
    if (((localObject instanceof Notification[])) || (localObject == null)) {
      return (Notification[])localObject;
    }
    localObject = (Notification[])Arrays.copyOf((Object[])localObject, localObject.length, Notification[].class);
    paramBundle.putParcelableArray(paramString, (Parcelable[])localObject);
    return (Notification[])localObject;
  }
  
  private boolean hasLargeIcon()
  {
    return (this.mLargeIcon != null) || (this.largeIcon != null);
  }
  
  public static String priorityToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "UNKNOWN(" + String.valueOf(paramInt) + ")";
    case -2: 
      return "MIN";
    case -1: 
      return "LOW";
    case 0: 
      return "DEFAULT";
    case 1: 
      return "HIGH";
    }
    return "MAX";
  }
  
  private void readFromParcelImpl(Parcel paramParcel)
  {
    boolean bool2 = true;
    paramParcel.readInt();
    this.when = paramParcel.readLong();
    this.creationTime = paramParcel.readLong();
    if (paramParcel.readInt() != 0)
    {
      this.mSmallIcon = ((Icon)Icon.CREATOR.createFromParcel(paramParcel));
      if (this.mSmallIcon.getType() == 2) {
        this.icon = this.mSmallIcon.getResId();
      }
    }
    this.number = paramParcel.readInt();
    if (paramParcel.readInt() != 0) {
      this.contentIntent = ((PendingIntent)PendingIntent.CREATOR.createFromParcel(paramParcel));
    }
    if (paramParcel.readInt() != 0) {
      this.deleteIntent = ((PendingIntent)PendingIntent.CREATOR.createFromParcel(paramParcel));
    }
    if (paramParcel.readInt() != 0) {
      this.tickerText = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
    }
    if (paramParcel.readInt() != 0) {
      this.tickerView = ((RemoteViews)RemoteViews.CREATOR.createFromParcel(paramParcel));
    }
    if (paramParcel.readInt() != 0) {
      this.contentView = ((RemoteViews)RemoteViews.CREATOR.createFromParcel(paramParcel));
    }
    if (paramParcel.readInt() != 0) {
      this.mLargeIcon = ((Icon)Icon.CREATOR.createFromParcel(paramParcel));
    }
    this.defaults = paramParcel.readInt();
    this.flags = paramParcel.readInt();
    if (paramParcel.readInt() != 0) {
      this.sound = ((Uri)Uri.CREATOR.createFromParcel(paramParcel));
    }
    this.audioStreamType = paramParcel.readInt();
    if (paramParcel.readInt() != 0) {
      this.audioAttributes = ((AudioAttributes)AudioAttributes.CREATOR.createFromParcel(paramParcel));
    }
    this.vibrate = paramParcel.createLongArray();
    this.ledARGB = paramParcel.readInt();
    this.ledOnMS = paramParcel.readInt();
    this.ledOffMS = paramParcel.readInt();
    this.iconLevel = paramParcel.readInt();
    if (paramParcel.readInt() != 0) {
      this.fullScreenIntent = ((PendingIntent)PendingIntent.CREATOR.createFromParcel(paramParcel));
    }
    this.priority = paramParcel.readInt();
    this.category = paramParcel.readString();
    this.mGroupKey = paramParcel.readString();
    this.mSortKey = paramParcel.readString();
    this.extras = Bundle.setDefusable(paramParcel.readBundle(), true);
    this.actions = ((Action[])paramParcel.createTypedArray(Action.CREATOR));
    if (paramParcel.readInt() != 0) {
      this.bigContentView = ((RemoteViews)RemoteViews.CREATOR.createFromParcel(paramParcel));
    }
    if (paramParcel.readInt() != 0) {
      this.headsUpContentView = ((RemoteViews)RemoteViews.CREATOR.createFromParcel(paramParcel));
    }
    this.visibility = paramParcel.readInt();
    if (paramParcel.readInt() != 0) {
      this.publicVersion = ((Notification)CREATOR.createFromParcel(paramParcel));
    }
    this.color = paramParcel.readInt();
    this.mStatusBarIcon = paramParcel.readInt();
    if (paramParcel.readByte() != 0)
    {
      bool1 = true;
      this.mShowOnStatusBar = bool1;
      if (paramParcel.readByte() == 0) {
        break label595;
      }
    }
    label595:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.mShowChronometerOnStatusBar = bool1;
      this.mChronometerBase = paramParcel.readLong();
      this.mChronometerState = paramParcel.readInt();
      this.mTextOnStatusBar = paramParcel.readInt();
      this.mBackgroundColorOnStatusBar = paramParcel.readInt();
      this.mPriorityOnStatusBar = paramParcel.readInt();
      if (paramParcel.readInt() != 0) {
        this.mActionIntentOnStatusBar = ((Intent)Intent.CREATOR.createFromParcel(paramParcel));
      }
      return;
      bool1 = false;
      break;
    }
  }
  
  private static CharSequence removeTextSizeSpans(CharSequence paramCharSequence)
  {
    if ((paramCharSequence instanceof Spanned))
    {
      Spanned localSpanned = (Spanned)paramCharSequence;
      Object[] arrayOfObject = localSpanned.getSpans(0, localSpanned.length(), Object.class);
      SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder(localSpanned.toString());
      int j = arrayOfObject.length;
      int i = 0;
      if (i < j)
      {
        paramCharSequence = arrayOfObject[i];
        Object localObject = paramCharSequence;
        if ((paramCharSequence instanceof CharacterStyle)) {
          localObject = ((CharacterStyle)paramCharSequence).getUnderlying();
        }
        if ((localObject instanceof TextAppearanceSpan)) {
          localObject = (TextAppearanceSpan)localObject;
        }
        for (localObject = new TextAppearanceSpan(((TextAppearanceSpan)localObject).getFamily(), ((TextAppearanceSpan)localObject).getTextStyle(), -1, ((TextAppearanceSpan)localObject).getTextColor(), ((TextAppearanceSpan)localObject).getLinkTextColor());; localObject = paramCharSequence)
        {
          localSpannableStringBuilder.setSpan(localObject, localSpanned.getSpanStart(paramCharSequence), localSpanned.getSpanEnd(paramCharSequence), localSpanned.getSpanFlags(paramCharSequence));
          do
          {
            i += 1;
            break;
          } while (((localObject instanceof RelativeSizeSpan)) || ((localObject instanceof AbsoluteSizeSpan)));
        }
      }
      return localSpannableStringBuilder;
    }
    return paramCharSequence;
  }
  
  public static CharSequence safeCharSequence(CharSequence paramCharSequence)
  {
    if (paramCharSequence == null) {
      return paramCharSequence;
    }
    CharSequence localCharSequence = paramCharSequence;
    if (paramCharSequence.length() > 5120) {
      localCharSequence = paramCharSequence.subSequence(0, 5120);
    }
    if ((localCharSequence instanceof Parcelable))
    {
      Log.e("Notification", "warning: " + localCharSequence.getClass().getCanonicalName() + " instance is a custom Parcelable and not allowed in Notification");
      return localCharSequence.toString();
    }
    return removeTextSizeSpans(localCharSequence);
  }
  
  public static String visibilityToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "UNKNOWN(" + String.valueOf(paramInt) + ")";
    case 0: 
      return "PRIVATE";
    case 1: 
      return "PUBLIC";
    }
    return "SECRET";
  }
  
  private void writeToParcelImpl(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(1);
    paramParcel.writeLong(this.when);
    paramParcel.writeLong(this.creationTime);
    if ((this.mSmallIcon == null) && (this.icon != 0)) {
      this.mSmallIcon = Icon.createWithResource("", this.icon);
    }
    if (this.mSmallIcon != null)
    {
      paramParcel.writeInt(1);
      this.mSmallIcon.writeToParcel(paramParcel, 0);
      paramParcel.writeInt(this.number);
      if (this.contentIntent == null) {
        break label592;
      }
      paramParcel.writeInt(1);
      this.contentIntent.writeToParcel(paramParcel, 0);
      label99:
      if (this.deleteIntent == null) {
        break label600;
      }
      paramParcel.writeInt(1);
      this.deleteIntent.writeToParcel(paramParcel, 0);
      label120:
      if (this.tickerText == null) {
        break label608;
      }
      paramParcel.writeInt(1);
      TextUtils.writeToParcel(this.tickerText, paramParcel, paramInt);
      label141:
      if (this.tickerView == null) {
        break label616;
      }
      paramParcel.writeInt(1);
      this.tickerView.writeToParcel(paramParcel, 0);
      label162:
      if (this.contentView == null) {
        break label624;
      }
      paramParcel.writeInt(1);
      this.contentView.writeToParcel(paramParcel, 0);
      label183:
      if ((this.mLargeIcon == null) && (this.largeIcon != null)) {
        this.mLargeIcon = Icon.createWithBitmap(this.largeIcon);
      }
      if (this.mLargeIcon == null) {
        break label632;
      }
      paramParcel.writeInt(1);
      this.mLargeIcon.writeToParcel(paramParcel, 0);
      label229:
      paramParcel.writeInt(this.defaults);
      paramParcel.writeInt(this.flags);
      if (this.sound == null) {
        break label640;
      }
      paramParcel.writeInt(1);
      this.sound.writeToParcel(paramParcel, 0);
      label266:
      paramParcel.writeInt(this.audioStreamType);
      if (this.audioAttributes == null) {
        break label648;
      }
      paramParcel.writeInt(1);
      this.audioAttributes.writeToParcel(paramParcel, 0);
      label295:
      paramParcel.writeLongArray(this.vibrate);
      paramParcel.writeInt(this.ledARGB);
      paramParcel.writeInt(this.ledOnMS);
      paramParcel.writeInt(this.ledOffMS);
      paramParcel.writeInt(this.iconLevel);
      if (this.fullScreenIntent == null) {
        break label656;
      }
      paramParcel.writeInt(1);
      this.fullScreenIntent.writeToParcel(paramParcel, 0);
      label356:
      paramParcel.writeInt(this.priority);
      paramParcel.writeString(this.category);
      paramParcel.writeString(this.mGroupKey);
      paramParcel.writeString(this.mSortKey);
      paramParcel.writeBundle(this.extras);
      paramParcel.writeTypedArray(this.actions, 0);
      if (this.bigContentView == null) {
        break label664;
      }
      paramParcel.writeInt(1);
      this.bigContentView.writeToParcel(paramParcel, 0);
      label426:
      if (this.headsUpContentView == null) {
        break label672;
      }
      paramParcel.writeInt(1);
      this.headsUpContentView.writeToParcel(paramParcel, 0);
      label447:
      paramParcel.writeInt(this.visibility);
      if (this.publicVersion == null) {
        break label680;
      }
      paramParcel.writeInt(1);
      this.publicVersion.writeToParcel(paramParcel, 0);
      label476:
      paramParcel.writeInt(this.color);
      paramParcel.writeInt(this.mStatusBarIcon);
      if (!this.mShowOnStatusBar) {
        break label688;
      }
      paramInt = 1;
      label501:
      paramParcel.writeByte((byte)paramInt);
      if (!this.mShowChronometerOnStatusBar) {
        break label693;
      }
    }
    label592:
    label600:
    label608:
    label616:
    label624:
    label632:
    label640:
    label648:
    label656:
    label664:
    label672:
    label680:
    label688:
    label693:
    for (paramInt = 1;; paramInt = 0)
    {
      paramParcel.writeByte((byte)paramInt);
      paramParcel.writeLong(this.mChronometerBase);
      paramParcel.writeInt(this.mChronometerState);
      paramParcel.writeInt(this.mTextOnStatusBar);
      paramParcel.writeInt(this.mBackgroundColorOnStatusBar);
      paramParcel.writeInt(this.mPriorityOnStatusBar);
      if (this.mActionIntentOnStatusBar == null) {
        break label698;
      }
      paramParcel.writeInt(1);
      this.mActionIntentOnStatusBar.writeToParcel(paramParcel, 0);
      return;
      paramParcel.writeInt(0);
      break;
      paramParcel.writeInt(0);
      break label99;
      paramParcel.writeInt(0);
      break label120;
      paramParcel.writeInt(0);
      break label141;
      paramParcel.writeInt(0);
      break label162;
      paramParcel.writeInt(0);
      break label183;
      paramParcel.writeInt(0);
      break label229;
      paramParcel.writeInt(0);
      break label266;
      paramParcel.writeInt(0);
      break label295;
      paramParcel.writeInt(0);
      break label356;
      paramParcel.writeInt(0);
      break label426;
      paramParcel.writeInt(0);
      break label447;
      paramParcel.writeInt(0);
      break label476;
      paramInt = 0;
      break label501;
    }
    label698:
    paramParcel.writeInt(0);
  }
  
  public boolean ShowChronometerOnStatusBar()
  {
    return this.mShowChronometerOnStatusBar;
  }
  
  public Notification clone()
  {
    Notification localNotification = new Notification();
    cloneInto(localNotification, true);
    return localNotification;
  }
  
  public void cloneInto(Notification paramNotification, boolean paramBoolean)
  {
    paramNotification.when = this.when;
    paramNotification.creationTime = this.creationTime;
    paramNotification.mSmallIcon = this.mSmallIcon;
    paramNotification.number = this.number;
    paramNotification.contentIntent = this.contentIntent;
    paramNotification.deleteIntent = this.deleteIntent;
    paramNotification.fullScreenIntent = this.fullScreenIntent;
    if (this.tickerText != null) {
      paramNotification.tickerText = this.tickerText.toString();
    }
    if ((paramBoolean) && (this.tickerView != null)) {
      paramNotification.tickerView = this.tickerView.clone();
    }
    if ((paramBoolean) && (this.contentView != null)) {
      paramNotification.contentView = this.contentView.clone();
    }
    if ((paramBoolean) && (this.mLargeIcon != null)) {
      paramNotification.mLargeIcon = this.mLargeIcon;
    }
    paramNotification.iconLevel = this.iconLevel;
    paramNotification.sound = this.sound;
    paramNotification.audioStreamType = this.audioStreamType;
    if (this.audioAttributes != null) {
      paramNotification.audioAttributes = new AudioAttributes.Builder(this.audioAttributes).build();
    }
    long[] arrayOfLong1 = this.vibrate;
    int i;
    if (arrayOfLong1 != null)
    {
      i = arrayOfLong1.length;
      long[] arrayOfLong2 = new long[i];
      paramNotification.vibrate = arrayOfLong2;
      System.arraycopy(arrayOfLong1, 0, arrayOfLong2, 0, i);
    }
    paramNotification.ledARGB = this.ledARGB;
    paramNotification.ledOnMS = this.ledOnMS;
    paramNotification.ledOffMS = this.ledOffMS;
    paramNotification.defaults = this.defaults;
    paramNotification.flags = this.flags;
    paramNotification.priority = this.priority;
    paramNotification.category = this.category;
    paramNotification.mGroupKey = this.mGroupKey;
    paramNotification.mSortKey = this.mSortKey;
    if (this.extras != null) {}
    try
    {
      paramNotification.extras = new Bundle(this.extras);
      paramNotification.extras.size();
      if (!ArrayUtils.isEmpty(this.allPendingIntents)) {
        paramNotification.allPendingIntents = new ArraySet(this.allPendingIntents);
      }
      if (this.actions != null)
      {
        paramNotification.actions = new Action[this.actions.length];
        i = 0;
        while (i < this.actions.length)
        {
          if (this.actions[i] != null) {
            paramNotification.actions[i] = this.actions[i].clone();
          }
          i += 1;
        }
      }
    }
    catch (BadParcelableException localBadParcelableException)
    {
      for (;;)
      {
        Log.e("Notification", "could not unparcel extras from notification: " + this, localBadParcelableException);
        paramNotification.extras = null;
      }
      if ((paramBoolean) && (this.bigContentView != null)) {
        paramNotification.bigContentView = this.bigContentView.clone();
      }
      if ((paramBoolean) && (this.headsUpContentView != null)) {
        paramNotification.headsUpContentView = this.headsUpContentView.clone();
      }
      paramNotification.visibility = this.visibility;
      if (this.publicVersion != null)
      {
        paramNotification.publicVersion = new Notification();
        this.publicVersion.cloneInto(paramNotification.publicVersion, paramBoolean);
      }
      paramNotification.color = this.color;
      if (!paramBoolean) {
        paramNotification.lightenPayload();
      }
      paramNotification.mStatusBarIcon = this.mStatusBarIcon;
      paramNotification.mShowOnStatusBar = this.mShowOnStatusBar;
      paramNotification.mShowChronometerOnStatusBar = this.mShowChronometerOnStatusBar;
      paramNotification.mChronometerBase = this.mChronometerBase;
      paramNotification.mChronometerState = this.mChronometerState;
      paramNotification.mTextOnStatusBar = this.mTextOnStatusBar;
      paramNotification.mBackgroundColorOnStatusBar = this.mBackgroundColorOnStatusBar;
      paramNotification.mPriorityOnStatusBar = this.mPriorityOnStatusBar;
      paramNotification.mActionIntentOnStatusBar = this.mActionIntentOnStatusBar;
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getBackgroundColorOnStatusBar()
  {
    return this.mBackgroundColorOnStatusBar;
  }
  
  public long getChronometerBase()
  {
    return this.mChronometerBase;
  }
  
  public int getChronometerState()
  {
    return this.mChronometerState;
  }
  
  public String getGroup()
  {
    return this.mGroupKey;
  }
  
  public Intent getIntentOnStatusBar()
  {
    return this.mActionIntentOnStatusBar;
  }
  
  public Icon getLargeIcon()
  {
    return this.mLargeIcon;
  }
  
  public int getPriorityOnStatusBar()
  {
    return this.mPriorityOnStatusBar;
  }
  
  public Icon getSmallIcon()
  {
    return this.mSmallIcon;
  }
  
  public String getSortKey()
  {
    return this.mSortKey;
  }
  
  public int getStatusBarIcon()
  {
    return this.mStatusBarIcon;
  }
  
  public int getTextOnStatusBar()
  {
    return this.mTextOnStatusBar;
  }
  
  public boolean isGroupChild()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mGroupKey != null)
    {
      bool1 = bool2;
      if ((this.flags & 0x200) == 0) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public boolean isGroupSummary()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mGroupKey != null)
    {
      bool1 = bool2;
      if ((this.flags & 0x200) != 0) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public final void lightenPayload()
  {
    this.tickerView = null;
    this.contentView = null;
    this.bigContentView = null;
    this.headsUpContentView = null;
    this.mLargeIcon = null;
    if ((this.extras == null) || (this.extras.isEmpty())) {}
    for (;;)
    {
      return;
      Object localObject1 = this.extras.keySet();
      int j = ((Set)localObject1).size();
      try
      {
        localObject1 = (String[])((Set)localObject1).toArray(new String[j]);
        int i = 0;
        while (i < j)
        {
          String str = localObject1[i];
          Object localObject2 = this.extras.get(str);
          if ((localObject2 != null) && (((localObject2 instanceof Parcelable)) || ((localObject2 instanceof Parcelable[])) || ((localObject2 instanceof SparseArray)) || ((localObject2 instanceof ArrayList)))) {
            this.extras.remove(str);
          }
          i += 1;
        }
        return;
      }
      catch (ArrayStoreException localArrayStoreException)
      {
        Log.e("Notification", "convert extras key set to array fail: " + this, localArrayStoreException);
      }
    }
  }
  
  @Deprecated
  public void setLatestEventInfo(Context paramContext, CharSequence paramCharSequence1, CharSequence paramCharSequence2, PendingIntent paramPendingIntent)
  {
    if (paramContext.getApplicationInfo().targetSdkVersion > 22) {
      Log.e("Notification", "setLatestEventInfo() is deprecated and you should feel deprecated.", new Throwable());
    }
    if (paramContext.getApplicationInfo().targetSdkVersion < 24) {
      this.extras.putBoolean("android.showWhen", true);
    }
    paramContext = new Builder(paramContext, this);
    if (paramCharSequence1 != null) {
      paramContext.setContentTitle(paramCharSequence1);
    }
    if (paramCharSequence2 != null) {
      paramContext.setContentText(paramCharSequence2);
    }
    paramContext.setContentIntent(paramPendingIntent);
    paramContext.build();
  }
  
  public void setSmallIcon(Icon paramIcon)
  {
    this.mSmallIcon = paramIcon;
  }
  
  public boolean showOnStatusBar()
  {
    return this.mShowOnStatusBar;
  }
  
  public boolean showsChronometer()
  {
    if (this.when != 0L) {
      return this.extras.getBoolean("android.showChronometer");
    }
    return false;
  }
  
  public boolean showsTime()
  {
    if (this.when != 0L) {
      return this.extras.getBoolean("android.showWhen");
    }
    return false;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Notification(pri=");
    localStringBuilder.append(this.priority);
    localStringBuilder.append(" contentView=");
    if (this.contentView != null)
    {
      localStringBuilder.append(this.contentView.getPackage());
      localStringBuilder.append("/0x");
      localStringBuilder.append(Integer.toHexString(this.contentView.getLayoutId()));
      localStringBuilder.append(" vibrate=");
      if ((this.defaults & 0x2) == 0) {
        break label373;
      }
      localStringBuilder.append("default");
      label100:
      localStringBuilder.append(" sound=");
      if ((this.defaults & 0x1) == 0) {
        break label466;
      }
      localStringBuilder.append("default");
    }
    for (;;)
    {
      if (this.tickerText != null) {
        localStringBuilder.append(" tick");
      }
      localStringBuilder.append(" defaults=0x");
      localStringBuilder.append(Integer.toHexString(this.defaults));
      localStringBuilder.append(" flags=0x");
      localStringBuilder.append(Integer.toHexString(this.flags));
      localStringBuilder.append(String.format(" color=0x%08x", new Object[] { Integer.valueOf(this.color) }));
      if (this.category != null)
      {
        localStringBuilder.append(" category=");
        localStringBuilder.append(this.category);
      }
      if (this.mGroupKey != null)
      {
        localStringBuilder.append(" groupKey=");
        localStringBuilder.append(this.mGroupKey);
      }
      if (this.mSortKey != null)
      {
        localStringBuilder.append(" sortKey=");
        localStringBuilder.append(this.mSortKey);
      }
      if (this.actions != null)
      {
        localStringBuilder.append(" actions=");
        localStringBuilder.append(this.actions.length);
      }
      localStringBuilder.append(" vis=");
      localStringBuilder.append(visibilityToString(this.visibility));
      if (this.publicVersion != null)
      {
        localStringBuilder.append(" publicVersion=");
        localStringBuilder.append(this.publicVersion.toString());
      }
      localStringBuilder.append(")");
      return localStringBuilder.toString();
      localStringBuilder.append("null");
      break;
      label373:
      if (this.vibrate != null)
      {
        int j = this.vibrate.length - 1;
        localStringBuilder.append("[");
        int i = 0;
        while (i < j)
        {
          localStringBuilder.append(this.vibrate[i]);
          localStringBuilder.append(',');
          i += 1;
        }
        if (j != -1) {
          localStringBuilder.append(this.vibrate[j]);
        }
        localStringBuilder.append("]");
        break label100;
      }
      localStringBuilder.append("null");
      break label100;
      label466:
      if (this.sound != null) {
        localStringBuilder.append(this.sound.toString());
      } else {
        localStringBuilder.append("null");
      }
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    if (this.allPendingIntents == null) {}
    for (i = 1;; i = 0)
    {
      if (i != 0) {
        PendingIntent.setOnMarshaledListener(new -void_writeToParcel_android_os_Parcel_parcel_int_flags_LambdaImpl0(paramParcel));
      }
      try
      {
        writeToParcelImpl(paramParcel, paramInt);
        paramParcel.writeArraySet(this.allPendingIntents);
        return;
      }
      finally
      {
        if (i == 0) {
          break;
        }
        PendingIntent.setOnMarshaledListener(null);
      }
    }
  }
  
  public static class Action
    implements Parcelable
  {
    public static final Parcelable.Creator<Action> CREATOR = new Parcelable.Creator()
    {
      public Notification.Action createFromParcel(Parcel paramAnonymousParcel)
      {
        return new Notification.Action(paramAnonymousParcel, null);
      }
      
      public Notification.Action[] newArray(int paramAnonymousInt)
      {
        return new Notification.Action[paramAnonymousInt];
      }
    };
    public PendingIntent actionIntent;
    @Deprecated
    public int icon;
    private boolean mAllowGeneratedReplies = false;
    private final Bundle mExtras;
    private Icon mIcon;
    private final RemoteInput[] mRemoteInputs;
    public CharSequence title;
    
    @Deprecated
    public Action(int paramInt, CharSequence paramCharSequence, PendingIntent paramPendingIntent)
    {
      this(Icon.createWithResource("", paramInt), paramCharSequence, paramPendingIntent, new Bundle(), null, false);
    }
    
    private Action(Icon paramIcon, CharSequence paramCharSequence, PendingIntent paramPendingIntent, Bundle paramBundle, RemoteInput[] paramArrayOfRemoteInput, boolean paramBoolean)
    {
      this.mIcon = paramIcon;
      if ((paramIcon != null) && (paramIcon.getType() == 2)) {
        this.icon = paramIcon.getResId();
      }
      this.title = paramCharSequence;
      this.actionIntent = paramPendingIntent;
      if (paramBundle != null) {}
      for (;;)
      {
        this.mExtras = paramBundle;
        this.mRemoteInputs = paramArrayOfRemoteInput;
        this.mAllowGeneratedReplies = paramBoolean;
        return;
        paramBundle = new Bundle();
      }
    }
    
    private Action(Parcel paramParcel)
    {
      if (paramParcel.readInt() != 0)
      {
        this.mIcon = ((Icon)Icon.CREATOR.createFromParcel(paramParcel));
        if (this.mIcon.getType() == 2) {
          this.icon = this.mIcon.getResId();
        }
      }
      this.title = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
      if (paramParcel.readInt() == 1) {
        this.actionIntent = ((PendingIntent)PendingIntent.CREATOR.createFromParcel(paramParcel));
      }
      this.mExtras = Bundle.setDefusable(paramParcel.readBundle(), true);
      this.mRemoteInputs = ((RemoteInput[])paramParcel.createTypedArray(RemoteInput.CREATOR));
      if (paramParcel.readInt() == 1) {}
      for (boolean bool = true;; bool = false)
      {
        this.mAllowGeneratedReplies = bool;
        return;
      }
    }
    
    public Action clone()
    {
      Icon localIcon = getIcon();
      CharSequence localCharSequence = this.title;
      PendingIntent localPendingIntent = this.actionIntent;
      if (this.mExtras == null) {}
      for (Bundle localBundle = new Bundle();; localBundle = new Bundle(this.mExtras)) {
        return new Action(localIcon, localCharSequence, localPendingIntent, localBundle, getRemoteInputs(), getAllowGeneratedReplies());
      }
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public boolean getAllowGeneratedReplies()
    {
      return this.mAllowGeneratedReplies;
    }
    
    public Bundle getExtras()
    {
      return this.mExtras;
    }
    
    public Icon getIcon()
    {
      if ((this.mIcon == null) && (this.icon != 0)) {
        this.mIcon = Icon.createWithResource("", this.icon);
      }
      return this.mIcon;
    }
    
    public RemoteInput[] getRemoteInputs()
    {
      return this.mRemoteInputs;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      int i = 1;
      Icon localIcon = getIcon();
      if (localIcon != null)
      {
        paramParcel.writeInt(1);
        localIcon.writeToParcel(paramParcel, 0);
        TextUtils.writeToParcel(this.title, paramParcel, paramInt);
        if (this.actionIntent == null) {
          break label95;
        }
        paramParcel.writeInt(1);
        this.actionIntent.writeToParcel(paramParcel, paramInt);
        label55:
        paramParcel.writeBundle(this.mExtras);
        paramParcel.writeTypedArray(this.mRemoteInputs, paramInt);
        if (!this.mAllowGeneratedReplies) {
          break label103;
        }
      }
      label95:
      label103:
      for (paramInt = i;; paramInt = 0)
      {
        paramParcel.writeInt(paramInt);
        return;
        paramParcel.writeInt(0);
        break;
        paramParcel.writeInt(0);
        break label55;
      }
    }
    
    public static final class Builder
    {
      private boolean mAllowGeneratedReplies;
      private final Bundle mExtras;
      private final Icon mIcon;
      private final PendingIntent mIntent;
      private ArrayList<RemoteInput> mRemoteInputs;
      private final CharSequence mTitle;
      
      @Deprecated
      public Builder(int paramInt, CharSequence paramCharSequence, PendingIntent paramPendingIntent)
      {
        this(Icon.createWithResource("", paramInt), paramCharSequence, paramPendingIntent);
      }
      
      public Builder(Notification.Action paramAction)
      {
        this(paramAction.getIcon(), paramAction.title, paramAction.actionIntent, new Bundle(Notification.Action.-get0(paramAction)), paramAction.getRemoteInputs(), paramAction.getAllowGeneratedReplies());
      }
      
      public Builder(Icon paramIcon, CharSequence paramCharSequence, PendingIntent paramPendingIntent)
      {
        this(paramIcon, paramCharSequence, paramPendingIntent, new Bundle(), null, false);
      }
      
      private Builder(Icon paramIcon, CharSequence paramCharSequence, PendingIntent paramPendingIntent, Bundle paramBundle, RemoteInput[] paramArrayOfRemoteInput, boolean paramBoolean)
      {
        this.mIcon = paramIcon;
        this.mTitle = paramCharSequence;
        this.mIntent = paramPendingIntent;
        this.mExtras = paramBundle;
        if (paramArrayOfRemoteInput != null)
        {
          this.mRemoteInputs = new ArrayList(paramArrayOfRemoteInput.length);
          Collections.addAll(this.mRemoteInputs, paramArrayOfRemoteInput);
        }
        this.mAllowGeneratedReplies = paramBoolean;
      }
      
      public Builder addExtras(Bundle paramBundle)
      {
        if (paramBundle != null) {
          this.mExtras.putAll(paramBundle);
        }
        return this;
      }
      
      public Builder addRemoteInput(RemoteInput paramRemoteInput)
      {
        if (this.mRemoteInputs == null) {
          this.mRemoteInputs = new ArrayList();
        }
        this.mRemoteInputs.add(paramRemoteInput);
        return this;
      }
      
      public Notification.Action build()
      {
        if (this.mRemoteInputs != null) {}
        for (RemoteInput[] arrayOfRemoteInput = (RemoteInput[])this.mRemoteInputs.toArray(new RemoteInput[this.mRemoteInputs.size()]);; arrayOfRemoteInput = null) {
          return new Notification.Action(this.mIcon, this.mTitle, this.mIntent, this.mExtras, arrayOfRemoteInput, this.mAllowGeneratedReplies, null);
        }
      }
      
      public Builder extend(Notification.Action.Extender paramExtender)
      {
        paramExtender.extend(this);
        return this;
      }
      
      public Bundle getExtras()
      {
        return this.mExtras;
      }
      
      public Builder setAllowGeneratedReplies(boolean paramBoolean)
      {
        this.mAllowGeneratedReplies = paramBoolean;
        return this;
      }
    }
    
    public static abstract interface Extender
    {
      public abstract Notification.Action.Builder extend(Notification.Action.Builder paramBuilder);
    }
    
    public static final class WearableExtender
      implements Notification.Action.Extender
    {
      private static final int DEFAULT_FLAGS = 1;
      private static final String EXTRA_WEARABLE_EXTENSIONS = "android.wearable.EXTENSIONS";
      private static final int FLAG_AVAILABLE_OFFLINE = 1;
      private static final int FLAG_HINT_DISPLAY_INLINE = 4;
      private static final int FLAG_HINT_LAUNCHES_ACTIVITY = 2;
      private static final String KEY_CANCEL_LABEL = "cancelLabel";
      private static final String KEY_CONFIRM_LABEL = "confirmLabel";
      private static final String KEY_FLAGS = "flags";
      private static final String KEY_IN_PROGRESS_LABEL = "inProgressLabel";
      private CharSequence mCancelLabel;
      private CharSequence mConfirmLabel;
      private int mFlags = 1;
      private CharSequence mInProgressLabel;
      
      public WearableExtender() {}
      
      public WearableExtender(Notification.Action paramAction)
      {
        paramAction = paramAction.getExtras().getBundle("android.wearable.EXTENSIONS");
        if (paramAction != null)
        {
          this.mFlags = paramAction.getInt("flags", 1);
          this.mInProgressLabel = paramAction.getCharSequence("inProgressLabel");
          this.mConfirmLabel = paramAction.getCharSequence("confirmLabel");
          this.mCancelLabel = paramAction.getCharSequence("cancelLabel");
        }
      }
      
      private void setFlag(int paramInt, boolean paramBoolean)
      {
        if (paramBoolean)
        {
          this.mFlags |= paramInt;
          return;
        }
        this.mFlags &= paramInt;
      }
      
      public WearableExtender clone()
      {
        WearableExtender localWearableExtender = new WearableExtender();
        localWearableExtender.mFlags = this.mFlags;
        localWearableExtender.mInProgressLabel = this.mInProgressLabel;
        localWearableExtender.mConfirmLabel = this.mConfirmLabel;
        localWearableExtender.mCancelLabel = this.mCancelLabel;
        return localWearableExtender;
      }
      
      public Notification.Action.Builder extend(Notification.Action.Builder paramBuilder)
      {
        Bundle localBundle = new Bundle();
        if (this.mFlags != 1) {
          localBundle.putInt("flags", this.mFlags);
        }
        if (this.mInProgressLabel != null) {
          localBundle.putCharSequence("inProgressLabel", this.mInProgressLabel);
        }
        if (this.mConfirmLabel != null) {
          localBundle.putCharSequence("confirmLabel", this.mConfirmLabel);
        }
        if (this.mCancelLabel != null) {
          localBundle.putCharSequence("cancelLabel", this.mCancelLabel);
        }
        paramBuilder.getExtras().putBundle("android.wearable.EXTENSIONS", localBundle);
        return paramBuilder;
      }
      
      public CharSequence getCancelLabel()
      {
        return this.mCancelLabel;
      }
      
      public CharSequence getConfirmLabel()
      {
        return this.mConfirmLabel;
      }
      
      public boolean getHintDisplayActionInline()
      {
        boolean bool = false;
        if ((this.mFlags & 0x4) != 0) {
          bool = true;
        }
        return bool;
      }
      
      public boolean getHintLaunchesActivity()
      {
        boolean bool = false;
        if ((this.mFlags & 0x2) != 0) {
          bool = true;
        }
        return bool;
      }
      
      public CharSequence getInProgressLabel()
      {
        return this.mInProgressLabel;
      }
      
      public boolean isAvailableOffline()
      {
        boolean bool = false;
        if ((this.mFlags & 0x1) != 0) {
          bool = true;
        }
        return bool;
      }
      
      public WearableExtender setAvailableOffline(boolean paramBoolean)
      {
        setFlag(1, paramBoolean);
        return this;
      }
      
      public WearableExtender setCancelLabel(CharSequence paramCharSequence)
      {
        this.mCancelLabel = paramCharSequence;
        return this;
      }
      
      public WearableExtender setConfirmLabel(CharSequence paramCharSequence)
      {
        this.mConfirmLabel = paramCharSequence;
        return this;
      }
      
      public WearableExtender setHintDisplayActionInline(boolean paramBoolean)
      {
        setFlag(4, paramBoolean);
        return this;
      }
      
      public WearableExtender setHintLaunchesActivity(boolean paramBoolean)
      {
        setFlag(2, paramBoolean);
        return this;
      }
      
      public WearableExtender setInProgressLabel(CharSequence paramCharSequence)
      {
        this.mInProgressLabel = paramCharSequence;
        return this;
      }
    }
  }
  
  public static class BigPictureStyle
    extends Notification.Style
  {
    public static final int MIN_ASHMEM_BITMAP_SIZE = 131072;
    private Icon mBigLargeIcon;
    private boolean mBigLargeIconSet = false;
    private Bitmap mPicture;
    
    public BigPictureStyle() {}
    
    @Deprecated
    public BigPictureStyle(Notification.Builder paramBuilder)
    {
      setBuilder(paramBuilder);
    }
    
    public void addExtras(Bundle paramBundle)
    {
      super.addExtras(paramBundle);
      if (this.mBigLargeIconSet) {
        paramBundle.putParcelable("android.largeIcon.big", this.mBigLargeIcon);
      }
      paramBundle.putParcelable("android.picture", this.mPicture);
    }
    
    public BigPictureStyle bigLargeIcon(Bitmap paramBitmap)
    {
      Icon localIcon = null;
      if (paramBitmap != null) {
        localIcon = Icon.createWithBitmap(paramBitmap);
      }
      return bigLargeIcon(localIcon);
    }
    
    public BigPictureStyle bigLargeIcon(Icon paramIcon)
    {
      this.mBigLargeIconSet = true;
      this.mBigLargeIcon = paramIcon;
      return this;
    }
    
    public BigPictureStyle bigPicture(Bitmap paramBitmap)
    {
      this.mPicture = paramBitmap;
      return this;
    }
    
    public boolean hasSummaryInHeader()
    {
      return false;
    }
    
    public RemoteViews makeBigContentView()
    {
      Icon localIcon = null;
      Bitmap localBitmap = null;
      if (this.mBigLargeIconSet)
      {
        localIcon = Notification.-get1(Notification.Builder.-get2(this.mBuilder));
        Notification.-set6(Notification.Builder.-get2(this.mBuilder), this.mBigLargeIcon);
        localBitmap = Notification.Builder.-get2(this.mBuilder).largeIcon;
        Notification.Builder.-get2(this.mBuilder).largeIcon = null;
      }
      RemoteViews localRemoteViews = getStandardView(Notification.Builder.-wrap8(this.mBuilder));
      if (this.mSummaryTextSet)
      {
        localRemoteViews.setTextViewText(16908414, Notification.Builder.-wrap12(this.mBuilder, this.mSummaryText));
        localRemoteViews.setViewVisibility(16908414, 0);
      }
      this.mBuilder.setContentMinHeight(localRemoteViews, Notification.-wrap1(Notification.Builder.-get2(this.mBuilder)));
      if (this.mBigLargeIconSet)
      {
        Notification.-set6(Notification.Builder.-get2(this.mBuilder), localIcon);
        Notification.Builder.-get2(this.mBuilder).largeIcon = localBitmap;
      }
      localRemoteViews.setImageViewBitmap(16909244, this.mPicture);
      return localRemoteViews;
    }
    
    public void purgeResources()
    {
      super.purgeResources();
      if ((this.mPicture != null) && (this.mPicture.isMutable()) && (this.mPicture.getAllocationByteCount() >= 131072)) {
        this.mPicture = this.mPicture.createAshmemBitmap();
      }
      if (this.mBigLargeIcon != null) {
        this.mBigLargeIcon.convertToAshmem();
      }
    }
    
    protected void restoreFromExtras(Bundle paramBundle)
    {
      super.restoreFromExtras(paramBundle);
      if (paramBundle.containsKey("android.largeIcon.big"))
      {
        this.mBigLargeIconSet = true;
        this.mBigLargeIcon = ((Icon)paramBundle.getParcelable("android.largeIcon.big"));
      }
      this.mPicture = ((Bitmap)paramBundle.getParcelable("android.picture"));
    }
    
    public BigPictureStyle setBigContentTitle(CharSequence paramCharSequence)
    {
      internalSetBigContentTitle(Notification.safeCharSequence(paramCharSequence));
      return this;
    }
    
    public BigPictureStyle setSummaryText(CharSequence paramCharSequence)
    {
      internalSetSummaryText(Notification.safeCharSequence(paramCharSequence));
      return this;
    }
  }
  
  public static class BigTextStyle
    extends Notification.Style
  {
    private static final int LINES_CONSUMED_BY_ACTIONS = 4;
    private static final int MAX_LINES = 13;
    private CharSequence mBigText;
    
    public BigTextStyle() {}
    
    @Deprecated
    public BigTextStyle(Notification.Builder paramBuilder)
    {
      setBuilder(paramBuilder);
    }
    
    static void applyBigTextContentView(Notification.Builder paramBuilder, RemoteViews paramRemoteViews, CharSequence paramCharSequence)
    {
      paramRemoteViews.setTextViewText(16909245, paramCharSequence);
      if (TextUtils.isEmpty(paramCharSequence)) {}
      for (int i = 8;; i = 0)
      {
        paramRemoteViews.setViewVisibility(16909245, i);
        paramRemoteViews.setInt(16909245, "setMaxLines", calculateMaxLines(paramBuilder));
        paramRemoteViews.setBoolean(16909245, "setHasImage", Notification.-wrap1(Notification.Builder.-get2(paramBuilder)));
        return;
      }
    }
    
    private static int calculateMaxLines(Notification.Builder paramBuilder)
    {
      int i = 0;
      int j = 13;
      if (Notification.Builder.-get0(paramBuilder).size() > 0) {
        i = 1;
      }
      if (i != 0) {
        j = 9;
      }
      return j;
    }
    
    public void addExtras(Bundle paramBundle)
    {
      super.addExtras(paramBundle);
      paramBundle.putCharSequence("android.bigText", this.mBigText);
    }
    
    public BigTextStyle bigText(CharSequence paramCharSequence)
    {
      this.mBigText = Notification.safeCharSequence(paramCharSequence);
      return this;
    }
    
    public RemoteViews makeBigContentView()
    {
      CharSequence localCharSequence3 = Notification.Builder.-wrap0(this.mBuilder).getCharSequence("android.text");
      Notification.Builder.-wrap0(this.mBuilder).putCharSequence("android.text", null);
      RemoteViews localRemoteViews = getStandardView(Notification.Builder.-wrap9(this.mBuilder));
      Notification.Builder.-wrap0(this.mBuilder).putCharSequence("android.text", localCharSequence3);
      CharSequence localCharSequence2 = Notification.Builder.-wrap12(this.mBuilder, this.mBigText);
      CharSequence localCharSequence1 = localCharSequence2;
      if (TextUtils.isEmpty(localCharSequence2)) {
        localCharSequence1 = Notification.Builder.-wrap12(this.mBuilder, localCharSequence3);
      }
      applyBigTextContentView(this.mBuilder, localRemoteViews, localCharSequence1);
      return localRemoteViews;
    }
    
    protected void restoreFromExtras(Bundle paramBundle)
    {
      super.restoreFromExtras(paramBundle);
      this.mBigText = paramBundle.getCharSequence("android.bigText");
    }
    
    public BigTextStyle setBigContentTitle(CharSequence paramCharSequence)
    {
      internalSetBigContentTitle(Notification.safeCharSequence(paramCharSequence));
      return this;
    }
    
    public BigTextStyle setSummaryText(CharSequence paramCharSequence)
    {
      internalSetSummaryText(Notification.safeCharSequence(paramCharSequence));
      return this;
    }
  }
  
  public static class Builder
  {
    public static final String EXTRA_REBUILD_BIG_CONTENT_VIEW_ACTION_COUNT = "android.rebuild.bigViewActionCount";
    public static final String EXTRA_REBUILD_CONTENT_VIEW_ACTION_COUNT = "android.rebuild.contentViewActionCount";
    public static final String EXTRA_REBUILD_HEADS_UP_CONTENT_VIEW_ACTION_COUNT = "android.rebuild.hudViewActionCount";
    private static final int MAX_ACTION_BUTTONS = 3;
    private Intent mActionIntentOnStatusBar = null;
    private ArrayList<Notification.Action> mActions = new ArrayList(3);
    private int mBackgroundColorOnStatusBar;
    private int mCachedContrastColor = 1;
    private int mCachedContrastColorIsFor = 1;
    private long mChronometerBase = 0L;
    private int mChronometerState = 0;
    private NotificationColorUtil mColorUtil;
    private boolean mColorUtilInited = false;
    private Context mContext;
    private Notification mN;
    private ArrayList<String> mPersonList = new ArrayList();
    private int mPriorityOnStatusBar = 0;
    private boolean mShowChronometerOnStatusBar = false;
    private boolean mShowOnStatusBar = false;
    private int mStatusBarIcon = -1;
    private Notification.Style mStyle;
    private int mTextOnStatusBar = -1;
    private Bundle mUserExtras = new Bundle();
    
    public Builder(Context paramContext)
    {
      this(paramContext, null);
    }
    
    public Builder(Context paramContext, Notification paramNotification)
    {
      this.mContext = paramContext;
      if (paramNotification == null)
      {
        this.mN = new Notification();
        if (paramContext.getApplicationInfo().targetSdkVersion < 24) {
          this.mN.extras.putBoolean("android.showWhen", true);
        }
        this.mN.priority = 0;
        this.mN.visibility = 0;
      }
      for (;;)
      {
        return;
        this.mN = paramNotification;
        if (this.mN.actions != null) {
          Collections.addAll(this.mActions, this.mN.actions);
        }
        if (this.mN.extras.containsKey("android.people")) {
          Collections.addAll(this.mPersonList, this.mN.extras.getStringArray("android.people"));
        }
        if ((this.mN.getSmallIcon() == null) && (this.mN.icon != 0)) {
          setSmallIcon(this.mN.icon);
        }
        if ((this.mN.getLargeIcon() == null) && (this.mN.largeIcon != null)) {
          setLargeIcon(this.mN.largeIcon);
        }
        paramContext = this.mN.extras.getString("android.template");
        if (!TextUtils.isEmpty(paramContext))
        {
          paramNotification = getNotificationStyleClass(paramContext);
          if (paramNotification == null)
          {
            Log.d("Notification", "Unknown style class: " + paramContext);
            return;
          }
          try
          {
            paramContext = paramNotification.getDeclaredConstructor(new Class[0]);
            paramContext.setAccessible(true);
            paramContext = (Notification.Style)paramContext.newInstance(new Object[0]);
            paramContext.restoreFromExtras(this.mN.extras);
            if (paramContext != null)
            {
              setStyle(paramContext);
              return;
            }
          }
          catch (Throwable paramContext)
          {
            Log.e("Notification", "Could not create Style", paramContext);
          }
        }
      }
    }
    
    private void adaptNotificationHeaderForBigContentView(RemoteViews paramRemoteViews)
    {
      if (paramRemoteViews != null) {
        paramRemoteViews.setBoolean(16909232, "setExpanded", true);
      }
    }
    
    private RemoteViews applyStandardTemplate(int paramInt)
    {
      return applyStandardTemplate(paramInt, true);
    }
    
    private RemoteViews applyStandardTemplate(int paramInt, boolean paramBoolean)
    {
      Bundle localBundle = this.mN.extras;
      return applyStandardTemplate(paramInt, paramBoolean, processLegacyText(localBundle.getCharSequence("android.title")), processLegacyText(localBundle.getCharSequence("android.text")));
    }
    
    private RemoteViews applyStandardTemplate(int paramInt, boolean paramBoolean, CharSequence paramCharSequence1, CharSequence paramCharSequence2)
    {
      Notification.BuilderRemoteViews localBuilderRemoteViews = new Notification.BuilderRemoteViews(this.mContext.getApplicationInfo(), paramInt);
      resetStandardTemplate(localBuilderRemoteViews);
      Bundle localBundle = this.mN.extras;
      bindNotificationHeader(localBuilderRemoteViews);
      bindLargeIcon(localBuilderRemoteViews);
      paramBoolean = handleProgressBar(paramBoolean, localBuilderRemoteViews, localBundle);
      if (paramCharSequence1 != null)
      {
        localBuilderRemoteViews.setViewVisibility(16908310, 0);
        localBuilderRemoteViews.setTextViewText(16908310, paramCharSequence1);
        if (paramBoolean)
        {
          paramInt = -2;
          localBuilderRemoteViews.setViewLayoutWidth(16908310, paramInt);
        }
      }
      else
      {
        if (paramCharSequence2 != null)
        {
          if (!paramBoolean) {
            break label147;
          }
          paramInt = 16909256;
          label105:
          localBuilderRemoteViews.setTextViewText(paramInt, paramCharSequence2);
          localBuilderRemoteViews.setViewVisibility(paramInt, 0);
        }
        if (paramBoolean) {
          break label154;
        }
      }
      label147:
      label154:
      for (paramBoolean = Notification.-wrap1(this.mN);; paramBoolean = true)
      {
        setContentMinHeight(localBuilderRemoteViews, paramBoolean);
        return localBuilderRemoteViews;
        paramInt = -1;
        break;
        paramInt = 16908414;
        break label105;
      }
    }
    
    private RemoteViews applyStandardTemplateWithActions(int paramInt)
    {
      Bundle localBundle = this.mN.extras;
      return applyStandardTemplateWithActions(paramInt, true, processLegacyText(localBundle.getCharSequence("android.title")), processLegacyText(localBundle.getCharSequence("android.text")));
    }
    
    private RemoteViews applyStandardTemplateWithActions(int paramInt, boolean paramBoolean, CharSequence paramCharSequence1, CharSequence paramCharSequence2)
    {
      paramCharSequence1 = applyStandardTemplate(paramInt, paramBoolean, paramCharSequence1, paramCharSequence2);
      resetStandardTemplateWithActions(paramCharSequence1);
      int k = 0;
      paramInt = 0;
      int j = this.mActions.size();
      if (this.mN.fullScreenIntent != null)
      {
        paramBoolean = true;
        paramCharSequence1.setBoolean(16909221, "setEmphasizedMode", paramBoolean);
        if (j <= 0) {
          break label178;
        }
        paramCharSequence1.setViewVisibility(16909226, 0);
        paramCharSequence1.setViewVisibility(16909221, 0);
        paramCharSequence1.setViewLayoutMarginBottomDimen(16909242, 17104965);
        int i = j;
        if (j > 3) {
          i = 3;
        }
        j = 0;
        label99:
        k = paramInt;
        if (j >= i) {
          break label187;
        }
        paramCharSequence2 = (Notification.Action)this.mActions.get(j);
        paramInt |= hasValidRemoteInput(paramCharSequence2);
        if (j % 2 == 0) {
          break label172;
        }
      }
      label172:
      for (boolean bool = true;; bool = false)
      {
        paramCharSequence1.addView(16909221, generateActionButton(paramCharSequence2, paramBoolean, bool));
        j += 1;
        break label99;
        paramBoolean = false;
        break;
      }
      label178:
      paramCharSequence1.setViewVisibility(16909226, 8);
      label187:
      paramCharSequence2 = this.mN.extras.getCharSequenceArray("android.remoteInputHistory");
      if ((k == 0) || (paramCharSequence2 == null) || (paramCharSequence2.length <= 0) || (TextUtils.isEmpty(paramCharSequence2[0]))) {}
      do
      {
        do
        {
          return paramCharSequence1;
          paramCharSequence1.setViewVisibility(16909227, 0);
          paramCharSequence1.setTextViewText(16909231, paramCharSequence2[0]);
        } while ((paramCharSequence2.length <= 1) || (TextUtils.isEmpty(paramCharSequence2[1])));
        paramCharSequence1.setViewVisibility(16909230, 0);
        paramCharSequence1.setTextViewText(16909230, paramCharSequence2[1]);
      } while ((paramCharSequence2.length <= 2) || (TextUtils.isEmpty(paramCharSequence2[2])));
      paramCharSequence1.setViewVisibility(16909229, 0);
      paramCharSequence1.setTextViewText(16909229, paramCharSequence2[2]);
      return paramCharSequence1;
    }
    
    private void bindExpandButton(RemoteViews paramRemoteViews)
    {
      paramRemoteViews.setDrawableParameters(16909238, false, -1, resolveContrastColor(), PorterDuff.Mode.SRC_ATOP, -1);
      paramRemoteViews.setInt(16909232, "setOriginalNotificationColor", resolveContrastColor());
    }
    
    private void bindHeaderAppName(RemoteViews paramRemoteViews)
    {
      paramRemoteViews.setTextViewText(16909233, loadHeaderAppName());
      paramRemoteViews.setTextColor(16909233, resolveContrastColor());
    }
    
    private void bindHeaderChronometerAndTime(RemoteViews paramRemoteViews)
    {
      if (showsTimeOrChronometer())
      {
        paramRemoteViews.setViewVisibility(16909236, 0);
        if (this.mN.extras.getBoolean("android.showChronometer"))
        {
          paramRemoteViews.setViewVisibility(16909237, 0);
          paramRemoteViews.setLong(16909237, "setBase", this.mN.when + (SystemClock.elapsedRealtime() - System.currentTimeMillis()));
          paramRemoteViews.setBoolean(16909237, "setStarted", true);
          paramRemoteViews.setChronometerCountDown(16909237, this.mN.extras.getBoolean("android.chronometerCountDown"));
          return;
        }
        paramRemoteViews.setViewVisibility(16908437, 0);
        paramRemoteViews.setLong(16908437, "setTime", this.mN.when);
        return;
      }
      if (this.mN.when != 0L) {}
      for (long l = this.mN.when;; l = Notification.-get0(this.mN))
      {
        paramRemoteViews.setLong(16908437, "setTime", l);
        return;
      }
    }
    
    private void bindHeaderText(RemoteViews paramRemoteViews)
    {
      Object localObject2 = this.mN.extras.getCharSequence("android.subText");
      Object localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject1 = localObject2;
        if (this.mStyle != null)
        {
          localObject1 = localObject2;
          if (this.mStyle.mSummaryTextSet)
          {
            localObject1 = localObject2;
            if (this.mStyle.hasSummaryInHeader()) {
              localObject1 = this.mStyle.mSummaryText;
            }
          }
        }
      }
      localObject2 = localObject1;
      if (localObject1 == null)
      {
        localObject2 = localObject1;
        if (this.mContext.getApplicationInfo().targetSdkVersion < 24)
        {
          localObject2 = localObject1;
          if (this.mN.extras.getCharSequence("android.infoText") != null) {
            localObject2 = this.mN.extras.getCharSequence("android.infoText");
          }
        }
      }
      if (localObject2 != null)
      {
        paramRemoteViews.setTextViewText(16909235, processLegacyText((CharSequence)localObject2));
        paramRemoteViews.setViewVisibility(16909235, 0);
        paramRemoteViews.setViewVisibility(16909234, 0);
      }
    }
    
    private void bindLargeIcon(RemoteViews paramRemoteViews)
    {
      if ((Notification.-get1(this.mN) == null) && (this.mN.largeIcon != null)) {
        Notification.-set6(this.mN, Icon.createWithBitmap(this.mN.largeIcon));
      }
      if (Notification.-get1(this.mN) != null)
      {
        paramRemoteViews.setViewVisibility(16908356, 0);
        paramRemoteViews.setImageViewIcon(16908356, Notification.-get1(this.mN));
        processLargeLegacyIcon(Notification.-get1(this.mN), paramRemoteViews);
        paramRemoteViews.setViewLayoutMarginEndDimen(16909255, 17104963);
        paramRemoteViews.setViewLayoutMarginEndDimen(16908414, 17104963);
        paramRemoteViews.setViewLayoutMarginEndDimen(16908301, 17104963);
      }
    }
    
    private void bindNotificationHeader(RemoteViews paramRemoteViews)
    {
      bindSmallIcon(paramRemoteViews);
      bindHeaderAppName(paramRemoteViews);
      bindHeaderText(paramRemoteViews);
      bindHeaderChronometerAndTime(paramRemoteViews);
      bindExpandButton(paramRemoteViews);
      bindProfileBadge(paramRemoteViews);
    }
    
    private void bindProfileBadge(RemoteViews paramRemoteViews)
    {
      Bitmap localBitmap = getProfileBadge();
      if (localBitmap != null)
      {
        paramRemoteViews.setImageViewBitmap(16909239, localBitmap);
        paramRemoteViews.setViewVisibility(16909239, 0);
      }
    }
    
    private void bindSmallIcon(RemoteViews paramRemoteViews)
    {
      if ((Notification.-get2(this.mN) == null) && (this.mN.icon != 0)) {
        Notification.-set10(this.mN, Icon.createWithResource(this.mContext, this.mN.icon));
      }
      paramRemoteViews.setImageViewIcon(16908294, Notification.-get2(this.mN));
      paramRemoteViews.setDrawableParameters(16908294, false, -1, -1, null, this.mN.iconLevel);
      processSmallIconColor(Notification.-get2(this.mN), paramRemoteViews);
    }
    
    private CharSequence clearColorSpans(CharSequence paramCharSequence)
    {
      if ((paramCharSequence instanceof Spanned))
      {
        Spanned localSpanned = (Spanned)paramCharSequence;
        Object[] arrayOfObject = localSpanned.getSpans(0, localSpanned.length(), Object.class);
        SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder(localSpanned.toString());
        int j = arrayOfObject.length;
        int i = 0;
        if (i < j)
        {
          Object localObject = arrayOfObject[i];
          paramCharSequence = (CharSequence)localObject;
          if ((localObject instanceof CharacterStyle)) {
            paramCharSequence = ((CharacterStyle)localObject).getUnderlying();
          }
          TextAppearanceSpan localTextAppearanceSpan;
          if ((paramCharSequence instanceof TextAppearanceSpan))
          {
            localTextAppearanceSpan = (TextAppearanceSpan)paramCharSequence;
            if (localTextAppearanceSpan.getTextColor() == null) {}
          }
          for (paramCharSequence = new TextAppearanceSpan(localTextAppearanceSpan.getFamily(), localTextAppearanceSpan.getTextStyle(), localTextAppearanceSpan.getTextSize(), null, localTextAppearanceSpan.getLinkTextColor());; paramCharSequence = (CharSequence)localObject)
          {
            localSpannableStringBuilder.setSpan(paramCharSequence, localSpanned.getSpanStart(localObject), localSpanned.getSpanEnd(localObject), localSpanned.getSpanFlags(localObject));
            do
            {
              i += 1;
              break;
            } while (((paramCharSequence instanceof ForegroundColorSpan)) || ((paramCharSequence instanceof BackgroundColorSpan)));
          }
        }
        return localSpannableStringBuilder;
      }
      return paramCharSequence;
    }
    
    private CharSequence ensureColorSpanContrast(CharSequence paramCharSequence, int paramInt, ColorStateList[] paramArrayOfColorStateList)
    {
      if ((paramCharSequence instanceof Spanned))
      {
        Spanned localSpanned = (Spanned)paramCharSequence;
        Object[] arrayOfObject = localSpanned.getSpans(0, localSpanned.length(), Object.class);
        SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder(localSpanned.toString());
        int m = arrayOfObject.length;
        int i = 0;
        if (i < m)
        {
          Object localObject2 = arrayOfObject[i];
          Object localObject1 = localObject2;
          int n = localSpanned.getSpanStart(localObject2);
          int i1 = localSpanned.getSpanEnd(localObject2);
          if (i1 - n == paramCharSequence.length()) {}
          int[] arrayOfInt;
          int k;
          for (int j = 1;; j = 0)
          {
            if ((localObject2 instanceof CharacterStyle)) {
              localObject1 = ((CharacterStyle)localObject2).getUnderlying();
            }
            if (!(localObject1 instanceof TextAppearanceSpan)) {
              break label330;
            }
            localObject3 = (TextAppearanceSpan)localObject1;
            localColorStateList = ((TextAppearanceSpan)localObject3).getTextColor();
            if (localColorStateList == null) {
              break label301;
            }
            localObject1 = localColorStateList.getColors();
            arrayOfInt = new int[localObject1.length];
            k = 0;
            while (k < arrayOfInt.length)
            {
              arrayOfInt[k] = NotificationColorUtil.ensureLargeTextContrast(localObject1[k], paramInt);
              k += 1;
            }
          }
          ColorStateList localColorStateList = new ColorStateList((int[][])localColorStateList.getStates().clone(), arrayOfInt);
          Object localObject3 = new TextAppearanceSpan(((TextAppearanceSpan)localObject3).getFamily(), ((TextAppearanceSpan)localObject3).getTextStyle(), ((TextAppearanceSpan)localObject3).getTextSize(), localColorStateList, ((TextAppearanceSpan)localObject3).getLinkTextColor());
          localObject1 = localObject3;
          if (j != 0)
          {
            paramArrayOfColorStateList[0] = new ColorStateList((int[][])localColorStateList.getStates().clone(), arrayOfInt);
            localObject1 = localObject3;
          }
          for (;;)
          {
            label301:
            localSpannableStringBuilder.setSpan(localObject1, n, i1, localSpanned.getSpanFlags(localObject2));
            i += 1;
            break;
            label330:
            if ((localObject1 instanceof ForegroundColorSpan))
            {
              k = NotificationColorUtil.ensureLargeTextContrast(((ForegroundColorSpan)localObject1).getForegroundColor(), paramInt);
              localObject3 = new ForegroundColorSpan(k);
              localObject1 = localObject3;
              if (j != 0)
              {
                paramArrayOfColorStateList[0] = ColorStateList.valueOf(k);
                localObject1 = localObject3;
              }
            }
            else
            {
              localObject1 = localObject2;
            }
          }
        }
        return localSpannableStringBuilder;
      }
      return paramCharSequence;
    }
    
    private RemoteViews generateActionButton(Notification.Action paramAction, boolean paramBoolean1, boolean paramBoolean2)
    {
      int j;
      Object localObject;
      int i;
      label29:
      Notification.BuilderRemoteViews localBuilderRemoteViews;
      if (paramAction.actionIntent == null)
      {
        j = 1;
        localObject = this.mContext.getApplicationInfo();
        if (!paramBoolean1) {
          break label197;
        }
        i = getEmphasizedActionLayoutResource();
        localBuilderRemoteViews = new Notification.BuilderRemoteViews((ApplicationInfo)localObject, i);
        if (j == 0) {
          localBuilderRemoteViews.setOnClickPendingIntent(16909222, paramAction.actionIntent);
        }
        localBuilderRemoteViews.setContentDescription(16909222, paramAction.title);
        if (Notification.Action.-get1(paramAction) != null) {
          localBuilderRemoteViews.setRemoteInputs(16909222, Notification.Action.-get1(paramAction));
        }
        if (!paramBoolean1) {
          break label272;
        }
        localObject = this.mContext;
        if (!paramBoolean2) {
          break label220;
        }
        i = 17170516;
        label109:
        i = ((Context)localObject).getColor(i);
        localBuilderRemoteViews.setDrawableParameters(16909225, true, -1, i, PorterDuff.Mode.SRC_ATOP, -1);
        localObject = paramAction.title;
        paramAction = null;
        if (!isLegacy()) {
          break label228;
        }
        localObject = clearColorSpans((CharSequence)localObject);
        label157:
        localBuilderRemoteViews.setTextViewText(16909222, (CharSequence)localObject);
        if ((paramAction == null) || (paramAction[0] == null)) {
          break label247;
        }
        localBuilderRemoteViews.setTextColor(16909222, paramAction[0]);
      }
      label197:
      label220:
      label228:
      label247:
      label272:
      do
      {
        do
        {
          return localBuilderRemoteViews;
          j = 0;
          break;
          if (j != 0)
          {
            i = getActionTombstoneLayoutResource();
            break label29;
          }
          i = getActionLayoutResource();
          break label29;
          i = 17170517;
          break label109;
          paramAction = new ColorStateList[1];
          localObject = ensureColorSpanContrast((CharSequence)localObject, i, paramAction);
          break label157;
        } while (this.mN.color == 0);
        localBuilderRemoteViews.setTextColor(16909222, resolveContrastColor());
        return localBuilderRemoteViews;
        localBuilderRemoteViews.setTextViewText(16909222, processLegacyText(paramAction.title));
      } while (this.mN.color == 0);
      localBuilderRemoteViews.setTextColor(16909222, resolveContrastColor());
      return localBuilderRemoteViews;
    }
    
    private int getActionLayoutResource()
    {
      return 17367174;
    }
    
    private int getActionTombstoneLayoutResource()
    {
      return 17367177;
    }
    
    private Bundle getAllExtras()
    {
      Bundle localBundle = (Bundle)this.mUserExtras.clone();
      localBundle.putAll(this.mN.extras);
      return localBundle;
    }
    
    private int getBaseLayoutResource()
    {
      return 17367181;
    }
    
    private int getBigBaseLayoutResource()
    {
      return 17367182;
    }
    
    private int getBigPictureLayoutResource()
    {
      return 17367184;
    }
    
    private int getBigTextLayoutResource()
    {
      return 17367185;
    }
    
    private NotificationColorUtil getColorUtil()
    {
      if (!this.mColorUtilInited)
      {
        this.mColorUtilInited = true;
        if (this.mContext.getApplicationInfo().targetSdkVersion < 21) {
          this.mColorUtil = NotificationColorUtil.getInstance(this.mContext);
        }
      }
      return this.mColorUtil;
    }
    
    private int getEmphasizedActionLayoutResource()
    {
      return 17367175;
    }
    
    private int getInboxLayoutResource()
    {
      return 17367186;
    }
    
    private int getMessagingLayoutResource()
    {
      return 17367188;
    }
    
    private static Class<? extends Notification.Style> getNotificationStyleClass(String paramString)
    {
      int i = 0;
      Class[] arrayOfClass = new Class[7];
      arrayOfClass[0] = Notification.BigTextStyle.class;
      arrayOfClass[1] = Notification.BigPictureStyle.class;
      arrayOfClass[2] = Notification.InboxStyle.class;
      arrayOfClass[3] = Notification.MediaStyle.class;
      arrayOfClass[4] = Notification.DecoratedCustomViewStyle.class;
      arrayOfClass[5] = Notification.DecoratedMediaCustomViewStyle.class;
      arrayOfClass[6] = Notification.MessagingStyle.class;
      int j = arrayOfClass.length;
      while (i < j)
      {
        Class localClass = arrayOfClass[i];
        if (paramString.equals(localClass.getName())) {
          return localClass;
        }
        i += 1;
      }
      return null;
    }
    
    private Bitmap getProfileBadge()
    {
      Drawable localDrawable = getProfileBadgeDrawable();
      if (localDrawable == null) {
        return null;
      }
      int i = this.mContext.getResources().getDimensionPixelSize(17105019);
      Bitmap localBitmap = Bitmap.createBitmap(i, i, Bitmap.Config.ARGB_8888);
      Canvas localCanvas = new Canvas(localBitmap);
      localDrawable.setBounds(0, 0, i, i);
      localDrawable.draw(localCanvas);
      return localBitmap;
    }
    
    private Drawable getProfileBadgeDrawable()
    {
      if (this.mContext.getUserId() == 0) {
        return null;
      }
      return this.mContext.getPackageManager().getUserBadgeForDensityNoBackground(new UserHandle(this.mContext.getUserId()), 0);
    }
    
    private boolean handleProgressBar(boolean paramBoolean, RemoteViews paramRemoteViews, Bundle paramBundle)
    {
      int i = paramBundle.getInt("android.progressMax", 0);
      int j = paramBundle.getInt("android.progress", 0);
      boolean bool = paramBundle.getBoolean("android.progressIndeterminate");
      if ((paramBoolean) && ((i != 0) || (bool)))
      {
        paramRemoteViews.setViewVisibility(16908301, 0);
        paramRemoteViews.setProgressBar(16908301, i, j, bool);
        paramRemoteViews.setProgressBackgroundTintList(16908301, ColorStateList.valueOf(this.mContext.getColor(17170515)));
        if (this.mN.color != 0)
        {
          paramBundle = ColorStateList.valueOf(resolveContrastColor());
          paramRemoteViews.setProgressTintList(16908301, paramBundle);
          paramRemoteViews.setProgressIndeterminateTintList(16908301, paramBundle);
        }
        return true;
      }
      paramRemoteViews.setViewVisibility(16908301, 8);
      return false;
    }
    
    private boolean hasValidRemoteInput(Notification.Action paramAction)
    {
      if ((TextUtils.isEmpty(paramAction.title)) || (paramAction.actionIntent == null)) {
        return false;
      }
      paramAction = paramAction.getRemoteInputs();
      if (paramAction == null) {
        return false;
      }
      int j = paramAction.length;
      int i = 0;
      while (i < j)
      {
        Object localObject = paramAction[i];
        CharSequence[] arrayOfCharSequence = ((RemoteInput)localObject).getChoices();
        if ((((RemoteInput)localObject).getAllowFreeFormInput()) || ((arrayOfCharSequence != null) && (arrayOfCharSequence.length != 0))) {
          return true;
        }
        i += 1;
      }
      return false;
    }
    
    private void hideLine1Text(RemoteViews paramRemoteViews)
    {
      if (paramRemoteViews != null) {
        paramRemoteViews.setViewVisibility(16909256, 8);
      }
    }
    
    private boolean isLegacy()
    {
      return getColorUtil() != null;
    }
    
    public static Notification maybeCloneStrippedForDelivery(Notification paramNotification)
    {
      String str = paramNotification.extras.getString("android.template");
      if ((!TextUtils.isEmpty(str)) && (getNotificationStyleClass(str) == null)) {
        return paramNotification;
      }
      int i;
      int j;
      label93:
      int k;
      if ((paramNotification.contentView instanceof Notification.BuilderRemoteViews)) {
        if (paramNotification.extras.getInt("android.rebuild.contentViewActionCount", -1) == paramNotification.contentView.getSequenceNumber())
        {
          i = 1;
          if (!(paramNotification.bigContentView instanceof Notification.BuilderRemoteViews)) {
            break label209;
          }
          if (paramNotification.extras.getInt("android.rebuild.bigViewActionCount", -1) != paramNotification.bigContentView.getSequenceNumber()) {
            break label204;
          }
          j = 1;
          if (!(paramNotification.headsUpContentView instanceof Notification.BuilderRemoteViews)) {
            break label219;
          }
          if (paramNotification.extras.getInt("android.rebuild.hudViewActionCount", -1) != paramNotification.headsUpContentView.getSequenceNumber()) {
            break label214;
          }
          k = 1;
          label125:
          if ((i == 0) && (j == 0)) {
            break label224;
          }
        }
      }
      label204:
      label209:
      label214:
      label219:
      label224:
      while (k != 0)
      {
        paramNotification = paramNotification.clone();
        if (i != 0)
        {
          paramNotification.contentView = null;
          paramNotification.extras.remove("android.rebuild.contentViewActionCount");
        }
        if (j != 0)
        {
          paramNotification.bigContentView = null;
          paramNotification.extras.remove("android.rebuild.bigViewActionCount");
        }
        if (k != 0)
        {
          paramNotification.headsUpContentView = null;
          paramNotification.extras.remove("android.rebuild.hudViewActionCount");
        }
        return paramNotification;
        i = 0;
        break;
        i = 0;
        break;
        j = 0;
        break label93;
        j = 0;
        break label93;
        k = 0;
        break label125;
        k = 0;
        break label125;
      }
      return paramNotification;
    }
    
    private void processLargeLegacyIcon(Icon paramIcon, RemoteViews paramRemoteViews)
    {
      if ((paramIcon != null) && (isLegacy()) && (getColorUtil().isGrayscaleIcon(this.mContext, paramIcon))) {
        paramRemoteViews.setDrawableParameters(16908294, false, -1, resolveContrastColor(), PorterDuff.Mode.SRC_ATOP, -1);
      }
    }
    
    private CharSequence processLegacyText(CharSequence paramCharSequence)
    {
      if (isLegacy()) {
        return getColorUtil().invertCharSequenceColors(paramCharSequence);
      }
      return paramCharSequence;
    }
    
    private void processSmallIconColor(Icon paramIcon, RemoteViews paramRemoteViews)
    {
      int i = -1;
      if (isLegacy()) {}
      for (boolean bool = getColorUtil().isGrayscaleIcon(this.mContext, paramIcon);; bool = true)
      {
        if (bool) {
          paramRemoteViews.setDrawableParameters(16908294, false, -1, resolveContrastColor(), PorterDuff.Mode.SRC_ATOP, -1);
        }
        if (bool) {
          i = resolveContrastColor();
        }
        paramRemoteViews.setInt(16909232, "setOriginalIconColor", i);
        return;
      }
    }
    
    public static Builder recoverBuilder(Context paramContext, Notification paramNotification)
    {
      ApplicationInfo localApplicationInfo = (ApplicationInfo)paramNotification.extras.getParcelable("android.appInfo");
      if (localApplicationInfo != null) {}
      for (;;)
      {
        try
        {
          Context localContext = paramContext.createApplicationContext(localApplicationInfo, 4);
          paramContext = localContext;
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {
          Log.e("Notification", "ApplicationInfo " + localApplicationInfo + " not found");
          continue;
        }
        return new Builder(paramContext, paramNotification);
      }
    }
    
    private void resetContentMargins(RemoteViews paramRemoteViews)
    {
      paramRemoteViews.setViewLayoutMarginEndDimen(16909255, 0);
      paramRemoteViews.setViewLayoutMarginEndDimen(16908414, 0);
    }
    
    private void resetNotificationHeader(RemoteViews paramRemoteViews)
    {
      paramRemoteViews.setBoolean(16909232, "setExpanded", false);
      paramRemoteViews.setTextViewText(16909233, null);
      paramRemoteViews.setViewVisibility(16909237, 8);
      paramRemoteViews.setViewVisibility(16909235, 8);
      paramRemoteViews.setTextViewText(16909235, null);
      paramRemoteViews.setViewVisibility(16909234, 8);
      paramRemoteViews.setViewVisibility(16909236, 8);
      paramRemoteViews.setViewVisibility(16908437, 8);
      paramRemoteViews.setImageViewIcon(16909239, null);
      paramRemoteViews.setViewVisibility(16909239, 8);
    }
    
    private void resetStandardTemplate(RemoteViews paramRemoteViews)
    {
      resetNotificationHeader(paramRemoteViews);
      resetContentMargins(paramRemoteViews);
      paramRemoteViews.setViewVisibility(16908356, 8);
      paramRemoteViews.setViewVisibility(16908310, 8);
      paramRemoteViews.setTextViewText(16908310, null);
      paramRemoteViews.setViewVisibility(16908414, 8);
      paramRemoteViews.setTextViewText(16908414, null);
      paramRemoteViews.setViewVisibility(16909256, 8);
      paramRemoteViews.setTextViewText(16909256, null);
      paramRemoteViews.setViewVisibility(16908301, 8);
    }
    
    private void resetStandardTemplateWithActions(RemoteViews paramRemoteViews)
    {
      paramRemoteViews.setViewVisibility(16909221, 8);
      paramRemoteViews.removeAllViews(16909221);
      paramRemoteViews.setViewVisibility(16909227, 8);
      paramRemoteViews.setTextViewText(16909231, null);
      paramRemoteViews.setViewVisibility(16909230, 8);
      paramRemoteViews.setTextViewText(16909230, null);
      paramRemoteViews.setViewVisibility(16909229, 8);
      paramRemoteViews.setTextViewText(16909229, null);
      paramRemoteViews.setViewLayoutMarginBottomDimen(16909242, 0);
    }
    
    private void sanitizeColor()
    {
      if (this.mN.color != 0)
      {
        Notification localNotification = this.mN;
        localNotification.color |= 0xFF000000;
      }
    }
    
    private boolean showsTimeOrChronometer()
    {
      if (!this.mN.showsTime()) {
        return this.mN.showsChronometer();
      }
      return true;
    }
    
    @Deprecated
    public Builder addAction(int paramInt, CharSequence paramCharSequence, PendingIntent paramPendingIntent)
    {
      this.mActions.add(new Notification.Action(paramInt, Notification.safeCharSequence(paramCharSequence), paramPendingIntent));
      return this;
    }
    
    public Builder addAction(Notification.Action paramAction)
    {
      if (paramAction != null) {
        this.mActions.add(paramAction);
      }
      return this;
    }
    
    public Builder addExtras(Bundle paramBundle)
    {
      if (paramBundle != null) {
        this.mUserExtras.putAll(paramBundle);
      }
      return this;
    }
    
    public Builder addPerson(String paramString)
    {
      this.mPersonList.add(paramString);
      return this;
    }
    
    public Notification build()
    {
      if (this.mUserExtras != null) {
        this.mN.extras = getAllExtras();
      }
      Notification.-set0(this.mN, System.currentTimeMillis());
      Notification.addFieldsFromContext(this.mContext, this.mN);
      buildUnstyled();
      if (this.mStyle != null) {
        this.mStyle.buildStyled(this.mN);
      }
      if ((this.mContext.getApplicationInfo().targetSdkVersion >= 24) || ((this.mStyle != null) && (this.mStyle.displayCustomViewInline()))) {}
      for (;;)
      {
        if ((this.mN.defaults & 0x4) != 0)
        {
          Notification localNotification = this.mN;
          localNotification.flags |= 0x1;
        }
        return this.mN;
        if (this.mN.contentView == null)
        {
          this.mN.contentView = createContentView();
          this.mN.extras.putInt("android.rebuild.contentViewActionCount", this.mN.contentView.getSequenceNumber());
        }
        if (this.mN.bigContentView == null)
        {
          this.mN.bigContentView = createBigContentView();
          if (this.mN.bigContentView != null) {
            this.mN.extras.putInt("android.rebuild.bigViewActionCount", this.mN.bigContentView.getSequenceNumber());
          }
        }
        if (this.mN.headsUpContentView == null)
        {
          this.mN.headsUpContentView = createHeadsUpContentView();
          if (this.mN.headsUpContentView != null) {
            this.mN.extras.putInt("android.rebuild.hudViewActionCount", this.mN.headsUpContentView.getSequenceNumber());
          }
        }
      }
    }
    
    public Notification buildInto(Notification paramNotification)
    {
      build().cloneInto(paramNotification, true);
      return paramNotification;
    }
    
    public Notification buildUnstyled()
    {
      if (this.mActions.size() > 0)
      {
        this.mN.actions = new Notification.Action[this.mActions.size()];
        this.mActions.toArray(this.mN.actions);
      }
      if (!this.mPersonList.isEmpty()) {
        this.mN.extras.putStringArray("android.people", (String[])this.mPersonList.toArray(new String[this.mPersonList.size()]));
      }
      if ((this.mN.bigContentView != null) || (this.mN.contentView != null)) {}
      for (;;)
      {
        this.mN.extras.putBoolean("android.contains.customView", true);
        do
        {
          Notification.-set12(this.mN, this.mStatusBarIcon);
          Notification.-set9(this.mN, this.mShowOnStatusBar);
          Notification.-set8(this.mN, this.mShowChronometerOnStatusBar);
          Notification.-set3(this.mN, this.mChronometerBase);
          Notification.-set4(this.mN, this.mChronometerState);
          Notification.-set13(this.mN, this.mTextOnStatusBar);
          Notification.-set2(this.mN, this.mBackgroundColorOnStatusBar);
          Notification.-set7(this.mN, this.mPriorityOnStatusBar);
          Notification.-set1(this.mN, this.mActionIntentOnStatusBar);
          return this.mN;
        } while (this.mN.headsUpContentView == null);
      }
    }
    
    public RemoteViews createBigContentView()
    {
      RemoteViews localRemoteViews = null;
      if ((this.mN.bigContentView == null) || ((this.mStyle != null) && (this.mStyle.displayCustomViewInline())))
      {
        if (this.mStyle == null) {
          break label64;
        }
        localRemoteViews = this.mStyle.makeBigContentView();
        hideLine1Text(localRemoteViews);
      }
      for (;;)
      {
        adaptNotificationHeaderForBigContentView(localRemoteViews);
        return localRemoteViews;
        return this.mN.bigContentView;
        label64:
        if (this.mActions.size() != 0) {
          localRemoteViews = applyStandardTemplateWithActions(getBigBaseLayoutResource());
        }
      }
    }
    
    public RemoteViews createContentView()
    {
      if ((this.mN.contentView == null) || ((this.mStyle != null) && (this.mStyle.displayCustomViewInline())))
      {
        if (this.mStyle != null)
        {
          RemoteViews localRemoteViews = this.mStyle.makeContentView();
          if (localRemoteViews != null) {
            return localRemoteViews;
          }
        }
      }
      else {
        return this.mN.contentView;
      }
      return applyStandardTemplate(getBaseLayoutResource());
    }
    
    public RemoteViews createHeadsUpContentView()
    {
      if ((this.mN.headsUpContentView == null) || ((this.mStyle != null) && (this.mStyle.displayCustomViewInline())))
      {
        if (this.mStyle != null)
        {
          RemoteViews localRemoteViews = this.mStyle.makeHeadsUpContentView();
          if (localRemoteViews == null) {
            break label68;
          }
          return localRemoteViews;
        }
      }
      else {
        return this.mN.headsUpContentView;
      }
      if (this.mActions.size() == 0) {
        return null;
      }
      label68:
      return applyStandardTemplateWithActions(getBigBaseLayoutResource());
    }
    
    public Builder extend(Notification.Extender paramExtender)
    {
      paramExtender.extend(this);
      return this;
    }
    
    public Bundle getExtras()
    {
      return this.mUserExtras;
    }
    
    @Deprecated
    public Notification getNotification()
    {
      return build();
    }
    
    public String loadHeaderAppName()
    {
      Object localObject2 = null;
      PackageManager localPackageManager = this.mContext.getPackageManager();
      Object localObject1 = localObject2;
      String str;
      if (this.mN.extras.containsKey("android.substName"))
      {
        str = this.mContext.getPackageName();
        localObject1 = this.mN.extras.getString("android.substName");
        if (localPackageManager.checkPermission("android.permission.SUBSTITUTE_NOTIFICATION_APP_NAME", str) != 0) {
          break label93;
        }
      }
      for (;;)
      {
        localObject2 = localObject1;
        if (TextUtils.isEmpty((CharSequence)localObject1)) {
          localObject2 = localPackageManager.getApplicationLabel(this.mContext.getApplicationInfo());
        }
        if (!TextUtils.isEmpty((CharSequence)localObject2)) {
          break;
        }
        return null;
        label93:
        Log.w("Notification", "warning: pkg " + str + " attempting to substitute app name '" + (String)localObject1 + "' without holding perm " + "android.permission.SUBSTITUTE_NOTIFICATION_APP_NAME");
        localObject1 = localObject2;
      }
      return String.valueOf(localObject2);
    }
    
    public RemoteViews makeNotificationHeader()
    {
      Notification.BuilderRemoteViews localBuilderRemoteViews = new Notification.BuilderRemoteViews(this.mContext.getApplicationInfo(), 17367180);
      resetNotificationHeader(localBuilderRemoteViews);
      bindNotificationHeader(localBuilderRemoteViews);
      return localBuilderRemoteViews;
    }
    
    public RemoteViews makePublicContentView()
    {
      if (this.mN.publicVersion != null) {
        return recoverBuilder(this.mContext, this.mN.publicVersion).createContentView();
      }
      Bundle localBundle = this.mN.extras;
      Notification.Style localStyle = this.mStyle;
      this.mStyle = null;
      Icon localIcon = Notification.-get1(this.mN);
      Notification.-set6(this.mN, null);
      Bitmap localBitmap = this.mN.largeIcon;
      this.mN.largeIcon = null;
      Object localObject = new Bundle();
      ((Bundle)localObject).putBoolean("android.showWhen", localBundle.getBoolean("android.showWhen"));
      ((Bundle)localObject).putBoolean("android.showChronometer", localBundle.getBoolean("android.showChronometer"));
      ((Bundle)localObject).putBoolean("android.chronometerCountDown", localBundle.getBoolean("android.chronometerCountDown"));
      ((Bundle)localObject).putCharSequence("android.title", this.mContext.getString(17039699));
      this.mN.extras = ((Bundle)localObject);
      localObject = applyStandardTemplate(getBaseLayoutResource());
      this.mN.extras = localBundle;
      Notification.-set6(this.mN, localIcon);
      this.mN.largeIcon = localBitmap;
      this.mStyle = localStyle;
      return (RemoteViews)localObject;
    }
    
    int resolveContrastColor()
    {
      if ((this.mCachedContrastColorIsFor == this.mN.color) && (this.mCachedContrastColor != 1)) {
        return this.mCachedContrastColor;
      }
      int i = NotificationColorUtil.resolveContrastColor(this.mContext, this.mN.color);
      this.mCachedContrastColorIsFor = this.mN.color;
      this.mCachedContrastColor = i;
      return i;
    }
    
    public Builder setActions(Notification.Action... paramVarArgs)
    {
      this.mActions.clear();
      int i = 0;
      while (i < paramVarArgs.length)
      {
        if (paramVarArgs[i] != null) {
          this.mActions.add(paramVarArgs[i]);
        }
        i += 1;
      }
      return this;
    }
    
    public Builder setAutoCancel(boolean paramBoolean)
    {
      setFlag(16, paramBoolean);
      return this;
    }
    
    public Builder setBackgroundColorOnStatusBar(int paramInt)
    {
      this.mBackgroundColorOnStatusBar = paramInt;
      return this;
    }
    
    public Builder setCategory(String paramString)
    {
      this.mN.category = paramString;
      return this;
    }
    
    public Builder setChronometerBase(long paramLong)
    {
      this.mChronometerBase = paramLong;
      return this;
    }
    
    public Builder setChronometerCountDown(boolean paramBoolean)
    {
      this.mN.extras.putBoolean("android.chronometerCountDown", paramBoolean);
      return this;
    }
    
    public Builder setChronometerState(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        this.mChronometerState = 0;
        return this;
      case 0: 
        this.mChronometerState = 0;
        return this;
      }
      this.mChronometerState = 1;
      return this;
    }
    
    public Builder setColor(int paramInt)
    {
      this.mN.color = paramInt;
      sanitizeColor();
      return this;
    }
    
    @Deprecated
    public Builder setContent(RemoteViews paramRemoteViews)
    {
      return setCustomContentView(paramRemoteViews);
    }
    
    public Builder setContentInfo(CharSequence paramCharSequence)
    {
      this.mN.extras.putCharSequence("android.infoText", Notification.safeCharSequence(paramCharSequence));
      return this;
    }
    
    public Builder setContentIntent(PendingIntent paramPendingIntent)
    {
      this.mN.contentIntent = paramPendingIntent;
      return this;
    }
    
    void setContentMinHeight(RemoteViews paramRemoteViews, boolean paramBoolean)
    {
      int i = 0;
      if (paramBoolean) {
        i = this.mContext.getResources().getDimensionPixelSize(17104971);
      }
      paramRemoteViews.setInt(16909241, "setMinimumHeight", i);
    }
    
    public Builder setContentText(CharSequence paramCharSequence)
    {
      this.mN.extras.putCharSequence("android.text", Notification.safeCharSequence(paramCharSequence));
      return this;
    }
    
    public Builder setContentTitle(CharSequence paramCharSequence)
    {
      this.mN.extras.putCharSequence("android.title", Notification.safeCharSequence(paramCharSequence));
      return this;
    }
    
    public Builder setCustomBigContentView(RemoteViews paramRemoteViews)
    {
      this.mN.bigContentView = paramRemoteViews;
      return this;
    }
    
    public Builder setCustomContentView(RemoteViews paramRemoteViews)
    {
      this.mN.contentView = paramRemoteViews;
      return this;
    }
    
    public Builder setCustomHeadsUpContentView(RemoteViews paramRemoteViews)
    {
      this.mN.headsUpContentView = paramRemoteViews;
      return this;
    }
    
    public Builder setDefaults(int paramInt)
    {
      this.mN.defaults = paramInt;
      return this;
    }
    
    public Builder setDeleteIntent(PendingIntent paramPendingIntent)
    {
      this.mN.deleteIntent = paramPendingIntent;
      return this;
    }
    
    public Builder setExtras(Bundle paramBundle)
    {
      if (paramBundle != null) {
        this.mUserExtras = paramBundle;
      }
      return this;
    }
    
    public Builder setFlag(int paramInt, boolean paramBoolean)
    {
      if (paramBoolean)
      {
        localNotification = this.mN;
        localNotification.flags |= paramInt;
        return this;
      }
      Notification localNotification = this.mN;
      localNotification.flags &= paramInt;
      return this;
    }
    
    public Builder setFullScreenIntent(PendingIntent paramPendingIntent, boolean paramBoolean)
    {
      this.mN.fullScreenIntent = paramPendingIntent;
      setFlag(128, paramBoolean);
      return this;
    }
    
    public Builder setGroup(String paramString)
    {
      Notification.-set5(this.mN, paramString);
      return this;
    }
    
    public Builder setGroupSummary(boolean paramBoolean)
    {
      setFlag(512, paramBoolean);
      return this;
    }
    
    public Builder setIconOnStatusBar(int paramInt)
    {
      this.mStatusBarIcon = paramInt;
      return this;
    }
    
    public Builder setIntentOnStatusBar(Intent paramIntent)
    {
      this.mActionIntentOnStatusBar = paramIntent;
      return this;
    }
    
    public Builder setLargeIcon(Bitmap paramBitmap)
    {
      Icon localIcon = null;
      if (paramBitmap != null) {
        localIcon = Icon.createWithBitmap(paramBitmap);
      }
      return setLargeIcon(localIcon);
    }
    
    public Builder setLargeIcon(Icon paramIcon)
    {
      Notification.-set6(this.mN, paramIcon);
      this.mN.extras.putParcelable("android.largeIcon", paramIcon);
      return this;
    }
    
    public Builder setLights(int paramInt1, int paramInt2, int paramInt3)
    {
      this.mN.ledARGB = paramInt1;
      this.mN.ledOnMS = paramInt2;
      this.mN.ledOffMS = paramInt3;
      if ((paramInt2 != 0) || (paramInt3 != 0))
      {
        Notification localNotification = this.mN;
        localNotification.flags |= 0x1;
      }
      return this;
    }
    
    public Builder setLocalOnly(boolean paramBoolean)
    {
      setFlag(256, paramBoolean);
      return this;
    }
    
    public Builder setNumber(int paramInt)
    {
      this.mN.number = paramInt;
      return this;
    }
    
    public Builder setOngoing(boolean paramBoolean)
    {
      setFlag(2, paramBoolean);
      return this;
    }
    
    public Builder setOnlyAlertOnce(boolean paramBoolean)
    {
      setFlag(8, paramBoolean);
      return this;
    }
    
    public Builder setPriority(int paramInt)
    {
      this.mN.priority = paramInt;
      return this;
    }
    
    public Builder setPriorityOnStatusBar(int paramInt)
    {
      this.mPriorityOnStatusBar = paramInt;
      return this;
    }
    
    public Builder setProgress(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      this.mN.extras.putInt("android.progress", paramInt2);
      this.mN.extras.putInt("android.progressMax", paramInt1);
      this.mN.extras.putBoolean("android.progressIndeterminate", paramBoolean);
      return this;
    }
    
    public Builder setPublicVersion(Notification paramNotification)
    {
      if (paramNotification != null)
      {
        this.mN.publicVersion = new Notification();
        paramNotification.cloneInto(this.mN.publicVersion, true);
        return this;
      }
      this.mN.publicVersion = null;
      return this;
    }
    
    public Builder setRemoteInputHistory(CharSequence[] paramArrayOfCharSequence)
    {
      if (paramArrayOfCharSequence == null)
      {
        this.mN.extras.putCharSequenceArray("android.remoteInputHistory", null);
        return this;
      }
      int j = Math.min(5, paramArrayOfCharSequence.length);
      CharSequence[] arrayOfCharSequence = new CharSequence[j];
      int i = 0;
      while (i < j)
      {
        arrayOfCharSequence[i] = Notification.safeCharSequence(paramArrayOfCharSequence[i]);
        i += 1;
      }
      this.mN.extras.putCharSequenceArray("android.remoteInputHistory", arrayOfCharSequence);
      return this;
    }
    
    public Builder setShowOnStatusBar(boolean paramBoolean)
    {
      this.mShowOnStatusBar = paramBoolean;
      return this;
    }
    
    public Builder setShowWhen(boolean paramBoolean)
    {
      this.mN.extras.putBoolean("android.showWhen", paramBoolean);
      return this;
    }
    
    public Builder setSmallIcon(int paramInt)
    {
      if (paramInt != 0) {}
      for (Icon localIcon = Icon.createWithResource(this.mContext, paramInt);; localIcon = null) {
        return setSmallIcon(localIcon);
      }
    }
    
    public Builder setSmallIcon(int paramInt1, int paramInt2)
    {
      this.mN.iconLevel = paramInt2;
      return setSmallIcon(paramInt1);
    }
    
    public Builder setSmallIcon(Icon paramIcon)
    {
      this.mN.setSmallIcon(paramIcon);
      if ((paramIcon != null) && (paramIcon.getType() == 2)) {
        this.mN.icon = paramIcon.getResId();
      }
      return this;
    }
    
    public Builder setSortKey(String paramString)
    {
      Notification.-set11(this.mN, paramString);
      return this;
    }
    
    public Builder setSound(Uri paramUri)
    {
      this.mN.sound = paramUri;
      this.mN.audioAttributes = Notification.AUDIO_ATTRIBUTES_DEFAULT;
      return this;
    }
    
    @Deprecated
    public Builder setSound(Uri paramUri, int paramInt)
    {
      this.mN.sound = paramUri;
      this.mN.audioStreamType = paramInt;
      return this;
    }
    
    public Builder setSound(Uri paramUri, AudioAttributes paramAudioAttributes)
    {
      this.mN.sound = paramUri;
      this.mN.audioAttributes = paramAudioAttributes;
      return this;
    }
    
    public Builder setStyle(Notification.Style paramStyle)
    {
      if (this.mStyle != paramStyle)
      {
        this.mStyle = paramStyle;
        if (this.mStyle != null)
        {
          this.mStyle.setBuilder(this);
          this.mN.extras.putString("android.template", paramStyle.getClass().getName());
        }
      }
      else
      {
        return this;
      }
      this.mN.extras.remove("android.template");
      return this;
    }
    
    public Builder setSubText(CharSequence paramCharSequence)
    {
      this.mN.extras.putCharSequence("android.subText", Notification.safeCharSequence(paramCharSequence));
      return this;
    }
    
    public Builder setTextOnStatusBar(int paramInt)
    {
      this.mTextOnStatusBar = paramInt;
      return this;
    }
    
    public Builder setTicker(CharSequence paramCharSequence)
    {
      this.mN.tickerText = Notification.safeCharSequence(paramCharSequence);
      return this;
    }
    
    @Deprecated
    public Builder setTicker(CharSequence paramCharSequence, RemoteViews paramRemoteViews)
    {
      setTicker(paramCharSequence);
      return this;
    }
    
    public Builder setUsesChronometer(boolean paramBoolean)
    {
      this.mN.extras.putBoolean("android.showChronometer", paramBoolean);
      return this;
    }
    
    public Builder setUsesChronometerOnStatusBar(boolean paramBoolean)
    {
      this.mShowChronometerOnStatusBar = paramBoolean;
      return this;
    }
    
    public Builder setVibrate(long[] paramArrayOfLong)
    {
      this.mN.vibrate = paramArrayOfLong;
      return this;
    }
    
    public Builder setVisibility(int paramInt)
    {
      this.mN.visibility = paramInt;
      return this;
    }
    
    public Builder setWhen(long paramLong)
    {
      this.mN.when = paramLong;
      return this;
    }
  }
  
  private static class BuilderRemoteViews
    extends RemoteViews
  {
    public BuilderRemoteViews(ApplicationInfo paramApplicationInfo, int paramInt)
    {
      super(paramInt);
    }
    
    public BuilderRemoteViews(Parcel paramParcel)
    {
      super();
    }
    
    public BuilderRemoteViews clone()
    {
      Parcel localParcel = Parcel.obtain();
      writeToParcel(localParcel, 0);
      localParcel.setDataPosition(0);
      BuilderRemoteViews localBuilderRemoteViews = new BuilderRemoteViews(localParcel);
      localParcel.recycle();
      return localBuilderRemoteViews;
    }
  }
  
  public static final class CarExtender
    implements Notification.Extender
  {
    private static final String EXTRA_CAR_EXTENDER = "android.car.EXTENSIONS";
    private static final String EXTRA_COLOR = "app_color";
    private static final String EXTRA_CONVERSATION = "car_conversation";
    private static final String EXTRA_LARGE_ICON = "large_icon";
    private static final String TAG = "CarExtender";
    private int mColor = 0;
    private Bitmap mLargeIcon;
    private UnreadConversation mUnreadConversation;
    
    public CarExtender() {}
    
    public CarExtender(Notification paramNotification)
    {
      if (paramNotification.extras == null) {}
      for (paramNotification = (Notification)localObject;; paramNotification = paramNotification.extras.getBundle("android.car.EXTENSIONS"))
      {
        if (paramNotification != null)
        {
          this.mLargeIcon = ((Bitmap)paramNotification.getParcelable("large_icon"));
          this.mColor = paramNotification.getInt("app_color", 0);
          this.mUnreadConversation = UnreadConversation.getUnreadConversationFromBundle(paramNotification.getBundle("car_conversation"));
        }
        return;
      }
    }
    
    public Notification.Builder extend(Notification.Builder paramBuilder)
    {
      Bundle localBundle = new Bundle();
      if (this.mLargeIcon != null) {
        localBundle.putParcelable("large_icon", this.mLargeIcon);
      }
      if (this.mColor != 0) {
        localBundle.putInt("app_color", this.mColor);
      }
      if (this.mUnreadConversation != null) {
        localBundle.putBundle("car_conversation", this.mUnreadConversation.getBundleForUnreadConversation());
      }
      paramBuilder.getExtras().putBundle("android.car.EXTENSIONS", localBundle);
      return paramBuilder;
    }
    
    public int getColor()
    {
      return this.mColor;
    }
    
    public Bitmap getLargeIcon()
    {
      return this.mLargeIcon;
    }
    
    public UnreadConversation getUnreadConversation()
    {
      return this.mUnreadConversation;
    }
    
    public CarExtender setColor(int paramInt)
    {
      this.mColor = paramInt;
      return this;
    }
    
    public CarExtender setLargeIcon(Bitmap paramBitmap)
    {
      this.mLargeIcon = paramBitmap;
      return this;
    }
    
    public CarExtender setUnreadConversation(UnreadConversation paramUnreadConversation)
    {
      this.mUnreadConversation = paramUnreadConversation;
      return this;
    }
    
    public static class Builder
    {
      private long mLatestTimestamp;
      private final List<String> mMessages = new ArrayList();
      private final String mParticipant;
      private PendingIntent mReadPendingIntent;
      private RemoteInput mRemoteInput;
      private PendingIntent mReplyPendingIntent;
      
      public Builder(String paramString)
      {
        this.mParticipant = paramString;
      }
      
      public Builder addMessage(String paramString)
      {
        this.mMessages.add(paramString);
        return this;
      }
      
      public Notification.CarExtender.UnreadConversation build()
      {
        String[] arrayOfString = (String[])this.mMessages.toArray(new String[this.mMessages.size()]);
        String str = this.mParticipant;
        RemoteInput localRemoteInput = this.mRemoteInput;
        PendingIntent localPendingIntent1 = this.mReplyPendingIntent;
        PendingIntent localPendingIntent2 = this.mReadPendingIntent;
        long l = this.mLatestTimestamp;
        return new Notification.CarExtender.UnreadConversation(arrayOfString, localRemoteInput, localPendingIntent1, localPendingIntent2, new String[] { str }, l);
      }
      
      public Builder setLatestTimestamp(long paramLong)
      {
        this.mLatestTimestamp = paramLong;
        return this;
      }
      
      public Builder setReadPendingIntent(PendingIntent paramPendingIntent)
      {
        this.mReadPendingIntent = paramPendingIntent;
        return this;
      }
      
      public Builder setReplyAction(PendingIntent paramPendingIntent, RemoteInput paramRemoteInput)
      {
        this.mRemoteInput = paramRemoteInput;
        this.mReplyPendingIntent = paramPendingIntent;
        return this;
      }
    }
    
    public static class UnreadConversation
    {
      private static final String KEY_AUTHOR = "author";
      private static final String KEY_MESSAGES = "messages";
      private static final String KEY_ON_READ = "on_read";
      private static final String KEY_ON_REPLY = "on_reply";
      private static final String KEY_PARTICIPANTS = "participants";
      private static final String KEY_REMOTE_INPUT = "remote_input";
      private static final String KEY_TEXT = "text";
      private static final String KEY_TIMESTAMP = "timestamp";
      private final long mLatestTimestamp;
      private final String[] mMessages;
      private final String[] mParticipants;
      private final PendingIntent mReadPendingIntent;
      private final RemoteInput mRemoteInput;
      private final PendingIntent mReplyPendingIntent;
      
      UnreadConversation(String[] paramArrayOfString1, RemoteInput paramRemoteInput, PendingIntent paramPendingIntent1, PendingIntent paramPendingIntent2, String[] paramArrayOfString2, long paramLong)
      {
        this.mMessages = paramArrayOfString1;
        this.mRemoteInput = paramRemoteInput;
        this.mReadPendingIntent = paramPendingIntent2;
        this.mReplyPendingIntent = paramPendingIntent1;
        this.mParticipants = paramArrayOfString2;
        this.mLatestTimestamp = paramLong;
      }
      
      static UnreadConversation getUnreadConversationFromBundle(Bundle paramBundle)
      {
        if (paramBundle == null) {
          return null;
        }
        Object localObject = paramBundle.getParcelableArray("messages");
        String[] arrayOfString1 = null;
        int k;
        int j;
        if (localObject != null)
        {
          arrayOfString1 = new String[localObject.length];
          k = 1;
          j = 0;
        }
        PendingIntent localPendingIntent;
        RemoteInput localRemoteInput;
        String[] arrayOfString2;
        for (;;)
        {
          int i = k;
          if (j < arrayOfString1.length) {
            if ((localObject[j] instanceof Bundle)) {
              break label114;
            }
          }
          for (i = 0;; i = 0)
          {
            if (i == 0) {
              break label149;
            }
            localObject = (PendingIntent)paramBundle.getParcelable("on_read");
            localPendingIntent = (PendingIntent)paramBundle.getParcelable("on_reply");
            localRemoteInput = (RemoteInput)paramBundle.getParcelable("remote_input");
            arrayOfString2 = paramBundle.getStringArray("participants");
            if ((arrayOfString2 != null) && (arrayOfString2.length == 1)) {
              break label151;
            }
            return null;
            label114:
            arrayOfString1[j] = ((Bundle)localObject[j]).getString("text");
            if (arrayOfString1[j] != null) {
              break;
            }
          }
          j += 1;
        }
        label149:
        return null;
        label151:
        return new UnreadConversation(arrayOfString1, localRemoteInput, localPendingIntent, (PendingIntent)localObject, arrayOfString2, paramBundle.getLong("timestamp"));
      }
      
      Bundle getBundleForUnreadConversation()
      {
        Bundle localBundle1 = new Bundle();
        Parcelable[] arrayOfParcelable = null;
        Object localObject = arrayOfParcelable;
        if (this.mParticipants != null)
        {
          localObject = arrayOfParcelable;
          if (this.mParticipants.length > 1) {
            localObject = this.mParticipants[0];
          }
        }
        arrayOfParcelable = new Parcelable[this.mMessages.length];
        int i = 0;
        while (i < arrayOfParcelable.length)
        {
          Bundle localBundle2 = new Bundle();
          localBundle2.putString("text", this.mMessages[i]);
          localBundle2.putString("author", (String)localObject);
          arrayOfParcelable[i] = localBundle2;
          i += 1;
        }
        localBundle1.putParcelableArray("messages", arrayOfParcelable);
        if (this.mRemoteInput != null) {
          localBundle1.putParcelable("remote_input", this.mRemoteInput);
        }
        localBundle1.putParcelable("on_reply", this.mReplyPendingIntent);
        localBundle1.putParcelable("on_read", this.mReadPendingIntent);
        localBundle1.putStringArray("participants", this.mParticipants);
        localBundle1.putLong("timestamp", this.mLatestTimestamp);
        return localBundle1;
      }
      
      public long getLatestTimestamp()
      {
        return this.mLatestTimestamp;
      }
      
      public String[] getMessages()
      {
        return this.mMessages;
      }
      
      public String getParticipant()
      {
        if (this.mParticipants.length > 0) {
          return this.mParticipants[0];
        }
        return null;
      }
      
      public String[] getParticipants()
      {
        return this.mParticipants;
      }
      
      public PendingIntent getReadPendingIntent()
      {
        return this.mReadPendingIntent;
      }
      
      public RemoteInput getRemoteInput()
      {
        return this.mRemoteInput;
      }
      
      public PendingIntent getReplyPendingIntent()
      {
        return this.mReplyPendingIntent;
      }
    }
  }
  
  public static class DecoratedCustomViewStyle
    extends Notification.Style
  {
    private void buildIntoRemoteViewContent(RemoteViews paramRemoteViews1, RemoteViews paramRemoteViews2)
    {
      if (paramRemoteViews2 != null)
      {
        paramRemoteViews2 = paramRemoteViews2.clone();
        paramRemoteViews1.removeAllViews(16909241);
        paramRemoteViews1.addView(16909241, paramRemoteViews2);
      }
      int i = 17104962;
      if (Notification.-wrap1(Notification.Builder.-get2(this.mBuilder))) {
        i = 17104964;
      }
      paramRemoteViews1.setViewLayoutMarginEndDimen(16909241, i);
    }
    
    private RemoteViews makeDecoratedBigContentView()
    {
      if (Notification.Builder.-get2(this.mBuilder).bigContentView == null) {}
      for (RemoteViews localRemoteViews1 = Notification.Builder.-get2(this.mBuilder).contentView; Notification.Builder.-get0(this.mBuilder).size() == 0; localRemoteViews1 = Notification.Builder.-get2(this.mBuilder).bigContentView) {
        return makeStandardTemplateWithCustomContent(localRemoteViews1);
      }
      RemoteViews localRemoteViews2 = Notification.Builder.-wrap2(this.mBuilder, Notification.Builder.-wrap7(this.mBuilder));
      buildIntoRemoteViewContent(localRemoteViews2, localRemoteViews1);
      return localRemoteViews2;
    }
    
    private RemoteViews makeDecoratedHeadsUpContentView()
    {
      if (Notification.Builder.-get2(this.mBuilder).headsUpContentView == null) {}
      for (RemoteViews localRemoteViews1 = Notification.Builder.-get2(this.mBuilder).contentView; Notification.Builder.-get0(this.mBuilder).size() == 0; localRemoteViews1 = Notification.Builder.-get2(this.mBuilder).headsUpContentView) {
        return makeStandardTemplateWithCustomContent(localRemoteViews1);
      }
      RemoteViews localRemoteViews2 = Notification.Builder.-wrap2(this.mBuilder, Notification.Builder.-wrap7(this.mBuilder));
      buildIntoRemoteViewContent(localRemoteViews2, localRemoteViews1);
      return localRemoteViews2;
    }
    
    private RemoteViews makeStandardTemplateWithCustomContent(RemoteViews paramRemoteViews)
    {
      RemoteViews localRemoteViews = Notification.Builder.-wrap5(this.mBuilder, Notification.Builder.-wrap6(this.mBuilder));
      buildIntoRemoteViewContent(localRemoteViews, paramRemoteViews);
      return localRemoteViews;
    }
    
    public boolean displayCustomViewInline()
    {
      return true;
    }
    
    public RemoteViews makeBigContentView()
    {
      return makeDecoratedBigContentView();
    }
    
    public RemoteViews makeContentView()
    {
      return makeStandardTemplateWithCustomContent(Notification.Builder.-get2(this.mBuilder).contentView);
    }
    
    public RemoteViews makeHeadsUpContentView()
    {
      return makeDecoratedHeadsUpContentView();
    }
  }
  
  public static class DecoratedMediaCustomViewStyle
    extends Notification.MediaStyle
  {
    private RemoteViews buildIntoRemoteView(RemoteViews paramRemoteViews1, int paramInt, RemoteViews paramRemoteViews2)
    {
      if (paramRemoteViews2 != null)
      {
        paramRemoteViews2 = paramRemoteViews2.clone();
        paramRemoteViews1.removeAllViews(paramInt);
        paramRemoteViews1.addView(paramInt, paramRemoteViews2);
      }
      return paramRemoteViews1;
    }
    
    private RemoteViews makeBigContentViewWithCustomContent(RemoteViews paramRemoteViews)
    {
      RemoteViews localRemoteViews = super.makeBigContentView();
      if (localRemoteViews != null) {
        return buildIntoRemoteView(localRemoteViews, 16909241, paramRemoteViews);
      }
      if (paramRemoteViews != Notification.Builder.-get2(this.mBuilder).contentView) {
        return buildIntoRemoteView(super.makeContentView(), 16909253, paramRemoteViews);
      }
      return null;
    }
    
    public boolean displayCustomViewInline()
    {
      return true;
    }
    
    public RemoteViews makeBigContentView()
    {
      if (Notification.Builder.-get2(this.mBuilder).bigContentView != null) {}
      for (RemoteViews localRemoteViews = Notification.Builder.-get2(this.mBuilder).bigContentView;; localRemoteViews = Notification.Builder.-get2(this.mBuilder).contentView) {
        return makeBigContentViewWithCustomContent(localRemoteViews);
      }
    }
    
    public RemoteViews makeContentView()
    {
      return buildIntoRemoteView(super.makeContentView(), 16909253, Notification.Builder.-get2(this.mBuilder).contentView);
    }
    
    public RemoteViews makeHeadsUpContentView()
    {
      if (Notification.Builder.-get2(this.mBuilder).headsUpContentView != null) {}
      for (RemoteViews localRemoteViews = Notification.Builder.-get2(this.mBuilder).headsUpContentView;; localRemoteViews = Notification.Builder.-get2(this.mBuilder).contentView) {
        return makeBigContentViewWithCustomContent(localRemoteViews);
      }
    }
  }
  
  public static abstract interface Extender
  {
    public abstract Notification.Builder extend(Notification.Builder paramBuilder);
  }
  
  public static class InboxStyle
    extends Notification.Style
  {
    private ArrayList<CharSequence> mTexts = new ArrayList(5);
    
    public InboxStyle() {}
    
    @Deprecated
    public InboxStyle(Notification.Builder paramBuilder)
    {
      setBuilder(paramBuilder);
    }
    
    private void handleInboxImageMargin(RemoteViews paramRemoteViews, int paramInt, boolean paramBoolean)
    {
      int j = 0;
      int i = j;
      if (paramBoolean)
      {
        i = Notification.Builder.-get2(this.mBuilder).extras.getInt("android.progressMax", 0);
        paramBoolean = Notification.Builder.-get2(this.mBuilder).extras.getBoolean("android.progressIndeterminate");
        if (i != 0) {
          break label83;
        }
        i = j;
        if (Notification.-wrap1(Notification.Builder.-get2(this.mBuilder))) {
          if (!paramBoolean) {
            break label88;
          }
        }
      }
      label83:
      label88:
      for (i = j;; i = 17104963)
      {
        paramRemoteViews.setViewLayoutMarginEndDimen(paramInt, i);
        return;
        paramBoolean = true;
        break;
      }
    }
    
    public void addExtras(Bundle paramBundle)
    {
      super.addExtras(paramBundle);
      CharSequence[] arrayOfCharSequence = new CharSequence[this.mTexts.size()];
      paramBundle.putCharSequenceArray("android.textLines", (CharSequence[])this.mTexts.toArray(arrayOfCharSequence));
    }
    
    public InboxStyle addLine(CharSequence paramCharSequence)
    {
      this.mTexts.add(Notification.safeCharSequence(paramCharSequence));
      return this;
    }
    
    public RemoteViews makeBigContentView()
    {
      Object localObject = Notification.Builder.-get2(this.mBuilder).extras.getCharSequence("android.text");
      Notification.Builder.-wrap0(this.mBuilder).putCharSequence("android.text", null);
      RemoteViews localRemoteViews = getStandardView(Notification.Builder.-wrap10(this.mBuilder));
      Notification.Builder.-wrap0(this.mBuilder).putCharSequence("android.text", (CharSequence)localObject);
      localObject = new int[7];
      Object tmp65_63 = localObject;
      tmp65_63[0] = 16909246;
      Object tmp70_65 = tmp65_63;
      tmp70_65[1] = 16909247;
      Object tmp75_70 = tmp70_65;
      tmp75_70[2] = 16909248;
      Object tmp80_75 = tmp75_70;
      tmp80_75[3] = 16909249;
      Object tmp85_80 = tmp80_75;
      tmp85_80[4] = 16909250;
      Object tmp90_85 = tmp85_80;
      tmp90_85[5] = 16909251;
      Object tmp95_90 = tmp90_85;
      tmp95_90[6] = 16909252;
      tmp95_90;
      int j = localObject.length;
      int i = 0;
      while (i < j)
      {
        localRemoteViews.setViewVisibility(localObject[i], 8);
        i += 1;
      }
      int m = 0;
      int i2 = Notification.Builder.-get1(this.mBuilder).getResources().getDimensionPixelSize(17105018);
      boolean bool2 = true;
      int n = 0;
      int i1 = localObject.length;
      i = n;
      boolean bool1 = bool2;
      int k = m;
      j = i1;
      if (Notification.Builder.-get0(this.mBuilder).size() > 0)
      {
        j = i1 - 1;
        k = m;
        bool1 = bool2;
        i = n;
      }
      if ((k < this.mTexts.size()) && (k < j))
      {
        CharSequence localCharSequence = (CharSequence)this.mTexts.get(k);
        bool2 = bool1;
        if (!TextUtils.isEmpty(localCharSequence))
        {
          localRemoteViews.setViewVisibility(localObject[k], 0);
          localRemoteViews.setTextViewText(localObject[k], Notification.Builder.-wrap12(this.mBuilder, localCharSequence));
          localRemoteViews.setViewPadding(localObject[k], 0, i2, 0, 0);
          handleInboxImageMargin(localRemoteViews, localObject[k], bool1);
          if (!bool1) {
            break label322;
          }
        }
        label322:
        for (i = localObject[k];; i = 0)
        {
          bool2 = false;
          k += 1;
          bool1 = bool2;
          break;
        }
      }
      if (i != 0) {
        localRemoteViews.setViewPadding(i, 0, Notification.Builder.-get1(this.mBuilder).getResources().getDimensionPixelSize(17105017), 0, 0);
      }
      return localRemoteViews;
    }
    
    protected void restoreFromExtras(Bundle paramBundle)
    {
      super.restoreFromExtras(paramBundle);
      this.mTexts.clear();
      if (paramBundle.containsKey("android.textLines")) {
        Collections.addAll(this.mTexts, paramBundle.getCharSequenceArray("android.textLines"));
      }
    }
    
    public InboxStyle setBigContentTitle(CharSequence paramCharSequence)
    {
      internalSetBigContentTitle(Notification.safeCharSequence(paramCharSequence));
      return this;
    }
    
    public InboxStyle setSummaryText(CharSequence paramCharSequence)
    {
      internalSetSummaryText(Notification.safeCharSequence(paramCharSequence));
      return this;
    }
  }
  
  public static class MediaStyle
    extends Notification.Style
  {
    static final int MAX_MEDIA_BUTTONS = 5;
    static final int MAX_MEDIA_BUTTONS_IN_COMPACT = 3;
    private int[] mActionsToShowInCompact = null;
    private MediaSession.Token mToken;
    
    public MediaStyle() {}
    
    @Deprecated
    public MediaStyle(Notification.Builder paramBuilder)
    {
      setBuilder(paramBuilder);
    }
    
    private RemoteViews generateMediaActionButton(Notification.Action paramAction, int paramInt)
    {
      if (paramAction.actionIntent == null) {}
      for (int i = 1;; i = 0)
      {
        Notification.BuilderRemoteViews localBuilderRemoteViews = new Notification.BuilderRemoteViews(Notification.Builder.-get1(this.mBuilder).getApplicationInfo(), 17367178);
        localBuilderRemoteViews.setImageViewIcon(16909222, paramAction.getIcon());
        localBuilderRemoteViews.setDrawableParameters(16909222, false, -1, paramInt, PorterDuff.Mode.SRC_ATOP, -1);
        if (i == 0) {
          localBuilderRemoteViews.setOnClickPendingIntent(16909222, paramAction.actionIntent);
        }
        localBuilderRemoteViews.setContentDescription(16909222, paramAction.title);
        return localBuilderRemoteViews;
      }
    }
    
    private void handleImage(RemoteViews paramRemoteViews)
    {
      if (Notification.-wrap1(Notification.Builder.-get2(this.mBuilder)))
      {
        paramRemoteViews.setViewLayoutMarginEndDimen(16909255, 0);
        paramRemoteViews.setViewLayoutMarginEndDimen(16908414, 0);
      }
    }
    
    private RemoteViews makeMediaBigContentView()
    {
      int j = Math.min(Notification.Builder.-get0(this.mBuilder).size(), 5);
      if (this.mActionsToShowInCompact == null) {}
      for (int i = 0; (!Notification.-wrap1(Notification.Builder.-get2(this.mBuilder))) && (j <= i); i = Math.min(this.mActionsToShowInCompact.length, 3)) {
        return null;
      }
      RemoteViews localRemoteViews = Notification.Builder.-wrap3(this.mBuilder, 17367183, false);
      if (j > 0)
      {
        localRemoteViews.removeAllViews(16909243);
        i = 0;
        while (i < j)
        {
          localRemoteViews.addView(16909243, generateMediaActionButton((Notification.Action)Notification.Builder.-get0(this.mBuilder).get(i), this.mBuilder.resolveContrastColor()));
          i += 1;
        }
      }
      handleImage(localRemoteViews);
      return localRemoteViews;
    }
    
    private RemoteViews makeMediaContentView()
    {
      RemoteViews localRemoteViews = Notification.Builder.-wrap3(this.mBuilder, 17367187, false);
      int k = Notification.Builder.-get0(this.mBuilder).size();
      int j;
      if (this.mActionsToShowInCompact == null)
      {
        i = 0;
        if (i > 0)
        {
          localRemoteViews.removeAllViews(16909243);
          j = 0;
        }
      }
      else
      {
        for (;;)
        {
          if (j >= i) {
            break label145;
          }
          if (j >= k)
          {
            throw new IllegalArgumentException(String.format("setShowActionsInCompactView: action %d out of bounds (max %d)", new Object[] { Integer.valueOf(j), Integer.valueOf(k - 1) }));
            i = Math.min(this.mActionsToShowInCompact.length, 3);
            break;
          }
          localRemoteViews.addView(16909243, generateMediaActionButton((Notification.Action)Notification.Builder.-get0(this.mBuilder).get(this.mActionsToShowInCompact[j]), this.mBuilder.resolveContrastColor()));
          j += 1;
        }
      }
      label145:
      handleImage(localRemoteViews);
      int i = 17104962;
      if (Notification.-wrap1(Notification.Builder.-get2(this.mBuilder))) {
        i = 17104964;
      }
      localRemoteViews.setViewLayoutMarginEndDimen(16909241, i);
      return localRemoteViews;
    }
    
    public void addExtras(Bundle paramBundle)
    {
      super.addExtras(paramBundle);
      if (this.mToken != null) {
        paramBundle.putParcelable("android.mediaSession", this.mToken);
      }
      if (this.mActionsToShowInCompact != null) {
        paramBundle.putIntArray("android.compactActions", this.mActionsToShowInCompact);
      }
    }
    
    public Notification buildStyled(Notification paramNotification)
    {
      super.buildStyled(paramNotification);
      if (paramNotification.category == null) {
        paramNotification.category = "transport";
      }
      return paramNotification;
    }
    
    protected boolean hasProgress()
    {
      return false;
    }
    
    public RemoteViews makeBigContentView()
    {
      return makeMediaBigContentView();
    }
    
    public RemoteViews makeContentView()
    {
      return makeMediaContentView();
    }
    
    public RemoteViews makeHeadsUpContentView()
    {
      RemoteViews localRemoteViews = makeMediaBigContentView();
      if (localRemoteViews != null) {
        return localRemoteViews;
      }
      return makeMediaContentView();
    }
    
    protected void restoreFromExtras(Bundle paramBundle)
    {
      super.restoreFromExtras(paramBundle);
      if (paramBundle.containsKey("android.mediaSession")) {
        this.mToken = ((MediaSession.Token)paramBundle.getParcelable("android.mediaSession"));
      }
      if (paramBundle.containsKey("android.compactActions")) {
        this.mActionsToShowInCompact = paramBundle.getIntArray("android.compactActions");
      }
    }
    
    public MediaStyle setMediaSession(MediaSession.Token paramToken)
    {
      this.mToken = paramToken;
      return this;
    }
    
    public MediaStyle setShowActionsInCompactView(int... paramVarArgs)
    {
      this.mActionsToShowInCompact = paramVarArgs;
      return this;
    }
  }
  
  public static class MessagingStyle
    extends Notification.Style
  {
    public static final int MAXIMUM_RETAINED_MESSAGES = 25;
    CharSequence mConversationTitle;
    List<Message> mMessages = new ArrayList();
    CharSequence mUserDisplayName;
    
    MessagingStyle() {}
    
    public MessagingStyle(CharSequence paramCharSequence)
    {
      this.mUserDisplayName = paramCharSequence;
    }
    
    private Message findLatestIncomingMessage()
    {
      int i = this.mMessages.size() - 1;
      while (i >= 0)
      {
        Message localMessage = (Message)this.mMessages.get(i);
        if (!TextUtils.isEmpty(Message.-get0(localMessage))) {
          return localMessage;
        }
        i -= 1;
      }
      if (!this.mMessages.isEmpty()) {
        return (Message)this.mMessages.get(this.mMessages.size() - 1);
      }
      return null;
    }
    
    private void fixTitleAndTextExtras(Bundle paramBundle)
    {
      Message localMessage = findLatestIncomingMessage();
      CharSequence localCharSequence;
      Object localObject;
      if (localMessage == null)
      {
        localCharSequence = null;
        if (localMessage != null) {
          break label113;
        }
        localObject = null;
        label20:
        if (TextUtils.isEmpty(this.mConversationTitle)) {
          break label149;
        }
        if (TextUtils.isEmpty((CharSequence)localObject)) {
          break label141;
        }
        localObject = BidiFormatter.getInstance();
        localObject = Notification.Builder.-get1(this.mBuilder).getString(17040880, new Object[] { ((BidiFormatter)localObject).unicodeWrap(this.mConversationTitle), ((BidiFormatter)localObject).unicodeWrap(Message.-get0(localMessage)) });
      }
      label113:
      label141:
      label149:
      for (;;)
      {
        if (localObject != null) {
          paramBundle.putCharSequence("android.title", (CharSequence)localObject);
        }
        if (localCharSequence != null) {
          paramBundle.putCharSequence("android.text", localCharSequence);
        }
        return;
        localCharSequence = Message.-get1(localMessage);
        break;
        if (TextUtils.isEmpty(Message.-get0(localMessage)))
        {
          localObject = this.mUserDisplayName;
          break label20;
        }
        localObject = Message.-get0(localMessage);
        break label20;
        localObject = this.mConversationTitle;
      }
    }
    
    private static TextAppearanceSpan makeFontColorSpan(int paramInt)
    {
      return new TextAppearanceSpan(null, 0, 0, ColorStateList.valueOf(paramInt), null);
    }
    
    private CharSequence makeMessageLine(Message paramMessage)
    {
      BidiFormatter localBidiFormatter = BidiFormatter.getInstance();
      SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder();
      Object localObject;
      if (TextUtils.isEmpty(Message.-get0(paramMessage))) {
        if (this.mUserDisplayName == null)
        {
          localObject = "";
          localSpannableStringBuilder.append(localBidiFormatter.unicodeWrap((CharSequence)localObject), makeFontColorSpan(this.mBuilder.resolveContrastColor()), 0);
          label55:
          if (Message.-get1(paramMessage) != null) {
            break label115;
          }
        }
      }
      label115:
      for (paramMessage = "";; paramMessage = Message.-get1(paramMessage))
      {
        localSpannableStringBuilder.append("  ").append(localBidiFormatter.unicodeWrap(paramMessage));
        return localSpannableStringBuilder;
        localObject = this.mUserDisplayName;
        break;
        localSpannableStringBuilder.append(localBidiFormatter.unicodeWrap(Message.-get0(paramMessage)), makeFontColorSpan(-16777216), 0);
        break label55;
      }
    }
    
    public void addExtras(Bundle paramBundle)
    {
      super.addExtras(paramBundle);
      if (this.mUserDisplayName != null) {
        paramBundle.putCharSequence("android.selfDisplayName", this.mUserDisplayName);
      }
      if (this.mConversationTitle != null) {
        paramBundle.putCharSequence("android.conversationTitle", this.mConversationTitle);
      }
      if (!this.mMessages.isEmpty()) {
        paramBundle.putParcelableArray("android.messages", Message.getBundleArrayForMessages(this.mMessages));
      }
      fixTitleAndTextExtras(paramBundle);
    }
    
    public MessagingStyle addMessage(Message paramMessage)
    {
      this.mMessages.add(paramMessage);
      if (this.mMessages.size() > 25) {
        this.mMessages.remove(0);
      }
      return this;
    }
    
    public MessagingStyle addMessage(CharSequence paramCharSequence1, long paramLong, CharSequence paramCharSequence2)
    {
      this.mMessages.add(new Message(paramCharSequence1, paramLong, paramCharSequence2));
      if (this.mMessages.size() > 25) {
        this.mMessages.remove(0);
      }
      return this;
    }
    
    public CharSequence getConversationTitle()
    {
      return this.mConversationTitle;
    }
    
    public List<Message> getMessages()
    {
      return this.mMessages;
    }
    
    public CharSequence getUserDisplayName()
    {
      return this.mUserDisplayName;
    }
    
    public RemoteViews makeBigContentView()
    {
      int i;
      if (!TextUtils.isEmpty(Notification.Style.-get0(this)))
      {
        localObject1 = Notification.Style.-get0(this);
        if (!TextUtils.isEmpty((CharSequence)localObject1)) {
          break label109;
        }
        i = 0;
        label26:
        if (this.mMessages.size() != 1) {
          break label153;
        }
        if (i == 0) {
          break label114;
        }
        localObject2 = localObject1;
      }
      for (Object localObject1 = makeMessageLine((Message)this.mMessages.get(0));; localObject1 = Message.-get1((Message)this.mMessages.get(0)))
      {
        localObject2 = Notification.Builder.-wrap1(this.mBuilder, Notification.Builder.-wrap9(this.mBuilder), false, (CharSequence)localObject2, null);
        Notification.BigTextStyle.applyBigTextContentView(this.mBuilder, (RemoteViews)localObject2, (CharSequence)localObject1);
        return (RemoteViews)localObject2;
        localObject1 = this.mConversationTitle;
        break;
        label109:
        i = 1;
        break label26;
        label114:
        localObject2 = Message.-get0((Message)this.mMessages.get(0));
      }
      label153:
      localObject1 = Notification.Builder.-wrap1(this.mBuilder, Notification.Builder.-wrap11(this.mBuilder), false, (CharSequence)localObject1, null);
      Object localObject2 = new int[7];
      Object tmp181_179 = localObject2;
      tmp181_179[0] = 16909246;
      Object tmp186_181 = tmp181_179;
      tmp186_181[1] = 16909247;
      Object tmp191_186 = tmp186_181;
      tmp191_186[2] = 16909248;
      Object tmp196_191 = tmp191_186;
      tmp196_191[3] = 16909249;
      Object tmp201_196 = tmp196_191;
      tmp201_196[4] = 16909250;
      Object tmp206_201 = tmp201_196;
      tmp206_201[5] = 16909251;
      Object tmp211_206 = tmp206_201;
      tmp211_206[6] = 16909252;
      tmp211_206;
      int j = 0;
      int k = localObject2.length;
      while (j < k)
      {
        ((RemoteViews)localObject1).setViewVisibility(localObject2[j], 8);
        j += 1;
      }
      k = 0;
      if (i != 0)
      {
        j = 17104975;
        ((RemoteViews)localObject1).setViewLayoutMarginBottomDimen(16909255, j);
        if (Notification.-wrap1(Notification.Builder.-get2(this.mBuilder))) {
          break label404;
        }
        i = 0;
      }
      for (;;)
      {
        ((RemoteViews)localObject1).setInt(16909254, "setNumIndentLines", i);
        j = -1;
        Message localMessage1 = findLatestIncomingMessage();
        int m = Math.max(0, this.mMessages.size() - localObject2.length);
        i = k;
        while ((m + i < this.mMessages.size()) && (i < localObject2.length))
        {
          Message localMessage2 = (Message)this.mMessages.get(m + i);
          k = localObject2[i];
          ((RemoteViews)localObject1).setViewVisibility(k, 0);
          ((RemoteViews)localObject1).setTextViewText(k, makeMessageLine(localMessage2));
          if (localMessage1 == localMessage2) {
            j = k;
          }
          i += 1;
        }
        j = 0;
        break;
        label404:
        if (i != 0) {
          i = 1;
        } else {
          i = 2;
        }
      }
      ((RemoteViews)localObject1).setInt(16909254, "setContractedChildId", j);
      return (RemoteViews)localObject1;
    }
    
    public RemoteViews makeContentView()
    {
      Object localObject = findLatestIncomingMessage();
      CharSequence localCharSequence;
      if (this.mConversationTitle != null)
      {
        localCharSequence = this.mConversationTitle;
        if (localObject != null) {
          break label58;
        }
        localObject = null;
      }
      for (;;)
      {
        return Notification.Builder.-wrap4(this.mBuilder, Notification.Builder.-wrap6(this.mBuilder), false, localCharSequence, (CharSequence)localObject);
        if (localObject == null)
        {
          localCharSequence = null;
          break;
        }
        localCharSequence = Message.-get0((Message)localObject);
        break;
        label58:
        if (this.mConversationTitle != null) {
          localObject = makeMessageLine((Message)localObject);
        } else {
          localObject = Message.-get1((Message)localObject);
        }
      }
    }
    
    public RemoteViews makeHeadsUpContentView()
    {
      Object localObject = findLatestIncomingMessage();
      CharSequence localCharSequence;
      if (this.mConversationTitle != null)
      {
        localCharSequence = this.mConversationTitle;
        if (localObject != null) {
          break label58;
        }
        localObject = null;
      }
      for (;;)
      {
        return Notification.Builder.-wrap1(this.mBuilder, Notification.Builder.-wrap7(this.mBuilder), false, localCharSequence, (CharSequence)localObject);
        if (localObject == null)
        {
          localCharSequence = null;
          break;
        }
        localCharSequence = Message.-get0((Message)localObject);
        break;
        label58:
        if (this.mConversationTitle != null) {
          localObject = makeMessageLine((Message)localObject);
        } else {
          localObject = Message.-get1((Message)localObject);
        }
      }
    }
    
    protected void restoreFromExtras(Bundle paramBundle)
    {
      super.restoreFromExtras(paramBundle);
      this.mMessages.clear();
      this.mUserDisplayName = paramBundle.getCharSequence("android.selfDisplayName");
      this.mConversationTitle = paramBundle.getCharSequence("android.conversationTitle");
      paramBundle = paramBundle.getParcelableArray("android.messages");
      if ((paramBundle != null) && ((paramBundle instanceof Parcelable[]))) {
        this.mMessages = Message.getMessagesFromBundleArray(paramBundle);
      }
    }
    
    public MessagingStyle setConversationTitle(CharSequence paramCharSequence)
    {
      this.mConversationTitle = paramCharSequence;
      return this;
    }
    
    public static final class Message
    {
      static final String KEY_DATA_MIME_TYPE = "type";
      static final String KEY_DATA_URI = "uri";
      static final String KEY_SENDER = "sender";
      static final String KEY_TEXT = "text";
      static final String KEY_TIMESTAMP = "time";
      private String mDataMimeType;
      private Uri mDataUri;
      private final CharSequence mSender;
      private final CharSequence mText;
      private final long mTimestamp;
      
      public Message(CharSequence paramCharSequence1, long paramLong, CharSequence paramCharSequence2)
      {
        this.mText = paramCharSequence1;
        this.mTimestamp = paramLong;
        this.mSender = paramCharSequence2;
      }
      
      static Bundle[] getBundleArrayForMessages(List<Message> paramList)
      {
        Bundle[] arrayOfBundle = new Bundle[paramList.size()];
        int j = paramList.size();
        int i = 0;
        while (i < j)
        {
          arrayOfBundle[i] = ((Message)paramList.get(i)).toBundle();
          i += 1;
        }
        return arrayOfBundle;
      }
      
      static Message getMessageFromBundle(Bundle paramBundle)
      {
        try
        {
          if ((paramBundle.containsKey("text")) && (paramBundle.containsKey("time")))
          {
            Message localMessage = new Message(paramBundle.getCharSequence("text"), paramBundle.getLong("time"), paramBundle.getCharSequence("sender"));
            if ((paramBundle.containsKey("type")) && (paramBundle.containsKey("uri"))) {
              localMessage.setData(paramBundle.getString("type"), (Uri)paramBundle.getParcelable("uri"));
            }
            return localMessage;
          }
          return null;
        }
        catch (ClassCastException paramBundle) {}
        return null;
      }
      
      static List<Message> getMessagesFromBundleArray(Parcelable[] paramArrayOfParcelable)
      {
        ArrayList localArrayList = new ArrayList(paramArrayOfParcelable.length);
        int i = 0;
        while (i < paramArrayOfParcelable.length)
        {
          if ((paramArrayOfParcelable[i] instanceof Bundle))
          {
            Message localMessage = getMessageFromBundle((Bundle)paramArrayOfParcelable[i]);
            if (localMessage != null) {
              localArrayList.add(localMessage);
            }
          }
          i += 1;
        }
        return localArrayList;
      }
      
      private Bundle toBundle()
      {
        Bundle localBundle = new Bundle();
        if (this.mText != null) {
          localBundle.putCharSequence("text", this.mText);
        }
        localBundle.putLong("time", this.mTimestamp);
        if (this.mSender != null) {
          localBundle.putCharSequence("sender", this.mSender);
        }
        if (this.mDataMimeType != null) {
          localBundle.putString("type", this.mDataMimeType);
        }
        if (this.mDataUri != null) {
          localBundle.putParcelable("uri", this.mDataUri);
        }
        return localBundle;
      }
      
      public String getDataMimeType()
      {
        return this.mDataMimeType;
      }
      
      public Uri getDataUri()
      {
        return this.mDataUri;
      }
      
      public CharSequence getSender()
      {
        return this.mSender;
      }
      
      public CharSequence getText()
      {
        return this.mText;
      }
      
      public long getTimestamp()
      {
        return this.mTimestamp;
      }
      
      public Message setData(String paramString, Uri paramUri)
      {
        this.mDataMimeType = paramString;
        this.mDataUri = paramUri;
        return this;
      }
    }
  }
  
  public static abstract class Style
  {
    private CharSequence mBigContentTitle;
    protected Notification.Builder mBuilder;
    protected CharSequence mSummaryText = null;
    protected boolean mSummaryTextSet = false;
    
    public void addExtras(Bundle paramBundle)
    {
      if (this.mSummaryTextSet) {
        paramBundle.putCharSequence("android.summaryText", this.mSummaryText);
      }
      if (this.mBigContentTitle != null) {
        paramBundle.putCharSequence("android.title.big", this.mBigContentTitle);
      }
      paramBundle.putString("android.template", getClass().getName());
    }
    
    public Notification build()
    {
      checkBuilder();
      return this.mBuilder.build();
    }
    
    public Notification buildStyled(Notification paramNotification)
    {
      addExtras(paramNotification.extras);
      return paramNotification;
    }
    
    protected void checkBuilder()
    {
      if (this.mBuilder == null) {
        throw new IllegalArgumentException("Style requires a valid Builder object");
      }
    }
    
    public boolean displayCustomViewInline()
    {
      return false;
    }
    
    protected RemoteViews getStandardView(int paramInt)
    {
      checkBuilder();
      CharSequence localCharSequence = Notification.Builder.-wrap0(this.mBuilder).getCharSequence("android.title");
      if (this.mBigContentTitle != null) {
        this.mBuilder.setContentTitle(this.mBigContentTitle);
      }
      RemoteViews localRemoteViews = Notification.Builder.-wrap2(this.mBuilder, paramInt);
      Notification.Builder.-wrap0(this.mBuilder).putCharSequence("android.title", localCharSequence);
      if ((this.mBigContentTitle != null) && (this.mBigContentTitle.equals("")))
      {
        localRemoteViews.setViewVisibility(16909255, 8);
        return localRemoteViews;
      }
      localRemoteViews.setViewVisibility(16909255, 0);
      return localRemoteViews;
    }
    
    protected boolean hasProgress()
    {
      return true;
    }
    
    public boolean hasSummaryInHeader()
    {
      return true;
    }
    
    protected void internalSetBigContentTitle(CharSequence paramCharSequence)
    {
      this.mBigContentTitle = paramCharSequence;
    }
    
    protected void internalSetSummaryText(CharSequence paramCharSequence)
    {
      this.mSummaryText = paramCharSequence;
      this.mSummaryTextSet = true;
    }
    
    public RemoteViews makeBigContentView()
    {
      return null;
    }
    
    public RemoteViews makeContentView()
    {
      return null;
    }
    
    public RemoteViews makeHeadsUpContentView()
    {
      return null;
    }
    
    public void purgeResources() {}
    
    protected void restoreFromExtras(Bundle paramBundle)
    {
      if (paramBundle.containsKey("android.summaryText"))
      {
        this.mSummaryText = paramBundle.getCharSequence("android.summaryText");
        this.mSummaryTextSet = true;
      }
      if (paramBundle.containsKey("android.title.big")) {
        this.mBigContentTitle = paramBundle.getCharSequence("android.title.big");
      }
    }
    
    public void setBuilder(Notification.Builder paramBuilder)
    {
      if (this.mBuilder != paramBuilder)
      {
        this.mBuilder = paramBuilder;
        if (this.mBuilder != null) {
          this.mBuilder.setStyle(this);
        }
      }
    }
  }
  
  public static final class WearableExtender
    implements Notification.Extender
  {
    private static final int DEFAULT_CONTENT_ICON_GRAVITY = 8388613;
    private static final int DEFAULT_FLAGS = 1;
    private static final int DEFAULT_GRAVITY = 80;
    private static final String EXTRA_WEARABLE_EXTENSIONS = "android.wearable.EXTENSIONS";
    private static final int FLAG_BIG_PICTURE_AMBIENT = 32;
    private static final int FLAG_CONTENT_INTENT_AVAILABLE_OFFLINE = 1;
    private static final int FLAG_HINT_AVOID_BACKGROUND_CLIPPING = 16;
    private static final int FLAG_HINT_CONTENT_INTENT_LAUNCHES_ACTIVITY = 64;
    private static final int FLAG_HINT_HIDE_ICON = 2;
    private static final int FLAG_HINT_SHOW_BACKGROUND_ONLY = 4;
    private static final int FLAG_START_SCROLL_BOTTOM = 8;
    private static final String KEY_ACTIONS = "actions";
    private static final String KEY_BACKGROUND = "background";
    private static final String KEY_CONTENT_ACTION_INDEX = "contentActionIndex";
    private static final String KEY_CONTENT_ICON = "contentIcon";
    private static final String KEY_CONTENT_ICON_GRAVITY = "contentIconGravity";
    private static final String KEY_CUSTOM_CONTENT_HEIGHT = "customContentHeight";
    private static final String KEY_CUSTOM_SIZE_PRESET = "customSizePreset";
    private static final String KEY_DISMISSAL_ID = "dismissalId";
    private static final String KEY_DISPLAY_INTENT = "displayIntent";
    private static final String KEY_FLAGS = "flags";
    private static final String KEY_GRAVITY = "gravity";
    private static final String KEY_HINT_SCREEN_TIMEOUT = "hintScreenTimeout";
    private static final String KEY_PAGES = "pages";
    public static final int SCREEN_TIMEOUT_LONG = -1;
    public static final int SCREEN_TIMEOUT_SHORT = 0;
    public static final int SIZE_DEFAULT = 0;
    public static final int SIZE_FULL_SCREEN = 5;
    public static final int SIZE_LARGE = 4;
    public static final int SIZE_MEDIUM = 3;
    public static final int SIZE_SMALL = 2;
    public static final int SIZE_XSMALL = 1;
    public static final int UNSET_ACTION_INDEX = -1;
    private ArrayList<Notification.Action> mActions = new ArrayList();
    private Bitmap mBackground;
    private int mContentActionIndex = -1;
    private int mContentIcon;
    private int mContentIconGravity = 8388613;
    private int mCustomContentHeight;
    private int mCustomSizePreset = 0;
    private String mDismissalId;
    private PendingIntent mDisplayIntent;
    private int mFlags = 1;
    private int mGravity = 80;
    private int mHintScreenTimeout;
    private ArrayList<Notification> mPages = new ArrayList();
    
    public WearableExtender() {}
    
    public WearableExtender(Notification paramNotification)
    {
      paramNotification = paramNotification.extras.getBundle("android.wearable.EXTENSIONS");
      if (paramNotification != null)
      {
        Object localObject = paramNotification.getParcelableArrayList("actions");
        if (localObject != null) {
          this.mActions.addAll((Collection)localObject);
        }
        this.mFlags = paramNotification.getInt("flags", 1);
        this.mDisplayIntent = ((PendingIntent)paramNotification.getParcelable("displayIntent"));
        localObject = Notification.-wrap0(paramNotification, "pages");
        if (localObject != null) {
          Collections.addAll(this.mPages, (Object[])localObject);
        }
        this.mBackground = ((Bitmap)paramNotification.getParcelable("background"));
        this.mContentIcon = paramNotification.getInt("contentIcon");
        this.mContentIconGravity = paramNotification.getInt("contentIconGravity", 8388613);
        this.mContentActionIndex = paramNotification.getInt("contentActionIndex", -1);
        this.mCustomSizePreset = paramNotification.getInt("customSizePreset", 0);
        this.mCustomContentHeight = paramNotification.getInt("customContentHeight");
        this.mGravity = paramNotification.getInt("gravity", 80);
        this.mHintScreenTimeout = paramNotification.getInt("hintScreenTimeout");
        this.mDismissalId = paramNotification.getString("dismissalId");
      }
    }
    
    private void setFlag(int paramInt, boolean paramBoolean)
    {
      if (paramBoolean)
      {
        this.mFlags |= paramInt;
        return;
      }
      this.mFlags &= paramInt;
    }
    
    public WearableExtender addAction(Notification.Action paramAction)
    {
      this.mActions.add(paramAction);
      return this;
    }
    
    public WearableExtender addActions(List<Notification.Action> paramList)
    {
      this.mActions.addAll(paramList);
      return this;
    }
    
    public WearableExtender addPage(Notification paramNotification)
    {
      this.mPages.add(paramNotification);
      return this;
    }
    
    public WearableExtender addPages(List<Notification> paramList)
    {
      this.mPages.addAll(paramList);
      return this;
    }
    
    public WearableExtender clearActions()
    {
      this.mActions.clear();
      return this;
    }
    
    public WearableExtender clearPages()
    {
      this.mPages.clear();
      return this;
    }
    
    public WearableExtender clone()
    {
      WearableExtender localWearableExtender = new WearableExtender();
      localWearableExtender.mActions = new ArrayList(this.mActions);
      localWearableExtender.mFlags = this.mFlags;
      localWearableExtender.mDisplayIntent = this.mDisplayIntent;
      localWearableExtender.mPages = new ArrayList(this.mPages);
      localWearableExtender.mBackground = this.mBackground;
      localWearableExtender.mContentIcon = this.mContentIcon;
      localWearableExtender.mContentIconGravity = this.mContentIconGravity;
      localWearableExtender.mContentActionIndex = this.mContentActionIndex;
      localWearableExtender.mCustomSizePreset = this.mCustomSizePreset;
      localWearableExtender.mCustomContentHeight = this.mCustomContentHeight;
      localWearableExtender.mGravity = this.mGravity;
      localWearableExtender.mHintScreenTimeout = this.mHintScreenTimeout;
      localWearableExtender.mDismissalId = this.mDismissalId;
      return localWearableExtender;
    }
    
    public Notification.Builder extend(Notification.Builder paramBuilder)
    {
      Bundle localBundle = new Bundle();
      if (!this.mActions.isEmpty()) {
        localBundle.putParcelableArrayList("actions", this.mActions);
      }
      if (this.mFlags != 1) {
        localBundle.putInt("flags", this.mFlags);
      }
      if (this.mDisplayIntent != null) {
        localBundle.putParcelable("displayIntent", this.mDisplayIntent);
      }
      if (!this.mPages.isEmpty()) {
        localBundle.putParcelableArray("pages", (Parcelable[])this.mPages.toArray(new Notification[this.mPages.size()]));
      }
      if (this.mBackground != null) {
        localBundle.putParcelable("background", this.mBackground);
      }
      if (this.mContentIcon != 0) {
        localBundle.putInt("contentIcon", this.mContentIcon);
      }
      if (this.mContentIconGravity != 8388613) {
        localBundle.putInt("contentIconGravity", this.mContentIconGravity);
      }
      if (this.mContentActionIndex != -1) {
        localBundle.putInt("contentActionIndex", this.mContentActionIndex);
      }
      if (this.mCustomSizePreset != 0) {
        localBundle.putInt("customSizePreset", this.mCustomSizePreset);
      }
      if (this.mCustomContentHeight != 0) {
        localBundle.putInt("customContentHeight", this.mCustomContentHeight);
      }
      if (this.mGravity != 80) {
        localBundle.putInt("gravity", this.mGravity);
      }
      if (this.mHintScreenTimeout != 0) {
        localBundle.putInt("hintScreenTimeout", this.mHintScreenTimeout);
      }
      if (this.mDismissalId != null) {
        localBundle.putString("dismissalId", this.mDismissalId);
      }
      paramBuilder.getExtras().putBundle("android.wearable.EXTENSIONS", localBundle);
      return paramBuilder;
    }
    
    public List<Notification.Action> getActions()
    {
      return this.mActions;
    }
    
    public Bitmap getBackground()
    {
      return this.mBackground;
    }
    
    public int getContentAction()
    {
      return this.mContentActionIndex;
    }
    
    public int getContentIcon()
    {
      return this.mContentIcon;
    }
    
    public int getContentIconGravity()
    {
      return this.mContentIconGravity;
    }
    
    public boolean getContentIntentAvailableOffline()
    {
      boolean bool = false;
      if ((this.mFlags & 0x1) != 0) {
        bool = true;
      }
      return bool;
    }
    
    public int getCustomContentHeight()
    {
      return this.mCustomContentHeight;
    }
    
    public int getCustomSizePreset()
    {
      return this.mCustomSizePreset;
    }
    
    public String getDismissalId()
    {
      return this.mDismissalId;
    }
    
    public PendingIntent getDisplayIntent()
    {
      return this.mDisplayIntent;
    }
    
    public int getGravity()
    {
      return this.mGravity;
    }
    
    public boolean getHintAmbientBigPicture()
    {
      boolean bool = false;
      if ((this.mFlags & 0x20) != 0) {
        bool = true;
      }
      return bool;
    }
    
    public boolean getHintAvoidBackgroundClipping()
    {
      boolean bool = false;
      if ((this.mFlags & 0x10) != 0) {
        bool = true;
      }
      return bool;
    }
    
    public boolean getHintContentIntentLaunchesActivity()
    {
      boolean bool = false;
      if ((this.mFlags & 0x40) != 0) {
        bool = true;
      }
      return bool;
    }
    
    public boolean getHintHideIcon()
    {
      boolean bool = false;
      if ((this.mFlags & 0x2) != 0) {
        bool = true;
      }
      return bool;
    }
    
    public int getHintScreenTimeout()
    {
      return this.mHintScreenTimeout;
    }
    
    public boolean getHintShowBackgroundOnly()
    {
      boolean bool = false;
      if ((this.mFlags & 0x4) != 0) {
        bool = true;
      }
      return bool;
    }
    
    public List<Notification> getPages()
    {
      return this.mPages;
    }
    
    public boolean getStartScrollBottom()
    {
      boolean bool = false;
      if ((this.mFlags & 0x8) != 0) {
        bool = true;
      }
      return bool;
    }
    
    public WearableExtender setBackground(Bitmap paramBitmap)
    {
      this.mBackground = paramBitmap;
      return this;
    }
    
    public WearableExtender setContentAction(int paramInt)
    {
      this.mContentActionIndex = paramInt;
      return this;
    }
    
    public WearableExtender setContentIcon(int paramInt)
    {
      this.mContentIcon = paramInt;
      return this;
    }
    
    public WearableExtender setContentIconGravity(int paramInt)
    {
      this.mContentIconGravity = paramInt;
      return this;
    }
    
    public WearableExtender setContentIntentAvailableOffline(boolean paramBoolean)
    {
      setFlag(1, paramBoolean);
      return this;
    }
    
    public WearableExtender setCustomContentHeight(int paramInt)
    {
      this.mCustomContentHeight = paramInt;
      return this;
    }
    
    public WearableExtender setCustomSizePreset(int paramInt)
    {
      this.mCustomSizePreset = paramInt;
      return this;
    }
    
    public WearableExtender setDismissalId(String paramString)
    {
      this.mDismissalId = paramString;
      return this;
    }
    
    public WearableExtender setDisplayIntent(PendingIntent paramPendingIntent)
    {
      this.mDisplayIntent = paramPendingIntent;
      return this;
    }
    
    public WearableExtender setGravity(int paramInt)
    {
      this.mGravity = paramInt;
      return this;
    }
    
    public WearableExtender setHintAmbientBigPicture(boolean paramBoolean)
    {
      setFlag(32, paramBoolean);
      return this;
    }
    
    public WearableExtender setHintAvoidBackgroundClipping(boolean paramBoolean)
    {
      setFlag(16, paramBoolean);
      return this;
    }
    
    public WearableExtender setHintContentIntentLaunchesActivity(boolean paramBoolean)
    {
      setFlag(64, paramBoolean);
      return this;
    }
    
    public WearableExtender setHintHideIcon(boolean paramBoolean)
    {
      setFlag(2, paramBoolean);
      return this;
    }
    
    public WearableExtender setHintScreenTimeout(int paramInt)
    {
      this.mHintScreenTimeout = paramInt;
      return this;
    }
    
    public WearableExtender setHintShowBackgroundOnly(boolean paramBoolean)
    {
      setFlag(4, paramBoolean);
      return this;
    }
    
    public WearableExtender setStartScrollBottom(boolean paramBoolean)
    {
      setFlag(8, paramBoolean);
      return this;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/Notification.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */