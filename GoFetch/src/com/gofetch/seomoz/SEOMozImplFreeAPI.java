package com.gofetch.seomoz;

import java.util.ArrayList;
import java.util.List;
//import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.reflect.Type;
//import java.net.SocketTimeoutException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

//import com.gofetch.entities.URLDBService;
import com.gofetch.utils.TextUtil;

/**
 * Implementation of the SEOMoz API Wrapper - Free API version.
 * 
 * Limitations to the free API:
 * 1) will only return top 1000 links to target - doesnt look like you can use offset to move onto next set
 * 2) of those 1000, you will only receive 25 links from each unique domain.
 * 3) Requests to the server must have a delay of 5-6 seconds bewtween them.
 * 		- worth looking into the cost of this, and how paying for the non-Free API might be cheaper than paying for GAE host time
 * 
 * @author alandonohoe
 *
 */
public class SEOMozImplFreeAPI implements SEOMoz {

	private static Logger log = Logger.getLogger(SEOMozImplFreeAPI.class.getName());

	private Authenticator authentSEOMoz;

	// for info on below variables: http://apiwiki.seomoz.org/w/page/13991141/Links%20API
	private String scope; 		//ex: PAGE_TO_PAGE - determines the scope of the Target link, as well as the Source results. 
	private String filters; 	//ex: LINKS_FILTER_FOLLOW - filters the links returned to only include links of the specified type
	private String sort; 		//ex: LINKS_SORT_PAGE_AUTHORITY - determines the sorting of the links,
	private long sourceCols;	//ex: URLMETRICS_COL_URL- specifies data about the source of the link is included
	private long targetCols; 	//ex: URLMETRICS_COL_URL- specifies data about the target of the link is included
	private long linkCols;		//ex: LINKS_COL_URL 	- specifies data about the link itself 
	private int offSet;			// used to scroll through results of SEOMoz server.

