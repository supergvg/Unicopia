package com.sollace.unicopia;

public enum Race {
	EARTH("Earth Pony", false, false),
	UNICORN("Unicorn", false, true),
	PEGASUS("Pegasus", true, false),
	ALICORN("Alicorn", true, true),
	CHANGELING("Changeling", true, false);
	
	private final String displayName;
	private final boolean fly;
	private final boolean magic;
	
	Race(String name, boolean fly, boolean magic) {
		displayName = name;
		this.fly = fly;
		this.magic = magic;
	}
	
	public static Race getDefault() {
		return EARTH;
	}
	
	public boolean canUseEarth() {
		return this == EARTH || this == ALICORN;
	}
	
	public boolean canCast() {
		return magic;
	}
	
	public boolean canFly() {
		return fly;
	}
	
	public boolean canInteractWithClouds() {
		return this == PEGASUS || this == ALICORN;
	}
	
	public String displayName() {
		return displayName;
	}
	
	public boolean startsWithVowel() {
		return "AEIO".indexOf(name().charAt(0)) != -1;
	}
	
	public static Race value(String s) {
		Race result = getSpeciesFromName(s);
		return result != null ? result : getDefault();
	}
	
	public static Race getSpeciesFromName(String s) {
		if (s != null && !s.isEmpty()) {
			s = s.toUpperCase();
			for (Race i : values()) {
				if (i.name().contentEquals(s) || i.displayName().contentEquals(s)) return i;
			}
		}
		return null;
	}
	
	public static Race getSpeciesFromId(String s) {
		try {
			int id = Integer.parseInt(s);
			Race[] values = values();
			if (id >= 0 || id < values.length) {
				return values[id];
			}
		} catch (NumberFormatException e) { }
		return null;
	}
}