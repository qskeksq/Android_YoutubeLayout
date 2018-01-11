package com.example.administrator.mysubway;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.administrator.mysubway.Remote.lineRemote.LineRemote;
import com.example.administrator.mysubway.Remote.lineRemote.LineTaskInterface;
import com.example.administrator.mysubway.domain.lineInfo.LineInfo;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LineTaskInterface{

    private ViewPager linePager;
    private TabLayout tabLayout;
    List<LineInfo> lineInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        LineRemote.load(this);
    }

    private void initView() {
        linePager = (ViewPager) findViewById(R.id.linePager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
    }

    private void setTabLayout(){

    }

    @Override
    public void setData(List<LineInfo> lineInfo) {
        this.lineInfos = lineInfo;
        Log.e("확인2", lineInfo.size()+"");
        for(LineInfo info : lineInfo){
            String line = info.getSearchSTNBySubwayLineService().getRow()[0].getLINE_NUM();
            Log.e("정보", line);
            tabLayout.addTab(tabLayout.newTab().setText(line));
        }
    }
}