	public SEOMozImplFreeAPI(){

		scope = LinksConstants.LINKS_SCOPE_PAGE_TO_PAGE;
		filters = LinksConstants.LINKS_FILTER_FOLLOW + LinksConstants.LINKS_FILTER_301 + LinksConstants.LINKS_FILTER_EXTERNAL; 
		sort = LinksConstants.LINKS_SORT_PAGE_AUTHORITY; //The Sources with the highest Page Authority are returned first.
		sourceCols = URLMetricsConstants.URLMETRICS_COL_URL;
		targetCols = URLMetricsConstants.URLMETRICS_COL_URL;
		linkCols   = LinksConstants.LINKS_COL_URL;
		offSet = 0;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getFilters() {
		return filters;
	}

	public void setFilters(String filters) {
		this.filters = filters;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public long getSourceCols() {
		return sourceCols;
	}

	public void setSourceCols(long sourceCols) {
		this.sourceCols = sourceCols;
	}

	public long getTargetCols() {
		return targetCols;
	}

	public void setTargetCols(long targetCols) {
		this.targetCols = targetCols;
	}

	public long getLinkCols() {
		return linkCols;
	}

	public void setLinkCols(long linkCols) {
		this.linkCols = linkCols;
	}


	// end of getters and setters
	/////////////

	/**
	 * Returns a list of List of URLPlusDataPoints containing all the backlinks to the target urlTarget.
	 * Free API Limitations - will only return the first 1000 links.
	 * 
	 * Of these links, will only return max 25 from each unique domain.
	 * 
	 * Requires a 6 second delay between each request to SEOMoz server.....
	 * 
	 * Quite commonly, the SEOMoz server times out - in this case an exception is thrown, and the target url is written to log files as an error
	 * and method returns an empty list
	 * @throws Exception 
	 */
	public List<URLPlusDataPoints> getLinks(String urlTarget) throws Exception {
		

		boolean calledOnce = false; // used in timer delay to hit SEOMoz server multiple times....
		boolean moreLinksLeft = true; // flag set to false when SEOMoz not sending back any more links...
		String response = null;
		String urlMinusHttp = urlTarget.replaceFirst("http://", ""); // SEOMoz server must have http:// removed from url

		ArrayList<URLPlusDataPoints> tempLinks     = new ArrayList<URLPlusDataPoints>();  
		ArrayList<URLPlusDataPoints> allLinksDAPA  = new ArrayList<URLPlusDataPoints>();

		Type linksListType = new TypeToken<ArrayList<URLPlusDataPoints>>() {}.getType(); // get type for GSON
		Gson gson = new Gson();

		Authenticate();

		LinksService linksService = new LinksService();
		linksService.setAuthenticator(authentSEOMoz);

		offSet = 0;

		while (moreLinksLeft) { // this is redundant in free api impl - just set to false below.... will be used in paid versions
			moreLinksLeft = false;
			if (calledOnce) /// no need to call the first time.. 
			{
				try {
					Thread.sleep(Constants.FREE_API_SEOMOZ_SERVER_DELAY); 
				} catch (InterruptedException ex) {

					// from: http://stackoverflow.com/questions/9139128/a-sleeping-thread-is-getting-interrupted-causing-loss-of-connection-to-db
					Thread.currentThread().interrupt(); // restore interrupted status

					String msg = "Exception in getLinks. SEOMozImplFreeAPI: getLinks";
					//logger.logp(Level.SEVERE, "SEOMozImplFreeAPI", "getLinks",msg ,ex);
					//ex.printStackTrace();
					log.warning(msg + ex.getMessage());
				}

			}
			calledOnce = true; // now there will be a delay on next iteration.... 

			try{ 
				response = linksService.getLinks(urlMinusHttp, scope, filters, sort, sourceCols, targetCols, linkCols, offSet, Constants.FREE_API_LINK_QUERY_MAX);
			}catch(Exception e){ // most likely time out from SEOMoz server....

				//String msg = "Exception in getLinks getting data for: " + urlTarget + ". SEOMozImplFreeAPI: getLinks";
				//logger.logp(Level.SEVERE, "SEOMozImplFreeAPI", "getLinks",msg ,e);
				//e.printStackTrace();	
				//log.severe(msg + e.getMessage());

				//return allLinksDAPA; // return null;??
				
				//replaced all the above with throwing the exception up the chain so we can deal with accordingly
				
				throw(e);

			}

			if (response.length() > 2) { // check for "[]" = empty response

				tempLinks = gson.fromJson(response, linksListType);

				allLinksDAPA.addAll(tempLinks); //append templinks to allLinksDAPA
				offSet = Constants.FREE_API_LINK_QUERY_MAX; //Old version: offSet = allLinksDAPA.size(); // move along to query next links...  and go back for more links

			} else{ // no_more_links 

				return allLinksDAPA;  
			}

		}
		return allLinksDAPA;
	}


	/**
	 * retrieves Domain and Page authority for "url" - returns a URLPlusDataPoints object with DA and PA, domain name and doc title.
	 * @throws Exception 
	 */
	public URLPlusDataPoints getURLMetricsData(URLPlusDataPoints url) throws Exception {

		URLMetricsService urlMetricsService = new URLMetricsService();
		Gson gson = new Gson();
		String response = null, paString, daString;
		UrlResponse res;
		String urlMinusHttp = url.getBackLinkURL().replaceFirst("http://", ""); // SEOMoz server must have http:// removed from url


		Authenticate();

		urlMetricsService.setAuthenticator(authentSEOMoz);

		try{
			// see: http://apiwiki.seomoz.org/url-metrics - for bitflag info...
			//long cols = 103079215104L = pa and da
			// 			  103616086049L = pa, da, no of external links, HTTP Status Code, Title,
			response = urlMetricsService.getUrlMetrics(urlMinusHttp, 103616086049L); 

			res = gson.fromJson(response, UrlResponse.class);

			// remove the digits after the decimal point
			paString = TextUtil.returnInteger(res.getUpa());
			daString = TextUtil.returnInteger(res.getPda());	

		} catch(Exception e){

			String msg = "Exception in SEOMozImplFreeAPI. Getting (PA/DA) data for: " + url.getBackLinkURL() + ". SEOMozImplFreeAPI: getURLMetricsData";
			//logger.logp(Level.SEVERE, "urlMetricsService", "getURLMetricsData",msg ,e);
			//e.printStackTrace();	
			log.warning(msg + e.getMessage());

			//assume there's a time out error from SEOMoz server - forget about getting any data back and just return
			//return url;
			//26-23-13 AD:
			throw (e);

		}


		url.setBackLinkPA(paString);
		url.setBackLinkDA(daString);
		url.setDomainName(res.getUpl()); //TODO: check that this is returning correct domain when dealing with wordpress - Gary wants to keep each wordpress unique.
		url.setDocTitle(res.getUt());

		return url;
	}

	/**
	 * retrieves Domain and Page authority for every url in "urls" - returns a URLPlusDataPoints List with DAs and PAs filled.
	 */
	public List<URLPlusDataPoints> getAuthorityData(List<URLPlusDataPoints> urls) {

		boolean calledOnce = false;

		for(URLPlusDataPoints currentURL : urls){

			if (calledOnce) /// no need to call the first time.. 
			{
				try {
					Thread.sleep(Constants.FREE_API_SEOMOZ_SERVER_DELAY); 
				} catch (InterruptedException ex) {

					// from: http://stackoverflow.com/questions/9139128/a-sleeping-thread-is-getting-interrupted-causing-loss-of-connection-to-db
					Thread.currentThread().interrupt(); // restore interrupted status

					String msg = "Exception in getAuthorityData. SEOMozImplFreeAPI: getAuthorityData.\n";
					//logger.logp(Level.SEVERE, "SEOMozImplFreeAPI", "getAuthorityData",msg ,ex);
					//ex.printStackTrace();
					log.severe(msg + ex.getMessage());
				}

			}
			calledOnce = true; // now there will be a delay on next iteration.... 

			//TODO: finish implementing this......
			// get the authority data....
			// getURLMetricsData(currentURL);
		}

		return urls;

	}

	private void Authenticate(){

		authentSEOMoz = new Authenticator(); 
		authentSEOMoz.setAccessID(Constants.ACCESS_ID);
		authentSEOMoz.setSecretKey(Constants.SECRET_KEY);

	}

	@Override
	public List<URLPlusDataPoints> getLinkFromUniqueDomains(String urlTarget)
			throws Exception {
		
		/*
		 * 	scope = LinksConstants.LINKS_SCOPE_PAGE_TO_PAGE;
		filters = LinksConstants.LINKS_FILTER_FOLLOW + LinksConstants.LINKS_FILTER_301 + LinksConstants.LINKS_FILTER_EXTERNAL; 
		sort = LinksConstants.LINKS_SORT_PAGE_AUTHORITY; //The Sources with the highest Page Authority are returned first.
		sourceCols = URLMetricsConstants.URLMETRICS_COL_URL;
		targetCols = URLMetricsConstants.URLMETRICS_COL_URL;
		linkCols   = LinksConstants.LINKS_COL_URL;
		offSet = 0;
		 */
		//simply change teh scope arg and call getLinks....
		scope = LinksConstants.LINKS_SCOPE_DOMAIN_TO_PAGE;
		sort = LinksConstants.LINKS_SORT_DOMAIN_AUTHORITY;
		
		return getLinks(urlTarget); 

	}

}
