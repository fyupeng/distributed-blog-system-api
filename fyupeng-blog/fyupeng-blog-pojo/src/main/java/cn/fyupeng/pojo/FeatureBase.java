package cn.fyupeng.pojo;


public class FeatureBase {
    /**
     * 主键
     */
    private String id;

    /**
     * 植物id
     */
    private String botanyId;

    /**
     * 病虫害id
     */
    private String pestId;

    /**
     * 植物名
     */
    private String botanyName;

    /**
     * 病虫害名
     */
    private String pestName;

    /**
     * 图片路径
     */
    private String picPath;

    /**
     * 内容描述
     */
    private String content;

    public FeatureBase() {
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
     * 获取植物id
     *
     * @return botany_id - 植物id
     */
    public String getBotanyId() {
        return botanyId;
    }

    /**
     * 设置植物id
     *
     * @param botanyId 植物id
     */
    public void setBotanyId(String botanyId) {
        this.botanyId = botanyId;
    }

    /**
     * 获取病虫害id
     *
     * @return pest_id - 病虫害id
     */
    public String getPestId() {
        return pestId;
    }

    /**
     * 设置病虫害id
     *
     * @param pestId 病虫害id
     */
    public void setPestId(String pestId) {
        this.pestId = pestId;
    }

    /**
     * 获取植物名
     *
     * @return botany_name - 植物名
     */
    public String getBotanyName() {
        return botanyName;
    }

    /**
     * 设置植物名
     *
     * @param botanyName 植物名
     */
    public void setBotanyName(String botanyName) {
        this.botanyName = botanyName;
    }

    /**
     * 获取病虫害名
     *
     * @return pest_name - 病虫害名
     */
    public String getPestName() {
        return pestName;
    }

    /**
     * 设置病虫害名
     *
     * @param pestName 病虫害名
     */
    public void setPestName(String pestName) {
        this.pestName = pestName;
    }

    /**
     * 获取图片路径
     *
     * @return pic_path - 图片路径
     */
    public String getPicPath() {
        return picPath;
    }

    /**
     * 设置图片路径
     *
     * @param picPath 图片路径
     */
    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    /**
     * 获取内容描述
     *
     * @return content - 内容描述
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置内容描述
     *
     * @param content 内容描述
     */
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "FeatureBase{" +
                "id='" + id + '\'' +
                ", botanyId='" + botanyId + '\'' +
                ", pestId='" + pestId + '\'' +
                ", botanyName='" + botanyName + '\'' +
                ", pestName='" + pestName + '\'' +
                ", picPath='" + picPath + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}