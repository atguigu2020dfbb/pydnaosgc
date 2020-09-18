package cn.osworks.aos.system.modules.service.archive;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import cn.osworks.aos.core.typewrap.Dto;
import cn.osworks.aos.core.typewrap.Dtos;
@Service
public class StatisticsService extends JdbcDaoSupport {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Resource
	public void setJb(JdbcTemplate jb) {
		super.setJdbcTemplate(jb);
	}
	/**
	 * 统计数据
	 * 
	 * @author PX
	 *
	 * 2018-9-19
	 * @return 
	 */
	public List<Dto> getCount() {
		// TODO Auto-generated method stub
		List<Dto> l = new ArrayList<Dto>();
		//统计条目总数
		int c=0;
		//统计电子文件数
		int d=0;
		//统计页数
		int y=0;
		String sql="select * from archive_TableName";
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		if(list!=null&&list.size()>0){
			for(int i=0;i<list.size();i++){
				Dto outDto=Dtos.newDto();
				String tablename=(String)list.get(i).get("TableName");
				String tabledesc=(String)list.get(i).get("TableDesc");
				outDto.put("category", tabledesc);
				int count = count(outDto,tablename);
				int countys = countys(outDto,tablename);
				int countpath = countpath(outDto,tablename);
				c=c+count;
				d=d+countpath;
				y=y+countys;
				l.add(outDto);
			}
		}
		Dto out=Dtos.newDto();
		out.put("category","总计");
		out.put("ajs",c);
		out.put("wjs",d);
		out.put("ajys",y);
		l.add(out);
		return l;
	}
	private int countpath(Dto outDto, String tablename) {
		// TODO Auto-generated method stub
		String sql4="select count(*) from "+tablename+"_path";
		int countpath = jdbcTemplate.queryForInt(sql4);
		outDto.put("wjs", countpath);
		return countpath;
	}
	//操作页数
	private int countys(Dto outDto, String tablename) {
		// TODO Auto-generated method stub
		String sql4="select * from archive_TableFieldList where tid in (select id_ from archive_TableName where TableName='"+tablename+"') and FieldEnName='ys'";
		List<Map<String,Object>> forList = jdbcTemplate.queryForList(sql4);
		int countys =0;
		if(forList!=null&&forList.size()>0){
			String lx=(String)forList.get(0).get("FieldClass");
			if(lx.equals("int")&&lx!=null&&lx!=""){
				String sql3="select sum(ys) from "+tablename;
				countys = jdbcTemplate.queryForInt(sql3);
				outDto.put("ajys", countys);
			}else{
				
				try {
					String sql3="select sum(cast(ys as numeric(12,0))) from "+tablename;
					countys = jdbcTemplate.queryForInt(sql3);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					countys=0;
				}
				outDto.put("ajys", countys);
			}
		}else{
			outDto.put("ajys", 0);
			
		}
		return countys;
	}
	private int count(Dto outDto, String tablename) {
		// TODO Auto-generated method stub
		String sql2="select count(*) from "+tablename;
		int count = jdbcTemplate.queryForInt(sql2);
		outDto.put("ajs",count);
		return count;
	}
	public List<Dto> getDataFieldListTitle(Dto qDto, HttpSession session) {
		// TODO Auto-generated method stub
		List<Dto> listcount=(List<Dto>)session.getAttribute("listcount");
		return listcount;
	}
	/**
	 * 利用统计
	 * 
	 * @author PX
	 *
	 * 2019-4-16
	 * @return 
	 */
	public List<Dto> listmessagestatistics() {
		// TODO Auto-generated method stub
		String sqlString="select * from archive_statistics";
		List<Dto> list = (List)jdbcTemplate.queryForList(sqlString);
		return list;
	}
}
