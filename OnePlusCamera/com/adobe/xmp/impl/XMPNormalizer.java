package com.adobe.xmp.impl;

import com.adobe.xmp.XMPDateTime;
import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.XMPSchemaRegistry;
import com.adobe.xmp.XMPUtils;
import com.adobe.xmp.impl.xpath.XMPPathParser;
import com.adobe.xmp.options.AliasOptions;
import com.adobe.xmp.options.ParseOptions;
import com.adobe.xmp.options.PropertyOptions;
import com.adobe.xmp.properties.XMPAliasInfo;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class XMPNormalizer
{
  private static Map dcArrayForms;
  
  static {}
  
  private static void compareAliasedSubtrees(XMPNode paramXMPNode1, XMPNode paramXMPNode2, boolean paramBoolean)
    throws XMPException
  {
    if (!paramXMPNode1.getValue().equals(paramXMPNode2.getValue())) {}
    while (paramXMPNode1.getChildrenLength() != paramXMPNode2.getChildrenLength()) {
      throw new XMPException("Mismatch between alias and base nodes", 203);
    }
    Iterator localIterator1;
    Iterator localIterator2;
    if (paramBoolean)
    {
      localIterator1 = paramXMPNode1.iterateChildren();
      localIterator2 = paramXMPNode2.iterateChildren();
      label53:
      if (localIterator1.hasNext()) {
        break label137;
      }
      label62:
      paramXMPNode1 = paramXMPNode1.iterateQualifier();
      paramXMPNode2 = paramXMPNode2.iterateQualifier();
    }
    for (;;)
    {
      if (!paramXMPNode1.hasNext()) {}
      label137:
      while (!paramXMPNode2.hasNext())
      {
        return;
        if (!paramXMPNode1.getName().equals(paramXMPNode2.getName())) {}
        while ((!paramXMPNode1.getOptions().equals(paramXMPNode2.getOptions())) || (paramXMPNode1.getQualifierLength() != paramXMPNode2.getQualifierLength())) {
          throw new XMPException("Mismatch between alias and base nodes", 203);
        }
        break;
        if (!localIterator2.hasNext()) {
          break label62;
        }
        compareAliasedSubtrees((XMPNode)localIterator1.next(), (XMPNode)localIterator2.next(), false);
        break label53;
      }
      compareAliasedSubtrees((XMPNode)paramXMPNode1.next(), (XMPNode)paramXMPNode2.next(), false);
    }
  }
  
  private static void deleteEmptySchemas(XMPNode paramXMPNode)
  {
    paramXMPNode = paramXMPNode.iterateChildren();
    while (paramXMPNode.hasNext()) {
      if (!((XMPNode)paramXMPNode.next()).hasChildren()) {
        paramXMPNode.remove();
      }
    }
  }
  
  private static void fixGPSTimeStamp(XMPNode paramXMPNode)
    throws XMPException
  {
    XMPNode localXMPNode = XMPNodeUtils.findChildNode(paramXMPNode, "exif:GPSTimeStamp", false);
    if (localXMPNode != null) {
      try
      {
        XMPDateTime localXMPDateTime = XMPUtils.convertToDate(localXMPNode.getValue());
        if (localXMPDateTime.getYear() != 0) {
          return;
        }
        if ((localXMPDateTime.getMonth() == 0) && (localXMPDateTime.getDay() == 0))
        {
          Object localObject = XMPNodeUtils.findChildNode(paramXMPNode, "exif:DateTimeOriginal", false);
          if (localObject != null) {}
          for (paramXMPNode = (XMPNode)localObject;; paramXMPNode = XMPNodeUtils.findChildNode(paramXMPNode, "exif:DateTimeDigitized", false))
          {
            paramXMPNode = XMPUtils.convertToDate(paramXMPNode.getValue());
            localObject = localXMPDateTime.getCalendar();
            ((Calendar)localObject).set(1, paramXMPNode.getYear());
            ((Calendar)localObject).set(2, paramXMPNode.getMonth());
            ((Calendar)localObject).set(5, paramXMPNode.getDay());
            localXMPNode.setValue(XMPUtils.convertFromDate(new XMPDateTimeImpl((Calendar)localObject)));
            return;
          }
        }
        return;
      }
      catch (XMPException paramXMPNode) {}
    }
  }
  
  private static void initDCArrays()
  {
    dcArrayForms = new HashMap();
    PropertyOptions localPropertyOptions = new PropertyOptions();
    localPropertyOptions.setArray(true);
    dcArrayForms.put("dc:contributor", localPropertyOptions);
    dcArrayForms.put("dc:language", localPropertyOptions);
    dcArrayForms.put("dc:publisher", localPropertyOptions);
    dcArrayForms.put("dc:relation", localPropertyOptions);
    dcArrayForms.put("dc:subject", localPropertyOptions);
    dcArrayForms.put("dc:type", localPropertyOptions);
    localPropertyOptions = new PropertyOptions();
    localPropertyOptions.setArray(true);
    localPropertyOptions.setArrayOrdered(true);
    dcArrayForms.put("dc:creator", localPropertyOptions);
    dcArrayForms.put("dc:date", localPropertyOptions);
    localPropertyOptions = new PropertyOptions();
    localPropertyOptions.setArray(true);
    localPropertyOptions.setArrayOrdered(true);
    localPropertyOptions.setArrayAlternate(true);
    localPropertyOptions.setArrayAltText(true);
    dcArrayForms.put("dc:description", localPropertyOptions);
    dcArrayForms.put("dc:rights", localPropertyOptions);
    dcArrayForms.put("dc:title", localPropertyOptions);
  }
  
  private static void migrateAudioCopyright(XMPMeta paramXMPMeta, XMPNode paramXMPNode)
  {
    try
    {
      Object localObject = XMPNodeUtils.findSchemaNode(((XMPMetaImpl)paramXMPMeta).getRoot(), "http://purl.org/dc/elements/1.1/", true);
      String str = paramXMPNode.getValue();
      localObject = XMPNodeUtils.findChildNode((XMPNode)localObject, "dc:rights", false);
      if (localObject == null) {
        paramXMPMeta.setLocalizedText("http://purl.org/dc/elements/1.1/", "rights", "", "x-default", "\n\n" + str, null);
      }
      for (;;)
      {
        paramXMPNode.getParent().removeChild(paramXMPNode);
        return;
        if (!((XMPNode)localObject).hasChildren()) {
          break;
        }
        int i = XMPNodeUtils.lookupLanguageItem((XMPNode)localObject, "x-default");
        if (i >= 0) {}
        for (;;)
        {
          paramXMPMeta = ((XMPNode)localObject).getChild(i);
          localObject = paramXMPMeta.getValue();
          i = ((String)localObject).indexOf("\n\n");
          if (i < 0) {
            break label206;
          }
          if (((String)localObject).substring(i + 2).equals(str)) {
            break;
          }
          paramXMPMeta.setValue(((String)localObject).substring(0, i + 2) + str);
          break;
          paramXMPMeta.setLocalizedText("http://purl.org/dc/elements/1.1/", "rights", "", "x-default", ((XMPNode)localObject).getChild(1).getValue(), null);
          i = XMPNodeUtils.lookupLanguageItem((XMPNode)localObject, "x-default");
        }
        label206:
        if (!str.equals(localObject)) {
          paramXMPMeta.setValue(localObject + "\n\n" + str);
        }
      }
      return;
    }
    catch (XMPException paramXMPMeta) {}
  }
  
  private static void moveExplicitAliases(XMPNode paramXMPNode, ParseOptions paramParseOptions)
    throws XMPException
  {
    boolean bool;
    Iterator localIterator1;
    if (paramXMPNode.getHasAliases())
    {
      paramXMPNode.setHasAliases(false);
      bool = paramParseOptions.getStrictAliasing();
      localIterator1 = paramXMPNode.getUnmodifiableChildren().iterator();
    }
    while (localIterator1.hasNext())
    {
      XMPNode localXMPNode1 = (XMPNode)localIterator1.next();
      if (localXMPNode1.getHasAliases())
      {
        Iterator localIterator2 = localXMPNode1.iterateChildren();
        while (localIterator2.hasNext())
        {
          XMPNode localXMPNode2 = (XMPNode)localIterator2.next();
          if (localXMPNode2.isAlias())
          {
            localXMPNode2.setAlias(false);
            XMPAliasInfo localXMPAliasInfo = XMPMetaFactory.getSchemaRegistry().findAlias(localXMPNode2.getName());
            if (localXMPAliasInfo != null)
            {
              paramParseOptions = XMPNodeUtils.findSchemaNode(paramXMPNode, localXMPAliasInfo.getNamespace(), null, true);
              paramParseOptions.setImplicit(false);
              XMPNode localXMPNode3 = XMPNodeUtils.findChildNode(paramParseOptions, localXMPAliasInfo.getPrefix() + localXMPAliasInfo.getPropName(), false);
              if (localXMPNode3 != null)
              {
                if (localXMPAliasInfo.getAliasForm().isSimple()) {
                  break label368;
                }
                if (localXMPAliasInfo.getAliasForm().isArrayAltText()) {
                  break label393;
                }
                if (localXMPNode3.hasChildren()) {
                  break label421;
                }
                paramParseOptions = null;
                label218:
                if (paramParseOptions == null) {
                  break label431;
                }
                if (bool) {
                  break label443;
                }
              }
              for (;;)
              {
                localIterator2.remove();
                break;
                return;
                if (!localXMPAliasInfo.getAliasForm().isSimple())
                {
                  localXMPNode3 = new XMPNode(localXMPAliasInfo.getPrefix() + localXMPAliasInfo.getPropName(), localXMPAliasInfo.getAliasForm().toPropertyOptions());
                  paramParseOptions.addChild(localXMPNode3);
                  transplantArrayItemAlias(localIterator2, localXMPNode2, localXMPNode3);
                  break;
                }
                localXMPNode2.setName(localXMPAliasInfo.getPrefix() + localXMPAliasInfo.getPropName());
                paramParseOptions.addChild(localXMPNode2);
                localIterator2.remove();
                break;
                label368:
                if (!bool) {}
                for (;;)
                {
                  localIterator2.remove();
                  break;
                  compareAliasedSubtrees(localXMPNode2, localXMPNode3, true);
                }
                label393:
                int i = XMPNodeUtils.lookupLanguageItem(localXMPNode3, "x-default");
                if (i == -1)
                {
                  paramParseOptions = null;
                  break label218;
                }
                paramParseOptions = localXMPNode3.getChild(i);
                break label218;
                label421:
                paramParseOptions = localXMPNode3.getChild(1);
                break label218;
                label431:
                transplantArrayItemAlias(localIterator2, localXMPNode2, localXMPNode3);
                break;
                label443:
                compareAliasedSubtrees(localXMPNode2, paramParseOptions, true);
              }
            }
          }
        }
        localXMPNode1.setHasAliases(false);
      }
    }
  }
  
  private static void normalizeDCArrays(XMPNode paramXMPNode)
    throws XMPException
  {
    int i = 1;
    if (i <= paramXMPNode.getChildrenLength())
    {
      XMPNode localXMPNode1 = paramXMPNode.getChild(i);
      PropertyOptions localPropertyOptions = (PropertyOptions)dcArrayForms.get(localXMPNode1.getName());
      if (localPropertyOptions != null)
      {
        if (localXMPNode1.getOptions().isSimple()) {
          break label79;
        }
        localXMPNode1.getOptions().setOption(7680, false);
        localXMPNode1.getOptions().mergeWith(localPropertyOptions);
        if (localPropertyOptions.isArrayAltText()) {
          break label150;
        }
      }
      for (;;)
      {
        i += 1;
        break;
        label79:
        XMPNode localXMPNode2 = new XMPNode(localXMPNode1.getName(), localPropertyOptions);
        localXMPNode1.setName("[]");
        localXMPNode2.addChild(localXMPNode1);
        paramXMPNode.replaceChild(i, localXMPNode2);
        if ((localPropertyOptions.isArrayAltText()) && (!localXMPNode1.getOptions().getHasLanguage()))
        {
          localXMPNode1.addQualifier(new XMPNode("xml:lang", "x-default", null));
          continue;
          label150:
          repairAltText(localXMPNode1);
        }
      }
    }
  }
  
  static XMPMeta process(XMPMetaImpl paramXMPMetaImpl, ParseOptions paramParseOptions)
    throws XMPException
  {
    XMPNode localXMPNode = paramXMPMetaImpl.getRoot();
    touchUpDataModel(paramXMPMetaImpl);
    moveExplicitAliases(localXMPNode, paramParseOptions);
    tweakOldXMP(localXMPNode);
    deleteEmptySchemas(localXMPNode);
    return paramXMPMetaImpl;
  }
  
  private static void repairAltText(XMPNode paramXMPNode)
    throws XMPException
  {
    if (paramXMPNode == null) {}
    while (!paramXMPNode.getOptions().isArray()) {
      return;
    }
    paramXMPNode.getOptions().setArrayOrdered(true).setArrayAlternate(true).setArrayAltText(true);
    paramXMPNode = paramXMPNode.iterateChildren();
    while (paramXMPNode.hasNext())
    {
      XMPNode localXMPNode = (XMPNode)paramXMPNode.next();
      String str;
      if (!localXMPNode.getOptions().isCompositeProperty())
      {
        if (localXMPNode.getOptions().getHasLanguage()) {
          continue;
        }
        str = localXMPNode.getValue();
        if (str != null) {
          break label103;
        }
      }
      label103:
      while (str.length() == 0)
      {
        paramXMPNode.remove();
        break;
        paramXMPNode.remove();
        break;
      }
      localXMPNode.addQualifier(new XMPNode("xml:lang", "x-repair", null));
    }
  }
  
  private static void touchUpDataModel(XMPMetaImpl paramXMPMetaImpl)
    throws XMPException
  {
    XMPNodeUtils.findSchemaNode(paramXMPMetaImpl.getRoot(), "http://purl.org/dc/elements/1.1/", true);
    Iterator localIterator = paramXMPMetaImpl.getRoot().iterateChildren();
    while (localIterator.hasNext())
    {
      XMPNode localXMPNode = (XMPNode)localIterator.next();
      if (!"http://purl.org/dc/elements/1.1/".equals(localXMPNode.getName()))
      {
        if (!"http://ns.adobe.com/exif/1.0/".equals(localXMPNode.getName()))
        {
          if ("http://ns.adobe.com/xmp/1.0/DynamicMedia/".equals(localXMPNode.getName())) {
            break label140;
          }
          if (!"http://ns.adobe.com/xap/1.0/rights/".equals(localXMPNode.getName())) {
            continue;
          }
          localXMPNode = XMPNodeUtils.findChildNode(localXMPNode, "xmpRights:UsageTerms", false);
          if (localXMPNode == null) {
            continue;
          }
          repairAltText(localXMPNode);
        }
      }
      else
      {
        normalizeDCArrays(localXMPNode);
        continue;
      }
      fixGPSTimeStamp(localXMPNode);
      localXMPNode = XMPNodeUtils.findChildNode(localXMPNode, "exif:UserComment", false);
      if (localXMPNode != null)
      {
        repairAltText(localXMPNode);
        continue;
        label140:
        localXMPNode = XMPNodeUtils.findChildNode(localXMPNode, "xmpDM:copyright", false);
        if (localXMPNode != null) {
          migrateAudioCopyright(paramXMPMetaImpl, localXMPNode);
        }
      }
    }
  }
  
  private static void transplantArrayItemAlias(Iterator paramIterator, XMPNode paramXMPNode1, XMPNode paramXMPNode2)
    throws XMPException
  {
    if (!paramXMPNode2.getOptions().isArrayAltText()) {}
    for (;;)
    {
      paramIterator.remove();
      paramXMPNode1.setName("[]");
      paramXMPNode2.addChild(paramXMPNode1);
      return;
      if (paramXMPNode1.getOptions().getHasLanguage()) {
        break;
      }
      paramXMPNode1.addQualifier(new XMPNode("xml:lang", "x-default", null));
    }
    throw new XMPException("Alias to x-default already has a language qualifier", 203);
  }
  
  private static void tweakOldXMP(XMPNode paramXMPNode)
    throws XMPException
  {
    if (paramXMPNode.getName() == null) {}
    String str;
    XMPNode localXMPNode;
    for (;;)
    {
      return;
      if (paramXMPNode.getName().length() >= 36)
      {
        str = paramXMPNode.getName().toLowerCase();
        if (!str.startsWith("uuid:")) {}
        while (Utils.checkUUIDFormat(str))
        {
          localXMPNode = XMPNodeUtils.findNode(paramXMPNode, XMPPathParser.expandXPath("http://ns.adobe.com/xap/1.0/mm/", "InstanceID"), true, null);
          if (localXMPNode != null) {
            break label87;
          }
          throw new XMPException("Failure creating xmpMM:InstanceID", 9);
          str = str.substring(5);
        }
      }
    }
    label87:
    localXMPNode.setOptions(null);
    localXMPNode.setValue("uuid:" + str);
    localXMPNode.removeChildren();
    localXMPNode.removeQualifiers();
    paramXMPNode.setName(null);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/impl/XMPNormalizer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */