package com.lan.accountbook.aspect;

import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import com.lan.accountbook.sys.domain.Billtype;

@Aspect
@Component
@EnableAspectJAutoProxy
public class BillTypeCacheAspect {

	private Map<String,Object> cache = new HashMap<>();
	private static final String BILL_TYPE_CACHE_PREFIX = "billtype:";

	@Pointcut("execution(* com.lan.accountbook.sys.service.impl.BilltypeServiceImpl.getById(..))")
	public void pc() { }

	@Around(value="pc()")
	public Object cacheBillType(ProceedingJoinPoint point) throws Throwable {
		Object[] args = point.getArgs();
		// 处理 null 参数
		if (args == null || args.length == 0 || args[0] == null) {
			return null;
		}
		Integer typeId = (Integer) args[0];
		Object obj = cache.get(BILL_TYPE_CACHE_PREFIX + typeId);
		if (obj != null) {
			System.out.println("缓存里面有数据");
			return obj;
		} else {
			System.err.println("缓存里面有没有数据执行SQL查询");
			Billtype res = (Billtype) point.proceed();
			if (res != null) {
				cache.put(BILL_TYPE_CACHE_PREFIX + res.getId(), res);
			}
			return res;
		}
	}
}