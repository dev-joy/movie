package com.o2o.action.server.app;


import com.google.actions.api.ActionRequest;
import com.google.actions.api.ActionResponse;
import com.google.actions.api.DialogflowApp;
import com.google.actions.api.ForIntent;
import com.google.actions.api.response.ResponseBuilder;
import com.google.actions.api.response.helperintent.SelectionCarousel;
import com.google.api.services.actions_fulfillment.v2.model.*;
import com.o2o.action.server.util.CommonUtil;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class internApp extends DialogflowApp {

	String theater;
	String movie;
	String date;
	String time;
	int seatFront;
	int seatBack;
	int headcount;

	@ForIntent("Default Welcome Intent")
	public ActionResponse defaultWelcome(ActionRequest request) throws ExecutionException, InterruptedException, IOException {
		ResponseBuilder rb = getResponseBuilder(request);
		Map<String, Object> data = rb.getConversationData();
		data.clear();

		List<String> suggestions = new ArrayList<String>();
		suggestions.add("명동 선택");

		SimpleResponse simpleResponse = new SimpleResponse();
		BasicCard basicCard = new BasicCard();


		simpleResponse.setTextToSpeech("극장을 선택해주세요.")
				.setDisplayText("극장을 선택해주세요.");

		basicCard
				.setTitle("극장의 목록")
				.setImage(new Image()
						.setUrl("https://actions.o2o.kr/devsvr7/image/theater.PNG"));

		rb.add(simpleResponse);
		rb.add(basicCard);
		rb.addSuggestions(suggestions.toArray(new String[suggestions.size()]));
		return rb.build();
	}

	@ForIntent("theater")
	public ActionResponse theater(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder rb = getResponseBuilder(request);
		Map<String, Object> data = rb.getConversationData();

		data.clear();

		List<String> suggestions = new ArrayList<String>();
		suggestions.add("기생충 예매");
		SimpleResponse simpleResponse = new SimpleResponse();


		theater = CommonUtil.makeSafeString( request.getParameter("theaterE"));
		simpleResponse.setTextToSpeech(theater+ " 선택하셨습니다.\n"+"현재 상영하는 영화 목록은 다음과 같습니다. 어떤 영화를 예매하시겠습니까?")
				.setDisplayText( theater+ " 선택하셨습니다.\n"+"현재 상영하는 영화 목록은 다음과 같습니다. 어떤 영화를 예매하시겠습니까?");



		rb.add(simpleResponse);
		rb.addSuggestions(suggestions.toArray(new String[suggestions.size()]));
		return rb.build();
	}

	@ForIntent("title")
	public ActionResponse movie(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder rb = getResponseBuilder(request);
		Map<String, Object> data = rb.getConversationData();

		data.clear();

		List<String> suggestions = new ArrayList<String>();
		suggestions.add("다음주 월요일");
		SimpleResponse simpleResponse = new SimpleResponse();
		BasicCard basicCard = new BasicCard();

		String genre = "스릴러";
		String actor = "송강호";
		movie = CommonUtil.makeSafeString(request.getParameter("movie"));

		simpleResponse.setTextToSpeech(genre + " 장르이고 "+actor +"가 출연하는 "+movie+" 선택하셨습니다."
		+"\n영화 관람 날짜를 말씀해주세요.")
				.setDisplayText(genre + " 장르이고 "+actor +"가 출연하는 "+movie+" 선택하셨습니다."+"\n영화 관람 날짜를 말씀해주세요.");

		basicCard
				.setTitle("이번달 달력")
				.setImage(new Image()
						.setUrl("https://actions.o2o.kr/devsvr7/image/calender8.jpg"));

		rb.add(simpleResponse);
		rb.add(basicCard);
		rb.addSuggestions(suggestions.toArray(new String[suggestions.size()]));
		return rb.build();
	}

	@ForIntent("date")
	public ActionResponse date(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder rb = getResponseBuilder(request);
		Map<String, Object> data = rb.getConversationData();

		data.clear();

		List<String> suggestions = new ArrayList<String>();
		suggestions.add("14시 10분");
		suggestions.add("16시 45분");
		suggestions.add("19시 15분");


		SimpleResponse simpleResponse = new SimpleResponse();


		date = CommonUtil.makeSafeString(request.getParameter("date")).substring(5,10);
		simpleResponse.setTextToSpeech("관람 날짜는 "+date+ "입니다.\n상영 시간은 다음과 같습니다.\n관람 시간을 선택해주세요.")
				.setDisplayText("관람 날짜는 "+date+ "입니다.\n상영 시간은 다음과 같습니다.\n관람 시간을 선택해주세요.")
		;

		rb.add(simpleResponse);

		rb.addSuggestions(suggestions.toArray(new String[suggestions.size()]));
		return rb.build();
	}

	@ForIntent("time")
	public ActionResponse time(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder rb = getResponseBuilder(request);
		Map<String, Object> data = rb.getConversationData();

		data.clear();

		List<String> suggestions = new ArrayList<String>();
		suggestions.add("b열 1부터 4까지");

		SimpleResponse simpleResponse = new SimpleResponse();
		BasicCard basicCard = new BasicCard();


		time = CommonUtil.makeSafeString( request.getParameter("time")).substring(11,16);

		simpleResponse.setTextToSpeech(time+ " 영화 선택하셨습니다." +
				"\n좌석 현황은 다음과 같습니다.\n" +
				"어느좌석으로 " +
				"예매하시겠습니까?")
				.setDisplayText(time+ " 영화 선택하셨습니다."+
						"\n좌석 현황은 다음과 같습니다.\n" +
						"어느좌석으로 " +
		"예매하시겠습니까?");

		basicCard
				.setTitle("좌석 스크립트")
				.setImage(new Image()
						.setUrl("https://actions.o2o.kr/devsvr7/image/calender8.jpg"));

		rb.add(simpleResponse);

		rb.addSuggestions(suggestions.toArray(new String[suggestions.size()]));
		return rb.build();
	}

	@ForIntent("seat")
	public ActionResponse seat(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder rb = getResponseBuilder(request);
		Map<String, Object> data = rb.getConversationData();

		data.clear();

		List<String> suggestions = new ArrayList<String>();

		SimpleResponse simpleResponse = new SimpleResponse();

		char seatrow;
		seatrow = CommonUtil.makeSafeString(request.getParameter("seatrow")).charAt(0);

		List<Double> list = (List)(request.getParameter("number"));
		int n = list.size();
		seatFront = list.get(0).intValue();
		seatBack = list.get(list.size() - 1).intValue();
		headcount = seatBack - seatFront +1;

		StringBuilder sb = new StringBuilder(Integer.toString(seatFront));
		for(int i = 0; i<seatBack-seatFront; i++)
			sb.append(Integer.toString(seatFront+i+1));
		String seat = sb.toString();


		simpleResponse.setTextToSpeech(seatrow +"열 " + seat +"확인되었습니다.\n"+
									"어른, 어린이 각각 몇명입니까?")
				.setDisplayText(seatrow +"열 " + seat +"확인되었습니다.\n"+
						"어른, 어린이 각각 몇명입니까?")
		;

		rb.add(simpleResponse);
		rb.addSuggestions(suggestions.toArray(new String[suggestions.size()]));
		return rb.build();
	}


	@ForIntent("adult_child")
	public ActionResponse adult_child(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder rb = getResponseBuilder(request);
		Map<String, Object> data = rb.getConversationData();

		data.clear();

		List<String> suggestions = new ArrayList<String>();

		SimpleResponse simpleResponse = new SimpleResponse();

		int adult = 0;
		int child = 0;

		List<String> list = (List)request.getParameter("adult_child");
		List<String> list2 = (List)request.getParameter("headcount");


		if(list2.size() == 0) {
			simpleResponse.setTextToSpeech(list.get(0) + headcount +" 확인되었습니다.\n"+ theater +"점 " +movie +" " +date +" " + time +" ")
					.setDisplayText(list.get(0) + " n명 "+" 확인되었습니다.\n"+ theater +"점 " +movie +" " +date +" " + time +" ");
		}
		if(list2.size() == 1) {
			simpleResponse.setTextToSpeech(list.get(0) +" "+ list2.get(0) +" 확인되었습니다.\n"+ theater +"점 " +movie +" " +date +" " + time +" ")
					.setDisplayText(list.get(0) + " "+ list2.get(0)+" 확인되었습니다.\n"+ theater +"점 " +movie +" " +date +" " + time +" ");
		}
		if(list2.size() == 2) {
			simpleResponse.setTextToSpeech(list.get(0) +" "+ list2.get(0) +" "+list.get(1) +" "+ list2.get(1) +" 확인되었습니다.\n"
			+ theater +"점 " +movie +" " +date +" " + time +" ")
					.setDisplayText(list.get(0) +" "+ list2.get(0) +" "+list.get(1) +"  "+ list2.get(1) +" 확인되었습니다.\n"
							+ theater +"점 " +movie +" " +date +" " + time +" ");
		}



		rb.add(simpleResponse);

		rb.addSuggestions(suggestions.toArray(new String[suggestions.size()]));
		return rb.build();
	}






	@ForIntent("next")
	public ActionResponse next(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder rb = getResponseBuilder(request);
		Map<String, Object> data = rb.getConversationData();

		data.clear();

		List<String> suggestions = new ArrayList<String>();
		SimpleResponse simpleResponse = new SimpleResponse();
		BasicCard basicCard = new BasicCard();

		simpleResponse.setTextToSpeech("안녕하세요, 넥스트 입니다.")
				.setDisplayText("안녕하세요, 넥스트 입니다.")
		;

		rb.add(simpleResponse);

		rb.addSuggestions(suggestions.toArray(new String[suggestions.size()]));
		return rb.build();
	}



	@ForIntent("basic card")
	public ActionResponse basicCard(ActionRequest request) {
		ResponseBuilder responseBuilder = getResponseBuilder(request);


		// Prepare formatted text for card
		String text =
				"\"사자\"는 __동물의 왕__이야. \n"
						+ "  *사바나*에가면 꼭 보고싶네\n";
						 // Note the two spaces before '\n' required for
		// a line break to be rendered in the card.

		responseBuilder
				.add("베이직 카드 봐바")
				.add(
						new BasicCard()
								.setTitle("사자")
								.setSubtitle("동물의 왕")
								.setFormattedText(text)
								.setImage(
										new Image()
												.setUrl(
														"https://storage.googleapis.com/actionsresources/logo_assistant_2x_64dp.png")
												.setAccessibilityText("Image alternate text"))
								.setImageDisplayOptions("CROPPED")
								.setButtons(
										new ArrayList<Button>(
												Arrays.asList(
														new Button()
																.setTitle("버튼 눌러바.")
																.setOpenUrlAction(
																		new OpenUrlAction().setUrl("https://www.google.com/search?q=%EC%82%AC%EC%9E%90&oq=%EC%82%AC%EC%9E%90&aqs=chrome..69i57j69i61l3.3551j0j4&sourceid=chrome&ie=UTF-8"))))))
				.add("사자가 짱이지");

		return responseBuilder.build();
	}

}

