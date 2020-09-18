package cn.osworks.aos.system.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.osworks.aos.core.annotation.Mapper;
import cn.osworks.aos.core.typewrap.Dto;
import cn.osworks.aos.system.dao.po.Act_procdef_infoPO;

/**
 * <b>ACT_PROCDEF_INFO[act_procdef_info]数据访问接口</b>
 * 
 * <p>
 * 注意:此文件由AOS平台自动生成-禁止手工修改
 * </p>
 * 
 * @author AHei
 * @date 2018-12-18 15:50:46
 */
@Mapper
public interface Act_procdef_infoMapper {

	/**
	 * 插入一个数据持久化对象(插入字段为传入PO实体的非空属性)
	 * <p> 防止DB字段缺省值需要程序中再次赋值
	 *
	 * @param act_procdef_infoPO
	 *            要插入的数据持久化对象
	 * @return 返回影响行数
	 */
	int insert(Act_procdef_infoPO act_procdef_infoPO);
	
	/**
	 * 插入一个数据持久化对象(含所有字段)
	 * 
	 * @param act_procdef_infoPO
	 *            要插入的数据持久化对象
	 * @return 返回影响行数
	 */
	int insertAll(Act_procdef_infoPO act_procdef_infoPO);

	/**
	 * 根据主键修改数据持久化对象
	 * 
	 * @param act_procdef_infoPO
	 *            要修改的数据持久化对象
	 * @return int 返回影响行数
	 */
	int updateByKey(Act_procdef_infoPO act_procdef_infoPO);

	/**
	 * 根据主键查询并返回数据持久化对象
	 * 
	 * @return Act_procdef_infoPO
	 */
	Act_procdef_infoPO selectByKey(@Param(value = "id_") String id_);

	/**
	 * 根据唯一组合条件查询并返回数据持久化对象
	 * 
	 * @return Act_procdef_infoPO
	 */
	Act_procdef_infoPO selectOne(Dto pDto);

	/**
	 * 根据Dto查询并返回数据持久化对象集合
	 * 
	 * @return List<Act_procdef_infoPO>
	 */
	List<Act_procdef_infoPO> list(Dto pDto);

	/**
	 * 根据Dto查询并返回分页数据持久化对象集合
	 * 
	 * @return List<Act_procdef_infoPO>
	 */
	List<Act_procdef_infoPO> listPage(Dto pDto);
		
	/**
	 * 根据Dto模糊查询并返回数据持久化对象集合(字符型字段模糊匹配，其余字段精确匹配)
	 * 
	 * @return List<Act_procdef_infoPO>
	 */
	List<Act_procdef_infoPO> like(Dto pDto);

	/**
	 * 根据Dto模糊查询并返回分页数据持久化对象集合(字符型字段模糊匹配，其余字段精确匹配)
	 * 
	 * @return List<Act_procdef_infoPO>
	 */
	List<Act_procdef_infoPO> likePage(Dto pDto);

	/**
	 * 根据主键删除数据持久化对象
	 *
	 * @return 影响行数
	 */
	int deleteByKey(@Param(value = "id_") String id_);
	
	/**
	 * 根据Dto统计行数
	 * 
	 * @param pDto
	 * @return
	 */
	int rows(Dto pDto);
	
	/**
	 * 根据数学表达式进行数学运算
	 * 
	 * @param pDto
	 * @return String
	 */
	String calc(Dto pDto);
	
}
