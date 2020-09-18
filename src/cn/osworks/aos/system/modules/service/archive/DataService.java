package cn.osworks.aos.system.modules.service.archive;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.JFileChooser;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.text.log.SysoCounter;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import cn.osworks.aos.core.asset.AOSCons;
import cn.osworks.aos.core.asset.AOSJson;
import cn.osworks.aos.core.asset.AOSUtils;
import cn.osworks.aos.core.asset.WebCxt;
import cn.osworks.aos.core.exception.AOSException;
import cn.osworks.aos.core.id.AOSId;
import cn.osworks.aos.core.typewrap.Dto;
import cn.osworks.aos.core.typewrap.Dtos;
import cn.osworks.aos.core.typewrap.impl.HashDto;
import cn.osworks.aos.system.dao.LogUtils;
import cn.osworks.aos.system.dao.mapper.Aos_sys_moduleMapper;
import cn.osworks.aos.system.dao.mapper.Aos_sys_userMapper;
import cn.osworks.aos.system.dao.mapper.Archive_tablefieldlistMapper;
import cn.osworks.aos.system.dao.mapper.Archive_tablenameMapper;
import cn.osworks.aos.system.dao.mapper.LogMapper;
import cn.osworks.aos.system.dao.po.Aos_sys_modulePO;
import cn.osworks.aos.system.dao.po.Aos_sys_userPO;
import cn.osworks.aos.system.dao.po.Archive_tablefieldlistPO;
import cn.osworks.aos.system.dao.po.Archive_tablenamePO;
import cn.osworks.aos.system.dao.po.LogPO;
import cn.osworks.aos.system.modules.dao.vo.UserInfoVO;
import cn.osworks.aos.web.report.AOSReport;
import cn.osworks.aos.web.report.AOSReportModel;

/**
 * 
 * 数据服务
 * 
 * @author shq
 * 
 * @date 2016-9-14
 */
@Service
public class DataService extends JdbcDaoSupport {

