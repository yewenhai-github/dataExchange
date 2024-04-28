package com.easy.utility;

import java.io.File;
import java.util.Comparator;

/**
 * so-easy private
 * 
 * @author yewh 2015-10-20
 * 
 * @version 7.0.0
 * 
 */
public class FileCompratorByLastModified implements Comparator<File> {

	@Override
	public int compare(File o1, File o2) {
		long diff = o1.lastModified() - o2.lastModified();
		if(diff > 0){
			return 1;  
		}else if(diff==0){
			return 0;
		}else{
			return -1;
		}
	}
	
}
