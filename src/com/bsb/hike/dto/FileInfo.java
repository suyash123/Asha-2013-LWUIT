package com.bsb.hike.dto;

/**
 * dto for file upload feature
 * @author Ankit Yadav
 */
public class FileInfo implements DataModel{
	private String fileKey;
	private String fileName;
	private String contentType;
	
	public FileInfo(String fkey, String fname, String ftype){
		fileKey = fkey;
		fileName = fname;
		contentType = ftype;
	}
	
        /**
         * 
         * @return unique file id
         */
	public String getFileKey() {
		return fileKey;
	}
        
        /**
         * 
         * @return file name on the server
         */
	public String getFileName() {
		return fileName;
	}
        
        /**
         * 
         * @return content type of file
         */
	public String getContentType() {
		return contentType;
	}
}
