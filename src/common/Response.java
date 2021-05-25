package common;

import java.io.Serializable;

public class Response implements Serializable {
    private static final long serialVersionUID = -3424055826225839709L;
    private String responseInfo;
    private String serverToken;
    private boolean OK = true;

    public Response(String responseInfo) {
        this.responseInfo = responseInfo;
    }

    public Response(String responseInfo, boolean OK) {
        this.responseInfo = responseInfo;
        this.OK = OK;
    }

    public Response(String responseInfo, boolean OK, String serverToken) {
        this.responseInfo = responseInfo;
        this.OK = OK;
        this.serverToken = serverToken;
    }

    public String getResponseInfo() {
        return responseInfo;
    }

    public String getServerToken() {
        return this.serverToken;
    }

    public boolean isOK() {
        return this.OK;
    }

    public boolean isEmpty() {
        return responseInfo == null;
    }
}
