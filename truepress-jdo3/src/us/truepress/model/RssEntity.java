package us.truepress.model;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.FetchGroup;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(detachable = "true")
@FetchGroup(name = "rssentries", members = { @Persistent(name = "entries") })
public class RssEntity {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long key;

	@Persistent
	private List<RssEntryEntity> entries = new ArrayList<RssEntryEntity>();

	@Persistent
	private String url;

	public RssEntity() {
		// TODO Auto-generated constructor stub
	}

	public RssEntity(String rssurl) {
		// TODO Auto-generated constructor stub
		this.url = rssurl;
	}

	public String getUrl() {
		return url;
	}

	public Long getId() {
		return this.key;
	}

	public List<RssEntryEntity> getEntries() {
		return entries;
	}

}
