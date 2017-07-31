package com.adobe.xmp.impl;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.options.PropertyOptions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

class XMPNode
  implements Comparable
{
  private boolean alias;
  private List children = null;
  private boolean hasAliases;
  private boolean hasValueChild;
  private boolean implicit;
  private String name;
  private PropertyOptions options = null;
  private XMPNode parent;
  private List qualifier = null;
  private String value;
  
  static
  {
    boolean bool = false;
    if (XMPNode.class.desiredAssertionStatus()) {}
    for (;;)
    {
      $assertionsDisabled = bool;
      return;
      bool = true;
    }
  }
  
  public XMPNode(String paramString, PropertyOptions paramPropertyOptions)
  {
    this(paramString, null, paramPropertyOptions);
  }
  
  public XMPNode(String paramString1, String paramString2, PropertyOptions paramPropertyOptions)
  {
    this.name = paramString1;
    this.value = paramString2;
    this.options = paramPropertyOptions;
  }
  
  private void assertChildNotExisting(String paramString)
    throws XMPException
  {
    if ("[]".equals(paramString)) {}
    while (findChildByName(paramString) == null) {
      return;
    }
    throw new XMPException("Duplicate property or field node '" + paramString + "'", 203);
  }
  
  private void assertQualifierNotExisting(String paramString)
    throws XMPException
  {
    if ("[]".equals(paramString)) {}
    while (findQualifierByName(paramString) == null) {
      return;
    }
    throw new XMPException("Duplicate '" + paramString + "' qualifier", 203);
  }
  
  private void dumpNode(StringBuffer paramStringBuffer, boolean paramBoolean, int paramInt1, int paramInt2)
  {
    int j = 0;
    int i = 0;
    while (i < paramInt1)
    {
      paramStringBuffer.append('\t');
      i += 1;
    }
    if (this.parent == null)
    {
      paramStringBuffer.append("ROOT NODE");
      if (this.name != null) {
        break label161;
      }
      if (this.value != null) {
        break label197;
      }
      label56:
      if (getOptions().containsOneOf(-1)) {
        break label233;
      }
      label67:
      paramStringBuffer.append('\n');
      if (paramBoolean) {
        break label281;
      }
      label78:
      if (paramBoolean) {
        break label411;
      }
    }
    for (;;)
    {
      return;
      if (!getOptions().isQualifier())
      {
        if (getParent().getOptions().isArray()) {
          break label137;
        }
        paramStringBuffer.append(this.name);
        break;
      }
      paramStringBuffer.append('?');
      paramStringBuffer.append(this.name);
      break;
      label137:
      paramStringBuffer.append('[');
      paramStringBuffer.append(paramInt2);
      paramStringBuffer.append(']');
      break;
      label161:
      if (this.name.length() <= 0) {
        break;
      }
      paramStringBuffer.append(" (");
      paramStringBuffer.append(this.name);
      paramStringBuffer.append(')');
      break;
      label197:
      if (this.value.length() <= 0) {
        break label56;
      }
      paramStringBuffer.append(" = \"");
      paramStringBuffer.append(this.value);
      paramStringBuffer.append('"');
      break label56;
      label233:
      paramStringBuffer.append("\t(");
      paramStringBuffer.append(getOptions().toString());
      paramStringBuffer.append(" : ");
      paramStringBuffer.append(getOptions().getOptionsString());
      paramStringBuffer.append(')');
      break label67;
      label281:
      if (!hasQualifier()) {
        break label78;
      }
      XMPNode[] arrayOfXMPNode = (XMPNode[])getQualifier().toArray(new XMPNode[getQualifierLength()]);
      paramInt2 = 0;
      if (arrayOfXMPNode.length <= paramInt2)
      {
        label320:
        Arrays.sort(arrayOfXMPNode, paramInt2, arrayOfXMPNode.length);
        paramInt2 = 0;
        while (paramInt2 < arrayOfXMPNode.length)
        {
          arrayOfXMPNode[paramInt2].dumpNode(paramStringBuffer, paramBoolean, paramInt1 + 2, paramInt2 + 1);
          paramInt2 += 1;
        }
        break label78;
      }
      if ("xml:lang".equals(arrayOfXMPNode[paramInt2].getName())) {}
      for (;;)
      {
        paramInt2 += 1;
        break;
        if (!"rdf:type".equals(arrayOfXMPNode[paramInt2].getName())) {
          break label320;
        }
      }
      label411:
      if (hasChildren())
      {
        arrayOfXMPNode = (XMPNode[])getChildren().toArray(new XMPNode[getChildrenLength()]);
        if (getOptions().isArray()) {
          paramInt2 = j;
        }
        while (paramInt2 < arrayOfXMPNode.length)
        {
          arrayOfXMPNode[paramInt2].dumpNode(paramStringBuffer, paramBoolean, paramInt1 + 1, paramInt2 + 1);
          paramInt2 += 1;
          continue;
          Arrays.sort(arrayOfXMPNode);
          paramInt2 = j;
        }
      }
    }
  }
  
  private XMPNode find(List paramList, String paramString)
  {
    if (paramList == null) {}
    XMPNode localXMPNode;
    do
    {
      while (!paramList.hasNext())
      {
        return null;
        paramList = paramList.iterator();
      }
      localXMPNode = (XMPNode)paramList.next();
    } while (!localXMPNode.getName().equals(paramString));
    return localXMPNode;
  }
  
  private List getChildren()
  {
    if (this.children != null) {}
    for (;;)
    {
      return this.children;
      this.children = new ArrayList(0);
    }
  }
  
  private List getQualifier()
  {
    if (this.qualifier != null) {}
    for (;;)
    {
      return this.qualifier;
      this.qualifier = new ArrayList(0);
    }
  }
  
  private boolean isLanguageNode()
  {
    return "xml:lang".equals(this.name);
  }
  
  private boolean isTypeNode()
  {
    return "rdf:type".equals(this.name);
  }
  
  public void addChild(int paramInt, XMPNode paramXMPNode)
    throws XMPException
  {
    assertChildNotExisting(paramXMPNode.getName());
    paramXMPNode.setParent(this);
    getChildren().add(paramInt - 1, paramXMPNode);
  }
  
  public void addChild(XMPNode paramXMPNode)
    throws XMPException
  {
    assertChildNotExisting(paramXMPNode.getName());
    paramXMPNode.setParent(this);
    getChildren().add(paramXMPNode);
  }
  
  public void addQualifier(XMPNode paramXMPNode)
    throws XMPException
  {
    int i = 1;
    assertQualifierNotExisting(paramXMPNode.getName());
    paramXMPNode.setParent(this);
    paramXMPNode.getOptions().setQualifier(true);
    getOptions().setHasQualifiers(true);
    if (!paramXMPNode.isLanguageNode())
    {
      if (!paramXMPNode.isTypeNode()) {
        getQualifier().add(paramXMPNode);
      }
    }
    else
    {
      this.options.setHasLanguage(true);
      getQualifier().add(0, paramXMPNode);
      return;
    }
    this.options.setHasType(true);
    List localList = getQualifier();
    if (this.options.getHasLanguage()) {}
    for (;;)
    {
      localList.add(i, paramXMPNode);
      return;
      i = 0;
    }
  }
  
  protected void cleanupChildren()
  {
    if (!this.children.isEmpty()) {
      return;
    }
    this.children = null;
  }
  
  public void clear()
  {
    this.options = null;
    this.name = null;
    this.value = null;
    this.children = null;
    this.qualifier = null;
  }
  
  public Object clone()
  {
    try
    {
      Object localObject = new PropertyOptions(getOptions().getOptions());
      localObject = new XMPNode(this.name, this.value, (PropertyOptions)localObject);
      cloneSubtree((XMPNode)localObject);
      return localObject;
    }
    catch (XMPException localXMPException)
    {
      for (;;)
      {
        PropertyOptions localPropertyOptions = new PropertyOptions();
      }
    }
  }
  
  /* Error */
  public void cloneSubtree(XMPNode paramXMPNode)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 269	com/adobe/xmp/impl/XMPNode:iterateChildren	()Ljava/util/Iterator;
    //   4: astore_2
    //   5: aload_2
    //   6: invokeinterface 205 1 0
    //   11: ifeq +33 -> 44
    //   14: aload_1
    //   15: aload_2
    //   16: invokeinterface 209 1 0
    //   21: checkcast 2	com/adobe/xmp/impl/XMPNode
    //   24: invokevirtual 271	com/adobe/xmp/impl/XMPNode:clone	()Ljava/lang/Object;
    //   27: checkcast 2	com/adobe/xmp/impl/XMPNode
    //   30: invokevirtual 273	com/adobe/xmp/impl/XMPNode:addChild	(Lcom/adobe/xmp/impl/XMPNode;)V
    //   33: goto -28 -> 5
    //   36: astore_1
    //   37: getstatic 34	com/adobe/xmp/impl/XMPNode:$assertionsDisabled	Z
    //   40: ifeq +40 -> 80
    //   43: return
    //   44: aload_0
    //   45: invokevirtual 276	com/adobe/xmp/impl/XMPNode:iterateQualifier	()Ljava/util/Iterator;
    //   48: astore_2
    //   49: aload_2
    //   50: invokeinterface 205 1 0
    //   55: ifeq -12 -> 43
    //   58: aload_1
    //   59: aload_2
    //   60: invokeinterface 209 1 0
    //   65: checkcast 2	com/adobe/xmp/impl/XMPNode
    //   68: invokevirtual 271	com/adobe/xmp/impl/XMPNode:clone	()Ljava/lang/Object;
    //   71: checkcast 2	com/adobe/xmp/impl/XMPNode
    //   74: invokevirtual 278	com/adobe/xmp/impl/XMPNode:addQualifier	(Lcom/adobe/xmp/impl/XMPNode;)V
    //   77: goto -28 -> 49
    //   80: new 280	java/lang/AssertionError
    //   83: dup
    //   84: invokespecial 281	java/lang/AssertionError:<init>	()V
    //   87: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	88	0	this	XMPNode
    //   0	88	1	paramXMPNode	XMPNode
    //   4	56	2	localIterator	Iterator
    // Exception table:
    //   from	to	target	type
    //   0	5	36	com/adobe/xmp/XMPException
    //   5	33	36	com/adobe/xmp/XMPException
    //   44	49	36	com/adobe/xmp/XMPException
    //   49	77	36	com/adobe/xmp/XMPException
  }
  
  public int compareTo(Object paramObject)
  {
    if (!getOptions().isSchemaNode()) {
      return this.name.compareTo(((XMPNode)paramObject).getName());
    }
    return this.value.compareTo(((XMPNode)paramObject).getValue());
  }
  
  public String dumpNode(boolean paramBoolean)
  {
    StringBuffer localStringBuffer = new StringBuffer(512);
    dumpNode(localStringBuffer, paramBoolean, 0, 0);
    return localStringBuffer.toString();
  }
  
  public XMPNode findChildByName(String paramString)
  {
    return find(getChildren(), paramString);
  }
  
  public XMPNode findQualifierByName(String paramString)
  {
    return find(this.qualifier, paramString);
  }
  
  public XMPNode getChild(int paramInt)
  {
    return (XMPNode)getChildren().get(paramInt - 1);
  }
  
  public int getChildrenLength()
  {
    if (this.children == null) {
      return 0;
    }
    return this.children.size();
  }
  
  public boolean getHasAliases()
  {
    return this.hasAliases;
  }
  
  public boolean getHasValueChild()
  {
    return this.hasValueChild;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public PropertyOptions getOptions()
  {
    if (this.options != null) {}
    for (;;)
    {
      return this.options;
      this.options = new PropertyOptions();
    }
  }
  
  public XMPNode getParent()
  {
    return this.parent;
  }
  
  public XMPNode getQualifier(int paramInt)
  {
    return (XMPNode)getQualifier().get(paramInt - 1);
  }
  
  public int getQualifierLength()
  {
    if (this.qualifier == null) {
      return 0;
    }
    return this.qualifier.size();
  }
  
  public List getUnmodifiableChildren()
  {
    return Collections.unmodifiableList(new ArrayList(getChildren()));
  }
  
  public String getValue()
  {
    return this.value;
  }
  
  public boolean hasChildren()
  {
    if (this.children == null) {}
    while (this.children.size() <= 0) {
      return false;
    }
    return true;
  }
  
  public boolean hasQualifier()
  {
    if (this.qualifier == null) {}
    while (this.qualifier.size() <= 0) {
      return false;
    }
    return true;
  }
  
  public boolean isAlias()
  {
    return this.alias;
  }
  
  public boolean isImplicit()
  {
    return this.implicit;
  }
  
  public Iterator iterateChildren()
  {
    if (this.children == null) {
      return Collections.EMPTY_LIST.listIterator();
    }
    return getChildren().iterator();
  }
  
  public Iterator iterateQualifier()
  {
    if (this.qualifier == null) {
      return Collections.EMPTY_LIST.iterator();
    }
    new Iterator()
    {
      public boolean hasNext()
      {
        return this.val$it.hasNext();
      }
      
      public Object next()
      {
        return this.val$it.next();
      }
      
      public void remove()
      {
        throw new UnsupportedOperationException("remove() is not allowed due to the internal contraints");
      }
    };
  }
  
  public void removeChild(int paramInt)
  {
    getChildren().remove(paramInt - 1);
    cleanupChildren();
  }
  
  public void removeChild(XMPNode paramXMPNode)
  {
    getChildren().remove(paramXMPNode);
    cleanupChildren();
  }
  
  public void removeChildren()
  {
    this.children = null;
  }
  
  public void removeQualifier(XMPNode paramXMPNode)
  {
    PropertyOptions localPropertyOptions = getOptions();
    if (!paramXMPNode.isLanguageNode()) {
      if (paramXMPNode.isTypeNode()) {
        break label52;
      }
    }
    for (;;)
    {
      getQualifier().remove(paramXMPNode);
      if (this.qualifier.isEmpty()) {
        break;
      }
      return;
      localPropertyOptions.setHasLanguage(false);
      continue;
      label52:
      localPropertyOptions.setHasType(false);
    }
    localPropertyOptions.setHasQualifiers(false);
    this.qualifier = null;
  }
  
  public void removeQualifiers()
  {
    PropertyOptions localPropertyOptions = getOptions();
    localPropertyOptions.setHasQualifiers(false);
    localPropertyOptions.setHasLanguage(false);
    localPropertyOptions.setHasType(false);
    this.qualifier = null;
  }
  
  public void replaceChild(int paramInt, XMPNode paramXMPNode)
  {
    paramXMPNode.setParent(this);
    getChildren().set(paramInt - 1, paramXMPNode);
  }
  
  public void setAlias(boolean paramBoolean)
  {
    this.alias = paramBoolean;
  }
  
  public void setHasAliases(boolean paramBoolean)
  {
    this.hasAliases = paramBoolean;
  }
  
  public void setHasValueChild(boolean paramBoolean)
  {
    this.hasValueChild = paramBoolean;
  }
  
  public void setImplicit(boolean paramBoolean)
  {
    this.implicit = paramBoolean;
  }
  
  public void setName(String paramString)
  {
    this.name = paramString;
  }
  
  public void setOptions(PropertyOptions paramPropertyOptions)
  {
    this.options = paramPropertyOptions;
  }
  
  protected void setParent(XMPNode paramXMPNode)
  {
    this.parent = paramXMPNode;
  }
  
  public void setValue(String paramString)
  {
    this.value = paramString;
  }
  
  public void sort()
  {
    int j = 0;
    if (!hasQualifier()) {}
    Object localObject;
    while (!hasChildren())
    {
      return;
      localObject = (XMPNode[])getQualifier().toArray(new XMPNode[getQualifierLength()]);
      int i = 0;
      if (localObject.length <= i)
      {
        label45:
        Arrays.sort((Object[])localObject, i, localObject.length);
        ListIterator localListIterator = this.qualifier.listIterator();
        i = j;
        while (i < localObject.length)
        {
          localListIterator.next();
          localListIterator.set(localObject[i]);
          localObject[i].sort();
          i += 1;
        }
      }
      else
      {
        if ("xml:lang".equals(localObject[i].getName())) {}
        for (;;)
        {
          localObject[i].sort();
          i += 1;
          break;
          if (!"rdf:type".equals(localObject[i].getName())) {
            break label45;
          }
        }
      }
    }
    if (getOptions().isArray()) {}
    for (;;)
    {
      localObject = iterateChildren();
      while (((Iterator)localObject).hasNext()) {
        ((XMPNode)((Iterator)localObject).next()).sort();
      }
      break;
      Collections.sort(this.children);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/impl/XMPNode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */