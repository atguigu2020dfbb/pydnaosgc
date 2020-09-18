package cn.osworks.aos.system.modules.service.dbtable;

import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import cn.osworks.aos.core.asset.AOSCons;
import cn.osworks.aos.core.asset.AOSUtils;
import cn.osworks.aos.core.id.AOSId;
import cn.osworks.aos.core.typewrap.Dto;
import cn.osworks.aos.core.typewrap.Dtos;
import cn.osworks.aos.system.dao.mapper.Archive_tablefieldlistMapper;
import cn.osworks.aos.system.dao.mapper.Archive_tableinputMapper;
import cn.osworks.aos.system.dao.po.Archive_tablefieldlistPO;
import cn.osworks.aos.system.dao.po.Archive_tableinputPO;
import cn.osworks.aos.system.modules.service.archive.DataService;

@Service
public class InputService {
	
	@Autowired
	private DataService dataService;
	@Autowired
	private Archive_tableinputMapper archive_tableinputMapper;
	
	@Autowired
	private Archive_tablefieldlistMapper archive_tablefieldlistMapper;
	
	/**
	 * 
	 * 获取表里最大值
	 * 
	 * @param inDto
	 * @return
	 */
	public int getMaxId(Dto inDto){
		Integer id =archive_tableinputMapper.getMaxId(inDto);
		return id;
		
	}
	
	
	/**
	 * 
	 * 获取下拉列表
	 * 
	 * @param tablename
	 * @return
	 */
	public List<Dto> getFieldsCombo(String tablename){
		List<Dto> listDto=Lists.newArrayList();
		
		List<Archive_tablefieldlistPO> list=archive_tablefieldlistMapper.getFieldsCombo(tablename);
		for(int i=0;i<list.size();i++){
			Dto inDto = Dtos.newDto();
			String fieldenname = list.get(i).getFieldenname();
			String fieldcnname = list.get(i).getFieldcnname();
			inDto.put("fieldenname", fieldenname);
			inDto.put("fieldcnname", fieldcnname);
			listDto.add(i,inDto);
		}
		
		return listDto;
	}
	
	/**
	 * 
	 * 获取下拉列表
	 * 
	 * @param tablename
	 * @return
	 */
	public List<Dto> listFieldInfos(String tablename){
		List<Dto> listDto=Lists.newArrayList();
		
		List<Archive_tablefieldlistPO> list=archive_tablefieldlistMapper.getTableFields(tablename);
		for(int i=0;i<list.size();i++){
			Dto inDto = Dtos.newDto();
			String fieldenname = list.get(i).getFieldenname();
			String fieldcnname = list.get(i).getFieldcnname();
			inDto.put("fieldenname", fieldenname);
			inDto.put("fieldcnname", fieldcnname);
			//inDto.put("FieldClass", fieldcnname);
			//inDto.put("FieldCnName", fieldcnname);
			//inDto.put("FieldCnName", fieldcnname);
			//inDto.put("FieldCnName", fieldcnname);
			//inDto.put("FieldCnName", fieldcnname);
			listDto.add(i,inDto);
		}
		
		return listDto;
	}
	/**
	 * 
	 * 保存录入信息
	 * 
	 * @param inDto
	 */
	public Dto saveInput(Dto inDto){
		Dto outDto = Dtos.newDto();
		JSONArray jsonArray = JSONArray.fromObject(inDto.getString("mydata"));
		archive_tableinputMapper.deleteByFieldname(inDto.getString("tablename"));
		for(int i=0;i<jsonArray.size();i++){
			Archive_tableinputPO archive_tableinputPO=new Archive_tableinputPO();
			 AOSUtils.copyProperties(jsonArray.getJSONObject(i), archive_tableinputPO);
			 archive_tableinputPO.setId(AOSId.uuid());
			 archive_tableinputMapper.insert(archive_tableinputPO);
			 
		}
//		archive_tableinputPO.setId(AOSId.uuid());
		outDto.setAppCode(AOSCons.SUCCESS);
		outDto.setAppMsg("操作完成，保存成功。");
		return outDto;
//		archive_bm_printMapper.insert(archive_bm_printPO);
		
		
	}
	
	/**
	 * 
	 * 携带集合
	 * 
	 * @param inDto
	 */
	public String listYnxd(Dto inDto){
		
		Dto pDto = Dtos.newDto();
		pDto.put("tablename", inDto.getString("tablename"));
		pDto.put("ynxd", "0");
		List<Archive_tableinputPO> list = archive_tableinputMapper.list(pDto);
		String strynxd="1";
		for(int i=0;i<list.size();i++){
			if(i==0){
				strynxd=list.get(i).getFieldname().substring(0, list.get(i).getFieldname().length()-1);
				continue;
			}
			strynxd=strynxd+","+list.get(i).getFieldname().substring(0, list.get(i).getFieldname().length()-1);
		}
		return strynxd;
		
	}
	
	
	public void deleteTablefields(String tablename){
		archive_tableinputMapper.deleteByFieldname(tablename);
		
	}

}
