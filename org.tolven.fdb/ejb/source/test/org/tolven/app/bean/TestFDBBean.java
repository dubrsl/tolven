package test.org.tolven.app.bean;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import junit.framework.TestCase;

import org.tolven.app.AllergyVO;
import org.tolven.app.DrugVO;
import org.tolven.app.MedicationDrug;
import org.tolven.app.bean.FDBBean;
import org.tolven.app.bean.FDBConnectionsVO;
import org.tolven.fdb.entity.FdbDispensable;
import org.tolven.fdb.entity.FdbRoute;
import org.tolven.fdb.entity.FdbSig;
import org.tolven.fdb.entity.ex.FdbDispensableEx;

public class TestFDBBean extends TestCase {
	private FDBConnectionsVO fdbConVo;
	private FDBBean fdbBean;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		fdbConVo = new FDBConnectionsVO();
		fdbConVo.setDriverClass("org.postgresql.Driver");
		fdbConVo.setdBURL("jdbc:postgresql://localhost:5432/postgres");
		fdbConVo.setUsername("postgres");
		fdbConVo.setPassword("1234qwer$");
		fdbConVo.setPooling(true);
		fdbConVo.setPoolSize(10);
		fdbConVo.setLoadLimit(5);
		fdbBean = new FDBBean(fdbConVo);
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("pu");
        EntityManager em = factory.createEntityManager();
        fdbBean.setEm(em);  
  }
	public void testFindFdbDispensable(){
		int i = 449969;
		FdbDispensable drug= fdbBean.findFdbDispensable(i);
		assertNotNull(drug);
	}
	
	public void testFindDrugAllergy(){
		AllergyVO allergy= fdbBean.findDrugAllergy("Vistaril");
		assertNotNull(allergy);
	}

	public void testRetrieveDrugInformation(){
		List<FdbDispensable> drugs = fdbBean.retrieveDrugInformation("Ibuprofen", 0);
		System.out.println("testRetrieveDrugInformation: Drugs found "+drugs.size());
		for(FdbDispensable drug: drugs){
			if(!drug.getDescdisplay().contains("Ibuprofen"))
				fail("Wrong drug found");
		}
	}
	public void testGetDrugInformationForRxChange(){
		try{
			MedicationDrug drug = fdbBean.getDrugInformationForRxChange(464131, "0.2 Micron Filter Attachment");
			System.out.println("getDrugInformationForRxChange(464131, \"0.2 Micron Filter Attachment\") :"+drug.toString());
			assertNotNull("Drug not found with details id:464131 and name:'0.2 Micron Filter Attachment'",drug);		
		}catch(Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}	
	}
	public void testFetchDrugAllergies(){
		List<AllergyVO> list = fdbBean.fetchDrugAllergies(1);
		assertEquals(14, list.size());
	}
	public void testFetchAllRoutes(){
		List<FdbRoute> routes = fdbBean.fetchAllRoutes();
		assertNotNull(routes);
	}
	public void testRetrieveSIG(){
		List<FdbSig> sigs = fdbBean.retrieveSIG();
		assertNotNull(sigs);
	}
	
	public void testGetSelectedDrugInformation(){
		FdbDispensableEx dispensableEx = null;
		try {
			dispensableEx = fdbBean.getSelectedDrugInformation(150010);
			assertEquals((Integer)150010, dispensableEx.getDispensable().getMedid());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail("Error in testGetSelectedDrugInformation");
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			fail("Error in testGetSelectedDrugInformation");
		}
		assertNotNull(dispensableEx);
		assertNotNull(dispensableEx.getRoute());
		assertNotNull(dispensableEx.getDoseform());		
	}
	public void testRetrieveOTCDrugInformation(){
		List<DrugVO> drugs= fdbBean.retrieveOTCDrugInformation(0,null);
		assertNotNull(drugs);
	}
	
	public void testRetrieveCodeFromDrugName(){
		Integer code = fdbBean.retrieveCodeFromDrugName("Erythromycin (Bulk) Powder");
		assertNotNull(code);		
	}
	public void testFindDrugCount(){
		Long count = fdbBean.findDrugCount("Erythromycin (Bulk) Powder");
		assertNotNull(count );
		System.out.println("Count ="+count );
	}
	public void testFindDrugAllergyCount(){
		Long count = fdbBean.findDrugAllergyCount("Vis");
		assertNotNull(count );
		System.out.println("Count ="+count );
	}
	public void testCheckForDrugValidity(){
		String valid = fdbBean.checkForDrugValidity("Erythromycin (Bulk) Powder");
		assertNotNull(valid);
		System.out.println("valid ="+valid);
	}
	
	
	public void testSearchDrugExhaustive(){
		try {
			fdbBean.searchDrugExhaustive((long)431801, "12 Hour Relief 6 mg-120 mg Tab", "", "20041021000000");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void testRetrieveNDCInformation(){
		try {
			Map<String,String> ndcInfo = fdbBean.retrieveNDCInformation(431073);
			assertNotNull(ndcInfo.get("ndcCode"));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
