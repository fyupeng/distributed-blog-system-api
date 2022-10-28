package cn.fyupeng.pojo;

import javax.persistence.Id;

public class Classfication {
    @Id
    private String id;

    private String name;

    public Classfication() {
    }

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
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