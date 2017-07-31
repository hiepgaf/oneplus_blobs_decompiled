package com.android.server.firewall;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.PatternMatcher;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

abstract class StringFilter
  implements Filter
{
  public static final FilterFactory ACTION;
  private static final String ATTR_CONTAINS = "contains";
  private static final String ATTR_EQUALS = "equals";
  private static final String ATTR_IS_NULL = "isNull";
  private static final String ATTR_PATTERN = "pattern";
  private static final String ATTR_REGEX = "regex";
  private static final String ATTR_STARTS_WITH = "startsWith";
  public static final ValueProvider COMPONENT = new ValueProvider("component")
  {
    public String getValue(ComponentName paramAnonymousComponentName, Intent paramAnonymousIntent, String paramAnonymousString)
    {
      if (paramAnonymousComponentName != null) {
        return paramAnonymousComponentName.flattenToString();
      }
      return null;
    }
  };
  public static final ValueProvider COMPONENT_NAME = new ValueProvider("component-name")
  {
    public String getValue(ComponentName paramAnonymousComponentName, Intent paramAnonymousIntent, String paramAnonymousString)
    {
      if (paramAnonymousComponentName != null) {
        return paramAnonymousComponentName.getClassName();
      }
      return null;
    }
  };
  public static final ValueProvider COMPONENT_PACKAGE = new ValueProvider("component-package")
  {
    public String getValue(ComponentName paramAnonymousComponentName, Intent paramAnonymousIntent, String paramAnonymousString)
    {
      if (paramAnonymousComponentName != null) {
        return paramAnonymousComponentName.getPackageName();
      }
      return null;
    }
  };
  public static final ValueProvider DATA;
  public static final ValueProvider HOST = new ValueProvider("host")
  {
    public String getValue(ComponentName paramAnonymousComponentName, Intent paramAnonymousIntent, String paramAnonymousString)
    {
      paramAnonymousComponentName = paramAnonymousIntent.getData();
      if (paramAnonymousComponentName != null) {
        return paramAnonymousComponentName.getHost();
      }
      return null;
    }
  };
  public static final ValueProvider MIME_TYPE;
  public static final ValueProvider PATH = new ValueProvider("path")
  {
    public String getValue(ComponentName paramAnonymousComponentName, Intent paramAnonymousIntent, String paramAnonymousString)
    {
      paramAnonymousComponentName = paramAnonymousIntent.getData();
      if (paramAnonymousComponentName != null) {
        return paramAnonymousComponentName.getPath();
      }
      return null;
    }
  };
  public static final ValueProvider SCHEME;
  public static final ValueProvider SSP;
  private final ValueProvider mValueProvider;
  
  static
  {
    ACTION = new ValueProvider("action")
    {
      public String getValue(ComponentName paramAnonymousComponentName, Intent paramAnonymousIntent, String paramAnonymousString)
      {
        return paramAnonymousIntent.getAction();
      }
    };
    DATA = new ValueProvider("data")
    {
      public String getValue(ComponentName paramAnonymousComponentName, Intent paramAnonymousIntent, String paramAnonymousString)
      {
        paramAnonymousComponentName = paramAnonymousIntent.getData();
        if (paramAnonymousComponentName != null) {
          return paramAnonymousComponentName.toString();
        }
        return null;
      }
    };
    MIME_TYPE = new ValueProvider("mime-type")
    {
      public String getValue(ComponentName paramAnonymousComponentName, Intent paramAnonymousIntent, String paramAnonymousString)
      {
        return paramAnonymousString;
      }
    };
    SCHEME = new ValueProvider("scheme")
    {
      public String getValue(ComponentName paramAnonymousComponentName, Intent paramAnonymousIntent, String paramAnonymousString)
      {
        paramAnonymousComponentName = paramAnonymousIntent.getData();
        if (paramAnonymousComponentName != null) {
          return paramAnonymousComponentName.getScheme();
        }
        return null;
      }
    };
    SSP = new ValueProvider("scheme-specific-part")
    {
      public String getValue(ComponentName paramAnonymousComponentName, Intent paramAnonymousIntent, String paramAnonymousString)
      {
        paramAnonymousComponentName = paramAnonymousIntent.getData();
        if (paramAnonymousComponentName != null) {
          return paramAnonymousComponentName.getSchemeSpecificPart();
        }
        return null;
      }
    };
  }
  
  private StringFilter(ValueProvider paramValueProvider)
  {
    this.mValueProvider = paramValueProvider;
  }
  
  private static StringFilter getFilter(ValueProvider paramValueProvider, XmlPullParser paramXmlPullParser, int paramInt)
  {
    String str = paramXmlPullParser.getAttributeName(paramInt);
    switch (str.charAt(0))
    {
    default: 
      return null;
    case 'e': 
      if (!str.equals("equals")) {
        return null;
      }
      return new EqualsFilter(paramValueProvider, paramXmlPullParser.getAttributeValue(paramInt));
    case 'i': 
      if (!str.equals("isNull")) {
        return null;
      }
      return new IsNullFilter(paramValueProvider, paramXmlPullParser.getAttributeValue(paramInt));
    case 's': 
      if (!str.equals("startsWith")) {
        return null;
      }
      return new StartsWithFilter(paramValueProvider, paramXmlPullParser.getAttributeValue(paramInt));
    case 'c': 
      if (!str.equals("contains")) {
        return null;
      }
      return new ContainsFilter(paramValueProvider, paramXmlPullParser.getAttributeValue(paramInt));
    case 'p': 
      if (!str.equals("pattern")) {
        return null;
      }
      return new PatternStringFilter(paramValueProvider, paramXmlPullParser.getAttributeValue(paramInt));
    }
    if (!str.equals("regex")) {
      return null;
    }
    return new RegexFilter(paramValueProvider, paramXmlPullParser.getAttributeValue(paramInt));
  }
  
  public static StringFilter readFromXml(ValueProvider paramValueProvider, XmlPullParser paramXmlPullParser)
    throws IOException, XmlPullParserException
  {
    Object localObject1 = null;
    int i = 0;
    while (i < paramXmlPullParser.getAttributeCount())
    {
      StringFilter localStringFilter = getFilter(paramValueProvider, paramXmlPullParser, i);
      Object localObject2 = localObject1;
      if (localStringFilter != null)
      {
        if (localObject1 != null) {
          throw new XmlPullParserException("Multiple string filter attributes found");
        }
        localObject2 = localStringFilter;
      }
      i += 1;
      localObject1 = localObject2;
    }
    paramXmlPullParser = (XmlPullParser)localObject1;
    if (localObject1 == null) {
      paramXmlPullParser = new IsNullFilter(paramValueProvider, false);
    }
    return paramXmlPullParser;
  }
  
  public boolean matches(IntentFirewall paramIntentFirewall, ComponentName paramComponentName, Intent paramIntent, int paramInt1, int paramInt2, String paramString, int paramInt3)
  {
    return matchesValue(this.mValueProvider.getValue(paramComponentName, paramIntent, paramString));
  }
  
  protected abstract boolean matchesValue(String paramString);
  
  private static class ContainsFilter
    extends StringFilter
  {
    private final String mFilterValue;
    
    public ContainsFilter(StringFilter.ValueProvider paramValueProvider, String paramString)
    {
      super(null);
      this.mFilterValue = paramString;
    }
    
    public boolean matchesValue(String paramString)
    {
      if (paramString != null) {
        return paramString.contains(this.mFilterValue);
      }
      return false;
    }
  }
  
  private static class EqualsFilter
    extends StringFilter
  {
    private final String mFilterValue;
    
    public EqualsFilter(StringFilter.ValueProvider paramValueProvider, String paramString)
    {
      super(null);
      this.mFilterValue = paramString;
    }
    
    public boolean matchesValue(String paramString)
    {
      if (paramString != null) {
        return paramString.equals(this.mFilterValue);
      }
      return false;
    }
  }
  
  private static class IsNullFilter
    extends StringFilter
  {
    private final boolean mIsNull;
    
    public IsNullFilter(StringFilter.ValueProvider paramValueProvider, String paramString)
    {
      super(null);
      this.mIsNull = Boolean.parseBoolean(paramString);
    }
    
    public IsNullFilter(StringFilter.ValueProvider paramValueProvider, boolean paramBoolean)
    {
      super(null);
      this.mIsNull = paramBoolean;
    }
    
    public boolean matchesValue(String paramString)
    {
      if (paramString == null) {}
      for (int i = 1; i == this.mIsNull; i = 0) {
        return true;
      }
      return false;
    }
  }
  
  private static class PatternStringFilter
    extends StringFilter
  {
    private final PatternMatcher mPattern;
    
    public PatternStringFilter(StringFilter.ValueProvider paramValueProvider, String paramString)
    {
      super(null);
      this.mPattern = new PatternMatcher(paramString, 2);
    }
    
    public boolean matchesValue(String paramString)
    {
      if (paramString != null) {
        return this.mPattern.match(paramString);
      }
      return false;
    }
  }
  
  private static class RegexFilter
    extends StringFilter
  {
    private final Pattern mPattern;
    
    public RegexFilter(StringFilter.ValueProvider paramValueProvider, String paramString)
    {
      super(null);
      this.mPattern = Pattern.compile(paramString);
    }
    
    public boolean matchesValue(String paramString)
    {
      if (paramString != null) {
        return this.mPattern.matcher(paramString).matches();
      }
      return false;
    }
  }
  
  private static class StartsWithFilter
    extends StringFilter
  {
    private final String mFilterValue;
    
    public StartsWithFilter(StringFilter.ValueProvider paramValueProvider, String paramString)
    {
      super(null);
      this.mFilterValue = paramString;
    }
    
    public boolean matchesValue(String paramString)
    {
      if (paramString != null) {
        return paramString.startsWith(this.mFilterValue);
      }
      return false;
    }
  }
  
  private static abstract class ValueProvider
    extends FilterFactory
  {
    protected ValueProvider(String paramString)
    {
      super();
    }
    
    public abstract String getValue(ComponentName paramComponentName, Intent paramIntent, String paramString);
    
    public Filter newFilter(XmlPullParser paramXmlPullParser)
      throws IOException, XmlPullParserException
    {
      return StringFilter.readFromXml(this, paramXmlPullParser);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/firewall/StringFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */