package com.oneplus.camera;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toolbar;
import com.oneplus.base.BaseActivity;
import com.oneplus.base.BaseActivity.ThemeMode;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.ScreenSize;
import com.oneplus.base.Settings;
import com.oneplus.camera.drawable.CameraPreviewGridDrawable;
import com.oneplus.camera.ui.CameraPreviewGrid.GridType;
import com.oneplus.camera.ui.menu.AboutMenuItem;
import com.oneplus.camera.ui.menu.BooleanSettingsMenuItem;
import com.oneplus.camera.ui.menu.DividerMenuItem;
import com.oneplus.camera.ui.menu.MenuItem;
import com.oneplus.camera.ui.menu.MenuItem.DividerStyle;
import com.oneplus.camera.ui.menu.MenuListView;
import com.oneplus.camera.ui.menu.RadioMenuItem;
import com.oneplus.camera.ui.menu.StorageMenuItem;
import com.oneplus.camera.watermark.SloganWatermarkDrawable;
import com.oneplus.camera.watermark.Watermark;
import com.oneplus.io.Storage;
import com.oneplus.io.Storage.Type;
import com.oneplus.io.StorageManager;
import com.oneplus.io.StorageUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AdvancedSettingsActivity
  extends BaseActivity
  implements OnActionBarTitleChangedListener
{
  public static final String EXTRA_IS_ACTIVE_PICTURE_INFO_OPTIONS_VISIBLE = "IsActivePictureInfoOptionsVisible";
  public static final String EXTRA_IS_BOKEH_ORIGINAL_SUPPORTED = "IsBokehOriginalPictureSupported";
  public static final String EXTRA_IS_MANUAL_CAPTURE_OPTIONS_VISIBLE = "IsManualCaptureOptionsVisible";
  public static final String EXTRA_IS_MIRROR_SUPPORTED = "IsMirrorSupported";
  public static final String EXTRA_IS_RAW_CAPTURE_OPTION_VISIBLE = "IsRawCaptureVisible";
  public static final String EXTRA_IS_SERVICE_MODE = "IsServiceMode";
  public static final String EXTRA_IS_SMILE_CAPTURE_OPTION_VISIBLE = "IsSmileCaptureVisible";
  public static final String EXTRA_IS_VIDEO_FRAME_RATE_OPTION_VISIBLE = "IsVideFrameRateVisible";
  public static final String EXTRA_OUTPUT_URI = "OutputUri";
  public static final String EXTRA_SETTINGS_IS_VOLATILE = "Settings.IsVolatile";
  public static final String EXTRA_SETTINGS_NAME = "Settings.Name";
  public static final String EXTRA_START_MODE = "StartMode";
  private static final int FRAGMENT_MODE_ABOUT = 1;
  private static final int FRAGMENT_MODE_AGREEMENT = 2;
  private static final int FRAGMENT_MODE_GRID = 3;
  private static final int FRAGMENT_MODE_TOP = 0;
  private static final int FRAGMENT_MODE_WATERMARK = 4;
  private static final String FRAGMENT_TAG_ABOUT = "About";
  private static final String FRAGMENT_TAG_AGREEMENT = "Agreement";
  private static final String FRAGMENT_TAG_ALERT_DIALOG = "AlertDialog";
  private static final String FRAGMENT_TAG_GRID = "Grid";
  private static final String FRAGMENT_TAG_WATERMARK = "Watermark";
  private static final String STATE_KEY_PREFIX = "FragmentMode";
  private static final String TAG = AdvancedSettingsActivity.class.getSimpleName();
  private AboutMenuItem m_AboutMenuItem;
  private Toolbar m_ActionBar;
  private TextView m_ActionBarTitle;
  private ImageButton m_BackButton;
  private MenuItem m_BokehOriginalMenuItem;
  private int m_FragmentMode;
  private MenuItem m_GridMenuItem;
  private boolean m_HasActivePictureInfoSetting;
  private boolean m_HasBokehOriginalSetting;
  private boolean m_HasManualCaptureSetting;
  private boolean m_HasRawCaptureSetting;
  private boolean m_HasSmileCaptureSetting;
  private boolean m_HasVideoFrameRateSetting;
  private boolean m_IsMirrorSupported;
  private MenuItem m_IsMirroredMenuItem;
  private boolean m_IsServiceMode;
  private MenuItem m_LocationMenuItem;
  private MenuItem m_ManualHistogramItem;
  private MenuItem m_ManualHorizontalLineItem;
  private MenuItem m_ManualPictureInformationItem;
  private final List<MenuItem> m_MenuItems = new ArrayList();
  private MenuListView m_MenuListView;
  private String m_OutputUriStr;
  private MenuItem m_QuickCaptureMenuItem;
  private MenuItem m_RawCaptureMenuItem;
  private Settings m_Settings;
  private BroadcastReceiver m_ShutdownReceiver;
  private MenuItem m_ShutterSoundMenuItem;
  private MenuItem m_SimpleUIModeMenuItem;
  private MenuItem m_SmileCaptureMenuItem;
  private StartMode m_StartMode;
  private StorageManager m_StorageManager;
  private PropertyChangedCallback<List<Storage>> m_StorageManagerCallBack;
  private MenuItem m_StorageMenuItem;
  private View m_TemporaryStatusBarSpacingView;
  private BaseActivity.ThemeMode m_ThemeMode;
  private String m_VersionName;
  private MenuItem m_VideoFrameRateMenuItem;
  private MenuItem m_WatermarkMenuItem;
  
  private boolean closeAboutFragment()
  {
    FragmentManager localFragmentManager = getFragmentManager();
    Fragment localFragment = localFragmentManager.findFragmentByTag("About");
    if (localFragment == null) {
      return false;
    }
    localFragmentManager.beginTransaction().remove(localFragment).setTransition(4099).commit();
    this.m_FragmentMode = 0;
    this.m_ActionBarTitle.setText(2131558414);
    return true;
  }
  
  private boolean closeAgreementFragment()
  {
    FragmentManager localFragmentManager = getFragmentManager();
    Fragment localFragment1 = localFragmentManager.findFragmentByTag("Agreement");
    if (localFragment1 == null) {
      return false;
    }
    Fragment localFragment2 = localFragmentManager.findFragmentByTag("About");
    if (localFragment2 != null) {
      localFragmentManager.beginTransaction().show(localFragment2).commitAllowingStateLoss();
    }
    localFragmentManager.beginTransaction().remove(localFragment1).setTransition(4099).commit();
    this.m_FragmentMode = 1;
    this.m_ActionBarTitle.setText(2131558435);
    return true;
  }
  
  private boolean closeGridFragment()
  {
    FragmentManager localFragmentManager = getFragmentManager();
    Fragment localFragment = localFragmentManager.findFragmentByTag("Grid");
    if (localFragment == null) {
      return false;
    }
    localFragmentManager.beginTransaction().remove(localFragment).setTransition(4099).commit();
    this.m_FragmentMode = 0;
    this.m_ActionBarTitle.setText(2131558414);
    this.m_GridMenuItem.set(MenuItem.PROP_SUBTITLE, getGridTypeDescription(this, (CameraPreviewGrid.GridType)this.m_Settings.getEnum("Grid.Type", CameraPreviewGrid.GridType.class)));
    return true;
  }
  
  private boolean closeWatermarkFragment(boolean paramBoolean)
  {
    FragmentManager localFragmentManager = getFragmentManager();
    Fragment localFragment = localFragmentManager.findFragmentByTag("Watermark");
    if (localFragment == null) {
      return false;
    }
    WatermarkFragment localWatermarkFragment = (WatermarkFragment)localFragment;
    if ((paramBoolean) && (localWatermarkFragment.isEditTextEmpty()))
    {
      if (localFragmentManager.findFragmentByTag("AlertDialog") != null) {
        return true;
      }
      Log.v(TAG, "closeWatermarkFragment() - Edit text is empty");
      new AlertDialogFragment().show(localFragmentManager, "AlertDialog");
      return true;
    }
    WatermarkFragment.-wrap0(localWatermarkFragment);
    localFragmentManager.beginTransaction().remove(localFragment).setTransition(4099).commit();
    this.m_FragmentMode = 0;
    this.m_ActionBarTitle.setText(2131558414);
    return true;
  }
  
  private void disableLayoutFullscreen()
  {
    if ((this.m_TemporaryStatusBarSpacingView != null) && (this.m_TemporaryStatusBarSpacingView.getVisibility() == 0))
    {
      int i = getWindow().getDecorView().getSystemUiVisibility();
      getWindow().getDecorView().setSystemUiVisibility(i & 0xFBFF);
      this.m_TemporaryStatusBarSpacingView.setVisibility(8);
      ((RelativeLayout.LayoutParams)this.m_ActionBar.getLayoutParams()).setMargins(0, 0, 0, 0);
    }
  }
  
  private static String getGridTypeDescription(Context paramContext, CameraPreviewGrid.GridType paramGridType)
  {
    if (paramGridType == null) {
      return paramContext.getString(2131558421);
    }
    switch (-getcom-oneplus-camera-ui-CameraPreviewGrid$GridTypeSwitchesValues()[paramGridType.ordinal()])
    {
    default: 
      return paramContext.getString(2131558421);
    case 2: 
      return paramContext.getString(2131558619);
    case 3: 
      return paramContext.getString(2131558620);
    }
    return paramContext.getString(2131558420);
  }
  
  private Settings getSettings()
  {
    return this.m_Settings;
  }
  
  private void handleStorageMenuItem()
  {
    if (StorageUtils.findStorage(this.m_StorageManager, Storage.Type.SD_CARD) != null)
    {
      if (this.m_StorageMenuItem != null)
      {
        setStoragePosition();
        if (!this.m_OutputUriStr.isEmpty()) {
          this.m_MenuItems.remove(this.m_StorageMenuItem);
        }
        return;
      }
      this.m_StorageMenuItem = new StorageMenuItem(this.m_Settings, "StorageType", this.m_StorageManager);
      this.m_StorageMenuItem.set(MenuItem.PROP_TITLE, getString(2131558434));
      setStoragePosition();
      List localList = this.m_MenuItems;
      if (this.m_MenuItems.size() > 1) {}
      for (int i = this.m_MenuItems.size() - 1;; i = 0)
      {
        localList.add(i, this.m_StorageMenuItem);
        break;
      }
    }
    if (this.m_StorageMenuItem != null) {
      this.m_MenuItems.remove(this.m_StorageMenuItem);
    }
    this.m_StorageMenuItem = null;
    this.m_Settings.set("StorageType", Storage.Type.INTERNAL);
  }
  
  private boolean isSecureMode()
  {
    if (this.m_StartMode == null) {
      this.m_StartMode = ((StartMode)getIntent().getExtras().get("StartMode"));
    }
    return this.m_StartMode == StartMode.SECURE_PHOTO;
  }
  
  private void onMenuItemClicked(MenuItem paramMenuItem)
  {
    Log.v(TAG, "onMenuItemClicked() - title : ", paramMenuItem.get(MenuItem.PROP_TITLE));
    if (paramMenuItem == this.m_WatermarkMenuItem)
    {
      disableLayoutFullscreen();
      openWatermarkFragment();
    }
    do
    {
      return;
      if (paramMenuItem == this.m_GridMenuItem)
      {
        openGridFragment();
        return;
      }
    } while (paramMenuItem != this.m_AboutMenuItem);
    openAboutFragment();
  }
  
  private void openAboutFragment()
  {
    if (this.m_FragmentMode != 0) {
      return;
    }
    FragmentManager localFragmentManager = getFragmentManager();
    if (localFragmentManager.findFragmentByTag("About") == null)
    {
      AboutFragment localAboutFragment = new AboutFragment(this.m_VersionName);
      localFragmentManager.beginTransaction().add(2131361820, localAboutFragment, "About").setTransition(4099).commit();
      this.m_FragmentMode = 1;
      this.m_ActionBarTitle.setText(2131558435);
    }
  }
  
  private void openAgreementFragment()
  {
    if (this.m_FragmentMode != 1) {
      return;
    }
    FragmentManager localFragmentManager = getFragmentManager();
    if (localFragmentManager.findFragmentByTag("Agreement") == null)
    {
      Object localObject = new AgreementFragment();
      localFragmentManager.beginTransaction().add(2131361820, (Fragment)localObject, "Agreement").commit();
      this.m_FragmentMode = 2;
      localObject = localFragmentManager.findFragmentByTag("About");
      if (localObject != null) {
        localFragmentManager.beginTransaction().hide((Fragment)localObject).commitAllowingStateLoss();
      }
    }
  }
  
  private void openGridFragment()
  {
    if (this.m_FragmentMode != 0) {
      return;
    }
    FragmentManager localFragmentManager = getFragmentManager();
    if (localFragmentManager.findFragmentByTag("Grid") == null)
    {
      GridFragment localGridFragment = new GridFragment();
      localFragmentManager.beginTransaction().add(2131361822, localGridFragment, "Grid").setTransition(4099).commit();
      this.m_FragmentMode = 3;
      this.m_ActionBarTitle.setText(2131558419);
    }
  }
  
  private void openWatermarkFragment()
  {
    if (this.m_FragmentMode != 0) {
      return;
    }
    FragmentManager localFragmentManager = getFragmentManager();
    if (localFragmentManager.findFragmentByTag("Watermark") == null)
    {
      WatermarkFragment localWatermarkFragment = new WatermarkFragment();
      localFragmentManager.beginTransaction().add(2131361823, localWatermarkFragment, "Watermark").setTransition(4099).commit();
      this.m_FragmentMode = 4;
      this.m_ActionBarTitle.setText(2131558440);
    }
  }
  
  private void registerReceivers()
  {
    if ((isSecureMode()) && (this.m_ShutdownReceiver == null))
    {
      Log.v(TAG, "registerReceivers() - Shutdown receiver");
      this.m_ShutdownReceiver = new BroadcastReceiver()
      {
        public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
        {
          if (AdvancedSettingsActivity.-wrap1(AdvancedSettingsActivity.this))
          {
            Log.v(AdvancedSettingsActivity.-get0(), "Intent: ", paramAnonymousIntent, ", finish itself");
            AdvancedSettingsActivity.this.finish();
          }
        }
      };
      IntentFilter localIntentFilter = new IntentFilter("android.intent.action.SCREEN_OFF");
      registerReceiver(this.m_ShutdownReceiver, localIntentFilter);
      localIntentFilter = new IntentFilter("android.intent.action.USER_PRESENT");
      registerReceiver(this.m_ShutdownReceiver, localIntentFilter);
    }
  }
  
  private void setStoragePosition()
  {
    Storage localStorage = StorageUtils.findStorageFromSettings(this.m_StorageManager, this.m_Settings, Storage.Type.INTERNAL);
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (localStorage != null)
    {
      bool1 = bool2;
      if (localStorage.getType() == Storage.Type.SD_CARD)
      {
        bool1 = bool2;
        if (localStorage.isReady()) {
          bool1 = true;
        }
      }
    }
    this.m_StorageMenuItem.set(MenuItem.PROP_IS_CHECKED, Boolean.valueOf(bool1));
  }
  
  private void setupMenuItems()
  {
    Object localObject = new DividerMenuItem();
    ((DividerMenuItem)localObject).set(MenuItem.PROP_TITLE, getString(2131558446));
    this.m_MenuItems.add(localObject);
    this.m_LocationMenuItem = new BooleanSettingsMenuItem(this.m_Settings, "Location.Save");
    this.m_LocationMenuItem.set(MenuItem.PROP_TITLE, getString(2131558422));
    this.m_LocationMenuItem.set(MenuItem.PROP_DIVIDER_STYLE, MenuItem.DividerStyle.INDENTED);
    this.m_MenuItems.add(this.m_LocationMenuItem);
    this.m_ShutterSoundMenuItem = new BooleanSettingsMenuItem(this.m_Settings, "ShutterSound");
    this.m_ShutterSoundMenuItem.set(MenuItem.PROP_TITLE, getString(2131558428));
    this.m_ShutterSoundMenuItem.set(MenuItem.PROP_DIVIDER_STYLE, MenuItem.DividerStyle.INDENTED);
    this.m_MenuItems.add(this.m_ShutterSoundMenuItem);
    this.m_QuickCaptureMenuItem = new BooleanSettingsMenuItem(this.m_Settings, "IsQuickCaptureEnabled");
    this.m_QuickCaptureMenuItem.set(MenuItem.PROP_TITLE, getString(2131558425));
    this.m_QuickCaptureMenuItem.set(MenuItem.PROP_SUBTITLE, getString(2131558426));
    if ((!this.m_IsServiceMode) || (this.m_HasRawCaptureSetting) || (this.m_HasSmileCaptureSetting) || (this.m_HasVideoFrameRateSetting)) {
      this.m_QuickCaptureMenuItem.set(MenuItem.PROP_DIVIDER_STYLE, MenuItem.DividerStyle.INDENTED);
    }
    this.m_MenuItems.add(this.m_QuickCaptureMenuItem);
    this.m_GridMenuItem = new MenuItem();
    this.m_GridMenuItem.set(MenuItem.PROP_TITLE, getString(2131558419));
    this.m_GridMenuItem.set(MenuItem.PROP_SUBTITLE, getGridTypeDescription(this, (CameraPreviewGrid.GridType)this.m_Settings.getEnum("Grid.Type", CameraPreviewGrid.GridType.class)));
    this.m_GridMenuItem.set(MenuItem.PROP_DIVIDER_STYLE, MenuItem.DividerStyle.INDENTED);
    this.m_MenuItems.add(this.m_GridMenuItem);
    if (!this.m_IsServiceMode)
    {
      this.m_WatermarkMenuItem = new MenuItem();
      this.m_WatermarkMenuItem.set(MenuItem.PROP_TITLE, getString(2131558440));
      this.m_WatermarkMenuItem.set(MenuItem.PROP_SUBTITLE, getString(2131558441));
      if ((this.m_HasRawCaptureSetting) || (this.m_HasSmileCaptureSetting) || (this.m_HasVideoFrameRateSetting)) {
        this.m_WatermarkMenuItem.set(MenuItem.PROP_DIVIDER_STYLE, MenuItem.DividerStyle.INDENTED);
      }
      this.m_MenuItems.add(this.m_WatermarkMenuItem);
    }
    if (this.m_HasRawCaptureSetting)
    {
      this.m_RawCaptureMenuItem = new BooleanSettingsMenuItem(this.m_Settings, "RawCapture");
      this.m_RawCaptureMenuItem.set(MenuItem.PROP_TITLE, getString(2131558427));
      if ((this.m_HasSmileCaptureSetting) || (this.m_HasVideoFrameRateSetting)) {
        this.m_RawCaptureMenuItem.set(MenuItem.PROP_DIVIDER_STYLE, MenuItem.DividerStyle.INDENTED);
      }
      this.m_MenuItems.add(this.m_RawCaptureMenuItem);
    }
    if (this.m_HasSmileCaptureSetting) {
      if (!this.m_Settings.getString("CameraLensFacing").toLowerCase().equals("front")) {
        break label1333;
      }
    }
    label1333:
    for (localObject = "SmileCapture.Front";; localObject = "SmileCapture.Back")
    {
      this.m_SmileCaptureMenuItem = new BooleanSettingsMenuItem(this.m_Settings, (String)localObject);
      this.m_SmileCaptureMenuItem.set(MenuItem.PROP_TITLE, getString(2131558431));
      if (this.m_HasVideoFrameRateSetting) {
        this.m_SmileCaptureMenuItem.set(MenuItem.PROP_DIVIDER_STYLE, MenuItem.DividerStyle.INDENTED);
      }
      this.m_MenuItems.add(this.m_SmileCaptureMenuItem);
      if (this.m_HasVideoFrameRateSetting)
      {
        this.m_VideoFrameRateMenuItem = new BooleanSettingsMenuItem(this.m_Settings, "VideoFrameRate");
        this.m_VideoFrameRateMenuItem.set(MenuItem.PROP_TITLE, getString(2131558433));
        this.m_MenuItems.add(this.m_VideoFrameRateMenuItem);
      }
      if (this.m_IsMirrorSupported)
      {
        localObject = new DividerMenuItem();
        ((DividerMenuItem)localObject).set(MenuItem.PROP_TITLE, getString(2131558477));
        this.m_MenuItems.add(localObject);
        this.m_IsMirroredMenuItem = new BooleanSettingsMenuItem(this.m_Settings, "IsMirrored");
        this.m_IsMirroredMenuItem.set(MenuItem.PROP_TITLE, getString(2131558418));
        this.m_MenuItems.add(this.m_IsMirroredMenuItem);
      }
      if (this.m_HasBokehOriginalSetting)
      {
        localObject = new DividerMenuItem();
        ((DividerMenuItem)localObject).set(MenuItem.PROP_TITLE, getString(2131558415));
        this.m_MenuItems.add(localObject);
        this.m_BokehOriginalMenuItem = new BooleanSettingsMenuItem(this.m_Settings, "BokehOriginalPicture");
        this.m_BokehOriginalMenuItem.set(MenuItem.PROP_TITLE, getString(2131558416));
        this.m_BokehOriginalMenuItem.set(MenuItem.PROP_SUBTITLE, getString(2131558417));
        this.m_MenuItems.add(this.m_BokehOriginalMenuItem);
      }
      if (this.m_HasManualCaptureSetting)
      {
        localObject = new DividerMenuItem();
        ((DividerMenuItem)localObject).set(MenuItem.PROP_TITLE, getString(2131558471));
        this.m_MenuItems.add(localObject);
        if (this.m_HasActivePictureInfoSetting)
        {
          this.m_ManualPictureInformationItem = new BooleanSettingsMenuItem(this.m_Settings, "PictureInformation");
          this.m_ManualPictureInformationItem.set(MenuItem.PROP_TITLE, getString(2131558437));
          this.m_ManualPictureInformationItem.set(MenuItem.PROP_DIVIDER_STYLE, MenuItem.DividerStyle.INDENTED);
          this.m_MenuItems.add(this.m_ManualPictureInformationItem);
        }
        this.m_ManualHistogramItem = new BooleanSettingsMenuItem(this.m_Settings, "Histogram");
        this.m_ManualHistogramItem.set(MenuItem.PROP_TITLE, getString(2131558439));
        this.m_ManualHistogramItem.set(MenuItem.PROP_DIVIDER_STYLE, MenuItem.DividerStyle.INDENTED);
        this.m_MenuItems.add(this.m_ManualHistogramItem);
        this.m_ManualHorizontalLineItem = new BooleanSettingsMenuItem(this.m_Settings, "HorizontalReferenceLine");
        this.m_ManualHorizontalLineItem.set(MenuItem.PROP_TITLE, getString(2131558438));
        this.m_ManualHorizontalLineItem.set(MenuItem.PROP_DIVIDER_STYLE, MenuItem.DividerStyle.INDENTED);
        this.m_MenuItems.add(this.m_ManualHorizontalLineItem);
        this.m_SimpleUIModeMenuItem = new BooleanSettingsMenuItem(this.m_Settings, "ManualModeUI.IsSimpleUIModeEnabled");
        this.m_SimpleUIModeMenuItem.set(MenuItem.PROP_TITLE, getString(2131558429));
        this.m_SimpleUIModeMenuItem.set(MenuItem.PROP_SUBTITLE, getString(2131558430));
        this.m_MenuItems.add(this.m_SimpleUIModeMenuItem);
      }
      localObject = new DividerMenuItem();
      ((DividerMenuItem)localObject).set(MenuItem.PROP_TITLE, getString(2131558436));
      this.m_MenuItems.add(localObject);
      this.m_AboutMenuItem = new AboutMenuItem(this);
      this.m_AboutMenuItem.set(MenuItem.PROP_TITLE, getString(2131558435));
      this.m_AboutMenuItem.set(MenuItem.PROP_SUBTITLE, getString(2131558533, new Object[] { this.m_VersionName }));
      this.m_MenuItems.add(this.m_AboutMenuItem);
      this.m_MenuListView.setMenuItems(this.m_MenuItems);
      return;
    }
  }
  
  private void setupUI()
  {
    int i = getThemeColor("adv_settings_action_bar_background");
    this.m_ActionBar = ((Toolbar)findViewById(2131361815));
    this.m_ActionBar.setBackgroundColor(i);
    findViewById(2131361819).setVisibility(0);
    i = new ScreenSize(this, true).getStatusBarSize();
    this.m_TemporaryStatusBarSpacingView = findViewById(2131361814);
    this.m_TemporaryStatusBarSpacingView.getLayoutParams().height = i;
    int j = getThemeColor("adv_settings_status_bar_background");
    this.m_TemporaryStatusBarSpacingView.setBackgroundColor(j);
    ((RelativeLayout.LayoutParams)this.m_ActionBar.getLayoutParams()).setMargins(0, i, 0, 0);
    i = getWindow().getDecorView().getSystemUiVisibility() | 0x400;
    switch (-getcom-oneplus-base-BaseActivity$ThemeModeSwitchesValues()[((BaseActivity.ThemeMode)get(PROP_THEME_MODE)).ordinal()])
    {
    }
    for (;;)
    {
      getWindow().getDecorView().setSystemUiVisibility(i);
      getWindow().setStatusBarColor(j);
      if (isSecureMode()) {
        getWindow().addFlags(524288);
      }
      this.m_ActionBarTitle = ((TextView)findViewById(2131361818));
      this.m_ActionBarTitle.setTextColor(getThemeColor("adv_settings_action_bar_title"));
      this.m_BackButton = ((ImageButton)findViewById(2131361817));
      Drawable localDrawable = getDrawable(getThemeDrawableResId("actionbar_button_back"));
      localDrawable.setAutoMirrored(true);
      this.m_BackButton.setImageDrawable(localDrawable);
      this.m_BackButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          AdvancedSettingsActivity.this.onBackPressed();
        }
      });
      this.m_MenuListView = ((MenuListView)findViewById(2131361821));
      this.m_MenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
      {
        public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          AdvancedSettingsActivity.-wrap6(AdvancedSettingsActivity.this, (MenuItem)AdvancedSettingsActivity.-get1(AdvancedSettingsActivity.this).get(paramAnonymousInt));
        }
      });
      return;
      i &= 0xDFFF;
      continue;
      i |= 0x2000;
    }
  }
  
  private void unregisterReceivers()
  {
    if ((isSecureMode()) && (this.m_ShutdownReceiver != null))
    {
      Log.v(TAG, "unregisterReceivers() - Shutdown receiver");
      unregisterReceiver(this.m_ShutdownReceiver);
      this.m_ShutdownReceiver = null;
    }
  }
  
  public void onBackPressed()
  {
    if ((closeGridFragment()) || (closeAgreementFragment())) {}
    while ((closeAboutFragment()) || (closeWatermarkFragment(true))) {
      return;
    }
    finish();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    Log.v(TAG, "onCreate() - Activity: ", this);
    this.m_ThemeMode = ((BaseActivity.ThemeMode)get(PROP_THEME_MODE));
    if (this.m_ThemeMode == BaseActivity.ThemeMode.DARK) {
      setTheme(2131492894);
    }
    for (;;)
    {
      setContentView(2130903045);
      setupUI();
      registerReceivers();
      Bundle localBundle = getIntent().getExtras();
      this.m_Settings = new Settings(this, localBundle.getString("Settings.Name"), localBundle.getBoolean("Settings.IsVolatile", false));
      this.m_HasActivePictureInfoSetting = localBundle.getBoolean("IsActivePictureInfoOptionsVisible", false);
      this.m_HasBokehOriginalSetting = localBundle.getBoolean("IsBokehOriginalPictureSupported", false);
      this.m_HasManualCaptureSetting = localBundle.getBoolean("IsManualCaptureOptionsVisible", false);
      this.m_HasRawCaptureSetting = false;
      this.m_HasVideoFrameRateSetting = localBundle.getBoolean("IsVideFrameRateVisible", false);
      this.m_OutputUriStr = localBundle.getString("OutputUri", "");
      this.m_StorageManager = ((StorageManager)CameraApplication.current().findComponent(StorageManager.class));
      if (this.m_StorageManager != null)
      {
        this.m_StorageManagerCallBack = new PropertyChangedCallback()
        {
          public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<List<Storage>> paramAnonymousPropertyKey, PropertyChangeEventArgs<List<Storage>> paramAnonymousPropertyChangeEventArgs)
          {
            AdvancedSettingsActivity.-wrap5(AdvancedSettingsActivity.this);
            AdvancedSettingsActivity.-get2(AdvancedSettingsActivity.this).setMenuItems(AdvancedSettingsActivity.-get1(AdvancedSettingsActivity.this));
          }
        };
        this.m_StorageManager.addCallback(StorageManager.PROP_STORAGE_LIST, this.m_StorageManagerCallBack);
      }
      this.m_IsMirrorSupported = localBundle.getBoolean("IsMirrorSupported", false);
      this.m_IsServiceMode = localBundle.getBoolean("IsServiceMode", false);
      try
      {
        this.m_VersionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        if (this.m_VersionName.length() > 5) {
          this.m_VersionName = this.m_VersionName.substring(0, 5);
        }
        setupMenuItems();
        if (paramBundle != null) {
          this.m_FragmentMode = paramBundle.getInt("FragmentMode");
        }
        return;
        if (this.m_ThemeMode == BaseActivity.ThemeMode.ANDROID)
        {
          setTheme(2131492886);
          continue;
        }
        setTheme(2131492885);
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        for (;;)
        {
          Log.e(TAG, "onCreate - getPackageInfo failed");
        }
      }
    }
  }
  
  protected void onDestroy()
  {
    Log.v(TAG, "onDestroy() - Activity: " + this);
    unregisterReceivers();
    if (this.m_StorageManager != null) {
      this.m_StorageManager.removeCallback(StorageManager.PROP_STORAGE_LIST, this.m_StorageManagerCallBack);
    }
    super.onDestroy();
  }
  
  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    return super.onKeyUp(paramInt, paramKeyEvent);
  }
  
  protected void onResume()
  {
    if (this.m_ThemeMode != get(PROP_THEME_MODE)) {
      recreate();
    }
    this.m_LocationMenuItem.set(MenuItem.PROP_IS_CHECKED, Boolean.valueOf(this.m_Settings.getBoolean("Location.Save")));
    this.m_ShutterSoundMenuItem.set(MenuItem.PROP_IS_CHECKED, Boolean.valueOf(this.m_Settings.getBoolean("ShutterSound")));
    this.m_QuickCaptureMenuItem.set(MenuItem.PROP_IS_CHECKED, Boolean.valueOf(this.m_Settings.getBoolean("IsQuickCaptureEnabled")));
    if (this.m_HasBokehOriginalSetting) {
      this.m_BokehOriginalMenuItem.set(MenuItem.PROP_IS_CHECKED, Boolean.valueOf(this.m_Settings.getBoolean("BokehOriginalPicture")));
    }
    if (this.m_HasRawCaptureSetting) {
      this.m_RawCaptureMenuItem.set(MenuItem.PROP_IS_CHECKED, Boolean.valueOf(this.m_Settings.getBoolean("RawCapture")));
    }
    if (this.m_HasSmileCaptureSetting) {
      if (!this.m_Settings.getString("CameraLensFacing").toLowerCase().equals("front")) {
        break label418;
      }
    }
    label418:
    for (String str = "SmileCapture.Front";; str = "SmileCapture.Back")
    {
      this.m_SmileCaptureMenuItem.set(MenuItem.PROP_IS_CHECKED, Boolean.valueOf(this.m_Settings.getBoolean(str)));
      if (this.m_HasVideoFrameRateSetting) {
        this.m_VideoFrameRateMenuItem.set(MenuItem.PROP_IS_CHECKED, Boolean.valueOf(this.m_Settings.getBoolean("VideoFrameRate")));
      }
      if (this.m_HasManualCaptureSetting)
      {
        if (this.m_ManualPictureInformationItem != null) {
          this.m_ManualPictureInformationItem.set(MenuItem.PROP_IS_CHECKED, Boolean.valueOf(this.m_Settings.getBoolean("PictureInformation")));
        }
        this.m_ManualHorizontalLineItem.set(MenuItem.PROP_IS_CHECKED, Boolean.valueOf(this.m_Settings.getBoolean("HorizontalReferenceLine")));
        this.m_ManualHistogramItem.set(MenuItem.PROP_IS_CHECKED, Boolean.valueOf(this.m_Settings.getBoolean("Histogram")));
        this.m_SimpleUIModeMenuItem.set(MenuItem.PROP_IS_CHECKED, Boolean.valueOf(this.m_Settings.getBoolean("ManualModeUI.IsSimpleUIModeEnabled")));
      }
      this.m_GridMenuItem.set(MenuItem.PROP_SUBTITLE, getGridTypeDescription(this, (CameraPreviewGrid.GridType)this.m_Settings.getEnum("Grid.Type", CameraPreviewGrid.GridType.class)));
      handleStorageMenuItem();
      if (this.m_IsMirrorSupported) {
        this.m_IsMirroredMenuItem.set(MenuItem.PROP_IS_CHECKED, Boolean.valueOf(this.m_Settings.getBoolean("IsMirrored")));
      }
      super.onResume();
      return;
    }
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    paramBundle.putInt("FragmentMode", this.m_FragmentMode);
    super.onSaveInstanceState(paramBundle);
  }
  
  public void onTitleChanged(int paramInt)
  {
    if (this.m_ActionBarTitle != null) {
      this.m_ActionBarTitle.setText(paramInt);
    }
  }
  
  public static class AboutFragment
    extends Fragment
  {
    private static final char[] COPYRIGHT_YEAR_TEXT = { 50, 48, 49, 55 };
    private static final Pattern COPYRIGHT_YEAR_TEXT_PATTERN = Pattern.compile("[\\d]{4}");
    private static final String EXTRA_VERSION = "VersionName";
    private OnActionBarTitleChangedListener m_OnActionBarTitleChangedListener;
    private String m_VersionName;
    
    public AboutFragment() {}
    
    public AboutFragment(String paramString)
    {
      this.m_VersionName = paramString;
    }
    
    public void onAttach(Context paramContext)
    {
      super.onAttach(paramContext);
      if ((paramContext instanceof OnActionBarTitleChangedListener)) {
        this.m_OnActionBarTitleChangedListener = ((OnActionBarTitleChangedListener)paramContext);
      }
    }
    
    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
    {
      paramLayoutInflater = paramLayoutInflater.inflate(2130903040, paramViewGroup, false);
      if (this.m_OnActionBarTitleChangedListener != null) {
        this.m_OnActionBarTitleChangedListener.onTitleChanged(2131558435);
      }
      paramViewGroup = (TextView)paramLayoutInflater.findViewById(2131361804);
      if (paramBundle != null) {
        this.m_VersionName = paramBundle.getString("VersionName", this.m_VersionName);
      }
      paramViewGroup.setText(getString(2131558533, new Object[] { this.m_VersionName }));
      paramViewGroup = new StringBuilder(getString(2131558535));
      paramBundle = COPYRIGHT_YEAR_TEXT_PATTERN.matcher(paramViewGroup);
      if (paramBundle.find())
      {
        int i = paramBundle.start();
        int j = 0;
        while (j < COPYRIGHT_YEAR_TEXT.length)
        {
          paramViewGroup.setCharAt(i, COPYRIGHT_YEAR_TEXT[j]);
          j += 1;
          i += 1;
        }
      }
      ((TextView)paramLayoutInflater.findViewById(2131361806)).setText(paramViewGroup);
      paramLayoutInflater.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          return true;
        }
      });
      ((TextView)paramLayoutInflater.findViewById(2131361805)).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          AdvancedSettingsActivity.-wrap7((AdvancedSettingsActivity)AdvancedSettingsActivity.AboutFragment.this.getActivity());
        }
      });
      return paramLayoutInflater;
    }
    
    public void onSaveInstanceState(Bundle paramBundle)
    {
      paramBundle.putString("VersionName", this.m_VersionName);
      super.onSaveInstanceState(paramBundle);
    }
  }
  
  public static class AgreementFragment
    extends Fragment
  {
    private static final String AGREEMENT_URL_CHS = "file:///android_asset/copyright_chs.htm";
    private static final String AGREEMENT_URL_CHT = "file:///android_asset/copyright_cht.htm";
    private static final String AGREEMENT_URL_EN = "file:///android_asset/copyright.htm";
    private OnActionBarTitleChangedListener m_OnActionBarTitleChangedListener;
    private WebView m_WebView;
    private String m_language;
    
    public void onAttach(Context paramContext)
    {
      super.onAttach(paramContext);
      if ((paramContext instanceof OnActionBarTitleChangedListener)) {
        this.m_OnActionBarTitleChangedListener = ((OnActionBarTitleChangedListener)paramContext);
      }
    }
    
    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
    {
      paramBundle = paramLayoutInflater.inflate(2130903041, paramViewGroup, false);
      if (this.m_OnActionBarTitleChangedListener != null) {
        this.m_OnActionBarTitleChangedListener.onTitleChanged(2131558534);
      }
      this.m_WebView = ((WebView)paramBundle.findViewById(2131361807));
      this.m_WebView.getSettings().setBuiltInZoomControls(false);
      this.m_WebView.setWebViewClient(new CustomWebViewClient(null));
      this.m_language = Locale.getDefault().toString();
      String[] arrayOfString = this.m_language.split("_");
      paramViewGroup = "file:///android_asset/copyright.htm";
      paramLayoutInflater = paramViewGroup;
      if (arrayOfString[0].equals("zh"))
      {
        if ((arrayOfString.length <= 2) || (arrayOfString[2] == null)) {
          break label150;
        }
        if (!arrayOfString[2].equals("#Hans")) {
          break label144;
        }
        paramLayoutInflater = "file:///android_asset/copyright_chs.htm";
      }
      for (;;)
      {
        this.m_WebView.loadUrl(paramLayoutInflater);
        return paramBundle;
        label144:
        paramLayoutInflater = "file:///android_asset/copyright_cht.htm";
        continue;
        label150:
        if (arrayOfString[1].equals("CN"))
        {
          paramLayoutInflater = "file:///android_asset/copyright_chs.htm";
        }
        else if (!arrayOfString[1].equals("HK"))
        {
          paramLayoutInflater = paramViewGroup;
          if (!arrayOfString[1].equals("TW")) {}
        }
        else
        {
          paramLayoutInflater = "file:///android_asset/copyright_cht.htm";
        }
      }
    }
    
    private class CustomWebViewClient
      extends WebViewClient
    {
      private static final String TEL_PREFIX = "tel:";
      
      private CustomWebViewClient() {}
      
      public boolean shouldOverrideUrlLoading(WebView paramWebView, String paramString)
      {
        if (paramString.startsWith("tel:"))
        {
          paramWebView = new Intent("android.intent.action.DIAL");
          paramWebView.setData(Uri.parse(paramString));
          AdvancedSettingsActivity.AgreementFragment.this.startActivity(paramWebView);
          return true;
        }
        return false;
      }
    }
  }
  
  public static class AlertDialogFragment
    extends DialogFragment
  {
    public AlertDialogFragment()
    {
      setRetainInstance(true);
    }
    
    public void onCancel(DialogInterface paramDialogInterface)
    {
      dismissAllowingStateLoss();
    }
    
    public Dialog onCreateDialog(Bundle paramBundle)
    {
      if (AdvancedSettingsActivity.-get4((AdvancedSettingsActivity)getActivity()) == BaseActivity.ThemeMode.DARK) {}
      for (int i = 2131492908;; i = 2131492906) {
        new AlertDialog.Builder(getActivity(), i).setMessage(2131558445).setPositiveButton(17039370, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            AdvancedSettingsActivity.AlertDialogFragment.this.dismiss();
            AdvancedSettingsActivity.-wrap0((AdvancedSettingsActivity)AdvancedSettingsActivity.AlertDialogFragment.this.getActivity(), false);
          }
        }).setNegativeButton(17039360, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            AdvancedSettingsActivity.AlertDialogFragment.this.dismiss();
          }
        }).create();
      }
    }
    
    public void onDismiss(DialogInterface paramDialogInterface) {}
  }
  
  public static final class GridFragment
    extends Fragment
  {
    private MenuListView m_GridTypeListView;
    private final List<MenuItem> m_GridTypeMenuItems = new ArrayList();
    private final PropertyChangedCallback<Boolean> m_IsCheckedChangedCB = new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        AdvancedSettingsActivity.GridFragment.-wrap0(AdvancedSettingsActivity.GridFragment.this, (MenuItem)paramAnonymousPropertySource, ((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue());
      }
    };
    private int m_MenuItemUpdateCounter;
    private View m_Preview;
    private CameraPreviewGridDrawable m_PreviewDrawable;
    
    private void checkCurrentGridType(CameraPreviewGrid.GridType paramGridType)
    {
      this.m_MenuItemUpdateCounter += 1;
      Iterator localIterator = this.m_GridTypeMenuItems.iterator();
      if (localIterator.hasNext())
      {
        MenuItem localMenuItem = (MenuItem)localIterator.next();
        PropertyKey localPropertyKey = MenuItem.PROP_IS_CHECKED;
        if (localMenuItem.getTag() == paramGridType) {}
        for (boolean bool = true;; bool = false)
        {
          localMenuItem.set(localPropertyKey, Boolean.valueOf(bool));
          break;
        }
      }
      this.m_MenuItemUpdateCounter -= 1;
    }
    
    private void onMenuItemCheckedChanged(MenuItem paramMenuItem, boolean paramBoolean)
    {
      if (this.m_MenuItemUpdateCounter > 0) {
        return;
      }
      if (paramBoolean) {
        onMenuItemClicked(paramMenuItem);
      }
    }
    
    private void onMenuItemClicked(MenuItem paramMenuItem)
    {
      paramMenuItem = (CameraPreviewGrid.GridType)paramMenuItem.getTag();
      AdvancedSettingsActivity.-get3((AdvancedSettingsActivity)getActivity()).set("Grid.Type", paramMenuItem);
      checkCurrentGridType(paramMenuItem);
      if (this.m_PreviewDrawable != null) {
        this.m_PreviewDrawable.setGridType(paramMenuItem);
      }
    }
    
    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
    {
      paramLayoutInflater = paramLayoutInflater.inflate(2130903042, null);
      this.m_Preview = paramLayoutInflater.findViewById(2131361808);
      this.m_GridTypeListView = ((MenuListView)paramLayoutInflater.findViewById(2131361809));
      paramViewGroup = (CameraPreviewGrid.GridType)AdvancedSettingsActivity.-get3((AdvancedSettingsActivity)getActivity()).getEnum("Grid.Type", CameraPreviewGrid.GridType.class, CameraPreviewGrid.GridType.NONE);
      if (this.m_PreviewDrawable == null)
      {
        this.m_PreviewDrawable = new CameraPreviewGridDrawable(getActivity());
        this.m_PreviewDrawable.setGridType(paramViewGroup);
      }
      this.m_Preview.setForeground(this.m_PreviewDrawable);
      if (this.m_GridTypeMenuItems.isEmpty())
      {
        paramBundle = getActivity();
        CameraPreviewGrid.GridType[] arrayOfGridType = CameraPreviewGrid.GridType.values();
        int i = 0;
        int j = arrayOfGridType.length;
        while (i < j)
        {
          CameraPreviewGrid.GridType localGridType = arrayOfGridType[i];
          RadioMenuItem localRadioMenuItem = new RadioMenuItem();
          localRadioMenuItem.set(MenuItem.PROP_TITLE, AdvancedSettingsActivity.-wrap3(paramBundle, localGridType));
          localRadioMenuItem.setTag(localGridType);
          localRadioMenuItem.addCallback(MenuItem.PROP_IS_CHECKED, this.m_IsCheckedChangedCB);
          this.m_GridTypeMenuItems.add(localRadioMenuItem);
          i += 1;
        }
      }
      this.m_GridTypeListView.setMenuItems(this.m_GridTypeMenuItems);
      this.m_GridTypeListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
      {
        public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          AdvancedSettingsActivity.GridFragment.-wrap1(AdvancedSettingsActivity.GridFragment.this, (MenuItem)AdvancedSettingsActivity.GridFragment.-get0(AdvancedSettingsActivity.GridFragment.this).get(paramAnonymousInt));
        }
      });
      checkCurrentGridType(paramViewGroup);
      paramLayoutInflater.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView) {}
      });
      paramViewGroup = getActivity();
      if ((paramViewGroup instanceof OnActionBarTitleChangedListener)) {
        ((OnActionBarTitleChangedListener)paramViewGroup).onTitleChanged(2131558419);
      }
      return paramLayoutInflater;
    }
  }
  
  public static final class WatermarkFragment
    extends Fragment
  {
    private View m_BottomDivider;
    private View m_MenuContainer;
    private EditText m_NameEditText;
    private TextView m_NameHintText;
    private View m_NameItemContainer;
    private Switch m_NameSwitch;
    private TextView m_NameTitle;
    private SloganWatermarkDrawable m_SloganDrawable;
    private View m_SloganView;
    private View m_WatermarkItemContainer;
    private Switch m_WatermarkSwitch;
    
    private void hideEditTextKeyboard()
    {
      ((InputMethodManager)getActivity().getSystemService("input_method")).hideSoftInputFromWindow(this.m_NameEditText.getWindowToken(), 0);
      if (this.m_NameEditText != null) {
        this.m_NameEditText.clearFocus();
      }
    }
    
    private void onNameSwitchChanged()
    {
      updateSloganDrawable();
      updateEditTextVisibility();
      updateEditTextEnableState();
      AdvancedSettingsActivity.-wrap2((AdvancedSettingsActivity)getActivity()).set("Watermark.Slogan.Author.Enabled", Boolean.valueOf(this.m_NameSwitch.isChecked()));
    }
    
    private void onNameTextChanged(CharSequence paramCharSequence)
    {
      updateSloganDrawable();
      updateEditTextVisibility();
      AdvancedSettingsActivity.-wrap2((AdvancedSettingsActivity)getActivity()).set("Watermark.Slogan.Author", paramCharSequence);
    }
    
    private void onWatermarkSwitchChanged()
    {
      if ((!this.m_WatermarkSwitch.isChecked()) && (this.m_NameEditText.getText().length() == 0)) {
        this.m_NameSwitch.setChecked(false);
      }
      updateSloganDrawable();
      updateNameItemEnableState();
      updateEditTextEnableState();
      AdvancedSettingsActivity localAdvancedSettingsActivity = (AdvancedSettingsActivity)getActivity();
      if (this.m_WatermarkSwitch.isChecked())
      {
        AdvancedSettingsActivity.-wrap2(localAdvancedSettingsActivity).set("Watermark", Watermark.SLOGAN);
        return;
      }
      AdvancedSettingsActivity.-wrap2(localAdvancedSettingsActivity).set("Watermark", Watermark.NONE);
    }
    
    private void updateEditTextEnableState()
    {
      if ((this.m_WatermarkSwitch.isChecked()) && (this.m_NameSwitch.isChecked()))
      {
        this.m_NameHintText.setEnabled(true);
        this.m_NameEditText.setEnabled(true);
        return;
      }
      this.m_NameHintText.setEnabled(false);
      this.m_NameEditText.setEnabled(false);
    }
    
    private void updateEditTextVisibility()
    {
      if (this.m_NameEditText.getText().length() > 0)
      {
        this.m_NameHintText.setVisibility(0);
        this.m_NameEditText.setVisibility(0);
        this.m_BottomDivider.setVisibility(8);
        return;
      }
      this.m_NameHintText.setVisibility(8);
      if (this.m_NameSwitch.isChecked())
      {
        this.m_NameEditText.setVisibility(0);
        this.m_BottomDivider.setVisibility(8);
        return;
      }
      this.m_NameEditText.setVisibility(8);
      this.m_BottomDivider.setVisibility(0);
    }
    
    private void updateNameItemEnableState()
    {
      if (this.m_WatermarkSwitch.isChecked())
      {
        this.m_NameItemContainer.setEnabled(true);
        this.m_NameTitle.setEnabled(true);
        this.m_NameSwitch.setEnabled(true);
        return;
      }
      this.m_NameItemContainer.setEnabled(false);
      this.m_NameTitle.setEnabled(false);
      this.m_NameSwitch.setEnabled(false);
    }
    
    private void updateSloganDrawable()
    {
      if (this.m_WatermarkSwitch.isChecked())
      {
        String str = this.m_NameEditText.getText().toString();
        if ((!str.isEmpty()) && (this.m_NameSwitch.isChecked())) {
          this.m_SloganDrawable.setSubtitleText(str);
        }
        for (;;)
        {
          this.m_SloganView.setBackground(this.m_SloganDrawable);
          return;
          this.m_SloganDrawable.setSubtitleText(null);
        }
      }
      this.m_SloganView.setBackground(null);
    }
    
    public boolean isEditTextEmpty()
    {
      String str = this.m_NameEditText.getText().toString().trim();
      return (this.m_NameSwitch.isChecked()) && (str.isEmpty());
    }
    
    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
    {
      paramLayoutInflater = paramLayoutInflater.inflate(2130903046, null);
      this.m_SloganView = paramLayoutInflater.findViewById(2131361825);
      this.m_MenuContainer = ((ViewStub)paramLayoutInflater.findViewById(2131361826)).inflate();
      this.m_WatermarkItemContainer = this.m_MenuContainer.findViewById(2131361846);
      this.m_WatermarkSwitch = ((Switch)this.m_MenuContainer.findViewById(2131361848));
      this.m_NameItemContainer = this.m_MenuContainer.findViewById(2131361849);
      this.m_NameTitle = ((TextView)this.m_MenuContainer.findViewById(2131361850));
      this.m_NameSwitch = ((Switch)this.m_MenuContainer.findViewById(2131361851));
      this.m_NameHintText = ((TextView)this.m_MenuContainer.findViewById(2131361852));
      this.m_NameEditText = ((EditText)this.m_MenuContainer.findViewById(2131361853));
      this.m_BottomDivider = this.m_MenuContainer.findViewById(2131361854);
      if (this.m_SloganDrawable == null) {
        this.m_SloganDrawable = new SloganWatermarkDrawable();
      }
      paramViewGroup = (AdvancedSettingsActivity)getActivity();
      paramBundle = (Watermark)AdvancedSettingsActivity.-wrap2(paramViewGroup).getEnum("Watermark", Watermark.class, Watermark.NONE);
      boolean bool2 = AdvancedSettingsActivity.-wrap2(paramViewGroup).getBoolean("Watermark.Slogan.Author.Enabled", false);
      String str = AdvancedSettingsActivity.-wrap2(paramViewGroup).getString("Watermark.Slogan.Author", "");
      Log.v(AdvancedSettingsActivity.-get0(), "onCreateView() - Watermark: ", paramBundle, ", author enabled: ", Boolean.valueOf(bool2), ", author: ", str);
      Switch localSwitch = this.m_WatermarkSwitch;
      if (paramBundle != Watermark.NONE) {}
      for (boolean bool1 = true;; bool1 = false)
      {
        localSwitch.setChecked(bool1);
        this.m_NameSwitch.setChecked(bool2);
        this.m_NameEditText.setText(str);
        updateSloganDrawable();
        updateNameItemEnableState();
        updateEditTextVisibility();
        updateEditTextEnableState();
        paramLayoutInflater.setOnTouchListener(new View.OnTouchListener()
        {
          public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
          {
            switch (paramAnonymousMotionEvent.getAction())
            {
            }
            for (;;)
            {
              return true;
              AdvancedSettingsActivity.WatermarkFragment.-wrap0(AdvancedSettingsActivity.WatermarkFragment.this);
            }
          }
        });
        this.m_NameItemContainer.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            paramAnonymousView = AdvancedSettingsActivity.WatermarkFragment.-get1(AdvancedSettingsActivity.WatermarkFragment.this);
            if (AdvancedSettingsActivity.WatermarkFragment.-get1(AdvancedSettingsActivity.WatermarkFragment.this).isChecked()) {}
            for (boolean bool = false;; bool = true)
            {
              paramAnonymousView.setChecked(bool);
              return;
            }
          }
        });
        this.m_WatermarkItemContainer.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            paramAnonymousView = AdvancedSettingsActivity.WatermarkFragment.-get2(AdvancedSettingsActivity.WatermarkFragment.this);
            if (AdvancedSettingsActivity.WatermarkFragment.-get2(AdvancedSettingsActivity.WatermarkFragment.this).isChecked()) {}
            for (boolean bool = false;; bool = true)
            {
              paramAnonymousView.setChecked(bool);
              return;
            }
          }
        });
        this.m_WatermarkSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
          public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
          {
            AdvancedSettingsActivity.WatermarkFragment.-wrap3(AdvancedSettingsActivity.WatermarkFragment.this);
          }
        });
        this.m_NameSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
          public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
          {
            AdvancedSettingsActivity.WatermarkFragment.-wrap1(AdvancedSettingsActivity.WatermarkFragment.this);
          }
        });
        this.m_NameEditText.addTextChangedListener(new TextWatcher()
        {
          public void afterTextChanged(Editable paramAnonymousEditable) {}
          
          public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
          
          public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
          {
            AdvancedSettingsActivity.WatermarkFragment.-wrap2(AdvancedSettingsActivity.WatermarkFragment.this, paramAnonymousCharSequence);
          }
        });
        if ((paramViewGroup instanceof OnActionBarTitleChangedListener)) {
          paramViewGroup.onTitleChanged(2131558440);
        }
        paramViewGroup.getHandler().postDelayed(new Runnable()
        {
          public void run()
          {
            if (AdvancedSettingsActivity.WatermarkFragment.this.getActivity() == null) {
              return;
            }
            AdvancedSettingsActivity.WatermarkFragment.-wrap0(AdvancedSettingsActivity.WatermarkFragment.this);
            AdvancedSettingsActivity.WatermarkFragment.-get0(AdvancedSettingsActivity.WatermarkFragment.this).requestFocus();
          }
        }, 100L);
        return paramLayoutInflater;
      }
    }
    
    public void onDestroyView()
    {
      Object localObject = this.m_NameEditText.getText().toString().trim();
      if ((this.m_NameSwitch.isChecked()) && (((String)localObject).isEmpty()))
      {
        localObject = (AdvancedSettingsActivity)getActivity();
        AdvancedSettingsActivity.-wrap2((AdvancedSettingsActivity)localObject).set("Watermark.Slogan.Author.Enabled", Boolean.valueOf(false));
        AdvancedSettingsActivity.-wrap2((AdvancedSettingsActivity)localObject).set("Watermark.Slogan.Author", "");
      }
      super.onDestroyView();
    }
    
    public void onStart()
    {
      super.onStart();
      Activity localActivity = getActivity();
      if ((localActivity instanceof AdvancedSettingsActivity)) {
        AdvancedSettingsActivity.-wrap4((AdvancedSettingsActivity)localActivity);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/AdvancedSettingsActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */