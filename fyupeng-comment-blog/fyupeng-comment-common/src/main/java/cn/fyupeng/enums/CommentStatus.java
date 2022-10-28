package cn.fyupeng.enums;

/**
 * @Auther: fyp
 * @Date: 2022/4/23
 * @Description:
 * @Package: com.crop.enums
 * @Version: 1.0
 */
public enum CommentStatus {

    NORMAL(0, "正常"),
    BLOCKED(1, "屏蔽");

    private Integer status;
    private String message;

    CommentStatus(Integer status, String message) {
        this.status = status;
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
