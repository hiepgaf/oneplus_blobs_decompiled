package android.sax;

class Children
{
  Child[] children = new Child[16];
  
  Element get(String paramString1, String paramString2)
  {
    int i = paramString1.hashCode() * 31 + paramString2.hashCode();
    Child localChild2 = this.children[(i & 0xF)];
    Child localChild1 = localChild2;
    if (localChild2 == null) {
      return null;
    }
    while ((localChild1.hash != i) || (localChild1.uri.compareTo(paramString1) != 0) || (localChild1.localName.compareTo(paramString2) != 0))
    {
      localChild1 = localChild1.next;
      if (localChild1 == null) {
        break;
      }
    }
    return localChild1;
    return null;
  }
  
  Element getOrCreate(Element paramElement, String paramString1, String paramString2)
  {
    int i = paramString1.hashCode() * 31 + paramString2.hashCode();
    int j = i & 0xF;
    Child localChild2 = this.children[j];
    Child localChild1 = localChild2;
    if (localChild2 == null)
    {
      paramElement = new Child(paramElement, paramString1, paramString2, paramElement.depth + 1, i);
      this.children[j] = paramElement;
      return paramElement;
    }
    while ((localChild1.hash != i) || (localChild1.uri.compareTo(paramString1) != 0) || (localChild1.localName.compareTo(paramString2) != 0))
    {
      localChild2 = localChild1.next;
      if (localChild2 == null) {
        break;
      }
      localChild1 = localChild2;
    }
    return localChild1;
    paramElement = new Child(paramElement, paramString1, paramString2, paramElement.depth + 1, i);
    localChild1.next = paramElement;
    return paramElement;
  }
  
  static class Child
    extends Element
  {
    final int hash;
    Child next;
    
    Child(Element paramElement, String paramString1, String paramString2, int paramInt1, int paramInt2)
    {
      super(paramString1, paramString2, paramInt1);
      this.hash = paramInt2;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/sax/Children.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */