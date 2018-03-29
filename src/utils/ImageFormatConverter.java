package utils;


/**
 * Cette classe permet de convertir un format d'image donné en entrée en png, exploitable par l'application.
 *
 */
public abstract class ImageFormatConverter {

	public String changeName(String name) {
		String[] fileName = name.split("[.]");
		return fileName[0]+ ".png";
	}
}
