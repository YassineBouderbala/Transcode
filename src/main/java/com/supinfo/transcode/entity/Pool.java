package com.supinfo.transcode.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonManagedReference;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@Entity
@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
public class Pool implements Serializable {
	
	public Pool(){
		this.queues = new ArrayList<Queue>();
		this.workers = new ArrayList<Worker>();
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@Column(nullable=false)
	private String name;
	
	@Column(nullable=false)
	private String path;
	
	private boolean status;
	
	@JsonIgnore
	@OneToMany(fetch=FetchType.LAZY, mappedBy="pool")
	private Collection<Queue> queues;
	
	@JsonManagedReference
	@OneToMany(fetch=FetchType.LAZY, mappedBy="pool", cascade = CascadeType.REMOVE, orphanRemoval = true )
	private Collection<Worker> workers;
	
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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Collection<Queue> getQueues() {
		return queues;
	}

	public void setQueues(Collection<Queue> queues) {
		this.queues = queues;
	}

	public Collection<Worker> getWorkers() {
		return workers;
	}

	public void setWorkers(Collection<Worker> workers) {
		this.workers = workers;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}
}
