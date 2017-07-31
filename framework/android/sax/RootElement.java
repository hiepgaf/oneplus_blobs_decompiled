package android.sax;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RootElement
  extends Element
{
  final Handler handler = new Handler();
  
  public RootElement(String paramString)
  {
    this("", paramString);
  }
  
  public RootElement(String paramString1, String paramString2)
  {
    super(null, paramString1, paramString2, 0);
  }
  
  public ContentHandler getContentHandler()
  {
    return this.handler;
  }
  
  class Handler
    extends DefaultHandler
  {
    StringBuilder bodyBuilder = null;
    Element current = null;
    int depth = -1;
    Locator locator;
    
    Handler() {}
    
    public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
      throws SAXException
    {
      if (this.bodyBuilder != null) {
        this.bodyBuilder.append(paramArrayOfChar, paramInt1, paramInt2);
      }
    }
    
    public void endElement(String paramString1, String paramString2, String paramString3)
      throws SAXException
    {
      paramString1 = this.current;
      if (this.depth == paramString1.depth)
      {
        paramString1.checkRequiredChildren(this.locator);
        if (paramString1.endElementListener != null) {
          paramString1.endElementListener.end();
        }
        if (this.bodyBuilder != null)
        {
          paramString2 = this.bodyBuilder.toString();
          this.bodyBuilder = null;
          paramString1.endTextElementListener.end(paramString2);
        }
        this.current = paramString1.parent;
      }
      this.depth -= 1;
    }
    
    public void setDocumentLocator(Locator paramLocator)
    {
      this.locator = paramLocator;
    }
    
    void start(Element paramElement, Attributes paramAttributes)
    {
      this.current = paramElement;
      if (paramElement.startElementListener != null) {
        paramElement.startElementListener.start(paramAttributes);
      }
      if (paramElement.endTextElementListener != null) {
        this.bodyBuilder = new StringBuilder();
      }
      paramElement.resetRequiredChildren();
      paramElement.visited = true;
    }
    
    public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
      throws SAXException
    {
      int i = this.depth + 1;
      this.depth = i;
      if (i == 0)
      {
        startRoot(paramString1, paramString2, paramAttributes);
        return;
      }
      if (this.bodyBuilder != null) {
        throw new BadXmlException("Encountered mixed content within text element named " + this.current + ".", this.locator);
      }
      if (i == this.current.depth + 1)
      {
        paramString3 = this.current.children;
        if (paramString3 != null)
        {
          paramString1 = paramString3.get(paramString1, paramString2);
          if (paramString1 != null) {
            start(paramString1, paramAttributes);
          }
        }
      }
    }
    
    void startRoot(String paramString1, String paramString2, Attributes paramAttributes)
      throws SAXException
    {
      RootElement localRootElement = RootElement.this;
      if ((localRootElement.uri.compareTo(paramString1) != 0) || (localRootElement.localName.compareTo(paramString2) != 0)) {
        throw new BadXmlException("Root element name does not match. Expected: " + localRootElement + ", Got: " + Element.toString(paramString1, paramString2), this.locator);
      }
      start(localRootElement, paramAttributes);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/sax/RootElement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */