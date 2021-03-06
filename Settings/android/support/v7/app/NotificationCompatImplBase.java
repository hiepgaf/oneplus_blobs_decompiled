package android.support.v7.app;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.SystemClock;
import android.support.v4.app.NotificationBuilderWithBuilderAccessor;
import android.support.v4.app.NotificationCompat.Action;
import android.support.v4.app.NotificationCompatBase.Action;
import android.support.v7.appcompat.R.color;
import android.support.v7.appcompat.R.dimen;
import android.support.v7.appcompat.R.drawable;
import android.support.v7.appcompat.R.id;
import android.support.v7.appcompat.R.integer;
import android.support.v7.appcompat.R.layout;
import android.support.v7.appcompat.R.string;
import android.widget.RemoteViews;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

class NotificationCompatImplBase
{
  private static final int MAX_ACTION_BUTTONS = 3;
  static final int MAX_MEDIA_BUTTONS = 5;
  static final int MAX_MEDIA_BUTTONS_IN_COMPACT = 3;
  
  public static RemoteViews applyStandardTemplate(Context paramContext, CharSequence paramCharSequence1, CharSequence paramCharSequence2, CharSequence paramCharSequence3, int paramInt1, int paramInt2, Bitmap paramBitmap, CharSequence paramCharSequence4, boolean paramBoolean1, long paramLong, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean2)
  {
    Resources localResources = paramContext.getResources();
    RemoteViews localRemoteViews = new RemoteViews(paramContext.getPackageName(), paramInt5);
    int k = 0;
    int j = 0;
    label47:
    int i;
    if (paramInt3 < -1)
    {
      paramInt3 = 1;
      if (Build.VERSION.SDK_INT < 16) {
        break label452;
      }
      paramInt5 = 1;
      if (Build.VERSION.SDK_INT < 21) {
        break label458;
      }
      i = 1;
      label58:
      if ((paramInt5 != 0) && (i == 0)) {
        break label464;
      }
      label68:
      if (paramBitmap == null) {
        break label558;
      }
      if (paramInt5 == 0) {
        break label527;
      }
      localRemoteViews.setViewVisibility(R.id.icon, 0);
      localRemoteViews.setImageViewBitmap(R.id.icon, paramBitmap);
      label97:
      if (paramInt2 != 0)
      {
        paramInt3 = localResources.getDimensionPixelSize(R.dimen.notification_right_icon_size);
        int m = localResources.getDimensionPixelSize(R.dimen.notification_small_icon_background_padding);
        if (i == 0) {
          break label540;
        }
        paramContext = createIconWithBackground(paramContext, paramInt2, paramInt3, paramInt3 - m * 2, paramInt4);
        localRemoteViews.setImageViewBitmap(R.id.right_icon, paramContext);
        label154:
        localRemoteViews.setViewVisibility(R.id.right_icon, 0);
      }
      label163:
      if (paramCharSequence1 != null) {
        localRemoteViews.setTextViewText(R.id.title, paramCharSequence1);
      }
      paramInt2 = k;
      if (paramCharSequence2 != null)
      {
        localRemoteViews.setTextViewText(R.id.text, paramCharSequence2);
        paramInt2 = 1;
      }
      if ((i != 0) || (paramBitmap == null)) {
        break label641;
      }
      paramInt3 = 1;
      label209:
      if (paramCharSequence3 == null) {
        break label647;
      }
      localRemoteViews.setTextViewText(R.id.info, paramCharSequence3);
      localRemoteViews.setViewVisibility(R.id.info, 0);
      paramInt2 = 1;
      paramInt1 = 1;
      label237:
      paramInt3 = j;
      if (paramCharSequence4 != null)
      {
        paramInt3 = j;
        if (paramInt5 != 0)
        {
          localRemoteViews.setTextViewText(R.id.text, paramCharSequence4);
          if (paramCharSequence2 == null) {
            break label738;
          }
          localRemoteViews.setTextViewText(R.id.text2, paramCharSequence2);
          localRemoteViews.setViewVisibility(R.id.text2, 0);
          paramInt3 = 1;
        }
      }
      label290:
      if ((paramInt3 != 0) && (paramInt5 != 0))
      {
        if (paramBoolean2)
        {
          float f = localResources.getDimensionPixelSize(R.dimen.notification_subtext_size);
          localRemoteViews.setTextViewTextSize(R.id.text, 0, f);
        }
        localRemoteViews.setViewPadding(R.id.line1, 0, 0, 0, 0);
      }
      if (paramLong != 0L)
      {
        if ((!paramBoolean1) || (paramInt5 == 0)) {
          break label755;
        }
        localRemoteViews.setViewVisibility(R.id.chronometer, 0);
        localRemoteViews.setLong(R.id.chronometer, "setBase", SystemClock.elapsedRealtime() - System.currentTimeMillis() + paramLong);
        localRemoteViews.setBoolean(R.id.chronometer, "setStarted", true);
        label396:
        paramInt1 = 1;
      }
      paramInt3 = R.id.right_side;
      if (paramInt1 == 0) {
        break label779;
      }
      paramInt1 = 0;
      label412:
      localRemoteViews.setViewVisibility(paramInt3, paramInt1);
      paramInt3 = R.id.line3;
      if (paramInt2 == 0) {
        break label786;
      }
    }
    label452:
    label458:
    label464:
    label527:
    label540:
    label558:
    label641:
    label647:
    label738:
    label755:
    label779:
    label786:
    for (paramInt1 = 0;; paramInt1 = 8)
    {
      localRemoteViews.setViewVisibility(paramInt3, paramInt1);
      return localRemoteViews;
      paramInt3 = 0;
      break;
      paramInt5 = 0;
      break label47;
      i = 0;
      break label58;
      if (paramInt3 != 0)
      {
        localRemoteViews.setInt(R.id.notification_background, "setBackgroundResource", R.drawable.notification_bg_low);
        localRemoteViews.setInt(R.id.icon, "setBackgroundResource", R.drawable.notification_template_icon_low_bg);
        break label68;
      }
      localRemoteViews.setInt(R.id.notification_background, "setBackgroundResource", R.drawable.notification_bg);
      localRemoteViews.setInt(R.id.icon, "setBackgroundResource", R.drawable.notification_template_icon_bg);
      break label68;
      localRemoteViews.setViewVisibility(R.id.icon, 8);
      break label97;
      localRemoteViews.setImageViewBitmap(R.id.right_icon, createColoredBitmap(paramContext, paramInt2, -1));
      break label154;
      if (paramInt2 == 0) {
        break label163;
      }
      localRemoteViews.setViewVisibility(R.id.icon, 0);
      if (i != 0)
      {
        paramContext = createIconWithBackground(paramContext, paramInt2, localResources.getDimensionPixelSize(R.dimen.notification_large_icon_width) - localResources.getDimensionPixelSize(R.dimen.notification_big_circle_margin), localResources.getDimensionPixelSize(R.dimen.notification_small_icon_size_as_large), paramInt4);
        localRemoteViews.setImageViewBitmap(R.id.icon, paramContext);
        break label163;
      }
      localRemoteViews.setImageViewBitmap(R.id.icon, createColoredBitmap(paramContext, paramInt2, -1));
      break label163;
      paramInt3 = 0;
      break label209;
      if (paramInt1 > 0)
      {
        if (paramInt1 > localResources.getInteger(R.integer.status_bar_notification_info_maxnum)) {
          localRemoteViews.setTextViewText(R.id.info, localResources.getString(R.string.status_bar_notification_info_overflow));
        }
        for (;;)
        {
          localRemoteViews.setViewVisibility(R.id.info, 0);
          paramInt2 = 1;
          paramInt1 = 1;
          break;
          paramContext = NumberFormat.getIntegerInstance();
          localRemoteViews.setTextViewText(R.id.info, paramContext.format(paramInt1));
        }
      }
      localRemoteViews.setViewVisibility(R.id.info, 8);
      paramInt1 = paramInt3;
      break label237;
      localRemoteViews.setViewVisibility(R.id.text2, 8);
      paramInt3 = j;
      break label290;
      localRemoteViews.setViewVisibility(R.id.time, 0);
      localRemoteViews.setLong(R.id.time, "setTime", paramLong);
      break label396;
      paramInt1 = 8;
      break label412;
    }
  }
  
  public static RemoteViews applyStandardTemplateWithActions(Context paramContext, CharSequence paramCharSequence1, CharSequence paramCharSequence2, CharSequence paramCharSequence3, int paramInt1, int paramInt2, Bitmap paramBitmap, CharSequence paramCharSequence4, boolean paramBoolean1, long paramLong, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean2, ArrayList<NotificationCompat.Action> paramArrayList)
  {
    paramCharSequence1 = applyStandardTemplate(paramContext, paramCharSequence1, paramCharSequence2, paramCharSequence3, paramInt1, paramInt2, paramBitmap, paramCharSequence4, paramBoolean1, paramLong, paramInt3, paramInt4, paramInt5, paramBoolean2);
    paramCharSequence1.removeAllViews(R.id.actions);
    paramInt1 = 0;
    paramInt2 = paramInt1;
    if (paramArrayList != null)
    {
      paramInt3 = paramArrayList.size();
      paramInt2 = paramInt1;
      if (paramInt3 > 0)
      {
        paramInt4 = 1;
        paramInt1 = paramInt3;
        if (paramInt3 > 3) {
          paramInt1 = 3;
        }
        paramInt3 = 0;
        for (;;)
        {
          paramInt2 = paramInt4;
          if (paramInt3 >= paramInt1) {
            break;
          }
          paramCharSequence2 = generateActionButton(paramContext, (NotificationCompat.Action)paramArrayList.get(paramInt3));
          paramCharSequence1.addView(R.id.actions, paramCharSequence2);
          paramInt3 += 1;
        }
      }
    }
    if (paramInt2 != 0) {}
    for (paramInt1 = 0;; paramInt1 = 8)
    {
      paramCharSequence1.setViewVisibility(R.id.actions, paramInt1);
      paramCharSequence1.setViewVisibility(R.id.action_divider, paramInt1);
      return paramCharSequence1;
    }
  }
  
  public static void buildIntoRemoteViews(Context paramContext, RemoteViews paramRemoteViews1, RemoteViews paramRemoteViews2)
  {
    hideNormalContent(paramRemoteViews1);
    paramRemoteViews1.removeAllViews(R.id.notification_main_column);
    paramRemoteViews1.addView(R.id.notification_main_column, paramRemoteViews2.clone());
    paramRemoteViews1.setViewVisibility(R.id.notification_main_column, 0);
    if (Build.VERSION.SDK_INT >= 21) {
      paramRemoteViews1.setViewPadding(R.id.notification_main_column_container, 0, calculateTopPadding(paramContext), 0, 0);
    }
  }
  
  public static int calculateTopPadding(Context paramContext)
  {
    int i = paramContext.getResources().getDimensionPixelSize(R.dimen.notification_top_pad);
    int j = paramContext.getResources().getDimensionPixelSize(R.dimen.notification_top_pad_large_text);
    float f = (constrain(paramContext.getResources().getConfiguration().fontScale, 1.0F, 1.3F) - 1.0F) / 0.29999995F;
    return Math.round((1.0F - f) * i + j * f);
  }
  
  public static float constrain(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    if (paramFloat1 < paramFloat2) {
      return paramFloat2;
    }
    if (paramFloat1 > paramFloat3) {
      return paramFloat3;
    }
    return paramFloat1;
  }
  
  private static Bitmap createColoredBitmap(Context paramContext, int paramInt1, int paramInt2)
  {
    return createColoredBitmap(paramContext, paramInt1, paramInt2, 0);
  }
  
  private static Bitmap createColoredBitmap(Context paramContext, int paramInt1, int paramInt2, int paramInt3)
  {
    paramContext = paramContext.getResources().getDrawable(paramInt1);
    if (paramInt3 == 0)
    {
      paramInt1 = paramContext.getIntrinsicWidth();
      if (paramInt3 != 0) {
        break label88;
      }
      paramInt3 = paramContext.getIntrinsicHeight();
    }
    label88:
    for (;;)
    {
      Bitmap localBitmap = Bitmap.createBitmap(paramInt1, paramInt3, Bitmap.Config.ARGB_8888);
      paramContext.setBounds(0, 0, paramInt1, paramInt3);
      if (paramInt2 != 0) {
        paramContext.mutate().setColorFilter(new PorterDuffColorFilter(paramInt2, PorterDuff.Mode.SRC_IN));
      }
      paramContext.draw(new Canvas(localBitmap));
      return localBitmap;
      paramInt1 = paramInt3;
      break;
    }
  }
  
  public static Bitmap createIconWithBackground(Context paramContext, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int j = R.drawable.notification_icon_background;
    int i = paramInt4;
    if (paramInt4 == 0) {
      i = 0;
    }
    Bitmap localBitmap = createColoredBitmap(paramContext, j, i, paramInt2);
    Canvas localCanvas = new Canvas(localBitmap);
    paramContext = paramContext.getResources().getDrawable(paramInt1).mutate();
    paramContext.setFilterBitmap(true);
    paramInt1 = (paramInt2 - paramInt3) / 2;
    paramContext.setBounds(paramInt1, paramInt1, paramInt3 + paramInt1, paramInt3 + paramInt1);
    paramContext.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.SRC_ATOP));
    paramContext.draw(localCanvas);
    return localBitmap;
  }
  
  private static RemoteViews generateActionButton(Context paramContext, NotificationCompat.Action paramAction)
  {
    int i;
    Object localObject;
    if (paramAction.actionIntent == null)
    {
      i = 1;
      localObject = paramContext.getPackageName();
      if (i == 0) {
        break label109;
      }
    }
    label109:
    for (int j = getActionTombstoneLayoutResource();; j = getActionLayoutResource())
    {
      localObject = new RemoteViews((String)localObject, j);
      ((RemoteViews)localObject).setImageViewBitmap(R.id.action_image, createColoredBitmap(paramContext, paramAction.getIcon(), paramContext.getResources().getColor(R.color.notification_action_color_filter)));
      ((RemoteViews)localObject).setTextViewText(R.id.action_text, paramAction.title);
      if (i == 0) {
        ((RemoteViews)localObject).setOnClickPendingIntent(R.id.action_container, paramAction.actionIntent);
      }
      ((RemoteViews)localObject).setContentDescription(R.id.action_container, paramAction.title);
      return (RemoteViews)localObject;
      i = 0;
      break;
    }
  }
  
  private static <T extends NotificationCompatBase.Action> RemoteViews generateContentViewMedia(Context paramContext, CharSequence paramCharSequence1, CharSequence paramCharSequence2, CharSequence paramCharSequence3, int paramInt1, Bitmap paramBitmap, CharSequence paramCharSequence4, boolean paramBoolean1, long paramLong, int paramInt2, List<T> paramList, int[] paramArrayOfInt, boolean paramBoolean2, PendingIntent paramPendingIntent, boolean paramBoolean3)
  {
    int i;
    if (paramBoolean3)
    {
      i = R.layout.notification_template_media_custom;
      paramCharSequence1 = applyStandardTemplate(paramContext, paramCharSequence1, paramCharSequence2, paramCharSequence3, paramInt1, 0, paramBitmap, paramCharSequence4, paramBoolean1, paramLong, paramInt2, 0, i, true);
      i = paramList.size();
      if (paramArrayOfInt != null) {
        break label125;
      }
      paramInt1 = 0;
      label52:
      paramCharSequence1.removeAllViews(R.id.media_actions);
      if (paramInt1 <= 0) {
        break label174;
      }
      paramInt2 = 0;
    }
    for (;;)
    {
      if (paramInt2 >= paramInt1) {
        break label174;
      }
      if (paramInt2 >= i)
      {
        throw new IllegalArgumentException(String.format("setShowActionsInCompactView: action %d out of bounds (max %d)", new Object[] { Integer.valueOf(paramInt2), Integer.valueOf(i - 1) }));
        i = R.layout.notification_template_media;
        break;
        label125:
        paramInt1 = Math.min(paramArrayOfInt.length, 3);
        break label52;
      }
      paramCharSequence2 = generateMediaActionButton(paramContext, (NotificationCompatBase.Action)paramList.get(paramArrayOfInt[paramInt2]));
      paramCharSequence1.addView(R.id.media_actions, paramCharSequence2);
      paramInt2 += 1;
    }
    label174:
    if (paramBoolean2)
    {
      paramCharSequence1.setViewVisibility(R.id.end_padder, 8);
      paramCharSequence1.setViewVisibility(R.id.cancel_action, 0);
      paramCharSequence1.setOnClickPendingIntent(R.id.cancel_action, paramPendingIntent);
      paramCharSequence1.setInt(R.id.cancel_action, "setAlpha", paramContext.getResources().getInteger(R.integer.cancel_button_image_alpha));
      return paramCharSequence1;
    }
    paramCharSequence1.setViewVisibility(R.id.end_padder, 0);
    paramCharSequence1.setViewVisibility(R.id.cancel_action, 8);
    return paramCharSequence1;
  }
  
  private static RemoteViews generateMediaActionButton(Context paramContext, NotificationCompatBase.Action paramAction)
  {
    if (paramAction.getActionIntent() == null) {}
    for (int i = 1;; i = 0)
    {
      paramContext = new RemoteViews(paramContext.getPackageName(), R.layout.notification_media_action);
      paramContext.setImageViewResource(R.id.action0, paramAction.getIcon());
      if (i == 0) {
        paramContext.setOnClickPendingIntent(R.id.action0, paramAction.getActionIntent());
      }
      if (Build.VERSION.SDK_INT >= 15) {
        paramContext.setContentDescription(R.id.action0, paramAction.getTitle());
      }
      return paramContext;
    }
  }
  
  public static <T extends NotificationCompatBase.Action> RemoteViews generateMediaBigView(Context paramContext, CharSequence paramCharSequence1, CharSequence paramCharSequence2, CharSequence paramCharSequence3, int paramInt1, Bitmap paramBitmap, CharSequence paramCharSequence4, boolean paramBoolean1, long paramLong, int paramInt2, int paramInt3, List<T> paramList, boolean paramBoolean2, PendingIntent paramPendingIntent, boolean paramBoolean3)
  {
    int i = Math.min(paramList.size(), 5);
    paramCharSequence1 = applyStandardTemplate(paramContext, paramCharSequence1, paramCharSequence2, paramCharSequence3, paramInt1, 0, paramBitmap, paramCharSequence4, paramBoolean1, paramLong, paramInt2, paramInt3, getBigMediaLayoutResource(paramBoolean3, i), false);
    paramCharSequence1.removeAllViews(R.id.media_actions);
    if (i > 0)
    {
      paramInt1 = 0;
      while (paramInt1 < i)
      {
        paramCharSequence2 = generateMediaActionButton(paramContext, (NotificationCompatBase.Action)paramList.get(paramInt1));
        paramCharSequence1.addView(R.id.media_actions, paramCharSequence2);
        paramInt1 += 1;
      }
    }
    if (paramBoolean2)
    {
      paramCharSequence1.setViewVisibility(R.id.cancel_action, 0);
      paramCharSequence1.setInt(R.id.cancel_action, "setAlpha", paramContext.getResources().getInteger(R.integer.cancel_button_image_alpha));
      paramCharSequence1.setOnClickPendingIntent(R.id.cancel_action, paramPendingIntent);
      return paramCharSequence1;
    }
    paramCharSequence1.setViewVisibility(R.id.cancel_action, 8);
    return paramCharSequence1;
  }
  
  private static int getActionLayoutResource()
  {
    return R.layout.notification_action;
  }
  
  private static int getActionTombstoneLayoutResource()
  {
    return R.layout.notification_action_tombstone;
  }
  
  private static int getBigMediaLayoutResource(boolean paramBoolean, int paramInt)
  {
    if (paramInt <= 3)
    {
      if (paramBoolean) {
        return R.layout.notification_template_big_media_narrow_custom;
      }
      return R.layout.notification_template_big_media_narrow;
    }
    if (paramBoolean) {
      return R.layout.notification_template_big_media_custom;
    }
    return R.layout.notification_template_big_media;
  }
  
  private static void hideNormalContent(RemoteViews paramRemoteViews)
  {
    paramRemoteViews.setViewVisibility(R.id.title, 8);
    paramRemoteViews.setViewVisibility(R.id.text2, 8);
    paramRemoteViews.setViewVisibility(R.id.text, 8);
  }
  
  public static <T extends NotificationCompatBase.Action> RemoteViews overrideContentViewMedia(NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor, Context paramContext, CharSequence paramCharSequence1, CharSequence paramCharSequence2, CharSequence paramCharSequence3, int paramInt1, Bitmap paramBitmap, CharSequence paramCharSequence4, boolean paramBoolean1, long paramLong, int paramInt2, List<T> paramList, int[] paramArrayOfInt, boolean paramBoolean2, PendingIntent paramPendingIntent, boolean paramBoolean3)
  {
    paramContext = generateContentViewMedia(paramContext, paramCharSequence1, paramCharSequence2, paramCharSequence3, paramInt1, paramBitmap, paramCharSequence4, paramBoolean1, paramLong, paramInt2, paramList, paramArrayOfInt, paramBoolean2, paramPendingIntent, paramBoolean3);
    paramNotificationBuilderWithBuilderAccessor.getBuilder().setContent(paramContext);
    if (paramBoolean2) {
      paramNotificationBuilderWithBuilderAccessor.getBuilder().setOngoing(true);
    }
    return paramContext;
  }
  
  public static <T extends NotificationCompatBase.Action> void overrideMediaBigContentView(Notification paramNotification, Context paramContext, CharSequence paramCharSequence1, CharSequence paramCharSequence2, CharSequence paramCharSequence3, int paramInt1, Bitmap paramBitmap, CharSequence paramCharSequence4, boolean paramBoolean1, long paramLong, int paramInt2, int paramInt3, List<T> paramList, boolean paramBoolean2, PendingIntent paramPendingIntent, boolean paramBoolean3)
  {
    paramNotification.bigContentView = generateMediaBigView(paramContext, paramCharSequence1, paramCharSequence2, paramCharSequence3, paramInt1, paramBitmap, paramCharSequence4, paramBoolean1, paramLong, paramInt2, paramInt3, paramList, paramBoolean2, paramPendingIntent, paramBoolean3);
    if (paramBoolean2) {
      paramNotification.flags |= 0x2;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v7/app/NotificationCompatImplBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */