package com.oneplus.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Message;
import android.preference.PreferenceManager;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;

public class Settings
  extends HandlerBaseObject
{
  public static final EventKey<SettingsValueChangedEventArgs> EVENT_VALUE_CHANGED = new EventKey("ValueChanged", SettingsValueChangedEventArgs.class, Settings.class);
  private static final Hashtable<String, Object> GLOBAL_DEFAULT_VALUES = new Hashtable();
  private static final ThreadLocal<SharedPreferences> GLOBAL_PREFS = new ThreadLocal();
  private static final int MSG_VALUE_CHANGED = 10000;
  private static final HashSet<String> PRIVATE_KEYS = new HashSet();
  private final SharedPreferences m_GlobalPreferences;
  private final boolean m_IsVolatile;
  private final String m_Name;
  private final SharedPreferences.OnSharedPreferenceChangeListener m_PreferenceChangedListener = new SharedPreferences.OnSharedPreferenceChangeListener()
  {
    public void onSharedPreferenceChanged(SharedPreferences paramAnonymousSharedPreferences, String paramAnonymousString)
    {
      if (Settings.this.isDependencyThread())
      {
        Settings.this.onValueChanged(paramAnonymousString);
        return;
      }
      HandlerUtils.sendMessage(Settings.this, 10000, 0, 0, paramAnonymousString);
    }
  };
  private final Hashtable<String, Object> m_PrivateDefaultValues;
  private final SharedPreferences m_PrivatePreferences;
  private final Hashtable<String, Object> m_PrivateVolatileValues;
  
  public Settings(Context paramContext, String paramString, boolean paramBoolean)
  {
    super(true);
    SharedPreferences localSharedPreferences2 = (SharedPreferences)GLOBAL_PREFS.get();
    SharedPreferences localSharedPreferences1 = localSharedPreferences2;
    if (localSharedPreferences2 == null)
    {
      localSharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(paramContext);
      GLOBAL_PREFS.set(localSharedPreferences1);
    }
    this.m_GlobalPreferences = localSharedPreferences1;
    if (paramString == null)
    {
      this.m_PrivatePreferences = this.m_GlobalPreferences;
      this.m_PrivateVolatileValues = null;
      this.m_PrivateDefaultValues = null;
    }
    for (;;)
    {
      this.m_GlobalPreferences.registerOnSharedPreferenceChangeListener(this.m_PreferenceChangedListener);
      if ((this.m_PrivatePreferences != null) && (this.m_PrivatePreferences != this.m_GlobalPreferences)) {
        this.m_PrivatePreferences.registerOnSharedPreferenceChangeListener(this.m_PreferenceChangedListener);
      }
      this.m_Name = paramString;
      this.m_IsVolatile = paramBoolean;
      return;
      if (!paramBoolean)
      {
        this.m_PrivatePreferences = paramContext.getSharedPreferences(paramString, 0);
        this.m_PrivateVolatileValues = null;
        this.m_PrivateDefaultValues = new Hashtable();
      }
      else
      {
        this.m_PrivatePreferences = null;
        this.m_PrivateVolatileValues = new Hashtable();
        this.m_PrivateDefaultValues = new Hashtable();
      }
    }
  }
  
  public static void addPrivateKey(String paramString)
  {
    synchronized (PRIVATE_KEYS)
    {
      PRIVATE_KEYS.add(paramString);
      return;
    }
  }
  
  private void set(SharedPreferences.Editor paramEditor, String paramString, Object paramObject)
  {
    if ((paramObject instanceof Boolean))
    {
      paramEditor.putBoolean(paramString, ((Boolean)paramObject).booleanValue());
      return;
    }
    if ((paramObject instanceof Integer))
    {
      paramEditor.putInt(paramString, ((Integer)paramObject).intValue());
      return;
    }
    if ((paramObject instanceof Long))
    {
      paramEditor.putLong(paramString, ((Long)paramObject).longValue());
      return;
    }
    if (paramObject != null)
    {
      paramEditor.putString(paramString, paramObject.toString());
      return;
    }
    paramEditor.remove(paramString);
  }
  
  public static void setGlobalDefaultValue(String paramString, Object paramObject)
  {
    synchronized (GLOBAL_DEFAULT_VALUES)
    {
      GLOBAL_DEFAULT_VALUES.put(paramString, paramObject);
      return;
    }
  }
  
  public final String[] getAllKeys()
  {
    Map localMap1;
    if (this.m_GlobalPreferences == null)
    {
      localMap1 = null;
      if (this.m_PrivatePreferences != null) {
        break label81;
      }
    }
    label81:
    for (Map localMap2 = null;; localMap2 = this.m_PrivatePreferences.getAll())
    {
      HashSet localHashSet = new HashSet();
      if (localMap1 != null) {
        localHashSet.addAll(localMap1.keySet());
      }
      if (localMap2 != null) {
        localHashSet.addAll(localMap2.keySet());
      }
      return (String[])localHashSet.toArray(new String[0]);
      localMap1 = this.m_GlobalPreferences.getAll();
      break;
    }
  }
  
  public final boolean getBoolean(String paramString)
  {
    Object localObject = getDefaultValue(paramString);
    if ((localObject instanceof Boolean)) {}
    for (boolean bool = ((Boolean)localObject).booleanValue();; bool = false) {
      return getBoolean(paramString, bool);
    }
  }
  
  public boolean getBoolean(String paramString, boolean paramBoolean)
  {
    if (!isPrivateKey(paramString)) {
      return this.m_GlobalPreferences.getBoolean(paramString, paramBoolean);
    }
    if (!this.m_IsVolatile) {
      return this.m_PrivatePreferences.getBoolean(paramString, paramBoolean);
    }
    synchronized (this.m_PrivateVolatileValues)
    {
      paramString = this.m_PrivateVolatileValues.get(paramString);
      if ((paramString instanceof Boolean)) {
        paramBoolean = ((Boolean)paramString).booleanValue();
      }
      return paramBoolean;
    }
  }
  
  public Object getDefaultValue(String paramString)
  {
    if (this.m_PrivateDefaultValues != null) {
      synchronized (this.m_PrivateDefaultValues)
      {
        Object localObject = this.m_PrivateDefaultValues.get(paramString);
        if (localObject != null) {
          return localObject;
        }
      }
    }
    synchronized (GLOBAL_DEFAULT_VALUES)
    {
      paramString = GLOBAL_DEFAULT_VALUES.get(paramString);
      if (paramString != null)
      {
        return paramString;
        paramString = finally;
        throw paramString;
      }
      return null;
    }
  }
  
  public final <T extends Enum<T>> T getEnum(String paramString, Class<T> paramClass)
  {
    Object localObject = getDefaultValue(paramString);
    if ((localObject != null) && (paramClass == localObject.getClass())) {
      localObject = (Enum)localObject;
    }
    for (;;)
    {
      return getEnum(paramString, paramClass, (Enum)localObject);
      localObject = (Enum[])paramClass.getEnumConstants();
      if (localObject.length > 0) {
        localObject = localObject[0];
      } else {
        localObject = null;
      }
    }
  }
  
  public final <T extends Enum<T>> T getEnum(String paramString, Class<T> paramClass, T paramT)
  {
    String str = null;
    if (paramT != null) {
      str = paramT.toString();
    }
    paramString = getString(paramString, str);
    if (paramString != null) {
      try
      {
        paramString = Enum.valueOf(paramClass, paramString);
        return paramString;
      }
      catch (Throwable paramString) {}
    }
    return paramT;
  }
  
  public final int getInt(String paramString)
  {
    Object localObject = getDefaultValue(paramString);
    if ((localObject instanceof Integer)) {}
    for (int i = ((Integer)localObject).intValue();; i = 0) {
      return getInt(paramString, i);
    }
  }
  
  public int getInt(String paramString, int paramInt)
  {
    if (!isPrivateKey(paramString)) {
      return this.m_GlobalPreferences.getInt(paramString, paramInt);
    }
    if (!this.m_IsVolatile) {
      return this.m_PrivatePreferences.getInt(paramString, paramInt);
    }
    synchronized (this.m_PrivateVolatileValues)
    {
      paramString = this.m_PrivateVolatileValues.get(paramString);
      if ((paramString instanceof Integer)) {
        paramInt = ((Integer)paramString).intValue();
      }
      return paramInt;
    }
  }
  
  public final long getLong(String paramString)
  {
    Object localObject = getDefaultValue(paramString);
    if ((localObject instanceof Long)) {
      ((Long)localObject).longValue();
    }
    if ((localObject instanceof Integer)) {}
    for (long l = ((Integer)localObject).intValue();; l = 0L) {
      return getLong(paramString, l);
    }
  }
  
  public long getLong(String paramString, long paramLong)
  {
    if (!isPrivateKey(paramString)) {
      return this.m_GlobalPreferences.getLong(paramString, paramLong);
    }
    if (!this.m_IsVolatile) {
      return this.m_PrivatePreferences.getLong(paramString, paramLong);
    }
    synchronized (this.m_PrivateVolatileValues)
    {
      paramString = this.m_PrivateVolatileValues.get(paramString);
      if ((paramString instanceof Long))
      {
        paramLong = ((Long)paramString).longValue();
        return paramLong;
      }
      if ((paramString instanceof Integer))
      {
        int i = ((Integer)paramString).intValue();
        paramLong = i;
        return paramLong;
      }
      return paramLong;
    }
  }
  
  public final String getName()
  {
    return this.m_Name;
  }
  
  public final String getString(String paramString)
  {
    String str = null;
    Object localObject = getDefaultValue(paramString);
    if (localObject != null) {
      str = localObject.toString();
    }
    return getString(paramString, str);
  }
  
  public String getString(String paramString1, String paramString2)
  {
    if (!isPrivateKey(paramString1)) {
      return this.m_GlobalPreferences.getString(paramString1, paramString2);
    }
    if (!this.m_IsVolatile) {
      return this.m_PrivatePreferences.getString(paramString1, paramString2);
    }
    synchronized (this.m_PrivateVolatileValues)
    {
      paramString1 = this.m_PrivateVolatileValues.get(paramString1);
      if (paramString1 != null) {
        paramString2 = paramString1.toString();
      }
      return paramString2;
    }
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
      return;
    }
    onValueChanged((String)paramMessage.obj);
  }
  
  public boolean isPrivateKey(String paramString)
  {
    if (paramString == null) {
      return false;
    }
    synchronized (PRIVATE_KEYS)
    {
      boolean bool = PRIVATE_KEYS.contains(paramString);
      return bool;
    }
  }
  
  public final boolean isVolatile()
  {
    return this.m_IsVolatile;
  }
  
  protected void notifyValueChanged(String paramString)
  {
    if (isDependencyThread())
    {
      onValueChanged(paramString);
      return;
    }
    HandlerUtils.sendMessage(this, 10000, 0, 0, paramString);
  }
  
  protected void onRelease()
  {
    this.m_GlobalPreferences.unregisterOnSharedPreferenceChangeListener(this.m_PreferenceChangedListener);
    if ((this.m_PrivatePreferences != null) && (this.m_PrivatePreferences != this.m_GlobalPreferences)) {
      this.m_PrivatePreferences.unregisterOnSharedPreferenceChangeListener(this.m_PreferenceChangedListener);
    }
    super.onRelease();
  }
  
  protected void onValueChanged(String paramString)
  {
    paramString = SettingsValueChangedEventArgs.obtain(paramString);
    raise(EVENT_VALUE_CHANGED, paramString);
    paramString.recycle();
  }
  
  public final void reset(String paramString)
  {
    set(paramString, null);
  }
  
  public void set(String paramString, Object paramObject)
  {
    if (!isPrivateKey(paramString))
    {
      ??? = this.m_GlobalPreferences.edit();
      set((SharedPreferences.Editor)???, paramString, paramObject);
      ((SharedPreferences.Editor)???).apply();
      return;
    }
    if (!this.m_IsVolatile)
    {
      ??? = this.m_PrivatePreferences.edit();
      set((SharedPreferences.Editor)???, paramString, paramObject);
      ((SharedPreferences.Editor)???).apply();
      return;
    }
    for (;;)
    {
      synchronized (this.m_PrivateVolatileValues)
      {
        if (((paramObject instanceof Boolean)) || ((paramObject instanceof Integer)) || ((paramObject instanceof Long)))
        {
          this.m_PrivateVolatileValues.put(paramString, paramObject);
          notifyValueChanged(paramString);
          return;
        }
        if (paramObject != null) {
          this.m_PrivateVolatileValues.put(paramString, paramObject.toString());
        }
      }
      if (this.m_PrivateVolatileValues.contains(paramString)) {
        this.m_PrivateVolatileValues.remove(paramString);
      }
    }
  }
  
  public final void setDefaultValue(String paramString, Object paramObject)
  {
    synchronized (this.m_PrivateDefaultValues)
    {
      this.m_PrivateDefaultValues.put(paramString, paramObject);
      return;
    }
  }
  
  public String toString()
  {
    if (this.m_Name != null) {
      return this.m_Name + "@" + hashCode();
    }
    return "(Global)@" + hashCode();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/Settings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */