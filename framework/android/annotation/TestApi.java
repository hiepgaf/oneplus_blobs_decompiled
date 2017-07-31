package android.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.CONSTRUCTOR, java.lang.annotation.ElementType.ANNOTATION_TYPE, java.lang.annotation.ElementType.PACKAGE})
public @interface TestApi {}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/annotation/TestApi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */