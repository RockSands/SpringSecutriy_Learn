package com.service;

import java.util.List;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mapper.batch.BatchVo;

@Service
public class BatchExcuteService {
	@Autowired
	private SqlSessionFactory sqlSessionFactory;

	public void excuteBatch(List<BatchVo> vos) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
			for (BatchVo vo : vos) {
				if (vo == null) {
					continue;
				}
				switch (vo.getSqlType()) {
				case INSERT:
					session.insert(vo.getSqlKey(), vo.getObj());
					break;
				case UPDATE:
					session.update(vo.getSqlKey(), vo.getObj());
					break;
				case DELETE:
					session.delete(vo.getSqlKey(), vo.getObj());
					break;
				default:
				}
			}
			session.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}

	}
}
