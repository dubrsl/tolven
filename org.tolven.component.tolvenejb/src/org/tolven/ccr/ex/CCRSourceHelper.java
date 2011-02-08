package org.tolven.ccr.ex;

import org.tolven.ccr.ActorReferenceType;
import org.tolven.ccr.ActorType;
import org.tolven.ccr.SourceType;
import org.tolven.ccr.CCRCodedDataObjectType;
import org.tolven.ccr.ContinuityOfCareRecord;
import org.tolven.ccr.ContinuityOfCareRecord.From;

import java.util.List;

public class CCRSourceHelper {
    /**
     * Helper Method to get the source
     */
    
    public static String getActorFromSource(CCRCodedDataObjectType sourceType, ContinuityOfCareRecord.Actors actors) {
    	return getActorFromSourceList(sourceType.getSource(), actors);
    }
    
    public static String getActorName(ActorReferenceType actorRef, ContinuityOfCareRecord.Actors actors) {
		PersonNameTypeEx sourceEx = null;
		String sourceName = null;
		
		if (actorRef!=null && actorRef.getActorID()!=null) {
			for (ActorType actor : actors.getActor()) {
				if (actorRef.getActorID().equals(actor.getActorObjectID())) { 
					if (actor.getPerson()!=null) {
					   sourceEx = (PersonNameTypeEx)(actor.getPerson().getName()).getCurrentName();
					   sourceName = getPersonDetails(sourceEx);
					   break;
					}
					if (actor.getOrganization()!=null) {
					   sourceName = actor.getOrganization().getName();
					   break;
					}
					if (actor.getInformationSystem()!=null) {
					   sourceName = actor.getInformationSystem().getName();
					   break;
					}					
				}
			}
		}
		if (sourceName==null) {
			sourceName = actorRef.getActorID();
		}
		return sourceName;
    }
    
    public static String getFromName( ContinuityOfCareRecord ccr) {
    	From from = ccr.getFrom();
    	if (from==null) return null;
    	List<ActorReferenceType> actorRefList = from.getActorLink();
    	if (actorRefList==null) return null;
    	if (actorRefList.size()<1) return null;
    	ActorReferenceType actorRef = actorRefList.get(0);
		return getActorName( actorRef, ccr.getActors());
    }
    
    public static String getActorFromSourceList(List<SourceType> sourceType, ContinuityOfCareRecord.Actors actors) {    	
		String sourceName = null;
    	try {
    		ActorReferenceType actorRef = sourceType.get(0).getActor().get(0);
	    	sourceName = getActorName( actorRef, actors);
        } catch(NullPointerException e) {
        	sourceName = null;
        }
        catch(ArrayIndexOutOfBoundsException e) {
        	sourceName = null;
        }
        catch(IndexOutOfBoundsException e) {
        	sourceName = null;
        }        
		return sourceName;
    }
    
    public static String getPersonDetails(PersonNameTypeEx sourceEx) {
    	StringBuffer sb = new StringBuffer(sourceEx.getFamilyString());
    	sb.append(",");
    	sb.append(sourceEx.getGivenString());
    	sb.append("-");
    	sb.append(sourceEx.getTitleString());
    	return sb.toString();    	 
    }    
}
