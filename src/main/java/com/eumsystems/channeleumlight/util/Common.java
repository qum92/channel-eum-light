package com.eumsystems.channeleumlight.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import org.springframework.stereotype.Service;

@Service
public class Common {

	public static String getCurrentTime(String format) {
		Date now = new Date();
		DateFormat df = new SimpleDateFormat(format);
		String dateString = df.format(now);
		return dateString;
	}
	
	public static String getCurrentEnvironmentNetworkIp(){
	    Enumeration netInterfaces = null;
	 
	    try {
	        netInterfaces = NetworkInterface.getNetworkInterfaces();
	    } catch (SocketException e) {
	        return getLocalIp();
	    }
	 
	    while (netInterfaces.hasMoreElements()) {
	        NetworkInterface ni = (NetworkInterface)netInterfaces.nextElement();
	        Enumeration address = ni.getInetAddresses();
	 
	        if (address == null) {
	            return getLocalIp();
	        }
	 
	        while (address.hasMoreElements()) {
	            InetAddress addr = (InetAddress)address.nextElement();
	            
	            if (!addr.isLoopbackAddress() && !addr.isSiteLocalAddress() && !addr.isAnyLocalAddress() ) {
	                String ip = addr.getHostAddress();
	                
	                if( ip.indexOf(".") != -1 && ip.indexOf(":") == -1 ){
	                    return ip;
	                }
	            }
	        }
	    }
	 
	    return getLocalIp();
	}
	
	public static String getLocalIp(){
	    try {
	        return InetAddress.getLocalHost().getHostAddress();
	    } catch (UnknownHostException e) {
	        return null;
	    }
	}
}
