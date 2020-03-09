package model;

//don't delete empty methods !!!

public class Photos {
    private int pages;
    private Photo[] photo;

    public void setPage(int page) {}

    public void setPages(int pages) {
        this.pages = pages;
    }

    public void setPerpage(int perpage) {}

    public void setTotal(int total) {}

    public void setPhoto(Photo[] photo) {
        this.photo = photo;
    }

    public Photo[] getPhoto(){
        return photo;
    }

    public int getPages() {
        return pages;
    }
}
