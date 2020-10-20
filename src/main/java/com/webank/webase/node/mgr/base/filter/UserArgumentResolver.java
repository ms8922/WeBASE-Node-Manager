package com.webank.webase.node.mgr.base.filter;


import com.webank.webase.node.mgr.base.tools.NodeMgrTools;
import com.webank.webase.node.mgr.user.entity.TbUser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * 用于方法参数上加Token4User标签获取当前登录用户的信息.
 * <p>
 *
 */
@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    private final Logger logger = LoggerFactory.getLogger(UserArgumentResolver.class);

    /**
     * 过滤出符合条件的参数，这里指的是加了 CurrentUser 注解的参数
     *
     * @param parameter
     * @return
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        AnnotatedElement annotatedElement = parameter.getAnnotatedElement();
        Annotation[] annotations = annotatedElement.getAnnotations();
        logger.info(annotations.toString());
        return parameter.hasParameterAnnotation(CurrentUser.class);
    }

    @Override
    public TbUser resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest
            webRequest, WebDataBinderFactory binderFactory) {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        //获取token
        String token = NodeMgrTools.getToken(servletRequest);


        if (StringUtils.isBlank(token)) {
            //todo 用户信息获取
            //UserInfoBean userInfoBo = new UserInfoBean();
        } else {
            //根据token获取用户信息
            return JwtTokenUtil.parsingUserInformation(token);
        }

        return null;
    }
}