package de.headshotharp.chestsort2.hibernate.testutils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@ExtendWith(H2HibernateConfigurationExtension.class)
public @interface ConfigureH2Hibernate {

}
