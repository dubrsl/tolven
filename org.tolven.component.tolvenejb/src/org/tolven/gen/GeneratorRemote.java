package org.tolven.gen;


/**
 * Remote services to generate patients using the data generator.
 * @author John Churin
 *
 */
public interface GeneratorRemote {

    public byte[] generateCCRXML(  int startYear );

}
