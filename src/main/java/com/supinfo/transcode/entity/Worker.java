package com.supinfo.transcode.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@Entity
@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
public class Worker implements Serializable {
	
	public Worker(){
		this.pool = new Pool();
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
		
	@Column(nullable=false, unique = true)
	private String ip;	
	
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name="fk_pool")
	@NotNull
	private Pool pool;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Pool getPool() {
		return pool;
	}

	public void setPool(Pool pool) {
		this.pool = pool;
	}

}
