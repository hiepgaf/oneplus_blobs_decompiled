package android.media;

import android.app.ActivityThread;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManager.DisplayListener;
import android.hardware.display.WifiDisplay;
import android.hardware.display.WifiDisplayStatus;
import android.media.session.MediaSession;
import android.os.Handler;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class MediaRouter
{
  public static final int AVAILABILITY_FLAG_IGNORE_DEFAULT_ROUTE = 1;
  public static final int CALLBACK_FLAG_PASSIVE_DISCOVERY = 8;
  public static final int CALLBACK_FLAG_PERFORM_ACTIVE_SCAN = 1;
  public static final int CALLBACK_FLAG_REQUEST_DISCOVERY = 4;
  public static final int CALLBACK_FLAG_UNFILTERED_EVENTS = 2;
  private static final boolean DEBUG;
  static final int ROUTE_TYPE_ANY = 8388615;
  public static final int ROUTE_TYPE_LIVE_AUDIO = 1;
  public static final int ROUTE_TYPE_LIVE_VIDEO = 2;
  public static final int ROUTE_TYPE_REMOTE_DISPLAY = 4;
  public static final int ROUTE_TYPE_USER = 8388608;
  private static final String TAG = "MediaRouter";
  static final HashMap<Context, MediaRouter> sRouters;
  static Static sStatic;
  
  static
  {
    if (MediaRouter.class.desiredAssertionStatus()) {}
    for (boolean bool = false;; bool = true)
    {
      -assertionsDisabled = bool;
      DEBUG = Log.isLoggable("MediaRouter", 3);
      sRouters = new HashMap();
      return;
    }
  }
  
  public MediaRouter(Context paramContext)
  {
    try
    {
      if (sStatic == null)
      {
        paramContext = paramContext.getApplicationContext();
        sStatic = new Static(paramContext);
        sStatic.startMonitoringRoutes(paramContext);
      }
      return;
    }
    finally
    {
      paramContext = finally;
      throw paramContext;
    }
  }
  
  static void addRouteStatic(RouteInfo paramRouteInfo)
  {
    Log.v("MediaRouter", "Adding route: " + paramRouteInfo);
    Object localObject = paramRouteInfo.getCategory();
    if (!sStatic.mCategories.contains(localObject)) {
      sStatic.mCategories.add(localObject);
    }
    if ((!((RouteCategory)localObject).isGroupable()) || ((paramRouteInfo instanceof RouteGroup)))
    {
      sStatic.mRoutes.add(paramRouteInfo);
      dispatchRouteAdded(paramRouteInfo);
      return;
    }
    localObject = new RouteGroup(paramRouteInfo.getCategory());
    ((RouteGroup)localObject).mSupportedTypes = paramRouteInfo.mSupportedTypes;
    sStatic.mRoutes.add(localObject);
    dispatchRouteAdded((RouteInfo)localObject);
    ((RouteGroup)localObject).addRoute(paramRouteInfo);
  }
  
  static void dispatchRouteAdded(RouteInfo paramRouteInfo)
  {
    Iterator localIterator = sStatic.mCallbacks.iterator();
    while (localIterator.hasNext())
    {
      CallbackInfo localCallbackInfo = (CallbackInfo)localIterator.next();
      if (localCallbackInfo.filterRouteEvent(paramRouteInfo)) {
        localCallbackInfo.cb.onRouteAdded(localCallbackInfo.router, paramRouteInfo);
      }
    }
  }
  
  static void dispatchRouteChanged(RouteInfo paramRouteInfo)
  {
    dispatchRouteChanged(paramRouteInfo, paramRouteInfo.mSupportedTypes);
  }
  
  static void dispatchRouteChanged(RouteInfo paramRouteInfo, int paramInt)
  {
    Log.v("MediaRouter", "Dispatching route change: " + paramRouteInfo);
    int i = paramRouteInfo.mSupportedTypes;
    Iterator localIterator = sStatic.mCallbacks.iterator();
    while (localIterator.hasNext())
    {
      CallbackInfo localCallbackInfo = (CallbackInfo)localIterator.next();
      boolean bool1 = localCallbackInfo.filterRouteEvent(paramInt);
      boolean bool2 = localCallbackInfo.filterRouteEvent(i);
      if ((!bool1) && (bool2))
      {
        localCallbackInfo.cb.onRouteAdded(localCallbackInfo.router, paramRouteInfo);
        if (paramRouteInfo.isSelected()) {
          localCallbackInfo.cb.onRouteSelected(localCallbackInfo.router, i, paramRouteInfo);
        }
      }
      if ((bool1) || (bool2)) {
        localCallbackInfo.cb.onRouteChanged(localCallbackInfo.router, paramRouteInfo);
      }
      if ((bool1) && (!bool2))
      {
        if (paramRouteInfo.isSelected()) {
          localCallbackInfo.cb.onRouteUnselected(localCallbackInfo.router, paramInt, paramRouteInfo);
        }
        localCallbackInfo.cb.onRouteRemoved(localCallbackInfo.router, paramRouteInfo);
      }
    }
  }
  
  static void dispatchRouteGrouped(RouteInfo paramRouteInfo, RouteGroup paramRouteGroup, int paramInt)
  {
    Iterator localIterator = sStatic.mCallbacks.iterator();
    while (localIterator.hasNext())
    {
      CallbackInfo localCallbackInfo = (CallbackInfo)localIterator.next();
      if (localCallbackInfo.filterRouteEvent(paramRouteGroup)) {
        localCallbackInfo.cb.onRouteGrouped(localCallbackInfo.router, paramRouteInfo, paramRouteGroup, paramInt);
      }
    }
  }
  
  static void dispatchRoutePresentationDisplayChanged(RouteInfo paramRouteInfo)
  {
    Iterator localIterator = sStatic.mCallbacks.iterator();
    while (localIterator.hasNext())
    {
      CallbackInfo localCallbackInfo = (CallbackInfo)localIterator.next();
      if (localCallbackInfo.filterRouteEvent(paramRouteInfo)) {
        localCallbackInfo.cb.onRoutePresentationDisplayChanged(localCallbackInfo.router, paramRouteInfo);
      }
    }
  }
  
  static void dispatchRouteRemoved(RouteInfo paramRouteInfo)
  {
    Iterator localIterator = sStatic.mCallbacks.iterator();
    while (localIterator.hasNext())
    {
      CallbackInfo localCallbackInfo = (CallbackInfo)localIterator.next();
      if (localCallbackInfo.filterRouteEvent(paramRouteInfo)) {
        localCallbackInfo.cb.onRouteRemoved(localCallbackInfo.router, paramRouteInfo);
      }
    }
  }
  
  static void dispatchRouteSelected(int paramInt, RouteInfo paramRouteInfo)
  {
    Iterator localIterator = sStatic.mCallbacks.iterator();
    while (localIterator.hasNext())
    {
      CallbackInfo localCallbackInfo = (CallbackInfo)localIterator.next();
      if (localCallbackInfo.filterRouteEvent(paramRouteInfo)) {
        localCallbackInfo.cb.onRouteSelected(localCallbackInfo.router, paramInt, paramRouteInfo);
      }
    }
  }
  
  static void dispatchRouteUngrouped(RouteInfo paramRouteInfo, RouteGroup paramRouteGroup)
  {
    Iterator localIterator = sStatic.mCallbacks.iterator();
    while (localIterator.hasNext())
    {
      CallbackInfo localCallbackInfo = (CallbackInfo)localIterator.next();
      if (localCallbackInfo.filterRouteEvent(paramRouteGroup)) {
        localCallbackInfo.cb.onRouteUngrouped(localCallbackInfo.router, paramRouteInfo, paramRouteGroup);
      }
    }
  }
  
  static void dispatchRouteUnselected(int paramInt, RouteInfo paramRouteInfo)
  {
    Iterator localIterator = sStatic.mCallbacks.iterator();
    while (localIterator.hasNext())
    {
      CallbackInfo localCallbackInfo = (CallbackInfo)localIterator.next();
      if (localCallbackInfo.filterRouteEvent(paramRouteInfo)) {
        localCallbackInfo.cb.onRouteUnselected(localCallbackInfo.router, paramInt, paramRouteInfo);
      }
    }
  }
  
  static void dispatchRouteVolumeChanged(RouteInfo paramRouteInfo)
  {
    Iterator localIterator = sStatic.mCallbacks.iterator();
    while (localIterator.hasNext())
    {
      CallbackInfo localCallbackInfo = (CallbackInfo)localIterator.next();
      if (localCallbackInfo.filterRouteEvent(paramRouteInfo)) {
        localCallbackInfo.cb.onRouteVolumeChanged(localCallbackInfo.router, paramRouteInfo);
      }
    }
  }
  
  private int findCallbackInfo(Callback paramCallback)
  {
    int j = sStatic.mCallbacks.size();
    int i = 0;
    while (i < j)
    {
      if (((CallbackInfo)sStatic.mCallbacks.get(i)).cb == paramCallback) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  private static WifiDisplay findWifiDisplay(WifiDisplay[] paramArrayOfWifiDisplay, String paramString)
  {
    int i = 0;
    while (i < paramArrayOfWifiDisplay.length)
    {
      WifiDisplay localWifiDisplay = paramArrayOfWifiDisplay[i];
      if (localWifiDisplay.getDeviceAddress().equals(paramString)) {
        return localWifiDisplay;
      }
      i += 1;
    }
    return null;
  }
  
  private static RouteInfo findWifiDisplayRoute(WifiDisplay paramWifiDisplay)
  {
    int j = sStatic.mRoutes.size();
    int i = 0;
    while (i < j)
    {
      RouteInfo localRouteInfo = (RouteInfo)sStatic.mRoutes.get(i);
      if (paramWifiDisplay.getDeviceAddress().equals(localRouteInfo.mDeviceAddress)) {
        return localRouteInfo;
      }
      i += 1;
    }
    return null;
  }
  
  static RouteInfo getRouteAtStatic(int paramInt)
  {
    return (RouteInfo)sStatic.mRoutes.get(paramInt);
  }
  
  static int getRouteCountStatic()
  {
    return sStatic.mRoutes.size();
  }
  
  static int getWifiDisplayStatusCode(WifiDisplay paramWifiDisplay, WifiDisplayStatus paramWifiDisplayStatus)
  {
    int i;
    if (paramWifiDisplayStatus.getScanState() == 1) {
      i = 1;
    }
    for (;;)
    {
      if (paramWifiDisplay.equals(paramWifiDisplayStatus.getActiveDisplay())) {}
      switch (paramWifiDisplayStatus.getActiveDisplayState())
      {
      default: 
        return i;
        if (paramWifiDisplay.isAvailable())
        {
          if (paramWifiDisplay.canConnect()) {
            i = 3;
          } else {
            i = 5;
          }
        }
        else {
          i = 4;
        }
        break;
      }
    }
    return 6;
    return 2;
    Log.e("MediaRouter", "Active display is not connected!");
    return i;
  }
  
  static boolean isWifiDisplayEnabled(WifiDisplay paramWifiDisplay, WifiDisplayStatus paramWifiDisplayStatus)
  {
    if (paramWifiDisplay.isAvailable())
    {
      if (!paramWifiDisplay.canConnect()) {
        return paramWifiDisplay.equals(paramWifiDisplayStatus.getActiveDisplay());
      }
      return true;
    }
    return false;
  }
  
  static RouteInfo makeWifiDisplayRoute(WifiDisplay paramWifiDisplay, WifiDisplayStatus paramWifiDisplayStatus)
  {
    RouteInfo localRouteInfo = new RouteInfo(sStatic.mSystemCategory);
    localRouteInfo.mDeviceAddress = paramWifiDisplay.getDeviceAddress();
    localRouteInfo.mSupportedTypes = 7;
    localRouteInfo.mVolumeHandling = 0;
    localRouteInfo.mPlaybackType = 1;
    localRouteInfo.setRealStatusCode(getWifiDisplayStatusCode(paramWifiDisplay, paramWifiDisplayStatus));
    localRouteInfo.mEnabled = isWifiDisplayEnabled(paramWifiDisplay, paramWifiDisplayStatus);
    localRouteInfo.mName = paramWifiDisplay.getFriendlyDisplayName();
    localRouteInfo.mDescription = sStatic.mResources.getText(17040655);
    localRouteInfo.updatePresentationDisplay();
    localRouteInfo.mDeviceType = 1;
    return localRouteInfo;
  }
  
  static boolean matchesDeviceAddress(WifiDisplay paramWifiDisplay, RouteInfo paramRouteInfo)
  {
    if ((paramRouteInfo != null) && (paramRouteInfo.mDeviceAddress != null)) {}
    for (int i = 1; (paramWifiDisplay != null) || (i != 0); i = 0)
    {
      if ((paramWifiDisplay == null) || (i == 0)) {
        break label48;
      }
      return paramWifiDisplay.getDeviceAddress().equals(paramRouteInfo.mDeviceAddress);
    }
    return true;
    label48:
    return false;
  }
  
  static void removeRouteStatic(RouteInfo paramRouteInfo)
  {
    Log.v("MediaRouter", "Removing route: " + paramRouteInfo);
    RouteCategory localRouteCategory;
    int m;
    int k;
    int i;
    if (sStatic.mRoutes.remove(paramRouteInfo))
    {
      localRouteCategory = paramRouteInfo.getCategory();
      m = sStatic.mRoutes.size();
      k = 0;
      i = 0;
    }
    for (;;)
    {
      int j = k;
      if (i < m)
      {
        if (localRouteCategory == ((RouteInfo)sStatic.mRoutes.get(i)).getCategory()) {
          j = 1;
        }
      }
      else
      {
        if (paramRouteInfo.isSelected()) {
          selectDefaultRouteStatic();
        }
        if (j == 0) {
          sStatic.mCategories.remove(localRouteCategory);
        }
        dispatchRouteRemoved(paramRouteInfo);
        return;
      }
      i += 1;
    }
  }
  
  static void selectDefaultRouteStatic()
  {
    if ((sStatic.mSelectedRoute != sStatic.mBluetoothA2dpRoute) && (sStatic.mBluetoothA2dpRoute != null) && (sStatic.isBluetoothA2dpOn()))
    {
      selectRouteStatic(8388615, sStatic.mBluetoothA2dpRoute, false);
      return;
    }
    selectRouteStatic(8388615, sStatic.mDefaultAudioVideo, false);
  }
  
  static void selectRouteStatic(int paramInt, RouteInfo paramRouteInfo, boolean paramBoolean)
  {
    boolean bool = true;
    Log.v("MediaRouter", "Selecting route: " + paramRouteInfo);
    if (!-assertionsDisabled)
    {
      if (paramRouteInfo != null) {}
      for (i = 1; i == 0; i = 0) {
        throw new AssertionError();
      }
    }
    RouteInfo localRouteInfo = sStatic.mSelectedRoute;
    if (localRouteInfo == paramRouteInfo) {
      return;
    }
    if (!paramRouteInfo.matchesTypes(paramInt))
    {
      Log.w("MediaRouter", "selectRoute ignored; cannot select route with supported types " + typesToString(paramRouteInfo.getSupportedTypes()) + " into route types " + typesToString(paramInt));
      return;
    }
    Object localObject = sStatic.mBluetoothA2dpRoute;
    if ((localObject != null) && ((paramInt & 0x1) != 0) && ((paramRouteInfo == localObject) || (paramRouteInfo == sStatic.mDefaultAudioVideo))) {}
    for (;;)
    {
      try
      {
        IAudioService localIAudioService = sStatic.mAudioService;
        if (paramRouteInfo != localObject) {
          continue;
        }
        localIAudioService.setBluetoothA2dpOn(bool);
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("MediaRouter", "Error changing Bluetooth A2DP state", localRemoteException);
        continue;
        i = 0;
        continue;
        int j = 0;
        continue;
        if (!sStatic.mCanConfigureWifiDisplays) {
          continue;
        }
        sStatic.mDisplayService.connectWifiDisplay(paramRouteInfo.mDeviceAddress);
        continue;
        Log.e("MediaRouter", "Cannot connect to wifi displays because this process is not allowed to do so.");
        continue;
        sStatic.mDisplayService.disconnectWifiDisplay();
        continue;
      }
      localObject = sStatic.mDisplayService.getWifiDisplayStatus().getActiveDisplay();
      if ((localRouteInfo == null) || (localRouteInfo.mDeviceAddress == null)) {
        continue;
      }
      i = 1;
      if (paramRouteInfo.mDeviceAddress == null) {
        continue;
      }
      j = 1;
      if ((localObject != null) || (i != 0) || (j != 0))
      {
        if ((j != 0) && (!matchesDeviceAddress((WifiDisplay)localObject, paramRouteInfo))) {
          continue;
        }
        if ((localObject != null) && (j == 0)) {
          continue;
        }
      }
      sStatic.setSelectedRoute(paramRouteInfo, paramBoolean);
      if (localRouteInfo != null)
      {
        dispatchRouteUnselected(localRouteInfo.getSupportedTypes() & paramInt, localRouteInfo);
        if (localRouteInfo.resolveStatusCode()) {
          dispatchRouteChanged(localRouteInfo);
        }
      }
      if (paramRouteInfo != null)
      {
        if (paramRouteInfo.resolveStatusCode()) {
          dispatchRouteChanged(paramRouteInfo);
        }
        dispatchRouteSelected(paramRouteInfo.getSupportedTypes() & paramInt, paramRouteInfo);
      }
      sStatic.updateDiscoveryRequest();
      return;
      bool = false;
    }
  }
  
  private static boolean shouldShowWifiDisplay(WifiDisplay paramWifiDisplay1, WifiDisplay paramWifiDisplay2)
  {
    if (!paramWifiDisplay1.isRemembered()) {
      return paramWifiDisplay1.equals(paramWifiDisplay2);
    }
    return true;
  }
  
  static void systemVolumeChanged(int paramInt)
  {
    RouteInfo localRouteInfo1 = sStatic.mSelectedRoute;
    if (localRouteInfo1 == null) {
      return;
    }
    if ((localRouteInfo1 == sStatic.mBluetoothA2dpRoute) || (localRouteInfo1 == sStatic.mDefaultAudioVideo))
    {
      dispatchRouteVolumeChanged(localRouteInfo1);
      return;
    }
    if (sStatic.mBluetoothA2dpRoute != null) {
      for (;;)
      {
        try
        {
          if (sStatic.mAudioService.isBluetoothA2dpOn())
          {
            localRouteInfo1 = sStatic.mBluetoothA2dpRoute;
            dispatchRouteVolumeChanged(localRouteInfo1);
            return;
          }
        }
        catch (RemoteException localRemoteException)
        {
          Log.e("MediaRouter", "Error checking Bluetooth A2DP state to report volume change", localRemoteException);
          return;
        }
        RouteInfo localRouteInfo2 = sStatic.mDefaultAudioVideo;
      }
    }
    dispatchRouteVolumeChanged(sStatic.mDefaultAudioVideo);
  }
  
  static String typesToString(int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if ((paramInt & 0x1) != 0) {
      localStringBuilder.append("ROUTE_TYPE_LIVE_AUDIO ");
    }
    if ((paramInt & 0x2) != 0) {
      localStringBuilder.append("ROUTE_TYPE_LIVE_VIDEO ");
    }
    if ((paramInt & 0x4) != 0) {
      localStringBuilder.append("ROUTE_TYPE_REMOTE_DISPLAY ");
    }
    if ((0x800000 & paramInt) != 0) {
      localStringBuilder.append("ROUTE_TYPE_USER ");
    }
    return localStringBuilder.toString();
  }
  
  static void updateRoute(RouteInfo paramRouteInfo)
  {
    dispatchRouteChanged(paramRouteInfo);
  }
  
  private static void updateWifiDisplayRoute(RouteInfo paramRouteInfo, WifiDisplay paramWifiDisplay, WifiDisplayStatus paramWifiDisplayStatus, boolean paramBoolean)
  {
    int i = 0;
    String str = paramWifiDisplay.getFriendlyDisplayName();
    if (!paramRouteInfo.getName().equals(str))
    {
      paramRouteInfo.mName = str;
      i = 1;
    }
    boolean bool = isWifiDisplayEnabled(paramWifiDisplay, paramWifiDisplayStatus);
    if (paramRouteInfo.mEnabled != bool) {}
    for (int j = 1;; j = 0)
    {
      paramRouteInfo.mEnabled = bool;
      if ((i | j | paramRouteInfo.setRealStatusCode(getWifiDisplayStatusCode(paramWifiDisplay, paramWifiDisplayStatus))) != 0) {
        dispatchRouteChanged(paramRouteInfo);
      }
      if (((!bool) || (paramBoolean)) && (paramRouteInfo.isSelected())) {
        selectDefaultRouteStatic();
      }
      return;
    }
  }
  
  static void updateWifiDisplayStatus(WifiDisplayStatus paramWifiDisplayStatus)
  {
    WifiDisplay[] arrayOfWifiDisplay;
    Object localObject1;
    if (paramWifiDisplayStatus.getFeatureState() == 3)
    {
      arrayOfWifiDisplay = paramWifiDisplayStatus.getDisplays();
      localObject2 = paramWifiDisplayStatus.getActiveDisplay();
      localObject1 = localObject2;
      if (!sStatic.mCanConfigureWifiDisplays)
      {
        if (localObject2 != null)
        {
          arrayOfWifiDisplay = new WifiDisplay[1];
          arrayOfWifiDisplay[0] = localObject2;
          localObject1 = localObject2;
        }
      }
      else {
        if (localObject1 == null) {
          break label167;
        }
      }
    }
    WifiDisplay localWifiDisplay;
    Object localObject3;
    label167:
    for (Object localObject2 = ((WifiDisplay)localObject1).getDeviceAddress();; localObject2 = null)
    {
      i = 0;
      for (;;)
      {
        if (i >= arrayOfWifiDisplay.length) {
          break label219;
        }
        localWifiDisplay = arrayOfWifiDisplay[i];
        if (shouldShowWifiDisplay(localWifiDisplay, (WifiDisplay)localObject1))
        {
          localObject3 = findWifiDisplayRoute(localWifiDisplay);
          if (localObject3 != null) {
            break;
          }
          localObject3 = makeWifiDisplayRoute(localWifiDisplay, paramWifiDisplayStatus);
          addRouteStatic((RouteInfo)localObject3);
          if (localWifiDisplay.equals((WifiDisplay)localObject1)) {
            selectRouteStatic(((RouteInfo)localObject3).getSupportedTypes(), (RouteInfo)localObject3, false);
          }
        }
        i += 1;
      }
      arrayOfWifiDisplay = WifiDisplay.EMPTY_ARRAY;
      localObject1 = localObject2;
      break;
      arrayOfWifiDisplay = WifiDisplay.EMPTY_ARRAY;
      localObject1 = null;
      break;
    }
    String str = localWifiDisplay.getDeviceAddress();
    if (!str.equals(localObject2)) {}
    for (boolean bool = str.equals(sStatic.mPreviousActiveWifiDisplayAddress);; bool = false)
    {
      updateWifiDisplayRoute((RouteInfo)localObject3, localWifiDisplay, paramWifiDisplayStatus, bool);
      break;
    }
    label219:
    int i = sStatic.mRoutes.size();
    int j = i - 1;
    if (i > 0)
    {
      paramWifiDisplayStatus = (RouteInfo)sStatic.mRoutes.get(j);
      if (paramWifiDisplayStatus.mDeviceAddress != null)
      {
        localObject3 = findWifiDisplay(arrayOfWifiDisplay, paramWifiDisplayStatus.mDeviceAddress);
        if ((localObject3 == null) || (!shouldShowWifiDisplay((WifiDisplay)localObject3, (WifiDisplay)localObject1))) {
          break label289;
        }
      }
      for (;;)
      {
        i = j;
        break;
        label289:
        removeRouteStatic(paramWifiDisplayStatus);
      }
    }
    sStatic.mPreviousActiveWifiDisplayAddress = ((String)localObject2);
  }
  
  public void addCallback(int paramInt, Callback paramCallback)
  {
    addCallback(paramInt, paramCallback, 0);
  }
  
  public void addCallback(int paramInt1, Callback paramCallback, int paramInt2)
  {
    int i = findCallbackInfo(paramCallback);
    if (i >= 0)
    {
      paramCallback = (CallbackInfo)sStatic.mCallbacks.get(i);
      paramCallback.type |= paramInt1;
      paramCallback.flags |= paramInt2;
    }
    for (;;)
    {
      sStatic.updateDiscoveryRequest();
      return;
      paramCallback = new CallbackInfo(paramCallback, paramInt1, paramInt2, this);
      sStatic.mCallbacks.add(paramCallback);
    }
  }
  
  public void addRouteInt(RouteInfo paramRouteInfo)
  {
    addRouteStatic(paramRouteInfo);
  }
  
  public void addUserRoute(UserRouteInfo paramUserRouteInfo)
  {
    addRouteStatic(paramUserRouteInfo);
  }
  
  public void clearUserRoutes()
  {
    int j;
    for (int i = 0; i < sStatic.mRoutes.size(); i = j + 1)
    {
      RouteInfo localRouteInfo = (RouteInfo)sStatic.mRoutes.get(i);
      if (!(localRouteInfo instanceof UserRouteInfo))
      {
        j = i;
        if (!(localRouteInfo instanceof RouteGroup)) {}
      }
      else
      {
        removeRouteStatic(localRouteInfo);
        j = i - 1;
      }
    }
  }
  
  public RouteCategory createRouteCategory(int paramInt, boolean paramBoolean)
  {
    return new RouteCategory(paramInt, 8388608, paramBoolean);
  }
  
  public RouteCategory createRouteCategory(CharSequence paramCharSequence, boolean paramBoolean)
  {
    return new RouteCategory(paramCharSequence, 8388608, paramBoolean);
  }
  
  public UserRouteInfo createUserRoute(RouteCategory paramRouteCategory)
  {
    return new UserRouteInfo(paramRouteCategory);
  }
  
  public RouteCategory getCategoryAt(int paramInt)
  {
    return (RouteCategory)sStatic.mCategories.get(paramInt);
  }
  
  public int getCategoryCount()
  {
    return sStatic.mCategories.size();
  }
  
  public RouteInfo getDefaultRoute()
  {
    return sStatic.mDefaultAudioVideo;
  }
  
  public RouteInfo getRouteAt(int paramInt)
  {
    return (RouteInfo)sStatic.mRoutes.get(paramInt);
  }
  
  public int getRouteCount()
  {
    return sStatic.mRoutes.size();
  }
  
  public RouteInfo getSelectedRoute()
  {
    return getSelectedRoute(8388615);
  }
  
  public RouteInfo getSelectedRoute(int paramInt)
  {
    if ((sStatic.mSelectedRoute != null) && ((sStatic.mSelectedRoute.mSupportedTypes & paramInt) != 0)) {
      return sStatic.mSelectedRoute;
    }
    if (paramInt == 8388608) {
      return null;
    }
    return sStatic.mDefaultAudioVideo;
  }
  
  public RouteCategory getSystemCategory()
  {
    return sStatic.mSystemCategory;
  }
  
  public boolean isRouteAvailable(int paramInt1, int paramInt2)
  {
    int j = sStatic.mRoutes.size();
    int i = 0;
    while (i < j)
    {
      RouteInfo localRouteInfo = (RouteInfo)sStatic.mRoutes.get(i);
      if ((localRouteInfo.matchesTypes(paramInt1)) && (((paramInt2 & 0x1) == 0) || (localRouteInfo != sStatic.mDefaultAudioVideo))) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public void rebindAsUser(int paramInt)
  {
    sStatic.rebindAsUser(paramInt);
  }
  
  public void removeCallback(Callback paramCallback)
  {
    int i = findCallbackInfo(paramCallback);
    if (i >= 0)
    {
      sStatic.mCallbacks.remove(i);
      sStatic.updateDiscoveryRequest();
      return;
    }
    Log.w("MediaRouter", "removeCallback(" + paramCallback + "): callback not registered");
  }
  
  public void removeRouteInt(RouteInfo paramRouteInfo)
  {
    removeRouteStatic(paramRouteInfo);
  }
  
  public void removeUserRoute(UserRouteInfo paramUserRouteInfo)
  {
    removeRouteStatic(paramUserRouteInfo);
  }
  
  public void selectRoute(int paramInt, RouteInfo paramRouteInfo)
  {
    if (paramRouteInfo == null) {
      throw new IllegalArgumentException("Route cannot be null.");
    }
    selectRouteStatic(paramInt, paramRouteInfo, true);
  }
  
  public void selectRouteInt(int paramInt, RouteInfo paramRouteInfo, boolean paramBoolean)
  {
    selectRouteStatic(paramInt, paramRouteInfo, paramBoolean);
  }
  
  public static abstract class Callback
  {
    public abstract void onRouteAdded(MediaRouter paramMediaRouter, MediaRouter.RouteInfo paramRouteInfo);
    
    public abstract void onRouteChanged(MediaRouter paramMediaRouter, MediaRouter.RouteInfo paramRouteInfo);
    
    public abstract void onRouteGrouped(MediaRouter paramMediaRouter, MediaRouter.RouteInfo paramRouteInfo, MediaRouter.RouteGroup paramRouteGroup, int paramInt);
    
    public void onRoutePresentationDisplayChanged(MediaRouter paramMediaRouter, MediaRouter.RouteInfo paramRouteInfo) {}
    
    public abstract void onRouteRemoved(MediaRouter paramMediaRouter, MediaRouter.RouteInfo paramRouteInfo);
    
    public abstract void onRouteSelected(MediaRouter paramMediaRouter, int paramInt, MediaRouter.RouteInfo paramRouteInfo);
    
    public abstract void onRouteUngrouped(MediaRouter paramMediaRouter, MediaRouter.RouteInfo paramRouteInfo, MediaRouter.RouteGroup paramRouteGroup);
    
    public abstract void onRouteUnselected(MediaRouter paramMediaRouter, int paramInt, MediaRouter.RouteInfo paramRouteInfo);
    
    public abstract void onRouteVolumeChanged(MediaRouter paramMediaRouter, MediaRouter.RouteInfo paramRouteInfo);
  }
  
  static class CallbackInfo
  {
    public final MediaRouter.Callback cb;
    public int flags;
    public final MediaRouter router;
    public int type;
    
    public CallbackInfo(MediaRouter.Callback paramCallback, int paramInt1, int paramInt2, MediaRouter paramMediaRouter)
    {
      this.cb = paramCallback;
      this.type = paramInt1;
      this.flags = paramInt2;
      this.router = paramMediaRouter;
    }
    
    public boolean filterRouteEvent(int paramInt)
    {
      return ((this.flags & 0x2) != 0) || ((this.type & paramInt) != 0);
    }
    
    public boolean filterRouteEvent(MediaRouter.RouteInfo paramRouteInfo)
    {
      return filterRouteEvent(paramRouteInfo.mSupportedTypes);
    }
  }
  
  public static class RouteCategory
  {
    final boolean mGroupable;
    boolean mIsSystem;
    CharSequence mName;
    int mNameResId;
    int mTypes;
    
    RouteCategory(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      this.mNameResId = paramInt1;
      this.mTypes = paramInt2;
      this.mGroupable = paramBoolean;
    }
    
    RouteCategory(CharSequence paramCharSequence, int paramInt, boolean paramBoolean)
    {
      this.mName = paramCharSequence;
      this.mTypes = paramInt;
      this.mGroupable = paramBoolean;
    }
    
    public CharSequence getName()
    {
      return getName(MediaRouter.sStatic.mResources);
    }
    
    public CharSequence getName(Context paramContext)
    {
      return getName(paramContext.getResources());
    }
    
    CharSequence getName(Resources paramResources)
    {
      if (this.mNameResId != 0) {
        return paramResources.getText(this.mNameResId);
      }
      return this.mName;
    }
    
    public List<MediaRouter.RouteInfo> getRoutes(List<MediaRouter.RouteInfo> paramList)
    {
      if (paramList == null) {
        paramList = new ArrayList();
      }
      for (;;)
      {
        int j = MediaRouter.getRouteCountStatic();
        int i = 0;
        while (i < j)
        {
          MediaRouter.RouteInfo localRouteInfo = MediaRouter.getRouteAtStatic(i);
          if (localRouteInfo.mCategory == this) {
            paramList.add(localRouteInfo);
          }
          i += 1;
        }
        paramList.clear();
      }
      return paramList;
    }
    
    public int getSupportedTypes()
    {
      return this.mTypes;
    }
    
    public boolean isGroupable()
    {
      return this.mGroupable;
    }
    
    public boolean isSystem()
    {
      return this.mIsSystem;
    }
    
    public String toString()
    {
      return "RouteCategory{ name=" + this.mName + " types=" + MediaRouter.typesToString(this.mTypes) + " groupable=" + this.mGroupable + " }";
    }
  }
  
  public static class RouteGroup
    extends MediaRouter.RouteInfo
  {
    final ArrayList<MediaRouter.RouteInfo> mRoutes = new ArrayList();
    private boolean mUpdateName;
    
    RouteGroup(MediaRouter.RouteCategory paramRouteCategory)
    {
      super();
      this.mGroup = this;
      this.mVolumeHandling = 0;
    }
    
    public void addRoute(MediaRouter.RouteInfo paramRouteInfo)
    {
      if (paramRouteInfo.getGroup() != null) {
        throw new IllegalStateException("Route " + paramRouteInfo + " is already part of a group.");
      }
      if (paramRouteInfo.getCategory() != this.mCategory) {
        throw new IllegalArgumentException("Route cannot be added to a group with a different category. (Route category=" + paramRouteInfo.getCategory() + " group category=" + this.mCategory + ")");
      }
      int i = this.mRoutes.size();
      this.mRoutes.add(paramRouteInfo);
      paramRouteInfo.mGroup = this;
      this.mUpdateName = true;
      updateVolume();
      routeUpdated();
      MediaRouter.dispatchRouteGrouped(paramRouteInfo, this, i);
    }
    
    public void addRoute(MediaRouter.RouteInfo paramRouteInfo, int paramInt)
    {
      if (paramRouteInfo.getGroup() != null) {
        throw new IllegalStateException("Route " + paramRouteInfo + " is already part of a group.");
      }
      if (paramRouteInfo.getCategory() != this.mCategory) {
        throw new IllegalArgumentException("Route cannot be added to a group with a different category. (Route category=" + paramRouteInfo.getCategory() + " group category=" + this.mCategory + ")");
      }
      this.mRoutes.add(paramInt, paramRouteInfo);
      paramRouteInfo.mGroup = this;
      this.mUpdateName = true;
      updateVolume();
      routeUpdated();
      MediaRouter.dispatchRouteGrouped(paramRouteInfo, this, paramInt);
    }
    
    CharSequence getName(Resources paramResources)
    {
      if (this.mUpdateName) {
        updateName();
      }
      return super.getName(paramResources);
    }
    
    public MediaRouter.RouteInfo getRouteAt(int paramInt)
    {
      return (MediaRouter.RouteInfo)this.mRoutes.get(paramInt);
    }
    
    public int getRouteCount()
    {
      return this.mRoutes.size();
    }
    
    void memberNameChanged(MediaRouter.RouteInfo paramRouteInfo, CharSequence paramCharSequence)
    {
      this.mUpdateName = true;
      routeUpdated();
    }
    
    void memberStatusChanged(MediaRouter.RouteInfo paramRouteInfo, CharSequence paramCharSequence)
    {
      setStatusInt(paramCharSequence);
    }
    
    void memberVolumeChanged(MediaRouter.RouteInfo paramRouteInfo)
    {
      updateVolume();
    }
    
    public void removeRoute(int paramInt)
    {
      MediaRouter.RouteInfo localRouteInfo = (MediaRouter.RouteInfo)this.mRoutes.remove(paramInt);
      localRouteInfo.mGroup = null;
      this.mUpdateName = true;
      updateVolume();
      MediaRouter.dispatchRouteUngrouped(localRouteInfo, this);
      routeUpdated();
    }
    
    public void removeRoute(MediaRouter.RouteInfo paramRouteInfo)
    {
      if (paramRouteInfo.getGroup() != this) {
        throw new IllegalArgumentException("Route " + paramRouteInfo + " is not a member of this group.");
      }
      this.mRoutes.remove(paramRouteInfo);
      paramRouteInfo.mGroup = null;
      this.mUpdateName = true;
      updateVolume();
      MediaRouter.dispatchRouteUngrouped(paramRouteInfo, this);
      routeUpdated();
    }
    
    public void requestSetVolume(int paramInt)
    {
      int i = getVolumeMax();
      if (i == 0) {
        return;
      }
      float f = paramInt / i;
      int j = getRouteCount();
      i = 0;
      while (i < j)
      {
        MediaRouter.RouteInfo localRouteInfo = getRouteAt(i);
        localRouteInfo.requestSetVolume((int)(localRouteInfo.getVolumeMax() * f));
        i += 1;
      }
      if (paramInt != this.mVolume)
      {
        this.mVolume = paramInt;
        MediaRouter.dispatchRouteVolumeChanged(this);
      }
    }
    
    public void requestUpdateVolume(int paramInt)
    {
      if (getVolumeMax() == 0) {
        return;
      }
      int n = getRouteCount();
      int j = 0;
      int i = 0;
      while (i < n)
      {
        MediaRouter.RouteInfo localRouteInfo = getRouteAt(i);
        localRouteInfo.requestUpdateVolume(paramInt);
        int m = localRouteInfo.getVolume();
        int k = j;
        if (m > j) {
          k = m;
        }
        i += 1;
        j = k;
      }
      if (j != this.mVolume)
      {
        this.mVolume = j;
        MediaRouter.dispatchRouteVolumeChanged(this);
      }
    }
    
    void routeUpdated()
    {
      int m = 0;
      int i3 = this.mRoutes.size();
      if (i3 == 0)
      {
        MediaRouter.removeRouteStatic(this);
        return;
      }
      int k = 0;
      int n = 1;
      int i = 1;
      int j = 0;
      if (j < i3)
      {
        localObject = (MediaRouter.RouteInfo)this.mRoutes.get(j);
        int i1 = m | ((MediaRouter.RouteInfo)localObject).mSupportedTypes;
        int i2 = ((MediaRouter.RouteInfo)localObject).getVolumeMax();
        m = k;
        if (i2 > k) {
          m = i2;
        }
        if (((MediaRouter.RouteInfo)localObject).getPlaybackType() == 0)
        {
          k = 1;
          label90:
          n &= k;
          if (((MediaRouter.RouteInfo)localObject).getVolumeHandling() != 0) {
            break label129;
          }
        }
        label129:
        for (k = 1;; k = 0)
        {
          i &= k;
          j += 1;
          k = m;
          m = i1;
          break;
          k = 0;
          break label90;
        }
      }
      if (n != 0)
      {
        j = 0;
        this.mPlaybackType = j;
        if (i == 0) {
          break label206;
        }
        i = 0;
        label152:
        this.mVolumeHandling = i;
        this.mSupportedTypes = m;
        this.mVolumeMax = k;
        if (i3 != 1) {
          break label211;
        }
      }
      label206:
      label211:
      for (Object localObject = ((MediaRouter.RouteInfo)this.mRoutes.get(0)).getIconDrawable();; localObject = null)
      {
        this.mIcon = ((Drawable)localObject);
        super.routeUpdated();
        return;
        j = 1;
        break;
        i = 1;
        break label152;
      }
    }
    
    public void setIconDrawable(Drawable paramDrawable)
    {
      this.mIcon = paramDrawable;
    }
    
    public void setIconResource(int paramInt)
    {
      setIconDrawable(MediaRouter.sStatic.mResources.getDrawable(paramInt));
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder(super.toString());
      localStringBuilder.append('[');
      int j = this.mRoutes.size();
      int i = 0;
      while (i < j)
      {
        if (i > 0) {
          localStringBuilder.append(", ");
        }
        localStringBuilder.append(this.mRoutes.get(i));
        i += 1;
      }
      localStringBuilder.append(']');
      return localStringBuilder.toString();
    }
    
    void updateName()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      int j = this.mRoutes.size();
      int i = 0;
      while (i < j)
      {
        MediaRouter.RouteInfo localRouteInfo = (MediaRouter.RouteInfo)this.mRoutes.get(i);
        if (i > 0) {
          localStringBuilder.append(", ");
        }
        localStringBuilder.append(localRouteInfo.mName);
        i += 1;
      }
      this.mName = localStringBuilder.toString();
      this.mUpdateName = false;
    }
    
    void updateVolume()
    {
      int n = getRouteCount();
      int j = 0;
      int i = 0;
      while (i < n)
      {
        int m = getRouteAt(i).getVolume();
        int k = j;
        if (m > j) {
          k = m;
        }
        i += 1;
        j = k;
      }
      if (j != this.mVolume)
      {
        this.mVolume = j;
        MediaRouter.dispatchRouteVolumeChanged(this);
      }
    }
  }
  
  public static class RouteInfo
  {
    public static final int DEVICE_TYPE_BLUETOOTH = 3;
    public static final int DEVICE_TYPE_SPEAKER = 2;
    public static final int DEVICE_TYPE_TV = 1;
    public static final int DEVICE_TYPE_UNKNOWN = 0;
    public static final int PLAYBACK_TYPE_LOCAL = 0;
    public static final int PLAYBACK_TYPE_REMOTE = 1;
    public static final int PLAYBACK_VOLUME_FIXED = 0;
    public static final int PLAYBACK_VOLUME_VARIABLE = 1;
    public static final int STATUS_AVAILABLE = 3;
    public static final int STATUS_CONNECTED = 6;
    public static final int STATUS_CONNECTING = 2;
    public static final int STATUS_IN_USE = 5;
    public static final int STATUS_NONE = 0;
    public static final int STATUS_NOT_AVAILABLE = 4;
    public static final int STATUS_SCANNING = 1;
    final MediaRouter.RouteCategory mCategory;
    CharSequence mDescription;
    String mDeviceAddress;
    int mDeviceType;
    boolean mEnabled = true;
    String mGlobalRouteId;
    MediaRouter.RouteGroup mGroup;
    Drawable mIcon;
    CharSequence mName;
    int mNameResId;
    int mPlaybackStream = 3;
    int mPlaybackType = 0;
    Display mPresentationDisplay;
    int mPresentationDisplayId = -1;
    private int mRealStatusCode;
    final IRemoteVolumeObserver.Stub mRemoteVolObserver = new IRemoteVolumeObserver.Stub()
    {
      public void dispatchRemoteVolumeUpdate(final int paramAnonymousInt1, final int paramAnonymousInt2)
      {
        MediaRouter.sStatic.mHandler.post(new Runnable()
        {
          public void run()
          {
            if (MediaRouter.RouteInfo.this.mVcb != null)
            {
              if (paramAnonymousInt1 != 0) {
                MediaRouter.RouteInfo.this.mVcb.vcb.onVolumeUpdateRequest(MediaRouter.RouteInfo.this.mVcb.route, paramAnonymousInt1);
              }
            }
            else {
              return;
            }
            MediaRouter.RouteInfo.this.mVcb.vcb.onVolumeSetRequest(MediaRouter.RouteInfo.this.mVcb.route, paramAnonymousInt2);
          }
        });
      }
    };
    private int mResolvedStatusCode;
    private CharSequence mStatus;
    int mSupportedTypes;
    private Object mTag;
    MediaRouter.VolumeCallbackInfo mVcb;
    int mVolume = 15;
    int mVolumeHandling = 1;
    int mVolumeMax = 15;
    
    RouteInfo(MediaRouter.RouteCategory paramRouteCategory)
    {
      this.mCategory = paramRouteCategory;
      this.mDeviceType = 0;
    }
    
    private Display choosePresentationDisplay()
    {
      int j = 0;
      int i = 0;
      if ((this.mSupportedTypes & 0x2) != 0)
      {
        Display[] arrayOfDisplay = MediaRouter.sStatic.getAllPresentationDisplays();
        Display localDisplay;
        if (this.mPresentationDisplayId >= 0)
        {
          j = arrayOfDisplay.length;
          while (i < j)
          {
            localDisplay = arrayOfDisplay[i];
            if (localDisplay.getDisplayId() == this.mPresentationDisplayId) {
              return localDisplay;
            }
            i += 1;
          }
          return null;
        }
        if (this.mDeviceAddress != null)
        {
          int k = arrayOfDisplay.length;
          i = j;
          while (i < k)
          {
            localDisplay = arrayOfDisplay[i];
            if ((localDisplay.getType() == 3) && (this.mDeviceAddress.equals(localDisplay.getAddress()))) {
              return localDisplay;
            }
            i += 1;
          }
          return null;
        }
        if ((this == MediaRouter.sStatic.mDefaultAudioVideo) && (arrayOfDisplay.length > 0)) {
          return arrayOfDisplay[0];
        }
      }
      return null;
    }
    
    public MediaRouter.RouteCategory getCategory()
    {
      return this.mCategory;
    }
    
    public CharSequence getDescription()
    {
      return this.mDescription;
    }
    
    public String getDeviceAddress()
    {
      return this.mDeviceAddress;
    }
    
    public int getDeviceType()
    {
      return this.mDeviceType;
    }
    
    public MediaRouter.RouteGroup getGroup()
    {
      return this.mGroup;
    }
    
    public Drawable getIconDrawable()
    {
      return this.mIcon;
    }
    
    public CharSequence getName()
    {
      return getName(MediaRouter.sStatic.mResources);
    }
    
    public CharSequence getName(Context paramContext)
    {
      return getName(paramContext.getResources());
    }
    
    CharSequence getName(Resources paramResources)
    {
      if (this.mNameResId != 0)
      {
        paramResources = paramResources.getText(this.mNameResId);
        this.mName = paramResources;
        return paramResources;
      }
      return this.mName;
    }
    
    public int getPlaybackStream()
    {
      return this.mPlaybackStream;
    }
    
    public int getPlaybackType()
    {
      return this.mPlaybackType;
    }
    
    public Display getPresentationDisplay()
    {
      return this.mPresentationDisplay;
    }
    
    public CharSequence getStatus()
    {
      return this.mStatus;
    }
    
    public int getStatusCode()
    {
      return this.mResolvedStatusCode;
    }
    
    public int getSupportedTypes()
    {
      return this.mSupportedTypes;
    }
    
    public Object getTag()
    {
      return this.mTag;
    }
    
    public int getVolume()
    {
      if (this.mPlaybackType == 0) {
        try
        {
          int i = MediaRouter.sStatic.mAudioService.getStreamVolume(this.mPlaybackStream);
          return i;
        }
        catch (RemoteException localRemoteException)
        {
          Log.e("MediaRouter", "Error getting local stream volume", localRemoteException);
          return 0;
        }
      }
      return this.mVolume;
    }
    
    public int getVolumeHandling()
    {
      return this.mVolumeHandling;
    }
    
    public int getVolumeMax()
    {
      if (this.mPlaybackType == 0) {
        try
        {
          int i = MediaRouter.sStatic.mAudioService.getStreamMaxVolume(this.mPlaybackStream);
          return i;
        }
        catch (RemoteException localRemoteException)
        {
          Log.e("MediaRouter", "Error getting local stream volume", localRemoteException);
          return 0;
        }
      }
      return this.mVolumeMax;
    }
    
    public boolean isConnecting()
    {
      return this.mResolvedStatusCode == 2;
    }
    
    public boolean isDefault()
    {
      return this == MediaRouter.sStatic.mDefaultAudioVideo;
    }
    
    public boolean isEnabled()
    {
      return this.mEnabled;
    }
    
    public boolean isSelected()
    {
      return this == MediaRouter.sStatic.mSelectedRoute;
    }
    
    public boolean matchesTypes(int paramInt)
    {
      boolean bool = false;
      if ((this.mSupportedTypes & paramInt) != 0) {
        bool = true;
      }
      return bool;
    }
    
    public void requestSetVolume(int paramInt)
    {
      if (this.mPlaybackType == 0) {
        try
        {
          MediaRouter.sStatic.mAudioService.setStreamVolume(this.mPlaybackStream, paramInt, 0, ActivityThread.currentPackageName());
          return;
        }
        catch (RemoteException localRemoteException)
        {
          Log.e("MediaRouter", "Error setting local stream volume", localRemoteException);
          return;
        }
      }
      MediaRouter.sStatic.requestSetVolume(this, paramInt);
    }
    
    public void requestUpdateVolume(int paramInt)
    {
      if (this.mPlaybackType == 0) {
        try
        {
          paramInt = Math.max(0, Math.min(getVolume() + paramInt, getVolumeMax()));
          MediaRouter.sStatic.mAudioService.setStreamVolume(this.mPlaybackStream, paramInt, 0, ActivityThread.currentPackageName());
          return;
        }
        catch (RemoteException localRemoteException)
        {
          Log.e("MediaRouter", "Error setting local stream volume", localRemoteException);
          return;
        }
      }
      MediaRouter.sStatic.requestUpdateVolume(this, paramInt);
    }
    
    boolean resolveStatusCode()
    {
      int j = this.mRealStatusCode;
      int i = j;
      if (isSelected())
      {
        i = j;
        switch (j)
        {
        }
      }
      for (i = j; this.mResolvedStatusCode == i; i = 2) {
        return false;
      }
      this.mResolvedStatusCode = i;
      switch (i)
      {
      default: 
        i = 0;
        if (i == 0) {
          break;
        }
      }
      for (CharSequence localCharSequence = MediaRouter.sStatic.mResources.getText(i);; localCharSequence = null)
      {
        this.mStatus = localCharSequence;
        return true;
        i = 17040662;
        break;
        i = 17040663;
        break;
        i = 17040664;
        break;
        i = 17040665;
        break;
        i = 17040666;
        break;
      }
    }
    
    void routeUpdated()
    {
      MediaRouter.updateRoute(this);
    }
    
    public void select()
    {
      MediaRouter.selectRouteStatic(this.mSupportedTypes, this, true);
    }
    
    boolean setRealStatusCode(int paramInt)
    {
      if (this.mRealStatusCode != paramInt)
      {
        this.mRealStatusCode = paramInt;
        return resolveStatusCode();
      }
      return false;
    }
    
    void setStatusInt(CharSequence paramCharSequence)
    {
      if (!paramCharSequence.equals(this.mStatus))
      {
        this.mStatus = paramCharSequence;
        if (this.mGroup != null) {
          this.mGroup.memberStatusChanged(this, paramCharSequence);
        }
        routeUpdated();
      }
    }
    
    public void setTag(Object paramObject)
    {
      this.mTag = paramObject;
      routeUpdated();
    }
    
    public String toString()
    {
      String str = MediaRouter.typesToString(getSupportedTypes());
      return getClass().getSimpleName() + "{ name=" + getName() + ", description=" + getDescription() + ", status=" + getStatus() + ", category=" + getCategory() + ", supportedTypes=" + str + ", presentationDisplay=" + this.mPresentationDisplay + " }";
    }
    
    boolean updatePresentationDisplay()
    {
      Display localDisplay = choosePresentationDisplay();
      if (this.mPresentationDisplay != localDisplay)
      {
        this.mPresentationDisplay = localDisplay;
        return true;
      }
      return false;
    }
  }
  
  public static class SimpleCallback
    extends MediaRouter.Callback
  {
    public void onRouteAdded(MediaRouter paramMediaRouter, MediaRouter.RouteInfo paramRouteInfo) {}
    
    public void onRouteChanged(MediaRouter paramMediaRouter, MediaRouter.RouteInfo paramRouteInfo) {}
    
    public void onRouteGrouped(MediaRouter paramMediaRouter, MediaRouter.RouteInfo paramRouteInfo, MediaRouter.RouteGroup paramRouteGroup, int paramInt) {}
    
    public void onRouteRemoved(MediaRouter paramMediaRouter, MediaRouter.RouteInfo paramRouteInfo) {}
    
    public void onRouteSelected(MediaRouter paramMediaRouter, int paramInt, MediaRouter.RouteInfo paramRouteInfo) {}
    
    public void onRouteUngrouped(MediaRouter paramMediaRouter, MediaRouter.RouteInfo paramRouteInfo, MediaRouter.RouteGroup paramRouteGroup) {}
    
    public void onRouteUnselected(MediaRouter paramMediaRouter, int paramInt, MediaRouter.RouteInfo paramRouteInfo) {}
    
    public void onRouteVolumeChanged(MediaRouter paramMediaRouter, MediaRouter.RouteInfo paramRouteInfo) {}
  }
  
  static class Static
    implements DisplayManager.DisplayListener
  {
    boolean mActivelyScanningWifiDisplays;
    final Context mAppContext;
    final IAudioRoutesObserver.Stub mAudioRoutesObserver = new IAudioRoutesObserver.Stub()
    {
      public void dispatchAudioRoutesChanged(final AudioRoutesInfo paramAnonymousAudioRoutesInfo)
      {
        MediaRouter.Static.this.mHandler.post(new Runnable()
        {
          public void run()
          {
            MediaRouter.Static.this.updateAudioRoutes(paramAnonymousAudioRoutesInfo);
          }
        });
      }
    };
    final IAudioService mAudioService;
    MediaRouter.RouteInfo mBluetoothA2dpRoute;
    final CopyOnWriteArrayList<MediaRouter.CallbackInfo> mCallbacks = new CopyOnWriteArrayList();
    final boolean mCanConfigureWifiDisplays;
    final ArrayList<MediaRouter.RouteCategory> mCategories = new ArrayList();
    IMediaRouterClient mClient;
    MediaRouterClientState mClientState;
    final AudioRoutesInfo mCurAudioRoutesInfo = new AudioRoutesInfo();
    int mCurrentUserId = -1;
    MediaRouter.RouteInfo mDefaultAudioVideo;
    boolean mDiscoverRequestActiveScan;
    int mDiscoveryRequestRouteTypes;
    final DisplayManager mDisplayService;
    final Handler mHandler;
    final IMediaRouterService mMediaRouterService;
    String mPreviousActiveWifiDisplayAddress;
    final Resources mResources;
    final ArrayList<MediaRouter.RouteInfo> mRoutes = new ArrayList();
    MediaRouter.RouteInfo mSelectedRoute;
    final MediaRouter.RouteCategory mSystemCategory;
    
    Static(Context paramContext)
    {
      this.mAppContext = paramContext;
      this.mResources = Resources.getSystem();
      this.mHandler = new Handler(paramContext.getMainLooper());
      this.mAudioService = IAudioService.Stub.asInterface(ServiceManager.getService("audio"));
      this.mDisplayService = ((DisplayManager)paramContext.getSystemService("display"));
      this.mMediaRouterService = IMediaRouterService.Stub.asInterface(ServiceManager.getService("media_router"));
      this.mSystemCategory = new MediaRouter.RouteCategory(17040653, 3, false);
      this.mSystemCategory.mIsSystem = true;
      if (paramContext.checkPermission("android.permission.CONFIGURE_WIFI_DISPLAY", Process.myPid(), Process.myUid()) == 0) {}
      for (boolean bool = true;; bool = false)
      {
        this.mCanConfigureWifiDisplays = bool;
        return;
      }
    }
    
    private void updatePresentationDisplays(int paramInt)
    {
      int j = this.mRoutes.size();
      int i = 0;
      while (i < j)
      {
        MediaRouter.RouteInfo localRouteInfo = (MediaRouter.RouteInfo)this.mRoutes.get(i);
        if ((localRouteInfo.updatePresentationDisplay()) || ((localRouteInfo.mPresentationDisplay != null) && (localRouteInfo.mPresentationDisplay.getDisplayId() == paramInt))) {
          MediaRouter.dispatchRoutePresentationDisplayChanged(localRouteInfo);
        }
        i += 1;
      }
    }
    
    MediaRouter.RouteInfo findGlobalRoute(String paramString)
    {
      int j = this.mRoutes.size();
      int i = 0;
      while (i < j)
      {
        MediaRouter.RouteInfo localRouteInfo = (MediaRouter.RouteInfo)this.mRoutes.get(i);
        if (paramString.equals(localRouteInfo.mGlobalRouteId)) {
          return localRouteInfo;
        }
        i += 1;
      }
      return null;
    }
    
    public Display[] getAllPresentationDisplays()
    {
      return this.mDisplayService.getDisplays("android.hardware.display.category.PRESENTATION");
    }
    
    boolean isASBluetoothA2dpOn()
    {
      try
      {
        boolean bool = this.mAudioService.isASBluetoothA2dpOn();
        return bool;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("MediaRouter", "Error querying isASBluetoothA2dpOn A2DP state", localRemoteException);
      }
      return false;
    }
    
    boolean isBluetoothA2dpOn()
    {
      try
      {
        boolean bool = this.mAudioService.isBluetoothA2dpOn();
        return bool;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("MediaRouter", "Error querying Bluetooth A2DP state", localRemoteException);
      }
      return false;
    }
    
    MediaRouter.RouteInfo makeGlobalRoute(MediaRouterClientState.RouteInfo paramRouteInfo)
    {
      MediaRouter.RouteInfo localRouteInfo = new MediaRouter.RouteInfo(MediaRouter.sStatic.mSystemCategory);
      localRouteInfo.mGlobalRouteId = paramRouteInfo.id;
      localRouteInfo.mName = paramRouteInfo.name;
      localRouteInfo.mDescription = paramRouteInfo.description;
      localRouteInfo.mSupportedTypes = paramRouteInfo.supportedTypes;
      localRouteInfo.mDeviceType = paramRouteInfo.deviceType;
      localRouteInfo.mEnabled = paramRouteInfo.enabled;
      localRouteInfo.setRealStatusCode(paramRouteInfo.statusCode);
      localRouteInfo.mPlaybackType = paramRouteInfo.playbackType;
      localRouteInfo.mPlaybackStream = paramRouteInfo.playbackStream;
      localRouteInfo.mVolume = paramRouteInfo.volume;
      localRouteInfo.mVolumeMax = paramRouteInfo.volumeMax;
      localRouteInfo.mVolumeHandling = paramRouteInfo.volumeHandling;
      localRouteInfo.mPresentationDisplayId = paramRouteInfo.presentationDisplayId;
      localRouteInfo.updatePresentationDisplay();
      return localRouteInfo;
    }
    
    public void onDisplayAdded(int paramInt)
    {
      updatePresentationDisplays(paramInt);
    }
    
    public void onDisplayChanged(int paramInt)
    {
      updatePresentationDisplays(paramInt);
    }
    
    public void onDisplayRemoved(int paramInt)
    {
      updatePresentationDisplays(paramInt);
    }
    
    void publishClientDiscoveryRequest()
    {
      if (this.mClient != null) {}
      try
      {
        this.mMediaRouterService.setDiscoveryRequest(this.mClient, this.mDiscoveryRequestRouteTypes, this.mDiscoverRequestActiveScan);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("MediaRouter", "Unable to publish media router client discovery request.", localRemoteException);
      }
    }
    
    void publishClientSelectedRoute(boolean paramBoolean)
    {
      String str = null;
      if (this.mClient != null) {}
      try
      {
        IMediaRouterService localIMediaRouterService = this.mMediaRouterService;
        IMediaRouterClient localIMediaRouterClient = this.mClient;
        if (this.mSelectedRoute != null) {
          str = this.mSelectedRoute.mGlobalRouteId;
        }
        localIMediaRouterService.setSelectedRoute(localIMediaRouterClient, str, paramBoolean);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("MediaRouter", "Unable to publish media router client selected route.", localRemoteException);
      }
    }
    
    void rebindAsUser(int paramInt)
    {
      for (;;)
      {
        if (((this.mCurrentUserId == paramInt) && (paramInt >= 0)) || (this.mClient != null)) {}
        try
        {
          this.mMediaRouterService.unregisterClient(this.mClient);
          this.mClient = null;
          this.mCurrentUserId = paramInt;
        }
        catch (RemoteException localRemoteException1)
        {
          try
          {
            Client localClient = new Client();
            this.mMediaRouterService.registerClientAsUser(localClient, this.mAppContext.getPackageName(), paramInt);
            this.mClient = localClient;
            publishClientDiscoveryRequest();
            publishClientSelectedRoute(false);
            updateClientState();
            do
            {
              return;
            } while (this.mClient != null);
            continue;
            localRemoteException1 = localRemoteException1;
            Log.e("MediaRouter", "Unable to unregister media router client.", localRemoteException1);
          }
          catch (RemoteException localRemoteException2)
          {
            for (;;)
            {
              Log.e("MediaRouter", "Unable to register media router client.", localRemoteException2);
            }
          }
        }
      }
    }
    
    void requestSetVolume(MediaRouter.RouteInfo paramRouteInfo, int paramInt)
    {
      if ((paramRouteInfo.mGlobalRouteId != null) && (this.mClient != null)) {}
      try
      {
        this.mMediaRouterService.requestSetVolume(this.mClient, paramRouteInfo.mGlobalRouteId, paramInt);
        return;
      }
      catch (RemoteException paramRouteInfo)
      {
        Log.w("MediaRouter", "Unable to request volume change.", paramRouteInfo);
      }
    }
    
    void requestUpdateVolume(MediaRouter.RouteInfo paramRouteInfo, int paramInt)
    {
      if ((paramRouteInfo.mGlobalRouteId != null) && (this.mClient != null)) {}
      try
      {
        this.mMediaRouterService.requestUpdateVolume(this.mClient, paramRouteInfo.mGlobalRouteId, paramInt);
        return;
      }
      catch (RemoteException paramRouteInfo)
      {
        Log.w("MediaRouter", "Unable to request volume change.", paramRouteInfo);
      }
    }
    
    void setSelectedRoute(MediaRouter.RouteInfo paramRouteInfo, boolean paramBoolean)
    {
      this.mSelectedRoute = paramRouteInfo;
      publishClientSelectedRoute(paramBoolean);
    }
    
    void startMonitoringRoutes(Context paramContext)
    {
      this.mDefaultAudioVideo = new MediaRouter.RouteInfo(this.mSystemCategory);
      this.mDefaultAudioVideo.mNameResId = 17040649;
      this.mDefaultAudioVideo.mSupportedTypes = 3;
      this.mDefaultAudioVideo.updatePresentationDisplay();
      MediaRouter.addRouteStatic(this.mDefaultAudioVideo);
      MediaRouter.updateWifiDisplayStatus(this.mDisplayService.getWifiDisplayStatus());
      paramContext.registerReceiver(new MediaRouter.WifiDisplayStatusChangedReceiver(), new IntentFilter("android.hardware.display.action.WIFI_DISPLAY_STATUS_CHANGED"));
      paramContext.registerReceiver(new MediaRouter.VolumeChangeReceiver(), new IntentFilter("android.media.VOLUME_CHANGED_ACTION"));
      this.mDisplayService.registerDisplayListener(this, this.mHandler);
      paramContext = null;
      try
      {
        AudioRoutesInfo localAudioRoutesInfo = this.mAudioService.startWatchingRoutes(this.mAudioRoutesObserver);
        paramContext = localAudioRoutesInfo;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;) {}
      }
      if (paramContext != null) {
        updateAudioRoutes(paramContext);
      }
      rebindAsUser(UserHandle.myUserId());
      if (this.mSelectedRoute == null) {
        MediaRouter.selectDefaultRouteStatic();
      }
    }
    
    void updateAudioRoutes(AudioRoutesInfo paramAudioRoutesInfo)
    {
      Log.v("MediaRouter", "Updating audio routes: " + paramAudioRoutesInfo);
      int i;
      if (paramAudioRoutesInfo.mainType != this.mCurAudioRoutesInfo.mainType)
      {
        this.mCurAudioRoutesInfo.mainType = paramAudioRoutesInfo.mainType;
        if (((paramAudioRoutesInfo.mainType & 0x2) != 0) || ((paramAudioRoutesInfo.mainType & 0x1) != 0))
        {
          i = 17040650;
          MediaRouter.sStatic.mDefaultAudioVideo.mNameResId = i;
          MediaRouter.dispatchRouteChanged(MediaRouter.sStatic.mDefaultAudioVideo);
        }
      }
      else
      {
        i = this.mCurAudioRoutesInfo.mainType;
        if (!TextUtils.equals(paramAudioRoutesInfo.bluetoothName, this.mCurAudioRoutesInfo.bluetoothName))
        {
          this.mCurAudioRoutesInfo.bluetoothName = paramAudioRoutesInfo.bluetoothName;
          if (this.mCurAudioRoutesInfo.bluetoothName == null) {
            break label348;
          }
          if (MediaRouter.sStatic.mBluetoothA2dpRoute != null) {
            break label320;
          }
          paramAudioRoutesInfo = new MediaRouter.RouteInfo(MediaRouter.sStatic.mSystemCategory);
          paramAudioRoutesInfo.mName = this.mCurAudioRoutesInfo.bluetoothName;
          paramAudioRoutesInfo.mDescription = MediaRouter.sStatic.mResources.getText(17040654);
          paramAudioRoutesInfo.mSupportedTypes = 1;
          paramAudioRoutesInfo.mDeviceType = 3;
          MediaRouter.sStatic.mBluetoothA2dpRoute = paramAudioRoutesInfo;
          MediaRouter.addRouteStatic(MediaRouter.sStatic.mBluetoothA2dpRoute);
        }
        label214:
        if (this.mBluetoothA2dpRoute != null) {
          if (isBluetoothA2dpOn()) {
            break label376;
          }
        }
      }
      label320:
      label348:
      label376:
      for (boolean bool = isASBluetoothA2dpOn();; bool = true)
      {
        if ((this.mSelectedRoute == this.mBluetoothA2dpRoute) && (!bool)) {
          break label381;
        }
        if (((this.mSelectedRoute == this.mDefaultAudioVideo) || (this.mSelectedRoute == null)) && (bool)) {
          MediaRouter.selectRouteStatic(1, this.mBluetoothA2dpRoute, false);
        }
        return;
        if ((paramAudioRoutesInfo.mainType & 0x4) != 0)
        {
          i = 17040651;
          break;
        }
        if ((paramAudioRoutesInfo.mainType & 0x8) != 0)
        {
          i = 17040652;
          break;
        }
        i = 17040649;
        break;
        MediaRouter.sStatic.mBluetoothA2dpRoute.mName = this.mCurAudioRoutesInfo.bluetoothName;
        MediaRouter.dispatchRouteChanged(MediaRouter.sStatic.mBluetoothA2dpRoute);
        break label214;
        if (MediaRouter.sStatic.mBluetoothA2dpRoute == null) {
          break label214;
        }
        MediaRouter.removeRouteStatic(MediaRouter.sStatic.mBluetoothA2dpRoute);
        MediaRouter.sStatic.mBluetoothA2dpRoute = null;
        break label214;
      }
      label381:
      MediaRouter.selectRouteStatic(1, this.mDefaultAudioVideo, false);
    }
    
    void updateClientState()
    {
      this.mClientState = null;
      if (this.mClient != null) {}
      try
      {
        this.mClientState = this.mMediaRouterService.getState(this.mClient);
        if (this.mClientState != null)
        {
          ArrayList localArrayList = this.mClientState.routes;
          if (this.mClientState == null) {
            break label144;
          }
          localObject2 = this.mClientState.globallySelectedRouteId;
          if (localArrayList == null) {
            break label150;
          }
          i = localArrayList.size();
          j = 0;
          for (;;)
          {
            if (j >= i) {
              break label166;
            }
            localObject3 = (MediaRouterClientState.RouteInfo)localArrayList.get(j);
            localRouteInfo = findGlobalRoute(((MediaRouterClientState.RouteInfo)localObject3).id);
            if (localRouteInfo != null) {
              break;
            }
            MediaRouter.addRouteStatic(makeGlobalRoute((MediaRouterClientState.RouteInfo)localObject3));
            j += 1;
          }
        }
      }
      catch (RemoteException localRemoteException)
      {
        Object localObject2;
        int i;
        int j;
        Object localObject3;
        Object localObject1;
        for (;;)
        {
          MediaRouter.RouteInfo localRouteInfo;
          Log.e("MediaRouter", "Unable to retrieve media router client state.", localRemoteException);
          continue;
          localObject1 = null;
          continue;
          label144:
          localObject2 = null;
          continue;
          label150:
          i = 0;
          continue;
          updateGlobalRoute(localRouteInfo, (MediaRouterClientState.RouteInfo)localObject3);
        }
        label166:
        label219:
        int k;
        if (localObject2 != null)
        {
          localObject3 = findGlobalRoute((String)localObject2);
          if (localObject3 == null)
          {
            Log.w("MediaRouter", "Could not find new globally selected route: " + (String)localObject2);
            j = this.mRoutes.size();
            k = j - 1;
            if (j <= 0) {
              return;
            }
            localObject2 = (MediaRouter.RouteInfo)this.mRoutes.get(k);
            localObject3 = ((MediaRouter.RouteInfo)localObject2).mGlobalRouteId;
            if (localObject3 != null)
            {
              j = 0;
              label254:
              if (j >= i) {
                break label405;
              }
              if (!((String)localObject3).equals(((MediaRouterClientState.RouteInfo)((ArrayList)localObject1).get(j)).id)) {
                break label398;
              }
            }
          }
        }
        for (;;)
        {
          j = k;
          break label219;
          if (localObject3 == this.mSelectedRoute) {
            break;
          }
          if (MediaRouter.-get0()) {
            Log.d("MediaRouter", "Selecting new globally selected route: " + localObject3);
          }
          MediaRouter.selectRouteStatic(((MediaRouter.RouteInfo)localObject3).mSupportedTypes, (MediaRouter.RouteInfo)localObject3, false);
          break;
          if ((this.mSelectedRoute == null) || (this.mSelectedRoute.mGlobalRouteId == null)) {
            break;
          }
          if (MediaRouter.-get0()) {
            Log.d("MediaRouter", "Unselecting previous globally selected route: " + this.mSelectedRoute);
          }
          MediaRouter.selectDefaultRouteStatic();
          break;
          label398:
          j += 1;
          break label254;
          label405:
          MediaRouter.removeRouteStatic((MediaRouter.RouteInfo)localObject2);
        }
      }
    }
    
    void updateDiscoveryRequest()
    {
      int j = 0;
      int m = 0;
      boolean bool1 = false;
      int i = 0;
      int i1 = this.mCallbacks.size();
      int k = 0;
      if (k < i1)
      {
        MediaRouter.CallbackInfo localCallbackInfo = (MediaRouter.CallbackInfo)this.mCallbacks.get(k);
        if ((localCallbackInfo.flags & 0x5) != 0) {
          j |= localCallbackInfo.type;
        }
        for (;;)
        {
          int n = i;
          if ((localCallbackInfo.flags & 0x1) != 0)
          {
            boolean bool2 = true;
            bool1 = bool2;
            n = i;
            if ((localCallbackInfo.type & 0x4) != 0)
            {
              n = 1;
              bool1 = bool2;
            }
          }
          k += 1;
          i = n;
          break;
          if ((localCallbackInfo.flags & 0x8) != 0) {
            m |= localCallbackInfo.type;
          } else {
            j |= localCallbackInfo.type;
          }
        }
      }
      if (j == 0)
      {
        k = j;
        if (!bool1) {}
      }
      else
      {
        k = j | m;
      }
      if (this.mCanConfigureWifiDisplays)
      {
        j = i;
        if (this.mSelectedRoute != null)
        {
          j = i;
          if (this.mSelectedRoute.matchesTypes(4)) {
            j = 0;
          }
        }
        if (j == 0) {
          break label246;
        }
        if (!this.mActivelyScanningWifiDisplays)
        {
          this.mActivelyScanningWifiDisplays = true;
          this.mDisplayService.startWifiDisplayScan();
        }
      }
      for (;;)
      {
        if ((k != this.mDiscoveryRequestRouteTypes) || (bool1 != this.mDiscoverRequestActiveScan))
        {
          this.mDiscoveryRequestRouteTypes = k;
          this.mDiscoverRequestActiveScan = bool1;
          publishClientDiscoveryRequest();
        }
        return;
        label246:
        if (this.mActivelyScanningWifiDisplays)
        {
          this.mActivelyScanningWifiDisplays = false;
          this.mDisplayService.stopWifiDisplayScan();
        }
      }
    }
    
    void updateGlobalRoute(MediaRouter.RouteInfo paramRouteInfo, MediaRouterClientState.RouteInfo paramRouteInfo1)
    {
      int i = 0;
      int m = 0;
      int k = 0;
      if (!Objects.equals(paramRouteInfo.mName, paramRouteInfo1.name))
      {
        paramRouteInfo.mName = paramRouteInfo1.name;
        i = 1;
      }
      if (!Objects.equals(paramRouteInfo.mDescription, paramRouteInfo1.description))
      {
        paramRouteInfo.mDescription = paramRouteInfo1.description;
        i = 1;
      }
      int n = paramRouteInfo.mSupportedTypes;
      if (n != paramRouteInfo1.supportedTypes)
      {
        paramRouteInfo.mSupportedTypes = paramRouteInfo1.supportedTypes;
        i = 1;
      }
      if (paramRouteInfo.mEnabled != paramRouteInfo1.enabled)
      {
        paramRouteInfo.mEnabled = paramRouteInfo1.enabled;
        i = 1;
      }
      if (MediaRouter.RouteInfo.-get0(paramRouteInfo) != paramRouteInfo1.statusCode)
      {
        paramRouteInfo.setRealStatusCode(paramRouteInfo1.statusCode);
        i = 1;
      }
      if (paramRouteInfo.mPlaybackType != paramRouteInfo1.playbackType)
      {
        paramRouteInfo.mPlaybackType = paramRouteInfo1.playbackType;
        i = 1;
      }
      if (paramRouteInfo.mPlaybackStream != paramRouteInfo1.playbackStream)
      {
        paramRouteInfo.mPlaybackStream = paramRouteInfo1.playbackStream;
        i = 1;
      }
      int j = i;
      i = m;
      if (paramRouteInfo.mVolume != paramRouteInfo1.volume)
      {
        paramRouteInfo.mVolume = paramRouteInfo1.volume;
        j = 1;
        i = 1;
      }
      if (paramRouteInfo.mVolumeMax != paramRouteInfo1.volumeMax)
      {
        paramRouteInfo.mVolumeMax = paramRouteInfo1.volumeMax;
        j = 1;
        i = 1;
      }
      if (paramRouteInfo.mVolumeHandling != paramRouteInfo1.volumeHandling)
      {
        paramRouteInfo.mVolumeHandling = paramRouteInfo1.volumeHandling;
        j = 1;
        i = 1;
      }
      if (paramRouteInfo.mPresentationDisplayId != paramRouteInfo1.presentationDisplayId)
      {
        paramRouteInfo.mPresentationDisplayId = paramRouteInfo1.presentationDisplayId;
        paramRouteInfo.updatePresentationDisplay();
        j = 1;
        k = 1;
      }
      if (j != 0) {
        MediaRouter.dispatchRouteChanged(paramRouteInfo, n);
      }
      if (i != 0) {
        MediaRouter.dispatchRouteVolumeChanged(paramRouteInfo);
      }
      if (k != 0) {
        MediaRouter.dispatchRoutePresentationDisplayChanged(paramRouteInfo);
      }
    }
    
    final class Client
      extends IMediaRouterClient.Stub
    {
      Client() {}
      
      public void onStateChanged()
      {
        MediaRouter.Static.this.mHandler.post(new Runnable()
        {
          public void run()
          {
            if (MediaRouter.Static.Client.this == MediaRouter.Static.this.mClient) {
              MediaRouter.Static.this.updateClientState();
            }
          }
        });
      }
    }
  }
  
  public static class UserRouteInfo
    extends MediaRouter.RouteInfo
  {
    RemoteControlClient mRcc;
    SessionVolumeProvider mSvp;
    
    UserRouteInfo(MediaRouter.RouteCategory paramRouteCategory)
    {
      super();
      this.mSupportedTypes = 8388608;
      this.mPlaybackType = 1;
      this.mVolumeHandling = 0;
    }
    
    private void configureSessionVolume()
    {
      if (this.mRcc == null)
      {
        if (MediaRouter.-get0()) {
          Log.d("MediaRouter", "No Rcc to configure volume for route " + this.mName);
        }
        return;
      }
      MediaSession localMediaSession = this.mRcc.getMediaSession();
      if (localMediaSession == null)
      {
        if (MediaRouter.-get0()) {
          Log.d("MediaRouter", "Rcc has no session to configure volume");
        }
        return;
      }
      if (this.mPlaybackType == 1)
      {
        int i = 0;
        switch (this.mVolumeHandling)
        {
        default: 
          if ((this.mSvp != null) && (this.mSvp.getVolumeControl() == i)) {
            break;
          }
        }
        for (;;)
        {
          this.mSvp = new SessionVolumeProvider(i, this.mVolumeMax, this.mVolume);
          localMediaSession.setPlaybackToRemote(this.mSvp);
          do
          {
            return;
            i = 2;
            break;
          } while (this.mSvp.getMaxVolume() == this.mVolumeMax);
        }
      }
      AudioAttributes.Builder localBuilder = new AudioAttributes.Builder();
      localBuilder.setLegacyStreamType(this.mPlaybackStream);
      localMediaSession.setPlaybackToLocal(localBuilder.build());
      this.mSvp = null;
    }
    
    private void updatePlaybackInfoOnRcc()
    {
      configureSessionVolume();
    }
    
    public RemoteControlClient getRemoteControlClient()
    {
      return this.mRcc;
    }
    
    public void requestSetVolume(int paramInt)
    {
      if (this.mVolumeHandling == 1)
      {
        if (this.mVcb == null)
        {
          Log.e("MediaRouter", "Cannot requestSetVolume on user route - no volume callback set");
          return;
        }
        this.mVcb.vcb.onVolumeSetRequest(this, paramInt);
      }
    }
    
    public void requestUpdateVolume(int paramInt)
    {
      if (this.mVolumeHandling == 1)
      {
        if (this.mVcb == null)
        {
          Log.e("MediaRouter", "Cannot requestChangeVolume on user route - no volumec callback set");
          return;
        }
        this.mVcb.vcb.onVolumeUpdateRequest(this, paramInt);
      }
    }
    
    public void setDescription(CharSequence paramCharSequence)
    {
      this.mDescription = paramCharSequence;
      routeUpdated();
    }
    
    public void setIconDrawable(Drawable paramDrawable)
    {
      this.mIcon = paramDrawable;
    }
    
    public void setIconResource(int paramInt)
    {
      setIconDrawable(MediaRouter.sStatic.mResources.getDrawable(paramInt));
    }
    
    public void setName(int paramInt)
    {
      this.mNameResId = paramInt;
      this.mName = null;
      routeUpdated();
    }
    
    public void setName(CharSequence paramCharSequence)
    {
      this.mName = paramCharSequence;
      routeUpdated();
    }
    
    public void setPlaybackStream(int paramInt)
    {
      if (this.mPlaybackStream != paramInt)
      {
        this.mPlaybackStream = paramInt;
        configureSessionVolume();
      }
    }
    
    public void setPlaybackType(int paramInt)
    {
      if (this.mPlaybackType != paramInt)
      {
        this.mPlaybackType = paramInt;
        configureSessionVolume();
      }
    }
    
    public void setRemoteControlClient(RemoteControlClient paramRemoteControlClient)
    {
      this.mRcc = paramRemoteControlClient;
      updatePlaybackInfoOnRcc();
    }
    
    public void setStatus(CharSequence paramCharSequence)
    {
      setStatusInt(paramCharSequence);
    }
    
    public void setVolume(int paramInt)
    {
      paramInt = Math.max(0, Math.min(paramInt, getVolumeMax()));
      if (this.mVolume != paramInt)
      {
        this.mVolume = paramInt;
        if (this.mSvp != null) {
          this.mSvp.setCurrentVolume(this.mVolume);
        }
        MediaRouter.dispatchRouteVolumeChanged(this);
        if (this.mGroup != null) {
          this.mGroup.memberVolumeChanged(this);
        }
      }
    }
    
    public void setVolumeCallback(MediaRouter.VolumeCallback paramVolumeCallback)
    {
      this.mVcb = new MediaRouter.VolumeCallbackInfo(paramVolumeCallback, this);
    }
    
    public void setVolumeHandling(int paramInt)
    {
      if (this.mVolumeHandling != paramInt)
      {
        this.mVolumeHandling = paramInt;
        configureSessionVolume();
      }
    }
    
    public void setVolumeMax(int paramInt)
    {
      if (this.mVolumeMax != paramInt)
      {
        this.mVolumeMax = paramInt;
        configureSessionVolume();
      }
    }
    
    class SessionVolumeProvider
      extends VolumeProvider
    {
      public SessionVolumeProvider(int paramInt1, int paramInt2, int paramInt3)
      {
        super(paramInt2, paramInt3);
      }
      
      public void onAdjustVolume(final int paramInt)
      {
        MediaRouter.sStatic.mHandler.post(new Runnable()
        {
          public void run()
          {
            if (MediaRouter.UserRouteInfo.this.mVcb != null) {
              MediaRouter.UserRouteInfo.this.mVcb.vcb.onVolumeUpdateRequest(MediaRouter.UserRouteInfo.this.mVcb.route, paramInt);
            }
          }
        });
      }
      
      public void onSetVolumeTo(final int paramInt)
      {
        MediaRouter.sStatic.mHandler.post(new Runnable()
        {
          public void run()
          {
            if (MediaRouter.UserRouteInfo.this.mVcb != null) {
              MediaRouter.UserRouteInfo.this.mVcb.vcb.onVolumeSetRequest(MediaRouter.UserRouteInfo.this.mVcb.route, paramInt);
            }
          }
        });
      }
    }
  }
  
  public static abstract class VolumeCallback
  {
    public abstract void onVolumeSetRequest(MediaRouter.RouteInfo paramRouteInfo, int paramInt);
    
    public abstract void onVolumeUpdateRequest(MediaRouter.RouteInfo paramRouteInfo, int paramInt);
  }
  
  static class VolumeCallbackInfo
  {
    public final MediaRouter.RouteInfo route;
    public final MediaRouter.VolumeCallback vcb;
    
    public VolumeCallbackInfo(MediaRouter.VolumeCallback paramVolumeCallback, MediaRouter.RouteInfo paramRouteInfo)
    {
      this.vcb = paramVolumeCallback;
      this.route = paramRouteInfo;
    }
  }
  
  static class VolumeChangeReceiver
    extends BroadcastReceiver
  {
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      if (paramIntent.getAction().equals("android.media.VOLUME_CHANGED_ACTION"))
      {
        if (paramIntent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1) != 3) {
          return;
        }
        int i = paramIntent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", 0);
        if (i != paramIntent.getIntExtra("android.media.EXTRA_PREV_VOLUME_STREAM_VALUE", 0)) {
          MediaRouter.systemVolumeChanged(i);
        }
      }
    }
  }
  
  static class WifiDisplayStatusChangedReceiver
    extends BroadcastReceiver
  {
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      if (paramIntent.getAction().equals("android.hardware.display.action.WIFI_DISPLAY_STATUS_CHANGED")) {
        MediaRouter.updateWifiDisplayStatus((WifiDisplayStatus)paramIntent.getParcelableExtra("android.hardware.display.extra.WIFI_DISPLAY_STATUS"));
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/MediaRouter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */