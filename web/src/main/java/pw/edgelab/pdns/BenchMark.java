package pw.edgelab.pdns;

import javax.interceptor.Interceptors;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Interceptors(ExecutionTimeInterceptor.class)
public @interface BenchMark {

}
