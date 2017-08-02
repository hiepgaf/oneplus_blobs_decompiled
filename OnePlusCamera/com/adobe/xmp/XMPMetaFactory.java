package com.adobe.xmp;

import com.adobe.xmp.impl.XMPMetaImpl;
import com.adobe.xmp.impl.XMPMetaParser;
import com.adobe.xmp.impl.XMPSchemaRegistryImpl;
import com.adobe.xmp.impl.XMPSerializerHelper;
import com.adobe.xmp.options.ParseOptions;
import com.adobe.xmp.options.SerializeOptions;
import java.io.InputStream;
import java.io.OutputStream;

public final class XMPMetaFactory
{
  private static XMPSchemaRegistry schema = new XMPSchemaRegistryImpl();
  private static XMPVersionInfo versionInfo = null;
  
  private static void assertImplementation(XMPMeta paramXMPMeta)
  {
    if ((paramXMPMeta instanceof XMPMetaImpl)) {
      return;
    }
    throw new UnsupportedOperationException("The serializing service works onlywith the XMPMeta implementation of this library");
  }
  
  public static XMPMeta create()
  {
    return new XMPMetaImpl();
  }
  
  public static XMPSchemaRegistry getSchemaRegistry()
  {
    return schema;
  }
  
    public static XMPVersionInfo getVersionInfo() {
        synchronized (XMPMetaFactory.class) {
            if (XMPMetaFactory.versionInfo == null) {
                try {
                    XMPMetaFactory.versionInfo = new XMPMetaFactory$1();
                }
                finally {
                    final Throwable t;
                    System.out.println(t);
                }
            }
            return XMPMetaFactory.versionInfo;
        }
    }
  
  public static XMPMeta parse(InputStream paramInputStream)
    throws XMPException
  {
    return parse(paramInputStream, null);
  }
  
  public static XMPMeta parse(InputStream paramInputStream, ParseOptions paramParseOptions)
    throws XMPException
  {
    return XMPMetaParser.parse(paramInputStream, paramParseOptions);
  }
  
  public static XMPMeta parseFromBuffer(byte[] paramArrayOfByte)
    throws XMPException
  {
    return parseFromBuffer(paramArrayOfByte, null);
  }
  
  public static XMPMeta parseFromBuffer(byte[] paramArrayOfByte, ParseOptions paramParseOptions)
    throws XMPException
  {
    return XMPMetaParser.parse(paramArrayOfByte, paramParseOptions);
  }
  
  public static XMPMeta parseFromString(String paramString)
    throws XMPException
  {
    return parseFromString(paramString, null);
  }
  
  public static XMPMeta parseFromString(String paramString, ParseOptions paramParseOptions)
    throws XMPException
  {
    return XMPMetaParser.parse(paramString, paramParseOptions);
  }
  
  public static void reset()
  {
    schema = new XMPSchemaRegistryImpl();
  }
  
  public static void serialize(XMPMeta paramXMPMeta, OutputStream paramOutputStream)
    throws XMPException
  {
    serialize(paramXMPMeta, paramOutputStream, null);
  }
  
  public static void serialize(XMPMeta paramXMPMeta, OutputStream paramOutputStream, SerializeOptions paramSerializeOptions)
    throws XMPException
  {
    assertImplementation(paramXMPMeta);
    XMPSerializerHelper.serialize((XMPMetaImpl)paramXMPMeta, paramOutputStream, paramSerializeOptions);
  }
  
  public static byte[] serializeToBuffer(XMPMeta paramXMPMeta, SerializeOptions paramSerializeOptions)
    throws XMPException
  {
    assertImplementation(paramXMPMeta);
    return XMPSerializerHelper.serializeToBuffer((XMPMetaImpl)paramXMPMeta, paramSerializeOptions);
  }
  
  public static String serializeToString(XMPMeta paramXMPMeta, SerializeOptions paramSerializeOptions)
    throws XMPException
  {
    assertImplementation(paramXMPMeta);
    return XMPSerializerHelper.serializeToString((XMPMetaImpl)paramXMPMeta, paramSerializeOptions);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/XMPMetaFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */