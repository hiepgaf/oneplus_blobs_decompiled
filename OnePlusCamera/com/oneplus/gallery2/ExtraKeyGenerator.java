package com.oneplus.gallery2;

public abstract interface ExtraKeyGenerator
{
  public abstract <TValue> ExtraKey<TValue> generateKey(Class<TValue> paramClass);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/ExtraKeyGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */