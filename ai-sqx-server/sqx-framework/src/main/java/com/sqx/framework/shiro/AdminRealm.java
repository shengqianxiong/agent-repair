package com.sqx.framework.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.Arrays;
import java.util.HashSet;

/**
 * 管理端权限 Realm（开发环境默认放行全部权限）
 */
public class AdminRealm extends AuthorizingRealm {

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setStringPermissions(new HashSet<>(Arrays.asList(
                "bar:product:list",
                "bar:product:detail",
                "bar:product:save",
                "bar:product:update",
                "bar:product:delete",
                "bar:category:list",
                "bar:category:save",
                "bar:dashboard:stats",
                "bar:order:list",
                "bar:order:detail",
                "bar:order:update",
                "bar:order:refund",
                "bar:booking:list",
                "bar:booking:detail",
                "bar:booking:confirm",
                "bar:booking:cancel",
                "bar:booking:assign",
                "bar:table:list",
                "bar:table:save",
                "bar:table:update",
                "bar:activity:list",
                "bar:activity:save",
                "bar:activity:update",
                "bar:activity:delete"
        )));
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
            throws AuthenticationException {
        return new SimpleAuthenticationInfo("admin", token.getCredentials(), getName());
    }
}
