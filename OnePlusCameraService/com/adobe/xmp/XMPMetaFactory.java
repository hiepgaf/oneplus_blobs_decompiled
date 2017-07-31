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
  
  /* Error */
  public static XMPVersionInfo getVersionInfo()
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 21	com/adobe/xmp/XMPMetaFactory:versionInfo	Lcom/adobe/xmp/XMPVersionInfo;
    //   6: ifnull +12 -> 18
    //   9: getstatic 21	com/adobe/xmp/XMPMetaFactory:versionInfo	Lcom/adobe/xmp/XMPVersionInfo;
    //   12: astore_0
    //   13: ldc 2
    //   15: monitorexit
    //   16: aload_0
    //   17: areturn
    //   18: new 6	com/adobe/xmp/XMPMetaFactory$1
    //   21: dup
    //   22: invokespecial 44	com/adobe/xmp/XMPMetaFactory$1:<init>	()V
    //   25: putstatic 21	com/adobe/xmp/XMPMetaFactory:versionInfo	Lcom/adobe/xmp/XMPVersionInfo;
    //   28: goto -19 -> 9
    //   31: astore_0
    //   32: getstatic 50	java/lang/System:out	Ljava/io/PrintStream;
    //   35: aload_0
    //   36: invokevirtual 56	java/io/PrintStream:println	(Ljava/lang/Object;)V
    //   39: goto -30 -> 9
    //   42: astore_0
    //   43: ldc 2
    //   45: monitorexit
    //   46: aload_0
    //   47: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   12	5	0	localXMPVersionInfo	XMPVersionInfo
    //   31	5	0	localThrowable	Throwable
    //   42	5	0	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   18	28	31	java/lang/Throwable
    //   3	9	42	finally
    //   9	13	42	finally
    //   18	28	42	finally
    //   32	39	42	finally
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