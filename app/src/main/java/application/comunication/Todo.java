package application.comunication;

/**
 * Created by Federico-PC on 05/04/2017.
 */

public class Todo {
    private String summary;
    private String description;

    public Todo(String sum,String des){
        summary = sum;
        description = des;
    }
    public String getSummary() {
        return summary;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

}
