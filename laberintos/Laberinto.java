package laberintos;

import processing.core.PApplet;
import java.util.ArrayList;
import java.util.Random;
import java.lang.Math;
import java.util.EmptyStackException;
import java.util.Stack;

/**
 * Clase que crea un laberinto con Processing.
 * 
 * @author Sara
 * @author Baruch
 */
public class Laberinto extends PApplet {
  int alto = 10; // Altura (en celdas) de la cuadricula.
  int ancho = 10; // Anchura (en celdas) de la cuadricula.
  int celda = 30; // Tamanio de cada celda cuadrada (en pixeles).
  ModeloLaberinto modelo; // El objeto que representa el modelo del laberinto.

  @Override
  public void setup() {
    frameRate(60);
    background(50);
    modelo = new ModeloLaberinto(ancho, alto, celda);
  }

  @Override
  public void settings() {
    size(ancho * celda, (alto * celda));
  }

  /**
   * Pintar el mundo del modelo.
   */
  @Override
  public void draw() {
    for (int i = 0; i < alto; i++)
      for (int j = 0; j < ancho; j++) {
        if (modelo.X == j && modelo.Y == i) {
          fill(255, 67, 88);
        } else {
          fill(204, 204, 204);
        }
        stroke(100, 230, 230);
        rect(j * modelo.tamanio, i * modelo.tamanio, modelo.tamanio, modelo.tamanio);
        // En caso de que las paredes de las celdas ya no se encuentren activas, estás
        // se
        // pintarán del color del fondo.
        if (!modelo.mundo[i][j].pared_1) {
          stroke(0);
          line(j * modelo.tamanio, i * modelo.tamanio, ((j + 1) * modelo.tamanio), i * modelo.tamanio);
        }
        if (!modelo.mundo[i][j].pared_2) {
          stroke(0);
          line((j * modelo.tamanio) + modelo.tamanio, i * modelo.tamanio, (j + 1) * modelo.tamanio,
              (((i + 1) * modelo.tamanio)));
        }
        if (!modelo.mundo[i][j].pared_3) {
          stroke(0);
          line(j * modelo.tamanio, (i * modelo.tamanio) + modelo.tamanio, ((j + 1) * modelo.tamanio),
              ((i + 1) * modelo.tamanio));
        }
        if (!modelo.mundo[i][j].pared_4) {
          stroke(0);
          line(j * modelo.tamanio, i * modelo.tamanio, j * modelo.tamanio, ((i + 1) * modelo.tamanio));
        }
      }
    modelo.generaLaberinto();
  }

  /**
   * Clase que representa cada celda de la cuadricula.
   */
  class Celda {
    int celdaX;
    int celdaY;
    boolean pared_1;
    boolean pared_2;
    boolean pared_3;
    boolean pared_4;
    boolean estado;
    boolean visitada;

    /**
     * Constructor de una celda.
     * 
     * @param celdaX Coordenada en x
     * @param celdaY Coordenada en y
     * @param estado Estado de la celda. true si no ha sido visitada, false en otro
     *               caso.
     */
    Celda(int celdaX, int celdaY, boolean estado) {
      this.celdaX = celdaX;
      this.celdaY = celdaY;
      this.estado = estado;
      this.pared_1 = true; // Booleano que representa la pared de arriba
      this.pared_2 = true; // Booleano que representa la pared de la derecha
      this.pared_3 = true; // Booleano que representa la pared de abajo
      this.pared_4 = true; // Booleano que representa la pared de la izquierda
      this.visitada = false;
    }
  }

  /**
   * Clase que modela el laberinto, es decir, crea el mundo del laberinto.
   */
  class ModeloLaberinto {
    int ancho, alto; // Tamaño de celdas a lo largo y ancho de la cuadrícula.
    int tamanio; // Tamaño en pixeles de cada celda.
    Celda[][] mundo; // Mundo de celdas
    int direccion;
    Random rnd = new Random(); // Auxiliar para decisiones aleatorias.
    int X; // Posicion en la recta X en la que esta el agente
    int Y; // Posicion en la recta Y en la que esta el agente
    Stack<Celda> pila = new Stack<>();

