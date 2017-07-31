package android.graphics;

import android.util.Xml;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class FontListParser
{
  private static final Pattern FILENAME_WHITESPACE_PATTERN = Pattern.compile("^[ \\n\\r\\t]+|[ \\n\\r\\t]+$");
  private static final Pattern STYLE_VALUE_PATTERN = Pattern.compile("-?(([0-9]+(\\.[0-9]+)?)|(\\.[0-9]+))");
  private static final Pattern TAG_PATTERN = Pattern.compile("[\\x00-\\xFF]{4}");
  
  public static Config parse(InputStream paramInputStream)
    throws XmlPullParserException, IOException
  {
    try
    {
      Object localObject1 = Xml.newPullParser();
      ((XmlPullParser)localObject1).setInput(paramInputStream, null);
      ((XmlPullParser)localObject1).nextTag();
      localObject1 = readFamilies((XmlPullParser)localObject1);
      return (Config)localObject1;
    }
    finally
    {
      paramInputStream.close();
    }
  }
  
  private static Alias readAlias(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    Alias localAlias = new Alias();
    localAlias.name = paramXmlPullParser.getAttributeValue(null, "name");
    localAlias.toName = paramXmlPullParser.getAttributeValue(null, "to");
    String str = paramXmlPullParser.getAttributeValue(null, "weight");
    if (str == null) {}
    for (localAlias.weight = 400;; localAlias.weight = Integer.parseInt(str))
    {
      skip(paramXmlPullParser);
      return localAlias;
    }
  }
  
  private static Axis readAxis(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    String str = paramXmlPullParser.getAttributeValue(null, "tag");
    if ((str != null) && (TAG_PATTERN.matcher(str).matches()))
    {
      int i = str.charAt(0);
      int j = str.charAt(1);
      int k = str.charAt(2);
      int m = str.charAt(3);
      str = paramXmlPullParser.getAttributeValue(null, "stylevalue");
      if ((str != null) && (STYLE_VALUE_PATTERN.matcher(str).matches()))
      {
        float f = Float.parseFloat(str);
        skip(paramXmlPullParser);
        return new Axis((i << 24) + (j << 16) + (k << 8) + m, f);
      }
    }
    else
    {
      throw new XmlPullParserException("Invalid tag attribute value.", paramXmlPullParser, null);
    }
    throw new XmlPullParserException("Invalid styleValue attribute value.", paramXmlPullParser, null);
  }
  
  private static Config readFamilies(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    Config localConfig = new Config();
    paramXmlPullParser.require(2, null, "familyset");
    while (paramXmlPullParser.next() != 3) {
      if (paramXmlPullParser.getEventType() == 2)
      {
        String str = paramXmlPullParser.getName();
        if (str.equals("family")) {
          localConfig.families.add(readFamily(paramXmlPullParser));
        } else if (str.equals("alias")) {
          localConfig.aliases.add(readAlias(paramXmlPullParser));
        } else {
          skip(paramXmlPullParser);
        }
      }
    }
    return localConfig;
  }
  
  private static Family readFamily(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    String str1 = paramXmlPullParser.getAttributeValue(null, "name");
    String str2 = paramXmlPullParser.getAttributeValue(null, "lang");
    String str3 = paramXmlPullParser.getAttributeValue(null, "variant");
    ArrayList localArrayList = new ArrayList();
    while (paramXmlPullParser.next() != 3) {
      if (paramXmlPullParser.getEventType() == 2) {
        if (paramXmlPullParser.getName().equals("font")) {
          localArrayList.add(readFont(paramXmlPullParser));
        } else {
          skip(paramXmlPullParser);
        }
      }
    }
    return new Family(str1, localArrayList, str2, str3);
  }
  
  private static Font readFont(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    Object localObject1 = paramXmlPullParser.getAttributeValue(null, "index");
    int i;
    Object localObject2;
    int j;
    label47:
    boolean bool;
    if (localObject1 == null)
    {
      i = 0;
      localObject1 = new ArrayList();
      localObject2 = paramXmlPullParser.getAttributeValue(null, "weight");
      if (localObject2 != null) {
        break label151;
      }
      j = 400;
      bool = "italic".equals(paramXmlPullParser.getAttributeValue(null, "style"));
      localObject2 = new StringBuilder();
    }
    for (;;)
    {
      if (paramXmlPullParser.next() == 3) {
        break label167;
      }
      if (paramXmlPullParser.getEventType() == 4) {
        ((StringBuilder)localObject2).append(paramXmlPullParser.getText());
      }
      if (paramXmlPullParser.getEventType() == 2)
      {
        if (paramXmlPullParser.getName().equals("axis"))
        {
          ((List)localObject1).add(readAxis(paramXmlPullParser));
          continue;
          i = Integer.parseInt((String)localObject1);
          break;
          label151:
          j = Integer.parseInt((String)localObject2);
          break label47;
        }
        skip(paramXmlPullParser);
      }
    }
    label167:
    return new Font("/system/fonts/" + FILENAME_WHITESPACE_PATTERN.matcher((CharSequence)localObject2).replaceAll(""), i, (List)localObject1, j, bool);
  }
  
  private static void skip(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    int i = 1;
    while (i > 0) {
      switch (paramXmlPullParser.next())
      {
      default: 
        break;
      case 2: 
        i += 1;
        break;
      case 3: 
        i -= 1;
      }
    }
  }
  
  public static class Alias
  {
    public String name;
    public String toName;
    public int weight;
  }
  
  public static class Axis
  {
    public final float styleValue;
    public final int tag;
    
    Axis(int paramInt, float paramFloat)
    {
      this.tag = paramInt;
      this.styleValue = paramFloat;
    }
  }
  
  public static class Config
  {
    public List<FontListParser.Alias> aliases = new ArrayList();
    public List<FontListParser.Family> families = new ArrayList();
  }
  
  public static class Family
  {
    public List<FontListParser.Font> fonts;
    public String lang;
    public String name;
    public String variant;
    
    public Family(String paramString1, List<FontListParser.Font> paramList, String paramString2, String paramString3)
    {
      this.name = paramString1;
      this.fonts = paramList;
      this.lang = paramString2;
      this.variant = paramString3;
    }
  }
  
  public static class Font
  {
    public final List<FontListParser.Axis> axes;
    public String fontName;
    public boolean isItalic;
    public int ttcIndex;
    public int weight;
    
    Font(String paramString, int paramInt1, List<FontListParser.Axis> paramList, int paramInt2, boolean paramBoolean)
    {
      this.fontName = paramString;
      this.ttcIndex = paramInt1;
      this.axes = paramList;
      this.weight = paramInt2;
      this.isItalic = paramBoolean;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/FontListParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */