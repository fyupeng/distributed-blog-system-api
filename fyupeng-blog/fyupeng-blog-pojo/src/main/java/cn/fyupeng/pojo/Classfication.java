package cn.fyupeng.pojo;


public class Classfication {
    /**
     * 主键
     */
    private String id;

    /**
     * 分类名
     */
    private String name;


    public Classfication() {
    }

    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public String getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取分类名
     *
     * @return name - 分类名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置分类名
     *
     * @param name 分类名
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Classfication{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}