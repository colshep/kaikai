package com.plunger.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * 使用@Aspect注解将一个java类定义为切面类
 * 使用@Pointcut定义一个切入点，可以是一个规则表达式，比如下例中某个package下的所有函数，也可以是一个注解等。
 * 根据需要在切入点不同位置的切入内容
 * -使用@Before在切入点开始处切入内容
 * -使用@After在切入点结尾处切入内容
 * -使用@AfterReturning在切入点return内容之后切入内容（可以用来对处理返回值做一些加工处理）
 * -使用@Around在切入点前后切入内容，并自己控制何时执行切入点自身的内容
 * -使用@AfterThrowing用来处理当切入内容部分抛出异常之后的处理逻辑
 */

@Aspect
@Component
@Order(1)
public class LogAspect {
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Pointcut("execution(public * com.plunger.controller..*.*(..))")
    public void methodExecute() {

    }

    @Before("methodExecute()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 记录下请求内容
        StringBuffer buffer = new StringBuffer();
        buffer.append("URL : " + request.getRequestURL().toString() + "；");
        buffer.append("HTTP_METHOD : " + request.getMethod() + "；");
        buffer.append("IP : " + request.getRemoteAddr() + "；");
        buffer.append("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName() + "；");
        buffer.append("ARGS : " + Arrays.toString(joinPoint.getArgs()) + "；");
        logger.info(buffer.toString());

    }

    @AfterReturning(returning = "ret", pointcut = "methodExecute()")
    public void doAfterReturning(Object ret) throws Throwable {
        // 处理完请求，返回内容
        logger.info("RESPONSE : " + ret);
    }
}
