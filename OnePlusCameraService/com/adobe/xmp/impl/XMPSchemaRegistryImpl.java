package com.adobe.xmp.impl;

import com.adobe.xmp.XMPConst;
import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPSchemaRegistry;
import com.adobe.xmp.options.AliasOptions;
import com.adobe.xmp.options.PropertyOptions;
import com.adobe.xmp.properties.XMPAliasInfo;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class XMPSchemaRegistryImpl
  implements XMPSchemaRegistry, XMPConst
{
  private Map aliasMap = new HashMap();
  private Map namespaceToPrefixMap = new HashMap();
  private Pattern p = Pattern.compile("[/*?\\[\\]]");
  private Map prefixToNamespaceMap = new HashMap();
  
  public XMPSchemaRegistryImpl()
  {
    try
    {
      registerStandardNamespaces();
      registerStandardAliases();
      return;
    }
    catch (XMPException localXMPException)
    {
      throw new RuntimeException("The XMPSchemaRegistry cannot be initialized!");
    }
  }
  
  private void registerStandardAliases()
    throws XMPException
  {
    AliasOptions localAliasOptions1 = new AliasOptions().setArrayOrdered(true);
    AliasOptions localAliasOptions2 = new AliasOptions().setArrayAltText(true);
    registerAlias("http://ns.adobe.com/xap/1.0/", "Author", "http://purl.org/dc/elements/1.1/", "creator", localAliasOptions1);
    registerAlias("http://ns.adobe.com/xap/1.0/", "Authors", "http://purl.org/dc/elements/1.1/", "creator", null);
    registerAlias("http://ns.adobe.com/xap/1.0/", "Description", "http://purl.org/dc/elements/1.1/", "description", null);
    registerAlias("http://ns.adobe.com/xap/1.0/", "Format", "http://purl.org/dc/elements/1.1/", "format", null);
    registerAlias("http://ns.adobe.com/xap/1.0/", "Keywords", "http://purl.org/dc/elements/1.1/", "subject", null);
    registerAlias("http://ns.adobe.com/xap/1.0/", "Locale", "http://purl.org/dc/elements/1.1/", "language", null);
    registerAlias("http://ns.adobe.com/xap/1.0/", "Title", "http://purl.org/dc/elements/1.1/", "title", null);
    registerAlias("http://ns.adobe.com/xap/1.0/rights/", "Copyright", "http://purl.org/dc/elements/1.1/", "rights", null);
    registerAlias("http://ns.adobe.com/pdf/1.3/", "Author", "http://purl.org/dc/elements/1.1/", "creator", localAliasOptions1);
    registerAlias("http://ns.adobe.com/pdf/1.3/", "BaseURL", "http://ns.adobe.com/xap/1.0/", "BaseURL", null);
    registerAlias("http://ns.adobe.com/pdf/1.3/", "CreationDate", "http://ns.adobe.com/xap/1.0/", "CreateDate", null);
    registerAlias("http://ns.adobe.com/pdf/1.3/", "Creator", "http://ns.adobe.com/xap/1.0/", "CreatorTool", null);
    registerAlias("http://ns.adobe.com/pdf/1.3/", "ModDate", "http://ns.adobe.com/xap/1.0/", "ModifyDate", null);
    registerAlias("http://ns.adobe.com/pdf/1.3/", "Subject", "http://purl.org/dc/elements/1.1/", "description", localAliasOptions2);
    registerAlias("http://ns.adobe.com/pdf/1.3/", "Title", "http://purl.org/dc/elements/1.1/", "title", localAliasOptions2);
    registerAlias("http://ns.adobe.com/photoshop/1.0/", "Author", "http://purl.org/dc/elements/1.1/", "creator", localAliasOptions1);
    registerAlias("http://ns.adobe.com/photoshop/1.0/", "Caption", "http://purl.org/dc/elements/1.1/", "description", localAliasOptions2);
    registerAlias("http://ns.adobe.com/photoshop/1.0/", "Copyright", "http://purl.org/dc/elements/1.1/", "rights", localAliasOptions2);
    registerAlias("http://ns.adobe.com/photoshop/1.0/", "Keywords", "http://purl.org/dc/elements/1.1/", "subject", null);
    registerAlias("http://ns.adobe.com/photoshop/1.0/", "Marked", "http://ns.adobe.com/xap/1.0/rights/", "Marked", null);
    registerAlias("http://ns.adobe.com/photoshop/1.0/", "Title", "http://purl.org/dc/elements/1.1/", "title", localAliasOptions2);
    registerAlias("http://ns.adobe.com/photoshop/1.0/", "WebStatement", "http://ns.adobe.com/xap/1.0/rights/", "WebStatement", null);
    registerAlias("http://ns.adobe.com/tiff/1.0/", "Artist", "http://purl.org/dc/elements/1.1/", "creator", localAliasOptions1);
    registerAlias("http://ns.adobe.com/tiff/1.0/", "Copyright", "http://purl.org/dc/elements/1.1/", "rights", null);
    registerAlias("http://ns.adobe.com/tiff/1.0/", "DateTime", "http://ns.adobe.com/xap/1.0/", "ModifyDate", null);
    registerAlias("http://ns.adobe.com/tiff/1.0/", "ImageDescription", "http://purl.org/dc/elements/1.1/", "description", null);
    registerAlias("http://ns.adobe.com/tiff/1.0/", "Software", "http://ns.adobe.com/xap/1.0/", "CreatorTool", null);
    registerAlias("http://ns.adobe.com/png/1.0/", "Author", "http://purl.org/dc/elements/1.1/", "creator", localAliasOptions1);
    registerAlias("http://ns.adobe.com/png/1.0/", "Copyright", "http://purl.org/dc/elements/1.1/", "rights", localAliasOptions2);
    registerAlias("http://ns.adobe.com/png/1.0/", "CreationTime", "http://ns.adobe.com/xap/1.0/", "CreateDate", null);
    registerAlias("http://ns.adobe.com/png/1.0/", "Description", "http://purl.org/dc/elements/1.1/", "description", localAliasOptions2);
    registerAlias("http://ns.adobe.com/png/1.0/", "ModificationTime", "http://ns.adobe.com/xap/1.0/", "ModifyDate", null);
    registerAlias("http://ns.adobe.com/png/1.0/", "Software", "http://ns.adobe.com/xap/1.0/", "CreatorTool", null);
    registerAlias("http://ns.adobe.com/png/1.0/", "Title", "http://purl.org/dc/elements/1.1/", "title", localAliasOptions2);
  }
  
  private void registerStandardNamespaces()
    throws XMPException
  {
    registerNamespace("http://www.w3.org/XML/1998/namespace", "xml");
    registerNamespace("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf");
    registerNamespace("http://purl.org/dc/elements/1.1/", "dc");
    registerNamespace("http://iptc.org/std/Iptc4xmpCore/1.0/xmlns/", "Iptc4xmpCore");
    registerNamespace("adobe:ns:meta/", "x");
    registerNamespace("http://ns.adobe.com/iX/1.0/", "iX");
    registerNamespace("http://ns.adobe.com/xap/1.0/", "xmp");
    registerNamespace("http://ns.adobe.com/xap/1.0/rights/", "xmpRights");
    registerNamespace("http://ns.adobe.com/xap/1.0/mm/", "xmpMM");
    registerNamespace("http://ns.adobe.com/xap/1.0/bj/", "xmpBJ");
    registerNamespace("http://ns.adobe.com/xmp/note/", "xmpNote");
    registerNamespace("http://ns.adobe.com/pdf/1.3/", "pdf");
    registerNamespace("http://ns.adobe.com/pdfx/1.3/", "pdfx");
    registerNamespace("http://www.npes.org/pdfx/ns/id/", "pdfxid");
    registerNamespace("http://www.aiim.org/pdfa/ns/schema#", "pdfaSchema");
    registerNamespace("http://www.aiim.org/pdfa/ns/property#", "pdfaProperty");
    registerNamespace("http://www.aiim.org/pdfa/ns/type#", "pdfaType");
    registerNamespace("http://www.aiim.org/pdfa/ns/field#", "pdfaField");
    registerNamespace("http://www.aiim.org/pdfa/ns/id/", "pdfaid");
    registerNamespace("http://www.aiim.org/pdfa/ns/extension/", "pdfaExtension");
    registerNamespace("http://ns.adobe.com/photoshop/1.0/", "photoshop");
    registerNamespace("http://ns.adobe.com/album/1.0/", "album");
    registerNamespace("http://ns.adobe.com/exif/1.0/", "exif");
    registerNamespace("http://ns.adobe.com/exif/1.0/aux/", "aux");
    registerNamespace("http://ns.adobe.com/tiff/1.0/", "tiff");
    registerNamespace("http://ns.adobe.com/png/1.0/", "png");
    registerNamespace("http://ns.adobe.com/jpeg/1.0/", "jpeg");
    registerNamespace("http://ns.adobe.com/jp2k/1.0/", "jp2k");
    registerNamespace("http://ns.adobe.com/camera-raw-settings/1.0/", "crs");
    registerNamespace("http://ns.adobe.com/StockPhoto/1.0/", "bmsp");
    registerNamespace("http://ns.adobe.com/creatorAtom/1.0/", "creatorAtom");
    registerNamespace("http://ns.adobe.com/asf/1.0/", "asf");
    registerNamespace("http://ns.adobe.com/xmp/wav/1.0/", "wav");
    registerNamespace("http://ns.adobe.com/xmp/1.0/DynamicMedia/", "xmpDM");
    registerNamespace("http://ns.adobe.com/xmp/transient/1.0/", "xmpx");
    registerNamespace("http://ns.adobe.com/xap/1.0/t/", "xmpT");
    registerNamespace("http://ns.adobe.com/xap/1.0/t/pg/", "xmpTPg");
    registerNamespace("http://ns.adobe.com/xap/1.0/g/", "xmpG");
    registerNamespace("http://ns.adobe.com/xap/1.0/g/img/", "xmpGImg");
    registerNamespace("http://ns.adobe.com/xap/1.0/sType/Font#", "stFNT");
    registerNamespace("http://ns.adobe.com/xap/1.0/sType/Dimensions#", "stDim");
    registerNamespace("http://ns.adobe.com/xap/1.0/sType/ResourceEvent#", "stEvt");
    registerNamespace("http://ns.adobe.com/xap/1.0/sType/ResourceRef#", "stRef");
    registerNamespace("http://ns.adobe.com/xap/1.0/sType/Version#", "stVer");
    registerNamespace("http://ns.adobe.com/xap/1.0/sType/Job#", "stJob");
    registerNamespace("http://ns.adobe.com/xap/1.0/sType/ManifestItem#", "stMfs");
    registerNamespace("http://ns.adobe.com/xmp/Identifier/qual/1.0/", "xmpidq");
  }
  
  /* Error */
  public void deleteNamespace(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: aload_1
    //   4: invokevirtual 331	com/adobe/xmp/impl/XMPSchemaRegistryImpl:getNamespacePrefix	(Ljava/lang/String;)Ljava/lang/String;
    //   7: astore_2
    //   8: aload_2
    //   9: ifnonnull +6 -> 15
    //   12: aload_0
    //   13: monitorexit
    //   14: return
    //   15: aload_0
    //   16: getfield 27	com/adobe/xmp/impl/XMPSchemaRegistryImpl:namespaceToPrefixMap	Ljava/util/Map;
    //   19: aload_1
    //   20: invokeinterface 337 2 0
    //   25: pop
    //   26: aload_0
    //   27: getfield 29	com/adobe/xmp/impl/XMPSchemaRegistryImpl:prefixToNamespaceMap	Ljava/util/Map;
    //   30: aload_2
    //   31: invokeinterface 337 2 0
    //   36: pop
    //   37: goto -25 -> 12
    //   40: astore_1
    //   41: aload_0
    //   42: monitorexit
    //   43: aload_1
    //   44: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	45	0	this	XMPSchemaRegistryImpl
    //   0	45	1	paramString	String
    //   7	24	2	str	String
    // Exception table:
    //   from	to	target	type
    //   2	8	40	finally
    //   15	37	40	finally
  }
  
  public XMPAliasInfo findAlias(String paramString)
  {
    try
    {
      paramString = (XMPAliasInfo)this.aliasMap.get(paramString);
      return paramString;
    }
    finally
    {
      paramString = finally;
      throw paramString;
    }
  }
  
  /* Error */
  public XMPAliasInfo[] findAliases(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: aload_1
    //   4: invokevirtual 331	com/adobe/xmp/impl/XMPSchemaRegistryImpl:getNamespacePrefix	(Ljava/lang/String;)Ljava/lang/String;
    //   7: astore_1
    //   8: new 348	java/util/ArrayList
    //   11: dup
    //   12: invokespecial 349	java/util/ArrayList:<init>	()V
    //   15: astore_2
    //   16: aload_1
    //   17: ifnonnull +26 -> 43
    //   20: aload_2
    //   21: aload_2
    //   22: invokeinterface 355 1 0
    //   27: anewarray 344	com/adobe/xmp/properties/XMPAliasInfo
    //   30: invokeinterface 359 2 0
    //   35: checkcast 361	[Lcom/adobe/xmp/properties/XMPAliasInfo;
    //   38: astore_1
    //   39: aload_0
    //   40: monitorexit
    //   41: aload_1
    //   42: areturn
    //   43: aload_0
    //   44: getfield 31	com/adobe/xmp/impl/XMPSchemaRegistryImpl:aliasMap	Ljava/util/Map;
    //   47: invokeinterface 365 1 0
    //   52: invokeinterface 371 1 0
    //   57: astore_3
    //   58: aload_3
    //   59: invokeinterface 377 1 0
    //   64: ifeq -44 -> 20
    //   67: aload_3
    //   68: invokeinterface 381 1 0
    //   73: checkcast 383	java/lang/String
    //   76: astore 4
    //   78: aload 4
    //   80: aload_1
    //   81: invokevirtual 387	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   84: ifeq -26 -> 58
    //   87: aload_2
    //   88: aload_0
    //   89: aload 4
    //   91: invokevirtual 389	com/adobe/xmp/impl/XMPSchemaRegistryImpl:findAlias	(Ljava/lang/String;)Lcom/adobe/xmp/properties/XMPAliasInfo;
    //   94: invokeinterface 393 2 0
    //   99: pop
    //   100: goto -42 -> 58
    //   103: astore_1
    //   104: aload_0
    //   105: monitorexit
    //   106: aload_1
    //   107: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	108	0	this	XMPSchemaRegistryImpl
    //   0	108	1	paramString	String
    //   15	73	2	localArrayList	java.util.ArrayList
    //   57	11	3	localIterator	java.util.Iterator
    //   76	14	4	str	String
    // Exception table:
    //   from	to	target	type
    //   2	16	103	finally
    //   20	39	103	finally
    //   43	58	103	finally
    //   58	100	103	finally
  }
  
  public Map getAliases()
  {
    try
    {
      Map localMap = Collections.unmodifiableMap(new TreeMap(this.aliasMap));
      return localMap;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public String getNamespacePrefix(String paramString)
  {
    try
    {
      paramString = (String)this.namespaceToPrefixMap.get(paramString);
      return paramString;
    }
    finally
    {
      paramString = finally;
      throw paramString;
    }
  }
  
  public String getNamespaceURI(String paramString)
  {
    String str;
    if (paramString == null) {
      str = paramString;
    }
    for (;;)
    {
      try
      {
        paramString = (String)this.prefixToNamespaceMap.get(str);
        return paramString;
      }
      finally {}
      str = paramString;
      if (!paramString.endsWith(":")) {
        str = paramString + ":";
      }
    }
  }
  
  public Map getNamespaces()
  {
    try
    {
      Map localMap = Collections.unmodifiableMap(new TreeMap(this.namespaceToPrefixMap));
      return localMap;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public Map getPrefixes()
  {
    try
    {
      Map localMap = Collections.unmodifiableMap(new TreeMap(this.prefixToNamespaceMap));
      return localMap;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  void registerAlias(final String paramString1, String paramString2, final String paramString3, final String paramString4, final AliasOptions paramAliasOptions)
    throws XMPException
  {
    do
    {
      for (;;)
      {
        try
        {
          ParameterAsserts.assertSchemaNS(paramString1);
          ParameterAsserts.assertPropName(paramString2);
          ParameterAsserts.assertSchemaNS(paramString3);
          ParameterAsserts.assertPropName(paramString4);
          if (paramAliasOptions == null)
          {
            paramAliasOptions = new AliasOptions();
            if (!this.p.matcher(paramString2).find()) {
              break;
            }
            throw new XMPException("Alias and actual property names must be simple", 102);
          }
        }
        finally {}
        paramAliasOptions = new AliasOptions(XMPNodeUtils.verifySetOptions(paramAliasOptions.toPropertyOptions(), null).getOptions());
      }
    } while (this.p.matcher(paramString4).find());
    String str = getNamespacePrefix(paramString1);
    paramString1 = getNamespacePrefix(paramString3);
    if (str != null)
    {
      if (paramString1 != null)
      {
        paramString2 = str + paramString2;
        if (this.aliasMap.containsKey(paramString2)) {
          break label246;
        }
        if (this.aliasMap.containsKey(paramString1 + paramString4)) {
          break label258;
        }
        paramString1 = new XMPAliasInfo()
        {
          public AliasOptions getAliasForm()
          {
            return paramAliasOptions;
          }
          
          public String getNamespace()
          {
            return paramString3;
          }
          
          public String getPrefix()
          {
            return paramString1;
          }
          
          public String getPropName()
          {
            return paramString4;
          }
          
          public String toString()
          {
            return paramString1 + paramString4 + " NS(" + paramString3 + "), FORM (" + getAliasForm() + ")";
          }
        };
        this.aliasMap.put(paramString2, paramString1);
      }
    }
    else {
      throw new XMPException("Alias namespace is not registered", 101);
    }
    throw new XMPException("Actual namespace is not registered", 101);
    label246:
    throw new XMPException("Alias is already existing", 4);
    label258:
    throw new XMPException("Actual property is already an alias, use the base property", 4);
  }
  
  public String registerNamespace(String paramString1, String paramString2)
    throws XMPException
  {
    for (;;)
    {
      try
      {
        ParameterAsserts.assertSchemaNS(paramString1);
        ParameterAsserts.assertPrefix(paramString2);
        if (paramString2.charAt(paramString2.length() - 1) == ':')
        {
          if (Utils.isXMLNameNS(paramString2.substring(0, paramString2.length() - 1)))
          {
            str1 = (String)this.namespaceToPrefixMap.get(paramString1);
            String str2 = (String)this.prefixToNamespaceMap.get(paramString2);
            if (str1 != null) {
              break label152;
            }
            if (str2 != null) {
              break label157;
            }
            this.prefixToNamespaceMap.put(paramString2, paramString1);
            this.namespaceToPrefixMap.put(paramString1, paramString2);
            return paramString2;
          }
        }
        else
        {
          paramString2 = paramString2 + ':';
          continue;
        }
        throw new XMPException("The prefix is a bad XML name", 201);
      }
      finally {}
      label152:
      return str1;
      label157:
      int i = 1;
      String str1 = paramString2;
      while (this.prefixToNamespaceMap.containsKey(str1))
      {
        str1 = paramString2.substring(0, paramString2.length() - 1) + "_" + i + "_:";
        i += 1;
      }
      paramString2 = str1;
    }
  }
  
  public XMPAliasInfo resolveAlias(String paramString1, String paramString2)
  {
    try
    {
      paramString1 = getNamespacePrefix(paramString1);
      if (paramString1 != null)
      {
        paramString1 = (XMPAliasInfo)this.aliasMap.get(paramString1 + paramString2);
        return paramString1;
      }
      return null;
    }
    finally {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/impl/XMPSchemaRegistryImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */