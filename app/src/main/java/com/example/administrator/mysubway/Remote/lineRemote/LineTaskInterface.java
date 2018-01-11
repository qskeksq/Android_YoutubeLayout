package com.example.administrator.mysubway.Remote.lineRemote;

import com.example.administrator.mysubway.domain.lineInfo.LineInfo;

import java.util.List;

/**
 * Created by Administrator on 2017-10-16.
 */

public interface LineTaskInterface {

    void setData(List<LineInfo> lineInfo);

}
