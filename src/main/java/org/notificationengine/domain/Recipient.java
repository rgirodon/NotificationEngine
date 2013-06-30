package org.notificationengine.domain;

public class Recipient {

	private String address;

	public Recipient() {
		super();
	}

	public Recipient(String address) {
		super();
		this.address = address;
	}

	@Override
	public String toString() {
		return "Recipient [address=" + address + "]";
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	
}
