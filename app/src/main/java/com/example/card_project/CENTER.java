package com.example.card_project;

import android.widget.EditText;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

public class CENTER {
    EditText edit;
    TextView text;

    XmlPullParser xpp;
    String key = "6DZ6vIYN0rhzin%2Bcypnb8Nc5WmA2uORlu6fMvMXcYFjhfpW5OXqe5x8qhK2KvTyI6R9gbl97zdNVeGE63n%2B22A%3D%3D"; //센터 공공데이터 서비스키


    String faclNm;
    String lcMnad;


    public String getXmlData() {
        StringBuffer buffer = new StringBuffer();

        String str = edit.getText().toString();//EditText에 작성된 Text얻어오기
        String location = null;
        try {
            location = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String queryUrl = "http://www.bokjiro.go.kr/openapi/nwel/getDisConvFaclList"//요청 URL
                + key +"&numOfRows=100" + "&emdongNm=" + location; //동 이름으로 검색

        try {
            URL url = new URL(queryUrl);
            InputStream is = url.openStream(); //url위치로 입력스트림 연결

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(is, "UTF-8")); //inputstream 으로부터 xml 입력받기

            String tag;

            xpp.next();
            int eventType = xpp.getEventType();
            faclNm="";
            lcMnad="";

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        buffer.append("파싱 시작...\n\n");
                        break;

                    case XmlPullParser.START_TAG:
                        tag = xpp.getName();//테그 이름 얻어오기

                        if (tag.equals("item")) ;// 첫번째 태그값이랑 비교
                        else if (tag.equals("addr")) {
                            xpp.next();
                            lcMnad = xpp.getText();
                        } else if (tag.equals("yadmNm")) {
                            buffer.append("\n");
                            xpp.next();
                            faclNm = xpp.getText();
                            buffer.append("센터명 : " + faclNm +"\n" + "주소 : " + lcMnad + "\n" );
                            buffer.append("\n"+"_____________________________________________________");
                        }
                        break;

                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        tag = xpp.getName(); //테그 이름 얻어오기
                        if (tag.equals("item"))
                            buffer.append("\n");// 첫번째 검색결과종료 후 줄바꿈
                        break;
                }

                eventType = xpp.next();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch blocke.printStackTrace();
        }
        return buffer.toString();//StringBuffer 문자열 객체 반환

    }
}
