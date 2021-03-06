package com.gofetch.beans;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import com.gofetch.GoFetchConstants;
import com.gofetch.entities.URLDBService;
import com.gofetch.entities.URL;
import com.gofetch.utils.DateUtil;
import com.gofetch.utils.TextUtil;

@ManagedBean
@RequestScoped
public class GoFetchRequestBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(GoFetchRequestBean.class
			.getName());
	private String url = "http://";
	private String user_id = "@propellernet.co.uk";
	private boolean socialData= true;
	private boolean backLinkData= true;
	private int noOfLayers = 1;
	private int[] rippleList = { 1, 2, 3 };

	private List<String> errorReport = new ArrayList<String>();
	private List<String> successReport = new ArrayList<String>();
	private List<String> urlAddressesInDB = new ArrayList<String>();
	private List<String> results = new ArrayList<String>(); //used in autocomplete

	private URLDBService urlDB = null;
	private List<URL> urlsinDB = null;



	public GoFetchRequestBean() {


		// could have all this here... need to find a way to load the url addresses into memory, seamlessly...
		//	maybe as app loads, then keep them in a session scoped bean...???
		//	can i have a managed bean that JUST is deals with the url addresses - make it application scope?

		//urlAddressesInDB = new ArrayList<String>();

		//		urlDB = new URLDBService();
		//		urlsinDB = urlDB.getURLs();
		//
		//		for (URL url : urlsinDB) {
		//			urlAddressesInDB.add(url.getUrl_address());
		//		}

	}



	public List<String> getErrorReport() {
		return errorReport;
	}

	public List<String> getUrlAddressesInDB() {
		return urlAddressesInDB;
	}

	public void setUrlAddressesInDB(List<String> urlAddressesInDB) {
		this.urlAddressesInDB = urlAddressesInDB;
	}

	public List<String> getResults() {
		return results;
	}

	public void setResults(List<String> results) {
		this.results = results;
	}

	public URLDBService getUrlDB() {
		return urlDB;
	}

	public void setUrlDB(URLDBService urlDB) {
		this.urlDB = urlDB;
	}

	public List<URL> getUrlsinDB() {
		return urlsinDB;
	}

	public void setUrlsinDB(List<URL> urlsinDB) {
		this.urlsinDB = urlsinDB;
	}

	public void setErrorReport(List<String> errorReport) {
		this.errorReport = errorReport;
	}

	public List<String> getSuccessReport() {
		return successReport;
	}

	public void setSuccessReport(List<String> successReport) {
		this.successReport = successReport;
	}

	public int[] getRippleList() {
		return rippleList;
	}

	public void setRippleList(int[] rippleList) {
		this.rippleList = rippleList;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}


	public int getNoOfLayers() {
		return noOfLayers;
	}

	public void setNoOfLayers(int noOfLayers) {
		this.noOfLayers = noOfLayers;
	}

	public boolean isSocialData() {
		return socialData;
	}



	public void setSocialData(boolean socialData) {
		this.socialData = socialData;
	}



	public boolean isBackLinkData() {
		return backLinkData;
	}



	public void setBackLinkData(boolean backLinkData) {
		this.backLinkData = backLinkData;
	}



	//TODO: change this to something faster like: http://igoro.com/archive/efficient-auto-complete-with-a-ternary-search-tree/
	public List<String> completeURLs(String query) {
		
		// new code:
		urlDB = new URLDBService();
		
		results = urlDB.getURLAddressesStartingWith(query, 5);
		
		return results;
		
		
//		OLD code:
//		//if(urlAddressesInDB.isEmpty()){
//			urlDB = new URLDBService();
//			urlsinDB = urlDB.getURLs();
//
//			for (URL url : urlsinDB) {
//				urlAddressesInDB.add(url.getUrl_address());
//			}
//		//}
//
//		///////////////
//		// above replaced here:
//		//		getURLAddresses() - not working... JQPL - dont know if you can select a single field....
//		//		if( null == urlAddressesInDB){
//		//			urlDB = new URLDBService();
//		//			urlAddressesInDB = urlDB.getURLAddresses();
//		//		}
//
//		for (String possibleURL : urlAddressesInDB) {
//
//			if (possibleURL.startsWith(query)) {
//				results.add(possibleURL);
//			}
//		}
//
//		return results;
	}



	// action controller method:
	public String addNewURLs() {

		errorReport = new ArrayList<String>();
		successReport = new ArrayList<String>();
//		urlAddressesInDB = new ArrayList<String>();
		results =  new ArrayList<String>();


		List<String> urlList = new ArrayList<String>();
		List<String> urlListPassed = new ArrayList<String>();
		List<URL> urlsinDB = null;
		String tempString = null;

		// first split the user entered string into single urls - space
		// separated.
		String[] urls = url.split(" ");

		int noOfURLs = urls.length;

		urlDB = new URLDBService();

		//TODO: replace this loop with a query that just returns all url's addresses in DB

//		//if(null == urlAddressesInDB){
//		// old code:
//			urlsinDB = urlDB.getURLs();
//
//			for (URL url : urlsinDB) {
//				urlAddressesInDB.add(url.getUrl_address());
//			}
//		//}

		// new code:
//		urlAddressesInDB = urlDB.getURLAddresses();
		////////////

		///////////////
		// replaced here:
		//TODO: not working... JQPL - dont know if you can select a single field....
		//urlAddressesInDB = urlDB.getURLAddresses();
		//
		// ////// getURLAddresses

		// //////////
		// check all entries length and "http://" prefix
		for (int i = 0; i < noOfURLs; i++) {

			// remove whitespace
			urls[i] = urls[i].trim();

			if (urls[i].length() < GoFetchConstants.MIN_VALID_URL_LENGTH) {

				tempString = urls[i]
						+ " - is too short to be added to GoFetch's database.";
				errorReport.add(tempString);

			} else if (!urls[i].startsWith("http")) {

				tempString = urls[i]
						+ " -  \"http://\" has been added to the beginning of URL and has been be added to GoFetch.";
				successReport.add(tempString);

				String temp = "http://" + urls[i];

				urls[i] = temp;

				urlList.add(urls[i]);
			}

			else { // its passed both basic checks - then can add to DB

				urlList.add(urls[i]);
			}
		}
		//
		// //////////////



		// /////////////////////
		// run through passed urls and check that url not already in DB....
		for (int x = 0; x < urlList.size(); x++) {
			
			//remove https: to avoid duplication:
			String urlAddress = urlList.get(x);
			urlAddress = TextUtil.replaceHttpsWithHttp(urlAddress);

			// add trailing slash to all urls so they can be checked against normalised urls in DB
			// old code:
			//if ((urlAddressesInDB.contains(urlList.get(x)))||(urlAddressesInDB.contains(TextUtil.addSlashToEndOfString(urlList.get(x))))) { // check that url not already in DB....
			//new code:
			if ((urlDB.urlInDB(urlAddress))||(urlDB.urlInDB(TextUtil.addSlashToEndOfString(urlAddress)))) { // check that url not already in DB....

				tempString = urlAddress
						+ " -  is already entered in GoFetch and is being monitored.";
				errorReport.add(tempString);

			} else { // add to final list to add:

				urlListPassed.add(urlAddress);

			}
		}

		//
		// //////////////

		// ////////
		// add the url to the DB

		for (String newURL : urlListPassed) {

			URL url = new URL();
			
			Date todaysDate = DateUtil.getTodaysDate();

			url.setDate(todaysDate);
			url.setSocial_data_date(todaysDate);
			url.setSocial_data_freq(GoFetchConstants.DAILY_FREQ);
			url.setUrl_address(newURL);
			url.setUser_id(user_id);

			url.setGet_backlinks(backLinkData);
			url.setGet_social_data(socialData);
			//url.setNo_of_layers(noOfLayers);
			url.setBacklinks_got(false); // turns to true, when ProcessNewTargets have got this url's backlinks
			url.setData_entered_by(GoFetchConstants.URL_ENTERED_BY_USER); 
			
			
			try {
				urlDB.createURL(url);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.severe(e.getMessage());
			}

			// add to report:

			successReport.add(newURL);

		}

		//
		// /////////

		return ("submitURLReport");


	}



}