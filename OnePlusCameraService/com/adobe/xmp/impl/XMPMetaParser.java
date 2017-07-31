package com.adobe.xmp.impl;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.options.ParseOptions;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMPMetaParser
{
  private static final Object XMP_RDF = new Object();
  private static DocumentBuilderFactory factory = createDocumentBuilderFactory();
  
  private static DocumentBuilderFactory createDocumentBuilderFactory()
  {
    DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
    localDocumentBuilderFactory.setNamespaceAware(true);
    localDocumentBuilderFactory.setIgnoringComments(true);
    try
    {
      localDocumentBuilderFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
      return localDocumentBuilderFactory;
    }
    catch (Exception localException) {}
    return localDocumentBuilderFactory;
  }
  
  private static Object[] findRootNode(Node paramNode, boolean paramBoolean, Object[] paramArrayOfObject)
  {
    paramNode = paramNode.getChildNodes();
    int i = 0;
    if (i < paramNode.getLength())
    {
      Object localObject = paramNode.item(i);
      if (7 != ((Node)localObject).getNodeType()) {
        label40:
        if (3 != ((Node)localObject).getNodeType()) {
          break label93;
        }
      }
      label93:
      while (7 == ((Node)localObject).getNodeType()) {
        for (;;)
        {
          i += 1;
          break;
          if (((ProcessingInstruction)localObject).getTarget() != "xpacket") {
            break label40;
          }
          if (paramArrayOfObject != null) {
            paramArrayOfObject[2] = ((ProcessingInstruction)localObject).getData();
          }
        }
      }
      String str1 = ((Node)localObject).getNamespaceURI();
      String str2 = ((Node)localObject).getLocalName();
      if ("xmpmeta".equals(str2))
      {
        label133:
        if ("adobe:ns:meta/".equals(str1)) {
          break label177;
        }
        label143:
        if (!paramBoolean) {
          break label185;
        }
      }
      label177:
      label185:
      while ((!"RDF".equals(str2)) || (!"http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(str1)))
      {
        localObject = findRootNode((Node)localObject, paramBoolean, paramArrayOfObject);
        if (localObject == null) {
          break;
        }
        return (Object[])localObject;
        if ("xapmeta".equals(str2)) {
          break label133;
        }
        break label143;
        return findRootNode((Node)localObject, false, paramArrayOfObject);
      }
      if (paramArrayOfObject == null) {
        return paramArrayOfObject;
      }
      paramArrayOfObject[0] = localObject;
      paramArrayOfObject[1] = XMP_RDF;
      return paramArrayOfObject;
    }
    return null;
  }
  
  public static XMPMeta parse(Object paramObject, ParseOptions paramParseOptions)
    throws XMPException
  {
    ParameterAsserts.assertNotNull(paramObject);
    ParseOptions localParseOptions = paramParseOptions;
    if (paramParseOptions == null) {
      localParseOptions = new ParseOptions();
    }
    paramObject = findRootNode(parseXml(paramObject, localParseOptions), localParseOptions.getRequireXMPMeta(), new Object[3]);
    if (paramObject == null) {}
    while (paramObject[1] != XMP_RDF) {
      return new XMPMetaImpl();
    }
    paramParseOptions = ParseRDF.parse((Node)paramObject[0]);
    paramParseOptions.setPacketHeader((String)paramObject[2]);
    if (localParseOptions.getOmitNormalization()) {
      return paramParseOptions;
    }
    return XMPNormalizer.process(paramParseOptions, localParseOptions);
  }
  
  private static Document parseInputSource(InputSource paramInputSource)
    throws XMPException
  {
    try
    {
      DocumentBuilder localDocumentBuilder = factory.newDocumentBuilder();
      localDocumentBuilder.setErrorHandler(null);
      paramInputSource = localDocumentBuilder.parse(paramInputSource);
      return paramInputSource;
    }
    catch (SAXException paramInputSource)
    {
      throw new XMPException("XML parsing failure", 201, paramInputSource);
    }
    catch (ParserConfigurationException paramInputSource)
    {
      throw new XMPException("XML Parser not correctly configured", 0, paramInputSource);
    }
    catch (IOException paramInputSource)
    {
      throw new XMPException("Error reading the XML-file", 204, paramInputSource);
    }
  }
  
  private static Document parseXml(Object paramObject, ParseOptions paramParseOptions)
    throws XMPException
  {
    if (!(paramObject instanceof InputStream))
    {
      if (!(paramObject instanceof byte[])) {
        return parseXmlFromString((String)paramObject, paramParseOptions);
      }
    }
    else {
      return parseXmlFromInputStream((InputStream)paramObject, paramParseOptions);
    }
    return parseXmlFromBytebuffer(new ByteBuffer((byte[])paramObject), paramParseOptions);
  }
  
  private static Document parseXmlFromBytebuffer(ByteBuffer paramByteBuffer, ParseOptions paramParseOptions)
    throws XMPException
  {
    Object localObject = new InputSource(paramByteBuffer.getByteStream());
    for (;;)
    {
      try
      {
        localObject = parseInputSource((InputSource)localObject);
        return (Document)localObject;
      }
      catch (XMPException localXMPException)
      {
        if (localXMPException.getErrorCode() == 201)
        {
          if (!paramParseOptions.getAcceptLatin1())
          {
            if (paramParseOptions.getFixControlChars()) {
              break;
            }
            return parseInputSource(new InputSource(paramByteBuffer.getByteStream()));
          }
        }
        else
        {
          if (localXMPException.getErrorCode() == 204) {
            continue;
          }
          throw localXMPException;
        }
      }
      paramByteBuffer = Latin1Converter.convert(paramByteBuffer);
    }
    try
    {
      paramParseOptions = paramByteBuffer.getEncoding();
      paramByteBuffer = parseInputSource(new InputSource(new FixASCIIControlsReader(new InputStreamReader(paramByteBuffer.getByteStream(), paramParseOptions))));
      return paramByteBuffer;
    }
    catch (UnsupportedEncodingException paramByteBuffer)
    {
      throw new XMPException("Unsupported Encoding", 9, localXMPException);
    }
  }
  
  private static Document parseXmlFromInputStream(InputStream paramInputStream, ParseOptions paramParseOptions)
    throws XMPException
  {
    if (paramParseOptions.getAcceptLatin1()) {}
    do
    {
      try
      {
        paramInputStream = parseXmlFromBytebuffer(new ByteBuffer(paramInputStream), paramParseOptions);
        return paramInputStream;
      }
      catch (IOException paramInputStream)
      {
        throw new XMPException("Error reading the XML-file", 204, paramInputStream);
      }
    } while (paramParseOptions.getFixControlChars());
    return parseInputSource(new InputSource(paramInputStream));
  }
  
  private static Document parseXmlFromString(String paramString, ParseOptions paramParseOptions)
    throws XMPException
  {
    Object localObject = new InputSource(new StringReader(paramString));
    try
    {
      localObject = parseInputSource((InputSource)localObject);
      return (Document)localObject;
    }
    catch (XMPException localXMPException)
    {
      if (localXMPException.getErrorCode() != 201) {}
      while (!paramParseOptions.getFixControlChars()) {
        throw localXMPException;
      }
    }
    return parseInputSource(new InputSource(new FixASCIIControlsReader(new StringReader(paramString))));
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/impl/XMPMetaParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */