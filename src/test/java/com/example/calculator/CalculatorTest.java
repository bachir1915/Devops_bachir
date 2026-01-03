package com.example.calculator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {

    private final Calculator calculator = new Calculator();

    @Test
    void testAdd() {
        System.out.println("Voilà : Test de l'addition (2 + 3)");
        int result = calculator.add(2, 3);
        if (result == 5) {
            System.out.println("Succès ! 2 + 3 = 5");
        } else {
            System.out.println("Erreur : 2 + 3 devrait être égal à 5, mais obtenu : " + result);
            fail("Calcul incorrect pour l'addition");
        }
    }

    @Test
    void testSubtract() {
        System.out.println("Voilà : Test de la soustraction (3 - 2)");
        int result = calculator.subtract(3, 2);
        if (result == 1) {
            System.out.println("Succès ! 3 - 2 = 1");
        } else {
            System.out.println("Erreur : 3 - 2 devrait être égal à 1, mais obtenu : " + result);
            fail("Calcul incorrect pour la soustraction");
        }
    }

    @Test
    void testMultiply() {
        System.out.println("Voilà : Test de la multiplication (2 * 3)");
        int result = calculator.multiply(2, 3);
        if (result == 6) {
            System.out.println("Succès ! 2 * 3 = 6");
        } else {
            System.out.println("Erreur : 2 * 3 devrait être égal à 6, mais obtenu : " + result);
            fail("Calcul incorrect pour la multiplication");
        }
    }

    @Test
    void testDivide() {
        System.out.println("Voilà : Test de la division (6 / 3)");
        double result = calculator.divide(6, 3);
        if (result == 2.0) {
            System.out.println("Succès ! 6 / 3 = 2.0");
        } else {
            System.out.println("Erreur : 6 / 3 devrait être égal à 2.0, mais obtenu : " + result);
            fail("Calcul incorrect pour la division");
        }
    }

    @Test
    void testDivideByZero() {
        System.out.println("Voilà : Test de la division par zéro (1 / 0)");
        try {
            calculator.divide(1, 0);
            System.out.println("Erreur : La division par zéro n'a pas déclenché d'erreur !");
            fail("Aurait dû lancer IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            System.out.println("Succès ! Une erreur a bien été signalée : " + e.getMessage());
        }
    }
}
