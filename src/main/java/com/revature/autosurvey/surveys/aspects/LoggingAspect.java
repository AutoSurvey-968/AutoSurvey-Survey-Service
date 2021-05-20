package com.revature.autosurvey.surveys.aspects;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggingAspect {

	@Around("everything()")
	public Object log(ProceedingJoinPoint pjp) throws Throwable {
		Object result = null;
		Logger log = LoggerFactory.getLogger(pjp.getTarget().getClass());
		log.debug("Method with signature: {}", pjp.getSignature());
		log.debug("with arguments: {}", Arrays.toString(pjp.getArgs()));
		try {
			result = pjp.proceed();
		} catch (Throwable t) {
			log.error("Method threw exception: {0}", t);
			for (StackTraceElement s : t.getStackTrace()) {
				log.warn(s.toString());
			}
			if (t.getCause() != null) {
				Throwable t2 = t.getCause();
				log.error("Method threw wrapped exception: {0}", t2);
				for (StackTraceElement s : t2.getStackTrace()) {
					log.warn(s.toString());
				}
			}
			throw t;
		}
		log.debug("Method returning with: %s", result);
		return result;
	}

	// hook - a method that only exists as the target for an annotation
	@Pointcut("execution( * com.revature.autosurvey.surveys..*(..) )"
			+ "&& !execution( * com.revature.autosurvey.surveys.karate..*(..) )")
	private void everything() {
		/* Empty method for hook */ }
}
