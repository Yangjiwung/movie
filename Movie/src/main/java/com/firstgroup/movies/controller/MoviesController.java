package com.firstgroup.movies.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.firstgroup.movies.domain.ActorVO;
import com.firstgroup.movies.domain.Criteria;
import com.firstgroup.movies.domain.DirectorVO;
import com.firstgroup.movies.domain.ImgVO;
import com.firstgroup.movies.domain.MemberVO;
import com.firstgroup.movies.domain.MoviesCommentVO;
import com.firstgroup.movies.domain.MoviesVO;
import com.firstgroup.movies.security.domain.CustomUser;
import com.firstgroup.movies.service.ActorService;
import com.firstgroup.movies.service.DirectorService;
import com.firstgroup.movies.service.ImgService;
import com.firstgroup.movies.service.MemberService;
import com.firstgroup.movies.service.MoviesService;
import com.firstgroup.movies.util.Utility;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
@AllArgsConstructor
public class MoviesController {
	
	@Setter(onMethod_ = @Autowired)
	private MoviesService movService; 	// 영화 서비스
	@Setter(onMethod_ = @Autowired)
	private MemberService memService; 	// 유저 서비스
	@Setter(onMethod_ = @Autowired)
	private ImgService imgService;		// 이미지 서비스
	@Setter(onMethod_ = @Autowired)
	private ActorService actService;	// 액터 서비스
	@Setter(onMethod_ = @Autowired)
	private DirectorService dirService;	// 디렉터 서비스
	@Setter(onMethod_ = @Autowired)		
	private Utility util;				// 공통적으로 사용할 Util 서비스
	

	// 댓글 삭제
   @DeleteMapping("/movies/deleteComment/{comBno}")
   public String removeComment(@PathVariable Long comBno) {
       log.info("삭제할 댓글의 번호 : " + comBno);
       movService.removeComment(comBno);
       
       return comBno.toString();
   }

   // 댓글 수정
   @PostMapping("/movies/modifyComment/{comBno}")
   public String updateComment(@RequestBody MoviesCommentVO vo) {
	   log.info("수정할 댓글 번호 : " + vo.getComBno());
	   log.info("수정할 댓글 내용 : " + vo.getContent());
	   
	   movService.modifyComment(vo);
	   
	   return vo.toString();
   }
	   

	@PostMapping(value = "/regComment") 
	public ResponseEntity<String> insertComment(@RequestParam Map<String,String> formData) { // getMovie 댓글 폼을 이용하여 Map으로 <key, value>로 받음
	    MoviesCommentVO vo = new MoviesCommentVO();
	    vo.setMemBno(Long.parseLong(formData.get("memBno"))); // vo객체에 form data(Stirng)를 Long다음으로 변환하여 넣음
	    vo.setMovBno(Long.parseLong(formData.get("movBno")));// vo객체에 form data를 Long다음으로 변환하여 넣음
	    vo.setContent(formData.get("content"));	// String 으로 넘어온 값을 vo에 셋팅 
	    vo.setStars(Integer.parseInt(formData.get("stars"))); // int형으로 타입변환 

	    log.info("---------------댓글 작성 객체 확인 : "+vo);
	    // int형으로 리턴값을 받아 성공하면 1 실패하면 0을 삼항 연산자를 통해 true일 경우 success메세지를 실패시 에러를 반환
	    return movService.insertComment(vo) == 1? new ResponseEntity<>("success", HttpStatus.OK) : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@GetMapping(value="/movies/getMovie/{movbno}")
	public ModelAndView getMovie(@PathVariable Long movbno,@AuthenticationPrincipal Model model){
		movService.views(movbno);// 컨트롤러를 통해 Movie에 들어올 때마다 movBno를 받아 조회수 1씩 증가
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/getMovie"); // /getMovie의 jsp파일을 movbno 가지고 찾아감
		MoviesVO mov = movService.get(movbno); // 영화 1개의 정보를 전부 불러옴
		mov.setImgList(imgService.findByBno("tbl_movies_img", movbno)); // ImgVO에서 tbl_movies_img테이블의 movbno번 이미지를 모두 불러와 MoviesVO imgList에 저장
		mov.setTitleList(imgService.findByBno("tbl_title_img", movbno)); // ImgVO에서 tbl_title_img테이블의 movbno번 이미지를 모두 불러와 MoviesVO imgList에 저장
		mov.setContentList(imgService.findByBno("tbl_content_img", movbno)); // ImgVO에서 tbl_content_img테이블의 movbno번 이미지를 모두 불러와 MoviesVO imgList에 저장
		
		long[] tmp = util.parseToList(mov.getActor());	// String 형태의 배열을 잘라서 long[]에 넣음
		
		List<ActorVO> actorList = new ArrayList<>(); // 배우 정보를 리스트로 새로 만들어줌
		for(long tp : tmp) { // long 타입의 tp변수를 잘라온 배열만큼 돌려 list에 저장 배우 정보 하나씩 불러옴
			actorList.add(actService.getActor(tp));
		}
		for(ActorVO vo: actorList) { // ActorVO에 이미지를 따로 불러옴
			vo.setImgList(imgService.findByBno("tbl_actor_img", 
					vo.getActbno())==null?new ArrayList<ImgVO>():imgService.findByBno("tbl_actor_img", vo.getActbno())); 
			// 배우 이미지를 저장시 배우bno가 없을 시 새 리스트를 만들고 있을 시 배우의 이미지를 불러온다
		}
		mov.setActList(actorList); // MoviesVO에 가져온 actorList를 저장
		
		tmp = util.parseToList(mov.getDirector());	// String 형태의 배열을 잘라서 long[]에 넣음
		
		List<DirectorVO> directorList = new ArrayList<>(); // 감독 정보를 불러오기 전 새로운 객체를 생성 List초기화 필요
		for(long tp : tmp) {
			directorList.add(dirService.getDirector(tp)); // 배열의 index만큼 배우 정보를 불러옴
		}
		log.info(directorList);
		for(DirectorVO vo: directorList) { // 감독의 이미지를 찾아 저장
			log.info(imgService.findByBno("tbl_director_img", vo.getDirBno()));
			List<ImgVO> tp = imgService.findByBno("tbl_director_img", vo.getDirBno());
			log.info(tp);
			vo.setImgList(imgService.findByBno("tbl_director_img", vo.getDirBno())==null?new ArrayList<ImgVO>():imgService.findByBno("tbl_director_img", vo.getDirBno())); 
		}
		mov.setDirList(directorList); // MoviesVO에 위에서 가져온 감독과 감독 이미지를 저장
		
		mv.addObject("movie", mov); // Model영역에 저장
		
		// 현재 사용자의 권한을 확인함
		String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
		if(role.equals("[ROLE_ANONYMOUS]")) { // 권한이 없는 비회원 상태일시 아무런 작업을 수행하지 않는다.
			
		} else { // 권한이 있는사용자일시 CustomUser객체에 저장하기 위함
			
			// 사용자의 권한을 확인 하여 넣음
			CustomUser user = (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			MemberVO member = user.getMember(); // 현재 사용자의 권한을 MemberVO에 저장
			String id = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString(); // principal 사용자의 id를 String id에 저장 user.id를 사용하기위함
			MemberVO memVo = memService.getMember(member.getId()); // Member객체에 일치시켜줌
			model.addAttribute("user", memVo); // model영역에 객체 저장
		}
		return mv;
	}
	
	// 영화정보입력 페이지를 불러오기위한 getMapping
	@GetMapping("/movies/register")
	public ModelAndView movieRegister(Model model) { 
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/movies/register");
		log.info("movie register........");
		// diretor정보를 이미지와 함께 불러옴
		List<DirectorVO> directorList = dirService.directorList(); // 감독 정보를 전부 출력하여 directorList 변수에 넣음
		for(DirectorVO vo: directorList) { // DirectorVO 객체에 directorList 불러온 List의 index만큼 이미지를 저장
			vo.setImgList(imgService.findByBno("tbl_director_img", vo.getDirBno()));
	    }
 		model.addAttribute("directorList", directorList);	// model영역에 저장
		
		// actor정보를 이미지와 함께 불러옴
		List<ActorVO> actorList = actService.actorList();// 감독 정보를 전부 출력하여 actorList 변수에 넣음
		for(ActorVO vo: actorList) { // ActorVO 객체에 actorList 불러온 List의 index만큼 이미지를 저장
			vo.setImgList(imgService.findByBno("tbl_actor_img", vo.getActbno()));
	    }
		model.addAttribute("actorList", actorList);	// model영역에 저장
		return mv;
	}	
	
	@PostMapping("/movies/register")
	public String movieRegisterAction(@RequestBody MoviesVO mov) { // 폼에서 입력한 데이터와 다수의 데이터를 List로 묶어서 가져옴

		log.info("movie register action..............");
		log.info(mov);
		 
		 // 리스트가 비어있지 않은 경우에만 String.join() 메소드 사용 
		 if (!mov.getActorList().isEmpty()) { // 리스트로 들어온 value를 ,를 붙여 합쳐줌 
		 //genre.stream().collect(Collectors.joining(","));
			//[actorbno, actorbno, actorbno] 들어온 값 String으로 ,를 붙여 만들어줌
			 mov.setActor(mov.getActorList().stream().collect(Collectors.joining(",")));
			 //diretor
			 mov.setDirector(mov.getDirectorList().stream().collect(Collectors.joining(",")));
		  }
		 movService.registerMovies(mov);
		 
		for (ImgVO img : mov.getImgList()) {	// 넘어온 이미지List만큼
			img.setBno(mov.getMovBno());	// img객체에 bno값을 movbno값이랑 일치 시켜주기 위해 셋팅
			img.setTblName("tbl_movies_img");	// 테이블 이름 셋팅
			log.info(img);
			imgService.insert(img);	// img테이블에 insert
		}
		
		for (ImgVO img : mov.getTitleList()) { 
			img.setBno(mov.getMovBno());
			img.setTblName("tbl_title_img");
			log.info(img);
			imgService.insert(img);
		}
		
		for (ImgVO img : mov.getContentList()) {
			img.setBno(mov.getMovBno());
			img.setTblName("tbl_content_img");
			log.info(img);
			imgService.insert(img);
		}
		return mov.getMovBno().toString();
	}
	
	@GetMapping("/movies/list")
	public ModelAndView movieList(Criteria cri, Model model) { // 페이징처리 미구현
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/movies/movieList");
		
		List<MoviesVO> movieList = movService.getMovieList(); // 테이블에 있는 모든 영화 정보를 MovieVO객체 List에 저장
		 for(MoviesVO vo: movieList) {
			// 테이블에 있는 모든 영화의 이미지를 이미지 테이블의 bno와 비교하여 일치시킴
	        vo.setImgList(imgService.findByBno("tbl_movies_img", vo.getMovBno())); 
	      }
		model.addAttribute("movieList",movieList); // model영역에 저장
		return mv;
	}
	// 현재 미사용
	@DeleteMapping("/movies/remove/{movBno}")
	public ResponseEntity<String> remove(@PathVariable Long movBno) {
	    // movBno를 사용하여 영화 삭제 로직을 수행
	    log.info("삭제할 영화 게시물 번호: " + movBno);
	    // 삭제 성공시 int형을 리턴 1이면 true success를 리턴 실패시 0 에러를 리턴 하여 응답
		return movService.removeMovie(movBno) == 1 ? new ResponseEntity<>("success", HttpStatus.OK)
				: new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@GetMapping("/movies/remove/{movBno}")
	public ModelAndView delete(@PathVariable Long movBno, HttpServletResponse response) {
	    // movBno를 사용하여 영화 삭제 로직을 수행
	    log.info("삭제할 영화 게시물 번호: " + movBno);
	    movService.removeMovie(movBno); // 영화 삭제할 메서드를 호출 하여 movBno를 파라미터 값으로 넘겨줌
	    
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/movies/movieList");
		
		try {
			response.sendRedirect("/movies/list"); // 작업 수행 후 movieList 컨트롤러로 보내줌
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mv;
	}

	//---03.30
 	@GetMapping("/movies/modify/{movbno}")
 	public ModelAndView updatePage(@PathVariable Long movbno, Model model) {
 		ModelAndView mv = new ModelAndView();
 		mv.setViewName("/movies/modify");
 		log.info("updatePage : " + movbno);
		MoviesVO mov = movService.get(movbno); // movbno를 받아 1개의 영화를 불러옴
		// 영화의 이미지들을 셋팅
		mov.setImgList(imgService.findByBno("tbl_movies_img", movbno)); 
		mov.setTitleList(imgService.findByBno("tbl_title_img", movbno));
		mov.setContentList(imgService.findByBno("tbl_content_img", movbno));
 		model.addAttribute("movie",mov); // model영역에 저장
 		model.addAttribute("actorList", actService.actorList());
		return mv;
 	}
 	
 	@PostMapping("/movies/modify")
 	public ModelAndView modify(@RequestBody MoviesVO mov) {

 		 // 리스트가 비어있지 않은 경우에만 String.join() 메소드 사용 
		 if (!mov.getActorList().isEmpty()) { // 리스트로 들어온 value를 ,를 붙여 합쳐줌 
			 //genre.stream().collect(Collectors.joining(","));
				 
				 //[actorbno, actorbno, actorbno] 들어온 값 String으로 ,를 붙여 만들어줌
				 mov.setActor(mov.getActorList().stream().collect(Collectors.joining(",")));
				 mov.setDirector("테스트용 수정감독");
			  }
			ImgVO tmp = mov.getImgList().get(0); // 첫번째 인덱스 이미지를 tmp에저장
			tmp.setBno(mov.getMovBno());		// 테이블 번호 셋팅
			tmp.setTblName("tbl_movies_img");	// 테이블 이름 세팅
			imgService.delete(tmp);				// 기존 데이터 삭제
			tmp.setTblName("tbl_title_img");
			imgService.delete(tmp);
			tmp.setTblName("tbl_content_img");
			imgService.delete(tmp);
			for (ImgVO img : mov.getImgList()) { 	// register에 있는 코드 재활용 이미지 넣는 과정
				img.setBno(mov.getMovBno());	 	// 테이블 bno셋팅
				img.setTblName("tbl_movies_img");	// 테이블 이름 셋팅
				log.info(img);
				imgService.insert(img);				// img객체 셋팅하여 insert작업 숫행
				mov.setMovImgNo(img.getBno());		// 이미지의 객체를 Movies객체에 셋팅
			}
			for (ImgVO img : mov.getTitleList()) {
				img.setBno(mov.getMovBno());
				img.setTblName("tbl_title_img");
				log.info(img);
				imgService.insert(img);
			}
			
			for (ImgVO img : mov.getContentList()) {
				img.setBno(mov.getMovBno());
				img.setTblName("tbl_content_img");
				log.info(img);
				imgService.insert(img);
			}
			movService.updateMovies(mov); // 모든 셋팅을 마친 후 객체를 업데이트
			// 페이지 이동
			ModelAndView mv = new ModelAndView();
			mv.setViewName("/movies/movieList"); // Movielist 페이지로 이동
			return mv;
 		
 	}


	
}
