package com.hs18.build.getlocalip;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * Goal which touches a timestamp file.
 *
 * @goal getlocalip
 * 
 * @phase initialize
 */
public class GetLocalIPMojo
    extends AbstractMojo
{
       /**
       * @parameter default-value="${project}"
       * @required
       * @readonly
       */
	private MavenProject project;

	public void execute() throws MojoExecutionException, MojoFailureException {
		String ip = getIP();
		if(ip==null || ip.trim().equals("")){
			ip = "127.0.0.1";
		}
		System.out.println("using local ip: "+ip);
		Properties projectProperties = project.getProperties();
		projectProperties.setProperty("localIP", ip);		
	}
	
	public String getIP() throws MojoExecutionException{
		String ipOnly = "";
		try {
			 Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();
	            if (nifs == null) return "";
	            while (nifs.hasMoreElements())
	            {
	                NetworkInterface nif = nifs.nextElement();
	                // We ignore subinterfaces - as not yet needed.

	                if (!nif.isLoopback() && nif.isUp() && !nif.isVirtual())
	                {
	                    Enumeration<InetAddress> adrs = nif.getInetAddresses();
	                    while (adrs.hasMoreElements())
	                    {
	                        InetAddress adr = adrs.nextElement();
	                        if (adr != null && !adr.isLoopbackAddress() && (nif.isPointToPoint() || !adr.isLinkLocalAddress()))
	                        {
	                            String adrIP = adr.getHostAddress();
	                            String adrName;
	                            if (nif.isPointToPoint()) // Performance issues getting hostname for mobile internet sticks
	                                adrName = adrIP;
	                            else
	                                adrName = adr.getCanonicalHostName();

	                            if (!adrName.equals(adrIP))
	                                return adrIP;
	                            else
	                                ipOnly = adrIP;
	                        }
	                    }
	                }
	            }
	            
	      } catch (Exception e) {
	    	  throw new MojoExecutionException("Error: ", e);
	      }
		  return ipOnly;
	}
	
	public static void main(String[] args) throws MojoExecutionException, MojoFailureException{
		GetLocalIPMojo ip = new GetLocalIPMojo();
		ip.execute();
	}
	
}
