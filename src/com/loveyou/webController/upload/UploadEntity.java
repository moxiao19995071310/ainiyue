package com.loveyou.webController.upload;

/** 
* @ClassName: UploadEntity 
* @Description: (这个类的作用是:封装jfinal文件上传后的返回路径) 
* @author  ggj
* @date 2016-6-9 上午11:15:08 
*  
*/
public class UploadEntity {
	/**
	 * 字段描述：主键 
	 * 字段类型 ：character varying 
	 */
	public static final String column_ids = "ids";
	/**
	 * 字段描述：文件名 
	 * 字段类型 ：character varying 
	 */
	public static final String column_filename = "filename";
	/**
	 * 字段描述：原文件名 
	 * 字段类型 ：character varying 
	 */
	public static final String column_originalfilename = "originalfilename";
	/**
	 * 字段描述：存放路径 
	 * 字段类型 ：character varying 
	 */
	public static final String column_path = "path";
	
	private String ids;
	private String version;
	//参数名称
	private String parametername;
	//文件名 
	private String filename;
	private String contenttype;
	//原文件名 
	private String originalfilename;
	//存放绝对路径 
	private String path;
	//存放相对路径
	private String relativelypath;
	
	
	public UploadEntity(String ids, String filename, String originalfilename,
			String path, String relativelypath) {
		super();
		this.ids = ids;
		this.filename = filename;
		this.originalfilename = originalfilename;
		this.path = path;
		this.relativelypath = relativelypath;
	}
	/**
	 * 
	 * @param ids
	 * @param filename
	 * @param originalfilename
	 * @param path
	 * @param relativelypath
	 * @param parametername 上传参数名称
	 */
	public UploadEntity(String ids, String filename, String originalfilename,
			String path, String relativelypath,String parametername) {
		super();
		this.ids = ids;
		this.filename = filename;
		this.originalfilename = originalfilename;
		this.path = path;
		this.relativelypath = relativelypath;
		this.parametername=parametername;
	}
 
	@Override
	public String toString() {
		if(parametername==null|"".equals(parametername))
		return "UploadEntity [ids=" + ids + ", filename=" + filename
				+ ", originalfilename=" + originalfilename + ", path=" + path
				+ ", relativelypath=" + relativelypath + "]";
		else{
			return "UploadEntity [ids=" + ids + ", filename=" + filename
					+ ", originalfilename=" + originalfilename + ", path=" + path
					+ ", relativelypath=" + relativelypath + ", parametername="+parametername+"]";

		}
			
	}


	public String getIds() {
		return ids;
	}
	public void setIds(String ids) {
		this.ids = ids;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getParametername() {
		return parametername;
	}
	public void setParametername(String parametername) {
		this.parametername = parametername;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getContenttype() {
		return contenttype;
	}
	public void setContenttype(String contenttype) {
		this.contenttype = contenttype;
	}
	public String getOriginalfilename() {
		return originalfilename;
	}
	public void setOriginalfilename(String originalfilename) {
		this.originalfilename = originalfilename;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getRelativelypath() {
		return relativelypath;
	}
	public void setRelativelypath(String relativelypath) {
		this.relativelypath = relativelypath;
	}
	
	
}
