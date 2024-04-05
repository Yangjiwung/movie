package com.firstgroup.movies.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.firstgroup.movies.domain.Criteria;
import com.firstgroup.movies.domain.MoviesCommentVO;

public interface MoviesCommentMapper {
	// 댓글 하나의 객체
	public MoviesCommentVO getComment(Long combno);

	// 영화에 달린 코멘트 리스트
	public List<MoviesCommentVO> getCommentList(Long movbno);
	
	// 댓글 등록
	public int insertComment(MoviesCommentVO vo);
	
	// 댓글 삭제처리
	public int removeComment(Long combno);
	
	// 댓글리스트 페이징 처리
	public List<MoviesCommentVO> getListWithPage
								(@Param("cri") Criteria cri,
								 @Param("movbno") Long movbno);
	// 추천순 높은 댓글 3개 출력
	public List<MoviesCommentVO> bestComment(Long movbno);
	
	// 댓글 수정
	public int modifyComment(MoviesCommentVO vo);
}
