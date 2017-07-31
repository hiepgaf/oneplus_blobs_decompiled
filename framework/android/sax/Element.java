package android.sax;

import java.util.ArrayList;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

public class Element
{
  Children children;
  final int depth;
  EndElementListener endElementListener;
  EndTextElementListener endTextElementListener;
  final String localName;
  final Element parent;
  ArrayList<Element> requiredChilden;
  StartElementListener startElementListener;
  final String uri;
  boolean visited;
  
  Element(Element paramElement, String paramString1, String paramString2, int paramInt)
  {
    this.parent = paramElement;
    this.uri = paramString1;
    this.localName = paramString2;
    this.depth = paramInt;
  }
  
  static String toString(String paramString1, String paramString2)
  {
    StringBuilder localStringBuilder = new StringBuilder().append("'");
    if (paramString1.equals("")) {}
    for (;;)
    {
      return paramString2 + "'";
      paramString2 = paramString1 + ":" + paramString2;
    }
  }
  
  void checkRequiredChildren(Locator paramLocator)
    throws SAXParseException
  {
    ArrayList localArrayList = this.requiredChilden;
    if (localArrayList != null)
    {
      int i = localArrayList.size() - 1;
      while (i >= 0)
      {
        Element localElement = (Element)localArrayList.get(i);
        if (!localElement.visited) {
          throw new BadXmlException("Element named " + this + " is missing required" + " child element named " + localElement + ".", paramLocator);
        }
        i -= 1;
      }
    }
  }
  
  public Element getChild(String paramString)
  {
    return getChild("", paramString);
  }
  
  public Element getChild(String paramString1, String paramString2)
  {
    if (this.endTextElementListener != null) {
      throw new IllegalStateException("This element already has an end text element listener. It cannot have children.");
    }
    if (this.children == null) {
      this.children = new Children();
    }
    return this.children.getOrCreate(this, paramString1, paramString2);
  }
  
  public Element requireChild(String paramString)
  {
    return requireChild("", paramString);
  }
  
  public Element requireChild(String paramString1, String paramString2)
  {
    paramString1 = getChild(paramString1, paramString2);
    if (this.requiredChilden == null)
    {
      this.requiredChilden = new ArrayList();
      this.requiredChilden.add(paramString1);
    }
    while (this.requiredChilden.contains(paramString1)) {
      return paramString1;
    }
    this.requiredChilden.add(paramString1);
    return paramString1;
  }
  
  void resetRequiredChildren()
  {
    ArrayList localArrayList = this.requiredChilden;
    if (localArrayList != null)
    {
      int i = localArrayList.size() - 1;
      while (i >= 0)
      {
        ((Element)localArrayList.get(i)).visited = false;
        i -= 1;
      }
    }
  }
  
  public void setElementListener(ElementListener paramElementListener)
  {
    setStartElementListener(paramElementListener);
    setEndElementListener(paramElementListener);
  }
  
  public void setEndElementListener(EndElementListener paramEndElementListener)
  {
    if (this.endElementListener != null) {
      throw new IllegalStateException("End element listener has already been set.");
    }
    this.endElementListener = paramEndElementListener;
  }
  
  public void setEndTextElementListener(EndTextElementListener paramEndTextElementListener)
  {
    if (this.endTextElementListener != null) {
      throw new IllegalStateException("End text element listener has already been set.");
    }
    if (this.children != null) {
      throw new IllegalStateException("This element already has children. It cannot have an end text element listener.");
    }
    this.endTextElementListener = paramEndTextElementListener;
  }
  
  public void setStartElementListener(StartElementListener paramStartElementListener)
  {
    if (this.startElementListener != null) {
      throw new IllegalStateException("Start element listener has already been set.");
    }
    this.startElementListener = paramStartElementListener;
  }
  
  public void setTextElementListener(TextElementListener paramTextElementListener)
  {
    setStartElementListener(paramTextElementListener);
    setEndTextElementListener(paramTextElementListener);
  }
  
  public String toString()
  {
    return toString(this.uri, this.localName);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/sax/Element.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */