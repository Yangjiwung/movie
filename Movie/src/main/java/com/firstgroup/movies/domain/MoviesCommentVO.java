package com.firstgroup.movies.domain;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class MoviesCommentVO {

	private Long comBno;		// 댓글 식별번호
	private Long movBno;		// 영화 식별 키

	private String content;		// 댓글 내용
	private Long memBno;		// 멤버 키
	private int stars;			// 평점
	private int recommend;		// 추천수
	private Date regDate;		// 작성일
	private Date upDate;		// 수정일
	
	// 이너조인
	private String name;		// 유저이름
	private String nickName;	// 유저 닉네임
	private Long memImgNo;		// 유저 이미지 번호
	private List<MemberVO> memberList; // 유저 이미지리스트
	
}
