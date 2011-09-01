package org.tolven.client.example;

import java.security.Principal;
import java.util.HashSet;

import org.tolven.logging.TolvenLogger;

public class MyPrincipal implements Principal {
	String name;
	MyPrincipal( String name ) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public String toString( ) {
		return "Principal: " + name;
	}

	public int hashCode( ) {
		if (name==null) return 0;
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if ( !(obj instanceof MyPrincipal)) return false;
		return name.equals(((MyPrincipal)obj).getName());
	}
	
	public static void main(String[] args) {
		TolvenLogger.initialize();
		
		HashSet<MyPrincipal> hash = new HashSet<MyPrincipal>();
		hash.add(new MyPrincipal( "Name 1"));
		hash.add(new MyPrincipal( "Name 2"));
		hash.add(new MyPrincipal( "Name 3"));
		hash.add(new MyPrincipal( "Name 3"));
		TolvenLogger.info( hash, MyPrincipal.class );
	}
}
