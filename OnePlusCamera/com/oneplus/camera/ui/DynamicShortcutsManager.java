package com.oneplus.camera.ui;

import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.base.Log;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.UIComponent;
import com.oneplus.camera.capturemode.CaptureMode;
import com.oneplus.camera.capturemode.CaptureModeEventArgs;
import com.oneplus.camera.capturemode.CaptureModeManager;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DynamicShortcutsManager
  extends UIComponent
{
  public static final String EXTRA_DISABLED_MESSAGE_RES_ID = "DisabledMessageResId";
  public static final String EXTRA_LONG_LABEL_RES_ID = "LongLabelResId";
  public static final String EXTRA_SHORT_LABEL_RES_ID = "ShortLabelResId";
  private CaptureModeManager m_CaptureModeManager;
  private Map<String, ShortcutInfo> m_ShortcutInfos = new HashMap();
  
  DynamicShortcutsManager(CameraActivity paramCameraActivity)
  {
    super("Dynamic Shortcuts Manager", paramCameraActivity, false);
  }
  
  private void addShortcut(ShortcutInfo paramShortcutInfo)
  {
    ShortcutInfo localShortcutInfo = (ShortcutInfo)this.m_ShortcutInfos.put(paramShortcutInfo.getId(), paramShortcutInfo);
    ShortcutManager localShortcutManager = (ShortcutManager)getCameraActivity().getSystemService(ShortcutManager.class);
    paramShortcutInfo = Arrays.asList(new ShortcutInfo[] { paramShortcutInfo });
    if (localShortcutInfo == null)
    {
      localShortcutManager.addDynamicShortcuts(paramShortcutInfo);
      return;
    }
    localShortcutManager.updateShortcuts(paramShortcutInfo);
  }
  
  private void initShortcutInfos()
  {
    Iterator localIterator = ((ShortcutManager)getCameraActivity().getSystemService(ShortcutManager.class)).getDynamicShortcuts().iterator();
    while (localIterator.hasNext())
    {
      ShortcutInfo localShortcutInfo = (ShortcutInfo)localIterator.next();
      Log.v(this.TAG, "initShortcutInfos() - Shortcut: ", localShortcutInfo.getId());
      this.m_ShortcutInfos.put(localShortcutInfo.getId(), localShortcutInfo);
    }
  }
  
  private void onCaptureModeAdded(CaptureMode paramCaptureMode)
  {
    paramCaptureMode = paramCaptureMode.getShortcutInfo();
    if (paramCaptureMode != null)
    {
      Log.v(this.TAG, "onCaptureModeAdded() - Add shortcut: ", paramCaptureMode.getId());
      addShortcut(paramCaptureMode);
    }
  }
  
  private void onCaptureModeRemoved(CaptureMode paramCaptureMode)
  {
    paramCaptureMode = paramCaptureMode.getShortcutInfo();
    if (paramCaptureMode != null)
    {
      Log.v(this.TAG, "onCaptureModeRemoved() - Remove shortcut: ", paramCaptureMode.getId());
      removeShortcut(paramCaptureMode.getId());
    }
  }
  
  private void removeShortcut(String paramString)
  {
    paramString = (ShortcutInfo)this.m_ShortcutInfos.remove(paramString);
    if (paramString == null) {
      return;
    }
    ((ShortcutManager)getCameraActivity().getSystemService(ShortcutManager.class)).removeDynamicShortcuts(Arrays.asList(new String[] { paramString.getId() }));
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    this.m_CaptureModeManager = ((CaptureModeManager)findComponent(CaptureModeManager.class));
    initShortcutInfos();
    if (this.m_CaptureModeManager != null)
    {
      Object localObject = this.m_ShortcutInfos.keySet();
      Iterator localIterator = ((List)this.m_CaptureModeManager.get(CaptureModeManager.PROP_CAPTURE_MODES)).iterator();
      while (localIterator.hasNext())
      {
        ShortcutInfo localShortcutInfo = ((CaptureMode)localIterator.next()).getShortcutInfo();
        if (localShortcutInfo != null)
        {
          Log.v(this.TAG, "onInitialize() - Add shortcut: ", localShortcutInfo.getId());
          addShortcut(localShortcutInfo);
          ((Set)localObject).remove(localShortcutInfo.getId());
        }
      }
      localObject = ((Iterable)localObject).iterator();
      while (((Iterator)localObject).hasNext()) {
        removeShortcut((String)((Iterator)localObject).next());
      }
      this.m_CaptureModeManager.addHandler(CaptureModeManager.EVENT_CAPTURE_MODE_ADDED, new EventHandler()
      {
        public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<CaptureModeEventArgs> paramAnonymousEventKey, CaptureModeEventArgs paramAnonymousCaptureModeEventArgs)
        {
          DynamicShortcutsManager.-wrap0(DynamicShortcutsManager.this, paramAnonymousCaptureModeEventArgs.getCaptureMode());
        }
      });
      this.m_CaptureModeManager.addHandler(CaptureModeManager.EVENT_CAPTURE_MODE_REMOVED, new EventHandler()
      {
        public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<CaptureModeEventArgs> paramAnonymousEventKey, CaptureModeEventArgs paramAnonymousCaptureModeEventArgs)
        {
          DynamicShortcutsManager.-wrap1(DynamicShortcutsManager.this, paramAnonymousCaptureModeEventArgs.getCaptureMode());
        }
      });
      return;
    }
    Log.w(this.TAG, "onInitialize() - Cannot find capture mode manager");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/DynamicShortcutsManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */