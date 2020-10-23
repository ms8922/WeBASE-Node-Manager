/**
 * Copyright 2014-2020  the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.webank.webase.node.mgr.base.filter;

import java.io.IOException;
import java.math.BigInteger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webank.webase.node.mgr.account.AccountService;
import com.webank.webase.node.mgr.account.entity.TbAccountInfo;
import com.webank.webase.node.mgr.node.entity.Node;
import com.webank.webase.node.mgr.user.entity.TbUser;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.web3j.utils.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import com.webank.webase.node.mgr.base.code.ConstantCode;
import com.webank.webase.node.mgr.base.exception.NodeMgrException;
import com.webank.webase.node.mgr.base.tools.HttpRequestTools;
import com.webank.webase.node.mgr.base.tools.NodeMgrTools;
import com.webank.webase.node.mgr.token.TokenService;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * validate code before login.
 */
@Log4j2
@Component
@Order(-1001)
@WebFilter(filterName = "validateCodeFilter", urlPatterns = "/*")
public class ValidateCodeFilter implements Filter {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private static final String LOGIN_URI = "/account/login";
    private static final String LOGIN_METHOD = "post";

    /**
     * do filter.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse rsp = (HttpServletResponse) response;
        String uri = HttpRequestTools.getUri(req);


        //is login
        if (LOGIN_URI.equalsIgnoreCase(uri) && LOGIN_METHOD.equalsIgnoreCase(req.getMethod())) {
            try {
                validateCode(req);
            } catch (NodeMgrException ex) {
                NodeMgrTools.responseRetCodeException(rsp, ex.getRetCode());
                return;
            } finally {
                //remove token
                tokenService.deleteToken(req.getHeader("token"), null);
            }
        }else{
            userCheck(req);

        }
        chain.doFilter(request, response);
    }


    /**
     * validate code.
     */
    private void validateCode(HttpServletRequest request) {
        String tokenInHeard = request.getHeader("token");
        String codeInRequest = request.getParameter("checkCode");
        log.info("validateCode. tokenInHeard:{} codeInRequest:{}", tokenInHeard, codeInRequest);

        if (StringUtils.isBlank(codeInRequest)) {
            throw new NodeMgrException(ConstantCode.CHECK_CODE_NULL);
        }
        if (StringUtils.isBlank(tokenInHeard)) {
            throw new NodeMgrException(ConstantCode.INVALID_CHECK_CODE);
        }
        userCheck(request);
        String code = tokenService.getValueFromToken(tokenInHeard);
        if (!codeInRequest.equalsIgnoreCase(code)) {
            log.warn("fail validateCode. realCheckCode:{} codeInRequest:{}", code,
                    codeInRequest);
            throw new NodeMgrException(ConstantCode.INVALID_CHECK_CODE);
        }
    }

    private void userCheck(HttpServletRequest req) {

        String token =getToken(req);

        if (token==null){
            return;
        }

        log.info("##########请求token {}",token);


        if (StringUtils.isBlank(token)) {

            return;
        }
        Integer bi=checkToken(token);
        req.setAttribute(token,(Integer)bi);

        log.info("##########添加缓存{} 用户 {}",token,bi);

    }


    public  Integer checkToken(String token){
        String userName=tokenService.getValueFromToken(token);

        BigInteger bi = new BigInteger(userName.getBytes());

        return bi.intValue();
    }





    public  String getToken(HttpServletRequest request) {

        String header = request.getHeader(NodeMgrTools.TOKEN_HEADER_NAME);
        if (StringUtils.isBlank(header)) {
            return null;
        }

        String token = StringUtils.removeStart(header, "Token").trim();
        if (StringUtils.isBlank(token)) {
            return null;
        }
        return token;
    }


}
