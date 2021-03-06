package com.gofetch.entities;

import java.io.Serializable;
import java.lang.Integer;
import java.util.Date;

import javax.persistence.*;

import org.hibernate.validator.NotNull;

import com.gofetch.seomoz.Constants;

/**
 * Entity implementation class for Entity: Links
 *
 */
@Entity
@Table(name="links")
public class Link implements Serializable {

	//TODO: finish the JPA annotation of this entity.....
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer links_id;
	private static final long serialVersionUID = 1L;

	//TODO: tried changing the link class to include two urls, but with JPA issues, when trying a join, I had to revert back to making a
	// 	series of select calls, rather than 1 join - to get the links' target and source ids -> addresses for displaying a link with addresses
	//		rather than ids to the user...
	
	//NEW:
//	@OneToOne (fetch=FetchType.LAZY)
//	@PrimaryKeyJoinColumn(name="url_id")
//	URL targetURL;
//	
//	//NEW:
//	@OneToOne (fetch=FetchType.LAZY)
//	@PrimaryKeyJoinColumn(name="url_id")
//	URL sourceURL;
	
	 //OLD: 
	 @NotNull private Integer target_id;
	 //OLD: 
	 @NotNull private Integer source_id;

	@NotNull private String anchor_text;

	@Temporal(TemporalType.DATE)
	@NotNull private Date date_detected;

	@Temporal(TemporalType.DATE)
	private Date date_expired;
	
	//replace the above field with new field below:
	private Integer final_target_url_id;
	
	private String user_category;
	
	private String user_assigned_to;
	
	private String user_campaign;
	
	private Integer users_user_id; // points to a client's id. so can assign this link to a client.
	//private Integer client_category_users_user_id; // same as above - but pointed to thru the client_category table... hence the seemingly duplication...
	private Integer client_category_id; 
	
	private Integer link_building_activity_id;
	
	private Integer data_entered_by; // 0 = sourced from SeoMoz, 1 = user entered, 2 = ?? majestic ??
	
 	
	
//	public Integer getClient_category_users_user_id() {
//		return client_category_users_user_id;
//	}
//
//
//	public void setClient_category_users_user_id(
//			Integer client_category_users_user_id) {
//		this.client_category_users_user_id = client_category_users_user_id;
//	}


	public Integer getLink_building_activity_id() {
		return link_building_activity_id;
	}


	public void setLink_building_activity_id(Integer link_building_activity_id) {
		this.link_building_activity_id = link_building_activity_id;
	}


	public Integer getClient_category_id() {
		return client_category_id;
	}


	public void setClient_category_id(Integer client_category_id) {
		this.client_category_id = client_category_id;
	}


	public Integer getData_entered_by() {
		return data_entered_by;
	}


	public void setData_entered_by(Integer data_entered_by) {
		this.data_entered_by = data_entered_by;
	}


	public String getUser_category() {
		return user_category;
	}


	public void setUser_category(String user_category) {
		this.user_category = user_category;
	}


	public String getUser_assigned_to() {
		return user_assigned_to;
	}


	public void setUser_assigned_to(String user_assigned_to) {
		this.user_assigned_to = user_assigned_to;
	}


	public String getUser_campaign() {
		return user_campaign;
	}


	public void setUser_campaign(String user_campaign) {
		this.user_campaign = user_campaign;
	}




	public Integer getUsers_user_id() {
		return users_user_id;
	}


	public void setUsers_user_id(Integer users_user_id) {
		this.users_user_id = users_user_id;
	}


	//TODO: why is there here???  - there's no superclass??? // serializable...??
	public Link() {
		super();
	}   

	
	///////
	// new code to replace above:

	public Integer getFinal_target_url_id() {
		return final_target_url_id;
	}


	public void setFinal_target_url_id(Integer final_target_url_id) {
		this.final_target_url_id = final_target_url_id;
	}

	//
	/////////////
	
	public String getAnchor_text() {
		return anchor_text;
	}


	public void setAnchor_text(String anchor_text) {

		if(anchor_text.length() > Constants.MAX_ANCHOR_TEXT_LENGTH)
			this.anchor_text = anchor_text.substring(0, Constants.MAX_ANCHOR_TEXT_LENGTH);
		else
			this.anchor_text = anchor_text;
	}

	///////////////////
	// OLD:
	
	public Integer getTarget_id() {
		return target_id;
	}

	public void setTarget_id(Integer target_id) {
		this.target_id = target_id;
	}

	public Integer getSource_id() {
		return source_id;
	}

	public void setSource_id(Integer source_id) {
		this.source_id = source_id;
	}
	////////////////////////
	
	///////////////////////
	// New:
//	public Integer getTarget_id() {
//		return targetURL.getId();
//	}
//
//	public void setTarget_id(Integer target_id) {
//		targetURL.setId(target_id);
//	}
//
//	public Integer getSource_id() {
//		return sourceURL.getId();
//	}
//
//	public void setSource_id(Integer source_id) {
//		sourceURL.setId(source_id);
//	}
	
	//
	//////////////////////


	public Date getDate_detected() {
		return date_detected;
	}


	public void setDate_detected(Date date_detected) {
		this.date_detected = date_detected;
	}


	public Date getDate_expired() {
		return date_expired;
	}


	public void setDate_expired(Date date_expired) {
		this.date_expired = date_expired;
	}


	public Integer getLinks_id() {
		return links_id;
	}


	public void setLinks_id(Integer links_id) {
		this.links_id = links_id;
	}



}
