package com.campus.myapp.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.campus.myapp.dao.MapDAO;
import com.campus.myapp.vo.ReviewVO;
import com.campus.myapp.vo.StoreVO;

@Service
public class MapServiceImpl implements MapService {
	@Inject
	MapDAO dao;

	@Override
	public List<ReviewVO> myReviewListAll(String userid) {
		// TODO Auto-generated method stub
		return dao.myReviewListAll(userid);
	}

	@Override
	public int myreviewDeleteOk(int reviewno) {
		// TODO Auto-generated method stub
		return dao.myreviewDeleteOk(reviewno);
	}
	
	@Override
	public int addplace(StoreVO vo) {
		return dao.addplace(vo);
	}
}