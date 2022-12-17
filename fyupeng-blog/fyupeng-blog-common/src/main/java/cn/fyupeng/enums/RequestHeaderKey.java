package cn.fyupeng.enums;

/**
 * @Auther: fyp
 * @Date: 2022/12/11
 * @Description: 请求头key
 * @Package: cn.fyupeng.enums
 * @Version: 1.0
 */
public enum RequestHeaderKey {
    TOKEN_HEADER_KEY("token");

    private String name;

    RequestHeaderKey(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
