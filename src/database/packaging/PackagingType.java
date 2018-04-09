package database.packaging;

public enum PackagingType {
	QFP("Quad Flat Packaging"), BGA("Ball Grid Array");
	
	private String text;
	
	private PackagingType(String text) {
		this.text = text;
	}
	
	public String toString() {
		return this.text;
	}
	
}
