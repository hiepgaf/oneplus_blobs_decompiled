package android.content.res;

import android.os.Build.VERSION;
import android.os.LocaleList;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public final class Configuration
  implements Parcelable, Comparable<Configuration>
{
  public static final Parcelable.Creator<Configuration> CREATOR = new Parcelable.Creator()
  {
    public Configuration createFromParcel(Parcel paramAnonymousParcel)
    {
      return new Configuration(paramAnonymousParcel, null);
    }
    
    public Configuration[] newArray(int paramAnonymousInt)
    {
      return new Configuration[paramAnonymousInt];
    }
  };
  public static final int DENSITY_DPI_ANY = 65534;
  public static final int DENSITY_DPI_NONE = 65535;
  public static final int DENSITY_DPI_UNDEFINED = 0;
  public static final Configuration EMPTY = new Configuration();
  public static final int HARDKEYBOARDHIDDEN_NO = 1;
  public static final int HARDKEYBOARDHIDDEN_UNDEFINED = 0;
  public static final int HARDKEYBOARDHIDDEN_YES = 2;
  public static final int KEYBOARDHIDDEN_NO = 1;
  public static final int KEYBOARDHIDDEN_SOFT = 3;
  public static final int KEYBOARDHIDDEN_UNDEFINED = 0;
  public static final int KEYBOARDHIDDEN_YES = 2;
  public static final int KEYBOARD_12KEY = 3;
  public static final int KEYBOARD_NOKEYS = 1;
  public static final int KEYBOARD_QWERTY = 2;
  public static final int KEYBOARD_UNDEFINED = 0;
  public static final int MNC_ZERO = 65535;
  public static final int NATIVE_CONFIG_DENSITY = 256;
  public static final int NATIVE_CONFIG_KEYBOARD = 16;
  public static final int NATIVE_CONFIG_KEYBOARD_HIDDEN = 32;
  public static final int NATIVE_CONFIG_LAYOUTDIR = 16384;
  public static final int NATIVE_CONFIG_LOCALE = 4;
  public static final int NATIVE_CONFIG_MCC = 1;
  public static final int NATIVE_CONFIG_MNC = 2;
  public static final int NATIVE_CONFIG_NAVIGATION = 64;
  public static final int NATIVE_CONFIG_ORIENTATION = 128;
  public static final int NATIVE_CONFIG_SCREEN_LAYOUT = 2048;
  public static final int NATIVE_CONFIG_SCREEN_SIZE = 512;
  public static final int NATIVE_CONFIG_SMALLEST_SCREEN_SIZE = 8192;
  public static final int NATIVE_CONFIG_TOUCHSCREEN = 8;
  public static final int NATIVE_CONFIG_UI_MODE = 4096;
  public static final int NATIVE_CONFIG_VERSION = 1024;
  public static final int NAVIGATIONHIDDEN_NO = 1;
  public static final int NAVIGATIONHIDDEN_UNDEFINED = 0;
  public static final int NAVIGATIONHIDDEN_YES = 2;
  public static final int NAVIGATION_DPAD = 2;
  public static final int NAVIGATION_NONAV = 1;
  public static final int NAVIGATION_TRACKBALL = 3;
  public static final int NAVIGATION_UNDEFINED = 0;
  public static final int NAVIGATION_WHEEL = 4;
  public static final int ORIENTATION_LANDSCAPE = 2;
  public static final int ORIENTATION_PORTRAIT = 1;
  @Deprecated
  public static final int ORIENTATION_SQUARE = 3;
  public static final int ORIENTATION_UNDEFINED = 0;
  public static final int SCREENLAYOUT_COMPAT_NEEDED = 268435456;
  public static final int SCREENLAYOUT_LAYOUTDIR_LTR = 64;
  public static final int SCREENLAYOUT_LAYOUTDIR_MASK = 192;
  public static final int SCREENLAYOUT_LAYOUTDIR_RTL = 128;
  public static final int SCREENLAYOUT_LAYOUTDIR_SHIFT = 6;
  public static final int SCREENLAYOUT_LAYOUTDIR_UNDEFINED = 0;
  public static final int SCREENLAYOUT_LONG_MASK = 48;
  public static final int SCREENLAYOUT_LONG_NO = 16;
  public static final int SCREENLAYOUT_LONG_UNDEFINED = 0;
  public static final int SCREENLAYOUT_LONG_YES = 32;
  public static final int SCREENLAYOUT_ROUND_MASK = 768;
  public static final int SCREENLAYOUT_ROUND_NO = 256;
  public static final int SCREENLAYOUT_ROUND_SHIFT = 8;
  public static final int SCREENLAYOUT_ROUND_UNDEFINED = 0;
  public static final int SCREENLAYOUT_ROUND_YES = 512;
  public static final int SCREENLAYOUT_SIZE_LARGE = 3;
  public static final int SCREENLAYOUT_SIZE_MASK = 15;
  public static final int SCREENLAYOUT_SIZE_NORMAL = 2;
  public static final int SCREENLAYOUT_SIZE_SMALL = 1;
  public static final int SCREENLAYOUT_SIZE_UNDEFINED = 0;
  public static final int SCREENLAYOUT_SIZE_XLARGE = 4;
  public static final int SCREENLAYOUT_UNDEFINED = 0;
  public static final int SCREEN_HEIGHT_DP_UNDEFINED = 0;
  public static final int SCREEN_WIDTH_DP_UNDEFINED = 0;
  public static final int SMALLEST_SCREEN_WIDTH_DP_UNDEFINED = 0;
  public static final int TOUCHSCREEN_FINGER = 3;
  public static final int TOUCHSCREEN_NOTOUCH = 1;
  @Deprecated
  public static final int TOUCHSCREEN_STYLUS = 2;
  public static final int TOUCHSCREEN_UNDEFINED = 0;
  public static final int UI_MODE_NIGHT_MASK = 48;
  public static final int UI_MODE_NIGHT_NO = 16;
  public static final int UI_MODE_NIGHT_UNDEFINED = 0;
  public static final int UI_MODE_NIGHT_YES = 32;
  public static final int UI_MODE_TYPE_APPLIANCE = 5;
  public static final int UI_MODE_TYPE_CAR = 3;
  public static final int UI_MODE_TYPE_DESK = 2;
  public static final int UI_MODE_TYPE_MASK = 15;
  public static final int UI_MODE_TYPE_NORMAL = 1;
  public static final int UI_MODE_TYPE_TELEVISION = 4;
  public static final int UI_MODE_TYPE_UNDEFINED = 0;
  public static final int UI_MODE_TYPE_WATCH = 6;
  private static final String XML_ATTR_DENSITY = "density";
  private static final String XML_ATTR_FONT_SCALE = "fs";
  private static final String XML_ATTR_HARD_KEYBOARD_HIDDEN = "hardKeyHid";
  private static final String XML_ATTR_KEYBOARD = "key";
  private static final String XML_ATTR_KEYBOARD_HIDDEN = "keyHid";
  private static final String XML_ATTR_LOCALES = "locales";
  private static final String XML_ATTR_MCC = "mcc";
  private static final String XML_ATTR_MNC = "mnc";
  private static final String XML_ATTR_NAVIGATION = "nav";
  private static final String XML_ATTR_NAVIGATION_HIDDEN = "navHid";
  private static final String XML_ATTR_ORIENTATION = "ori";
  private static final String XML_ATTR_SCREEN_HEIGHT = "height";
  private static final String XML_ATTR_SCREEN_LAYOUT = "scrLay";
  private static final String XML_ATTR_SCREEN_WIDTH = "width";
  private static final String XML_ATTR_SMALLEST_WIDTH = "sw";
  private static final String XML_ATTR_TOUCHSCREEN = "touch";
  private static final String XML_ATTR_UI_MODE = "ui";
  public int compatScreenHeightDp;
  public int compatScreenWidthDp;
  public int compatSmallestScreenWidthDp;
  public int densityDpi;
  public float fontScale;
  public int hardKeyboardHidden;
  public int keyboard;
  public int keyboardHidden;
  @Deprecated
  public Locale locale;
  private LocaleList mLocaleList;
  public OpExtraConfiguration mOpExtraConfiguration = null;
  public int mcc;
  public int mnc;
  public int navigation;
  public int navigationHidden;
  public int orientation;
  public int screenHeightDp;
  public int screenLayout;
  public int screenWidthDp;
  public int seq;
  public int smallestScreenWidthDp;
  public int touchscreen;
  public int uiMode;
  public boolean userSetLocale;
  
  public Configuration()
  {
    setToDefaults();
  }
  
  public Configuration(Configuration paramConfiguration)
  {
    setTo(paramConfiguration);
  }
  
  private Configuration(Parcel paramParcel)
  {
    readFromParcel(paramParcel);
  }
  
  public static String configurationDiffToString(int paramInt)
  {
    ArrayList localArrayList = new ArrayList();
    if ((paramInt & 0x1) != 0) {
      localArrayList.add("CONFIG_MCC");
    }
    if ((paramInt & 0x2) != 0) {
      localArrayList.add("CONFIG_MNC");
    }
    if ((paramInt & 0x4) != 0) {
      localArrayList.add("CONFIG_LOCALE");
    }
    if ((paramInt & 0x8) != 0) {
      localArrayList.add("CONFIG_TOUCHSCREEN");
    }
    if ((paramInt & 0x10) != 0) {
      localArrayList.add("CONFIG_KEYBOARD");
    }
    if ((paramInt & 0x20) != 0) {
      localArrayList.add("CONFIG_KEYBOARD_HIDDEN");
    }
    if ((paramInt & 0x40) != 0) {
      localArrayList.add("CONFIG_NAVIGATION");
    }
    if ((paramInt & 0x80) != 0) {
      localArrayList.add("CONFIG_ORIENTATION");
    }
    if ((paramInt & 0x100) != 0) {
      localArrayList.add("CONFIG_SCREEN_LAYOUT");
    }
    if ((paramInt & 0x200) != 0) {
      localArrayList.add("CONFIG_UI_MODE");
    }
    if ((paramInt & 0x400) != 0) {
      localArrayList.add("CONFIG_SCREEN_SIZE");
    }
    if ((paramInt & 0x800) != 0) {
      localArrayList.add("CONFIG_SMALLEST_SCREEN_SIZE");
    }
    if ((paramInt & 0x2000) != 0) {
      localArrayList.add("CONFIG_LAYOUT_DIRECTION");
    }
    if ((0x40000000 & paramInt) != 0) {
      localArrayList.add("CONFIG_FONT_SCALE");
    }
    StringBuilder localStringBuilder = new StringBuilder("{");
    paramInt = 0;
    int i = localArrayList.size();
    while (paramInt < i)
    {
      localStringBuilder.append((String)localArrayList.get(paramInt));
      if (paramInt != i - 1) {
        localStringBuilder.append(", ");
      }
      paramInt += 1;
    }
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
  
  private void fixUpLocaleList()
  {
    if (((this.locale != null) || (this.mLocaleList.isEmpty())) && ((this.locale == null) || (this.locale.equals(this.mLocaleList.get(0))))) {
      return;
    }
    if (this.locale == null) {}
    for (LocaleList localLocaleList = LocaleList.getEmptyLocaleList();; localLocaleList = new LocaleList(new Locale[] { this.locale }))
    {
      this.mLocaleList = localLocaleList;
      return;
    }
  }
  
  public static Configuration generateDelta(Configuration paramConfiguration1, Configuration paramConfiguration2)
  {
    Configuration localConfiguration = new Configuration();
    if (paramConfiguration1.fontScale != paramConfiguration2.fontScale) {
      localConfiguration.fontScale = paramConfiguration2.fontScale;
    }
    if (paramConfiguration1.mcc != paramConfiguration2.mcc) {
      localConfiguration.mcc = paramConfiguration2.mcc;
    }
    if (paramConfiguration1.mnc != paramConfiguration2.mnc) {
      localConfiguration.mnc = paramConfiguration2.mnc;
    }
    paramConfiguration1.fixUpLocaleList();
    paramConfiguration2.fixUpLocaleList();
    if (!paramConfiguration1.mLocaleList.equals(paramConfiguration2.mLocaleList))
    {
      localConfiguration.mLocaleList = paramConfiguration2.mLocaleList;
      localConfiguration.locale = paramConfiguration2.locale;
    }
    if (paramConfiguration1.touchscreen != paramConfiguration2.touchscreen) {
      localConfiguration.touchscreen = paramConfiguration2.touchscreen;
    }
    if (paramConfiguration1.keyboard != paramConfiguration2.keyboard) {
      localConfiguration.keyboard = paramConfiguration2.keyboard;
    }
    if (paramConfiguration1.keyboardHidden != paramConfiguration2.keyboardHidden) {
      localConfiguration.keyboardHidden = paramConfiguration2.keyboardHidden;
    }
    if (paramConfiguration1.navigation != paramConfiguration2.navigation) {
      localConfiguration.navigation = paramConfiguration2.navigation;
    }
    if (paramConfiguration1.navigationHidden != paramConfiguration2.navigationHidden) {
      localConfiguration.navigationHidden = paramConfiguration2.navigationHidden;
    }
    if (paramConfiguration1.orientation != paramConfiguration2.orientation) {
      localConfiguration.orientation = paramConfiguration2.orientation;
    }
    if ((paramConfiguration1.screenLayout & 0xF) != (paramConfiguration2.screenLayout & 0xF)) {
      localConfiguration.screenLayout |= paramConfiguration2.screenLayout & 0xF;
    }
    if ((paramConfiguration1.screenLayout & 0xC0) != (paramConfiguration2.screenLayout & 0xC0)) {
      localConfiguration.screenLayout |= paramConfiguration2.screenLayout & 0xC0;
    }
    if ((paramConfiguration1.screenLayout & 0x30) != (paramConfiguration2.screenLayout & 0x30)) {
      localConfiguration.screenLayout |= paramConfiguration2.screenLayout & 0x30;
    }
    if ((paramConfiguration1.screenLayout & 0x300) != (paramConfiguration2.screenLayout & 0x300)) {
      localConfiguration.screenLayout |= paramConfiguration2.screenLayout & 0x300;
    }
    if ((paramConfiguration1.uiMode & 0xF) != (paramConfiguration2.uiMode & 0xF)) {
      localConfiguration.uiMode |= paramConfiguration2.uiMode & 0xF;
    }
    if ((paramConfiguration1.uiMode & 0x30) != (paramConfiguration2.uiMode & 0x30)) {
      localConfiguration.uiMode |= paramConfiguration2.uiMode & 0x30;
    }
    if (paramConfiguration1.screenWidthDp != paramConfiguration2.screenWidthDp) {
      localConfiguration.screenWidthDp = paramConfiguration2.screenWidthDp;
    }
    if (paramConfiguration1.screenHeightDp != paramConfiguration2.screenHeightDp) {
      localConfiguration.screenHeightDp = paramConfiguration2.screenHeightDp;
    }
    if (paramConfiguration1.smallestScreenWidthDp != paramConfiguration2.smallestScreenWidthDp) {
      localConfiguration.smallestScreenWidthDp = paramConfiguration2.smallestScreenWidthDp;
    }
    if (paramConfiguration1.densityDpi != paramConfiguration2.densityDpi) {
      localConfiguration.densityDpi = paramConfiguration2.densityDpi;
    }
    return localConfiguration;
  }
  
  private static int getScreenLayoutNoDirection(int paramInt)
  {
    return paramInt & 0xFF3F;
  }
  
  public static String localesToResourceQualifier(LocaleList paramLocaleList)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    if (i < paramLocaleList.size())
    {
      Locale localLocale = paramLocaleList.get(i);
      int j = localLocale.getLanguage().length();
      if (j == 0) {}
      for (;;)
      {
        i += 1;
        break;
        int k = localLocale.getScript().length();
        int m = localLocale.getCountry().length();
        int n = localLocale.getVariant().length();
        if (localStringBuilder.length() != 0) {
          localStringBuilder.append(",");
        }
        if ((j == 2) && (k == 0) && ((m == 0) || (m == 2)) && (n == 0))
        {
          localStringBuilder.append(localLocale.getLanguage());
          if (m == 2) {
            localStringBuilder.append("-r").append(localLocale.getCountry());
          }
        }
        else
        {
          localStringBuilder.append("b+");
          localStringBuilder.append(localLocale.getLanguage());
          if (k != 0)
          {
            localStringBuilder.append("+");
            localStringBuilder.append(localLocale.getScript());
          }
          if (m != 0)
          {
            localStringBuilder.append("+");
            localStringBuilder.append(localLocale.getCountry());
          }
          if (n != 0)
          {
            localStringBuilder.append("+");
            localStringBuilder.append(localLocale.getVariant());
          }
        }
      }
    }
    return localStringBuilder.toString();
  }
  
  public static boolean needNewResources(int paramInt1, int paramInt2)
  {
    if (((0x40000000 | paramInt2) & paramInt1) == 0) {
      return OpExtraConfiguration.needNewResources(paramInt1);
    }
    return true;
  }
  
  public static void readXmlAttrs(XmlPullParser paramXmlPullParser, Configuration paramConfiguration)
    throws XmlPullParserException, IOException
  {
    paramConfiguration.fontScale = Float.intBitsToFloat(XmlUtils.readIntAttribute(paramXmlPullParser, "fs", 0));
    paramConfiguration.mcc = XmlUtils.readIntAttribute(paramXmlPullParser, "mcc", 0);
    paramConfiguration.mnc = XmlUtils.readIntAttribute(paramXmlPullParser, "mnc", 0);
    paramConfiguration.mLocaleList = LocaleList.forLanguageTags(XmlUtils.readStringAttribute(paramXmlPullParser, "locales"));
    paramConfiguration.locale = paramConfiguration.mLocaleList.get(0);
    paramConfiguration.touchscreen = XmlUtils.readIntAttribute(paramXmlPullParser, "touch", 0);
    paramConfiguration.keyboard = XmlUtils.readIntAttribute(paramXmlPullParser, "key", 0);
    paramConfiguration.keyboardHidden = XmlUtils.readIntAttribute(paramXmlPullParser, "keyHid", 0);
    paramConfiguration.hardKeyboardHidden = XmlUtils.readIntAttribute(paramXmlPullParser, "hardKeyHid", 0);
    paramConfiguration.navigation = XmlUtils.readIntAttribute(paramXmlPullParser, "nav", 0);
    paramConfiguration.navigationHidden = XmlUtils.readIntAttribute(paramXmlPullParser, "navHid", 0);
    paramConfiguration.orientation = XmlUtils.readIntAttribute(paramXmlPullParser, "ori", 0);
    paramConfiguration.screenLayout = XmlUtils.readIntAttribute(paramXmlPullParser, "scrLay", 0);
    paramConfiguration.uiMode = XmlUtils.readIntAttribute(paramXmlPullParser, "ui", 0);
    paramConfiguration.screenWidthDp = XmlUtils.readIntAttribute(paramXmlPullParser, "width", 0);
    paramConfiguration.screenHeightDp = XmlUtils.readIntAttribute(paramXmlPullParser, "height", 0);
    paramConfiguration.smallestScreenWidthDp = XmlUtils.readIntAttribute(paramXmlPullParser, "sw", 0);
    paramConfiguration.densityDpi = XmlUtils.readIntAttribute(paramXmlPullParser, "density", 0);
  }
  
  public static int reduceScreenLayout(int paramInt1, int paramInt2, int paramInt3)
  {
    int i;
    int j;
    if (paramInt2 < 470)
    {
      i = 1;
      paramInt2 = 0;
      j = 0;
    }
    for (;;)
    {
      paramInt3 = paramInt1;
      if (paramInt2 == 0) {
        paramInt3 = paramInt1 & 0xFFFFFFCF | 0x10;
      }
      paramInt1 = paramInt3;
      if (j != 0) {
        paramInt1 = paramInt3 | 0x10000000;
      }
      paramInt2 = paramInt1;
      if (i < (paramInt1 & 0xF)) {
        paramInt2 = paramInt1 & 0xFFFFFFF0 | i;
      }
      return paramInt2;
      if ((paramInt2 >= 960) && (paramInt3 >= 720))
      {
        i = 4;
        label75:
        if ((paramInt3 <= 321) && (paramInt2 <= 570)) {
          break label132;
        }
      }
      label132:
      for (j = 1;; j = 0)
      {
        if (paramInt2 * 3 / 5 < paramInt3 - 1) {
          break label138;
        }
        paramInt2 = 1;
        break;
        if ((paramInt2 >= 640) && (paramInt3 >= 480))
        {
          i = 3;
          break label75;
        }
        i = 2;
        break label75;
      }
      label138:
      paramInt2 = 0;
    }
  }
  
  public static int resetScreenLayout(int paramInt)
  {
    return 0xEFFFFFC0 & paramInt | 0x24;
  }
  
  public static String resourceQualifierString(Configuration paramConfiguration)
  {
    ArrayList localArrayList = new ArrayList();
    if (paramConfiguration.mcc != 0)
    {
      localArrayList.add("mcc" + paramConfiguration.mcc);
      if (paramConfiguration.mnc != 0) {
        localArrayList.add("mnc" + paramConfiguration.mnc);
      }
    }
    if (!paramConfiguration.mLocaleList.isEmpty())
    {
      String str = localesToResourceQualifier(paramConfiguration.mLocaleList);
      if (!str.isEmpty()) {
        localArrayList.add(str);
      }
    }
    switch (paramConfiguration.screenLayout & 0xC0)
    {
    default: 
      if (paramConfiguration.smallestScreenWidthDp != 0) {
        localArrayList.add("sw" + paramConfiguration.smallestScreenWidthDp + "dp");
      }
      if (paramConfiguration.screenWidthDp != 0) {
        localArrayList.add("w" + paramConfiguration.screenWidthDp + "dp");
      }
      if (paramConfiguration.screenHeightDp != 0) {
        localArrayList.add("h" + paramConfiguration.screenHeightDp + "dp");
      }
      switch (paramConfiguration.screenLayout & 0xF)
      {
      default: 
        label300:
        switch (paramConfiguration.screenLayout & 0x30)
        {
        default: 
          label332:
          switch (paramConfiguration.screenLayout & 0x300)
          {
          default: 
            label368:
            switch (paramConfiguration.orientation)
            {
            default: 
              label396:
              switch (paramConfiguration.uiMode & 0xF)
              {
              default: 
                label436:
                switch (paramConfiguration.uiMode & 0x30)
                {
                default: 
                  label468:
                  switch (paramConfiguration.densityDpi)
                  {
                  default: 
                    localArrayList.add(paramConfiguration.densityDpi + "dpi");
                  case 0: 
                    label564:
                    label592:
                    switch (paramConfiguration.touchscreen)
                    {
                    case 2: 
                    default: 
                      label624:
                      switch (paramConfiguration.keyboardHidden)
                      {
                      default: 
                        label656:
                        switch (paramConfiguration.keyboard)
                        {
                        default: 
                          label688:
                          switch (paramConfiguration.navigationHidden)
                          {
                          default: 
                            label716:
                            switch (paramConfiguration.navigation)
                            {
                            }
                            break;
                          }
                          break;
                        }
                        break;
                      }
                      break;
                    }
                    break;
                  }
                  break;
                }
                break;
              }
              break;
            }
            break;
          }
          break;
        }
        break;
      }
      break;
    }
    for (;;)
    {
      localArrayList.add("v" + Build.VERSION.RESOURCES_SDK_INT);
      return TextUtils.join("-", localArrayList);
      localArrayList.add("ldltr");
      break;
      localArrayList.add("ldrtl");
      break;
      localArrayList.add("small");
      break label300;
      localArrayList.add("normal");
      break label300;
      localArrayList.add("large");
      break label300;
      localArrayList.add("xlarge");
      break label300;
      localArrayList.add("long");
      break label332;
      localArrayList.add("notlong");
      break label332;
      localArrayList.add("round");
      break label368;
      localArrayList.add("notround");
      break label368;
      localArrayList.add("land");
      break label396;
      localArrayList.add("port");
      break label396;
      localArrayList.add("appliance");
      break label436;
      localArrayList.add("desk");
      break label436;
      localArrayList.add("television");
      break label436;
      localArrayList.add("car");
      break label436;
      localArrayList.add("watch");
      break label436;
      localArrayList.add("night");
      break label468;
      localArrayList.add("notnight");
      break label468;
      localArrayList.add("ldpi");
      break label592;
      localArrayList.add("mdpi");
      break label592;
      localArrayList.add("tvdpi");
      break label592;
      localArrayList.add("hdpi");
      break label592;
      localArrayList.add("xhdpi");
      break label592;
      localArrayList.add("xxhdpi");
      break label592;
      localArrayList.add("xxxhdpi");
      break label592;
      localArrayList.add("anydpi");
      break label592;
      localArrayList.add("nodpi");
      break label564;
      localArrayList.add("notouch");
      break label624;
      localArrayList.add("finger");
      break label624;
      localArrayList.add("keysexposed");
      break label656;
      localArrayList.add("keyshidden");
      break label656;
      localArrayList.add("keyssoft");
      break label656;
      localArrayList.add("nokeys");
      break label688;
      localArrayList.add("qwerty");
      break label688;
      localArrayList.add("12key");
      break label688;
      localArrayList.add("navexposed");
      break label716;
      localArrayList.add("navhidden");
      break label716;
      localArrayList.add("nonav");
      continue;
      localArrayList.add("dpad");
      continue;
      localArrayList.add("trackball");
      continue;
      localArrayList.add("wheel");
    }
  }
  
  public static void writeXmlAttrs(XmlSerializer paramXmlSerializer, Configuration paramConfiguration)
    throws IOException
  {
    XmlUtils.writeIntAttribute(paramXmlSerializer, "fs", Float.floatToIntBits(paramConfiguration.fontScale));
    if (paramConfiguration.mcc != 0) {
      XmlUtils.writeIntAttribute(paramXmlSerializer, "mcc", paramConfiguration.mcc);
    }
    if (paramConfiguration.mnc != 0) {
      XmlUtils.writeIntAttribute(paramXmlSerializer, "mnc", paramConfiguration.mnc);
    }
    paramConfiguration.fixUpLocaleList();
    if (!paramConfiguration.mLocaleList.isEmpty()) {
      XmlUtils.writeStringAttribute(paramXmlSerializer, "locales", paramConfiguration.mLocaleList.toLanguageTags());
    }
    if (paramConfiguration.touchscreen != 0) {
      XmlUtils.writeIntAttribute(paramXmlSerializer, "touch", paramConfiguration.touchscreen);
    }
    if (paramConfiguration.keyboard != 0) {
      XmlUtils.writeIntAttribute(paramXmlSerializer, "key", paramConfiguration.keyboard);
    }
    if (paramConfiguration.keyboardHidden != 0) {
      XmlUtils.writeIntAttribute(paramXmlSerializer, "keyHid", paramConfiguration.keyboardHidden);
    }
    if (paramConfiguration.hardKeyboardHidden != 0) {
      XmlUtils.writeIntAttribute(paramXmlSerializer, "hardKeyHid", paramConfiguration.hardKeyboardHidden);
    }
    if (paramConfiguration.navigation != 0) {
      XmlUtils.writeIntAttribute(paramXmlSerializer, "nav", paramConfiguration.navigation);
    }
    if (paramConfiguration.navigationHidden != 0) {
      XmlUtils.writeIntAttribute(paramXmlSerializer, "navHid", paramConfiguration.navigationHidden);
    }
    if (paramConfiguration.orientation != 0) {
      XmlUtils.writeIntAttribute(paramXmlSerializer, "ori", paramConfiguration.orientation);
    }
    if (paramConfiguration.screenLayout != 0) {
      XmlUtils.writeIntAttribute(paramXmlSerializer, "scrLay", paramConfiguration.screenLayout);
    }
    if (paramConfiguration.uiMode != 0) {
      XmlUtils.writeIntAttribute(paramXmlSerializer, "ui", paramConfiguration.uiMode);
    }
    if (paramConfiguration.screenWidthDp != 0) {
      XmlUtils.writeIntAttribute(paramXmlSerializer, "width", paramConfiguration.screenWidthDp);
    }
    if (paramConfiguration.screenHeightDp != 0) {
      XmlUtils.writeIntAttribute(paramXmlSerializer, "height", paramConfiguration.screenHeightDp);
    }
    if (paramConfiguration.smallestScreenWidthDp != 0) {
      XmlUtils.writeIntAttribute(paramXmlSerializer, "sw", paramConfiguration.smallestScreenWidthDp);
    }
    if (paramConfiguration.densityDpi != 0) {
      XmlUtils.writeIntAttribute(paramXmlSerializer, "density", paramConfiguration.densityDpi);
    }
  }
  
  public void clearLocales()
  {
    this.mLocaleList = LocaleList.getEmptyLocaleList();
    this.locale = null;
  }
  
  public int compareTo(Configuration paramConfiguration)
  {
    float f1 = this.fontScale;
    float f2 = paramConfiguration.fontScale;
    if (f1 < f2) {
      return -1;
    }
    if (f1 > f2) {
      return 1;
    }
    int i = this.mcc - paramConfiguration.mcc;
    if (i != 0) {
      return i;
    }
    i = this.mnc - paramConfiguration.mnc;
    if (i != 0) {
      return i;
    }
    fixUpLocaleList();
    paramConfiguration.fixUpLocaleList();
    if (this.mLocaleList.isEmpty())
    {
      if (!paramConfiguration.mLocaleList.isEmpty()) {
        return 1;
      }
    }
    else
    {
      if (paramConfiguration.mLocaleList.isEmpty()) {
        return -1;
      }
      int j = Math.min(this.mLocaleList.size(), paramConfiguration.mLocaleList.size());
      i = 0;
      while (i < j)
      {
        Locale localLocale1 = this.mLocaleList.get(i);
        Locale localLocale2 = paramConfiguration.mLocaleList.get(i);
        int k = localLocale1.getLanguage().compareTo(localLocale2.getLanguage());
        if (k != 0) {
          return k;
        }
        k = localLocale1.getCountry().compareTo(localLocale2.getCountry());
        if (k != 0) {
          return k;
        }
        k = localLocale1.getVariant().compareTo(localLocale2.getVariant());
        if (k != 0) {
          return k;
        }
        k = localLocale1.toLanguageTag().compareTo(localLocale2.toLanguageTag());
        if (k != 0) {
          return k;
        }
        i += 1;
      }
      i = this.mLocaleList.size() - paramConfiguration.mLocaleList.size();
      if (i != 0) {
        return i;
      }
    }
    i = this.touchscreen - paramConfiguration.touchscreen;
    if (i != 0) {
      return i;
    }
    i = this.keyboard - paramConfiguration.keyboard;
    if (i != 0) {
      return i;
    }
    i = this.keyboardHidden - paramConfiguration.keyboardHidden;
    if (i != 0) {
      return i;
    }
    i = this.hardKeyboardHidden - paramConfiguration.hardKeyboardHidden;
    if (i != 0) {
      return i;
    }
    i = this.navigation - paramConfiguration.navigation;
    if (i != 0) {
      return i;
    }
    i = this.navigationHidden - paramConfiguration.navigationHidden;
    if (i != 0) {
      return i;
    }
    i = this.orientation - paramConfiguration.orientation;
    if (i != 0) {
      return i;
    }
    i = this.screenLayout - paramConfiguration.screenLayout;
    if (i != 0) {
      return i;
    }
    i = this.uiMode - paramConfiguration.uiMode;
    if (i != 0) {
      return i;
    }
    i = this.screenWidthDp - paramConfiguration.screenWidthDp;
    if (i != 0) {
      return i;
    }
    i = this.screenHeightDp - paramConfiguration.screenHeightDp;
    if (i != 0) {
      return i;
    }
    i = this.smallestScreenWidthDp - paramConfiguration.smallestScreenWidthDp;
    if (i != 0) {
      return i;
    }
    i = this.densityDpi - paramConfiguration.densityDpi;
    if (i != 0) {
      return i;
    }
    return this.mOpExtraConfiguration.compareTo(paramConfiguration.mOpExtraConfiguration);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int diff(Configuration paramConfiguration)
  {
    int j = 0;
    int i = j;
    if (paramConfiguration.fontScale > 0.0F)
    {
      i = j;
      if (this.fontScale != paramConfiguration.fontScale) {
        i = 1073741824;
      }
    }
    j = i;
    if (paramConfiguration.mcc != 0)
    {
      j = i;
      if (this.mcc != paramConfiguration.mcc) {
        j = i | 0x1;
      }
    }
    int k = j;
    if (paramConfiguration.mnc != 0)
    {
      k = j;
      if (this.mnc != paramConfiguration.mnc) {
        k = j | 0x2;
      }
    }
    fixUpLocaleList();
    paramConfiguration.fixUpLocaleList();
    i = k;
    if (!paramConfiguration.mLocaleList.isEmpty()) {
      if (!this.mLocaleList.equals(paramConfiguration.mLocaleList)) {
        break label543;
      }
    }
    label543:
    for (i = k;; i = k | 0x4 | 0x2000)
    {
      k = paramConfiguration.screenLayout & 0xC0;
      j = i;
      if (k != 0)
      {
        j = i;
        if (k != (this.screenLayout & 0xC0)) {
          j = i | 0x2000;
        }
      }
      i = j;
      if (paramConfiguration.touchscreen != 0)
      {
        i = j;
        if (this.touchscreen != paramConfiguration.touchscreen) {
          i = j | 0x8;
        }
      }
      j = i;
      if (paramConfiguration.keyboard != 0)
      {
        j = i;
        if (this.keyboard != paramConfiguration.keyboard) {
          j = i | 0x10;
        }
      }
      i = j;
      if (paramConfiguration.keyboardHidden != 0)
      {
        i = j;
        if (this.keyboardHidden != paramConfiguration.keyboardHidden) {
          i = j | 0x20;
        }
      }
      j = i;
      if (paramConfiguration.hardKeyboardHidden != 0)
      {
        j = i;
        if (this.hardKeyboardHidden != paramConfiguration.hardKeyboardHidden) {
          j = i | 0x20;
        }
      }
      i = j;
      if (paramConfiguration.navigation != 0)
      {
        i = j;
        if (this.navigation != paramConfiguration.navigation) {
          i = j | 0x40;
        }
      }
      j = i;
      if (paramConfiguration.navigationHidden != 0)
      {
        j = i;
        if (this.navigationHidden != paramConfiguration.navigationHidden) {
          j = i | 0x20;
        }
      }
      i = j;
      if (paramConfiguration.orientation != 0)
      {
        i = j;
        if (this.orientation != paramConfiguration.orientation) {
          i = j | 0x80;
        }
      }
      j = i;
      if (getScreenLayoutNoDirection(paramConfiguration.screenLayout) != 0)
      {
        j = i;
        if (getScreenLayoutNoDirection(this.screenLayout) != getScreenLayoutNoDirection(paramConfiguration.screenLayout)) {
          j = i | 0x100;
        }
      }
      i = j;
      if (paramConfiguration.uiMode != 0)
      {
        i = j;
        if (this.uiMode != paramConfiguration.uiMode) {
          i = j | 0x200;
        }
      }
      j = i;
      if (paramConfiguration.screenWidthDp != 0)
      {
        j = i;
        if (this.screenWidthDp != paramConfiguration.screenWidthDp) {
          j = i | 0x400;
        }
      }
      i = j;
      if (paramConfiguration.screenHeightDp != 0)
      {
        i = j;
        if (this.screenHeightDp != paramConfiguration.screenHeightDp) {
          i = j | 0x400;
        }
      }
      j = i;
      if (paramConfiguration.smallestScreenWidthDp != 0)
      {
        j = i;
        if (this.smallestScreenWidthDp != paramConfiguration.smallestScreenWidthDp) {
          j = i | 0x800;
        }
      }
      i = j;
      if (paramConfiguration.densityDpi != 0)
      {
        i = j;
        if (this.densityDpi != paramConfiguration.densityDpi) {
          i = j | 0x1000;
        }
      }
      return i | this.mOpExtraConfiguration.diff(paramConfiguration.mOpExtraConfiguration);
    }
  }
  
  public boolean equals(Configuration paramConfiguration)
  {
    if (paramConfiguration == null) {
      return false;
    }
    if (paramConfiguration == this) {
      return true;
    }
    return compareTo(paramConfiguration) == 0;
  }
  
  public boolean equals(Object paramObject)
  {
    try
    {
      boolean bool = equals((Configuration)paramObject);
      return bool;
    }
    catch (ClassCastException paramObject) {}
    return false;
  }
  
  public int getLayoutDirection()
  {
    if ((this.screenLayout & 0xC0) == 128) {
      return 1;
    }
    return 0;
  }
  
  public LocaleList getLocales()
  {
    fixUpLocaleList();
    return this.mLocaleList;
  }
  
  public int hashCode()
  {
    int i = Float.floatToIntBits(this.fontScale);
    int j = this.mcc;
    int k = this.mnc;
    int m = this.mLocaleList.hashCode();
    int n = this.touchscreen;
    int i1 = this.keyboard;
    int i2 = this.keyboardHidden;
    int i3 = this.hardKeyboardHidden;
    int i4 = this.navigation;
    int i5 = this.navigationHidden;
    int i6 = this.orientation;
    int i7 = this.screenLayout;
    int i8 = this.uiMode;
    int i9 = this.screenWidthDp;
    int i10 = this.screenHeightDp;
    int i11 = this.smallestScreenWidthDp;
    int i12 = this.densityDpi;
    return this.mOpExtraConfiguration.hashCode() + (((((((((((((((((i + 527) * 31 + j) * 31 + k) * 31 + m) * 31 + n) * 31 + i1) * 31 + i2) * 31 + i3) * 31 + i4) * 31 + i5) * 31 + i6) * 31 + i7) * 31 + i8) * 31 + i9) * 31 + i10) * 31 + i11) * 31 + i12);
  }
  
  public boolean isLayoutSizeAtLeast(int paramInt)
  {
    boolean bool = false;
    int i = this.screenLayout & 0xF;
    if (i == 0) {
      return false;
    }
    if (i >= paramInt) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isOtherSeqNewer(Configuration paramConfiguration)
  {
    if (paramConfiguration == null) {
      return false;
    }
    if (paramConfiguration.seq == 0) {
      return true;
    }
    if (this.seq == 0) {
      return true;
    }
    int i = paramConfiguration.seq - this.seq;
    if (i > 65536) {
      return false;
    }
    return i > 0;
  }
  
  public boolean isScreenRound()
  {
    return (this.screenLayout & 0x300) == 512;
  }
  
  @Deprecated
  public void makeDefault()
  {
    setToDefaults();
  }
  
  public void readFromParcel(Parcel paramParcel)
  {
    boolean bool = true;
    this.fontScale = paramParcel.readFloat();
    this.mcc = paramParcel.readInt();
    this.mnc = paramParcel.readInt();
    int j = paramParcel.readInt();
    Locale[] arrayOfLocale = new Locale[j];
    int i = 0;
    while (i < j)
    {
      arrayOfLocale[i] = Locale.forLanguageTag(paramParcel.readString());
      i += 1;
    }
    this.mLocaleList = new LocaleList(arrayOfLocale);
    this.locale = this.mLocaleList.get(0);
    if (paramParcel.readInt() == 1) {}
    for (;;)
    {
      this.userSetLocale = bool;
      this.touchscreen = paramParcel.readInt();
      this.keyboard = paramParcel.readInt();
      this.keyboardHidden = paramParcel.readInt();
      this.hardKeyboardHidden = paramParcel.readInt();
      this.navigation = paramParcel.readInt();
      this.navigationHidden = paramParcel.readInt();
      this.orientation = paramParcel.readInt();
      this.screenLayout = paramParcel.readInt();
      this.uiMode = paramParcel.readInt();
      this.screenWidthDp = paramParcel.readInt();
      this.screenHeightDp = paramParcel.readInt();
      this.smallestScreenWidthDp = paramParcel.readInt();
      this.densityDpi = paramParcel.readInt();
      this.compatScreenWidthDp = paramParcel.readInt();
      this.compatScreenHeightDp = paramParcel.readInt();
      this.compatSmallestScreenWidthDp = paramParcel.readInt();
      this.seq = paramParcel.readInt();
      this.mOpExtraConfiguration.readFromParcel(paramParcel);
      return;
      bool = false;
    }
  }
  
  public void setLayoutDirection(Locale paramLocale)
  {
    int i = TextUtils.getLayoutDirectionFromLocale(paramLocale);
    this.screenLayout = (this.screenLayout & 0xFF3F | i + 1 << 6);
  }
  
  public void setLocale(Locale paramLocale)
  {
    if (paramLocale == null) {}
    for (paramLocale = LocaleList.getEmptyLocaleList();; paramLocale = new LocaleList(new Locale[] { paramLocale }))
    {
      setLocales(paramLocale);
      return;
    }
  }
  
  public void setLocales(LocaleList paramLocaleList)
  {
    LocaleList localLocaleList = paramLocaleList;
    if (paramLocaleList == null) {
      localLocaleList = LocaleList.getEmptyLocaleList();
    }
    this.mLocaleList = localLocaleList;
    this.locale = this.mLocaleList.get(0);
    setLayoutDirection(this.locale);
  }
  
  public void setTo(Configuration paramConfiguration)
  {
    Locale localLocale = null;
    this.fontScale = paramConfiguration.fontScale;
    this.mcc = paramConfiguration.mcc;
    this.mnc = paramConfiguration.mnc;
    if (paramConfiguration.locale == null) {}
    for (;;)
    {
      this.locale = localLocale;
      paramConfiguration.fixUpLocaleList();
      this.mLocaleList = paramConfiguration.mLocaleList;
      this.userSetLocale = paramConfiguration.userSetLocale;
      this.touchscreen = paramConfiguration.touchscreen;
      this.keyboard = paramConfiguration.keyboard;
      this.keyboardHidden = paramConfiguration.keyboardHidden;
      this.hardKeyboardHidden = paramConfiguration.hardKeyboardHidden;
      this.navigation = paramConfiguration.navigation;
      this.navigationHidden = paramConfiguration.navigationHidden;
      this.orientation = paramConfiguration.orientation;
      this.screenLayout = paramConfiguration.screenLayout;
      this.uiMode = paramConfiguration.uiMode;
      this.screenWidthDp = paramConfiguration.screenWidthDp;
      this.screenHeightDp = paramConfiguration.screenHeightDp;
      this.smallestScreenWidthDp = paramConfiguration.smallestScreenWidthDp;
      this.densityDpi = paramConfiguration.densityDpi;
      this.compatScreenWidthDp = paramConfiguration.compatScreenWidthDp;
      this.compatScreenHeightDp = paramConfiguration.compatScreenHeightDp;
      this.compatSmallestScreenWidthDp = paramConfiguration.compatSmallestScreenWidthDp;
      this.seq = paramConfiguration.seq;
      this.mOpExtraConfiguration.setTo(paramConfiguration.mOpExtraConfiguration);
      return;
      localLocale = (Locale)paramConfiguration.locale.clone();
    }
  }
  
  public void setToDefaults()
  {
    this.fontScale = 1.0F;
    this.mnc = 0;
    this.mcc = 0;
    this.mLocaleList = LocaleList.getEmptyLocaleList();
    this.locale = null;
    this.userSetLocale = false;
    this.touchscreen = 0;
    this.keyboard = 0;
    this.keyboardHidden = 0;
    this.hardKeyboardHidden = 0;
    this.navigation = 0;
    this.navigationHidden = 0;
    this.orientation = 0;
    this.screenLayout = 0;
    this.uiMode = 0;
    this.compatScreenWidthDp = 0;
    this.screenWidthDp = 0;
    this.compatScreenHeightDp = 0;
    this.screenHeightDp = 0;
    this.compatSmallestScreenWidthDp = 0;
    this.smallestScreenWidthDp = 0;
    this.densityDpi = 0;
    this.seq = 0;
    this.mOpExtraConfiguration.setToDefaults();
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    localStringBuilder.append("{");
    localStringBuilder.append(this.fontScale);
    localStringBuilder.append(" ");
    if (this.mcc != 0)
    {
      localStringBuilder.append(this.mcc);
      localStringBuilder.append("mcc");
      if (this.mnc == 0) {
        break label972;
      }
      localStringBuilder.append(this.mnc);
      localStringBuilder.append("mnc");
      label82:
      fixUpLocaleList();
      if (this.mLocaleList.isEmpty()) {
        break label983;
      }
      localStringBuilder.append(" ");
      localStringBuilder.append(this.mLocaleList);
      label113:
      int i = this.screenLayout & 0xC0;
      switch (i)
      {
      default: 
        localStringBuilder.append(" layoutDir=");
        localStringBuilder.append(i >> 6);
        label173:
        if (this.smallestScreenWidthDp != 0)
        {
          localStringBuilder.append(" sw");
          localStringBuilder.append(this.smallestScreenWidthDp);
          localStringBuilder.append("dp");
          label205:
          if (this.screenWidthDp == 0) {
            break label1038;
          }
          localStringBuilder.append(" w");
          localStringBuilder.append(this.screenWidthDp);
          localStringBuilder.append("dp");
          label237:
          if (this.screenHeightDp == 0) {
            break label1049;
          }
          localStringBuilder.append(" h");
          localStringBuilder.append(this.screenHeightDp);
          localStringBuilder.append("dp");
          label269:
          if (this.densityDpi == 0) {
            break label1060;
          }
          localStringBuilder.append(" ");
          localStringBuilder.append(this.densityDpi);
          localStringBuilder.append("dpi");
          label301:
          switch (this.screenLayout & 0xF)
          {
          default: 
            localStringBuilder.append(" layoutSize=");
            localStringBuilder.append(this.screenLayout & 0xF);
            label364:
            switch (this.screenLayout & 0x30)
            {
            default: 
              localStringBuilder.append(" layoutLong=");
              localStringBuilder.append(this.screenLayout & 0x30);
            case 16: 
              label424:
              switch (this.orientation)
              {
              default: 
                localStringBuilder.append(" orien=");
                localStringBuilder.append(this.orientation);
                label473:
                switch (this.uiMode & 0xF)
                {
                default: 
                  localStringBuilder.append(" uimode=");
                  localStringBuilder.append(this.uiMode & 0xF);
                case 1: 
                  label544:
                  switch (this.uiMode & 0x30)
                  {
                  default: 
                    localStringBuilder.append(" night=");
                    localStringBuilder.append(this.uiMode & 0x30);
                  case 16: 
                    label604:
                    switch (this.touchscreen)
                    {
                    default: 
                      localStringBuilder.append(" touch=");
                      localStringBuilder.append(this.touchscreen);
                      label657:
                      switch (this.keyboard)
                      {
                      default: 
                        localStringBuilder.append(" keys=");
                        localStringBuilder.append(this.keyboard);
                        label709:
                        switch (this.keyboardHidden)
                        {
                        default: 
                          localStringBuilder.append("/");
                          localStringBuilder.append(this.keyboardHidden);
                          label761:
                          switch (this.hardKeyboardHidden)
                          {
                          default: 
                            localStringBuilder.append("/");
                            localStringBuilder.append(this.hardKeyboardHidden);
                            label809:
                            switch (this.navigation)
                            {
                            default: 
                              localStringBuilder.append(" nav=");
                              localStringBuilder.append(this.navigation);
                              label865:
                              switch (this.navigationHidden)
                              {
                              default: 
                                localStringBuilder.append("/");
                                localStringBuilder.append(this.navigationHidden);
                              }
                              break;
                            }
                            break;
                          }
                          break;
                        }
                        break;
                      }
                      break;
                    }
                    break;
                  }
                  break;
                }
                break;
              }
              break;
            }
            break;
          }
        }
        break;
      }
    }
    for (;;)
    {
      if (this.seq != 0)
      {
        localStringBuilder.append(" s.");
        localStringBuilder.append(this.seq);
      }
      localStringBuilder.append(this.mOpExtraConfiguration.toString());
      localStringBuilder.append('}');
      return localStringBuilder.toString();
      localStringBuilder.append("?mcc");
      break;
      label972:
      localStringBuilder.append("?mnc");
      break label82;
      label983:
      localStringBuilder.append(" ?localeList");
      break label113;
      localStringBuilder.append(" ?layoutDir");
      break label173;
      localStringBuilder.append(" ldltr");
      break label173;
      localStringBuilder.append(" ldrtl");
      break label173;
      localStringBuilder.append(" ?swdp");
      break label205;
      label1038:
      localStringBuilder.append(" ?wdp");
      break label237;
      label1049:
      localStringBuilder.append(" ?hdp");
      break label269;
      label1060:
      localStringBuilder.append(" ?density");
      break label301;
      localStringBuilder.append(" ?lsize");
      break label364;
      localStringBuilder.append(" smll");
      break label364;
      localStringBuilder.append(" nrml");
      break label364;
      localStringBuilder.append(" lrg");
      break label364;
      localStringBuilder.append(" xlrg");
      break label364;
      localStringBuilder.append(" ?long");
      break label424;
      localStringBuilder.append(" long");
      break label424;
      localStringBuilder.append(" ?orien");
      break label473;
      localStringBuilder.append(" land");
      break label473;
      localStringBuilder.append(" port");
      break label473;
      localStringBuilder.append(" ?uimode");
      break label544;
      localStringBuilder.append(" desk");
      break label544;
      localStringBuilder.append(" car");
      break label544;
      localStringBuilder.append(" television");
      break label544;
      localStringBuilder.append(" appliance");
      break label544;
      localStringBuilder.append(" watch");
      break label544;
      localStringBuilder.append(" ?night");
      break label604;
      localStringBuilder.append(" night");
      break label604;
      localStringBuilder.append(" ?touch");
      break label657;
      localStringBuilder.append(" -touch");
      break label657;
      localStringBuilder.append(" stylus");
      break label657;
      localStringBuilder.append(" finger");
      break label657;
      localStringBuilder.append(" ?keyb");
      break label709;
      localStringBuilder.append(" -keyb");
      break label709;
      localStringBuilder.append(" qwerty");
      break label709;
      localStringBuilder.append(" 12key");
      break label709;
      localStringBuilder.append("/?");
      break label761;
      localStringBuilder.append("/v");
      break label761;
      localStringBuilder.append("/h");
      break label761;
      localStringBuilder.append("/s");
      break label761;
      localStringBuilder.append("/?");
      break label809;
      localStringBuilder.append("/v");
      break label809;
      localStringBuilder.append("/h");
      break label809;
      localStringBuilder.append(" ?nav");
      break label865;
      localStringBuilder.append(" -nav");
      break label865;
      localStringBuilder.append(" dpad");
      break label865;
      localStringBuilder.append(" tball");
      break label865;
      localStringBuilder.append(" wheel");
      break label865;
      localStringBuilder.append("/?");
      continue;
      localStringBuilder.append("/v");
      continue;
      localStringBuilder.append("/h");
    }
  }
  
  public int updateFrom(Configuration paramConfiguration)
  {
    int j = 0;
    int i = j;
    if (paramConfiguration.fontScale > 0.0F)
    {
      i = j;
      if (this.fontScale != paramConfiguration.fontScale)
      {
        i = 1073741824;
        this.fontScale = paramConfiguration.fontScale;
      }
    }
    j = i;
    if (paramConfiguration.mcc != 0)
    {
      j = i;
      if (this.mcc != paramConfiguration.mcc)
      {
        j = i | 0x1;
        this.mcc = paramConfiguration.mcc;
      }
    }
    int k = j;
    if (paramConfiguration.mnc != 0)
    {
      k = j;
      if (this.mnc != paramConfiguration.mnc)
      {
        k = j | 0x2;
        this.mnc = paramConfiguration.mnc;
      }
    }
    fixUpLocaleList();
    paramConfiguration.fixUpLocaleList();
    i = k;
    if (!paramConfiguration.mLocaleList.isEmpty())
    {
      if (this.mLocaleList.equals(paramConfiguration.mLocaleList)) {
        i = k;
      }
    }
    else
    {
      k = paramConfiguration.screenLayout & 0xC0;
      j = i;
      if (k != 0)
      {
        j = i;
        if (k != (this.screenLayout & 0xC0))
        {
          this.screenLayout = (this.screenLayout & 0xFF3F | k);
          j = i | 0x2000;
        }
      }
      i = j;
      if (paramConfiguration.userSetLocale) {
        if (this.userSetLocale)
        {
          i = j;
          if ((j & 0x4) == 0) {}
        }
        else
        {
          i = j | 0x4;
          this.userSetLocale = true;
        }
      }
      j = i;
      if (paramConfiguration.touchscreen != 0)
      {
        j = i;
        if (this.touchscreen != paramConfiguration.touchscreen)
        {
          j = i | 0x8;
          this.touchscreen = paramConfiguration.touchscreen;
        }
      }
      i = j;
      if (paramConfiguration.keyboard != 0)
      {
        i = j;
        if (this.keyboard != paramConfiguration.keyboard)
        {
          i = j | 0x10;
          this.keyboard = paramConfiguration.keyboard;
        }
      }
      j = i;
      if (paramConfiguration.keyboardHidden != 0)
      {
        j = i;
        if (this.keyboardHidden != paramConfiguration.keyboardHidden)
        {
          j = i | 0x20;
          this.keyboardHidden = paramConfiguration.keyboardHidden;
        }
      }
      i = j;
      if (paramConfiguration.hardKeyboardHidden != 0)
      {
        i = j;
        if (this.hardKeyboardHidden != paramConfiguration.hardKeyboardHidden)
        {
          i = j | 0x20;
          this.hardKeyboardHidden = paramConfiguration.hardKeyboardHidden;
        }
      }
      j = i;
      if (paramConfiguration.navigation != 0)
      {
        j = i;
        if (this.navigation != paramConfiguration.navigation)
        {
          j = i | 0x40;
          this.navigation = paramConfiguration.navigation;
        }
      }
      i = j;
      if (paramConfiguration.navigationHidden != 0)
      {
        i = j;
        if (this.navigationHidden != paramConfiguration.navigationHidden)
        {
          i = j | 0x20;
          this.navigationHidden = paramConfiguration.navigationHidden;
        }
      }
      j = i;
      if (paramConfiguration.orientation != 0)
      {
        j = i;
        if (this.orientation != paramConfiguration.orientation)
        {
          j = i | 0x80;
          this.orientation = paramConfiguration.orientation;
        }
      }
      k = j;
      if (getScreenLayoutNoDirection(paramConfiguration.screenLayout) != 0)
      {
        k = j;
        if (getScreenLayoutNoDirection(this.screenLayout) != getScreenLayoutNoDirection(paramConfiguration.screenLayout))
        {
          k = j | 0x100;
          if ((paramConfiguration.screenLayout & 0xC0) != 0) {
            break label919;
          }
        }
      }
    }
    label919:
    for (this.screenLayout = (this.screenLayout & 0xC0 | paramConfiguration.screenLayout);; this.screenLayout = paramConfiguration.screenLayout)
    {
      i = k;
      if (paramConfiguration.uiMode != 0)
      {
        i = k;
        if (this.uiMode != paramConfiguration.uiMode)
        {
          j = k | 0x200;
          if ((paramConfiguration.uiMode & 0xF) != 0) {
            this.uiMode = (this.uiMode & 0xFFFFFFF0 | paramConfiguration.uiMode & 0xF);
          }
          i = j;
          if ((paramConfiguration.uiMode & 0x30) != 0)
          {
            this.uiMode = (this.uiMode & 0xFFFFFFCF | paramConfiguration.uiMode & 0x30);
            i = j;
          }
        }
      }
      j = i;
      if (paramConfiguration.screenWidthDp != 0)
      {
        j = i;
        if (this.screenWidthDp != paramConfiguration.screenWidthDp)
        {
          j = i | 0x400;
          this.screenWidthDp = paramConfiguration.screenWidthDp;
        }
      }
      i = j;
      if (paramConfiguration.screenHeightDp != 0)
      {
        i = j;
        if (this.screenHeightDp != paramConfiguration.screenHeightDp)
        {
          i = j | 0x400;
          this.screenHeightDp = paramConfiguration.screenHeightDp;
        }
      }
      j = i;
      if (paramConfiguration.smallestScreenWidthDp != 0)
      {
        j = i;
        if (this.smallestScreenWidthDp != paramConfiguration.smallestScreenWidthDp)
        {
          j = i | 0x800;
          this.smallestScreenWidthDp = paramConfiguration.smallestScreenWidthDp;
        }
      }
      i = j;
      if (paramConfiguration.densityDpi != 0)
      {
        i = j;
        if (this.densityDpi != paramConfiguration.densityDpi)
        {
          i = j | 0x1000;
          this.densityDpi = paramConfiguration.densityDpi;
        }
      }
      if (paramConfiguration.compatScreenWidthDp != 0) {
        this.compatScreenWidthDp = paramConfiguration.compatScreenWidthDp;
      }
      if (paramConfiguration.compatScreenHeightDp != 0) {
        this.compatScreenHeightDp = paramConfiguration.compatScreenHeightDp;
      }
      if (paramConfiguration.compatSmallestScreenWidthDp != 0) {
        this.compatSmallestScreenWidthDp = paramConfiguration.compatSmallestScreenWidthDp;
      }
      if (paramConfiguration.seq != 0) {
        this.seq = paramConfiguration.seq;
      }
      return i | this.mOpExtraConfiguration.updateFrom(paramConfiguration.mOpExtraConfiguration);
      j = k | 0x4;
      this.mLocaleList = paramConfiguration.mLocaleList;
      i = j;
      if (paramConfiguration.locale.equals(this.locale)) {
        break;
      }
      this.locale = ((Locale)paramConfiguration.locale.clone());
      i = j | 0x2000;
      setLayoutDirection(this.locale);
      break;
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeFloat(this.fontScale);
    paramParcel.writeInt(this.mcc);
    paramParcel.writeInt(this.mnc);
    fixUpLocaleList();
    int j = this.mLocaleList.size();
    paramParcel.writeInt(j);
    int i = 0;
    while (i < j)
    {
      paramParcel.writeString(this.mLocaleList.get(i).toLanguageTag());
      i += 1;
    }
    if (this.userSetLocale) {
      paramParcel.writeInt(1);
    }
    for (;;)
    {
      paramParcel.writeInt(this.touchscreen);
      paramParcel.writeInt(this.keyboard);
      paramParcel.writeInt(this.keyboardHidden);
      paramParcel.writeInt(this.hardKeyboardHidden);
      paramParcel.writeInt(this.navigation);
      paramParcel.writeInt(this.navigationHidden);
      paramParcel.writeInt(this.orientation);
      paramParcel.writeInt(this.screenLayout);
      paramParcel.writeInt(this.uiMode);
      paramParcel.writeInt(this.screenWidthDp);
      paramParcel.writeInt(this.screenHeightDp);
      paramParcel.writeInt(this.smallestScreenWidthDp);
      paramParcel.writeInt(this.densityDpi);
      paramParcel.writeInt(this.compatScreenWidthDp);
      paramParcel.writeInt(this.compatScreenHeightDp);
      paramParcel.writeInt(this.compatSmallestScreenWidthDp);
      paramParcel.writeInt(this.seq);
      this.mOpExtraConfiguration.writeToParcel(paramParcel, paramInt);
      return;
      paramParcel.writeInt(0);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/res/Configuration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */