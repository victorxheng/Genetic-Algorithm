package com.victorcheng;

import java.util.ArrayList;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        int cities = 100;

        double[][] distances = new double[cities][cities];

        //populate distances
        int[] xLocations = new int[cities];
        int[] yLocations = new int[cities];

        int maxX = 1000;
        int maxY = 1000;
        Random r = new Random();
        for(int i = 0; i< cities; i++){
            xLocations[i] = r.nextInt(maxX);
            yLocations[i] = r.nextInt(maxY);
        }

        for(int i=0; i< distances.length; i++) {
            for(int j=0; j< distances[i].length; j++) {
                distances[i][j] = Math.sqrt(Math.pow(xLocations[i]-xLocations[j],2)+Math.pow(yLocations[i]-yLocations[j],2));
                //System.out.print(distances[i][j] + "\t");
            }
            //System.out.println();
        }

        int poolSize = 100000;
        int numberOfParents = 50;
        int mutationsEach = 50;
        int crossEach = 3;
        ArrayList<int[]> currentGeneration = generateInitialParents(distances,poolSize,numberOfParents);
        double previousFitness = Double.MAX_VALUE;
        int optimized = 0;
        while(true){
            int parentSize = currentGeneration.size();
            //cross
            for(int i = 0; i< parentSize-1; i++){
                for(int j = i+1; j<parentSize; j++){
                    for(int k = 0; k<crossEach; k++){
                        currentGeneration.add(cross(currentGeneration.get(i), currentGeneration.get(j)));
                    }
                }
            }
            int initialSize = currentGeneration.size();
            for(int i = 0; i< initialSize; i++){
                for(int j = 0; j<mutationsEach; j++){
                    currentGeneration.add(mutate(currentGeneration.get(i)));
                }
            }
            for(int i = currentGeneration.size(); i<poolSize; i++){
                currentGeneration.add(generateRandomSequence(cities));
            }

            ArrayList<Double> fitness = new ArrayList<Double>();
            for(int i =0; i<currentGeneration.size(); i++){
                fitness.add(calculateFitness(currentGeneration.get(i), distances));
                //System.out.println(fitness.get(i));
            }

            ArrayList<int[]> nextGeneration = new ArrayList<>();
            for(int i = 0; i<numberOfParents; i++){
                int min = truncateMinimum(fitness);
                nextGeneration.add(currentGeneration.remove(min));
                fitness.remove(min);
            }
            currentGeneration = new ArrayList<>();
            for(int i = 0; i<nextGeneration.size(); i++){
                currentGeneration.add(nextGeneration.get(i));
            }
            if(calculateFitness(currentGeneration.get(0),distances)==previousFitness){
                optimized++;
                if(optimized > 20)break;
            }
            else{
                optimized = 0;
                previousFitness =calculateFitness(currentGeneration.get(0),distances);
            }
        }
        System.out.println("Final Sequence: ");
        for(int i = 0; i<currentGeneration.get(0).length; i++){
            System.out.print(currentGeneration.get(0)[i] + " ");
        }
        System.out.println();
        System.out.println("Fitness: "+ calculateFitness(currentGeneration.get(0),distances));





    }
    private static ArrayList<int[]> generateInitialParents(double[][] distances, int poolSize, int numberOfParents){
        ArrayList<int[]> sequences = new ArrayList<>();
        ArrayList<Double> fitness = new ArrayList<>();
        for(int i = 0; i< poolSize; i++){
            sequences.add(generateRandomSequence(distances.length));
            fitness.add(calculateFitness(sequences.get(i), distances));
        }
        ArrayList<int[]> parents = new ArrayList<>();
        for(int i = 0; i<numberOfParents; i++){
            int min = truncateMinimum(fitness);
            parents.add(sequences.remove(min));
            fitness.remove(min);
        }
        return parents;
    }
    private static int truncateMinimum(ArrayList<Double> fitness){
        double min = fitness.get(0);
        int index = 0;
        for (int i = 0; i < fitness.size(); i++) {
            if(fitness.get(i) < min){
                min = fitness.get(i);
                index = i;
            }
        }
        System.out.println(min);
        return index;
    }
    private static int[] mutate(int[] sequence){
        Random r = new Random();
        int index1 = r.nextInt(sequence.length-1);
        int index2 = r.nextInt(sequence.length-index1-1)+index1+1;
        int[] mutation = new int[sequence.length];
        for(int i = 0; i< sequence.length; i++){
            mutation[i] = sequence[i];
        }
        int temp = mutation[index1];
        mutation[index1] = mutation[index2];
        mutation[index2] = temp;

        /*System.out.println();
        for(int g : mutation){
            System.out.print(g + " ");
        }*/
        return mutation;
    }
    private static int[] cross(int[] sequence1, int[] sequence2){
        //order crossover
        Random r = new Random();
        int index1 = r.nextInt(sequence1.length-1);
        int index2 = r.nextInt(sequence1.length-index1-1)+index1+1;
        int[] crossover = new int[sequence1.length];
        for(int i = 0; i<crossover.length; i++){
            crossover[i] = -1;
        }


        ArrayList<Integer> locations = new ArrayList<>();
        for(int i = index1; i <= index2; i++){
            crossover[i] = sequence1[i];
            locations.add(sequence1[i]);
        }
        int i2 = index2+1;
        for(int i = index2+1; i != index1;i++){
            if(i >= sequence1.length) i=0;
            if(i==index1)break;
            if(i2>=sequence2.length) i2=0;
            int genome = sequence2[i2];
            while(locations.contains(genome)){
                i2++;
                if(i2>=sequence2.length) i2=0;
                genome=sequence2[i2];
            }
            i2++;
            locations.add(genome);
            crossover[i] = genome;

        }

/*
        System.out.println();
        for(int g : crossover){
            System.out.print(g + " ");
        }
*/
        return crossover;

    }

    private static int[] generateRandomSequence(int cities){
        //System.out.println();
        ArrayList<Integer> nums = new ArrayList<>();
        for(int i = 0; i<cities; i++){
            nums.add(i);
        }
        int[] sequence = new int[cities];
        Random r = new Random();
        int index = 0;
        while(nums.size()!=0){
            sequence[index] = nums.remove(r.nextInt(nums.size()));
            //System.out.print(sequence[index] +" ");
            index++;
        }
        return sequence;
    }
    private static double calculateFitness(int[] sequence, double[][] distances){
        double fitness = distances[sequence[sequence.length-1]][sequence[0]];
        for(int i = 1; i< sequence.length; i++){
            fitness += distances[sequence[i-1]][sequence[i]];
        }
        return fitness;
    }
}
