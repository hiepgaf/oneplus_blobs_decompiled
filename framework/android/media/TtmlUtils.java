package android.media;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class TtmlUtils
{
  public static final String ATTR_BEGIN = "begin";
  public static final String ATTR_DURATION = "dur";
  public static final String ATTR_END = "end";
  private static final Pattern CLOCK_TIME = Pattern.compile("^([0-9][0-9]+):([0-9][0-9]):([0-9][0-9])(?:(\\.[0-9]+)|:([0-9][0-9])(?:\\.([0-9]+))?)?$");
  public static final long INVALID_TIMESTAMP = Long.MAX_VALUE;
  private static final Pattern OFFSET_TIME = Pattern.compile("^([0-9]+(?:\\.[0-9]+)?)(h|m|s|ms|f|t)$");
  public static final String PCDATA = "#pcdata";
  public static final String TAG_BODY = "body";
  public static final String TAG_BR = "br";
  public static final String TAG_DIV = "div";
  public static final String TAG_HEAD = "head";
  public static final String TAG_LAYOUT = "layout";
  public static final String TAG_METADATA = "metadata";
  public static final String TAG_P = "p";
  public static final String TAG_REGION = "region";
  public static final String TAG_SMPTE_DATA = "smpte:data";
  public static final String TAG_SMPTE_IMAGE = "smpte:image";
  public static final String TAG_SMPTE_INFORMATION = "smpte:information";
  public static final String TAG_SPAN = "span";
  public static final String TAG_STYLE = "style";
  public static final String TAG_STYLING = "styling";
  public static final String TAG_TT = "tt";
  
  public static String applyDefaultSpacePolicy(String paramString)
  {
    return applySpacePolicy(paramString, true);
  }
  
  public static String applySpacePolicy(String paramString, boolean paramBoolean)
  {
    paramString = paramString.replaceAll("\r\n", "\n").replaceAll(" *\n *", "\n");
    if (paramBoolean) {
      paramString = paramString.replaceAll("\n", " ");
    }
    for (;;)
    {
      return paramString.replaceAll("[ \t\\x0B\f\r]+", " ");
    }
  }
  
  public static String extractText(TtmlNode paramTtmlNode, long paramLong1, long paramLong2)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    extractText(paramTtmlNode, paramLong1, paramLong2, localStringBuilder, false);
    return localStringBuilder.toString().replaceAll("\n$", "");
  }
  
  private static void extractText(TtmlNode paramTtmlNode, long paramLong1, long paramLong2, StringBuilder paramStringBuilder, boolean paramBoolean)
  {
    if ((paramTtmlNode.mName.equals("#pcdata")) && (paramBoolean)) {
      paramStringBuilder.append(paramTtmlNode.mText);
    }
    boolean bool2;
    int j;
    do
    {
      do
      {
        return;
        if ((paramTtmlNode.mName.equals("br")) && (paramBoolean))
        {
          paramStringBuilder.append("\n");
          return;
        }
      } while ((paramTtmlNode.mName.equals("metadata")) || (!paramTtmlNode.isActive(paramLong1, paramLong2)));
      bool2 = paramTtmlNode.mName.equals("p");
      j = paramStringBuilder.length();
      int i = 0;
      if (i < paramTtmlNode.mChildren.size())
      {
        TtmlNode localTtmlNode = (TtmlNode)paramTtmlNode.mChildren.get(i);
        if (!bool2) {}
        for (boolean bool1 = paramBoolean;; bool1 = true)
        {
          extractText(localTtmlNode, paramLong1, paramLong2, paramStringBuilder, bool1);
          i += 1;
          break;
        }
      }
    } while ((!bool2) || (j == paramStringBuilder.length()));
    paramStringBuilder.append("\n");
  }
  
  public static String extractTtmlFragment(TtmlNode paramTtmlNode, long paramLong1, long paramLong2)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    extractTtmlFragment(paramTtmlNode, paramLong1, paramLong2, localStringBuilder);
    return localStringBuilder.toString();
  }
  
  private static void extractTtmlFragment(TtmlNode paramTtmlNode, long paramLong1, long paramLong2, StringBuilder paramStringBuilder)
  {
    if (paramTtmlNode.mName.equals("#pcdata")) {
      paramStringBuilder.append(paramTtmlNode.mText);
    }
    do
    {
      return;
      if (paramTtmlNode.mName.equals("br"))
      {
        paramStringBuilder.append("<br/>");
        return;
      }
    } while (!paramTtmlNode.isActive(paramLong1, paramLong2));
    paramStringBuilder.append("<");
    paramStringBuilder.append(paramTtmlNode.mName);
    paramStringBuilder.append(paramTtmlNode.mAttributes);
    paramStringBuilder.append(">");
    int i = 0;
    while (i < paramTtmlNode.mChildren.size())
    {
      extractTtmlFragment((TtmlNode)paramTtmlNode.mChildren.get(i), paramLong1, paramLong2, paramStringBuilder);
      i += 1;
    }
    paramStringBuilder.append("</");
    paramStringBuilder.append(paramTtmlNode.mName);
    paramStringBuilder.append(">");
  }
  
  public static long parseTimeExpression(String paramString, int paramInt1, int paramInt2, int paramInt3)
    throws NumberFormatException
  {
    Matcher localMatcher = CLOCK_TIME.matcher(paramString);
    double d1;
    double d2;
    if (localMatcher.matches())
    {
      double d4 = Long.parseLong(localMatcher.group(1)) * 3600L;
      double d5 = Long.parseLong(localMatcher.group(2)) * 60L;
      double d6 = Long.parseLong(localMatcher.group(3));
      paramString = localMatcher.group(4);
      if (paramString != null)
      {
        d1 = Double.parseDouble(paramString);
        paramString = localMatcher.group(5);
        if (paramString == null) {
          break label153;
        }
        d2 = Long.parseLong(paramString) / paramInt1;
        label99:
        paramString = localMatcher.group(6);
        if (paramString == null) {
          break label159;
        }
      }
      label153:
      label159:
      for (double d3 = Long.parseLong(paramString) / paramInt2 / paramInt1;; d3 = 0.0D)
      {
        return (1000.0D * (d4 + d5 + d6 + d1 + d2 + d3));
        d1 = 0.0D;
        break;
        d2 = 0.0D;
        break label99;
      }
    }
    localMatcher = OFFSET_TIME.matcher(paramString);
    if (localMatcher.matches())
    {
      d2 = Double.parseDouble(localMatcher.group(1));
      paramString = localMatcher.group(2);
      if (paramString.equals("h")) {
        d1 = d2 * 3.6E9D;
      }
      for (;;)
      {
        return d1;
        if (paramString.equals("m"))
        {
          d1 = d2 * 6.0E7D;
        }
        else if (paramString.equals("s"))
        {
          d1 = d2 * 1000000.0D;
        }
        else if (paramString.equals("ms"))
        {
          d1 = d2 * 1000.0D;
        }
        else if (paramString.equals("f"))
        {
          d1 = d2 / paramInt1 * 1000000.0D;
        }
        else
        {
          d1 = d2;
          if (paramString.equals("t")) {
            d1 = d2 / paramInt3 * 1000000.0D;
          }
        }
      }
    }
    throw new NumberFormatException("Malformed time expression : " + paramString);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/TtmlUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */