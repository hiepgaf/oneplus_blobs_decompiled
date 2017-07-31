package android.filterfw.core;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.FIELD})
public @interface GenerateProgramPort
{
  boolean hasDefault() default false;
  
  String name();
  
  Class type();
  
  String variableName() default "";
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/GenerateProgramPort.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */