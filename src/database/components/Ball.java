package database.components;

public class Ball extends Component {
	private double radius ;
	
	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public Ball(double x , double y, double radius) {
		this.setX(x);
		this.setY(y);
		this.radius = radius ;
	}

}
