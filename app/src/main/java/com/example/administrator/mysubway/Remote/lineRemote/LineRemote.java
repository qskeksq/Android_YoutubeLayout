package com.example.administrator.mysubway.Remote.lineRemote;

import android.os.AsyncTask;
import android.util.Log;

import com.example.administrator.mysubway.domain.lineInfo.LineInfo;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-10-26.
 */

public class LineRemote {


    // 노선 정보
    // http://openapi.seoul.go.kr:8088/414478706c71736b39384775654e67/json/SearchSTNBySubwayLineService/1/5/1/
    // 실시간 정보
    // http://swopenapi.seoul.go.kr/api/subway/57774d4e7671736b3130324264436942/json/realtimeStationArrival/0/5/%EC%84%9C%EC%9A%B8

    public static void load(final LineTaskInterface taskInterface){
        final List<LineInfo> lineInfos = new ArrayList<>();
        System.out.println("=================load()");
        // data 변수에 불러온 데이터를 입력
        new AsyncTask<String, Void, List<LineInfo>>(){
            @Override
            protected List<LineInfo> doInBackground(String... voids) {
                System.out.println("=================doInBackground()");

                List<LineInfo> subwayLineList = new ArrayList<>();
                for(String url : voids){
                    String result = LineRemote.getData(url);
                    Log.e("결과", result);
                    LineInfo subwayLine = parsingJson(result);
                    subwayLineList.add(subwayLine);
                }
                Log.e("확인", subwayLineList.size()+"");
                return subwayLineList;
            }
            @Override
            protected void onPostExecute(List<LineInfo> datas) {
                System.out.println("=================onPostExecute()");
                // jsonString 을 parsing
                Log.e("확인", datas.size()+"");
                taskInterface.setData(datas);
            }
        }.execute(getUrlInfo("1"), getUrlInfo("2"), getUrlInfo("3"), getUrlInfo("4"), getUrlInfo("5"), getUrlInfo("6"), getUrlInfo("7"), getUrlInfo("8"), getUrlInfo("9"));
    }

    private static LineInfo parsingJson(String data) {
        Gson gson = new Gson();
        return gson.fromJson(data, LineInfo.class);
    }

    private static String getUrlInfo(String line){
        String url = "http://openapi.seoul.go.kr:8088/414478706c71736b39384775654e67/json/SearchSTNBySubwayLineService/1/110/"+line;
        return url;
    }

    private static String getData(String string){
        final StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(string);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            // 통신이 성공인지 체크
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // 여기서 부터는 파일에서 데이터를 가져오는 것과 동일
                InputStreamReader isr = new InputStreamReader(con.getInputStream());
                BufferedReader br = new BufferedReader(isr);
                String temp = "";
                while ((temp = br.readLine()) != null) {
                    result.append(temp).append("\n");
                }
                br.close();
                isr.close();
            } else {
                Log.e("ServerError", con.getResponseCode()+"");
            }
            con.disconnect();
        }catch(Exception e){
            Log.e("Error", e.toString());
        }
        return result.toString();
    }
}
