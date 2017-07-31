package android.content;

import android.net.Uri;
import android.util.Xml;
import android.util.Xml.Encoding;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;
import java.util.Vector;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class DefaultDataHandler
  implements ContentInsertHandler
{
  private static final String ARG = "arg";
  private static final String COL = "col";
  private static final String DEL = "del";
  private static final String POSTFIX = "postfix";
  private static final String ROW = "row";
  private static final String SELECT = "select";
  private static final String URI_STR = "uri";
  private ContentResolver mContentResolver;
  private Stack<Uri> mUris = new Stack();
  private ContentValues mValues;
  
  private Uri insertRow()
  {
    Uri localUri = this.mContentResolver.insert((Uri)this.mUris.lastElement(), this.mValues);
    this.mValues = null;
    return localUri;
  }
  
  private void parseRow(Attributes paramAttributes)
    throws SAXException
  {
    Object localObject = paramAttributes.getValue("uri");
    if (localObject != null)
    {
      Uri localUri = Uri.parse((String)localObject);
      localObject = localUri;
      if (localUri == null) {
        throw new SAXException("attribute " + paramAttributes.getValue("uri") + " parsing failure");
      }
    }
    else
    {
      if (this.mUris.size() <= 0) {
        break label125;
      }
      paramAttributes = paramAttributes.getValue("postfix");
      if (paramAttributes == null) {
        break label111;
      }
    }
    label111:
    for (localObject = Uri.withAppendedPath((Uri)this.mUris.lastElement(), paramAttributes);; localObject = (Uri)this.mUris.lastElement())
    {
      this.mUris.push(localObject);
      return;
    }
    label125:
    throw new SAXException("attribute parsing failure");
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {}
  
  public void endDocument()
    throws SAXException
  {}
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    if ("row".equals(paramString2))
    {
      if (this.mUris.empty()) {
        throw new SAXException("uri mismatch");
      }
      if (this.mValues != null) {
        insertRow();
      }
      this.mUris.pop();
    }
  }
  
  public void endPrefixMapping(String paramString)
    throws SAXException
  {}
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {}
  
  public void insert(ContentResolver paramContentResolver, InputStream paramInputStream)
    throws IOException, SAXException
  {
    this.mContentResolver = paramContentResolver;
    Xml.parse(paramInputStream, Xml.Encoding.UTF_8, this);
  }
  
  public void insert(ContentResolver paramContentResolver, String paramString)
    throws SAXException
  {
    this.mContentResolver = paramContentResolver;
    Xml.parse(paramString, this);
  }
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {}
  
  public void setDocumentLocator(Locator paramLocator) {}
  
  public void skippedEntity(String paramString)
    throws SAXException
  {}
  
  public void startDocument()
    throws SAXException
  {}
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    if ("row".equals(paramString2))
    {
      if (this.mValues != null)
      {
        if (this.mUris.empty()) {
          throw new SAXException("uri is empty");
        }
        paramString1 = insertRow();
        if (paramString1 == null) {
          throw new SAXException("insert to uri " + ((Uri)this.mUris.lastElement()).toString() + " failure");
        }
        this.mUris.pop();
        this.mUris.push(paramString1);
        parseRow(paramAttributes);
        return;
      }
      if (paramAttributes.getLength() == 0)
      {
        this.mUris.push((Uri)this.mUris.lastElement());
        return;
      }
      parseRow(paramAttributes);
      return;
    }
    int i;
    if ("col".equals(paramString2))
    {
      i = paramAttributes.getLength();
      if (i != 2) {
        throw new SAXException("illegal attributes number " + i);
      }
      paramString1 = paramAttributes.getValue(0);
      paramString2 = paramAttributes.getValue(1);
      if ((paramString1 != null) && (paramString1.length() > 0) && (paramString2 != null) && (paramString2.length() > 0))
      {
        if (this.mValues == null) {
          this.mValues = new ContentValues();
        }
        this.mValues.put(paramString1, paramString2);
        return;
      }
      throw new SAXException("illegal attributes value");
    }
    if ("del".equals(paramString2))
    {
      paramString1 = Uri.parse(paramAttributes.getValue("uri"));
      if (paramString1 == null) {
        throw new SAXException("attribute " + paramAttributes.getValue("uri") + " parsing failure");
      }
      int j = paramAttributes.getLength() - 2;
      if (j > 0)
      {
        paramString2 = new String[j];
        i = 0;
        while (i < j)
        {
          paramString2[i] = paramAttributes.getValue(i + 2);
          i += 1;
        }
        this.mContentResolver.delete(paramString1, paramAttributes.getValue(1), paramString2);
        return;
      }
      if (j == 0)
      {
        this.mContentResolver.delete(paramString1, paramAttributes.getValue(1), null);
        return;
      }
      this.mContentResolver.delete(paramString1, null, null);
      return;
    }
    throw new SAXException("unknown element: " + paramString2);
  }
  
  public void startPrefixMapping(String paramString1, String paramString2)
    throws SAXException
  {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/DefaultDataHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */