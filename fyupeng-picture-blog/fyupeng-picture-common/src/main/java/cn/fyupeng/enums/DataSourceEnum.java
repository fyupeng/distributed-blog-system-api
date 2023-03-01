package cn.fyupeng.enums;

/**
 * @Auther: fyp
 * @Date: 2023/2/23
 * @Description: 数据源枚举类
 * @Package: cn.fyupeng.enums
 * @Version: 1.0
 */
public enum DataSourceEnum {
    // 主库
    MASTER("master"),
    // 从库
    SLAVE("slave");

    private String name;

    private DataSourceEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
