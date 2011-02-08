package test.org.tolven.ccr;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.tolven.ccr.ActorType;
import org.tolven.ccr.AlertType;
import org.tolven.ccr.CodedDescriptionType;
import org.tolven.ccr.ContinuityOfCareRecord;
import org.tolven.ccr.ObjectFactory;
import org.tolven.logging.TolvenLogger;

public class Demog extends TestCase {
	private Logger logger = Logger.getLogger(this.getClass());

	public JAXBContext setupJAXBContext() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance( "org.tolven.ccr", Demog.class.getClassLoader() );
        return jc;
	}

	public String marshalCCR(ContinuityOfCareRecord ccr ) throws JAXBException, IOException {
        JAXBContext jc = setupJAXBContext();
        Marshaller m = jc.createMarshaller();
        m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
//        m.setProperty( Marshaller.JAXB_FRAGMENT, Boolean.TRUE );
//        m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://xxx" );
        StringWriter result = new StringWriter( 1000 ); 
        m.marshal( ccr, result );
        result.close();
        return result.toString();
	}

	public void testCCR() throws JAXBException, IOException {
		TolvenLogger.defaultInitialize();
		String rslt = marshalCCR( createCCR() );
		logger.info( rslt );
	}

	public static ContinuityOfCareRecord createCCR() {
		ObjectFactory factory = new ObjectFactory();
		ContinuityOfCareRecord ccr = factory.createContinuityOfCareRecord();
		ContinuityOfCareRecord.Body body = factory.createContinuityOfCareRecordBody(); 
		ccr.setActors(factory.createContinuityOfCareRecordActors());
		body.setAlerts(factory.createContinuityOfCareRecordBodyAlerts());
		ActorType actor1 = factory.createActorType();
		actor1.setPerson(createActorBob());
		actor1.setActorObjectID("102");
		ccr.getActors().getActor().add(actor1);
		ContinuityOfCareRecord.Patient patient = factory.createContinuityOfCareRecordPatient();
		patient.setActorID("102");
		ccr.getPatient().add(patient);
		AlertType alert1 = factory.createAlertType();
		CodedDescriptionType alert1Descr = new CodedDescriptionType();
		alert1Descr.setText("Itchy fungus");
		alert1.setDescription(alert1Descr );
		body.getAlerts().getAlert().add(alert1);
		ccr.setBody(body);
		return ccr;
	}

	public static ActorType.Person createActorBob() {
		ActorType.Person person = new ActorType.Person();
		ActorType.Person.Name name = new ActorType.Person.Name();
		name.setDisplayName( "Bob");
		person.setName(name);
		return person;
		
//		TolvenLogger.info("My name is " + person.getName().getDisplayName(), , Demog.class);
	}

	
}
