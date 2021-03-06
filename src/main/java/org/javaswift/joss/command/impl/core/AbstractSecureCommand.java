package org.javaswift.joss.command.impl.core;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.javaswift.joss.exception.UnauthorizedException;
import org.javaswift.joss.headers.ConnectionKeepAlive;
import org.javaswift.joss.headers.Token;
import org.javaswift.joss.model.Access;
import org.javaswift.joss.model.Account;
import org.javaswift.joss.model.ObjectStoreEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSecureCommand<M extends HttpRequestBase, N> extends AbstractCommand<M, N> {

    private Account account;

    public static final Logger LOG = LoggerFactory.getLogger(AbstractSecureCommand.class);

    public AbstractSecureCommand(Account account, HttpClient httpClient, String url, String token) {
        super(httpClient, url);
        this.account = account;
        setToken(token);
        setConnectionKeepAlive();
    }

    public static String getURL(Access access, ObjectStoreEntity entity, boolean usePrivateURL) {
        if (usePrivateURL) {
            return access.getInternalURL() + entity.getPath();
        }
        return access.getPublicURL() + entity.getPath();
    }

    @Override
    public N call() {
        account.increaseCallCounter();
        try {
            if (account.isAllowReauthenticate()) {
                super.setAllowErrorLog(false);
            }
            return super.call();
        } catch (UnauthorizedException err) {
            if (account.isAllowReauthenticate()) {
                LOG.info("Reauthenticate");
                Access access = account.authenticate();
                setToken(access.getToken());
                super.setAllowErrorLog(true);
                return super.call();
            }
            throw err;
        }
    }

    protected void setConnectionKeepAlive() {
        setHeader(new ConnectionKeepAlive());
    }

    private void setToken(String token) {
        setHeader(new Token(token));
    }

}
