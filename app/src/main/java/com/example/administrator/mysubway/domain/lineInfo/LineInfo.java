package com.example.administrator.mysubway.domain.lineInfo;

/**
 * Created by Administrator on 2017-10-26.
 */

public class LineInfo {

    private SearchSTNBySubwayLineService SearchSTNBySubwayLineService;

    public SearchSTNBySubwayLineService getSearchSTNBySubwayLineService ()
    {
        return SearchSTNBySubwayLineService;
    }

    public void setSearchSTNBySubwayLineService (SearchSTNBySubwayLineService SearchSTNBySubwayLineService)
    {
        this.SearchSTNBySubwayLineService = SearchSTNBySubwayLineService;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [SearchSTNBySubwayLineService = "+SearchSTNBySubwayLineService+"]";
    }

}
