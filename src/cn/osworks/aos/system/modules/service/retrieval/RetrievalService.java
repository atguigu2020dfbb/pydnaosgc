package cn.osworks.aos.system.modules.service.retrieval;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.sun.xml.internal.fastinfoset.sax.Properties;

import cn.osworks.aos.core.asset.AOSJson;
import cn.osworks.aos.core.id.AOSId;
import cn.osworks.aos.core.typewrap.Dto;
import cn.osworks.aos.core.typewrap.Dtos;
import cn.osworks.aos.system.modules.service.archive.DataService;
/**
 * 
 * 全文检索服务
 * 
 * @author Sun
 *
 */
@Service
public class RetrievalService{

	@Autowired
	private DataService dataService;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * 增加索引
	 * 
	 * @throws Exception
	 */
	public static void insertIndex(Dto qDto) throws Exception {
		java.util.Properties prop = PropertiesLoaderUtils.loadAllProperties("config.properties");
		String lucenIndex =prop.getProperty("filePath")+"/luceneIndex";
		File file = new File(lucenIndex);
		if(file.exists()){
			file.mkdirs();
		}
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
		Directory directory = FSDirectory.open(new File(file.toString()));

		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36,
				analyzer);
		IndexWriter indexWriter = new IndexWriter(directory, config);

		Document doc = new Document();
		// indexWriter.addDocument(doc1);
		File pdfFile = new File(qDto.getString("fileName"));
		PDFTextStripper stripper = new PDFTextStripper();
		PDDocument pddocument = PDDocument.load(pdfFile);
		String body = stripper.getText(pddocument);
		// Dto tm = dataService.selectOne(id,tablename);
		Field fieldid = new Field("id_", qDto.getString("tid"), Field.Store.YES,
				Field.Index.ANALYZED, TermVector.WITH_POSITIONS_OFFSETS);
		Field pathid = new Field("pathid", qDto.getString("pathid"), Field.Store.YES,
				Field.Index.ANALYZED, TermVector.WITH_POSITIONS_OFFSETS);
		Field fieldbody = new Field("body", body, Field.Store.YES,
				Field.Index.ANALYZED, TermVector.WITH_POSITIONS_OFFSETS);
		doc.add(fieldid);
		//doc.add(fieldtm);
		doc.add(fieldbody);
		doc.add(pathid);
		indexWriter.addDocument(doc);
		indexWriter.commit();
		indexWriter.close();
	}

	/**
	 * 删除索引
	 * 
	 * @param str
	 *            删除的关键字
	 * @throws Exception
	 */
	public static void delete(String idexDir) throws Exception {
		Date date1 = new Date();
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
		Directory directory = FSDirectory.open(new File(idexDir));

		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36,
				analyzer);
		IndexWriter indexWriter = new IndexWriter(directory, config);

		// indexWriter.deleteDocuments(new Term("filename", str));

		indexWriter.close();

		Date date2 = new Date();
		System.out.println("删除索引耗时：" + (date2.getTime() - date1.getTime())
				+ "ms\n");
	}

	/**
	 * 更新索引
	 * 
	 * @throws Exception
	 */
	public static void update(String idexDir) throws Exception {
		String text1 = "update,hello,man!";
		Date date1 = new Date();
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
		Directory directory = FSDirectory.open(new File(idexDir));

		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36,
				analyzer);
		IndexWriter indexWriter = new IndexWriter(directory, config);

		Document doc1 = new Document();
		// doc1.add(new TextField("filename", "text1", Store.YES));
		// doc1.add(new TextField("content", text1, Store.YES));

		// indexWriter.updateDocument(new Term("filename", "text1"), doc1);

		indexWriter.close();

		Date date2 = new Date();
		System.out.println("更新索引耗时：" + (date2.getTime() - date1.getTime())
				+ "ms\n");
	}
	
	/**
	 * 
	 * 通过主键查询数据
	 * 
	 * @author Sun
	 * @param id
	 * @return
	 *
	 * 2018-8-22
	 */
	public Dto selectId(String id){
		String sql=" SELECT dh,tm,zrz,bgqx,nd,dalb,tablename FROM ALL_TABLE WHERE ID_='"+id+"'";
		Dto qDto=Dtos.newDto();
		try {
				qDto = dataService.convertMapListToDto(jdbcTemplate.queryForList(sql));
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return   qDto;
	}
	
	//添加电子文件全部索引
	public void addIndex() throws IOException{
		//tid  主表主建
		
				//pathid 电子表主键
				
				//filename 电子文件路径
				
				//
				String sql="SELECT  path,tid,id_ From allpath";
				List<Map<String, Object>> listDto = jdbcTemplate.queryForList(sql);
				
				for(int i=0;i<listDto.size();i++){
					
					String Filepath = listDto.get(i).get("path").toString();
					String strpathid=listDto.get(i).get("id_").toString();
					String tid=listDto.get(i).get("tid").toString();
					//String strpathid=AOSId.uuid();
					java.util.Properties prop = PropertiesLoaderUtils.loadAllProperties("config.properties");
					String lucenIndex =prop.getProperty("filePath")+"/luceneIndex";
					File file = new File(lucenIndex);
					if(file.exists()){
						file.mkdirs();
					}
					Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
					Directory directory = FSDirectory.open(new File(file.toString()));

					IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36,
							analyzer);
					IndexWriter indexWriter = new IndexWriter(directory, config);

					Document doc = new Document();
					// indexWriter.addDocument(doc1);
					File pdfFile = new File(Filepath);
					PDFTextStripper stripper = new PDFTextStripper();
					PDDocument pddocument = PDDocument.load(pdfFile);
					String body = stripper.getText(pddocument);
					// Dto tm = dataService.selectOne(id,tablename);
					Field fieldid = new Field("id_", tid, Field.Store.YES,
							Field.Index.ANALYZED, TermVector.WITH_POSITIONS_OFFSETS);
					Field pathid = new Field("pathid", strpathid, Field.Store.YES,
							Field.Index.ANALYZED, TermVector.WITH_POSITIONS_OFFSETS);
					Field fieldbody = new Field("body", body, Field.Store.YES,
							Field.Index.ANALYZED, TermVector.WITH_POSITIONS_OFFSETS);
					doc.add(fieldid);
					//doc.add(fieldtm);
					doc.add(fieldbody);
					doc.add(pathid);
					indexWriter.addDocument(doc);
					indexWriter.commit();
					indexWriter.close();
				}
				
				
				
		
	}
	
	
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	
}
