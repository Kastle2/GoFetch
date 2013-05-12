package com.gofetch.beans;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.TreeNode;

import com.gofetch.models.URLNodeImpl;

@ManagedBean(name = "urlTreeBean")
@ViewScoped
public class URLTreeBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// from:
	// http://javaevangelist.blogspot.co.uk/2012/07/custom-primefaces-tree-example.html

	private TreeNode root;
	//private TreeNode[] selectedNodes;

	public void clearTree() {

		root.getChildren().clear();

	}

	// default constructor
	public URLTreeBean() {

		root = new URLNodeImpl("Root", null);

	}

	public URLTreeBean(Object data, TreeNode parent, String title) {

		root = new URLNodeImpl(title, null);

	}

	/**
	 * This method returns the tree model based on the root node.
	 * 
	 * @return root node.
	 */
	public TreeNode getModel() {
		return root;
	}

	/**
	 * {@inheritDoc }
	 * 
	 * Adds a {@link javax.faces.application.FacesMessage} with event data to
	 * the {@link javax.faces.context.FacesContext}.
	 */
	public void onNodeSelect(NodeSelectEvent event) {
		// System.out.println("NodeSelectEvent Fired");
		// FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
		// "Selected", event.getTreeNode().getData().toString());
		// FacesContext.getCurrentInstance().addMessage(event.getComponent().getId(),
		// msg);

	}

	/**
	 * {@inheritDoc}
	 * 
	 * Adds a {@link javax.faces.application.FacesMessage} with event data to
	 * the {@link javax.faces.context.FacesContext}.
	 */
	public void onNodeExpand(NodeExpandEvent event) {

		// System.out.println("NodeExpandEvent Fired");
		// FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
		// "Expanded", event.getTreeNode().getData().toString());
		// FacesContext.getCurrentInstance().addMessage(event.getComponent().getId(),
		// msg);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Adds a {@link javax.faces.application.FacesMessage} with event data to
	 * the {@link javax.faces.context.FacesContext}.
	 */
	public void onNodeCollapse(NodeCollapseEvent event) {
		// System.out.println("NodeCollapseEvent Fired");
		// FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
		// "Collapsed", event.getTreeNode().getData().toString());
		// FacesContext.getCurrentInstance().addMessage(event.getComponent().getId(),
		// msg);
	}

//	public TreeNode[] getSelectedNodes() {
//		return selectedNodes;
//	}
//
//	public void setSelectedNodes(TreeNode[] selectedNodes) {
//		this.selectedNodes = selectedNodes;
//	}

}