package com.supinfo.transcode.entity;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@Entity
@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
public class File implements Serializable {
		
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@Column(nullable=false)
	private String name;
	
	@Column(nullable=false)
	private String convertValue;
	
	@ManyToOne
	@JoinColumn(name="fk_user")
	private User user;
	
	@JsonIgnore
	@OneToMany(fetch=FetchType.LAZY, mappedBy="file")
	private Collection<File_part> parts;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="fk_queue")
	private Queue queue;
	
	@Transient
	String download_link;
	
	public String getDownload_link() {
		return download_link;
	}

	public void setDownload_link(String download_link) {
		this.download_link = download_link;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User getUser_file() {
		return user;
	}

	public void setUser_file(User user_file) {
		this.user = user_file;
	}

	public Collection<File_part> getParts() {
		return parts;
	}

	public void setParts(Collection<File_part> parts) {
		this.parts = parts;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Queue getQueue() {
		return queue;
	}

	public void setQueue(Queue queue) {
		this.queue = queue;
	}

	public String getConvertValue() {
		return convertValue;
	}

	public void setConvertValue(String convertValue) {
		this.convertValue = convertValue;
	}
}
