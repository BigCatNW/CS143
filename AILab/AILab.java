// Matthew Cook
// CS143
// HW Extra Credit 1: Have ChatGPT Write a Java Program
// Created with Claude
// The propmt read 'Write a java program that helps me learn to program my own AI.'
// I am framiliar with AI's ability to write code so I was not really suprised that Claude pulled this off.

import java.util.*;

/**
 * AILab - learn how AI works by building it from scratch, in plain Java.
 *
 * No libraries needed. Run it with (Java 11 or newer):
 *
 *     java AILab.java
 *
 * or the classic way:
 *
 *     javac AILab.java && java AILab
 *
 * The lessons build on each other:
 *   1. Perceptron        - a single artificial neuron (and its limits)
 *   2. Gradient descent  - how a machine "learns" numbers by reducing error
 *   3. Neural network    - hidden layers + backpropagation solve XOR
 *   4. Digit recognizer  - a tiny image classifier you can test by "drawing"
 *
 * Read the code alongside the output. Every lesson is a separate method, and
 * the NeuralNetwork class near the bottom is the real engine: it's only ~60
 * lines, and it's the same idea that powers much bigger systems.
 */
public class AILab {

    // A fixed seed makes every run repeatable. Change 7 to any other number
    // to see how random starting weights change the outcome.
    static final Random RNG = new Random(7);
    static final Scanner IN = new Scanner(System.in);

    // ------------------------------------------------------------------
    // Main menu
    // ------------------------------------------------------------------
    public static void main(String[] args) {
        while (true) {
            System.out.println();
            System.out.println("=============== AI LAB ===============");
            System.out.println(" 1. The Perceptron      (one artificial neuron)");
            System.out.println(" 2. Gradient Descent    (how machines learn)");
            System.out.println(" 3. Neural Network      (hidden layers + backprop)");
            System.out.println(" 4. Digit Recognizer    (a tiny image classifier)");
            System.out.println(" 5. Challenges          (things to try next)");
            System.out.println(" 0. Quit");
            String choice = prompt("Choose a lesson: ", "");
            switch (choice) {
                case "1": lessonPerceptron(); break;
                case "2": lessonGradientDescent(); break;
                case "3": lessonNeuralNetwork(); break;
                case "4": lessonDigits(); break;
                case "5": challenges(); break;
                case "0": System.out.println("Happy learning!"); return;
                default:  System.out.println("Please type 0-5.");
            }
        }
    }

    // ------------------------------------------------------------------
    // LESSON 1: The Perceptron
    // ------------------------------------------------------------------
    // A perceptron is the simplest "neuron":
    //     output = 1 if (w1*x1 + w2*x2 + bias) >= 0, else 0
    // Learning = nudging the weights whenever the neuron gets an answer wrong:
    //     weight += learningRate * error * input      (error = target - output)
    static void lessonPerceptron() {
        header("LESSON 1: The Perceptron");
        System.out.println("A neuron multiplies each input by a weight, adds a bias, and fires (1)");
        System.out.println("if the total is >= 0. We'll teach it a logic gate by trial and error.");
        System.out.println();

        String gate = prompt("Pick a gate to learn: AND, OR, NAND or XOR [AND]: ", "AND").toUpperCase();
        double[][] x = {{0, 0}, {0, 1}, {1, 0}, {1, 1}};
        double[] target;
        if (gate.equals("OR"))        target = new double[]{0, 1, 1, 1};
        else if (gate.equals("NAND")) target = new double[]{1, 1, 1, 0};
        else if (gate.equals("XOR"))  target = new double[]{0, 1, 1, 0};
        else { gate = "AND";          target = new double[]{0, 0, 0, 1}; }

        double[] w = {RNG.nextGaussian() * 0.5, RNG.nextGaussian() * 0.5}; // random start
        double bias = 0;
        double learningRate = 0.1;
        boolean learned = false;

        out("%nTraining on %s. Starting weights: w1=%.2f w2=%.2f bias=%.2f%n%n", gate, w[0], w[1], bias);
        for (int epoch = 1; epoch <= 25 && !learned; epoch++) {
            int mistakes = 0;
            for (int i = 0; i < x.length; i++) {
                double z = w[0] * x[i][0] + w[1] * x[i][1] + bias;
                double output = z >= 0 ? 1 : 0;
                double error = target[i] - output;           // -1, 0 or +1
                if (error != 0) mistakes++;
                w[0] += learningRate * error * x[i][0];      // the learning rule
                w[1] += learningRate * error * x[i][1];
                bias += learningRate * error;
            }
            out("epoch %2d: mistakes=%d  w1=%+.2f  w2=%+.2f  bias=%+.2f%n", epoch, mistakes, w[0], w[1], bias);
            learned = (mistakes == 0);
        }

        System.out.println();
        System.out.println(" x1 x2 | target | neuron says");
        for (int i = 0; i < x.length; i++) {
            double z = w[0] * x[i][0] + w[1] * x[i][1] + bias;
            out("  %d  %d |   %d    |     %d%n", (int) x[i][0], (int) x[i][1], (int) target[i], z >= 0 ? 1 : 0);
        }
        System.out.println();
        if (learned) {
            System.out.println("It learned " + gate + "! The weights define a straight line that separates the 1s from the 0s.");
        } else {
            System.out.println("It never got there. XOR can't be split by ONE straight line, so a single");
            System.out.println("neuron is stuck forever. This famous limit is why we need layers -> Lesson 3.");
        }
        pause();
    }

