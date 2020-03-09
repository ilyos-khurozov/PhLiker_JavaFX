package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.LRUMap;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.CachedPhoto;
import model.FlickrResponse;
import model.Photo;

import java.io.IOException;
import java.net.URL;

public class Controller {
    private LRUMap<String, CachedPhoto> cache = new LRUMap<>(0,200);
    private ObjectMapper mapper = new ObjectMapper();
    private Photo[] photos;
    private int cur = 1, curPage = 1, pages;
    private double mx=0, my=0;
    private static Stage primaryStage;

    @FXML
    private TextField textField;

    @FXML
    private ImageView imgView;

    @FXML
    private ImageView loaderImgView;

    @FXML
    private Button searchBtn;

    @FXML
    private Button prevBtn;

    @FXML
    private Button nextBtn;

    @FXML
    private Label title;

    @FXML
    private Label counter;

    @FXML
    void close() {
        Platform.exit();
    }

    @FXML
    void next() {
        //if it's last picture on the page
        //go to next page
        if (cur == 50){
            curPage++;
            cur = 1;
            search();
        } else {
            cur++;
            reload();
        }
    }

    @FXML
    void prev() {
        //if it's first picture on the page
        //goto previous page
        if (cur == 1){
            curPage--;
            cur = 50;
            search();
        } else {
            cur--;
            reload();
        }
    }

    @FXML
    void search() {
        //change all spaces to '+' sign
        String tag = textField.getText().replace(' ', '+');

        //escaping from tag injection
        if (tag.contains("&") || tag.isEmpty()){
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setHeaderText(null);
            err.setContentText(tag.isEmpty() ? "Enter tag" : "Don't use '&' sign");
            err.showAndWait();
            return;
        }
        String url = "https://www.flickr.com/services/rest/?method=flickr.photos.search" +
                "&api_key=950888e0219426f8682b3027344570f0" +
                "&tags=" + tag +
                "&per_page=50" +
                "&page=" + curPage +
                "&format=json&nojsoncallback=1";

        Task<Void> searching = new Task<>() {
            @Override
            protected Void call() {
                searchBtn.setDisable(true); //escape from many clicks
                imgView.setOpacity(0);
                loaderImgView.setOpacity(1);

                try {
                    //get JSON from API and set variables
                    FlickrResponse fr = mapper.readValue(new URL(url), FlickrResponse.class);
                    photos = fr.getPhotos().getPhoto();
                    pages = fr.getPhotos().getPages();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                searchBtn.setDisable(false);

                return null;
            }
        };

        //event starts when task is done
        searching.setOnSucceeded(event -> reload());
        //event start when task is failed
        searching.setOnFailed(event -> System.out.println("searching failed"));

        new Thread(searching).start();


    }

    @FXML
    void mouseDragged(MouseEvent event){
        //change primaryStage's coordinates
        primaryStage.setX(event.getScreenX()-mx);
        primaryStage.setY(event.getScreenY()-my);
    }

    @FXML
    void mousePressed(MouseEvent event) {
        ////get initial points
        mx = event.getSceneX();
        my = event.getSceneY();
    }

    //reloading window
    private void reload(){
        Task<Void> loading = new Task<>() {
            @Override
            protected Void call() {
                nextBtn.setDisable(true); //escape from many clicks
                prevBtn.setDisable(true);
                imgView.setOpacity(0);
                loaderImgView.setOpacity(1);

                //get photo from cache
                CachedPhoto cachedPhoto = cache.get(photos[cur-1].link());

                //if there is no photo in cache
                //create new instance and put it to cache
                if (cachedPhoto == null){
                    cachedPhoto = new CachedPhoto(new Image(photos[cur-1].link()), photos[cur-1].getTitle());
                    cache.put(photos[cur-1].link(), cachedPhoto);
                }
                imgView.setImage(cachedPhoto.getImage());
                CachedPhoto finalCachedPhoto = cachedPhoto;
                //change indicators
                Platform.runLater(() -> {
                    title.setText(finalCachedPhoto.getTitle());
                    counter.setText(cur+" of "+photos.length+" images\n"+curPage+" of "+pages+" pages");
                });

                prevBtn.setDisable(cur == 1 && curPage == 1);
                nextBtn.setDisable(cur == photos.length-1 && curPage == pages);

                return null;
            }
        };

        loading.setOnSucceeded(event -> {
            loaderImgView.setOpacity(0);
            imgView.setOpacity(1);
        });
        loading.setOnFailed(event -> System.out.println("loading failed"));

        new Thread(loading).start();
    }

    //primary stage is sent with this method by Main class
    //this is for dragging window
    public static void initPrimaryStage(Stage stage){
        primaryStage = stage;
    }
}
