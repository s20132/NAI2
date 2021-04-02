package main.test;

import java.io.*;
import java.util.*;

public class Perceptron {

    static final String PATH = "iris.csv";
    static int lineLength;
    static double alpha;
    static double theta;
    static double localError;
    static double globalError;
    static double wynik;
    static double dokladnosc;


    public static void main(String[] args) throws IOException {

        Scanner sc = new Scanner(System.in);
        List<Wektor> trening = csvToListTraining();
        List<Wektor> test = csvToListTest();

        //System.out.println("Size listy treningowej = " + trening.size());
        //System.out.println("Size listy testowej = " + test.size());

        System.out.println("Alpha: ");
        alpha = sc.nextDouble();
        System.out.println("Theta:");
        theta = sc.nextDouble();
        double poprawne = 0;

        double[] weights = new double[0];
        do {
            globalError = 0;
            double[] positionArray = new double[0];
            System.out.println();
            for (Wektor w : trening) {
                setLineLength(w.position.length);

                weights = fillWeights(w.position.length);

                positionArray = new double[w.position.length];

                //uzupełniamy pozycje x-ami
                System.arraycopy(w.position, 0, positionArray, 0, w.position.length);

                wynik = calcWynik(theta, weights, positionArray);

                //roznica pomiędzy wytypowaną wartością a wynikiem
                localError = w.type - wynik;

                //System.out.println( w.type +"-"+ wynik + " = " + localError);

                //mnożenie wag
                for (int i = 0; i < weights.length; i++) {

                    // w'= w + (d-y)*a*x
                    weights[i] += localError * alpha * w.position[i];

                    //ostatnia kolumna
                    if (i == (weights.length - 1)) {
                        weights[weights.length - 1] = localError * alpha;
                    }

                    // jezeli globalerror bedzie 0 to wagi tez beda 0
                    globalError += (localError * localError);
              }
            }

            System.out.println("Zbiór testowy: ");
            for (Wektor w2 : test) {
                wynik = calcWynik(theta, weights, positionArray);
                System.out.println("klasa = " + wynik);

                for (int i = 0; i < w2.position.length; i++) {
                    System.out.print("w["+i+"] = " + w2.position[i] + ",");
                }
                System.out.println();

                if ((w2.type) == wynik) {
                    poprawne++;
                   // System.out.println("poprawne++ " + "  wynik: " + wynik + " typ " +  w.type);
                }

            }

            dokladnosc = (poprawne / test.size()) * 100;
            System.out.println("Dokładność: " + dokladnosc + "%" + "\n");
            System.out.println("theta " + theta);
            if (dokladnosc < theta) System.out.println("Dokładność poniżej progu theta");
            poprawne = 0;


        } while (globalError !=0 && dokladnosc < theta);

        System.out.println("Wektor granicy decyzyjnej:");
        for (int i = 0; i < weights.length; i++) {
            System.out.print("w["+i+"] =" + weights[i] + "," );
        }
        System.out.println();
        addWektor(weights);

    }

    public static void setLineLength(int lineLength) {
        Perceptron.lineLength = lineLength;
    }

    public static int countLines(String filename) throws IOException {
        LineNumberReader reader  = new LineNumberReader(new FileReader(filename));
        int count;
        String lineRead = "";
        while ((lineRead = reader.readLine()) != null) {}
        count = reader.getLineNumber();
        reader.close();
        return count;
    }

    private static List<Wektor> csvToListTraining() throws IOException {

        int numberOfLines = (int)(countLines(Perceptron.PATH) * 0.75);

        List<Wektor> list = new ArrayList<>();
        int type_number; // 1 or 0
        Scanner input = new Scanner(new File(Perceptron.PATH));

        int counter = 0;
        while (input.hasNextLine() && counter <= numberOfLines) {

            String line = input.nextLine();

            String[] row = line.split(";");

            //argumenty bez typu
            double[] x = new double[row.length-1];

            String type = row[row.length-1];

            for (int i = 0; i < row.length-1; i++) {
                x[i] = Double.parseDouble(row[i]);
            }

            if (type.equals("1")) {
                type_number = 1;
            }
            else
                type_number = 0;

            list.add(new Wektor(x, type_number));

            counter++;
        }

        input.close();

        return list;
    }

    private static List<Wektor> csvToListTest() throws IOException {

        int tmp = (int)(countLines(Perceptron.PATH) * 0.75);
        int numberOfLines = countLines(Perceptron.PATH);

        List<Wektor> list = new ArrayList<>();
        int type_number; // 1 or 0
        Scanner input = new Scanner(new File(Perceptron.PATH));

        int counter = tmp;
        while (input.hasNextLine() && counter <= numberOfLines) {

            String line = input.nextLine();

            String[] row = line.split(";");

            //argumenty bez typu
            double[] x = new double[row.length-1];

            String type = row[row.length-1];

            for (int i = 0; i < row.length-1; i++) {
                x[i] = Double.parseDouble(row[i]);
            }

            if (type.equals("1")) {
                type_number = 1;
            }
            else
                type_number = 0;

            list.add(new Wektor(x, type_number));
            counter++;
        }
        input.close();

        return list;
    }


    private static double[] fillWeights(int lineLength) {

        double[] weights = new double[lineLength];

        for (int i = 0; i < weights.length; i++) {
            Random r = new Random(20);
            weights[i] = r.nextDouble();
        }

        return weights;
    }

    private static int calcWynik(double theta, double[] weights, double[] position) {

        double suma_wag = 0;

        for (int i = 0; i < position.length; i++) {
            suma_wag += position[i] * weights[i] ;
        }

        if (suma_wag >= theta)
            return 1;
        else
            return 0;
    }

    public static void addWektor(double[] weights){
        Scanner sc = new Scanner(System.in);
        //dodawanie własnego wektora
        System.out.println("Czy chcesz podać wektor do klasyfikacji (y/n)");
        String odp = sc.nextLine();
        double x;
        double[] tmp = new double[lineLength];
        if (odp.equals("y")) {
            for (int i = 0; i < tmp.length ; i++) {
                System.out.println("x"+i+":");
                x = sc.nextDouble();
                tmp[i] = x;
            }
            wynik = calcWynik(theta,weights,tmp);
            if (wynik == 1) System.out.println("Podany wektor klasyfikuje się do 1");
            else  System.out.println("Podany wektor klasyfikuje się do 0");
        }
    }

}
