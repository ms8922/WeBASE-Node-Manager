package com.webank.webase.node.mgr.token;


import com.webank.webase.node.mgr.base.tools.NodeMgrTools;
import com.webank.webase.node.mgr.user.entity.TbUser;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;



@Log4j2
public class TokenUserCache {

    private static ConcurrentHashMap<String,Integer> Token2UserMap;

    private static ConcurrentHashMap<String,String> User2TokenMap;



    public static boolean isTokenExist(String token){
        if (StringUtils.isBlank(token)) {
            return false;
        }
        return Token2UserMap.containsKey(token);
    }

    public static void addUserToken(Integer userid,String token){
        if (StringUtils.isBlank(token)||userid==null) {
            return;
        }
        if (User2TokenMap.containsKey(userid)){
            String oldToken=User2TokenMap.get(userid);
            if (!token.equals(oldToken)){
                //token更新
                Token2UserMap.remove(oldToken);
            }else{
                return;
            }
        }
        User2TokenMap.put(userid+"",token);
        Token2UserMap.put(token,userid);
    }


    public static Integer getUserId(){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest req = attributes.getRequest();

        String token =NodeMgrTools.getToken(req);
       Object userid= req.getAttribute(token);


        log.info("##########获取缓存{} 用户 {}",token,userid);
        return  (Integer)userid;

    }




    public static Integer getUserIdByToken(String token){
        if (StringUtils.isBlank(token)) {
            return null;
        }
        return Token2UserMap.get(token);
    }


    public static TbUser checkToken(String token){
        TbUser user=new TbUser();
        user.setUserId(111111);
        return user;
    }

}
