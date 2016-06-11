package com.sam_chordas.android.stockhawk.data;

/**
 * Created by kishor on 11/6/16.
 */
public class Stock {
    private String _ID;
    private String SYMBOL;
    private String PERCENT_CHANGE;
    private String CHANGE;
    private String BIDPRICE;
    private String CREATED;
    private String ISUP;
    private String ISCURRENT;

    public String get_ID() {
        return _ID;
    }

    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    public String getSYMBOL() {
        return SYMBOL;
    }

    public void setSYMBOL(String SYMBOL) {
        this.SYMBOL = SYMBOL;
    }

    public String getPERCENT_CHANGE() {
        return PERCENT_CHANGE;
    }

    public void setPERCENT_CHANGE(String PERCENT_CHANGE) {
        this.PERCENT_CHANGE = PERCENT_CHANGE;
    }

    public String getCHANGE() {
        return CHANGE;
    }

    public void setCHANGE(String CHANGE) {
        this.CHANGE = CHANGE;
    }

    public String getBIDPRICE() {
        return BIDPRICE;
    }

    public void setBIDPRICE(String BIDPRICE) {
        this.BIDPRICE = BIDPRICE;
    }

    public String getCREATED() {
        return CREATED;
    }

    public void setCREATED(String CREATED) {
        this.CREATED = CREATED;
    }

    public String getISUP() {
        return ISUP;
    }

    public void setISUP(String ISUP) {
        this.ISUP = ISUP;
    }

    public String getISCURRENT() {
        return ISCURRENT;
    }

    public void setISCURRENT(String ISCURRENT) {
        this.ISCURRENT = ISCURRENT;
    }
}