	@Autowired
	private Archive_tablenameMapper archive_tablenameMapper;
	@Autowired
	private Archive_tablefieldlistMapper archive_tablefieldlistMapper;
	@Autowired
	private Aos_sys_moduleMapper aos_sys_moduleMapper;
	@Autowired
	private Aos_sys_userMapper aos_sys_userMapper;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	public static String url = "";
	@Autowired
	public static String username = "";
	@Autowired
	public static String password = "";
	@Autowired
	public static Connection connection = null;
	@Autowired
	public static PreparedStatement ps;
	@Autowired
	private LogMapper logMapper;



// 加载配置文件
	static {
		try {
			Properties props = new Properties();
			InputStream in = DataService.class
					.getResourceAsStream("/jdbc.properties");
			
			props.load(in);
			
			url = props.getProperty("jdbc.url");
			username = props.getProperty("jdbc.username");
			password = props.getProperty("jdbc.password");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Resource
	public void setJb(JdbcTemplate jb) {
		super.setJdbcTemplate(jb);
	}

	/**
	 * 
	 * 显示表头
	 * 
	 * @param qDto
	 * @return
	 */
	public List<Dto> getDataFieldListDisplayAll(Dto qDto) {
		String query = queryConditions2(qDto);
		String sql="";
		String fieldName;
		String enfield = "id_";
		List<Archive_tablefieldlistPO> list = archive_tablefieldlistMapper
				.getDataFieldDisplayAll(qDto.getString("tablename"));
		String orderenfield = "" ;
		for (int i = 0; i < list.size(); i++) {
			fieldName = list.get(i).getFieldenname();
			enfield = enfield + "," + fieldName;
			if(i==3){
				orderenfield=enfield;
			}
		}
		orderenfield = orderenfield.substring(4);
		if(qDto.getString("page")!=null&&qDto.getString("page")!=""){
			sql = "WITH aos_query_ AS (SELECT TOP 100 PERCENT ROW_NUMBER () OVER (ORDER BY "+orderenfield+") AS aos_rn_,"
					+ enfield
					+ " FROM "
					+ qDto.getString("tablename")
					+ " WHERE "+qDto.getString("queryclass")
					+ query
					+ " ) "
					+ "SELECT * FROM aos_query_ WHERE aos_rn_ BETWEEN "
					+ qDto.getPageStart()
					+ " AND "
					+ qDto.getPageLimit()
					* Integer.valueOf(qDto.getString("page")) + " ORDER BY aos_rn_";
		}else{
			sql = "WITH aos_query_ AS (SELECT TOP 100 PERCENT ROW_NUMBER () OVER (ORDER BY "+orderenfield+") AS aos_rn_,"
					+ enfield
					+ " FROM "
					+ qDto.getString("tablename")
					+ " WHERE "+qDto.getString("queryclass")
					+ query
					+ " ) "
					+ "SELECT * FROM aos_query_  ORDER BY aos_rn_";
		}	
		List listDto = jdbcTemplate.queryForList(sql);
		return listDto;
	}

	/**
	 * 
	 * 页面初始化
	 * 
	 * @param tablename
	 * @return
	 */
	public List<Archive_tablefieldlistPO> getDataFieldListTitle(String tablename) {
		List<Archive_tablefieldlistPO> list = archive_tablefieldlistMapper
				.getDataFieldDisplayAll(tablename);
		return list;
	}
	
	/**
	 * 
	 * 
	 * 
	 * @author Sun
	 * @param tablename
	 * @return
	 *
	 * 2018-8-14
	 */
	public List<Archive_tablefieldlistPO> getInfoFieldListTitle(String tablename) {
		List<Archive_tablefieldlistPO> list = archive_tablefieldlistMapper
				.getInfoFieldDisplayAll(tablename);
		return list;
	}

	/**
	 * 功能：转换MapList为数组List
	 * 
	 * @param list
	 * @return
	 */
	public static List convertMapListToArrayList(List list) {
		//Map map = null;
		/*
		 * Dto qDto = Dtos.newDto(); if (list != null) { Iterator iterator =
		 * list.iterator(); while (iterator.hasNext()) { map = (Map)
		 * iterator.next(); Iterator<?> keyIt = map.keySet().iterator(); while
		 * (keyIt.hasNext()) { String key = (String) keyIt.next(); String value
		 * = ((Object) map.get(key)) == null ? "" : ((Object)
		 * map.get(key)).toString(); qDto.put(key, value); } } }
		 */

		return list;
	}

	/**
	 * 功能：转换MapList为Dto
	 * 
	 * @param list
	 * @return
	 */
	public static Dto convertMapListToDto(List list) {
		Map map = null;
		Dto qDto = Dtos.newDto();
		if (list != null) {
			Iterator iterator = list.iterator();
			while (iterator.hasNext()) {
				map = (Map) iterator.next();
				Iterator<?> keyIt = map.keySet().iterator();
				while (keyIt.hasNext()) {
					String key = (String) keyIt.next();
					String value = ((Object) map.get(key)) == null ? ""
							: ((Object) map.get(key)).toString();
					qDto.put(key, value);
				}
			}
		}

		return qDto;
	}

	/**
	 * 
	 * 查询记录总数
	 * 
	 * @param tablename
	 * @return
	 */
	public int getPageTotal(Dto qDto) {
		String query = queryConditions2(qDto);
		String sql = "SELECT COUNT(*) FROM " + qDto.getString("tablename")
				+ " WHERE 1=1 " + query;
		return jdbcTemplate.queryForInt(sql);
	}

	public Object getData(Dto qDto) {
		String tablename = qDto.getString("tablename");
		String id = qDto.getString("id_");
		String sql = "SELECT * FROM " + tablename + " WHERE id_='" + id + "'";
		List<Dto> listDto = convertMapListToArrayList(jdbcTemplate
				.queryForList(sql));
		return listDto.get(0);
	}

	/**
	 * 
	 * 获得电子文件信息
	 * 
	 * @param qDto
	 * @return
	 */
	public List<Dto> getPath(Dto qDto) {
		String tablename = qDto.getString("tablename") + "_path";
		String tid = qDto.getString("tid");
		String sql = " SELECT id_,tid,_path,dirname,sdatetime,_s_path ,RIGHT(RTRIM(_Path), CHARINDEX('.',REVERSE(RTRIM(_path))) - 1) as filetype FROM "
				+ tablename + " WHERE tid ='" + tid + "'";
		List<Dto> listDto = convertMapListToArrayList(jdbcTemplate
				.queryForList(sql));
		return listDto;
	}

	/**
	 * 
	 * 获得全部电子文件信息
	 * 
	 * @param qDto
	 * @return
	 */
	public List<Dto> getAllPath(Dto qDto) {
		String tablename = qDto.getString("tablename") + "_path";
		// String tid = qDto.getString("tid");
		// String sql = " SELECT dirname+_s_path as path,tid From " + tablename;
		String sql = "SELECT b.id_,dirname+_s_path as path,tid,b.tm From "
				+ tablename + " a  " + "left join "
				+ qDto.getString("tablename") + " b on a.tid=b.id_";
		List<Dto> listDto = convertMapListToArrayList(jdbcTemplate
				.queryForList(sql));
		return listDto;
	}

	/**
	 * 
	 * 电子文件路径
	 * 
	 * @param qDto
	 * @return
	 */
	public String getDocumentPath(Dto qDto) {
		//dirname+_s_path 文件路径
		String tablename = qDto.getString("tablename") + "_path";
		String pid = qDto.getString("id");
		String sql = " SELECT dirname+_s_path as path From " + tablename+ " WHERE id_='" + pid + "'";
		/*if(tablename.equals("ctda_path")){
			
			sql="  SELECT '" + qDto.getString("base")
				+ "'+'/'+dirname+'/'+_path as path From " + tablename
				+ " WHERE id_='" + pid + "'";
		}if(tablename.equals("zpda_path")){
			
			sql="  SELECT '" + qDto.getString("base")
				+ "'+'/'+dirname+'/'+_s_path as path From " + tablename
				+ " WHERE id_='" + pid + "'";
		}*/
		
		List<Map<String, Object>> listDto = jdbcTemplate.queryForList(sql);
		String path = listDto.get(0).get("path").toString();
		// String path=" ";
		return path;
	}

	/**
	 * 
	 * 删除条目
	 * 
	 * @param qDto
	 * @return
	 */
	public Dto deleteData(Dto qDto) {
		Dto outDto = Dtos.newDto();
		String[] selections = qDto.getRows();
		String tablename = qDto.getString("tablename");
		int del = 0;
		for (String id_ : selections) {
			jdbcTemplate.execute(" delete from " + tablename + " where id_='"
					+ id_ + "'");
			del++;
		}
		String msg = "操作完成，";
		if (del > 0) {
			msg = AOSUtils.merge(msg + "成功删除信息[{0}]个", del);
		}
		outDto.setAppMsg(msg);
		return outDto;

	}

	/**
	 * 
	 * 删除单个电子文件
	 * 
	 * @param qDto
	 * @return
	 */
	public Dto deletePath(Dto qDto) {
		Dto outDto = Dtos.newDto();
		Dto dto=Dtos.newDto();
		dto.put("tablename", qDto.getString("tablename"));
		int del = 1;
		String id = qDto.getString("id_");
		String tablename = qDto.getString("tablename") + "_path";
		String tableinfo = qDto.getString("tablename")+"_info";
		jdbcTemplate.execute(" delete from " + tablename + " where id_='" + id
				+ "'");
		Archive_tablenamePO archive_tablenamePO = archive_tablenameMapper.selectOne(dto);
		if(archive_tablenamePO.getTablemedia()!=null&&archive_tablenamePO.getTablemedia()!=""){
			jdbcTemplate.execute(" delete from " + tableinfo + " where did='" + id
					+ "'");
		}
		String msg = AOSUtils.merge("操作完成，成功删除信息[{0}]个", del);
		outDto.setAppMsg(msg);
		return outDto;
	}

	/**
	 * 
	 * 删除全部电子文件
	 * 
	 * @param qDto
	 * @return
	 */
	public Dto deletePathAll(Dto qDto) {
		Dto outDto = Dtos.newDto();
		int del = 0;
		String[] selections = qDto.getRows();
		String tablename = qDto.getString("tablename") + "_path";
		String tableinfo = qDto.getString("tablename") + "_info";
		Dto dto = Dtos.newDto();
		dto.put("tablename", qDto.getString("tablename"));
		for (String id : selections) {
			jdbcTemplate.execute(" DELETE FROM " + tablename + " WHERE tid='"
					+ id + "'");
			Archive_tablenamePO archive_tablenamePO = archive_tablenameMapper.selectOne(dto);
			if(archive_tablenamePO.getTablemedia()!=null&&archive_tablenamePO.getTablemedia()!=""){
				jdbcTemplate.execute(" DELETE FROM " + tableinfo + " WHERE tid='"
						+ id + "'");
			}
			
			jdbcTemplate.execute(" UPDATE  " + qDto.getString("tablename")
					+ " SET _path='0' WHERE id_='" + id + "'");
			del++;
		}
		
		
		// jdbcTemplate.execute(" UPDATE  "+tablename+" SET _path='0' WHERE id_='"+id+"'");
		String msg = AOSUtils.merge("操作完成，成功删除", del);
		outDto.setAppMsg(msg);
		return outDto;
	}

	/**
	 * 
	 * 更新电子文件个数
	 * 
	 * @param qDto
	 * @return
	 */
	public void refreshPath(Dto qDto) {
		String tablename = qDto.getString("tablename");
		String tid = qDto.getString("tid");
		String sql = " UPDATE " + tablename
				+ " set _path =(select count(id_) from " + tablename
				+ "_path where tid='" + tid + "' ) where id_='" + tid + "'";
		jdbcTemplate.execute(sql);
	}
	/**
	 * 下载电子文件
	 * @param dto
	 * @param request 
	 * @param response 
	 * @throws IOException 
	 */
	public void download_electronic_file(String filePath,Dto dto, HttpServletResponse response, HttpServletRequest request) throws IOException {
		// TODO Auto-generated method stub
		String filename = null;
		String filepath = null;
		String _path = null;
		// 查询文件路径信息
		List<Map<String, Object>> list =find_pathmessage(
				dto.getString("id_"), dto.getString("tablename"));
		if (list != null && list.size() > 0) {
			filename = (String) list.get(0).get("dirname");
			filepath = (String) list.get(0).get("_s_path");
			_path = (String) list.get(0).get("_path");
			response.setContentType("application/x-" + filepath.split("\\.")[1]
					+ ";charset=gb2312");
			//名称点上后缀
			response.addHeader("Content-Disposition", "attachment;filename="
					+ URLEncoder.encode(_path , "utf-8"));
			OutputStream outputStream = response.getOutputStream();
			try {
				FileInputStream fileInputStream = new FileInputStream(new File(filePath+dto.getString("tablename")

+File.separator+filename + filepath));
				byte[] b = new byte[1024];
				int len = 0;
				while ((len = fileInputStream.read(b)) != -1) {
					outputStream.write(b, 0, len);
					outputStream.flush();
				}
			} catch (Exception e) {
				throw new AOSException(e);
			} finally {
				if (outputStream != null) {
					try {
						outputStream.close();
					} catch (IOException ex) {

					}
				}
			}
		}
	}

	/**
	 * 根据id查询文件详细信息
	 * 
	 * @param id
	 * @param tablename
	 * @return
	 */
	public List<Map<String, Object>> find_pathmessage(String id,
			String tablename) {
		// TODO Auto-generated method stub
		String sql = "select*from " + tablename + "_path " + " where id_='"
				+ id + "'";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		return list;
	}
	/**
	 * 
	 * 保存信息
	 * 
	 * @param qDto
	 * @return
	 */
	public Dto saveData(Dto qDto) {
		Dto outDto = Dtos.newDto();
		// Map map = new HashMap();
		String columns = "id_";
		// String _lrr=outDto.getUserInfo().getName_();
		// String _lrrq="";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		String date = df.format(new Date());
		String values = "";
		Iterator iter = qDto.entrySet().iterator(); // 获取key和value的set
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next(); // 把hashmap转成Iterator再迭代到entry
			Object key = entry.getKey(); // 从entry获取keyObject
			if (key.equals("app") || key.equals("tablename")
					|| key.equals("_userInfoVO")) {
				continue;
			}
			columns = columns + "," + key;
			Object val = "'" + entry.getValue() + "'"; // 从entry获取value}
			values = values + "," + val;
		}
		String sql = " INSERT INTO " + qDto.getString("tablename") + " ("
				+ columns + ",_lrr,_lrrq) VALUES ('" + AOSId.uuid() + "'"
				+ values + ",'" + qDto.getUserInfo().getName_() + "','" + date
				+ "')";
		jdbcTemplate.execute(sql);
		
		//List<Archive_tableinputPO> list = archive_tableinputMapper.
		outDto.setAppCode(AOSCons.SUCCESS);
		outDto.setAppMsg("操作完成，加存成功。");
		return outDto;
	}

	public Dto updateData(Dto qDto) {
		Dto outDto = Dtos.newDto();
		String columns = "id_";
		String strsql = "";
		Iterator iter = qDto.entrySet().iterator(); // 获取key和value的set
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next(); // 把hashmap转成Iterator再迭代到entry
			Object key = entry.getKey(); // 从entry获取keyObject
			if (key.equals("app") || key.equals("tablename")
					|| key.equals("_userInfoVO") || key.equals("id")) {
				continue;
			}
			Object val = "'" + entry.getValue() + "'"; // 从entry获取value}
			// strsql=key+"="+val+"";
			if (strsql.equals("")) {
				strsql = key + "=" + val + "";
			} else
				strsql = strsql + "," + key + "=" + val + "";
		}
		String sql = " UPDATE " + qDto.getString("tablename") + " SET "
				+ strsql + " WHERE id_='" + qDto.getString("id") + "'";
		jdbcTemplate.execute(sql);
		outDto.setAppCode(AOSCons.SUCCESS);
		outDto.setAppMsg("操作完成，加存成功。");
		return outDto;
	}

	/**
	 * 
	 * 查询条件拼接
	 * 
	 * @param qDto
	 * @return
	 */
	public String queryConditions(Dto qDto) {

		if (qDto.getString("columnnum") != "") {
			int contentLength = Integer.parseInt(qDto.getString("columnnum"));
			StringBuffer query = new StringBuffer();
			for (int i = 1; i <= contentLength + 1; i++) {
				if (qDto.getString("content" + i) != null
						&& qDto.getString("content" + i) != "") {

					if (qDto.getString("and" + i).equals("on")) {
						query.append("and " + qDto.getString("filedname" + i));
					}
					if (qDto.getString("or" + i).equals("on")) {
						query.append("or " + qDto.getString("filedname" + i)
								+ " " + qDto.getString("condition" + i));
					}
					if (qDto.getString("condition" + i).equals("like")) {
						query.append(" " + qDto.getString("condition" + i)
								+ " '%" + qDto.getString("content" + i) + "%'");
					}
					if (qDto.getString("condition" + i).equals("left")) {
						query.append(" like '%" + qDto.getString("content" + i)
								+ "'");
					}
					if (qDto.getString("condition" + i).equals("right")) {
						query.append(" like '" + qDto.getString("content" + i)
								+ "%'");
					}
					if (qDto.getString("condition" + i).equals(">")) {
						query.append(" > " + qDto.getString("content" + i));
					}
					if (qDto.getString("condition" + i).equals("<")) {
						query.append(" < " + qDto.getString("content" + i));
					}
					if (qDto.getString("condition" + i).equals("<=")) {
						query.append(" <= " + qDto.getString("content" + i));
					}
					if (qDto.getString("condition" + i).equals(">=")) {
						query.append(" >= " + qDto.getString("content" + i));
					}
					if (qDto.getString("condition" + i).equals("=")) {
						query.append(" = '" + qDto.getString("content" + i)
								+ "'");
					}

				}

			}
			return query.toString();
		} else
			return "";

	}
	/**
	 * 
	 * 查询条件拼接
	 * 
	 * @param qDto
	 * @return
	 */
	public String queryConditions2(Dto qDto) {

		if (qDto.getString("columnnum") != "") {
			int contentLength = Integer.parseInt(qDto.getString("columnnum"));
			StringBuffer query = new StringBuffer();
			for (int i = 1; i <= contentLength + 1; i++) {
				if (qDto.getString("content" + i) != null
						&& qDto.getString("content" + i) != "") {

					if (qDto.getString("and" + i).equals("on")) {
						query.append("and " + qDto.getString("filedname" + i));
					}else{
						query.append("or " + qDto.getString("filedname" + i)
								+ " ");
					}
					if (qDto.getString("condition" + i).equals("like")) {
						query.append(" " + qDto.getString("condition" + i)
								+ " '%" + qDto.getString("content" + i) + "%'");
					}
					if (qDto.getString("condition" + i).equals("left")) {
						query.append(" like '%" + qDto.getString("content" + i)
								+ "'");
					}
					if (qDto.getString("condition" + i).equals("right")) {
						query.append(" like '" + qDto.getString("content" + i)
								+ "%'");
					}
					if (qDto.getString("condition" + i).equals(">")) {
						query.append(" > " + qDto.getString("content" + i));
					}
					if (qDto.getString("condition" + i).equals("<")) {
						query.append(" < " + qDto.getString("content" + i));
					}
					if (qDto.getString("condition" + i).equals("<=")) {
						query.append(" <= " + qDto.getString("content" + i));
					}
					if (qDto.getString("condition" + i).equals(">=")) {
						query.append(" >= " + qDto.getString("content" + i));
					}
					if (qDto.getString("condition" + i).equals("=")) {
						query.append(" = '" + qDto.getString("content" + i)
								+ "'");
					}

				}

			}
			return query.toString();
		} else
			return "";

	}

	/**
	 * 
	 * 记录更新
	 * 
	 * @param qDto
	 * @return
	 */
	public Dto updateRecord(Dto qDto) {
		Dto outDto = Dtos.newDto();
		String sql = " update " + qDto.getString("tablename") + " set "
				+ qDto.getString("filedname") + " ='"
				+ qDto.getString("update_content") + "' WHERE 1=1 "
				+ qDto.getString("query");
		int count = jdbcTemplate.update(sql);
		String msg = AOSUtils.merge("操作完成，成功更新[{0}]条记录", count);
		outDto.setAppMsg(msg);
		return outDto;
	}

	/**
	 * 
	 * 记录替换
	 * 
	 * @param qDto
	 * @return
	 */
	public Dto replaceRecord(Dto qDto) {
		Dto outDto = Dtos.newDto();
		String sql = " UPDATE " + qDto.getString("tablename") + " SET "
				+ qDto.getString("filedname") + " =REPLACE("
				+ qDto.getString("filedname") + ",'"
				+ qDto.getString("replace_source") + "','"
				+ qDto.getString("replace_new") + "') WHERE 1=1 "
				+ qDto.getString("query");
		int count = jdbcTemplate.update(sql);
		String msg = AOSUtils.merge("操作完成，成功替换[{0}]条记录", count);
		outDto.setAppMsg(msg);
		return outDto;
	}

	/**
	 * 
	 * 前后辍
	 * 
	 * @param qDto
	 * @return
	 */
	public Dto suffixRecord(Dto qDto) {
		Dto outDto = Dtos.newDto();
		String sql = " UPDATE " + qDto.getString("tablename") + " SET "
				+ qDto.getString("filedname") + "=";
		if (qDto.getString("suffix_front") != "") {
			sql = sql + "'" + qDto.getString("suffix_front") + "' +";
		}
		sql = sql + qDto.getString("filedname");
		if (qDto.getString("suffix_after") != "") {
			sql = sql + "+'" + qDto.getString("suffix_after") + "'";
		}
		sql = sql + " WHERE 1=1 " + qDto.getString("query");
		int count = jdbcTemplate.update(sql);
		String msg = AOSUtils.merge("操作完成，成功替换[{0}]条记录", count);
		outDto.setAppMsg(msg);
		return outDto;
	}

	/**
	 * 
	 * 补位
	 * 
	 * @param qDto
	 * @return
	 */
	public Dto repairRecord(Dto qDto) {
		Dto outDto = Dtos.newDto();
		String sql = " UPDATE " + qDto.getString("tablename") + " SET "
				+ qDto.getString("filedname") + "=RIGHT('"
				+ qDto.getString("repair_value") + "' + "
				+ qDto.getString("filedname") + ","
				+ qDto.getString("repair_long") + ") WHERE 1=1 "
				+ qDto.getString("query");
		int count = jdbcTemplate.update(sql);
		String msg = AOSUtils.merge("操作完成，成功替换[{0}]条记录", count);
		outDto.setAppMsg(msg);
		return outDto;
	}

	/**
	 * 
	 * 导出EXCEL
	 * 
	 * @param qDto
	 * @return
	 */
	public List<Dto> exportData(Dto qDto) {
		// String query = queryConditions(qDto);
		String fieldName;
		String enfield = "";
		List<Archive_tablefieldlistPO> list = archive_tablefieldlistMapper
				.getDataFieldDisplayAll(qDto.getString("tablename"));
		for (int i = 0; i < list.size(); i++) {
			fieldName = list.get(i).getFieldenname();
			if (enfield.equals("")) {
				enfield = fieldName;
				continue;
			}
			enfield = enfield + "," + fieldName;
		}
		String sql = "WITH aos_query_ AS (SELECT " + enfield + " FROM "
				+ qDto.getString("tablename") + " WHERE 1=1 "
				+ qDto.getString("query") + " ) " + "SELECT * FROM aos_query_ ";
		List<Dto> listDto = convertMapListToArrayList(jdbcTemplate
				.queryForList(sql));
		return listDto;

	}

	/**
	 * 
	 * 导出EXCEL
	 * 
	 * @param qDto
	 * @return
	 */
	public List<Dto> exportDatagd(Dto qDto) {
		// String query = queryConditions(qDto);
		
		String sql = "WITH aos_query_ AS (SELECT " + qDto.getString("enfield") + " FROM "
				+ qDto.getString("tablename") + " WHERE 1=1 "
				+ qDto.getString("query") + " ) " + "SELECT * FROM aos_query_ ";
		List<Dto> listDto = convertMapListToArrayList(jdbcTemplate
				.queryForList(sql));
		return listDto;

	}
	
	/**
	 * 
	 * 查询单条数据
	 * 
	 * @param id
	 * @param tablename
	 * @return
	 */
	public Dto selectOne(String id, String tablename) {
		Dto qDto = Dtos.newDto();
		// String tablename = qDto.getString("tablename");
		// String id = qDto.getString("id_");
		String sql = "SELECT * FROM " + tablename + " WHERE id_='" + id + "'";
		qDto = convertMapListToDto(jdbcTemplate.queryForList(sql));
		return qDto;
	}
	
	/**
	 * 
	 * 判断是不是全部
	 * 
	 * @param pDto
	 * @return
	 */
	
	public String isAll(Dto pDto){
		Dto qDto = Dtos.newDto();
		String sql = "SELECT * FROM aos_sys_module  WHERE cascade_id_='" + pDto.getString("cascode_id_") + "'";
		qDto = convertMapListToDto(jdbcTemplate.queryForList(sql));
		String queryclass=" 1=1";
		if(!qDto.getString("name_").equals("全部")){
			queryclass = " _classtree='"+pDto.getString("cascode_id_")+"'";
		}
		return queryclass;
	}
	
	/**
	 * 
	 * 显示档案元数据
	 * 
	 * @param qDto
	 * @return
	 */
	public List<Dto> getDataExifs(Dto qDto) {
		String query = queryConditions2(qDto);
		String fieldName;
		String enfield = "id_";
		List<Archive_tablefieldlistPO> list = archive_tablefieldlistMapper
				.getInfoFieldDisplayAll(qDto.getString("tablename")+"_info");
		for (int i = 0; i < list.size(); i++) {
			fieldName = list.get(i).getFieldenname();
			enfield = enfield + "," + fieldName;
		}
		
		String sql = "WITH aos_query_ AS (SELECT TOP 100 PERCENT ROW_NUMBER () OVER (ORDER BY CURRENT_TIMESTAMP) AS aos_rn_,"
				+ enfield
				+ " FROM "
				+ qDto.getString("tablename")+"_info"
				+ " WHERE tid='"+qDto.getString("id")+"'"
				+ " ) "
				+ "SELECT * FROM aos_query_  ORDER BY aos_rn_";
		List listDto = jdbcTemplate.queryForList(sql);
		return listDto;
	}
	
	
	/**
	 * 
	 * 显示文书档案元数据
	 * 
	 * @param qDto
	 * @return
	 */
	public List<Dto> getDataWsdaExifs(Dto qDto) {
		String query = queryConditions2(qDto);
		//String fieldName;
		String enfield = "id_,xh,tid,intw,inth,dpi,fsize,paper,sckj,ysl,sblx,sbzzs,sbxh,sbxlh,sbggq,szhrjmc,szhrjbb,szhrjscs,szhsj,szhms,szhgs";
		String sql = "WITH aos_query_ AS (SELECT TOP 100 PERCENT ROW_NUMBER () OVER (ORDER BY CURRENT_TIMESTAMP) AS aos_rn_,"
				+ enfield
				+ " FROM "
				+ qDto.getString("tablename")+"_info"
				+ " WHERE tid in( select tid from "+qDto.getString("tablename")+"_path where id_='"+qDto.getString("id")+"' "
				+ " )) "
				+ "SELECT * FROM aos_query_ WHERE aos_rn_ BETWEEN "
				+ qDto.getPageStart()
				+ " AND "
				+ qDto.getPageLimit()
				* Integer.valueOf(qDto.getString("page")) + " ORDER BY aos_rn_";
		List listDto = jdbcTemplate.queryForList(sql);
		return listDto;
	}
	
	/**
	 * 
	 * 设置显示行数
	 * 
	 * @param qDto
	 * @return
	 */
	public Dto setPagesize(Dto inDto) {
		Dto outDto = Dtos.newDto();
		
		Aos_sys_userPO aos_sys_user =new Aos_sys_userPO();
		
		AOSUtils.copyProperties(inDto, aos_sys_user);
		
		aos_sys_userMapper.updateByKey(aos_sys_user);
		
		String msg = AOSUtils.merge("操作完成，请重新登录！！！");
		outDto.setAppMsg(msg);
		return outDto;
	}
	// 开启事务
	public static void connOpen() throws SQLException {
		connection = DriverManager.getConnection(url, username, password);
	}

