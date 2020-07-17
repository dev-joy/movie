package com.o2o.action.server.app;

import com.google.actions.api.*;
import com.google.actions.api.response.ResponseBuilder;
import com.google.actions.api.response.helperintent.SelectionList;
import com.google.api.services.actions_fulfillment.v2.model.*;
import com.o2o.action.server.util.CommonUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class internApp extends DialogflowApp {

	Map<String, String> img;		
	Map<String, String> genrelist;
	Map<String, String> actorlist;
	Set<String> timeline;

	int adult_price;
	int child_price;

	List<String> theaterList;
	List<String> movieList;
	Map<String,String> movieDes;
	boolean seatnotavail[][];



	public static void main(String[] args) throws UnsupportedEncodingException {
		String clientId = "EhpS5HYc9tpATQTzfMd7"; //애플리케이션 클라이언트 아이디값"
		String clientSecret = "y4ELWKCGk2"; //애플리케이션 클라이언트 시크릿값"




		String text = text = URLEncoder.encode("기생충", "UTF-8");

		//String apiURL = "https://openapi.naver.com/v1/search/movie.json?query="+text+"&display=1";    // json 결과
		String apiURL = "https://openapi.naver.com/v1/search/blog.xml?query="+ text +"&display=1" ; // xml 결과

		Map<String, String> requestHeaders = new HashMap<>();
		requestHeaders.put("X-Naver-Client-Id", clientId);
		requestHeaders.put("X-Naver-Client-Secret", clientSecret);
		String responseBody = get(apiURL,requestHeaders);


		System.out.println(responseBody);


	}

	private static String get(String apiUrl, Map<String, String> requestHeaders){
		HttpURLConnection con = connect(apiUrl);
		try {
			con.setRequestMethod("GET");
			for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
				con.setRequestProperty(header.getKey(), header.getValue());
			}

			int responseCode = con.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
				return readBody(con.getInputStream());
			} else { // 에러 발생
				return readBody(con.getErrorStream());
			}
		} catch (IOException e) {
			throw new RuntimeException("API 요청과 응답 실패", e);
		} finally {
			con.disconnect();
		}
	}

	private static HttpURLConnection connect(String apiUrl){
		try {
			URL url = new URL(apiUrl);
			return (HttpURLConnection)url.openConnection();
		} catch (MalformedURLException e) {
			throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
		} catch (IOException e) {
			throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
		}
	}

	private static String readBody(InputStream body) throws UnsupportedEncodingException {
		InputStreamReader streamReader = new InputStreamReader(body,"euc-kr");

		try (BufferedReader lineReader = new BufferedReader(streamReader)) {
			StringBuilder responseBody = new StringBuilder();

			String line;
			while ((line = lineReader.readLine()) != null) {
				responseBody.append(line);
			}

			return responseBody.toString();
		} catch (IOException e) {
			throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
		}
	}


	public void setUp() {
		genrelist = new HashMap<>();
		actorlist = new HashMap<>();

		genrelist.put("기생충","스릴러");
		genrelist.put("존윅","액션");
		genrelist.put("겨울왕국","애니메이션");
		actorlist.put("기생충","송강호");
		actorlist.put("존윅","키아누리브스");
		actorlist.put("겨울왕국","박지윤(성우)");

		theaterList = new ArrayList<>();
		theaterList.add("강남");
		theaterList.add("건대입구");
		theaterList.add("명동");
		theaterList.add("상봉");
		theaterList.add("압구정");
		theaterList.add("홍대");

		img = new HashMap();
		img.put("기생충","https://actions.o2o.kr/devsvr7/image/parasite.jpg");
		img.put("존윅","https://actions.o2o.kr/devsvr7/image/john.jpg");
		img.put("겨울왕국","https://actions.o2o.kr/devsvr7/image/winter.jpg");

		movieList = new ArrayList<>();
		movieList.add("기생충");
		movieList.add("존윅");
		movieList.add("겨울왕국");

		movieDes = new HashMap<>();
		movieDes.put("기생충","감독: 봉준호  출연: 송강호  장르: 스릴러\n줄거리 : 기생충과같은 삶..");
		movieDes.put("존윅","출연: 키아누 리브스  장르: 액션\n줄거리 : 복수는 끝나지 않는다.");
		movieDes.put("겨울왕국","등장인물: 엘사, 안나  장르: 애니메이션\n줄거리 : 겨울왕국의 시련을 이겨내자!");


		timeline = new HashSet<>();
		timeline.add("14:10");
		timeline.add("16:45");
		timeline.add("19:15");

		seatnotavail = new boolean[3][5];
		seatnotavail[0][4] = true;
		seatnotavail[1][0] = true;
		seatnotavail[1][1] = true;
		seatnotavail[2][3] = true;
		seatnotavail[2][4] = true;

		adult_price = 12000;
		child_price = 8000;
	}




	@ForIntent("Default Welcome Intent")
	public ActionResponse defaultWelcome(ActionRequest request) throws ExecutionException, InterruptedException, IOException {
		ResponseBuilder rb = getResponseBuilder(request);
		Map<String, Object> data = rb.getConversationData();
		data.clear();
		//필요한 데이타 셋업
		setUp();



		//극장 리스트 생성
		List<ListSelectListItem> theaterSelectList = new ArrayList<>();
		for(String theater : theaterList)
			theaterSelectList.add(new ListSelectListItem().setTitle(theater).setOptionInfo(new OptionInfo().setKey(theater)));
		rb.add("극장을 선택해주세요").add(new SelectionList().setTitle("극장 목록").setItems(theaterSelectList));

		//suggestions chip
		List<String> suggestions = new ArrayList<String>();
		suggestions.add("명동 선택");
		rb.addSuggestions(suggestions.toArray(new String[suggestions.size()]));

		return rb.build();
	}


	@ForIntent("theater")
	public ActionResponse theater(ActionRequest request) throws ExecutionException, InterruptedException {

		ResponseBuilder rb = getResponseBuilder(request);
		Map<String, Object> data = rb.getConversationData();



		//theater 변수 읽어오기
		String theater ="";
		String selectedItem = request.getSelectedOption();
		if(selectedItem !=null)
					theater = selectedItem;
		else
			theater = CommonUtil.makeSafeString(request.getParameter("theaterE"));

		//data에 theater 변수 저장
		data.put("theater",theater);

		//영화 리스트 생성
		List<ListSelectListItem> movieSelectList = new ArrayList<>();
		for(String movie : movieList)
			movieSelectList.add(new ListSelectListItem().setTitle(movie).setDescription(movieDes.get(movie)).setImage(new Image().setUrl(img.get(movie)).setAccessibilityText("기생충")).setOptionInfo(new OptionInfo().setKey(movie)));
		rb.add(""+theater+ " 선택하셨습니다.\n"+"현재 상영하는 영화 목록은 다음과 같습니다. 어떤 영화를 예매하시겠습니까?");
		rb.add("영화를 선택해주세요").add(new SelectionList().setTitle("상영 중인 영화").setItems(movieSelectList));

		//추후의 인텐트가 theaterfollowup context 만 소유하고 defaultfollowup context는 제거하도록 함
		rb.removeContext("DefaultWelcomeIntent-followup");
		return rb.build();
	}

	@ForIntent("theater - fallback")
	public ActionResponse theaterfallback(ActionRequest request) throws ExecutionException, InterruptedException {

		ResponseBuilder rb = getResponseBuilder(request);

		//영화 리스트 생성
		List<ListSelectListItem> movieSelectList = new ArrayList<>();
		for(String movie : movieList)
			movieSelectList.add(new ListSelectListItem().setTitle(movie).setDescription(movieDes.get(movie)).setImage(new Image().setUrl(img.get(movie)).setAccessibilityText("기생충")).setOptionInfo(new OptionInfo().setKey(movie)));
		rb.add("현재 상영중인 영화가 아닙니다.\n"+"현재 상영하는 영화 목록은 다음과 같습니다. 어떤 영화를 예매하시겠습니까?");
		rb.add("영화를 선택해주세요").add(new SelectionList().setTitle("상영 중인 영화").setItems(movieSelectList));

		return rb.build();
	}


	@ForIntent("title")
	public ActionResponse movie(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder rb = getResponseBuilder(request);

		List<String> suggestions = new ArrayList<String>();
		suggestions.add("다음주 월요일");
		SimpleResponse simpleResponse = new SimpleResponse();
		BasicCard basicCard = new BasicCard();

		String movie = "";

		//movie 변수 불러오기
		String selectedItem = request.getSelectedOption();
		if(selectedItem !=null)
					movie = selectedItem;
		else
			movie = CommonUtil.makeSafeString(request.getParameter("movie"));

		//data에 movie 변수 저장
		Map<String, Object> data = rb.getConversationData();
		data.put("movie",movie);



		simpleResponse.setTextToSpeech(genrelist.get(movie) + " 장르이고 "+actorlist.get(movie) +"가 출연하는 "+movie+" 선택하셨습니다."
		+"\n영화 관람 날짜를 말씀해주세요.")
				.setDisplayText(genrelist.get(movie) + " 장르이고 "+actorlist.get(movie) +"가 출연하는 "+movie+" 선택하셨습니다."+"\n영화 관람 날짜를 말씀해주세요.");

		basicCard
				.setTitle("이번달 달력")
				.setImage(new Image()
						.setUrl("https://actions.o2o.kr/devsvr7/image/calender8.jpg"));

		rb.add(simpleResponse);
		rb.add(basicCard);
		rb.addSuggestions(suggestions.toArray(new String[suggestions.size()]));
		return rb.build();
	}

	@ForIntent("title - fallback")
	public ActionResponse moviefallback(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder rb = getResponseBuilder(request);

		List<String> suggestions = new ArrayList<String>();
		suggestions.add("다음주 월요일");
		BasicCard basicCard = new BasicCard();

		String response = "";
		response = "영화 관람 날짜를 다시 말씀해주세요.";

		basicCard
				.setTitle("이번달 달력")
				.setImage(new Image()
						.setUrl("https://actions.o2o.kr/devsvr7/image/calender8.jpg"));

		rb.add(response);
		rb.add(basicCard);
		rb.addSuggestions(suggestions.toArray(new String[suggestions.size()]));
		return rb.build();
	}

	@ForIntent("date")
	public ActionResponse date(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder rb = getResponseBuilder(request);


		//date 변수 추출 및 date에 date변수 저장
		String date="";
		date = CommonUtil.makeSafeString(request.getParameter("date")).substring(5,10);
		Map<String, Object> data = rb.getConversationData();
		data.put("date",date);


		Calendar cal = Calendar.getInstance();
		int year = cal.get ( cal.YEAR );
		int month = cal.get ( cal.MONTH ) + 1 ;
		int dedate = cal.get ( cal.DATE ) ;

		if(month>Integer.parseInt(date.substring(0,2)) || (month==Integer.parseInt(date.substring(0,2)) && dedate >Integer.parseInt(date.substring(3,5)))){
			rb.add("지나간 날짜입니다. 다시 날짜를 선택해주세요");
			rb.removeContext("date-followup");
			return rb.build();
		}


		rb.add("<speak>관람 날짜는  <say-as interpret-as=\"date\" format=\"md\"> " +date + "</say-as> 입니다.\n상영 시간은 다음과 같습니다.\n관람 시간을 선택해주세요.</speak>");


		//time 리스트 생성
		List<ListSelectListItem> list = new ArrayList<>();
		for(String str : timeline) {
			list.add(new ListSelectListItem().setTitle(str).setOptionInfo(new OptionInfo().setKey(str)));
	}
		rb.add(new SelectionList().setItems(list).setTitle("상영 시간"));

		rb.removeContext("theater-followup");
		return rb.build();
	}

	@ForIntent("date - fallback")
	public ActionResponse datefallback(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder rb = getResponseBuilder(request);

		SimpleResponse simpleResponse = new SimpleResponse();


		String response = "잘 못 이해했습니다.\n상영 시간은 다음과 같습니다.\n관람 시간을 선택해주세요.";
		rb.add(response);

		//time 리스트 생성
		List<ListSelectListItem> list = new ArrayList<>();
		for(String str : timeline) {
			list.add(new ListSelectListItem().setTitle(str).setOptionInfo(new OptionInfo().setKey(str)));
		}
		rb.add(new SelectionList().setItems(list).setTitle("상영 시간"));

		return rb.build();
	}

	@ForIntent("time")
	public ActionResponse time(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder rb = getResponseBuilder(request);

		Map<String, Object> data = rb.getConversationData();

		List<String> suggestions = new ArrayList<String>();


		SimpleResponse simpleResponse = new SimpleResponse();
		BasicCard basicCard = new BasicCard();



		String time = "";
		String selectedItem = request.getSelectedOption();

		if(selectedItem !=null)
			time = selectedItem;
		else
			time = CommonUtil.makeSafeString(request.getParameter("time")).substring(11,16);
		data.put("time",time);


		if(!timeline.contains(time)) {
			rb.add("지정된 영화시간을 선택해주세요.");
			//time 리스트 생성
			List<ListSelectListItem> list = new ArrayList<>();
			for(String str : timeline) {
				list.add(new ListSelectListItem().setTitle(str).setOptionInfo(new OptionInfo().setKey(str)));
			}
			rb.add(new SelectionList().setItems(list).setTitle("상영 시간"));
			rb.removeContext("time-followup");
			return rb.build();
		}

		suggestions.add("b열 1부터 4까지");
		String response = ""+ time+ " 영화 선택하셨습니다.\n좌석 현황은 다음과 같습니다.\n" +
				"어느좌석으로 " + "예매하시겠습니까?";

		basicCard
				.setTitle("좌석 스크립트")
				.setImage(new Image()
						.setUrl("https://actions.o2o.kr/devsvr7/image/seat.PNG"));

		rb.add(response);
		rb.add(basicCard);
		rb.addSuggestions(suggestions.toArray(new String[suggestions.size()]));
		return rb.build();
	}

	@ForIntent("time - fallback")
	public ActionResponse timefallback(ActionRequest request) throws ExecutionException, InterruptedException {
		return makeTimeFallback(request,2).build();
	}

	@ForIntent("seat")
	public ActionResponse seat(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder rb = getResponseBuilder(request);
		Map<String, Object> data = rb.getConversationData();

		List<String> suggestions = new ArrayList<String>();


		char seatrow;
		seatrow = CommonUtil.makeSafeString(request.getParameter("seatrow")).charAt(0);

		List<Double> list = (List)(request.getParameter("number"));
		int seatFront = list.get(0).intValue();
		int seatBack = list.get(list.size() - 1).intValue();
		int headcount = seatBack - seatFront +1;


		data.put("seatFront",seatFront);
		data.put("seatBack",seatBack);
		data.put("headcount",headcount);



		int row = (int)seatrow-65;

		//빈좌석이 아니라면 fallback생성
		for(int i = 0; i<headcount; i++)
				if(seatnotavail[row][i+seatFront-1]==true)
					return makeTimeFallback(request,1).build();



		StringBuilder sb = new StringBuilder(Integer.toString(seatFront));
		for(int i = 0; i<seatBack-seatFront; i++)
			sb.append(seatFront+i+1);
		String seat = sb.toString();


		String response = ""+seatrow +"열 " +seat +"확인되었습니다.\n"+
				"어른, 어린이 각각 몇명입니까?";

		rb.add(response);
		rb.addSuggestions(suggestions.toArray(new String[suggestions.size()]));
		return rb.build();
	}

	@ForIntent("seat - fallback")
	public ActionResponse seatfallback(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder rb = getResponseBuilder(request);
		rb.add("어른 몇명, 어린이 몇명 인지 말씀해주세요.");
		return rb.build();
	}
	private ResponseBuilder makeTimeFallback(ActionRequest request, int Fcase){
		ResponseBuilder rb = getResponseBuilder(request);
		if(Fcase == 1){
			rb.add("빈좌석을 선택해주세요");
			rb.removeContext("seat-followup");
		}
		else {
			rb.add("좌석 열과 좌석 번호를 말씀해주세요");
		}
		BasicCard basicCard = new BasicCard();
		basicCard
				.setTitle("좌석 스크립트")
				.setImage(new Image()
						.setUrl("https://actions.o2o.kr/devsvr7/image/seat.PNG"));
		rb.add(basicCard);
		return rb;
	}

	@ForIntent("adult_child")
	public ActionResponse adult_child(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder rb = getResponseBuilder(request);

		Map<String, Object> data = rb.getConversationData();
		String theater = CommonUtil.makeSafeString(data.get("theater"));
		String movie = CommonUtil.makeSafeString(data.get("movie"));
		String date = CommonUtil.makeSafeString(data.get("date"));
		String time = CommonUtil.makeSafeString(data.get("time"));
		int headcount = CommonUtil.makeSafeInt(data.get("headcount"));


		SimpleResponse simpleResponse = new SimpleResponse();
		BasicCard basicCard = new BasicCard();

		int adult = 0;
		int child = 0;
		int cost = 0;
		String response = "";
		List<String> list = (List)request.getParameter("adult_child");
		List<String> list2 = (List)request.getParameter("headcount");
		Map<String,Integer> headToInt = new HashMap<>();
		headToInt.put("한명",1);
		headToInt.put("두명",2);
		headToInt.put("세명",3);
		headToInt.put("네명",4);
		headToInt.put("다섯명",5);
		headToInt.put("여섯명",6);



		if(list2.size() == 0) {

			if(list.get(0).equals("어른")) {
				cost = headcount * adult_price;
				adult = headcount;
			}
			else{
					cost = headcount * child_price;
					child = headcount;
				}
			response = ""+list.get(0) +" "+ headcount +"명 "+" 확인되었습니다.\n"
			+ theater +"점 " +movie +" " +date +" " + time +" \n"
			+"총 "+cost+"원입니다.";
		}
		if(list2.size() == 1) {
			if(list.get(0).equals("어른")) {
				cost = headcount * adult_price;
				adult = headToInt.get(list2.get(0));
			}
			else {
				cost = headcount * child_price;
				child = headToInt.get(list2.get(0));
			}
			response = ""+list.get(0) +" "+ list2.get(0) +" 확인되었습니다.\n"+ theater +"점 " +movie +" " +date +" " + time +" "+"\n총 "+cost+"원입니다.";
			}

		if(list2.size() == 2) {

			if(list.get(0).equals("어른")) {
				adult = headToInt.get(list2.get(0));
				child = headToInt.get(list2.get(1));
			}
			else{
					adult = headToInt.get(list2.get(1));
					child = headToInt.get(list2.get(2));
			}

			 cost = adult*12000 + child*8000;


			response = ""+list.get(0) +" "+ list2.get(0) +" "+list.get(1) +" "+ list2.get(1) +" 확인되었습니다.\n"
					+ theater +"점 " +movie +" " +date +" " + time +" "+"\n총 "+cost+"원입니다.";
		}

		if(headcount!=adult+child) {
			rb.add("예매하신 좌석수와 인원이 일치하지 않습니다.\n어른과 어린이가 각각 몇명입니까?");
			return rb.build();
		}
		simpleResponse.setTextToSpeech(response)
				.setDisplayText(response);

		basicCard
				.setTitle(movie)
				.setImage(new Image()
						.setUrl(img.get(movie)));

		rb.add(simpleResponse);
		rb.add(basicCard);
		return rb.build();
	}




}

