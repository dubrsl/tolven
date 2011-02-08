package org.tolven.app;

import java.io.Serializable;
/**
 * An instance of this message will cause any outstanding trim headers to be activated, that is, attached to appropriate menus as specified in the trims.
 * This is done in batches, typically 500 trims at a time. Completing one batch issues this message again causing another batch to be updated.
 * @author John Churin
 *
 */
public class ActivateNewTrimHeadersMessage implements Serializable {

}
