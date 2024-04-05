package com.firstgroup.movies.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.firstgroup.movies.domain.ImgVO;
import com.firstgroup.movies.domain.MoviesCommentVO;
import com.firstgroup.movies.domain.MoviesVO;
import com.firstgroup.movies.mapper.MemberMapper;
import com.firstgroup.movies.mapper.MoviesCommentMapper;
import com.firstgroup.movies.mapper.MoviesMapper;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@NoArgsConstructor
public class MoviesServiceImpl implements MoviesService{

	@Setter(onMethod_ = @Autowired)
	private MoviesMapper movMapper;
	
	@Setter(onMethod_ = @Autowired)
	private MoviesCommentMapper comMapper;
	
	@Setter(onMethod_ = @Autowired)
	private MemberMapper memMapper;
	
	@Override
	public MoviesVO get(Long movbno) { 
		
		log.info("불러올 영화 식별 번호 : " + movbno);
		// 영화 상세페이지의 정보와 영화의 댓글을 불러옴
		MoviesVO vo = movMapper.get(movbno);
		vo.setComment(comMapper.getCommentList(movbno));
		
		return vo;
	}


	@Override
	public int insertComment(MoviesCommentVO vo) {
		// 객페를 받아 insert 수행
		log.info("들어온 객체 확인========"+vo);
		int result = comMapper.insertComment(vo);
		return result;
	}


	@Override
	public MoviesVO registerMovies(MoviesVO mov) {
		log.info("영화 등록...........");
		//mov.setMovBno(movMapper.insertSelectKey(mov)); 
		movMapper.insert(mov);
		return mov;
		
	}


	@Override
	public List<MoviesCommentVO> getCommentList(Long movbno) {
		// 댓글 리스트를 전부 불러옴
		return comMapper.getCommentList(movbno);
	}


	@Override
	public int removeComment(Long combno) {
		
		MoviesCommentVO vo = comMapper.getComment(combno);
		
		return comMapper.removeComment(vo.getComBno());
		
	}


	@Override
	public MoviesCommentVO getComment(Long combno) {
		// 미사용 중
		return null;
	}


	@Override
	public List<MoviesVO> getMovieList() {
		//전체 영화 리스트 출력
		return movMapper.getMovieList();
	}


	@Override
	public void updateMovies(MoviesVO mvo) {
		// 영화 정보 수정
		log.info("영화 정보 수정 : " + mvo);
		movMapper.update(mvo);
	}


	@Override
	public int removeMovie(Long movbno) {
		log.info(movbno+"번 게시물 삭제 중......");
		// 삭제 처리후 리턴
		return movMapper.delete(movbno);
		
	}


	@Override
	public List<ImgVO> imgList(Long movbno) {
		// 영화에 이미지 리스트를 셋팅
		return movMapper.imgList(movbno);
	}
	
	@Override
	public void views(Long movbno) {
		// 조회수 업데이트용 
		movMapper.views(movbno);
	}


	@Override
	public List<MoviesCommentVO> bestComment(Long movbno) {
		// 추천수 높은 댓글 3개의 댓글 출력
		return comMapper.bestComment(movbno);
	}

	@Override
	public int modifyComment(MoviesCommentVO vo) {
		log.info(vo.getComBno()+"번 게시물 수정......");
		// 댓글 수정 작업
		return comMapper.modifyComment(vo);
	}

}