    // ------------------------------------------------------------------
    // LESSON 2: Gradient Descent
    // ------------------------------------------------------------------
    // Goal: find w and b so that  prediction = w*x + b  fits some data.
    // 1. Measure how wrong we are (loss = mean squared error).
    // 2. Work out which direction to nudge w and b to REDUCE the loss
    //    (that direction is the "gradient").
    // 3. Take a small step downhill. Repeat. This is how almost all AI trains.
    static void lessonGradientDescent() {
        header("LESSON 2: Gradient Descent");
        System.out.println("We made noisy data from the secret rule  y = 3x + 2.");
        System.out.println("The machine must discover the 3 and the 2 by itself.");
        System.out.println();

        int n = 20;
        double[] xs = new double[n], ys = new double[n];
        for (int i = 0; i < n; i++) {
            xs[i] = i * 0.5;
            ys[i] = 3 * xs[i] + 2 + RNG.nextGaussian() * 0.5;
        }

        double lr = askDouble("Learning rate (step size). Try 0.01, then 0.001, then 0.05", 0.01);
        double w = 0, b = 0;   // start knowing nothing
        int epochs = 2000;

        System.out.println();
        for (int epoch = 0; epoch <= epochs; epoch++) {
            double gradW = 0, gradB = 0, loss = 0;
            for (int i = 0; i < n; i++) {
                double prediction = w * xs[i] + b;
                double error = prediction - ys[i];
                loss += error * error;
                gradW += 2 * error * xs[i];   // d(loss)/d(w)
                gradB += 2 * error;           // d(loss)/d(b)
            }
            loss /= n; gradW /= n; gradB /= n;

            if (epoch % 200 == 0) out("epoch %4d: w=%8.4f  b=%8.4f  loss=%.4f%n", epoch, w, b, loss);
            if (Double.isNaN(loss) || loss > 1e12) {
                System.out.println("\nBOOM! The loss exploded. The steps were so big we overshot the");
                System.out.println("valley and bounced out of control. Lower the learning rate.");
                pause();
                return;
            }
            w -= lr * gradW;   // step downhill
            b -= lr * gradB;
        }
        out("%nLearned: y = %.3f x + %.3f   (secret rule was 3x + 2)%n", w, b);
        System.out.println("With a tiny rate (0.001) it is still learning when we stop; with a big one it explodes.");
        pause();
    }

