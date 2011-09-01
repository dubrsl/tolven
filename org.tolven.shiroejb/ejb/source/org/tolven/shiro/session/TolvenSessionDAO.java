package org.tolven.shiro.session;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;
import org.tolven.exeption.TolvenSessionNotFoundException;
import org.tolven.session.DefaultTolvenSessionFactory;
import org.tolven.session.SecretKeyThreadLocal;
import org.tolven.session.TolvenSessionFactory;
import org.tolven.session.TolvenSessionState;

public class TolvenSessionDAO extends CachingSessionDAO {

    private AesCipherService aes;

    private Logger logger = Logger.getLogger(TolvenSessionDAO.class);
    @EJB
    private TolvenSessionLocal tolvenSessionBean;

    private TolvenSessionFactory tolvenSessionFactory;

    protected TolvenSessionState convertSession(Session session) {
        TolvenSessionState ts = getTolvenSessionBean().findSession(session.getId());
        if (ts == null) {
            ts = getTolvenSessionBean().createSession();
            ts.setId((String) session.getId());
            //TODO Shiro code indicates that they too must remove direct references to DEFAULT_GLOBAL_SESSION_TIMEOUT
            ts.setTimeout(DefaultSessionManager.DEFAULT_GLOBAL_SESSION_TIMEOUT);
            ts.setStartTimestamp(new Date());
            ts.setLastAccessTime(ts.getStartTimestamp());
        }
        ts.setHost(session.getHost());
        ts.setLastAccessTime(session.getLastAccessTime());
        ts.setStartTimestamp(session.getStartTimestamp());
        ts.setTimeout(session.getTimeout());
        encryptAttributes(session, ts);
        return ts;
    }

    protected byte[] decodeDecrypt(String encodedEncyptedData) {
        byte[] encryptedData = Base64.decode(encodedEncyptedData);
        byte[] data = getAesCipherService().decrypt(encryptedData, getSecretKey()).getBytes();
        return data;
    }

    protected void decryptAttributes(TolvenSessionState ts, Session session) {
        if (logger.isDebugEnabled()) {
            logger.debug("Decrypting attributes for session:" + ts.getId());
        }
        String encodedEncryptedAttributes = ts.getEncryptedAttributes();
        byte[] serializedAttributes = decodeDecrypt(encodedEncryptedAttributes);
        Map<Object, Object> attributes = deserializeAttributes(serializedAttributes);
        for (Entry<Object, Object> entry : attributes.entrySet()) {
            session.setAttribute(entry.getKey(), entry.getValue());
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Decrypted attributes for session:" + ts.getId());
        }
    }

    protected Map<Object, Object> deserializeAttributes(byte[] serializedAttributes) {
        ByteArrayInputStream bais = new ByteArrayInputStream(serializedAttributes);
        try {
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (Map<Object, Object>) ois.readObject();
        } catch (Exception ex) {
            throw new RuntimeException("Could not deserialize attributes", ex);
        }
    }

    @Override
    protected Serializable doCreate(Session session) {
        TolvenSessionState ts = convertSession(session);
        encryptAttributes(session, ts);
        if (logger.isDebugEnabled()) {
            logger.debug("Persisting session:" + session.getId());
        }
        Serializable sessionId = getTolvenSessionBean().persistSession(ts);
        if (logger.isDebugEnabled()) {
            logger.debug("Persisted session:" + sessionId);
        }
        return sessionId;
    }

    @Override
    protected void doDelete(Session session) {
        if (logger.isDebugEnabled()) {
            logger.debug("Deleting session: " + session.getId());
        }
        getTolvenSessionBean().deleteSession((String) session.getId());
        if (logger.isDebugEnabled()) {
            logger.debug("Deleted session: " + session.getId());
        }
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        Session s = getCachedSession(sessionId);
        if (s == null) {
            TolvenSessionState ts = getTolvenSessionBean().findSession(sessionId);
            if (ts == null) {
                throw new TolvenSessionNotFoundException("Could not find session: " + (String) sessionId);
            }
            s = getTolvenSessionFactory().createSession(ts);
            decryptAttributes(ts, s);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Read from cache session: " + sessionId);
            }
        }
        return s;
    }

    @Override
    protected void doUpdate(Session session) {
        if (logger.isDebugEnabled()) {
            StringBuffer buff = new StringBuffer();
            buff.append("[");
            Iterator<Object> it = session.getAttributeKeys().iterator();
            while (it.hasNext()) {
                buff.append(it.next());
                if (it.hasNext()) {
                    buff.append(",");
                }
            }
            buff.append("]");
            logger.debug("Session attributes with keys: " + buff.toString());
            System.out.println("Session attributes with keys: " + buff.toString());
        }
        TolvenSessionState ts = convertSession(session);
        if (logger.isDebugEnabled()) {
            logger.debug("Updating session: " + session.getId());
        }
        getTolvenSessionBean().updateSession(ts);
        if (logger.isDebugEnabled()) {
            logger.debug("Updated session: " + session.getId());
        }
    }

    protected void encryptAttributes(Session session, TolvenSessionState ts) {
        if (logger.isDebugEnabled()) {
            logger.debug("Encrypting attributes for session:" + ts.getId());
        }
        byte[] serializedAttributes = serializeAttributes(session);
        String encodedEncryptedAttributes = encryptEncode(serializedAttributes);
        ts.setEncryptedAttributes(encodedEncryptedAttributes);
        if (logger.isDebugEnabled()) {
            logger.debug("Encrypted attributes for session:" + ts.getId());
        }
    }

    protected String encryptEncode(byte[] data) {
        byte[] encryptedAttributes = getAesCipherService().encrypt(data, getSecretKey()).getBytes();
        String encodedEncryptedAttributes = Base64.encodeToString(encryptedAttributes);
        return encodedEncryptedAttributes;
    }

    private AesCipherService getAesCipherService() {
        if (aes == null) {
            aes = new AesCipherService();
        }
        return aes;
    }

    protected byte[] getSecretKey() {
        byte[] secretKey = SecretKeyThreadLocal.get();
        if (secretKey == null) {
            throw new RuntimeException("Could not find secret key in ThreadLocal");
        }
        return secretKey;
    }

    private TolvenSessionLocal getTolvenSessionBean() {
        if (tolvenSessionBean == null) {
            String jndiName = null;
            try {
                InitialContext ctx = new InitialContext();
                jndiName = "java:app/shiroEJB/TolvenSessionBean!org.tolven.shiro.session.TolvenSessionLocal";
                if (logger.isDebugEnabled()) {
                    logger.debug("JNDI lookup: " + jndiName);
                }
                tolvenSessionBean = (TolvenSessionLocal) ctx.lookup(jndiName);
                if (logger.isDebugEnabled()) {
                    logger.debug("Found: " + jndiName);
                }
            } catch (NamingException e) {
                throw new RuntimeException("Could not look up " + jndiName, e);
            }
        }
        return tolvenSessionBean;
    }

    public TolvenSessionFactory getTolvenSessionFactory() {
        if (tolvenSessionFactory == null) {
            tolvenSessionFactory = new DefaultTolvenSessionFactory();
        }
        return tolvenSessionFactory;
    }

    protected byte[] serializeAttributes(Session session) {
        if (logger.isDebugEnabled()) {
            logger.debug("Serializing attributes for session:" + session.getId());
        }
        Map<Object, Object> map = new HashMap<Object, Object>();
        for (Object obj : session.getAttributeKeys()) {
            map.put(obj, session.getAttribute(obj));
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(map);
            byte[] serializedBytes = baos.toByteArray();
            if (logger.isDebugEnabled()) {
                logger.debug("Serialized attributes for session:" + session.getId());
            }
            return serializedBytes;
        } catch (IOException ex) {
            throw new RuntimeException("Could not serialize attributes", ex);
        }
    }

    public void setTolvenSessionFactory(TolvenSessionFactory tolvenSessionFactory) {
        this.tolvenSessionFactory = tolvenSessionFactory;
    }

}
