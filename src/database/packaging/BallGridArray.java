package database.packaging;

import java.util.ArrayList;
import java.util.List;

import database.components.*;

public class BallGridArray {
	private List<Route> routes;
	private List<Plan> plans;
	private List<Via> vias;
	private List<Ball> balls;
	private List<BondingWireConnector> connectors;
	private List<Chip> chips;
	
	public BallGridArray() {
		routes = new ArrayList<>();
		plans = new ArrayList<>();
		vias = new ArrayList<>();
		balls = new ArrayList<>();
		connectors = new ArrayList<>();
	}
	
	public List<Route> getRoutes() {
		return routes;
	}

	public void setRoutes(List<Route> routes) {
		this.routes = routes;
	}

	public List<Plan> getPlans() {
		return plans;
	}

	public void setPlans(List<Plan> plans) {
		this.plans = plans;
	}

	public List<Via> getVias() {
		return vias;
	}

	public void setVias(List<Via> vias) {
		this.vias = vias;
	}

	public List<Ball> getBalls() {
		return balls;
	}

	public void setBalls(List<Ball> balls) {
		this.balls = balls;
	}

	public List<BondingWireConnector> getConnectors() {
		return connectors;
	}

	public void setConnectors(List<BondingWireConnector> connectors) {
		this.connectors = connectors;
	}

	public List<Chip> getChips() {
		return chips;
	}

	public void setChips(List<Chip> chips) {
		this.chips = chips;
	}
	
	@Override
	public String toString() {
		return "BallGridArray";
	}
}
