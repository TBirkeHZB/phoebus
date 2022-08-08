/**
 * Copyright (C) 2018 European Spallation Source ERIC.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.phoebus.applications.saveandrestore.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
/**
 * Class representing a node in a tree structure maintained by the save-and-restore service. Node types are
 * defined in enum {@link NodeType}.
 *
 * @author georgweiss
 *
 */
public class Node implements Comparable<Node>, Serializable {

	private int id;
	private String uniqueId = UUID.randomUUID().toString();
	private String name;
	private Date created;
	private Date lastModified;
	private NodeType nodeType = NodeType.FOLDER;
	private String userName;
	private Map<String, String> properties;
	private List<Tag> tags;

	/**
	 * Do not change!!!
	 */
	public static final int ROOT_NODE_ID = 0;

	public static final String ROOT_FOLDER_UNIQUE_ID = "44bef5de-e8e6-4014-af37-b8f6c8a939a2";

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public NodeType getNodeType() {
		return nodeType;
	}

	public void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public void putProperty(String key, String value) {
		if(properties == null) {
			properties = new HashMap<>();
		}
		properties.put(key,  value);
	}

	public void removeProperty(String key) {
		if(properties != null) {
			properties.remove(key);
		}
	}

	public String getProperty(String key) {
		if(properties == null) {
			return null;
		}
		return properties.get(key);
	}

	public void addTag(Tag tag) {
		if (tags == null) {
			tags = new ArrayList<>();
		}

		if (tags.stream().noneMatch(item -> item.getName().equals(tag.getName()))) {
			tags.add(tag);
		}
	}

	public void removeTag(Tag tag) {
		if (tags != null) {
			tags.stream()
					.filter(item -> item.getName().equals(tag.getName()))
					.findFirst()
					.ifPresent(item -> tags.remove(item));
		}
	}

	@Override
	public boolean equals(Object other) {
		if(other == null) {
			return false;
		}
		if(other instanceof Node) {
			Node otherNode = (Node)other;
			return nodeType.equals(otherNode.getNodeType()) &&
					uniqueId.equals(otherNode.getUniqueId());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(nodeType, uniqueId);
	}

	/**
	 * Implements strategy where folders are sorted before configurations (save sets), and
	 * equal node types are sorted alphabetically.
	 * @param other The tree item to compare to
	 * @return -1 if this item is a folder and the other item is a save set,
	 * 1 if vice versa, and result of name comparison if node types are equal.
	 */
	@Override
	public int compareTo(Node other) {

		if(nodeType.equals(NodeType.FOLDER) && other.getNodeType().equals(NodeType.CONFIGURATION)){
			return -1;
		}
		else if(getNodeType().equals(NodeType.CONFIGURATION) && other.getNodeType().equals(NodeType.FOLDER)){
			return 1;
		}
		else{
			return getName().compareTo(other.getName());
		}
	}

	/**
	 * Clones a {@link Node} object. The created date and last modified date is set to the current time stamp.
	 * The unique id is not copied, as it should not be.
	 * @param nodeToClone source {@link Node}
	 * @return The cloned {@link Node}
	 */
	public static Node clone(Node nodeToClone){
		Node clonedNode = new Node();
		clonedNode.setNodeType(nodeToClone.getNodeType());
		clonedNode.setUserName(nodeToClone.getUserName());
		Date now = new Date();
		clonedNode.setLastModified(now);
		clonedNode.setCreated(now);
		clonedNode.setName(nodeToClone.getName());
		clonedNode.setProperties(nodeToClone.getProperties());
		clonedNode.setTags(nodeToClone.getTags());

		return clonedNode;
	}

	public static Builder builder(){
		return new Builder();
	}

	public static class Builder{

		private Node node;

		private Builder(){
			node = new Node();
		}

		public Builder id(int id){
			node.setId(id);
			return this;
		}

		public Builder uniqueId(String uniqueId){
			node.setUniqueId(uniqueId);
			return this;
		}

		public Builder name(String name){
			node.setName(name);
			return this;
		}

		public Builder userName(String userName){
			node.setUserName(userName);
			return this;
		}

		public Builder created(Date created){
			node.setCreated(created);
			return this;
		}

		public Builder lastModified(Date lastModified){
			node.setLastModified(lastModified);
			return this;
		}

		public Builder nodeType(NodeType nodeType){
			node.setNodeType(nodeType);
			return this;
		}

		public Builder properties(Map<String, String> properties){
			node.setProperties(properties);
			return this;
		}

		public Builder tags(List<Tag> tags){
			node.setTags(tags);
			return this;
		}

		public Node build(){
			return node;
		}
	}
}
