package com.adobe.xmp.impl;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPIterator;
import com.adobe.xmp.impl.xpath.XMPPath;
import com.adobe.xmp.impl.xpath.XMPPathParser;
import com.adobe.xmp.options.IteratorOptions;
import com.adobe.xmp.options.PropertyOptions;
import com.adobe.xmp.properties.XMPPropertyInfo;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class XMPIteratorImpl
  implements XMPIterator
{
  private String baseNS = null;
  private Iterator nodeIterator = null;
  private IteratorOptions options;
  protected boolean skipSiblings = false;
  protected boolean skipSubtree = false;
  
  public XMPIteratorImpl(XMPMetaImpl paramXMPMetaImpl, String paramString1, String paramString2, IteratorOptions paramIteratorOptions)
    throws XMPException
  {
    IteratorOptions localIteratorOptions = paramIteratorOptions;
    if (paramIteratorOptions == null) {
      localIteratorOptions = new IteratorOptions();
    }
    this.options = localIteratorOptions;
    int i;
    label55:
    label59:
    int j;
    if (paramString1 == null)
    {
      i = 0;
      if (paramString2 != null) {
        break label102;
      }
      j = 0;
      label62:
      if (i == 0) {
        break label115;
      }
    }
    for (;;)
    {
      if (i == 0)
      {
        if (i != 0) {
          break label225;
        }
        label77:
        throw new XMPException("Schema namespace URI is required", 101);
        if (paramString1.length() <= 0) {
          break;
        }
        i = 1;
        break label55;
        label102:
        if (paramString2.length() <= 0) {
          break label59;
        }
        j = 1;
        break label62;
        label115:
        if (j == 0)
        {
          paramString1 = paramXMPMetaImpl.getRoot();
          paramXMPMetaImpl = null;
        }
      }
    }
    for (;;)
    {
      if (paramString1 != null) {
        break label245;
      }
      this.nodeIterator = Collections.EMPTY_LIST.iterator();
      return;
      if (j == 0) {
        break;
      }
      paramString2 = XMPPathParser.expandXPath(paramString1, paramString2);
      paramIteratorOptions = new XMPPath();
      i = 0;
      while (i < paramString2.size() - 1)
      {
        paramIteratorOptions.add(paramString2.getSegment(i));
        i += 1;
      }
      paramString2 = XMPNodeUtils.findNode(paramXMPMetaImpl.getRoot(), paramString2, false, null);
      this.baseNS = paramString1;
      paramXMPMetaImpl = paramIteratorOptions.toString();
      paramString1 = paramString2;
      continue;
      label225:
      if (j != 0) {
        break label77;
      }
      paramString1 = XMPNodeUtils.findSchemaNode(paramXMPMetaImpl.getRoot(), paramString1, false);
      paramXMPMetaImpl = null;
    }
    label245:
    if (this.options.isJustChildren())
    {
      this.nodeIterator = new NodeIteratorChildren(paramString1, paramXMPMetaImpl);
      return;
    }
    this.nodeIterator = new NodeIterator(paramString1, paramXMPMetaImpl, 1);
  }
  
  protected String getBaseNS()
  {
    return this.baseNS;
  }
  
  protected IteratorOptions getOptions()
  {
    return this.options;
  }
  
  public boolean hasNext()
  {
    return this.nodeIterator.hasNext();
  }
  
  public Object next()
  {
    return this.nodeIterator.next();
  }
  
  public void remove()
  {
    throw new UnsupportedOperationException("The XMPIterator does not support remove().");
  }
  
  protected void setBaseNS(String paramString)
  {
    this.baseNS = paramString;
  }
  
  public void skipSiblings()
  {
    skipSubtree();
    this.skipSiblings = true;
  }
  
  public void skipSubtree()
  {
    this.skipSubtree = true;
  }
  
  private class NodeIterator
    implements Iterator
  {
    protected static final int ITERATE_CHILDREN = 1;
    protected static final int ITERATE_NODE = 0;
    protected static final int ITERATE_QUALIFIER = 2;
    private Iterator childrenIterator = null;
    private int index = 0;
    private String path;
    private XMPPropertyInfo returnProperty = null;
    private int state = 0;
    private Iterator subIterator = Collections.EMPTY_LIST.iterator();
    private XMPNode visitedNode;
    
    public NodeIterator() {}
    
    public NodeIterator(XMPNode paramXMPNode, String paramString, int paramInt)
    {
      this.visitedNode = paramXMPNode;
      this.state = 0;
      if (!paramXMPNode.getOptions().isSchemaNode()) {}
      for (;;)
      {
        this.path = accumulatePath(paramXMPNode, paramString, paramInt);
        return;
        XMPIteratorImpl.this.setBaseNS(paramXMPNode.getName());
      }
    }
    
    private boolean iterateChildren(Iterator paramIterator)
    {
      if (!XMPIteratorImpl.this.skipSiblings) {
        if (!this.subIterator.hasNext()) {
          break label59;
        }
      }
      for (;;)
      {
        if (this.subIterator.hasNext()) {
          break label115;
        }
        return false;
        XMPIteratorImpl.this.skipSiblings = false;
        this.subIterator = Collections.EMPTY_LIST.iterator();
        break;
        label59:
        if (paramIterator.hasNext())
        {
          paramIterator = (XMPNode)paramIterator.next();
          this.index += 1;
          this.subIterator = new NodeIterator(XMPIteratorImpl.this, paramIterator, this.path, this.index);
        }
      }
      label115:
      this.returnProperty = ((XMPPropertyInfo)this.subIterator.next());
      return true;
    }
    
    protected String accumulatePath(XMPNode paramXMPNode, String paramString, int paramInt)
    {
      if (paramXMPNode.getParent() == null) {}
      while (paramXMPNode.getOptions().isSchemaNode()) {
        return null;
      }
      String str;
      if (!paramXMPNode.getParent().getOptions().isArray())
      {
        str = "/";
        paramXMPNode = paramXMPNode.getName();
        if (paramString != null) {
          break label79;
        }
      }
      label79:
      while (paramString.length() == 0)
      {
        return paramXMPNode;
        str = "";
        paramXMPNode = "[" + String.valueOf(paramInt) + "]";
        break;
      }
      if (!XMPIteratorImpl.this.getOptions().isJustLeafname()) {
        return paramString + str + paramXMPNode;
      }
      paramString = paramXMPNode;
      if (paramXMPNode.startsWith("?")) {
        paramString = paramXMPNode.substring(1);
      }
      return paramString;
    }
    
    protected XMPPropertyInfo createPropertyInfo(final XMPNode paramXMPNode, final String paramString1, final String paramString2)
    {
      if (!paramXMPNode.getOptions().isSchemaNode()) {}
      for (final String str = paramXMPNode.getValue();; str = null) {
        new XMPPropertyInfo()
        {
          public String getLanguage()
          {
            return null;
          }
          
          public String getNamespace()
          {
            return paramString1;
          }
          
          public PropertyOptions getOptions()
          {
            return paramXMPNode.getOptions();
          }
          
          public String getPath()
          {
            return paramString2;
          }
          
          public Object getValue()
          {
            return str;
          }
        };
      }
    }
    
    protected Iterator getChildrenIterator()
    {
      return this.childrenIterator;
    }
    
    protected XMPPropertyInfo getReturnProperty()
    {
      return this.returnProperty;
    }
    
    public boolean hasNext()
    {
      if (this.returnProperty == null)
      {
        if (this.state == 0) {
          break label40;
        }
        if (this.state == 1) {
          break label45;
        }
        if (this.childrenIterator == null) {
          break label119;
        }
      }
      for (;;)
      {
        return iterateChildren(this.childrenIterator);
        return true;
        label40:
        return reportNode();
        label45:
        boolean bool;
        if (this.childrenIterator != null)
        {
          bool = iterateChildren(this.childrenIterator);
          if (!bool) {
            break label81;
          }
        }
        label81:
        while ((!this.visitedNode.hasQualifier()) || (XMPIteratorImpl.this.getOptions().isOmitQualifiers()))
        {
          return bool;
          this.childrenIterator = this.visitedNode.iterateChildren();
          break;
        }
        this.state = 2;
        this.childrenIterator = null;
        return hasNext();
        label119:
        this.childrenIterator = this.visitedNode.iterateQualifier();
      }
    }
    
    public Object next()
    {
      if (!hasNext()) {
        throw new NoSuchElementException("There are no more nodes to return");
      }
      XMPPropertyInfo localXMPPropertyInfo = this.returnProperty;
      this.returnProperty = null;
      return localXMPPropertyInfo;
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
    
    protected boolean reportNode()
    {
      this.state = 1;
      if (this.visitedNode.getParent() == null) {}
      for (;;)
      {
        return hasNext();
        if (!XMPIteratorImpl.this.getOptions().isJustLeafnodes()) {}
        while (!this.visitedNode.hasChildren())
        {
          this.returnProperty = createPropertyInfo(this.visitedNode, XMPIteratorImpl.this.getBaseNS(), this.path);
          return true;
        }
      }
    }
    
    protected void setChildrenIterator(Iterator paramIterator)
    {
      this.childrenIterator = paramIterator;
    }
    
    protected void setReturnProperty(XMPPropertyInfo paramXMPPropertyInfo)
    {
      this.returnProperty = paramXMPPropertyInfo;
    }
  }
  
  private class NodeIteratorChildren
    extends XMPIteratorImpl.NodeIterator
  {
    private Iterator childrenIterator;
    private int index = 0;
    private String parentPath;
    
    public NodeIteratorChildren(XMPNode paramXMPNode, String paramString)
    {
      super();
      if (!paramXMPNode.getOptions().isSchemaNode()) {}
      for (;;)
      {
        this.parentPath = accumulatePath(paramXMPNode, paramString, 1);
        this.childrenIterator = paramXMPNode.iterateChildren();
        return;
        XMPIteratorImpl.this.setBaseNS(paramXMPNode.getName());
      }
    }
    
    public boolean hasNext()
    {
      String str = null;
      if (getReturnProperty() == null)
      {
        if (!XMPIteratorImpl.this.skipSiblings)
        {
          if (this.childrenIterator.hasNext()) {
            break label37;
          }
          return false;
        }
      }
      else {
        return true;
      }
      return false;
      label37:
      XMPNode localXMPNode = (XMPNode)this.childrenIterator.next();
      this.index += 1;
      if (!localXMPNode.getOptions().isSchemaNode())
      {
        if (localXMPNode.getParent() != null) {
          break label123;
        }
        if (XMPIteratorImpl.this.getOptions().isJustLeafnodes()) {
          break label140;
        }
      }
      label123:
      label140:
      while (!localXMPNode.hasChildren())
      {
        setReturnProperty(createPropertyInfo(localXMPNode, XMPIteratorImpl.this.getBaseNS(), str));
        return true;
        XMPIteratorImpl.this.setBaseNS(localXMPNode.getName());
        break;
        str = accumulatePath(localXMPNode, this.parentPath, this.index);
        break;
      }
      return hasNext();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/impl/XMPIteratorImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */