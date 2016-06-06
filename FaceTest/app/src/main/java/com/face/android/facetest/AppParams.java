package com.face.android.facetest;

import android.app.Application;

public class AppParams extends Application {
	private String store_id;

	public String getStore_id() {
		return store_id;
	}

	public void setStore_id(String store_id) {
		this.store_id = store_id;
	}
	
}
