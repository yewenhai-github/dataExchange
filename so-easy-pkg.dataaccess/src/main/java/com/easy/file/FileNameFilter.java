package com.easy.file;

import java.io.File;
import java.io.FilenameFilter;

public class FileNameFilter implements FilenameFilter{
	private int count = 0;
	private int totalCount = 100000;
	@Override  
    public boolean accept(File dir, String name) {
		addCount();
		
        if(getCount() > totalCount){
            return false;  
        }else{  
            return true;  
        }  
    }

	public int getCount() {
		return count;
	}

	public void addCount() {
		this.count = this.count + 1;
	}
	
	public void resetCount() {
		this.count = 0;
	}
}
