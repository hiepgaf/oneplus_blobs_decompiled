package android.support.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.CLASS)
@Target({java.lang.annotation.ElementType.METHOD})
public @interface CallSuper {}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/annotation/CallSuper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */