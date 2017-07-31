package android.appwidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AppWidgetProvider
  extends BroadcastReceiver
{
  public void onAppWidgetOptionsChanged(Context paramContext, AppWidgetManager paramAppWidgetManager, int paramInt, Bundle paramBundle) {}
  
  public void onDeleted(Context paramContext, int[] paramArrayOfInt) {}
  
  public void onDisabled(Context paramContext) {}
  
  public void onEnabled(Context paramContext) {}
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    Object localObject = paramIntent.getAction();
    if ("android.appwidget.action.APPWIDGET_UPDATE".equals(localObject))
    {
      paramIntent = paramIntent.getExtras();
      if (paramIntent != null)
      {
        paramIntent = paramIntent.getIntArray("appWidgetIds");
        if ((paramIntent != null) && (paramIntent.length > 0)) {
          onUpdate(paramContext, AppWidgetManager.getInstance(paramContext), paramIntent);
        }
      }
    }
    do
    {
      do
      {
        do
        {
          do
          {
            do
            {
              return;
              if (!"android.appwidget.action.APPWIDGET_DELETED".equals(localObject)) {
                break;
              }
              paramIntent = paramIntent.getExtras();
            } while ((paramIntent == null) || (!paramIntent.containsKey("appWidgetId")));
            onDeleted(paramContext, new int[] { paramIntent.getInt("appWidgetId") });
            return;
            if (!"android.appwidget.action.APPWIDGET_UPDATE_OPTIONS".equals(localObject)) {
              break;
            }
            paramIntent = paramIntent.getExtras();
          } while ((paramIntent == null) || (!paramIntent.containsKey("appWidgetId")) || (!paramIntent.containsKey("appWidgetOptions")));
          int i = paramIntent.getInt("appWidgetId");
          paramIntent = paramIntent.getBundle("appWidgetOptions");
          onAppWidgetOptionsChanged(paramContext, AppWidgetManager.getInstance(paramContext), i, paramIntent);
          return;
          if ("android.appwidget.action.APPWIDGET_ENABLED".equals(localObject))
          {
            onEnabled(paramContext);
            return;
          }
          if ("android.appwidget.action.APPWIDGET_DISABLED".equals(localObject))
          {
            onDisabled(paramContext);
            return;
          }
        } while (!"android.appwidget.action.APPWIDGET_RESTORED".equals(localObject));
        localObject = paramIntent.getExtras();
      } while (localObject == null);
      paramIntent = ((Bundle)localObject).getIntArray("appWidgetOldIds");
      localObject = ((Bundle)localObject).getIntArray("appWidgetIds");
    } while ((paramIntent == null) || (paramIntent.length <= 0));
    onRestored(paramContext, paramIntent, (int[])localObject);
    onUpdate(paramContext, AppWidgetManager.getInstance(paramContext), (int[])localObject);
  }
  
  public void onRestored(Context paramContext, int[] paramArrayOfInt1, int[] paramArrayOfInt2) {}
  
  public void onUpdate(Context paramContext, AppWidgetManager paramAppWidgetManager, int[] paramArrayOfInt) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/appwidget/AppWidgetProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */