package com.oneplus.camera.ui.menu;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.Settings;
import com.oneplus.camera.media.Resolution;
import java.util.ArrayList;
import java.util.List;

public final class ResolutionMenuItem
  extends MenuItem
{
  public static final PropertyKey<Resolution> PROP_RESOLUTION = new PropertyKey("Resolution", Resolution.class, ResolutionMenuItem.class, 0, null);
  private final Context m_Context;
  private AdapterView.OnItemClickListener m_ItemClickListener = new AdapterView.OnItemClickListener()
  {
    public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
    {
      ResolutionMenuItem.this.set(ResolutionMenuItem.PROP_RESOLUTION, (Resolution)ResolutionMenuItem.-get1(ResolutionMenuItem.this).get(paramAnonymousInt));
      ResolutionMenuItem.-get2(ResolutionMenuItem.this).set(ResolutionMenuItem.-get0(ResolutionMenuItem.this), ((Resolution)ResolutionMenuItem.-get1(ResolutionMenuItem.this).get(paramAnonymousInt)).getKey());
    }
  };
  private final String m_ResoltuonSettingKey;
  private final String m_ResolutionStringFormat;
  private ResolutionWindow m_ResolutionWindow = null;
  private final List<Resolution> m_Resolutions;
  private final Settings m_Settings;
  
  public ResolutionMenuItem(Context paramContext, List<Resolution> paramList, String paramString, Settings paramSettings)
  {
    this.m_Context = paramContext;
    this.m_Resolutions = paramList;
    this.m_ResoltuonSettingKey = paramString;
    this.m_Settings = paramSettings;
    this.m_ResolutionStringFormat = paramContext.getString(2131558424);
  }
  
  private String getDisplayString(Resolution paramResolution)
  {
    if (paramResolution == null) {
      return "";
    }
    int i = paramResolution.getMegaPixels();
    switch (-getcom-oneplus-util-AspectRatioSwitchesValues()[paramResolution.getAspectRatio().ordinal()])
    {
    default: 
      paramResolution = "";
    }
    for (;;)
    {
      return String.format(this.m_ResolutionStringFormat, new Object[] { Integer.valueOf(i), paramResolution });
      paramResolution = "16:9";
      continue;
      paramResolution = "4:3";
      continue;
      paramResolution = "1:1";
    }
  }
  
  private boolean setResolutionProp(Resolution paramResolution)
  {
    if (paramResolution != null) {
      if ((this.m_Resolutions != null) && (this.m_Resolutions.contains(paramResolution))) {
        set(PROP_SUMMARY, getDisplayString(paramResolution));
      }
    }
    for (;;)
    {
      return super.set(PROP_RESOLUTION, paramResolution);
      return false;
      set(PROP_SUMMARY, null);
    }
  }
  
  public ResolutionWindow getResolutionWindow()
  {
    return this.m_ResolutionWindow;
  }
  
  public <TValue> boolean set(PropertyKey<TValue> paramPropertyKey, TValue paramTValue)
  {
    if (paramPropertyKey == PROP_RESOLUTION) {
      return setResolutionProp((Resolution)paramTValue);
    }
    return super.set(paramPropertyKey, paramTValue);
  }
  
  public final void showResolutionSelector()
  {
    if ((this.m_Resolutions == null) || (this.m_Resolutions.isEmpty()))
    {
      Log.e(this.TAG, "showResolutionSelector() - No resolution list");
      return;
    }
    int j = 0;
    ArrayList localArrayList = new ArrayList(this.m_Resolutions.size());
    int i = 0;
    while (i < this.m_Resolutions.size())
    {
      Resolution localResolution = (Resolution)this.m_Resolutions.get(i);
      if (getDisplayString(localResolution).equals(get(PROP_SUMMARY))) {
        j = i;
      }
      localArrayList.add(getDisplayString(localResolution));
      i += 1;
    }
    this.m_ResolutionWindow = new ResolutionWindow((Activity)this.m_Context, localArrayList, j);
    this.m_ResolutionWindow.showAtLocation(((Activity)this.m_Context).findViewById(2131361813), 81, 0, 0);
    this.m_ResolutionWindow.setFocusable(true);
    this.m_ResolutionWindow.setOnItemClickListener(this.m_ItemClickListener);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/menu/ResolutionMenuItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */