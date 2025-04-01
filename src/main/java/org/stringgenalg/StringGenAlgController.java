package org.stringgenalg;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StringGenAlgController {
    // Define Variables/Constants as well as instantiate random module
    private static StringGenAlgApplication mainApp;
    private final List<String> csvData = new ArrayList<>();
    private String csvFileName;
    private String target;
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

    // Allows switching of scenes.
    public void setMainApp(StringGenAlgApplication mainApp) {
        StringGenAlgController.mainApp = mainApp;
    }

    // Method to run algorithm
    private void runGeneticAlgorithm() {
        String[] population = new String[StringGenAlgApplication.populationSize];
        for (int i = 0; i < StringGenAlgApplication.populationSize; i++) {
            population[i] = generateRandomString(target.length());
        }

        int generation = 0;
        boolean found = false;
        String bestIndividual = "";
        int bestFitness = 0;

        while (isRunning) {
            int[] fitness = new int[StringGenAlgApplication.populationSize];
            int maxFitness = 0;
            String currentBest = "";

            for (int i = 0; i < StringGenAlgApplication.populationSize; i++) {
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

            // Update display
            javafx.application.Platform.runLater(() -> {
                getGen.setText(String.valueOf(currentGen));
                getBestFit.setText(String.valueOf(currentFitness));
                getMatch.setText(currentMatch);
            });

            if (found) break;

            // Generate new population
            String[] newPopulation = new String[StringGenAlgApplication.populationSize];
            for (int i = 0; i < StringGenAlgApplication.populationSize; i++) {
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
            if (random.nextDouble() < StringGenAlgApplication.mutationRate) {
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

        getPopSize.setText(String.valueOf(StringGenAlgApplication.populationSize));
        getMutRate.setText(StringGenAlgApplication.mutationRate * 100 + "%");
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
    private Button onClickSettings;

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

    // When settings button is clicked, go to settings scene
    @FXML
    protected void onClickSettings(ActionEvent event) {
        // REMEMBER IF YOU WANT TO CREATE A NEW SCENE YOU MUST DECLARE THAT IN THE FXML FILE
        // ex: settings.fxml -> SettingsController.java
        // THIS WORKS NOW
        try {
            mainApp.showSettingsPage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}