    // ------------------------------------------------------------------
    // LESSON 3: Neural network + backpropagation
    // ------------------------------------------------------------------
    static void lessonNeuralNetwork() {
        header("LESSON 3: A Neural Network learns XOR");
        System.out.println("Hidden neurons let the network bend its decision boundary,");
        System.out.println("which solves the problem a single neuron couldn't.");
        System.out.println();

        int hidden = askInt("Hidden neurons (try 4, then 1 to watch it fail)", 4);
        int epochs = askInt("Training rounds", 10000);
        hidden = Math.max(1, hidden);

        double[][] x = {{0, 0}, {0, 1}, {1, 0}, {1, 1}};
        double[][] y = {{0}, {1}, {1}, {0}};
        NeuralNetwork net = new NeuralNetwork(new int[]{2, hidden, 1}, 0.5, RNG);

        System.out.println();
        for (int epoch = 1; epoch <= epochs; epoch++) {
            double loss = 0;
            for (int i : shuffledOrder(x.length)) loss += net.train(x[i], y[i]);
            if (epoch == 1 || epoch % Math.max(1, epochs / 10) == 0) {
                out("round %6d: average error = %.5f%n", epoch, loss / x.length);
            }
        }

        System.out.println();
        System.out.println(" x1 x2 | target | network output");
        for (int i = 0; i < x.length; i++) {
            double result = net.predict(x[i])[0];
            out("  %d  %d |   %d    |   %.3f  -> %d%n", (int) x[i][0], (int) x[i][1], (int) y[i][0], result, result >= 0.5 ? 1 : 0);
        }
        System.out.println();
        System.out.println("Outputs near 0 or 1 that match the target = it learned. Run it again with 1");
        System.out.println("hidden neuron (stuck) or 2 (sometimes works, depends on the random start).");
        pause();
    }

    // ------------------------------------------------------------------
    // LESSON 4: Tiny digit recognizer
    // ------------------------------------------------------------------
    // Each digit is a 5x5 grid of pixels (25 inputs). The network has 4 outputs,
    // one per digit; the output with the highest value is its answer.
    static final String[][] DIGITS = {
        {".###.", "#...#", "#...#", "#...#", ".###."},   // 0
        {"..#..", ".##..", "..#..", "..#..", ".###."},   // 1
        {".###.", "#...#", "...#.", "..#..", "#####"},   // 2
        {"####.", "....#", ".###.", "....#", "####."},   // 3
    };

    static void lessonDigits() {
        header("LESSON 4: Digit Recognizer");
        System.out.println("Teaching a network to read 5x5 pixel digits 0-3. We train it on clean AND");
        System.out.println("slightly corrupted copies, so it learns the shape, not exact pixels.");
        System.out.println();

        int classes = DIGITS.length;
        double[][] clean = new double[classes][];
        double[][] labels = new double[classes][classes];
        for (int d = 0; d < classes; d++) {
            clean[d] = toPixels(DIGITS[d]);
            labels[d][d] = 1;                    // "one-hot": digit 2 -> {0,0,1,0}
        }

        NeuralNetwork net = new NeuralNetwork(new int[]{25, 10, classes}, 0.3, RNG);
        int epochs = 3000;
        for (int epoch = 1; epoch <= epochs; epoch++) {
            double loss = 0;
            for (int d : shuffledOrder(classes)) {
                loss += net.train(clean[d], labels[d]);
                loss += net.train(noisy(clean[d], 0.08), labels[d]);   // fresh noise every time
            }
            if (epoch == 1 || epoch % 500 == 0) out("round %5d: average error = %.5f%n", epoch, loss / (2 * classes));
        }

        System.out.println("\nAccuracy on 1000 fresh test images at different noise levels:");
        for (double noise : new double[]{0.0, 0.05, 0.10, 0.20}) {
            int correct = 0;
            for (int t = 0; t < 1000; t++) {
                int d = RNG.nextInt(classes);
                if (argMax(net.predict(noisy(clean[d], noise))) == d) correct++;
            }
            out("  %2.0f%% of pixels flipped -> %.1f%% correct%n", noise * 100, correct / 10.0);
        }

        // Show one corrupted example
        int d = RNG.nextInt(classes);
        double[] sample = noisy(clean[d], 0.10);
        System.out.println("\nHere's a corrupted digit (true answer: " + d + "):");
        printImage(sample);
        showPrediction(net.predict(sample));

        // Let the user draw
        System.out.println("\nNow YOU draw. Type 5 rows of 5 characters, '#' = ink, anything else = blank.");
        System.out.println("Press Enter on an empty first row to stop.");
        while (true) {
            double[] pixels = new double[25];
            for (int r = 0; r < 5; r++) {
                String row = prompt("row " + (r + 1) + ": ", "");
                if (r == 0 && row.isEmpty()) { pause(); return; }
                for (int c = 0; c < 5 && c < row.length(); c++) pixels[r * 5 + c] = row.charAt(c) == '#' ? 1 : 0;
            }
            System.out.println("You drew:");
            printImage(pixels);
            showPrediction(net.predict(pixels));
            System.out.println();
        }
    }

