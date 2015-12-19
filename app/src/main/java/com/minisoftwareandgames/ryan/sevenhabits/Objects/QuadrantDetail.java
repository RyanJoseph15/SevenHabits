package com.minisoftwareandgames.ryan.sevenhabits.Objects;

/**
 * Created by ryan on 12/18/15.
 */
public class QuadrantDetail {

    private String title;
    private int quadrant;
    private String details;

    public static QuadrantDetail newInstance(String title, int quadrant, String details) {
        QuadrantDetail quadrantDetail = new QuadrantDetail();
        quadrantDetail.setTitle(title);
        quadrantDetail.setQuadrant(quadrant);
        quadrantDetail.setDetails(details);
        return quadrantDetail;
    }

    public void setTitle(String title) {this.title = title;}
    public String getTitle() {return this.title;}
    public void setQuadrant(int quadrant) {this.quadrant = quadrant;}
    public int getQuadrant() {return this.quadrant;}
    public void setDetails(String details) {this.details = details;}
    public String getDetails() {return this.details;}

}
