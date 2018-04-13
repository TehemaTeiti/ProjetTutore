package database.components;

public class Ball extends Component {
	private int radius ;
	
	public Ball(int x , int y, int radius) {
		this.setX(x);
		this.setY(y);
		this.radius = radius ;
	}

}
