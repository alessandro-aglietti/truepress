package us.truepress.model;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.FetchGroup;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(detachable = "true")
@FetchGroup(name = "boxs", members = { @Persistent(name = "boxs") })
public class PressEntity {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long key;

	@Persistent
	private String name;

	@Persistent
	List<BoxEntity> boxs = new ArrayList<BoxEntity>();

	public PressEntity() {
		// TODO Auto-generated constructor stub
	}

	public PressEntity(String pressName) {
		// TODO Auto-generated constructor stub
		this.name = pressName;
	}

	public Long getId() {
		// TODO Auto-generated method stub
		return key;
	}

	public List<BoxEntity> getBoxs() {
		// TODO Auto-generated method stub
		return this.boxs;
	}

	public String getName() {
		return name;
	}
}