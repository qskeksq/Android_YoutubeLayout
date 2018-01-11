package com.example.administrator.mysubway.domain.lineInfo;

/**
 * Created by Administrator on 2017-10-26.
 */

public class RESULT {

    private String MESSAGE;

    private String CODE;

    public String getMESSAGE ()
    {
        return MESSAGE;
    }

    public void setMESSAGE (String MESSAGE)
    {
        this.MESSAGE = MESSAGE;
    }

    public String getCODE ()
    {
        return CODE;
    }

    public void setCODE (String CODE)
    {
        this.CODE = CODE;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [MESSAGE = "+MESSAGE+", CODE = "+CODE+"]";
    }

}
