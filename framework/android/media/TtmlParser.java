package android.media;

import android.text.TextUtils;
import android.util.Log;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

class TtmlParser
{
  private static final int DEFAULT_FRAMERATE = 30;
  private static final int DEFAULT_SUBFRAMERATE = 1;
  private static final int DEFAULT_TICKRATE = 1;
  static final String TAG = "TtmlParser";
  private long mCurrentRunId;
  private final TtmlNodeListener mListener;
  private XmlPullParser mParser;
  
  public TtmlParser(TtmlNodeListener paramTtmlNodeListener)
  {
    this.mListener = paramTtmlNodeListener;
  }
  
  private void extractAttribute(XmlPullParser paramXmlPullParser, int paramInt, StringBuilder paramStringBuilder)
  {
    paramStringBuilder.append(" ");
    paramStringBuilder.append(paramXmlPullParser.getAttributeName(paramInt));
    paramStringBuilder.append("=\"");
    paramStringBuilder.append(paramXmlPullParser.getAttributeValue(paramInt));
    paramStringBuilder.append("\"");
  }
  
  private boolean isEndOfDoc()
    throws XmlPullParserException
  {
    return this.mParser.getEventType() == 1;
  }
  
  private static boolean isSupportedTag(String paramString)
  {
    return (paramString.equals("tt")) || (paramString.equals("head")) || (paramString.equals("body")) || (paramString.equals("div")) || (paramString.equals("p")) || (paramString.equals("span")) || (paramString.equals("br")) || (paramString.equals("style")) || (paramString.equals("styling")) || (paramString.equals("layout")) || (paramString.equals("region")) || (paramString.equals("metadata")) || (paramString.equals("smpte:image")) || (paramString.equals("smpte:data")) || (paramString.equals("smpte:information"));
  }
  
  private void loadParser(String paramString)
    throws XmlPullParserException
  {
    XmlPullParserFactory localXmlPullParserFactory = XmlPullParserFactory.newInstance();
    localXmlPullParserFactory.setNamespaceAware(false);
    this.mParser = localXmlPullParserFactory.newPullParser();
    paramString = new StringReader(paramString);
    this.mParser.setInput(paramString);
  }
  
  private TtmlNode parseNode(TtmlNode paramTtmlNode)
    throws XmlPullParserException, IOException
  {
    if (this.mParser.getEventType() != 2) {
      return null;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    long l2 = 0L;
    long l1 = Long.MAX_VALUE;
    long l4 = 0L;
    int i = 0;
    if (i < this.mParser.getAttributeCount())
    {
      String str2 = this.mParser.getAttributeName(i);
      String str1 = this.mParser.getAttributeValue(i);
      str2 = str2.replaceFirst("^.*:", "");
      if (str2.equals("begin")) {
        l2 = TtmlUtils.parseTimeExpression(str1, 30, 1, 1);
      }
      for (;;)
      {
        i += 1;
        break;
        if (str2.equals("end")) {
          l1 = TtmlUtils.parseTimeExpression(str1, 30, 1, 1);
        } else if (str2.equals("dur")) {
          l4 = TtmlUtils.parseTimeExpression(str1, 30, 1, 1);
        } else {
          extractAttribute(this.mParser, i, localStringBuilder);
        }
      }
    }
    long l5 = l2;
    long l3 = l1;
    if (paramTtmlNode != null)
    {
      l2 += paramTtmlNode.mStartTimeMs;
      l5 = l2;
      l3 = l1;
      if (l1 != Long.MAX_VALUE)
      {
        l3 = l1 + paramTtmlNode.mStartTimeMs;
        l5 = l2;
      }
    }
    l1 = l3;
    if (l4 > 0L)
    {
      if (l3 != Long.MAX_VALUE) {
        Log.e("TtmlParser", "'dur' and 'end' attributes are defined at the same time.'end' value is ignored.");
      }
      l1 = l5 + l4;
    }
    l2 = l1;
    if (paramTtmlNode != null)
    {
      l2 = l1;
      if (l1 == Long.MAX_VALUE)
      {
        l2 = l1;
        if (paramTtmlNode.mEndTimeMs != Long.MAX_VALUE)
        {
          l2 = l1;
          if (l1 > paramTtmlNode.mEndTimeMs) {
            l2 = paramTtmlNode.mEndTimeMs;
          }
        }
      }
    }
    return new TtmlNode(this.mParser.getName(), localStringBuilder.toString(), null, l5, l2, paramTtmlNode, this.mCurrentRunId);
  }
  
  private void parseTtml()
    throws XmlPullParserException, IOException
  {
    LinkedList localLinkedList = new LinkedList();
    int m = 0;
    int k = 1;
    if (!isEndOfDoc())
    {
      int n = this.mParser.getEventType();
      TtmlNode localTtmlNode = (TtmlNode)localLinkedList.peekLast();
      int i;
      int j;
      if (k != 0) {
        if (n == 2) {
          if (!isSupportedTag(this.mParser.getName()))
          {
            Log.w("TtmlParser", "Unsupported tag " + this.mParser.getName() + " is ignored.");
            i = m + 1;
            j = 0;
          }
        }
      }
      for (;;)
      {
        this.mParser.next();
        k = j;
        m = i;
        break;
        Object localObject = parseNode(localTtmlNode);
        localLinkedList.addLast(localObject);
        j = k;
        i = m;
        if (localTtmlNode != null)
        {
          localTtmlNode.mChildren.add(localObject);
          j = k;
          i = m;
          continue;
          if (n == 4)
          {
            localObject = TtmlUtils.applyDefaultSpacePolicy(this.mParser.getText());
            j = k;
            i = m;
            if (!TextUtils.isEmpty((CharSequence)localObject))
            {
              localTtmlNode.mChildren.add(new TtmlNode("#pcdata", "", (String)localObject, 0L, Long.MAX_VALUE, localTtmlNode, this.mCurrentRunId));
              j = k;
              i = m;
            }
          }
          else
          {
            j = k;
            i = m;
            if (n == 3)
            {
              if (this.mParser.getName().equals("p")) {
                this.mListener.onTtmlNodeParsed((TtmlNode)localLinkedList.getLast());
              }
              for (;;)
              {
                localLinkedList.removeLast();
                j = k;
                i = m;
                break;
                if (this.mParser.getName().equals("tt")) {
                  this.mListener.onRootNodeParsed((TtmlNode)localLinkedList.getLast());
                }
              }
              if (n == 2)
              {
                i = m + 1;
                j = k;
              }
              else
              {
                j = k;
                i = m;
                if (n == 3)
                {
                  m -= 1;
                  j = k;
                  i = m;
                  if (m == 0)
                  {
                    j = 1;
                    i = m;
                  }
                }
              }
            }
          }
        }
      }
    }
  }
  
  public void parse(String paramString, long paramLong)
    throws XmlPullParserException, IOException
  {
    this.mParser = null;
    this.mCurrentRunId = paramLong;
    loadParser(paramString);
    parseTtml();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/TtmlParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */