package android.app.admin;

import android.content.Intent;
import java.util.List;

public abstract class DevicePolicyManagerInternal
{
  public abstract void addOnCrossProfileWidgetProvidersChangeListener(OnCrossProfileWidgetProvidersChangeListener paramOnCrossProfileWidgetProvidersChangeListener);
  
  public abstract Intent createPackageSuspendedDialogIntent(String paramString, int paramInt);
  
  public abstract List<String> getCrossProfileWidgetProviders(int paramInt);
  
  public abstract boolean isActiveAdminWithPolicy(int paramInt1, int paramInt2);
  
  public static abstract interface OnCrossProfileWidgetProvidersChangeListener
  {
    public abstract void onCrossProfileWidgetProvidersChanged(int paramInt, List<String> paramList);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/admin/DevicePolicyManagerInternal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */