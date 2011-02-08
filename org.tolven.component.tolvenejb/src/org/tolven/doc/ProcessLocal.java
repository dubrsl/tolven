package org.tolven.doc;

import java.util.Date;

import org.tolven.doc.bean.TolvenMessage;

public interface ProcessLocal {

    /**
     * Synchronously process a TolvenMessage.
     * @param payload
     * @param mediaType
     * @param xmlns
     * @param accountId
     * @param userId
     * @param now
     * @return The created message - note that the message header will contain the resulting DocumentId of the payload.
     * @throws Exception
     */
    public long processMessage(byte[] payload, String mediaType, String xmlns, long accountId, long userId, Date now);

    /**
     * Synchronously dispatch a TolvenMessage to the appropriate message processor(s).
     * @param tm A TolvenMessage containing the payload. May also contain attachments 
     * @param now The time considered transaction now
     * @throws Exception
     */
    public void dispatchToMessageProcessors(TolvenMessage tm, Date now);

}
