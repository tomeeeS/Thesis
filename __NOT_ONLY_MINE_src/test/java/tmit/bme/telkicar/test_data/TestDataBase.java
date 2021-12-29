package tmit.bme.telkicar.test_data;

import org.jetbrains.annotations.NotNull;
import tmit.bme.telkicar.base.TLocation;
import tmit.bme.telkicar.logic.helpers.AppContextHelper;
import tmit.bme.telkicar.logic.helpers.NycAppContextHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public abstract class TestDataBase {

	public static Map<String, TLocation> testLocations = new HashMap<>() {{
		put("nycSpecial", new TLocation("NYC, Floral park",  40.723544, -73.701542, ""));
		put("telki", new TLocation("Telki, 2089", 47.5494957, 18.8231733, "ChIJC01pqgZ0akcRgKYeDCnEAAQ"));
		put("oktogon", new TLocation("Budapest, Oktogon", 47.5055, 19.0637, "EhpCdWRhcGVzdCwgT2t0b2dvbiwgSHVuZ2FyeSIuKiwKFAoSCaP41nlu3EFHETf06g8AcstzEhQKEgnJz9TRNMNBRxFgER4MKcQABA"));
		put("bme", new TLocation("Budapest, Magyar Tudósok Körútja 2, 1117", 47.4733, 19.0599, "ChIJMeAUNardQUcRFdRH4YNCqyI"));
		put("batthyany", new TLocation("Budapest, Batthyány tér, 1011", 47.5065, 19.0382, "EidCdWRhcGVzdCwgQmF0dGh5w6FueSB0w6lyLCAxMDExIEh1bmdhcnkiLiosChQKEgnNvygoGtxBRxHyRkvxR_-kIxIUChIJyc_U0TTDQUcRYBEeDCnEAAQ"));
		put("blaha", new TLocation("Budapest, Blaha Lujza tér, 1085", 47.4963, 19.0696, "EihCdWRhcGVzdCwgQmxhaGEgTHVqemEgdMOpciwgMTA4NSBIdW5nYXJ5Ii4qLAoUChIJm32wZ2fcQUcRSQAyREXi4nsSFAoSCcnP1NE0w0FHEWARHgwpxAAE"));
		put("jaszai", new TLocation("Budapest, Jászai Mari tér", 47.5132, 19.0477, "EiRCdWRhcGVzdCwgSsOhc3phaSBNYXJpIHTDqXIsIEh1bmdhcnkiLiosChQKEgmjAtJxD9xBRxEGt3FVGNNqyxIUChIJyc_U0TTDQUcRYBEeDCnEAAQ"));
	}};

	//BP bounds
	//  lat 47°25'10.16" - 47°33'35.81"
	//  lon 19° 0'7.51"  - 19° 9'47.98"
	static int[] latBpBounds = {47419489, 47559947}; // lat * 1_000_000
	static int[] lngBpBounds = {19002086, 19163328};

	static int[] latNycBounds = {40616742, 40749747}; // lat * 1_000_000
	static int[] lngNycBounds = {-73986351, -73817601};

	public static TLocation getRandomDefinedBPLocation() {
		Object[] locations = testLocations.values().toArray();
		return (TLocation) locations[new Random().nextInt(locations.length - 1) + 1];
	}

	public static TLocation getRandomBPLocation() {
		String address = "testAddress_" + new Random().nextInt();
		double lat = (double) (new Random().nextInt(latBpBounds[1] - latBpBounds[0]) + latBpBounds[0]) / 1_000_000;
		double lng = (double) (new Random().nextInt(lngBpBounds[1] - lngBpBounds[0]) + lngBpBounds[0]) / 1_000_000;
		String placeId = "ChIJMeAUNardQUcRFdRH4YNCqyI";
		return new TLocation(address, lat, lng, placeId);
	}

	public static TLocation getRandomNycLocation() {
		String address = "testAddress_" + new Random().nextInt();
		double lat = (double) (new Random().nextInt(latNycBounds[1] - latNycBounds[0]) + latNycBounds[0]) / 1_000_000;
		double lng = (double) (new Random().nextInt(lngNycBounds[1] - lngNycBounds[0]) + lngNycBounds[0]) / 1_000_000;
		String placeId = " ";
		return new TLocation(address, lat, lng, placeId);
	}

	public static String[] getRandomName() {
		return new String[]{csaladNevek[new Random().nextInt(csaladNevek.length)], kersztNevek[new Random().nextInt(kersztNevek.length)]};
	}

	public static String getRandomRendszam() {
		int a = 97;
		int z = 122;
		int targetStringLength = 3;
		Random random = new Random();

		StringBuilder buffer = new StringBuilder(targetStringLength);
		for (int i = 0; i < targetStringLength; i++) {
			int randomLimitedInt = a + (int) (random.nextFloat() * (z - a + 1));
			buffer.append((char) randomLimitedInt);
		}
		String generatedString = buffer.toString();
		return generatedString.toUpperCase() + "-" + Math.abs(random.nextInt(899) + 100);
	}

	private static final String[] csaladNevek = {
		"Nagy",
		"Lukács",
		"Jónás",
		"Kovács",
		"Gulyás",
		"Szücs",
		"Tóth",
		"Biró",
		"Hajdu",
		"Szabó",
		"Király",
		"Halász",
		"Horváth",
		"Balog",
		"Máté",
		"Varga",
		"László",
		"Székely",
		"Kiss",
		"Bogdán",
		"Gáspár",
		"Molnár",
		"Jakab",
		"Kozma",
		"Németh",
		"Katona",
		"Pásztor",
		"Farkas",
		"Sándor",
		"Bakos",
		"Balogh",
		"Váradi",
		"Dudás",
		"Papp",
		"Boros",
		"Virág",
		"Lakatos",
		"Fazekas",
		"Major",
		"Takács",
		"Kelemen",
		"Orbán",
		"Juhász",
		"Antal",
		"Hegedüs",
		"Oláh",
		"Orosz",
		"Barna",
		"Mészáros",
		"Somogyi",
		"Novák",
		"Simon",
		"Fülöp",
		"Soós",
		"Rácz",
		"Veres",
		"Tamás",
		"Fekete",
		"Budai",
		"Nemes",
		"Szilágyi",
		"Vincze",
		"Pataki",
		"Török",
		"Hegedűs",
		"Balla",
		"Fehér",
		"Deák",
		"Faragó",
		"Balázs",
		"Pap",
		"Kerekes",
		"Gál",
		"Bálint",
		"Barta",
		"Kis",
		"Illés",
		"Péter",
		"Szűcs",
		"Pál",
		"Borbély",
		"Orsós",
		"Vass",
		"Csonka",
		"Kocsis",
		"Szőke",
		"Mezei",
		"Fodor",
		"Fábián",
		"Sárközi",
		"Pintér",
		"Vörös",
		"Berki",
		"Szalai",
		"Lengyel",
		"Márton",
		"Sipos",
		"Bodnár"};

	private static final String[] kersztNevek = {
		"Antónia",
		"Alexandra",
		"Vivien",
		"Viktória",
		"Dóra",
		"Nikolett",
		"Fanni",
		"Eszter",
		"Barbara",
		"Anna",
		"Klaudia",
		"Zsófia",
		"Krisztina",
		"Réka",
		"Kitti",
		"Petra",
		"Renáta",
		"Bettina",
		"Evelin",
		"Adrienn",
		"Enikő",
		"Brigitta",
		"Cintia",
		"Bianka",
		"Anita",
		"Andrea",
		"Laura",
		"Noémi",
		"Boglárka",
		"Bernadett",
		"Katalin",
		"Fruzsina",
		"Nikoletta",
		"Nóra",
		"Dorina",
		"Rebeka",
		"Regina",
		"Anett",
		"Dorottya",
		"Szilvia",
		"Lilla",
		"Ádám",
		"Dániel",
		"Tamás",
		"Bence",
		"Péter",
		"Dávid",
		"Márk",
		"László",
		"Zoltán",
		"Krisztián",
		"Máté",
		"Gábor",
		"Attila",
		"Richárd",
		"Balázs",
		"István",
		"Zsolt",
		"Norbert",
		"Gergő",
		"Roland",
		"János",
		"József",
		"Sándor",
		"Martin",
		"Bálint",
		"András",
		"Patrik",
		"Csaba",
		"Róbert",
		"Kristóf",
		"Tibor",
		"Ákos",
		"Szabolcs",
		"Ferenc",
		"Viktor",
		"Gergely",
		"Márton",
		"Levente",
		"Marcell",
		"Alex",
		"Erik",
		"Imre"
	};

	@NotNull
	public static TLocation getRandomLocation(@NotNull AppContextHelper appContextHelper) {
		if (appContextHelper instanceof NycAppContextHelper)
			return getRandomNycLocation();
		else
			return getRandomBPLocation();
	}
}