    // ------------------------------------------------------------------
    // Challenges
    // ------------------------------------------------------------------
    static void challenges() {
        header("CHALLENGES");
        System.out.println(" 1. Lesson 1: add a NOR gate. Then change the learning rate and compare epochs needed.");
        System.out.println(" 2. Lesson 2: add a third parameter so it learns y = a*x*x + b*x + c.");
        System.out.println(" 3. Lesson 3: switch the activation from sigmoid to ReLU (max(0, z)).");
        System.out.println("    Hint: its derivative is 1 if z > 0 else 0. What happens to training?");
        System.out.println(" 4. Lesson 3: add a second hidden layer: new int[]{2, 4, 4, 1}. It just works!");
        System.out.println(" 5. Lesson 4: add digits 4-9 (add patterns to DIGITS, widen the output layer).");
        System.out.println(" 6. Lesson 4: print how many training rounds you need to hit 100% at 10% noise.");
        System.out.println(" 7. Replace per-sample updates with mini-batches (average several gradients).");
        System.out.println(" 8. Save the trained weights to a file and load them back.");
        System.out.println(" 9. Next steps: read about softmax + cross-entropy, then convolutional layers,");
        System.out.println("    then try a real dataset like MNIST (handwritten digits).");
        pause();
    }

    // ==================================================================
    // THE ENGINE: a small, general feed-forward neural network
    // ==================================================================
    static class NeuralNetwork {
        final int[] sizes;          // neurons per layer, e.g. {2, 4, 1}
        final double[][][] weights; // weights[layer][toNeuron][fromNeuron]
        final double[][] biases;    // biases[layer][toNeuron]
        final double learningRate;
        double[][] activations;     // remembered from the last forward pass

        NeuralNetwork(int[] sizes, double learningRate, Random rng) {
            this.sizes = sizes;
            this.learningRate = learningRate;
            int layers = sizes.length - 1;
            weights = new double[layers][][];
            biases = new double[layers][];
            for (int l = 0; l < layers; l++) {
                weights[l] = new double[sizes[l + 1]][sizes[l]];
                biases[l] = new double[sizes[l + 1]];
                double scale = Math.sqrt(2.0 / (sizes[l] + sizes[l + 1]));   // Xavier-style start
                for (double[] row : weights[l])
                    for (int i = 0; i < row.length; i++) row[i] = rng.nextGaussian() * scale;
            }
        }

        static double sigmoid(double z) { return 1.0 / (1.0 + Math.exp(-z)); }

        /** Forward pass: push inputs through every layer. */
        double[] predict(double[] input) {
            activations = new double[sizes.length][];
            activations[0] = input;
            for (int l = 0; l < weights.length; l++) {
                double[] next = new double[sizes[l + 1]];
                for (int j = 0; j < next.length; j++) {
                    double z = biases[l][j];
                    for (int i = 0; i < sizes[l]; i++) z += weights[l][j][i] * activations[l][i];
                    next[j] = sigmoid(z);
                }
                activations[l + 1] = next;
            }
            return activations[sizes.length - 1];
        }

        /** One learning step on one example. Returns the mean squared error. */
        double train(double[] input, double[] target) {
            double[] output = predict(input);
            int layers = weights.length;

            // delta[l][j] = "how much does neuron j of layer l contribute to the error?"
            double[][] delta = new double[layers][];

            // Output layer: error * slope of sigmoid, where slope = a * (1 - a)
            delta[layers - 1] = new double[output.length];
            double loss = 0;
            for (int j = 0; j < output.length; j++) {
                double error = output[j] - target[j];
                loss += error * error;
                delta[layers - 1][j] = error * output[j] * (1 - output[j]);
            }

            // BACKpropagation: pass blame backwards through the weights
            for (int l = layers - 2; l >= 0; l--) {
                delta[l] = new double[sizes[l + 1]];
                for (int i = 0; i < sizes[l + 1]; i++) {
                    double blame = 0;
                    for (int j = 0; j < sizes[l + 2]; j++) blame += weights[l + 1][j][i] * delta[l + 1][j];
                    double a = activations[l + 1][i];
                    delta[l][i] = blame * a * (1 - a);
                }
            }

            // Gradient descent step on every weight and bias
            for (int l = 0; l < layers; l++) {
                for (int j = 0; j < sizes[l + 1]; j++) {
                    for (int i = 0; i < sizes[l]; i++) weights[l][j][i] -= learningRate * delta[l][j] * activations[l][i];
                    biases[l][j] -= learningRate * delta[l][j];
                }
            }
            return loss / output.length;
        }
    }

    // ==================================================================
    // Helpers
    // ==================================================================
    static double[] toPixels(String[] rows) {
        double[] p = new double[25];
        for (int r = 0; r < 5; r++)
            for (int c = 0; c < 5; c++) p[r * 5 + c] = rows[r].charAt(c) == '#' ? 1 : 0;
        return p;
    }

    /** Flip each pixel with the given probability. */
    static double[] noisy(double[] pixels, double probability) {
        double[] copy = pixels.clone();
        for (int i = 0; i < copy.length; i++) if (RNG.nextDouble() < probability) copy[i] = 1 - copy[i];
        return copy;
    }

    static void printImage(double[] p) {
        for (int r = 0; r < 5; r++) {
            StringBuilder sb = new StringBuilder("   ");
            for (int c = 0; c < 5; c++) sb.append(p[r * 5 + c] > 0.5 ? "# " : ". ");
            System.out.println(sb);
        }
    }

    static void showPrediction(double[] scores) {
        int best = argMax(scores);
        for (int d = 0; d < scores.length; d++) {
            int bar = (int) Math.round(Math.max(0, Math.min(1, scores[d])) * 30);
            out("   digit %d  %-30s %5.1f%%%s%n", d, "=".repeat(bar), scores[d] * 100, d == best ? "  <-- answer" : "");
        }
    }

    static int argMax(double[] v) {
        int best = 0;
        for (int i = 1; i < v.length; i++) if (v[i] > v[best]) best = i;
        return best;
    }

    static int[] shuffledOrder(int n) {
        int[] order = new int[n];
        for (int i = 0; i < n; i++) order[i] = i;
        for (int i = n - 1; i > 0; i--) {
            int j = RNG.nextInt(i + 1);
            int tmp = order[i]; order[i] = order[j]; order[j] = tmp;
        }
        return order;
    }

    static void header(String title) {
        System.out.println();
        System.out.println("------------------------------------------------------");
        System.out.println(title);
        System.out.println("------------------------------------------------------");
    }

    static void out(String format, Object... args) {
        System.out.print(String.format(Locale.US, format, args));
    }

    static void pause() { prompt("\n(press Enter to return to the menu) ", ""); }

    static String prompt(String message, String defaultValue) {
        System.out.print(message);
        if (!IN.hasNextLine()) { System.out.println(); System.exit(0); }
        String line = IN.nextLine().trim();
        return line.isEmpty() ? defaultValue : line;
    }

    static double askDouble(String message, double def) {
        String s = prompt(message + " [" + def + "]: ", "");
        if (s.isEmpty()) return def;
        try { return Double.parseDouble(s); }
        catch (NumberFormatException e) { System.out.println("  (not a number, using " + def + ")"); return def; }
    }

    static int askInt(String message, int def) {
        String s = prompt(message + " [" + def + "]: ", "");
        if (s.isEmpty()) return def;
        try { return Integer.parseInt(s); }
        catch (NumberFormatException e) { System.out.println("  (not a whole number, using " + def + ")"); return def; }
    }
}

/*
jGRASP Output

  ----jGRASP exec: java -ea AILab
 
 =============== AI LAB ===============
  1. The Perceptron      (one artificial neuron)
  2. Gradient Descent    (how machines learn)
  3. Neural Network      (hidden layers + backprop)
  4. Digit Recognizer    (a tiny image classifier)
  5. Challenges          (things to try next)
  0. Quit
 Choose a lesson: 0
 Happy learning!
 
  ----jGRASP: Operation complete.

*/  