	// 关闭事务
	public static void connClose() throws SQLException {
		if (ps != null) {
			ps.close();
			ps = null;
		}
		if (connection != null) {
			connection.close();
			connection = null;
		}
	}
	/**
	 * 根据表英文名查询属性信息
	 * 
	 * @param dto
	 * @return
	 */
	public Archive_tablenamePO findtablenameid(Dto dto) {
		// TODO Auto-generated method stub
		Archive_tablenamePO tablenamePO = archive_tablenameMapper
				.selectOne(dto);
		return tablenamePO;
	}	
	/**
	 * 根据tid和FieldCnName查询到FieldEnName
	 * 
	 * @param id_
	 * @param string
	 */
	private String findenname(String id_, String string) {
		// TODO Auto-generated method stub
		String sql = "select * from archive_TableFieldList where tid='" + id_
				+ "' and FieldCnName='" + string + "'";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		if (list != null && list.size() > 0) {
			return list.get(0).get("FieldEnName").toString();
		}
		return null;
	}
	/**
	 * 查询鉴定数据
	 * @param outDto
	 */
	public List<Map<String,Object>> findSj(Dto outDto) {
		// TODO Auto-generated method stub
		String sql="select * from "+outDto.getString("tablename")+" where 1=1  "+outDto.getString("querySession");
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		List<Map<String,Object>> appraisallist =new ArrayList<Map<String,Object>>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        Date date = new Date();
        sdf.format(date);
		//当前年度
		Integer newnd=Integer.valueOf(sdf.format(date));
		if(list!=null&&list.size()>0){
			for(int i=0;i<list.size();i++){
				Map<String, Object> map = list.get(i);
				//获得成文日期
				Object cwrq = map.get("cwrq");
				if(cwrq==null||cwrq==""){
					continue;
				}
				String cwrq2=cwrq.toString();
				Integer cwrq3=Integer.valueOf(cwrq2);
				
				//保管期限
				Object bgqx=map.get("bgqx");
				if(bgqx==null||bgqx==""){
					continue;
				}
				String bgqx2=bgqx.toString();
				Integer bgqx3=Integer.valueOf(bgqx2);
				if(bgqx2.endsWith("Y")){
					continue;
				}
				if((newnd-Integer.valueOf(cwrq2)>Integer.valueOf(bgqx2))&&!bgqx2.endsWith("Y")){
					appraisallist.add(map);
					continue;
				}
			}
		}
		return appraisallist;
	}
	/**
	 * 修改标记
	 * @param outDto
	 */
	public boolean updateappraisal(Dto outDto) {
		// TODO Auto-generated method stub
		try {
			String sql="update "+outDto.getString("tablename")+" set zzjjg='1' where id_='"+outDto.getString("aos_rows_").split(",")[0]+"'";
			int update = jdbcTemplate.update(sql);
			if(update==1){
				return true;
			}
			return false;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 根据条件删除
	 * @param outDto
	 */
	public Dto deleteTermData(Dto outDto) {
		// TODO Auto-generated method stub
		Dto out= Dtos.newDto();
		int d=0;
		try {
			String conditions = queryConditions2(outDto);
			String sql = "SELECT COUNT(*) FROM " + outDto.getString("tablename")
					+ " WHERE 1=1 " + conditions;
			d=jdbcTemplate.queryForInt(sql);
			String sql2="delete from "+outDto.getString("tablename")+" where 1=1 "+conditions;
			jdbcTemplate.execute(sql2);
			String msg="操作完成,";
			if (d > 0) {
				msg = AOSUtils.merge(msg + "成功删除信息[{0}]个", d);
			}
			out.setAppMsg(msg);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return out;
	}
	/**
	 * 进行文件夹和移交电子文件的创建
	 * @param path 
	 * @param fieldDtos
	 * @param outDto
	 * @throws Exception 
	 */
	public void createfiletransfer(String path, String filepath,List<Dto> fieldDtos, Dto outDto){
		// TODO Auto-generated method stub
		//比如是一个固定的文件夹
		try {
			String tablename=outDto.getString("tablename");
			String transferpath=path;
			String filedname=outDto.getString("filedname");
			if(fieldDtos!=null&&fieldDtos.size()>0){
				for(int i=0;i<fieldDtos.size();i++){
					Map<String, Object> fieldmap= fieldDtos.get(i);
					//根据当前条目进行电子文件路径的查询，执行读写操作
					String id_ = (String)fieldmap.get("id_");
					//得到移交文件夹名称
					String transferfile=(String)fieldmap.get(filedname);
					//
					if(transferfile==null||transferfile==""){
						continue;
					}
					//根据id_查询到对应电子文件的完整路径
					List<Map<String, Object>> listpath = find_path(id_,tablename);
					if(listpath!=null&&listpath.size()>0){
						for(int t=0;t<listpath.size();t++){
							Map<String, Object> map = listpath.get(t);
							String dirname = (String)map.get("dirname");
							String _s_path = (String)map.get("_s_path");
							//拼接上传路径
							String splituploadpath=filepath+tablename+"\\"+dirname+_s_path;
							//拼接移交路径
							String splittransferpath=transferpath+File.separator+tablename+File.separator+transferfile;
							//执行读写操作
							ReadWriterfilename(splituploadpath,splittransferpath);
						}
					}else{
						//此时不存在电子文件，
						//创建空文件加
						File f=new File(transferpath+transferfile);
						if(!f.exists()){
							f.mkdirs();
						}
						continue;
					}				
				}			
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	/**
	 * 移交文件读写操作
	 * @param splituploadpath
	 * @param splittransferpath
	 * @throws Exception 
	 */
	private void ReadWriterfilename(String splituploadpath,
			String splittransferpath){
		// TODO Auto-generated method stub
		try {
			//完整上传路径
			String[] split = splituploadpath.split("\\.");
			String suffix = splituploadpath.split("\\.")[split.length-1];
			//wsda\003feb45e18041e6b15f91827a705f00\2aba031077644ab58bcf51cb2ede9165.jpg
			//D:\移交\1
			//得到文件名称
			String filename=splituploadpath.split("\\\\")[splituploadpath.split("\\\\").length-1];
			//完整下载路径
			String allsplituploadpath=splituploadpath;
			String allsplittransferpath=splittransferpath+File.separator+filename;
			//源文件
			//目的文件
			//写入文件
			File f=new File(allsplituploadpath);
			File s=new File(splittransferpath);
			File w=new File(allsplittransferpath);
			InputStream in=null;
			OutputStream out=null;
			try {
				if(!s.exists()){
					s.mkdirs();
				}
				if(!w.exists()){
					w.createNewFile();
				}
				in=new FileInputStream(f);
				out=new FileOutputStream(w,true);
				byte[] temp=new byte[1024];
				int length=0;
				while((length=in.read(temp))!=-1){
					out.write(temp,0,length);
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}finally{
				try {
					in.close();
					out.close();
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
	}

	/**
	 * 查询电子文件路径集合
	 * @param id_
	 * @param tablename
	 */
	private List<Map<String,Object>> find_path(String id_, String tablename) {
		// TODO Auto-generated method stub
		String sql="select * from "+tablename+"_path where tid='"+id_+"'";
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		return list;
	}
	

	public List<Dto> exportTransfer(Dto outDto) {
		// TODO Auto-generated method stub
		String fieldName;
		String enfield = "";
		List<Archive_tablefieldlistPO> list = archive_tablefieldlistMapper
				.getDataFieldDisplayAll(outDto.getString("tablename"));
		for (int i = 0; i < list.size(); i++) {
			fieldName = list.get(i).getFieldenname();
			if (enfield.equals("")) {
				enfield = fieldName;
				continue;
			}
			enfield = enfield + "," + fieldName;
		}
		String sql = "WITH aos_query_ AS (SELECT " + enfield + " FROM "
				+ outDto.getString("tablename") + " WHERE 1=1 "
				+ outDto.getString("query") + " and "+outDto.getString("queryclass")+") " + "SELECT * FROM aos_query_ ";
		List<Dto> listDto = convertMapListToArrayList(jdbcTemplate
				.queryForList(sql));
		return listDto;

	}
	/**
	 * 弹出文件夹窗口选择
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	public String transferfilepathData(HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		// TODO Auto-generated method stub
		JFileChooser fileChooser = new JFileChooser("D:\\"); 

		 fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 

		 int returnVal = fileChooser.showOpenDialog(fileChooser); 

		 if(returnVal == JFileChooser.APPROVE_OPTION)

		{ String filePath= fileChooser.getSelectedFile().getAbsolutePath();//这个就是你选择的文件夹的
			return filePath;
		}
		 return null;
	}
	public Dto transferfile(Dto outDto, String path,String filepath, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		// TODO Auto-generated method stub
		//查询到所有数据(得到指定分类下面的所有分类信息)
		Dto out = Dtos.newDto();
		List fieldDtos = getDataFieldListDisplayAll(outDto);
		//通过选泽的文件夹名字段，进行执行路径进行文件名的创建
		//电子文件路径创建\
		try {
			createfiletransfer(path,filepath,fieldDtos,outDto);
			//添加日志
			UserInfoVO userInfoVO=(UserInfoVO) session.getAttribute("_userInfoVO");
			LogPO logPO = LogUtils.InsertLog(AOSId.uuid(), userInfoVO.getAccount_(), "移交电子文件["+outDto.getString("tablename")+"]", "", "移交", request.getRemoteAddr(), new Date());
			logMapper.insert(logPO);
			//
			out.setAppCode(1);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			out.setAppCode(-1);
		}
		return out;
	}

	/**
	 * 移交电子条目
	 * @param outDto
	 * @param path 
	 * @param session 
	 * @param response 
	 * @param request 
	 */
	public void transferReport(Dto outDto, String path, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		// TODO Auto-generated method stub				
			try {
				outDto.put("query", request.getSession().getAttribute("querySession"));
				List<Dto> list =exportTransfer(outDto);		
				List<Archive_tablefieldlistPO> titleDtos =getDataFieldListTitle(outDto.getString("tablename"));
				//组装报表数据模型
				AOSReportModel reportModel = new AOSReportModel();
				reportModel.setFileName(outDto.getString("tablename"));
				//设置报表集合
				reportModel.setFieldsList(list);
				reportModel.setExcelHeader(titleDtos);
				//填充报表
				session.setAttribute(AOSReport.DEFAULT_AOSPRINT_KEY, reportModel);
				//添加日志
				UserInfoVO userInfoVO=(UserInfoVO) session.getAttribute("_userInfoVO");
				LogPO logPO = LogUtils.InsertLog(AOSId.uuid(), userInfoVO.getAccount_(), "移交条目["+outDto.getString("tablename")+"]", "", "移交条目", request.getRemoteAddr(), new Date());
				logMapper.insert(logPO);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
	}
	/**
	 * 移交电子条目
	 * @param outDto
	 * @param path 
	 * @param session 
	 * @param response 
	 * @param request 
	 */
	public List<Archive_tablefieldlistPO> getExcelTitle(Dto qDto) {
		// TODO Auto-generated method stub	
			// String query = queryConditions(qDto);
		
			String sql = "WITH aos_query_ AS (SELECT " + qDto.getString("enfield") + " FROM "
					+ qDto.getString("tablename") + " WHERE 1=1 "
					+ qDto.getString("query") + " ) " + "SELECT * FROM aos_query_ ";
			List<Archive_tablefieldlistPO> listDto = convertMapListToArrayList(jdbcTemplate
					.queryForList(sql));
			return listDto;
	}
	public Dto deleteAllData(Dto qDto) {
		// TODO Auto-generated method stub
		Dto outDto = Dtos.newDto();
		String tablename = qDto.getString("tablename");
		//查询条目数
		List<Map<String,Object>> list = jdbcTemplate.queryForList("select * from "+tablename+" where 1=1 "+qDto.getString("query"));
		try {
			jdbcTemplate.execute(" delete from " + tablename + " where 1=1  "+qDto.getString("query"));
			String msg = "操作完成,";
			msg = AOSUtils.merge(msg + "成功删除信息[{0}]个",list.size());
			outDto.setAppMsg(msg);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			outDto.setAppMsg("删除失败!");
		}
		return outDto;
	}
	/**
	 * 获取当前选中条目的数据信息
	 * 
	 * @author PX
	 * @param out
	 *
	 * 2019-1-25
	 * @return 
	 */
	public Map<String, Object> firstcheckOpen(Dto out) {
		// TODO Auto-generated method stub
		String id_=out.getString("id_").split(",")[0];
		String tablename=out.getString("tablename");
		String limit=out.getString("limit");
		String page=out.getString("page");
		String query=out.getString("query");
		String index=out.getString("index");
		String sql="select * from "+tablename+" where 1=1 and id_='"+id_+"'";
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		if(list!=null&&list.size()>0){
			Map<String, Object> map = list.get(0);
			map.put("limit",limit);
			map.put("page",page);
			map.put("query",query);
			map.put("index",index);
			return map;
		}
		return null;
		
	}
	/**
	 * 根据pdf标志和条目id值得到pdf的文件信息
	 * 
	 * @param fileid
	 * @param oldfilename
	 * @param tablename
	 * @return
	 */
	public List<Map<String, Object>> find_path2pdf(String fileid,String tablename) {
		// TODO Auto-generated method stub
		String sql = "select * from " + tablename + "_path " + "where tid='"
				+ fileid +"'";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		return list;
	}
	public Dto getData1(Dto qDto) {
		Dto outDto = Dtos.newDto();
		String tablename = qDto.getString("tablename");
		String id = qDto.getString("id_");
		String sql = "SELECT * FROM " + tablename + " WHERE id_='" + id + "'";
		List<Dto> listDto = convertMapListToArrayList(jdbcTemplate
				.queryForList(sql));
		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql);
		if (queryForList != null && queryForList.size() > 0) {
			Map<String, Object> map = queryForList.get(0);
			Set<String> set = map.keySet();
			Iterator<String> it = set.iterator();
			while (it.hasNext()) {
				String k = it.next();
				Object v = map.get(k);
				outDto.put(k, v);
			}
		}
		return outDto;
	}
	/**
	 * 
	 * 初检数据信息查询
	 * 
	 * @param qDto
	 * @return
	 */
	public List<Dto> getDataFieldListDisplayAll2(Dto qDto, String username) {
		String query = qDto.getString("query");
		Integer limit = Integer.valueOf(qDto.getString("limit"));
		Integer page = Integer.valueOf(qDto.getString("page"));
		// 将条件存入到一个数据表中，方便与缓存查询
		String selectmark = qDto.getString("selectmark");
		if (selectmark.equals("1")) {
			// 存入缓存表中
			addSearchData(qDto, username);
		}
		String sql = "";
		String term = "";
		String fieldName;
		String enfield = "id_";
		List<Archive_tablefieldlistPO> list = archive_tablefieldlistMapper
				.getDataFieldDisplayAll(qDto.getString("tablename"));
		String orderenfield = "";
		for (int i = 0; i < list.size(); i++) {
			fieldName = list.get(i).getFieldenname();
			enfield = enfield + "," + fieldName;
			if (i == 3) {
				orderenfield = enfield;
			}
		}
		orderenfield = orderenfield.substring(4);
		// 到这一步进行全宗筛选
		Map<String, Object> map = findGeneral(username);
		if (map != null && map.size() > 0) {
			String qzh = (String) map.get("generalnumber");
			term = " and qzh='" + qzh + "'";
		} else {
			term = term;
		}
		// 排列条件传递过去
		String orderBy = getOrderBy(qDto,username);
		if (orderBy!=null) {
			if ( orderenfield!=null) {
				orderBy = "," + orderBy;
			}
		}else{
			orderBy="";
		}
		if (qDto.getString("page") != null && qDto.getString("page") != "") {
			sql = "WITH aos_query_ AS (SELECT TOP 100 PERCENT ROW_NUMBER () OVER (ORDER BY "
					+ orderenfield
					+ orderBy
					+ ") AS aos_rn_,"
					+ enfield
					+ " FROM "
					+ qDto.getString("tablename")
					+ " WHERE "
					+ qDto.getString("queryclass")
					+ " and _xh is null "
					+ query
					+ term
					+ ") "
					+ "SELECT * FROM aos_query_ WHERE aos_rn_ BETWEEN "
					+ (limit * (page - 1) + 1)
					+ " AND "
					+ qDto.getPageLimit()
					* Integer.valueOf(qDto.getString("page"))
					+ " ORDER BY aos_rn_";
		} else {
			sql = "WITH aos_query_ AS (SELECT TOP 100 PERCENT ROW_NUMBER () OVER (ORDER BY "
					+ orderenfield
					+ orderBy
					+ ") AS aos_rn_,"
					+ enfield
					+ " FROM "
					+ qDto.getString("tablename")
					+ " WHERE "
					+ qDto.getString("queryclass")
					+ " and _xh is null "
					+ query
					+ term
					+ ") "
					+ "SELECT * FROM aos_query_  ORDER BY aos_rn_";
		}
		List listDto = jdbcTemplate.queryForList(sql);
		return listDto;
	}
	/**
	 * 存入查询缓存
	 * 
	 * @author PX
	 * @param qDto
	 * @param username2
	 * 
	 *            2019-1-14
	 */
	private void addSearchData(Dto qDto, String username) {
		// TODO Auto-generated method stub
		// 得到当前时间毫秒
		//查询用户中文名
		//{columnnum=7, filedname8=, filedname7=, filedname4=, filedname3=, filedname6=, filedname5=, 
		//page=1, and6=on, and5=on, and4=on, and3=on, and2=on, and1=on,and7=on,  and8=on,
		//filedcnname1=信用代码, condition2=like, condition1=like,
		//filedname1=xydm, content1=, condition8=like, filedname2=, content2=, condition7=like, content3=, 
		//content4=, condition4=like, content5=, queryclass= 1=1, selectmark=1, condition3=like, content6=, condition6=like, content7=, condition5=like, content8=}
		String chinese = getUserChinese(username);
		long nowtime = System.currentTimeMillis();
		SimpleDateFormat dfDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String bh = dfDateFormat.format(new Date());
		for (int q = 1; q <= 8; q++){
			String filedname = qDto.getString("filedname" +q);
			String filedcnname = qDto.getString("filedcnname" + q);
			String and = qDto.getString("and" +q);
			String content = qDto.getString("content" + q);
			String condition = qDto.getString("condition" +q);
			String sql = "insert into archive_Reseach_Data(bh,gx,zdmc,nr,tj,tablename,mk,cxr,mk_en) values ('"
					+ bh
					+ "','"
					+ and
					+ "','"
					+ filedname
					+ "','"
					+ content
					+ "','"
					+ condition
					+ "','"
					+ qDto.getString("tablename")
					+ "','" + chinese + "','"+filedcnname+"','"+username+"')";
			// 存入到数据库中
			jdbcTemplate.execute(sql);
		}
	}
	/**
	 * 得到中文登录名
	 * 
	 * @author PX
	 * @param username2
	 *
	 * 2019-1-14
	 * @return 
	 */
	private String getUserChinese(String username2) {
		// TODO Auto-generated method stub
		String sql="select * from aos_sys_user where account_='"+username2+"'";
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		if(list!=null&&list.size()>0){
			String name_=(String)list.get(0).get("name_");
			return name_;
		}
		return null;
	}
	/**
	 * 初检保存
	 * 
	 * @author PX
	 * @param userInfoVO 
	 * @param dto
	 * @return
	 *
	 * 2019-2-13
	 */
	public Dto firstchecksaveData(Dto qDto, UserInfoVO userInfoVO) {
		// TODO Auto-generated method stub
		Dto outDto = Dtos.newDto();
		String tablename = qDto.getString("tablename");
		// Map map = new HashMap();
		String id_ = qDto.getString("id").split(",")[0];
		// String _lrr=outDto.getUserInfo().getName_();
		// String _lrrq="";
		String fieldName="";
		//判断是否存在初检人和初检日期
		List<Archive_tablefieldlistPO> list = archive_tablefieldlistMapper
				.getDataFieldDisplayAll(qDto.getString("tablename"));
		String orderenfield = "";
		for (int i = 0; i < list.size(); i++) {
			fieldName += list.get(i).getFieldenname()+",";
		}
		if(!fieldName.contains("_cjr")||!fieldName.contains("_cjrq")){
			Dto out =Dtos.newDto();
			out.setAppCode(-1);
			return out;
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		String date = df.format(new Date());
		String user_ = userInfoVO.getName_();
		String sql="update "+tablename+" set _cjr='"+user_+"' , _cjrq='"+date+"' where id_='"+id_+"'";
		jdbcTemplate.execute(sql);
		// List<Archive_tableinputPO> list = archive_tableinputMapper.
		outDto.setAppCode(AOSCons.SUCCESS);
		outDto.setAppMsg("操作完成，初检保存成功。");
		return outDto;
	}
	/**
	 * 查找该用户全宗
	 * 
	 * @author PX
	 * @param username2
	 * 
	 *            2018-11-8
	 * @return
	 */
	private Map<String, Object> findGeneral(String username2) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		String sql = "select * from archive_general where username='"
				+ username2 + "'";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		if (list != null && list.size() > 0) {
			map = list.get(0);
		}
		return map;
	}
	/**
	 * 
	 * 获取排列条件(根据表名和用户名)
	 * 
	 * @author PX
	 * @param qDto
	 * 
	 *            2019-1-7
	 * @param username 
	 * @return
	 */
	private String getOrderBy(Dto qDto, String username) {
		// TODO Auto-generated method stub
		String sql = "select * from archive_Sort_Data where tablename='"
				+ qDto.getString("tablename") + "' and cxr='"+username+"'";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		if (list.size()>0) {
			return list.get(0).get("nr").toString();
		} else {
			return null;
		}
	}
	/**
	 * 根据id查询tm
	 * 
	 * @author PX
	 * @param string
	 * @param string2
	 *
	 * 2019-4-16
	 * @return 
	 */
	public String getTm(String tablename, String id) {
		// TODO Auto-generated method stub
		String sqlString="select * from "+tablename+" id_='"+id+"'";
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sqlString);
		String tm=(String) list.get(0).get("tm");
		return tm;
	}
}
