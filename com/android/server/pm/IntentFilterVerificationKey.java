package com.android.server.pm;

class IntentFilterVerificationKey
{
  public String className;
  public String domains;
  public String packageName;
  
  public IntentFilterVerificationKey(String[] paramArrayOfString, String paramString1, String paramString2)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    int j = paramArrayOfString.length;
    while (i < j)
    {
      localStringBuilder.append(paramArrayOfString[i]);
      i += 1;
    }
    this.domains = localStringBuilder.toString();
    this.packageName = paramString1;
    this.className = paramString2;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (getClass() != paramObject.getClass())) {
      return false;
    }
    paramObject = (IntentFilterVerificationKey)paramObject;
    if (this.domains != null)
    {
      if (!this.domains.equals(((IntentFilterVerificationKey)paramObject).domains)) {
        break label101;
      }
      if (this.className == null) {
        break label103;
      }
      if (!this.className.equals(((IntentFilterVerificationKey)paramObject).className)) {
        break label110;
      }
      label71:
      if (this.packageName == null) {
        break label112;
      }
      if (!this.packageName.equals(((IntentFilterVerificationKey)paramObject).packageName)) {
        break label119;
      }
    }
    label101:
    label103:
    label110:
    label112:
    while (((IntentFilterVerificationKey)paramObject).packageName == null)
    {
      return true;
      if (((IntentFilterVerificationKey)paramObject).domains == null) {
        break;
      }
      return false;
      if (((IntentFilterVerificationKey)paramObject).className == null) {
        break label71;
      }
      return false;
    }
    label119:
    return false;
  }
  
  public int hashCode()
  {
    int k = 0;
    int i;
    if (this.domains != null)
    {
      i = this.domains.hashCode();
      if (this.packageName == null) {
        break label64;
      }
    }
    label64:
    for (int j = this.packageName.hashCode();; j = 0)
    {
      if (this.className != null) {
        k = this.className.hashCode();
      }
      return (i * 31 + j) * 31 + k;
      i = 0;
      break;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/IntentFilterVerificationKey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */