package org.notificationengine.authentication.activedirectory;


import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.notificationengine.authentication.Authenticator;
import org.notificationengine.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.security.ldap.LdapUtils;

import javax.naming.directory.DirContext;
import java.util.List;

public class ActiveDirectoryAuthenticator implements Authenticator{

    private static Logger LOGGER = Logger.getLogger(ActiveDirectoryAuthenticator.class);

    private ContextSource contextSource;

    private LdapTemplate ldapTemplate;

    public ActiveDirectoryAuthenticator(ContextSource contextSource, LdapTemplate ldapTemplate) {
        this.contextSource = contextSource;
        this.ldapTemplate = ldapTemplate;
    }

    @Override
    public Boolean isAdmin(User user) {

        String username = user.getUsername();
        String password = user.getPassword();

        return this.isAdmin(username, password);
    }

    @Override
    public Boolean isAdmin(String username, String encryptedPassword) {

        try {
            String userDn = this.getDnForUser(username);

            return this.authenticate(userDn, encryptedPassword);

        } catch (Exception e) {

            LOGGER.error(ExceptionUtils.getFullStackTrace(e));

            return Boolean.FALSE;
        }
    }

    private Boolean authenticate(String userDn, String credentials) {

        DirContext ctx = null;

        try {

            ctx = contextSource.getContext(userDn, credentials);

            return Boolean.TRUE;

        } catch (Exception e) {

            // Context creation failed - authentication did not succeed
            LOGGER.error(ExceptionUtils.getFullStackTrace(e));
            return Boolean.FALSE;

        } finally {

            // It is imperative that the created DirContext instance is always closed
            LdapUtils.closeContext(ctx);
        }
    }

    private String getDnForUser(String uid) {

        Filter f = new EqualsFilter("uid", uid);
        List result = ldapTemplate.search(DistinguishedName.EMPTY_PATH, f.toString(),
                new AbstractContextMapper() {
                    protected Object doMapFromContext(DirContextOperations ctx) {
                        return ctx.getNameInNamespace();
                    }
                });

        if(result.size() != 1) {
            throw new RuntimeException("User not found or not unique");
        }

        return (String)result.get(0);
    }

    public ContextSource getContextSource() {
        return contextSource;
    }

    public void setContextSource(ContextSource contextSource) {
        this.contextSource = contextSource;
    }

    public LdapTemplate getLdapTemplate() {
        return ldapTemplate;
    }

    public void setLdapTemplate(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }
}
