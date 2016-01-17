package ro.as_mi.sniff.sniffevents;

/**
 * Created by liviu on 16.01.2016.
 */
public class Messages {

    String date_added;
    String message_text;
    Integer message_id;
    Integer project_id;
    String project_name;
    Integer mes_nr;
    Integer index;

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public String getMessage_text() {
        return message_text;
    }

    public void setMessage_text(String message_text) {
        this.message_text = message_text;
    }

    public Integer getMessage_id() {
        return message_id;
    }

    public void setMessage_id(Integer message_id) {
        this.message_id = message_id;
    }

    public Integer getProject_id() {
        return project_id;
    }

    public void setProject_id(Integer project_id) {
        this.project_id = project_id;
    }
    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public Integer getMes_nr() {
        return mes_nr;
    }

    public void setMes_nr(Integer mes_nr) {
        this.mes_nr = mes_nr;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
