/*
 * Copyright (C) 2009 Tolven Inc

 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;  
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 *
 * Contact: info@tolvenhealth.com 
 *
 * @author <your name>
 * @version $Id: PackageCompiler.java,v 1.2 2010/03/17 20:10:23 jchurin Exp $
 */  

package org.tolven.rules;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.compiler.PackageBuilderErrors;
import org.drools.rule.Package;
import org.drools.rule.builder.dialect.java.JavaDialectConfiguration;

/**
 * This class contains functions for compiling and serializing rules.
 * @author John Churin
 *
 */
public class PackageCompiler {
	
	/**
	 * Compile a rule package and store the compiled result into the package entity.
	 * No initial package, such as containing factTemplates, is defined.
	 * @param packageBody The Rule Package body - the rule source code
	 */
	public Package compile( String packageBody ) {
		return compile(packageBody, null);
	}
	
	/**
	 * Compile a rule package and store the compiled result into the package entity
	 * An initial package can be specified, such as containing factTemplates.
	 * @param reader The Rule Package body - essentially source code - in a Reader.
	 * @param initialPackage A pre-build package from which to start or null. If supplied, the package name of 
	 * initialPackage must be the same as the package name of the packageBody being compiled.
	 */
	public Package compile( Reader reader, Package initialPackage ) {
		PackageBuilderConfiguration conf = new PackageBuilderConfiguration();
		JavaDialectConfiguration javaConf = (JavaDialectConfiguration) conf.getDialectConfiguration( "java" );
		javaConf.setJavaLanguageLevel( "1.5" );
		PackageBuilder builder = initialPackage == null ? new PackageBuilder(conf) : new PackageBuilder(initialPackage, conf);
		try {
			builder.addPackageFromDrl( reader );
			reader.close();
		} catch (Exception e) {
			throw new RuntimeException( "Error compiling rule package", e);
		}
		if (builder.hasErrors()) {
			PackageBuilderErrors errors = builder.getErrors();
			StringBuffer sb = new StringBuffer();
			try {
				reader.reset();
				int c;
				while ((c = reader.read()) != -1 ) {
					sb.append((char)c);
				}
			} catch (Exception e) {
			}
			String packageBody = sb.toString();
			int lx = packageBody.indexOf('\n');
			if (lx < 0) lx = packageBody.length();
			if (lx > 100) lx = 100;
			String firstLine = packageBody.substring(0,lx);
			throw new RuntimeException( "Error compiling " + firstLine + errors.toString() );
		}
		return builder.getPackage();
	}
	
	/**
	 * Compile a rule package and store the compiled result into the package entity
	 * An initial package can be specified, such as containing factTemplates.
	 * @param packageBody The Rule Package body - essentially source code
	 * @param initialPackage A pre-build package from which to start or null. If supplied, the package name of 
	 * initialPackage must be the same as the package name of the packageBody being compiled.
	 */
	public Package compile( String packageBody, Package initialPackage ) {
		Reader reader = new StringReader( packageBody );
		return compile( reader, initialPackage );
	}
	
	/**
	 * Serialize a compiled rules package
	 * @param pkg
	 * @return byte array containing the serialized package
	 */
	public byte[] serializePackage( Package pkg ) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream( bos );
			oos.writeObject(pkg);
			oos.close();
			return bos.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException( "Error serializing rule package " + pkg.getName());
		}
	}
	//loadPlaceholdersFrom echr
	/**
	 * Extract the accountType(s) from a rule package that must be loaded as factTemplates. 
	 
	 * @param reader
	 * @return
	 */
	public String[] extractPlaholderAccountType( String packageBody ) {
		Pattern placeholderPattern = Pattern.compile("\\@use\\s*(\\S*)\\s*placeholders"); 
		Matcher placeholderMatcher = placeholderPattern.matcher(packageBody);
		List<String> knownNames = new LinkedList<String>();
		while( placeholderMatcher.find() ) {
			knownNames.add(placeholderMatcher.group(1).trim()); 
		}
		return knownNames.toArray(new String[knownNames.size()]);
	}
	
	/**
	 * Extract the package name from a rule package. We'll need this, for example, when we use package-merging
	 * since the package to merge must have the same name as the package being compiled.
	 * @param packageBody the package source
	 * @return Name of the package
	 */
	public String extractPackageName( String packageBody ) {
		Pattern packagePattern = Pattern.compile("^package\\s*(\\S*)"); 
		Matcher packageMatcher = packagePattern.matcher(packageBody);
		String packageName = null;
		if( packageMatcher.find()) {
			packageName = packageMatcher.group(1);
		}
		
		return packageName;
	}
}
