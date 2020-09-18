package cn.osworks.aos.system.modules.service.batchUpload;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import cn.osworks.aos.core.typewrap.Dto;
import cn.osworks.aos.core.typewrap.Dtos;
import cn.osworks.aos.system.dao.mapper.Archive_tablenameMapper;
import cn.osworks.aos.system.dao.po.Archive_tablenamePO;
/**
 * 
 * 批量挂接
 * 
 * @author Sun
 *
 * 2018-10-20
 */
@Service
public class BatchUploadService {
	
	@Autowired
	private Archive_tablenameMapper archive_tablenameMapper;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public List<Dto> getCount(Dto qDto) {
		
		List<Archive_tablenamePO> list = archive_tablenameMapper.list(qDto);
		List<Dto> listCount =new ArrayList<Dto>();
		for(int i=0;i<list.size();i++){
			Dto inDto = Dtos.newDto();
			int mls=jdbcTemplate.queryForInt(" select count(*) from " + list.get(i).getTablename());
			int ygj=jdbcTemplate.queryForInt(" select count(*) from " + list.get(i).getTablename()+" where _path>0");
			int a=mls-ygj;
			
			inDto.put("tablename", list.get(i).getTablename());
			inDto.put("tabledesc", list.get(i).getTabledesc());
			inDto.put("mls", mls);
			inDto.put("ygj", ygj);
			inDto.put("wgj", mls-ygj);
			
			listCount.add(inDto);
			
		}
		
		//System.out.print(list.get(0).getTablename());
		
		return listCount;
	}

}
