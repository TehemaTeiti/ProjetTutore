package database.packaging;

import java.util.ArrayList;
import java.util.List;

import database.components.Ball;
import database.components.BondingWireConnector;
import database.components.Plan;
import database.components.Route;
import database.components.Via;

public class BallGridArray extends PackagingType {
	private List<Route> routes;
	private List<Plan> plans;
	private List<Via> vias;
	private List<Ball> balls;
	private List<BondingWireConnector> connectors;
	
	public BallGridArray() {
		routes = new ArrayList<>();
		plans = new ArrayList<>();
		vias = new ArrayList<>();
		balls = new ArrayList<>();
		connectors = new ArrayList<>();
	}
	
	@Override
	public String toString() {
		return "BallGridArray";
	}
}
