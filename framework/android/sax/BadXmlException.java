package android.sax;

import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

class BadXmlException
  extends SAXParseException
{
  public BadXmlException(String paramString, Locator paramLocator)
  {
    super(paramString, paramLocator);
  }
  
  public String getMessage()
  {
    return "Line " + getLineNumber() + ": " + super.getMessage();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/sax/BadXmlException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */