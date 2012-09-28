package com.gofetch.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
//import javax.persistence.TemporalType;

public class LinkDBService{

	@PersistenceUnit(unitName="GoFetch")
	EntityManagerFactory emf;

	private static Logger log = Logger.getLogger(URLDBService.class.getName());

	public void createLink(Link link){

		log.info("Entering new link. Source: [" + link.getSource_id() + " Target: " + link.getTarget_id() + "]");

		emf = Persistence.createEntityManagerFactory("GoFetch");
		EntityManager mgr = emf.createEntityManager();

		try {

			mgr.getTransaction().begin();
			mgr.persist(link);
			mgr.getTransaction().commit();

		}catch(Exception e){
			String msg = "Exception thrown. " + "LinkDBService: createLink";
			//logger.logp(Level.SEVERE, "LinkDBService", "createLink",msg ,e);
			log.severe(msg + e.getMessage());
		}
		finally {
			mgr.close();
		}
	}
	
	/**
	 * 
	 * @param finalTargetURLAddress
	 * @return list of Links that all have their final_target_url as finalTargetURLAddress
	 */
	public List<Link> getLinksInTreePointingTo(String finalTargetURLAddress){
		
		log.info("Entering getSourceURLsIDsPointingTo [" + finalTargetURLAddress + "]");

		emf = Persistence.createEntityManagerFactory("GoFetch");
		EntityManager mgr = emf.createEntityManager();

		//Integer targetURLID = targetURL.getId();

		//List<Integer> backLinkIDs = new ArrayList<Integer>();
		List<Link> links = null;

		try {
			links =  mgr.createQuery("SELECT u FROM Link u WHERE u.final_target_url = :final_target_url")
					.setParameter("final_target_url",  finalTargetURLAddress)
					.getResultList();

		}catch(Exception e){
			String msg = "Exception thrown getting data for: " + finalTargetURLAddress + ". LinkDBService: getLinksInTreePointingTo";
			//logger.logp(Level.SEVERE, "LinkDBService", "getLinksInTreePointingTo",msg ,e);
			log.severe(msg + e.getMessage());
		} finally {
			mgr.close();
		}

		log.info("Exiting getSourceURLsIDsPointingTo [" + finalTargetURLAddress + "]"); 

		return links;
		
	}
	
	/**
	 * 
	 * @param targetURL 
	 * @return - list of ids of each url pointing to the target.
	 */
	public List<Integer> getLinkIDsPointingTo(URL targetURL){

		log.info("Entering getLinkIDsPointingTo [" + targetURL.getUrl_address() + "]");

		emf = Persistence.createEntityManagerFactory("GoFetch");
		EntityManager mgr = emf.createEntityManager();

		Integer targetURLID = targetURL.getId();

		List<Integer> linkIDs = new ArrayList<Integer>();
		List<Link> links = null;

		try {
			links = mgr.createQuery("SELECT u FROM Link u WHERE u.target_id = :targetURLID")
					.setParameter("targetURLID",  targetURLID)
					.getResultList();

		}catch(Exception e){
			String msg = "Exception thrown getting data for: " + targetURL.getUrl_address() + "LinkDBService: getLinkIDsPointingTo \n";
			//logger.logp(Level.SEVERE, "LinkDBService", "getLinkIDsPointingTo",msg ,e);
			log.severe(msg + e.getMessage());
		} finally {
			mgr.close();
		}

		// add all links' source id to list of ids
		for(Link currentLink : links){
			linkIDs.add(currentLink.getLinks_id());
		}

		log.info("Exiting getLinkIDsPointingTo [" + targetURL + "]"); 

		return linkIDs;
	}

	/**
	 * 
	 * @param targetURL 
	 * @return - list of ids of each url pointing to the target.
	 */
	public List<Integer> getSourceURLsIDsPointingTo(URL targetURL){

		log.info("Entering getSourceURLsIDsPointingTo [" + targetURL.getUrl_address() + "]");

		emf = Persistence.createEntityManagerFactory("GoFetch");
		EntityManager mgr = emf.createEntityManager();

		Integer targetURLID = targetURL.getId();

		List<Integer> backLinkIDs = new ArrayList<Integer>();
		List<Link> links;

		try {
			links = mgr.createQuery("SELECT u FROM Link u WHERE u.target_id = :targetURLID")
					.setParameter("targetURLID",  targetURLID)
					.getResultList();

		} finally {
			mgr.close();
		}

		// add all links' source id to list of ids
		for(Link currentLink : links){
			backLinkIDs.add(currentLink.getSource_id());
		}

		log.info("Exiting getSourceURLsIDsPointingTo [" + targetURL + "]"); 

		return backLinkIDs;
	}

	/**
	 * returns the number of times the url with the urlID param occurs in the links table. Either as a source or target.
	 * - useful for checking when you want to delete the url, if it occurs more than once, then the url is linked to
	 * 	multiple times, so avoid deleting...
	 * @param urlID
	 * @return
	 */
	public Integer noOfTimesURLIsLinkedTo(Integer urlID){

		log.info("Entering noOfTimesURLIsLinkedTo: " + urlID);

		Integer result =0;

		emf = Persistence.createEntityManagerFactory("GoFetch");
		EntityManager mgr = emf.createEntityManager();

		List<Link> links;

		try {
			links = mgr.createQuery("SELECT u FROM Link u WHERE u.target_id = :urlID OR u.source_id = :urlID")
					.setParameter("urlID",  urlID)
					.getResultList();

		} finally {
			mgr.close();
		}

		result = links.size();

		log.info("Exiting noOfTimesURLIsLinkedTo(...)");

		return result;

	}

	/**
	 * returns the number of times the url with the urlID param occurs in the links table as a source.
	 * @param urlID
	 * @return
	 */
	public Integer noOfTimesURLIsSourceURL(Integer urlID){

		log.info("Entering noOfTimesURLIsSourceURL: " + urlID);

		Integer result =0;

		emf = Persistence.createEntityManagerFactory("GoFetch");
		EntityManager mgr = emf.createEntityManager();

		List<Link> links;

		try {
			links = mgr.createQuery("SELECT u FROM Link u WHERE u.source_id = :urlID")
					.setParameter("urlID",  urlID)
					.getResultList();

		} finally {
			mgr.close();
		}

		result = links.size();

		log.info("Exiting noOfTimesURLIsSourceURL(...)");

		return result;

	}


	public void deleteLinksInTreeWithRoot(String finalTargetURL){

		log.info("Entering deleteLinksInTreeWithRoot. Deleting all links pointing to " + finalTargetURL);

		//Link link;

		emf = Persistence.createEntityManagerFactory("GoFetch");
		EntityManager mgr = emf.createEntityManager();

		try{

			mgr.getTransaction().begin();
			
			mgr.createQuery("DELETE FROM Link u WHERE u.final_target_url = :final_target_url")
			.setParameter("final_target_url",  finalTargetURL)
			.executeUpdate();
			
			mgr.getTransaction().commit();


		}catch(Exception e){
			String msg = "Exception thrown deleting: " + finalTargetURL + "LinkDBService: deleteLinksInTreeWithRoot";
			//logger.logp(Level.SEVERE, "LinkDBService", "deleteLinksInTreeWithRoot",msg ,e);
			log.severe(msg + e.getMessage());

		} finally {
			mgr.close();
		}

		log.info("Exiting deleteLinksInTreeWithRoot.");

	}
	
	public void deleteLinksPointingTo(int targetURLID){

		log.info("Entering deleteLinksPointingTo. Deleting all links pointing to " + targetURLID);

		//Link link;

		emf = Persistence.createEntityManagerFactory("GoFetch");
		EntityManager mgr = emf.createEntityManager();

		try{

			mgr.getTransaction().begin();
			
			mgr.createQuery("DELETE FROM Link u WHERE u.target_id = :targetURLID")
			.setParameter("targetURLID",  targetURLID)
			.executeUpdate();
			
			mgr.getTransaction().commit();


		}catch(Exception e){
			String msg = "Exception thrown deleting: " + targetURLID + " LinkDBService + deleteLinksPointingTo";
			//logger.logp(Level.SEVERE, "LinkDBService", "deleteLinksPointingTo",msg ,e);
			log.severe(msg + e.getMessage());

		} finally {
			mgr.close();
		}

		log.info("Exiting deleteLinksPointingTo.");

	}
	
	
	public void deleteLinkByLinkID(Integer links_id){

		log.info("Entering deleteLinkByLinkID. Deleting " + links_id);

		//Link link;

		emf = Persistence.createEntityManagerFactory("GoFetch");
		EntityManager mgr = emf.createEntityManager();

			try{
				mgr.getTransaction().begin();
				mgr.createQuery("DELETE FROM Link u WHERE u.links_id = :links_id")
				.setParameter("links_id",  links_id)
				.executeUpdate();
				
				mgr.getTransaction().commit();

			}catch(Exception e){
				String msg = "Exception thrown deleting: " + links_id + ". LinkDBService: deleteLinkByLinkID";
				//logger.logp(Level.SEVERE, "LinkDBService", "deleteLinkByLinkID",msg ,e);
				log.severe(msg + e.getMessage());
				
			} finally {
				mgr.close();
			}

		log.info("Exiting deleteLinkByLinkID.");
	}


	public void deleteLinkBySourceID(Integer source_id){

		log.info("Entering deleteLinkBySourceID. Deleting " + source_id);

		//Link link;

		emf = Persistence.createEntityManagerFactory("GoFetch");
		EntityManager mgr = emf.createEntityManager();


			try{
				mgr.getTransaction().begin();
				mgr.createQuery("DELETE FROM Link u WHERE u.source_id = :source_id")
				.setParameter("source_id",  source_id)
				.executeUpdate();
				
				mgr.getTransaction().commit();

			}catch(Exception e){
				String msg = "Exception thrown deleting: " + source_id + ". LinkDBService + deleteLinkBySourceID";
				//logger.logp(Level.SEVERE, "LinkDBService", "deleteLinkBySourceID",msg ,e);
				log.severe(msg + e.getMessage());

			} finally {
				mgr.close();
			}


		log.info("Exiting deleteLinkBySourceID.");
	}

	/**
	 * returns list of all links that this link (id) occurs in...
	 * @param id
	 * @return
	 */
	public List<Link> getAllLinks(Integer id){
		
		log.info("Entering getAllLinks");

		emf = Persistence.createEntityManagerFactory("GoFetch");
		EntityManager mgr = emf.createEntityManager();

		List<Link> links;

		try {
			links = mgr.createQuery("SELECT u FROM Link u WHERE u.target_id = :urlID OR u.source_id = :urlID")
					.setParameter("urlID",  id)
					.getResultList();

		} finally {
			mgr.close();
		}

		log.info("Exiting getAllLinks"); 
		
		return links;
	}

	public Link getLink(Integer id){
		log.info("Entering getLink[" + id + "]");
		Link result = null;

		emf = Persistence.createEntityManagerFactory("GoFetch");
		EntityManager mgr = emf.createEntityManager();

		try {
			result = mgr.find(Link.class, id);
		}catch(Exception e){
			String msg = "Exception thrown...";
			log.logp(Level.SEVERE, "LinkDBService", "getLink",msg ,e);

		} finally {
			mgr.close();
		}
		if (result == null) {
			log.warning("No link returned");
		}
		log.info("Exiting getLink");
		return result;
	}

	public List<Integer> getURLIDsPointingTo(URL url) {

		log.info("Entering getURLIDsPointingTo");

		emf = Persistence.createEntityManagerFactory("GoFetch");
		EntityManager mgr = emf.createEntityManager();

		List<Integer> result;

		try {
			result = (List<Integer>)mgr.createQuery("SELECT u.source_id FROM Link u WHERE u.target_id = :urlID")
					.setParameter("urlID",  url.getId())
					.getResultList();

		} finally {
			mgr.close();
		}


		log.info("Exiting getURLIDsPointingTo");

		return result;

	}
}
