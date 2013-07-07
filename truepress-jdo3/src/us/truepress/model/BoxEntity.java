package us.truepress.model;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.FetchGroup;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unowned;

@PersistenceCapable(detachable = "true")
@FetchGroup(name = "rss", members = { @Persistent(name = "rss") })
public class BoxEntity {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private Double top;

	@Persistent
	private Double left;

	@Persistent
	private Double width;

	@Persistent
	private Double height;

	@Persistent
	@Unowned
	List<RssEntity> rss = new ArrayList<RssEntity>();

	public BoxEntity() {
		// TODO Auto-generated constructor stub
	}

	public Double getTop() {
		return top;
	}

	public void setTop(Double top) {
		this.top = top;
	}

	public Double getLeft() {
		return left;
	}

	public void setLeft(Double left) {
		this.left = left;
	}

	public Double getWidth() {
		return width;
	}

	public void setWidth(Double width) {
		this.width = width;
	}

	public Double getHeight() {
		return height;
	}

	public List<RssEntity> getRss() {
		return rss;
	}

	public void setHeight(Double height) {
		this.height = height;
	}

	public Long getId() {
		return key.getId();
	}

	public void setRss(List<RssEntity> rss) {
		// TODO Auto-generated method stub
		this.rss = rss;
	}
}