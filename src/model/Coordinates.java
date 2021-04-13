package model;

import java.util.Objects;

/**
 * Coordinates data class
 */

public class Coordinates {
    private Double x; //Значение поля должно быть больше -194, Поле не может быть null
    private Float y; //Поле не может быть null

    public Coordinates() {}

    /**
     * @param x - X coordinate
     * @param y - Y coordinate
     */
    public Coordinates(Double x, Float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return X coordinate
     */
    public Double getX() {
        return x;
    }

    /**
     * @return Y coordinate
     */
    public Float getY() {
        return y;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public void setY(Float y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return y == that.y &&
                Objects.equals(x, that.x);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}