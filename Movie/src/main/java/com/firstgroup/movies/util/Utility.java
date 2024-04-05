package com.firstgroup.movies.util;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Utility {
	public long[] parseToList(String listString){ // String을 받아 long[]배열에 타입 변환하여 넣음
		List<Long> parsingIntList = new ArrayList<Long>(); // 빈 List<long>을 만듬
		String[] valueList = listString.replace(" ","").split(",");	// String[]배열을 만들어 공백 및 , 제거
		for(String value : valueList) {	//  배열에 집어 넣을때  Long타입으로 변환하여 List에 집어넣음
			parsingIntList.add(Long.parseLong(value));
		}
		return listToLongList(parsingIntList); 
	}
	
	public long[] listToLongList(List<Long> list) { // long[]로 변환
		//Long 객체이므로, stream() 메소드를 이용하여 스트림을 생성합니다. 그리고 mapToLong을 사용하여 각 Long 객체를 해당하는 기본 자료형인 long으로 변환합니다.
		//이 때 Long::longValue 메소드 레퍼런스를 이용하여 Long 객체를 long 값으로 변환합니다.
	
		//마지막으로, toArray() 메소드를 호출하여 이를 long[] 배열로 반환합니다.
		return list.stream().mapToLong(Long::longValue).toArray();
	}
}
