package com.face.android.pojo;

public class StaffPojo {

	private int id;
	private String name;
	private String img_src;

	public StaffPojo() {
	}

	public StaffPojo(int id, String name, String img_src) {
		super();
		this.id = id;
		this.name = name;
		this.img_src = img_src;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImg_src() {
		return img_src;
	}

	public void setImg_src(String img_src) {
		this.img_src = img_src;
	};

}
