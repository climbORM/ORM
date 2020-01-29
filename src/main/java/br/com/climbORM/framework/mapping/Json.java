package br.com.climbORM.framework.mapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Json {

    public static final String JSON = "JSON";
    public static final String JSONB = "JSONB";

    public String typeJson();

}