    /**
     * Constructor del modelo
     * 
     * @param ancho   Cantidad de celdas a lo ancho en la cuadricula.
     * @param ancho   Cantidad de celdas a lo largo en la cuadricula.
     * @param tamanio Tamaño (en pixeles) de cada celda cuadrada que compone la
     *                cuadricula.
     */
    ModeloLaberinto(int ancho, int alto, int tamanio) {
      this.ancho = ancho;
      this.alto = alto;
      this.tamanio = tamanio;
      this.mundo = new Celda[alto][ancho];
      this.X = rnd.nextInt(ancho);
      this.Y = rnd.nextInt(alto);


      for (int i = 0; i < alto; i++)
        for (int j = 0; j < ancho; j++)
          mundo[i][j] = new Celda(i, j, true);
    }

    /**
     * Mueve el agente a un celda que no ha sido visitada.
     */
    void moverAgente() {
      int direccion = buscaDireccion();
      switch (direccion) {
        case 0:
          mundo[Y][X].pared_2 = false;
          pila.push(mundo[Y][X]);
          Y = Y - 1;
          mundo[Y][X].pared_4 = false;
          break;
        case 1:
          mundo[Y][X].pared_3 = false;
          pila.push(mundo[Y][X]);
          X = X + 1;
          mundo[Y][X].pared_1 = false;
          break;
        case 2:
          mundo[Y][X].pared_4 = false;
          pila.push(mundo[Y][X]);
          Y += 1;
          mundo[Y][X].pared_2 = false;
          break;
        case 3:
          mundo[Y][X].pared_1 = false;
          pila.push(mundo[Y][X]);
          X -= 1;
          mundo[Y][X].pared_3 = false;
          break;
      }
    }

    /**
     * Busca una celda libre y regresa su dirección.
     * 
     * @return la dirección de la celda.
     */
    int buscaDireccion() {
      int direccion = rnd.nextInt(4);
      if (4 - direccion >= 0 && esCeldaNoVisitada(4 - direccion)) {
        return 4 - direccion;
      }

      for (int i = 0; i < 4; i++) {
        if (esCeldaNoVisitada(i)) {

          return i;
        }
      }

      return -1;
    }

    /**
     * Determina si una celda en una dirección no ha sido
     * visitada.
     * 
     * @param direccion la dirección de la celda a analizar.
     * @return True si no ha sido visitada y false en otro caso.
     */
    boolean esCeldaNoVisitada(int direccion) {
      try {
        int x = 0;
        int y = 0;
        switch (direccion) {
          case 0:
            x = (X);
            y = (Y - 1);
            break;
          case 1:
            x = (X + 1);
            y = (Y);
            break;
          case 2:
            x = (X);
            y = (Y + 1);
            break;
          case 3:
            x = (X - 1);
            y = (Y);
            break;
        }
        return !mundo[y][x].visitada;

      } catch (Exception e) {
        return false;
      }
    }

    /**
     * Determina si un vecino de una celda con coordenadas (x,y)
     * no ha sido visitado.
     * @param x coordenada x de la celda.
     * @param y coordenada y de la celda.
     * @return true si tiene un vecino sin visitar, false en otro caso.
     */
    boolean hayVecinoNoVisitado(int x, int y) {
      boolean arriba = false, derecha = false, abajo = false, izquierda = false;
      if (!mundo[y][x].visitada)
        return true;

      try {
        arriba = !mundo[y - 1][x].visitada;
      } catch (Exception e) {
      }

      try {
        derecha = !mundo[y][x + 1].visitada;
      } catch (Exception e) {
      }
      try {
        abajo = !mundo[y + 1][x].visitada;
      } catch (Exception e) {
      }
      try {
        izquierda = !mundo[y][x - 1].visitada;
      } catch (Exception e) {
      }
      return arriba || derecha || abajo || izquierda;
    }

    /**
     * Genera el laberinto.
     */
    void generaLaberinto() {
      try {

        mundo[Y][X].visitada = true;
        mundo[Y][X].estado = hayVecinoNoVisitado(X, Y);

        if (mundo[Y][X].estado == false) {
          Celda mover = pila.pop();
          X = mover.celdaY;
          Y = mover.celdaX;
        } else {
          this.moverAgente();
        }
      } catch (EmptyStackException e) {
      }
    }
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    PApplet.main(new String[] { "laberintos.Laberinto" });
  }
}
