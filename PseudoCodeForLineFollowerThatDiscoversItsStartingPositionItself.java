package nl.hva.miw.robot.cohort12;

import java.util.ArrayList;

public class PseudoLijnFollower {
// Laat Robot voor x aantal metingen naar links gaan

// Bereken aan het eind van deze metingen het gemiddelde van alle waarden

// Laat de Robot weer x aantal metingen naar rechts gaan, zodat hij weer ongeveer op dezelfde positie is t.o.v. lijn als bij de start

// Als gemiddelde > 0.5, dan is de robot kennelijk gestart vanaf de linker rand van de lijn
// Laat de robot dan de lijn volgen door naar rechts te gaan bij wit en links bij zwart

// Als gemdidelde < 0.5, dan de robot kennelijk gestart van de rechter rand van de lijn
// Laat de robot dan de lijn volgen door naar links te gaan bij wit en rechts bij zwart


	public static double calculateAverage(ArrayList<Float> listWithValues) {
		int numberOfValues = listWithValues.size();
		double average;
		double sum = 0;
		for (int i = numberOfValues - 100; i < numberOfValues; i++) {
			sum += listWithValues.get(i);
		}
		average = sum / listWithValues.size();
		return average;
	}
}
