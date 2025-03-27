package org.stringgenalg;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StringGenAlgController {
    private final List<String> csvData = new ArrayList<>();
    private String csvFileName;
    private int lastGeneration = 0;
    private int lastBestFitness = 0;
    private String lastBestIndividual = "";
    // Define Variables/Constants as well as instantiate random module
    private String target;
    private static final int POPULATION_SIZE = 250;
    private static final double MUTATION_RATE = 0.01;
    private static final Random random = new Random();
    private volatile boolean isRunning = false;
    // Labels
    @FXML private TextField inputString;
    @FXML private Label getPopSize;
    @FXML private Label getMutRate;
    @FXML private Label getGen;
    @FXML private Label getBestFit;
    @FXML private Label getMatch;
    @FXML private Label exportLabel;

    // Method to run algorithm
    private void runGeneticAlgorithm() {
        String[] population = new String[POPULATION_SIZE];
        for (int i = 0; i < POPULATION_SIZE; i++) {
            population[i] = generateRandomString(target.length());
        }

        int generation = 0;
        boolean found = false;
        String bestIndividual = "";
        int bestFitness = 0;

        while (!found && isRunning) {
            int[] fitness = new int[POPULATION_SIZE];
            int maxFitness = 0;
            String currentBest = "";

            for (int i = 0; i < POPULATION_SIZE; i++) {
                fitness[i] = calculateFitness(population[i]);
                if (fitness[i] > maxFitness) {
                    maxFitness = fitness[i];
                    currentBest = population[i];
                }

                if (fitness[i] == target.length()) {
                    found = true;
                    break;
                }
            }

            bestIndividual = currentBest;
            bestFitness = maxFitness;

            final int currentGen = generation;
            final int currentFitness = bestFitness;
            final String currentMatch = bestIndividual;

            javafx.application.Platform.runLater(() -> {
                getGen.setText(String.valueOf(currentGen));
                getBestFit.setText(String.valueOf(currentFitness));
                getMatch.setText(currentMatch);
            });

            if (found) break;

            // Generate new population
            String[] newPopulation = new String[POPULATION_SIZE];
            for (int i = 0; i < POPULATION_SIZE; i++) {
                String parent1 = selectParent(population, fitness);
                String parent2 = selectParent(population, fitness);
                String child = crossover(parent1, parent2);
                child = mutate(child);
                newPopulation[i] = child;
            }

            population = newPopulation;
            generation++;

            // Calls method to append current data
            writeToCSV(currentGen, currentFitness, currentMatch);

            try {
                Thread.sleep(50); // to let the UI breathe and reduce lag for higher populations
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Adds final correct individual at end of csv
        if (found) {
            writeToCSV(generation, bestFitness, bestIndividual);
        }
    }

    // Generate a random string of given length
    private static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            // Generate random character between 32 (space) and 126 (~) in ASCII
            char c = (char) (32 + random.nextInt(95));
            sb.append(c);
        }
        return sb.toString();
    }

    // Calculate fitness (number of correct characters)
    private int calculateFitness(String individual) {
        int fitness = 0;
        for (int i = 0; i < individual.length(); i++) {
            if (individual.charAt(i) == target.charAt(i)) {
                fitness++;
            }
        }
        return fitness;
    }

    // Select a parent based on fitness (roulette wheel selection)
    private static String selectParent(String[] population, int[] fitness) {
        // Calculate total fitness
        int totalFitness = 0;
        for (int f : fitness) {
            totalFitness += f;
        }

        // Select random point
        int randomPoint = random.nextInt(totalFitness);

        // Find which individual this point corresponds to
        int runningSum = 0;
        for (int i = 0; i < population.length; i++) {
            runningSum += fitness[i];
            if (runningSum >= randomPoint) {
                return population[i];
            }
        }

        // Fallback
        return population[0];
    }

    // Single-point crossover
    private static String crossover(String parent1, String parent2) {
        int crossoverPoint = random.nextInt(parent1.length());
        return parent1.substring(0, crossoverPoint) + parent2.substring(crossoverPoint);
    }

    // Random mutation of characters
    private static String mutate(String individual) {
        StringBuilder sb = new StringBuilder(individual);
        for (int i = 0; i < individual.length(); i++) {
            if (random.nextDouble() < MUTATION_RATE) {
                sb.setCharAt(i, (char) (32 + random.nextInt(95)));
            }
        }
        return sb.toString();
    }

    // Writes data into a CSV file
    private void writeToCSV(int generation, int bestFitness, String bestIndividual) {
        if (generation == 0) {
            csvData.clear(); // clear any old runs
            csvData.add("Generation,Best Fitness,Best Individual");
        }

        String escapedIndividual = bestIndividual.replace("\"", "\"\"");
        String line = String.format("%d,%d,\"%s\"", generation, bestFitness, escapedIndividual);
        csvData.add(line);

        // Also update last-known data for potential future use
        lastGeneration = generation;
        lastBestFitness = bestFitness;
        lastBestIndividual = bestIndividual;
    }

    // Buttons
    @FXML
    protected void onClickStart() {
        target = inputString.getText();

        if (target == null || target.trim().isEmpty()) {
            System.out.println("Target string is empty!");
            return;
        }

        isRunning = true;

        getPopSize.setText(String.valueOf(POPULATION_SIZE));
        getMutRate.setText(String.valueOf(MUTATION_RATE * 100 + "%"));
        getGen.setText("0");
        getBestFit.setText("0");
        getMatch.setText(" ");

        new Thread(this::runGeneticAlgorithm).start();
    }

    @FXML
    protected void onClickReset() {
        isRunning = false;
        inputString.clear();

        getGen.setText("");
        getBestFit.setText("");
        getMatch.setText("");
        getPopSize.setText("");
        getMutRate.setText("");
    }

    @FXML
    protected void onClickCSV() {

        // If no data, print out this.
        if (csvData.isEmpty()) {
            System.out.println("No data to export yet.");
            return;
        }

        csvFileName = "genetic_algorithm_" + System.currentTimeMillis() + ".csv";

        new Thread(() -> {
            try (FileWriter writer = new FileWriter(csvFileName)) {
                for (String line : csvData) {
                    writer.write(line + System.lineSeparator());
                }
                System.out.println("CSV saved to: " + csvFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        exportLabel.setText("CSV saved to: " + csvFileName);
    }
}