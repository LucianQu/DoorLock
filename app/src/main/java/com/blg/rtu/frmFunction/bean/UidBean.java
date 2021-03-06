package com.blg.rtu.frmFunction.bean;

public class UidBean {

    /**
     * code : 200
     * message : SUCCESS
     * result : {"compile_time":"1546089746","uid":"FXVY6HMHL571XSU9111A","device_id":"0101b7617b6ad000","hosttype":"1","ip":"192.168.1.106\u0000","devstatus":"1","pid":"00002000","logtime":"2019-01-17 21:56:59","version":"1.0.0.6"}
     */

    private int code;
    private String message;
    private ResultBean result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * compile_time : 1546089746
         * uid : FXVY6HMHL571XSU9111A
         * device_id : 0101b7617b6ad000
         * hosttype : 1
         * ip : 192.168.1.106 
         * devstatus : 1
         * pid : 00002000
         * logtime : 2019-01-17 21:56:59
         * version : 1.0.0.6
         */

        private String compile_time;
        private String uid;
        private String device_id;
        private String hosttype;
        private String ip;
        private String devstatus;
        private String pid;
        private String logtime;
        private String version;

        public String getCompile_time() {
            return compile_time;
        }

        public void setCompile_time(String compile_time) {
            this.compile_time = compile_time;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getDevice_id() {
            return device_id;
        }

        public void setDevice_id(String device_id) {
            this.device_id = device_id;
        }

        public String getHosttype() {
            return hosttype;
        }

        public void setHosttype(String hosttype) {
            this.hosttype = hosttype;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getDevstatus() {
            return devstatus;
        }

        public void setDevstatus(String devstatus) {
            this.devstatus = devstatus;
        }

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public String getLogtime() {
            return logtime;
        }

        public void setLogtime(String logtime) {
            this.logtime = logtime;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }
}
