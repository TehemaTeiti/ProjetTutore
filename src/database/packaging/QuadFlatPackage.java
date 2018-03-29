package database.packaging;

import java.util.ArrayList;
import java.util.List;

import database.components.BondingWire;
import database.components.Chip;
import database.components.Lead;
import database.components.Pad;

public class QuadFlatPackage extends PackagingType {
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
	
	@Override
	public String toString() {
		return "QuadFlatPackage";
	}
	
}
