package main;
import java.util.Comparator;
import java.util.LinkedList;

import static java.lang.Math.*;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * @author Tehema
 * 
 * Test opencv :
 * 
 * - Charge image
 * - Applique medianBlur (méthode de lissage/réduction du bruit)
 * - Applique Canny (edge detection)
 * - Applique Standard Hough Line Transform
 * - Filtre les lignes (garde les lignes horizontales et verticales selon epsilon)
 * - Filtre les lignes trop proches selon epsilon TODO
 * - Dessine les lignes sur les images
 * - Affiche les images obtenues
 */
/**
 * @author Tehema
 *
 */
public class HelloCV {

    private static String[] names = {"src/image/panneau.jpg", 
            "src/image/visa.png",
            "src/image/QFP44_XRAY.png",
            "src/image/bga.png"};
    private static String NAME = names[2];
    private static String OUT_NAME = "src/image/res.png";
    
    public static void main(String[] args){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        // variable
        int houghline_threshold = 200; // nb d'intersection en coordonnées polaires
        double eps = 0.05; 
        int gap = 10;
        Scalar color = new Scalar(0, 0, 255); // couleur des lignes dessinées BGR
        Mat nlines;
        Mat lines = new Mat(); // coordonées polaires (rho, theta)
        Mat canny = new Mat();
        Mat cdst = new Mat();
        Mat src = Imgcodecs.imread("test.png");
        
        // check si image source valide
        if( src.empty() ) {
            System.out.println("Error opening image!");
            System.out.println("Program Arguments: [image_name -- default "
                    + NAME +"] \n");
            System.exit(-1);
        }
        System.out.println("Taille de l'image : " + src.size());
       
        // standard hough line detection
        Imgproc.Canny(src, canny, 50, 200, 3, false);
        Imgproc.HoughLines(canny, lines, 1, Math.PI/180, houghline_threshold); // runs the actual detection
        
        // dessine les lignes
        int tailleLine = 10000;
        Imgproc.cvtColor(canny, cdst, Imgproc.COLOR_GRAY2BGR);
        for (int x = 0; x < lines.rows(); x++) {
            double rho = lines.get(x, 0)[0],
                    theta = lines.get(x, 0)[1];
            double a = Math.cos(theta), b = Math.sin(theta);
            double x0 = a*rho, y0 = b*rho;
			Point pt1 = new Point(Math.round(x0 + tailleLine*(-b)), Math.round(y0 + tailleLine*(a)));
            Point pt2 = new Point(Math.round(x0 - tailleLine*(-b)), Math.round(y0 - tailleLine*(a)));
            Imgproc.line(cdst, pt1, pt2, color, 1, Imgproc.LINE_AA, 0); // draw line on canny image
            Imgproc.line(src, pt1, pt2, color, 1, Imgproc.LINE_AA, 0); // draw line on original image
        }
        
        // show
        HighGui.imshow("Source", src);
        HighGui.imshow("Hough Line", cdst);
        
        HighGui.waitKey();
        
        if (!Imgcodecs.imwrite(OUT_NAME, src)) {
            System.out.println("Error saving result!");
            System.exit(-1);
        }
        
        System.exit(0);
    }

    /**
     * Cherche le point haut-gauche et le point bas-droite du rectangle a detecter.
     * 
     * @param nlines
     * @return pts tableau 2 dimensions avec pts[0] le point haut-gauche et p[1] le point bas-droite, null si non trouve  
     */
    private static Point[] detectRect(Mat nlines, double eps) {
        Point[] pts = new Point[2];
        double[] left, right, bottom, top;
        double pi2 = Math.PI/2;
        LinkedList<double[]> horizontales = new LinkedList<>();
        LinkedList<double[]> verticales = new LinkedList<>();
        
        // si on a pas 4 lignes, retourne null
        if (nlines.rows() != 4)
            return null;
        
        // cherche les lignes gauche, droite, haut et bas
        for (int i=0 ; i < nlines.rows() ; i++) {
            double theta = nlines.get(i,0)[1];
            
            if (theta <= eps || theta >= 2*Math.PI - eps)
                verticales.add(nlines.get(i, 0));
            else if (Math.abs(theta - pi2) <= eps)
                horizontales.add(nlines.get(i, 0));
        }

        // si on a pas 2 lignes horizontales et 2 lignes verticales, retourne null
        if (horizontales.size() != 2 || verticales.size() != 2)
            return null;
        
        // definit les lignes haut, bas, gauche, droite
        verticales.sort(new Comp_Ligne());
        horizontales.sort(new Comp_Ligne());
        left = verticales.get(0);
        right = verticales.get(1);
        top = horizontales.get(0);
        bottom = horizontales.get(1);
        
        // recherche du point haut-gauche
        double rho1 = left[0], rho2 = top[0];
        double theta1 = left[1], theta2 = top[1];
        double[] intersect = calcul_intersect(rho1, rho2, theta1, theta2);
        
        pts[0] = new Point(intersect[0], intersect[1]);

        // recherche du point bas-droite
        rho1 = right[0];
        rho2 = bottom[0];
        theta1 = right[1];
        theta2 = bottom[1];
        intersect = calcul_intersect(rho1, rho2, theta1, theta2);
        
        pts[1] = new Point(intersect[0], intersect[1]);
        
        return pts;
    }

    /**
     * Calcul le point d'intersection entre 2 droites selon leur coordonnee polaire.
     * 
     * @param r1 rho de la droite 1
     * @param r2 rho de la droite 2
     * @param t1 theta de la droite 1
     * @param t2 theta de la droite 2
     * @return intersect[] tableau de double 2 dim qui donne le point d'intersection de coordonnees (intersect[0] ; intersect[1]) 
     */
    private static double[] calcul_intersect(double r1, double r2, double t1, double t2) {
        double intersect[] = new double[2];
        double x10 = r1 * cos(t1), y10 = r1 * sin(t1);
        double x1a = x10 - 1000 * sin(t1), y1a = y10 + 1000 * cos(t1);
        double x1b = x10 + 1000 * sin(t1), y1b = y10 - 1000 * cos(t1);
        double a1 = (y1a - y1b) / (x1a - x1b);
        double b1 = y1a - a1 * x1a;

        double x20 = r2 * cos(t2), y20 = r2 * sin(t2);
        double x2a = x20 - 1000 * sin(t2), y2a = y20 + 1000 * cos(t2);
        double x2b = x20 + 1000 * sin(t2), y2b = y20 - 1000 * cos(t2);
        double a2 = (y2a - y2b) / (x2a - x2b);
        double b2 = y2a - a2 * x2a;
        
        double xf = (b2 - b1) / (a1 - a2);
        double yf = a2 * xf + b2;
        
        intersect[0] = xf;
        intersect[1] = yf;
        return intersect;
    }

    /**
     * Garde uniquement les lignes horizontaux et verticaux.
     * Les lignes sont stockées selon leur coordonée polaire (rho, theta).
     * On définit une ligne horizontale si theta appartient à [pi/2 - eps ; pi/2 + eps]
     * On définit une ligne verticale si theta appartient à [- eps ; eps]
     * @param lines
     * @return une matrice contenant les lignes horizontaux et verticaux
     */
    private static Mat delete_diag(Mat lines, double eps) {
        Mat nlines = new Mat();
        int row = 0;
        double pi2 = Math.PI/2;
        LinkedList<double[]> l = new LinkedList<>();
        
        // stocke les lignes horizontales et verticales
        for (int i=0 ; i < lines.rows() ; i++) {
            double theta = lines.get(i, 0)[1];
            // si la ligne est horizontal ou vertical
            if ((theta <= eps || theta >= 2*Math.PI - eps) 
                    || (Math.abs(theta - pi2) <= eps)){
                l.add(lines.get(i, 0));
            }
        }

        // initialise la matrice résultat
        nlines.create(l.size(), 1, CvType.CV_32FC2); // type de la matrice : tab 2 col float 32 bits
        for (double[] el : l) {
            nlines.put(row, 0, el);
            row++;
        }
        
        return nlines;
    }
    
    /**
     * Supprime les lignes trop proches.
     * Garde la ligne la plus "droite" (dont le theta est proche de pi/2 pour une ligne horizontale, proche de 0 pour une ligne verticale)
     * 
     * @param lines
     * @param eps pour theta
     * @param gap distance entre 2 droites à l'origine considérées comme proches
     * @return une matrice sans lignes proches et sans diagonales
     */
    private static Mat delete_near(Mat lines, double eps, int gap) {
        Mat nlines = new Mat();
        double pi2 = Math.PI/2;
        int row = 0;
        LinkedList<double[]> horizontales = new LinkedList<>();
        LinkedList<double[]> verticales = new LinkedList<>();
        LinkedList<double[]> nhorizontales = new LinkedList<>();
        LinkedList<double[]> nverticales = new LinkedList<>();
        
        // tri les lignes horizontales et verticales
        for (int i=0 ; i<lines.rows() ; i++) {
            double theta = lines.get(i, 0)[1];
            if (theta <= eps || theta >= 2*Math.PI - eps) {
                verticales.add(lines.get(i, 0));
            }
            else if (Math.abs(theta - pi2) <= eps) {
                horizontales.add(lines.get(i, 0));
            }
        }

        verticales.sort(new Comp_Ligne());
        horizontales.sort(new Comp_Ligne());

        // retire les lignes verticales proches
        double[] old_line = verticales.get(0);
        for (int i = 1 ; i < verticales.size() ; i++) {
            double[] new_line = verticales.get(i);

            // si les lignes sont proches
            if ((new_line[0] - old_line[0]) <= gap) {
                // on garde celui qui a abs(theta) le plus petit
                old_line = (Math.abs(new_line[1]) < Math.abs(old_line[1])) ? new_line : old_line;
                
            }
            else {
                nverticales.add(old_line);
                old_line = new_line;
            }

            // si c'est le dernier element, on l'ajoute
            if (i == verticales.size()-1)
                nverticales.add(old_line);
        }
        
        // retire les lignes horizontales proches
        old_line = horizontales.get(0);
        for (int i = 1 ; i < horizontales.size() ; i++) {
            double[] new_line = horizontales.get(i);

            // si les lignes sont proches
            if ((new_line[0] - old_line[0]) <= gap) {
                // on garde celui qui a abs(theta - pi/2) le plus petit
                old_line = (Math.abs(new_line[1] - pi2) < Math.abs(old_line[1] - pi2)) ? new_line : old_line;
                
                // si c'est le dernier element, on l'ajoute
                if (i == horizontales.size()-1)
                    nhorizontales.add(old_line);
            }
            else {
                nhorizontales.add(old_line);
                old_line = new_line;
            }
        }
        
        // ajout des lignes dans la matrice resultat
        nlines.create(nhorizontales.size() + nverticales.size(), 1, CvType.CV_32FC2);
        for (double[] el : nverticales) {
            nlines.put(row, 0, el);
            row++;
        }
        for (double[] el : nhorizontales) {
            nlines.put(row, 0, el);
            row++;
        }
        
        return nlines;
    }

    /**
     * @author Tehema
     *
     *    Permet de comparer des lignes par rapport à la valeur de rho.
     */
    private static class Comp_Ligne implements Comparator<double[]> {
        @Override
        public int compare(double[] o1, double[] o2) {
            return Double.compare(o1[0], o2[0]);
        }
    }
    
}


