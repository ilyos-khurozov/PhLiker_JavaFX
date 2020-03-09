package model;

//don't delete empty methods !!!

public class Photo {
    private int farm;
    private String id;
    private String secret;
    private String server;
    private String title;

    public void setFarm(int farm) {
        this.farm = farm;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setOwner(String owner) {}

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIsfamily(boolean isfamily) {}

    public void setIsfriend(boolean isfriend) {}

    public void setIspublic(boolean ispublic) {}

    public String getTitle() {
        return title;
    }

    public String link() {
        return "https://farm"+farm+".staticflickr.com/"+server+"/"+id+"_"+secret+".jpg";
    }
}
