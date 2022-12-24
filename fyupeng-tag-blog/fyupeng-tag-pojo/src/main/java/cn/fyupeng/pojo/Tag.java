package cn.fyupeng.pojo;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

public class Tag implements Serializable {
    @Id
    private String id;

    private String name;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "tag_cover_id")
    private String tag_cover_id;

    public Tag() {
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

    /**
     * @return user_id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTag_cover_id() {
        return tag_cover_id;
    }

    public void setTag_cover_id(String tag_cover_id) {
        this.tag_cover_id = tag_cover_id;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", userId='" + userId + '\'' +
                ", tag_cover_id='" + tag_cover_id + '\'' +
                '}';
    }
}