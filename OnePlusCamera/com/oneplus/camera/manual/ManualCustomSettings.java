package com.oneplus.camera.manual;

import android.content.Context;
import com.oneplus.base.Settings;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class ManualCustomSettings
  extends Settings
{
  public static final String MANUAL_AWB = "Manual.AWB";
  public static final String MANUAL_AWB_TICK = "Manual.AWB.TICK";
  public static final String MANUAL_COLOR_TEMPERATURE = "Manual.COLOR.TEMPERATURE";
  public static final String MANUAL_COLOR_TEMPERATURE_TICK = "Manual.COLOR.TEMPERATURE.TICK";
  public static final String MANUAL_EV = "Manual.EV";
  public static final String MANUAL_EV_TICK = "Manual.EV.TICK";
  public static final String MANUAL_EXPOSURE = "Manual.EXPOSURE";
  public static final String MANUAL_EXPOSURE_TICK = "Manual.EXPOSURE.TICK";
  public static final String MANUAL_FOCUS = "Manual.FOCUS";
  public static final String MANUAL_FOCUS_TICK = "Manual.FOCUS.TICK";
  public static final String MANUAL_ISO = "Manual.ISO";
  public static final String MANUAL_ISO_TICK = "Manual.ISO.TICK";
  public static final String MANUAL_SETTING_SAVED = "Manual.Saved";
  private Map<String, Integer> m_KeyValueCounters = new HashMap();
  private Map<String, Object> m_KeyValues = new HashMap();
  
  public ManualCustomSettings(Context paramContext, String paramString, boolean paramBoolean)
  {
    super(paramContext, paramString, paramBoolean);
  }
  
  private Object getManualCustomSetting(String paramString)
  {
    if (paramString.equals("Manual.AWB")) {
      return Integer.valueOf(getInt("Manual.AWB", 1));
    }
    if (paramString.equals("Manual.AWB.TICK")) {
      return Integer.valueOf(getInt("Manual.AWB.TICK"));
    }
    if (paramString.equals("Manual.COLOR.TEMPERATURE")) {
      return Integer.valueOf(getInt("Manual.COLOR.TEMPERATURE", 0));
    }
    if (paramString.equals("Manual.COLOR.TEMPERATURE.TICK")) {
      return Integer.valueOf(getInt("Manual.COLOR.TEMPERATURE.TICK"));
    }
    if (paramString.equals("Manual.ISO")) {
      return Integer.valueOf(getInt("Manual.ISO", -1));
    }
    if (paramString.equals("Manual.ISO.TICK")) {
      return Integer.valueOf(getInt("Manual.ISO.TICK"));
    }
    if (paramString.equals("Manual.EV")) {
      return Integer.valueOf(getInt("Manual.EV", 0));
    }
    if (paramString.equals("Manual.EV.TICK")) {
      return Integer.valueOf(getInt("Manual.EV.TICK"));
    }
    if (paramString.equals("Manual.EXPOSURE")) {
      return Long.valueOf(getLong("Manual.EXPOSURE", -1L));
    }
    if (paramString.equals("Manual.EXPOSURE.TICK")) {
      return Integer.valueOf(getInt("Manual.EXPOSURE.TICK"));
    }
    if (paramString.equals("Manual.FOCUS")) {
      return Integer.valueOf(getInt("Manual.FOCUS", -1));
    }
    if (paramString.equals("Manual.FOCUS.TICK")) {
      return Integer.valueOf(getInt("Manual.FOCUS.TICK"));
    }
    if (paramString.equals("RawCapture")) {
      return Boolean.valueOf(getBoolean("RawCapture", false));
    }
    if (paramString.equals("SelfTimer.Back")) {
      return Long.valueOf(getLong("SelfTimer.Back", 0L));
    }
    if (paramString.equals("Resolution.Photo.Back")) {
      return getString("Resolution.Photo.Back");
    }
    return null;
  }
  
  private boolean isManualCustomSettingKey(String paramString)
  {
    if (paramString.equals("Manual.AWB")) {}
    while ((paramString.equals("Manual.AWB.TICK")) || (paramString.equals("Manual.COLOR.TEMPERATURE")) || (paramString.equals("Manual.COLOR.TEMPERATURE.TICK")) || (paramString.equals("Manual.ISO")) || (paramString.equals("Manual.ISO.TICK")) || (paramString.equals("Manual.EV")) || (paramString.equals("Manual.EV.TICK")) || (paramString.equals("Manual.EXPOSURE")) || (paramString.equals("Manual.EXPOSURE.TICK")) || (paramString.equals("Manual.FOCUS")) || (paramString.equals("Manual.FOCUS.TICK")) || (paramString.equals("Manual.Saved")) || (paramString.equals("RawCapture")) || (paramString.equals("SelfTimer.Back")) || (paramString.equals("Resolution.Photo.Back"))) {
      return true;
    }
    return false;
  }
  
  public void abandonUnCommittedValues()
  {
    this.m_KeyValues.clear();
  }
  
  public void commitManualCustomSetting()
  {
    Object localObject = new HashMap(this.m_KeyValues);
    this.m_KeyValues.clear();
    localObject = ((Map)localObject).entrySet().iterator();
    while (((Iterator)localObject).hasNext())
    {
      Map.Entry localEntry = (Map.Entry)((Iterator)localObject).next();
      super.set((String)localEntry.getKey(), localEntry.getValue());
    }
  }
  
  public void copyCustomSettings(Settings paramSettings)
  {
    if (paramSettings == null) {
      return;
    }
    set("RawCapture", Boolean.valueOf(paramSettings.getBoolean("RawCapture")));
    set("Resolution.Photo.Back", paramSettings.getString("Resolution.Photo.Back"));
    set("SelfTimer.Back", Long.valueOf(paramSettings.getLong("SelfTimer.Back", 0L)));
    set("Manual.ISO", Integer.valueOf(paramSettings.getInt("Manual.ISO", -1)));
    set("Manual.AWB", Integer.valueOf(paramSettings.getInt("Manual.AWB", 1)));
    set("Manual.COLOR.TEMPERATURE", Integer.valueOf(paramSettings.getInt("Manual.COLOR.TEMPERATURE", 0)));
    set("Manual.EXPOSURE", Long.valueOf(paramSettings.getLong("Manual.EXPOSURE", -1L)));
    set("Manual.EV", Integer.valueOf(paramSettings.getInt("Manual.EV", 0)));
    set("Manual.FOCUS", Integer.valueOf(paramSettings.getInt("Manual.FOCUS", 55536)));
    set("Manual.ISO.TICK", Integer.valueOf(paramSettings.getInt("Manual.ISO.TICK")));
    set("Manual.AWB.TICK", Integer.valueOf(paramSettings.getInt("Manual.AWB.TICK")));
    set("Manual.COLOR.TEMPERATURE.TICK", Integer.valueOf(paramSettings.getInt("Manual.COLOR.TEMPERATURE.TICK")));
    set("Manual.EXPOSURE.TICK", Integer.valueOf(paramSettings.getInt("Manual.EXPOSURE.TICK")));
    set("Manual.EV.TICK", Integer.valueOf(paramSettings.getInt("Manual.EV.TICK")));
    set("Manual.FOCUS.TICK", Integer.valueOf(paramSettings.getInt("Manual.FOCUS.TICK")));
  }
  
  public boolean getBoolean(String paramString, boolean paramBoolean)
  {
    Object localObject = this.m_KeyValues.get(paramString);
    if ((localObject instanceof Boolean)) {
      return ((Boolean)localObject).booleanValue();
    }
    return super.getBoolean(paramString, paramBoolean);
  }
  
  public int getInt(String paramString, int paramInt)
  {
    Object localObject = this.m_KeyValues.get(paramString);
    if ((localObject instanceof Integer)) {
      return ((Integer)localObject).intValue();
    }
    return super.getInt(paramString, paramInt);
  }
  
  public long getLong(String paramString, long paramLong)
  {
    Object localObject = this.m_KeyValues.get(paramString);
    if ((localObject instanceof Long)) {
      return ((Long)localObject).longValue();
    }
    if ((localObject instanceof Integer)) {
      return ((Integer)localObject).intValue();
    }
    return super.getLong(paramString, paramLong);
  }
  
  public String getString(String paramString1, String paramString2)
  {
    Object localObject = this.m_KeyValues.get(paramString1);
    if ((localObject instanceof String)) {
      return localObject.toString();
    }
    return super.getString(paramString1, paramString2);
  }
  
  public boolean hasUnCommitedChanges()
  {
    return !this.m_KeyValues.isEmpty();
  }
  
  public boolean isPrivateKey(String paramString)
  {
    if (isManualCustomSettingKey(paramString)) {
      return true;
    }
    return super.isPrivateKey(paramString);
  }
  
  protected void onValueChanged(String paramString)
  {
    if ((this.m_KeyValueCounters.containsKey(paramString)) && (((Integer)this.m_KeyValueCounters.get(paramString)).intValue() > 0))
    {
      super.onValueChanged(paramString);
      return;
    }
    this.m_KeyValues.remove(paramString);
  }
  
  public void set(String paramString, Object paramObject)
  {
    if (isManualCustomSettingKey(paramString))
    {
      if (!this.m_KeyValues.containsKey(paramString))
      {
        localObject = getManualCustomSetting(paramString);
        if (paramObject != null)
        {
          if (!paramObject.equals(localObject)) {}
        }
        else if (localObject == null) {
          return;
        }
      }
      Object localObject = (Integer)this.m_KeyValueCounters.get(paramString);
      if (localObject != null) {
        this.m_KeyValueCounters.put(paramString, Integer.valueOf(((Integer)localObject).intValue() + 1));
      }
      for (;;)
      {
        this.m_KeyValues.put(paramString, paramObject);
        notifyValueChanged(paramString);
        paramObject = (Integer)this.m_KeyValueCounters.get(paramString);
        if (paramObject != null)
        {
          paramObject = Integer.valueOf(((Integer)paramObject).intValue() - 1);
          if (((Integer)paramObject).intValue() > 0) {
            break;
          }
          this.m_KeyValueCounters.remove(paramString);
        }
        return;
        this.m_KeyValueCounters.put(paramString, Integer.valueOf(1));
      }
      this.m_KeyValueCounters.put(paramString, paramObject);
      return;
    }
    super.set(paramString, paramObject);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/manual/ManualCustomSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */