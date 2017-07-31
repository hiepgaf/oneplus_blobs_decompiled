package com.adobe.xmp.impl;

import com.adobe.xmp.XMPConst;
import com.adobe.xmp.XMPDateTime;
import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPIterator;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.XMPPathFactory;
import com.adobe.xmp.XMPUtils;
import com.adobe.xmp.impl.xpath.XMPPathParser;
import com.adobe.xmp.options.IteratorOptions;
import com.adobe.xmp.options.ParseOptions;
import com.adobe.xmp.options.PropertyOptions;
import com.adobe.xmp.properties.XMPProperty;
import java.util.Calendar;
import java.util.Iterator;

public class XMPMetaImpl
  implements XMPMeta, XMPConst
{
  private static final int VALUE_BASE64 = 7;
  private static final int VALUE_BOOLEAN = 1;
  private static final int VALUE_CALENDAR = 6;
  private static final int VALUE_DATE = 5;
  private static final int VALUE_DOUBLE = 4;
  private static final int VALUE_INTEGER = 2;
  private static final int VALUE_LONG = 3;
  private static final int VALUE_STRING = 0;
  private String packetHeader = null;
  private XMPNode tree;
  
  static
  {
    boolean bool = false;
    if (XMPMetaImpl.class.desiredAssertionStatus()) {}
    for (;;)
    {
      $assertionsDisabled = bool;
      return;
      bool = true;
    }
  }
  
  public XMPMetaImpl()
  {
    this.tree = new XMPNode(null, null, null);
  }
  
  public XMPMetaImpl(XMPNode paramXMPNode)
  {
    this.tree = paramXMPNode;
  }
  
  private void doSetArrayItem(XMPNode paramXMPNode, int paramInt, String paramString, PropertyOptions paramPropertyOptions, boolean paramBoolean)
    throws XMPException
  {
    XMPNode localXMPNode = new XMPNode("[]", null);
    paramPropertyOptions = XMPNodeUtils.verifySetOptions(paramPropertyOptions, paramString);
    int i;
    if (!paramBoolean)
    {
      i = paramXMPNode.getChildrenLength();
      if (paramInt == -1) {
        break label64;
      }
      label36:
      if (1 <= paramInt) {
        break label70;
      }
    }
    label64:
    label70:
    while (paramInt > i)
    {
      throw new XMPException("Array index out of bounds", 104);
      i = paramXMPNode.getChildrenLength() + 1;
      break;
      paramInt = i;
      break label36;
    }
    if (paramBoolean) {}
    for (;;)
    {
      paramXMPNode.addChild(paramInt, localXMPNode);
      setNode(localXMPNode, paramString, paramPropertyOptions, false);
      return;
      paramXMPNode.removeChild(paramInt);
    }
  }
  
  private Object evaluateNodeValue(int paramInt, XMPNode paramXMPNode)
    throws XMPException
  {
    String str = paramXMPNode.getValue();
    switch (paramInt)
    {
    case 0: 
    default: 
      if (str == null) {
        break;
      }
    }
    while (paramXMPNode.getOptions().isCompositeProperty())
    {
      return str;
      return new Boolean(XMPUtils.convertToBoolean(str));
      return new Integer(XMPUtils.convertToInteger(str));
      return new Long(XMPUtils.convertToLong(str));
      return new Double(XMPUtils.convertToDouble(str));
      return XMPUtils.convertToDate(str);
      return XMPUtils.convertToDate(str).getCalendar();
      return XMPUtils.decodeBase64(str);
    }
    return "";
  }
  
  public void appendArrayItem(String paramString1, String paramString2, PropertyOptions paramPropertyOptions1, String paramString3, PropertyOptions paramPropertyOptions2)
    throws XMPException
  {
    ParameterAsserts.assertSchemaNS(paramString1);
    ParameterAsserts.assertArrayName(paramString2);
    if (paramPropertyOptions1 != null) {}
    while (paramPropertyOptions1.isOnlyArrayOptions())
    {
      paramPropertyOptions1 = XMPNodeUtils.verifySetOptions(paramPropertyOptions1, null);
      paramString2 = XMPPathParser.expandXPath(paramString1, paramString2);
      paramString1 = XMPNodeUtils.findNode(this.tree, paramString2, false, null);
      if (paramString1 != null) {
        break label88;
      }
      if (paramPropertyOptions1.isArray()) {
        break label122;
      }
      throw new XMPException("Explicit arrayOptions required to create new array", 103);
      paramPropertyOptions1 = new PropertyOptions();
    }
    throw new XMPException("Only array form flags allowed for arrayOptions", 103);
    label88:
    if (paramString1.getOptions().isArray()) {}
    label122:
    do
    {
      doSetArrayItem(paramString1, -1, paramString3, paramPropertyOptions2, true);
      return;
      throw new XMPException("The named property is not an array", 102);
      paramString2 = XMPNodeUtils.findNode(this.tree, paramString2, true, paramPropertyOptions1);
      paramString1 = paramString2;
    } while (paramString2 != null);
    throw new XMPException("Failure creating array node", 102);
  }
  
  public void appendArrayItem(String paramString1, String paramString2, String paramString3)
    throws XMPException
  {
    appendArrayItem(paramString1, paramString2, null, paramString3, null);
  }
  
  public Object clone()
  {
    return new XMPMetaImpl((XMPNode)this.tree.clone());
  }
  
  public int countArrayItems(String paramString1, String paramString2)
    throws XMPException
  {
    ParameterAsserts.assertSchemaNS(paramString1);
    ParameterAsserts.assertArrayName(paramString2);
    paramString1 = XMPPathParser.expandXPath(paramString1, paramString2);
    paramString1 = XMPNodeUtils.findNode(this.tree, paramString1, false, null);
    if (paramString1 != null)
    {
      if (!paramString1.getOptions().isArray()) {
        throw new XMPException("The named property is not an array", 102);
      }
    }
    else {
      return 0;
    }
    return paramString1.getChildrenLength();
  }
  
  public void deleteArrayItem(String paramString1, String paramString2, int paramInt)
  {
    try
    {
      ParameterAsserts.assertSchemaNS(paramString1);
      ParameterAsserts.assertArrayName(paramString2);
      deleteProperty(paramString1, XMPPathFactory.composeArrayItemPath(paramString2, paramInt));
      return;
    }
    catch (XMPException paramString1) {}
  }
  
  public void deleteProperty(String paramString1, String paramString2)
  {
    try
    {
      ParameterAsserts.assertSchemaNS(paramString1);
      ParameterAsserts.assertPropName(paramString2);
      paramString1 = XMPPathParser.expandXPath(paramString1, paramString2);
      paramString1 = XMPNodeUtils.findNode(this.tree, paramString1, false, null);
      if (paramString1 == null) {
        return;
      }
      XMPNodeUtils.deleteNode(paramString1);
      return;
    }
    catch (XMPException paramString1) {}
  }
  
  public void deleteQualifier(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    try
    {
      ParameterAsserts.assertSchemaNS(paramString1);
      ParameterAsserts.assertPropName(paramString2);
      deleteProperty(paramString1, paramString2 + XMPPathFactory.composeQualifierPath(paramString3, paramString4));
      return;
    }
    catch (XMPException paramString1) {}
  }
  
  public void deleteStructField(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    try
    {
      ParameterAsserts.assertSchemaNS(paramString1);
      ParameterAsserts.assertStructName(paramString2);
      deleteProperty(paramString1, paramString2 + XMPPathFactory.composeStructFieldPath(paramString3, paramString4));
      return;
    }
    catch (XMPException paramString1) {}
  }
  
  public boolean doesArrayItemExist(String paramString1, String paramString2, int paramInt)
  {
    try
    {
      ParameterAsserts.assertSchemaNS(paramString1);
      ParameterAsserts.assertArrayName(paramString2);
      boolean bool = doesPropertyExist(paramString1, XMPPathFactory.composeArrayItemPath(paramString2, paramInt));
      return bool;
    }
    catch (XMPException paramString1) {}
    return false;
  }
  
  public boolean doesPropertyExist(String paramString1, String paramString2)
  {
    try
    {
      ParameterAsserts.assertSchemaNS(paramString1);
      ParameterAsserts.assertPropName(paramString2);
      paramString1 = XMPPathParser.expandXPath(paramString1, paramString2);
      paramString1 = XMPNodeUtils.findNode(this.tree, paramString1, false, null);
      return paramString1 != null;
    }
    catch (XMPException paramString1) {}
    return false;
  }
  
  public boolean doesQualifierExist(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    try
    {
      ParameterAsserts.assertSchemaNS(paramString1);
      ParameterAsserts.assertPropName(paramString2);
      paramString3 = XMPPathFactory.composeQualifierPath(paramString3, paramString4);
      boolean bool = doesPropertyExist(paramString1, paramString2 + paramString3);
      return bool;
    }
    catch (XMPException paramString1) {}
    return false;
  }
  
  public boolean doesStructFieldExist(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    try
    {
      ParameterAsserts.assertSchemaNS(paramString1);
      ParameterAsserts.assertStructName(paramString2);
      paramString3 = XMPPathFactory.composeStructFieldPath(paramString3, paramString4);
      boolean bool = doesPropertyExist(paramString1, paramString2 + paramString3);
      return bool;
    }
    catch (XMPException paramString1) {}
    return false;
  }
  
  public String dumpObject()
  {
    return getRoot().dumpNode(true);
  }
  
  public XMPProperty getArrayItem(String paramString1, String paramString2, int paramInt)
    throws XMPException
  {
    ParameterAsserts.assertSchemaNS(paramString1);
    ParameterAsserts.assertArrayName(paramString2);
    return getProperty(paramString1, XMPPathFactory.composeArrayItemPath(paramString2, paramInt));
  }
  
  public XMPProperty getLocalizedText(final String paramString1, String paramString2, String paramString3, String paramString4)
    throws XMPException
  {
    ParameterAsserts.assertSchemaNS(paramString1);
    ParameterAsserts.assertArrayName(paramString2);
    ParameterAsserts.assertSpecificLang(paramString4);
    if (paramString3 == null) {}
    for (paramString3 = null;; paramString3 = Utils.normalizeLangValue(paramString3))
    {
      paramString4 = Utils.normalizeLangValue(paramString4);
      paramString1 = XMPPathParser.expandXPath(paramString1, paramString2);
      paramString1 = XMPNodeUtils.findNode(this.tree, paramString1, false, null);
      if (paramString1 == null) {
        break;
      }
      paramString1 = XMPNodeUtils.chooseLocalizedText(paramString1, paramString3, paramString4);
      int i = ((Integer)paramString1[0]).intValue();
      paramString1 = (XMPNode)paramString1[1];
      if (i != 0) {
        break label90;
      }
      return null;
    }
    return null;
    label90:
    new XMPProperty()
    {
      public String getLanguage()
      {
        return paramString1.getQualifier(1).getValue();
      }
      
      public PropertyOptions getOptions()
      {
        return paramString1.getOptions();
      }
      
      public Object getValue()
      {
        return paramString1.getValue();
      }
      
      public String toString()
      {
        return paramString1.getValue().toString();
      }
    };
  }
  
  public String getObjectName()
  {
    if (this.tree.getName() == null) {
      return "";
    }
    return this.tree.getName();
  }
  
  public String getPacketHeader()
  {
    return this.packetHeader;
  }
  
  public XMPProperty getProperty(String paramString1, String paramString2)
    throws XMPException
  {
    return getProperty(paramString1, paramString2, 0);
  }
  
  protected XMPProperty getProperty(final String paramString1, String paramString2, int paramInt)
    throws XMPException
  {
    ParameterAsserts.assertSchemaNS(paramString1);
    ParameterAsserts.assertPropName(paramString2);
    paramString1 = XMPPathParser.expandXPath(paramString1, paramString2);
    paramString1 = XMPNodeUtils.findNode(this.tree, paramString1, false, null);
    if (paramString1 == null) {
      return null;
    }
    if (paramInt == 0) {}
    while (!paramString1.getOptions().isCompositeProperty()) {
      new XMPProperty()
      {
        public String getLanguage()
        {
          return null;
        }
        
        public PropertyOptions getOptions()
        {
          return paramString1.getOptions();
        }
        
        public Object getValue()
        {
          return this.val$value;
        }
        
        public String toString()
        {
          return this.val$value.toString();
        }
      };
    }
    throw new XMPException("Property must be simple when a value type is requested", 102);
  }
  
  public byte[] getPropertyBase64(String paramString1, String paramString2)
    throws XMPException
  {
    return (byte[])getPropertyObject(paramString1, paramString2, 7);
  }
  
  public Boolean getPropertyBoolean(String paramString1, String paramString2)
    throws XMPException
  {
    return (Boolean)getPropertyObject(paramString1, paramString2, 1);
  }
  
  public Calendar getPropertyCalendar(String paramString1, String paramString2)
    throws XMPException
  {
    return (Calendar)getPropertyObject(paramString1, paramString2, 6);
  }
  
  public XMPDateTime getPropertyDate(String paramString1, String paramString2)
    throws XMPException
  {
    return (XMPDateTime)getPropertyObject(paramString1, paramString2, 5);
  }
  
  public Double getPropertyDouble(String paramString1, String paramString2)
    throws XMPException
  {
    return (Double)getPropertyObject(paramString1, paramString2, 4);
  }
  
  public Integer getPropertyInteger(String paramString1, String paramString2)
    throws XMPException
  {
    return (Integer)getPropertyObject(paramString1, paramString2, 2);
  }
  
  public Long getPropertyLong(String paramString1, String paramString2)
    throws XMPException
  {
    return (Long)getPropertyObject(paramString1, paramString2, 3);
  }
  
  protected Object getPropertyObject(String paramString1, String paramString2, int paramInt)
    throws XMPException
  {
    ParameterAsserts.assertSchemaNS(paramString1);
    ParameterAsserts.assertPropName(paramString2);
    paramString1 = XMPPathParser.expandXPath(paramString1, paramString2);
    paramString1 = XMPNodeUtils.findNode(this.tree, paramString1, false, null);
    if (paramString1 == null) {
      return null;
    }
    if (paramInt == 0) {}
    while (!paramString1.getOptions().isCompositeProperty()) {
      return evaluateNodeValue(paramInt, paramString1);
    }
    throw new XMPException("Property must be simple when a value type is requested", 102);
  }
  
  public String getPropertyString(String paramString1, String paramString2)
    throws XMPException
  {
    return (String)getPropertyObject(paramString1, paramString2, 0);
  }
  
  public XMPProperty getQualifier(String paramString1, String paramString2, String paramString3, String paramString4)
    throws XMPException
  {
    ParameterAsserts.assertSchemaNS(paramString1);
    ParameterAsserts.assertPropName(paramString2);
    return getProperty(paramString1, paramString2 + XMPPathFactory.composeQualifierPath(paramString3, paramString4));
  }
  
  public XMPNode getRoot()
  {
    return this.tree;
  }
  
  public XMPProperty getStructField(String paramString1, String paramString2, String paramString3, String paramString4)
    throws XMPException
  {
    ParameterAsserts.assertSchemaNS(paramString1);
    ParameterAsserts.assertStructName(paramString2);
    return getProperty(paramString1, paramString2 + XMPPathFactory.composeStructFieldPath(paramString3, paramString4));
  }
  
  public void insertArrayItem(String paramString1, String paramString2, int paramInt, String paramString3)
    throws XMPException
  {
    insertArrayItem(paramString1, paramString2, paramInt, paramString3, null);
  }
  
  public void insertArrayItem(String paramString1, String paramString2, int paramInt, String paramString3, PropertyOptions paramPropertyOptions)
    throws XMPException
  {
    ParameterAsserts.assertSchemaNS(paramString1);
    ParameterAsserts.assertArrayName(paramString2);
    paramString1 = XMPPathParser.expandXPath(paramString1, paramString2);
    paramString1 = XMPNodeUtils.findNode(this.tree, paramString1, false, null);
    if (paramString1 == null) {
      throw new XMPException("Specified array does not exist", 102);
    }
    doSetArrayItem(paramString1, paramInt, paramString3, paramPropertyOptions, true);
  }
  
  public XMPIterator iterator()
    throws XMPException
  {
    return iterator(null, null, null);
  }
  
  public XMPIterator iterator(IteratorOptions paramIteratorOptions)
    throws XMPException
  {
    return iterator(null, null, paramIteratorOptions);
  }
  
  public XMPIterator iterator(String paramString1, String paramString2, IteratorOptions paramIteratorOptions)
    throws XMPException
  {
    return new XMPIteratorImpl(this, paramString1, paramString2, paramIteratorOptions);
  }
  
  public void normalize(ParseOptions paramParseOptions)
    throws XMPException
  {
    if (paramParseOptions != null) {}
    for (;;)
    {
      XMPNormalizer.process(this, paramParseOptions);
      return;
      paramParseOptions = new ParseOptions();
    }
  }
  
  public void setArrayItem(String paramString1, String paramString2, int paramInt, String paramString3)
    throws XMPException
  {
    setArrayItem(paramString1, paramString2, paramInt, paramString3, null);
  }
  
  public void setArrayItem(String paramString1, String paramString2, int paramInt, String paramString3, PropertyOptions paramPropertyOptions)
    throws XMPException
  {
    ParameterAsserts.assertSchemaNS(paramString1);
    ParameterAsserts.assertArrayName(paramString2);
    paramString1 = XMPPathParser.expandXPath(paramString1, paramString2);
    paramString1 = XMPNodeUtils.findNode(this.tree, paramString1, false, null);
    if (paramString1 == null) {
      throw new XMPException("Specified array does not exist", 102);
    }
    doSetArrayItem(paramString1, paramInt, paramString3, paramPropertyOptions, false);
  }
  
  public void setLocalizedText(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
    throws XMPException
  {
    setLocalizedText(paramString1, paramString2, paramString3, paramString4, paramString5, null);
  }
  
  public void setLocalizedText(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, PropertyOptions paramPropertyOptions)
    throws XMPException
  {
    ParameterAsserts.assertSchemaNS(paramString1);
    ParameterAsserts.assertArrayName(paramString2);
    ParameterAsserts.assertSpecificLang(paramString4);
    if (paramString3 == null)
    {
      paramString3 = null;
      paramPropertyOptions = Utils.normalizeLangValue(paramString4);
      paramString1 = XMPPathParser.expandXPath(paramString1, paramString2);
      paramString4 = XMPNodeUtils.findNode(this.tree, paramString1, true, new PropertyOptions(7680));
      if (paramString4 == null) {
        break label122;
      }
      if (!paramString4.getOptions().isArrayAltText()) {
        break label135;
      }
      label69:
      paramString2 = paramString4.iterateChildren();
    }
    label122:
    label135:
    int i;
    for (;;)
    {
      if (paramString2.hasNext())
      {
        paramString1 = (XMPNode)paramString2.next();
        if (!paramString1.hasQualifier()) {}
        while (!"xml:lang".equals(paramString1.getQualifier(1).getName()))
        {
          throw new XMPException("Language qualifier must be first", 102);
          paramString3 = Utils.normalizeLangValue(paramString3);
          break;
          throw new XMPException("Failed to find or create array node", 102);
          if (paramString4.hasChildren()) {}
          while (!paramString4.getOptions().isArrayAlternate()) {
            throw new XMPException("Specified property is no alt-text array", 102);
          }
          paramString4.getOptions().setArrayAltText(true);
          break label69;
        }
        if ("x-default".equals(paramString1.getQualifier(1).getValue())) {
          i = 1;
        }
      }
    }
    for (;;)
    {
      if (paramString1 == null) {}
      boolean bool;
      for (;;)
      {
        paramString2 = XMPNodeUtils.chooseLocalizedText(paramString4, paramString3, paramPropertyOptions);
        int j = ((Integer)paramString2[0]).intValue();
        paramString2 = (XMPNode)paramString2[1];
        bool = "x-default".equals(paramPropertyOptions);
        switch (j)
        {
        default: 
          throw new XMPException("Unexpected result from ChooseLocalizedText", 9);
          if (paramString4.getChildrenLength() > 1)
          {
            paramString4.removeChild(paramString1);
            paramString4.addChild(1, paramString1);
          }
          break;
        }
      }
      XMPNodeUtils.appendLangItem(paramString4, "x-default", paramString5);
      i = 1;
      if (bool) {
        if (i == 0) {
          break label666;
        }
      }
      label385:
      label391:
      label495:
      label522:
      label524:
      label666:
      while (paramString4.getChildrenLength() != 1)
      {
        return;
        XMPNodeUtils.appendLangItem(paramString4, paramPropertyOptions, paramString5);
        break;
        if (bool)
        {
          if (!$assertionsDisabled) {
            break label495;
          }
          paramString3 = paramString4.iterateChildren();
        }
        for (;;)
        {
          if (!paramString3.hasNext()) {
            break label524;
          }
          paramPropertyOptions = (XMPNode)paramString3.next();
          if (paramPropertyOptions != paramString1)
          {
            String str = paramPropertyOptions.getValue();
            if (paramString1 == null) {}
            for (paramString2 = null;; paramString2 = paramString1.getValue())
            {
              if (!str.equals(paramString2)) {
                break label522;
              }
              paramPropertyOptions.setValue(paramString5);
              break label391;
              if (i == 0) {}
              for (;;)
              {
                paramString2.setValue(paramString5);
                break;
                if ((paramString1 != paramString2) && (paramString1 != null) && (paramString1.getValue().equals(paramString2.getValue()))) {
                  paramString1.setValue(paramString5);
                }
              }
              if (i == 0) {}
              while (paramString1 != paramString2) {
                throw new AssertionError();
              }
              break label385;
            }
          }
        }
        if (paramString1 == null) {
          break;
        }
        paramString1.setValue(paramString5);
        break;
        if (i == 0) {}
        for (;;)
        {
          paramString2.setValue(paramString5);
          break;
          if ((paramString1 != paramString2) && (paramString1 != null) && (paramString1.getValue().equals(paramString2.getValue()))) {
            paramString1.setValue(paramString5);
          }
        }
        XMPNodeUtils.appendLangItem(paramString4, paramPropertyOptions, paramString5);
        if (!bool) {
          break;
        }
        i = 1;
        break;
        if (paramString1 == null) {}
        for (;;)
        {
          XMPNodeUtils.appendLangItem(paramString4, paramPropertyOptions, paramString5);
          break;
          if (paramString4.getChildrenLength() == 1) {
            paramString1.setValue(paramString5);
          }
        }
        XMPNodeUtils.appendLangItem(paramString4, paramPropertyOptions, paramString5);
        if (!bool) {
          break;
        }
        i = 1;
        break;
      }
      XMPNodeUtils.appendLangItem(paramString4, "x-default", paramString5);
      return;
      i = 0;
      paramString1 = null;
    }
  }
  
  void setNode(XMPNode paramXMPNode, Object paramObject, PropertyOptions paramPropertyOptions, boolean paramBoolean)
    throws XMPException
  {
    if (!paramBoolean)
    {
      paramXMPNode.getOptions().mergeWith(paramPropertyOptions);
      if (!paramXMPNode.getOptions().isCompositeProperty()) {
        break label39;
      }
      if (paramObject != null) {
        break label45;
      }
    }
    label39:
    label45:
    while (paramObject.toString().length() <= 0)
    {
      paramXMPNode.removeChildren();
      return;
      paramXMPNode.clear();
      break;
      XMPNodeUtils.setNodeValue(paramXMPNode, paramObject);
      return;
    }
    throw new XMPException("Composite nodes can't have values", 102);
  }
  
  public void setObjectName(String paramString)
  {
    this.tree.setName(paramString);
  }
  
  public void setPacketHeader(String paramString)
  {
    this.packetHeader = paramString;
  }
  
  public void setProperty(String paramString1, String paramString2, Object paramObject)
    throws XMPException
  {
    setProperty(paramString1, paramString2, paramObject, null);
  }
  
  public void setProperty(String paramString1, String paramString2, Object paramObject, PropertyOptions paramPropertyOptions)
    throws XMPException
  {
    ParameterAsserts.assertSchemaNS(paramString1);
    ParameterAsserts.assertPropName(paramString2);
    paramPropertyOptions = XMPNodeUtils.verifySetOptions(paramPropertyOptions, paramObject);
    paramString1 = XMPPathParser.expandXPath(paramString1, paramString2);
    paramString1 = XMPNodeUtils.findNode(this.tree, paramString1, true, paramPropertyOptions);
    if (paramString1 == null) {
      throw new XMPException("Specified property does not exist", 102);
    }
    setNode(paramString1, paramObject, paramPropertyOptions, false);
  }
  
  public void setPropertyBase64(String paramString1, String paramString2, byte[] paramArrayOfByte)
    throws XMPException
  {
    setProperty(paramString1, paramString2, paramArrayOfByte, null);
  }
  
  public void setPropertyBase64(String paramString1, String paramString2, byte[] paramArrayOfByte, PropertyOptions paramPropertyOptions)
    throws XMPException
  {
    setProperty(paramString1, paramString2, paramArrayOfByte, paramPropertyOptions);
  }
  
  public void setPropertyBoolean(String paramString1, String paramString2, boolean paramBoolean)
    throws XMPException
  {
    if (!paramBoolean) {}
    for (String str = "False";; str = "True")
    {
      setProperty(paramString1, paramString2, str, null);
      return;
    }
  }
  
  public void setPropertyBoolean(String paramString1, String paramString2, boolean paramBoolean, PropertyOptions paramPropertyOptions)
    throws XMPException
  {
    if (!paramBoolean) {}
    for (String str = "False";; str = "True")
    {
      setProperty(paramString1, paramString2, str, paramPropertyOptions);
      return;
    }
  }
  
  public void setPropertyCalendar(String paramString1, String paramString2, Calendar paramCalendar)
    throws XMPException
  {
    setProperty(paramString1, paramString2, paramCalendar, null);
  }
  
  public void setPropertyCalendar(String paramString1, String paramString2, Calendar paramCalendar, PropertyOptions paramPropertyOptions)
    throws XMPException
  {
    setProperty(paramString1, paramString2, paramCalendar, paramPropertyOptions);
  }
  
  public void setPropertyDate(String paramString1, String paramString2, XMPDateTime paramXMPDateTime)
    throws XMPException
  {
    setProperty(paramString1, paramString2, paramXMPDateTime, null);
  }
  
  public void setPropertyDate(String paramString1, String paramString2, XMPDateTime paramXMPDateTime, PropertyOptions paramPropertyOptions)
    throws XMPException
  {
    setProperty(paramString1, paramString2, paramXMPDateTime, paramPropertyOptions);
  }
  
  public void setPropertyDouble(String paramString1, String paramString2, double paramDouble)
    throws XMPException
  {
    setProperty(paramString1, paramString2, new Double(paramDouble), null);
  }
  
  public void setPropertyDouble(String paramString1, String paramString2, double paramDouble, PropertyOptions paramPropertyOptions)
    throws XMPException
  {
    setProperty(paramString1, paramString2, new Double(paramDouble), paramPropertyOptions);
  }
  
  public void setPropertyInteger(String paramString1, String paramString2, int paramInt)
    throws XMPException
  {
    setProperty(paramString1, paramString2, new Integer(paramInt), null);
  }
  
  public void setPropertyInteger(String paramString1, String paramString2, int paramInt, PropertyOptions paramPropertyOptions)
    throws XMPException
  {
    setProperty(paramString1, paramString2, new Integer(paramInt), paramPropertyOptions);
  }
  
  public void setPropertyLong(String paramString1, String paramString2, long paramLong)
    throws XMPException
  {
    setProperty(paramString1, paramString2, new Long(paramLong), null);
  }
  
  public void setPropertyLong(String paramString1, String paramString2, long paramLong, PropertyOptions paramPropertyOptions)
    throws XMPException
  {
    setProperty(paramString1, paramString2, new Long(paramLong), paramPropertyOptions);
  }
  
  public void setQualifier(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
    throws XMPException
  {
    setQualifier(paramString1, paramString2, paramString3, paramString4, paramString5, null);
  }
  
  public void setQualifier(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, PropertyOptions paramPropertyOptions)
    throws XMPException
  {
    ParameterAsserts.assertSchemaNS(paramString1);
    ParameterAsserts.assertPropName(paramString2);
    if (doesPropertyExist(paramString1, paramString2))
    {
      setProperty(paramString1, paramString2 + XMPPathFactory.composeQualifierPath(paramString3, paramString4), paramString5, paramPropertyOptions);
      return;
    }
    throw new XMPException("Specified property does not exist!", 102);
  }
  
  public void setStructField(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
    throws XMPException
  {
    setStructField(paramString1, paramString2, paramString3, paramString4, paramString5, null);
  }
  
  public void setStructField(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, PropertyOptions paramPropertyOptions)
    throws XMPException
  {
    ParameterAsserts.assertSchemaNS(paramString1);
    ParameterAsserts.assertStructName(paramString2);
    setProperty(paramString1, paramString2 + XMPPathFactory.composeStructFieldPath(paramString3, paramString4), paramString5, paramPropertyOptions);
  }
  
  public void sort()
  {
    this.tree.sort();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/impl/XMPMetaImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */