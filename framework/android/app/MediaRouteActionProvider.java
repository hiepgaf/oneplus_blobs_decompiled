package android.app;

import android.content.Context;
import android.media.MediaRouter;
import android.media.MediaRouter.RouteInfo;
import android.media.MediaRouter.SimpleCallback;
import android.util.Log;
import android.view.ActionProvider;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public class MediaRouteActionProvider
  extends ActionProvider
{
  private static final String TAG = "MediaRouteActionProvider";
  private MediaRouteButton mButton;
  private final MediaRouterCallback mCallback;
  private final Context mContext;
  private View.OnClickListener mExtendedSettingsListener;
  private int mRouteTypes;
  private final MediaRouter mRouter;
  
  public MediaRouteActionProvider(Context paramContext)
  {
    super(paramContext);
    this.mContext = paramContext;
    this.mRouter = ((MediaRouter)paramContext.getSystemService("media_router"));
    this.mCallback = new MediaRouterCallback(this);
    setRouteTypes(1);
  }
  
  private void refreshRoute()
  {
    refreshVisibility();
  }
  
  public boolean isVisible()
  {
    return this.mRouter.isRouteAvailable(this.mRouteTypes, 1);
  }
  
  public View onCreateActionView()
  {
    throw new UnsupportedOperationException("Use onCreateActionView(MenuItem) instead.");
  }
  
  public View onCreateActionView(MenuItem paramMenuItem)
  {
    if (this.mButton != null) {
      Log.e("MediaRouteActionProvider", "onCreateActionView: this ActionProvider is already associated with a menu item. Don't reuse MediaRouteActionProvider instances! Abandoning the old one...");
    }
    this.mButton = new MediaRouteButton(this.mContext);
    this.mButton.setCheatSheetEnabled(true);
    this.mButton.setRouteTypes(this.mRouteTypes);
    this.mButton.setExtendedSettingsClickListener(this.mExtendedSettingsListener);
    this.mButton.setLayoutParams(new ViewGroup.LayoutParams(-2, -1));
    return this.mButton;
  }
  
  public boolean onPerformDefaultAction()
  {
    if (this.mButton != null) {
      return this.mButton.showDialogInternal();
    }
    return false;
  }
  
  public boolean overridesItemVisibility()
  {
    return true;
  }
  
  public void setExtendedSettingsClickListener(View.OnClickListener paramOnClickListener)
  {
    this.mExtendedSettingsListener = paramOnClickListener;
    if (this.mButton != null) {
      this.mButton.setExtendedSettingsClickListener(paramOnClickListener);
    }
  }
  
  public void setRouteTypes(int paramInt)
  {
    if (this.mRouteTypes != paramInt)
    {
      if (this.mRouteTypes != 0) {
        this.mRouter.removeCallback(this.mCallback);
      }
      this.mRouteTypes = paramInt;
      if (paramInt != 0) {
        this.mRouter.addCallback(paramInt, this.mCallback, 8);
      }
      refreshRoute();
      if (this.mButton != null) {
        this.mButton.setRouteTypes(this.mRouteTypes);
      }
    }
  }
  
  private static class MediaRouterCallback
    extends MediaRouter.SimpleCallback
  {
    private final WeakReference<MediaRouteActionProvider> mProviderWeak;
    
    public MediaRouterCallback(MediaRouteActionProvider paramMediaRouteActionProvider)
    {
      this.mProviderWeak = new WeakReference(paramMediaRouteActionProvider);
    }
    
    private void refreshRoute(MediaRouter paramMediaRouter)
    {
      MediaRouteActionProvider localMediaRouteActionProvider = (MediaRouteActionProvider)this.mProviderWeak.get();
      if (localMediaRouteActionProvider != null)
      {
        MediaRouteActionProvider.-wrap0(localMediaRouteActionProvider);
        return;
      }
      paramMediaRouter.removeCallback(this);
    }
    
    public void onRouteAdded(MediaRouter paramMediaRouter, MediaRouter.RouteInfo paramRouteInfo)
    {
      refreshRoute(paramMediaRouter);
    }
    
    public void onRouteChanged(MediaRouter paramMediaRouter, MediaRouter.RouteInfo paramRouteInfo)
    {
      refreshRoute(paramMediaRouter);
    }
    
    public void onRouteRemoved(MediaRouter paramMediaRouter, MediaRouter.RouteInfo paramRouteInfo)
    {
      refreshRoute(paramMediaRouter);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/MediaRouteActionProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */