package cn.osworks.aos.system.dao.po;

import cn.osworks.aos.core.typewrap.PO;
import java.util.Date;

/**
 * <b>Log[log]数据持久化对象</b>
 * <p>
 * 注意:此文件由AOS平台自动生成-禁止手工修改。
 * </p>
 * 
 * @author AHei
 * @date 2018-07-02 11:19:05
 */
public class LogPO extends PO {
	private static final long serialVersionUID = 1L;
	/**
	 * id
	 */
	private String id;
	/**
	 * party
	 */
	private String party;
	/**
	 * category
	 */
	private String category;
	/**
	 * title
	 */
	private String title;
	/**
	 * take
	 */
	private String take;
	/**
	 * ip_address
	 */
	private String ip_address;
	/**
	 * create_time
	 */
	private Date create_time;
	/**
	 * id
	 * 
	 * @return id
	 */
	public String getId() {
		return id;
	}
	/**
	 * party
	 * 
	 * @return party
	 */
	public String getParty() {
		return party;
	}
	/**
	 * category
	 * 
	 * @return category
	 */
	public String getCategory() {
		return category;
	}
	/**
	 * title
	 * 
	 * @return title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * take
	 * 
	 * @return take
	 */
	public String getTake() {
		return take;
	}
	/**
	 * ip_address
	 * 
	 * @return ip_address
	 */
	public String getIp_address() {
		return ip_address;
	}
	/**
	 * create_time
	 * 
	 * @return create_time
	 */
	public Date getCreate_time() {
		return create_time;
	}
	/**
	 * id
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * party
	 * 
	 * @param party
	 */
	public void setParty(String party) {
		this.party = party;
	}
	/**
	 * category
	 * 
	 * @param category
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	/**
	 * title
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * take
	 * 
	 * @param take
	 */
	public void setTake(String take) {
		this.take = take;
	}
	/**
	 * ip_address
	 * 
	 * @param ip_address
	 */
	public void setIp_address(String ip_address) {
		this.ip_address = ip_address;
	}
	/**
	 * create_time
	 * 
	 * @param create_time
	 */
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
}