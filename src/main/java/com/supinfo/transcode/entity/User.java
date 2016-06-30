package com.supinfo.transcode.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonManagedReference;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.stereotype.Component;

import com.supinfo.transcode.interfaces.global.job.IGlobalUserJob;
import com.supinfo.transcode.security.Crypter;
import com.supinfo.transcode.utils.Response;

@Entity
@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
public class User implements Serializable {
		
	public User(){
		roles = new ArrayList<Role>();
	}
	
	public User(String username, String first_name, String last_name, String email, String password,
			String password_confirm) {
		super();
		this.username = username;
		this.first_name = first_name;
		this.last_name = last_name;
		this.email = email;
		this.password = password;
		this.password_confirm = password_confirm;
		roles = new ArrayList<Role>();
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@Column(nullable=false, unique = true)
	private String username;
	
	@Column(nullable=false)
	private String first_name;
	
	@Column(nullable=false)
	private String last_name;
	
	@Column(nullable=false,unique = true)
	private String email;
	
	@Column(nullable=false)
	private String password;
	
	@Transient
	private String password_confirm;
	
	@Transient
	@JsonIgnore
	private boolean success;
	
	@JsonManagedReference
	@OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy="user")
	@Column(nullable=false)
	private Collection<Role> roles;
	
	@JsonIgnore
	@OneToMany(fetch=FetchType.LAZY, mappedBy="user")
	private Collection<Streaming> streams;
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="user")
	private Collection<File> files;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword_confirm() {
		return password_confirm;
	}

	public void setPassword_confirm(String password_confirm) {
		this.password_confirm = password_confirm;
	}

	public Collection<Role> getRoles() {
		return roles;
	}

	public void setRoles(Collection<Role> roles) {
		this.roles = roles;
	}

	public Collection<File> getFiles() {
		return files;
	}

	public void setFiles(Collection<File> files) {
		this.files = files;
	}

	public Collection<Streaming> getStreams() {
		return streams;
	}

	public void setStreams(Collection<Streaming> streams) {
		this.streams = streams;
	}

}
