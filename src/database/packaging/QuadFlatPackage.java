package database.packaging;

import java.util.ArrayList;
import java.util.List;

import database.components.BondingWire;
import database.components.Chip;
import database.components.Lead;
import database.components.Pad;

public class QuadFlatPackage {
	private List<Lead> leads;
	private List<BondingWire> bondingWires;
	private List<Pad> pads;
	private List<Chip> chips;
	
	public QuadFlatPackage() {
		leads = new ArrayList<>();
		bondingWires = new ArrayList<>();
		pads = new ArrayList<>();
		chips = new ArrayList<>();
	}
	
	public List<Lead> getLeads() {
		return leads;
	}

	public void setLeads(List<Lead> leads) {
		this.leads = leads;
	}

	public List<BondingWire> getBondingWires() {
		return bondingWires;
	}

	public void setBondingWires(List<BondingWire> bondingWires) {
		this.bondingWires = bondingWires;
	}

	public List<Pad> getPads() {
		return pads;
	}

	public void setPads(List<Pad> pads) {
		this.pads = pads;
	}

	public List<Chip> getChips() {
		return chips;
	}

	public void setChips(List<Chip> chips) {
		this.chips = chips;
	}

	@Override
	public String toString() {
		return "QuadFlatPackage";
	}
	
}
