package com.adobe.xmp.impl;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.options.SerializeOptions;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class XMPSerializerHelper
{
  public static void serialize(XMPMetaImpl paramXMPMetaImpl, OutputStream paramOutputStream, SerializeOptions paramSerializeOptions)
    throws XMPException
  {
    SerializeOptions localSerializeOptions = paramSerializeOptions;
    if (paramSerializeOptions == null) {
      localSerializeOptions = new SerializeOptions();
    }
    if (!localSerializeOptions.getSort()) {}
    for (;;)
    {
      new XMPSerializerRDF().serialize(paramXMPMetaImpl, paramOutputStream, localSerializeOptions);
      return;
      paramXMPMetaImpl.sort();
    }
  }
  
  public static byte[] serializeToBuffer(XMPMetaImpl paramXMPMetaImpl, SerializeOptions paramSerializeOptions)
    throws XMPException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(2048);
    serialize(paramXMPMetaImpl, localByteArrayOutputStream, paramSerializeOptions);
    return localByteArrayOutputStream.toByteArray();
  }
  
  public static String serializeToString(XMPMetaImpl paramXMPMetaImpl, SerializeOptions paramSerializeOptions)
    throws XMPException
  {
    SerializeOptions localSerializeOptions = paramSerializeOptions;
    if (paramSerializeOptions == null) {
      localSerializeOptions = new SerializeOptions();
    }
    localSerializeOptions.setEncodeUTF16BE(true);
    paramSerializeOptions = new ByteArrayOutputStream(2048);
    serialize(paramXMPMetaImpl, paramSerializeOptions, localSerializeOptions);
    try
    {
      paramXMPMetaImpl = paramSerializeOptions.toString(localSerializeOptions.getEncoding());
      return paramXMPMetaImpl;
    }
    catch (UnsupportedEncodingException paramXMPMetaImpl) {}
    return paramSerializeOptions.toString();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/impl/XMPSerializerHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */