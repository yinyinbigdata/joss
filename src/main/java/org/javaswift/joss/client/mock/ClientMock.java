package org.javaswift.joss.client.mock;

import org.javaswift.joss.command.mock.factory.AuthenticationCommandFactoryMock;
import org.javaswift.joss.command.shared.factory.AuthenticationCommandFactory;
import org.javaswift.joss.model.Client;
import org.javaswift.joss.swift.MockUserStore;
import org.javaswift.joss.swift.Swift;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientMock implements Client<AccountMock> {

    public static final Logger LOG = LoggerFactory.getLogger(ClientMock.class);

    private MockUserStore users = new MockUserStore();

    private boolean allowEveryone = true;

    private boolean allowObjectDeleter = true;

    private String onFileObjectStore = null;

    private long millisDelay = 0;

    public AccountMock authenticate(String tenant, String username, String password, String authUrl) {
        return authenticate(tenant, username, password, authUrl, null);
    }

    public AccountMock authenticate(String tenant, String username, String password, String authUrl, String preferredRegion) {
        LOG.info("JOSS / MOCK mode");
        LOG.info("JOSS / Creating mock account instance");
        LOG.info("JOSS / * Check credentials: "+!allowEveryone);
        LOG.info("JOSS / * Allow objectdeleter: "+allowObjectDeleter);
        LOG.info("JOSS / * On File ObjectStore: "+onFileObjectStore);
        LOG.info("JOSS / * Use milliseconds delay: "+ millisDelay);
        Swift swift = new Swift()
                .setAllowObjectDeleter(allowObjectDeleter)
                .setOnFileObjectStore(onFileObjectStore)
                .setUserStore(users)
                .setMillisDelay(millisDelay);
        if (!allowEveryone) {
            AuthenticationCommandFactory authenticationCommandFactory = new AuthenticationCommandFactoryMock(swift);
            LOG.info("JOSS / Attempting authentication with tenant: " + tenant + ", username: " + username + ", Auth URL: " + authUrl);
            authenticationCommandFactory.createAuthenticationCommand(null, null, tenant, username, password).call();
        }
        return new AccountMock(swift);
    }

    public ClientMock setAllowObjectDeleter(boolean allowObjectDeleter) {
        this.allowObjectDeleter = allowObjectDeleter;
        return this;
    }

    public ClientMock setAllowEveryone(boolean allowEveryone) {
        this.allowEveryone = allowEveryone;
        return this;
    }

    public ClientMock setOnFileObjectStore(String onFileObjectStore) {
        this.onFileObjectStore = onFileObjectStore;
        return this;
    }

    public ClientMock setMillisDelay(long millisDelay) {
        this.millisDelay = millisDelay;
        return this;
    }

    public MockUserStore getUsers() { return this.users; }
    public void setUsers(MockUserStore users) { this.users = users; }
}
