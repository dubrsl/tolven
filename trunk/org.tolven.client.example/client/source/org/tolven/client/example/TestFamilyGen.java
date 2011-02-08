package org.tolven.client.example;

import java.util.Date;

import javax.naming.InitialContext;

import org.tolven.gen.FamilyGeneratorRemote;
import org.tolven.gen.entity.FamilyUnit;
import org.tolven.logging.TolvenLogger;

public class TestFamilyGen {

	public static void main(String[] args) throws Exception {
		TolvenLogger.initialize();

		InitialContext ctx = new InitialContext();
		// Bind to the remote session bean interface in the running server via JNDI/RMI
		FamilyGeneratorRemote familyGen = (FamilyGeneratorRemote) ctx.lookup("tolven/FamilyGenerator/remote");
		Date now = new Date();
		for (int x = 0; x < 100; x++) {
			FamilyUnit fu = familyGen.generateFamily( null, now );
			TolvenLogger.info(fu, TestFamilyGen.class);
		}
	}
}
