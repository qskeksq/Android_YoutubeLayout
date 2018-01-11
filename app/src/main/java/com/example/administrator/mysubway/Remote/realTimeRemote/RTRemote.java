package com.example.administrator.mysubway.Remote.realTimeRemote;

import android.os.AsyncTask;
import android.util.Log;

import com.example.administrator.mysubway.domain.realTimeInfo.RealTimeInfo;
import com.example.administrator.mysubway.domain.realTimeInfo.RealtimeArrivalList;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2017-10-26.
 */

public class RTRemote {

    // 노선 정보
    // http://openapi.seoul.go.kr:8088/414478706c71736b39384775654e67/json/SearchSTNBySubwayLineService/1/5/1/
    // 실시간 정보
    // http://swopenapi.seoul.go.kr/api/subway/57774d4e7671736b3130324264436942/json/realtimeStationArrival/0/5/%EC%84%9C%EC%9A%B8

    public static void load(final RTTaskInterface taskInterface){
        System.out.println("=================load()");
        // data 변수에 불러온 데이터를 입력
        new AsyncTask<Void, Void, String>(){
            @Override
            protected String doInBackground(Void... voids) {
                System.out.println("=================doInBackground()");
                String str = RTRemote.getData("https://api.github.com/users");
                return str;
            }
            @Override
            protected void onPostExecute(String jsonString) {
                System.out.println("=================onPostExecute()");
                // jsonString 을 parsing
                Gson gson = new Gson();
                RealTimeInfo data = gson.fromJson(jsonString, RealTimeInfo.class);
                RealtimeArrivalList[] arrivalLists = data.getRealtimeArrivalList();
                taskInterface.setData(arrivalLists);
            }
        }.execute();
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
