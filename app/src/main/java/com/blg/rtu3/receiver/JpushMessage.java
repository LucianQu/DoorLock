package com.blg.rtu3.receiver;

/*
 * 推送消息体（保存到数据库）
 */
public class JpushMessage {

    private Integer _id;
    private String wellNo;//机井编码
    private String wellName;//机井名称
    private String msgTime;//推送时间
    private String msgId;//消息id
    //private String msgContent;//消息内容
    private String isReaded;//是否已读
    private String type;//消息类型
//    /**
//     * 水电量不足报警 waterElecless 	typeOne
//     * 开箱提醒   openBox 				typeTwo
//     * 超年计划水量报警 overPlanedWater typeThree
//     * 设备工况报警 deviceWorkWarn    	typeFour
//     * 水电配比报警 waterElecAbnormal  	typeFive
//     **/
//    private String typeOne;
//    private String typeTwo;
//    private String typeThree;
//    private String typeFour;
//    private String typeFive;


    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }

    public String getWellNo() {
        return wellNo;
    }

    public void setWellNo(String wellNo) {
        this.wellNo = wellNo;
    }

    public String getWellName() {
        return wellName;
    }

    public void setWellName(String wellName) {
        this.wellName = wellName;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getIsReaded() {
        return isReaded;
    }

    public void setIsReaded(String isReaded) {
        this.isReaded = isReaded;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    //    public String getTypeOne() {
//        return typeOne;
//    }
//
//    public void setTypeOne(String typeOne) {
//        this.typeOne = typeOne;
//    }
//
//    public String getTypeTwo() {
//        return typeTwo;
//    }
//
//    public void setTypeTwo(String typeTwo) {
//        this.typeTwo = typeTwo;
//    }
//
//    public String getTypeThree() {
//        return typeThree;
//    }
//
//    public void setTypeThree(String typeThree) {
//        this.typeThree = typeThree;
//    }
//
//    public String getTypeFour() {
//        return typeFour;
//    }
//
//    public void setTypeFour(String typeFour) {
//        this.typeFour = typeFour;
//    }
//
//    public String getTypeFive() {
//        return typeFive;
//    }
//
//    public void setTypeFive(String typeFive) {
//        this.typeFive = typeFive;
//    }

    public JpushMessage() {
        super();
    }

}
