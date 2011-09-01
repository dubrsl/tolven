package org.tolven.session;

import org.apache.log4j.Logger;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;

public class DefaultTolvenSessionFactory implements TolvenSessionFactory {

    private SessionIdGenerator sessionIdGenerator = new JavaUuidSessionIdGenerator();

    private Logger logger = Logger.getLogger(DefaultTolvenSessionFactory.class);

    @Override
    public Session createSession(SessionContext initData) {
        DefaultTolvenSession session = new DefaultTolvenSession();
        if (initData != null) {
            //TODO Is init.Data.getSessionId() ever already set?
            String sessionId = (String) getSessionIdGenerator().generateId(session);
            if (logger.isDebugEnabled()) {
                logger.debug("Generated session Id: " + sessionId);
            }
            session.setId(sessionId);
            session.setHost(initData.getHost());
        }
        return session;
    }

    @Override
    public Session createSession(TolvenSessionState tolvenSessionState) {
        DefaultTolvenSession session = new DefaultTolvenSession();
        session.setId(tolvenSessionState.getId());
        session.setHost(tolvenSessionState.getHost());
        session.setLastAccessTime(tolvenSessionState.getLastAccessTime());
        session.setStartTimestamp(tolvenSessionState.getStartTimestamp());
        session.setTimeout(tolvenSessionState.getTimeout());
        return session;
    }

    private SessionIdGenerator getSessionIdGenerator() {
        if (sessionIdGenerator == null) {
            sessionIdGenerator = new JavaUuidSessionIdGenerator();
        }
        return sessionIdGenerator;
    }

}
