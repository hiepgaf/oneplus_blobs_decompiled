package com.amap.api.mapcore2d;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.TYPE})
public @interface di
{
  String a();
  
  boolean b() default false;
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/di.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */