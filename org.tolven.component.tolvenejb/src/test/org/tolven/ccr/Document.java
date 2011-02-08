package test.org.tolven.ccr;

import junit.framework.TestCase;

public class Document extends TestCase {

	public void testGetBinding() {
//		DocCCR doc = new DocCCR();
//		doc.setBinding(Demog.createCCR());
//		TolvenLogger.info(doc.getContentString(), Document.class);
//		doc.resetBinding( );
//		Object obj = doc.getBinding();
//		ContinuityOfCareRecord ccr = (ContinuityOfCareRecord)obj;
//		TolvenLogger.info( ccr.getBody().getAlerts().getAlert().get(0).getDescription().getText(), Document.class );
//		TolvenLogger.info( ccr.getActors().getActor().get(0).getPerson().getName().getDisplayName(), Document.class );
	}

	public void testRepeatBinding() {
		for (int x = 0; x < 10;x++) {
			testGetBinding();
		}
	}

}
