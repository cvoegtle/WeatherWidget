package org.voegtle.weatherwidget.diagram;

import android.graphics.drawable.Drawable;

import java.util.Date;

public class Diagram {
  private DiagramEnum id;
  private Drawable image;
  private Date updateTimestamp;

  public Diagram(DiagramEnum id, Drawable image, Date updateTimestamp) {
    this.id = id;
    this.image = image;
    this.updateTimestamp = updateTimestamp;
  }

  public boolean isOld() {
    return (new Date().getTime() - updateTimestamp.getTime()) > 60 * 60 * 1000;
  }


  public Diagram(DiagramEnum id, Drawable image) {
    this.id = id;
    setImage(image);
  }

  public void setImage(Drawable image) {
    this.image = image;
    this.updateTimestamp = new Date();
  }

  public DiagramEnum getId() {
    return id;
  }

  public Drawable getImage() {
    return image;
  }

  public Date getUpdateTimestamp() {
    return updateTimestamp;
  }
}
